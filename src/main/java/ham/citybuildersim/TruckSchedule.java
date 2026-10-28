package ham.citybuildersim;

import ham.citybuildersim.sectors.Freight;
import ham.citybuildersim.sectors.Haulage;
import java.util.ArrayList;
import java.util.List;

/**
 * The month's trucks, as a pure function of time: which loads Haulage's
 * trucks carry, when each reaches its gate, and where each one is at any
 * moment - TrainSchedule's shape (0.8.8, batch FR5;
 * docs/studies/spec-freight-and-infrastructure.md 4.7, D21), on whole trips
 * since FR9. Model only: ui/MapView draws it on the boats' clock, on
 * CityMap.tripsDrawn(), at the near view alone (drawnAt()) and only while the
 * player's setting is on (GamePrefs.isTrucksOnMap(), on by default).
 *
 * WHY. Haulage (FR2) carries every bulk tonne's city leg (since FR6 less
 * the railway's share of the city's own and the quay's bulk) and the highways
 * and access roads (FR7) are drawn, but the roads stood empty while the sea
 * had its boats and the railway its trains. So A TRUCK IS A SCHEDULE ENTRY,
 * NOT AN OBJECT, as a boat and a train are: a load, a route and an arrival
 * hashed from (load, month, truck number), and a truck's place is
 * route(call, t). Nothing is simulated, stored or saved; panning away and
 * back shows the same truck in the same place, and a city that hauls
 * nothing has no trucks. Nothing in the model reads it, and what it counts
 * from is read-only (Freight.truckedIn() and the others), so the map
 * changes no figure of the game. FR5 drew two kinds of truck on two
 * schedules - the highways' to each arm's end, a ramp inside the city, where
 * they stood and turned back, and the access roads', which came out of the
 * street grid from nowhere - so a truck vanished in the middle of the city
 * (Jerus, 2026-10-28: "disappear into the void... but still inside the
 * city"). FR9 gives each truck ONE WHOLE TRIP, from the world to a works'
 * gate and back, and a district draws its own works' share of the loads.
 *
 * CALLS. The month billed's city-leg tonnes by load (sectors.Freight.truckedIn(),
 * truckedOut() and truckedHome(): what Haulage's trucks were asked, which
 * come to Haulage.getTonnes()), at the share Haulage carried of them
 * (getCarried() over getTonnes(), a fleet too small leaves tonnes on the
 * ground) and a share of each load (`shares`, by Load's order: a district's
 * works' truck tonnes of it over the city's - sharesOf(), perBuilding() and
 * truckedBy(), the model's own Move.truck() read), over a truck's load,
 * Haulage.TONNES_A_LOAD. A fraction of a load is a whole truck in the share
 * of months its fraction says (BoatSchedule.callsFor()). Past MOST_A_MONTH
 * loads a month (the city's, every load before `shares`, so every district's
 * schedule stands for one N) each truck drawn stands for stands() of them,
 * every load alike, N a round 1, 2, 5, 10, 20, 50... so the legend reads
 * "trucks: one per N loads" and a big city's roads read as busy and not as a
 * blur.
 *
 * TIME. The boats' month, MONTH_SECONDS. A truck runs its route, from where
 * it comes in to its gate, over its route's legSeconds() - the route's length
 * over TrainSchedule.TRAIN_PLOTS_A_SECOND, the trains' pace, no less than
 * LEG_SECONDS and no more than LEG_MOST - stands DWELL_SECONDS at the gate
 * and runs back out; its arrival is spread through the month by its hash,
 * between LEG_MOST and the month's end less LEG_MOST and a stand, so its
 * whole visit is inside the month whatever its route and a truck never
 * appears or vanishes at the month's turn. An import arrives loaded and
 * leaves empty, an export arrives empty and leaves loaded, and a tonne
 * bought at home is carried as an import is. A truck fades in over the first
 * fadeTo plots of its route, the world line's.
 *
 * ROUTES (CityMap.tripsDrawn(); the map's, never the model's). A trip is a
 * route a truck drives whole, from where its load enters to the gate of the
 * work it goes to: a WORLD trip (Route.through(true, ...)) from WORLD_PLOTS
 * out along the highway's world line, in along the highways to a ramp, along
 * the streets to an access road's end and down it to its work's gate, which
 * takes imports and exports alone (what crosses the city's edge comes in by
 * it and leaves by it); and a HOME trip (Route.home()) from the gate of
 * another work whose road comes to the same ramp, along the streets to this
 * road's end and down it to this work's gate, which takes the tonnes bought
 * at home alone (they cross no edge). A load goes to one of the routes that
 * take it, by a hash. A load no route takes has no truck, and a city with no
 * routes has none at all. Route.through(false, ...), the access road's own
 * route of FR5 that takes every load, is kept for the harnesses.
 */
