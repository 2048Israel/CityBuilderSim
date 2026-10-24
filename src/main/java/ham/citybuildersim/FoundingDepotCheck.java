package ham.citybuildersim;

import ham.citybuildersim.sectors.Construction;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The founding depot (PT1-M4, 2026-10-12): the works department's free 400
 * points a month are retired and every city founds with one Construction Depot
 * standing, staffed and paid like any other, so the builders earn only for the
 * work their own people do - and keep the one depot they cannot sell.
 *
 * WHY. PT1's month 183 was the finding: $377k of a month at a 72% margin with
 * 0 workers in the sector's posts. BuildingManager.BASE_CONSTRUCTION was 400
 * points of building work with no posts behind them, paid to Construction as
 * revenue. Jerus: "not meant ... ya sure fix, perhaps a starting depot or
 * something instead". The constant is gone; Game.run() and newGame() place the
 * depot (Game.foundingDepot()); the builders' last depot is not sold by either
 * of the retirement rules nor bought out (Construction.mayRetire()); and the
 * staffing test of a new building counts the builders' withheld posts only as
 * far as their crews have filled them (Construction.crewsLaidOff()), because a
 * founded village whose depot was never staffed stalled at 146 people with no
 * shop in twenty years, where main's opened its first at month 13.
 *
 * What this has to prove:
 *   1. THE FOUNDING STANDS ONE DEPOT, through both doors (run() and
 *      newGame()): standing, none on site, the builders', the capacity its
 *      nameplate and no other points, and the land office holds its ground.
 *   2. NO DEPOT, NO POINTS: with the depot retired by fiat the builders'
 *      capacity and output are 0, and houses ordered owe what they owed, are
 *      not finished and are not recognised, month after month.
 *   3. MONTH 1 IS THE FIRST-MONTH CONVENTION, and month 2 on is worked by the
 *      crews the city has: the fill reads 1 until the first labour market,
 *      the depot works month 1 at its nameplate; from then the output is the
 *      nameplate x the posts offered share x the fill x the road and work
 *      ratios; and a founded city saved before its first month loads with one
 *      depot, its capacity and its ground.
 *   4. A FOUNDING TOWN BUILDS, AND EARNS ONLY FOR WORK ITS CREWS DO: the
 *      founding village stands within TOWN_MONTHS; once crews come the sites
 *      get work every month while anything is owed (T1-C's trap); a month
 *      whose crews are 0 has no output and recognises nothing; the builders'
 *      payroll is positive.
 *   5. THE LAST DEPOT IS NEVER SOLD: neither the spare-capacity rule nor the
 *      distress rule sells it, nor does the city buy it out; with a second
 *      standing, both sell at most all but one.
 *   6. A FOUNDED VILLAGE'S IDLE BUILDERS HOLD BACK ONLY THE CREWS THEY LAID
 *      OFF: the staffing test reserves the crews the builders laid off, not
 *      the posts nobody ever filled; the village opens its first shop within
 *      FIRST_SHOP_MONTHS; and a builder that did lay crews off holds them back.
 *   7. MONEY IS CONSERVED: the money audit every month played here.
 *
 * Every fixture causes its condition and says so in an assertion labelled
 * "fixture:". How a real run goes is LongPlaytest's.
 */
public class FoundingDepotCheck {

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

    /** The builders' depot: the one template that adds to their capacity. */
    static final String DEPOT = "Construction Depot";

    /** The founding bank, whose ground the depot's is added to. */
    static final String BANK = "Commercial Bank";

    /** The months a city with no depot is played: long enough for a month's work to show it is not done. */
    static final int NO_DEPOT_MONTHS = 3;

    /**
     * The months the founding village is given to stand: the two founding months nobody is there to work, then its
     * points at the core crew alone (Construction.IDLE_PAYROLL_FLOOR of the depot's nameplate a month, the least the
     * builders ever keep on). A village that stands inside it was built by the people the city had.
     */
    static int townMonths(Game g) {
        BuildingsTemplate depot = template(g, DEPOT);
        return 2 + (int) Math.ceil(villagePoints(g)
                / (depot.makes(Good.BUILDING_WORK) * Construction.IDLE_PAYROLL_FLOOR));
    }

    /**
     * The months a hands-off founded village is given to open its first shop: two years. Main's village of 0.8.34
     * opened its first at month 13, and this batch's, before the builders held back only the crews they laid off,
     * stalled with none in 240.
     */
    static final int FIRST_SHOP_MONTHS = 24;

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

    /** What the builders lay off, summed over the job types. */
    static long laidOff(Construction builders) {
        long total = 0;
        for (JobType job : JobType.values()) total += builders.crewsLaidOff(job);
        return total;
    }

    static long withheld(Construction builders) {
        long total = 0;
        for (JobType job : JobType.values()) total += builders.postsWithheld(job);
        return total;
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
        Path root = TempDirs.create("foundingdepotcheck");

        standing(root);
        noDepot(root);
        monthOne(root);
        town(root);
        lastDepot(root);
        idleBuilders(root);
        conserved();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE FOUNDING STANDS ONE DEPOT ============================ */

    static void standing(Path root) {
        out.println("--- 1. the founding stands one depot, through both doors ---");
        for (boolean byNewGame : new boolean[] { false, true }) {
            String door = byNewGame ? "newGame()" : "run()";
            City c = founded(root, byNewGame ? "door-new-game" : "door-run", byNewGame);
            Game g = c.game();
            BuildingManager bm = g.getBuildingManager();
            BuildingsTemplate depot = template(g, DEPOT), bank = template(g, BANK);
            check("a city founded by " + door + " stands one Construction Depot", bm.countByName(DEPOT), 1);
            check("...none on site", bm.underConstructionByName(DEPOT), 0);
            assertTrue("...owned by the builders (" + door + ")", depot.getSector().equals(Sectors.CONSTRUCTION)
                    && depot.makes(Good.BUILDING_WORK) > 0);
            check("...and the builders' capacity is that depot's nameplate, no other points (" + door + ")",
                    bm.getTotalConstructionCapacity(), (long) depot.makes(Good.BUILDING_WORK));
            check("...as the builders' own screen reads it (" + door + ")",
                    g.getSectors().construction().getConstructionCapacity(), depot.makes(Good.BUILDING_WORK), 1e-12);
            assertTrue("...with its posts standing: " + g.getSectors().construction().getPostsStanding() + " of the depot's "
                    + (depot.getJobs(JobType.NO_DIPLOMA) + depot.getJobs(JobType.DIPLOMA)),
                    g.getSectors().construction().getPostsStanding() == depot.getJobs(JobType.NO_DIPLOMA)
                            + depot.getJobs(JobType.DIPLOMA)
                            && depot.getJobs(JobType.NO_DIPLOMA) + depot.getJobs(JobType.DIPLOMA) > 0);
            assertTrue("...and the land office holds its ground beside the bank's (" + door + ")",
                    g.getLandManager().getAllocatedSqFt() + 1e-6 >= bank.getLandSqFt() + depot.getLandSqFt());
        }
    }

    /* ============================ 2. NO DEPOT, NO POINTS ============================ */

    static void noDepot(Path root) {
        out.println("\n--- 2. no depot, no points ---");
        City c = founded(root, "no-depot", false);
        Game g = c.game();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        BuildingsTemplate depot = template(g, DEPOT), house = template(g, "House");
        bm.retire(depot, 1);                       // the fixture: by fiat, past the guard that refuses a sale
        assertTrue("fixture: no depot stands", bm.countByName(DEPOT) == 0);
        check("with no depot the builders' capacity is 0", bm.getTotalConstructionCapacity(), 0);
        check("...and their output 0", g.getConstructionOutput(), 0);

        int ordered = villageCount("House");
        Game.BuildResult[] result = new Game.BuildResult[1];
        quietly(() -> result[0] = g.buildStack(house, ordered, false));
        double owed = bm.getRemainingConstructionPoints();
        int standingBefore = bm.countByName("House");
        assertTrue("fixture: " + ordered + " houses are ordered and the sites owe their points",
                result[0] == Game.BuildResult.SUCCESS && owed > 0);
        boolean unchanged = true, none = true;
        for (int m = 1; m <= NO_DEPOT_MONTHS; m++) {
            month(g);
            unchanged &= Math.abs(bm.getRemainingConstructionPoints() - owed) <= 1e-9 * owed;
            none &= builders.getRecognisedThisMonth() == 0;
        }
        assertTrue("the sites owe what they owed after " + NO_DEPOT_MONTHS + " months: nothing advanced them", unchanged);
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
        BuildingsTemplate depot = template(g, DEPOT);
        double nameplate = depot.makes(Good.BUILDING_WORK);
        assertTrue("fixture: nobody lives in the city at its founding", g.getPopulationManager().getPopulation() == 0);
        check("before the first labour market the builders' fill reads 1, as every employer's does",
                builders.getAverageFill(), 1.0, 1e-12);
        double roadAtFounding = g.getServicesManager().getRoadRatio(), workAtFounding = g.getHealth().getWorkRatio();
        check("...so the depot's output is its nameplate x the road ratio x the work ratio",
                g.getConstructionOutput(), Math.round(nameplate * roadAtFounding * workAtFounding), 0);

        assertTrue("fixture: the founding village is ordered before the first month", orderVillage(g));
        double owedBefore = bm.getRemainingConstructionPoints();
        int outputBefore = g.getConstructionOutput();
        assertTrue("fixture: the village owes more than the depot does in a month",
                owedBefore > outputBefore && outputBefore > 0);

        // The save before month 1, taken now, into the city's own folder: slot 10 of a scratch folder.
        quietly(() -> g.saveGame(10, "foundingdepot"));
        Game twin = new Game(c.files());
        quietly(() -> twin.loadGameSave(10));
        // The twin, loaded from the save before month 1: one depot, its capacity, its ground, its orders.
        BuildingManager tb = twin.getBuildingManager();
        check("the city reloaded from a save before its first month stands one Construction Depot (the load founds none more)",
                tb.countByName(DEPOT), 1);
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
        assertTrue("month 1 is worked at the depot's nameplate: the sites advanced, by no more than its output",
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
                Integer.parseInt(line[1]) + (line[0].equals(DEPOT) ? 1 : 0)));
        out.println("   standing at month " + g.getMonth() + ":" + end);
        if (standsAt < 0 || monthsWithCrews == 0) out.print(trace);
        assertTrue("every building of the village stood at once by month " + limit + " (the depot beside the founding one)", standsAt > 0);
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

    /** Whether every building of the founding village stands (the depot line: the founding's own and the ordered one). */
    static boolean villageStands(BuildingManager bm) {
        for (String[] line : Founding.VILLAGE) {
            int wanted = Integer.parseInt(line[1]) + (line[0].equals(DEPOT) ? 1 : 0);
            if (bm.countByName(line[0]) < wanted) return false;
        }
        return true;
    }

    /* ============================ 5. THE LAST DEPOT IS NEVER SOLD ============================ */

    static void lastDepot(Path root) {
        out.println("\n--- 5. the last depot is never sold ---");
        City c = founded(root, "last-depot", false);
        Game g = c.game();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        BuildingsTemplate depot = template(g, DEPOT);
        double nameplate = bm.getTotalConstructionCapacity();
        BusinessInvestment bi = new BusinessInvestment(bm, g.getEconomyManager());
        int months = Math.max(BusinessInvestment.RETIREMENT_LOSS_MONTHS, BusinessInvestment.DISTRESS_LOSS_MONTHS);
        for (int i = 0; i < months; i++) bi.recordSectorResult(builders.key(), -1);
        assertTrue("fixture: the builders have lost money for " + months + " months and stand one depot",
                bi.getLossMonths(builders.key()) >= months && bm.countByName(DEPOT) == 1);

        BusinessInvestment.Decision spare = bi.planRetirement(builders, 0, nameplate, 0);
        assertTrue("the spare-capacity rule does not sell the last depot, though nothing uses it", !spare.build);
        assertTrue("...and says why: " + spare.reason, spare.reason.equals(builders.noRetirementReason(false)));
        BusinessInvestment.Decision distress = bi.planDistressRetirement(builders, -1, 0);
        assertTrue("the distress rule does not sell it either, overdrawn and refused credit", !distress.build);
        assertTrue("...and says why: " + distress.reason, distress.reason.equals(builders.noRetirementReason(true)));
        check("the city may not buy the last depot out and demolish it", g.demolishable(depot), 0);

        quietly(() -> bm.addStack(depot, 1, true));
        int held = bm.countByName(DEPOT);
        assertTrue("fixture: a second depot stands", held == 2);
        spare = bi.planRetirement(builders, 0, bm.getTotalConstructionCapacity(), 0);
        assertTrue("with two standing the spare-capacity rule sells, at most all but one (" + spare.quantity + " of " + held + ")",
                spare.build && spare.quantity >= 1 && spare.quantity <= held - 1);
        distress = bi.planDistressRetirement(builders, -1, 0);
        assertTrue("...and so does the distress rule (" + distress.quantity + " of " + held + ")",
                distress.build && distress.quantity >= 1 && distress.quantity <= held - 1);
        check("...and the city may buy one of the two out", g.demolishable(depot), 1);
    }

    /* ============================ 6. A FOUNDED VILLAGE'S IDLE BUILDERS ============================ */

    /** The builders' saved extras, read through the base class (Sector.saveExtras() is protected, this package's): the harness's seam to the builders' memory of their crews. */
    static Map<String, Double> extrasOf(Construction builders) {
        Map<String, Double> extras = new HashMap<>();
        ((Sector) builders).saveExtras(extras);
        return extras;
    }

    static void idleBuilders(Path root) {
        out.println("\n--- 6. a founded village's idle builders hold back only the crews they laid off ---");
        City c = founded(root, "idle-village", false);
        Game g = c.game();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        JobType[] jobs = JobType.values();
        double[] held = new double[jobs.length];     // the most each job type's crews have filled, struck here from the city's own figures
        int firstShop = -1, idleMonths = 0, unfilled = 0, overHeld = 0, overWithheld = 0;
        for (int m = 1; m <= FIRST_SHOP_MONTHS; m++) {
            month(g);
            double[] fillRate = g.getPopulationManager().getJobFillRate();
            long[] offered = builders.postsOfferedPerTier();
            for (int i = 0; i < jobs.length; i++) held[i] = Math.max(held[i], offered[i] * fillRate[i]);
            if (firstShop < 0 && bm.countByName("Convenience Store") + bm.countByName("Small Grocery Store") > 0) firstShop = m;
            if (builders.getPostsOfferedShare() <= Construction.IDLE_PAYROLL_FLOOR + 1e-9 && bm.getPostsWithheld() > 0) {
                idleMonths++;
                for (int i = 0; i < jobs.length; i++) {
                    long laid = builders.crewsLaidOff(jobs[i]);
                    // ...at most what the crews ever filled beyond today's, and never more than the posts withheld.
                    if (laid > Math.max(0, Math.round(held[i] - offered[i] * fillRate[i]))) overHeld++;
                    if (laid > builders.postsWithheld(jobs[i])) overWithheld++;
                }
                if (laidOff(builders) < withheld(builders)) unfilled++;
            }
        }
        out.printf("   month %d: %d people, %d idle months, first shop at month %d; builders withhold %d posts, crews laid off %d%n",
                g.getMonth(), g.getPopulationManager().getPopulation(), idleMonths, firstShop, withheld(builders), laidOff(builders));
        assertTrue("fixture: the builders were idle (posts offered at the core crew, posts withheld) in " + idleMonths + " months",
                idleMonths > 0);
        assertTrue("fixture: in some idle month a withheld post was one nobody was ever laid off from (" + unfilled + " months)",
                unfilled > 0);
        assertTrue("in every idle month the crews laid off were at most what the crews ever filled beyond today's", overHeld == 0);
        assertTrue("...and never more than the posts withheld", overWithheld == 0);
        assertTrue("the village opened its first shop at month " + firstShop + ", within FIRST_SHOP_MONTHS (" + FIRST_SHOP_MONTHS + ")",
                firstShop > 0 && firstShop <= FIRST_SHOP_MONTHS);

        // A builder that did lay crews off holds them back: the builders are idle now, after a month of full crews.
        assertTrue("fixture: the builders are idle and have laid crews off (" + laidOff(builders) + " of " + withheld(builders)
                + " posts withheld)", builders.getPostsOfferedShare() <= Construction.IDLE_PAYROLL_FLOOR + 1e-9
                && withheld(builders) > 0 && laidOff(builders) > 0);
        BuildingsTemplate store = template(g, "Convenience Store");
        Sector.Staffing reserved = builders.staffing(store);
        Map<String, Double> memory = extrasOf(builders);
        Map<String, Double> forgotten = new HashMap<>(memory);
        forgotten.keySet().removeIf(k -> k.startsWith("crewsHeld."));
        ((Sector) builders).restoreExtras(forgotten);
        assertTrue("fixture: with the builders' memory of their crews cleared nobody is reserved", laidOff(builders) == 0);
        Sector.Staffing open = builders.staffing(store);
        ((Sector) builders).restoreExtras(memory);
        out.printf("   a Convenience Store's posts the city could staff: %.1f%% with the laid-off crews reserved, %.1f%% with none%n",
                reserved.share * 100, open.share * 100);
        assertTrue("fixture: the crews are back in the builders' memory", laidOff(builders) > 0);
        assertTrue("the staffing test counts the crews laid off as taken: a shop's staffable share is lower for them",
                reserved.share < open.share);

        // ...and the memory is saved: a city reloaded holds back the same crews.
        quietly(() -> g.saveGame(10, "idlevillage"));
        Game twin = new Game(c.files());
        quietly(() -> twin.loadGameSave(10));
        boolean same = true;
        for (JobType job : jobs) same &= twin.getSectors().construction().crewsLaidOff(job) == builders.crewsLaidOff(job);
        assertTrue("a reloaded city holds back the same crews as the one saved (" + laidOff(twin.getSectors().construction())
                + " of " + laidOff(builders) + ")", same);
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
