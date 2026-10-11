# EarthTerrainCheck.java - 1,186 lines · 29 methods · 25 constants · harnesses

`ham/citybuildersim/EarthTerrainCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key holding the generated world as it was, and the game's founding, land and screen standing on an Earth world - and since 0.7.117 (batch E3) its deposits on the Earth's geology (Geology).
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
> THE DATA. EarthCheck's fixture (earth/earth-1-fixture.bin, cut from
> earth-1.bin by tools/earth/fixture.py) holds the 15" tiles of the study's
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
>      CELLS_PASS_MS (the study 0.37 to 0.45 s); the world's land, its cells'
>      shares added, the land inside its square within LAND_WITHIN - every
>      4' cell of the globe projected, the antipode's cap past the square
>      left out; and the widest river EarthTerrain.WIDEST_RIVER_M, its half
>      within EarthTerrain.WATER_PAD_DEG.
> 
> ... (37 more lines in the source)

**Uses:** [World](World.md) (202), [Geology](Geology.md) (96), [Resource](Resource.md) (77), [EarthData](EarthData.md) (39), [EarthTerrain](EarthTerrain.md) (21), [Deposit](Deposit.md) (21), [MapCheck](MapCheck.md) (18), [EarthProj](EarthProj.md) (9), [CityLand](CityLand.md) (6), [CityMap](CityMap.md) (6), [EarthCheck](EarthCheck.md) (5), [LandManager](LandManager.md) (5), [TilePainter](TilePainter.md) (5), [BuildingVisual](BuildingVisual.md) (3), [Founding](Founding.md) (2), [WorldCheck](WorldCheck.md) (1), [BuildingCatalog](BuildingCatalog.md) (1), [TileRaster](TileRaster.md) (1)

**Used by (1):** [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 228 | A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells |
| 340 | 1. A GENERATED WORLD THROUGH ITS KEY |
| 385 | 3. THE SAME KEY, THE SAME GROUND |
| 425 | 4. ANOTHER SEED |
| 457 | 5. A TILE'S TIME |
| 482 | 6. NO SEAM AT 180 DEGREES |
| 515 | 7. THE RULE'S PIECES |
| 613 | 8. THE GAME STANDS ON IT |
| 699 | 9. THE FULL FILE |
| 766 | 10 TO 14. THE DEPOSITS ON THE EARTH'S GEOLOGY (0.7.117, batch E3; |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 115 | `EarthTerrainCheck.SHARE_POINTS` | `2` | Each whole 15" cell's sea share of the plots drawn is the data's within this, in points, in the mean over a window (spec-earth 8: 2; the study measured 0.39 to 1.49). |
| 118 | `EarthTerrainCheck.SEED_CHANGES` | `0.10` | A second seed changes at least this share of a window's plots (spec-earth 8: 10%; the study measured 15.7 to 28.6%). |
| 121 | `EarthTerrainCheck.SEED_WATER_POINTS` | `0.5` | ...while a window's water, sea and fresh, moves no more than this, in points (spec-earth 8: 0.5; the study measured 0.1). |
| 124 | `EarthTerrainCheck.TILE_MS` | `0.3` | A tile's ground in no more than this, ms (spec-earth 8: 0.3; the study measured 0.09 to 0.20). |
| 127 | `EarthTerrainCheck.CELLS_PASS_MS` | `1000` | The cells pass in no more than this, ms (spec-earth 8: 1 s; the study measured 0.37 to 0.45 s). |
| 130 | `EarthTerrainCheck.WINDOW` | `World.CELL` | The window about each spot, in plots a side: a world cell, 61.44 km (spec-earth 3, "61 x 61 km about each spot"). |
| 133 | `EarthTerrainCheck.TIMED_TILES` | `32` | The tiles a side of each window's middle that are timed: 32, 30.7 km. |
| 136 | `EarthTerrainCheck.TILE_ROUNDS` | `3` | Rounds of the timed tiles after one to warm: 3, the median taken (the study's 3 repeats). |
| 139 | `EarthTerrainCheck.SEED` | `Founding.DEFAULT_WORLD_SEED, SECOND_SEED = Founding.DEFAULT_WORLD_SEED + 1` | The seed the windows are drawn with, and the second one section 4 draws them with. |
| 142 | `EarthTerrainCheck.SEAM_DEG` | `0.05` | How close to the 180th meridian Fiji's cells are held, in degrees: 0.05, twelve 15" cells (5.5 km) each side. |
| 145 | `EarthTerrainCheck.LAKE_AREA` | `0.10` | The small lakes clear of other water draw their own area within this share of it, over the windows: 10% (★E2: each blob covers its lake's area in expectation, and its fray varies it a few percent; measured in section 7). |
| 148 | `EarthTerrainCheck.LAND_WITHIN` | `0.005` | The world's land on the full file - its cells' shares added - is the land the 4' layers put inside the world's square within this share: half a percent (★E2: sixteen points a 61 km cell, each its 7.4 km 4' cell's, sum... |
| 152 | `EarthTerrainCheck.out` | `System.out` | Where the harness prints: the console. |
| 772 | `EarthTerrainCheck.STUDY_CLASSES` | `{ 55, 38, 151, 20, 66, 474, 219 }` | The study's count of the provinces by geology.py's rules (spec-earth 4): 474 basin, 151 fold belt, 66 shelf, 55 ocean, 38 shield, 20 rift and 219 other, in Geology.Kind's order. |
| 775 | `EarthTerrainCheck.STUDY_SHARES` | `{ 0.7, 17.8, 18.9, 2.2, 2.8, 35.1, 22.5 }` | The study's land in each class by the rules, Antarctica left out, in points (spec-earth 4), in Geology.Kind's order: 0.7 ocean, 17.8 shield, 18.9 fold belt, 2.2 rift, 2.8 shelf, 35.1 basin, 22.5 other. |
| 778 | `EarthTerrainCheck.SHARE_WITHIN` | `0.5` | ...reproduced within this, in points: half a point (★E3: the study's are rounded to a tenth, and its land was its prototype's cells). |
| 781 | `EarthTerrainCheck.STUDY_Z` | `{ 0.888, 0.701, 0.751, 0.959, 0.846, 1.049 }` | The study's Z on the rules' classes (spec-earth 4), in Resource's order for the six in fields: iron 0.888, oil 0.701, stone 0.751, coal 0.959, copper 0.846, uranium 1.049. |
| 784 | `EarthTerrainCheck.STUDY_LIMESTONE_BASIN` | `0.886, STUDY_LIMESTONE_SHIELD = 0.493` | The study's limestone in basins and on shields on the rules' classes (spec-materials 2.4, out/earth-types.txt): .886 and .493. |
| 787 | `EarthTerrainCheck.LIMESTONE_WITHIN` | `0.005` | ...reproduced within this: half a point (★E3: the study's are rounded to three places). |
| 790 | `EarthTerrainCheck.SIGMAS` | `3` | A sampled figure is held to its expectation within this many standard deviations: 3 (★E3). |
| 793 | `EarthTerrainCheck.Z_ROUNDING` | `0.00005` | A Z on the full file is Geology's constant to its rounding, four decimals: half the last place. |
| 796 | `EarthTerrainCheck.ODDS_ROUNDING` | `0.000005` | An odds on the full file is Geology's constant to its rounding, six figures: half the last place, relative. |
| 799 | `EarthTerrainCheck.YILGARN_KM` | `150` | How far east of Perth the study's shield spot lies, in km: 150 (spec-earth 4, "150 km east of Perth (Yilgarn Block)"). |
| 802 | `EarthTerrainCheck.TYPE_DRAWS` | `200_000` | Synthetic fields drawn for each class to hold the types' odds: 200,000 (one standard deviation about 0.1 point). |
| 805 | `EarthTerrainCheck.SEA_OIL_CELLS` | `8` | How far about Rotterdam's site the North Sea's cells are looked at for oil at sea, in cells: 8 (490 km), within the fixture's bands. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 150 | `static int fails` |  |
| 233 | `final World world` |  |
| 234 | `final long x0, y0` |  |
| 235 | `final byte[] plots` |  |
| 236 | `final long[] classes` |  |
| 238 | `final long rMin, cMin` | Cells counted: the window's 15" cells, rows from rMin and columns (unwrapped about the spot) from cMin. |
| 239 | `final int rows, cols` |  |
| 240 | `final int[] count, salt` |  |
| 241 | `final boolean[] edge` |  |
| 242 | `final int cells` |  |
| 243 | `final double seaPoints, dataSea, dataLake, dataForest` |  |
| 244 | `final long dryForestData` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 112 | 1075 | **type** `public class EarthTerrainCheck` | The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key h... |
| 154 | 4 | `static void check(String label, boolean ok)` |  |
| 159 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 165 | 1 | **type** `record Spot(String name, double lat, double lon)` | The fixture's windows: name, latitude, longitude. |
| 167 | 54 | `public static void main(String[] args) throws Exception` |  |
| 222 | 5 | `static double timed(Runnable r)` |  |

### A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells (lines 228-339)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 107 | **type** `static final class Window` |  |
| 246 | 70 | `Window(World world, EarthData data)` _(in EarthTerrainCheck.Window)_ |  |
| 318 | 1 | `double share(byte cls)` _(in EarthTerrainCheck.Window)_ | A class's share of the window's plots. |
| 321 | 4 | `double forestOfDry()` _(in EarthTerrainCheck.Window)_ | Forest's share of the dry ground drawn. |
| 327 | 1 | `byte at(int px, int py)` _(in EarthTerrainCheck.Window)_ | The class of window plot (px, py). |
| 330 | 1 | `static long row(double lat)` _(in EarthTerrainCheck.Window)_ | The share of a cell's plots of every cell index (count, salt) for the cells within `deg` of a longitude, and on which side. |
| 332 | 6 | `static long col(double lon, double about)` _(in EarthTerrainCheck.Window)_ |  |

### 1. A GENERATED WORLD THROUGH ITS KEY (lines 340-384)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 40 | `static void generated(String id)` |  |

### 3. THE SAME KEY, THE SAME GROUND (lines 385-424)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 35 | `static void twice(Spot s, String id, Window w)` |  |

### 4. ANOTHER SEED (lines 425-456)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 27 | `static void seeds(List<Spot> spots, String id, Map<String, Window> windows)` |  |

### 5. A TILE'S TIME (lines 457-481)

| line | len | member | says |
|---:|---:|---|---|
| 461 | 20 | `static void tileTime(List<Spot> spots, String id)` |  |

### 6. NO SEAM AT 180 DEGREES (lines 482-514)

| line | len | member | says |
|---:|---:|---|---|
| 486 | 28 | `static void seam(Spot fiji, String id, EarthData data)` |  |

### 7. THE RULE'S PIECES (lines 515-612)

| line | len | member | says |
|---:|---:|---|---|
| 519 | 70 | `static void pieces(Map<String, Window> windows, EarthData data)` |  |
| 591 | 21 | `static boolean clearOfWater(Window w, EarthData data, EarthProj proj, double x, double y, double reach)` | Whether no sea, lake share or river reaches within `reach` plots of (x, y): the data's sea and lake there nought, and no plot drawn fresh by a river (a river vertex within reach plus its half-width). |

### 8. THE GAME STANDS ON IT (lines 613-698)

| line | len | member | says |
|---:|---:|---|---|
| 617 | 30 | `static void stands(List<Spot> spots, String id)` |  |
| 648 | 4 | `static boolean dryPlot(World w, long x, long y)` |  |
| 654 | 44 | `static void screen(Spot s, String id)` | MapCheck 5's screen on Earth: SCREEN_ACROSS x SCREEN_DOWN tiles about the site of a square city at the 5B copy's density, each tile's ground read from the world, painted and rastered. |

### 9. THE FULL FILE (lines 699-765)

| line | len | member | says |
|---:|---:|---|---|
| 704 | 61 | `static EarthData full(Spot s) throws Exception` | Section 9; returns the full file's reader when it is named, else null (section 13 reads it). |

### 10 TO 14. THE DEPOSITS ON THE EARTH'S GEOLOGY (0.7.117, batch E3; (lines 766-1186)

| line | len | member | says |
|---:|---:|---|---|
| 808 | 1 | **type** `record GeoSpot(String name, double lat, double lon, String province, Geology.Kind kind, double knownMmboe, ...` | One of the study's spots: its place, its province and class, and the multipliers it measured (spec-earth 4). |
| 811 | 42 | `static void classes(EarthData data)` | Section 10: the provinces' classes, the rules' and the hand-checked table over them - on the fixture, which holds the whole geology. |
| 855 | 9 | `static String kinds(int[] by)` | A count by class, in Geology.Kind's order, as words. |
| 866 | 11 | `static List<GeoSpot> geoSpots(Map<String, Spot> spots, String id)` | The study's spots: the three windows' and the shield's, 150 km east of Perth on the Perth world's plots. |
| 879 | 37 | `static void spots(EarthData data, String id, Map<String, Spot> spots)` | Section 11: the study's spots - their provinces and classes, the study's multipliers at its Z, and the model's at its own. |
| 924 | 65 | `static void byClass(World w, String where)` | Section 12: iron's fields per land km2 by class, on a world's PURE cells - those whose field land is their land x the weight x Z of the class at their centre for iron and copper both, so all their land is of that clas... |
| 991 | 7 | `static boolean pureIn(World w, int c, double land, Geology.Kind k)` | Whether a cell's land is all of class k: its field land for iron and for copper each its land x the weight x Z there (Geology's constants; the field land is kept as a float). |
| 1000 | 61 | `static void planet(EarthData file, Spot rotterdam)` | Section 13, on the full file: Z and the odds are its own to their rounding, and the study's figures on the rules' classes; section 12 on the whole planet. |
| 1063 | 123 | `static void types(String id, Map<String, Spot> spots)` | Section 14: the totals exact on Earth and every field typed by the class under it. |

