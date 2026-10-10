package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The month's ships, as a pure function of time: which calls the city's sea
 * trade makes, when each arrives, and where each boat is at any moment
 * (0.7.86, batch O9; runs/spec-oil.md 2.10, the research's 4.3-4.4 and Q8;
 * scratch-oil's BoatProto, ported). Model only: since 0.7.97 (batch O13)
 * ui/MapView draws it on the game's clock, on the map's sea routes.
 *
 * WHY. A port that is a share of the freight band is invisible; the
 * research's answer is boats that follow the trade exactly - a city with no
 * seaborne trade has none - and cost nothing off screen. So A CALL IS A
 * SCHEDULE ENTRY, NOT AN OBJECT: a kind, a class, a direction, a route and an
 * arrival time hashed from (kind, direction, class, month, call number), and
 * a boat's place is route(call, t). Nothing is simulated, stored or saved;
 * panning away and back shows the same boat in the same place.
 *
 * CALLS. Tonnes ÷ the class's cargo, by kind and direction (Ports' month):
 * crude in the class its tank room allowed, the other liquids in MR product
 * tankers, dry bulk in a Capesize when the month fills one and a Panamax
 * when not, boxes in feeders - one call both ways, so the larger direction
 * counts [4.3 rule 3] - and general cargo in 5,500 t ships. A fraction of a
 * call is a whole one in the share of months its fraction says, by a hash of
 * the month: whole each month, exact on average.
 *
 * TIME (Q8). A month is MONTH_SECONDS of boats. A call runs in from its lane's
 * end to its quay over LEG_SECONDS, lies at the quay BERTH_SECONDS (about 24
 * hours [P33]) and runs out again; its arrival is spread through the month by
 * its hash, inside the month, so a boat never appears or vanishes at the
 * month's turn.
 *
 * ROUTES. Each berth's quay runs straight out to sea, away from the founding
 * site, for LANE_PLOTS (lane(): O9's, still each berth's with no sea routes
 * found). Since 0.7.97 (batch O13; spec-roads-and-ports.md 4.1) the map's
 * berths sail SeaRoutes' routes: from the quay's end along the way found on
 * the sea's grid to the offing and on into the abyss, where a boat fades
 * (Boat.alpha()); each call's last leg turned a few degrees about its start
 * by the call's own hash (Route.spread()), so the boats do not queue on one
 * line. A leg is the whole route, quay to the abyss's end, whatever its
 * length. A frame asks which routes cross the screen (an index by district
 * of every leg) and, on each, which calls are on the water now (a binary
 * search of the route's sorted arrivals).
 *
 * CHEAP TO COUNT, BUILT ONLY WHEN ASKED. The month's calls are counted by
 * kind, direction and class when the schedule is made (callCount()); each
 * call's route and arrival are worked out the first time a frame or the
 * list asks, as one sorted long a call - its route, its arrival to 2^-36 of
 * a month, and its group - so a city of billions is a sorted array, not
 * millions of objects (spec-oil 6's scale).
 */
public final class BoatSchedule {

    /** Seconds of boats a game month: 60 (the research's Q8: about one boat every 4 s at district zoom in the big city). */
    public static final double MONTH_SECONDS = 60;

    /** Seconds a boat takes to run its lane, in or out: 4 (spec-oil 2.10, the UI's). */
    public static final double LEG_SECONDS = 4;

    /** Seconds a boat lies at the quay: about 24 hours of a 730.5-hour month [P33], about 2 s. */
    public static final double BERTH_SECONDS = 24 / 730.5 * MONTH_SECONDS;

    /** A lane's length, quay to the map's sea, in plots: 1,536 (six districts, spec-oil 2.10). */
    public static final long LANE_PLOTS = 1536;

    /** The salt the calls' hashes start from. */
    private static final long SALT = 0xB0A7_5417L;

    /* A call's key: its route + 1 (0 for none) in the top ROUTE_BITS, its arrival in ARRIVAL_BITS ticks of a month, its group in the low GROUP_BITS. */
    private static final int GROUP_BITS = 7, ARRIVAL_BITS = 36, ROUTE_SHIFT = GROUP_BITS + ARRIVAL_BITS;
    private static final long GROUP_MASK = (1L << GROUP_BITS) - 1, ARRIVAL_MASK = (1L << ARRIVAL_BITS) - 1;
    private static final double TICKS = (double) (1L << ARRIVAL_BITS);

    /** One terminal's quay: its kind and where on the map it stands, in plots. */
    public record Berth(Ports.Cargo cargo, long x, long y) { }

    /**
     * A berth's route: its quay, its far end (x1, y1) out at sea, and the
     * way between as points in plots - the berth first, its turns, the
     * offing (point `offing`), the abyss's end last - with each point's
     * distance along it (`along`); the point its last leg turns about for a
     * call's spread (`pivot`), and how far that leg may turn either way, in
     * radians (`spread`). A lane (O9's) is two points, no abyss, no spread.
     */
    public record Route(Berth berth, long x1, long y1, double[] xs, double[] ys, double[] along, int offing, int pivot, double spread) {

        /** A straight lane from its quay to (x1, y1): O9's. */
        public Route(Berth berth, long x1, long y1) {
            this(berth, x1, y1, new double[] { berth.x(), x1 }, new double[] { berth.y(), y1 },
                    new double[] { 0, Math.hypot(x1 - berth.x(), y1 - berth.y()) }, 1, 0, 0);
        }

        /** A route through points (SeaRoutes'): the offing's index, the pivot's, the spread. */
        public static Route through(Berth berth, double[] xs, double[] ys, int offing, int pivot, double spread) {
            double[] along = new double[xs.length];
            for (int i = 1; i < xs.length; i++) along[i] = along[i - 1] + Math.hypot(xs[i] - xs[i - 1], ys[i] - ys[i - 1]);
            int n = xs.length - 1;
            return new Route(berth, (long) Math.floor(xs[n]), (long) Math.floor(ys[n]), xs.clone(), ys.clone(), along,
                    Math.max(1, Math.min(n, offing)), Math.max(0, Math.min(n - 1, pivot)), Math.max(0, spread));
        }

        /** Its length, quay to far end, in plots. */
        public double length() { return along[along.length - 1]; }

        /** Where the abyss begins, in plots along it: at the offing; a lane's whole length. */
        public double fadeFrom() { return along[offing]; }
    }

    /**
     * One call: a kind and its class, whether it lands cargo (an import, or a
     * box ship, which carries both ways) or comes to load it, its route among
     * the schedule's (-1 with no berth of its kind to draw it at), and when it
     * reaches the quay, in month fractions.
     */
    public record Call(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, int route, double arrives) { }

    /**
     * A boat at a moment: its call, where it is in plots, whether it is
     * loaded, whether it lies at the quay, its heading (radians, east 0,
     * south a quarter turn: the way it is going; at the quay, out along its
     * route), and how much of it shows (1, fading to 0 in the abyss).
     */
    public record Boat(Call call, double x, double y, boolean loaded, boolean docked, double heading, double alpha) { }

    /** One (kind, class, direction) of the month: its seed and its calls. */
    private record Group(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, long seed, int calls) { }

    private final int month;
    private final List<Route> routes;
    private final List<Group> groups;
    private final int count;

    /* Built on the first ask: the calls' keys, sorted; where each route's begin (route + 1, so routeStart[0] is the calls with none); the index of routes by district cell. */
    private long[] keys;
    private int[] routeStart;
    private java.util.Map<Long, int[]> index;
    private int touched;

    /* Built with them: each route's legs' boxes (legBoxes()); and the frame each route was last looked at in, so a frame looks at a route once without a set. */
    private double[][][] boxes;
    private int[] seenAt;
    private int frames;

    private BoatSchedule(int month, List<Route> routes, List<Group> groups) {
        this.month = month;
        this.routes = routes;
        this.groups = groups;
        int n = 0;
        for (Group g : groups) n += g.calls();
        this.count = n;
    }

    private static long key(long cx, long cy) { return (cx << 32) ^ (cy & 0xFFFF_FFFFL); }

    /* =====================================================================
       THE MONTH'S CALLS
       ===================================================================== */

    /** The calls a month's tonnes make in a ship of this cargo: the whole calls, and one more in the share of months the fraction says. */
    public static int callsFor(double tonnes, double cargo, long seed) {
        if (!(tonnes > 0) || !(cargo > 0)) return 0;
        double whole = Math.floor(tonnes / cargo);
        double frac = tonnes / cargo - whole;
        return (int) whole + (World.unit(World.mix(seed)) < frac ? 1 : 0);
    }

    /** The seed of one (kind, direction, class) in one month. */
    static long seed(int month, Ports.Cargo cargo, boolean inbound, Ports.Ship ship) {
        return ((long) month << 32) ^ ((long) cargo.ordinal() << 16) ^ ((long) (inbound ? 1 : 0) << 12) ^ ((long) ship.ordinal() << 4) ^ SALT;
    }

    /** When a group's k-th call reaches its quay, in ticks of a month: hashed through the month, its whole visit inside it. */
    static long arrivalTicks(long seed, int k) {
        double leg = LEG_SECONDS / MONTH_SECONDS, berth = BERTH_SECONDS / MONTH_SECONDS;
        double a = leg + World.unit(World.mix(World.mix(seed) ^ ((long) k * 0x9E37_79B9L) ^ 0xA77L)) * (1 - 2 * leg - berth);
        return (long) Math.floor(a * TICKS);
    }

    /**
     * The month's schedule from the ports' month billed: its calls, counted
     * by kind, direction and class, each to go on a route of its kind's
     * berths by a hash (none with no berth of its kind), and the routes, each
     * quay's lane run straight out away from the founding site.
     */
    public static BoatSchedule of(int month, Ports ports, List<Berth> berths, long foundX, long foundY) {
        List<Route> routes = new ArrayList<>();
        if (berths != null) for (Berth b : berths) routes.add(lane(b, foundX, foundY));
        return of(month, ports, routes);
    }

    /** ...on routes found already (0.7.97: the map's, SeaRoutes'), one a berth. */
    public static BoatSchedule of(int month, Ports ports, List<Route> routes) {
        routes = routes == null ? new ArrayList<>() : new ArrayList<>(routes);
        List<Group> groups = new ArrayList<>();
        if (ports != null) {
            for (Ports.Cargo c : Ports.Cargo.values()) {
                double in = ports.seaIn(c), out = ports.seaOut(c);
                switch (c) {
                    case LIQUID: {
                        Ports.Ship crude = ports.billedCrudeShip() != null ? ports.billedCrudeShip() : Ports.Ship.MR;
                        add(groups, month, c, true, crude, ports.crudeSeaIn());
                        add(groups, month, c, false, crude, ports.crudeSeaOut());
                        add(groups, month, c, true, Ports.Ship.MR, Math.max(0, in - ports.crudeSeaIn()));
                        add(groups, month, c, false, Ports.Ship.MR, Math.max(0, out - ports.crudeSeaOut()));
                        break;
                    }
                    case DRY_BULK:
                        add(groups, month, c, true, Ports.bulkShipFor(in), in);
                        add(groups, month, c, false, Ports.bulkShipFor(out), out);
                        break;
                    case CONTAINER:
                        // One call carries both ways: the larger direction counts, and the ship is loaded both legs.
                        add(groups, month, c, true, Ports.Ship.FEEDER, Math.max(in, out));
                        break;
                    default:
                        add(groups, month, c, true, Ports.Ship.GENERAL_CARGO, in);
                        add(groups, month, c, false, Ports.Ship.GENERAL_CARGO, out);
                }
            }
        }
        return new BoatSchedule(month, routes, groups);
    }

    private static void add(List<Group> groups, int month, Ports.Cargo c, boolean inbound, Ports.Ship ship, double tonnes) {
        long seed = seed(month, c, inbound, ship);
        int n = callsFor(tonnes, ship.tonnes(), seed);
        if (n > 0) {
            // A group the same (kind, class, direction) as one before it - crude in MRs beside the products - is merged into it.
            for (int i = 0; i < groups.size(); i++) {
                Group g = groups.get(i);
                if (g.cargo() == c && g.ship() == ship && g.inbound() == inbound) {
                    groups.set(i, new Group(c, ship, inbound, g.seed(), g.calls() + n));
                    return;
                }
            }
            groups.add(new Group(c, ship, inbound, seed, n));
        }
    }

    /** A quay's lane: straight out to sea, away from the founding site, for LANE_PLOTS (north when the quay is the site itself). */
    public static Route lane(Berth b, long foundX, long foundY) {
        double dx = b.x() - foundX, dy = b.y() - foundY, len = Math.hypot(dx, dy);
        if (!(len > 0)) { dx = 0; dy = -1; len = 1; }
        return new Route(b, b.x() + Math.round(dx / len * LANE_PLOTS), b.y() + Math.round(dy / len * LANE_PLOTS));
    }

    /** The calls' keys, sorted by route, then arrival; built on the first ask. */
    private void build() {
        if (keys != null) return;
        int[][] mine = new int[Ports.Cargo.values().length][];
        for (Ports.Cargo c : Ports.Cargo.values()) {
            mine[c.ordinal()] = java.util.stream.IntStream.range(0, routes.size()).filter(i -> routes.get(i).berth().cargo() == c).toArray();
        }
        keys = new long[count];
        int at = 0;
        for (int gi = 0; gi < groups.size(); gi++) {
            Group g = groups.get(gi);
            int[] rs = mine[g.cargo().ordinal()];
            for (int k = 0; k < g.calls(); k++) {
                long route = rs.length == 0 ? 0
                        : 1 + rs[(int) Long.remainderUnsigned(World.mix(g.seed() ^ ((long) k << 24) ^ 0x5417L), rs.length)];
                keys[at++] = (route << ROUTE_SHIFT) | (arrivalTicks(g.seed(), k) << GROUP_BITS) | gi;
            }
        }
        java.util.Arrays.sort(keys);
        routeStart = new int[routes.size() + 2];
        int r = 0;
        for (int i = 0; i < keys.length; i++) {
            int route = (int) (keys[i] >>> ROUTE_SHIFT);
            while (r <= route) routeStart[r++] = i;
        }
        while (r < routeStart.length) routeStart[r++] = keys.length;
        java.util.Map<Long, List<Integer>> cells = new java.util.HashMap<>();
        boxes = new double[routes.size()][][];
        seenAt = new int[routes.size()];
        for (int i = 0; i < routes.size(); i++) {
            java.util.Set<Long> its = new java.util.HashSet<>();
            boxes[i] = legBoxes(routes.get(i));
            for (double[] box : boxes[i]) {
                long cx0 = Math.floorDiv((long) Math.floor(box[0]), World.DISTRICT), cx1 = Math.floorDiv((long) Math.floor(box[2]), World.DISTRICT);
                long cy0 = Math.floorDiv((long) Math.floor(box[1]), World.DISTRICT), cy1 = Math.floorDiv((long) Math.floor(box[3]), World.DISTRICT);
                for (long cx = cx0; cx <= cx1; cx++) for (long cy = cy0; cy <= cy1; cy++) if (its.add(key(cx, cy))) cells.computeIfAbsent(key(cx, cy), x -> new ArrayList<>()).add(i);
            }
        }
        index = new java.util.HashMap<>();
        for (java.util.Map.Entry<Long, List<Integer>> e : cells.entrySet()) index.put(e.getKey(), e.getValue().stream().mapToInt(Integer::intValue).toArray());
    }

    /** A call out of its key. */
    private Call call(long key) {
        Group g = groups.get((int) (key & GROUP_MASK));
        int route = (int) (key >>> ROUTE_SHIFT) - 1;
        double arrives = ((key >>> GROUP_BITS) & ARRIVAL_MASK) / TICKS;
        return new Call(g.cargo(), g.ship(), g.inbound(), route, arrives);
    }

    /* =====================================================================
       WHERE A BOAT IS
       ===================================================================== */

    /**
     * Where a call's boat is at t (month fractions), or null when it is not
     * on the water: running in over its leg, at the quay, or running out.
     * Pure in (call, t) and the route.
     */
    public static Boat position(Call call, Route route, double t) {
        if (call == null || route == null) return null;
        double leg = LEG_SECONDS / MONTH_SECONDS, berth = BERTH_SECONDS / MONTH_SECONDS, a = call.arrives();
        boolean boxes = call.cargo() == Ports.Cargo.CONTAINER;
        if (t >= a - leg && t < a) return along(call, route, (a - t) / leg, true, boxes || call.inbound(), false);
        if (t >= a && t <= a + berth) return along(call, route, 0, false, boxes || call.inbound(), true);
        if (t > a + berth && t <= a + berth + leg) return along(call, route, (t - a - berth) / leg, false, boxes || !call.inbound(), false);
        return null;
    }

    /**
     * A call's boat at a share f of its route from the quay (0 at the quay,
     * lying there; 1 at the far end), coming in or going out: its place on
     * the way, turned about the pivot by the call's spread past it, its
     * heading, and how much of it shows past the offing.
     */
    static Boat along(Call call, Route route, double f, boolean coming, boolean loaded, boolean docked) {
        double[] xs = route.xs(), ys = route.ys(), al = route.along();
        int n = xs.length - 1;
        double d = f * al[n];
        int s = 0;
        while (s < n - 1 && al[s + 1] < d) s++;
        double seg = al[s + 1] - al[s], u = seg > 0 ? (d - al[s]) / seg : 0;
        double x = xs[s] + (xs[s + 1] - xs[s]) * u, y = ys[s] + (ys[s + 1] - ys[s]) * u;
        double heading = Math.atan2(ys[s + 1] - ys[s], xs[s + 1] - xs[s]);
        if (route.spread() > 0 && s >= route.pivot()) {
            double turn = turnOf(call, route), c = Math.cos(turn), sn = Math.sin(turn);
            double px = xs[route.pivot()], py = ys[route.pivot()], dx = x - px, dy = y - py;
            x = px + dx * c - dy * sn;
            y = py + dx * sn + dy * c;
            heading += turn;
        }
        if (coming) heading += Math.PI;
        double fade = route.fadeFrom(), alpha = d <= fade || !(al[n] > fade) ? 1 : Math.max(0, 1 - (d - fade) / (al[n] - fade));
        return new Boat(call, x, y, loaded, docked, heading, alpha);
    }

    /** A call's turn of its route's last leg, in radians: within its route's spread either way, by the call's own hash (its arrival and its route). */
    static double turnOf(Call call, Route route) {
        long h = World.mix(Double.doubleToLongBits(call.arrives()) ^ ((long) call.route() << 40) ^ 0x5B4EADL);
        return route.spread() * (2 * World.unit(h) - 1);
    }

    /**
     * The boats on the water at t whose routes cross the rectangle [x0, x1)
     * x [y0, y1) in plots: the routes indexed by the district cells the
     * rectangle covers, and on each a binary search of its arrivals. Only
     * those routes are touched (touched()).
     */
    public List<Boat> frame(long x0, long y0, long x1, long y1, double t) {
        build();
        touched = 0;
        List<Boat> out = new ArrayList<>();
        int stamp = ++frames;
        double leg = LEG_SECONDS / MONTH_SECONDS, berth = BERTH_SECONDS / MONTH_SECONDS;
        long cx0 = Math.floorDiv(x0, World.DISTRICT), cx1 = Math.floorDiv(Math.max(x0, x1 - 1), World.DISTRICT);
        long cy0 = Math.floorDiv(y0, World.DISTRICT), cy1 = Math.floorDiv(Math.max(y0, y1 - 1), World.DISTRICT);
        for (long cx = cx0; cx <= cx1; cx++) {
            for (long cy = cy0; cy <= cy1; cy++) {
                int[] rs = index.get(key(cx, cy));
                if (rs == null) continue;
                for (int r : rs) {
                    if (seenAt[r] == stamp) continue;
                    seenAt[r] = stamp;
                    if (!crosses(boxes[r], x0, y0, x1, y1)) continue;
                    Route rt = routes.get(r);
                    touched++;
                    int from = routeStart[r + 1], to = routeStart[r + 2];
                    long low = ((long) (r + 1) << ROUTE_SHIFT) | ((long) Math.max(0, Math.floor((t - berth - leg) * TICKS)) << GROUP_BITS);
                    for (int i = lower(keys, from, to, low); i < to; i++) {
                        Call c = call(keys[i]);
                        if (c.arrives() > t + leg) break;
                        Boat b = position(c, rt, t);
                        if (b != null) out.add(b);
                    }
                }
            }
        }
        return out;
    }

    /** Whether a route meets the rectangle: one of its legs' boxes does (legBoxes()) - a lane's, its one box. */
    public static boolean crosses(Route rt, long x0, long y0, long x1, long y1) {
        return crosses(legBoxes(rt), x0, y0, x1, y1);
    }

    /** ...by its legs' boxes, worked out already. */
    static boolean crosses(double[][] legs, long x0, long y0, long x1, long y1) {
        for (double[] b : legs) if (b[2] >= x0 && b[0] < x1 && b[3] >= y0 && b[1] < y1) return true;
        return false;
    }

    /**
     * Each leg's box, {x0, y0, x1, y1} in plots, inclusive: its two points'
     * - past the pivot widened by how far the call's spread can swing it
     * (its far end's distance from the pivot x sin(spread), and a plot).
     */
    static double[][] legBoxes(Route rt) {
        double[] xs = rt.xs(), ys = rt.ys();
        double[][] out = new double[xs.length - 1][];
        for (int i = 0; i + 1 < xs.length; i++) {
            double w = 0;
            if (rt.spread() > 0 && i >= rt.pivot()) {
                double far = Math.hypot(xs[i + 1] - xs[rt.pivot()], ys[i + 1] - ys[rt.pivot()]);
                w = far * Math.sin(rt.spread()) + 1;
            }
            out[i] = new double[] { Math.min(xs[i], xs[i + 1]) - w, Math.min(ys[i], ys[i + 1]) - w, Math.max(xs[i], xs[i + 1]) + w, Math.max(ys[i], ys[i + 1]) + w };
        }
        return out;
    }

    /** The first index in [from, to) whose key is at least v. */
    static int lower(long[] a, int from, int to, long v) {
        int lo = from, hi = to;
        while (lo < hi) { int m = (lo + hi) >>> 1; if (a[m] < v) lo = m + 1; else hi = m; }
        return lo;
    }

    /* ----- reads ----- */

    public int month()            { return month; }
    public List<Route> routes()   { return java.util.Collections.unmodifiableList(routes); }

    /** Every call of the month, by route and arrival (those with no route first). Built on the first ask. */
    public List<Call> calls() {
        build();
        List<Call> out = new ArrayList<>(keys.length);
        for (long k : keys) out.add(call(k));
        return out;
    }

    /** The month's calls, all kinds: counted when the schedule is made, nothing built. */
    public int callCount()        { return count; }

    /** The routes the last frame() looked at. */
    public int touched()          { return touched; }

    /** The calls of one kind. */
    public int callsOf(Ports.Cargo c) {
        int n = 0;
        for (Group g : groups) if (g.cargo() == c) n += g.calls();
        return n;
    }

    @Override public String toString() { return "BoatSchedule[m" + month + ", " + count + " calls, " + routes.size() + " routes]"; }
}
