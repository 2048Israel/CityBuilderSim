# SlopeRenders.java - 275 lines · 10 methods · 4 constants · tools

`ham/citybuildersim/tools/SlopeRenders.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> Renders what ET2 did to the map and the founding card, for review: a city played on the real Earth and drawn by the
> game's own painters, headless, and the card's words for three spots.
> 
>     java -cp "target/classes;<gson.jar>;<javafx jars>" ham.citybuildersim.tools.SlopeRenders [earth-2.bin [outDir [name...]]]
> 
> The file is the argument, else -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); outDir is docs/queue/ET2
> unless named; names (render-a, render-b, render-c) pick which to write, all three otherwise. -Dslope.months=N plays
> the city N months (2,000 unless named: SlopeCheck 9's run, the length at which its land office's offers have
> brought it steep ground). The city is founded at Medellin (LongPlaytest.earthCity(): the playtest's own play, in a
> GameFiles.scratch() folder that is deleted when the JVM exits - never the real saves) and its map read tile by tile
> through CityMap.tileInput(), TilePainter.paint() and TileRaster.raster(), as the screen reads it. It writes, each
> under about a megabyte:
> 
>   et2-a-medellin-city.png    the city at plot zoom, 4 px a plot, over the window of 8 x 6 tiles (1,024 x 768 px)
>                              with the most buildings among those that hold steep ground and buildings on foothill:
>                              streets and buildings on the flat and the foothill, none on the steep greys
>   et2-b-medellin-works.png   the window round the most works on steep ground (a mine or a quarry standing where no
>                              building may) - not written when the run has none
>   et2-c-refusal.txt          the founding card's words (FoundingScreen's, in its order) for Medellin's site (the spot itself and one the snap moves),
>                              for a spot in the Andes at -13.2, -72.5, for Everest's slopes and for its summit
> 
> The render-c card needs JavaFX on the classpath (FoundingScreen is the screen's class and reads its colours); the
> other two do not.
> 
> WHY (ET2 slice E): the map is checked by eye. SlopeCheck holds the counts - no building on steep ground, a works
> on it, the preparation charged - and these show the game drawing a city where the ground tilts. Not a harness:
> nothing here asserts, and nothing under docs/ but its outDir is written.

**Uses:** [TilePainter](TilePainter.md) (16), [CityMap](CityMap.md) (11), [LandManager](LandManager.md) (9), [World](World.md) (8), [EarthSite](EarthSite.md) (7), [FoundingScreen](FoundingScreen.md) (6), [EarthData](EarthData.md) (4), [Founding](Founding.md) (2), [Game](Game.md) (1), [LongPlaytest](LongPlaytest.md) (1), [GameFiles](GameFiles.md) (1), [CityLand](CityLand.md) (1), [BuildingVisual](BuildingVisual.md) (1), [TileRaster](TileRaster.md) (1)

## Sections

| line | section |
|---:|---|
| 89 | · the city |
| 232 | · the founding card |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `SlopeRenders.PX` | `4, ACROSS = 8, DOWN = 6` | Pixels a plot is drawn at, and the window in tiles: 4 x 32 x 8 by 4 x 32 x 6, 1,024 x 768 - inside the review's 800 x 600 to 1,200 x 900. |
| 68 | `SlopeRenders.SPOTS` | `{ { 6.25, - 75.57 }, { 6.12, - 75.57 }, { - 13.2, - 72.5 }, { 27.60, 86.70 },...` | Where the founding cards are measured: Medellin's spot (SlopeCheck's), a spot in the hills south of it that the snap moves 1.4 km, a spot in the Andes slice C probed, Everest's slopes (SlopeCheck's) and its summit (Ea... |
| 69 | `SlopeRenders.SPOT_NAMES` | `{ "Medellin", "the hills south of Medellin, a spot the snap moves", "the Ande...` |  |
| 198 | `SlopeRenders.CEILING` | `2_500` | The buildings after which a window is not the better for more. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 93 | `int buildings, steepDry, steepOwned, foothillBuilt, onSteep, plain` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 216 | **type** `public final class SlopeRenders` | Renders what ET2 did to the map and the founding card, for review: a city played on the real Earth and drawn by the game's own painters, headless, and the card's words for three spots. |
| 62 | 1 | `private SlopeRenders()` |  |
| 71 | 17 | `public static void main(String[] args) throws IOException` |  |

### the city (lines 89-231)

| line | len | member | says |
|---:|---:|---|---|
| 92 | 4 | **type** `private static final class Count` | What one tile of the map holds, counted for choosing a window: all in plots or buildings. |
| 94 | 1 | `void add(Count o)` _(in SlopeRenders.Count)_ |  |
| 97 | 90 | `private static void writeCity(Path outDir, boolean a, boolean b) throws IOException` |  |
| 193 | 3 | `private static long scoreOf(Count w)` | A window's worth as picture a: a city to look at with ground of its own that nothing stands on in it - its buildings (to a ceiling: a window all city shows no steep ground) and, a plot for a building and a half, the s... |
| 199 | 6 | `private static String tally(Map<String, int[]> by)` |  |
| 206 | 1 | `private static long key(long tx, long ty)` |  |
| 209 | 16 | `private static BufferedImage window(CityMap map, long tx0, long ty0)` | A window of ACROSS x DOWN tiles from tile (tx0, ty0), as the screen draws it: CityMap.tileInput() with the city's own ownership, streets and buildings, painted and rastered at PX a plot. |
| 226 | 5 | `private static void write(Path dir, String name, BufferedImage img) throws IOException` |  |

### the founding card (lines 232-275)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 40 | `private static void writeCards(Path to) throws IOException` | The card's words for each spot, as FoundingScreen.fillCard() lays them: the refusal where there is no site or the site is refused, else the site's lines in order. |

