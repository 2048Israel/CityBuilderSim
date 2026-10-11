# EarthTerrainCheck.java - 722 lines · 21 methods · 13 constants · harnesses

`ham/citybuildersim/EarthTerrainCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key holding the generated world as it was, and the game's founding, land and screen standing on an Earth world.
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
> ... (3 more lines in the source)

**Uses:** [World](World.md) (143), [EarthData](EarthData.md) (29), [EarthTerrain](EarthTerrain.md) (18), [MapCheck](MapCheck.md) (18), [EarthProj](EarthProj.md) (9), [CityLand](CityLand.md) (6), [CityMap](CityMap.md) (6), [EarthCheck](EarthCheck.md) (5), [LandManager](LandManager.md) (5), [TilePainter](TilePainter.md) (5), [BuildingVisual](BuildingVisual.md) (3), [Founding](Founding.md) (2), [WorldCheck](WorldCheck.md) (1), [BuildingCatalog](BuildingCatalog.md) (1), [TileRaster](TileRaster.md) (1)

## Sections

| line | section |
|---:|---|
| 189 | A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells |
| 301 | 1. A GENERATED WORLD THROUGH ITS KEY |
| 344 | 3. THE SAME KEY, THE SAME GROUND |
| 384 | 4. ANOTHER SEED |
| 416 | 5. A TILE'S TIME |
| 441 | 6. NO SEAM AT 180 DEGREES |
| 474 | 7. THE RULE'S PIECES |
| 572 | 8. THE GAME STANDS ON IT |
| 658 | 9. THE FULL FILE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `EarthTerrainCheck.SHARE_POINTS` | `2` | Each whole 15" cell's sea share of the plots drawn is the data's within this, in points, in the mean over a window (spec-earth 8: 2; the study measured 0.39 to 1.49). |
| 84 | `EarthTerrainCheck.SEED_CHANGES` | `0.10` | A second seed changes at least this share of a window's plots (spec-earth 8: 10%; the study measured 15.7 to 28.6%). |
| 87 | `EarthTerrainCheck.SEED_WATER_POINTS` | `0.5` | ...while a window's water, sea and fresh, moves no more than this, in points (spec-earth 8: 0.5; the study measured 0.1). |
| 90 | `EarthTerrainCheck.TILE_MS` | `0.3` | A tile's ground in no more than this, ms (spec-earth 8: 0.3; the study measured 0.09 to 0.20). |
| 93 | `EarthTerrainCheck.CELLS_PASS_MS` | `1000` | The cells pass in no more than this, ms (spec-earth 8: 1 s; the study measured 0.37 to 0.45 s). |
| 96 | `EarthTerrainCheck.WINDOW` | `World.CELL` | The window about each spot, in plots a side: a world cell, 61.44 km (spec-earth 3, "61 x 61 km about each spot"). |
| 99 | `EarthTerrainCheck.TIMED_TILES` | `32` | The tiles a side of each window's middle that are timed: 32, 30.7 km. |
| 102 | `EarthTerrainCheck.TILE_ROUNDS` | `3` | Rounds of the timed tiles after one to warm: 3, the median taken (the study's 3 repeats). |
| 105 | `EarthTerrainCheck.SEED` | `Founding.DEFAULT_WORLD_SEED, SECOND_SEED = Founding.DEFAULT_WORLD_SEED + 1` | The seed the windows are drawn with, and the second one section 4 draws them with. |
| 108 | `EarthTerrainCheck.SEAM_DEG` | `0.05` | How close to the 180th meridian Fiji's cells are held, in degrees: 0.05, twelve 15" cells (5.5 km) each side. |
| 111 | `EarthTerrainCheck.LAKE_AREA` | `0.10` | The small lakes clear of other water draw their own area within this share of it, over the windows: 10% (★E2: each blob covers its lake's area in expectation, and its fray varies it a few percent; measured in section 7). |
| 114 | `EarthTerrainCheck.LAND_WITHIN` | `0.005` | The world's land on the full file - its cells' shares added - is the land the 4' layers put inside the world's square within this share: half a percent (★E2: sixteen points a 61 km cell, each its 7.4 km 4' cell's, sum... |
| 118 | `EarthTerrainCheck.out` | `System.out` | Where the harness prints: the console. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 116 | `static int fails` |  |
| 194 | `final World world` |  |
| 195 | `final long x0, y0` |  |
| 196 | `final byte[] plots` |  |
| 197 | `final long[] classes` |  |
| 199 | `final long rMin, cMin` | Cells counted: the window's 15" cells, rows from rMin and columns (unwrapped about the spot) from cMin. |
| 200 | `final int rows, cols` |  |
| 201 | `final int[] count, salt` |  |
| 202 | `final boolean[] edge` |  |
| 203 | `final int cells` |  |
| 204 | `final double seaPoints, dataSea, dataLake, dataForest` |  |
| 205 | `final long dryForestData` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 78 | 645 | **type** `public class EarthTerrainCheck` | The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three windows of EarthCheck's fixture and Fiji's date line, a world's key h... |
| 120 | 4 | `static void check(String label, boolean ok)` |  |
| 125 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 131 | 1 | **type** `record Spot(String name, double lat, double lon)` | The fixture's windows: name, latitude, longitude. |
| 133 | 49 | `public static void main(String[] args) throws Exception` |  |
| 183 | 5 | `static double timed(Runnable r)` |  |

### A WINDOW: the world cell about a spot, every plot drawn and laid on the data's 15" cells (lines 189-300)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 107 | **type** `static final class Window` |  |
| 207 | 70 | `Window(World world, EarthData data)` _(in EarthTerrainCheck.Window)_ |  |
| 279 | 1 | `double share(byte cls)` _(in EarthTerrainCheck.Window)_ | A class's share of the window's plots. |
| 282 | 4 | `double forestOfDry()` _(in EarthTerrainCheck.Window)_ | Forest's share of the dry ground drawn. |
| 288 | 1 | `byte at(int px, int py)` _(in EarthTerrainCheck.Window)_ | The class of window plot (px, py). |
| 291 | 1 | `static long row(double lat)` _(in EarthTerrainCheck.Window)_ | The share of a cell's plots of every cell index (count, salt) for the cells within `deg` of a longitude, and on which side. |
| 293 | 6 | `static long col(double lon, double about)` _(in EarthTerrainCheck.Window)_ |  |

### 1. A GENERATED WORLD THROUGH ITS KEY (lines 301-343)

| line | len | member | says |
|---:|---:|---|---|
| 305 | 38 | `static void generated(String id)` |  |

### 3. THE SAME KEY, THE SAME GROUND (lines 344-383)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 35 | `static void twice(Spot s, String id, Window w)` |  |

### 4. ANOTHER SEED (lines 384-415)

| line | len | member | says |
|---:|---:|---|---|
| 388 | 27 | `static void seeds(List<Spot> spots, String id, Map<String, Window> windows)` |  |

### 5. A TILE'S TIME (lines 416-440)

| line | len | member | says |
|---:|---:|---|---|
| 420 | 20 | `static void tileTime(List<Spot> spots, String id)` |  |

### 6. NO SEAM AT 180 DEGREES (lines 441-473)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 28 | `static void seam(Spot fiji, String id, EarthData data)` |  |

### 7. THE RULE'S PIECES (lines 474-571)

| line | len | member | says |
|---:|---:|---|---|
| 478 | 70 | `static void pieces(Map<String, Window> windows, EarthData data)` |  |
| 550 | 21 | `static boolean clearOfWater(Window w, EarthData data, EarthProj proj, double x, double y, double reach)` | Whether no sea, lake share or river reaches within `reach` plots of (x, y): the data's sea and lake there nought, and no plot drawn fresh by a river (a river vertex within reach plus its half-width). |

### 8. THE GAME STANDS ON IT (lines 572-657)

| line | len | member | says |
|---:|---:|---|---|
| 576 | 30 | `static void stands(List<Spot> spots, String id)` |  |
| 607 | 4 | `static boolean dryPlot(World w, long x, long y)` |  |
| 613 | 44 | `static void screen(Spot s, String id)` | MapCheck 5's screen on Earth: SCREEN_ACROSS x SCREEN_DOWN tiles about the site of a square city at the 5B copy's density, each tile's ground read from the world, painted and rastered. |

### 9. THE FULL FILE (lines 658-722)

| line | len | member | says |
|---:|---:|---|---|
| 662 | 60 | `static void full(Spot s) throws Exception` |  |

