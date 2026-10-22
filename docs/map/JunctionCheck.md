# JunctionCheck.java - 1,768 lines · 76 methods · 8 constants · harnesses

`ham/citybuildersim/JunctionCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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
> ... (60 more lines in the source)

**Uses:** [TileRaster](TileRaster.md) (153), [DistrictPlan](DistrictPlan.md) (117), [TilePainter](TilePainter.md) (87), [CityRuns](CityRuns.md) (56), [BuildingVisual](BuildingVisual.md) (50), [InterchangeGround](InterchangeGround.md) (20), [TrackCurves](TrackCurves.md) (9), [PlanCheck](PlanCheck.md) (5), [World](World.md) (2), [BuildingCatalog](BuildingCatalog.md) (1), [MainLineCheck](MainLineCheck.md) (1), [Founding](Founding.md) (1), [BuildingType](BuildingType.md) (1), [MapTiles](MapTiles.md) (1)

**Used by (3):** [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 249 | 1. signals |
| 295 | 2. a boulevard crossing a boulevard |
| 333 | 3. a diamond |
| 373 | 4. a highway arm's end |
| 405 | 5. a stack |
| 436 | 6. below JUNCTIONS_FROM |
| 458 | 7. a roundabout across a tile's edge |
| 478 | 8. a diamond across the tile's edge |
| 510 | 9. a ramp mid-highway with no street beneath |
| 538 | 10. a 45-degree highway stretch |
| 606 | 11. a road two plots wide |
| 672 | 12. the interchanges' ground (batch FR7, slice 4) |
| 910 | 14. access roads (batch FR7, slice 4) |
| 1063 | 15. kerb corners and estate T's (batch FR7, slice 4) |
| 1149 | 16. sidings into the bulk works the plans place (batch FR7, slice 5) |
| 1293 | 13. the loops (batch FR7, slice 4) |
| 1381 | 17. one junction a crossing, mostly signals (batch FX5) |
| 1566 | 18. a 45-degree highway joins the grid cleanly (FX5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 139 | `JunctionCheck.T` | `TilePainter.TILE` |  |
| 168 | `JunctionCheck.ARTERIAL` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_ARTERIAL)` |  |
| 169 | `JunctionCheck.BOULEVARD` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_BOULEVARD)` |  |
| 170 | `JunctionCheck.STREET` | `streetCode(DistrictPlan.PAVED, 1, 0)` |  |
| 683 | `JunctionCheck.SITE` | `112` | The founding site the fixture cities lie round: a line crossing of the highways' (16 plots into a cell), so the hub is here. |
| 913 | `JunctionCheck.YARD` | `{ 163, 131, 20, 20 }, PORT = { 40, 100, 12, 10 }` | The yard, sea terminal and depot the access fixtures use: a Rail Terminal's box east of the hub's cell row, a terminal's west of it. |
| 1152 | `JunctionCheck.BIG` | `51, SMALL = 12` | The two bulk works the siding fixtures place: Machine Works-sized 11 x 11 (id 51) and the smaller 6 x 7 (id 12) - ids in buildings.json are permanent. |
| 1155 | `JunctionCheck.LINE_ROW` | `125` | The main line's row in the siding fixtures: 125, 16 plots below the first works' box, with a street between. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 132 | `static int fails` |  |
| 674 | `static BuildingVisual.Type[] types` |  |
| 687 | `final java.util.Map<Long, Integer> patch` |  |
| 1575 | `final double[] x, y, cum` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 130 | 1639 | **type** `public class JunctionCheck` | Proves that the junctions and interchanges of spec-freight 4.6 are drawn by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19). |
| 134 | 4 | `static void check(String label, boolean ok)` |  |
| 142 | 6 | `static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px)` | The image a painted tile rasters to at px pixels a plot. |
| 150 | 5 | `static TilePainter.Input owned()` | A tile whose plots are all owned, with no buildings and no sites. |
| 157 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` | Paints `in` into a fresh Painted. |
| 164 | 3 | `static int streetCode(int kind, int width, int role)` | A street code: kind in the low bits, then width and the painter's role. |
| 173 | 3 | `static void streetColumn(TilePainter.Input in, int x, int code)` | Sets a column of street plots to `code`. |
| 178 | 3 | `static void streetRow(TilePainter.Input in, int y, int code)` | Sets a row of street plots to `code`. |
| 183 | 3 | `static void highwayRow(TilePainter.Input in, int y)` | Sets a row of highway plots. |
| 188 | 3 | `static void highwayRowTo(TilePainter.Input in, int y, int last)` | Sets a row of highway plots ending at `last` (exclusive). |
| 193 | 3 | `static void highwayColumn(TilePainter.Input in, int x)` | Sets a column of highway plots. |
| 198 | 6 | `static void building(TilePainter.Input in, int x, int y)` | Adds a one-plot building (an industry's fill) at (x, y). |
| 206 | 3 | `static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px)` | One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. |
| 211 | 6 | `static boolean hasColour(int[] img, int w, int plotX, int plotY, int px, int colour)` | Whether plot (plotX, plotY) has any pixel of `colour` at px pixels a plot. |
| 219 | 6 | `static boolean noneOf(int[] img, int...colours)` | Whether the whole image has none of the given colours. |
| 226 | 22 | `public static void main(String[] args)` |  |

### 1. signals (lines 249-294)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 44 | `static void signals()` |  |

### 2. a boulevard crossing a boulevard (lines 295-332)

| line | len | member | says |
|---:|---:|---|---|
| 296 | 36 | `static void roundabout()` |  |

### 3. a diamond (lines 333-372)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 38 | `static void diamond()` |  |

### 4. a highway arm's end (lines 373-404)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 26 | `static void highwayEnd()` |  |
| 401 | 3 | `static int crossSectionPaved(int px)` |  |

### 5. a stack (lines 405-435)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 29 | `static void stack()` |  |

### 6. below JUNCTIONS_FROM (lines 436-457)

| line | len | member | says |
|---:|---:|---|---|
| 437 | 20 | `static void belowJunctionsFrom()` |  |

### 7. a roundabout across a tile's edge (lines 458-477)

| line | len | member | says |
|---:|---:|---|---|
| 459 | 18 | `static void roundaboutAcrossEdge()` |  |

### 8. a diamond across the tile's edge (lines 478-509)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 30 | `static void diamondAcrossEdge()` |  |

### 9. a ramp mid-highway with no street beneath (lines 510-537)

| line | len | member | says |
|---:|---:|---|---|
| 511 | 26 | `static void rampMidHighway()` |  |

### 10. a 45-degree highway stretch (lines 538-605)

| line | len | member | says |
|---:|---:|---|---|
| 539 | 58 | `static void diagonalStretch()` |  |
| 599 | 6 | `static boolean anyPlotHas(int[] img, int w, int x0, int x1, int y0, int y1, int px, int...colours)` | Whether any plot (x, y) with x in x0..x1 and y in y0..y1 has a pixel of one of the colours. |

### 11. a road two plots wide (lines 606-671)

| line | len | member | says |
|---:|---:|---|---|
| 607 | 64 | `static void twoPlotsWide()` |  |

### 12. the interchanges' ground (batch FR7, slice 4) (lines 672-909)

| line | len | member | says |
|---:|---:|---|---|
| 677 | 4 | `static BuildingVisual.Type[] types()` | The game's building types, by id (read once). |
| 686 | 15 | **type** `static class PatchGround extends MainLineCheck.BoxGround` | Dry owned ground with some plots of another class or none, to make the ground short: -1 (unowned) or a World class at the named plots. |
| 689 | 1 | `PatchGround(long x0, long y0, long x1, long y1)` _(in JunctionCheck.PatchGround)_ |  |
| 691 | 4 | `PatchGround with(long x, long y, int cls)` _(in JunctionCheck.PatchGround)_ |  |
| 696 | 4 | `public int at(long x, long y, int dir)` _(in JunctionCheck.PatchGround)_ |  |
| 703 | 6 | `static CityRuns highwayCity(CityRuns.Ground g, long plots)` | A fixture city: highways laid to `plots` over the ground g from the founding site, no railway; its gateway none. |
| 711 | 4 | `static InterchangeGround.Site siteAt(CityRuns runs, long x, long y)` | The interchange site at (x, y) among the runs' sites, or null. |
| 717 | 16 | `static void addArm(CityRuns runs, long fromX, long fromY, int h, int steps)` | A highway arm laid by hand: from (fromX, fromY) `steps` plots along heading h (0 east, 2 south, 4 west, 6 north), as step() lays one. |
| 735 | 5 | `static byte[] planGround(CityRuns runs, long x0, long y0, int w, int h)` | The plots of the runs' fill over the w x h box from (x0, y0), as a plan is given them (yardsOver). |
| 741 | 91 | `static void interchangeGround()` |  |
| 834 | 1 | **type** `interface Blocked` | A box test as the works the map has placed (version 1: the harness changes it by setting another). |
| 834 | 1 | `boolean in(long x0, long y0, long x1, long y1)` _(in JunctionCheck.Blocked)_ |  |
| 836 | 7 | `static InterchangeGround.Guard guard(Blocked b)` |  |
| 844 | 4 | `static boolean noReserve(byte[] fixed)` |  |
| 850 | 29 | `static void planRound(CityRuns runs)` | Plans a district round the fixture city's reserve, and again without it. |
| 881 | 9 | `static int reserveCapacity(CityRuns runs)` | The plots every laid reserve's box holds that are not a highway's: what a plan is given. |
| 892 | 17 | `static DistrictPlan.Input planInput(CityRuns runs)` | A plan's input over the district whose frame starts at the world's origin: owned grass, the PlanCheck test district's buildings and road, the hub the founding site, the runs' plots and reserves as a plan is given them. |

### 14. access roads (batch FR7, slice 4) (lines 910-1062)

| line | len | member | says |
|---:|---:|---|---|
| 916 | 4 | `static void work(DistrictPlan.Input in, int[] box)` | Marks a work's ground in the plan's input, as the map does (FIXED_YARD) and lists it as a work needing a road. |
| 922 | 5 | `static int depotType()` | The type id of the Truck Depot (the one building of the category HAULAGE). |
| 929 | 14 | `static DistrictPlan.Input accessInput(CityRuns runs)` | A plan's input with a yard, a sea terminal and a Truck Depot to place, and (with highways) their ramps given as the map gives them. |
| 945 | 5 | `static boolean beside(int[] box, int x, int y)` | Whether plot (x, y) is beside the box {bx, by, w, h}, across one of its sides. |
| 952 | 1 | `static boolean access(DistrictPlan p, int q)` | Whether plot q of the plan is one an access road runs over. |
| 955 | 5 | `static int accessPlots(DistrictPlan p)` | The plots the access roads run over that are streets with a surface to pay (not the ramp's, beneath a highway). |
| 962 | 15 | `static boolean chained(DistrictPlan p, int gx, int gy, int ex, int ey)` | Whether the access road runs unbroken from (gx, gy) to (ex, ey): a chain of plots of the access role, side to side. |
| 978 | 84 | `static void accessRoads()` |  |

### 15. kerb corners and estate T's (batch FR7, slice 4) (lines 1063-1148)

| line | len | member | says |
|---:|---:|---|---|
| 1066 | 4 | `static void buildingOf(TilePainter.Input in, int x, int y, int type)` | A one-plot building of the type at (x, y). |
| 1072 | 4 | `static int typeOfClass(int cls)` | The id of a type of the given class. |
| 1077 | 71 | `static void kerbCorners()` |  |

### 16. sidings into the bulk works the plans place (batch FR7, slice 5) (lines 1149-1292)

| line | len | member | says |
|---:|---:|---|---|
| 1158 | 10 | `static DistrictPlan.Input sidingInput(int row, int share, int bigs, int smalls)` | A plan's input with `bigs` and `smalls` bulk works to place and nothing else, an east-west main line along `row` (none for a negative row) of the runs' railway, and `share` sidings to lay. |
| 1170 | 1 | `static boolean siding(DistrictPlan p, int q)` | Whether plot q of the plan is one a siding runs over. |
| 1173 | 15 | `static boolean sidingChained(DistrictPlan p, int sx, int sy, int ex, int ey)` | Whether the siding runs unbroken from (sx, sy) to (ex, ey): a chain of siding plots, side to side. |
| 1189 | 103 | `static void sidings()` |  |

### 13. the loops (batch FR7, slice 4) (lines 1293-1380)

| line | len | member | says |
|---:|---:|---|---|
| 1296 | 4 | `static int atOffset(int[] img, int w, int jx, int jy, double u, double v, int px)` | The pixel of the tile at plot (junction x + u, junction y + v) in plots from the junction plot's middle, at px pixels a plot. |
| 1301 | 79 | `static void loopsDrawn()` |  |

### 17. one junction a crossing, mostly signals (batch FX5) (lines 1381-1565)

| line | len | member | says |
|---:|---:|---|---|
| 1384 | 1 | **type** `interface Streets` | The street code at world plot (x, y), as a map's plans give it: 0 for none. |
| 1384 | 1 | `int at(int x, int y)` _(in JunctionCheck.Streets)_ |  |
| 1387 | 17 | `static TilePainter.Input inputFor(int tx, int ty, Streets f)` | Tile (tx, ty) as the map fills its input from the streets `f`: its own plots, the street just beyond each edge, and the ring of REACH plots about it. |
| 1406 | 3 | `static Streets cross(int x0, int x1, int y0, int y1, int code)` | A road `code` across the world's columns x0 to x1 and another across its rows y0 to y1. |
| 1411 | 6 | `static Streets withAvenue(Streets f, int x, int y, int dx, int dy, int len)` | The streets `f` and a slanted road of plain street plots leaving a corner: from (x, y) by (dx, dy), `len` plots. |
| 1419 | 4 | `static int[] tileImage(int tx, int ty, Streets f, int px)` | The image of tile (tx, ty) of the streets `f` at px pixels a plot. |
| 1425 | 9 | `static boolean surfaceIs(int[] img, int w, int x, int y, int px, int colour, boolean h, boolean v)` | Whether every pixel of plot (x, y)'s street surface - a half-width band along each axis `h` or `v` names - is `colour`. |
| 1436 | 8 | `static boolean islandIsRound(int[] img, int w, int px, double cx, double cy, double inner)` | Whether, over the whole image (w pixels across), a pixel is ISLAND exactly when it lies within `inner` plots of (cx, cy), in plots from the image's corner. |
| 1446 | 6 | `static boolean sameWindow(int[] a, int wa, int ax0, int ay0, int[] b, int wb, int bx0, int by0, int n, int px)` | Whether the n x n plots from (ax0, ay0) of image a (wa pixels across) are pixel for pixel those from (bx0, by0) of image b (wb across). |
| 1454 | 10 | `static int[] fourTiles(Streets f, int px)` | The four tiles (0, 0), (1, 0), (0, 1) and (1, 1) of the streets `f`, each rastered alone and laid side by side in one image 2 * T plots across. |
| 1465 | 100 | `static void oneJunctionACrossing()` |  |

### 18. a 45-degree highway joins the grid cleanly (FX5) (lines 1566-1768)

| line | len | member | says |
|---:|---:|---|---|
| 1569 | 3 | `static boolean highwayColour(int c)` | The highway colours a pixel may have: the surface, the casing and the centre line. |
| 1574 | 30 | **type** `static final class JPath` | The carriageway's centre line as the map's own rule rounds it (TrackCurves.round() at HIGHWAY_RADIUS), in pixels, with the length along it at each point. |
| 1577 | 11 | `JPath(double[] xs, double[] ys, int px)` _(in JunctionCheck.JPath)_ |  |
| 1590 | 13 | `double dist(double u, double v, double[] out)` _(in JunctionCheck.JPath)_ | The distance from pixel point (u, v) to the path; out[0] receives the length along the path to the nearest foot. |
| 1611 | 33 | `static TilePainter.Input joinedStretch(int x0, int y0, int steps, boolean southOut, boolean ramp, boolean bent)` | Section 10's stretch (a plot where x + y is odd, `steps` steps south-east at (x0 + k, y0 + k) with their corners) with straight highway east into it from x = 2 and out of it: south (southOut) from its last step, or ea... |
| 1650 | 18 | `static int[] diagonalCounts(int[] img, int px, int x0, int y0, int plotLo, int plotHi, int kLo, int kHi)` | Section 10's three counts for the 45-degree band between plots x0 + plotLo .. |
| 1669 | 98 | `static void diagonalJoints()` |  |

