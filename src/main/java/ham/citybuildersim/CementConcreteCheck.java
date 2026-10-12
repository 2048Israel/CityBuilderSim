package ham.citybuildersim;

import ham.citybuildersim.sectors.CementConcrete;
import ham.citybuildersim.sectors.ProcessingSector;
import ham.citybuildersim.sectors.Quarrying;
import ham.citybuildersim.sectors.SpreadPlanner;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/**
 * Cement & Concrete, checked (0.7.131, batch I7; docs/studies/spec-materials.md
 * 2.1, 2.3, 2.12, 4 and 5's I7 row; the study's - materials-chains-research.md
 * - 1.2, 6 and 7): stone's processing industry, on Sectors.KEYS after
 * Quarrying (its end when it came).
 *
 * WHY A HARNESS OF ITS OWN. A new sector gets one (CLAUDE.md; I6's
 * CoalMiningCheck), and this one is ProcessingSector's rule on stone: its
 * rows, its four cases against the study, who buys what it makes and takes,
 * and the one thing the carry adds to how a works is weighed - limestone
 * the quarries ship as aggregate is limestone the city exported.
 *
 * WHAT THIS HAS TO PROVE:
 *
 *   1. THE SECTOR ON THE REGISTRY: a ProcessingSector after Quarrying,
 *      making cement and lime and using limestone, aggregate and thermal
 *      coal, Iron & Steel's group; nothing captive; cement in a silo.
 *
 *   2. THE THREE ROWS ARE THE STUDY'S (ids 118 to 120, at parity): cost,
 *      posts, draw (cement 91 kWh a tonne; the kiln at the Pelletising
 *      Plant's ratio), ground (the Steel Foundry's per D$), road load,
 *      stock, consumables; and their four cases, worked from the study's own
 *      figures (its 6.4 rows at its 6.3 prices) as I6's were, to its
 *      rounding; then the returns its 6.5 and 1.2 state - cement plants
 *      0.1-0.3% exporting and 1.5-1.9% replacing imports, the kiln 1.6% on
 *      limestone at its export price and 5.7% replacing imported lime, the
 *      kiln's case A 0.2% and the cement plants' below nothing.
 *
 *   3. THE BUYERS (spec 2.12): limestone to the blast furnaces and the kilns
 *      and cement plants; lime to the oxygen shop; thermal coal to the kilns
 *      and cement plants, Coal Mining's where the city digs it; aggregate to
 *      the cement plants alone; cement and dimension stone to nobody here
 *      until the construction upgrade - they ship.
 *
 *   4. LIMESTONE CARRIED IS LIMESTONE EXPORTED (ProcessingSector.values()):
 *      with the quarries carrying limestone into aggregate, a works' limestone
 *      is weighed at the export price for as much as was carried, the import
 *      price past it - a kiln's appraisal that figure to the bit; with none
 *      carried, SpreadPlanner.values() unchanged.
 *
 *   5. THE PLAN: the best earnings on the whole cost of the works that pass
 *      every gate (SpreadPlanner.best()), or the refusal of the best fed.
 *
 *   6. A PLAYED MONTH: a Cement Plant beside a quarry buys the quarry's
 *      limestone at home, puts its cement in its silo or ships it, and the
 *      money audit closes.
 */
public class CementConcreteCheck {

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
        sector();
        rows();
        buyers();
        carriedIsExported();
        playedMonth();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. the sector ============================ */

