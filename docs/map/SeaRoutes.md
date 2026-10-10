# SeaRoutes.java - 473 lines · 17 methods · 13 constants · model

`ham/citybuildersim/SeaRoutes.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [BoatSchedule](BoatSchedule.md) (9), [World](World.md) (9)

**Used by (2):** [CityMap](CityMap.md), [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 106 | THE GRID |
| 169 | A ROUTE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 62 | `SeaRoutes.CELL` | `16` | The sea grid's cell, in plots: 16, 480 m (spec 4.1; the prototype's CG). |
| 65 | `SeaRoutes.SAMPLE` | `4` | A cell's ground is read at a sample every this many plots: 4, so 4 x 4 samples a cell. |
| 68 | `SeaRoutes.SALT_LEAST` | `12` | A cell is sea when at least this many of its 16 samples are salt: 12, three quarters (star O13-5; the prototype's 95% shut a 300 m narrows). |
| 71 | `SeaRoutes.SHORE_COST` | `3` | A shore cell's cost against an open one: 3 (spec 4.1: "three times the cost one cell from the shore"). |
| 74 | `SeaRoutes.OFFING_M` | `10_000` | How far past the city's radius the offing lies: 10 km (spec 4.1). |
| 77 | `SeaRoutes.BEARING_STEP` | `5` | The bearings tried for the offing, every this many degrees: 5 (spec 4.1). |
| 80 | `SeaRoutes.RAY_CELLS` | `400` | The longest run of sea a bearing is measured to, in cells: 400, 192 km (the prototype's). |
| 83 | `SeaRoutes.NEAR_CELLS` | `6` | How far round a berth its first sea cell is looked for, in cells: 6 (the prototype's nearest_sea()). |
| 86 | `SeaRoutes.ABYSS_M` | `3_000` | How far past the offing a boat sails into the abyss, fading: 3 km - "fading over a few km" (spec 4.1); less where land comes sooner (abyssRun()). |
| 89 | `SeaRoutes.SPREAD_DEG` | `3` | The most a call's last leg is turned either way, in degrees: 3 - "a few degrees" (spec 4.1). |
| 92 | `SeaRoutes.EXPANSIONS_MOST` | `250_000` | The most cells a route's A* looks at before it takes the straight lane: 250,000 - a way of about 1,000 km through open sea. |
| 95 | `SeaRoutes.BLOCK` | `64` | Cells a side of a block of the grid read at once: 64, 1,024 plots (a block a World.regionTerrain() call). |
| 302 | `SeaRoutes.OPEN_M` | `OFFING_M` | How far out a berth's water must reach for a terminal to stand on it, in metres: OFFING_M, the offing's own margin past the city (star O13-5). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 112 | `final World world` |  |
| 113 | `final Map<Long, boolean[]> blocks` |  |
| 115 | `final Map<Long, byte[]> opens` | Each block's cells' openness as worked out: 0 not yet, 1 the shore's, 2 open water. |
| 116 | `final byte[] buf` |  |
| 117 | `int read` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 417 | **type** `public final class SeaRoutes` | Each sea terminal's route out to the world: found once on a grid of the sea at 480 m, pulled straight, sailed from the terminal's quay out to the offing - past the city's ground, on the bearing with the most open sea ... |
| 59 | 1 | `private SeaRoutes()` |  |
| 98 | 7 | `public static List<BoatSchedule.Route> of(World world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius)` | Each terminal's route, in the berths' order (pure in the world, the berths, the site and the radius). |

### THE GRID (lines 106-168)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 57 | **type** `static final class Grid` | The sea's cells, read a block at a time as asked. |
| 119 | 1 | `Grid(World world)` _(in SeaRoutes.Grid)_ |  |
| 122 | 21 | `boolean sea(long ci, long cj)` _(in SeaRoutes.Grid)_ | Whether cell (ci, cj) is sea: every sample of it salt; off the world, sea. |
| 145 | 12 | `boolean open(long ci, long cj)` _(in SeaRoutes.Grid)_ | Whether a sea cell is open water: every cell about it sea too; else it is the shore's. |
| 159 | 8 | `boolean clear(double ai, double aj, double bi, double bj)` _(in SeaRoutes.Grid)_ | Whether the straight line between two cells' middles crosses only sea cells, looked at three times a cell along it (the prototype's clear()). |

### A ROUTE (lines 169-473)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 64 | `static BoatSchedule.Route route(Grid g, BoatSchedule.Berth b, long siteX, long siteY, double radius)` | A berth's route (SeaRoutes' rule). |
| 249 | 51 | `static List<long[]> outOfTheBay(Grid g, long[] start, int reach, double away)` | The way out of a bay (star O13-5): Dijkstra over the sea's cells from `start` (eight ways, a shore cell SHORE_COST times an open one), no cell farther than `reach` cells from it in a straight line, at most EXPANSIONS_... |
| 311 | 26 | `public static boolean opensToSea(Grid g, long x, long y, java.util.Set<Long> shut)` | Whether the water at world plot (x, y) opens to the sea (star O13-5): from its cell, or the nearest sea cell within NEAR_CELLS, the sea's cells reach OPEN_M away in a straight line, eight ways (a breadth-first flood, ... |
| 344 | 5 | `static double abyssRun(Grid g, double x, double y, double ux, double uy)` | How far past the offing (x, y) the abyss runs on along (ux, uy), in plots: ABYSS_M, or the longest of its lengths a third of a cell shorter at a time whose leg keeps to the sea's cells (legOnSea()) - a third of a cell... |
| 351 | 8 | `static boolean legOnSea(Grid g, double ax, double ay, double bx, double by)` | Whether the straight leg from (ax, ay) to (bx, by) keeps to the sea's cells, looked at three times a cell along it, both ends too. |
| 361 | 13 | `static long[] nearestSea(Grid g, long ci, long cj)` | The berth's own cell when it is sea, else the nearest sea cell within NEAR_CELLS (the nearer, then the first in rows), or null. |
| 376 | 12 | `static int run(Grid g, long[] from, int deg)` | The run of sea cells out from a cell on a bearing, in cells, up to RAY_CELLS; 1,000 more when it leaves the world at sea (the prototype's open water). |
| 390 | 41 | `static List<long[]> astar(Grid g, long[] a, long[] b)` | The cheapest way over the sea's cells, eight ways, a shore cell SHORE_COST times an open one, Euclidean ahead (the prototype's astar()); null when none, or past EXPANSIONS_MOST. |
| 432 | 1 | `private static long key(long i, long j)` |  |
| 435 | 12 | `static List<long[]> pull(Grid g, List<long[]> path)` | String pulling (the prototype's pull()): from each point kept, on to the farthest point the straight line reaches over sea alone. |
| 455 | 18 | `static boolean tailClear(Grid g, double[] xs, double[] ys, int pivot, double turn)` | Whether the route's last leg and abyss, turned by `turn` radians about the pivot, keep to sea cells: looked at three times a cell along them, past the pivot's own cell and the one beside it (the pivot is the berth whe... |

