package ham.citybuildersim;

import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Mining;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The materials chains, checked: the shared machinery every chain stands on
 * (0.7.112, batch I2; runs/spec-materials.md 2.1, 2.5-2.8 and 4), and each
 * chain's section as its batch comes (I3 onwards).
 *
 * WHY. Five chains - iron redone, coal, stone, copper and wood - share one
 * planner, two sector bases, a captive good, a works' own power and the
 * consumables it buys abroad, and none of them built a works until batch I3
 * brought the first rows. So the machinery is held here on fixtures that cause
 * each condition, before any chain leans on it (the playtest, which had none
 * of it, stayed the same to the byte at I2); and from I3 each chain's rows are
 * held to the study's arithmetic, at parity, before they move a city.
 *
 * WHAT THIS HAS TO PROVE (the spec's 4, MaterialsCheck's I2 bullets):
 *
 *   1. THE CITY'S VALUE OF A WORKS' OUTPUT AND FEED, SPLIT AT THE MONTH'S
 *      TRADE (SpreadPlanner.outValue(), inValue()): made here, the net import
 *      price for as much as the city imported and the net export price past
 *      it; used here, the net export price for as much as it exported and the
 *      net import price past it; the first unit's worth cityValue()'s, to the
 *      bit; a price the world does not quote, the local price; the game's
 *      values the market's own tally.
 *
 *   2. THE WHOLE-COST GATE REFUSES A CASH-RICH SECTOR'S CASE A (decision A):
 *      a works whose case A - feed imported, output exported - earns
 *      something, but half the hurdle on its whole cost, in a sector holding
 *      that cost in its till: the rule to 0.7.111 (ON_ITS_BORROWING) passes
 *      it, the rule in force (ON_ITS_WHOLE_COST, the default) refuses it at
 *      the money; and the four cases are the study's arithmetic, the works'
 *      consumables and power in them.
 *
 *   3. CAPTIVE USE BOOKS NO SALE: a ProcessingSector's good it both makes and
 *      uses goes to its own need first, min(made, needed); the market sees
 *      the net - bid() and what is made for the market - and the ledger
 *      books sales of the rest and no purchase of it, a flow good and a
 *      stockable one; no sector in the game has a captive good.
 *
 *   4. A WORKS' OWN POWER IS BILLED NET AND THE CITY PAYS THE SURPLUS: in a
 *      town whose bakeries are handed power of their own, each building buys
 *      its draw past what it makes and sells the grid what it makes past its
 *      draw; the sector's bill is its net draw at the ratio and the tariff,
 *      its statement's powerSold the surplus at the tariff, in its revenue;
 *      the grid's consumption is the net draws, its generation the plants'
 *      and the works' surplus; the utility pays the statements' figure out of
 *      its income; the audit closes; powerSold crosses a save.
 *
 *   5. CONSUMABLES GO TO TRADE: a works' consumables, world money a month at
 *      nameplate, are bought at the operating rate and the exchange factor as
 *      a service from the world - an import named "Consumables" on the input
 *      line - and the audit sees them leave in TRADE.
 *
 *   6. A PROCESSING SECTOR'S PLAN: a candidate earns what it would make less
 *      what it would use, at outValue() and inValue(), less its running and
 *      standing costs and its consumables and power, to the bit; the last
 *      one standing earns the mirror; the investors' test reads that
 *      estimate.
 *
 *   7. IDLE, THEN SHED: a kind of its works whose last one earns below
 *      nothing counts a month idle, a working kind none; at IDLE_MONTHS that
 *      kind alone may be sold, the spare-capacity rule's measure its
 *      buildings, and the rule sells it; the months cross a save (extras
 *      idleMonths.<id>); a month working clears the count.
 *
 *   8. MINING ON THE EXTRACTION BASE: Mining is an ExtractionSector on iron,
 *      its counts the iron ones, its plan sinks a mine on a free site, its
 *      dig the iron's tally, and its plant spare only once the ore is out.
 *
 * ...AND EACH CHAIN'S SECTIONS, FROM BATCH I3 (0.7.116; iron's prices and
 * scrap split from ore - runs/spec-materials.md 4):
 *
 *   9. THE IRON ROWS ARE THE STUDY'S: the Iron Mine (id 14), the Electric Arc
 *      Furnace (11) and the Steel Mini-Mill (12), each at parity, its four
 *      cases (SpreadPlanner.caseEarns()) the study's 2.5 to its rounding and
 *      its cost a year abroad its last column; their consumables the study's
 *      running cost less wages, power, water and diesel. The study's
 *      prototypes did not survive (the workspace restarted on 2026-10-09),
 *      so the rows are rebuilt from its tables, and where caseEarns() reads
 *      them otherwise than the study's arithmetic did it says how: the
 *      mine's diesel is a feed at the case's price, not D$0.60 in its
 *      running cost; and the study's cases carry no repairs or tax.
 *
 *  10. A SAVE FROM BEFORE THE SPLIT IS REFUSED (the rule, not the spec's
 *      IronSplit): a format-36 save, whose IRON is scrap and ore as one good,
 *      is refused in P7's words naming the build that saved it, nothing of
 *      it applied and its file as it was; a save of this build's, its
 *      furnaces' scrap in it apart from the ore, loads as it was saved.
 */
public class MaterialsCheck {

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

    /** Two money figures agree to MoneyAudit's tolerance at their size. */
    static boolean near(double a, double b) {
        return Math.abs(a - b) <= MoneyAudit.tolerance(Math.max(Math.abs(a), Math.abs(b)));
    }

    /**
     * A processing sector of the fixture's own (no ProcessingSector stands in
     * the game until batch I3 moves Heavy Industry onto it): Heavy Industry's
     * key, so it reads that sector's buildings, and the goods it is told.
     */
    static final class Works extends ProcessingSector {
        Works(Good[] made, Good[] used) {
            super(Sectors.HEAVY_INDUSTRY, "Works", BuildingType.HEAVY_INDUSTRY);
            for (Good g : made) makes(g);
            for (Good g : used) uses(g);
        }

        /** Its extras as a save writes them. */
        Map<String, Double> extras() {
            Map<String, Double> m = new LinkedHashMap<>();
            saveExtras(m);
            return m;
        }