    static void sector() {
        out.println("--- 1. the sector on the registry: a works on limestone, aggregate and coal, making cement and lime ---");
        String[] keys = Sectors.KEYS;
        // ...on the end when it came (0.7.131), and copper's two after it since 0.7.133 (batch I8; Q21's order).
        int at = java.util.Arrays.asList(keys).indexOf(Sectors.CEMENT_CONCRETE);
        assertTrue("Sectors.KEYS has Cement & Concrete after Quarrying, the twentieth, and copper's two after it (0.7.133)",
                at == 19 && keys[at - 1].equals(Sectors.QUARRYING) && keys[at + 1].equals(Sectors.COPPER_MINING));
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        CementConcrete c = s.cementConcrete();
        assertTrue("...a processing sector found by its saved name, Iron & Steel's group, making cement and lime and using limestone,"
                        + " aggregate and thermal coal; cement its planning good",
                s.byKey(Sectors.CEMENT_CONCRETE) == c && c instanceof ProcessingSector && c.group() == BuildingType.HEAVY_INDUSTRY
                        && java.util.Set.copyOf(c.goodsMade()).equals(java.util.Set.of(Good.CEMENT, Good.LIME))
                        && c.isUser(Good.LIMESTONE) && c.isUser(Good.AGGREGATE) && c.isUser(Good.THERMAL_COAL)
                        && c.planningGood() == Good.CEMENT);
        assertTrue("...nothing it makes does it use, so nothing is captive", !c.captive(Good.CEMENT) && !c.captive(Good.LIME)
                && !c.captive(Good.LIMESTONE));
    }

    /* ============================ 2. the three rows against the study ============================ */

    /** Cement & Concrete's rows, by their permanent ids, in the study's 6.4 order. */
    static final int[] ROWS = { CementConcrete.CEMENT_PLANT, CementConcrete.LARGE_CEMENT_PLANT, CementConcrete.LIME_KILN };

    /** The study's 6.4: each one's cost, D$ thousands all in. */
    static final double[] STUDY_COSTS = { 120_000, 500_000, 75_000 };

    /** ...its posts. */
    static final int[] STUDY_POSTS = { 60, 150, 45 };

    /** ...its running cost a month, D$ thousands. */
    static final double[] STUDY_RUNS = { 501, 1_730, 298 };

    /** ...what it makes a month, t: cement, cement, lime. */
    static final double[] STUDY_MAKES = { 25_000, 125_000, 18_000 };

    /** ...and what it takes, t a month: limestone, aggregate, thermal coal. */
    static final double[][] STUDY_USES = { { 30_000, 3_750, 3_350 }, { 150_000, 18_750, 16_750 }, { 32_400, 0, 3_600 } };

    /** The research's cement, kWh a tonne: the study's 6.2, 91 [S31]. */
    static final double CEMENT_KWH = 91;

    /** The hours of a month the study reads its kW at (its 2.2): QuarryingCheck's. */
    static final double HOURS = QuarryingCheck.HOURS;

    /** The year each first existed: 1900 - standing before the start - but the Large Cement Plant, Edison's long kilns at New Village in 1902 (research-1900.md 1.2). */
    static final int[] SINCE = { 1900, 1902, 1900 };

    /** The study's rounding: its returns to a tenth of a point. */
    static final double STUDY_ROUNDING = .05;

