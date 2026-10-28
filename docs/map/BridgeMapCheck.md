# BridgeMapCheck.java - 1,015 lines · 51 methods · 9 constants · harnesses

`ham/citybuildersim/BridgeMapCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> A long bridge that stands is laid by the runs, kept clear by the plans and drawn with its deck (ET6; docs/studies/spec-earth2-terrain.md 6.5 and 6.6).
> 
> WHAT THIS HAS TO PROVE (the sections below are numbered to match; each has its part on the runs alone, on ground of two dry islands and a
> channel the harness makes, and its part on a real CityMap, on a channel of fresh water in the default world):
> 
>   1. A HIGHWAY BRIDGE ACROSS A CHANNEL NO SHORT CROSSING SPANS IS LAID WHOLE.
>      The channel is wider than the highway's and the railway's reach
>      (TilePainter.MAX_BRIDGE, RAIL_BRIDGE: the premise; laid alone the net
>      puts no plot across it). A bridge handed to the runs
>      (CityRuns.setBridges(); to the map, CityMap.bridgePicture() and the
>      month's reconcile()) has every plot of its path, from landing to
>      landing, a highway plot (fill(), at(), the painter's Input.fixed), the
>      far landing too; a diagonal leg is laid as the nets' staircase, no ramp
>      is marked, and the net's plots and what it is short of are what they
>      were (the bridge spends none of its growth). On the map the plots of it
>      over the water are the painter's bridge plots, its landings are not.
> 
>   2. A RAIL BRIDGE LIKEWISE; A ROAD BRIDGE DRAWS A PAVED DECK AND JOINS NO
>      NET. A rail bridge's plots are the railway's (a railway over a highway
>      where they meet), drawn with TileRaster.DECK beside the bed over the
>      water and as ordinary track on its landings. A road bridge is no plot of
>      any net (at() sees none, the tile's runs hold none, no net's plots
>      move) and is a paved street a whole plot wide, its deck under it over
>      the water and none on its landings; its ground is a plan's, kept as a
>      reserve.
> 
>   3. ITS LANDINGS AND DECK HOLD NO BUILDING AFTER THE PLANS ARE DRAWN. The
>      premise: in the same city drawn with no bridge a building stands on
>      most of the landings (the fixture chooses the rows whose landings are
>      built). With the bridges handed, the plans of the districts they cross
>      see them (their input holds the highway's, the railway's and the road's
>      reserve on the landing) and not one building or field stands on a plot
>      of any path.
> 
>   4. THE BRIDGE DOES NOT MOVE. The net grown, taken back and grown again with
>      the bridge standing: every plot of the bridge is where it was and the
>      net's plots are the model's every time (the bridge spends none of the
>      budget). On the map a month with more highway plots in the model, then
>      fewer, then more: the same, every arm a prefix of the next month's;
>      canonical() and reconcile() lay the same bridges, and the journal of the
>      runs (CityMap.journalDropped()) is at none.
> 
>   5. NO BRIDGE, NO CHANGE. Runs and maps handed no bridge, a null and an empty
>      list are the same: the same fill, frame hash and signature of the runs;
>      on the map the same runs, the same raster bytes on a sample of tiles and
>      the same sidecar, byte for byte. A bridge handed and taken back leaves
>      the same runs, plot for plot.
> 
>   6. THE STAMP MOVES WHEN A BRIDGE IS HANDED. The same bridges in any order
>      are the one stamp, another list another; a run that is no path is not
>      laid and is counted; the witness is told the districts a bridge comes
>      to. On the map the sidecar's land stamp moves with it, so a sidecar
>      written before the bridge is not read back under it, one written after
>      is read back only under the list it was written with and is then the same
>      map with the bridge laid; the runs' version and the plan stamp of a
>      district the bridge crosses move.
> 
> WHY THIS EXISTS: the bridge the model builds was nowhere on the map: no run met it, a plan was free to put a building where it came
> ashore, and the raster drew water. A fixture CAUSES each case: a channel wider than any short crossing, landings with buildings on
> them, a month that grows the net, a sidecar written under another list.

**Uses:** [CityRuns](CityRuns.md) (74), [World](World.md) (69), [CityMap](CityMap.md) (64), [TilePainter](TilePainter.md) (21), [IslandNetCheck](IslandNetCheck.md) (19), [TileRaster](TileRaster.md) (13), [BuildingVisual](BuildingVisual.md) (10), [DistrictPlan](DistrictPlan.md) (9), [CityLand](CityLand.md) (8), [MapCheck](MapCheck.md) (3), [Timing](Timing.md) (1), [BuildingCatalog](BuildingCatalog.md) (1), [Founding](Founding.md) (1), [GatewayCheck](GatewayCheck.md) (1), [JunctionCheck](JunctionCheck.md) (1), [Resource](Resource.md) (1)

## Sections

| line | section |
|---:|---|
| 169 | THE RUNS: two islands, a channel, three bridges |
| 375 | THE MAP: a channel of fresh water in the default world |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 85 | `BridgeMapCheck.SITE` | `IslandNetCheck.SITE` | The founding site of the runs' ground: a highway line crossing, so that the hub is the site itself. |
| 88 | `BridgeMapCheck.ROAD` | `CityMap.BridgeRun.ROAD, HIGHWAY = CityMap.BridgeRun.HIGHWAY, RAIL = CityMap.B...` | The kinds, short. |
| 174 | `BridgeMapCheck.W0` | `SITE - 700, W1 = SITE + 150, E0 = SITE + 221, E1 = SITE + 721, Y0 = SITE - 45...` | The runs' ground: the west island (to x = SITE + 150), a channel of 70 plots unowned between, the east island from SITE + 221. |
| 177 | `BridgeMapCheck.HW` | `{ W1, SITE + 40, SITE + 170, SITE + 40, SITE + 182, SITE + 52, E0, SITE + 52 }` | The highway bridge: out of the west island's shore plot, east, a 45-degree stretch, east into the east island's first column. |
| 179 | `BridgeMapCheck.RL` | `{ W1, SITE + 40, SITE + 160, SITE + 30, E0 + 1, SITE + 30 }` | The rail bridge: out of the same landing, north-east, then east. |
| 181 | `BridgeMapCheck.RD` | `{ W1, SITE - 60, E0, SITE - 60 }` | The road bridge: straight across, a row above. |
| 380 | `BridgeMapCheck.MOST_WIDE` | `140` | The most plots wide a channel may be for the fixture: the bridges are paths of a hundred plots, not thousands. |
| 383 | `BridgeMapCheck.BAND` | `24` | Rows of the band either side of its middle row. |
| 579 | `BridgeMapCheck.MapFixture.APPROACH` | `14` | How far inland of the shore a bridge's landing may be chosen, in plots: the city builds on a bank's first plots in a few rows only. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 76 | `static int fails` |  |
| 184 | `final IslandNetCheck.Land g` |  |
| 187 | `final long target` |  |
| 188 | `final int w` |  |
| 189 | `final Set<Long> hwPath` |  |
| 190 | `CityRuns runs` |  |
| 191 | `byte[] alone` |  |
| 387 | `final World world` |  |
| 388 | `private final Map<Long, byte[]> tiles` |  |
| 435 | `World world` |  |
| 436 | `Ground ground` |  |
| 437 | `double[][] rects` |  |
| 438 | `CityLand land` |  |
| 439 | `BuildingVisual.Type[] types` |  |
| 440 | `long[] counts` |  |
| 441 | `int hwType, cellsPer` |  |
| 442 | `long[][] rows` | {y, first fresh x, last fresh x} for each row of the band |
| 443 | `long[] hw, rl, rd` | {y, first fresh x, last fresh x} for each row of the band |
| 444 | `List<CityMap.BridgeRun> bridges` | the three bridges' corners |
| 445 | `long sx, sy` |  |
| 446 | `int built` | how many of the six landings stand on a building in the city drawn with no bridge |
| 447 | `CityMap live` | how many of the six landings stand on a building in the city drawn with no bridge |
| 448 | `final Map<Long, Tile> liveTiles` | drawn with no bridge and handed them |
| 449 | `CityMap bare` |  |
| 450 | `Set<Long> hwPath, rlPath, rdPath` |  |
| 644 | `final Map<String, int[]> images` | Tile images at a width of px pixels a plot, kept by tile and width. |
| 812 | `CityMap cano` | The city drawn with the bridges standing before it is built: kept. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 942 | **type** `public class BridgeMapCheck` | A long bridge that stands is laid by the runs, kept clear by the plans and drawn with its deck (ET6; docs/studies/spec-earth2-terrain.md 6.5 and 6.6). |
| 78 | 5 | `static void check(String label, boolean ok)` |  |
| 91 | 3 | `static int shortest()` | The widest water a short crossing spans, in plots: the highway's reach and the railway's, as the painter has them. |
| 95 | 3 | `static long key(long x, long y)` |  |
| 99 | 3 | `static long kx(long k)` |  |
| 103 | 3 | `static long ky(long k)` |  |
| 111 | 14 | `static Set<Long> pathOf(long[] c)` | The plots of a bridge's path, worked out here and not by CityRuns: the first corner, then every leg step by step, a diagonal's step with the plot beside it (x + dx, y) that keeps the run joined. |
| 126 | 3 | `static CityMap.BridgeRun run(int kind, long...corners)` |  |
| 131 | 5 | `static byte[] fillOf(CityRuns runs, long x0, long y0, int w, int h, boolean plan, byte[] marks)` | The runs' codes over a box, row by row: fill() with the plan's ground or the tile's. |
| 138 | 4 | `static boolean stands(CityRuns runs, Set<Long> path, byte code)` | Whether every plot of a path is `code` on the runs. |
| 143 | 25 | `public static void main(String[] args)` |  |

### THE RUNS: two islands, a channel, three bridges (lines 169-374)

| line | len | member | says |
|---:|---:|---|---|
| 183 | 191 | **type** `static final class RunsFx` |  |
| 193 | 5 | `CityRuns fresh()` _(in BridgeMapCheck.RunsFx)_ |  |
| 200 | 11 | `boolean windows(CityRuns r, byte[] whole, boolean plan, long[]...wins)` _(in BridgeMapCheck.RunsFx)_ | Whether a window of the box is what the whole box holds there. |
| 212 | 43 | `void highway()` _(in BridgeMapCheck.RunsFx)_ |  |
| 256 | 41 | `void railAndRoad()` _(in BridgeMapCheck.RunsFx)_ |  |
| 298 | 18 | `void unmoved()` _(in BridgeMapCheck.RunsFx)_ |  |
| 318 | 8 | `boolean onTheRuns()` _(in BridgeMapCheck.RunsFx)_ | The three bridges on the runs as the section 2 left them. |
| 327 | 16 | `void unchanged()` _(in BridgeMapCheck.RunsFx)_ |  |
| 344 | 29 | `void stamps()` _(in BridgeMapCheck.RunsFx)_ |  |

### THE MAP: a channel of fresh water in the default world (lines 375-1015)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 18 | **type** `static final class Ground` | The world's ground as the map reads it: its tiles' (World.tileTerrain(), which the painter and the runs' ground use), not a plot's own (terrainAt()), which differs along a shore. |
| 390 | 3 | `Ground(World world)` _(in BridgeMapCheck.Ground)_ |  |
| 394 | 9 | `byte at(long x, long y)` _(in BridgeMapCheck.Ground)_ |  |
| 406 | 1 | **type** `record Tile(TilePainter.Input in, TilePainter.Painted p)` | A tile of the map as the painter has it. |
| 409 | 9 | `static Tile tile(CityMap map, Map<Long, Tile> drawn, long tx, long ty)` | A tile painted, kept in `drawn`. |
| 420 | 3 | `static Tile at(CityMap map, Map<Long, Tile> drawn, long x, long y)` | Plot (x, y)'s tile and its index in it. |
| 424 | 3 | `static int index(long x, long y)` |  |
| 429 | 3 | `static int shade(Tile t, int i, int colour)` | A colour as the raster draws it on plot i of a tile: dimmed off the city's ground as every deck and surface is. |
| 434 | 523 | **type** `static final class MapFixture` | The fixture: a land of two banks parted by a channel, the city on it drawn as the model draws it, the three bridges. |
| 452 | 3 | `CityMap draw(List<CityMap.BridgeRun> list)` _(in BridgeMapCheck.MapFixture)_ |  |
| 457 | 3 | `CityMap draw(List<CityMap.BridgeRun> list, long[] at)` _(in BridgeMapCheck.MapFixture)_ | The city drawn canonically at the counts given, with the bridges (null for none). |
| 462 | 5 | `long[] scaled(double by)` _(in BridgeMapCheck.MapFixture)_ | The counts with every type but the highways' scaled (rounded down), so that a city drawn at them has room for the full counts. |
| 468 | 109 | `static MapFixture build()` _(in BridgeMapCheck.MapFixture)_ |  |
| 582 | 3 | `boolean builtAt(long x, long y)` _(in BridgeMapCheck.MapFixture)_ | Whether plot (x, y) stands under a building in the city drawn with no bridge. |
| 587 | 6 | `void handed()` _(in BridgeMapCheck.MapFixture)_ | The map with the bridges handed and the month laid, drawn from the city with none. |
| 595 | 8 | `List<long[]> plots(Set<Long> path, boolean water)` _(in BridgeMapCheck.MapFixture)_ | The tiles' plots of a path that are water (fresh), and the dry ones. |
| 604 | 38 | `void highway()` _(in BridgeMapCheck.MapFixture)_ |  |
| 646 | 7 | `int[] image(Tile t, int px)` _(in BridgeMapCheck.MapFixture)_ |  |
| 655 | 5 | `boolean draws(long x, long y, int colour, int px)` _(in BridgeMapCheck.MapFixture)_ | Whether plot (x, y) of the tile it lies in draws `colour` (dimmed as the plot's ownership has it) at px pixels a plot. |
| 661 | 51 | `void railAndRoad()` _(in BridgeMapCheck.MapFixture)_ |  |
| 714 | 5 | `long freshAlong(long[] b)` _(in BridgeMapCheck.MapFixture)_ | How many plots of a bridge's row between its landings are fresh water, by the ground the map reads. |
| 721 | 16 | `Set<String> onPaths(CityMap map, Map<Long, Tile> tiles)` _(in BridgeMapCheck.MapFixture)_ | The buildings and fields on the plots of the three paths in a map's tiles (kept in `tiles`): each as its tile, box and type; fields as "field" and the plot. |
| 739 | 11 | `int buildingsAlong(Map<Long, Tile> tiles, CityMap map)` _(in BridgeMapCheck.MapFixture)_ | How many buildings the tiles the three paths run through hold (each counted on the tile it is counted on). |
| 751 | 49 | `void clearance()` _(in BridgeMapCheck.MapFixture)_ |  |
| 802 | 8 | `boolean stampMoved()` _(in BridgeMapCheck.MapFixture)_ | Whether the plan stamp of the district holding the highway's near landing differs from the city's drawn without the bridges, and a district no bridge crosses has the one it had. |
| 814 | 4 | `CityMap canonical()` _(in BridgeMapCheck.MapFixture)_ |  |
| 820 | 8 | `boolean bridgesStand(CityMap map)` _(in BridgeMapCheck.MapFixture)_ | Whether every plot of every bridge is on the map's runs as it was laid: the highway's, the railway's, the road's (by its own kind: it is no plot of the runs). |
| 829 | 36 | `void unmoved()` _(in BridgeMapCheck.MapFixture)_ |  |
| 866 | 3 | `long bridgesStamp()` _(in BridgeMapCheck.MapFixture)_ |  |
| 871 | 7 | `List<long[]> sample()` _(in BridgeMapCheck.MapFixture)_ | The tiles a sample of the raster checks look at: those of each path's landings and middle. |
| 880 | 11 | `long rasterHash(CityMap map)` _(in BridgeMapCheck.MapFixture)_ | A hash of the raster of the sample of tiles, 4 px a plot. |
| 892 | 25 | `void unchanged()` _(in BridgeMapCheck.MapFixture)_ |  |
| 918 | 38 | `void stamps()` _(in BridgeMapCheck.MapFixture)_ |  |
| 959 | 4 | `static boolean dryAround(Ground world, long x, long y)` | Whether the plots about (x, y), 20 each way, are all dry ground. |
| 969 | 20 | `static long[][] channel(Ground ground, long[] place, int minWide)` | The first channel of fresh water in the world near `place`: 2 * BAND + 1 rows, each {y, first fresh x, last fresh x} of the run through the middle row's centre, every run at least `minWide` plots and at most MOST_WIDE... |
| 990 | 25 | `static long[][] band(Ground world, long yc, long xMid, int minWide)` |  |

