package ham.citybuildersim;

import ham.citybuildersim.sectors.CoalMining;
import ham.citybuildersim.sectors.CopperMining;
import ham.citybuildersim.sectors.CopperRefining;
import ham.citybuildersim.sectors.CementConcrete;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.Forestry;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Mining;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.Quarrying;
import ham.citybuildersim.sectors.SpreadPlanner;
import ham.citybuildersim.sectors.WoodProducts;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The materials pictograms' model, checked (0.7.141, batch I11;
 * docs/studies/spec-materials.md 2.13, 4's IndustryViewCheck line and 5's
 * I11 row): IndustryView's figures held to the model's own reads in three
 * played towns built to cause each part of the picture, and three at their
 * founding where the plans order.
 *
 * WHY. The nine materials industries' Operations pages draw IndustryView's
 * figures (batch I12): the fields as vessels, the feeds by where they came
 * from, each kind of works with its run and its gate, the products and who
 * took them, the power, the forest. A feed drawn from a seller that sold
 * nothing, a product whose parts do not add to what was made, a gate in
 * words the plan never says, or a view that wrote to the city it read would
 * be a confident wrong picture. The screen is checked by eye on the PC; this
 * holds what it draws.
 *
 * What this has to prove:
 *   1. THE NINE: the view covers the nine industries and no other, each
 *      under its roof, and reads every one of them in every town.
 *   2. THE FIELDS: mines on typed fields - the works town's thermal coal and
 *      hematite and limestone by fiat beside its founding coking field, the
 *      copper town's sulphide by fiat beside its oxide field: each vessel is
 *      the ground's own reads by type, its sites worked the buildings'
 *      claims; the forest's vessel what stands of it.
 *   3. EVERY GOOD BALANCES: in every industry of every town, each product's
 *      made, kept, sold at home, shipped, carried and lost foots to what its
 *      warehouse moved over the month, measured before and after, in units
 *      and in tonnes - and the industry's tonnes with it; each feed's parts
 *      add to what its works received, and what they received is what they
 *      needed.
 *   4. EVERY FIGURE IS THE MODEL'S: a product's and a feed's figures are its
 *      rows'; its buyers and sellers the month's fills, adding to the rows;
 *      every kind's count, one-more appraisal, last earnings and idle months
 *      its sector's own; the kind the view would order is what the plan
 *      orders.
 *   5. FROM WHERE: a works on imported feed (the smelter's concentrate), a
 *      captive chain (coal to coke to the blast furnace, the sawmill's
 *      residues to the pellet and pulp mills, anode to cathode to rod), old
 *      metal bought (steel at the yard, copper at the scrap refinery), and
 *      goods sold at home across industries (ore, coal, limestone, acid,
 *      sawlogs), limestone no buyer took carried into aggregate.
 *   6. THE POWER: made less drawn is sold less bought, every industry; the
 *      coke ovens sell their surplus and the pulp mill its 13,500 kW.
 *   7. THE FOREST: a forest with a crew - each figure the forest's and
 *      Forestry's own; it settles where Forestry.settlesAt() says with the
 *      law and at the regrowth rule's depletion without; with 40% protected,
 *      at no less than 40%.
 *   8. THE WORDS AND COLOURS: every good the nine make or use has a colour of
 *      its own; every kind, vessel, part and line has its words.
 *   9. PURE: read twice, the same view; reading it makes no production row;
 *      the save before a read and after it is the same, byte for byte, but
 *      for its time stamp.
 *  10. AFTER A LOAD: a saved town's twin reads its month as not counted
 *      until one runs.
 *  11. THE PLAN'S ORDER: in towns at their founding where kinds pass every
 *      gate - a longwall's coal unwashed, a forest with no crew, ore with no
 *      mine - each sector's plan orders the kind the view would.
 *  12. THE PICTURE (0.7.143, batch I12; IndustryPicture): every industry the
 *      towns draw, at the 1,389 x 868 window's width - its ribbons to scale,
 *      each pair's its flows' quantity times one scale and each node's the
 *      view's figures; every label's figure the view's; nothing off the
 *      frame; each under its roof; nothing drawn with nothing standing.
 *
 * Every fixture causes its condition.
 */
public class IndustryViewCheck {

    static int fails = 0;
    static PrintStream out = System.out;
    static PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-100s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    /** Within a millionth of the larger, or of one: the rows are sums of many fills. */
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-6 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    /** Months a town plays before the month it is read on. */
    static final int SETTLE_MONTHS = 2;

    /** Old metal arisen before the month read, tonnes before recovery: enough for one Scrap Yard's 10,500 t and some to landfill. */
    static final double OLD_STEEL = 14_000;
    /** ...and old copper, for one Scrap Copper Refinery's 4,400 t. */
    static final double OLD_COPPER = 4_000;

    /** A town, what its industries' stock was before the month read, and its files. */
    record Town(String name, Game game, Map<String, Map<Good, Double>> stockBefore) { }

    static final List<GameFiles> FILES = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);

        Town works = works(), copper = copper(), wood = wood();
        List<Town> towns = List.of(works, copper, wood);
        for (Town t : towns) printTown(t);

        theNine(towns);
        theFields(works, copper, wood);
        everyGoodBalances(towns);
        everyFigure(towns);
        fromWhere(works, copper, wood);
        thePower(towns, works, wood);
        theForest(wood);
        wordsAndColours(towns);
        pure(works);
        afterALoad(copper);
        thePlansOrder();
        thePicture(towns);

        for (GameFiles f : FILES) LongPlaytest.cleanUp(f.getDirectory().getParent());
        out.println();
        if (fails == 0) {
            out.println("All checks passed.");
        } else {
            out.println(fails + " FAILED");
            System.exit(fails);
        }
    }

    /* ============================ the towns ============================ */

    /**
     * The works town: CoalMiningCheck's coal town (its founding coking field
     * and a thermal site by fiat), a hematite and a limestone site by fiat on
     * it, and a chain on them - an Iron Mine, a Surface and an Underground
     * Coal Mine and a Preparation Plant, a Quarry, Coke Ovens and a Mini Blast
     * Furnace, a Scrap Yard and an Electric Arc Furnace, a Lime Kiln - with
     * old steel arising for the yard every month.
     */
    static Town works() {
        Game g = CoalMiningCheck.coalTown("industryviewcheck-works", 1);
        FILES.add(g.getGameFiles());
        LandManager land = g.getLandManager();
        BuildingManager b = g.getBuildingManager();
        quietly(() -> {
            land.restoreSites(Resource.IRON, land.getSites(Resource.IRON) + 1,
                    land.getOwnedAmount(Resource.IRON) + Resource.IRON.amountPerSite());
            land.restoreSites(Resource.STONE, land.getSites(Resource.STONE) + 2,
                    land.getOwnedAmount(Resource.STONE) + 2 * Resource.STONE.amountPerSite());
            for (int id : new int[] { Mining.IRON_MINE, CoalMining.SURFACE_MINE, CoalMining.UNDERGROUND_MINE,
                    CoalMining.PREPARATION_PLANT, Quarrying.QUARRY, Quarrying.QUARRY, HeavyIndustry.COKE_OVENS,
                    HeavyIndustry.MINI_BLAST_FURNACE, HeavyIndustry.SCRAP_YARD, HeavyIndustry.ARC_FURNACE, CementConcrete.LIME_KILN }) {
                b.addStack(b.getTemplate(id), 1, true);
            }
        });
        return play("the works town", g, () -> g.getOldMetal().arise(OldMetal.Source.DEMOLITIONS, Sectors.REAL_ESTATE, OLD_STEEL));
    }

    /**
     * The copper town: CopperMiningCheck's (its founding oxide field and a
     * sulphide site by fiat) with a Copper Mine & Mill and a Leach & SX-EW
     * Plant on them, and every works of Copper Refining - a smelter far bigger
     * than the mine, so its concentrate is mostly landed; a refinery and a rod
     * mill on its anode and cathode; a scrap refinery on old copper arising
     * every month.
     */
    static Town copper() {
        Game g = CopperMiningCheck.copperTown("industryviewcheck-copper", 1);
        FILES.add(g.getGameFiles());
        BuildingManager b = g.getBuildingManager();
        quietly(() -> {
            for (int id : new int[] { CopperMining.MINE_AND_MILL, CopperMining.LEACH_PLANT, CopperRefining.SMELTER,
                    CopperRefining.ELECTROLYTIC_REFINERY, CopperRefining.SCRAP_REFINERY, CopperRefining.ROD_MILL }) {
                b.addStack(b.getTemplate(id), 1, true);
            }
        });
        return play("the copper town", g,
                () -> g.getOldMetal().copper().arise(OldMetal.Source.DEMOLITIONS, Sectors.REAL_ESTATE, OLD_COPPER));
    }

    /** The wood town: ForestryCheck's forest town with a Logging Camp on its forest, and a Sawmill, a Pulp Mill and a Wood Pellet Mill. */
    static Town wood() {
        Game g = ForestryCheck.forestTown("industryviewcheck-wood");
        FILES.add(g.getGameFiles());
        BuildingManager b = g.getBuildingManager();
        quietly(() -> {
            for (int id : new int[] { Forestry.LOGGING_CAMP, WoodProducts.SAWMILL, WoodProducts.PULP_MILL, WoodProducts.PELLET_MILL }) {
                b.addStack(b.getTemplate(id), 1, true);
            }
        });
        return play("the wood town", g, null);
    }

    /** Hands a town its workers, plays SETTLE_MONTHS, notes every industry's stock, and plays the month it is read on - `each` before every month. */
    static Town play(String name, Game g, Runnable each) {
        MaterialsCheck.handWorkers(g);
        for (int m = 0; m < SETTLE_MONTHS; m++) {
            if (each != null) each.run();
            quietly(() -> g.simulateMonths(1));
        }
        Map<String, Map<Good, Double>> before = new HashMap<>();
        for (String key : IndustryView.INDUSTRIES) {
            Sector s = g.getSectors().byKey(key);
            Map<Good, Double> stock = new HashMap<>();
            for (Good good : s.goodsMade()) stock.put(good, good.stockable() ? s.getStock(good) : 0.0);
            before.put(key, stock);
        }
        if (each != null) each.run();
        quietly(() -> g.simulateMonths(1));
        return new Town(name, g, before);
    }

    static void printTown(Town t) {
        out.printf("%n%s at month %,d: %,d people%n", t.name(), t.game().getMonth(), t.game().getPopulationManager().getPopulation());
        for (IndustryView.View v : IndustryView.all(t.game())) {
            if (v.standing() <= 0) continue;
            SectorFlow.Plant plant = SectorFlow.plant(v.sector());
            StringBuilder ratios = new StringBuilder();
            for (int i = 0; i < SectorFlow.THROTTLES.length; i++) {
                ratios.append(String.format(" %s %.0f%%", SectorFlow.THROTTLES[i], 100 * plant.ratios()[i]));
            }
            out.printf("  %s; %d feeds, %d products, %d kinds;%s%n", IndustryView.headline(v), v.feeds().size(), v.products().size(),
                    v.built().size(), ratios);
        }
    }

    /* ============================ 1. THE NINE ============================ */

    static void theNine(List<Town> towns) {
        out.println("\n--- 1. the nine industries, each under its roof, and no other ---");
        Sectors sectors = towns.get(0).game().getSectors();
        int covered = 0;
        boolean others = true;
        for (String key : Sectors.KEYS) {
            Sector s = sectors.byKey(key);
            if (IndustryView.covers(s)) covered++;
            else others &= IndustryView.of(towns.get(0).game(), s) == null;
        }
        assertTrue("it covers the nine materials industries of Sectors.KEYS, and reads no other sector (null for each)",
                covered == 9 && IndustryView.INDUSTRIES.size() == 9 && others);
        assertTrue("...each under its roof: the four that dig a pit, Forestry its camp, the smelters a furnace, the kilns a kiln,"
                        + " the mills a mill",
                IndustryView.roofOf(sectors.mining()) == IndustryView.Roof.PIT
                        && IndustryView.roofOf(sectors.coalMining()) == IndustryView.Roof.PIT
                        && IndustryView.roofOf(sectors.quarrying()) == IndustryView.Roof.PIT
                        && IndustryView.roofOf(sectors.copperMining()) == IndustryView.Roof.PIT
                        && IndustryView.roofOf(sectors.forestry()) == IndustryView.Roof.CAMP
                        && IndustryView.roofOf(sectors.heavyIndustry()) == IndustryView.Roof.FURNACE
                        && IndustryView.roofOf(sectors.copperRefining()) == IndustryView.Roof.FURNACE
                        && IndustryView.roofOf(sectors.cementConcrete()) == IndustryView.Roof.KILN
                        && IndustryView.roofOf(sectors.woodProducts()) == IndustryView.Roof.MILL);
        boolean all = true;
        StringBuilder drawn = new StringBuilder();
        for (Town t : towns) {
            List<IndustryView.View> views = IndustryView.all(t.game());
            all &= views.size() == 9;
            int n = 0;
            for (IndustryView.View v : views) {
                all &= v.sector() == t.game().getSectors().byKey(v.sector().key()) && v.roof() == IndustryView.roofOf(v.sector())
                        && v.counted() && v.standing() == v.sector().buildingsStanding();
                if (v.drawn()) n++;
            }
            drawn.append(String.format(" %s %d;", t.name(), n));
        }
        report("every town reads all nine, counted, each its sector's own and its buildings standing", all,
                "drawn:" + drawn);
    }

    /* ============================ 2. THE FIELDS ============================ */

    static void theFields(Town works, Town copper, Town wood) {
        out.println("\n--- 2. the fields by type, each a vessel filled to what remains ---");
        for (Town t : List.of(works, copper)) {
            Game g = t.game();
            LandManager land = g.getLandManager();
            for (IndustryView.View v : IndustryView.all(g)) {
                if (!(v.sector() instanceof ExtractionSector ex) || v.sector() instanceof Forestry) continue;
                Resource r = ex.resource();
                boolean same = true;
                int typed = 0;
                for (Deposit.FieldType type : r.typesOf()) {
                    IndustryView.Vessel vessel = null;
                    for (IndustryView.Vessel x : v.fields()) if (x.type() == type) vessel = x;
                    int sites = land.getSites(r, type);
                    double owned = land.getOwnedAmount(r, type);
                    if (sites <= 0 && !(owned > 0)) {
                        same &= vessel == null;
                        continue;
                    }
                    typed++;
                    // ...the works on ANY where the packing puts them (LandManager.packed(), Quarrying's reading of its quarries).
                    List<LandManager.Claim> claims = g.claimsOn(r);
                    int[][] packed = land.packed(r, claims);
                    int onAny = 0, k = r.typesOf().indexOf(type);
                    for (int i = 0; i < packed.length; i++) if (claims.get(i).type() == null) onAny += packed[i][k];
                    same &= vessel != null && vessel.resource() == r && vessel.sites() == sites && vessel.owned() == owned
                            && vessel.remaining() == land.getRemaining(r, type) && vessel.worked() == g.committedOn(r, type)
                            && vessel.onAny() == onAny && vessel.taking() <= sites && vessel.filled() >= 0 && vessel.filled() <= 1;
                }
                if (typed == 0) continue;
                StringBuilder s = new StringBuilder();
                for (IndustryView.Vessel x : v.fields()) s.append(" ").append(IndustryView.vesselWords(x)).append(";");
                report(String.format("%s, %s: each type's vessel is the ground's reads - sites, owned, what remains, the sites"
                        + " its works claim, on the type and on ANY", t.name(), v.sector().label()), same, s.toString());
            }
        }
        Game g = works.game();
        IndustryView.View coal = IndustryView.of(g, g.getSectors().coalMining());
        IndustryView.Vessel thermal = null, coking = null;
        for (IndustryView.Vessel x : coal.fields()) {
            if (x.type() == Deposit.FieldType.THERMAL) thermal = x;
            if (x.type() == Deposit.FieldType.COKING) coking = x;
        }
        report("fixture: the works town's Surface Coal Mine works a thermal site and its Underground Coal Mine a coking one, each"
                        + " type's vessel drawn down by what was dug",
                thermal != null && coking != null && thermal.worked() >= 1 && coking.worked() >= 1
                        && thermal.remaining() < thermal.owned() && coking.remaining() < coking.owned(),
                thermal == null || coking == null ? "missing" : IndustryView.vesselWords(thermal) + "; " + IndustryView.vesselWords(coking));
        IndustryView.View iron = IndustryView.of(g, g.getSectors().mining());
        IndustryView.Vessel hematite = null;
        for (IndustryView.Vessel x : iron.fields()) if (x.type() == Deposit.FieldType.HEMATITE) hematite = x;
        report("...and its Iron Mine a hematite site", hematite != null && hematite.worked() >= 1 && hematite.remaining() < hematite.owned(),
                hematite == null ? "missing" : IndustryView.vesselWords(hematite));
        IndustryView.View stone = IndustryView.of(g, g.getSectors().quarrying());
        IndustryView.Vessel limestone = null;
        for (IndustryView.Vessel x : stone.fields()) if (x.type() == Deposit.FieldType.LIMESTONE) limestone = x;
        report("...and its two Quarries, which stand on ANY stone, on its two limestone sites by fiat, where the packing put them",
                limestone != null && limestone.worked() == 0 && limestone.onAny() == 2 && limestone.taking() == 2,
                limestone == null ? "missing" : IndustryView.vesselWords(limestone));
        Game w = wood.game();
        IndustryView.View forest = IndustryView.of(w, w.getSectors().forestry());
        LandManager land = w.getLandManager();
        report("the forest town's forest is one vessel: its full stock, and what stands of it",
                forest.fields().size() == 1 && forest.fields().get(0).resource() == Resource.FOREST
                        && forest.fields().get(0).owned() == land.getOwnedAmount(Resource.FOREST)
                        && forest.fields().get(0).remaining() == land.getRemaining(Resource.FOREST),
                forest.fields().isEmpty() ? "none" : IndustryView.vesselWords(forest.fields().get(0)));
        IndustryView.View works2 = IndustryView.of(g, g.getSectors().heavyIndustry());
        assertTrue("a works has no fields", works2.fields().isEmpty() && works2.forest() == null);
    }

    /* ============================ 3. EVERY GOOD BALANCES ============================ */

    static void everyGoodBalances(List<Town> towns) {
        out.println("\n--- 3. every good balances: made = kept + sold + shipped + carried + lost + what the warehouse moved ---");
        for (Town t : towns) {
            int goods = 0, wrong = 0;
            StringBuilder bad = new StringBuilder();
            boolean tonnes = true;
            for (IndustryView.View v : IndustryView.all(t.game())) {
                Map<Good, Double> before = t.stockBefore().get(v.sector().key());
                double madeT = 0, outT = 0;
                for (IndustryView.Product p : v.products()) {
                    double moved = p.stock() - before.getOrDefault(p.good(), 0.0);
                    goods++;
                    if (!near(p.intoStock(), moved) || p.made() < -1e-9) {
                        wrong++;
                        bad.append(String.format(" %s %s: into stock %.4f, moved %.4f;", v.sector().label(), p.good(),
                                p.intoStock(), moved));
                    }
                    double tpu = p.good().tonnesPerUnit();
                    madeT += (p.made() + p.carriedIn()) * tpu;
                    outT += (p.captive() + p.home() + p.exported() + p.carriedOut() + p.lost() + moved) * tpu;
                }
                tonnes &= near(madeT, outT);
            }
            report(String.format("%s: each of its industries' %d goods made foots to where it went and its warehouse's move,"
                    + " measured before and after the month", t.name(), goods), wrong == 0, wrong == 0 ? "" : bad.toString());
            assertTrue("...and each industry's tonnes made to its tonnes out, the warehouse's move in them", tonnes);
            int feeds = 0, unfed = 0;
            StringBuilder short_ = new StringBuilder();
            boolean parts = true;
            for (IndustryView.View v : IndustryView.all(t.game())) {
                for (IndustryView.Feed f : v.feeds()) {
                    double sum = 0;
                    for (IndustryView.Part p : f.parts()) sum += p.units();
                    parts &= near(sum, f.received());
                    // ...the lorries' diesel is drawn on top of what the works needed (Sector, AND THE DIESEL THEY BURN).
                    double wanted = f.needed() + f.lorries();
                    feeds++;
                    if (!near(f.received(), wanted)) {
                        unfed++;
                        short_.append(String.format(" %s %s: received %.4f, needed %.4f;", v.sector().label(), f.good(),
                                f.received(), wanted));
                    }
                }
            }
            assertTrue(String.format("%s: each feed's parts - its own works, old metal, each seller, the world - add to what its"
                    + " works received", t.name()), parts);
            report(String.format("...and what they received of each of its %d feeds is what they needed at the month's rate, the"
                    + " lorries' diesel beside it", feeds), unfed == 0, unfed == 0 ? "" : short_.toString());
        }
    }

    /* ============================ 4. EVERY FIGURE IS THE MODEL'S ============================ */

    static void everyFigure(List<Town> towns) {
        out.println("\n--- 4. every figure is the model's: rows, fills, counts, appraisals and the plan's order ---");
        for (Town t : towns) {
            Game g = t.game();
            BuildingManager b = g.getBuildingManager();
            BusinessInvestment plans = g.getBusinessInvestment();
            boolean rows = true, fills = true, counts = true, appraisals = true, last = true, power = true, sign = true;
            int kinds = 0, idleKinds = 0;
            int[] plan = new int[3];
            StringBuilder why = new StringBuilder();
            for (IndustryView.View v : IndustryView.all(g)) {
                Sector s = v.sector();
                for (IndustryView.Product p : v.products()) {
                    Sector.Output o = s.outputRow(p.good());
                    double carried = s instanceof Quarrying q ? q.carried() : 0;
                    double made = o == null ? 0 : o.produced + o.exportBound + o.captive;
                    if (p.good() == Good.LIMESTONE) made += carried;
                    if (p.good() == Good.AGGREGATE) made -= carried;
                    rows &= o != null && p.made() == made && p.captive() == o.captive && p.home() == o.soldLocal
                            && p.exported() == o.exported && p.stock() == (p.good().stockable() ? s.getStock(p.good()) : 0);
                    double sum = 0;
                    for (IndustryView.Part x : p.buyers()) sum += x.units();
                    fills &= near(sum, p.home()) && sold(g, p.good(), s.key(), p.buyers());
                }
                for (IndustryView.Feed f : v.feeds()) {
                    Sector.Input in = s.inputRow(f.good());
                    rows &= in == null ? f.home() == 0 && f.imported() == 0
                            : f.home() == in.boughtLocal && f.imported() == in.imported && f.needed() == in.needed;
                    Sector.Output own = s.outputRow(f.good());
                    rows &= f.captive() == (s.captive(f.good()) && own != null ? own.captive : 0);
                    double sum = 0;
                    for (IndustryView.Part x : f.sellers()) sum += x.units();
                    fills &= near(sum, f.home()) && bought(g, f.good(), s.key(), f.sellers());
                }
                int[] building = b.getUnderConstructionById();
                List<SpreadPlanner.Candidate> list = s instanceof ProcessingSector ps ? ps.appraise(plans, g) : null;
                for (IndustryView.Unit u : v.units()) {
                    kinds++;
                    BuildingsTemplate tp = u.template();
                    counts &= u.standing() == b.getQuantity(tp.getId()) && u.onSite() == building[tp.getId()] && u.rate() == v.rate()
                            && u.feedShare() >= 0 && u.feedShare() <= 1;
                    if (s instanceof ProcessingSector ps) {
                        SpreadPlanner.Candidate c = null;
                        for (SpreadPlanner.Candidate x : list) if (x.template() == tp) c = x;
                        appraisals &= same(u.oneMore(), c);
                        last &= u.idleMonths() == ps.idleMonths(tp)
                                && (u.standing() > 0 ? same(u.lastEarns(), ps.lastEarns(tp)) : Double.isNaN(u.lastEarns()));
                        // ...counted at the month's end as the view reads it: idle this month exactly where the last one loses.
                        if (u.standing() > 0) {
                            idleKinds += u.idleMonths() > 0 ? 1 : 0;
                            sign &= (u.idleMonths() > 0) == (u.lastEarns() < 0);
                        }
                    } else if (s instanceof ExtractionSector ex) {
                        appraisals &= same(u.oneMore(), ex.appraise(tp, plans)) && (u.oneMore() == null) == !ex.isWeighed(tp)
                                && u.workedOut() == ex.isWorkedOut(tp) && Double.isNaN(u.lastEarns());
                    }
                }
                // ...and the plan orders the kind the view would: the best that passes every gate, or none.
                String disagrees = planAgrees(g, v, plan);
                if (disagrees != null) why.append(" ").append(s.label()).append(": ").append(disagrees).append(";");
                IndustryView.Power pw = v.power();
                power &= pw.made() == s.getPowerMade() && pw.drawn() == s.getPowerDrawn() && pw.bought() == s.getGridDraw()
                        && pw.sold() == s.getPowerSurplus() && pw.soldValue() == s.getPowerSoldValue()
                        && v.lorries().vans() == s.vanFleet() && v.lorries().diesel() == s.getFleetDieselLitres();
            }
            assertTrue(t.name() + ": each product's and feed's figures are its production rows', its stock the warehouse's", rows);
            assertTrue("...its buyers and sellers are the month's fills, by party, adding to what the rows sold and bought at home", fills);
            assertTrue(String.format("...each of its %d kinds counts the buildings standing and on site, at its sector's rate", kinds),
                    counts);
            assertTrue("...one more of each is its sector's own appraisal - a works' from the plan's list, a mine's the plan's"
                    + " gates - every gate asked", appraisals);
            assertTrue("...a works' last earnings and idle months are the idle book's", last);
            report("...and the book counted each kind standing idle this month exactly where the view's last one loses money", sign,
                    idleKinds + " kind(s) idle");
            report("...and the kind the view would order is the one each sector's plan orders, or none where it orders none",
                    why.length() == 0, why.length() > 0 ? why.toString()
                            : String.format("%d ordered as the view says, %d ordering nothing, %d with an order in flight", plan[0], plan[1],
                                    plan[2]));
            assertTrue("...and its power and lorries are its sector's reads", power);
        }
    }

    /** Whether `parts` are exactly the month's fills of `g` that `key` sold in the city, by buyer. */
    static boolean sold(Game g, Good good, String key, List<IndustryView.Part> parts) {
        Map<String, Double> by = new HashMap<>();
        for (Trade t : g.getMarkets().get(good).getTrades()) {
            if (key.equals(t.seller()) && !t.isExport()) by.merge(t.buyer(), t.units(), Double::sum);
        }
        return matches(by, parts);
    }

    /** ...and the fills `key` bought in the city, by seller. */
    static boolean bought(Game g, Good good, String key, List<IndustryView.Part> parts) {
        Map<String, Double> by = new HashMap<>();
        for (Trade t : g.getMarkets().get(good).getTrades()) {
            if (key.equals(t.buyer()) && !t.isImport()) by.merge(t.seller(), t.units(), Double::sum);
        }
        return matches(by, parts);
    }

    static boolean matches(Map<String, Double> by, List<IndustryView.Part> parts) {
        int n = 0;
        for (Map.Entry<String, Double> e : by.entrySet()) if (e.getValue() > 0) n++;
        if (n != parts.size()) return false;
        for (IndustryView.Part p : parts) {
            if (p.origin() != IndustryView.Origin.HOME || !by.containsKey(p.party()) || by.get(p.party()) != p.units()) return false;
        }
        return true;
    }

    /** Whether two appraisals of a building are the same: its template, earnings, cost, first gate and every refusal. */
    static boolean same(SpreadPlanner.Candidate a, SpreadPlanner.Candidate b) {
        if (a == null || b == null) return a == b;
        return a.template() == b.template() && same(a.earns(), b.earns()) && same(a.cost(), b.cost()) && a.failed() == b.failed()
                && a.refusals().equals(b.refusals());
    }

    static boolean same(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /**
     * Whether the sector's plan orders what the view says it would (null) or
     * why not, `tally` counting {ordered, none, waiting}: with an order in
     * flight the plan waits; Coal Mining washes
     * first (planWash()), so a passing wash plant is its order; otherwise the
     * view's best() is the order, and with none passing it orders nothing.
     */
    static String planAgrees(Game g, IndustryView.View v, int[] tally) {
        Sector s = v.sector();
        BusinessInvestment plans = g.getBusinessInvestment();
        BusinessInvestment.Decision[] d = new BusinessInvestment.Decision[1];
        quietly(() -> d[0] = s.plan(plans, g));
        BuildingsTemplate want = null;
        if (s instanceof CoalMining cm && cm.unwashed() > 0) {
            List<SpreadPlanner.Candidate> wash = new ArrayList<>();
            for (IndustryView.Unit u : v.units()) {
                if (u.oneMore() != null && u.template().getWash() > 0 && u.template().makes(Good.THERMAL_COAL) <= 0) wash.add(u.oneMore());
            }
            SpreadPlanner.Candidate c = SpreadPlanner.best(wash);
            if (c != null) want = c.template();
        }
        if (want == null && v.best() != null) want = v.best().template();
        BuildingsTemplate got = d[0] == null ? null : d[0].template;
        boolean ordered = d[0] != null && d[0].build && d[0].quantity > 0 && got != null;
        if (!ordered && "already building".equals(d[0] == null ? null : d[0].reason)) {
            tally[2]++;
            return null;
        }
        if (want == null) {
            if (ordered) return "the plan orders " + got.getName() + ", the view none";
            tally[1]++;
            return null;
        }
        if (!ordered) return "the view would order " + want.getName() + ", the plan none: " + (d[0] == null ? "" : d[0].reason);
        if (got != want) return "the plan orders " + got.getName() + ", the view " + want.getName();
        tally[0]++;
        return null;
    }

    /* ============================ 5. FROM WHERE ============================ */

    static void fromWhere(Town works, Town copper, Town wood) {
        out.println("\n--- 5. from where: imported feed, captive chains, old metal, sold across industries, carried ---");
        Game g = copper.game();
        IndustryView.View refining = IndustryView.of(g, g.getSectors().copperRefining());
        IndustryView.Feed conc = refining.feed(Good.COPPER_CONC);
        report("a works on imported feed: the copper smelter takes far more concentrate than the town's one mine digs, the rest"
                        + " landed - its feed's world part the row's imports",
                conc != null && conc.imported() > conc.home() && conc.home() > 0 && conc.parts().get(conc.parts().size() - 1).origin()
                        == IndustryView.Origin.WORLD && IndustryView.hatched(IndustryView.Origin.WORLD),
                conc == null ? "none" : String.format("%s at home from %s, %s landed", IndustryView.figure(Good.COPPER_CONC, conc.home()),
                        conc.sellers().isEmpty() ? "nobody" : IndustryView.partyWords(conc.sellers().get(0).party(), g),
                        IndustryView.figure(Good.COPPER_CONC, conc.imported())));
        IndustryView.Product anode = refining.product(Good.ANODE), cathode = refining.product(Good.COPPER);
        IndustryView.Feed anodeIn = refining.feed(Good.ANODE), cathodeIn = refining.feed(Good.COPPER);
        report("a captive chain: the smelter's anode to the refinery, the refinery's cathode to the rod mill, each its own works'"
                        + " first - the product's captive part the feed's",
                anode != null && anodeIn != null && anode.captive() > 0 && anodeIn.captive() == anode.captive()
                        && cathode != null && cathodeIn != null && cathode.captive() > 0 && cathodeIn.captive() == cathode.captive()
                        && anode.parts().get(0).origin() == IndustryView.Origin.CAPTIVE,
                anode == null || cathode == null ? "none" : String.format("anode %s captive, cathode %s",
                        IndustryView.figure(Good.ANODE, anode.captive()), IndustryView.figure(Good.COPPER, cathode.captive())));
        IndustryView.Feed scrap = refining.feed(Good.COPPER_SCRAP);
        IndustryView.Metal oldCopper = refining.oldMetal();
        OldMetal.Pool cu = g.getOldMetal().copper();
        report("old metal bought: the scrap refinery takes the city's old copper first, its feed's old-metal part the pool's"
                        + " purchase, and the pool's month the view's",
                scrap != null && oldCopper != null && scrap.oldMetal() > 0 && scrap.oldMetal() == cu.lastBought()
                        && oldCopper.metal() == Good.COPPER && oldCopper.arose() == cu.lastArisen() && oldCopper.bought() == cu.lastBought()
                        && oldCopper.landfill() == cu.lastLandfill() && oldCopper.price() == cu.lastPrice()
                        && oldCopper.intake() == g.getSectors().copperRefining().oldCopperIntake(),
                oldCopper == null ? "none" : IndustryView.metalWords(oldCopper));
        IndustryView.View mining = IndustryView.of(g, g.getSectors().copperMining());
        IndustryView.Feed acid = mining.feed(Good.ACID);
        report("sold at home across industries: the leach plant's acid bought from Copper Refining's smelter",
                acid != null && acid.home() > 0 && acid.sellers().size() == 1 && Sectors.COPPER_REFINING.equals(acid.sellers().get(0).party())
                        && refining.product(Good.ACID) != null && refining.product(Good.ACID).buyers().stream()
                                .anyMatch(p -> Sectors.COPPER_MINING.equals(p.party())),
                acid == null ? "none" : IndustryView.figure(Good.ACID, acid.home()));

        Game w = works.game();
        IndustryView.View steel = IndustryView.of(w, w.getSectors().heavyIndustry());
        IndustryView.Product coke = steel.product(Good.MET_COKE), scrapMade = steel.product(Good.SCRAP);
        IndustryView.Feed cokeIn = steel.feed(Good.MET_COKE), coking = steel.feed(Good.COKING_COAL), ore = steel.feed(Good.IRON),
                lime = steel.feed(Good.LIMESTONE);
        report("the works town's chain: coking coal bought from Coal Mining, its ovens' coke to the blast furnace first, captive",
                coke != null && cokeIn != null && coking != null && coke.captive() > 0 && cokeIn.captive() == coke.captive()
                        && coking.sellers().stream().anyMatch(p -> Sectors.COAL_MINING.equals(p.party())),
                coke == null ? "none" : String.format("coke %s made, %s captive", IndustryView.figure(Good.MET_COKE, coke.made()),
                        IndustryView.figure(Good.MET_COKE, coke.captive())));
        report("...the blast furnace's ore from Iron Mining at home, the rest landed, and its limestone from Quarrying",
                ore != null && lime != null && ore.home() > 0 && ore.imported() > 0
                        && ore.sellers().stream().anyMatch(p -> Sectors.MINING.equals(p.party()))
                        && lime.sellers().stream().anyMatch(p -> Sectors.QUARRYING.equals(p.party())),
                ore == null ? "none" : String.format("ore %s at home, %s landed", IndustryView.figure(Good.IRON, ore.home()),
                        IndustryView.figure(Good.IRON, ore.imported())));
        IndustryView.Metal oldSteel = steel.oldMetal();
        OldMetal.Pool st = w.getOldMetal().steel();
        IndustryView.Unit yard = steel.unit(HeavyIndustry.SCRAP_YARD);
        IndustryView.Feed scrapIn = steel.feed(Good.SCRAP);
        report("...old steel bought at the yard: the pool's month the view's, the yard's scrap to the arc furnace first, captive,"
                        + " its run what the old steel bought let it",
                oldSteel != null && oldSteel.metal() == Good.STEEL && oldSteel.bought() == st.lastBought() && oldSteel.bought() > 0
                        && oldSteel.arose() == st.lastArisen() && oldSteel.bySource().containsKey(OldMetal.Source.DEMOLITIONS)
                        && scrapMade != null && scrapIn != null && scrapMade.captive() > 0 && scrapIn.captive() == scrapMade.captive()
                        && yard != null && yard.feedShare() > 0 && yard.feedShare() <= 1,
                oldSteel == null ? "none" : IndustryView.metalWords(oldSteel) + (yard == null ? "" : "; " + IndustryView.unitWords(yard)));
        IndustryView.View quarry = IndustryView.of(w, w.getSectors().quarrying());
        IndustryView.Product limestone = quarry.product(Good.LIMESTONE), aggregate = quarry.product(Good.AGGREGATE);
        double carried = w.getSectors().quarrying().carried();
        report("...the limestone no buyer took carried into aggregate: out of the one, into the other, the quarry's carry",
                carried > 0 && quarry.carried() == carried && limestone != null && aggregate != null && limestone.carriedOut() == carried
                        && aggregate.carriedIn() == carried && limestone.buyers().size() >= 2,
                limestone == null ? "none" : String.format("limestone %s dug, %s sold at home to %d buyers, %s carried",
                        IndustryView.figure(Good.LIMESTONE, limestone.made()), IndustryView.figure(Good.LIMESTONE, limestone.home()),
                        limestone.buyers().size(), IndustryView.figure(Good.LIMESTONE, carried)));
        IndustryView.View coal = IndustryView.of(w, w.getSectors().coalMining());
        CoalMining.Flow flow = coal.wash();
        IndustryView.Unit plant = coal.unit(CoalMining.PREPARATION_PLANT);
        report("...the coking mine's raw coal through the wash plant: the view's wash the sector's lift, the plant run on what it"
                        + " washed",
                flow != null && flow.washed() > 0 && flow.spoil() > 0 && plant != null && plant.feedShare() > 0
                        && same(flow.washed(), w.getSectors().coalMining().lift().washed()),
                flow == null ? "none" : String.format("raw %,.0f t, washed %,.0f, coking %,.0f, spoil %,.0f", flow.raw(), flow.washed(),
                        flow.coking(), flow.spoil()));

        IndustryView.Product cokingCoal = coal.product(Good.COKING_COAL);
        report("...and its coking coal the month's: what the rows made and idled is the lift's, the ground holding plenty",
                flow != null && cokingCoal != null && near(cokingCoal.made() + cokingCoal.idled(), flow.coking()),
                cokingCoal == null || flow == null ? "none" : String.format("made %,.1f t and idled %,.1f, the lift's %,.1f",
                        cokingCoal.made(), cokingCoal.idled(), flow.coking()));

        Game wd = wood.game();
        IndustryView.View mills = IndustryView.of(wd, wd.getSectors().woodProducts());
        IndustryView.Product residues = mills.product(Good.RESIDUES);
        IndustryView.Feed resIn = mills.feed(Good.RESIDUES), logs = mills.feed(Good.SAWLOGS);
        report("the wood town: the sawmill's residues to the pellet and pulp mills first, captive, the rest of their need landed;"
                        + " its sawlogs Forestry's at home",
                residues != null && resIn != null && residues.captive() > 0 && resIn.captive() == residues.captive()
                        && resIn.imported() > 0 && logs != null && logs.sellers().stream().anyMatch(p -> Sectors.FORESTRY.equals(p.party())),
                residues == null || logs == null ? "none" : String.format("residues %s captive, %s landed; sawlogs %s from Forestry",
                        IndustryView.figure(Good.RESIDUES, residues.captive()), IndustryView.figure(Good.RESIDUES, resIn.imported()),
                        IndustryView.figure(Good.SAWLOGS, logs.home())));
    }

    /* ============================ 6. THE POWER ============================ */

    static void thePower(List<Town> towns, Town works, Town wood) {
        out.println("\n--- 6. the power: made less drawn is sold less bought ---");
        boolean balance = true;
        for (Town t : towns) {
            for (IndustryView.View v : IndustryView.all(t.game())) {
                IndustryView.Power p = v.power();
                balance &= near(p.made() - p.drawn(), p.sold() - p.bought()) && p.sold() >= 0 && p.bought() >= 0;
            }
        }
        assertTrue("in every industry of every town, the power its works make less what they draw is what they sell the grid less"
                + " what they buy from it", balance);
        Game w = works.game();
        BuildingsTemplate ovens = w.getBuildingManager().getTemplate(HeavyIndustry.COKE_OVENS);
        IndustryView.Power steel = IndustryView.of(w, w.getSectors().heavyIndustry()).power();
        report("fixture: the works town's Coke Ovens make 8,000 kW of their gas: Iron & Steel makes them, and sells their surplus"
                        + " past their own draw",
                steel.made() == ovens.getGen() && steel.sold() == ovens.powerSold() && steel.sold() > 0 && steel.soldValue() > 0,
                IndustryView.powerWords(steel));
        Game wd = wood.game();
        BuildingsTemplate pulp = wd.getBuildingManager().getTemplate(WoodProducts.PULP_MILL);
        IndustryView.Power mills = IndustryView.of(wd, wd.getSectors().woodProducts()).power();
        report("...and the wood town's Pulp Mill its 13,500 kW, all of it sold, the sawmill's and the pellet mill's draws bought",
                mills.made() == pulp.getGen() && mills.sold() == pulp.powerSold() && mills.sold() == 13_500 && mills.bought() > 0,
                IndustryView.powerWords(mills));
    }

    /* ============================ 7. THE FOREST ============================ */

    static void theForest(Town wood) {
        out.println("\n--- 7. the forest: what stands, the cut, the planting line, where it settles ---");
        Game g = wood.game();
        Forestry fo = g.getSectors().forestry();
        LandManager land = g.getLandManager();
        IndustryView.View v = IndustryView.of(g, fo);
        IndustryView.Forest f = v.forest();
        report("fixture: the forest town's Logging Camp cut this month",
                f != null && f.cut() > 0 && f.nameplate() == fo.crewsCut(false) && f.cut() == fo.getCutThisMonth(),
                f == null ? "none" : String.format("%,.0f m3 of %,.0f at nameplate", f.cut(), f.nameplate()));
        assertTrue("its figures are the forest's own reads: full, standing, protected, cuttable, cleared",
                f.owned() == land.getOwnedAmount(Resource.FOREST) && f.standing() == land.getRemaining(Resource.FOREST)
                        && f.protectedWood() == land.getProtectedWood() && f.cuttable() == land.cuttableForest()
                        && f.cleared() == land.getForestClearedM3());
        assertTrue("...and Forestry's: the crews with those on site, the salvage, the stands, the planting word",
                f.committed() == fo.crewsCut(true) && f.salvage() == fo.getSalvageThisMonth()
                        && f.plantedGrowth() == fo.plantedGrowth(false) && f.planting().equals(fo.getPlantingWord())
                        && f.plantations() == g.getBuildingManager().getQuantity(Forestry.TREE_PLANTATION)
                        && f.woodlots() == g.getBuildingManager().getQuantity(Forestry.WOODLOT));
        assertTrue("the planting line is the regrowth Forestry's planting weighs the cut against, its stands on site counted as"
                        + " grown; the regrowth the month's end gives at its stands standing",
                f.plantingLine() == land.regrowthDue(fo.plantedGrowth(true)) && f.regrowth() == land.regrowthDue(fo.plantedGrowth(false))
                        && f.plantingLine() >= f.regrowth());
        report("it settles where Forestry.settlesAt() says, and with no law at the regrowth rule's depletion - the same here, the"
                        + " law at nothing",
                f.settlesWithLaw() == fo.settlesAt() && f.protectedShare() == 0
                        && f.settlesWithout() == Forestry.settlesAt(f.owned(), f.cleared(), f.cut(), f.plantedGrowth(), 0)
                        && f.settlesWithLaw() == f.settlesWithout(),
                String.join("; ", IndustryView.forestWords(f)));
        IndustryView.Unit camp = v.unit(Forestry.LOGGING_CAMP);
        assertTrue("the camp's run is the forest's cut over the crews' ask at the rate",
                camp != null && camp.feedShare() > 0 && near(camp.run() * fo.crewsCut(false), f.cut()));
        double share = .4;
        quietly(() -> g.setForestLaw(share, share));
        IndustryView.Forest law = IndustryView.of(g, fo).forest();
        report("with 40% of the forest protected, it settles at no less than 40% - Forestry.settlesAt() under the law - and with no"
                        + " law where it did",
                law.protectedShare() == share && law.settlesWithLaw() == fo.settlesAt() && law.settlesWithLaw() >= share - 1e-12
                        && law.settlesWithout() == f.settlesWithout() && law.settlesWithLaw() >= law.settlesWithout(),
                String.join("; ", IndustryView.forestWords(law)));
        quietly(() -> g.setForestLaw(0, 0));
    }

    /* ============================ 8. THE WORDS AND COLOURS ============================ */

    static void wordsAndColours(List<Town> towns) {
        out.println("\n--- 8. the words and the colours ---");
        Game g = towns.get(0).game();
        Set<Good> goods = EnumSet.noneOf(Good.class);
        for (String key : IndustryView.INDUSTRIES) {
            Sector s = g.getSectors().byKey(key);
            goods.addAll(s.goodsMade());
            goods.addAll(s.goodsUsed());
        }
        goods.remove(Good.VANS);
        Set<String> colours = new HashSet<>();
        boolean own = true;
        for (Good x : goods) {
            String c = IndustryView.colour(x);
            own &= c != null && c.matches("#[0-9a-f]{6}") && !c.equals(RefineryView.TEXT_3);
            colours.add(c);
        }
        report("every good the nine make or use has a colour of its own", own && colours.size() == goods.size(),
                goods.size() + " goods, " + colours.size() + " colours");
        boolean origins = true;
        for (IndustryView.Origin o : IndustryView.Origin.values()) origins &= IndustryView.colour(o).matches("#[0-9a-f]{6}");
        boolean types = true;
        for (Deposit.FieldType t : Deposit.FieldType.values()) types &= IndustryView.colour(Resource.IRON, t).matches("#[0-9a-f]{6}");
        assertTrue("...and every origin and every field type, the world's hatched", origins && types
                && IndustryView.hatched(IndustryView.Origin.WORLD) && !IndustryView.hatched(IndustryView.Origin.HOME));
        boolean words = true;
        int lines = 0;
        for (Town t : towns) {
            for (IndustryView.View v : IndustryView.all(t.game())) {
                words &= !IndustryView.headline(v).isEmpty() && !IndustryView.powerWords(v.power()).isEmpty();
                lines += 2;
                for (IndustryView.Unit u : v.units()) {
                    words &= !IndustryView.unitWords(u).isEmpty() && (u.oneMore() == null || !IndustryView.gateWords(u).isEmpty())
                            && (u.standing() == 0 || !(u.lastEarns() == u.lastEarns()) || !IndustryView.lastWords(u).isEmpty());
                    lines += 3;
                }
                for (IndustryView.Vessel x : v.fields()) {
                    words &= !IndustryView.vesselWords(x).isEmpty();
                    lines++;
                }
                for (IndustryView.Feed f : v.feeds()) {
                    for (IndustryView.Part p : f.parts()) {
                        words &= !IndustryView.partWords(p, t.game(), false).isEmpty();
                        lines++;
                    }
                }
                for (IndustryView.Product p : v.products()) {
                    for (IndustryView.Part x : p.parts()) {
                        words &= !IndustryView.partWords(x, t.game(), true).isEmpty();
                        lines++;
                    }
                }
                if (v.forest() != null) {
                    words &= IndustryView.forestWords(v.forest()).size() == 4;
                    lines += 4;
                }
            }
        }
        report("every headline, kind, gate, vessel, part, power line and forest line has its words", words, lines + " lines");
        assertTrue("...a good's figure in its own unit: tonnes, cubic metres, litres",
                IndustryView.figure(Good.STEEL, 8_306).equals("8,306 t") && IndustryView.figure(Good.SAWLOGS, 830_000).equals("830k m³")
                        && IndustryView.figure(Good.DIESEL, 2_000_000).equals("2.00M L")
                        && IndustryView.unitsWords(Good.LUMBER, 1_640).equals("1,640 m³"));
        IndustryView.Unit u = null;
        for (IndustryView.View v : IndustryView.all(g)) {
            for (IndustryView.Unit x : v.units()) if (x.oneMore() != null && !x.oneMore().passes() && u == null) u = x;
        }
        report("...and a gate one more fails, in the plan's words: its first refusal", u == null || IndustryView.gateWords(u)
                .equals("one more: " + u.oneMore().why()), u == null ? "none fails" : u.template().getName() + ": " + IndustryView.gateWords(u));
    }

    /* ============================ 9. PURE ============================ */

    static void pure(Town town) throws Exception {
        out.println("\n--- 9. pure ---");
        Game g = town.game();
        assertTrue("read twice, the same views, figure for figure and word for word", words(g).equals(words(g)) && !words(g).isEmpty());
        Sector homes = g.getSectors().realEstate();
        boolean before = homes.inputRow(Good.LIMESTONE) == null && homes.outputRow(Good.COKING_COAL) == null;
        IndustryView.all(g);
        assertTrue("...and reading them makes no production row (asked of a sector that buys and makes none of the goods)", before
                && homes.inputRow(Good.LIMESTONE) == null && homes.outputRow(Good.COKING_COAL) == null);
        String a = saved(g, 4), control = saved(g, 5);
        words(g);
        String after = saved(g, 6);
        report("the city saved before the nine are read and after is the same save, byte for byte but its time stamp",
                a.equals(control) && control.equals(after) && !a.isEmpty(),
                String.format("%,d bytes", after.length()));
    }

    /** Every view of the town and its words, as text. */
    static String words(Game g) {
        StringBuilder s = new StringBuilder();
        for (IndustryView.View v : IndustryView.all(g)) {
            s.append(v).append(IndustryView.headline(v)).append(IndustryView.powerWords(v.power()));
            for (IndustryView.Unit u : v.units()) {
                s.append(IndustryView.unitWords(u)).append(IndustryView.lastWords(u)).append(IndustryView.gateWords(u));
            }
            for (IndustryView.Vessel x : v.fields()) s.append(IndustryView.vesselWords(x));
            if (v.forest() != null) s.append(IndustryView.forestWords(v.forest()));
            if (v.oldMetal() != null) s.append(IndustryView.metalWords(v.oldMetal()));
            s.append(IndustryPicture.picture(g, v, FRAME_WIDTH, IndustryPicture.HEIGHT).texts()).append(IndustryPicture.strip(v));
        }
        return s.toString();
    }

    /** The town saved to a slot of its scratch folder, as text without the save's time stamp. */
    static String saved(Game g, int slot) throws Exception {
        quietly(() -> g.saveGame(slot));
        String text = new String(Files.readAllBytes(g.getGameFiles().saveFile(slot)), StandardCharsets.UTF_8);
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(text).getAsJsonObject();
        json.remove("savedAt");
        return json.toString();
    }

    /* ============================ 11. THE PLAN'S ORDER ============================ */

    /**
     * Towns at their founding, where kinds pass every gate - a sector with
     * nothing running weighs one more at nameplate - and each plan orders:
     * CoalMiningCheck's longwall with its raw coal unwashed, ForestryCheck's
     * forest with no crew, and MaterialsCheck's town on three hematite sites
     * by fiat; the staff handed in each (MaterialsCheck.handWorkers()).
     */
    static void thePlansOrder() {
        out.println("\n--- 11. the plan's order, in towns at their founding where kinds pass ---");
        Game coal = CoalMiningCheck.coalTown("industryviewcheck-plan-coal", 3);
        FILES.add(coal.getGameFiles());
        BuildingManager b = coal.getBuildingManager();
        quietly(() -> b.addStack(b.getTemplate(CoalMining.UNDERGROUND_MINE), 1, true));
        Game forest = ForestryCheck.forestTown("industryviewcheck-plan-forest");
        FILES.add(forest.getGameFiles());
        Game iron = MaterialsCheck.town("industryviewcheck-plan-iron");
        FILES.add(iron.getGameFiles());
        quietly(() -> iron.getLandManager().restoreSites(Resource.IRON, 3, 3 * Resource.IRON.amountPerSite()));
        int ordered = 0;
        StringBuilder why = new StringBuilder(), orders = new StringBuilder();
        for (Game g : List.of(coal, forest, iron)) {
            MaterialsCheck.handWorkers(g);
            int[] tally = new int[3];
            for (IndustryView.View v : IndustryView.all(g)) {
                int before = tally[0];
                String disagrees = planAgrees(g, v, tally);
                if (disagrees != null) why.append(" ").append(v.sector().label()).append(": ").append(disagrees).append(";");
                if (tally[0] > before) {
                    BusinessInvestment.Decision[] d = new BusinessInvestment.Decision[1];
                    quietly(() -> d[0] = v.sector().plan(g.getBusinessInvestment(), g));
                    IndustryView.Unit u = v.unit(d[0].template.getId());
                    orders.append(" ").append(v.sector().label()).append(": ").append(IndustryView.gateWords(u)).append(";");
                }
            }
            ordered += tally[0];
        }
        report("fixture: in each town the plan orders the kind the view would - the best that passes every gate, Coal Mining's"
                        + " wash plant first - and where none passes, nothing",
                why.length() == 0 && ordered >= 3, why.length() > 0 ? why.toString() : ordered + " ordered:" + orders);
    }

    /* ============================ 10. AFTER A LOAD ============================ */

    static void afterALoad(Town town) {
        out.println("\n--- 10. after a load ---");
        Game g = town.game();
        quietly(() -> g.saveGame(7));
        Game twin = new Game(g.getGameFiles());
        quietly(() -> twin.loadGameSave(7));
        IndustryView.View v = IndustryView.of(twin, twin.getSectors().copperRefining());
        boolean none = true;
        for (IndustryView.Product p : v.products()) none &= p.made() == 0 && p.home() == 0 && p.exported() == 0;
        report("the loaded twin reads its month as not counted, drawing nothing, its rows not saved",
                (twin.getLoadFailure() == null || twin.getLoadFailure().isEmpty()) && !v.counted() && !v.drawn() && v.standing() > 0
                        && none, IndustryView.headline(v));
        report("...and its picture is empty, saying a month must run", IndustryPicture.picture(twin, v, FRAME_WIDTH,
                IndustryPicture.HEIGHT).empty() && IndustryPicture.emptyWords(v).contains("once a month has run"), IndustryPicture.emptyWords(v));
        quietly(() -> twin.simulateMonths(1));
        IndustryView.View on = IndustryView.of(twin, twin.getSectors().copperRefining());
        assertTrue("...and a month on, it is counted and drawn again", on.counted() && on.drawn()
                && !IndustryPicture.picture(twin, on, FRAME_WIDTH, IndustryPicture.HEIGHT).empty());
    }

    /* ============================ 12. THE PICTURE ============================ */

    /** The picture's width at the 1,389 x 868 window: the Operations card's inside (RefineryViewCheck's 1,200). */
    static final double FRAME_WIDTH = 1_200;

    /** The slack a laid-out pixel is held to. */
    static final double PX = 1e-6;

    /**
     * The picture of every industry drawn in the three towns, at the 1,389 x
     * 868 window's width: every ribbon from one node's right edge to another's
     * left, its height its flow's quantity times the picture's one scale; each
     * node's ribbons the view's own figures times that scale - a product's band
     * what left the works, a taker's bar what it took, a source's bar what was
     * bought from it; every label's figure the view's, in IndustryView's
     * words; and nothing off the frame.
     */
    static void thePicture(List<Town> towns) {
        out.println("\n--- 12. the picture: ribbons to scale, every label's figure the view's, nothing off the frame at 1,389 x 868 ---");
        int pictures = 0, ribbons = 0, nodes = 0, labels = 0, roofs = 0;
        List<String> loose = new ArrayList<>(), unscaled = new ArrayList<>(), figures = new ArrayList<>(), off = new ArrayList<>();
        Set<IndustryView.Roof> roofed = EnumSet.noneOf(IndustryView.Roof.class);
        for (Town t : towns) {
            Game g = t.game();
            for (IndustryView.View v : IndustryView.all(g)) {
                if (!v.drawn()) continue;
                IndustryPicture.Picture p = IndustryPicture.picture(g, v, FRAME_WIDTH, IndustryPicture.HEIGHT);
                String who = v.sector().label();
                if (p.empty()) {
                    figures.add(who + " drew nothing");
                    continue;
                }
                pictures++;
                double k = p.scale();
                List<IndustryPicture.Flow> flows = IndustryPicture.trace(v);

                // Every ribbon attached at both ends, and each pair's ribbons its flows' quantity times the one scale.
                Map<String, Double> want = new HashMap<>(), drawn = new HashMap<>();
                Map<String, double[]> sides = new HashMap<>();
                for (IndustryPicture.Flow f : flows) want.merge(f.from() + ">" + f.to(), f.drawn() * k, Double::sum);
                for (RefineryView.Ribbon r : p.ribbons()) {
                    RefineryView.Place from = null, to = null;
                    for (RefineryView.Place q : p.places()) {
                        if (from == null && Math.abs(q.right() - r.x1()) < PX && r.a0() >= q.y() - PX && r.a1() <= q.y() + q.h() + PX) from = q;
                        if (to == null && Math.abs(q.x() - r.x2()) < PX && r.b0() >= q.y() - PX && r.b1() <= q.y() + q.h() + PX) to = q;
                    }
                    if (from == null || to == null) {
                        loose.add(String.format("%s: %.1f,%.1f-%.1f to %.1f,%.1f-%.1f", who, r.x1(), r.a0(), r.a1(), r.x2(), r.b0(), r.b1()));
                        continue;
                    }
                    ribbons++;
                    drawn.merge(from.key() + ">" + to.key(), r.height(), Double::sum);
                    sides.computeIfAbsent(from.key(), x -> new double[2])[1] += r.height();
                    sides.computeIfAbsent(to.key(), x -> new double[2])[0] += r.height();
                }
                for (String pair : want.keySet()) {
                    double a = want.get(pair), b = drawn.getOrDefault(pair, 0.0);
                    if (Math.abs(a - b) > 1e-6 * Math.max(1, a)) unscaled.add(String.format("%s %s %.3f px, wants %.3f", who, pair, b, a));
                }
                for (String pair : drawn.keySet()) if (!want.containsKey(pair)) unscaled.add(who + " " + pair + " drawn, never traced");

                // Each node's ribbons are the view's figures times the scale.
                Map<String, Double> node = new HashMap<>();
                for (IndustryView.Product q : v.products()) {
                    Good x = q.good();
                    node.merge("in " + IndustryPicture.bandKey(x), IndustryPicture.drawn(v, x, q.made() + q.carriedIn() - q.captive()
                            - q.carriedOut()), Double::sum);
                    node.merge("out " + IndustryPicture.storedKey(x), IndustryPicture.drawn(v, x, Math.max(0, -q.intoStock())), Double::sum);
                    for (IndustryView.Part b : q.buyers()) {
                        node.merge("in " + IndustryPicture.takerKey(b.party()), IndustryPicture.drawn(v, x, b.units()), Double::sum);
                    }
                    node.merge("in " + IndustryPicture.takerKey(IndustryPicture.ABROAD), IndustryPicture.drawn(v, x, q.exported()), Double::sum);
                    node.merge("in " + IndustryPicture.takerKey(IndustryPicture.WAREHOUSE), IndustryPicture.drawn(v, x, Math.max(0, q.intoStock())),
                            Double::sum);
                    node.merge("in " + IndustryPicture.takerKey(IndustryPicture.LOST), IndustryPicture.drawn(v, x, q.lost()), Double::sum);
                }
                for (IndustryView.Feed f : v.feeds()) {
                    Good x = f.good();
                    boolean used = false;
                    for (IndustryView.Unit u : v.units()) used |= u.standing() > 0 && u.template().uses(x) > 0;
                    if (!used) continue;
                    node.merge("out " + IndustryPicture.sourceKey(x, IndustryView.Origin.HOME), IndustryPicture.drawn(v, x, f.home()), Double::sum);
                    node.merge("out " + IndustryPicture.sourceKey(x, IndustryView.Origin.WORLD), IndustryPicture.drawn(v, x, f.imported()),
                            Double::sum);
                    node.merge("out " + IndustryPicture.sourceKey(x, IndustryView.Origin.OLD_METAL), IndustryPicture.drawn(v, x, f.oldMetal()),
                            Double::sum);
                    if (x == Good.DIESEL) node.merge("in " + IndustryPicture.LORRIES, IndustryPicture.drawn(v, x, Math.min(f.lorries(),
                            f.home() + f.imported())), Double::sum);
                }
                boolean yards = false;
                for (IndustryView.Unit u : v.units()) yards |= u.standing() > 0 && u.template().oldMetal(Good.STEEL) > 0;
                if (v.sector() instanceof HeavyIndustry && v.oldMetal() != null && yards) {
                    node.merge("out " + IndustryPicture.sourceKey(Good.STEEL, IndustryView.Origin.OLD_METAL), v.oldMetal().bought(), Double::sum);
                }
                for (Map.Entry<String, Double> e : node.entrySet()) {
                    String key = e.getKey().substring(e.getKey().indexOf(' ') + 1);
                    int side = e.getKey().startsWith("in ") ? 0 : 1;
                    double a = e.getValue() * k, b = sides.getOrDefault(key, new double[2])[side];
                    nodes++;
                    if (Math.abs(a - b) > 1e-6 * Math.max(1, a)) {
                        unscaled.add(String.format("%s %s %s %.3f px, the view's %.3f", who, key, side == 0 ? "in" : "out", b, a));
                    }
                }
                for (RefineryView.Place q : p.places()) {
                    double[] s = sides.getOrDefault(q.key(), new double[2]);
                    if (Math.max(s[0], s[1]) > q.h() + 1e-6 * Math.max(1, q.h())) unscaled.add(who + " " + q.key() + " taller than its node");
                }

                // Every label's figure the view's, in IndustryView's words.
                List<String> texts = p.texts();
                for (IndustryView.Product q : v.products()) {
                    RefineryView.Place band = p.place(IndustryPicture.bandKey(q.good()));
                    if (band == null || band.h() < 4) continue;
                    String fig = IndustryView.figure(q.good(), q.made() + q.carriedIn() - q.captive() - q.carriedOut());
                    labels++;
                    if (!texts.contains(fig) && texts.stream().noneMatch(s -> s.endsWith(" " + fig))) figures.add(who + " band " + q.good() + " " + fig);
                }
                for (IndustryView.Vessel x : v.fields()) {
                    String fig = IndustryView.pct(x.filled()) + (x.resource() == Resource.FOREST ? " standing · " : " left · ")
                            + IndustryView.groundWords(x.resource(), x.remaining());
                    labels++;
                    if (!texts.contains(fig)) figures.add(who + " vessel " + fig);
                }
                for (IndustryView.Feed f : v.feeds()) {
                    for (IndustryView.Origin o : new IndustryView.Origin[] { IndustryView.Origin.HOME, IndustryView.Origin.WORLD }) {
                        if (p.place(IndustryPicture.sourceKey(f.good(), o)) == null) continue;
                        String fig = IndustryView.unitsWords(f.good(), o == IndustryView.Origin.HOME ? f.home() : f.imported());
                        labels++;
                        if (texts.stream().noneMatch(s -> s.endsWith(" · " + fig))) figures.add(who + " source " + f.good() + " " + o + " " + fig);
                    }
                }
                for (IndustryView.Unit u : v.built()) {
                    String name = u.standing() > 0 ? u.template().getName() + " ×" + u.standing() : u.template().getName();
                    labels++;
                    if (!texts.contains(name)) figures.add(who + " kind " + name);
                    if (u.standing() > 0 && u.run() > 0 && !u.workedOut() && u.oneMore() != null) {
                        String run = "runs " + IndustryView.pct(u.run());
                        labels++;
                        if (texts.stream().noneMatch(s -> s.startsWith(run))) figures.add(who + " kind " + name + " " + run);
                    }
                }
                for (RefineryView.Place q : p.places()) {
                    if (!q.key().startsWith("taker:")) continue;
                    Map<Good, Double> took = new HashMap<>();
                    String party = q.key().substring("taker:".length());
                    for (IndustryView.Product x : v.products()) {
                        double u = party.equals(IndustryPicture.ABROAD) ? x.exported() : party.equals(IndustryPicture.WAREHOUSE)
                                ? Math.max(0, x.intoStock()) : party.equals(IndustryPicture.LOST) ? x.lost() : 0;
                        for (IndustryView.Part b : x.buyers()) if (party.equals(b.party())) u += b.units();
                        if (u > 0) took.put(x.good(), u);
                    }
                    if (took.size() != 1) continue;
                    Map.Entry<Good, Double> e = took.entrySet().iterator().next();
                    String fig = IndustryView.figure(e.getKey(), e.getValue());
                    labels++;
                    if (!texts.contains(fig)) figures.add(who + " taker " + party + " " + fig);
                }
                if (v.power().made() > 0 || v.power().drawn() > 0) {
                    labels++;
                    if (!texts.contains(IndustryView.powerWords(v.power()))) figures.add(who + " power");
                }
                if (!IndustryPicture.noteWords(v).isEmpty()) {
                    labels++;
                    if (!texts.contains(IndustryPicture.noteWords(v))) figures.add(who + " note");
                }
                for (IndustryPicture.Cell c : IndustryPicture.strip(v)) {
                    for (IndustryView.Product q : v.products()) {
                        if (!c.name().equals(q.good().label())) continue;
                        labels++;
                        if (!c.rows().get(0).figure().equals(IndustryView.unitsWords(q.good(), q.made()))) figures.add(who + " cell " + c.name());
                    }
                }

                // Nothing off the frame: every ribbon, box, line, icon and outline, and every line of words with the room it is given.
                double w = p.width(), h = p.height();
                for (RefineryView.Ribbon r : p.ribbons()) {
                    if (!inside(Math.min(r.x1(), r.x2()), Math.min(r.a0(), r.b0()), Math.max(r.x1(), r.x2()), Math.max(r.a1(), r.b1()), w, h)) {
                        off.add(who + " a ribbon");
                    }
                }
                for (RefineryView.Box b : p.boxes()) if (!inside(b.x(), b.y(), b.x() + b.w(), b.y() + b.h(), w, h)) off.add(who + " a box " + b.tip());
                for (RefineryView.Line l : p.lines()) {
                    if (!inside(Math.min(l.x1(), l.x2()), Math.min(l.y1(), l.y2()), Math.max(l.x1(), l.x2()), Math.max(l.y1(), l.y2()), w, h)) {
                        off.add(who + " a line");
                    }
                }
                for (IndustryPicture.Mark m : p.marks()) if (!inside(m.x(), m.y(), m.x() + m.size(), m.y() + m.size(), w, h)) off.add(who + " an icon");
                for (RefineryView.Outline o : p.outlines()) {
                    String[] n = o.path().replaceAll("[MLQZ]", " ").trim().split("\\s+");
                    for (int i = 0; i + 1 < n.length; i += 2) {
                        double x = Double.parseDouble(n[i]), y = Double.parseDouble(n[i + 1]);
                        if (!inside(x, y, x, y, w, h)) {
                            off.add(who + " its roof");
                            break;
                        }
                    }
                }
                for (RefineryView.Words x : p.words()) {
                    double left = x.align() == RefineryView.Align.START ? x.x() : x.align() == RefineryView.Align.END ? x.x() - x.slot()
                            : x.x() - x.slot() / 2;
                    if (!inside(left, x.y() - x.size(), left + x.slot(), x.y() + 3, w, h) || x.slot() <= 0) off.add(who + " \"" + x.line() + "\"");
                }
                if (!p.outlines().isEmpty()) {
                    roofs++;
                    roofed.add(v.roof());
                }
            }
        }
        report("every ribbon leaves one node's right edge and reaches another's left, inside each", loose.isEmpty() && ribbons > 0,
                loose.isEmpty() ? String.format("%,d ribbons in %d pictures", ribbons, pictures) : loose.toString());
        report("ribbons to scale: each pair's ribbons are its flows' quantity times the picture's one scale, and each node's"
                + " the view's figures - a band what left the works, a taker's bar what it took, a source's what was bought",
                unscaled.isEmpty() && nodes > 0, unscaled.isEmpty() ? nodes + " node sides" : unscaled.toString());
        report("every label's figure is the view's, in its words: bands, vessels, sources, kinds and runs, takers, power, notes, cells",
                figures.isEmpty() && labels > 0, figures.isEmpty() ? labels + " labels" : figures.toString());
        report("nothing off the frame at the 1,389 x 868 window's " + (int) FRAME_WIDTH + " x " + (int) IndustryPicture.HEIGHT
                + ": ribbons, boxes, lines, icons, roofs, and every line of words with its room", off.isEmpty() && pictures > 0,
                off.isEmpty() ? pictures + " pictures" : off.toString());
        Set<String> shapes = new HashSet<>();
        for (IndustryView.Roof r : IndustryView.Roof.values()) shapes.add(IndustryPicture.roof(r, 100, 100, IndustryPicture.TANK_W).toString());
        report("each picture under its industry's roof, the five roofs each its own outline (CAMP Forestry's)", roofs == pictures
                && shapes.size() == IndustryView.Roof.values().length && roofed.contains(IndustryView.Roof.CAMP), roofed.toString());
        Game g = towns.get(0).game();
        IndustryView.View none = IndustryView.of(g, g.getSectors().byKey(Sectors.FORESTRY));
        report("fixture: an industry with nothing standing (the works town's Forestry) draws nothing, and says so",
                none.standing() == 0 && IndustryPicture.picture(g, none, FRAME_WIDTH, IndustryPicture.HEIGHT).empty()
                        && IndustryPicture.emptyWords(none).endsWith("has nothing standing."), IndustryPicture.emptyWords(none));
    }

    static boolean inside(double x0, double y0, double x1, double y1, double w, double h) {
        return x0 >= -PX && y0 >= -PX && x1 <= w + PX && y1 <= h + PX;
    }
}