    static void rows() {
        out.println("\n--- 2. the three rows at parity: the study's 6.4, and its four cases worked from its own figures, to its rounding ---");
        BuildingManager b = new BuildingManager();
        b.initializeTemplates();
        UtilitiesHandler founding = new UtilitiesHandler();
        double tariff = founding.getPricerPerWatt(), water = founding.getPricePerWaterUnit();
        java.util.function.ToDoubleFunction<Good> imp = Good::worldImportPrice, exp = Good::worldExportPrice;
        Good[] feed = { Good.LIMESTONE, Good.AGGREGATE, Good.THERMAL_COAL };
        BuildingsTemplate foundry = b.getTemplate(ham.citybuildersim.sectors.HeavyIndustry.ARC_FURNACE),
                pellets = b.getTemplate(ham.citybuildersim.sectors.HeavyIndustry.PELLETISING_PLANT);
        double foundryCost = foundry.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * foundry.getConstructionMaterials();
        String[] cases = { "A", "B", "C", "D" };
        double[][] pct = new double[ROWS.length][4];
        for (int r = 0; r < ROWS.length; r++) {
            BuildingsTemplate t = b.getTemplate(ROWS[r]);
            boolean kiln = r == 2;
            Good made = kiln ? Good.LIME : Good.CEMENT;
            double cost = t.getCashCost() + BuildingManager.MATERIALS_WORLD_PRICE * t.getConstructionMaterials();
            long kw = kiln ? Math.round(pellets.getElectricityConsumption() * STUDY_MAKES[r] / pellets.makes(Good.PELLETS))
                    : Math.round(CEMENT_KWH * STUDY_MAKES[r] / HOURS);
            boolean uses = true;
            for (int i = 0; i < feed.length; i++) uses &= t.uses(feed[i]) == STUDY_USES[r][i];
            report("the " + t.getName() + " (" + t.getId() + ") is the study's: D$" + String.format("%,.0f", STUDY_COSTS[r]) + "k all in, "
                            + STUDY_POSTS[r] + " posts, " + kw + " kW, " + String.format("%,.0f", STUDY_MAKES[r]) + " t of " + made.label()
                            + " from its 6.4 feed; Cement & Concrete's, HEAVY_INDUSTRY, dated " + SINCE[r] + ", on no site",
                    cost == STUDY_COSTS[r] && t.getTotalJobs() == STUDY_POSTS[r] && t.getElectricityConsumption() == kw
                            && t.makes(made) == STUDY_MAKES[r] && t.goodsMade().size() == 1 && uses
                            && t.goodsUsed().size() == (kiln ? 2 : 3) && Sectors.CEMENT_CONCRETE.equals(t.getSector())
                            && t.getCategory() == BuildingType.HEAVY_INDUSTRY && t.getSince() == SINCE[r] && t.site() == null,
                    String.format("cash %,.0f + 18 x %,d material", t.getCashCost(), t.getConstructionMaterials()));
            double tonnes = STUDY_MAKES[r];
            for (double u : STUDY_USES[r]) tonnes += u;
            report("...its ground the Steel Foundry's per D$ of cost, its road load its tonnes in and out at the Foundry's 110 for 2,520 t,"
                            + " and three months of " + (kiln ? "nothing: lime is a flow good" : "cement in its silo"),
                    Math.abs(t.getLandSqFt() - STUDY_COSTS[r] * foundry.getLandSqFt() / foundryCost) <= .5
                            && t.getRoadLoad() == Math.round(tonnes * foundry.getRoadLoad() / 2_520)
                            && t.getStock() == (kiln ? 0 : 3 * STUDY_MAKES[r]),
                    String.format("%,.0f sq ft, %,.0f, stock %,.0f", t.getLandSqFt(), t.getRoadLoad(), t.getStock()));
            double wages = 0;
            for (JobType j : JobType.values()) wages += t.getJobs(j) * PayTier.wageOf(j);
            double running = wages + t.getElectricityConsumption() * tariff + t.getWaterConsumption() * water;
            report("its consumables are the study's running cost less its wages, power and water",
                    Math.abs(t.getConsumables() - (STUDY_RUNS[r] - running)) < 1e-9 && t.getGen() == 0,
                    String.format("%,.2f = %,.0f - %,.2f", t.getConsumables(), STUDY_RUNS[r], running));
            boolean all = true;
            StringBuilder got = new StringBuilder(), study = new StringBuilder();
            for (SpreadPlanner.Case c : SpreadPlanner.Case.values()) {
                java.util.function.ToDoubleFunction<Good> out = c.outputAtImport() ? imp : exp, in = c.feedAtImport() ? imp : exp;
                double model = SpreadPlanner.caseEarns(t, c, imp, exp, running, 0, 1, tariff);
                double theirs = STUDY_MAKES[r] * out.applyAsDouble(made) - STUDY_RUNS[r];
                for (int i = 0; i < feed.length; i++) theirs -= STUDY_USES[r][i] * in.applyAsDouble(feed[i]);
                double p = 100 * model / cost, want = 100 * theirs / STUDY_COSTS[r];
                pct[r][c.ordinal()] = want;
                all &= Math.abs(p - want) <= STUDY_ROUNDING + 1e-9;
                got.append(String.format("%s %.3f%%  ", cases[c.ordinal()], p));
                study.append(c.ordinal() == 0 ? "" : "/").append(String.format("%.3f", want));
            }
            report("...its four cases are the study's arithmetic to its rounding: " + study + "%", all, got.toString().trim());
        }
        int a = SpreadPlanner.Case.A.ordinal(), bb = SpreadPlanner.Case.B.ordinal(), cc = SpreadPlanner.Case.C.ordinal();
        report("the study's 6.5: cement plants return 0.1-0.3% exporting (case B) and 1.5-1.9% replacing imports (case C)",
                Math.abs(pct[0][bb] - .1) <= STUDY_ROUNDING && Math.abs(pct[1][bb] - .3) <= STUDY_ROUNDING
                        && Math.abs(pct[0][cc] - 1.5) <= STUDY_ROUNDING && Math.abs(pct[1][cc] - 1.9) <= STUDY_ROUNDING,
                String.format("B %.3f/%.3f, C %.3f/%.3f", pct[0][bb], pct[1][bb], pct[0][cc], pct[1][cc]));
        report("...the lime kiln 1.6% on limestone at its export price and 5.7% replacing imported lime",
                Math.abs(pct[2][bb] - 1.6) <= STUDY_ROUNDING && Math.abs(pct[2][cc] - 5.7) <= STUDY_ROUNDING,
                String.format("B %.3f, C %.3f", pct[2][bb], pct[2][cc]));
        report("...and its 1.2's case A: the kiln 0.2%, under every hurdle, and both cement plants below nothing",
                Math.abs(pct[2][a] - .2) <= STUDY_ROUNDING && pct[0][a] < 0 && pct[1][a] < 0,
                String.format("kiln %.3f, cement %.3f/%.3f", pct[2][a], pct[0][a], pct[1][a]));
    }

