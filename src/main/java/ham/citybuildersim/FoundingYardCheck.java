package ham.citybuildersim;

import ham.citybuildersim.sectors.Construction;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Locale;

/**
 * The founding yard (PT1-M4, 2026-10-12): the works department's free 400
 * points a month are retired and every city founds with one Builders' Yard
 * standing, staffed and paid like any other, so the builders earn only for the
 * work their own people do - and keep the one building that builds, which they
 * cannot sell.
 *
 * WHY. PT1's month 183 was the finding: $377k of a month at a 72% margin with
 * 0 workers in the sector's posts. BuildingManager.BASE_CONSTRUCTION was 400
 * points of building work with no posts behind them, paid to Construction as
 * revenue. Jerus: "not meant ... ya sure fix, perhaps a starting depot or
 * something instead". The constant is gone; Game.run() and newGame() place the
 * yard (Game.foundingYard()); the builders' last building that builds is not
 * sold by either of the retirement rules nor bought out (Construction.
 * mayRetire(), Game.demolishable()). A yard and not the Construction Depot,
 * because the depot's 50 posts were a founding village's whole workforce: idle,
 * its builders laid 37 of them off and every planner counted them as spoken for
 * (Sector.staffing()), so a founded village stood at 146 people with no shop
 * in twenty years, where main's opened its first at month 13. The yard is the
 * depot at two fifths (20 posts, 160 points); section 6 is the proof.
 *
 * What this has to prove:
 *   1. THE FOUNDING STANDS ONE YARD, through both doors (run() and newGame()):
 *      standing, none on site, no depot, the builders', the capacity its
 *      nameplate and no other points, and the land office holds its ground.
 *   2. NO YARD, NO POINTS: with the yard retired by fiat the builders'
 *      capacity and output are 0, and houses ordered owe what they owed, are
 *      not finished and are not recognised, month after month.
 *   3. MONTH 1 IS THE FIRST-MONTH CONVENTION, and month 2 on is worked by the
 *      crews the city has: the fill reads 1 until the first labour market,
 *      the yard works month 1 at its nameplate; from then the output is the
 *      nameplate x the posts offered share x the fill x the road and work
 *      ratios; and a founded city saved before its first month loads with one
 *      yard, its capacity and its ground.
 *   4. A FOUNDING TOWN BUILDS, AND EARNS ONLY FOR WORK ITS CREWS DO: the
 *      founding village stands within TOWN_MONTHS; once crews come the sites
 *      get work every month while anything is owed (T1-C's trap); a month
 *      whose crews are 0 has no output and recognises nothing; the builders'
 *      payroll is positive.
 *   5. THE LAST BUILDING THAT BUILDS IS NEVER SOLD: neither the
 *      spare-capacity rule nor the distress rule sells the yard, nor does the
 *      city buy it out; with a Construction Depot standing beside it, both
 *      sell one of the two and leave the other.
 *   6. A FOUNDED VILLAGE HAS HANDS FOR ITS SHOPS, WHERE A DEPOT WOULD HAVE HELD
 *      THEM: a hands-off village founded on the yard opens its first shop
 *      within FIRST_SHOP_MONTHS, its builders idle at the core crew and holding
 *      posts back; its twin, founded the same way with a Construction Depot
 *      in the yard's place, holds more posts back in its idle months and opens
 *      none in the same months.
 *   7. MONEY IS CONSERVED: the money audit every month played here.
 *
 * Every fixture causes its condition and says so in an assertion labelled
 * "fixture:". How a real run goes is LongPlaytest's.
 */
public class FoundingYardCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-100s %s  %,.6f against %,.6f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void check(String label, long actual, long expected) {
        boolean ok = actual == expected;
        if (!ok) fails++;
        out.printf("%-100s %s  %,d against %,d%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static BuildingsTemplate template(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new IllegalStateException("no template named " + name);
        return t;
    }

    /** The builders' yard: what every city is founded with. */
    static final String YARD = Game.FOUNDING_YARD;

    /** The builders' depot: the village's own order, and the second builders' building of sections 5 and 6. */
    static final String DEPOT = "Construction Depot";

