# World.java - 1,918 lines · 102 methods · 96 constants · model

`ham/citybuildersim/World.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and river, and the fields of the seven resources - every one of them a pure function of one seed, or since 0.7.115 of a key naming the real Earth round a spot (EarthTerrain).
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
> first question asked of a world runs the sea pass (the sea's level, and
> since 0.7.79 its shelf's, from four samples a cell, the land and forest in
> every cell), the founding
> site's search and the river - about half a second - and nothing is stored
> that the seed cannot give back. World.of() keeps the last few worlds asked
> for, so the screens and the land office share one.
> 
> FINER WHERE IT MATTERS. The cells carry the totals and the far view; near
> the city every function is evaluated per plot (terrainAt()), a tile at a
> time (tileTerrain(), each octave's lattice once a tile) or a region at a
> stride (regionTerrain(), the octaves narrower than a pixel replaced by
> their mean), and a cell's fields are drawn only when asked for.
> 
> A WORLD'S KEY (0.7.115, batch E2; the project's spec-earth.md 2 and 8):
> since "the world as planet Earth" a world is either the generated one its
> seed makes, or the real Earth round a spot - Key holds which, its seed, and
> on Earth the spot and the data. World.of(seed) is the generated world as
> ever, and World.of(key) the one a key names; a generated key reaches the
> same world and the same code. An Earth world's ground is EarthTerrain's
> (spec-earth 3): the cells pass replaces the sea pass, the founding is the
> snap to dry ground near the spot, and it has no founding river and no
> heights (shelfTheta() refuses; since 0.7.121 its sea's depth is the
> distance from the coast's, depthAt()); its fields are drawn on its
> cells' land, off its sea, and since 0.7.117 (batch E3) on its geology:
> today's densities times each province's weight and Z (Geology; oil's
> petroleum, and its offshore sea), each field's type leaning on the
> class under it (Deposit.type()). Since 0.7.121 (batch E4) the founding
> screen founds a city on it (EarthSite, Founding.onEarth()).

**Uses:** [Resource](Resource.md) (38), [Deposit](Deposit.md) (15), [EarthTerrain](EarthTerrain.md) (5), [Geology](Geology.md) (2)

**Used by (70):** [BoatSchedule](BoatSchedule.md), [BuildCardCheck](BuildCardCheck.md), [BuildingVisual](BuildingVisual.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [CityShore](CityShore.md), [CoalMiningCheck](CoalMiningCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [DataSave](DataSave.md), [Deposit](Deposit.md), [DistrictPicture](DistrictPicture.md), [DistrictPlan](DistrictPlan.md), [EarthCheck](EarthCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthProj](EarthProj.md), [EarthSite](EarthSite.md), [EarthTerrain](EarthTerrain.md), [EarthTerrainCheck](EarthTerrainCheck.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [Gateways](Gateways.md), [GlobeCheck](GlobeCheck.md), [GlobeFrame](GlobeFrame.md), [GlobePicture](GlobePicture.md), [GridCheck](GridCheck.md), [GridConversion](GridConversion.md), [GridOffers](GridOffers.md), [InterchangeGround](InterchangeGround.md), [JunctionCheck](JunctionCheck.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandGrid](LandGrid.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LegacyLand](LegacyLand.md), [LongPlaytest](LongPlaytest.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapFrame](MapFrame.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [MiniMapFrame](MiniMapFrame.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [PlanCheck](PlanCheck.md), [PortCheck](PortCheck.md), [QuarryingCheck](QuarryingCheck.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SeaRoutes](SeaRoutes.md), [ShipShapes](ShipShapes.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md), [TrainSchedule](TrainSchedule.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WorldCheck](WorldCheck.md), [ZoningCheck](ZoningCheck.md)

## Sections

| line | section |
|---:|---|
| 70 | THE GRID |
| 98 | · the terrain's classes |
| 138 | THE TERRAIN: VALUE NOISE IN OCTAVES (spec-land 2.1) |
| 219 | · the sea's depth (0.7.79, batch O3; docs/studies/spec-oil.md 2.7) |
| 236 | THE FOUNDING SITE (spec-land 2.1) |
| 292 | THE FOUNDING LAKE AND RIVER (the mockup's random walk, in plots) |
| 363 | THE DEPOSITS (spec-land 2.1; the densities and sizes are Resource's) |
| 417 | FEWER AND BIGGER DEPOSITS (0.7.99, batch W1; Jerus, 2026-10-08: "the |
| 483 | THE WORLD, ITS STATE |
| 622 | THE NOISE |
| 701 | THE SEA PASS: the sea's level, and each cell's land and forest |
| 811 | THE FOUNDING SITE |
| 957 | THE FOUNDING LAKE AND RIVER |
| 1071 | THE TERRAIN, A PLOT, A TILE OR A REGION AT A TIME |
| 1416 | THE DEPOSITS |
| 1577 | FEWER AND BIGGER DEPOSITS: THE POOLS AND THEIR CLUSTERS (0.7.99; the |

## Enum constants

| line | constant | says |
|---:|---|---|
| 497 | `World.Kind.GENERATED` |  |
| 497 | `World.Kind.EARTH` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 75 | `World.PLOT_M` | `30` | A plot's side, in metres: 30, the map's smallest square and the mockup's cell. |
| 78 | `World.KM2_PER_PLOT` | `PLOT_M * PLOT_M / 1e6` | A plot's area in square kilometres: 30 m squared, 0.0009. |
| 81 | `World.TILE` | `32` | Plots on a tile's side: 32, 0.96 km - the unit the map paints (spec-land 2.6). |
| 84 | `World.DISTRICT` | `256` | Plots on a district's side: 256, 7.68 km - the unit the map stores (spec-land 2.5). |
| 87 | `World.CELL` | `2048` | Plots on a world cell's side: 2,048, 61.44 km - the unit the world's totals and far view are counted in, so plot, tile, district and cell nest by powers of two (spec-land star 4). |
| 90 | `World.CELLS` | `368` | World cells on the world's side: 368, so the world is 511.2 million km2, within 0.2% of the Earth's 510.1 million. |
| 93 | `World.SIDE` | `(long) CELL * CELLS` | Plots on the world's side: 2,048 x 368, 753,664 (22,610 km). |
| 96 | `World.CELL_KM2` | `(CELL * PLOT_M / 1000) *(CELL * PLOT_M / 1000)` | A world cell's area in square kilometres: 61.44 km squared, 3,774.87. |
| 101 | `World.GRASS` | `0` | Open ground: dry and buildable. |
| 104 | `World.FOREST` | `1` | Forest: dry and buildable - a building on it clears it, and the forest law may keep it from building (LandManager, ZONES AND THE FOREST LAW, 0.7.107) - and standing timber (Resource.FOREST). |
| 107 | `World.FRESH` | `2` | Fresh water: a lake, or the founding river. |
| 110 | `World.SALT` | `3` | Salt water: the sea, and everything beyond the world's edge. |
| 113 | `World.SAND` | `4` | Beach: dry land within BEACH_BAND of the sea's level. |
| 116 | `World.ICE` | `5` | Ice (0.7.127, batch E7; spec-earth's E14, docs/studies/spec-globe.md 3.9): land under snow and ice - never dry, never built on, never a founding site - counted in a holding's whole area only. |
| 119 | `World.DESERT` | `6` | Desert (0.7.127, batch E7; spec-globe Q1): dry ground every rule treats as grass - only its colour and its name differ. |
| 122 | `World.CLASSES` | `7` | How many classes the ground has: 7, GRASS to DESERT - the length of an array a class indexes (0.7.127; five until then, and a generated world draws only those). |
| 125 | `World.DRY_CLASSES` | `1<<GRASS \| 1<<FOREST \| 1<<SAND \| 1<<DESERT` | The dry classes, a bit each: grass, forest, the beach and the desert. |
| 150 | `World.OCTAVE_FALLOFF` | `0.55` | Each octave's amplitude against the one above it: 0.55, a little rougher than classic fractal noise's half (the mockup's terrain, scaled to plots). |
| 153 | `World.ELEV_COARSE_HI` | `16` | Elevation's coarse octaves run from 2^16 plots (1,966 km): continents and oceans. |
| 156 | `World.ELEV_COARSE_LO` | `9` | ...down to 2^9 plots (15 km): coasts and bays. |
| 159 | `World.ELEV_FINE_HI` | `8` | Elevation's fine octaves run from 2^8 plots (7.7 km)... |
| 162 | `World.ELEV_FINE_LO` | `4` | ...down to 2^4 plots (480 m): headlands, coves and islets. |
| 165 | `World.ELEV_COARSE_WEIGHT` | `0.85` | The coarse octaves' share of the elevation: 0.85, so the coast is the continents' and the fine octaves only fray it. |
| 168 | `World.ELEV_FINE_WEIGHT` | `0.15` | The fine octaves' share: the rest, 0.15. |
| 171 | `World.LAKE_HI` | `10` | The lake field's octaves run from 2^10 plots (30.7 km)... |
| 174 | `World.LAKE_LO` | `5` | ...down to 2^5 plots (960 m). |
| 177 | `World.FOREST_HI` | `8` | The forest field's octaves run from 2^8 plots (7.7 km)... |
| 180 | `World.FOREST_LO` | `5` | ...down to 2^5 plots (960 m). |
| 183 | `World.SEA_SHARE` | `0.71` | The share of the world that is sea: 71%, the Earth's. |
| 195 | `World.SEA_SAMPLES` | `4` | Elevation samples a world cell the sea's level is found from: 4, one at a hashed point in each quarter of the cell (541,696 in all). |
| 198 | `World.LAKE_SHARE` | `0.037` | Lakes' share of land: 3.7%, of the Earth's non-glaciated land (Verpoorter 2014). |
| 208 | `World.LAKE_THETA` | `0.7092` | The lake field's level from which ground is a lake: 0.7092, the 96.3rd percentile of the field over land (LAKE_SHARE) - measured 0.7088 to 0.7095 over five seeds, a million points each, off the lattice; a constant, so... |
| 211 | `World.FOREST_SHARE` | `0.31` | Forest's share of land: 31% (FAO 2020). |
| 214 | `World.FOREST_THETA` | `0.5676` | The forest field's level from which land is forest: 0.5676, the 69th percentile of the field over land (FOREST_SHARE), measured 0.5672 to 0.5679 as LAKE_THETA was (the design's 0.595 makes 24% of land forest). |
| 217 | `World.BEACH_BAND` | `0.0015` | How far above the sea's level, in the elevation field, land is beach: 0.0015. |
| 231 | `World.SHELF_SHARE` | `0.0886` | The continental shelf's share of the sea: 8.86% (est., the shelf's share of the ocean's area, Harris et al. |
| 234 | `World.SHELF_BREAK_M` | `140` | The depth of the shelf's edge, in metres: 140 (est., the mean shelf break - spec-oil 2.7 and 6, to confirm); depthAt() is this at shelfTheta(). |
| 252 | `World.SITE_CELL_LAND_MIN` | `1` | The least land, in quarters of a cell's SEA_SAMPLES, a cell needs to be searched for a site: 1, 25%. |
| 255 | `World.SITE_CELL_LAND_MAX` | `3` | The most: 3, 75% - a coast, not open sea or an interior. |
| 258 | `World.SITE_STEP` | `17` | The spiral's step: 17 plots (510 m) between rings, and the ring's points about that far apart (six a ring per ring). |
| 261 | `World.SITE_RINGS` | `70` | Rings in a cell's spiral: 70, out to 1,173 plots (35 km), past the cell's own half-width of 1,024. |
| 264 | `World.SITE_DRY_PLOTS` | `20` | Test 1: the site and eight points this far round it are dry ground, in plots: 20 (600 m), room for a new city's centre - since 0.7.67 rings of 120 m blocks round the site's own, on the default world 21 of them, 0.30 k... |
| 267 | `World.SITE_LAND_SHARE` | `0.6` | Test 2: of SITE_LAND_SAMPLES points within 5 km, at least this share are land: 60%. |
| 270 | `World.SITE_LAND_SAMPLES` | `48` | ...over 48 points, at 1.5, 3 and 5 km in turn round the compass. |
| 273 | `World.SITE_LAND_RADII` | `{ 50, 100, 167 }` | ...at these radii, in plots: 50, 100 and 167 (1.5, 3 and 5 km). |
| 276 | `World.SITE_SEA_NOT_WITHIN` | `45` | Test 3: no sea within this many plots in 16 directions: 45 (1.35 km). |
| 279 | `World.SITE_SEA_WITHIN` | `70` | ...but sea within this many in at least one: 70 (2.1 km) - a coast with room for a town. |
| 290 | `World.SITE_IRON_KM` | `2` | Test 4 (spec-land star), the conversion's alone since 0.7.99: an iron field's centre within this many km of the old world's fields (legacyFieldsInCell()) - the mockup put deposits near the site "so the first mines com... |
| 301 | `World.RIVER_SEA_DIRECTIONS` | `32` | Directions the sea is looked for in, round the site: 32. |
| 304 | `World.RIVER_SEA_REACH` | `100` | ...out to this many plots, from SITE_SEA_NOT_WITHIN: 100 (3 km). |
| 307 | `World.RIVER_SEA_STEP` | `5` | ...in steps of this many plots: 5. |
| 310 | `World.RIVER_LAKE_TRIES` | `32` | Tries at a lake whose shore is dry: 32. |
| 313 | `World.RIVER_LAKE_FAN` | `1.7` | The lake's bearing from the site: opposite the sea, give or take half of this many radians: 1.7. |
| 316 | `World.RIVER_LAKE_NEAR` | `64` | The lake's centre this many plots from the site, at the least: 64 (1.9 km)... |
| 319 | `World.RIVER_LAKE_SPAN` | `26` | ...plus up to this many: 26, so 64 to 90 plots (1.9 to 2.7 km). |
| 322 | `World.LAKE_R_MIN` | `11` | The lake's radius in plots, at the least: 11 (330 m)... |
| 325 | `World.LAKE_R_SPAN` | `6` | ...plus up to this many: 6, so 11 to 17 plots. |
| 328 | `World.LAKE_SHORE` | `1.6` | The lake's shore is tested this many radii out: 1.6... |
| 331 | `World.LAKE_SHORE_RISE` | `0.003` | ...for ground this far above the sea's level: 0.003, two beaches. |
| 334 | `World.RIVER_PASS_MIN` | `8` | The river passes the site at least this many plots to one side: 8 (240 m)... |
| 337 | `World.RIVER_PASS_SPAN` | `12` | ...plus up to this many: 12, so 8 to 20 plots. |
| 340 | `World.RIVER_PASS_REACHED` | `10` | Within this many plots of its passing point the river turns for the sea: 10. |
| 343 | `World.RIVER_STEP` | `3` | Plots the river moves a step: 3 (90 m). |
| 346 | `World.RIVER_WANDER` | `0.75` | Its heading wanders by up to half this many radians a step: 0.75... |
| 349 | `World.RIVER_STEER` | `0.25` | ...and turns this share of the way to its target a step: 0.25. |
| 352 | `World.RIVER_MAX_STEPS` | `500` | The most steps it takes: 500 (45 km). |
| 355 | `World.RIVER_MOUTH` | `0.002` | It ends where the elevation is this far under the sea's level: 0.002. |
| 358 | `World.RIVER_HALF_WIDTH` | `1.0` | Its half-width at the lake, in plots: 1 (a 60 m river)... |
| 361 | `World.RIVER_WIDENS` | `0.95` | ...growing by this many plots to the sea: 0.95, so 1.95 at the mouth (117 m). |
| 394 | `World.FIELD_TAIL` | `1.5` | How heavy the tail of a field's size is: 1.5, so P(sites >= k) = k^-1.5 - most small, a few huge. |
| 397 | `World.LEGACY_MAX_SITES` | `512` | The most sites one of the old world's fields holds: 512 (the cap of the tail's draw, sites()). |
| 400 | `World.MEAN_SITES` | `meanSites()` | The mean sites one of the old world's fields: the sum of k^-1.5 for k = 1 to LEGACY_MAX_SITES, 2.524 - exact for the capped tail, since P(sites >= k) = k^-1.5. |
| 403 | `World.RICHNESS_MIN` | `0.6` | A cell's richness, its total against its count's mean, at the least: 0.6... |
| 406 | `World.RICHNESS_SPAN` | `0.8` | ...plus up to this: 0.8, so 0.6 to 1.4, with a mean of one. |
| 409 | `World.SEA_REDRAWS` | `8` | Times a field centred in the sea is drawn again: 8, since ore lies under land. |
| 412 | `World.POISSON_NORMAL_ABOVE` | `40` | Above this mean a cell's count is drawn as a rounded normal rather than by inversion: 40. |
| 415 | `World.FOREST_M3_PER_KM2` | `13_700` | Standing timber a square kilometre of forest, in cubic metres: 13,700 (FAO 2020: 557 billion m3 on 4.06 billion hectares). |
| 463 | `World.FIELD_SCALE` | `10` | How many of the old world's fields one field stands for: 10 (Jerus, 2026-10-08: "a tenth as many fields, each ten times bigger", the world's totals as they were). |
| 466 | `World.MAX_SITES` | `FIELD_SCALE * LEGACY_MAX_SITES` | The most sites one field holds: FIELD_SCALE of the old world's largest, 5,120. |
| 469 | `World.CLUSTER_FIELDS` | `10` | The fields a cluster holds on the mean: 10 (★W1-2, est.: Jerus's "very big clusters" - ten fields a cluster, as a field is ten of the old). |
| 472 | `World.POOL_CLUSTERS` | `10` | The clusters a pool of land holds on the mean, at the least: 10 (★W1-2, est.): enough that a pool's clusters lie where the draws put them, not one a pool on a lattice - a pool a quarter land still holds two or three. |
| 475 | `World.FIELD_INDEX_FROM` | `1<<16` | The first number a field is listed under in its cell: 65,536, past any old world's field's (a cell held at most 868 of them on the default world, iron's). |
| 478 | `World.POOLS_KEPT` | `256` | Pools kept, by resource and place: 256 (an iron pool is about 300 fields, some 15 KB). |
| 481 | `World.POOL_SALT` | `0x5EB0C1A5L` | The stream a pool is drawn from. |
| 488 | `World.WORLDS_KEPT` | `4` | How many worlds World.of() keeps: 4, the city's and a few the founding screen looked at (about 0.3 MB each). |
| 490 | `World.KEPT` | `new LinkedHashMap<>(8, 0.75f, true) { @ Override protected boolean removeElde...` |  |
| 524 | `World.KEPT_EARTH` | `new LinkedHashMap<>(8, 0.75f, true) { @ Override protected boolean removeElde...` | Earth worlds kept as World.of(key) is asked for them: WORLDS_KEPT, apart from the generated, so those are kept as they were. |
| 652 | `World.F_ELEV` | `0x1111, F_ELEV_FINE = F_ELEV ^ 0x55, F_LAKE = 0x2222, F_FOREST = 0x3333` | The fields' keys: what makes one field's octaves another's. |
| 655 | `World.AMP` | `new double [ ELEV_COARSE_HI + 1 ]` | OCTAVE_FALLOFF to the power n, by repeated multiplication - the order fbm() has always summed in. |
| 671 | `World.NORM_COARSE` | `norm(ELEV_COARSE_HI, ELEV_COARSE_LO), NORM_FINE = norm(ELEV_FINE_HI, ELEV_FIN...` |  |
| 1097 | `World.SMOOTH` | `new double [ ELEV_COARSE_HI + 1 ][]` | The smoothstep at a plot's centre within an octave's lattice cell, for every wavelength 2^k: SMOOTH[k][f] for f = 0 .. |
| 1116 | `World.SCRATCH` | `ThreadLocal.withInitial(TileScratch : : new)` |  |
| 1258 | `World.LineScratch.CORNERS` | `64` | The corners it keeps: 64 slots, picked by the top six bits of the corner's hash. |
| 1275 | `World.LINE_SCRATCH` | `ThreadLocal.withInitial(LineScratch : : new)` | Each thread's LineScratch, made on its first line. |
| 1583 | `World.POOL_SIDE` | `new int [ Resource.values().length ]` | Each resource's pool side in cells, in Resource's order (poolCells()). |
| 1636 | `World.NO_POOL` | `new Pool(new long [ 0 ], new long [ 0 ], new int [ 0 ], new double [ 0 ], 0, 0)` | The pool of a place whose land cells hold no old draw of the resource: no field, no tonnes. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 530 | `private final long seed` |  |
| 531 | `private final Key key` |  |
| 533 | `private final EarthTerrain earth` | An Earth world's ground (0.7.115); null on a generated world. |
| 534 | `private volatile boolean built` |  |
| 536 | `private double seaTheta` |  |
| 538 | `private double shelfTheta` | The shelf's edge in the elevation field (0.7.79): see shelfTheta(). |
| 540 | `private byte[] landQuarters, forestQuarters` | Each cell's land and forest, in quarters (of its SEA_SAMPLES). |
| 541 | `private long fx, fy` |  |
| 542 | `private int siteChecks, siteCells` |  |
| 543 | `private double[] riverX, riverY, riverW` |  |
| 544 | `private double lakeX, lakeY, lakeR` |  |
| 545 | `private double riverBox0x, riverBox0y, riverBox1x, riverBox1y` |  |
| 1111 | `final double[] e` |  |
| 1112 | `final double[] lat` |  |
| 1113 | `int[] segs` |  |
| 1254 | `final double[] e` |  |
| 1255 | `final double[] lat` |  |
| 1256 | `int[] segs` |  |
| 1259 | `final long[] cornerX` |  |
| 1260 | `final boolean[] cornerSet` |  |
| 1261 | `final double[][] cornerV` |  |
| 1619 | `final long[] xs, ys` |  |
| 1620 | `final int[] sites` |  |
| 1621 | `final double[] amounts` |  |
| 1622 | `final double total` |  |
| 1623 | `final long old` |  |
| 1639 | `private final Map<Long, Pool> pools` | The pools drawn, kept: by resource and place, the last POOLS_KEPT asked for. |
| 1883 | `private double[] totals` | The totals once computed (0.7.57): what every city founded on this world stores. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 68 | 1851 | **type** `public final class World` | The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and river, and the fields of the seven resources - every one of them a pure ... |

### THE GRID (lines 70-97)

### the terrain's classes (lines 98-137)

| line | len | member | says |
|---:|---:|---|---|
| 134 | 3 | `public static boolean dry(int cls)` | Whether a class is dry ground - grass, forest, the beach or the desert: what buildings stand on, the founding's test 1 asks for and a holding's dry area counts. |

### THE TERRAIN: VALUE NOISE IN OCTAVES (spec-land 2.1) (lines 138-218)

### the sea's depth (0.7.79, batch O3; docs/studies/spec-oil.md 2.7) (lines 219-235)

### THE FOUNDING SITE (spec-land 2.1) (lines 236-291)

### THE FOUNDING LAKE AND RIVER (the mockup's random walk, in plots) (lines 292-362)

### THE DEPOSITS (spec-land 2.1; the densities and sizes are Resource's) (lines 363-416)

### FEWER AND BIGGER DEPOSITS (0.7.99, batch W1; Jerus, 2026-10-08: "the (lines 417-482)

### THE WORLD, ITS STATE (lines 483-621)

| line | len | member | says |
|---:|---:|---|---|
| 497 | 1 | **type** `public enum Kind` | Which world a key names: the generated one its seed makes, or the real Earth round a spot (0.7.115). |
| 507 | 15 | **type** `public record Key(Kind kind, long seed, double lat, double lon, String data)` | What makes one world another (0.7.115, batch E2; spec-earth 6): its kind and seed, and on Earth the spot the projection is centred on (degrees) and the data it is drawn from, by its identity (EarthData.identity(), "ea... |
| 510 | 1 | `public static Key generated(long seed)` _(in World.Key)_ | The generated world a seed makes. |
| 513 | 5 | `public static Key earth(long seed, double lat, double lon, String data)` _(in World.Key)_ | The real Earth round (lat, lon), its detail from the seed, read from the data of that identity. |
| 520 | 1 | `public boolean earth()` _(in World.Key)_ | Whether it is an Earth world's. |
| 548 | 5 | `public World(long seed)` | A world from its seed. |
| 555 | 5 | `public World(Key key)` | The world a key names (0.7.115): a generated key's is new World(seed); an Earth key's finds its data now (EarthTerrain.source()) and is built, like any, when first asked. |
| 562 | 5 | `public static World of(long seed)` | The world a seed makes, shared: the same object for the same seed while it is among the last WORLDS_KEPT asked for. |
| 569 | 6 | `public static World of(Key key)` | The world a key names, shared (0.7.115): a generated key's is World.of(its seed), the same object; an Earth key's is kept apart, the last WORLDS_KEPT asked for. |
| 577 | 1 | `public long seed()` | The seed it was made from. |
| 580 | 1 | `public Key key()` | Its key (0.7.115): Key.generated(seed()) for a generated world. |
| 583 | 1 | `public boolean isEarth()` | Whether it is the real Earth (0.7.115). |
| 586 | 1 | `EarthTerrain earthTerrain()` | An Earth world's ground, null on a generated world (0.7.115): EarthTerrainCheck's door. |
| 589 | 27 | `private void build()` | The sea pass, the site and the river, once - on Earth (0.7.115) the cells pass and the snap, and no river. |
| 618 | 3 | `private void noHeightsOnEarth(String what)` | What a generated world's heights answer and an Earth world has none of (earth-1 holds no heights, Jerus's Q8): refused there. |

### THE NOISE (lines 622-700)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 6 | `static long mix(long z)` | SplitMix64's finaliser - LandMarket's scramble: every value in the world is this of the seed and a place. |
| 635 | 1 | `static double unit(long h)` | A hash as a number in [0, 1), from its top 53 bits. |
| 638 | 3 | `static double lattice(long s, long ix, long iy)` | The value at one lattice point of one octave. |
| 643 | 7 | `static double vnoise(long s, double x, double y)` | Value noise at (x, y) in lattice units, smoothstep-interpolated. |
| 656 | 4 | `static { ... }` |  |
| 662 | 1 | `static double amp(int hi, int k)` | An octave's amplitude under a field's top octave. |
| 665 | 5 | `static double norm(int hi, int lo)` | The sum of a field's amplitudes, top octave first. |
| 675 | 1 | `private long oct(long field, int k)` | One octave's stream. |
| 678 | 10 | `double fbm(long field, double x, double y, int hi, int lo)` | A field at (x, y), in plots: octaves hi to lo, normalised to 0..1. |
| 690 | 4 | `double elevation(double x, double y)` | The elevation at a point, in plots: below seaTheta() is sea. |
| 696 | 1 | `double lakeField(double x, double y)` | The lake field at a point: LAKE_THETA and over, on land, is a lake. |
| 699 | 1 | `double forestField(double x, double y)` | The forest field at a point: FOREST_THETA and over, on dry land, is forest. |

### THE SEA PASS: the sea's level, and each cell's land and forest (lines 701-810)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 30 | `private void seaPass()` |  |
| 737 | 1 | `public double seaTheta()` | The sea's level: the elevation SEA_SHARE of the world's samples lie under; on Earth (0.7.115), with no heights, EarthTerrain.SEA_LEVEL, the share the coast is drawn at. |
| 740 | 1 | `public double shelfTheta()` | The continental shelf's edge in the elevation field (0.7.79): the level the shallowest SHELF_SHARE of the sea's samples lie at and above, so under seaTheta(). |
| 755 | 6 | `public double depthAt(long x, long y)` | The sea's depth at a plot's centre, in metres below its level (0.7.79, batch O3; spec-oil 2.7): 0 where the plot is not sea (land, beach, a lake or the river - on the world terrainAt() is SALT exactly where it is over... |
| 763 | 5 | `public double cellLandShare(int cell)` | A cell's land, as a share of its samples: 0, 0.25, 0.5, 0.75 or 1 - on Earth (0.7.115) its cells pass's share, of 16 points. |
| 770 | 5 | `public double cellForestShare(int cell)` | A cell's forest, as a share of its samples (on Earth, its cells pass's). |
| 777 | 11 | `public double landShare()` | The world's land, as a share of every cell's samples: 29%, by the sea's level's definition - on Earth, its cells' shares averaged. |
| 790 | 3 | `private boolean hasLand(int c)` | Whether a cell holds land, which its fields are drawn on: on Earth (0.7.115) its cells pass's share over nothing. |
| 795 | 4 | `private boolean holds(int c, Resource r)` | Whether a cell holds ground a resource's fields are drawn on: its land (hasLand()); on Earth (0.7.117) its field land, oil's with a shelf's, basin's or rift's sea, and forest's its land. |
| 801 | 3 | `private boolean seaAt(double x, double y)` | Whether a point is sea, for a field drawn off it: the elevation under the sea's level - on Earth (0.7.115) the coast's rule at the point (EarthTerrain.seaAt()). |
| 806 | 4 | `public static int cellOf(long x, long y)` | The world cell a plot is in, as row x CELLS + column; -1 off the world. |

### THE FOUNDING SITE (lines 811-956)

| line | len | member | says |
|---:|---:|---|---|
| 815 | 29 | `private void found()` |  |
| 846 | 1 | `private boolean dryAt(double x, double y)` | Dry ground at a point: land, and not a lake. |
| 849 | 4 | `private boolean siteOk(long px, long py)` | A founding's tests, cheapest first, at the centre of plot (px, py) - the first three since 0.7.99 (no iron) - counted in siteChecks(). |
| 859 | 34 | `private boolean siteTests(long px, long py, Map<Integer, List<Deposit>> iron)` | ...and uncounted: tests 1 to 3, and with `iron` (the old world's iron fields by cell, filled as they are asked for) the fourth, for the conversion's search (searchSites()). |
| 907 | 34 | `public long[] searchSites(int cells, java.util.function.Predicate<long[]> accept)` | THE FOUNDING SEARCH AGAIN, A CELL AT A TIME (0.7.57, batch J1b): in each of the nearest `cells` coastal cells, in the founding search's order, the first plot of its spiral that passes the four tests is offered to `acc... |
| 943 | 1 | `static int siteLandMin()` | Test 2's least count of land samples: SITE_LAND_SHARE of SITE_LAND_SAMPLES, rounded up - 29 of 48. |
| 946 | 1 | `public long foundingX()` | The founding site, as a plot: east from the world's west edge. |
| 949 | 1 | `public long foundingY()` | ...and south from its north edge. |
| 952 | 1 | `public int siteChecks()` | How many plots the founding search tested - the cost of finding the site (on Earth, the snap's). |
| 955 | 1 | `public int siteCells()` | ...and in how many cells: one, unless the nearest coastal cell had no site (on Earth, one). |

### THE FOUNDING LAKE AND RIVER (lines 957-1070)

| line | len | member | says |
|---:|---:|---|---|
| 961 | 65 | `private void walkRiver()` |  |
| 1028 | 12 | **type** `public record River(double[] xs, double[] ys, double[] halfWidths, double lakeX, double lakeY, double lakeR)` | The founding river and its lake, in plots: the walk's points and its half-width at each, and the lake's centre and radius. |
| 1031 | 1 | `public int points()` _(in World.River)_ | How many points the walk has. |
| 1034 | 5 | `public boolean same(River o)` _(in World.River)_ | Whether two rivers are the same walk to the last bit. |
| 1042 | 4 | `public River river()` | The founding river and lake (copies: nothing a caller does moves them). |
| 1048 | 7 | `private boolean inRiver(double x, double y)` | Whether a point is in the founding river. |
| 1057 | 7 | `private boolean onSegment(int k, double x, double y)` | Whether a point is within the river's half-width of its k-th step. |
| 1066 | 4 | `private boolean inLake(double x, double y)` | Whether a point is in the founding lake: within its radius (the mockup's halo past it never reaches the lake's level). |

### THE TERRAIN, A PLOT, A TILE OR A REGION AT A TIME (lines 1071-1415)

| line | len | member | says |
|---:|---:|---|---|
| 1076 | 6 | `private byte classOf(double x, double y, double e, double lake, double forest)` | The class of a point's ground, from its fields: SALT, FRESH, SAND, FOREST or GRASS. |
| 1084 | 11 | `public byte terrainAt(long x, long y)` | One plot's ground, at its centre: GRASS, FOREST, FRESH, SALT or SAND; SALT off the world. |
| 1098 | 10 | `static { ... }` |  |
| 1110 | 5 | **type** `private static final class TileScratch` | One thread's working arrays for a tile. |
| 1131 | 52 | `public void tileTerrain(long tx, long ty, byte[] out)` | One tile's ground, a byte a plot in rows (out[py x 32 + px]): what terrainAt() gives each of its plots, on 99.9 to 100% of them, five times faster - each fine octave's lattice is hashed once a tile, and the coarse oct... |
| 1194 | 57 | `public void lineTerrain(long tx, long ty, boolean column, int at, byte[] out)` | One row (or, `column`, one column) of a tile's ground, its `at`-th: the TILE plots tileTerrain() gives that line, byte for byte - the same corners, octaves and sums in the same order, evaluated on the line's plots alo... |
| 1253 | 20 | **type** `private static final class LineScratch` | One thread's working arrays for a line, and the tile corners it met last (their elevation's coarse octaves and lake's, as tileTerrain() reads them). |
| 1264 | 8 | `double[] corner(World w, long x, long y)` _(in World.LineScratch)_ | A tile corner's {elevation's coarse octaves, lake's corner octaves}, as tileTerrain()'s c00 and l00 at (x, y): kept by place and world. |
| 1278 | 8 | `private static void bilinearLine(double c00, double c10, double c01, double c11, double scale, boolean column, int at, double[]...` | bilinear() on one line of the tile's plots: acc[i] is the line's i-th plot's value. |
| 1288 | 19 | `private static void octaveLine(double[] lat, long os, int k, long x0, long y0, double amp, boolean column, int at, double[] acc)` | octaveTile() on one line of the tile's plots: the same lattice, the same sum at each of its plots. |
| 1309 | 9 | `private static void bilinear(double c00, double c10, double c01, double c11, double scale, double[] acc)` | Sets acc to scale x the bilinear blend of four corner values at each plot's centre. |
| 1320 | 20 | `private static void octaveTile(double[] lat, long os, int k, long x0, long y0, double amp, double[] acc)` | Adds amp x one octave's value noise at a tile's 32 x 32 plot centres: its lattice hashed once, the smoothstep from SMOOTH. |
| 1342 | 8 | `private double part(long field, double x, double y, int top, int lo)` | Octaves hi to lo of a field at a point, at the amplitudes fbm() gives them under the field's top octave, not normalised. |
| 1361 | 24 | `public void regionTerrain(long x0, long y0, int stride, int n, byte[] out)` | A region's ground at a stride, for the far view (spec-land 2.6, L2): n x n pixels from plot (x0, y0), each `stride` plots wide, classed at its centre (out[py x n + px]). |
| 1387 | 28 | `private void regionOctave(long field, int k, long x0, long y0, int stride, int n, double amp, double[] acc)` | Adds amp x one octave over a region's pixel centres, or amp x its mean, 0.5, when the octave is no wider than a pixel. |

### THE DEPOSITS (lines 1416-1576)

| line | len | member | says |
|---:|---:|---|---|
| 1421 | 5 | `private static double meanSites()` | The mean of the capped tail: P(sites >= k) summed over k = 1 .. |
| 1428 | 3 | `static int sites(double u)` | One of the old world's fields' sites from a uniform draw: floor((1-u)^(-1/FIELD_TAIL)), at most LEGACY_MAX_SITES - and since 0.7.99 each of the FIELD_SCALE draws a field's sites add up. |
| 1433 | 17 | `static long poisson(long h, double m)` | A Poisson count from a hash: by inversion for a small mean, a rounded normal above POISSON_NORMAL_ABOVE. |
| 1452 | 3 | `private long cellHash(int cell, Resource r)` | A cell's stream for a resource. |
| 1457 | 1 | `private double landKm2(int cell)` | A cell's land, in square kilometres: its land share times its area. |
| 1467 | 1 | `private double fieldKm2(int cell, Resource r)` | The ground a resource's fields are drawn on in a cell, km2: its land (landKm2()) on a generated world; on Earth (0.7.117, batch E3; spec-earth 4) its field land, the land at each of the cells pass's points times its p... |
| 1470 | 4 | `double cellFieldShare(int cell, Resource r)` | A cell's field land for a resource as a share of its area (0.7.117): its land share on a generated world, on Earth EarthTerrain.cellFieldLand() - EarthTerrainCheck's door. |
| 1476 | 1 | `public Geology geology()` | An Earth world's geology (0.7.117): its data's provinces, classes and multipliers; null on a generated world. |
| 1479 | 1 | `public int provinceAt(long x, long y)` | The province under a plot's centre on Earth, its row in the data (0 for none); 0 on a generated world (0.7.117). |
| 1482 | 1 | `public Geology.Kind geologyAt(long x, long y)` | The class of the ground under a plot's centre on Earth (0.7.117; Geology.Kind.OTHER where no province is); null on a generated world - what a field's type leans on (Deposit.type()). |
| 1485 | 4 | `public long cellFieldCount(int cell, Resource r)` | How many fields of a resource a cell lists (fieldsInCell(); since 0.7.99 the fewer and bigger ones). |
| 1491 | 4 | `public long legacyCellFieldCount(int cell, Resource r)` | ...and how many of the old world's it held (to 0.7.98): a Poisson count, its mean the resource's density times the cell's land - what its total is drawn from, and what legacyFieldsInCell() lists. |
| 1496 | 4 | `private long countOf(int cell, Resource r)` |  |
| 1509 | 4 | `public double cellTotal(int cell, Resource r)` | What a cell holds of a resource, in whole tonnes (forest, whole cubic metres): its old count x MEAN_SITES x the amount a site x the cell's richness, drawn without drawing a field. |
| 1514 | 10 | `private double totalOf(int cell, Resource r)` |  |
| 1526 | 3 | `private double richnessOf(int cell, Resource r)` | A cell's richness for a resource, RICHNESS_MIN to + RICHNESS_SPAN: the draw its total is struck at (totalOf()), and since 0.7.99 the richness of a cluster standing in it. |
| 1538 | 4 | `public List<Deposit> legacyFieldsInCell(int cell, Resource r)` | A cell's fields of a resource as the old world drew them (to 0.7.98), from the cell's own stream: each one's centre (drawn again, up to SEA_REDRAWS times, while it is in the sea - but oil's), its sites, and its share ... |
| 1543 | 33 | `private List<Deposit> legacyFieldsOf(int cell, Resource r)` |  |

### FEWER AND BIGGER DEPOSITS: THE POOLS AND THEIR CLUSTERS (0.7.99; the (lines 1577-1918)

| line | len | member | says |
|---:|---:|---|---|
| 1584 | 8 | `static { ... }` |  |
| 1599 | 1 | `public static int poolCells(Resource r)` | The side of a resource's pool, in cells (0.7.99): the fewest whose square of land holds POOL_CLUSTERS clusters on the mean, POOL_CLUSTERS x CLUSTER_FIELDS x FIELD_SCALE of the old world's fields - iron 2, oil 4, stone... |
| 1602 | 4 | `public static int poolsOnSide(Resource r)` | A resource's pools on the world's side: CELLS over poolCells(), rounded up (the last a part pool); 0 for forest. |
| 1613 | 3 | `public static double clusterKm(Resource r)` | A cluster's radius for a resource, in km (0.7.99): the disc that holds CLUSTER_FIELDS fields at the old world's density, sqrt(CLUSTER_FIELDS / (pi x its fields a km2)) - iron 4.0, oil 12.6, stone 17.8, coal 39.9, copp... |
| 1618 | 16 | **type** `private static final class Pool` | A pool's fields as drawn: each one's centre plot, sites and amount, in the order drawn; the pool's total and its old count. |
| 1625 | 8 | `Pool(long[] xs, long[] ys, int[] sites, double[] amounts, double total, long old)` _(in World.Pool)_ |  |
| 1646 | 12 | `private Pool pool(Resource r, int px, int py)` | A pool, drawn once and kept: its place in pools (px east, py south, each 0 to poolsOnSide() - 1). |
| 1660 | 3 | `private long poolHash(Resource r, int px, int py)` | A pool's stream for a resource. |
| 1665 | 5 | `static int atLeastOne(long count, int per, double u)` | `count` over `per`, its fraction a draw (u under it adds one), at least one when `count` is not nought: how many fields N old ones make, and how many clusters those fields lie in. |
| 1682 | 87 | `private Pool drawPool(Resource r, int px, int py)` | Draws a pool (0.7.99): its cells' old counts and totals, row by row; its fields, N / FIELD_SCALE of the old count's N; its clusters, one for every CLUSTER_FIELDS fields, each centred in a cell drawn as its share of th... |
| 1771 | 5 | `public static int[] poolOf(int cell, Resource r)` | The pool holding a cell, as {px, py}, for a resource; null for forest or off the world. |
| 1778 | 12 | `public double poolTotal(Resource r, int px, int py)` | What a pool shares among its fields: its cells' totals added (cellTotal()), whole tonnes, row by row - without drawing a field. |
| 1792 | 8 | `public long poolFieldCount(Resource r, int px, int py)` | How many fields a pool holds (fewer and bigger, 0.7.99) - computed from its cells' old counts without drawing them. |
| 1802 | 13 | `public long poolLegacyCount(Resource r, int px, int py)` | ...and how many of the old world's fields its cells held: their old counts added. |
| 1817 | 5 | `public long fieldCount(Resource r)` | The fields of a resource the whole world holds (0.7.99): every pool's, from their old counts, without drawing one. |
| 1824 | 6 | `public long legacyFieldCount(Resource r)` | ...and the old world's: every cell's old count. |
| 1832 | 15 | `public List<Deposit> poolFields(Resource r, int px, int py)` | A pool's fields, each as the cell its centre is in lists it (fieldsInCell()), in the order the pool drew them; one not found there is left out. |
| 1855 | 4 | `public List<Deposit> fieldsInCell(int cell, Resource r)` | A cell's fields of a resource (since 0.7.99, FEWER AND BIGGER DEPOSITS): every field of every pool whose clusters reach the cell whose centre plot is in it, pool by pool (north to south, west to east) in the order eac... |
| 1860 | 21 | `private List<Deposit> fieldsOf(int cell, Resource r)` |  |
| 1890 | 6 | `public double[] totals()` | The world's totals, computed once and kept (0.7.57): computeTotals()'s figures, a copy - what a city founded or converted on this world stores, so a load needs no pass (spec-land 2.1). |
| 1904 | 14 | `public double[] computeTotals()` | The world's totals, a resource at a time in Resource's order: every cell's cellTotal(), summed in cell order - one pass over 135,424 cells, about 30 ms, the same every time. |