    /* ============================ 3. the buyers ============================ */

    static void buyers() {
        out.println("\n--- 3. the buyers: who takes stone's goods, and what ships ---");
        Sectors s = new Sectors(new BuildingManager(), new Markets());
        java.util.Map<Good, List<String>> users = new java.util.EnumMap<>(Good.class), makers = new java.util.EnumMap<>(Good.class);
        for (Good g : new Good[] { Good.LIMESTONE, Good.LIME, Good.THERMAL_COAL, Good.AGGREGATE, Good.CEMENT, Good.DIMENSION_STONE }) {
            List<String> u = new java.util.ArrayList<>(), m = new java.util.ArrayList<>();
            for (Sector sec : s.all()) {
                if (sec.isUser(g)) u.add(sec.key());
                if (sec.isMaker(g)) m.add(sec.key());
            }
            users.put(g, u);
            makers.put(g, m);
        }
        report("limestone: made by Quarrying, taken by Iron & Steel's blast furnaces and Cement & Concrete's kilns and plants",
                makers.get(Good.LIMESTONE).equals(List.of(Sectors.QUARRYING))
                        && users.get(Good.LIMESTONE).equals(List.of(Sectors.HEAVY_INDUSTRY, Sectors.CEMENT_CONCRETE)), users.get(Good.LIMESTONE).toString());
        report("lime: made by the kiln, taken by Iron & Steel's oxygen shop", makers.get(Good.LIME).equals(List.of(Sectors.CEMENT_CONCRETE))
                && users.get(Good.LIME).equals(List.of(Sectors.HEAVY_INDUSTRY)), users.get(Good.LIME).toString());
        report("thermal coal: Coal Mining's, taken by Iron & Steel and the kilns and cement plants",
                makers.get(Good.THERMAL_COAL).equals(List.of(Sectors.COAL_MINING))
                        && users.get(Good.THERMAL_COAL).equals(List.of(Sectors.HEAVY_INDUSTRY, Sectors.CEMENT_CONCRETE)),
                users.get(Good.THERMAL_COAL).toString());
        report("aggregate: Quarrying's, taken by the cement plants alone; cement and dimension stone by nobody here - they ship until"
                        + " the construction upgrade",
                makers.get(Good.AGGREGATE).equals(List.of(Sectors.QUARRYING)) && users.get(Good.AGGREGATE).equals(List.of(Sectors.CEMENT_CONCRETE))
                        && users.get(Good.CEMENT).isEmpty() && users.get(Good.DIMENSION_STONE).isEmpty()
                        && Good.CEMENT.exportable() && Good.DIMENSION_STONE.exportable(),
                "aggregate " + users.get(Good.AGGREGATE) + ", cement " + users.get(Good.CEMENT) + ", blocks " + users.get(Good.DIMENSION_STONE));
    }

