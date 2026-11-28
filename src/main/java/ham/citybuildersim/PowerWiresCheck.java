package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The wires are ordered with the power, and the grid is planned at its date and latitude (batch PW9-G).
 *
 * WHY. REV-PW9 P1-b: 0.9.218's modern-equipment city ordered a 21 MW Diesel and 9 Small Steam Stations at pre134-135 behind 15 MW of
 * wires, its Substation held 13 passes by the High school's protected completion; PW9's round 2 had 31 stations behind the founding 10 MW,
 * and 12.61 GWh of its months unserved. The dispatch caps every half-block at what the wires carry, so generation past them is capital
 * that never flows. Each of these fails on the model before PW9-G.
 *
 * WHAT THIS HAS TO PROVE.
 *
 *   1. THE CONDITION AND ITS REAL EFFECT. A town on the founding wires, its load's peak past them, plants standing with firm supply well
 *      past it: a native month serves no half-block more than the wires carry, leaves load unserved, generates no more than the wires let
 *      through, and serves what it asked less what it left. No law here: it is the physics the law answers to.
 *
 *   2. THE WIRES ORDERED WITH THE POWER. Ample builders, nothing protected, the automatic builder on: the plants short at the next
 *      plant's opening and the wires short of the load there though they carry today's. In one pass the wires are ordered before the plant,
 *      and carry the load at its date; the months that follow, until every order has opened, are never wire-bound. The same town built
 *      afresh and run the same months with the law off (AutoBuilder.wiresOffForTest, 0.9.218's builder) is wire-bound in some month, and
 *      the law serves of the load asked no less than it does.
 *
 *   3. THE WIRES HELD BY A PROTECTED SCHOOL'S DEADLINE. Where a service lacking now has its first completion quoted and the builders are
 *      tight, the wires are held by it (the protected law stands) and the plants are cut to the share the wires carry, or held whole where
 *      they already reach it; and while the service goes up the grid is ordered only in a month it is lacked now (the crew guard's exemption).
 *      Against the same town with the law off, the cut orders fewer plant units, leaves no more months unserved with the plants short, and
 *      serves of the load asked at least what the plants it cut would have (less a part in a thousand). (a) is cut to the count the harness's own frontier
 *      allows (every month from the order's opening to its date); (b)'s wire-bound months are the town's own, law on or off: its standing plants offer more than
 *      the carry before any order, the same months are wire-bound in both runs and the law leaves no more unserved in them, and the grid is ordered in the
 *      first month it is lacked now, every due the law keeps recorded for a plant it placed.
 *
 *   4. WHAT THE LAW LEAVES ALONE. (a) Wires that carry the load at the plant's date: the order is not cut and no wires are ordered. (b) An
 *      order the plants on site already serve the load for (the spread's wind, for the fuel it saves) behind wires short at its date: placed
 *      whole, not cut.
 *
 *   5. THE GRID'S PEAK AT ITS DATE AND LATITUDE. Now it is the native dispatch's own month at the city's latitude; ahead it is the highest
 *      month of that season over the months the order serves, from its opening to its horizon, at most a year of them.
 *
 *   6. THE ALLOWANCE IS A SEARCH. The rule that sizes an order (its months, each with the wires that open by then) allows a count only if it allows
 *      every smaller one, since the builder finds the count by halving.
 *
 *   7. A FUNDED ASK IS PLACED THROUGH THE WIRES TOO (REV-PW9-G 1). The ask for a plant whose bond has raised its cost, reopened by the pass: the wires
 *      short of it are ordered first (the grid's step runs before the plant is placed), the plant is placed cut to the count the wires allow, or held
 *      with the ask's own line, the ask still open and what it raised unchanged.
 *
 *   8. THE FRONTIER (REV-PW9-G 2). A plant that opens months before its date, and a grid unit on site that opens between the two: the wires carry
 *      the load at the date and not in the months before, so the order is cut to the count the harness's own frontier allows, and no month from
 *      the plant's opening to the wires' is wire-bound.
 *
 *   9. THE PROMISE (REV-PW9-G 3). A plant placed relying on grid units on site records the month they are due by; while it stands an automatic order
 *      whose points would put a unit past it is held by the builders, the units are open by it, it goes the month after it passes, and a save keeps it.
 *
 *  10. A RUSHED GRID SITE (REV-PW9-G 4). Units on a rushed site, and a plant's points on the same crews: the units the wires are credited by a date
 *      are no more than open by it.
 *
 *  11. AN ABROAD GRID SITE (REV-PW9-G 5). A site with its work handed to the foreign crews at a slow pace credits no unit before the foreign crews
 *      can build it, and no more units by a date than open by it.
 *
 *  12. A CANCELLED GRID SITE (REV-PW9-G 7). The law reads no carry from a site the player is cancelling: the order is cut as it is in the same
 *      town with no grid site.
 *
 *  13. THE MONEY AUDIT STAYS EXACT to the cent over every month the sections above ran.
 *
 * Every fixture CAUSES its condition rather than waiting for it, and asserts against the model's own constants and getters.
 */
public final class PowerWiresCheck {
    static int fails;
    static final PrintStream OUT = System.out, QUIET = new PrintStream(OutputStream.nullOutputStream());

    static void check(String label, boolean ok) { OUT.println((ok ? "  OK   " : "  FAIL ") + label); if (!ok) fails++; }
    static boolean near(double a, double b, double relative) { return Math.abs(a - b) <= relative * Math.max(1, Math.max(Math.abs(a), Math.abs(b))); }
    static void quietly(Runnable work) { System.setOut(QUIET); try { work.run(); } finally { System.setOut(OUT); } }

    static final BuildAdvice.Measure GRID = BuildAdvice.Measure.of(BuildAdvice.Kind.GRID), POWER = BuildAdvice.Measure.of(BuildAdvice.Kind.POWER);

    /** The one plant the fixtures' city knows: 19 MW firm in one unit, small enough to be ordered a unit at a time. */
    static final String DIESEL = "Diesel Station";

    /** Section 1: the town's peak is this many times the founding wires'; its plants' firm supply this many times the peak. */
    static final double OVER_CARRY = 1.4, PLANTS_OVER = 2;

    /** Section 2: the town's peak today is this share of the founding wires' carry, so the wires carry it now and not the peak a plant's opening brings. */
    static final double UNDER_CARRY = .85;

    /** Section 3: the town's peak today as a share of the carry of the founding wires and a distribution unit. */
    static final double HELD_LOAD = .95;

    /** Section 3: the native months run after the pass: the school's promised thirteen and three more. */
    static final int HELD_MONTHS = 16;

    /** Section 2: the native months run at least this many (a year and a quarter, so that the highest season comes round), at most MOST_MONTHS. */
    static final int LEAST_MONTHS = 15, MOST_MONTHS = 70;

    /** Every month's money audit residual, sections 2 and 3's, for section 6. */
    static final List<Double> AUDIT = new ArrayList<>();

    // ------------------------------------------------------------------------------------------------------------------ fixtures

    static BuildingsTemplate named(Game g, String name) {
        BuildingsTemplate t = g.getBuildingManager().getTemplateByName(name);
        if (t == null) throw new AssertionError("the catalogue has no " + name);
        return t;
    }

    /**
     * A founded city with cash and ground to spare, the founding 10 MW its wires and nothing built of the grid or the power; the Diesel
     * Station the one plant known, and builders of `crews` output (Construction Depots stood until it is reached).
     */
    static Game city(String label, long crews) {
        Game g = new Game(GameFiles.scratch("powerwires-" + label));
        quietly(() -> {
            g.newGame();
            g.setCashForTest(1_000_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000L);
            BuildingManager bm = g.getBuildingManager();
            bm.setTemplateKnown(t -> t.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(t) || DIESEL.equals(t.getName()));
            for (int guard = 0; guard < 80 && g.getBuildingOutput() < crews; guard++) bm.addStack(named(g, "Construction Depot"), 25, true);
            g.getServicesManager().updateServices();
        });
        return g;
    }

    /**
     * A founded town (WorksFinanceCheck.reserveTown(): its people, its works, every essential service served, a flat history) with its
     * Coal Power Plant retired, so that its plants are the founding grid's 10 MW alone, and the Diesel Station the one plant known.
     */
    static Game town(String label, String... menu) {
        java.util.Set<String> known = java.util.Set.of(menu);
        java.nio.file.Path root;
        try { root = TempDirs.create("powerwires-" + label); } catch (java.io.IOException e) { throw new IllegalStateException(e); }
        Game g = WorksFinanceCheck.reserveTown(root, label);
        BuildingManager bm = g.getBuildingManager();
        quietly(() -> {
            BuildingsTemplate coal = named(g, "Coal Power Plant");
            bm.retire(coal, bm.getQuantity(coal.getId()));
            bm.setTemplateKnown(t -> t.getCategory() != BuildingType.ELECTRICITY || Substations.isSubstation(t) || known.contains(t.getName()));
            g.setCashForTest(1_000_000_000 * PriceLevel.FACTOR);
            g.getServicesManager().updateServices();
            EssentialWorksCheck.serveOtherEssentials(g, POWER);
        });
        return g;
    }

    /** What the fixture's town is, for the log: people, draw, wires, plants, the plants' staffing and the builders. */
    static String describe(Game g) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        BuildingsTemplate diesel = named(g, DIESEL);
        return String.format("people %d, draw %.0f kW, peak %.0f of carry %.0f kW, firm %.0f of %.0f kW, diesel staffed %.3f, job fill %s, builders %.0f at every post, month %d",
                g.getPopulationManager().getPopulation(), u.getConsumption(), wires(g)[1], wires(g)[0], plants(g)[0], plants(g)[1], u.plantFill(diesel),
                java.util.Arrays.toString(java.util.Arrays.stream(g.getPopulationManager().getJobFillRate()).map(x -> Math.round(x * 100) / 100.0).toArray()),
                (double) g.getBuildingOutputAtEveryPost(), g.getMonth());
    }

    /** The grid's pair at today's demand: the wires' carry and the peak asked of them. */
    static double[] wires(Game g) { return BuildAdvice.supplyDemand(g, GRID, Map.of()); }

    /** The plants' pair at today's demand. */
    static double[] plants(Game g) { return BuildAdvice.supplyDemand(g, POWER, Map.of()); }

    /** The town's water works, a municipal building that stands whatever the businesses do and draws power by its template: stock added until the grid's peak is over `kw`. */
    static void drawPast(Game g, double kw) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate works = named(g, "Water Treatment Plant");
        quietly(() -> {
            double before = wires(g)[1];
            bm.addStack(works, 1, true);
            g.getServicesManager().updateServices();
            double each = wires(g)[1] - before;
            if (!(each > 0)) throw new AssertionError("the water works draw nothing");
            long more = (long) Math.ceil((kw - wires(g)[1]) / each);
            if (more > 0) bm.addStack(works, more, true);
            g.getServicesManager().updateServices();
        });
    }

    /** The town's water works stood as far as the grid's peak stays at or under `kw` (drawPast()'s whole works, one fewer than it): at most that. */
    static void drawTo(Game g, double kw) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate works = named(g, "Water Treatment Plant");
        quietly(() -> {
            double before = wires(g)[1];
            bm.addStack(works, 1, true);
            g.getServicesManager().updateServices();
            double each = wires(g)[1] - before;
            if (!(each > 0)) throw new AssertionError("the water works draw nothing");
            long fit = (long) Math.floor((kw - wires(g)[1]) / each);
            if (fit > 0) bm.addStack(works, fit, true);
            g.getServicesManager().updateServices();
        });
    }

    /** Units of a plant stood until the plants' firm supply is `kw`. */
    static void plantsTo(Game g, double kw, String plant) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate diesel = named(g, plant);
        quietly(() -> {
            for (int guard = 0; guard < 80 && plants(g)[0] < kw; guard++) {
                bm.addStack(diesel, 1, true);
                g.getServicesManager().updateServices();
            }
        });
    }

    /** The calendar month (1-12) whose season is the year's lowest on the north's calendar: a plant ordered in it opens into a higher one. */
    static int lowestMonth() {
        int low = 1;
        for (int m = 2; m <= 12; m++) if (PowerDispatch.season(m) < PowerDispatch.season(low)) low = m;
        return low;
    }

    /** The months run, quietly, until the calendar stands at `monthOfYear`. */
    static void advanceTo(Game g, int monthOfYear) {
        quietly(() -> { for (int guard = 0; guard < 24 && CityCalendar.monthOfYear(g.getMonth()) != monthOfYear; guard++) g.simulateMonths(1); });
    }

    /** The power suggestion as the automatic builder's pass asks it: the city's cash and ground, its slack, every plant it knows. */
    static BuildAdvice.Suggestion powerCard(Game g) {
        return BuildAdvice.suggestFor(g, null, POWER, g.getCash(), g.getLandManager().getAvailableSqFt(), g.getAutoBuilder().getSlack(), Set.of(), false, true);
    }

    /** What the wires are asked at a power order's own date and demand: its months, growth and slack, the grid reading no power factor. */
    static BuildAdvice.Ahead loadAt(BuildAdvice.Ahead p) { return new BuildAdvice.Ahead(p.months(), p.k(), p.slack()); }

    /** The wires' served share at a date, the grid's own units on site counted. */
    static double wiresShare(Game g, BuildAdvice.Ahead p) { return AutoBuilder.servedAt(g, GRID, g.getAutoBuilder().pendingCapacity(g, GRID), loadAt(p)); }

    /** The plants' served share at the order's own date, the plants on site counted from their own openings. */
    static double plantsShare(Game g, BuildAdvice.Ahead p) {
        return AutoBuilder.servedAt(g, POWER, WorksValue.openBy(g, g.getAutoBuilder().pendingCapacity(g, POWER), p.months()), p);
    }

    /** The highest season of the native dispatch over the months an order serves: from its opening to its horizon, at most a year of them; the harness's own oracle. */
    static double windowSeason(Game g, double months, double lat) {
        int now = g.getMonth();
        int from = (int) Math.ceil(BusinessInvestment.leadFor(months));
        int to = Math.min(from + 11, (int) Math.floor(months));
        double most = 0;
        for (int t = from; t <= to; t++) most = Math.max(most, PowerDispatch.season(CityCalendar.monthOfYear(now + t), lat));
        return most;
    }

    /** What the plants offered the month last struck, kW: every dispatch row's offer. */
    static double offered(Game g) {
        UtilitiesHandler.DispatchRecord d = g.getServicesManager().getUtilitiesHandler().getStruckBook().dispatch;
        double kw = 0;
        if (d != null) for (UtilitiesHandler.RowRecord r : d.rows()) kw += r.offeredKw();
        return kw;
    }

    /** Whether the month last struck left load unserved while the plants offered more than the wires carry. */
    static boolean wireBound(Game g) {
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        return u.getUnservedKwh() > 0 && offered(g) > Substations.costs(g).carryKw();
    }

    /** `n` native months run, quietly, each month's money audit kept for section 13. */
    static void months(Game g, int n) {
        for (int i = 0; i < n; i++) {
            quietly(() -> g.simulateMonths(1));
            AUDIT.add(g.getLastMoneyAudit().residual);
        }
    }

    /** A measure's sites on order less those the player is cancelling (the harness's own reading: the law reads no carry from a cancelled site, REV-PW9-G 7); `counted`, all of them. */
    static Map<BuildingsTemplate, Long> building(Game g, BuildAdvice.Measure m, boolean counted) {
        Map<BuildingsTemplate, Long> site = new LinkedHashMap<>(g.getAutoBuilder().pendingCapacity(g, m));
        if (!counted) site.keySet().removeIf(t -> g.getBuildingManager().getControl().isCancelling(ConstructionControl.keyOf(t)));
        return site;
    }

    /** What `count` of `t` puts on the crews: its buildings at the points their equipment takes and its works (AutoBuilder.orderPoints() is private: its three lines). */
    static double orderPoints(Game g, BuildingsTemplate t, long count) {
        Game.BuildQuote quote = g.quoteBuild(t, count);
        return count * (quote.fitout() == null ? (double) t.getConstructionPoints() : quote.fitout().points())
                + quote.prepPoints + quote.worksPoints + quote.bridgePoints + quote.deepWaterPoints;
    }

    /**
     * The law's rule read again by the harness (REV-PW9-G 2) from the model's own parts, for a power suggestion: a count's months run from the whole
     * month its quote opens it by (AutoBuilder.wait()), never more than a year before its date, to the date; at each the plants on site open by then
     * (WorksValue.openBy()), the plan's load there (BuildAdvice.powerAheadAt()) and the wires on site open by then with the count's points on the crews
     * (WorksValue.openByAfter()); a month the plants on site serve alone is exempt; the m-th unit is allowed where, in every month not exempt, the wires
     * carry the load with m on the crews, or the plants with m - 1 serve less than the wires do. `window` false reads the date alone (what the law was
     * before REV-PW9-G 2); `cancelled` true counts the sites the player is cancelling (before REV-PW9-G 7).
     */
    static final class Frontier {
        final Game g;
        final BuildAdvice.Suggestion card;
        final BuildAdvice.Ahead p;
        final boolean window;
        final Map<BuildingsTemplate, Long> plants, grid;

        Frontier(Game g, BuildAdvice.Suggestion card, boolean window, boolean cancelled) {
            this.g = g;
            this.card = card;
            this.p = card.ahead();
            this.window = window;
            this.plants = building(g, POWER, cancelled);
            this.grid = building(g, GRID, cancelled);
        }

        /** The months n of the plant stand to its date. */
        List<Double> dates(long n) {
            List<Double> d = new ArrayList<>();
            double lead = AutoBuilder.wait(g, card, Math.max(1, n));
            if (window && Double.isFinite(lead))
                for (double month = Math.max(Math.ceil(lead), Math.floor(p.months()) - 11); month < p.months(); month++) d.add(month);
            d.add(p.months());
            return d;
        }

        BuildAdvice.Ahead power(double d) { return d == p.months() ? p : BuildAdvice.powerAheadAt(g, d, Integer.MIN_VALUE, 0); }

        BuildAdvice.Ahead load(double d) { BuildAdvice.Ahead a = power(d); return new BuildAdvice.Ahead(a.months(), a.k(), a.slack()); }

        Map<BuildingsTemplate, Long> plantsAt(double d) { return WorksValue.openBy(g, plants, d); }

        boolean exempt(double d) { return !(AutoBuilder.servedAt(g, POWER, plantsAt(d), power(d)) < 1); }

        /** The wires' served share at month d with `open` of the grid's units on site open. */
        double carried(Map<BuildingsTemplate, Long> open, double d) {
            if (d == p.months()) return AutoBuilder.servedAt(g, GRID, open, load(d));
            double[] sd = BuildAdvice.gridInMonth(g, open, load(d));
            return sd[1] > 0 ? sd[0] / sd[1] : Double.POSITIVE_INFINITY;
        }

        /** ...with m of the plant on the crews (none: the queue as it stands). */
        double wires(long m, double d) {
            return carried(WorksValue.openByAfter(g, grid, d, card.template(), m, m < 1 ? 0 : orderPoints(g, card.template(), m)), d);
        }

        boolean allows(long m) {
            if (m < 1) return true;
            for (double d : dates(m)) {
                if (exempt(d) || !(wires(m, d) < 1)) continue;
                if (!(AutoBuilder.servedAt(g, POWER, BuildAdvice.plus(plantsAt(d), card.template(), m - 1), power(d)) < wires(m - 1, d))) return false;
            }
            return true;
        }

        /** The most units allowed, each smaller count allowed too. */
        long least() { long n = 0; while (n < card.count() && allows(n + 1)) n++; return n; }

        /** The first month n of the plant relies on wires on site (the month the law records): bound there, the standing wires short of its load; NaN for none. */
        double due(long n) {
            if (BuildAdvice.units(grid) == 0) return Double.NaN;
            for (double d : dates(n)) if (!exempt(d) && carried(Map.of(), d) < 1) return d;
            return Double.NaN;
        }
    }

    /** The harness's own frontier count of a power suggestion: the most units whose every month, from its opening to its date, the wires allow (the model's count, Frontier.least()). */
    static long frontierLeast(Game g, BuildAdvice.Suggestion card) { return card.ahead() == null ? card.count() : new Frontier(g, card, true, false).least(); }

    /** The month the n-th of a measure's units on order opens by, counted from now (0 for none within `most` months): by WorksValue.openByAfter() with the plant's points. */
    static int openMonth(Game g, Map<BuildingsTemplate, Long> site, long unit, BuildingsTemplate plant, long count, int most) {
        double points = plant == null || count < 1 ? 0 : orderPoints(g, plant, count);
        for (int d = 0; d <= most; d++) if (BuildAdvice.units(WorksValue.openByAfter(g, site, d, plant, count, points)) >= unit) return d;
        return 0;
    }

    // ------------------------------------------------------------------------------------------------------------------ 1

    static void physics() {
        OUT.println("--- 1. the condition and its real effect ---");
        Game g = town("physics", DIESEL);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double founding = u.getFoundingGridKw();
        drawPast(g, OVER_CARRY * founding);
        plantsTo(g, PLANTS_OVER * wires(g)[1], DIESEL);
        OUT.println("   " + describe(g));
        quietly(() -> g.simulateMonths(1));
        UtilitiesHandler.Book b = u.getStruckBook();
        double carry = Substations.costs(g).carryKw();
        double peakLoad = 0, through = 0, generated = 0;
        boolean carried = true;
        for (int s = 0; s < PowerDispatch.HALVES; s++) {
            peakLoad = Math.max(peakLoad, b.load[s]);
            carried &= b.load[s] - b.unserved[s] <= carry * (1 + 1e-9);
            through += Math.min(b.load[s], carry) * PowerDispatch.halfHours(s);
        }
        if (b.dispatch != null) for (UtilitiesHandler.RowRecord r : b.dispatch.rows()) generated += r.kwh();
        double unserved = u.getUnservedKwh();
        OUT.printf("   carry %.0f kW, founding %.0f kW; the month's peak load %.0f kW, offered %.0f kW; asked %.0f kWh, served %.0f, unserved %.0f; generated %.0f of the %.0f the wires let through%n",
                carry, founding, peakLoad, offered(g), b.askedKwh, b.servedKwh, unserved, generated, through);
        check("fixture: the wires are the founding grid's (" + carry + " kW) and the month's peak load is past them (" + peakLoad + " kW)", near(carry, founding, 1e-9) && peakLoad > carry);
        check("fixture: the plants offer well past the wires (" + offered(g) + " kW against " + carry + " kW) and the dispatch is recorded", b.dispatch != null && offered(g) > PLANTS_OVER * carry / 2);
        check("no half-block is served more than the wires carry", carried);
        check("load is left unserved (" + unserved + " kWh)", unserved > 0);
        check("the plants generate no more than the wires let through (" + generated + " kWh of " + through + ")", generated <= through * (1 + 1e-9));
        check("served is asked less unserved (" + b.servedKwh + " = " + b.askedKwh + " - " + unserved + ") and is what the plants generated", near(b.servedKwh, b.askedKwh - unserved, 1e-9) && near(b.servedKwh, generated, 1e-6));
    }

    // ------------------------------------------------------------------------------------------------------------------ 2

    /** The months a run goes on after every order has opened (a year and a month, so that the highest season comes round with them all standing). */
    static final int YEAR_AFTER = 13;

    /** What a run of native months found. */
    static final class Run {
        int months, wireBound, gridWhileHeld, firstGrid = -1, openedAt = -1, lackedAt = -1, gridAt = -1, duesWithoutPlant;
        /** The dues the law recorded, month by month as "month: due". */
        final List<String> dues = new ArrayList<>();
    }

    /** What a run delivered, the law on or off: the plants the builder ordered and what they cost, the months the grid failed and how, the load asked and served. */
    static final class Delivery {
        final String law;
        long plantUnits;
        double plantSpend, plantPoints, asked, served;
        int months, wireBound, plantBound;
        final StringBuilder gridOpened = new StringBuilder();
        /** Month by month: plants standing, plants on site, offered kW, carry kW, peak kW, unserved kWh, and what bound the month. */
        final List<String> trace = new ArrayList<>();
        /** Month by month: the plants standing, the grid's units standing and the kWh served, for comparing one run with another. */
        final List<Long> standing = new ArrayList<>(), gridStanding = new ArrayList<>();
        final List<Double> servedBy = new ArrayList<>();
        /** Month by month: the care centres' construction points done (the protected site the plants share the crews with), whether the plants bound the month, and the plants' offered kW. */
        final List<Double> careDone = new ArrayList<>(), offeredBy = new ArrayList<>();
        final List<Boolean> plantBoundBy = new ArrayList<>(), wireBoundBy = new ArrayList<>();
        /** Month by month: the kWh unserved, and the grid's peak as the month was entered (the draw the month's season asks of the wires). */
        final List<Double> unservedBy = new ArrayList<>(), peakBy = new ArrayList<>();
        /** The peak of the month about to run, set by run() before it. */
        double peakNow;

        Delivery(String law) { this.law = law; }

        /** The plant units the builder's last pass ordered, and their cost. */
        void passed(AutoBuilder auto) {
            for (AutoBuilder.Step s : auto.steps()) if (s.measure().equals(POWER) && s.ordered() > 0) { plantUnits += s.ordered(); plantSpend += s.cost(); }
        }

        /** ...and the construction points those units put on the crews (the units' own: the preparation and works a site may owe are not counted). */
        void passed(Game g) {
            passed(g.getAutoBuilder());
            for (AutoBuilder.Step s : g.getAutoBuilder().steps())
                if (s.measure().equals(POWER) && s.ordered() > 0) plantPoints += s.ordered() * (double) named(g, s.building()).getConstructionPoints();
        }

        /** What the month last struck asked and served, and whether the wires or the plants bound it (unserved while the plants offered no more than the carry). */
        void struck(Game g) {
            UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
            months++;
            asked += u.getStruckBook().askedKwh;
            served += u.getStruckBook().servedKwh;
            if (wireBound(g)) wireBound++;
            else if (u.getUnservedKwh() > 0) plantBound++;
            long stand = 0;
            for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (POWER.serves(t)) stand += g.getBuildingManager().getQuantity(t.getId());
            standing.add(stand);
            gridStanding.add(gridUnits(g));
            servedBy.add(u.getStruckBook().servedKwh);
            BuildingsTemplate centre = named(g, "Community Health Centre");
            BuildingsStacks care = g.getBuildingManager().getStack(centre);
            careDone.add(care == null ? 0 : care.getQuantity() * (double) centre.getConstructionPoints() + care.getConstructionProgress());
            offeredBy.add(offered(g));
            plantBoundBy.add(!wireBound(g) && u.getUnservedKwh() > 0);
            wireBoundBy.add(wireBound(g));
            unservedBy.add(u.getUnservedKwh());
            peakBy.add(peakNow);
            trace.add(String.format("   (law %-3s) month %2d: plants %d standing, %d on site; offered %.0f kW, carry %.0f kW, peak %.0f kW; unserved %.0f kWh%s", law, g.getMonth(), stand,
                    BuildAdvice.units(BuildAdvice.onSite(g, POWER)), offered(g), Substations.costs(g).carryKw(), wires(g)[1], u.getUnservedKwh(),
                    wireBound(g) ? " WIRE-BOUND" : u.getUnservedKwh() > 0 ? " PLANT-BOUND" : "") + "; " + sites(g) + " grid ordered " + g.getAutoBuilder().steps().stream()
                    .filter(x -> x.measure().equals(GRID)).mapToLong(AutoBuilder.Step::ordered).sum());
        }

        /** The first month (counted from 1) in which this run's plants standing, grid standing or kWh served differ from `o`'s, or 0 where they are the same in every month of both. */
        int firstDifference(Delivery o) {
            if (standing.size() != o.standing.size()) return Math.min(standing.size(), o.standing.size()) + 1;
            for (int i = 0; i < standing.size(); i++)
                if (!standing.get(i).equals(o.standing.get(i)) || !gridStanding.get(i).equals(o.gridStanding.get(i)) || !near(servedBy.get(i), o.servedBy.get(i), 1e-9)) return i + 1;
            return 0;
        }

        /** Served over asked across the run: 1 where nothing was asked. */
        double share() { return asked > 0 ? served / asked : 1; }

        void print() {
            OUT.printf("   law %-3s: %d plant units ordered for Cr %.1fM; of %d months %d wire-bound, %d plant-bound; asked %.0f MWh, served %.0f (%.4f); grid units opened in month%s%n",
                    law, plantUnits, plantSpend / PriceLevel.FACTOR / 1e6, months, wireBound, plantBound, asked / 1000, served / 1000, share(), gridOpened.length() == 0 ? ": none" : gridOpened.toString());
        }
    }

    /** The grid units standing: Substations and distribution units. */
    static long gridUnits(Game g) {
        BuildingManager bm = g.getBuildingManager();
        return bm.getQuantity(Substations.ID) + bm.getQuantity(Substations.DISTRIBUTION_ID);
    }

    /**
     * Native months, one at a time and the automatic builder on, until every order of the grid, the power and `care` (null for none) has opened
     * and YEAR_AFTER months more have run, at most `most`; or, `exact`, `most` months whatever has opened. The months that were wire-bound, the
     * grid's orders while `care`'s site was still going up (a month it opens in not counted either way), and the month the grid's first order
     * came after it opened; `d` takes what each month ordered, asked, served and opened.
     */
    static Run run(Game g, BuildAdvice.Measure care, int most, Delivery d, boolean exact) {
        AutoBuilder auto = g.getAutoBuilder();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        Run r = new Run();
        long units = gridUnits(g);
        for (int i = 0; i < most; i++) {
            boolean lackedBefore = BuildAdvice.lacksNow(g, GRID);
            boolean heldBefore = care != null && BuildAdvice.units(BuildAdvice.onSite(g, care)) > 0;
            TreeSet<Double> duesBefore = new TreeSet<>(auto.wiresDue());
            d.peakNow = wires(g)[1];
            quietly(() -> g.simulateMonths(1));
            AUDIT.add(g.getLastMoneyAudit().residual);
            r.months++;
            d.passed(g);
            d.struck(g);
            boolean placed = auto.steps().stream().anyMatch(s -> s.measure().equals(POWER) && s.ordered() > 0);
            for (double due : auto.wiresDue()) if (!duesBefore.contains(due)) {
                r.dues.add(String.format("month %d: due by month %.0f%s", g.getMonth() - 1, due, placed ? "" : " (no plant placed)"));
                if (!placed) r.duesWithoutPlant++;
            }
            if (lackedBefore && r.lackedAt < 0) r.lackedAt = i;
            if (auto.steps().stream().anyMatch(s -> s.measure().equals(GRID) && s.ordered() > 0) && r.gridAt < 0) r.gridAt = i;
            if (gridUnits(g) > units) { d.gridOpened.append(' ').append(r.months).append(" (+").append(gridUnits(g) - units).append(')'); units = gridUnits(g); }
            boolean heldAfter = care != null && BuildAdvice.units(BuildAdvice.onSite(g, care)) > 0;
            boolean grid = auto.steps().stream().anyMatch(s -> s.measure().equals(GRID) && s.ordered() > 0);
            if (grid && heldBefore && heldAfter && !lackedBefore && !BuildAdvice.lacksNow(g, GRID)) r.gridWhileHeld++;
            if (grid && !(heldBefore && heldAfter) && r.firstGrid < 0) r.firstGrid = i;
            if (wireBound(g)) {
                r.wireBound++;
                OUT.printf("   (law %s) month %d is wire-bound: unserved %.0f kWh, offered %.0f kW, carry %.0f kW%n", d.law, g.getMonth(), u.getUnservedKwh(), offered(g), Substations.costs(g).carryKw());
            }
            long site = BuildAdvice.units(BuildAdvice.onSite(g, GRID)) + BuildAdvice.units(BuildAdvice.onSite(g, POWER)) + (care == null ? 0 : BuildAdvice.units(BuildAdvice.onSite(g, care)));
            if (site == 0 && r.openedAt < 0) r.openedAt = i;
            if (!exact && r.openedAt >= 0 && i - r.openedAt >= YEAR_AFTER) break;
        }
        return r;
    }

    /** Section 2's town, built afresh: the builders ample, nothing protected, the automatic builder on at slack 0, the draw UNDER_CARRY of the founding wires in the year's lowest month. */
    static Game orderTown(String label) {
        Game g = town(label, DIESEL);
        advanceTo(g, lowestMonth());
        drawPast(g, UNDER_CARRY * g.getServicesManager().getUtilitiesHandler().getFoundingGridKw());
        quietly(() -> { g.getAutoBuilder().setSlack(0, null); g.getAutoBuilder().setOn(true, null); });
        return g;
    }

    static void ordered() {
        OUT.println("--- 2. the wires are ordered with the power: the law on, and off as 0.9.218 had it ---");
        Game g = orderTown("order");
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        BuildingsTemplate diesel = named(g, DIESEL);
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        double[] now = wires(g);
        double seasonNow = PowerDispatch.season(CityCalendar.monthOfYear(g.getMonth()));
        double seasonThen = p == null ? 0 : windowSeason(g, p.months(), Double.NaN);
        double plantsAt = p == null ? Double.NaN : plantsShare(g, p), wiresAt = p == null ? Double.NaN : wiresShare(g, p);
        OUT.println("   " + describe(g));
        OUT.printf("   the card %s x %d opens in %.1f months, planned %.1f months ahead (season then %.3f, now %.3f); plants' share there %.3f, the wires' %.3f%n",
                card == null ? "none" : card.template().getName(), card == null ? 0 : card.count(), card == null ? 0 : card.lead(), p == null ? 0 : p.months(),
                seasonThen, seasonNow, plantsAt, wiresAt);
        check("fixture: no service's first completion is protected, and the wires carry today's peak (" + now[1] + " of " + now[0] + " kW)", auto.toState().pendingUntil.isEmpty() && now[1] <= now[0]);
        check("fixture: the power card is Diesel Stations (" + (card == null ? "none" : card.template().getName() + " x " + card.count()) + ")", card != null && card.count() >= 1 && diesel.equals(card.template()));
        check("fixture: the order's window season (" + seasonThen + ") is past today's (" + seasonNow + ")", seasonThen > seasonNow);
        check("fixture: the plants are short at the plant's opening (" + plantsAt + ") and the wires short of the load there (" + wiresAt + ")", plantsAt < 1 && wiresAt < 1);
        if (card == null) return;
        Delivery on = new Delivery("on");
        quietly(() -> auto.pass(g));
        on.passed(g);
        int grid = -1, power = -1, at = 0;
        StringBuilder said = new StringBuilder();
        for (AutoBuilder.Step s : auto.steps()) {
            if (s.measure().equals(GRID) || s.measure().equals(POWER)) said.append(' ').append(s.measure().label()).append(':').append(s.ordered()).append('/').append(s.wanted()).append(':').append(s.cut());
            if (grid < 0 && s.measure().equals(GRID) && s.ordered() > 0) grid = at;
            if (power < 0 && s.measure().equals(POWER) && s.ordered() > 0) power = at;
            at++;
        }
        double after = wiresShare(g, p);
        OUT.printf("   the pass:%s; wires' share at the card's date after it %.3f%n", said, after);
        check("one pass orders the wires (step " + grid + ") before the plant (step " + power + ")", grid >= 0 && power >= 0 && grid < power);
        check("...and the wires then carry the load at the plant's date (the wires' share " + after + ")", after >= 1);
        Run r = run(g, null, MOST_MONTHS, on, false);
        long open = bm.getQuantity(diesel.getId()), units = bm.getQuantity(Substations.DISTRIBUTION_ID);
        OUT.printf("   %d months run: %d diesels and %d units stand; %d wire-bound%n", r.months, open, units, r.wireBound);
        check("every order has opened (" + open + " Diesel Stations and " + units + " distribution units stand) and the plants stood a year (" + r.months + " months)", r.openedAt >= 0 && open >= 1 && units >= 1 && r.months > r.openedAt + YEAR_AFTER);
        check("no month of the " + r.months + " was wire-bound (load unserved while the plants offered more than the carry)", r.wireBound == 0);
        // The same town built afresh and the same months run with the law off (0.9.218's builder): the plants wait for no wires and none are ordered with them.
        Delivery off = new Delivery("off");
        Game h = orderTown("order-off");
        AutoBuilder.wiresOffForTest = true;
        try {
            quietly(() -> h.getAutoBuilder().pass(h));
            off.passed(h);
            StringBuilder offSaid = new StringBuilder();
            for (AutoBuilder.Step s : h.getAutoBuilder().steps())
                if (s.measure().equals(GRID) || s.measure().equals(POWER)) offSaid.append(' ').append(s.measure().label()).append(':').append(s.ordered()).append('/').append(s.wanted()).append(':').append(s.cut());
            OUT.println("   the pass, law off:" + offSaid);
            run(h, null, r.months, off, true);
        } finally { AutoBuilder.wiresOffForTest = false; }
        on.print();
        off.print();
        int differ = on.firstDifference(off);
        check("where nothing holds the wires the law changes nothing: law on and law off have the same plants standing, grid standing and kWh served in each of " + on.months + " months ("
                + (differ == 0 ? "no month differs" : "month " + differ + " differs: plants " + on.standing.get(differ - 1) + " against " + off.standing.get(differ - 1) + ", grid " + on.gridStanding.get(differ - 1) + " against "
                + off.gridStanding.get(differ - 1) + ", served " + on.servedBy.get(differ - 1) + " against " + off.servedBy.get(differ - 1)) + ")", on.months == off.months && differ == 0);
        check("law on serves of the load asked no less than law off (" + on.share() + " against " + off.share() + ")", on.share() >= off.share() - 1e-9);
    }

    // ------------------------------------------------------------------------------------------------------------------ 3

    /** The service whose first completion the fixtures protect: General care's Community Health Centres. */
    static final BuildAdvice.Measure CARE = BuildAdvice.Measure.care(CareType.GENERAL);

    /** The one plant section 3 knows: a unit a twentieth of a Diesel Station's, so that an order of several is cut to fewer. */
    static final String STEAM = "Small Steam Station";

    /**
     * 3's town: section 2's, the Small Steam Station the plant known, with General care lacking now, its Community Health Centres going up on the
     * builders the town has and their first completion saved (PowerPlanCheck's careTown(): pendingUntil, as rememberPending() quotes it).
     */
    static Game schoolTown(String label) {
        Game g = town(label, STEAM);
        BuildingManager bm = g.getBuildingManager();
        quietly(() -> {
            for (BuildingsTemplate t : bm.getTemplates()) if (CARE.serves(t) && bm.getQuantity(t.getId()) > 0) bm.retire(t, bm.getQuantity(t.getId()));
            BuildingsTemplate centre = named(g, "Community Health Centre");
            long required = BuildAdvice.count(g, CARE, Map.of(), Map.of(centre, 1L), g.getAutoBuilder().target(), BuildAdvice.MOST, true)[0];
            g.buildStack(centre, required, false);
            AutoBuilder ab = g.getAutoBuilder();
            AutoBuilder.State state = ab.toState();
            state.on = true;
            state.pendingUntil.clear();
            state.pendingUntil.put(CARE.label(), g.getMonth() + BuildAdvice.firstCompletion(g, CARE, BuildAdvice.onSite(g, CARE)));
            ab.restore(state);
        });
        return g;
    }

    /** Whether `count` of the order would put the protected centres' first completion past its quote (AutoBuilder.pendingDelay(), read from outside). */
    static boolean delays(Game g, BuildingsTemplate t, long count) {
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate centre = named(g, "Community Health Centre");
        double output = g.getBuildingOutputAtEveryPost(), due = g.getAutoBuilder().toState().pendingUntil.getOrDefault(CARE.label(), Double.NaN);
        double alone = bm.siteUnitMonths(centre, 1, output);
        double with = bm.siteUnitMonthsAfter(centre, 1, output, t, count, WorksFinanceCheck.addedPoints(g.quoteBuild(t, count), t, count));
        return Double.isFinite(alone) && with > alone + 1e-9 && g.getMonth() + Math.ceil(with - 1e-9) > due;
    }

    /**
     * 3's town, built afresh: the school's town in the year's lowest month with a distribution unit standing and the draw HELD_LOAD of the carry
     * (the wires carry today's peak, and not the highest season's), the automatic builder on at slack 0 and, for (b), the plants stood halfway
     * between the wires' share at the plant's date and the load there: past the wires' and short of 1.
     */
    static Game heldTown(boolean cut) {
        Game g = schoolTown(cut ? "held-short" : "held-at");
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        advanceTo(g, lowestMonth());
        quietly(() -> { bm.addStack(bm.getTemplate(Substations.DISTRIBUTION_ID), 1, true); g.getServicesManager().updateServices(); });
        drawTo(g, HELD_LOAD * wires(g)[0]);
        quietly(() -> { auto.setSlack(0, null); auto.setOn(true, null); });
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        if (!cut && p != null) {
            double[] sd = BuildAdvice.supplyDemand(g, POWER, Map.of(), p);
            plantsTo(g, sd[1] * (1 + wiresShare(g, p)) / 2, STEAM);
        }
        return g;
    }

    /** The two sites a plant order crowds, for the log: the care centres and the grid's units, each as standing, on site and points done of points on site. */
    static String sites(Game g) {
        BuildingManager bm = g.getBuildingManager();
        StringBuilder out = new StringBuilder();
        for (BuildingsTemplate t : new BuildingsTemplate[] {named(g, "Community Health Centre"), bm.getTemplate(Substations.ID), bm.getTemplate(Substations.DISTRIBUTION_ID)}) {
            BuildingsStacks s = bm.getStack(t);
            out.append(String.format("%s: %d standing, %d on site, %.0f of %.0f points done; ", t.getName(), s == null ? 0 : s.getQuantity(), s == null ? 0 : s.getUnderConstruction(),
                    s == null ? 0 : s.getConstructionProgress(), s == null ? 0 : s.pointsOnSite()));
        }
        return out.toString();
    }

    /**
     * 3 (a) and (b): the plants short of the wires' share (a) or already at it and short of the reserve (b), and the wires' units held by the
     * school's deadline; then the same town with the law off, and what each delivered.
     */
    static void held(boolean cut) {
        String which = cut ? "(a) the plants short of the wires' share" : "(b) the plants already at the wires' share";
        OUT.println("--- 3 " + which + ": the wires wait for the school's completion, the plants are cut or held; the law on, and off ---");
        Game g = heldTown(cut);
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        OUT.println("   " + describe(g));
        double wiresBefore = p == null ? Double.NaN : wiresShare(g, p), plantsBefore = p == null ? Double.NaN : plantsShare(g, p);
        BuildingsTemplate unit = bm.getTemplate(Substations.DISTRIBUTION_ID);
        BuildAdvice.Suggestion nested = p == null ? null : BuildAdvice.suggestFor(g, null, GRID, g.getCash(), g.getLandManager().getAvailableSqFt(), auto.getSlack(), Set.of(), false, true, loadAt(p));
        // The least count of the plant whose share reaches the wires' at every month from its opening to its date: the harness's own frontier (REV-PW9-G 2),
        // and the date's alone beside it (the binding month's: what the model counted before).
        long least = p != null && card != null ? frontierLeast(g, card) : 0;
        long atDate = p != null && card != null ? new Frontier(g, card, false, false).least() : 0;
        double promised = auto.promisedMonths(g, CARE);
        OUT.printf("   the card %s x %d opening in %.1f months, least count to the wires' share at every month from its opening to its date %d (at its date alone %d); plants' share %.3f, wires' %.3f; care promised in %.2f months%n",
                card == null ? "none" : card.template().getName(), card == null ? 0 : card.count(), card == null ? 0 : card.lead(), least, atDate, plantsBefore, wiresBefore, promised);
        check("fixture: General care lacks now and its first completion is protected (" + promised + " months); the wires carry today's peak (" + wires(g)[1] + " of " + wires(g)[0] + " kW), so the grid is not lacked and the crew guard holds its order",
                BuildAdvice.lacksNow(g, CARE) && Double.isFinite(promised) && wires(g)[1] <= wires(g)[0] && !BuildAdvice.lacksNow(g, GRID));
        check("fixture: the wires' order for the plant's load (" + (nested == null ? "none" : nested.count() + " x " + nested.template().getName()) + ") would put the centres' completion past its quote",
                nested != null && unit.equals(nested.template()) && delays(g, nested.template(), nested.count()));
        if (cut)
            check("fixture: the plants are short of the wires' share (" + plantsBefore + " against " + wiresBefore + "), the power lacked (the crew guard leaves it), the order is more units (" + (card == null ? 0 : card.count()) + ") than the share needs at every month (" + least + ") and the date alone would allow more (" + atDate + ")",
                    card != null && plantsBefore < wiresBefore && wiresBefore < 1 && BuildAdvice.lacksNow(g, POWER) && least >= 1 && card.count() > least && atDate > least);
        else
            check("fixture: the plants are at the wires' share (" + plantsBefore + " against " + wiresBefore + ") and short of the reserve at the plant's date",
                    card != null && plantsBefore >= wiresBefore && plantsBefore < 1 && wiresBefore < 1);
        if (card == null || nested == null) return;
        Delivery on = new Delivery("on");
        quietly(() -> auto.pass(g));
        on.passed(g);
        AutoBuilder.Step wiresStep = null, powerStep = null;
        for (AutoBuilder.Step s : auto.steps()) {
            if (s.measure().equals(GRID) && s.cut() == AutoBuilder.Cut.BUILDERS && wiresStep == null) wiresStep = s;
            if (s.measure().equals(POWER) && powerStep == null) powerStep = s;
        }
        String heldGrid = GRID.label() + " waits to preserve " + CARE.label() + "'s quoted first completion.";
        boolean words = auto.held().stream().anyMatch(h -> h.contains(heldGrid));
        OUT.println("   the pass: the wires " + (wiresStep == null ? "no step" : wiresStep.outcome() + " " + wiresStep.ordered() + "/" + wiresStep.wanted() + " cut " + wiresStep.cut())
                + "; the plant " + (powerStep == null ? "no step" : powerStep.outcome() + " " + powerStep.ordered() + "/" + powerStep.wanted() + " " + powerStep.building() + " cut " + powerStep.cut()));
        for (String h : auto.held()) OUT.println("   held: " + (h.length() > 200 ? h.substring(0, 200) : h));
        check("the wires' step is held by the builders, ordering none, and says it waits to preserve " + CARE.label() + "'s quoted first completion",
                wiresStep != null && wiresStep.ordered() == 0 && words && BuildAdvice.units(BuildAdvice.onSite(g, GRID)) == 0);
        check("...and the school's promised months are what they were (" + promised + ", now " + auto.promisedMonths(g, CARE) + ")", auto.promisedMonths(g, CARE) == promised);
        if (cut)
            check("the plants' order is cut for the wires (" + (powerStep == null ? "none" : powerStep.ordered() + " of " + powerStep.wanted() + ", cut " + powerStep.cut())
                    + ") to the least count whose share reaches the wires' at every month from its opening to its date (" + least + ")",
                    powerStep != null && powerStep.cut() == AutoBuilder.Cut.WIRES && powerStep.ordered() == least);
        else {
            boolean line = auto.held().stream().anyMatch(h -> h.startsWith(POWER.label() + ":") && h.contains("wires"));
            check("the plants' order is held whole by the wires (" + (powerStep == null ? "none" : powerStep.ordered() + " of " + powerStep.wanted() + ", cut " + powerStep.cut())
                    + "), its line naming them, where the frontier allows none (" + least + ")",
                    powerStep != null && powerStep.ordered() == 0 && powerStep.cut() == AutoBuilder.Cut.WIRES && line && least == 0);
        }
        // The months of the school's deadline run on: the protected law stands, so the grid is ordered in them only in a month it is lacked now
        // (the crew guard's exemption: the peak of the season past the carry). The wires ordered then, and the plants that are not cut, crowd the
        // same few builders: the months the plants open into are printed, and held to what the law is for below, against the same town with it off.
        double firmAtStart = plants(g)[0], carryAtStart = Substations.costs(g).carryKw();
        Run r = run(g, CARE, HELD_MONTHS, on, false);
        OUT.printf("   %d months run; %d of them wire-bound; the grid ordered in %d months it was not lacked while the centres went up%n", r.months, r.wireBound, r.gridWhileHeld);
        check("the grid is ordered while the centres go up only in a month it is lacked now (" + r.gridWhileHeld + " months it was not)", r.months > 0 && r.gridWhileHeld == 0);
        // The same town built afresh, the same months, the law off (0.9.218's builder: the plants wait for no wires and none are ordered with them).
        Delivery off = new Delivery("off");
        Game other = heldTown(cut);
        AutoBuilder.wiresOffForTest = true;
        try {
            quietly(() -> other.getAutoBuilder().pass(other));
            off.passed(other);
            run(other, CARE, r.months, off, true);
        } finally { AutoBuilder.wiresOffForTest = false; }
        on.print();
        off.print();
        for (int i = 0; i < Math.max(on.trace.size(), off.trace.size()); i++) {
            if (i < on.trace.size()) OUT.println(on.trace.get(i));
            if (i < off.trace.size()) OUT.println(off.trace.get(i));
        }
        OUT.printf("   plant orders' construction points on the crews: on %.0f, off %.0f%n", on.plantPoints, off.plantPoints);
        OUT.println("   end, law on : " + sites(g));
        OUT.println("   end, law off: " + sites(other));
        BuildAdvice.Suggestion lastOn = powerCard(g), lastOff = powerCard(other);
        OUT.printf("   at the last month, the plants' share at the first card's date (%.1f months): on %.4f, off %.4f; the wires' share there: on %.4f, off %.4f; the last cards: on %s, off %s%n",
                p.months(), plantsShare(g, p), plantsShare(other, p), wiresShare(g, p), wiresShare(other, p),
                lastOn == null ? "none" : lastOn.template().getName() + " x " + lastOn.count(), lastOff == null ? "none" : lastOff.template().getName() + " x " + lastOff.count());
        check("law on orders fewer plant units than law off (" + on.plantUnits + " against " + off.plantUnits + "): the excess is held", on.plantUnits < off.plantUnits);
        // What the held units were (PW9-G's notes, "the crews"): law off's units past the wires take a larger share of two depots (the crews' weight is
        // units x points^0.7, ConstructionControl), so its first plants open sooner and it serves more in the months law on's are still being built;
        // law on puts those points on the protected care centre instead, and its plants reach the carry. Its delivery over the months is printed, not
        // held to law off's: the months it serves less are months its plants stand fewer than law off's, never months the cut kept them short.
        check(String.format("law on's plant orders put fewer construction points on the crews than law off's (%.0f against %.0f)", on.plantPoints, off.plantPoints),
                on.plantPoints < off.plantPoints);
        int careBehind = 0, cutShort = 0;
        for (int i = 0; i < Math.min(on.careDone.size(), off.careDone.size()); i++) {
            if (on.careDone.get(i) < off.careDone.get(i) - 1e-9) careBehind++;
            if (on.plantBoundBy.get(i) && !off.plantBoundBy.get(i) && on.standing.get(i) >= off.standing.get(i)) cutShort++;
        }
        check("...and the protected care centre is as far along under law on as under law off in every month (" + careBehind + " months behind)", careBehind == 0);
        check("each month law on's plants bound and law off's did not, law on had fewer plants standing: they were still being built, not cut short ("
                + cutShort + " months otherwise)", cutShort == 0);
        double lastOffered = on.offeredBy.isEmpty() ? 0 : on.offeredBy.get(on.offeredBy.size() - 1);
        check(String.format("by the last month the plants law on allowed offer the carry (%.0f of %.0f kW): nothing the wires carry was left unbuilt", lastOffered, Substations.costs(g).carryKw()),
                lastOffered >= Substations.costs(g).carryKw() - 1e-9);
        OUT.printf("   delivery over the %d months: law on serves %.4f of the load asked, law off %.4f%s%n", on.months, on.share(), off.share(),
                on.share() < off.share() ? " (law off's earlier plants, at the care centre's expense)" : "");
        if (cut) return;
        // What (b)'s wire-bound months are (PW9-G's notes, REV-PW9-G): the town's 15 standing plants offer more than the carry before any order is placed, so
        // every month whose peak passes the carry is wire-bound with the law or without it; the grid is first lacked now in the run's first such month and
        // ordered then (the protected centre holds it till then), and its first unit cannot open in the months the builders the centre leaves allow.
        // The law is held to what it can do: the same months, and no more unserved in any of them.
        int differ = 0, moreUnserved = 0, notPast = 0, bound = 0;
        for (int i = 0; i < Math.min(on.wireBoundBy.size(), off.wireBoundBy.size()); i++) {
            boolean a = on.wireBoundBy.get(i), b = off.wireBoundBy.get(i);
            if (a != b) differ++;
            if (!a && !b) continue;
            bound++;
            if (on.unservedBy.get(i) > off.unservedBy.get(i) * (1 + 1e-3) + 1e-9) moreUnserved++;
            if (!(on.peakBy.get(i) > carryAtStart) || !(off.peakBy.get(i) > carryAtStart)) notPast++;
        }
        OUT.printf("   wire-bound months: %d (law on %d, law off %d), %d differ; the standing plants' firm supply %.0f kW over the carry %.0f kW; the grid first lacked now in run month %d, ordered in run month %d%n",
                bound, on.wireBound, off.wireBound, differ, firmAtStart, carryAtStart, r.lackedAt, r.gridAt);
        check("(i) law on's wire-bound months are law off's (" + bound + " months, " + differ + " differ), and in each law on leaves no more unserved than law off, to a part in a thousand ("
                + moreUnserved + " months more)", bound > 0 && differ == 0 && moreUnserved == 0);
        check("(ii) the plants standing at the run's start alone offer more than the carry (" + firmAtStart + " kW firm against " + carryAtStart + "), and in each of the " + bound
                + " months the peak exceeds the carry in both runs (" + notPast + " not): they are wire-bound with the law or without it", firmAtStart > carryAtStart && notPast == 0);
        check("(iii) the grid is ordered in the first month it is lacked now (lacked in run month " + r.lackedAt + ", ordered in " + r.gridAt + ")", r.lackedAt >= 0 && r.gridAt == r.lackedAt);
        for (String line : r.dues) OUT.println("   due: " + line);
        check("(iv) every due the law kept during the run (" + r.dues.size() + ") was recorded in a month a plant was placed (" + r.duesWithoutPlant + " were not); the wires meeting them is section 9's",
                r.duesWithoutPlant == 0);
    }

    // ------------------------------------------------------------------------------------------------------------------ 4

    static void leftAlone() {
        OUT.println("--- 4 (a) wires that carry the load at the plant's date: the order is not cut and no wires are ordered ---");
        Game g = town("carries", DIESEL);
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        advanceTo(g, lowestMonth());
        drawPast(g, UNDER_CARRY * u.getFoundingGridKw());
        quietly(() -> { auto.setSlack(0, null); auto.setOn(true, null); });
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        if (p != null) {
            // Distribution units standing for the peak at the plant's date and a tenth more.
            double need = BuildAdvice.supplyDemand(g, GRID, Map.of(), loadAt(p))[1] * 1.1;
            long units = (long) Math.ceil((need - wires(g)[0]) / (Substations.DISTRIBUTION_MW * 1000));
            if (units > 0) quietly(() -> { bm.addStack(bm.getTemplate(Substations.DISTRIBUTION_ID), units, true); g.getServicesManager().updateServices(); });
            card = powerCard(g);
            p = card == null ? null : card.ahead();
        }
        double wiresAt = p == null ? Double.NaN : wiresShare(g, p), plantsAt = p == null ? Double.NaN : plantsShare(g, p);
        OUT.printf("   the card %s x %d; wires' share at its date %.3f, plants' %.3f%n", card == null ? "none" : card.template().getName(), card == null ? 0 : card.count(), wiresAt, plantsAt);
        check("fixture: the plants are short at the plant's date (" + plantsAt + ") and the wires carry the load there (" + wiresAt + ")", card != null && plantsAt < 1 && wiresAt >= 1);
        if (card == null) return;
        quietly(() -> auto.pass(g));
        AutoBuilder.Step power = null;
        boolean grid = false;
        for (AutoBuilder.Step s : auto.steps()) {
            if (s.measure().equals(POWER) && power == null) power = s;
            if (s.measure().equals(GRID) && s.ordered() > 0) grid = true;
        }
        OUT.println("   the pass: the plant " + (power == null ? "no step" : power.outcome() + " " + power.ordered() + "/" + power.wanted() + " cut " + power.cut()) + "; any grid order " + grid);
        check("the plants' order is placed whole, not cut for the wires", power != null && power.ordered() > 0 && power.ordered() == power.wanted() && power.cut() != AutoBuilder.Cut.WIRES);
        check("no grid unit is ordered in the pass", !grid);
    }

    /** The one plant section 4 (b)'s spread orders: a farm that displaces the diesels' fuel. */
    static final String WIND = "Wind Farm";

    /** Section 4 (b): the distribution units that stand, so that the wires carry more than the founding grid's plant and the diesels run past it. */
    static final long SPREAD_UNITS = 2;

    /**
     * 4 (b): the town of section 1 (the founding wires short of its peak, Diesel Stations standing well past it) knowing the Wind Farm too, so that
     * the plants on site serve the load at the farm's opening and the one order the planner has is the spread's, wind for the fuel it saves; its
     * builders kept as EssentialWorksCheck.serveGrid keeps them (depots stacked until the output at every post is what it was, and above 0).
     */
    static void spreadLeftAlone() {
        OUT.println("--- 4 (b) an order the plants on site already serve the load for, behind short wires: not cut ---");
        Game g = town("spread", DIESEL, WIND);
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double site = Math.max(1, g.getBuildingOutputAtEveryPost());
        advanceTo(g, lowestMonth());
        // Distribution units stand (SPREAD_UNITS of them) so that the diesels run on a good part of the load past the founding grid's own plant (a farm saves fuel only where they do);
        // the draw is HELD_LOAD of the carry today, and past it at the highest season; the diesels stand well past that.
        quietly(() -> { bm.addStack(bm.getTemplate(Substations.DISTRIBUTION_ID), SPREAD_UNITS, true); g.getServicesManager().updateServices(); });
        drawTo(g, HELD_LOAD * wires(g)[0]);
        plantsTo(g, PLANTS_OVER * OVER_CARRY * wires(g)[0], DIESEL);
        quietly(() -> {
            for (int guard = 0; guard < 64 && !(g.getBuildingOutputAtEveryPost() >= site && g.getBuildingOutputAtEveryPost() > 0); guard++)
                bm.addStack(named(g, "Construction Depot"), 1, true);
            auto.setSlack(0, null);
            auto.setOn(true, null);
        });
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        double wiresAt = p == null ? Double.NaN : wiresShare(g, p), plantsAt = p == null ? Double.NaN : plantsShare(g, p);
        OUT.println("   " + describe(g));
        for (BuildAdvice.Spread s : BuildAdvice.spreads(g, g.getLandManager().getAvailableSqFt(), Set.of()))
            OUT.printf("   the spread weighs %s: saves %.0f a year against %.0f (net %.0f)%n", s.template().getName(), s.saved(), s.cost(), s.net());
        OUT.printf("   the card %s x %d; wires' share at its date %.3f, plants' %.3f%n", card == null ? "none" : card.template().getName(), card == null ? 0 : card.count(), wiresAt, plantsAt);
        check("fixture: the card is the Wind Farm, the plants serve the load at its date (" + plantsAt + ") and the wires are short of it (" + wiresAt + ")",
                card != null && WIND.equals(card.template().getName()) && plantsAt >= 1 && wiresAt < 1);
        if (card == null) return;
        long count = card.count();
        quietly(() -> auto.pass(g));
        AutoBuilder.Step power = null;
        for (AutoBuilder.Step s : auto.steps()) if (s.measure().equals(POWER) && power == null) power = s;
        OUT.println("   the pass: the plant " + (power == null ? "no step" : power.outcome() + " " + power.ordered() + "/" + power.wanted() + " " + power.building() + " cut " + power.cut()));
        for (String h : auto.held()) OUT.println("   held: " + (h.length() > 200 ? h.substring(0, 200) : h));
        check("the spread's order is placed whole, not cut for the wires (" + (power == null ? "none" : power.ordered() + " of " + count + ", cut " + power.cut()) + ")",
                power != null && power.ordered() == count && power.cut() != AutoBuilder.Cut.WIRES);
    }

    // ------------------------------------------------------------------------------------------------------------------ 5

    /** Section 5: the town's draw as a share of the founding wires', the ahead windows' months, and their growth and slack. */
    static final double SEASON_DRAW = .5;
    static final double[] WINDOWS = {3, 8, 14, 20, 30, 45};
    static final double WINDOW_K = 1.07, WINDOW_SLACK = .1;

    static void season() {
        OUT.println("--- 5. the grid's peak at its date and latitude ---");
        Game g = city("season", 1);
        UtilitiesHandler u = g.getServicesManager().getUtilitiesHandler();
        double[] lat = {45};
        u.setPlace(() -> lat[0], () -> true);
        drawPast(g, SEASON_DRAW * u.getFoundingGridKw());
        double[] lats = {45, -35, Double.NaN};
        int cases = 0, nowRed = 0, windowRed = 0, aboveRed = 0, southDiffers = 0, southNow = 0, above = 0;
        BuildAdvice.Ahead far = new BuildAdvice.Ahead(24, WINDOW_K, WINDOW_SLACK);
        for (int i = 0; i < 12; i++) {
            for (double l : lats) {
                lat[0] = l;
                double c = u.getConsumption();
                int calendar = CityCalendar.monthOfYear(g.getMonth());
                double drawNow = c + WorksValue.pendingPowerLoad(g, 0);
                double read = BuildAdvice.supplyDemand(g, GRID, Map.of(), BuildAdvice.NOW)[1];
                double want = Substations.peak(drawNow, PowerDispatch.season(calendar, l));
                cases++;
                if (read != want) nowRed++;
                if (l < 0 && Substations.peak(drawNow, PowerDispatch.season(calendar)) != want) { southDiffers++; if (read == want) southNow++; }
                for (double months : WINDOWS) {
                    BuildAdvice.Ahead p = new BuildAdvice.Ahead(months, WINDOW_K, WINDOW_SLACK);
                    double draw = p.scale() * c + WorksValue.pendingPowerLoad(g, months);
                    if (!near(BuildAdvice.supplyDemand(g, GRID, Map.of(), p)[1], Substations.peak(draw, windowSeason(g, months, l)), 1e-12)) windowRed++;
                    cases++;
                }
                double drawFar = far.scale() * c + WorksValue.pendingPowerLoad(g, far.months());
                double present = Substations.peak(drawFar, PowerDispatch.season(calendar, l));
                if (PowerDispatch.season(calendar, l) < PowerDispatch.season(PowerDispatch.peakMonth())) {
                    above++;
                    if (!(BuildAdvice.supplyDemand(g, GRID, Map.of(), far)[1] > present)) aboveRed++;
                }
            }
            quietly(() -> g.simulateMonths(1));
        }
        OUT.printf("   %d readings over 12 months and 3 latitudes; NOW off %d, windows off %d; the south's month differs from the north's in %d of 12 (read right in %d); above the present month %d, not above %d%n",
                cases, nowRed, windowRed, southDiffers, southNow, above, aboveRed);
        check("fixture: the town draws (" + u.getConsumption() + " kW) and the south's month differs from the north's in some months (" + southDiffers + ")", u.getConsumption() > 0 && southDiffers >= 6);
        check("NOW reads the dispatch's own month at the latitude exactly, at 45, -35 and NaN, in every month of the year", nowRed == 0);
        check("...which for -35 is not the north's month in the " + southDiffers + " months they differ (" + southNow + " read right)", southNow == southDiffers);
        check("a future date reads the highest season over its opening to its horizon, for " + WINDOWS.length + " windows, 3 latitudes and 12 months", windowRed == 0);
        check("a window holding the year's highest month reads above the present month's season (" + above + " cases, " + aboveRed + " not)", above >= 24 && aboveRed == 0);
    }

    // ------------------------------------------------------------------------------------------------------------------ 6

    /** The power plants standing, for the runs' months. */
    static long plantsStanding(Game g) {
        long stand = 0;
        for (BuildingsTemplate t : g.getBuildingManager().getTemplates()) if (POWER.serves(t)) stand += g.getBuildingManager().getQuantity(t.getId());
        return stand;
    }

    /** The steps of the last pass that are the grid's or the power's, for the log: measure, ordered of wanted, and what cut them. */
    static String said(AutoBuilder auto) {
        StringBuilder out = new StringBuilder();
        for (AutoBuilder.Step s : auto.steps())
            if (s.measure().equals(GRID) || s.measure().equals(POWER)) out.append(' ').append(s.measure().label()).append(':').append(s.ordered()).append('/').append(s.wanted()).append(':').append(s.cut());
        return out.toString();
    }

    static void searched() {
        OUT.println("--- 6. the allowance is a search: a count is allowed only if every smaller one is ---");
        Game g = heldTown(true);
        BuildAdvice.Suggestion card = powerCard(g);
        check("fixture: the town has a power card of several units behind wires short at its date", card != null && card.ahead() != null && card.count() > 1);
        if (card == null || card.ahead() == null) return;
        Frontier f = new Frontier(g, card, true, false);
        long least = f.least(), allowedAbove = 0;
        StringBuilder row = new StringBuilder();
        for (long n = 1; n <= card.count(); n++) {
            boolean ok = f.allows(n);
            row.append(ok ? '+' : '-');
            if (n > least && ok) allowedAbove++;
        }
        OUT.println("   allowed by count 1.." + card.count() + ": " + row + " (least " + least + ")");
        check("the counts the rule allows are 1 to " + least + " and no count above (" + allowedAbove + " allowed above)", least >= 1 && least < card.count() && allowedAbove == 0);
    }

    // ------------------------------------------------------------------------------------------------------------------ 7

    /** The ask for a plant, open and funded: its bond has raised the order's cost and a tenth more (AskProceedsCheck's ask()), opened six months ago. */
    static AutoBuilder.OpenAsk fundedAsk(Game g, BuildAdvice.Suggestion card) {
        AutoBuilder.OpenAsk a = new AutoBuilder.OpenAsk();
        a.measure = POWER.label();
        a.building = card.template().getName();
        a.templateId = card.template().getId();
        a.paving = card.paving();
        a.count = card.count();
        a.cost = AutoBuilder.total(g, card, card.count()) + AutoBuilder.groundFor(g, card.template(), card.count()).cash();
        a.raised = 1.1 * a.cost;
        a.toRaise = 0;
        a.month = g.getMonth() - 6;
        return a;
    }

    /** The ask open on the city's books, the pass its next one. */
    static void open(Game g, AutoBuilder.OpenAsk ask) {
        AutoBuilder auto = g.getAutoBuilder();
        quietly(() -> {
            AutoBuilder.State state = auto.toState();
            state.on = true;
            state.asks = new ArrayList<>(List.of(ask));
            auto.restore(state);
        });
    }

    /** The index of the first step of the last pass that is `m`'s (and ordered some, if `ordering`); -1 for none. */
    static int stepOf(AutoBuilder auto, BuildAdvice.Measure m, boolean ordering) {
        int at = 0;
        for (AutoBuilder.Step s : auto.steps()) {
            if (s.measure().equals(m) && (!ordering || s.ordered() > 0)) return at;
            at++;
        }
        return -1;
    }

    static void reoffered() {
        OUT.println("--- 7. a funded ask is placed through the wires too: the wires ordered first, the plants cut to the count they allow, or held with the ask's line ---");
        // (a) the builders ample: the wires short of the plant's date are ordered by the pass before the ask's plant is placed.
        Game g = orderTown("reoffer");
        AutoBuilder auto = g.getAutoBuilder();
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        double wiresAt = p == null ? Double.NaN : wiresShare(g, p), plantsAt = p == null ? Double.NaN : plantsShare(g, p);
        check("(a) fixture: the plants are short at the plant's opening (" + plantsAt + ") and the wires short of the load there (" + wiresAt + "), with no grid unit on order",
                card != null && plantsAt < 1 && wiresAt < 1 && BuildAdvice.units(BuildAdvice.onSite(g, GRID)) == 0);
        if (card == null) return;
        AutoBuilder.OpenAsk ask = fundedAsk(g, card);
        open(g, ask);
        quietly(() -> auto.pass(g));
        int grid = stepOf(auto, GRID, true), power = stepOf(auto, POWER, true);
        OUT.println("   (a) the pass:" + said(auto) + "; wires' share at the card's date after it " + wiresShare(g, p));
        check("(a) the pass orders the wires (step " + grid + ") before the ask's plant (step " + power + ")", grid >= 0 && power >= 0 && grid < power);
        check("...the ask is placed and closed, and the wires then carry the load at the plant's date (" + wiresShare(g, p) + ")", auto.askFor(POWER) == null && wiresShare(g, p) >= 1);
        // (b) the wires held by a protected completion: the ask's plant is cut to the frontier's count, the wires' step run first and held.
        Game h = heldTown(true);
        AutoBuilder cutAuto = h.getAutoBuilder();
        BuildAdvice.Suggestion cutCard = powerCard(h);
        long least = cutCard == null ? 0 : frontierLeast(h, cutCard);
        check("(b) fixture: the ask is for " + (cutCard == null ? 0 : cutCard.count()) + " units, the wires allow " + least, cutCard != null && least >= 1 && least < cutCard.count());
        if (cutCard == null) return;
        open(h, fundedAsk(h, cutCard));
        quietly(() -> cutAuto.pass(h));
        int cutGrid = stepOf(cutAuto, GRID, false), cutPower = stepOf(cutAuto, POWER, true);
        AutoBuilder.Step planted = cutPower < 0 ? null : cutAuto.steps().get(cutPower);
        OUT.println("   (b) the pass:" + said(cutAuto));
        check("(b) the wires' step runs before the plant is placed and is held by the builders (step " + cutGrid + " before step " + cutPower + ")",
                cutGrid >= 0 && cutGrid < cutPower && cutAuto.steps().get(cutGrid).ordered() == 0 && cutAuto.steps().get(cutGrid).cut() == AutoBuilder.Cut.BUILDERS);
        check("...the plant is placed cut WIRES to the frontier's count (" + (planted == null ? "none" : planted.ordered() + " of " + planted.wanted() + ", cut " + planted.cut()) + "; " + least + ") and its ask closed",
                planted != null && planted.cut() == AutoBuilder.Cut.WIRES && planted.ordered() == least && cutAuto.askFor(POWER) == null);
        // (c) the plants on site already serve all the wires will carry: the ask is held whole with its own line, open, what it raised unchanged.
        Game k = heldTown(false);
        AutoBuilder heldAuto = k.getAutoBuilder();
        BuildAdvice.Suggestion heldCard = powerCard(k);
        if (heldCard == null) { check("(c) fixture: a power card", false); return; }
        long none = frontierLeast(k, heldCard);
        AutoBuilder.OpenAsk own = fundedAsk(k, heldCard);
        double raised = own.raised;
        open(k, own);
        quietly(() -> heldAuto.pass(k));
        int heldPower = stepOf(heldAuto, POWER, false);
        AutoBuilder.Step heldStep = heldPower < 0 ? null : heldAuto.steps().get(heldPower);
        boolean line = heldAuto.held().stream().anyMatch(s -> s.startsWith(POWER.label() + ": the ask for " + heldCard.count() + " " + heldCard.template().getName() + " has ")
                && s.contains("raised; the plants standing and on site already serve all the load the wires will carry by its opening, and the wires to carry more could not be ordered with it."));
        OUT.println("   (c) the pass:" + said(heldAuto));
        for (String s : heldAuto.held()) OUT.println("   held: " + (s.length() > 240 ? s.substring(0, 240) : s));
        check("(c) the plants are held whole (" + (heldStep == null ? "no step" : heldStep.ordered() + " of " + heldStep.wanted() + ", cut " + heldStep.cut()) + "; the frontier allows " + none
                + ") with the ask's line, the ask open and what it raised unchanged (" + (heldAuto.askFor(POWER) == null ? "closed" : heldAuto.askFor(POWER).raised()) + " of " + raised + ")",
                none == 0 && heldStep != null && heldStep.ordered() == 0 && heldStep.cut() == AutoBuilder.Cut.WIRES && line && heldAuto.askFor(POWER) != null && heldAuto.askFor(POWER).raised() == raised);
    }

    // ------------------------------------------------------------------------------------------------------------------ 8

    /** Sections 8, 9: the distribution units ordered before the pass (8: so many that the first of them opens after the plant; 9: few, so that it opens before). */
    static final long FRONTIER_UNITS = 2, PROMISE_UNITS = 3;

    /**
     * The town of sections 8 and 9: the Small Steam Station the one plant known, the founding wires with a peak that passes them at the year's high season,
     * `units` distribution units ordered through the game's own build path, the automatic builder on at slack 0 (and so no unit standing).
     */
    static Game wiresTown(String label, long units) {
        Game g = town(label, STEAM);
        advanceTo(g, lowestMonth());
        drawPast(g, UNDER_CARRY * g.getServicesManager().getUtilitiesHandler().getFoundingGridKw());
        quietly(() -> {
            if (units > 0) g.buildStack(g.getBuildingManager().getTemplate(Substations.DISTRIBUTION_ID), units, false);
            g.getAutoBuilder().setSlack(0, null);
            g.getAutoBuilder().setOn(true, null);
        });
        return g;
    }

    /** The months from the plants' opening (the first run month with more plants standing than at the start) to the wires' (the first with more grid units), and the kW-months the plants offered past the carry in them. */
    static double idleBefore(Delivery d, long plants0, long grid0, double carry, int[] bounds) {
        bounds[0] = bounds[1] = -1;
        for (int i = 0; i < d.standing.size(); i++) {
            if (bounds[0] < 0 && d.standing.get(i) > plants0) bounds[0] = i;
            if (bounds[1] < 0 && d.gridStanding.get(i) > grid0) bounds[1] = i;
        }
        double idle = 0;
        if (bounds[0] >= 0 && bounds[1] > bounds[0]) for (int i = bounds[0]; i < bounds[1]; i++) idle += Math.max(0, d.offeredBy.get(i) - carry);
        return idle;
    }

    /** Section 8: the months run, the law on and off. */
    static final int FRONTIER_MONTHS = 36;

    static void frontier() {
        OUT.println("--- 8. the frontier: wires that carry the load at the plant's date and not in the months before ---");
        Game g = wiresTown("frontier", FRONTIER_UNITS);
        AutoBuilder auto = g.getAutoBuilder();
        BuildAdvice.Suggestion card = powerCard(g);
        BuildAdvice.Ahead p = card == null ? null : card.ahead();
        if (card == null || p == null) { check("fixture: the town has a power card", false); return; }
        Frontier all = new Frontier(g, card, true, false), dateOnly = new Frontier(g, card, false, false);
        long least = all.least(), atDate = dateOnly.least();
        double lead = AutoBuilder.wait(g, card, Math.max(1, least));
        int wiresOpen = openMonth(g, building(g, GRID, false), 1, card.template(), least, 80);
        int short_ = 0;
        for (double d : all.dates(least)) if (!all.exempt(d) && all.wires(least, d) < 1) short_++;
        OUT.printf("   the card %s x %d, least %d (date alone %d): opens in %.1f months of its date %.1f; the first grid unit opens in month %d; wires short in %d of %d months%n",
                card.template().getName(), card.count(), least, atDate, lead, p.months(), wiresOpen, short_, all.dates(least).size());
        check("fixture: the plant opens (" + lead + ") well before its date (" + p.months() + ") and the first grid unit on site opens between the two (month " + wiresOpen + ")",
                lead + 3 <= p.months() && wiresOpen > lead && wiresOpen < p.months());
        check("fixture: at its date the wires carry the load (" + all.wires(least, p.months()) + ") and in " + short_ + " months before it they do not, the plants on site serving none of it",
                all.wires(least, p.months()) >= 1 && short_ >= 1);
        check("fixture: the date alone would allow more (" + atDate + ") than the frontier (" + least + ")", atDate > least);
        long before = plantsStanding(g), units = gridUnits(g);
        double carry = Substations.costs(g).carryKw();
        quietly(() -> auto.pass(g));
        AutoBuilder.Step power = null;
        for (AutoBuilder.Step s : auto.steps()) if (s.measure().equals(POWER) && power == null) power = s;
        OUT.println("   the pass:" + said(auto));
        check("the order is cut WIRES to the frontier's count (" + (power == null ? "none" : power.ordered() + " of " + power.wanted() + ", cut " + power.cut()) + "; " + least + ")",
                power != null && power.cut() == AutoBuilder.Cut.WIRES && power.ordered() == least);
        // The months run, with the law on and with it off: the plants' orders open before the wires, and what they offer past the carry in the months between is what
        // the wires' opening leaves idle (a month the founding plants alone offer the carry; the first plant past it is wire-bound if the peak passes it, with the
        // law or without it). The law's cut leaves less of it idle than 0.9.218's builder does.
        Delivery on = new Delivery("on"), off = new Delivery("off");
        on.passed(g);
        Run r = run(g, null, FRONTIER_MONTHS, on, true);
        Game h = wiresTown("frontier-off", FRONTIER_UNITS);
        long before2 = plantsStanding(h), units2 = gridUnits(h), offFirst;
        AutoBuilder.wiresOffForTest = true;
        try {
            quietly(() -> h.getAutoBuilder().pass(h));
            off.passed(h);
            offFirst = stepOf(h.getAutoBuilder(), POWER, true) < 0 ? 0 : h.getAutoBuilder().steps().get(stepOf(h.getAutoBuilder(), POWER, true)).ordered();
            run(h, null, FRONTIER_MONTHS, off, true);
        } finally { AutoBuilder.wiresOffForTest = false; }
        int[] a = new int[2], b = new int[2];
        double idleOn = idleBefore(on, before, units, carry, a), idleOff = idleBefore(off, before2, units2, carry, b);
        OUT.printf("   %d months run: law on orders %d plant units, the plants open in month %d and the wires in month %d, %.0f kW-months offered past the carry between, %d wire-bound; law off %d units, months %d and %d, %.0f kW-months, %d wire-bound%n",
                r.months, on.plantUnits, a[0], a[1], idleOn, on.wireBound, off.plantUnits, b[0], b[1], idleOff, off.wireBound);
        check("fixture: in both runs the plants open (months " + a[0] + ", " + b[0] + ") before the wires do (" + a[1] + ", " + b[1] + ")", a[0] >= 0 && a[1] > a[0] && b[0] >= 0 && b[1] > b[0]);
        check("from the plants' opening to the wires' the law's cut leaves less offered past the carry than the date alone's (0.9.218's) order does (" + idleOn + " against " + idleOff + " kW-months)",
                a[1] > a[0] && b[1] > b[0] && idleOn < idleOff);
        check("...the first pass ordering fewer plant units (" + power.ordered() + " against " + offFirst + ")", power.ordered() < offFirst);
    }

    // ------------------------------------------------------------------------------------------------------------------ 9

    /**
     * The grid's soonest first unit on site as the crew guard reads it (AutoBuilder.wiresDelayed(): each site's quote or its foreign crews' floor, the later), without
     * and with `n` of the suggestion on the crews: {before, after}.
     */
    static double[] firstUnit(Game g, BuildAdvice.Suggestion s, long n) {
        BuildingManager bm = g.getBuildingManager();
        double output = g.getBuildingOutputAtEveryPost(), points = orderPoints(g, s.template(), n), before = Double.POSITIVE_INFINITY, after = Double.POSITIVE_INFINITY;
        for (Map.Entry<BuildingsTemplate, Long> e : building(g, GRID, false).entrySet()) {
            if (e.getValue() <= 0) continue;
            BuildingsTemplate t = e.getKey();
            double floor = bm.abroadUnitFloor(t, 1);
            double old = Math.max(bm.siteUnitMonths(t, 1, output), floor), proposed = Math.max(bm.siteUnitMonthsAfter(t, 1, output, s.template(), n, points), floor);
            if (Double.isFinite(old)) before = Math.min(before, old);
            if (!Double.isNaN(proposed)) after = Math.min(after, proposed);
        }
        return new double[] {before, after};
    }

    /** Whether the guard holds that reading: the first unit opens later with the order, and past the earliest due. */
    static boolean guardHolds(Game g, double[] reading, double firstDue) {
        return Double.isFinite(reading[0]) && reading[1] > reading[0] + 1e-9 && g.getMonth() + Math.ceil(reading[1] - 1e-9) > firstDue;
    }

    /** Whether the queue already reads the grid's first unit past the due, with no order of the plant's. */
    static boolean unitLate(Game g, double firstDue) {
        BuildingManager bm = g.getBuildingManager();
        double output = g.getBuildingOutputAtEveryPost();
        for (Map.Entry<BuildingsTemplate, Long> e : building(g, GRID, false).entrySet())
            if (e.getValue() > 0 && g.getMonth() + Math.ceil(Math.max(bm.siteUnitMonths(e.getKey(), 1, output), bm.abroadUnitFloor(e.getKey(), 1)) - 1e-9) > firstDue) return true;
        return false;
    }

    static final String PROMISE_WORDS = "waits to preserve the opening of the wires Power's plants were ordered with";

    /**
     * While a due stands, the town's draw grows until the pass would place an order the wires allow whose first unit the guard reads later and past the earliest due;
     * then the pass holds it. `counter`: the first order whose reading leaves the unit on time or unslowed is placed on the way, and the plants it puts on the crews
     * leave the queue reading the unit past the due already (the case the first reading of 0.9.219 left unprotected: it held only a unit read on time).
     */
    static void heavyHeld(Game g, String what, boolean counter) {
        AutoBuilder auto = g.getAutoBuilder();
        double first = auto.wiresDue().first();
        check(what + "fixture: the queue reads the grid's first unit by the earliest due (month " + first + ") before any order", !unitLate(g, first));
        BuildAdvice.Suggestion heavy = null;
        double[] reading = null;
        boolean counterDone = !counter;
        double peak0 = wires(g)[1];
        for (double more = 1.05; more <= 3.0 && heavy == null; more += .1) {
            drawPast(g, more * peak0);
            BuildAdvice.Suggestion c = powerCard(g);
            if (c == null || c.ahead() == null) continue;
            long allowed = Math.min(c.count(), new Frontier(g, c, true, false).least());
            if (allowed < 1) continue;
            double[] one = firstUnit(g, c, 1), whole = firstUnit(g, c, allowed);
            if (guardHolds(g, one, auto.wiresDue().first())) { heavy = c; reading = one; }
            else if (!counterDone && !guardHolds(g, whole, auto.wiresDue().first())) {
                counterDone = true;
                quietly(() -> auto.pass(g));
                int at = stepOf(auto, POWER, false);
                AutoBuilder.Step s = at < 0 ? null : auto.steps().get(at);
                boolean line = auto.held().stream().anyMatch(h -> h.contains(PROMISE_WORDS));
                OUT.printf("   %scounter-case: %d x %s, the first unit reads %.2f months without it and %.2f with (due month %.0f, now %d); the pass:%s%n", what, allowed, c.template().getName(),
                        whole[0], whole[1], auto.wiresDue().first(), g.getMonth(), said(auto));
                check(what + "an order whose reading leaves the first unit on time or unslowed is placed (" + (s == null ? "no step" : s.ordered() + " of " + allowed + ", cut " + s.cut()) + ")",
                        s != null && s.ordered() == allowed && s.cut() != AutoBuilder.Cut.BUILDERS && !line);
            }
        }
        OUT.printf("   %sthe order the pass would place: %s; its first unit reads %s months without it and %s with one unit of it, the earliest due month %.0f, now %d%n", what,
                heavy == null ? "none" : heavy.count() + " x " + heavy.template().getName(), reading == null ? "-" : String.format("%.2f", reading[0]), reading == null ? "-" : String.format("%.2f", reading[1]),
                first, g.getMonth());
        check(what + "fixture: the order the pass would place opens the grid's first unit later (" + (reading == null ? "none" : reading[1] + " against " + reading[0]) + ") and past the earliest due, the queue already reading it past (" + unitLate(g, first) + ")",
                heavy != null && counterDone && guardHolds(g, reading, auto.wiresDue().first()) && unitLate(g, first));
        if (heavy == null) return;
        quietly(() -> auto.pass(g));
        OUT.println("   " + what + "a pass:" + said(auto));
        for (String s : auto.held()) OUT.println("   held: " + (s.length() > 240 ? s.substring(0, 240) : s));
        String line = POWER.label() + " " + PROMISE_WORDS + ".";
        int at = stepOf(auto, POWER, false);
        check(what + "the order is held by the builders (" + (at < 0 ? "no step" : auto.steps().get(at).ordered() + " of " + auto.steps().get(at).wanted() + ", cut " + auto.steps().get(at).cut())
                + ") with \"" + line + "\"", at >= 0 && auto.steps().get(at).ordered() == 0 && auto.steps().get(at).cut() == AutoBuilder.Cut.BUILDERS && auto.held().contains(line));
    }

    static void promise() {
        OUT.println("--- 9. the promise: the wires a plant was ordered with open by its month, and hold every other order that would put them past it ---");
        Game g = wiresTown("promise", PROMISE_UNITS);
        AutoBuilder auto = g.getAutoBuilder();
        BuildAdvice.Suggestion card = powerCard(g);
        if (card == null || card.ahead() == null) { check("fixture: the town has a power card", false); return; }
        Frontier f = new Frontier(g, card, true, false);
        long least = f.least();
        double due = f.due(least);
        int month0 = g.getMonth();
        OUT.printf("   the card %s x %d, the frontier allows %d; the plant relies on the wires on site from month +%.0f%n", card.template().getName(), card.count(), least, due);
        check("fixture: grid units on order and the standing wires short in a month the plant stands, the plants on site serving none of it (due +" + due + ")",
                least >= 1 && Double.isFinite(due) && BuildAdvice.units(building(g, GRID, false)) > 0);
        if (!Double.isFinite(due)) return;
        quietly(() -> auto.pass(g));
        OUT.println("   the pass:" + said(auto) + "; wiresDue " + auto.wiresDue());
        check("the plant is placed (step " + stepOf(auto, POWER, true) + ") and the law records the month it relies on the wires by (" + (month0 + due) + "): wiresDue " + auto.wiresDue(),
                stepOf(auto, POWER, true) >= 0 && auto.wiresDue().contains(month0 + due));
        double by = month0 + due;
        long counted = BuildAdvice.units(WorksValue.openByAfter(g, building(g, GRID, false), due, null, 0, 0));
        long units0 = gridUnits(g);
        // The save keeps the due: the state through Gson into the city another game's.
        AutoBuilder.State back = new com.google.gson.Gson().fromJson(new com.google.gson.Gson().toJson(auto.toState()), AutoBuilder.State.class);
        Game other2 = city("promise-load", 1);
        quietly(() -> other2.getAutoBuilder().restore(back));
        check("a save round trip keeps the dues (" + auto.wiresDue() + " and " + other2.getAutoBuilder().wiresDue() + ")", !auto.wiresDue().isEmpty() && auto.wiresDue().equals(other2.getAutoBuilder().wiresDue()));
        // While the due stands, an order that would put the first unit past it. No other measure not lacked now could be caused in this town (water's works retired one
        // at a time never put its card up, and General care lacked at the first): the town's draw grows, so that the pass orders more plants, and power's own orders
        // are bound by the promise as the rest are (a plant that put its predecessor's wires past its opening would leave both behind them).
        heavyHeld(g, "", true);
        // Run on: the units the due counted are open by its month, and it is gone the month after it passes.
        int to = (int) Math.ceil(by) - g.getMonth();
        Delivery trace = new Delivery("on");
        Run ran = run(g, null, to, trace, true);
        for (String t : ran.dues) OUT.println("   due: " + t);
        long open = gridUnits(g) - units0;
        OUT.printf("   month %d (due by %.0f): %d of the %d units the due counted are open; wiresDue %s%n", g.getMonth(), by, open, counted, auto.wiresDue());
        check("the grid units the due counted (" + counted + ") are open by its month (" + open + ")", counted >= 1 && open >= counted);
        months(g, 2);
        check("the due is gone the month after it passes (month " + g.getMonth() + ", wiresDue " + auto.wiresDue() + ")", !auto.wiresDue().contains(by));
    }

    // ------------------------------------------------------------------------------------------------------------------ 10

    /** Section 10: the units on the rushed site, the plants whose points share its crews (the chosen count, and those the search tried), and the months run counting the grid's units. */
    static final long RUSHED_UNITS = 3, RUSHED_PLANTS = 100;
    static final long[] RUSHED_SEARCH = {RUSHED_PLANTS};

    /** Sections 10, 11: the months run counting the grid's units standing. */
    static final int GRID_MONTHS = 70;

    /** The grid units open by each month 0..most of `most` months from now: the game's own count, every month's. */
    static long[] gridOpened(Game g, int most) {
        long[] opened = new long[most + 1];
        long units0 = gridUnits(g);
        for (int d = 1; d <= most; d++) {
            months(g, 1);
            opened[d] = gridUnits(g) - units0;
        }
        return opened;
    }

    /** The first month by which `k` units are counted open in a by-month count, 0 for never within it. */
    static int monthOf(long[] byMonth, int k) {
        for (int d = 0; d < byMonth.length; d++) if (byMonth[d] >= k) return d;
        return 0;
    }

    static void rushed() {
        OUT.println("--- 10. a rushed grid site: the units the wires are credited by a date are no more than open by it ---");
        for (long plants : RUSHED_SEARCH) rushed(plants, plants == RUSHED_PLANTS);
    }

    static void rushed(long plants, boolean asserted) {
        Game g = town("rushed" + plants, STEAM);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate unit = bm.getTemplate(Substations.DISTRIBUTION_ID), plant = named(g, STEAM);
        String key = ConstructionControl.keyOf(unit);
        quietly(() -> g.buildStack(unit, RUSHED_UNITS, false));
        boolean rushed = g.rushSite(key, true) && bm.getControl().isRushed(key);
        Map<BuildingsTemplate, Long> grid = building(g, GRID, false);
        double points = orderPoints(g, plant, plants), output = g.getBuildingOutputAtEveryPost();
        // The readings: the model's credit with the plant on the crews, and the quote alone (siteUnitMonths(), what the credit read for a rushed site's later units
        // before REV-PW9-G 4: each unit searched by it once the first unit's quote with the plant had passed).
        double afterFirst = bm.siteUnitMonthsAfter(unit, 1, output, plant, plants, points);
        long[] credit = new long[GRID_MONTHS + 1], alone = new long[GRID_MONTHS + 1], old = new long[GRID_MONTHS + 1];
        StringBuilder quotes = new StringBuilder();
        for (int k = 1; k <= RUSHED_UNITS; k++) quotes.append(String.format(" %d: %.2f", k, bm.siteUnitMonths(unit, k, output)));
        for (int d = 0; d <= GRID_MONTHS; d++) {
            credit[d] = BuildAdvice.units(WorksValue.openByAfter(g, grid, d, plant, plants, points));
            alone[d] = BuildAdvice.units(WorksValue.openByAfter(g, grid, d, null, 0, 0));
            if (d >= afterFirst) for (int k = 1; k <= RUSHED_UNITS; k++) if (bm.siteUnitMonths(unit, k, output) <= d) old[d] = k;
        }
        quietly(() -> g.buildStack(plant, plants, false));
        long[] actual = gridOpened(g, GRID_MONTHS);
        int over = 0, oldOver = 0, slowed = 0, lateUnits = 0;
        for (int d = 0; d <= GRID_MONTHS; d++) {
            if (credit[d] > actual[d]) over++;
            if (old[d] > actual[d]) oldOver++;
            if (credit[d] < alone[d]) slowed++;
        }
        StringBuilder real = new StringBuilder(), cred = new StringBuilder(), oldMonths = new StringBuilder();
        for (int k = 1; k <= RUSHED_UNITS; k++) {
            real.append(' ').append(monthOf(actual, k));
            cred.append(' ').append(monthOf(credit, k));
            oldMonths.append(' ').append(monthOf(old, k));
            if (monthOf(actual, k) > bm.siteUnitMonths(unit, k, output) + 1) lateUnits++;
        }
        OUT.printf("   %d plants (%.0f points): quote alone by units%s; the first unit's quote with them %.2f; openings in months: real%s, credited by the model%s, credited by the old reading%s; over-credited months: model %d, old %d%n",
                plants, points, quotes, afterFirst, real, cred, oldMonths, over, oldOver);
        if (!asserted) return;
        check("fixture: the site of " + RUSHED_UNITS + " units is rushed and all of them open within " + GRID_MONTHS + " months (" + actual[GRID_MONTHS] + ")", rushed && actual[GRID_MONTHS] == RUSHED_UNITS);
        check("fixture: the plant's " + points + " points slow the rushed site (credited fewer than without it in " + slowed + " months)", slowed >= 1);
        check("fixture: the real openings with the plant fall more than a month past the quote alone for " + lateUnits + " of the units, and the old reading credits more than open in " + oldOver + " months",
                lateUnits >= 1 && oldOver >= 1);
        check("the units credited by a date are no more than open by it, in each of " + (GRID_MONTHS + 1) + " months (" + over + " over)", over == 0);
    }

    // ------------------------------------------------------------------------------------------------------------------ 11

    /** Section 11: the units on the site handed abroad, and the months the foreign crews take on the first of them. */
    static final long ABROAD_UNITS = 3;
    static final double ABROAD_FIRST_MONTHS = 30;

    static void abroad() {
        OUT.println("--- 11. an abroad grid site: no unit is credited before the foreign crews can build it, and no more than open by a date ---");
        Game g = town("abroad", STEAM);
        BuildingManager bm = g.getBuildingManager();
        BuildingsTemplate unit = bm.getTemplate(Substations.DISTRIBUTION_ID);
        String key = ConstructionControl.keyOf(unit);
        quietly(() -> g.buildStack(unit, ABROAD_UNITS, false));
        BuildingsStacks stack = bm.getStack(unit);
        double crews = bm.siteUnitMonths(unit, 1, g.getBuildingOutputAtEveryPost()), crewsLast = bm.siteUnitMonths(unit, ABROAD_UNITS, g.getBuildingOutputAtEveryPost());
        BuildingManager.AbroadTerms terms = bm.abroadTermsOf(unit, "City", g.orderWaitOutput());
        ConstructionControl.Abroad rec = terms == null ? null : bm.handSiteAbroad(terms, g.getMonth(), 0, 0, 0);
        if (rec == null) {
            rec = new ConstructionControl.Abroad();
            rec.key = key;
            rec.templateId = unit.getId();
            rec.building = unit.getName();
            rec.payer = "City";
            rec.month = g.getMonth();
            rec.points = rec.owed = stack.owedPoints();
            stack.handAbroad("City");
            bm.getControl().addAbroad(rec);
        }
        rec.pace = stack.pointsOfFirst(1) / ABROAD_FIRST_MONTHS;
        boolean handed = bm.getControl().abroadOf(key) != null;
        Map<BuildingsTemplate, Long> grid = building(g, GRID, false);
        double[] floor = new double[(int) ABROAD_UNITS + 1];
        for (int j = 1; j <= ABROAD_UNITS; j++) floor[j] = bm.abroadUnitFloor(unit, j);
        long[] credit = new long[GRID_MONTHS + 1];
        for (int d = 0; d <= GRID_MONTHS; d++) credit[d] = BuildAdvice.units(WorksValue.openByAfter(g, grid, d, null, 0, 0));
        long[] actual = gridOpened(g, GRID_MONTHS);
        int early = 0, over = 0;
        StringBuilder row = new StringBuilder();
        for (int d = 0; d <= GRID_MONTHS; d++) {
            long allowed = 0;
            for (int j = 1; j <= ABROAD_UNITS; j++) if (floor[j] <= d) allowed = j;
            if (credit[d] > allowed) early++;
            if (credit[d] > actual[d]) over++;
            if (d == 0 || credit[d] != credit[d - 1] || actual[d] != actual[d - 1]) row.append(String.format(" %d:%d/%d", d, credit[d], actual[d]));
        }
        OUT.printf("   terms %s; the crews alone would open the first unit in %.1f months; the foreign crews' floors %.1f %.1f %.1f; by month, credited/open:%s%n",
                terms == null ? "none (the record made by hand)" : "struck", crews, floor[1], floor[2], floor[3], row);
        check("fixture: the site is handed abroad at a pace that opens its last unit (" + floor[(int) ABROAD_UNITS] + " months) later than the city's crews would (" + crewsLast + ")",
                handed && Double.isFinite(floor[(int) ABROAD_UNITS]) && floor[(int) ABROAD_UNITS] > crewsLast + 1);
        check("fixture: all the units open within " + GRID_MONTHS + " months (" + actual[GRID_MONTHS] + ")", actual[GRID_MONTHS] == ABROAD_UNITS);
        check("no unit is credited before the foreign crews' floor (abroadUnitFloor) in any of " + (GRID_MONTHS + 1) + " months (" + early + " early)", early == 0);
        check("no more units are credited by a date than open by it (" + over + " months over)", over == 0);
    }

    // ------------------------------------------------------------------------------------------------------------------ 12

    /** Section 12: the units on the site that is cancelled. */
    static final long CANCELLED_UNITS = 3;

    static void cancelled() {
        OUT.println("--- 12. a cancelled grid site: the law reads no carry from it, as in the same town with no grid site ---");
        Game g = wiresTown("cancelled", CANCELLED_UNITS);
        BuildingManager bm = g.getBuildingManager();
        AutoBuilder auto = g.getAutoBuilder();
        String key = ConstructionControl.keyOf(bm.getTemplate(Substations.DISTRIBUTION_ID));
        BuildAdvice.Suggestion card = powerCard(g);
        if (card == null || card.ahead() == null) { check("fixture: the town has a power card", false); return; }
        long counted = new Frontier(g, card, true, true).least();
        boolean cancelling = g.cancelSite(key, true) && bm.getControl().isCancelling(key);
        long excluded = new Frontier(g, card, true, false).least();
        Game h = wiresTown("cancelled-none", 0);
        BuildAdvice.Suggestion plain = powerCard(h);
        long none = plain == null ? -1 : frontierLeast(h, plain);
        OUT.printf("   the card %s x %d: allowed %d counting the cancelled units, %d without them; the same town with no grid site %d%n", card.template().getName(), card.count(), counted, excluded, none);
        check("fixture: the site is being cancelled and its units would carry the plant if counted (" + counted + " units allowed against " + excluded + "; the same town with no grid site " + none + ")",
                cancelling && counted > excluded && excluded == none);
        if (plain == null) return;
        quietly(() -> auto.pass(g));
        quietly(() -> h.getAutoBuilder().pass(h));
        AutoBuilder other = h.getAutoBuilder();
        int gridA = stepOf(auto, GRID, true), powerA = stepOf(auto, POWER, false), gridB = stepOf(other, GRID, true), powerB = stepOf(other, POWER, false);
        OUT.println("   the pass, site cancelled:" + said(auto) + "; the same town with no grid site:" + said(other));
        AutoBuilder.Step planA = powerA < 0 ? null : auto.steps().get(powerA), planB = powerB < 0 ? null : other.steps().get(powerB);
        check("the plant is held or cut as in the same town with no grid site (" + (planA == null ? "none" : planA.ordered() + " of " + planA.wanted() + ", cut " + planA.cut()) + " against "
                + (planB == null ? "none" : planB.ordered() + " of " + planB.wanted() + ", cut " + planB.cut()) + "), not placed whole as the cancelled units would have it (" + counted + " allowed)",
                planA != null && planB != null && planA.ordered() == planB.ordered() && planA.cut() == planB.cut() && planA.cut() == AutoBuilder.Cut.WIRES && planA.ordered() < card.count());
    }

    // ------------------------------------------------------------------------------------------------------------------ 13

    static void money() {
        OUT.println("--- 13. the money audit stays exact over the months run ---");
        double worst = AUDIT.stream().mapToDouble(Math::abs).max().orElse(Double.NaN);
        OUT.printf("   %d months; the worst residual %.3g (a cent is %.3g)%n", AUDIT.size(), worst, MoneyAudit.CENT);
        check("every one of the " + AUDIT.size() + " months' money audits closes to the cent", AUDIT.size() >= LEAST_MONTHS && worst <= MoneyAudit.CENT);
    }



    public static void main(String[] args) {
        physics();
        ordered();
        held(true);
        held(false);
        leftAlone();
        spreadLeftAlone();
        season();
        searched();
        reoffered();
        frontier();
        promise();
        rushed();
        abroad();
        cancelled();
        money();
        OUT.println("PowerWiresCheck: " + fails + " failures");
        if (fails > 0) System.exit(1);
    }
}
