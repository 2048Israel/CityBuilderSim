# EarthTerrain.java - 1,705 lines · 75 methods · 44 constants · model

`ham/citybuildersim/EarthTerrain.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The real Earth's ground on the world's plots: a tile, a plot, a line or a region at a time, each world cell's land and forest (the cells pass), the founding site snapped to dry ground, and a point's sea - spec-earth 3's detail below the data, drawn from earth-1 through EarthData and EarthProj.
> 
> WHY THIS EXISTS (0.7.115, batch E2; the project's spec-earth.md 2, 3 and
> 8; Jerus's answers of 2026-10-10, "all rec"). E1 built the reader of the
> real Earth's data and the projection that lays it on the world's square;
> nothing drew ground from them. The data is coarse: a 15" cell (463 m) of
> sea and lake shares, a 2' cell (3.7 km) of land cover, so at a new city's
> zoom it holds two or three samples, and a world drawn straight from it would
> be squares. This draws the ground the world's way - a plot at a time, 30 m -
> keeping the data's shares and making only the detail below them from the
> seed, so the coasts, lakes and rivers are the real ones and the seed decides
> where within a cell the sea's edge, a patch of forest or a beach falls. The
> study's prototype of it (EarthWorld) was lost with the workspace on
> 2026-10-09; this is built from the spec's words.
> 
> THE RULE (spec-earth 3), a tile at a time (tileTerrain()):
>   - the tile and a ring of one plot (34 x 34, the ring for the beaches)
>     read their shares at a 10 x 10 lattice (LATTICE points LATTICE_STEP
>     plots apart, on the world's own grid of them, so two tiles read a
>     shared point alike), each point's latitude and longitude from the
>     tile's four corners (EarthProj.blend()), each share interpolated
>     between the data's cell centres, and blended between the points;
>   - each class has its own value noise, octaves of 2^NOISE_HI plots (480 m)
>     down to 2^NOISE_LO (60 m), each World.OCTAVE_FALLOFF of the one above;
>     fresh water's, forest's and a small lake's are ranked through one table
>     of the noise's distribution (QUANTILE_SAMPLES fixed samples), so each
>     covers in expectation what the data gives it;
>   - SEA where the sea share plus COAST_FRAY x the coast noise about its
>     mean, unranked, reaches SEA_LEVEL: the data's half-contour, frayed by a
>     plot or two;
>   - FRESH where a river's centreline (GRWL) passes within half its width,
>     at least RIVER_MIN_HALF; where a small lake (under 10 km2, a point and
>     an area in the data) lies, drawn as a blob of its own area in
>     expectation, its edge frayed by BLOB_FRAY of its own noise; or where the
>     fresh noise ranks under the lake share of the ground that is not sea;
>   - SAND where a dry plot has the sea on one of its eight sides and its 2'
>     cell's bare ground is 2% of its land or more (EarthData.LC_BEACH);
>   - FOREST where the forest noise ranks under the forest share of the dry
>     ground; GRASS the rest. Real land use is ignored: the city starts on
>     unsettled ground.
> A plot (terrainAt()) is its tile's, from a kept tile, and a line
> (lineTerrain()) its tile's byte for byte, drawn alone where the tile is not
> kept (draw()); a region at a stride (regionTerrain()) classes each pixel's
> centre from the shares there and the octaves wider than a pixel, the
> narrower at their mean - World's rule for its far view.
> 
> THE FAR VIEW (0.7.119, batch E5; spec-earth 3, "Far views"). The map's far
> nodes (MapTiles.nodeTerrain()) are farTerrain(): the region's pixels, but
> the rivers stroked from their vectors at least a pixel wide and thinned
> by width as the view widens (FAR_RIVER_SHARE), the small lakes drawn from
> their points and areas, and from DITHER_STRIDE the forest dithered by a
> hash - its vectors read from the 1-degree tiles under the node, either
> side of the date line alike (nodeTiles()). MapCheck 10 holds it.
> 
> THE WORLD'S OTHER HALF. The cells pass (cellsPass()) replaces the
> generated world's sea pass: each world cell's land and forest from
> CELL_SAMPLES x CELL_SAMPLES points of the 4' layers, which the fields of
> ore and oil are drawn on as the generated world's are on its cells - since
> 0.7.117 (batch E3) each point's land weighted by its province's geology
> (Geology, cellFieldLand()), oil's with a basin's, shelf's or rift's sea. The
> ... (16 more lines in the source)

**Uses:** [World](World.md) (104), [EarthData](EarthData.md) (68), [EarthProj](EarthProj.md) (17), [Resource](Resource.md) (5), [Geology](Geology.md) (3)

**Used by (9):** [EarthFoundingCheck](EarthFoundingCheck.md), [EarthSite](EarthSite.md), [EarthTerrainCheck](EarthTerrainCheck.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [World](World.md)

## Sections

| line | section |
|---:|---|
| 94 | THE DIALS |
| 198 | THE DATA A KEY NAMES |
| 235 | ONE EARTH WORLD'S STATE |
| 278 | THE RANKING TABLE (spec-earth 3's QUANTILE) |
| 334 | READING THE DATA: a layer's cells, a few tiles kept in hand |
| 448 | A TILE, OR A LINE OF ONE |
| 627 | A PLOT, A LINE, A REGION AND A POINT'S SEA |
| 766 | RIVERS AND SMALL LAKES, PROJECTED: a 1-degree tile's, and a district's |
| 911 | THE FAR VIEW'S NODES (0.7.119, batch E5; spec-earth 3, "Far views") |
| 1266 | THE CELLS PASS AND THE SNAP (the world's build) |
| 1281 | THE SEA'S DEPTH (0.7.121, batch E4; spec-earth 6's E4, E2's star 7) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `EarthTerrain.NOISE_HI` | `4` | The noise's widest octave, 2^4 plots (480 m): just over the data's 15" cell (463 m), so the detail drawn lives below what the data resolves (spec-earth 3). |
| 102 | `EarthTerrain.NOISE_LO` | `1` | ...down to 2^1 plots (60 m), two plots, the finest a plot-centred noise can carry (spec-earth 3). |
| 105 | `EarthTerrain.SEA_LEVEL` | `0.5` | The sea's share from which the coast is drawn, 0.5: the data's half-contour (spec-earth 3) - and what an Earth world gives as its sea's level (World.seaTheta()), having no heights. |
| 117 | `EarthTerrain.COAST_FRAY` | `1.0` | How far the coast noise frays the coast: 1.0 (spec-earth 3's COAST_FRAY) - sea where share + 1.0 x (noise - 0.5) reaches SEA_LEVEL, the noise unranked, so the coast is the interpolated share's half-contour moved by th... |
| 126 | `EarthTerrain.BLOB_FRAY` | `1.0` | How far a small lake's edge is frayed: 1.0 (★E2, the coast's own rule on a lake's disc) - a plot at distance d from a lake of radius r is in it where (d/r)^2 + 1.0 x (rank - 0.5) is under 1, so the blob reaches from 0... |
| 129 | `EarthTerrain.QUANTILE_SAMPLES` | `1<<18` | The noise's samples the ranking table holds: 2^18 (spec-earth 3's QUANTILE), at fixed places, one table for every class and every world. |
| 132 | `EarthTerrain.LATTICE_STEP` | `4` | Plots between the points a tile reads its shares at: 4 (120 m), so a tile's 34 x 34 region holds 10 x 10 of them on the world's own grid of them (spec-earth 3: a 10 x 10 lattice). |
| 135 | `EarthTerrain.LATTICE` | `World.TILE / LATTICE_STEP + 2` | Points a side of a tile's lattice: TILE / LATTICE_STEP + 2, 10 - the region's ring and its far edge. |
| 138 | `EarthTerrain.RIVER_MIN_HALF` | `0.75` | A river's least half-width, in plots: 0.75, so a 30 m river - half a plot each side of its line - leaves no gaps (spec-earth 3). |
| 141 | `EarthTerrain.SNAP_PLOTS` | `167` | How far the founding snaps from the spot, in plots: 167 (5 km, the founding card's reach, spec-earth 2); a spot with no site within it has none, and E4's screen refuses it. |
| 144 | `EarthTerrain.CELL_SAMPLES` | `4` | Points a side of a world cell the cells pass reads: 4, so 16 a cell of the 4' layers (spec-earth 4). |
| 147 | `EarthTerrain.FAR_STRIDE` | `256` | The stride, in plots, from which a region reads the 4' far layers instead of the 15" shares: 256 (7.68 km, a district), about a 4' cell's 7.4 km (★E2: wider pixels would inflate a 15" tile for every few). |
| 150 | `EarthTerrain.WATER_STRIDE` | `16` | The stride under which a region draws rivers and small lakes: 16 plots (480 m), the noise's widest octave (★E2): past it a pixel is wider than any detail of the ground's own, and the far view's rivers are stroked from... |
| 159 | `EarthTerrain.WATER_PAD_DEG` | `0.25` | How far round a district its rivers are looked for, in degrees of latitude: 0.25 (27.8 km; ★E2) - a river is stored in every 1-degree tile its centreline's box touches, so a district reads the tiles within this of it ... |
| 162 | `EarthTerrain.WIDEST_RIVER_M` | `40_650` | The widest river in earth-1, in metres: 40,650, measured over all its tiles (EarthTerrainCheck 9 holds it, and WATER_PAD_DEG to it, when the file is named). |
| 165 | `EarthTerrain.TILES_KEPT` | `1024` | Tiles of ground kept for terrainAt(), lineTerrain() and the snap: 1,024 (1 MB), the snap's 5 km disc and a screen's worth. |
| 168 | `EarthTerrain.DISTRICTS_KEPT` | `256` | Districts' rivers and small lakes kept, projected: 256. |
| 171 | `EarthTerrain.DATA_TILES_KEPT` | `64` | 1-degree tiles' rivers and small lakes kept, projected: 64. |
| 174 | `EarthTerrain.F_COAST` | `0xC0A57L, F_FRESH = 0xF4E54L, F_FOREST = 0xF0E57L, F_BLOB = 0xB10BL` | The fields' keys: what makes one class's noise another's. |
| 177 | `EarthTerrain.QUANTILE_SALT` | `0x0A1E5A3DL` | The stream the ranking table's samples are drawn from. |
| 180 | `EarthTerrain.AMP_N` | `new double [ NOISE_HI + 1 ]` | Each octave's amplitude over the sum of them, octave k at AMP_N[k]. |
| 183 | `EarthTerrain.SMOOTH` | `new double [ NOISE_HI + 1 ][]` | The smoothstep at a plot's centre within octave k's lattice cell, SMOOTH[k][f] for f = 0 .. |
| 203 | `EarthTerrain.SOURCES` | `new HashMap<>()` | The readers use() has made available, by their identity: where source() looks for a key's data first. |
| 246 | `EarthTerrain.COAST` | `0, FRESH = 1, FOREST = 2, BLOB = 3` | Each noise field's row in os: the coast's, fresh water's, the forest's and the small lakes' blobs'. |
| 418 | `EarthTerrain.Scratch.RING` | `World.TILE + 2, RING_AREA = RING * RING, AREA = World.TILE * World.TILE` | A tile with its ring of one plot each side, RING plots a side (RING_AREA in all); a tile alone, AREA plots. |
| 440 | `EarthTerrain.SCRATCH` | `ThreadLocal.withInitial(Scratch : : new)` | Each thread's own working arrays and readers, so tiles drawn on several threads at once share none. |
| 802 | `EarthTerrain.NO_WATER` | `new Water()` | A district's water where it lies off the world or off the planet: no river and no small lake. |
| 916 | `EarthTerrain.DITHER_STRIDE` | `1<<NOISE_HI` | A far node's forest is dithered by a hash from this stride, in plots: 2^NOISE_HI, 16 (480 m) - where no octave of the forest's noise is wider than a pixel, so the ranked noise stands at its mean and would draw forest ... |
| 919 | `EarthTerrain.FAR_RIVER_HALF_PX` | `RIVER_MIN_HALF` | A river's least half-width on a far node, in pixels: RIVER_MIN_HALF, 0.75 - a far pixel is to the node what a plot is to a tile, so a river is stroked at least 1.5 px wide, past the 0.71 px (half a pixel's diagonal) u... |
| 934 | `EarthTerrain.FAR_RIVER_SHARE` | `30.0 / 2500` | The least width, as a share of a far pixel, at which a river is stroked: 30 / 2,500, 0.012 (★E5) - the narrowest river in the data, GRWL's 30 m (spec-earth 1), at the study's far render of 2.5 km a pixel (spec-earth 3... |
| 937 | `EarthTerrain.FAR_BLEND_PX` | `8` | From WATER_STRIDE a far node's pixels take their latitude and longitude from blocks of this many a side, each blended from its four corners (EarthProj.blend(), the tile's way): 8 - measured, at worst 0.15 of a pixel o... |
| 949 | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | `64` | From this stride, in plots, a far node places a 1-degree tile's river vertices and small lakes bilinear between the tile's four corners, projected exactly, rather than projecting each: 64 (★E5) - measured at worst 0.0... |
| 952 | `EarthTerrain.NODE_EDGE_POINTS` | `32` | Points along each edge of a far node its box of 1-degree tiles is found from: 32 - at the widest node (1,024 plots a pixel, 3,932 km a side) one every 123 km, where a straight edge strays from its chord by a fraction ... |
| 955 | `EarthTerrain.LONGEST_SEGMENT_M` | `17_915` | The longest river segment in earth-1, in metres on the sphere: 17,915, measured over all its tiles (MapCheck 10 holds it when the file is named). |
| 967 | `EarthTerrain.RIM_M` | `(double) LONGEST_SEGMENT_M * LONGEST_SEGMENT_M /(4 * EarthProj.RADIUS_M)` | A river segment with an end this close to the planet's rim is not stroked, in metres: LONGEST_SEGMENT_M^2 / 4R, 12.6 (★E5). |
| 970 | `EarthTerrain.F_DITHER` | `0xD17E4L` | The far view's dither's stream. |
| 1025 | `EarthTerrain.FAR_COUNTS` | `ThreadLocal.withInitial(() -> new long [ 3 ])` | How many river segments and small lakes the last far node on this thread drew from, and how many 1-degree tiles it read: what a harness prints. |
| 1238 | `EarthTerrain.FOOTPRINT_POINTS` | `3` | Points a side of a 1-degree tile its footprint is read at: 3, its corners, the middles of its edges and its centre. |
| 1322 | `EarthTerrain.SHELF_WIDTH_M` | `54_167` | The distance within which SHELF_SHARE of the Earth's sea lies from the coast, metres: 54,167, measured on earth-1 (measureShelfWidthM(): 46,434 of SHELF_SAMPLES points the sea's, 54,167.3 m; EarthFoundingCheck holds i... |
| 1325 | `EarthTerrain.SHELF_SAMPLES` | `1<<16` | Points the shelf's width is measured over: 2^16, evenly on the sphere by a fixed hash (SHELF_SALT), of which the sea's (about 71%) are measured - the generated world's measure is a share of its sea's samples too (Worl... |
| 1328 | `EarthTerrain.SHELF_MEASURE_REACH_M` | `250_000` | How far a sample's coast is looked for while the width is measured, metres: 250 km, past which a sample is beyond the shelf by far - over four times its width (the measure is a low quantile; depthAt() itself looks as ... |
| 1331 | `EarthTerrain.SHELF_SALT` | `0x5E1F5A3DL` | The stream the shelf's sample points are drawn from. |
| 1334 | `EarthTerrain.COAST_BELOW` | `(int) Math.ceil(SEA_LEVEL * EarthData.SHARE_LEVELS)` | The sea layer's levels under which a 15" cell is the coast's land side: the share under SEA_LEVEL, 0 to 7 of 15. |
| 1337 | `EarthTerrain.DEPTHS_KEPT` | `4096` | Depths kept, by plot: 4,096 (a field's depth is asked again every time the city's ground changes). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 239 | `private final World.Key key` |  |
| 240 | `private final long seed` |  |
| 241 | `private final EarthData data` |  |
| 242 | `private final EarthProj proj` |  |
| 244 | `private final long[][] os` | Each field's octave streams: COAST, FRESH, FOREST, BLOB, octave k at [f][k]. |
| 248 | `private volatile boolean built` |  |
| 249 | `private double[] cellLand, cellForest` |  |
| 251 | `private float[][] cellFields` | Each resource's field land a cell, by Resource's ordinal (null for forest): cellFieldLand(). |
| 252 | `private long siteX, siteY` |  |
| 253 | `private int siteTests` |  |
| 254 | `private double cellsMs` |  |
| 282 | `private static volatile double[] quantile` |  |
| 340 | `EarthData data` |  |
| 341 | `EarthData.Layer layer` |  |
| 342 | `int cpd, cols` |  |
| 343 | `boolean tiles` |  |
| 344 | `final int[] ids` |  |
| 345 | `final byte[][] bytes` |  |
| 346 | `final int[] uniform` |  |
| 347 | `int next` |  |
| 419 | `final double[] coast` |  |
| 420 | `final double[] lat` |  |
| 421 | `final double[] seaL` |  |
| 422 | `final boolean[] sea` |  |
| 423 | `final double[] ll` |  |
| 424 | `int[] segs` |  |
| 425 | `final Reader seaR` |  |
| 426 | `EarthData bound` |  |
| 631 | `private final Map<Long, byte[]> tiles` |  |
| 772 | `double[] ax` |  |
| 773 | `double[] minX` |  |
| 774 | `double[] lx` |  |
| 775 | `int segs, lakes` |  |
| 804 | `private final Map<Integer, Water> dataTiles` |  |
| 810 | `private final Map<Long, Water> districts` |  |
| 973 | `private volatile int[] widestRun` | Each 1-degree tile's widest river run, by its median width in metres, once a far node has read it: -1 until then. |
| 1339 | `private final Map<Long, Double> depths` |  |
| 1642 | `private volatile long[] site` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 1614 | **type** `public final class EarthTerrain` | The real Earth's ground on the world's plots: a tile, a plot, a line or a region at a time, each world cell's land and forest (the cells pass), the founding site snapped to dry ground, and a point's sea - spec-earth 3... |

### THE DIALS (lines 94-197)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 12 | `static { ... }` |  |

### THE DATA A KEY NAMES (lines 198-234)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 5 | `public static String use(EarthData data)` | Makes a reader of Earth's data available to the worlds whose keys name it: returns its identity, the key's `data`. |
| 213 | 21 | `static EarthData source(String identity)` | The reader a key's data names: one use() made available, else the file EarthData.locate() finds when its identity is that. |

### ONE EARTH WORLD'S STATE (lines 235-277)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 11 | `EarthTerrain(World.Key key)` | The ground of an Earth world's key; the data it names is found here (source()). |
| 270 | 1 | `public World.Key key()` | Its key. |
| 273 | 1 | `public EarthProj projection()` | The projection it is laid through, centred on the key's spot. |
| 276 | 1 | `public EarthData data()` | The data it is drawn from. |

### THE RANKING TABLE (spec-earth 3's QUANTILE) (lines 278-333)

| line | len | member | says |
|---:|---:|---|---|
| 285 | 19 | `static double[] quantile()` | The noise's distribution: QUANTILE_SAMPLES values of it at plot centres hashed from QUANTILE_SALT, sorted - built once, the same everywhere. |
| 306 | 5 | `static double below(double p)` | The noise value under which share p of its samples lie: minus infinity at p of nought or less, plus infinity at one or more. |
| 313 | 5 | `private static double below(double[] q, double p)` | ...from a table in hand. |
| 320 | 3 | `static boolean sea(double share, double coastNoise)` | Whether a plot is sea (spec-earth 3): its sea share plus COAST_FRAY x its coast noise about the noise's mean, 0.5, reaches SEA_LEVEL - the share's half-contour, frayed. |
| 325 | 8 | `static double noiseAt(long[] os, double x, double y, double stride)` | One field's noise at a point in plots, its octaves no wider than `stride` at their mean (World.regionTerrain()'s rule): at a stride of one, every octave. |

### READING THE DATA: a layer's cells, a few tiles kept in hand (lines 334-447)

| line | len | member | says |
|---:|---:|---|---|
| 339 | 75 | **type** `private static final class Reader` | One share layer read cell by cell, the last few of its tiles (or bands) in hand: one thread's. |
| 349 | 9 | `void bind(EarthData d, String name)` _(in EarthTerrain.Reader)_ |  |
| 360 | 12 | `int value(long r, long c)` _(in EarthTerrain.Reader)_ | The value of the cell in global row r (from 90 N) and column c (from 180 W), rows clamped to the globe and columns wrapped round it. |
| 373 | 14 | `private int slot(int id)` _(in EarthTerrain.Reader)_ |  |
| 389 | 8 | `double bilinear(double lat, double lon)` _(in EarthTerrain.Reader)_ | The layer's value at (lat, lon) interpolated between the four cell centres about it, 0 to 255 (bilinear; columns wrap at the date line). |
| 399 | 9 | `double bilinearLow(double lat, double lon)` _(in EarthTerrain.Reader)_ | ...the forest level of land cover, 0 to 15, interpolated the same way. |
| 410 | 3 | `int at(double lat, double lon)` _(in EarthTerrain.Reader)_ | The value of the cell (lat, lon) lies in: a cell holds its north and west edges (EarthData's WHERE A POINT FALLS). |
| 416 | 22 | **type** `private static final class Scratch` | One thread's working arrays and readers. |
| 428 | 9 | `void bind(EarthData d)` _(in EarthTerrain.Scratch)_ |  |
| 442 | 5 | `private Scratch scratch()` |  |

### A TILE, OR A LINE OF ONE (lines 448-626)

| line | len | member | says |
|---:|---:|---|---|
| 457 | 3 | `public void tileTerrain(long tx, long ty, byte[] out)` | One tile's ground, a byte a plot in rows (out[py x 32 + px]): spec-earth 3's rule (the class header). |
| 468 | 102 | `private void draw(long tx, long ty, int ox, int oy, int w, int h, byte[] out)` | Plots (ox, oy) to (ox + w - 1, oy + h - 1) of tile (tx, ty), row by row (out[(py - oy) x w + px - ox]): each the tile's, byte for byte - the same lattice points from the same corners, the same shares, octaves and sums... |
| 572 | 8 | `private static double lattice(double[] at, int px, int py)` | A share at the tile's plot (px, py) - -1 to 32, the ring included - blended between the lattice's points about it. |
| 582 | 4 | `private static boolean beach(Scratch s, EarthProj.Blend b, double x, double y)` | Whether the 2' cell under the point (plot coordinates) has bare ground on 2% of its land or more (EarthData.LC_BEACH). |
| 588 | 20 | `private static void octave(double[] lat, long os, int k, long gx0, long gy0, int w, int h, double amp, double[] acc)` | Adds `amp` x octave k's value noise at the plot centres of a w x h rectangle from plot (gx0, gy0), row by row: its lattice hashed once, the smoothstep from SMOOTH (World.octaveTile()'s way) - a plot's value the same w... |
| 610 | 7 | `private static boolean onRiver(Water w, int k, double x, double y)` | Whether a point lies within a river segment's half-width, interpolated along it. |
| 619 | 7 | `private static boolean inLake(Water w, int k, double x, double y, double n, double[] q)` | Whether a point is in small lake k's blob, its blob noise there `n` (a stride's noise, at a region's pixel). |

### A PLOT, A LINE, A REGION AND A POINT'S SEA (lines 627-765)

| line | len | member | says |
|---:|---:|---|---|
| 638 | 11 | `byte[] keptTile(long tx, long ty)` | A tile's ground, kept (TILES_KEPT): shared, do not write into it. |
| 651 | 5 | `public byte terrainAt(long x, long y)` | One plot's ground: its tile's (tileTerrain()), from a kept tile; sea off the world. |
| 658 | 14 | `public void lineTerrain(long tx, long ty, boolean column, int at, byte[] out)` | One row (or, `column`, one column) of a tile's ground, its `at`-th: the tile's, byte for byte (World.lineTerrain()'s contract) - from the tile if it is kept, else the line drawn alone (draw()). |
| 681 | 17 | `public void regionTerrain(long x0, long y0, int stride, int n, byte[] out)` | A region's ground at a stride (World.regionTerrain()'s contract): n x n pixels from plot (x0, y0), each `stride` plots wide. |
| 700 | 3 | `private byte pointClass(Scratch s, double[] q, double x, double y, int stride)` | A point's class at a stride: see regionTerrain(). |
| 710 | 6 | `private byte pointClass(Scratch s, double[] q, double x, double y, int stride, boolean water, double dither)` | ...with the rivers and small lakes drawn (`water`) or left to the caller, and the forest ranked through its noise or, where `dither` is a number (0 to 1), drawn where it is under the forest's share - the far node's (f... |
| 718 | 31 | `private byte classAt(Scratch s, double[] q, double x, double y, double lat, double lon, int stride, boolean water, double dither)` | ...at a point on the planet whose latitude and longitude are known. |
| 757 | 8 | `public boolean seaAt(double x, double y)` | Whether a point (plot coordinates) is sea, by the coast's rule at the point itself: the sea share there, read through the exact projection, and the coast noise at every octave - what a field drawn off the sea asks (Wo... |

### RIVERS AND SMALL LAKES, PROJECTED: a 1-degree tile's, and a district's (lines 766-910)

| line | len | member | says |
|---:|---:|---|---|
| 771 | 29 | **type** `private static final class Water` | Rivers' segments and small lakes in plots: each segment's ends, half-widths and box widened by them, each lake's centre and radius. |
| 777 | 13 | `void addSeg(double ax0, double ay0, double bx0, double by0, double h0, double h1)` _(in EarthTerrain.Water)_ |  |
| 791 | 8 | `void addLake(double x, double y, double r)` _(in EarthTerrain.Water)_ |  |
| 817 | 27 | `private Water dataTile(int tile)` | A 1-degree tile's rivers and small lakes through the projection, kept: every run's segments as stored (one crossing the tile's edge is in each tile it touches), each half-width at least RIVER_MIN_HALF. |
| 846 | 1 | `private static double halfWidth(int widthM)` | A river's half-width in plots from its width in metres: at least RIVER_MIN_HALF. |
| 849 | 10 | `private Water water(long dx, long dy)` | A district's rivers and small lakes, kept: those of the 1-degree tiles within WATER_PAD_DEG of it whose reach touches it. |
| 860 | 50 | `private Water districtWater(long dx, long dy)` |  |

### THE FAR VIEW'S NODES (0.7.119, batch E5; spec-earth 3, "Far views") (lines 911-1265)

| line | len | member | says |
|---:|---:|---|---|
| 992 | 31 | `public void farTerrain(long x0, long y0, int stride, int n, byte[] out)` | A far node's ground (spec-earth 3, "Far views"; MapTiles.nodeTerrain()): n x n pixels from plot (x0, y0), `stride` plots each. |
| 1028 | 60 | `private void strokeWater(long x0, long y0, int stride, int n, byte[] out, double[] q)` | The rivers and small lakes over a far node's pixels (farTerrain()): fresh water on every pixel that is not sea whose centre a river's stroke or a small lake's blob covers. |
| 1090 | 11 | `private boolean tileCorners(int t, double[] c, double rim)` | Tile t's four corners in plots into c (south-west, south-east, north-west, north-east, x then y), projected exactly: false - the tile's points then each projected - where the tile reaches past EarthProj.BLEND_MAX_LAT ... |
| 1103 | 8 | `private boolean place(double lat, double lon, boolean blended, double[] c, int t, double[] out)` | A point's plot in tile t: bilinear between the tile's corners where `blended` (tileCorners()), else projected; false off the planet. |
| 1113 | 4 | `private static boolean nearRim(double[] p, double rim)` | Whether a point in plots lies within RIM_M of the rim: `rim` metres or more from the spot. |
| 1119 | 19 | `private static void stroke(double ax, double ay, double bx, double by, double ha, double hb, long x0, long y0, int stride, int ...` | One river segment stroked onto a far node: every pixel not sea whose centre lies within the segment's half-width there, interpolated along it (onRiver()'s rule). |
| 1140 | 16 | `private void blob(double lx, double ly, double r, long x0, long y0, int stride, int n, byte[] out, double[] q)` | One small lake drawn onto a far node: every pixel not sea whose centre its blob holds - inLake()'s rule, its noise at the node's stride (at DITHER_STRIDE and up, at its mean: the lake's disc). |
| 1158 | 4 | `private static int first(double v, long o, int stride, int n)` | The first pixel of a node (from plot `o`, `stride` plots each, n of them) whose centre is at `v` or past it, clamped to the node: n when none is. |
| 1164 | 4 | `private static int last(double v, long o, int stride, int n)` | ...and the last whose centre is at `v` or before it: -1 when none is. |
| 1180 | 56 | `int[] nodeTiles(double x0, double y0, double side)` | The 1-degree tiles whose rivers and small lakes may reach a box of plots [x0, x0 + side)^2. |
| 1247 | 18 | `private boolean footprint(int t, double x0, double y0, double side)` | Whether 1-degree tile t, widened by WATER_PAD_DEG (the reach of its widest river past its edge), may reach a box of plots: its FOOTPRINT_POINTS x FOOTPRINT_POINTS points projected, their box widened by an eighth of it... |

### THE CELLS PASS AND THE SNAP (the world's build) (lines 1266-1280)

| line | len | member | says |
|---:|---:|---|---|
| 1271 | 9 | `void build()` | The cells pass and the snap, once (World.build() asks). |

### THE SEA'S DEPTH (0.7.121, batch E4; spec-earth 6's E4, E2's star 7) (lines 1281-1705)

| line | len | member | says |
|---:|---:|---|---|
| 1351 | 15 | `public double depthAt(long x, long y)` | The sea's depth at a plot's centre, metres below its level (★E4-1): 0 where the plot is not SALT, else World.SHELF_BREAK_M x its distance from the coast (coastDistanceM(), at least a plot) over SHELF_WIDTH_M. |
| 1368 | 3 | `public double coastDistanceM(double lat, double lon)` | ...a point's distance from the coast, metres (see the banner): the great circle to the centre of the nearest 15" cell whose sea share is under SEA_LEVEL. |
| 1381 | 54 | `static double coastDistanceM(EarthData data, double lat, double lon, double reachM)` | The great-circle distance from (lat, lon) to the centre of the nearest 15" cell of the data whose sea share is under SEA_LEVEL (COAST_BELOW), metres; infinity when there is none within reachM. |
| 1437 | 14 | `private static void offer(java.util.PriorityQueue<double[]> queue, boolean[] seen, int t, double[] p, double[] c, double best)` | A tile into the queue once, by the least angle any point of it can lie from p: the angle to its centre less the farthest of its corners from that centre. |
| 1453 | 6 | `private static void unitVector(double lat, double lon, double[] v)` | A point's unit vector on the sphere. |
| 1461 | 3 | `private static double angle(double[] a, double[] b)` | The angle between two unit vectors, radians. |
| 1473 | 14 | `public static double measureShelfWidthM(EarthData data)` | The shelf's width on a reader's data (★E4-1), metres: SHELF_SAMPLES points evenly on the sphere (a fixed hash: latitude by the arcsine of an even draw, longitude even), the sea's among them - its 15" cell's share at S... |
| 1489 | 10 | `public static int shelfSeaSamples(EarthData data)` | How many of the shelf's samples are the sea's, on a reader's data. |
| 1517 | 76 | `public void cellsPass()` | Each world cell's land and forest, as shares of its area (spec-earth 4): CELL_SAMPLES x CELL_SAMPLES points a cell, each read from the 4' far layers - its land the ground that is not sea, dry and lake (the generated w... |
| 1595 | 1 | `public Geology geology()` | The geology of its data (Geology.of()): the provinces, their classes and multipliers. |
| 1603 | 5 | `double cellFieldLand(int cell, Resource r)` | A cell's FIELD LAND for a resource, as a share of its area (0.7.117): its 16 points' land each times its province's Geology.multiplier(), oil's with a shelf's, basin's or rift's sea - what today's density is taken ove... |
| 1610 | 5 | `public int provinceAt(double x, double y)` | The province under a point in plot coordinates, its row in the data; 0 off the planet or in none. |
| 1617 | 1 | `public double cellsPassMs()` | How long the cells pass took, in ms (the last time it ran). |
| 1620 | 1 | `double cellLand(int cell)` | A cell's land, as a share of its area: the ground that is not sea (World.cellLandShare()). |
| 1623 | 1 | `double cellForest(int cell)` | A cell's forest, as a share of its area. |
| 1634 | 7 | `private void snap()` | THE SNAP (spec-earth 2): from the spot's plot - the projection's centre is the corner of four, and this is the one south-east of it, (SIDE/2, SIDE/2) - the nearest plot that passes the founding's test 1, dry ground wi... |
| 1650 | 27 | `public long[] siteNear()` | The snap's answer without the cells pass (0.7.121, batch E4: the founding card's first question, and Random spot's test): {x, y, plots tested}, x and y -1 when no plot within SNAP_PLOTS passes - a spot in the sea, or ... |
| 1679 | 9 | `boolean siteDry(long px, long py)` | The founding's test 1 at plot (px, py): it and eight points World.SITE_DRY_PLOTS round its centre are dry ground. |
| 1689 | 4 | `private boolean dry(long x, long y)` |  |
| 1695 | 1 | `long siteX()` | The founding site, as a plot: the snap's. |
| 1698 | 1 | `long siteY()` | ...and its row. |
| 1701 | 1 | `int siteTests()` | How many plots the snap tested. |
| 1704 | 1 | `public static long spotPlot()` | The plot the snap starts from: the spot's, (SIDE/2, SIDE/2). |

