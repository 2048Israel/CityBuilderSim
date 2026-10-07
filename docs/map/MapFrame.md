# MapFrame.java - 231 lines · 37 methods · 14 constants · model

`ham/citybuildersim/MapFrame.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The map view's arithmetic without the toolkit: where the view looks and how close, how a drag and a notch of the wheel move it, which level of detail it draws and which tiles or far nodes that takes, and how long its scale bar is - what ui/MapView draws by and MapCheck holds.
> 
> WHY THIS EXISTS (0.7.61, batch J4; the project's spec-land.md 2.6). The
> map is drawn on a JavaFX Canvas, and JavaFX cannot draw headless in the
> cloud loop, so the look and a smooth pan are checked by eye on the PC. The
> half of the view that is arithmetic is kept out of the toolkit, as
> ChartModel is for TimeChart's charts, so a harness can hold what the eye
> cannot: that a plot under the pointer stays under it through a zoom, that
> the zoom stops at the whole world and at 16 px a plot, that the level of
> detail changes at the mockup's thresholds, and that a screen asks for the
> tiles it shows and no more.
> 
> Coordinates are the world's plots (World, 0 to SIDE each way, y south);
> the view is a centre in plots and a scale in screen pixels a plot.
> 
> THE LEVELS (the mockup's thresholds, spec-land 2.6):
>   - L0, L0_FROM px a plot and closer: painted tiles, buildings one by one,
>     rastered at SMALL_TILE_PX a plot, at BIG_TILE_PX past BIG_TILES_FROM
>     (where the raster draws edges and the road casings), drawn scaled;
>   - L1, L1_FROM to L0_FROM: the painted tile as the mockup's middle-view
>     blocks of four plots (TileRaster.blocks()), rastered at MIDDLE_TILE_PX;
>   - L2, below L1_FROM: the far view's blocks of eight, gravel hidden, at a
>     pixel a plot - while the view holds no more than NEAR_TILES_MOST tiles;
>   - FAR, past that: node images NODE_PX across from the world's terrain
>     and the districts' counts, one image pixel nodePlots() plots.

**Uses:** [World](World.md) (14), [TileRaster](TileRaster.md) (2)

**Used by (4):** [LandMap](LandMap.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `MapFrame.ZOOM_STEP` | `1.25` | A notch of the wheel zooms by this much, at the pointer: 1.25 (spec-land 2.6). |
| 36 | `MapFrame.MOST_PX_A_PLOT` | `16` | The closest the view comes: 16 px a plot (spec-land 2.6), a plot's buildings and roads drawn at the raster's own widths. |
| 39 | `MapFrame.L0_FROM` | `3.2` | L0 from here: 3.2 px a plot (the mockup's): buildings one by one. |
| 42 | `MapFrame.L1_FROM` | `1.4` | L1 from here to L0: 1.4 px a plot (the mockup's): blocks of four plots; below it, blocks of eight. |
| 45 | `MapFrame.BIG_TILES_FROM` | `6` | Past this the L0 tiles are rastered at BIG_TILE_PX: 6 px a plot (spec-land 2.6), where TileRaster begins to edge the buildings (EDGE_FROM) and case the roads. |
| 48 | `MapFrame.SMALL_TILE_PX` | `4` | Pixels a plot an L0 tile is rastered at up to BIG_TILES_FROM: 4 (spec-land 2.6), TileRaster's first width with every road's line drawn. |
| 51 | `MapFrame.BIG_TILE_PX` | `8` | ...and past it: 8 (spec-land 2.6). |
| 54 | `MapFrame.MIDDLE_TILE_PX` | `2` | Pixels a plot an L1 tile is rastered at: 2 - the blocks of four drawn at the middle view's own scale, as J3b's renders drew them (round(1.74) at Jerus's expanded view), so a road is a line, not a band, when drawn up t... |
| 57 | `MapFrame.FAR_TILE_PX` | `1` | ...and an L2 tile: 1, a pixel a plot, drawn smaller than it is. |
| 60 | `MapFrame.NEAR_TILES_MOST` | `1024` | L2 draws painted tiles while the view holds no more than this many (star): 1,024 - a city the size of Jerus's (about 350 tiles fitted into the land office's small map) is drawn from its painted streets, as J3b's rende... |
| 63 | `MapFrame.NODE_PX` | `128` | A far node image's side, in pixels: 128 (star) - shown between 128 and 256 px, so about 96 of them at most fill a 1,345 x 806 view. |
| 66 | `MapFrame.FIT_MARGIN` | `0.94` | A fitted view leaves this much of itself round what it fits: 0.94 of the view (J3b's renders), a margin of 3% each side. |
| 69 | `MapFrame.OPENING_MARGIN` | `0.35` | The land office opens on the city's own ground with this share of its half-size round it each way (star): 0.35 - the near part of every lane's offer is in view, where fitting every offer whole would shrink Jerus's cit... |
| 72 | `MapFrame.L0` | `0, L1 = 1, L2 = 2, FAR = 3` | The levels. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `private double width, height, cx, cy, scale` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 30 | 202 | **type** `public final class MapFrame` | The map view's arithmetic without the toolkit: where the view looks and how close, how a drag and a notch of the wheel move it, which level of detail it draws and which tiles or far nodes that takes, and how long its ... |
| 77 | 7 | `public MapFrame(double width, double height)` | A view of width x height pixels, looking at the whole world. |
| 85 | 1 | `public double width()` |  |
| 86 | 1 | `public double height()` |  |
| 88 | 1 | `public double centreX()` | Where it looks, in plots. |
| 89 | 1 | `public double centreY()` |  |
| 91 | 1 | `public double scale()` | How close: screen pixels a plot. |
| 94 | 1 | `public double leastScale()` | The farthest the view goes: the whole world across its shorter side - about 28 km a pixel at the land office's expanded size. |
| 97 | 5 | `public void resize(double w, double h)` | A new size, the centre and scale kept (clamped to the new least). |
| 104 | 6 | `public void set(double x, double y, double s)` | Looks at (x, y) in plots, at a scale; both clamped. |
| 111 | 6 | `private void clamp()` |  |
| 119 | 4 | `public void fit(double x0, double y0, double x1, double y1)` | Fits a box of plots (x0, y0) to (x1, y1) into the view, FIT_MARGIN of it, centred. |
| 125 | 8 | `public void zoomAt(double sx, double sy, double factor)` | Zooms by a factor about a screen point: the plot under it stays under it, unless a clamp stops it. |
| 135 | 3 | `public void zoomNotches(double sx, double sy, double notches)` | ...by notches of the wheel, ZOOM_STEP each, in (positive) or out. |
| 140 | 5 | `public void pan(double dx, double dy)` | A drag of (dx, dy) screen pixels: the ground moves with the pointer. |
| 147 | 1 | `public double screenX(double x)` | A plot's place on the screen. |
| 148 | 1 | `public double screenY(double y)` |  |
| 151 | 1 | `public double plotX(double sx)` | The plot coordinate under a screen point. |
| 152 | 1 | `public double plotY(double sy)` |  |
| 155 | 3 | `public static int levelOf(double scale)` | The level a scale draws at, before the count of tiles is asked: L0, L1 or L2. |
| 160 | 5 | `public int level()` | The level this view draws at: L0, L1, L2, or FAR when an L2 view holds more than NEAR_TILES_MOST tiles. |
| 167 | 8 | `public static int tilePx(int level, double scale)` | Pixels a plot a tile is rastered at for a level and scale: SMALL_TILE_PX or BIG_TILE_PX at L0, MIDDLE_TILE_PX at L1, FAR_TILE_PX at L2; 0 at FAR. |
| 177 | 3 | `public static int blockOf(int level)` | The block a level's tiles are drawn in (TileRaster.blocks()): 0 at L0 (buildings one by one), BLOCK_MIDDLE at L1, BLOCK_FAR at L2. |
| 182 | 1 | `public long tileX0()` | The first and last tile columns and rows in view (inclusive). |
| 183 | 1 | `public long tileX1()` |  |
| 184 | 1 | `public long tileY0()` |  |
| 185 | 1 | `public long tileY1()` |  |
| 188 | 1 | `public long tilesInView()` | How many tiles the view holds, whole and in part. |
| 191 | 4 | `public long tilesAt(double s)` | The most tiles a view this size holds at a scale, wherever it looks: what a level's cache must hold for one screen. |
| 197 | 5 | `public long nodePlots()` | Plots a far node's pixel stands for: the least power of two at least a screen pixel wide, so a node is shown at NODE_PX to twice that. |
| 204 | 1 | `public long nodeSpan()` | A far node's side in plots. |
| 207 | 1 | `public long nodeX0()` | The first and last far nodes in view (inclusive), in node spans from the world's corner, clipped to the world. |
| 208 | 1 | `public long nodeX1()` |  |
| 209 | 1 | `public long nodeY0()` |  |
| 210 | 1 | `public long nodeY1()` |  |
| 213 | 3 | `public long nodesAtMost()` | The most far nodes a view this size holds: what the node cache must hold. |
| 218 | 8 | `public double[] scaleBar(double mostPx)` | The scale bar: the longest of 1, 2 and 5 times a power of ten metres no wider than mostPx, as {metres, pixels}. |
| 228 | 3 | `public static String scaleWords(double metres)` | A scale bar's words: "500 m", "2 km", "1,000 km". |