public final class TruckSchedule {

    /** Seconds of trucks a game month: the boats' (spec-freight 4.7, as the trains). */
    public static final double MONTH_SECONDS = BoatSchedule.MONTH_SECONDS;

    /** The shortest leg a truck takes to run its route, in or out, in seconds: 6 (spec-freight 4.7, a visual pace, the trains'), for a route of 300 plots or less. */
    public static final double LEG_SECONDS = 6;

    /** The longest leg, in seconds: 10, the trains' (TrainSchedule.LEG_MOST), for a route of 500 plots or more, so a trip of the whole city's length stays inside the month (a visit is two legs and a stand). */
    public static final double LEG_MOST = TrainSchedule.LEG_MOST;

    /** Seconds a truck stands at its gate, loading or unloading: 2 (spec-freight 4.7). */
    public static final double DWELL_SECONDS = 2;

    /** A route's part along the world line, past the city's ground, in plots: 64, as the trains' (TrainSchedule.WORLD_PLOTS), over which a truck fades in. */
    public static final double WORLD_PLOTS = TrainSchedule.WORLD_PLOTS;

    /** An articulated truck's length in plots: 0.55, 16.5 m of a plot's 30 m (TrainSchedule.CAR_PLOTS, a wagon, is TileRaster.WAGON_LENGTH for scale). */
    public static final double TRUCK_PLOTS = 0.55;

    /** The most trucks a month drawn one for one: 1,000, a truck about every 0.06 s of a 60 s month (spec-freight 4.7; the roads take many more marks than the rails). */
    public static final int MOST_A_MONTH = 1000;

    /** The salt the trucks' hashes start from. */
    private static final long SALT = 0x7C4B_E5E7L;

    /* A truck's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. */
    private static final int GROUP_BITS = 7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS;
    /** The masks that take a key's group, route and arrival back out, once shifted down. */
    private static final long GROUP_MASK = (1L << GROUP_BITS) - 1, ROUTE_MASK = (1L << ROUTE_BITS) - 1, ARRIVAL_MASK = (1L << ARRIVAL_BITS) - 1;
    /** A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. */
    private static final double TICKS = (double) (1L << ARRIVAL_BITS);

    /**
     * A load's way through the city: what it carries each way and its colour's
     * sense (spec-freight 4.7: imports blue, exports orange, empty grey).
     * IMPORT arrives at its gate loaded and leaves empty; EXPORT arrives empty
     * and leaves loaded; HOME, a tonne bought at home, arrives loaded and
     * leaves empty and never crosses the city's edge (an access road's alone).
     */
    public enum Load { IMPORT, EXPORT, HOME }

    /**
     * A truck's way: points in plots, from where it comes in (the highway's
     * world line's far end, another work's gate, or an access road's end on
     * the street grid) to its gate, with each point's distance along it; the
     * first fadeTo plots are faded in. `world` is true for a route from the
     * world line (imports and exports alone), false for an access road's
     * (every load); `home` is true for a trip between two works of the city
     * (the tonnes bought at home alone: through() makes none).
     */
    public record Route(boolean world, boolean home, double[] xs, double[] ys, double[] along, double fadeTo) {
        /** A route through points: their distances along it worked out. A world one takes imports and exports, any other every load. */
        public static Route through(boolean world, double[] xs, double[] ys, double fadeTo) {
            return make(world, false, xs, ys, fadeTo);
        }

