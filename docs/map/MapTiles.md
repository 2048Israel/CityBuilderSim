# MapTiles.java - 1,358 lines · 73 methods · 14 constants · model

`ham/citybuildersim/MapTiles.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

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

**Uses:** [World](World.md) (115), [MapFrame](MapFrame.md) (53), [TilePainter](TilePainter.md) (42), [CityMap](CityMap.md) (36), [TileRaster](TileRaster.md) (28), [LandGrid](LandGrid.md) (21), [DistrictPicture](DistrictPicture.md) (18), [CityRuns](CityRuns.md) (15), [BuildingVisual](BuildingVisual.md) (12), [EarthProj](EarthProj.md) (7), [Kept](Kept.md) (6), [CityLand](CityLand.md) (6), [CityShore](CityShore.md) (5), [GlobeFrame](GlobeFrame.md) (3), [EarthTerrain](EarthTerrain.md) (1), [Resource](Resource.md) (1)

**Used by (11):** [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [GlobeCheck](GlobeCheck.md), [JunctionCheck](JunctionCheck.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [MiniMapFrame](MiniMapFrame.md), [RailDrawCheck](RailDrawCheck.md), [ReliefRenders](ReliefRenders.md)

## Sections

| line | section |
|---:|---|
| 316 | THE BUDGET (spec-land 2.6: every cache within 48 MB) |
| 371 | THE FAR NODES |
| 441 | · the far nodes from the district pictures (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) |
| 726 | · a far node kept: redrawn only where it moved (batch UP3) |
| 1062 | · the runs key of a far node (batch UP5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 34 | `MapTiles.TERRAIN_KEPT` | `2048` | Tiles' ground kept: 2,048 (spec-land 2.6), a kilobyte each - a far screen of NEAR_TILES_MOST and its margin. |
| 37 | `MapTiles.PAINTED_KEPT` | `1` | Painted tiles kept, with their inputs: 1, the hover's (star RD2-5; 64 until 0.7.87) - about 31 KB each on MapCheck 7's densest screen, x 10,000 (the painter's arrays grow to 512). |
| 40 | `MapTiles.BUDGET_MB` | `48` | What every cache of the view together may hold, in MB: 48 (spec-land 2.6). |
| 43 | `MapTiles.FAR_HIGHWAY_LIGHTEN` | `0.4` | How much the far view's highway line is lightened toward white against the dark ground (spec-freight 4.8): 40%. |
| 46 | `MapTiles.DESIGN_W` | `1345, DESIGN_H = 806` | The view the budget is sized for, in pixels: the land office's expanded map in a 1,389 x 868 window - 1,345 across inside the pane's padding, 806 down under its head (UserInterface's chart pane, 0.7.23). |
| 444 | `MapTiles.PICTURE_FROM_PX` | `4` | A district is drawn from its picture (DistrictPicture) on a far node where it is at least this many of the node's pixels across: 4 (the study's 8.2) - a node's pixel is shown 1 to 2 screen pixels wide (MapFrame.nodePl... |
| 447 | `MapTiles.EDGE` | `0xffffffff` | The city's edge over a far node's pixels: white at 0.55, the view's edge's (MapView's EDGE_ALPHA, since 0.7.61), on the inner ring of the pixels drawn as the city's and on any holding ground of it smaller than a pixel... |
| 450 | `MapTiles.EDGE_ALPHA` | `0.55` | ...its white's alpha: 0.55, the view's own edge's since 0.7.61 - ui/MapView's EDGE_ALPHA reads this one since 0.7.128, so the two are one dial. |
| 459 | `MapTiles.SITES_AT_MOST` | `1` | The expanded map's far node tints the world's deposits' sites as the plane's tiles do (TileRaster.siteTint(): the resource's colour, speckled, grey where worked) on a node of this many plots a pixel or fewer: 1 (MS2, ... |
| 462 | `MapTiles.SITE_PAD` | `16` | How far past a node's box a site's district is looked for, in plots: 16 - a site is under 8 plots wide and is listed under the district of its centre. |
| 729 | `MapTiles.NO_DISTRICT` | `- 1, VOID_PIXEL = - 2` | KeptNode's district index at a pixel with no district (no city ground there, or ground no district holds), and at one past the planet's rim on Earth, which nodePixels() draws the void. |
| 732 | `MapTiles.NO_PICTURE` | `0x4E4F504943L, NO_LAND = 0x4C414E44L, NO_RUNS = 0x4E4F52554E53L` | nodeKey()'s fixed marks: a district whose picture is not current, the land's key where the city has no land, the runs' where it has none. |
| 1065 | `MapTiles.TAG_HIGHWAY` | `0x484957L, TAG_RAIL = 0x5241494CL, TAG_DOT = 0x444F54L` | The tags runsKey() folds a highway's stretch, the railway's and a 2 x 2 dot (a yard's, a quay's) in under, so a highway and a railway over the same plots do not hash alike. |
| 1068 | `MapTiles.RUNS_INDEXES_KEPT` | `4` | How many scales' runs indexes (RunsIndex) a MapTiles keeps: 4 - the far view's, the minimap's stamp's and preview's, and one spare as the wheel turns; past it they are all made again. |

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
| 465 | `private final java.util.Set<CityMap.District> wanted` | The districts the last nodePixels() drew at PICTURE_FROM_PX or more with no current picture: what the view asks the pictures of (CityMap.pictureJobs()). |
| 468 | `private boolean[] lastEdge` | Which of the last nodePixels()'s pixels it drew as the city's edge, and as the far skeleton's - a highway, the railway, a yard or a sea terminal's dot: a harness's. |
| 754 | `private CityMap map` |  |
| 755 | `private CityLand land` |  |
| 756 | `private LandGrid grid` |  |
| 757 | `private long x0, y0, plots, landKey, runsKey, runsStamp` |  |
| 758 | `private byte[] ground, relief` |  |
| 759 | `private boolean runsKept` |  |
| 761 | `private byte[] cover` | The land layer: the city's cover of each pixel and a ring about the node, whether it owns the pixel's sample, whether the pixel is its edge, and each pixel's district (an index into districts, or NO_DISTRICT, or VOID_... |
| 762 | `private boolean[] city, edge` |  |
| 763 | `private int[] district` |  |
| 764 | `private CityMap.District[] districts` |  |
| 766 | `private int[] base` | The district layer: each pixel's colour before the runs and the edge, and each district's key as it was drawn. |
| 767 | `private long[] dVersion, dUsed` |  |
| 768 | `private int[] dOwned` |  |
| 769 | `private DistrictPicture[] dPic` |  |
| 771 | `private int[] runColour` | The runs layer: the colour of each pixel the runs cross, and which they cross. |
| 772 | `private boolean[] runMask` |  |
| 773 | `private long lands, runsDrawn, repainted, calls` |  |
| 1077 | `final long stamp` |  |
| 1078 | `final Map<Long, Long> sums` |  |
| 1091 | `private final Map<Long, RunsIndex> runsIndexes` | The scales' runs indexes, by plots a pixel. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 31 | 1328 | **type** `public final class MapTiles` | The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again only when what it is painted from has changed, a few painted tiles kep... |
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
| 118 | 3 | `public long tileKey(long tx, long ty)` | A tile's key (batch UP5; CityMap.tileKey()): equal keys, equal inputs and so equal stamps, worked out from what the inputs are read from in a few lookups - a view that painted a tile at a key does not read its inputs ... |
| 135 | 51 | `public static long stamp(TilePainter.Input in)` | A stamp of everything a tile is painted from but its ground, which is the world's and never changes: what is owned, its plan's streets and those just beyond its edges (and, since FX5, on the ring of REACH plots about ... |
| 188 | 9 | `private static long packed(byte[] codes)` | Bytes a plot folded into one number, eight a word. |
| 199 | 9 | `private static long packed(boolean[] flags)` | A plot-by-plot flag folded into one number, sixty-four plots a word. |
| 214 | 12 | `public TilePainter.Painted painted(long tx, long ty, TilePainter.Input in, long stamp)` | A tile painted from `in` (whose stamp is `stamp`): the kept picture when it was painted at that stamp, else painted now and kept. |
| 228 | 4 | `public TilePainter.Input paintedInput(long tx, long ty)` | The inputs a kept picture was painted from, or null when the tile is not kept. |
| 234 | 57 | `static void copy(TilePainter.Input a, TilePainter.Input b)` | A copy of a tile's inputs, the arrays reused. |
| 299 | 7 | `public long pixels(long tx, long ty, int level, int px, TilePainter.Input in, int[] out)` | The per-tile path a frame takes: the tile's inputs (its ground kept), painted - or the kept picture when its stamp has not moved - and rastered at a level's width into `out` (TileRaster.raster() at L0, its blocks at L... |
| 308 | 4 | `public static void raster(TilePainter.Input in, TilePainter.Painted p, int level, int px, int[] out)` | A painted tile rastered as a level draws it. |
| 314 | 1 | `public static int tileImagePixels(int px)` | Pixels in a tile's image at a width. |

### THE BUDGET (spec-land 2.6: every cache within 48 MB) (lines 316-370)

| line | len | member | says |
|---:|---:|---|---|
| 328 | 7 | `public static long tilesKept(MapFrame f, int level, int px)` | The tiles a level's image cache keeps for a view this size (star): one screen at the level's least scale with a tile of margin round it (MapFrame.tilesAt()) - at L0 from L0_FROM at SMALL_TILE_PX and from BIG_TILES_FRO... |
| 337 | 7 | `public static long paintedBytes(TilePainter.Input in, TilePainter.Painted p)` | A kept painted tile's bytes, from its arrays' lengths: what PAINTED_KEPT of them weigh - since 0.7.88 its plan's streets and runs and its buildings' boxes in, its plots' use, kind, width, role, flags, building and sit... |
| 354 | 3 | `public static double budgetBytes(MapFrame f, double paintedBytes, double planBytes)` | Every cache of the view together, in bytes, for a view this size: each level's images at four bytes a pixel (L0 at both widths, L1, L2), the far nodes (MapFrame.nodesAtMost()), the tiles' ground and PAINTED_KEPT paint... |
| 359 | 11 | `public static double budgetBytes(MapFrame f, double paintedBytes)` | ...without the plans: what the view's own caches hold. |

### THE FAR NODES (lines 371-440)

| line | len | member | says |
|---:|---:|---|---|
| 376 | 3 | `public static byte[] nodeTerrain(long seed, long x0, long y0, long plots)` | A far node's ground: MapFrame.NODE_PX squared samples a `plots` apart from (x0, y0), a World class each. |
| 390 | 3 | `public static byte[] nodeTerrain(World.Key world, long x0, long y0, long plots)` | ...on the world a key names (0.7.115; the map's: CityMap.worldKey()). |
| 400 | 11 | `public static byte[] nodeTerrain(World.Key world, long x0, long y0, long plots, byte[] relief)` | ...and, where `relief` is not null (MapFrame.NODE_PX squared bytes), the node's relief beside it (ET1 slice D; spec-earth2-terrain 9.3): each pixel's World relief (FLAT, FOOTHILL, STEEP, VERY_STEEP) as the far rule dr... |
| 419 | 9 | `public static byte[] chartTerrain(World.Key world, GlobeFrame.Chart chart, long plots, long nx, long ny, byte[] relief)` | A chart's node (MS3): MapFrame.NODE_PX squared pixels of the chart about an anchor (GlobeFrame.chart()), node (nx, ny) at `plots` plots a pixel, a World class each - the far rule through EarthTerrain.chart(), the plan... |
| 434 | 6 | `public static void chartPixels(byte[] ground, byte[] relief, int[] out)` | A chart node's colours (MS3): each pixel as nodePixels() colours ground the city does not own, with no site, district, run or edge - TileRaster.colour() of its class and relief (the class's colour where `relief` is nu... |

### the far nodes from the district pictures (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) (lines 441-725)

| line | len | member | says |
|---:|---:|---|---|
| 470 | 1 | `public java.util.Set<CityMap.District> wanted()` |  |
| 471 | 1 | `public boolean[] lastEdge()` |  |
| 472 | 1 | `public boolean[] lastRun()` |  |
| 496 | 3 | `public int nodePixels(long x0, long y0, long plots, byte[] ground, int[] out)` | A far node's pixels from its ground (0.7.128, batch MM1; spec-globe 8.2): each sample's terrain colour, dimmed where the city does not own it (TileRaster's UNOWNED_DIM); over the city's districts what stands there - w... |
| 505 | 3 | `public int nodePixels(long x0, long y0, long plots, byte[] ground, byte[] relief, int[] out)` | ...with the node's relief (nodeTerrain()'s, ET1 slice D): each pixel's ground in TileRaster.colour()'s - the three greys on open dry ground, forest shaded toward them - where `relief` is not null; null, and a generate... |
| 514 | 121 | `public int nodePixels(long x0, long y0, long plots, byte[] ground, byte[] relief, boolean sites, int[] out)` | ...and, where `sites` is true, the world's deposits' sites tinted from SITES_AT_MOST plots a pixel down as the plane's tiles tint them (MS2): the expanded map's own colouring (ui/MapView). |
| 644 | 33 | `private void stroke(CityRuns.Net net, int colour, long x0, long y0, long plots, int[] out)` | A net's stretches over a far node's pixels in a colour: every pixel holding a plot of a stretch - each step's, from the plot it starts from (its arm's start, or the last of the stretch before) to its last, and a 45-de... |
| 679 | 8 | `private void mark(long x, long y, long x0, long y0, long plots, int colour, int[] out)` | Plot (x, y)'s pixel on the node, when it is on it, in a run's colour. |
| 689 | 13 | `private void dot(double x, double y, long x0, long y0, long plots, int colour, int[] out)` | A yard's or a sea terminal's 2 x 2 pixel dot at its box's middle, in TRANSPORT's fill (spec-freight 4.8). |
| 711 | 14 | `public boolean picturesFit(MapFrame f)` | Whether a far view asks for the pictures of the built districts it shows (0.7.128): while they are no more than CityMap.PICTURES_KEPT and lie in no more of the map's bands than it keeps the links of (CityMap.BANDS_KEP... |

### a far node kept: redrawn only where it moved (batch UP3) (lines 726-1061)

| line | len | member | says |
|---:|---:|---|---|
| 753 | 33 | **type** `public static final class KeptNode` | What a far node was last drawn from and its layers, for one node (batch UP3, docs/queue/UP3.md; the minimap's stamp is one - the expanded map's far view has too many nodes to keep layers for, and asks nodeKey() instea... |
| 776 | 1 | `public long lands()` _(in MapTiles.KeptNode)_ | Times its land layer was drawn. |
| 778 | 1 | `public long runsDrawn()` _(in MapTiles.KeptNode)_ | Times its runs layer was drawn. |
| 780 | 1 | `public long districtsRepainted()` _(in MapTiles.KeptNode)_ | Districts whose pixels were repainted, summed over the calls. |
| 782 | 1 | `public long calls()` _(in MapTiles.KeptNode)_ | Times nodePixels() was called with it. |
| 784 | 1 | `public int districts()` _(in MapTiles.KeptNode)_ | How many districts it lists now: those it met at a pixel of the city's ground. |
| 798 | 3 | `public int nodePixels(KeptNode keep, long x0, long y0, long plots, byte[] ground, int[] out)` | A far node's pixels as nodePixels(x0, y0, plots, ground, out) gives them - `out`, wanted(), lastEdge(), lastRun() and the return all equal - redoing only what moved since `keep` was last drawn (batch UP3): the land la... |
| 803 | 74 | `public int nodePixels(KeptNode keep, long x0, long y0, long plots, byte[] ground, byte[] relief, int[] out)` | ...with the node's relief (nodeTerrain()'s; null as before), equal to nodePixels(x0, y0, plots, ground, relief, out) as the plain one is. |
| 887 | 85 | `private void drawLand(KeptNode keep, CityLand land, LandGrid grid)` | The land layer of a kept node: nodePixels()'s first loop (the city's cover a pixel at a time and a ring round the node, and whether it owns each pixel's sample) and last (the edge), and which district each pixel lies ... |
| 981 | 40 | `private void repaint(KeptNode keep, boolean[] dirty, boolean all)` | The district layer of a kept node: each pixel's colour before the runs and the edge - the terrain's, dimmed where the city does not own it, a built district's picture's pixel or its shares' dither over it - for the pi... |
| 1023 | 19 | `private void drawRuns(KeptNode keep)` | The runs layer of a kept node: nodePixels()'s runs block, drawn into the node's own colours and its mask the pixels they cross (lastRun after it). |
| 1053 | 8 | `private long landKey(long x0, long y0, long plots)` | The land key of the node at (x0, y0) of `plots` a pixel: how many plots the city owns in its box and the ring of a pixel round it - [x0 - plots, x0 + (NODE_PX + 1) plots) each way - folded with the map's district coun... |

### the runs key of a far node (batch UP5) (lines 1062-1358)

| line | len | member | says |
|---:|---:|---|---|
| 1076 | 13 | **type** `private static final class RunsIndex` | What the runs draw on each far node of one scale, as sums of hashes (batch UP5): for each aligned node (x0 and y0 multiples of its span) the sum of runsKey()'s hash of every stretch and dot that lands on it, made in o... |
| 1080 | 1 | `RunsIndex(long stamp)` _(in MapTiles.RunsIndex)_ |  |
| 1082 | 1 | `void add(long nx, long ny, long hash)` _(in MapTiles.RunsIndex)_ |  |
| 1084 | 4 | `long at(long nx, long ny)` _(in MapTiles.RunsIndex)_ |  |
| 1094 | 1 | `static long nodeId(long nx, long ny)` | A node's place in a RunsIndex: its column and row of nodes. |
| 1101 | 9 | `private long runsStamp()` | A stamp of everything runsKey() hashes, for the whole city (batch UP5): the runs' version (CityRuns' stamp of everything laid), whether the highways reach a street, and the shore's works (its version and how many) - a... |
| 1119 | 9 | `long runsKey(long x0, long y0, long plots)` | The runs key of the node at (x0, y0) of `plots` plots a pixel (batch UP5; the city's before it): a hash of exactly what nodePixels()'s runs block draws on it - each net's stretches as stroke() clips them (the highways... |
| 1130 | 15 | `private RunsIndex runsIndex(CityRuns runs, long plots)` | The scale's RunsIndex, made again when the runs' stamp has moved. |
| 1147 | 10 | `private long runsSum(CityRuns runs, long x0, long y0, long plots)` | The runs key's sum for one node by walking every stretch and dot (a node not aligned to its span): what a RunsIndex holds for an aligned one. |
| 1164 | 16 | `private static long stretchHash(long tag, long[] s, long x0, long y0, long plots)` | What stroke() draws of one stretch {x, y, heading, steps} on the node at (x0, y0), as a hash, 0 for nothing: the stretch's start, heading and the steps stroke() would walk - its clip of [x0 - 1, x0 + NODE_PX plots] ea... |
| 1182 | 6 | `private static long dotHash(double x, double y, long x0, long y0, long plots)` | What dot() draws of the 2 x 2 dot at (x, y) on the node at (x0, y0), as a hash, 0 for nothing: the pixel it starts at, when any of its four lands on the node. |
| 1190 | 3 | `private static void enterNet(RunsIndex ix, long tag, CityRuns.Net net, long plots)` | Enters each stretch of a net in the index at every node it may be drawn on. |
| 1199 | 18 | `private static void enterStretch(RunsIndex ix, long tag, long[] s, long plots)` | Enters one stretch at the nodes it is drawn on: walked a column of nodes at a time - the steps whose plots fall in the column (a plot round it), the rows those cover (two plots round, for a corner) - so a long stretch... |
| 1219 | 9 | `private static void enterDot(RunsIndex ix, double x, double y, long plots)` | Enters a 2 x 2 dot at (x, y) at the nodes it lands on, asking dotHash() of each. |
| 1230 | 5 | `static Map<Long, Long> indexOfStretch(long tag, long[] s, long plots)` | A harness's (MapCheck 18): the node sums enterStretch() gives one stretch {x, y, heading, steps, home} at a scale, by node id (nodeId()); the nodes it does not enter are not there. |
| 1237 | 3 | `static long stretchKey(long tag, long[] s, long x0, long y0, long plots)` | A harness's: what stretchHash() gives one stretch on the node at (x0, y0), the authority the index is held to. |
| 1242 | 4 | `long runsKeyWalked(long x0, long y0, long plots)` | A harness's: runsKey() worked by walking every stretch and dot, as it is for a node not aligned to its span, whatever the node. |
| 1266 | 3 | `public long nodeKey(long x0, long y0, long plots)` | A hash of everything nodePixels() draws the node at (x0, y0) from but its ground, which is the world's and fixed for the node (batch UP3; the expanded map's far view keeps this and an image a node, redrawing a node wh... |
| 1271 | 31 | `public long nodeKey(long x0, long y0, long plots, boolean sites)` | ...and, where `sites` is true, with the sites nodePixels(..., sites, ...) tints folded in (MS2): each one's kind, state and mine. |
| 1304 | 12 | `private java.util.List<CityMap.DrawnSite> sitesIn(long x0, long y0, long span)` | The deposits' sites overlapping the box [x0, x0 + span) each way, every resource's, owned or not (CityMap.siteList(), as the plane's tiles list them). |
| 1318 | 10 | `private static int[] siteMarks(java.util.List<CityMap.DrawnSite> sites, long x0, long y0, long plots, int n)` | Each pixel of a node, the index plus one of the site whose square holds the plot at its middle, else 0. |
| 1330 | 5 | `private TilePainter.Input siteInput()` | The scratch tile input a site's tint is asked of: the plane's own function (TileRaster.siteTint()) reads a site's kind and state, the plot's place in its tile and the world's seed from one. |
| 1337 | 8 | `private static int siteTinted(TilePainter.Input in, CityMap.DrawnSite site, byte ground, long x, long y, int c)` | A plot's ground colour under a site, as the plane tints it (TileRaster.groundColour()): the resource's colour, speckled, grey where worked - on water only an oil site's. |
| 1347 | 11 | `private long districtKey(CityMap.District d, boolean pictures)` | One district's part of nodeKey(): its place, version, built and owned plots, and (`pictures`) its current picture's stamp or NO_PICTURE. |

