# TrackCurves.java - 346 lines · 26 methods · 7 constants · model

`ham/citybuildersim/TrackCurves.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

> The railway's curves as one geometry, for the drawing and for the trains'
> routes alike (FX5, 0.8.18): a polyline's corners rounded with arcs of a
> radius, the arcs the tiles draw and the routes run along, and the yard's
> ladder as an S that leaves its line and meets its siding tangent at both
> ends. Pure: points in plots in, points in plots out, no map and no screen.
> 
> WHY THIS EXISTS. Until FX5 the track was drawn plot by plot, so a corner was
> an L and a 45-degree turn a notch, and the routes were polylines through
> plot middles, so each car of a train swung 90 degrees one after another at a
> corner (Jerus, 2026-10-20: "do trains make 90 degree turns?"); the yard's
> ladders were one straight pixel line from the line to the siding, 60 degrees
> off it for the outer sidings, and the route ran along the same straight. The
> drawing and the routes each had their own idea of where a corner was. They
> now read this one: CityMap rounds each route with round() and lays each
> ladder with ladder(), the tiles draw the same bends() and the same
> ladder(), so a car is on the rail it is drawn on (FX3's rule, one layout
> read by both, for the curves).
> 
> A bend carries the net it belongs to (RAILWAY, 0): the highways' 45-degree
> joints (FX5 slice D) are bends of the other net, drawn by the same arcs.

**Used by (9):** [CityMap](CityMap.md), [GatewayCheck](GatewayCheck.md), [JunctionCheck](JunctionCheck.md), [MapCheck](MapCheck.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [StreetCurves](StreetCurves.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `TrackCurves.RADIUS` | `3` | A curve's radius on the line, in plots: 3 (90 m), a few plots at the near view (Jerus, 2026-10-20); real freight curves are 150 to 300 m, wider than a corridor's corner here. |
| 36 | `TrackCurves.STEP` | `0.25` | The routes' sampling along an arc or a ladder, in plots: a quarter, as MapCheck reads the trains' routes. |
| 39 | `TrackCurves.RAILWAY` | `0, HIGHWAY = 1` | A bend's net: the railway's 0 and (FX5 slice D) the highway's 1 - its own numbering, not CityRuns's (whose HIGHWAYS is 0 and RAILWAY 1). |
| 42 | `TrackCurves.HIGHWAY_RADIUS` | `4` | A highway bend's radius, in plots: 4 - a carriageway is broader than a railway's bed, and a 45-degree joint's tangent is then 1.66 plots (FX5 slice D: "a plot and a half"), so the arc leaves the straight a plot before... |
| 45 | `TrackCurves.STRAIGHT` | `1e-6` | The turns of a polyline smaller than this, in radians, are straight: a millionth. |
| 175 | `TrackCurves.DX` | `{ 0, 1, 0, - 1 }, DY = { - 1, 0, 1, 0 }` | Plots beside one another: north, east, south, west, as TilePainter has them. |
| 213 | `TrackCurves.ON_PATH` | `0.75` | How far a plot's middle may lie from a leg's line, in plots, and still be a plot of the highway on it: three quarters - a staircase's corner plot lies 0.71 from its stretch's line. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 28 | 319 | **type** `public final class TrackCurves` | The railway's curves as one geometry, for the drawing and for the trains' routes alike (FX5, 0.8.18): a polyline's corners rounded with arcs of a radius, the arcs the tiles draw and the routes run along, and the yard'... |
| 30 | 1 | `private TrackCurves()` |  |
| 57 | 32 | **type** `public record Bend(int net, double x, double y, double ux, double uy, double vx, double vy, double r, doubl...` | One turn of a polyline: its corner point (x, y), the unit headings into and out of it, the radius it is rounded with (the radius asked, less where the legs beside it are short), the tangent length along each leg from ... |
| 61 | 7 | `public static Bend of(int net, double x, double y, double ux, double uy, double vx, double vy, double r, double t)` _(in TrackCurves.Bend)_ | The bend at corner (x, y) between headings u and v (unit) with the arc's radius r and tangent length t (the radius and the length agree: t = r * tan(turn / 2)); its circle worked out. |
| 70 | 1 | `public double startX()` _(in TrackCurves.Bend)_ | Where the arc starts, on the incoming leg. |
| 71 | 1 | `public double startY()` _(in TrackCurves.Bend)_ |  |
| 74 | 1 | `public double endX()` _(in TrackCurves.Bend)_ | ...and where it ends, on the outgoing leg. |
| 75 | 1 | `public double endY()` _(in TrackCurves.Bend)_ |  |
| 78 | 1 | `public double length()` _(in TrackCurves.Bend)_ | The arc's length, in plots. |
| 81 | 6 | `public double[] at(double s)` _(in TrackCurves.Bend)_ | The point on the arc a distance s along it from its start, and the heading there: {x, y, heading in radians}. |
| 98 | 3 | `public static List<Bend> bends(double[] xs, double[] ys, double r)` | The bends of a polyline (points xs[i], ys[i]) rounded with radius r: each interior point where the heading turns by more than a millionth of a radian and by less than a half turn is a bend. |
| 103 | 8 | `public static List<Bend> bends(double[] xs, double[] ys, double r, int net)` | ...a bend of net `net` (RAILWAY or HIGHWAY). |
| 113 | 16 | `private static Bend bendAt(double[] xs, double[] ys, int i, double r, int net)` | The bend at interior point i, or null where the polyline runs on straight there (or doubles back). |
| 136 | 23 | `public static double[][] round(double[] xs, double[] ys, double r)` | The polyline with each bend's corner replaced by its arc: {xs, ys}. |
| 161 | 7 | `private static void add(List<double[]> pts, double x, double y)` | Adds the point unless the last one is there (no leg of no length). |
| 170 | 3 | **type** `public interface Rail` | Whether plot (x, y) holds track: how a bend asks the map, or a fixture, about the plots round it. |
| 171 | 1 | `boolean at(int x, int y)` _(in TrackCurves.Rail)_ |  |
| 178 | 5 | `private static int dir(double ux, double uy)` | The direction (0 north, 1 east, 2 south, 3 west) of a unit axis heading, or -1 where it is not one. |
| 194 | 17 | `public static List<int[]> replaced(Bend b, Rail rail)` | The plots a bend replaces, each {x, y}: those whose middle lies on its legs strictly inside the tangent points - the corner's plot among them - and whose only track is the bend's own two legs, exactly two arms of trac... |
| 227 | 16 | `public static List<int[]> replacedHighway(Bend b, Rail hw)` | The plots a highway bend replaces (FX5 slice D), each {x, y}: the plots of the highway (hw.at()) whose middles lie within the tangent length of the corner - a plot and a half out along each leg of a 45-degree joint - ... |
| 245 | 3 | `private static boolean within(Bend b, int x, int y)` | Whether plot (x, y)'s middle lies strictly within the bend's tangent length of its corner. |
| 250 | 5 | `private static boolean onPath(Bend b, double px, double py)` | Whether the point (px, py), in plots, lies within ON_PATH of the line of the incoming leg (not far past the corner) or of the outgoing one. |
| 257 | 4 | `public static boolean onGrid(Bend b)` | Whether the bend is on the plot grid: its corner on a plot's middle and both its legs along an axis - a corner of straight track, as replaced() reads. |
| 269 | 5 | `public static double stub(Bend b)` | How far past each tangent point, back along its leg, the last plot the bend may replace reaches, in plots: the straight the tiles draw from the tangent point out to that plot's far edge, so nothing is left between the... |
| 276 | 5 | `private static void addIf(List<int[]> out, Rail rail, int x, int y, int mask)` | Adds plot (x, y) to out if its track beside it is exactly the arms in `mask`. |
| 283 | 4 | `public static double smoothstep(double s)` | The smooth step 3s^2 - 2s^3: 0 at 0 and 1 at 1, level at both. |
| 299 | 4 | `public static double[][] ladder(double ax, double ay, double ux, double uy, double nx, double ny, double along, double off)` | A yard's ladder from the fan's apex (ax, ay), on the line heading (ux, uy), to its siding's start `along` plots on and `off` plots across (signed, on the unit normal (nx, ny)): an S tangent to the line at the apex and... |
| 305 | 20 | `public static double[][] ladder(double ax, double ay, double ux, double uy, double nx, double ny, double along, double off, dou...` | ...sampled every `step` of its length instead, a straight one too: the tiles draw it a pixel at a time. |
| 334 | 12 | `public static double arcDistance(Bend b, double px, double py, double[] along)` | The distance from point (px, py) to the bend's arc, where the point lies within the arc's sweep (seen from its centre) - \|distance from the centre - r\| - else Double.POSITIVE_INFINITY: the pixel helper the raster draw... |

