# JunctionCheck.java - 2,164 lines · 87 methods · 8 constants · harnesses

`ham/citybuildersim/JunctionCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> Proves that the junctions and interchanges of spec-freight 4.6 are drawn
> by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19).
> Not part of the game.
> 
> WHY THIS EXISTS. Until 0.8.1 a + was two street bars overlapping, a ramp
> was one diagonal stroke across a verge plot's corner, and a highway
> crossing a highway was a plain overlap. This draws the table's first five
> rows at the 8-px raster: signals, roundabouts, diamonds, an arm's end and
> a stack, each from hand-built painted tiles.
> 
> What it has to prove, a section each, every one a fixture that sets the
> condition by hand:
> 
>   1. an arterial crossing an arterial has JUNCTION_BOX pixels on the
>      junction plot and ZEBRA stripes and stop lines on its four arms; a
>      plain street crossing keeps the paved colour;
>   2. a boulevard crossing a boulevard has JUNCTION_BOX pixels at its middle
>      and ZEBRA stripes on its four neighbouring street plots, no ISLAND
>      pixel, and none of the ring on a building placed diagonally beside it
>      (FX5, Jerus, 2026-10-20, "Mostly signals": until then a ring - its
>      assertions are 17c's now);
>   3. a highway with M_RAMP where an arterial passes beneath has paved
>      slip pixels on the verge rows above and below, both sides of the
>      street, SLIP_LENGTH plots out, and none where a building stands on
>      the verge row;
>   4. an arm's last plot with M_RAMP and no street beneath rasters
>      differently from the same tile without the mark, and at its open end
>      the band is no wider than the paved road's width;
>   5. two highways crossing have HIGHWAY_CASE pixels across the column's
>      highway and slip pixels in the highway's colour in the four corner
>      plots, but none in a corner plot holding a building;
>   6. at 4 px, below JUNCTIONS_FROM, none of these fixtures has a
>      JUNCTION_BOX, ZEBRA or ISLAND pixel;
>   7. a roundabout whose centre is a tile's last column is read through
>      Input.beyond and Input.ring by the next tile, which draws the island's
>      and the ring's pixels on its first column (FX5: the five-road roundabout
>      of 17c, since a boulevard crossing a boulevard is a signal now).
>   8. a diamond whose street is a tile's first row (an arterial lies on a
>      cell's ring, so most do): the tile holding the ramp draws its side's
>      slips, and the tile across the edge draws the other side's from the
>      ramp it reads beyond its edge;
>   9. a ramp in the middle of a highway with no street beneath (the plan
>      laid none on the arterial there) is not an arm's end: at 8 px and at
>      4 px the tile rasters exactly as the same tile without the mark,
>      along a row and down a column; and an arm's last plot still narrows
>      (section 4) - Jerus's eye check of 0.8.2 (FX2).
>  10. (batch FX3, red until the fix) a 45-degree highway stretch laid as a
>      staircase rasters as one carriageway along its line: no deck on a step
>      and no slip beside it, nothing highway-coloured off the carriageway,
>      every pixel inside it road or centre line, and a centre line on the
>      steps;
>  11. (batch FX3, red until the fix) a road two plots wide is one road: two
>      arterials side by side have a zebra crossing and a junction box only
>      where a street crosses them, two boulevards side by side likewise (FX5:
>      a signal where one crosses them, no longer a roundabout); and three
>      boulevards side by side, as his city's are across a cell line.
>  12. (batch FR7, slice 4: FR0-L's D19) THE INTERCHANGES' GROUND: in a city
>      of highways laid over owned dry ground the spine's hub, where four arms
>      cross, is a cloverleaf site with a CLOVERLEAF_SIDE reserve centred on
>      it, and an arm laid to end beside the spine a trumpet site with a
> ... (69 more lines in the source)

**Uses:** [TileRaster](TileRaster.md) (184), [DistrictPlan](DistrictPlan.md) (118), [TilePainter](TilePainter.md) (107), [BuildingVisual](BuildingVisual.md) (58), [CityRuns](CityRuns.md) (56), [InterchangeGround](InterchangeGround.md) (24), [TrackCurves](TrackCurves.md) (9), [PlanCheck](PlanCheck.md) (5), [World](World.md) (2), [BuildingCatalog](BuildingCatalog.md) (1), [MainLineCheck](MainLineCheck.md) (1), [Founding](Founding.md) (1), [BuildingType](BuildingType.md) (1), [MapTiles](MapTiles.md) (1), [MapCheck](MapCheck.md) (1)

**Used by (5):** [BridgeMapCheck](BridgeMapCheck.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [RailDrawCheck](RailDrawCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 261 | 1. signals |
| 307 | 2. a boulevard crossing a boulevard |
| 345 | 3. a diamond |
| 385 | 4. a highway arm's end |
| 417 | 5. a stack |
| 448 | 6. below JUNCTIONS_FROM |
| 470 | 7. a roundabout across a tile's edge |
| 490 | 8. a diamond across the tile's edge |
| 522 | 9. a ramp mid-highway with no street beneath |
| 550 | 10. a 45-degree highway stretch |
| 618 | 11. a road two plots wide |
| 684 | 12. the interchanges' ground (batch FR7, slice 4) |
| 922 | 14. access roads (batch FR7, slice 4) |
| 1075 | 15. kerb corners and estate T's (batch FR7, slice 4) |
| 1161 | 16. sidings into the bulk works the plans place (batch FR7, slice 5) |
| 1305 | 13. the loops (batch FR7, slice 4) |
| 1400 | 17. one junction a crossing, mostly signals (batch FX5) |
| 1585 | 18. a 45-degree highway joins the grid cleanly (FX5) |
| 1787 | 19. a crossing is read alike from every tile that draws it (MF1) |
| 1921 | 20. a cloverleaf's loops are leaves (MF1) |
| 2075 | 21. a wide road is one road (MF1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 148 | `JunctionCheck.T` | `TilePainter.TILE` |  |
| 177 | `JunctionCheck.ARTERIAL` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_ARTERIAL)` |  |
| 178 | `JunctionCheck.BOULEVARD` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_BOULEVARD)` |  |
| 179 | `JunctionCheck.STREET` | `streetCode(DistrictPlan.PAVED, 1, 0)` |  |
| 695 | `JunctionCheck.SITE` | `112` | The founding site the fixture cities lie round: a line crossing of the highways' (16 plots into a cell), so the hub is here. |
| 925 | `JunctionCheck.YARD` | `{ 163, 131, 20, 20 }, PORT = { 40, 100, 12, 10 }` | The yard, sea terminal and depot the access fixtures use: a Rail Terminal's box east of the hub's cell row, a terminal's west of it. |
| 1164 | `JunctionCheck.BIG` | `51, SMALL = 12` | The two bulk works the siding fixtures place: Machine Works-sized 11 x 11 (id 51) and the smaller 6 x 7 (id 12) - ids in buildings.json are permanent. |
| 1167 | `JunctionCheck.LINE_ROW` | `125` | The main line's row in the siding fixtures: 125, 16 plots below the first works' box, with a street between. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 141 | `static int fails` |  |
| 686 | `static BuildingVisual.Type[] types` |  |
| 699 | `final java.util.Map<Long, Integer> patch` |  |
| 1594 | `final double[] x, y, cum` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 139 | 2026 | **type** `public class JunctionCheck` | Proves that the junctions and interchanges of spec-freight 4.6 are drawn by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19). |
| 143 | 4 | `static void check(String label, boolean ok)` |  |
| 151 | 6 | `static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px)` | The image a painted tile rasters to at px pixels a plot. |
| 159 | 5 | `static TilePainter.Input owned()` | A tile whose plots are all owned, with no buildings and no sites. |
| 166 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` | Paints `in` into a fresh Painted. |
| 173 | 3 | `static int streetCode(int kind, int width, int role)` | A street code: kind in the low bits, then width and the painter's role. |
| 182 | 3 | `static void streetColumn(TilePainter.Input in, int x, int code)` | Sets a column of street plots to `code`. |
| 187 | 3 | `static void streetRow(TilePainter.Input in, int y, int code)` | Sets a row of street plots to `code`. |
| 192 | 3 | `static void highwayRow(TilePainter.Input in, int y)` | Sets a row of highway plots. |
| 197 | 3 | `static void highwayRowTo(TilePainter.Input in, int y, int last)` | Sets a row of highway plots ending at `last` (exclusive). |
| 202 | 3 | `static void highwayColumn(TilePainter.Input in, int x)` | Sets a column of highway plots. |
| 207 | 6 | `static void building(TilePainter.Input in, int x, int y)` | Adds a one-plot building (an industry's fill) at (x, y). |
| 215 | 3 | `static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px)` | One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. |
| 220 | 6 | `static boolean hasColour(int[] img, int w, int plotX, int plotY, int px, int colour)` | Whether plot (plotX, plotY) has any pixel of `colour` at px pixels a plot. |
| 228 | 6 | `static boolean noneOf(int[] img, int...colours)` | Whether the whole image has none of the given colours. |
| 235 | 25 | `public static void main(String[] args)` |  |

### 1. signals (lines 261-306)

| line | len | member | says |
|---:|---:|---|---|
| 262 | 44 | `static void signals()` |  |

### 2. a boulevard crossing a boulevard (lines 307-344)

| line | len | member | says |
|---:|---:|---|---|
| 308 | 36 | `static void roundabout()` |  |

### 3. a diamond (lines 345-384)

| line | len | member | says |
|---:|---:|---|---|
| 346 | 38 | `static void diamond()` |  |

### 4. a highway arm's end (lines 385-416)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 26 | `static void highwayEnd()` |  |
| 413 | 3 | `static int crossSectionPaved(int px)` |  |

### 5. a stack (lines 417-447)

| line | len | member | says |
|---:|---:|---|---|
| 418 | 29 | `static void stack()` |  |

### 6. below JUNCTIONS_FROM (lines 448-469)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 20 | `static void belowJunctionsFrom()` |  |

### 7. a roundabout across a tile's edge (lines 470-489)

| line | len | member | says |
|---:|---:|---|---|
| 471 | 18 | `static void roundaboutAcrossEdge()` |  |

### 8. a diamond across the tile's edge (lines 490-521)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 30 | `static void diamondAcrossEdge()` |  |

### 9. a ramp mid-highway with no street beneath (lines 522-549)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 26 | `static void rampMidHighway()` |  |

### 10. a 45-degree highway stretch (lines 550-617)

| line | len | member | says |
|---:|---:|---|---|
| 551 | 58 | `static void diagonalStretch()` |  |
| 611 | 6 | `static boolean anyPlotHas(int[] img, int w, int x0, int x1, int y0, int y1, int px, int...colours)` | Whether any plot (x, y) with x in x0..x1 and y in y0..y1 has a pixel of one of the colours. |

### 11. a road two plots wide (lines 618-683)

| line | len | member | says |
|---:|---:|---|---|
| 619 | 64 | `static void twoPlotsWide()` |  |

### 12. the interchanges' ground (batch FR7, slice 4) (lines 684-921)

| line | len | member | says |
|---:|---:|---|---|
| 689 | 4 | `static BuildingVisual.Type[] types()` | The game's building types, by id (read once). |
| 698 | 15 | **type** `static class PatchGround extends MainLineCheck.BoxGround` | Dry owned ground with some plots of another class or none, to make the ground short: -1 (unowned) or a World class at the named plots. |
| 701 | 1 | `PatchGround(long x0, long y0, long x1, long y1)` _(in JunctionCheck.PatchGround)_ |  |
| 703 | 4 | `PatchGround with(long x, long y, int cls)` _(in JunctionCheck.PatchGround)_ |  |
| 708 | 4 | `public int at(long x, long y, int dir)` _(in JunctionCheck.PatchGround)_ |  |
| 715 | 6 | `static CityRuns highwayCity(CityRuns.Ground g, long plots)` | A fixture city: highways laid to `plots` over the ground g from the founding site, no railway; its gateway none. |
| 723 | 4 | `static InterchangeGround.Site siteAt(CityRuns runs, long x, long y)` | The interchange site at (x, y) among the runs' sites, or null. |
| 729 | 16 | `static void addArm(CityRuns runs, long fromX, long fromY, int h, int steps)` | A highway arm laid by hand: from (fromX, fromY) `steps` plots along heading h (0 east, 2 south, 4 west, 6 north), as step() lays one. |
| 747 | 5 | `static byte[] planGround(CityRuns runs, long x0, long y0, int w, int h)` | The plots of the runs' fill over the w x h box from (x0, y0), as a plan is given them (yardsOver). |
| 753 | 91 | `static void interchangeGround()` |  |
| 846 | 1 | **type** `interface Blocked` | A box test as the works the map has placed (version 1: the harness changes it by setting another). |
| 846 | 1 | `boolean in(long x0, long y0, long x1, long y1)` _(in JunctionCheck.Blocked)_ |  |
| 848 | 7 | `static InterchangeGround.Guard guard(Blocked b)` |  |
| 856 | 4 | `static boolean noReserve(byte[] fixed)` |  |
| 862 | 29 | `static void planRound(CityRuns runs)` | Plans a district round the fixture city's reserve, and again without it. |
| 893 | 9 | `static int reserveCapacity(CityRuns runs)` | The plots every laid reserve's box holds that are not a highway's: what a plan is given. |
| 904 | 17 | `static DistrictPlan.Input planInput(CityRuns runs)` | A plan's input over the district whose frame starts at the world's origin: owned grass, the PlanCheck test district's buildings and road, the hub the founding site, the runs' plots and reserves as a plan is given them. |

### 14. access roads (batch FR7, slice 4) (lines 922-1074)

| line | len | member | says |
|---:|---:|---|---|
| 928 | 4 | `static void work(DistrictPlan.Input in, int[] box)` | Marks a work's ground in the plan's input, as the map does (FIXED_YARD) and lists it as a work needing a road. |
| 934 | 5 | `static int depotType()` | The type id of the Truck Depot (the one building of the category HAULAGE). |
| 941 | 14 | `static DistrictPlan.Input accessInput(CityRuns runs)` | A plan's input with a yard, a sea terminal and a Truck Depot to place, and (with highways) their ramps given as the map gives them. |
| 957 | 5 | `static boolean beside(int[] box, int x, int y)` | Whether plot (x, y) is beside the box {bx, by, w, h}, across one of its sides. |
| 964 | 1 | `static boolean access(DistrictPlan p, int q)` | Whether plot q of the plan is one an access road runs over. |
| 967 | 5 | `static int accessPlots(DistrictPlan p)` | The plots the access roads run over that are streets with a surface to pay (not the ramp's, beneath a highway). |
| 974 | 15 | `static boolean chained(DistrictPlan p, int gx, int gy, int ex, int ey)` | Whether the access road runs unbroken from (gx, gy) to (ex, ey): a chain of plots of the access role, side to side. |
| 990 | 84 | `static void accessRoads()` |  |

### 15. kerb corners and estate T's (batch FR7, slice 4) (lines 1075-1160)

| line | len | member | says |
|---:|---:|---|---|
| 1078 | 4 | `static void buildingOf(TilePainter.Input in, int x, int y, int type)` | A one-plot building of the type at (x, y). |
| 1084 | 4 | `static int typeOfClass(int cls)` | The id of a type of the given class. |
| 1089 | 71 | `static void kerbCorners()` |  |

### 16. sidings into the bulk works the plans place (batch FR7, slice 5) (lines 1161-1304)

| line | len | member | says |
|---:|---:|---|---|
| 1170 | 10 | `static DistrictPlan.Input sidingInput(int row, int share, int bigs, int smalls)` | A plan's input with `bigs` and `smalls` bulk works to place and nothing else, an east-west main line along `row` (none for a negative row) of the runs' railway, and `share` sidings to lay. |
| 1182 | 1 | `static boolean siding(DistrictPlan p, int q)` | Whether plot q of the plan is one a siding runs over. |
| 1185 | 15 | `static boolean sidingChained(DistrictPlan p, int sx, int sy, int ex, int ey)` | Whether the siding runs unbroken from (sx, sy) to (ex, ey): a chain of siding plots, side to side. |
| 1201 | 103 | `static void sidings()` |  |

### 13. the loops (batch FR7, slice 4) (lines 1305-1399)

| line | len | member | says |
|---:|---:|---|---|
| 1308 | 4 | `static int atOffset(int[] img, int w, int jx, int jy, double u, double v, int px)` | The pixel of the tile at plot (junction x + u, junction y + v) in plots from the junction plot's middle, at px pixels a plot. |
| 1313 | 86 | `static void loopsDrawn()` |  |

### 17. one junction a crossing, mostly signals (batch FX5) (lines 1400-1584)

| line | len | member | says |
|---:|---:|---|---|
| 1403 | 1 | **type** `interface Streets` | The street code at world plot (x, y), as a map's plans give it: 0 for none. |
| 1403 | 1 | `int at(int x, int y)` _(in JunctionCheck.Streets)_ |  |
| 1406 | 17 | `static TilePainter.Input inputFor(int tx, int ty, Streets f)` | Tile (tx, ty) as the map fills its input from the streets `f`: its own plots, the street just beyond each edge, and the ring of CROSS_REACH plots about it. |
| 1425 | 3 | `static Streets cross(int x0, int x1, int y0, int y1, int code)` | A road `code` across the world's columns x0 to x1 and another across its rows y0 to y1. |
| 1430 | 6 | `static Streets withAvenue(Streets f, int x, int y, int dx, int dy, int len)` | The streets `f` and a slanted road of plain street plots leaving a corner: from (x, y) by (dx, dy), `len` plots. |
| 1438 | 4 | `static int[] tileImage(int tx, int ty, Streets f, int px)` | The image of tile (tx, ty) of the streets `f` at px pixels a plot. |
| 1444 | 9 | `static boolean surfaceIs(int[] img, int w, int x, int y, int px, int colour, boolean h, boolean v)` | Whether every pixel of plot (x, y)'s street surface - a half-width band along each axis `h` or `v` names - is `colour`. |
| 1455 | 8 | `static boolean islandIsRound(int[] img, int w, int px, double cx, double cy, double inner)` | Whether, over the whole image (w pixels across), a pixel is ISLAND exactly when it lies within `inner` plots of (cx, cy), in plots from the image's corner. |
| 1465 | 6 | `static boolean sameWindow(int[] a, int wa, int ax0, int ay0, int[] b, int wb, int bx0, int by0, int n, int px)` | Whether the n x n plots from (ax0, ay0) of image a (wa pixels across) are pixel for pixel those from (bx0, by0) of image b (wb across). |
| 1473 | 10 | `static int[] fourTiles(Streets f, int px)` | The four tiles (0, 0), (1, 0), (0, 1) and (1, 1) of the streets `f`, each rastered alone and laid side by side in one image 2 * T plots across. |
| 1484 | 100 | `static void oneJunctionACrossing()` |  |

### 18. a 45-degree highway joins the grid cleanly (FX5) (lines 1585-1786)

| line | len | member | says |
|---:|---:|---|---|
| 1588 | 3 | `static boolean highwayColour(int c)` | The highway colours a pixel may have: the surface, the casing and the centre line. |
| 1593 | 30 | **type** `static final class JPath` | The carriageway's centre line as the map's own rule rounds it (TrackCurves.round() at HIGHWAY_RADIUS), in pixels, with the length along it at each point. |
| 1596 | 11 | `JPath(double[] xs, double[] ys, int px)` _(in JunctionCheck.JPath)_ |  |
| 1609 | 13 | `double dist(double u, double v, double[] out)` _(in JunctionCheck.JPath)_ | The distance from pixel point (u, v) to the path; out[0] receives the length along the path to the nearest foot. |
| 1630 | 33 | `static TilePainter.Input joinedStretch(int x0, int y0, int steps, boolean southOut, boolean ramp, boolean bent)` | Section 10's stretch (a plot where x + y is odd, `steps` steps south-east at (x0 + k, y0 + k) with their corners) with straight highway east into it from x = 2 and out of it: south (southOut) from its last step, or ea... |
| 1669 | 18 | `static int[] diagonalCounts(int[] img, int px, int x0, int y0, int plotLo, int plotHi, int kLo, int kHi)` | Section 10's three counts for the 45-degree band between plots x0 + plotLo .. |
| 1688 | 98 | `static void diagonalJoints()` |  |

### 19. a crossing is read alike from every tile that draws it (MF1) (lines 1787-1920)

| line | len | member | says |
|---:|---:|---|---|
| 1790 | 8 | `static TilePainter.Input inputWithAvenue(int tx, int ty, Streets f, Streets marks, int ox, int oy)` | Tile (tx, ty) of the streets `f` with the avenue plots `marks` laid and the avenues' origin at world plot (ox, oy), as the map fills its input. |
| 1800 | 9 | `static java.util.List<int[]> readBy(TilePainter.Input in, int tx, int ty)` | The crossings tile (tx, ty) reads from `in` that touch its plots, in world plots: {first column, first row, last column, last row, roads, 1 for a roundabout}. |
| 1811 | 11 | `static int unlike(java.util.List<int[]> reads)` | How many pairs of reads of crossings that overlap - read by different tiles - are not the same rectangle and verdict (roads leaving it apart: a verdict is what is drawn). |
| 1824 | 9 | `static boolean laneIsPaved(int[] img, int w, int px, double cx, double cy, double inner, double outer)` | Whether, over the whole image (w pixels across), every pixel between `inner` and `outer` plots of (cx, cy), in plots from the image's corner, is the paved colour: a roundabout's lane drawn whole. |
| 1835 | 12 | `static int[] fourTilesWithAvenue(Streets f, Streets marks, int ox, int oy, int px)` | The four tiles (0, 0), (1, 0), (0, 1), (1, 1) of the streets `f` and avenue plots `marks` (origin ox, oy), each rastered alone and laid side by side. |
| 1860 | 60 | `static void crossingReadAlike()` | (MF1, 2026-10-28; Jerus, shot 11: "a half roundabout" - the tree circle where the diagonal avenue meets a street cut flat down one side) A crossing is the plan's, so every tile that draws a part of it reads one rectan... |

### 20. a cloverleaf's loops are leaves (MF1) (lines 1921-2074)

| line | len | member | says |
|---:|---:|---|---|
| 1924 | 24 | `static int components(boolean[] mask, int w, int[] label)` | The 4-connected components of `mask` (w pixels across): each pixel's number, from 1, 0 for none; the count. |
| 1950 | 22 | `static int[] groundReached(boolean[] mask, int w)` | How many pixels outside `mask` the 4-connected ground reaches from pixel (0, 0), and how many there are in all: unequal where a curve of the mask closes round some ground. |
| 1981 | 93 | `static void leavesDrawn()` | (MF1, 2026-10-28; Jerus, shot 5: "Draw a cleaner interchange (a cloverleaf's or a diamond's shape)") A cloverleaf drew four separate ring loops, each round an island of ground, each with two straight spokes to the hig... |

### 21. a wide road is one road (MF1) (lines 2075-2164)

| line | len | member | says |
|---:|---:|---|---|
| 2078 | 3 | `static void band(TilePainter.Input in, int x0, int x1, int y0, int y1, int code)` | A band of street plots `code`: the columns x0 to x1 across and the rows y0 to y1 down. |
| 2090 | 73 | `static void wideRoad()` | (MF1, 2026-10-28; Jerus, on his city: "continous intersection, like its a 3x14 road roughly, but shown as 3 roads, with intersection on every node") A street several plots across drew every plot's own bar through its ... |

