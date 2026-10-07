# MapTiles.java - 310 lines · 22 methods · 4 constants · model

`ham/citybuildersim/MapTiles.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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
> (the world's ground, which World keeps safe) runs on the view's worker.

**Uses:** [MapFrame](MapFrame.md) (30), [TilePainter](TilePainter.md) (26), [World](World.md) (21), [TileRaster](TileRaster.md) (11), [BuildingVisual](BuildingVisual.md) (10), [CityMap](CityMap.md) (7), [CityLand](CityLand.md) (2)

**Used by (2):** [MapCheck](MapCheck.md), [MapView](MapView.md)

## Sections

| line | section |
|---:|---|
| 190 | THE BUDGET (spec-land 2.6: every cache within 48 MB) |
| 236 | THE FAR NODES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 27 | `MapTiles.TERRAIN_KEPT` | `2048` | Tiles' ground kept: 2,048 (spec-land 2.6), a kilobyte each - a far screen of NEAR_TILES_MOST and its margin. |
| 30 | `MapTiles.PAINTED_KEPT` | `64` | Painted tiles kept, with their inputs: 64 (star) - measured at up to about 36 KB each on MapCheck 7's densest screen, x 10,000 (the painter's arrays grow to 512), so the design's 1,024 would be 36 MB; the hover asks f... |
| 33 | `MapTiles.BUDGET_MB` | `48` | What every cache of the view together may hold, in MB: 48 (spec-land 2.6). |
| 36 | `MapTiles.DESIGN_W` | `1345, DESIGN_H = 806` | The view the budget is sized for, in pixels: the land office's expanded map in a 1,389 x 868 window - 1,345 across inside the pane's padding, 806 down under its head (UserInterface's chart pane, 0.7.23). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 40 | `final TilePainter.Input in` |  |
| 41 | `final TilePainter.Painted p` |  |
| 42 | `long stamp` |  |
| 45 | `private final CityMap map` |  |
| 46 | `private final Map<Long, byte[]> terrain` |  |
| 49 | `private final Map<Long, Kept> painted` |  |
| 54 | `private long paints, stamps` | How many tiles have been painted and how many stamps taken: what a harness reads. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 287 | **type** `public final class MapTiles` | The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again only when what it is painted from has changed, a few painted tiles kep... |
| 39 | 5 | **type** `private static final class Kept` | A tile's painted bytes, kept: its inputs, the picture and the stamp it was painted at. |
| 56 | 1 | `public MapTiles(CityMap map)` |  |
| 59 | 1 | `public CityMap map()` | The map these tiles are painted from. |
| 61 | 1 | `public long paints()` |  |
| 62 | 1 | `public long stamps()` |  |
| 65 | 5 | `public static long version(CityMap map, CityLand land)` | What a view's pictures are current against: the map's changes (CityMap.changes()), and the land's purchases and centre - a purchase between months owns ground at once (a tile's ownership is the live land's) that the m... |
| 72 | 1 | `public static long key(long tx, long ty)` | A tile's key: its column and row, the world's tiles numbering under 2^20 each way. |
| 75 | 10 | `public byte[] terrain(long tx, long ty)` | A tile's ground, from the world the first time and kept. |
| 87 | 5 | `public long input(long tx, long ty, TilePainter.Input in)` | A tile's inputs into `in` (its ground from the cache), and their stamp. |
| 99 | 13 | `public static long stamp(TilePainter.Input in)` | A stamp of everything a tile is painted from but its ground, which is the world's and never changes: what is owned, the buildings and road plots dealt to it, its neighbours' roads and its sites with their states and m... |
| 114 | 9 | `private static long packed(boolean[] flags)` | A plot-by-plot flag folded into one number, sixty-four plots a word. |
| 129 | 12 | `public TilePainter.Painted painted(long tx, long ty, TilePainter.Input in, long stamp)` | A tile painted from `in` (whose stamp is `stamp`): the kept picture when it was painted at that stamp, else painted now and kept. |
| 143 | 4 | `public TilePainter.Input paintedInput(long tx, long ty)` | The inputs a kept picture was painted from, or null when the tile is not kept. |
| 149 | 16 | `static void copy(TilePainter.Input a, TilePainter.Input b)` | A copy of a tile's inputs, the arrays reused. |
| 173 | 7 | `public long pixels(long tx, long ty, int level, int px, TilePainter.Input in, int[] out)` | The per-tile path a frame takes: the tile's inputs (its ground kept), painted - or the kept picture when its stamp has not moved - and rastered at a level's width into `out` (TileRaster.raster() at L0, its blocks at L... |
| 182 | 4 | `public static void raster(TilePainter.Input in, TilePainter.Painted p, int level, int px, int[] out)` | A painted tile rastered as a level draws it. |
| 188 | 1 | `public static int tileImagePixels(int px)` | Pixels in a tile's image at a width. |

### THE BUDGET (spec-land 2.6: every cache within 48 MB) (lines 190-235)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 7 | `public static long tilesKept(MapFrame f, int level, int px)` | The tiles a level's image cache keeps for a view this size (star): one screen at the level's least scale with a tile of margin round it (MapFrame.tilesAt()) - at L0 from L0_FROM at SMALL_TILE_PX and from BIG_TILES_FRO... |
| 211 | 6 | `public static long paintedBytes(TilePainter.Input in, TilePainter.Painted p)` | A kept painted tile's bytes, from its arrays' lengths: what PAINTED_KEPT of them weigh. |
| 224 | 11 | `public static double budgetBytes(MapFrame f, double paintedBytes)` | Every cache of the view together, in bytes, for a view this size: each level's images at four bytes a pixel (L0 at both widths, L1, L2), the far nodes (MapFrame.nodesAtMost()), the tiles' ground and PAINTED_KEPT paint... |

### THE FAR NODES (lines 236-310)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 5 | `public static byte[] nodeTerrain(long seed, long x0, long y0, long plots)` | A far node's ground: MapFrame.NODE_PX squared samples a `plots` apart from (x0, y0), a World class each. |
| 256 | 30 | `public void nodePixels(long x0, long y0, long plots, byte[] ground, int[] out)` | A far node's pixels from its ground: each sample's terrain colour, dimmed where the city does not own it (TileRaster's UNOWNED_DIM), and where it owns dry ground its district's tint over it - the class its buildings t... |
| 288 | 22 | `int tintOf(CityMap.District d)` | A district's tint for the far nodes: its colour with its opacity in the top byte, 0 for none built. |

