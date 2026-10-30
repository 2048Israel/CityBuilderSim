# SeaLanes.java - 210 lines · 15 methods · 1 constants · model

`ham/citybuildersim/SeaLanes.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The sea lanes a map draws: every leg of every terminal's route once - a leg that many routes share, once - indexed by the district cells it reaches, so a frame asks for the legs on its screen and touches only those; pure, kept by the map with its routes and never saved.
> 
> WHY THIS EXISTS (SL1, 2026-10-28; Jerus: "if lots of ship lanes, its not different lanes, its same lane that later
> diverages if needed"). MapView.boats() looped over every route every frame, tested each against the screen and stroked
> its whole polyline twice, so a late city's frame paid for every terminal, though SeaRoutes now has most of its
> terminals share a lane and a screen shows few of them. The routes stay one to a berth (BoatSchedule's boats ride
> them, one route to count a call against); what is drawn is the lanes, a leg a hundred routes share stroked once, and
> found the way BoatSchedule finds its routes' boats: by the district cell. A leg's box is its two points', as
> BoatSchedule.legBoxes() takes it with no spread (the boats' swing past the pivot is theirs, not the lane's). A query
> keeps a stamp per leg and is for one thread at a time, the screen's.

**Uses:** [World](World.md) (8), [BoatSchedule](BoatSchedule.md) (2)

**Used by (4):** [CityMap](CityMap.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [SeaRoutes](SeaRoutes.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `SeaLanes.Legs.ON_LINE` | `1e-6` | How far off a kept leg's line, in plots, a leg's start may lie and be on it: a join point is worked out in doubles. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `double[] pts` | The legs kept, four doubles each: x0 y0 x1 y1. |
| 34 | `int count` |  |
| 36 | `private final Map<Long, End> ends` | The starts of the legs kept, by the point they end at (its two coordinates' bits mixed). |
| 39 | `final double x, y` |  |
| 40 | `double[] from` |  |
| 41 | `int n` |  |
| 83 | `private final double[] legs` | The legs in the order met (the routes' order, each route's own order): four doubles each, x0 y0 x1 y1. |
| 84 | `private final int count` |  |
| 86 | `private final int routeLegs` | How many legs the routes had between them, a shared one every time. |
| 88 | `private final Map<Long, int[]> index` | Each district cell's legs, those whose box reaches it. |
| 90 | `private final int[] seenAt` | The frame a leg was last looked at in, for a query to meet a leg once, and the frames asked. |
| 91 | `private int frames` |  |
| 92 | `private int touched` |  |
| 127 | `int[] a` |  |
| 128 | `int n` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 191 | **type** `public final class SeaLanes` | The sea lanes a map draws: every leg of every terminal's route once - a leg that many routes share, once - indexed by the district cells it reaches, so a frame asks for the legs on its screen and touches only those; p... |
| 28 | 53 | **type** `static final class Legs` | Legs taken in one at a time, each kept once: a leg is not kept when the same one is kept already, nor when it lies on one kept that ends at the same point (a route that joins a lane at a point of its leg goes on along... |
| 38 | 13 | **type** `private static final class End` _(in SeaLanes.Legs)_ |  |
| 43 | 1 | `End(double x, double y)` _(in SeaLanes.Legs.End)_ |  |
| 45 | 5 | `void add(double fx, double fy)` _(in SeaLanes.Legs.End)_ |  |
| 53 | 19 | `boolean add(double ax, double ay, double bx, double by)` _(in SeaLanes.Legs)_ | Takes the leg (ax, ay) to (bx, by) in; false when it is kept already or lies on a kept one ending at (bx, by). |
| 74 | 6 | `private static boolean lies(double cx, double cy, double bx, double by, double px, double py)` _(in SeaLanes.Legs)_ | Whether (px, py) lies on the segment (cx, cy) to (bx, by), its two ends too, to within ON_LINE. |
| 94 | 7 | `private SeaLanes(double[] legs, int count, int routeLegs, Map<Long, int[]> index)` |  |
| 103 | 21 | `public static SeaLanes of(List<BoatSchedule.Route> routes)` | The lanes of these routes: each of their legs once, the abyss's too. |
| 126 | 9 | **type** `private static final class Bin` | A cell's legs while they are gathered. |
| 130 | 4 | `void add(int v)` _(in SeaLanes.Bin)_ |  |
| 136 | 3 | `private static long key(long cx, long cy)` |  |
| 141 | 1 | `public int legs()` | How many legs there are, each once. |
| 144 | 1 | `public int routeLegs()` | How many legs the routes had between them: each route's, a leg shared by many counted for every one. |
| 147 | 1 | `public int touched()` | How many legs the last crossing() looked at (those of the district cells it covers, each once). |
| 150 | 3 | `public double[] leg(int i)` | Leg i's points, {x0, y0, x1, y1} in plots. |
| 159 | 41 | `public double[] crossing(long x0, long y0, long x1, long y1)` | The legs whose box meets the rectangle [x0, x1) x [y0, y1) in plots, as the points of each in turn, x0 y0 x1 y1 (four doubles a leg, in the order they were met): the district cells the rectangle covers asked, and only... |
| 202 | 4 | `private boolean meets(int l, long x0, long y0, long x1, long y1)` | Whether leg l's box (inclusive) meets the rectangle, as BoatSchedule.crosses() takes a leg's. |
| 207 | 3 | `public String toString()` |  |

