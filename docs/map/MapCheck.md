# MapCheck.java - 11,650 lines · 300 methods · 117 constants · harnesses

`ham/citybuildersim/MapCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's street plan and paints the same pixels from the same inputs, the drawn raster itself is one street network with + junctions apart and every building within reach, the sidecar comes back byte for byte, and a screen costs what the screen holds, never what the city does - at Jerus's size and at five and ten billion people.
> 
> WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.5, 2.6 and
> 3). The map is the one part of the city nothing in the model reads, so
> nothing else would notice it drifting: districts that stopped adding up to
> the buildings, a building that jumped across the city when another was
> built, a tile that painted differently twice, a sidecar from another save
> read as this one's, or a screen that slowed as the city grew - the failure
> the design was drawn to avoid; and (J3b) a city of half a million drawn as
> scattered dots on a field, one dot for each apartment block, which is what
> J3's first render of Jerus's city showed. Headless: no JavaFX (the view is
> J4's).
> 
> What it has to prove:
>   1. a city's map adds up: every month of a 120-month city played as the
>      playtest plays it (land bought, built, demolished), its districts sum
>      to the model's count of every type and its pyramid's top to its
>      districts; a district recounted after a purchase holds what a map
>      drawn afresh measures - a whole iron field's sites among it, wherever
>      they lie (0.7.64); its mines stand on owned iron sites; a demolition
>      comes off the outermost district holding the type; and at every
>      DRAWN_EVERY-th month, every tile painted, the map draws exactly what
>      the city has - each type's buildings as many as the model's (since
>      0.7.88 those its plans could not hold packed at the city's edge), a
>      mine once on its site, its streets' surface kind by kind with what the
>      plans have no street for the model's road plots, every plot of its
>      track; and (0.8.3, batch FX2) in every one of those months no line to
>      the world starts on a plot the city owns or on water, none runs out
>      over ground that is not dry (at least TrainSchedule.WORLD_PLOTS, at
>      most CityMap.WORLD_REACH, ending where the land does), and no train
>      position of any route is on water off the track - the months that had
>      a line, and trains, are printed;
>   2. nothing placed moves: one more building changes one district's count
>      by one, inner types in the first district with room, outer types in
>      the last; and (0.7.88, spec-roads-and-ports.md 8.4; one tile's count
>      by one while the deal stood) in that district's plan it moves only the
>      buildings placed after it - where the plan's ladder takes the same
>      step and its first band is dealt round as many cells - and one fewer
>      likewise; and (2b, CL3) a district takes buildings month by month
>      until its used cells are no more than its room, PLAN_FILL of its
>      owned plots, and within one building's cells of it, the next going to
>      the next district; and a spill keeps its time: two districts' inputs
>      chained by hand, the second carried the pairs {month, type} the first
>      leaves, and when the first's log gains a later month's arrivals that
>      overflow too, the second's carry keeps its earlier pairs as they were
>      and every box the second had placed stands where it stood; and (2d,
>      FX7) a run laid across a district's buildings - a Rail Depot's yard and
>      main line at the hub, the yard gone and laid again, Elevated Highways
>      drawn across them - moves none of them, every box of the plan before
>      standing in the plan after, and the arrivals after a run stand on none
>      of its plots;
>   3. the painter draws the plan (0.7.88; the mockup's rules on the deal's
>      tiles before): on the dense screen every plot a district's plan
>      surfaces is painted as that street - kind and width - its seams once;
>      no bridge crosses more fresh water than its line may (a street
>      STREET_BRIDGE, an arterial ARTERIAL_BRIDGE) and no road is on the sea;
>      every mine on its own site; the same inputs paint the same pixels,
>      and the far view's blocks, from a twin map drawn the same way;
>   4. the sidecar round-trips byte for byte through a save and a load, the
>      next month plays to the same map, a stale or missing sidecar draws the
> ... (295 more lines in the source)

**Uses:** [CityMap](CityMap.md) (493), [World](World.md) (490), [LandMap](LandMap.md) (398), [TilePainter](TilePainter.md) (220), [TrainSchedule](TrainSchedule.md) (208), [MapFrame](MapFrame.md) (195), [BuildingVisual](BuildingVisual.md) (178), [DistrictPlan](DistrictPlan.md) (172), [TileRaster](TileRaster.md) (165), [MapTiles](MapTiles.md) (138), [GlobeFrame](GlobeFrame.md) (117), [LandParcel](LandParcel.md) (112), [MiniMapFrame](MiniMapFrame.md) (93), [Game](Game.md) (76), [SeaRoutes](SeaRoutes.md) (75), [CityRuns](CityRuns.md) (68), [CityLand](CityLand.md) (63), [JunctionCheck](JunctionCheck.md) (62), [DistrictPicture](DistrictPicture.md) (58), [BoatSchedule](BoatSchedule.md) (55), [EarthTerrainCheck](EarthTerrainCheck.md) (46), [CityShore](CityShore.md) (45), [Ports](Ports.md) (38), [LongPlaytest](LongPlaytest.md) (37), [Timing](Timing.md) (31), [EarthTerrain](EarthTerrain.md) (31), [LandGrid](LandGrid.md) (29), [GlobePicture](GlobePicture.md) (27), [EarthData](EarthData.md) (24), [Resource](Resource.md) (23)... and 31 more

**Used by (9):** [BridgeMapCheck](BridgeMapCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [GlobeCheck](GlobeCheck.md), [IslandNetCheck](IslandNetCheck.md), [MainLineCheck](MainLineCheck.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 510 | 1. A CITY'S MAP ADDS UP |
| 692 | · WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) |
| 826 | 2. NOTHING PLACED MOVES |
| 905 | 2b. A DISTRICT IS FULL AT WHAT ITS PLAN CAN DRAW, AND A SPILL KEEPS ITS TIME (batch CL3) |
| 1059 | 2c. A DISTRICT THAT BUYS LAND INSIDE ITSELF KEEPS ITS BUILDINGS WHERE THEY STAND (batch FX6) |
| 1321 | 2d. A RUN LAID ACROSS A DISTRICT'S BUILDINGS MOVES NONE OF THEM (batch FX7) |
| 1774 | THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY |
| 1893 | 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on |
| 1957 | 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) |
| 2063 | 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its |
| 2489 | 8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4; |
| 2657 | 8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec- |
| 3200 | 4. THE SIDECAR |
| 3412 | 5. THE COST FOLLOWS THE SCREEN |
| 3579 | 7. THE VIEW'S PURE HALF (0.7.61, batch J4) |
| 4269 | 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) |
| 4322 | · the shore's cost follows what a month changes (SL1, 2026-10-28) |
| 4826 | · SL1 (2026-10-28): the terminals share a lane, found once, drawn once |
| 5385 | · FX5, slice T: real-length trains, onto clear sidings, at a pace that reads |
| 6010 | 10. THE MAP ON EARTH (0.7.119, batch E5) |
| 6426 | 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) |
| 6899 | 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) |
| 7144 | 13. THE GLOBE ON SCREEN (0.7.135, batch E9) |
| 7659 | 14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |
| 8332 | 14e. BLOCKS PICKED (BL1) |
| 8978 | 14f. THE EXACT GROUND ON THE SCREEN (BL1, slice D) |
| 9292 | 15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; |
| 9659 | 16. THE AVENUES DRAWN (batch CL4; the study spec-city-layout 5.2) |
| 10016 | 17. THE CURVES DRAWN (batch CL4, slice D; the study spec-city-layout 5.2) |
| 10619 | 18. A MONTH REDRAWS ONLY WHAT MOVED (batch UP3, docs/queue/UP3.md) |
| 11352 | THE RELIEF ON THE MAP (ET1 slice D; docs/studies/spec-earth2-terrain.md |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 385 | `MapCheck.MONTHS` | `120` | Months the city of section 1 is played: 120 (the design's). |
| 388 | `MapCheck.IRON_AT` | `MONTHS / 3` | The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. |
| 400 | `MapCheck.JERUS_COUNTS` | `{ 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114...` | Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2 of dry ground): every building type's count, by id - 14,214 buildings, the design's own fixture for the map's sizes; 8,204 since 0.7.71, its child... |
| 405 | `MapCheck.JERUS_PEOPLE` | `509_455` | ...his people. |
| 408 | `MapCheck.JERUS_KM2` | `89.63` | ...and his dry ground, in km2. |
| 411 | `MapCheck.JERUS_FILL` | `0.92` | His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. |
| 414 | `MapCheck.JERUS_FOOTPRINT_KM2` | `82.273` | ...the land those buildings stood on, by the catalogue his save was measured with (0.7.70's): 82.273 km2, 91.8% of his dry ground (runs/fixN2-notes.md). |
| 432 | `MapCheck.TIMES` | `{ 1, 9_814, 10_000, 19_629 }` | The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). |
| 435 | `MapCheck.DENSE` | `2` | The copy whose screen is all city, at his density, that section 3 paints: x 10,000. |
| 438 | `MapCheck.SCREEN_ACROSS` | `18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN` | The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". |
| 441 | `MapCheck.SCREEN_MS` | `80` | The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). |
| 444 | `MapCheck.SCREEN_RATIO` | `1.5` | The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). |
| 447 | `MapCheck.RECONCILE_MS` | `5` | The design's bound on a month's change at 5B and at 10B, in ms of the main thread's CPU since 0.7.100 (measured 0.76; section 5's note). |
| 450 | `MapCheck.PX` | `4` | Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). |
| 453 | `MapCheck.WARM_ROUNDS` | `3, TIMED_ROUNDS = 5` | Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. |
| 456 | `MapCheck.DRY_PLACE` | `0.97` | How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. |
| 459 | `MapCheck.DRAWN_EVERY` | `30` | The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). |
| 505 | `MapCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 920 | `MapCheck.SPILL_FIRST_CELLS` | `2, SPILL_SECOND_CELLS = 6` | The cells across the window of whole cells a spill's first district owns and its second: 2 and 6 of a district's 8 - the first draws little, the second all it is carried. |
| 1079 | `MapCheck.BOUGHT_PLOTS` | `64` | The side of a bought block, at least: 64 plots (rounded up to whole blocks of the land's level), a few cells of a district's 8 across. |
| 1346 | `MapCheck.MIX` | `{ "Low-Rise Apartments", "Small Grocery Store", "Bakery", "Elementary School"...` | What the founding district is given month by month besides houses, in turn (an apartment block, shops, a school, works): buildings of several sizes for its plan to place. |
| 1349 | `MapCheck.USED_SHARE` | `0.5` | How much of its room the founding district is brought to before a run is laid across it: half of the cells its plan may fill (CityMap.PLAN_FILL of its owned plots). |
| 1352 | `MapCheck.BUILDINGS_A_MONTH` | `3` | The buildings of the mix the founding district is given each month, beside its houses. |
| 1355 | `MapCheck.MONTHS_BETWEEN` | `4` | Months of arrivals after each change of the runs, before the next plan is drawn: the buildings that must go on the ground as the run left it. |
| 1358 | `MapCheck.HIGHWAY_MONTHS` | `60, HIGHWAY_STEP = 8` | The most months the highways are given to reach the district, and the Elevated Highways added a month while they do. |
| 1361 | `MapCheck.HIGHWAY_CLEAR` | `1` | A building keeps this far from a highway plot (batch H5: no building touches one, corners included): the reach, in plots, within which a highway laid takes a box's place. |
| 1475 | `MapCheck.RUN_KINDS` | `{ DistrictPlan.FIXED_YARD, DistrictPlan.FIXED_RAIL, DistrictPlan.FIXED_HIGHWA...` | The kinds of fixed plot the runs lay, in the order a line prints them. |
| 1477 | `MapCheck.RUN_NAMES` | `{ "yard", "rail", "highway", "reserve" }` | ...and their names, in the same order. |
| 1962 | `MapCheck.NEW_CITY_MONTHS` | `{ 0, 1, 12 }` | The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). |
| 2496 | `MapCheck.ACCESS_HIGHWAYS_ADDED` | `200, ACCESS_DEPOTS_ADDED = 3` | The access fixture's added highways and Truck Depots: enough highways to cross, three depots to place. |
| 2664 | `MapCheck.SPURS_ADDED` | `12, BULK_WORKS_ADDED = 90` | The sidings fixture's added Rail Spurs and bulk works (JunctionCheck's two bulk types, two in three the larger). |
| 2968 | `MapCheck.CORRIDOR_STAGES` | `{ { 1, 6 }, { 3, 92 }, { 6, 400 }, { 9, 1385 } }` | Spec 2.7's table on the game's own ground: the design's dry place, the city's ground a square 1, 3, 6 and 9 districts a side (4 to 35 km from its middle, the prototype's 4 to 34), and its Elevated Highways the prototy... |
| 3123 | `MapCheck.YARDS_ADDED` | `2, FREIGHT_ADDED = 1` | The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard; 0.7.72's fixture). |
| 3387 | `MapCheck.SIDECAR_BYTES` | `500_000` | The most a sidecar may be at 5 and 10 billion people, log and all, in deflated bytes (CL3): the 10B copy's measured 333,776 (294,101 without the log, in FORMAT 8; its 1,457,418 records the canonical deal's, one batch,... |
| 3390 | `MapCheck.SIDECAR_MS` | `500` | The most the 5B and 10B copies' sidecar may take to write, and to read, in ms (CL3): measured 271 and 267 at 10B (240 and 161 at 5B), and near twice that. |
| 3437 | `MapCheck.THREADS` | `java.lang.management.ManagementFactory.getThreadMXBean()` | The JVM's threads, read for the main thread's CPU time. |
| 3440 | `MapCheck.CPU_STEP_MS` | `0.1` | The coarsest step of the thread's CPU clock a bound is read on, in ms: 0.1, a twentieth of the quickest stamp timed; coarser (Windows: 15.6) and the bounds are read on the wall clock. |
| 3443 | `MapCheck.CPU_TIMED` | `cpuTimed()` | Whether a month is timed on the main thread's CPU: this JVM measures it, it is on (turned on here if it is off), and its clock steps no coarser than CPU_STEP_MS. |
| 3584 | `MapCheck.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 3587 | `MapCheck.SCREEN_POINTS` | `{ { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } }` | Points across the screen the transforms are tried at. |
| 4037 | `MapCheck.BIG_OFFER_LEVEL` | `9` | The level of the offer the overlay's clip is tried on, zoomed in as far as the view goes: 9, blocks of 15.36 km - an offer of 2 x 4 of them is over three views across even at the expanded size, which 0.7.61's corners'... |
| 4144 | `MapCheck.DRAFT_MONTHS` | `24` | Months the draft's town is played before its map is drawn: 24. |
| 4274 | `MapCheck.SHORE_SIDE` | `3` | The section's city: three districts a side, all of it owned and drawn canonically on its measured ground - the playtest's coast (its bay, its south shore, its lagoon) - about a site one district west of the default wo... |
| 4291 | `MapCheck.BOAT_FRAME_MS` | `0.5` | The design's bound on a frame's boats at 10 billion, in ms (spec-oil 5's O13 row: "a frame's boats <= 0.5 ms at 10B, measured 0.38"). |
| 4294 | `MapCheck.BOAT_FRAMES` | `200` | Frames timed for that bound, a round: 200 at times through the month; the least of TIMED_ROUNDS rounds' means after WARM_ROUNDS. |
| 4297 | `MapCheck.ROUTES_ALL_MS` | `ham.citybuildersim.ui.UserInterface.SECONDS_PER_MONTH * 1000` | A month at normal speed, in ms: ui.UserInterface.SECONDS_PER_MONTH x 1000, 5,000 - what every terminal's route found from scratch at 10 billion must be found within (SL1, 2026-10-28). |
| 4300 | `MapCheck.ROUTES_MONTH_MS` | `ham.citybuildersim.ui.UserInterface.SECONDS_PER_MONTH * 1000 / ham.citybuilde...` | A month at the clock's top speed, in ms: SECONDS_PER_MONTH x 1000 over UserInterface.SPEED_TOP, 100 - what a month's new terminals' routes, laid onto those kept, must be found within at 10 billion (SL1). |
| 4303 | `MapCheck.ROUTES_MONTH_SHARE` | `0.01` | The share of the 10 billion lanes' terminals a month lays: 1%, 200 of the 20,000 (SL1). |
| 4306 | `MapCheck.ROUTES_ROUNDS` | `3` | Rounds of the 10 billion routes' timings, the least kept (a find from scratch is the slow one, so fewer than the frames'): 3 after one warm round. |
| 4325 | `MapCheck.SHORE_FEW` | `10` | General Cargo Terminals standing on the small coast of the shore's cost: 10. |
| 4328 | `MapCheck.SHORE_MANY` | `300` | ...and on the big coast: 300, where a late city stands thousands. |
| 4331 | `MapCheck.SHORE_GROWTH` | `2` | How many times the cost of a month's terminal may grow from SHORE_FEW standing to SHORE_MANY: twice (it was 21 times, 74 ms and 1,557 ms: every plot the search read asked every work, and every search began again at th... |
| 4334 | `MapCheck.SHORE_WARM` | `3, SHORE_TIMED = 15` | Months of one more terminal before the timing and timed, a terminal each: SHORE_WARM that are not, then the median of SHORE_TIMED kept (a month is about a ms, which a pause moves). |
| 4337 | `MapCheck.SHORE_MARGIN` | `2` | Plots asked about each work beyond its box, verge and quay: 2, past the cell the index keeps a work in. |
| 4340 | `MapCheck.SHORE_FILL` | `20` | Terminals asked of the coast that fills, one at a time: 20, more than its 12. |
| 4343 | `MapCheck.SHORE_LAGOON` | `40` | Terminals asked of each lagoon, one at a time: 40. |
| 5133 | `MapCheck.TRAIN_FRAME_MS` | `BOAT_FRAME_MS` | The design's bound on a frame's trains at 10 billion, in ms: the boats' (spec-freight 4.4, "the same structure is well inside budget"). |
| 5136 | `MapCheck.TRAIN_YARDS_A_SIDE` | `40` | Yards a side of the synthetic city the frame is timed on at 10 billion: 40, 1,600 yards, 12,800 routes on one main line. |
| 5606 | `MapCheck.MOST_TURN_DEG` | `30` | The most two neighbouring vehicles of a train may differ in heading, in degrees: 30 (FX5; on 0.8.14 a corner turned them 90 and a ladder 60). |
| 5609 | `MapCheck.LADDER_TURN_DEG` | `45` | ...and on a yard's ladder, the tightest curve the railway has: 45 degrees (FX5: an S of up to a few plots across over YARD_LADDER, 3 plots, turns a depot's neighbours 40; the long yards slice Y lays will lengthen the ... |
| 5753 | `MapCheck.TRUCK_FRAME_MS` | `TRAIN_FRAME_MS` | The design's bound on a frame's trucks at 10 billion, in ms: the trains' (FR9: the trips run the trains' pace, and a frame is the same structure). |
| 5756 | `MapCheck.TRUCK_ROADS` | `20` | Roads a district of the synthetic city has at 10 billion: 20, each with a world trip and a home trip, so a district's schedule holds 40 routes. |
| 5847 | `MapCheck.SCREEN_W_PX` | `1389, SCREEN_H_PX = 868` | The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. |
| 5850 | `MapCheck.PROTO_TONNES` | `896_308, PROTO_PEOPLE = 469_092` | BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. |
| 5853 | `MapCheck.PROTO_LANES` | `20_000` | BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). |
| 5855 | `MapCheck.PROTO_LANE_DX` | `200, PROTO_LANE_DY = - 1_500` | ...each lane's far end, in plots from its berth: 200 east and 1,500 north (north is less y). |
| 6015 | `MapCheck.WINDOW_STRIDE` | `EarthTerrainCheck.WINDOW / MapFrame.NODE_PX` | The stride the study's windows are drawn at: EarthTerrainCheck.WINDOW / NODE_PX, 16 plots a pixel - one node is the window, a world cell, and from it the far node strokes its own water and dithers its forest (EarthTer... |
| 6018 | `MapCheck.CLOSE_STRIDES` | `{ 2, 4, 8 }` | The strides under WINDOW_STRIDE the windows are drawn at too, where the far node is E2's region but for its rivers: 2, 4 and 8 plots a pixel. |
| 6021 | `MapCheck.SEAM_SHIFT_PX` | `MapFrame.NODE_PX / 2` | How far a node is moved to hold the 180th meridian with its neighbour, in pixels: half a node, 64 - whole blocks of EarthTerrain.FAR_BLEND_PX, so two nodes read every pixel they share alike. |
| 6024 | `MapCheck.CHUKOTKA_LAT` | `66, CHUKOTKA_LON = 180` | The full file's seam: the 180th meridian in Chukotka, 66 N, rivers and lakes either side of it. |
| 6027 | `MapCheck.CHUKOTKA_STRIDE` | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | ...drawn at 64 plots a pixel (1.9 km; a node 246 km across), EarthTerrain.FAR_TILE_BLEND_STRIDE: every river stroked (FAR_RIVER_SHARE's 23 m), each tile's points blended. |
| 6030 | `MapCheck.LAKES_LAT` | `62, LAKES_LON = 26` | The full file's small lakes: Finland's lake district, 62 N 26 E. |
| 6033 | `MapCheck.LAKES_NODES` | `3` | ...over this many nodes a side about the spot at WINDOW_STRIDE: 3, 184 km, a few thousand lakes. |
| 6431 | `MapCheck.PICTURE_MONTHS` | `MONTHS` | Months section 11 plays the played city on, every district's picture asked after each as a far view asks for it: MONTHS more. |
| 6439 | `MapCheck.PICTURE_CASH_MONTHS` | `3` | FR8, 2026-10-28 (the path moved): the months of its GDP the played city is handed in cash at each look of the advisor in 11's run when it holds less. |
| 6448 | `MapCheck.PICTURE_SIGMAS` | `4` | How far a picture's share of a kind may stand from its plots' share: this many of its binomial spreads (DistrictPicture.spread()), and a pixel - four, about one district in 16,000 past it by chance. |
| 6451 | `MapCheck.NODE_ROUNDS` | `TIMED_ROUNDS` | Rounds a far node is timed over after WARM_ROUNDS, the least its time: TIMED_ROUNDS. |
| 6904 | `MapCheck.FAR_DISTRICTS` | `4` | How far out the played city's far block is bought: 4 districts past its built-up ground's east edge (31 km) - districts of its own, nothing built in them. |
| 6907 | `MapCheck.STAMP_WARM` | `20, STAMP_TIMED = 10` | Rounds the stamp is drawn before it is timed, and rounds timed, the least its time: 20 and 10 - it is drawn every month of play, so its steady state is what is timed, the JIT's compiling done (about 5 ms a round; sect... |
| 7149 | `MapCheck.KEY_FRAMES` | `60` | Frames of the keys' second: 60, the pump's (a sixtieth each). |
| 7152 | `MapCheck.KEY_SLOP_PX` | `1e-3` | How far a second of the keys may miss KEY_PX_A_SECOND, in pixels: a thousandth (★E9) - on the plane it is exact; on the globe each frame's drag keeps the point grabbed under the pointer to GlobeCheck's POINTER_PX. |
| 7155 | `MapCheck.MATRIX_PLOTS` | `1e-6` | The composed matrix's worst against the camera's and the plane's own projections, in plots: a millionth (★E9; measured 4e-10). |
| 7664 | `MapCheck.BOXES` | `200` | Boxes drawn at random on the played city's opening view, each tried against every plot under it: 200. |
| 7667 | `MapCheck.GLOBE_BOXES` | `60` | ...and through the globe's camera, each against a point at every free plot's middle: 60. |
| 7670 | `MapCheck.FIELD_BUYS_MOST` | `200` | The most offers the city buys toward an iron field for one to be on offer: 200, MiningCheck.makeRoom()'s own bound. |
| 7673 | `MapCheck.BOX_PLOTS_MOST` | `200_000` | The most plots a box is tried on, one by one: 200,000 - a box larger is drawn again smaller. |
| 7676 | `MapCheck.GLOBE_LAT` | `51.90625, GLOBE_LON = 4.48125` | Where the globe's check looks from: EarthCheck's Rotterdam window's centre, the played city's site put there. |
| 8042 | `MapCheck.PICKS_MONTHS` | `3` | The months section 14b holds the picks over, each with a place relisted: 3. |
| 8045 | `MapCheck.PICKS_HELD` | `3` | The offers section 14b picks and holds over the months: 3. |
| 8200 | `MapCheck.FAR_PLOTS` | `World.SIDE / 4.0` | How far from the city section 14c sets the plane: a quarter of the world's side, in plots. |
| 8203 | `MapCheck.FAR_DRAGS` | `50` | The drags section 14c carries the plane on, further out, of the pixels each. |
| 8249 | `MapCheck.PRESS_PLOTS` | `400` | The plots of an offer's rectangle section 14d tries for the owned-or-free rule: at most about this many, a stride over the rectangle. |
| 8350 | `MapCheck.STEPS` | `{ { 1, 0 }, { - 1, 0 }, { 0, 1 }, { 0, - 1 } }` | The four steps from a block to its neighbours: east, west, south, north. |
| 9298 | `MapCheck.MATERIALS_SIDE` | `10` | The materials fixture's square, in districts a side: 10 (77 km), wide enough to hold fields of every typed slot on the default world. |
| 9301 | `MapCheck.MATERIALS_SECTORS` | `{ Sectors.MINING, Sectors.HEAVY_INDUSTRY, Sectors.COAL_MINING, Sectors.QUARRY...` | The nine materials industries, Iron Mining to Wood Products (spec-materials 2.1): every building of theirs drawn with a look of its own. |
| 9638 | `MapCheck.MATERIALS_RINGS` | `24` | How far out materialsPlace() looks, in cells: 24. |
| 9679 | `MapCheck.AV_SAMPLES` | `4` | How many samples a step of an avenue's line is sampled at: four, a quarter step apart. |
| 9682 | `MapCheck.AV_STEPS` | `400` | The farthest an arm is followed from the origin, in steps: 400, past a district and a half. |
| 9690 | `MapCheck.AV_TIMES` | `9, AV_SIDE = 6, AV_SHIFT_X = 104, AV_SHIFT_Y = 56` | The city the avenues are drawn on: Jerus's city x 9 on a square of 6 districts a side (his density, as the copies', every district full) whose founding site is the dry place's moved AV_SHIFT_X plots east and AV_SHIFT_... |
| 9710 | `MapCheck.AvTiles.OWNED` | `1, BENEATH = 2, BRIDGE = 4` | Flag bits of a plot: it is owned, a street passes beneath a highway on it, it is a bridge's. |
| 10035 | `MapCheck.CV_FROM` | `3, CV_TO = 8` | The districts the curves are looked for in, in districts east and south of the founding site's: 3 to 8 - past CRESCENT_FROM (16 cells, two districts) wherever the cell lies. |
| 10038 | `MapCheck.CV_CRESCENTS` | `8, CV_SHORES = 16` | The most crescents and shore curves the audit follows: 8 and 16 (the shore curves that cross a tile's edge, and those that bend, first). |
| 10041 | `MapCheck.CV_TURN_SPAN` | `4` | How far apart, in plots, the turn audit's samples are: 4 - at a crescent's radius (22.6 plots) a turn of 0.18 radian between neighbours. |
| 10044 | `MapCheck.CV_CENTRE_PX` | `1.0` | How far the band's middle, measured across it from the pixels, may lie from the line: a pixel - the least a raster can place a band, and less than the 2.8 pixels at 4 px a plot a staircase's bars lie from the arc thro... |
| 10047 | `MapCheck.CV_NARROW_PX` | `1.5` | How much narrower than its street the band, measured across it, may measure: 1.5 pixels (a pixel's rounding at either end and a half). |
| 10050 | `MapCheck.CV_GENTLE` | `0.2` | How much the line may turn within a plot and a quarter either side of a sample for the band to be measured across it there: 0.2 radian - a crescent turns 0.06 over that; a shore street's corner (rounded to as little a... |
| 10053 | `MapCheck.CV_MEASURED` | `{ 0.5, 0.35 }` | The share of a kind's samples the band must have been measured across (crescents, shore curves): a half, and 35% - a shore's line turns a corner every few plots, and the band is not measured within a plot and a quarte... |
| 10653 | `MapCheck.HIS_TIMES` | `80` | The copy at Jerus's own district count: x 80, 125 districts, 11 a side (CL3's notes cite 115 for his cities), between x 1's 4 districts and 5B's 14,661. |
| 10656 | `MapCheck.REDRAW_MONTHS` | `24, REDRAW_MONTHS_5B = 12` | The months grown at x 1 and at x 80, and at 5B (its months are slower): 24 and 12. |
| 10659 | `MapCheck.REDRAW_WARM` | `3` | The first months of each fixture are drawn, compared and not counted in its figures: 3, the JIT's compiling done before a month is read. |
| 10662 | `MapCheck.GROW_PER_MILLE` | `1000, GROW_PER_TEN_THOUSAND = 10_000` | A month's growth as a share of each type's count: 1/1,000 (+0.1%) at x 1 and x 80, 1/10,000 (+0.01%) at 5B, as section 5's, at least one building each. |
| 10665 | `MapCheck.RUNS_EVERY` | `4` | The runs' types (a highway's, a track's) are grown only in every 4th month, so most months lay no run, as in play. |
| 10668 | `MapCheck.CHAIN_ROUNDS` | `4` | The times a month's screens are drawn: the month's, then again after each round of the chain's jobs (handed out, run, adopted) until none is left, at most 4. |
| 10671 | `MapCheck.PLAYED_BEFORE` | `3, PLAYED_AFTER = 3` | The months the played city is grown before its land purchase and after it: 3 and 3. |
| 10674 | `MapCheck.PURCHASE_SIDE` | `32` | The side of the block the played city buys, in plots: 32, a hole in its built-up ground's box (the played city's ground is irregular). |
| 10677 | `MapCheck.MAP_MONTH_MS` | `58` | The most a month's FX-side map work may take at 5B, in ms, its median month - the stamp, the far view's screen, an L0 screen re-stamped and the chain's handing out and adopting: 58, nine tenths of the 65 this section ... |
| 10680 | `MapCheck.GROW` | `0, ONE = 1, SETTLE = 2` | The kinds of month: the grown one, one built type raised by one, and the first, which only draws. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 376 | `static int fails` |  |
| 377 | `static PrintStream out` |  |
| 699 | `long[] buildings` | Buildings by type id: a mine or well on a site once, however many tiles its site spans - since 0.8.0 a works once for all the sites it takes; packed ones among them. |
| 701 | `long[] siteCount` | The sites under each type's works, by type id (0.8.0), each once; and the types whose sites are not a whole number of its works. |
| 702 | `long partWorks` |  |
| 704 | `final double[] surface` | The streets' surface in plots by kind [0, gravel, paved, highway] - a highway's own plots among the highway's - and what the plans have no street for (CityMap.leftoverByKind()). |
| 706 | `long rail, trackSurplus, packed, tiles, districts` | The railway's track laid, in plots (0.7.72), and (0.7.89) what the runs could not lay - since FR7 slice 3 the plots no train's way uses (CityRuns.trackSurplus()); buildings packed at the city's edge (R7); tiles painte... |
| 803 | `static GameFiles cityFiles` | The played city's save folder. |
| 1099 | `final CityMap map` |  |
| 1100 | `final long[] model` |  |
| 1101 | `final int house` |  |
| 1103 | `int month` | The month the next one is run in. |
| 1104 | `boolean reconciled` |  |
| 1365 | `final Months h` |  |
| 1366 | `final int[] mix` |  |
| 1367 | `final int step` |  |
| 1368 | `int next` |  |
| 1390 | `final DistrictPlan plan` |  |
| 1391 | `final java.util.Set<PlanBox> boxes` |  |
| 1392 | `final byte[] fixed, runs` |  |
| 1393 | `final boolean[] ramp` |  |
| 1395 | `final int month, journal` | The map's month when the snapshot was taken, and how many records the district's journal of the runs held then. |
| 1780 | `final BuildingVisual.Type[] types` |  |
| 1781 | `final long seed` |  |
| 1782 | `final long x, y` |  |
| 1783 | `final CityMap[] maps` |  |
| 1784 | `final long[][] counts` |  |
| 1785 | `final double[] buildMs` |  |
| 1787 | `final long ironX, ironY` | His city x 1 again where its square holds iron (0.7.99, batch W1; dryPlace(w, side)): the fixtures that need his mines - the runs' marks, the rail yards - stand on it; the dry place's own holds none since the deposits... |
| 1788 | `final CityMap ironX1` |  |
| 1789 | `final long[] ironCounts` |  |
| 2081 | `final CityMap map` |  |
| 2082 | `final long px0, py0` |  |
| 2083 | `final int w, h` |  |
| 2084 | `final byte[] use, road, width, role, run` |  |
| 2085 | `final boolean[] owned, sea, wet, bridge, beneath, mined` |  |
| 2087 | `final List<int[]> boxes` | Each building: {x0, y0, w, h, type, site (or -1), packed (1 or 0)} in the box's plots. |
| 2088 | `long rail, crossings, packed, siding` |  |
| 2089 | `final double[] surface` |  |
| 2090 | `final long[] buildings` |  |
| 2091 | `final java.util.Set<Long> sites` |  |
| 3031 | `final long x0, y0` |  |
| 3032 | `final int w, h` |  |
| 3033 | `final byte[] fixed` |  |
| 6232 | `final List<double[]> segs` |  |
| 6233 | `final List<double[]> lakes` |  |
| 6234 | `final long x0, y0` |  |
| 6235 | `final int stride` |  |
| 9703 | `final CityMap m` |  |
| 9704 | `final TilePainter.Input in` |  |
| 9705 | `final TilePainter.Painted p` |  |
| 9706 | `final java.util.Map<Long, int[]> img` |  |
| 9707 | `final java.util.Map<Long, byte[]> street` |  |
| 10073 | `final CityMap m` |  |
| 10074 | `final int px` |  |
| 10075 | `final TilePainter.Input in` |  |
| 10076 | `final TilePainter.Painted p` |  |
| 10077 | `final java.util.Map<Long, int[]> img` |  |
| 10078 | `final java.util.Map<Long, byte[]> street` |  |
| 10079 | `final java.util.Map<Long, short[]> types` |  |
| 10082 | `final boolean bare` | Whether the tiles are drawn without the curves (the control: as they were before CL4). |
| 10148 | `int samples, badColour, onBuilding, edges, audited, narrow, off, turns, turnsBad, tracks` |  |
| 10149 | `double worstCentre, worstExcess, worstIdeal` |  |
| 10151 | `String firstBad` | The first sample that was not the street's colour or lay off the line, for the line a failure prints: where, what. |
| 10684 | `final String name` |  |
| 10685 | `final MapFrame f` |  |
| 10686 | `final long plots, span` |  |
| 10687 | `final boolean pictures` |  |
| 10688 | `final int count` |  |
| 10689 | `final long[] nx, ny` |  |
| 10690 | `final byte[][] ground` |  |
| 10692 | `final long[] key` | Now's: the key each node was drawn from, its pixels, whether it was drawn, and short of a picture. |
| 10693 | `final int[][] pix` |  |
| 10694 | `final boolean[] drawn, partial` |  |
| 10696 | `final boolean[] todayPartial` | Today's: whether it would be drawn again after a picture came. |
| 10698 | `final boolean[] held` | The nodes held (not drawn again) in the month under way. |
| 10699 | `final int[] buf` |  |
| 10739 | `int kind, number, moved, rounds, astray, kept, built` |  |
| 10741 | `final java.util.List<CityMap.District> staleList` | The built districts whose pictures were no longer current right after a one-type month (`stale` of them). |
| 10742 | `boolean timed, reconciled, viewChanged, runsMoved, settled` |  |
| 10743 | `CityMap.District onlyMoved` |  |
| 10745 | `long lands, runsDrawn, repainted, onStamp` | The stamp's kept nodes in the month: land layers drawn, runs layers drawn, districts repainted (all 0 when the view changed and they were made anew), and the districts they list. |
| 10746 | `double stampNow, stampToday, farNow, farToday, l0, chain, keys, lazy` |  |
| 10748 | `int[] recoloured, partialBefore` | Per far screen: its nodes drawn this month (the month's round and the rounds after), and the partial ones before it. |
| 10750 | `final java.util.List<java.util.List<Integer>> keyMoved` | Per far screen: the nodes drawn in the month's own round because the key they were drawn from had moved. |
| 10758 | `final String name` |  |
| 10759 | `final CityMap m` |  |
| 10760 | `final long[] c` |  |
| 10761 | `final int divisor, mine` |  |
| 10762 | `final long sites` |  |
| 10763 | `final BuildingVisual.Type[] tys` |  |
| 10764 | `final MapTiles tk, tr` |  |
| 10765 | `final MapFrame l0` |  |
| 10766 | `final boolean withL0, checkLinks` |  |
| 10767 | `final java.util.List<FarScreen> fars` |  |
| 10768 | `final java.util.List<RedrawMonth> all` |  |
| 10769 | `final TilePainter.Input in` |  |
| 10770 | `MiniMapFrame.Box frame` |  |
| 10771 | `MiniMapFrame.View stampV, previewV` |  |
| 10772 | `byte[][] stampG, previewG` |  |
| 10773 | `MapTiles.KeptNode[] stampK, previewK` |  |
| 10774 | `int[] stampNow, stampToday, previewNow, previewToday` |  |
| 10775 | `int viewChanges` |  |
| 10777 | `int stampsDrawn, previewsDrawn, keyUnmoved, heldCompared` | What was compared: stamps and previews drawn two ways (their pixels counted), and far nodes whose key was unmoved against a fresh one (those held in the month among them); whether every one was equal. |
| 10778 | `long pixelsCompared` |  |
| 10779 | `boolean stampEqual` |  |
| 10780 | `double tookS` |  |
| 10782 | `Runnable beforeReconcile` | Run before the next month's reconcile (a land purchase). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 374 | 11277 | **type** `public class MapCheck` | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's street plan and paints the same pixels from the same inputs, the draw... |
| 379 | 4 | `static void check(String label, boolean ok)` |  |
| 423 | 7 | `static double groundKm2(BuildingVisual.Type[] types)` | The dry ground the copies' x 1 stands on (0.7.71): his, scaled by his buildings' land on this catalogue over theirs then, so the copies keep his density (JERUS_FILL) whatever the catalogue: with his childcare restated... |
| 461 | 43 | `public static void main(String[] args) throws Exception` |  |

### 1. A CITY'S MAP ADDS UP (lines 510-691)

| line | len | member | says |
|---:|---:|---|---|
| 514 | 127 | `static Game playedCity(Path root) throws Exception` |  |
| 655 | 36 | `static boolean worldWaysOk(CityMap m, int[] seen)` | Whether a month's lines to the world and trains lie on the ground they should (FX2; GatewayCheck holds the whole list on its fixtures, this is the cheap half, the points and the routes and never the box): each world l... |

### WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) (lines 692-825)

| line | len | member | says |
|---:|---:|---|---|
| 697 | 32 | **type** `static final class Drawn` | What the painted map draws over every tile of a map's districts and a tile round them (0.7.88: a district's plan lays the next one's first column and row). |
| 708 | 1 | `long total()` _(in MapCheck.Drawn)_ |  |
| 709 | 1 | `int kinds()` _(in MapCheck.Drawn)_ |  |
| 710 | 1 | `double surfaced()` _(in MapCheck.Drawn)_ |  |
| 711 | 1 | `double left()` _(in MapCheck.Drawn)_ |  |
| 714 | 14 | `boolean sameAs(long[] model, BuildingVisual.Type[] types)` _(in MapCheck.Drawn)_ | Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn); each road kind's plots its streets' surface and what they do not carry, to within half a plot a district ... |
| 731 | 9 | `static long[] tilesOf(CityMap m)` | The tiles a map's districts cover and one round them: {tx0, ty0, across, down}. |
| 742 | 37 | `static Drawn drawn(CityMap m)` | Paints every tile of every district on a map, and a tile round them, and counts what is drawn. |
| 781 | 5 | `static long modelBuildings(long[] model, BuildingVisual.Type[] types)` | The model's buildings the map draws: every type but the roads. |
| 788 | 7 | `static long roadPlotsOf(long[] model, BuildingVisual.Type[] types, int kind)` | The model's road plots of a kind: each road type's count x its own plots (BuildingVisual.cells()). |
| 797 | 4 | `static long modelRoadPlots(long[] model, BuildingVisual.Type[] types)` | ...of every kind. |
| 806 | 8 | `static boolean roomAt(CityMap.District d, BuildingVisual.Type type, double share)` | Whether district d has room for one more of a type at a road share: CityMap.hasRoomFor()'s rule (buildingRoom()), at the share given rather than the map's own. |
| 816 | 4 | `static long mineSites(CityMap m, int mine)` | The sites a mine type may stand on (0.8.0, batch I14): its resource's of its type of field - the game's own rule since I5 (Game.committedOn(r, type), LandManager.fits()), an Iron Mine on hematite. |
| 821 | 4 | `static int mineId(Game g)` |  |

### 2. NOTHING PLACED MOVES (lines 826-904)

| line | len | member | says |
|---:|---:|---|---|
| 830 | 74 | `static void nothingMoves(Game g)` |  |

### 2b. A DISTRICT IS FULL AT WHAT ITS PLAN CAN DRAW, AND A SPILL KEEPS ITS TIME (batch CL3) (lines 905-1058)

| line | len | member | says |
|---:|---:|---|---|
| 922 | 7 | `static void districtsHoldAndSpill(Game g)` |  |
| 935 | 39 | `static void holdsWhatItCanDraw(BuildingVisual.Type[] types, int flats)` | A district holds what its plan can draw: on a square city of four districts, a few apartment blocks added month by month (reconcile) until the nearest district stops taking them. |
| 980 | 52 | `static void spillKeepsItsTime(BuildingVisual.Type[] types, int house, int store)` | A spill keeps its time, at the plan's level: the first district's input is given more than its window of ground can draw, the second's carry is the pairs the first leaves ({month, type}, the month each came in) - and ... |
| 1037 | 21 | `static DistrictPlan.Input spillInput(BuildingVisual.Type[] types, int cellsAcross, int[][] records, int firstBatch, int[] carry)` | A hand-made district for 2b's spill: flat ground, a square window of whole cells round the hub owned (the rest not), its types, its log (records {month, type, change}, the first `firstBatch` one batch; its counts the ... |

### 2c. A DISTRICT THAT BUYS LAND INSIDE ITSELF KEEPS ITS BUILDINGS WHERE THEY STAND (batch FX6) (lines 1059-1320)

| line | len | member | says |
|---:|---:|---|---|
| 1082 | 1 | **type** `record PlanBox(int type, int x, int y, int w, int h)` | One building of a plan: its type and its box in the frame's plots. |
| 1085 | 11 | **type** `record Bought(long x0, long y0, long x1, long y1, int dry)` | A rectangle of plots bought inside a district, [x0, x1) x [y0, y1) in the world, and how many of its plots are dry. |
| 1087 | 3 | `double nearest(long sx, long sy)` _(in MapCheck.Bought)_ | How far its nearest plot is from plot (sx, sy), in plots. |
| 1092 | 3 | `boolean holds(PlanBox b, long fx, long fy)` _(in MapCheck.Bought)_ | Whether the box lies wholly on this block, a box in the frame whose first plot is (fx, fy) in the world. |
| 1098 | 24 | **type** `static final class Months` | A harness city's months: houses added to the model a month at a time and the map reconciled to them, each in its month. |
| 1106 | 6 | `Months(CityMap map, long[] model, int house, int month)` _(in MapCheck.Months)_ |  |
| 1114 | 1 | `int last()` _(in MapCheck.Months)_ | The month that last ran: what a purchase made between months is dated. |
| 1117 | 4 | `void run(int houses)` _(in MapCheck.Months)_ | One month of `houses` more houses. |
| 1129 | 122 | `static void districtBuysLandInside(Game g)` | Whether a district that buys land inside itself keeps its buildings where they stand (batch FX6): on the played city's ground, a block is bought in the next district, and the district holding the founding site is give... |
| 1253 | 5 | `static java.util.Set<PlanBox> boxesOf(DistrictPlan p)` | The plan's buildings as boxes, a set: a building stands where it did when its box is in the plan again. |
| 1260 | 5 | `static int movedOf(java.util.Set<PlanBox> before, java.util.Set<PlanBox> after)` | How many of the boxes before are not among the boxes after: the buildings that moved. |
| 1267 | 5 | `static int housesElsewhere(CityMap m, CityMap.District d, int house)` | The houses the map holds in the districts but d. |
| 1278 | 22 | `static Bought nearestFreeBlock(CityLand land, DistrictPlan.Input frame)` | The block of whole land blocks (BOUGHT_PLOTS a side at the least, rounded up to the level's blocks) the city could buy inside a district's frame (not on its last row or column) that lies wholly unowned, is at least ha... |
| 1302 | 7 | `static int dryPlots(DistrictPlan.Input frame, long x0, long y0, long x1, long y1)` | The dry plots of [x0, x1) x [y0, y1) in the world, read from a plan input's ground (the world's, over the frame). |
| 1311 | 9 | `static boolean soundBlock(CityLand land, DistrictPlan.Input frame, Bought b)` | Whether a block about to be bought is what 2c says it is, checked on the land as it stands: whole blocks of its level, inside the frame, wholly unowned, at least half dry. |

### 2d. A RUN LAID ACROSS A DISTRICT'S BUILDINGS MOVES NONE OF THEM (batch FX7) (lines 1321-1773)

| line | len | member | says |
|---:|---:|---|---|
| 1364 | 23 | **type** `static final class Arrivals` | The arrivals of the founding district, month by month: `step` houses and BUILDINGS_A_MONTH buildings of the mix, in turn. |
| 1370 | 5 | `Arrivals(Months h, int[] mix, int step)` _(in MapCheck.Arrivals)_ |  |
| 1377 | 4 | `void month()` _(in MapCheck.Arrivals)_ | One month: the mix's next buildings and the houses. |
| 1383 | 3 | `void months(int n)` _(in MapCheck.Arrivals)_ | `n` months. |
| 1389 | 59 | **type** `static final class Snap` | The founding district's plan at one moment: its boxes, the plots its input fixes, the city's runs alone in its frame, and the ramps. |
| 1397 | 11 | `Snap(CityMap m, CityMap.District d)` _(in MapCheck.Snap)_ |  |
| 1410 | 5 | `int under(Snap later, int reach)` _(in MapCheck.Snap)_ | How many of this plan's boxes have a plot within `reach` plots of a plot the runs hold in `later` and did not hold here: the buildings a run laid in between lies across. |
| 1417 | 5 | `int arrivalsOnRuns(Snap later)` _(in MapCheck.Snap)_ | How many of `later`'s boxes that are not among this plan's lie on a plot fixed in `later`, in its input or among the runs: the arrivals that stand on a run. |
| 1424 | 5 | `int plots(byte kind)` _(in MapCheck.Snap)_ | The plots of kind `kind` in the runs. |
| 1431 | 5 | `int newPlots(Snap later, byte kind)` _(in MapCheck.Snap)_ | The plots of kind `kind` the runs hold in `later` and did not hold here. |
| 1441 | 6 | `int movedWithTheseRuns(CityMap m, CityMap.District d)` _(in MapCheck.Snap)_ | How many of this plan's boxes are not in the plan the map makes now when its input is given this snapshot's fixed ground and ramps: the buildings that move with the arrivals alone, the runs left as they were. |
| 1450 | 6 | `static boolean anyPlot(PlanBox b, int reach, java.util.function.IntPredicate test)` | Whether any plot within `reach` plots of the box (in the frame's plots, clipped to the frame) satisfies the test, a plot's index in the frame. |
| 1461 | 12 | `static int journalDiffers(DistrictPlan.Input later, Snap s)` | How many plots of `later`'s fixed ground, worked back through its frame's records (Input.fixedLog) dated after the snapshot's month, differ from the fixed ground the snapshot's input held (the interchanges' reserves, ... |
| 1480 | 8 | `static String laidOf(Snap before, Snap after)` | What the runs laid between two snapshots, by kind: "yard 56, rail 19". |
| 1490 | 4 | `static Snap quietMonth(Months h, CityMap.District d)` | The district's plan after one month of the model as it stands, nothing added: the month a run is laid in. |
| 1496 | 3 | `static int idOf(Game g, String name)` | The id of the building template of that name. |
| 1505 | 19 | `static long[] highwayCrossing(CityLand land, DistrictPlan.Input frame)` | Where the model starts the highways once the city owns the ground there (CityRuns.start(): the crossing of the lattice, a line every CityRuns.CELL plots from CityRuns.HIGHWAY_AT, nearest the founding site that is dry)... |
| 1529 | 18 | `static long handGroundRound(CityLand land, DistrictPlan.Input frame, long[] at, int month)` | Hands the city, between months, every whole block of its level that lies in the square of CityRuns.CELL plots each way round `at` and that it does not own (a row of them side by side one parcel, bought as 2c's blocks ... |
| 1561 | 187 | `static void runsLaidAcross(Game g)` | Whether a run laid across a district's buildings moves none of them (batch FX7): on the played city's ground the district holding the founding site is given houses and BUILDINGS_A_MONTH buildings of the mix a month un... |
| 1750 | 5 | `static int cellsMin(BuildingVisual.Type[] types, int[] ids)` | The smallest and the largest footprint, in cells, of the types listed. |
| 1756 | 5 | `static int cellsMax(BuildingVisual.Type[] types, int[] ids)` |  |
| 1763 | 3 | `static String hubText(long[] hub, DistrictPlan.Input frame)` | A net's hub in the frame's plots, for a line: "40,72". |
| 1768 | 5 | `static String yardsText(List<long[]> yards, DistrictPlan.Input frame)` | The runs' yards in the frame's plots, for a line: "[x,y 8x7]". |

### THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY (lines 1774-1892)

| line | len | member | says |
|---:|---:|---|---|
| 1779 | 62 | **type** `static final class Squares` | His city x 1 and its copies, each on the design's square city at the dry place, its mines no more than its sites. |
| 1791 | 49 | `Squares()` _(in MapCheck.Squares)_ |  |
| 1843 | 3 | `static int x1Side(BuildingVisual.Type[] types)` | The side of Jerus's city x 1 on the design's square city, in districts: its ground over a district's (Squares). |
| 1848 | 3 | `static long[] dryPlace(World w)` | The world cell nearest the founding site's whose middle 3 x 3 districts are DRY_PLACE dry at a sample a tile: {x, y, rings out}. |
| 1860 | 18 | `static long[] dryPlace(World w, int side)` | ...and (0.7.99, batch W1), with `side` over 0, whose square for Jerus's city x 1 - `side` districts round its middle, as CityMap.square() lays it - holds an iron field's centre, so his mines have sites and his railway... |
| 1880 | 12 | `static boolean holdsIron(World w, long x, long y, int side)` | Whether the square of `side` districts CityMap.square() lays round plot (x, y) holds an iron field's centre. |

### 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on (lines 1893-1956)

| line | len | member | says |
|---:|---:|---|---|
| 1898 | 58 | `static void painterRules(CityMap m)` |  |

### 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) (lines 1957-2062)

| line | len | member | says |
|---:|---:|---|---|
| 1975 | 87 | `static void drawnAsItIs(CityMap dense)` | Jerus, 2026-10-07: "the generation should only put what the city has, not more not less". |

### 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its (lines 2063-2488)

| line | len | member | says |
|---:|---:|---|---|
| 2080 | 312 | **type** `static final class Raster` | A map painted whole over a box of tiles and put together: what each plot is, every building's box - what holds the network's rules. |
| 2093 | 57 | `Raster(CityMap map, long tx0, long ty0, int tw, int th)` _(in MapCheck.Raster)_ |  |
| 2152 | 4 | `static Raster of(CityMap map)` _(in MapCheck.Raster)_ | Every tile of every district of a map, and a tile round them. |
| 2158 | 1 | `boolean isRoad(int g)` _(in MapCheck.Raster)_ | A road plot: a street (a track among them, the track's level crossings among them), or a highway's. |
| 2161 | 1 | `boolean isStreet(int g)` _(in MapCheck.Raster)_ | A street plot: a road plot with a street's role - not a highway's own. |
| 2164 | 1 | `boolean isHighway(int g)` _(in MapCheck.Raster)_ | A highway's own plot (its runs'). |
| 2166 | 1 | `boolean isRail(int g)` _(in MapCheck.Raster)_ |  |
| 2169 | 1 | `boolean isRunHighway(int g)` _(in MapCheck.Raster)_ | A plot of the city's highways as its runs lay it (0.7.89), under a building or not - the railway bridging it among them. |
| 2172 | 1 | `boolean isRunRail(int g)` _(in MapCheck.Raster)_ | ...of its railway's track, under a yard or a mine too, or bridging a highway. |
| 2174 | 5 | `long streetPlots()` _(in MapCheck.Raster)_ |  |
| 2180 | 8 | `int beside(int g, java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 2190 | 19 | `int[] pieces(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ | The pieces plots of a kind make, four ways joined: each plot's piece number (0 for none), [0] = how many pieces. |
| 2220 | 37 | `int[] network()` _(in MapCheck.Raster)_ | The road's pieces and those the city's own ground joins to the largest: {pieces, plots of the largest, pieces the ground joins to it} - from the largest piece, four ways over owned ground that is not the sea or a mine... |
| 2258 | 5 | `long count(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 2265 | 5 | `long alone()` _(in MapCheck.Raster)_ | Road plots with no road beside them, four ways: a stray. |
| 2272 | 9 | `boolean crossing(int g)` _(in MapCheck.Raster)_ | A + junction of streets: a street plot with street on its four sides and none on its four corners (a square's middle, street on its corners too, is not one; nor a street beneath a highway, which passes under it). |
| 2283 | 16 | `long[] crossings(int apart)` _(in MapCheck.Raster)_ | {crossings, pairs of them nearer than `apart` either way}. |
| 2301 | 18 | `int[] reach(int most)` _(in MapCheck.Raster)_ | Each plot's distance from a street, across corners (a street 0), to `most` + 1. |
| 2321 | 5 | `static int boxReach(int[] dist, int w, int[] b)` _(in MapCheck.Raster)_ | A box's nearest plot's distance. |
| 2328 | 3 | `CityMap.District districtAt(int g)` _(in MapCheck.Raster)_ | The district a plot of the box is in. |
| 2338 | 24 | `int[] asPlanned()` _(in MapCheck.Raster)_ | Every plot the plans of the districts in the box surface (Drawn's codes, its frame's east column and south row among them) against the painted plot: {plots, those not painted as that street - its kind and its width}. |
| 2364 | 27 | `int[] bridges()` _(in MapCheck.Raster)_ | The bridges: {runs longer than their line may cross - an arterial's or a boulevard's ARTERIAL_BRIDGE, a street's STREET_BRIDGE, a highway's MAX_BRIDGE - roads on the sea, bridge plots}. |
| 2399 | 3 | `static void networkOn(CityMap m, long[] model, String what)` | The network's rules on a map's drawn raster (0.7.88): one network but where the ground parts it, no road alone, + junctions apart, every building within reach, the streets' surface the model's road kind by kind, every... |
| 2408 | 47 | `static void networkOn(CityMap m, long[] model, String what, boolean crosses)` | ...and whether the city must have + junctions for the + rule to bite (`crosses`): not the played city since CL3, whose few cells each opened while its road was short - a cell's squares are decided as it opens, never a... |
| 2456 | 32 | `static void network(Squares sq, Game city, String[] saves) throws Exception` |  |

### 8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4; (lines 2489-2656)

| line | len | member | says |
|---:|---:|---|---|
| 2499 | 5 | `static int depotType(BuildingVisual.Type[] types)` | The type id of the Truck Depot, the one building of the category HAULAGE. |
| 2509 | 3 | `static CityMap accessCity(Squares sq, boolean highways)` | The yard city (his city x 1 where its square holds iron, with two Rail Terminals and a freight line) with highways added to cross and Truck Depots to place; with `highways` false his own highways taken out too: none, ... |
| 2514 | 33 | `static CityMap accessCity(Squares sq, boolean highways, long[] extra)` | ...with `extra` counts added (batch FR7 slice 5: spurs and bulk works for the sidings' deal): a track type's to the railway, any other's to the buildings. |
| 2552 | 20 | `static int[] accessFigures(CityMap m)` | What a map's plans gave its works' access roads: {roads, on a ramp, on an arterial, already on one, gates not beside their box, ends neither a ramp's street nor an arterial, the map's works (yards and sea terminals) g... |
| 2573 | 83 | `static void interchangesAndAccess(Squares sq)` |  |

### 8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec- (lines 2657-3199)

| line | len | member | says |
|---:|---:|---|---|
| 2667 | 74 | `static void sidingsDealt(Squares sq)` | The spurs the runs' sidings left, as the map deals them: each dealt district is one the main line crosses with bulk works of its own, no more than it holds, and the plans lay at most their share, drawn on the raster; ... |
| 2743 | 23 | `static Game loadCopy(Path src) throws Exception` | A save's autosave copied into a temporary folder and loaded (its map drawn from its sidecar or canonically): saves named on the command line, never written. |
| 2775 | 3 | `static CityMap riverCity(Squares sq, int kind)` | The river fixture (0.7.77, batch N5): Jerus's city x 1 on the design's square city sited at the default world's founding river - its point nearest the founding site - with its streets all of one kind: his Paved Roads ... |
| 2780 | 30 | `static CityMap riverCity(BuildingVisual.Type[] types, long seed, long[] counts, int kind)` | The river fixture from the types, the world's seed and his city x 1's counts: PlanCheck 17 plans its districts (FR8). |
| 2820 | 44 | `static void riverCrossed(Squares sq)` | A river parts gravel streets no more than paved ones (0.7.77; Jerus, 2026-10-08: "gravel road bridge rivers sure"): the river fixture all gravel against its paved twin - the road pieces with a plot within a tile of th... |
| 2873 | 32 | `static void highways(Raster net, String what)` | The city's highways on the drawn raster (0.7.89, batch RD3; spec 2.7): drawn plot for plot as its runs lay them, and with what they could not lay the model's Elevated Highways; one network; no building plot touching a... |
| 2919 | 47 | `static int marksReachTheView(CityMap m, long tx0, long ty0, int tw, int th, String what, boolean bands)` | The runs' marks reach the picture a screen draws (0.7.98, batch O14): a screen rasters a tile from the view's kept copy of its inputs (MapTiles.painted(), paintedInput()), and until then that copy held none of the run... |
| 2981 | 47 | `static void corridors(Squares sq)` | The highways on corridors on the game's own ground (0.7.89; spec 2.7): each stage of spec 2.7's table on a square city of highways alone at the dry place, laid at once - its plots each laid or counted, one network, st... |
| 3030 | 55 | **type** `static final class Box` | The runs' plots over a map's districts and a plot round them, put together: one network, its junctions. |
| 3035 | 5 | `Box(long x0, long y0, int w, int h, CityRuns r)` _(in MapCheck.Box)_ |  |
| 3041 | 1 | `static Box of(CityMap m)` _(in MapCheck.Box)_ |  |
| 3043 | 4 | `static Box of(CityMap m, CityRuns r)` _(in MapCheck.Box)_ |  |
| 3048 | 1 | `boolean hw(int g)` _(in MapCheck.Box)_ |  |
| 3051 | 18 | `int pieces()` _(in MapCheck.Box)_ | The highways' pieces, four ways joined. |
| 3071 | 13 | `long junctions()` _(in MapCheck.Box)_ | Highway plots with highways on three or four sides: where they meet. |
| 3087 | 17 | `static int bendsStopped(CityMap m, CityRuns.Net n)` | The bends of a net that are not where its way was stopped: a stretch off its arm's heading begun where the way on along the heading before ran LOOK plots clear, or one back onto it where the way home did not run TURN_... |
| 3106 | 15 | `static void rail(Raster net, String what)` | The railway on the drawn raster (0.7.89; spec 2.8): one network, every plot of the model's track drawn - under its yards and mines too - or counted as what the runs could not lay (since FR7 slice 3 the surplus no trai... |
| 3126 | 25 | `static CityMap yardCity(Squares sq)` | The x 1 copy with rail yards and a freight line added (0.7.72's fixture, restored in 0.7.89): its buildings, then its mines on their sites, then its railway - so the railway starts at its mine nearest the founding sit... |
| 3159 | 40 | `static void yardsNearMines(CityMap m, Raster net)` | Every rail yard on its track, in the cell along it nearest a mine that had room (spec 2.8: "laid as runs from the rail terminals, which sit in estate cells nearest the mines"; 0.7.72's rule, restored): the track runs ... |

### 4. THE SIDECAR (lines 3200-3411)

| line | len | member | says |
|---:|---:|---|---|
| 3204 | 177 | `static void sidecar(Game g, Path root, Squares sq) throws Exception` |  |
| 3393 | 7 | `static boolean sameBoxes(DistrictPlan a, DistrictPlan b)` | Whether two plans place the same boxes: the same count of buildings, each one's type and its box {x, y, w, h}, in the same order, and the same buildings left without a place (by type). |
| 3402 | 9 | `static boolean logsAddUp(CityMap m)` | Whether every district's log sums, type by type, to its counts: what a log is for, and what a sidecar read back is held to (CL3). |

### 5. THE COST FOLLOWS THE SCREEN (lines 3412-3578)

| line | len | member | says |
|---:|---:|---|---|
| 3445 | 9 | `static boolean cpuTimed()` |  |
| 3456 | 10 | `static double cpuStepMs()` | The least step the main thread's CPU clock takes, in ms: five times, spinning until it moves (200 ms of the wall clock at most each); infinite if it never does. |
| 3468 | 3 | `static long monthClock()` | The clock a month's bound reads, in ns: the main thread's CPU, or the wall clock where CPU_TIMED is false. |
| 3472 | 106 | `static void cost(Squares sq)` |  |

### 7. THE VIEW'S PURE HALF (0.7.61, batch J4) (lines 3579-4268)

| line | len | member | says |
|---:|---:|---|---|
| 3589 | 8 | `static void viewHalf(Game city, Squares sq, Path root) throws Exception` |  |
| 3599 | 72 | `static void frames()` | MapFrame: the transforms, a notch of the wheel at the pointer, the clamps, the levels and what a screen asks for. |
| 3673 | 121 | `static void picks(Game g)` | LandMap: what a click and a hover pick on the played city agrees with the model's own grid (CityLand.holdingOf()) and GridOffers' sides and places, and the outlines with the picks (0.7.67; the lanes' owns() and sideLa... |
| 3807 | 228 | `static void overlay(Game g)` | The overlay on the block grid (0.7.69, batch M5; spec-grid 2.4), what MapView draws by: the city's edge as maximal runs with its ground on their right, drawn crisp - whole pixels, a pixel thick, just inside the ground... |
| 4040 | 3 | `static char dir(double x0, double y0, double x1, double y1)` | A run's way: E, S, W or N. |
| 4045 | 3 | `static boolean touch(double[] a, double[] b)` | Whether two boxes of pixels {x, y, w, h} share a pixel or touch at a corner or an edge. |
| 4050 | 13 | `static int inQuad(double[] q, double x, double y)` | Whether a point is inside a convex quadrilateral (corners in order): 1 inside, 0 within a hair of an edge, -1 outside. |
| 4065 | 77 | `static void draft(Path root) throws Exception` | Game.mapDraft()/adoptMap(): the map drawn on another thread is the map drawn here, a month between is caught up, and a draft for other land is not kept. |
| 4147 | 120 | `static void tiles(Squares sq)` | MapTiles: a tile's stamp holds while nothing moves, one more building restamps only the tiles it changes, the view's per-tile path follows the screen, and every cache fits the budget. |

### 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) (lines 4269-4321)

| line | len | member | says |
|---:|---:|---|---|
| 4277 | 12 | `static CityMap coastal(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts)` | A city owning `side` districts a side about (sx, sy), every plot of them, its map drawn canonically on the ground measured. |
| 4309 | 12 | `static long[] shoreCounts(BuildingVisual.Type[] types)` | The section's town: Jerus's mix at a tenth, one of each of the refinery's units, two of each terminal and one of each tank farm. |

### the shore's cost follows what a month changes (SL1, 2026-10-28) (lines 4322-4825)

| line | len | member | says |
|---:|---:|---|---|
| 4346 | 7 | `static boolean blocksByLoop(CityShore shore, long x, long y)` | Whether any work of `shore` blocks world plot (x, y), by the loop over every work that CityShore.blocks() was before SL1: the reference. |
| 4355 | 4 | `static CityShore.Work atByLoop(CityShore shore, long x, long y)` | The first work whose box holds world plot (x, y), by the loop over every work: the reference. |
| 4361 | 6 | `static void askBoth(CityShore shore, long x, long y, long[] counts)` | Whether blocks() and at() answer as the loops do at world plot (x, y); `counts` takes {asked, blocked, differ}. |
| 4369 | 15 | `static long[] blocksAgainstLoops(CityShore shore, int margin)` | blocks() and at() against the loops at every plot of each work's box, its verge and its quay and `margin` plots about them: {plots asked, of them blocked, answers that differ}. |
| 4390 | 22 | `static long[] layOneAtATime(CityMap m, int t, long[] counts, long from, long to)` | Terminals of type t laid one at a time, the model's count n from `from` to `to` (counts[t] is set to it): before each month the spot the search from where the last found room finds and the spot the search from the nea... |
| 4414 | 5 | `static double medianAfter(double[] ms, int skip)` | The median of the figures after the first `skip`. |
| 4428 | 132 | `static void shoreFollowsChange(BuildingVisual.Type[] types, long seed, CityMap coast)` | SL1 (2026-10-28): what the shore costs follows what a month changes and not the terminals standing. |
| 4561 | 264 | `static void atSea(BuildingVisual.Type[] types, long seed)` |  |

### SL1 (2026-10-28): the terminals share a lane, found once, drawn once (lines 4826-5384)

| line | len | member | says |
|---:|---:|---|---|
| 4829 | 4 | `static boolean sameRoute(BoatSchedule.Route a, BoatSchedule.Route b)` | Whether two routes are the same, point for point (their berth, offing, pivot and spread too). |
| 4835 | 5 | `static boolean sameRoutes(List<BoatSchedule.Route> a, List<BoatSchedule.Route> b)` | Whether two lists of routes are the same, route by route. |
| 4846 | 17 | `static boolean joinedOnALane(List<BoatSchedule.Route> all, int r, int j)` | Whether route r, joined at its point j (SeaRoutes.Found.joinAt()), goes on from there as the lane of one before it in `all`: that route ends in the same points, point for point, after the join point; the join point li... |
| 4865 | 6 | `static boolean onLeg(double ax, double ay, double bx, double by, double px, double py)` | Whether (px, py) lies on the segment (ax, ay) to (bx, by), to a millionth of a plot. |
| 4873 | 13 | `static boolean everyLegDrawn(List<BoatSchedule.Route> routes, SeaLanes lanes)` | Whether every leg of every route is drawn: some leg of the lanes ends where it does and holds its start. |
| 4888 | 9 | `static boolean sharesNoPoint(List<BoatSchedule.Route> all, int k)` | Whether no point of route k is a point of another route's: a lane of its own. |
| 4903 | 17 | `static void sharedLanesOnTheCoast(World w, CityMap m, BuildingVisual.Type[] types)` | Section 9's lanes shared (SL1): on the coast's own terminals, fewer routes are searched than there are routes and the rest joined to a lane, each going on from its join point as a lane's own points, point for point; a... |
| 4927 | 56 | `static void lanesOnAMask()` | Section 9's lanes on a mask of the sea (SL1): a straight coast, a headland wider than JOIN_M, a far shore, one berth inland. |
| 4990 | 73 | `static void routesFoundOnce(World w, CityMap m, BuildingVisual.Type[] types, int gct, long[] counts)` | Section 9's routes found once, on the coast's map (SL1): a terminal laid is routed alone onto the routes kept (one new, none searched again, the kept the same objects), a tank farm laid changes no berth and finds noth... |
| 5069 | 62 | `static void routesAt10B(CityMap m)` | Section 9's routes at 10 billion (SL1): PROTO_LANES berths along a mask coast of BoatProto's span, each route found from scratch within ROUTES_ALL_MS, a month's new terminals routed onto the kept ones within ROUTES_MO... |
| 5148 | 236 | `static void trains(Squares sq, Game city)` | Section 9's trains (0.8.2, batch FR1; spec-freight 4.4, D16): on the yard city (yardCity()), the routes run from the world line along the track into each yard and onto its sidings; the trains are its railed tonnes ove... |

### FX5, slice T: real-length trains, onto clear sidings, at a pace that reads (lines 5385-6009)

| line | len | member | says |
|---:|---:|---|---|
| 5388 | 3 | `static double holdsOf(TrainSchedule.Route rt)` | The plots of train a route's siding holds: Route.holds(). |
| 5393 | 3 | `static double ladderOf(TrainSchedule.Route rt)` | How far along a route its siding's ladder begins, in plots: Route.enters(). |
| 5398 | 3 | `static double legSecondsOf(double length)` | Seconds a leg of a route `length` plots long takes: its length over the pace, no less than the shortest leg and no more than the longest. |
| 5403 | 3 | `static double standSecondsOf(TrainSchedule.Kind k)` | Seconds a train of a kind stands on its siding: a base and so much a wagon. |
| 5411 | 6 | `static List<TrainSchedule.Route> handYard(double first, double second)` | A hand-made yard (FX5 T): three sidings off a ladder of 3 plots from a main line 64 plots long, the first holding `first` plots of train and the other two `second`. |
| 5419 | 3 | `static TrainSchedule.Route handRoute(int siding, double length)` | A hand-made route `length` plots long: the world line 64 plots, a ladder of 3, and the siding the rest, all on one line. |
| 5424 | 4 | `static double frontAt(TrainSchedule.Call c, TrainSchedule.Route rt, double t)` | How far along its route a call's train has its front at t, in plots; NaN while it is not on the line. |
| 5430 | 6 | `static boolean onSiding(TrainSchedule.Call c, TrainSchedule.Route rt, double t)` | Whether a call's train is on its route's siding at t: from its front reaching the ladder in to its rear leaving it out. |
| 5438 | 9 | `static int mostOnASiding(TrainSchedule month, List<TrainSchedule.Route> routes)` | The most trains on one siding at once, over a month sampled 2,000 times. |
| 5449 | 9 | `static boolean rearOnSiding(TrainSchedule.Call c, TrainSchedule.Route rt)` | Whether a standing train's rear vehicle lies on the route's last leg, its siding: the rear locomotive's middle between the leg's start and its end. |
| 5466 | 89 | `static void trainsByLength(List<TrainSchedule.Route> routes, TrainSchedule mix)` | Section 9's trains at their real length, on sidings that hold them, at a pace that reads (FX5, slice T; Jerus, 2026-10-20: "real trains are really long", "the rail depots would have to be redesigned to accomdoate such... |
| 5557 | 5 | `static int standingOn(CityMap m, int yi)` | The sidings of yard yi of the map that wagons stand on in the picture the map has: the tiles' count, scaled for the yard's sidings, never all of them. |
| 5568 | 36 | `static void trainsAndTheWagons(CityMap m, int kinds)` | Section 9's routes and the wagons standing in the yards (FX5, slice T; FX3's star 6: a train stood over the yard's own wagons): with none, half and all of the fleet's wagon sets standing, no route is laid to a siding ... |
| 5623 | 84 | `static void trainsOnTheCurves(CityMap m, List<TrainSchedule.Route> routes, TrainSchedule mix)` | Section 9's trains on the curves (FX5, 0.8.18; Jerus, 2026-10-20: "do trains make 90 degree turns?"): on the yard city's routes, at every sampled moment of the month's trains and at fronts all along every route, (a) n... |
| 5713 | 6 | `static boolean onLadder(TrainSchedule.Route rt, double fromEnd)` | Whether a point `fromEnd` plots before a route's end lies on its yard's ladder (FX5): the S between the fan's apex and the siding's start, the siding being the route's last leg - within a locomotive's length either side. |
| 5721 | 3 | `static double headingApart(double a, double b)` | How far apart two headings are, in degrees, 0 to 180. |
| 5726 | 25 | `static boolean onDrawnTrack(CityMap m, java.util.Map<Long, int[]> tiles, double x, double y, int px, int w)` | Whether the pixel under plot point (x, y), or one of its eight neighbours, is the railway's as the tile at 8 px a plot draws it (its tile rastered once into `tiles`). |
| 5765 | 55 | `static void trucksAtTenBillion(long cx, long cy)` | Section 9's trucks at 10 billion (FR9): CityMap.PLANS_KEPT districts' trips, the most the map draws, each district's schedule at its 1/PLANS_KEPT of the prototype's tonnes scaled by people (PROTO_TONNES over PROTO_PEO... |
| 5828 | 17 | `static List<TrainSchedule.Route> syntheticYards(long cx, long cy, int side)` | A city of yards about (cx, cy) for the frame at 10 billion: side x side yards on a grid four cells apart, each with its YARD_TRACKS routes in from one gateway WORLD_PLOTS west - along the row to its column, down the c... |
| 5862 | 12 | `static List<BoatSchedule.Route> protoLanes(long cx, long cy)` | BoatProto's 20,000 lanes about (cx, cy): berths at hashed places along its coast's span at 10B - sqrt(10B / 5,000) x 33 plots, its "city's span in plots (rough)" - every other one liquid bulk's, the rest dry bulk's. |
| 5876 | 5 | `static int tileGround(World w, long x, long y)` | The world's ground at a plot as the painter reads it: its tile's (World.tileTerrain()), what the shore's search and the routes read. |
| 5883 | 19 | `static boolean connected(java.util.Set<Integer> cells)` | Whether a set of cells (ci + cj x CELLS_A_SIDE) is one piece, eight ways: a corner touching counts, as DistrictPlan.campusNext() opens them. |
| 5904 | 5 | `static String worksWords(CityShore shore, BuildingVisual.Type[] types)` | The shore's works by type, in words. |
| 5911 | 17 | `static Ports tradeAtSea(double liquid, double dryBulk, double general, double boxes)` | A month billed with these tonnes of each kind shipped out, all of them by sea: each on the first good of its kind that is not crude. |
| 5936 | 73 | `static void oilAtSea()` | The game's own platform, its wells and its pipe on the map (WellCheck's sea town): Game.mapAtSea() puts the jacket in the middle of its slotted sites and its wells on them, the pipe from the jacket to the founding sit... |

### 10. THE MAP ON EARTH (0.7.119, batch E5) (lines 6010-6425)

| line | len | member | says |
|---:|---:|---|---|
| 6036 | 107 | `static void onEarth(BuildingVisual.Type[] types) throws Exception` | Section 10. |
| 6151 | 73 | `static void farWindow(String name, World w, EarthData data)` | One window drawn as a far node at WINDOW_STRIDE and at CLOSE_STRIDES: closer, the region but for its rivers stroked wider; at the window's stride every river stroked at least FAR_RIVER_HALF_PX a side, every small lake... |
| 6231 | 60 | **type** `static final class Water` | The rivers and small lakes under a far node, as the check reads them from the data itself: each run the node would stroke (its median width at least FAR_RIVER_SHARE of a pixel), its segments projected exactly, and eac... |
| 6237 | 29 | `Water(EarthTerrain e, EarthData data, long x0, long y0, int stride)` _(in MapCheck.Water)_ |  |
| 6268 | 12 | `boolean inStroke(int i, int j, boolean least)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies within a river's stroke: its least half-width alone (`least`), or its own. |
| 6282 | 8 | `boolean inCore(int i, int j)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies in a small lake's sure part: within its radius x the root of 1 - BLOB_FRAY / 2, which its blob holds whatever its noise. |
| 6299 | 36 | `static void seam(String where, World w, int stride, EarthData data, boolean water)` | A node holding the 180th meridian (its middle on the spot, which is on it) and its neighbours moved SEAM_SHIFT_PX east and west: on the half each shares with it, pixel for pixel the same; each side of the line's land ... |
| 6337 | 88 | `static void fullFile(int widest, EarthTerrainCheck.Spot rot) throws Exception` | Section 10 on the full file, when it is named (-Dcbs.earth): the longest segment, every stride's cost, the whole world, the small lakes' area and Chukotka's seam. |

### 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) (lines 6426-6898)

| line | len | member | says |
|---:|---:|---|---|
| 6442 | 4 | `static void handCash(Game city)` | Tops the played city's cash up to PICTURE_CASH_MONTHS months of its GDP when it holds less: the money a young city the playtest left broke needs for the advisor's purchases to happen. |
| 6453 | 360 | `static void pictures(Game city, Squares sq)` |  |
| 6815 | 5 | `static Map<CityMap.District, DistrictPicture> picturesOf(CityMap m)` | A district's pictures, every one asked for: each district of the map to its picture (null where it has none). |
| 6822 | 5 | `static long[] countsOf(CityMap.District d, int types)` | A district's counts by type id. |
| 6829 | 5 | `static String codeName(int c)` | A picture's code's name, for the prints. |
| 6843 | 26 | `static byte[] pictureSource(CityMap m, CityMap.District d, int[] boxPlots)` | The plots a district's picture is drawn from, read here from its plan as the painter reads it (CityMap.Drawn: its streets, its boxes by tile) and its own plan's inputs (the runs over its frame, the mines on their site... |
| 6870 | 5 | `private static void fill(byte[] src, int x0, int y0, int w, int h, byte code, int[] counted)` |  |
| 6877 | 15 | `static boolean[] runPixelsOf(CityMap m, long x0, long y0, long plots)` | Which pixels of a far node at (x0, y0), `plots` a pixel, hold a plot of the city's highways or track: every plot of every stretch walked, each 45-degree step's corner with it (CityRuns.onStretch()). |
| 6893 | 5 | `private static void markPixel(boolean[] out, long x, long y, long x0, long y0, long plots)` |  |

### 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) (lines 6899-7143)

| line | len | member | says |
|---:|---:|---|---|
| 6909 | 196 | `static void miniMap(Game city, Squares sq) throws Exception` |  |
| 7111 | 32 | `static MiniMapFrame.Box builtUpByLeaves(CityMap m)` | The built-up ground found another way, for the check: every leaf of the grid cut to each district with buildings it lies in, their bounds; a built district with none owned its whole square. |

### 13. THE GLOBE ON SCREEN (0.7.135, batch E9) (lines 7144-7658)

| line | len | member | says |
|---:|---:|---|---|
| 7157 | 461 | `static void globeOnScreen(Squares sq) throws Exception` |  |
| 7620 | 4 | `static byte groundClass(String words)` | The class a legend's ground words name. |
| 7626 | 15 | `static double foundingParting(double lat, double lon, double km)` | How far the founding globe's two drawings part at a scale, in pixels at worst over its canvas's corners and edges: the globe through GlobeFrame against the ground drawn round the view's centre (Lambert's plane there, ... |
| 7643 | 15 | `static CityMap earthX1(Squares sq) throws Exception` | Jerus's city x 1 on the real Earth at Rotterdam, EarthCheck's fixture (section 10's window): null when the fixture is not beside the classes. |

### 14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 7659-8331)

| line | len | member | says |
|---:|---:|---|---|
| 7678 | 362 | `static void buyingFromTheMap(Game g) throws Exception` |  |
| 8048 | 10 | `static boolean buysQuietly(Game g, LandParcel p)` | The city buys land as a player would: cash made up to the price when short, then Game.buyLandParcel (LandManager.buyParcel). |
| 8063 | 10 | `static List<LandParcel> listedAgain(CityLand land, LandMarket market, LandParcel was, List<Integer> notIds)` | The listed offers, not those ids, whose rectangle shares ground the city does not own with a rectangle that was listed: the place listed again over that ground under a new id (its rectangle is sized by the city as it ... |
| 8079 | 119 | `static void picksLast(Game g) throws Exception` | MS1 (Jerus, 2026-10-26: "on monthly update it doesnt reset ... |
| 8210 | 37 | `static void farOnThePlane(Game g)` | MS1 (Jerus, 2026-10-26: "you cant zoom in on areas of the map far away from the city, and if zoomed in there is a wall that you can cross"): the wall and the stop were the globe's (GlobeCheck 2b); a generated world's ... |
| 8257 | 74 | `static void plainDragSelects(Game g)` | MS1 (Jerus, 2026-10-26: "i should be able to not only click and shift click, but also drag and select"): what a left press on the expanded map starts, a box or a pan, is the model's (LandMap.pressBoxes(), which MapVie... |

### 14e. BLOCKS PICKED (BL1) (lines 8332-8977)

| line | len | member | says |
|---:|---:|---|---|
| 8353 | 4 | `static boolean blockFree(LandGrid g, int level, long bx, long by)` | Whether block (bx, by) of `level` is on the world and not wholly the city's. |
| 8359 | 9 | `static java.util.Set<Long> blocksMetBy(CityLand land, LandParcel o, int level)` | The blocks of `level` an offer's free ground meets: every block holding a plot the offer holds, the plots counted one by one. |
| 8370 | 7 | `static boolean completesAnOffer(CityLand land, LandMarket market, int level, java.util.Set<Long> blocks)` | Whether some standing offer's whole free ground lies in these blocks: such a set is, to the model, that offer picked. |
| 8383 | 24 | `static long[] chainFrom(CityLand land, LandMarket market, int level, java.util.Set<Long> avoid)` | A free block on the city's frontier and a direction outward from it along which the next two blocks are free and not on the frontier - three in a row, the middle one joined only through the first - none within a block... |
| 8409 | 21 | `static boolean allJoined(CityLand land, int level, java.util.Set<Long> blocks, List<LandParcel> offers)` | The blocks a Picks holds that join the city, worked out another way: every block of the set, reached from one on the city's frontier or beside a picked offer's free ground by steps through the set. |
| 8432 | 4 | `static boolean edgeNeighbour(double[] c, long x0, long y0, long b)` | Whether a free cell {x0, y0, x1, y1} lies across an edge of the block at (x0, y0) of side b, sharing some of its length: beside the block, not on it. |
| 8438 | 32 | `static java.util.Set<Long> floodExpect(CityLand land, int level, long[] box, java.util.Set<Long> picked)` | The blocks of a box of plots {x0, y0, x1, y1} that join the city, worked out block by block: free blocks of the box on the frontier or beside a block already picked, and the free blocks of the box reached from them by... |
| 8472 | 5 | `static List<LandParcel> offersIn(LandMarket market, List<Integer> ids)` | The offers behind these picks' ids (the positive ones), as the office lists them. |
| 8479 | 5 | `static java.util.Set<Long> everyPicked(CityLand land, LandMarket market, int level, LandMap.Picks picks, List<Integer> ids)` | The blocks a Picks holds, and those of the offers it holds, as one set. |
| 8486 | 8 | `static Map<Long, Integer> coverage(List<long[]> rects, int level)` | The blocks the rectangles cover, counting each time: block -> how many rectangles hold it. |
| 8500 | 21 | `static boolean edgeOf(double[][] runs, java.util.function.BiPredicate<Long, Long> in, long plots)` | Whether an outline's runs are the edge of the ground `in` says: they close (every run's end is another's start, as often), the plot on the right of every unit of every run is the ground and the plot on its left is not... |
| 8523 | 3 | `static boolean inBlocks(java.util.Set<Long> blocks, int level, long x, long y)` | Whether plot (x, y) is in a block of the set at `level`. |
| 8532 | 15 | `static LandParcel ownBlocks(CityLand land, LandMarket market, int level, int nth)` | The nth standing offer (in the office's order) of the city's own level that holds two or more blocks of free ground, each block's free ground wholly inside the offer's rectangle - one that picking blocks makes the off... |
| 8549 | 5 | `static java.util.Set<Long> blocksOf(long[]...at)` | Blocks as a set, from {bx, by} pairs. |
| 8560 | 9 | `static LandParcel ownBlocksKeepingLevel(CityLand land, LandMarket market, int level, long more)` | The first offer ownBlocks() finds (in the office's order) whose purchase, and `more` plots bought after it, leave the city's block level `level` where it is - LandGrid.levelFor() of the plots the city would own then i... |
| 8575 | 10 | `static long buyTheLargestOffer(Game g)` | The city buys, as it buys any ground (Game.buyLandParcel(), handed the price), the standing offer with the most free ground that it can buy: the ground that takes a city standing just under the plots of its next block... |
| 8586 | 391 | `static void blocksPicked(Game g) throws Exception` |  |

### 14f. THE EXACT GROUND ON THE SCREEN (BL1, slice D) (lines 8978-9291)

| line | len | member | says |
|---:|---:|---|---|
| 8996 | 4 | `static long[] plotIn(int level, long key)` | A plot near the middle of a block of `level`: {x, y}. |
| 9002 | 4 | `static long unownedIn(LandGrid grid, int level, long key)` | The plots of a block the city does not own. |
| 9008 | 8 | `static long[] ownedAndAll(CityLand land, List<double[]> cells)` | The cells of a fill: how many plots of them the city owns, and how many they hold in all. |
| 9017 | 234 | `static void groundOnScreen(Game g) throws Exception` |  |
| 9253 | 3 | `static String owner(int owner)` | A pick's owner in words. |
| 9258 | 4 | `static boolean clears(LandMap.Picks picks, Game g, int id)` | Whether a plain click on an offer (only) clears a note that stood. |
| 9264 | 10 | `static java.util.Set<Integer> boxByPlots(CityLand land, List<LandParcel> offers, double x0, double y0, double x1, double y1)` | The offers a box of plots meets, plot by plot: every plot under (x0, y0)-(x1, y1) in some part, not the city's, inside an offer's rectangle. |
| 9276 | 10 | `static boolean inOrder(List<Integer> ids, List<LandParcel> listing)` | Whether these ids stand in the listing's order. |
| 9288 | 3 | `static boolean sameFigure(double a, double b)` | Two money or area figures the same to a billionth of the larger. |

### 15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; (lines 9292-9658)

| line | len | member | says |
|---:|---:|---|---|
| 9305 | 3 | `static boolean materialsOf(BuildingsTemplate t)` | Whether a template is one of the nine industries'. |
| 9310 | 3 | `static long materialsCount(BuildingVisual.Type t)` | The fixture's count of a materials type: one of a works on more than one site, two of anything else - so each slot holds two kinds and the larger is laid first. |
| 9314 | 264 | `static void materials(long seed)` |  |
| 9580 | 3 | `static long countZone(List<CityMap.Tinted> tint, int z)` | How many districts a zone's tint takes. |
| 9585 | 7 | `static java.util.Map<Long, Integer> sitesUnder(CityMap m)` | Each site under a works on a map, by its key, and the works' type. |
| 9594 | 12 | `static int lookSign(BuildingVisual.Type t)` | The colour a look shows on a box of it rastered (TileRaster's THE MATERIALS AS THEMSELVES): a pit's benches, a shaft's headframe, a quarry's stone, the leach plant's pond, the wash plant's conveyor, a camp's logs, pla... |
| 9614 | 22 | `static long[] materialsPlace(World w, int side, int[] need)` | The world cell nearest the founding site's whose square of `side` districts (as CityMap.square() lays it round the cell's middle) is DRY_PLACE dry in its middle 3 x 3 districts and holds fields centred in it with at l... |
| 9641 | 17 | `static int[] slotSites(World w, long x, long y, int side)` | The sites of each slot of the fields centred in the square of `side` districts CityMap.square() lays round plot (x, y). |

### 16. THE AVENUES DRAWN (batch CL4; the study spec-city-layout 5.2) (lines 9659-10015)

| line | len | member | says |
|---:|---:|---|---|
| 9693 | 7 | `static CityMap avenueCity(Squares sq)` | The avenues' city, built from the copies' constants (Squares): twice alike gives a twin. |
| 9702 | 52 | **type** `static final class AvTiles` | One map's tiles, kept as the section samples them: the rastered image at PX and what the painter was given and made of each plot. |
| 9712 | 1 | `AvTiles(CityMap m)` _(in MapCheck.AvTiles)_ |  |
| 9714 | 1 | `private static long key(long tx, long ty)` _(in MapCheck.AvTiles)_ |  |
| 9717 | 16 | `private void load(long tx, long ty)` _(in MapCheck.AvTiles)_ | The tile (tx, ty) as the painter makes it, loaded on first use. |
| 9734 | 5 | `private int at(java.util.Map<Long, byte[]> what, long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 9741 | 1 | `int code(long x, long y)` _(in MapCheck.AvTiles)_ | The plan's street code, the avenue's mark, the painter's use, the ground and the flags of world plot (x, y). |
| 9742 | 1 | `int mark(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 9743 | 1 | `int use(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 9744 | 1 | `int ground(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 9745 | 1 | `int flag(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 9748 | 5 | `int pixel(long X, long Y)` _(in MapCheck.AvTiles)_ | The pixel of the near view's raster at world pixel (X, Y). |
| 9756 | 4 | `static long[] avenueOrigin(CityMap m)` | The lattice node the avenues run out from, nearest the founding site (DistrictPlan's avSetup), in world plots: {x, y}. |
| 9762 | 14 | `static boolean avenueOpen(AvTiles t, long ox, long oy, int a, int k)` | Whether the avenue is open at step k of arm a, as the near view draws it: the staircase's plots S_k, C_(k+1) and S_(k+1) laid as a surfaced street on the ground, and the outer plot O_(k+1) between them owned dry groun... |
| 9778 | 3 | `static boolean streetColour(int c)` | Whether a pixel is a surfaced street's: the paved colour, the gravel's, or its grain. |
| 9783 | 12 | `static int handMark(int ox, int oy, int arms, int len, int x, int y)` | Where a hand-built tile's avenues stand: the avenue plots of the arms `arms` (bit a) from origin (ox, oy) for `len` steps - a plot two arms share the first arm's mark, as DistrictPlan lays it - and 0 for none. |
| 9797 | 8 | `static int[] handAvenue(int ox, int oy, int arms, int len, JunctionCheck.Streets extra, boolean withOrigin)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: the avenue plots of the arms `arms` from the origin laid as arterial streets with their marks, and the streets `extra` besides; the origin given ... |
| 9806 | 209 | `static void avenuesDrawn(Squares sq)` |  |

### 17. THE CURVES DRAWN (batch CL4, slice D; the study spec-city-layout 5.2) (lines 10016-10618)

| line | len | member | says |
|---:|---:|---|---|
| 10056 | 14 | **type** `record CvFound(CityMap.District d, CityMap.DrawnCurve c, long ox, long oy)` | A curve found in a Drawn, with where its district's frame lies in the world. |
| 10058 | 5 | `double[][] line()` _(in MapCheck.CvFound)_ | Its line in the world's plots: {xs, ys}. |
| 10065 | 4 | `long[] plot(int k)` _(in MapCheck.CvFound)_ | Plot k of its list in the world's plots: {x, y}. |
| 10072 | 65 | **type** `static final class CvTiles` | One map's tiles as the curves section samples them at `px` a plot: the rastered image, and what the painter was given and made of each plot. |
| 10084 | 1 | `CvTiles(CityMap m, int px)` _(in MapCheck.CvTiles)_ |  |
| 10086 | 1 | `CvTiles(CityMap m, int px, boolean bare)` _(in MapCheck.CvTiles)_ |  |
| 10088 | 1 | `private static long key(long tx, long ty)` _(in MapCheck.CvTiles)_ |  |
| 10090 | 20 | `private void load(long tx, long ty)` _(in MapCheck.CvTiles)_ |  |
| 10111 | 5 | `private int at(java.util.Map<Long, byte[]> what, long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 10118 | 1 | `int code(long x, long y)` _(in MapCheck.CvTiles)_ | The plan's street code, the painter's use, the curve mark and the flags (1 owned, 2 bridge, 4 beneath) of world plot (x, y). |
| 10119 | 1 | `int use(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 10120 | 1 | `int mark(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 10121 | 1 | `int flag(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 10124 | 5 | `int type(long x, long y)` _(in MapCheck.CvTiles)_ | The type id of the building on world plot (x, y), or -1 for none. |
| 10131 | 5 | `int pixel(long X, long Y)` _(in MapCheck.CvTiles)_ | The pixel of the raster at world pixel (X, Y). |
| 10139 | 6 | `static boolean cvStreetColour(int c)` | Whether a pixel is a surfaced street's, owned or dimmed as ground the city has not bought is: the paved colour, the gravel's or its grain. |
| 10147 | 6 | **type** `static final class CvAudit` | What an audit of one curve's drawn line found. |
| 10155 | 5 | `static java.util.Set<Long> cvPlotSet(CvFound f)` | The own plots of a curve (the first `own` of its list) as a set of world plot keys. |
| 10170 | 89 | `static CvAudit cvAudit(CvTiles t, CvFound f, java.util.Set<Integer> fills)` | One curve's line audited on the raster t (THE CURVES DRAWN): samples every pixel along it, from its first point to its last. |
| 10261 | 4 | `static double cvTurn(double x0, double y0, double x1, double y1, double x2, double y2)` | The signed turn, in radians, from the chord (x0, y0)-(x1, y1) to (x1, y1)-(x2, y2). |
| 10267 | 7 | `static boolean cvCrossesTile(double[][] line)` | Whether the line passes from one tile to another. |
| 10276 | 8 | `static java.util.List<long[]> cvTiles(CvFound f)` | The tiles about a curve's plots: its bounding box in tiles, a tile more on each side, as {tx, ty} pairs. |
| 10286 | 19 | `static int[] handCrescent(JunctionCheck.Streets extra)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: a crescent in the cell whose interior's north-west plot is (0, 1), bulging toward +x, its staircase laid as street plots with its curve, and the ... |
| 10307 | 19 | `static int[] handShore(boolean bridge)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: a street along a cut, a path of street plots along row 10 from column 2 to 20 with its curve (StreetCurves' line, its band beside it) - with `bri... |
| 10327 | 291 | `static void curvesDrawn(Squares sq)` |  |

### 18. A MONTH REDRAWS ONLY WHAT MOVED (batch UP3, docs/queue/UP3.md) (lines 10619-11351)

| line | len | member | says |
|---:|---:|---|---|
| 10683 | 53 | **type** `static final class FarScreen` | One far screen of section 18: a MapFrame fitted on the city's frame and zoomed out to FAR (or on to a level that draws no pictures), its nodes, their ground, and what each was last drawn from and as - the way ui/MapVi... |
| 10701 | 29 | `FarScreen(String name, CityMap m, MiniMapFrame.Box frame, boolean wide)` _(in MapCheck.FarScreen)_ |  |
| 10732 | 3 | `boolean overlaps(int i, long x0, long y0, long x1, long y1)` _(in MapCheck.FarScreen)_ | Whether node i lies over any of the box x0 to x1 and y0 to y1 (in plots). |
| 10738 | 17 | **type** `static final class RedrawMonth` | What one month did to one fixture's screens, as Redraw.month() found it; times in ms. |
| 10752 | 1 | `double nowSum()` _(in MapCheck.RedrawMonth)_ |  |
| 10753 | 1 | `double todaySum()` _(in MapCheck.RedrawMonth)_ |  |
| 10757 | 321 | **type** `static final class Redraw` | A fixture of section 18: a map grown a month at a time, its screens drawn two ways (today's whole on `tr`, now's kept on `tk`) and held equal. |
| 10784 | 21 | `Redraw(String name, CityMap m, long[] counts, int divisor, boolean withFar, boolean wide, boolean withL0, boolean checkLinks)` _(in MapCheck.Redraw)_ |  |
| 10806 | 3 | `boolean runType(int t)` _(in MapCheck.Redraw)_ |  |
| 10811 | 5 | `int biggest()` _(in MapCheck.Redraw)_ | The built type with the most buildings that is no run's and no mine: the one a single-district month raises. |
| 10817 | 5 | `static long sum(MapTiles.KeptNode[] keeps, int what)` _(in MapCheck.Redraw)_ |  |
| 10824 | 115 | `RedrawMonth month(int kind, int number, boolean timed)` _(in MapCheck.Redraw)_ | Plays one month and draws it; `timed` months are the ones whose figures are read. |
| 10946 | 61 | `long[] drawStamp(RedrawMonth r, int round, java.util.Set<CityMap.District> wanted)` _(in MapCheck.Redraw)_ | The stamp and the preview drawn two ways, equal or not noted: returns the ns now's took and today's. |
| 11009 | 11 | `long todayPass(FarScreen fs, int round, boolean warm)` _(in MapCheck.Redraw)_ | Today's draw of a far screen: every node in the month's round, the partial ones after; ns. |
| 11028 | 33 | `long[] drawFar(FarScreen fs, int s, RedrawMonth r, int round, java.util.Set<CityMap.District> wanted)` _(in MapCheck.Redraw)_ | A far screen drawn two ways, the ns of each: now's as ui/MapView.paintNodes() draws it (nodeKey() for every node in the month's round and for the partial ones after; drawn when the key moved, or it was drawn partial a... |
| 11063 | 14 | `void verify(FarScreen fs)` _(in MapCheck.Redraw)_ | Every node of the screen drawn from the key it has now holds the pixels a fresh nodePixels() gives it: those held through the month are the ones that count. |
| 11079 | 5 | `static int differ(int[] a, int[] b)` |  |
| 11085 | 6 | `static double median18(java.util.List<Double> l)` |  |
| 11092 | 5 | `static double worst18(java.util.List<Double> l)` |  |
| 11098 | 6 | `static int medianInt(java.util.List<Integer> l)` |  |
| 11106 | 16 | `static CityMap hisCopy(Squares sq, double times)` | Jerus's city at `times` on the design's square city at the dry place, as Squares builds its copies. |
| 11128 | 16 | `static long[] purchaseBlock(CityMap m, CityLand land)` | The block of PURCHASE_SIDE plots to buy: wholly inside the box of the city's built-up ground (so the stamp's frame is the one it was, however the district's owned ground grows), none of it owned, and the most ground o... |
| 11145 | 130 | `static void redrawsOnlyWhatMoved(Game city, Squares sq)` |  |
| 11277 | 37 | `static void oneDistrict(Redraw w, RedrawMonth one, int s, boolean picturesOn)` | Check 3 on one month that raised one built type by one: the far nodes whose key moved against the 3 x 3 districts round the one district that moved, and against every node in view. |
| 11316 | 35 | `static void report18(Redraw w)` | The figures section 18 prints for a fixture. |

### THE RELIEF ON THE MAP (ET1 slice D; docs/studies/spec-earth2-terrain.md (lines 11352-11650)

| line | len | member | says |
|---:|---:|---|---|
| 11364 | 4 | `static int reliefColour(int c, int r)` | The colour a plot of cover `c` and relief `r` is painted, said again from TileRaster's constants (D4, D5, D19): flat ground, water and ice (which have no relief, and white wins over a slope) in their cover's colour; o... |
| 11370 | 4 | `static World.Key fixtureKey(double[] at) throws Exception` | A map's relief on EarthCheck's fixture's key at (lat, lon): the data in use (EarthTerrain.use()) and the key of the Earth world there. |
| 11376 | 144 | `static void reliefOnMap(BuildingVisual.Type[] types) throws Exception` | Section 10: the plane's plots and the far node over Medellin painted by their relief; a generated world painted as it was. |
| 11522 | 34 | `static void pictureRelief(BuildingVisual.Type[] types) throws Exception` | Section 11: a built district's far node over Medellin's hills: the greys on the city's own ground, undimmed, and nowhere else but the plan's own colours. |
| 11558 | 92 | `static void reliefWordsAndLegend(BuildingVisual.Type[] types) throws Exception` | Section 13: the words and the legend by relief; the globe's hover and its picture's codes. |

