# MapCheck.java - 13,637 lines · 359 methods · 128 constants · harnesses

`ham/citybuildersim/MapCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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
>      FX7, MF1) a run laid across a district's buildings - a Rail Depot's yard
>      and main line at the hub, the yard gone and laid again, Elevated Highways
>      drawn across them - moves off it the boxes under it, or beside a highway
>      (its verge), and only those: every other box of the plan before stands in
>      the plan after, none stands on a run or a highway's verge after the
>      month, and the arrivals after a run stand on none of its plots;
>      and (2e, MF1) a yard whose building left stays laid, idle,
>      for CityRuns.YARD_IDLE_MONTHS - the depot ordered again takes the same
>      yard back, nothing taken up and laid anew, the net held, no record in
>      the journal - and is taken up when its time is up; and
>      (2f, MF1) what a city out of ground has no room for is
>      packed beside a street of the edge districts - within REACH of one on the
>      painted raster, on a tile that has one, together and not one to a tile,
>      a street laid only where none is near (LAID_MOST plots at the most, a T
> ... (324 more lines in the source)

**Uses:** [CityMap](CityMap.md) (640), [World](World.md) (561), [LandMap](LandMap.md) (400), [TilePainter](TilePainter.md) (256), [TrainSchedule](TrainSchedule.md) (209), [BuildingVisual](BuildingVisual.md) (202), [MapFrame](MapFrame.md) (202), [DistrictPlan](DistrictPlan.md) (200), [TileRaster](TileRaster.md) (168), [MapTiles](MapTiles.md) (147), [GlobeFrame](GlobeFrame.md) (117), [LandParcel](LandParcel.md) (116), [CityRuns](CityRuns.md) (96), [MiniMapFrame](MiniMapFrame.md) (93), [CityShore](CityShore.md) (85), [SeaRoutes](SeaRoutes.md) (83), [Game](Game.md) (79), [CityLand](CityLand.md) (77), [JunctionCheck](JunctionCheck.md) (64), [BoatSchedule](BoatSchedule.md) (58), [DistrictPicture](DistrictPicture.md) (58), [LongPlaytest](LongPlaytest.md) (55), [EarthTerrainCheck](EarthTerrainCheck.md) (46), [Ports](Ports.md) (40), [Timing](Timing.md) (32), [EarthTerrain](EarthTerrain.md) (31), [LandGrid](LandGrid.md) (30), [GlobePicture](GlobePicture.md) (27), [EarthData](EarthData.md) (24), [Resource](Resource.md) (23)... and 35 more

**Used by (10):** [BridgeMapCheck](BridgeMapCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [GlobeCheck](GlobeCheck.md), [IslandNetCheck](IslandNetCheck.md), [JunctionCheck](JunctionCheck.md), [MainLineCheck](MainLineCheck.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 546 | 1. A CITY'S MAP ADDS UP |
| 728 | · WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) |
| 862 | 2. NOTHING PLACED MOVES |
| 941 | 2b. A DISTRICT IS FULL AT WHAT ITS PLAN CAN DRAW, AND A SPILL KEEPS ITS TIME (batch CL3) |
| 1095 | 2c. A DISTRICT THAT BUYS LAND INSIDE ITSELF KEEPS ITS BUILDINGS WHERE THEY STAND (batch FX6) |
| 1358 | 2f. THE OUTER RIM: WHAT A CITY HAS NO ROOM FOR STANDS BESIDE A STREET (batch MF1) |
| 1596 | 2g. A DISTRICT BOUGHT INTO THE MAP'S ORDER AHEAD OF OTHERS KEEPS WHAT IS CARRIED IN PLACE (batch MF1; CL3's re-chaining) |
| 1724 | 2d. A RUN LAID ACROSS A DISTRICT'S BUILDINGS MOVES NONE OF THEM (batch FX7) |
| 2214 | 2h. AN INTERCHANGE'S RESERVE DATED (batch UP5, FX7's star 2) |
| 2437 | 2e. A YARD WHOSE BUILDING LEFT STAYS LAID, IDLE (batch MF1, item 6; FX7's star 4) |
| 2698 | THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY |
| 2817 | 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on |
| 2881 | 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) |
| 2987 | 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its |
| 3413 | 8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4; |
| 3581 | 8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec- |
| 4124 | 4. THE SIDECAR |
| 4336 | 5. THE COST FOLLOWS THE SCREEN |
| 4503 | 7. THE VIEW'S PURE HALF (0.7.61, batch J4) |
| 5193 | 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) |
| 5273 | · the coast: one or two harbours, not a shore of piers (MF1, 2026-10-28) |
| 5603 | · the shore's cost follows what a month changes (SL1, 2026-10-28) |
| 6108 | · SL1 (2026-10-28): the terminals share a lane, found once, drawn once |
| 6667 | · FX5, slice T: real-length trains, onto clear sidings, at a pace that reads |
| 7292 | 10. THE MAP ON EARTH (0.7.119, batch E5) |
| 7708 | 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) |
| 8195 | 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) |
| 8440 | 13. THE GLOBE ON SCREEN (0.7.135, batch E9) |
| 8955 | 14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) |
| 9640 | 14e. BLOCKS PICKED (BL1) |
| 10286 | 14f. THE EXACT GROUND ON THE SCREEN (BL1, slice D) |
| 10600 | 15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; |
| 10967 | 16. THE AVENUES DRAWN (batch CL4; the study spec-city-layout 5.2) |
| 11361 | 17. THE CURVES DRAWN (batch CL4, slice D; the study spec-city-layout 5.2) |
| 11964 | 18. A MONTH REDRAWS ONLY WHAT MOVED (batch UP3, docs/queue/UP3.md) |
| 13251 | THE RELIEF ON THE MAP (ET1 slice D; docs/studies/spec-earth2-terrain.md |
| 13550 | 19. THE BORDERS ON THE NEAR VIEW (batch WN2, slice A; docs/studies/spec-nations.md 6.1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 414 | `MapCheck.MONTHS` | `120` | Months the city of section 1 is played: 120 (the design's). |
| 417 | `MapCheck.IRON_AT` | `MONTHS / 3` | The month the played city is handed a whole iron field and orders a mine on it, if it has none (0.7.64): a third of the way. |
| 429 | `MapCheck.JERUS_COUNTS` | `{ 1913, 200, 1212, 0, 65, 4, 1701, 4, 0, 2, 5, 48, 11, 81, 21, 0, 0, 239, 114...` | Jerus's city at month 1,851 (his autosave, 509,455 people on 89.63 km2 of dry ground): every building type's count, by id - 14,214 buildings, the design's own fixture for the map's sizes; 8,204 since 0.7.71, its child... |
| 434 | `MapCheck.JERUS_PEOPLE` | `509_455` | ...his people. |
| 437 | `MapCheck.JERUS_KM2` | `89.63` | ...and his dry ground, in km2. |
| 440 | `MapCheck.JERUS_FILL` | `0.92` | His buildings' footprint over his dry ground: 91.8% (the design's measure), so the design's square city holds this share of a district - his density. |
| 443 | `MapCheck.JERUS_FOOTPRINT_KM2` | `82.273` | ...the land those buildings stood on, by the catalogue his save was measured with (0.7.70's): 82.273 km2, 91.8% of his dry ground (runs/fixN2-notes.md). |
| 461 | `MapCheck.TIMES` | `{ 1, 9_814, 10_000, 19_629 }` | The copies measured: his city x 1, x 9,814 (5 billion people), x 10,000 (the design's) and x 19,629 (10 billion). |
| 464 | `MapCheck.DENSE` | `2` | The copy whose screen is all city, at his density, that section 3 paints: x 10,000. |
| 467 | `MapCheck.SCREEN_ACROSS` | `18, SCREEN_DOWN = 11, SCREEN_TILES = SCREEN_ACROSS * SCREEN_DOWN` | The screen: 18 x 11 tiles, 198 - a 1,389 x 868 view at L0's least 3.2 px a plot is 13.6 x 8.5 tiles, the design's "about 200 with a margin". |
| 470 | `MapCheck.SCREEN_MS` | `80` | The design's bound on that screen's paint and raster, in ms (derived: 38 at its measured 0.19 ms a tile). |
| 473 | `MapCheck.SCREEN_RATIO` | `1.5` | The most any copy's screen may take against the 5B copy's, the first whose screen is all city (the design's; against the city x 1's until 0.7.64 - section 5's note). |
| 476 | `MapCheck.RECONCILE_MS` | `5` | The design's bound on a month's change at 5B and at 10B, in ms of the main thread's CPU since 0.7.100 (measured 0.76; section 5's note). |
| 479 | `MapCheck.PX` | `4` | Pixels a plot the screen is rastered at: 4, L0's image (spec-land 2.6). |
| 482 | `MapCheck.WARM_ROUNDS` | `3, TIMED_ROUNDS = 5` | Rounds of the screen run over every copy before any is timed, and rounds timed, each copy in turn: the least of each copy's timed rounds is its time. |
| 485 | `MapCheck.DRY_PLACE` | `0.97` | How dry the place the copies stand on must be, at a sample a tile over 3 x 3 districts: 97% - every screen tile can be built on, the painter's worst case. |
| 488 | `MapCheck.DRAWN_EVERY` | `30` | The months of section 1's city at which every tile is painted and what is drawn counted against the model: every 30th, four of its 120 (0.7.64). |
| 541 | `MapCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 956 | `MapCheck.SPILL_FIRST_CELLS` | `2, SPILL_SECOND_CELLS = 6` | The cells across the window of whole cells a spill's first district owns and its second: 2 and 6 of a district's 8 - the first draws little, the second all it is carried. |
| 1115 | `MapCheck.BOUGHT_PLOTS` | `64` | The side of a bought block, at least: 64 plots (rounded up to whole blocks of the land's level), a few cells of a district's 8 across. |
| 1375 | `MapCheck.RIM_WORKS` | `12, RIM_FLATS = 72` | The works and the flats the out-of-ground city is given a month, and where its founding site stands in its district (plots from its north-west corner): a district's order from the site is then 92, 272, 348 and 432 plo... |
| 1376 | `MapCheck.RIM_SITE_AT` | `{ 220, 128 }` |  |
| 1379 | `MapCheck.RIM_PACKED` | `60, RIM_LOOK_EVERY = 4` | The packed buildings 2e wants before it looks, and how many months it adds between looks. |
| 1749 | `MapCheck.MIX` | `{ "Low-Rise Apartments", "Small Grocery Store", "Bakery", "Elementary School"...` | What the founding district is given month by month besides houses, in turn (an apartment block, shops, a school, works): buildings of several sizes for its plan to place. |
| 1752 | `MapCheck.USED_SHARE` | `0.5` | How much of its room the founding district is brought to before a run is laid across it: half of the cells its plan may fill (CityMap.PLAN_FILL of its owned plots). |
| 1755 | `MapCheck.BUILDINGS_A_MONTH` | `3` | The buildings of the mix the founding district is given each month, beside its houses. |
| 1758 | `MapCheck.MONTHS_BETWEEN` | `4` | Months of arrivals after each change of the runs, before the next plan is drawn: the buildings that must go on the ground as the run left it. |
| 1761 | `MapCheck.HIGHWAY_MONTHS` | `60, HIGHWAY_STEP = 8` | The most months the highways are given to reach the district, and the Elevated Highways added a month while they do. |
| 1764 | `MapCheck.HIGHWAY_CLEAR` | `1` | A building keeps this far from a highway plot (batch H5: no building touches one, corners included): the reach, in plots, within which a highway laid takes a box's place. |
| 1894 | `MapCheck.RUN_KINDS` | `{ DistrictPlan.FIXED_YARD, DistrictPlan.FIXED_RAIL, DistrictPlan.FIXED_HIGHWA...` | The kinds of fixed plot the runs lay, in the order a line prints them. |
| 1896 | `MapCheck.RUN_NAMES` | `{ "yard", "rail", "highway", "reserve" }` | ...and their names, in the same order. |
| 2454 | `MapCheck.IDLE_GAP` | `3` | Months a depot is away in 2e's short gap: fewer than CityRuns.YARD_IDLE_MONTHS (the playtest's gaps were 3 to 93). |
| 2886 | `MapCheck.NEW_CITY_MONTHS` | `{ 0, 1, 12 }` | The months a new default city is drawn at: as founded, a month on and a year on (Jerus: "a brand new city shows that it has a few houses and a shop when it doesnt"). |
| 3420 | `MapCheck.ACCESS_HIGHWAYS_ADDED` | `200, ACCESS_DEPOTS_ADDED = 3` | The access fixture's added highways and Truck Depots: enough highways to cross, three depots to place. |
| 3588 | `MapCheck.SPURS_ADDED` | `12, BULK_WORKS_ADDED = 90` | The sidings fixture's added Rail Spurs and bulk works (JunctionCheck's two bulk types, two in three the larger). |
| 3892 | `MapCheck.CORRIDOR_STAGES` | `{ { 1, 6 }, { 3, 92 }, { 6, 400 }, { 9, 1385 } }` | Spec 2.7's table on the game's own ground: the design's dry place, the city's ground a square 1, 3, 6 and 9 districts a side (4 to 35 km from its middle, the prototype's 4 to 34), and its Elevated Highways the prototy... |
| 4047 | `MapCheck.YARDS_ADDED` | `2, FREIGHT_ADDED = 1` | The fixture's added rail: Rail Terminals (yards) and Freight Lines, to hold the yards' rule on (his city has spurs, no yard; 0.7.72's fixture). |
| 4311 | `MapCheck.SIDECAR_BYTES` | `500_000` | The most a sidecar may be at 5 and 10 billion people, log and all, in deflated bytes (CL3): the 10B copy's measured 333,776 (294,101 without the log, in FORMAT 8; its 1,457,418 records the canonical deal's, one batch,... |
| 4314 | `MapCheck.SIDECAR_MS` | `500` | The most the 5B and 10B copies' sidecar may take to write, and to read, in ms (CL3): measured 271 and 267 at 10B (240 and 161 at 5B), and near twice that. |
| 4361 | `MapCheck.THREADS` | `java.lang.management.ManagementFactory.getThreadMXBean()` | The JVM's threads, read for the main thread's CPU time. |
| 4364 | `MapCheck.CPU_STEP_MS` | `0.1` | The coarsest step of the thread's CPU clock a bound is read on, in ms: 0.1, a twentieth of the quickest stamp timed; coarser (Windows: 15.6) and the bounds are read on the wall clock. |
| 4367 | `MapCheck.CPU_TIMED` | `cpuTimed()` | Whether a month is timed on the main thread's CPU: this JVM measures it, it is on (turned on here if it is off), and its clock steps no coarser than CPU_STEP_MS. |
| 4508 | `MapCheck.SMALL_W` | `600, SMALL_H = 400` | The land office's small map, in pixels (spec-land 2.8). |
| 4511 | `MapCheck.SCREEN_POINTS` | `{ { 0, 0 }, { 300, 200 }, { 1344, 805 }, { 17.25, 640.5 }, { 1000, 3 } }` | Points across the screen the transforms are tried at. |
| 4961 | `MapCheck.BIG_OFFER_LEVEL` | `9` | The level of the offer the overlay's clip is tried on, zoomed in as far as the view goes: 9, blocks of 15.36 km - an offer of 2 x 4 of them is over three views across even at the expanded size, which 0.7.61's corners'... |
| 5068 | `MapCheck.DRAFT_MONTHS` | `24` | Months the draft's town is played before its map is drawn: 24. |
| 5198 | `MapCheck.SHORE_SIDE` | `3` | The section's city: three districts a side, all of it owned and drawn canonically on its measured ground - the playtest's coast (its bay, its south shore, its lagoon) - about a site one district west of the default wo... |
| 5242 | `MapCheck.BOAT_FRAME_MS` | `0.5` | The design's bound on a frame's boats at 10 billion, in ms (spec-oil 5's O13 row: "a frame's boats <= 0.5 ms at 10B, measured 0.38"). |
| 5245 | `MapCheck.BOAT_FRAMES` | `200` | Frames timed for that bound, a round: 200 at times through the month; the least of TIMED_ROUNDS rounds' means after WARM_ROUNDS. |
| 5248 | `MapCheck.ROUTES_ALL_MS` | `ham.citybuildersim.ui.UserInterface.SECONDS_PER_MONTH * 1000` | A month at normal speed, in ms: ui.UserInterface.SECONDS_PER_MONTH x 1000, 5,000 - what every terminal's route found from scratch at 10 billion must be found within (SL1, 2026-10-28). |
| 5251 | `MapCheck.ROUTES_MONTH_MS` | `ham.citybuildersim.ui.UserInterface.SECONDS_PER_MONTH * 1000 / ham.citybuilde...` | A month at the clock's top speed, in ms: SECONDS_PER_MONTH x 1000 over UserInterface.SPEED_TOP, 100 - what a month's new terminals' routes, laid onto those kept, must be found within at 10 billion (SL1). |
| 5254 | `MapCheck.ROUTES_MONTH_SHARE` | `0.01` | The share of the 10 billion lanes' terminals a month lays: 1%, 200 of the 20,000 (SL1). |
| 5257 | `MapCheck.ROUTES_ROUNDS` | `3` | Rounds of the 10 billion routes' timings, the least kept (a find from scratch is the slow one, so fewer than the frames'): 3 after one warm round. |
| 5288 | `MapCheck.HARBOUR_ROUNDS` | `12` | Rounds of orders on the harbour's coast, each one more of every terminal and tank farm: 12, more than the harbours hold, so that some come up short. |
| 5291 | `MapCheck.HARBOUR_FILL` | `60` | Terminals asked of the harbour's coast one at a time for the exactness: 60, more than its two harbours take. |
| 5496 | `MapCheck.ARM_WALK_SHIFT_X` | `- 60, ARM_WALK_SHIFT_Y = 12` | GatewayCheck's coast with its founding site 60 plots west and 12 south (found by trying the site's shifts): the coast whose best shore is the gate arm's end - the rule, its works not kept off the arms' walks, opens it... |
| 5549 | `MapCheck.HARBOUR_MANY` | `60` | General Cargo Terminals standing on the small coast of the harbours' cost: 10 (SHORE_FEW), and on the big: 60, which its two harbours hold (about 96 do). |
| 5606 | `MapCheck.SHORE_FEW` | `10` | General Cargo Terminals standing on the small coast of the shore's cost: 10. |
| 5609 | `MapCheck.SHORE_MANY` | `300` | ...and on the big coast: 300, where a late city stands thousands. |
| 5612 | `MapCheck.SHORE_GROWTH` | `2` | How many times the cost of a month's terminal may grow from SHORE_FEW standing to SHORE_MANY: twice (it was 21 times, 74 ms and 1,557 ms: every plot the search read asked every work, and every search began again at th... |
| 5615 | `MapCheck.SHORE_WARM` | `3, SHORE_TIMED = 15` | Months of one more terminal before the timing and timed, a terminal each: SHORE_WARM that are not, then the median of SHORE_TIMED kept (a month is about a ms, which a pause moves). |
| 5618 | `MapCheck.SHORE_MARGIN` | `2` | Plots asked about each work beyond its box, verge and quay: 2, past the cell the index keeps a work in. |
| 5621 | `MapCheck.SHORE_FILL` | `20` | Terminals asked of the coast that fills, one at a time: 20, more than its 12. |
| 5624 | `MapCheck.SHORE_LAGOON` | `40` | Terminals asked of each lagoon, one at a time: 40. |
| 6415 | `MapCheck.TRAIN_FRAME_MS` | `BOAT_FRAME_MS` | The design's bound on a frame's trains at 10 billion, in ms: the boats' (spec-freight 4.4, "the same structure is well inside budget"). |
| 6418 | `MapCheck.TRAIN_YARDS_A_SIDE` | `40` | Yards a side of the synthetic city the frame is timed on at 10 billion: 40, 1,600 yards, 12,800 routes on one main line. |
| 6888 | `MapCheck.MOST_TURN_DEG` | `30` | The most two neighbouring vehicles of a train may differ in heading, in degrees: 30 (FX5; on 0.8.14 a corner turned them 90 and a ladder 60). |
| 6891 | `MapCheck.LADDER_TURN_DEG` | `45` | ...and on a yard's ladder, the tightest curve the railway has: 45 degrees (FX5: an S of up to a few plots across over YARD_LADDER, 3 plots, turns a depot's neighbours 40; the long yards slice Y lays will lengthen the ... |
| 7035 | `MapCheck.TRUCK_FRAME_MS` | `TRAIN_FRAME_MS` | The design's bound on a frame's trucks at 10 billion, in ms: the trains' (FR9: the trips run the trains' pace, and a frame is the same structure). |
| 7038 | `MapCheck.TRUCK_ROADS` | `20` | Roads a district of the synthetic city has at 10 billion: 20, each with a world trip and a home trip, so a district's schedule holds 40 routes. |
| 7129 | `MapCheck.SCREEN_W_PX` | `1389, SCREEN_H_PX = 868` | The view's size in pixels the frames are timed at: section 5's 1,389 x 868 screen. |
| 7132 | `MapCheck.PROTO_TONNES` | `896_308, PROTO_PEOPLE = 469_092` | BoatProto's trade (the oil spec's prototype, its out/boat.txt): the playtest's tonnes across the boundary a month at m4000 (pt0770's save) and its people then, scaled from by people. |
| 7135 | `MapCheck.PROTO_LANES` | `20_000` | BoatProto's lanes at 10B: its cap of 20,000, each from its berth 200 plots east and 1,500 north ("a lane 45 km out to sea, north of the coast"). |
| 7137 | `MapCheck.PROTO_LANE_DX` | `200, PROTO_LANE_DY = - 1_500` | ...each lane's far end, in plots from its berth: 200 east and 1,500 north (north is less y). |
| 7297 | `MapCheck.WINDOW_STRIDE` | `EarthTerrainCheck.WINDOW / MapFrame.NODE_PX` | The stride the study's windows are drawn at: EarthTerrainCheck.WINDOW / NODE_PX, 16 plots a pixel - one node is the window, a world cell, and from it the far node strokes its own water and dithers its forest (EarthTer... |
| 7300 | `MapCheck.CLOSE_STRIDES` | `{ 2, 4, 8 }` | The strides under WINDOW_STRIDE the windows are drawn at too, where the far node is E2's region but for its rivers: 2, 4 and 8 plots a pixel. |
| 7303 | `MapCheck.SEAM_SHIFT_PX` | `MapFrame.NODE_PX / 2` | How far a node is moved to hold the 180th meridian with its neighbour, in pixels: half a node, 64 - whole blocks of EarthTerrain.FAR_BLEND_PX, so two nodes read every pixel they share alike. |
| 7306 | `MapCheck.CHUKOTKA_LAT` | `66, CHUKOTKA_LON = 180` | The full file's seam: the 180th meridian in Chukotka, 66 N, rivers and lakes either side of it. |
| 7309 | `MapCheck.CHUKOTKA_STRIDE` | `EarthTerrain.FAR_TILE_BLEND_STRIDE` | ...drawn at 64 plots a pixel (1.9 km; a node 246 km across), EarthTerrain.FAR_TILE_BLEND_STRIDE: every river stroked (FAR_RIVER_SHARE's 23 m), each tile's points blended. |
| 7312 | `MapCheck.LAKES_LAT` | `62, LAKES_LON = 26` | The full file's small lakes: Finland's lake district, 62 N 26 E. |
| 7315 | `MapCheck.LAKES_NODES` | `3` | ...over this many nodes a side about the spot at WINDOW_STRIDE: 3, 184 km, a few thousand lakes. |
| 7713 | `MapCheck.PICTURE_MONTHS` | `MONTHS` | Months section 11 plays the played city on, every district's picture asked after each as a far view asks for it: MONTHS more. |
| 7721 | `MapCheck.PICTURE_CASH_MONTHS` | `3` | FR8, 2026-10-28 (the path moved): the months of its GDP the played city is handed in cash at each look of the advisor in 11's run when it holds less. |
| 7730 | `MapCheck.PICTURE_SIGMAS` | `4` | How far a picture's share of a kind may stand from its plots' share: this many of its binomial spreads (DistrictPicture.spread()), and a pixel - four, about one district in 16,000 past it by chance. |
| 7733 | `MapCheck.NODE_ROUNDS` | `TIMED_ROUNDS` | Rounds a far node is timed over after WARM_ROUNDS, the least its time: TIMED_ROUNDS. |
| 8200 | `MapCheck.FAR_DISTRICTS` | `4` | How far out the played city's far block is bought: 4 districts past its built-up ground's east edge (31 km) - districts of its own, nothing built in them. |
| 8203 | `MapCheck.STAMP_WARM` | `20, STAMP_TIMED = 10` | Rounds the stamp is drawn before it is timed, and rounds timed, the least its time: 20 and 10 - it is drawn every month of play, so its steady state is what is timed, the JIT's compiling done (about 5 ms a round; sect... |
| 8445 | `MapCheck.KEY_FRAMES` | `60` | Frames of the keys' second: 60, the pump's (a sixtieth each). |
| 8448 | `MapCheck.KEY_SLOP_PX` | `1e-3` | How far a second of the keys may miss KEY_PX_A_SECOND, in pixels: a thousandth (★E9) - on the plane it is exact; on the globe each frame's drag keeps the point grabbed under the pointer to GlobeCheck's POINTER_PX. |
| 8451 | `MapCheck.MATRIX_PLOTS` | `1e-6` | The composed matrix's worst against the camera's and the plane's own projections, in plots: a millionth (★E9; measured 4e-10). |
| 8960 | `MapCheck.BOXES` | `200` | Boxes drawn at random on the played city's opening view, each tried against every plot under it: 200. |
| 8963 | `MapCheck.GLOBE_BOXES` | `60` | ...and through the globe's camera, each against a point at every free plot's middle: 60. |
| 8966 | `MapCheck.FIELD_BUYS_MOST` | `200` | The most offers the city buys toward an iron field for one to be on offer: 200, MiningCheck.makeRoom()'s own bound. |
| 8969 | `MapCheck.BOX_PLOTS_MOST` | `200_000` | The most plots a box is tried on, one by one: 200,000 - a box larger is drawn again smaller. |
| 8972 | `MapCheck.GLOBE_LAT` | `51.90625, GLOBE_LON = 4.48125` | Where the globe's check looks from: EarthCheck's Rotterdam window's centre, the played city's site put there. |
| 9338 | `MapCheck.PICKS_MONTHS` | `3` | The months section 14b holds the picks over, each with a place relisted: 3. |
| 9341 | `MapCheck.PICKS_HELD` | `3` | The offers section 14b picks and holds over the months: 3. |
| 9508 | `MapCheck.FAR_PLOTS` | `World.SIDE / 4.0` | How far from the city section 14c sets the plane: a quarter of the world's side, in plots. |
| 9511 | `MapCheck.FAR_DRAGS` | `50` | The drags section 14c carries the plane on, further out, of the pixels each. |
| 9557 | `MapCheck.PRESS_PLOTS` | `400` | The plots of an offer's rectangle section 14d tries for the owned-or-free rule: at most about this many, a stride over the rectangle. |
| 9658 | `MapCheck.STEPS` | `{ { 1, 0 }, { - 1, 0 }, { 0, 1 }, { 0, - 1 } }` | The four steps from a block to its neighbours: east, west, south, north. |
| 10606 | `MapCheck.MATERIALS_SIDE` | `10` | The materials fixture's square, in districts a side: 10 (77 km), wide enough to hold fields of every typed slot on the default world. |
| 10609 | `MapCheck.MATERIALS_SECTORS` | `{ Sectors.MINING, Sectors.HEAVY_INDUSTRY, Sectors.COAL_MINING, Sectors.QUARRY...` | The nine materials industries, Iron Mining to Wood Products (spec-materials 2.1): every building of theirs drawn with a look of its own. |
| 10946 | `MapCheck.MATERIALS_RINGS` | `24` | How far out materialsPlace() looks, in cells: 24. |
| 10987 | `MapCheck.AV_SAMPLES` | `4` | How many samples a step of an avenue's line is sampled at: four, a quarter step apart. |
| 10990 | `MapCheck.CROSSING_TILES` | `4` | How many tiles either way of the origin's tile the crossings are read over: 4, nine tiles a side (MF1). |
| 10993 | `MapCheck.AV_STEPS` | `400` | The farthest an arm is followed from the origin, in steps: 400, past a district and a half. |
| 11001 | `MapCheck.AV_TIMES` | `9, AV_SIDE = 6, AV_SHIFT_X = 104, AV_SHIFT_Y = 56` | The city the avenues are drawn on: Jerus's city x 9 on a square of 6 districts a side (his density, as the copies', every district full) whose founding site is the dry place's moved AV_SHIFT_X plots east and AV_SHIFT_... |
| 11021 | `MapCheck.AvTiles.OWNED` | `1, BENEATH = 2, BRIDGE = 4` | Flag bits of a plot: it is owned, a street passes beneath a highway on it, it is a bridge's. |
| 11380 | `MapCheck.CV_FROM` | `3, CV_TO = 8` | The districts the curves are looked for in, in districts east and south of the founding site's: 3 to 8 - past CRESCENT_FROM (16 cells, two districts) wherever the cell lies. |
| 11383 | `MapCheck.CV_CRESCENTS` | `8, CV_SHORES = 16` | The most crescents and shore curves the audit follows: 8 and 16 (the shore curves that cross a tile's edge, and those that bend, first). |
| 11386 | `MapCheck.CV_TURN_SPAN` | `4` | How far apart, in plots, the turn audit's samples are: 4 - at a crescent's radius (22.6 plots) a turn of 0.18 radian between neighbours. |
| 11389 | `MapCheck.CV_CENTRE_PX` | `1.0` | How far the band's middle, measured across it from the pixels, may lie from the line: a pixel - the least a raster can place a band, and less than the 2.8 pixels at 4 px a plot a staircase's bars lie from the arc thro... |
| 11392 | `MapCheck.CV_NARROW_PX` | `1.5` | How much narrower than its street the band, measured across it, may measure: 1.5 pixels (a pixel's rounding at either end and a half). |
| 11395 | `MapCheck.CV_GENTLE` | `0.2` | How much the line may turn within a plot and a quarter either side of a sample for the band to be measured across it there: 0.2 radian - a crescent turns 0.06 over that; a shore street's corner (rounded to as little a... |
| 11398 | `MapCheck.CV_MEASURED` | `{ 0.5, 0.35 }` | The share of a kind's samples the band must have been measured across (crescents, shore curves): a half, and 35% - a shore's line turns a corner every few plots, and the band is not measured within a plot and a quarte... |
| 11998 | `MapCheck.HIS_TIMES` | `80` | The copy at Jerus's own district count: x 80, 125 districts, 11 a side (CL3's notes cite 115 for his cities), between x 1's 4 districts and 5B's 14,661. |
| 12001 | `MapCheck.REDRAW_MONTHS` | `24, REDRAW_MONTHS_5B = 12` | The months grown at x 1 and at x 80, and at 5B (its months are slower): 24 and 12. |
| 12004 | `MapCheck.REDRAW_WARM` | `3` | The first months of each fixture are drawn, compared and not counted in its figures: 3, the JIT's compiling done before a month is read. |
| 12007 | `MapCheck.GROW_PER_MILLE` | `1000, GROW_PER_TEN_THOUSAND = 10_000` | A month's growth as a share of each type's count: 1/1,000 (+0.1%) at x 1 and x 80, 1/10,000 (+0.01%) at 5B, as section 5's, at least one building each. |
| 12010 | `MapCheck.RUNS_EVERY` | `4` | The runs' types (a highway's, a track's) are grown only in every 4th month, so most months lay no run, as in play. |
| 12013 | `MapCheck.CHAIN_ROUNDS` | `5` | The times a month's screens are drawn: the month's, then again after each round of the chain's jobs (handed out, run, adopted) until none is left, at most 5 (4 until batch UP5: a lazy job that cannot take its link as ... |
| 12016 | `MapCheck.PLAYED_BEFORE` | `3, PLAYED_AFTER = 3` | The months the played city is grown before its land purchase and after it: 3 and 3. |
| 12019 | `MapCheck.PLAYED_WARM_MAX` | `6` | The most months the played city is grown, not judged, to settle its stamp's view before the months judged: 6 (WN3, after EM1: it took 1). |
| 12022 | `MapCheck.PURCHASE_SIDE` | `32` | The side of the block the played city buys, in plots: 32, a hole in its built-up ground's box (the played city's ground is irregular). |
| 12025 | `MapCheck.MAP_MONTH_MS` | `19` | The most a month's FX-side map work may take at 5B, in ms, its median month - the stamp, the far view's screen, an L0 screen re-stamped (since UP5 keyed, and read where the key moved) and the chain's handing out and a... |
| 12028 | `MapCheck.GROW` | `0, ONE = 1, SETTLE = 2` | The kinds of month: the grown one, one built type raised by one, and the first, which only draws. |
| 13555 | `MapCheck.NEAR_BOX_KM` | `8` | The side of the box of the city's ground the borders are crossed against, km: 8 - a few kilometres, and wide enough for the fixture's Belgium-Netherlands line, which Natural Earth runs 2.7 km from Baarle-Nassau (a box... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 405 | `static int fails` |  |
| 406 | `static PrintStream out` |  |
| 735 | `long[] buildings` | Buildings by type id: a mine or well on a site once, however many tiles its site spans - since 0.8.0 a works once for all the sites it takes; packed ones among them. |
| 737 | `long[] siteCount` | The sites under each type's works, by type id (0.8.0), each once; and the types whose sites are not a whole number of its works. |
| 738 | `long partWorks` |  |
| 740 | `final double[] surface` | The streets' surface in plots by kind [0, gravel, paved, highway] - a highway's own plots among the highway's - and what the plans have no street for (CityMap.leftoverByKind()). |
| 742 | `long rail, trackSurplus, packed, tiles, districts` | The railway's track laid, in plots (0.7.72), and (0.7.89) what the runs could not lay - since FR7 slice 3 the plots no train's way uses (CityRuns.trackSurplus()); buildings packed at the city's edge (R7); tiles painte... |
| 839 | `static GameFiles cityFiles` | The played city's save folder. |
| 1135 | `final CityMap map` |  |
| 1136 | `final long[] model` |  |
| 1137 | `final int house` |  |
| 1139 | `int month` | The month the next one is run in. |
| 1140 | `boolean reconciled` |  |
| 1389 | `final BuildingVisual.Type[] types` |  |
| 1390 | `final long seed` |  |
| 1391 | `final long siteX, siteY` |  |
| 1392 | `final CityMap map` |  |
| 1393 | `final long[] model` |  |
| 1394 | `final int works` |  |
| 1396 | `int month` | The month the next one is run in. |
| 1397 | `boolean reconciled` |  |
| 1768 | `final Months h` |  |
| 1769 | `final int[] mix` |  |
| 1770 | `final int step` |  |
| 1771 | `int next` |  |
| 1793 | `final DistrictPlan plan` |  |
| 1794 | `final java.util.Set<PlanBox> boxes` |  |
| 1795 | `final byte[] fixed, runs` |  |
| 1796 | `final boolean[] ramp` |  |
| 1798 | `final int month, journal` | The map's month when the snapshot was taken, and how many records the district's journal of the runs held then. |
| 2704 | `final BuildingVisual.Type[] types` |  |
| 2705 | `final long seed` |  |
| 2706 | `final long x, y` |  |
| 2707 | `final CityMap[] maps` |  |
| 2708 | `final long[][] counts` |  |
| 2709 | `final double[] buildMs` |  |
| 2711 | `final long ironX, ironY` | His city x 1 again where its square holds iron (0.7.99, batch W1; dryPlace(w, side)): the fixtures that need his mines - the runs' marks, the rail yards - stand on it; the dry place's own holds none since the deposits... |
| 2712 | `final CityMap ironX1` |  |
| 2713 | `final long[] ironCounts` |  |
| 3005 | `final CityMap map` |  |
| 3006 | `final long px0, py0` |  |
| 3007 | `final int w, h` |  |
| 3008 | `final byte[] use, road, width, role, run` |  |
| 3009 | `final boolean[] owned, sea, wet, bridge, beneath, mined` |  |
| 3011 | `final List<int[]> boxes` | Each building: {x0, y0, w, h, type, site (or -1), packed (1 or 0)} in the box's plots. |
| 3012 | `long rail, crossings, packed, siding` |  |
| 3013 | `final double[] surface` |  |
| 3014 | `final long[] buildings` |  |
| 3015 | `final java.util.Set<Long> sites` |  |
| 3955 | `final long x0, y0` |  |
| 3956 | `final int w, h` |  |
| 3957 | `final byte[] fixed` |  |
| 7514 | `final List<double[]> segs` |  |
| 7515 | `final List<double[]> lakes` |  |
| 7516 | `final long x0, y0` |  |
| 7517 | `final int stride` |  |
| 11014 | `final CityMap m` |  |
| 11015 | `final TilePainter.Input in` |  |
| 11016 | `final TilePainter.Painted p` |  |
| 11017 | `final java.util.Map<Long, int[]> img` |  |
| 11018 | `final java.util.Map<Long, byte[]> street` |  |
| 11418 | `final CityMap m` |  |
| 11419 | `final int px` |  |
| 11420 | `final TilePainter.Input in` |  |
| 11421 | `final TilePainter.Painted p` |  |
| 11422 | `final java.util.Map<Long, int[]> img` |  |
| 11423 | `final java.util.Map<Long, byte[]> street` |  |
| 11424 | `final java.util.Map<Long, short[]> types` |  |
| 11427 | `final boolean bare` | Whether the tiles are drawn without the curves (the control: as they were before CL4). |
| 11493 | `int samples, badColour, onBuilding, edges, audited, narrow, off, turns, turnsBad, tracks` |  |
| 11494 | `double worstCentre, worstExcess, worstIdeal` |  |
| 11496 | `String firstBad` | The first sample that was not the street's colour or lay off the line, for the line a failure prints: where, what. |
| 12032 | `final String name` |  |
| 12033 | `final MapFrame f` |  |
| 12034 | `final long plots, span` |  |
| 12035 | `final boolean pictures` |  |
| 12036 | `final int count` |  |
| 12037 | `final long[] nx, ny` |  |
| 12038 | `final byte[][] ground` |  |
| 12040 | `final long[] key` | Now's: the key each node was drawn from, its pixels, whether it was drawn, and short of a picture. |
| 12041 | `final int[][] pix` |  |
| 12042 | `final boolean[] drawn, partial` |  |
| 12044 | `final boolean[] todayPartial` | Today's: whether it would be drawn again after a picture came. |
| 12046 | `final boolean[] held` | The nodes held (not drawn again) in the month under way. |
| 12047 | `final int[] buf` |  |
| 12087 | `int kind, number, moved, rounds, astray, kept, built` |  |
| 12089 | `final java.util.List<CityMap.District> staleList` | The built districts whose pictures were no longer current right after a one-type month (`stale` of them). |
| 12090 | `boolean timed, reconciled, viewChanged, runsMoved, settled` |  |
| 12091 | `CityMap.District onlyMoved` |  |
| 12093 | `long lands, runsDrawn, repainted, onStamp` | The stamp's kept nodes in the month: land layers drawn, runs layers drawn, districts repainted (all 0 when the view changed and they were made anew), and the districts they list. |
| 12094 | `double stampNow, stampToday, farNow, farToday, l0, chain, keys, lazy` |  |
| 12096 | `double l0Keys, l0Inputs` | (batch UP5) The L0 screen's pass: ms the key pass (ready() and tileKey() of every tile in view) and the input pass (input() of the tiles whose key moved) took; the ready tiles in view, those whose key held and those w... |
| 12097 | `int l0Tiles, l0Held, l0Moved, l0KeyOnly, l0Checked, l0Wrong` |  |
| 12099 | `final int[] l0PartMoved` | ...and, of the keys that moved (a tile read before), how many moved in each of the four parts CityMap.tileKeyParts() folds: what the city owns of the tile, its plans, the railway's bends on it, the whole map's. |
| 12101 | `final java.util.List<CityMap.District> movedList` | (batch UP5) The districts whose version moved this month, and the boxes {x0, y0, x1, y1, pad} in plots a stretch or a dot the runs draw on a far node was put in or taken from (pad 1 for a dot, which is two pixels acro... |
| 12102 | `final java.util.List<long[]> runBoxes` |  |
| 12104 | `int[] recoloured, partialBefore` | Per far screen: its nodes drawn this month (the month's round and the rounds after), and the partial ones before it. |
| 12106 | `final java.util.List<java.util.List<Integer>> keyMoved` | Per far screen: the nodes drawn in the month's own round because the key they were drawn from had moved. |
| 12108 | `final java.util.List<java.util.List<Integer>> keyMovedFinal` | (batch UP5, slice A) Per far screen: the nodes whose key at the end of the month's rounds is not the one they were drawn from before the month (what the month changed, once the chain has settled). |
| 12110 | `int handed` | (batch UP5, slice A) The chain of the month: plan jobs handed out and the ms the worker spent on them (plans and pictures, run in turn); as adopt() kept them, the plans made, the links taken as they stood, the jobs th... |
| 12111 | `long madePlans, reusedLinks, failedJobs, wastedPlans` |  |
| 12112 | `double workerMs` |  |
| 12114 | `int remade, carryMoved, staleAfter` | (batch UP5, slice A) Links kept from before the month: re-made, and of those how many carried a different carry out; the built districts left with no current picture after the rounds. |
| 12116 | `long[] ownCalls` | (batch UP5, slice A) Own stamps {asked, worked out} this month and, after the rounds, {kept, those whose key held, those of them that differ from a stamp worked out afresh} (CityMap.ownStampsKept()). |
| 12117 | `int[] ownKept` |  |
| 12125 | `final String name` |  |
| 12126 | `final CityMap m` |  |
| 12127 | `final long[] c` |  |
| 12128 | `final int divisor, mine` |  |
| 12129 | `final long sites` |  |
| 12130 | `final BuildingVisual.Type[] tys` |  |
| 12131 | `final MapTiles tk, tr` |  |
| 12132 | `final MapFrame l0` |  |
| 12133 | `final boolean withL0, checkLinks` |  |
| 12134 | `final java.util.List<FarScreen> fars` |  |
| 12135 | `final java.util.List<RedrawMonth> all` |  |
| 12136 | `final TilePainter.Input in` |  |
| 12137 | `MiniMapFrame.Box frame` |  |
| 12138 | `MiniMapFrame.View stampV, previewV` |  |
| 12139 | `byte[][] stampG, previewG` |  |
| 12140 | `MapTiles.KeptNode[] stampK, previewK` |  |
| 12141 | `int[] stampNow, stampToday, previewNow, previewToday` |  |
| 12142 | `int viewChanges` |  |
| 12144 | `int stampsDrawn, previewsDrawn, keyUnmoved, heldCompared` | What was compared: stamps and previews drawn two ways (their pixels counted), and far nodes whose key was unmoved against a fresh one (those held in the month among them); whether every one was equal. |
| 12145 | `long pixelsCompared` |  |
| 12146 | `boolean stampEqual` |  |
| 12147 | `double tookS` |  |
| 12149 | `Runnable beforeReconcile` | Run before the next month's reconcile (a land purchase). |
| 12151 | `final java.util.Map<Long, long[]> l0Kept` | (batch UP5) The L0 screen's tiles as the view keeps them: by MapTiles.key(), the key and stamp each was last read at; the totals of tiles held, moved, read afresh and found wrong, over the months. |
| 12152 | `final java.util.Map<Long, long[]> l0Parts` |  |
| 12153 | `final int[] l0PartMoved` |  |
| 12154 | `int l0Held, l0Moved, l0KeyOnly, l0Checked, l0Wrong` |  |
| 12348 | `private long[] l0Tx, l0Ty, l0Key` | The ready tiles of the L0 screen with their column, row and key (l0Pass()): the arrays are kept between months, the screen being one. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 403 | 13235 | **type** `public class MapCheck` | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's street plan and paints the same pixels from the same inputs, the draw... |
| 408 | 4 | `static void check(String label, boolean ok)` |  |
| 452 | 7 | `static double groundKm2(BuildingVisual.Type[] types)` | The dry ground the copies' x 1 stands on (0.7.71): his, scaled by his buildings' land on this catalogue over theirs then, so the copies keep his density (JERUS_FILL) whatever the catalogue: with his childcare restated... |
| 490 | 50 | `public static void main(String[] args) throws Exception` |  |

### 1. A CITY'S MAP ADDS UP (lines 546-727)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 127 | `static Game playedCity(Path root) throws Exception` |  |
| 691 | 36 | `static boolean worldWaysOk(CityMap m, int[] seen)` | Whether a month's lines to the world and trains lie on the ground they should (FX2; GatewayCheck holds the whole list on its fixtures, this is the cheap half, the points and the routes and never the box): each world l... |

### WHAT THE MAP DRAWS, EVERY TILE PAINTED (0.7.64) (lines 728-861)

| line | len | member | says |
|---:|---:|---|---|
| 733 | 32 | **type** `static final class Drawn` | What the painted map draws over every tile of a map's districts and a tile round them (0.7.88: a district's plan lays the next one's first column and row). |
| 744 | 1 | `long total()` _(in MapCheck.Drawn)_ |  |
| 745 | 1 | `int kinds()` _(in MapCheck.Drawn)_ |  |
| 746 | 1 | `double surfaced()` _(in MapCheck.Drawn)_ |  |
| 747 | 1 | `double left()` _(in MapCheck.Drawn)_ |  |
| 750 | 14 | `boolean sameAs(long[] model, BuildingVisual.Type[] types)` _(in MapCheck.Drawn)_ | Whether it is exactly the model's: every drawn type as many as the model has (a kind it has none of not drawn); each road kind's plots its streets' surface and what they do not carry, to within half a plot a district ... |
| 767 | 9 | `static long[] tilesOf(CityMap m)` | The tiles a map's districts cover and one round them: {tx0, ty0, across, down}. |
| 778 | 37 | `static Drawn drawn(CityMap m)` | Paints every tile of every district on a map, and a tile round them, and counts what is drawn. |
| 817 | 5 | `static long modelBuildings(long[] model, BuildingVisual.Type[] types)` | The model's buildings the map draws: every type but the roads. |
| 824 | 7 | `static long roadPlotsOf(long[] model, BuildingVisual.Type[] types, int kind)` | The model's road plots of a kind: each road type's count x its own plots (BuildingVisual.cells()). |
| 833 | 4 | `static long modelRoadPlots(long[] model, BuildingVisual.Type[] types)` | ...of every kind. |
| 842 | 8 | `static boolean roomAt(CityMap.District d, BuildingVisual.Type type, double share)` | Whether district d has room for one more of a type at a road share: CityMap.hasRoomFor()'s rule (buildingRoom()), at the share given rather than the map's own. |
| 852 | 4 | `static long mineSites(CityMap m, int mine)` | The sites a mine type may stand on (0.8.0, batch I14): its resource's of its type of field - the game's own rule since I5 (Game.committedOn(r, type), LandManager.fits()), an Iron Mine on hematite. |
| 857 | 4 | `static int mineId(Game g)` |  |

### 2. NOTHING PLACED MOVES (lines 862-940)

| line | len | member | says |
|---:|---:|---|---|
| 866 | 74 | `static void nothingMoves(Game g)` |  |

### 2b. A DISTRICT IS FULL AT WHAT ITS PLAN CAN DRAW, AND A SPILL KEEPS ITS TIME (batch CL3) (lines 941-1094)

| line | len | member | says |
|---:|---:|---|---|
| 958 | 7 | `static void districtsHoldAndSpill(Game g)` |  |
| 971 | 39 | `static void holdsWhatItCanDraw(BuildingVisual.Type[] types, int flats)` | A district holds what its plan can draw: on a square city of four districts, a few apartment blocks added month by month (reconcile) until the nearest district stops taking them. |
| 1016 | 52 | `static void spillKeepsItsTime(BuildingVisual.Type[] types, int house, int store)` | A spill keeps its time, at the plan's level: the first district's input is given more than its window of ground can draw, the second's carry is the pairs the first leaves ({month, type}, the month each came in) - and ... |
| 1073 | 21 | `static DistrictPlan.Input spillInput(BuildingVisual.Type[] types, int cellsAcross, int[][] records, int firstBatch, int[] carry)` | A hand-made district for 2b's spill: flat ground, a square window of whole cells round the hub owned (the rest not), its types, its log (records {month, type, change}, the first `firstBatch` one batch; its counts the ... |

### 2c. A DISTRICT THAT BUYS LAND INSIDE ITSELF KEEPS ITS BUILDINGS WHERE THEY STAND (batch FX6) (lines 1095-1357)

| line | len | member | says |
|---:|---:|---|---|
| 1118 | 1 | **type** `record PlanBox(int type, int x, int y, int w, int h)` | One building of a plan: its type and its box in the frame's plots. |
| 1121 | 11 | **type** `record Bought(long x0, long y0, long x1, long y1, int dry)` | A rectangle of plots bought inside a district, [x0, x1) x [y0, y1) in the world, and how many of its plots are dry. |
| 1123 | 3 | `double nearest(long sx, long sy)` _(in MapCheck.Bought)_ | How far its nearest plot is from plot (sx, sy), in plots. |
| 1128 | 3 | `boolean holds(PlanBox b, long fx, long fy)` _(in MapCheck.Bought)_ | Whether the box lies wholly on this block, a box in the frame whose first plot is (fx, fy) in the world. |
| 1134 | 24 | **type** `static final class Months` | A harness city's months: houses added to the model a month at a time and the map reconciled to them, each in its month. |
| 1142 | 6 | `Months(CityMap map, long[] model, int house, int month)` _(in MapCheck.Months)_ |  |
| 1150 | 1 | `int last()` _(in MapCheck.Months)_ | The month that last ran: what a purchase made between months is dated. |
| 1153 | 4 | `void run(int houses)` _(in MapCheck.Months)_ | One month of `houses` more houses. |
| 1165 | 122 | `static void districtBuysLandInside(Game g)` | Whether a district that buys land inside itself keeps its buildings where they stand (batch FX6): on the played city's ground, a block is bought in the next district, and the district holding the founding site is give... |
| 1289 | 5 | `static java.util.Set<PlanBox> boxesOf(DistrictPlan p)` | The plan's buildings as boxes, a set: a building stands where it did when its box is in the plan again. |
| 1296 | 5 | `static int movedOf(java.util.Set<PlanBox> before, java.util.Set<PlanBox> after)` | How many of the boxes before are not among the boxes after: the buildings that moved. |
| 1303 | 5 | `static int housesElsewhere(CityMap m, CityMap.District d, int house)` | The houses the map holds in the districts but d. |
| 1314 | 22 | `static Bought nearestFreeBlock(CityLand land, DistrictPlan.Input frame)` | The block of whole land blocks (BOUGHT_PLOTS a side at the least, rounded up to the level's blocks) the city could buy inside a district's frame (not on its last row or column) that lies wholly unowned, is at least ha... |
| 1338 | 7 | `static int dryPlots(DistrictPlan.Input frame, long x0, long y0, long x1, long y1)` | The dry plots of [x0, x1) x [y0, y1) in the world, read from a plan input's ground (the world's, over the frame). |
| 1347 | 9 | `static boolean soundBlock(CityLand land, DistrictPlan.Input frame, Bought b)` | Whether a block about to be bought is what 2c says it is, checked on the land as it stands: whole blocks of its level, inside the frame, wholly unowned, at least half dry. |

### 2f. THE OUTER RIM: WHAT A CITY HAS NO ROOM FOR STANDS BESIDE A STREET (batch MF1) (lines 1358-1595)

| line | len | member | says |
|---:|---:|---|---|
| 1382 | 4 | `static int idNamed(String name)` | The id of the building template of that name, from the catalog. |
| 1388 | 40 | **type** `static final class Rim` | The out-of-ground city of 2e and 2f: two districts a side on the default world's dry place, the founding site at RIM_SITE_AT in the last of them, drawn canonically and given works and flats month by month. |
| 1399 | 15 | `Rim()` _(in MapCheck.Rim)_ |  |
| 1416 | 5 | `void run()` _(in MapCheck.Rim)_ | One month: RIM_WORKS more works and RIM_FLATS more flats, the map reconciled to them. |
| 1423 | 4 | `void runFlats()` _(in MapCheck.Rim)_ | ...and one month of the flats alone. |
| 1430 | 13 | `static java.util.Set<String> packedBoxes(CityMap m)` | Every packed box on a map, by district and tile: the box as Drawn.boxes has it, with its type. |
| 1444 | 72 | `static void outerRim()` |  |
| 1518 | 3 | `static void plotOn(long[] m, int x, int y)` | A plot of a district's mask (a bit a plot, DISTRICT / 64 words a row). |
| 1522 | 3 | `static boolean plotIs(long[] m, int x, int y)` |  |
| 1527 | 7 | `static CityMap.Pack handPack(BuildingVisual.Type[] types, int t, long[] free, long[] street, CityMap.District e)` | One hand-made edge district packed with one building of type t: its free ground and its streets as given, its tiles in order. |
| 1536 | 4 | `static int[] onlyBox(CityMap.Pack p, CityMap.District e)` | The one box a hand-made packing put on tile 25 of the district, or none. |
| 1548 | 47 | `static void laidByHand(BuildingVisual.Type[] types)` | ...the street laid to a building no street is near, on ground made for it: a street down x = 40, the one free ground a corridor a plot wide along y = 100 from it (x 41 to 46) and a block of three rows beyond (x 47 to ... |

### 2g. A DISTRICT BOUGHT INTO THE MAP'S ORDER AHEAD OF OTHERS KEEPS WHAT IS CARRIED IN PLACE (batch MF1; CL3's re-chaining) (lines 1596-1723)

| line | len | member | says |
|---:|---:|---|---|
| 1612 | 22 | `static long[] dryBlockIn(World w, CityLand land, long x0, long y0)` | The block of 64 x 64 plots, whole blocks of the land's level at least, over ground at least nine tenths dry, nearest the middle of the district at (x0, y0) of the world: {x0, y0, x1, y1}, or null. |
| 1636 | 1 | **type** `record ChainSnap(java.util.Map<String, java.util.Set<Long>> boxes, java.util.Map<String, int[]> carry)` | Each district's placed boxes (a box in the district, the type with it, the packed left out) and what was carried to it, by its place in the map: what a month's change is held against. |
| 1638 | 21 | `static ChainSnap chainSnap(CityMap m)` |  |
| 1661 | 6 | `static boolean keepsPrefix(int[] before, int[] after)` | Whether the pairs of `after` begin with all the pairs `before` had, in order (an append moves nothing; a pair taken out of the middle is a different history). |
| 1668 | 55 | `static void districtJoinsTheChain()` |  |

### 2d. A RUN LAID ACROSS A DISTRICT'S BUILDINGS MOVES NONE OF THEM (batch FX7) (lines 1724-2213)

| line | len | member | says |
|---:|---:|---|---|
| 1767 | 23 | **type** `static final class Arrivals` | The arrivals of the founding district, month by month: `step` houses and BUILDINGS_A_MONTH buildings of the mix, in turn. |
| 1773 | 5 | `Arrivals(Months h, int[] mix, int step)` _(in MapCheck.Arrivals)_ |  |
| 1780 | 4 | `void month()` _(in MapCheck.Arrivals)_ | One month: the mix's next buildings and the houses. |
| 1786 | 3 | `void months(int n)` _(in MapCheck.Arrivals)_ | `n` months. |
| 1792 | 75 | **type** `static final class Snap` | The founding district's plan at one moment: its boxes, the plots its input fixes, the city's runs alone in its frame, and the ramps. |
| 1800 | 11 | `Snap(CityMap m, CityMap.District d)` _(in MapCheck.Snap)_ |  |
| 1813 | 5 | `int under(Snap later, int reach)` _(in MapCheck.Snap)_ | How many of this plan's boxes have a plot within `reach` plots of a plot the runs hold in `later` and did not hold here: the buildings a run laid in between lies across. |
| 1820 | 5 | `int arrivalsOnRuns(Snap later)` _(in MapCheck.Snap)_ | How many of `later`'s boxes that are not among this plan's lie on a plot fixed in `later`, in its input or among the runs: the arrivals that stand on a run. |
| 1827 | 5 | `int onRuns()` _(in MapCheck.Snap)_ | How many of this plan's boxes stand on a plot its input fixes (a highway, the railway, a yard, a reserve) or beside a highway plot of it, the highway's verge (H5): the buildings a run lies across (MF1). |
| 1834 | 7 | `int lying(Snap later)` _(in MapCheck.Snap)_ | How many of this plan's boxes `later`'s runs lie across or beside (MF1): a plot of the box is a plot the runs hold in `later` and did not hold here, or lies within a plot of a highway they hold there and did not here. |
| 1843 | 5 | `int plots(byte kind)` _(in MapCheck.Snap)_ | The plots of kind `kind` in the runs. |
| 1850 | 5 | `int newPlots(Snap later, byte kind)` _(in MapCheck.Snap)_ | The plots of kind `kind` the runs hold in `later` and did not hold here. |
| 1860 | 6 | `int movedWithTheseRuns(CityMap m, CityMap.District d)` _(in MapCheck.Snap)_ | How many of this plan's boxes are not in the plan the map makes now when its input is given this snapshot's fixed ground and ramps: the buildings that move with the arrivals alone, the runs left as they were. |
| 1869 | 6 | `static boolean anyPlot(PlanBox b, int reach, java.util.function.IntPredicate test)` | Whether any plot within `reach` plots of the box (in the frame's plots, clipped to the frame) satisfies the test, a plot's index in the frame. |
| 1880 | 12 | `static int journalDiffers(DistrictPlan.Input later, Snap s)` | How many plots of `later`'s fixed ground, worked back through its frame's records (Input.fixedLog) dated after the snapshot's month, differ from the fixed ground the snapshot's input held (the interchanges' reserves, ... |
| 1899 | 8 | `static String laidOf(Snap before, Snap after)` | What the runs laid between two snapshots, by kind: "yard 56, rail 19". |
| 1909 | 4 | `static Snap quietMonth(Months h, CityMap.District d)` | The district's plan after one month of the model as it stands, nothing added: the month a run is laid in. |
| 1915 | 3 | `static int idOf(Game g, String name)` | The id of the building template of that name. |
| 1924 | 19 | `static long[] highwayCrossing(CityLand land, DistrictPlan.Input frame)` | Where the model starts the highways once the city owns the ground there (CityRuns.start(): the crossing of the lattice, a line every CityRuns.CELL plots from CityRuns.HIGHWAY_AT, nearest the founding site that is dry)... |
| 1952 | 3 | `static long handGroundRound(CityLand land, DistrictPlan.Input frame, long[] at, int month)` | Hands the city, between months, every whole block of its level that lies in the square of CityRuns.CELL plots each way round `at` and that it does not own (a row of them side by side one parcel, bought as 2c's blocks ... |
| 1957 | 5 | `static long handGroundRound(CityLand land, DistrictPlan.Input frame, long[] at, int month, long[] skip)` | ...leaving out the block that holds plot `skip` ({x, y}; none for null): ground to be bought in a month of its own (2h). |
| 1968 | 21 | `static long handBlocks(CityLand land, long x0, long y0, long x1, long y1, int level, int month, long[] skip)` | Hands the city the blocks of `level` in [x0, x1) x [y0, y1) that no holding owns (a row of them side by side one parcel), and each block it owns part of in its four halves, one level down, until LandGrid.MIN_LEVEL (a ... |
| 2004 | 209 | `static void runsLaidAcross(Game g)` | Whether a run laid across a district's buildings moves off it the buildings it lies across or beside and no others (batch FX7: moved none; MF1, 2026-10-28: "Buildings move off", the boxes under a new run, or beside a ... |

### 2h. AN INTERCHANGE'S RESERVE DATED (batch UP5, FX7's star 2) (lines 2214-2436)

| line | len | member | says |
|---:|---:|---|---|
| 2236 | 8 | `static long[] reserveHole(CityLand land, long[] crossing)` | The block of the plots the crossing's reserve box would hold that the crossing is not in: a plot {x, y} in it, a block of its own to be handed the city last; null where the box lies in the crossing's block. |
| 2246 | 6 | `static long handBlock(CityLand land, long[] at, int month)` | Hands the city the one block that holds plot `at` ({x, y, the block level it was found at}: the level moves as the city's ground grows, the block is the one handGroundRound() left out), between months, as 2c's blocks ... |
| 2254 | 9 | `static int reserveRecords(CityMap.District d, int month, boolean toReserve)` | How many of a district's journal records at `month` take a plot from nothing to an interchange's reserve (`toReserve`), or the other way. |
| 2265 | 14 | `static DistrictPlan undatedReserve(CityMap m, CityMap.District d)` | The plan of d's input with every record of a plot that lies in an interchange's reserve left out: the journal as it was before UP5, which did not know the reserves. |
| 2280 | 6 | `static void reservesDated(Game g)` |  |
| 2290 | 23 | `static void guardLocal(CityMap m)` | The guard an interchange's reserve asks (reserveBlocked(): a mine on its site or a work on the shore in the box) reads the districts the box lies in and a margin about it, not the list of every mined site: for boxes o... |
| 2315 | 110 | `static void reserveScenario(Game g, boolean blockLeft)` | 2h's one scenario: with one block of the reserve's box left out of the ground handed (`blockLeft`), bought later, or with all of it handed. |
| 2427 | 9 | `static int exactJournalDiffers(DistrictPlan.Input later, Snap s)` | How many plots of `later`'s fixed ground worked back through its records dated after the snapshot's month differ from the fixed ground the snapshot's input held: journalDiffers() with the reserves counted, as the jour... |

### 2e. A YARD WHOSE BUILDING LEFT STAYS LAID, IDLE (batch MF1, item 6; FX7's star 4) (lines 2437-2697)

| line | len | member | says |
|---:|---:|---|---|
| 2457 | 5 | `static int journalRecords(CityMap m)` | The records of the runs' journal over all the map's districts (THE RUNS DATED): one for each plot whose kind of fixed ground a month changed. |
| 2464 | 5 | `static int routesToYard(CityMap m, int i)` | How many routes a train runs to yard i of the map (not to a works' siding). |
| 2471 | 16 | `static int wagonPixels(CityMap m, long[] yard)` | The wagon pixels the tiles a yard's box reaches paint at 8 px a plot (TileRaster.WAGON): 0 where no wagons stand. |
| 2489 | 14 | `static boolean[] idleOnTiles(CityMap m, long[] yard)` | Whether the tiles a yard's box reaches hand the painter a yard at all, and whether any is handed as idle (TilePainter.Input.YARD_IDLE): {handed, idle}. |
| 2504 | 88 | `static void idleYards(Game g)` |  |
| 2594 | 53 | `static void idleYardsOnRuns()` | 2e's runs-level half: several yards on open ground (MainLineCheck's), one month at a time. |
| 2649 | 23 | `static void idleYardsTrains(Squares sq)` | 2e's last part, on the yard city (its gateway, its railway's world line, two depots' yards): no train runs to an idle yard. |
| 2674 | 5 | `static int cellsMin(BuildingVisual.Type[] types, int[] ids)` | The smallest and the largest footprint, in cells, of the types listed. |
| 2680 | 5 | `static int cellsMax(BuildingVisual.Type[] types, int[] ids)` |  |
| 2687 | 3 | `static String hubText(long[] hub, DistrictPlan.Input frame)` | A net's hub in the frame's plots, for a line: "40,72". |
| 2692 | 5 | `static String yardsText(List<long[]> yards, DistrictPlan.Input frame)` | The runs' yards in the frame's plots, for a line: "[x,y 8x7]". |

### THE COPIES: JERUS'S CITY ON A SQUARE OF DISTRICTS, AT HIS DENSITY (lines 2698-2816)

| line | len | member | says |
|---:|---:|---|---|
| 2703 | 62 | **type** `static final class Squares` | His city x 1 and its copies, each on the design's square city at the dry place, its mines no more than its sites. |
| 2715 | 49 | `Squares()` _(in MapCheck.Squares)_ |  |
| 2767 | 3 | `static int x1Side(BuildingVisual.Type[] types)` | The side of Jerus's city x 1 on the design's square city, in districts: its ground over a district's (Squares). |
| 2772 | 3 | `static long[] dryPlace(World w)` | The world cell nearest the founding site's whose middle 3 x 3 districts are DRY_PLACE dry at a sample a tile: {x, y, rings out}. |
| 2784 | 18 | `static long[] dryPlace(World w, int side)` | ...and (0.7.99, batch W1), with `side` over 0, whose square for Jerus's city x 1 - `side` districts round its middle, as CityMap.square() lays it - holds an iron field's centre, so his mines have sites and his railway... |
| 2804 | 12 | `static boolean holdsIron(World w, long x, long y, int side)` | Whether the square of `side` districts CityMap.square() lays round plot (x, y) holds an iron field's centre. |

### 3. THE PAINTER DRAWS THE PLAN (0.7.88, batch RD2; the mockup's rules on (lines 2817-2880)

| line | len | member | says |
|---:|---:|---|---|
| 2822 | 58 | `static void painterRules(CityMap m)` |  |

### 6. THE CITY DRAWN AS WHAT IT HAS (0.7.64, batch L2) (lines 2881-2986)

| line | len | member | says |
|---:|---:|---|---|
| 2899 | 87 | `static void drawnAsItIs(CityMap dense)` | Jerus, 2026-10-07: "the generation should only put what the city has, not more not less". |

### 8. THE NETWORK ON THE DRAWN RASTER (0.7.88, batch RD2; N3's on its (lines 2987-3412)

| line | len | member | says |
|---:|---:|---|---|
| 3004 | 312 | **type** `static final class Raster` | A map painted whole over a box of tiles and put together: what each plot is, every building's box - what holds the network's rules. |
| 3017 | 57 | `Raster(CityMap map, long tx0, long ty0, int tw, int th)` _(in MapCheck.Raster)_ |  |
| 3076 | 4 | `static Raster of(CityMap map)` _(in MapCheck.Raster)_ | Every tile of every district of a map, and a tile round them. |
| 3082 | 1 | `boolean isRoad(int g)` _(in MapCheck.Raster)_ | A road plot: a street (a track among them, the track's level crossings among them), or a highway's. |
| 3085 | 1 | `boolean isStreet(int g)` _(in MapCheck.Raster)_ | A street plot: a road plot with a street's role - not a highway's own. |
| 3088 | 1 | `boolean isHighway(int g)` _(in MapCheck.Raster)_ | A highway's own plot (its runs'). |
| 3090 | 1 | `boolean isRail(int g)` _(in MapCheck.Raster)_ |  |
| 3093 | 1 | `boolean isRunHighway(int g)` _(in MapCheck.Raster)_ | A plot of the city's highways as its runs lay it (0.7.89), under a building or not - the railway bridging it among them. |
| 3096 | 1 | `boolean isRunRail(int g)` _(in MapCheck.Raster)_ | ...of its railway's track, under a yard or a mine too, or bridging a highway. |
| 3098 | 5 | `long streetPlots()` _(in MapCheck.Raster)_ |  |
| 3104 | 8 | `int beside(int g, java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 3114 | 19 | `int[] pieces(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ | The pieces plots of a kind make, four ways joined: each plot's piece number (0 for none), [0] = how many pieces. |
| 3144 | 37 | `int[] network()` _(in MapCheck.Raster)_ | The road's pieces and those the city's own ground joins to the largest: {pieces, plots of the largest, pieces the ground joins to it} - from the largest piece, four ways over owned ground that is not the sea or a mine... |
| 3182 | 5 | `long count(java.util.function.IntPredicate is)` _(in MapCheck.Raster)_ |  |
| 3189 | 5 | `long alone()` _(in MapCheck.Raster)_ | Road plots with no road beside them, four ways: a stray. |
| 3196 | 9 | `boolean crossing(int g)` _(in MapCheck.Raster)_ | A + junction of streets: a street plot with street on its four sides and none on its four corners (a square's middle, street on its corners too, is not one; nor a street beneath a highway, which passes under it). |
| 3207 | 16 | `long[] crossings(int apart)` _(in MapCheck.Raster)_ | {crossings, pairs of them nearer than `apart` either way}. |
| 3225 | 18 | `int[] reach(int most)` _(in MapCheck.Raster)_ | Each plot's distance from a street, across corners (a street 0), to `most` + 1. |
| 3245 | 5 | `static int boxReach(int[] dist, int w, int[] b)` _(in MapCheck.Raster)_ | A box's nearest plot's distance. |
| 3252 | 3 | `CityMap.District districtAt(int g)` _(in MapCheck.Raster)_ | The district a plot of the box is in. |
| 3262 | 24 | `int[] asPlanned()` _(in MapCheck.Raster)_ | Every plot the plans of the districts in the box surface (Drawn's codes, its frame's east column and south row among them) against the painted plot: {plots, those not painted as that street - its kind and its width}. |
| 3288 | 27 | `int[] bridges()` _(in MapCheck.Raster)_ | The bridges: {runs longer than their line may cross - an arterial's or a boulevard's ARTERIAL_BRIDGE, a street's STREET_BRIDGE, a highway's MAX_BRIDGE - roads on the sea, bridge plots}. |
| 3323 | 3 | `static void networkOn(CityMap m, long[] model, String what)` | The network's rules on a map's drawn raster (0.7.88): one network but where the ground parts it, no road alone, + junctions apart, every building within reach, the streets' surface the model's road kind by kind, every... |
| 3332 | 47 | `static void networkOn(CityMap m, long[] model, String what, boolean crosses)` | ...and whether the city must have + junctions for the + rule to bite (`crosses`): not the played city since CL3, whose few cells each opened while its road was short - a cell's squares are decided as it opens, never a... |
| 3380 | 32 | `static void network(Squares sq, Game city, String[] saves) throws Exception` |  |

### 8b. THE INTERCHANGES' GROUND AND THE ACCESS ROADS (batch FR7, slice 4; (lines 3413-3580)

| line | len | member | says |
|---:|---:|---|---|
| 3423 | 5 | `static int depotType(BuildingVisual.Type[] types)` | The type id of the Truck Depot, the one building of the category HAULAGE. |
| 3433 | 3 | `static CityMap accessCity(Squares sq, boolean highways)` | The yard city (his city x 1 where its square holds iron, with two Rail Terminals and a freight line) with highways added to cross and Truck Depots to place; with `highways` false his own highways taken out too: none, ... |
| 3438 | 33 | `static CityMap accessCity(Squares sq, boolean highways, long[] extra)` | ...with `extra` counts added (batch FR7 slice 5: spurs and bulk works for the sidings' deal): a track type's to the railway, any other's to the buildings. |
| 3476 | 20 | `static int[] accessFigures(CityMap m)` | What a map's plans gave its works' access roads: {roads, on a ramp, on an arterial, already on one, gates not beside their box, ends neither a ramp's street nor an arterial, the map's works (yards and sea terminals) g... |
| 3497 | 83 | `static void interchangesAndAccess(Squares sq)` |  |

### 8c. THE SPURS DEALT TO THE PLANS' SIDINGS (batch FR7, slice 5; spec- (lines 3581-4123)

| line | len | member | says |
|---:|---:|---|---|
| 3591 | 74 | `static void sidingsDealt(Squares sq)` | The spurs the runs' sidings left, as the map deals them: each dealt district is one the main line crosses with bulk works of its own, no more than it holds, and the plans lay at most their share, drawn on the raster; ... |
| 3667 | 23 | `static Game loadCopy(Path src) throws Exception` | A save's autosave copied into a temporary folder and loaded (its map drawn from its sidecar or canonically): saves named on the command line, never written. |
| 3699 | 3 | `static CityMap riverCity(Squares sq, int kind)` | The river fixture (0.7.77, batch N5): Jerus's city x 1 on the design's square city sited at the default world's founding river - its point nearest the founding site - with its streets all of one kind: his Paved Roads ... |
| 3704 | 30 | `static CityMap riverCity(BuildingVisual.Type[] types, long seed, long[] counts, int kind)` | The river fixture from the types, the world's seed and his city x 1's counts: PlanCheck 17 plans its districts (FR8). |
| 3744 | 44 | `static void riverCrossed(Squares sq)` | A river parts gravel streets no more than paved ones (0.7.77; Jerus, 2026-10-08: "gravel road bridge rivers sure"): the river fixture all gravel against its paved twin - the road pieces with a plot within a tile of th... |
| 3797 | 32 | `static void highways(Raster net, String what)` | The city's highways on the drawn raster (0.7.89, batch RD3; spec 2.7): drawn plot for plot as its runs lay them, and with what they could not lay the model's Elevated Highways; one network; no building plot touching a... |
| 3843 | 47 | `static int marksReachTheView(CityMap m, long tx0, long ty0, int tw, int th, String what, boolean bands)` | The runs' marks reach the picture a screen draws (0.7.98, batch O14): a screen rasters a tile from the view's kept copy of its inputs (MapTiles.painted(), paintedInput()), and until then that copy held none of the run... |
| 3905 | 47 | `static void corridors(Squares sq)` | The highways on corridors on the game's own ground (0.7.89; spec 2.7): each stage of spec 2.7's table on a square city of highways alone at the dry place, laid at once - its plots each laid or counted, one network, st... |
| 3954 | 55 | **type** `static final class Box` | The runs' plots over a map's districts and a plot round them, put together: one network, its junctions. |
| 3959 | 5 | `Box(long x0, long y0, int w, int h, CityRuns r)` _(in MapCheck.Box)_ |  |
| 3965 | 1 | `static Box of(CityMap m)` _(in MapCheck.Box)_ |  |
| 3967 | 4 | `static Box of(CityMap m, CityRuns r)` _(in MapCheck.Box)_ |  |
| 3972 | 1 | `boolean hw(int g)` _(in MapCheck.Box)_ |  |
| 3975 | 18 | `int pieces()` _(in MapCheck.Box)_ | The highways' pieces, four ways joined. |
| 3995 | 13 | `long junctions()` _(in MapCheck.Box)_ | Highway plots with highways on three or four sides: where they meet. |
| 4011 | 17 | `static int bendsStopped(CityMap m, CityRuns.Net n)` | The bends of a net that are not where its way was stopped: a stretch off its arm's heading begun where the way on along the heading before ran LOOK plots clear, or one back onto it where the way home did not run TURN_... |
| 4030 | 15 | `static void rail(Raster net, String what)` | The railway on the drawn raster (0.7.89; spec 2.8): one network, every plot of the model's track drawn - under its yards and mines too - or counted as what the runs could not lay (since FR7 slice 3 the surplus no trai... |
| 4050 | 25 | `static CityMap yardCity(Squares sq)` | The x 1 copy with rail yards and a freight line added (0.7.72's fixture, restored in 0.7.89): its buildings, then its mines on their sites, then its railway - so the railway starts at its mine nearest the founding sit... |
| 4083 | 40 | `static void yardsNearMines(CityMap m, Raster net)` | Every rail yard on its track, in the cell along it nearest a mine that had room (spec 2.8: "laid as runs from the rail terminals, which sit in estate cells nearest the mines"; 0.7.72's rule, restored): the track runs ... |

### 4. THE SIDECAR (lines 4124-4335)

| line | len | member | says |
|---:|---:|---|---|
| 4128 | 177 | `static void sidecar(Game g, Path root, Squares sq) throws Exception` |  |
| 4317 | 7 | `static boolean sameBoxes(DistrictPlan a, DistrictPlan b)` | Whether two plans place the same boxes: the same count of buildings, each one's type and its box {x, y, w, h}, in the same order, and the same buildings left without a place (by type). |
| 4326 | 9 | `static boolean logsAddUp(CityMap m)` | Whether every district's log sums, type by type, to its counts: what a log is for, and what a sidecar read back is held to (CL3). |

### 5. THE COST FOLLOWS THE SCREEN (lines 4336-4502)

| line | len | member | says |
|---:|---:|---|---|
| 4369 | 9 | `static boolean cpuTimed()` |  |
| 4380 | 10 | `static double cpuStepMs()` | The least step the main thread's CPU clock takes, in ms: five times, spinning until it moves (200 ms of the wall clock at most each); infinite if it never does. |
| 4392 | 3 | `static long monthClock()` | The clock a month's bound reads, in ns: the main thread's CPU, or the wall clock where CPU_TIMED is false. |
| 4396 | 106 | `static void cost(Squares sq)` |  |

### 7. THE VIEW'S PURE HALF (0.7.61, batch J4) (lines 4503-5192)

| line | len | member | says |
|---:|---:|---|---|
| 4513 | 8 | `static void viewHalf(Game city, Squares sq, Path root) throws Exception` |  |
| 4523 | 72 | `static void frames()` | MapFrame: the transforms, a notch of the wheel at the pointer, the clamps, the levels and what a screen asks for. |
| 4597 | 121 | `static void picks(Game g)` | LandMap: what a click and a hover pick on the played city agrees with the model's own grid (CityLand.holdingOf()) and GridOffers' sides and places, and the outlines with the picks (0.7.67; the lanes' owns() and sideLa... |
| 4731 | 228 | `static void overlay(Game g)` | The overlay on the block grid (0.7.69, batch M5; spec-grid 2.4), what MapView draws by: the city's edge as maximal runs with its ground on their right, drawn crisp - whole pixels, a pixel thick, just inside the ground... |
| 4964 | 3 | `static char dir(double x0, double y0, double x1, double y1)` | A run's way: E, S, W or N. |
| 4969 | 3 | `static boolean touch(double[] a, double[] b)` | Whether two boxes of pixels {x, y, w, h} share a pixel or touch at a corner or an edge. |
| 4974 | 13 | `static int inQuad(double[] q, double x, double y)` | Whether a point is inside a convex quadrilateral (corners in order): 1 inside, 0 within a hair of an edge, -1 outside. |
| 4989 | 77 | `static void draft(Path root) throws Exception` | Game.mapDraft()/adoptMap(): the map drawn on another thread is the map drawn here, a month between is caught up, and a draft for other land is not kept. |
| 5071 | 120 | `static void tiles(Squares sq)` | MapTiles: a tile's stamp holds while nothing moves, one more building restamps only the tiles it changes, the view's per-tile path follows the screen, and every cache fits the budget. |

### 9. AT SEA AND ON THE SHORE (0.7.97, batch O13) (lines 5193-5272)

| line | len | member | says |
|---:|---:|---|---|
| 5204 | 3 | `static CityMap coastal(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts)` | A city owning `side` districts a side about (sx, sy), every plot of them, its map drawn canonically on the ground measured, its shore laid by the harbour rule (MF1), as a city's map is drawn. |
| 5214 | 3 | `static CityMap coastBeforeMF1(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts)` | ...with the shore laid as it was before MF1 (the harbour rule off): the coast whose fixture's premise is where a work stands or how many stand - SL1's cost of the shore (hundreds of works on one coast, which two harbo... |
| 5218 | 9 | `static CityMap coastal(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts, boolean harbour)` |  |
| 5228 | 12 | `private static CityMap coastalDrawn(long seed, long sx, long sy, BuildingVisual.Type[] types, int side, long[] counts)` |  |
| 5260 | 12 | `static long[] shoreCounts(BuildingVisual.Type[] types)` | The section's town: Jerus's mix at a tenth, one of each of the refinery's units, two of each terminal and one of each tank farm. |

### the coast: one or two harbours, not a shore of piers (MF1, 2026-10-28) (lines 5273-5602)

| line | len | member | says |
|---:|---:|---|---|
| 5294 | 13 | `static CityMap harborOrdered(BuildingVisual.Type[] types, long seed, List<List<CityShore.Work>> laidByRound, boolean rule)` | The coast's shore works asked for round by round: every shore type's model count one more each round, from none, the rest of the town as the section's. |
| 5309 | 3 | `static List<CityShore.Harbour> harboursOf(CityShore shore)` | The harbours of a shore by the rule's reach, whichever rule laid it: CityShore.harbours() on one landmass. |
| 5313 | 115 | `static void theHarbour(BuildingVisual.Type[] types, long seed)` |  |
| 5430 | 5 | `static CityShore.Harbour harbourOf(List<CityShore.Harbour> harbours, CityShore.Work k)` | The harbour (of `harbours`) a work stands in: the first whose first work is within the reach of its shore plot, or null. |
| 5441 | 53 | `static void harbourExactness(BuildingVisual.Type[] types, long seed)` | A search that starts where the last found room is the search from the harbour's first tile (shoreSpotFull(): the harbour's tiles from the nearest its first work, every time), for each General Cargo Terminal laid one a... |
| 5499 | 9 | `static int worksOnWalks(CityMap m)` | How many of a map's spine arm walks (CityMap.worldWalks()) a work's box, verge or quay lies on. |
| 5516 | 24 | `static void harbourKeepsOffTheArms()` | MF1, the gate arm (found in review): a harbour is not opened, and no work laid, where it would stand on the walk the world line reads from a spine arm's end (CityMap.worldWalks(): WORLD_GAP plots along the arm's headi... |
| 5542 | 5 | `static long[] noShore(BuildingVisual.Type[] types, long[] orders)` | The orders with every shore type's count taken out. |
| 5557 | 44 | `static void harbourMonthCost(long seed)` | SL1's timing line on the harbours (MF1: the game's own path is timed, not only the shore as it was): a month that lays one more terminal costs no more than SHORE_GROWTH times as long with HARBOUR_MANY standing as with... |

### the shore's cost follows what a month changes (SL1, 2026-10-28) (lines 5603-6107)

| line | len | member | says |
|---:|---:|---|---|
| 5627 | 7 | `static boolean blocksByLoop(CityShore shore, long x, long y)` | Whether any work of `shore` blocks world plot (x, y), by the loop over every work that CityShore.blocks() was before SL1: the reference. |
| 5636 | 4 | `static CityShore.Work atByLoop(CityShore shore, long x, long y)` | The first work whose box holds world plot (x, y), by the loop over every work: the reference. |
| 5642 | 6 | `static void askBoth(CityShore shore, long x, long y, long[] counts)` | Whether blocks() and at() answer as the loops do at world plot (x, y); `counts` takes {asked, blocked, differ}. |
| 5650 | 15 | `static long[] blocksAgainstLoops(CityShore shore, int margin)` | blocks() and at() against the loops at every plot of each work's box, its verge and its quay and `margin` plots about them: {plots asked, of them blocked, answers that differ}. |
| 5671 | 22 | `static long[] layOneAtATime(CityMap m, int t, long[] counts, long from, long to)` | Terminals of type t laid one at a time, the model's count n from `from` to `to` (counts[t] is set to it): before each month the spot the search from where the last found room finds and the spot the search from the nea... |
| 5695 | 5 | `static double medianAfter(double[] ms, int skip)` | The median of the figures after the first `skip`. |
| 5709 | 132 | `static void shoreFollowsChange(BuildingVisual.Type[] types, long seed, CityMap coast)` | SL1 (2026-10-28): what the shore costs follows what a month changes and not the terminals standing. |
| 5842 | 265 | `static void atSea(BuildingVisual.Type[] types, long seed)` |  |

### SL1 (2026-10-28): the terminals share a lane, found once, drawn once (lines 6108-6666)

| line | len | member | says |
|---:|---:|---|---|
| 6111 | 4 | `static boolean sameRoute(BoatSchedule.Route a, BoatSchedule.Route b)` | Whether two routes are the same, point for point (their berth, offing, pivot and spread too). |
| 6117 | 5 | `static boolean sameRoutes(List<BoatSchedule.Route> a, List<BoatSchedule.Route> b)` | Whether two lists of routes are the same, route by route. |
| 6128 | 17 | `static boolean joinedOnALane(List<BoatSchedule.Route> all, int r, int j)` | Whether route r, joined at its point j (SeaRoutes.Found.joinAt()), goes on from there as the lane of one before it in `all`: that route ends in the same points, point for point, after the join point; the join point li... |
| 6147 | 6 | `static boolean onLeg(double ax, double ay, double bx, double by, double px, double py)` | Whether (px, py) lies on the segment (ax, ay) to (bx, by), to a millionth of a plot. |
| 6155 | 13 | `static boolean everyLegDrawn(List<BoatSchedule.Route> routes, SeaLanes lanes)` | Whether every leg of every route is drawn: some leg of the lanes ends where it does and holds its start. |
| 6170 | 9 | `static boolean sharesNoPoint(List<BoatSchedule.Route> all, int k)` | Whether no point of route k is a point of another route's: a lane of its own. |
| 6185 | 17 | `static void sharedLanesOnTheCoast(World w, CityMap m, BuildingVisual.Type[] types)` | Section 9's lanes shared (SL1): on the coast's own terminals, fewer routes are searched than there are routes and the rest joined to a lane, each going on from its join point as a lane's own points, point for point; a... |
| 6209 | 56 | `static void lanesOnAMask()` | Section 9's lanes on a mask of the sea (SL1): a straight coast, a headland wider than JOIN_M, a far shore, one berth inland. |
| 6272 | 73 | `static void routesFoundOnce(World w, CityMap m, BuildingVisual.Type[] types, int gct, long[] counts)` | Section 9's routes found once, on the coast's map (SL1): a terminal laid is routed alone onto the routes kept (one new, none searched again, the kept the same objects), a tank farm laid changes no berth and finds noth... |
| 6351 | 62 | `static void routesAt10B(CityMap m)` | Section 9's routes at 10 billion (SL1): PROTO_LANES berths along a mask coast of BoatProto's span, each route found from scratch within ROUTES_ALL_MS, a month's new terminals routed onto the kept ones within ROUTES_MO... |
| 6430 | 236 | `static void trains(Squares sq, Game city)` | Section 9's trains (0.8.2, batch FR1; spec-freight 4.4, D16): on the yard city (yardCity()), the routes run from the world line along the track into each yard and onto its sidings; the trains are its railed tonnes ove... |

### FX5, slice T: real-length trains, onto clear sidings, at a pace that reads (lines 6667-7291)

| line | len | member | says |
|---:|---:|---|---|
| 6670 | 3 | `static double holdsOf(TrainSchedule.Route rt)` | The plots of train a route's siding holds: Route.holds(). |
| 6675 | 3 | `static double ladderOf(TrainSchedule.Route rt)` | How far along a route its siding's ladder begins, in plots: Route.enters(). |
| 6680 | 3 | `static double legSecondsOf(double length)` | Seconds a leg of a route `length` plots long takes: its length over the pace, no less than the shortest leg and no more than the longest. |
| 6685 | 3 | `static double standSecondsOf(TrainSchedule.Kind k)` | Seconds a train of a kind stands on its siding: a base and so much a wagon. |
| 6693 | 6 | `static List<TrainSchedule.Route> handYard(double first, double second)` | A hand-made yard (FX5 T): three sidings off a ladder of 3 plots from a main line 64 plots long, the first holding `first` plots of train and the other two `second`. |
| 6701 | 3 | `static TrainSchedule.Route handRoute(int siding, double length)` | A hand-made route `length` plots long: the world line 64 plots, a ladder of 3, and the siding the rest, all on one line. |
| 6706 | 4 | `static double frontAt(TrainSchedule.Call c, TrainSchedule.Route rt, double t)` | How far along its route a call's train has its front at t, in plots; NaN while it is not on the line. |
| 6712 | 6 | `static boolean onSiding(TrainSchedule.Call c, TrainSchedule.Route rt, double t)` | Whether a call's train is on its route's siding at t: from its front reaching the ladder in to its rear leaving it out. |
| 6720 | 9 | `static int mostOnASiding(TrainSchedule month, List<TrainSchedule.Route> routes)` | The most trains on one siding at once, over a month sampled 2,000 times. |
| 6731 | 9 | `static boolean rearOnSiding(TrainSchedule.Call c, TrainSchedule.Route rt)` | Whether a standing train's rear vehicle lies on the route's last leg, its siding: the rear locomotive's middle between the leg's start and its end. |
| 6748 | 89 | `static void trainsByLength(List<TrainSchedule.Route> routes, TrainSchedule mix)` | Section 9's trains at their real length, on sidings that hold them, at a pace that reads (FX5, slice T; Jerus, 2026-10-20: "real trains are really long", "the rail depots would have to be redesigned to accomdoate such... |
| 6839 | 5 | `static int standingOn(CityMap m, int yi)` | The sidings of yard yi of the map that wagons stand on in the picture the map has: the tiles' count, scaled for the yard's sidings, never all of them. |
| 6850 | 36 | `static void trainsAndTheWagons(CityMap m, int kinds)` | Section 9's routes and the wagons standing in the yards (FX5, slice T; FX3's star 6: a train stood over the yard's own wagons): with none, half and all of the fleet's wagon sets standing, no route is laid to a siding ... |
| 6905 | 84 | `static void trainsOnTheCurves(CityMap m, List<TrainSchedule.Route> routes, TrainSchedule mix)` | Section 9's trains on the curves (FX5, 0.8.18; Jerus, 2026-10-20: "do trains make 90 degree turns?"): on the yard city's routes, at every sampled moment of the month's trains and at fronts all along every route, (a) n... |
| 6995 | 6 | `static boolean onLadder(TrainSchedule.Route rt, double fromEnd)` | Whether a point `fromEnd` plots before a route's end lies on its yard's ladder (FX5): the S between the fan's apex and the siding's start, the siding being the route's last leg - within a locomotive's length either side. |
| 7003 | 3 | `static double headingApart(double a, double b)` | How far apart two headings are, in degrees, 0 to 180. |
| 7008 | 25 | `static boolean onDrawnTrack(CityMap m, java.util.Map<Long, int[]> tiles, double x, double y, int px, int w)` | Whether the pixel under plot point (x, y), or one of its eight neighbours, is the railway's as the tile at 8 px a plot draws it (its tile rastered once into `tiles`). |
| 7047 | 55 | `static void trucksAtTenBillion(long cx, long cy)` | Section 9's trucks at 10 billion (FR9): CityMap.PLANS_KEPT districts' trips, the most the map draws, each district's schedule at its 1/PLANS_KEPT of the prototype's tonnes scaled by people (PROTO_TONNES over PROTO_PEO... |
| 7110 | 17 | `static List<TrainSchedule.Route> syntheticYards(long cx, long cy, int side)` | A city of yards about (cx, cy) for the frame at 10 billion: side x side yards on a grid four cells apart, each with its YARD_TRACKS routes in from one gateway WORLD_PLOTS west - along the row to its column, down the c... |
| 7144 | 12 | `static List<BoatSchedule.Route> protoLanes(long cx, long cy)` | BoatProto's 20,000 lanes about (cx, cy): berths at hashed places along its coast's span at 10B - sqrt(10B / 5,000) x 33 plots, its "city's span in plots (rough)" - every other one liquid bulk's, the rest dry bulk's. |
| 7158 | 5 | `static int tileGround(World w, long x, long y)` | The world's ground at a plot as the painter reads it: its tile's (World.tileTerrain()), what the shore's search and the routes read. |
| 7165 | 19 | `static boolean connected(java.util.Set<Integer> cells)` | Whether a set of cells (ci + cj x CELLS_A_SIDE) is one piece, eight ways: a corner touching counts, as DistrictPlan.campusNext() opens them. |
| 7186 | 5 | `static String worksWords(CityShore shore, BuildingVisual.Type[] types)` | The shore's works by type, in words. |
| 7193 | 17 | `static Ports tradeAtSea(double liquid, double dryBulk, double general, double boxes)` | A month billed with these tonnes of each kind shipped out, all of them by sea: each on the first good of its kind that is not crude. |
| 7218 | 73 | `static void oilAtSea()` | The game's own platform, its wells and its pipe on the map (WellCheck's sea town): Game.mapAtSea() puts the jacket in the middle of its slotted sites and its wells on them, the pipe from the jacket to the founding sit... |

### 10. THE MAP ON EARTH (0.7.119, batch E5) (lines 7292-7707)

| line | len | member | says |
|---:|---:|---|---|
| 7318 | 107 | `static void onEarth(BuildingVisual.Type[] types) throws Exception` | Section 10. |
| 7433 | 73 | `static void farWindow(String name, World w, EarthData data)` | One window drawn as a far node at WINDOW_STRIDE and at CLOSE_STRIDES: closer, the region but for its rivers stroked wider; at the window's stride every river stroked at least FAR_RIVER_HALF_PX a side, every small lake... |
| 7513 | 60 | **type** `static final class Water` | The rivers and small lakes under a far node, as the check reads them from the data itself: each run the node would stroke (its median width at least FAR_RIVER_SHARE of a pixel), its segments projected exactly, and eac... |
| 7519 | 29 | `Water(EarthTerrain e, EarthData data, long x0, long y0, int stride)` _(in MapCheck.Water)_ |  |
| 7550 | 12 | `boolean inStroke(int i, int j, boolean least)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies within a river's stroke: its least half-width alone (`least`), or its own. |
| 7564 | 8 | `boolean inCore(int i, int j)` _(in MapCheck.Water)_ | Whether pixel (i, j)'s centre lies in a small lake's sure part: within its radius x the root of 1 - BLOB_FRAY / 2, which its blob holds whatever its noise. |
| 7581 | 36 | `static void seam(String where, World w, int stride, EarthData data, boolean water)` | A node holding the 180th meridian (its middle on the spot, which is on it) and its neighbours moved SEAM_SHIFT_PX east and west: on the half each shares with it, pixel for pixel the same; each side of the line's land ... |
| 7619 | 88 | `static void fullFile(int widest, EarthTerrainCheck.Spot rot) throws Exception` | Section 10 on the full file, when it is named (-Dcbs.earth): the longest segment, every stride's cost, the whole world, the small lakes' area and Chukotka's seam. |

### 11. THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md 8.2) (lines 7708-8194)

| line | len | member | says |
|---:|---:|---|---|
| 7724 | 4 | `static void handCash(Game city)` | Tops the played city's cash up to PICTURE_CASH_MONTHS months of its GDP when it holds less: the money a young city the playtest left broke needs for the advisor's purchases to happen. |
| 7735 | 360 | `static void pictures(Game city, Squares sq)` |  |
| 8097 | 5 | `static Map<CityMap.District, DistrictPicture> picturesOf(CityMap m)` | A district's pictures, every one asked for: each district of the map to its picture (null where it has none). |
| 8104 | 5 | `static long[] countsOf(CityMap.District d, int types)` | A district's counts by type id. |
| 8111 | 5 | `static String codeName(int c)` | A picture's code's name, for the prints. |
| 8125 | 26 | `static byte[] pictureSource(CityMap m, CityMap.District d, int[] boxPlots)` | The plots a district's picture is drawn from, read here from its plan as the painter reads it (CityMap.Drawn: its streets, its boxes by tile) and its own plan's inputs (the runs over its frame, the mines on their site... |
| 8152 | 5 | `private static void fill(byte[] src, int x0, int y0, int w, int h, byte code, int[] counted)` |  |
| 8165 | 17 | `static boolean[] runPixelsOf(CityMap m, long x0, long y0, long plots)` | Which pixels of a far node at (x0, y0), `plots` a pixel, hold a plot of the city's highways or track: every plot of every stretch walked, each 45-degree step's corner with it (CityRuns.onStretch()); and the 2 x 2 dot ... |
| 8184 | 4 | `private static void markDot(boolean[] out, double x, double y, long x0, long y0, long plots)` | The 2 x 2 pixels from the one holding (x, y). |
| 8189 | 5 | `private static void markPixel(boolean[] out, long x, long y, long x0, long y0, long plots)` |  |

### 12. THE MINIMAP (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3) (lines 8195-8439)

| line | len | member | says |
|---:|---:|---|---|
| 8205 | 196 | `static void miniMap(Game city, Squares sq) throws Exception` |  |
| 8407 | 32 | `static MiniMapFrame.Box builtUpByLeaves(CityMap m)` | The built-up ground found another way, for the check: every leaf of the grid cut to each district with buildings it lies in, their bounds; a built district with none owned its whole square. |

### 13. THE GLOBE ON SCREEN (0.7.135, batch E9) (lines 8440-8954)

| line | len | member | says |
|---:|---:|---|---|
| 8453 | 461 | `static void globeOnScreen(Squares sq) throws Exception` |  |
| 8916 | 4 | `static byte groundClass(String words)` | The class a legend's ground words name. |
| 8922 | 15 | `static double foundingParting(double lat, double lon, double km)` | How far the founding globe's two drawings part at a scale, in pixels at worst over its canvas's corners and edges: the globe through GlobeFrame against the ground drawn round the view's centre (Lambert's plane there, ... |
| 8939 | 15 | `static CityMap earthX1(Squares sq) throws Exception` | Jerus's city x 1 on the real Earth at Rotterdam, EarthCheck's fixture (section 10's window): null when the fixture is not beside the classes. |

### 14. BUYING FROM THE MAP (0.7.137, batch M6; docs/studies/spec-earth.md 3) (lines 8955-9639)

| line | len | member | says |
|---:|---:|---|---|
| 8974 | 362 | `static void buyingFromTheMap(Game g) throws Exception` |  |
| 9344 | 10 | `static boolean buysQuietly(Game g, LandParcel p)` | The city buys land as a player would: cash made up to the price when short, then Game.buyLandParcel (LandManager.buyParcel). |
| 9359 | 10 | `static List<LandParcel> listedAgain(CityLand land, LandMarket market, LandParcel was, List<Integer> notIds)` | The listed offers, not those ids, whose rectangle shares ground the city does not own with a rectangle that was listed: the place listed again over that ground under a new id (its rectangle is sized by the city as it ... |
| 9375 | 131 | `static void picksLast(Game g) throws Exception` | MS1 (Jerus, 2026-10-26: "on monthly update it doesnt reset ... |
| 9518 | 37 | `static void farOnThePlane(Game g)` | MS1 (Jerus, 2026-10-26: "you cant zoom in on areas of the map far away from the city, and if zoomed in there is a wall that you can cross"): the wall and the stop were the globe's (GlobeCheck 2b); a generated world's ... |
| 9565 | 74 | `static void plainDragSelects(Game g)` | MS1 (Jerus, 2026-10-26: "i should be able to not only click and shift click, but also drag and select"): what a left press on the expanded map starts, a box or a pan, is the model's (LandMap.pressBoxes(), which MapVie... |

### 14e. BLOCKS PICKED (BL1) (lines 9640-10285)

| line | len | member | says |
|---:|---:|---|---|
| 9661 | 4 | `static boolean blockFree(LandGrid g, int level, long bx, long by)` | Whether block (bx, by) of `level` is on the world and not wholly the city's. |
| 9667 | 9 | `static java.util.Set<Long> blocksMetBy(CityLand land, LandParcel o, int level)` | The blocks of `level` an offer's free ground meets: every block holding a plot the offer holds, the plots counted one by one. |
| 9678 | 7 | `static boolean completesAnOffer(CityLand land, LandMarket market, int level, java.util.Set<Long> blocks)` | Whether some standing offer's whole free ground lies in these blocks: such a set is, to the model, that offer picked. |
| 9691 | 24 | `static long[] chainFrom(CityLand land, LandMarket market, int level, java.util.Set<Long> avoid)` | A free block on the city's frontier and a direction outward from it along which the next two blocks are free and not on the frontier - three in a row, the middle one joined only through the first - none within a block... |
| 9717 | 21 | `static boolean allJoined(CityLand land, int level, java.util.Set<Long> blocks, List<LandParcel> offers)` | The blocks a Picks holds that join the city, worked out another way: every block of the set, reached from one on the city's frontier or beside a picked offer's free ground by steps through the set. |
| 9740 | 4 | `static boolean edgeNeighbour(double[] c, long x0, long y0, long b)` | Whether a free cell {x0, y0, x1, y1} lies across an edge of the block at (x0, y0) of side b, sharing some of its length: beside the block, not on it. |
| 9746 | 32 | `static java.util.Set<Long> floodExpect(CityLand land, int level, long[] box, java.util.Set<Long> picked)` | The blocks of a box of plots {x0, y0, x1, y1} that join the city, worked out block by block: free blocks of the box on the frontier or beside a block already picked, and the free blocks of the box reached from them by... |
| 9780 | 5 | `static List<LandParcel> offersIn(LandMarket market, List<Integer> ids)` | The offers behind these picks' ids (the positive ones), as the office lists them. |
| 9787 | 5 | `static java.util.Set<Long> everyPicked(CityLand land, LandMarket market, int level, LandMap.Picks picks, List<Integer> ids)` | The blocks a Picks holds, and those of the offers it holds, as one set. |
| 9794 | 8 | `static Map<Long, Integer> coverage(List<long[]> rects, int level)` | The blocks the rectangles cover, counting each time: block -> how many rectangles hold it. |
| 9808 | 21 | `static boolean edgeOf(double[][] runs, java.util.function.BiPredicate<Long, Long> in, long plots)` | Whether an outline's runs are the edge of the ground `in` says: they close (every run's end is another's start, as often), the plot on the right of every unit of every run is the ground and the plot on its left is not... |
| 9831 | 3 | `static boolean inBlocks(java.util.Set<Long> blocks, int level, long x, long y)` | Whether plot (x, y) is in a block of the set at `level`. |
| 9840 | 15 | `static LandParcel ownBlocks(CityLand land, LandMarket market, int level, int nth)` | The nth standing offer (in the office's order) of the city's own level that holds two or more blocks of free ground, each block's free ground wholly inside the offer's rectangle - one that picking blocks makes the off... |
| 9857 | 5 | `static java.util.Set<Long> blocksOf(long[]...at)` | Blocks as a set, from {bx, by} pairs. |
| 9868 | 9 | `static LandParcel ownBlocksKeepingLevel(CityLand land, LandMarket market, int level, long more)` | The first offer ownBlocks() finds (in the office's order) whose purchase, and `more` plots bought after it, leave the city's block level `level` where it is - LandGrid.levelFor() of the plots the city would own then i... |
| 9883 | 10 | `static long buyTheLargestOffer(Game g)` | The city buys, as it buys any ground (Game.buyLandParcel(), handed the price), the standing offer with the most free ground that it can buy: the ground that takes a city standing just under the plots of its next block... |
| 9894 | 391 | `static void blocksPicked(Game g) throws Exception` |  |

### 14f. THE EXACT GROUND ON THE SCREEN (BL1, slice D) (lines 10286-10599)

| line | len | member | says |
|---:|---:|---|---|
| 10304 | 4 | `static long[] plotIn(int level, long key)` | A plot near the middle of a block of `level`: {x, y}. |
| 10310 | 4 | `static long unownedIn(LandGrid grid, int level, long key)` | The plots of a block the city does not own. |
| 10316 | 8 | `static long[] ownedAndAll(CityLand land, List<double[]> cells)` | The cells of a fill: how many plots of them the city owns, and how many they hold in all. |
| 10325 | 234 | `static void groundOnScreen(Game g) throws Exception` |  |
| 10561 | 3 | `static String owner(int owner)` | A pick's owner in words. |
| 10566 | 4 | `static boolean clears(LandMap.Picks picks, Game g, int id)` | Whether a plain click on an offer (only) clears a note that stood. |
| 10572 | 10 | `static java.util.Set<Integer> boxByPlots(CityLand land, List<LandParcel> offers, double x0, double y0, double x1, double y1)` | The offers a box of plots meets, plot by plot: every plot under (x0, y0)-(x1, y1) in some part, not the city's, inside an offer's rectangle. |
| 10584 | 10 | `static boolean inOrder(List<Integer> ids, List<LandParcel> listing)` | Whether these ids stand in the listing's order. |
| 10596 | 3 | `static boolean sameFigure(double a, double b)` | Two money or area figures the same to a billionth of the larger. |

### 15. THE MATERIALS DRAWN AS THEMSELVES (0.8.0, batch I14; (lines 10600-10966)

| line | len | member | says |
|---:|---:|---|---|
| 10613 | 3 | `static boolean materialsOf(BuildingsTemplate t)` | Whether a template is one of the nine industries'. |
| 10618 | 3 | `static long materialsCount(BuildingVisual.Type t)` | The fixture's count of a materials type: one of a works on more than one site, two of anything else - so each slot holds two kinds and the larger is laid first. |
| 10622 | 264 | `static void materials(long seed)` |  |
| 10888 | 3 | `static long countZone(List<CityMap.Tinted> tint, int z)` | How many districts a zone's tint takes. |
| 10893 | 7 | `static java.util.Map<Long, Integer> sitesUnder(CityMap m)` | Each site under a works on a map, by its key, and the works' type. |
| 10902 | 12 | `static int lookSign(BuildingVisual.Type t)` | The colour a look shows on a box of it rastered (TileRaster's THE MATERIALS AS THEMSELVES): a pit's benches, a shaft's headframe, a quarry's stone, the leach plant's pond, the wash plant's conveyor, a camp's logs, pla... |
| 10922 | 22 | `static long[] materialsPlace(World w, int side, int[] need)` | The world cell nearest the founding site's whose square of `side` districts (as CityMap.square() lays it round the cell's middle) is DRY_PLACE dry in its middle 3 x 3 districts and holds fields centred in it with at l... |
| 10949 | 17 | `static int[] slotSites(World w, long x, long y, int side)` | The sites of each slot of the fields centred in the square of `side` districts CityMap.square() lays round plot (x, y). |

### 16. THE AVENUES DRAWN (batch CL4; the study spec-city-layout 5.2) (lines 10967-11360)

| line | len | member | says |
|---:|---:|---|---|
| 11004 | 7 | `static CityMap avenueCity(Squares sq)` | The avenues' city, built from the copies' constants (Squares): twice alike gives a twin. |
| 11013 | 52 | **type** `static final class AvTiles` | One map's tiles, kept as the section samples them: the rastered image at PX and what the painter was given and made of each plot. |
| 11023 | 1 | `AvTiles(CityMap m)` _(in MapCheck.AvTiles)_ |  |
| 11025 | 1 | `private static long key(long tx, long ty)` _(in MapCheck.AvTiles)_ |  |
| 11028 | 16 | `private void load(long tx, long ty)` _(in MapCheck.AvTiles)_ | The tile (tx, ty) as the painter makes it, loaded on first use. |
| 11045 | 5 | `private int at(java.util.Map<Long, byte[]> what, long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 11052 | 1 | `int code(long x, long y)` _(in MapCheck.AvTiles)_ | The plan's street code, the avenue's mark, the painter's use, the ground and the flags of world plot (x, y). |
| 11053 | 1 | `int mark(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 11054 | 1 | `int use(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 11055 | 1 | `int ground(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 11056 | 1 | `int flag(long x, long y)` _(in MapCheck.AvTiles)_ |  |
| 11059 | 5 | `int pixel(long X, long Y)` _(in MapCheck.AvTiles)_ | The pixel of the near view's raster at world pixel (X, Y). |
| 11067 | 4 | `static long[] avenueOrigin(CityMap m)` | The lattice node the avenues run out from, nearest the founding site (DistrictPlan's avSetup), in world plots: {x, y}. |
| 11073 | 14 | `static boolean avenueOpen(AvTiles t, long ox, long oy, int a, int k)` | Whether the avenue is open at step k of arm a, as the near view draws it: the staircase's plots S_k, C_(k+1) and S_(k+1) laid as a surfaced street on the ground, and the outer plot O_(k+1) between them owned dry groun... |
| 11089 | 3 | `static boolean streetColour(int c)` | Whether a pixel is a surfaced street's: the paved colour, the gravel's, or its grain. |
| 11094 | 12 | `static int handMark(int ox, int oy, int arms, int len, int x, int y)` | Where a hand-built tile's avenues stand: the avenue plots of the arms `arms` (bit a) from origin (ox, oy) for `len` steps - a plot two arms share the first arm's mark, as DistrictPlan lays it - and 0 for none. |
| 11108 | 8 | `static int[] handAvenue(int ox, int oy, int arms, int len, JunctionCheck.Streets extra, boolean withOrigin)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: the avenue plots of the arms `arms` from the origin laid as arterial streets with their marks, and the streets `extra` besides; the origin given ... |
| 11117 | 243 | `static void avenuesDrawn(Squares sq)` |  |

### 17. THE CURVES DRAWN (batch CL4, slice D; the study spec-city-layout 5.2) (lines 11361-11963)

| line | len | member | says |
|---:|---:|---|---|
| 11401 | 14 | **type** `record CvFound(CityMap.District d, CityMap.DrawnCurve c, long ox, long oy)` | A curve found in a Drawn, with where its district's frame lies in the world. |
| 11403 | 5 | `double[][] line()` _(in MapCheck.CvFound)_ | Its line in the world's plots: {xs, ys}. |
| 11410 | 4 | `long[] plot(int k)` _(in MapCheck.CvFound)_ | Plot k of its list in the world's plots: {x, y}. |
| 11417 | 65 | **type** `static final class CvTiles` | One map's tiles as the curves section samples them at `px` a plot: the rastered image, and what the painter was given and made of each plot. |
| 11429 | 1 | `CvTiles(CityMap m, int px)` _(in MapCheck.CvTiles)_ |  |
| 11431 | 1 | `CvTiles(CityMap m, int px, boolean bare)` _(in MapCheck.CvTiles)_ |  |
| 11433 | 1 | `private static long key(long tx, long ty)` _(in MapCheck.CvTiles)_ |  |
| 11435 | 20 | `private void load(long tx, long ty)` _(in MapCheck.CvTiles)_ |  |
| 11456 | 5 | `private int at(java.util.Map<Long, byte[]> what, long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 11463 | 1 | `int code(long x, long y)` _(in MapCheck.CvTiles)_ | The plan's street code, the painter's use, the curve mark and the flags (1 owned, 2 bridge, 4 beneath) of world plot (x, y). |
| 11464 | 1 | `int use(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 11465 | 1 | `int mark(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 11466 | 1 | `int flag(long x, long y)` _(in MapCheck.CvTiles)_ |  |
| 11469 | 5 | `int type(long x, long y)` _(in MapCheck.CvTiles)_ | The type id of the building on world plot (x, y), or -1 for none. |
| 11476 | 5 | `int pixel(long X, long Y)` _(in MapCheck.CvTiles)_ | The pixel of the raster at world pixel (X, Y). |
| 11484 | 6 | `static boolean cvStreetColour(int c)` | Whether a pixel is a surfaced street's, owned or dimmed as ground the city has not bought is: the paved colour, the gravel's or its grain. |
| 11492 | 6 | **type** `static final class CvAudit` | What an audit of one curve's drawn line found. |
| 11500 | 5 | `static java.util.Set<Long> cvPlotSet(CvFound f)` | The own plots of a curve (the first `own` of its list) as a set of world plot keys. |
| 11515 | 89 | `static CvAudit cvAudit(CvTiles t, CvFound f, java.util.Set<Integer> fills)` | One curve's line audited on the raster t (THE CURVES DRAWN): samples every pixel along it, from its first point to its last. |
| 11606 | 4 | `static double cvTurn(double x0, double y0, double x1, double y1, double x2, double y2)` | The signed turn, in radians, from the chord (x0, y0)-(x1, y1) to (x1, y1)-(x2, y2). |
| 11612 | 7 | `static boolean cvCrossesTile(double[][] line)` | Whether the line passes from one tile to another. |
| 11621 | 8 | `static java.util.List<long[]> cvTiles(CvFound f)` | The tiles about a curve's plots: its bounding box in tiles, a tile more on each side, as {tx, ty} pairs. |
| 11631 | 19 | `static int[] handCrescent(JunctionCheck.Streets extra)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: a crescent in the cell whose interior's north-west plot is (0, 1), bulging toward +x, its staircase laid as street plots with its curve, and the ... |
| 11652 | 19 | `static int[] handShore(boolean bridge)` | A hand-built tile of owned bare ground at 8 px a plot, JunctionCheck's: a street along a cut, a path of street plots along row 10 from column 2 to 20 with its curve (StreetCurves' line, its band beside it) - with `bri... |
| 11672 | 291 | `static void curvesDrawn(Squares sq)` |  |

### 18. A MONTH REDRAWS ONLY WHAT MOVED (batch UP3, docs/queue/UP3.md) (lines 11964-13250)

| line | len | member | says |
|---:|---:|---|---|
| 12031 | 53 | **type** `static final class FarScreen` | One far screen of section 18: a MapFrame fitted on the city's frame and zoomed out to FAR (or on to a level that draws no pictures), its nodes, their ground, and what each was last drawn from and as - the way ui/MapVi... |
| 12049 | 29 | `FarScreen(String name, CityMap m, MiniMapFrame.Box frame, boolean wide)` _(in MapCheck.FarScreen)_ |  |
| 12080 | 3 | `boolean overlaps(int i, long x0, long y0, long x1, long y1)` _(in MapCheck.FarScreen)_ | Whether node i lies over any of the box x0 to x1 and y0 to y1 (in plots). |
| 12086 | 36 | **type** `static final class RedrawMonth` | What one month did to one fixture's screens, as Redraw.month() found it; times in ms. |
| 12119 | 1 | `double nowSum()` _(in MapCheck.RedrawMonth)_ |  |
| 12120 | 1 | `double todaySum()` _(in MapCheck.RedrawMonth)_ |  |
| 12124 | 449 | **type** `static final class Redraw` | A fixture of section 18: a map grown a month at a time, its screens drawn two ways (today's whole on `tr`, now's kept on `tk`) and held equal. |
| 12156 | 21 | `Redraw(String name, CityMap m, long[] counts, int divisor, boolean withFar, boolean wide, boolean withL0, boolean checkLinks)` _(in MapCheck.Redraw)_ |  |
| 12178 | 3 | `boolean runType(int t)` _(in MapCheck.Redraw)_ |  |
| 12183 | 5 | `int biggest()` _(in MapCheck.Redraw)_ | The built type with the most buildings that is no run's and no mine: the one a single-district month raises. |
| 12189 | 5 | `static long sum(MapTiles.KeptNode[] keeps, int what)` _(in MapCheck.Redraw)_ |  |
| 12196 | 150 | `RedrawMonth month(int kind, int number, boolean timed)` _(in MapCheck.Redraw)_ | Plays one month and draws it; `timed` months are the ones whose figures are read. |
| 12351 | 14 | `long[] planFolds(long tx, long ty)` _(in MapCheck.Redraw)_ | What the plans part of tile (tx, ty)'s key folds, apart: the sum of its districts' versions and of which plan each keeps (the Drawn object: a plan drawn again to the same streets is another; a harness's, to say which ... |
| 12373 | 61 | `void l0Pass(RedrawMonth r)` _(in MapCheck.Redraw)_ | The L0 screen drawn as ui/MapView.paint() draws it after a month (batch UP5): the key of every tile in view whose plans are drawn (ready(), MapTiles.tileKey()), then input() - the read of the tile's inputs and its sta... |
| 12441 | 61 | `long[] drawStamp(RedrawMonth r, int round, java.util.Set<CityMap.District> wanted)` _(in MapCheck.Redraw)_ | The stamp and the preview drawn two ways, equal or not noted: returns the ns now's took and today's. |
| 12504 | 11 | `long todayPass(FarScreen fs, int round, boolean warm)` _(in MapCheck.Redraw)_ | Today's draw of a far screen: every node in the month's round, the partial ones after; ns. |
| 12523 | 33 | `long[] drawFar(FarScreen fs, int s, RedrawMonth r, int round, java.util.Set<CityMap.District> wanted)` _(in MapCheck.Redraw)_ | A far screen drawn two ways, the ns of each: now's as ui/MapView.paintNodes() draws it (nodeKey() for every node in the month's round and for the partial ones after; drawn when the key moved, or it was drawn partial a... |
| 12558 | 14 | `void verify(FarScreen fs)` _(in MapCheck.Redraw)_ | Every node of the screen drawn from the key it has now holds the pixels a fresh nodePixels() gives it: those held through the month are the ones that count. |
| 12579 | 22 | `static java.util.Map<String, long[]> runsDrawn(CityMap m)` | What the runs draw on a far node as the map stands (batch UP5): each stretch {x, y, heading, steps} of the highways (once they reach a street) and of the railway, each yard's dot and each quay work's, by a name that s... |
| 12603 | 4 | `static void runBoxesInto(java.util.Map<String, long[]> before, java.util.Map<String, long[]> after, java.util.List<long[]> out)` | The boxes of what the runs drew on a far node in one of two states and not the other (runsDrawn() before and after a month), into `out`. |
| 12614 | 41 | `static void runNodes(Redraw w, int s)` | Check on the months that laid a run at 5B (batch UP5; far screen `s` of `w`, the one wide enough to draw no pictures): the far nodes whose key moved in each, against those over a district whose version moved (and its ... |
| 12661 | 72 | `static void runsIndexHolds(Redraw[] fx)` | Checks on the runs index (batch UP5): the nodes it enters a stretch at are the nodes stretchHash() - stroke()'s clip - says it is drawn on, for random stretches of all eight headings at four scales (starts on and besi... |
| 12734 | 5 | `static int differ(int[] a, int[] b)` |  |
| 12740 | 6 | `static double median18(java.util.List<Double> l)` |  |
| 12747 | 5 | `static double worst18(java.util.List<Double> l)` |  |
| 12753 | 6 | `static int medianInt(java.util.List<Integer> l)` |  |
| 12761 | 16 | `static CityMap hisCopy(Squares sq, double times)` | Jerus's city at `times` on the design's square city at the dry place, as Squares builds its copies. |
| 12783 | 16 | `static long[] purchaseBlock(CityMap m, CityLand land)` | The block of PURCHASE_SIDE plots to buy: wholly inside the box of the city's built-up ground (so the stamp's frame is the one it was, however the district's owned ground grows), none of it owned, and the most ground o... |
| 12800 | 247 | `static void redrawsOnlyWhatMoved(Game city, Squares sq)` |  |
| 13049 | 42 | `static void oneDistrict(Redraw w, RedrawMonth one, int s, boolean picturesOn)` | Check 3 on one month that raised one built type by one: the far nodes whose key moved against the 3 x 3 districts round the one district that moved, and against every node in view. |
| 13098 | 27 | `static double[] quietChain(CityMap m, int at, boolean all)` | The chain asked for the picture of every built district after links were let go and nothing else moved (batch UP5, check 6b), as the far view asks: the link of district `at` alone, or (`all`) of every district from it... |
| 13127 | 18 | `static long drawnHash(CityMap.Drawn g)` | A plan as the painter reads it, hashed all through (batch UP5, check 6b): what two plans of one district are compared by. |
| 13152 | 34 | `static int[] chainEqualsWhole(Redraw w)` | The links and plans a fixture's months left kept equal a chain planned whole (batch UP5, check 6b): each district's link (stamp, the margins' hash, the carry on, its picture's stamp) and plan, as kept, are noted; the ... |
| 13188 | 62 | `static void report18(Redraw w)` | The figures section 18 prints for a fixture. |

### THE RELIEF ON THE MAP (ET1 slice D; docs/studies/spec-earth2-terrain.md (lines 13251-13549)

| line | len | member | says |
|---:|---:|---|---|
| 13263 | 4 | `static int reliefColour(int c, int r)` | The colour a plot of cover `c` and relief `r` is painted, said again from TileRaster's constants (D4, D5, D19): flat ground, water and ice (which have no relief, and white wins over a slope) in their cover's colour; o... |
| 13269 | 4 | `static World.Key fixtureKey(double[] at) throws Exception` | A map's relief on EarthCheck's fixture's key at (lat, lon): the data in use (EarthTerrain.use()) and the key of the Earth world there. |
| 13275 | 144 | `static void reliefOnMap(BuildingVisual.Type[] types) throws Exception` | Section 10: the plane's plots and the far node over Medellin painted by their relief; a generated world painted as it was. |
| 13421 | 34 | `static void pictureRelief(BuildingVisual.Type[] types) throws Exception` | Section 11: a built district's far node over Medellin's hills: the greys on the city's own ground, undimmed, and nowhere else but the plan's own colours. |
| 13457 | 92 | `static void reliefWordsAndLegend(BuildingVisual.Type[] types) throws Exception` | Section 13: the words and the legend by relief; the globe's hover and its picture's codes. |

### 19. THE BORDERS ON THE NEAR VIEW (batch WN2, slice A; docs/studies/spec-nations.md 6.1) (lines 13550-13637)

| line | len | member | says |
|---:|---:|---|---|
| 13558 | 4 | `static double[] nearBox()` | A box of the plane's plots, {x0, y0, x1, y1}, NEAR_BOX_KM a side round the plane's spot (the world's middle plot). |
| 13564 | 11 | `static boolean sampledMeets(WorldData.Border b, EarthProj plane, double[] box)` | Whether a line has a segment meeting a box of the plane's plots, found the slow way: each segment sampled at a thousand points. |
| 13576 | 61 | `static void nationsNearView() throws Exception` |  |

