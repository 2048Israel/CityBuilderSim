# World.java - 1,129 lines · 59 methods · 78 constants · model

`ham/citybuildersim/World.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and river, and the fields of the seven resources - every one of them a pure function of one seed.
> 
> WHY THIS EXISTS (0.7.56, batch J1a; the project's spec-land.md 2.1). The
> land office sold parcels drawn by a generator seeded with the parcel's id:
> a plot had a size, a price and some iron, and no place. Jerus's map mockup
> (city-map.html) painted a city on real ground - a coast, a lake, a river,
> forest, deposits - and asked for the land office to sell that ground. This
> is that ground at the size of a world, so a city can grow from a village to
> ten billion people without reaching an edge. The model reads it since
> 0.7.57: the city's land is a piece of it (CityLand, batch J1b), its lakes
> and river hold the water plants to what they yield (J2), the map paints it
> (J3, J4) and the wells lift its oil (K). At 0.7.56 nothing read it, and the
> default playtest was byte for byte 0.7.55's.
> 
> THE GRID (spec-land star 4). A plot is 30 m; a tile 32 plots (0.96 km); a
> district 256 (7.68 km); a world cell 2,048 (61.44 km) - each nested in the
> next by a power of two. The world is 368 x 368 cells, 511.2 million km2,
> the Earth's 510.1 million within 0.2%. A plot's coordinates run 0 to
> 753,663 east and south from the north-west corner; beyond them is sea.
> 
> THE SAME ON EVERY MACHINE. Every value comes from SplitMix64 (mix(), the
> finaliser LandMarket's scramble uses) of the seed, the field, the octave
> and the lattice point, and from IEEE arithmetic, which Java does the same
> everywhere; every sine, cosine, power, logarithm and exponential is
> StrictMath's, which is bit-for-bit the same on every JVM where Math's may
> differ by an ulp. A world built twice, or on Jerus's PC, is the same world.
> 
> BUILT ON FIRST USE. new World(seed) and World.of(seed) cost nothing; the
> first question asked of a world runs the sea pass (the sea's level from
> four samples a cell, the land and forest in every cell), the founding
> site's search and the river - about half a second - and nothing is stored
> that the seed cannot give back. World.of() keeps the last few worlds asked
> for, so the screens and the land office share one.
> 
> FINER WHERE IT MATTERS. The cells carry the totals and the far view; near
> the city every function is evaluated per plot (terrainAt()), a tile at a
> time (tileTerrain(), each octave's lattice once a tile) or a region at a
> stride (regionTerrain(), the octaves narrower than a pixel replaced by
> their mean), and a cell's fields are drawn only when asked for.

**Uses:** [Resource](Resource.md) (12), [Deposit](Deposit.md) (9)

**Used by (24):** [BuildingVisual](BuildingVisual.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [Deposit](Deposit.md), [Founding](Founding.md), [Game](Game.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [MapCheck](MapCheck.md), [MapFrame](MapFrame.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [ReadPathCheck](ReadPathCheck.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md), [WaterCheck](WaterCheck.md), [WorldCheck](WorldCheck.md)

## Sections

| line | section |
|---:|---|
| 53 | THE GRID |
| 81 | · the terrain's classes |
| 98 | THE TERRAIN: VALUE NOISE IN OCTAVES (spec-land 2.1) |
| 179 | THE FOUNDING SITE (spec-land 2.1) |
| 222 | THE FOUNDING LAKE AND RIVER (the mockup's random walk, in plots) |
| 293 | THE DEPOSITS (spec-land 2.1; the densities and sizes are Resource's) |
| 332 | THE WORLD, ITS STATE |
| 384 | THE NOISE |
| 463 | THE SEA PASS: the sea's level, and each cell's land and forest |
| 518 | THE FOUNDING SITE |
| 656 | THE FOUNDING LAKE AND RIVER |
| 770 | THE TERRAIN, A PLOT, A TILE OR A REGION AT A TIME |
| 979 | THE DEPOSITS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `World.PLOT_M` | `30` | A plot's side, in metres: 30, the map's smallest square and the mockup's cell. |
| 61 | `World.KM2_PER_PLOT` | `PLOT_M * PLOT_M / 1e6` | A plot's area in square kilometres: 30 m squared, 0.0009. |
| 64 | `World.TILE` | `32` | Plots on a tile's side: 32, 0.96 km - the unit the map paints (spec-land 2.6). |
| 67 | `World.DISTRICT` | `256` | Plots on a district's side: 256, 7.68 km - the unit the map stores (spec-land 2.5). |
| 70 | `World.CELL` | `2048` | Plots on a world cell's side: 2,048, 61.44 km - the unit the world's totals and far view are counted in, so plot, tile, district and cell nest by powers of two (spec-land star 4). |
| 73 | `World.CELLS` | `368` | World cells on the world's side: 368, so the world is 511.2 million km2, within 0.2% of the Earth's 510.1 million. |
| 76 | `World.SIDE` | `(long) CELL * CELLS` | Plots on the world's side: 2,048 x 368, 753,664 (22,610 km). |
| 79 | `World.CELL_KM2` | `(CELL * PLOT_M / 1000) *(CELL * PLOT_M / 1000)` | A world cell's area in square kilometres: 61.44 km squared, 3,774.87. |
| 84 | `World.GRASS` | `0` | Open ground: dry and buildable. |
| 87 | `World.FOREST` | `1` | Forest: dry and buildable, and standing timber (Resource.FOREST). |
| 90 | `World.FRESH` | `2` | Fresh water: a lake, or the founding river. |
| 93 | `World.SALT` | `3` | Salt water: the sea, and everything beyond the world's edge. |
| 96 | `World.SAND` | `4` | Beach: dry land within BEACH_BAND of the sea's level. |
| 110 | `World.OCTAVE_FALLOFF` | `0.55` | Each octave's amplitude against the one above it: 0.55, a little rougher than classic fractal noise's half (the mockup's terrain, scaled to plots). |
| 113 | `World.ELEV_COARSE_HI` | `16` | Elevation's coarse octaves run from 2^16 plots (1,966 km): continents and oceans. |
| 116 | `World.ELEV_COARSE_LO` | `9` | ...down to 2^9 plots (15 km): coasts and bays. |
| 119 | `World.ELEV_FINE_HI` | `8` | Elevation's fine octaves run from 2^8 plots (7.7 km)... |
| 122 | `World.ELEV_FINE_LO` | `4` | ...down to 2^4 plots (480 m): headlands, coves and islets. |
| 125 | `World.ELEV_COARSE_WEIGHT` | `0.85` | The coarse octaves' share of the elevation: 0.85, so the coast is the continents' and the fine octaves only fray it. |
| 128 | `World.ELEV_FINE_WEIGHT` | `0.15` | The fine octaves' share: the rest, 0.15. |
| 131 | `World.LAKE_HI` | `10` | The lake field's octaves run from 2^10 plots (30.7 km)... |
| 134 | `World.LAKE_LO` | `5` | ...down to 2^5 plots (960 m). |
| 137 | `World.FOREST_HI` | `8` | The forest field's octaves run from 2^8 plots (7.7 km)... |
| 140 | `World.FOREST_LO` | `5` | ...down to 2^5 plots (960 m). |
| 143 | `World.SEA_SHARE` | `0.71` | The share of the world that is sea: 71%, the Earth's. |
| 155 | `World.SEA_SAMPLES` | `4` | Elevation samples a world cell the sea's level is found from: 4, one at a hashed point in each quarter of the cell (541,696 in all). |
| 158 | `World.LAKE_SHARE` | `0.037` | Lakes' share of land: 3.7%, of the Earth's non-glaciated land (Verpoorter 2014). |
| 168 | `World.LAKE_THETA` | `0.7092` | The lake field's level from which ground is a lake: 0.7092, the 96.3rd percentile of the field over land (LAKE_SHARE) - measured 0.7088 to 0.7095 over five seeds, a million points each, off the lattice; a constant, so... |
| 171 | `World.FOREST_SHARE` | `0.31` | Forest's share of land: 31% (FAO 2020). |
| 174 | `World.FOREST_THETA` | `0.5676` | The forest field's level from which land is forest: 0.5676, the 69th percentile of the field over land (FOREST_SHARE), measured 0.5672 to 0.5679 as LAKE_THETA was (the design's 0.595 makes 24% of land forest). |
| 177 | `World.BEACH_BAND` | `0.0015` | How far above the sea's level, in the elevation field, land is beach: 0.0015. |
| 190 | `World.SITE_CELL_LAND_MIN` | `1` | The least land, in quarters of a cell's SEA_SAMPLES, a cell needs to be searched for a site: 1, 25%. |
| 193 | `World.SITE_CELL_LAND_MAX` | `3` | The most: 3, 75% - a coast, not open sea or an interior. |
| 196 | `World.SITE_STEP` | `17` | The spiral's step: 17 plots (510 m) between rings, and the ring's points about that far apart (six a ring per ring). |
| 199 | `World.SITE_RINGS` | `70` | Rings in a cell's spiral: 70, out to 1,173 plots (35 km), past the cell's own half-width of 1,024. |
| 202 | `World.SITE_DRY_PLOTS` | `20` | Test 1: the site and eight points this far round it are dry ground, in plots: 20 (600 m), room for a new city's 0.28 km2 square (half-side 264 m). |
| 205 | `World.SITE_LAND_SHARE` | `0.6` | Test 2: of SITE_LAND_SAMPLES points within 5 km, at least this share are land: 60%. |
| 208 | `World.SITE_LAND_SAMPLES` | `48` | ...over 48 points, at 1.5, 3 and 5 km in turn round the compass. |
| 211 | `World.SITE_LAND_RADII` | `{ 50, 100, 167 }` | ...at these radii, in plots: 50, 100 and 167 (1.5, 3 and 5 km). |
| 214 | `World.SITE_SEA_NOT_WITHIN` | `45` | Test 3: no sea within this many plots in 16 directions: 45 (1.35 km). |
| 217 | `World.SITE_SEA_WITHIN` | `70` | ...but sea within this many in at least one: 70 (2.1 km) - a coast with room for a town. |
| 220 | `World.SITE_IRON_KM` | `2` | Test 4 (spec-land star): an iron field's centre within this many km - the mockup put deposits near the site "so the first mines come early"; at IRON's 0.2 fields a km2 it passes about 92% of the time. |
| 231 | `World.RIVER_SEA_DIRECTIONS` | `32` | Directions the sea is looked for in, round the site: 32. |
| 234 | `World.RIVER_SEA_REACH` | `100` | ...out to this many plots, from SITE_SEA_NOT_WITHIN: 100 (3 km). |
| 237 | `World.RIVER_SEA_STEP` | `5` | ...in steps of this many plots: 5. |
| 240 | `World.RIVER_LAKE_TRIES` | `32` | Tries at a lake whose shore is dry: 32. |
| 243 | `World.RIVER_LAKE_FAN` | `1.7` | The lake's bearing from the site: opposite the sea, give or take half of this many radians: 1.7. |
| 246 | `World.RIVER_LAKE_NEAR` | `64` | The lake's centre this many plots from the site, at the least: 64 (1.9 km)... |
| 249 | `World.RIVER_LAKE_SPAN` | `26` | ...plus up to this many: 26, so 64 to 90 plots (1.9 to 2.7 km). |
| 252 | `World.LAKE_R_MIN` | `11` | The lake's radius in plots, at the least: 11 (330 m)... |
| 255 | `World.LAKE_R_SPAN` | `6` | ...plus up to this many: 6, so 11 to 17 plots. |
| 258 | `World.LAKE_SHORE` | `1.6` | The lake's shore is tested this many radii out: 1.6... |
| 261 | `World.LAKE_SHORE_RISE` | `0.003` | ...for ground this far above the sea's level: 0.003, two beaches. |
| 264 | `World.RIVER_PASS_MIN` | `8` | The river passes the site at least this many plots to one side: 8 (240 m)... |
| 267 | `World.RIVER_PASS_SPAN` | `12` | ...plus up to this many: 12, so 8 to 20 plots. |
| 270 | `World.RIVER_PASS_REACHED` | `10` | Within this many plots of its passing point the river turns for the sea: 10. |
| 273 | `World.RIVER_STEP` | `3` | Plots the river moves a step: 3 (90 m). |
| 276 | `World.RIVER_WANDER` | `0.75` | Its heading wanders by up to half this many radians a step: 0.75... |
| 279 | `World.RIVER_STEER` | `0.25` | ...and turns this share of the way to its target a step: 0.25. |
| 282 | `World.RIVER_MAX_STEPS` | `500` | The most steps it takes: 500 (45 km). |
| 285 | `World.RIVER_MOUTH` | `0.002` | It ends where the elevation is this far under the sea's level: 0.002. |
| 288 | `World.RIVER_HALF_WIDTH` | `1.0` | Its half-width at the lake, in plots: 1 (a 60 m river)... |
| 291 | `World.RIVER_WIDENS` | `0.95` | ...growing by this many plots to the sea: 0.95, so 1.95 at the mouth (117 m). |
| 309 | `World.FIELD_TAIL` | `1.5` | How heavy the tail of a field's size is: 1.5, so P(sites >= k) = k^-1.5 - most small, a few huge. |
| 312 | `World.MAX_SITES` | `512` | The most sites one field holds: 512. |
| 315 | `World.MEAN_SITES` | `meanSites()` | The mean sites a field: the sum of k^-1.5 for k = 1 to MAX_SITES, 2.524 - exact for the capped tail, since P(sites >= k) = k^-1.5. |
| 318 | `World.RICHNESS_MIN` | `0.6` | A cell's richness, its total against its count's mean, at the least: 0.6... |
| 321 | `World.RICHNESS_SPAN` | `0.8` | ...plus up to this: 0.8, so 0.6 to 1.4, with a mean of one. |
| 324 | `World.SEA_REDRAWS` | `8` | Times a field centred in the sea is drawn again: 8, since ore lies under land. |
| 327 | `World.POISSON_NORMAL_ABOVE` | `40` | Above this mean a cell's count is drawn as a rounded normal rather than by inversion: 40. |
| 330 | `World.FOREST_M3_PER_KM2` | `13_700` | Standing timber a square kilometre of forest, in cubic metres: 13,700 (FAO 2020: 557 billion m3 on 4.06 billion hectares). |
| 337 | `World.WORLDS_KEPT` | `4` | How many worlds World.of() keeps: 4, the city's and a few the founding screen looked at (about 0.3 MB each). |
| 339 | `World.KEPT` | `new LinkedHashMap<>(8, 0.75f, true) { @ Override protected boolean removeElde...` |  |
| 414 | `World.F_ELEV` | `0x1111, F_ELEV_FINE = F_ELEV ^ 0x55, F_LAKE = 0x2222, F_FOREST = 0x3333` | The fields' keys: what makes one field's octaves another's. |
| 417 | `World.AMP` | `new double [ ELEV_COARSE_HI + 1 ]` | OCTAVE_FALLOFF to the power n, by repeated multiplication - the order fbm() has always summed in. |
| 433 | `World.NORM_COARSE` | `norm(ELEV_COARSE_HI, ELEV_COARSE_LO), NORM_FINE = norm(ELEV_FINE_HI, ELEV_FIN...` |  |
| 795 | `World.SMOOTH` | `new double [ ELEV_COARSE_HI + 1 ][]` | The smoothstep at a plot's centre within an octave's lattice cell, for every wavelength 2^k: SMOOTH[k][f] for f = 0 .. |
| 814 | `World.SCRATCH` | `ThreadLocal.withInitial(TileScratch : : new)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 345 | `private final long seed` |  |
| 346 | `private volatile boolean built` |  |
| 348 | `private double seaTheta` |  |
| 350 | `private byte[] landQuarters, forestQuarters` | Each cell's land and forest, in quarters (of its SEA_SAMPLES). |
| 351 | `private long fx, fy` |  |
| 352 | `private int siteChecks, siteCells` |  |
| 353 | `private double[] riverX, riverY, riverW` |  |
| 354 | `private double lakeX, lakeY, lakeR` |  |
| 355 | `private double riverBox0x, riverBox0y, riverBox1x, riverBox1y` |  |
| 809 | `final double[] e` |  |
| 810 | `final double[] lat` |  |
| 811 | `int[] segs` |  |
| 1100 | `private double[] totals` | The totals once computed (0.7.57): what every city founded on this world stores. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 1079 | **type** `public final class World` | The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and river, and the fields of the seven resources - every one of them a pure ... |

### THE GRID (lines 53-80)

### the terrain's classes (lines 81-97)

### THE TERRAIN: VALUE NOISE IN OCTAVES (spec-land 2.1) (lines 98-178)

### THE FOUNDING SITE (spec-land 2.1) (lines 179-221)

### THE FOUNDING LAKE AND RIVER (the mockup's random walk, in plots) (lines 222-292)

### THE DEPOSITS (spec-land 2.1; the densities and sizes are Resource's) (lines 293-331)

### THE WORLD, ITS STATE (lines 332-383)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 3 | `public World(long seed)` | A world from its seed. |
| 363 | 5 | `public static World of(long seed)` | The world a seed makes, shared: the same object for the same seed while it is among the last WORLDS_KEPT asked for. |
| 370 | 1 | `public long seed()` | The seed it was made from. |
| 373 | 10 | `private void build()` | The sea pass, the site and the river, once. |

### THE NOISE (lines 384-462)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 6 | `static long mix(long z)` | SplitMix64's finaliser - LandMarket's scramble: every value in the world is this of the seed and a place. |
| 397 | 1 | `static double unit(long h)` | A hash as a number in [0, 1), from its top 53 bits. |
| 400 | 3 | `static double lattice(long s, long ix, long iy)` | The value at one lattice point of one octave. |
| 405 | 7 | `static double vnoise(long s, double x, double y)` | Value noise at (x, y) in lattice units, smoothstep-interpolated. |
| 418 | 4 | `static { ... }` |  |
| 424 | 1 | `static double amp(int hi, int k)` | An octave's amplitude under a field's top octave. |
| 427 | 5 | `static double norm(int hi, int lo)` | The sum of a field's amplitudes, top octave first. |
| 437 | 1 | `private long oct(long field, int k)` | One octave's stream. |
| 440 | 10 | `double fbm(long field, double x, double y, int hi, int lo)` | A field at (x, y), in plots: octaves hi to lo, normalised to 0..1. |
| 452 | 4 | `double elevation(double x, double y)` | The elevation at a point, in plots: below seaTheta() is sea. |
| 458 | 1 | `double lakeField(double x, double y)` | The lake field at a point: LAKE_THETA and over, on land, is a lake. |
| 461 | 1 | `double forestField(double x, double y)` | The forest field at a point: FOREST_THETA and over, on dry land, is forest. |

### THE SEA PASS: the sea's level, and each cell's land and forest (lines 463-517)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 27 | `private void seaPass()` |  |
| 496 | 1 | `public double seaTheta()` | The sea's level: the elevation SEA_SHARE of the world's samples lie under. |
| 499 | 1 | `public double cellLandShare(int cell)` | A cell's land, as a share of its samples: 0, 0.25, 0.5, 0.75 or 1. |
| 502 | 1 | `public double cellForestShare(int cell)` | A cell's forest, as a share of its samples. |
| 505 | 6 | `public double landShare()` | The world's land, as a share of every cell's samples: 29%, by the sea's level's definition. |
| 513 | 4 | `public static int cellOf(long x, long y)` | The world cell a plot is in, as row x CELLS + column; -1 off the world. |

### THE FOUNDING SITE (lines 518-655)

| line | len | member | says |
|---:|---:|---|---|
| 522 | 30 | `private void found()` |  |
| 554 | 1 | `private boolean dryAt(double x, double y)` | Dry ground at a point: land, and not a lake. |
| 557 | 4 | `private boolean siteOk(long px, long py, Map<Integer, List<Deposit>> iron)` | The four tests, cheapest first, at the centre of plot (px, py) - counted in siteChecks(). |
| 563 | 33 | `private boolean siteTests(long px, long py, Map<Integer, List<Deposit>> iron)` | ...and uncounted, for a search after the founding's (searchSites()). |
| 607 | 33 | `public long[] searchSites(int cells, java.util.function.Predicate<long[]> accept)` | THE FOUNDING SEARCH AGAIN, A CELL AT A TIME (0.7.57, batch J1b): in each of the nearest `cells` coastal cells, in the founding search's order, the first plot of its spiral that passes the four tests is offered to `acc... |
| 642 | 1 | `static int siteLandMin()` | Test 2's least count of land samples: SITE_LAND_SHARE of SITE_LAND_SAMPLES, rounded up - 29 of 48. |
| 645 | 1 | `public long foundingX()` | The founding site, as a plot: east from the world's west edge. |
| 648 | 1 | `public long foundingY()` | ...and south from its north edge. |
| 651 | 1 | `public int siteChecks()` | How many plots the founding search tested - the cost of finding the site. |
| 654 | 1 | `public int siteCells()` | ...and in how many cells: one, unless the nearest coastal cell had no site. |

### THE FOUNDING LAKE AND RIVER (lines 656-769)

| line | len | member | says |
|---:|---:|---|---|
| 660 | 65 | `private void walkRiver()` |  |
| 727 | 12 | **type** `public record River(double[] xs, double[] ys, double[] halfWidths, double lakeX, double lakeY, double lakeR)` | The founding river and its lake, in plots: the walk's points and its half-width at each, and the lake's centre and radius. |
| 730 | 1 | `public int points()` _(in World.River)_ | How many points the walk has. |
| 733 | 5 | `public boolean same(River o)` _(in World.River)_ | Whether two rivers are the same walk to the last bit. |
| 741 | 4 | `public River river()` | The founding river and lake (copies: nothing a caller does moves them). |
| 747 | 7 | `private boolean inRiver(double x, double y)` | Whether a point is in the founding river. |
| 756 | 7 | `private boolean onSegment(int k, double x, double y)` | Whether a point is within the river's half-width of its k-th step. |
| 765 | 4 | `private boolean inLake(double x, double y)` | Whether a point is in the founding lake: within its radius (the mockup's halo past it never reaches the lake's level). |

### THE TERRAIN, A PLOT, A TILE OR A REGION AT A TIME (lines 770-978)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 6 | `private byte classOf(double x, double y, double e, double lake, double forest)` | The class of a point's ground, from its fields: SALT, FRESH, SAND, FOREST or GRASS. |
| 783 | 10 | `public byte terrainAt(long x, long y)` | One plot's ground, at its centre: GRASS, FOREST, FRESH, SALT or SAND; SALT off the world. |
| 796 | 10 | `static { ... }` |  |
| 808 | 5 | **type** `private static final class TileScratch` | One thread's working arrays for a tile. |
| 827 | 48 | `public void tileTerrain(long tx, long ty, byte[] out)` | One tile's ground, a byte a plot in rows (out[py x 32 + px]): what terrainAt() gives each of its plots, on 99.9 to 100% of them, five times faster - each fine octave's lattice is hashed once a tile, and the coarse oct... |
| 877 | 9 | `private static void bilinear(double c00, double c10, double c01, double c11, double scale, double[] acc)` | Sets acc to scale x the bilinear blend of four corner values at each plot's centre. |
| 888 | 20 | `private static void octaveTile(double[] lat, long os, int k, long x0, long y0, double amp, double[] acc)` | Adds amp x one octave's value noise at a tile's 32 x 32 plot centres: its lattice hashed once, the smoothstep from SMOOTH. |
| 910 | 8 | `private double part(long field, double x, double y, int top, int lo)` | Octaves hi to lo of a field at a point, at the amplitudes fbm() gives them under the field's top octave, not normalised. |
| 928 | 20 | `public void regionTerrain(long x0, long y0, int stride, int n, byte[] out)` | A region's ground at a stride, for the far view (spec-land 2.6, L2): n x n pixels from plot (x0, y0), each `stride` plots wide, classed at its centre (out[py x n + px]). |
| 950 | 28 | `private void regionOctave(long field, int k, long x0, long y0, int stride, int n, double amp, double[] acc)` | Adds amp x one octave over a region's pixel centres, or amp x its mean, 0.5, when the octave is no wider than a pixel. |

### THE DEPOSITS (lines 979-1129)

| line | len | member | says |
|---:|---:|---|---|
| 984 | 5 | `private static double meanSites()` | The mean of the capped tail: P(sites >= k) summed over k = 1 .. |
| 991 | 3 | `static int sites(double u)` | A field's sites from a uniform draw: floor((1-u)^(-1/FIELD_TAIL)), at most MAX_SITES. |
| 996 | 17 | `static long poisson(long h, double m)` | A Poisson count from a hash: by inversion for a small mean, a rounded normal above POISSON_NORMAL_ABOVE. |
| 1015 | 3 | `private long cellHash(int cell, Resource r)` | A cell's stream for a resource. |
| 1020 | 1 | `private double landKm2(int cell)` | A cell's land, in square kilometres: its land share times its area. |
| 1023 | 4 | `public long cellFieldCount(int cell, Resource r)` | How many fields of a resource a cell holds: a Poisson count, its mean the resource's density times the cell's land. |
| 1028 | 4 | `private long countOf(int cell, Resource r)` |  |
| 1039 | 4 | `public double cellTotal(int cell, Resource r)` | What a cell holds of a resource, in whole tonnes (forest, whole cubic metres): its count x MEAN_SITES x the amount a site x the cell's richness, drawn without drawing a field. |
| 1044 | 9 | `private double totalOf(int cell, Resource r)` |  |
| 1060 | 4 | `public List<Deposit> fieldsInCell(int cell, Resource r)` | A cell's fields of a resource, drawn from the cell's own stream: each one's centre (drawn again, up to SEA_REDRAWS times, while it is in the sea - but oil's), its sites, and its share of cellTotal() by sites, rounded ... |
| 1065 | 33 | `private List<Deposit> fieldsOf(int cell, Resource r)` |  |
| 1107 | 6 | `public double[] totals()` | The world's totals, computed once and kept (0.7.57): computeTotals()'s figures, a copy - what a city founded or converted on this world stores, so a load needs no pass (spec-land 2.1). |
| 1119 | 10 | `public double[] computeTotals()` | The world's totals, a resource at a time in Resource's order: every cell's cellTotal(), summed in cell order - one pass over 135,424 cells, about 30 ms, the same every time. |

