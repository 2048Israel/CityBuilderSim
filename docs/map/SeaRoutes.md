# SeaRoutes.java - 946 lines · 44 methods · 19 constants · model

`ham/citybuildersim/SeaRoutes.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> Each sea terminal's route out to the world: found once on a grid of the sea at 480 m, pulled straight, sailed from the terminal's quay out to the offing - past the city's ground, on the bearing with the most open sea - and on into the abyss, where a boat fades - what BoatSchedule's boats sail; pure, kept by the map and never saved.
> 
> WHY THIS EXISTS (0.7.97, batch O13; the project's spec-roads-and-ports.md
> 4.1 and 7's S1 and S2, a port of its prototype roads-prototype/islands.py:
> sea_grid(), astar(), pull(), offing()). O9's boats ran from each quay
> straight out, away from the founding site, for BoatSchedule.LANE_PLOTS: a
> lane that crossed the land wherever the coast turned, and a city's ships
> all queued on one line. A route keeps to the water, comes out of the bay
> the way a ship would, and leaves the city's waters on the open side.
> 
> THE GRID. The world's sea in cells of CELL plots (480 m), a cell sea when
> at least SALT_LEAST of its SAMPLE x SAMPLE samples of the world's ground
> are salt, read a block of BLOCK x BLOCK cells at a time
> (World.regionTerrain()); off the world is sea - three quarters of a cell,
> where the prototype took 95% of its plots, so a channel a ship could use
> at 480 m is one the grid sees (star O13-5). A terminal stands only where
> its water OPENS TO THE SEA (opensToSea(): the playtest's first terminals
> stood on a salt lagoon a sand bar closes, where no ship could come).
> A cell beside a cell that is not sea is the shore's, which costs SHORE_COST
> times as much to cross (ships keep off it).
> 
> A ROUTE. From the cell of the terminal's berth (its quay's end), or the
> nearest sea cell within NEAR_CELLS:
>   THE OFFING  the bearing, every BEARING_STEP degrees, with the longest run
>               of sea cells out from it (up to RAY_CELLS; one that leaves the
>               world at sea is open water) - the one nearest the way away
>               from the founding site on a tie - and on it the point the
>               city's radius plus OFFING_M out (the prototype's rule);
>   ...IN A BAY when the sea ends sooner on every bearing (a terminal up an
>               inlet: the playtest's, star O13-5): the sea reachable from
>               the berth flooded out to that distance, and of the cells
>               farthest out the one with the longest run of sea on beyond
>               it, the way away from the site on a tie - so the boats come
>               out of the inlet the way the water goes;
>   THE WAY     A* over the sea's cells, eight ways, to the offing (the
>               flood's own way in a bay; at most EXPANSIONS_MOST cells looked
>               at), then pulled straight: a point kept only where the
>               straight line to the next would cross a cell that is not sea;
>   THE ABYSS   on along the last leg's heading ABYSS_M past the offing, or
>               as far as the sea goes where land comes sooner (abyssRun()),
>               the boat fading as it goes (BoatSchedule.Boat.alpha()).
> Each call's last leg and the abyss are turned by up to SPREAD_DEG either way
> about the leg's start, by the call's own hash, so boats do not queue on one
> line (spec 4.1) - where the fan of those lines keeps to the sea; else none.
> A berth with no sea within reach, or no way found, has the straight lane on
> its bearing (BoatSchedule.lane() with no sea at all).
> 
> AN OFFING PER BEARING (WN7; docs/studies/spec-nations.md 9). A route also says its EXITS: from its offing, for each compass bearing every
> BEARING_STEP degrees from north, the run of open sea in plots - the least of abyssRun() on the bearing and on the bearing SPREAD_DEG either side,
> so a leg turned by a call's spread keeps to the sea - or 0 where that is under EXIT_LEAST_M (exitsAt()). A boat bound for a nation leaves the
> offing on the open exit nearest the bearing of its node, not on the abyss's. Routes that share an offing (a joined route has its lane's) share
> its exits, worked out once; routes kept keep theirs; a straight lane has none.
> 
> SHARED LANES (SL1, 2026-10-28; Jerus: "if lots of ship lanes, its not
> different lanes, its same lane that later diverages if needed"; "late
> cities thats probably the thing that slows down stuff the most"). Every
> terminal used to find its own way from scratch - the 72 bearings, an A* of
> up to EXPANSIONS_MOST cells - and every berth laid found them all again, so
> a late city's routes were its slowest thing, and its map drew a lane for
> each terminal side by side. The berths are routed now in the order the
> ... (24 more lines in the source)

**Uses:** [BoatSchedule](BoatSchedule.md) (36), [World](World.md) (14), [SeaLanes](SeaLanes.md) (2)

**Used by (6):** [BoatSchedule](BoatSchedule.md), [CityMap](CityMap.md), [GatewayCheck](GatewayCheck.md), [Gateways](Gateways.md), [MapCheck](MapCheck.md), [PartnerCallsCheck](PartnerCallsCheck.md)

## Sections

| line | section |
|---:|---|
| 213 | THE GRID |
| 281 | A ROUTE |
| 628 | SHARED LANES (SL1, 2026-10-28) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 100 | `SeaRoutes.CELL` | `16` | The sea grid's cell, in plots: 16, 480 m (spec 4.1; the prototype's CG). |
| 103 | `SeaRoutes.SAMPLE` | `4` | A cell's ground is read at a sample every this many plots: 4, so 4 x 4 samples a cell. |
| 106 | `SeaRoutes.SALT_LEAST` | `12` | A cell is sea when at least this many of its 16 samples are salt: 12, three quarters (star O13-5; the prototype's 95% shut a 300 m narrows). |
| 109 | `SeaRoutes.SHORE_COST` | `3` | A shore cell's cost against an open one: 3 (spec 4.1: "three times the cost one cell from the shore"). |
| 112 | `SeaRoutes.OFFING_M` | `10_000` | How far past the city's radius the offing lies: 10 km (spec 4.1). |
| 115 | `SeaRoutes.BEARING_STEP` | `5` | The bearings tried for the offing, every this many degrees: 5 (spec 4.1). |
| 118 | `SeaRoutes.RAY_CELLS` | `400` | The longest run of sea a bearing is measured to, in cells: 400, 192 km (the prototype's). |
| 121 | `SeaRoutes.NEAR_CELLS` | `6` | How far round a berth its first sea cell is looked for, in cells: 6 (the prototype's nearest_sea()). |
| 124 | `SeaRoutes.ABYSS_M` | `3_000` | How far past the offing a boat sails into the abyss, fading: 3 km - "fading over a few km" (spec 4.1); less where land comes sooner (abyssRun()). |
| 127 | `SeaRoutes.SPREAD_DEG` | `3` | The most a call's last leg is turned either way, in degrees: 3 - "a few degrees" (spec 4.1). |
| 130 | `SeaRoutes.EXPANSIONS_MOST` | `250_000` | The most cells a route's A* looks at before it takes the straight lane: 250,000 - a way of about 1,000 km through open sea. |
| 133 | `SeaRoutes.JOIN_M` | `OFFING_M` | How far by water a terminal looks for a lane to join, in metres: OFFING_M, the offing's own margin (SL1, 2026-10-28). |
| 136 | `SeaRoutes.JOIN_CELLS` | `(int) Math.ceil(JOIN_M / World.PLOT_M / CELL)` | ...in cells of CELL plots, rounded up: 21 (no cell of a join's search is farther from the berth's start in a straight line). |
| 139 | `SeaRoutes.REFIND_M` | `OFFING_M / 2` | How much the city's radius may grow before the routes are found again, in metres: OFFING_M / 2, 5 km - they are found at the radius rounded up to a multiple of it (band(); SL1, 2026-10-28). |
| 142 | `SeaRoutes.EXITS` | `360 / BEARING_STEP` | How many exits a route has: one every BEARING_STEP degrees round the compass, 72 (WN7). |
| 145 | `SeaRoutes.EXIT_LEAST_M` | `ABYSS_M / 2` | The least run of open sea an exit has, in metres: ABYSS_M / 2, 1.5 km; a bearing whose sea (and its turned legs') ends sooner is closed, for a boat would fade within sight of the offing (WN7). |
| 148 | `SeaRoutes.BLOCK` | `64` | Cells a side of a block of the grid read at once: 64, 1,024 plots (a block a World.regionTerrain() call). |
| 435 | `SeaRoutes.OPEN_M` | `OFFING_M` | How far out a berth's water must reach for a terminal to stand on it, in metres: OFFING_M, the offing's own margin past the city (star O13-5). |
| 635 | `SeaRoutes.Router.SIDE` | `2 * JOIN_CELLS + 1` | A join's scratch window: JOIN_CELLS either way of the berth's start cell. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 219 | `final World world` |  |
| 221 | `final Sea mask` | A fixture's mask of the sea (SL1), or null: the world's ground is read. |
| 222 | `final Map<Long, boolean[]> blocks` |  |
| 224 | `final Map<Long, byte[]> opens` | Each block's cells' openness as worked out: 0 not yet, 1 the shore's, 2 open water. |
| 225 | `final byte[] buf` |  |
| 226 | `int read` |  |
| 637 | `final Grid g` |  |
| 638 | `final long siteX, siteY` |  |
| 639 | `final double radius` |  |
| 640 | `final int reach` |  |
| 641 | `final Lanes lanes` |  |
| 642 | `final int[] joinAt` |  |
| 643 | `int searched, joined` |  |
| 646 | `private final Map<List<Double>, double[]> exitsAt` | The exits worked out for each offing point so far (WN7): a joined route has its lane's offing, so it has its lane's exits, found once. |
| 649 | `private final double[] cost` | The window's cells: the cost reached, the cell it was reached from, and the join they were last reached in (a stamp, so nothing is cleared). |
| 650 | `private final int[] prev` |  |
| 651 | `private int gen` |  |
| 653 | `private double[] heapCost` | The cells reached and not yet looked at, a binary heap by cost, then by cell. |
| 654 | `private int[] heapAt` |  |
| 655 | `private int heapN` |  |
| 863 | `final List<BoatSchedule.Route> routes` | The routes taken in, in order (all of them, whether they are lanes or not). |
| 865 | `int count` | How many of them are lanes. |
| 867 | `private final Map<Long, long[]> cells` | Each cell's legs: route << 24 \| leg, in the order taken in. |
| 869 | `private final SeaLanes.Legs kept` | The legs indexed, each once (SeaLanes.Legs: a leg kept already, or lying on one that ends where it does, is not indexed again). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 95 | 852 | **type** `public final class SeaRoutes` | Each sea terminal's route out to the world: found once on a grid of the sea at 480 m, pulled straight, sailed from the terminal's quay out to the offing - past the city's ground, on the bearing with the most open sea ... |
| 97 | 1 | `private SeaRoutes()` |  |
| 151 | 3 | `public static List<BoatSchedule.Route> of(World world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius)` | Each terminal's route, in the berths' order, each against those before it (pure in the world, the berths, the site and the radius). |
| 161 | 1 | **type** `public record Found(List<BoatSchedule.Route> routes, int searched, int joined, int[] joinAt)` | The routes found, and how: how many berths were searched (route(): the 72 bearings and an A*, a straight lane where the sea gave no way) and how many joined a lane (join()), of the berths this call routed; for each of... |
| 164 | 3 | `public static Found find(World world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius)` | ...every berth's route, with how each was found. |
| 173 | 5 | `public static Found extend(World world, List<BoatSchedule.Route> kept, List<BoatSchedule.Berth> berths, long siteX, long siteY,...` | The routes of `berths` given those already found for its first berths (`kept`, the same routes of() or an earlier extend() made, for the same world, site and radius): the rest routed onto them, equal point for point t... |
| 180 | 3 | `static Found find(Grid g, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius)` | ...on a grid given (a harness's, from a mask of the sea). |
| 185 | 10 | `static Found extend(Grid g, List<BoatSchedule.Route> kept, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius)` | ...on a grid given. |
| 197 | 4 | `public static double band(double radius)` | The radius the routes are found at, in plots: `radius` rounded up to a multiple of REFIND_M, never below it (so the offing stays at least the radius plus OFFING_M out from the berth). |
| 204 | 3 | **type** `interface Sea` | The cells of the sea as a fixture says them (a harness's way to cause each condition): true where the cell is sea. |
| 205 | 1 | `boolean at(long ci, long cj)` _(in SeaRoutes.Sea)_ |  |
| 209 | 3 | `static Grid grid(Sea sea)` | A grid over a mask of the sea: what `sea` answers, nothing read from the world (off the mask is whatever it answers). |

### THE GRID (lines 213-280)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 62 | **type** `static final class Grid` | The sea's cells, read a block at a time as asked. |
| 228 | 1 | `Grid(World world)` _(in SeaRoutes.Grid)_ |  |
| 230 | 1 | `Grid(Sea mask)` _(in SeaRoutes.Grid)_ |  |
| 233 | 22 | `boolean sea(long ci, long cj)` _(in SeaRoutes.Grid)_ | Whether cell (ci, cj) is sea: at least SALT_LEAST of its samples salt; off the world, sea. |
| 257 | 12 | `boolean open(long ci, long cj)` _(in SeaRoutes.Grid)_ | Whether a sea cell is open water: every cell about it sea too; else it is the shore's. |
| 271 | 8 | `boolean clear(double ai, double aj, double bi, double bj)` _(in SeaRoutes.Grid)_ | Whether the straight line between two cells' middles crosses only sea cells, looked at three times a cell along it (the prototype's clear()). |

### A ROUTE (lines 281-627)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 4 | `static int reachOf(double radius)` | How far out the offing lies from a berth's start cell for a city of this radius, in cells: the radius plus OFFING_M, at least 1, at most RAY_CELLS. |
| 292 | 3 | `static BoatSchedule.Route route(Grid g, BoatSchedule.Berth b, long siteX, long siteY, double radius)` | A berth's route, searched (SeaRoutes' rule). |
| 297 | 74 | `static BoatSchedule.Route route(Grid g, BoatSchedule.Berth b, long[] start, long siteX, long siteY, double radius)` | ...from its start cell, which nearestSea() found (null for none: the straight lane). |
| 382 | 51 | `static List<long[]> outOfTheBay(Grid g, long[] start, int reach, double away)` | The way out of a bay (star O13-5): Dijkstra over the sea's cells from `start` (eight ways, a shore cell SHORE_COST times an open one), no cell farther than `reach` cells from it in a straight line, at most EXPANSIONS_... |
| 444 | 26 | `public static boolean opensToSea(Grid g, long x, long y, java.util.Set<Long> shut)` | Whether the water at world plot (x, y) opens to the sea (star O13-5): from its cell, or the nearest sea cell within NEAR_CELLS, the sea's cells reach OPEN_M away in a straight line, eight ways (a breadth-first flood, ... |
| 477 | 5 | `static double abyssRun(Grid g, double x, double y, double ux, double uy)` | How far past the offing (x, y) the abyss runs on along (ux, uy), in plots: ABYSS_M, or the longest of its lengths a third of a cell shorter at a time whose leg keeps to the sea's cells (legOnSea()) - a third of a cell... |
| 489 | 14 | `static double[] exitsAt(Grid g, double x, double y)` | The exits of the offing at (x, y) (WN7): for each compass bearing every BEARING_STEP degrees from north (the plane's BoatSchedule.compassX() and compassY()), the run of open sea out on it in plots - the least of abyss... |
| 505 | 8 | `static boolean legOnSea(Grid g, double ax, double ay, double bx, double by)` | Whether the straight leg from (ax, ay) to (bx, by) keeps to the sea's cells, looked at three times a cell along it, both ends too. |
| 515 | 13 | `static long[] nearestSea(Grid g, long ci, long cj)` | The berth's own cell when it is sea, else the nearest sea cell within NEAR_CELLS (the nearer, then the first in rows), or null. |
| 530 | 12 | `static int run(Grid g, long[] from, int deg)` | The run of sea cells out from a cell on a bearing, in cells, up to RAY_CELLS; 1,000 more when it leaves the world at sea (the prototype's open water). |
| 544 | 41 | `static List<long[]> astar(Grid g, long[] a, long[] b)` | The cheapest way over the sea's cells, eight ways, a shore cell SHORE_COST times an open one, Euclidean ahead (the prototype's astar()); null when none, or past EXPANSIONS_MOST. |
| 586 | 1 | `private static long key(long i, long j)` |  |
| 589 | 12 | `static List<long[]> pull(Grid g, List<long[]> path)` | String pulling (the prototype's pull()): from each point kept, on to the farthest point the straight line reaches over sea alone. |
| 609 | 18 | `static boolean tailClear(Grid g, double[] xs, double[] ys, int pivot, double turn)` | Whether the route's last leg and abyss, turned by `turn` radians about the pivot, keep to sea cells: looked at three times a cell along them, past the pivot's own cell and the one beside it (the pivot is the berth whe... |

### SHARED LANES (SL1, 2026-10-28) (lines 628-946)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 221 | **type** `private static final class Router` | The routes of one call, in the berths' order: each joins a lane of those before it (join()) or is searched (route()). |
| 657 | 9 | `Router(Grid g, long siteX, long siteY, double radius, int berths)` _(in SeaRoutes.Router)_ |  |
| 668 | 4 | `void keep(BoatSchedule.Route r)` _(in SeaRoutes.Router)_ | A route found already, for the next berth: it is a lane for those after, and its exits are those of its offing. |
| 673 | 3 | `private static List<Double> offingOf(BoatSchedule.Route r)` _(in SeaRoutes.Router)_ |  |
| 678 | 5 | `private BoatSchedule.Route withExits(BoatSchedule.Route r)` _(in SeaRoutes.Router)_ | The route with the exits of its offing (WN7), made once for each offing point; a straight lane has none. |
| 685 | 12 | `void next(BoatSchedule.Berth b)` _(in SeaRoutes.Router)_ | The next berth's route: joined to a lane when one is in reach, else searched. |
| 704 | 44 | `private BoatSchedule.Route join(int at, BoatSchedule.Berth b, long[] start)` _(in SeaRoutes.Router)_ | The berth's route by a lane (SL1): a Dijkstra over the sea's cells from `start` (eight ways, a shore cell SHORE_COST times an open one, as outOfTheBay() costs them), no cell farther than JOIN_CELLS from it in a straig... |
| 755 | 52 | `private BoatSchedule.Route assemble(int at, BoatSchedule.Berth b, long[] start, List<long[]> path, int[] cover)` _(in SeaRoutes.Router)_ | The joined route: the berth, the way to the covering cell pulled straight (pull(), with route()'s rule for the berth's own cell and its FR3 check of the berth's first leg), the join point, then the lane's own points a... |
| 808 | 3 | `private boolean before(int a, int b)` _(in SeaRoutes.Router)_ |  |
| 812 | 15 | `private void push(double c, int at)` _(in SeaRoutes.Router)_ |  |
| 829 | 15 | `private void pop()` _(in SeaRoutes.Router)_ | Takes the heap's first away. |
| 845 | 8 | `private void swap(int a, int b)` _(in SeaRoutes.Router)_ |  |
| 861 | 64 | **type** `private static final class Lanes` | The lanes a berth may join: the legs of the routes found by a search or joined (a straight lane, two points, is never one), each from its berth up to its offing and never the abyss's, indexed by the sea cell each pass... |
| 872 | 27 | `void add(BoatSchedule.Route r)` _(in SeaRoutes.Lanes)_ | The next route taken in: indexed when it is a lane. |
| 901 | 3 | `static int samples(double ax, double ay, double bx, double by)` _(in SeaRoutes.Lanes)_ | The samples along a leg, three a cell as legOnSea() takes them (the last index; there is one more). |
| 905 | 3 | `static long cellOf(double v)` _(in SeaRoutes.Lanes)_ |  |
| 913 | 11 | `int[] covering(long ci, long cj, long[] start, int reach)` _(in SeaRoutes.Lanes)_ | The lane covering cell (ci, cj) that a berth whose start cell is `start` may join, as {route, leg}, or null: the first in the order taken in whose offing lies at least (reach - 1) cells from the start. |
| 931 | 15 | `private static double[] joinPoint(Grid g, BoatSchedule.Route q, int leg, long ci, long cj)` | Where a berth's way meets leg `leg` of lane q, which covers cell (ci, cj): the point on the leg nearest the cell's middle; or, where the straight way from the middle to it would leave the sea (the point lies in the ne... |

