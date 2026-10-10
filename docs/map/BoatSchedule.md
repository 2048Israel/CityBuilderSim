# BoatSchedule.java - 453 lines · 30 methods · 8 constants · model

`ham/citybuildersim/BoatSchedule.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The month's ships, as a pure function of time: which calls the city's sea
> trade makes, when each arrives, and where each boat is at any moment
> (0.7.86, batch O9; runs/spec-oil.md 2.10, the research's 4.3-4.4 and Q8;
> scratch-oil's BoatProto, ported). Model only: since 0.7.97 (batch O13)
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
> by the call's own hash (Route.spread()), so the boats do not queue on one
> line. A leg is the whole route, quay to the abyss's end, whatever its
> length. A frame asks which routes cross the screen (an index by district
> of every leg) and, on each, which calls are on the water now (a binary
> search of the route's sorted arrivals).
> 
> CHEAP TO COUNT, BUILT ONLY WHEN ASKED. The month's calls are counted by
> kind, direction and class when the schedule is made (callCount()); each
> call's route and arrival are worked out the first time a frame or the
> list asks, as one sorted long a call - its route, its arrival to 2^-36 of
> a month, and its group - so a city of billions is a sorted array, not
> millions of objects (spec-oil 6's scale).

**Uses:** [Ports](Ports.md) (27), [World](World.md) (16)

**Used by (10):** [CityMap](CityMap.md), [CityShore](CityShore.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [PortCheck](PortCheck.md), [ReadPathCheck](ReadPathCheck.md), [SeaRoutes](SeaRoutes.md), [ShipShapes](ShipShapes.md)

## Sections

| line | section |
|---:|---|
| 157 | THE MONTH'S CALLS |
| 299 | WHERE A BOAT IS |
| 426 | · reads |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `BoatSchedule.MONTH_SECONDS` | `60` | Seconds of boats a game month: 60 (the research's Q8: about one boat every 4 s at district zoom in the big city). |
| 60 | `BoatSchedule.LEG_SECONDS` | `4` | Seconds a boat takes to run its lane, in or out: 4 (spec-oil 2.10, the UI's). |
| 63 | `BoatSchedule.BERTH_SECONDS` | `24 / 730.5 * MONTH_SECONDS` | Seconds a boat lies at the quay: about 24 hours of a 730.5-hour month [P33], about 2 s. |
| 66 | `BoatSchedule.LANE_PLOTS` | `1536` | A lane's length, quay to the map's sea, in plots: 1,536 (six districts, spec-oil 2.10). |
| 69 | `BoatSchedule.SALT` | `0xB0A7_5417L` | The salt the calls' hashes start from. |
| 72 | `BoatSchedule.GROUP_BITS` | `7, ARRIVAL_BITS = 36, ROUTE_SHIFT = GROUP_BITS + ARRIVAL_BITS` | A call's key: its route + 1 (0 for none) in the top ROUTE_BITS, its arrival in ARRIVAL_BITS ticks of a month, its group in the low GROUP_BITS. |
| 73 | `BoatSchedule.GROUP_MASK` | `(1L<<GROUP_BITS) - 1, ARRIVAL_MASK =(1L<<ARRIVAL_BITS) - 1` |  |
| 74 | `BoatSchedule.TICKS` | `(double)(1L<<ARRIVAL_BITS)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 130 | `private final int month` |  |
| 131 | `private final List<Route> routes` |  |
| 132 | `private final List<Group> groups` |  |
| 133 | `private final int count` |  |
| 136 | `private long[] keys` | Built on the first ask: the calls' keys, sorted; where each route's begin (route + 1, so routeStart[0] is the calls with none); the index of routes by district cell. |
| 137 | `private int[] routeStart` |  |
| 138 | `private java.util.Map<Long, int[]> index` |  |
| 139 | `private int touched` |  |
| 142 | `private double[][][] boxes` | Built with them: each route's legs' boxes (legBoxes()); and the frame each route was last looked at in, so a frame looks at a route once without a set. |
| 143 | `private int[] seenAt` |  |
| 144 | `private int frames` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 400 | **type** `public final class BoatSchedule` | The month's ships, as a pure function of time: which calls the city's sea trade makes, when each arrives, and where each boat is at any moment (0.7.86, batch O9; runs/spec-oil.md 2.10, the research's 4.3-4.4 and Q8; s... |
| 77 | 1 | **type** `public record Berth(Ports.Cargo cargo, long x, long y)` | One terminal's quay: its kind and where on the map it stands, in plots. |
| 87 | 23 | **type** `public record Route(Berth berth, long x1, long y1, double[] xs, double[] ys, double[] along, int offing, in...` | A berth's route: its quay, its far end (x1, y1) out at sea, and the way between as points in plots - the berth first, its turns, the offing (point `offing`), the abyss's end last - with each point's distance along it ... |
| 90 | 4 | `public Route(Berth berth, long x1, long y1)` _(in BoatSchedule.Route)_ | A straight lane from its quay to (x1, y1): O9's. |
| 96 | 7 | `public static Route through(Berth berth, double[] xs, double[] ys, int offing, int pivot, double spread)` _(in BoatSchedule.Route)_ | A route through points (SeaRoutes'): the offing's index, the pivot's, the spread. |
| 105 | 1 | `public double length()` _(in BoatSchedule.Route)_ | Its length, quay to far end, in plots. |
| 108 | 1 | `public double fadeFrom()` _(in BoatSchedule.Route)_ | Where the abyss begins, in plots along it: at the offing; a lane's whole length. |
| 117 | 1 | **type** `public record Call(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, int route, double arrives)` | One call: a kind and its class, whether it lands cargo (an import, or a box ship, which carries both ways) or comes to load it, its route among the schedule's (-1 with no berth of its kind to draw it at), and when it ... |
| 125 | 1 | **type** `public record Boat(Call call, double x, double y, boolean loaded, boolean docked, double heading, double al...` | A boat at a moment: its call, where it is in plots, whether it is loaded, whether it lies at the quay, its heading (radians, east 0, south a quarter turn: the way it is going; at the quay, out along its route), and ho... |
| 128 | 1 | **type** `private record Group(Ports.Cargo cargo, Ports.Ship ship, boolean inbound, long seed, int calls)` | One (kind, class, direction) of the month: its seed and its calls. |
| 146 | 8 | `private BoatSchedule(int month, List<Route> routes, List<Group> groups)` |  |
| 155 | 1 | `private static long key(long cx, long cy)` |  |

### THE MONTH'S CALLS (lines 157-298)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 6 | `public static int callsFor(double tonnes, double cargo, long seed)` | The calls a month's tonnes make in a ship of this cargo: the whole calls, and one more in the share of months the fraction says. |
| 170 | 3 | `static long seed(int month, Ports.Cargo cargo, boolean inbound, Ports.Ship ship)` | The seed of one (kind, direction, class) in one month. |
| 175 | 5 | `static long arrivalTicks(long seed, int k)` | When a group's k-th call reaches its quay, in ticks of a month: hashed through the month, its whole visit inside it. |
| 187 | 5 | `public static BoatSchedule of(int month, Ports ports, List<Berth> berths, long foundX, long foundY)` | The month's schedule from the ports' month billed: its calls, counted by kind, direction and class, each to go on a route of its kind's berths by a hash (none with no berth of its kind), and the routes, each quay's la... |
| 194 | 31 | `public static BoatSchedule of(int month, Ports ports, List<Route> routes)` | ...on routes found already (0.7.97: the map's, SeaRoutes'), one a berth. |
| 226 | 15 | `private static void add(List<Group> groups, int month, Ports.Cargo c, boolean inbound, Ports.Ship ship, double tonnes)` |  |
| 243 | 5 | `public static Route lane(Berth b, long foundX, long foundY)` | A quay's lane: straight out to sea, away from the founding site, for LANE_PLOTS (north when the quay is the site itself). |
| 250 | 40 | `private void build()` | The calls' keys, sorted by route, then arrival; built on the first ask. |
| 292 | 6 | `private Call call(long key)` | A call out of its key. |

### WHERE A BOAT IS (lines 299-425)

| line | len | member | says |
|---:|---:|---|---|
| 308 | 9 | `public static Boat position(Call call, Route route, double t)` | Where a call's boat is at t (month fractions), or null when it is not on the water: running in over its leg, at the quay, or running out. |
| 324 | 20 | `static Boat along(Call call, Route route, double f, boolean coming, boolean loaded, boolean docked)` | A call's boat at a share f of its route from the quay (0 at the quay, lying there; 1 at the far end), coming in or going out: its place on the way, turned about the pivot by the call's spread past it, its heading, and... |
| 346 | 4 | `static double turnOf(Call call, Route route)` | A call's turn of its route's last leg, in radians: within its route's spread either way, by the call's own hash (its arrival and its route). |
| 357 | 31 | `public List<Boat> frame(long x0, long y0, long x1, long y1, double t)` | The boats on the water at t whose routes cross the rectangle [x0, x1) x [y0, y1) in plots: the routes indexed by the district cells the rectangle covers, and on each a binary search of its arrivals. |
| 390 | 3 | `public static boolean crosses(Route rt, long x0, long y0, long x1, long y1)` | Whether a route meets the rectangle: one of its legs' boxes does (legBoxes()) - a lane's, its one box. |
| 395 | 4 | `static boolean crosses(double[][] legs, long x0, long y0, long x1, long y1)` | ...by its legs' boxes, worked out already. |
| 405 | 13 | `static double[][] legBoxes(Route rt)` | Each leg's box, {x0, y0, x1, y1} in plots, inclusive: its two points' - past the pivot widened by how far the call's spread can swing it (its far end's distance from the pivot x sin(spread), and a plot). |
| 420 | 5 | `static int lower(long[] a, int from, int to, long v)` | The first index in [from, to) whose key is at least v. |

### reads (lines 426-453)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 1 | `public int month()` |  |
| 429 | 1 | `public List<Route> routes()` |  |
| 432 | 6 | `public List<Call> calls()` | Every call of the month, by route and arrival (those with no route first). |
| 440 | 1 | `public int callCount()` | The month's calls, all kinds: counted when the schedule is made, nothing built. |
| 443 | 1 | `public int touched()` | The routes the last frame() looked at. |
| 446 | 5 | `public int callsOf(Ports.Cargo c)` | The calls of one kind. |
| 452 | 1 | `public String toString()` |  |

