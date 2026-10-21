# EarthTerrainCheck.java - 1,547 lines · 41 methods · 31 constants · harnesses

`ham/citybuildersim/EarthTerrainCheck.java` - generated 2026-10-20 by CodeMap; line numbers are as of that run.

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
>      CELLS_PASS_MS of the main thread's CPU, the wall clock printed beside
>      it (batch EC1; the study 0.37 to 0.45 s); the world's land, its cells'
>      shares added, the land inside its square within LAND_WITHIN - every
>      4' cell of the globe projected, the antipode's cap past the square
>      left out; and the widest river EarthTerrain.WIDEST_RIVER_M, its half
>      within EarthTerrain.WATER_PAD_DEG.
> ... (58 more lines in the source)

**Uses:** [World](World.md) (314), [Geology](Geology.md) (96), [Resource](Resource.md) (77), [EarthData](EarthData.md) (51), [EarthTerrain](EarthTerrain.md) (33), [CityLand](CityLand.md) (29), [MapCheck](MapCheck.md) (22), [Deposit](Deposit.md) (21), [EarthSite](EarthSite.md) (21), [DistrictPlan](DistrictPlan.md) (12), [EarthProj](EarthProj.md) (11), [CityMap](CityMap.md) (6), [GridConversion](GridConversion.md) (6), [GameVersion](GameVersion.md) (6), [EarthCheck](EarthCheck.md) (5), [LandManager](LandManager.md) (5), [BuildingVisual](BuildingVisual.md) (5), [TilePainter](TilePainter.md) (5), [LandMap](LandMap.md) (5), [CityRuns](CityRuns.md) (5), [DataSave](DataSave.md) (5), [Timing](Timing.md) (4), [PlanCheck](PlanCheck.md) (3), [Founding](Founding.md) (2), [BuildingCatalog](BuildingCatalog.md) (2), [TileRaster](TileRaster.md) (2), [WorldCheck](WorldCheck.md) (1), [MapFrame](MapFrame.md) (1), [MapTiles](MapTiles.md) (1)

**Used by (3):** [GatewayCheck](GatewayCheck.md), [GlobeCheck](GlobeCheck.md), [MapCheck](MapCheck.md)

## Sections

