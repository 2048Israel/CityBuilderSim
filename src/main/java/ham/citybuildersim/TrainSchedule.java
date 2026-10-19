package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The month's trains, as a pure function of time: which trains the city's
 * railed tonnes make, when each reaches its yard, and where each one is at
 * any moment - BoatSchedule's shape, almost line for line (0.8.2, batch FR1;
 * docs/studies/spec-freight-and-infrastructure.md 4.4, D16). Model only:
 * ui/MapView draws it on the boats' clock, on CityMap.trainRoutes().
 *
 * WHY. The railway was a share of the freight band and a line on the map
 * with nothing running on it, while the ships already showed the sea trade
 * as boats that follow it exactly. So A TRAIN IS A SCHEDULE ENTRY, NOT AN
 * OBJECT: a kind, a direction, a route and an arrival time hashed from
 * (kind, direction, month, train number), and a train's place is
 * route(call, t). Nothing is simulated, stored or saved; panning away and
 * back shows the same train in the same place, and a city that rails
 * nothing has no trains.
 *
 * CALLS. The month billed's railed tonnes by kind (sectors.Freight.railedIn()
 * and railedOut()) over a train's payload: dry bulk in unit trains of
 * hoppers and liquids in unit trains of tank wagons, UNIT_TONNES each;
 * general cargo in mixed trains of MIXED_TONNES; boxes in intermodal trains
 * of INTERMODAL_BOXES, one train carrying both ways, so the larger direction
 * counts (the boats' rule 3). A fraction of a train is a whole one in the
 * share of months its fraction says (BoatSchedule.callsFor()). Past
 * MOST_A_MONTH trains a month each train drawn stands for stands() of them,
 * every kind alike, so a big city's line reads as busy and not as a blur.
 *
 * TIME. The boats' month, MONTH_SECONDS. A train runs its whole route, from
 * the world line's far end to its siding's, over LEG_SECONDS, stands there
 * DWELL_SECONDS and runs back out; its arrival is spread through the month
 * by its hash, its whole visit inside it, so a train never appears or
 * vanishes at the month's turn. An import arrives full and leaves empty, an
 * export arrives empty and leaves full, and boxes are loaded both ways.
 *
 * ROUTES (CityMap.trainRoutes(); the map's, never the model's). From
 * WORLD_PLOTS out along the railway's world line, in to its gate arm's end,
 * along the main line and the corridors to a yard, through its ladder and
 * onto a siding: one route a yard and siding. Boxes and general cargo go to
 * the freight station; bulk to the other yards when the station is a
 * container yard (boxesByRail()), to every yard when it is not. A train
 * fades in over the world line's part.
 *
 * A TRAIN. A locomotive at each end - distributed power, as a unit train
 * has - so it leaves the way it came with no run-round, and its wagons
 * between: each vehicle's place is its distance along the route behind the
 * train's front (cars()). A frame looks only at the trains on the line now,
 * found by a binary search of the month's arrivals.
 */
public final class TrainSchedule {

    /** Seconds of trains a game month: the boats' (spec-freight 4.4, "the clock is the boats'"). */
    public static final double MONTH_SECONDS = BoatSchedule.MONTH_SECONDS;

    /** Seconds a train takes to run its route, in or out: 6 (spec-freight 4.4, a visual pace). */
    public static final double LEG_SECONDS = 6;

    /** Seconds a train stands on its siding: 3 (spec-freight 4.4). */
    public static final double DWELL_SECONDS = 3;

    /** A route's part along the world line, past the city's ground, in plots: 64, about 1.9 km, over which a train fades in. */
    public static final double WORLD_PLOTS = 64;

    /** A unit train's payload, hoppers or tank wagons: 8,000 t (est., between Europe's 4,000 t block trains [R47] and a 12,100 t North American coal train [R39]). */
    public static final double UNIT_TONNES = 8_000;

    /** A mixed (manifest) train's payload: 1,500 t (Europe's route limits run 1,300 to 2,400 t [R45]). */
    public static final double MIXED_TONNES = 1_500;

    /** An intermodal train's boxes: 100 (a European 750 m train is 50 wagons [R46]; Union Pacific's averaged 173 [R40]). */
    public static final int INTERMODAL_BOXES = 100;

    /** Tonnes a wagon carries, for the count of wagons drawn: 100 (spec-freight 4.4, "the payload / 100 t"). */
    public static final double WAGON_TONNES = 100;

    /** Boxes a flat wagon carries: 2, so an intermodal train is 50 wagons, as [R46]'s. */
    public static final int BOXES_A_WAGON = 2;

    /** The most wagons a train is drawn with: 40 (spec-freight 4.4). */
    public static final int MOST_WAGONS = 40;

    /** A wagon's length in plots: TileRaster.WAGON_LENGTH, about 15 m. */
    public static final double CAR_PLOTS = TileRaster.WAGON_LENGTH;

    /** A locomotive's length in plots: 0.7, about 21 m. */
    public static final double LOCO_PLOTS = 0.7;

    /** The most trains a month drawn one for one: 30, a train about every 2 s (spec-freight 4.4's worked example, 25, "reads as a busy line, not a blur"). */
    public static final int MOST_A_MONTH = 30;

    /** The salt the trains' hashes start from. */
    private static final long SALT = 0x7A41_45EDL;

    /* A train's key: its arrival in ARRIVAL_BITS ticks of a month from bit ARRIVAL_SHIFT up, its route + 1 (0 for none) in ROUTE_BITS, its group in the low GROUP_BITS. */
    private static final int GROUP_BITS = 7, ROUTE_BITS = 20, ARRIVAL_BITS = 36, ARRIVAL_SHIFT = GROUP_BITS + ROUTE_BITS;
    /** The masks that take a key's group, route and arrival back out, once shifted down. */
    private static final long GROUP_MASK = (1L << GROUP_BITS) - 1, ROUTE_MASK = (1L << ROUTE_BITS) - 1, ARRIVAL_MASK = (1L << ARRIVAL_BITS) - 1;
    /** A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. */
    private static final double TICKS = (double) (1L << ARRIVAL_BITS);

    /** The kinds of train, one a kind of cargo (Ports.Cargo): its words, its payload in tonnes and the wagons it is drawn with. */
    public enum Kind {
        TANK(Ports.Cargo.LIQUID, "unit train of tank wagons", UNIT_TONNES, wagonsOf(UNIT_TONNES)),
        HOPPER(Ports.Cargo.DRY_BULK, "unit train of hoppers", UNIT_TONNES, wagonsOf(UNIT_TONNES)),
        INTERMODAL(Ports.Cargo.CONTAINER, "intermodal train", INTERMODAL_BOXES * Ports.TONNES_A_TEU,
                Math.min(MOST_WAGONS, (INTERMODAL_BOXES + BOXES_A_WAGON - 1) / BOXES_A_WAGON)),
        MIXED(Ports.Cargo.GENERAL, "mixed train", MIXED_TONNES, wagonsOf(MIXED_TONNES));

        private final Ports.Cargo cargo;
        private final String label;
        private final double tonnes;
        private final int wagons;

        Kind(Ports.Cargo cargo, String label, double tonnes, int wagons) {
            this.cargo = cargo; this.label = label; this.tonnes = tonnes; this.wagons = wagons;
        }

        public Ports.Cargo cargo() { return cargo; }
        public String label()      { return label; }
        /** A train's payload, in tonnes. */
        public double tonnes()     { return tonnes; }
        /** Its wagons, between its two locomotives. */
        public int wagons()        { return wagons; }
        /** Its length, both locomotives and its wagons, in plots. */
        public double length()     { return 2 * LOCO_PLOTS + wagons * CAR_PLOTS; }
        /** Whether it goes to the freight station: boxes and general cargo. */
        public boolean toStation() { return this == INTERMODAL || this == MIXED; }

        /** The kind that carries a kind of cargo. */
        public static Kind of(Ports.Cargo c) {
            for (Kind k : values()) if (k.cargo == c) return k;
            return null;
        }

        private static int wagonsOf(double tonnes) {
            return (int) Math.min(MOST_WAGONS, Math.ceil(tonnes / WAGON_TONNES));
        }
    }

    /**
     * A train's way: its yard (an index in CityRuns.yards()), whether that
     * yard is the freight station, its siding (0 to TileRaster.YARD_TRACKS
     * - 1, -1 with none), and the way as points in plots - the world line's
     * far end first, the siding's end last - with each point's distance
     * along it; the first fadeTo plots are the world line's, faded in.
     */
    public record Route(int yard, boolean station, int siding, double[] xs, double[] ys, double[] along, double fadeTo) {

        /** A route through points: their distances along it worked out. */
        public static Route through(int yard, boolean station, int siding, double[] xs, double[] ys, double fadeTo) {
            double[] along = new double[xs.length];
            for (int i = 1; i < xs.length; i++) along[i] = along[i - 1] + Math.hypot(xs[i] - xs[i - 1], ys[i] - ys[i - 1]);
            return new Route(yard, station, siding, xs.clone(), ys.clone(), along, Math.max(0, fadeTo));
        }

        /** Its length, the world line's far end to the siding's, in plots. */
        public double length() { return along[along.length - 1]; }
    }

    /** One train: its kind, whether it lands cargo (an import, or boxes, which go both ways) or comes to load it, its route among the schedule's (-1 with none), and when it reaches its siding's end, in month fractions. */
    public record Call(Kind kind, boolean inbound, int route, double arrives) { }

    /** A train at a moment: its call, how far along its route its front is, in plots, whether it is loaded, and whether it stands on its siding. */
    public record Train(Call call, double front, boolean loaded, boolean standing) { }

    /** One vehicle of a train at a moment: its middle in plots, its heading along the route toward the yard (radians, east 0, south a quarter turn), whether it is a locomotive, and how much of it shows (0 at the world line's far end, 1 from the gateway in). */
    public record Car(double x, double y, double heading, boolean loco, double alpha) { }

    /** One (kind, direction) of the month: its seed and its trains. */
    private record Group(Kind kind, boolean inbound, long seed, int calls) { }

    private final int month;
    private final List<Route> routes;
    private final List<Group> groups;
    private final int count, stands;
    private final boolean boxes;

    /* Built on the first ask: the trains' keys, sorted by arrival; and each route's box, {x0, y0, x1, y1} in plots. */
    private long[] keys;
    private double[][] boxesOf;
    private int touched;

    private TrainSchedule(int month, List<Route> routes, List<Group> groups, int stands, boolean boxes) {
        this.month = month;
        this.routes = routes;
        this.groups = groups;
        this.stands = stands;
        this.boxes = boxes;
        int n = 0;
        for (Group g : groups) n += g.calls();
        this.count = n;
    }

    /* =====================================================================
       THE MONTH'S TRAINS
       ===================================================================== */

    /** Whether the freight station is a container yard: the railway landed or shipped any boxes the month billed (the map's yard and these trains read the same). */
    public static boolean boxesByRail(ham.citybuildersim.sectors.Freight rail) {
        return rail != null && rail.railedIn(Ports.Cargo.CONTAINER) + rail.railedOut(Ports.Cargo.CONTAINER) > 0;
    }

    /** The month's schedule from the railway's month billed (sectors.Freight.railedIn() and railedOut()), on the map's routes; none with no railway. */
    public static TrainSchedule of(int month, ham.citybuildersim.sectors.Freight rail, List<Route> routes) {
        int n = Ports.Cargo.values().length;
        double[] in = new double[n], out = new double[n];
        if (rail != null) {
            for (Ports.Cargo c : Ports.Cargo.values()) {
                in[c.ordinal()] = rail.railedIn(c);
                out[c.ordinal()] = rail.railedOut(c);
            }
        }
        return of(month, in, out, routes);
    }

    /**
     * ...from tonnes by kind of cargo (Ports.Cargo's order), landed and
     * shipped: each kind's trains counted at its payload - times stands(),
     * past MOST_A_MONTH trains in all - each to go on a route its kind goes
     * to, by a hash, when its key is built.
     */
    public static TrainSchedule of(int month, double[] in, double[] out, List<Route> routes) {
        routes = routes == null ? new ArrayList<>() : new ArrayList<>(routes);
        double exact = 0;
        for (Kind k : Kind.values()) {
            double i = tonnes(in, k), o = tonnes(out, k);
            exact += (k == Kind.INTERMODAL ? Math.max(i, o) : i + o) / k.tonnes();
        }
        int stands = exact > MOST_A_MONTH ? (int) Math.ceil(exact / MOST_A_MONTH) : 1;
        List<Group> groups = new ArrayList<>();
        for (Kind k : Kind.values()) {
            double i = tonnes(in, k), o = tonnes(out, k);
            if (k == Kind.INTERMODAL) {
                add(groups, month, k, true, Math.max(i, o), stands);
            } else {
                add(groups, month, k, true, i, stands);
                add(groups, month, k, false, o, stands);
            }
        }
        boolean boxes = tonnes(in, Kind.INTERMODAL) + tonnes(out, Kind.INTERMODAL) > 0;
        return new TrainSchedule(month, routes, groups, stands, boxes);
    }

    private static double tonnes(double[] t, Kind k) {
        int i = k.cargo().ordinal();
        return t == null || i >= t.length || !(t[i] > 0) ? 0 : t[i];
    }

    private static void add(List<Group> groups, int month, Kind k, boolean inbound, double tonnes, int stands) {
        long seed = seed(month, k, inbound);
        int n = BoatSchedule.callsFor(tonnes, k.tonnes() * stands, seed);
        if (n > 0) groups.add(new Group(k, inbound, seed, n));
    }

    /** The seed of one (kind, direction) in one month. */
    static long seed(int month, Kind k, boolean inbound) {
        return ((long) month << 32) ^ ((long) k.ordinal() << 16) ^ ((long) (inbound ? 1 : 0) << 12) ^ SALT;
    }

    /** When a group's k-th train reaches its siding's end, in ticks of a month: hashed through the month, its whole visit inside it. */
    static long arrivalTicks(long seed, int k) {
        double leg = LEG_SECONDS / MONTH_SECONDS, dwell = DWELL_SECONDS / MONTH_SECONDS;
        double a = leg + World.unit(World.mix(World.mix(seed) ^ ((long) k * 0x9E37_79B9L) ^ 0x7A1L)) * (1 - 2 * leg - dwell);
        return (long) Math.floor(a * TICKS);
    }

    /**
     * The routes a kind goes on: boxes and general cargo the freight
     * station's; bulk the other yards' when the station is a container yard,
     * every yard's when it is not; every route when none is its kind's.
     */
    private int[] routesFor(Kind k) {
        int n = Math.min(routes.size(), (int) ROUTE_MASK - 1);
        List<Integer> mine = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Route r = routes.get(i);
            if (k.toStation() ? r.station() : !(boxes && r.station())) mine.add(i);
        }
        if (mine.isEmpty()) for (int i = 0; i < n; i++) mine.add(i);
        return mine.stream().mapToInt(Integer::intValue).toArray();
    }

    /** The trains' keys, sorted by arrival, and the routes' boxes; built on the first ask. */
    private void build() {
        if (keys != null) return;
        int[][] mine = new int[Kind.values().length][];
        for (Kind k : Kind.values()) mine[k.ordinal()] = routesFor(k);
        keys = new long[count];
        int at = 0;
        for (int gi = 0; gi < groups.size(); gi++) {
            Group g = groups.get(gi);
            int[] rs = mine[g.kind().ordinal()];
            for (int k = 0; k < g.calls(); k++) {
                long route = rs.length == 0 ? 0
                        : 1 + rs[(int) Long.remainderUnsigned(World.mix(g.seed() ^ ((long) k << 24) ^ 0x7A15L), rs.length)];
                keys[at++] = (arrivalTicks(g.seed(), k) << ARRIVAL_SHIFT) | (route << GROUP_BITS) | gi;
            }
        }
        java.util.Arrays.sort(keys);
        boxesOf = new double[routes.size()][];
        for (int i = 0; i < routes.size(); i++) boxesOf[i] = box(routes.get(i));
    }

    /** A call out of its key. */
    private Call call(long key) {
        Group g = groups.get((int) (key & GROUP_MASK));
        int route = (int) ((key >>> GROUP_BITS) & ROUTE_MASK) - 1;
        double arrives = ((key >>> ARRIVAL_SHIFT) & ARRIVAL_MASK) / TICKS;
        return new Call(g.kind(), g.inbound(), route, arrives);
    }

    /* =====================================================================
       WHERE A TRAIN IS
       ===================================================================== */

    /**
     * Where a call's train is at t (month fractions), or null when it is not
     * on the line: running in over its leg, standing on its siding, or
     * running back out. Pure in (call, t) and the route.
     */
    public static Train position(Call call, Route route, double t) {
        if (call == null || route == null) return null;
        double leg = LEG_SECONDS / MONTH_SECONDS, dwell = DWELL_SECONDS / MONTH_SECONDS, a = call.arrives(), len = route.length();
        boolean both = call.kind() == Kind.INTERMODAL;
        if (t >= a - leg && t < a) return new Train(call, len * (1 - (a - t) / leg), both || call.inbound(), false);
        if (t >= a && t <= a + dwell) return new Train(call, len, both || call.inbound(), true);
        if (t > a + dwell && t <= a + dwell + leg) return new Train(call, len * (1 - (t - a - dwell) / leg), both || !call.inbound(), false);
        return null;
    }

    /**
     * A train's vehicles, front first: a locomotive, its wagons, a
     * locomotive, each a whole length behind the one before along the
     * route - those still past the world line's far end left out.
     */
    public static List<Car> cars(Train train, Route route) {
        List<Car> out = new ArrayList<>();
        if (train == null || route == null) return out;
        Kind k = train.call().kind();
        int n = k.wagons() + 2;
        double d = train.front();
        double[] xs = route.xs(), ys = route.ys(), al = route.along();
        int last = xs.length - 1, s = last - 1;
        for (int i = 0; i < n; i++) {
            boolean loco = i == 0 || i == n - 1;
            double len = loco ? LOCO_PLOTS : CAR_PLOTS, mid = d - len / 2;
            d -= len;
            if (mid < 0) break;
            if (last < 1) break;
            // Walking back from the front, the stretch a vehicle is on only moves toward the route's start.
            while (s > 0 && al[s] > mid) s--;
            double seg = al[s + 1] - al[s], u = seg > 0 ? Math.min(1, (mid - al[s]) / seg) : 0;
            double x = xs[s] + (xs[s + 1] - xs[s]) * u, y = ys[s] + (ys[s + 1] - ys[s]) * u;
            double heading = Math.atan2(ys[s + 1] - ys[s], xs[s + 1] - xs[s]);
            double alpha = route.fadeTo() > 0 ? Math.min(1, mid / route.fadeTo()) : 1;
            out.add(new Car(x, y, heading, loco, alpha));
        }
        return out;
    }

    /**
     * The trains on the line at t with a vehicle in the rectangle [x0, x1) x
     * [y0, y1) in plots, widened by a vehicle's length: the month's arrivals
     * searched for those whose visit spans t, and only those looked at
     * (touched()).
     */
    public List<Train> frame(long x0, long y0, long x1, long y1, double t) {
        build();
        touched = 0;
        List<Train> out = new ArrayList<>();
        double leg = LEG_SECONDS / MONTH_SECONDS, dwell = DWELL_SECONDS / MONTH_SECONDS, w = LOCO_PLOTS;
        long low = (long) Math.max(0, Math.floor((t - dwell - leg) * TICKS)) << ARRIVAL_SHIFT;
        for (int i = BoatSchedule.lower(keys, 0, keys.length, low); i < keys.length; i++) {
            Call c = call(keys[i]);
            if (c.arrives() > t + leg) break;
            if (c.route() < 0) continue;
            Route r = routes.get(c.route());
            double[] b = boxesOf[c.route()];
            if (b[2] + w < x0 || b[0] - w >= x1 || b[3] + w < y0 || b[1] - w >= y1) continue;
            touched++;
            Train tr = position(c, r, t);
            if (tr == null) continue;
            for (Car car : cars(tr, r)) {
                if (car.x() + w >= x0 && car.x() - w < x1 && car.y() + w >= y0 && car.y() - w < y1) {
                    out.add(tr);
                    break;
                }
            }
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

    /* ----- reads ----- */

    public int month()            { return month; }
    public List<Route> routes()   { return java.util.Collections.unmodifiableList(routes); }

    /** Every train of the month, by arrival. Built on the first ask. */
    public List<Call> calls() {
        build();
        List<Call> out = new ArrayList<>(keys.length);
        for (long k : keys) out.add(call(k));
        return out;
    }

    /** The month's trains drawn, all kinds: counted when the schedule is made, nothing built. */
    public int callCount()        { return count; }

    /** How many trains each one drawn stands for: 1 up to MOST_A_MONTH a month. */
    public int stands()           { return stands; }

    /** Whether the freight station is a container yard this month (boxesByRail()'s rule, from the tonnes given). */
    public boolean boxes()        { return boxes; }

    /** The trains the last frame() looked at. */
    public int touched()          { return touched; }

    /** The trains drawn of one kind. */
    public int callsOf(Kind k) {
        int n = 0;
        for (Group g : groups) if (g.kind() == k) n += g.calls();
        return n;
    }

    @Override public String toString() { return "TrainSchedule[m" + month + ", " + count + " trains x " + stands + ", " + routes.size() + " routes]"; }
}
