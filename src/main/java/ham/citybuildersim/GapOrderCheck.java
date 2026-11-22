package ham.citybuildersim;

import ham.citybuildersim.sectors.Construction;
import ham.citybuildersim.sectors.FoodProcessing;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A planner orders to the gap, not one building a month (OG1, 2026-11-22).
 *
 * WHY. Jerus: "quite often construction has a huge backlog yet it doesnt
 * expand its building capacity to tackle it", and "in big cities, even if
 * building on depot every month its not enough, same with every other
 * sector". On main's playtest the builders held queues of 20 to 31 months for
 * 89 months running on one depot an order, and Food Processing imported up
 * to eight plants' worth while it ordered one. A first cut sized the depots
 * to the queue and ran the same playtest to 7,771 depots to main's 299 - the
 * runaway 0.7.130 measured - so the builders' order is sized to the work
 * the city sends them, and this harness holds both halves.
 *
 * WHAT THIS HAS TO PROVE, every fixture causing its condition:
 *
 *   1. THE BUILDERS' TARGET IS THE WORK COMING IN (Construction.
 *      sitesMonthWanted()): the typical month at the knee or under it; past
 *      it, the queue past the knee over QUEUE_WORKED_OFF_MONTHS more; and
 *      never more than 1 + BuildersShort.CEILING of the typical month however
 *      long the queue - the runaway's bound.
 *
 *   2. THE TYPICAL MONTH IS THE MEDIAN (BusinessInvestment.
 *      typicalMonthOfSiteWork()): one month's spike is worked off, not built
 *      for; under MIN_MONTHS_OF_SITE_WORK there is none, and the order is one
 *      depot, as before OG1.
 *
 *   3. THE RECORD IS THE WORK ORDERED, THE BUILDERS' OWN LEFT OUT: in a
 *      played city a month whose orders are houses records their points, and
 *      a month whose orders are depots records none - an order of depots is
 *      never read as work that calls for more.
 *
 *   4. ...AND A SAVE KEEPS IT, so a loaded city sizes its next order as the
 *      unbroken one does.
 *
 *   5. THE ORDER IS THE GAP: a city that has ordered twice its sites' month
 *      every month for a year, its queue past the knee - its own plan orders
 *      several depots, where one a month ordered one: no more points than the
 *      premium's share of the sites' month, the most that share, the staffing
 *      and the plots allow, and never the sites past 1 + CEILING of the work
 *      it orders a month; and it holds while they are on site.
 *
 *   6. THE TRAP STAYS AT ONE: BuildersFirstCheck's stalled city, given a year
 *      of orders that ask for depots it could staff, its sites short of a
 *      depot's points on the premium's share of a month, orders one - the
 *      premium's share the only cut, so the section fails without it.
 *
 *   7. A MAKER SHORT BY SEVERAL PLANTS ORDERS SEVERAL: Food Processing, the
 *      city eating k plants' worth of drinks - the n-th plant's fill counts
 *      the n - 1 before it, and the plan orders k.
 */
