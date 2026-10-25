# EarthTerrainCheck.java - 2,506 lines · 60 methods · 50 constants · harnesses

`ham/citybuildersim/EarthTerrainCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key holding the generated world as it was, and the game's founding, land and screen standing on an Earth world - and since 0.7.117 (batch E3) its deposits on the Earth's geology (Geology), and since 0.7.127 (batch E7) its ice and desert.
> 
> WHY. E2 lays the real Earth's ground on the world's plots, finer than the
> data by 15 times, and the finer detail is made from the seed: a rule that
> kept the look of a coast and lost its share of sea would move every city's
> books, and one that drew the same detail for every seed would make every
> city founded on a spot the same. So the drawn ground is held to the data
> cell by 15" cell, and to the seed's detail moving it and nothing more; and
> because a generated world must reach exactly the code it reached before
> the key, that is held too (WorldCheck holds the generated world itself,
> unchanged).
> 
> THE DATA. EarthCheck's fixture (earth/earth-2-fixture.bin, cut from
> earth-2.bin by tools/earth/fixture.py) holds the 15" tiles of the study's
> three windows - Rotterdam, Perth and Paris - and Fiji's four on the 180th
> meridian, and the 4' bands they lie in; a window here is the world cell
> about the spot, 61.44 km a side, the study's "61 x 61 km about each spot".
> Every 1-degree tile it does not hold reads as no sea and no lake, so
> nothing here is drawn beyond them. The full file's section runs when it is
> named (-Dcbs.earth=<path>, EarthData.locate()), and says so in one line
> when it is not.
> 
> What this has to prove (spec-earth 8, E2):
>   1. A GENERATED WORLD THROUGH ITS KEY IS THE WORLD IT WAS: World.of() of a
>      generated key is World.of() of its seed, the same object; a world
>      made from the key draws the same site, totals, tiles, lines and region
>      as one made from the seed; an Earth world is kept apart, Earth's.
>   2. THE SHARES ARE THE DATA'S: on each window, every whole 15" cell's sea
>      share of the plots drawn is the data's within SHARE_POINTS in the
>      mean (the study measured 0.39 to 1.49); the windows' fresh water and
>      forest are printed against the data's.
>   3. THE SAME KEY, THE SAME GROUND: two worlds made from one key draw every
>      sampled tile byte for byte, and a plot, a line and a region at a
>      stride of one are their tile's.
>   4. ANOTHER SEED MOVES THE DETAIL, NOT THE DATA: a second seed changes at
>      least SEED_CHANGES of a window's plots (the study 15.7 to 28.6%) and
>      its water - sea and fresh - within SEED_WATER_POINTS (the study 0.1).
>   5. A TILE IN NO MORE THAN TILE_MS (the study 0.09 to 0.20 ms), the median
>      of TILE_ROUNDS rounds over each window's middle; wall clock, so run
>      under the JVM lock.
>   6. NO SEAM AT 180 DEGREES: Fiji's window, centred on the meridian, holds
>      whole cells on both sides of it, and the cells beside the line on
>      each side are the data's within SHARE_POINTS.
>   7. THE RULE'S PIECES: every river vertex in a window lies on a fresh (or
>      sea) plot; every small lake's point does, and the lakes clear of other
>      water draw their own area within LAKE_AREA; every beach is a dry plot
>      beside the sea in a 2' cell with bare ground, and Perth has beaches.
>   8. THE GAME STANDS ON IT: each window's founding site, the snap's, passes
>      the founding's test 1 within SNAP_PLOTS of the spot; the land a city is
>      founded with there (LandManager.foundingLand()) holds its dry ground
>      and, beside a river, its fresh water; a screen of MapCheck's
>      SCREEN_TILES on Rotterdam, of a square city at the 5B copy's density,
>      paints and rasters in no more than MapCheck.SCREEN_MS.
>   9. THE FULL FILE, when named: the cells pass in no more than
>      CELLS_PASS_MS of the main thread's CPU, the wall clock printed beside
>      it (batch EC1; the study 0.37 to 0.45 s); the world's land, its cells'
>      shares added, the land inside its square within LAND_WITHIN - every
>      4' cell of the globe projected, the antipode's cap past the square
>      left out; and the widest river EarthTerrain.WIDEST_RIVER_M, its half
>      within EarthTerrain.WATER_PAD_DEG.
> ... (114 more lines in the source)

**Uses:** [World](World.md) (626), [Geology](Geology.md) (112), [EarthData](EarthData.md) (101), [Resource](Resource.md) (82), [EarthTerrain](EarthTerrain.md) (59), [EarthSite](EarthSite.md) (46), [CityLand](CityLand.md) (34), [MapCheck](MapCheck.md) (23), [Deposit](Deposit.md) (21), [EarthProj](EarthProj.md) (19), [DataSave](DataSave.md) (15), [DistrictPlan](DistrictPlan.md) (12), [LandMap](LandMap.md) (10), [GameVersion](GameVersion.md) (10), [Timing](Timing.md) (9), [TileRaster](TileRaster.md) (8), [GridConversion](GridConversion.md) (8), [CityRuns](CityRuns.md) (8), [GlobeFrame](GlobeFrame.md) (7), [CityMap](CityMap.md) (6), [MapFrame](MapFrame.md) (6), [EarthCheck](EarthCheck.md) (5), [LandManager](LandManager.md) (5), [BuildingVisual](BuildingVisual.md) (5), [TilePainter](TilePainter.md) (5), [GlobePicture](GlobePicture.md) (5), [PlanCheck](PlanCheck.md) (3), [Founding](Founding.md) (2), [BuildingCatalog](BuildingCatalog.md) (2), [WorldCheck](WorldCheck.md) (1)... and 2 more

**Used by (4):** [GatewayCheck](GatewayCheck.md), [GlobeCheck](GlobeCheck.md), [MapCheck](MapCheck.md), [SlopeCheck](SlopeCheck.md)

## Sections

| line | section |
|---:|---|
| 313 | A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells |
| 436 | 1. A GENERATED WORLD THROUGH ITS KEY |
| 481 | 3. THE SAME KEY, THE SAME GROUND |
| 521 | 4. ANOTHER SEED |
| 553 | 5. A TILE'S TIME |
| 578 | 18. A TILE'S LANDMASS (ET5) |
| 613 | 6. NO SEAM AT 180 DEGREES |
| 646 | 7. THE RULE'S PIECES |
| 744 | 8. THE GAME STANDS ON IT |
| 829 | 9. THE FULL FILE |
| 926 | 10 TO 14. THE DEPOSITS ON THE EARTH'S GEOLOGY (0.7.117, batch E3; |
| 1416 | 15. ICE AND DESERT AS KINDS OF GROUND (0.7.127, batch E7; the |
| 1578 | 16. THE SHALLOW SEA (ET1, spec-earth2-terrain 6.1, D11 and 12's 6) |
| 1761 | 17. THE RELIEF PLANE (ET1, spec-earth2-terrain 3.1, D4, D5 and D19) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 193 | `EarthTerrainCheck.SHARE_POINTS` | `2` | Each whole 15" cell's sea share of the plots drawn is the data's within this, in points, in the mean over a window (spec-earth 8: 2; the study measured 0.39 to 1.49). |
| 196 | `EarthTerrainCheck.SEED_CHANGES` | `0.10` | A second seed changes at least this share of a window's plots (spec-earth 8: 10%; the study measured 15.7 to 28.6%). |
| 199 | `EarthTerrainCheck.SEED_WATER_POINTS` | `0.5` | ...while a window's water, sea and fresh, moves no more than this, in points (spec-earth 8: 0.5; the study measured 0.1). |
| 202 | `EarthTerrainCheck.TILE_MS` | `0.3` | A tile's ground in no more than this, ms (spec-earth 8: 0.3; the study measured 0.09 to 0.20). |
| 205 | `EarthTerrainCheck.LANDMASS_TILE_MS` | `0.3` | A tile's landmass (ET5) in no more than this, ms, its cover kept: the data's cell at each of its 1,024 plots' blend points, the nearest land cell's for a dry plot on a cell with none (the median of TILE_ROUNDS rounds ... |
| 208 | `EarthTerrainCheck.CELLS_PASS_MS` | `1000` | The cells pass in no more than this, ms of the main thread's CPU since batch EC1 (spec-earth 8: 1 s; the study measured 0.37 to 0.45 s on the wall clock). |
| 211 | `EarthTerrainCheck.WINDOW` | `World.CELL` | The window about each spot, in plots a side: a world cell, 61.44 km (spec-earth 3, "61 x 61 km about each spot"). |
| 214 | `EarthTerrainCheck.TIMED_TILES` | `32` | The tiles a side of each window's middle that are timed: 32, 30.7 km. |
| 217 | `EarthTerrainCheck.TILE_ROUNDS` | `3` | Rounds of the timed tiles after one to warm: 3, the median taken (the study's 3 repeats). |
| 220 | `EarthTerrainCheck.SEED` | `Founding.DEFAULT_WORLD_SEED, SECOND_SEED = Founding.DEFAULT_WORLD_SEED + 1` | The seed the windows are drawn with, and the second one section 4 draws them with. |
| 223 | `EarthTerrainCheck.SEAM_DEG` | `0.05` | How close to the 180th meridian Fiji's cells are held, in degrees: 0.05, twelve 15" cells (5.5 km) each side. |
| 226 | `EarthTerrainCheck.LAKE_AREA` | `0.10` | The small lakes clear of other water draw their own area within this share of it, over the windows: 10% (★E2: each blob covers its lake's area in expectation, and its fray varies it a few percent; measured in section 7). |
| 229 | `EarthTerrainCheck.LAND_WITHIN` | `0.005` | The world's land on the full file - its cells' shares added - is the land the 4' layers put inside the world's square within this share: half a percent (★E2: sixteen points a 61 km cell, each its 7.4 km 4' cell's, sum... |
| 233 | `EarthTerrainCheck.out` | `System.out` | Where the harness prints: the console. |
| 932 | `EarthTerrainCheck.STUDY_CLASSES` | `{ 55, 38, 151, 20, 66, 474, 219 }` | The study's count of the provinces by geology.py's rules (spec-earth 4): 474 basin, 151 fold belt, 66 shelf, 55 ocean, 38 shield, 20 rift and 219 other, in Geology.Kind's order. |
| 935 | `EarthTerrainCheck.STUDY_SHARES` | `{ 0.7, 17.8, 18.9, 2.2, 2.8, 35.1, 22.5 }` | The study's land in each class by the rules, Antarctica left out, in points (spec-earth 4), in Geology.Kind's order: 0.7 ocean, 17.8 shield, 18.9 fold belt, 2.2 rift, 2.8 shelf, 35.1 basin, 22.5 other. |
| 938 | `EarthTerrainCheck.SHARE_WITHIN` | `0.5` | ...reproduced within this, in points: half a point (★E3: the study's are rounded to a tenth, and its land was its prototype's cells). |
| 941 | `EarthTerrainCheck.STUDY_Z` | `{ 0.888, 0.701, 0.751, 0.959, 0.846, 1.049 }` | The study's Z on the rules' classes (spec-earth 4), in Resource's order for the six in fields: iron 0.888, oil 0.701, stone 0.751, coal 0.959, copper 0.846, uranium 1.049. |
| 944 | `EarthTerrainCheck.STUDY_LIMESTONE_BASIN` | `0.886, STUDY_LIMESTONE_SHIELD = 0.493` | The study's limestone in basins and on shields on the rules' classes (spec-materials 2.4, out/earth-types.txt): .886 and .493. |
| 947 | `EarthTerrainCheck.LIMESTONE_WITHIN` | `0.005` | ...reproduced within this: half a point (★E3: the study's are rounded to three places). |
| 950 | `EarthTerrainCheck.SIGMAS` | `3` | A sampled figure is held to its expectation within this many standard deviations: 3 (★E3). |
| 953 | `EarthTerrainCheck.Z_ROUNDING` | `0.00005` | A Z on the full file is Geology's constant to its rounding, four decimals: half the last place. |
| 956 | `EarthTerrainCheck.E3_Z` | `{ 0.7219, 0.6033, 0.6915, 0.7884, 0.7322, 0.7518 }` | E3's Z on the hand table (0.7.117, before EG1), in Resource's order for the six in fields: iron 0.7219, oil 0.6033, stone 0.6915, coal 0.7884, copper 0.7322, uranium 0.7518. |
| 959 | `EarthTerrainCheck.E3_Z_WITHIN` | `0.0001` | ...replayed on today's measure within this: a place and a half of the last (the Sydney patch, EG1, moves a little land, and copper's 0.73226 is E3's 0.7322 and a bit). |
| 962 | `EarthTerrainCheck.ODDS_ROUNDING` | `0.000005` | An odds on the full file is Geology's constant to its rounding, six figures: half the last place, relative. |
| 965 | `EarthTerrainCheck.YILGARN_KM` | `150` | How far east of Perth the study's shield spot lies, in km: 150 (spec-earth 4, "150 km east of Perth (Yilgarn Block)"). |
| 968 | `EarthTerrainCheck.TYPE_DRAWS` | `200_000` | Synthetic fields drawn for each class to hold the types' odds: 200,000 (one standard deviation about 0.1 point). |
| 971 | `EarthTerrainCheck.SEA_OIL_CELLS` | `8` | How far about Rotterdam's site the North Sea's cells are looked at for oil at sea, in cells: 8 (490 km), within the fixture's bands. |
| 1422 | `EarthTerrainCheck.COVER_POINTS` | `SHARE_POINTS` | Each kind's share of a 1-degree cell's ground drawn is the data's within this, in points: SHARE_POINTS, the bound the sea's share of a 15" cell is held to (spec-earth 8); E7 measured 0.02 to 0.34. |
| 1425 | `EarthTerrainCheck.ICE_CELLS` | `{ { 64, - 17 }, { 61, - 46 } }, DESERT_CELLS = { { 16, 0 }, { 44, 56 } }` | The 1-degree cells each kind is held on, {lat0, lon0}, on the full file: ice at Vatnajokull and on south Greenland's margin, the desert at the Sahel's edge and on the Ustyurt by the Aral Sea - each a cell with its kin... |
| 1428 | `EarthTerrainCheck.SHEET` | `{ 72, - 40 }` | Greenland's sheet's middle, EarthFoundingCheck 3's spot on ice. |
| 1431 | `EarthTerrainCheck.MARGIN` | `{ 61.48, - 45.68 }` | A spot on south Greenland's ice margin whose snapped site lies on grass in a 2' cell with the ice bit - the bit's rule (E4's) refused it, the plot's does not (found by E7's probe, the default seed; moved about 9 km fr... |
| 1434 | `EarthTerrainCheck.OFF_THE_ICE` | `{ 61.42, - 45.62 }` | A spot on the ice there whose site the snap finds off it, on dry ground over a kilometre away (E7's probe). |
| 1437 | `EarthTerrainCheck.SAHARA` | `{ 23, 10 }` | The Sahara's spot, EarthFoundingCheck 3's desert. |
| 1583 | `EarthTerrainCheck.SHALLOW_POINTS` | `COVER_POINTS` | The shallow share of a window's sea drawn is the data's within this, in points: COVER_POINTS, the bound ice's and the desert's share of a cell is held to (the sea-weighted share of cells whose coast the fray moves a p... |
| 1586 | `EarthTerrainCheck.TWO_COPIES_POINTS` | `0.5` | The tile rule (draw()) and the point rule (classAt()) split the sea alike on all but this share of the plots both call sea, in points: half a point (the lattice blends the shares between points 120 m apart, the point ... |
| 1589 | `EarthTerrainCheck.SPOT_REACH` | `5` | How far round a spot the Narrows' and the Basin's plots are all held to their class, in plots: 5 (150 m, a Narrows is a few hundred metres across). |
| 1592 | `EarthTerrainCheck.NARROWS` | `{ 44.67, - 63.59 }, BASIN = { 44.70, - 63.64 }` | The Halifax Narrows between Bedford Basin and the harbour, and Bedford Basin's middle (the fixture holds the Narrows' tiles: tools/earth/fixture.py NARROWS). |
| 1766 | `EarthTerrainCheck.RELIEF_POINTS` | `SHARE_POINTS` | Each relief class's share of a window's dry ground drawn is the data's within this, in points, for a window of RELIEF_REF_PLOTS dry plots: SHARE_POINTS, section 15's bound - and more for a window with less dry ground,... |
| 1769 | `EarthTerrainCheck.RELIEF_REF_PLOTS` | `(long) WINDOW * WINDOW` | The dry plots a 61 km window of land holds: 2,048 squared, 4,194,304 - the size RELIEF_POINTS is the bound for. |
| 1772 | `EarthTerrainCheck.RELIEF_COPIES_POINTS` | `1.0` | The relief's two copies (the tile's drawRelief() and the point's reliefOf()) give a dry plot the same class on all but this share of the plots both draw dry, in points: 1 (the lattice blends the shares between points ... |
| 1775 | `EarthTerrainCheck.FAR_POINTS` | `2 * RELIEF_POINTS` | A far node's relief shares are the data's cells' at its dry pixels within this, in points: twice RELIEF_POINTS (a node's 16,384 pixels are a sample, a pixel a hash's draw against the share), where it holds at least FA... |
| 1776 | `EarthTerrainCheck.FAR_DRY_LEAST` | `2000` |  |
| 1779 | `EarthTerrainCheck.FLOOR` | `{ 6.25, - 75.57 }, VALLEY = { 6.25, - 75.58 }` | Medellin's valley floor, the centre of the city (the study's 2.3: 98% flat in a 3 x 3 of 15" cells, 1,482 m), and the middle of the fixture's window about it (tools/earth/fixture.py MEDELLIN, one 1-degree tile). |
| 1782 | `EarthTerrainCheck.SUMMIT` | `{ 27.99, 86.93 }` | Everest's summit area: snow and ice on all its high ground (the study's 12, 7), the rock between its glaciers the only dry land. |
| 1785 | `EarthTerrainCheck.HILLS` | `{ VALLEY, SUMMIT, { 46.7, 10.3 }, { 22.3, 114.2 }, { - 16.5, - 68.1 }, { 27.7...` | The full file's hilly spots, {lat, lon}: Medellin, Everest's, the Upper Engadin, Hong Kong (a steep coast), La Paz and Kathmandu - their classes in the study's table of 2.3. |
| 1786 | `EarthTerrainCheck.HILL_NAMES` | `{ "Medellin", "Everest", "the Upper Engadin", "Hong Kong", "La Paz", "Kathman...` |  |
| 1789 | `EarthTerrainCheck.FLOOR_REACH` | `5, FLOOR_WIDE = 17` | How far round the floor's spot the plots are all held FLAT, in plots: 5 (150 m); and the share of the plots within FLOOR_WIDE (17: 510 m) that are flat, at least. |
| 1790 | `EarthTerrainCheck.FLOOR_FLAT` | `0.95` |  |
| 2332 | `EarthTerrainCheck.SHELVES` | `{ { 52.0, 3.0 }, { - 32.0, 115.4 }, { 28.5, - 94.0 }, { 27.0, 50.5 } }` | Four coasts of the full file, {lat, lon}, each a 61 km window with sea of both depths: the North Sea at the Flemish banks, the shelf off Perth, the Texas shelf off Galveston and the Gulf off Ras Tanura. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 231 | `static int fails` |  |
| 318 | `final World world` |  |
| 319 | `final long x0, y0` |  |
| 320 | `final byte[] plots` |  |
| 321 | `final long[] classes` |  |
| 323 | `final long rMin, cMin` | Cells counted: the window's 15" cells, rows from rMin and columns (unwrapped about the spot) from cMin. |
| 324 | `final int rows, cols` |  |
| 325 | `final int[] count, salt` |  |
| 326 | `final boolean[] edge` |  |
| 327 | `final int cells` |  |
| 328 | `final double seaPoints, dataSea, dataLake, dataForest` |  |
| 330 | `final double dataShallow` | The shallow share of the window's sea in the data (ET1): its cells' shallow levels weighted by each cell's sea and the plots laid on it; NaN where the file has no shallow layer or the window no sea. |
| 331 | `final long dryForestData` |  |
| 1798 | `final World world` |  |
| 1799 | `final EarthData data` |  |
| 1800 | `final long x0, y0` |  |
| 1801 | `final int side` |  |
| 1802 | `final byte[] cover, relief` |  |
| 1803 | `final long[] drawn` |  |
| 1804 | `final double[] want` |  |
| 1805 | `long dry, lost` |  |
| 1807 | `final long[] sloped` | Dry plots of each cover with a relief above FLAT, by cover class. |
| 2473 | `final EarthData d` |  |
| 2474 | `final int cpd` |  |
| 2475 | `final Map<Integer, byte[]> grids` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 190 | 2317 | **type** `public class EarthTerrainCheck` | The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key h... |
| 235 | 4 | `static void check(String label, boolean ok)` |  |
| 240 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 246 | 1 | **type** `record Spot(String name, double lat, double lon)` | The fixture's windows: name, latitude, longitude. |
| 248 | 58 | `public static void main(String[] args) throws Exception` |  |
| 307 | 5 | `static double timed(Runnable r)` |  |

### A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells (lines 313-435)

| line | len | member | says |
|---:|---:|---|---|
| 317 | 118 | **type** `static final class Window` |  |
| 333 | 72 | `Window(World world, EarthData data)` _(in EarthTerrainCheck.Window)_ |  |
| 407 | 4 | `double drawnShallow()` _(in EarthTerrainCheck.Window)_ | The shallow share of the sea drawn: SHALLOW plots over all the sea's, NaN where there is none. |
| 413 | 1 | `double share(byte cls)` _(in EarthTerrainCheck.Window)_ | A class's share of the window's plots. |
| 416 | 5 | `double forestOfDry()` _(in EarthTerrainCheck.Window)_ | Forest's share of the dry ground drawn. |
| 423 | 1 | `byte at(int px, int py)` _(in EarthTerrainCheck.Window)_ | The class of window plot (px, py). |
| 426 | 1 | `static long row(double lat)` _(in EarthTerrainCheck.Window)_ | The share of a cell's plots of every cell index (count, salt) for the cells within `deg` of a longitude, and on which side. |
| 428 | 6 | `static long col(double lon, double about)` _(in EarthTerrainCheck.Window)_ |  |

### 1. A GENERATED WORLD THROUGH ITS KEY (lines 436-480)

| line | len | member | says |
|---:|---:|---|---|
| 440 | 40 | `static void generated(String id)` |  |

### 3. THE SAME KEY, THE SAME GROUND (lines 481-520)

| line | len | member | says |
|---:|---:|---|---|
| 485 | 35 | `static void twice(Spot s, String id, Window w)` |  |

### 4. ANOTHER SEED (lines 521-552)

| line | len | member | says |
|---:|---:|---|---|
| 525 | 27 | `static void seeds(List<Spot> spots, String id, Map<String, Window> windows)` |  |

### 5. A TILE'S TIME (lines 553-577)

| line | len | member | says |
|---:|---:|---|---|
| 557 | 20 | `static void tileTime(List<Spot> spots, String id)` |  |

### 18. A TILE'S LANDMASS (ET5) (lines 578-612)

| line | len | member | says |
|---:|---:|---|---|
| 582 | 30 | `static void landmassTime(List<Spot> spots, String id)` |  |

### 6. NO SEAM AT 180 DEGREES (lines 613-645)

| line | len | member | says |
|---:|---:|---|---|
| 617 | 28 | `static void seam(Spot fiji, String id, EarthData data)` |  |

### 7. THE RULE'S PIECES (lines 646-743)

| line | len | member | says |
|---:|---:|---|---|
| 650 | 70 | `static void pieces(Map<String, Window> windows, EarthData data)` |  |
| 722 | 21 | `static boolean clearOfWater(Window w, EarthData data, EarthProj proj, double x, double y, double reach)` | Whether no sea, lake share or river reaches within `reach` plots of (x, y): the data's sea and lake there nought, and no plot drawn fresh by a river (a river vertex within reach plus its half-width). |

### 8. THE GAME STANDS ON IT (lines 744-828)

| line | len | member | says |
|---:|---:|---|---|
| 748 | 30 | `static void stands(List<Spot> spots, String id)` |  |
| 779 | 3 | `static boolean dryPlot(World w, long x, long y)` |  |
| 784 | 44 | `static void screen(Spot s, String id)` | MapCheck 5's screen on Earth: SCREEN_ACROSS x SCREEN_DOWN tiles about the site of a square city at the 5B copy's density, each tile's ground read from the world, painted and rastered. |

### 9. THE FULL FILE (lines 829-925)

| line | len | member | says |
|---:|---:|---|---|
| 853 | 3 | `static long passClock()` | The clock the cells pass's bound reads, in ns: the main thread's CPU, or the wall clock where MapCheck.CPU_TIMED is false. |
| 858 | 67 | `static EarthData full(Spot s) throws Exception` | Section 9; returns the full file's reader when it is named, else null (section 13 reads it). |

### 10 TO 14. THE DEPOSITS ON THE EARTH'S GEOLOGY (0.7.117, batch E3; (lines 926-1415)

| line | len | member | says |
|---:|---:|---|---|
| 974 | 1 | **type** `record GeoSpot(String name, double lat, double lon, String province, Geology.Kind kind, double knownMmboe, ...` | One of the study's spots: its place, its province and class, and the multipliers it measured (spec-earth 4). |
| 977 | 42 | `static void classes(EarthData data)` | Section 10: the provinces' classes, the rules' and the hand-checked table over them - on the fixture, which holds the whole geology. |
| 1021 | 9 | `static String kinds(int[] by)` | A count by class, in Geology.Kind's order, as words. |
| 1032 | 11 | `static List<GeoSpot> geoSpots(Map<String, Spot> spots, String id)` | The study's spots: the three windows' and the shield's, 150 km east of Perth on the Perth world's plots. |
| 1051 | 15 | `static double eg1Multiplier(Geology g, EarthData.Province p, Resource r, double lat, double lon)` | A resource's multiplier for a province by EG1's rules (0.8.12; spec-earth2-terrain 8.5), worked out here from Geology's constants and the province's USGS code, not by asking Geology's tables: oil's weight is OIL_ALLOW... |
| 1068 | 46 | `static void spots(EarthData data, String id, Map<String, Spot> spots)` | Section 11: the study's spots - their provinces and classes, the study's multipliers at its Z, and the model's at its own. |
| 1127 | 69 | `static void byClass(World w, String where)` | Section 12: iron's fields per land km2 by class, on a world's PURE cells - those with no point in a recorded iron district (EG1) whose field land is their land x the weight x Z of the class at their centre for iron an... |
| 1198 | 7 | `static boolean pureIn(World w, int c, double land, Geology.Kind k)` | Whether a cell's land is all of class k: its field land for iron and for copper each its land x the weight x Z there (Geology's constants, iron's the EG1 weight; the field land is kept as a float). |
| 1207 | 14 | `static boolean districtPointIn(World w, int c)` | Whether any of a cell's CELL_SAMPLES x CELL_SAMPLES points - the ones the cells pass reads - lies in a recorded iron district (Geology.districtAt(), EG1). |
| 1223 | 67 | `static void planet(EarthData file, Spot rotterdam)` | Section 13, on the full file: Z and the odds are its own to their rounding, and the study's figures on the rules' classes; section 12 on the whole planet. |
| 1292 | 123 | `static void types(String id, Map<String, Spot> spots)` | Section 14: the totals exact on Earth and every field typed by the class under it. |

### 15. ICE AND DESERT AS KINDS OF GROUND (0.7.127, batch E7; the (lines 1416-1577)

| line | len | member | says |
|---:|---:|---|---|
| 1439 | 138 | `static void iceAndDesert(EarthData file, Map<String, Window> windows, String id)` |  |

### 16. THE SHALLOW SEA (ET1, spec-earth2-terrain 6.1, D11 and 12's 6) (lines 1578-1760)

| line | len | member | says |
|---:|---:|---|---|
| 1594 | 166 | `static void shallowSea(EarthData file, Map<String, Window> windows, EarthData fixture, String id) throws Exception` |  |

### 17. THE RELIEF PLANE (ET1, spec-earth2-terrain 3.1, D4, D5 and D19) (lines 1761-2506)

| line | len | member | says |
|---:|---:|---|---|
| 1797 | 79 | **type** `static final class ReliefWindow` | A window of plots - the world cell's middle, side x side of them - drawn (cover and relief) and laid on the data's slope: its dry plots counted by relief against what the data's cells say of them (each plot's own 15" ... |
| 1809 | 41 | `ReliefWindow(World world, EarthData data, int side)` _(in EarthTerrainCheck.ReliefWindow)_ |  |
| 1851 | 1 | `double drawnShare(int k)` _(in EarthTerrainCheck.ReliefWindow)_ |  |
| 1853 | 1 | `double wantShare(int k)` _(in EarthTerrainCheck.ReliefWindow)_ |  |
| 1856 | 5 | `double points()` _(in EarthTerrainCheck.ReliefWindow)_ | The largest gap between a class's share drawn and the data's, in points. |
| 1863 | 1 | `double tolerance()` _(in EarthTerrainCheck.ReliefWindow)_ | The bound on points(): RELIEF_POINTS, and past it by the root of the window's dry ground against RELIEF_REF_PLOTS. |
| 1866 | 1 | `boolean hilly()` _(in EarthTerrainCheck.ReliefWindow)_ | Whether the data holds slope under the window: at least 5% of its dry ground at or above foothill. |
| 1869 | 1 | `boolean steep()` _(in EarthTerrainCheck.ReliefWindow)_ | Whether it holds steep ground: at least 5% of its dry ground at or above steep. |
| 1871 | 4 | `String shares(boolean data)` _(in EarthTerrainCheck.ReliefWindow)_ |  |
| 1878 | 12 | `static long[] reliefAbout(World w, double lat, double lon, int reach)` | The relief about a point (its lat, lon), plots dx, dy of it, for a window's world: counted by class over the dry plots within `reach`. |
| 1891 | 132 | `static void reliefPlane(EarthData file, Map<String, Spot> spots, EarthData fixture, String id) throws Exception` |  |
| 2025 | 27 | `static void reliefWindows(String where, List<String> names, List<ReliefWindow> ws, boolean fixtureWindows)` | The share check on each window, the checks that hold of all of them, and which hold slope. |
| 2054 | 134 | `static void reliefRule(EarthData fixture, EarthData file, String id) throws Exception` | The rule's own: the cover untouched, the readers one another's, the classes nested within the data, the point rule the tile's, a tile's time. |
| 2190 | 140 | `static void reliefFar(EarthData file) throws Exception` | The far views' relief: a far node at a stride, the globe's camera, the picture's, their cost against SCREEN_MS and FRAME_MS. |
| 2335 | 1 | `static int luma(int c)` | A colour's luma, the plain sum of its channels. |
| 2338 | 5 | `static long[] only(byte cls, long n)` | A count of n plots of one class, World.CLASSES long. |
| 2345 | 3 | `static boolean adds(EarthSite.Card c)` | A card's plots add up: dry, fresh, sea and ice. |
| 2350 | 5 | `static int westOf(DistrictPlan p)` | How many of a plan's buildings stand west of its district's middle. |
| 2357 | 16 | `static long[] tileOf(World w, CityLand land, byte cls)` | The nearest whole tile to a city's founding site, ring by ring of tiles out to 48 (46 km), that the city owns no plot of and whose every plot is of one class: its north-west plot, or null. |
| 2375 | 16 | `static boolean far(World.Key key, byte cls, String[] words)` | Whether the far nodes about a world's spot draw a class: every pixel of it at WATER_STRIDE and FAR_STRIDE / 4 (61 and 246 km a node), and at FAR_STRIDE (983 km, which reaches past the sheet's coast) more of it than of... |
| 2402 | 68 | `static void cover(EarthData d, boolean ice, int lat0, int lon0)` | One kind's 1-degree cell on the full file, on a world centred there: every plot whose centre lies in the cell drawn, and among those the kind could take (ice any ground not water; the desert grass or desert, which for... |
| 2472 | 34 | **type** `static final class Cover` | Land cover's 2' cells read from the file's grids, kept by tile: a cell's value, and a bit's share between the cells' centres as EarthTerrain reads it. |
| 2477 | 4 | `Cover(EarthData d)` _(in EarthTerrainCheck.Cover)_ |  |
| 2483 | 7 | `int cell(long r, long c)` _(in EarthTerrainCheck.Cover)_ | The value of the cell in global row r (from 90 N) and column c (from 180 W). |
| 2492 | 3 | `int at(double lat, double lon)` _(in EarthTerrainCheck.Cover)_ | The value of the cell a point lies in. |
| 2497 | 8 | `double share(double lat, double lon, int bit)` _(in EarthTerrainCheck.Cover)_ | A bit read 0 or 1 at each cell and interpolated between the four cells' centres about a point. |