        /** ...and read back. */
        void load(Map<String, Double> m) { restoreExtras(m); }
    }

    /** A fixture's works, wired as Sectors wires a sector: to buildings and markets, and to a game when there is one. */
    static Works works(BuildingManager b, Markets m, Game g, Good[] made, Good[] used) {
        Works w = new Works(made, used);
        Sector s = w;
        s.attach(b, m);
        if (g != null) s.attachGame(g);
        return w;
    }

    /** A town to stand works in: founded, the Wealthy preset's cash and ground, and the houses, shops, power and water to run them. */
    static Game town(String label) {
        return town(GameFiles.scratch(label));
    }

    /** ...in a save folder of the caller's, for a section that saves the town and loads its twin. */
    static Game town(GameFiles files) {
        Game g = new Game(files);
        quietly(() -> {
            g.newGame();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingManager b = g.getBuildingManager();
            g.buildStack(b.getTemplateByName("House"), 400, true);
            g.buildStack(b.getTemplateByName("Small Grocery Store"), 4, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.buildStack(b.getTemplateByName("Paved Road"), 10, true);
        });
        return g;
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        values();
        wholeCost();
        captive();
        power();
        consumables();
        plan();
        idleThenShed();
        mining();
        ironRows();
        olderSave();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. outValue and inValue ============================ */

    static void values() {
        out.println("--- 1. the city's value of a works' output and feed, split at the month's imports and exports ---");
        GoodsMarket bought = new GoodsMarket(Good.STEEL), sold = new GoodsMarket(Good.MACHINERY), neither = new GoodsMarket(Good.MATERIALS);
        for (GoodsMarket m : new GoodsMarket[] { bought, sold, neither }) {
            m.setExchangeRate(1.37);
            m.setRailCharge(.3);
            m.startMonth();
        }
        double imported = 1000, exported = 400;
        bought.record(Trade.WORLD, Sectors.MANUFACTURING, imported, bought.importPrice());
        sold.record(Sectors.MANUFACTURING, Trade.WORLD, exported, sold.exportPrice());
        neither.strike(500, 0, 200);
        double in = bought.netImportPrice(), outP = bought.netExportPrice();
        report("fixture: steel the city imported 1,000 t of this month, machinery it exported 400 t of, materials it traded none of",
                bought.getImported() == imported && sold.getExported() == exported && neither.getImported() == 0
                        && neither.getExported() == 0 && in > outP,
                String.format("steel %.5f landed, %.5f netted", in, outP));
        assertTrue("made here, up to what the city imported a unit is worth the net import price, to the bit",
                SpreadPlanner.outValue(bought, imported, 600) == 600 * in && SpreadPlanner.outValue(bought, imported, imported) == imported * in);
        assertTrue("...past it the net export price: 1,500 t is 1,000 at the import price and 500 at the export price, to the bit",
                SpreadPlanner.outValue(bought, imported, 1500) == imported * in + 500 * outP);
        assertTrue("...so the first unit is worth cityValue(), to the bit, and the game's reading is the market's own tally",
                SpreadPlanner.outValue(bought, imported, 1) == SpreadPlanner.cityValue(bought)
                        && SpreadPlanner.outValue(bought, 1500) == SpreadPlanner.outValue(bought, imported, 1500));
        double mIn = sold.netImportPrice(), mOut = sold.netExportPrice();
        assertTrue("used here, up to what the city exported a unit costs the net export price - what it would no longer ship - and"
                + " past it the net import price, to the bit", SpreadPlanner.inValue(sold, exported, 300) == 300 * mOut
                && SpreadPlanner.inValue(sold, exported, 700) == exported * mOut + 300 * mIn
                && SpreadPlanner.inValue(sold, 700) == SpreadPlanner.inValue(sold, exported, 700));
        assertTrue("...and the mirrors: what the city exported, made here, is worth the net export price; what it imported, used"
                + " here, costs the net import price", SpreadPlanner.outValue(sold, 0, 50) == 50 * mOut
                && SpreadPlanner.inValue(bought, 0, 50) == 50 * in);
        assertTrue("a good the city traded none of: made here its net export price on every unit, cityValue()'s (0.7.105, R1); used"
                + " here its net import price", SpreadPlanner.outValue(neither, 0, 250) == 250 * neither.netExportPrice()
                && SpreadPlanner.outValue(neither, 0, 1) == SpreadPlanner.cityValue(neither)
                && SpreadPlanner.inValue(neither, 0, 250) == 250 * neither.netImportPrice());
        GoodsMarket kept = new GoodsMarket(Good.VANS);
        kept.setExchangeRate(1.37);
        kept.startMonth();
        kept.strike(10, 0, 4);
        kept.record(Trade.WORLD, Sectors.RETAIL, 3, kept.importPrice());
        report("a part whose price the world does not quote reads the local price: vans past the city's imports, which the world will"
                        + " not buy", !Good.VANS.exportable() && SpreadPlanner.outValue(kept, 3, 5)
                        == 3 * kept.netImportPrice() + 2 * kept.getLocalPrice() && kept.getLocalPrice() > 0,
                String.format("%.4f a van here", kept.getLocalPrice()));
        assertTrue("no units, or no market, are worth nothing", SpreadPlanner.outValue(bought, imported, 0) == 0
                && SpreadPlanner.inValue(null, 0, 10) == 0 && SpreadPlanner.outValue(null, 10) == 0);
    }

    /* ============================ 2. the whole-cost gate and the four cases ============================ */

    static void wholeCost() {
        out.println("\n--- 2. the whole-cost gate refuses a cash-rich sector's case A, which the rule to 0.7.111 passed ---");
        Game g = new Game(GameFiles.scratch("materialscheck-money"));
        quietly(() -> {
            g.newGame();
            g.simulateMonths(2);
        });
        BusinessInvestment plans = g.getBusinessInvestment();
        Sector heavy = g.getSectors().heavyIndustry();
        Markets markets = g.getMarkets();
        BuildingsTemplate foundry = g.getBuildingManager().getTemplate(HeavyIndustry.ARC_FURNACE);
        double cost = plans.getCostOf(foundry, 1);
        heavy.setCash(cost);
        SpreadPlanner p = new SpreadPlanner();
        double hurdle = p.hurdle(heavy, plans, g, cost);
        double running = plans.runningCostOf(foundry), standing = plans.standingCostOf(heavy, foundry);
        double factor = markets.getExchangeRate(), tariff = g.getEconomyManager().getPricePerWatt();
        // THE FIXTURE CAUSES IT: steel's export price set so that case A - scrap imported, steel exported - earns half the hurdle.
        double scrapIn = foundry.uses(Good.SCRAP) * markets.get(Good.SCRAP).importPrice();
        double steelOut = (hurdle / 2 + scrapIn + running + standing + SpreadPlanner.ownCosts(foundry, 1, factor, tariff))
                / foundry.makes(Good.STEEL);
        java.util.function.ToDoubleFunction<Good> imp = x -> markets.get(x).importPrice();
        java.util.function.ToDoubleFunction<Good> exp = x -> x == Good.STEEL ? steelOut : markets.get(x).exportPrice();
        double a = SpreadPlanner.caseEarns(foundry, SpreadPlanner.Case.A, imp, exp, running, standing, factor, tariff);
        report("fixture: an Electric Arc Furnace whose case A earns half the hurdle on its whole cost, Heavy Industry holding that cost in its"
                        + " till", a > 0 && a < hurdle && near(a, hurdle / 2) && heavy.getCash() >= cost,
                String.format("earns %,.2fk a month on %,.0fk; the hurdle %,.2fk", a, cost, hurdle));
        p.setMoneyGate(SpreadPlanner.ON_ITS_BORROWING);
        boolean oldPasses = p.passesMoney(heavy, plans, g, a, cost) && p.hurdle(heavy, plans, g, cost) == 0;
        SpreadPlanner.Candidate oldC = SpreadPlanner.judge(foundry, a, cost, null, p.city(heavy, plans, g), null);
        p.setMoneyGate(null);
        SpreadPlanner.Candidate newC = SpreadPlanner.judge(foundry, a, cost, null, p.city(heavy, plans, g), null);
        assertTrue("the rule to 0.7.111 (ON_ITS_BORROWING) passed it: nothing borrowed, so it asked only that it earn something",
                oldPasses && oldC.refusal(SpreadPlanner.Gate.MONEY) == null);
        report("the rule in force (ON_ITS_WHOLE_COST, the default) refuses it at the money: the whole cost is tested whoever pays",
                p.moneyGate() == SpreadPlanner.ON_ITS_WHOLE_COST && !p.passesMoney(heavy, plans, g, a, cost)
                        && p.passesMoney(heavy, plans, g, hurdle, cost) && newC.refusal(SpreadPlanner.Gate.MONEY) != null,
                newC.refusal(SpreadPlanner.Gate.MONEY));
        // The four cases, by hand, on a works with consumables and power of its own (a fixture's row).
        BuildingsTemplate fixture = new BuildingsTemplate("Fixture works", BuildingType.HEAVY_INDUSTRY)
                .makes(Good.STEEL, 1000).uses(Good.SCRAP, 1100).setConsumables(40).setGen(2500);
        double own = SpreadPlanner.ownCosts(fixture, 1, factor, tariff);
        boolean cases = true;
        for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
            double steel = c.outputAtImport() ? imp.applyAsDouble(Good.STEEL) : exp.applyAsDouble(Good.STEEL);
            double scrap = c.feedAtImport() ? imp.applyAsDouble(Good.SCRAP) : exp.applyAsDouble(Good.SCRAP);
            double byHand = 1000 * steel - 1100 * scrap - running - standing - own;
            cases &= SpreadPlanner.caseEarns(fixture, c, imp, exp, running, standing, factor, tariff) == byHand;
        }
        assertTrue("the four cases are the study's (its 2.5): A feed imported, output exported; B feed and output at the export price;"
                + " C feed at the export price, output replacing imports; D both at the import price - to the bit", cases
                && SpreadPlanner.Case.A.feedAtImport() && !SpreadPlanner.Case.A.outputAtImport()
                && !SpreadPlanner.Case.B.feedAtImport() && !SpreadPlanner.Case.B.outputAtImport()
                && !SpreadPlanner.Case.C.feedAtImport() && SpreadPlanner.Case.C.outputAtImport()
                && SpreadPlanner.Case.D.feedAtImport() && SpreadPlanner.Case.D.outputAtImport());
        assertTrue("...with its consumables at the exchange factor and its power at the tariff in them: ownCosts() is consumables x rate"
                + " x factor less gen x tariff, to the bit", own == 40 * 1.0 * factor - 2500 * tariff
                && SpreadPlanner.ownCosts(fixture, .5, factor, tariff) == 40 * .5 * factor - 2500 * tariff
                // ...and the furnace's own, since 0.7.116: its electrodes at the factor, and no power of its own.
                && SpreadPlanner.ownCosts(foundry, 1, factor, tariff) == foundry.getConsumables() * 1.0 * factor
                && foundry.getConsumables() > 0 && foundry.getGen() == 0);
    }

    /* ============================ 3. captive use ============================ */

    static void captive() {
        out.println("\n--- 3. captive use: a good a works makes and uses goes to its own need first, and no sale is booked for it ---");
        BuildingManager b = new BuildingManager();
        b.initializeBuiltInTemplates();
        Markets markets = new Markets();
        markets.setExchangeRate(1);
        BuildingsTemplate foundry = b.getTemplate(HeavyIndustry.ARC_FURNACE);
        // THE FIXTURE CAUSES IT: the foundry uses a third of the steel it makes, and makes building material and uses some of it.
        foundry.uses(Good.STEEL, 400).makes(Good.MATERIALS, 500).uses(Good.MATERIALS, 200);
        b.addStack(foundry, 2, true);
        Works w = works(b, markets, null, new Good[] { Good.STEEL, Good.MATERIALS }, new Good[] { Good.SCRAP, Good.STEEL, Good.MATERIALS });
        double rate = w.getOperatingRate();
        double steelMade = 2 * foundry.makes(Good.STEEL) * rate, steelNeed = 2 * 400 * rate;
        report("fixture: two Electric Arc Furnaces of a works sector, each using 400 t of the 1,200 t of steel it makes, and 200 of the 500"
                        + " units of material it makes", w.captive(Good.STEEL) && w.captive(Good.MATERIALS) && !w.captive(Good.SCRAP)
                        && !Good.STEEL.stockable() && Good.MATERIALS.stockable(),
                String.format("rate %.3f", rate));
        w.produceFlow(Good.STEEL);
        Sector.Output steel = w.outputRow(Good.STEEL);
        assertTrue("a flow good: its own need is made first - min(made, needed), 800 t - and the market is brought the rest, 1,600 t",
                steel.captive == Math.min(steelMade, steelNeed) && steel.produced == steelMade - steel.captive
                        && w.captiveUnits(Good.STEEL) == steel.captive);
        assertTrue("...and it asks the market for none of it, while its scrap it asks for whole",
                w.bid(Good.STEEL) == 0 && w.bid(Good.SCRAP) == 2 * foundry.uses(Good.SCRAP) * rate && w.bid(Good.SCRAP) > 0);
        w.shipUnsoldFlow(Good.STEEL, markets.get(Good.STEEL));
        Sector.Ledger l = w.pending();
        assertTrue("the ledger books the 1,600 t it shipped as a sale, and nothing for the 800 t it used: no sale and no purchase of it",
                l.unitsSold.getOrDefault(Good.STEEL, 0.0) == steel.produced && !l.unitsBought.containsKey(Good.STEEL)
                        && l.soldOf(Good.STEEL).abroad == steel.produced * markets.get(Good.STEEL).exportPrice()
                        && l.boughtOf(Good.STEEL).total() == 0 && l.purchases() == 0);
        double matMade = 2 * 500 * rate, matNeed = 2 * 200 * rate;
        assertTrue("a stockable good the same: its need, 400 units, asked of nobody",
                w.captiveUnits(Good.MATERIALS) == Math.min(matMade, matNeed) && w.bid(Good.MATERIALS) == 0);
        w.produceStock(Good.MATERIALS, markets.get(Good.MATERIALS));
        Sector.Output mat = w.outputRow(Good.MATERIALS);
        assertTrue("...and what it makes for the market - for home, for the ship and idled - is the 600 past it",
                mat.captive == matNeed && mat.planned + mat.exportBound + mat.idled == matMade - matNeed);
        // ...and a works that needs more than it makes asks the market for the difference.
        foundry.uses(Good.STEEL, 2000);
        w.produceFlow(Good.STEEL);
        assertTrue("a works needing more than it makes uses all it makes and asks the market for the rest: 2,400 t made, 1,600 t asked",
                w.outputRow(Good.STEEL).captive == steelMade && w.outputRow(Good.STEEL).produced == 0
                        && w.bid(Good.STEEL) == 2 * 2000 * rate - steelMade);
        // No sector in the game keeps a captive good: each month is what it was, to the bit.
        Game g = new Game(GameFiles.scratch("materialscheck-captive"));
        quietly(g::newGame);
        boolean none = true;
        for (Sector s : g.getSectors().all()) for (Good x : s.goodsMade()) none &= !s.captive(x) && s.captiveUnits(x) == 0;
        assertTrue("no sector in the game keeps a captive good (Sector.captive() is false but on a ProcessingSector, and Iron & Steel,"
                + " the one since 0.7.116, makes nothing it uses)", none && g.getSectors().heavyIndustry() instanceof ProcessingSector);
    }

    /* ============================ 4. the works' own power ============================ */

    /** The power town: section 4's and 5's, bakeries handed power of their own and consumables, run `months`. */
    static Game powerTown(GameFiles files, int months) {
        Game g = town(files);
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate big = b.getTemplateByName("Industrial Bakery"), small = b.getTemplateByName("Bakery");
        quietly(() -> {
            big.setGen(3.0 * big.getElectricityConsumption());
            small.setGen(.5 * small.getElectricityConsumption());
            big.setConsumables(.001 * big.getCashCost());
            g.buildStack(big, 3, true);
            g.buildStack(small, 2, true);
            g.simulateMonths(months);
        });
        return g;
    }

    static void power() throws Exception {
        out.println("\n--- 4. a works' own power: billed net, the surplus sold to the grid, and the city's utility pays it ---");
        BuildingsTemplate t = new BuildingsTemplate("Fixture", BuildingType.INDUSTRIAL).setElectricityConsumption(100);
        BuildingsTemplate over = new BuildingsTemplate("Fixture", BuildingType.INDUSTRIAL).setElectricityConsumption(100).setGen(250);
        BuildingsTemplate under = new BuildingsTemplate("Fixture", BuildingType.INDUSTRIAL).setElectricityConsumption(100).setGen(40);
        assertTrue("a building buys from the grid its draw past what it makes and sells the grid what it makes past its draw: none made,"
                + " its whole draw bought and none sold", t.powerBought() == 100 && t.powerSold() == 0
                && over.powerBought() == 0 && over.powerSold() == 150 && under.powerBought() == 60 && under.powerSold() == 0);

        GameFiles files = GameFiles.scratch("materialscheck-power");
        Game g = powerTown(files, 3);
        BuildingManager b = g.getBuildingManager();
        Sector industry = g.getSectors().byKey(Sectors.INDUSTRY);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double price = g.getEconomyManager().getPricePerWatt();
        double surplus = b.totalBySector(Sectors.INDUSTRY, x -> BuildingsTemplate.isBilledForUtilities(x) ? x.powerSold() : 0);
        double net = b.totalBySector(Sectors.INDUSTRY, x -> BuildingsTemplate.isBilledForUtilities(x) ? x.powerBought() : 0);
        double gross = b.totalBySector(Sectors.INDUSTRY, x -> BuildingsTemplate.isBilledForUtilities(x) ? x.getElectricityConsumption() : 0);
        report("fixture: three Industrial Bakeries making three times their draw and two Bakeries half theirs, three months on",
                surplus > 0 && net > 0 && net < gross, String.format("Industry draws %,.0f kW net of %,.0f, sells %,.0f kW", net, gross, surplus));
        assertTrue("the sector's surplus is its buildings' power past their draws, and its bill its net draw at the ratio and the tariff",
                industry.getPowerSurplus() == surplus
                        && industry.getElectricityCost() == net * industry.getEnergyRatio() * price);
        Sector.Statement st = industry.statement();
        assertTrue("its statement's powerSold is the surplus at the tariff, undimmed by the grid, and in its revenue past its sales",
                st.powerSold == surplus * price && st.powerSold > 0
                        && st.revenue == st.localSales + st.exports + st.otherRevenue + st.powerSold);
        assertTrue("...and in its operating income, every cost taken off the revenue it is in",
                st.operatingIncome == st.revenue - st.inputs - st.payroll - st.electricity - st.water - st.maintenance);
        double allNet = b.getTotalDouble(BuildingsTemplate::powerBought), allSold = b.getTotalDouble(BuildingsTemplate::powerSold);
        assertTrue("the grid's consumption is every building's net draw, and the works' surplus is added to what it generates, whatever"
                + " the utility's staffing", u.getConsumption() == allNet && u.getWorksPower() == allSold && allSold == surplus
                && u.getProduction() == (u.getAverageUtilityFill() == 0 ? 10000 : u.getBaseProduction() * u.getAverageUtilityFill()) + allSold);
        assertTrue("the utility pays the works what their statements booked, out of its electricity income",
                u.getPowerBought() == g.getEconomyManager().getSectorPowerSold() && u.getPowerBought() == st.powerSold
                        && u.getElectricityIncome() == u.getElectricityRevenue() - u.getElectricityPayroll() - u.getPowerBought());
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes, the pair noted - the sectors' power sold and the utility's power bought - each off its"
                        + " own book", audit.relative() < 1e-4 && audit.detail.contains("chk + sectors PowerSold")
                        && audit.detail.contains("chk - utilities PowerBought"),
                String.format("residual %.3g of what moved", audit.relative()));
        // ...and the statement's powerSold crosses a save: the town saved in its own scratch folder's slot 10, and its twin loaded.
        quietly(() -> g.saveGame(10, "powerSold"));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        assertTrue("its powerSold crosses a save (SectorState), to the bit, and the twin's utility pays the same",
                back.getSectors().byKey(Sectors.INDUSTRY).statement().powerSold == st.powerSold && st.powerSold > 0
                        && back.getServicesManager().getUtilitiesHandler().getPowerBought() == u.getPowerBought());
    }