        /** ...a home trip through points (FR9): from a work's gate to another's, taking the tonnes bought at home alone. */
        public static Route home(double[] xs, double[] ys, double fadeTo) {
            return make(false, true, xs, ys, fadeTo);
        }

        private static Route make(boolean world, boolean home, double[] xs, double[] ys, double fadeTo) {
            double[] along = new double[xs.length];
            for (int i = 1; i < xs.length; i++) along[i] = along[i - 1] + Math.hypot(xs[i] - xs[i - 1], ys[i] - ys[i - 1]);
            return new Route(world, home, xs.clone(), ys.clone(), along, Math.max(0, fadeTo));
        }

        /** Its length, in plots. */
        public double length() { return along.length == 0 ? 0 : along[along.length - 1]; }

        /** Seconds a truck takes to run it, in or out: its length over TrainSchedule.TRAIN_PLOTS_A_SECOND, no less than LEG_SECONDS and no more than LEG_MOST (the trains' pace). */
        public double legSeconds() { return Math.max(LEG_SECONDS, Math.min(LEG_MOST, length() / TrainSchedule.TRAIN_PLOTS_A_SECOND)); }
    }

    /** One truck: its load, its route among the schedule's, and when it reaches its gate, in month fractions. */
    public record Call(Load load, int route, double arrives) { }

    /** A truck at a moment: its call, its middle in plots, its heading (radians, east 0, south a quarter turn), whether it is loaded, and how much of it shows (0 to 1). */
    public record Truck(Call call, double x, double y, double heading, boolean loaded, double alpha) { }

    /** One load of the month: its seed and its trucks. */
    private record Group(Load load, long seed, int calls) { }

    private final int month;
    private final List<Route> routes;
    private final List<Group> groups;
    private final double[] loads;
    private final int count, stands;

    /* Built on the first ask: the trucks' keys, sorted by arrival; and each route's box, {x0, y0, x1, y1} in plots. */
    private long[] keys;
    private double[][] boxesOf;
    private int touched;

    private TruckSchedule(int month, List<Route> routes, List<Group> groups, double[] loads, int stands) {
        this.month = month;
        this.routes = routes;
        this.groups = groups;
        this.loads = loads;
        this.stands = stands;
        int n = 0;
        for (Group g : groups) n += g.calls();
        this.count = n;
    }

    /** Whether trucks are drawn at a map level with the player's setting: at the near view (MapFrame.L0) alone, and only with the setting on. */
    public static boolean drawnAt(int level, boolean settingOn) {
        return settingOn && level == MapFrame.L0;
    }

    /* =====================================================================
       THE MONTH'S TRUCKS
       ===================================================================== */

    /**
     * The month's schedule from Haulage's month billed: Freight's city-leg
     * truck tonnes by load (Freight.truckedIn(), truckedOut(), truckedHome()),
     * at the share Haulage carried (Haulage.getCarried() over getTonnes()),
     * times `share` (1 for the highways' routes; an access road's part of
     * the city's for a district's), over Haulage.TONNES_A_LOAD; `salt` keeps
     * two schedules of one month apart. A world route takes imports and
     * exports alone; an access road's every load; a home trip home loads
     * alone. None with no freight.
     */
    public static TruckSchedule of(int month, Freight freight, List<Route> routes, double share, long salt) {
        return of(month, freight, routes, sameShare(share), salt);
    }

    /**
     * ...with a share for each load (by Load's order: a district's works'
     * truck tonnes of it over the city's, sharesOf()), since a district that
     * holds a sector's buildings holds its imports and its exports in other
     * measure than the tonnes it bought at home (FR9). N, stands(), is still
     * the city's, from every load before the shares.
     */
    public static TruckSchedule of(int month, Freight freight, List<Route> routes, double[] shares, long salt) {
        double[] loads = new double[Load.values().length];
        if (freight != null) {
            Haulage h = freight.haulage();
            double carried = h == null || !(h.getTonnes() > 0) ? 0 : Math.max(0, Math.min(1, h.getCarried() / h.getTonnes()));
            double k = carried / Haulage.TONNES_A_LOAD;
            loads[Load.IMPORT.ordinal()] = tonnesOf(freight.truckedIn()) * k;
            loads[Load.EXPORT.ordinal()] = tonnesOf(freight.truckedOut()) * k;
            loads[Load.HOME.ordinal()] = tonnesOf(freight.truckedHome()) * k;
        }
        return of(month, loads, routes, shares, salt);
    }

