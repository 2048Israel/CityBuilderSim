# IslandRailRenders.java - 123 lines · 7 methods · 1 constants · tools

`ham/citybuildersim/tools/IslandRailRenders.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> Renders what RI1 did for an island the city's port links, for review: the island's own railway, a rail hub nearest the terminal with
> its track and yard, beside the home hub's, drawn by the game's own painters, headless.
> 
>     java -cp "target/classes;<gson.jar>" -Dcbs.earth=data/earth-2.bin ham.citybuildersim.tools.IslandRailRenders [outDir]
> 
> The city is IslandRailCheck's section 8 (IslandRailCheck.portIsland(): Rotterdam, the island of 648 linked by its General Cargo
> Terminal, a railway with yards, buildings on the island; built in GameFiles.scratch() folders deleted when the JVM exits - never the
> real saves) and its map read tile by tile through CityMap.tileInput(), TilePainter.paint() and TileRaster.raster() at 8 px a plot, as
> the screen reads it. outDir is docs/queue/RI1 unless named; the fixture is skipped, and nothing written, where the Earth file is not
> beside the classes. It writes:
> 
>   ri1-a-island-port.png   the window of 4 x 3 tiles (1,024 x 768 px) centred on the island's rail hub: the terminal, the hub, the
>                           island's track and the yard that stands on it
>   ri1-b-home.png          the same window centred on the home rail hub, for comparison
>   ri1-renders.txt         one line a render: its tiles, the hub and terminal plots, the island's rail plots (the net's piecePlots
>                           for the island's piece) and yards, and the railway plots the window shows
> 
> WHY (RI1 slice C): the railway on the island is checked by eye. IslandRailCheck holds the hub, the track and the yards; these show
> the game drawing them. Not a harness: nothing here asserts, and nothing under docs/ but its outDir is written.

**Uses:** [World](World.md) (8), [TilePainter](TilePainter.md) (7), [IslandRailCheck](IslandRailCheck.md) (2), [CityMap](CityMap.md) (2), [TileRaster](TileRaster.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `IslandRailRenders.PX` | `8, ACROSS = 4, DOWN = 3` | Pixels a plot, and the window in tiles: 8 x 32 x 4 by 8 x 32 x 3, 1,024 x 768 (RunRenders'). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 39 | 85 | **type** `public final class IslandRailRenders` | Renders what RI1 did for an island the city's port links, for review: the island's own railway, a rail hub nearest the terminal with its track and yard, beside the home hub's, drawn by the game's own painters, headless. |
| 41 | 1 | `private IslandRailRenders()` |  |
| 46 | 31 | `public static void main(String[] args) throws Exception` |  |
| 79 | 3 | `private static long[] windowAt(long[] plot)` | The window's top-left tile that centres a plot: the hub's tile is the middle row, and the middle column boundary the nearer to it. |
| 84 | 8 | `private static String describe(String what, long[] at, long[] hub, String hubName, long[] mid, int railShown)` | One render's line: the tiles, the hub, the terminal (null for none) and whether it lies in the window, and the railway plots the window shows. |
| 93 | 5 | `private static String yardList(List<long[]> yards)` |  |
| 100 | 17 | `private static BufferedImage window(CityMap map, long tx0, long ty0, int[] railPlots)` | A window of ACROSS x DOWN tiles from tile (tx0, ty0), as the screen draws it at PX a plot; railPlots[0] is how many of its plots the painter marks as railway track. |
| 118 | 5 | `private static void write(Path dir, String name, BufferedImage img) throws IOException` |  |