| line | section |
|---:|---|
| 250 | A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells |
| 363 | 1. A GENERATED WORLD THROUGH ITS KEY |
| 408 | 3. THE SAME KEY, THE SAME GROUND |
| 448 | 4. ANOTHER SEED |
| 480 | 5. A TILE'S TIME |
| 505 | 6. NO SEAM AT 180 DEGREES |
| 538 | 7. THE RULE'S PIECES |
| 636 | 8. THE GAME STANDS ON IT |
| 721 | 9. THE FULL FILE |
| 818 | 10 TO 14. THE DEPOSITS ON THE EARTH'S GEOLOGY (0.7.117, batch E3; |
| 1239 | 15. ICE AND DESERT AS KINDS OF GROUND (0.7.127, batch E7; the |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 136 | `EarthTerrainCheck.SHARE_POINTS` | `2` | Each whole 15" cell's sea share of the plots drawn is the data's within this, in points, in the mean over a window (spec-earth 8: 2; the study measured 0.39 to 1.49). |
| 139 | `EarthTerrainCheck.SEED_CHANGES` | `0.10` | A second seed changes at least this share of a window's plots (spec-earth 8: 10%; the study measured 15.7 to 28.6%). |
| 142 | `EarthTerrainCheck.SEED_WATER_POINTS` | `0.5` | ...while a window's water, sea and fresh, moves no more than this, in points (spec-earth 8: 0.5; the study measured 0.1). |
| 145 | `EarthTerrainCheck.TILE_MS` | `0.3` | A tile's ground in no more than this, ms (spec-earth 8: 0.3; the study measured 0.09 to 0.20). |
| 148 | `EarthTerrainCheck.CELLS_PASS_MS` | `1000` | The cells pass in no more than this, ms of the main thread's CPU since batch EC1 (spec-earth 8: 1 s; the study measured 0.37 to 0.45 s on the wall clock). |
| 151 | `EarthTerrainCheck.WINDOW` | `World.CELL` | The window about each spot, in plots a side: a world cell, 61.44 km (spec-earth 3, "61 x 61 km about each spot"). |
| 154 | `EarthTerrainCheck.TIMED_TILES` | `32` | The tiles a side of each window's middle that are timed: 32, 30.7 km. |
| 157 | `EarthTerrainCheck.TILE_ROUNDS` | `3` | Rounds of the timed tiles after one to warm: 3, the median taken (the study's 3 repeats). |
| 160 | `EarthTerrainCheck.SEED` | `Founding.DEFAULT_WORLD_SEED, SECOND_SEED = Founding.DEFAULT_WORLD_SEED + 1` | The seed the windows are drawn with, and the second one section 4 draws them with. |
| 163 | `EarthTerrainCheck.SEAM_DEG` | `0.05` | How close to the 180th meridian Fiji's cells are held, in degrees: 0.05, twelve 15" cells (5.5 km) each side. |
| 166 | `EarthTerrainCheck.LAKE_AREA` | `0.10` | The small lakes clear of other water draw their own area within this share of it, over the windows: 10% (★E2: each blob covers its lake's area in expectation, and its fray varies it a few percent; measured in section 7). |
| 169 | `EarthTerrainCheck.LAND_WITHIN` | `0.005` | The world's land on the full file - its cells' shares added - is the land the 4' layers put inside the world's square within this share: half a percent (★E2: sixteen points a 61 km cell, each its 7.4 km 4' cell's, sum... |
| 173 | `EarthTerrainCheck.out` | `System.out` | Where the harness prints: the console. |
| 824 | `EarthTerrainCheck.STUDY_CLASSES` | `{ 55, 38, 151, 20, 66, 474, 219 }` | The study's count of the provinces by geology.py's rules (spec-earth 4): 474 basin, 151 fold belt, 66 shelf, 55 ocean, 38 shield, 20 rift and 219 other, in Geology.Kind's order. |
| 827 | `EarthTerrainCheck.STUDY_SHARES` | `{ 0.7, 17.8, 18.9, 2.2, 2.8, 35.1, 22.5 }` | The study's land in each class by the rules, Antarctica left out, in points (spec-earth 4), in Geology.Kind's order: 0.7 ocean, 17.8 shield, 18.9 fold belt, 2.2 rift, 2.8 shelf, 35.1 basin, 22.5 other. |
| 830 | `EarthTerrainCheck.SHARE_WITHIN` | `0.5` | ...reproduced within this, in points: half a point (★E3: the study's are rounded to a tenth, and its land was its prototype's cells). |
| 833 | `EarthTerrainCheck.STUDY_Z` | `{ 0.888, 0.701, 0.751, 0.959, 0.846, 1.049 }` | The study's Z on the rules' classes (spec-earth 4), in Resource's order for the six in fields: iron 0.888, oil 0.701, stone 0.751, coal 0.959, copper 0.846, uranium 1.049. |
| 836 | `EarthTerrainCheck.STUDY_LIMESTONE_BASIN` | `0.886, STUDY_LIMESTONE_SHIELD = 0.493` | The study's limestone in basins and on shields on the rules' classes (spec-materials 2.4, out/earth-types.txt): .886 and .493. |
| 839 | `EarthTerrainCheck.LIMESTONE_WITHIN` | `0.005` | ...reproduced within this: half a point (★E3: the study's are rounded to three places). |
| 842 | `EarthTerrainCheck.SIGMAS` | `3` | A sampled figure is held to its expectation within this many standard deviations: 3 (★E3). |
| 845 | `EarthTerrainCheck.Z_ROUNDING` | `0.00005` | A Z on the full file is Geology's constant to its rounding, four decimals: half the last place. |
| 848 | `EarthTerrainCheck.ODDS_ROUNDING` | `0.000005` | An odds on the full file is Geology's constant to its rounding, six figures: half the last place, relative. |
| 851 | `EarthTerrainCheck.YILGARN_KM` | `150` | How far east of Perth the study's shield spot lies, in km: 150 (spec-earth 4, "150 km east of Perth (Yilgarn Block)"). |
| 854 | `EarthTerrainCheck.TYPE_DRAWS` | `200_000` | Synthetic fields drawn for each class to hold the types' odds: 200,000 (one standard deviation about 0.1 point). |
| 857 | `EarthTerrainCheck.SEA_OIL_CELLS` | `8` | How far about Rotterdam's site the North Sea's cells are looked at for oil at sea, in cells: 8 (490 km), within the fixture's bands. |
| 1245 | `EarthTerrainCheck.COVER_POINTS` | `SHARE_POINTS` | Each kind's share of a 1-degree cell's ground drawn is the data's within this, in points: SHARE_POINTS, the bound the sea's share of a 15" cell is held to (spec-earth 8); E7 measured 0.02 to 0.34. |
| 1248 | `EarthTerrainCheck.ICE_CELLS` | `{ { 64, - 17 }, { 61, - 46 } }, DESERT_CELLS = { { 16, 0 }, { 44, 56 } }` | The 1-degree cells each kind is held on, {lat0, lon0}, on the full file: ice at Vatnajokull and on south Greenland's margin, the desert at the Sahel's edge and on the Ustyurt by the Aral Sea - each a cell with its kin... |
| 1251 | `EarthTerrainCheck.SHEET` | `{ 72, - 40 }` | Greenland's sheet's middle, EarthFoundingCheck 3's spot on ice. |
| 1254 | `EarthTerrainCheck.MARGIN` | `{ 61.41, - 45.53 }` | A spot on south Greenland's ice margin whose snapped site lies on grass in a 2' cell with the ice bit - the bit's rule (E4's) refused it, the plot's does not (found by E7's probe, the default seed). |
| 1257 | `EarthTerrainCheck.OFF_THE_ICE` | `{ 61.42, - 45.62 }` | A spot on the ice there whose site the snap finds off it, on dry ground over a kilometre away (E7's probe). |
| 1260 | `EarthTerrainCheck.SAHARA` | `{ 23, 10 }` | The Sahara's spot, EarthFoundingCheck 3's desert. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 171 | `static int fails` |  |
| 255 | `final World world` |  |
| 256 | `final long x0, y0` |  |
| 257 | `final byte[] plots` |  |
| 258 | `final long[] classes` |  |
| 260 | `final long rMin, cMin` | Cells counted: the window's 15" cells, rows from rMin and columns (unwrapped about the spot) from cMin. |
| 261 | `final int rows, cols` |  |
| 262 | `final int[] count, salt` |  |
| 263 | `final boolean[] edge` |  |
| 264 | `final int cells` |  |
| 265 | `final double seaPoints, dataSea, dataLake, dataForest` |  |
| 266 | `final long dryForestData` |  |
| 1514 | `final EarthData d` |  |
| 1515 | `final int cpd` |  |
| 1516 | `final Map<Integer, byte[]> grids` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 133 | 1415 | **type** `public class EarthTerrainCheck` | The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key h... |
| 175 | 4 | `static void check(String label, boolean ok)` |  |
| 180 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 186 | 1 | **type** `record Spot(String name, double lat, double lon)` | The fixture's windows: name, latitude, longitude. |
| 188 | 55 | `public static void main(String[] args) throws Exception` |  |
| 244 | 5 | `static double timed(Runnable r)` |  |

### A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells (lines 250-362)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 108 | **type** `static final class Window` |  |
| 268 | 70 | `Window(World world, EarthData data)` _(in EarthTerrainCheck.Window)_ |  |
| 340 | 1 | `double share(byte cls)` _(in EarthTerrainCheck.Window)_ | A class's share of the window's plots. |
| 343 | 5 | `double forestOfDry()` _(in EarthTerrainCheck.Window)_ | Forest's share of the dry ground drawn. |
| 350 | 1 | `byte at(int px, int py)` _(in EarthTerrainCheck.Window)_ | The class of window plot (px, py). |
| 353 | 1 | `static long row(double lat)` _(in EarthTerrainCheck.Window)_ | The share of a cell's plots of every cell index (count, salt) for the cells within `deg` of a longitude, and on which side. |
| 355 | 6 | `static long col(double lon, double about)` _(in EarthTerrainCheck.Window)_ |  |

### 1. A GENERATED WORLD THROUGH ITS KEY (lines 363-407)

| line | len | member | says |
|---:|---:|---|---|
| 367 | 40 | `static void generated(String id)` |  |

### 3. THE SAME KEY, THE SAME GROUND (lines 408-447)

| line | len | member | says |
|---:|---:|---|---|
| 412 | 35 | `static void twice(Spot s, String id, Window w)` |  |

### 4. ANOTHER SEED (lines 448-479)

| line | len | member | says |
|---:|---:|---|---|
| 452 | 27 | `static void seeds(List<Spot> spots, String id, Map<String, Window> windows)` |  |

### 5. A TILE'S TIME (lines 480-504)

| line | len | member | says |
|---:|---:|---|---|
| 484 | 20 | `static void tileTime(List<Spot> spots, String id)` |  |

### 6. NO SEAM AT 180 DEGREES (lines 505-537)

| line | len | member | says |
|---:|---:|---|---|
| 509 | 28 | `static void seam(Spot fiji, String id, EarthData data)` |  |

### 7. THE RULE'S PIECES (lines 538-635)

| line | len | member | says |
|---:|---:|---|---|
| 542 | 70 | `static void pieces(Map<String, Window> windows, EarthData data)` |  |
| 614 | 21 | `static boolean clearOfWater(Window w, EarthData data, EarthProj proj, double x, double y, double reach)` | Whether no sea, lake share or river reaches within `reach` plots of (x, y): the data's sea and lake there nought, and no plot drawn fresh by a river (a river vertex within reach plus its half-width). |

### 8. THE GAME STANDS ON IT (lines 636-720)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 30 | `static void stands(List<Spot> spots, String id)` |  |
| 671 | 3 | `static boolean dryPlot(World w, long x, long y)` |  |
| 676 | 44 | `static void screen(Spot s, String id)` | MapCheck 5's screen on Earth: SCREEN_ACROSS x SCREEN_DOWN tiles about the site of a square city at the 5B copy's density, each tile's ground read from the world, painted and rastered. |

### 9. THE FULL FILE (lines 721-817)

| line | len | member | says |
|---:|---:|---|---|
| 745 | 3 | `static long passClock()` | The clock the cells pass's bound reads, in ns: the main thread's CPU, or the wall clock where MapCheck.CPU_TIMED is false. |
| 750 | 67 | `static EarthData full(Spot s) throws Exception` | Section 9; returns the full file's reader when it is named, else null (section 13 reads it). |

### 10 TO 14. THE DEPOSITS ON THE EARTH'S GEOLOGY (0.7.117, batch E3; (lines 818-1238)

| line | len | member | says |
|---:|---:|---|---|
| 860 | 1 | **type** `record GeoSpot(String name, double lat, double lon, String province, Geology.Kind kind, double knownMmboe, ...` | One of the study's spots: its place, its province and class, and the multipliers it measured (spec-earth 4). |
| 863 | 42 | `static void classes(EarthData data)` | Section 10: the provinces' classes, the rules' and the hand-checked table over them - on the fixture, which holds the whole geology. |
| 907 | 9 | `static String kinds(int[] by)` | A count by class, in Geology.Kind's order, as words. |
| 918 | 11 | `static List<GeoSpot> geoSpots(Map<String, Spot> spots, String id)` | The study's spots: the three windows' and the shield's, 150 km east of Perth on the Perth world's plots. |
| 931 | 37 | `static void spots(EarthData data, String id, Map<String, Spot> spots)` | Section 11: the study's spots - their provinces and classes, the study's multipliers at its Z, and the model's at its own. |
| 976 | 65 | `static void byClass(World w, String where)` | Section 12: iron's fields per land km2 by class, on a world's PURE cells - those whose field land is their land x the weight x Z of the class at their centre for iron and copper both, so all their land is of that clas... |
| 1043 | 7 | `static boolean pureIn(World w, int c, double land, Geology.Kind k)` | Whether a cell's land is all of class k: its field land for iron and for copper each its land x the weight x Z there (Geology's constants; the field land is kept as a float). |
| 1052 | 61 | `static void planet(EarthData file, Spot rotterdam)` | Section 13, on the full file: Z and the odds are its own to their rounding, and the study's figures on the rules' classes; section 12 on the whole planet. |
| 1115 | 123 | `static void types(String id, Map<String, Spot> spots)` | Section 14: the totals exact on Earth and every field typed by the class under it. |

### 15. ICE AND DESERT AS KINDS OF GROUND (0.7.127, batch E7; the (lines 1239-1547)

| line | len | member | says |
|---:|---:|---|---|
| 1262 | 115 | `static void iceAndDesert(EarthData file, Map<String, Window> windows, String id)` |  |
| 1379 | 5 | `static long[] only(byte cls, long n)` | A count of n plots of one class, World.CLASSES long. |
| 1386 | 3 | `static boolean adds(EarthSite.Card c)` | A card's plots add up: dry, fresh, sea and ice. |
| 1391 | 5 | `static int westOf(DistrictPlan p)` | How many of a plan's buildings stand west of its district's middle. |
| 1398 | 16 | `static long[] tileOf(World w, CityLand land, byte cls)` | The nearest whole tile to a city's founding site, ring by ring of tiles out to 48 (46 km), that the city owns no plot of and whose every plot is of one class: its north-west plot, or null. |
| 1416 | 16 | `static boolean far(World.Key key, byte cls, String[] words)` | Whether the far nodes about a world's spot draw a class: every pixel of it at WATER_STRIDE and FAR_STRIDE / 4 (61 and 246 km a node), and at FAR_STRIDE (983 km, which reaches past the sheet's coast) more of it than of... |
| 1443 | 68 | `static void cover(EarthData d, boolean ice, int lat0, int lon0)` | One kind's 1-degree cell on the full file, on a world centred there: every plot whose centre lies in the cell drawn, and among those the kind could take (ice any ground not water; the desert grass or desert, which for... |
| 1513 | 34 | **type** `static final class Cover` | Land cover's 2' cells read from the file's grids, kept by tile: a cell's value, and a bit's share between the cells' centres as EarthTerrain reads it. |
| 1518 | 4 | `Cover(EarthData d)` _(in EarthTerrainCheck.Cover)_ |  |
| 1524 | 7 | `int cell(long r, long c)` _(in EarthTerrainCheck.Cover)_ | The value of the cell in global row r (from 90 N) and column c (from 180 W). |
| 1533 | 3 | `int at(double lat, double lon)` _(in EarthTerrainCheck.Cover)_ | The value of the cell a point lies in. |
| 1538 | 8 | `double share(double lat, double lon, int bit)` _(in EarthTerrainCheck.Cover)_ | A bit read 0 or 1 at each cell and interpolated between the four cells' centres about a point. |