    /**
     * ...from the month's loads counted already, by Load's order, at the
     * share Haulage carried and before `share` (a fraction of a load is a
     * whole truck in the share of months it says): the headless renders'
     * and the harness's way in, with no played city. A load no route takes has no truck.
     */
    public static TruckSchedule of(int month, double[] loadsIn, List<Route> routes, double share, long salt) {
        return of(month, loadsIn, routes, sameShare(share), salt);
    }

    /** ...with a share for each load, by Load's order: the way in the others come to. A share that is missing, not a number or not over 0 is 0. */
    public static TruckSchedule of(int month, double[] loadsIn, List<Route> routes, double[] shares, long salt) {
        routes = routes == null ? new ArrayList<>() : new ArrayList<>(routes);
        double[] loads = new double[Load.values().length];
        double city = 0;
        for (Load l : Load.values()) {
            double v = loadsIn == null || l.ordinal() >= loadsIn.length ? 0 : tonnesOf(loadsIn[l.ordinal()]);
            double part = shares == null || l.ordinal() >= shares.length || !Double.isFinite(shares[l.ordinal()]) || !(shares[l.ordinal()] > 0) ? 0 : shares[l.ordinal()];
            city += v;
            loads[l.ordinal()] = v * part;
        }
        // N is the city's, from every load before the shares (the review, FR5): every district's part then draws a truck for
        // the same N loads, the one the legend gives.
        int stands = standsFor(city);
        List<Group> groups = new ArrayList<>();
        for (Load l : Load.values()) {
            if (!takenBy(routes, l)) continue;
            long seed = seed(month, l, salt);
            int n = BoatSchedule.callsFor(loads[l.ordinal()], stands, seed);
            if (n > 0) groups.add(new Group(l, seed, n));
        }
        return new TruckSchedule(month, routes, groups, loads, stands);
    }

    private static double tonnesOf(double t) { return Double.isFinite(t) && t > 0 ? t : 0; }

    /** One share for every load. */
    private static double[] sameShare(double share) {
        double[] out = new double[Load.values().length];
        java.util.Arrays.fill(out, share);
        return out;
    }

    /** Loads each truck drawn stands for when a month has `loads`: 1 up to MOST_A_MONTH, then the smallest of 1, 2, 5, 10, 20, 50, 100... that brings the trucks to MOST_A_MONTH or fewer. */
    public static int standsFor(double loads) {
        if (!(loads > MOST_A_MONTH)) return 1;
        for (long mag = 1; mag <= 1_000_000_000L; mag *= 10) {
            for (int m : new int[] { 1, 2, 5 }) {
                long n = mag * m;
                if (loads / n <= MOST_A_MONTH) return (int) n;
            }
        }
        return Integer.MAX_VALUE;
    }

    /** Whether a load goes on a route: an import or an export on any but a home trip, a tonne bought at home on an access road or a home trip alone. */
    private static boolean takes(Route r, Load l) { return r.home() ? l == Load.HOME : l != Load.HOME || !r.world(); }

    /** Whether any of the routes a schedule keeps takes a load. */
    private static boolean takenBy(List<Route> routes, Load l) {
        int n = Math.min(routes.size(), (int) ROUTE_MASK - 1);
        for (int i = 0; i < n; i++) if (takes(routes.get(i), l)) return true;
        return false;
    }

    /** The seed of one load in one month; `salt` is mixed into it, so it is mixed into every hash drawn from it. */
    static long seed(int month, Load l, long salt) {
        return ((long) month << 32) ^ ((long) l.ordinal() << 16) ^ SALT ^ World.mix(salt ^ 0x5A17_0F1DL);
    }

