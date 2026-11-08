package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The month's ships, as a pure function of time: which calls the city's sea
 * trade makes, when each arrives, and where each boat is at any moment
 * (0.7.86, batch O9; docs/studies/spec-oil.md 2.10, the research's 4.3-4.4 and
 * Q8; scratch-oil's BoatProto, ported). Model only: since 0.7.97 (batch O13)
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
 * by the call's own hash (turnOf()), so the boats do not queue on one
 * line. A leg is the whole route, quay to the abyss's end, whatever its
 * length. A frame asks which routes cross the screen (an index by district
 * of every leg) and, on each, which calls are on the water now (a binary
 * search of the route's sorted arrivals).
 *
 * PARTNERS AND EXITS (WN7; docs/studies/spec-nations.md 9). A call may carry
 * the nation it is bound for or from (Call.partner), drawn by CallPartners
 * from the month's tonnes of its kind of cargo by a hash of its key and
 * PARTNER_SALT - never the arrival's or the route's, so a month's calls are
 * the same with partners as without - and, on a route that says its exits
 * (SeaRoutes: an offing per bearing), the exit nearest that nation's compass
 * bearing (Call.exit). A boat with an exit rides the route's points to the
 * offing unturned and leaves it on the exit's bearing, its last leg turned by
 * turnOf() about the offing, fading to the exit's end; one with none is the
 * route's own, byte for byte. The partner is drawn only for a call whose boat
 * is on the water, so a frame stays as cheap.
 *
 * FORKS (WN7, slice U). The lanes the map strokes fork at the offing into the
 * exits the month's big partners take: forksOf() gives a route the exits
 * exitFor() finds for the nations with tonnes of its kind of cargo
 * (CallPartners.seaPartners()) that carry at least FORK_SHARE of them between
 * them, the MAX_FORKS largest at most, and forks() the legs of them on a
 * rectangle, each offing's once whatever routes share it, indexed by district
 * cell, so a frame touches only the offings in view, no route and no call.
 * Pure in the month's partners; with none, nothing.
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

    /** The salt a call's partner is drawn with (WN7): a hash of the call's key and this, never the arrival's or the route's, so a month's calls are the same with partners as without. */
    private static final long PARTNER_SALT = 0x9A47_4E25L;

    /* A call's key: its route + 1 (0 for none) from bit ROUTE_SHIFT up, its arrival in ARRIVAL_BITS ticks of a month, its group in the low GROUP_BITS. */
    private static final int GROUP_BITS = 7, ARRIVAL_BITS = 36, ROUTE_SHIFT = GROUP_BITS + ARRIVAL_BITS;
    /** The masks that take a key's group and its arrival back out, once shifted down. */
    private static final long GROUP_MASK = (1L << GROUP_BITS) - 1, ARRIVAL_MASK = (1L << ARRIVAL_BITS) - 1;
    /** A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. */
    private static final double TICKS = (double) (1L << ARRIVAL_BITS);

    /**
     * One terminal's quay: its kind, where on the map it stands in plots (the plot just past its quay's end), and (HB1) the side its
     * sea is on, CityShore.Work.side(): 0 north to 3 west, or -1 when it is not known (a berth made by hand, a fixture's).
     */
    public record Berth(Ports.Cargo cargo, long x, long y, int side) {

        /** A berth whose side is not known: its boats lie at its point as they did before HB1 (alongside() leaves them). */
        public Berth(Ports.Cargo cargo, long x, long y) {
            this(cargo, x, y, -1);
        }
    }

    /**
     * A berth's route: its quay, its far end (x1, y1) out at sea, and the
     * way between as points in plots - the berth first, its turns, the
     * offing (point `offing`), the abyss's end last - with each point's
     * distance along it (`along`); the point its last leg turns about for a
     * call's spread (`pivot`), and how far that leg may turn either way, in
     * radians (`spread`). A lane (O9's) is two points, no abyss, no spread.
     * Since WN7 a route found on the sea may say its EXITS: for each compass
     * bearing every SeaRoutes.BEARING_STEP degrees from north, the run of open
     * sea from the offing on it, in plots, 0 where it is closed (`exits`; null
     * for a lane, a fixture's route, and any made without them). A call with a
     * partner leaves the offing on the exit nearest its partner's bearing
     * instead of going on into the abyss. Never written once made, and shared
     * by the routes that share an offing.
     */
    public record Route(Berth berth, long x1, long y1, double[] xs, double[] ys, double[] along, int offing, int pivot, double spread, double[] exits) {

        /** A route through points with no exits: the abyss alone, as every route was before WN7. */
        public Route(Berth berth, long x1, long y1, double[] xs, double[] ys, double[] along, int offing, int pivot, double spread) {
            this(berth, x1, y1, xs, ys, along, offing, pivot, spread, null);
        }

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

        /** The same route saying its exits (SeaRoutes.EXITS runs, in plots; the array is kept, not copied), or none with null. */
        public Route withExits(double[] exits) {
            return new Route(berth, x1, y1, xs, ys, along, offing, pivot, spread, exits);
        }

        /** The run of open sea from the offing on a compass bearing (the nearest of its exits), in plots; 0 where it is closed or the route has none. */
        public double exitRun(int bearing) {
            if (exits == null || exits.length != SeaRoutes.EXITS || bearing < 0) return 0;
            return exits[exitIndex(bearing)];
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
     * reaches the quay, in month fractions. Since WN7 it may say its partner,
     * the nation (1-based, 0 for none) it is bound for or from, drawn by
     * CallPartners from the month's trade, and its exit: the compass bearing
     * in whole degrees it leaves the offing on, the nearest open one of its
     * route to the partner's (-1 for none: the route's own abyss, as before).
     */
    public record Call(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, int route, double arrives, int partner, int exit) {

        /** A call with no partner and no exit: every call before WN7. */
        public Call(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, int route, double arrives) {
            this(cargo, ship, inbound, route, arrives, 0, -1);
        }
    }

    /**
     * A boat at a moment: its call, where it is in plots, whether it is
     * loaded, whether it lies at the quay, its heading (radians, east 0,
     * south a quarter turn: the way it is going; at the quay, out along its
     * route), and how much of it shows (1, fading to 0 in the abyss).
     */
    public record Boat(Call call, double x, double y, boolean loaded, boolean docked, double heading, double alpha) { }

    /** One (kind, class, direction) of the month: its seed and its calls. */
    private record Group(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, long seed, int calls) { }

    /** An offing that forks (WN7): where it is, in plots, and the legs of its forks - four doubles each, the offing then the exit's end - by bearing from north. */
    private record Offing(double x, double y, double[] legs) { }

    /**
     * The least share of a kind's tonnes an exit must carry to be drawn as a fork: 0.05, an exit a twentieth of the kind's partners' tonnes take.
     * The Earth's 196 nations stand on 47 to 61 of the 72 bearings from a port (thirteen measured), so every bearing with a partner on it drawn
     * would be a starburst; a boat bound for a small partner still leaves the offing on its exit and sails off the drawn forks, as a partner under
     * TradeLanes.MIN_LANE has no lane on the Earth map.
     */
    public static final double FORK_SHARE = 0.05;

    /**
     * The most forks a route's kind of cargo draws at its offing: 8, the largest shares kept, the smaller bearing on a tie - a bound on the
     * strokes, like TradeLanes' 16 (BUCKETS), though at 0.05 a share (FORK_SHARE) at most 20 exits qualify. An offing draws the union of its routes'
     * forks, so at most MAX_FORKS for each of the four kinds of cargo.
     */
    public static final int MAX_FORKS = 8;

    private static final int[] NO_BEARINGS = new int[0];
    private static final double[] NO_LEGS = new double[0];

    private final int month;
    private final List<Route> routes;
    private final List<Group> groups;
    private final int count;
    private final CallPartners partners;

    /* Built on the first ask: the calls' keys, sorted; where each route's begin (route + 1, so routeStart[0] is the calls with none); the index of routes by district cell. */
    private long[] keys;
    private int[] routeStart;
    private java.util.Map<Long, int[]> index;
    private int touched;

    /* Built with them: each route's legs' boxes (legBoxes()); and the frame each route was last looked at in, so a frame looks at a route once without a set. */
    private double[][][] boxes;
    private int[] seenAt;
    private int frames;

    /** Each call's partner and exit once a frame or the list has drawn it (callAt()); null until then, and always with no partners. */
    private int[] drawn;

    /* Built when asked (WN7, slice U): each route's forks (forksOf()), by route and, so that the routes of one lane work them out once, by exits array and kind of cargo; the offings that fork, by district cell (forks()). */
    private int[][] forkCache;
    private java.util.IdentityHashMap<double[], int[][]> forkMemo;
    private Offing[] offings;
    private java.util.Map<Long, int[]> offingIndex;
    private int[] offingSeen;
    private int offingFrames;

    private BoatSchedule(int month, List<Route> routes, List<Group> groups, CallPartners partners) {
        this.month = month;
        this.routes = routes;
        this.groups = groups;
        this.partners = partners;
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
        return of(month, ports, berths, foundX, foundY, Ports.BULK_TERMINAL_SHIPS);
    }

    /** ...with the dry bulk classes the city's berths take (FR3 call 6, Ports.bulkShipsStanding()). */
    public static BoatSchedule of(int month, Ports ports, List<Berth> berths, long foundX, long foundY, List<Ports.Ship> bulkShips) {
        List<Route> routes = new ArrayList<>();
        if (berths != null) for (Berth b : berths) routes.add(lane(b, foundX, foundY));
        return of(month, ports, routes, bulkShips);
    }

    /** ...on routes found already (0.7.97: the map's, SeaRoutes'), one a berth. */
    public static BoatSchedule of(int month, Ports ports, List<Route> routes) {
        return of(month, ports, routes, Ports.BULK_TERMINAL_SHIPS);
    }

    /**
     * ...and the dry bulk classes the city's berths take (FR3 call 6,
     * docs/queue/FR3.md 2: Ports.bulkShipsStanding(), a pure function of the
     * buildings standing, so the schedule keeps no state of it). The two
     * shorter forms use the Bulk Terminal's, Panamax and Capesize.
     */
    public static BoatSchedule of(int month, Ports ports, List<Route> routes, List<Ports.Ship> bulkShips) {
        return of(month, ports, routes, bulkShips, CallPartners.NONE);
    }

    /**
     * ...and the partners its calls draw (WN7): each call's nation by the month's tonnes of its cargo (CallPartners.pick()), and on a route with
     * exits the one nearest the partner's bearing. With CallPartners.NONE, or null, this is every form above: the same calls, arrivals,
     * routes and positions, byte for byte.
     */
    public static BoatSchedule of(int month, Ports ports, List<Route> routes, List<Ports.Ship> bulkShips, CallPartners partners) {
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
                        add(groups, month, c, true, Ports.bulkShipFor(in, bulkShips), in);
                        add(groups, month, c, false, Ports.bulkShipFor(out, bulkShips), out);
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
        return new BoatSchedule(month, routes, groups, partners == null ? CallPartners.NONE : partners);
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

    /**
     * A call out of its key, with its partner and exit (WN7): the nation CallPartners draws for it by a hash of the key and PARTNER_SALT, and the exit of
     * its route nearest that nation's bearing (exitFor()); with no partners, the call as it always was.
     */
    private Call call(long key) {
        Group g = groups.get((int) (key & GROUP_MASK));
        int route = (int) (key >>> ROUTE_SHIFT) - 1;
        double arrives = arrivalOf(key);
        if (partners == CallPartners.NONE) return new Call(g.cargo(), g.ship(), g.inbound(), route, arrives);
        int partner = partners.pick(g.cargo(), g.inbound(), false, World.mix(key ^ PARTNER_SALT));
        int exit = partner > 0 && route >= 0 ? exitFor(routes.get(route), partners.bearing(partner)) : -1;
        return new Call(g.cargo(), g.ship(), g.inbound(), route, arrives, partner, exit);
    }

    /**
     * The i-th call of the sorted keys: its partner and exit drawn the first time it is asked for and kept (drawn[i], the partner from bit
     * DRAWN_PARTNER up and the exit + 1 below it, DRAWN_SET once drawn), so the frames after see a boat's partner for the price of a read - a
     * frame at 10 billion meets the same thousands of boats sixty times a second. Built with the keys on the first ask, with partners only.
     */
    private Call callAt(int i) {
        if (partners == CallPartners.NONE) return call(keys[i]);
        if (drawn == null) drawn = new int[keys.length];
        int d = drawn[i];
        if (d == 0) {
            Call c = call(keys[i]);
            drawn[i] = DRAWN_SET | (c.partner() << DRAWN_PARTNER) | (c.exit() + 1);
            return c;
        }
        long key = keys[i];
        Group g = groups.get((int) (key & GROUP_MASK));
        int partner = (d & ~DRAWN_SET) >>> DRAWN_PARTNER, exit = (d & ((1 << DRAWN_PARTNER) - 1)) - 1;
        return new Call(g.cargo(), g.ship(), g.inbound(), (int) (key >>> ROUTE_SHIFT) - 1, arrivalOf(key), partner, exit);
    }

    /** drawn[]'s layout: set once drawn; the partner from this bit up (a nation's number, under 2^20); the exit + 1 (0 to 360) below it. */
    private static final int DRAWN_SET = 1 << 30, DRAWN_PARTNER = 10;

    /** When a key's call reaches its quay, in month fractions. */
    private static double arrivalOf(long key) {
        return ((key >>> GROUP_BITS) & ARRIVAL_MASK) / TICKS;
    }

    /** The index among a route's exits of a compass bearing: the nearest of them, wrapping at north. */
    static int exitIndex(double bearing) {
        return Math.floorMod((int) Math.round(bearing / SeaRoutes.BEARING_STEP), SeaRoutes.EXITS);
    }

    /** The plane's east component of a step along a compass bearing (degrees clockwise from north): sin b. The one place the compass meets the plane (x east, y south, World's). */
    public static double compassX(double bearing) { return StrictMath.sin(StrictMath.toRadians(bearing)); }

    /** ...and its south component, -cos b (y runs south). */
    public static double compassY(double bearing) { return -StrictMath.cos(StrictMath.toRadians(bearing)); }

    /** The plane's step and heading (radians, east 0, south a quarter turn) along each exit's bearing, worked out once: a frame's boats on exits do not take a sine and an arctangent a boat. */
    private static final double[] EXIT_X = new double[SeaRoutes.EXITS], EXIT_Y = new double[SeaRoutes.EXITS], EXIT_HEADING = new double[SeaRoutes.EXITS];

    static {
        for (int k = 0; k < SeaRoutes.EXITS; k++) {
            double b = (double) k * SeaRoutes.BEARING_STEP;
            EXIT_X[k] = compassX(b);
            EXIT_Y[k] = compassY(b);
            EXIT_HEADING[k] = Math.atan2(EXIT_Y[k], EXIT_X[k]);
        }
    }

    /** The angle between two compass bearings, in degrees, [0, 180]. */
    static double apart(double a, double b) { return Math.abs(Math.IEEEremainder(a - b, 360)); }

    /**
     * The exit of a route nearest a compass bearing (WN7): of the bearings whose run is open (> 0), the one the least angle from it, the smaller
     * bearing on a tie, in whole degrees; -1 for a bearing unknown (< 0), a route with no exits, or none open. The first open exit either way
     * round from the bearing is the nearest on that side, so only those two are looked at.
     */
    static int exitFor(Route rt, double bearing) {
        double[] ex = rt.exits();
        if (ex == null || ex.length != SeaRoutes.EXITS || !(bearing >= 0)) return -1;
        double b = bearing % 360;
        int n = SeaRoutes.EXITS, lo = (int) Math.floor(b / SeaRoutes.BEARING_STEP);
        int best = -1;
        double bestD = Double.MAX_VALUE;
        for (int s = 0; s < n; s++) {
            int k = Math.floorMod(lo - s, n);
            if (ex[k] > 0) { best = k; bestD = apart(b, (double) k * SeaRoutes.BEARING_STEP); break; }
        }
        for (int s = 0; s < n; s++) {
            int k = Math.floorMod(lo + 1 + s, n);
            if (ex[k] > 0) {
                double d = apart(b, (double) k * SeaRoutes.BEARING_STEP);
                if (best < 0 || d < bestD || (d == bestD && k < best)) { best = k; bestD = d; }
                break;
            }
        }
        return best < 0 ? -1 : best * SeaRoutes.BEARING_STEP;
    }

    /* =====================================================================
       THE FORKS AT THE OFFING (WN7, slice U)
       ===================================================================== */

    /**
     * The exits a route draws as forks: of the nations a ship of the route's kind of cargo can be bound for or from (CallPartners.seaPartners(): those
     * with tonnes of that kind in either direction, else of every kind, as pick() falls back), each one's share of those tonnes (seaShares()) is added
     * to the exit exitFor() gives its bearing, so two nations on one exit add; a fork is an exit whose shares add to at least FORK_SHARE, and of those
     * at most MAX_FORKS are kept, the largest, the smaller bearing on a tie. As compass degrees ascending, a multiple of SeaRoutes.BEARING_STEP each.
     * Empty for a route without exits, partners without a bearing or none with a share to speak of, and for a schedule without partners.
     * Boats still leave on every partner's exit (Call.exit()): a small partner's boat just sails off the drawn forks, as a partner under
     * TradeLanes.MIN_LANE has no lane on the Earth map. Worked out the first time it is asked and kept (the same array every time: never written).
     */
    public int[] forksOf(int route) {
        if (partners == CallPartners.NONE || route < 0 || route >= routes.size()) return NO_BEARINGS;
        if (forkCache == null) forkCache = new int[routes.size()][];
        int[] f = forkCache[route];
        if (f == null) f = forkCache[route] = forksOf(routes.get(route));
        return f;
    }

    private int[] forksOf(Route rt) {
        double[] ex = rt.exits();
        if (ex == null || ex.length != SeaRoutes.EXITS) return NO_BEARINGS;
        if (forkMemo == null) forkMemo = new java.util.IdentityHashMap<>();
        int[][] byKind = forkMemo.computeIfAbsent(ex, k -> new int[Ports.Cargo.values().length][]);
        Ports.Cargo kind = rt.berth().cargo();
        int[] f = byKind[kind.ordinal()];
        if (f == null) {
            int[] nations = partners.seaPartners(kind);
            double[] shares = partners.seaShares(kind), byExit = new double[SeaRoutes.EXITS];
            for (int k = 0; k < nations.length; k++) {
                int e = exitFor(rt, partners.bearing(nations[k]));
                if (e >= 0) byExit[e / SeaRoutes.BEARING_STEP] += shares[k];
            }
            // The exits over FORK_SHARE, the largest MAX_FORKS of them (the smaller bearing first on a tie), in ascending bearings.
            int[] big = java.util.stream.IntStream.range(0, byExit.length).filter(k -> byExit[k] >= FORK_SHARE).boxed()
                    .sorted((a, b) -> byExit[a] == byExit[b] ? Integer.compare(a, b) : Double.compare(byExit[b], byExit[a]))
                    .limit(MAX_FORKS).mapToInt(Integer::intValue).sorted().toArray();
            f = new int[big.length];
            for (int k = 0; k < big.length; k++) f[k] = big[k] * SeaRoutes.BEARING_STEP;
            byKind[kind.ordinal()] = f;
        }
        return f;
    }

    /**
     * The forks on the rectangle [x0, x1) x [y0, y1) in plots, as the legs of each in turn - four doubles a fork: the offing's x and y, then the
     * end of the exit's run - for the routes' offings, each offing's forks once whatever routes share it (the union of their routes' forksOf(), each
     * kind of cargo's capped at MAX_FORKS already), a fork drawn when its leg's box meets the rectangle. Only the offings in the district cells the
     * rectangle covers are looked at; empty with no partners. Built on the first ask; for one thread at a time, the screen's.
     */
    public double[] forks(long x0, long y0, long x1, long y1) {
        if (partners == CallPartners.NONE) return NO_LEGS;
        buildOffings();
        if (offings.length == 0) return NO_LEGS;
        int stamp = ++offingFrames, n = 0;
        long cx0 = Math.floorDiv(x0, World.DISTRICT), cx1 = Math.floorDiv(Math.max(x0, x1 - 1), World.DISTRICT);
        long cy0 = Math.floorDiv(y0, World.DISTRICT), cy1 = Math.floorDiv(Math.max(y0, y1 - 1), World.DISTRICT);
        if ((cx1 - cx0 + 1) * (cy1 - cy0 + 1) > offingIndex.size()) {
            for (java.util.Map.Entry<Long, int[]> e : offingIndex.entrySet()) {
                long cx = e.getKey() >> 32, cy = (int) (long) e.getKey();
                if (cx < cx0 || cx > cx1 || cy < cy0 || cy > cy1) continue;
                for (int o : e.getValue()) {
                    if (offingSeen[o] == stamp) continue;
                    offingSeen[o] = stamp;
                    n = forksInto(n, offings[o], x0, y0, x1, y1);
                }
            }
        } else {
            for (long cx = cx0; cx <= cx1; cx++) {
                for (long cy = cy0; cy <= cy1; cy++) {
                    int[] os = offingIndex.get(key(cx, cy));
                    if (os == null) continue;
                    for (int o : os) {
                        if (offingSeen[o] == stamp) continue;
                        offingSeen[o] = stamp;
                        n = forksInto(n, offings[o], x0, y0, x1, y1);
                    }
                }
            }
        }
        return n == 0 ? NO_LEGS : java.util.Arrays.copyOf(forkOut, n);
    }

    /** The legs a frame has gathered, grown as needed and kept (a frame allocates only the copy it hands out). */
    private double[] forkOut = new double[64];

    /** An offing's forks whose legs' boxes meet the rectangle, added to the frame's after its first n doubles; the new count. */
    private int forksInto(int n, Offing o, long x0, long y0, long x1, long y1) {
        double[] legs = o.legs();
        for (int i = 0; i < legs.length; i += 4) {
            double ex = legs[i + 2], ey = legs[i + 3];
            if (Math.max(o.x(), ex) < x0 || Math.min(o.x(), ex) >= x1 || Math.max(o.y(), ey) < y0 || Math.min(o.y(), ey) >= y1) continue;
            if (n + 4 > forkOut.length) forkOut = java.util.Arrays.copyOf(forkOut, forkOut.length * 2);
            System.arraycopy(legs, i, forkOut, n, 4);
            n += 4;
        }
        return n;
    }

    /**
     * The offings that fork, made once from the routes: each distinct offing point of a route with forks, with the union of the forks of the
     * routes that share it (a lane's kinds of cargo each take their own partners' exits), and indexed by the district cells its longest fork reaches.
     */
    private void buildOffings() {
        if (offings != null) return;
        java.util.Map<List<Double>, double[]> runs = new java.util.LinkedHashMap<>();
        for (int r = 0; r < routes.size(); r++) {
            int[] f = forksOf(r);
            if (f.length == 0) continue;
            Route rt = routes.get(r);
            double[] at = runs.computeIfAbsent(List.of(rt.xs()[rt.offing()], rt.ys()[rt.offing()]), p -> new double[SeaRoutes.EXITS]);
            for (int e : f) at[e / SeaRoutes.BEARING_STEP] = Math.max(at[e / SeaRoutes.BEARING_STEP], rt.exitRun(e));
        }
        offings = new Offing[runs.size()];
        java.util.Map<Long, List<Integer>> cells = new java.util.HashMap<>();
        int i = 0;
        for (java.util.Map.Entry<List<Double>, double[]> e : runs.entrySet()) {
            double ox = e.getKey().get(0), oy = e.getKey().get(1), longest = 0;
            double[] legs = new double[SeaRoutes.EXITS * 4];
            int n = 0;
            for (int k = 0; k < SeaRoutes.EXITS; k++) {
                double run = e.getValue()[k];
                if (!(run > 0)) continue;
                legs[n++] = ox;
                legs[n++] = oy;
                legs[n++] = ox + EXIT_X[k] * run;
                legs[n++] = oy + EXIT_Y[k] * run;
                longest = Math.max(longest, run);
            }
            offings[i] = new Offing(ox, oy, java.util.Arrays.copyOf(legs, n));
            long cx0 = Math.floorDiv((long) Math.floor(ox - longest), World.DISTRICT), cx1 = Math.floorDiv((long) Math.floor(ox + longest), World.DISTRICT);
            long cy0 = Math.floorDiv((long) Math.floor(oy - longest), World.DISTRICT), cy1 = Math.floorDiv((long) Math.floor(oy + longest), World.DISTRICT);
            for (long cx = cx0; cx <= cx1; cx++) for (long cy = cy0; cy <= cy1; cy++) cells.computeIfAbsent(key(cx, cy), x -> new ArrayList<>()).add(i);
            i++;
        }
        offingIndex = new java.util.HashMap<>();
        for (java.util.Map.Entry<Long, List<Integer>> e : cells.entrySet()) offingIndex.put(e.getKey(), e.getValue().stream().mapToInt(Integer::intValue).toArray());
        offingSeen = new int[offings.length];
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
        double run = call.exit() >= 0 ? route.exitRun(call.exit()) : 0;
        if (run > 0) return alongExit(call, route, run, f, coming, loaded, docked);
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

    /**
     * ...for a call that leaves by an exit (WN7), `run` plots of open sea from the offing on its bearing: f is a share of the way from the quay
     * (0) to the exit's end (1), so the boat rides the route's own points up to the offing unturned, then the exit's leg, turned by turnOf() about the
     * offing where the route has a spread, and shows less from the offing on (alpha falls to 0 at the exit's end).
     */
    private static Boat alongExit(Call call, Route route, double run, double f, boolean coming, boolean loaded, boolean docked) {
        double[] xs = route.xs(), ys = route.ys(), al = route.along();
        int o = route.offing();
        double fade = al[o], d = f * (fade + run), x, y, heading;
        if (d <= fade) {
            int s = 0;
            while (s < o - 1 && al[s + 1] < d) s++;
            double seg = al[s + 1] - al[s], u = seg > 0 ? (d - al[s]) / seg : 0;
            x = xs[s] + (xs[s + 1] - xs[s]) * u;
            y = ys[s] + (ys[s + 1] - ys[s]) * u;
            heading = Math.atan2(ys[s + 1] - ys[s], xs[s + 1] - xs[s]);
        } else {
            int ei = call.exit() % SeaRoutes.BEARING_STEP == 0 ? exitIndex(call.exit()) : -1;
            double ux = ei >= 0 ? EXIT_X[ei] : compassX(call.exit()), uy = ei >= 0 ? EXIT_Y[ei] : compassY(call.exit()), e = d - fade;
            x = xs[o] + ux * e;
            y = ys[o] + uy * e;
            heading = ei >= 0 ? EXIT_HEADING[ei] : Math.atan2(uy, ux);
            if (route.spread() > 0) {
                double turn = turnOf(call, route), c = Math.cos(turn), sn = Math.sin(turn), dx = x - xs[o], dy = y - ys[o];
                x = xs[o] + dx * c - dy * sn;
                y = ys[o] + dx * sn + dy * c;
                heading += turn;
            }
        }
        if (coming) heading += Math.PI;
        double alpha = d <= fade ? 1 : Math.max(0, 1 - (d - fade) / run);
        return new Boat(call, x, y, loaded, docked, heading, alpha);
    }

    /* ----- the harbour's quay and its boats alongside (HB1, 2026-11-08) ----- */

    /**
     * A docked boat lying alongside its quay, not bow-on to it (HB1; Jerus, 2026-10-31: "and that ships dock properly"), from the detail
     * zoom in: above MapFrame.DETAIL_FROM px a plot, strictly; at it and below, and for a boat not docked, or a berth whose side is not
     * known (-1: a berth made by hand), `b` itself - the map is drawn as it was.
     *
     * The geometry, worked from CityShore.Work. Its quay runs `quay` plots out from the middle of its shore plot, so the quay's outer face
     * is (quay + 0.5) plots out from that middle; its berth point (Work.berth(), where a docked boat is) is the middle of the plot just
     * past the quay, (quay + 1) out - half a plot seaward of the face. The boat heads along the face: the sea's direction (CityShore.DX,
     * DY of `side`) turned a quarter, clockwise on the screen, the same way on every side. Its side lies on the face when its centre is
     * half its drawn beam (ShipShapes.drawnLength() x BEAM / 2) seaward of the face, which from the berth point is (half beam - 0.5)
     * plots along the sea's direction: a little shoreward for a hull narrower than a plot, seaward for a wider one, and the same for a
     * quay of any length. The centre stays on the berth's line out from the work's middle, so the boat lies off the middle of the work's
     * sea side. Pure.
     */
    public static Boat alongside(Boat b, int side, double scale) {
        if (b == null || !b.docked() || side < 0 || side >= CityShore.DX.length || !(scale > MapFrame.DETAIL_FROM)) return b;
        double dx = CityShore.DX[side], dy = CityShore.DY[side];
        double shift = ShipShapes.drawnLength(b, scale) * ShipShapes.BEAM / 2 - 0.5;
        return new Boat(b.call(), b.x() + dx * shift, b.y() + dy * shift, b.loaded(), true, Math.atan2(dy, dx) + Math.PI / 2, b.alpha());
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
                        long k = keys[i];
                        double a = arrivalOf(k);
                        if (a > t + leg) break;
                        // A partner is drawn only for a call whose boat is on the water now (position() answers null outside this span): a frame stays as cheap as without them.
                        if (partners != CallPartners.NONE && (t < a - leg || t > a + berth + leg)) continue;
                        Boat b = position(callAt(i), rt, t);
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
     * (its far end's distance from the pivot x sin(spread), and a plot). A
     * route with exits (WN7) has one box more, the offing's, a plot past its
     * longest exit's run every way: the legs of the boats that leave by them.
     */
    static double[][] legBoxes(Route rt) {
        double[] xs = rt.xs(), ys = rt.ys();
        boolean exits = rt.exits() != null;
        double[][] out = new double[xs.length - 1 + (exits ? 1 : 0)][];
        if (exits) {
            double longest = 0;
            for (double e : rt.exits()) longest = Math.max(longest, e);
            double ox = xs[rt.offing()], oy = ys[rt.offing()], w = longest + 1;
            out[xs.length - 1] = new double[] { ox - w, oy - w, ox + w, oy + w };
        }
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
        for (int i = 0; i < keys.length; i++) out.add(callAt(i));
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
