# JunctionCheck.java - 1,335 lines · 60 methods · 8 constants · harnesses

`ham/citybuildersim/JunctionCheck.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

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
>   2. a boulevard crossing a boulevard has ISLAND pixels at its middle and
>      the paved ring's pixels on its four neighbouring street plots, and
>      none on a building placed diagonally beside it;
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
>      Input.beyond by the next tile, which draws the ring's pixels on its
>      first column.
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
>      where a street crosses them, two boulevards side by side a roundabout
>      only where one crosses them; and three boulevards side by side, as his
>      city's are across a cell line.
>  12. (batch FR7, slice 4: FR0-L's D19) THE INTERCHANGES' GROUND: in a city
>      of highways laid over owned dry ground the spine's hub, where four arms
>      cross, is a cloverleaf site with a CLOVERLEAF_SIDE reserve centred on
>      it, and an arm laid to end beside the spine a trumpet site with a
>      TRUMPET_SIDE one beside the junction on its stub's side; the plan's
>      ground (fill with yardsOver) holds F_RESERVE on every open plot of each
>      box and nowhere else, a tile's fill none; each way the rule names - a
> ... (33 more lines in the source)

**Uses:** [DistrictPlan](DistrictPlan.md) (117), [TilePainter](TilePainter.md) (76), [TileRaster](TileRaster.md) (75), [CityRuns](CityRuns.md) (52), [BuildingVisual](BuildingVisual.md) (38), [InterchangeGround](InterchangeGround.md) (20), [PlanCheck](PlanCheck.md) (5), [World](World.md) (2), [BuildingCatalog](BuildingCatalog.md) (1), [MainLineCheck](MainLineCheck.md) (1), [Founding](Founding.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (1):** [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 220 | 1. signals |
| 266 | 2. a roundabout |
| 300 | 3. a diamond |
| 340 | 4. a highway arm's end |
| 372 | 5. a stack |
| 403 | 6. below JUNCTIONS_FROM |
| 425 | 7. a roundabout across a tile's edge |
| 446 | 8. a diamond across the tile's edge |
| 478 | 9. a ramp mid-highway with no street beneath |
| 506 | 10. a 45-degree highway stretch |
| 574 | 11. a road two plots wide |
| 632 | 12. the interchanges' ground (batch FR7, slice 4) |
| 870 | 14. access roads (batch FR7, slice 4) |
| 1023 | 15. kerb corners and estate T's (batch FR7, slice 4) |
| 1109 | 16. sidings into the bulk works the plans place (batch FR7, slice 5) |
| 1253 | 13. the loops (batch FR7, slice 4) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `JunctionCheck.T` | `TilePainter.TILE` |  |
| 141 | `JunctionCheck.ARTERIAL` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_ARTERIAL)` |  |
| 142 | `JunctionCheck.BOULEVARD` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_BOULEVARD)` |  |
| 143 | `JunctionCheck.STREET` | `streetCode(DistrictPlan.PAVED, 1, 0)` |  |
| 643 | `JunctionCheck.SITE` | `112` | The founding site the fixture cities lie round: a line crossing of the highways' (16 plots into a cell), so the hub is here. |
| 873 | `JunctionCheck.YARD` | `{ 163, 131, 20, 20 }, PORT = { 40, 100, 12, 10 }` | The yard, sea terminal and depot the access fixtures use: a Rail Terminal's box east of the hub's cell row, a terminal's west of it. |
| 1112 | `JunctionCheck.BIG` | `51, SMALL = 12` | The two bulk works the siding fixtures place: Machine Works-sized 11 x 11 (id 51) and the smaller 6 x 7 (id 12) - ids in buildings.json are permanent. |
| 1115 | `JunctionCheck.LINE_ROW` | `125` | The main line's row in the siding fixtures: 125, 16 plots below the first works' box, with a street between. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 105 | `static int fails` |  |
| 634 | `static BuildingVisual.Type[] types` |  |
| 647 | `final java.util.Map<Long, Integer> patch` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 103 | 1233 | **type** `public class JunctionCheck` | Proves that the junctions and interchanges of spec-freight 4.6 are drawn by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19). |
| 107 | 4 | `static void check(String label, boolean ok)` |  |
| 115 | 6 | `static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px)` | The image a painted tile rasters to at px pixels a plot. |
| 123 | 5 | `static TilePainter.Input owned()` | A tile whose plots are all owned, with no buildings and no sites. |
| 130 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` | Paints `in` into a fresh Painted. |
| 137 | 3 | `static int streetCode(int kind, int width, int role)` | A street code: kind in the low bits, then width and the painter's role. |
| 146 | 3 | `static void streetColumn(TilePainter.Input in, int x, int code)` | Sets a column of street plots to `code`. |
| 151 | 3 | `static void streetRow(TilePainter.Input in, int y, int code)` | Sets a row of street plots to `code`. |
| 156 | 3 | `static void highwayRow(TilePainter.Input in, int y)` | Sets a row of highway plots. |
| 161 | 3 | `static void highwayRowTo(TilePainter.Input in, int y, int last)` | Sets a row of highway plots ending at `last` (exclusive). |
| 166 | 3 | `static void highwayColumn(TilePainter.Input in, int x)` | Sets a column of highway plots. |
| 171 | 6 | `static void building(TilePainter.Input in, int x, int y)` | Adds a one-plot building (an industry's fill) at (x, y). |
| 179 | 3 | `static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px)` | One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. |
| 184 | 6 | `static boolean hasColour(int[] img, int w, int plotX, int plotY, int px, int colour)` | Whether plot (plotX, plotY) has any pixel of `colour` at px pixels a plot. |
| 192 | 6 | `static boolean noneOf(int[] img, int...colours)` | Whether the whole image has none of the given colours. |
| 199 | 20 | `public static void main(String[] args)` |  |

### 1. signals (lines 220-265)

| line | len | member | says |
|---:|---:|---|---|
| 221 | 44 | `static void signals()` |  |

### 2. a roundabout (lines 266-299)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 32 | `static void roundabout()` |  |

### 3. a diamond (lines 300-339)

| line | len | member | says |
|---:|---:|---|---|
| 301 | 38 | `static void diamond()` |  |

### 4. a highway arm's end (lines 340-371)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 26 | `static void highwayEnd()` |  |
| 368 | 3 | `static int crossSectionPaved(int px)` |  |

### 5. a stack (lines 372-402)

| line | len | member | says |
|---:|---:|---|---|
| 373 | 29 | `static void stack()` |  |

### 6. below JUNCTIONS_FROM (lines 403-424)

| line | len | member | says |
|---:|---:|---|---|
| 404 | 20 | `static void belowJunctionsFrom()` |  |

### 7. a roundabout across a tile's edge (lines 425-445)

| line | len | member | says |
|---:|---:|---|---|
| 426 | 19 | `static void roundaboutAcrossEdge()` |  |

### 8. a diamond across the tile's edge (lines 446-477)

| line | len | member | says |
|---:|---:|---|---|
| 447 | 30 | `static void diamondAcrossEdge()` |  |

### 9. a ramp mid-highway with no street beneath (lines 478-505)

| line | len | member | says |
|---:|---:|---|---|
| 479 | 26 | `static void rampMidHighway()` |  |

### 10. a 45-degree highway stretch (lines 506-573)

| line | len | member | says |
|---:|---:|---|---|
| 507 | 58 | `static void diagonalStretch()` |  |
| 567 | 6 | `static boolean anyPlotHas(int[] img, int w, int x0, int x1, int y0, int y1, int px, int...colours)` | Whether any plot (x, y) with x in x0..x1 and y in y0..y1 has a pixel of one of the colours. |

### 11. a road two plots wide (lines 574-631)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 56 | `static void twoPlotsWide()` |  |

### 12. the interchanges' ground (batch FR7, slice 4) (lines 632-869)

| line | len | member | says |
|---:|---:|---|---|
| 637 | 4 | `static BuildingVisual.Type[] types()` | The game's building types, by id (read once). |
| 646 | 15 | **type** `static class PatchGround extends MainLineCheck.BoxGround` | Dry owned ground with some plots of another class or none, to make the ground short: -1 (unowned) or a World class at the named plots. |
| 649 | 1 | `PatchGround(long x0, long y0, long x1, long y1)` _(in JunctionCheck.PatchGround)_ |  |
| 651 | 4 | `PatchGround with(long x, long y, int cls)` _(in JunctionCheck.PatchGround)_ |  |
| 656 | 4 | `public int at(long x, long y, int dir)` _(in JunctionCheck.PatchGround)_ |  |
| 663 | 6 | `static CityRuns highwayCity(CityRuns.Ground g, long plots)` | A fixture city: highways laid to `plots` over the ground g from the founding site, no railway; its gateway none. |
| 671 | 4 | `static InterchangeGround.Site siteAt(CityRuns runs, long x, long y)` | The interchange site at (x, y) among the runs' sites, or null. |
| 677 | 16 | `static void addArm(CityRuns runs, long fromX, long fromY, int h, int steps)` | A highway arm laid by hand: from (fromX, fromY) `steps` plots along heading h (0 east, 2 south, 4 west, 6 north), as step() lays one. |
| 695 | 5 | `static byte[] planGround(CityRuns runs, long x0, long y0, int w, int h)` | The plots of the runs' fill over the w x h box from (x0, y0), as a plan is given them (yardsOver). |
| 701 | 91 | `static void interchangeGround()` |  |
| 794 | 1 | **type** `interface Blocked` | A box test as the works the map has placed (version 1: the harness changes it by setting another). |
| 794 | 1 | `boolean in(long x0, long y0, long x1, long y1)` _(in JunctionCheck.Blocked)_ |  |
| 796 | 7 | `static InterchangeGround.Guard guard(Blocked b)` |  |
| 804 | 4 | `static boolean noReserve(byte[] fixed)` |  |
| 810 | 29 | `static void planRound(CityRuns runs)` | Plans a district round the fixture city's reserve, and again without it. |
| 841 | 9 | `static int reserveCapacity(CityRuns runs)` | The plots every laid reserve's box holds that are not a highway's: what a plan is given. |
| 852 | 17 | `static DistrictPlan.Input planInput(CityRuns runs)` | A plan's input over the district whose frame starts at the world's origin: owned grass, the PlanCheck test district's buildings and road, the hub the founding site, the runs' plots and reserves as a plan is given them. |

### 14. access roads (batch FR7, slice 4) (lines 870-1022)

| line | len | member | says |
|---:|---:|---|---|
| 876 | 4 | `static void work(DistrictPlan.Input in, int[] box)` | Marks a work's ground in the plan's input, as the map does (FIXED_YARD) and lists it as a work needing a road. |
| 882 | 5 | `static int depotType()` | The type id of the Truck Depot (the one building of the category HAULAGE). |
| 889 | 14 | `static DistrictPlan.Input accessInput(CityRuns runs)` | A plan's input with a yard, a sea terminal and a Truck Depot to place, and (with highways) their ramps given as the map gives them. |
| 905 | 5 | `static boolean beside(int[] box, int x, int y)` | Whether plot (x, y) is beside the box {bx, by, w, h}, across one of its sides. |
| 912 | 1 | `static boolean access(DistrictPlan p, int q)` | Whether plot q of the plan is one an access road runs over. |
| 915 | 5 | `static int accessPlots(DistrictPlan p)` | The plots the access roads run over that are streets with a surface to pay (not the ramp's, beneath a highway). |
| 922 | 15 | `static boolean chained(DistrictPlan p, int gx, int gy, int ex, int ey)` | Whether the access road runs unbroken from (gx, gy) to (ex, ey): a chain of plots of the access role, side to side. |
| 938 | 84 | `static void accessRoads()` |  |

### 15. kerb corners and estate T's (batch FR7, slice 4) (lines 1023-1108)

| line | len | member | says |
|---:|---:|---|---|
| 1026 | 4 | `static void buildingOf(TilePainter.Input in, int x, int y, int type)` | A one-plot building of the type at (x, y). |
| 1032 | 4 | `static int typeOfClass(int cls)` | The id of a type of the given class. |
| 1037 | 71 | `static void kerbCorners()` |  |

### 16. sidings into the bulk works the plans place (batch FR7, slice 5) (lines 1109-1252)

| line | len | member | says |
|---:|---:|---|---|
| 1118 | 10 | `static DistrictPlan.Input sidingInput(int row, int share, int bigs, int smalls)` | A plan's input with `bigs` and `smalls` bulk works to place and nothing else, an east-west main line along `row` (none for a negative row) of the runs' railway, and `share` sidings to lay. |
| 1130 | 1 | `static boolean siding(DistrictPlan p, int q)` | Whether plot q of the plan is one a siding runs over. |
| 1133 | 15 | `static boolean sidingChained(DistrictPlan p, int sx, int sy, int ex, int ey)` | Whether the siding runs unbroken from (sx, sy) to (ex, ey): a chain of siding plots, side to side. |
| 1149 | 103 | `static void sidings()` |  |

### 13. the loops (batch FR7, slice 4) (lines 1253-1335)

| line | len | member | says |
|---:|---:|---|---|
| 1256 | 4 | `static int atOffset(int[] img, int w, int jx, int jy, double u, double v, int px)` | The pixel of the tile at plot (junction x + u, junction y + v) in plots from the junction plot's middle, at px pixels a plot. |
| 1261 | 73 | `static void loopsDrawn()` |  |

