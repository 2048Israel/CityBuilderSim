# JunctionCheck.java - 447 lines · 25 methods · 4 constants · harnesses

`ham/citybuildersim/JunctionCheck.java` - generated 2026-10-18 by CodeMap; line numbers are as of that run.

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
> 
> Numbering is the running order.

**Uses:** [TilePainter](TilePainter.md) (54), [TileRaster](TileRaster.md) (36), [BuildingVisual](BuildingVisual.md) (12), [CityRuns](CityRuns.md) (9), [DistrictPlan](DistrictPlan.md) (3)

## Sections

| line | section |
|---:|---|
| 164 | 1. signals |
| 210 | 2. a roundabout |
| 244 | 3. a diamond |
| 284 | 4. a highway arm's end |
| 316 | 5. a stack |
| 347 | 6. below JUNCTIONS_FROM |
| 369 | 7. a roundabout across a tile's edge |
| 387 | 8. a diamond across the tile's edge |
| 419 | 9. a ramp mid-highway with no street beneath |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `JunctionCheck.T` | `TilePainter.TILE` |  |
| 92 | `JunctionCheck.ARTERIAL` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_ARTERIAL)` |  |
| 93 | `JunctionCheck.BOULEVARD` | `streetCode(DistrictPlan.PAVED, 1, TilePainter.S_BOULEVARD)` |  |
| 94 | `JunctionCheck.STREET` | `streetCode(DistrictPlan.PAVED, 1, 0)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 56 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 54 | 394 | **type** `public class JunctionCheck` | Proves that the junctions and interchanges of spec-freight 4.6 are drawn by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19). |
| 58 | 4 | `static void check(String label, boolean ok)` |  |
| 66 | 6 | `static int[] raster(TilePainter.Input in, TilePainter.Painted p, int px)` | The image a painted tile rasters to at px pixels a plot. |
| 74 | 5 | `static TilePainter.Input owned()` | A tile whose plots are all owned, with no buildings and no sites. |
| 81 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` | Paints `in` into a fresh Painted. |
| 88 | 3 | `static int streetCode(int kind, int width, int role)` | A street code: kind in the low bits, then width and the painter's role. |
| 97 | 3 | `static void streetColumn(TilePainter.Input in, int x, int code)` | Sets a column of street plots to `code`. |
| 102 | 3 | `static void streetRow(TilePainter.Input in, int y, int code)` | Sets a row of street plots to `code`. |
| 107 | 3 | `static void highwayRow(TilePainter.Input in, int y)` | Sets a row of highway plots. |
| 112 | 3 | `static void highwayRowTo(TilePainter.Input in, int y, int last)` | Sets a row of highway plots ending at `last` (exclusive). |
| 117 | 3 | `static void highwayColumn(TilePainter.Input in, int x)` | Sets a column of highway plots. |
| 122 | 6 | `static void building(TilePainter.Input in, int x, int y)` | Adds a one-plot building (an industry's fill) at (x, y). |
| 130 | 3 | `static int pixel(int[] img, int w, int plotX, int plotY, int dx, int dy, int px)` | One pixel of plot (plotX, plotY), its local (dx, dy) at px pixels a plot. |
| 135 | 6 | `static boolean hasColour(int[] img, int w, int plotX, int plotY, int px, int colour)` | Whether plot (plotX, plotY) has any pixel of `colour` at px pixels a plot. |
| 143 | 6 | `static boolean noneOf(int[] img, int...colours)` | Whether the whole image has none of the given colours. |
| 150 | 13 | `public static void main(String[] args)` |  |

### 1. signals (lines 164-209)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 44 | `static void signals()` |  |

### 2. a roundabout (lines 210-243)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 32 | `static void roundabout()` |  |

### 3. a diamond (lines 244-283)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 38 | `static void diamond()` |  |

### 4. a highway arm's end (lines 284-315)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 26 | `static void highwayEnd()` |  |
| 312 | 3 | `static int crossSectionPaved(int px)` |  |

### 5. a stack (lines 316-346)

| line | len | member | says |
|---:|---:|---|---|
| 317 | 29 | `static void stack()` |  |

### 6. below JUNCTIONS_FROM (lines 347-368)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 20 | `static void belowJunctionsFrom()` |  |

### 7. a roundabout across a tile's edge (lines 369-386)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 16 | `static void roundaboutAcrossEdge()` |  |

### 8. a diamond across the tile's edge (lines 387-418)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 30 | `static void diamondAcrossEdge()` |  |

### 9. a ramp mid-highway with no street beneath (lines 419-447)

| line | len | member | says |
|---:|---:|---|---|
| 420 | 26 | `static void rampMidHighway()` |  |