    /* ============================ 4. limestone carried is limestone exported ============================ */

    static void carriedIsExported() {
        out.println("\n--- 4. limestone the quarries carried into aggregate is weighed as exported: its export price, the import price past it ---");
        Game g = QuarryingCheck.stoneTown("cementconcretecheck-carry", Deposit.FieldType.LIMESTONE, 0);
        BuildingManager b = g.getBuildingManager();
        Quarrying q = g.getSectors().quarrying();
        CementConcrete c = g.getSectors().cementConcrete();
        BusinessInvestment plans = g.getBusinessInvestment();
        MarketsCheckValues before = valuesOf(g);
        BuildingsTemplate kiln = b.getTemplate(CementConcrete.LIME_KILN);
        double plain = c.appraise(kiln, plans).earns();
        report("fixture: a town on a limestone field, no quarry yet: nothing carried, and a kiln weighed at the city's values as they were",
                q.carried() == 0 && plain == weigh(c, kiln, SpreadPlanner.values(g.getMarkets()), plans, g).earns(),
                String.format("D$%,.1fk a month", plain));
        quietly(() -> b.addStack(b.getTemplate(Quarrying.QUARRY), 2, true));
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        double carried = q.carried();
        GoodsMarket lime = g.getMarkets().get(Good.LIMESTONE);
        SpreadPlanner.Values plainV = SpreadPlanner.values(g.getMarkets());
        SpreadPlanner.Values carriedV = new SpreadPlanner.Values() {
            @Override public double out(Good gd, double u) { return plainV.out(gd, u); }
            @Override public double in(Good gd, double u) {
                return gd == Good.LIMESTONE ? SpreadPlanner.inValue(lime, lime.getExported() + carried, u) : plainV.in(gd, u);
            }
        };
        double earns = c.appraise(kiln, plans).earns();
        double byHand = weigh(c, kiln, carriedV, plans, g).earns(), asBefore = weigh(c, kiln, plainV, plans, g).earns();
        report("two Quarries a month on, no buyer: their limestone carried, the market recording none exported; the kiln's appraisal"
                        + " its limestone at the export price for as much as was carried, to the bit",
                carried > 0 && lime.getExported() == 0 && earns == byHand && earns > asBefore,
                String.format("%,.0f t carried: D$%,.1fk a month, D$%,.1fk had it been priced at import", carried, earns, asBefore));
        double kilnLime = kiln.uses(Good.LIMESTONE) * BusinessInvestment.operatingRateOf(c.getOperatingRate());
        report("...the kiln's limestone worth D$10 a tonne at parity's export price for as much as was carried, D$40 past it",
                Math.abs(carriedV.in(Good.LIMESTONE, kilnLime) - (Math.min(kilnLime, carried) * lime.netExportPrice()
                        + Math.max(0, kilnLime - carried) * lime.netImportPrice())) < 1e-6,
                String.format("%,.0f t at D$%.4fk and D$%.4fk", kilnLime, lime.netExportPrice(), lime.netImportPrice()));
        // The plan: SpreadPlanner.best() of the appraisal, or the refusal of the best fed.
        List<SpreadPlanner.Candidate> all = c.appraise(plans, g);
        SpreadPlanner.Candidate best = SpreadPlanner.best(all);
        BusinessInvestment.Decision d = c.plan(plans, g);
        StringBuilder each = new StringBuilder();
        for (SpreadPlanner.Candidate k : all) {
            each.append(String.format("%s %.3f%%%s; ", k.template().getName(), 100 * k.score(), k.passes() ? "" : " (" + k.failed() + ")"));
        }
        report("5. the plan orders the best earnings on the whole cost of the works that pass every gate, or none, and says why",
                best == null ? !d.build && d.reason != null : d.build && d.template == best.template(),
                d.reason + " - " + each);
        assertTrue("(the city's values before the quarries stood: limestone imported at its net import price)",
                before.limeIn > 0);
    }