    /** When a group's k-th truck reaches its gate, in ticks of a month: hashed through the month between the longest leg (LEG_MOST) and the month's end less that and a stand, so any truck's whole visit is inside it whatever its route. */
    static long arrivalTicks(long seed, int k) {
        double leg = LEG_MOST / MONTH_SECONDS, dwell = DWELL_SECONDS / MONTH_SECONDS;
        double a = leg + World.unit(World.mix(World.mix(seed) ^ ((long) k * 0x9E37_79B9L) ^ 0x7C1L)) * (1 - 2 * leg - dwell);
        return (long) Math.floor(a * TICKS);
    }

    /** The routes a load goes on (their indices): every one that takes it. */
    private int[] routesFor(Load l) {
        int n = Math.min(routes.size(), (int) ROUTE_MASK - 1);
        List<Integer> mine = new ArrayList<>();
        for (int i = 0; i < n; i++) if (takes(routes.get(i), l)) mine.add(i);
        return mine.stream().mapToInt(Integer::intValue).toArray();
    }

    /** The trucks' keys, sorted by arrival, and the routes' boxes; built on the first ask. */
    private void build() {
        if (keys != null) return;
        int[][] mine = new int[Load.values().length][];
        for (Load l : Load.values()) mine[l.ordinal()] = routesFor(l);
        long[] ks = new long[count];
        int at = 0;
        for (int gi = 0; gi < groups.size(); gi++) {
            Group g = groups.get(gi);
            int[] rs = mine[g.load().ordinal()];
            for (int k = 0; k < g.calls(); k++) {
                long route = rs.length == 0 ? 0
                        : 1 + rs[(int) Long.remainderUnsigned(World.mix(g.seed() ^ ((long) k << 24) ^ 0x7C15L), rs.length)];
                ks[at++] = (arrivalTicks(g.seed(), k) << ARRIVAL_SHIFT) | (route << GROUP_BITS) | gi;
            }
        }
        java.util.Arrays.sort(ks);
        double[][] bs = new double[routes.size()][];
        for (int i = 0; i < routes.size(); i++) bs[i] = box(routes.get(i));
        boxesOf = bs;
        keys = ks;
    }

    /** A call out of its key. */
    private Call call(long key) {
        Group g = groups.get((int) (key & GROUP_MASK));
        int route = (int) ((key >>> GROUP_BITS) & ROUTE_MASK) - 1;
        double arrives = ((key >>> ARRIVAL_SHIFT) & ARRIVAL_MASK) / TICKS;
        return new Call(g.load(), route, arrives);
    }

    /* =====================================================================
       WHERE A TRUCK IS
       ===================================================================== */

    /**
     * Where a call's truck is at t (month fractions), or null when it is not
     * on the road: running in over its leg, standing at its gate (loaded as it
     * came for the first half of its stand, as it will leave for the second),
     * or running back out. Pure in (call, t) and the route.
     */
    public static Truck position(Call call, Route route, double t) {
        if (call == null || route == null || route.xs().length == 0) return null;
        double leg = route.legSeconds() / MONTH_SECONDS, dwell = DWELL_SECONDS / MONTH_SECONDS, a = call.arrives(), len = route.length();
        boolean inLoaded = call.load() != Load.EXPORT, outLoaded = call.load() == Load.EXPORT;
        double d;
        boolean outward, loaded;
        if (t >= a - leg && t < a) {
            d = len * (1 - (a - t) / leg); outward = false; loaded = inLoaded;
        } else if (t >= a && t <= a + dwell) {
            d = len; outward = t >= a + dwell / 2; loaded = outward ? outLoaded : inLoaded;
        } else if (t > a + dwell && t <= a + dwell + leg) {
            d = len * (1 - (t - a - dwell) / leg); outward = true; loaded = outLoaded;
        } else {
            return null;
        }
        double[] xs = route.xs(), ys = route.ys(), al = route.along();
        int last = xs.length - 1;
        double x = xs[0], y = ys[0], heading = 0;
        if (last >= 1) {
            int lo = 0, hi = last - 1;
            while (lo < hi) { int m = (lo + hi + 1) >>> 1; if (al[m] <= d) lo = m; else hi = m - 1; }
            double seg = al[lo + 1] - al[lo], u = seg > 0 ? Math.max(0, Math.min(1, (d - al[lo]) / seg)) : 0;
            x = xs[lo] + (xs[lo + 1] - xs[lo]) * u;
            y = ys[lo] + (ys[lo + 1] - ys[lo]) * u;
            double dx = xs[lo + 1] - xs[lo], dy = ys[lo + 1] - ys[lo];
            heading = outward ? Math.atan2(-dy, -dx) : Math.atan2(dy, dx);
        }
        double alpha = route.fadeTo() > 0 ? Math.min(1, Math.max(0, d / route.fadeTo())) : 1;
        return new Truck(call, x, y, heading, loaded, alpha);
    }