    /* ============================ 5. consumables ============================ */

    static void consumables() {
        out.println("\n--- 5. consumables: bought from the world at the rate and the exchange factor, an import, in TRADE ---");
        BuildingManager b = new BuildingManager();
        b.initializeBuiltInTemplates();
        Markets markets = new Markets();
        markets.setExchangeRate(1.37);
        BuildingsTemplate foundry = b.getTemplate(HeavyIndustry.ARC_FURNACE);
        // THE FIXTURE CAUSES IT: the furnace's own electrodes (0.7.116) taken away, so the works has none.
        foundry.setConsumables(0);
        Works none = works(b, markets, null, new Good[] { Good.STEEL }, new Good[] { Good.SCRAP });
        b.addStack(foundry, 2, true);
        none.buyConsumables();
        assertTrue("a works with none buys none, and books nothing", none.consumablesBill() == 0
                && !none.pending().otherInputs.containsKey(Sector.CONSUMABLES) && none.pending().imports == 0);
        foundry.setConsumables(35);
        Works w = works(b, markets, null, new Good[] { Good.STEEL }, new Good[] { Good.SCRAP });
        double bill = 2 * 35 * w.getOperatingRate() * 1.37;
        report("fixture: two Electric Arc Furnaces with 35 (world thousands) of consumables a month each, the world's money at 1.37",
                w.getConsumablesAtNameplate() == 70 && w.consumablesBill() == bill, String.format("%,.2fk a month", bill));
        w.buyConsumables();
        assertTrue("bought: their nameplate at the operating rate and the exchange factor, an import named Consumables on the input line",
                w.pending().imports == bill && w.pending().otherInputs.get(Sector.CONSUMABLES) == bill);
        w.strike();
        Sector.Statement st = w.statement();
        assertTrue("...struck: in the statement's imports and inputs, and named among its inputs that are not a good",
                st.imports == bill && st.inputs == bill && st.otherInputs.get(Sector.CONSUMABLES) == bill);
        Game g = powerTown(GameFiles.scratch("materialscheck-consumables"), 3);
        Sector industry = g.getSectors().byKey(Sectors.INDUSTRY);
        Double paid = industry.statement().otherInputs.get(Sector.CONSUMABLES);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("in the town, the bakeries' consumables are in Industry's imports, and the audit sees them leave in TRADE and closes",
                paid != null && paid > 0 && industry.statement().imports >= paid && audit.tradeOut >= paid
                        && audit.detail.contains("- Industry Imports") && audit.relative() < 1e-4,
                String.format("%,.2fk of %,.2fk imports", paid == null ? 0 : paid, industry.statement().imports));
    }

