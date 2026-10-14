# CityMap.java - 4,535 lines · 252 methods · 36 constants · model

`ham/citybuildersim/CityMap.java` - generated 2026-10-14 by CodeMap; line numbers are as of that run.

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
> 
> THE RUNS (0.7.89, batch RD3; spec-roads-and-ports.md 2.7, 2.8): the
> city's Elevated Highways and railway are laid city-wide, month by month,
> on corridors from the founding site's lines (CityRuns) - never moved, the
> newest end taken first - and each district's plan is drawn round them.
> 
> THE PYRAMID sums ten classes, used plots and owned plots two by two up to
> one node, built when the map is, each district's ancestors updated as it
> changes: batch J4's for the far view, which since 0.7.128 draws from the
> ... (44 more lines in the source)

**Uses:** [World](World.md) (190), [DistrictPlan](DistrictPlan.md) (73), [BuildingVisual](BuildingVisual.md) (71), [TilePainter](TilePainter.md) (44), [Resource](Resource.md) (42), [CityShore](CityShore.md) (23), [Deposit](Deposit.md) (19), [CityRuns](CityRuns.md) (16), [CityLand](CityLand.md) (13), [DistrictPicture](DistrictPicture.md) (12), [BoatSchedule](BoatSchedule.md) (7), [LandGrid](LandGrid.md) (5), [SeaRoutes](SeaRoutes.md) (5), [LandParcel](LandParcel.md) (1)

