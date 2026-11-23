package ham.citybuildersim;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static ham.citybuildersim.HistoricalRecipeCheck.allExcept;
import static ham.citybuildersim.HistoricalRecipeCheck.at;
import static ham.citybuildersim.HistoricalRecipeCheck.priceForBothReaders;
import static ham.citybuildersim.HistoricalRecipeCheck.run;
import static ham.citybuildersim.HistoricalRecipeCheck.silent;

/**
 * A new building's equipment is chosen and priced at its order, kept on its site, saved, and delivered at its finish (R1e):
 * Fitout, BuildingsStacks' lots, Game.fitWhatFinished().
 *
 * WHY (R1e, 2026-11-19; docs/queue/R1e.md, R1b-R's call 3 left open by R1d). A new building paid its template's whole cost at
 * the order and what it came with was decided at its finish, so a building that came without a piece had paid for it, and
 * no discount could be given while the finish might deliver another set than the one priced. Now the order's quote chooses
 * the set (the pieces EquipmentFit.fittedForNew() keeps then), prices the shell and those pieces, the site keeps the set, the
 * save carries it, and the finish delivers it; a discovery or a price change after the order is a new fitting decision.
 *
 * No expected value here comes from Fitout. The split is the harness's own reading of the catalogue (R1c call 10: a piece
 * with work buys its goods at the table's import price as a share of the template's founding cash, and its work is its share
 * of the points; a piece with no work is carried in and in no price), the price is BuildingManager.nonMaterialCost()'s
 * arithmetic written out here on that shell, the set is EquipmentFit.fittedForNew() asked at the order, and what a building
 * came with is the stack's unfitted counts after the month's construction statements ran (SimulationEngine's order: the
 * sites, the completions, the finish, the material, the work recognised, the contracts settled, the hand's events).
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. The split, sourced: every piece with work in the catalogue is its goods at the import price over its template's
 *      founding cash and its work, the 25% pieces a quarter (the Vehicle Works's carbide tools, the Grain Farm's combine), a
 *      carried-in piece no part of the price, no shell below nothing; the whole template is its own cash and points to the
 *      bit, and the split does not move with the machinery market, which a fitting quote does.
 *   2. A city founded the default way: every template's fitout is the whole template and chosen by no one; an order there
 *      is priced as before (nonMaterialCost(t) on the template's points), leaves no lot on site and saves no key.
 *   3. Two orders for the same template, with and without a piece: the quote, the investors' estimate and the money test
 *      price each order's own set (the planners and the invoice agree), the site keeps each order's set front first, and
 *      it owes each building the points its set takes.
 *   4. A discovery and a price change while both are on site: they move a new order's set, not the orders on site; the
 *      site finishes first placed first, and each order's buildings come with the set it froze; the loose (no lot) are
 *      decided at the finish as before.
 *   5. A save and load mid-build: the lots come back exactly, the twins finish alike; a save without the key (every save
 *      from before R1e) reads its sites as the whole template, decided at the finish, as main runs it: no format bump.
 *   6. The builders' actual receipts, investment and VAT entries: what the payer paid, the builders' order book and points,
 *      the contract, the work recognised as investment and the owner's capital purchases for its tax credit are each the
 *      order's own set's price.
 *   7. A stopped site's shell keeps its orders' sets, its restart is priced on them, and the restarted site delivers them.
 *   8. R1d's rules kept: the builders are credited what the treasury paid for an order, and a building delivered without a
 *      piece, given a fitting job and then scrapped, refunds nothing while an unfitted building is left for the job.
 */
public final class FitoutCheck {
    static int fails, checks;
    static boolean quietRun;
    static final PrintStream out = System.out;

    static final String COMBINE = "combine", KIT = "autonomy-kit";
    /** The autonomy kit's discovery: until it is found a new farm's kit is NOT_KNOWN (a near-future entry is known when found, not by a date). */
    static final String KIT_ENTRY = "FE8";

    static void check(String label, boolean ok, String detail) {
        checks++;
        if (!ok) fails++;
        if (ok && quietRun) return;
        out.println((ok ? "  ok " : "FAIL ") + label + (detail == null || detail.isBlank() ? "" : ": " + detail));
    }

    static void section(String title) { out.println("--- " + title + " ---"); }

    static boolean near(double a, double b) { return Math.abs(a - b) <= 1e-9 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }

    /* ---------------- the harness's own split and price ---------------- */

    /** A piece's part of its template's cash, read off the catalogue (R1c call 10): its goods at the import price over the founding cash, for a piece with work; none for one carried in. */
    static double share(BuildingsTemplate t, Equipment piece) {
        if (piece == null || piece.getWork() <= 0) return 0;
        double value = 0;
        for (Map.Entry<Good, Double> b : piece.getBuys().entrySet()) value += b.getValue() * b.getKey().worldImportPrice();
        return value / t.getFoundingCashCost();
    }

    /** A built-in piece's goods at the import price, in founding money; 0 for one carried in. */
    static double goodsAt(Equipment piece) {
        if (piece == null || piece.getWork() <= 0) return 0;
        double value = 0;
        for (Map.Entry<Good, Double> b : piece.getBuys().entrySet()) {
            double price = b.getKey().worldImportPrice();
            if (Double.isFinite(price) && price > 0) value += b.getValue() * price;
        }
        return value;
    }

    /**
     * One building's cash and points without these pieces, from the catalogue in money (REV-R1e): the founding cash less every
     * built-in piece's goods is the frame (its labour, its own goods, the rest), the same a point in every building of the
     * template; a building without pieces does fewer points and pays the frame for those, plus the goods of the pieces it keeps.
     * Struck as the template's cash is.
     */
    static double[] shell(BuildingsTemplate t, Set<String> off) {
        double founding = t.getFoundingCashCost(), goodsAll = 0, goodsKept = 0, work = 0;
        for (Equipment e : t.getEquipment()) {
            if (e.getWork() <= 0) continue;
            double v = goodsAt(e);
            goodsAll += v;
            if (off.contains(e.getId())) work += e.getWork();
            else goodsKept += v;
        }
        double points = t.getConstructionPoints() - work;
        double frame = Math.max(0, founding - goodsAll) * points / t.getConstructionPoints();
        return new double[] { (frame + goodsKept) * t.getCashCost() / founding, points };
    }

