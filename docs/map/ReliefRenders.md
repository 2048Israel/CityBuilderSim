# ReliefRenders.java - 152 lines · 6 methods · 2 constants · tools

`ham/citybuildersim/tools/ReliefRenders.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> Renders the map's relief for review: six PNG files drawn by the game's own painters, headless.
> 
>     java -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.ReliefRenders [earth-2.bin [outDir [name...]]]
> 
> The file is the argument, else -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); outDir is docs/queue/ET1
> unless named; names (render-a ... render-f) pick which to write, all six otherwise. It writes, each under about
> a megabyte:
> 
>   et1-a-medellin-plots.png    Medellin at the plane's plot zoom (2.7 km east of the valley floor, where the hills begin): TileRaster.raster() over the plots' own ground and
>                               relief (World.tileTerrain(), tileRelief()), 4 px a plot, the city's ground undimmed
>   et1-b-medellin-far.png      the Andes round it as a far node paints them, 16 plots (0.48 km) a pixel: the far
>                               rule (MapTiles.nodeTerrain() with its relief) and TileRaster.colour()
>   et1-c-halifax-narrows.png   the Narrows at plot zoom, 5 px a plot: the channel shallow sea, Bedford Basin deep
>   et1-d-everest-far.png       Everest at a far stride, 8 plots (0.24 km) a pixel: snow white over the greys
>   et1-e-globe-andes.png       the globe's picture (GlobePicture.frame()) over the Andes
>   et1-f-globe-himalaya.png    ...over the Himalaya and Tibet, closer (4 km a pixel: two pixels a texel)
> 
> WHY (ET1 slice D): the map is checked by eye, and the study's mockups (docs/studies/earth2/) are the standard the
> game's own colours are held to. These are the game drawing the same places; MapCheck holds the colours, these show them.
> Not a harness: nothing here asserts, and nothing under docs/ but its outDir is written.

**Uses:** [World](World.md) (7), [GlobePicture](GlobePicture.md) (6), [EarthData](EarthData.md) (5), [TilePainter](TilePainter.md) (5), [EarthTerrain](EarthTerrain.md) (3), [TileRaster](TileRaster.md) (2), [GlobeFrame](GlobeFrame.md) (2), [MapFrame](MapFrame.md) (1), [MapTiles](MapTiles.md) (1)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 45 | `ReliefRenders.SEED` | `1` | The world's seed the plot-zoom renders are drawn on: any (the cover and relief noise are the world's); EarthTerrainCheck's. |
| 48 | `ReliefRenders.W` | `1000, H = 750` | The renders' size in pixels: 1,000 x 750, inside the 800 x 600 to 1,200 x 900 the review asked. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 40 | 113 | **type** `public final class ReliefRenders` | Renders the map's relief for review: six PNG files drawn by the game's own painters, headless. |
| 42 | 1 | `private ReliefRenders()` |  |
| 50 | 23 | `public static void main(String[] args) throws IOException` |  |
| 74 | 5 | `private static void write(Path dir, String name, BufferedImage img) throws IOException` |  |
| 81 | 35 | `static BufferedImage plots(String id, double lat, double lon, int px, long dx, long dy)` | The ground at the plane's plot zoom: W x H pixels at `px` a plot, centred (dx, dy) plots from the spot (lat, lon), tile by tile through TileRaster.raster() with the city owning all of it (nothing dimmed, no streets or... |
| 118 | 22 | `static BufferedImage far(String id, double lat, double lon, int stride)` | A far view: W x H pixels, each `stride` plots across, centred on the spot - far nodes (MapTiles.nodeTerrain(), the ground and its relief) coloured by TileRaster.colour(), as MapTiles.nodePixels() colours an undimmed p... |
| 142 | 10 | `static BufferedImage globe(EarthData data, double cityLat, double cityLon, double viewLat, double viewLon, double kmPx)` | The globe over (viewLat, viewLon) at `kmPx` a pixel, W x H: the planet's picture (GlobePicture.of() at ARCMIN) drawn by frame() with its code colours, north up, space dark. |