    /* ============================ 6. a processing sector's plan ============================ */

    static void plan() {
        out.println("\n--- 6. a processing sector's plan: what a works would make less what it would use, at the margin, less its costs ---");
        BuildingsTemplate t = new BuildingsTemplate("Fixture works", BuildingType.HEAVY_INDUSTRY)
                .makes(Good.STEEL, 1000).uses(Good.SCRAP, 1100).setConsumables(20).setGen(900).setElectricityConsumption(600);
        // A frame of the fixture's: each unit made worth 2 + its count / 1e6, each used 1 - its count / 1e6, so the split shows.
        SpreadPlanner.Values v = new SpreadPlanner.Values() {
            @Override public double out(Good g, double u) { return u * (2 + u / 1e6); }
            @Override public double in(Good g, double u) { return u * (1 - u / 1e6); }
        };
        double rate = .8, running = 120, standing = 30, factor = 1.25, tariff = .01;
        SpreadPlanner.City city = new SpreadPlanner.City() {
            @Override public double cost(BuildingsTemplate b) { return 50_000; }
            @Override public double running(BuildingsTemplate b) { return running; }
            @Override public double standing(BuildingsTemplate b) { return standing; }
            @Override public double rate() { return rate; }
            @Override public String land(BuildingsTemplate b) { return null; }
            @Override public String staff(BuildingsTemplate b) { return null; }
            @Override public String money(double earns, double cost) { return earns > 0 ? null : "loses"; }
        };
        SpreadPlanner.Candidate c = ProcessingSector.estimate(t, v, city, factor, tariff, null);
        double byHand = v.out(Good.STEEL, 1000 * rate) - v.in(Good.SCRAP, 1100 * rate) - running - standing
                - (20 * rate * factor - 900 * tariff);
        report("a candidate earns what it makes at outValue() less what it uses at inValue(), at the rate, less its running and"
                        + " standing costs, its consumables and its power - to the bit", c.earns() == byHand && c.cost() == 50_000
                        && c.passes(), String.format("%,.4fk a month", c.earns()));
        double last = ProcessingSector.lastEarns(t, v, city, factor, tariff);
        assertTrue("...and the last one standing the mirror: what it makes at inValue(), what losing it would cost the city, and what it"
                + " uses at outValue(), what losing it would free", last == v.in(Good.STEEL, 1000 * rate) - v.out(Good.SCRAP, 1100 * rate)
                - running - standing - (20 * rate * factor - 900 * tariff));
        SpreadPlanner.Candidate fed = ProcessingSector.estimate(t, v, city, factor, tariff, "no feed");
        assertTrue("a refusal of its feed is the first gate it fails (a chain's own feed gate; none by default)",
                fed.failed() == SpreadPlanner.Gate.FEED && SpreadPlanner.best(List.of(fed, c)) == c);
        // In a game: the investors' test reads the planner's estimate (Game.consider()) - Iron & Steel's own since 0.7.116 (batch I3).
        Game g = town("materialscheck-plan");
        ProcessingSector w = g.getSectors().heavyIndustry();
        w.setPricePerWatt(g.getEconomyManager().getPricePerWatt());
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate foundry = g.getBuildingManager().getTemplate(HeavyIndustry.ARC_FURNACE);
        List<SpreadPlanner.Candidate> all = w.appraise(plans, g);
        assertTrue("in a town Iron & Steel's candidates are its two furnaces, each the planner's estimate, and the investors' test"
                        + " reads it", all.size() == 2 && w.estimatedMonthlyProfit(foundry, plans) == w.appraise(foundry, plans).earns()
                        && plans.estimatedMonthlyProfit(Sectors.HEAVY_INDUSTRY, foundry) == w.appraise(foundry, plans).earns()
                        && w.planner().moneyGate() == SpreadPlanner.ON_ITS_WHOLE_COST);
        BusinessInvestment.Decision d = w.plan(plans, g);
        SpreadPlanner.Candidate best = SpreadPlanner.best(all);
        report("...and it orders the best that passes every gate, or says why not", best != null
                        ? d.build && d.template == best.template() && d.quantity == 1 : !d.build && d.reason != null,
                d.reason);
    }

