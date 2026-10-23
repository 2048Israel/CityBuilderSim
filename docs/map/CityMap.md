# CityMap.java - 6,661 lines · 342 methods · 48 constants · model

`ham/citybuildersim/CityMap.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

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
> A DISTRICT holds its free plots - owned and dry, a resource's sites among
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
> can is packed at the city's edge without a street (R7). A city is drawn
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
> on corridors from the founding site's lines (CityRuns) - never moved, the
> ... (68 more lines in the source)

**Uses:** [World](World.md) (238), [DistrictPlan](DistrictPlan.md) (104), [BuildingVisual](BuildingVisual.md) (81), [TilePainter](TilePainter.md) (60), [CityRuns](CityRuns.md) (44), [Resource](Resource.md) (43), [CityShore](CityShore.md) (28), [TrackCurves](TrackCurves.md) (26), [Deposit](Deposit.md) (19), [TrainSchedule](TrainSchedule.md) (16), [CityLand](CityLand.md) (15), [DistrictPicture](DistrictPicture.md) (12), [TileRaster](TileRaster.md) (12), [TruckSchedule](TruckSchedule.md) (10), [BoatSchedule](BoatSchedule.md) (7), [LandGrid](LandGrid.md) (6), [SeaRoutes](SeaRoutes.md) (5), [InterchangeGround](InterchangeGround.md) (2), [Gateways](Gateways.md) (2), [LandParcel](LandParcel.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (20):** [CityRuns](CityRuns.md), [DistrictPicture](DistrictPicture.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [Game](Game.md), [GatewayCheck](GatewayCheck.md), [LandMap](LandMap.md), [LongPlaytest](LongPlaytest.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [MiniMapFrame](MiniMapFrame.md), [PlanCheck](PlanCheck.md), [RailDrawCheck](RailDrawCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [TilePainter](TilePainter.md), [TruckRouteCheck](TruckRouteCheck.md)

## Sections

| line | section |
|---:|---|
| 152 | THE GEOMETRY |
| 222 | A DISTRICT |
| 443 | THE STATE |
| 523 | BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE |
| 649 | MEASURING THE GROUND: OWNED DRY PLOTS |
| 850 | THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN |
| 898 | PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION |
| 1031 | · ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3) |
| 1581 | MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) |
| 2123 | THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch |
| 3386 | THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md |
| 3491 | THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch |
| 3685 | THE TRAINS' ROUTES (0.8.2, batch FR1; spec-freight 4.4, D16) |
| 4109 | THE TRUCKS' ROUTES (0.8.8, batch FR5; spec-freight 4.7, D21) |
| 4647 | · the Rail Spurs the runs' sidings left: dealt to the plans' works (batch FR7 slice 5, D14) |
| 4787 | THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13; |
| 5036 | THE OIL AT SEA (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the |
| 5133 | THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1) |
| 5218 | A TILE'S INPUTS FOR THE PAINTER |
| 5712 | THE RUNS DATED (batch FX7, 0.8.24) |
| 5937 | THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's |
| 6094 | ZONES BY DISTRICT (0.8.0, batch I14; docs/studies/spec-zoning.md 4: |
| 6165 | THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 |
| 6297 | THE SIDECAR (spec-land 2.5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 157 | `CityMap.DISTRICT` | `World.DISTRICT` | Plots on a district's side: World.DISTRICT, 256 (7.68 km). |
| 160 | `CityMap.TILES_A_SIDE` | `DISTRICT / World.TILE` | Tiles on a district's side: 8. |
| 163 | `CityMap.TILES` | `TILES_A_SIDE * TILES_A_SIDE` | Tiles in a district: 64. |
| 166 | `CityMap.HALF_SQ_FT_PER_PLOT` | `Math.round(2 * BuildingVisual.SQ_FT_PER_PLOT)` | Half square feet in a plot: 9,687.5 sq ft twice, so a district's room and use are whole numbers and a month's change is exact however it is added up. |
| 169 | `CityMap.MIN_ROOM` | `1` | A district with less room than this many whole plots is passed by the cursors: one, the least a building is drawn on (0.7.64; half a plot of ground before). |
| 172 | `CityMap.PLAN_FILL` | `0.68` | The share of a district's owned plots its buildings' boxes may take: 0.68 (CL3), a full plan's boxes over its ground on Jerus's densest districts (the CL1 study, section 5.3) - so a district is full where its plan is,... |
| 175 | `CityMap.NO_LOG` | `new int [ 0 ]` | A district's empty log: the one array every district without records shares. |
| 178 | `CityMap.SITED` | `{ Resource.IRON, Resource.OIL, Resource.COAL, Resource.STONE, Resource.COPPER }` | The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5); since 0.8.0 (batch I14) coal, stone and copper after them, every resource a works on dry ground stands on. |
| 181 | `CityMap.SLOTS` |  | The site slots (0.8.0, batch I14): each SITED resource's types of field in Resource.typesOf()'s order - iron's hematite and magnetite, oil's one, coal's thermal and coking, stone's limestone and hard rock, copper's su... |
| 184 | `CityMap.SLOT_RESOURCE` |  | ...each slot's resource... |
| 187 | `CityMap.SLOT_TYPE` |  | ...and its type of field. |
| 199 | `CityMap.OLD_SITED` | `2` | The sites a district the sidecar wrote before FORMAT 7: iron's and oil's, SITED's until 0.8.0. |
| 202 | `CityMap.SITE_LISTS_KEPT` | `64` | How many districts' lists of sites are kept: 64. |
| 205 | `CityMap.COARSE_ABOVE` | `1024` | Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). |
| 208 | `CityMap.COARSE_STRIDE` | `32` | The coarse look's stride, in plots: 32, a sample a tile. |
| 211 | `CityMap.MAGIC` | `0x434D4150` | The sidecar's magic: "CMAP". |
| 214 | `CityMap.FORMAT` | `11` | The sidecar's format: 11 since FX7 (0.8.24), each district's journal of its runs after the logs (THE RUNS DATED: a record for each plot of its own box whose kind of fixed ground a month changed, its month, plot and th... |
| 217 | `CityMap.OLDEST_READ` | `4` | The oldest sidecar read: FORMAT 4 (0.7.72 to 0.7.87) - its districts are placed as 0.7.88 places them; it has no runs, which are laid from its counts as it is read. |
| 220 | `CityMap.STAMP_AT` | `4 + 4 + 8 + 4` | Where the stamp sits in the sidecar's raw bytes: after the magic, the format, the seed and the month. |
| 712 | `CityMap.NONE` | `0, SOME = 1, ALL = 2` |  |
| 1295 | `CityMap.CAMPUS_REACH` | `64` | The most districts a works group's units may stand across, in the map's order, and still have a campus a new one goes to first: 64 (0.8.0) - past it, a city whose works of a group stand in every district (a city of bi... |
| 1750 | `CityMap.DISTRICT_ORDER` | `Comparator.comparingDouble((District d) -> d.order).thenComparingInt(d -> d.d...` | Nearest the founding site first, then north to south, west to east. |
| 2158 | `CityMap.PLANS_KEPT` | `64` | How many districts' plans are kept, packed for the painter (Drawn): 64, as the deals and road tiles were (spec 2.9) - each tile's street rows once a pattern and four bytes a building (RD1's plan was 140 KB as District... |
| 2161 | `CityMap.PACKED_BIT` | `1<<20` | A packed box's flag (Drawn.boxes): drawn without a street, R7's packing at the city's edge. |
| 2164 | `CityMap.SIDING_END_BIT` | `1<<24` | A siding plot of a Drawn (Drawn.siding) whose bit this is: the siding's first, beside the works' box, where its loading shed stands (batch FR7 slice 5). |
| 2167 | `CityMap.AVENUE_MARK_SHIFT` | `17` | Where a Drawn avenue plot's mark starts (Drawn.avenue): above the frame's plot, which is under 2^17 (a frame has 66,049 of them). |
| 2170 | `CityMap.CURVE_BAND_BIT` | `1<<20` | A band plot of a DrawnCurve (DrawnCurve.plots) whose bit this is: a plot the curve's band reaches that is not its own. |
| 2217 | `CityMap.BOX_TYPE_SHIFT` | `21` | Where a Drawn box's type id starts: above the box's 20 bits and PACKED_BIT, eleven bits for ids under 2,048 (buildings.json's ids are under 128). |
| 2520 | `CityMap.M_WEST` | `0, M_NORTH = 1, M_EAST = 2, M_SOUTH = 3, M_PARTED = 4` | A plan frame's edges' street codes (Link.margins): its west column (x 0) and north row (y 0), its east column (x 256, the next district's first) and south row (y 256), each 257 long; and M_PARTED, a byte along them wi... |
| 2541 | `CityMap.AROUND` | `{ { 0, - 1 }, { 1, 0 }, { 0, 1 }, { - 1, 0 }, { - 1, - 1 }, { 1, - 1 }, { 1, ...` | The eight districts about one, by offset {dx, dy}: north, east, south, west, then north-west, north-east, south-east, south-west - the order of a frame's seam parts (DistrictPlan.Input.surfaces). |
| 2544 | `CityMap.CORNER_DIRS` | `{ { 3, 0, 4 }, { 1, 0, 5 }, { 1, 2, 6 }, { 3, 2, 7 } }` | Each corner seam part's districts sharing it besides its own, as AROUND's directions: north-west, north-east, south-east, south-west. |
| 2671 | `CityMap.CHAIN_BAND` | `128` | THE CHAIN'S BANDS (0.7.89, batch RD3). |
| 2674 | `CityMap.BANDS_KEPT` | `16` | The bands whose links are kept, the last used: 16 (2,048 districts' links, about 4 MB) - a band let go is planned again when a screen asks for it. |
| 3347 | `CityMap.INTERIOR_MOST` | `World.TILE - 1` | A tile's interior holds a box no larger than this a side: 31 plots, off its arterial lines. |
| 3403 | `CityMap.PICTURES_KEPT` | `128` | How many districts' pictures are kept, the last used: 128 (star MM1-2) - a far view of up to 128 built districts (Jerus's city has 93) in DistrictPicture.BYTES each, 0.14 MB, within the view's 48 MB with the rest (Map... |
| 3580 | `CityMap.WORLD_GAP` | `2 * CityRuns.LOOK` | How far ahead of a track's end the city's edge may lie for its line to the world to start there, in plots: 12, twice CityRuns.LOOK - an arm waits LOOK short of the edge, a 45-degree arm sooner (FX2). |
| 3583 | `CityMap.WORLD_REACH` | `4 * DISTRICT` | How far the world line is drawn at most, in plots: 1,024 (30.7 km), four districts; it stops sooner at the first plot that is not dry ground (FX2). |
| 3586 | `CityMap.WORLD_STEP` | `0.25` | How finely a way out is read, in plots: a quarter, as MapCheck reads the trains' routes (FX2). |
| 4126 | `CityMap.TRUCK_ROUTE_LEAST` | `4` | The least a highway route runs along the highways, in plots, past the world line's own: shorter is the arm the line leaves from, where the trucks have no road to drive. |
| 4129 | `CityMap.TRUCK_ROUTES_MOST` | `40` | The most highway routes kept, 40: one to each ramp, the arms' order, a city of many arms is cut here (a route is a lane of trucks, and the legend would not read more). |
| 4132 | `CityMap.TRUCK_ACCESS_FADE` | `3` | How far an access road's trucks fade in off the street grid, in plots: 3. |
| 4299 | `CityMap.RUN_LINES_KEPT` | `4096` | Terrain lines (a tile's row or column, World.lineTerrain()) the runs' ground keeps: 4,096, about 460 KB - a corridor's way ahead and back, at any size. |
| 4302 | `CityMap.RUN_TILES_KEPT` | `256` | ...whole tiles, for a 45-degree way: 256. |
| 4416 | `CityMap.WAYS_WORKS_DISTRICTS` | `1000` | Up to this many districts the map weighs its mined sites each month for the railway's sidings and further yards: 1,000 (Jerus's city has 93). |
| 5090 | `CityMap.AtSea.NONE` | `new AtSea(List.of(), List.of())` | None. |
| 6170 | `CityMap.NODE_WIDTH` | `BuildingVisual.CLASSES + 2` | How many numbers a node sums: the ten classes, the ground used (in half square feet, exact) and the owned dry plots. |
| 6173 | `CityMap.NODE_USED` | `BuildingVisual.CLASSES` | Where a node keeps the ground used, in half square feet. |
| 6176 | `CityMap.NODE_OWNED` | `BuildingVisual.CLASSES + 1` | ...and its owned dry plots. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 229 | `public final int dx, dy` | Its column and row, in districts from the founding site's. |
| 230 | `final long key` |  |
| 231 | `final double order` |  |
| 233 | `int owned` | Its free plots: owned and dry, counted plot by plot (0.7.64; from 32 x 32 samples before) - or, for a harness's square city, given. |
| 235 | `long usedHalf` | The ground its buildings use, in half square feet: each type's count x its land, kept exactly. |
| 237 | `long usedCells` | The whole plots its buildings and roads are drawn on (BuildingVisual.cells()), a mine or well on its site taking its own: what its room is kept in (0.7.64). |
| 239 | `int inner` | Its free plots inside its tiles' edge rings, where alone the painter laid a road until 0.7.87, and the road plots it holds: its room for roads, by which its counts are still placed (0.7.64). |
| 240 | `long usedRoad` |  |
| 242 | `final int[] counts` | Its count of every type, by id. |
| 244 | `final int[] sites` | Its owned sites of each slot (SLOTS; of each SITED resource until 0.8.0). |
| 246 | `short[] tileFree` | Each tile's free plots, row by row: owned and dry; null until measured or asked for. |
| 248 | `short[] tileInner` | ...and those inside its edge ring, where alone the painter laid a road until 0.7.87 (the counts' room for roads still measures it). |
| 250 | `long[][] tileSiteKey` | Each tile's sites, by key (TilePainter.Input.siteKey), and their free plots on it, all and inside the edge ring: what a mine standing on one takes of the tile. |
| 251 | `short[][] tileSiteFree, tileSiteInner` |  |
| 253 | `int[] sited` | How many of each site-bound type stand on sites, each counted in the district of its first site (the rest of its count has none); null when none. |
| 255 | `int[][] siteRuns` | For painting (0.8.0, batch I14; pairs of {holding, mines} by resource before): per slot, the works on its sites here in the walk's order, triples of {holding, type, sites} - which type each holding's sites here carry,... |
| 257 | `long version` | Bumped whenever its counts change: what its sites' list is kept against (its deal's until 0.7.87). |
| 259 | `int index` | Its place in the map's order, nearest first. |
| 267 | `int[] logMonth` | ITS LOG (batch CL3): the changes to its counts, month by month, in the order they came - a record {month, type, change} (+ come, - gone) for each type a month moved, the types in id order, months never falling, one re... |
| 269 | `int logN` | How many records the log holds. |
| 271 | `int firstBatch` | How many of its first records are one batch, placed in today's order (a canonical map's deal; a sidecar's from before FORMAT 9). |
| 273 | `int[] logged` | Its counts as last logged; null before the first (the same as none). |
| 275 | `boolean logDirty` | Whether its counts may have moved since they were logged: set wherever they change (a new district is dirty), cleared by logChanges(). |
| 277 | `long logHash` | A hash of its log, kept with it: what its plan's stamp folds, so a plan is drawn again when its log moved and its counts did not. |
| 285 | `int[] jMonth` | ITS JOURNAL OF THE RUNS (batch FX7, THE RUNS DATED): a record {month, plot, before, after} for each plot of its own DISTRICT x DISTRICT box whose kind of fixed ground a month changed - the plot's index in the box, row... |
| 287 | `int jN` | How many records its journal holds. |
| 289 | `long jHash, jHashCol, jHashRow, jHashCorner` | Hashes of its journal, kept with it: of every record, of those on its first column, of those on its first row, and of the one on its first plot - what the plans of it and of its west, north and north-west neighbours (... |
| 447 | `private final long seed` |  |
| 449 | `private final World.Key worldKey` | The world it is drawn on (0.7.115, batch E2): its land's key, whose ground every tile and plan reads. |
| 450 | `private final long siteX, siteY` |  |
| 451 | `private final long baseDX, baseDY` |  |
| 452 | `private final BuildingVisual.Type[] types` |  |
| 453 | `private CityLand land` |  |
| 454 | `private Function<Resource, double[]> remaining` |  |
| 456 | `private final List<District> districts` |  |
| 457 | `private final Map<Long, District> byKey` |  |
| 458 | `private final long[] have` |  |
| 459 | `private final int[] lo, hi` |  |
| 460 | `private int innerCursor, outerCursor` |  |
| 461 | `private int purchasesSeen` |  |
| 462 | `private long centreSeen` |  |
| 463 | `private boolean measured` |  |
| 464 | `private Pyramid pyramid` |  |
| 465 | `private SiteIndex siteIndex` |  |
| 466 | `private boolean sitedPlaced` |  |
| 468 | `private int month` | The month of its last change (CL3): what its districts' logs date their records by; 0 for a map drawn canonically with no month given, a harness's square city. |
| 1004 | `private long changes` | How many changes the map has taken since it was drawn - a district's count, a purchase measured, a site's state: what the view stamps its tiles again on (MapTiles). |
| 1023 | `private int[][] lastStates` | Each resource's holdings' states at the last month: when they move, the sites are drawn again. |
| 1026 | `int lastTouched` | How many district-types the last month's change touched. |
| 1029 | `double lastRunsMs` | How long the last month's runs took to lay, in ms (0.7.89): a harness's. |
| 1046 | `private double roadShare` | The city's road plots for each plot of its other buildings, this month: its roads' whole plots over everything else's (0 with no road). |
| 1080 | `private final List<District> builtIn` | The districts the month's new buildings went into (ROADS FOLLOW THEIR BUILDINGS): where its new roads are looked for, so a month's work stays with the districts it changes. |
| 1081 | `private final java.util.Set<District> builtInSet` |  |
| 1116 | `private double[] lackOf` | THE LACKING HEAP (0.7.94, batch RD7): placeRoad()'s districts, the one whose buildings lack road the most first, the lower index on a tie - the order the PriorityQueue it replaces kept, entry for entry (no two entries... |
| 1117 | `private int[] lackAt` |  |
| 1118 | `private int lackN` |  |
| 1227 | `private long[] passedSize` | WALKS PASSED (0.7.94, batch RD7). |
| 1228 | `private int[] passedTo` |  |
| 1229 | `private int nPassed` |  |
| 1298 | `private int[][] campusTypes` | Each group's types, by id: worked out once a table. |
| 1598 | `District[][][] where` | [resource][holding]: the districts, and the sites in each. |
| 1599 | `int[][][] how` |  |
| 1601 | `final List<List<Map<District, int[]>>> tally` | The tallies they are read from: [resource] then holding, district to sites. |
| 1790 | `District[] at` |  |
| 1791 | `int[] holding` |  |
| 1792 | `int length, slot` |  |
| 1794 | `int pos, partial` | The next free site: its entry, and the sites of that entry taken. |
| 1796 | `long total, taken` | Its sites, and those taken. |
| 1803 | `SiteIndex ix` |  |
| 1804 | `int[] states` |  |
| 1806 | `Walk[] walks` | By the resource's own slots, in SLOTS' order. |
| 1808 | `long[][] on` | By claim (claims()): its works on each of the resource's slots, on none, and its count at the last month. |
| 1809 | `long[] over, count` |  |
| 1812 | `private final Lay[] lays` |  |
| 1815 | `private int[][] claims` | Each SITED resource's claims (the types standing on its sites) in the order they are laid: worked out once a table. |
| 2179 | `final byte kind` | Its kind (DistrictPlan.Curve.CRESCENT or SHORE), its radius and its geometry as DistrictPlan.Curve has them, in the frame's plots. |
| 2180 | `final double radius` |  |
| 2181 | `final double[] geom` |  |
| 2183 | `final int[] plots` | Its own plots, in the order DistrictPlan.Curve.plots() gives them (the first and the last are its ends), then the plots its band reaches, each with CURVE_BAND_BIT; all frame plots (y x FRAME + x). |
| 2185 | `final int own` | How many of those are its own. |
| 2187 | `final int x0, y0, x1, y1` | The box of all its plots, in the frame's plots (inclusive). |
| 2222 | `long stamp` | Its link's stamp: its own inputs (ownStamp()) and what was carried to it. |
| 2224 | `final byte[][] tiles` | Each tile's street codes (TilePainter's S_ codes), null where it has none, packed by row (pack()): a byte for each of its 32 rows saying which of its patterns that row is, then the patterns, 32 codes each - a tile's s... |
| 2226 | `byte[] east, south` | The frame's east column and south row: the first column and row of the districts east and south of it, where its edge cells' last arterials run (null where it lays none). |
| 2228 | `final int[][] boxes` | Each tile's buildings, an int each: the box - x, y, w - 1 and h - 1 in the tile's plots, five bits each, PACKED_BIT when packed without a street - and the type id from BOX_TYPE_SHIFT. |
| 2230 | `int[] siding` | The plots its plan's sidings run over (batch FR7 slice 5, D14), each a plot of its frame (y x FRAME + x) with SIDING_END_BIT set on the first of a siding, the loading shed's; null for none. |
| 2232 | `int[] avenue` | The plots its plan's avenues run over (batch CL4, DistrictPlan.avenue), each a plot of its frame (y x FRAME + x) with the avenue's mark - CityRuns' M_DIAG, the arm's heading and M_CORNER - above it at AVENUE_MARK_SHIF... |
| 2234 | `DrawnCurve[] curves` | Its plan's curves (batch CL4, slice D; DistrictPlan.curves) as the painter reads them, in the order the plan has them; null for none. |
| 2236 | `int[][] access` | Its plan's access roads (batch FR7, D17) as the trucks drive them (FR5, slice B): each a way of plots of its frame (y x FRAME + x), the road's end first and its gate last, 4-connected over the plots the roads run over... |
| 2238 | `int placed, refused` | Buildings its plan placed, and what it could not hold (carried on, R7). |
| 2240 | `final long[] halves` | Its streets' surface in half plots by kind, [0, gravel, paved, highway], the plots of street it lays and how many of them are tracks: what its own road is drawn as. |
| 2241 | `int streets, tracks` |  |
| 2243 | `int joinsOut, partedOut, seamsSurfaced` | Its pieces joined to the streets of the districts before it, those no way over its ground joins, and its one-sided seams surfaced (DistrictPlan's figures). |
| 2249 | `double surplus` | Road it has no street for (the plan's surplus), in plots, and by kind [0, gravel, paved, highway]. |
| 2250 | `final double[] leftover` |  |
| 2463 | `final long own, serial` |  |
| 2464 | `final int[] in, out` |  |
| 2465 | `final long[] free` |  |
| 2466 | `final byte[][] margins` |  |
| 2467 | `final District[] earlier` |  |
| 2468 | `final long[] read` |  |
| 2470 | `final double surplus` | Its plan's road with no street to carry it (DistrictPlan.surplus), in plots: what the legend counts (star RD2-4); by kind [0, gravel, paved, highway]. |
| 2471 | `double[] leftover` |  |
| 2473 | `long picture` | Its plan's picture's stamp (0.7.128, batch MM1; DistrictPicture.stampOf()): the picture kept for it is current when it bears this. |
| 2644 | `long stamp` |  |
| 2645 | `final Map<District, int[][]> boxes` |  |
| 2646 | `int packed, smaller, none` |  |
| 2649 | `private final Map<District, Drawn> drawn` |  |
| 2652 | `private final Map<District, Link> links` |  |
| 2653 | `private final Map<District, PlanJob> pending` |  |
| 2655 | `private final Map<Integer, Pack> packs` | Each band's packing at its edge, and the job making it. |
| 2656 | `private final Map<Integer, PackJob> packings` |  |
| 2683 | `private long[] bandEpoch` | Each band's links checked at an epoch (bandEpoch): those of its districts up to bandValidTo (an index in the map's order) hold, and (when all do) the stamp of the packing they make. |
| 2684 | `private int[] bandValidTo` |  |
| 2685 | `private int bandsSized` |  |
| 2688 | `private final LinkedHashMap<Integer, Boolean> bandsUsed` | The bands used, the last last: those past BANDS_KEPT are let go (forgetBand()). |
| 2691 | `private long jobsHanded` | Jobs handed out so far: each job's serial, which keeps a later job's link over an earlier one's adopted after it. |
| 2863 | `final CityMap map` |  |
| 2865 | `final long epoch, serial` | The epoch it was handed out at (planEpoch()), and its place among every job the map handed out: a later job's inputs are the newer. |
| 2866 | `private boolean done` |  |
| 2881 | `final District d` |  |
| 2882 | `final DistrictPlan.Input in` |  |
| 2883 | `final long own` |  |
| 2884 | `final PlanJob after` |  |
| 2885 | `final int[] given` |  |
| 2886 | `final boolean edge` |  |
| 2888 | `final District[] earlier` | The districts about it before it in the map's order (earlierOf()), and each one's link or job: whose frames' edges it reads (readNeighbours()). |
| 2889 | `final Object[] sources` |  |
| 2891 | `int[] carryIn, carryOut` | What was carried to it and what it carries on: {month, code} pairs in the order they came (CL3; an arrival, or the departure ~k of the k-th pair's, DistrictPlan.Input.carry), null for none. |
| 2892 | `Drawn result` |  |
| 2893 | `long[] free, read` |  |
| 2894 | `byte[][] margins` |  |
| 2895 | `double ms` |  |
| 2897 | `final int[] extras` | Its picture's (0.7.128, batch MM1): the boxes the runs and the shore stand on its ground (extrasIn()), the stamp of the picture kept for it when it was handed out (0 for none), its plan's plots' stamp (DistrictPicture... |
| 2898 | `final long keptPicture` |  |
| 2899 | `long pictureStamp` |  |
| 2900 | `DistrictPicture picture` |  |
| 2901 | `double pictureMs` |  |
| 2962 | `long stamp` |  |
| 2963 | `int band` |  |
| 2964 | `final District[] edges` |  |
| 2965 | `final Object[] from` |  |
| 2966 | `final Object last` |  |
| 2967 | `final BuildingVisual.Type[] types` |  |
| 2968 | `final long[] tileOrder` |  |
| 2969 | `Pack result` |  |
| 3405 | `private final Map<District, DistrictPicture> pictures` |  |
| 3410 | `private long picturesKept` | How many pictures have been kept since the map was drawn, each drawn for plots the one kept before it was not: what the far view repaints a waiting node on, and a harness counts a month's redraws by. |
| 3533 | `private long landVersion` | Bumped whenever free ground is measured again (a purchase): what a run waiting on the ground is tried again on. |
| 3536 | `private CityRuns runs` | The city's highways and railway (spec 2.7, 2.8). |
| 3589 | `private List<WorldLine> worldLinesCache` | worldLines()'s last answer, kept against the map's changes: recomputed when a purchase or a month's runs move changes(). |
| 3590 | `private long worldLinesAt` |  |
| 3690 | `private List<TrainSchedule.Route> trainRoutesCache` | trainRoutes()'s last answer, kept against the map's changes and the wagon tracks the railway's picture gave (FX5, slice T). |
| 3691 | `private long trainRoutesAt` |  |
| 3692 | `private int trainRoutesWagons` |  |
| 3698 | `private Map<Long, List<DrawnBend>> trainBends` | The railway's bends the tiles draw, by the tiles they reach (bendKey()), made with the routes and kept with them. |
| 3813 | `private Map<Long, List<TrackCurves.Bend>> highwayBends` | The highways' bends the tiles draw (FX5 slice D), by the tiles they reach (bendKey()), kept against changes() and whether the highways reach a street. |
| 3814 | `private long highwayBendsAt` |  |
| 3815 | `private boolean highwayBendsReach` |  |
| 3881 | `private volatile YardLayouts yardLayouts` |  |
| 4135 | `private List<TruckSchedule.Route> truckRoutesCache` | truckRoutes()'s last answer, kept against the map's changes. |
| 4136 | `private long truckRoutesAt` |  |
| 4193 | `long stamp, x0, y0` |  |
| 4194 | `DistrictRoutes routes` |  |
| 4197 | `private final Map<Long, AccessRoutes> accessRoutesCache` |  |
| 4295 | `private int accessRoadCountCache` | accessRoadCount()'s last answer, kept against the map's changes. |
| 4296 | `private long accessRoadCountAt` |  |
| 4312 | `private final Map<Long, byte[]> lines` |  |
| 4315 | `private final Map<Long, byte[]> tiles` |  |
| 4318 | `private final Map<Long, boolean[]> owned` |  |
| 4321 | `private long ownedAt` |  |
| 4322 | `private byte[] lastLine` |  |
| 4323 | `private boolean[] lastOwn` |  |
| 4324 | `private int lastCover` |  |
| 4325 | `private final boolean[] ALL_OWNED` |  |
| 4399 | `private final RunGround runGround` |  |
| 4504 | `private int[] yardSeqCache` | yardSequence() of a set of counts, the last kept against the terminal types' counts in it (a month and a tile ask for it again and again; a city of billions has thousands of yards). |
| 4505 | `private long yardSeqKey` |  |
| 4528 | `private int[][] footprints` |  |
| 4579 | `private List<long[]> minesKept, worksKept` | layRuns()'s mined sites and bulk works, kept against a digest of the districts' mines (their site runs): a city of billions has some two hundred thousand sites, and walking every district's site list costs seconds the... |
| 4580 | `private long minesKey` |  |
| 4596 | `private int[][] yardShare` | Each district's Rail Terminals and Depots the runs draw as yards, by its index and then the terminal type's place among the terminal types (terminalTypes()): the yards of each type dealt to the districts holding that ... |
| 4597 | `private long yardShareAt` |  |
| 4611 | `private int[] terminalTypesKept` |  |
| 4650 | `private int spurCount` | The Rail Spurs the model has and the track plots they are, as layRuns() last saw them: the runs lay a siding into a mined site for each spur they can; the rest are dealt (sidingsOf()). |
| 4651 | `private long spurTrack` |  |
| 4654 | `private int[] sidingShare` | Each district's share of the spurs, by its index, how many are dealt in all, and what they were kept against (the runs' version, the map's changes and the spurs). |
| 4655 | `private long sidingsDealt` |  |
| 4656 | `private long sidingShareAt` |  |
| 4804 | `private CityShore shore` | The city's works on its shore. |
| 4810 | `private int[] shoreTypes` | The shore's types, by id: worked out once a table. |
| 4825 | `private long shoreShortAt` | The ground's key a search came up short at: none is made again until the ground moves (a purchase) or a work is taken. |
| 4828 | `private int shoreShort` | How many works the model has that the shore holds no place for (drawn by their districts' plans). |
| 5013 | `private int[][] shoreShare` | Each district's works the shore holds, by its index and the shore's type: the works dealt to the districts holding their type in the map's order, so the rest are their plans' (kept against the shore and the counts). |
| 5014 | `private long shoreShareAt` |  |
| 5057 | `private RailPicture railPicture` |  |
| 5093 | `private AtSea atSea` |  |
| 5143 | `private List<BoatSchedule.Route> routes` | The routes found, and the key of what they were found from. |
| 5144 | `private long routesKey` |  |
| 5178 | `final long key, seed, siteX, siteY` |  |
| 5180 | `final World.Key world` | The world the routes are found on (0.7.115). |
| 5181 | `final List<BoatSchedule.Berth> berths` |  |
| 5182 | `final double radius` |  |
| 5183 | `private volatile List<BoatSchedule.Route> found` |  |
| 5626 | `private final Map<Long, List<DrawnSite>> siteLists` |  |
| 5629 | `private final Map<Long, Long> siteListVersion` |  |
| 5774 | `private boolean journalling` | Whether the month's runs are being journalled: only inside reconcile()'s layRuns() and layShore(), never the canonical deal, a square city, a sidecar's read or a harness's own laying. |
| 5777 | `private boolean reachAtStart` | highwaysReach() as the month began: the districts the month changes are read as the highways were drawn then. |
| 5780 | `private final Map<Long, byte[]> journalWas` | The districts the month has read so far, by key: each one's kinds as they were before the month's first change to it. |
| 5783 | `private long lastWitnessed` | The key of the district the witness was told of last: a stretch is told of one district plot by plot. |
| 5786 | `private final CityRuns.Witness witness` | The witness set on the runs for the length of a month's laying. |
| 5789 | `private long journalDropped` | How many plots' records a plan's input has left out because the journal did not end on the runs as they stand (rule (b) of fixedLogInto()): a journal that disagrees with today's runs is a bug, and MapCheck holds this ... |
| 6185 | `private final CityMap map` |  |
| 6186 | `private final int minX, minY` |  |
| 6188 | `private final int[] widths, heights` | Each level's grid: its width, height and nodes (NODE_WIDTH numbers each, row by row). |
| 6189 | `private final long[][] nodes` |  |
| 6190 | `private final int count` |  |
| 6420 | `private long lastStamp` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 150 | 6512 | **type** `public final class CityMap` | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, each district's street plan kept for the painter with what it cannot ... |

### THE GEOMETRY (lines 152-221)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 8 | `static { ... }` |  |

### A DISTRICT (lines 222-442)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 213 | **type** `public static final class District` | One district: where it is, its free plots, the ground and the plots used, its owned sites, its counts by type. |
| 291 | 7 | `District(int dx, int dy, int types, double order)` _(in CityMap.District)_ |  |
| 300 | 1 | `public int owned()` _(in CityMap.District)_ | Its free plots (owned and dry, its sites' plots among them). |
| 302 | 1 | `public double used()` _(in CityMap.District)_ | The plots of ground its buildings use: each type's count x its land in plots. |
| 304 | 1 | `public long usedCells()` _(in CityMap.District)_ | The whole plots its buildings and roads are drawn on. |
| 305 | 1 | `public int count(int t)` _(in CityMap.District)_ |  |
| 306 | 5 | `public int sites(Resource r)` _(in CityMap.District)_ |  |
| 312 | 1 | `long room()` _(in CityMap.District)_ | Its room in whole plots: PLAN_FILL of its owned plots, what its plan can draw (CL3; every owned plot before). |
| 314 | 1 | `long freeCells()` _(in CityMap.District)_ | Its room left in whole plots, never below nothing (0.7.64; in ground, half square feet, before). |
| 316 | 1 | `long freeRoad()` _(in CityMap.District)_ | ...and for roads: its whole room, no more than its plots inside the edge rings less its road. |
| 319 | 1 | `public int logSize()` _(in CityMap.District)_ | How many records its log holds. |
| 322 | 1 | `public int journalSize()` _(in CityMap.District)_ | How many records its journal of the runs holds (batch FX7). |
| 329 | 28 | `void logAdd(int month, int t, int change)` _(in CityMap.District)_ | Adds a change of type t, made in a month, to its log: a record at the end, or - when the month's records after the first batch already hold the type - into that one (one record to a type and month; gone when the two c... |
| 359 | 5 | `void rehash()` _(in CityMap.District)_ | Its log's hash worked out again from its records. |
| 365 | 4 | `private static long fold(long h, int month, int t, int change)` _(in CityMap.District)_ |  |
| 371 | 7 | `boolean sameLog(District b)` _(in CityMap.District)_ | Whether its log and b's are the same: the count of records, the first batch and every record. |
| 380 | 5 | `boolean sameJournal(District b)` _(in CityMap.District)_ | Whether its journal of the runs and b's are the same: the count of records and every record. |
| 391 | 19 | `void jAdd(int month, int plot, int before, int after)` _(in CityMap.District)_ | Adds the change of a plot's kind from `before` to `after`, made in a month, to its journal (batch FX7): a record at the end - the month never falls, one before the journal's last is the last's - or, when the plot has ... |
| 412 | 11 | `void jAppend(int month, int plot, int before, int after)` _(in CityMap.District)_ | ...a record at the end, for a month later than the journal's last (a month's first records need no look for a plot's own). |
| 425 | 8 | `private void jFold(int month, int rec)` _(in CityMap.District)_ | Folds one record into its journal's hashes. |
| 435 | 4 | `void jRehash()` _(in CityMap.District)_ | Its journal's hashes worked out again from its records. |
| 441 | 1 | `static long key(int dx, int dy)` |  |

### THE STATE (lines 443-522)

| line | len | member | says |
|---:|---:|---|---|
| 470 | 12 | `private CityMap(World.Key worldKey, long siteX, long siteY, BuildingVisual.Type[] types)` |  |
| 484 | 1 | `public long seed()` | The world's seed. |
| 487 | 1 | `public int month()` | The month of its last change (CL3): the month its districts' newest log records are dated. |
| 490 | 1 | `public World.Key worldKey()` | The world's key (0.7.115): World.of(worldKey()) is the ground it is drawn on. |
| 493 | 1 | `public BuildingVisual.Type[] types()` | The types it counts, by id. |
| 496 | 1 | `public List<District> districts()` | Its districts, nearest the founding site first. |
| 499 | 1 | `public District district(int dx, int dy)` | The district at (dx, dy) from the founding site's, or null. |
| 502 | 1 | `public Pyramid pyramid()` | The pyramid. |
| 505 | 1 | `public boolean measured()` | Whether its districts were measured on the ground (false for a harness's square city, whose capacities were given). |
| 508 | 5 | `public long[] totals()` | Every type's count over all districts. |
| 515 | 1 | `public CityLand land()` | The land it was drawn on. |
| 518 | 1 | `long baseDX()` | The founding site's district's column, absolute. |
| 521 | 1 | `long baseDY()` | ...and row. |

### BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE (lines 523-648)

| line | len | member | says |
|---:|---:|---|---|
| 537 | 4 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] counts)` | A map drawn canonically (spec-land 2.5): the land's districts measured, then each type in proportion in every district, inner first, the remainders by largest remainder; mines and wells on their sites. |
| 546 | 14 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] coun...` | ...in a given month (CL3): the month its districts' logs date the deal by, each log one batch (firstBatch) of its counts as the deal gave them. |
| 571 | 4 | `static CityMap square(long seed, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity, long[] counts)` | A HARNESS'S SQUARE CITY (the design's scale case): side x side districts round the founding site, every one owned and holding `capacity` plots - a city of any size at one density without measuring its ground; a distri... |
| 577 | 32 | `static CityMap square(World.Key world, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity, long[] counts)` | ...on the world a key names (0.7.115): EarthTerrainCheck's screen on Earth. |
| 610 | 9 | `private District newDistrict(int dx, int dy)` |  |
| 620 | 8 | `private void sortDistricts()` |  |
| 630 | 13 | `private void rangeTypes()` | Each type's lowest and highest district index holding it. |
| 644 | 4 | `private void advanceCursors()` |  |

### MEASURING THE GROUND: OWNED DRY PLOTS (lines 649-849)

| line | len | member | says |
|---:|---:|---|---|
| 654 | 27 | `private void measureAll()` | Every district the land's owned box reaches, measured; the coarse look first when there are many. |
| 683 | 28 | `private boolean[] coarseLand(int dx0, int dy0, int nx, int ny)` | Which districts have land in or beside them, by a sample a tile (COARSE_STRIDE): a district all sea there, with all-sea neighbours, is taken as sea and not measured. |
| 715 | 3 | `int ownership(int dx, int dy)` | Whether the city owns none, some or all of a district: the grid's cover of it, a level-8 block (spec-grid 2.4; by its radii and lanes until 0.7.66). |
| 720 | 3 | `private static int cover(int c)` | LandGrid's NONE, SOME or ALL as this class's. |
| 725 | 5 | `double[] ownedBox()` | The land's owned box from the site, in plots, inclusive {dx0, dy0, dx1, dy1}: its reach each way (an empty land, the site's plot). |
| 741 | 64 | `private void measure(District d, int own)` | Counts a district's free plots, tile by tile and plot by plot (0.7.64): owned and dry - the ground the model builds on, a resource's sites with it (a mine stands on its site; another building takes a site's plot only ... |
| 807 | 18 | `private List<long[]> siteSquares(long bx0, long by0, long bx1, long by1)` | Every site's square, {x0, y0, x1, y1} in plots inclusive, of every resource in fields, owned or not, that reaches into a box of plots: the plots the painter keeps as fields. |
| 827 | 8 | `private District measureNew(int dx, int dy, int own)` | A district not on the map yet, measured, and kept only when it holds free ground: a district of water joins the map only when a site in it needs one (tallySites()). |
| 837 | 1 | `public short[] tileFreeOf(District d)` | A district's tiles' free plots (a copy), counted if they were not: for a harness and a probe. |
| 840 | 9 | `short[] tileFree(District d)` | A district's tiles' free plots, counted if they were not (after a sidecar is read, or for a harness's square city); its room stands as it was. |

### THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN (lines 850-897)

| line | len | member | says |
|---:|---:|---|---|
| 860 | 37 | `boolean syncLand()` | Catches the map up with its land: every district a new purchase touches recounted (new ones added); true when the land was drawn again (a restatement, a conversion), which the caller answers with a canonical map. |

### PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION (lines 898-1030)

| line | len | member | says |
|---:|---:|---|---|
| 913 | 3 | `public boolean reconcile(long[] model)` | THE MONTH (spec-land 2.5, star 12): the land caught up, then each type's change placed - new buildings into the first district with room (nearest first; farms, utilities, mines and wells, the railway and the car plant... |
| 918 | 50 | `public boolean reconcile(long[] model, int month)` | ...in a given month, the game's: what the districts' logs date the month's changes by. |
| 976 | 3 | `void logChanges()` | THE LOG (CL3): into the log of every district whose counts may have moved (logDirty), a record {month, type, change} for each type whose count is not what was last logged, the types in id order, at the map's month; th... |
| 981 | 16 | `void logChanges(boolean all)` | ...of every district, dirty or not (all): what a sidecar is written after, so a count moved by hand (a harness's) is a record too and what is written always adds up. |
| 999 | 3 | `private void batchLogs()` | Every district's whole log so far one batch, placed in today's order: after a canonical deal, a square city's or a sidecar of FORMAT 8 or older read. |
| 1007 | 1 | `public long changes()` | How many changes the map has taken since it was drawn: when it moves, a view stamps the tiles it shows again (batch J4). |
| 1016 | 5 | `void rebind(CityLand live, Function<Resource, double[]> liveRemaining)` | A map drawn away from the screen's thread, on a copy of the land (Game.MapDraft, batch J4), bound to the city's own land and what remains of its resources before it is kept: the copy was the land as it stood, field fo... |

### ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3) (lines 1031-1580)

| line | len | member | says |
|---:|---:|---|---|
| 1049 | 9 | `double roadShareOf(long[] model)` | The model's road plots over its other whole plots. |
| 1060 | 7 | `long buildingRoom(District d, long each)` | How many buildings of `each` whole plots district d has room for, keeping room for the road they and its buildings need at the city's share: k with k x each plus the road short of share(d's other plots + k x each) no ... |
| 1069 | 4 | `boolean hasRoomFor(District d, int t)` | Whether district d has room for one more of type t, as the month places it (MapCheck 2 asks). |
| 1075 | 3 | `double roadLacking(District d)` | The road a district's buildings lack, in plots, at the city's share: share x its other plots less its road plots. |
| 1084 | 22 | `private int placeRoad(int t, long n)` | Places n more roads of type t, one at a time: each into the district, of those the month's buildings went into, whose buildings lack road the most and that has room for it inside its tiles' edge rings, the nearer on a... |
| 1121 | 4 | `private boolean lackFirst(int i, int j)` | Whether heap entry i comes before entry j: more lacking, then the lower index. |
| 1127 | 6 | `private void lackAdd(double lack, int index)` | An entry added at the end, the heap not kept (placeRoad() makes it a heap after). |
| 1134 | 10 | `private void lackPush(double lack, int index)` |  |
| 1146 | 10 | `private int lackPoll()` | The first entry's district index, taken off the heap. |
| 1158 | 10 | `private void lackDown(int k)` | Entry k sifted down to its place below. |
| 1169 | 4 | `private void lackSwap(int i, int j)` |  |
| 1175 | 38 | `private int place(int t, long n)` | Places n more of type t: into the first districts with room for its whole plots in its direction (a road's inside its tiles' edge rings; since 0.7.72 a building's keeping room for its road, buildingRoom()), the rest i... |
| 1232 | 8 | `private int passed(int kind, long each, int i)` | Where a walk of `kind` for buildings of `each` plots from cursor i may start (WALKS PASSED). |
| 1242 | 17 | `private void notePassed(int kind, long each, int to)` | A walk of `kind` for `each` plots found no room before district index `to` (WALKS PASSED). |
| 1300 | 15 | `private int[] campusTypes(int g)` |  |
| 1317 | 3 | `private int[] campusTypes()` | The refinery's unit types, by id. |
| 1322 | 3 | `long campusCells(District d)` | The refinery's ground in district d, in the whole plots its units are drawn on. |
| 1327 | 6 | `long campusCells(District d, int g)` | ...a group's (0.8.0). |
| 1335 | 3 | `District campusDistrict()` | The district holding the most of the refinery's ground, the nearer on a tie; null when it has none. |
| 1340 | 17 | `District campusDistrict(int g)` | ...of a group's (0.8.0). |
| 1359 | 24 | `int gatherCampus()` | The refinery's units in other districts moved into its campus district while it has room, the largest kinds first, then by district in the map's order (a sidecar older than FORMAT 6, read). |
| 1385 | 5 | `District roomiest()` | The district with the most room left, the nearer on a tie: where what no district has room for goes, to be drawn smaller there. |
| 1392 | 21 | `private int remove(int t, long n)` | Takes n of type t away: an inner type from the outermost district holding it in, an outer type from the innermost out. |
| 1415 | 3 | `private void add(District d, int i, int t, long k)` | Adds k (negative to take away) of type t to the district at index i, keeping its ground and plots used, the running total, the type's range, the cursors and the pyramid. |
| 1420 | 21 | `private void add(District d, int i, int t, long k, boolean sited)` | ...a mine or well on a site (sited) taking its whole plots too: its site is its ground, the land the model gives it. |
| 1443 | 5 | `long usedOf(District d)` | The ground a district's buildings use, in half square feet: each type's count x its land, twice. |
| 1450 | 7 | `long roadOf(District d)` | The road plots a district holds. |
| 1459 | 5 | `long cellsOf(District d)` | The whole plots a district's buildings and roads are drawn on, a mine or well on its site taking its own. |
| 1475 | 105 | `private void allocate(long[] counts)` | THE CANONICAL ALLOCATION (spec-land 2.5): the mines and wells on their sites first (0.7.64), then the city's footprint F, in the whole plots it is drawn on, shared inner first - each district its share of F, min(its r... |

### MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) (lines 1581-2122)

| line | len | member | says |
|---:|---:|---|---|
| 1596 | 7 | **type** `static final class SiteIndex` | Every owned site of the SITED resources: per resource and holding, the districts holding them, nearest first, and how many in each. |
| 1605 | 3 | `int holdingOf(long dx, long dy)` | The holding a plot (dx, dy) from the site lies in: 0 the centre, k the k-th purchase, -1 none - the grid's owner (spec-grid 2.4). |
| 1610 | 17 | `private List<Deposit> fieldsNear(Resource r, double bx0, double by0, double bx1, double by1)` | The fields of a resource that reach into a box of plots: the world's as the city sees them (CityLand.fieldsIn(): its old world's ground's own, 0.7.99), and a converted centre's legacy iron field. |
| 1629 | 4 | `Deposit legacyField()` | A converted centre's legacy iron field, as a field of its sites at its plot (cell -1). |
| 1641 | 9 | `SiteIndex siteIndex()` | The site index: every owned site of the SITED resources by holding and district, built with the map and extended by each purchase (a new holding's sites are those of the fields centred on its ground, wherever they lie). |
| 1656 | 65 | `private void tallySites(SiteIndex ix, int from, double bx0, double by0, double bx1, double by1)` | Adds to the index the sites of holdings `from` on that lie in a box of plots from the site, and lays its lists out again: a new index object, so the walks lay themselves out again too. |
| 1723 | 3 | `public long ownedSites(Resource r)` | The owned sites of a SITED resource on the map. |
| 1728 | 9 | `public long ownedSites(Resource r, Deposit.FieldType type)` | ...of one type of its fields (0.8.0, batch I14), or of all of them (null): what a works of that type may stand on. |
| 1739 | 3 | `private boolean onSites(int t)` | Whether type t stands on its resource's sites (placeSited()): a site of a SITED resource, and sites to take - any other building, a site on none, placed as any building is, so the districts always sum to the counts. |
| 1744 | 4 | `static int slotOf(Resource r, Deposit.FieldType type)` | The slot of a resource's fields of a type, or -1 (not SITED, or a type not the resource's). |
| 1754 | 5 | `static int holdingState(double listed, double left)` | A holding's state for a resource from what remains in it: UNWORKED, WORKING or WORKED_OUT (one that listed none, unworked). |
| 1761 | 7 | `int[] holdingStates(Resource r)` | Each holding's state for a resource, the centre first. |
| 1789 | 11 | **type** `private static final class Walk` | One slot's sites in the order works take them (layWalk()), and how far its claims have filled them. |
| 1798 | 1 | `long free()` _(in CityMap.Walk)_ |  |
| 1802 | 9 | **type** `private static final class Lay` | One SITED resource's works on its sites: its slots' walks, laid against an index and its holdings' states, and each claim's works on each slot and on none. |
| 1818 | 19 | `int[] claims(int k)` | A SITED resource's claims: its typed works first, by their type's slot, then those on ANY of its fields; within each the larger first (BuildingVisual.Type.sites()), then by id - first fit decreasing, as LandManager.fi... |
| 1839 | 4 | `private int claimSlot(int t)` | A claim's slot to sort by: its type's, or past every slot for ANY (or a type not its resource's). |
| 1845 | 9 | `private int[] slotsOf(int k, int t)` | The resource's own slots (indexes into Lay.walks) a claim stands on, in order: its type's alone, or every one for ANY. |
| 1856 | 8 | `private boolean lastOnItsSlots(int k, int c)` | Whether claim c is the last laid on each of its slots: no later claim stands on any of them. |
| 1874 | 26 | `private void placeSited(long[] model)` | Puts each SITED resource's works on its sites (spec-land 2.4, 2.5; since 0.8.0 every type of them, by the type of its field and on as many sites as it takes): in each slot the first sites in acquisition order whose ho... |
| 1902 | 18 | `private void relay(int k, SiteIndex ix, int[] states, long[] want)` | Lays a resource's works on its sites again: every one of its types taken off the map, then each claim in order on its slots' walks from their first site. |
| 1922 | 36 | `private void restand(int k, Lay lay, int c, long n)` | One claim to a new count: more filled onto its slots in order from where they stand, then on none; fewer taken off what stands on none first, then off its slots from the last back. |
| 1960 | 23 | `private Walk layWalk(SiteIndex ix, int[] states, int k)` | The walk's order: holdings not worked out in acquisition order, then the worked-out; within each, its districts nearest first. |
| 1985 | 36 | `private long fill(Walk w, int t, int each, long n)` | Up to n works of type t, `each` sites apiece, onto a walk from its next free site - only while a whole one has room - each counted in the district of its first site. |
| 2023 | 28 | `private void empty(Walk w, int t, int each, long n)` | n works of type t, `each` sites apiece, taken back off a walk from its last taken site: the last claim on it, so they are the ones there. |
| 2053 | 22 | `private void markRun(District d, int slot, int holding, int t, int m)` | m more (or fewer) sites of a district's slot, of one holding, under a works of type t: its painting's runs. |
| 2077 | 6 | `private void count(District d, int t, long q)` | q more (or fewer) works of type t counted on sites in a district. |
| 2085 | 3 | `private void sitedNow()` | The mines and wells stood on their sites (placeSited()), once, before anything reads a district's counts less them: a plan's inputs and its stamp (0.7.88), as the sites' lists do. |
| 2089 | 11 | `private District home()` |  |
| 2101 | 1 | `private static int indexOf(District d)` |  |
| 2104 | 4 | `static int siteSlot(Resource r)` | Where a resource sits among SITED, or -1. |
| 2110 | 12 | `private void clearType(int t)` | Takes every one of type t off the map (its sites' runs are its resource's lay's to clear: relay()). |

### THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch (lines 2123-3385)

| line | len | member | says |
|---:|---:|---|---|
| 2177 | 38 | **type** `static final class DrawnCurve` | A district plan's curve as the painter reads it (batch CL4, slice D; DistrictPlan.Curve): kept small - the map keeps up to PLANS_KEPT of these for a city - its geometry as numbers and the plots it is laid on and reach... |
| 2189 | 22 | `DrawnCurve(DistrictPlan.Curve c)` _(in CityMap.DrawnCurve)_ |  |
| 2213 | 1 | `long bytes()` _(in CityMap.DrawnCurve)_ | The bytes it holds, about. |
| 2220 | 101 | **type** `public static final class Drawn` | A district's plan as the painter reads it (0.7.88): each tile's street codes and its buildings' boxes, and the figures a harness reads. |
| 2244 | 1 | `public int joinsOut()` _(in CityMap.Drawn)_ |  |
| 2245 | 1 | `public int partedOut()` _(in CityMap.Drawn)_ |  |
| 2246 | 1 | `public int seamsSurfaced()` _(in CityMap.Drawn)_ |  |
| 2253 | 16 | `long bytes()` _(in CityMap.Drawn)_ | The bytes it holds, about: what the view's budget counts. |
| 2271 | 5 | `public long[] buildingsByType(int types)` _(in CityMap.Drawn)_ | Its plan's buildings, by type id: the boxes counted. |
| 2278 | 6 | `public byte codeAt(int x, int y)` _(in CityMap.Drawn)_ | The street code its plan lays on plot (x, y) of its frame: the district's 256 x 256 and its east column and south row (x or y = DISTRICT); 0 for none. |
| 2286 | 1 | `static byte codeIn(byte[] t, int x, int y)` _(in CityMap.Drawn)_ | A packed tile's code at plot (x, y) of it. |
| 2289 | 3 | `static void unpack(byte[] t, byte[] out)` _(in CityMap.Drawn)_ | A packed tile's codes, a byte a plot row by row, into out. |
| 2294 | 14 | `static byte[] pack(byte[] plots)` _(in CityMap.Drawn)_ | A tile's codes (a byte a plot row by row) packed by row: each row's pattern once, in the order first met. |
| 2310 | 1 | `public double surface(int kind)` _(in CityMap.Drawn)_ | Its surface in plots of kind (BuildingVisual's GRAVEL to HIGHWAY). |
| 2312 | 1 | `public int streets()` _(in CityMap.Drawn)_ | The plots of street it lays, and those of them tracks. |
| 2313 | 1 | `public int tracks()` _(in CityMap.Drawn)_ |  |
| 2315 | 1 | `public int placed()` _(in CityMap.Drawn)_ | Buildings its plan placed, and refused (carried on). |
| 2316 | 1 | `public int refused()` _(in CityMap.Drawn)_ |  |
| 2318 | 1 | `public double surplus()` _(in CityMap.Drawn)_ | Road it has no street for, in plots; of a kind. |
| 2319 | 1 | `public double leftover(int kind)` _(in CityMap.Drawn)_ |  |
| 2323 | 9 | `static byte paintCode(short code)` | A plan's street code as the painter reads it (TilePainter's S_ codes): DistrictPlan's kind and width, its role - an arterial, a boulevard's, or a street (a cell's, along a cut, the join's) - and a bridge. |
| 2334 | 76 | `static Drawn drawnOf(DistrictPlan p, long stamp)` | A plan packed for the painter, with its stamp. |
| 2420 | 35 | `static int[][] accessWays(DistrictPlan p)` | A plan's access roads as ways of plots (FR5, slice B; Drawn.access): for each road the plan laid (DistrictPlan.accessRoads, those that have a gate and an end - a work that already stood on one has neither) the shortes... |
| 2457 | 3 | `static int box(int x, int y, int w, int h, boolean packed)` | A box packed into an int: x, y, w - 1 and h - 1 at five bits each, PACKED_BIT when packed without a street. |
| 2462 | 19 | **type** `static final class Link` | A district's link in the chain (R7): the stamp of its own inputs, what was carried to it and what it carried on ({month, code} pairs in the order they came, since CL3 - the month each came in, so the spill keeps its t... |
| 2474 | 4 | `Link(long own, int[] in, int[] out, long[] free, byte[][] margins, District[] earlier, long[] read, double surplus, long serial)` _(in CityMap.Link)_ |  |
| 2479 | 1 | `long stamp()` _(in CityMap.Link)_ | Its plan's stamp: its own inputs and what came in. |
| 2483 | 8 | `static long stampOf(long own, int[] in)` | A plan's stamp: its own inputs' and the carry in's, {month, code} pairs (CL3): the list's length and every entry folded, zeros included, in order - so two different lists never stamp alike, a list of none and no list ... |
| 2497 | 21 | `static int[] countsOf(int[] pairs, int types)` | A carry's {month, code} pairs as counts by type (CL3): the arrivals (code >= 0, a type id) less the departures (code < 0, ~k: the building the k-th pair brought, so the type of that pair) - what the packing at the edg... |
| 2523 | 16 | `static byte[][] marginsOf(DistrictPlan p)` | A plan's frame edges' street codes (M_WEST to M_SOUTH) and which of them are parted (M_PARTED). |
| 2547 | 8 | `District[] earlierOf(District d)` | The districts about d (AROUND's order) that come before it in the map's order, in its band (0.7.89; CHAIN_BAND), null for the rest: whose plans its plan reads - which of its seams they leave unlaid, and where its stre... |
| 2557 | 4 | `static byte marginAt(byte[][] m, int x, int y, int ox, int oy)` | Where district d's frame plot (x, y) lies in the frame of its neighbour at offset (ox, oy), read from that plan's margins - a plot of its frame's edge - or 0 when it is not on them. |
| 2563 | 4 | `static boolean partedAt(byte[][] m, int x, int y, int ox, int oy)` | ...whether the street there is parted from the network (M_PARTED); false where it is not on them. |
| 2569 | 9 | `private static int marginOf(int x, int y, int ox, int oy)` | ...which margin and place: the margin (M_WEST to M_SOUTH) << 16 \| the index along it, or -1. |
| 2591 | 46 | `static void readNeighbours(District d, District[] earlier, byte[][][] margins, DistrictPlan.Input in)` | What district d's plan reads of the plans before it (0.7.88), from their frames' edges (margins, AROUND's order; null for a district after it or none): ONE-SIDED SEAMS, its seams no district before it sharing them lay... |
| 2638 | 3 | `private static void open(DistrictPlan.Input in, int x, int y, byte code)` |  |
| 2643 | 5 | **type** `static final class Pack` | What the city has no room for, packed at its edge (R7): each edge district's packed boxes by tile, an int each as Drawn.boxes; how many were packed, how many drawn smaller than their own land, and how many found no gr... |
| 2677 | 1 | `static int band(int i)` | The band of the district at index i in the map's order. |
| 2680 | 1 | `int bands()` | Bands in the map's order. |
| 2694 | 3 | `private long planEpoch()` | What the plans hang on, cheaply: the map's changes, and the land's purchases (a purchase owns ground at once, before the month measures it; the land drawn again draws a new map). |
| 2699 | 4 | `boolean isEdge(District d)` | Whether district d is at the city's edge: a neighbour of its eight is not on the map - where what the city has no room for is packed (R7). |
| 2713 | 38 | `long ownStamp(District d)` | A stamp of everything district d's own plan is drawn from but what is carried to it: its buildings and road by type (its mines and wells on sites apart) and the log they came in (CL3), the ground the city owns over it... |
| 2753 | 9 | `private void sizeBands()` | The bands' arrays sized to the districts, every band to be checked again. |
| 2764 | 4 | `private void bandsStale()` | Every band to be checked again (a plan adopted). |
| 2770 | 22 | `private void checkBand(int b)` | Band b's links walked in the map's order at the current epoch: its bandValidTo is the first whose own stamp moved, whose carry in is not the one before's carry on (none for the band's first), or (an edge district) tha... |
| 2794 | 8 | `private void useBand(int b)` | Marks band b used, letting go of the bands used longest ago past BANDS_KEPT. |
| 2804 | 11 | `private void forgetBand(int b)` | Lets go of band b's links, plans and packing: drawn again when a screen asks for it. |
| 2817 | 8 | `private boolean allDrawn()` | Whether every band's links hold and its packing is current: the whole city drawn. |
| 2827 | 8 | `private int[][] packedIn(District d)` | District d's band's packing of what the band cannot hold, by tile, when current; null for none. |
| 2837 | 10 | `private boolean readHolds(Link l)` | Whether the plans a link read its neighbours' edges from are those standing: each such district's link the one it read (they are before it in the map's order, walked first). |
| 2849 | 11 | `public boolean planned(District d)` | Whether district d's plan is drawn and current, and every link before it in its band holds; for an edge district, its band's packing too: its tiles paint without planning. |
| 2862 | 16 | **type** `public abstract static class Job` | A job to run away from the screen's thread (0.7.88): run() on any one thread, in the order handed out, then adopt() on the map's. |
| 2867 | 1 | `Job(CityMap map, long epoch)` _(in CityMap.Job)_ |  |
| 2869 | 5 | `public final synchronized void run()` _(in CityMap.Job)_ | Does the work: any thread, the jobs handed out in their order on one; safe to call twice. |
| 2874 | 1 | `abstract void work()` _(in CityMap.Job)_ |  |
| 2876 | 1 | `public final synchronized boolean done()` _(in CityMap.Job)_ | Whether it has run. |
| 2880 | 79 | **type** `public static final class PlanJob extends Job` | One district's plan: its inputs gathered on the map's thread, its ground read and its plan drawn by run(), with what the job before it carried on (R7). |
| 2903 | 9 | `PlanJob(CityMap map, District d, DistrictPlan.Input in, long own, PlanJob after, int[] given, boolean edge, District[] earlier,...` _(in CityMap.PlanJob)_ |  |
| 2913 | 36 | `void work()` _(in CityMap.PlanJob)_ |  |
| 2951 | 1 | `public double ms()` _(in CityMap.PlanJob)_ | How long it took, in ms: a harness's figure (its plan's; its picture's apart, pictureMs()). |
| 2954 | 1 | `public double pictureMs()` _(in CityMap.PlanJob)_ | How long its picture took, in ms - its plots, and its pixels when it drew them: a harness's figure. |
| 2957 | 1 | `public boolean drewPicture()` _(in CityMap.PlanJob)_ | Whether it drew a picture (the one kept was not of its plots): a harness's figure. |
| 2961 | 41 | **type** `public static final class PackJob extends Job` | A band's packing at its edge (R7; the city's, in a city of one band): what its last link carried on, into its edge districts' free ground - their links', or the jobs' that make them. |
| 2971 | 4 | `PackJob(CityMap map, District[] edges, Object[] from, Object last, long[] tileOrder, long epoch)` _(in CityMap.PackJob)_ |  |
| 2976 | 25 | `void work()` _(in CityMap.PackJob)_ |  |
| 3011 | 8 | `public List<Job> planJobs(District d)` | The jobs that make district d's plan current (R7: every district before it in the map's order whose link does not hold, each after the one before it), and, for an edge district, the city's packing after them: to run i... |
| 3021 | 31 | `private PlanJob chainTo(int i, List<Job> out)` | The plan jobs up to the district at index i from its band's first, into out; the last job of the chain to it (handed out now or before), or null when its link holds. |
| 3054 | 10 | `private Object[] sources(District[] earlier)` | Each earlier district's plan as a job reads it: its job handed out at this epoch, else its link (it is before the job's district in the map's order, so one or the other holds). |
| 3066 | 24 | `private void packAfter(int b, List<Job> out)` | Band b's packing at its edge, after the chain to its last district, into out - unless it is current or handed out at this epoch. |
| 3092 | 21 | `private long[] tileOrder(List<District> edges)` | The edge districts' tiles in the order the packing spreads over them: each a district's index in `edges` and the tile, the farthest tile from the founding site first. |
| 3115 | 26 | `public boolean adopt(Job j)` | Keeps a job's work: a plan's link and its packed plan, the city's packing. |
| 3143 | 13 | `Drawn drawnOf(District d)` | District d's plan for the painter, made here when it is not current - every district before it in the map's order first where its link does not hold, and for an edge district the city's packing - on this thread: a har... |
| 3158 | 7 | `void packed()` | Every band's packing at its edge, made here when it is not current (a harness's): the whole city drawn. |
| 3167 | 11 | `private int[] packSums()` | The bands' packings summed: {packed, smaller, none}; null when one is not current. |
| 3180 | 5 | `public int[] packedCounts()` | How many buildings the city has no room for in any district's plan, packed at its edge (R7; since 0.7.89 at its band's edge) - and how many of them drawn smaller than their own land or with no ground at all: {packed, ... |
| 3187 | 3 | `public int[] packedCountsIfDrawn()` | ...the same, or null when the packing is not current: the screen's thread, which never plans. |
| 3192 | 7 | `public long[] legendFiguresIfDrawn()` | The legend's figures once every plan and the packing are drawn (never planned here: the screen's thread): {buildings packed at the city's edge, plots of road its streets do not carry (star RD2-4) - since 0.7.89 with t... |
| 3201 | 4 | `long dealtSpurTrack()` | The track plots of the Rail Spurs dealt to the plans' works (sidingsOf()), a spur's share of the model's spur track each: used by a siding the plans lay, so no longer the legend's "track more than the line uses" (batc... |
| 3207 | 10 | `public double[] leftoverByKind()` | The road the plans have no street for, by kind [0, gravel, paved, highway], over every district (made now if need be), and (0.7.89) the Elevated Highway plots the runs could not lay: a harness's - with the surface dra... |
| 3219 | 4 | `public long[] legendFigures()` | ...the same, made now if need be: a harness's. |
| 3225 | 1 | `public Drawn drawn(District d)` | District d's plan as drawn (made now if need be): a harness's and a probe's. |
| 3228 | 5 | `public int[][] carried(District d)` | What was carried to district d and what it carried on (R7), counted by type id, null for none - its link's, once planned. |
| 3235 | 1 | `int linksKept()` | The links kept (0.7.89: a band's let go past BANDS_KEPT): a harness's. |
| 3238 | 5 | `public long plansBytes()` | The bytes the kept plans hold: what MapTiles' budget counts. |
| 3250 | 25 | `static long[] freeGround(DistrictPlan.Input in, DistrictPlan p)` | A plan's free ground (R7), for an edge district: a bit a plot of the district's 256 x 256, set where the city owns dry ground no street, building, highway or its verge (H5), track or mine's site takes - a field's plot... |
| 3290 | 55 | `static Pack packAtEdge(int[] carry, BuildingVisual.Type[] types, District[] edges, long[][] free, long[] tileOrder)` | PACKED AT THE CITY'S EDGE (R7; spec 2.6: "a city with no room anywhere packs them at its edge, without a street, and the map's legend says how many"). |
| 3350 | 35 | `private static int[] fitTile(long[][] free, long key, int w, int h, int[][] failW, int[][] failH, int[] fails, int ti)` | The first spot, in rows, on tile key (edge index << 32 \| tile) of the free ground where a w x h box - or h x w - fits inside the tile's interior: {edge index, tile, x, y, w, h} in the tile's plots, or null (and the sh... |

### THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md (lines 3386-3490)

| line | len | member | says |
|---:|---:|---|---|
| 3413 | 18 | `int[] extrasIn(District d)` | The boxes the runs and the shore stand on district d's ground, as the painter draws them over its plan (fillInput()): its yards as Rail Terminals and its terminals and tank farms, each {x, y, w, h, type id} in the dis... |
| 3433 | 10 | `public DistrictPicture pictureIfCurrent(District d)` | District d's picture when it is its plan's, the plan current - every link before it in its band holding - else null: the screen's thread, which never plans. |
| 3452 | 16 | `public List<Job> pictureJobs(District d)` | The jobs that draw district d's picture: its plan's chain, as planJobs() hands it out but for its band's packing at the edge, which a picture does not draw (star MM1-3); or, where its plan is current and its picture i... |
| 3470 | 11 | `public DistrictPicture picture(District d)` | District d's picture, drawn here when it is not current (pictureJobs() run and kept on this thread): a harness's and a probe's. |
| 3483 | 1 | `public long picturesKept()` | How many pictures have been kept since the map was drawn (picturesKept). |
| 3486 | 1 | `public int picturesHeld()` | The pictures kept now: what MapTiles' budget counts PICTURES_KEPT of, DistrictPicture.BYTES each. |
| 3489 | 1 | `public static int bandOf(District d)` | The band district d's links are kept in (CHAIN_BAND districts of the map's order a band): what a far view counts the bands it would plan by (MapTiles.picturesFit()). |

### THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch (lines 3491-3684)

| line | len | member | says |
|---:|---:|---|---|
| 3539 | 9 | `private CityRuns guarded(CityRuns r)` | The runs given the works an interchange's reserve may not take (batch FR7, InterchangeGround.Guard): the mines on their sites and the shore's works. |
| 3550 | 7 | `private boolean reserveBlocked(long x0, long y0, long x1, long y1)` | Whether a mine on its site or a work on the shore stands in the box (x0, y0) to (x1, y1), inclusive: ground an interchange's reserve may not take. |
| 3559 | 1 | `public CityRuns runs()` | The city's runs. |
| 3562 | 1 | `public int gateway()` | The gateway's bearing, as CityRuns keeps it: GATEWAY_UNKNOWN until it is looked for, NO_GATEWAY where the land has none. |
| 3577 | 1 | **type** `public record WorldLine(int net, double x, double y, int bearing, int arm, int way, double reach)` | A net's line out to the world (spec-freight 4.1): the highway's or the railway's run, from the first plot past the city's ground, faded to the edge of the view and labelled "to the world". |
| 3603 | 14 | `public List<WorldLine> worldLines()` | The world lines (spec-freight 4.1): for each net whose gateway is found and which has started, the line to the world that worldLine() finds - where an arm's track ends at the city's edge on dry ground, and the way out... |
| 3619 | 1 | **type** `private record WayOut(double score, int arm, int which, int way, long ex, long ey)` | One way out of the city from an arm at its edge: the score it is tried by, the arm, 0 for the gateway's bearing or 1 for the arm's heading, the degrees, and the edge plot. |
| 3633 | 41 | `private WorldLine worldLine(CityRuns.Net n, int gateway, List<WorldLine> out)` | Adds net n's line to the world to out and returns it; null (none added) for a net that has not started, for the highways while they reach no street (CityRuns.highwaysReach(), FX2: they are not drawn), or that has no a... |
| 3676 | 8 | `private static double dryRun(World w, double x, double y, int deg, double most)` | How far from (x, y) the way out along deg degrees runs over dry ground, in plots: the distance to the first point, read every WORLD_STEP, that lies on a plot that is not World.dry(); `most` where none does. |

### THE TRAINS' ROUTES (0.8.2, batch FR1; spec-freight 4.4, D16) (lines 3685-4108)

| line | len | member | says |
|---:|---:|---|---|
| 3695 | 1 | **type** `private record DrawnBend(TrackCurves.Bend bend, boolean doubled)` | A bend of the routes the tiles draw (FX5), with whether it lies on double track (its corner plot's M_DOUBLE). |
| 3701 | 1 | `private static long bendKey(long tx, long ty)` | A tile's key in trainBends. |
| 3736 | 40 | `public List<TrainSchedule.Route> trainRoutes()` | The trains' routes (TrainSchedule.Route): for each yard the railway's way reaches, from TrainSchedule.WORLD_PLOTS out along the railway's world line (along line.way(), over dry ground: FX2), in to the main arm's end (... |
| 3786 | 25 | `private Map<Long, List<DrawnBend>> drawnBends(List<TrackCurves.Bend> found)` | The bends the routes make (TrackCurves.bends() of each route's points before it is rounded) that the tiles draw (FX5), by the tiles they reach: those whose corner and both tangent points lie on plots the runs lay trac... |
| 3828 | 29 | `private List<TrackCurves.Bend> highwayBendsIn(long tx, long ty)` | The highways' bends that reach tile (tx, ty) (FX5 slice D): the turns of each arm's polyline (CityRuns.highwayPolylines()) rounded by TrackCurves.bends() at TrackCurves.HIGHWAY_RADIUS, of net HIGHWAY - a 45-degree str... |
| 3859 | 4 | `private boolean highwayPlot(long x, long y)` | Whether the runs lay highway on plot (x, y): a highway, or a highway under the railway's bridge. |
| 3865 | 12 | `private boolean railPlot(List<long[]> yards, boolean[][] yardRails, long x, long y)` | Whether the runs lay track on plot (x, y): track, track bridging a highway, or the track in a yard's box (yardRails caches each yard's, by index). |
| 3879 | 1 | **type** `private record YardLayouts(long at, int[][] by)` | The yard layouts (TileRaster.yardLayout() of yardRail()) as the map's changes left them: valid at `at`, one per yard of the runs, made when first asked. |
| 3888 | 15 | `int[] yardLayoutOf(int yi)` | Where yard yi's fan lies in its box (FX5 Y2): TileRaster.yardLayout() of the box's track as the runs lay it (yardRail()), the layout trainRoutesInto() lays the routes on and the tiles draw the yard from. |
| 3905 | 8 | `static boolean[] yardRail(CityRuns runs, long[] yard)` | The track in a yard's box, plot by plot (row by row, yard[2] across and yard[3] down): where the runs lay the railway, as the tile the yard is drawn on carries it (TileRaster.yardLayout()'s input; FX3, 0.8.4). |
| 3928 | 4 | `static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way, double fx,...` | Adds one yard's routes to out: the world line's part (far end, then where it leaves the city's ground), the way to the box's edge, and the ladder onto each siding. |
| 3934 | 4 | `static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way, double fx,...` | ...with the yard's own number of sidings (TileRaster.tracksOf(): a Rail Depot's three, FR7 slice 3), one route to each. |
| 3946 | 5 | `static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way, double fx,...` | ...and (FX5) the bends of the routes' points before they are rounded (TrackCurves.bends()) added to bendsOut, where it is not null: the turns the tiles draw. |
| 3956 | 111 | `static void trainRoutesInto(List<TrainSchedule.Route> out, int yardAt, boolean station, long[] yard, double[][] way, double fx,...` | ...and (FX5, slice T) with the yard's first `standing` sidings from the shed's side left without a route, the wagons standing on them (TileRaster.wagonTracksOn()): the routes are those of the sidings after, each with ... |
| 4069 | 1 | **type** `private record Rounded(double[] xs, double[] ys, double fadeTo)` | A polyline's points rounded by TrackCurves (RADIUS), with the world line's part: how far along the rounded polyline its first straight lasts. |
| 4079 | 10 | `private static Rounded rounded(List<double[]> pts, List<TrackCurves.Bend> bendsOut)` | The route's points rounded (FX5): every turn replaced by its arc of TrackCurves.RADIUS plots, less where the legs beside it are short; the turns found are added to bendsOut where it is not null. |
| 4091 | 5 | `private static void addNew(List<double[]> pts, double x, double y)` | Adds the point (x, y) to pts unless the last one is there (no leg of no length). |
| 4098 | 4 | `private static TrainSchedule.Route route(int yard, boolean station, int siding, List<double[]> pts, List<TrackCurves.Bend> bend...` | A route through points, rounded at every turn (FX5), the world line's part its first leg's straight. |
| 4104 | 4 | `private static TrainSchedule.Route routeToWorks(List<double[]> pts, List<TrackCurves.Bend> bendsOut, double holds)` | ...to a works' siding (FR7 slice 3): through the points, rounded, the world line's part its first leg's straight, no yard; a bulk unit train's alone. |

### THE TRUCKS' ROUTES (0.8.8, batch FR5; spec-freight 4.7, D21) (lines 4109-4646)

| line | len | member | says |
|---:|---:|---|---|
| 4153 | 27 | `public List<TruckSchedule.Route> truckRoutes()` | The trucks' highway routes (TruckSchedule.Route, world true): for each ramp the highways have (CityRuns.highwayRamps(): their arms' ends), from TruckSchedule.WORLD_PLOTS out along the highways' world line (along line.... |
| 4182 | 5 | `private static TruckSchedule.Route truckRoute(boolean world, List<double[]> pts, double fadeTo)` | A truck route through points, fadeTo plots faded in. |
| 4189 | 1 | **type** `public record DistrictRoutes(long key, List<TruckSchedule.Route> routes)` | One district's access roads' routes, with the stable key it is told apart by: the district's own, the same every call. |
| 4192 | 4 | **type** `private static final class AccessRoutes` | A district's routes as last made, and what they were made against: its Drawn's stamp and its frame's origin in world plots. |
| 4214 | 18 | `public List<DistrictRoutes> accessRoutesIn(long x0, long y0, long x1, long y1)` | The access roads' routes (TruckSchedule.Route, world false) of the districts already drawn whose frame meets the box (x0, y0) to (x1, y1), in world plots, the corners inclusive: for each district its roads, each from ... |
| 4234 | 21 | `private void accessRoutesOf(District d, List<DistrictRoutes> out)` | Adds district d's access routes to out when its plan is drawn and current and it has any. |
| 4257 | 14 | `private static void accessRouteInto(List<TruckSchedule.Route> out, int[] way, long ox, long oy)` | Adds the route of one access road (Drawn.access: frame indices, the end first) to out: its plots' middles in world plots from the frame's origin (ox, oy), a straight run kept to its two ends. |
| 4281 | 12 | `public int accessRoadCount()` | How many works the city has that need an access road (batch FR7, D17): the railway's yards the runs draw, the shore's berthed works (sea terminals) and the Truck Depots among its buildings - read from what the map hol... |
| 4311 | 87 | **type** `private final class RunGround implements CityRuns.Ground` | The ground the runs read (CityRuns.Ground): a plot's World class where the city owns it, -1 where it does not - the terrain a tile's row or column at a time (World.lineTerrain(), byte for byte the painter's tileTerrai... |
| 4327 | 35 | `public int at(long x, long y, int dir)` _(in CityMap.RunGround)_ |  |
| 4364 | 23 | `private boolean ownedAt(long tx, long ty, int ix, int iy)` _(in CityMap.RunGround)_ | Whether the city owns plot (ix, iy) of tile (tx, ty): the grid's cover of the tile, its leaves where it owns some. |
| 4388 | 3 | `public long version()` _(in CityMap.RunGround)_ |  |
| 4392 | 5 | `public long[] box()` _(in CityMap.RunGround)_ |  |
| 4402 | 1 | `CityRuns.Ground runGround()` | The ground the runs read: a harness's, to lay runs of its own on the city's ground. |
| 4405 | 1 | `long[] site()` | The founding site, {x, y} in plots. |
| 4408 | 6 | `private boolean hasFreightLine(long[] counts)` | Whether the counts have any Freight Line, whose track makes the main line double. |
| 4428 | 54 | `boolean layRuns(long[] model)` | Lays the runs to the model's counts (CityRuns.layTo()): its Elevated Highways' plots, its track's, its Rail Terminals and Rail Depots as yards, each asked for its type's footprint (FR7 slice 3: Terminals first, then D... |
| 4489 | 13 | `private int[] yardSequence(long[] counts)` | The type of each yard the model has, in the order the runs lay them (FR7 slice 3): every Rail Terminal (by type index), then every Rail Depot - each yard is asked for its type's footprint (laid YARD_LONG along its lin... |
| 4507 | 9 | `private int[] yardSequenceOf(long[] counts)` |  |
| 4517 | 3 | `private int[] haveSequence()` |  |
| 4522 | 5 | `private int[] footprintOf(int t)` | A type's footprint, {across, down} in whole plots (BuildingVisual.footprint()), kept: the size a yard is asked for, every yard every month (FX5 Y1: laid so long along its line as the ground allows, CityRuns.YARD_LONG,... |
| 4531 | 5 | `int yardTypeOf(int i)` | The type index of the runs' yard i: the model's sequence (yardSequence() of the map's counts), the first terminal type past it; -1 with no terminal type. |
| 4538 | 4 | `private int firstTerminal()` | The first Rail Terminal type's index, or -1. |
| 4544 | 5 | `List<long[]> minedSites()` | The city's mines and wells standing on their sites, each {x0, y0, x1, y1} inclusive: where the railway starts and its yards are drawn near. |
| 4551 | 5 | `List<long[]> bulkSites()` | ...and (FR7 slice 3) of those, the BULK works' (BuildingVisual.Type.bulk(): a mine, a quarry - not a well): the works a siding is laid into and a further yard stands beside. |
| 4558 | 14 | `private void scanMinedSites(List<long[]> all, List<long[]> bulk)` | The mined sites into `all` and the bulk works' among them into `bulk` (either null to leave out): one pass over the districts' site lists. |
| 4582 | 12 | `private void refreshMines()` |  |
| 4600 | 10 | `private int[] terminalTypes()` | The terminal types' indices (BuildingVisual.Type.terminal()), in type order; worked out once (the types are the map's for life). |
| 4614 | 25 | `int yardsOf(District d, int t)` | How many of district d's buildings of terminal type t are the runs' yards. |
| 4641 | 5 | `int yardsOf(District d)` | How many of district d's Rail Terminals and Depots are the runs' yards. |

### the Rail Spurs the runs' sidings left: dealt to the plans' works (batch FR7 slice 5, D14) (lines 4647-4786)

| line | len | member | says |
|---:|---:|---|---|
| 4659 | 3 | `int spursLeft()` | The Rail Spurs the runs' sidings did not take: the model's less the sidings the runs laid into mined sites. |
| 4664 | 4 | `long sidingsDealt()` | How many Rail Spurs are dealt to the districts' plans in all (sidingsOf()). |
| 4682 | 10 | `int sidingsOf(District d)` | How many sidings district d's plan lays (batch FR7 slice 5, D14): its share of the Rail Spurs the runs' sidings left (spursLeft()). |
| 4693 | 26 | `private void dealSidings(int[] share)` |  |
| 4721 | 3 | `long runsInto(District d, byte[] fixed)` | The runs' frame for district d's plan: their plots over its 257 x 257 (DistrictPlan's codes), a hash of them returned. |
| 4726 | 14 | `long runsInto(District d, byte[] fixed, boolean[] ramp)` | ...and (batch FR7) the frame's highway plots that carry a ramp into `ramp` (null to leave them): where an access road may end. |
| 4751 | 8 | `void worksInto(District d, DistrictPlan.Input in)` | The works of the city that need an access road and stand in district d's frame (batch FR7, D17): each Rail Terminal and Rail Depot the runs draw as a yard, each sea terminal on the shore (a tank farm has no berth and ... |
| 4766 | 6 | `static int[] workIn(long[] yard, long x0, long y0)` | A yard's box as the district frame whose first plot is (x0, y0) holds it, {x, y, w, h} in the frame's plots, or null when another frame holds more of it. |
| 4774 | 8 | `private static boolean holdsMost(long p, long n, long f)` | Whether the frame whose first plot is f holds more of the n plots from p than any other frame does; a tie to the first. |
| 4783 | 3 | `private static void addWork(DistrictPlan.Input in, long x, long y, long w, long h)` |  |

### THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13; (lines 4787-5035)

| line | len | member | says |
|---:|---:|---|---|
| 4807 | 1 | `public CityShore shore()` | The city's works on its shore (0.7.97). |
| 4812 | 11 | `int[] shoreTypes()` |  |
| 4831 | 1 | `public int shoreShort()` | How many of the model's terminals and tank farms the shore has no place for: their districts' plans draw them. |
| 4834 | 3 | `private long groundKey()` | The ground's key, without the shore's own works: what a search that came up short waits on. |
| 4843 | 53 | `boolean layShore(long[] model)` | Lays the shore to the model's counts: one fewer of a type takes its newest work; one more is laid where shoreSpot() finds it, the first types first. |
| 4897 | 4 | `private static boolean anyOf(long[] model, int[] ids)` |  |
| 4911 | 68 | `CityShore.Work shoreSpot(int t, List<long[]> mines)` | Where the next work of type t goes (CityShore's rule), or null: the districts in the map's order, each one's tiles nearest the founding site first; on each tile with owned salt water and owned dry ground, its shore pl... |
| 4981 | 20 | `private static boolean shoreFits(CityShore.Work c, int[] ground, byte[] fixed, long wx0, long wy0, int ww)` | Whether a work fits where c puts it, on the window's ground and runs from (wx0, wy0), ww a side: its box in one cell's interior on owned dry ground no run, mine or other work takes, no highway beside it, and its quay ... |
| 5003 | 8 | `static boolean nearRing(CityShore.Work c)` | Whether a work's box lies within TilePainter.REACH of its cell's ring on a side away from the sea: where an arterial runs when the cell is open. |
| 5017 | 18 | `int shoreOf(District d, int t)` | How many of district d's buildings of type t the shore holds. |

### THE OIL AT SEA (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the (lines 5036-5132)

| line | len | member | says |
|---:|---:|---|---|
| 5049 | 1 | **type** `public record Jacket(double x, double y, int wells, List<Long> wellPlots)` | A platform as the map draws it: its jacket's middle in plots, its wells, and the plots its wells stand on (each x << 32 \| y). |
| 5052 | 1 | **type** `public record Pipe(double x0, double y0, double x1, double y1)` | A pipe as the map draws it: from its field's middle (x0, y0) toward the founding site, to (x1, y1), in plots - its kilometres standing. |
| 5055 | 1 | **type** `public record RailPicture(boolean goodsByRail, double wagonShare)` | The railway as the map draws its yard: whether it carries goods, and the share of the track's wagon sets the fleet has standing. |
| 5060 | 1 | `public RailPicture railPicture()` | The railway's picture, as the game last handed it. |
| 5063 | 3 | `static int wagonTracksOf(double share)` | The whole wagon tracks a share of the fleet gives: its share of YARD_TRACKS, rounded to a whole track. |
| 5068 | 8 | `public void railPicture(RailPicture r)` | The railway's picture handed over by the game (Game.mapRail()); a change of goods or of the whole wagon tracks restamps the tiles. |
| 5077 | 3 | `private static double clamp(double v, double lo, double hi)` |  |
| 5082 | 4 | `public long[] freightStation()` | The freight station's yard, {x0, y0, w, h} in world plots: the yard nearest the rail main arm's end (spec 4.2; CityRuns.freightStation()); null for none. |
| 5088 | 4 | **type** `public record AtSea(List<Jacket> jackets, List<Pipe> pipes)` | The oil at sea: its jackets and pipes. |
| 5096 | 1 | `public AtSea atSea()` | The oil at sea, as the game last handed it. |
| 5099 | 6 | `public void atSea(AtSea a)` | The oil at sea handed over by the game (Game.mapAtSea()); a change restamps the tiles it crosses. |
| 5107 | 25 | `private void atSeaInto(long px0, long py0, TilePainter.Input in)` | The oil at sea over tile (px0, py0)'s plots, into its inputs: the jackets' and wells' plots, the rings and pipes that may cross it. |

### THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1) (lines 5133-5217)

| line | len | member | says |
|---:|---:|---|---|
| 5147 | 6 | `double cityRadius()` | The city's radius for the offing, in plots: the farthest corner of its owned box from the founding site. |
| 5155 | 5 | `private long routesKeyNow()` | The key the routes are found against: the berths, the founding site and the city's radius. |
| 5162 | 8 | `public List<BoatSchedule.Route> seaRoutes()` | The routes now, found here when they are not (a harness's way; the view hands a RoutesJob to its worker). |
| 5172 | 3 | `public List<BoatSchedule.Route> seaRoutesIfFound()` | The routes when they are found and current, else null: the screen's thread, which never finds them itself. |
| 5177 | 25 | **type** `public static final class RoutesJob implements Runnable` | The berths' routes found away from the screen's thread: what they are found from, copied here. |
| 5185 | 9 | `RoutesJob(long key, World.Key world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius)` _(in CityMap.RoutesJob)_ |  |
| 5195 | 3 | `public void run()` _(in CityMap.RoutesJob)_ |  |
| 5200 | 1 | `public boolean done()` _(in CityMap.RoutesJob)_ | Whether it has run. |
| 5204 | 5 | `public RoutesJob routesJob()` | The job that finds the routes now, or null when they are current. |
| 5211 | 6 | `public boolean adoptRoutes(RoutesJob j)` | A job run: its routes kept when they are still the city's. |

### A TILE'S INPUTS FOR THE PAINTER (lines 5218-5711)

| line | len | member | says |
|---:|---:|---|---|
| 5223 | 3 | `public District districtOfTile(long tx, long ty)` | The district holding tile (tx, ty), or null. |
| 5228 | 3 | `static int tileIn(long tx, long ty)` | The tile in district d at its column and row of tiles (tile = row x 8 + column), from world tile (tx, ty). |
| 5233 | 10 | `public void tileCounts(long tx, long ty, int[] out)` | A tile's buildings by type id as its district's plan draws them (0.7.88; the deal's until 0.7.87), packed ones among them: into out, zeros where no district is. |
| 5245 | 8 | `public void tileOwnership(long tx, long ty, boolean[] out)` | Which of a tile's plots the city owns: a tile owned wholly or not at all in one test (the grid's cover at level 5), the rest from the grid's leaves under it (spec-grid 2.4). |
| 5264 | 4 | `public void tileInput(long tx, long ty, TilePainter.Input in)` | Everything the painter needs for tile (tx, ty), into in: its ground, what the city owns of it, and from its district's plan (0.7.88; the deal's counts and road plots until 0.7.87) the streets through it and every buil... |
| 5270 | 4 | `public void tileInput(long tx, long ty, TilePainter.Input in, byte[] terrain)` | ...with the tile's ground given (a view keeps it: MapTiles.terrain(), batch J4) rather than read from the world. |
| 5276 | 10 | `public List<District> tileDistricts(long tx, long ty)` | The districts a tile's picture is drawn from (0.7.88): those of the tiles about it - its own, the ones whose plans lay the street on its first column or row (west, north, north-west) and those its edges look across to... |
| 5288 | 4 | `public boolean tileReady(long tx, long ty)` | Whether tile (tx, ty) paints without planning: every plan it is drawn from current (planned()). |
| 5294 | 5 | `public List<Job> tileJobs(long tx, long ty)` | The jobs that make tile (tx, ty)'s plans current (planJobs()), in order; empty when they are. |
| 5308 | 3 | `byte streetAt(long x, long y)` | The street a plan draws on world plot (x, y) (TilePainter's S_ codes): the district's own; on its first column or row the district west or north of it (or north-west, at its corner) lays the same plot as its frame's m... |
| 5313 | 32 | `private byte streetAt(long x, long y, Map<Long, Drawn> seen)` | ...the plans looked up through `seen`, by district key (null for none), which it fills. |
| 5353 | 43 | `private void fillAvenues(long px0, long py0, TilePainter.Input in, Map<Long, Drawn> seen)` | The avenues' marks on the tile at world plot (px0, py0) and the REACH plots about it (batch CL4), into in.avenue and in.avenueRing, and their origin when it lies near: each plan of the districts the ring reaches (seen... |
| 5405 | 40 | `private void fillCurves(long px0, long py0, TilePainter.Input in, Map<Long, Drawn> seen)` | The curves that reach the tile at world plot (px0, py0) and the REACH plots about it (batch CL4, slice D), into the input's curves: each plan of the districts the ring reaches (seen, looked up as fillAvenues() has the... |
| 5447 | 175 | `private void fillInput(long tx, long ty, TilePainter.Input in)` | Everything tileInput() fills but the ground, which is in `in` already. |
| 5624 | 1 | **type** `record DrawnSite(long x0, long y0, long x1, long y1, int kind, int state, int mine)` | One site as the map draws it: its square of plots (inclusive), resource, state and the mine on it (-1 for none). |
| 5640 | 57 | `List<DrawnSite> siteList(int dx, int dy)` | The sites whose centres lie in district (dx, dy) - every resource's, owned or not - each with its holding's state (its field's holding, the one holding the field's centre, since 0.7.64) and, for the SITED resources, t... |
| 5699 | 12 | `public void forgetPainted()` | Forgets the painted state of every district - the plans, their links, the packing and the site lists - after the ground's states moved (what is worked out). |

### THE RUNS DATED (batch FX7, 0.8.24) (lines 5712-5936)

| line | len | member | says |
|---:|---:|---|---|
| 5769 | 3 | `private static boolean journalKind(int k)` | The kinds a journal's record holds: 0, a highway's, the railway's and a yard's. |
| 5792 | 1 | `public long journalDropped()` | How many plots' records the plans' inputs have left out because the journal did not end on the runs as they stand: 0. |
| 5795 | 7 | `private void beginJournal()` | Starts a month's journalling: the witness set on the runs, the highways' reach noted. |
| 5804 | 11 | `private void witnessed(long wx, long wy)` | The runs tell the map that world district (wx, wy) is about to change: its kinds are read, once a month, as they are now. |
| 5821 | 8 | `byte[] kindsOf(District d, boolean reach)` | What a district's own DISTRICT x DISTRICT plots carry in a plan's input, the interchanges' reserves left out: the runs as runsInto() maps them (a railway over a highway is a highway's) with the shore's works over them... |
| 5831 | 18 | `private void endJournal()` | Ends a month's journalling, the runs and the shore laid: each district read is read again, and a record made of every plot whose kind differs, at the map's month. |
| 5851 | 8 | `private long journalStamp(District d, long h)` | `h` with district d's journal over its frame folded in (ownStamp()): its own, and its east neighbour's first column, its south neighbour's first row and its south-east neighbour's first plot - nothing where there is n... |
| 5869 | 67 | `private void fixedLogInto(District d, DistrictPlan.Input in)` | District d's frame's records for its plan's input (Input.fixedLogMonth and fixedLog): its own journal's, and its east neighbour's records on its first column (the frame's last), its south neighbour's on its first row ... |

### THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's (lines 5937-6093)

| line | len | member | says |
|---:|---:|---|---|
| 5956 | 3 | `DistrictPlan plan(District d)` | District d's own plan, drawn now from planInput(d) - its own buildings alone, none carried to it (R7): not kept (THE DRAWN PLANS keep the painter's). |
| 5961 | 5 | `DistrictPlan.Input planInput(District d)` | The inputs of district d's plan (DistrictPlan.Input), its ground read from the world. |
| 5968 | 15 | `static void readGround(DistrictPlan.Input in, World.Key world)` | The ground over a plan's frame, a World class a plot, read from the world the key names (0.7.115; the input's seed's until then): any thread (World is safe), so a plan's job reads it away from the screen's (0.7.88). |
| 5985 | 108 | `DistrictPlan.Input planInputHere(District d)` | ...everything but the ground: what the map's own thread reads (the land, the counts, the sites, the runs). |

### ZONES BY DISTRICT (0.8.0, batch I14; docs/studies/spec-zoning.md 4: (lines 6094-6164)

| line | len | member | says |
|---:|---:|---|---|
| 6112 | 1 | **type** `public record ZoneArea(String sector, double sqFt)` | A zone as the map tints it: its sector and the ground it covers, in square feet - what it holds free and what its sector has built in it (LandManager.heldBy() and usedBy()). |
| 6115 | 1 | **type** `public record Tinted(long x0, long y0, int zone)` | A district a zone's tint covers: its box's first plot, {x, y} in the world, a district (DISTRICT plots) a side, and the zone's index in the list given. |
| 6118 | 46 | `public List<Tinted> zoneTint(List<ZoneArea> zones)` | Each zone's districts (ZONES BY DISTRICT), in the map's order: none with no zone. |

### THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 (lines 6165-6296)

| line | len | member | says |
|---:|---:|---|---|
| 6184 | 112 | **type** `public static final class Pyramid` | The districts summed two by two, level by level, to one node - level 0 is the districts themselves, each level above a grid of nodes over the districts' box, a node NODE_WIDTH numbers. |
| 6192 | 45 | `Pyramid(CityMap map)` _(in CityMap.Pyramid)_ |  |
| 6239 | 11 | `long[] vector(District d)` _(in CityMap.Pyramid)_ | A district's numbers: its buildings by class, its ground used (half square feet) and its owned dry plots. |
| 6252 | 15 | `void add(District d, int t, long k)` _(in CityMap.Pyramid)_ | k more (or fewer) of type t in a district: its ancestors take them. |
| 6269 | 1 | `public int levels()` _(in CityMap.Pyramid)_ | How many levels above the districts. |
| 6272 | 1 | `public int nodes()` _(in CityMap.Pyramid)_ | How many nodes above the districts hold anything (and the top). |
| 6275 | 11 | `public long[] root()` _(in CityMap.Pyramid)_ | The top node: the whole city's numbers. |
| 6288 | 7 | `public long[] node(int level, int dx, int dy)` _(in CityMap.Pyramid)_ | The node at a level (1 up) covering district (dx, dy), or null. |

### THE SIDECAR (spec-land 2.5) (lines 6297-6661)

| line | len | member | says |
|---:|---:|---|---|
| 6327 | 3 | `public byte[] writeSidecar(int month)` | The map as its sidecar's bytes, deflated: the header (magic, format, the world - its seed, or since 0.7.121 an Earth world's key folded, worldId() - month, stamp, the founding site, the stamp of the land's rectangles ... |
| 6332 | 3 | `byte[] writeSidecar(int month, int format)` | ...in a given format, FORMAT or OLDEST_READ (a harness writes the older one to read it back). |
| 6337 | 82 | `byte[] writeSidecar(int month, int format, boolean withRuns)` | ...and with its runs or (a harness's, as 0.7.88 wrote FORMAT 5) a count of none. |
| 6423 | 7 | `static void writeVarint(DataOutputStream out, int v) throws IOException` | An unsigned varint (CL3): seven bits a byte, the low first, the high bit set where more follow. |
| 6432 | 9 | `static int readVarint(DataInputStream in) throws IOException` | ...read back; an IOException for one more than five bytes long. |
| 6450 | 7 | `static long worldId(World.Key key)` | The world a sidecar names in its header (0.7.121, batch E4): a generated world's seed, as every sidecar has carried it, so theirs are the bytes they were; an Earth world's whole key - its seed, spot and data - folded ... |
| 6459 | 1 | `public long lastStamp()` | The stamp of the sidecar last written: what the save carries as mapStamp. |
| 6462 | 13 | `static long stampOf(byte[] raw)` | The stamp of a sidecar's raw bytes: SplitMix64 folded over them, the stamp's own eight bytes read as zero. |
| 6477 | 7 | `public static long stampIn(byte[] deflated)` | The stamp a sidecar carries, or 0 when it is not one. |
| 6485 | 7 | `private static byte[] inflate(byte[] deflated)` |  |
| 6503 | 137 | `public static CityMap readSidecar(byte[] deflated, CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[]...` | A map read back from its sidecar, or null when it is not one, is not this city's (another seed, month or stamp, or land drawn otherwise), or does not add up. |
| 6642 | 3 | `public boolean same(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order, and (0.7.89) the same runs, and (0.7.97) the same works on the shore. |
| 6647 | 3 | `public boolean sameDistricts(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order, and (CL3) the same logs: the count of records, the first batch and every record. |
| 6652 | 9 | `public boolean sameDistricts(CityMap o, boolean withLogs)` | ...with or without their logs: the districts of a map read from a sidecar of FORMAT 8 or older, whose logs begin at its month, are the same as the map's own without them. |

