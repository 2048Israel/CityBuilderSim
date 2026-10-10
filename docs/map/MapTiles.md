# MapTiles.java - 367 lines · 26 methods · 4 constants · model

`ham/citybuildersim/MapTiles.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again only when what it is painted from has changed, a few painted tiles kept for the hover and a change of level, the per-tile path from inputs to pixels, and the far view's node pixels from the world's ground and the districts' counts.
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
> draws them.

**Uses:** [MapFrame](MapFrame.md) (31), [TilePainter](TilePainter.md) (30), [World](World.md) (29), [TileRaster](TileRaster.md) (11), [BuildingVisual](BuildingVisual.md) (10), [CityMap](CityMap.md) (9), [CityLand](CityLand.md) (2)

**Used by (2):** [MapCheck](MapCheck.md), [MapView](MapView.md)

## Sections

| line | section |
|---:|---|
| 240 | THE BUDGET (spec-land 2.6: every cache within 48 MB) |
| 293 | THE FAR NODES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `MapTiles.TERRAIN_KEPT` | `2048` | Tiles' ground kept: 2,048 (spec-land 2.6), a kilobyte each - a far screen of NEAR_TILES_MOST and its margin. |
| 34 | `MapTiles.PAINTED_KEPT` | `1` | Painted tiles kept, with their inputs: 1, the hover's (star RD2-5; 64 until 0.7.87) - about 31 KB each on MapCheck 7's densest screen, x 10,000 (the painter's arrays grow to 512). |
| 37 | `MapTiles.BUDGET_MB` | `48` | What every cache of the view together may hold, in MB: 48 (spec-land 2.6). |
| 40 | `MapTiles.DESIGN_W` | `1345, DESIGN_H = 806` | The view the budget is sized for, in pixels: the land office's expanded map in a 1,389 x 868 window - 1,345 across inside the pane's padding, 806 down under its head (UserInterface's chart pane, 0.7.23). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `final TilePainter.Input in` |  |
| 45 | `final TilePainter.Painted p` |  |
| 46 | `long stamp` |  |
| 49 | `private final CityMap map` |  |
| 50 | `private final Map<Long, byte[]> terrain` |  |
| 53 | `private final Map<Long, Kept> painted` |  |
| 58 | `private long paints, stamps` | How many tiles have been painted and how many stamps taken: what a harness reads. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 28 | 340 | **type** `public final class MapTiles` | The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again only when what it is painted from has changed, a few painted tiles kep... |
| 43 | 5 | **type** `private static final class Kept` | A tile's painted bytes, kept: its inputs, the picture and the stamp it was painted at. |
| 60 | 1 | `public MapTiles(CityMap map)` |  |
| 63 | 1 | `public CityMap map()` | The map these tiles are painted from. |
| 65 | 1 | `public long paints()` |  |
| 66 | 1 | `public long stamps()` |  |
| 69 | 5 | `public static long version(CityMap map, CityLand land)` | What a view's pictures are current against: the map's changes (CityMap.changes()), and the land's purchases and centre - a purchase between months owns ground at once (a tile's ownership is the live land's) that the m... |
| 76 | 1 | `public static long key(long tx, long ty)` | A tile's key: its column and row, the world's tiles numbering under 2^20 each way. |
| 79 | 10 | `public byte[] terrain(long tx, long ty)` | A tile's ground, from the world the first time and kept. |
| 91 | 3 | `public boolean ready(long tx, long ty)` | Whether tile (tx, ty) paints without planning (0.7.88): the district plans it is drawn from are drawn (CityMap.tileReady()). |
| 96 | 3 | `public java.util.List<CityMap.Job> jobs(long tx, long ty)` | The jobs that draw tile (tx, ty)'s plans, to run in order on one thread away from the screen's and then keep (CityMap.adopt()); none handed out twice. |
| 101 | 5 | `public long input(long tx, long ty, TilePainter.Input in)` | A tile's inputs into `in` (its ground from the cache), and their stamp - its plans made here if they are not drawn (a harness's way; the view asks ready() first). |
| 115 | 24 | `public static long stamp(TilePainter.Input in)` | A stamp of everything a tile is painted from but its ground, which is the world's and never changes: what is owned, its plan's streets and those just beyond its edges, the city's highways and track through it, its bui... |
| 141 | 9 | `private static long packed(byte[] codes)` | Bytes a plot folded into one number, eight a word. |
| 152 | 9 | `private static long packed(boolean[] flags)` | A plot-by-plot flag folded into one number, sixty-four plots a word. |
| 167 | 12 | `public TilePainter.Painted painted(long tx, long ty, TilePainter.Input in, long stamp)` | A tile painted from `in` (whose stamp is `stamp`): the kept picture when it was painted at that stamp, else painted now and kept. |
| 181 | 4 | `public TilePainter.Input paintedInput(long tx, long ty)` | The inputs a kept picture was painted from, or null when the tile is not kept. |
| 187 | 28 | `static void copy(TilePainter.Input a, TilePainter.Input b)` | A copy of a tile's inputs, the arrays reused. |
| 223 | 7 | `public long pixels(long tx, long ty, int level, int px, TilePainter.Input in, int[] out)` | The per-tile path a frame takes: the tile's inputs (its ground kept), painted - or the kept picture when its stamp has not moved - and rastered at a level's width into `out` (TileRaster.raster() at L0, its blocks at L... |
| 232 | 4 | `public static void raster(TilePainter.Input in, TilePainter.Painted p, int level, int px, int[] out)` | A painted tile rastered as a level draws it. |
| 238 | 1 | `public static int tileImagePixels(int px)` | Pixels in a tile's image at a width. |

### THE BUDGET (spec-land 2.6: every cache within 48 MB) (lines 240-292)

| line | len | member | says |
|---:|---:|---|---|
| 252 | 7 | `public static long tilesKept(MapFrame f, int level, int px)` | The tiles a level's image cache keeps for a view this size (star): one screen at the level's least scale with a tile of margin round it (MapFrame.tilesAt()) - at L0 from L0_FROM at SMALL_TILE_PX and from BIG_TILES_FRO... |
| 261 | 6 | `public static long paintedBytes(TilePainter.Input in, TilePainter.Painted p)` | A kept painted tile's bytes, from its arrays' lengths: what PAINTED_KEPT of them weigh - since 0.7.88 its plan's streets and runs and its buildings' boxes in, its plots' use, kind, width, role, flags, building and sit... |
| 276 | 3 | `public static double budgetBytes(MapFrame f, double paintedBytes, double planBytes)` | Every cache of the view together, in bytes, for a view this size: each level's images at four bytes a pixel (L0 at both widths, L1, L2), the far nodes (MapFrame.nodesAtMost()), the tiles' ground and PAINTED_KEPT paint... |
| 281 | 11 | `public static double budgetBytes(MapFrame f, double paintedBytes)` | ...without the plans: what the view's own caches hold. |

### THE FAR NODES (lines 293-367)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 5 | `public static byte[] nodeTerrain(long seed, long x0, long y0, long plots)` | A far node's ground: MapFrame.NODE_PX squared samples a `plots` apart from (x0, y0), a World class each. |
| 313 | 30 | `public void nodePixels(long x0, long y0, long plots, byte[] ground, int[] out)` | A far node's pixels from its ground: each sample's terrain colour, dimmed where the city does not own it (TileRaster's UNOWNED_DIM), and where it owns dry ground its district's tint over it - the class its buildings t... |
| 345 | 22 | `int tintOf(CityMap.District d)` | A district's tint for the far nodes: its colour with its opacity in the top byte, 0 for none built. |