    /** The founding bank, whose ground the yard's is added to. */
    static final String BANK = "Commercial Bank";

    /** The months a city with no yard is played: long enough for a month's work to show it is not done. */
    static final int NO_YARD_MONTHS = 3;

    /**
     * The months the founding village is given to stand: the two founding months nobody is there to work, then its
     * points at the core crew alone (Construction.IDLE_PAYROLL_FLOOR of the yard's nameplate a month, the least the
     * builders ever keep on). A village that stands inside it was built by the people the city had.
     */
    static int townMonths(Game g) {
        BuildingsTemplate yard = template(g, YARD);
        return 2 + (int) Math.ceil(villagePoints(g)
                / (yard.makes(Good.BUILDING_WORK) * Construction.IDLE_PAYROLL_FLOOR));
    }

    /**
     * The months a hands-off founded village is given to open its first shop: three years. Main's village of 0.8.34,
     * on the works department's free points, opened its first at month 13; the yard's opens at month 24, its idle
     * crews holding 14 posts; the village on a Construction Depot stalled with none in 240, holding 37.
     */
    static final int FIRST_SHOP_MONTHS = 36;

    /** The worst month of the audit any month here saw, and the worst drift after it. */
    static double worstResidual = 0, worstDrift = 0;
    static int monthsAudited = 0;

    /** One month, audited. */
    static void month(Game g) {
        quietly(() -> g.simulateMonths(1));
        MoneyAudit.Result r = g.getLastMoneyAudit();
        if (r != null && r.relative() > worstResidual) worstResidual = r.relative();
        if (Math.abs(g.getPostAuditDrift()) > Math.abs(worstDrift)) worstDrift = g.getPostAuditDrift();
        monthsAudited++;
    }

    /** A city and the folder it is saved into. */
    record City(Game game, GameFiles files) { }

