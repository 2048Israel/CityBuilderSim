package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The Hydrogen Steel Works, checked (R6b, FE4: hydrogen-reduced iron, Boden, Sweden, projected June 2028): the near-future row that makes steel from
 * pellets and the grid's power alone, no coke, no limestone, no scrap, in a city that knows it.
 *
 * WHY. The game has no DRI good, so R6b made the shaft furnace and the arc furnace one integrated works in Heavy Industry (154), its figures composed as 0.4 of
 * a Blast Furnace plus 0.8 of a Large Mini-Mill (estimates); Iron & Steel now uses pellets, so a Pelletising Plant's pellets go to the works first (captive,
 * ProcessingSector) and a city that has no works must play exactly as it did. And a row whose cost is its power is only worth building where power is cheap:
 * that has to be a property of the planner's own arithmetic, not a number typed here. So this harness proves the row, the rule that knows it, where the planner
 * orders it and where it does not, the pellets that go to it first, and that nothing moves without it.
 *
 * What this has to prove:
 *   1. THE ROW. 154 makes steel and nothing else, takes pellets at 1 / 0.65 a tonne of it, draws HYDROGEN_MWH_A_TONNE a tonne in the game's units (a unit is
 *      HydroSites.KWH_PER_UNIT kWh) to within a unit's rounding, takes no coke, coking or thermal coal, limestone, lime, pig iron, scrap or ore, is dated "FE4"
 *      (a near-future row), and Heavy Industry uses pellets as well as makes them.
 *   2. KNOWN BY THE RESEARCH. A city founded the 1900 way does not have it among Heavy Industry's candidates (what the planner reads) the month before FE4's
 *      and does at it, or at the month its own research finds FE4; a city founded today's way never does, past FE4's month, and refuses an order for it.
 *   3. WHERE IT PAYS, AND WHERE IT DOES NOT. Cities the same in everything but the price of the grid's power - the model keeps one tariff, the utility's
 *      (UtilitiesHandler), and no lever on it by city, so a fixture sets it (EconomyManager.setPricePerWatt()), the break-even read off the planner's own
 *      figures at the model's own price and at none - the city's fabricators having imported steel (so 154 would replace imports, the study's case D; with
 *      steel exported and no pellets of the city's own it earns under the hurdle on any power at all): on the model's own tariff the planner's earnings on
 *      154's whole cost clear Heavy Industry's own hurdle at its own gate and the plan orders it, ranked first of the works that pass; in the city whose power
 *      is dearer, as far past the break-even as the model's own is under it, they do not, and the two cities' earnings differ by exactly the power the planner
 *      bills at the two prices; and on the model's own power in a city that imports no steel it does not clear either. Printed: the earnings, the hurdles, the prices.
 *   4. CAPTIVE PELLETS. The plants' pellets go to the works first, min(made, needed): in the works of the real rows the market bids only the rest and no
 *      purchase or sale is booked for what the works took; in a 1900 city at FE4's month with both placed by hand a played month makes steel, the works' pellets
 *      are the plant's first (the captive figure the sector reports, the bid only the rest), and the works' power is drawn at the template's figure.
 *   5. NOTHING MOVES WITHOUT THE WORKS. With a Pelletising Plant and no 154 the sector's captive pellets are exactly 0 and its pellets go to the market whole:
 *      the same month's figures, to the bit, as a sector that only makes pellets (the arithmetic the sector had before it used them), in a bare sector and in a
 *      played city.
 *   6. A PLAYED COUNT. The 1900 city on the model's own tariff played two years - from FE4's month, and from its founding with FE4 found by its own research
 *      (a stable economy) - with its plants standing, the planner's money gate its own and then set aside: the 154s ordered and built are counted and printed,
 *      none asserted, with the planner's last word; a city founded today's way, played the same months, builds none. The tariff cannot be held cheap across
 *      played months (the game sets it from the utility each month), so the cheaper-power count is section 3's plan, not a played one.
 *
 * Every fixture causes its condition: the 1900 city's clock is set at FE4's month (ResearchCheck.atMonth()) before its works are built, so the whole catalogue
 * is known; the works and the plant are placed by hand where a section is about them; the labour is handed (MaterialsCheck.handWorkers()) as the plan fixtures
 * of the materials chains hand it, because the plan under test is the planner's own, not a labour market's.
 */
public class HydrogenSteelCheck {

    static int fails = 0;
    /** Where the harness prints: the console, kept while a fixture town's prose is sent to `quiet`. */
    static final PrintStream out = System.out;
    /** A stream that drops what is written to it: the game's prose while a fixture town is founded and played. */
    static final PrintStream quiet = new PrintStream(OutputStream.nullOutputStream());

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

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        theRow();
        knownByTheResearch();
        wherePays();
        capturedPellets();
        nothingMoves();
        aPlayedCount();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** The iron the row's pellets carry, 65% Fe: the row's own comment (1 / 0.65 t of pellets a tonne of steel); the model keeps no constant for it. */
    static final double PELLET_FE = .65;
    /** The months section 6 plays from FE4's: two years. */
    static final int PLAYED_MONTHS = 24;
    /** The Pelletising Plants section 3 and 4 stand: enough for the works' pellets twice over at nameplate. */
    static final int PLANTS = 2;

    /** FE4's month in the game's count, from the catalogue. */
    static int fe4() { return ResearchCatalog.get().entry("FE4").worldMonth(); }

    /** The clock set at a month (ResearchCheck.atMonth()), its checked exception the harness's own. */
    static void at(Game g, int month) {
        try { ResearchCheck.atMonth(g, month); } catch (Exception e) { throw new IllegalStateException(e); }
    }

    /**
     * A city founded the 1900 way with its clock at FE4's month before its works are built, so that the whole catalogue is known: the Wealthy preset's cash
     * and ground, houses, shops, enough power and water for the works (a Coal Power Plant makes 280,000 units, the works draw HYDROGEN_MWH_A_TONNE of it a tonne),
     * and roads.
     */
    static Game town1900(String label) { return town(label, true); }

    /** ...or, with `atFe4` false, left at its founding month with its own research having found FE4 and the two it needs (E4, PE1): the second way a 1900 city comes to know the row, in an economy not thrown 128 years. */
    static Game town(String label, boolean atFe4) {
        Game g = new Game(GameFiles.scratch(label));
        quietly(() -> {
            g.newGame(Founding.defaults().withUnfitted(true));
            if (atFe4) at(g, fe4());
            else g.getResearch().restore(null, null, null, Map.of("FE4", g.getMonth(), "E4", g.getMonth(), "PE1", g.getMonth()));
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 6, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 4, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 10, true);
        });
        return g;
    }

    /** The standing and the ordered of a template: what is built plus what is under construction. */
    static long held(BuildingManager b, BuildingsTemplate t) {
        BuildingsStacks s = b.getStack(t);
        return s == null ? 0 : s.getQuantity() + s.getUnderConstruction();
    }

    /* ============================ 1. the row ============================ */

    static void theRow() {
        out.println("--- 1. the row: steel from pellets at 1 / 0.65 and HYDROGEN_MWH_A_TONNE of power a tonne, no coke, dated FE4, Heavy Industry uses pellets ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        BuildingsTemplate t = b.getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        assertTrue("fixture: the Hydrogen Steel Works (154) is in the catalogue, in Heavy Industry",
                t != null && t.getSector().equals(Sectors.HEAVY_INDUSTRY) && t.getName().equals("Hydrogen Steel Works"));
        if (t == null) return;
        double steel = t.makes(Good.STEEL);
        assertTrue("it makes steel and nothing else", steel > 0 && t.goodsMade().size() == 1);
        double pellets = steel / PELLET_FE;
        report("it takes pellets at 1 / 0.65 a tonne of its steel, to within a tonne's rounding",
                Math.abs(t.uses(Good.PELLETS) - pellets) <= .5,
                String.format("%,.0f t of pellets for %,.0f t of steel (%,.2f exactly)", t.uses(Good.PELLETS), steel, pellets));
        double units = steel * HeavyIndustry.HYDROGEN_MWH_A_TONNE * 1000 / HydroSites.KWH_PER_UNIT;
        report("it draws HYDROGEN_MWH_A_TONNE a tonne of steel in the game's units (HydroSites.KWH_PER_UNIT kWh a unit), to within a unit's rounding",
                Math.abs(t.getElectricityConsumption() - units) <= .5 && t.getGen() == 0 && t.powerBought() == t.getElectricityConsumption(),
                String.format("%,.0f units; %.2f MWh a tonne is %,.2f", (double) t.getElectricityConsumption(), HeavyIndustry.HYDROGEN_MWH_A_TONNE, units));
        boolean none = true;
        for (Good g : new Good[] { Good.MET_COKE, Good.COKING_COAL, Good.THERMAL_COAL, Good.LIMESTONE, Good.LIME, Good.PIG_IRON, Good.SCRAP, Good.IRON }) {
            none &= t.uses(g) == 0;
        }
        assertTrue("it takes no coke, coking coal, thermal coal, limestone, lime, pig iron, scrap or ore: pellets are the only good it uses",
                none && t.goodsUsed().size() == 1 && t.uses(Good.PELLETS) > 0);
        assertTrue("it is dated by FE4, a near-future row (a city founded today's way never knows it)",
                t.getSinceText().equals("FE4") && ResearchCatalog.nearFuture(t.getSinceText()));
        HeavyIndustry heavy = new HeavyIndustry();
        assertTrue("Heavy Industry uses pellets as well as makes them, so its pellets are captive (a good it both makes and uses)",
                heavy.isUser(Good.PELLETS) && heavy.isMaker(Good.PELLETS) && heavy.captive(Good.PELLETS) && heavy.isMaker(Good.STEEL)
                        && !heavy.captive(Good.STEEL));
    }

    /* ============================ 2. known by the research ============================ */

    /** Whether a template is among the candidates a sector's planner weighs. */
    static boolean weighed(HeavyIndustry heavy, Game g, BuildingsTemplate t) {
        for (SpreadPlanner.Candidate c : heavy.appraise(g.getBusinessInvestment(), g)) if (c.template() == t) return true;
        return false;
    }

    static void knownByTheResearch() {
        out.println("\n--- 2. known by the research: a 1900 city from FE4's month, or when its research finds FE4, and not before; a city founded today's way never ---");
        int fe4 = fe4();
        Game old = town1900("hydrogensteelcheck-1900");
        BuildingManager ob = old.getBuildingManager();
        BuildingsTemplate t = ob.getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        HeavyIndustry heavy = old.getSectors().heavyIndustry();
        assertTrue("fixture: a city founded the 1900 way, FE4 dated " + CityCalendar.format(fe4),
                old.foundedUnfitted() && fe4 == ResearchCatalog.get().entry("FE4").worldMonth() && t != null);
        at(old, fe4 - 1);
        boolean knowsBefore = ob.isTemplateKnown(t), listedBefore = ob.getTemplatesBySector(Sectors.HEAVY_INDUSTRY).contains(t),
                weighedBefore = weighed(heavy, old, t);
        Game.BuildResult refused = old.buildStack(t, 1, false);
        at(old, fe4);
        boolean knowsAt = ob.isTemplateKnown(t), listedAt = ob.getTemplatesBySector(Sectors.HEAVY_INDUSTRY).contains(t), weighedAt = weighed(heavy, old, t);
        assertTrue("the 1900 city does not know it, nor list it among Heavy Industry's candidates, nor weigh it, the month before FE4's (and refuses an order for it)",
                !knowsBefore && !listedBefore && !weighedBefore && refused == Game.BuildResult.NOT_KNOWN && ob.getStack(t) == null);
        assertTrue("...and knows it, lists it and weighs it at FE4's month, the Blast Furnace and the Large Mini-Mill among them throughout",
                knowsAt && listedAt && weighedAt && weighed(heavy, old, ob.getTemplate(HeavyIndustry.BLAST_FURNACE))
                        && weighed(heavy, old, ob.getTemplate(HeavyIndustry.LARGE_MINI_MILL)));

        Game early = ResearchCheck.datedCity("hydrogensteelcheck-found", true);
        BuildingsTemplate te = early.getBuildingManager().getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        boolean before = early.getBuildingManager().isTemplateKnown(te);
        early.getResearch().restore(null, null, null, Map.of("FE4", early.getMonth()));
        report("a 1900 city whose research finds FE4 knows it from the month it finds it, long before FE4's own month",
                !before && early.getBuildingManager().isTemplateKnown(te) && early.getMonth() < fe4, "month " + CityCalendar.format(early.getMonth()) + ", FE4 dated " + CityCalendar.format(fe4));

        Game modern = ResearchCheck.datedCity("hydrogensteelcheck-today", false);
        BuildingManager mb = modern.getBuildingManager();
        BuildingsTemplate tm = mb.getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        at(modern, fe4 + 12);
        Game.BuildResult order = modern.buildStack(tm, 1, false);
        report("a city founded today's way, its clock a year past FE4's month, does not know it, lists it for no sector, weighs it not, and refuses the order",
                !modern.foundedUnfitted() && !mb.isTemplateKnown(tm) && !mb.getTemplatesBySector(Sectors.HEAVY_INDUSTRY).contains(tm)
                        && !weighed(modern.getSectors().heavyIndustry(), modern, tm) && order == Game.BuildResult.NOT_KNOWN && mb.getStack(tm) == null,
                "month " + CityCalendar.format(modern.getMonth()) + ", order " + order + ", unfitted " + modern.foundedUnfitted() + ", knows " + mb.isTemplateKnown(tm)
                        + ", listed " + mb.getTemplatesBySector(Sectors.HEAVY_INDUSTRY).contains(tm) + ", weighed "
                        + weighed(modern.getSectors().heavyIndustry(), modern, tm) + ", stack " + mb.getStack(tm));
    }

    /* ============================ 3. where it pays, and where it does not ============================ */

    /** What the planner reads of 154 in a city: its candidate, the hurdle on its whole cost at Heavy Industry's own gate, and the tariff the planner bills its power at. */
    record Figure(SpreadPlanner.Candidate c, double hurdle, double tariff) {
        boolean clears() { return c.refusal(SpreadPlanner.Gate.MONEY) == null; }
    }

    static Figure figure(Game g) {
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate t = g.getBuildingManager().getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        SpreadPlanner.Candidate c = heavy.appraise(t, plans);
        return new Figure(c, heavy.planner().hurdle(heavy, plans, g, t, 1, c.cost()), g.getEconomyManager().getPricePerWatt());
    }

    /**
     * A city for section 3: the 1900 town at FE4's month, its labour handed, the grid billing `tariff` (NaN: the model's own, the utility's) and, with
     * `importsSteel`, the city's fabricators having bought the works' whole nameplate of steel from the world last month, so that what 154 makes would replace imports
     * (the study's case D: "built where the city's fabricators import steel"). The tally is written as the clearing writes an import (GoodsMarket.record(), as
     * MaterialsCheck 1 does), and not played, because a played month brings the order's wait (BusinessInvestment.carryOf(), the queue's and not the row's) into
     * the hurdle and a 1900 city thrown to 2028 moves its exchange rate tenfold.
     */
    static Game paysTown(String label, double tariff, boolean importsSteel) {
        Game g = town1900(label);
        MaterialsCheck.handWorkers(g);
        BuildingsTemplate t = g.getBuildingManager().getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        g.getEconomyManager().setPricePerWatt(Double.isNaN(tariff) ? g.getServicesManager().getPricePerWatt() : tariff);
        if (importsSteel) {
            GoodsMarket steel = g.getMarkets().get(Good.STEEL);
            steel.record(Trade.WORLD, Sectors.MANUFACTURING, t.makes(Good.STEEL), steel.importPrice());
        }
        return g;
    }

    /** The candidates the planner ranks, best earnings on the whole cost first, as text. */
    static String ranked(List<SpreadPlanner.Candidate> all) {
        StringBuilder sb = new StringBuilder();
        all.stream().sorted((a, b) -> Double.compare(b.score(), a.score())).forEach(c -> sb.append(String.format("%s %.3f%%%s; ", c.template().getName(),
                100 * c.score(), c.passes() ? "" : " (" + c.failed() + ")")));
        return sb.toString();
    }

    static void wherePays() {
        out.println("\n--- 3. where it pays, and where it does not: cities that differ only in the price of the grid's power (and, once, in whether the city imports steel) ---");
        // THE MODEL'S OWN TARIFF, from which the break-even is read: 154's earnings are linear in the price of power (the planner's running cost prices its draw
        // at EconomyManager.getPricePerWatt(); the row makes no power of its own, so its ownCosts() leave the tariff alone), and the planner says how steeply:
        // asked at the model's own price and at none. The price at which they are the hurdle then follows from the planner's own figures.
        Game cheap = paysTown("hydrogensteelcheck-cheap", Double.NaN, true);
        BuildingsTemplate t = cheap.getBuildingManager().getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        GoodsMarket steel = cheap.getMarkets().get(Good.STEEL);
        Figure on = figure(cheap);
        double ownTariff = cheap.getServicesManager().getPricePerWatt(), draw = t.getElectricityConsumption();
        cheap.getEconomyManager().setPricePerWatt(0);
        Figure free = figure(cheap);
        cheap.getEconomyManager().setPricePerWatt(ownTariff);
        double slope = (free.c().earns() - on.c().earns()) / ownTariff;   // money a month a unit of tariff: what the planner bills 154's power at
        assertTrue("fixture: the planner bills 154's power by its draw: its earnings fall as the tariff rises, by no more than the draw a unit of it",
                slope > 0 && slope <= draw * (1 + 1e-9) && free.c().cost() == on.c().cost());
        out.printf("    the planner bills %,.0f of the %,.0f units 154 draws at the tariff%n", slope, draw);
        double breakEven = ownTariff + (on.c().earns() - on.hurdle()) / slope;
        report("the cheaper city: the model's own tariff, the city's fabricators having imported the works' whole output of steel, no month played - 154 is weighed,"
                        + " passes the ground, the staff and the feed, and its earnings on its whole cost clear Heavy Industry's own hurdle at its own gate",
                steel.getImported() >= t.makes(Good.STEEL) && on.tariff() == ownTariff && ownTariff > 0 && on.c().earns() > on.hurdle() && on.clears() && on.c().passes()
                        && on.c().refusal(SpreadPlanner.Gate.LAND) == null && on.c().refusal(SpreadPlanner.Gate.STAFF) == null && on.c().refusal(SpreadPlanner.Gate.FEED) == null,
                String.format("earns %,.2fk a month on %,.0fk (%.3f%%) against the hurdle %,.2fk", on.c().earns(), on.c().cost(), 100 * on.c().score(), on.hurdle()));
        // THE DEARER CITY: as far past the break-even as the model's own is under it - the price of power at which the planner's own figures meet its own hurdle
        double dearTariff = 2 * breakEven - ownTariff;
        Game dear = paysTown("hydrogensteelcheck-dear", dearTariff, true);
        Figure off = figure(dear);
        out.printf("    the price of power a unit: %.6f in the cheaper city (the model's own), %.6f at the break-even, %.6f in the dearer (%.1f times the model's own)%n",
                ownTariff, breakEven, dearTariff, dearTariff / ownTariff);
        out.printf("    154 earns on its whole cost: %.3f%% (%,.2fk a month, hurdle %,.2fk) in the cheaper city, %.3f%% (%,.2fk a month, hurdle %,.2fk) in the dearer%n",
                100 * on.c().score(), on.c().earns(), on.hurdle(), 100 * off.c().score(), off.c().earns(), off.hurdle());
        report("in the dearer city they do not: the planner refuses it at the money, and for nothing else",
                off.tariff() == dearTariff && dearTariff > breakEven && off.c().earns() < off.hurdle() && !off.clears() && off.c().failed() == SpreadPlanner.Gate.MONEY
                        && off.c().refusals().size() == 1, off.c().refusal(SpreadPlanner.Gate.MONEY));
        assertTrue("the two cities differ by the power and nothing else: the same cost, the same hurdle, and earnings apart by exactly the power the planner bills at the two prices",
                on.c().cost() == off.c().cost() && MaterialsCheck.near(on.hurdle(), off.hurdle())
                        && MaterialsCheck.near(on.c().earns() - off.c().earns(), slope * (dearTariff - ownTariff)));
        assertTrue("...and the break-even is where they meet: the cheaper city's earnings are the hurdle plus its saving on the break-even, the dearer's the hurdle less its cost over it",
                MaterialsCheck.near(on.c().earns() - on.hurdle(), slope * (breakEven - ownTariff))
                        && MaterialsCheck.near(off.hurdle() - off.c().earns(), slope * (dearTariff - breakEven)));
        // RANKED WHERE THE PLANNER WOULD ORDER IT
        HeavyIndustry heavy = cheap.getSectors().heavyIndustry();
        BusinessInvestment plans = cheap.getBusinessInvestment();
        List<SpreadPlanner.Candidate> all = heavy.appraise(plans, cheap);
        SpreadPlanner.Candidate best = SpreadPlanner.best(all);
        BusinessInvestment.Decision d = heavy.plan(plans, cheap);
        report("in the cheaper city the planner's own rule ranks 154 first of Heavy Industry's works that pass every gate, and the plan orders it",
                best != null && best.template() == t && d.build && d.template == t && d.quantity == 1, ranked(all) + "- " + d.reason);
        HeavyIndustry heavyDear = dear.getSectors().heavyIndustry();
        List<SpreadPlanner.Candidate> allDear = heavyDear.appraise(dear.getBusinessInvestment(), dear);
        SpreadPlanner.Candidate bestDear = SpreadPlanner.best(allDear);
        BusinessInvestment.Decision dd = heavyDear.plan(dear.getBusinessInvestment(), dear);
        report("...and in the dearer one 154 is not among the works that pass, and the plan does not order it",
                (bestDear == null || bestDear.template() != t) && !(dd.build && dd.template == t), ranked(allDear) + "- " + dd.reason);
        // WHAT IT REPLACES: the model's own power, the city's steel not imported - what 154 makes would be exported
        Game exports = paysTown("hydrogensteelcheck-exports", Double.NaN, false);
        Figure sold = figure(exports);
        report("what it replaces decides it too: on the model's own power in a city that imports no steel, so that what 154 makes would be exported, it does not clear",
                sold.tariff() == ownTariff && exports.getMarkets().get(Good.STEEL).getImported() == 0 && sold.c().earns() < on.c().earns()
                        && sold.c().earns() < sold.hurdle() && !sold.clears() && MaterialsCheck.near(sold.hurdle(), on.hurdle()),
                String.format("earns %,.2fk a month against the hurdle %,.2fk, replacing imports %,.2fk", sold.c().earns(), sold.hurdle(), on.c().earns()));
    }

    /* ============================ 4. captive pellets ============================ */

    static void capturedPellets() {
        out.println("\n--- 4. captive pellets: the pelletising plants' pellets to the hydrogen works first, only the rest bought or sold ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        Markets markets = new Markets();
        markets.setExchangeRate(1);
        BuildingsTemplate plant = b.getTemplate(HeavyIndustry.PELLETISING_PLANT), works = b.getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        b.addStack(plant, PLANTS, true);
        b.addStack(works, 1, true);
        HeavyIndustry w = new HeavyIndustry();
        Sector s = w;
        s.attach(b, markets);
        double rate = w.getOperatingRate();
        double made = PLANTS * plant.makes(Good.PELLETS) * rate, need = works.uses(Good.PELLETS) * rate;
        report("fixture: Iron & Steel's real rows, the Pelletising Plants (twice over) and one Hydrogen Steel Works, the pellets captive and the plant's the larger",
                w.captive(Good.PELLETS) && made > need && need > 0 && rate > 0, String.format("rate %.3f; pellets %,.0f t made for %,.0f t needed", rate, made, need));
        w.produceFlow(Good.PELLETS);
        Sector.Output o = w.outputRow(Good.PELLETS);
        assertTrue("a month's flow: the works' need is the captive figure, taken off what the plant makes; the market is bid nothing for it and sees the rest",
                o.captive == need && o.produced == made - need && w.captiveUnits(Good.PELLETS) == need && w.bid(Good.PELLETS) == 0);
        w.shipUnsoldFlow(Good.PELLETS, markets.get(Good.PELLETS));
        Sector.Ledger l = w.pending();
        assertTrue("...no purchase booked for the pellets the works took: none bought, only the rest sold",
                !l.unitsBought.containsKey(Good.PELLETS) && l.unitsSold.getOrDefault(Good.PELLETS, 0.0) <= made - need + 1e-6);
        // a plant that makes less than the works need: all of it is the works', the market bid for the rest
        BuildingManager b2 = new BuildingManager();
        b2.initializeTemplates();
        b2.addStack(b2.getTemplate(HeavyIndustry.PELLETISING_PLANT), 1, true);
        b2.addStack(b2.getTemplate(HeavyIndustry.HYDROGEN_WORKS), 3, true);
        HeavyIndustry w2 = new HeavyIndustry();
        Sector s2 = w2;
        s2.attach(b2, markets);
        double made2 = plant.makes(Good.PELLETS) * w2.getOperatingRate(), need2 = 3 * works.uses(Good.PELLETS) * w2.getOperatingRate();
        w2.produceFlow(Good.PELLETS);
        report("...and three works over one plant: all the plant's pellets are the works', none to the market, and the market is bid for the rest",
                made2 < need2 && w2.outputRow(Good.PELLETS).captive == made2 && w2.outputRow(Good.PELLETS).produced == 0
                        && w2.bid(Good.PELLETS) == need2 - made2, String.format("%,.0f t made, %,.0f t needed, %,.0f t bid for", made2, need2, w2.bid(Good.PELLETS)));

        // in a played 1900 city at FE4's month
        Game g = town1900("hydrogensteelcheck-captive");
        BuildingManager gb = g.getBuildingManager();
        BuildingsTemplate gp = gb.getTemplate(HeavyIndustry.PELLETISING_PLANT), gw = gb.getTemplate(HeavyIndustry.HYDROGEN_WORKS);
        quietly(() -> {
            gb.addStack(gp, PLANTS, true);
            gb.addStack(gw, 1, true);
        });
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        for (int m = 0; m < 2; m++) {
            MaterialsCheck.handWorkers(g);
            quietly(() -> g.simulateMonths(1));
        }
        double steel = heavy.outputRow(Good.STEEL) == null ? 0 : heavy.outputRow(Good.STEEL).produced + heavy.outputRow(Good.STEEL).captive;
        Sector.Output po = heavy.outputRow(Good.PELLETS);
        Sector.Input pi = heavy.inputRow(Good.PELLETS);
        double rateNow = heavy.getOperatingRate();
        report("fixture: a 1900 city at FE4's month with " + PLANTS + " Pelletising Plants and a Hydrogen Steel Works placed by hand, two months played",
                gb.getQuantity(HeavyIndustry.HYDROGEN_WORKS) == 1 && gb.getQuantity(HeavyIndustry.PELLETISING_PLANT) == PLANTS && po != null && pi != null,
                String.format("rate %.3f (grid %.3f, water %.3f)", rateNow, heavy.getEnergyRatio(), heavy.getWaterRatio()));
        if (po == null || pi == null) return;
        out.printf("    pellets: %,.0f t the works' need at the rate, %,.0f t of it from the plants first, %,.0f t to the market, %,.0f t bid for, %,.0f t imported%n",
                pi.needed, po.captive, po.produced, pi.bid, pi.imported);
        assertTrue("a played month makes steel: Iron & Steel's steel is made, with the works standing", steel > 0 && heavy.getCapacity(Good.STEEL) >= gw.makes(Good.STEEL));
        assertTrue("the works' pellets are the plants' first: the captive figure covers what the works need so far as the plants make it, and the sector bids only for the"
                        + " rest (what it needed less what it took)", po.captive > 0 && po.captive <= pi.needed + 1e-6
                        && Math.abs(pi.bid - Math.max(0, pi.needed - po.captive)) <= 1e-6 * Math.max(1, pi.needed) && heavy.captiveUnits(Good.PELLETS) == po.captive);
        Sector.Statement st = heavy.statement();
        Sector.Split bought = st.bought.get(Good.PELLETS);
        assertTrue("...and the pellets the works took cost it nothing at the market: what it bought is the rest, " + "none where the plants cover the works",
                po.captive < pi.needed - 1e-6 || bought == null || (bought.atHome == 0 && bought.abroad == 0));
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double bill = heavy.getElectricityCost(), tariff = g.getEconomyManager().getPricePerWatt();
        double drawn = gb.totalBySector(Sectors.HEAVY_INDUSTRY, x -> x.powerBought());
        out.printf("    the grid: %,.0f units drawn by the city, %,.0f made (utilities' fill %.3f), delivering %.3f of it; Iron & Steel draws %,.0f%n",
                u.getConsumption(), u.getProduction(), u.getAverageUtilityFill(), heavy.getEnergyRatio(), drawn);
        report("the works' power is drawn at the template's figure: Iron & Steel's draw is its buildings' net draw, the plants' and the works' own, and the works'"
                        + " own is HYDROGEN_MWH_A_TONNE a tonne of its steel (HydroSites.KWH_PER_UNIT kWh a unit)",
                drawn == PLANTS * gp.powerBought() + gw.powerBought()
                        && Math.abs(gw.powerBought() * HydroSites.KWH_PER_UNIT / 1000 / gw.makes(Good.STEEL) - HeavyIndustry.HYDROGEN_MWH_A_TONNE) < 1e-3,
                String.format("%,.0f units for the works, %,.0f for the plants", gw.powerBought(), PLANTS * gp.powerBought()));
        report("...billed at the ratio the grid delivered and the tariff: the sector's electricity is its draw times both",
                MaterialsCheck.near(bill, drawn * heavy.getEnergyRatio() * tariff) && bill > 0,
                String.format("bill %,.4fk, draw x ratio x tariff %,.4fk", bill, drawn * heavy.getEnergyRatio() * tariff));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4, audit == null ? "no audit" : String.format("residual %.3g of what moved", audit.relative()));
    }

    /* ============================ 5. nothing moves without the works ============================ */

    static void nothingMoves() {
        out.println("\n--- 5. nothing moves without the works: no 154, so no pellets are captive and the plants' pellets all go to the market as before ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        Markets markets = new Markets();
        markets.setExchangeRate(1);
        b.addStack(b.getTemplate(HeavyIndustry.PELLETISING_PLANT), 2, true);
        HeavyIndustry real = new HeavyIndustry();
        Sector s = real;
        s.attach(b, markets);
        // The sector as it was before the change: it makes pellets and uses none (so none captive); the same buildings, the same key.
        MaterialsCheck.Works before = MaterialsCheck.works(b, markets, null, new Good[] { Good.PELLETS }, new Good[0]);
        assertTrue("fixture: two Pelletising Plants and no Hydrogen Steel Works; Heavy Industry's pellets are captive in law and need none",
                b.getQuantity(HeavyIndustry.HYDROGEN_WORKS) == 0 && real.captive(Good.PELLETS) && !before.captive(Good.PELLETS)
                        && real.getInputAtCapacity(Good.PELLETS) == 0);
        real.produceFlow(Good.PELLETS);
        before.produceFlow(Good.PELLETS);
        Sector.Output a = real.outputRow(Good.PELLETS), c = before.outputRow(Good.PELLETS);
        assertTrue("the captive figure is exactly 0 and the market is bid nothing for pellets",
                real.captiveUnits(Good.PELLETS) == 0 && a.captive == 0 && real.bid(Good.PELLETS) == 0 && before.bid(Good.PELLETS) == 0);
        assertTrue("the plants' pellets all go to the market: what the sector makes of them is what a sector that only makes them makes, to the bit"
                        + " (capacity, plan, made, idled, made for the ship)", a.capacity == c.capacity && a.planned == c.planned && a.produced == c.produced
                        && a.idled == c.idled && a.exportBound == c.exportBound && a.produced > 0);
        real.shipUnsoldFlow(Good.PELLETS, markets.get(Good.PELLETS));
        before.shipUnsoldFlow(Good.PELLETS, markets.get(Good.PELLETS));
        Sector.Ledger l = real.pending(), lb = before.pending();
        assertTrue("...sold and booked the same: the units shipped, and no pellets bought", l.unitsSold.getOrDefault(Good.PELLETS, 0.0).equals(lb.unitsSold.getOrDefault(Good.PELLETS, 0.0))
                && l.unitsSold.getOrDefault(Good.PELLETS, 0.0) > 0 && !l.unitsBought.containsKey(Good.PELLETS));
        // in a played city: the plants stand, the works do not
        Game g = town1900("hydrogensteelcheck-nothing");
        BuildingManager gb = g.getBuildingManager();
        quietly(() -> gb.addStack(gb.getTemplate(HeavyIndustry.PELLETISING_PLANT), PLANTS, true));
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        for (int m = 0; m < 2; m++) {
            MaterialsCheck.handWorkers(g);
            quietly(() -> g.simulateMonths(1));
        }
        Sector.Output po = heavy.outputRow(Good.PELLETS);
        Sector.Input pi = heavy.inputRow(Good.PELLETS);
        report("a played 1900 city at FE4's month with the plants and no works: the sector's captive pellets are exactly 0 and the market is bid nothing for them",
                gb.getQuantity(HeavyIndustry.HYDROGEN_WORKS) == 0 && po != null && po.captive == 0 && heavy.captiveUnits(Good.PELLETS) == 0
                        && (pi == null || (pi.bid == 0 && pi.needed == 0 && pi.boughtLocal == 0 && pi.imported == 0)),
                po == null ? "no pellets row" : String.format("%,.0f t made for the market, %,.0f t captive", po.produced, po.captive));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("...and the month's audit closes", audit != null && audit.relative() < 1e-4, audit == null ? "no audit" : String.format("residual %.3g of what moved", audit.relative()));
    }

    /* ============================ 6. a played count ============================ */

    /** The dearest of Heavy Industry's candidates' whole cost: what the sector's till is topped to when its money gate is set aside. */
    static double dearest(Game g) {
        double most = 0;
        for (SpreadPlanner.Candidate c : g.getSectors().heavyIndustry().appraise(g.getBusinessInvestment(), g)) most = Math.max(most, c.cost());
        return most;
    }

    /** The 154s held (built and ordered) after the played months, with the money gate its own or set aside (RefineryCheck's lever: ON_ITS_BORROWING and the dearest cost in the till). */
    static long played(Game g, boolean freed) {
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        if (freed) heavy.planner().setMoneyGate(SpreadPlanner.ON_ITS_BORROWING);
        for (int m = 0; m < PLAYED_MONTHS; m++) {
            MaterialsCheck.handWorkers(g);
            if (freed) heavy.setCash(Math.max(heavy.getCash(), dearest(g)));
            quietly(() -> g.simulateMonths(1));
        }
        MaterialsCheck.handWorkers(g);
        out.println("    the planner's last word (gate " + (freed ? "set aside" : "its own") + "): " + heavy.plan(g.getBusinessInvestment(), g).reason + " - "
                + ranked(heavy.appraise(g.getBusinessInvestment(), g)));
        if (freed) heavy.planner().setMoneyGate(null);
        BuildingManager b = g.getBuildingManager();
        return held(b, b.getTemplate(HeavyIndustry.HYDROGEN_WORKS));
    }

    static void aPlayedCount() {
        out.println("\n--- 6. a played count: two years from FE4's month with the planner free - the 154s ordered, and none in a city founded today's way ---");
        for (boolean thrown : new boolean[] { true, false }) for (boolean freed : new boolean[] { false, true }) {
            Game g = town("hydrogensteelcheck-played-" + (thrown ? "fe4" : "found") + "-" + (freed ? "freed" : "gated"), thrown);
            BuildingManager b = g.getBuildingManager();
            quietly(() -> b.addStack(b.getTemplate(HeavyIndustry.PELLETISING_PLANT), PLANTS, true));
            int from = g.getMonth();
            long n = played(g, freed);
            MoneyAudit.Result audit = g.getLastMoneyAudit();
            out.printf("    the 1900 city %s, on the model's own tariff, %s to %s, the money gate %s: Hydrogen Steel Works %d ordered or built, Large Mini-Mills %d, Blast Furnaces %d%n",
                    thrown ? "with its clock at FE4's month" : "that found FE4 by its own research", CityCalendar.format(from), CityCalendar.format(g.getMonth()),
                    freed ? "set aside" : "the sector's own", n, held(b, b.getTemplate(HeavyIndustry.LARGE_MINI_MILL)), held(b, b.getTemplate(HeavyIndustry.BLAST_FURNACE)));
            report("the " + (thrown ? "FE4-month" : "researched") + " 1900 city played " + PLAYED_MONTHS + " months (gate " + (freed ? "set aside" : "its own")
                            + "): the clock moved on by them and the audit closes", g.getMonth() == from + PLAYED_MONTHS && audit != null && audit.relative() < 1e-4,
                    audit == null ? "no audit" : String.format("residual %.3g of what moved", audit.relative()));
        }
        Game today = MaterialsCheck.town("hydrogensteelcheck-today-played");
        quietly(() -> today.getBuildingManager().addStack(today.getBuildingManager().getTemplate(HeavyIndustry.PELLETISING_PLANT), PLANTS, true));
        at(today, fe4());
        long n = played(today, true);
        MoneyAudit.Result audit = today.getLastMoneyAudit();
        report("a city founded today's way, played the same months from FE4's with the gate set aside, orders and builds no 154, and its audit closes",
                n == 0 && audit != null && audit.relative() < 1e-4, audit == null ? "no audit" : String.format("residual %.3g of what moved", audit.relative()));
    }
}