    /** BuildingManager.nonMaterialCost()'s arithmetic written out: the cash, its founding labour (the points at the founding wage a point, never more than the cash over the expected level) taken out at the level and put back at today's wages. */
    static double nonMaterial(Game g, double cash, double points) {
        BuildingManager bm = g.getBuildingManager();
        double index = bm.buildersWageIndex(), then = bm.buildersWagePerPoint() / index;
        if (!(then > 0)) return cash;
        double level = bm.getExpectedLevel();
        double labour = Math.min(cash / level, points * then);
        return cash - labour * level + labour * index;
    }

    /** A quote's work for its buildings alone: its sticker less the pad, the works, the span and the deep water, which no fitout moves but by the multiple. */
    static double buildingsPart(Game.BuildQuote q) {
        return q.sticker - q.prepCost - q.worksCost - q.bridgeCost - q.deepWaterCost;
    }

    /** What an order of n of t with these pieces off should be quoted for its buildings: the shell's price at the multiple on its points. */
    static double expectedPart(Game g, BuildingsTemplate t, Set<String> off, long n) {
        double[] s = shell(t, off);
        return nonMaterial(g, s[0], s[1]) * n * g.getBuildingManager().orderPriceMultiplier(0, s[1], n);
    }

    /** The pieces a new building of t would not get now, by the rule the finish asked before R1e. */
    static Set<String> rejected(Game g, BuildingsTemplate t) {
        Set<String> kept = EquipmentFit.fittedForNew(g, t);
        Set<String> lacking = new TreeSet<>();
        for (Equipment e : t.getEquipment()) if (!kept.contains(e.getId())) lacking.add(e.getId());
        return lacking;
    }

    static String lots(BuildingsStacks s) {
        StringBuilder b = new StringBuilder();
        for (BuildingsStacks.Lot lot : s.getLots()) b.append(b.length() == 0 ? "" : " ").append(lot.buildings).append(':').append(new TreeSet<>(lot.without));
        return b.length() == 0 ? "none" : b.toString();
    }

    static String lot(long n, Set<String> off) { return n + ":" + new TreeSet<>(off); }

    static Map<String, Long> unfitted(BuildingsStacks s) {
        Map<String, Long> m = new TreeMap<>();
        for (Equipment e : s.getBuilding().getEquipment()) m.put(e.getId(), s.getUnfitted(e.getId()));
        return m;
    }

    /** The month's construction statements in SimulationEngine's order on `points` of site output; the completions. */
    static List<BuildingManager.Completion> work(Game g, int points) {
        BuildingManager bm = g.getBuildingManager();
        List<List<BuildingManager.Completion>> opened = new ArrayList<>();
        silent(() -> {
            List<BuildingManager.Completion> done = bm.advanceConstruction(points);
            opened.add(done);
            g.recordCompletions(done);
            g.fitWhatFinished(done);
            g.drawSiteMaterials(bm.takeMaterialsDue());
            g.recogniseSiteWork(bm.takeRevenueDue(), bm.getPointsBuilt(), points);
            g.settleSiteContracts(bm.takeContractsDue());
            g.settleConstructionControl(bm.takeControlEvents());
        });
        return opened.get(0);
    }

    /* ---------------- the 1900 farm town ---------------- */

    /** A 1900 town where every discovery but the autonomy kit's is found, past the combine's date, with two builders' depots, their yard full (no material is bought, so no escalation). */
    static final class Farms {
        Game game;
        BuildingsTemplate farm;
        BuildingsStacks stack;
        double cheap = .01, dear;
    }