    /**
     * The trucks on the roads at t whose middle is in the rectangle [x0, x1) x
     * [y0, y1) in plots, widened by a truck's length, in the order of their
     * arrival: the month's arrivals searched for those whose visit spans t,
     * and only the routes that cross the rectangle looked at (touched()).
     */
    public List<Truck> frame(long x0, long y0, long x1, long y1, double t) {
        build();
        touched = 0;
        List<Truck> out = new ArrayList<>();
        double leg = LEG_MOST / MONTH_SECONDS, dwell = DWELL_SECONDS / MONTH_SECONDS, w = TRUCK_PLOTS;
        long low = (long) Math.max(0, Math.floor((t - dwell - leg) * TICKS)) << ARRIVAL_SHIFT;
        for (int i = BoatSchedule.lower(keys, 0, keys.length, low); i < keys.length; i++) {
            Call c = call(keys[i]);
            if (c.arrives() > t + leg) break;
            if (c.route() < 0) continue;
            double[] b = boxesOf[c.route()];
            if (b[2] + w < x0 || b[0] - w >= x1 || b[3] + w < y0 || b[1] - w >= y1) continue;
            touched++;
            Truck tr = position(c, routes.get(c.route()), t);
            if (tr == null) continue;
            if (tr.x() + w >= x0 && tr.x() - w < x1 && tr.y() + w >= y0 && tr.y() - w < y1) out.add(tr);
        }
        return out;
    }

    /** Whether any route meets the rectangle [x0, x1) x [y0, y1) in plots: the view keeps its frame going while one does. */
    public boolean crosses(long x0, long y0, long x1, long y1) {
        build();
        for (double[] b : boxesOf) if (b[2] >= x0 && b[0] < x1 && b[3] >= y0 && b[1] < y1) return true;
        return false;
    }

    /** A route's box, {x0, y0, x1, y1} in plots, inclusive of its points. */
    static double[] box(Route r) {
        double ax = Double.MAX_VALUE, ay = Double.MAX_VALUE, bx = -Double.MAX_VALUE, by = -Double.MAX_VALUE;
        for (int i = 0; i < r.xs().length; i++) {
            ax = Math.min(ax, r.xs()[i]); bx = Math.max(bx, r.xs()[i]);
            ay = Math.min(ay, r.ys()[i]); by = Math.max(by, r.ys()[i]);
        }
        return new double[] { ax, ay, bx, by };
    }

    /* =====================================================================
       A DISTRICT'S PART OF THE CITY'S LOADS (batch FR9)
       ===================================================================== */

    /**
     * What ONE standing building of each type carries in the month, in
     * truck tonnes by load (Load's order: imports, exports, tonnes bought at
     * home), by the type's index in `types`: its sector's truck tonnes by load
     * (`truckedBy` of the sector's key, truckedBy(Freight, String) in the
     * game) over the city's count of that sector's buildings standing (the sum
     * of `totals` over every type of the sector). A type of no sector ("") or
     * of a sector with none standing carries 0: the tonnes the model puts on
     * a sector are shared among its buildings alike, the one measure the model
     * gives (Haulage does not tally a building). Pure; a missing type is all 0.
     */
    public static double[][] perBuilding(java.util.function.Function<String, double[]> truckedBy, BuildingVisual.Type[] types, long[] totals) {
        int loads = Load.values().length;
        double[][] out = new double[types.length][loads];
        java.util.Map<String, Long> standing = new java.util.HashMap<>();
        for (int t = 0; t < types.length; t++) {
            String sector = types[t] == null ? null : types[t].sector();
            if (sector == null || sector.isEmpty()) continue;
            standing.merge(sector, t < totals.length ? Math.max(0, totals[t]) : 0L, Long::sum);
        }
        java.util.Map<String, double[]> tonnes = new java.util.HashMap<>();
        for (int t = 0; t < types.length; t++) {
            String sector = types[t] == null ? null : types[t].sector();
            if (sector == null || sector.isEmpty()) continue;
            long n = standing.getOrDefault(sector, 0L);
            if (n <= 0) continue;
            double[] by = tonnes.computeIfAbsent(sector, truckedBy);
            for (int l = 0; l < loads && by != null && l < by.length; l++) out[t][l] = tonnesOf(by[l]) / n;
        }
        return out;
    }