    /** One works of `c` weighed at `v`, as ProcessingSector weighs it: its lorries in its earnings and its cost. */
    static SpreadPlanner.Candidate weigh(CementConcrete c, BuildingsTemplate t, SpreadPlanner.Values v, BusinessInvestment plans, Game g) {
        SpreadPlanner.City city = c.planner().city(c, plans, g);
        return ProcessingSector.estimate(t, v, city, c.exchangeFactor(), c.tariff(), null, 1,
                ProcessingSector.fleetRunning(t, v, city.rate()), ProcessingSector.fleetCapital(t, v));
    }

    /** What a tonne of limestone used costs the city now (the values' in()). */
    record MarketsCheckValues(double limeIn) { }

    static MarketsCheckValues valuesOf(Game g) {
        return new MarketsCheckValues(SpreadPlanner.values(g.getMarkets()).in(Good.LIMESTONE, 1));
    }

    /* ============================ 6. a played month ============================ */

    static void playedMonth() {
        out.println("\n--- 6. a played month: a Cement Plant on a quarry's limestone, its cement in its silo or abroad, the audit closed ---");
        Game g = QuarryingCheck.stoneTown("cementconcretecheck-month", Deposit.FieldType.LIMESTONE, 0);
        BuildingManager b = g.getBuildingManager();
        Quarrying q = g.getSectors().quarrying();
        CementConcrete c = g.getSectors().cementConcrete();
        quietly(() -> {
            b.addStack(b.getTemplate(Quarrying.QUARRY), 1, true);
            b.addStack(b.getTemplate(CementConcrete.CEMENT_PLANT), 1, true);
        });
        MaterialsCheck.handWorkers(g);
        quietly(() -> g.simulateMonths(2));
        Sector.Output cement = c.outputRow(Good.CEMENT);
        double made = cement == null ? 0 : cement.planned + cement.exportBound;
        double stock = c.getStock(Good.CEMENT), direct = cement == null ? 0 : cement.exportBound;
        // ...its statement struck at the top of the next month (Game.startOfMonthUpdate()).
        quietly(() -> g.simulateMonths(1));
        Sector.Split bought = c.statement().bought.get(Good.LIMESTONE);
        report("fixture: a Quarry and a Cement Plant standing, two months on: the plant bought the quarry's limestone at home (its"
                        + " statement, struck at the next month's top)",
                bought != null && bought.atHome > 0 && q.statement().sold.get(Good.LIMESTONE) != null
                        && q.statement().sold.get(Good.LIMESTONE).atHome > 0,
                (bought == null ? "none bought" : String.format("D$%,.1fk at home, D$%,.1fk abroad", bought.atHome, bought.abroad))
                        + String.format("; rate %.3f (fill %.3f, power %.3f, water %.3f, roads %.3f, health %.3f, vans %.3f)",
                        c.getOperatingRate(), c.getAverageFill(), c.getEnergyRatio(), c.getWaterRatio(), c.getRoadRatio(),
                        c.getHealthRatio(), c.getVanRatio()));
        report("...and made cement, into its silo or straight abroad (the export-bound share leaves from the line)",
                made > 0 && (stock > 0 || direct > 0),
                String.format("%,.0f t made, %,.0f t in the silo, %,.0f t straight abroad", made, stock, direct));
        MoneyAudit.Result audit = g.getLastMoneyAudit();
        report("the month's money audit closes", audit != null && audit.relative() < 1e-4,
                audit == null ? "none" : String.format("residual %.3g of what moved", audit.relative()));
    }
}
