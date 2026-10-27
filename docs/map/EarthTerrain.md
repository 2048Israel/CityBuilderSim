# EarthTerrain.java - 3,048 lines · 153 methods · 61 constants · model

`ham/citybuildersim/EarthTerrain.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

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
>   - SHALLOW (ET1, spec-earth2-terrain 6.1) for a sea plot where the shallow
>     sea's share of the sea there - earth-2's shallow layer, interpolated
>     between the 15" cells' centres WEIGHTED BY EACH CELL'S SEA, so a coast
>     grows no fringe of deep sea - ranks over the shallow noise (its own,
>     ranked as the forest's is, so it covers in expectation what the data
>     gives it); the rest of the sea is SALT. Only on data that has the
>     layer (EarthData.hasRelief()): an earth-1 file draws none;
>   - FRESH where a river's centreline (GRWL) passes within half its width,
>     at least RIVER_MIN_HALF; where a small lake (under 10 km2, a point and
>     an area in the data) lies, drawn as a blob of its own area in
>     expectation, its edge frayed by BLOB_FRAY of its own noise; or where the
>     fresh noise ranks under the lake share of the ground that is not sea;
>   - ICE (0.7.127, batch E7; spec-globe 3.9) where its 2' cell's snow and
>     ice bit (EarthData.LC_ICE), read 0 or 1 at each cell and interpolated
>     between the cells' centres as the shares are, plus COVER_FRAY x its
>     own noise about its mean, unranked, reaches COVER_LEVEL: the bit's
>     half-contour, frayed as the coast is - land, never dry (World.dry());
>   - SAND where a dry plot has the sea on one of its eight sides and its 2'
>     cell's bare ground is 2% of its land or more (EarthData.LC_BEACH);
>   - FOREST where the forest noise ranks under the forest share of the dry
>     ground;
>   - DESERT (0.7.127) where the bare-ground bit (EarthData.LC_DESERT) passes
>     the same test as ice's, with its own noise: dry ground every rule
>     treats as grass, only its colour and name its own (spec-globe Q1);
>   - GRASS the rest. Real land use is ignored: the city starts on
>     unsettled ground.
> A plot (terrainAt()) is its tile's, from a kept tile, and a line
> (lineTerrain()) its tile's byte for byte, drawn alone where the tile is not
> kept (draw()); a region at a stride (regionTerrain()) classes each pixel's
> centre from the shares there and the octaves wider than a pixel, the
> ... (61 more lines in the source)

**Uses:** [World](World.md) (186), [EarthData](EarthData.md) (90), [EarthProj](EarthProj.md) (28), [Resource](Resource.md) (13), [Geology](Geology.md) (7)

**Used by (22):** [BridgeCheck](BridgeCheck.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthSite](EarthSite.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [Gateways](Gateways.md), [GlobeCheck](GlobeCheck.md), [GlobeFrame](GlobeFrame.md), [GlobePicture](GlobePicture.md), [HydroSites](HydroSites.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LandmassCheck](LandmassCheck.md), [Landmasses](Landmasses.md), [MapCheck](MapCheck.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [ReliefRenders](ReliefRenders.md), [SaveFileCheck](SaveFileCheck.md), [SlopeCheck](SlopeCheck.md), [World](World.md)

## Sections

| line | section |
|---:|---|
| 140 | THE DIALS |
| 297 | THE DATA A KEY NAMES |
| 334 | ONE EARTH WORLD'S STATE |
| 380 | THE RANKING TABLE (spec-earth 3's QUANTILE) |
| 441 | READING THE DATA: a layer's cells, a few tiles kept in hand |
| 657 | A TILE, OR A LINE OF ONE |
| 895 | A PLOT, A LINE, A REGION AND A POINT'S SEA |
| 1072 | THE RELIEF: a second plane beside the cover (ET1, spec-earth2-terrain 3.1) |
| 1216 | THE LANDMASS OF A PLOT (ET5, 2026-10-25; the ET5 design, D2 to D4) |
| 1634 | RIVERS AND SMALL LAKES, PROJECTED: a 1-degree tile's, and a district's |
| 1779 | THE FAR VIEW'S NODES (0.7.119, batch E5; spec-earth 3, "Far views") |
| 2098 | · the camera: where the far rule's pixels lie on the planet (0.7.132, batch E8; spec-globe 3.5) |
| 2562 | THE CELLS PASS AND THE SNAP (the world's build) |
| 2577 | THE SEA'S DEPTH (0.7.121, batch E4; ET1, 2026-10-24: spec-earth2-terrain 7) |
| 2736 | FIELD LAND AT A POINT (0.8.12, batch EG1; spec-earth2-terrain 8.5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 145 | `EarthTerrain.NOISE_HI` | `4` | The noise's widest octave, 2^4 plots (480 m): just over the data's 15" cell (463 m), so the detail drawn lives below what the data resolves (spec-earth 3). |
| 148 | `EarthTerrain.NOISE_LO` | `1` | ...down to 2^1 plots (60 m), two plots, the finest a plot-centred noise can carry (spec-earth 3). |
| 151 | `EarthTerrain.SEA_LEVEL` | `0.5` | The sea's share from which the coast is drawn, 0.5: the data's half-contour (spec-earth 3) - and what an Earth world gives as its sea's level (World.seaTheta()), having no heights. |
| 163 | `EarthTerrain.COAST_FRAY` | `1.0` | How far the coast noise frays the coast: 1.0 (spec-earth 3's COAST_FRAY) - sea where share + 1.0 x (noise - 0.5) reaches SEA_LEVEL, the noise unranked, so the coast is the interpolated share's half-contour moved by th... |
| 172 | `EarthTerrain.BLOB_FRAY` | `1.0` | How far a small lake's edge is frayed: 1.0 (★E2, the coast's own rule on a lake's disc) - a plot at distance d from a lake of radius r is in it where (d/r)^2 + 1.0 x (rank - 0.5) is under 1, so the blob reaches from 0... |
| 175 | `EarthTerrain.COVER_LEVEL` | `0.5` | The bit's share from which a plot is ice, or desert (0.7.127, batch E7; spec-globe 3.9): 0.5, its half-contour - each kind's land-cover bit read 0 or 1 at its 2' cell and interpolated between the cells' centres, as th... |
| 184 | `EarthTerrain.COVER_FRAY` | `COAST_FRAY` | How far each kind's own noise frays the edge of ice and of the desert (0.7.127, batch E7; spec-globe 3.9: "tested against 1/2 plus its own noise, as the coast is"): COAST_FRAY, 1.0 - ice where the bit's share + 1.0 x ... |
| 187 | `EarthTerrain.QUANTILE_SAMPLES` | `1<<18` | The noise's samples the ranking table holds: 2^18 (spec-earth 3's QUANTILE), at fixed places, one table for every class and every world. |
| 190 | `EarthTerrain.LATTICE_STEP` | `4` | Plots between the points a tile reads its shares at: 4 (120 m), so a tile's 34 x 34 region holds 10 x 10 of them on the world's own grid of them (spec-earth 3: a 10 x 10 lattice). |
| 193 | `EarthTerrain.LATTICE` | `World.TILE / LATTICE_STEP + 2` | Points a side of a tile's lattice: TILE / LATTICE_STEP + 2, 10 - the region's ring and its far edge. |
| 196 | `EarthTerrain.RIVER_MIN_HALF` | `0.75` | A river's least half-width, in plots: 0.75, so a 30 m river - half a plot each side of its line - leaves no gaps (spec-earth 3). |
| 199 | `EarthTerrain.SNAP_PLOTS` | `167` | How far the founding snaps from the spot, in plots: 167 (5 km, the founding card's reach, spec-earth 2); a spot with no site within it has none, and E4's screen refuses it. |
| 202 | `EarthTerrain.BEYOND_PLOTS` | `1667` | How far past the spot the nearest buildable ground is looked for when the snap finds none (ET2, spec-earth2-terrain 3.4): 1,667 plots (50 km); past it the founding card says there is none. |
| 205 | `EarthTerrain.BEYOND_STEP` | `8` | The step of that search (ET2): a lattice of spots 8 plots (240 m) apart, each tried by the founding's test 1, so the nearest it reports is within a lattice step of the true nearest - cheap where the snap's ring of one... |
| 208 | `EarthTerrain.CELL_SAMPLES` | `4` | Points a side of a world cell the cells pass reads: 4, so 16 a cell of the 4' layers (spec-earth 4). |
| 211 | `EarthTerrain.FAR_STRIDE` | `256` | The stride, in plots, from which a region reads the 4' far layers instead of the 15" shares: 256 (7.68 km, a district), about a 4' cell's 7.4 km (★E2: wider pixels would inflate a 15" tile for every few). |
| 214 | `EarthTerrain.WATER_STRIDE` | `16` | The stride under which a region draws rivers and small lakes: 16 plots (480 m), the noise's widest octave (★E2): past it a pixel is wider than any detail of the ground's own, and the far view's rivers are stroked from... |
| 223 | `EarthTerrain.WATER_PAD_DEG` | `0.25` | How far round a district its rivers are looked for, in degrees of latitude: 0.25 (27.8 km; ★E2) - a river is stored in every 1-degree tile its centreline's box touches, so a district reads the tiles within this of it ... |
| 226 | `EarthTerrain.WIDEST_RIVER_M` | `40_650` | The widest river in earth-1, in metres: 40,650, measured over all its tiles (EarthTerrainCheck 9 holds it, and WATER_PAD_DEG to it, when the file is named). |
| 229 | `EarthTerrain.TILES_KEPT` | `1024` | Tiles of ground kept for terrainAt(), lineTerrain() and the snap: 1,024 (1 MB), the snap's 5 km disc and a screen's worth. |
| 232 | `EarthTerrain.DISTRICTS_KEPT` | `256` | Districts' rivers and small lakes kept, projected: 256. |
| 235 | `EarthTerrain.DATA_TILES_KEPT` | `64` | 1-degree tiles' rivers and small lakes kept, projected: 64. |
| 247 | `EarthTerrain.RELIEF_SHIFT` | `2` | How much wider the relief's noise is than the cover noises' (ET1, spec-earth2-terrain 3.1): its octaves are theirs scaled by 2^RELIEF_SHIFT, 2^3 plots (240 m) down to 2^6 (1.9 km) at 2, so a slope is drawn as a connec... |
| 250 | `EarthTerrain.RELIEF_SCALE` | `1<<RELIEF_SHIFT` | The relief noise's lattice scale as a divisor of plot coordinates: 2^RELIEF_SHIFT, 4 (the noise is the cover noises' at x / 4, y / 4). |
| 253 | `EarthTerrain.RELIEF_KEPT` | `1024` | Tiles of relief kept for reliefAt() and tileRelief(): 1,024 (1 MB), a tile of ground's partner (TILES_KEPT). |
| 256 | `EarthTerrain.FAR_BLOCK` | `16` | Cells a side of the block a far pixel reads the slope of from FAR_STRIDE: 16 (4', 7.4 km), the 4' far layers' cell - 15 of them a side of a 1-degree tile, 225 a tile. |
| 259 | `EarthTerrain.F_COAST` | `0xC0A57L, F_FRESH = 0xF4E54L, F_FOREST = 0xF0E57L, F_BLOB = 0xB10BL, F_ICE = ...` | The fields' keys: what makes one class's noise another's. |
| 262 | `EarthTerrain.F_RELIEF_DITHER` | `0x4E11D17L` | The stream a far pixel's relief dither is drawn from: the camera's dither (the forest's) remixed with it, for the two must not be one number, or the steep ground would be the forested. |
| 265 | `EarthTerrain.SHARE_AREA` | `(double) EarthData.SHARE_LEVELS * EarthData.SHARE_LEVELS` | A share layer's levels squared, 15 x 15: what bilinearProduct() of two of them is over to be a share of the area (ET1). |
| 268 | `EarthTerrain.QUANTILE_SALT` | `0x0A1E5A3DL` | The stream the ranking table's samples are drawn from. |
| 271 | `EarthTerrain.AMP_N` | `new double [ NOISE_HI + 1 ]` | Each octave's amplitude over the sum of them, octave k at AMP_N[k]. |
| 274 | `EarthTerrain.SLOPE_F` | `new double [ 256 ], SLOPE_S = new double [ 256 ], SLOPE_V = new double [ 256 ]` | A slope byte's shares of the land at or above foothill, steep and very steep, by byte (ET1): EarthData's decoders, the steep share held within the foothill share and the very steep within the steep - the file promises... |
| 277 | `EarthTerrain.SMOOTH` | `new double [ NOISE_HI + RELIEF_SHIFT + 1 ][]` | The smoothstep at a plot's centre within octave k's lattice cell, SMOOTH[k][f] for f = 0 .. |
| 302 | `EarthTerrain.SOURCES` | `new HashMap<>()` | The readers use() has made available, by their identity: where source() looks for a key's data first. |
| 347 | `EarthTerrain.COAST` | `0, FRESH = 1, FOREST = 2, BLOB = 3, ICE = 4, DESERT = 5, SHALLOW = 6, RELIEF = 7` | Each noise field's row in os: the coast's, fresh water's, the forest's, the small lakes' blobs', (0.7.127) the ice's and the desert's edges', and (ET1) the shallow sea's and the relief's. |
| 602 | `EarthTerrain.Scratch.RING` | `World.TILE + 2, RING_AREA = RING * RING, AREA = World.TILE * World.TILE` | A tile with its ring of one plot each side, RING plots a side (RING_AREA in all); a tile alone, AREA plots. |
| 649 | `EarthTerrain.SCRATCH` | `ThreadLocal.withInitial(Scratch : : new)` | Each thread's own working arrays and readers, so tiles drawn on several threads at once share none. |
| 1241 | `EarthTerrain.LANDMASS_REACH` | `8` | How many 15" cells either way (east-west and north-south, about 3.7 km north-south) a dry plot on a cell the layer calls no land looks for the nearest land cell: 8. |
| 1244 | `EarthTerrain.RESOLVED_KEPT` | `65_536` | Cells whose nearest land cell is kept (resolvedCell()) before the memory is cleared: 65,536. |
| 1575 | `EarthTerrain.FarShares.OF` | `new WeakHashMap<>()` |  |
| 1577 | `EarthTerrain.FarShares.NONE` | `new byte [ 0 ]` | A tile built and found flat or without land, kept in place of an array. |
| 1670 | `EarthTerrain.NO_WATER` | `new Water()` | A district's water where it lies off the world or off the planet: no river and no small lake. |
| 1784 | `EarthTerrain.DITHER_STRIDE` | `1<<NOISE_HI` | A far node's forest is dithered by a hash from this stride, in plots: 2^NOISE_HI, 16 (480 m) - where no octave of the forest's noise is wider than a pixel, so the ranked noise stands at its mean and would draw forest ... |
| 1787 | `EarthTerrain.FAR_RIVER_HALF_PX` | `RIVER_MIN_HALF` | A river's least half-width on a far node, in pixels: RIVER_MIN_HALF, 0.75 - a far pixel is to the node what a plot is to a tile, so a river is stroked at least 1.5 px wide, past the 0.71 px (half a pixel's diagonal) u... |
| 1802 | `EarthTerrain.FAR_RIVER_SHARE` | `30.0 / 2500` | The least width, as a share of a far pixel, at which a river is stroked: 30 / 2,500, 0.012 (★E5) - the narrowest river in the data, GRWL's 30 m (spec-earth 1), at the study's far render of 2.5 km a pixel (spec-earth 3... |
| 1805 | `EarthTerrain.FAR_BLEND_PX` | `8` | From WATER_STRIDE a far node's pixels take their latitude and longitude from blocks of this many a side, each blended from its four corners (EarthProj.blend(), the tile's way): 8 - measured, at worst 0.15 of a pixel o... |
| 1817 | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | `64` | From this stride, in plots, a far node places a 1-degree tile's river vertices and small lakes bilinear between the tile's four corners, projected exactly, rather than projecting each: 64 (★E5) - measured at worst 0.0... |
| 1820 | `EarthTerrain.NODE_EDGE_POINTS` | `32` | Points along each edge of a far node its box of 1-degree tiles is found from: 32 - at the widest node (1,024 plots a pixel, 3,932 km a side) one every 123 km, where a straight edge strays from its chord by a fraction ... |
| 1823 | `EarthTerrain.LONGEST_SEGMENT_M` | `17_915` | The longest river segment in earth-1, in metres on the sphere: 17,915, measured over all its tiles (MapCheck 10 holds it when the file is named). |
| 1835 | `EarthTerrain.RIM_M` | `(double) LONGEST_SEGMENT_M * LONGEST_SEGMENT_M /(4 * EarthProj.RADIUS_M)` | A river segment with an end this close to the planet's rim is not stroked, in metres: LONGEST_SEGMENT_M^2 / 4R, 12.6 (★E5). |
| 1838 | `EarthTerrain.F_DITHER` | `0xD17E4L` | The far view's dither's stream. |
| 1934 | `EarthTerrain.FAR_COUNTS` | `ThreadLocal.withInitial(() -> new long [ 3 ])` | How many river segments and small lakes the last far node on this thread drew from, and how many 1-degree tiles it read: what a harness prints. |
| 2171 | `EarthTerrain.Camera.NO_SHIFT` | `{ 0 }` | The one shift a camera whose x does not wrap draws a shape at: none, the shape where it is. |
| 2251 | `EarthTerrain.GLOBE_DITHER_DEG` | `4.0 / 60` | The globe's dither cell (spec-globe 3.5: "the dither's hash comes from a fixed latitude-longitude grid at the pixel's scale, so the forest does not shimmer as the globe turns"): rows of GLOBE_DITHER_DEG x 2^level degr... |
| 2254 | `EarthTerrain.DITHER_WIDENS` | `new double [ 30 ]` | The latitudes past which a dither cell is 2^m times as wide in longitude as tall, m = 1, 2, ...: where cos lat falls to 2^-m. |
| 2261 | `EarthTerrain.F_GLOBE_DITHER` | `0x610BED17L` | The globe's dither's stream (the far node's F_DITHER is the plane's, by plot and the world's seed). |
| 2284 | `EarthTerrain.M_A_DEGREE` | `Math.PI * EarthProj.RADIUS_M / 180` | How many metres of ground a degree of latitude is on the sphere: pi R / 180. |
| 2534 | `EarthTerrain.FOOTPRINT_POINTS` | `3` | Points a side of a 1-degree tile its footprint is read at: 3, its corners, the middles of its edges and its centre. |
| 2610 | `EarthTerrain.COAST_BELOW` | `(int) Math.ceil(SEA_LEVEL * EarthData.SHARE_LEVELS)` | The sea layer's levels under which a 15" cell is the coast's land side: the share under SEA_LEVEL, 0 to 7 of 15 - the land of the landmass layer (tools/earth/FORMAT-2.txt). |
| 2613 | `EarthTerrain.SEA_LEAST_DEPTH_M` | `1` | The shallowest a sea plot is, metres: 1 (FORMAT-2: "a sea plot is never shallower than 1 m in the game"), so World.depthAt() over 0 is still how a sea plot is told from land. |
| 2616 | `EarthTerrain.DEPTHS_KEPT` | `4096` | Depths kept, by plot: 4,096 (a field's depth is asked again every time the city's ground changes). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 338 | `private final World.Key key` |  |
| 339 | `private final long seed` |  |
| 340 | `private final EarthData data` |  |
| 341 | `private final EarthProj proj` |  |
| 343 | `private final boolean relief` | Whether its data has the slope layer (EarthData.hasRelief()): without it every plot's relief is FLAT. |
| 345 | `private final long[][] os` | Each field's octave streams: COAST, FRESH, FOREST, BLOB, ICE, DESERT, SHALLOW, RELIEF, octave k at [f][k] (the relief's octave k is the lattice 2^(k + RELIEF_SHIFT) wide). |
| 349 | `private volatile boolean built` |  |
| 350 | `private double[] cellLand, cellForest` |  |
| 352 | `private float[][] cellFields` | Each resource's field land a cell, by Resource's ordinal (null for forest): cellFieldLand(). |
| 353 | `private long siteX, siteY` |  |
| 354 | `private int siteTests` |  |
| 355 | `private double cellsMs` |  |
| 384 | `private static volatile double[] quantile` |  |
| 447 | `EarthData data` |  |
| 448 | `EarthData.Layer layer` |  |
| 449 | `int cpd, cols` |  |
| 450 | `boolean tiles` |  |
| 451 | `final int[] ids` |  |
| 452 | `final byte[][] bytes` |  |
| 453 | `final int[] uniform` |  |
| 454 | `int next` |  |
| 603 | `final double[] coast` |  |
| 604 | `final double[] ice` |  |
| 605 | `final double[] lat` |  |
| 606 | `final double[] seaL` |  |
| 607 | `final double[] iceL` |  |
| 609 | `final double[] shalL` | The shallow sea's share of the area at each lattice point (ET1): over seaL, the shallow share of the sea there. |
| 610 | `final double[] shal` |  |
| 612 | `final double[] rel` | The relief's noise over a tile (ET1), and the slope shares at the lattice's points: at or above foothill, steep, very steep. |
| 613 | `final double[] sh` |  |
| 614 | `final boolean[] sea` |  |
| 615 | `final double[] ll` |  |
| 616 | `int[] segs` |  |
| 617 | `final Reader seaR` |  |
| 618 | `EarthData bound` |  |
| 620 | `boolean shallow` | Whether the data bound has earth-2's layers (EarthData.hasRelief()): an earth-1 file draws no SHALLOW and no relief. |
| 622 | `FarShares far` | The far shares of the bound data's slope (FarShares), the last 1-degree tile read from them and its number: one thread's, so a far view's neighbouring pixels take the lock once a tile. |
| 623 | `int farTile` |  |
| 624 | `byte[] farBlocks` |  |
| 899 | `private final Map<Long, byte[]> tiles` |  |
| 1122 | `private final Map<Long, byte[]> reliefTiles` |  |
| 1246 | `private final Map<Long, Integer> resolved` |  |
| 1248 | `private final Map<Long, int[]> landmassTiles` |  |
| 1578 | `private final byte[][] tiles` |  |
| 1640 | `double[] ax` |  |
| 1641 | `double[] minX` |  |
| 1642 | `double[] lx` |  |
| 1643 | `int segs, lakes` |  |
| 1672 | `private final Map<Integer, Water> dataTiles` |  |
| 1678 | `private final Map<Long, Water> districts` |  |
| 1841 | `private volatile int[] widestRun` | Each 1-degree tile's widest river run, by its median width in metres, once a far node has read it: -1 until then. |
| 2122 | `final int w, h, stride` |  |
| 2123 | `final double ox, oy, step, unitM` |  |
| 2125 | `final double wrap` | Units round the planet across, where the camera's x wraps (the grid's columns at the date line); 0 where it does not. |
| 2192 | `private final long x0, y0` |  |
| 2193 | `private final long ds` |  |
| 2194 | `private final double rim` |  |
| 2195 | `private final double[] corners` |  |
| 2196 | `private boolean blended` |  |
| 2197 | `private int tile` |  |
| 2198 | `private EarthProj.Blend blend` |  |
| 2199 | `private int blendX` |  |
| 2299 | `private final double perDeg` |  |
| 2300 | `private final int level` |  |
| 2357 | `private final EarthProj.Ortho view` |  |
| 2358 | `private final double viewW, viewH, m` |  |
| 2359 | `private final int level` |  |
| 2618 | `private final Map<Long, Double> depths` |  |
| 2751 | `final EarthData.Layer farLand` |  |
| 2752 | `final int fcpd` |  |
| 2753 | `final byte[][] landBands` |  |
| 2754 | `final int[] landUniform` |  |
| 2755 | `final boolean[] bandRead` |  |
| 2756 | `int dry, wet` |  |
| 2778 | `private final Geology geo` |  |
| 2779 | `private final double[][] mult` |  |
| 2780 | `private final boolean[] seaOil` |  |
| 2781 | `private final int oil` |  |
| 2803 | `private volatile PointWeights pointWeights` |  |
| 2916 | `private volatile long[] site` |  |
| 2969 | `private volatile long[] snapGround, beyond` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 138 | 2911 | **type** `public final class EarthTerrain` | The real Earth's ground on the world's plots: a tile, a plot, a line or a region at a time, each world cell's land and forest (the cells pass), the founding site snapped to dry ground, and a point's sea - spec-earth 3... |

### THE DIALS (lines 140-296)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 17 | `static { ... }` |  |

### THE DATA A KEY NAMES (lines 297-333)

| line | len | member | says |
|---:|---:|---|---|
| 305 | 5 | `public static String use(EarthData data)` | Makes a reader of Earth's data available to the worlds whose keys name it: returns its identity, the key's `data`. |
| 312 | 21 | `static EarthData source(String identity)` | The reader a key's data names: one use() made available, else the file EarthData.locate() finds when its identity is that. |

### ONE EARTH WORLD'S STATE (lines 334-379)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 12 | `EarthTerrain(World.Key key)` | The ground of an Earth world's key; the data it names is found here (source()). |
| 372 | 1 | `public World.Key key()` | Its key. |
| 375 | 1 | `public EarthProj projection()` | The projection it is laid through, centred on the key's spot. |
| 378 | 1 | `public EarthData data()` | The data it is drawn from. |

### THE RANKING TABLE (spec-earth 3's QUANTILE) (lines 380-440)

| line | len | member | says |
|---:|---:|---|---|
| 387 | 19 | `static double[] quantile()` | The noise's distribution: QUANTILE_SAMPLES values of it at plot centres hashed from QUANTILE_SALT, sorted - built once, the same everywhere. |
| 408 | 5 | `static double below(double p)` | The noise value under which share p of its samples lie: minus infinity at p of nought or less, plus infinity at one or more. |
| 415 | 5 | `private static double below(double[] q, double p)` | ...from a table in hand. |
| 422 | 3 | `static boolean sea(double share, double coastNoise)` | Whether a plot is sea (spec-earth 3): its sea share plus COAST_FRAY x its coast noise about the noise's mean, 0.5, reaches SEA_LEVEL - the share's half-contour, frayed. |
| 427 | 3 | `static boolean covered(double share, double noise)` | Whether a plot is ice, or desert (0.7.127, batch E7; spec-globe 3.9): its kind's bit interpolated there plus COVER_FRAY x its own noise about the noise's mean reaches COVER_LEVEL - the bit's half-contour, frayed as th... |
| 432 | 8 | `static double noiseAt(long[] os, double x, double y, double stride)` | One field's noise at a point in plots, its octaves no wider than `stride` at their mean (World.regionTerrain()'s rule): at a stride of one, every octave. |

### READING THE DATA: a layer's cells, a few tiles kept in hand (lines 441-656)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 152 | **type** `private static final class Reader` | One share layer read cell by cell, the last few of its tiles (or bands) in hand: one thread's. |
| 456 | 9 | `void bind(EarthData d, String name)` _(in EarthTerrain.Reader)_ |  |
| 467 | 12 | `int value(long r, long c)` _(in EarthTerrain.Reader)_ | The value of the cell in global row r (from 90 N) and column c (from 180 W), rows clamped to the globe and columns wrapped round it. |
| 480 | 14 | `private int slot(int id)` _(in EarthTerrain.Reader)_ |  |
| 496 | 8 | `double bilinear(double lat, double lon)` _(in EarthTerrain.Reader)_ | The layer's value at (lat, lon) interpolated between the four cell centres about it, 0 to 255 (bilinear; columns wrap at the date line). |
| 515 | 8 | `double bilinearProduct(Reader sea, double lat, double lon)` _(in EarthTerrain.Reader)_ | ...the product of this layer's level and `sea`'s at each cell, both 0 to 15, interpolated the same way, so 0 to 15 x 15 (ET1): the shallow sea's share of a cell's AREA when this is the shallow layer and `sea` the sea ... |
| 537 | 25 | `void shares(Reader sea, Reader lake, double lat, double lon, double[] out)` _(in EarthTerrain.Reader)_ | ...the slope layer's (ET1, spec-earth2-terrain 3.1): the shares of the LAND at a point at or above foothill, steep and very steep into out[0..2], 0 to 1, each interpolated between the four cell centres about it WEIGHT... |
| 571 | 13 | `void cover(double lat, double lon, double[] out)` _(in EarthTerrain.Reader)_ | ...land cover's: the forest level, 0 to 15, interpolated the same way, and (0.7.127, batch E7) its ice and desert bits each read 0 or 1 at its cell and interpolated from the same four cells: out[0] the level, out[1] i... |
| 586 | 6 | `private static double bit(int a, int b, int d, int e, int bit, double fr, double fc)` _(in EarthTerrain.Reader)_ | A bit of four cells' values, each 0 or 1, interpolated between their centres. |
| 594 | 3 | `int at(double lat, double lon)` _(in EarthTerrain.Reader)_ | The value of the cell (lat, lon) lies in: a cell holds its north and west edges (EarthData's WHERE A POINT FALLS). |
| 600 | 47 | **type** `private static final class Scratch` | One thread's working arrays and readers. |
| 626 | 20 | `void bind(EarthData d)` _(in EarthTerrain.Scratch)_ |  |
| 651 | 5 | `private Scratch scratch()` |  |

### A TILE, OR A LINE OF ONE (lines 657-894)

| line | len | member | says |
|---:|---:|---|---|
| 666 | 3 | `public void tileTerrain(long tx, long ty, byte[] out)` | One tile's ground, a byte a plot in rows (out[py x 32 + px]): spec-earth 3's rule (the class header). |
| 677 | 150 | `private void draw(long tx, long ty, int ox, int oy, int w, int h, byte[] out)` | Plots (ox, oy) to (ox + w - 1, oy + h - 1) of tile (tx, ty), row by row (out[(py - oy) x w + px - ox]): each the tile's, byte for byte - the same lattice points from the same corners, the same shares, octaves and sums... |
| 834 | 4 | `private static double shallowShare(Scratch s, int px, int py)` | The shallow share of the sea at the tile's plot (px, py) - -1 to 32 (ET1): the lattice's blend of the shallow sea's area over its blend of the sea's, so the share is weighted by the sea share (no fringe of deep sea al... |
| 840 | 8 | `private static double lattice(double[] at, int px, int py)` | A share at the tile's plot (px, py) - -1 to 32, the ring included - blended between the lattice's points about it. |
| 850 | 4 | `private static boolean beach(Scratch s, EarthProj.Blend b, double x, double y)` | Whether the 2' cell under the point (plot coordinates) has bare ground on 2% of its land or more (EarthData.LC_BEACH). |
| 856 | 20 | `private static void octave(double[] lat, long os, int k, long gx0, long gy0, int w, int h, double amp, double[] acc)` | Adds `amp` x octave k's value noise at the plot centres of a w x h rectangle from plot (gx0, gy0), row by row: its lattice hashed once, the smoothstep from SMOOTH (World.octaveTile()'s way) - a plot's value the same w... |
| 878 | 7 | `private static boolean onRiver(Water w, int k, double x, double y)` | Whether a point lies within a river segment's half-width, interpolated along it. |
| 887 | 7 | `private static boolean inLake(Water w, int k, double x, double y, double n, double[] q)` | Whether a point is in small lake k's blob, its blob noise there `n` (a stride's noise, at a region's pixel). |

### A PLOT, A LINE, A REGION AND A POINT'S SEA (lines 895-1071)

| line | len | member | says |
|---:|---:|---|---|
| 906 | 11 | `byte[] keptTile(long tx, long ty)` | A tile's ground, kept (TILES_KEPT): shared, do not write into it. |
| 919 | 5 | `public byte terrainAt(long x, long y)` | One plot's ground: its tile's (tileTerrain()), from a kept tile; sea off the world. |
| 926 | 14 | `public void lineTerrain(long tx, long ty, boolean column, int at, byte[] out)` | One row (or, `column`, one column) of a tile's ground, its `at`-th: the tile's, byte for byte (World.lineTerrain()'s contract) - from the tile if it is kept, else the line drawn alone (draw()). |
| 949 | 17 | `public void regionTerrain(long x0, long y0, int stride, int n, byte[] out)` | A region's ground at a stride (World.regionTerrain()'s contract): n x n pixels from plot (x0, y0), each `stride` plots wide. |
| 968 | 3 | `private byte pointClass(Scratch s, double[] q, double x, double y, int stride)` | A point's class at a stride: see regionTerrain(). |
| 978 | 6 | `private byte pointClass(Scratch s, double[] q, double x, double y, int stride, boolean water, double dither)` | ...with the rivers and small lakes drawn (`water`) or left to the caller, and the forest ranked through its noise or, where `dither` is a number (0 to 1), drawn where it is under the forest's share - the far node's (f... |
| 986 | 40 | `private byte classAt(Scratch s, double[] q, double x, double y, double lat, double lon, int stride, boolean water, double dither)` | ...at a point on the planet whose latitude and longitude are known. |
| 1034 | 3 | `byte pointTerrain(long x, long y)` | A plot's class by the point rule (classAt(), the regions' and the far views') at its centre, at a stride of one, rather than its tile's (draw()): the rule's two copies, which agree but for the lattice's blend of the s... |
| 1049 | 6 | `private boolean shallowAt(Scratch s, double[] q, double x, double y, double lat, double lon, double sea, int stride)` | Whether a point of the sea is SHALLOW (ET1), the tile rule's other copy (draw()): the shallow share of the sea there, interpolated between the 15" cells' centres weighted by each cell's sea (Reader.bilinearProduct() o... |
| 1063 | 8 | `public boolean seaAt(double x, double y)` | Whether a point (plot coordinates) is sea, by the coast's rule at the point itself: the sea share there, read through the exact projection, and the coast noise at every octave - what a field drawn off the sea asks (Wo... |

### THE RELIEF: a second plane beside the cover (ET1, spec-earth2-terrain 3.1) (lines 1072-1215)

| line | len | member | says |
|---:|---:|---|---|
| 1129 | 11 | `byte[] keptRelief(long tx, long ty)` | A tile's relief, kept (RELIEF_KEPT): shared, do not write into it. |
| 1142 | 5 | `public byte reliefAt(long x, long y)` | One plot's relief: its tile's (tileRelief()), from a kept tile; FLAT off the world, and for data with no slope layer. |
| 1154 | 8 | `public void tileRelief(long tx, long ty, byte[] out)` | One tile's relief, a byte a plot in rows (out[py x 32 + px], as tileTerrain()'s cover): the rule above, on the plots whose cover tileTerrain() draws dry. |
| 1168 | 47 | `private void drawRelief(long tx, long ty, byte[] cover, byte[] out)` | A tile's relief given its cover (the rule's tile copy): FLAT but on dry plots, and on those the nested classes over the relief noise. |

### THE LANDMASS OF A PLOT (ET5, 2026-10-25; the ET5 design, D2 to D4) (lines 1216-1633)

| line | len | member | says |
|---:|---:|---|---|
| 1255 | 1 | `public boolean hasLandmasses()` | Whether the data has the landmass layer (EarthData.hasRelief()): without it every plot's landmass is 0. |
| 1258 | 1 | `static boolean onLand(byte cover)` | Whether a plot of this cover class has a landmass: dry ground and ice, never water. |
| 1265 | 40 | `int resolvedCell(long g, long c)` | The landmass a plot of the layer's cell (row g, column c) belongs to when it is dry: the cell's own id, or - for a cell with no land - the nearest land cell's within LANDMASS_REACH (least dr squared + dc squared, the ... |
| 1307 | 11 | `int[] keptLandmass(long tx, long ty)` | A tile's landmass, kept (RELIEF_KEPT): shared, do not write into it. |
| 1320 | 28 | `private void drawLandmass(long tx, long ty, byte[] cover, int[] out)` | A tile's landmass given its cover: 0 on water and where the layer places nothing (see the section above), the plot's landmass on the rest. |
| 1353 | 8 | `public void tileLandmass(long tx, long ty, int[] out)` | One tile's landmass, an id a plot in rows (out[py x 32 + px], as tileTerrain()'s cover): 0 on water, off the world's square and for data with no landmass layer. |
| 1363 | 5 | `public int landmassAt(long x, long y)` | One plot's landmass: its tile's (tileLandmass()), from a kept tile; 0 off the world and for data with no landmass layer. |
| 1375 | 9 | `public int landmassOfPlot(long x, long y)` | A plot's landmass by its 15" cell alone, whether the plot is dry or water: the cell's own id, or the nearest land cell's within LANDMASS_REACH (resolvedCell(), the rule landmassAt() gives a dry plot), read at the plot... |
| 1391 | 55 | `int[] idsNear(long tx, long ty)` | The landmass ids a tile's plots can take, the non-zero ones ascending: those of the land cells under the tile's blend box widened by LANDMASS_REACH cells every way. |
| 1456 | 20 | `private byte reliefOf(Scratch s, double[] q, double x, double y, double lat, double lon, int stride, double dither)` | A point's relief at a stride, the rule's point copy (the regions' and the far views'): the shares at the point (Reader.shares(), a 15" cell's own land-weighted blend; from FAR_STRIDE the 4' block's - FarShares) agains... |
| 1482 | 7 | `byte pointRelief(long x, long y, byte cover)` | A plot's relief by the point rule at its centre, at a stride of one, given its cover: the rule's other copy (drawRelief() is the tile's) for a plot of dry cover, FLAT for any other. |
| 1496 | 8 | `double[] reliefShares(double lat, double lon, boolean far)` | The shares the tile and point rules draw against at a point (ET1): of the land there, at or above foothill, at or above steep and very steep (Reader.shares(): the four 15" cells' about it, weighted by each cell's land... |
| 1513 | 27 | `public void regionRelief(long x0, long y0, int stride, int n, byte[] out)` | A region's relief at a stride (World.regionRelief()'s contract), as regionTerrain() lays its cover: n x n pixels from plot (x0, y0), each `stride` plots wide, FLAT where the pixel's cover is not dry ground. |
| 1542 | 21 | `private void farShares(Scratch s, double lat, double lon, double[] out)` | The far shares of the slope at a point into out[0..2] (the 4' block it lies in, FarShares), the last tile read kept in hand. |
| 1574 | 59 | **type** `private static final class FarShares` | The slope's shares summarised for the far views (ET1): per 1-degree tile, built the first time a far pixel lies in it and kept, the land-weighted shares at or above foothill, steep and very steep of each FAR_BLOCK x F... |
| 1580 | 3 | `static FarShares of(EarthData d)` _(in EarthTerrain.FarShares)_ |  |
| 1585 | 11 | `byte[] blocks(EarthData d, int t)` _(in EarthTerrain.FarShares)_ | Tile t's blocks, three planes of (cpd / FAR_BLOCK)^2; null where the tile has no slope to speak of. |
| 1597 | 35 | `private static byte[] build(EarthData d, int t)` _(in EarthTerrain.FarShares)_ |  |

### RIVERS AND SMALL LAKES, PROJECTED: a 1-degree tile's, and a district's (lines 1634-1778)

| line | len | member | says |
|---:|---:|---|---|
| 1639 | 29 | **type** `private static final class Water` | Rivers' segments and small lakes in plots: each segment's ends, half-widths and box widened by them, each lake's centre and radius. |
| 1645 | 13 | `void addSeg(double ax0, double ay0, double bx0, double by0, double h0, double h1)` _(in EarthTerrain.Water)_ |  |
| 1659 | 8 | `void addLake(double x, double y, double r)` _(in EarthTerrain.Water)_ |  |
| 1685 | 27 | `private Water dataTile(int tile)` | A 1-degree tile's rivers and small lakes through the projection, kept: every run's segments as stored (one crossing the tile's edge is in each tile it touches), each half-width at least RIVER_MIN_HALF. |
| 1714 | 1 | `private static double halfWidth(int widthM)` | A river's half-width in plots from its width in metres: at least RIVER_MIN_HALF. |
| 1717 | 10 | `private Water water(long dx, long dy)` | A district's rivers and small lakes, kept: those of the 1-degree tiles within WATER_PAD_DEG of it whose reach touches it. |
| 1728 | 50 | `private Water districtWater(long dx, long dy)` |  |

### THE FAR VIEW'S NODES (0.7.119, batch E5; spec-earth 3, "Far views") (lines 1779-2097)

| line | len | member | says |
|---:|---:|---|---|
| 1862 | 3 | `public void farTerrain(long x0, long y0, int stride, int n, byte[] out)` | A far node's ground (spec-earth 3, "Far views"; MapTiles.nodeTerrain()): n x n pixels from plot (x0, y0), `stride` plots each. |
| 1872 | 8 | `public void farTerrain(long x0, long y0, int stride, int n, byte[] out, byte[] relief)` | ...and, where `relief` is not null, its relief beside it (ET1, spec-earth2-terrain 3.1): n x n bytes laid as `out` is, FLAT where the pixel's cover is not dry ground - the cover is the same byte for byte as without it. |
| 1891 | 3 | `public void farTerrain(Camera c, byte[] out)` | The far rule through a camera (0.7.132, batch E8; spec-globe 3.5): the camera's w x h pixels, each classed at its centre on the planet as a far node's are (classAt(), the camera's stride picking the layers and the oct... |
| 1903 | 29 | `public void farTerrain(Camera c, byte[] out, byte[] relief)` | ...and, where `relief` is not null, the relief of its pixels beside the cover (ET1, spec-earth2-terrain 3.1; w x h bytes laid as `out` is): on each pixel whose cover is dry ground, once the rivers and small lakes are ... |
| 1937 | 67 | `private void strokeWater(Camera c, byte[] out, double[] q)` | The rivers and small lakes over a camera's pixels (farTerrain()): fresh water on every pixel that is not sea whose centre a river's stroke or a small lake's blob covers. |
| 2006 | 5 | `private static double unwrapped(double v, double ref, double wrap)` | A wrapped coordinate moved by whole turns to within half a turn of a reference: a river's next vertex across the grid's date line. |
| 2013 | 11 | `private boolean tileCorners(int t, double[] c, double rim)` | Tile t's four corners in plots into c (south-west, south-east, north-west, north-east, x then y), projected exactly: false - the tile's points then each projected - where the tile reaches past EarthProj.BLEND_MAX_LAT ... |
| 2026 | 8 | `private boolean place(double lat, double lon, boolean blended, double[] c, int t, double[] out)` | A point's plot in tile t: bilinear between the tile's corners where `blended` (tileCorners()), else projected; false off the planet. |
| 2036 | 4 | `private static boolean nearRim(double[] p, double rim)` | Whether a point in plots lies within RIM_M of the rim: `rim` metres or more from the spot. |
| 2048 | 19 | `private static void stroke(double ax, double ay, double bx, double by, double ha, double hb, Camera c, double xs, byte[] out)` | One river segment stroked onto a camera's pixels: every pixel not sea whose centre lies within the segment's half-width there, interpolated along it (onRiver()'s rule) - the camera's x units xs of its y units on the g... |
| 2069 | 16 | `private void blob(double lx, double ly, double r, Camera c, double xs, byte[] out, double[] q)` | One small lake drawn onto a camera's pixels: every pixel not sea whose centre its blob holds - inLake()'s rule, its noise at the camera's stride (at DITHER_STRIDE and up, at its mean: the lake's disc) - x scaled as st... |
| 2087 | 4 | `private static int first(double v, double o, double step, int n)` | The first pixel of a run (from `o`, `step` units each, n of them) whose centre is at `v` or past it, clamped to the run: n when none is. |
| 2093 | 4 | `private static int last(double v, double o, double step, int n)` | ...and the last whose centre is at `v` or before it: -1 when none is. |

### the camera: where the far rule's pixels lie on the planet (0.7.132, batch E8; spec-globe 3.5) (lines 2098-2561)

| line | len | member | says |
|---:|---:|---|---|
| 2121 | 63 | **type** `public abstract static class Camera` | Where the far rule's pixels lie on the planet, and where a river's vertex lands among them (0.7.132, batch E8; docs/studies/spec-globe.md 3.5). |
| 2127 | 10 | `Camera(int w, int h, int stride, double ox, double oy, double step, double unitM, double wrap)` _(in EarthTerrain.Camera)_ |  |
| 2139 | 1 | `public int width()` _(in EarthTerrain.Camera)_ | Its pixels across and down. |
| 2140 | 1 | `public int height()` _(in EarthTerrain.Camera)_ |  |
| 2143 | 1 | `public int stride()` _(in EarthTerrain.Camera)_ | The rule's stride: a pixel's ground, in plots. |
| 2146 | 1 | `public double pixelM()` _(in EarthTerrain.Camera)_ | Metres of ground a pixel stands for, down. |
| 2149 | 1 | `abstract boolean latLon(int px, int py, double[] ll)` _(in EarthTerrain.Camera)_ | Pixel (px, py)'s centre on the planet into ll; false off the planet (or off the plane's square), where it is drawn as sea. |
| 2152 | 1 | `abstract double x(int px)` _(in EarthTerrain.Camera)_ | Where pixel column px and row py stand for the rule's noise, in plots. |
| 2153 | 1 | `abstract double y(int py)` _(in EarthTerrain.Camera)_ |  |
| 2156 | 1 | `abstract double dither(int px, int py, double lat, double lon)` _(in EarthTerrain.Camera)_ | The dither at pixel (px, py), whose centre is (lat, lon): a number in [0, 1), or NaN where the forest is ranked through its noise. |
| 2159 | 1 | `abstract int[] tiles()` _(in EarthTerrain.Camera)_ | The 1-degree tiles whose rivers and small lakes may reach its pixels. |
| 2162 | 1 | `void tile(int t)` _(in EarthTerrain.Camera)_ | Ready to place tile t's vertices and small lakes. |
| 2165 | 1 | `abstract boolean place(double lat, double lon, double[] xy, boolean river)` _(in EarthTerrain.Camera)_ | A point of tile t's - a river's vertex (`river`) or a small lake's - in the camera's units into xy; false where it is not drawn. |
| 2168 | 1 | `double xScale(double lat)` _(in EarthTerrain.Camera)_ | A unit's ground across over a unit's ground down, at a latitude: 1 but on the grid. |
| 2174 | 9 | `double[] shifts(double x0, double x1)` _(in EarthTerrain.Camera)_ | The whole turns a shape spanning x0 to x1 is drawn at: none but where x wraps (the grid's). |
| 2186 | 3 | `public Camera plane(long x0, long y0, int stride, int n)` | The city's plane's camera: a far node of n x n pixels from plot (x0, y0), stride plots each (MapTiles.nodeTerrain()). |
| 2191 | 50 | **type** `private final class Plane extends Camera` | The city's plane: the far node as it was drawn before the camera, the same arithmetic in the same order. |
| 2201 | 6 | `Plane(long x0, long y0, int stride, int n)` _(in EarthTerrain.Plane)_ |  |
| 2208 | 1 | `double x(int px)` _(in EarthTerrain.Plane)_ |  |
| 2209 | 1 | `double y(int py)` _(in EarthTerrain.Plane)_ |  |
| 2211 | 12 | `boolean latLon(int px, int py, double[] ll)` _(in EarthTerrain.Plane)_ |  |
| 2224 | 5 | `double dither(int px, int py, double lat, double lon)` _(in EarthTerrain.Plane)_ |  |
| 2230 | 1 | `int[] tiles()` _(in EarthTerrain.Plane)_ |  |
| 2232 | 4 | `void tile(int t)` _(in EarthTerrain.Plane)_ |  |
| 2237 | 3 | `boolean place(double lat, double lon, double[] xy, boolean river)` _(in EarthTerrain.Plane)_ |  |
| 2256 | 3 | `static { ... }` |  |
| 2264 | 3 | `public static int ditherLevel(double degrees)` | The dither level of a pixel `degrees` of latitude tall: the least whose cell, GLOBE_DITHER_DEG x 2^level, is no shorter than the pixel. |
| 2269 | 13 | `public static double globeDither(int level, double lat, double lon)` | The globe's dither at (lat, lon) at a level: its cell's hash, a number in [0, 1) - a function of the point and the level alone, not of the view, the city or its seed. |
| 2287 | 3 | `public Camera grid(int arcmin, int row0, int rows)` | A latitude-longitude grid's camera: rows row0 to row0 + rows - 1 of the planet at `arcmin` minutes a texel, every column (360 x 60 / arcmin), row 0 at the north pole - the planet's picture (GlobePicture). |
| 2298 | 45 | **type** `private final class Grid extends Camera` | The planet on a latitude-longitude grid: a pixel a texel, its centre the texel's; the rule's stride at least FAR_STRIDE, so each pixel reads the 4' far layers at its centre - at 4' its own cell of them (★E8); a vertex... |
| 2302 | 7 | `Grid(int arcmin, int row0, int rows)` _(in EarthTerrain.Grid)_ |  |
| 2310 | 1 | `double x(int px)` _(in EarthTerrain.Grid)_ |  |
| 2311 | 1 | `double y(int py)` _(in EarthTerrain.Grid)_ |  |
| 2313 | 5 | `boolean latLon(int px, int py, double[] ll)` _(in EarthTerrain.Grid)_ |  |
| 2319 | 1 | `double dither(int px, int py, double lat, double lon)` _(in EarthTerrain.Grid)_ |  |
| 2321 | 13 | `int[] tiles()` _(in EarthTerrain.Grid)_ |  |
| 2335 | 5 | `boolean place(double lat, double lon, double[] xy, boolean river)` _(in EarthTerrain.Grid)_ |  |
| 2341 | 1 | `double xScale(double lat)` _(in EarthTerrain.Grid)_ |  |
| 2351 | 3 | `public Camera globe(EarthProj.Ortho view, double kmPx, double viewW, double viewH, int sx0, int sy0, int w, int h)` | The globe's camera: pixels sx0 to sx0 + w - 1 across and sy0 to sy0 + h - 1 down of a view viewW x viewH pixels at kmPx km a pixel, orthographic round its centre with its roll (EarthProj.Ortho) - GlobeFrame's GROUND t... |
| 2356 | 33 | **type** `private final class Globe extends Camera` | The globe round a view centre with a roll: a pixel's centre through the orthographic inverse, a vertex through the forward (hidden on the far side), the forest dithered by globeDither() at the pixel's scale. |
| 2361 | 8 | `Globe(EarthProj.Ortho view, double kmPx, double viewW, double viewH, int sx0, int sy0, int w, int h)` _(in EarthTerrain.Globe)_ |  |
| 2370 | 1 | `double x(int px)` _(in EarthTerrain.Globe)_ |  |
| 2371 | 1 | `double y(int py)` _(in EarthTerrain.Globe)_ |  |
| 2373 | 4 | `boolean latLon(int px, int py, double[] ll)` _(in EarthTerrain.Globe)_ |  |
| 2378 | 1 | `double dither(int px, int py, double lat, double lon)` _(in EarthTerrain.Globe)_ |  |
| 2380 | 1 | `int[] tiles()` _(in EarthTerrain.Globe)_ |  |
| 2382 | 6 | `boolean place(double lat, double lon, double[] xy, boolean river)` _(in EarthTerrain.Globe)_ |  |
| 2398 | 60 | `private int[] globeTiles(EarthProj.Ortho view, double m, double viewW, double viewH, double sx0, double sy0, int w, int h)` | The 1-degree tiles whose rivers and small lakes may reach a piece of the globe's screen: (NODE_EDGE_POINTS + 1)^2 points over the piece - those past the disc taken at its limb - give a box of latitude and its arc of l... |
| 2460 | 4 | `private static boolean inPiece(double[] xy, double m, double viewW, double viewH, double sx0, double sy0, int w, int h)` | Whether a point of the view's plane (metres) lies on the piece's pixels. |
| 2476 | 56 | `int[] nodeTiles(double x0, double y0, double side)` | The 1-degree tiles whose rivers and small lakes may reach a box of plots [x0, x0 + side)^2. |
| 2543 | 18 | `private boolean footprint(int t, double x0, double y0, double side)` | Whether 1-degree tile t, widened by WATER_PAD_DEG (the reach of its widest river past its edge), may reach a box of plots: its FOOTPRINT_POINTS x FOOTPRINT_POINTS points projected, their box widened by an eighth of it... |

### THE CELLS PASS AND THE SNAP (the world's build) (lines 2562-2576)

| line | len | member | says |
|---:|---:|---|---|
| 2567 | 9 | `void build()` | The cells pass and the snap, once (World.build() asks). |

### THE SEA'S DEPTH (0.7.121, batch E4; ET1, 2026-10-24: spec-earth2-terrain 7) (lines 2577-2735)

| line | len | member | says |
|---:|---:|---|---|
| 2633 | 13 | `public double depthAt(long x, long y)` | The sea's depth at a plot's centre, metres below its level (ET1, the study's 7): 0 where the plot is not the sea's (World.salt()), else the depth layer's metres there (EarthData.depthM()), at least SEA_LEAST_DEPTH_M, ... |
| 2652 | 4 | `public double depthOf(double lat, double lon)` | The depth layer's metres at a point of the planet (ET1 slice D; the globe's hover, which has a point and no plot): what depthAt() gives the plot there if it is sea - at least SEA_LEAST_DEPTH_M - whether or not the poi... |
| 2681 | 54 | `public void cellsPass()` | Each world cell's land and forest, as shares of its area (spec-earth 4): CELL_SAMPLES x CELL_SAMPLES points a cell, each read from the 4' far layers - its land the ground that is not sea, dry and lake (the generated w... |

### FIELD LAND AT A POINT (0.8.12, batch EG1; spec-earth2-terrain 8.5) (lines 2736-3048)

| line | len | member | says |
|---:|---:|---|---|
| 2750 | 25 | **type** `private final class FarReader` | Reads the 4' far layers at a point, keeping the bands it has inflated (a band of a layer is a row of degrees all round the planet): the cells pass's reader, and pointFieldWeights()'s - dry and wet, 0 to FAR_LEVELS each. |
| 2759 | 15 | `void read(double lat, double lon)` _(in EarthTerrain.FarReader)_ | The point's dry land and its lakes, in the layers' levels, left in dry and wet. |
| 2777 | 25 | **type** `private static final class PointWeights` | What a point contributes to each resource's field land: its land share (and oil's sea share on a province whose sea holds oil) times the multiplier there. |
| 2783 | 12 | `PointWeights(Geology geo)` _(in EarthTerrain.PointWeights)_ |  |
| 2797 | 4 | `double of(int k, int row, double lat, double lon, double pl, int levels)` _(in EarthTerrain.PointWeights)_ | Resource k's weight at a point in province `row`: `pl` the point's land share, `levels` its dry and lake levels added (FAR_LEVELS less them is the sea). |
| 2805 | 10 | `private PointWeights pointWeights()` |  |
| 2825 | 19 | `double[] pointFieldWeights(int cell, Resource r)` | The weights a cell's CELL_SAMPLES x CELL_SAMPLES points carry for a resource (EG1), in the cells pass's order - rows from the north, each from the west, j x CELL_SAMPLES + i - each the point's land (oil's with a sea t... |
| 2853 | 9 | `boolean fieldGround(Resource r, double x, double y)` | Whether a point (plot coordinates) is ground a resource's fields lie on (EG1): the province under it (Geology.provinceAt(), the patches in) gives the resource a multiplier above nought there (Geology.multiplierAt(), a... |
| 2864 | 1 | `public Geology geology()` | The geology of its data (Geology.of()): the provinces, their classes and multipliers. |
| 2873 | 5 | `double cellFieldLand(int cell, Resource r)` | A cell's FIELD LAND for a resource, as a share of its area (0.7.117): its 16 points' land each times the multiplier there (Geology.multiplier(), and since EG1 iron's at the point, Geology.multiplierAt()), oil's with a... |
| 2880 | 5 | `public int provinceAt(double x, double y)` | The province under a point in plot coordinates, its row in the data; 0 off the planet or in none. |
| 2887 | 1 | `public double cellsPassMs()` | How long the cells pass took, in ms (the last time it ran). |
| 2890 | 1 | `double cellLand(int cell)` | A cell's land, as a share of its area: the ground that is not sea (World.cellLandShare()). |
| 2893 | 1 | `double cellForest(int cell)` | A cell's forest, as a share of its area. |
| 2908 | 7 | `private void snap()` | THE SNAP (spec-earth 2): from the spot's plot - the projection's centre is the corner of four, and this is the one south-east of it, (SIDE/2, SIDE/2) - the nearest plot that passes the founding's test 1, buildable gro... |
| 2924 | 27 | `public long[] siteNear()` | The snap's answer without the cells pass (0.7.121, batch E4: the founding card's first question, and Random spot's test): {x, y, plots tested}, x and y -1 when no plot within SNAP_PLOTS passes - a spot in the sea, or ... |
| 2953 | 9 | `boolean siteBuildable(long px, long py)` | The founding's test 1 at plot (px, py) (ET2: buildable ground, spec-earth2-terrain 3.4): it and eight points World.SITE_DRY_PLOTS round its centre are ground a building may stand on - dry, and flat or foothill (World.... |
| 2964 | 4 | `private boolean buildable(long x, long y)` | Whether plot (x, y) is ground a building may stand on: its cover is dry, and its relief - read only where it is - flat or foothill. |
| 2976 | 24 | `long[] snapGround()` | What the snap's disc holds when it found no site (ET2): {dry plots, and of them the steep and very steep} within SNAP_PLOTS of the spot, counted plot by plot from the tiles drawn for them - what tells a spot among ste... |
| 3008 | 28 | `long[] siteBeyond()` | The nearest buildable ground past a snap that found none (ET2, spec-earth2-terrain 3.4): {x, y} of the nearest spot that passes the founding's test 1 (siteBuildable()) on a lattice of BEYOND_STEP plots from the spot, ... |
| 3038 | 1 | `long siteX()` | The founding site, as a plot: the snap's. |
| 3041 | 1 | `long siteY()` | ...and its row. |
| 3044 | 1 | `int siteTests()` | How many plots the snap tested. |
| 3047 | 1 | `public static long spotPlot()` | The plot the snap starts from: the spot's, (SIDE/2, SIDE/2). |

