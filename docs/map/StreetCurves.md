# StreetCurves.java - 152 lines · 8 methods · 4 constants · model

`ham/citybuildersim/StreetCurves.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> A street's smooth line from the plots it is laid on, and the plots that line's band reaches: pure - plots in, points in plots out, no plan and no screen.
> 
> WHY THIS EXISTS (batch CL4, slice C; Jerus, 2026-10-20: "having some slanted roads and curves"; the study spec-city-layout 5.2).
> A street the ground cuts a cell along (DistrictPlan, WHERE THE GROUND CUTS A CELL: the plots of ROLE_SHORE that join a dead end to
> its neighbour) is a path of plots that follows the cut, and drawn plot by plot - a bar across each - it is a staircase of crosses.
> FX5 rounded the railway's corners as one geometry that the routes and the tiles both read (TrackCurves); the shore's streets are
> given the same: the middles of the path's plots are simplified (Douglas-Peucker, FIT), the corners of what is left rounded with
> TrackCurves at CURVE_RADIUS, and the plan and the painter both call this class, so the plot a building may not stand on (the band
> the line draws reaches it) is the plot the painter draws the band across, the one geometry read by both - FX3's rule, again.
> 
> COORDINATES. A frame's plot (x, y) is the square [x, x + 1] by [y, y + 1] in plots and its middle (x + 0.5, y + 0.5), as
> TrackCurves has it; a plot's index in a frame of side `frame` is y * frame + x.

**Uses:** [TrackCurves](TrackCurves.md) (1)

**Used by (4):** [DistrictPlan](DistrictPlan.md), [MapCheck](MapCheck.md), [PlanCheck](PlanCheck.md), [TileRaster](TileRaster.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 24 | `StreetCurves.CURVE_RADIUS` | `2` | The radius the corners of a street along a cut are rounded with, in plots: 2 (60 m) - the railway's is 3 (TrackCurves.RADIUS), a street's turn at the near view is tighter; where a leg is shorter than twice the tangent... |
| 27 | `StreetCurves.FIT` | `0.75` | How far the simplified line of a path may lie from the middles of its plots, in plots: three quarters (Douglas-Peucker) - more than a 45-degree staircase's corners lie from the line through its steps (sqrt 2 / 2, 0.71... |
| 30 | `StreetCurves.BAND_HALF` | `0.5` | The half width of the band a street's curve is drawn with, in plots: a half, the widest surface a street has (DistrictPlan.FULL, a whole plot) - a plot whose square lies nearer the line than this is one the band reach... |
| 33 | `StreetCurves.EDGE` | `1e-9` | The margin a distance must be inside BAND_HALF to count, so a plot beside a line along a plot's middles (exactly BAND_HALF away) is not reached, whatever the last bit of the arithmetic says. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 19 | 134 | **type** `public final class StreetCurves` | A street's smooth line from the plots it is laid on, and the plots that line's band reaches: pure - plots in, points in plots out, no plan and no screen. |
| 21 | 1 | `private StreetCurves()` |  |
| 40 | 41 | `public static double[][] fit(int[] plots, int n, int frame)` | The simplified polyline through the middles of the first n plots of `plots` (frame indices of side `frame`), in order: {xs, ys}. |
| 83 | 3 | `public static double[][] line(double[] vx, double[] vy)` | The line of a simplified path (fit()): its corners rounded with TrackCurves at CURVE_RADIUS, the arcs sampled every TrackCurves.STEP of their length: {xs, ys}, from the first vertex to the last. |
| 92 | 21 | `public static int[] reached(double[] xs, double[] ys, int frame)` | The plots the band of a line reaches, sorted, each once, as indices in a frame of side `frame` (plots off the frame are left out): those whose square lies nearer the polyline (xs, ys) than BAND_HALF. |
| 115 | 6 | `static double segmentDistance(double px, double py, double ax, double ay, double bx, double by)` | The distance from point (px, py) to the segment (ax, ay)-(bx, by). |
| 123 | 4 | `static double pointSquare(double px, double py, int x, int y)` | The distance from point (px, py) to the closed square of plot (x, y): 0 inside it. |
| 129 | 6 | `static double squareDistance(double ax, double ay, double bx, double by, int x, int y)` | The distance from segment (ax, ay)-(bx, by) to the closed square of plot (x, y): 0 where they meet, else the least of the ends' distances to the square and the square's corners' to the segment (both convex, so one of ... |
| 137 | 15 | `static boolean meets(double ax, double ay, double bx, double by, int x, int y)` | Whether the segment (ax, ay)-(bx, by) meets the closed square of plot (x, y) (Liang-Barsky). |

