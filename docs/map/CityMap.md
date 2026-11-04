# CityMap.java - 10,404 lines · 521 methods · 73 constants · model

`ham/citybuildersim/CityMap.java` - generated 2026-11-04 by CodeMap; line numbers are as of that run.

> The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, each district's street plan kept for the painter with what it cannot hold carried to the next, the city's highways and railway laid on corridors across it, one drawn building for each the model has, summed up a pyramid, each built district's picture drawn with its plan for the far view (0.7.128), and written beside the save as a sidecar - what the tile painter paints the city from.
> 
> WHY THIS EXISTS (0.7.60, batch J3; the project's spec-land.md 2.5). The
> model counts buildings by type and nothing else: a city of ten billion has
> 279 million of them and no places. Jerus's mockup placed every building on
> a 30 m plot as it grew; stored that way a 10B city is gigabytes. So the map
> keeps counts by type in districts of 256 x 256 plots - 29,929 of them at
> 10B, 10.8 MB - and paints a tile from its district's counts when the screen
> needs it (TilePainter), so its cost follows the screen, never the
> population. NOTHING IN THE MODEL READS IT.
> 
> A DISTRICT holds its free plots - owned and buildable (World.buildable(),
> since ET2: dry, flat or foothill), a resource's sites among
> them (a mine or well takes its own site's; the painter builds on a site
> last) - counted plot by plot on each of its tiles since 0.7.64 (32 x 32
> samples before), recounted in every district a purchase touches; the
> ground its buildings use (a type's land / 9,687.5 sq ft a plot) and the
> whole plots they are drawn on (BuildingVisual.cells(), roads by their own
> plots); its owned sites of each SITED resource's types of field (iron's
> and oil's until 0.8.0: SLOTS); and its count of every type.
> 
> EACH MONTH, ONCE CONSTRUCTION COMPLETES (reconcile(), star 12): each type's
> change is placed - new buildings into the first district with room for
> their whole plots,
> districts tried nearest the founding site first, but farms, utilities,
> mines and wells, the railway and the car plants farthest first; removals in
> reverse - and nothing placed ever moves. Running totals and a cursor each
> way keep a month's work to the districts it changes: measured 0.76 ms a
> month at 10B in the design. Mines and wells are the exception the design
> names: they stand on their resource's sites, the first in acquisition order
> (the centre, then each purchase) not worked out, so they go to the
> districts holding those sites - since 0.8.0 every works on a SITED
> resource, by the type of its field and on as many sites as it takes
> (THE WORKS ON THEIR SITES BY TYPE).
> 
> CANONICALLY (canonical()), for a save with no sidecar, a lost one or one
> that does not match: each type in proportion in every district, inner
> first, the remainders by largest remainder - exact. The picture shifts
> once.
> 
> THE DRAWN PLANS (0.7.88, batch RD2; the deal of 0.7.64 to 0.7.87 before
> them, which dealt a district's buildings and road plots onto its tiles
> for each to grow its own roads): each district is painted from its
> street plan (DistrictPlan) - its streets with the model's road as their
> surface, a box for every building - kept for the last PLANS_KEPT districts
> asked and made away from the screen's thread; what a district's plan
> cannot hold goes to the next district in the map's order, and what none
> can is packed at the city's edge, beside a street since MF1 (R7). A city is drawn
> as what it has, not more, not less (Jerus, 2026-10-07). Since 0.7.89 the
> order is cut into bands of CHAIN_BAND districts (THE CHAIN'S BANDS), so a
> screen at the edge of a city of ten billion plans one band, not the city;
> a city of CHAIN_BAND districts or fewer is one band, drawn as before.
> Since 0.8.14 (batch CL3) a district's plan places its buildings in the
> order its LOG has them (THE SIDECAR, below; DistrictPlan's THE ORDER OF
> ARRIVAL), since 0.8.16 (batch FX6) on its ground as the city bought it
> (DistrictPlan's THE GROUND BOUGHT), so what is placed never moves; and a
> district is full at PLAN_FILL of its owned plots.
> 
> THE RUNS (0.7.89, batch RD3; spec-roads-and-ports.md 2.7, 2.8): the
> city's Elevated Highways and railway are laid city-wide, month by month,
> ... (69 more lines in the source)

**Uses:** [World](World.md) (369), [DistrictPlan](DistrictPlan.md) (127), [TilePainter](TilePainter.md) (96), [CityShore](CityShore.md) (96), [BuildingVisual](BuildingVisual.md) (86), [CityRuns](CityRuns.md) (71), [Resource](Resource.md) (47), [TrackCurves](TrackCurves.md) (27), [CityLand](CityLand.md) (23), [Deposit](Deposit.md) (19), [TrainSchedule](TrainSchedule.md) (19), [TruckSchedule](TruckSchedule.md) (17), [SeaRoutes](SeaRoutes.md) (14), [LandGrid](LandGrid.md) (13), [DistrictPicture](DistrictPicture.md) (12), [TileRaster](TileRaster.md) (12), [Landmasses](Landmasses.md) (11), [BoatSchedule](BoatSchedule.md) (9), [InterchangeGround](InterchangeGround.md) (6), [SeaLanes](SeaLanes.md) (5), [LandParcel](LandParcel.md) (3), [Gateways](Gateways.md) (2), [BuildingType](BuildingType.md) (1), [Pieces](Pieces.md) (1)

**Used by (30):** [BridgeCheck](BridgeCheck.md), [BridgeMapCheck](BridgeMapCheck.md), [CityRuns](CityRuns.md), [CrossingCheck](CrossingCheck.md), [DistrictPicture](DistrictPicture.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [IslandNetCheck](IslandNetCheck.md), [LandMap](LandMap.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [MiniMapFrame](MiniMapFrame.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [ReplayCheck](ReplayCheck.md), [RunRenders](RunRenders.md), [SaveFileCheck](SaveFileCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [TilePainter](TilePainter.md), [TruckRouteCheck](TruckRouteCheck.md), [TruckSchedule](TruckSchedule.md)

## Sections

| line | section |
|---:|---|
| 155 | THE GEOMETRY |
| 228 | A DISTRICT |
| 476 | THE STATE |
| 581 | BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE |
| 771 | MEASURING THE GROUND: OWNED DRY PLOTS |
| 980 | THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN |
| 1028 | PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION |
| 1208 | · ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3) |
| 1785 | MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) |
| 2337 | THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch |
| 4373 | THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md |
| 4478 | THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch |
| 4709 | THE TRAINS' ROUTES (0.8.2, batch FR1; spec-freight 4.4, D16) |
| 5148 | · the plans' sidings' trains (MF1, 2026-10-28) |
| 5193 | THE TRUCKS' ROUTES (0.8.8, batch FR5; spec-freight 4.7, D21) |
| 5392 | · the trips (batch FR9) |
| 6009 | THE CITY'S GROUND IN PIECES (batch CL5, slice B) |
| 6784 | · the Rail Spurs the runs' sidings left: dealt to the plans' works (batch FR7 slice 5, D14) |
| 6924 | THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13; |
| 7634 | · THE HARBOUR (MF1, 2026-10-28) |
| 7915 | THE OIL AT SEA (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the |
| 7960 | THE ISLAND RULE ON THE MAP (ET5, 2026-10-25) |
| 8202 | LONG BRIDGES ON THE MAP (ET6, 2026-10-26; docs/studies/spec-earth2-terrain.md 6.5 and 6.6) |
| 8312 | THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1) |
| 8470 | A TILE'S INPUTS FOR THE PAINTER |
| 9108 | THE RUNS DATED (batch FX7, 0.8.24) |
| 9349 | · THE RESERVES DATED (batch UP5, FX7's star 2) |
| 9646 | THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's |
| 9805 | ZONES BY DISTRICT (0.8.0, batch I14; docs/studies/spec-zoning.md 4: |
| 9876 | THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 |
| 10008 | THE SIDECAR (spec-land 2.5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 160 | `CityMap.DISTRICT` | `World.DISTRICT` | Plots on a district's side: World.DISTRICT, 256 (7.68 km). |
| 163 | `CityMap.TILES_A_SIDE` | `DISTRICT / World.TILE` | Tiles on a district's side: 8. |
| 166 | `CityMap.TILES` | `TILES_A_SIDE * TILES_A_SIDE` | Tiles in a district: 64. |
| 169 | `CityMap.HALF_SQ_FT_PER_PLOT` | `Math.round(2 * BuildingVisual.SQ_FT_PER_PLOT)` | Half square feet in a plot: 9,687.5 sq ft twice, so a district's room and use are whole numbers and a month's change is exact however it is added up. |
| 172 | `CityMap.MIN_ROOM` | `1` | A district with less room than this many whole plots is passed by the cursors: one, the least a building is drawn on (0.7.64; half a plot of ground before). |
| 175 | `CityMap.PLAN_FILL` | `0.68` | The share of a district's owned plots its buildings' boxes may take: 0.68 (CL3), a full plan's boxes over its ground on Jerus's densest districts (the CL1 study, section 5.3) - so a district is full where its plan is,... |
| 178 | `CityMap.NO_LOG` | `new int [ 0 ]` | A district's empty log: the one array every district without records shares. |
| 181 | `CityMap.TOUCHED_MAX` | `8` | How many types a district notes as changed between two logs (District.touch()) before the log compares them all: 8. |
| 184 | `CityMap.SITED` | `{ Resource.IRON, Resource.OIL, Resource.COAL, Resource.STONE, Resource.COPPER }` | The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5); since 0.8.0 (batch I14) coal, stone and copper after them, every resource a works on dry ground stands on. |
| 187 | `CityMap.SLOTS` |  | The site slots (0.8.0, batch I14): each SITED resource's types of field in Resource.typesOf()'s order - iron's hematite and magnetite, oil's one, coal's thermal and coking, stone's limestone and hard rock, copper's su... |
| 190 | `CityMap.SLOT_RESOURCE` |  | ...each slot's resource... |
| 193 | `CityMap.SLOT_TYPE` |  | ...and its type of field. |
| 205 | `CityMap.OLD_SITED` | `2` | The sites a district the sidecar wrote before FORMAT 7: iron's and oil's, SITED's until 0.8.0. |
| 208 | `CityMap.SITE_LISTS_KEPT` | `64` | How many districts' lists of sites are kept: 64. |
| 211 | `CityMap.COARSE_ABOVE` | `1024` | Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). |
| 214 | `CityMap.COARSE_STRIDE` | `32` | The coarse look's stride, in plots: 32, a sample a tile. |
| 217 | `CityMap.MAGIC` | `0x434D4150` | The sidecar's magic: "CMAP". |
| 220 | `CityMap.FORMAT` | `15` | The sidecar's format: 15 since UP5, each district's journal of the runs holding the interchanges' reserves too, as a kind of its own (THE RESERVES DATED: a record 0 to FIXED_RESERVE for a plot a reserve came to, the o... |
| 223 | `CityMap.OLDEST_READ` | `4` | The oldest sidecar read: FORMAT 4 (0.7.72 to 0.7.87) - its districts are placed as 0.7.88 places them; it has no runs, which are laid from its counts as it is read. |
| 226 | `CityMap.STAMP_AT` | `4 + 4 + 8 + 4` | Where the stamp sits in the sidecar's raw bytes: after the magic, the format, the seed and the month. |
| 834 | `CityMap.NONE` | `0, SOME = 1, ALL = 2` |  |
| 1490 | `CityMap.CAMPUS_REACH` | `64` | The most districts a works group's units may stand across, in the map's order, and still have a campus a new one goes to first: 64 (0.8.0) - past it, a city whose works of a group stand in every district (a city of bi... |
| 1956 | `CityMap.DISTRICT_ORDER` | `Comparator.comparingDouble((District d) -> d.order).thenComparingInt(d -> d.d...` | Nearest the founding site first, then north to south, west to east. |
| 2391 | `CityMap.PLANS_KEPT` | `64` | How many districts' plans are kept, packed for the painter (Drawn): 64, as the deals and road tiles were (spec 2.9) - each tile's street rows once a pattern and four bytes a building (RD1's plan was 140 KB as District... |
| 2394 | `CityMap.PACKED_BIT` | `1<<20` | A packed box's flag (Drawn.boxes): drawn dimmed, R7's packing at the city's edge (since MF1 beside a street). |
| 2397 | `CityMap.SIDING_END_BIT` | `1<<24` | A siding plot of a Drawn (Drawn.siding) whose bit this is: the siding's first, beside the works' box, where its loading shed stands (batch FR7 slice 5). |
| 2400 | `CityMap.AVENUE_MARK_SHIFT` | `17` | Where a Drawn avenue plot's mark starts (Drawn.avenue): above the frame's plot, which is under 2^17 (a frame has 66,049 of them). |
| 2403 | `CityMap.CURVE_BAND_BIT` | `1<<20` | A band plot of a DrawnCurve (DrawnCurve.plots) whose bit this is: a plot the curve's band reaches that is not its own. |
| 2460 | `CityMap.BOX_TYPE_SHIFT` | `21` | Where a Drawn box's type id starts: above the box's 20 bits and PACKED_BIT, eleven bits for ids under 2,048 (buildings.json's ids are under 128). |
| 2883 | `CityMap.M_WEST` | `0, M_NORTH = 1, M_EAST = 2, M_SOUTH = 3, M_PARTED = 4` | A plan frame's edges' street codes (Link.margins): its west column (x 0) and north row (y 0), its east column (x 256, the next district's first) and south row (y 256), each 257 long; and M_PARTED, a byte along them wi... |
| 2904 | `CityMap.AROUND` | `{ { 0, - 1 }, { 1, 0 }, { 0, 1 }, { - 1, 0 }, { - 1, - 1 }, { 1, - 1 }, { 1, ...` | The eight districts about one, by offset {dx, dy}: north, east, south, west, then north-west, north-east, south-east, south-west - the order of a frame's seam parts (DistrictPlan.Input.surfaces). |
| 2907 | `CityMap.CORNER_DIRS` | `{ { 3, 0, 4 }, { 1, 0, 5 }, { 1, 2, 6 }, { 3, 2, 7 } }` | Each corner seam part's districts sharing it besides its own, as AROUND's directions: north-west, north-east, south-east, south-west. |
| 3063 | `CityMap.CHAIN_BAND` | `128` | THE CHAIN'S BANDS (0.7.89, batch RD3). |
| 3066 | `CityMap.BANDS_KEPT` | `16` | The bands whose links are kept, the last used: 16 (2,048 districts' links, about 4 MB) - a band let go is planned again when a screen asks for it. |
| 4008 | `CityMap.LAID_MOST` | `TilePainter.JUNCTION_APART` | The longest street laid to a packed building, in plots: 8, one lattice width (MF1). |
| 4011 | `CityMap.AGAINST` | `0, REACHED = 1, LAID = 2, FIELD = 3, WAYS = 4` | The ways a packed building may stand, in the order they are tried: against a street (within a plot of one, across corners), within REACH of one, with a street laid to it, or - none of these held it, its district havin... |
| 4014 | `CityMap.INTERIOR_MOST` | `World.TILE - 1` | A tile's interior holds a box no larger than this a side: 31 plots, off its arterial lines. |
| 4017 | `CityMap.LAID_CODE` | `(byte) DistrictPlan.TRACK` | The code a laid street is drawn with: an unmade street (TRACK), no width. |
| 4390 | `CityMap.PICTURES_KEPT` | `128` | How many districts' pictures are kept, the last used: 128 (star MM1-2) - a far view of up to 128 built districts (Jerus's city has 93) in DistrictPicture.BYTES each, 0.14 MB, within the view's 48 MB with the rest (Map... |
| 4588 | `CityMap.WORLD_GAP` | `2 * CityRuns.LOOK` | How far ahead of a track's end the city's edge may lie for its line to the world to start there, in plots: 12, twice CityRuns.LOOK - an arm waits LOOK short of the edge, a 45-degree arm sooner (FX2). |
| 4591 | `CityMap.WORLD_REACH` | `4 * DISTRICT` | How far the world line is drawn at most, in plots: 1,024 (30.7 km), four districts; it stops sooner at the first plot that is not dry ground (FX2). |
| 4594 | `CityMap.WORLD_STEP` | `0.25` | How finely a way out is read, in plots: a quarter, as MapCheck reads the trains' routes (FX2). |
| 5220 | `CityMap.TRUCK_ROUTE_LEAST` | `4` | The least a highway route runs along the highways, in plots, past the world line's own: shorter is the arm the line leaves from, where the trucks have no road to drive. |
| 5223 | `CityMap.TRUCK_ROUTES_MOST` | `40` | The most highway routes kept, 40: one to each ramp, the arms' order, a city of many arms is cut here (a route is a lane of trucks, and the legend would not read more). |
| 5226 | `CityMap.TRUCK_ACCESS_FADE` | `3` | How far an access road's trucks fade in off the street grid, in plots: 3. |
| 5404 | `CityMap.TRIP_REACH` | `DISTRICT` | The plots about a district's frame, on every side, its trips look for a ramp over: DISTRICT (256), the three districts across about it. |
| 5407 | `CityMap.TRIP_STREET_COST` | `3` | What a truck pays to cross a street plot that is not an arterial's, boulevard's or access road's (those cost 1) in the trips' street search: 3, so a truck takes the arterials unless the other way is under a third as l... |
| 5410 | `CityMap.TRIP_BOX` | `DistrictPlan.FRAME + 2 * TRIP_REACH` | The side of the box a district's street search runs in, in plots: its frame grown by TRIP_REACH on every side. |
| 5416 | `CityMap.NO_PLOT` | `Long.MIN_VALUE` |  |
| 5848 | `CityMap.RUN_LINES_KEPT` | `4096` | Terrain lines (a tile's row or column, World.lineTerrain()) the runs' ground keeps: 4,096, about 460 KB - a corridor's way ahead and back, at any size. |
| 5851 | `CityMap.RUN_TILES_KEPT` | `256` | ...whole tiles, for a 45-degree way: 256. |
| 6048 | `CityMap.CHUNK_CACHE` | `256` | How many chunks of the pieces' tables are kept at hand (MapPieces.cacheChunk): 256 (a city of billions keeps about 250: its 4 MB of tables in chunks of 16 KB). |
| 6051 | `CityMap.PIECE_CHUNK_SHIFT` | `6` | Tiles a side of a chunk of the pieces' tables (CL5): 2^6 = 64, a table of 4,096 per chunk, and a district (8 tiles) is never split by one. |
| 6053 | `CityMap.PIECE_CHUNK_MASK` | `(1L<<PIECE_CHUNK_SHIFT) - 1` |  |
| 6056 | `CityMap.PIECE_SAMPLE_AT` | `8, PIECE_SAMPLE_EVERY = 16` | Where in a tile its four samples lie (CL5; IslandProbe's tile rule): the first 8 plots in and each of the others 16 further, the centres of its four quarters. |
| 6059 | `CityMap.PIECE_HOME_REACH` | `8` | How many tiles from the founding site's tile the home piece is looked for, ring by ring, when its own tile is not land (CL5). |
| 6539 | `CityMap.WAYS_WORKS_DISTRICTS` | `1000` | Up to this many districts the map weighs its mined sites each month for the railway's sidings and further yards: 1,000 (Jerus's city has 93). |
| 7000 | `CityMap.SHORT_ANY` | `0x7fff_fffeL` | The landmass half of the key of a search that asked for no landmass: not any landmass's id (an int). |
| 7205 | `CityMap.ANY_LANDMASS` | `Integer.MIN_VALUE` | ET5: no landmass asked for: the search runs as it always did, over the ground the runs can use. |
| 7307 | `CityMap.SHUT_REACH` | `SeaRoutes.OPEN_M / World.PLOT_M / SeaRoutes.CELL` | The flood's reach in cells (SeaRoutes.opensToSea()): a water whose cells all lie within it of one another opens to nothing, whichever of them a flood starts from. |
| 8024 | `CityMap.MASK_TILES_KEPT` | `1<<16` | The tiles whose masks are kept: 65,536 (a tile's mask is a reference, and a boolean array only for a tile holding masked ground). |
| 8155 | `CityMap.AtSea.NONE` | `new AtSea(List.of(), List.of())` | None. |
| 8249 | `CityMap.BridgeRun.ROAD` | `0` | A street-sized deck that belongs to no net... |
| 8251 | `CityMap.BridgeRun.HIGHWAY` | `1` | ...a highway's, which joins the highways... |
| 8253 | `CityMap.BridgeRun.RAIL` | `2` | ...and the railway's. |
| 8275 | `CityMap.ROAD_BRIDGE_STREET` | `(byte)(DistrictPlan.PAVED \|(1<<TilePainter.S_WIDTH_SHIFT))` | A road bridge's plot as a plan's street code: paved, half a plot wide (a street's surface, so that its deck shows beside it), a cell's street, no bridge bit - the painter reads the water under it. |
| 8549 | `CityMap.NO_PLAN` | `0x4E4F504C414EL` | tileKey()'s fixed mark for a district whose plan is not kept drawn. |
| 9272 | `CityMap.KINDS_KEPT` | `16` | How many kinds arrays are kept: 16, eight districts' pair. |
| 9374 | `CityMap.SITE_MARGIN` | `32` | How far a mine's site (a few plots across, centred in its district) can reach beyond its district's box: 32 plots. |
| 9377 | `CityMap.RESERVE_PAD` | `InterchangeGround.REACH + 1` | How far a reserve's box can be from a plot that moves it: the longest reach of a box from its junction plot, and one for the highway beside the junction. |
| 9881 | `CityMap.NODE_WIDTH` | `BuildingVisual.CLASSES + 2` | How many numbers a node sums: the ten classes, the ground used (in half square feet, exact) and the owned buildable plots. |
| 9884 | `CityMap.NODE_USED` | `BuildingVisual.CLASSES` | Where a node keeps the ground used, in half square feet. |
| 9887 | `CityMap.NODE_OWNED` | `BuildingVisual.CLASSES + 1` | ...and its owned buildable plots. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 235 | `public final int dx, dy` | Its column and row, in districts from the founding site's. |
| 236 | `final long key` |  |
| 237 | `final double order` |  |
| 239 | `int owned` | Its free plots: owned and buildable (since ET2: dry, flat or foothill), counted plot by plot (0.7.64; from 32 x 32 samples before) - or, for a harness's square city, given. |
| 241 | `long usedHalf` | The ground its buildings use, in half square feet: each type's count x its land, kept exactly. |
| 243 | `long usedCells` | The whole plots its buildings and roads are drawn on (BuildingVisual.cells()), a mine or well on its site taking its own: what its room is kept in (0.7.64). |
| 245 | `int inner` | Its free plots inside its tiles' edge rings, where alone the painter laid a road until 0.7.87, and the road plots it holds: its room for roads, by which its counts are still placed (0.7.64). |
| 246 | `long usedRoad` |  |
| 248 | `final int[] counts` | Its count of every type, by id. |
| 250 | `final int[] sites` | Its owned sites of each slot (SLOTS; of each SITED resource until 0.8.0). |
| 252 | `short[] tileFree` | Each tile's free plots, row by row: owned and buildable (World.buildable()); null until measured or asked for. |
| 254 | `short[] tileInner` | ...and those inside its edge ring, where alone the painter laid a road until 0.7.87 (the counts' room for roads still measures it). |
| 256 | `long[][] tileSiteKey` | Each tile's sites, by key (TilePainter.Input.siteKey), and their free plots on it, all and inside the edge ring: what a mine standing on one takes of the tile. |
| 257 | `short[][] tileSiteFree, tileSiteInner` |  |
| 259 | `int[] sited` | How many of each site-bound type stand on sites, each counted in the district of its first site (the rest of its count has none); null when none. |
| 261 | `int[][] siteRuns` | For painting (0.8.0, batch I14; pairs of {holding, mines} by resource before): per slot, the works on its sites here in the walk's order, triples of {holding, type, sites} - which type each holding's sites here carry,... |
| 263 | `long version` | Bumped whenever its counts change: what its sites' list is kept against (its deal's until 0.7.87). |
| 265 | `int index` | Its place in the map's order, nearest first. |
| 267 | `long weighedBuild` | (CL5) What the map's pieces last credited it with: the pieces' build it was weighed in, its piece then, its built cells and the version they were read at (MapPieces.weigh()). |
| 268 | `int weighedPiece` |  |
| 270 | `long ownKept, ownKeyKept` | (UP5) Its own stamp as last worked out (CityMap.ownStamp()) and the key of the inputs it was worked out from; ownHas false until the first. |
| 271 | `boolean ownHas` |  |
| 279 | `int[] logMonth` | ITS LOG (batch CL3): the changes to its counts, month by month, in the order they came - a record {month, type, change} (+ come, - gone) for each type a month moved, the types in id order, months never falling, one re... |
| 281 | `int logN` | How many records the log holds. |
| 283 | `int firstBatch` | How many of its first records are one batch, placed in today's order (a canonical map's deal; a sidecar's from before FORMAT 9). |
| 285 | `int[] logged` | Its counts as last logged; null before the first (the same as none). |
| 287 | `boolean logDirty` | Whether its counts may have moved since they were logged: set wherever they change (a new district is dirty), cleared by logChanges(). |
| 289 | `boolean weighQueued` | Whether it stands in the map's weighQueue: its version moved since the pieces last weighed it (moved()). |
| 291 | `int[] touched` | The types add() has changed here since it was logged, first touchedN of them in the order they came; touchedN is -1 when more than TOUCHED_MAX came (every type is compared then). |
| 292 | `int touchedN` |  |
| 294 | `int builtAt` | The map's builtEpoch when the month's new buildings first went into it: it stands in builtInAt when this is the epoch. |
| 296 | `long logHash` | A hash of its log, kept with it: what its plan's stamp folds, so a plan is drawn again when its log moved and its counts did not. |
| 304 | `int[] jMonth` | ITS JOURNAL OF THE RUNS (batch FX7, THE RUNS DATED): a record {month, plot, before, after} for each plot of its own DISTRICT x DISTRICT box whose kind of fixed ground a month changed - the plot's index in the box, row... |
| 306 | `int jN` | How many records its journal holds. |
| 308 | `long jHash, jHashCol, jHashRow, jHashCorner` | Hashes of its journal, kept with it: of every record, of those on its first column, of those on its first row, and of the one on its first plot - what the plans of it and of its west, north and north-west neighbours (... |
| 480 | `private final long seed` |  |
| 482 | `private final World.Key worldKey` | The world it is drawn on (0.7.115, batch E2): its land's key, whose ground every tile and plan reads. |
| 483 | `private final long siteX, siteY` |  |
| 484 | `private final long baseDX, baseDY` |  |
| 485 | `private final BuildingVisual.Type[] types` |  |
| 486 | `private CityLand land` |  |
| 487 | `private Function<Resource, double[]> remaining` |  |
| 489 | `private final List<District> districts` |  |
| 495 | `private final Map<Long, District> byKey` | The districts by key(dx, dy), stored under spread(key): a Long's hash is its halves xor-ed (dx ^ dy), which only a few hundred values of a city round the origin share, so the HashMap's bins grew into trees and every l... |
| 508 | `private final long[] have` |  |
| 509 | `private final int[] lo, hi` |  |
| 510 | `private int innerCursor, outerCursor` |  |
| 511 | `private int purchasesSeen` |  |
| 512 | `private long centreSeen` |  |
| 513 | `private boolean measured` |  |
| 514 | `private Pyramid pyramid` |  |
| 515 | `private SiteIndex siteIndex` |  |
| 516 | `private boolean sitedPlaced` |  |
| 518 | `private int month` | The month of its last change (CL3): what its districts' logs date their records by; 0 for a map drawn canonically with no month given, a harness's square city. |
| 569 | `private long[] totalsCache` | totals()'s last answer, kept against the map's changes. |
| 570 | `private long totalsAt` |  |
| 704 | `private final List<District> weighQueue` | The districts whose version moved since the pieces last weighed them, each once (District.weighQueued): what MapPieces.weigh() reads instead of every district (GT1). |
| 725 | `private long[] freeOf, freeRoadOf` | Each district's free whole plots (District.freeCells()) and free plots for roads (District.freeRoad()), by its index, kept as its counts and ground move (moved()): what the walks of place() and placeRoad() read first. |
| 1127 | `private final List<District> logQueue` | The districts add() dirtied since logChanges() last ran, each once, in the order they first moved. |
| 1130 | `private boolean logAll` | Whether the next logChanges() reads every district: a new district starts dirty and the canonical deal dirties all, and neither passes through add(). |
| 1181 | `private long changes` | How many changes the map has taken since it was drawn - a district's count, a purchase measured, a site's state: what the view stamps its tiles again on (MapTiles). |
| 1200 | `private int[][] lastStates` | Each resource's holdings' states at the last month: when they move, the sites are drawn again. |
| 1203 | `int lastTouched` | How many district-types the last month's change touched. |
| 1206 | `double lastRunsMs` | How long the last month's runs took to lay, in ms (0.7.89): a harness's. |
| 1223 | `private double roadShare` | The city's road plots for each plot of its other buildings, this month: its roads' whole plots over everything else's (0 with no road). |
| 1259 | `private int[] builtInAt` | The districts the month's new buildings went into, by index, the first change to each in order (ROADS FOLLOW THEIR BUILDINGS): where its new roads are looked for, so a month's work stays with the districts it changes. |
| 1260 | `private int builtInN` |  |
| 1263 | `private int builtEpoch` | The month builtInAt was started in (a count of reconcile()s): a district stands in it when its builtAt is this (GT1: a hash set of the month's districts made a month's first change a hash and a node for each). |
| 1304 | `private double[] lackOf` | THE LACKING HEAP (0.7.94, batch RD7): placeRoad()'s districts, the one whose buildings lack road the most first, the lower index on a tie - the order the PriorityQueue it replaces kept, entry for entry (no two entries... |
| 1305 | `private int[] lackAt` |  |
| 1306 | `private int lackN` |  |
| 1422 | `private long[] passedSize` | WALKS PASSED (0.7.94, batch RD7). |
| 1423 | `private int[] passedTo` |  |
| 1424 | `private int nPassed` |  |
| 1493 | `private int[][] campusTypes` | Each group's types, by id: worked out once a table. |
| 1802 | `District[][][] where` | [resource][holding]: the districts, and the sites in each. |
| 1803 | `int[][][] how` |  |
| 1805 | `final List<List<Map<District, int[]>>> tally` | The tallies they are read from: [resource] then holding, district to sites. |
| 1996 | `District[] at` |  |
| 1997 | `int[] holding` |  |
| 1998 | `int length, slot` |  |
| 2000 | `int pos, partial` | The next free site: its entry, and the sites of that entry taken. |
| 2002 | `long total, taken` | Its sites, and those taken. |
| 2009 | `SiteIndex ix` |  |
| 2010 | `int[] states` |  |
| 2012 | `Walk[] walks` | By the resource's own slots, in SLOTS' order. |
| 2014 | `long[][] on` | By claim (claims()): its works on each of the resource's slots, on none, and its count at the last month. |
| 2015 | `long[] over, count` |  |
| 2018 | `private final Lay[] lays` |  |
| 2021 | `private int[][] claims` | Each SITED resource's claims (the types standing on its sites) in the order they are laid: worked out once a table. |
| 2412 | `final byte kind` | Its kind (DistrictPlan.Curve.CRESCENT or SHORE), its radius and its geometry as DistrictPlan.Curve has them, in the frame's plots. |
| 2413 | `final double radius` |  |
| 2414 | `final double[] geom` |  |
| 2416 | `final int[] plots` | Its own plots, in the order DistrictPlan.Curve.plots() gives them (the first and the last are its ends), then the plots its band reaches, each with CURVE_BAND_BIT; all frame plots (y x FRAME + x). |
| 2418 | `final int own` | How many of those are its own. |
| 2420 | `final int x0, y0, x1, y1` | The box of all its plots, in the frame's plots (inclusive). |
| 2465 | `long stamp` | Its link's stamp: its own inputs (ownStamp()) and what was carried to it. |
| 2467 | `private long digest` | A hash of everything the painter reads of the plan (batch UP5, tileKey()), worked out the first time it is asked: its tiles' street codes, the east column and south row, its buildings' boxes, sidings, avenues and curves. |
| 2468 | `private boolean digested` |  |
| 2523 | `final byte[][] tiles` | Each tile's street codes (TilePainter's S_ codes), null where it has none, packed by row (pack()): a byte for each of its 32 rows saying which of its patterns that row is, then the patterns, 32 codes each - a tile's s... |
| 2525 | `byte[] east, south` | The frame's east column and south row: the first column and row of the districts east and south of it, where its edge cells' last arterials run (null where it lays none). |
| 2527 | `final int[][] boxes` | Each tile's buildings, an int each: the box - x, y, w - 1 and h - 1 in the tile's plots, five bits each, PACKED_BIT when packed at the city's edge - and the type id from BOX_TYPE_SHIFT. |
| 2529 | `int[] siding` | The plots its plan's sidings run over (batch FR7 slice 5, D14), each a plot of its frame (y x FRAME + x) with SIDING_END_BIT set on the first of a siding, the loading shed's; null for none. |
| 2531 | `int[][] sidingWays` | Its plan's sidings' ways as corners (MF1; DistrictPlan.sidingWays), each {the railway plot it meets, then the frame plots of its first plot beside the railway, its corners and its shed}; null for none: what the trains... |
| 2533 | `int sidingsStub` | How many sidings its plan laid that are not drawn, as no unit train fits on them (MF1, sidingLeast()): the stubs. |
| 2535 | `int[] avenue` | The plots its plan's avenues run over (batch CL4, DistrictPlan.avenue), each a plot of its frame (y x FRAME + x) with the avenue's mark - CityRuns' M_DIAG, the arm's heading and M_CORNER - above it at AVENUE_MARK_SHIF... |
| 2537 | `DrawnCurve[] curves` | Its plan's curves (batch CL4, slice D; DistrictPlan.curves) as the painter reads them, in the order the plan has them; null for none. |
| 2539 | `byte[][] laid` | The streets the city's packing has laid to the buildings packed in its ground (MF1; Pack.laid): each tile's codes, a byte a plot row by row, null for none - the packing's, shared, taken up from it when this plan is as... |
| 2540 | `Object laidFor` |  |
| 2542 | `int[][] access` | Its plan's access roads (batch FR7, D17) as the trucks drive them (FR5, slice B): each a way of plots of its frame (y x FRAME + x), the road's end first and its gate last, 4-connected over the plots the roads run over... |
| 2544 | `int placed, refused` | Buildings its plan placed, and what it could not hold (carried on, R7). |
| 2546 | `final long[] halves` | Its streets' surface in half plots by kind, [0, gravel, paved, highway], the plots of street it lays and how many of them are tracks: what its own road is drawn as. |
| 2547 | `int streets, tracks` |  |
| 2549 | `int joinsOut, partedOut, seamsSurfaced` | Its pieces joined to the streets of the districts before it, those no way over its ground joins, and its one-sided seams surfaced (DistrictPlan's figures). |
| 2555 | `double surplus` | Road it has no street for (the plan's surplus), in plots, and by kind [0, gravel, paved, highway]. |
| 2556 | `final double[] leftover` |  |
| 2802 | `final long own, serial` |  |
| 2803 | `final int[] in, out` |  |
| 2804 | `final long[] free, streets` |  |
| 2805 | `final byte[][] margins` |  |
| 2807 | `final long marginsHash` | Its margins hashed (marginsHashOf()), worked out once: what the plans after it that read it keep of it. |
| 2808 | `final District[] earlier` |  |
| 2809 | `final long[] read` |  |
| 2811 | `final double surplus` | Its plan's road with no street to carry it (DistrictPlan.surplus), in plots: what the legend counts (star RD2-4); by kind [0, gravel, paved, highway]. |
| 2812 | `double[] leftover` |  |
| 2814 | `long picture` | Its plan's picture's stamp (0.7.128, batch MM1; DistrictPicture.stampOf()): the picture kept for it is current when it bears this. |
| 2816 | `private final long stamp` | Its plan's stamp, worked out once (batch UP3): `own` and `in` are final and a carry is never written after it is made (a job's carry on is its overflow log's clone), so the stamp cannot move - where stampOf() folds ev... |
| 3007 | `long stamp` |  |
| 3008 | `final Map<District, int[][]> boxes` |  |
| 3009 | `final Map<District, byte[][]> laid` |  |
| 3010 | `private final Map<District, Long> digests` |  |
| 3024 | `int packed, smaller, none` |  |
| 3025 | `int touching, reached, laidTo, field, laidStreets, laidPlots, laidLongest` |  |
| 3028 | `private final Map<District, Drawn> drawn` |  |
| 3043 | `private long drawnMoves` | How many times the plans kept (the map above) were put, replaced, evicted, removed or cleared: what the trips (tripsDrawn()) are kept against besides changes(), since a plan is drawn in the background without the map ... |
| 3044 | `private final Map<District, Link> links` |  |
| 3045 | `private final Map<District, PlanJob> pending` |  |
| 3047 | `private final Map<Integer, Pack> packs` | Each band's packing at its edge, and the job making it. |
| 3048 | `private final Map<Integer, PackJob> packings` |  |
| 3075 | `private long[] bandEpoch` | Each band's links checked at an epoch (bandEpoch): those of its districts up to bandValidTo (an index in the map's order) hold, and (when all do) the stamp of the packing they make. |
| 3076 | `private int[] bandValidTo` |  |
| 3079 | `private long[] bandFull` | Each band's epoch (planEpoch()) at which a job that tried to take its district's link as it stood could not (PlanJob, lazy), Long.MIN_VALUE for none: the chain is handed out in full for the band at that epoch (batch U... |
| 3082 | `private long jobsPlanned, jobsReused, jobsFailed, jobsWasted` | How the chain's jobs came out since the map was drawn (batch UP5), counted as adopt() keeps them: plans made, links taken as they stood, jobs that could do neither (a lazy job whose carry in or edges had moved: its ba... |
| 3083 | `private int bandsSized` |  |
| 3086 | `private final LinkedHashMap<Integer, Boolean> bandsUsed` | The bands used, the last last: those past BANDS_KEPT are let go (forgetBand()). |
| 3089 | `private long jobsHanded` | Jobs handed out so far: each job's serial, which keeps a later job's link over an earlier one's adopted after it. |
| 3134 | `private long ownServed, ownMade` |  |
| 3364 | `final CityMap map` |  |
| 3366 | `final long epoch, serial` | The epoch it was handed out at (planEpoch()), and its place among every job the map handed out: a later job's inputs are the newer. |
| 3367 | `private boolean done` |  |
| 3395 | `final District d` |  |
| 3397 | `final DistrictPlan.Input in` | Its input, gathered on the map's thread; null for a lazy job (see above). |
| 3398 | `final long own` |  |
| 3399 | `final PlanJob after` |  |
| 3400 | `final int[] given` |  |
| 3401 | `final boolean edge` |  |
| 3403 | `final District[] earlier` | The districts about it before it in the map's order (earlierOf()), and each one's link or job: whose frames' edges it reads (readNeighbours()). |
| 3404 | `final Object[] sources` |  |
| 3406 | `final Link old` | Its district's link and plan (null where the plan was let go) as they stood when the job was made, which it takes where it can; null for a job with none to take (the first that broke, a district whose own stamp moved). |
| 3407 | `final Drawn oldDrawn` |  |
| 3409 | `int[] carryIn, carryOut` | What was carried to it and what it carries on: {month, code} pairs in the order they came (CL3; an arrival, or the departure ~k of the k-th pair's, DistrictPlan.Input.carry), null for none. |
| 3410 | `Drawn result` |  |
| 3411 | `long[] free, streets, read` |  |
| 3412 | `byte[][] margins` |  |
| 3414 | `long marginsHash` | Its margins hashed (marginsHashOf()): what the plans that read it keep of it (Link.read). |
| 3416 | `volatile boolean failed` | Whether it made no plan and took no link (see above): volatile, as chainTo() looks at a job that may still run. |
| 3418 | `boolean reused` | Whether it took its link's work (the inputs it would be planned from were its link's). |
| 3419 | `double ms` |  |
| 3421 | `final int[] extras` | Its picture's (0.7.128, batch MM1): the boxes the runs and the shore stand on its ground (extrasIn()), the stamp of the picture kept for it when it was handed out (0 for none), its plan's plots' stamp (DistrictPicture... |
| 3422 | `final long keptPicture` |  |
| 3423 | `long pictureStamp` |  |
| 3424 | `DistrictPicture picture` |  |
| 3425 | `double pictureMs` |  |
| 3542 | `long stamp` |  |
| 3543 | `int band` |  |
| 3544 | `final District[] edges` |  |
| 3545 | `final Object[] from` |  |
| 3546 | `final Object last` |  |
| 3547 | `final BuildingVisual.Type[] types` |  |
| 3548 | `final long[] tileOrder` |  |
| 3549 | `Pack result` |  |
| 4080 | `final long[] free, street, against, reach, near` |  |
| 4081 | `byte[][] laid` |  |
| 4094 | `final int[][] w, h` |  |
| 4095 | `final int[] n` |  |
| 4392 | `private final Map<District, DistrictPicture> pictures` |  |
| 4397 | `private long picturesKept` | How many pictures have been kept since the map was drawn, each drawn for plots the one kept before it was not: what the far view repaints a waiting node on, and a harness counts a month's redraws by. |
| 4523 | `private long landVersion` | Bumped whenever free ground is measured again (a purchase): what a run waiting on the ground is tried again on. |
| 4526 | `private CityRuns runs` | The city's highways and railway (spec 2.7, 2.8). |
| 4597 | `private List<WorldLine> worldLinesCache` | worldLines()'s last answer, kept against the map's changes: recomputed when a purchase or a month's runs move changes(). |
| 4598 | `private long worldLinesAt` |  |
| 4714 | `private List<TrainSchedule.Route> trainRoutesCache` | trainRoutes()'s last answer, kept against the map's changes and the wagon tracks the railway's picture gave (FX5, slice T). |
| 4715 | `private long trainRoutesAt` |  |
| 4716 | `private int trainRoutesWagons` |  |
| 4717 | `private long trainRoutesSidings` |  |
| 4721 | `private final Map<Long, PlanSidings> planSidings` |  |
| 4723 | `private long planSidingsVersion` | Moved whenever a plan adopted has other sidings than the ones kept: trainRoutes() is made again. |
| 4729 | `private Map<Long, List<DrawnBend>> trainBends` | The railway's bends the tiles draw, by the tiles they reach (bendKey()), made with the routes and kept with them. |
| 4851 | `private Map<Long, List<TrackCurves.Bend>> highwayBends` | The highways' bends the tiles draw (FX5 slice D), by the tiles they reach (bendKey()), kept against changes() and whether the highways reach a street. |
| 4852 | `private long highwayBendsAt` |  |
| 4853 | `private boolean highwayBendsReach` |  |
| 4920 | `private volatile YardLayouts yardLayouts` |  |
| 5229 | `private List<TruckSchedule.Route> truckRoutesCache` | truckRoutes()'s last answer, kept against the map's changes. |
| 5230 | `private long truckRoutesAt` |  |
| 5287 | `long stamp, x0, y0` |  |
| 5288 | `DistrictRoutes routes` |  |
| 5291 | `private final Map<Long, AccessRoutes> accessRoutesCache` |  |
| 5389 | `private int accessRoadCountCache` | accessRoadCount()'s last answer, kept against the map's changes. |
| 5390 | `private long accessRoadCountAt` |  |
| 5423 | `final int[] way` |  |
| 5424 | `final long ox, oy, end, gate` |  |
| 5425 | `long ramp` |  |
| 5426 | `long[] leg` |  |
| 5440 | `long stamp, met, hash` |  |
| 5441 | `int arm` |  |
| 5442 | `final List<TripRoad> roads` |  |
| 5448 | `private final Map<Long, TripLegs> tripLegs` |  |
| 5450 | `private List<DistrictTrips> tripsCache` | tripsDrawn()'s last answer, kept against the map's changes and the plans kept (drawnMoves). |
| 5451 | `private long tripsAt` |  |
| 5453 | `private final Map<Long, Boolean> tripReach` | Whether the highways have a way to a ramp plot, by the plot, for the pass being made. |
| 5564 | `private int[] tripSeen, tripDist, tripPrev` | The street search's scratch, kept and stamped (as DistrictPlan's accSeen) so none is cleared or allocated per search: made on the first search, TRIP_BOX squared a plot. |
| 5565 | `private byte[] tripCost, tripEnd, tripFixed, tripMarks` |  |
| 5566 | `private int tripStamp` |  |
| 5567 | `private final int[][] tripQ` |  |
| 5568 | `private final int[] tripN` |  |
| 5570 | `private final Map<Long, Drawn> tripPlans` | The drawn plans the search box meets by district key, read by streetAt() without planning (its `plan` false): a district missing here has none. |
| 5579 | `private int tripSearches` | How many street searches were made: a harness's, to tell legs kept from legs made again. |
| 5861 | `private final Map<Long, byte[]> lines` |  |
| 5864 | `private final Map<Long, byte[]> tiles` |  |
| 5867 | `private final Map<Long, boolean[]> owned` |  |
| 5871 | `private final Map<Long, boolean[]> rawOwned` | ET5: ownership with no mask (atRaw()), kept only while the city holds a picture of its landmasses; the same size and the same version as `owned`. |
| 5874 | `private long rawAt` |  |
| 5875 | `private boolean[] lastRaw` |  |
| 5876 | `private long ownedAt` |  |
| 5877 | `private byte[] lastLine` |  |
| 5878 | `private boolean[] lastOwn` |  |
| 5879 | `private int lastCover` |  |
| 5880 | `private final boolean[] ALL_OWNED` |  |
| 6004 | `private final RunGround runGround` |  |
| 6063 | `private final Map<Long, byte[]> terrain` |  |
| 6064 | `private final Map<Long, int[]> labels` |  |
| 6065 | `private final HashSet<Long> blocked` |  |
| 6066 | `private World world` |  |
| 6067 | `private int[] lastChunk` |  |
| 6068 | `private long lastKey` |  |
| 6074 | `private final long[] cacheKey` | The chunks looked up lately, by a hash of their column and row (GT1): pieceOf() is asked of every net's hub a few times a month, and the one chunk remembered above holds for a run of neighbours only, so nearly every a... |
| 6075 | `private final int[][] cacheChunk` |  |
| 6076 | `private int[] parent` |  |
| 6077 | `private long[] first` |  |
| 6078 | `private int nLabels` |  |
| 6080 | `private boolean built` |  |
| 6081 | `private long builtVer` |  |
| 6082 | `private CityLand seenLand` |  |
| 6083 | `private CityShore seenShore` |  |
| 6084 | `private long seenShoreVer, seenCentre` |  |
| 6085 | `private int seenPurchases` |  |
| 6087 | `private long seenLinks` | The picture's stamp the labels were read under (ET5; 0 for none): a purchase adds ground the labels can be extended by only under the same linked set, else they are read from the start. |
| 6089 | `private int count` |  |
| 6090 | `private int[] pieceOfLabel` |  |
| 6091 | `private long[] tilesOf` |  |
| 6092 | `private long[][] hubs` |  |
| 6093 | `private boolean[] hubDone` |  |
| 6094 | `private long buildId` |  |
| 6096 | `private long weighedBuild` |  |
| 6097 | `private long[] weights` |  |
| 6098 | `private boolean weightsValid` |  |
| 6099 | `private int[] undrawn, undrawnCells` |  |
| 6102 | `int floods, updates` | How many times the whole ground was read (the first time, a restatement, a work on the shore), how many times only a purchase's was, and the last of either in ms: what a harness times. |
| 6103 | `double lastMs` |  |
| 6420 | `private long[] nearestKey` | Each piece's nearest tile's key, once found: nearest the site's tile first, then the northern, then the western; Long.MAX_VALUE for a piece with no tile. |
| 6522 | `private final MapPieces pieces` |  |
| 6634 | `private int[] yardSeqCache` | yardSequence() of a set of counts, the last kept against the terminal types' counts in it (a month and a tile ask for it again and again; a city of billions has thousands of yards). |
| 6635 | `private long yardSeqKey` |  |
| 6658 | `private int[][] footprints` |  |
| 6716 | `private List<long[]> minesKept, worksKept` | layRuns()'s mined sites and bulk works, kept against a digest of the districts' mines (their site runs): a city of billions has some two hundred thousand sites, and walking every district's site list costs seconds the... |
| 6717 | `private long minesKey` |  |
| 6733 | `private int[][] yardShare` | Each district's Rail Terminals and Depots the runs draw as yards, by its index and then the terminal type's place among the terminal types (terminalTypes()): the yards of each type dealt to the districts holding that ... |
| 6734 | `private long yardShareAt` |  |
| 6748 | `private int[] terminalTypesKept` |  |
| 6787 | `private int spurCount` | The Rail Spurs the model has and the track plots they are, as layRuns() last saw them: the runs lay a siding into a mined site for each spur they can; the rest are dealt (sidingsOf()). |
| 6788 | `private long spurTrack` |  |
| 6791 | `private int[] sidingShare` | Each district's share of the spurs, by its index, how many are dealt in all, and what they were kept against (the runs' version, the map's changes and the spurs). |
| 6792 | `private long sidingsDealt` |  |
| 6793 | `private long sidingShareAt` |  |
| 6963 | `private CityShore shore` | The city's works on its shore. |
| 6969 | `private int[] shoreTypes` | The shore's types, by id: worked out once a table. |
| 6984 | `private int shoreShort` | How many works the model has that the shore holds no place for (drawn by their districts' plans). |
| 6997 | `private final Map<Long, Long> shoreShortBy` | The searches that came up short, by template and landmass (ET5) - or by template alone, with SHORT_ANY (MF1) - with the ground's key they came up short at: none is made again until the ground moves (a purchase) or a w... |
| 7248 | `int nearFrom, anyFrom` | The tiles before these (positions in the district's order) hold no near spot, no spot at all. |
| 7250 | `int countedNear, countedAny` | The tiles the search counts (SEARCH_TILES_MOST) before those positions; once the position is TILES, the district's all. |
| 7255 | `final CityShore shore` |  |
| 7256 | `final long removals, stamp` |  |
| 7257 | `final DTrack[] at` |  |
| 7259 | `boolean used` | Whether a search has been through and left its marks. |
| 7261 | `int from` | A harbour's search (MF1): the first of its tiles, in their order, that may hold a spot - the tiles before it held none and were not unsteady. |
| 7272 | `private final Map<Long, ShoreTrack> shoreTracks` | The marks of the searches, by type and landmass. |
| 7275 | `private long shoreLooked` | How many tiles the searches have looked at, ever (a tile with no salt water or no dry ground too): what the marks save, which MapCheck 9 counts. |
| 7280 | `private long shoreTouched` | How many spots have been asked of a water as wide as the flood's reach that had been found shut, or shut it (ShutSet.touched): what the lagoons MapCheck 9 makes cause. |
| 7293 | `private SeaRoutes.Grid shoreSea` | The routes' grid of the sea as the searches read it, kept: the terrain's cells never change, and a grid made for each search read the same cells again (half of a search with 300 terminals standing). |
| 7314 | `final HashSet<Long> hot` |  |
| 7316 | `boolean touched` | Whether the flood in hand put a hot water in the set or met one there. |
| 7343 | `final int t, landmass, quay` |  |
| 7345 | `final boolean harbour` | MF1: a harbour's search: the quay is the common one, and no work may stand on a spine arm's walk to the world (worldWalks()). |
| 7346 | `final long[][] walks` |  |
| 7347 | `final boolean island` |  |
| 7348 | `final World world` |  |
| 7349 | `final int tile` |  |
| 7350 | `final int[] fp, tileLm, windowLm` |  |
| 7351 | `final List<long[]> mines` |  |
| 7352 | `final byte[] fixed` |  |
| 7353 | `final int[] ground` |  |
| 7356 | `final SeaRoutes.Grid sea` | A terminal's water must open to the sea (SeaRoutes.opensToSea(), star O13-5): the routes' own grid, and the water found shut, kept for the search. |
| 7357 | `ShutSet shut` |  |
| 7359 | `CityShore.Work near, far, first` | The tile last looked at: its first near spot, its first other spot, the first of either (stopAtAny), and whether an interchange's reserve lies inside it. |
| 7360 | `boolean firstIsNear, unsteady` |  |
| 7363 | `long ax, ay, reachSq` | MF1: the harbour the search is for, as its first work's shore plot and the reach about it; reachSq is MAX_VALUE for a search of the whole coast. |
| 7648 | `static int harbourReachDefault` | The reach a new map starts with: CityShore.HARBOUR_REACH. |
| 7651 | `private int harbourReach` | The harbour rule's reach in plots, 0 for the rule off (the whole coast is searched, as before MF1). |
| 7800 | `static boolean keepWorldWalks` | MF1 test switch, true: a harbour's works keep off the spine arms' walks (worldWalks()). |
| 7892 | `private int[][] shoreShare` | Each district's works the shore holds, by its index and the shore's type: the works dealt to the districts holding their type in the map's order, so the rest are their plans' (kept against the shore and the counts). |
| 7893 | `private long shoreShareAt` |  |
| 7936 | `private RailPicture railPicture` |  |
| 8027 | `private Landmasses.Picture landmasses` | The landmasses as the game last handed them, or null while the city owns nothing off its home landmass. |
| 8030 | `private final LinkedMask mask` | Which plots of a tile the island rule masks, for the picture held. |
| 8085 | `private Landmasses.Picture p` |  |
| 8086 | `private final Map<Long, boolean[]> tiles` |  |
| 8089 | `private final boolean[] NONE` |  |
| 8090 | `private final int[] ids` |  |
| 8158 | `private AtSea atSea` |  |
| 8173 | `private long atSeaMoves` | How many times the oil at sea was handed over changed (atSea()): what a tile's key (tileKey(), batch UP5) holds the sea's plots, rings and pipes against. |
| 8269 | `private List<BridgeRun> bridgeRuns` | The bridges as the game last handed them, in any order; empty while the city has none. |
| 8272 | `private long bridgeStamp` | Their stamp (CityRuns.stampOf()), 0 for none: what a cache of anything the bridges touch keys on. |
| 8337 | `private List<BoatSchedule.Route> routes` | The routes found, the lanes drawn of them, and the berths they were found for (the shore's, in the order laid). |
| 8338 | `private SeaLanes lanes` |  |
| 8339 | `private List<BoatSchedule.Berth> routesBerths` |  |
| 8341 | `private long routesKey` | What they were found at: the world, the site and the radius's band (routesKeyNow()). |
| 8343 | `private CityShore routesShore` | The shore and its version the berths were read from: while both hold, the berths are the same. |
| 8344 | `private long routesShoreVersion` |  |
| 8392 | `final long key, seed, siteX, siteY` |  |
| 8394 | `final World.Key world` | The world the routes are found on (0.7.115). |
| 8396 | `final CityShore shore` | The shore and its version the berths were read from: the routes are the city's only while they hold. |
| 8397 | `final long shoreVersion` |  |
| 8398 | `final List<BoatSchedule.Berth> berths` |  |
| 8400 | `final List<BoatSchedule.Route> kept` | The routes of the berths' first ones that are kept (SeaRoutes.extend()). |
| 8401 | `final double radius` |  |
| 8402 | `private volatile SeaRoutes.Found found` |  |
| 8403 | `private volatile SeaLanes lanes` |  |
| 8552 | `private final boolean[] keyFlags` | The scratch flags tileKey() reads a part-owned tile's ownership into. |
| 9018 | `private final Map<Long, List<DrawnSite>> siteLists` |  |
| 9021 | `private final Map<Long, Long> siteListVersion` |  |
| 9024 | `private long siteGen` | How many times the site lists were all let go because the holdings' states moved (reconcile(), forgetPainted()): a list is kept against its district's version and this, so is a stamp worked out from them (ownKey(), ba... |
| 9200 | `private boolean journalling` | Whether the month's runs are being journalled: only inside reconcile()'s layRuns() and layShore(), never the canonical deal, a square city, a sidecar's read or a harness's own laying. |
| 9203 | `private boolean reachAtStart` | highwaysReach() as the month began: the districts the month changes are read as the highways were drawn then. |
| 9206 | `private final Map<Long, byte[]> journalWas` | The districts the month has read so far, by key: each one's kinds as they were before the month's first change to it. |
| 9209 | `private long lastWitnessed` | The key of the district the witness was told of last: a stretch is told of one district plot by plot. |
| 9212 | `private final CityRuns.Witness witness` | The witness set on the runs for the length of a month's laying. |
| 9215 | `private long journalDropped` | How many plots' records a plan's input has left out because the journal did not end on the runs as they stand (rule (b) of fixedLogInto()): a journal that disagrees with today's runs is a bug, and MapCheck holds this ... |
| 9258 | `private final java.util.ArrayDeque<byte[]> kindsPool` | The 64 KB arrays kindsOf() fills, kept from month to month (GT1): each month's journal made a pair for every district the runs changed, about half a megabyte a month at 10 billion people and all the garbage the month ... |
| 9352 | `private final java.util.Set<Long> journalTouched` | The world districts the witness was told of this month that the map does not hold, as column << 32 ^ row: not journalled, but a reserve beside a district it holds may have moved. |
| 9359 | `private List<long[]> reserveLaid` | The interchanges' reserves that were laid (Site.got()) as the last journalled month ended, each {x0, y0, w, h} in world plots, over the districts the highways lie in and the margin about them; null until first asked (... |
| 9362 | `private CityRuns reserveRuns` | The runs (and their version) the reserves were last found against, and the purchases then: what the next month's changes are found against. |
| 9363 | `private long reserveRunsAt` |  |
| 9364 | `private int reservePurchasesSeen` |  |
| 9371 | `private final java.util.Set<Long> minedTouched` | The world districts a mine or well came to or left since the last journalled month, as column << 32 ^ row (markRun(), relay()): where a mined site's box may have moved, which a reserve may not take (reserveBlocked()). |
| 9520 | `private List<long[]> reservesMovedNow` | The reserves that came or went as the last month ended, when reservesEnded() knew them without comparing the whole sets; null when it did not. |
| 9896 | `private final CityMap map` |  |
| 9897 | `private final int minX, minY` |  |
| 9899 | `private final int[] widths, heights` | Each level's grid: its width, height and nodes (NODE_WIDTH numbers each, row by row). |
| 9900 | `private final long[][] nodes` |  |
| 9901 | `private final int count` |  |
| 10132 | `private long lastStamp` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 153 | 10252 | **type** `public final class CityMap` | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, each district's street plan kept for the painter with what it cannot ... |

### THE GEOMETRY (lines 155-227)

| line | len | member | says |
|---:|---:|---|---|
| 195 | 8 | `static { ... }` |  |

### A DISTRICT (lines 228-475)

| line | len | member | says |
|---:|---:|---|---|
| 233 | 240 | **type** `public static final class District` | One district: where it is, its free plots, the ground and the plots used, its owned sites, its counts by type. |
| 310 | 7 | `District(int dx, int dy, int types, double order)` _(in CityMap.District)_ |  |
| 319 | 12 | `void touch(int t)` _(in CityMap.District)_ | Notes that type t was changed here (add()): what logChanges() reads of a district that was logged before. |
| 333 | 1 | `public int owned()` _(in CityMap.District)_ | Its free plots (owned and buildable, its sites' plots among them). |
| 335 | 1 | `public double used()` _(in CityMap.District)_ | The plots of ground its buildings use: each type's count x its land in plots. |
| 337 | 1 | `public long usedCells()` _(in CityMap.District)_ | The whole plots its buildings and roads are drawn on. |
| 338 | 1 | `public int count(int t)` _(in CityMap.District)_ |  |
| 339 | 5 | `public int sites(Resource r)` _(in CityMap.District)_ |  |
| 345 | 1 | `long room()` _(in CityMap.District)_ | Its room in whole plots: PLAN_FILL of its owned plots, what its plan can draw (CL3; every owned plot before). |
| 347 | 1 | `long freeCells()` _(in CityMap.District)_ | Its room left in whole plots, never below nothing (0.7.64; in ground, half square feet, before). |
| 349 | 1 | `long freeRoad()` _(in CityMap.District)_ | ...and for roads: its whole room, no more than its plots inside the edge rings less its road. |
| 352 | 1 | `public int logSize()` _(in CityMap.District)_ | How many records its log holds. |
| 355 | 1 | `public int journalSize()` _(in CityMap.District)_ | How many records its journal of the runs holds (batch FX7). |
| 362 | 28 | `void logAdd(int month, int t, int change)` _(in CityMap.District)_ | Adds a change of type t, made in a month, to its log: a record at the end, or - when the month's records after the first batch already hold the type - into that one (one record to a type and month; gone when the two c... |
| 392 | 5 | `void rehash()` _(in CityMap.District)_ | Its log's hash worked out again from its records. |
| 398 | 4 | `private static long fold(long h, int month, int t, int change)` _(in CityMap.District)_ |  |
| 404 | 7 | `boolean sameLog(District b)` _(in CityMap.District)_ | Whether its log and b's are the same: the count of records, the first batch and every record. |
| 413 | 5 | `boolean sameJournal(District b)` _(in CityMap.District)_ | Whether its journal of the runs and b's are the same: the count of records and every record. |
| 424 | 19 | `void jAdd(int month, int plot, int before, int after)` _(in CityMap.District)_ | Adds the change of a plot's kind from `before` to `after`, made in a month, to its journal (batch FX7): a record at the end - the month never falls, one before the journal's last is the last's - or, when the plot has ... |
| 445 | 11 | `void jAppend(int month, int plot, int before, int after)` _(in CityMap.District)_ | ...a record at the end, for a month later than the journal's last (a month's first records need no look for a plot's own). |
| 458 | 8 | `private void jFold(int month, int rec)` _(in CityMap.District)_ | Folds one record into its journal's hashes. |
| 468 | 4 | `void jRehash()` _(in CityMap.District)_ | Its journal's hashes worked out again from its records. |
| 474 | 1 | `static long key(int dx, int dy)` |  |

### THE STATE (lines 476-580)

| line | len | member | says |
|---:|---:|---|---|
| 498 | 1 | `private static long spread(long k)` | A key() spread over all 64 bits: multiplied by an odd constant, which is one-to-one on 64 bits, so equal keys stay equal and different ones different. |
| 501 | 1 | `private District districtAt(long k)` | The district under a key() (null for none): byKey, through spread(). |
| 504 | 1 | `private void indexDistrict(District d)` | Files a district in byKey under its key. |
| 507 | 1 | `private void unindexDistrict(District d)` | Takes a district out of byKey. |
| 520 | 12 | `private CityMap(World.Key worldKey, long siteX, long siteY, BuildingVisual.Type[] types)` |  |
| 534 | 1 | `public long seed()` | The world's seed. |
| 537 | 1 | `public int month()` | The month of its last change (CL3): the month its districts' newest log records are dated. |
| 540 | 1 | `public World.Key worldKey()` | The world's key (0.7.115): World.of(worldKey()) is the ground it is drawn on. |
| 543 | 1 | `public BuildingVisual.Type[] types()` | The types it counts, by id. |
| 546 | 1 | `public List<District> districts()` | Its districts, nearest the founding site first. |
| 549 | 1 | `public District district(int dx, int dy)` | The district at (dx, dy) from the founding site's, or null. |
| 552 | 1 | `public Pyramid pyramid()` | The pyramid. |
| 555 | 1 | `public boolean measured()` | Whether its districts were measured on the ground (false for a harness's square city, whose capacities were given). |
| 558 | 9 | `public long[] totals()` | Every type's count over all districts, a copy: kept against changes() (FR9: the screen reads it once a month), since a count never moves without one. |
| 573 | 1 | `public CityLand land()` | The land it was drawn on. |
| 576 | 1 | `long baseDX()` | The founding site's district's column, absolute. |
| 579 | 1 | `long baseDY()` | ...and row. |

### BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE (lines 581-770)

| line | len | member | says |
|---:|---:|---|---|
| 595 | 4 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] counts)` | A map drawn canonically (spec-land 2.5): the land's districts measured, then each type in proportion in every district, inner first, the remainders by largest remainder; mines and wells on their sites. |
| 604 | 4 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] coun...` | ...in a given month (CL3): the month its districts' logs date the deal by, each log one batch (firstBatch) of its counts as the deal gave them. |
| 613 | 4 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] coun...` | ...on the landmasses the game hands over (ET5): the districts are measured with the island rule's mask from the start, so nothing is dealt to ground that is no room; null for a city on one landmass, and then the same ... |
| 621 | 17 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] coun...` | ...and the long bridges that stand (ET6, bridgePicture()), laid with the runs; null or empty for none, and then the same map as the form above. |
| 649 | 4 | `static CityMap square(long seed, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity, long[] counts)` | A HARNESS'S SQUARE CITY (the design's scale case): side x side districts round the founding site, every one owned and holding `capacity` plots - a city of any size at one density without measuring its ground; a distri... |
| 655 | 32 | `static CityMap square(World.Key world, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity, long[] counts)` | ...on the world a key names (0.7.115): EarthTerrainCheck's screen on Earth. |
| 688 | 14 | `private District newDistrict(int dx, int dy)` |  |
| 707 | 11 | `private void moved(District d)` | District d's counts or ground moved: its version bumped (what its sites' list, its plan and its weight are kept against), and it is queued for the pieces' next weighing. |
| 728 | 12 | `private void keepFree()` | freeOf and freeRoadOf, current. |
| 741 | 9 | `private void sortDistricts()` |  |
| 752 | 13 | `private void rangeTypes()` | Each type's lowest and highest district index holding it. |
| 766 | 4 | `private void advanceCursors()` |  |

### MEASURING THE GROUND: OWNED DRY PLOTS (lines 771-979)

| line | len | member | says |
|---:|---:|---|---|
| 776 | 27 | `private void measureAll()` | Every district the land's owned box reaches, measured; the coarse look first when there are many. |
| 805 | 28 | `private boolean[] coarseLand(int dx0, int dy0, int nx, int ny)` | Which districts have land in or beside them, by a sample a tile (COARSE_STRIDE): a district all sea there, with all-sea neighbours, is taken as sea and not measured. |
| 837 | 3 | `int ownership(int dx, int dy)` | Whether the city owns none, some or all of a district: the grid's cover of it, a level-8 block (spec-grid 2.4; by its radii and lanes until 0.7.66). |
| 842 | 3 | `private static int cover(int c)` | LandGrid's NONE, SOME or ALL as this class's. |
| 847 | 5 | `double[] ownedBox()` | The land's owned box from the site, in plots, inclusive {dx0, dy0, dx1, dy1}: its reach each way (an empty land, the site's plot). |
| 868 | 65 | `private void measure(District d, int own)` | Counts a district's free plots, tile by tile and plot by plot (0.7.64): owned and buildable - the ground the model builds on, a resource's sites with it (a mine stands on its site; another building takes a site's plot... |
| 935 | 18 | `private List<long[]> siteSquares(long bx0, long by0, long bx1, long by1)` | Every site's square, {x0, y0, x1, y1} in plots inclusive, of every resource in fields, owned or not, that reaches into a box of plots: the plots the painter keeps as fields. |
| 955 | 10 | `private District measureNew(int dx, int dy, int own)` | A district not on the map yet, measured, and kept only when it holds free ground (or, since ET5, owned ground the island rule masks): a district of water joins the map only when a site in it needs one (tallySites()). |
| 967 | 1 | `public short[] tileFreeOf(District d)` | A district's tiles' free plots (a copy), counted if they were not: for a harness and a probe. |
| 970 | 9 | `short[] tileFree(District d)` | A district's tiles' free plots, counted if they were not (after a sidecar is read, or for a harness's square city); its room stands as it was. |

### THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN (lines 980-1027)

| line | len | member | says |
|---:|---:|---|---|
| 990 | 37 | `boolean syncLand()` | Catches the map up with its land: every district a new purchase touches recounted (new ones added); true when the land was drawn again (a restatement, a conversion), which the caller answers with a canonical map. |

### PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION (lines 1028-1207)

| line | len | member | says |
|---:|---:|---|---|
| 1043 | 3 | `public boolean reconcile(long[] model)` | THE MONTH (spec-land 2.5, star 12): the land caught up, then each type's change placed - new buildings into the first district with room (nearest first; farms, utilities, mines and wells, the railway and the car plant... |
| 1048 | 52 | `public boolean reconcile(long[] model, int month)` | ...in a given month, the game's: what the districts' logs date the month's changes by. |
| 1108 | 3 | `void logChanges()` | THE LOG (CL3): into the log of every district whose counts may have moved (logDirty), a record {month, type, change} for each type whose count is not what was last logged, the types in id order, at the map's month; th... |
| 1113 | 12 | `void logChanges(boolean all)` | ...of every district, dirty or not (all): what a sidecar is written after, so a count moved by hand (a harness's) is a record too and what is written always adds up. |
| 1136 | 38 | `private void logOne(District d, boolean all, boolean queued)` | District d's change since it was logged, into its log (a district not dirty is passed unless `all`). |
| 1176 | 3 | `private void batchLogs()` | Every district's whole log so far one batch, placed in today's order: after a canonical deal, a square city's or a sidecar of FORMAT 8 or older read. |
| 1184 | 1 | `public long changes()` | How many changes the map has taken since it was drawn: when it moves, a view stamps the tiles it shows again (batch J4). |
| 1193 | 5 | `void rebind(CityLand live, Function<Resource, double[]> liveRemaining)` | A map drawn away from the screen's thread, on a copy of the land (Game.MapDraft, batch J4), bound to the city's own land and what remains of its resources before it is kept: the copy was the land as it stood, field fo... |

### ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3) (lines 1208-1784)

| line | len | member | says |
|---:|---:|---|---|
| 1226 | 9 | `double roadShareOf(long[] model)` | The model's road plots over its other whole plots. |
| 1237 | 9 | `long buildingRoom(District d, long each)` | How many buildings of `each` whole plots district d has room for, keeping room for the road they and its buildings need at the city's share: k with k x each plus the road short of share(d's other plots + k x each) no ... |
| 1248 | 4 | `boolean hasRoomFor(District d, int t)` | Whether district d has room for one more of type t, as the month places it (MapCheck 2 asks). |
| 1254 | 3 | `double roadLacking(District d)` | The road a district's buildings lack, in plots, at the city's share: share x its other plots less its road plots. |
| 1266 | 28 | `private int placeRoad(int t, long n)` | Places n more roads of type t, one at a time: each into the district, of those the month's buildings went into, whose buildings lack road the most and that has room for it inside its tiles' edge rings, the nearer on a... |
| 1309 | 4 | `private boolean lackFirst(int i, int j)` | Whether heap entry i comes before entry j: more lacking, then the lower index. |
| 1315 | 6 | `private void lackAdd(double lack, int index)` | An entry added at the end, the heap not kept (placeRoad() makes it a heap after). |
| 1322 | 10 | `private void lackPush(double lack, int index)` |  |
| 1334 | 10 | `private int lackPoll()` | The first entry's district index, taken off the heap. |
| 1346 | 10 | `private void lackDown(int k)` | Entry k sifted down to its place below. |
| 1357 | 4 | `private void lackSwap(int i, int j)` |  |
| 1363 | 45 | `private int place(int t, long n)` | Places n more of type t: into the first districts with room for its whole plots in its direction (a road's inside its tiles' edge rings; since 0.7.72 a building's keeping room for its road, buildingRoom()), the rest i... |
| 1427 | 8 | `private int passed(int kind, long each, int i)` | Where a walk of `kind` for buildings of `each` plots from cursor i may start (WALKS PASSED). |
| 1437 | 17 | `private void notePassed(int kind, long each, int to)` | A walk of `kind` for `each` plots found no room before district index `to` (WALKS PASSED). |
| 1495 | 15 | `private int[] campusTypes(int g)` |  |
| 1512 | 3 | `private int[] campusTypes()` | The refinery's unit types, by id. |
| 1517 | 3 | `long campusCells(District d)` | The refinery's ground in district d, in the whole plots its units are drawn on. |
| 1522 | 6 | `long campusCells(District d, int g)` | ...a group's (0.8.0). |
| 1530 | 3 | `District campusDistrict()` | The district holding the most of the refinery's ground, the nearer on a tie; null when it has none. |
| 1535 | 17 | `District campusDistrict(int g)` | ...of a group's (0.8.0). |
| 1554 | 24 | `int gatherCampus()` | The refinery's units in other districts moved into its campus district while it has room, the largest kinds first, then by district in the map's order (a sidecar older than FORMAT 6, read). |
| 1580 | 5 | `District roomiest()` | The district with the most room left, the nearer on a tie: where what no district has room for goes, to be drawn smaller there. |
| 1587 | 21 | `private int remove(int t, long n)` | Takes n of type t away: an inner type from the outermost district holding it in, an outer type from the innermost out. |
| 1610 | 3 | `private void add(District d, int i, int t, long k)` | Adds k (negative to take away) of type t to the district at index i, keeping its ground and plots used, the running total, the type's range, the cursors and the pyramid. |
| 1615 | 29 | `private void add(District d, int i, int t, long k, boolean sited)` | ...a mine or well on a site (sited) taking its whole plots too: its site is its ground, the land the model gives it. |
| 1646 | 5 | `long usedOf(District d)` | The ground a district's buildings use, in half square feet: each type's count x its land, twice. |
| 1653 | 7 | `long roadOf(District d)` | The road plots a district holds. |
| 1662 | 5 | `long cellsOf(District d)` | The whole plots a district's buildings and roads are drawn on, a mine or well on its site taking its own. |
| 1678 | 106 | `private void allocate(long[] counts)` | THE CANONICAL ALLOCATION (spec-land 2.5): the mines and wells on their sites first (0.7.64), then the city's footprint F, in the whole plots it is drawn on, shared inner first - each district its share of F, min(its r... |

### MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) (lines 1785-2336)

| line | len | member | says |
|---:|---:|---|---|
| 1800 | 7 | **type** `static final class SiteIndex` | Every owned site of the SITED resources: per resource and holding, the districts holding them, nearest first, and how many in each. |
| 1809 | 3 | `int holdingOf(long dx, long dy)` | The holding a plot (dx, dy) from the site lies in: 0 the centre, k the k-th purchase, -1 none - the grid's owner (spec-grid 2.4). |
| 1814 | 17 | `private List<Deposit> fieldsNear(Resource r, double bx0, double by0, double bx1, double by1)` | The fields of a resource that reach into a box of plots: the world's as the city sees them (CityLand.fieldsIn(): its old world's ground's own, 0.7.99), and a converted centre's legacy iron field. |
| 1833 | 4 | `Deposit legacyField()` | A converted centre's legacy iron field, as a field of its sites at its plot (cell -1). |
| 1845 | 9 | `SiteIndex siteIndex()` | The site index: every owned site of the SITED resources by holding and district, built with the map and extended by each purchase (a new holding's sites are those of the fields centred on its ground, wherever they lie). |
| 1860 | 67 | `private void tallySites(SiteIndex ix, int from, double bx0, double by0, double bx1, double by1)` | Adds to the index the sites of holdings `from` on that lie in a box of plots from the site, and lays its lists out again: a new index object, so the walks lay themselves out again too. |
| 1929 | 3 | `public long ownedSites(Resource r)` | The owned sites of a SITED resource on the map. |
| 1934 | 9 | `public long ownedSites(Resource r, Deposit.FieldType type)` | ...of one type of its fields (0.8.0, batch I14), or of all of them (null): what a works of that type may stand on. |
| 1945 | 3 | `private boolean onSites(int t)` | Whether type t stands on its resource's sites (placeSited()): a site of a SITED resource, and sites to take - any other building, a site on none, placed as any building is, so the districts always sum to the counts. |
| 1950 | 4 | `static int slotOf(Resource r, Deposit.FieldType type)` | The slot of a resource's fields of a type, or -1 (not SITED, or a type not the resource's). |
| 1960 | 5 | `static int holdingState(double listed, double left)` | A holding's state for a resource from what remains in it: UNWORKED, WORKING or WORKED_OUT (one that listed none, unworked). |
| 1967 | 7 | `int[] holdingStates(Resource r)` | Each holding's state for a resource, the centre first. |
| 1995 | 11 | **type** `private static final class Walk` | One slot's sites in the order works take them (layWalk()), and how far its claims have filled them. |
| 2004 | 1 | `long free()` _(in CityMap.Walk)_ |  |
| 2008 | 9 | **type** `private static final class Lay` | One SITED resource's works on its sites: its slots' walks, laid against an index and its holdings' states, and each claim's works on each slot and on none. |
| 2024 | 19 | `int[] claims(int k)` | A SITED resource's claims: its typed works first, by their type's slot, then those on ANY of its fields; within each the larger first (BuildingVisual.Type.sites()), then by id - first fit decreasing, as LandManager.fi... |
| 2045 | 4 | `private int claimSlot(int t)` | A claim's slot to sort by: its type's, or past every slot for ANY (or a type not its resource's). |
| 2051 | 9 | `private int[] slotsOf(int k, int t)` | The resource's own slots (indexes into Lay.walks) a claim stands on, in order: its type's alone, or every one for ANY. |
| 2062 | 8 | `private boolean lastOnItsSlots(int k, int c)` | Whether claim c is the last laid on each of its slots: no later claim stands on any of them. |
| 2080 | 26 | `private void placeSited(long[] model)` | Puts each SITED resource's works on its sites (spec-land 2.4, 2.5; since 0.8.0 every type of them, by the type of its field and on as many sites as it takes): in each slot the first sites in acquisition order whose ho... |
| 2108 | 25 | `private void relay(int k, SiteIndex ix, int[] states, long[] want)` | Lays a resource's works on its sites again: every one of its types taken off the map, then each claim in order on its slots' walks from their first site. |
| 2135 | 36 | `private void restand(int k, Lay lay, int c, long n)` | One claim to a new count: more filled onto its slots in order from where they stand, then on none; fewer taken off what stands on none first, then off its slots from the last back. |
| 2173 | 23 | `private Walk layWalk(SiteIndex ix, int[] states, int k)` | The walk's order: holdings not worked out in acquisition order, then the worked-out; within each, its districts nearest first. |
| 2198 | 36 | `private long fill(Walk w, int t, int each, long n)` | Up to n works of type t, `each` sites apiece, onto a walk from its next free site - only while a whole one has room - each counted in the district of its first site. |
| 2236 | 28 | `private void empty(Walk w, int t, int each, long n)` | n works of type t, `each` sites apiece, taken back off a walk from its last taken site: the last claim on it, so they are the ones there. |
| 2266 | 23 | `private void markRun(District d, int slot, int holding, int t, int m)` | m more (or fewer) sites of a district's slot, of one holding, under a works of type t: its painting's runs. |
| 2291 | 6 | `private void count(District d, int t, long q)` | q more (or fewer) works of type t counted on sites in a district. |
| 2299 | 3 | `private void sitedNow()` | The mines and wells stood on their sites (placeSited()), once, before anything reads a district's counts less them: a plan's inputs and its stamp (0.7.88), as the sites' lists do. |
| 2303 | 11 | `private District home()` |  |
| 2315 | 1 | `private static int indexOf(District d)` |  |
| 2318 | 4 | `static int siteSlot(Resource r)` | Where a resource sits among SITED, or -1. |
| 2324 | 12 | `private void clearType(int t)` | Takes every one of type t off the map (its sites' runs are its resource's lay's to clear: relay()). |

### THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch (lines 2337-4372)

| line | len | member | says |
|---:|---:|---|---|
| 2410 | 38 | **type** `static final class DrawnCurve` | A district plan's curve as the painter reads it (batch CL4, slice D; DistrictPlan.Curve): kept small - the map keeps up to PLANS_KEPT of these for a city - its geometry as numbers and the plots it is laid on and reach... |
| 2422 | 22 | `DrawnCurve(DistrictPlan.Curve c)` _(in CityMap.DrawnCurve)_ |  |
| 2446 | 1 | `long bytes()` _(in CityMap.DrawnCurve)_ | The bytes it holds, about. |
| 2455 | 3 | `public static int sidingLeast()` | The fewest plots a siding must have to be drawn: those of a unit train of hoppers (TrainSchedule.Kind.HOPPER.length(), 41.4, so 42), the only train that runs to a works' siding. |
| 2463 | 176 | **type** `public static final class Drawn` | A district's plan as the painter reads it (0.7.88): each tile's street codes and its buildings' boxes, and the figures a harness reads. |
| 2470 | 20 | `long digest()` _(in CityMap.Drawn)_ |  |
| 2492 | 13 | `private static long hashOf(byte[] a)` _(in CityMap.Drawn)_ | An array of bytes folded into one number, eight a word; a fixed number for none. |
| 2507 | 7 | `private static long hashOf(int[] a)` _(in CityMap.Drawn)_ | ...ints, two a word. |
| 2516 | 6 | `private static long hashOf(double[] a)` _(in CityMap.Drawn)_ | ...doubles. |
| 2550 | 1 | `public int joinsOut()` _(in CityMap.Drawn)_ |  |
| 2551 | 1 | `public int partedOut()` _(in CityMap.Drawn)_ |  |
| 2552 | 1 | `public int seamsSurfaced()` _(in CityMap.Drawn)_ |  |
| 2559 | 20 | `long bytes()` _(in CityMap.Drawn)_ | The bytes it holds, about: what the view's budget counts. |
| 2581 | 5 | `public long[] buildingsByType(int types)` _(in CityMap.Drawn)_ | Its plan's buildings, by type id: the boxes counted. |
| 2588 | 6 | `public byte codeAt(int x, int y)` _(in CityMap.Drawn)_ | The street code its plan lays on plot (x, y) of its frame: the district's 256 x 256 and its east column and south row (x or y = DISTRICT); 0 for none. |
| 2596 | 6 | `byte codeBase(int x, int y)` _(in CityMap.Drawn)_ | ...its plan's alone, the streets laid to the packed buildings (laid) left out: what the trucks' trips look for their way over (MF1). |
| 2604 | 1 | `static byte codeIn(byte[] t, int x, int y)` _(in CityMap.Drawn)_ | A packed tile's code at plot (x, y) of it. |
| 2607 | 3 | `static void unpack(byte[] t, byte[] out)` _(in CityMap.Drawn)_ | A packed tile's codes, a byte a plot row by row, into out. |
| 2612 | 14 | `static byte[] pack(byte[] plots)` _(in CityMap.Drawn)_ | A tile's codes (a byte a plot row by row) packed by row: each row's pattern once, in the order first met. |
| 2628 | 1 | `public double surface(int kind)` _(in CityMap.Drawn)_ | Its surface in plots of kind (BuildingVisual's GRAVEL to HIGHWAY). |
| 2630 | 1 | `public int streets()` _(in CityMap.Drawn)_ | The plots of street it lays, and those of them tracks. |
| 2631 | 1 | `public int tracks()` _(in CityMap.Drawn)_ |  |
| 2633 | 1 | `public int placed()` _(in CityMap.Drawn)_ | Buildings its plan placed, and refused (carried on). |
| 2634 | 1 | `public int refused()` _(in CityMap.Drawn)_ |  |
| 2636 | 1 | `public double surplus()` _(in CityMap.Drawn)_ | Road it has no street for, in plots; of a kind. |
| 2637 | 1 | `public double leftover(int kind)` _(in CityMap.Drawn)_ |  |
| 2641 | 9 | `static byte paintCode(short code)` | A plan's street code as the painter reads it (TilePainter's S_ codes): DistrictPlan's kind and width, its role - an arterial, a boulevard's, or a street (a cell's, along a cut, the join's) - and a bridge. |
| 2652 | 97 | `static Drawn drawnOf(DistrictPlan p, long stamp)` | A plan packed for the painter, with its stamp. |
| 2759 | 35 | `static int[][] accessWays(DistrictPlan p)` | A plan's access roads as ways of plots (FR5, slice B; Drawn.access): for each road the plan laid (DistrictPlan.accessRoads, those that have a gate and an end - a work that already stood on one has neither) the shortes... |
| 2796 | 3 | `static int box(int x, int y, int w, int h, boolean packed)` | A box packed into an int: x, y, w - 1 and h - 1 at five bits each, PACKED_BIT when packed at the city's edge. |
| 2801 | 24 | **type** `static final class Link` | A district's link in the chain (R7): the stamp of its own inputs, what was carried to it and what it carried on ({month, code} pairs in the order they came, since CL3 - the month each came in, so the spill keeps its t... |
| 2817 | 5 | `Link(long own, int[] in, int[] out, long[] free, long[] streets, byte[][] margins, long marginsHash, District[] earlier, long[]...` _(in CityMap.Link)_ |  |
| 2823 | 1 | `long stamp()` _(in CityMap.Link)_ | Its plan's stamp: its own inputs and what came in. |
| 2827 | 5 | `int linksAstray()` | How many of the links kept bear a stamp other than stampOf()'s of their own inputs and carry worked out afresh, or a margins' hash other than marginsHashOf()'s of their margins: none (batch UP3, UP5; MapCheck 18's - L... |
| 2834 | 9 | `static long marginsHashOf(byte[][] m)` | A plan's margins hashed, all five arrays (Link.marginsHash; batch UP5): everything the plans after it read of it (readNeighbours()), so a link's `read` holds these and a plan made again whose margins did not move leav... |
| 2845 | 8 | `static long stampOf(long own, int[] in)` | A plan's stamp: its own inputs' and the carry in's, {month, code} pairs (CL3): the list's length and every entry folded, zeros included, in order - so two different lists never stamp alike, a list of none and no list ... |
| 2859 | 21 | `static int[] countsOf(int[] pairs, int types)` | A carry's {month, code} pairs as counts by type (CL3): the arrivals (code >= 0, a type id) less the departures (code < 0, ~k: the building the k-th pair brought, so the type of that pair) - what the packing at the edg... |
| 2886 | 16 | `static byte[][] marginsOf(DistrictPlan p)` | A plan's frame edges' street codes (M_WEST to M_SOUTH) and which of them are parted (M_PARTED). |
| 2910 | 8 | `District[] earlierOf(District d)` | The districts about d (AROUND's order) that come before it in the map's order, in its band (0.7.89; CHAIN_BAND), null for the rest: whose plans its plan reads - which of its seams they leave unlaid, and where its stre... |
| 2920 | 4 | `static byte marginAt(byte[][] m, int x, int y, int ox, int oy)` | Where district d's frame plot (x, y) lies in the frame of its neighbour at offset (ox, oy), read from that plan's margins - a plot of its frame's edge - or 0 when it is not on them. |
| 2926 | 4 | `static boolean partedAt(byte[][] m, int x, int y, int ox, int oy)` | ...whether the street there is parted from the network (M_PARTED); false where it is not on them. |
| 2932 | 9 | `private static int marginOf(int x, int y, int ox, int oy)` | ...which margin and place: the margin (M_WEST to M_SOUTH) << 16 \| the index along it, or -1. |
| 2954 | 46 | `static void readNeighbours(District d, District[] earlier, byte[][][] margins, DistrictPlan.Input in)` | What district d's plan reads of the plans before it (0.7.88), from their frames' edges (margins, AROUND's order; null for a district after it or none): ONE-SIDED SEAMS, its seams no district before it sharing them lay... |
| 3001 | 3 | `private static void open(DistrictPlan.Input in, int x, int y, byte code)` |  |
| 3006 | 21 | **type** `static final class Pack` | What the city has no room for, packed at its edge (R7): each edge district's packed boxes by tile, an int each as Drawn.boxes; how many were packed, how many drawn smaller than their own land, and how many found no gr... |
| 3013 | 11 | `long digestOf(District d)` _(in CityMap.Pack)_ | A hash of what the packing holds for district d (batch UP5, tileKey()): its packed buildings' boxes and the streets laid to them, by tile. |
| 3069 | 1 | `static int band(int i)` | The band of the district at index i in the map's order. |
| 3072 | 1 | `int bands()` | Bands in the map's order. |
| 3092 | 3 | `private long planEpoch()` | What the plans hang on, cheaply: the map's changes, and the land's purchases (a purchase owns ground at once, before the month measures it; the land drawn again draws a new map). |
| 3097 | 4 | `boolean isEdge(District d)` | Whether district d is at the city's edge: a neighbour of its eight is not on the map - where what the city has no room for is packed (R7). |
| 3116 | 14 | `long ownStamp(District d)` | A stamp of everything district d's own plan is drawn from but what is carried to it: its buildings and road by type (its mines and wells on sites apart) and the log they came in (CL3), the ground the city owns over it... |
| 3132 | 1 | `long[] ownStampCalls()` | How many own stamps were asked for since the map was drawn, and of those how many were worked out ({asked, worked out}): a harness's. |
| 3140 | 12 | `int[] ownStampsKept()` | How the own stamps kept stand (batch UP5; a harness's): {districts with one kept, those of them whose key is unmoved so that ownStamp() would hand the kept one out, and of those, how many differ from ownStampFresh() -... |
| 3168 | 30 | `private long ownKey(District d)` | A key of everything district d's own stamp is worked out from (ownStampFresh(), line by line), itself worked out from the version counters those inputs move and the few values that have none: equal keys, equal stamps ... |
| 3200 | 8 | `private long ownGlobalKey()` | The part of ownKey() that is the same for every district: the land, the landmasses, the site lists' generation, the runs and the shore. |
| 3210 | 40 | `long ownStampFresh(District d)` | ownStamp() worked out afresh, the kept one bypassed: what it is checked against. |
| 3252 | 11 | `private void sizeBands()` | The bands' arrays sized to the districts, every band to be checked again. |
| 3265 | 4 | `private void bandsStale()` | Every band to be checked again (a plan adopted). |
| 3271 | 22 | `private void checkBand(int b)` | Band b's links walked in the map's order at the current epoch: its bandValidTo is the first whose own stamp moved, whose carry in is not the one before's carry on (none for the band's first), or (an edge district) tha... |
| 3295 | 8 | `private void useBand(int b)` | Marks band b used, letting go of the bands used longest ago past BANDS_KEPT. |
| 3305 | 11 | `private void forgetBand(int b)` | Lets go of band b's links, plans and packing: drawn again when a screen asks for it. |
| 3318 | 8 | `private boolean allDrawn()` | Whether every band's links hold and its packing is current: the whole city drawn. |
| 3328 | 8 | `private int[][] packedIn(District d)` | District d's band's packing of what the band cannot hold, by tile, when current; null for none. |
| 3338 | 10 | `private boolean readHolds(Link l)` | Whether the edges of the plans a link read its neighbours' from are those standing: each such district's link bears the margins it read (they are before it in the map's order, walked first). |
| 3350 | 11 | `public boolean planned(District d)` | Whether district d's plan is drawn and current, and every link before it in its band holds; for an edge district, its band's packing too: its tiles paint without planning. |
| 3363 | 16 | **type** `public abstract static class Job` | A job to run away from the screen's thread (0.7.88): run() on any one thread, in the order handed out, then adopt() on the map's. |
| 3368 | 1 | `Job(CityMap map, long epoch)` _(in CityMap.Job)_ |  |
| 3370 | 5 | `public final synchronized void run()` _(in CityMap.Job)_ | Does the work: any thread, the jobs handed out in their order on one; safe to call twice. |
| 3375 | 1 | `abstract void work()` _(in CityMap.Job)_ |  |
| 3377 | 1 | `public final synchronized boolean done()` _(in CityMap.Job)_ | Whether it has run. |
| 3394 | 145 | **type** `public static final class PlanJob extends Job` | One district's plan: its inputs gathered on the map's thread, its ground read and its plan drawn by run(), with what the job before it carried on (R7). |
| 3427 | 4 | `PlanJob(CityMap map, District d, DistrictPlan.Input in, long own, PlanJob after, int[] given, boolean edge, District[] earlier,...` _(in CityMap.PlanJob)_ |  |
| 3433 | 10 | `PlanJob(CityMap map, District d, DistrictPlan.Input in, long own, PlanJob after, int[] given, boolean edge, District[] earlier,...` _(in CityMap.PlanJob)_ | A job with an input gathered, that takes its district's link (`old`, and its plan `oldDrawn`: both may be null) where it can. |
| 3445 | 9 | `PlanJob(CityMap map, District d, Link old, Drawn oldDrawn, long own, PlanJob after, boolean edge, District[] earlier, Object[] ...` _(in CityMap.PlanJob)_ | A lazy job (see above): `old` is its district's link, `oldDrawn` its plan or null, `after` the job before it in the chain (never null: the first that broke is planned). |
| 3456 | 16 | `private long[] readSources(byte[][][] m)` _(in CityMap.PlanJob)_ | Runs the jobs it reads (any thread, in the chain's order) and gives a hash of each one's margins, and their margins into m unless that is null; null when one of them made no plan. |
| 3474 | 12 | `private void takeOld()` _(in CityMap.PlanJob)_ | Its link's work taken as the job's own, nothing planned. |
| 3487 | 42 | `void work()` _(in CityMap.PlanJob)_ |  |
| 3531 | 1 | `public double ms()` _(in CityMap.PlanJob)_ | How long it took, in ms: a harness's figure (its plan's; its picture's apart, pictureMs()). |
| 3534 | 1 | `public double pictureMs()` _(in CityMap.PlanJob)_ | How long its picture took, in ms - its plots, and its pixels when it drew them: a harness's figure. |
| 3537 | 1 | `public boolean drewPicture()` _(in CityMap.PlanJob)_ | Whether it drew a picture (the one kept was not of its plots): a harness's figure. |
| 3541 | 55 | **type** `public static final class PackJob extends Job` | A band's packing at its edge (R7; the city's, in a city of one band): what its last link carried on, into its edge districts' free ground - their links', or the jobs' that make them. |
| 3551 | 4 | `PackJob(CityMap map, District[] edges, Object[] from, Object last, long[] tileOrder, long epoch)` _(in CityMap.PackJob)_ |  |
| 3556 | 39 | `void work()` _(in CityMap.PackJob)_ |  |
| 3605 | 8 | `public List<Job> planJobs(District d)` | The jobs that make district d's plan current (R7: every district before it in the map's order whose link does not hold, each after the one before it), and, for an edge district, the city's packing after them: to run i... |
| 3615 | 50 | `private PlanJob chainTo(int i, List<Job> out)` | The plan jobs up to the district at index i from its band's first, into out; the last job of the chain to it (handed out now or before), or null when its link holds. |
| 3667 | 1 | `long[] chainJobs()` | How the chain's jobs came out since the map was drawn, as adopt() kept them (batch UP5): {plans made, links taken as they stood, jobs that could do neither, plans made that were not needed}. |
| 3670 | 8 | `java.util.Map<District, Object[]> linkSnapshot()` | The links kept as a harness compares them month to month (batch UP5): each district's {own, carry in, carry out, read, margins' hash, the link}. |
| 3680 | 1 | `Drawn drawnKept(District d)` | District d's plan as kept, null where it was let go (the plans kept are a harness's to compare, never made here). |
| 3683 | 5 | `void dropLink(District d)` | District d's link and plan let go, as a band's forgetting lets go all of its: a harness's, to see the chain plan one district and take the links of those after it (batch UP5, MapCheck 18). |
| 3690 | 1 | `void epochMoves()` | The map's epoch moved (planEpoch()) with nothing else: a harness's, for a chain handed out at a new epoch, the first time lazy. |
| 3693 | 4 | `long[] linkDigest(District d)` | District d's link's margins hash, carry out and stamp as {stamp, margins' hash, hash of the carry out}, null for no link: a harness's. |
| 3699 | 10 | `private Object[] sources(District[] earlier)` | Each earlier district's plan as a job reads it: its job handed out at this epoch, else its link (it is before the job's district in the map's order, so one or the other holds). |
| 3711 | 24 | `private void packAfter(int b, List<Job> out)` | Band b's packing at its edge, after the chain to its last district, into out - unless it is current or handed out at this epoch. |
| 3737 | 21 | `private long[] tileOrder(List<District> edges)` | The edge districts' tiles in the order the packing spreads over them: each a district's index in `edges` and the tile, the farthest tile from the founding site first. |
| 3760 | 55 | `public boolean adopt(Job j)` | Keeps a job's work: a plan's link and its packed plan, the city's packing. |
| 3817 | 16 | `Drawn drawnOf(District d)` | District d's plan for the painter, made here when it is not current - every district before it in the map's order first where its link does not hold, and for an edge district the city's packing - on this thread: a har... |
| 3835 | 6 | `private void useLaid(District d, Drawn got)` | Takes up, into the plan got, the streets its band's packing has laid in district d (MF1): a plan lives past a packing, so it keeps which packing it took them from and takes the new one's when that moves. |
| 3843 | 7 | `void packed()` | Every band's packing at its edge, made here when it is not current (a harness's): the whole city drawn. |
| 3852 | 11 | `private int[] packSums()` | The bands' packings summed: {packed, smaller, none}; null when one is not current. |
| 3865 | 5 | `public int[] packedCounts()` | How many buildings the city has no room for in any district's plan, packed at its edge (R7; since 0.7.89 at its band's edge) - and how many of them drawn smaller than their own land or with no ground at all: {packed, ... |
| 3872 | 3 | `public int[] packedCountsIfDrawn()` | ...the same, or null when the packing is not current: the screen's thread, which never plans. |
| 3877 | 16 | `int[] packedWays()` | How the packed buildings stand (MF1), summed over the bands: {against a street, within REACH of one, at the end of a street laid to them, in the field (no street near), the streets laid, their plots, the longest}; eve... |
| 3895 | 7 | `public long[] legendFiguresIfDrawn()` | The legend's figures once every plan and the packing are drawn (never planned here: the screen's thread): {buildings packed at the city's edge, plots of road its streets do not carry (star RD2-4) - since 0.7.89 with t... |
| 3904 | 4 | `long dealtSpurTrack()` | The track plots of the Rail Spurs dealt to the plans' works (sidingsOf()), a spur's share of the model's spur track each: used by a siding the plans lay, so no longer the legend's "track more than the line uses" (batc... |
| 3910 | 10 | `public double[] leftoverByKind()` | The road the plans have no street for, by kind [0, gravel, paved, highway], over every district (made now if need be), and (0.7.89) the Elevated Highway plots the runs could not lay: a harness's - with the surface dra... |
| 3922 | 4 | `public long[] legendFigures()` | ...the same, made now if need be: a harness's. |
| 3928 | 1 | `public Drawn drawn(District d)` | District d's plan as drawn (made now if need be): a harness's and a probe's. |
| 3931 | 5 | `public int[][] carried(District d)` | What was carried to district d and what it carried on (R7), counted by type id, null for none - its link's, once planned. |
| 3943 | 5 | `public int[] carriedLog(District d)` | What was carried to district d as {month, code} pairs, in the order they came (CL3; Link.in), null for none - its link's, once planned. |
| 3950 | 1 | `int linksKept()` | The links kept (0.7.89: a band's let go past BANDS_KEPT): a harness's. |
| 3953 | 5 | `public long plansBytes()` | The bytes the kept plans hold: what MapTiles' budget counts. |
| 3966 | 25 | `static long[] freeGround(DistrictPlan.Input in, DistrictPlan p)` | A plan's free ground (R7), for an edge district: a bit a plot of the district's 256 x 256, set where the city owns ground a building may stand on (ET2: World.buildable(), dry and flat or foothill) no street, building,... |
| 4020 | 11 | `static long[] streetsOf(DistrictPlan p)` | The plots of a plan's streets (MF1), for an edge district: a bit a plot of the district as freeGround() has them, set where the plan lays a street of any kind but one beneath a highway - the streets a building is with... |
| 4033 | 25 | `static long[] dilate(long[] src, int steps)` | A district's plot mask grown by `steps` plots every way, across corners (a plot within `steps` of a set one, as the plan's reach is). |
| 4059 | 3 | `private static boolean bit(long[] m, int x, int y)` |  |
| 4063 | 5 | `private static void set(long[] m, int x, int y, boolean on)` |  |
| 4070 | 7 | `private static boolean anySet(long[] m, int x, int y, int w, int h)` | Whether any plot of the w x h box at (x, y) is set in m. |
| 4079 | 12 | **type** `private static final class Ground` | An edge district's ground as the packing works it: its free plots and its streets, the plots against a street and within REACH of one, and within LAID_MOST + 1 of one, where a street may be laid from (all as freeGroun... |
| 4083 | 7 | `Ground(long[] free, long[] street)` _(in CityMap.Ground)_ |  |
| 4093 | 28 | **type** `private static final class Fails` | The shapes each tile is known to hold no box of, at one way of standing: free ground only shrinks, so a shape that did not fit does not later (until a street is laid near the tile, which clear() answers). |
| 4097 | 5 | `Fails(int tiles)` _(in CityMap.Fails)_ |  |
| 4103 | 7 | `boolean holds(int ti, int ww, int hh)` _(in CityMap.Fails)_ |  |
| 4111 | 7 | `void add(int ti, int ww, int hh)` _(in CityMap.Fails)_ |  |
| 4119 | 1 | `void clear(int ti)` _(in CityMap.Fails)_ |  |
| 4123 | 1 | **type** `private record Spot(int di, int k, int x, int y, int w, int h, int[] laid)` | Where a packed building stands: district (an index in the edges), tile, its box in the tile's plots, and the street laid to it (plots of the district, y x DISTRICT + x, from the box outward; null for none). |
| 4139 | 91 | `static Pack packAtEdge(int[] carry, BuildingVisual.Type[] types, District[] edges, long[][] free, long[][] streets, long[] tile...` | PACKED AT THE CITY'S EDGE (R7; spec 2.6: "a city with no room anywhere packs them at its edge, without a street, and the map's legend says how many"; since MF1 beside a street, above). |
| 4232 | 14 | `private static Spot place(Ground[] gr, long[] tileOrder, Fails[] fails, int[] cursor, int lo, int hi, int w, int h)` | The first spot for a w x h building (or h x w) in the ways lo to hi, each way over the tiles from the cursor's: the tile the last of its kind took first. |
| 4254 | 50 | `private static Spot fitTile(Ground[] gr, long key, int w, int h, int way, Fails fails, int ti)` | The first spot, in rows, on tile key (edge index << 32 \| tile) of the free ground where a w x h box - or h x w - fits inside the tile's interior and stands the way asked: against a street, within REACH of one (some pl... |
| 4306 | 3 | `private static int rowOf(long[] m, int bx, int y)` | A tile's row y (a plot row of the district) of mask m, its 32 plots as the bits of an int: bit x the plot x from the tile's west edge bx. |
| 4311 | 9 | `private static int runOf(int row, int w, boolean all)` | A row's w-wide runs: bit x set where all of plots x to x + w - 1 are set (`all`), or where any is. |
| 4322 | 8 | `private static void down(int[] rows, int h, boolean all)` | The rows of a tile in place as the and (`all`) or the or of themselves and the h - 1 rows below: row y holds what rows y to y + h - 1 do, and none where they run off the tile. |
| 4337 | 14 | `private static int[] laidTo(Ground g, int x, int y, int w, int h)` | The street laid to the w x h box at (x, y) of a district (MF1): straight out from a side - west, east, north, south, from the middle of the side and then the plots beside it - over free ground, to the first street it ... |
| 4353 | 19 | `private static int[] walkOut(Ground g, int sx, int sy, int dx, int dy)` | ...one walk of it: from (sx, sy) in direction (dx, dy). |

### THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md (lines 4373-4477)

| line | len | member | says |
|---:|---:|---|---|
| 4400 | 18 | `int[] extrasIn(District d)` | The boxes the runs and the shore stand on district d's ground, as the painter draws them over its plan (fillInput()): its yards as Rail Terminals and its terminals and tank farms, each {x, y, w, h, type id} in the dis... |
| 4420 | 10 | `public DistrictPicture pictureIfCurrent(District d)` | District d's picture when it is its plan's, the plan current - every link before it in its band holding - else null: the screen's thread, which never plans. |
| 4439 | 16 | `public List<Job> pictureJobs(District d)` | The jobs that draw district d's picture: its plan's chain, as planJobs() hands it out but for its band's packing at the edge, which a picture does not draw (star MM1-3); or, where its plan is current and its picture i... |
| 4457 | 11 | `public DistrictPicture picture(District d)` | District d's picture, drawn here when it is not current (pictureJobs() run and kept on this thread): a harness's and a probe's. |
| 4470 | 1 | `public long picturesKept()` | How many pictures have been kept since the map was drawn (picturesKept). |
| 4473 | 1 | `public int picturesHeld()` | The pictures kept now: what MapTiles' budget counts PICTURES_KEPT of, DistrictPicture.BYTES each. |
| 4476 | 1 | `public static int bandOf(District d)` | The band district d's links are kept in (CHAIN_BAND districts of the map's order a band): what a far view counts the bands it would plan by (MapTiles.picturesFit()). |

### THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch (lines 4478-4708)

| line | len | member | says |
|---:|---:|---|---|
| 4529 | 9 | `private CityRuns guarded(CityRuns r)` | The runs given the works an interchange's reserve may not take (batch FR7, InterchangeGround.Guard): the mines on their sites and the shore's works. |
| 4546 | 19 | `boolean reserveBlocked(long x0, long y0, long x1, long y1)` | Whether a mine on its site or a work on the shore stands in the box (x0, y0) to (x1, y1), inclusive: ground an interchange's reserve may not take. |
| 4567 | 1 | `public CityRuns runs()` | The city's runs. |
| 4570 | 1 | `public int gateway()` | The gateway's bearing, as CityRuns keeps it: GATEWAY_UNKNOWN until it is looked for, NO_GATEWAY where the land has none. |
| 4585 | 1 | **type** `public record WorldLine(int net, double x, double y, int bearing, int arm, int way, double reach)` | A net's line out to the world (spec-freight 4.1): the highway's or the railway's run, from the first plot past the city's ground, faded to the edge of the view and labelled "to the world". |
| 4611 | 14 | `public List<WorldLine> worldLines()` | The world lines (spec-freight 4.1): for each net whose gateway is found and which has started, the line to the world that worldLine() finds - where an arm's track ends at the city's edge on dry ground, and the way out... |
| 4627 | 1 | **type** `private record WayOut(double score, int arm, int which, int way, long ex, long ey)` | One way out of the city from an arm at its edge: the score it is tried by, the arm, 0 for the gateway's bearing or 1 for the arm's heading, the degrees, and the edge plot. |
| 4641 | 45 | `private WorldLine worldLine(CityRuns.Net n, int gateway, List<WorldLine> out)` | Adds net n's line to the world to out and returns it; null (none added) for a net that has not started, for the highways while they reach no street (CityRuns.highwaysReach(), FX2: they are not drawn), or that has no a... |
| 4691 | 17 | `private static double dryRun(World w, double x, double y, int deg, double most, int fromStep)` | How far from (x, y) the way out along deg degrees runs over dry ground, in plots: the distance to the first point, read every WORLD_STEP, that lies on a plot that is not World.dry(); `most` where none does. |

### THE TRAINS' ROUTES (0.8.2, batch FR1; spec-freight 4.4, D16) (lines 4709-5147)

| line | len | member | says |
|---:|---:|---|---|
| 4720 | 1 | **type** `private record PlanSidings(long stamp, int[][] ways)` | A district's plan's sidings' ways (Drawn.sidingWays) with the stamp of the link they came with (MF1): kept when its plan is adopted, so the trains' routes do not wait on a plan being kept drawn (PLANS_KEPT); valid whi... |
| 4726 | 1 | **type** `private record DrawnBend(TrackCurves.Bend bend, boolean doubled)` | A bend of the routes the tiles draw (FX5), with whether it lies on double track (its corner plot's M_DOUBLE). |
| 4732 | 1 | `private static long bendKey(long tx, long ty)` | A tile's key in trainBends. |
| 4767 | 47 | `public List<TrainSchedule.Route> trainRoutes()` | The trains' routes (TrainSchedule.Route): for each yard the railway's way reaches, from TrainSchedule.WORLD_PLOTS out along the railway's world line (along line.way(), over dry ground: FX2), in to the main arm's end (... |
| 4824 | 25 | `private Map<Long, List<DrawnBend>> drawnBends(List<TrackCurves.Bend> found, java.util.Set<Long> sidingPlots)` | The bends the routes make (TrackCurves.bends() of each route's points before it is rounded) that the tiles draw (FX5), by the tiles they reach: those whose corner and both tangent points lie on plots the runs lay trac... |
| 4866 | 29 | `private List<TrackCurves.Bend> highwayBendsIn(long tx, long ty)` | The highways' bends that reach tile (tx, ty) (FX5 slice D): the turns of each arm's polyline (CityRuns.highwayPolylines()) rounded by TrackCurves.bends() at TrackCurves.HIGHWAY_RADIUS, of net HIGHWAY - a 45-degree str... |
| 4897 | 4 | `private boolean highwayPlot(long x, long y)` | Whether the runs lay highway on plot (x, y): a highway, or a highway under the railway's bridge. |
| 4903 | 13 | `private boolean railPlot(List<long[]> yards, boolean[][] yardRails, java.util.Set<Long> sidingPlots, long x, long y)` | Whether the runs lay track on plot (x, y): track, track bridging a highway, a plot of a district plan's siding (MF1; sidingPlots), or the track in a yard's box (yardRails caches each yard's, by index). |
| 4918 | 1 | **type** `private record YardLayouts(long at, int[][] by)` | The yard layouts (TileRaster.yardLayout() of yardRail()) as the map's changes left them: valid at `at`, one per yard of the runs, made when first asked. |
| 4927 | 15 | `int[] yardLayoutOf(int yi)` | Where yard yi's fan lies in its box (FX5 Y2): TileRaster.yardLayout() of the box's track as the runs lay it (yardRail()), the layout trainRoutesInto() lays the routes on and the tiles draw the yard from. |
| 4944 | 8 | `static boolean[] yardRail(CityRuns runs, long[] yard)` | The track in a yard's box, plot by plot (row by row, yard[2] across and yard[3] down): where the runs lay the railway, as the tile the yard is drawn on carries it (TileRaster.yardLayout()'s input; FX3, 0.8.4). |
| 4967 | 4 | `static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way, double fx,...` | Adds one yard's routes to out: the world line's part (far end, then where it leaves the city's ground), the way to the box's edge, and the ladder onto each siding. |
| 4973 | 4 | `static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way, double fx,...` | ...with the yard's own number of sidings (TileRaster.tracksOf(): a Rail Depot's three, FR7 slice 3), one route to each. |
| 4985 | 5 | `static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way, double fx,...` | ...and (FX5) the bends of the routes' points before they are rounded (TrackCurves.bends()) added to bendsOut, where it is not null: the turns the tiles draw. |
| 4995 | 111 | `static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way, double fx,...` | ...and (FX5, slice T) with the yard's first `standing` sidings from the shed's side left without a route, the wagons standing on them (TileRaster.wagonTracksOn()): the routes are those of the sidings after, each with ... |
| 5108 | 1 | **type** `private record Rounded(double[] xs, double[] ys, double fadeTo)` | A polyline's points rounded by TrackCurves (RADIUS), with the world line's part: how far along the rounded polyline its first straight lasts. |
| 5118 | 10 | `private static Rounded rounded(List<double[]> pts, List<TrackCurves.Bend> bendsOut)` | The route's points rounded (FX5): every turn replaced by its arc of TrackCurves.RADIUS plots, less where the legs beside it are short; the turns found are added to bendsOut where it is not null. |
| 5130 | 5 | `private static void addNew(List<double[]> pts, double x, double y)` | Adds the point (x, y) to pts unless the last one is there (no leg of no length). |
| 5137 | 4 | `private static TrainSchedule.Route route(int yard, boolean station, int siding, List<double[]> pts, List<TrackCurves.Bend> bend...` | A route through points, rounded at every turn (FX5), the world line's part its first leg's straight. |
| 5143 | 4 | `private static TrainSchedule.Route routeToWorks(List<double[]> pts, List<TrackCurves.Bend> bendsOut, double holds)` | ...to a works' siding (FR7 slice 3): through the points, rounded, the world line's part its first leg's straight, no yard; a bulk unit train's alone. |

### the plans' sidings' trains (MF1, 2026-10-28) (lines 5148-5192)

| line | len | member | says |
|---:|---:|---|---|
| 5160 | 32 | `private void planSidingRoutes(List<TrainSchedule.Route> out, List<TrackCurves.Bend> found, java.util.Set<Long> sidingPlots, dou...` | Adds a route to every siding a district's plan laid (DistrictPlan.sidings(), kept at adopt() against its link): from the world line along the main line to the railway plot the siding meets, then through its corners to... |

### THE TRUCKS' ROUTES (0.8.8, batch FR5; spec-freight 4.7, D21) (lines 5193-5391)

| line | len | member | says |
|---:|---:|---|---|
| 5247 | 27 | `public List<TruckSchedule.Route> truckRoutes()` | The trucks' highway routes (TruckSchedule.Route, world true): for each ramp the highways have (CityRuns.highwayRamps(): their arms' ends), from TruckSchedule.WORLD_PLOTS out along the highways' world line (along line.... |
| 5276 | 5 | `private static TruckSchedule.Route truckRoute(boolean world, List<double[]> pts, double fadeTo)` | A truck route through points, fadeTo plots faded in. |
| 5283 | 1 | **type** `public record DistrictRoutes(long key, List<TruckSchedule.Route> routes)` | One district's access roads' routes, with the stable key it is told apart by: the district's own, the same every call. |
| 5286 | 4 | **type** `private static final class AccessRoutes` | A district's routes as last made, and what they were made against: its Drawn's stamp and its frame's origin in world plots. |
| 5308 | 18 | `public List<DistrictRoutes> accessRoutesIn(long x0, long y0, long x1, long y1)` | The access roads' routes (TruckSchedule.Route, world false) of the districts already drawn whose frame meets the box (x0, y0) to (x1, y1), in world plots, the corners inclusive: for each district its roads, each from ... |
| 5328 | 21 | `private void accessRoutesOf(District d, List<DistrictRoutes> out)` | Adds district d's access routes to out when its plan is drawn and current and it has any. |
| 5351 | 14 | `private static void accessRouteInto(List<TruckSchedule.Route> out, int[] way, long ox, long oy)` | Adds the route of one access road (Drawn.access: frame indices, the end first) to out: its plots' middles in world plots from the frame's origin (ox, oy), a straight run kept to its two ends. |
| 5375 | 12 | `public int accessRoadCount()` | How many works the city has that need an access road (batch FR7, D17): the railway's yards the runs draw, the shore's berthed works (sea terminals) and the Truck Depots among its buildings - read from what the map hol... |

### the trips (batch FR9) (lines 5392-6008)

| line | len | member | says |
|---:|---:|---|---|
| 5413 | 1 | `private static long plot(long x, long y)` | A plot packed into a long (x high, y low), and none. |
| 5414 | 1 | `private static long plotX(long p)` |  |
| 5415 | 1 | `private static long plotY(long p)` |  |
| 5419 | 1 | **type** `public record DistrictTrips(long key, District district, List<TruckSchedule.Route> trips)` | One district's trips (TruckSchedule.Route: each road's world trip, then its home trip, road by road) with the stable key it is told apart by, the district's own, and the district: the tonnes its trips carry are its ca... |
| 5422 | 15 | **type** `private static final class TripRoad` | One access road as the trips see it: its way (Drawn.access, in its frame's plots, the end first and the gate last), where that frame starts, and, once the search settled it, the ramp its street leg comes from and the ... |
| 5428 | 8 | `TripRoad(int[] way, long ox, long oy)` _(in CityMap.TripRoad)_ |  |
| 5439 | 5 | **type** `private static final class TripLegs` | A district's street legs as last made, and what they were made against: its plan's stamp, the stamps of the drawn plans its box meets, the runs over the box, and the arm the highways' line leaves from. |
| 5446 | 1 | **type** `private record TripPlan(District d, Drawn dr, long ox, long oy)` | A drawn, current plan and where its frame starts, in world plots. |
| 5492 | 55 | `public List<DistrictTrips> tripsDrawn()` | The trucks' trips (FR9; TruckSchedule.Route) of the districts whose plan is drawn and current, by district key, those with a trip. |
| 5549 | 8 | `private static long metStamps(List<TripPlan> plans, TripPlan p)` | A fold of the keys and stamps of the drawn plans whose frame meets p's search box, in key order: what its street legs were read from. |
| 5559 | 3 | `private static boolean meetsBox(TripPlan q, long x0, long y0)` | Whether plan q's frame meets the search box starting at world plot (x0, y0). |
| 5573 | 4 | `void forgetTrips()` | Forgets the street legs kept and the trips asked last, so the next tripsDrawn() makes them all again: a harness's, to time a warm remake. |
| 5581 | 1 | `int tripSearches()` |  |
| 5584 | 8 | `long tripScratchBytes()` | The bytes the street search's scratch holds (0 before the first search). |
| 5597 | 81 | `private void tripSearch(TripPlan p, List<TripPlan> plans, WorldLine line, TripLegs legs)` | The street legs of one district's roads (legs.roads), settled from the ramps in its search box by a Dial's search over the drawn plans' streets; each road's ramp and leg are set when a ramp reached its end. |
| 5683 | 11 | `private int tripCostOf(int i, long bx0, long by0, int bw)` | What it costs a truck to drive box plot i (the world plot (bx0 + i % bw, by0 + i / bw)): 1 on an arterial, a boulevard or an access road, TRIP_STREET_COST on any other street, 0 where no street is drawn. |
| 5696 | 6 | `private void tripPush(int d, int a)` | Queues plot a at distance d in its bucket (d modulo the buckets: an edge costs at most TRIP_STREET_COST, one more buckets than that are Dial's whole circle). |
| 5707 | 76 | `private void tripTrips(List<TripPlan> plans, Map<Long, TripLegs> kept, WorldLine line, List<DistrictTrips> out)` | The trips of every district in `plans` whose street legs are in `kept`, into out: the world trips, and the home trips among the roads whose legs come to one ramp. |
| 5785 | 5 | `private static TruckSchedule.Route homeRoute(List<double[]> pts)` | A home trip through points, TRUCK_ACCESS_FADE faded in. |
| 5792 | 3 | `private static void addLeg(List<double[]> pts, long[] leg, int from, int to, int step)` | Adds the middles of leg plots from index `from` to `to` (inclusive) stepping `step`, no point twice running. |
| 5797 | 5 | `private static void addWay(List<double[]> pts, int[] way, long ox, long oy, boolean reversed, int from)` | Adds the middles of an access way's plots (Drawn.access: frame indices, the end first) in the world from frame origin (ox, oy): from index `from` to the gate, or, when `reversed`, the gate to the end. |
| 5803 | 7 | `private static void addPoint(List<double[]> pts, double x, double y)` |  |
| 5812 | 3 | `private static void appendStraight(List<double[]> out, List<double[]> piece)` | Adds the points of `piece` straightened (straight()) to out, none twice running. |
| 5817 | 12 | `private static List<double[]> straight(List<double[]> pts)` | The points with those dropped that lie on the straight line between their neighbours, going on: a run along a row or a column is kept to its two ends. |
| 5834 | 12 | `int[] tripRoadCounts()` | What the trips made of the roads of the districts last asked (tripsDrawn()): {roads, roads whose leg is a ramp alone (the road ends at one), roads with a street leg of more than a plot, roads no ramp reached}. |
| 5860 | 143 | **type** `private final class RunGround implements CityRuns.Ground` | The ground the runs read (CityRuns.Ground): a plot's World class where the city owns it, -1 where it does not - the terrain a tile's row or column at a time (World.lineTerrain(), byte for byte the painter's tileTerrai... |
| 5882 | 3 | `public int at(long x, long y, int dir)` _(in CityMap.RunGround)_ |  |
| 5891 | 3 | `int atRaw(long x, long y, int dir)` _(in CityMap.RunGround)_ | ...with the ownership unmasked (ET5): a plot the city owns whatever landmass it lies on, as at() reads it where the city is on one landmass. |
| 5895 | 35 | `private int read(long x, long y, int dir, boolean masked)` _(in CityMap.RunGround)_ |  |
| 5932 | 32 | `private boolean ownedAt(long tx, long ty, int ix, int iy)` _(in CityMap.RunGround)_ | Whether the city owns plot (ix, iy) of tile (tx, ty): the grid's cover of the tile, its leaves where it owns some. |
| 5966 | 23 | `private boolean rawOwnedAt(long tx, long ty, int ix, int iy)` _(in CityMap.RunGround)_ | Whether the city owns plot (ix, iy) of tile (tx, ty), whatever landmass it is on: ownedAt() without the island rule's mask (ET5; atRaw()). |
| 5990 | 6 | `public long version()` _(in CityMap.RunGround)_ |  |
| 5997 | 5 | `public long[] box()` _(in CityMap.RunGround)_ |  |
| 6007 | 1 | `CityRuns.Ground runGround()` | The ground the runs read: a harness's, to lay runs of its own on the city's ground. |

### THE CITY'S GROUND IN PIECES (batch CL5, slice B) (lines 6009-6783)

| line | len | member | says |
|---:|---:|---|---|
| 6062 | 459 | **type** `final class MapPieces implements CityRuns.Pieces` | The city's ground in pieces (CL5): what CityRuns.layTo() is handed so that a net is laid on every piece of ground the city builds on. |
| 6106 | 3 | `void begin()` _(in CityMap.MapPieces)_ | A month's runs are about to ask: the weights are worked out again at the first ask. |
| 6110 | 4 | `public int count()` _(in CityMap.MapPieces)_ |  |
| 6115 | 6 | `public int pieceOf(long x, long y)` _(in CityMap.MapPieces)_ |  |
| 6122 | 6 | `public long weight(int p)` _(in CityMap.MapPieces)_ |  |
| 6129 | 9 | `public long[] hub(int p)` _(in CityMap.MapPieces)_ |  |
| 6140 | 4 | `long tiles(int p)` _(in CityMap.MapPieces)_ | The land tiles piece p holds. |
| 6146 | 6 | `long landTiles()` _(in CityMap.MapPieces)_ | The land tiles of every piece. |
| 6155 | 43 | `private void ensure()` _(in CityMap.MapPieces)_ | ----- keeping it against the ground ----- |
| 6200 | 16 | `private void indexShore()` _(in CityMap.MapPieces)_ | The tiles a work on the shore may block a sample of: its box and clear verge and its quay, tile by tile (a few; blocks() is asked of a sample in none of them never). |
| 6220 | 18 | `private void scan(long tx0, long ty0, long tx1, long ty1)` _(in CityMap.MapPieces)_ | Reads the tiles tx0..tx1 by ty0..ty1 (inclusive) that are not yet land, a district's block of them at a time so ground the city owns none of is passed over whole. |
| 6240 | 24 | `private boolean isLand(LandGrid g, long tx, long ty, int c8)` _(in CityMap.MapPieces)_ | Whether tile (tx, ty) is land: one of its four samples owned, dry (the terrain's verdict, kept) and not blocked by a work on the shore. |
| 6266 | 9 | `private byte[] terrainChunk(long tx, long ty)` _(in CityMap.MapPieces)_ | A chunk of the kept terrain verdicts: per tile a byte, the four samples' dryness in its low bits and, in its high bits, which of them have been read. |
| 6277 | 19 | `private int labAt(long tx, long ty)` _(in CityMap.MapPieces)_ | The label of tile (tx, ty), -1 where it is not land. |
| 6297 | 12 | `private void setLab(long tx, long ty, int l)` _(in CityMap.MapPieces)_ |  |
| 6310 | 7 | `private int find(int l)` _(in CityMap.MapPieces)_ |  |
| 6319 | 34 | `private void join(long tx, long ty)` _(in CityMap.MapPieces)_ | A land tile joins the pieces round it: the labels of its eight neighbours that are land, merged into one (the smaller joined to the larger), or a new label when it has none. |
| 6357 | 34 | `private void finish()` _(in CityMap.MapPieces)_ | The pieces from the labels: the home piece 0, the others by their first tile (row by row); every label's piece. |
| 6399 | 19 | `private long[] findHub(int p)` _(in CityMap.MapPieces)_ | The first tile of piece p, nearest the founding site's tile (ties: the northern, then the western), whose highway line crossing is owned dry ground by the runs' own ground; null when none is. |
| 6422 | 1 | **type** `private interface TileSink` _(in CityMap.MapPieces)_ |  |
| 6422 | 1 | `void at(long key, int piece)` _(in CityMap.MapPieces.TileSink)_ |  |
| 6425 | 15 | `private void eachTile(int only, TileSink sink)` _(in CityMap.MapPieces)_ | Gives the sink each land tile's hub key (squared distance from the founding site's tile << 30 \| row << 15 \| column: tile coordinates are under 2^15, the world being 23,552 tiles a side, the squared distance under 2^31... |
| 6442 | 6 | `private long[] hubAt(long key)` _(in CityMap.MapPieces)_ | The highway line crossing of the tile a hub key names if it is owned dry ground by the runs' own ground, else null. |
| 6452 | 18 | `private void weigh()` _(in CityMap.MapPieces)_ | Each piece's built ground this month: the plots its districts' buildings stand on, each district whole to the piece holding most of its 8 x 8 tiles' land. |
| 6472 | 12 | `private void weighOne(District d)` _(in CityMap.MapPieces)_ | District d weighed again if its counts moved or the pieces did. |
| 6486 | 13 | `long builtCells(District d)` _(in CityMap.MapPieces)_ | The plots a district's buildings stand on: its used cells less those of its roads, highways, track and works at sea (the types a district does not draw on its ground). |
| 6501 | 19 | `int pieceOfDistrict(District d)` _(in CityMap.MapPieces)_ | The piece holding most of the land tiles of a district's 8 x 8 (ties: the lower index), -1 for none. |
| 6525 | 1 | `MapPieces pieces()` | The city's ground in pieces (CL5): what the runs are laid on. |
| 6528 | 1 | `long[] site()` | The founding site, {x, y} in plots. |
| 6531 | 6 | `private boolean hasFreightLine(long[] counts)` | Whether the counts have any Freight Line, whose track makes the main line double. |
| 6553 | 59 | `boolean layRuns(long[] model)` | Lays the runs to the model's counts (CityRuns.layTo()): its Elevated Highways' plots, its track's, its Rail Terminals and Rail Depots as yards, each asked for its type's footprint (FR7 slice 3: Terminals first, then D... |
| 6619 | 13 | `private int[] yardSequence(long[] counts)` | The type of each yard the model has, in the order the runs lay them (FR7 slice 3): every Rail Terminal (by type index), then every Rail Depot - each yard is asked for its type's footprint (laid YARD_LONG along its lin... |
| 6637 | 9 | `private int[] yardSequenceOf(long[] counts)` |  |
| 6647 | 3 | `private int[] haveSequence()` |  |
| 6652 | 5 | `private int[] footprintOf(int t)` | A type's footprint, {across, down} in whole plots (BuildingVisual.footprint()), kept: the size a yard is asked for, every yard every month (FX5 Y1: laid so long along its line as the ground allows, CityRuns.YARD_LONG,... |
| 6664 | 9 | `int yardTypeOf(int i)` | The type index of the runs' yard i: the model's sequence (yardSequence() of the map's counts); past it (MF1) the terminal type whose footprint the yard was laid for, where the yard stands idle (CityRuns.yardIdleSince(... |
| 6675 | 4 | `private int firstTerminal()` | The first Rail Terminal type's index, or -1. |
| 6681 | 5 | `List<long[]> minedSites()` | The city's mines and wells standing on their sites, each {x0, y0, x1, y1} inclusive: where the railway starts and its yards are drawn near. |
| 6688 | 5 | `List<long[]> bulkSites()` | ...and (FR7 slice 3) of those, the BULK works' (BuildingVisual.Type.bulk(): a mine, a quarry - not a well): the works a siding is laid into and a further yard stands beside. |
| 6695 | 14 | `private void scanMinedSites(List<long[]> all, List<long[]> bulk)` | The mined sites into `all` and the bulk works' among them into `bulk` (either null to leave out): one pass over the districts' site lists. |
| 6719 | 12 | `private void refreshMines()` |  |
| 6737 | 10 | `private int[] terminalTypes()` | The terminal types' indices (BuildingVisual.Type.terminal()), in type order; worked out once (the types are the map's for life). |
| 6751 | 25 | `int yardsOf(District d, int t)` | How many of district d's buildings of terminal type t are the runs' yards. |
| 6778 | 5 | `int yardsOf(District d)` | How many of district d's Rail Terminals and Depots are the runs' yards. |

### the Rail Spurs the runs' sidings left: dealt to the plans' works (batch FR7 slice 5, D14) (lines 6784-6923)

| line | len | member | says |
|---:|---:|---|---|
| 6796 | 3 | `int spursLeft()` | The Rail Spurs the runs' sidings did not take: the model's less the sidings the runs laid into mined sites. |
| 6801 | 4 | `long sidingsDealt()` | How many Rail Spurs are dealt to the districts' plans in all (sidingsOf()). |
| 6819 | 10 | `int sidingsOf(District d)` | How many sidings district d's plan lays (batch FR7 slice 5, D14): its share of the Rail Spurs the runs' sidings left (spursLeft()). |
| 6830 | 26 | `private void dealSidings(int[] share)` |  |
| 6858 | 3 | `long runsInto(District d, byte[] fixed)` | The runs' frame for district d's plan: their plots over its 257 x 257 (DistrictPlan's codes), a hash of them returned. |
| 6863 | 14 | `long runsInto(District d, byte[] fixed, boolean[] ramp)` | ...and (batch FR7) the frame's highway plots that carry a ramp into `ramp` (null to leave them): where an access road may end. |
| 6888 | 8 | `void worksInto(District d, DistrictPlan.Input in)` | The works of the city that need an access road and stand in district d's frame (batch FR7, D17): each Rail Terminal and Rail Depot the runs draw as a yard, each sea terminal on the shore (a tank farm has no berth and ... |
| 6903 | 6 | `static int[] workIn(long[] yard, long x0, long y0)` | A yard's box as the district frame whose first plot is (x0, y0) holds it, {x, y, w, h} in the frame's plots, or null when another frame holds more of it. |
| 6911 | 8 | `private static boolean holdsMost(long p, long n, long f)` | Whether the frame whose first plot is f holds more of the n plots from p than any other frame does; a tie to the first. |
| 6920 | 3 | `private static void addWork(DistrictPlan.Input in, long x, long y, long w, long h)` |  |

### THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13; (lines 6924-7633)

| line | len | member | says |
|---:|---:|---|---|
| 6966 | 1 | `public CityShore shore()` | The city's works on its shore (0.7.97). |
| 6971 | 11 | `int[] shoreTypes()` |  |
| 6987 | 1 | `public int shoreShort()` | How many of the model's terminals and tank farms the shore has no place for: their districts' plans draw them. |
| 6990 | 5 | `private long groundKey()` | The ground's key, without the shore's own works: what a search that came up short waits on. |
| 7007 | 95 | `boolean layShore(long[] model)` | Lays the shore to the model's counts: one fewer of a type takes its newest work; one more is laid where shoreSpot() finds it, the first types first. |
| 7103 | 4 | `private static boolean anyOf(long[] model, int[] ids)` |  |
| 7116 | 30 | `private LinkedHashMap<Integer, Long> portWants(int t, long total)` | How many of a terminal's `total` the city has on each landmass (ET5), the home landmass first and then the others ascending, only those with any: what the picture says stands off home (Landmasses.picture()'s triples o... |
| 7148 | 12 | `private List<Integer> shoreLandmasses(int t, LinkedHashMap<Integer, Long> wants)` | The landmasses a terminal type is to be taken from or laid on (ET5): those it is wanted on, and those its works stand on now, the home landmass first. |
| 7162 | 4 | `private int workLandmass(World world, CityShore.Work w)` | The landmass a work stands on (ET5): its shore plot's, a derived read - CityShore's sidecar carries none. |
| 7168 | 6 | `private int shoreOn(int t, int at)` | How many works of type t stand on a landmass. |
| 7176 | 14 | `private boolean takeNewest(int t, int at)` | The newest work of type t on a landmass taken away (the districts it lies in read first, as layShore() reads them); false when there is none. |
| 7200 | 3 | `CityShore.Work shoreSpot(int t, List<long[]> mines)` | Where the next work of type t goes (CityShore's rule), or null: the districts in the map's order, each one's tiles nearest the founding site first; on each tile with owned salt water and owned dry ground, its shore pl... |
| 7214 | 3 | `CityShore.Work shoreSpot(int t, List<long[]> mines, int landmass)` | ...for a terminal on a landmass (ET5, 2026-10-25): while the city holds a picture of its landmasses and asks for one, the ground is read with the ownership unmasked (RunGround.atRaw(): an island no port links is owned... |
| 7219 | 3 | `CityShore.Work shoreSpotFull(int t, List<long[]> mines)` | ...searched from the nearest tile every time, nothing kept or read of where the last search found room: the search as it was before SL1, which MapCheck 9 holds shoreSpot() equal to. |
| 7223 | 3 | `CityShore.Work shoreSpotFull(int t, List<long[]> mines, int landmass)` |  |
| 7246 | 6 | **type** `private static final class DTrack` | WHERE THE LAST SEARCH FOUND ROOM (SL1, 2026-10-28). |
| 7254 | 16 | **type** `private static final class ShoreTrack` | What the searches for one type on one landmass kept, and what it is kept against. |
| 7263 | 6 | `ShoreTrack(CityShore shore, long removals, long stamp, int districts)` _(in CityMap.ShoreTrack)_ |  |
| 7277 | 1 | `long shoreLooked()` |  |
| 7282 | 1 | `long shoreTouched()` |  |
| 7285 | 6 | `private long shoreStamp(List<long[]> mines)` | What the kept marks are valid against besides the shore: the ground, the runs, the mined sites and the districts in their order. |
| 7295 | 4 | `private SeaRoutes.Grid shoreSea()` |  |
| 7301 | 4 | `void shoreSeaIs(SeaRoutes.Grid grid)` | A harness's sea for the searches (a mask of the cells, SeaRoutes.grid(): a lagoon as wide as the flood's reach, say), in place of the world's; the marks of the searches are forgotten. |
| 7313 | 27 | **type** `private static final class ShutSet extends HashSet<Long>` | The water found shut (SeaRoutes.opensToSea()), noting when a flood touches a water as wide as the reach (hot): where a flood that starts in another part of it may open, so that what a terminal's water is told depends ... |
| 7318 | 15 | `public boolean addAll(java.util.Collection<? extends Long> cells)` _(in CityMap.ShutSet)_ |  |
| 7334 | 5 | `public boolean contains(Object k)` _(in CityMap.ShutSet)_ |  |
| 7342 | 141 | **type** `private final class ShoreScan` | One search for a type's spot: the scratch its tiles are read into, and what the tile last looked at held. |
| 7365 | 3 | `ShoreScan(int t, List<long[]> mines, int landmass)` _(in CityMap.ShoreScan)_ |  |
| 7370 | 13 | `ShoreScan(int t, List<long[]> mines, int landmass, boolean harbour)` _(in CityMap.ShoreScan)_ | `harbour`: a terminal's quay is the harbour's common one (MF1), not its pier of its own. |
| 7385 | 6 | `ShoreScan within(long x, long y, int reach)` _(in CityMap.ShoreScan)_ | The search is for the harbour whose first work's shore plot is (x, y): no spot farther from it than `reach` plots is one (MF1). |
| 7397 | 85 | `boolean look(long tx, long ty, boolean stopAtAny)` _(in CityMap.ShoreScan)_ | Looks at tile (tx, ty): false when it has no salt water or no dry ground (nothing to stand on: no window is read), else its spots are tried in the order of the plots, the first near one ending the look (near) and the ... |
| 7490 | 16 | `private CityShore.Work shoreSpot(int t, List<long[]> mines, int landmass, boolean kept)` | Where the next work of type t goes, by the harbour rule (MF1): in the first harbour of its coast that has room, else in a harbour opened at the best shore the search weighs - the first on a coast, or a second once the... |
| 7512 | 121 | `private CityShore.Work shoreSpotWholeCoast(int t, List<long[]> mines, int landmass, boolean kept)` | The districts in the map's order, each one's tiles nearest the site first: the first district with a spot gives its first near spot, else its first spot. |

### THE HARBOUR (MF1, 2026-10-28) (lines 7634-7914)

| line | len | member | says |
|---:|---:|---|---|
| 7654 | 5 | `void harbourReachLike(CityMap o)` | A harness's map read back from another's sidecar takes the other's harbour rule (its reach), so that both lay what they lay alike. |
| 7661 | 4 | `List<CityShore.Harbour> harbours()` | The shore's harbours in the order opened (CityShore.harbours()), each work on the landmass its shore plot is on - ANY_LANDMASS for all while the city holds no picture of its landmasses. |
| 7672 | 32 | `private CityShore.Work harbourSpot(int t, List<long[]> mines, int landmass, boolean kept, CityShore.Harbour h)` | The first spot in harbour h's reach for a work of type t, or null: the harbour's tiles (those within its reach of its first work's shore plot, owned) nearest that plot first, the first spot of a tile in the order of i... |
| 7706 | 20 | `private long[] harbourTiles(CityShore.Harbour h)` | The tiles of harbour h's reach, as pairs (tx, ty) in a flat array: a tile whose square lies within HARBOUR_REACH of its first work's shore plot, the nearest that plot first (the tile's middle's distance, then its row ... |
| 7736 | 62 | `private CityShore.Work openHarbour(int t, List<long[]> mines, int landmass, List<CityShore.Harbour> all, int coast)` | A harbour opened: the first work on a coast, or a second once the first has none left in its reach. |
| 7809 | 14 | `List<long[]> worldWalks()` | The walks the world line reads (worldLine()), as {x, y, dx, dy}: from the end of each spine arm that has steps - the railway's trunk and corridor arms (not its branches and sidings), the highways' of the home piece - ... |
| 7825 | 10 | `private static boolean onAWalk(CityShore.Work c, long[][] walks)` | Whether any plot of c's box, verge or quay lies on one of the walks (worldWalks(), as arrays). |
| 7842 | 16 | `double harbourScore(World world, CityShore.Work c)` | What a shore costs as a harbour's, in plots of distance, the least the best: its distance from the founding site (the built middle, where the freight yard stands), and a reach more for water open all round it (CitySho... |
| 7860 | 20 | `private static boolean shoreFits(CityShore.Work c, int[] ground, byte[] fixed, long wx0, long wy0, int ww)` | Whether a work fits where c puts it, on the window's ground and runs from (wx0, wy0), ww a side: its box in one cell's interior on owned dry ground no run, mine or other work takes (and, since ET2, not steep: shoreSpo... |
| 7882 | 8 | `static boolean nearRing(CityShore.Work c)` | Whether a work's box lies within TilePainter.REACH of its cell's ring on a side away from the sea: where an arterial runs when the cell is open. |
| 7896 | 18 | `int shoreOf(District d, int t)` | How many of district d's buildings of type t the shore holds. |

### THE OIL AT SEA (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the (lines 7915-7959)

| line | len | member | says |
|---:|---:|---|---|
| 7928 | 1 | **type** `public record Jacket(double x, double y, int wells, List<Long> wellPlots)` | A platform as the map draws it: its jacket's middle in plots, its wells, and the plots its wells stand on (each x << 32 \| y). |
| 7931 | 1 | **type** `public record Pipe(double x0, double y0, double x1, double y1)` | A pipe as the map draws it: from its field's middle (x0, y0) toward the founding site, to (x1, y1), in plots - its kilometres standing. |
| 7934 | 1 | **type** `public record RailPicture(boolean goodsByRail, double wagonShare)` | The railway as the map draws its yard: whether it carries goods, and the share of the track's wagon sets the fleet has standing. |
| 7939 | 1 | `public RailPicture railPicture()` | The railway's picture, as the game last handed it. |
| 7942 | 3 | `static int wagonTracksOf(double share)` | The whole wagon tracks a share of the fleet gives: its share of YARD_TRACKS, rounded to a whole track. |
| 7947 | 8 | `public void railPicture(RailPicture r)` | The railway's picture handed over by the game (Game.mapRail()); a change of goods or of the whole wagon tracks restamps the tiles. |
| 7956 | 3 | `private static double clamp(double v, double lo, double hi)` |  |

### THE ISLAND RULE ON THE MAP (ET5, 2026-10-25) (lines 7960-8201)

| line | len | member | says |
|---:|---:|---|---|
| 8033 | 1 | `public Landmasses.Picture landmassPicture()` | The picture of the landmasses the game last handed over: null while the city is on one landmass. |
| 8036 | 1 | `private long linksStamp()` | The picture's stamp, 0 for none: what a cache of anything the mask touches keys on. |
| 8044 | 18 | `public void landmassPicture(Landmasses.Picture p)` | The landmasses handed over by the game (Game.reconcileMap(), beside railPicture()). |
| 8064 | 3 | `private boolean linkedLandmass(int id)` | Whether a landmass is one the picture lets the city's buildings stand on: 0 (the sea), home and the linked ids. |
| 8069 | 4 | `private int landmassOfShorePlot(World world, long x, long y)` | A plot's landmass as the map reads it, 0 (no landmass) as home: the id a terminal's shore plot is laid for. |
| 8075 | 3 | `private boolean onLandmass(int id, int landmass)` | Whether a dry plot whose landmass is `id` (World.tileLandmass()) is on `landmass`, 0 read as home - the test a terminal's search puts to every plot. |
| 8084 | 38 | **type** `private final class LinkedMask` | Which plots of each tile are masked, kept for the picture held: the plots whose landmass the picture does not link. |
| 8092 | 4 | `void reset(Landmasses.Picture picture)` _(in CityMap.LinkedMask)_ |  |
| 8098 | 23 | `boolean[] of(long tx, long ty)` _(in CityMap.LinkedMask)_ | The mask of tile (tx, ty), a boolean a plot in rows (true = masked), or null when no plot of it is masked or there is no picture. |
| 8124 | 7 | `public void buildableOwnership(long tx, long ty, boolean[] out)` | Which plots of tile (tx, ty) the city owns and may build on: tileOwnership() less the plots the island rule masks (ET5; all of it while the city is on one landmass). |
| 8133 | 12 | `private boolean holdsMasked(int dx, int dy)` | Whether the city owns ground in district (dx, dy) that the island rule masks: such a district stays on the map with no room, so the shore reaches it. |
| 8147 | 4 | `public long[] freightStation()` | The freight station's yard, {x0, y0, w, h} in world plots: the yard nearest the rail main arm's end (spec 4.2; CityRuns.freightStation()); null for none. |
| 8153 | 4 | **type** `public record AtSea(List<Jacket> jackets, List<Pipe> pipes)` | The oil at sea: its jackets and pipes. |
| 8161 | 1 | `public AtSea atSea()` | The oil at sea, as the game last handed it. |
| 8164 | 7 | `public void atSea(AtSea a)` | The oil at sea handed over by the game (Game.mapAtSea()); a change restamps the tiles it crosses. |
| 8176 | 25 | `private void atSeaInto(long px0, long py0, TilePainter.Input in)` | The oil at sea over tile (px0, py0)'s plots, into its inputs: the jackets' and wells' plots, the rings and pipes that may cross it. |

### LONG BRIDGES ON THE MAP (ET6, 2026-10-26; docs/studies/spec-earth2-terrain.md 6.5 and 6.6) (lines 8202-8311)

| line | len | member | says |
|---:|---:|---|---|
| 8247 | 20 | **type** `public record BridgeRun(int kind, long[] corners)` | One long bridge that stands, as the model hands it to the map (ET6): its kind (ROAD, HIGHWAY or RAIL) and the plots of its path where its heading changes, x0, y0, x1, y1, ... |
| 8255 | 3 | `public boolean equals(Object o)` _(in CityMap.BridgeRun)_ |  |
| 8259 | 3 | `public int hashCode()` _(in CityMap.BridgeRun)_ |  |
| 8263 | 3 | `public String toString()` _(in CityMap.BridgeRun)_ |  |
| 8278 | 1 | `public List<BridgeRun> bridgePicture()` | The long bridges as the game last handed them: empty while the city has none. |
| 8281 | 1 | `public long bridgeStamp()` | The bridges' stamp, 0 for none; the same bridges in another order are the same stamp. |
| 8288 | 6 | `public void bridgePicture(List<BridgeRun> runs)` | The long bridges that stand, handed over by the game (Game.reconcileMap(), beside landmassPicture()); null or empty for none. |
| 8296 | 3 | `private boolean applyBridges()` | The bridges noted laid on the runs, if they are not: true when they moved. |
| 8301 | 10 | `private void roadBridgesInto(long px0, long py0, TilePainter.Input in)` | A road bridge's plots on the tile whose first plot is (px0, py0), as the street the plan draws none on: paved and whole, the deck under it the painter's. |

### THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1) (lines 8312-8469)

| line | len | member | says |
|---:|---:|---|---|
| 8347 | 6 | `double cityRadius()` | The city's radius for the offing, in plots: the farthest corner of its owned box from the founding site. |
| 8355 | 3 | `private long routesKeyNow()` | What the routes are found at: the world, the founding site and the city's radius's band (SeaRoutes.band()). |
| 8360 | 3 | `private boolean routesCurrent()` | Whether the routes kept are the city's: found, the shore and its version those they were read from, the band the same. |
| 8365 | 8 | `public List<BoatSchedule.Route> seaRoutes()` | The routes now, found here when they are not (a harness's way; the view hands a RoutesJob to its worker). |
| 8375 | 3 | `public List<BoatSchedule.Route> seaRoutesIfFound()` | The routes when they are found and current, else null: the screen's thread, which never finds them itself. |
| 8380 | 4 | `public SeaLanes seaLanes()` | The lanes of the routes now (SeaLanes), found here when they are not. |
| 8386 | 3 | `public SeaLanes seaLanesIfFound()` | The lanes when the routes are found and current, else null: the screen's thread. |
| 8391 | 44 | **type** `public static final class RoutesJob implements Runnable` | The berths' routes found away from the screen's thread: what they are found from, copied here, and the routes already found for the berths' first ones. |
| 8405 | 13 | `RoutesJob(long key, World.Key world, CityShore shore, long shoreVersion, List<BoatSchedule.Berth> berths, List<BoatSchedule.Rou...` _(in CityMap.RoutesJob)_ |  |
| 8419 | 6 | `public void run()` _(in CityMap.RoutesJob)_ |  |
| 8427 | 1 | `public boolean done()` _(in CityMap.RoutesJob)_ | Whether it has run. |
| 8430 | 1 | `public SeaRoutes.Found found()` _(in CityMap.RoutesJob)_ | What it found, with how many berths it searched and how many joined a lane; null before it has run. |
| 8433 | 1 | `public int kept()` _(in CityMap.RoutesJob)_ | How many routes it was handed, those kept. |
| 8441 | 16 | `public RoutesJob routesJob()` | The job that finds the routes now, or null when they are current. |
| 8459 | 10 | `public boolean adoptRoutes(RoutesJob j)` | A job run: its routes kept when they are still the city's. |

### A TILE'S INPUTS FOR THE PAINTER (lines 8470-9107)

| line | len | member | says |
|---:|---:|---|---|
| 8475 | 3 | `public District districtOfTile(long tx, long ty)` | The district holding tile (tx, ty), or null. |
| 8480 | 3 | `static int tileIn(long tx, long ty)` | The tile in district d at its column and row of tiles (tile = row x 8 + column), from world tile (tx, ty). |
| 8485 | 10 | `public void tileCounts(long tx, long ty, int[] out)` | A tile's buildings by type id as its district's plan draws them (0.7.88; the deal's until 0.7.87), packed ones among them: into out, zeros where no district is. |
| 8497 | 8 | `public void tileOwnership(long tx, long ty, boolean[] out)` | Which of a tile's plots the city owns: a tile owned wholly or not at all in one test (the grid's cover at level 5), the rest from the grid's leaves under it (spec-grid 2.4). |
| 8516 | 6 | `public void tileInput(long tx, long ty, TilePainter.Input in)` | Everything the painter needs for tile (tx, ty), into in: its ground, what the city owns of it, and from its district's plan (0.7.88; the deal's counts and road plots until 0.7.87) the streets through it and every buil... |
| 8524 | 5 | `public void tileInput(long tx, long ty, TilePainter.Input in, byte[] terrain)` | ...with the tile's ground given (a view keeps it: MapTiles.terrain(), batch J4) rather than read from the world. |
| 8531 | 10 | `public List<District> tileDistricts(long tx, long ty)` | The districts a tile's picture is drawn from (0.7.88): those of the tiles about it - its own, the ones whose plans lay the street on its first column or row (west, north, north-west) and those its edges look across to... |
| 8543 | 4 | `public boolean tileReady(long tx, long ty)` | Whether tile (tx, ty) paints without planning: every plan it is drawn from current (planned()). |
| 8570 | 6 | `public long tileKey(long tx, long ty)` | A key of everything tileInput() reads for tile (tx, ty) (batch UP5): equal keys, equal inputs, so equal MapTiles.stamp()s, and a view that holds a picture against it asks fillInput() for nothing (ui/MapView.paint(): a... |
| 8578 | 35 | `long[] tileKeyParts(long tx, long ty)` | The four parts tileKey() folds, in order (a harness's: which of them moved when a key did): what the city owns of the tile, its plans', the railway's bends on it, and the whole map's (tileWorldKey()). |
| 8615 | 12 | `private long tileWorldKey()` | The part of tileKey() that is the whole map's and not a tile's: see tileKey(). |
| 8629 | 5 | `public List<Job> tileJobs(long tx, long ty)` | The jobs that make tile (tx, ty)'s plans current (planJobs()), in order; empty when they are. |
| 8643 | 3 | `byte streetAt(long x, long y)` | The street a plan draws on world plot (x, y) (TilePainter's S_ codes): the district's own; on its first column or row the district west or north of it (or north-west, at its corner) lays the same plot as its frame's m... |
| 8648 | 3 | `private byte streetAt(long x, long y, Map<Long, Drawn> seen)` | ...the plans looked up through `seen`, by district key (null for none), which it fills. |
| 8657 | 28 | `private byte streetAt(long x, long y, Map<Long, Drawn> seen, boolean plan)` | ...and, `plan` false, from the plans in `seen` alone, a district missing there having none: the trips' way (FR9), which plans nothing. |
| 8690 | 9 | `private Drawn drawnAt(long dk, Map<Long, Drawn> seen, boolean plan)` | The plan drawn for the district keyed `dk`, looked up in `seen` once (a plan already seen is one lookup, a district with none is looked for once) and, `plan` true, made and kept there when not seen: null for none. |
| 8706 | 8 | `private static byte interiorCode(Drawn dr, int lx, int ly)` | What streetAt() answers for a plot inside its district's frame, off its first column and row (where no other district lays a code) and with no road bridge to ask about (MF1: the ring of CROSS_REACH about a tile reads ... |
| 8722 | 43 | `private void fillAvenues(long px0, long py0, TilePainter.Input in, Map<Long, Drawn> seen)` | The avenues' marks on the tile at world plot (px0, py0) and the CROSS_REACH plots about it (batch CL4), into in.avenue and in.avenueRing, and their origin when it lies near: each plan of the districts the ring reaches... |
| 8774 | 40 | `private void fillCurves(long px0, long py0, TilePainter.Input in, Map<Long, Drawn> seen)` | The curves that reach the tile at world plot (px0, py0) and the REACH plots about it (batch CL4, slice D), into the input's curves: each plan of the districts the ring reaches (seen, looked up as fillAvenues() has the... |
| 8816 | 198 | `private void fillInput(long tx, long ty, TilePainter.Input in)` | Everything tileInput() fills but the ground, which is in `in` already. |
| 9016 | 1 | **type** `record DrawnSite(long x0, long y0, long x1, long y1, int kind, int state, int mine)` | One site as the map draws it: its square of plots (inclusive), resource, state and the mine on it (-1 for none). |
| 9035 | 57 | `List<DrawnSite> siteList(int dx, int dy)` | The sites whose centres lie in district (dx, dy) - every resource's, owned or not - each with its holding's state (its field's holding, the one holding the field's centre, since 0.7.64) and, for the SITED resources, t... |
| 9094 | 13 | `public void forgetPainted()` | Forgets the painted state of every district - the plans, their links, the packing and the site lists - after the ground's states moved (what is worked out). |

### THE RUNS DATED (batch FX7, 0.8.24) (lines 9108-9348)

| line | len | member | says |
|---:|---:|---|---|
| 9195 | 3 | `private static boolean journalKind(int k)` | The kinds a journal's record holds: 0, a highway's, the railway's, a yard's and (since UP5, FORMAT 15) an interchange's reserve. |
| 9218 | 1 | `public long journalDropped()` | How many plots' records the plans' inputs have left out because the journal did not end on the runs as they stand: 0. |
| 9221 | 14 | `private void beginJournal()` | Starts a month's journalling: the witness set on the runs, the highways' reach noted, the reserves as the month begins found if they are not known. |
| 9237 | 15 | `private void witnessed(long wx, long wy)` | The runs tell the map that world district (wx, wy) is about to change: its kinds are read, once a month, as they are now. |
| 9261 | 4 | `private byte[] takeKinds()` | A kinds array: one kept, or a new one. |
| 9267 | 3 | `private void giveKinds(byte[] b)` | An array kindsOf() gave and nothing holds any more, kept for the next month (no more than KINDS_KEPT). |
| 9279 | 8 | `byte[] kindsOf(District d, boolean reach)` | What a district's own DISTRICT x DISTRICT plots carry in a plan's input, the interchanges' reserves left out: the runs as runsInto() maps them (a railway over a highway is a highway's) with the shore's works over them... |
| 9293 | 55 | `private void endJournal()` | Ends a month's journalling, the runs and the shore laid: each district read is read again, and a record made of every plot whose kind differs, at the map's month - the interchanges' reserves laid over what the runs le... |

### THE RESERVES DATED (batch UP5, FX7's star 2) (lines 9349-9645)

| line | len | member | says |
|---:|---:|---|---|
| 9380 | 3 | `private static List<Long> boxKey(long[] b)` | The key of a reserve's box, for comparing sets of them. |
| 9385 | 3 | `private static boolean meets(long[] b, long[] r)` | Whether the world plot box b = {x0, y0, w, h} meets the box r. |
| 9390 | 7 | `private static void addLaid(List<InterchangeGround.Site> sites, List<long[]> out, java.util.Set<List<Long>> have)` | The sites that got their reserve, as boxes, into out unless already in `have`. |
| 9399 | 13 | `private long[] changedBox(byte[] was, byte[] now, District d)` | The box {x0, y0, w, h}, in world plots, about the plots of district d whose kind differs between `was` and `now` (kindsOf()); null where none does. |
| 9414 | 9 | `private boolean heldBeside(long wx, long wy)` | Whether a district the map holds lies beside world district (wx, wy). |
| 9425 | 3 | `private static long[] districtRegion(long wx, long wy)` | The region of world district (wx, wy) and a margin about it, {x0, y0, w, h}: where a change in it can move a reserve. |
| 9430 | 10 | `private List<long[]> laidReserves()` | The reserves that are laid now, found over every district the highways lie in and the margin about it (while the highways reach a street: none before). |
| 9447 | 71 | `private List<long[]> reservesEnded(boolean reach, List<long[]> changed)` | The reserves as the month ended: those found afresh in the regions the month could have moved one in - a margin about the plots the runs changed, the districts a mine came to or left, the districts under the land boug... |
| 9523 | 4 | `private static boolean meetsAny(long[] b, List<long[]> rs)` | Whether the world plot box b meets any of the boxes r. |
| 9529 | 9 | `private static List<long[]> reservesMoved(List<long[]> a, List<long[]> b)` | The boxes in one of two sets of reserves and not the other: the reserves that came or went. |
| 9540 | 13 | `private void laidOver(byte[] kinds, District d, List<long[]> reserves)` | `kinds` (district d's own plots, as kindsOf() has them) with every reserve of the set that meets d laid over the plots the runs leave at 0. |
| 9555 | 8 | `private long journalStamp(District d, long h)` | `h` with district d's journal over its frame folded in (ownStamp()): its own, and its east neighbour's first column, its south neighbour's first row and its south-east neighbour's first plot - nothing where there is n... |
| 9575 | 70 | `private void fixedLogInto(District d, DistrictPlan.Input in)` | District d's frame's records for its plan's input (Input.fixedLogMonth and fixedLog): its own journal's, and its east neighbour's records on its first column (the frame's last), its south neighbour's on its first row ... |

### THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's (lines 9646-9804)

| line | len | member | says |
|---:|---:|---|---|
| 9665 | 3 | `DistrictPlan plan(District d)` | District d's own plan, drawn now from planInput(d) - its own buildings alone, none carried to it (R7): not kept (THE DRAWN PLANS keep the painter's). |
| 9670 | 5 | `DistrictPlan.Input planInput(District d)` | The inputs of district d's plan (DistrictPlan.Input), its ground read from the world. |
| 9677 | 17 | `static void readGround(DistrictPlan.Input in, World.Key world)` | The ground over a plan's frame, a World class a plot, read from the world the key names (0.7.115; the input's seed's until then), and its relief beside it (ET2: World.tileRelief(), a tile at a time as the cover is; FL... |
| 9696 | 108 | `DistrictPlan.Input planInputHere(District d)` | ...everything but the ground: what the map's own thread reads (the land, the counts, the sites, the runs). |

### ZONES BY DISTRICT (0.8.0, batch I14; docs/studies/spec-zoning.md 4: (lines 9805-9875)

| line | len | member | says |
|---:|---:|---|---|
| 9823 | 1 | **type** `public record ZoneArea(String sector, double sqFt)` | A zone as the map tints it: its sector and the ground it covers, in square feet - what it holds free and what its sector has built in it (LandManager.heldBy() and usedBy()). |
| 9826 | 1 | **type** `public record Tinted(long x0, long y0, int zone)` | A district a zone's tint covers: its box's first plot, {x, y} in the world, a district (DISTRICT plots) a side, and the zone's index in the list given. |
| 9829 | 46 | `public List<Tinted> zoneTint(List<ZoneArea> zones)` | Each zone's districts (ZONES BY DISTRICT), in the map's order: none with no zone. |

### THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 (lines 9876-10007)

| line | len | member | says |
|---:|---:|---|---|
| 9895 | 112 | **type** `public static final class Pyramid` | The districts summed two by two, level by level, to one node - level 0 is the districts themselves, each level above a grid of nodes over the districts' box, a node NODE_WIDTH numbers. |
| 9903 | 45 | `Pyramid(CityMap map)` _(in CityMap.Pyramid)_ |  |
| 9950 | 11 | `long[] vector(District d)` _(in CityMap.Pyramid)_ | A district's numbers: its buildings by class, its ground used (half square feet) and its owned buildable plots. |
| 9963 | 15 | `void add(District d, int t, long k)` _(in CityMap.Pyramid)_ | k more (or fewer) of type t in a district: its ancestors take them. |
| 9980 | 1 | `public int levels()` _(in CityMap.Pyramid)_ | How many levels above the districts. |
| 9983 | 1 | `public int nodes()` _(in CityMap.Pyramid)_ | How many nodes above the districts hold anything (and the top). |
| 9986 | 11 | `public long[] root()` _(in CityMap.Pyramid)_ | The top node: the whole city's numbers. |
| 9999 | 7 | `public long[] node(int level, int dx, int dy)` _(in CityMap.Pyramid)_ | The node at a level (1 up) covering district (dx, dy), or null. |

### THE SIDECAR (spec-land 2.5) (lines 10008-10404)

| line | len | member | says |
|---:|---:|---|---|
| 10039 | 3 | `public byte[] writeSidecar(int month)` | The map as its sidecar's bytes, deflated: the header (magic, format, the world - its seed, or since 0.7.121 an Earth world's key folded, worldId() - month, stamp, the founding site, the stamp of the land's rectangles ... |
| 10044 | 3 | `byte[] writeSidecar(int month, int format)` | ...in a given format, FORMAT or OLDEST_READ (a harness writes the older one to read it back). |
| 10049 | 82 | `byte[] writeSidecar(int month, int format, boolean withRuns)` | ...and with its runs or (a harness's, as 0.7.88 wrote FORMAT 5) a count of none. |
| 10135 | 6 | `private static long landStampOf(CityLand land, int bought, Landmasses.Picture landmasses, long bridges)` | The land's stamp over its first `bought` purchases, with the picture's stamp folded in when the city holds one (ET5): the sidecar's land stamp. |
| 10143 | 7 | `static void writeVarint(DataOutputStream out, int v) throws IOException` | An unsigned varint (CL3): seven bits a byte, the low first, the high bit set where more follow. |
| 10152 | 9 | `static int readVarint(DataInputStream in) throws IOException` | ...read back; an IOException for one more than five bytes long. |
| 10170 | 7 | `static long worldId(World.Key key)` | The world a sidecar names in its header (0.7.121, batch E4): a generated world's seed, as every sidecar has carried it, so theirs are the bytes they were; an Earth world's whole key - its seed, spot and data - folded ... |
| 10179 | 1 | `public long lastStamp()` | The stamp of the sidecar last written: what the save carries as mapStamp. |
| 10182 | 13 | `static long stampOf(byte[] raw)` | The stamp of a sidecar's raw bytes: SplitMix64 folded over them, the stamp's own eight bytes read as zero. |
| 10197 | 7 | `public static long stampIn(byte[] deflated)` | The stamp a sidecar carries, or 0 when it is not one. |
| 10205 | 7 | `private static byte[] inflate(byte[] deflated)` |  |
| 10223 | 4 | `public static CityMap readSidecar(byte[] deflated, CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[]...` | A map read back from its sidecar, or null when it is not one, is not this city's (another seed, month or stamp, or land drawn otherwise), or does not add up. |
| 10232 | 4 | `public static CityMap readSidecar(byte[] deflated, CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[]...` | ...on the landmasses the game hands over (ET5): a sidecar written under another linked set - or under none - is not this city's, and the map is drawn again; the picture is the map's own from the first district read, s... |
| 10241 | 142 | `public static CityMap readSidecar(byte[] deflated, CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[]...` | ...and the long bridges the game hands over (ET6): a sidecar written under another list of them - or none - is not this city's and the map is drawn again; the bridges are laid on the runs read back, none in the file. |
| 10385 | 3 | `public boolean same(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order, and (0.7.89) the same runs, and (0.7.97) the same works on the shore. |
| 10390 | 3 | `public boolean sameDistricts(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order, and (CL3) the same logs: the count of records, the first batch and every record. |
| 10395 | 9 | `public boolean sameDistricts(CityMap o, boolean withLogs)` | ...with or without their logs: the districts of a map read from a sidecar of FORMAT 8 or older, whose logs begin at its month, are the same as the map's own without them. |

