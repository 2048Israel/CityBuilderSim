# RunRenders.java - 206 lines · 7 methods · 1 constants · tools

`ham/citybuildersim/tools/RunRenders.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> Renders what ET4 slice C did to the map, for review: where a city's highways and railway run across steep ground, a
> viaduct, and across very steep ground, a tunnel, drawn by the game's own painters, headless.
> 
>     java -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.RunRenders [earth-2.bin [outDir]]
> 
> The file is the argument, else -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); outDir is docs/queue/ET4
> unless named. -Drun.months=N plays the city N months (2,000 unless named: SlopeRenders' length). The city is founded
> at Medellin (LongPlaytest.earthCity(), in a GameFiles.scratch() folder deleted when the JVM exits - never the real
> saves) and its map read tile by tile through CityMap.tileInput(), TilePainter.paint() and TileRaster.raster() at 8 px
> a plot, as the screen reads it. It writes:
> 
>   et4-a-medellin-viaduct.png   the window of 4 x 3 tiles (1,024 x 768 px) with the most plots of the runs on steep
>                                ground, a bridge over dry land (TilePainter.Painted.bridge off the water); when the run
>                                has none, et4-a-viaduct.txt says so
>   et4-b-medellin-tunnel.png    the window with the most plots of the runs on very steep ground (Painted.tunnel), and
>                                RunWorksCheck's fixture tile as et4-c-fixture.png (256 x 256 px: an east-west highway and a
>                                north-south track each across flat, steep, very steep and flat ground); when the run has
>                                no tunnel, et4-b-tunnel.txt says so and the fixture tile is written as
>                                et4-b-fixture-tunnel.png instead, the picture of a tunnel
>   README.md                    one line a picture
> 
> WHY (ET4 slice C): the map is checked by eye. RunWorksCheck holds the flags and the pixels; these show the game
> drawing a city's runs where the ground tilts. Not a harness: nothing here asserts, and nothing under docs/ but its
> outDir is written.

**Uses:** [TilePainter](TilePainter.md) (17), [CityMap](CityMap.md) (11), [World](World.md) (10), [EarthData](EarthData.md) (3), [TileRaster](TileRaster.md) (2), [RunWorksCheck](RunWorksCheck.md) (1), [Game](Game.md) (1), [LongPlaytest](LongPlaytest.md) (1), [GameFiles](GameFiles.md) (1), [CityLand](CityLand.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 56 | `RunRenders.PX` | `8, ACROSS = 4, DOWN = 3` | Pixels a plot, and the window in tiles: 8 x 32 x 4 by 8 x 32 x 3, 1,024 x 768. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 60 | `int runs, viaducts, tunnels` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 51 | 156 | **type** `public final class RunRenders` | Renders what ET4 slice C did to the map, for review: where a city's highways and railway run across steep ground, a viaduct, and across very steep ground, a tunnel, drawn by the game's own painters, headless. |
| 53 | 1 | `private RunRenders()` |  |
| 59 | 4 | **type** `private static final class Count` | What one tile of the map holds of the runs: their plots, and those on steep and on very steep ground. |
| 61 | 1 | `void add(Count o)` _(in RunRenders.Count)_ |  |
| 64 | 91 | `public static void main(String[] args) throws IOException` |  |
| 160 | 20 | `private static long[] best(Map<Long, Count> counts, boolean tunnels)` | The window's top-left tile with the most plots of the runs on steep ground (tunnels false) or on very steep (true), a window being ACROSS x DOWN tiles; null where no tile has any. |
| 181 | 1 | `private static long key(long tx, long ty)` |  |
| 184 | 16 | `private static BufferedImage window(CityMap map, long tx0, long ty0)` | A window of ACROSS x DOWN tiles from tile (tx0, ty0), as the screen draws it at PX a plot. |
| 201 | 5 | `private static void write(Path dir, String name, BufferedImage img) throws IOException` |  |

