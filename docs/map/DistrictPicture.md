# DistrictPicture.java - 309 lines · 17 methods · 15 constants · model

`ham/citybuildersim/DistrictPicture.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> A district's picture for the far views: 32 x 32 pixels, a pixel for 8 x 8 plots (240 m), each the colour of one of its 64 plots - the plot a fixed hash picks from them ranked by class - drawn from the district's own plan as the painter reads it, so a far view shows where things stand, each kind in proportion to the ground it stands on, the same from month to month.
> 
> WHY THIS EXISTS (0.7.128, batch MM1; docs/studies/spec-globe.md 8.1 and
> 8.2). Past the painted tiles the far view drew each district one tint
> (MapTiles' tint, until 0.7.127): the class whose buildings took the
> most ground in it, at TileRaster.BLOCK_ALPHA by how much of its ground they
> used. A district is 256 plots, 7.68 km, so a city of a million on a dozen
> districts was a dozen flat squares at every size - Jerus, 2026-10-11:
> "at a certain city size you might just see a blob of blue and a blob of
> purple and thats it". The study's mockup (docs/studies/minimap/) drew a
> picture of each district instead, a pixel picked from the plots under it,
> and the core, the rings of homes and the industry on the port came out.
> 
> WHAT A PIXEL IS. Its 64 plots are counted by code (plotCodes(): the ground,
> a building by its fill, a paved street, the city's highways and track),
> the codes taken in their rank (GROUND first, the buildings in
> BuildingVisual's order and the fills of their own, then the paved
> streets, the highways and the track), and the plot at rank k is
> the pixel's, k a fixed hash of the pixel's place in the world over 0 to
> 63. Over a uniform hash a pixel is a code with the chance its plots give
> it, so a picture's shares are the district's in expectation, within a
> binomial spread of a pixel's (spread()); and the hash stands still, so a
> pixel moves only when the plots under it do. A GROUND pixel is none of
> the picture's own: the far node shows its own ground there (water, forest,
> the grass), dimmed where the city does not own it.
> 
> DRAWN WITH ITS PLAN, KEPT BY ITS STAMP: CityMap's PlanJob draws it from the
> plan it has just drawn and the ground it read for it (DistrictPlan.Input),
> on whatever thread runs the job - the map view's worker, "city-map", or a
> harness's own - and CityMap keeps it with the plan's link, under a stamp of
> the plots it is drawn from (stampOf()). A plan drawn again to the same
> plots - its packed plan let go from CityMap.PLANS_KEPT, or drawn again
> after a district before it moved (its link's stamp: its own inputs, its
> purchases and what was carried to it, and those of the plans it read) -
> keeps the picture it has. Not saved (star MM1-1): it is the plan's, which
> is the counts'.
> 
> Pure: the same plan and stamp give the same picture.

**Uses:** [BuildingVisual](BuildingVisual.md) (34), [World](World.md) (11), [DistrictPlan](DistrictPlan.md) (9), [TileRaster](TileRaster.md) (4), [CityMap](CityMap.md) (1)

**Used by (3):** [CityMap](CityMap.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md)

## Sections

| line | section |
|---:|---|
| 59 | · a plot's code, in rank order |
| 122 | · the picture |
| 268 | · a district too small for its picture |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 48 | `DistrictPicture.PLOTS_A_PIXEL` | `TileRaster.BLOCK_FAR` | Plots a pixel takes, a side: 8 (240 m, the study's), TileRaster.BLOCK_FAR - the far view's blocks at L2, so a picture's pixels lie on the blocks the closer view draws. |
| 51 | `DistrictPicture.SIDE` | `World.DISTRICT / PLOTS_A_PIXEL` | Pixels a side: 32, a district's World.DISTRICT plots at PLOTS_A_PIXEL. |
| 54 | `DistrictPicture.PIXELS` | `SIDE * SIDE` | Pixels in a picture: 1,024. |
| 57 | `DistrictPicture.PLOTS_IN` | `PLOTS_A_PIXEL * PLOTS_A_PIXEL` | The plots a pixel is picked from: 64. |
| 62 | `DistrictPicture.GROUND` | `0` | Nothing the picture draws: the ground, owned or not - the far node shows its own there. |
| 64 | `DistrictPicture.FIRST_CLASS` | `1` | A building of a class, HOME to TRANSPORT: its class + FIRST_CLASS (BuildingVisual.FILL's colour). |
| 66 | `DistrictPicture.FLATS` | `FIRST_CLASS + BuildingVisual.CLASSES` | ...the flats (BuildingVisual.FLATS_FILL)... |
| 68 | `DistrictPicture.CAMPUS` | `FLATS + 1, TERMINAL = FLATS + 2, TANKS = FLATS + 3` | ...the refinery's units (CAMPUS_FILL), a sea terminal (TERMINAL_FILL) and a tank farm (TANKS_FILL): each type's own fill, as the painter draws it (BuildingVisual.Type.fill()). |
| 70 | `DistrictPicture.WORKS` | `TANKS + 1` | ...since 0.8.0 (batch I14) each processing industry's works, Iron & Steel's to Wood Products' (BuildingVisual.GROUP_FILL, STEEL to WOOD), at WORKS + its group - STEEL... |
| 72 | `DistrictPicture.WASH` | `WORKS + BuildingVisual.GROUPS - BuildingVisual.STEEL, CAMP = WASH + 1, STAND ...` | ...and the coal wash plant, a logging camp and planted forest (BuildingVisual.LOOK_FILL): the materials' boxes that are not on a site (a mine on its site is the plan's SITE_MINED, Mines and wells). |
| 74 | `DistrictPicture.PAVED` | `STAND + 1` | A paved street of the plan, in TileRaster's colour below LINES_FROM px a plot (ROAD_SMALL's paved: its casing) - its gravel streets and tracks are the ground's, hidden as the far view's blocks hide them at L2 (TileRas... |
| 76 | `DistrictPicture.HIGHWAY` | `STAND + 2, RAIL = STAND + 3` | ...and a highway's plot - the city's runs' Elevated Highway, or a street of highway plots - and the railway's track: ROAD_SMALL's highway and RAIL_DARK. |
| 78 | `DistrictPicture.CODES` | `RAIL + 1` | How many codes. |
| 81 | `DistrictPicture.COLOUR` | `new int [ CODES ]` | Each code's colour, 0xAARRGGBB - the painter's (BuildingVisual's fills, TileRaster's roads below LINES_FROM); 0 for GROUND, which is the node's own. |
| 152 | `DistrictPicture.BYTES` | `PIXELS + 16 + 24` | The bytes it holds, about: its codes, their array's header and the picture's own. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 125 | `public final long stamp` | The stamp of the plots it was drawn from (stampOf()): what it is kept against. |
| 128 | `private final byte[] codes` | Each pixel's code, row by row. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 45 | 265 | **type** `public final class DistrictPicture` | A district's picture for the far views: 32 x 32 pixels, a pixel for 8 x 8 plots (240 m), each the colour of one of its 64 plots - the plot a fixed hash picks from them ranked by class - drawn from the district's own p... |

### a plot's code, in rank order (lines 59-121)

| line | len | member | says |
|---:|---:|---|---|
| 82 | 14 | `static { ... }` |  |
| 98 | 1 | `public static int colour(int code)` | A code's colour (0 for GROUND: none of the picture's own). |
| 101 | 1 | `public static boolean isBuilding(int code)` | Whether a code is a building's (FIRST_CLASS to STAND): what a picture's class shares are counted over. |
| 104 | 8 | `public static byte codeOf(BuildingVisual.Type t)` | A type's code: its fill's - its class's, the flats', or the refinery's units', a terminal's or a tank farm's own, since 0.8.0 a works' group's and the wash plant's, a camp's and planted forest's (BuildingVisual.Type.f... |
| 114 | 7 | `static byte streetCode(int kind)` | A plan's street's code by its kind (DistrictPlan's): a paved street PAVED, one of highway plots HIGHWAY; gravel, a track, a seam (a track where no district surfaced it, CityMap.streetAt()) and a street beneath a highw... |

### the picture (lines 122-267)

| line | len | member | says |
|---:|---:|---|---|
| 130 | 4 | `private DistrictPicture(long stamp, byte[] codes)` |  |
| 136 | 1 | `public int code(int x, int y)` | Pixel (x, y)'s code, 0 to SIDE - 1 each way. |
| 139 | 1 | `public int codeAtPlot(int lx, int ly)` | The code at plot (lx, ly) of the district, 0 to World.DISTRICT - 1 each way: the pixel's that holds it. |
| 142 | 5 | `public int[] counts()` | Its pixels by code. |
| 149 | 1 | `public boolean samePixels(DistrictPicture o)` | Whether another picture has the same pixels. |
| 166 | 26 | `public static byte[] plotCodes(DistrictPlan.Input in, DistrictPlan p, BuildingVisual.Type[] types, int[] extras)` | Each plot of a district's plan by code, row by row over its World.DISTRICT x World.DISTRICT (its frame less the east column and south row, the next districts'), as the painter would draw them: the plan's paved streets... |
| 194 | 6 | `private static void box(byte[] out, int x0, int y0, int w, int h, byte code)` | A box of plots set to a code, clipped to the district. |
| 202 | 10 | `public static long stampOf(byte[] plots, long seed, long x0, long y0)` | A picture's stamp: its plots' codes (plotCodes()) folded, with the world's seed and the district's north-west plot, which its pixels' ranks are drawn on - the same plots there, the same picture. |
| 214 | 5 | `public static int rankAt(long seed, long px, long py)` | The rank a pixel picks among its PLOTS_IN plots, 0 to PLOTS_IN - 1: a fixed hash of where it lies in the world, its column and row of PLOTS_A_PIXEL plots (px, py), on the world's seed. |
| 226 | 19 | `public static DistrictPicture of(byte[] plots, long stamp, long seed, long x0, long y0)` | A district's picture from its plots' codes (plotCodes()), their `stamp` (stampOf()), the world's seed and the district's north-west plot in the world (x0, y0): each pixel the code at its rank (rankAt()) among its plot... |
| 252 | 15 | `public static double spread(byte[] plots, int code)` | The spread a picture's share of a code may take from its plots' share over a uniform hash, one standard deviation: each pixel is the code with the chance q its own plots give it, so the share's variance is the sum of ... |

### a district too small for its picture (lines 268-309)

| line | len | member | says |
|---:|---:|---|---|
| 280 | 20 | `public static double[] shares(CityMap.District d, BuildingVisual.Type[] types)` | A district's shares of its owned dry ground by code, from its counts (the study's second rule, for a district drawn under MapTiles.PICTURE_FROM_PX pixels across): each drawn type's buildings on their whole plots (Buil... |
| 302 | 7 | `public static int pick(double[] shares, double u)` | The code a node's pixel takes from a district's shares at a hash u, 0 to 1: the code whose share u falls in, in code order, GROUND past them all. |