    static Farms farmTown(String label) throws Exception {
        Farms f = new Farms();
        f.game = HistoricalLicenceCheck.town(label, true);
        allExcept(f.game, KIT_ENTRY);
        at(f.game, ResearchCatalog.knownFrom("PE17") + 1);
        // Two depots (BO1-S): one left the builders 173 points a month against 174 of repairs with the owners off the
        // labour force, so nothing for the sites and every wait endless; on main it left them a few, a wait of 61.67 months.
        HistoricalLicenceCheck.stand(f.game, "Construction Depot", 2);
        f.farm = f.game.getBuildingManager().getTemplateByName("Grain Farm");
        f.stack = f.game.getBuildingManager().getStack(f.farm);
        Game g = f.game;
        silent(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH * 100);
            g.getBuildingManager().setConstructionMaterials(5_000_000);
            g.getSectors().agriculture().addCash(1e7);
        });
        cheap(f);
        // The machinery price, from a cent up by half again, at which a new farm first goes without its combine.
        for (int step = 0; step < 120 && !rejected(g, f.farm).contains(COMBINE); step++) {
            f.dear = f.cheap * Math.pow(1.5, step + 1);
            priceForBothReaders(g, Good.MACHINERY, f.dear);
        }
        cheap(f);
        return f;
    }

    static void cheap(Farms f) {
        priceForBothReaders(f.game, Good.MACHINERY, f.cheap);
        run(f.game);
    }

    static void dear(Farms f) {
        priceForBothReaders(f.game, Good.MACHINERY, f.dear);
        run(f.game);
    }

    static boolean cityOrder(Game g, BuildingsTemplate t, long n) {
        Game.BuildResult[] r = new Game.BuildResult[1];
        silent(() -> r[0] = g.buildStack(t, n, false));
        return r[0] == Game.BuildResult.SUCCESS;
    }

    static boolean farmersOrder(Game g, BuildingsTemplate t, long n) {
        boolean[] r = new boolean[1];
        silent(() -> r[0] = g.buildFor(g.getSectorInvestor(Sectors.AGRICULTURE), t, n));
        return r[0];
    }

    /** Both orders on site: A, two farms with every piece that pays at cheap machinery; B, three at dear machinery. Their sets. */
    static final class Two {
        Set<String> offA, offB;
        Game.BuildQuote quoteA, quoteB;
        boolean placed;
    }

    static Two placeBoth(Farms f) {
        return placeBoth(f, true);
    }

    /** ...B the farmers' order, or the city's (a site the city may stop is the city's alone). */
    static Two placeBoth(Farms f, boolean farmersB) {
        Two two = new Two();
        Game g = f.game;
        cheap(f);
        two.offA = rejected(g, f.farm);
        two.quoteA = g.quoteBuild(f.farm, 2);
        boolean a = cityOrder(g, f.farm, 2);
        dear(f);
        two.offB = rejected(g, f.farm);
        two.quoteB = g.quoteBuild(f.farm, 3);
        boolean b = farmersB ? farmersOrder(g, f.farm, 3) : cityOrder(g, f.farm, 3);
        two.placed = a && b;
        return two;
    }

    /* ---------------- 1 ---------------- */

    static void splitSourced() throws Exception {
        section("1. the split, sourced: a built-in piece's goods at the import price and its work; a carried-in piece in no price");
        Game g = HistoricalLicenceCheck.town("R1e-split", false);
        BuildingManager bm = g.getBuildingManager();
        int pieces = 0, builtIn = 0, carried = 0, wrong = 0, overWhole = 0, notWhole = 0;
        String first = "";
        for (BuildingsTemplate t : bm.getTemplates()) {
            if (t.getEquipment().isEmpty()) continue;
            Fitout whole = Fitout.whole(t);
            if (whole.cashCost() != t.getCashCost() || whole.points() != t.getConstructionPoints() || !whole.isWhole() || whole.isChosen())
                notWhole++;
            double sum = 0;
            Set<String> all = new TreeSet<>();
            for (Equipment e : t.getEquipment()) {
                pieces++;
                all.add(e.getId());
                if (e.getWork() > 0) builtIn++; else carried++;
                double want = share(t, e);
                sum += want;
                if (!near(Fitout.cashShare(t, e), want) || Fitout.inThePrice(e) != e.getWork() > 0) {
                    wrong++;
                    if (first.isEmpty()) first = t.getName() + "/" + e.getId() + " " + Fitout.cashShare(t, e) + " vs " + want;
                }
            }
            Fitout none = Fitout.without(t, all);
            double[] s = shell(t, all);
            if (sum > 1 || !near(none.cashCost(), s[0]) || none.points() != s[1] || none.cashCost() < 0 || none.points() < 0) {
                overWhole++;
                if (first.isEmpty()) first = t.getName() + " shell " + none.cashCost() + "/" + none.points() + " vs " + s[0] + "/" + s[1];
            }
        }
        check("fixture: the catalogue carries both kinds of piece", builtIn > 0 && carried > 0, builtIn + " built in, " + carried + " carried in of " + pieces);
        check("each piece's part of its template's cash is its goods at the import price over the founding cash, with work; none without",
                wrong == 0, wrong + " wrong" + (first.isEmpty() ? "" : ", first " + first));
        check("no template's pieces come to more than its price; the shell without every piece is that cash and those points, never below nothing",
                overWhole == 0, overWhole + " faults");
        check("the whole template is its own cash and points, to the bit, and chosen by no one", notWhole == 0, notWhole + " not");

        // A point of work costs the same in a building with or without a piece, the pieces' goods apart (REV-R1e).
        int perPoint = 0, tried = 0;
        String firstPoint = "";
        for (BuildingsTemplate t : bm.getTemplates()) {
            double k = t.getCashCost() / t.getFoundingCashCost(), goodsAll = 0;
            for (Equipment e : t.getEquipment()) goodsAll += goodsAt(e) * k;
            double wholeAPoint = (t.getCashCost() - goodsAll) / t.getConstructionPoints();
            for (Equipment e : t.getEquipment()) {
                if (e.getWork() <= 0) continue;
                tried++;
                Fitout without = Fitout.without(t, List.of(e.getId()));
                double aPoint = (without.cashCost() - (goodsAll - goodsAt(e) * k)) / without.points();
                if (!near(aPoint, wholeAPoint)) {
                    perPoint++;
                    if (firstPoint.isEmpty()) firstPoint = t.getName() + " without " + e.getId() + ": " + aPoint + " a point vs " + wholeAPoint;
                }
            }
        }
        check("a point of work costs the same with or without a piece: each built-in piece left off takes its goods and its work's cash, the shell's cash a point (its pieces' goods apart) the whole template's",
                tried > 0 && perPoint == 0, perPoint + " of " + tried + (firstPoint.isEmpty() ? "" : ", first " + firstPoint));

        BuildingsTemplate cars = bm.getTemplateByName("Vehicle Works"), farm = bm.getTemplateByName("Grain Farm"),
                depot = bm.getTemplateByName("Truck Depot");
        check("the 25% pieces read a quarter: the Vehicle Works's carbide tools and the Grain Farm's combine, at a tenth of the points' work",
                Math.abs(Fitout.cashShare(cars, cars.getEquipment("carbide-tools")) - .25) < 1e-3
                        && Math.abs(Fitout.cashShare(farm, farm.getEquipment(COMBINE)) - .25) < 1e-3
                        && cars.getEquipment("carbide-tools").getWork() == Math.round(cars.getConstructionPoints() * .1),
                Fitout.cashShare(cars, cars.getEquipment("carbide-tools")) + ", " + Fitout.cashShare(farm, farm.getEquipment(COMBINE)));
        Fitout farmNoCombine = Fitout.without(farm, List.of(COMBINE));
        check("the Grain Farm without its combine (the catalogue's 1,200 of cash for 100 points, the combine 300 of goods and 10 points): 840 for 90, the 600 the whole farm pays beside its pieces' goods for 100 now 540 for 90, 0.70 of the cash",
                Math.abs(farmNoCombine.cashCost() / farm.getCashCost() - .70) < 1e-3 && farmNoCombine.points() == 90
                        && farm.getConstructionPoints() == 100,
                farmNoCombine.cashCost() / farm.getCashCost() + " of the cash, " + farmNoCombine.points() + " points");
        Fitout noTrucks = Fitout.without(depot, List.of("motor-truck"));
        check("a carried-in piece is bought apart: a Truck Depot without its trucks costs what the depot does",
                noTrucks.cashCost() == depot.getCashCost() && noTrucks.points() == depot.getConstructionPoints() && !noTrucks.isWhole(), noTrucks.toString());
        check("an id the template does not carry is dropped", Fitout.without(farm, List.of("no-such-piece", COMBINE)).without().equals(Set.of(COMBINE)), "");

        Fitout noCombine = Fitout.without(farm, List.of(COMBINE));
        double shellCash = noCombine.cashCost(), quote0 = g.quoteFitting(farm.getEquipment(COMBINE));
        priceForBothReaders(g, Good.MACHINERY, Good.MACHINERY.worldImportPrice() * 100);
        double quote1 = g.quoteFitting(farm.getEquipment(COMBINE));
        check("the split does not move with the machinery market: the shell's cash at a hundred times the import price is what it was, while the fitting quote moves",
                noCombine.cashCost() == shellCash && Fitout.without(farm, List.of(COMBINE)).cashCost() == shellCash && apart(quote0, quote1),
                "shell " + shellCash + ", fitting " + quote0 + " -> " + quote1);
    }

    static boolean apart(double a, double b) { return Math.abs(a - b) > 1e-6 * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }

    /* ---------------- 2 ---------------- */

    static void defaultCity() throws Exception {
        section("2. a city founded the default way: the whole template, chosen by no one, priced as before, no lot, no key");
        Game g = HistoricalLicenceCheck.town("R1e-default", false);
        BuildingManager bm = g.getBuildingManager();
        int chosen = 0, withPieces = 0;
        for (BuildingsTemplate t : bm.getTemplates()) {
            Fitout f = Fitout.of(g, t);
            if (!t.getEquipment().isEmpty()) withPieces++;
            if (f.isChosen() || !f.isWhole() || f.template() != t) chosen++;
        }
        check("fixture: a city founded the default way, with templates that carry equipment", !g.foundedUnfitted() && withPieces > 0, withPieces + " templates");
        check("every template's fitout there is the whole template and chosen by no one", chosen == 0, chosen + " otherwise");
        BuildingsTemplate farm = bm.getTemplateByName("Grain Farm");
        silent(() -> g.setCashForTest(Founding.WEALTHY_CASH * 100));
        Game.BuildQuote q = g.quoteBuild(farm, 2);
        // REV-FC2: an order's multiple strikes the foreign crews' threshold and share on the base with the order on site,
        // at its own base a point (BuildingManager.basePerPointOf()), so "as before" reads the template's points and its base.
        double multiple = bm.orderPriceMultiplier(0, farm.getConstructionPoints(), 2, bm.basePerPointOf(farm));
        double old = bm.nonMaterialCost(farm) * 2 * multiple;
        check("an order's buildings are priced as before: nonMaterialCost(t) on the template's points, to the bit", buildingsPart(q) == old
                && q.fitout() != null && q.fitout().isWhole() && !q.fitout().isChosen(), buildingsPart(q) + " vs " + old);
        check("...and the money test's multiple reads the template's points", g.getBusinessInvestment().dearOf(farm, 2)
                == multiple, "");
        BuildingsStacks s = bm.getStack(farm);
        long before = s.getUnderConstruction();
        boolean placed = cityOrder(g, farm, 2);
        check("the order, placed, leaves its buildings loose on site: no lot", placed && s.getUnderConstruction() == before + 2
                && s.getLots().isEmpty() && s.getLooseOnSite() == s.getUnderConstruction()
                && s.pointsOnSite() == s.getUnderConstruction() * (double) farm.getConstructionPoints(), lots(s));
        check("...and nothing is saved for it", bm.getLotsById().isEmpty(), "");
        GameFiles.Result saved = RecipeCheck.save10(g, "R1e default");
        String json = saved != null && saved.ok ? Files.readString(saved.file, StandardCharsets.UTF_8) : "";
        check("the save carries no lotsById key", saved != null && saved.ok && !json.contains("\"lotsById\""), saved == null ? "no save" : saved.error);
    }

    /* ---------------- 3 ---------------- */

    static void twoOrders() throws Exception {
        section("3. two orders for one template, with and without a piece: each priced, kept and owed on its own set");
        Farms f = farmTown("R1e-two");
        Game g = f.game;
        BuildingManager bm = g.getBuildingManager();
        BusinessInvestment inv = g.getBusinessInvestment();
        cheap(f);
        Set<String> offCheap = rejected(g, f.farm);
        dear(f);
        Set<String> offDear = rejected(g, f.farm);
        check("fixture: a 1900 town; at cheap machinery a new farm keeps its combine, not its autonomy kit (not yet found); at dear it goes without the combine",
                g.foundedUnfitted() && !offCheap.contains(COMBINE) && offCheap.contains(KIT) && offDear.contains(COMBINE) && f.dear > 0,
                "cheap off " + offCheap + ", dear off " + offDear + " at " + f.dear);
        check("fixture: the nonMaterialCost() arithmetic written out here reads the whole template as the model does",
                near(nonMaterial(g, f.farm.getCashCost(), f.farm.getConstructionPoints()), bm.nonMaterialCost(f.farm)), "");

        // The planners and the invoice on both sets, in one state.
        cheap(f);
        Game.BuildQuote qa = g.quoteBuild(f.farm, 3);
        double costA = inv.getCostOf(f.farm, 3), dearA = inv.dearOf(f.farm, 3);
        dear(f);
        Game.BuildQuote qb = g.quoteBuild(f.farm, 3);
        double costB = inv.getCostOf(f.farm, 3), dearB = inv.dearOf(f.farm, 3);
        boolean keepsBuiltIn = true;
        for (String id : offCheap) if (f.farm.getEquipment(id).getWork() > 0) keepsBuiltIn = false;
        check("the quote with every piece that pays is the shell plus its pieces (the kit, carried in, no part of it): with every built-in piece, the template's price",
                near(buildingsPart(qa), expectedPart(g, f.farm, offCheap, 3)) && (!keepsBuiltIn || near(buildingsPart(qa), bm.nonMaterialCost(f.farm) * 3
                        * bm.orderPriceMultiplier(0, f.farm.getConstructionPoints(), 3))), buildingsPart(qa) + ", off " + offCheap);
        check("the quote without the combine is the shell plus the pieces it keeps, at the multiple on its own points",
                near(buildingsPart(qb), expectedPart(g, f.farm, offDear, 3)) && buildingsPart(qb) < buildingsPart(qa),
                buildingsPart(qb) + " vs " + expectedPart(g, f.farm, offDear, 3) + " (whole " + buildingsPart(qa) + ")");
        check("...its fitout is the set fittedForNew() keeps at the quote", qb.fitout() != null && qb.fitout().isChosen()
                && qb.fitout().without().equals(offDear) && qa.fitout().without().equals(offCheap), qb.fitout() + " / " + qa.fitout());
        check("the investors' estimate prices each set as the quote does: its fall is the quote's",
                near(costA - costB, qa.total - qb.total) && costB < costA, (costA - costB) + " vs " + (qa.total - qb.total));
        check("...and the money test's multiple reads each set's points", dearA == bm.orderPriceMultiplier(0, f.farm.getConstructionPoints(), 3)
                && dearB == bm.orderPriceMultiplier(0, shell(f.farm, offDear)[1], 3), dearA + ", " + dearB);

        // The twin (REV-R1e): a farm without the combine and the combine itself, at the split's prices, are a whole farm.
        double whole = bm.nonMaterialCost(f.farm), without = bm.nonMaterialCost(Fitout.without(f.farm, offDear));
        double k = f.farm.getCashCost() / f.farm.getFoundingCashCost(), goodsAll = 0, goodsOff = 0, workOff = 0;
        for (Equipment e : f.farm.getEquipment()) {
            goodsAll += goodsAt(e) * k;
            if (offDear.contains(e.getId()) && e.getWork() > 0) {
                goodsOff += goodsAt(e) * k;
                workOff += e.getWork();
            }
        }
        double aPoint = (whole - goodsAll) / f.farm.getConstructionPoints();
        check("premise: the farm's labour is under its cash (nonMaterialCost() does not cap it)",
                f.farm.getConstructionPoints() * bm.buildersWagePerPoint() / bm.buildersWageIndex() < f.farm.getCashCost() / bm.getExpectedLevel(), "");
        check("the twin: a farm without the combine, and the combine's goods at the import price with its work at the whole farm's own price a point (its pieces' goods apart), cost a whole farm: the combine's work is no part of the shell, so a fitting later pays it once",
                workOff > 0 && near(without + goodsOff + workOff * aPoint, whole),
                without + " + " + goodsOff + " + " + workOff * aPoint + " vs " + whole + "; a refit quotes " + g.quoteFitting(f.farm.getEquipment(COMBINE)));

        Two two = placeBoth(f);
        BuildingsStacks s = f.stack;
        double pointsA = shell(f.farm, two.offA)[1], pointsB = shell(f.farm, two.offB)[1];
        check("both orders placed: the city's two at cheap machinery, the farmers' three at dear", two.placed && s.getUnderConstruction() == 5, "on site " + s.getUnderConstruction());
        check("the site keeps each order's set, front first", lots(s).equals(lot(2, two.offA) + " " + lot(3, two.offB)), lots(s));
        check("...and owes each building the points its set takes", near(s.pointsOnSite(), 2 * pointsA + 3 * pointsB)
                && near(s.pointsOfFirst(2), 2 * pointsA) && pointsB < pointsA, s.pointsOnSite() + " vs " + (2 * pointsA + 3 * pointsB));
        Game.BuildQuote q3 = g.quoteBuild(f.farm, 3);
        double extra = q3.prepPoints + q3.worksPoints + q3.bridgePoints + q3.deepWaterPoints;
        double owedLess = g.quoteCityMonths(f.farm, 3, extra - 3 * (f.farm.getConstructionPoints() - pointsB));
        check("the quote's wait reads the work its set owes: three without the combine, on the same site, wait what that work waits, no longer than three with it",
                Double.isFinite(q3.months) && near(q3.months, owedLess) && owedLess <= g.quoteCityMonths(f.farm, 3, extra), q3.months + " vs " + owedLess);
        double lead = inv.leadTime(f.farm, 3, 1000), leadWant = bm.waitFor(f.farm, 3, -3 * (f.farm.getConstructionPoints() - pointsB), 1000);
        check("...and the investors' lead time reads it too", near(lead, leadWant) && lead <= bm.waitFor(f.farm, 3, 1000), lead + " vs " + leadWant);
    }

    /* ---------------- 4 ---------------- */

    static void discoveryOnSite() throws Exception {
        section("4. a discovery and a price change while both are on site: a new order's set moves, the orders on site deliver theirs");
        Farms f = farmTown("R1e-discovery");
        Game g = f.game;
        Two two = placeBoth(f);
        BuildingsStacks s = f.stack;
        String frozen = lots(s);
        Map<String, Long> before = unfitted(s);
        // The kit found, and machinery cheap again.
        allExcept(g, "");
        cheap(f);
        Set<String> now = rejected(g, f.farm);
        check("fixture: both on site; now a new farm would come with every piece (the kit found, machinery cheap)",
                two.placed && s.getUnderConstruction() == 5 && now.isEmpty(), "rejected now " + now);
        Game.BuildQuote qc = g.quoteBuild(f.farm, 1);
        check("a new order is quoted on the new set, the whole template", qc.fitout() != null && qc.fitout().isWhole()
                && near(buildingsPart(qc), expectedPart(g, f.farm, Set.of(), 1)), qc.fitout() + "");
        check("...and the orders on site keep theirs", lots(s).equals(frozen), lots(s));
        boolean c = cityOrder(g, f.farm, 1);
        check("the new order joins the site behind them with its own set", c && lots(s).equals(frozen + " " + lot(1, Set.of())), lots(s));

        // Small months, until A's two have opened: what opened came with A's set, and any of B's that opened with them with B's.
        long firstDone = 0, loose = 0;
        Map<String, Long> firstOff = new TreeMap<>();
        for (int m = 0; m < 400 && firstDone < 2; m++) {
            for (BuildingManager.Completion d : work(g, 25)) {
                if (!d.building.equals(f.farm.getName())) continue;
                firstDone += d.quantity;
                loose += d.loose;
                for (Map.Entry<String, Long> e : d.without.entrySet()) firstOff.merge(e.getKey(), e.getValue(), Long::sum);
            }
        }
        Map<String, Long> wantFirst = new TreeMap<>();
        for (String id : two.offA) wantFirst.merge(id, 2L, Long::sum);
        for (String id : two.offB) if (firstDone > 2) wantFirst.merge(id, firstDone - 2, Long::sum);
        String leftWant = (5 - firstDone > 0 ? lot(5 - firstDone, two.offB) + " " : "") + lot(1, Set.of());
        check("the site finishes first placed first: A's two open first, with A's set, B's behind them", firstDone >= 2 && loose == 0
                && firstOff.equals(wantFirst) && lots(s).equals(leftWant), firstDone + " opened, " + firstOff + ", left " + lots(s));
        for (int m = 0; m < 40 && s.getUnderConstruction() > 0; m++) work(g, 2000);
        Map<String, Long> after = unfitted(s), want = new TreeMap<>(before);
        for (String id : two.offA) want.merge(id, 2L, Long::sum);
        for (String id : two.offB) want.merge(id, 3L, Long::sum);
        check("every order's buildings came with the set it froze: the unfitted counts rose by A's two and B's three, C's none",
                s.getUnderConstruction() == 0 && after.equals(want), "after " + after + ", want " + want + " (before " + before + ")");
        check("...which the rule asked at the finish would not have given: every piece pays now", rejected(g, f.farm).isEmpty()
                && (after.get(COMBINE) > before.get(COMBINE)), "");

        // The loose: buildings no order fitted out (a site a harness put up, a save from before R1e) are decided at the finish.
        dear(f);
        Set<String> offLoose = rejected(g, f.farm);
        Map<String, Long> b2 = unfitted(s);
        silent(() -> s.startConstruction(2));
        check("fixture: two put on site with no order's set are loose", s.getLots().isEmpty() && s.getLooseOnSite() == 2, lots(s));
        for (int m = 0; m < 40 && s.getUnderConstruction() > 0; m++) work(g, 2000);
        Map<String, Long> a2 = unfitted(s), w2 = new TreeMap<>(b2);
        for (String id : offLoose) w2.merge(id, 2L, Long::sum);
        check("...and come with what the rule keeps at their finish, as before R1e", a2.equals(w2), a2 + " vs " + w2);
    }

    /* ---------------- 5 ---------------- */

    static void saveMidBuild() throws Exception {
        section("5. a save and load mid-build: the sets come back and the twins finish alike; a save without the key reads as main");
        Farms f = farmTown("R1e-save");
        Game g = f.game;
        Two two = placeBoth(f);
        BuildingsStacks s = f.stack;
        // Small months until one of A's has opened and the next is begun.
        for (int m = 0; m < 400 && (s.getUnderConstruction() > 4 || s.getConstructionProgress() <= 0); m++) work(g, 25);
        String onSite = lots(s);
        double progress = s.getConstructionProgress(), points = s.pointsOnSite();
        check("fixture: mid-build, one of A's open, the next part worked, B's three behind", two.placed && s.getUnderConstruction() == 4 && progress > 0
                && onSite.equals(lot(1, two.offA) + " " + lot(3, two.offB)), onSite + ", progress " + progress);
        GameFiles.Result saved = RecipeCheck.save10(g, "R1e mid-build");
        String json = saved != null && saved.ok ? Files.readString(saved.file, StandardCharsets.UTF_8) : "";
        check("the save carries the sites' sets", saved != null && saved.ok && json.contains("\"lotsById\""), saved == null ? "no save" : saved.error);
        Game b = RecipeCheck.load10(g);
        BuildingsStacks bs = b.getBuildingManager().getStack(b.getBuildingManager().getTemplateByName("Grain Farm"));
        check("loaded, the site holds the same sets, the same progress and owes the same points", bs != null && lots(bs).equals(onSite)
                && bs.getConstructionProgress() == progress && bs.pointsOnSite() == points, bs == null ? "no stack" : lots(bs));

        for (Game twin : List.of(g, b)) {
            allExcept(twin, "");
            priceForBothReaders(twin, Good.MACHINERY, f.cheap);
            run(twin);
        }
        Map<String, Long> wantFrom = unfitted(s);
        for (String id : two.offA) wantFrom.merge(id, 1L, Long::sum);
        for (String id : two.offB) wantFrom.merge(id, 3L, Long::sum);
        for (int m = 0; m < 40 && (s.getUnderConstruction() > 0 || bs.getUnderConstruction() > 0); m++) {
            work(g, 2000);
            work(b, 2000);
        }
        check("the twins, finishing after a discovery and a price change, deliver the frozen sets alike",
                s.getUnderConstruction() == 0 && bs.getUnderConstruction() == 0 && unfitted(s).equals(unfitted(bs)) && unfitted(s).equals(wantFrom),
                unfitted(s) + " / " + unfitted(bs) + " want " + wantFrom);

        // A save from before R1e: the same file with no key.
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        root.remove("lotsById");
        Files.writeString(saved.file, root.toString(), StandardCharsets.UTF_8);
        Game old = RecipeCheck.load10(g);
        BuildingsStacks os = old.getBuildingManager().getStack(old.getBuildingManager().getTemplateByName("Grain Farm"));
        check("a save without the key loads its site whole: every building loose, owing the template's points, as it was paid",
                os != null && os.getLots().isEmpty() && os.getUnderConstruction() == 4 && os.getLooseOnSite() == 4
                        && os.pointsOnSite() == 4 * (double) f.farm.getConstructionPoints(), os == null ? "no stack" : lots(os));
        allExcept(old, "");
        priceForBothReaders(old, Good.MACHINERY, f.dear);
        run(old);
        Set<String> offThen = rejected(old, os.getBuilding());
        Map<String, Long> wantOld = unfitted(os);
        for (String id : offThen) wantOld.merge(id, 4L, Long::sum);
        for (int m = 0; m < 40 && os.getUnderConstruction() > 0; m++) work(old, 2000);
        check("...and its buildings come with what the rule keeps at the finish, as main delivers them", os.getUnderConstruction() == 0
                && unfitted(os).equals(wantOld), unfitted(os) + " vs " + wantOld);
    }

    /* ---------------- 6 ---------------- */

    static void receipts() throws Exception {
        section("6. the builders' receipts, investment and VAT entries are each order's own set's price");
        Farms f = farmTown("R1e-receipts");
        Game g = f.game;
        BuildingManager bm = g.getBuildingManager();
        ham.citybuildersim.sectors.Construction builders = g.getSectors().construction();
        Sector farmers = g.getSectors().agriculture();
        dear(f);
        Set<String> off = rejected(g, f.farm);
        Game.BuildQuote q = g.quoteBuild(f.farm, 3);
        double[] sh = shell(f.farm, off);
        double rate = g.getEconomyManager().buildersSalesRate();
        // The yard covers the material, so the quote is its work and the tax: what that work should cost, taxed.
        double expected = (expectedPart(g, f.farm, off, 3) + q.sticker - buildingsPart(q)) / (1 - rate);
        double unearned0 = builders.getUnearnedRevenue(), billed0 = builders.getBilledPoints(), cash0 = farmers.getCash();
        double capital0 = farmers.pending().capitalBySupplier.getOrDefault(builders.key(), 0.0);
        boolean placed = farmersOrder(g, f.farm, 3);
        BuildingsStacks.Contract contract = null;
        for (BuildingsStacks.Contract c : f.stack.getContracts()) if (c.payer.equals(Sectors.AGRICULTURE)) contract = c;
        check("fixture: the farmers order three without the combine; the yard covers the material, so nothing is bought", placed && off.contains(COMBINE)
                && contract != null && q.plantCost == 0 && q.importCost == 0 && q.bitumen == 0 && expectedPart(g, f.farm, Set.of(), 3) > buildingsPart(q), off + "");
        check("the builders' order book took the set's price, taxed", near(builders.getUnearnedRevenue() - unearned0, expected), (builders.getUnearnedRevenue() - unearned0) + " vs " + expected);
        check("...for the points the set takes, three times the shell's", near(builders.getBilledPoints() - billed0, 3 * sh[1] + q.prepPoints + q.worksPoints + q.deepWaterPoints),
                (builders.getBilledPoints() - billed0) + " vs " + 3 * sh[1]);
        check("the farmers paid it and their ground", cash0 - farmers.getCash() >= expected * (1 - 1e-9), (cash0 - farmers.getCash()) + " vs " + expected);
        check("their contract on the site is the set's price", near(contract.getValue(), expected), contract.getValue() + "");
        check("the sales tax in the quote, the builders' VAT, is their rate on the set's price", near(q.salesTax, expected * rate), q.salesTax + " vs " + expected * rate);
        double recovered = contract.getRecovered();
        double recognised = 0;
        for (int m = 0; m < 40 && f.stack.getUnderConstruction() > 0; m++) {
            work(g, 2000);
            recognised += builders.getRecognisedThisMonth();
        }
        double capital = farmers.pending().capitalBySupplier.getOrDefault(builders.key(), 0.0) - capital0;
        check("the work recognised as the builders' revenue, the accounts' investment, is the set's price, earned to the last as the farms open",
                f.stack.getUnderConstruction() == 0 && near(recognised, expected) && near(builders.getUnearnedRevenue(), unearned0), recognised + " vs " + expected);
        check("the farmers' capital purchases from the builders, the base of their VAT credit, are the set's price at their share (no escalation: no material bought)",
                recovered > 0 && near(capital, expected * recovered), capital + " vs " + expected * recovered);
    }

    /* ---------------- 7 ---------------- */

    static void shell() throws Exception {
        section("7. a stopped site's shell keeps its orders' sets; its restart is priced on them and delivers them");
        Farms f = farmTown("R1e-shell");
        Game g = f.game;
        BuildingManager bm = g.getBuildingManager();
        Two two = placeBoth(f, false);
        BuildingsStacks s = f.stack;
        String frozen = lots(s);
        String key = ConstructionControl.keyOf(f.farm);
        boolean[] cancelled = new boolean[1];
        silent(() -> cancelled[0] = g.cancelSite(key, true));
        work(g, 1);
        ConstructionControl.Shell shell = bm.getControl().shellOf(f.farm.getId());
        String shellLots = "none";
        if (shell != null && shell.lots != null) {
            StringBuilder b = new StringBuilder();
            for (BuildingsStacks.Lot lot : shell.lots) b.append(b.length() == 0 ? "" : " ").append(lot.buildings).append(':').append(new TreeSet<>(lot.without));
            shellLots = b.toString();
        }
        check("fixture: the city stops the site with both orders on it", two.placed && cancelled[0] && shell != null && s.getUnderConstruction() == 0, "");
        check("the shell keeps both orders' sets", shellLots.equals(frozen) && shell.buildings == 5, shellLots + " vs " + frozen);
        double pointsA = shell(f.farm, two.offA)[1], pointsB = shell(f.farm, two.offB)[1];
        double sets = 2 * nonMaterial(g, shell(f.farm, two.offA)[0], pointsA) + 3 * nonMaterial(g, shell(f.farm, two.offB)[0], pointsB);
        double left = Math.max(0, 2 * pointsA + 3 * pointsB - shell.progress);
        Game.BuildQuote restart = g.quoteRestart(shell);
        check("its restart is priced on the sets' work left: their price a point, at the multiple", restart != null
                && near(restart.sticker, sets * left / (2 * pointsA + 3 * pointsB) * bm.orderPriceMultiplier()), (restart == null ? "none" : restart.sticker) + "");
        Map<String, Long> before = unfitted(s);
        allExcept(g, "");
        cheap(f);
        boolean[] restarted = new boolean[1];
        silent(() -> restarted[0] = g.restartShell(f.farm.getId()));
        check("restarted, the site holds the sets again", restarted[0] && lots(s).equals(frozen) && near(s.pointsOnSite(), 2 * pointsA + 3 * pointsB), lots(s));
        for (int m = 0; m < 40 && s.getUnderConstruction() > 0; m++) work(g, 2000);
        Map<String, Long> want = new TreeMap<>(before);
        for (String id : two.offA) want.merge(id, 2L, Long::sum);
        for (String id : two.offB) want.merge(id, 3L, Long::sum);
        check("...and delivers them after a discovery and a price change", s.getUnderConstruction() == 0 && unfitted(s).equals(want), unfitted(s) + " vs " + want);
    }

    /* ---------------- 8 ---------------- */

    static void r1dKept() throws Exception {
        section("8. R1d's rules kept: the builders credited what the treasury paid; a delivered building without a piece, scrapped mid-fitting, refunds nothing");
        Farms f = farmTown("R1e-r1d");
        Game g = f.game;
        ham.citybuildersim.sectors.Construction builders = g.getSectors().construction();
        dear(f);
        double cash0 = g.getCash(), unearned0 = builders.getUnearnedRevenue();
        Game.BuildQuote q = g.quoteBuild(f.farm, 3);
        boolean placed = cityOrder(g, f.farm, 3);
        double paid = cash0 - g.getCash(), booked = builders.getUnearnedRevenue() - unearned0;
        check("the city's order without the combine: the builders' book took what the treasury paid (to the rounding of the city's cash), the set's price",
                placed && q.fitout().without().contains(COMBINE) && Math.abs(booked - paid) <= 1e-12 * Math.max(1, cash0) && near(booked, q.total), paid + " paid, " + booked + " booked, quote " + q.total);
        BuildingsStacks s = f.stack;
        for (int m = 0; m < 40 && s.getUnderConstruction() > 0; m++) work(g, 2000);
        long without = s.getUnfitted(COMBINE);
        cheap(f);
        String ordered = g.fitForCity(f.farm, COMBINE);
        int id = f.farm.getId();
        check("fixture: three farms delivered without the combine, and the city pays for a fitting on one", without >= 3 && ordered == null
                && g.getFittings().onSite(id, COMBINE) == 1, ordered + ", " + without + " without");
        double cash = g.getCash(), earned = builders.getFittingThisMonth();
        silent(() -> s.removeQuantity(1));
        check("a farm scrapped while the job is on site, with an unfitted farm left for it: the job goes on and nothing is refunded",
                g.getFittings().onSite(id, COMBINE) == 1 && g.getCash() == cash && builders.getFittingThisMonth() == earned, "");
    }

    /* ---------------- 9 ---------------- */

    static void runInvoice() throws Exception {
        section("9. a run's invoice is its orders placed one by one: each queued on its set's points, as bill() is handed them");
        Farms f = farmTown("R1e-run");
        Game g = f.game;
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate depot = bm.getTemplateByName("Construction Depot");
        dear(f);
        // The builders short: farms on site, without their combines, until the queue is past the knee.
        for (int i = 0; i < 400 && bm.monthsQueued() < BuildersShort.KNEE_MONTHS + 3; i++) if (!cityOrder(g, f.farm, 5)) break;
        Game.BuildQuote first = g.quoteBuild(f.farm, 6);
        double setPoints = first.fitout() == null ? Double.NaN : first.fitout().points();
        check("fixture: the builders are short, the queue past the knee, and a new farm goes without its combine",
                bm.monthsQueued() > BuildersShort.KNEE_MONTHS && first.fitout() != null && first.fitout().without().contains(COMBINE)
                        && setPoints < f.farm.getConstructionPoints(), bm.monthsQueued() + " months queued, " + first.fitout());
        check("premise: the depot's multiple moves with the farms' points ahead of it, their set's against the template's",
                apart(bm.orderPriceMultiplier(6 * setPoints, depot.getConstructionPoints(), 1),
                        bm.orderPriceMultiplier(6.0 * f.farm.getConstructionPoints(), depot.getConstructionPoints(), 1)), "");
        Map<BuildingsTemplate, Long> run = new LinkedHashMap<>();
        run.put(f.farm, 6L);
        run.put(depot, 1L);
        double invoice = g.buildRunInvoice(run), charged = 0;
        boolean placed = true;
        for (Map.Entry<BuildingsTemplate, Long> e : run.entrySet()) {
            placed &= cityOrder(g, e.getKey(), e.getValue());
            charged += g.getTotalBuildingCost();
        }
        check("fixture: the run's two orders placed in turn", placed, "");
        check("the run's invoice is what placing its orders in turn charged: the depot priced on the farms' sets' points ahead of it",
                near(invoice, charged), invoice + " vs " + charged);
    }

    public static void main(String[] args) throws Exception {
        quietRun = Arrays.asList(args).contains("-q");
        RecipeCheck.out = out; RecipeCheck.quiet = HistoricalRecipeCheck.quiet;
        FittingCheck.out = out; FittingCheck.quiet = HistoricalRecipeCheck.quiet;
        EquipmentCheck.out = out; EquipmentCheck.quiet = HistoricalRecipeCheck.quiet;
        Forms1900Check.out = out; Forms1900Check.quiet = HistoricalRecipeCheck.quiet;
        try {
            splitSourced();
            defaultCity();
            twoOrders();
            discoveryOnSite();
            saveMidBuild();
            receipts();
            shell();
            r1dKept();
            runInvoice();
        } catch (Throwable t) {
            out.println("FAIL: " + t);
            t.printStackTrace();
            fails++;
        }
        out.println(fails == 0 ? "All FitoutCheck assertions passed (" + checks + ")." : fails + " FAILED of " + checks);
        System.exit(fails == 0 ? 0 : 1);
    }
}