public class GapOrderCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void report(String label, boolean ok, String detail) {
        if (!ok) fails++;
        out.printf("%-96s %s  %s%n", label, ok ? "OK" : "FAIL", detail);
    }

    static boolean near(double a, double b) {
        return Math.abs(a - b) <= 1e-6 * Math.max(1, Math.max(Math.abs(a), Math.abs(b)));
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    static final String BUILDERS = Sectors.CONSTRUCTION;
    static final String DEPOT = "Construction Depot";
    static final String HOUSE = "House";
    static final String CLINIC = "Walk-in Clinic";

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        DeathCareFirstCheck.out = out;
        DeathCareFirstCheck.quiet = quiet;
        BuildersFirstCheck.out = out;
        BuildersFirstCheck.quiet = quiet;
        Path root = TempDirs.create("gapordercheck");

        target();
        Game g = city(root, "town");
        median(g);
        record(g);
        saved(root, g);
        theGap(g);
        trap(root);
        food(g);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE TARGET ============================ */

    static void target() {
        out.println("--- 1. the builders' target is the work coming in, and bounded however long the queue ---");
        double k = BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING;
        double s = 10_000, typical = 15_000;
        assertTrue("under the knee the target is the typical month",
                near(Construction.sitesMonthWanted(typical, 5 * s, s), typical));
        assertTrue("...and at the knee",
                near(Construction.sitesMonthWanted(typical, k * s, s), typical));
        double q = 21 * s;
        report("past it, the queue past the knee over QUEUE_WORKED_OFF_MONTHS more",
                near(Construction.sitesMonthWanted(typical, q, s), typical + (q - k * s) / Construction.QUEUE_WORKED_OFF_MONTHS),
                String.format("21 months: %,.0f", Construction.sitesMonthWanted(typical, q, s)));
        double most = typical * (1 + BuildersShort.CEILING);
        boolean bounded = true;
        for (double months : new double[] { 40, 100, 1_000, 1e6 }) {
            bounded &= Construction.sitesMonthWanted(typical, months * s, s) <= most * (1 + 1e-12);
        }
        report("never more than 1 + CEILING of the typical month, a queue of a million months and all", bounded,
                String.format("%,.0f at most", most));
        assertTrue("...and none at all with no work coming in", near(Construction.sitesMonthWanted(0, 1e9, s), 0));
    }

    /* ============================ THE FIXTURE ============================ */

    /**
     * A town of 800 houses and eight depots, every sector held, a year played: the builders' month is a few
     * thousand points, which the premium's share of can make several depots.
     */
    static Game city(Path root, String name) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.getEconomyManager().getTaxPolicy().reset();
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 120_000_000L);
            for (Sector s : g.getSectors().all()) g.getBusinessInvestment().holdSector(s.key());
            for (String[] w : new String[][] {
                    { "Industrial Bakery", "6" }, { "Wind Farm", "8" }, { "Water Treatment Plant", "2" },
                    { "Commercial Bank", "1" }, { "Convenience Store", "12" }, { HOUSE, "800" },
                    { "Elementary School", "6" }, { "Walk-in Clinic", "4" }, { DeathCareFirstCheck.CEMETERY, "1" },
                    { DEPOT, "8" }, { DeathCareFirstCheck.ROAD, "8" } }) {
                g.buildStack(DeathCareFirstCheck.template(g, w[0]), Integer.parseInt(w[1]), true);
            }
            g.simulateMonths(12);
        });
        BuildingManager bm = g.getBuildingManager();
        report("fixture: a town whose builders have a month of their own", g.getBuildingOutputAtEveryPost() > 0,
                String.format("month %d, %,d people, %.1f%% out of work, nameplate %,d, sites %,d a month, %,.0f owed",
                        g.getMonth(), g.getPopulationManager().getPopulation(),
                        g.getPopulationManager().getUnemploymentRate() * 100, bm.getTotalConstructionCapacity(),
                        g.getBuildingOutputAtEveryPost(), bm.getRemainingConstructionPoints()));
        return g;
    }

    /* ============================ 2. THE MEDIAN ============================ */

    static void median(Game g) {
        out.println("\n--- 2. the typical month is the median: a spike is worked off, not built for ---");
        BusinessInvestment plans = g.getBusinessInvestment();
        List<Double> kept = plans.getSiteWorkOrdered();
        Double owed = plans.getOthersOwedAfterAdvance();
        report("fixture: a played town has kept its months", kept.size() >= BusinessInvestment.MIN_MONTHS_OF_SITE_WORK
                && owed != null, kept.size() + " months kept");

        List<Double> spike = new ArrayList<>();
        for (int i = 0; i < BusinessInvestment.TREND_WINDOW - 1; i++) spike.add(1_000.0 + i);
        spike.add(18_000_000.0);
        plans.restoreSiteWork(spike, owed);
        report("eleven months of about a thousand and one of 18 million read as about a thousand",
                plans.typicalMonthOfSiteWork() < 1_100, String.format("%,.0f", plans.typicalMonthOfSiteWork()));

        List<Double> few = new ArrayList<>();
        for (int i = 0; i < BusinessInvestment.MIN_MONTHS_OF_SITE_WORK - 1; i++) few.add(5_000.0);
        plans.restoreSiteWork(few, owed);
        assertTrue("under MIN_MONTHS_OF_SITE_WORK there is no typical month", Double.isNaN(plans.typicalMonthOfSiteWork()));
        Construction builders = g.getSectors().construction();
        BuildingsTemplate depot = DeathCareFirstCheck.template(g, DEPOT);
        double s = g.getBuildingOutputAtEveryPost();
        assertTrue("...and a queue of a hundred months orders one depot, as before OG1",
                builders.depotsForTheQueue(depot, plans, s, 100 * s) == 1);
        plans.restoreSiteWork(kept, owed);
    }

    /* ============================ 3. THE RECORD ============================ */

    static void record(Game g) {
        out.println("\n--- 3. the record is the work ordered, the builders' own depots left out ---");
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate house = DeathCareFirstCheck.template(g, HOUSE);
        BuildingsTemplate depot = DeathCareFirstCheck.template(g, DEPOT);
        quietly(() -> g.simulateMonths(1));

        quietly(() -> g.buildStack(house, 10, false));
        double ordered = 10.0 * house.getConstructionPoints();
        quietly(() -> g.simulateMonths(1));
        List<Double> kept = plans.getSiteWorkOrdered();
        double last = kept.get(kept.size() - 1);
        report("a month whose orders were ten houses records their points", near(last, ordered),
                String.format("%,.0f recorded, %,.0f ordered", last, ordered));

        quietly(() -> g.buildStack(depot, 2, false));
        quietly(() -> g.simulateMonths(1));
        kept = plans.getSiteWorkOrdered();
        last = kept.get(kept.size() - 1);
        report("a month whose orders were two depots records none", near(last, 0),
                String.format("%,.0f recorded", last));
        quietly(() -> {
            for (int i = 0; i < 6 && g.getBuildingManager().getUnderConstructionBySector(BUILDERS) > 0; i++) g.simulateMonths(1);
        });
        assertTrue("fixture: the two depots opened", g.getBuildingManager().getUnderConstructionBySector(BUILDERS) == 0);
    }

    /* ============================ 4. THE SAVE ============================ */

    static void saved(Path root, Game g) {
        out.println("\n--- 4. a save keeps the record ---");
        BusinessInvestment plans = g.getBusinessInvestment();
        List<Double> kept = plans.getSiteWorkOrdered();
        Double owed = plans.getOthersOwedAfterAdvance();
        GameFiles.Result[] wrote = new GameFiles.Result[1];
        quietly(() -> wrote[0] = g.saveGame(11, "gapordercheck"));
        assertTrue("fixture: the town saved itself", wrote[0] != null && wrote[0].ok);
        Game back = new Game(new GameFiles(root.resolve("town"), root.resolve("town-no-legacy")));
        quietly(() -> { back.run(); back.loadGame(11); });
        BusinessInvestment again = back.getBusinessInvestment();
        assertTrue("the months kept came back, each to the point", again.getSiteWorkOrdered().equals(kept));
        assertTrue("...and what the sites owed when the last advance ended",
                owed != null && again.getOthersOwedAfterAdvance() != null && near(again.getOthersOwedAfterAdvance(), owed));
        assertTrue("...so the typical month reads the same", near(again.typicalMonthOfSiteWork(), plans.typicalMonthOfSiteWork()));
    }

    /* ============================ 5. THE GAP ============================ */

    static void theGap(Game g) {
        out.println("\n--- 5. the order is the gap: a year of orders past the sites, a queue past the knee, several depots ---");
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingManager bm = g.getBuildingManager();
        Construction builders = g.getSectors().construction();
        // The work coming in, caused: a year in which the city orders clinics of about twice its sites' month, month by month.
        double w = aYearOfOrders(g, (int) Math.ceil(2 * g.getBuildingOutputAtEveryPost()
                / DeathCareFirstCheck.template(g, CLINIC).getConstructionPoints()));
        double s = g.getBuildingOutputAtEveryPost();
        report("fixture: the typical month read is the clinics the city ordered each month", near(plans.typicalMonthOfSiteWork(), w),
                String.format("%,.0f read, %,.0f ordered a month, sites %,.0f a month", plans.typicalMonthOfSiteWork(), w, s));

        // ...and a queue whose premium's share of the month is three depots' points or more: the city's own orders,
        // penitentiaries of 62,000 points, placed after the last month, so the record has not read them.
        BuildingsTemplate big = DeathCareFirstCheck.template(g, "Penitentiary");
        double perDepot = DeathCareFirstCheck.template(g, DEPOT).getConstructionPoints();
        double months = BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING;
        while (months < 600 && BuildersShort.premium(months) * s < 3 * perDepot) months += 1;
        long prisons = (long) Math.ceil(Math.max(0, months * s - bm.getRemainingConstructionPoints()) / big.getConstructionPoints());
        quietly(() -> g.buildStack(big, prisons, false));
        double remaining = bm.getRemainingConstructionPoints();
        double share = BuildersShort.premium(remaining / s) * s;
        report("fixture: the queue is past the knee, its premium's share of the month three depots or more",
                remaining / s >= BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING && share >= 3 * perDepot,
                String.format("%,.0f owed on %,.0f a month = %.1f months, premium %.3f, its share %,.0f", remaining, s, remaining / s,
                        BuildersShort.premium(remaining / s), share));

        g.getBusinessInvestment().releaseSector(BUILDERS);
        BusinessInvestment.Decision d = builders.plan(plans, g);
        g.getBusinessInvestment().holdSector(BUILDERS);
        out.println("  the builders' plan: " + (d.build ? "build " + d.quantity + " " + d.template.getName() : "") + " - " + d.reason);
        report("their own plan builds", d.build && d.template != null, d.reason);
        if (!d.build || d.template == null) return;

        // The expectations are the brief's, read off the city: the ration (BA1, the premium's share of the sites' month),
        // the work the city orders a month (w, the harness's own clinics), and what it could staff and has plots for.
        BuildingsTemplate t = d.template;
        long n = d.quantity;
        double points = t.getConstructionPoints();
        double each = plans.newUnit(t).makes(Good.BUILDING_WORK) * Math.max(0, Math.min(1, builders.staffing(t).share));
        report("it orders several, where one a month ordered one", n > 1, n + " ordered");
        report("...their points no more than the premium's share of the sites' month",
                n * points <= share + 1e-9, String.format("%,.0f points of %,.0f", n * points, share));
        boolean oneMoreRefused = (n + 1) * points > share || builders.staffableCount(t, n + 1) < n + 1 || plans.plotsAvailableFor(t) < n + 1;
        report("...and the most that share, the staffing and the plots allow, the city's month asking for more",
                oneMoreRefused && s + n * each < w,
                String.format("%d depots would be %,.0f points; the sites %,.0f a month with them, the city orders %,.0f",
                        n + 1, (n + 1) * points, s + n * each, w));
        report("...nor take the sites past 1 + CEILING of the work the city orders a month",
                s + (n - 1) * each < (1 + BuildersShort.CEILING) * w,
                String.format("%,.0f a month with all but one, %,.0f at most", s + (n - 1) * each, (1 + BuildersShort.CEILING) * w));

        quietly(() -> g.buildStack(t, (int) n, false));
        g.getBusinessInvestment().releaseSector(BUILDERS);
        BusinessInvestment.Decision held = builders.plan(plans, g);
        g.getBusinessInvestment().holdSector(BUILDERS);
        report("with the order on site the plan holds", !held.build && held.reason.contains(Construction.ONE_DEPOT_ON_SITE), held.reason);
    }

    /**
     * A year of the city's own orders, the same each month: so many clinics placed by the city, then the month played,
     * the builders held. Returns the points ordered a month. The record reads them as the advance begins (section 3).
     */
    static double aYearOfOrders(Game g, int clinics) {
        BuildingsTemplate clinic = DeathCareFirstCheck.template(g, CLINIC);
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH);
            for (int i = 0; i < BusinessInvestment.TREND_WINDOW; i++) {
                g.buildStack(clinic, clinics, false);
                g.simulateMonths(1);
            }
        });
        return clinics * (double) clinic.getConstructionPoints();
    }

    /* ============================ 6. THE TRAP ============================ */

    /**
     * BuildersFirstCheck.stalled()'s city, given first a year of orders it cannot keep up with, so the typical month
     * is read and the order passes the record's guard: the work coming in asks for depots the city could staff, and
     * only the premium's share of the sites' month, under one depot's points, holds it at one.
     */
    static void trap(Path root) {
        out.println("\n--- 6. the trap stays at one: a year of orders, but the stalled city's sites are short of a depot on the premium's share ---");
        Game g = DeathCareFirstCheck.city(root, "trap");
        DeathCareFirstCheck.sites(g);
        g.getBuildingManager().setUnburied(() -> 0);
        g.getBuildingManager().setBuildersFirstForTest(true);
        double w = aYearOfOrders(g, 2);

        BusinessInvestment plans = g.getBusinessInvestment();
        Construction builders = g.getSectors().construction();
        BuildingsTemplate depot = DeathCareFirstCheck.template(g, DEPOT);
        double s = g.getBuildingOutputAtEveryPost();
        double q = g.getBuildingManager().getRemainingConstructionPoints();
        double each = plans.newUnit(depot).makes(Good.BUILDING_WORK) * Math.max(0, Math.min(1, builders.staffing(depot).share));
        report("fixture: the stalled city has a year of orders, the typical month the clinics it ordered",
                near(plans.typicalMonthOfSiteWork(), w), String.format("%,.0f read, %,.0f ordered a month", plans.typicalMonthOfSiteWork(), w));
        report("fixture: its queue past the knee and the premium's share under a depot",
                q / s >= BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING && BuildersShort.premium(q / s) * s < depot.getConstructionPoints(),
                String.format("sites %,.0f a month, %,.0f owed, the share %,.0f", s, q, BuildersShort.premium(q / s) * s));
        report("fixture: the work coming in asks for two depots or more, and the city could staff two",
                s + 2 * each <= w && builders.staffableCount(depot, 2L) == 2,
                String.format("sites %,.0f and %,.0f a depot against %,.0f ordered a month; %d of 2 staffable", s, each, w,
                        builders.staffableCount(depot, 2L)));

        g.getBusinessInvestment().releaseSector(BUILDERS);
        quietly(() -> g.simulateMonths(1));
        String plan = g.getLastInvestment(BUILDERS);
        report("its own plan ordered one depot, the premium's share the only cut", plan != null && plan.startsWith("Built 1 " + DEPOT), plan);
    }

    /* ============================ 7. A MAKER ============================ */

    /**
     * The one restored input in this harness. A Bottling Plant makes 60,000 kg of drinks a month and a person drinks
     * about 12 (consumption.json), so a town eating six plants' worth is about 30,000 people - a playtest, not a
     * fixture. The market's demand is set to six plants beyond what the city makes and has coming; the expectation is
     * that six, stated here, not the planner's own sum, once the fixture shows the city has the plots and could staff
     * six together.
     */
    static void food(Game g) {
        out.println("\n--- 7. a maker short by several plants orders several: Food Processing, the city eating k plants' drinks ---");
        FoodProcessing fp = g.getSectors().foodProcessing();
        BusinessInvestment plans = g.getBusinessInvestment();
        BuildingsTemplate bottling = DeathCareFirstCheck.template(g, "Bottling Plant");
        NewUnit unit = plans.newUnit(bottling);
        double made = unit.makes(Good.DRINKS);
        int k = 6;
        GoodsMarket drinks = g.getEconomyManager().getMarkets().get(Good.DRINKS);
        drinks.restoreStrike(0, 0, k * made + fp.getCapacity(Good.DRINKS) + fp.getPipeline(Good.DRINKS));
        report("fixture: the city eats six Bottling Plants' drinks beyond what it makes", unit.goodsMade().size() == 1 && made > 0,
                String.format("%,.0f a plant, %,.0f eaten", made, drinks.getDemand()));
        report("fixture: it has the plots for six and could staff six together",
                plans.plotsAvailableFor(bottling) >= k && fp.staffableCount(bottling, (long) k) == k,
                String.format("%d plots, %d of %d staffable", plans.plotsAvailableFor(bottling), fp.staffableCount(bottling, (long) k), k));
        boolean falls = true;
        for (int n = 1; n <= k + 1; n++) falls &= fp.fillFor(unit, n) <= fp.fillFor(unit, n - 1);
        assertTrue("the n-th plant's fill counts the n - 1 before it, so it falls with n", falls);
        report("the sixth is FILLED with five before it, the seventh is not",
                fp.fillFor(unit, k - 1) >= FoodProcessing.FILLED && fp.fillFor(unit, k) < FoodProcessing.FILLED,
                String.format("%.2f, %.2f", fp.fillFor(unit, k - 1), fp.fillFor(unit, k)));

        g.getBusinessInvestment().releaseSector(fp.key());
        BusinessInvestment.Decision d = fp.plan(plans, g);
        g.getBusinessInvestment().holdSector(fp.key());
        out.println("  the plan: " + (d.build ? "build " + d.quantity + " " + d.template.getName() : "") + " - " + d.reason);
        report("its plan orders the six, where one a month ordered one",
                d.build && d.template == bottling && d.quantity == k, d.reason);
    }
}
