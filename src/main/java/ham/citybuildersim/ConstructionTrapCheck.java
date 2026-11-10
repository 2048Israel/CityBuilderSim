package ham.citybuildersim;

import ham.citybuildersim.sectors.Construction;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The builders short (FC1): what an order pays a point of work as the repairs leave the builders fewer points for the
 * sites, and the foreign contractor that takes the orders, builds the builders' own depots first and is paid abroad
 * when they leave none - held on a city whose repairs take the whole of the builders' month.
 *
 * WHY. Jerus's city, 2026-11-01 (docs/queue/FC1.md): "in a sufficiently big city, on a specific sequence of events, a
 * city could end up, where there are no crews to build, they are all repairing. and it gets stuck, no way to fix it".
 * The repairs take the builders' points before the sites, the builders' own depot waits in the order book for points
 * the repairs never leave (BuildersFirstCheck's stall, but now to the last point), and the price of a point did not read
 * how short they were. The model is BuildersShort (the arithmetic), BuildingManager (the multiple every order's work
 * pays), Game.foreignCrewsOffer() and settleForeignCrews() (the contractor's month) and sectors.Construction (its book
 * and its bill). This is the evidence that the trap is met, that the price answers it and that the contractor lets the
 * city out: DeathCareFirstCheck's city with enough standing roads that the repairs take all but a sliver of the month.
 *
 * What this has to prove:
 *   1. THE PRICE'S SHAPE (BuildersShort alone): no premium to the knee, strictly rising past it, half the ceiling at
 *      the knee and HALF_MONTHS past it, under the ceiling for the month counts a city can have and exactly it
 *      with no points for the sites at all; the contractor takes orders under the floor of the month and not at it,
 *      never with no month; his price is above anything the city's own premium reaches; his capacity never under
 *      a depot's points.
 *   2. THE TRAP, CAUSED: standing roads, one at a time past the model's own figure, until the repairs take more than
 *      all but FOREIGN_FLOOR of the builders' month at every post (one road fewer did not); the city's own sites and
 *      the landlords' Houses on site; the contractor takes orders and has the sites' orders on his book.
 *   3. THE PRICE RISES AS THE FREE POINTS FALL, in a twin that never met the trap: the quote of a House at five
 *      states - the free points ample (no premium, exactly the template's price), then the roads taking them, the
 *      months queued passing the knee and rising, and under the floor the contractor's price; every quote the
 *      multiple the model says, the planners' cost rising with it, and an order placed raising the next one's price.
 *   4. THE FOREIGN CREWS TAKE THE DEPOT FIRST: the builders' own depot ordered in the trap is priced at the
 *      contractor's price and served as their own depot, not put on his book a second time; it is built first out
 *      of the month by his points, he is paid for the points the sites took
 *      past the city's own month at FOREIGN_PRICE the base price of a point, and the builders' statement carries the
 *      bill as an import.
 *   5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS: the builders' own plan keeps opening depots, the sites at
 *      every post climb over the floor and the trap does not return, the contractor works off his book and no new
 *      order adds to it.
 *   6. THE MONEY: the sum of the contractor's bills is the sum of what the builders' statements carried as imports
 *      from him, each in the audit's TRADE scope, and nothing was paid abroad in a month he built nothing.
 *   7. A CITY THAT NEVER MEETS THE TRAP PAYS NOTHING ABROAD: free points and a queue under the knee for months
 *      played: no book, no points, no bill, the multiple exactly one.
 *   8. SAVED AND LOADED: the contractor's book and month come back from a save to the bit, and a city that never met
 *      the trap saves none of it.
 *   9. A CANCEL IN THE TRAP (FC1b): the city's site ordered while he takes orders, cancelled in one of two twins, leaves
 *      his book at the month's end by the points the order had left at the stop and nothing else of his month moves;
 *      a restart while he still takes orders puts them on once, never twice; and a depot of the builders' that goes
 *      first, which he never had on his book, takes none off it when it is cancelled.
 *  10. A RUSH BESIDE THE CONTRACTOR (FC1b): in a month he builds beside the city's own crews, a rushed site's
 *      overtime, its premium and the wages the city pays for it are struck on the part of its share the city's own
 *      crews worked, not on his; he is paid for the points the sites took past the city's own month, the overtime
 *      among it; and in a city with no contractor that part is the whole share, to the bit.
 *  11. SAVED MID-TRAP, THE NEXT MONTH THE SAME: a trapped city saved and loaded plays the month after as the twin that
 *      was never saved does - his book, points and bill, the builders' cash and order book, the points built, the
 *      price of an order and whether he takes them, to the bit.
 *  12. AN ORDER'S OWN BUILDINGS (BA1): with no cap on orders an order of six hundred homes is not priced at the first's queue; each
 *      building stands in the queue the ones before it leave, and the order pays the mean premium over them
 *      (BuildersShort.meanPremium()). One building is FC1's price to the bit (meanPremium(from, step, 1) == premium(from),
 *      orderPriceMultiplier(x) == orderPriceMultiplier(x, pointsEach, 1)); up to EXACT_SUM_MOST it is the explicit mean and past it
 *      the integral within the javadoc's stated figures of the exact sum, on the grid it was measured on; never above CEILING, under
 *      nothing or NaN; never falling as the order grows; and a quote of 600 homes on a queue near the knee costs more a home than
 *      a quote of one (and the contractor's price is the same for any size of order).
 *
 * Every fixture causes its condition. How often the trap is met in a real run is the ensemble's count (FC1.md).
 */
public class ConstructionTrapCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-100s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= Math.max(tol, Math.abs(expected) * tol);
        if (!ok) fails++;
        out.printf("%-100s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void bits(String label, double actual, double expected) {
        boolean ok = Double.doubleToLongBits(actual) == Double.doubleToLongBits(expected);
        if (!ok) fails++;
        out.printf("%-100s %s  %s against %s%n", label, ok ? "OK" : "FAIL", actual, expected);
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

    /** The builders' sector's key, the landlords' (whose Houses are the queue), and the templates the fixture orders. */
    static final String BUILDERS = Sectors.CONSTRUCTION;
    static final String RE = Sectors.REAL_ESTATE;
    static final String DEPOT = BuildersFirstCheck.DEPOT;
    static final String ROAD = DeathCareFirstCheck.ROAD;
    static final String HOUSE = "House";

    /** The landlords' Houses the trapped city has on site beside the city's own sites. */
    static final int LANDLORDS_HOUSES = 40;

    /** The queue of the twins that never met the trap, as a share of the knee in months of the free points they start with: well under it. */
    static final double CALM_SHARE_OF_KNEE = 0.8;

    /** The most months the climb out is played, and the months of quiet (no book, no points) that end it: the last bill is struck the month after. */
    static final int RUN_MONTHS = 60;
    static final int QUIET_MONTHS = 3;

    /** The months the twin that never met the trap is played. */
    static final int NEVER_MONTHS = 6;

    /** The slot the saved city is written to, in its own scratch folder (never the player's slots). */
    static final int SCRATCH_SLOT = 10;

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

    /** What one month played left: the trap as the month met it, the sites and the month at every post, the contractor's month, the statement's lines and the audit's. */
    record Row(int month, boolean trapped, double sites, double output, double points, double bill,
               double stmtForeign, double stmtImports, double auditImports, double tradeOut, double book) { }

    /** One month played, audited, and what it left, added to `into`. */
    static Row play(Game g, List<Row> into) {
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        boolean trapped = bm.foreignCrewsTakeOrders();
        month(g);
        MoneyAudit.Result r = g.getLastMoneyAudit();
        Row row = new Row(g.getMonth(), trapped, g.getBuildingOutputAtEveryPost(), g.getConstructionOutputAtEveryPost(),
                c.getForeignPointsThisMonth(), c.getForeignBillThisMonth(),
                c.statement().otherInputs.getOrDefault(Construction.FOREIGN_CREWS, 0.0), c.statement().imports,
                auditLine(r, "- " + BUILDERS + " Imports"), r == null ? 0 : r.tradeOut, c.getForeignBook());
        into.add(row);
        return row;
    }

    /** A line of the audit by its label, as the model recorded it (EP2: Result.figure(), not the detail's two printed decimals); 0 when it struck none.
     *  The 0.01 that stood beside the Imports line in the checks above, for the two printed places, is MoneyAudit.CENT now (tradeOut's was never read from text and stays). */
    static double auditLine(MoneyAudit.Result r, String label) {
        return r == null ? 0 : r.figure(label);
    }

    /** The files DeathCareFirstCheck.city() gives a city of that name, so a second Game can be pointed at its folder. */
    static GameFiles filesOf(Path root, String name) {
        return new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.CANADA);
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        // DeathCareFirstCheck's fixture builds the city, quietly, through its own streams.
        DeathCareFirstCheck.out = out;
        DeathCareFirstCheck.quiet = quiet;
        Path root = TempDirs.create("constructiontrapcheck");

        shape();
        // The trapped city is held in an array, so that it is let go before the twins that follow it are built.
        Game[] held = { trap(root) };
        priceRises(root);
        List<Row> rows = new ArrayList<>();
        depotFirst(held[0], rows);
        climbsOut(held[0], rows);
        held[0] = null;
        money(rows);
        Game never = never(root);
        saved(root, never);
        cancelInTheTrap(root);
        rushBesideTheContractor(root);
        nextMonthTheSame(root);
        ordersOwnBuildings(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE PRICE'S SHAPE ============================ */

    static void shape() {
        out.println("--- 1. the price's shape: the premium, the contractor's floor, his price and his capacity ---");
        double knee = BuildersShort.KNEE_MONTHS, half = BuildersShort.HALF_MONTHS, ceiling = BuildersShort.CEILING;
        assertTrue("the knee is the backlog the builders themselves expand at", knee == BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING);
        bits("no premium with nothing queued", BuildersShort.premium(0), 0);
        bits("...none just under the knee", BuildersShort.premium(Math.nextDown(knee)), 0);
        bits("...and none at it", BuildersShort.premium(knee), 0);
        bits("...a NaN reads as none", BuildersShort.premium(Double.NaN), 0);
        assertTrue("no jump at the knee: a millionth of a month past it, a hair of premium",
                BuildersShort.premium(knee + 1e-6) > 0 && BuildersShort.premium(knee + 1e-6) < 1e-9);
        double[] past = { 0.01, 0.1, 1, 6, half / 2, half, 2 * half, 4 * half, 10 * half, 50 * half, 500 * half, 5_000 * half };
        double previous = 0;
        boolean rising = true, under = true;
        for (double p : past) {
            double premium = BuildersShort.premium(knee + p);
            rising &= premium > previous;
            under &= premium < ceiling;
            previous = premium;
        }
        assertTrue("strictly rising past the knee, at every step from a day to a thousand years past it", rising);
        assertTrue("...and under the ceiling at every one of them: the months a city can have never reach it", under);
        close("half the ceiling at the knee and HALF_MONTHS", BuildersShort.premium(knee + half), ceiling / 2, 1e-12);
        bits("exactly the ceiling with the sites at nothing (infinite months)", BuildersShort.premium(Double.POSITIVE_INFINITY), ceiling);

        assertTrue("with no points for the sites the months queued are infinite, though a queue stands",
                Double.isInfinite(BuildersShort.monthsQueued(500, 0)));
        assertTrue("...and though nothing is queued", Double.isInfinite(BuildersShort.monthsQueued(0, 0)));
        bits("an empty queue over points for the sites is none", BuildersShort.monthsQueued(0, 5), 0);
        close("a queue is its points over the sites' month", BuildersShort.monthsQueued(50, 5), 10, 1e-12);
        bits("a NaN queue reads as none", BuildersShort.monthsQueued(Double.NaN, 5), 0);
        assertTrue("...a NaN month for the sites as none of them", Double.isInfinite(BuildersShort.monthsQueued(5, Double.NaN)));

        double month = 1_000, floor = BuildersShort.FOREIGN_FLOOR * month;
        assertTrue("the contractor takes orders just under FOREIGN_FLOOR of the month", BuildersShort.foreignTakesOrders(Math.nextDown(floor), month));
        assertTrue("...with the sites at nothing", BuildersShort.foreignTakesOrders(0, month));
        assertTrue("...not at the floor", !BuildersShort.foreignTakesOrders(floor, month));
        assertTrue("...nor over it, up to the whole month",
                !BuildersShort.foreignTakesOrders(Math.nextUp(floor), month) && !BuildersShort.foreignTakesOrders(month, month));
        assertTrue("...nor with no month at all, the sites at nothing too", !BuildersShort.foreignTakesOrders(0, 0));
        assertTrue("his price is above anything the city's own premium reaches: no city prefers him",
                BuildersShort.FOREIGN_PRICE > 1 + BuildersShort.CEILING);
        boolean multiples = true;
        for (double p : past) {
            multiples &= Double.doubleToLongBits(BuildersShort.multiplier(knee + p, true)) == Double.doubleToLongBits(BuildersShort.FOREIGN_PRICE);
            multiples &= Double.doubleToLongBits(BuildersShort.multiplier(knee + p, false)) == Double.doubleToLongBits(1 + BuildersShort.premium(knee + p));
        }
        multiples &= BuildersShort.multiplier(Double.POSITIVE_INFINITY, true) == BuildersShort.FOREIGN_PRICE
                && BuildersShort.multiplier(0, false) == 1;
        assertTrue("the multiple is FOREIGN_PRICE while he takes orders, else one and the premium", multiples);

        double depot = 400;
        close("his capacity is the depot's points when the builders' month is small", BuildersShort.foreignCapacity(100, depot), depot, 1e-12);
        close("...his share of a large month", BuildersShort.foreignCapacity(10_000, depot), BuildersShort.FOREIGN_CAPACITY_SHARE * 10_000, 1e-12);
        close("...the two meet where the share is the depot", BuildersShort.foreignCapacity(depot / BuildersShort.FOREIGN_CAPACITY_SHARE, depot), depot, 1e-9);
        boolean never = true;
        for (double o : new double[] { -50, 0, 1, 100, 1_600, 1_601, 1e6 }) never &= BuildersShort.foreignCapacity(o, depot) >= depot;
        assertTrue("...never under the depot's points, at any month of the builders'", never);
        bits("...and never negative with no depot", BuildersShort.foreignCapacity(-100, 0), 0);
    }

    /* ============================ 2. THE TRAP, CAUSED ============================ */

    /** What the road loop left: the roads added, and the sites at every post before and after the one that tipped it. */
    static int roadsAdded;
    static double sitesBeforeTheLastRoad, sitesAfterTheLastRoad;

    /** The repairs one standing Paved Road adds to the builders' month (as DeathCareFirstCheck.sites() has it). */
    static double perRoad(Game g) {
        BuildingsTemplate road = template(g, ROAD);
        return road.getConstructionPoints() * road.repairsPerYear() / 12;
    }

    /** n standing Paved Roads, as DeathCareFirstCheck.sites() builds them. */
    static void roads(Game g, int n) {
        if (n > 0) quietly(() -> g.buildStack(template(g, ROAD), n, true));
    }

    /**
     * DeathCareFirstCheck's city, then standing Paved Roads: two short of the model's own figure for the roads whose
     * repairs leave the sites the floor of the builders' month, and then one at a time until the contractor takes
     * orders - so the last road is the one that tips it, and the one before it did not.
     */
    static Game trapped(Path root, String name) {
        Game g = DeathCareFirstCheck.city(root, name);
        BuildingManager bm = g.getBuildingManager();
        double output = g.getConstructionOutputAtEveryPost();
        int near = (int) Math.floor((output * (1 - BuildersShort.FOREIGN_FLOOR) - g.getMaintenancePoints()) / perRoad(g)) - 2;
        roadsAdded = Math.max(0, near);
        roads(g, roadsAdded);
        sitesBeforeTheLastRoad = sitesAfterTheLastRoad = Double.NaN;
        while (!bm.foreignCrewsTakeOrders() && roadsAdded < Math.max(0, near) + 50) {
            sitesBeforeTheLastRoad = g.getBuildingOutputAtEveryPost();
            roads(g, 1);
            roadsAdded++;
            sitesAfterTheLastRoad = g.getBuildingOutputAtEveryPost();
        }
        return g;
    }

    /** ...and the sites DeathCareFirstCheck.sites() puts on it, less its cemetery: the city's University and Middle Schools, the landlords' Houses; nobody lies unburied. */
    static void sites(Game g) {
        BuildingManager bm = g.getBuildingManager();
        quietly(() -> {
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.buildStack(template(g, "University"), 1, false);
            g.buildStack(template(g, "Middle School"), 2, false);
            g.getEconomyManager().setSectorCash(RE, g.getEconomyManager().getSectorCash(RE) + 200_000);
            g.buildFor(g.getSectorInvestor(RE), template(g, HOUSE), LANDLORDS_HOUSES);
            bm.setUnburied(() -> 0);
        });
    }

    static Game trap(Path root) {
        out.println("\n--- 2. the trap, caused: the repairs take all but a sliver of the builders' month, every post offered ---");
        Game g = trapped(root, "trapped");
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        double output = g.getConstructionOutputAtEveryPost(), repairs = g.getMaintenancePoints();
        double floor = BuildersShort.FOREIGN_FLOOR * output;
        double queueBefore = bm.getRemainingConstructionPoints(), bookBefore = c.getForeignBook();
        sites(g);
        double sites = g.getBuildingOutputAtEveryPost();
        out.printf("  month %d: nameplate %,d, month at every post %,.0f, repairs %,.1f (%d roads added), sites %,.0f of a floor of %,.1f;"
                        + " queue %,.0f = %,.1f months%n",
                g.getMonth(), bm.getTotalConstructionCapacity(), output, repairs, roadsAdded, sites, floor,
                bm.getRemainingConstructionPoints(), bm.monthsQueued());
        assertTrue("fixture: the repairs take more than all but FOREIGN_FLOOR of the builders' month, every post offered",
                repairs > (1 - BuildersShort.FOREIGN_FLOOR) * output);
        assertTrue("...so the sites get under FOREIGN_FLOOR of it", sites < floor);
        assertTrue("...but only just: one road fewer left them at the floor or over it, the contractor not yet taking orders",
                sitesBeforeTheLastRoad >= floor && sitesAfterTheLastRoad < floor
                        && sitesBeforeTheLastRoad - sitesAfterTheLastRoad <= perRoad(g) + 1);
        assertTrue("fixture: the builders offer every post, as the trace's city did", g.getBuildingOutput() == g.getBuildingOutputAtEveryPost());
        assertTrue("fixture: the Penitentiary still owes most of its points (it keeps every post offered)",
                bm.stackOfKey(ConstructionControl.keyOf(template(g, "Penitentiary"))).getConstructionProgress()
                        < template(g, "Penitentiary").getConstructionPoints() / 2.0);
        assertTrue("fixture: work owed on site, the city's own and the landlords' Houses",
                bm.getRemainingConstructionPoints() > queueBefore && bm.citySiteKeys().size() >= 3
                        && bm.siteKeysOnSite().contains(ConstructionControl.keyOf(template(g, HOUSE))));
        assertTrue("the contractor takes orders", bm.foreignCrewsTakeOrders());
        bits("...every order's work at his price: the multiple is FOREIGN_PRICE", bm.orderPriceMultiplier(), BuildersShort.FOREIGN_PRICE);
        assertTrue("the months queued are far past the knee and the city's own premium is within a hair of its ceiling",
                bm.monthsQueued() > 100 * BuildersShort.KNEE_MONTHS && bm.scarcityPremium() > 0.99 * BuildersShort.CEILING);
        bits("fixture: nothing was on his book before the trap's sites (the Penitentiary was ordered before it)", bookBefore, 0);
        close("the sites ordered in the trap are on his book, point for point: what the queue gained", c.getForeignBook(),
                bm.getRemainingConstructionPoints() - queueBefore, 1e-9);
        return g;
    }

    /* ============================ 3. THE PRICE RISES AS THE FREE POINTS FALL ============================ */

    /**
     * A twin that never met the trap: the trap's city (DeathCareFirstCheck.city()) with its Penitentiary cancelled -
     * its queue was the only thing in the way of a premium of none - and, in its place, the landlords' Houses on site:
     * CALM_SHARE_OF_KNEE of the knee in months of the free points the builders have.
     */
    static Game calm(Path root, String name) {
        Game g = DeathCareFirstCheck.city(root, name);
        if (!g.cancelSite(ConstructionControl.keyOf(template(g, "Penitentiary")), true))
            throw new IllegalStateException("the Penitentiary's site cannot be cancelled");
        month(g);
        BuildingsTemplate house = template(g, HOUSE);
        int n = (int) Math.floor(CALM_SHARE_OF_KNEE * BuildersShort.KNEE_MONTHS * g.getBuildingOutputAtEveryPost()
                / house.getConstructionPoints());
        boolean[] ordered = new boolean[1];
        quietly(() -> {
            double price = g.quoteBuild(house, n).total;
            g.getEconomyManager().setSectorCash(RE, g.getEconomyManager().getSectorCash(RE) + 2 * price);
            ordered[0] = g.buildFor(g.getSectorInvestor(RE), house, n);
        });
        if (!ordered[0]) throw new IllegalStateException("the landlords' Houses could not be ordered");
        return g;
    }

    /** A House's quote at a state of the city: the free points, the months queued, the premium, the multiple, the sticker and the planners' cost. */
    record State(String name, double sites, double months, double premium, double multiple, double sticker,
                 double plannersCost, boolean foreign, double prepCost) { }

    static State state(Game g, String name) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate house = template(g, HOUSE);
        Game.BuildQuote q = g.quoteBuild(house, 1);
        State s = new State(name, g.getBuildingOutputAtEveryPost(), bm.monthsQueued(), bm.scarcityPremium(),
                bm.orderPriceMultiplier(), q.sticker, g.getBusinessInvestment().getCostOf(house, 1),
                bm.foreignCrewsTakeOrders(), q.prepCost);
        out.printf("  %-24s free points %,5.0f; queue %,6.0f = %,6.1f months; premium %.3f; multiple %.4f;"
                        + " a House's work %.4f (the template's %.4f); the planners' cost %.4f%n",
                name, s.sites, bm.getRemainingConstructionPoints(), s.months, s.premium, s.multiple, s.sticker,
                bm.nonMaterialCost(house), s.plannersCost);
        return s;
    }

    /** The standing roads that bring the months queued to `months` at the points the builders have now. */
    static int roadsFor(Game g, double months) {
        double free = g.getBuildingManager().getRemainingConstructionPoints() / months;
        double room = g.getConstructionOutputAtEveryPost() - g.getMaintenancePoints() - free;
        return (int) Math.max(0, Math.ceil(room / perRoad(g)));
    }

    static void priceRises(Path root) {
        out.println("\n--- 3. the price rises as the free points fall: a House's quote at five states of a city that never met the trap ---");
        Game g = calm(root, "calm");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate house = template(g, HOUSE);
        double template = bm.nonMaterialCost(house);
        double knee = BuildersShort.KNEE_MONTHS, half = BuildersShort.HALF_MONTHS;

        State s0 = state(g, "free points ample");
        assertTrue("fixture: the queue is under the knee at the points the builders have, the contractor not taking orders",
                s0.months <= knee && s0.months > 0 && !s0.foreign);
        assertTrue("fixture: flat ground, so the quote's work is the House's alone (no foothill to prepare)", s0.prepCost == 0);
        bits("no premium", s0.premium, 0);
        bits("...so the multiple is exactly one", s0.multiple, 1.0);
        bits("...and the quote's sticker is the template's work to the bit", s0.sticker, template);
        bits("...and the price a point earns the builders is the base price", bm.nonMaterialPricePerPoint(), bm.basePricePerPoint());

        List<State> states = new ArrayList<>(List.of(s0));
        double[] targets = { knee + half / 2, knee + half };
        for (int i = 0; i < targets.length; i++) {
            roads(g, roadsFor(g, targets[i]));
            states.add(state(g, "roads: " + (i == 0 ? "past the knee" : "the premium's half")));
        }
        // An order placed raises the price of the next: priced on the queue as it stands with the orders before it.
        State at = states.get(states.size() - 1);
        int houses = 20;
        double points = houses * house.getConstructionPoints();
        double multipleBefore = bm.orderPriceMultiplier(), aheadOfIt = bm.orderPriceMultiplier(points), monthsBefore = bm.monthsQueued();
        boolean[] ordered = new boolean[1];
        quietly(() -> {
            g.getEconomyManager().setSectorCash(RE, g.getEconomyManager().getSectorCash(RE) + 2 * g.quoteBuild(house, houses).total);
            ordered[0] = g.buildFor(g.getSectorInvestor(RE), house, houses);
        });
        assertTrue("fixture: " + houses + " Houses more ordered by the landlords", ordered[0]);
        out.printf("  an order of %d Houses (%,.0f points) at that state: the multiple %.4f before, %.4f after, %.4f for an order ahead of it%n",
                houses, points, multipleBefore, bm.orderPriceMultiplier(), aheadOfIt);
        assertTrue("an order placed raises the next order's price", bm.orderPriceMultiplier() > multipleBefore);
        close("...the months queued up by exactly its points over the free points", bm.monthsQueued() - monthsBefore, points / at.sites, 1e-6);
        close("...and the next is priced as an order after it in a run is: on the queue with its points ahead of it",
                bm.orderPriceMultiplier(), aheadOfIt, 1e-12);
        close("...at the premium of the queue as it now stands", bm.orderPriceMultiplier(), 1 + BuildersShort.premium(bm.monthsQueued()), 1e-12);

        roads(g, roadsFor(g, knee + 3 * half));
        states.add(state(g, "roads: the premium's most"));
        // ...and the roads one at a time, until the contractor takes the orders.
        int steps = 0;
        while (!bm.foreignCrewsTakeOrders() && steps++ < 50) roads(g, 1);
        State foreign = state(g, "under the floor");
        State last = states.get(states.size() - 1);
        states.add(foreign);

        for (int i = 1; i < states.size() - 1; i++) {
            State s = states.get(i);
            String where = " (" + s.name + ")";
            assertTrue("the free points fell" + where, s.sites < states.get(i - 1).sites);
            assertTrue("...past the knee, the contractor not taking orders" + where, s.months > knee && !s.foreign);
            close("...the multiple is one and the premium at the months queued" + where, s.multiple, 1 + BuildersShort.premium(s.months), 1e-12);
            close("...the quote's work over the template's is that multiple" + where, s.sticker / template, s.multiple, 1e-12);
            assertTrue("...and it is above the state before, strictly" + where, s.multiple > states.get(i - 1).multiple);
            assertTrue("...the planners' cost with it" + where, s.plannersCost > states.get(i - 1).plannersCost);
        }
        assertTrue("the premium's states rise in the months queued too", states.get(1).months < states.get(2).months
                && states.get(2).months < last.months);
        assertTrue("under the floor the contractor takes the orders, the state before it not", foreign.foreign && !last.foreign);
        bits("...the multiple is FOREIGN_PRICE", foreign.multiple, BuildersShort.FOREIGN_PRICE);
        close("...and the quote's work is FOREIGN_PRICE times the template's", foreign.sticker / template, BuildersShort.FOREIGN_PRICE, 1e-12);
        assertTrue("...above the city's own premium at its most, and above anything it could reach",
                foreign.multiple > last.multiple && foreign.multiple > 1 + BuildersShort.CEILING);
        assertTrue("...the planners' cost with it", foreign.plannersCost > last.plannersCost);
        assertTrue("...but the builders' own price of a point is the city's premium, never the contractor's multiple",
                bm.nonMaterialPricePerPoint() < BuildersShort.FOREIGN_PRICE * bm.basePricePerPoint());
        close("...which is the base with the city's own premium: the contractor's price pays abroad, not the builders", bm.nonMaterialPricePerPoint(),
                bm.basePricePerPoint() * (1 + bm.scarcityPremium()), 1e-12);
    }

    /* ============================ 4. THE FOREIGN CREWS TAKE THE DEPOT FIRST ============================ */

    static void depotFirst(Game g, List<Row> rows) {
        out.println("\n--- 4. the foreign crews take the builders' depot first: priced at his price, built out of his month as theirs, billed ---");
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        BuildingsTemplate depot = template(g, DEPOT);
        String key = ConstructionControl.keyOf(depot);
        double depotPoints = depot.getConstructionPoints();
        // The builders' own plan, as BuildersFirstCheck.stalled() releases it, would order this depot and the contractor build it
        // in the month it is ordered; the fixture orders it by hand (their sector held), so that what stood before the month can be read.
        Game.BuildQuote q = g.quoteBuild(depot, 1);
        close("the depot's order is priced at the contractor's: its work FOREIGN_PRICE times the template's", q.sticker,
                bm.nonMaterialCost(depot) * BuildersShort.FOREIGN_PRICE, 1e-12);
        double bookBefore = c.getForeignBook();
        int depotsBefore = bm.countByName(DEPOT);
        long nameplateBefore = bm.getTotalConstructionCapacity();
        boolean[] ordered = new boolean[1];
        quietly(() -> {
            g.getEconomyManager().setSectorCash(BUILDERS, g.getEconomyManager().getSectorCash(BUILDERS) + 2 * q.total + 1_000);
            ordered[0] = g.buildFor(g.getSectorInvestor(BUILDERS), depot, 1);
        });
        assertTrue("fixture: the builders order a Construction Depot, in the trap", ordered[0] && bm.foreignCrewsTakeOrders()
                && bm.underConstructionByName(DEPOT) == 1);
        assertTrue("...the builders' own depot on site, served first", bm.buildersOwnOnSite().contains(key)
                && !bm.isCitySite(key) && bm.addsToTheBuilders(key));
        close("...owing all its points", bm.buildersOwnOwed(), depotPoints, 1e-12);
        // Construction.bill(amount, points, t): a depot the builders serve first is the contractor's as theirs, not on his book a second time.
        close("its order is not put on the contractor's book too: he builds it as the builders' own depot", c.getForeignBook(), bookBefore, 1e-12);

        double base = bm.basePricePerPoint();
        double capacity = BuildersShort.foreignCapacity(g.getConstructionOutputAtEveryPost(), bm.largestDepotPoints());
        double work = c.getForeignBook() + bm.buildersOwnOwed();
        double offered = Math.round(Math.min(capacity, work));
        double own = g.getBuildingOutput();
        double owed = bm.buildersOwnOwed();
        out.printf("  before the month: the city's own month for the sites %,.0f; the contractor's capacity %,.0f, his work %,.0f, so %,.0f offered;"
                        + " his price a point %.4f (the base %.4f)%n", own, capacity, work, offered,
                BuildersShort.FOREIGN_PRICE * base, base);
        assertTrue("fixture: the contractor's capacity is not under a depot's points, and his work more than it",
                capacity >= bm.largestDepotPoints() && work > capacity);
        // His offer as the month will strike it (Game.foreignCrewsOffer(): idempotent here, the book already at most what is owed on site).
        Game.ForeignOffer offer = g.foreignCrewsOffer();
        close("his offer: the points are his capacity, the work being more than it", offer.points(), offered, 1e-12);
        close("...the builders' own depot's points are the part of it served first", offer.depotPoints(), Math.min(owed, offered), 1e-12);
        bits("...his price a point is FOREIGN_PRICE times the base price of a point", offer.pricePerPoint(), BuildersShort.FOREIGN_PRICE * base);

        Row first = play(g, rows);
        double pointsBuilt = bm.getPointsBuilt();
        out.printf("  the month: the contractor built %,.0f points, billed %,.2f; the sites built %,.0f in all; nameplate %,d -> %,d; the book %,.0f%n",
                first.points, first.bill, pointsBuilt, nameplateBefore, bm.getTotalConstructionCapacity(), c.getForeignBook());
        assertTrue("the contractor built this month: his points are more than none", first.points > 0);
        close("...what he was offered: his capacity, the depot's order the least of it", first.points, offered, 1e-12);
        close("...what the sites took past the city's own month", first.points, pointsBuilt - own, 1e-9);
        assertTrue("the depot was built first: it owed its points and took them, opened this month, owing nothing",
                bm.countByName(DEPOT) == depotsBefore + 1 && bm.underConstructionByName(DEPOT) == 0 && bm.buildersOwnOwed() == 0);
        close("...the builders' nameplate up by its points", bm.getTotalConstructionCapacity() - nameplateBefore, depotPoints, 1e-12);
        assertTrue("...the contractor's month went to it: the other sites took no more than the city's own month",
                pointsBuilt - owed <= own + 1e-9);
        // The month strikes its money constants at its top (Game.restrikeMoneyConstants(): the expected price level), before its
        // advance, so the base it struck is the one read here to a fraction of a percent, not to the bit (the offer above is to the bit).
        out.printf("  his price struck in the month: %.6f a point against the %.6f read before it%n", first.bill / first.points,
                BuildersShort.FOREIGN_PRICE * base);
        close("he is billed his points at FOREIGN_PRICE the base price of a point, as the month struck it", first.bill,
                first.points * BuildersShort.FOREIGN_PRICE * base, 5e-3);
        close("his book came down by what he built beyond the depot's part (the model's rule), the depot never on it",
                c.getForeignBook(), Math.max(0, bookBefore - Math.max(0, first.points - Math.min(owed, first.points))), 1e-9);

        // The builders' statement carries the bill as an import - struck the month after, with every site's book (SimulationEngine).
        Row second = play(g, rows);
        close("the builders' statement the month after carries that bill, named for the contractor, as an import", second.stmtForeign, first.bill, 1e-12);
        assertTrue("...in their imports line, which the audit's TRADE scope reads", second.stmtImports >= first.bill - 1e-9
                && second.auditImports >= first.bill - MoneyAudit.CENT && Math.abs(second.auditImports - second.stmtImports) < MoneyAudit.CENT);
        assertTrue("...and the city climbed out in the month: its own sites at every post over the floor, the contractor not taking orders",
                !bm.foreignCrewsTakeOrders() && first.sites >= BuildersShort.FOREIGN_FLOOR * first.output);
    }

    /* ============================ 5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS ============================ */

    static void climbsOut(Game g, List<Row> rows) {
        out.println("\n--- 5. the city climbs out and the contractor stops: depots open, the sites climb, the book is worked off ---");
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        int depotsAtTheTrap = bm.countByName(DEPOT) - 1;
        long nameplateAtTheTrap = bm.getTotalConstructionCapacity() - Math.round(template(g, DEPOT).getConstructionPoints());
        g.getBusinessInvestment().releaseSector(BUILDERS);
        int first = rows.size(), quietMonths = 0;
        String plan = null;
        for (int i = 0; i < RUN_MONTHS && quietMonths < QUIET_MONTHS; i++) {
            Row r = play(g, rows);
            if (i == 0) {
                plan = g.getLastInvestment(BUILDERS);
                out.println("  the builders' plan, released: " + plan);
            }
            quietMonths = c.getForeignBook() == 0 && r.points == 0 ? quietMonths + 1 : 0;
            if ((i + 1) % 12 == 0 || quietMonths == QUIET_MONTHS) {
                out.printf("  month %d: nameplate %,d (%d depots), free points %,.0f of %,.0f, queue %,.0f, the contractor %,.0f points, his book %,.0f%n",
                        r.month, bm.getTotalConstructionCapacity(), bm.countByName(DEPOT), r.sites, r.output,
                        bm.getRemainingConstructionPoints(), r.points, r.book);
            }
        }
        List<Row> run = rows.subList(first, rows.size());
        long trapMonths = rows.stream().filter(Row::trapped).count(), foreignMonths = rows.stream().filter(r -> r.points > 0).count();
        out.printf("  %d months played from the trap: %d met it, the contractor worked %d of them, and the nameplate went %,d -> %,d (%d depots opened)%n",
                rows.size(), trapMonths, foreignMonths, nameplateAtTheTrap, bm.getTotalConstructionCapacity(),
                bm.countByName(DEPOT) - depotsAtTheTrap);
        assertTrue("the builders' own plan, released, orders depots: the plan says so", plan != null && plan.startsWith("Built 1 " + DEPOT));
        assertTrue("the builders' depots open: the nameplate is up on the trap's, by more than the depot built by hand",
                bm.getTotalConstructionCapacity() > nameplateAtTheTrap + Math.round(template(g, DEPOT).getConstructionPoints())
                        && bm.countByName(DEPOT) > depotsAtTheTrap + 1);
        Row last = rows.get(rows.size() - 1);
        assertTrue("the city's own sites at every post are over FOREIGN_FLOOR of the month again", last.sites >= BuildersShort.FOREIGN_FLOOR * last.output);
        assertTrue("...the contractor no longer takes orders", !bm.foreignCrewsTakeOrders());
        assertTrue("...and the trap did not return in any month after the first it was over", rows.stream().skip(1).noneMatch(Row::trapped));
        assertTrue("the trap was met, and the contractor worked on past it, off his book", trapMonths >= 1 && foreignMonths > trapMonths);
        assertTrue("the run ended with the book worked off: " + QUIET_MONTHS + " months with none and no points", quietMonths == QUIET_MONTHS);
        bits("his book is empty", c.getForeignBook(), 0);
        boolean none = run.subList(run.size() - QUIET_MONTHS, run.size()).stream().allMatch(r -> r.points == 0 && r.bill == 0);
        assertTrue("from then his points and his bill are none, month after month", none && c.getForeignPointsThisMonth() == 0
                && c.getForeignBillThisMonth() == 0);
        // ...and no new order adds to the book now the city's own crews have points for sites.
        double bookBefore = c.getForeignBook();
        BuildingsTemplate house = template(g, HOUSE);
        boolean[] ordered = new boolean[1];
        quietly(() -> {
            g.getEconomyManager().setSectorCash(RE, g.getEconomyManager().getSectorCash(RE) + 2 * g.quoteBuild(house, 10).total);
            ordered[0] = g.buildFor(g.getSectorInvestor(RE), house, 10);
        });
        assertTrue("fixture: the landlords order ten Houses more", ordered[0] && bm.underConstructionByName(HOUSE) >= 10);
        bits("no new order adds to his book: it is as it was", c.getForeignBook(), bookBefore);
        assertTrue("...and the multiple is the city's own premium, under the contractor's", bm.orderPriceMultiplier() < BuildersShort.FOREIGN_PRICE);
    }

    /* ============================ 6. THE MONEY ============================ */

    static void audited(String where) {
        out.printf("  %d months audited %s; the worst residual %.3g, the worst drift after the audit %.3g%n",
                monthsAudited, where, worstResidual, worstDrift);
        assertTrue("every month played " + where + " was audited, the worst within 0.01% of what moved", monthsAudited > 0 && worstResidual < 1e-4);
        assertTrue("...and nothing moved a pool after it", Math.abs(worstDrift) < 1e-6);
    }

    static void money(List<Row> rows) {
        out.println("\n--- 6. the money: his bills are the builders' imports from him, in the audit's TRADE scope; none in a month he built nothing ---");
        double billed = 0, carried = 0, billedMonths = 0;
        boolean next = true, onTheAudit = true, none = true, imports = true;
        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            billed += i < rows.size() - 1 ? r.bill : 0;
            if (r.bill > 0) billedMonths++;
            none &= (r.points > 0) == (r.bill > 0);
            if (i == 0) {
                none &= r.stmtForeign == 0;   // nothing carried from a month before he worked
                continue;
            }
            Row before = rows.get(i - 1);
            carried += r.stmtForeign;
            next &= Math.abs(r.stmtForeign - before.bill) <= 1e-9 * Math.max(1, before.bill);
            imports &= r.stmtImports >= r.stmtForeign - 1e-9;
            onTheAudit &= r.tradeOut >= r.stmtForeign - 0.01 && r.auditImports >= r.stmtForeign - MoneyAudit.CENT
                    && Math.abs(r.auditImports - r.stmtImports) < MoneyAudit.CENT;
            // a month he built nothing in leaves nothing abroad the month after
            if (before.points == 0) none &= r.stmtForeign == 0;
        }
        out.printf("  %d months, the contractor billed in %.0f of them; his bills %,.2f in all, the statements' lines %,.2f%n",
                rows.size(), billedMonths, billed, carried);
        assertTrue("the contractor worked and was paid: bills in some months", billedMonths > 0 && billed > 0);
        assertTrue("every month's statement carries the month before's bill as its import from him (struck a month on, as the sites' book is)", next);
        close("the sum of his bills is the sum of what the statements carried from him", carried, billed, 1e-9);
        assertTrue("it is in their imports line, every month", imports);
        assertTrue("...and in the audit's TRADE scope: its imports line for the builders is the statement's, the trade out at least that", onTheAudit);
        assertTrue("nothing was paid abroad in a month he built nothing: no bill, and no line the month after", none);
        audited("so far");
    }

    /* ============================ 7. A CITY THAT NEVER MEETS THE TRAP ============================ */

    static Game never(Path root) {
        out.println("\n--- 7. a city that never meets the trap pays nothing abroad: free points, a queue under the knee ---");
        Game g = calm(root, "never");
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        List<Row> rows = new ArrayList<>();
        boolean underTheKnee = true, one = true, free = true;
        for (int i = 0; i < NEVER_MONTHS; i++) {
            underTheKnee &= bm.monthsQueued() <= BuildersShort.KNEE_MONTHS && !bm.foreignCrewsTakeOrders();
            one &= Double.doubleToLongBits(bm.orderPriceMultiplier()) == Double.doubleToLongBits(1.0);
            Row r = play(g, rows);
            free &= r.points == 0 && r.bill == 0 && r.stmtForeign == 0 && !r.trapped
                    && !g.getSectors().construction().statement().otherInputs.containsKey(Construction.FOREIGN_CREWS);
        }
        out.printf("  %d months played, the queue %,.0f points = %,.1f months of the free points %,.0f%n", NEVER_MONTHS,
                bm.getRemainingConstructionPoints(), bm.monthsQueued(), (double) g.getBuildingOutputAtEveryPost());
        assertTrue("fixture: every month began with the queue under the knee and the contractor not taking orders", underTheKnee);
        assertTrue("...and the multiple exactly one in every month", one);
        assertTrue("...and the Houses were being built: the queue is shorter", bm.getRemainingConstructionPoints() > 0
                && rows.get(rows.size() - 1).book == 0);
        bits("his book is empty", c.getForeignBook(), 0);
        bits("his points this month are none", c.getForeignPointsThisMonth(), 0);
        bits("his bill this month is none", c.getForeignBillThisMonth(), 0);
        assertTrue("no month's points, bill or statement line abroad in any of them, and no line for him on the builders' inputs", free);
        audited("including the twin's");
        return g;
    }

    /* ============================ 8. SAVED AND LOADED ============================ */

    static void saved(Path root, Game never) throws Exception {
        out.println("\n--- 8. saved and loaded: the contractor's book and month come back to the bit; a city that never met the trap saves none ---");
        Game g = trapped(root, "saved");
        sites(g);
        List<Row> rows = new ArrayList<>();
        play(g, rows);
        play(g, rows);
        Construction c = g.getSectors().construction();
        GameFiles files = filesOf(root, "saved");
        quietly(() -> g.saveGame(SCRATCH_SLOT, "constructiontrapcheck"));
        Game loaded = new Game(files);
        quietly(() -> loaded.loadGameSave(SCRATCH_SLOT));
        Construction back = loaded.getSectors().construction();
        out.printf("  saved mid-episode: his book %,.2f, his month %,.2f points, billed %,.2f%n", c.getForeignBook(),
                c.getForeignPointsThisMonth(), c.getForeignBillThisMonth());
        assertTrue("fixture: saved with the contractor mid-episode: a book, a month of points, a bill",
                c.getForeignBook() > 0 && c.getForeignPointsThisMonth() > 0 && c.getForeignBillThisMonth() > 0);
        assertTrue("fixture: the save loaded", loaded.getMonth() == g.getMonth());
        bits("the book comes back to the bit", back.getForeignBook(), c.getForeignBook());
        bits("...the month's points", back.getForeignPointsThisMonth(), c.getForeignPointsThisMonth());
        bits("...and its bill", back.getForeignBillThisMonth(), c.getForeignBillThisMonth());
        String text = Files.readString(files.saveFile(SCRATCH_SLOT));
        assertTrue("the save carries them, where the sector keeps its extras", text.contains("foreignBook")
                && text.contains("foreignPointsThisMonth") && text.contains("foreignBillThisMonth"));

        GameFiles neverFiles = filesOf(root, "never");
        quietly(() -> never.saveGame(SCRATCH_SLOT, "constructiontrapcheck-never"));
        String clean = Files.readString(neverFiles.saveFile(SCRATCH_SLOT));
        assertTrue("a city that never met the trap saves what it always saved: none of the contractor's keys",
                !clean.contains("foreignBook") && !clean.contains("foreignPointsThisMonth") && !clean.contains("foreignBillThisMonth"));
        audited("in all");
    }

    /* ============================ 9. A CANCEL IN THE TRAP ============================ */

    /** The Construction Depots the city orders in the trap: more points than the contractor is offered, so one is still on site when the month ends and the cancel has something to stop. */
    static final int CITY_DEPOTS = 2;

    /** The refund the month's stop made for the building of that name (ConstructionControl.Refund), or null when it stopped none. */
    static ConstructionControl.Refund refundOf(Game g, String building) {
        for (ConstructionControl.Refund r : g.getControlThisMonth().refunds) if (r.building().equals(building)) return r;
        return null;
    }

    /** Two trapped cities that stand alike to the bit: the free points, the repairs, the queue, his book and the treasury's cash, and he takes orders in both. */
    static void sameStart(String where, Game a, Game b) {
        BuildingManager x = a.getBuildingManager(), y = b.getBuildingManager();
        Construction cx = a.getSectors().construction(), cy = b.getSectors().construction();
        assertTrue("fixture: the twins start the same " + where + ": the month, and the contractor takes orders in both",
                a.getMonth() == b.getMonth() && x.foreignCrewsTakeOrders() && y.foreignCrewsTakeOrders());
        bits("...the free points for the sites", a.getBuildingOutputAtEveryPost(), b.getBuildingOutputAtEveryPost());
        bits("...the repairs", a.getMaintenancePoints(), b.getMaintenancePoints());
        bits("...the queue", x.getRemainingConstructionPoints(), y.getRemainingConstructionPoints());
        bits("...his book", cx.getForeignBook(), cy.getForeignBook());
        bits("...and the treasury's cash", a.getCash(), b.getCash());
    }

    static void cancelInTheTrap(Path root) {
        out.println("\n--- 9. a cancel in the trap: the order's points leave the contractor's book when it stops, and a restart puts them on once ---");
        Game kept = trapped(root, "cancel-kept"), gone = trapped(root, "cancel-gone");
        sameStart("at the trap", kept, gone);
        BuildingManager bk = kept.getBuildingManager(), bg = gone.getBuildingManager();
        Construction ck = kept.getSectors().construction(), cg = gone.getSectors().construction();
        BuildingsTemplate university = template(kept, "University");
        String key = ConstructionControl.keyOf(university);
        double queueBefore = bk.getRemainingConstructionPoints(), bookBefore = ck.getForeignBook();
        sites(kept);
        sites(gone);
        sameStart("with the sites ordered", kept, gone);
        double onTheBook = ck.getForeignBook() - bookBefore;
        out.printf("  the sites ordered in the trap: the queue %,.0f -> %,.0f, his book %,.0f -> %,.0f (the University is %,d points)%n",
                queueBefore, bk.getRemainingConstructionPoints(), bookBefore, ck.getForeignBook(), university.getConstructionPoints());
        assertTrue("fixture: the University is the city's own site, ordered while the contractor took orders (he takes them still)",
                bg.isCitySite(key) && bg.foreignCrewsTakeOrders() && bk.foreignCrewsTakeOrders());
        close("...and its points are on his book: the book gained what the queue gained, point for point", onTheBook,
                bk.getRemainingConstructionPoints() - queueBefore, 1e-9);
        assertTrue("...with more on it than the University's points", onTheBook >= university.getConstructionPoints());

        assertTrue("the city cancels the University in one twin", gone.cancelSite(key, true) && bg.getControl().isCancelling(key)
                && !bk.getControl().isCancelling(key));
        List<Row> keptRows = new ArrayList<>(), goneRows = new ArrayList<>();
        Row month = play(kept, keptRows), stoppedMonth = play(gone, goneRows);
        ConstructionControl.Refund refund = refundOf(gone, university.getName());
        assertTrue("fixture: the cancel stopped at the month's end, with points left on site, a depot served first it was not",
                refund != null && refund.points() > 0 && refund.points() <= university.getConstructionPoints() && !refund.servedFirst());
        assertTrue("...and the twin that kept it stopped nothing", refundOf(kept, university.getName()) == null);
        ConstructionControl.Shell shell = bg.getControl().shellOf(university.getId());
        assertTrue("...leaving its shell", shell != null && shell.buildings == refund.buildings());
        out.printf("  the month in both: he built %,.0f points, billed %,.2f; the stop found %,.2f of the University's %,d points left;"
                        + " his book %,.2f where it was kept, %,.2f where it was cancelled%n", month.points, month.bill, refund.points(),
                university.getConstructionPoints(), ck.getForeignBook(), cg.getForeignBook());
        bits("the month's advance was the same in both: the points the sites took", bg.getPointsBuilt(), bk.getPointsBuilt());
        bits("...the contractor's points", stoppedMonth.points, month.points);
        bits("...and his bill", stoppedMonth.bill, month.bill);
        close("the Refund's points are the shell's: the University's points less the progress it kept", refund.points(),
                shell.buildings * (double) university.getConstructionPoints() - shell.progress, 1e-9);
        close("his book is the other twin's less the University's points left at the stop", cg.getForeignBook(),
                ck.getForeignBook() - refund.points(), 1e-9);

        // ...restarted while he takes orders: the shell's points go on his book once.
        double stopped = cg.getForeignBook();
        assertTrue("fixture: he still takes orders when the shell is restarted", bg.foreignCrewsTakeOrders());
        boolean[] restarted = new boolean[1];
        quietly(() -> restarted[0] = gone.restartShell(university.getId()));
        assertTrue("fixture: the restart is booked, the University the city's own site again", restarted[0] && bg.isCitySite(key)
                && gone.getBuildingManager().getControl().shellOf(university.getId()) == null);
        out.printf("  restarted: his book %,.2f -> %,.2f, the twin that never cancelled %,.2f; the queue %,.0f against %,.0f%n", stopped,
                cg.getForeignBook(), ck.getForeignBook(), bg.getRemainingConstructionPoints(), bk.getRemainingConstructionPoints());
        close("a restart puts its points on his book once: the book is the twin's that never cancelled", cg.getForeignBook(),
                ck.getForeignBook(), 1e-9);
        close("...by exactly the points the order had left", cg.getForeignBook() - stopped, refund.points(), 1e-9);
        assertTrue("...and not the billed twice case: the twin's book plus the order's points, as before FC1b",
                Math.abs(cg.getForeignBook() - (ck.getForeignBook() + refund.points())) > refund.points() / 2);
        close("...and the queue is the twin's again", bg.getRemainingConstructionPoints(), bk.getRemainingConstructionPoints(), 1e-9);

        cancelADepotFirst(root);
        audited("including the cancel's twins");
    }

    /** A depot of the builders' ordered by the city and served first: he never had it on his book, so cancelling it takes nothing off. */
    static void cancelADepotFirst(Path root) {
        out.println("  a Construction Depot the city orders in the trap, served first as the builders' own, and cancelled:");
        Game kept = trapped(root, "depot-kept"), gone = trapped(root, "depot-gone");
        sameStart("at the trap", kept, gone);
        sites(kept);
        sites(gone);
        BuildingManager bk = kept.getBuildingManager(), bg = gone.getBuildingManager();
        Construction ck = kept.getSectors().construction(), cg = gone.getSectors().construction();
        BuildingsTemplate depot = template(kept, DEPOT);
        String key = ConstructionControl.keyOf(depot);
        double bookBefore = ck.getForeignBook();
        Game.BuildResult[] ordered = new Game.BuildResult[2];
        quietly(() -> {
            kept.setCashForTest(Founding.WEALTHY_CASH);
            ordered[0] = kept.buildStack(template(kept, DEPOT), CITY_DEPOTS, false);
            gone.setCashForTest(Founding.WEALTHY_CASH);
            ordered[1] = gone.buildStack(template(gone, DEPOT), CITY_DEPOTS, false);
        });
        assertTrue("fixture: the city orders " + CITY_DEPOTS + " Construction Depots of its own in the trap, in both twins",
                ordered[0] == Game.BuildResult.SUCCESS && ordered[1] == Game.BuildResult.SUCCESS && bk.foreignCrewsTakeOrders()
                        && bk.underConstructionByName(DEPOT) == CITY_DEPOTS && bg.underConstructionByName(DEPOT) == CITY_DEPOTS
                        && bg.isCitySite(key));
        assertTrue("...served first, as the builders' own depots, in both twins", bk.goesFirst(depot, 0)
                && bg.goesFirst(template(gone, DEPOT), 0) && bg.buildersOwnOnSite().contains(key) && bk.buildersOwnOnSite().contains(key));
        bits("...and not on his book: the order left it as it was", ck.getForeignBook(), bookBefore);
        Game.ForeignOffer offer = kept.foreignCrewsOffer();
        assertTrue("fixture: the depots are more points than he is offered, and part of his offer: one is left unbuilt at the month's end",
                offer.depotPoints() > 0 && offer.points() < CITY_DEPOTS * depot.getConstructionPoints());

        assertTrue("the city cancels its depots in one twin", gone.cancelSite(key, true));
        List<Row> keptRows = new ArrayList<>(), goneRows = new ArrayList<>();
        Row month = play(kept, keptRows), stoppedMonth = play(gone, goneRows);
        ConstructionControl.Refund refund = refundOf(gone, DEPOT);
        out.printf("  the month in both: he built %,.0f points of the %,.0f offered; the stop found %,.2f of the depots' points left (served first: %s);"
                        + " his book %,.2f kept, %,.2f cancelled%n", month.points, (double) offer.points(), refund == null ? 0 : refund.points(),
                refund == null ? "no stop" : refund.servedFirst(), ck.getForeignBook(), cg.getForeignBook());
        assertTrue("fixture: the cancel stopped a depot served first, with points left on site", refund != null && refund.servedFirst()
                && refund.points() > 0);
        assertTrue("fixture: he has a book to fall from, in both twins", ck.getForeignBook() > 0 && cg.getForeignBook() > 0);
        bits("the month's advance was the same in both: his points", stoppedMonth.points, month.points);
        bits("...and his bill", stoppedMonth.bill, month.bill);
        bits("his book does not fall when the depot is cancelled: it is the twin's, a depot served first never having been on it",
                cg.getForeignBook(), ck.getForeignBook());
    }

    /* ============================ 10. A RUSH BESIDE THE CONTRACTOR ============================ */

    /** The overtime of the rushed site the month struck, with the plan it was struck in. */
    static ConstructionControl.Overtime overtimeOf(Game g, String key) {
        BuildingManager.Plan plan = g.getBuildingManager().getLastPlan();
        return plan == null ? null : plan.overtimeOf(key);
    }

    static void rushBesideTheContractor(Path root) {
        out.println("\n--- 10. a rush beside the contractor: overtime and its premium struck on the city's own crews' part of the share alone ---");
        Game g = trapped(root, "rush");
        sites(g);
        BuildingManager bm = g.getBuildingManager();
        Construction c = g.getSectors().construction();
        int siteOutput = g.getBuildingOutput();
        Game.ForeignOffer offer = g.foreignCrewsOffer(), again = g.foreignCrewsOffer();
        assertTrue("fixture: the contractor builds this month (his offer is more than none) and the city's own crews have site output too",
                offer.points() > 0 && siteOutput > 0);
        assertTrue("...his offer read before the month is the same read twice, so reading it changes nothing",
                again.points() == offer.points() && again.depotPoints() == offer.depotPoints()
                        && Double.doubleToLongBits(again.pricePerPoint()) == Double.doubleToLongBits(offer.pricePerPoint()));
        BuildingManager.Plan before = bm.plan(siteOutput + offer.points(), offer.points());
        String key = null;
        for (String k : bm.citySiteKeys()) {
            if (key == null && before.shareOf(k) > 0 && before.shareOf(k) < before.owedOf(k)) key = k;
        }
        assertTrue("fixture: a city site whose share of the month is over none and under what it owes", key != null);
        String rushed = key;
        out.printf("  before the month: the city's own %,d points for the sites, his offer %,d; the plan's whole month %,.0f;"
                        + " %s's share %,.2f of the %,.0f it owes%n", siteOutput, offer.points(), before.output, bm.nameOfSite(rushed),
                before.shareOf(rushed), before.owedOf(rushed));
        assertTrue("the city rushes it", g.rushSite(rushed, true) && bm.getControl().isRushed(rushed));
        List<Row> rows = new ArrayList<>();
        Row month = play(g, rows);
        BuildingManager.Plan plan = bm.getLastPlan();
        ConstructionControl.Overtime o = overtimeOf(g, rushed);
        assertTrue("fixture: the month worked the rushed site on overtime, in a plan struck on the whole month: the city's points and his",
                o != null && plan.output == siteOutput + offer.points());
        double owed = plan.owedOf(rushed), overtimeOutput = ConstructionControl.overtimeOutput(o.month());
        out.printf("  the month: share %,.3f, of it the city's own crews' %,.3f; work %,.3f (month %d on overtime, x%.4f); premium %,.4f at %.6f a point;"
                        + " the treasury paid %,.4f%n", o.share(), o.own(), o.work(), o.month(), overtimeOutput, o.premium(), o.perPoint(),
                g.getOvertimePaidThisMonth());
        assertTrue("fixture: the contractor's crews worked part of the share: the city's own part is under the share and over none",
                o.own() < o.share() && o.own() > 0);
        assertTrue("fixture: overtime is more work than a normal month in this month of the rush, and the site did not finish on it",
                overtimeOutput > 1 && o.work() < owed);
        close("the city's own crews' part of the share is the share over the whole month, less his points, of it", o.own(),
                o.share() * (plan.output - offer.points()) / plan.output, 1e-12);
        assertTrue("fixture: the builders' wages a point are over none", o.perPoint() > 0);
        close("the premium is the premium share of the wage bill on the city's own part alone, not on the share", o.premium(),
                ConstructionControl.premiumShare() * o.own() * o.perPoint(), 1e-12);
        assertTrue("...under what the whole share would have cost", o.premium() < ConstructionControl.premiumShare() * o.share() * o.perPoint());
        close("the work is his part of the share as it is and the city's part on overtime, at most what the site owes", o.work(),
                Math.min(owed, (o.share() - o.own()) + o.own() * overtimeOutput), 1e-12);
        bits("...the plan's work for the site is the overtime's", plan.workOf(rushed), o.work());
        close("the extra the month worked is the work over the share, the city's crews' hours alone", plan.overtimePoints,
                o.work() - o.share(), 1e-12);
        assertTrue("...under the extra the whole share would have worked", plan.overtimePoints < o.share() * (overtimeOutput - 1));
        assertTrue("fixture: the builders were paid overtime this month", c.getOvertimeThisMonth() > 0);
        bits("the builders' overtime is what the treasury paid", c.getOvertimeThisMonth(), g.getOvertimePaidThisMonth());
        close("...the premium on the city's own part, with the builders' tax on it", g.getOvertimePaidThisMonth(),
                o.premium() / (1 - Math.max(0, Math.min(TaxPolicy.MAX_INCOME_TAX, g.getEconomyManager().buildersSalesRate()))), 1e-9);
        double pastTheCitysMonth = bm.getPointsBuilt() - (siteOutput + g.getControlThisMonth().overtimePoints);
        out.printf("  the sites took %,.3f points in all: the city's own month %,d and its overtime %,.3f, and past them %,.3f;"
                        + " he built %,.0f of his %,d and billed %,.2f%n", bm.getPointsBuilt(), siteOutput, g.getControlThisMonth().overtimePoints,
                pastTheCitysMonth, month.points, offer.points(), month.bill);
        bits("the month's extra points are the plan's", g.getControlThisMonth().overtimePoints, plan.overtimePoints);
        close("he is paid for the points the sites took past the city's own month, its overtime among it (his offer, in full)",
                month.points, Math.min(offer.points(), Math.max(0, pastTheCitysMonth)), 1e-9);
        close("...which is what he was offered: the sites took the whole month", month.points, offer.points(), 1e-9);
        // As section 4: the month restrikes its money constants at its top, so his price is the offer's read before it to a
        // fraction of a percent, not to the bit.
        close("...at FOREIGN_PRICE the base price of a point, his offer's price as the month struck it", month.bill,
                month.points * offer.pricePerPoint(), 5e-3);

        // ...and in a city with no contractor the whole share is the city's own: the month it always was.
        Game calm = calm(root, "rush-calm");
        BuildingManager bc = calm.getBuildingManager();
        quietly(() -> {
            calm.setCashForTest(Founding.WEALTHY_CASH);
            calm.buildStack(template(calm, "University"), 1, false);
        });
        String calmKey = ConstructionControl.keyOf(template(calm, "University"));
        int calmOutput = calm.getBuildingOutput();
        BuildingManager.Plan calmBefore = bc.plan(calmOutput, 0);
        assertTrue("fixture: the twin that never met the trap has no contractor at work, and the city's own crews have site output",
                !bc.foreignCrewsTakeOrders() && calm.foreignCrewsOffer().points() == 0 && calmOutput > 0);
        assertTrue("...and the University's share is over none and under what it owes", calmBefore.shareOf(calmKey) > 0
                && calmBefore.shareOf(calmKey) < calmBefore.owedOf(calmKey));
        assertTrue("the city rushes it there too", calm.rushSite(calmKey, true));
        List<Row> calmRows = new ArrayList<>();
        play(calm, calmRows);
        ConstructionControl.Overtime co = overtimeOf(calm, calmKey);
        assertTrue("fixture: the month worked the rushed site on overtime, and no contractor built in it",
                co != null && calm.getSectors().construction().getForeignPointsThisMonth() == 0);
        out.printf("  the twin with no contractor: share %,.3f, the city's own %,.3f; work %,.3f; premium %,.4f%n", co.share(), co.own(),
                co.work(), co.premium());
        bits("with no contractor the city's own part is the whole share, to the bit", co.own(), co.share());
        bits("...the premium is the premium share of the wage bill on the share", co.premium(),
                ConstructionControl.premiumShare() * co.share() * co.perPoint());
        bits("...and the work is the share on overtime, at most what the site owes", co.work(),
                Math.min(bc.getLastPlan().owedOf(calmKey), co.share() * ConstructionControl.overtimeOutput(co.month())));
        audited("including the rush's twins");
    }

    /* ============================ 11. SAVED MID-TRAP, THE NEXT MONTH THE SAME ============================ */

    static void nextMonthTheSame(Path root) throws Exception {
        out.println("\n--- 11. saved mid-trap, the next month the same: the reloaded city plays the month as the twin that was never saved does ---");
        Game g = trapped(root, "next");
        sites(g);
        List<Row> rows = new ArrayList<>();
        play(g, rows);
        play(g, rows);
        Construction c = g.getSectors().construction();
        assertTrue("fixture: saved mid-episode: a book, a month of points and a bill, and he takes orders still",
                c.getForeignBook() > 0 && c.getForeignPointsThisMonth() > 0 && c.getForeignBillThisMonth() > 0
                        && g.getBuildingManager().foreignCrewsTakeOrders());
        quietly(() -> g.saveGame(SCRATCH_SLOT, "constructiontrapcheck-next"));
        Game loaded = new Game(filesOf(root, "next"));
        quietly(() -> loaded.loadGameSave(SCRATCH_SLOT));
        // The fixture holds every sector (DeathCareFirstCheck.city()) so no planner builds but by this harness's hand; a hold is a
        // harness lever and a save does not carry it (BusinessInvestment.holdSector()), so the reloaded city is held as the original is.
        for (Sector s : loaded.getSectors().all()) loaded.getBusinessInvestment().holdSector(s.key());
        BuildingManager bm = g.getBuildingManager(), bl = loaded.getBuildingManager();
        Construction cl = loaded.getSectors().construction();
        assertTrue("fixture: the save loaded", loaded.getMonth() == g.getMonth());
        assertTrue("fixture: the reloaded city is held as the original is: no planner of either asks to build",
                loaded.getSectors().all().stream().allMatch(s -> loaded.getBusinessInvestment().isHeld(s.key())
                        && g.getBusinessInvestment().isHeld(s.key())));
        bits("before the month: his book", cl.getForeignBook(), c.getForeignBook());
        bits("...the builders' cash", loaded.getEconomyManager().getSectorCash(BUILDERS), g.getEconomyManager().getSectorCash(BUILDERS));
        bits("...their order book's backlog points", cl.getBacklogPoints(), c.getBacklogPoints());
        bits("...and its unearned revenue", cl.getUnearnedRevenue(), c.getUnearnedRevenue());
        bits("...the price of an order", bl.orderPriceMultiplier(), bm.orderPriceMultiplier());
        assertTrue("...and he takes orders in both", bm.foreignCrewsTakeOrders() && bl.foreignCrewsTakeOrders());

        List<Row> original = new ArrayList<>(), reloaded = new ArrayList<>();
        Row twin = play(g, original), back = play(loaded, reloaded);
        out.printf("  the month after: the original built %,.0f points, billed %,.4f, the sites %,.3f in all, the book %,.2f;"
                        + " the reloaded %,.0f, %,.4f, %,.3f, %,.2f%n", twin.points, twin.bill, bm.getPointsBuilt(), c.getForeignBook(),
                back.points, back.bill, bl.getPointsBuilt(), cl.getForeignBook());
        assertTrue("fixture: he built in the month, in the city that was never saved", twin.points > 0 && twin.bill > 0);
        bits("the next month's contractor's book comes out the same in the reloaded city", cl.getForeignBook(), c.getForeignBook());
        bits("...his points", back.points, twin.points);
        bits("...his bill", back.bill, twin.bill);
        bits("...the builders' cash", loaded.getEconomyManager().getSectorCash(BUILDERS), g.getEconomyManager().getSectorCash(BUILDERS));
        bits("...their order book's backlog points", cl.getBacklogPoints(), c.getBacklogPoints());
        bits("...and its unearned revenue", cl.getUnearnedRevenue(), c.getUnearnedRevenue());
        bits("...the points the sites built", bl.getPointsBuilt(), bm.getPointsBuilt());
        bits("...the price of an order", bl.orderPriceMultiplier(), bm.orderPriceMultiplier());
        assertTrue("...and whether he takes orders", bl.foreignCrewsTakeOrders() == bm.foreignCrewsTakeOrders());
        audited("including the reloaded city's");
    }

    /* ============================ 12. AN ORDER'S OWN BUILDINGS (BA1) ============================ */

    /** The sums an order of n buildings is priced by, in the order the model sums them (BuildersShort.meanPremium() up to EXACT_SUM_MOST). */
    static double explicitMean(double from, double step, long n) {
        double sum = 0;
        for (long i = 0; i < n; i++) sum += BuildersShort.premium(from + i * step);
        return Math.max(0, Math.min(BuildersShort.CEILING, sum / n));
    }

    /** The tolerance meanPremium()'s javadoc states for the integral past EXACT_SUM_MOST, by the step in months (its MEASURED figures). */
    static double integralTolerance(double stepMonths) {
        if (stepMonths < 0.1) return 1e-6;      // a home at a city's pace: a part in a million
        if (stepMonths <= 2) return 3e-5;       // up to two months
        if (stepMonths <= 22) return 3.4e-4;    // a materials plant behind a founding city's yard
        return 2.8e-3;                          // a hundred
    }

    /**
     * AN ORDER'S OWN BUILDINGS (BA1). With no cap on orders (BusinessInvestment.MAX_CONCURRENT_ORDERS and MAX_ORDER_MONTHS went),
     * an order of six hundred homes would take years of the queue at the price of the first. Each of its buildings is priced on
     * the queue the ones before it leave: the mean of the premium over the n queues it stands in (BuildersShort.meanPremium()).
     * The arithmetic is asked alone, over the grid the javadoc was measured on, then on a real city's quote: one building is
     * FC1's price to the bit; the exact sum up to EXACT_SUM_MOST and the integral past it, within the javadoc's stated figures
     * of the exact sum; never above the ceiling, never under nothing, never NaN; rising with the order; and an order of 600
     * homes on a queue near the knee costs more a home than one.
     */
    static void ordersOwnBuildings(Path root) {
        out.println("\n--- 12. an order's own buildings (BA1) ---");
        double knee = BuildersShort.KNEE_MONTHS, ceiling = BuildersShort.CEILING;
        double[] froms = { 0, 5, 9, 20, 60, 200, 2000 };
        double[] steps = { 0.05, 0.5, 2, 22, 100 };
        long[] counts = { 65, 100, 1000, 100_000 };

        // One building is FC1's price, to the bit - for any step, and for no step, and for no number of buildings.
        boolean oneBits = true, noStepBits = true;
        for (double from : froms) {
            for (double step : steps) {
                oneBits &= Double.doubleToLongBits(BuildersShort.meanPremium(from, step, 1)) == Double.doubleToLongBits(BuildersShort.premium(from));
                oneBits &= Double.doubleToLongBits(BuildersShort.multiplier(from, step, 1, false))
                        == Double.doubleToLongBits(BuildersShort.multiplier(from, false));
            }
            for (double step : new double[] { 0, -1, Double.NaN }) {
                noStepBits &= Double.doubleToLongBits(BuildersShort.meanPremium(from, step, 1000)) == Double.doubleToLongBits(BuildersShort.premium(from));
            }
            noStepBits &= Double.doubleToLongBits(BuildersShort.meanPremium(from, 2, 0)) == Double.doubleToLongBits(BuildersShort.premium(from));
        }
        assertTrue("meanPremium(from, step, 1) is premium(from) to the bit, and multiplier(from, step, 1) is FC1's multiplier(from), on the grid"
                + " of queues from nothing to 2,000 months and steps from a twentieth of a month to a hundred", oneBits);
        assertTrue("...and for no step (zero, under nothing, not a number) or no building at all, whatever the order's size: premium(from) too", noStepBits);
        bits("...a queue already endless is the ceiling, for any order", BuildersShort.meanPremium(Double.POSITIVE_INFINITY, 2, 1000), BuildersShort.premium(Double.POSITIVE_INFINITY));
        bits("...and the ceiling itself", BuildersShort.premium(Double.POSITIVE_INFINITY), ceiling);

        // Up to EXACT_SUM_MOST buildings it is the explicit mean of the premium over the queues they stand in.
        boolean explicit = true;
        double worstExact = 0;
        int cases = 0;
        for (double from : froms) {
            for (double step : steps) {
                for (long n : new long[] { 2, 3, 10, 33, 63, BuildersShort.EXACT_SUM_MOST }) {
                    double mean = BuildersShort.meanPremium(from, step, n), want = explicitMean(from, step, n);
                    explicit &= Double.doubleToLongBits(mean) == Double.doubleToLongBits(want);
                    worstExact = Math.max(worstExact, Math.abs(mean - want));
                    cases++;
                }
            }
        }
        assertTrue("for an order of up to EXACT_SUM_MOST (" + BuildersShort.EXACT_SUM_MOST + ") the mean premium is the explicit mean over the"
                + " queues its buildings stand in, to the bit (" + cases + " orders, worst " + worstExact + ")", explicit);

        // Past it, the integral: within the javadoc's stated figures of the exact sum, by the step.
        boolean within = true;
        double worst = 0;
        String where = "";
        int past = 0;
        double[] worstByStep = new double[steps.length];
        for (double from : froms) {
            for (int s = 0; s < steps.length; s++) {
                double step = steps[s];
                double sum = 0;
                long done = 0;
                for (long n : counts) {
                    for (; done < n; done++) sum += BuildersShort.premium(from + done * step);
                    double exact = Math.max(0, Math.min(ceiling, sum / n));
                    double got = BuildersShort.meanPremium(from, step, n);
                    double miss = Math.abs(got - exact);
                    past++;
                    worstByStep[s] = Math.max(worstByStep[s], miss);
                    if (miss > integralTolerance(step)) {
                        within = false;
                        where = String.format("from %.0f, step %.2f, n %,d: %.3e against %.3e", from, step, n, miss, integralTolerance(step));
                    }
                    if (miss > worst) worst = miss;
                }
            }
        }
        out.printf("  %d orders past EXACT_SUM_MOST against the exact sum: the integral misses by at most", past);
        for (int s = 0; s < steps.length; s++) out.printf(" %.1e (step %s)", worstByStep[s], steps[s]);
        out.printf("%n");
        assertTrue("past EXACT_SUM_MOST the integral is within the javadoc's stated figures of the exact sum: 1e-6 for a home's step, 3e-5 up to"
                + " two months, 3.4e-4 at 22, 2.8e-3 at a hundred (orders of 65 to 100,000 on queues of nothing to 2,000 months)"
                + (within ? "" : " - " + where), within);

        // Bounds: never above the ceiling, never under nothing, never NaN - for ordinary orders and for the extremes.
        boolean bounded = true;
        double[] wild = { -5, 0, 5, 9, 9.0000001, 33, 1e6, 1e300, Double.MAX_VALUE, Double.POSITIVE_INFINITY, Double.NaN };
        double[] wildSteps = { Double.NEGATIVE_INFINITY, -1, 0, 1e-12, 0.05, 2, 100, 1e300, Double.POSITIVE_INFINITY, Double.NaN };
        double biggest = 0, smallest = 1;
        for (double from : wild) {
            for (double step : wildSteps) {
                for (long n : new long[] { Long.MIN_VALUE, -1, 0, 1, 2, 64, 65, 1000, 1_000_000_000L, Long.MAX_VALUE }) {
                    double p = BuildersShort.meanPremium(from, step, n);
                    bounded &= !Double.isNaN(p) && p >= 0 && p <= ceiling;
                    biggest = Math.max(biggest, p);
                    smallest = Math.min(smallest, p);
                    double m = BuildersShort.multiplier(from, step, n, false);
                    bounded &= !Double.isNaN(m) && m >= 1 && m <= 1 + ceiling;
                    bounded &= BuildersShort.multiplier(from, step, n, true) == BuildersShort.FOREIGN_PRICE;
                }
            }
        }
        for (double from : froms) {
            for (double step : steps) {
                for (long n : counts) {
                    double p = BuildersShort.meanPremium(from, step, n);
                    bounded &= !Double.isNaN(p) && p >= 0 && p <= ceiling;
                }
            }
        }
        out.printf("  %d odd and ordinary orders: the mean premium from %s to %s, the ceiling %s%n", wild.length * wildSteps.length * 10, smallest, biggest, ceiling);
        assertTrue("the mean premium is never above CEILING, never under nothing and never NaN - and the multiplier is between 1 and 1 + CEILING,"
                + " or FOREIGN_PRICE where the contractor takes the order, whatever the queue, the step or the size", bounded);

        // Rising with the order: more buildings stand in longer queues, so the mean never falls; across the seam at EXACT_SUM_MOST by the stated tolerance only.
        boolean rises = true, seam = true, dearer = true;
        for (double from : froms) {
            for (double step : steps) {
                double prev = BuildersShort.meanPremium(from, step, 1);
                for (long n = 2; n <= BuildersShort.EXACT_SUM_MOST; n++) {
                    double m = BuildersShort.meanPremium(from, step, n);
                    rises &= m >= prev - 1e-12;
                    prev = m;
                }
                double atSeam = BuildersShort.meanPremium(from, step, BuildersShort.EXACT_SUM_MOST + 1);
                seam &= atSeam >= prev - integralTolerance(step);
                prev = atSeam;
                for (long n = BuildersShort.EXACT_SUM_MOST + 2; n <= 3_000; n++) {
                    double m = BuildersShort.meanPremium(from, step, n);
                    rises &= m >= prev - 1e-12;
                    prev = m;
                }
                // ...and strictly dearer for a hundred buildings than for one wherever the queue they stand in reaches past the knee.
                if (from + 99 * step > knee && BuildersShort.premium(from + 99 * step) - BuildersShort.premium(from) > 4 * integralTolerance(step)) {
                    dearer &= BuildersShort.meanPremium(from, step, 100) > BuildersShort.premium(from)
                            && BuildersShort.meanPremium(from, step, 100) < BuildersShort.premium(from + 99 * step);
                }
            }
        }
        assertTrue("the mean premium never falls as the order grows, from one building to 64 and from 65 to 3,000, for every queue and step", rises);
        assertTrue("...across the seam at EXACT_SUM_MOST it falls by no more than the integral's stated tolerance", seam);
        assertTrue("...and an order of a hundred standing in a queue that reaches past the knee pays more than the first building alone but less than the last",
                dearer);

        // On a real city's quote.
        Game g = calm(root, "calm-ba1");
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate house = template(g, HOUSE);
        double points = house.getConstructionPoints();
        double nmc = bm.nonMaterialCost(house);
        State s0 = state(g, "calm, free points ample");
        assertTrue("fixture: the queue is under the knee, the contractor not taking orders", s0.months <= knee && !s0.foreign);

        // The one-argument multiple is FC1's: exactly the three-argument one with one building.
        boolean bitsEqual = true;
        double[] aheads = { 0, 1, 100, 5_000, 250_000, 1e7 };
        double[] eachs = { 0, points, 400, 5_000 };
        bitsEqual &= Double.doubleToLongBits(bm.orderPriceMultiplier()) == Double.doubleToLongBits(bm.orderPriceMultiplier(0));
        for (double ahead : aheads) {
            for (double each : eachs) {
                bitsEqual &= Double.doubleToLongBits(bm.orderPriceMultiplier(ahead)) == Double.doubleToLongBits(bm.orderPriceMultiplier(ahead, each, 1));
            }
        }
        assertTrue("on the calm city, orderPriceMultiplier(x) is orderPriceMultiplier(x, pointsEach, 1) to the bit, for queues ahead of it from nothing to ten million points", bitsEqual);

        // Just under the knee: one home carries no premium, an order of 600 reaches past the knee and pays more a home.
        roads(g, roadsFor(g, 0.9 * knee));
        State near = state(g, "roads: under the knee");
        assertTrue("fixture: the queue is just under the knee and the contractor not taking orders", near.months > 0.8 * knee && near.months <= knee && !near.foreign);
        bitsEqual = true;
        for (double ahead : aheads) {
            for (double each : eachs) {
                bitsEqual &= Double.doubleToLongBits(bm.orderPriceMultiplier(ahead)) == Double.doubleToLongBits(bm.orderPriceMultiplier(ahead, each, 1));
            }
        }
        assertTrue("...and the same at the queue just under the knee, to the bit", bitsEqual);
        int homes = 600;
        Game.BuildQuote one = g.quoteBuild(house, 1), many = g.quoteBuild(house, homes);
        double stepMonths = points / near.sites;
        out.printf("  a House's work is %.4f of the template's; 600 Houses' %.4f a home (the queue %.2f months, a home %.4f months of the sites')%n",
                one.sticker / nmc, many.sticker / (homes * nmc), near.months, stepMonths);
        assertTrue("fixture: flat ground, so a quote's work is the Houses' alone (no foothill to prepare)", one.prepCost == 0 && many.prepCost == 0);
        assertTrue("fixture: 600 Houses reach past the knee on that queue: its months plus 599 homes' steps", near.months + (homes - 1) * stepMonths > knee);
        close("one House's work is the template's with the premium at the queue it stands in (FC1's quote)", one.sticker / nmc, near.multiple, 1e-12);
        close("600 Houses' work a home is the template's times the mean multiple over the queues they stand in", many.sticker / (homes * nmc),
                bm.orderPriceMultiplier(0, points, homes), 1e-9);
        assertTrue("A QUOTE OF 600 HOMES ON A QUEUE NEAR THE KNEE COSTS MORE A HOME THAN A QUOTE OF 1: the work a home, and the quote's total a home",
                many.sticker / homes > one.sticker && many.total / homes > one.total);
        assertTrue("...and the planners' cost a home with it (BusinessInvestment.getCostOf())",
                g.getBusinessInvestment().getCostOf(house, homes) / homes > g.getBusinessInvestment().getCostOf(house, 1));
        // ...and an order that stays under the knee is priced as one: three Houses on that queue.
        Game.BuildQuote three = g.quoteBuild(house, 3);
        assertTrue("fixture: three Houses stay under the knee on it", near.months + 2 * stepMonths <= knee);
        close("an order that stays under the knee pays no premium: three Houses' work a home is one's", three.sticker / 3, one.sticker, 1e-12);

        // Past the knee, and under the floor: the contractor's price is the same for any size of order.
        roads(g, roadsFor(g, knee + 1.5 * BuildersShort.HALF_MONTHS));
        State past2 = state(g, "roads: past the knee");
        assertTrue("fixture: well past the knee, the contractor not yet taking orders", past2.months > knee + BuildersShort.HALF_MONTHS && !past2.foreign);
        Game.BuildQuote oneP = g.quoteBuild(house, 1), manyP = g.quoteBuild(house, homes);
        assertTrue("well past the knee a home of 600 still costs more than one of one, and neither is the contractor's price",
                manyP.sticker / homes > oneP.sticker && manyP.sticker / (homes * nmc) < 1 + ceiling);
        int steps2 = 0;
        while (!bm.foreignCrewsTakeOrders() && steps2++ < 80) roads(g, 1);
        assertTrue("fixture: under the floor of the builders' month the contractor takes the orders", bm.foreignCrewsTakeOrders());
        Game.BuildQuote oneF = g.quoteBuild(house, 1), manyF = g.quoteBuild(house, homes);
        close("...his price is the same for one home and for 600: FOREIGN_PRICE times the template's work", manyF.sticker / (homes * nmc),
                oneF.sticker / nmc, 1e-12);
        bits("...and it is FOREIGN_PRICE to the bit, whatever the number of buildings", bm.orderPriceMultiplier(0, points, homes), BuildersShort.FOREIGN_PRICE);
        bits("...as the one-building multiple is", bm.orderPriceMultiplier(), BuildersShort.FOREIGN_PRICE);
    }
}
