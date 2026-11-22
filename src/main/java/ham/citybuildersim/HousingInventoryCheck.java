package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import static ham.citybuildersim.HousingInventory.Owner.*;

/**
 * Aggregate housing inventory, caused through recipes, construction and transfers.
 * WHY (RES2): title/listing quantities must conserve homes independently of the
 * represented population, and a load must restore them without a second completion.
 */
public final class HousingInventoryCheck {
    static int fails;
    static PrintStream out;
    static void check(String label, boolean ok) {
        out.println((ok ? "  ok   " : "  FAIL ") + label);
        if (!ok) fails++;
    }
    static void near(String label, double actual, double expected) {
        check(label + " (" + actual + ", expected " + expected + ")",
                Math.abs(actual - expected) <= Math.max(1e-8, Math.ulp(expected) * 32));
    }
    static void refused(String label, Runnable action) {
        boolean refused = false;
        try { action.run(); } catch (IllegalArgumentException | IllegalStateException e) { refused = true; }
        check(label, refused);
    }
    static BuildingManager mixed(long count, boolean standing) {
        BuildingManager b = new BuildingManager();
        BuildingsTemplate t = new BuildingsTemplate("Mixed fixture", BuildingType.RESIDENTIAL)
                .setId(900).setSector(Sectors.REAL_ESTATE).setCapacity(10).setDwellings(3)
                .setConstructionPoints(10).setHousingRecipe(new HousingRecipe("Mixed fixture", List.of(
                        new HousingRecipe.Dwelling("small", 1, 2, 40, 0, 1, 0, .5, 40),
                        new HousingRecipe.Dwelling("family", 2, 4, 80, 2, 1, 1, .5, 80)), 2));
        b.addStack(t, count, standing);
        return b;
    }
    static HousingInventory.Key key(String dwelling) { return new HousingInventory.Key(900, dwelling); }
    static double sumOwned(HousingInventory h, HousingInventory.Key k) {
        double sum = 0;
        for (var o : HousingInventory.Owner.values()) sum += h.owned(k, o);
        return sum;
    }
    static double sumOccupied(HousingInventory h, HousingInventory.Key k) {
        double sum = 0;
        for (var o : HousingInventory.Owner.values()) sum += h.occupied(k, o);
        return sum;
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());
        System.setOut(quiet);
        try {
            out.println("--- compatibility and named founding endowment ---");
            BuildingManager b = new BuildingManager();
            b.initializeTemplates();
            long doors = 0, capacity = 0;
            for (BuildingsTemplate t : b.getTemplates()) if (t.getCategory() == BuildingType.RESIDENTIAL) {
                var r = t.getHousingRecipe();
                check(t.getName() + " mix keeps its doors and capacity", r.doors() == t.getDwellings() && r.capacity() == t.getCapacity());
                b.addStack(t, 2, true); doors += 2L * t.getDwellings(); capacity += 2L * t.getCapacity();
            }
            HousingInventory h = b.getHousingInventory();
            near("all three physical door totals derive from the registry", b.getTotalHomes(), doors);
            near("founding places add capacity without saleable doors", b.getTotalHouseCapacity(), capacity + h.foundingCapacity());
            near("endowment is separately named", h.foundingCapacity(), HousingInventory.FOUNDING_PLACES);
            near("RE owns the structures, without the endowment", h.ownedCapacity(REAL_ESTATE, false), capacity);
            refused("the endowment cannot be conveyed as a title", () -> h.transfer(new HousingInventory.Key(-1, "founding"), REAL_ESTATE, HOUSEHOLD, 1, 0));

            out.println("--- mixed recipe delivered by one real completion ---");
            BuildingManager mixed = mixed(2, false);
            HousingInventory m = mixed.getHousingInventory();
            near("sites contain the whole mixed pipeline", m.pipelineHomes(), 6);
            near("sites create no finished homes", m.homes(), 0);
            near("zero work emits no completion", mixed.advanceConstruction(0).size(), 0);
            var events = mixed.advanceConstruction(20);
            check("one aggregate event carries both structures and their immutable mix", events.size() == 1
                    && events.get(0).quantity == 2 && events.get(0).housingRecipe.mix().size() == 2);
            near("completion delivers each small home once", m.owned(key("small"), REAL_ESTATE), 2);
            near("completion delivers each family home once", m.owned(key("family"), REAL_ESTATE), 4);
            near("mixed capacity remains physical capacity plus the endowment", m.capacity(), 20 + m.foundingCapacity());
            near("pipeline has emptied", m.pipelineHomes(), 0);
            near("repeating work cannot finish the same homes twice", mixed.advanceConstruction(20).size(), 0);
            near("size two doors are the mixed recipe's small class", mixed.homesBySize()[2], 2);
            near("size four doors are the mixed recipe's family class", mixed.homesBySize()[4], 4);

            out.println("--- fractional titles, occupancy, listings and split/merge ---");
            var k = key("family");
            m.match(new double[] { 0, 0, 0, 0, 2.5 });
            near("actual size match assigns fractional lets to the family class", m.occupied(k, REAL_ESTATE), 2.5);
            near("zero class lets create no phantom occupants", m.occupied(key("small"), REAL_ESTATE), 0);
            m.list(k, REAL_ESTATE, 1, 1.25);
            m.transfer(k, REAL_ESTATE, HOUSEHOLD, .75, .5);
            near("fractional title arrives once", m.owned(k, HOUSEHOLD), .75);
            near("occupied transfer retains the tenants", m.occupied(k, HOUSEHOLD), .5);
            near("ownership sums to physical homes", sumOwned(m, k), 4);
            near("occupancy is conserved through a sale", sumOccupied(m, k), 2.5);
            near("listings are subsets and add no homes", m.homes(), 6);
            near("owned capacity excludes conveyed capacity", m.ownedCapacity(REAL_ESTATE, false), 17);
            String before = new com.google.gson.Gson().toJson(m.forSave());
            refused("overselling is atomic", () -> m.transfer(k, REAL_ESTATE, ESTATE, 8, 0));
            refused("occupied offer cannot exceed occupants", () -> m.list(k, REAL_ESTATE, 0, 3));
            refused("non-finite quantities are refused", () -> m.occupy(k, REAL_ESTATE, Double.NaN));
            check("every refused operation leaves all cohorts unchanged", before.equals(new com.google.gson.Gson().toJson(m.forSave())));
            m.transfer(k, HOUSEHOLD, ESTATE, .25, .125);
            m.transfer(k, ESTATE, HOUSEHOLD, .25, .125);
            near("equivalent title parts merge back into one quantity", m.owned(k, HOUSEHOLD), .75);
            near("equivalent occupant parts merge too", m.occupied(k, HOUSEHOLD), .5);
            check("empty estate cohort is coalesced away", m.forSave().stream().noneMatch(x -> x.owner == ESTATE));
            m.transfer(k, HOUSEHOLD, OUTBOUND, 0, 0);
            check("zero transfer creates no cohort", m.forSave().stream().noneMatch(x -> x.owner == OUTBOUND));
            refused("a physical removal cannot destroy a conveyed title", () -> mixed.getStacksView().get(0).removeQuantity(2));
            near("refused removal leaves physical homes whole", m.homes(), 6);

            out.println("--- fixed book and collateral allocation ---");
            var first = m.allocate(k, HOUSEHOLD, .25, 1000, 500, 800);
            var second = m.allocate(k, HOUSEHOLD, .5, 1000, 500, 800);
            var whole = m.allocate(k, HOUSEHOLD, .75, 1000, 500, 800);
            near("partial structure book allocations sum", first.structure() + second.structure(), whole.structure());
            near("partial land allocations sum", first.land() + second.land(), whole.land());
            near("partial collateral release sums", first.collateral() + second.collateral(), whole.collateral());
            double structure = 0, land = 0, collateral = 0;
            for (var s : m.stock()) for (var o : HousingInventory.Owner.values()) {
                var a = m.allocate(s.key(), o, m.owned(s.key(), o), 1000, 500, 800);
                structure += a.structure(); land += a.land(); collateral += a.collateral();
            }
            near("all titles allocate the project structure book once", structure, 1000);
            near("all titles allocate the project land book once", land, 500);
            near("all titles allocate the project collateral once", collateral, 800);

            out.println("--- represented stock scaling leaves dimensions fixed ---");
            BuildingManager big = mixed(2000, true);
            HousingInventory huge = big.getHousingInventory();
            huge.occupy(k, REAL_ESTATE, 2500);
            huge.list(k, REAL_ESTATE, 1000, 1250);
            huge.transfer(k, REAL_ESTATE, HOUSEHOLD, 750, 500);
            check("1000x structures still have exactly two physical class rows", huge.stock().size() == m.stock().size());
            check("1000x titles/listings still have the same saved cohort count", huge.forSave().size() == m.forSave().size());
            near("1000x stock counts whole", huge.homes(), m.homes() * 1000);
            near("1000x fractional owned share scales", huge.owned(k, HOUSEHOLD), m.owned(k, HOUSEHOLD) * 1000);

            out.println("--- bounded matrix serialization and pure reads ---");
            DataSave data = new DataSave(); data.setHousingInventory(m.forSave());
            DataSave decoded = new com.google.gson.Gson().fromJson(new com.google.gson.Gson().toJson(data), DataSave.class);
            HousingInventory restored = mixed(2, true).getHousingInventory();
            restored.restore(decoded.getHousingInventory());
            near("fractional owners survive DataSave serialization", restored.owned(k, HOUSEHOLD), .75);
            near("occupied owners survive DataSave serialization", restored.occupied(k, HOUSEHOLD), .5);
            near("occupied listings survive DataSave serialization", restored.listed(k, REAL_ESTATE, true), m.listed(k, REAL_ESTATE, true));
            String restoredBefore = new com.google.gson.Gson().toJson(restored.forSave());
            for (int n = 0; n < 100; n++) {
                restored.stock(); restored.homesBySize(); restored.capacity(); restored.pipelineHomes();
                restored.ownedCapacity(REAL_ESTATE, true); restored.vacant(k, HOUSEHOLD);
                restored.allocate(k, HOUSEHOLD, .5, 1000, 500, 800);
            }
            check("quotes and compatibility reads never advance saved state", restoredBefore.equals(new com.google.gson.Gson().toJson(restored.forSave())));
            var bad = decoded.getHousingInventory(); bad.get(0).occupied = 100;
            refused("invalid save cannot replace a valid matrix", () -> restored.restore(bad));
            check("failed restore is atomic", restoredBefore.equals(new com.google.gson.Gson().toJson(restored.forSave())));
            var badTitles = m.forSave();
            badTitles.stream().filter(x -> x.owner == HOUSEHOLD).findFirst().orElseThrow().owned = 100;
            refused("save cannot claim more titles than physical homes", () -> restored.restore(badTitles));
            check("refused excess titles preserve the old matrix", restoredBefore.equals(new com.google.gson.Gson().toJson(restored.forSave())));

            out.println("--- actual game save/rebuild preserves titles and RN2's debt baseline ---");
            GameFiles files = GameFiles.scratch("housing-inventory");
            Game city = new Game(files); city.run();
            BuildingManager cb = city.getBuildingManager();
            BuildingsTemplate house = cb.getTemplate(0);
            cb.addStack(house, 8, true);
            var home = new HousingInventory.Key(house.getId(), house.getHousingRecipe().mix().get(0).id());
            var ch = cb.getHousingInventory();
            ch.occupy(home, REAL_ESTATE, 4.5); ch.list(home, REAL_ESTATE, 1, 1);
            ch.transfer(home, REAL_ESTATE, HOUSEHOLD, .5, .25);
            city.getEconomyManager().updateHousing();
            var re = city.getSectors().realEstate();
            double current = re.getOwnedHousingCapacity();
            re.setOwnedHousingCapacity(current * .95);
            double savedCount = re.getOwnedHousingCapacity(), savedShare = re.strandedDebtShare();
            check("fixture changed the capacity baseline and stranded debt share", savedCount > 0 && savedCount < current && savedShare > 0);
            check("fixture wrote only its scratch save", city.saveGame(10, "RES2 inventory").ok);
            Game back = new Game(files); back.loadGameSave(10);
            check("actual game loader accepted its inventory save", back.getLoadFailure() == null);
            var bh = back.getBuildingManager().getHousingInventory();
            near("load rebuild restores conveyed quantity", bh.owned(home, HOUSEHOLD), ch.owned(home, HOUSEHOLD));
            near("load rebuild restores occupancy", bh.occupied(home, HOUSEHOLD), ch.occupied(home, HOUSEHOLD));
            near("load rebuild restores listing subsets", bh.listed(home, REAL_ESTATE, true), ch.listed(home, REAL_ESTATE, true));
            near("RN2 load keeps the changed saved count", back.getSectors().realEstate().getOwnedHousingCapacity(), savedCount);
            near("RN2 load keeps the saved stranded share", back.getSectors().realEstate().strandedDebtShare(), savedShare);
            long monthStarted = System.nanoTime();
            city.simulateMonths(1);
            double monthMs = (System.nanoTime() - monthStarted) / 1e6;
            back.simulateMonths(1);
            out.printf(java.util.Locale.ROOT, "   integrated scratch month %.3f ms; %d physical class rows, %d saved cohorts; scaled fixture %d/%d rows%n",
                    monthMs, ch.stock().size(), ch.forSave().size(), huge.stock().size(), huge.forSave().size());
            near("first month allocates debt from the same changed baseline", back.getSectors().realEstate().strandedDebtShare(), re.strandedDebtShare());
            near("first month counts the same RE-owned capacity", back.getSectors().realEstate().getOwnedHousingCapacity(), re.getOwnedHousingCapacity());
            near("first month retains the conveyed title", bh.owned(home, HOUSEHOLD), ch.owned(home, HOUSEHOLD));
            b.clearStacks();
            check("a new city clears both physical and assignment cohorts", h.stock().isEmpty() && h.forSave().isEmpty());
        } finally { System.setOut(out); }
        out.println(fails == 0 ? "HousingInventoryCheck: all checks passed" : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
