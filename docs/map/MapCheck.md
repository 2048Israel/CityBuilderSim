# MapCheck.java - 9,846 lines · 257 methods · 102 constants · harnesses

`ham/citybuildersim/MapCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

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
> ... (279 more lines in the source)

**Uses:** [CityMap](CityMap.md) (478), [World](World.md) (450), [TilePainter](TilePainter.md) (214), [TrainSchedule](TrainSchedule.md) (208), [LandMap](LandMap.md) (197), [BuildingVisual](BuildingVisual.md) (171), [DistrictPlan](DistrictPlan.md) (167), [TileRaster](TileRaster.md) (165), [MapFrame](MapFrame.md) (164), [MapTiles](MapTiles.md) (134), [GlobeFrame](GlobeFrame.md) (103), [MiniMapFrame](MiniMapFrame.md) (93), [Game](Game.md) (68), [LandParcel](LandParcel.md) (62), [JunctionCheck](JunctionCheck.md) (62), [DistrictPicture](DistrictPicture.md) (58), [CityRuns](CityRuns.md) (54), [EarthTerrainCheck](EarthTerrainCheck.md) (46), [LongPlaytest](LongPlaytest.md) (37), [CityLand](CityLand.md) (33), [Ports](Ports.md) (33), [EarthTerrain](EarthTerrain.md) (31), [GlobePicture](GlobePicture.md) (27), [BoatSchedule](BoatSchedule.md) (25), [EarthData](EarthData.md) (24), [Resource](Resource.md) (23), [Timing](Timing.md) (22), [CityShore](CityShore.md) (21), [SeaRoutes](SeaRoutes.md) (17), [EarthCheck](EarthCheck.md) (17)... and 26 more

**Used by (8):** [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [GlobeCheck](GlobeCheck.md), [IslandNetCheck](IslandNetCheck.md), [MainLineCheck](MainLineCheck.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 492 | 1. A CITY'S MAP ADDS UP |
| 674 | · WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) |
| 808 | 2. NOTHING PLACED MOVES |
| 887 | 2b. A DISTRICT IS FULL AT WHAT ITS PLAN CAN DRAW, AND A SPILL KEEPS ITS TIME (batch CL3) |
| 1041 | 2c. A DISTRICT THAT BUYS LAND INSIDE ITSELF KEEPS ITS BUILDINGS WHERE THEY STAND (batch FX6) |
| 1303 | 2d. A RUN LAID ACROSS A DISTRICT'S BUILDINGS MOVES NONE OF THEM (batch FX7) |
| 1690 | THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY |
| 1809 | 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on |
| 1873 | 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) |
| 1979 | 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its |
| 2405 | 8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4; |
| 2573 | 8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec- |
| 3111 | 4. THE SIDECAR |
| 3323 | 5. THE COST FOLLOWS THE SCREEN |
| 3490 | 7. THE VIEW'S PURE HALF (0.7.61, batch J4) |
| 4180 | 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) |
| 4731 | · FX5, slice T: real-length trains, onto clear sidings, at a pace that reads |
| 5287 | 10. THE MAP ON EARTH (0.7.119, batch E5) |
| 5703 | 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) |
| 6159 | 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) |
| 6404 | 13. THE GLOBE ON SCREEN (0.7.135, batch E9) |
| 6783 | 14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |
| 7488 | 15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; |
| 7855 | 16. THE AVENUES DRAWN (batch CL4; the study spec-city-layout 5.2) |
| 8212 | 17. THE CURVES DRAWN (batch CL4, slice D; the study spec-city-layout 5.2) |
| 8815 | 18. A MONTH REDRAWS ONLY WHAT MOVED (batch UP3, docs/queue/UP3.md) |
| 9548 | THE RELIEF ON THE MAP (ET1 slice D; docs/studies/spec-earth2-terrain.md |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 369 | `MapCheck.MONTHS` | `120` | Months the city of section 1 is played: 120 (the design's). |
| 372 | `MapCheck.IRON_AT` | `MONTHS / 3` | The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. |
| 384 | `MapCheck.JERUS_COUNTS` | `{ 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114...` | Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2 of dry ground): every building type's count, by id - 14,214 buildings, the design's own fixture for the map's sizes; 8,204 since 0.7.71, its child... |
| 389 | `MapCheck.JERUS_PEOPLE` | `509_455` | ...his people. |
| 392 | `MapCheck.JERUS_KM2` | `89.63` | ...and his dry ground, in km2. |
| 395 | `MapCheck.JERUS_FILL` | `0.92` | His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. |
| 398 | `MapCheck.JERUS_FOOTPRINT_KM2` | `82.273` | ...the land those buildings stood on, by the catalogue his save was measured with (0.7.70's): 82.273 km2, 91.8% of his dry ground (runs/fixN2-notes.md). |
| 416 | `MapCheck.TIMES` | `{ 1, 9_814, 10_000, 19_629 }` | The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). |
| 419 | `MapCheck.DENSE` | `2` | The copy whose screen is all city, at his density, that section 3 paints: x 10,000. |
| 422 | `MapCheck.SCREEN_ACROSS` | `18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN` | The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". |
| 425 | `MapCheck.SCREEN_MS` | `80` | The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). |
| 428 | `MapCheck.SCREEN_RATIO` | `1.5` | The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). |
| 431 | `MapCheck.RECONCILE_MS` | `5` | The design's bound on a month's change at 5B and at 10B, in ms of the main thread's CPU since 0.7.100 (measured 0.76; section 5's note). |
| 434 | `MapCheck.PX` | `4` | Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). |
| 437 | `MapCheck.WARM_ROUNDS` | `3, TIMED_ROUNDS = 5` | Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. |
| 440 | `MapCheck.DRY_PLACE` | `0.97` | How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. |
| 443 | `MapCheck.DRAWN_EVERY` | `30` | The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). |
| 487 | `MapCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 902 | `MapCheck.SPILL_FIRST_CELLS` | `2, SPILL_SECOND_CELLS = 6` | The cells across the window of whole cells a spill's first district owns and its second: 2 and 6 of a district's 8 - the first draws little, the second all it is carried. |
| 1061 | `MapCheck.BOUGHT_PLOTS` | `64` | The side of a bought block, at least: 64 plots (rounded up to whole blocks of the land's level), a few cells of a district's 8 across. |
| 1328 | `MapCheck.MIX` | `{ "Low-Rise Apartments", "Small Grocery Store", "Bakery", "Elementary School"...` | What the founding district is given month by month besides houses, in turn (an apartment block, shops, a school, works): buildings of several sizes for its plan to place. |
| 1331 | `MapCheck.USED_SHARE` | `0.5` | How much of its room the founding district is brought to before a run is laid across it: half of the cells its plan may fill (CityMap.PLAN_FILL of its owned plots). |
| 1334 | `MapCheck.BUILDINGS_A_MONTH` | `3` | The buildings of the mix the founding district is given each month, beside its houses. |
| 1337 | `MapCheck.MONTHS_BETWEEN` | `4` | Months of arrivals after each change of the runs, before the next plan is drawn: the buildings that must go on the ground as the run left it. |
| 1340 | `MapCheck.HIGHWAY_MONTHS` | `60, HIGHWAY_STEP = 8` | The most months the highways are given to reach the district, and the Elevated Highways added a month while they do. |
| 1343 | `MapCheck.HIGHWAY_CLEAR` | `1` | A building keeps this far from a highway plot (batch H5: no building touches one, corners included): the reach, in plots, within which a highway laid takes a box's place. |
| 1457 | `MapCheck.RUN_KINDS` | `{ DistrictPlan.FIXED_YARD, DistrictPlan.FIXED_RAIL, DistrictPlan.FIXED_HIGHWA...` | The kinds of fixed plot the runs lay, in the order a line prints them. |
| 1459 | `MapCheck.RUN_NAMES` | `{ "yard", "rail", "highway", "reserve" }` | ...and their names, in the same order. |
| 1878 | `MapCheck.NEW_CITY_MONTHS` | `{ 0, 1, 12 }` | The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). |
| 2412 | `MapCheck.ACCESS_HIGHWAYS_ADDED` | `200, ACCESS_DEPOTS_ADDED = 3` | The access fixture's added highways and Truck Depots: enough highways to cross, three depots to place. |
| 2580 | `MapCheck.SPURS_ADDED` | `12, BULK_WORKS_ADDED = 90` | The sidings fixture's added Rail Spurs and bulk works (JunctionCheck's two bulk types, two in three the larger). |
| 2879 | `MapCheck.CORRIDOR_STAGES` | `{ { 1, 6 }, { 3, 92 }, { 6, 400 }, { 9, 1385 } }` | Spec 2.7's table on the game's own ground: the design's dry place, the city's ground a square 1, 3, 6 and 9 districts a side (4 to 35 km from its middle, the prototype's 4 to 34), and its Elevated Highways the prototy... |
| 3034 | `MapCheck.YARDS_ADDED` | `2, FREIGHT_ADDED = 1` | The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard; 0.7.72's fixture). |
| 3298 | `MapCheck.SIDECAR_BYTES` | `500_000` | The most a sidecar may be at 5 and 10 billion people, log and all, in deflated bytes (CL3): the 10B copy's measured 333,776 (294,101 without the log, in FORMAT 8; its 1,457,418 records the canonical deal's, one batch,... |
| 3301 | `MapCheck.SIDECAR_MS` | `500` | The most the 5B and 10B copies' sidecar may take to write, and to read, in ms (CL3): measured 271 and 267 at 10B (240 and 161 at 5B), and near twice that. |
| 3348 | `MapCheck.THREADS` | `java.lang.management.ManagementFactory.getThreadMXBean()` | The JVM's threads, read for the main thread's CPU time. |
| 3351 | `MapCheck.CPU_STEP_MS` | `0.1` | The coarsest step of the thread's CPU clock a bound is read on, in ms: 0.1, a twentieth of the quickest stamp timed; coarser (Windows: 15.6) and the bounds are read on the wall clock. |
| 3354 | `MapCheck.CPU_TIMED` | `cpuTimed()` | Whether a month is timed on the main thread's CPU: this JVM measures it, it is on (turned on here if it is off), and its clock steps no coarser than CPU_STEP_MS. |
| 3495 | `MapCheck.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 3498 | `MapCheck.SCREEN_POINTS` | `{ { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } }` | Points across the screen the transforms are tried at. |
| 3948 | `MapCheck.BIG_OFFER_LEVEL` | `9` | The level of the offer the overlay's clip is tried on, zoomed in as far as the view goes: 9, blocks of 15.36 km - an offer of 2 x 4 of them is over three views across even at the expanded size, which 0.7.61's corners'... |
| 4055 | `MapCheck.DRAFT_MONTHS` | `24` | Months the draft's town is played before its map is drawn: 24. |
| 4185 | `MapCheck.SHORE_SIDE` | `3` | The section's city: three districts a side, all of it owned and drawn canonically on its measured ground - the playtest's coast (its bay, its south shore, its lagoon) - about a site one district west of the default wo... |
| 4202 | `MapCheck.BOAT_FRAME_MS` | `0.5` | The design's bound on a frame's boats at 10 billion, in ms (spec-oil 5's O13 row: "a frame's boats <= 0.5 ms at 10B, measured 0.38"). |
| 4205 | `MapCheck.BOAT_FRAMES` | `200` | Frames timed for that bound, a round: 200 at times through the month; the least of TIMED_ROUNDS rounds' means after WARM_ROUNDS. |
| 4482 | `MapCheck.TRAIN_FRAME_MS` | `BOAT_FRAME_MS` | The design's bound on a frame's trains at 10 billion, in ms: the boats' (spec-freight 4.4, "the same structure is well inside budget"). |
| 4485 | `MapCheck.TRAIN_YARDS_A_SIDE` | `40` | Yards a side of the synthetic city the frame is timed on at 10 billion: 40, 1,600 yards, 12,800 routes on one main line. |
| 4952 | `MapCheck.MOST_TURN_DEG` | `30` | The most two neighbouring vehicles of a train may differ in heading, in degrees: 30 (FX5; on 0.8.14 a corner turned them 90 and a ladder 60). |
| 4955 | `MapCheck.LADDER_TURN_DEG` | `45` | ...and on a yard's ladder, the tightest curve the railway has: 45 degrees (FX5: an S of up to a few plots across over YARD_LADDER, 3 plots, turns a depot's neighbours 40; the long yards slice Y lays will lengthen the ... |
| 5124 | `MapCheck.SCREEN_W_PX` | `1389, SCREEN_H_PX = 868` | The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. |
| 5127 | `MapCheck.PROTO_TONNES` | `896_308, PROTO_PEOPLE = 469_092` | BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. |
| 5130 | `MapCheck.PROTO_LANES` | `20_000` | BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). |
| 5132 | `MapCheck.PROTO_LANE_DX` | `200, PROTO_LANE_DY = - 1_500` | ...each lane's far end, in plots from its berth: 200 east and 1,500 north (north is less y). |
| 5292 | `MapCheck.WINDOW_STRIDE` | `EarthTerrainCheck.WINDOW / MapFrame.NODE_PX` | The stride the study's windows are drawn at: EarthTerrainCheck.WINDOW / NODE_PX, 16 plots a pixel - one node is the window, a world cell, and from it the far node strokes its own water and dithers its forest (EarthTer... |
| 5295 | `MapCheck.CLOSE_STRIDES` | `{ 2, 4, 8 }` | The strides under WINDOW_STRIDE the windows are drawn at too, where the far node is E2's region but for its rivers: 2, 4 and 8 plots a pixel. |
| 5298 | `MapCheck.SEAM_SHIFT_PX` | `MapFrame.NODE_PX / 2` | How far a node is moved to hold the 180th meridian with its neighbour, in pixels: half a node, 64 - whole blocks of EarthTerrain.FAR_BLEND_PX, so two nodes read every pixel they share alike. |
| 5301 | `MapCheck.CHUKOTKA_LAT` | `66, CHUKOTKA_LON = 180` | The full file's seam: the 180th meridian in Chukotka, 66 N, rivers and lakes either side of it. |
| 5304 | `MapCheck.CHUKOTKA_STRIDE` | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | ...drawn at 64 plots a pixel (1.9 km; a node 246 km across), EarthTerrain.FAR_TILE_BLEND_STRIDE: every river stroked (FAR_RIVER_SHARE's 23 m), each tile's points blended. |
| 5307 | `MapCheck.LAKES_LAT` | `62, LAKES_LON = 26` | The full file's small lakes: Finland's lake district, 62 N 26 E. |
| 5310 | `MapCheck.LAKES_NODES` | `3` | ...over this many nodes a side about the spot at WINDOW_STRIDE: 3, 184 km, a few thousand lakes. |
| 5708 | `MapCheck.PICTURE_MONTHS` | `MONTHS` | Months section 11 plays the played city on, every district's picture asked after each as a far view asks for it: MONTHS more. |
| 5711 | `MapCheck.PICTURE_SIGMAS` | `4` | How far a picture's share of a kind may stand from its plots' share: this many of its binomial spreads (DistrictPicture.spread()), and a pixel - four, about one district in 16,000 past it by chance. |
| 5714 | `MapCheck.NODE_ROUNDS` | `TIMED_ROUNDS` | Rounds a far node is timed over after WARM_ROUNDS, the least its time: TIMED_ROUNDS. |
| 6164 | `MapCheck.FAR_DISTRICTS` | `4` | How far out the played city's far block is bought: 4 districts past its built-up ground's east edge (31 km) - districts of its own, nothing built in them. |
| 6167 | `MapCheck.STAMP_WARM` | `20, STAMP_TIMED = 10` | Rounds the stamp is drawn before it is timed, and rounds timed, the least its time: 20 and 10 - it is drawn every month of play, so its steady state is what is timed, the JIT's compiling done (about 5 ms a round; sect... |
| 6409 | `MapCheck.KEY_FRAMES` | `60` | Frames of the keys' second: 60, the pump's (a sixtieth each). |
| 6412 | `MapCheck.KEY_SLOP_PX` | `1e-3` | How far a second of the keys may miss KEY_PX_A_SECOND, in pixels: a thousandth (★E9) - on the plane it is exact; on the globe each frame's drag keeps the point grabbed under the pointer to GlobeCheck's POINTER_PX. |
| 6415 | `MapCheck.MATRIX_PLOTS` | `1e-6` | The composed matrix's worst against the camera's and the plane's own projections, in plots: a millionth (★E9; measured 4e-10). |
| 6788 | `MapCheck.BOXES` | `200` | Boxes drawn at random on the played city's opening view, each tried against every plot under it: 200. |
| 6791 | `MapCheck.GLOBE_BOXES` | `60` | ...and through the globe's camera, each against a point at every free plot's middle: 60. |
| 6794 | `MapCheck.FIELD_BUYS_MOST` | `200` | The most offers the city buys toward an iron field for one to be on offer: 200, MiningCheck.makeRoom()'s own bound. |
| 6797 | `MapCheck.BOX_PLOTS_MOST` | `200_000` | The most plots a box is tried on, one by one: 200,000 - a box larger is drawn again smaller. |
| 6800 | `MapCheck.GLOBE_LAT` | `51.90625, GLOBE_LON = 4.48125` | Where the globe's check looks from: EarthCheck's Rotterdam window's centre, the played city's site put there. |
| 7166 | `MapCheck.PICKS_MONTHS` | `3` | The months section 14b holds the picks over, each with a place relisted: 3. |
| 7169 | `MapCheck.PICKS_HELD` | `3` | The offers section 14b picks and holds over the months: 3. |
| 7324 | `MapCheck.FAR_PLOTS` | `World.SIDE / 4.0` | How far from the city section 14c sets the plane: a quarter of the world's side, in plots. |
| 7327 | `MapCheck.FAR_DRAGS` | `50` | The drags section 14c carries the plane on, further out, of the pixels each. |
| 7373 | `MapCheck.PRESS_PLOTS` | `400` | The plots of an offer's rectangle section 14d tries for the owned-or-free rule: at most about this many, a stride over the rectangle. |
| 7494 | `MapCheck.MATERIALS_SIDE` | `10` | The materials fixture's square, in districts a side: 10 (77 km), wide enough to hold fields of every typed slot on the default world. |
| 7497 | `MapCheck.MATERIALS_SECTORS` | `{ Sectors.MINING, Sectors.HEAVY_INDUSTRY, Sectors.COAL_MINING, Sectors.QUARRY...` | The nine materials industries, Iron Mining to Wood Products (spec-materials 2.1): every building of theirs drawn with a look of its own. |
| 7834 | `MapCheck.MATERIALS_RINGS` | `24` | How far out materialsPlace() looks, in cells: 24. |
| 7875 | `MapCheck.AV_SAMPLES` | `4` | How many samples a step of an avenue's line is sampled at: four, a quarter step apart. |
| 7878 | `MapCheck.AV_STEPS` | `400` | The farthest an arm is followed from the origin, in steps: 400, past a district and a half. |
| 7886 | `MapCheck.AV_TIMES` | `9, AV_SIDE = 6, AV_SHIFT_X = 104, AV_SHIFT_Y = 56` | The city the avenues are drawn on: Jerus's city x 9 on a square of 6 districts a side (his density, as the copies', every district full) whose founding site is the dry place's moved AV_SHIFT_X plots east and AV_SHIFT_... |
| 7906 | `MapCheck.AvTiles.OWNED` | `1, BENEATH = 2, BRIDGE = 4` | Flag bits of a plot: it is owned, a street passes beneath a highway on it, it is a bridge's. |
| 8231 | `MapCheck.CV_FROM` | `3, CV_TO = 8` | The districts the curves are looked for in, in districts east and south of the founding site's: 3 to 8 - past CRESCENT_FROM (16 cells, two districts) wherever the cell lies. |
| 8234 | `MapCheck.CV_CRESCENTS` | `8, CV_SHORES = 16` | The most crescents and shore curves the audit follows: 8 and 16 (the shore curves that cross a tile's edge, and those that bend, first). |
| 8237 | `MapCheck.CV_TURN_SPAN` | `4` | How far apart, in plots, the turn audit's samples are: 4 - at a crescent's radius (22.6 plots) a turn of 0.18 radian between neighbours. |
| 8240 | `MapCheck.CV_CENTRE_PX` | `1.0` | How far the band's middle, measured across it from the pixels, may lie from the line: a pixel - the least a raster can place a band, and less than the 2.8 pixels at 4 px a plot a staircase's bars lie from the arc thro... |
| 8243 | `MapCheck.CV_NARROW_PX` | `1.5` | How much narrower than its street the band, measured across it, may measure: 1.5 pixels (a pixel's rounding at either end and a half). |
| 8246 | `MapCheck.CV_GENTLE` | `0.2` | How much the line may turn within a plot and a quarter either side of a sample for the band to be measured across it there: 0.2 radian - a crescent turns 0.06 over that; a shore street's corner (rounded to as little a... |
| 8249 | `MapCheck.CV_MEASURED` | `{ 0.5, 0.35 }` | The share of a kind's samples the band must have been measured across (crescents, shore curves): a half, and 35% - a shore's line turns a corner every few plots, and the band is not measured within a plot and a quarte... |
| 8849 | `MapCheck.HIS_TIMES` | `80` | The copy at Jerus's own district count: x 80, 125 districts, 11 a side (CL3's notes cite 115 for his cities), between x 1's 4 districts and 5B's 14,661. |
| 8852 | `MapCheck.REDRAW_MONTHS` | `24, REDRAW_MONTHS_5B = 12` | The months grown at x 1 and at x 80, and at 5B (its months are slower): 24 and 12. |
| 8855 | `MapCheck.REDRAW_WARM` | `3` | The first months of each fixture are drawn, compared and not counted in its figures: 3, the JIT's compiling done before a month is read. |
| 8858 | `MapCheck.GROW_PER_MILLE` | `1000, GROW_PER_TEN_THOUSAND = 10_000` | A month's growth as a share of each type's count: 1/1,000 (+0.1%) at x 1 and x 80, 1/10,000 (+0.01%) at 5B, as section 5's, at least one building each. |
| 8861 | `MapCheck.RUNS_EVERY` | `4` | The runs' types (a highway's, a track's) are grown only in every 4th month, so most months lay no run, as in play. |
| 8864 | `MapCheck.CHAIN_ROUNDS` | `4` | The times a month's screens are drawn: the month's, then again after each round of the chain's jobs (handed out, run, adopted) until none is left, at most 4. |
| 8867 | `MapCheck.PLAYED_BEFORE` | `3, PLAYED_AFTER = 3` | The months the played city is grown before its land purchase and after it: 3 and 3. |
| 8870 | `MapCheck.PURCHASE_SIDE` | `32` | The side of the block the played city buys, in plots: 32, a hole in its built-up ground's box (the played city's ground is irregular). |
| 8873 | `MapCheck.MAP_MONTH_MS` | `58` | The most a month's FX-side map work may take at 5B, in ms, its median month - the stamp, the far view's screen, an L0 screen re-stamped and the chain's handing out and adopting: 58, nine tenths of the 65 this section ... |
| 8876 | `MapCheck.GROW` | `0, ONE = 1, SETTLE = 2` | The kinds of month: the grown one, one built type raised by one, and the first, which only draws. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 360 | `static int fails` |  |
| 361 | `static PrintStream out` |  |
| 681 | `long[] buildings` | Buildings by type id: a mine or well on a site once, however many tiles its site spans - since 0.8.0 a works once for all the sites it takes; packed ones among them. |
| 683 | `long[] siteCount` | The sites under each type's works, by type id (0.8.0), each once; and the types whose sites are not a whole number of its works. |
| 684 | `long partWorks` |  |
| 686 | `final double[] surface` | The streets' surface in plots by kind [0, gravel, paved, highway] - a highway's own plots among the highway's - and what the plans have no street for (CityMap.leftoverByKind()). |
| 688 | `long rail, trackSurplus, packed, tiles, districts` | The railway's track laid, in plots (0.7.72), and (0.7.89) what the runs could not lay - since FR7 slice 3 the plots no train's way uses (CityRuns.trackSurplus()); buildings packed at the city's edge (R7); tiles painte... |
| 785 | `static GameFiles cityFiles` | The played city's save folder. |
| 1081 | `final CityMap map` |  |
| 1082 | `final long[] model` |  |
| 1083 | `final int house` |  |
| 1085 | `int month` | The month the next one is run in. |
| 1086 | `boolean reconciled` |  |
| 1347 | `final Months h` |  |
| 1348 | `final int[] mix` |  |
| 1349 | `final int step` |  |
| 1350 | `int next` |  |
| 1372 | `final DistrictPlan plan` |  |
| 1373 | `final java.util.Set<PlanBox> boxes` |  |
| 1374 | `final byte[] fixed, runs` |  |
| 1375 | `final boolean[] ramp` |  |
| 1377 | `final int month, journal` | The map's month when the snapshot was taken, and how many records the district's journal of the runs held then. |
| 1696 | `final BuildingVisual.Type[] types` |  |
| 1697 | `final long seed` |  |
| 1698 | `final long x, y` |  |
| 1699 | `final CityMap[] maps` |  |
| 1700 | `final long[][] counts` |  |
| 1701 | `final double[] buildMs` |  |
| 1703 | `final long ironX, ironY` | His city x 1 again where its square holds iron (0.7.99, batch W1; dryPlace(w, side)): the fixtures that need his mines - the runs' marks, the rail yards - stand on it; the dry place's own holds none since the deposits... |
| 1704 | `final CityMap ironX1` |  |
| 1705 | `final long[] ironCounts` |  |
| 1997 | `final CityMap map` |  |
| 1998 | `final long px0, py0` |  |
| 1999 | `final int w, h` |  |
| 2000 | `final byte[] use, road, width, role, run` |  |
| 2001 | `final boolean[] owned, sea, wet, bridge, beneath, mined` |  |
| 2003 | `final List<int[]> boxes` | Each building: {x0, y0, w, h, type, site (or -1), packed (1 or 0)} in the box's plots. |
| 2004 | `long rail, crossings, packed, siding` |  |
| 2005 | `final double[] surface` |  |
| 2006 | `final long[] buildings` |  |
| 2007 | `final java.util.Set<Long> sites` |  |
| 2942 | `final long x0, y0` |  |
| 2943 | `final int w, h` |  |
| 2944 | `final byte[] fixed` |  |
| 5509 | `final List<double[]> segs` |  |
| 5510 | `final List<double[]> lakes` |  |
| 5511 | `final long x0, y0` |  |
| 5512 | `final int stride` |  |
| 7899 | `final CityMap m` |  |
| 7900 | `final TilePainter.Input in` |  |
| 7901 | `final TilePainter.Painted p` |  |
| 7902 | `final java.util.Map<Long, int[]> img` |  |
| 7903 | `final java.util.Map<Long, byte[]> street` |  |
| 8269 | `final CityMap m` |  |
| 8270 | `final int px` |  |
| 8271 | `final TilePainter.Input in` |  |
| 8272 | `final TilePainter.Painted p` |  |
| 8273 | `final java.util.Map<Long, int[]> img` |  |
| 8274 | `final java.util.Map<Long, byte[]> street` |  |
| 8275 | `final java.util.Map<Long, short[]> types` |  |
| 8278 | `final boolean bare` | Whether the tiles are drawn without the curves (the control: as they were before CL4). |
| 8344 | `int samples, badColour, onBuilding, edges, audited, narrow, off, turns, turnsBad, tracks` |  |
| 8345 | `double worstCentre, worstExcess, worstIdeal` |  |
| 8347 | `String firstBad` | The first sample that was not the street's colour or lay off the line, for the line a failure prints: where, what. |
| 8880 | `final String name` |  |
| 8881 | `final MapFrame f` |  |
| 8882 | `final long plots, span` |  |
| 8883 | `final boolean pictures` |  |
| 8884 | `final int count` |  |
| 8885 | `final long[] nx, ny` |  |
| 8886 | `final byte[][] ground` |  |
| 8888 | `final long[] key` | Now's: the key each node was drawn from, its pixels, whether it was drawn, and short of a picture. |
| 8889 | `final int[][] pix` |  |
| 8890 | `final boolean[] drawn, partial` |  |
| 8892 | `final boolean[] todayPartial` | Today's: whether it would be drawn again after a picture came. |
| 8894 | `final boolean[] held` | The nodes held (not drawn again) in the month under way. |
| 8895 | `final int[] buf` |  |
| 8935 | `int kind, number, moved, rounds, astray, kept, built` |  |
| 8937 | `final java.util.List<CityMap.District> staleList` | The built districts whose pictures were no longer current right after a one-type month (`stale` of them). |
| 8938 | `boolean timed, reconciled, viewChanged, runsMoved, settled` |  |
| 8939 | `CityMap.District onlyMoved` |  |
| 8941 | `long lands, runsDrawn, repainted, onStamp` | The stamp's kept nodes in the month: land layers drawn, runs layers drawn, districts repainted (all 0 when the view changed and they were made anew), and the districts they list. |
| 8942 | `double stampNow, stampToday, farNow, farToday, l0, chain, keys, lazy` |  |
| 8944 | `int[] recoloured, partialBefore` | Per far screen: its nodes drawn this month (the month's round and the rounds after), and the partial ones before it. |
| 8946 | `final java.util.List<java.util.List<Integer>> keyMoved` | Per far screen: the nodes drawn in the month's own round because the key they were drawn from had moved. |
| 8954 | `final String name` |  |
| 8955 | `final CityMap m` |  |
| 8956 | `final long[] c` |  |
| 8957 | `final int divisor, mine` |  |
| 8958 | `final long sites` |  |
| 8959 | `final BuildingVisual.Type[] tys` |  |
| 8960 | `final MapTiles tk, tr` |  |
| 8961 | `final MapFrame l0` |  |
| 8962 | `final boolean withL0, checkLinks` |  |
| 8963 | `final java.util.List<FarScreen> fars` |  |
| 8964 | `final java.util.List<RedrawMonth> all` |  |
| 8965 | `final TilePainter.Input in` |  |
| 8966 | `MiniMapFrame.Box frame` |  |
| 8967 | `MiniMapFrame.View stampV, previewV` |  |
| 8968 | `byte[][] stampG, previewG` |  |
| 8969 | `MapTiles.KeptNode[] stampK, previewK` |  |
| 8970 | `int[] stampNow, stampToday, previewNow, previewToday` |  |
| 8971 | `int viewChanges` |  |
| 8973 | `int stampsDrawn, previewsDrawn, keyUnmoved, heldCompared` | What was compared: stamps and previews drawn two ways (their pixels counted), and far nodes whose key was unmoved against a fresh one (those held in the month among them); whether every one was equal. |
| 8974 | `long pixelsCompared` |  |
| 8975 | `boolean stampEqual` |  |
| 8976 | `double tookS` |  |
| 8978 | `Runnable beforeReconcile` | Run before the next month's reconcile (a land purchase). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 358 | 9489 | **type** `public class MapCheck` | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's street plan and paints the same pixels from the same inputs, the draw... |
| 363 | 4 | `static void check(String label, boolean ok)` |  |
| 407 | 7 | `static double groundKm2(BuildingVisual.Type[] types)` | The dry ground the copies' x 1 stands on (0.7.71): his, scaled by his buildings' land on this catalogue over theirs then, so the copies keep his density (JERUS_FILL) whatever the catalogue: with his childcare restated... |
| 445 | 41 | `public static void main(String[] args) throws Exception` |  |

### 1. A CITY'S MAP ADDS UP (lines 492-673)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 127 | `static Game playedCity(Path root) throws Exception` |  |
| 637 | 36 | `static boolean worldWaysOk(CityMap m, int[] seen)` | Whether a month's lines to the world and trains lie on the ground they should (FX2; GatewayCheck holds the whole list on its fixtures, this is the cheap half, the points and the routes and never the box): each world l... |

### WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) (lines 674-807)

| line | len | member | says |
|---:|---:|---|---|
| 679 | 32 | **type** `static final class Drawn` | What the painted map draws over every tile of a map's districts and a tile round them (0.7.88: a district's plan lays the next one's first column and row). |
| 690 | 1 | `long total()` _(in MapCheck.Drawn)_ |  |
| 691 | 1 | `int kinds()` _(in MapCheck.Drawn)_ |  |
| 692 | 1 | `double surfaced()` _(in MapCheck.Drawn)_ |  |
| 693 | 1 | `double left()` _(in MapCheck.Drawn)_ |  |
| 696 | 14 | `boolean sameAs(long[] model, BuildingVisual.Type[] types)` _(in MapCheck.Drawn)_ | Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn); each road kind's plots its streets' surface and what they do not carry, to within half a plot a district ... |
| 713 | 9 | `static long[] tilesOf(CityMap m)` | The tiles a map's districts cover and one round them: {tx0, ty0, across, down}. |
| 724 | 37 | `static Drawn drawn(CityMap m)` | Paints every tile of every district on a map, and a tile round them, and counts what is drawn. |
| 763 | 5 | `static long modelBuildings(long[] model, BuildingVisual.Type[] types)` | The model's buildings the map draws: every type but the roads. |
| 770 | 7 | `static long roadPlotsOf(long[] model, BuildingVisual.Type[] types, int kind)` | The model's road plots of a kind: each road type's count x its own plots (BuildingVisual.cells()). |
| 779 | 4 | `static long modelRoadPlots(long[] model, BuildingVisual.Type[] types)` | ...of every kind. |
| 788 | 8 | `static boolean roomAt(CityMap.District d, BuildingVisual.Type type, double share)` | Whether district d has room for one more of a type at a road share: CityMap.hasRoomFor()'s rule (buildingRoom()), at the share given rather than the map's own. |
| 798 | 4 | `static long mineSites(CityMap m, int mine)` | The sites a mine type may stand on (0.8.0, batch I14): its resource's of its type of field - the game's own rule since I5 (Game.committedOn(r, type), LandManager.fits()), an Iron Mine on hematite. |
| 803 | 4 | `static int mineId(Game g)` |  |

### 2. NOTHING PLACED MOVES (lines 808-886)

| line | len | member | says |
|---:|---:|---|---|
| 812 | 74 | `static void nothingMoves(Game g)` |  |

### 2b. A DISTRICT IS FULL AT WHAT ITS PLAN CAN DRAW, AND A SPILL KEEPS ITS TIME (batch CL3) (lines 887-1040)

| line | len | member | says |
|---:|---:|---|---|
| 904 | 7 | `static void districtsHoldAndSpill(Game g)` |  |
| 917 | 39 | `static void holdsWhatItCanDraw(BuildingVisual.Type[] types, int flats)` | A district holds what its plan can draw: on a square city of four districts, a few apartment blocks added month by month (reconcile) until the nearest district stops taking them. |
| 962 | 52 | `static void spillKeepsItsTime(BuildingVisual.Type[] types, int house, int store)` | A spill keeps its time, at the plan's level: the first district's input is given more than its window of ground can draw, the second's carry is the pairs the first leaves ({month, type}, the month each came in) - and ... |
| 1019 | 21 | `static DistrictPlan.Input spillInput(BuildingVisual.Type[] types, int cellsAcross, int[][] records, int firstBatch, int[] carry)` | A hand-made district for 2b's spill: flat ground, a square window of whole cells round the hub owned (the rest not), its types, its log (records {month, type, change}, the first `firstBatch` one batch; its counts the ... |

### 2c. A DISTRICT THAT BUYS LAND INSIDE ITSELF KEEPS ITS BUILDINGS WHERE THEY STAND (batch FX6) (lines 1041-1302)

| line | len | member | says |
|---:|---:|---|---|
| 1064 | 1 | **type** `record PlanBox(int type, int x, int y, int w, int h)` | One building of a plan: its type and its box in the frame's plots. |
| 1067 | 11 | **type** `record Bought(long x0, long y0, long x1, long y1, int dry)` | A rectangle of plots bought inside a district, [x0, x1) x [y0, y1) in the world, and how many of its plots are dry. |
| 1069 | 3 | `double nearest(long sx, long sy)` _(in MapCheck.Bought)_ | How far its nearest plot is from plot (sx, sy), in plots. |
| 1074 | 3 | `boolean holds(PlanBox b, long fx, long fy)` _(in MapCheck.Bought)_ | Whether the box lies wholly on this block, a box in the frame whose first plot is (fx, fy) in the world. |
| 1080 | 24 | **type** `static final class Months` | A harness city's months: houses added to the model a month at a time and the map reconciled to them, each in its month. |
| 1088 | 6 | `Months(CityMap map, long[] model, int house, int month)` _(in MapCheck.Months)_ |  |
| 1096 | 1 | `int last()` _(in MapCheck.Months)_ | The month that last ran: what a purchase made between months is dated. |
| 1099 | 4 | `void run(int houses)` _(in MapCheck.Months)_ | One month of `houses` more houses. |
| 1111 | 122 | `static void districtBuysLandInside(Game g)` | Whether a district that buys land inside itself keeps its buildings where they stand (batch FX6): on the played city's ground, a block is bought in the next district, and the district holding the founding site is give... |
| 1235 | 5 | `static java.util.Set<PlanBox> boxesOf(DistrictPlan p)` | The plan's buildings as boxes, a set: a building stands where it did when its box is in the plan again. |
| 1242 | 5 | `static int movedOf(java.util.Set<PlanBox> before, java.util.Set<PlanBox> after)` | How many of the boxes before are not among the boxes after: the buildings that moved. |
| 1249 | 5 | `static int housesElsewhere(CityMap m, CityMap.District d, int house)` | The houses the map holds in the districts but d. |
| 1260 | 22 | `static Bought nearestFreeBlock(CityLand land, DistrictPlan.Input frame)` | The block of whole land blocks (BOUGHT_PLOTS a side at the least, rounded up to the level's blocks) the city could buy inside a district's frame (not on its last row or column) that lies wholly unowned, is at least ha... |
| 1284 | 7 | `static int dryPlots(DistrictPlan.Input frame, long x0, long y0, long x1, long y1)` | The dry plots of [x0, x1) x [y0, y1) in the world, read from a plan input's ground (the world's, over the frame). |
| 1293 | 9 | `static boolean soundBlock(CityLand land, DistrictPlan.Input frame, Bought b)` | Whether a block about to be bought is what 2c says it is, checked on the land as it stands: whole blocks of its level, inside the frame, wholly unowned, at least half dry. |

### 2d. A RUN LAID ACROSS A DISTRICT'S BUILDINGS MOVES NONE OF THEM (batch FX7) (lines 1303-1689)

| line | len | member | says |
|---:|---:|---|---|
| 1346 | 23 | **type** `static final class Arrivals` | The arrivals of the founding district, month by month: `step` houses and BUILDINGS_A_MONTH buildings of the mix, in turn. |
| 1352 | 5 | `Arrivals(Months h, int[] mix, int step)` _(in MapCheck.Arrivals)_ |  |
| 1359 | 4 | `void month()` _(in MapCheck.Arrivals)_ | One month: the mix's next buildings and the houses. |
| 1365 | 3 | `void months(int n)` _(in MapCheck.Arrivals)_ | `n` months. |
| 1371 | 59 | **type** `static final class Snap` | The founding district's plan at one moment: its boxes, the plots its input fixes, the city's runs alone in its frame, and the ramps. |
| 1379 | 11 | `Snap(CityMap m, CityMap.District d)` _(in MapCheck.Snap)_ |  |
| 1392 | 5 | `int under(Snap later, int reach)` _(in MapCheck.Snap)_ | How many of this plan's boxes have a plot within `reach` plots of a plot the runs hold in `later` and did not hold here: the buildings a run laid in between lies across. |
| 1399 | 5 | `int arrivalsOnRuns(Snap later)` _(in MapCheck.Snap)_ | How many of `later`'s boxes that are not among this plan's lie on a plot fixed in `later`, in its input or among the runs: the arrivals that stand on a run. |
| 1406 | 5 | `int plots(byte kind)` _(in MapCheck.Snap)_ | The plots of kind `kind` in the runs. |
| 1413 | 5 | `int newPlots(Snap later, byte kind)` _(in MapCheck.Snap)_ | The plots of kind `kind` the runs hold in `later` and did not hold here. |
| 1423 | 6 | `int movedWithTheseRuns(CityMap m, CityMap.District d)` _(in MapCheck.Snap)_ | How many of this plan's boxes are not in the plan the map makes now when its input is given this snapshot's fixed ground and ramps: the buildings that move with the arrivals alone, the runs left as they were. |
| 1432 | 6 | `static boolean anyPlot(PlanBox b, int reach, java.util.function.IntPredicate test)` | Whether any plot within `reach` plots of the box (in the frame's plots, clipped to the frame) satisfies the test, a plot's index in the frame. |
| 1443 | 12 | `static int journalDiffers(DistrictPlan.Input later, Snap s)` | How many plots of `later`'s fixed ground, worked back through its frame's records (Input.fixedLog) dated after the snapshot's month, differ from the fixed ground the snapshot's input held (the interchanges' reserves, ... |
| 1462 | 8 | `static String laidOf(Snap before, Snap after)` | What the runs laid between two snapshots, by kind: "yard 56, rail 19". |
| 1472 | 4 | `static Snap quietMonth(Months h, CityMap.District d)` | The district's plan after one month of the model as it stands, nothing added: the month a run is laid in. |
| 1478 | 3 | `static int idOf(Game g, String name)` | The id of the building template of that name. |
| 1488 | 176 | `static void runsLaidAcross(Game g)` | Whether a run laid across a district's buildings moves none of them (batch FX7): on the played city's ground the district holding the founding site is given houses and BUILDINGS_A_MONTH buildings of the mix a month un... |
| 1666 | 5 | `static int cellsMin(BuildingVisual.Type[] types, int[] ids)` | The smallest and the largest footprint, in cells, of the types listed. |
| 1672 | 5 | `static int cellsMax(BuildingVisual.Type[] types, int[] ids)` |  |
| 1679 | 3 | `static String hubText(long[] hub, DistrictPlan.Input frame)` | A net's hub in the frame's plots, for a line: "40,72". |
| 1684 | 5 | `static String yardsText(List<long[]> yards, DistrictPlan.Input frame)` | The runs' yards in the frame's plots, for a line: "[x,y 8x7]". |

### THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY (lines 1690-1808)

| line | len | member | says |
|---:|---:|---|---|
| 1695 | 62 | **type** `static final class Squares` | His city x 1 and its copies, each on the design's square city at the dry place, its mines no more than its sites. |
| 1707 | 49 | `Squares()` _(in MapCheck.Squares)_ |  |
| 1759 | 3 | `static int x1Side(BuildingVisual.Type[] types)` | The side of Jerus's city x 1 on the design's square city, in districts: its ground over a district's (Squares). |
| 1764 | 3 | `static long[] dryPlace(World w)` | The world cell nearest the founding site's whose middle 3 x 3 districts are DRY_PLACE dry at a sample a tile: {x, y, rings out}. |
| 1776 | 18 | `static long[] dryPlace(World w, int side)` | ...and (0.7.99, batch W1), with `side` over 0, whose square for Jerus's city x 1 - `side` districts round its middle, as CityMap.square() lays it - holds an iron field's centre, so his mines have sites and his railway... |
| 1796 | 12 | `static boolean holdsIron(World w, long x, long y, int side)` | Whether the square of `side` districts CityMap.square() lays round plot (x, y) holds an iron field's centre. |

### 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on (lines 1809-1872)

| line | len | member | says |
|---:|---:|---|---|
| 1814 | 58 | `static void painterRules(CityMap m)` |  |

### 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) (lines 1873-1978)

| line | len | member | says |
|---:|---:|---|---|
| 1891 | 87 | `static void drawnAsItIs(CityMap dense)` | Jerus, 2026-10-07: "the generation should only put what the city has, not more not less". |

### 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its (lines 1979-2404)

| line | len | member | says |
|---:|---:|---|---|
| 1996 | 312 | **type** `static final class Raster` | A map painted whole over a box of tiles and put together: what each plot is, every building's box - what holds the network's rules. |
| 2009 | 57 | `Raster(CityMap map, long tx0, long ty0, int tw, int th)` _(in MapCheck.Raster)_ |  |
| 2068 | 4 | `static Raster of(CityMap map)` _(in MapCheck.Raster)_ | Every tile of every district of a map, and a tile round them. |
| 2074 | 1 | `boolean isRoad(int g)` _(in MapCheck.Raster)_ | A road plot: a street (a track among them, the track's level crossings among them), or a highway's. |
| 2077 | 1 | `boolean isStreet(int g)` _(in MapCheck.Raster)_ | A street plot: a road plot with a street's role - not a highway's own. |
| 2080 | 1 | `boolean isHighway(int g)` _(in MapCheck.Raster)_ | A highway's own plot (its runs'). |
| 2082 | 1 | `boolean isRail(int g)` _(in MapCheck.Raster)_ |  |
| 2085 | 1 | `boolean isRunHighway(int g)` _(in MapCheck.Raster)_ | A plot of the city's highways as its runs lay it (0.7.89), under a building or not - the railway bridging it among them. |
| 2088 | 1 | `boolean isRunRail(int g)` _(in MapCheck.Raster)_ | ...of its railway's track, under a yard or a mine too, or bridging a highway. |
| 2090 | 5 | `long streetPlots()` _(in MapCheck.Raster)_ |  |
| 2096 | 8 | `int beside(int g, java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 2106 | 19 | `int[] pieces(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ | The pieces plots of a kind make, four ways joined: each plot's piece number (0 for none), [0] = how many pieces. |
| 2136 | 37 | `int[] network()` _(in MapCheck.Raster)_ | The road's pieces and those the city's own ground joins to the largest: {pieces, plots of the largest, pieces the ground joins to it} - from the largest piece, four ways over owned ground that is not the sea or a mine... |
| 2174 | 5 | `long count(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 2181 | 5 | `long alone()` _(in MapCheck.Raster)_ | Road plots with no road beside them, four ways: a stray. |
| 2188 | 9 | `boolean crossing(int g)` _(in MapCheck.Raster)_ | A + junction of streets: a street plot with street on its four sides and none on its four corners (a square's middle, street on its corners too, is not one; nor a street beneath a highway, which passes under it). |
| 2199 | 16 | `long[] crossings(int apart)` _(in MapCheck.Raster)_ | {crossings, pairs of them nearer than `apart` either way}. |
| 2217 | 18 | `int[] reach(int most)` _(in MapCheck.Raster)_ | Each plot's distance from a street, across corners (a street 0), to `most` + 1. |
| 2237 | 5 | `static int boxReach(int[] dist, int w, int[] b)` _(in MapCheck.Raster)_ | A box's nearest plot's distance. |
| 2244 | 3 | `CityMap.District districtAt(int g)` _(in MapCheck.Raster)_ | The district a plot of the box is in. |
| 2254 | 24 | `int[] asPlanned()` _(in MapCheck.Raster)_ | Every plot the plans of the districts in the box surface (Drawn's codes, its frame's east column and south row among them) against the painted plot: {plots, those not painted as that street - its kind and its width}. |
| 2280 | 27 | `int[] bridges()` _(in MapCheck.Raster)_ | The bridges: {runs longer than their line may cross - an arterial's or a boulevard's ARTERIAL_BRIDGE, a street's STREET_BRIDGE, a highway's MAX_BRIDGE - roads on the sea, bridge plots}. |
| 2315 | 3 | `static void networkOn(CityMap m, long[] model, String what)` | The network's rules on a map's drawn raster (0.7.88): one network but where the ground parts it, no road alone, + junctions apart, every building within reach, the streets' surface the model's road kind by kind, every... |
| 2324 | 47 | `static void networkOn(CityMap m, long[] model, String what, boolean crosses)` | ...and whether the city must have + junctions for the + rule to bite (`crosses`): not the played city since CL3, whose few cells each opened while its road was short - a cell's squares are decided as it opens, never a... |
| 2372 | 32 | `static void network(Squares sq, Game city, String[] saves) throws Exception` |  |

### 8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4; (lines 2405-2572)

| line | len | member | says |
|---:|---:|---|---|
| 2415 | 5 | `static int depotType(BuildingVisual.Type[] types)` | The type id of the Truck Depot, the one building of the category HAULAGE. |
| 2425 | 3 | `static CityMap accessCity(Squares sq, boolean highways)` | The yard city (his city x 1 where its square holds iron, with two Rail Terminals and a freight line) with highways added to cross and Truck Depots to place; with `highways` false his own highways taken out too: none, ... |
| 2430 | 33 | `static CityMap accessCity(Squares sq, boolean highways, long[] extra)` | ...with `extra` counts added (batch FR7 slice 5: spurs and bulk works for the sidings' deal): a track type's to the railway, any other's to the buildings. |
| 2468 | 20 | `static int[] accessFigures(CityMap m)` | What a map's plans gave its works' access roads: {roads, on a ramp, on an arterial, already on one, gates not beside their box, ends neither a ramp's street nor an arterial, the map's works (yards and sea terminals) g... |
| 2489 | 83 | `static void interchangesAndAccess(Squares sq)` |  |

### 8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec- (lines 2573-3110)

| line | len | member | says |
|---:|---:|---|---|
| 2583 | 74 | `static void sidingsDealt(Squares sq)` | The spurs the runs' sidings left, as the map deals them: each dealt district is one the main line crosses with bulk works of its own, no more than it holds, and the plans lay at most their share, drawn on the raster; ... |
| 2659 | 23 | `static Game loadCopy(Path src) throws Exception` | A save's autosave copied into a temporary folder and loaded (its map drawn from its sidecar or canonically): saves named on the command line, never written. |
| 2691 | 30 | `static CityMap riverCity(Squares sq, int kind)` | The river fixture (0.7.77, batch N5): Jerus's city x 1 on the design's square city sited at the default world's founding river - its point nearest the founding site - with its streets all of one kind: his Paved Roads ... |
| 2731 | 44 | `static void riverCrossed(Squares sq)` | A river parts gravel streets no more than paved ones (0.7.77; Jerus, 2026-10-08: "gravel road bridge rivers sure"): the river fixture all gravel against its paved twin - the road pieces with a plot within a tile of th... |
| 2784 | 32 | `static void highways(Raster net, String what)` | The city's highways on the drawn raster (0.7.89, batch RD3; spec 2.7): drawn plot for plot as its runs lay them, and with what they could not lay the model's Elevated Highways; one network; no building plot touching a... |
| 2830 | 47 | `static int marksReachTheView(CityMap m, long tx0, long ty0, int tw, int th, String what, boolean bands)` | The runs' marks reach the picture a screen draws (0.7.98, batch O14): a screen rasters a tile from the view's kept copy of its inputs (MapTiles.painted(), paintedInput()), and until then that copy held none of the run... |
| 2892 | 47 | `static void corridors(Squares sq)` | The highways on corridors on the game's own ground (0.7.89; spec 2.7): each stage of spec 2.7's table on a square city of highways alone at the dry place, laid at once - its plots each laid or counted, one network, st... |
| 2941 | 55 | **type** `static final class Box` | The runs' plots over a map's districts and a plot round them, put together: one network, its junctions. |
| 2946 | 5 | `Box(long x0, long y0, int w, int h, CityRuns r)` _(in MapCheck.Box)_ |  |
| 2952 | 1 | `static Box of(CityMap m)` _(in MapCheck.Box)_ |  |
| 2954 | 4 | `static Box of(CityMap m, CityRuns r)` _(in MapCheck.Box)_ |  |
| 2959 | 1 | `boolean hw(int g)` _(in MapCheck.Box)_ |  |
| 2962 | 18 | `int pieces()` _(in MapCheck.Box)_ | The highways' pieces, four ways joined. |
| 2982 | 13 | `long junctions()` _(in MapCheck.Box)_ | Highway plots with highways on three or four sides: where they meet. |
| 2998 | 17 | `static int bendsStopped(CityMap m, CityRuns.Net n)` | The bends of a net that are not where its way was stopped: a stretch off its arm's heading begun where the way on along the heading before ran LOOK plots clear, or one back onto it where the way home did not run TURN_... |
| 3017 | 15 | `static void rail(Raster net, String what)` | The railway on the drawn raster (0.7.89; spec 2.8): one network, every plot of the model's track drawn - under its yards and mines too - or counted as what the runs could not lay (since FR7 slice 3 the surplus no trai... |
| 3037 | 25 | `static CityMap yardCity(Squares sq)` | The x 1 copy with rail yards and a freight line added (0.7.72's fixture, restored in 0.7.89): its buildings, then its mines on their sites, then its railway - so the railway starts at its mine nearest the founding sit... |
| 3070 | 40 | `static void yardsNearMines(CityMap m, Raster net)` | Every rail yard on its track, in the cell along it nearest a mine that had room (spec 2.8: "laid as runs from the rail terminals, which sit in estate cells nearest the mines"; 0.7.72's rule, restored): the track runs ... |

### 4. THE SIDECAR (lines 3111-3322)

| line | len | member | says |
|---:|---:|---|---|
| 3115 | 177 | `static void sidecar(Game g, Path root, Squares sq) throws Exception` |  |
| 3304 | 7 | `static boolean sameBoxes(DistrictPlan a, DistrictPlan b)` | Whether two plans place the same boxes: the same count of buildings, each one's type and its box {x, y, w, h}, in the same order, and the same buildings left without a place (by type). |
| 3313 | 9 | `static boolean logsAddUp(CityMap m)` | Whether every district's log sums, type by type, to its counts: what a log is for, and what a sidecar read back is held to (CL3). |

### 5. THE COST FOLLOWS THE SCREEN (lines 3323-3489)

| line | len | member | says |
|---:|---:|---|---|
| 3356 | 9 | `static boolean cpuTimed()` |  |
| 3367 | 10 | `static double cpuStepMs()` | The least step the main thread's CPU clock takes, in ms: five times, spinning until it moves (200 ms of the wall clock at most each); infinite if it never does. |
| 3379 | 3 | `static long monthClock()` | The clock a month's bound reads, in ns: the main thread's CPU, or the wall clock where CPU_TIMED is false. |
| 3383 | 106 | `static void cost(Squares sq)` |  |

### 7. THE VIEW'S PURE HALF (0.7.61, batch J4) (lines 3490-4179)

| line | len | member | says |
|---:|---:|---|---|
| 3500 | 8 | `static void viewHalf(Game city, Squares sq, Path root) throws Exception` |  |
| 3510 | 72 | `static void frames()` | MapFrame: the transforms, a notch of the wheel at the pointer, the clamps, the levels and what a screen asks for. |
| 3584 | 121 | `static void picks(Game g)` | LandMap: what a click and a hover pick on the played city agrees with the model's own grid (CityLand.holdingOf()) and GridOffers' sides and places, and the outlines with the picks (0.7.67; the lanes' owns() and sideLa... |
| 3718 | 228 | `static void overlay(Game g)` | The overlay on the block grid (0.7.69, batch M5; spec-grid 2.4), what MapView draws by: the city's edge as maximal runs with its ground on their right, drawn crisp - whole pixels, a pixel thick, just inside the ground... |
| 3951 | 3 | `static char dir(double x0, double y0, double x1, double y1)` | A run's way: E, S, W or N. |
| 3956 | 3 | `static boolean touch(double[] a, double[] b)` | Whether two boxes of pixels {x, y, w, h} share a pixel or touch at a corner or an edge. |
| 3961 | 13 | `static int inQuad(double[] q, double x, double y)` | Whether a point is inside a convex quadrilateral (corners in order): 1 inside, 0 within a hair of an edge, -1 outside. |
| 3976 | 77 | `static void draft(Path root) throws Exception` | Game.mapDraft()/adoptMap(): the map drawn on another thread is the map drawn here, a month between is caught up, and a draft for other land is not kept. |
| 4058 | 120 | `static void tiles(Squares sq)` | MapTiles: a tile's stamp holds while nothing moves, one more building restamps only the tiles it changes, the view's per-tile path follows the screen, and every cache fits the budget. |

### 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) (lines 4180-4730)

| line | len | member | says |
|---:|---:|---|---|
| 4188 | 12 | `static CityMap coastal(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts)` | A city owning `side` districts a side about (sx, sy), every plot of them, its map drawn canonically on the ground measured. |
| 4208 | 12 | `static long[] shoreCounts(BuildingVisual.Type[] types)` | The section's town: Jerus's mix at a tenth, one of each of the refinery's units, two of each terminal and one of each tank farm. |
| 4221 | 259 | `static void atSea(BuildingVisual.Type[] types, long seed)` |  |
| 4497 | 233 | `static void trains(Squares sq, Game city)` | Section 9's trains (0.8.2, batch FR1; spec-freight 4.4, D16): on the yard city (yardCity()), the routes run from the world line along the track into each yard and onto its sidings; the trains are its railed tonnes ove... |

### FX5, slice T: real-length trains, onto clear sidings, at a pace that reads (lines 4731-5286)

| line | len | member | says |
|---:|---:|---|---|
| 4734 | 3 | `static double holdsOf(TrainSchedule.Route rt)` | The plots of train a route's siding holds: Route.holds(). |
| 4739 | 3 | `static double ladderOf(TrainSchedule.Route rt)` | How far along a route its siding's ladder begins, in plots: Route.enters(). |
| 4744 | 3 | `static double legSecondsOf(double length)` | Seconds a leg of a route `length` plots long takes: its length over the pace, no less than the shortest leg and no more than the longest. |
| 4749 | 3 | `static double standSecondsOf(TrainSchedule.Kind k)` | Seconds a train of a kind stands on its siding: a base and so much a wagon. |
| 4757 | 6 | `static List<TrainSchedule.Route> handYard(double first, double second)` | A hand-made yard (FX5 T): three sidings off a ladder of 3 plots from a main line 64 plots long, the first holding `first` plots of train and the other two `second`. |
| 4765 | 3 | `static TrainSchedule.Route handRoute(int siding, double length)` | A hand-made route `length` plots long: the world line 64 plots, a ladder of 3, and the siding the rest, all on one line. |
| 4770 | 4 | `static double frontAt(TrainSchedule.Call c, TrainSchedule.Route rt, double t)` | How far along its route a call's train has its front at t, in plots; NaN while it is not on the line. |
| 4776 | 6 | `static boolean onSiding(TrainSchedule.Call c, TrainSchedule.Route rt, double t)` | Whether a call's train is on its route's siding at t: from its front reaching the ladder in to its rear leaving it out. |
| 4784 | 9 | `static int mostOnASiding(TrainSchedule month, List<TrainSchedule.Route> routes)` | The most trains on one siding at once, over a month sampled 2,000 times. |
| 4795 | 9 | `static boolean rearOnSiding(TrainSchedule.Call c, TrainSchedule.Route rt)` | Whether a standing train's rear vehicle lies on the route's last leg, its siding: the rear locomotive's middle between the leg's start and its end. |
| 4812 | 89 | `static void trainsByLength(List<TrainSchedule.Route> routes, TrainSchedule mix)` | Section 9's trains at their real length, on sidings that hold them, at a pace that reads (FX5, slice T; Jerus, 2026-10-20: "real trains are really long", "the rail depots would have to be redesigned to accomdoate such... |
| 4903 | 5 | `static int standingOn(CityMap m, int yi)` | The sidings of yard yi of the map that wagons stand on in the picture the map has: the tiles' count, scaled for the yard's sidings, never all of them. |
| 4914 | 36 | `static void trainsAndTheWagons(CityMap m, int kinds)` | Section 9's routes and the wagons standing in the yards (FX5, slice T; FX3's star 6: a train stood over the yard's own wagons): with none, half and all of the fleet's wagon sets standing, no route is laid to a siding ... |
| 4969 | 84 | `static void trainsOnTheCurves(CityMap m, List<TrainSchedule.Route> routes, TrainSchedule mix)` | Section 9's trains on the curves (FX5, 0.8.18; Jerus, 2026-10-20: "do trains make 90 degree turns?"): on the yard city's routes, at every sampled moment of the month's trains and at fronts all along every route, (a) n... |
| 5059 | 6 | `static boolean onLadder(TrainSchedule.Route rt, double fromEnd)` | Whether a point `fromEnd` plots before a route's end lies on its yard's ladder (FX5): the S between the fan's apex and the siding's start, the siding being the route's last leg - within a locomotive's length either side. |
| 5067 | 3 | `static double headingApart(double a, double b)` | How far apart two headings are, in degrees, 0 to 180. |
| 5072 | 25 | `static boolean onDrawnTrack(CityMap m, java.util.Map<Long, int[]> tiles, double x, double y, int px, int w)` | Whether the pixel under plot point (x, y), or one of its eight neighbours, is the railway's as the tile at 8 px a plot draws it (its tile rastered once into `tiles`). |
| 5105 | 17 | `static List<TrainSchedule.Route> syntheticYards(long cx, long cy, int side)` | A city of yards about (cx, cy) for the frame at 10 billion: side x side yards on a grid four cells apart, each with its YARD_TRACKS routes in from one gateway WORLD_PLOTS west - along the row to its column, down the c... |
| 5139 | 12 | `static List<BoatSchedule.Route> protoLanes(long cx, long cy)` | BoatProto's 20,000 lanes about (cx, cy): berths at hashed places along its coast's span at 10B - sqrt(10B / 5,000) x 33 plots, its "city's span in plots (rough)" - every other one liquid bulk's, the rest dry bulk's. |
| 5153 | 5 | `static int tileGround(World w, long x, long y)` | The world's ground at a plot as the painter reads it: its tile's (World.tileTerrain()), what the shore's search and the routes read. |
| 5160 | 19 | `static boolean connected(java.util.Set<Integer> cells)` | Whether a set of cells (ci + cj x CELLS_A_SIDE) is one piece, eight ways: a corner touching counts, as DistrictPlan.campusNext() opens them. |
| 5181 | 5 | `static String worksWords(CityShore shore, BuildingVisual.Type[] types)` | The shore's works by type, in words. |
| 5188 | 17 | `static Ports tradeAtSea(double liquid, double dryBulk, double general, double boxes)` | A month billed with these tonnes of each kind shipped out, all of them by sea: each on the first good of its kind that is not crude. |
| 5213 | 73 | `static void oilAtSea()` | The game's own platform, its wells and its pipe on the map (WellCheck's sea town): Game.mapAtSea() puts the jacket in the middle of its slotted sites and its wells on them, the pipe from the jacket to the founding sit... |

### 10. THE MAP ON EARTH (0.7.119, batch E5) (lines 5287-5702)

| line | len | member | says |
|---:|---:|---|---|
| 5313 | 107 | `static void onEarth(BuildingVisual.Type[] types) throws Exception` | Section 10. |
| 5428 | 73 | `static void farWindow(String name, World w, EarthData data)` | One window drawn as a far node at WINDOW_STRIDE and at CLOSE_STRIDES: closer, the region but for its rivers stroked wider; at the window's stride every river stroked at least FAR_RIVER_HALF_PX a side, every small lake... |
| 5508 | 60 | **type** `static final class Water` | The rivers and small lakes under a far node, as the check reads them from the data itself: each run the node would stroke (its median width at least FAR_RIVER_SHARE of a pixel), its segments projected exactly, and eac... |
| 5514 | 29 | `Water(EarthTerrain e, EarthData data, long x0, long y0, int stride)` _(in MapCheck.Water)_ |  |
| 5545 | 12 | `boolean inStroke(int i, int j, boolean least)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies within a river's stroke: its least half-width alone (`least`), or its own. |
| 5559 | 8 | `boolean inCore(int i, int j)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies in a small lake's sure part: within its radius x the root of 1 - BLOB_FRAY / 2, which its blob holds whatever its noise. |
| 5576 | 36 | `static void seam(String where, World w, int stride, EarthData data, boolean water)` | A node holding the 180th meridian (its middle on the spot, which is on it) and its neighbours moved SEAM_SHIFT_PX east and west: on the half each shares with it, pixel for pixel the same; each side of the line's land ... |
| 5614 | 88 | `static void fullFile(int widest, EarthTerrainCheck.Spot rot) throws Exception` | Section 10 on the full file, when it is named (-Dcbs.earth): the longest segment, every stride's cost, the whole world, the small lakes' area and Chukotka's seam. |

### 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) (lines 5703-6158)

| line | len | member | says |
|---:|---:|---|---|
| 5716 | 357 | `static void pictures(Game city, Squares sq)` |  |
| 6075 | 5 | `static Map<CityMap.District, DistrictPicture> picturesOf(CityMap m)` | A district's pictures, every one asked for: each district of the map to its picture (null where it has none). |
| 6082 | 5 | `static long[] countsOf(CityMap.District d, int types)` | A district's counts by type id. |
| 6089 | 5 | `static String codeName(int c)` | A picture's code's name, for the prints. |
| 6103 | 26 | `static byte[] pictureSource(CityMap m, CityMap.District d, int[] boxPlots)` | The plots a district's picture is drawn from, read here from its plan as the painter reads it (CityMap.Drawn: its streets, its boxes by tile) and its own plan's inputs (the runs over its frame, the mines on their site... |
| 6130 | 5 | `private static void fill(byte[] src, int x0, int y0, int w, int h, byte code, int[] counted)` |  |
| 6137 | 15 | `static boolean[] runPixelsOf(CityMap m, long x0, long y0, long plots)` | Which pixels of a far node at (x0, y0), `plots` a pixel, hold a plot of the city's highways or track: every plot of every stretch walked, each 45-degree step's corner with it (CityRuns.onStretch()). |
| 6153 | 5 | `private static void markPixel(boolean[] out, long x, long y, long x0, long y0, long plots)` |  |

### 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) (lines 6159-6403)

| line | len | member | says |
|---:|---:|---|---|
| 6169 | 196 | `static void miniMap(Game city, Squares sq) throws Exception` |  |
| 6371 | 32 | `static MiniMapFrame.Box builtUpByLeaves(CityMap m)` | The built-up ground found another way, for the check: every leaf of the grid cut to each district with buildings it lies in, their bounds; a built district with none owned its whole square. |

### 13. THE GLOBE ON SCREEN (0.7.135, batch E9) (lines 6404-6782)

| line | len | member | says |
|---:|---:|---|---|
| 6417 | 325 | `static void globeOnScreen(Squares sq) throws Exception` |  |
| 6744 | 4 | `static byte groundClass(String words)` | The class a legend's ground words name. |
| 6750 | 15 | `static double foundingParting(double lat, double lon, double km)` | How far the founding globe's two drawings part at a scale, in pixels at worst over its canvas's corners and edges: the globe through GlobeFrame against the ground drawn round the view's centre (Lambert's plane there, ... |
| 6767 | 15 | `static CityMap earthX1(Squares sq) throws Exception` | Jerus's city x 1 on the real Earth at Rotterdam, EarthCheck's fixture (section 10's window): null when the fixture is not beside the classes. |

### 14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 6783-7487)

| line | len | member | says |
|---:|---:|---|---|
| 6802 | 362 | `static void buyingFromTheMap(Game g) throws Exception` |  |
| 7172 | 10 | `static boolean buysQuietly(Game g, LandParcel p)` | The city buys land as a player would: cash made up to the price when short, then Game.buyLandParcel (LandManager.buyParcel). |
| 7187 | 10 | `static List<LandParcel> listedAgain(CityLand land, LandMarket market, LandParcel was, List<Integer> notIds)` | The listed offers, not those ids, whose rectangle shares ground the city does not own with a rectangle that was listed: the place listed again over that ground under a new id (its rectangle is sized by the city as it ... |
| 7203 | 119 | `static void picksLast(Game g) throws Exception` | MS1 (Jerus, 2026-10-26: "on monthly update it doesnt reset ... |
| 7334 | 37 | `static void farOnThePlane(Game g)` | MS1 (Jerus, 2026-10-26: "you cant zoom in on areas of the map far away from the city, and if zoomed in there is a wall that you can cross"): the wall and the stop were the globe's (GlobeCheck 2b); a generated world's ... |
| 7381 | 66 | `static void plainDragSelects(Game g)` | MS1 (Jerus, 2026-10-26: "i should be able to not only click and shift click, but also drag and select"): what a left press on the expanded map starts, a box or a pan, is the model's (LandMap.pressBoxes(), which MapVie... |
| 7449 | 3 | `static String owner(int owner)` | A pick's owner in words. |
| 7454 | 4 | `static boolean clears(LandMap.Picks picks, Game g, int id)` | Whether a plain click on an offer (only) clears a note that stood. |
| 7460 | 10 | `static java.util.Set<Integer> boxByPlots(CityLand land, List<LandParcel> offers, double x0, double y0, double x1, double y1)` | The offers a box of plots meets, plot by plot: every plot under (x0, y0)-(x1, y1) in some part, not the city's, inside an offer's rectangle. |
| 7472 | 10 | `static boolean inOrder(List<Integer> ids, List<LandParcel> listing)` | Whether these ids stand in the listing's order. |
| 7484 | 3 | `static boolean sameFigure(double a, double b)` | Two money or area figures the same to a billionth of the larger. |

### 15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; (lines 7488-7854)

| line | len | member | says |
|---:|---:|---|---|
| 7501 | 3 | `static boolean materialsOf(BuildingsTemplate t)` | Whether a template is one of the nine industries'. |
| 7506 | 3 | `static long materialsCount(BuildingVisual.Type t)` | The fixture's count of a materials type: one of a works on more than one site, two of anything else - so each slot holds two kinds and the larger is laid first. |
| 7510 | 264 | `static void materials(long seed)` |  |
| 7776 | 3 | `static long countZone(List<CityMap.Tinted> tint, int z)` | How many districts a zone's tint takes. |
| 7781 | 7 | `static java.util.Map<Long, Integer> sitesUnder(CityMap m)` | Each site under a works on a map, by its key, and the works' type. |
| 7790 | 12 | `static int lookSign(BuildingVisual.Type t)` | The colour a look shows on a box of it rastered (TileRaster's THE MATERIALS AS THEMSELVES): a pit's benches, a shaft's headframe, a quarry's stone, the leach plant's pond, the wash plant's conveyor, a camp's logs, pla... |
| 7810 | 22 | `static long[] materialsPlace(World w, int side, int[] need)` | The world cell nearest the founding site's whose square of `side` districts (as CityMap.square() lays it round the cell's middle) is DRY_PLACE dry in its middle 3 x 3 districts and holds fields centred in it with at l... |
| 7837 | 17 | `static int[] slotSites(World w, long x, long y, int side)` | The sites of each slot of the fields centred in the square of `side` districts CityMap.square() lays round plot (x, y). |

### 16. THE AVENUES DRAWN (batch CL4; the study spec-city-layout 5.2) (lines 7855-8211)

| line | len | member | says |
|---:|---:|---|---|
| 7889 | 7 | `static CityMap avenueCity(Squares sq)` | The avenues' city, built from the copies' constants (Squares): twice alike gives a twin. |
| 7898 | 52 | **type** `static final class AvTiles` | One map's tiles, kept as the section samples them: the rastered image at PX and what the painter was given and made of each plot. |
| 7908 | 1 | `AvTiles(CityMap m)` _(in MapCheck.AvTiles)_ |  |
| 7910 | 1 | `private static long key(long tx, long ty)` _(in MapCheck.AvTiles)_ |  |
| 7913 | 16 | `private void load(long tx, long ty)` _(in MapCheck.AvTiles)_ | The tile (tx, ty) as the painter makes it, loaded on first use. |
| 7930 | 5 | `private int at(java.util.Map<Long, byte[]> what, long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 7937 | 1 | `int code(long x, long y)` _(in MapCheck.AvTiles)_ | The plan's street code, the avenue's mark, the painter's use, the ground and the flags of world plot (x, y). |
| 7938 | 1 | `int mark(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 7939 | 1 | `int use(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 7940 | 1 | `int ground(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 7941 | 1 | `int flag(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 7944 | 5 | `int pixel(long X, long Y)` _(in MapCheck.AvTiles)_ | The pixel of the near view's raster at world pixel (X, Y). |
| 7952 | 4 | `static long[] avenueOrigin(CityMap m)` | The lattice node the avenues run out from, nearest the founding site (DistrictPlan's avSetup), in world plots: {x, y}. |
| 7958 | 14 | `static boolean avenueOpen(AvTiles t, long ox, long oy, int a, int k)` | Whether the avenue is open at step k of arm a, as the near view draws it: the staircase's plots S_k, C_(k+1) and S_(k+1) laid as a surfaced street on the ground, and the outer plot O_(k+1) between them owned dry groun... |
| 7974 | 3 | `static boolean streetColour(int c)` | Whether a pixel is a surfaced street's: the paved colour, the gravel's, or its grain. |
| 7979 | 12 | `static int handMark(int ox, int oy, int arms, int len, int x, int y)` | Where a hand-built tile's avenues stand: the avenue plots of the arms `arms` (bit a) from origin (ox, oy) for `len` steps - a plot two arms share the first arm's mark, as DistrictPlan lays it - and 0 for none. |
| 7993 | 8 | `static int[] handAvenue(int ox, int oy, int arms, int len, JunctionCheck.Streets extra, boolean withOrigin)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: the avenue plots of the arms `arms` from the origin laid as arterial streets with their marks, and the streets `extra` besides; the origin given ... |
| 8002 | 209 | `static void avenuesDrawn(Squares sq)` |  |

### 17. THE CURVES DRAWN (batch CL4, slice D; the study spec-city-layout 5.2) (lines 8212-8814)

| line | len | member | says |
|---:|---:|---|---|
| 8252 | 14 | **type** `record CvFound(CityMap.District d, CityMap.DrawnCurve c, long ox, long oy)` | A curve found in a Drawn, with where its district's frame lies in the world. |
| 8254 | 5 | `double[][] line()` _(in MapCheck.CvFound)_ | Its line in the world's plots: {xs, ys}. |
| 8261 | 4 | `long[] plot(int k)` _(in MapCheck.CvFound)_ | Plot k of its list in the world's plots: {x, y}. |
| 8268 | 65 | **type** `static final class CvTiles` | One map's tiles as the curves section samples them at `px` a plot: the rastered image, and what the painter was given and made of each plot. |
| 8280 | 1 | `CvTiles(CityMap m, int px)` _(in MapCheck.CvTiles)_ |  |
| 8282 | 1 | `CvTiles(CityMap m, int px, boolean bare)` _(in MapCheck.CvTiles)_ |  |
| 8284 | 1 | `private static long key(long tx, long ty)` _(in MapCheck.CvTiles)_ |  |
| 8286 | 20 | `private void load(long tx, long ty)` _(in MapCheck.CvTiles)_ |  |
| 8307 | 5 | `private int at(java.util.Map<Long, byte[]> what, long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 8314 | 1 | `int code(long x, long y)` _(in MapCheck.CvTiles)_ | The plan's street code, the painter's use, the curve mark and the flags (1 owned, 2 bridge, 4 beneath) of world plot (x, y). |
| 8315 | 1 | `int use(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 8316 | 1 | `int mark(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 8317 | 1 | `int flag(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 8320 | 5 | `int type(long x, long y)` _(in MapCheck.CvTiles)_ | The type id of the building on world plot (x, y), or -1 for none. |
| 8327 | 5 | `int pixel(long X, long Y)` _(in MapCheck.CvTiles)_ | The pixel of the raster at world pixel (X, Y). |
| 8335 | 6 | `static boolean cvStreetColour(int c)` | Whether a pixel is a surfaced street's, owned or dimmed as ground the city has not bought is: the paved colour, the gravel's or its grain. |
| 8343 | 6 | **type** `static final class CvAudit` | What an audit of one curve's drawn line found. |
| 8351 | 5 | `static java.util.Set<Long> cvPlotSet(CvFound f)` | The own plots of a curve (the first `own` of its list) as a set of world plot keys. |
| 8366 | 89 | `static CvAudit cvAudit(CvTiles t, CvFound f, java.util.Set<Integer> fills)` | One curve's line audited on the raster t (THE CURVES DRAWN): samples every pixel along it, from its first point to its last. |
| 8457 | 4 | `static double cvTurn(double x0, double y0, double x1, double y1, double x2, double y2)` | The signed turn, in radians, from the chord (x0, y0)-(x1, y1) to (x1, y1)-(x2, y2). |
| 8463 | 7 | `static boolean cvCrossesTile(double[][] line)` | Whether the line passes from one tile to another. |
| 8472 | 8 | `static java.util.List<long[]> cvTiles(CvFound f)` | The tiles about a curve's plots: its bounding box in tiles, a tile more on each side, as {tx, ty} pairs. |
| 8482 | 19 | `static int[] handCrescent(JunctionCheck.Streets extra)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: a crescent in the cell whose interior's north-west plot is (0, 1), bulging toward +x, its staircase laid as street plots with its curve, and the ... |
| 8503 | 19 | `static int[] handShore(boolean bridge)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: a street along a cut, a path of street plots along row 10 from column 2 to 20 with its curve (StreetCurves' line, its band beside it) - with `bri... |
| 8523 | 291 | `static void curvesDrawn(Squares sq)` |  |

### 18. A MONTH REDRAWS ONLY WHAT MOVED (batch UP3, docs/queue/UP3.md) (lines 8815-9547)

| line | len | member | says |
|---:|---:|---|---|
| 8879 | 53 | **type** `static final class FarScreen` | One far screen of section 18: a MapFrame fitted on the city's frame and zoomed out to FAR (or on to a level that draws no pictures), its nodes, their ground, and what each was last drawn from and as - the way ui/MapVi... |
| 8897 | 29 | `FarScreen(String name, CityMap m, MiniMapFrame.Box frame, boolean wide)` _(in MapCheck.FarScreen)_ |  |
| 8928 | 3 | `boolean overlaps(int i, long x0, long y0, long x1, long y1)` _(in MapCheck.FarScreen)_ | Whether node i lies over any of the box x0 to x1 and y0 to y1 (in plots). |
| 8934 | 17 | **type** `static final class RedrawMonth` | What one month did to one fixture's screens, as Redraw.month() found it; times in ms. |
| 8948 | 1 | `double nowSum()` _(in MapCheck.RedrawMonth)_ |  |
| 8949 | 1 | `double todaySum()` _(in MapCheck.RedrawMonth)_ |  |
| 8953 | 321 | **type** `static final class Redraw` | A fixture of section 18: a map grown a month at a time, its screens drawn two ways (today's whole on `tr`, now's kept on `tk`) and held equal. |
| 8980 | 21 | `Redraw(String name, CityMap m, long[] counts, int divisor, boolean withFar, boolean wide, boolean withL0, boolean checkLinks)` _(in MapCheck.Redraw)_ |  |
| 9002 | 3 | `boolean runType(int t)` _(in MapCheck.Redraw)_ |  |
| 9007 | 5 | `int biggest()` _(in MapCheck.Redraw)_ | The built type with the most buildings that is no run's and no mine: the one a single-district month raises. |
| 9013 | 5 | `static long sum(MapTiles.KeptNode[] keeps, int what)` _(in MapCheck.Redraw)_ |  |
| 9020 | 115 | `RedrawMonth month(int kind, int number, boolean timed)` _(in MapCheck.Redraw)_ | Plays one month and draws it; `timed` months are the ones whose figures are read. |
| 9142 | 61 | `long[] drawStamp(RedrawMonth r, int round, java.util.Set<CityMap.District> wanted)` _(in MapCheck.Redraw)_ | The stamp and the preview drawn two ways, equal or not noted: returns the ns now's took and today's. |
| 9205 | 11 | `long todayPass(FarScreen fs, int round, boolean warm)` _(in MapCheck.Redraw)_ | Today's draw of a far screen: every node in the month's round, the partial ones after; ns. |
| 9224 | 33 | `long[] drawFar(FarScreen fs, int s, RedrawMonth r, int round, java.util.Set<CityMap.District> wanted)` _(in MapCheck.Redraw)_ | A far screen drawn two ways, the ns of each: now's as ui/MapView.paintNodes() draws it (nodeKey() for every node in the month's round and for the partial ones after; drawn when the key moved, or it was drawn partial a... |
| 9259 | 14 | `void verify(FarScreen fs)` _(in MapCheck.Redraw)_ | Every node of the screen drawn from the key it has now holds the pixels a fresh nodePixels() gives it: those held through the month are the ones that count. |
| 9275 | 5 | `static int differ(int[] a, int[] b)` |  |
| 9281 | 6 | `static double median18(java.util.List<Double> l)` |  |
| 9288 | 5 | `static double worst18(java.util.List<Double> l)` |  |
| 9294 | 6 | `static int medianInt(java.util.List<Integer> l)` |  |
| 9302 | 16 | `static CityMap hisCopy(Squares sq, double times)` | Jerus's city at `times` on the design's square city at the dry place, as Squares builds its copies. |
| 9324 | 16 | `static long[] purchaseBlock(CityMap m, CityLand land)` | The block of PURCHASE_SIDE plots to buy: wholly inside the box of the city's built-up ground (so the stamp's frame is the one it was, however the district's owned ground grows), none of it owned, and the most ground o... |
| 9341 | 130 | `static void redrawsOnlyWhatMoved(Game city, Squares sq)` |  |
| 9473 | 37 | `static void oneDistrict(Redraw w, RedrawMonth one, int s, boolean picturesOn)` | Check 3 on one month that raised one built type by one: the far nodes whose key moved against the 3 x 3 districts round the one district that moved, and against every node in view. |
| 9512 | 35 | `static void report18(Redraw w)` | The figures section 18 prints for a fixture. |

### THE RELIEF ON THE MAP (ET1 slice D; docs/studies/spec-earth2-terrain.md (lines 9548-9846)

| line | len | member | says |
|---:|---:|---|---|
| 9560 | 4 | `static int reliefColour(int c, int r)` | The colour a plot of cover `c` and relief `r` is painted, said again from TileRaster's constants (D4, D5, D19): flat ground, water and ice (which have no relief, and white wins over a slope) in their cover's colour; o... |
| 9566 | 4 | `static World.Key fixtureKey(double[] at) throws Exception` | A map's relief on EarthCheck's fixture's key at (lat, lon): the data in use (EarthTerrain.use()) and the key of the Earth world there. |
| 9572 | 144 | `static void reliefOnMap(BuildingVisual.Type[] types) throws Exception` | Section 10: the plane's plots and the far node over Medellin painted by their relief; a generated world painted as it was. |
| 9718 | 34 | `static void pictureRelief(BuildingVisual.Type[] types) throws Exception` | Section 11: a built district's far node over Medellin's hills: the greys on the city's own ground, undimmed, and nowhere else but the plan's own colours. |
| 9754 | 92 | `static void reliefWordsAndLegend(BuildingVisual.Type[] types) throws Exception` | Section 13: the words and the legend by relief; the globe's hover and its picture's codes. |

