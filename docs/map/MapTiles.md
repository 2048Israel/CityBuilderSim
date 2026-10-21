# MapTiles.java - 592 lines · 33 methods · 8 constants · model

`ham/citybuildersim/MapTiles.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again only when what it is painted from has changed, a few painted tiles kept for the hover and a change of level, the per-tile path from inputs to pixels, and the far view's node pixels from the world's ground, the districts' pictures and shares, and the city's runs and edge.
> 
> WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6). The
> view holds its images (ui/MapView: JavaFX's WritableImage, one cache a
> level); what it paints them from is arithmetic, and kept here so MapCheck
> can time the path a frame takes and see that a tile whose counts did not
> change keeps its picture. A month changes a few districts; a district's
> change moves its own tiles (CityMap's deal since 0.7.64; up to half a
> district into its neighbours before); a purchase changes what is owned.
> Rather than track which, the view stamps every tile it shows when the map
> or the land moved (stamp()), and repaints only those whose stamp changed -
> the spec's "a district change repaints only the tiles whose counts
> changed, and a purchase repaints the districts it touched", as one rule.
> 
> NOT THREAD-SAFE: the FX thread's alone, as CityMap is. Only nodeTerrain()
> (the world's ground, which World keeps safe) and, since 0.7.88, the
> district plans' jobs (CityMap.Job: their inputs gathered on the FX thread,
> kept there by CityMap.adopt()) run on the view's worker: a tile is painted
> only once ready() says its plans are drawn, and jobs() hands out what
> draws them. Since 0.7.128 (batch MM1) a plan's job draws its district's
> picture with it (DistrictPicture), which the far nodes read; a node drawn
> without one it wanted says so (nodePixels()'s count, wanted()), and the
> view asks CityMap.pictureJobs() for them while picturesFit().

**Uses:** [World](World.md) (48), [MapFrame](MapFrame.md) (36), [TilePainter](TilePainter.md) (32), [CityMap](CityMap.md) (16), [TileRaster](TileRaster.md) (11), [LandGrid](LandGrid.md) (9), [DistrictPicture](DistrictPicture.md) (8), [BuildingVisual](BuildingVisual.md) (6), [CityRuns](CityRuns.md) (4), [EarthProj](EarthProj.md) (3), [CityLand](CityLand.md) (2), [CityShore](CityShore.md) (1)

**Used by (9):** [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [GlobeCheck](GlobeCheck.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [MiniMapFrame](MiniMapFrame.md), [RailDrawCheck](RailDrawCheck.md)

## Sections

| line | section |
|---:|---|
| 261 | THE BUDGET (spec-land 2.6: every cache within 48 MB) |
| 315 | THE FAR NODES |
| 343 | · the far nodes from the district pictures (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `MapTiles.TERRAIN_KEPT` | `2048` | Tiles' ground kept: 2,048 (spec-land 2.6), a kilobyte each - a far screen of NEAR_TILES_MOST and its margin. |
| 37 | `MapTiles.PAINTED_KEPT` | `1` | Painted tiles kept, with their inputs: 1, the hover's (star RD2-5; 64 until 0.7.87) - about 31 KB each on MapCheck 7's densest screen, x 10,000 (the painter's arrays grow to 512). |
| 40 | `MapTiles.BUDGET_MB` | `48` | What every cache of the view together may hold, in MB: 48 (spec-land 2.6). |
| 43 | `MapTiles.FAR_HIGHWAY_LIGHTEN` | `0.4` | How much the far view's highway line is lightened toward white against the dark ground (spec-freight 4.8): 40%. |
| 46 | `MapTiles.DESIGN_W` | `1345, DESIGN_H = 806` | The view the budget is sized for, in pixels: the land office's expanded map in a 1,389 x 868 window - 1,345 across inside the pane's padding, 806 down under its head (UserInterface's chart pane, 0.7.23). |
| 346 | `MapTiles.PICTURE_FROM_PX` | `4` | A district is drawn from its picture (DistrictPicture) on a far node where it is at least this many of the node's pixels across: 4 (the study's 8.2) - a node's pixel is shown 1 to 2 screen pixels wide (MapFrame.nodePl... |
| 349 | `MapTiles.EDGE` | `0xffffffff` | The city's edge over a far node's pixels: white at 0.55, the view's edge's (MapView's EDGE_ALPHA, since 0.7.61), on the inner ring of the pixels drawn as the city's and on any holding ground of it smaller than a pixel... |
| 352 | `MapTiles.EDGE_ALPHA` | `0.55` | ...its white's alpha: 0.55, the view's own edge's since 0.7.61 - ui/MapView's EDGE_ALPHA reads this one since 0.7.128, so the two are one dial. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `final TilePainter.Input in` |  |
| 51 | `final TilePainter.Painted p` |  |
| 52 | `long stamp` |  |
| 55 | `private final CityMap map` |  |
| 56 | `private final Map<Long, byte[]> terrain` |  |
| 59 | `private final Map<Long, Kept> painted` |  |
| 64 | `private long paints, stamps` | How many tiles have been painted and how many stamps taken: what a harness reads. |
| 355 | `private final java.util.Set<CityMap.District> wanted` | The districts the last nodePixels() drew at PICTURE_FROM_PX or more with no current picture: what the view asks the pictures of (CityMap.pictureJobs()). |
| 358 | `private boolean[] lastEdge` | Which of the last nodePixels()'s pixels it drew as the city's edge, and as the far skeleton's - a highway, the railway, a yard or a sea terminal's dot: a harness's. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 562 | **type** `public final class MapTiles` | The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again only when what it is painted from has changed, a few painted tiles kep... |
| 49 | 5 | **type** `private static final class Kept` | A tile's painted bytes, kept: its inputs, the picture and the stamp it was painted at. |
| 66 | 1 | `public MapTiles(CityMap map)` |  |
| 69 | 1 | `public CityMap map()` | The map these tiles are painted from. |
| 71 | 1 | `public long paints()` |  |
| 72 | 1 | `public long stamps()` |  |
| 75 | 5 | `public static long version(CityMap map, CityLand land)` | What a view's pictures are current against: the map's changes (CityMap.changes()), and the land's purchases and centre - a purchase between months owns ground at once (a tile's ownership is the live land's) that the m... |
| 82 | 1 | `public static long key(long tx, long ty)` | A tile's key: its column and row, the world's tiles numbering under 2^20 each way. |
| 85 | 10 | `public byte[] terrain(long tx, long ty)` | A tile's ground, from the world the first time and kept. |
| 97 | 3 | `public boolean ready(long tx, long ty)` | Whether tile (tx, ty) paints without planning (0.7.88): the district plans it is drawn from are drawn (CityMap.tileReady()). |
| 102 | 3 | `public java.util.List<CityMap.Job> jobs(long tx, long ty)` | The jobs that draw tile (tx, ty)'s plans, to run in order on one thread away from the screen's and then keep (CityMap.adopt()); none handed out twice. |
| 107 | 5 | `public long input(long tx, long ty, TilePainter.Input in)` | A tile's inputs into `in` (its ground from the cache), and their stamp - its plans made here if they are not drawn (a harness's way; the view asks ready() first). |
| 123 | 32 | `public static long stamp(TilePainter.Input in)` | A stamp of everything a tile is painted from but its ground, which is the world's and never changes: what is owned, its plan's streets and those just beyond its edges, the city's highways and track through it, its bui... |
| 157 | 9 | `private static long packed(byte[] codes)` | Bytes a plot folded into one number, eight a word. |
| 168 | 9 | `private static long packed(boolean[] flags)` | A plot-by-plot flag folded into one number, sixty-four plots a word. |
| 183 | 12 | `public TilePainter.Painted painted(long tx, long ty, TilePainter.Input in, long stamp)` | A tile painted from `in` (whose stamp is `stamp`): the kept picture when it was painted at that stamp, else painted now and kept. |
| 197 | 4 | `public TilePainter.Input paintedInput(long tx, long ty)` | The inputs a kept picture was painted from, or null when the tile is not kept. |
| 203 | 33 | `static void copy(TilePainter.Input a, TilePainter.Input b)` | A copy of a tile's inputs, the arrays reused. |
| 244 | 7 | `public long pixels(long tx, long ty, int level, int px, TilePainter.Input in, int[] out)` | The per-tile path a frame takes: the tile's inputs (its ground kept), painted - or the kept picture when its stamp has not moved - and rastered at a level's width into `out` (TileRaster.raster() at L0, its blocks at L... |
| 253 | 4 | `public static void raster(TilePainter.Input in, TilePainter.Painted p, int level, int px, int[] out)` | A painted tile rastered as a level draws it. |
| 259 | 1 | `public static int tileImagePixels(int px)` | Pixels in a tile's image at a width. |

### THE BUDGET (spec-land 2.6: every cache within 48 MB) (lines 261-314)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 7 | `public static long tilesKept(MapFrame f, int level, int px)` | The tiles a level's image cache keeps for a view this size (star): one screen at the level's least scale with a tile of margin round it (MapFrame.tilesAt()) - at L0 from L0_FROM at SMALL_TILE_PX and from BIG_TILES_FRO... |
| 282 | 6 | `public static long paintedBytes(TilePainter.Input in, TilePainter.Painted p)` | A kept painted tile's bytes, from its arrays' lengths: what PAINTED_KEPT of them weigh - since 0.7.88 its plan's streets and runs and its buildings' boxes in, its plots' use, kind, width, role, flags, building and sit... |
| 298 | 3 | `public static double budgetBytes(MapFrame f, double paintedBytes, double planBytes)` | Every cache of the view together, in bytes, for a view this size: each level's images at four bytes a pixel (L0 at both widths, L1, L2), the far nodes (MapFrame.nodesAtMost()), the tiles' ground and PAINTED_KEPT paint... |
| 303 | 11 | `public static double budgetBytes(MapFrame f, double paintedBytes)` | ...without the plans: what the view's own caches hold. |

### THE FAR NODES (lines 315-342)

| line | len | member | says |
|---:|---:|---|---|
| 320 | 3 | `public static byte[] nodeTerrain(long seed, long x0, long y0, long plots)` | A far node's ground: MapFrame.NODE_PX squared samples a `plots` apart from (x0, y0), a World class each. |
| 334 | 8 | `public static byte[] nodeTerrain(World.Key world, long x0, long y0, long plots)` | ...on the world a key names (0.7.115; the map's: CityMap.worldKey()). |

### the far nodes from the district pictures (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) (lines 343-592)

| line | len | member | says |
|---:|---:|---|---|
| 360 | 1 | `public java.util.Set<CityMap.District> wanted()` |  |
| 361 | 1 | `public boolean[] lastEdge()` |  |
| 362 | 1 | `public boolean[] lastRun()` |  |
| 386 | 116 | `public int nodePixels(long x0, long y0, long plots, byte[] ground, int[] out)` | A far node's pixels from its ground (0.7.128, batch MM1; spec-globe 8.2): each sample's terrain colour, dimmed where the city does not own it (TileRaster's UNOWNED_DIM); over the city's districts what stands there - w... |
| 511 | 33 | `private void stroke(CityRuns.Net net, int colour, long x0, long y0, long plots, int[] out)` | A net's stretches over a far node's pixels in a colour: every pixel holding a plot of a stretch - each step's, from the plot it starts from (its arm's start, or the last of the stretch before) to its last, and a 45-de... |
| 546 | 8 | `private void mark(long x, long y, long x0, long y0, long plots, int colour, int[] out)` | Plot (x, y)'s pixel on the node, when it is on it, in a run's colour. |
| 556 | 13 | `private void dot(double x, double y, long x0, long y0, long plots, int colour, int[] out)` | A yard's or a sea terminal's 2 x 2 pixel dot at its box's middle, in TRANSPORT's fill (spec-freight 4.8). |
| 578 | 14 | `public boolean picturesFit(MapFrame f)` | Whether a far view asks for the pictures of the built districts it shows (0.7.128): while they are no more than CityMap.PICTURES_KEPT and lie in no more of the map's bands than it keeps the links of (CityMap.BANDS_KEP... |