    /* ============================ 7. idle, then shed ============================ */

    static void idleThenShed() {
        out.println("\n--- 7. idle, then shed: a kind of works whose last one loses money six months running may be sold ---");
        Game g = town("materialscheck-idle");
        BuildingManager b = g.getBuildingManager();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate foundry = b.getTemplate(HeavyIndustry.ARC_FURNACE), mill = b.getTemplate(HeavyIndustry.MINI_MILL);
        quietly(() -> {
            g.buildStack(foundry, 4, true);
            g.buildStack(mill, 1, true);
        });
        Works w = works(b, g.getMarkets(), g, new Good[] { Good.STEEL }, new Good[] { Good.SCRAP });
        w.setPricePerWatt(g.getEconomyManager().getPricePerWatt());
        SpreadPlanner.Values v = SpreadPlanner.values(g.getMarkets());
        SpreadPlanner.City city = w.planner().city(w, plans, g);
        double factor = w.exchangeFactor(), tariff = w.tariff();
        // THE FIXTURE CAUSES IT: the foundry made to lose by its consumables, the mini-mill to earn by power it sells.
        double fLast = ProcessingSector.lastEarns(foundry, v, city, factor, tariff);
        foundry.setConsumables(Math.max(0, fLast + 1000) / (city.rate() * factor));
        double mLast = ProcessingSector.lastEarns(mill, v, city, factor, tariff);
        mill.setGen(Math.max(0, -mLast + 1000) / tariff + mill.getElectricityConsumption());
        report("fixture: four Electric Arc Furnaces whose last one loses money, one Steel Mini-Mill that earns",
                ProcessingSector.lastEarns(foundry, v, city, factor, tariff) < 0 && ProcessingSector.lastEarns(mill, v, city, factor, tariff) > 0,
                String.format("%,.1fk and %,.1fk a month", ProcessingSector.lastEarns(foundry, v, city, factor, tariff),
                        ProcessingSector.lastEarns(mill, v, city, factor, tariff)));
        boolean counts = true, before = true;
        for (int m = 1; m <= SpreadPlanner.IDLE_MONTHS; m++) {
            w.endOfMonth(g);
            counts &= w.idleMonths(foundry) == m && w.idleMonths(mill) == 0;
            if (m < SpreadPlanner.IDLE_MONTHS) {
                before &= w.mayRetire(foundry) && w.mayRetire(mill) && w.retirementDemandAndCapacity(g) == null;
            }
        }
        assertTrue("each month the losing kind counts one more idle month, and the earning kind none", counts);
        assertTrue("...under IDLE_MONTHS no kind is singled out: both are the distress rule's, and the spare-capacity rule has no"
                + " measure", before);
        double[] measure = w.retirementDemandAndCapacity(g);
        assertTrue("at IDLE_MONTHS the losing kind may be sold and the earning kind may not", w.mayRetire(foundry) && !w.mayRetire(mill));
        report("...the spare-capacity rule's measure is nothing used against its buildings, one a building",
                measure != null && measure[0] == 0 && measure[1] == 4 && w.unitsOf(foundry) == 1,
                measure == null ? "none" : String.format("{%,.0f, %,.0f}", measure[0], measure[1]));
        for (int i = 0; i < BusinessInvestment.RETIREMENT_LOSS_MONTHS; i++) plans.recordSectorResult(w.key(), -1);
        BusinessInvestment.Decision d = plans.planRetirement(w, measure[0], measure[1], 0);
        report("...so, losing money six months, the rule sells a quarter of the foundries and nothing else",
                d.build && d.template == foundry && d.quantity == 1, d.reason);
        Map<String, Double> extras = w.extras();
        Works twin = works(b, g.getMarkets(), g, new Good[] { Good.STEEL }, new Good[] { Good.SCRAP });
        twin.load(extras);
        assertTrue("the idle months cross a save, by the template's id (extras idleMonths.<id>), the working kind's none",
                extras.get(SpreadPlanner.IDLE_KEY + foundry.getId()) == SpreadPlanner.IDLE_MONTHS && twin.idleMonths(foundry)
                        == SpreadPlanner.IDLE_MONTHS && twin.idleMonths(mill) == 0
                        && !extras.containsKey(SpreadPlanner.IDLE_KEY + mill.getId()));
        foundry.setConsumables(0);
        mill.setGen(0);
        if (ProcessingSector.lastEarns(foundry, v, city, factor, tariff) < 0) foundry.setGen(-ProcessingSector.lastEarns(foundry, v, city,
                factor, tariff) / tariff + 2 * foundry.getElectricityConsumption());
        w.endOfMonth(g);
        assertTrue("a month the foundry earns clears its count, and no kind may be singled out again", w.idleMonths(foundry) == 0
                && w.retirementDemandAndCapacity(g) == null && w.mayRetire(foundry));
    }