    /** A new city in a folder of its own, founded through run() or through newGame(). */
    static City founded(Path root, String name, boolean byNewGame) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> { if (byNewGame) g.newGame(); else g.run(); });
        return new City(g, files);
    }

    /** The founding village's points: each building's construction points times its count. */
    static double villagePoints(Game g) {
        double points = 0;
        for (String[] line : Founding.VILLAGE) points += template(g, line[0]).getConstructionPoints() * Integer.parseInt(line[1]);
        return points;
    }

    /** Orders the founding village, line by line, the treasury paying; returns whether every order was taken. */
    static boolean orderVillage(Game g) {
        boolean all = true;
        for (String[] line : Founding.VILLAGE) {
            Game.BuildResult[] result = new Game.BuildResult[1];
            quietly(() -> result[0] = g.buildStack(template(g, line[0]), Integer.parseInt(line[1]), false));
            all &= result[0] == Game.BuildResult.SUCCESS;
        }
        return all;
    }

    static int villageCount(String name) {
        for (String[] line : Founding.VILLAGE) if (line[0].equals(name)) return Integer.parseInt(line[1]);
        return 0;
    }

    /** The output the model says the builders give now: nameplate x posts offered share x fill x road ratio x work ratio. */
    static long outputFormula(Game g) {
        Construction builders = g.getSectors().construction();
        return Math.round(g.getBuildingManager().getTotalConstructionCapacity() * builders.getPostsOfferedShare()
                * builders.getAverageFill() * g.getServicesManager().getRoadRatio() * g.getHealth().getWorkRatio());
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("foundingyardcheck");

        standing(root);
        noYard(root);
        monthOne(root);
        town(root);
        lastYard(root);
        handsForShops(root);
        conserved();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE FOUNDING STANDS ONE YARD ============================ */

    static void standing(Path root) {
        out.println("--- 1. the founding stands one yard, through both doors ---");
        for (boolean byNewGame : new boolean[] { false, true }) {
            String door = byNewGame ? "newGame()" : "run()";
            City c = founded(root, byNewGame ? "door-new-game" : "door-run", byNewGame);
            Game g = c.game();
            BuildingManager bm = g.getBuildingManager();
            BuildingsTemplate yard = template(g, YARD), bank = template(g, BANK);
            check("a city founded by " + door + " stands one " + YARD, bm.countByName(YARD), 1);
            check("...and no " + DEPOT, bm.countByName(DEPOT), 0);
            check("...none on site", bm.underConstructionByName(YARD), 0);
            assertTrue("...owned by the builders (" + door + ")", yard.getSector().equals(Sectors.CONSTRUCTION)
                    && yard.makes(Good.BUILDING_WORK) > 0);
            check("...and the builders' capacity is that yard's nameplate, no other points (" + door + ")",
                    bm.getTotalConstructionCapacity(), (long) yard.makes(Good.BUILDING_WORK));
            check("...as the builders' own screen reads it (" + door + ")",
                    g.getSectors().construction().getConstructionCapacity(), yard.makes(Good.BUILDING_WORK), 1e-12);
            assertTrue("...with its posts standing: " + g.getSectors().construction().getPostsStanding() + " of the yard's "
                    + (yard.getJobs(JobType.NO_DIPLOMA) + yard.getJobs(JobType.DIPLOMA)),
                    g.getSectors().construction().getPostsStanding() == yard.getJobs(JobType.NO_DIPLOMA)
                            + yard.getJobs(JobType.DIPLOMA)
                            && yard.getJobs(JobType.NO_DIPLOMA) + yard.getJobs(JobType.DIPLOMA) > 0);
            assertTrue("...and the land office holds its ground beside the bank's (" + door + ")",
                    g.getLandManager().getAllocatedSqFt() + 1e-6 >= bank.getLandSqFt() + yard.getLandSqFt());
        }
    }

    /* ============================ 2. NO YARD, NO POINTS ============================ */

    static void noYard(Path root) {
        out.println("\n--- 2. no yard, no points ---");
        City c = founded(root, "no-yard", false);
        Game g = c.game();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        BuildingsTemplate yard = template(g, YARD), house = template(g, "House");
        bm.retire(yard, 1);                        // the fixture: by fiat, past the guard that refuses a sale
        assertTrue("fixture: no builders' building stands", bm.countByName(YARD) == 0 && bm.countByName(DEPOT) == 0);
        check("with no yard the builders' capacity is 0", bm.getTotalConstructionCapacity(), 0);
        check("...and their output 0", g.getConstructionOutput(), 0);

        int ordered = villageCount("House");
        Game.BuildResult[] result = new Game.BuildResult[1];
        quietly(() -> result[0] = g.buildStack(house, ordered, false));
        double owed = bm.getRemainingConstructionPoints();
        int standingBefore = bm.countByName("House");
        assertTrue("fixture: " + ordered + " houses are ordered and the sites owe their points",
                result[0] == Game.BuildResult.SUCCESS && owed > 0);
        boolean unchanged = true, none = true;
        for (int m = 1; m <= NO_YARD_MONTHS; m++) {
            month(g);
            unchanged &= Math.abs(bm.getRemainingConstructionPoints() - owed) <= 1e-9 * owed;
            none &= builders.getRecognisedThisMonth() == 0;
        }
        assertTrue("the sites owe what they owed after " + NO_YARD_MONTHS + " months: nothing advanced them", unchanged);
        check("...no house is finished", bm.countByName("House"), standingBefore);
        assertTrue("...and Construction recognised nothing in any of them", none);
    }

    /* ============================ 3. MONTH 1, AND MONTH 2 ON ============================ */

    static void monthOne(Path root) {
        out.println("\n--- 3. month 1 is the first-month convention; month 2 on is worked by the crews the city has ---");
        City c = founded(root, "month-one", false);
        Game g = c.game();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        BuildingsTemplate yard = template(g, YARD);
        double nameplate = yard.makes(Good.BUILDING_WORK);
        assertTrue("fixture: nobody lives in the city at its founding", g.getPopulationManager().getPopulation() == 0);
        check("before the first labour market the builders' fill reads 1, as every employer's does",
                builders.getAverageFill(), 1.0, 1e-12);
        double roadAtFounding = g.getServicesManager().getRoadRatio(), workAtFounding = g.getHealth().getWorkRatio();
        check("...so the yard's output is its nameplate x the road ratio x the work ratio",
                g.getConstructionOutput(), Math.round(nameplate * roadAtFounding * workAtFounding), 0);

        assertTrue("fixture: the founding village is ordered before the first month", orderVillage(g));
        double owedBefore = bm.getRemainingConstructionPoints();
        int outputBefore = g.getConstructionOutput();
        assertTrue("fixture: the village owes more than the yard does in a month",
                owedBefore > outputBefore && outputBefore > 0);

        // The save before month 1, taken now, into the city's own folder: slot 10 of a scratch folder.
        quietly(() -> g.saveGame(10, "foundingyard"));
        Game twin = new Game(c.files());
        quietly(() -> twin.loadGameSave(10));
        // The twin, loaded from the save before month 1: one yard, its capacity, its ground, its orders.
        BuildingManager tb = twin.getBuildingManager();
        check("the city reloaded from a save before its first month stands one " + YARD + " (the load founds none more)",
                tb.countByName(YARD), 1);
        check("...and no " + DEPOT + " (the village's own is still on site)", tb.countByName(DEPOT), 0);
        check("...with the same capacity", tb.getTotalConstructionCapacity(), (long) nameplate);
        check("...the same points owed on site", tb.getRemainingConstructionPoints(), owedBefore, 1e-9);
        check("...and the same ground allocated", twin.getLandManager().getAllocatedSqFt(),
                g.getLandManager().getAllocatedSqFt(), 1e-9);

        month(g);
        double advanced = owedBefore - bm.getRemainingConstructionPoints();
        double fillAfter = builders.getAverageFill(), recognisedLive = builders.getRecognisedThisMonth();
        out.printf("   month 1: the sites advanced %,.1f of %,d points, the builders recognised %,.1f; the fill is now %.3f "
                + "with %,d people%n", advanced, outputBefore, builders.getRecognisedThisMonth(), fillAfter,
                g.getPopulationManager().getPopulation());
        assertTrue("month 1 is worked at the yard's nameplate: the sites advanced, by no more than its output",
                advanced > 0 && advanced <= outputBefore + 1e-9);
        assertTrue("...and the builders recognised the work", builders.getRecognisedThisMonth() > 0);
        assertTrue("fixture: the first labour market struck the fill off the city's people, below the convention's 1",
                fillAfter < 1);

        // ...a reload strikes every fill from the empty workforce, so its month 1 may differ from the live city's
        // (the live one worked at the convention's 1): printed, not asserted. Then month 2 each.
        Construction twinBuilders = twin.getSectors().construction();
        double twinOwed = tb.getRemainingConstructionPoints();
        month(twin);
        double twinAdvanced = twinOwed - tb.getRemainingConstructionPoints();
        out.printf("   the twin's month 1: the sites advanced %,.1f, recognised %,.1f, fill %.3f (the live city: %,.1f, %,.1f, %.3f)%n",
                twinAdvanced, twinBuilders.getRecognisedThisMonth(), twinBuilders.getAverageFill(),
                advanced, recognisedLive, fillAfter);
        month(g);
        month(twin);
        out.printf("   after month 2, live: %,d people, fill %.3f, output %d, owed %,.1f, recognised %,.1f%n",
                g.getPopulationManager().getPopulation(), builders.getAverageFill(), g.getConstructionOutput(),
                bm.getRemainingConstructionPoints(), builders.getRecognisedThisMonth());
        out.printf("   after month 2, twin: %,d people, fill %.3f, output %d, owed %,.1f, recognised %,.1f%n",
                twin.getPopulationManager().getPopulation(), twinBuilders.getAverageFill(), twin.getConstructionOutput(),
                tb.getRemainingConstructionPoints(), twinBuilders.getRecognisedThisMonth());

        boolean formula = g.getConstructionOutput() == outputFormula(g);
        boolean twinFormula = twin.getConstructionOutput() == outputFormula(twin);
        int monthsRead = 1;
        for (int m = 3; m <= 4; m++) {
            month(g);
            formula &= g.getConstructionOutput() == outputFormula(g);
            monthsRead++;
        }
        assertTrue("from the first labour market the output is nameplate x posts offered share x fill x road x work ("
                + monthsRead + " months)", formula);
        assertTrue("...and a reloaded city's builders read the same formula", twinFormula);
    }

    /* ============================ 4. A FOUNDING TOWN BUILDS ============================ */

    static void town(Path root) {
        out.println("\n--- 4. a founding town builds, and earns only for work its crews do ---");
        City c = founded(root, "town", false);
        Game g = c.game();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        assertTrue("fixture: the founding village is ordered at the founding", orderVillage(g));
        int limit = townMonths(g);
        out.printf("   the village is %,.0f points; TOWN_MONTHS is %d%n", villagePoints(g), limit);

        int monthsWithCrews = 0, monthsWithout = 0, standsAt = -1, firstCrews = -1;
        int zeroLeaks = 0, trapMonths = 0, trapStarved = 0, formulaOff = 0;
        double payroll = 0;
        StringBuilder trace = new StringBuilder();
        for (int m = 1; m <= limit; m++) {
            double crews = builders.getWorkers();
            int output = g.getConstructionOutput();
            double owed = bm.getRemainingConstructionPoints();
            int buildingOutput = g.getBuildingOutput();
            if (m >= 2 && output != outputFormula(g)) formulaOff++;
            month(g);
            payroll += builders.statement().payroll;
            double recognised = builders.getRecognisedThisMonth();
            if (m <= 24) {
                trace.append(String.format("      m%-3d pop %,5d crews %6.1f fill %.2f output %4d owed %7.0f homes %4d recognised %,.1f%n",
                        m, g.getPopulationManager().getPopulation(), crews, builders.getAverageFill(), output, owed,
                        bm.getTotalHomes(), recognised));
            }
            if (m >= 2) {
                if (crews > 0) {
                    monthsWithCrews++;
                    if (firstCrews < 0) firstCrews = m;
                    else if (owed > 0) {
                        trapMonths++;
                        if (!(buildingOutput > 0)) trapStarved++;
                    }
                } else {
                    monthsWithout++;
                    if (output != 0 || recognised != 0) zeroLeaks++;
                }
            }
            if (standsAt < 0 && villageStands(bm)) standsAt = m;
        }
        out.printf("   the village stood at month %s (of %d); %d months from month 2 had crews, %d had none; the builders paid %,.0f%n",
                standsAt < 0 ? "never" : String.valueOf(standsAt), limit, monthsWithCrews, monthsWithout, payroll);
        StringBuilder end = new StringBuilder();
        for (String[] line : Founding.VILLAGE) end.append(String.format(" %s %d/%d", line[0], bm.countByName(line[0]),
                Integer.parseInt(line[1])));
        out.println("   standing at the end of the run, month " + g.getMonth() + ":" + end);
        if (standsAt < 0 || monthsWithCrews == 0) out.print(trace);
        assertTrue("every building of the village stood at once by month " + limit + " (the depot beside the founding yard)", standsAt > 0);
        assertTrue("...the last finished in month " + standsAt + ", within TOWN_MONTHS", standsAt > 0 && standsAt <= limit);
        assertTrue("the people came: " + g.getPopulationManager().getPopulation() + " live in the town and the builders' crews filled",
                g.getPopulationManager().getPopulation() > 0 && monthsWithCrews > 0);
        assertTrue("fixture: at least one month from month 2 was worked by no crews (" + monthsWithout + ") and one by crews ("
                + monthsWithCrews + ")", monthsWithout >= 1 && monthsWithCrews >= 1);
        assertTrue("once the first crews arrive (month " + firstCrews + ") the sites get work every month while anything is owed ("
                + trapMonths + " months)", trapMonths > 0 && trapStarved == 0);
        assertTrue("in every month whose crews were 0 the output was 0 and nothing was recognised", zeroLeaks == 0);
        assertTrue("every month from month 2, the output was nameplate x posts offered share x fill x road x work", formulaOff == 0);
        assertTrue("the builders' payroll over the run is positive: they paid the people who did the work", payroll > 0);
    }

    /** Whether every building of the founding village stands (the depot line is the village's own, beside the founding yard). */
    static boolean villageStands(BuildingManager bm) {
        for (String[] line : Founding.VILLAGE) {
            if (bm.countByName(line[0]) < Integer.parseInt(line[1])) return false;
        }
        return true;
    }

    /* ============================ 5. THE LAST BUILDING THAT BUILDS IS NEVER SOLD ============================ */

    static void lastYard(Path root) {
        out.println("\n--- 5. the last building that builds is never sold ---");
        City c = founded(root, "last-yard", false);
        Game g = c.game();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        BuildingsTemplate yard = template(g, YARD), depot = template(g, DEPOT);
        double nameplate = bm.getTotalConstructionCapacity();
        BusinessInvestment bi = new BusinessInvestment(bm, g.getEconomyManager());
        int months = Math.max(BusinessInvestment.RETIREMENT_LOSS_MONTHS, BusinessInvestment.DISTRESS_LOSS_MONTHS);
        for (int i = 0; i < months; i++) bi.recordSectorResult(builders.key(), -1);
        assertTrue("fixture: the builders have lost money for " + months + " months and stand one yard, no depot",
                bi.getLossMonths(builders.key()) >= months && bm.countByName(YARD) == 1 && bm.countByName(DEPOT) == 0);

        BusinessInvestment.Decision spare = bi.planRetirement(builders, 0, nameplate, 0);
        assertTrue("the spare-capacity rule does not sell the yard, though nothing uses it", !spare.build);
        assertTrue("...and says why: " + spare.reason, spare.reason.equals(builders.noRetirementReason(false)));
        BusinessInvestment.Decision distress = bi.planDistressRetirement(builders, -1, 0);
        assertTrue("the distress rule does not sell it either, overdrawn and refused credit", !distress.build);
        assertTrue("...and says why: " + distress.reason, distress.reason.equals(builders.noRetirementReason(true)));
        check("the city may not buy the yard out and demolish it", g.demolishable(yard), 0);

        quietly(() -> bm.addStack(depot, 1, true));
        assertTrue("fixture: a " + DEPOT + " stands beside the yard", bm.countByName(YARD) == 1 && bm.countByName(DEPOT) == 1);
        spare = bi.planRetirement(builders, 0, bm.getTotalConstructionCapacity(), 0);
        assertTrue("with a depot beside it the spare-capacity rule sells one of the two (it picked the "
                + (spare.template == null ? "none" : spare.template.getName()) + "), and leaves the other",
                spare.build && spare.quantity == 1 && spare.template != null
                        && (spare.template.getId() == yard.getId() || spare.template.getId() == depot.getId()));
        distress = bi.planDistressRetirement(builders, -1, 0);
        assertTrue("...and so does the distress rule (it picked the "
                + (distress.template == null ? "none" : distress.template.getName()) + ")",
                distress.build && distress.quantity == 1 && distress.template != null
                        && (distress.template.getId() == yard.getId() || distress.template.getId() == depot.getId()));
        check("...and the city may buy the yard out, the depot standing", g.demolishable(yard), 1);
        check("...or the depot out, the yard standing", g.demolishable(depot), 1);
    }

    /* ============================ 6. HANDS FOR ITS SHOPS, WHERE A DEPOT WOULD HAVE HELD THEM ============================ */

    /** How many of the village's two small food shops stand. */
    static int shops(BuildingManager bm) {
        return bm.countByName("Convenience Store") + bm.countByName("Small Grocery Store");
    }

    /** One hands-off village, played FIRST_SHOP_MONTHS months: when its first shop stood, and what its builders held back while idle. */
    record Village(Game game, int firstShop, int idleMonths, long withheldInIdle, long mostWithheld, long people) {
        double meanWithheld() { return idleMonths == 0 ? 0 : (double) withheldInIdle / idleMonths; }
    }

    /**
     * A hands-off founded city (run(), no orders, Founding.defaults()), its yard retired and a Construction Depot placed
     * in its stead before month 1 when depotInstead, played FIRST_SHOP_MONTHS months. An idle month is one in which the
     * builders offer the core crew and no more (Construction.IDLE_PAYROLL_FLOOR) with posts held back (the figure
     * Sector.staffing() counts as spoken for: BuildingManager.getPostsWithheld()).
     */
    static Village playHandsOff(Path root, String name, boolean depotInstead) {
        City c = founded(root, name, false);
        Game g = c.game();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        String stands = (depotInstead ? "the " + DEPOT : "the " + YARD) + " of " + name;
        if (depotInstead) {
            BuildingsTemplate yard = template(g, YARD), depot = template(g, DEPOT);
            bm.retire(yard, 1);
            quietly(() -> bm.addStack(depot, 1, true));
            // ...the ground goes with it, as the land office would have it had the city been founded so.
            g.getLandManager().release(yard.getLandSqFt());
            g.getLandManager().allocate(depot.getLandSqFt());
        }
        assertTrue("fixture: " + name + " stands its one builders' building, " + stands
                + (depotInstead ? ", the yard retired" : ", no depot"),
                bm.countByName(YARD) == (depotInstead ? 0 : 1) && bm.countByName(DEPOT) == (depotInstead ? 1 : 0));
        int firstShop = -1, idleMonths = 0;
        long withheldInIdle = 0, mostWithheld = 0;
        for (int m = 1; m <= FIRST_SHOP_MONTHS; m++) {
            month(g);
            if (firstShop < 0 && shops(bm) > 0) firstShop = m;
            long withheld = bm.getPostsWithheld();
            if (builders.getPostsOfferedShare() <= Construction.IDLE_PAYROLL_FLOOR + 1e-9 && withheld > 0) {
                idleMonths++;
                withheldInIdle += withheld;
                mostWithheld = Math.max(mostWithheld, withheld);
            }
        }
        return new Village(g, firstShop, idleMonths, withheldInIdle, mostWithheld, g.getPopulationManager().getPopulation());
    }

    static void handsForShops(Path root) {
        out.println("\n--- 6. a founded village has hands for its shops, where a depot would have held them ---");
        Village yard = playHandsOff(root, "hands-yard", false);
        Village depot = playHandsOff(root, "hands-depot", true);
        out.printf("   the yard's village, %d months: %d people, first shop at month %d, %d idle months, %.1f posts held back in an idle month (most %d)%n",
                FIRST_SHOP_MONTHS, yard.people(), yard.firstShop(), yard.idleMonths(), yard.meanWithheld(), yard.mostWithheld());
        out.printf("   the depot's twin, %d months: %d people, first shop at month %d, %d idle months, %.1f posts held back in an idle month (most %d)%n",
                FIRST_SHOP_MONTHS, depot.people(), depot.firstShop(), depot.idleMonths(), depot.meanWithheld(), depot.mostWithheld());
        assertTrue("the yard's village opened its first shop at month " + yard.firstShop() + ", within FIRST_SHOP_MONTHS ("
                + FIRST_SHOP_MONTHS + ")", yard.firstShop() > 0 && yard.firstShop() <= FIRST_SHOP_MONTHS);
        assertTrue("fixture: the yard's builders went idle at the core crew in " + yard.idleMonths()
                + " months and held posts back then (" + yard.mostWithheld() + " at most)",
                yard.idleMonths() > 0 && yard.mostWithheld() > 0);
        assertTrue("fixture: the depot's twin went idle at the core crew in " + depot.idleMonths() + " months and held posts back",
                depot.idleMonths() > 0 && depot.mostWithheld() > 0);
        assertTrue("the twin held back more posts in its idle months than the yard did (" + String.format("%.1f against %.1f",
                depot.meanWithheld(), yard.meanWithheld()) + ")", depot.meanWithheld() > yard.meanWithheld());
        assertTrue("...and opened no shop in the same " + FIRST_SHOP_MONTHS + " months, the reason the founding building is a yard",
                depot.firstShop() < 0);
    }

    /* ============================ 7. MONEY IS CONSERVED ============================ */

    static void conserved() {
        out.println("\n--- 7. money is conserved ---");
        out.printf("  %d months audited; the worst residual %.3g, the worst drift after the audit %.3g%n",
                monthsAudited, worstResidual, worstDrift);
        assertTrue("every month played here was audited", monthsAudited > 0);
        assertTrue("the money audit's worst month is within 0.01% of what moved", worstResidual < 1e-4);
        assertTrue("...and nothing moved a pool after it", Math.abs(worstDrift) < 1e-6);
    }
}