**Used by (16):** [CityRuns](CityRuns.md), [DistrictPicture](DistrictPicture.md), [DistrictPlan](DistrictPlan.md), [EarthTerrainCheck](EarthTerrainCheck.md), [Game](Game.md), [LandMap](LandMap.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [MiniMap](MiniMap.md), [MiniMapFrame](MiniMapFrame.md), [PlanCheck](PlanCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [TilePainter](TilePainter.md)

## Sections

| line | section |
|---:|---|
| 128 | THE GEOMETRY |
| 192 | A DISTRICT |
| 259 | THE STATE |
| 334 | BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE |
| 446 | MEASURING THE GROUND: OWNED DRY PLOTS |
| 647 | THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN |
| 695 | PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION |
| 775 | · ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3) |
| 1322 | MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) |
| 1864 | THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch |
| 2942 | THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md |
| 3046 | THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch |
| 3272 | THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13; |
| 3509 | THE OIL AT SEA (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the |
| 3573 | THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1) |
| 3658 | A TILE'S INPUTS FOR THE PAINTER |
| 3969 | THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's |
| 4102 | ZONES BY DISTRICT (0.8.0, batch I14; docs/studies/spec-zoning.md 4: |
| 4173 | THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 |
| 4305 | THE SIDECAR (spec-land 2.5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 133 | `CityMap.DISTRICT` | `World.DISTRICT` | Plots on a district's side: World.DISTRICT, 256 (7.68 km). |
| 136 | `CityMap.TILES_A_SIDE` | `DISTRICT / World.TILE` | Tiles on a district's side: 8. |
| 139 | `CityMap.TILES` | `TILES_A_SIDE * TILES_A_SIDE` | Tiles in a district: 64. |
| 142 | `CityMap.HALF_SQ_FT_PER_PLOT` | `Math.round(2 * BuildingVisual.SQ_FT_PER_PLOT)` | Half square feet in a plot: 9,687.5 sq ft twice, so a district's room and use are whole numbers and a month's change is exact however it is added up. |
| 145 | `CityMap.MIN_ROOM` | `1` | A district with less room than this many whole plots is passed by the cursors: one, the least a building is drawn on (0.7.64; half a plot of ground before). |
| 148 | `CityMap.SITED` | `{ Resource.IRON, Resource.OIL, Resource.COAL, Resource.STONE, Resource.COPPER }` | The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5); since 0.8.0 (batch I14) coal, stone and copper after them, every resource a works on dry ground stands on. |
| 151 | `CityMap.SLOTS` |  | The site slots (0.8.0, batch I14): each SITED resource's types of field in Resource.typesOf()'s order - iron's hematite and magnetite, oil's one, coal's thermal and coking, stone's limestone and hard rock, copper's su... |
| 154 | `CityMap.SLOT_RESOURCE` |  | ...each slot's resource... |
| 157 | `CityMap.SLOT_TYPE` |  | ...and its type of field. |
| 169 | `CityMap.OLD_SITED` | `2` | The sites a district the sidecar wrote before FORMAT 7: iron's and oil's, SITED's until 0.8.0. |
| 172 | `CityMap.SITE_LISTS_KEPT` | `64` | How many districts' lists of sites are kept: 64. |
| 175 | `CityMap.COARSE_ABOVE` | `1024` | Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). |
| 178 | `CityMap.COARSE_STRIDE` | `32` | The coarse look's stride, in plots: 32, a sample a tile. |
| 181 | `CityMap.MAGIC` | `0x434D4150` | The sidecar's magic: "CMAP". |
| 184 | `CityMap.FORMAT` | `7` | The sidecar's format: 7 since 0.8.0 (batch I14), a district's owned sites by slot (SLOTS: each SITED resource's types of field) where it wrote iron's and oil's - an older sidecar's put on their first slots until the i... |
| 187 | `CityMap.OLDEST_READ` | `4` | The oldest sidecar read: FORMAT 4 (0.7.72 to 0.7.87) - its districts are placed as 0.7.88 places them; it has no runs, which are laid from its counts as it is read. |
| 190 | `CityMap.STAMP_AT` | `4 + 4 + 8 + 4` | Where the stamp sits in the sidecar's raw bytes: after the magic, the format, the seed and the month. |
| 509 | `CityMap.NONE` | `0, SOME = 1, ALL = 2` |  |
| 1039 | `CityMap.CAMPUS_REACH` | `64` | The most districts a works group's units may stand across, in the map's order, and still have a campus a new one goes to first: 64 (0.8.0) - past it, a city whose works of a group stand in every district (a city of bi... |
| 1491 | `CityMap.DISTRICT_ORDER` | `Comparator.comparingDouble((District d) -> d.order).thenComparingInt(d -> d.d...` | Nearest the founding site first, then north to south, west to east. |
| 1899 | `CityMap.PLANS_KEPT` | `64` | How many districts' plans are kept, packed for the painter (Drawn): 64, as the deals and road tiles were (spec 2.9) - each tile's street rows once a pattern and four bytes a building (RD1's plan was 140 KB as District... |
| 1902 | `CityMap.PACKED_BIT` | `1<<20` | A packed box's flag (Drawn.boxes): drawn without a street, R7's packing at the city's edge. |
| 1905 | `CityMap.BOX_TYPE_SHIFT` | `21` | Where a Drawn box's type id starts: above the box's 20 bits and PACKED_BIT, eleven bits for ids under 2,048 (buildings.json's ids are under 128). |
| 2091 | `CityMap.M_WEST` | `0, M_NORTH = 1, M_EAST = 2, M_SOUTH = 3, M_PARTED = 4` | A plan frame's edges' street codes (Link.margins): its west column (x 0) and north row (y 0), its east column (x 256, the next district's first) and south row (y 256), each 257 long; and M_PARTED, a byte along them wi... |
| 2112 | `CityMap.AROUND` | `{ { 0, - 1 }, { 1, 0 }, { 0, 1 }, { - 1, 0 }, { - 1, - 1 }, { 1, - 1 }, { 1, ...` | The eight districts about one, by offset {dx, dy}: north, east, south, west, then north-west, north-east, south-east, south-west - the order of a frame's seam parts (DistrictPlan.Input.surfaces). |
| 2115 | `CityMap.CORNER_DIRS` | `{ { 3, 0, 4 }, { 1, 0, 5 }, { 1, 2, 6 }, { 3, 2, 7 } }` | Each corner seam part's districts sharing it besides its own, as AROUND's directions: north-west, north-east, south-east, south-west. |
| 2242 | `CityMap.CHAIN_BAND` | `128` | THE CHAIN'S BANDS (0.7.89, batch RD3). |
| 2245 | `CityMap.BANDS_KEPT` | `16` | The bands whose links are kept, the last used: 16 (2,048 districts' links, about 4 MB) - a band let go is planned again when a screen asks for it. |
| 2903 | `CityMap.INTERIOR_MOST` | `World.TILE - 1` | A tile's interior holds a box no larger than this a side: 31 plots, off its arterial lines. |
| 2959 | `CityMap.PICTURES_KEPT` | `128` | How many districts' pictures are kept, the last used: 128 (star MM1-2) - a far view of up to 128 built districts (Jerus's city has 93) in DistrictPicture.BYTES each, 0.14 MB, within the view's 48 MB with the rest (Map... |
| 3077 | `CityMap.RUN_LINES_KEPT` | `4096` | Terrain lines (a tile's row or column, World.lineTerrain()) the runs' ground keeps: 4,096, about 460 KB - a corridor's way ahead and back, at any size. |
| 3080 | `CityMap.RUN_TILES_KEPT` | `256` | ...whole tiles, for a 45-degree way: 256. |
| 3530 | `CityMap.AtSea.NONE` | `new AtSea(List.of(), List.of())` | None. |
| 4178 | `CityMap.NODE_WIDTH` | `BuildingVisual.CLASSES + 2` | How many numbers a node sums: the ten classes, the ground used (in half square feet, exact) and the owned dry plots. |
| 4181 | `CityMap.NODE_USED` | `BuildingVisual.CLASSES` | Where a node keeps the ground used, in half square feet. |
| 4184 | `CityMap.NODE_OWNED` | `BuildingVisual.CLASSES + 1` | ...and its owned dry plots. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 199 | `public final int dx, dy` | Its column and row, in districts from the founding site's. |
| 200 | `final long key` |  |
| 201 | `final double order` |  |
| 203 | `int owned` | Its free plots: owned and dry, counted plot by plot (0.7.64; from 32 x 32 samples before) - or, for a harness's square city, given. |
| 205 | `long usedHalf` | The ground its buildings use, in half square feet: each type's count x its land, kept exactly. |
| 207 | `long usedCells` | The whole plots its buildings and roads are drawn on (BuildingVisual.cells()), a mine or well on its site taking its own: what its room is kept in (0.7.64). |
| 209 | `int inner` | Its free plots inside its tiles' edge rings, where alone the painter laid a road until 0.7.87, and the road plots it holds: its room for roads, by which its counts are still placed (0.7.64). |
| 210 | `long usedRoad` |  |
| 212 | `final int[] counts` | Its count of every type, by id. |
| 214 | `final int[] sites` | Its owned sites of each slot (SLOTS; of each SITED resource until 0.8.0). |
| 216 | `short[] tileFree` | Each tile's free plots, row by row: owned and dry; null until measured or asked for. |
| 218 | `short[] tileInner` | ...and those inside its edge ring, where alone the painter laid a road until 0.7.87 (the counts' room for roads still measures it). |
| 220 | `long[][] tileSiteKey` | Each tile's sites, by key (TilePainter.Input.siteKey), and their free plots on it, all and inside the edge ring: what a mine standing on one takes of the tile. |
| 221 | `short[][] tileSiteFree, tileSiteInner` |  |
| 223 | `int[] sited` | How many of each site-bound type stand on sites, each counted in the district of its first site (the rest of its count has none); null when none. |
| 225 | `int[][] siteRuns` | For painting (0.8.0, batch I14; pairs of {holding, mines} by resource before): per slot, the works on its sites here in the walk's order, triples of {holding, type, sites} - which type each holding's sites here carry,... |
| 227 | `long version` | Bumped whenever its counts change: what its sites' list is kept against (its deal's until 0.7.87). |
| 229 | `int index` | Its place in the map's order, nearest first. |
| 263 | `private final long seed` |  |
| 265 | `private final World.Key worldKey` | The world it is drawn on (0.7.115, batch E2): its land's key, whose ground every tile and plan reads. |
| 266 | `private final long siteX, siteY` |  |
| 267 | `private final long baseDX, baseDY` |  |
| 268 | `private final BuildingVisual.Type[] types` |  |
| 269 | `private CityLand land` |  |
| 270 | `private Function<Resource, double[]> remaining` |  |
| 272 | `private final List<District> districts` |  |
| 273 | `private final Map<Long, District> byKey` |  |
| 274 | `private final long[] have` |  |
| 275 | `private final int[] lo, hi` |  |
| 276 | `private int innerCursor, outerCursor` |  |
| 277 | `private int purchasesSeen` |  |
| 278 | `private long centreSeen` |  |
| 279 | `private boolean measured` |  |
| 280 | `private Pyramid pyramid` |  |
| 281 | `private SiteIndex siteIndex` |  |
| 282 | `private boolean sitedPlaced` |  |
| 748 | `private long changes` | How many changes the map has taken since it was drawn - a district's count, a purchase measured, a site's state: what the view stamps its tiles again on (MapTiles). |
| 767 | `private int[][] lastStates` | Each resource's holdings' states at the last month: when they move, the sites are drawn again. |
| 770 | `int lastTouched` | How many district-types the last month's change touched. |
| 773 | `double lastRunsMs` | How long the last month's runs took to lay, in ms (0.7.89): a harness's. |
| 790 | `private double roadShare` | The city's road plots for each plot of its other buildings, this month: its roads' whole plots over everything else's (0 with no road). |
| 824 | `private final List<District> builtIn` | The districts the month's new buildings went into (ROADS FOLLOW THEIR BUILDINGS): where its new roads are looked for, so a month's work stays with the districts it changes. |
| 825 | `private final java.util.Set<District> builtInSet` |  |
| 860 | `private double[] lackOf` | THE LACKING HEAP (0.7.94, batch RD7): placeRoad()'s districts, the one whose buildings lack road the most first, the lower index on a tie - the order the PriorityQueue it replaces kept, entry for entry (no two entries... |
| 861 | `private int[] lackAt` |  |
| 862 | `private int lackN` |  |
| 971 | `private long[] passedSize` | WALKS PASSED (0.7.94, batch RD7). |
| 972 | `private int[] passedTo` |  |
| 973 | `private int nPassed` |  |
| 1042 | `private int[][] campusTypes` | Each group's types, by id: worked out once a table. |
| 1339 | `District[][][] where` | [resource][holding]: the districts, and the sites in each. |
| 1340 | `int[][][] how` |  |
| 1342 | `final List<List<Map<District, int[]>>> tally` | The tallies they are read from: [resource] then holding, district to sites. |
| 1531 | `District[] at` |  |
| 1532 | `int[] holding` |  |
| 1533 | `int length, slot` |  |
| 1535 | `int pos, partial` | The next free site: its entry, and the sites of that entry taken. |
| 1537 | `long total, taken` | Its sites, and those taken. |
| 1544 | `SiteIndex ix` |  |
| 1545 | `int[] states` |  |
| 1547 | `Walk[] walks` | By the resource's own slots, in SLOTS' order. |
| 1549 | `long[][] on` | By claim (claims()): its works on each of the resource's slots, on none, and its count at the last month. |
| 1550 | `long[] over, count` |  |
| 1553 | `private final Lay[] lays` |  |
| 1556 | `private int[][] claims` | Each SITED resource's claims (the types standing on its sites) in the order they are laid: worked out once a table. |
| 1910 | `long stamp` | Its link's stamp: its own inputs (ownStamp()) and what was carried to it. |
| 1912 | `final byte[][] tiles` | Each tile's street codes (TilePainter's S_ codes), null where it has none, packed by row (pack()): a byte for each of its 32 rows saying which of its patterns that row is, then the patterns, 32 codes each - a tile's s... |
| 1914 | `byte[] east, south` | The frame's east column and south row: the first column and row of the districts east and south of it, where its edge cells' last arterials run (null where it lays none). |
| 1916 | `final int[][] boxes` | Each tile's buildings, an int each: the box - x, y, w - 1 and h - 1 in the tile's plots, five bits each, PACKED_BIT when packed without a street - and the type id from BOX_TYPE_SHIFT. |
| 1918 | `int placed, refused` | Buildings its plan placed, and what it could not hold (carried on, R7). |
| 1920 | `final long[] halves` | Its streets' surface in half plots by kind, [0, gravel, paved, highway], the plots of street it lays and how many of them are tracks: what its own road is drawn as. |
| 1921 | `int streets, tracks` |  |
| 1923 | `int joinsOut, partedOut, seamsSurfaced` | Its pieces joined to the streets of the districts before it, those no way over its ground joins, and its one-sided seams surfaced (DistrictPlan's figures). |
| 1929 | `double surplus` | Road it has no street for (the plan's surplus), in plots, and by kind [0, gravel, paved, highway]. |
| 1930 | `final double[] leftover` |  |
| 2064 | `final long own, serial` |  |
| 2065 | `final int[] in, out` |  |
| 2066 | `final long[] free` |  |
| 2067 | `final byte[][] margins` |  |
| 2068 | `final District[] earlier` |  |
| 2069 | `final long[] read` |  |
| 2071 | `final double surplus` | Its plan's road with no street to carry it (DistrictPlan.surplus), in plots: what the legend counts (star RD2-4); by kind [0, gravel, paved, highway]. |
| 2072 | `double[] leftover` |  |
| 2074 | `long picture` | Its plan's picture's stamp (0.7.128, batch MM1; DistrictPicture.stampOf()): the picture kept for it is current when it bears this. |
| 2215 | `long stamp` |  |
| 2216 | `final Map<District, int[][]> boxes` |  |
| 2217 | `int packed, smaller, none` |  |
| 2220 | `private final Map<District, Drawn> drawn` |  |
| 2223 | `private final Map<District, Link> links` |  |
| 2224 | `private final Map<District, PlanJob> pending` |  |
| 2226 | `private final Map<Integer, Pack> packs` | Each band's packing at its edge, and the job making it. |
| 2227 | `private final Map<Integer, PackJob> packings` |  |
| 2254 | `private long[] bandEpoch` | Each band's links checked at an epoch (bandEpoch): those of its districts up to bandValidTo (an index in the map's order) hold, and (when all do) the stamp of the packing they make. |
| 2255 | `private int[] bandValidTo` |  |
| 2256 | `private int bandsSized` |  |
| 2259 | `private final LinkedHashMap<Integer, Boolean> bandsUsed` | The bands used, the last last: those past BANDS_KEPT are let go (forgetBand()). |
| 2262 | `private long jobsHanded` | Jobs handed out so far: each job's serial, which keeps a later job's link over an earlier one's adopted after it. |
| 2427 | `final CityMap map` |  |
| 2429 | `final long epoch, serial` | The epoch it was handed out at (planEpoch()), and its place among every job the map handed out: a later job's inputs are the newer. |
| 2430 | `private boolean done` |  |
| 2445 | `final District d` |  |
| 2446 | `final DistrictPlan.Input in` |  |
| 2447 | `final long own` |  |
| 2448 | `final PlanJob after` |  |
| 2449 | `final int[] given` |  |
| 2450 | `final boolean edge` |  |
| 2452 | `final District[] earlier` | The districts about it before it in the map's order (earlierOf()), and each one's link or job: whose frames' edges it reads (readNeighbours()). |
| 2453 | `final Object[] sources` |  |
| 2454 | `int[] carryIn, carryOut` |  |
| 2455 | `Drawn result` |  |
| 2456 | `long[] free, read` |  |
| 2457 | `byte[][] margins` |  |
| 2458 | `double ms` |  |
| 2460 | `final int[] extras` | Its picture's (0.7.128, batch MM1): the boxes the runs and the shore stand on its ground (extrasIn()), the stamp of the picture kept for it when it was handed out (0 for none), its plan's plots' stamp (DistrictPicture... |
| 2461 | `final long keptPicture` |  |
| 2462 | `long pictureStamp` |  |
| 2463 | `DistrictPicture picture` |  |
| 2464 | `double pictureMs` |  |
| 2524 | `long stamp` |  |
| 2525 | `int band` |  |
| 2526 | `final District[] edges` |  |
| 2527 | `final Object[] from` |  |
| 2528 | `final Object last` |  |
| 2529 | `final BuildingVisual.Type[] types` |  |
| 2530 | `final long[] tileOrder` |  |
| 2531 | `Pack result` |  |
| 2961 | `private final Map<District, DistrictPicture> pictures` |  |
| 2966 | `private long picturesKept` | How many pictures have been kept since the map was drawn, each drawn for plots the one kept before it was not: what the far view repaints a waiting node on, and a harness counts a month's redraws by. |
| 3068 | `private long landVersion` | Bumped whenever free ground is measured again (a purchase): what a run waiting on the ground is tried again on. |
| 3071 | `private CityRuns runs` | The city's highways and railway (spec 2.7, 2.8). |
| 3090 | `private final Map<Long, byte[]> lines` |  |
| 3093 | `private final Map<Long, byte[]> tiles` |  |
| 3096 | `private final Map<Long, boolean[]> owned` |  |
| 3099 | `private long ownedAt` |  |
| 3100 | `private byte[] lastLine` |  |
| 3101 | `private boolean[] lastOwn` |  |
| 3102 | `private int lastCover` |  |
| 3103 | `private final boolean[] ALL_OWNED` |  |
| 3177 | `private final RunGround runGround` |  |
| 3237 | `private int[] yardShare` | Each district's Rail Terminals the runs draw as yards, by its index: the yards dealt to the districts holding terminals in the map's order, so the rest are their plans' (kept against the runs' version). |
| 3238 | `private long yardShareAt` |  |
| 3289 | `private CityShore shore` | The city's works on its shore. |
| 3295 | `private int[] shoreTypes` | The shore's types, by id: worked out once a table. |
| 3310 | `private long shoreShortAt` | The ground's key a search came up short at: none is made again until the ground moves (a purchase) or a work is taken. |
| 3313 | `private int shoreShort` | How many works the model has that the shore holds no place for (drawn by their districts' plans). |
| 3486 | `private int[][] shoreShare` | Each district's works the shore holds, by its index and the shore's type: the works dealt to the districts holding their type in the map's order, so the rest are their plans' (kept against the shore and the counts). |
| 3487 | `private long shoreShareAt` |  |
| 3533 | `private AtSea atSea` |  |
| 3583 | `private List<BoatSchedule.Route> routes` | The routes found, and the key of what they were found from. |
| 3584 | `private long routesKey` |  |
| 3618 | `final long key, seed, siteX, siteY` |  |
| 3620 | `final World.Key world` | The world the routes are found on (0.7.115). |
| 3621 | `final List<BoatSchedule.Berth> berths` |  |
| 3622 | `final double radius` |  |
| 3623 | `private volatile List<BoatSchedule.Route> found` |  |
| 3883 | `private final Map<Long, List<DrawnSite>> siteLists` |  |
| 3886 | `private final Map<Long, Long> siteListVersion` |  |
| 4193 | `private final CityMap map` |  |
| 4194 | `private final int minX, minY` |  |
| 4196 | `private final int[] widths, heights` | Each level's grid: its width, height and nodes (NODE_WIDTH numbers each, row by row). |
| 4197 | `private final long[][] nodes` |  |
| 4198 | `private final int count` |  |
| 4386 | `private long lastStamp` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 126 | 4410 | **type** `public final class CityMap` | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, each district's street plan kept for the painter with what it cannot ... |

### THE GEOMETRY (lines 128-191)

| line | len | member | says |
|---:|---:|---|---|
| 159 | 8 | `static { ... }` |  |

### A DISTRICT (lines 192-258)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 59 | **type** `public static final class District` | One district: where it is, its free plots, the ground and the plots used, its owned sites, its counts by type. |
| 231 | 7 | `District(int dx, int dy, int types, double order)` _(in CityMap.District)_ |  |
| 240 | 1 | `public int owned()` _(in CityMap.District)_ | Its free plots (owned and dry, its sites' plots among them). |
| 242 | 1 | `public double used()` _(in CityMap.District)_ | The plots of ground its buildings use: each type's count x its land in plots. |
| 244 | 1 | `public long usedCells()` _(in CityMap.District)_ | The whole plots its buildings and roads are drawn on. |
| 245 | 1 | `public int count(int t)` _(in CityMap.District)_ |  |
| 246 | 5 | `public int sites(Resource r)` _(in CityMap.District)_ |  |
| 252 | 1 | `long freeCells()` _(in CityMap.District)_ | Its room in whole plots, never below nothing (0.7.64; in ground, half square feet, before). |
| 254 | 1 | `long freeRoad()` _(in CityMap.District)_ | ...and for roads: its whole room, no more than its plots inside the edge rings less its road. |
| 257 | 1 | `static long key(int dx, int dy)` |  |

### THE STATE (lines 259-333)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 12 | `private CityMap(World.Key worldKey, long siteX, long siteY, BuildingVisual.Type[] types)` |  |
| 298 | 1 | `public long seed()` | The world's seed. |
| 301 | 1 | `public World.Key worldKey()` | The world's key (0.7.115): World.of(worldKey()) is the ground it is drawn on. |
| 304 | 1 | `public BuildingVisual.Type[] types()` | The types it counts, by id. |
| 307 | 1 | `public List<District> districts()` | Its districts, nearest the founding site first. |
| 310 | 1 | `public District district(int dx, int dy)` | The district at (dx, dy) from the founding site's, or null. |
| 313 | 1 | `public Pyramid pyramid()` | The pyramid. |
| 316 | 1 | `public boolean measured()` | Whether its districts were measured on the ground (false for a harness's square city, whose capacities were given). |
| 319 | 5 | `public long[] totals()` | Every type's count over all districts. |
| 326 | 1 | `public CityLand land()` | The land it was drawn on. |
| 329 | 1 | `long baseDX()` | The founding site's district's column, absolute. |
| 332 | 1 | `long baseDY()` | ...and row. |

### BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE (lines 334-445)

| line | len | member | says |
|---:|---:|---|---|
| 348 | 11 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] counts)` | A map drawn canonically (spec-land 2.5): the land's districts measured, then each type in proportion in every district, inner first, the remainders by largest remainder; mines and wells on their sites. |
| 370 | 4 | `static CityMap square(long seed, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity, long[] counts)` | A HARNESS'S SQUARE CITY (the design's scale case): side x side districts round the founding site, every one owned and holding `capacity` plots - a city of any size at one density without measuring its ground; a distri... |
| 376 | 30 | `static CityMap square(World.Key world, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity, long[] counts)` | ...on the world a key names (0.7.115): EarthTerrainCheck's screen on Earth. |
| 407 | 9 | `private District newDistrict(int dx, int dy)` |  |
| 417 | 8 | `private void sortDistricts()` |  |
| 427 | 13 | `private void rangeTypes()` | Each type's lowest and highest district index holding it. |
| 441 | 4 | `private void advanceCursors()` |  |

### MEASURING THE GROUND: OWNED DRY PLOTS (lines 446-646)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 27 | `private void measureAll()` | Every district the land's owned box reaches, measured; the coarse look first when there are many. |
| 480 | 28 | `private boolean[] coarseLand(int dx0, int dy0, int nx, int ny)` | Which districts have land in or beside them, by a sample a tile (COARSE_STRIDE): a district all sea there, with all-sea neighbours, is taken as sea and not measured. |
| 512 | 3 | `int ownership(int dx, int dy)` | Whether the city owns none, some or all of a district: the grid's cover of it, a level-8 block (spec-grid 2.4; by its radii and lanes until 0.7.66). |
| 517 | 3 | `private static int cover(int c)` | LandGrid's NONE, SOME or ALL as this class's. |
| 522 | 5 | `double[] ownedBox()` | The land's owned box from the site, in plots, inclusive {dx0, dy0, dx1, dy1}: its reach each way (an empty land, the site's plot). |
| 538 | 64 | `private void measure(District d, int own)` | Counts a district's free plots, tile by tile and plot by plot (0.7.64): owned and dry - the ground the model builds on, a resource's sites with it (a mine stands on its site; another building takes a site's plot only ... |
| 604 | 18 | `private List<long[]> siteSquares(long bx0, long by0, long bx1, long by1)` | Every site's square, {x0, y0, x1, y1} in plots inclusive, of every resource in fields, owned or not, that reaches into a box of plots: the plots the painter keeps as fields. |
| 624 | 8 | `private District measureNew(int dx, int dy, int own)` | A district not on the map yet, measured, and kept only when it holds free ground: a district of water joins the map only when a site in it needs one (tallySites()). |
| 634 | 1 | `public short[] tileFreeOf(District d)` | A district's tiles' free plots (a copy), counted if they were not: for a harness and a probe. |
| 637 | 9 | `short[] tileFree(District d)` | A district's tiles' free plots, counted if they were not (after a sidecar is read, or for a harness's square city); its room stands as it was. |

### THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN (lines 647-694)

| line | len | member | says |
|---:|---:|---|---|
| 657 | 37 | `boolean syncLand()` | Catches the map up with its land: every district a new purchase touches recounted (new ones added); true when the land was drawn again (a restatement, a conversion), which the caller answers with a canonical map. |

### PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION (lines 695-774)

| line | len | member | says |
|---:|---:|---|---|
| 708 | 38 | `public boolean reconcile(long[] model)` | THE MONTH (spec-land 2.5, star 12): the land caught up, then each type's change placed - new buildings into the first district with room (nearest first; farms, utilities, mines and wells, the railway and the car plant... |
| 751 | 1 | `public long changes()` | How many changes the map has taken since it was drawn: when it moves, a view stamps the tiles it shows again (batch J4). |
| 760 | 5 | `void rebind(CityLand live, Function<Resource, double[]> liveRemaining)` | A map drawn away from the screen's thread, on a copy of the land (Game.MapDraft, batch J4), bound to the city's own land and what remains of its resources before it is kept: the copy was the land as it stood, field fo... |

### ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3) (lines 775-1321)

| line | len | member | says |
|---:|---:|---|---|
| 793 | 9 | `double roadShareOf(long[] model)` | The model's road plots over its other whole plots. |
| 804 | 7 | `long buildingRoom(District d, long each)` | How many buildings of `each` whole plots district d has room for, keeping room for the road they and its buildings need at the city's share: k with k x each plus the road short of share(d's other plots + k x each) no ... |
| 813 | 4 | `boolean hasRoomFor(District d, int t)` | Whether district d has room for one more of type t, as the month places it (MapCheck 2 asks). |
| 819 | 3 | `double roadLacking(District d)` | The road a district's buildings lack, in plots, at the city's share: share x its other plots less its road plots. |
| 828 | 22 | `private int placeRoad(int t, long n)` | Places n more roads of type t, one at a time: each into the district, of those the month's buildings went into, whose buildings lack road the most and that has room for it inside its tiles' edge rings, the nearer on a... |
| 865 | 4 | `private boolean lackFirst(int i, int j)` | Whether heap entry i comes before entry j: more lacking, then the lower index. |
| 871 | 6 | `private void lackAdd(double lack, int index)` | An entry added at the end, the heap not kept (placeRoad() makes it a heap after). |
| 878 | 10 | `private void lackPush(double lack, int index)` |  |
| 890 | 10 | `private int lackPoll()` | The first entry's district index, taken off the heap. |
| 902 | 10 | `private void lackDown(int k)` | Entry k sifted down to its place below. |
| 913 | 4 | `private void lackSwap(int i, int j)` |  |
| 919 | 38 | `private int place(int t, long n)` | Places n more of type t: into the first districts with room for its whole plots in its direction (a road's inside its tiles' edge rings; since 0.7.72 a building's keeping room for its road, buildingRoom()), the rest i... |
| 976 | 8 | `private int passed(int kind, long each, int i)` | Where a walk of `kind` for buildings of `each` plots from cursor i may start (WALKS PASSED). |
| 986 | 17 | `private void notePassed(int kind, long each, int to)` | A walk of `kind` for `each` plots found no room before district index `to` (WALKS PASSED). |
| 1044 | 15 | `private int[] campusTypes(int g)` |  |
| 1061 | 3 | `private int[] campusTypes()` | The refinery's unit types, by id. |
| 1066 | 3 | `long campusCells(District d)` | The refinery's ground in district d, in the whole plots its units are drawn on. |
| 1071 | 6 | `long campusCells(District d, int g)` | ...a group's (0.8.0). |
| 1079 | 3 | `District campusDistrict()` | The district holding the most of the refinery's ground, the nearer on a tie; null when it has none. |
| 1084 | 17 | `District campusDistrict(int g)` | ...of a group's (0.8.0). |
| 1103 | 24 | `int gatherCampus()` | The refinery's units in other districts moved into its campus district while it has room, the largest kinds first, then by district in the map's order (a sidecar older than FORMAT 6, read). |
| 1129 | 5 | `District roomiest()` | The district with the most room left, the nearer on a tie: where what no district has room for goes, to be drawn smaller there. |
| 1136 | 21 | `private int remove(int t, long n)` | Takes n of type t away: an inner type from the outermost district holding it in, an outer type from the innermost out. |
| 1159 | 3 | `private void add(District d, int i, int t, long k)` | Adds k (negative to take away) of type t to the district at index i, keeping its ground and plots used, the running total, the type's range, the cursors and the pyramid. |
| 1164 | 20 | `private void add(District d, int i, int t, long k, boolean sited)` | ...a mine or well on a site (sited) taking its whole plots too: its site is its ground, the land the model gives it. |
| 1186 | 5 | `long usedOf(District d)` | The ground a district's buildings use, in half square feet: each type's count x its land, twice. |
| 1193 | 7 | `long roadOf(District d)` | The road plots a district holds. |
| 1202 | 5 | `long cellsOf(District d)` | The whole plots a district's buildings and roads are drawn on, a mine or well on its site taking its own. |
| 1218 | 103 | `private void allocate(long[] counts)` | THE CANONICAL ALLOCATION (spec-land 2.5): the mines and wells on their sites first (0.7.64), then the city's footprint F, in the whole plots it is drawn on, shared inner first - each district its share of F, min(its r... |

### MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) (lines 1322-1863)

| line | len | member | says |
|---:|---:|---|---|
| 1337 | 7 | **type** `static final class SiteIndex` | Every owned site of the SITED resources: per resource and holding, the districts holding them, nearest first, and how many in each. |
| 1346 | 3 | `int holdingOf(long dx, long dy)` | The holding a plot (dx, dy) from the site lies in: 0 the centre, k the k-th purchase, -1 none - the grid's owner (spec-grid 2.4). |
| 1351 | 17 | `private List<Deposit> fieldsNear(Resource r, double bx0, double by0, double bx1, double by1)` | The fields of a resource that reach into a box of plots: the world's as the city sees them (CityLand.fieldsIn(): its old world's ground's own, 0.7.99), and a converted centre's legacy iron field. |
| 1370 | 4 | `Deposit legacyField()` | A converted centre's legacy iron field, as a field of its sites at its plot (cell -1). |
| 1382 | 9 | `SiteIndex siteIndex()` | The site index: every owned site of the SITED resources by holding and district, built with the map and extended by each purchase (a new holding's sites are those of the fields centred on its ground, wherever they lie). |
| 1397 | 65 | `private void tallySites(SiteIndex ix, int from, double bx0, double by0, double bx1, double by1)` | Adds to the index the sites of holdings `from` on that lie in a box of plots from the site, and lays its lists out again: a new index object, so the walks lay themselves out again too. |
| 1464 | 3 | `public long ownedSites(Resource r)` | The owned sites of a SITED resource on the map. |
| 1469 | 9 | `public long ownedSites(Resource r, Deposit.FieldType type)` | ...of one type of its fields (0.8.0, batch I14), or of all of them (null): what a works of that type may stand on. |
| 1480 | 3 | `private boolean onSites(int t)` | Whether type t stands on its resource's sites (placeSited()): a site of a SITED resource, and sites to take - any other building, a site on none, placed as any building is, so the districts always sum to the counts. |
| 1485 | 4 | `static int slotOf(Resource r, Deposit.FieldType type)` | The slot of a resource's fields of a type, or -1 (not SITED, or a type not the resource's). |
| 1495 | 5 | `static int holdingState(double listed, double left)` | A holding's state for a resource from what remains in it: UNWORKED, WORKING or WORKED_OUT (one that listed none, unworked). |
| 1502 | 7 | `int[] holdingStates(Resource r)` | Each holding's state for a resource, the centre first. |
| 1530 | 11 | **type** `private static final class Walk` | One slot's sites in the order works take them (layWalk()), and how far its claims have filled them. |
| 1539 | 1 | `long free()` _(in CityMap.Walk)_ |  |
| 1543 | 9 | **type** `private static final class Lay` | One SITED resource's works on its sites: its slots' walks, laid against an index and its holdings' states, and each claim's works on each slot and on none. |
| 1559 | 19 | `int[] claims(int k)` | A SITED resource's claims: its typed works first, by their type's slot, then those on ANY of its fields; within each the larger first (BuildingVisual.Type.sites()), then by id - first fit decreasing, as LandManager.fi... |
| 1580 | 4 | `private int claimSlot(int t)` | A claim's slot to sort by: its type's, or past every slot for ANY (or a type not its resource's). |
| 1586 | 9 | `private int[] slotsOf(int k, int t)` | The resource's own slots (indexes into Lay.walks) a claim stands on, in order: its type's alone, or every one for ANY. |
| 1597 | 8 | `private boolean lastOnItsSlots(int k, int c)` | Whether claim c is the last laid on each of its slots: no later claim stands on any of them. |
| 1615 | 26 | `private void placeSited(long[] model)` | Puts each SITED resource's works on its sites (spec-land 2.4, 2.5; since 0.8.0 every type of them, by the type of its field and on as many sites as it takes): in each slot the first sites in acquisition order whose ho... |
| 1643 | 18 | `private void relay(int k, SiteIndex ix, int[] states, long[] want)` | Lays a resource's works on its sites again: every one of its types taken off the map, then each claim in order on its slots' walks from their first site. |
| 1663 | 36 | `private void restand(int k, Lay lay, int c, long n)` | One claim to a new count: more filled onto its slots in order from where they stand, then on none; fewer taken off what stands on none first, then off its slots from the last back. |
| 1701 | 23 | `private Walk layWalk(SiteIndex ix, int[] states, int k)` | The walk's order: holdings not worked out in acquisition order, then the worked-out; within each, its districts nearest first. |
| 1726 | 36 | `private long fill(Walk w, int t, int each, long n)` | Up to n works of type t, `each` sites apiece, onto a walk from its next free site - only while a whole one has room - each counted in the district of its first site. |
| 1764 | 28 | `private void empty(Walk w, int t, int each, long n)` | n works of type t, `each` sites apiece, taken back off a walk from its last taken site: the last claim on it, so they are the ones there. |
| 1794 | 22 | `private void markRun(District d, int slot, int holding, int t, int m)` | m more (or fewer) sites of a district's slot, of one holding, under a works of type t: its painting's runs. |
| 1818 | 6 | `private void count(District d, int t, long q)` | q more (or fewer) works of type t counted on sites in a district. |
| 1826 | 3 | `private void sitedNow()` | The mines and wells stood on their sites (placeSited()), once, before anything reads a district's counts less them: a plan's inputs and its stamp (0.7.88), as the sites' lists do. |
| 1830 | 11 | `private District home()` |  |
| 1842 | 1 | `private static int indexOf(District d)` |  |
| 1845 | 4 | `static int siteSlot(Resource r)` | Where a resource sits among SITED, or -1. |
| 1851 | 12 | `private void clearType(int t)` | Takes every one of type t off the map (its sites' runs are its resource's lay's to clear: relay()). |

### THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch (lines 1864-2941)

| line | len | member | says |
|---:|---:|---|---|
| 1908 | 83 | **type** `public static final class Drawn` | A district's plan as the painter reads it (0.7.88): each tile's street codes and its buildings' boxes, and the figures a harness reads. |
| 1924 | 1 | `public int joinsOut()` _(in CityMap.Drawn)_ |  |
| 1925 | 1 | `public int partedOut()` _(in CityMap.Drawn)_ |  |
| 1926 | 1 | `public int seamsSurfaced()` _(in CityMap.Drawn)_ |  |
| 1933 | 6 | `long bytes()` _(in CityMap.Drawn)_ | The bytes it holds, about: what the view's budget counts. |
| 1941 | 5 | `public long[] buildingsByType(int types)` _(in CityMap.Drawn)_ | Its plan's buildings, by type id: the boxes counted. |
| 1948 | 6 | `public byte codeAt(int x, int y)` _(in CityMap.Drawn)_ | The street code its plan lays on plot (x, y) of its frame: the district's 256 x 256 and its east column and south row (x or y = DISTRICT); 0 for none. |
| 1956 | 1 | `static byte codeIn(byte[] t, int x, int y)` _(in CityMap.Drawn)_ | A packed tile's code at plot (x, y) of it. |
| 1959 | 3 | `static void unpack(byte[] t, byte[] out)` _(in CityMap.Drawn)_ | A packed tile's codes, a byte a plot row by row, into out. |
| 1964 | 14 | `static byte[] pack(byte[] plots)` _(in CityMap.Drawn)_ | A tile's codes (a byte a plot row by row) packed by row: each row's pattern once, in the order first met. |
| 1980 | 1 | `public double surface(int kind)` _(in CityMap.Drawn)_ | Its surface in plots of kind (BuildingVisual's GRAVEL to HIGHWAY). |
| 1982 | 1 | `public int streets()` _(in CityMap.Drawn)_ | The plots of street it lays, and those of them tracks. |
| 1983 | 1 | `public int tracks()` _(in CityMap.Drawn)_ |  |
| 1985 | 1 | `public int placed()` _(in CityMap.Drawn)_ | Buildings its plan placed, and refused (carried on). |
| 1986 | 1 | `public int refused()` _(in CityMap.Drawn)_ |  |
| 1988 | 1 | `public double surplus()` _(in CityMap.Drawn)_ | Road it has no street for, in plots; of a kind. |
| 1989 | 1 | `public double leftover(int kind)` _(in CityMap.Drawn)_ |  |
| 1993 | 8 | `static byte paintCode(short code)` | A plan's street code as the painter reads it (TilePainter's S_ codes): DistrictPlan's kind and width, its role - an arterial, a boulevard's, or a street (a cell's, along a cut, the join's) - and a bridge. |
| 2003 | 53 | `static Drawn drawnOf(DistrictPlan p, long stamp)` | A plan packed for the painter, with its stamp. |
| 2058 | 3 | `static int box(int x, int y, int w, int h, boolean packed)` | A box packed into an int: x, y, w - 1 and h - 1 at five bits each, PACKED_BIT when packed without a street. |
| 2063 | 19 | **type** `static final class Link` | A district's link in the chain (R7): the stamp of its own inputs, what was carried to it and what it carried on (by type id, null for none); for an edge district, its plan's free ground (a bit a plot of the district),... |
| 2075 | 4 | `Link(long own, int[] in, int[] out, long[] free, byte[][] margins, District[] earlier, long[] read, double surplus, long serial)` _(in CityMap.Link)_ |  |
| 2080 | 1 | `long stamp()` _(in CityMap.Link)_ | Its plan's stamp: its own inputs and what came in. |
| 2084 | 5 | `static long stampOf(long own, int[] in)` | A plan's stamp: its own inputs' and the carry in's. |
| 2094 | 16 | `static byte[][] marginsOf(DistrictPlan p)` | A plan's frame edges' street codes (M_WEST to M_SOUTH) and which of them are parted (M_PARTED). |
| 2118 | 8 | `District[] earlierOf(District d)` | The districts about d (AROUND's order) that come before it in the map's order, in its band (0.7.89; CHAIN_BAND), null for the rest: whose plans its plan reads - which of its seams they leave unlaid, and where its stre... |
| 2128 | 4 | `static byte marginAt(byte[][] m, int x, int y, int ox, int oy)` | Where district d's frame plot (x, y) lies in the frame of its neighbour at offset (ox, oy), read from that plan's margins - a plot of its frame's edge - or 0 when it is not on them. |
| 2134 | 4 | `static boolean partedAt(byte[][] m, int x, int y, int ox, int oy)` | ...whether the street there is parted from the network (M_PARTED); false where it is not on them. |
| 2140 | 9 | `private static int marginOf(int x, int y, int ox, int oy)` | ...which margin and place: the margin (M_WEST to M_SOUTH) << 16 \| the index along it, or -1. |
| 2162 | 46 | `static void readNeighbours(District d, District[] earlier, byte[][][] margins, DistrictPlan.Input in)` | What district d's plan reads of the plans before it (0.7.88), from their frames' edges (margins, AROUND's order; null for a district after it or none): ONE-SIDED SEAMS, its seams no district before it sharing them lay... |
| 2209 | 3 | `private static void open(DistrictPlan.Input in, int x, int y, byte code)` |  |
| 2214 | 5 | **type** `static final class Pack` | What the city has no room for, packed at its edge (R7): each edge district's packed boxes by tile, an int each as Drawn.boxes; how many were packed, how many drawn smaller than their own land, and how many found no gr... |
| 2248 | 1 | `static int band(int i)` | The band of the district at index i in the map's order. |
| 2251 | 1 | `int bands()` | Bands in the map's order. |
| 2265 | 3 | `private long planEpoch()` | What the plans hang on, cheaply: the map's changes, and the land's purchases (a purchase owns ground at once, before the month measures it; the land drawn again draws a new map). |
| 2270 | 4 | `boolean isEdge(District d)` | Whether district d is at the city's edge: a neighbour of its eight is not on the map - where what the city has no room for is packed (R7). |
| 2284 | 31 | `long ownStamp(District d)` | A stamp of everything district d's own plan is drawn from but what is carried to it: its buildings and road by type (its mines and wells on sites apart), the ground the city owns over its frame (ownership only grows, ... |
| 2317 | 9 | `private void sizeBands()` | The bands' arrays sized to the districts, every band to be checked again. |
| 2328 | 4 | `private void bandsStale()` | Every band to be checked again (a plan adopted). |
| 2334 | 22 | `private void checkBand(int b)` | Band b's links walked in the map's order at the current epoch: its bandValidTo is the first whose own stamp moved, whose carry in is not the one before's carry on (none for the band's first), or (an edge district) tha... |
| 2358 | 8 | `private void useBand(int b)` | Marks band b used, letting go of the bands used longest ago past BANDS_KEPT. |
| 2368 | 11 | `private void forgetBand(int b)` | Lets go of band b's links, plans and packing: drawn again when a screen asks for it. |
| 2381 | 8 | `private boolean allDrawn()` | Whether every band's links hold and its packing is current: the whole city drawn. |
| 2391 | 8 | `private int[][] packedIn(District d)` | District d's band's packing of what the band cannot hold, by tile, when current; null for none. |
| 2401 | 10 | `private boolean readHolds(Link l)` | Whether the plans a link read its neighbours' edges from are those standing: each such district's link the one it read (they are before it in the map's order, walked first). |
| 2413 | 11 | `public boolean planned(District d)` | Whether district d's plan is drawn and current, and every link before it in its band holds; for an edge district, its band's packing too: its tiles paint without planning. |
| 2426 | 16 | **type** `public abstract static class Job` | A job to run away from the screen's thread (0.7.88): run() on any one thread, in the order handed out, then adopt() on the map's. |
| 2431 | 1 | `Job(CityMap map, long epoch)` _(in CityMap.Job)_ |  |
| 2433 | 5 | `public final synchronized void run()` _(in CityMap.Job)_ | Does the work: any thread, the jobs handed out in their order on one; safe to call twice. |
| 2438 | 1 | `abstract void work()` _(in CityMap.Job)_ |  |
| 2440 | 1 | `public final synchronized boolean done()` _(in CityMap.Job)_ | Whether it has run. |
| 2444 | 77 | **type** `public static final class PlanJob extends Job` | One district's plan: its inputs gathered on the map's thread, its ground read and its plan drawn by run(), with what the job before it carried on (R7). |
| 2466 | 9 | `PlanJob(CityMap map, District d, DistrictPlan.Input in, long own, PlanJob after, int[] given, boolean edge, District[] earlier,...` _(in CityMap.PlanJob)_ |  |
| 2476 | 35 | `void work()` _(in CityMap.PlanJob)_ |  |
| 2513 | 1 | `public double ms()` _(in CityMap.PlanJob)_ | How long it took, in ms: a harness's figure (its plan's; its picture's apart, pictureMs()). |
| 2516 | 1 | `public double pictureMs()` _(in CityMap.PlanJob)_ | How long its picture took, in ms - its plots, and its pixels when it drew them: a harness's figure. |
| 2519 | 1 | `public boolean drewPicture()` _(in CityMap.PlanJob)_ | Whether it drew a picture (the one kept was not of its plots): a harness's figure. |
| 2523 | 41 | **type** `public static final class PackJob extends Job` | A band's packing at its edge (R7; the city's, in a city of one band): what its last link carried on, into its edge districts' free ground - their links', or the jobs' that make them. |
| 2533 | 4 | `PackJob(CityMap map, District[] edges, Object[] from, Object last, long[] tileOrder, long epoch)` _(in CityMap.PackJob)_ |  |
| 2538 | 25 | `void work()` _(in CityMap.PackJob)_ |  |
| 2573 | 8 | `public List<Job> planJobs(District d)` | The jobs that make district d's plan current (R7: every district before it in the map's order whose link does not hold, each after the one before it), and, for an edge district, the city's packing after them: to run i... |
| 2583 | 31 | `private PlanJob chainTo(int i, List<Job> out)` | The plan jobs up to the district at index i from its band's first, into out; the last job of the chain to it (handed out now or before), or null when its link holds. |
| 2616 | 10 | `private Object[] sources(District[] earlier)` | Each earlier district's plan as a job reads it: its job handed out at this epoch, else its link (it is before the job's district in the map's order, so one or the other holds). |
| 2628 | 24 | `private void packAfter(int b, List<Job> out)` | Band b's packing at its edge, after the chain to its last district, into out - unless it is current or handed out at this epoch. |
| 2654 | 21 | `private long[] tileOrder(List<District> edges)` | The edge districts' tiles in the order the packing spreads over them: each a district's index in `edges` and the tile, the farthest tile from the founding site first. |
| 2677 | 26 | `public boolean adopt(Job j)` | Keeps a job's work: a plan's link and its packed plan, the city's packing. |
| 2705 | 13 | `Drawn drawnOf(District d)` | District d's plan for the painter, made here when it is not current - every district before it in the map's order first where its link does not hold, and for an edge district the city's packing - on this thread: a har... |
| 2720 | 7 | `void packed()` | Every band's packing at its edge, made here when it is not current (a harness's): the whole city drawn. |
| 2729 | 11 | `private int[] packSums()` | The bands' packings summed: {packed, smaller, none}; null when one is not current. |
| 2742 | 5 | `public int[] packedCounts()` | How many buildings the city has no room for in any district's plan, packed at its edge (R7; since 0.7.89 at its band's edge) - and how many of them drawn smaller than their own land or with no ground at all: {packed, ... |
| 2749 | 3 | `public int[] packedCountsIfDrawn()` | ...the same, or null when the packing is not current: the screen's thread, which never plans. |
| 2754 | 7 | `public long[] legendFiguresIfDrawn()` | The legend's figures once every plan and the packing are drawn (never planned here: the screen's thread): {buildings packed at the city's edge, plots of road its streets do not carry (star RD2-4) - since 0.7.89 with t... |
| 2763 | 10 | `public double[] leftoverByKind()` | The road the plans have no street for, by kind [0, gravel, paved, highway], over every district (made now if need be), and (0.7.89) the Elevated Highway plots the runs could not lay: a harness's - with the surface dra... |
| 2775 | 4 | `public long[] legendFigures()` | ...the same, made now if need be: a harness's. |
| 2781 | 1 | `public Drawn drawn(District d)` | District d's plan as drawn (made now if need be): a harness's and a probe's. |
| 2784 | 5 | `public int[][] carried(District d)` | What was carried to district d and what it carried on (R7), by type id, null for none - its link's, once planned. |
| 2791 | 1 | `int linksKept()` | The links kept (0.7.89: a band's let go past BANDS_KEPT): a harness's. |
| 2794 | 5 | `public long plansBytes()` | The bytes the kept plans hold: what MapTiles' budget counts. |
| 2806 | 25 | `static long[] freeGround(DistrictPlan.Input in, DistrictPlan p)` | A plan's free ground (R7), for an edge district: a bit a plot of the district's 256 x 256, set where the city owns dry ground no street, building, highway or its verge (H5), track or mine's site takes - a field's plot... |
| 2846 | 55 | `static Pack packAtEdge(int[] carry, BuildingVisual.Type[] types, District[] edges, long[][] free, long[] tileOrder)` | PACKED AT THE CITY'S EDGE (R7; spec 2.6: "a city with no room anywhere packs them at its edge, without a street, and the map's legend says how many"). |
| 2906 | 35 | `private static int[] fitTile(long[][] free, long key, int w, int h, int[][] failW, int[][] failH, int[] fails, int ti)` | The first spot, in rows, on tile key (edge index << 32 \| tile) of the free ground where a w x h box - or h x w - fits inside the tile's interior: {edge index, tile, x, y, w, h} in the tile's plots, or null (and the sh... |

### THE DISTRICT PICTURES (0.7.128, batch MM1; docs/studies/spec-globe.md (lines 2942-3045)

| line | len | member | says |
|---:|---:|---|---|
| 2969 | 17 | `int[] extrasIn(District d)` | The boxes the runs and the shore stand on district d's ground, as the painter draws them over its plan (fillInput()): its yards as Rail Terminals and its terminals and tank farms, each {x, y, w, h, type id} in the dis... |
| 2988 | 10 | `public DistrictPicture pictureIfCurrent(District d)` | District d's picture when it is its plan's, the plan current - every link before it in its band holding - else null: the screen's thread, which never plans. |
| 3007 | 16 | `public List<Job> pictureJobs(District d)` | The jobs that draw district d's picture: its plan's chain, as planJobs() hands it out but for its band's packing at the edge, which a picture does not draw (star MM1-3); or, where its plan is current and its picture i... |
| 3025 | 11 | `public DistrictPicture picture(District d)` | District d's picture, drawn here when it is not current (pictureJobs() run and kept on this thread): a harness's and a probe's. |
| 3038 | 1 | `public long picturesKept()` | How many pictures have been kept since the map was drawn (picturesKept). |
| 3041 | 1 | `public int picturesHeld()` | The pictures kept now: what MapTiles' budget counts PICTURES_KEPT of, DistrictPicture.BYTES each. |
| 3044 | 1 | `public static int bandOf(District d)` | The band district d's links are kept in (CHAIN_BAND districts of the map's order a band): what a far view counts the bands it would plan by (MapTiles.picturesFit()). |

### THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch (lines 3046-3271)

| line | len | member | says |
|---:|---:|---|---|
| 3074 | 1 | `public CityRuns runs()` | The city's runs. |
| 3089 | 87 | **type** `private final class RunGround implements CityRuns.Ground` | The ground the runs read (CityRuns.Ground): a plot's World class where the city owns it, -1 where it does not - the terrain a tile's row or column at a time (World.lineTerrain(), byte for byte the painter's tileTerrai... |
| 3105 | 35 | `public int at(long x, long y, int dir)` _(in CityMap.RunGround)_ |  |
| 3142 | 23 | `private boolean ownedAt(long tx, long ty, int ix, int iy)` _(in CityMap.RunGround)_ | Whether the city owns plot (ix, iy) of tile (tx, ty): the grid's cover of the tile, its leaves where it owns some. |
| 3166 | 3 | `public long version()` _(in CityMap.RunGround)_ |  |
| 3170 | 5 | `public long[] box()` _(in CityMap.RunGround)_ |  |
| 3180 | 1 | `CityRuns.Ground runGround()` | The ground the runs read: a harness's, to lay runs of its own on the city's ground. |
| 3183 | 1 | `long[] site()` | The founding site, {x, y} in plots. |
| 3192 | 30 | `boolean layRuns(long[] model)` | Lays the runs to the model's counts (CityRuns.layTo()): its Elevated Highways' plots, its track's, its Rail Terminals as yards - the highways from the founding site, the railway from the city's mine nearest it (the fo... |
| 3224 | 11 | `List<long[]> minedSites()` | The city's mines and wells standing on their sites, each {x0, y0, x1, y1} inclusive: where the railway starts and its yards are drawn near. |
| 3241 | 17 | `int yardsOf(District d)` | How many of district d's Rail Terminals are the runs' yards. |
| 3260 | 11 | `long runsInto(District d, byte[] fixed)` | The runs' frame for district d's plan: their plots over its 257 x 257 (DistrictPlan's codes), a hash of them returned. |

### THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13; (lines 3272-3508)

| line | len | member | says |
|---:|---:|---|---|
| 3292 | 1 | `public CityShore shore()` | The city's works on its shore (0.7.97). |
| 3297 | 11 | `int[] shoreTypes()` |  |
| 3316 | 1 | `public int shoreShort()` | How many of the model's terminals and tank farms the shore has no place for: their districts' plans draw them. |
| 3319 | 3 | `private long groundKey()` | The ground's key, without the shore's own works: what a search that came up short waits on. |
| 3328 | 41 | `boolean layShore(long[] model)` | Lays the shore to the model's counts: one fewer of a type takes its newest work; one more is laid where shoreSpot() finds it, the first types first. |
| 3370 | 4 | `private static boolean anyOf(long[] model, int[] ids)` |  |
| 3384 | 68 | `CityShore.Work shoreSpot(int t, List<long[]> mines)` | Where the next work of type t goes (CityShore's rule), or null: the districts in the map's order, each one's tiles nearest the founding site first; on each tile with owned salt water and owned dry ground, its shore pl... |
| 3454 | 20 | `private static boolean shoreFits(CityShore.Work c, int[] ground, byte[] fixed, long wx0, long wy0, int ww)` | Whether a work fits where c puts it, on the window's ground and runs from (wx0, wy0), ww a side: its box in one cell's interior on owned dry ground no run, mine or other work takes, no highway beside it, and its quay ... |
| 3476 | 8 | `static boolean nearRing(CityShore.Work c)` | Whether a work's box lies within TilePainter.REACH of its cell's ring on a side away from the sea: where an arterial runs when the cell is open. |
| 3490 | 18 | `int shoreOf(District d, int t)` | How many of district d's buildings of type t the shore holds. |

### THE OIL AT SEA (0.7.97, batch O13; docs/studies/spec-oil.md 2.12, the (lines 3509-3572)

| line | len | member | says |
|---:|---:|---|---|
| 3522 | 1 | **type** `public record Jacket(double x, double y, int wells, List<Long> wellPlots)` | A platform as the map draws it: its jacket's middle in plots, its wells, and the plots its wells stand on (each x << 32 \| y). |
| 3525 | 1 | **type** `public record Pipe(double x0, double y0, double x1, double y1)` | A pipe as the map draws it: from its field's middle (x0, y0) toward the founding site, to (x1, y1), in plots - its kilometres standing. |
| 3528 | 4 | **type** `public record AtSea(List<Jacket> jackets, List<Pipe> pipes)` | The oil at sea: its jackets and pipes. |
| 3536 | 1 | `public AtSea atSea()` | The oil at sea, as the game last handed it. |
| 3539 | 6 | `public void atSea(AtSea a)` | The oil at sea handed over by the game (Game.mapAtSea()); a change restamps the tiles it crosses. |
| 3547 | 25 | `private void atSeaInto(long px0, long py0, TilePainter.Input in)` | The oil at sea over tile (px0, py0)'s plots, into its inputs: the jackets' and wells' plots, the rings and pipes that may cross it. |

### THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1) (lines 3573-3657)

| line | len | member | says |
|---:|---:|---|---|
| 3587 | 6 | `double cityRadius()` | The city's radius for the offing, in plots: the farthest corner of its owned box from the founding site. |
| 3595 | 5 | `private long routesKeyNow()` | The key the routes are found against: the berths, the founding site and the city's radius. |
| 3602 | 8 | `public List<BoatSchedule.Route> seaRoutes()` | The routes now, found here when they are not (a harness's way; the view hands a RoutesJob to its worker). |
| 3612 | 3 | `public List<BoatSchedule.Route> seaRoutesIfFound()` | The routes when they are found and current, else null: the screen's thread, which never finds them itself. |
| 3617 | 25 | **type** `public static final class RoutesJob implements Runnable` | The berths' routes found away from the screen's thread: what they are found from, copied here. |
| 3625 | 9 | `RoutesJob(long key, World.Key world, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius)` _(in CityMap.RoutesJob)_ |  |
| 3635 | 3 | `public void run()` _(in CityMap.RoutesJob)_ |  |
| 3640 | 1 | `public boolean done()` _(in CityMap.RoutesJob)_ | Whether it has run. |
| 3644 | 5 | `public RoutesJob routesJob()` | The job that finds the routes now, or null when they are current. |
| 3651 | 6 | `public boolean adoptRoutes(RoutesJob j)` | A job run: its routes kept when they are still the city's. |

### A TILE'S INPUTS FOR THE PAINTER (lines 3658-3968)

| line | len | member | says |
|---:|---:|---|---|
| 3663 | 3 | `public District districtOfTile(long tx, long ty)` | The district holding tile (tx, ty), or null. |
| 3668 | 3 | `static int tileIn(long tx, long ty)` | The tile in district d at its column and row of tiles (tile = row x 8 + column), from world tile (tx, ty). |
| 3673 | 10 | `public void tileCounts(long tx, long ty, int[] out)` | A tile's buildings by type id as its district's plan draws them (0.7.88; the deal's until 0.7.87), packed ones among them: into out, zeros where no district is. |
| 3685 | 8 | `public void tileOwnership(long tx, long ty, boolean[] out)` | Which of a tile's plots the city owns: a tile owned wholly or not at all in one test (the grid's cover at level 5), the rest from the grid's leaves under it (spec-grid 2.4). |
| 3704 | 4 | `public void tileInput(long tx, long ty, TilePainter.Input in)` | Everything the painter needs for tile (tx, ty), into in: its ground, what the city owns of it, and from its district's plan (0.7.88; the deal's counts and road plots until 0.7.87) the streets through it and every buil... |
| 3710 | 4 | `public void tileInput(long tx, long ty, TilePainter.Input in, byte[] terrain)` | ...with the tile's ground given (a view keeps it: MapTiles.terrain(), batch J4) rather than read from the world. |
| 3716 | 10 | `public List<District> tileDistricts(long tx, long ty)` | The districts a tile's picture is drawn from (0.7.88): those of the tiles about it - its own, the ones whose plans lay the street on its first column or row (west, north, north-west) and those its edges look across to... |
| 3728 | 4 | `public boolean tileReady(long tx, long ty)` | Whether tile (tx, ty) paints without planning: every plan it is drawn from current (planned()). |
| 3734 | 5 | `public List<Job> tileJobs(long tx, long ty)` | The jobs that make tile (tx, ty)'s plans current (planJobs()), in order; empty when they are. |
| 3748 | 3 | `byte streetAt(long x, long y)` | The street a plan draws on world plot (x, y) (TilePainter's S_ codes): the district's own; on its first column or row the district west or north of it (or north-west, at its corner) lays the same plot as its frame's m... |
| 3753 | 32 | `private byte streetAt(long x, long y, Map<Long, Drawn> seen)` | ...the plans looked up through `seen`, by district key (null for none), which it fills. |
| 3787 | 92 | `private void fillInput(long tx, long ty, TilePainter.Input in)` | Everything tileInput() fills but the ground, which is in `in` already. |
| 3881 | 1 | **type** `record DrawnSite(long x0, long y0, long x1, long y1, int kind, int state, int mine)` | One site as the map draws it: its square of plots (inclusive), resource, state and the mine on it (-1 for none). |
| 3897 | 57 | `List<DrawnSite> siteList(int dx, int dy)` | The sites whose centres lie in district (dx, dy) - every resource's, owned or not - each with its holding's state (its field's holding, the one holding the field's centre, since 0.7.64) and, for the SITED resources, t... |
| 3956 | 12 | `public void forgetPainted()` | Forgets the painted state of every district - the plans, their links, the packing and the site lists - after the ground's states moved (what is worked out). |

### THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's (lines 3969-4101)

| line | len | member | says |
|---:|---:|---|---|
| 3988 | 3 | `DistrictPlan plan(District d)` | District d's own plan, drawn now from planInput(d) - its own buildings alone, none carried to it (R7): not kept (THE DRAWN PLANS keep the painter's). |
| 3993 | 5 | `DistrictPlan.Input planInput(District d)` | The inputs of district d's plan (DistrictPlan.Input), its ground read from the world. |
| 4000 | 15 | `static void readGround(DistrictPlan.Input in, World.Key world)` | The ground over a plan's frame, a World class a plot, read from the world the key names (0.7.115; the input's seed's until then): any thread (World is safe), so a plan's job reads it away from the screen's (0.7.88). |
| 4017 | 84 | `DistrictPlan.Input planInputHere(District d)` | ...everything but the ground: what the map's own thread reads (the land, the counts, the sites, the runs). |

### ZONES BY DISTRICT (0.8.0, batch I14; docs/studies/spec-zoning.md 4: (lines 4102-4172)

| line | len | member | says |
|---:|---:|---|---|
| 4120 | 1 | **type** `public record ZoneArea(String sector, double sqFt)` | A zone as the map tints it: its sector and the ground it covers, in square feet - what it holds free and what its sector has built in it (LandManager.heldBy() and usedBy()). |
| 4123 | 1 | **type** `public record Tinted(long x0, long y0, int zone)` | A district a zone's tint covers: its box's first plot, {x, y} in the world, a district (DISTRICT plots) a side, and the zone's index in the list given. |
| 4126 | 46 | `public List<Tinted> zoneTint(List<ZoneArea> zones)` | Each zone's districts (ZONES BY DISTRICT), in the map's order: none with no zone. |

### THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 (lines 4173-4304)

| line | len | member | says |
|---:|---:|---|---|
| 4192 | 112 | **type** `public static final class Pyramid` | The districts summed two by two, level by level, to one node - level 0 is the districts themselves, each level above a grid of nodes over the districts' box, a node NODE_WIDTH numbers. |
| 4200 | 45 | `Pyramid(CityMap map)` _(in CityMap.Pyramid)_ |  |
| 4247 | 11 | `long[] vector(District d)` _(in CityMap.Pyramid)_ | A district's numbers: its buildings by class, its ground used (half square feet) and its owned dry plots. |
| 4260 | 15 | `void add(District d, int t, long k)` _(in CityMap.Pyramid)_ | k more (or fewer) of type t in a district: its ancestors take them. |
| 4277 | 1 | `public int levels()` _(in CityMap.Pyramid)_ | How many levels above the districts. |
| 4280 | 1 | `public int nodes()` _(in CityMap.Pyramid)_ | How many nodes above the districts hold anything (and the top). |
| 4283 | 11 | `public long[] root()` _(in CityMap.Pyramid)_ | The top node: the whole city's numbers. |
| 4296 | 7 | `public long[] node(int level, int dx, int dy)` _(in CityMap.Pyramid)_ | The node at a level (1 up) covering district (dx, dy), or null. |

### THE SIDECAR (spec-land 2.5) (lines 4305-4535)

| line | len | member | says |
|---:|---:|---|---|
| 4324 | 3 | `public byte[] writeSidecar(int month)` | The map as its sidecar's bytes, deflated: the header (magic, format, the world - its seed, or since 0.7.121 an Earth world's key folded, worldId() - month, stamp, the founding site, the stamp of the land's rectangles ... |
| 4329 | 3 | `byte[] writeSidecar(int month, int format)` | ...in a given format, FORMAT or OLDEST_READ (a harness writes the older one to read it back). |
| 4334 | 51 | `byte[] writeSidecar(int month, int format, boolean withRuns)` | ...and with its runs or (a harness's, as 0.7.88 wrote FORMAT 5) a count of none. |
| 4396 | 7 | `static long worldId(World.Key key)` | The world a sidecar names in its header (0.7.121, batch E4): a generated world's seed, as every sidecar has carried it, so theirs are the bytes they were; an Earth world's whole key - its seed, spot and data - folded ... |
| 4405 | 1 | `public long lastStamp()` | The stamp of the sidecar last written: what the save carries as mapStamp. |
| 4408 | 13 | `static long stampOf(byte[] raw)` | The stamp of a sidecar's raw bytes: SplitMix64 folded over them, the stamp's own eight bytes read as zero. |
| 4423 | 7 | `public static long stampIn(byte[] deflated)` | The stamp a sidecar carries, or 0 when it is not one. |
| 4431 | 7 | `private static byte[] inflate(byte[] deflated)` |  |
| 4446 | 74 | `public static CityMap readSidecar(byte[] deflated, CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[]...` | A map read back from its sidecar, or null when it is not one, is not this city's (another seed, month or stamp, or land drawn otherwise), or does not add up. |
| 4522 | 3 | `public boolean same(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order, and (0.7.89) the same runs, and (0.7.97) the same works on the shore. |
| 4527 | 8 | `public boolean sameDistricts(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order. |