    /* ============================ 8. Mining on the extraction base ============================ */

    static void mining() {
        out.println("\n--- 8. Mining on the extraction base: iron's counts, a mine on a free site, the dig, spare once worked out ---");
        Game g = town("materialscheck-mining");
        Mining mining = g.getSectors().mining();
        LandManager land = g.getLandManager();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate mine = g.getBuildingManager().getTemplate(Mining.IRON_MINE);
        quietly(() -> land.restoreSites(Resource.IRON, 3, 5_000_000));
        assertTrue("Mining is an ExtractionSector on iron's ground, its good iron ore", mining instanceof ExtractionSector
                && mining.resource() == Resource.IRON && mining.groundGood() == Good.IRON);
        int sites = mining.sitesOwned();
        report("its sites owned and worked are the iron ones (LandManager.getIronDeposits(), Game.minesCommitted())",
                sites == land.getIronDeposits() && mining.sitesWorked() == g.minesCommitted() && sites >= 3
                        && mining.sitesWorked() == 0, String.format("%d sites, %d worked", sites, mining.sitesWorked()));
        BusinessInvestment.Decision d = mining.plan(plans, g);
        report("with free sites and ore in the ground it sinks an Iron Mine, or says why not (the staff, the ground)",
                d.build ? d.template == mine && d.quantity == 1 && d.reason.contains("deposit(s) unworked")
                        : d.reason != null && !d.reason.equals("no deposit to dig") && !d.reason.equals("the ore is worked out"),
                d.reason);
        quietly(() -> g.buildStack(mine, sites, true));
        double before = land.getIronReserveTonnes();
        mining.produceFlow(Good.IRON);
        Sector.Output ore = mining.outputRow(Good.IRON);
        report("its dig is the iron's tally (LandManager.extractIron()): what it lifted is what the ground gave, and kept for the land"
                        + " office", ore.produced > 0 && land.getIronMinedThisMonth() == ore.produced
                        && near(before - land.getIronReserveTonnes(), ore.produced) && ore.captive == 0,
                String.format("%,.0f t", ore.produced));
        assertTrue("...with every site worked there is none left to dig, and while there is ore its plant is not spare",
                mining.sitesWorked() == sites && "no deposit to dig".equals(mining.plan(plans, g).reason)
                        && mining.retirementDemandAndCapacity(g) == null);
        quietly(() -> land.restoreSites(Resource.IRON, sites, 0));
        double[] spare = mining.retirementDemandAndCapacity(g);
        assertTrue("once the ore is out all its plant is spare: nothing used against its nameplate", spare != null && spare[0] == 0
                && spare[1] == mining.getCapacity(Good.IRON) && spare[1] == sites * mine.makes(Good.IRON));
    }

