# EarthTerrain.java - 1,029 lines · 50 methods · 27 constants · model

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
> THE WORLD'S OTHER HALF. The cells pass (cellsPass()) replaces the
> generated world's sea pass: each world cell's land and forest from
> CELL_SAMPLES x CELL_SAMPLES points of the 4' layers, which the fields of
> ore and oil are drawn on as the generated world's are on its cells. The
> founding is the snap (spec-earth 2): from the spot, the projection's
> centre, the nearest plot, ring by ring of one plot, that passes the
> founding's test 1 (World.SITE_DRY_PLOTS); there is no founding river.
> 
> WHICH DATA. A world's key names its data by identity (EarthData.identity(),
> "earth-1:<hash>"); a harness registers its EarthData with use(), and the
> game's file is found by EarthData.locate() the first time its identity is
> asked for. Every value is a pure function of the key: the same key draws
> the same ground on every machine (StrictMath, World's hashes).
> 
> ... (3 more lines in the source)

**Uses:** [World](World.md) (75), [EarthData](EarthData.md) (43), [EarthProj](EarthProj.md) (6)

**Used by (2):** [EarthTerrainCheck](EarthTerrainCheck.md), [World](World.md)

## Sections

| line | section |
|---:|---|
| 81 | THE DIALS |
| 185 | THE DATA A KEY NAMES |
| 222 | ONE EARTH WORLD'S STATE |
| 263 | THE RANKING TABLE (spec-earth 3's QUANTILE) |
| 319 | READING THE DATA: a layer's cells, a few tiles kept in hand |
| 433 | A TILE, OR A LINE OF ONE |
| 612 | A PLOT, A LINE, A REGION AND A POINT'S SEA |
| 736 | RIVERS AND SMALL LAKES, PROJECTED: a 1-degree tile's, and a district's |
| 881 | THE CELLS PASS AND THE SNAP (the world's build) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `EarthTerrain.NOISE_HI` | `4` | The noise's widest octave, 2^4 plots (480 m): just over the data's 15" cell (463 m), so the detail drawn lives below what the data resolves (spec-earth 3). |
| 89 | `EarthTerrain.NOISE_LO` | `1` | ...down to 2^1 plots (60 m), two plots, the finest a plot-centred noise can carry (spec-earth 3). |
| 92 | `EarthTerrain.SEA_LEVEL` | `0.5` | The sea's share from which the coast is drawn, 0.5: the data's half-contour (spec-earth 3) - and what an Earth world gives as its sea's level (World.seaTheta()), having no heights. |
| 104 | `EarthTerrain.COAST_FRAY` | `1.0` | How far the coast noise frays the coast: 1.0 (spec-earth 3's COAST_FRAY) - sea where share + 1.0 x (noise - 0.5) reaches SEA_LEVEL, the noise unranked, so the coast is the interpolated share's half-contour moved by th... |
| 113 | `EarthTerrain.BLOB_FRAY` | `1.0` | How far a small lake's edge is frayed: 1.0 (★E2, the coast's own rule on a lake's disc) - a plot at distance d from a lake of radius r is in it where (d/r)^2 + 1.0 x (rank - 0.5) is under 1, so the blob reaches from 0... |
| 116 | `EarthTerrain.QUANTILE_SAMPLES` | `1<<18` | The noise's samples the ranking table holds: 2^18 (spec-earth 3's QUANTILE), at fixed places, one table for every class and every world. |
| 119 | `EarthTerrain.LATTICE_STEP` | `4` | Plots between the points a tile reads its shares at: 4 (120 m), so a tile's 34 x 34 region holds 10 x 10 of them on the world's own grid of them (spec-earth 3: a 10 x 10 lattice). |
| 122 | `EarthTerrain.LATTICE` | `World.TILE / LATTICE_STEP + 2` | Points a side of a tile's lattice: TILE / LATTICE_STEP + 2, 10 - the region's ring and its far edge. |
| 125 | `EarthTerrain.RIVER_MIN_HALF` | `0.75` | A river's least half-width, in plots: 0.75, so a 30 m river - half a plot each side of its line - leaves no gaps (spec-earth 3). |
| 128 | `EarthTerrain.SNAP_PLOTS` | `167` | How far the founding snaps from the spot, in plots: 167 (5 km, the founding card's reach, spec-earth 2); a spot with no site within it has none, and E4's screen refuses it. |
| 131 | `EarthTerrain.CELL_SAMPLES` | `4` | Points a side of a world cell the cells pass reads: 4, so 16 a cell of the 4' layers (spec-earth 4). |
| 134 | `EarthTerrain.FAR_STRIDE` | `256` | The stride, in plots, from which a region reads the 4' far layers instead of the 15" shares: 256 (7.68 km, a district), about a 4' cell's 7.4 km (★E2: wider pixels would inflate a 15" tile for every few). |
| 137 | `EarthTerrain.WATER_STRIDE` | `16` | The stride under which a region draws rivers and small lakes: 16 plots (480 m), the noise's widest octave (★E2): past it a pixel is wider than any detail of the ground's own, and the far view's rivers are stroked from... |
| 146 | `EarthTerrain.WATER_PAD_DEG` | `0.25` | How far round a district its rivers are looked for, in degrees of latitude: 0.25 (27.8 km; ★E2) - a river is stored in every 1-degree tile its centreline's box touches, so a district reads the tiles within this of it ... |
| 149 | `EarthTerrain.WIDEST_RIVER_M` | `40_650` | The widest river in earth-1, in metres: 40,650, measured over all its tiles (EarthTerrainCheck 9 holds it, and WATER_PAD_DEG to it, when the file is named). |
| 152 | `EarthTerrain.TILES_KEPT` | `1024` | Tiles of ground kept for terrainAt(), lineTerrain() and the snap: 1,024 (1 MB), the snap's 5 km disc and a screen's worth. |
| 155 | `EarthTerrain.DISTRICTS_KEPT` | `256` | Districts' rivers and small lakes kept, projected: 256. |
| 158 | `EarthTerrain.DATA_TILES_KEPT` | `64` | 1-degree tiles' rivers and small lakes kept, projected: 64. |
| 161 | `EarthTerrain.F_COAST` | `0xC0A57L, F_FRESH = 0xF4E54L, F_FOREST = 0xF0E57L, F_BLOB = 0xB10BL` | The fields' keys: what makes one class's noise another's. |
| 164 | `EarthTerrain.QUANTILE_SALT` | `0x0A1E5A3DL` | The stream the ranking table's samples are drawn from. |
| 167 | `EarthTerrain.AMP_N` | `new double [ NOISE_HI + 1 ]` | Each octave's amplitude over the sum of them, octave k at AMP_N[k]. |
| 170 | `EarthTerrain.SMOOTH` | `new double [ NOISE_HI + 1 ][]` | The smoothstep at a plot's centre within octave k's lattice cell, SMOOTH[k][f] for f = 0 .. |
| 190 | `EarthTerrain.SOURCES` | `new HashMap<>()` | The readers use() has made available, by their identity: where source() looks for a key's data first. |
| 233 | `EarthTerrain.COAST` | `0, FRESH = 1, FOREST = 2, BLOB = 3` | Each noise field's row in os: the coast's, fresh water's, the forest's and the small lakes' blobs'. |
| 403 | `EarthTerrain.Scratch.RING` | `World.TILE + 2, RING_AREA = RING * RING, AREA = World.TILE * World.TILE` | A tile with its ring of one plot each side, RING plots a side (RING_AREA in all); a tile alone, AREA plots. |
| 425 | `EarthTerrain.SCRATCH` | `ThreadLocal.withInitial(Scratch : : new)` | Each thread's own working arrays and readers, so tiles drawn on several threads at once share none. |
| 772 | `EarthTerrain.NO_WATER` | `new Water()` | A district's water where it lies off the world or off the planet: no river and no small lake. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 226 | `private final World.Key key` |  |
| 227 | `private final long seed` |  |
| 228 | `private final EarthData data` |  |
| 229 | `private final EarthProj proj` |  |
| 231 | `private final long[][] os` | Each field's octave streams: COAST, FRESH, FOREST, BLOB, octave k at [f][k]. |
| 235 | `private volatile boolean built` |  |
| 236 | `private double[] cellLand, cellForest` |  |
| 237 | `private long siteX, siteY` |  |
| 238 | `private int siteTests` |  |
| 239 | `private double cellsMs` |  |
| 267 | `private static volatile double[] quantile` |  |
| 325 | `EarthData data` |  |
| 326 | `EarthData.Layer layer` |  |
| 327 | `int cpd, cols` |  |
| 328 | `boolean tiles` |  |
| 329 | `final int[] ids` |  |
| 330 | `final byte[][] bytes` |  |
| 331 | `final int[] uniform` |  |
| 332 | `int next` |  |
| 404 | `final double[] coast` |  |
| 405 | `final double[] lat` |  |
| 406 | `final double[] seaL` |  |
| 407 | `final boolean[] sea` |  |
| 408 | `final double[] ll` |  |
| 409 | `int[] segs` |  |
| 410 | `final Reader seaR` |  |
| 411 | `EarthData bound` |  |
| 616 | `private final Map<Long, byte[]> tiles` |  |
| 742 | `double[] ax` |  |
| 743 | `double[] minX` |  |
| 744 | `double[] lx` |  |
| 745 | `int segs, lakes` |  |
| 774 | `private final Map<Integer, Water> dataTiles` |  |
| 780 | `private final Map<Long, Water> districts` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 951 | **type** `public final class EarthTerrain` | The real Earth's ground on the world's plots: a tile, a plot, a line or a region at a time, each world cell's land and forest (the cells pass), the founding site snapped to dry ground, and a point's sea - spec-earth 3... |

### THE DIALS (lines 81-184)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 12 | `static { ... }` |  |

### THE DATA A KEY NAMES (lines 185-221)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 5 | `public static String use(EarthData data)` | Makes a reader of Earth's data available to the worlds whose keys name it: returns its identity, the key's `data`. |
| 200 | 21 | `static EarthData source(String identity)` | The reader a key's data names: one use() made available, else the file EarthData.locate() finds when its identity is that. |

### ONE EARTH WORLD'S STATE (lines 222-262)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 11 | `EarthTerrain(World.Key key)` | The ground of an Earth world's key; the data it names is found here (source()). |
| 255 | 1 | `public World.Key key()` | Its key. |
| 258 | 1 | `public EarthProj projection()` | The projection it is laid through, centred on the key's spot. |
| 261 | 1 | `public EarthData data()` | The data it is drawn from. |

### THE RANKING TABLE (spec-earth 3's QUANTILE) (lines 263-318)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 19 | `static double[] quantile()` | The noise's distribution: QUANTILE_SAMPLES values of it at plot centres hashed from QUANTILE_SALT, sorted - built once, the same everywhere. |
| 291 | 5 | `static double below(double p)` | The noise value under which share p of its samples lie: minus infinity at p of nought or less, plus infinity at one or more. |
| 298 | 5 | `private static double below(double[] q, double p)` | ...from a table in hand. |
| 305 | 3 | `static boolean sea(double share, double coastNoise)` | Whether a plot is sea (spec-earth 3): its sea share plus COAST_FRAY x its coast noise about the noise's mean, 0.5, reaches SEA_LEVEL - the share's half-contour, frayed. |
| 310 | 8 | `static double noiseAt(long[] os, double x, double y, double stride)` | One field's noise at a point in plots, its octaves no wider than `stride` at their mean (World.regionTerrain()'s rule): at a stride of one, every octave. |

### READING THE DATA: a layer's cells, a few tiles kept in hand (lines 319-432)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 75 | **type** `private static final class Reader` | One share layer read cell by cell, the last few of its tiles (or bands) in hand: one thread's. |
| 334 | 9 | `void bind(EarthData d, String name)` _(in EarthTerrain.Reader)_ |  |
| 345 | 12 | `int value(long r, long c)` _(in EarthTerrain.Reader)_ | The value of the cell in global row r (from 90 N) and column c (from 180 W), rows clamped to the globe and columns wrapped round it. |
| 358 | 14 | `private int slot(int id)` _(in EarthTerrain.Reader)_ |  |
| 374 | 8 | `double bilinear(double lat, double lon)` _(in EarthTerrain.Reader)_ | The layer's value at (lat, lon) interpolated between the four cell centres about it, 0 to 255 (bilinear; columns wrap at the date line). |
| 384 | 9 | `double bilinearLow(double lat, double lon)` _(in EarthTerrain.Reader)_ | ...the forest level of land cover, 0 to 15, interpolated the same way. |
| 395 | 3 | `int at(double lat, double lon)` _(in EarthTerrain.Reader)_ | The value of the cell (lat, lon) lies in: a cell holds its north and west edges (EarthData's WHERE A POINT FALLS). |
| 401 | 22 | **type** `private static final class Scratch` | One thread's working arrays and readers. |
| 413 | 9 | `void bind(EarthData d)` _(in EarthTerrain.Scratch)_ |  |
| 427 | 5 | `private Scratch scratch()` |  |

### A TILE, OR A LINE OF ONE (lines 433-611)

| line | len | member | says |
|---:|---:|---|---|
| 442 | 3 | `public void tileTerrain(long tx, long ty, byte[] out)` | One tile's ground, a byte a plot in rows (out[py x 32 + px]): spec-earth 3's rule (the class header). |
| 453 | 102 | `private void draw(long tx, long ty, int ox, int oy, int w, int h, byte[] out)` | Plots (ox, oy) to (ox + w - 1, oy + h - 1) of tile (tx, ty), row by row (out[(py - oy) x w + px - ox]): each the tile's, byte for byte - the same lattice points from the same corners, the same shares, octaves and sums... |
| 557 | 8 | `private static double lattice(double[] at, int px, int py)` | A share at the tile's plot (px, py) - -1 to 32, the ring included - blended between the lattice's points about it. |
| 567 | 4 | `private static boolean beach(Scratch s, EarthProj.Blend b, double x, double y)` | Whether the 2' cell under the point (plot coordinates) has bare ground on 2% of its land or more (EarthData.LC_BEACH). |
| 573 | 20 | `private static void octave(double[] lat, long os, int k, long gx0, long gy0, int w, int h, double amp, double[] acc)` | Adds `amp` x octave k's value noise at the plot centres of a w x h rectangle from plot (gx0, gy0), row by row: its lattice hashed once, the smoothstep from SMOOTH (World.octaveTile()'s way) - a plot's value the same w... |
| 595 | 7 | `private static boolean onRiver(Water w, int k, double x, double y)` | Whether a point lies within a river segment's half-width, interpolated along it. |
| 604 | 7 | `private static boolean inLake(Water w, int k, double x, double y, double n, double[] q)` | Whether a point is in small lake k's blob, its blob noise there `n` (a stride's noise, at a region's pixel). |

### A PLOT, A LINE, A REGION AND A POINT'S SEA (lines 612-735)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 11 | `byte[] keptTile(long tx, long ty)` | A tile's ground, kept (TILES_KEPT): shared, do not write into it. |
| 636 | 5 | `public byte terrainAt(long x, long y)` | One plot's ground: its tile's (tileTerrain()), from a kept tile; sea off the world. |
| 643 | 14 | `public void lineTerrain(long tx, long ty, boolean column, int at, byte[] out)` | One row (or, `column`, one column) of a tile's ground, its `at`-th: the tile's, byte for byte (World.lineTerrain()'s contract) - from the tile if it is kept, else the line drawn alone (draw()). |
| 666 | 17 | `public void regionTerrain(long x0, long y0, int stride, int n, byte[] out)` | A region's ground at a stride (World.regionTerrain()'s contract): n x n pixels from plot (x0, y0), each `stride` plots wide. |
| 685 | 34 | `private byte pointClass(Scratch s, double[] q, double x, double y, int stride)` | A point's class at a stride: see regionTerrain(). |
| 727 | 8 | `public boolean seaAt(double x, double y)` | Whether a point (plot coordinates) is sea, by the coast's rule at the point itself: the sea share there, read through the exact projection, and the coast noise at every octave - what a field drawn off the sea asks (Wo... |

### RIVERS AND SMALL LAKES, PROJECTED: a 1-degree tile's, and a district's (lines 736-880)

| line | len | member | says |
|---:|---:|---|---|
| 741 | 29 | **type** `private static final class Water` | Rivers' segments and small lakes in plots: each segment's ends, half-widths and box widened by them, each lake's centre and radius. |
| 747 | 13 | `void addSeg(double ax0, double ay0, double bx0, double by0, double h0, double h1)` _(in EarthTerrain.Water)_ |  |
| 761 | 8 | `void addLake(double x, double y, double r)` _(in EarthTerrain.Water)_ |  |
| 787 | 27 | `private Water dataTile(int tile)` | A 1-degree tile's rivers and small lakes through the projection, kept: every run's segments as stored (one crossing the tile's edge is in each tile it touches), each half-width at least RIVER_MIN_HALF. |
| 816 | 1 | `private static double halfWidth(int widthM)` | A river's half-width in plots from its width in metres: at least RIVER_MIN_HALF. |
| 819 | 10 | `private Water water(long dx, long dy)` | A district's rivers and small lakes, kept: those of the 1-degree tiles within WATER_PAD_DEG of it whose reach touches it. |
| 830 | 50 | `private Water districtWater(long dx, long dy)` |  |

### THE CELLS PASS AND THE SNAP (the world's build) (lines 881-1029)

| line | len | member | says |
|---:|---:|---|---|
| 886 | 9 | `void build()` | The cells pass and the snap, once (World.build() asks). |
| 906 | 52 | `public void cellsPass()` | Each world cell's land and forest, as shares of its area (spec-earth 4): CELL_SAMPLES x CELL_SAMPLES points a cell, each read from the 4' far layers - its land the ground that is not sea, dry and lake (the generated w... |
| 960 | 1 | `public double cellsPassMs()` | How long the cells pass took, in ms (the last time it ran). |
| 963 | 1 | `double cellLand(int cell)` | A cell's land, as a share of its area: the ground that is not sea (World.cellLandShare()). |
| 966 | 1 | `double cellForest(int cell)` | A cell's forest, as a share of its area. |
| 977 | 24 | `private void snap()` | THE SNAP (spec-earth 2): from the spot's plot - the projection's centre is the corner of four, and this is the one south-east of it, (SIDE/2, SIDE/2) - the nearest plot that passes the founding's test 1, dry ground wi... |
| 1003 | 9 | `boolean siteDry(long px, long py)` | The founding's test 1 at plot (px, py): it and eight points World.SITE_DRY_PLOTS round its centre are dry ground. |
| 1013 | 4 | `private boolean dry(long x, long y)` |  |
| 1019 | 1 | `long siteX()` | The founding site, as a plot: the snap's. |
| 1022 | 1 | `long siteY()` | ...and its row. |
| 1025 | 1 | `int siteTests()` | How many plots the snap tested. |
| 1028 | 1 | `public static long spotPlot()` | The plot the snap starts from: the spot's, (SIDE/2, SIDE/2). |