    /**
     * The model's own figure for a sector, read: the truck tonnes (Move.truck())
     * of its moves in the month billed (Haulage.movesOf()), by load - landed
     * imports, shipped exports, bought at home. Nothing recomputed; all 0 for
     * no freight, no haulage or a sector that moved nothing.
     */
    public static double[] truckedBy(Freight freight, String sector) {
        double[] out = new double[Load.values().length];
        Haulage h = freight == null ? null : freight.haulage();
        if (h == null || sector == null) return out;
        for (Haulage.Move mv : h.movesOf(sector)) {
            Load l = switch (mv.way()) {
                case LANDED -> Load.IMPORT;
                case SHIPPED -> Load.EXPORT;
                case HOME -> Load.HOME;
            };
            out[l.ordinal()] += tonnesOf(mv.truck());
        }
        return out;
    }

    /**
     * A district's part of the city's truck tonnes, by load: the sum over
     * the types of its count times perBuilding() (what one building of the type
     * carries) over `cityTonnes`, the city's by load (Freight.truckedIn(),
     * truckedOut(), truckedHome()), each in [0, 1] and 0 where the city's is 0.
     * The shares over every district come to the part of the city's tonnes
     * whose sectors have buildings on the map: all of it when all do.
     */
    public static double[] sharesOf(double[][] perBuilding, CityMap.District d, double[] cityTonnes) {
        int loads = Load.values().length;
        double[] mine = new double[loads], out = new double[loads];
        if (perBuilding == null || d == null) return out;
        for (int t = 0; t < perBuilding.length; t++) {
            int n = d.count(t);
            if (n <= 0 || perBuilding[t] == null) continue;
            for (int l = 0; l < loads && l < perBuilding[t].length; l++) mine[l] += n * perBuilding[t][l];
        }
        for (int l = 0; l < loads; l++) {
            double city = cityTonnes == null || l >= cityTonnes.length ? 0 : tonnesOf(cityTonnes[l]);
            out[l] = city > 0 ? Math.max(0, Math.min(1, mine[l] / city)) : 0;
        }
        return out;
    }

    /* ----- reads ----- */

    public int month()            { return month; }
    public List<Route> routes()   { return java.util.Collections.unmodifiableList(routes); }

    /** Every truck of the month, by arrival. Built on the first ask. */
    public List<Call> calls() {
        build();
        List<Call> out = new ArrayList<>(keys.length);
        for (long k : keys) out.add(call(k));
        return out;
    }

    /** The trucks drawn this month: counted when the schedule is made, nothing built. */
    public int callCount() { return count; }

    /** Loads each drawn truck stands for: N in the legend's "one per N loads", 1 for a quiet month. */
    public int stands() { return stands; }

    /** The trucks drawn of one load. */
    public int callsOf(Load l) {
        int n = 0;
        for (Group g : groups) if (g.load() == l) n += g.calls();
        return n;
    }

    /** The month's loads of one kind at the share given, before stands() and whether a route takes them (a fraction is a load not yet whole). */
    public double loads(Load l) { return loads[l.ordinal()]; }

    /** The trucks the last frame() looked at. */
    public int touched() { return touched; }

    @Override public String toString() { return "TruckSchedule[m" + month + ", " + count + " trucks x " + stands + ", " + routes.size() + " routes]"; }
}