    /* ============================ 9. the iron rows against the study ============================ */

    /** The iron rows of batch I3 (0.7.116), by their permanent ids: the Iron Mine, the Electric Arc Furnace, the Steel Mini-Mill. */
    static final int[] IRON_ROWS = { Mining.IRON_MINE, HeavyIndustry.ARC_FURNACE, HeavyIndustry.MINI_MILL };

    /** The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). */
    static final double[][] STUDY_CASES = { { 2.7, 2.7, 4.1, 4.1 }, { -0.5, -0.1, 0.7, 0.2 }, { -0.5, 0.1, 1.0, 0.4 } };

    /** ...their cost over a year of what each would sell abroad, its last column. */
    static final double[] STUDY_YEARS = { 2.2, 4.4, 3.5 };

    /** ...and their running cost a month, D$ thousands, the study's 2.4 "Runs" (the mine's with its diesel in it). */
    static final double[] STUDY_RUNS = { 469, 187, 708 };

    /** The study's price of a litre of diesel in its runs, D$ thousands: the oil mockups' D$0.60 (its 1.5). */
    static final double STUDY_DIESEL = .0006;

    /** The study's rounding: its cases to a tenth of a point, its years to a tenth. */
    static final double STUDY_ROUNDING = .05;

    static void ironRows() {
        out.println("\n--- 9. the iron rows of batch I3 at parity: the study's four cases and its years abroad, to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        // The game's founding tariffs, the utility's own (the study's 1.5: D$10 a kW a month, D$50 a unit of water).
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        report("fixture: the game's founding tariffs are the study's: D$10 a kW a month, D$50 a unit of water",
                tariff == .01 && water == .05, String.format("%.3f, %.3f", tariff, water));
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        String[] cases = { "A", "B", "C", "D" };
        for (int r = 0; r < IRON_ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(IRON_ROWS[r]);
            String name = t.getName();
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            // Its running cost at founding, as BusinessInvestment.runningCostOf() strikes it: its posts at PayTier's wages, its power and water.
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            double diesel = t.uses(Good.DIESEL) * STUDY_DIESEL;
            report("its consumables are the study's running cost less its wages, power, water and diesel (at the study's D$0.60 a litre)",
                    Math.abs(t.getConsumables() - (STUDY_RUNS[r] - running - diesel)) < 1e-9,
                    String.format("%,.2f = %,.0f - %,.2f - %,.1f", t.getConsumables(), STUDY_RUNS[r], running, diesel));
            boolean all = true;
            StringBuilder got = new StringBuilder(), study = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                double pct = 100 * SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff) / cost;
                all &= Math.abs(pct - STUDY_CASES[r][c.ordinal()]) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.2f%%  ", cases[c.ordinal()], pct));
                study.append(c.ordinal() == 0 ? "" : "/").append(STUDY_CASES[r][c.ordinal()]);
            }
            report("the " + name + "'s four cases (SpreadPlanner.caseEarns(), at parity) are the study's to its rounding: " + study + "%",
                    all, got.toString().trim());
            double abroad = 0;
            for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) abroad += e.getValue() * e.getKey().worldExportPrice();
            double years = cost / (12 * abroad);
            report("...and its cost over a year of what it would sell abroad is the study's " + STUDY_YEARS[r],
                    Math.abs(years - STUDY_YEARS[r]) <= STUDY_ROUNDING + 1e-9, String.format("%.3f; D$%,.0fk all in", years, cost));
        }
        /*
         * WHERE caseEarns() READS THE ROWS OTHERWISE THAN THE STUDY DID (its
         * balance.py did not survive; its 1.2 and 2.5 are the arithmetic):
         *   - the Iron Mine's diesel is a feed, priced as the case prices
         *     feed - landed at DIESEL's import price in A and D, at its export
         *     price in B and C - where the study fixed it at D$0.60 a litre in
         *     its running cost. 60,000 L at .6883 is D$41.3k, at .5864 D$35.2k,
         *     against the study's D$36k: no case moves past its rounding;
         *   - the study's cases carry no repairs and no property tax, so
         *     standing is 0 here; the planner's estimate in a city adds them
         *     (BusinessInvestment.standingCostOf()), as it does every works'.
         */
        BuildingsTemplate mine = b.getTemplate(Mining.IRON_MINE);
        double atImport = mine.uses(Good.DIESEL) * Good.DIESEL.worldImportPrice(), atExport = mine.uses(Good.DIESEL) * Good.DIESEL.worldExportPrice();
        report("where caseEarns() and the study differ: the mine's diesel is a feed at the case's price, not D$0.60 a litre in its"
                        + " running cost - within D$6k a month of the study's either way, and no case past its rounding",
                Math.abs(atImport - mine.uses(Good.DIESEL) * STUDY_DIESEL) < 6 && Math.abs(atExport - mine.uses(Good.DIESEL) * STUDY_DIESEL) < 6,
                String.format("D$%,.1fk landed, D$%,.1fk at export, the study's D$%,.1fk", atImport, atExport, mine.uses(Good.DIESEL) * STUDY_DIESEL));
    }

    /* ============================ 10. a save from before the split ============================ */

    static void olderSave() throws Exception {
        out.println("\n--- 10. a save from before the split is refused, in P7's words and its file untouched; this build's loads ---");
        report("the floor is this build's own format (GameVersion): a save one format older - IRON holding scrap and ore as one - is"
                        + " no longer readable", GameVersion.OLDEST_LOADABLE_FORMAT == GameVersion.SAVE_FORMAT
                        && GameVersion.isNoLongerReadable(GameVersion.SAVE_FORMAT - 1) && !GameVersion.isNoLongerReadable(GameVersion.SAVE_FORMAT),
                String.format("formats %d to %d", GameVersion.OLDEST_LOADABLE_FORMAT, GameVersion.SAVE_FORMAT));
        GameFiles files = GameFiles.scratch("materialscheck-split");
        Game g = town(files);
        quietly(() -> {
            g.buildStack(g.getBuildingManager().getTemplate(HeavyIndustry.ARC_FURNACE), 2, true);
            g.simulateMonths(3);
            g.saveGame(10, "split");
        });
        String saved = Files.readString(files.saveFile(10));
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(saved).getAsJsonObject();
        Sector heavy = g.getSectors().heavyIndustry();
        double scrapBought = heavy.statement().bought == null || heavy.statement().bought.get(Good.SCRAP) == null ? 0
                : heavy.statement().bought.get(Good.SCRAP).total();
        report("fixture: a town whose two furnaces bought scrap three months, saved by this build in its own format, SCRAP in it by name",
                json.get("saveFormat").getAsInt() == GameVersion.SAVE_FORMAT && saved.contains("\"SCRAP\"") && scrapBought > 0,
                String.format("D$%,.1fk of scrap last month", scrapBought));

        GameFiles older = GameFiles.scratch("materialscheck-split-36");
        Files.createDirectories(older.saveFile(10).getParent());
        json.addProperty("saveFormat", GameVersion.SAVE_FORMAT - 1);
        json.addProperty("gameVersion", "0.7.115");
        Files.writeString(older.saveFile(10), new com.google.gson.Gson().toJson(json));
        byte[] before = Files.readAllBytes(older.saveFile(10));
        Game turned = new Game(older);
        quietly(() -> turned.loadGameSave(10));
        report("its twin written down to format 36 and stamped 0.7.115 is refused in P7's words: the build that saved it, which still"
                        + " opens it", GameVersion.noLongerReadable("0.7.115").equals(turned.getLoadFailure())
                        && turned.getLoadFailure().contains("Build 0.7.115 still opens it"),
                turned.getLoadFailure());
        assertTrue("...nothing of it applied, and its file byte for byte as it was",
                turned.getMonth() == 1 && java.util.Arrays.equals(before, Files.readAllBytes(older.saveFile(10))));

        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        Sector heavyBack = back.getSectors().heavyIndustry();
        double scrapBack = heavyBack.statement().bought == null || heavyBack.statement().bought.get(Good.SCRAP) == null ? 0
                : heavyBack.statement().bought.get(Good.SCRAP).total();
        report("this build's own save loads, its month, the scrap market's price and the furnaces' scrap as saved",
                back.getLoadFailure() == null && back.getMonth() == g.getMonth() && scrapBack == scrapBought
                        && back.getMarkets().get(Good.SCRAP).getLocalPrice() == g.getMarkets().get(Good.SCRAP).getLocalPrice()
                        && back.getMarkets().get(Good.IRON).getLocalPrice() == g.getMarkets().get(Good.IRON).getLocalPrice(),
                String.format("m%d, scrap at %.4f", back.getMonth(), back.getMarkets().get(Good.SCRAP).getLocalPrice()));
    }
}
