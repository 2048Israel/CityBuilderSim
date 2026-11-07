# BoatSchedule.java - 847 lines · 50 methods · 15 constants · model

`ham/citybuildersim/BoatSchedule.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> The month's ships, as a pure function of time: which calls the city's sea
> trade makes, when each arrives, and where each boat is at any moment
> (0.7.86, batch O9; docs/studies/spec-oil.md 2.10, the research's 4.3-4.4 and
> Q8; scratch-oil's BoatProto, ported). Model only: since 0.7.97 (batch O13)
> ui/MapView draws it on the game's clock, on the map's sea routes.
> 
> WHY. A port that is a share of the freight band is invisible; the
> research's answer is boats that follow the trade exactly - a city with no
> seaborne trade has none - and cost nothing off screen. So A CALL IS A
> SCHEDULE ENTRY, NOT AN OBJECT: a kind, a class, a direction, a route and an
> arrival time hashed from (kind, direction, class, month, call number), and
> a boat's place is route(call, t). Nothing is simulated, stored or saved;
> panning away and back shows the same boat in the same place.
> 
> CALLS. Tonnes ÷ the class's cargo, by kind and direction (Ports' month):
> crude in the class its tank room allowed, the other liquids in MR product
> tankers, dry bulk in a Capesize when the month fills one and a Panamax
> when not, boxes in feeders - one call both ways, so the larger direction
> counts [4.3 rule 3] - and general cargo in 5,500 t ships. A fraction of a
> call is a whole one in the share of months its fraction says, by a hash of
> the month: whole each month, exact on average.
> 
> TIME (Q8). A month is MONTH_SECONDS of boats. A call runs in from its lane's
> end to its quay over LEG_SECONDS, lies at the quay BERTH_SECONDS (about 24
> hours [P33]) and runs out again; its arrival is spread through the month by
> its hash, inside the month, so a boat never appears or vanishes at the
> month's turn.
> 
> ROUTES. Each berth's quay runs straight out to sea, away from the founding
> site, for LANE_PLOTS (lane(): O9's, still each berth's with no sea routes
> found). Since 0.7.97 (batch O13; spec-roads-and-ports.md 4.1) the map's
> berths sail SeaRoutes' routes: from the quay's end along the way found on
> the sea's grid to the offing and on into the abyss, where a boat fades
> (Boat.alpha()); each call's last leg turned a few degrees about its start
> by the call's own hash (turnOf()), so the boats do not queue on one
> line. A leg is the whole route, quay to the abyss's end, whatever its
> length. A frame asks which routes cross the screen (an index by district
> of every leg) and, on each, which calls are on the water now (a binary
> search of the route's sorted arrivals).
> 
> PARTNERS AND EXITS (WN7; docs/studies/spec-nations.md 9). A call may carry
> the nation it is bound for or from (Call.partner), drawn by CallPartners
> from the month's tonnes of its kind of cargo by a hash of its key and
> PARTNER_SALT - never the arrival's or the route's, so a month's calls are
> the same with partners as without - and, on a route that says its exits
> (SeaRoutes: an offing per bearing), the exit nearest that nation's compass
> bearing (Call.exit). A boat with an exit rides the route's points to the
> offing unturned and leaves it on the exit's bearing, its last leg turned by
> turnOf() about the offing, fading to the exit's end; one with none is the
> route's own, byte for byte. The partner is drawn only for a call whose boat
> is on the water, so a frame stays as cheap.
> 
> FORKS (WN7, slice U). The lanes the map strokes fork at the offing into the
> exits the month's big partners take: forksOf() gives a route the exits
> exitFor() finds for the nations with tonnes of its kind of cargo
> (CallPartners.seaPartners()) that carry at least FORK_SHARE of them between
> them, the MAX_FORKS largest at most, and forks() the legs of them on a
> rectangle, each offing's once whatever routes share it, indexed by district
> cell, so a frame touches only the offings in view, no route and no call.
> Pure in the month's partners; with none, nothing.
> ... (7 more lines in the source)

**Uses:** [Ports](Ports.md) (39), [World](World.md) (25), [SeaRoutes](SeaRoutes.md) (24), [CallPartners](CallPartners.md) (10)

**Used by (18):** [CallPartners](CallPartners.md), [CityMap](CityMap.md), [CityShore](CityShore.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [Game](Game.md), [InfrastructureScreen](InfrastructureScreen.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [PartnerCallsCheck](PartnerCallsCheck.md), [PortCheck](PortCheck.md), [ReadPathCheck](ReadPathCheck.md), [SeaLanes](SeaLanes.md), [SeaRoutes](SeaRoutes.md), [ShipShapes](ShipShapes.md), [TrainSchedule](TrainSchedule.md), [TruckSchedule](TruckSchedule.md)

## Sections

| line | section |
|---:|---|
| 250 | THE MONTH'S CALLS |
| 503 | THE FORKS AT THE OFFING (WN7, slice U) |
| 645 | WHERE A BOAT IS |
| 820 | · reads |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `BoatSchedule.MONTH_SECONDS` | `60` | Seconds of boats a game month: 60 (the research's Q8: about one boat every 4 s at district zoom in the big city). |
| 81 | `BoatSchedule.LEG_SECONDS` | `4` | Seconds a boat takes to run its lane, in or out: 4 (spec-oil 2.10, the UI's). |
| 84 | `BoatSchedule.BERTH_SECONDS` | `24 / 730.5 * MONTH_SECONDS` | Seconds a boat lies at the quay: about 24 hours of a 730.5-hour month [P33], about 2 s. |
| 87 | `BoatSchedule.LANE_PLOTS` | `1536` | A lane's length, quay to the map's sea, in plots: 1,536 (six districts, spec-oil 2.10). |
| 90 | `BoatSchedule.SALT` | `0xB0A7_5417L` | The salt the calls' hashes start from. |
| 93 | `BoatSchedule.PARTNER_SALT` | `0x9A47_4E25L` | The salt a call's partner is drawn with (WN7): a hash of the call's key and this, never the arrival's or the route's, so a month's calls are the same with partners as without. |
| 96 | `BoatSchedule.GROUP_BITS` | `7, ARRIVAL_BITS = 36, ROUTE_SHIFT = GROUP_BITS + ARRIVAL_BITS` | A call's key: its route + 1 (0 for none) from bit ROUTE_SHIFT up, its arrival in ARRIVAL_BITS ticks of a month, its group in the low GROUP_BITS. |
| 98 | `BoatSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ARRIVAL_MASK =(1L<<ARRIVAL_BITS) - 1` | The masks that take a key's group and its arrival back out, once shifted down. |
| 100 | `BoatSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` | A month's arrival ticks, 2^36: an arrival in ticks over this is its share of the month. |
| 198 | `BoatSchedule.FORK_SHARE` | `0.05` | The least share of a kind's tonnes an exit must carry to be drawn as a fork: 0.05, an exit a twentieth of the kind's partners' tonnes take. |
| 205 | `BoatSchedule.MAX_FORKS` | `8` | The most forks a route's kind of cargo draws at its offing: 8, the largest shares kept, the smaller bearing on a tie - a bound on the strokes, like TradeLanes' 16 (BUCKETS), though at 0.05 a share (FORK_SHARE) at most... |
| 207 | `BoatSchedule.NO_BEARINGS` | `new int [ 0 ]` |  |
| 208 | `BoatSchedule.NO_LEGS` | `new double [ 0 ]` |  |
| 443 | `BoatSchedule.DRAWN_SET` | `1<<30, DRAWN_PARTNER = 10` | drawn[]'s layout: set once drawn; the partner from this bit up (a nation's number, under 2^20); the exit + 1 (0 to 360) below it. |
| 462 | `BoatSchedule.EXIT_X` | `new double [ SeaRoutes.EXITS ], EXIT_Y = new double [ SeaRoutes.EXITS ], EXIT...` | The plane's step and heading (radians, east 0, south a quarter turn) along each exit's bearing, worked out once: a frame's boats on exits do not take a sine and an arctangent a boat. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 210 | `private final int month` |  |
| 211 | `private final List<Route> routes` |  |
| 212 | `private final List<Group> groups` |  |
| 213 | `private final int count` |  |
| 214 | `private final CallPartners partners` |  |
| 217 | `private long[] keys` | Built on the first ask: the calls' keys, sorted; where each route's begin (route + 1, so routeStart[0] is the calls with none); the index of routes by district cell. |
| 218 | `private int[] routeStart` |  |
| 219 | `private java.util.Map<Long, int[]> index` |  |
| 220 | `private int touched` |  |
| 223 | `private double[][][] boxes` | Built with them: each route's legs' boxes (legBoxes()); and the frame each route was last looked at in, so a frame looks at a route once without a set. |
| 224 | `private int[] seenAt` |  |
| 225 | `private int frames` |  |
| 228 | `private int[] drawn` | Each call's partner and exit once a frame or the list has drawn it (callAt()); null until then, and always with no partners. |
| 231 | `private int[][] forkCache` | Built when asked (WN7, slice U): each route's forks (forksOf()), by route and, so that the routes of one lane work them out once, by exits array and kind of cargo; the offings that fork, by district cell (forks()). |
| 232 | `private java.util.IdentityHashMap<double[], int[][]> forkMemo` |  |
| 233 | `private Offing[] offings` |  |
| 234 | `private java.util.Map<Long, int[]> offingIndex` |  |
| 235 | `private int[] offingSeen` |  |
| 236 | `private int offingFrames` |  |
| 589 | `private double[] forkOut` | The legs a frame has gathered, grown as needed and kept (a frame allocates only the copy it hands out). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 773 | **type** `public final class BoatSchedule` | The month's ships, as a pure function of time: which calls the city's sea trade makes, when each arrives, and where each boat is at any moment (0.7.86, batch O9; docs/studies/spec-oil.md 2.10, the research's 4.3-4.4 a... |
| 103 | 1 | **type** `public record Berth(Ports.Cargo cargo, long x, long y)` | One terminal's quay: its kind and where on the map it stands, in plots. |
| 120 | 39 | **type** `public record Route(Berth berth, long x1, long y1, double[] xs, double[] ys, double[] along, int offing, in...` | A berth's route: its quay, its far end (x1, y1) out at sea, and the way between as points in plots - the berth first, its turns, the offing (point `offing`), the abyss's end last - with each point's distance along it ... |
| 123 | 3 | `public Route(Berth berth, long x1, long y1, double[] xs, double[] ys, double[] along, int offing, int pivot, double spread)` _(in BoatSchedule.Route)_ | A route through points with no exits: the abyss alone, as every route was before WN7. |
| 128 | 4 | `public Route(Berth berth, long x1, long y1)` _(in BoatSchedule.Route)_ | A straight lane from its quay to (x1, y1): O9's. |
| 134 | 7 | `public static Route through(Berth berth, double[] xs, double[] ys, int offing, int pivot, double spread)` _(in BoatSchedule.Route)_ | A route through points (SeaRoutes'): the offing's index, the pivot's, the spread. |
| 143 | 3 | `public Route withExits(double[] exits)` _(in BoatSchedule.Route)_ | The same route saying its exits (SeaRoutes.EXITS runs, in plots; the array is kept, not copied), or none with null. |
| 148 | 4 | `public double exitRun(int bearing)` _(in BoatSchedule.Route)_ | The run of open sea from the offing on a compass bearing (the nearest of its exits), in plots; 0 where it is closed or the route has none. |
| 154 | 1 | `public double length()` _(in BoatSchedule.Route)_ | Its length, quay to far end, in plots. |
| 157 | 1 | `public double fadeFrom()` _(in BoatSchedule.Route)_ | Where the abyss begins, in plots along it: at the offing; a lane's whole length. |
| 170 | 7 | **type** `public record Call(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, int route, double arrives, int part...` | One call: a kind and its class, whether it lands cargo (an import, or a box ship, which carries both ways) or comes to load it, its route among the schedule's (-1 with no berth of its kind to draw it at), and when it ... |
| 173 | 3 | `public Call(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, int route, double arrives)` _(in BoatSchedule.Call)_ | A call with no partner and no exit: every call before WN7. |
| 184 | 1 | **type** `public record Boat(Call call, double x, double y, boolean loaded, boolean docked, double heading, double al...` | A boat at a moment: its call, where it is in plots, whether it is loaded, whether it lies at the quay, its heading (radians, east 0, south a quarter turn: the way it is going; at the quay, out along its route), and ho... |
| 187 | 1 | **type** `private record Group(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, long seed, int calls)` | One (kind, class, direction) of the month: its seed and its calls. |
| 190 | 1 | **type** `private record Offing(double x, double y, double[] legs)` | An offing that forks (WN7): where it is, in plots, and the legs of its forks - four doubles each, the offing then the exit's end - by bearing from north. |
| 238 | 9 | `private BoatSchedule(int month, List<Route> routes, List<Group> groups, CallPartners partners)` |  |
| 248 | 1 | `private static long key(long cx, long cy)` |  |

### THE MONTH'S CALLS (lines 250-502)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 6 | `public static int callsFor(double tonnes, double cargo, long seed)` | The calls a month's tonnes make in a ship of this cargo: the whole calls, and one more in the share of months the fraction says. |
| 263 | 3 | `static long seed(int month, Ports.Cargo cargo, boolean inbound, Ports.Ship ship)` | The seed of one (kind, direction, class) in one month. |
| 268 | 5 | `static long arrivalTicks(long seed, int k)` | When a group's k-th call reaches its quay, in ticks of a month: hashed through the month, its whole visit inside it. |
| 280 | 3 | `public static BoatSchedule of(int month, Ports ports, List<Berth> berths, long foundX, long foundY)` | The month's schedule from the ports' month billed: its calls, counted by kind, direction and class, each to go on a route of its kind's berths by a hash (none with no berth of its kind), and the routes, each quay's la... |
| 285 | 5 | `public static BoatSchedule of(int month, Ports ports, List<Berth> berths, long foundX, long foundY, List<Ports.Ship> bulkShips)` | ...with the dry bulk classes the city's berths take (FR3 call 6, Ports.bulkShipsStanding()). |
| 292 | 3 | `public static BoatSchedule of(int month, Ports ports, List<Route> routes)` | ...on routes found already (0.7.97: the map's, SeaRoutes'), one a berth. |
| 302 | 3 | `public static BoatSchedule of(int month, Ports ports, List<Route> routes, List<Ports.Ship> bulkShips)` | ...and the dry bulk classes the city's berths take (FR3 call 6, docs/queue/FR3.md 2: Ports.bulkShipsStanding(), a pure function of the buildings standing, so the schedule keeps no state of it). |
| 311 | 31 | `public static BoatSchedule of(int month, Ports ports, List<Route> routes, List<Ports.Ship> bulkShips, CallPartners partners)` | ...and the partners its calls draw (WN7): each call's nation by the month's tonnes of its cargo (CallPartners.pick()), and on a route with exits the one nearest the partner's bearing. |
| 343 | 15 | `private static void add(List<Group> groups, int month, Ports.Cargo c, boolean inbound, Ports.Ship ship, double tonnes)` |  |
| 360 | 5 | `public static Route lane(Berth b, long foundX, long foundY)` | A quay's lane: straight out to sea, away from the founding site, for LANE_PLOTS (north when the quay is the site itself). |
| 367 | 40 | `private void build()` | The calls' keys, sorted by route, then arrival; built on the first ask. |
| 412 | 9 | `private Call call(long key)` | A call out of its key, with its partner and exit (WN7): the nation CallPartners draws for it by a hash of the key and PARTNER_SALT, and the exit of its route nearest that nation's bearing (exitFor()); with no partners... |
| 427 | 14 | `private Call callAt(int i)` | The i-th call of the sorted keys: its partner and exit drawn the first time it is asked for and kept (drawn[i], the partner from bit DRAWN_PARTNER up and the exit + 1 below it, DRAWN_SET once drawn), so the frames aft... |
| 446 | 3 | `private static double arrivalOf(long key)` | When a key's call reaches its quay, in month fractions. |
| 451 | 3 | `static int exitIndex(double bearing)` | The index among a route's exits of a compass bearing: the nearest of them, wrapping at north. |
| 456 | 1 | `public static double compassX(double bearing)` | The plane's east component of a step along a compass bearing (degrees clockwise from north): sin b. |
| 459 | 1 | `public static double compassY(double bearing)` | ...and its south component, -cos b (y runs south). |
| 464 | 8 | `static { ... }` |  |
| 474 | 1 | `static double apart(double a, double b)` | The angle between two compass bearings, in degrees, [0, 180]. |
| 481 | 21 | `static int exitFor(Route rt, double bearing)` | The exit of a route nearest a compass bearing (WN7): of the bearings whose run is open (> 0), the one the least angle from it, the smaller bearing on a tie, in whole degrees; -1 for a bearing unknown (< 0), a route wi... |

### THE FORKS AT THE OFFING (WN7, slice U) (lines 503-644)

| line | len | member | says |
|---:|---:|---|---|
| 516 | 7 | `public int[] forksOf(int route)` | The exits a route draws as forks: of the nations a ship of the route's kind of cargo can be bound for or from (CallPartners.seaPartners(): those with tonnes of that kind in either direction, else of every kind, as pic... |
| 524 | 24 | `private int[] forksOf(Route rt)` |  |
| 555 | 32 | `public double[] forks(long x0, long y0, long x1, long y1)` | The forks on the rectangle [x0, x1) x [y0, y1) in plots, as the legs of each in turn - four doubles a fork: the offing's x and y, then the end of the exit's run - for the routes' offings, each offing's forks once what... |
| 592 | 11 | `private int forksInto(int n, Offing o, long x0, long y0, long x1, long y1)` | An offing's forks whose legs' boxes meet the rectangle, added to the frame's after its first n doubles; the new count. |
| 608 | 36 | `private void buildOffings()` | The offings that fork, made once from the routes: each distinct offing point of a route with forks, with the union of the forks of the routes that share it (a lane's kinds of cargo each take their own partners' exits)... |

### WHERE A BOAT IS (lines 645-819)

| line | len | member | says |
|---:|---:|---|---|
| 654 | 9 | `public static Boat position(Call call, Route route, double t)` | Where a call's boat is at t (month fractions), or null when it is not on the water: running in over its leg, at the quay, or running out. |
| 670 | 22 | `static Boat along(Call call, Route route, double f, boolean coming, boolean loaded, boolean docked)` | A call's boat at a share f of its route from the quay (0 at the quay, lying there; 1 at the far end), coming in or going out: its place on the way, turned about the pivot by the call's spread past it, its heading, and... |
| 698 | 28 | `private static Boat alongExit(Call call, Route route, double run, double f, boolean coming, boolean loaded, boolean docked)` | ...for a call that leaves by an exit (WN7), `run` plots of open sea from the offing on its bearing: f is a share of the way from the quay (0) to the exit's end (1), so the boat rides the route's own points up to the o... |
| 728 | 4 | `static double turnOf(Call call, Route route)` | A call's turn of its route's last leg, in radians: within its route's spread either way, by the call's own hash (its arrival and its route). |
| 739 | 34 | `public List<Boat> frame(long x0, long y0, long x1, long y1, double t)` | The boats on the water at t whose routes cross the rectangle [x0, x1) x [y0, y1) in plots: the routes indexed by the district cells the rectangle covers, and on each a binary search of its arrivals. |
| 775 | 3 | `public static boolean crosses(Route rt, long x0, long y0, long x1, long y1)` | Whether a route meets the rectangle: one of its legs' boxes does (legBoxes()) - a lane's, its one box. |
| 780 | 4 | `static boolean crosses(double[][] legs, long x0, long y0, long x1, long y1)` | ...by its legs' boxes, worked out already. |
| 792 | 20 | `static double[][] legBoxes(Route rt)` | Each leg's box, {x0, y0, x1, y1} in plots, inclusive: its two points' - past the pivot widened by how far the call's spread can swing it (its far end's distance from the pivot x sin(spread), and a plot). |
| 814 | 5 | `static int lower(long[] a, int from, int to, long v)` | The first index in [from, to) whose key is at least v. |

### reads (lines 820-847)

| line | len | member | says |
|---:|---:|---|---|
| 822 | 1 | `public int month()` |  |
| 823 | 1 | `public List<Route> routes()` |  |
| 826 | 6 | `public List<Call> calls()` | Every call of the month, by route and arrival (those with no route first). |
| 834 | 1 | `public int callCount()` | The month's calls, all kinds: counted when the schedule is made, nothing built. |
| 837 | 1 | `public int touched()` | The routes the last frame() looked at. |
| 840 | 5 | `public int callsOf(Ports.Cargo c)` | The calls of one kind. |
| 846 | 1 | `public String toString()` |  |

