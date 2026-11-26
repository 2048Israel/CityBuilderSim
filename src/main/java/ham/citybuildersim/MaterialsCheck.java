package ham.citybuildersim;

import ham.citybuildersim.sectors.CopperRefining;
import ham.citybuildersim.sectors.ExtractionSector;
import ham.citybuildersim.sectors.Haulage;
import ham.citybuildersim.sectors.HeavyIndustry;
import ham.citybuildersim.sectors.Manufacturing;
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
 * (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.5-2.8 and 4), and
 * iron's sections as its batches came (I3 to I5) - coal, stone and copper
 * have harnesses of their own (CoalMiningCheck, QuarryingCheck,
 * CementConcreteCheck, CopperMiningCheck, CopperRefiningCheck).
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
 *      stockable one; three sectors in the game have captive goods, Iron &
 *      Steel (its scrap, coke and pig iron, since 0.7.123 - none until then),
 *      Copper Refining (its anode and cathode, since 0.7.133) and Wood
 *      Products (its residues and pulp, since 0.7.140).
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
 * ...AND EACH CHAIN'S SECTIONS, FROM BATCH I3 (0.7.120; iron's prices and
 * scrap split from ore - docs/studies/spec-materials.md 4):
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
 *
 * ...AND BATCH I4's (0.7.123; the integrated route and old steel -
 * docs/studies/spec-materials.md 2.2, 2.3, 2.7 to 2.9 and 4):
 *
 *  11. THE INTEGRATED ROUTE'S ROWS ARE THE STUDY'S: the Scrap Yard, the
 *      Pelletising Plant, the Coke Ovens, the Mini Blast Furnace, the Blast
 *      Furnace, the Oxygen Steel Shop and the Large Mini-Mill (ids 101 to
 *      107), each at parity: its all-in cost, posts, draw and ground the
 *      study's 2.4, its four cases (SpreadPlanner.caseEarns(), the yard's old
 *      steel at 70% of scrap's export price in every case) the study's 2.5 to
 *      its rounding, its cost a year abroad its last column, its consumables
 *      the study's running cost less wages, the power it buys and water.
 *
 *  12. OLD STEEL, AND THE 70% RULE (OldMetal): the four sources arise at
 *      RECOVERY of the steel in each, kept by seller; a yard buys as much as
 *      its scrap at the rate takes, no more than arose, pro rata from every
 *      seller at 70% of scrap's export price, the rest to landfill; the
 *      households are paid into their savings by the cars each scrapped, a
 *      sector by its other revenue, named apart, the yard's input billed by
 *      each; the yard is weighed against the pool (its feed gate and share);
 *      in a played town the audit closes with the households' part declared,
 *      and the pool and a seller's sale cross a save.
 *
 *  13. CAPTIVE COKE AND PIG IRON: in a works of the route's real rows, the
 *      ovens' coke goes to the furnaces first and the furnaces' pig iron to
 *      the shop, no sale or purchase booked for either, the market bid only
 *      the rest; and the vans count its goods only (FR2), none of it bulk it hands itself.
 *
 *  14. THE WORKS' OWN POWER SOLD: in a town with the ovens, the furnace and
 *      the shop standing, Iron & Steel sells the grid what the ovens and the
 *      furnace make past their draws (5,800 and 8,600 kW) and buys the shop's
 *      26,600, its statement's powerSold the surplus at the tariff, the
 *      utility paying it, and the audit closes.
 *
 * ...AND BATCH I5's (0.7.124; Iron Mining by type and size -
 * docs/studies/spec-materials.md 2.2, 2.4, 2.6b and 5's I5 row):
 *
 *  15. IRON MINING'S ROWS ARE THE STUDY'S: the Large Open-Pit Mine, the
 *      Magnetite Mine & Concentrator and the Large Magnetite Complex (ids 108
 *      to 110) at parity - all-in cost, posts, draw, water, the type, sites
 *      and dig they stand on - their four cases the study's 2.5 to its
 *      rounding and their cost a year abroad its last column; the Iron Mine
 *      (14) on one hematite site.
 *
 *  16. A MINE FITS ITS FIELD: on a town's hematite by fiat, a works of twenty
 *      sites is refused on a field of nineteen and ordered on one of twenty,
 *      its twenty then taken from the next order and from a run's next order;
 *      a magnetite works is refused on hematite whatever its free sites; and
 *      the plan orders only a mine that fits.
 *
 *  17. THE DIG AND THE RETIREMENT BY TYPE: on real fields of both types,
 *      as many twenty-site works fit as the fields of their type hold twenty
 *      sites, and the plan's order is the best earnings on the whole cost -
 *      its freight in it - of the mines that fit; with hematite and magnetite
 *      mines standing, a month's dig takes each type's share of the ask from
 *      its own ground, the magnetite works three tonnes of rock a tonne, and
 *      W holds by type to the tonne; once the hematite is out its mines alone
 *      are spare and may be sold, while the magnetite digs on.
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
        routeRows();
        oldSteel();
        captiveRoute();
        routePower();
        mineRows();
        mineFits();
        mineTypes();
        trailingYear();
        spikeMonth();
        threeMonthCycle();
        feedShortWorks();
        idleOverYear();
        ownPowerPlanned();
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
                // ...and the furnace's own, since 0.7.120: its electrodes at the factor, and no power of its own.
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
        // One sector in the game keeps captive goods since 0.7.123 (batch I4): Iron & Steel, its scrap, coke and pig iron (and since R6b, FE4, its pellets: the Hydrogen Steel Works uses what the pelletising plants make); and since
        // 0.7.133 (batch I8; spec 2.7, "anode and cathode in Copper Refining") a second, Copper Refining; and since 0.7.140 (batch
        // I10; spec 2.7, "residues and pulp") a third, Wood Products.
        Game g = new Game(GameFiles.scratch("materialscheck-captive"));
        quietly(g::newGame);
        boolean others = true;
        java.util.Set<Good> kept = java.util.EnumSet.noneOf(Good.class), copper = java.util.EnumSet.noneOf(Good.class),
                wood = java.util.EnumSet.noneOf(Good.class);
        for (Sector s : g.getSectors().all()) for (Good x : s.goodsMade()) {
            if (s == g.getSectors().heavyIndustry()) { if (s.captive(x)) kept.add(x); }
            else if (s == g.getSectors().copperRefining()) { if (s.captive(x)) copper.add(x); }
            else if (s == g.getSectors().woodProducts()) { if (s.captive(x)) wood.add(x); }
            else others &= !s.captive(x) && s.captiveUnits(x) == 0;
        }
        assertTrue("three sectors in the game keep captive goods, Iron & Steel (a ProcessingSector since 0.7.120): since 0.7.123 its"
                        + " scrap, coke and pig iron, which its works both make and use, and since R6b (FE4) its pellets, which the Hydrogen Steel Works uses - since 0.7.133 Copper Refining, its anode and"
                        + " cathode, and since 0.7.140 Wood Products, its residues and pulp; every other none (Sector.captive() false): "
                        + kept + " " + copper + " " + wood,
                others && g.getSectors().heavyIndustry() instanceof ProcessingSector
                        && kept.equals(java.util.EnumSet.of(Good.SCRAP, Good.PELLETS, Good.PIG_IRON, Good.MET_COKE))
                        && copper.equals(java.util.EnumSet.of(Good.ANODE, Good.COPPER))
                        && wood.equals(java.util.EnumSet.of(Good.RESIDUES, Good.PULP)));
    }

    /* ============================ 4. the works' own power ============================ */

    /** The works' price the last month of powerTown() was struck at: the book of the month before, read before that month ran (PW1; a statement is struck at the top of its month, at the book the month before struck). */
    static double powerTownWorksPrice;

    /** The power town: section 4's and 5's, bakeries handed power of their own and consumables, run `months`. */
    static Game powerTown(GameFiles files, int months) {
        Game g = town(files);
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate big = b.getTemplateByName("Industrial Bakery"), small = b.getTemplateByName("Bakery");
        quietly(() -> {
            // THE FIXTURE CAUSES IT (PW1): the merit order pays the works what their kWh clear at, and the founding grid (10,000 kW, free)
            // clears a town of 400 homes at nothing. The homes draw 50 kW each, so the town outruns the grid and the coal plant is the margin.
            b.getTemplateByName("House").setElectricityConsumption(50);
            big.setGen(3.0 * big.getElectricityConsumption());
            small.setGen(.5 * small.getElectricityConsumption());
            big.setConsumables(.001 * big.getCashCost());
            g.buildStack(big, 3, true);
            g.buildStack(small, 2, true);
            g.simulateMonths(months - 1);
            powerTownWorksPrice = g.getEconomyManager().getWorksPricePerWatt();
            g.simulateMonths(1);
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
        double price = g.getEconomyManager().getPricePerWatt(), worksPrice = powerTownWorksPrice;
        double surplus = b.totalBySector(Sectors.INDUSTRY, x -> BuildingsTemplate.isBilledForUtilities(x) ? x.powerSold() : 0);
        double net = b.totalBySector(Sectors.INDUSTRY, x -> BuildingsTemplate.isBilledForUtilities(x) ? x.powerBought() : 0);
        double gross = b.totalBySector(Sectors.INDUSTRY, x -> BuildingsTemplate.isBilledForUtilities(x) ? x.getElectricityConsumption() : 0);
        report("fixture: three Industrial Bakeries making three times their draw and two Bakeries half theirs, three months on",
                surplus > 0 && net > 0 && net < gross, String.format("Industry draws %,.0f kW net of %,.0f, sells %,.0f kW", net, gross, surplus));
        assertTrue("the sector's surplus is its buildings' power past their draws, and its bill its net draw at the ratio, the month's season (PW1-R: the kWh served) and the tariff",
                industry.getPowerSurplus() == surplus
                        && industry.getElectricityCost() == net * industry.getEnergyRatio() * industry.getPowerSeason() * price
                        && industry.getPowerSeason() == u.getPowerSeason());
        Sector.Statement st = industry.statement();
        assertTrue("its statement's powerSold is the surplus at the works' price (the struck book's), undimmed by the grid, and in its revenue past its sales",
                st.powerSold == surplus * worksPrice && st.powerSold > 0
                        && st.revenue == st.localSales + st.exports + st.otherRevenue + st.powerSold);
        assertTrue("...and in its operating income, every cost taken off the revenue it is in",
                st.operatingIncome == st.revenue - st.inputs - st.payroll - st.electricity - st.water - st.maintenance);
        double allNet = b.getTotalDouble(BuildingsTemplate::powerBought), allSold = b.getTotalDouble(BuildingsTemplate::powerSold);
        // PW9: what the grid generates is its jobless founding endowment and each plant
        // standing at its own posts' fill (a hydro station at its water), as updateEnergyRatio() offers them; the works' surplus on top.
        double generated = u.getFoundingGridKw();
        for (BuildingsTemplate p : b.getTemplates()) {
            long n = p.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(p) ? 0 : b.getQuantity(p.getId());
            if (n > 0) generated += p.getProduction1() * n * (p.isHydro() ? (u.plantFill(p) > 0 ? 1 : 0) : u.plantFill(p));
        }
        report("the grid's consumption is every building's net draw, and the works' surplus is added to what it generates, whatever"
                + " the utility's staffing", u.getConsumption() == allNet && u.getWorksPower() == allSold && allSold == surplus
                && Math.abs(u.getProduction() - (generated + allSold)) <= 1e-9 * (generated + allSold),
                String.format("production %,.4f against %,.4f generated and %,.4f sold", u.getProduction(), generated, allSold));
        // PW1-W: the water's books pay the power's for the water works' draw, a line of the electricity income (UtilitiesHandler, THE WATER'S POWER).
        report("fixture: the town's water works drew power the water's books paid for", u.getWaterPowerPaid() > 0, String.format("%,.6f", u.getWaterPowerPaid()));
        assertTrue("the utility pays the works what their statements booked, out of its electricity income, which gains what the water's books"
                        + " pay it for the water works' power",
                u.getPowerBought() == g.getEconomyManager().getSectorPowerSold() && u.getPowerBought() == st.powerSold
                        && u.getElectricityIncome() == u.getElectricityRevenue() - u.getElectricityPayroll() - u.getPowerBought() + u.getWaterPowerPaid());
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
        // THE FIXTURE CAUSES IT: the furnace's own electrodes (0.7.120) taken away, so the works has none.
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
        // In a game: the investors' test reads the planner's estimate (Game.consider()) - Iron & Steel's own since 0.7.120 (batch I3).
        Game g = town("materialscheck-plan");
        ProcessingSector w = g.getSectors().heavyIndustry();
        w.setPricePerWatt(g.getEconomyManager().getPricePerWatt());
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate foundry = g.getBuildingManager().getTemplate(HeavyIndustry.ARC_FURNACE);
        List<SpreadPlanner.Candidate> all = w.appraise(plans, g);
        assertTrue("in a town Iron & Steel's candidates are its nine works (the two furnaces since 0.7.120, and since 0.7.123 the"
                        + " integrated route's seven), each the planner's estimate, and the investors' test reads it",
                all.size() == 9 && w.estimatedMonthlyProfit(foundry, plans) == w.appraise(foundry, plans).earns()
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
        long sites = mining.sitesOwned();
        report("its sites owned and worked are the iron ones (LandManager.getIronDeposits(), Game.minesCommitted())",
                sites == land.getIronDeposits() && mining.sitesWorked() == g.minesCommitted() && sites >= 3
                        && mining.sitesWorked() == 0, String.format("%d sites, %d worked", sites, mining.sitesWorked()));
        BusinessInvestment.Decision d = mining.plan(plans, g);
        report("with free sites and ore in the ground it sinks an Iron Mine, or says why not (the staff, the ground)",
                d.build ? d.template == mine && d.quantity == 1 && d.reason.contains("deposit(s) unworked")
                        : d.reason != null && !d.reason.equals("no deposit to dig") && !d.reason.equals("the iron ore is worked out"),
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

    /** The iron rows of batch I3 (0.7.120), by their permanent ids: the Iron Mine, the Electric Arc Furnace, the Steel Mini-Mill. */
    static final int[] IRON_ROWS = { Mining.IRON_MINE, HeavyIndustry.ARC_FURNACE, HeavyIndustry.MINI_MILL };

    /** The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). */
    static final double[][] STUDY_CASES = { { 2.7, 2.7, 4.1, 4.1 }, { -0.5, -0.1, 0.7, 0.2 }, { -0.5, 0.1, 1.0, 0.4 } };

    /** ...their cost over a year of what each would sell abroad, its last column. */
    static final double[] STUDY_YEARS = { 2.2, 4.4, 3.5 };

    /** ...and their running cost a month, D$ thousands, the study's 2.4 "Runs" (the mine's with its diesel in it). */
    static final double[] STUDY_RUNS = PriceLevelCheck.atFounding(469, 187, 708); // today's figures, priced at the founding's level (EP1)

    /** The study's price of a litre of diesel in its runs, D$ thousands: the oil mockups' D$0.60 (its 1.5). */
    static final double STUDY_DIESEL = .0006 * PriceLevel.FACTOR; // today's figures, priced at the founding's level (EP1)

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
                tariff == .01 * PriceLevel.FACTOR && water == .05 * PriceLevel.FACTOR, String.format("%.3f, %.3f", tariff, water));
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
                Math.abs(atImport - mine.uses(Good.DIESEL) * STUDY_DIESEL) < 6 * PriceLevel.FACTOR && Math.abs(atExport - mine.uses(Good.DIESEL) * STUDY_DIESEL) < 6 * PriceLevel.FACTOR,
                String.format("D$%,.1fk landed, D$%,.1fk at export, the study's D$%,.1fk", atImport, atExport, mine.uses(Good.DIESEL) * STUDY_DIESEL));
    }

    /* ============================ 10. a save from before the split ============================ */

    static void olderSave() throws Exception {
        out.println("\n--- 10. a save from before the split is refused, in P7's words and its file untouched; this build's loads ---");
        // (Since 0.7.121 the floor was the split's format, no longer this build's: E4's format 38 left it, a format-37 save loading
        // rightly; since 0.8.5 it is FR2's 40, past the split, so a save from before the split is refused with every older one.)
        report("the floor is at or past the split's format (GameVersion): a save one format older is no longer readable, and one"
                        + " from before the split - IRON holding scrap and ore as one - with it", GameVersion.OLDEST_LOADABLE_FORMAT <= GameVersion.SAVE_FORMAT
                        && GameVersion.isNoLongerReadable(GameVersion.OLDEST_LOADABLE_FORMAT - 1) && !GameVersion.isNoLongerReadable(GameVersion.OLDEST_LOADABLE_FORMAT),
                String.format("formats %d to %d", GameVersion.OLDEST_LOADABLE_FORMAT, GameVersion.SAVE_FORMAT));
        GameFiles files = GameFiles.scratch("materialscheck-split");
        Game g = town(files);
        // ...on the literals, steel's world prices doubled (FL1): an Arc Furnace on imported scrap makes steel that nets under its scrap's cost
        // abroad, and THE ORDER no longer buys scrap for an export at a loss; the premise is furnaces that bought scrap, so their steel pays.
        g.playOnLiterals();
        g.getMarkets().get(Good.STEEL).setWorldPrices(Good.STEEL.worldImportPrice() * 2, Good.STEEL.worldExportPrice() * 2);
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
        json.addProperty("saveFormat", GameVersion.OLDEST_LOADABLE_FORMAT - 1);
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

    /* ============================ 11. the integrated route's rows against the study ============================ */

    /** The integrated route's rows of batch I4 (0.7.123), by their permanent ids, in the study's 2.4 order. */
    static final int[] ROUTE_ROWS = { HeavyIndustry.SCRAP_YARD, HeavyIndustry.PELLETISING_PLANT, HeavyIndustry.COKE_OVENS,
            HeavyIndustry.MINI_BLAST_FURNACE, HeavyIndustry.BLAST_FURNACE, HeavyIndustry.OXYGEN_STEEL_SHOP, HeavyIndustry.LARGE_MINI_MILL };

    /** The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). */
    static final double[][] ROUTE_CASES = { { 1.5, 1.5, 4.7, 4.7 }, { 0.1, 1.8, 3.7, 2.0 }, { -0.2, 0.7, 2.2, 1.3 },
            { -1.1, 1.7, 3.3, 0.5 }, { 0.2, 1.9, 3.0, 1.3 }, { -2.1, -0.6, 2.3, 0.8 }, { -0.2, 1.0, 2.9, 1.6 } };

    /** ...their cost over a year of what each would sell abroad, its last column. */
    static final double[] ROUTE_YEARS = { 1.0, 1.2, 1.7, 0.8, 1.1, 1.1, 1.7 };

    /** ...their running cost a month, D$ thousands, the study's 2.4 "Runs" (only the power each buys from the grid in it, the study's 1.6). */
    static final double[] ROUTE_RUNS = PriceLevelCheck.atFounding(255, 1000, 1250, 868, 11700, 25600, 5100); // today's figures, priced at the founding's level (EP1)

    /** ...their all-in cost, D$ thousands (the study's 2.4). */
    static final double[] ROUTE_COSTS = PriceLevelCheck.atFounding(35_000, 270_000, 264_000, 54_000, 1_350_000, 1_800_000, 1_200_000); // today's figures, priced at the founding's level (EP1)

    /** ...their posts (the study's 2.4). */
    static final int[] ROUTE_POSTS = { 40, 150, 200, 150, 1200, 1800, 400 };

    /** ...their draw, kW (the study's 2.4 and 1.6). */
    static final int[] ROUTE_KW = { 550, 5750, 2200, 2050, 51400, 51600, 77000 };

    /** ...and what they make, kW: the Coke Ovens', the Blast Furnace's and the Oxygen Steel Shop's power from their gas (the study's 2.4 and 1.6). */
    static final double[] ROUTE_GEN = { 0, 0, 8000, 0, 60000, 25000, 0 };

    /** ...and their ground, hectares (the study's 2.4). */
    static final double[] ROUTE_HECTARES = { 1.9, 15, 9, 3.7, 60, 84, 46 };

    static void routeRows() {
        out.println("\n--- 11. the integrated route's rows of batch I4 at parity: the study's four cases and its years abroad, to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        String[] cases = { "A", "B", "C", "D" };
        for (int r = 0; r < ROUTE_ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(ROUTE_ROWS[r]);
            String name = t.getName();
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            report("the " + name + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", ROUTE_COSTS[r] / PriceLevel.FACTOR) + "k all in, "
                            + ROUTE_POSTS[r] + " posts, " + String.format("%,d", ROUTE_KW[r]) + " kW drawn and "
                            + String.format("%,.0f", ROUTE_GEN[r]) + " made, " + ROUTE_HECTARES[r] + " ha; Iron & Steel's, dated "
                            + t.getSince(),
                    PriceLevelCheck.sameMoney(cost, ROUTE_COSTS[r]) && t.getTotalJobs() == ROUTE_POSTS[r] && t.getElectricityConsumption() == ROUTE_KW[r]
                            && t.getGen() == ROUTE_GEN[r] && Math.round(t.getLandSqFt() / 107_639.104 * 100) == Math.round(ROUTE_HECTARES[r] * 100)
                            && Sectors.HEAVY_INDUSTRY.equals(t.getSector()) && t.getSince() >= 1900,
                    String.format("cash %,.0f + 18 x %,d material", t.getCashCost(), t.getConstructionMaterials()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            // Its running cost as BusinessInvestment.runningCostOf() strikes it at founding: posts, its whole draw and water.
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            double bought = t.powerBought() * tariff;
            report("its consumables are the study's running cost less its wages, the power it buys from the grid and its water",
                    Math.abs(t.getConsumables() - (ROUTE_RUNS[r] - wages - bought - t.getWaterConsumption() * water)) < 1e-9,
                    String.format("%,.2f = %,.0f - %,.2f - %,.1f - %,.2f", t.getConsumables(), ROUTE_RUNS[r], wages, bought,
                            t.getWaterConsumption() * water));
            boolean all = true;
            StringBuilder got = new StringBuilder(), study = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                double pct = 100 * SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff) / cost;
                all &= Math.abs(pct - ROUTE_CASES[r][c.ordinal()]) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.2f%%  ", cases[c.ordinal()], pct));
                study.append(c.ordinal() == 0 ? "" : "/").append(ROUTE_CASES[r][c.ordinal()]);
            }
            report("...its four cases (SpreadPlanner.caseEarns(), at parity) are the study's to its rounding: " + study + "%",
                    all, got.toString().trim());
            double abroad = 0;
            for (Map.Entry<Good, Double> e : t.goodsMade().entrySet()) abroad += e.getValue() * e.getKey().worldExportPrice();
            double years = cost / (12 * abroad);
            report("...and its cost over a year of what it would sell abroad is the study's " + ROUTE_YEARS[r],
                    Math.abs(years - ROUTE_YEARS[r]) <= STUDY_ROUNDING + 1e-9, String.format("%.3f", years));
        }
        /*
         * WHAT THE CASES READ (the study's 1.2 and 2.5): the yard's old steel
         * is not a good, so caseEarns() takes it at 70% of scrap's export
         * price in every case (OldMetal.PAY_SHARE, Jerus's Q8), D$210 a
         * tonne; its power the study's 1.6 rule - running charges the whole
         * draw, and ownCosts() gives back what the works makes, so a works
         * making more than it draws sells the rest at the tariff. And on
         * imported feed for export (case A) none earns past 0.2%: under every
         * hurdle the game strikes, freight in the bands holding it (Q7).
         */
        BuildingsTemplate yard = b.getTemplate(HeavyIndustry.SCRAP_YARD);
        double withOld = SpreadPlanner.caseEarns(yard, SpreadPlanner.Case.A, imp, exp, 0, 0, 1, tariff);
        double without = yard.makes(Good.SCRAP) * Good.SCRAP.worldExportPrice() - (yard.getConsumables() - yard.getGen() * tariff);
        assertTrue("the yard's old steel is in every case at 70% of scrap's export price, D$210 a tonne at parity: 10,500 t, D$2,205k",
                Math.abs((without - withOld) - yard.oldMetal(Good.STEEL) * OldMetal.PAY_SHARE * Good.SCRAP.worldExportPrice()) < 1e-9
                        && Math.abs(yard.oldMetal(Good.STEEL) * OldMetal.PAY_SHARE * Good.SCRAP.worldExportPrice() - 2205 * PriceLevel.FACTOR) < 1e-9);
        double worstA = Double.NEGATIVE_INFINITY;
        for (int r = 1; r < ROUTE_ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(ROUTE_ROWS[r]);
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            worstA = Math.max(worstA, 100 * SpreadPlanner.caseEarns(t, SpreadPlanner.Case.A, imp, exp, running, 0, 1, tariff) / cost);
        }
        report("on imported feed for export (case A) no works of the route earns past 0.25% of its cost a month at parity (the study's"
                + " 1.2: at most 0.2%)", worstA <= .25, String.format("the best %.3f%%", worstA));
    }

    /* ============================ 12. old steel, and the 70% rule ============================ */

    static void oldSteel() throws Exception {
        out.println("\n--- 12. old steel: the four sources at 85%, a yard buying it pro rata at 70% of scrap's export price, the rest to landfill ---");
        OldMetal pool = new OldMetal();
        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        BuildingsTemplate house = bm.getTemplateByName("House");
        pool.carsScrapped(100);
        pool.demolished(house, 2, Sectors.REAL_ESTATE);
        pool.fabricated(Sectors.MANUFACTURING, 1000);
        pool.setsRetired(Sectors.FREIGHT, 2);
        double cars = 100 * OldMetal.STEEL_A_CAR * OldMetal.RECOVERY, demol = 2 * house.getConstructionMaterials() * OldMetal.STEEL_A_MATERIALS_UNIT
                * OldMetal.RECOVERY, offcuts = 1000 * OldMetal.OFFCUTS_A_TONNE_FABRICATED * OldMetal.RECOVERY,
                sets = 2 * OldMetal.STEEL_A_SET * OldMetal.RECOVERY;
        report("each source arises at RECOVERY (85%) of the steel in it, kept by its seller: 100 cars at 0.9 t the households', two Houses"
                        + " at 0.3 t a unit of material the landlords', 1,000 t of fabricated steel's 5% offcuts its maker's, two worn sets at"
                        + " 300 t the railway's",
                near(pool.availableFrom(Trade.HOUSEHOLDS), cars) && near(pool.availableFrom(Sectors.REAL_ESTATE), demol)
                        && near(pool.availableFrom(Sectors.MANUFACTURING), offcuts) && near(pool.availableFrom(Sectors.FREIGHT), sets)
                        && near(pool.available(OldMetal.Source.CARS), cars) && near(pool.available(OldMetal.Source.DEMOLITIONS), demol)
                        && near(pool.available(OldMetal.Source.OFFCUTS), offcuts) && near(pool.available(OldMetal.Source.ROLLING_STOCK), sets)
                        && near(pool.available(), cars + demol + offcuts + sets),
                String.format("%,.2f t: cars %,.2f, demolitions %,.2f, offcuts %,.2f, sets %,.2f", pool.available(), cars, demol, offcuts, sets));
        double stood = pool.available(), take = stood / 4, price = OldMetal.PAY_SHARE * .30;
        java.util.List<OldMetal.Sale> sales = pool.settle(take, price);
        boolean pro = sales.size() == 4;
        double paid = 0, tonnes = 0;
        for (OldMetal.Sale x : sales) {
            tonnes += x.tonnes();
            paid += x.paid();
            double had = x.seller().equals(Trade.HOUSEHOLDS) ? cars : x.seller().equals(Sectors.REAL_ESTATE) ? demol
                    : x.seller().equals(Sectors.MANUFACTURING) ? offcuts : sets;
            pro &= near(x.tonnes(), had / 4) && near(x.paid(), had / 4 * price);
        }
        report("the yards take a quarter of what stands: from every seller a quarter of theirs, each paid its tonnes at the price; the"
                        + " rest to landfill, the pool empty after",
                pro && near(tonnes, take) && near(paid, take * price) && pool.available() == 0 && near(pool.lastBought(), take)
                        && near(pool.lastLandfill(), stood - take) && near(pool.lastArisen(), stood) && pool.lastPrice() == price
                        && near(pool.lastPaidTotal(), paid) && near(pool.lastArisen(OldMetal.Source.CARS), cars),
                String.format("%,.2f t bought of %,.2f, %,.2f to landfill", pool.lastBought(), pool.lastArisen(), pool.lastLandfill()));
        pool.carsScrapped(10);
        pool.settle(1e9, price);
        assertTrue("...and asked for more than arose they take what arose, no more, and nothing goes to landfill",
                near(pool.lastBought(), 10 * OldMetal.STEEL_A_CAR * OldMetal.RECOVERY) && pool.lastLandfill() == 0);
        pool.settle(0, price);
        assertTrue("...a month nothing arose records nothing, and pays nobody", pool.lastArisen() == 0 && pool.lastBought() == 0
                && pool.lastPaid().isEmpty());

        // THE FIXTURE CAUSES IT: a town with a Scrap Yard standing, and old steel handed to its pool from the households and the fabricators.
        GameFiles files = GameFiles.scratch("materialscheck-oldsteel");
        Game g = town(files);
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        Sector maker = g.getSectors().byKey(Sectors.MANUFACTURING);
        BuildingsTemplate yardT = g.getBuildingManager().getTemplate(HeavyIndustry.SCRAP_YARD);
        // ...three months on, so the town has its households and a yard standing.
        quietly(() -> {
            g.buildStack(yardT, 1, true);
            g.simulateMonths(3);
        });
        OldMetal city = g.getOldMetal();
        HouseholdBalance hh = g.getHouseholdBalance();
        double before = hh.totalSavings();
        double credited = hh.creditScrappedCars(1000);
        assertTrue("the households are paid for their cars into their savings, all of it, by the cars each scrapped",
                credited == 1000 && near(hh.totalSavings() - before, 1000));
        double handedHh = 4_000 / OldMetal.STEEL_A_CAR / OldMetal.RECOVERY, handedMfg = 3_000 / OldMetal.OFFCUTS_A_TONNE_FABRICATED / OldMetal.RECOVERY;
        city.carsScrapped(handedHh);
        city.fabricated(Sectors.MANUFACTURING, handedMfg);
        double arisen = city.available();
        heavy.setPricePerWatt(g.getEconomyManager().getPricePerWatt());
        double rate = heavy.getOperatingRate();
        double scrapAsked = yardT.makes(Good.SCRAP) * rate;
        double per = yardT.oldMetal(Good.STEEL) / yardT.makes(Good.SCRAP);
        report("fixture: a town's Scrap Yard standing, its pool handed 4,000 t of old steel from the households' cars and 3,000 t from"
                        + " the fabricators' offcuts",
                heavy.oldSteelIntake() == 10_500 && near(heavy.oldSteelPerTonneOfScrap(), 1.05) && near(arisen, 7000) && rate > 0,
                String.format("rate %.3f; %,.0f t of scrap asked for %,.0f t of old steel", rate, scrapAsked, scrapAsked * per));
        double hhSavings = hh.totalSavings();
        heavy.produceFlow(Good.SCRAP);
        Sector.Output scrap = heavy.outputRow(Good.SCRAP);
        double made = scrap.produced + scrap.captive, bought = Math.min(arisen, scrapAsked * per);
        double p70 = OldMetal.PAY_SHARE * g.getMarkets().get(Good.SCRAP).exportPrice();
        report("the yard makes its scrap from what arose, at 1.05 t of old steel a tonne, no more than arose, and buys it at 70% of scrap's"
                        + " export price (Jerus's Q8)",
                bought > 0 && near(made, bought / per) && near(city.lastBought(), bought) && near(city.lastLandfill(), arisen - bought)
                        && heavy.oldSteelPrice() == p70 && city.lastPrice() == p70,
                String.format("%,.1f t of scrap from %,.1f t at D$%.4fk a tonne; %,.1f t to landfill", made, bought, p70, city.lastLandfill()));
        double hhPaid = city.lastPaid().getOrDefault(Trade.HOUSEHOLDS, 0.0), mfgPaid = city.lastPaid().getOrDefault(Sectors.MANUFACTURING, 0.0);
        Sector.Ledger yl = heavy.pending(), ml = maker.pending();
        report("each seller paid pro rata: the households into their savings and on the yard's input billed by them, the fabricators by"
                        + " their other revenue, named apart, and billed by them",
                near(hhPaid, 4000 * (bought / arisen) * p70) && near(mfgPaid, 3000 * (bought / arisen) * p70)
                        && near(hh.totalSavings() - hhSavings, hhPaid)
                        && near(yl.purchasesBySupplier.getOrDefault(Trade.HOUSEHOLDS, 0.0), hhPaid)
                        && near(yl.purchasesBySupplier.getOrDefault(Sectors.MANUFACTURING, 0.0), mfgPaid)
                        && near(yl.otherInputs.getOrDefault(OldMetal.LINE, 0.0), hhPaid + mfgPaid)
                        && near(ml.oldMetalSold, mfgPaid) && near(ml.otherRevenue, mfgPaid),
                String.format("households D$%,.1fk, Manufacturing D$%,.1fk", hhPaid, mfgPaid));
        // ...struck: the seller's sale named apart, the buyer's input named, in the strike's window.
        maker.strike();
        heavy.strike();
        assertTrue("struck: the fabricators' sale is \"" + OldMetal.SOLD + "\" in their other revenue, and the yard's \"" + OldMetal.LINE
                        + "\" an input, the households' part at no supplier's sales tax",
                near(maker.statement().otherParts.getOrDefault(OldMetal.SOLD, 0.0), mfgPaid)
                        && near(heavy.statement().otherInputs.getOrDefault(OldMetal.LINE, 0.0), hhPaid + mfgPaid)
                        && g.getSectors().byKey(Trade.HOUSEHOLDS) == null);

        // The planner weighs a yard against the pool: its feed gate and the share of nameplate it would run at.
        BusinessInvestment plans = g.getBusinessInvestment();
        double spare = Math.max(0, city.lastArisen() - heavy.oldSteelIntake());
        SpreadPlanner.Candidate another = heavy.appraise(yardT, plans);
        report("a second yard is refused its feed while the old steel past what the yard standing takes is under FEED_GATE of one",
                spare < SpreadPlanner.FEED_GATE * yardT.oldMetal(Good.STEEL) ? another.failed() == SpreadPlanner.Gate.FEED
                        && another.why().contains("too little old steel") : another.refusal(SpreadPlanner.Gate.FEED) == null,
                another.why());
        city.carsScrapped(30_000 / OldMetal.STEEL_A_CAR / OldMetal.RECOVERY);
        city.settle(0, p70);
        double spareNow = Math.max(0, city.lastArisen() - heavy.oldSteelIntake());
        SpreadPlanner.Candidate fed = heavy.appraise(yardT, plans);
        SpreadPlanner.City frame = heavy.planner().city(heavy, plans, g);
        double share = Math.min(1, spareNow / yardT.oldMetal(Good.STEEL)), at = frame.rate() * share;
        SpreadPlanner.Values here = SpreadPlanner.values(g.getMarkets());
        double byHand = ProcessingSector.gross(yardT, here, at) - frame.running(yardT) - frame.standing(yardT)
                - SpreadPlanner.ownCosts(yardT, at, heavy.exchangeFactor(), heavy.tariff())
                - (yardT.oldMetal(Good.STEEL) * at * p70 + ProcessingSector.freightRunning(yardT, here, at, heavy.legPrice()));
        report("...with 30,000 t arising it is fed, weighed on the share of nameplate the pool fills, paying 70% for its old steel and"
                        + " paying its freight, on its whole cost with its vans (ProcessingSector.feedShare(), offMarketFeed(), freightRunning(),"
                        + " fleetCapital())",
                fed.refusal(SpreadPlanner.Gate.FEED) == null && share == 1 && Math.abs(fed.earns() - byHand) <= 1e-9 * Math.max(1, Math.abs(byHand))
                        && fed.cost() == frame.cost(yardT) + ProcessingSector.fleetCapital(yardT, here),
                String.format("%,.1fk a month, %.2f%% of its cost", fed.earns(), 100 * fed.score()));
        double vans = ProcessingSector.vansFor(yardT);
        Haulage.LegPrice legs = heavy.legPrice();
        double legBill = ProcessingSector.cityLegs(yardT, at, legs), scrapLeg = legs.out(Good.SCRAP);
        assertTrue("a yard keeps no van for its 10,000 t of scrap, which is bulk and goes by Haulage's trucks (FR2): no van, no van's capital"
                        + " or wear, and its freight is its city leg alone - the scrap at the rate, at the leg Haulage's quote prices a made tonne",
                vans == 0 && ProcessingSector.fleetRunning(yardT, here, at) == 0 && ProcessingSector.fleetCapital(yardT, here) == 0
                        && scrapLeg > 0 && Math.abs(legBill - at * 10_000 * Good.SCRAP.tonnesPerUnit() * scrapLeg) <= 1e-9 * Math.max(1, legBill)
                        && ProcessingSector.freightRunning(yardT, here, at, legs) == legBill);

        // In a played town: old steel handed in each month, the yard buying it, and the audit closing with the households' part declared.
        GameFiles playFiles = GameFiles.scratch("materialscheck-oldsteel-play");
        Game play = town(playFiles);
        quietly(() -> {
            play.buildStack(play.getBuildingManager().getTemplate(HeavyIndustry.SCRAP_YARD), 1, true);
            play.simulateMonths(3);
        });
        double worst = 0, paidHh = 0;
        boolean declared = true;
        double capturedHh = 0;
        for (int m = 0; m < 4; m++) {
            // ...more than the yard takes, so it runs at its rate and the rest goes to landfill.
            play.getOldMetal().carsScrapped(6_000 / OldMetal.STEEL_A_CAR / OldMetal.RECOVERY);
            play.getOldMetal().fabricated(Sectors.MANUFACTURING, 6_000 / OldMetal.OFFCUTS_A_TONNE_FABRICATED / OldMetal.RECOVERY);
            quietly(() -> play.simulateMonths(1));
            MoneyAudit.Result r = play.getLastMoneyAudit();
            worst = Math.max(worst, r.relative());
            if (m > 0) declared &= near(MoneyAudit.oldMetalBoughtOfHouseholds(play.getSectors()), capturedHh)
                    && r.detail.contains("- sectors OldMetalToHouseholds") && r.detail.contains("chk + households OldMetalSold")
                    && r.detail.contains("chk + sectors OldMetalSold") && r.detail.contains("chk - sectors OldMetalBought");
            capturedHh = play.getOldMetal().lastPaid().getOrDefault(Trade.HOUSEHOLDS, 0.0);
            paidHh += capturedHh;
        }
        Sector.Statement yst = play.getSectors().heavyIndustry().statement();
        double soldBySectors = 0;
        for (Sector x : play.getSectors().all()) soldBySectors += x.statement().otherParts.getOrDefault(OldMetal.SOLD, 0.0);
        report("in a town played four months, the yard buying each month's old steel: every month's audit closes, the households' part"
                        + " declared as it leaves the pools (\"- sectors OldMetalToHouseholds\") and the yard's purchase the households' and"
                        + " the sectors' together",
                worst < 1e-4 && declared && paidHh > 0 && near(yst.otherInputs.getOrDefault(OldMetal.LINE, 0.0),
                        MoneyAudit.oldMetalBoughtOfHouseholds(play.getSectors()) + soldBySectors) && soldBySectors > 0,
                String.format("worst residual %.3g of what moved; the yard bought D$%,.1fk", worst, yst.otherInputs.getOrDefault(OldMetal.LINE, 0.0)));
        // ...and across a save: the pool, its record, and a seller's sale still to strike.
        play.getOldMetal().carsScrapped(500);
        play.getSectors().byKey(Sectors.FREIGHT).sellOldMetal(12.5);
        quietly(() -> play.saveGame(10, "oldsteel"));
        Game back = new Game(playFiles);
        quietly(() -> back.loadGameSave(10));
        OldMetal then = play.getOldMetal(), now = back.getOldMetal();
        assertTrue("the pool crosses a save (DataSave.oldMetal) to the bit - what stands by seller and source, the last month's record and"
                        + " who was paid - and a seller's sale still to strike (SectorState), named apart at its strike",
                back.getLoadFailure() == null && now.available() == then.available() && now.availableFrom(Trade.HOUSEHOLDS) == then.availableFrom(Trade.HOUSEHOLDS)
                        && now.available(OldMetal.Source.CARS) == then.available(OldMetal.Source.CARS)
                        && now.lastArisen() == then.lastArisen() && now.lastBought() == then.lastBought()
                        && now.lastLandfill() == then.lastLandfill() && now.lastPrice() == then.lastPrice()
                        && now.lastPaid().equals(then.lastPaid())
                        && back.getSectors().byKey(Sectors.FREIGHT).pending().oldMetalSold == play.getSectors().byKey(Sectors.FREIGHT).pending().oldMetalSold);
    }

    /* ============================ 13. captive coke and pig iron ============================ */

    static void captiveRoute() {
        out.println("\n--- 13. captive coke and pig iron: the ovens' coke to the furnaces first, their pig iron to the shop, nothing booked for either ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        Markets markets = new Markets();
        markets.setExchangeRate(1);
        BuildingsTemplate ovens = b.getTemplate(HeavyIndustry.COKE_OVENS), mini = b.getTemplate(HeavyIndustry.MINI_BLAST_FURNACE),
                shop = b.getTemplate(HeavyIndustry.OXYGEN_STEEL_SHOP);
        b.addStack(ovens, 1, true);
        b.addStack(mini, 2, true);
        b.addStack(shop, 1, true);
        HeavyIndustry w = new HeavyIndustry();
        Sector s = w;
        s.attach(b, markets);
        double rate = w.getOperatingRate();
        double cokeMade = ovens.makes(Good.MET_COKE) * rate, cokeNeed = 2 * mini.uses(Good.MET_COKE) * rate;
        double pigMade = 2 * mini.makes(Good.PIG_IRON) * rate, pigNeed = shop.uses(Good.PIG_IRON) * rate;
        report("fixture: Iron & Steel's real rows, one Coke Ovens, two Mini Blast Furnaces and one Oxygen Steel Shop, the coke and pig iron"
                        + " captive", w.captive(Good.MET_COKE) && w.captive(Good.PIG_IRON) && w.captive(Good.SCRAP) && !w.captive(Good.STEEL)
                        && Good.MET_COKE.stockable() && !Good.PIG_IRON.stockable(),
                String.format("rate %.3f; coke %,.0f made for %,.0f needed, pig iron %,.0f for %,.0f", rate, cokeMade, cokeNeed, pigMade, pigNeed));
        assertTrue("the coke: the furnaces' 13,500 t met first out of the ovens' 40,000, the market bid none of it",
                w.captiveUnits(Good.MET_COKE) == Math.min(cokeMade, cokeNeed) && w.bid(Good.MET_COKE) == 0);
        w.produceStock(Good.MET_COKE, markets.get(Good.MET_COKE));
        Sector.Output coke = w.outputRow(Good.MET_COKE);
        assertTrue("...and what the ovens make for the market - for home, for the ship and idled - is the 26,500 t past it",
                coke.captive == cokeNeed && Math.abs(coke.planned + coke.exportBound + coke.idled - (cokeMade - cokeNeed)) < 1e-6);
        w.produceFlow(Good.PIG_IRON);
        Sector.Output pig = w.outputRow(Good.PIG_IRON);
        assertTrue("the pig iron: all the furnaces' 30,000 t to the shop, none to the market, and the shop bids for the 216,500 t past it",
                pig.captive == pigMade && pig.produced == 0 && w.bid(Good.PIG_IRON) == pigNeed - pigMade);
        w.shipUnsoldFlow(Good.PIG_IRON, markets.get(Good.PIG_IRON));
        Sector.Ledger l = w.pending();
        assertTrue("...no sale and no purchase booked for what it handed itself: no pig iron sold or bought, no coke bought",
                !l.unitsSold.containsKey(Good.PIG_IRON) && !l.unitsBought.containsKey(Good.PIG_IRON) && !l.unitsBought.containsKey(Good.MET_COKE));
        double goodsGross = 0, bulkGross = 0, insideGoods = 0;
        for (Good g : w.goodsMade()) {
            double tonnes = w.getCapacity(g) * g.tonnesPerUnit();
            if (g.traffic() == Traffic.GOODS) goodsGross += tonnes; else bulkGross += tonnes;
        }
        for (Good g : w.goodsUsed()) {
            if (g == Good.VANS || g == Good.TRUCKS) continue;
            double tonnes = w.getInputAtCapacity(g) * g.tonnesPerUnit();
            if (g.traffic() == Traffic.GOODS) goodsGross += tonnes; else bulkGross += tonnes;
        }
        for (Good g : new Good[] {Good.MET_COKE, Good.PIG_IRON}) {
            if (g.traffic() == Traffic.GOODS) insideGoods += 2 * Math.min(w.getCapacity(g), w.getInputAtCapacity(g));
        }
        double inside = 2 * Math.min(w.getCapacity(Good.MET_COKE), w.getInputAtCapacity(Good.MET_COKE))
                + 2 * Math.min(w.getCapacity(Good.PIG_IRON), w.getInputAtCapacity(Good.PIG_IRON));
        report("its vans move its GOODS tonnes less what the works hands itself (Sector.tonnesMoved(), FR2): the route's coke, pig iron, ore,"
                        + " scrap and steel are bulk and go by Haulage's trucks, so none is a van's tonne and its own rails take none off",
                Math.abs(w.tonnesMoved() - (goodsGross - insideGoods)) < 1e-6 && insideGoods == 0 && bulkGross > inside
                        && inside == 2 * (13_500 + 30_000),
                String.format("%,.0f t a month for the vans, %,.0f t of bulk for the trucks, %,.0f t of it handed itself", w.tonnesMoved(), bulkGross, inside));
    }

    /* ============================ 14. the works' own power sold ============================ */

    static void routePower() {
        out.println("\n--- 14. the works' own power: the ovens and the furnace sell what they make past their draws, the shop buys the rest ---");
        GameFiles files = GameFiles.scratch("materialscheck-routepower");
        Game g = town(files);
        BuildingManager b = g.getBuildingManager();
        // ...the works' price the last month's statement is struck at: the book of the month before, read before that month runs (PW1).
        double[] struckAt = new double[1];
        quietly(() -> {
            b.addStack(b.getTemplate(HeavyIndustry.COKE_OVENS), 1, true);
            b.addStack(b.getTemplate(HeavyIndustry.BLAST_FURNACE), 1, true);
            b.addStack(b.getTemplate(HeavyIndustry.OXYGEN_STEEL_SHOP), 1, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            // ...four months: the first two clear before the plants are staffed (at nothing, then short), and a statement is struck at the month before's book.
            g.simulateMonths(3);
            struckAt[0] = g.getEconomyManager().getWorksPricePerWatt();
            g.simulateMonths(1);
        });
        Sector heavy = g.getSectors().heavyIndustry();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double industryPrice = g.getEconomyManager().getIndustryPricePerWatt(), worksPrice = struckAt[0];
        double sold = 5_800 + 8_600, bought = b.getTemplate(HeavyIndustry.COKE_OVENS).powerBought()
                + b.getTemplate(HeavyIndustry.BLAST_FURNACE).powerBought() + b.getTemplate(HeavyIndustry.OXYGEN_STEEL_SHOP).powerBought();
        report("fixture: a town with the Coke Ovens, the Blast Furnace and the Oxygen Steel Shop standing, four months on",
                b.getQuantity(HeavyIndustry.BLAST_FURNACE) == 1 && heavy.getPowerSurplus() == sold && bought == 26_600,
                String.format("Iron & Steel sells %,.0f kW, buys %,.0f", heavy.getPowerSurplus(), bought));
        Sector.Statement st = heavy.statement();
        assertTrue("its statement's powerSold is the ovens' 5,800 kW and the furnace's 8,600 at the works' price, in its revenue past its sales;"
                        + " its bill the shop's 26,600 at the ratio, the month's season and the industry price (Heavy Industry takes power at high voltage)",
                st.powerSold == sold * worksPrice && st.powerSold > 0 && st.revenue == st.localSales + st.exports + st.otherRevenue + st.powerSold
                        && heavy.getElectricityCost() == bought * heavy.getEnergyRatio() * heavy.getPowerSeason() * industryPrice
                        && heavy.getPowerSeason() == u.getPowerSeason());
        assertTrue("the utility pays the works what their statement booked, and the grid counts their surplus as generation",
                u.getPowerBought() == g.getEconomyManager().getSectorPowerSold() && u.getWorksPower() >= sold);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit.relative() < 1e-4, String.format("residual %.3g of what moved", audit.relative()));
    }

    /* ============================ 15. Iron Mining's rows against the study ============================ */

    /** Iron Mining's rows of batch I5 (0.7.124), by their permanent ids, in the study's 2.4 order: the Large Open-Pit Mine, the Magnetite Mine & Concentrator, the Large Magnetite Complex. */
    static final int[] MINE_ROWS = { Mining.LARGE_OPEN_PIT, Mining.MAGNETITE_MINE, Mining.LARGE_MAGNETITE };

    /** The study's four cases for them, A to D, in % of the cost a month at parity (materials-chains-research.md 2.5). */
    static final double[][] MINE_CASES = { { 3.7, 3.7, 5.2, 5.2 }, { 1.2, 1.2, 2.2, 2.2 }, { 2.2, 2.2, 3.3, 3.3 } };

    /** ...their cost over a year of what each would sell abroad, its last column. */
    static final double[] MINE_YEARS = { 2.0, 2.9, 2.7 };

    /** ...their running cost a month, D$ thousands, the study's 2.4 "Runs". */
    static final double[] MINE_RUNS = PriceLevelCheck.atFounding(5_420, 951, 9_580); // today's figures, priced at the founding's level (EP1)

    /** ...their all-in cost, D$ thousands (the study's 2.4). */
    static final double[] MINE_COSTS = PriceLevelCheck.atFounding(1_000_000, 60_000, 1_100_000); // today's figures, priced at the founding's level (EP1)

    /** ...their posts (the study's 2.4). */
    static final int[] MINE_POSTS = { 400, 120, 500 };

    /** ...their draw, kW, and water, units a month (the study's 2.4). */
    static final int[] MINE_KW = { 3_000, 3_000, 52_000 }, MINE_WATER = { 300, 400, 7_900 };

    /** ...what each makes a month, t of ore or concentrate, and the t of rock it digs for it (the study's 2.4: 60,000 t for the small magnetite works' 20,000, 1.2 Mt for the large one's 400,000; the hematite pit ships its ore as dug). */
    static final double[] MINE_MAKES = { 500_000, 20_000, 400_000 }, MINE_ROCK = { 500_000, 60_000, 1_200_000 };

    /** ...the type of field each stands on, and the sites it takes (the study's 1.3, 2.4: twenty sites for a large mine, one for a small). */
    static final Deposit.FieldType[] MINE_TYPES = { Deposit.FieldType.HEMATITE, Deposit.FieldType.MAGNETITE, Deposit.FieldType.MAGNETITE };
    /** ...and the sites each takes. */
    static final int[] MINE_SITES = { 20, 1, 20 };

    static void mineRows() {
        out.println("\n--- 15. Iron Mining's rows of batch I5 at parity: by type and size, the study's four cases and its years abroad, to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        String[] cases = { "A", "B", "C", "D" };
        BuildingsTemplate.Site iron = b.getTemplate(Mining.IRON_MINE).site();
        assertTrue("the Iron Mine (14) stands on one site of a hematite field, ore as dug (ANY until batch I5)",
                iron != null && iron.resource() == Resource.IRON && iron.type() == Deposit.FieldType.HEMATITE && iron.sites() == 1
                        && iron.dig() == 1);
        for (int r = 0; r < MINE_ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(MINE_ROWS[r]);
            String name = t.getName();
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            BuildingsTemplate.Site site = t.site();
            report("the " + name + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", MINE_COSTS[r] / PriceLevel.FACTOR) + "k all in, "
                            + MINE_POSTS[r] + " posts, " + String.format("%,d", MINE_KW[r]) + " kW, " + String.format("%,d", MINE_WATER[r])
                            + " water, " + String.format("%,.0f", MINE_MAKES[r]) + " t a month from " + String.format("%,.0f", MINE_ROCK[r])
                            + " t dug, on " + MINE_SITES[r] + " " + MINE_TYPES[r] + " site(s); Iron Mining's, dated " + t.getSince(),
                    PriceLevelCheck.sameMoney(cost, MINE_COSTS[r]) && t.getTotalJobs() == MINE_POSTS[r] && t.getElectricityConsumption() == MINE_KW[r]
                            && t.getWaterConsumption() == MINE_WATER[r] && t.makes(Good.IRON) == MINE_MAKES[r] && site != null
                            && site.resource() == Resource.IRON && site.type() == MINE_TYPES[r] && site.sites() == MINE_SITES[r]
                            && site.dig() * MINE_MAKES[r] == MINE_ROCK[r] && Sectors.MINING.equals(t.getSector()) && t.getSince() >= 1900
                            && t.getCategory() == BuildingType.MINING,
                    String.format("cash %,.0f + 18 x %,d material; dig %.0f", t.getCashCost(), t.getConstructionMaterials(),
                            site == null ? 0 : site.dig()));
            report("...its ground the iron sites it takes, an iron site's ground each (Resource.IRON.siteKm2())",
                    Math.abs(t.getLandSqFt() - MINE_SITES[r] * Resource.IRON.siteKm2() * LandManager.SQ_FT_PER_KM2) < 1e-6,
                    String.format("%,.0f sq ft", t.getLandSqFt()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            double diesel = t.uses(Good.DIESEL) * STUDY_DIESEL;
            report("its consumables are the study's running cost less its wages, power, water and diesel (at the study's D$0.60 a litre)",
                    Math.abs(t.getConsumables() - (MINE_RUNS[r] - running - diesel)) < 1e-9,
                    String.format("%,.2f = %,.0f - %,.2f - %,.1f", t.getConsumables(), MINE_RUNS[r], running, diesel));
            boolean all = true;
            StringBuilder got = new StringBuilder(), study = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                double pct = 100 * SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff) / cost;
                all &= Math.abs(pct - MINE_CASES[r][c.ordinal()]) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.2f%%  ", cases[c.ordinal()], pct));
                study.append(c.ordinal() == 0 ? "" : "/").append(MINE_CASES[r][c.ordinal()]);
            }
            report("...its four cases (SpreadPlanner.caseEarns(), at parity) are the study's to its rounding: " + study + "%",
                    all, got.toString().trim());
            double years = cost / (12 * t.makes(Good.IRON) * Good.IRON.worldExportPrice());
            report("...and its cost over a year of what it would sell abroad is the study's " + MINE_YEARS[r],
                    Math.abs(years - MINE_YEARS[r]) <= STUDY_ROUNDING + 1e-9, String.format("%.3f", years));
        }
        /*
         * WHERE THE DIESEL IS (the spec's 2.2: "DIESEL where the study names
         * it, scaled by tonnes to the large sibling"): the study names it for
         * the Iron Mine, 3 L a tonne, so the Large Open-Pit Mine burns the
         * same a tonne; it names none for the magnetite works, whose running
         * cost is their grinding power and consumables. With the magnetite
         * mine's 60,000 t of rock at the Iron Mine's 3 L, priced as caseEarns()
         * prices a feed, its case B would read 1.25%, past the study's 1.2.
         */
        BuildingsTemplate pit = b.getTemplate(Mining.LARGE_OPEN_PIT), mine = b.getTemplate(Mining.IRON_MINE);
        assertTrue("the open pit burns the Iron Mine's diesel a tonne; the magnetite works burn none",
                pit.uses(Good.DIESEL) / pit.makes(Good.IRON) == mine.uses(Good.DIESEL) / mine.makes(Good.IRON)
                        && b.getTemplate(Mining.MAGNETITE_MINE).uses(Good.DIESEL) == 0 && b.getTemplate(Mining.LARGE_MAGNETITE).uses(Good.DIESEL) == 0);
    }

    /* ============================ 16. a mine fits its field ============================ */

    static void mineFits() {
        out.println("\n--- 16. a mine fits its field: twenty sites of one field of its type for a large mine, never magnetite works on hematite ---");
        Game g = town("materialscheck-fits");
        Mining mining = g.getSectors().mining();
        LandManager land = g.getLandManager();
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate mine = b.getTemplate(Mining.IRON_MINE), pit = b.getTemplate(Mining.LARGE_OPEN_PIT),
                magnetite = b.getTemplate(Mining.MAGNETITE_MINE), complex = b.getTemplate(Mining.LARGE_MAGNETITE);
        final Deposit.FieldType hem = Deposit.FieldType.HEMATITE, mag = Deposit.FieldType.MAGNETITE;
        double tonnes = 19 * Resource.IRON.amountPerSite();
        quietly(() -> land.restoreSites(Resource.IRON, 19, tonnes));
        report("fixture: a town's iron by fiat, one field of 19 hematite sites (ground by fiat is the first type), none magnetite",
                land.getSites(Resource.IRON, hem) == 19 && land.getSites(Resource.IRON, mag) == 0
                        && land.getCityLand().typedPieces(Resource.IRON).size() == 1,
                String.format("%,.0f t", land.getRemaining(Resource.IRON, hem)));
        g.setCashForTest(g.getCash() + 4 * BuildingManager.MATERIALS_WORLD_PRICE * pit.getConstructionMaterials() + 4 * pit.getCashCost());
        Game.BuildResult refused = quietlyBuild(g, pit);
        assertTrue("a works of twenty sites is refused on a field of nineteen: no deposit for it, before the money",
                !g.hasDepositFor(pit, 1) && !mining.fits(pit) && refused == Game.BuildResult.NO_DEPOSIT && b.getQuantity(pit.getId()) == 0);
        assertTrue("...where one-site Iron Mines fit, nineteen and not twenty", g.hasDepositFor(mine, 19) && !g.hasDepositFor(mine, 20)
                && mining.fits(mine));
        assertTrue("a magnetite works is refused on hematite, nineteen sites free: the small one and the large",
                !g.hasDepositFor(magnetite, 1) && !g.hasDepositFor(complex, 1) && !mining.fits(magnetite) && !mining.fits(complex));
        // Workers handed to the town, as InvestCheck hands a shop's, so the staff is not what stands in the plan's way.
        handWorkers(g);
        BusinessInvestment.Decision d = mining.plan(g.getBusinessInvestment(), g);
        report("...and the plan orders the mine that fits, the Iron Mine - never the pit or a magnetite works",
                d.build && d.template == mine && d.reason.contains("of hematite"), d.reason);

        quietly(() -> land.restoreSites(Resource.IRON, 20, 20 * Resource.IRON.amountPerSite()));
        assertTrue("on a field of twenty the works of twenty fits, one and not two",
                g.hasDepositFor(pit, 1) && !g.hasDepositFor(pit, 2) && mining.fits(pit));
        BusinessInvestment plans = g.getBusinessInvestment();
        double pitScore = mining.estimatedMonthlyProfit(pit, plans) / (plans.getCostOf(pit, 1) + mining.fleetCapital(pit));
        double mineScore = mining.estimatedMonthlyProfit(mine, plans) / (plans.getCostOf(mine, 1) + mining.fleetCapital(mine));
        BusinessInvestment.Decision both = mining.plan(plans, g);
        report("...and the plan orders the one of the two that earns more on its whole cost, its freight in it",
                both.build && both.template == (pitScore > mineScore ? pit : mine),
                String.format("%s; the pit %.3f%%, the Iron Mine %.3f%% a month", both.reason, 100 * pitScore, 100 * mineScore));
        Map<BuildingsTemplate, Long> run = new LinkedHashMap<>();
        run.put(mine, 1L);
        run.put(pit, 1L);
        Map<BuildingsTemplate, Long> pitFirst = new LinkedHashMap<>();
        pitFirst.put(pit, 1L);
        pitFirst.put(mine, 1L);
        assertTrue("a run counts the sites its orders take, not the orders: an Iron Mine ahead leaves the pit nineteen, the pit ahead"
                        + " leaves the mine none", g.buildRunAhead(run) == 1 && g.buildRunStop(run) == Game.BuildResult.NO_DEPOSIT
                        && g.buildRunAhead(pitFirst) == 1 && g.buildRunStop(pitFirst) == Game.BuildResult.NO_DEPOSIT);
        quietly(() -> b.addStack(pit, 1, true));
        assertTrue("with the pit standing its twenty sites are taken: not one Iron Mine more fits, and the sites committed are twenty",
                !g.hasDepositFor(mine, 1) && g.minesCommitted() == 20 && g.committedOn(Resource.IRON, hem) == 20
                        && g.claimsOn(Resource.IRON).equals(List.of(new LandManager.Claim(hem, 20, 1))));
        quietly(() -> land.restoreSites(Resource.IRON, 21, 21 * Resource.IRON.amountPerSite()));
        assertTrue("...a twenty-first site takes one more Iron Mine, and still no magnetite works",
                g.hasDepositFor(mine, 1) && !g.hasDepositFor(mine, 2) && !g.hasDepositFor(magnetite, 1));
    }

    /**
     * A fixture city made ready to plan before its next month is played:
     * workers handed to it as InvestCheck hands a shop's - twenty thousand,
     * with diplomas, college and university among them - and the investors
     * told the ground it now has free, as the month's Game.refreshLand()
     * tells them, so neither the staff nor a stale figure for the ground is
     * a mine's refusal.
     */
    static void handWorkers(Game g) {
        PopulationManager hands = g.getPopulationManager();
        hands.restoreWorkforce(20_000);
        hands.restoreSkilledHeads(new double[] { 0, 5_000, 2_000, 1_000 });
        g.getBusinessInvestment().setLandAvailable(g.getLandManager().getAvailableSqFt(), g.getLandManager().getPricePerSqFt());
    }

    /** buildStack() quietly, as the Build tab places one. */
    static Game.BuildResult quietlyBuild(Game g, BuildingsTemplate t) {
        Game.BuildResult[] r = new Game.BuildResult[1];
        quietly(() -> r[0] = g.buildStack(t, 1, false));
        return r[0];
    }

    /* ============================ 17. the dig and the retirement by type ============================ */

    static void mineTypes() {
        out.println("\n--- 17. on real fields of both types: what fits, the plan's choice, the dig by type with W exact, and retirement by type ---");
        Game g = new Game(GameFiles.scratch("materialscheck-types"), Founding.defaults().withWorldSeed(MiningCheck.IRON_SEED));
        quietly(g::run);
        final Resource iron = Resource.IRON;
        final Deposit.FieldType hem = Deposit.FieldType.HEMATITE, mag = Deposit.FieldType.MAGNETITE;
        LandManager land = g.getLandManager();
        CityLand city = g.getCityLand();
        quietly(() -> {
            for (int i = 0; i < 300 && (land.getSites(iron, hem) == 0 || land.getSites(iron, mag) == 0); i++) {
                LandParcel offer = land.getMarket().richest(iron);
                if (offer == null) {
                    Deposit f = LandCheck.nearestUnowned(city, iron);
                    if (f == null) break;
                    offer = MiningCheck.nearestOffer(land.getMarket(), f.x(), f.y());
                }
                if (offer == null) break;
                land.buyParcel(offer.getId(), 1e12, 0);
            }
        });
        int fitsHem = 0, fitsMag = 0, bigHem = 0, bigMag = 0;
        for (CityLand.Piece piece : city.typedPieces(iron)) {
            if (piece.type() == hem) { fitsHem += piece.sites() / 20; bigHem = Math.max(bigHem, piece.sites()); }
            if (piece.type() == mag) { fitsMag += piece.sites() / 20; bigMag = Math.max(bigMag, piece.sites()); }
        }
        int hemSites = land.getSites(iron, hem), magSites = land.getSites(iron, mag);
        report("fixture: a city on world " + MiningCheck.IRON_SEED + " that bought iron until it held both types",
                hemSites > 0 && magSites > 0, String.format("%d hematite sites (largest field %d), %d magnetite (largest %d)",
                        hemSites, bigHem, magSites, bigMag));
        BuildingManager b = g.getBuildingManager();
        Mining mining = g.getSectors().mining();
        BuildingsTemplate mine = b.getTemplate(Mining.IRON_MINE), pit = b.getTemplate(Mining.LARGE_OPEN_PIT),
                magnetite = b.getTemplate(Mining.MAGNETITE_MINE), complex = b.getTemplate(Mining.LARGE_MAGNETITE);
        report("as many twenty-site works fit as the fields of their type hold twenty sites, each on one field: the open pits on"
                        + " hematite, the complexes on magnetite",
                (fitsHem == 0 || g.hasDepositFor(pit, fitsHem)) && !g.hasDepositFor(pit, fitsHem + 1)
                        && (fitsMag == 0 || g.hasDepositFor(complex, fitsMag)) && !g.hasDepositFor(complex, fitsMag + 1)
                        && mining.fits(pit) == (bigHem >= 20) && mining.fits(complex) == (bigMag >= 20),
                String.format("%d open pit(s), %d complex(es)", fitsHem, fitsMag));
        assertTrue("...one-site works on every site of their type and not one more; magnetite works never on the hematite's",
                g.hasDepositFor(mine, hemSites) && !g.hasDepositFor(mine, hemSites + 1)
                        && g.hasDepositFor(magnetite, magSites) && !g.hasDepositFor(magnetite, magSites + 1));

        // The plan: the best earnings on the whole cost, its freight in both, among the mines that fit, staff and pay.
        handWorkers(g);
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate expected = null;
        double best = 0;
        StringBuilder seen = new StringBuilder();
        for (BuildingsTemplate t : b.getTemplatesBySector(Sectors.MINING)) {
            double earns = mining.estimatedMonthlyProfit(t, plans), cost = plans.getCostOf(t, 1) + mining.fleetCapital(t);
            boolean fits = mining.fits(t), staffs = mining.staffing(t).passes(),
                    pays = mining.planner().passesMoney(mining, plans, g, earns, cost);
            seen.append(String.format("%n      %-30s fits %-5s staffs %-5s pays %-5s %,.1fk on %,.0fk, %.3f%%", t.getName(), fits, staffs,
                    pays, earns, cost, 100 * earns / cost));
            if (fits && staffs && pays && earns / cost > best) {
                best = earns / cost;
                expected = t;
            }
        }
        BusinessInvestment.Decision d = mining.plan(plans, g);
        report("the plan's order is the best earnings on the whole cost, freight in, of the mines that fit, staff and pay",
                expected != null && d.build && d.template == expected, d.reason + seen);
        double vans = ProcessingSector.vansFor(pit);
        SpreadPlanner.Values here = SpreadPlanner.values(g.getMarkets());
        assertTrue("...its freight in what it earns and costs: the open pit's vans carry its goods alone, the diesel it burns and not its ore"
                        + " (bulk, Haulage's trucks'), and are all of its capital; its freight is their running and the ore's city leg at the quote",
                Math.abs(vans * Sector.TONNES_PER_VAN - pit.uses(Good.DIESEL) * Good.DIESEL.tonnesPerUnit()) < 1e-6
                        && mining.fleetCapital(pit) > 0 && mining.fleetCapital(pit) == ProcessingSector.fleetCapital(pit, here)
                        && ProcessingSector.cityLegs(pit, 1, mining.legPrice()) > 0
                        && mining.freightRunning(pit, 1) == ProcessingSector.fleetRunning(pit, here, 1)
                        + ProcessingSector.cityLegs(pit, 1, mining.legPrice()));

        // The dig by type: two Iron Mines and two magnetite works standing.
        quietly(() -> {
            b.addStack(mine, 2, true);
            b.addStack(magnetite, 2, true);
        });
        double[] before = land.getExtractedByType(iron);
        double eBefore = land.getExtracted(iron);
        mining.produceFlow(Good.IRON);
        Sector.Output ore = mining.outputRow(Good.IRON);
        double asked = ore.planned, hemPart = asked * (2 * mine.makes(Good.IRON)) / mining.getCapacity(Good.IRON);
        double magPart = asked * (2 * magnetite.makes(Good.IRON)) / mining.getCapacity(Good.IRON), rock = magPart * magnetite.site().dig();
        double[] after = land.getExtractedByType(iron);
        int h = iron.typesOf().indexOf(hem), m = iron.typesOf().indexOf(mag);
        report("fixture: two Iron Mines and two magnetite works standing, a month's ore asked of them at the sector's rate",
                asked > 0 && magnetite.site().dig() == 3, String.format("%,.0f t asked", asked));
        report("each type's share of the ask comes out of its own ground: the hematite as dug, the magnetite three tonnes of rock a tonne",
                Math.abs(after[h] - before[h] - hemPart) < 1e-6 && Math.abs(after[m] - before[m] - rock) < 1e-6
                        && Math.abs(ore.produced - (hemPart + magPart)) < 1e-6,
                String.format("hematite %,.0f t, magnetite %,.0f t of rock for %,.0f t; %,.0f t made", after[h] - before[h],
                        after[m] - before[m], magPart, ore.produced));
        assertTrue("...E rising by the ground taken out, the land office's month the same tonnes, and W by type to the tonne",
                Math.abs(land.getExtracted(iron) - eBefore - (hemPart + rock)) < 1e-6
                        && Math.abs(land.getIronMinedThisMonth() - (hemPart + rock)) < 1e-6 && LandCheck.wByType(land));
        assertTrue("...and from the first dig by type the save carries E by type", land.getDepletionByTypeState() != null
                && java.util.Arrays.equals(land.getDepletionByTypeState()[iron.ordinal()], land.getExtractedByType(iron)));

        // The retirement by type.
        assertTrue("while both types have ore no mine is spare, and any may be sold",
                mining.retirementDemandAndCapacity(g) == null && mining.mayRetire(mine) && mining.mayRetire(magnetite));
        double hemLeft = land.getRemaining(iron, hem);
        land.extract(iron, hem, hemLeft);
        double[] spare = mining.retirementDemandAndCapacity(g);
        report("once the hematite is out the Iron Mines are spare - nothing used against their nameplate - while the magnetite has ore",
                land.getRemaining(iron, hem) == 0 && land.getRemaining(iron, mag) > 0 && spare != null && spare[0] == 0
                        && spare[1] == 2 * mine.makes(Good.IRON), String.format("%,.0f t taken to the end", hemLeft));
        assertTrue("...and only they may be sold, by the spare-capacity rule and the distress rule alike",
                mining.mayRetire(mine) && !mining.mayRetire(magnetite));
        assertTrue("...no hematite works is ordered on hematite sites with no ore under them, a magnetite one still may be",
                !g.hasDepositFor(mine, 1) && !mining.fits(mine) && !mining.fits(pit) && g.hasDepositFor(magnetite, 1) == (magSites > 2)
                        && LandCheck.wByType(land));
        mining.produceFlow(Good.IRON);
        Sector.Output dug = mining.outputRow(Good.IRON);
        assertTrue("...and the next month's dig gives the hematite mines nothing and the magnetite works their share",
                Math.abs(dug.produced - dug.planned * (2 * magnetite.makes(Good.IRON)) / mining.getCapacity(Good.IRON)) < 1e-6
                        && land.getRemaining(iron, hem) == 0 && LandCheck.wByType(land));
    }

    /* ============================ 18. a kind's year (CY1) ============================ */

    /** What FC2's seed 1 appraised one more Rod Mill at, a month at a time (D$k): +54k, -83k and -33k, over and over, against a hurdle of 45k. */
    static final double[] FC2_CYCLE = { 54, -83, -33 };

    static void trailingYear() {
        out.println("\n--- 18. a kind's year (CY1): a month noted once, a year kept, the lesser of the two, the count idle over its year, and a save's round trip ---");
        final int n = SpreadPlanner.TRAILING_MONTHS;

        // THE YEAR, PURE.
        SpreadPlanner p = new SpreadPlanner();
        p.noteMargin("A", 1, 10);
        p.noteMargin("A", 1, 30);
        assertTrue("a second note in the same month replaces the first: one month kept, its mean the second's",
                p.marginMonths("A") == 1 && p.marginOverYear("A") == 30);
        p.noteMargin("A", 2, 50);
        assertTrue("...and a note in the next month is a second month: the mean of both", p.marginMonths("A") == 2 && p.marginOverYear("A") == 40);
        p.noteMargin("A", 3, Double.NaN);
        p.noteMargin("A", 3, Double.POSITIVE_INFINITY);
        assertTrue("a figure that is not finite is not noted", p.marginMonths("A") == 2 && p.marginOverYear("A") == 40);
        assertTrue("a kind never noted reads no months and no mean", p.marginMonths("none") == 0 && Double.isNaN(p.marginOverYear("none"))
                && Double.isNaN(p.lastOverYear("none")));
        SpreadPlanner q = new SpreadPlanner();
        int months = n + 3;
        double all = 0, lastYear = 0;
        boolean rises = true;
        for (int m = 1; m <= months; m++) {
            double figure = 100 + 7.0 * m * m;
            q.noteMargin("A", m, figure);
            rises &= q.marginMonths("A") == Math.min(m, n);
            all += figure;
            if (m > months - n) lastYear += figure;
        }
        all /= months;
        lastYear /= n;
        report("TRAILING_MONTHS (" + n + ") + 3 months noted keep the last TRAILING_MONTHS, and the mean is theirs, not all " + months + "'s",
                q.marginMonths("A") == n && near(q.marginOverYear("A"), lastYear) && !near(q.marginOverYear("A"), all),
                String.format("%d kept; mean %,.3fk, the last %d months' %,.3fk, all %d's %,.3fk", q.marginMonths("A"), q.marginOverYear("A"), n, lastYear,
                        months, all));
        assertTrue("...the months kept rise by one a month to TRAILING_MONTHS and no further", rises);
        assertTrue("lesser() is the month's where the year is not a number, and else the lower of the two",
                SpreadPlanner.lesser(5, Double.NaN) == 5 && SpreadPlanner.lesser(-5, Double.NaN) == -5 && SpreadPlanner.lesser(5, 3) == 3
                        && SpreadPlanner.lesser(3, 5) == 3 && SpreadPlanner.lesser(-2, -2) == -2);

        // THE COUNT OVER ITS YEAR: FC2's cycle of the last one's earnings.
        SpreadPlanner year = new SpreadPlanner(), mains = new SpreadPlanner();
        int mainsMax = 0, reachedAt = -1, peaksCounted = 0, countedAt = -1;
        boolean everyMonth = true;
        for (int m = 1; m <= 3 * SpreadPlanner.IDLE_MONTHS; m++) {
            double earns = FC2_CYCLE[(m - 1) % FC2_CYCLE.length];
            int before = year.idleMonths("rod");
            year.noteMonth("rod", true, m, earns);
            mains.noteMonth("rod", true, earns < 0);
            mainsMax = Math.max(mainsMax, mains.idleMonths("rod"));
            if (m >= 2) everyMonth &= year.idleMonths("rod") == before + 1;
            if (m >= 2 && earns > 0 && year.idleMonths("rod") == before + 1) peaksCounted++;
            if (countedAt < 0 && year.idleMonths("rod") >= SpreadPlanner.IDLE_MONTHS) countedAt = m;
            if (reachedAt < 0 && year.mayShed("rod")) reachedAt = m;
        }
        // REV-CY1: a paying month sells none, so the kind is shed from the first losing month the count has reached IDLE_MONTHS by.
        int losingFrom = countedAt;
        while (losingFrom > 0 && FC2_CYCLE[(losingFrom - 1) % FC2_CYCLE.length] >= 0) losingFrom++;
        report("premise: on FC2's figures (+54k, -83k, -33k) the count that a working month starts again (noteMonth(kind, standing, idle)) never reaches"
                        + " IDLE_MONTHS (" + SpreadPlanner.IDLE_MONTHS + ")",
                mainsMax > 0 && mainsMax < SpreadPlanner.IDLE_MONTHS && !mains.mayShed("rod"),
                String.format("its count reached %d", mainsMax));
        report("CY1: the same figures count every month idle from the second - the working months too, the year's mean below nothing - and the kind may be shed from the first"
                        + " losing month its count has reached IDLE_MONTHS by (REV-CY1: a paying month sells none)",
                everyMonth && peaksCounted > 0 && countedAt > 0 && countedAt <= SpreadPlanner.IDLE_MONTHS + 1 && reachedAt == losingFrom && year.mayShed("rod"),
                String.format("the count reached %d at month %d, mayShed from month %d; %d working months counted; mean over its year %,.1fk", SpreadPlanner.IDLE_MONTHS,
                        countedAt, reachedAt, peaksCounted, year.lastOverYear("rod")));
        SpreadPlanner one = new SpreadPlanner();
        for (int m = 1; m < n; m++) one.noteMonth("rod", true, m, 50);
        int working = one.idleMonths("rod");
        one.noteMonth("rod", true, n, -10);
        int lost = one.idleMonths("rod");
        boolean shed = one.mayShed("rod");
        one.noteMonth("rod", true, n + 1, 50);
        report("one losing month in a year of paying ones counts one idle month, and the next paying month starts the count again",
                working == 0 && lost == 1 && !shed && one.idleMonths("rod") == 0,
                String.format("%d, then %d, then %d", working, lost, one.idleMonths("rod")));

        // SOLD ONLY IN A MONTH IT LOSES IN AND OVER ITS YEAR (REV-CY1): a paying month, though the year's mean lags it, sells none.
        final int idleMonths = SpreadPlanner.IDLE_MONTHS;
        SpreadPlanner lag = new SpreadPlanner();
        for (int m = 1; m <= idleMonths; m++) lag.noteMonth("rod", 3.0, m, -100);
        boolean bothLose = lag.mayShed("rod");
        int atIdle = lag.idleMonths("rod");
        lag.noteMonth("rod", 3.0, idleMonths + 1, 20);
        report("a kind idle IDLE_MONTHS (" + idleMonths + ") running, its last month and its year both losing, may be shed; and the month after, the last one paying while the year's mean"
                        + " still lags below nothing, it may not, though its count is past IDLE_MONTHS",
                bothLose && atIdle == idleMonths && lag.idleMonths("rod") == idleMonths + 1 && lag.lastOverYear("rod") < 0 && !lag.mayShed("rod"),
                String.format("count %d, may shed %s; then count %d, year D$%,.1fk, may shed %s", atIdle, bothLose, lag.idleMonths("rod"), lag.lastOverYear("rod"),
                        lag.mayShed("rod")));
        lag.noteMonth("rod", 3.0, idleMonths + 2, -30);
        assertTrue("...and the month after that, losing again with the year below nothing, it may be shed again", lag.mayShed("rod") && lag.idleMonths("rod") == idleMonths + 2);
        SpreadPlanner rich = new SpreadPlanner();
        rich.noteMonth("rod", 3.0, 1, 1_000);
        for (int m = 2; m <= idleMonths + 1; m++) rich.noteMonth("rod", 3.0, m, -10);
        report("a kind idle IDLE_MONTHS running whose last month loses but whose year pays (the mean above nothing) may not be shed",
                rich.idleMonths("rod") == idleMonths && rich.lastOverYear("rod") > 0 && !rich.mayShed("rod"),
                String.format("count %d, year D$%,.1fk, may shed %s", rich.idleMonths("rod"), rich.lastOverYear("rod"), rich.mayShed("rod")));

        // A HOLDING SMALLER THAN AT THE LAST NOTE (one sold) STARTS THE YEAR AGAIN; an equal, a larger and an unknown one do not.
        final double held = 4;
        double[] prior = new double[n - 1];
        double priorSum = 0;
        for (int m = 1; m < n; m++) priorSum += prior[m - 1] = -50 - m;
        double[] holdings = { held - 1, held, held + 1, Double.NaN };
        String[] names = { "smaller", "equal", "larger", "unknown (NaN)" };
        boolean cleared = true, kept = true;
        StringBuilder holdingsSeen = new StringBuilder();
        for (int i = 0; i < holdings.length; i++) {
            SpreadPlanner h = new SpreadPlanner();
            for (int m = 1; m < n; m++) h.noteMonth("rod", held, m, prior[m - 1]);
            h.noteMonth("rod", holdings[i], n, -9);
            double expectAll = (priorSum - 9) / n;
            if (i == 0) cleared = h.lastOverYear("rod") == -9 && h.idleMonths("rod") == n && h.mayShed("rod");
            else kept &= near(h.lastOverYear("rod"), expectAll) && h.idleMonths("rod") == n;
            holdingsSeen.append(String.format("%s: year D$%,.2fk; ", names[i], h.lastOverYear("rod")));
        }
        report("a holding smaller than at the kind's last note starts its year again (the new figure alone), its count going on; an equal, a larger and an unknown (NaN) holding keep the year",
                cleared && kept, holdingsSeen.toString());
        SpreadPlanner paid = new SpreadPlanner();
        for (int m = 1; m < n; m++) paid.noteMonth("rod", held, m, prior[m - 1]);
        int idleBefore = paid.idleMonths("rod");
        paid.noteMonth("rod", held - 1, n, 30);
        assertTrue("...a smaller holding and a paying month: the year is that month alone and the count starts again, as it would not on the year that lagged",
                idleBefore == n - 1 && paid.lastOverYear("rod") == 30 && paid.idleMonths("rod") == 0 && !paid.mayShed("rod"));
        // ...and the holding survives a save: a smaller one after the restore still clears the year, an equal one does not.
        SpreadPlanner kept1 = new SpreadPlanner(), cleared1 = new SpreadPlanner(), saved = new SpreadPlanner();
        for (int m = 1; m < n; m++) saved.noteMonth("rod", held, m, prior[m - 1]);
        Map<String, Double> held1 = new LinkedHashMap<>();
        saved.saveMargins(held1);
        kept1.restoreMargins(held1);
        cleared1.restoreMargins(held1);
        kept1.noteMonth("rod", held, n, 30);
        cleared1.noteMonth("rod", held - 1, n, 30);
        report("the holding survives saveMargins/restoreMargins (HOLDING_KEY): a smaller holding noted after the restore starts the year again, an equal one does not",
                held1.get(SpreadPlanner.HOLDING_KEY + "rod") != null && held1.get(SpreadPlanner.HOLDING_KEY + "rod") == held && cleared1.lastOverYear("rod") == 30
                        && near(kept1.lastOverYear("rod"), (priorSum + 30) / n),
                String.format("saved holding %s; after the restore a holding of %.0f: year D$%,.2fk, of %.0f: D$%,.2fk", held1.get(SpreadPlanner.HOLDING_KEY + "rod"), held - 1,
                        cleared1.lastOverYear("rod"), held, kept1.lastOverYear("rod")));
        year.noteMonth("rod", false, 100, Double.NaN);
        assertTrue("none standing clears the kind's count and its year", year.idleMonths("rod") == 0 && !year.mayShed("rod")
                && Double.isNaN(year.lastOverYear("rod")));
        year.noteMonth("rod", true, 101, 20);
        assertTrue("...and the next one standing starts a year of its own: its first month alone, not idle",
                year.lastOverYear("rod") == 20 && year.idleMonths("rod") == 0);
        SpreadPlanner blank = new SpreadPlanner();
        blank.noteMonth("rod", true, 1, -5);
        blank.noteMonth("rod", true, 2, -5);
        int before = blank.idleMonths("rod");
        blank.noteMonth("rod", true, 3, Double.NaN);
        assertTrue("a figure that is not a number counts as a working month (it did) and is not kept in the year",
                before == 2 && blank.idleMonths("rod") == 0 && blank.lastOverYear("rod") == -5);

        // THE SAVE: each kind's year and its mean, exactly.
        SpreadPlanner a = new SpreadPlanner();
        for (int m = 1; m <= n + 2; m++) {
            a.noteMargin("108", m, 40 + 13.37 * m);
            a.noteMargin("109", m, -250 + 3.1 * m * m);
            a.noteMonth("108", true, m, 17.3 - 2.9 * m);
            a.noteMonth("109", true, m, 61.1 + 0.37 * m);
        }
        Map<String, Double> extras = new LinkedHashMap<>();
        a.saveMargins(extras);
        SpreadPlanner b = new SpreadPlanner();
        b.noteMargin("old", 1, 9);
        b.restoreMargins(extras);
        boolean same = true;
        StringBuilder seen = new StringBuilder();
        for (String kind : new String[] { "108", "109" }) {
            same &= b.marginOverYear(kind) == a.marginOverYear(kind) && b.marginMonths(kind) == a.marginMonths(kind) && a.marginMonths(kind) == n
                    && b.lastOverYear(kind) == a.lastOverYear(kind) && !Double.isNaN(a.lastOverYear(kind));
            seen.append(String.format("%s: %d months, mean %,.6fk, last one's %,.6fk; ", kind, b.marginMonths(kind), b.marginOverYear(kind),
                    b.lastOverYear(kind)));
        }
        report("a save carries each kind's year and what its last one earned, and the planner read back has the same means to the bit",
                same && extras.containsKey(SpreadPlanner.MARGIN_KEY + "108." + (n - 1)) && !extras.containsKey(SpreadPlanner.MARGIN_KEY + "108." + n)
                        && extras.containsKey(SpreadPlanner.LAST_KEY + "109.0"),
                seen.toString());
        assertTrue("...and what it held before the restore is gone", b.marginMonths("old") == 0 && Double.isNaN(b.marginOverYear("old")));
        Map<String, Double> bad = new LinkedHashMap<>();
        bad.put(SpreadPlanner.MARGIN_KEY + "K.x", 1.0);
        bad.put(SpreadPlanner.MARGIN_KEY + "K." + n, 2.0);
        bad.put(SpreadPlanner.MARGIN_KEY + "K.-1", 3.0);
        bad.put(SpreadPlanner.MARGIN_KEY + ".0", 4.0);
        bad.put(SpreadPlanner.MARGIN_KEY + "K", 5.0);
        bad.put(SpreadPlanner.LAST_KEY + "K.", 6.0);
        bad.put(SpreadPlanner.MARGIN_KEY + "K.0", Double.NaN);
        bad.put("other.K.0", 7.0);
        SpreadPlanner c = new SpreadPlanner();
        c.restoreMargins(bad);
        boolean none = c.marginMonths("K") == 0 && Double.isNaN(c.marginOverYear("K")) && Double.isNaN(c.lastOverYear("K"));
        bad.put(SpreadPlanner.MARGIN_KEY + "K.1", 8.0);
        c.restoreMargins(bad);
        assertTrue("a key with an age that is no number, none or past the year, one with no kind or no age, a figure that is not finite and a key of"
                        + " another prefix are ignored; a good key beside them is read",
                none && c.marginMonths("K") == 1 && c.marginOverYear("K") == 8 && Double.isNaN(c.lastOverYear("K")));
        c.restoreMargins(Map.of());
        boolean empty = c.marginMonths("K") == 0 && Double.isNaN(c.marginOverYear("K"));
        c.noteMargin("K", 1, 5);
        c.restoreMargins(null);
        assertTrue("a map without the keys (a save from before CY1), or none, reads no months and no mean",
                empty && c.marginMonths("K") == 0 && Double.isNaN(c.marginOverYear("K")) && Double.isNaN(c.lastOverYear("K")));
    }

    /* ============================ the copper town the next sections play ============================ */

    /** A copper town on the literals with one Electrolytic Refinery standing (the cathode a Rod Mill runs on) and `mills` Rod Mills, the hands hired, two months settled. */
    static Game rodTown(String label, int mills) {
        return CopperRefiningCheck.copperWorks(label, 0, 1, mills, Double.NaN);
    }

    /** One real month with rod's world prices paying a mill or not (CopperRefiningCheck.rodPays(), rodUnpaid()), the hands hired first so the staffing gate refuses no one. */
    static void rodMonth(Game g, boolean pays) {
        if (pays) CopperRefiningCheck.rodPays(g);
        else CopperRefiningCheck.rodUnpaid(g);
        handWorkers(g);
        quietly(() -> g.simulateMonths(1));
    }

    /** ...with the planner's year emptied first, so that month's plan weighs the month alone: the town on main's rule (to CY1). */
    static void monthAlone(Game g, boolean pays) {
        g.getSectors().copperRefining().planner().restoreMargins(Map.of());
        rodMonth(g, pays);
    }

    /** Rod Mills standing and under construction. */
    static long rodMills(Game g) {
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate rod = b.getTemplate(CopperRefining.ROD_MILL);
        return b.getQuantity(rod.getId()) + b.underConstructionByName(rod.getName());
    }

    /** What Copper Refining owes, loans and bonds. */
    static double refiningDebt(Game g) {
        return g.getEconomyManager().getBusinessDebtManager().getPrincipal(Sectors.COPPER_REFINING);
    }

    /** The candidate for one template in an appraisal; null for none. */
    static SpreadPlanner.Candidate candidate(List<SpreadPlanner.Candidate> all, BuildingsTemplate t) {
        for (SpreadPlanner.Candidate c : all) if (c.template().getId() == t.getId()) return c;
        return null;
    }

    /* ============================ 19. a one-month spike orders no works (CY1) ============================ */

    static void spikeMonth() {
        out.println("\n--- 19. a one-month spike orders no works (CY1): the month alone passes, its year does not, and the order waits for the year ---");
        final int n = SpreadPlanner.TRAILING_MONTHS;
        // The city plays its own plan() each month; its twin plays on main's rule (the year emptied before each month); the third is held, and the check asks
        // its planner once a month, after the month, with the hands hired - the premise and the words, on a state the plan would have read.
        Game g = rodTown("materialscheck-spike", 0), twin = rodTown("materialscheck-spike-twin", 0), h = rodTown("materialscheck-spike-held", 0);
        CopperRefining hr = h.getSectors().copperRefining();
        BuildingsTemplate rod = g.getBuildingManager().getTemplate(CopperRefining.ROD_MILL);
        String kind = ProcessingSector.kindOf(rod);
        BusinessInvestment hp = h.getBusinessInvestment();
        hp.holdSector(Sectors.COPPER_REFINING);
        boolean calm = true;
        double worst = Double.NEGATIVE_INFINITY, lowestHurdle = Double.POSITIVE_INFINITY;
        for (int k = 1; k < n; k++) {
            rodMonth(g, false);
            monthAlone(twin, false);
            rodMonth(h, false);
            handWorkers(h);
            SpreadPlanner.Candidate c = candidate(hr.appraise(hp, h), rod);
            BusinessInvestment.Decision d = hr.plan(hp, h);
            double hurdle = hr.planner().hurdle(hr, hp, h, rod, 1, c.cost());
            calm &= rodMills(g) == 0 && rodMills(twin) == 0 && !d.build && !c.passes() && c.refusal(SpreadPlanner.Gate.MONEY) != null && c.earns() < hurdle;
            worst = Math.max(worst, c.earns());
            lowestHurdle = Math.min(lowestHurdle, hurdle);
        }
        report("fixture: rod's export price at half the literal for TRAILING_MONTHS - 1 (" + (n - 1) + ") months: the month alone is under the money gate every month, and"
                        + " neither the city nor its twin on main's rule orders a Rod Mill",
                calm, String.format("a Rod Mill's best month earns D$%,.1fk against a hurdle of D$%,.1fk or more", worst, lowestHurdle));

        // THE SPIKE MONTH: rod pays for one month.
        rodMonth(g, true);
        monthAlone(twin, true);
        rodMonth(h, true);
        handWorkers(h);
        SpreadPlanner.Candidate c = candidate(hr.appraise(hp, h), rod);
        SpreadPlanner.Candidate best = SpreadPlanner.best(hr.appraise(hp, h));
        double hurdle = hr.planner().hurdle(hr, hp, h, rod, 1, c.cost());
        BusinessInvestment.Decision d = hr.plan(hp, h);
        double year = hr.planner().marginOverYear(kind);
        report("premise: in the spike month the month alone passes every gate - the planner's best is a Rod Mill - and the town on main's rule orders one",
                best != null && best.template().getId() == rod.getId() && c.passes() && c.earns() > hurdle && rodMills(twin) > 0,
                String.format("the month earns D$%,.1fk against a hurdle of D$%,.1fk; its twin has %d Rod Mills", c.earns(), hurdle, rodMills(twin)));
        report("CY1: its year's mean is under the hurdle, so the plan orders no Rod Mill, and its words give the year: its mean over its last months against the month's",
                year < hurdle && !(d.build && d.template.getId() == rod.getId()) && d.reason != null && d.reason.contains("its mean over its last " + hr.planner().marginMonths(kind)
                        + " months"),
                String.format("mean D$%,.1fk over %d months against D$%,.1fk; \"%s\"", year, hr.planner().marginMonths(kind), hurdle, d.reason));
        report("...and the city orders none that month, as it had none before", rodMills(g) == 0 && refiningDebt(g) <= refiningDebt(twin),
                String.format("%d Rod Mills; debt D$%,.0fk against its twin's D$%,.0fk", rodMills(g), refiningDebt(g), refiningDebt(twin)));

        // THE OTHER SIDE: rod keeps paying, and the year comes to pass.
        int orderedAt = 0, plansAt = 0, waited = 0, passesAlone = 1;
        boolean underWhileWaiting = year < hurdle;
        for (int k = 2; k <= n; k++) {
            rodMonth(g, true);
            if (orderedAt == 0 && rodMills(g) > 0) orderedAt = k;
            if (plansAt == 0) {
                rodMonth(h, true);
                handWorkers(h);
                c = candidate(hr.appraise(hp, h), rod);
                hurdle = hr.planner().hurdle(hr, hp, h, rod, 1, c.cost());
                d = hr.plan(hp, h);
                year = hr.planner().marginOverYear(kind);
                if (d.build && d.template.getId() == rod.getId()) plansAt = k;
                else {
                    waited++;
                    if (c.passes()) passesAlone++;
                    underWhileWaiting &= year < hurdle;
                }
            }
        }
        report("the order waits for the year: with rod paying on, the city orders its first Rod Mill after the spike month and within TRAILING_MONTHS (" + n + ") paying months",
                orderedAt > 1 && orderedAt <= n && rodMills(g) > 0, String.format("ordered in paying month %d of %d; %d Rod Mills, debt D$%,.0fk", orderedAt, n,
                        rodMills(g), refiningDebt(g)));
        report("...and the held planner agrees: until the month it plans one, every paying month passes alone and its year's mean sits under the hurdle",
                plansAt > 1 && plansAt <= n && passesAlone == waited + 1 && underWhileWaiting,
                String.format("plans one in paying month %d; %d months it declined, each passing alone and with its year under the hurdle", plansAt, waited + 1));
    }

    /* ============================ 20. a three-month cycle whose year does not pay builds nothing at its peaks (CY1) ============================ */

    static void threeMonthCycle() {
        out.println("\n--- 20. a three-month cycle whose year does not pay builds nothing at its peaks, so its troughs strand no works (CY1) ---");
        final int n = SpreadPlanner.TRAILING_MONTHS;
        Game g = rodTown("materialscheck-cycle", 0), twin = rodTown("materialscheck-cycle-twin", 0), h = rodTown("materialscheck-cycle-held", 0);
        CopperRefining hr = h.getSectors().copperRefining(), tr = twin.getSectors().copperRefining();
        BuildingsTemplate rod = g.getBuildingManager().getTemplate(CopperRefining.ROD_MILL), trod = twin.getBuildingManager().getTemplate(CopperRefining.ROD_MILL);
        String kind = ProcessingSector.kindOf(rod);
        BusinessInvestment hp = h.getBusinessInvestment();
        hp.holdSector(Sectors.COPPER_REFINING);
        // ...a year of the trough first, so the cycle is read on a full year (a short one reads the months it has).
        for (int k = 1; k <= n; k++) {
            rodMonth(g, false);
            monthAlone(twin, false);
            rodMonth(h, false);
        }
        int peaks = 0, passAlone = 0, yearUnder = 0, declinedAtPass = 0, mostMills = 0, firstPeak = 0;
        boolean everBuilt = false;
        double firstMonth = Double.NaN, firstYear = Double.NaN, firstHurdle = Double.NaN, stranded = Double.NaN;
        for (int k = 1; k <= 2 * n; k++) {
            boolean peak = k % 3 == 0;
            rodMonth(g, peak);
            monthAlone(twin, peak);
            rodMonth(h, peak);
            handWorkers(h);
            SpreadPlanner.Candidate c = candidate(hr.appraise(hp, h), rod);
            BusinessInvestment.Decision d = hr.plan(hp, h);
            mostMills = (int) Math.max(mostMills, rodMills(g));
            everBuilt |= d.build;
            if (peak) {
                double hurdle = hr.planner().hurdle(hr, hp, h, rod, 1, c.cost()), year = hr.planner().marginOverYear(kind);
                peaks++;
                if (year < hurdle) yearUnder++;
                if (c.passes() && c.earns() > hurdle) {
                    if (passAlone++ == 0) {
                        firstPeak = peaks;
                        firstMonth = c.earns();
                        firstYear = year;
                        firstHurdle = hurdle;
                    }
                    if (!d.build) declinedAtPass++;
                }
            } else {
                stranded = tr.lastEarns(trod);
            }
        }
        report("premise: at a peak the month alone passes every gate and clears the hurdle (the peaks do not all: the town's tallies move as the cycle runs), and at every peak the year's mean sits under it",
                peaks == 2 * n / 3 && passAlone > 0 && yearUnder == peaks,
                String.format("%d peaks, %d passing alone, %d with the year under the hurdle; the first passing peak (%d): the month D$%,.1fk, its year's mean D$%,.1fk, the hurdle D$%,.1fk",
                        peaks, passAlone, yearUnder, firstPeak, firstMonth, firstYear, firstHurdle));
        report("premise: on main's rule the town orders Rod Mills at the peaks, and they stand losing in a trough",
                rodMills(twin) > 0 && stranded < 0, String.format("%d Rod Mills, the last one earning D$%,.1fk a month in the last trough", rodMills(twin), stranded));
        report("CY1: the plan builds nothing at any month of the cycle, at the peaks the month alone passes least of all",
                !everBuilt && declinedAtPass == passAlone, String.format("%d of %d peaks that passed alone declined; no build in %d months", declinedAtPass, passAlone, 2 * n));
        report("...so the city orders no Rod Mill over " + (2 * n) + " months of the cycle and its debt is not raised for one: it is under its twin's, which is",
                rodMills(g) == 0 && mostMills == 0 && refiningDebt(g) < refiningDebt(twin),
                String.format("%d Rod Mills in the city, %d in its twin; debt D$%,.0fk against D$%,.0fk", rodMills(g), rodMills(twin), refiningDebt(g), refiningDebt(twin)));
    }

    /* ============================ 21. a feed-short works orders no mill (CY1) ============================ */

    static void feedShortWorks() {
        out.println("\n--- 21. a feed-short works orders no mill (CY1): its works that would run under FEED_GATE of nameplate on last month's make are refused one more ---");
        // (a) the short works: three Rod Mills on imported cathode, rod's export price half the literal, a Wire & Cable Works beside them to ask for some: they make
        // what the wire works asks for and idle the rest of the nameplate.
        Game g = CopperRefiningCheck.copperWorks("materialscheck-feedshort", 0, 0, 3, Double.NaN);
        CopperRefining cr = g.getSectors().copperRefining();
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate rod = b.getTemplate(CopperRefining.ROD_MILL), smelter = b.getTemplate(CopperRefining.SMELTER);
        BusinessInvestment plans = g.getBusinessInvestment();
        plans.holdSector(Sectors.COPPER_REFINING);   // the works stand as the fixture put them: the subject is the gate, not the planner's orders
        quietly(() -> b.addStack(b.getTemplate(Manufacturing.WIRE_CABLE_WORKS), 1, true));
        for (int k = 0; k < 4; k++) rodMonth(g, false);
        Sector.Output ro = cr.outputRow(Good.COPPER_ROD);
        double made = ro == null ? 0 : ro.produced + ro.exportBound + ro.captive, full = ro == null ? 0 : made + ro.idled;
        double now = cr.getCapacity(Good.COPPER_ROD);   // none on site: the fixture's check below
        double expect = ro == null ? Double.NaN : Math.min(1, made / full * ro.capacity / now);
        double run = cr.standingRun(rod);
        report("fixture: three Rod Mills and a wire works, rod unpaid: the mills make some rod and idle most of their nameplate, under FEED_GATE (" + SpreadPlanner.FEED_GATE + ") of it",
                ro != null && made > 0 && ro.idled > made && made / full < SpreadPlanner.FEED_GATE && b.getUnderConstructionBySector(Sectors.COPPER_REFINING) == 0,
                ro == null ? "none" : String.format("made %,.0f t, idled %,.0f t of %,.0f t", made, ro.idled, ro.capacity));
        report("the run the sector reads is the Output row's own, made over made and idled times last month's capacity over today's, and it is under FEED_GATE",
                near(run, expect) && run < SpreadPlanner.FEED_GATE && run > 0, String.format("%.4f (the row's %.4f)", run, expect));
        SpreadPlanner.Candidate c = cr.appraise(rod, plans);
        String words = c.refusal(SpreadPlanner.Gate.FEED);
        report("...so a Rod Mill's first gate is its feed, and its refusal says the works would run at that share of nameplate on last month's make",
                c.failed() == SpreadPlanner.Gate.FEED && words != null && words.contains("of nameplate on last month's make")
                        && words.contains(String.format("%.0f%%", 100 * run)) && words.contains(String.format("%.0f%%", 100 * SpreadPlanner.FEED_GATE)),
                String.valueOf(words));
        SpreadPlanner.Candidate other = cr.appraise(smelter, plans);
        report("a kind none of whose goods' works stand is not refused on it: a smelter's run reads nothing and its feed gate is open",
                Double.isNaN(cr.standingRun(smelter)) && other.refusal(SpreadPlanner.Gate.FEED) == null, String.format("%s", cr.standingRun(smelter)));

        // (a, saved) the run crosses a save (REV-CY1): the month's Output rows are not saved, so the month after a load read none and the gate stood open on a works
        // it should refuse. The whole city is saved to its scratch files (never a save slot of the player's) and read back by a fresh game.
        double savedRun = cr.standingRun(rod);
        quietly(() -> g.saveGame(10, "feedshort"));
        Game back = new Game(g.getGameFiles());
        quietly(() -> back.loadGameSave(10));
        CopperRefining br = back.getSectors().copperRefining();
        BuildingsTemplate backRod = back.getBuildingManager().getTemplate(CopperRefining.ROD_MILL);
        double backRun = br.standingRun(backRod);
        String backWords = br.appraise(backRod, back.getBusinessInvestment()).refusal(SpreadPlanner.Gate.FEED);
        report("premise: the loaded town has read no month yet: its save loaded, no Output row for rod, and the Rod Mills as many as were saved",
                back.getLoadFailure() == null && br.outputRow(Good.COPPER_ROD) == null && back.getBuildingManager().getQuantity(backRod.getId()) == b.getQuantity(rod.getId()),
                String.format("%s; %d Rod Mills", back.getLoadFailure() == null ? "loaded" : back.getLoadFailure(), back.getBuildingManager().getQuantity(backRod.getId())));
        report("CY1 (REV-CY1): the feed gate's run survives a save: before any month runs the loaded town reads the saved town's run, under FEED_GATE, and refuses a Rod Mill on its feed",
                near(backRun, savedRun) && backRun < SpreadPlanner.FEED_GATE && backWords != null && backWords.contains("of nameplate on last month's make")
                        && backWords.contains(String.format("%.0f%%", 100 * backRun)),
                String.format("saved %.6f, loaded %.6f; %s", savedRun, backRun, backWords));

        // (b) the other side: rod paying, the same mills run their whole nameplate.
        Game paid = CopperRefiningCheck.copperWorks("materialscheck-feedpays", 0, 0, 3, Double.NaN);
        CopperRefining pr = paid.getSectors().copperRefining();
        BuildingManager pb = paid.getBuildingManager();
        paid.getBusinessInvestment().holdSector(Sectors.COPPER_REFINING);
        quietly(() -> pb.addStack(pb.getTemplate(Manufacturing.WIRE_CABLE_WORKS), 1, true));
        for (int k = 0; k < 4; k++) rodMonth(paid, true);
        Sector.Output po = pr.outputRow(Good.COPPER_ROD);
        double prun = pr.standingRun(rod);
        SpreadPlanner.Candidate pc = pr.appraise(rod, paid.getBusinessInvestment());
        report("rod paying, the same mills run at or above FEED_GATE of nameplate and their feed gate is open",
                po != null && po.produced + po.exportBound + po.captive > 0 && prun >= SpreadPlanner.FEED_GATE && pc.refusal(SpreadPlanner.Gate.FEED) == null
                        && pc.failed() != SpreadPlanner.Gate.FEED,
                String.format("run %.4f; made %,.0f t, idled %,.0f t", prun, po == null ? 0 : po.produced + po.exportBound + po.captive, po == null ? 0 : po.idled));

        // (c) nothing to read: Rod Mills standing in a town no month has run in since its rows were made.
        Game fresh = CopperMiningCheck.copperTown("materialscheck-norows", 0);
        fresh.playOnLiterals();
        BuildingManager fb = fresh.getBuildingManager();
        quietly(() -> fb.addStack(fb.getTemplate(CopperRefining.ROD_MILL), 3, true));
        CopperRefining fr = fresh.getSectors().copperRefining();
        SpreadPlanner.Candidate fc = fr.appraise(rod, fresh.getBusinessInvestment());
        report("Rod Mills standing in a town with no month run since: no Output row, a run of nothing (NaN) and no refusal on the feed",
                fr.outputRow(Good.COPPER_ROD) == null && Double.isNaN(fr.standingRun(rod)) && fc.refusal(SpreadPlanner.Gate.FEED) == null,
                String.format("%s", fr.standingRun(rod)));
    }

    /* ============================ 22. an idle works is shed over its year (CY1) ============================ */

    /** Rod's world prices, import and export, at `scale` times the literal: a month that pays the last Rod Mill something or loses it more. */
    static void rodAt(Game g, double scale) {
        Good rod = Good.COPPER_ROD;
        g.getMarkets().get(rod).setWorldPrices(rod.worldImportPrice() * scale, rod.worldExportPrice() * scale);
    }

    /** Rod's prices in the trough of the cycle of section 22: half the literal, so the last Rod Mill loses money on every tonne of its feed. */
    static final double TROUGH_SCALE = .5;

    /** ...and in its peak: 1.3 times the literal, which pays the last Rod Mill a little. */
    static final double PEAK_SCALE = 1.3;

    static void idleOverYear() {
        out.println("\n--- 22. an idle works is shed over its year (CY1): a kind whose last one loses two months in three counts them all, and may be sold ---");
        final int idleMonths = SpreadPlanner.IDLE_MONTHS;
        Game g = CopperRefiningCheck.copperWorks("materialscheck-idleyear", 0, 0, 3, Double.NaN);
        CopperRefining cr = g.getSectors().copperRefining();
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate rod = b.getTemplate(CopperRefining.ROD_MILL);
        BusinessInvestment plans = g.getBusinessInvestment();
        String kind = ProcessingSector.kindOf(rod);
        quietly(() -> b.addStack(b.getTemplate(Manufacturing.WIRE_CABLE_WORKS), 1, true));
        int standing = (int) b.getQuantity(rod.getId()), k = 0, mainsCount = 0, mainsMax = 0, peaks = 0, countedOnPeaks = 0, trough = 0;
        boolean steady = true;
        double peakEarns = Double.NaN, troughEarns = Double.NaN;
        // The sector is the city's own, unheld: its plan and its retirement are the real ones.
        while (k < 3 * idleMonths && !(cr.idleBuildings() > 0)) {
            boolean peak = k % 3 == 2;
            rodAt(g, peak ? PEAK_SCALE : TROUGH_SCALE);
            handWorkers(g);
            int before = cr.idleMonths(rod);
            quietly(() -> g.simulateMonths(1));
            double last = cr.lastEarns(rod);
            mainsCount = last < 0 ? mainsCount + 1 : 0;
            mainsMax = Math.max(mainsMax, mainsCount);
            steady &= cr.idleMonths(rod) == before + 1;
            if (last >= 0) {
                peaks++;
                peakEarns = last;
                if (cr.idleMonths(rod) == before + 1) countedOnPeaks++;
            } else {
                trough++;
                troughEarns = last;
            }
            k++;
        }
        report("premise: the last Rod Mill earns something in a peak month (a working month that starts main's count again) and loses in the two between; the count that a working month starts again never reaches IDLE_MONTHS",
                peaks > 0 && trough > 0 && mainsMax > 0 && mainsMax < idleMonths,
                String.format("%d months of the cycle: the peak's D$%,.1fk, the trough's D$%,.1fk; main's count reached %d of %d", k, peakEarns, troughEarns, mainsMax, idleMonths));
        report("CY1: the count rises every month of the cycle, the working months with it, and reaches IDLE_MONTHS (" + idleMonths + ")",
                steady && peaks > 0 && countedOnPeaks == peaks && cr.idleMonths(rod) >= idleMonths && cr.planner().mayShed(kind),
                String.format("%d idle months after %d; the last one's mean over its year D$%,.1fk", cr.idleMonths(rod), k, cr.planner().lastOverYear(kind)));
        double[] measure = cr.retirementDemandAndCapacity(g);
        report("...so the Rod Mill may be sold and the spare-capacity rule has a measure: nothing used against the idle kind's buildings",
                cr.idleBuildings() == standing && cr.mayRetire(rod) && measure != null && measure[0] == 0 && measure[1] == standing,
                measure == null ? "no measure" : String.format("{%,.0f, %,.0f}", measure[0], measure[1]));

        // THE SALE, the cycle played on: the sector is unheld and none of its works is on site, so Game.runRetirement() sells the idle kind
        // on its own year, though the sector's losing streak, which each paying month starts again, is under RETIREMENT_LOSS_MONTHS (CY1).
        while (k % 3 != 1) {
            rodAt(g, k % 3 == 2 ? PEAK_SCALE : TROUGH_SCALE);
            handWorkers(g);
            quietly(() -> g.simulateMonths(1));
            k++;
        }
        int streak = plans.getLossMonths(Sectors.COPPER_REFINING);
        long millsBefore = b.getQuantity(rod.getId()), orders = b.getUnderConstructionBySector(Sectors.COPPER_REFINING);
        double[] now = cr.retirementDemandAndCapacity(g);
        final int idleAtSale = cr.idleMonths(rod);
        final double yearAtSale = cr.planner().lastOverYear(kind);
        BusinessInvestment.Decision d = now == null ? BusinessInvestment.Decision.no(Sectors.COPPER_REFINING, "no idle kind")
                : plans.planRetirement(cr, now[0], now[1], orders);
        rodAt(g, TROUGH_SCALE);
        handWorkers(g);
        quietly(() -> g.simulateMonths(1));
        long millsAfter = b.getQuantity(rod.getId());
        report("premise: the sector's own losing streak is under RETIREMENT_LOSS_MONTHS (" + BusinessInvestment.RETIREMENT_LOSS_MONTHS
                        + "), the wait that held an idle kind before CY1", streak < BusinessInvestment.RETIREMENT_LOSS_MONTHS,
                String.format("%d months", streak));
        report("CY1: the rule sells the idle kind on its own year, and the city's month sells a Rod Mill",
                orders == 0 && d.build && d.template == rod && d.quantity >= 1 && millsAfter < millsBefore,
                String.format("%s (%s); %d Rod Mills before the month, %d after", d.build ? d.template.getName() + " x" + d.quantity : "no", d.reason, millsBefore, millsAfter));
        report("CY1: the sale's reason names the idle kind and its months over its year, not the sector's own \"0 months of losses\"",
                d.build && d.reason.startsWith(String.format("%s idle %d months over its year", rod.getName(), idleAtSale))
                        && d.reason.startsWith(rod.getName() + " idle ") && !d.reason.contains("0 months of losses") && idleAtSale >= idleMonths,
                d.reason);
        // ...and the holding that fell by the sale started the last one's year again (REV-CY1): the month's figure alone, not the seven before it.
        double figureAfter = cr.lastEarns(rod), yearAfter = cr.planner().lastOverYear(kind);
        report("CY1: the sale started the last Rod Mill's year again: the month after it has that month's figure alone for its year, not the year's mean before it",
                millsAfter < millsBefore && near(yearAfter, figureAfter) && !near(yearAfter, yearAtSale),
                String.format("D$%,.1fk over its year after the month's sale, D$%,.1fk before it (the month's figure D$%,.1fk)", yearAfter, yearAtSale, figureAfter));

        idleLagsItsYear(idleMonths);
    }

    /**
     * REV-CY1: the month a kind's last works PAID while its year's mean still lagged below nothing. The count (a month counts idle when it or the year is
     * below nothing) was at IDLE_MONTHS and past it, and with nothing else braking the sale Game.runRetirement() sold ceil 25% of the kind each month on the lag.
     * It cannot come after a sale - one starts the year again, and a month after is a paying figure alone (the count starts again) or a losing one (sold
     * again) - so it is played on the cycle's own first peak at the count, the year nothing but its troughs and that peak.
     */
    static void idleLagsItsYear(int idleMonths) {
        Game g = CopperRefiningCheck.copperWorks("materialscheck-idlelag", 0, 0, 3, Double.NaN);
        CopperRefining cr = g.getSectors().copperRefining();
        BuildingManager b = g.getBuildingManager();
        BuildingsTemplate rod = b.getTemplate(CopperRefining.ROD_MILL);
        String kind = ProcessingSector.kindOf(rod);
        quietly(() -> b.addStack(b.getTemplate(Manufacturing.WIRE_CABLE_WORKS), 1, true));
        final long standing = b.getQuantity(rod.getId());
        int k = 0, lagAt = -1, lagCount = 0;
        double lagLast = Double.NaN, lagYear = Double.NaN;
        boolean lagMayShed = true, sold = false;
        long lagMills = -1;
        double lagIdle = -1;
        double[] measure = null;
        while (k < 3 * idleMonths && lagAt < 0) {
            rodAt(g, k % 3 == 2 ? PEAK_SCALE : TROUGH_SCALE);
            handWorkers(g);
            quietly(() -> g.simulateMonths(1));
            double last = cr.lastEarns(rod);
            sold |= b.getQuantity(rod.getId()) < standing;
            if (last >= 0 && cr.idleMonths(rod) >= idleMonths && cr.planner().lastOverYear(kind) < 0) {
                lagAt = k;
                lagLast = last;
                lagCount = cr.idleMonths(rod);
                lagYear = cr.planner().lastOverYear(kind);
                lagMayShed = cr.planner().mayShed(kind);
                lagMills = b.getQuantity(rod.getId());
                measure = cr.retirementDemandAndCapacity(g);
                lagIdle = cr.idleBuildings();
            }
            k++;
        }
        report("premise: a month in which the last Rod Mill earns something while its idle count is at IDLE_MONTHS (" + idleMonths
                        + ") or past it and its year's mean is still below nothing, and none was sold before it",
                lagAt >= 0 && lagLast >= 0 && lagCount >= idleMonths && lagYear < 0 && !sold && lagMills == standing,
                String.format("month %d of the cycle: the last one earned D$%,.1fk, %d idle months, D$%,.1fk over its year; %d Rod Mills standing", lagAt + 1,
                        lagLast, lagCount, lagYear, lagMills));
        report("CY1 (REV-CY1): ...the kind may not be sold on that note, though its count is at IDLE_MONTHS: the last one paid, so the sector counts no idle works and offers"
                        + " the retirement no measure for it",
                !lagMayShed && lagIdle == 0 && measure == null,
                String.format("mayShed %s, %,.0f idle works, %s", lagMayShed, lagIdle, measure == null ? "no measure" : "a measure"));
        // The next real month: its start runs Game.runRetirement() on that note.
        rodAt(g, k % 3 == 2 ? PEAK_SCALE : TROUGH_SCALE);
        handWorkers(g);
        quietly(() -> g.simulateMonths(1));
        k++;
        long after = b.getQuantity(rod.getId());
        report("CY1 (REV-CY1): the next month's retirement, run on that note, sells none: the Rod Mill count is unchanged across it",
                after == lagMills && b.getUnderConstructionBySector(Sectors.COPPER_REFINING) == 0,
                String.format("%d Rod Mills before the month, %d after", lagMills, after));
        // ...and nothing else was braking it: the last one loses again that month, in it and over its year, and the retirement of the month after sells one.
        boolean mayAgain = cr.planner().mayShed(kind), losing = cr.lastEarns(rod) < 0;
        int idleNow = cr.idleMonths(rod);
        double yearNow = cr.planner().lastOverYear(kind);
        rodAt(g, k % 3 == 2 ? PEAK_SCALE : TROUGH_SCALE);
        handWorkers(g);
        quietly(() -> g.simulateMonths(1));
        long later = b.getQuantity(rod.getId());
        report("...and nothing else was braking it: the month after, the last one losing again in its month and over its year, the rule may shed it and the retirement sells a Rod Mill",
                mayAgain && losing && idleNow >= idleMonths && yearNow < 0 && later < after,
                String.format("%d idle months, D$%,.1fk over its year, mayShed %s; %d Rod Mills before the month, %d after", idleNow, yearNow, mayAgain, after, later));
    }

    /* ============================ 23. the planner weighs a works' own power as it is paid ============================ */

    static void ownPowerPlanned() {
        out.println("\n--- 23. the planner weighs a works' own power as it is paid (PW1-R): what replaces its draw at its class's price, the surplus at the works' price ---");
        GameFiles files = GameFiles.scratch("materialscheck-ownpower");
        Game g = town(files);
        BuildingManager b = g.getBuildingManager();
        quietly(() -> {
            b.addStack(b.getTemplate(HeavyIndustry.COKE_OVENS), 1, true);
            b.addStack(b.getTemplate(HeavyIndustry.BLAST_FURNACE), 1, true);
            b.addStack(b.getTemplate(HeavyIndustry.OXYGEN_STEEL_SHOP), 1, true);
            g.buildStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            // ...four months, as section 14 plays them: the furnace and the shop load the grid past what the ovens make, so a book is struck at a price above nothing.
            g.simulateMonths(4);
        });
        HeavyIndustry heavy = g.getSectors().heavyIndustry();
        EconomyManager econ = g.getEconomyManager();
        BuildingsTemplate ovens = b.getTemplate(HeavyIndustry.COKE_OVENS);
        double gen = ovens.getGen(), draw = ovens.getElectricityConsumption();
        double commerce = econ.getPricePerWatt(), industry = econ.getIndustryPricePerWatt(), works = econ.getWorksPricePerWatt();
        double factor = heavy.exchangeFactor(), rate = .8, tariff = heavy.tariff(), worksTariff = heavy.worksTariff();
        report("fixture: section 14's town, four months on: the Coke Ovens make more power than they draw, and the struck book pays"
                        + " the works' surplus a price apart from what Heavy Industry is billed",
                b.getQuantity(HeavyIndustry.COKE_OVENS) == 1 && gen > draw && draw > 0 && ovens.getConsumables() > 0
                        && industry > 0 && works > 0 && industry != works && tariff == industry && worksTariff == works,
                String.format("%,.0f kW made past a %,.0f kW draw; commerce %.6g, industry %.6g (sector %.6g), works %.6g (sector %.6g) a kW", gen, draw, commerce, industry, tariff, works, worksTariff));

        double own = SpreadPlanner.ownCosts(ovens, rate, factor, tariff, worksTariff);
        double byHand = ovens.getConsumables() * rate * factor - Math.min(gen, draw) * tariff - (gen - draw) * worksTariff;
        report("its own costs are its consumables at the rate and the exchange factor, less its draw replaced at the industry price and its surplus"
                        + " past the draw at the works' price (to 1e-12)",
                Math.abs(own - byHand) <= 1e-12 * Math.abs(byHand) && byHand != SpreadPlanner.ownCosts(ovens, rate, factor, tariff),
                String.format("D$%.6gk a month, not the flat price's D$%.6gk", own, SpreadPlanner.ownCosts(ovens, rate, factor, tariff)));

        // ...the idle book's figure for the works that stand: the works' price moved by a tenth, and the last one earns the surplus times that more.
        double d = .1 * works;
        double lastBefore = heavy.lastEarns(ovens), lastAfter, lastBack;
        try {
            econ.setPowerPrices(commerce, industry, works + d);
            lastAfter = heavy.lastEarns(ovens);
        } finally {
            econ.setPowerPrices(commerce, industry, works);
        }
        lastBack = heavy.lastEarns(ovens);
        report("the last one standing earns what its surplus gains at a works' price a tenth higher, and no more: lastEarns() rises by the surplus"
                        + " times the rise (to 1e-9), and falls back with the price",
                Double.isFinite(lastBefore) && Double.isFinite(lastAfter) && lastBack == lastBefore
                        && Math.abs((lastAfter - lastBefore) - (gen - draw) * d) <= 1e-9 * Math.abs(lastBefore),
                String.format("D$%.6gk a month, then D$%.6gk (+%.4gk of the %.4gk surplus x the rise)", lastBefore, lastAfter,
                        lastAfter - lastBefore, (gen - draw) * d));

        // ...and the industry price moved by the same: it moves what replaces the draw, and not the surplus.
        double ownMoved;
        try {
            econ.setPowerPrices(commerce, industry + d, works);
            ownMoved = SpreadPlanner.ownCosts(ovens, rate, factor, heavy.tariff(), heavy.worksTariff());
        } finally {
            econ.setPowerPrices(commerce, industry, works);
        }
        report("the industry price a tenth higher moves its own-power credit by its draw replaced times the rise alone (to 1e-12): the surplus is not paid the industry price",
                Math.abs((ownMoved - own) - (-Math.min(gen, draw) * d)) <= 1e-12 * Math.max(Math.abs(own), Math.abs(ownMoved)),
                String.format("%.6g against %.6g", ownMoved - own, -Math.min(gen, draw) * d));

        // ...REV-PW1c: the recipes' prices are the class's, as the planner's are. This town has no street wires, so its commerce and industry prices are one:
        // the commerce price is moved a tenth of the works' above the industry's while they are read, so a shop's and the ovens' cannot be mistaken for each other.
        BuildingsTemplate shop = b.getTemplateByName("Small Grocery Store");
        Recipes.Prices heavyPrices, shopPrices;
        double commerceNow;
        try {
            econ.setPowerPrices(commerce + d, industry, works);
            commerceNow = econ.getPricePerWatt();
            heavyPrices = Recipes.pricesOf(g, ovens);
            shopPrices = Recipes.pricesOf(g, shop);
        } finally {
            econ.setPowerPrices(commerce, industry, works);
        }
        assertTrue("a recipe of the ovens is weighed at the industry price for its draw and the works' for its surplus, and a shop's at the commerce price",
                heavyPrices.pricePerWatt() == industry && heavyPrices.worksPricePerWatt() == works
                        && shopPrices.pricePerWatt() == commerceNow && commerceNow != industry);
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit.relative() < 1e-4, String.format("residual %.3g of what moved", audit.relative()));
    }
}
