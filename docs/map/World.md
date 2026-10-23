# World.java - 2,028 lines · 108 methods · 97 constants · model

`ham/citybuildersim/World.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

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
> screen founds a city on it (EarthSite, Founding.onEarth()). Since 0.8.12
> (batch EG1; spec-earth2-terrain 8.5) its oil follows the known petroleum,
> its iron the shields and recorded districts, its fields are a third as
> many and three times wider than a generated world's, and a cluster stands
> in its province's own cells (EARTH_FIELD_SCALE, drawPool()): a generated
> ... (1 more lines in the source)

**Uses:** [Resource](Resource.md) (38), [Deposit](Deposit.md) (15), [EarthTerrain](EarthTerrain.md) (6), [Geology](Geology.md) (2)

**Used by (73):** [BoatSchedule](BoatSchedule.md), [BuildCardCheck](BuildCardCheck.md), [BuildingVisual](BuildingVisual.md), [CityLand](CityLand.md), [CityMap](CityMap.md), [CityRuns](CityRuns.md), [CityShore](CityShore.md), [CoalMiningCheck](CoalMiningCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [DataSave](DataSave.md), [Deposit](Deposit.md), [DistrictPicture](DistrictPicture.md), [DistrictPlan](DistrictPlan.md), [EarthCheck](EarthCheck.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthProj](EarthProj.md), [EarthSite](EarthSite.md), [EarthTerrain](EarthTerrain.md), [EarthTerrainCheck](EarthTerrainCheck.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [Gateways](Gateways.md), [GlobeCheck](GlobeCheck.md), [GlobeFrame](GlobeFrame.md), [GlobePicture](GlobePicture.md), [GridCheck](GridCheck.md), [GridConversion](GridConversion.md), [GridOffers](GridOffers.md), [InterchangeGround](InterchangeGround.md), [JunctionCheck](JunctionCheck.md), [LandCheck](LandCheck.md), [LandConversion](LandConversion.md), [LandGrid](LandGrid.md), [LandManager](LandManager.md), [LandMap](LandMap.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LegacyLand](LegacyLand.md), [LongPlaytest](LongPlaytest.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapFrame](MapFrame.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [MiniMapFrame](MiniMapFrame.md), [MiningCheck](MiningCheck.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [PlanCheck](PlanCheck.md), [PortCheck](PortCheck.md), [QuarryingCheck](QuarryingCheck.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SeaRoutes](SeaRoutes.md), [ShipShapes](ShipShapes.md), [TilePainter](TilePainter.md), [TileRaster](TileRaster.md), [TrainSchedule](TrainSchedule.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckSchedule](TruckSchedule.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WorldCheck](WorldCheck.md), [ZoningCheck](ZoningCheck.md)

## Sections

| line | section |
|---:|---|
| 76 | THE GRID |
| 104 | · the terrain's classes |
| 144 | THE TERRAIN: VALUE NOISE IN OCTAVES (spec-land 2.1) |
| 225 | · the sea's depth (0.7.79, batch O3; docs/studies/spec-oil.md 2.7) |
| 242 | THE FOUNDING SITE (spec-land 2.1) |
| 298 | THE FOUNDING LAKE AND RIVER (the mockup's random walk, in plots) |
| 369 | THE DEPOSITS (spec-land 2.1; the densities and sizes are Resource's) |
| 423 | FEWER AND BIGGER DEPOSITS (0.7.99, batch W1; Jerus, 2026-10-08: "the |
| 471 | FEWER, BIGGER AND IN THEIR OWN PROVINCE, ON EARTH (0.8.12, batch EG1; |
| 520 | THE WORLD, ITS STATE |
| 659 | THE NOISE |
| 738 | THE SEA PASS: the sea's level, and each cell's land and forest |
| 848 | THE FOUNDING SITE |
| 994 | THE FOUNDING LAKE AND RIVER |
| 1108 | THE TERRAIN, A PLOT, A TILE OR A REGION AT A TIME |
| 1453 | THE DEPOSITS |
| 1615 | FEWER AND BIGGER DEPOSITS: THE POOLS AND THEIR CLUSTERS (0.7.99; the |
| 1683 | · EG1's counters: how often the Earth rules fire (they feed no draw) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 534 | `World.Kind.GENERATED` |  |
| 534 | `World.Kind.EARTH` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `World.PLOT_M` | `30` | A plot's side, in metres: 30, the map's smallest square and the mockup's cell. |
| 84 | `World.KM2_PER_PLOT` | `PLOT_M * PLOT_M / 1e6` | A plot's area in square kilometres: 30 m squared, 0.0009. |
| 87 | `World.TILE` | `32` | Plots on a tile's side: 32, 0.96 km - the unit the map paints (spec-land 2.6). |
| 90 | `World.DISTRICT` | `256` | Plots on a district's side: 256, 7.68 km - the unit the map stores (spec-land 2.5). |
| 93 | `World.CELL` | `2048` | Plots on a world cell's side: 2,048, 61.44 km - the unit the world's totals and far view are counted in, so plot, tile, district and cell nest by powers of two (spec-land star 4). |
| 96 | `World.CELLS` | `368` | World cells on the world's side: 368, so the world is 511.2 million km2, within 0.2% of the Earth's 510.1 million. |
| 99 | `World.SIDE` | `(long) CELL * CELLS` | Plots on the world's side: 2,048 x 368, 753,664 (22,610 km). |
| 102 | `World.CELL_KM2` | `(CELL * PLOT_M / 1000) *(CELL * PLOT_M / 1000)` | A world cell's area in square kilometres: 61.44 km squared, 3,774.87. |
| 107 | `World.GRASS` | `0` | Open ground: dry and buildable. |
| 110 | `World.FOREST` | `1` | Forest: dry and buildable - a building on it clears it, and the forest law may keep it from building (LandManager, ZONES AND THE FOREST LAW, 0.7.107) - and standing timber (Resource.FOREST). |
| 113 | `World.FRESH` | `2` | Fresh water: a lake, or the founding river. |
| 116 | `World.SALT` | `3` | Salt water: the sea, and everything beyond the world's edge. |
| 119 | `World.SAND` | `4` | Beach: dry land within BEACH_BAND of the sea's level. |
| 122 | `World.ICE` | `5` | Ice (0.7.127, batch E7; spec-earth's E14, docs/studies/spec-globe.md 3.9): land under snow and ice - never dry, never built on, never a founding site - counted in a holding's whole area only. |
| 125 | `World.DESERT` | `6` | Desert (0.7.127, batch E7; spec-globe Q1): dry ground every rule treats as grass - only its colour and its name differ. |
| 128 | `World.CLASSES` | `7` | How many classes the ground has: 7, GRASS to DESERT - the length of an array a class indexes (0.7.127; five until then, and a generated world draws only those). |
| 131 | `World.DRY_CLASSES` | `1<<GRASS \| 1<<FOREST \| 1<<SAND \| 1<<DESERT` | The dry classes, a bit each: grass, forest, the beach and the desert. |
| 156 | `World.OCTAVE_FALLOFF` | `0.55` | Each octave's amplitude against the one above it: 0.55, a little rougher than classic fractal noise's half (the mockup's terrain, scaled to plots). |
| 159 | `World.ELEV_COARSE_HI` | `16` | Elevation's coarse octaves run from 2^16 plots (1,966 km): continents and oceans. |
| 162 | `World.ELEV_COARSE_LO` | `9` | ...down to 2^9 plots (15 km): coasts and bays. |
| 165 | `World.ELEV_FINE_HI` | `8` | Elevation's fine octaves run from 2^8 plots (7.7 km)... |
| 168 | `World.ELEV_FINE_LO` | `4` | ...down to 2^4 plots (480 m): headlands, coves and islets. |
| 171 | `World.ELEV_COARSE_WEIGHT` | `0.85` | The coarse octaves' share of the elevation: 0.85, so the coast is the continents' and the fine octaves only fray it. |
| 174 | `World.ELEV_FINE_WEIGHT` | `0.15` | The fine octaves' share: the rest, 0.15. |
| 177 | `World.LAKE_HI` | `10` | The lake field's octaves run from 2^10 plots (30.7 km)... |
| 180 | `World.LAKE_LO` | `5` | ...down to 2^5 plots (960 m). |
| 183 | `World.FOREST_HI` | `8` | The forest field's octaves run from 2^8 plots (7.7 km)... |
| 186 | `World.FOREST_LO` | `5` | ...down to 2^5 plots (960 m). |
| 189 | `World.SEA_SHARE` | `0.71` | The share of the world that is sea: 71%, the Earth's. |
| 201 | `World.SEA_SAMPLES` | `4` | Elevation samples a world cell the sea's level is found from: 4, one at a hashed point in each quarter of the cell (541,696 in all). |
| 204 | `World.LAKE_SHARE` | `0.037` | Lakes' share of land: 3.7%, of the Earth's non-glaciated land (Verpoorter 2014). |
| 214 | `World.LAKE_THETA` | `0.7092` | The lake field's level from which ground is a lake: 0.7092, the 96.3rd percentile of the field over land (LAKE_SHARE) - measured 0.7088 to 0.7095 over five seeds, a million points each, off the lattice; a constant, so... |
| 217 | `World.FOREST_SHARE` | `0.31` | Forest's share of land: 31% (FAO 2020). |
| 220 | `World.FOREST_THETA` | `0.5676` | The forest field's level from which land is forest: 0.5676, the 69th percentile of the field over land (FOREST_SHARE), measured 0.5672 to 0.5679 as LAKE_THETA was (the design's 0.595 makes 24% of land forest). |
| 223 | `World.BEACH_BAND` | `0.0015` | How far above the sea's level, in the elevation field, land is beach: 0.0015. |
| 237 | `World.SHELF_SHARE` | `0.0886` | The continental shelf's share of the sea: 8.86% (est., the shelf's share of the ocean's area, Harris et al. |
| 240 | `World.SHELF_BREAK_M` | `140` | The depth of the shelf's edge, in metres: 140 (est., the mean shelf break - spec-oil 2.7 and 6, to confirm); depthAt() is this at shelfTheta(). |
| 258 | `World.SITE_CELL_LAND_MIN` | `1` | The least land, in quarters of a cell's SEA_SAMPLES, a cell needs to be searched for a site: 1, 25%. |
| 261 | `World.SITE_CELL_LAND_MAX` | `3` | The most: 3, 75% - a coast, not open sea or an interior. |
| 264 | `World.SITE_STEP` | `17` | The spiral's step: 17 plots (510 m) between rings, and the ring's points about that far apart (six a ring per ring). |
| 267 | `World.SITE_RINGS` | `70` | Rings in a cell's spiral: 70, out to 1,173 plots (35 km), past the cell's own half-width of 1,024. |
| 270 | `World.SITE_DRY_PLOTS` | `20` | Test 1: the site and eight points this far round it are dry ground, in plots: 20 (600 m), room for a new city's centre - since 0.7.67 rings of 120 m blocks round the site's own, on the default world 21 of them, 0.30 k... |
| 273 | `World.SITE_LAND_SHARE` | `0.6` | Test 2: of SITE_LAND_SAMPLES points within 5 km, at least this share are land: 60%. |
| 276 | `World.SITE_LAND_SAMPLES` | `48` | ...over 48 points, at 1.5, 3 and 5 km in turn round the compass. |
| 279 | `World.SITE_LAND_RADII` | `{ 50, 100, 167 }` | ...at these radii, in plots: 50, 100 and 167 (1.5, 3 and 5 km). |
| 282 | `World.SITE_SEA_NOT_WITHIN` | `45` | Test 3: no sea within this many plots in 16 directions: 45 (1.35 km). |
| 285 | `World.SITE_SEA_WITHIN` | `70` | ...but sea within this many in at least one: 70 (2.1 km) - a coast with room for a town. |
| 296 | `World.SITE_IRON_KM` | `2` | Test 4 (spec-land star), the conversion's alone since 0.7.99: an iron field's centre within this many km of the old world's fields (legacyFieldsInCell()) - the mockup put deposits near the site "so the first mines com... |
| 307 | `World.RIVER_SEA_DIRECTIONS` | `32` | Directions the sea is looked for in, round the site: 32. |
| 310 | `World.RIVER_SEA_REACH` | `100` | ...out to this many plots, from SITE_SEA_NOT_WITHIN: 100 (3 km). |
| 313 | `World.RIVER_SEA_STEP` | `5` | ...in steps of this many plots: 5. |
| 316 | `World.RIVER_LAKE_TRIES` | `32` | Tries at a lake whose shore is dry: 32. |
| 319 | `World.RIVER_LAKE_FAN` | `1.7` | The lake's bearing from the site: opposite the sea, give or take half of this many radians: 1.7. |
| 322 | `World.RIVER_LAKE_NEAR` | `64` | The lake's centre this many plots from the site, at the least: 64 (1.9 km)... |
| 325 | `World.RIVER_LAKE_SPAN` | `26` | ...plus up to this many: 26, so 64 to 90 plots (1.9 to 2.7 km). |
| 328 | `World.LAKE_R_MIN` | `11` | The lake's radius in plots, at the least: 11 (330 m)... |
| 331 | `World.LAKE_R_SPAN` | `6` | ...plus up to this many: 6, so 11 to 17 plots. |
| 334 | `World.LAKE_SHORE` | `1.6` | The lake's shore is tested this many radii out: 1.6... |
| 337 | `World.LAKE_SHORE_RISE` | `0.003` | ...for ground this far above the sea's level: 0.003, two beaches. |
| 340 | `World.RIVER_PASS_MIN` | `8` | The river passes the site at least this many plots to one side: 8 (240 m)... |
| 343 | `World.RIVER_PASS_SPAN` | `12` | ...plus up to this many: 12, so 8 to 20 plots. |
| 346 | `World.RIVER_PASS_REACHED` | `10` | Within this many plots of its passing point the river turns for the sea: 10. |
| 349 | `World.RIVER_STEP` | `3` | Plots the river moves a step: 3 (90 m). |
| 352 | `World.RIVER_WANDER` | `0.75` | Its heading wanders by up to half this many radians a step: 0.75... |
| 355 | `World.RIVER_STEER` | `0.25` | ...and turns this share of the way to its target a step: 0.25. |
| 358 | `World.RIVER_MAX_STEPS` | `500` | The most steps it takes: 500 (45 km). |
| 361 | `World.RIVER_MOUTH` | `0.002` | It ends where the elevation is this far under the sea's level: 0.002. |
| 364 | `World.RIVER_HALF_WIDTH` | `1.0` | Its half-width at the lake, in plots: 1 (a 60 m river)... |
| 367 | `World.RIVER_WIDENS` | `0.95` | ...growing by this many plots to the sea: 0.95, so 1.95 at the mouth (117 m). |
| 400 | `World.FIELD_TAIL` | `1.5` | How heavy the tail of a field's size is: 1.5, so P(sites >= k) = k^-1.5 - most small, a few huge. |
| 403 | `World.LEGACY_MAX_SITES` | `512` | The most sites one of the old world's fields holds: 512 (the cap of the tail's draw, sites()). |
| 406 | `World.MEAN_SITES` | `meanSites()` | The mean sites one of the old world's fields: the sum of k^-1.5 for k = 1 to LEGACY_MAX_SITES, 2.524 - exact for the capped tail, since P(sites >= k) = k^-1.5. |
| 409 | `World.RICHNESS_MIN` | `0.6` | A cell's richness, its total against its count's mean, at the least: 0.6... |
| 412 | `World.RICHNESS_SPAN` | `0.8` | ...plus up to this: 0.8, so 0.6 to 1.4, with a mean of one. |
| 415 | `World.SEA_REDRAWS` | `8` | Times a field centred in the sea is drawn again: 8, since ore lies under land. |
| 418 | `World.POISSON_NORMAL_ABOVE` | `40` | Above this mean a cell's count is drawn as a rounded normal rather than by inversion: 40. |
| 421 | `World.FOREST_M3_PER_KM2` | `13_700` | Standing timber a square kilometre of forest, in cubic metres: 13,700 (FAO 2020: 557 billion m3 on 4.06 billion hectares). |
| 469 | `World.FIELD_SCALE` | `10` | How many of the old world's fields one field stands for: 10 (Jerus, 2026-10-08: "a tenth as many fields, each ten times bigger", the world's totals as they were). |
| 500 | `World.EARTH_FIELD_SCALE` | `3` | On Earth a field stands for FIELD_SCALE x this of the old world's fields: three, so the Earth's fields are a third as many, each three times wider (EG1; Jerus, 2026-10-08, "huge and rare"; est.). |
| 503 | `World.MAX_SITES` | `FIELD_SCALE * EARTH_FIELD_SCALE * LEGACY_MAX_SITES` | The most sites one field holds, on Earth: FIELD_SCALE x EARTH_FIELD_SCALE of the old world's largest, 15,360 (a generated world's, 5,120, are its first places). |
| 506 | `World.CLUSTER_FIELDS` | `10` | The fields a cluster holds on the mean: 10 (★W1-2, est.: Jerus's "very big clusters" - ten fields a cluster, as a field is ten of the old). |
| 509 | `World.POOL_CLUSTERS` | `10` | The clusters a pool of land holds on the mean, at the least: 10 (★W1-2, est.): enough that a pool's clusters lie where the draws put them, not one a pool on a lattice - a pool a quarter land still holds two or three. |
| 512 | `World.FIELD_INDEX_FROM` | `1<<16` | The first number a field is listed under in its cell: 65,536, past any old world's field's (a cell held at most 868 of them on the default world, iron's). |
| 515 | `World.POOLS_KEPT` | `256` | Pools kept, by resource and place: 256 (an iron pool is about 300 fields, some 15 KB). |
| 518 | `World.POOL_SALT` | `0x5EB0C1A5L` | The stream a pool is drawn from. |
| 525 | `World.WORLDS_KEPT` | `4` | How many worlds World.of() keeps: 4, the city's and a few the founding screen looked at (about 0.3 MB each). |
| 527 | `World.KEPT` | `new LinkedHashMap<>(8, 0.75f, true) { @ Override protected boolean removeElde...` |  |
| 561 | `World.KEPT_EARTH` | `new LinkedHashMap<>(8, 0.75f, true) { @ Override protected boolean removeElde...` | Earth worlds kept as World.of(key) is asked for them: WORLDS_KEPT, apart from the generated, so those are kept as they were. |
| 689 | `World.F_ELEV` | `0x1111, F_ELEV_FINE = F_ELEV ^ 0x55, F_LAKE = 0x2222, F_FOREST = 0x3333` | The fields' keys: what makes one field's octaves another's. |
| 692 | `World.AMP` | `new double [ ELEV_COARSE_HI + 1 ]` | OCTAVE_FALLOFF to the power n, by repeated multiplication - the order fbm() has always summed in. |
| 708 | `World.NORM_COARSE` | `norm(ELEV_COARSE_HI, ELEV_COARSE_LO), NORM_FINE = norm(ELEV_FINE_HI, ELEV_FIN...` |  |
| 1134 | `World.SMOOTH` | `new double [ ELEV_COARSE_HI + 1 ][]` | The smoothstep at a plot's centre within an octave's lattice cell, for every wavelength 2^k: SMOOTH[k][f] for f = 0 .. |
| 1153 | `World.SCRATCH` | `ThreadLocal.withInitial(TileScratch : : new)` |  |
| 1295 | `World.LineScratch.CORNERS` | `64` | The corners it keeps: 64 slots, picked by the top six bits of the corner's hash. |
| 1312 | `World.LINE_SCRATCH` | `ThreadLocal.withInitial(LineScratch : : new)` | Each thread's LineScratch, made on its first line. |
| 1621 | `World.POOL_SIDE` | `new int [ Resource.values().length ]` | Each resource's pool side in cells, in Resource's order (poolCells()). |
| 1674 | `World.NO_POOL` | `new Pool(new long [ 0 ], new long [ 0 ], new int [ 0 ], new double [ 0 ], 0, 0)` | The pool of a place whose land cells hold no old draw of the resource: no field, no tonnes. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 567 | `private final long seed` |  |
| 568 | `private final Key key` |  |
| 570 | `private final EarthTerrain earth` | An Earth world's ground (0.7.115); null on a generated world. |
| 571 | `private volatile boolean built` |  |
| 573 | `private double seaTheta` |  |
| 575 | `private double shelfTheta` | The shelf's edge in the elevation field (0.7.79): see shelfTheta(). |
| 577 | `private byte[] landQuarters, forestQuarters` | Each cell's land and forest, in quarters (of its SEA_SAMPLES). |
| 578 | `private long fx, fy` |  |
| 579 | `private int siteChecks, siteCells` |  |
| 580 | `private double[] riverX, riverY, riverW` |  |
| 581 | `private double lakeX, lakeY, lakeR` |  |
| 582 | `private double riverBox0x, riverBox0y, riverBox1x, riverBox1y` |  |
| 1148 | `final double[] e` |  |
| 1149 | `final double[] lat` |  |
| 1150 | `int[] segs` |  |
| 1291 | `final double[] e` |  |
| 1292 | `final double[] lat` |  |
| 1293 | `int[] segs` |  |
| 1296 | `final long[] cornerX` |  |
| 1297 | `final boolean[] cornerSet` |  |
| 1298 | `final double[][] cornerV` |  |
| 1657 | `final long[] xs, ys` |  |
| 1658 | `final int[] sites` |  |
| 1659 | `final double[] amounts` |  |
| 1660 | `final double total` |  |
| 1661 | `final long old` |  |
| 1677 | `private final Map<Long, Pool> pools` | The pools drawn, kept: by resource and place, the last POOLS_KEPT asked for. |
| 1685 | `private final AtomicLong centresByWeight` |  |
| 1686 | `private final AtomicLong fieldRedraws` |  |
| 1993 | `private double[] totals` | The totals once computed (0.7.57): what every city founded on this world stores. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 74 | 1955 | **type** `public final class World` | The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and river, and the fields of the seven resources - every one of them a pure ... |

### THE GRID (lines 76-103)

### the terrain's classes (lines 104-143)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 3 | `public static boolean dry(int cls)` | Whether a class is dry ground - grass, forest, the beach or the desert: what buildings stand on, the founding's test 1 asks for and a holding's dry area counts. |

### THE TERRAIN: VALUE NOISE IN OCTAVES (spec-land 2.1) (lines 144-224)

### the sea's depth (0.7.79, batch O3; docs/studies/spec-oil.md 2.7) (lines 225-241)

### THE FOUNDING SITE (spec-land 2.1) (lines 242-297)

### THE FOUNDING LAKE AND RIVER (the mockup's random walk, in plots) (lines 298-368)

### THE DEPOSITS (spec-land 2.1; the densities and sizes are Resource's) (lines 369-422)

### FEWER AND BIGGER DEPOSITS (0.7.99, batch W1; Jerus, 2026-10-08: "the (lines 423-470)

### FEWER, BIGGER AND IN THEIR OWN PROVINCE, ON EARTH (0.8.12, batch EG1; (lines 471-519)

### THE WORLD, ITS STATE (lines 520-658)

| line | len | member | says |
|---:|---:|---|---|
| 534 | 1 | **type** `public enum Kind` | Which world a key names: the generated one its seed makes, or the real Earth round a spot (0.7.115). |
| 544 | 15 | **type** `public record Key(Kind kind, long seed, double lat, double lon, String data)` | What makes one world another (0.7.115, batch E2; spec-earth 6): its kind and seed, and on Earth the spot the projection is centred on (degrees) and the data it is drawn from, by its identity (EarthData.identity(), "ea... |
| 547 | 1 | `public static Key generated(long seed)` _(in World.Key)_ | The generated world a seed makes. |
| 550 | 5 | `public static Key earth(long seed, double lat, double lon, String data)` _(in World.Key)_ | The real Earth round (lat, lon), its detail from the seed, read from the data of that identity. |
| 557 | 1 | `public boolean earth()` _(in World.Key)_ | Whether it is an Earth world's. |
| 585 | 5 | `public World(long seed)` | A world from its seed. |
| 592 | 5 | `public World(Key key)` | The world a key names (0.7.115): a generated key's is new World(seed); an Earth key's finds its data now (EarthTerrain.source()) and is built, like any, when first asked. |
| 599 | 5 | `public static World of(long seed)` | The world a seed makes, shared: the same object for the same seed while it is among the last WORLDS_KEPT asked for. |
| 606 | 6 | `public static World of(Key key)` | The world a key names, shared (0.7.115): a generated key's is World.of(its seed), the same object; an Earth key's is kept apart, the last WORLDS_KEPT asked for. |
| 614 | 1 | `public long seed()` | The seed it was made from. |
| 617 | 1 | `public Key key()` | Its key (0.7.115): Key.generated(seed()) for a generated world. |
| 620 | 1 | `public boolean isEarth()` | Whether it is the real Earth (0.7.115). |
| 623 | 1 | `EarthTerrain earthTerrain()` | An Earth world's ground, null on a generated world (0.7.115): EarthTerrainCheck's door. |
| 626 | 27 | `private void build()` | The sea pass, the site and the river, once - on Earth (0.7.115) the cells pass and the snap, and no river. |
| 655 | 3 | `private void noHeightsOnEarth(String what)` | What a generated world's heights answer and an Earth world has none of (earth-1 holds no heights, Jerus's Q8): refused there. |

### THE NOISE (lines 659-737)

| line | len | member | says |
|---:|---:|---|---|
| 664 | 6 | `static long mix(long z)` | SplitMix64's finaliser - LandMarket's scramble: every value in the world is this of the seed and a place. |
| 672 | 1 | `static double unit(long h)` | A hash as a number in [0, 1), from its top 53 bits. |
| 675 | 3 | `static double lattice(long s, long ix, long iy)` | The value at one lattice point of one octave. |
| 680 | 7 | `static double vnoise(long s, double x, double y)` | Value noise at (x, y) in lattice units, smoothstep-interpolated. |
| 693 | 4 | `static { ... }` |  |
| 699 | 1 | `static double amp(int hi, int k)` | An octave's amplitude under a field's top octave. |
| 702 | 5 | `static double norm(int hi, int lo)` | The sum of a field's amplitudes, top octave first. |
| 712 | 1 | `private long oct(long field, int k)` | One octave's stream. |
| 715 | 10 | `double fbm(long field, double x, double y, int hi, int lo)` | A field at (x, y), in plots: octaves hi to lo, normalised to 0..1. |
| 727 | 4 | `double elevation(double x, double y)` | The elevation at a point, in plots: below seaTheta() is sea. |
| 733 | 1 | `double lakeField(double x, double y)` | The lake field at a point: LAKE_THETA and over, on land, is a lake. |
| 736 | 1 | `double forestField(double x, double y)` | The forest field at a point: FOREST_THETA and over, on dry land, is forest. |

### THE SEA PASS: the sea's level, and each cell's land and forest (lines 738-847)

| line | len | member | says |
|---:|---:|---|---|
| 742 | 30 | `private void seaPass()` |  |
| 774 | 1 | `public double seaTheta()` | The sea's level: the elevation SEA_SHARE of the world's samples lie under; on Earth (0.7.115), with no heights, EarthTerrain.SEA_LEVEL, the share the coast is drawn at. |
| 777 | 1 | `public double shelfTheta()` | The continental shelf's edge in the elevation field (0.7.79): the level the shallowest SHELF_SHARE of the sea's samples lie at and above, so under seaTheta(). |
| 792 | 6 | `public double depthAt(long x, long y)` | The sea's depth at a plot's centre, in metres below its level (0.7.79, batch O3; spec-oil 2.7): 0 where the plot is not sea (land, beach, a lake or the river - on the world terrainAt() is SALT exactly where it is over... |
| 800 | 5 | `public double cellLandShare(int cell)` | A cell's land, as a share of its samples: 0, 0.25, 0.5, 0.75 or 1 - on Earth (0.7.115) its cells pass's share, of 16 points. |
| 807 | 5 | `public double cellForestShare(int cell)` | A cell's forest, as a share of its samples (on Earth, its cells pass's). |
| 814 | 11 | `public double landShare()` | The world's land, as a share of every cell's samples: 29%, by the sea's level's definition - on Earth, its cells' shares averaged. |
| 827 | 3 | `private boolean hasLand(int c)` | Whether a cell holds land, which its fields are drawn on: on Earth (0.7.115) its cells pass's share over nothing. |
| 832 | 4 | `private boolean holds(int c, Resource r)` | Whether a cell holds ground a resource's fields are drawn on: its land (hasLand()); on Earth (0.7.117) its field land, oil's with a shelf's, basin's or rift's sea, and forest's its land. |
| 838 | 3 | `private boolean seaAt(double x, double y)` | Whether a point is sea, for a field drawn off it: the elevation under the sea's level - on Earth (0.7.115) the coast's rule at the point (EarthTerrain.seaAt()). |
| 843 | 4 | `public static int cellOf(long x, long y)` | The world cell a plot is in, as row x CELLS + column; -1 off the world. |

### THE FOUNDING SITE (lines 848-993)

| line | len | member | says |
|---:|---:|---|---|
| 852 | 29 | `private void found()` |  |
| 883 | 1 | `private boolean dryAt(double x, double y)` | Dry ground at a point: land, and not a lake. |
| 886 | 4 | `private boolean siteOk(long px, long py)` | A founding's tests, cheapest first, at the centre of plot (px, py) - the first three since 0.7.99 (no iron) - counted in siteChecks(). |
| 896 | 34 | `private boolean siteTests(long px, long py, Map<Integer, List<Deposit>> iron)` | ...and uncounted: tests 1 to 3, and with `iron` (the old world's iron fields by cell, filled as they are asked for) the fourth, for the conversion's search (searchSites()). |
| 944 | 34 | `public long[] searchSites(int cells, java.util.function.Predicate<long[]> accept)` | THE FOUNDING SEARCH AGAIN, A CELL AT A TIME (0.7.57, batch J1b): in each of the nearest `cells` coastal cells, in the founding search's order, the first plot of its spiral that passes the four tests is offered to `acc... |
| 980 | 1 | `static int siteLandMin()` | Test 2's least count of land samples: SITE_LAND_SHARE of SITE_LAND_SAMPLES, rounded up - 29 of 48. |
| 983 | 1 | `public long foundingX()` | The founding site, as a plot: east from the world's west edge. |
| 986 | 1 | `public long foundingY()` | ...and south from its north edge. |
| 989 | 1 | `public int siteChecks()` | How many plots the founding search tested - the cost of finding the site (on Earth, the snap's). |
| 992 | 1 | `public int siteCells()` | ...and in how many cells: one, unless the nearest coastal cell had no site (on Earth, one). |

### THE FOUNDING LAKE AND RIVER (lines 994-1107)

| line | len | member | says |
|---:|---:|---|---|
| 998 | 65 | `private void walkRiver()` |  |
| 1065 | 12 | **type** `public record River(double[] xs, double[] ys, double[] halfWidths, double lakeX, double lakeY, double lakeR)` | The founding river and its lake, in plots: the walk's points and its half-width at each, and the lake's centre and radius. |
| 1068 | 1 | `public int points()` _(in World.River)_ | How many points the walk has. |
| 1071 | 5 | `public boolean same(River o)` _(in World.River)_ | Whether two rivers are the same walk to the last bit. |
| 1079 | 4 | `public River river()` | The founding river and lake (copies: nothing a caller does moves them). |
| 1085 | 7 | `private boolean inRiver(double x, double y)` | Whether a point is in the founding river. |
| 1094 | 7 | `private boolean onSegment(int k, double x, double y)` | Whether a point is within the river's half-width of its k-th step. |
| 1103 | 4 | `private boolean inLake(double x, double y)` | Whether a point is in the founding lake: within its radius (the mockup's halo past it never reaches the lake's level). |

### THE TERRAIN, A PLOT, A TILE OR A REGION AT A TIME (lines 1108-1452)

| line | len | member | says |
|---:|---:|---|---|
| 1113 | 6 | `private byte classOf(double x, double y, double e, double lake, double forest)` | The class of a point's ground, from its fields: SALT, FRESH, SAND, FOREST or GRASS. |
| 1121 | 11 | `public byte terrainAt(long x, long y)` | One plot's ground, at its centre: GRASS, FOREST, FRESH, SALT or SAND; SALT off the world. |
| 1135 | 10 | `static { ... }` |  |
| 1147 | 5 | **type** `private static final class TileScratch` | One thread's working arrays for a tile. |
| 1168 | 52 | `public void tileTerrain(long tx, long ty, byte[] out)` | One tile's ground, a byte a plot in rows (out[py x 32 + px]): what terrainAt() gives each of its plots, on 99.9 to 100% of them, five times faster - each fine octave's lattice is hashed once a tile, and the coarse oct... |
| 1231 | 57 | `public void lineTerrain(long tx, long ty, boolean column, int at, byte[] out)` | One row (or, `column`, one column) of a tile's ground, its `at`-th: the TILE plots tileTerrain() gives that line, byte for byte - the same corners, octaves and sums in the same order, evaluated on the line's plots alo... |
| 1290 | 20 | **type** `private static final class LineScratch` | One thread's working arrays for a line, and the tile corners it met last (their elevation's coarse octaves and lake's, as tileTerrain() reads them). |
| 1301 | 8 | `double[] corner(World w, long x, long y)` _(in World.LineScratch)_ | A tile corner's {elevation's coarse octaves, lake's corner octaves}, as tileTerrain()'s c00 and l00 at (x, y): kept by place and world. |
| 1315 | 8 | `private static void bilinearLine(double c00, double c10, double c01, double c11, double scale, boolean column, int at, double[]...` | bilinear() on one line of the tile's plots: acc[i] is the line's i-th plot's value. |
| 1325 | 19 | `private static void octaveLine(double[] lat, long os, int k, long x0, long y0, double amp, boolean column, int at, double[] acc)` | octaveTile() on one line of the tile's plots: the same lattice, the same sum at each of its plots. |
| 1346 | 9 | `private static void bilinear(double c00, double c10, double c01, double c11, double scale, double[] acc)` | Sets acc to scale x the bilinear blend of four corner values at each plot's centre. |
| 1357 | 20 | `private static void octaveTile(double[] lat, long os, int k, long x0, long y0, double amp, double[] acc)` | Adds amp x one octave's value noise at a tile's 32 x 32 plot centres: its lattice hashed once, the smoothstep from SMOOTH. |
| 1379 | 8 | `private double part(long field, double x, double y, int top, int lo)` | Octaves hi to lo of a field at a point, at the amplitudes fbm() gives them under the field's top octave, not normalised. |
| 1398 | 24 | `public void regionTerrain(long x0, long y0, int stride, int n, byte[] out)` | A region's ground at a stride, for the far view (spec-land 2.6, L2): n x n pixels from plot (x0, y0), each `stride` plots wide, classed at its centre (out[py x n + px]). |
| 1424 | 28 | `private void regionOctave(long field, int k, long x0, long y0, int stride, int n, double amp, double[] acc)` | Adds amp x one octave over a region's pixel centres, or amp x its mean, 0.5, when the octave is no wider than a pixel. |

### THE DEPOSITS (lines 1453-1614)

| line | len | member | says |
|---:|---:|---|---|
| 1458 | 5 | `private static double meanSites()` | The mean of the capped tail: P(sites >= k) summed over k = 1 .. |
| 1465 | 3 | `static int sites(double u)` | One of the old world's fields' sites from a uniform draw: floor((1-u)^(-1/FIELD_TAIL)), at most LEGACY_MAX_SITES - and since 0.7.99 each of the FIELD_SCALE draws a field's sites add up. |
| 1470 | 17 | `static long poisson(long h, double m)` | A Poisson count from a hash: by inversion for a small mean, a rounded normal above POISSON_NORMAL_ABOVE. |
| 1489 | 3 | `private long cellHash(int cell, Resource r)` | A cell's stream for a resource. |
| 1494 | 1 | `private double landKm2(int cell)` | A cell's land, in square kilometres: its land share times its area. |
| 1505 | 1 | `private double fieldKm2(int cell, Resource r)` | The ground a resource's fields are drawn on in a cell, km2: its land (landKm2()) on a generated world; on Earth (0.7.117, batch E3; spec-earth 4) its field land, the land at each of the cells pass's points times its p... |
| 1508 | 4 | `double cellFieldShare(int cell, Resource r)` | A cell's field land for a resource as a share of its area (0.7.117): its land share on a generated world, on Earth EarthTerrain.cellFieldLand() - EarthTerrainCheck's door. |
| 1514 | 1 | `public Geology geology()` | An Earth world's geology (0.7.117): its data's provinces, classes and multipliers; null on a generated world. |
| 1517 | 1 | `public int provinceAt(long x, long y)` | The province under a plot's centre on Earth, its row in the data (0 for none); 0 on a generated world (0.7.117). |
| 1520 | 1 | `public Geology.Kind geologyAt(long x, long y)` | The class of the ground under a plot's centre on Earth (0.7.117; Geology.Kind.OTHER where no province is); null on a generated world - what a field's type leans on (Deposit.type()). |
| 1523 | 4 | `public long cellFieldCount(int cell, Resource r)` | How many fields of a resource a cell lists (fieldsInCell(); since 0.7.99 the fewer and bigger ones). |
| 1529 | 4 | `public long legacyCellFieldCount(int cell, Resource r)` | ...and how many of the old world's it held (to 0.7.98): a Poisson count, its mean the resource's density times the cell's land - what its total is drawn from, and what legacyFieldsInCell() lists. |
| 1534 | 4 | `private long countOf(int cell, Resource r)` |  |
| 1547 | 4 | `public double cellTotal(int cell, Resource r)` | What a cell holds of a resource, in whole tonnes (forest, whole cubic metres): its old count x MEAN_SITES x the amount a site x the cell's richness, drawn without drawing a field. |
| 1552 | 10 | `private double totalOf(int cell, Resource r)` |  |
| 1564 | 3 | `private double richnessOf(int cell, Resource r)` | A cell's richness for a resource, RICHNESS_MIN to + RICHNESS_SPAN: the draw its total is struck at (totalOf()), and since 0.7.99 the richness of a cluster standing in it. |
| 1576 | 4 | `public List<Deposit> legacyFieldsInCell(int cell, Resource r)` | A cell's fields of a resource as the old world drew them (to 0.7.98), from the cell's own stream: each one's centre (drawn again, up to SEA_REDRAWS times, while it is in the sea - but oil's), its sites, and its share ... |
| 1581 | 33 | `private List<Deposit> legacyFieldsOf(int cell, Resource r)` |  |

### FEWER AND BIGGER DEPOSITS: THE POOLS AND THEIR CLUSTERS (0.7.99; the (lines 1615-1682)

| line | len | member | says |
|---:|---:|---|---|
| 1622 | 8 | `static { ... }` |  |
| 1637 | 1 | `public static int poolCells(Resource r)` | The side of a resource's pool, in cells (0.7.99): the fewest whose square of land holds POOL_CLUSTERS clusters on the mean, POOL_CLUSTERS x CLUSTER_FIELDS x FIELD_SCALE of the old world's fields - iron 2, oil 4, stone... |
| 1640 | 4 | `public static int poolsOnSide(Resource r)` | A resource's pools on the world's side: CELLS over poolCells(), rounded up (the last a part pool); 0 for forest. |
| 1651 | 3 | `public static double clusterKm(Resource r)` | A cluster's radius for a resource, in km (0.7.99): the disc that holds CLUSTER_FIELDS fields at the old world's density, sqrt(CLUSTER_FIELDS / (pi x its fields a km2)) - iron 4.0, oil 12.6, stone 17.8, coal 39.9, copp... |
| 1656 | 16 | **type** `private static final class Pool` | A pool's fields as drawn: each one's centre plot, sites and amount, in the order drawn; the pool's total and its old count. |
| 1663 | 8 | `Pool(long[] xs, long[] ys, int[] sites, double[] amounts, double total, long old)` _(in World.Pool)_ |  |

### EG1's counters: how often the Earth rules fire (they feed no draw) (lines 1683-2028)

| line | len | member | says |
|---:|---:|---|---|
| 1689 | 1 | `long clustersByWeight()` | Clusters whose centre was drawn among its cell's points by their weights, on Earth, since this world began drawing pools (EG1). |
| 1692 | 1 | `long centreRedraws()` | Times a cluster's centre was drawn again inside its point's square for lying on ground that holds none of the resource (EG1). |
| 1695 | 1 | `long centresOnNone()` | Clusters whose centre still lay on ground that holds none after SEA_REDRAWS draws again (EG1). |
| 1698 | 1 | `long fieldRedraws()` | Times a field was drawn again in its cluster's disc for lying on ground that holds none of the resource (EG1). |
| 1701 | 1 | `long fieldsOnNone()` | Fields that still lay on ground that holds none after SEA_REDRAWS draws again (EG1). |
| 1704 | 12 | `private Pool pool(Resource r, int px, int py)` | A pool, drawn once and kept: its place in pools (px east, py south, each 0 to poolsOnSide() - 1). |
| 1718 | 3 | `private long poolHash(Resource r, int px, int py)` | A pool's stream for a resource. |
| 1723 | 1 | `private int fieldScale()` | How many of the old world's fields one field stands for in this world: FIELD_SCALE on a generated world, FIELD_SCALE x EARTH_FIELD_SCALE on Earth (EG1). |
| 1726 | 5 | `static int atLeastOne(long count, int per, double u)` | `count` over `per`, its fraction a draw (u under it adds one), at least one when `count` is not nought: how many fields N old ones make, and how many clusters those fields lie in. |
| 1747 | 132 | `private Pool drawPool(Resource r, int px, int py)` | Draws a pool (0.7.99): its cells' old counts and totals, row by row; its fields, N / fieldScale() of the old count's N; its clusters, one for every CLUSTER_FIELDS fields, each centred in a cell drawn as its share of t... |
| 1881 | 5 | `public static int[] poolOf(int cell, Resource r)` | The pool holding a cell, as {px, py}, for a resource; null for forest or off the world. |
| 1888 | 12 | `public double poolTotal(Resource r, int px, int py)` | What a pool shares among its fields: its cells' totals added (cellTotal()), whole tonnes, row by row - without drawing a field. |
| 1902 | 8 | `public long poolFieldCount(Resource r, int px, int py)` | How many fields a pool holds (fewer and bigger, 0.7.99) - computed from its cells' old counts without drawing them. |
| 1912 | 13 | `public long poolLegacyCount(Resource r, int px, int py)` | ...and how many of the old world's fields its cells held: their old counts added. |
| 1927 | 5 | `public long fieldCount(Resource r)` | The fields of a resource the whole world holds (0.7.99): every pool's, from their old counts, without drawing one. |
| 1934 | 6 | `public long legacyFieldCount(Resource r)` | ...and the old world's: every cell's old count. |
| 1942 | 15 | `public List<Deposit> poolFields(Resource r, int px, int py)` | A pool's fields, each as the cell its centre is in lists it (fieldsInCell()), in the order the pool drew them; one not found there is left out. |
| 1965 | 4 | `public List<Deposit> fieldsInCell(int cell, Resource r)` | A cell's fields of a resource (since 0.7.99, FEWER AND BIGGER DEPOSITS): every field of every pool whose clusters reach the cell whose centre plot is in it, pool by pool (north to south, west to east) in the order eac... |
| 1970 | 21 | `private List<Deposit> fieldsOf(int cell, Resource r)` |  |
| 2000 | 6 | `public double[] totals()` | The world's totals, computed once and kept (0.7.57): computeTotals()'s figures, a copy - what a city founded or converted on this world stores, so a load needs no pass (spec-land 2.1). |
| 2014 | 14 | `public double[] computeTotals()` | The world's totals, a resource at a time in Resource's order: every cell's cellTotal(), summed in cell order - one pass over 135,424 cells, about 30 ms, the same every time. |

