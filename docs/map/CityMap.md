# CityMap.java - 3,992 lines · 225 methods · 30 constants · model

`ham/citybuildersim/CityMap.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, each district's street plan kept for the painter with what it cannot hold carried to the next, the city's highways and railway laid on corridors across it, one drawn building for each the model has, summed up a pyramid for the far view, and written beside the save as a sidecar - what the tile painter paints the city from.
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
> plots); its owned iron and oil sites; and its count of every type.
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
> districts holding those sites.
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
> changes: what the far view draws (batch J4).
> 
> THE SIDECAR (slot-NN-map.bin, writeSidecar()/readSidecar()): one record a
> district, deflated, its header the magic, the format, the world's seed, the
> ... (24 more lines in the source)

**Uses:** [World](World.md) (181), [DistrictPlan](DistrictPlan.md) (73), [BuildingVisual](BuildingVisual.md) (57), [TilePainter](TilePainter.md) (44), [Resource](Resource.md) (26), [CityShore](CityShore.md) (22), [CityRuns](CityRuns.md) (16), [CityLand](CityLand.md) (13), [Deposit](Deposit.md) (13), [BoatSchedule](BoatSchedule.md) (7), [LandGrid](LandGrid.md) (5), [SeaRoutes](SeaRoutes.md) (5), [LandParcel](LandParcel.md) (1)

**Used by (10):** [CityRuns](CityRuns.md), [DistrictPlan](DistrictPlan.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [PlanCheck](PlanCheck.md), [ReadPathCheck](ReadPathCheck.md), [TilePainter](TilePainter.md)

## Sections

| line | section |
|---:|---|
| 108 | THE GEOMETRY |
| 151 | A DISTRICT |
| 217 | THE STATE |
| 286 | BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE |
| 392 | MEASURING THE GROUND: OWNED DRY PLOTS |
| 595 | THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN |
| 643 | PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION |
| 723 | · ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3) |
| 1223 | MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) |
| 1561 | THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch |
| 2612 | THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch |
| 2839 | THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13; |
| 3076 | THE OIL AT SEA (0.7.97, batch O13; runs/spec-oil.md 2.12, the |
| 3140 | THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1) |
| 3222 | A TILE'S INPUTS FOR THE PAINTER |
| 3522 | THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's |
| 3655 | THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 |
| 3787 | THE SIDECAR (spec-land 2.5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 113 | `CityMap.DISTRICT` | `World.DISTRICT` | Plots on a district's side: World.DISTRICT, 256 (7.68 km). |
| 116 | `CityMap.TILES_A_SIDE` | `DISTRICT / World.TILE` | Tiles on a district's side: 8. |
| 119 | `CityMap.TILES` | `TILES_A_SIDE * TILES_A_SIDE` | Tiles in a district: 64. |
| 122 | `CityMap.HALF_SQ_FT_PER_PLOT` | `Math.round(2 * BuildingVisual.SQ_FT_PER_PLOT)` | Half square feet in a plot: 9,687.5 sq ft twice, so a district's room and use are whole numbers and a month's change is exact however it is added up. |
| 125 | `CityMap.MIN_ROOM` | `1` | A district with less room than this many whole plots is passed by the cursors: one, the least a building is drawn on (0.7.64; half a plot of ground before). |
| 128 | `CityMap.SITED` | `{ Resource.IRON, Resource.OIL }` | The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5). |
| 131 | `CityMap.SITE_LISTS_KEPT` | `64` | How many districts' lists of sites are kept: 64. |
| 134 | `CityMap.COARSE_ABOVE` | `1024` | Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). |
| 137 | `CityMap.COARSE_STRIDE` | `32` | The coarse look's stride, in plots: 32, a sample a tile. |
| 140 | `CityMap.MAGIC` | `0x434D4150` | The sidecar's magic: "CMAP". |
| 143 | `CityMap.FORMAT` | `6` | The sidecar's format: 6 since 0.7.97, the city's works on its shore after the runs (CityShore: each terminal's and tank farm's box, never moved) - a FORMAT 5 sidecar has them laid from its counts as it is read, its di... |
| 146 | `CityMap.OLDEST_READ` | `4` | The oldest sidecar read: FORMAT 4 (0.7.72 to 0.7.87) - its districts are placed as 0.7.88 places them; it has no runs, which are laid from its counts as it is read. |
| 149 | `CityMap.STAMP_AT` | `4 + 4 + 8 + 4` | Where the stamp sits in the sidecar's raw bytes: after the magic, the format, the seed and the month. |
| 455 | `CityMap.NONE` | `0, SOME = 1, ALL = 2` |  |
| 1369 | `CityMap.DISTRICT_ORDER` | `Comparator.comparingDouble((District d) -> d.order).thenComparingInt(d -> d.d...` | Nearest the founding site first, then north to south, west to east. |
| 1596 | `CityMap.PLANS_KEPT` | `64` | How many districts' plans are kept, packed for the painter (Drawn): 64, as the deals and road tiles were (spec 2.9) - each tile's street rows once a pattern and four bytes a building (RD1's plan was 140 KB as District... |
| 1599 | `CityMap.PACKED_BIT` | `1<<20` | A packed box's flag (Drawn.boxes): drawn without a street, R7's packing at the city's edge. |
| 1602 | `CityMap.BOX_TYPE_SHIFT` | `21` | Where a Drawn box's type id starts: above the box's 20 bits and PACKED_BIT, eleven bits for ids under 2,048 (buildings.json's ids are under 128). |
| 1786 | `CityMap.M_WEST` | `0, M_NORTH = 1, M_EAST = 2, M_SOUTH = 3, M_PARTED = 4` | A plan frame's edges' street codes (Link.margins): its west column (x 0) and north row (y 0), its east column (x 256, the next district's first) and south row (y 256), each 257 long; and M_PARTED, a byte along them wi... |
| 1807 | `CityMap.AROUND` | `{ { 0, - 1 }, { 1, 0 }, { 0, 1 }, { - 1, 0 }, { - 1, - 1 }, { 1, - 1 }, { 1, ...` | The eight districts about one, by offset {dx, dy}: north, east, south, west, then north-west, north-east, south-east, south-west - the order of a frame's seam parts (DistrictPlan.Input.surfaces). |
| 1810 | `CityMap.CORNER_DIRS` | `{ { 3, 0, 4 }, { 1, 0, 5 }, { 1, 2, 6 }, { 3, 2, 7 } }` | Each corner seam part's districts sharing it besides its own, as AROUND's directions: north-west, north-east, south-east, south-west. |
| 1937 | `CityMap.CHAIN_BAND` | `128` | THE CHAIN'S BANDS (0.7.89, batch RD3). |
| 1940 | `CityMap.BANDS_KEPT` | `16` | The bands whose links are kept, the last used: 16 (2,048 districts' links, about 4 MB) - a band let go is planned again when a screen asks for it. |
| 2573 | `CityMap.INTERIOR_MOST` | `World.TILE - 1` | A tile's interior holds a box no larger than this a side: 31 plots, off its arterial lines. |
| 2643 | `CityMap.RUN_LINES_KEPT` | `4096` | Terrain lines (a tile's row or column, World.lineTerrain()) the runs' ground keeps: 4,096, about 460 KB - a corridor's way ahead and back, at any size. |
| 2646 | `CityMap.RUN_TILES_KEPT` | `256` | ...whole tiles, for a 45-degree way: 256. |
| 3097 | `CityMap.AtSea.NONE` | `new AtSea(List.of(), List.of())` | None. |
| 3660 | `CityMap.NODE_WIDTH` | `BuildingVisual.CLASSES + 2` | How many numbers a node sums: the ten classes, the ground used (in half square feet, exact) and the owned dry plots. |
| 3663 | `CityMap.NODE_USED` | `BuildingVisual.CLASSES` | Where a node keeps the ground used, in half square feet. |
| 3666 | `CityMap.NODE_OWNED` | `BuildingVisual.CLASSES + 1` | ...and its owned dry plots. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 158 | `public final int dx, dy` | Its column and row, in districts from the founding site's. |
| 159 | `final long key` |  |
| 160 | `final double order` |  |
| 162 | `int owned` | Its free plots: owned and dry, counted plot by plot (0.7.64; from 32 x 32 samples before) - or, for a harness's square city, given. |
| 164 | `long usedHalf` | The ground its buildings use, in half square feet: each type's count x its land, kept exactly. |
| 166 | `long usedCells` | The whole plots its buildings and roads are drawn on (BuildingVisual.cells()), a mine or well on its site taking its own: what its room is kept in (0.7.64). |
| 168 | `int inner` | Its free plots inside its tiles' edge rings, where alone the painter laid a road until 0.7.87, and the road plots it holds: its room for roads, by which its counts are still placed (0.7.64). |
| 169 | `long usedRoad` |  |
| 171 | `final int[] counts` | Its count of every type, by id. |
| 173 | `final int[] sites` | Its owned sites of each SITED resource. |
| 175 | `short[] tileFree` | Each tile's free plots, row by row: owned and dry; null until measured or asked for. |
| 177 | `short[] tileInner` | ...and those inside its edge ring, where alone the painter laid a road until 0.7.87 (the counts' room for roads still measures it). |
| 179 | `long[][] tileSiteKey` | Each tile's sites, by key (TilePainter.Input.siteKey), and their free plots on it, all and inside the edge ring: what a mine standing on one takes of the tile. |
| 180 | `short[][] tileSiteFree, tileSiteInner` |  |
| 182 | `int[] sited` | How many of each site-bound type stand on its sites (the rest of its count has none); null when none. |
| 184 | `int[][] holdingMines` | For painting: per SITED resource, pairs of {holding, mines} - how many of each holding's sites here carry one. |
| 186 | `long version` | Bumped whenever its counts change: what its sites' list is kept against (its deal's until 0.7.87). |
| 188 | `int index` | Its place in the map's order, nearest first. |
| 221 | `private final long seed` |  |
| 222 | `private final long siteX, siteY` |  |
| 223 | `private final long baseDX, baseDY` |  |
| 224 | `private final BuildingVisual.Type[] types` |  |
| 225 | `private CityLand land` |  |
| 226 | `private Function<Resource, double[]> remaining` |  |
| 228 | `private final List<District> districts` |  |
| 229 | `private final Map<Long, District> byKey` |  |
| 230 | `private final long[] have` |  |
| 231 | `private final int[] lo, hi` |  |
| 232 | `private int innerCursor, outerCursor` |  |
| 233 | `private int purchasesSeen` |  |
| 234 | `private long centreSeen` |  |
| 235 | `private boolean measured` |  |
| 236 | `private Pyramid pyramid` |  |
| 237 | `private SiteIndex siteIndex` |  |
| 238 | `private boolean sitedPlaced` |  |
| 696 | `private long changes` | How many changes the map has taken since it was drawn - a district's count, a purchase measured, a site's state: what the view stamps its tiles again on (MapTiles). |
| 715 | `private int[][] lastStates` | Each resource's holdings' states at the last month: when they move, the sites are drawn again. |
| 718 | `int lastTouched` | How many district-types the last month's change touched. |
| 721 | `double lastRunsMs` | How long the last month's runs took to lay, in ms (0.7.89): a harness's. |
| 738 | `private double roadShare` | The city's road plots for each plot of its other buildings, this month: its roads' whole plots over everything else's (0 with no road). |
| 772 | `private final List<District> builtIn` | The districts the month's new buildings went into (ROADS FOLLOW THEIR BUILDINGS): where its new roads are looked for, so a month's work stays with the districts it changes. |
| 773 | `private final java.util.Set<District> builtInSet` |  |
| 808 | `private double[] lackOf` | THE LACKING HEAP (0.7.94, batch RD7): placeRoad()'s districts, the one whose buildings lack road the most first, the lower index on a tie - the order the PriorityQueue it replaces kept, entry for entry (no two entries... |
| 809 | `private int[] lackAt` |  |
| 810 | `private int lackN` |  |
| 918 | `private long[] passedSize` | WALKS PASSED (0.7.94, batch RD7). |
| 919 | `private int[] passedTo` |  |
| 920 | `private int nPassed` |  |
| 970 | `private int[] campusTypes` | The refinery's unit types, by id: worked out once a table. |
| 1240 | `District[][][] where` | [resource][holding]: the districts, and the sites in each. |
| 1241 | `int[][][] how` |  |
| 1243 | `final List<List<Map<District, int[]>>> tally` | The tallies they are read from: [resource] then holding, district to sites. |
| 1390 | `SiteIndex ix` |  |
| 1391 | `int[] states` |  |
| 1392 | `int type` |  |
| 1393 | `District[] at` |  |
| 1394 | `int[] holding` |  |
| 1395 | `int length, pos, partial` |  |
| 1396 | `long placed, overflow` |  |
| 1399 | `private final Walk[] walks` |  |
| 1607 | `long stamp` | Its link's stamp: its own inputs (ownStamp()) and what was carried to it. |
| 1609 | `final byte[][] tiles` | Each tile's street codes (TilePainter's S_ codes), null where it has none, packed by row (pack()): a byte for each of its 32 rows saying which of its patterns that row is, then the patterns, 32 codes each - a tile's s... |
| 1611 | `byte[] east, south` | The frame's east column and south row: the first column and row of the districts east and south of it, where its edge cells' last arterials run (null where it lays none). |
| 1613 | `final int[][] boxes` | Each tile's buildings, an int each: the box - x, y, w - 1 and h - 1 in the tile's plots, five bits each, PACKED_BIT when packed without a street - and the type id from BOX_TYPE_SHIFT. |
| 1615 | `int placed, refused` | Buildings its plan placed, and what it could not hold (carried on, R7). |
| 1617 | `final long[] halves` | Its streets' surface in half plots by kind, [0, gravel, paved, highway], the plots of street it lays and how many of them are tracks: what its own road is drawn as. |
| 1618 | `int streets, tracks` |  |
| 1620 | `int joinsOut, partedOut, seamsSurfaced` | Its pieces joined to the streets of the districts before it, those no way over its ground joins, and its one-sided seams surfaced (DistrictPlan's figures). |
| 1626 | `double surplus` | Road it has no street for (the plan's surplus), in plots, and by kind [0, gravel, paved, highway]. |
| 1627 | `final double[] leftover` |  |
| 1761 | `final long own, serial` |  |
| 1762 | `final int[] in, out` |  |
| 1763 | `final long[] free` |  |
| 1764 | `final byte[][] margins` |  |
| 1765 | `final District[] earlier` |  |
| 1766 | `final long[] read` |  |
| 1768 | `final double surplus` | Its plan's road with no street to carry it (DistrictPlan.surplus), in plots: what the legend counts (star RD2-4); by kind [0, gravel, paved, highway]. |
| 1769 | `double[] leftover` |  |
| 1910 | `long stamp` |  |
| 1911 | `final Map<District, int[][]> boxes` |  |
| 1912 | `int packed, smaller, none` |  |
| 1915 | `private final Map<District, Drawn> drawn` |  |
| 1918 | `private final Map<District, Link> links` |  |
| 1919 | `private final Map<District, PlanJob> pending` |  |
| 1921 | `private final Map<Integer, Pack> packs` | Each band's packing at its edge, and the job making it. |
| 1922 | `private final Map<Integer, PackJob> packings` |  |
| 1949 | `private long[] bandEpoch` | Each band's links checked at an epoch (bandEpoch): those of its districts up to bandValidTo (an index in the map's order) hold, and (when all do) the stamp of the packing they make. |
| 1950 | `private int[] bandValidTo` |  |
| 1951 | `private int bandsSized` |  |
| 1954 | `private final LinkedHashMap<Integer, Boolean> bandsUsed` | The bands used, the last last: those past BANDS_KEPT are let go (forgetBand()). |
| 1957 | `private long jobsHanded` | Jobs handed out so far: each job's serial, which keeps a later job's link over an earlier one's adopted after it. |
| 2122 | `final CityMap map` |  |
| 2124 | `final long epoch, serial` | The epoch it was handed out at (planEpoch()), and its place among every job the map handed out: a later job's inputs are the newer. |
| 2125 | `private boolean done` |  |
| 2140 | `final District d` |  |
| 2141 | `final DistrictPlan.Input in` |  |
| 2142 | `final long own` |  |
| 2143 | `final PlanJob after` |  |
| 2144 | `final int[] given` |  |
| 2145 | `final boolean edge` |  |
| 2147 | `final District[] earlier` | The districts about it before it in the map's order (earlierOf()), and each one's link or job: whose frames' edges it reads (readNeighbours()). |
| 2148 | `final Object[] sources` |  |
| 2149 | `int[] carryIn, carryOut` |  |
| 2150 | `Drawn result` |  |
| 2151 | `long[] free, read` |  |
| 2152 | `byte[][] margins` |  |
| 2153 | `double ms` |  |
| 2198 | `long stamp` |  |
| 2199 | `int band` |  |
| 2200 | `final District[] edges` |  |
| 2201 | `final Object[] from` |  |
| 2202 | `final Object last` |  |
| 2203 | `final BuildingVisual.Type[] types` |  |
| 2204 | `final long[] tileOrder` |  |
| 2205 | `Pack result` |  |
| 2634 | `private long landVersion` | Bumped whenever free ground is measured again (a purchase): what a run waiting on the ground is tried again on. |
| 2637 | `private CityRuns runs` | The city's highways and railway (spec 2.7, 2.8). |
| 2656 | `private final Map<Long, byte[]> lines` |  |
| 2659 | `private final Map<Long, byte[]> tiles` |  |
| 2662 | `private final Map<Long, boolean[]> owned` |  |
| 2665 | `private long ownedAt` |  |
| 2666 | `private byte[] lastLine` |  |
| 2667 | `private boolean[] lastOwn` |  |
| 2668 | `private int lastCover` |  |
| 2669 | `private final boolean[] ALL_OWNED` |  |
| 2743 | `private final RunGround runGround` |  |
| 2804 | `private int[] yardShare` | Each district's Rail Terminals the runs draw as yards, by its index: the yards dealt to the districts holding terminals in the map's order, so the rest are their plans' (kept against the runs' version). |
| 2805 | `private long yardShareAt` |  |
| 2856 | `private CityShore shore` | The city's works on its shore. |
| 2862 | `private int[] shoreTypes` | The shore's types, by id: worked out once a table. |
| 2877 | `private long shoreShortAt` | The ground's key a search came up short at: none is made again until the ground moves (a purchase) or a work is taken. |
| 2880 | `private int shoreShort` | How many works the model has that the shore holds no place for (drawn by their districts' plans). |
| 3053 | `private int[][] shoreShare` | Each district's works the shore holds, by its index and the shore's type: the works dealt to the districts holding their type in the map's order, so the rest are their plans' (kept against the shore and the counts). |
| 3054 | `private long shoreShareAt` |  |
| 3100 | `private AtSea atSea` |  |
| 3150 | `private List<BoatSchedule.Route> routes` | The routes found, and the key of what they were found from. |
| 3151 | `private long routesKey` |  |
| 3185 | `final long key, seed, siteX, siteY` |  |
| 3186 | `final List<BoatSchedule.Berth> berths` |  |
| 3187 | `final double radius` |  |
| 3188 | `private volatile List<BoatSchedule.Route> found` |  |
| 3447 | `private final Map<Long, List<DrawnSite>> siteLists` |  |
| 3450 | `private final Map<Long, Long> siteListVersion` |  |
| 3675 | `private final CityMap map` |  |
| 3676 | `private final int minX, minY` |  |
| 3678 | `private final int[] widths, heights` | Each level's grid: its width, height and nodes (NODE_WIDTH numbers each, row by row). |
| 3679 | `private final long[][] nodes` |  |
| 3680 | `private final int count` |  |
| 3864 | `private long lastStamp` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 106 | 3887 | **type** `public final class CityMap` | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, each district's street plan kept for the painter with what it cannot ... |

### THE GEOMETRY (lines 108-150)

### A DISTRICT (lines 151-216)

| line | len | member | says |
|---:|---:|---|---|
| 156 | 58 | **type** `public static final class District` | One district: where it is, its free plots, the ground and the plots used, its owned sites, its counts by type. |
| 190 | 7 | `District(int dx, int dy, int types, double order)` _(in CityMap.District)_ |  |
| 199 | 1 | `public int owned()` _(in CityMap.District)_ | Its free plots (owned and dry, its sites' plots among them). |
| 201 | 1 | `public double used()` _(in CityMap.District)_ | The plots of ground its buildings use: each type's count x its land in plots. |
| 203 | 1 | `public long usedCells()` _(in CityMap.District)_ | The whole plots its buildings and roads are drawn on. |
| 204 | 1 | `public int count(int t)` _(in CityMap.District)_ |  |
| 205 | 4 | `public int sites(Resource r)` _(in CityMap.District)_ |  |
| 210 | 1 | `long freeCells()` _(in CityMap.District)_ | Its room in whole plots, never below nothing (0.7.64; in ground, half square feet, before). |
| 212 | 1 | `long freeRoad()` _(in CityMap.District)_ | ...and for roads: its whole room, no more than its plots inside the edge rings less its road. |
| 215 | 1 | `static long key(int dx, int dy)` |  |

### THE STATE (lines 217-285)

| line | len | member | says |
|---:|---:|---|---|
| 240 | 11 | `private CityMap(long seed, long siteX, long siteY, BuildingVisual.Type[] types)` |  |
| 253 | 1 | `public long seed()` | The world's seed. |
| 256 | 1 | `public BuildingVisual.Type[] types()` | The types it counts, by id. |
| 259 | 1 | `public List<District> districts()` | Its districts, nearest the founding site first. |
| 262 | 1 | `public District district(int dx, int dy)` | The district at (dx, dy) from the founding site's, or null. |
| 265 | 1 | `public Pyramid pyramid()` | The pyramid. |
| 268 | 1 | `public boolean measured()` | Whether its districts were measured on the ground (false for a harness's square city, whose capacities were given). |
| 271 | 5 | `public long[] totals()` | Every type's count over all districts. |
| 278 | 1 | `public CityLand land()` | The land it was drawn on. |
| 281 | 1 | `long baseDX()` | The founding site's district's column, absolute. |
| 284 | 1 | `long baseDY()` | ...and row. |

### BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE (lines 286-391)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 11 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] counts)` | A map drawn canonically (spec-land 2.5): the land's districts measured, then each type in proportion in every district, inner first, the remainders by largest remainder; mines and wells on their sites. |
| 322 | 30 | `static CityMap square(long seed, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity, long[] counts)` | A HARNESS'S SQUARE CITY (the design's scale case): side x side districts round the founding site, every one owned and holding `capacity` plots - a city of any size at one density without measuring its ground; a distri... |
| 353 | 9 | `private District newDistrict(int dx, int dy)` |  |
| 363 | 8 | `private void sortDistricts()` |  |
| 373 | 13 | `private void rangeTypes()` | Each type's lowest and highest district index holding it. |
| 387 | 4 | `private void advanceCursors()` |  |

### MEASURING THE GROUND: OWNED DRY PLOTS (lines 392-594)

| line | len | member | says |
|---:|---:|---|---|
| 397 | 27 | `private void measureAll()` | Every district the land's owned box reaches, measured; the coarse look first when there are many. |
| 426 | 28 | `private boolean[] coarseLand(int dx0, int dy0, int nx, int ny)` | Which districts have land in or beside them, by a sample a tile (COARSE_STRIDE): a district all sea there, with all-sea neighbours, is taken as sea and not measured. |
| 458 | 3 | `int ownership(int dx, int dy)` | Whether the city owns none, some or all of a district: the grid's cover of it, a level-8 block (spec-grid 2.4; by its radii and lanes until 0.7.66). |
| 463 | 3 | `private static int cover(int c)` | LandGrid's NONE, SOME or ALL as this class's. |
| 468 | 5 | `double[] ownedBox()` | The land's owned box from the site, in plots, inclusive {dx0, dy0, dx1, dy1}: its reach each way (an empty land, the site's plot). |
| 484 | 66 | `private void measure(District d, int own)` | Counts a district's free plots, tile by tile and plot by plot (0.7.64): owned and dry - the ground the model builds on, a resource's sites with it (a mine stands on its site; another building takes a site's plot only ... |
| 552 | 18 | `private List<long[]> siteSquares(long bx0, long by0, long bx1, long by1)` | Every site's square, {x0, y0, x1, y1} in plots inclusive, of every resource in fields, owned or not, that reaches into a box of plots: the plots the painter keeps as fields. |
| 572 | 8 | `private District measureNew(int dx, int dy, int own)` | A district not on the map yet, measured, and kept only when it holds free ground: a district of water joins the map only when a site in it needs one (tallySites()). |
| 582 | 1 | `public short[] tileFreeOf(District d)` | A district's tiles' free plots (a copy), counted if they were not: for a harness and a probe. |
| 585 | 9 | `short[] tileFree(District d)` | A district's tiles' free plots, counted if they were not (after a sidecar is read, or for a harness's square city); its room stands as it was. |

### THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN (lines 595-642)

| line | len | member | says |
|---:|---:|---|---|
| 605 | 37 | `boolean syncLand()` | Catches the map up with its land: every district a new purchase touches recounted (new ones added); true when the land was drawn again (a restatement, a conversion), which the caller answers with a canonical map. |

### PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION (lines 643-722)

| line | len | member | says |
|---:|---:|---|---|
| 656 | 38 | `public boolean reconcile(long[] model)` | THE MONTH (spec-land 2.5, star 12): the land caught up, then each type's change placed - new buildings into the first district with room (nearest first; farms, utilities, mines and wells, the railway and the car plant... |
| 699 | 1 | `public long changes()` | How many changes the map has taken since it was drawn: when it moves, a view stamps the tiles it shows again (batch J4). |
| 708 | 5 | `void rebind(CityLand live, Function<Resource, double[]> liveRemaining)` | A map drawn away from the screen's thread, on a copy of the land (Game.MapDraft, batch J4), bound to the city's own land and what remains of its resources before it is kept: the copy was the land as it stood, field fo... |

### ROADS FOLLOW THEIR BUILDINGS (0.7.72, batch N3) (lines 723-1222)

| line | len | member | says |
|---:|---:|---|---|
| 741 | 9 | `double roadShareOf(long[] model)` | The model's road plots over its other whole plots. |
| 752 | 7 | `long buildingRoom(District d, long each)` | How many buildings of `each` whole plots district d has room for, keeping room for the road they and its buildings need at the city's share: k with k x each plus the road short of share(d's other plots + k x each) no ... |
| 761 | 4 | `boolean hasRoomFor(District d, int t)` | Whether district d has room for one more of type t, as the month places it (MapCheck 2 asks). |
| 767 | 3 | `double roadLacking(District d)` | The road a district's buildings lack, in plots, at the city's share: share x its other plots less its road plots. |
| 776 | 22 | `private int placeRoad(int t, long n)` | Places n more roads of type t, one at a time: each into the district, of those the month's buildings went into, whose buildings lack road the most and that has room for it inside its tiles' edge rings, the nearer on a... |
| 813 | 4 | `private boolean lackFirst(int i, int j)` | Whether heap entry i comes before entry j: more lacking, then the lower index. |
| 819 | 6 | `private void lackAdd(double lack, int index)` | An entry added at the end, the heap not kept (placeRoad() makes it a heap after). |
| 826 | 10 | `private void lackPush(double lack, int index)` |  |
| 838 | 10 | `private int lackPoll()` | The first entry's district index, taken off the heap. |
| 850 | 10 | `private void lackDown(int k)` | Entry k sifted down to its place below. |
| 861 | 4 | `private void lackSwap(int i, int j)` |  |
| 867 | 37 | `private int place(int t, long n)` | Places n more of type t: into the first districts with room for its whole plots in its direction (a road's inside its tiles' edge rings; since 0.7.72 a building's keeping room for its road, buildingRoom()), the rest i... |
| 923 | 8 | `private int passed(int kind, long each, int i)` | Where a walk of `kind` for buildings of `each` plots from cursor i may start (WALKS PASSED). |
| 933 | 17 | `private void notePassed(int kind, long each, int to)` | A walk of `kind` for `each` plots found no room before district index `to` (WALKS PASSED). |
| 972 | 11 | `private int[] campusTypes()` |  |
| 985 | 5 | `long campusCells(District d)` | The refinery's ground in district d, in the whole plots its units are drawn on. |
| 992 | 15 | `District campusDistrict()` | The district holding the most of the refinery's ground, the nearer on a tie; null when it has none. |
| 1009 | 24 | `int gatherCampus()` | The refinery's units in other districts moved into its campus district while it has room, the largest kinds first, then by district in the map's order (a sidecar older than FORMAT 6, read). |
| 1035 | 5 | `District roomiest()` | The district with the most room left, the nearer on a tie: where what no district has room for goes, to be drawn smaller there. |
| 1042 | 21 | `private int remove(int t, long n)` | Takes n of type t away: an inner type from the outermost district holding it in, an outer type from the innermost out. |
| 1065 | 3 | `private void add(District d, int i, int t, long k)` | Adds k (negative to take away) of type t to the district at index i, keeping its ground and plots used, the running total, the type's range, the cursors and the pyramid. |
| 1070 | 20 | `private void add(District d, int i, int t, long k, boolean sited)` | ...a mine or well on a site (sited) taking its whole plots too: its site is its ground, the land the model gives it. |
| 1092 | 5 | `long usedOf(District d)` | The ground a district's buildings use, in half square feet: each type's count x its land, twice. |
| 1099 | 7 | `long roadOf(District d)` | The road plots a district holds. |
| 1108 | 5 | `long cellsOf(District d)` | The whole plots a district's buildings and roads are drawn on, a mine or well on its site taking its own. |
| 1124 | 98 | `private void allocate(long[] counts)` | THE CANONICAL ALLOCATION (spec-land 2.5): the mines and wells on their sites first (0.7.64), then the city's footprint F, in the whole plots it is drawn on, shared inner first - each district its share of F, min(its r... |

### MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) (lines 1223-1560)

| line | len | member | says |
|---:|---:|---|---|
| 1238 | 7 | **type** `static final class SiteIndex` | Every owned site of the SITED resources: per resource and holding, the districts holding them, nearest first, and how many in each. |
| 1247 | 3 | `int holdingOf(long dx, long dy)` | The holding a plot (dx, dy) from the site lies in: 0 the centre, k the k-th purchase, -1 none - the grid's owner (spec-grid 2.4). |
| 1252 | 17 | `private List<Deposit> fieldsNear(Resource r, double bx0, double by0, double bx1, double by1)` | The fields of a resource that reach into a box of plots: the world's as the city sees them (CityLand.fieldsIn(): its old world's ground's own, 0.7.99), and a converted centre's legacy iron field. |
| 1271 | 4 | `Deposit legacyField()` | A converted centre's legacy iron field, as a field of its sites at its plot (cell -1). |
| 1283 | 9 | `SiteIndex siteIndex()` | The site index: every owned site of the SITED resources by holding and district, built with the map and extended by each purchase (a new holding's sites are those of the fields centred on its ground, wherever they lie). |
| 1298 | 60 | `private void tallySites(SiteIndex ix, int from, double bx0, double by0, double bx1, double by1)` | Adds to the index the sites of holdings `from` on that lie in a box of plots from the site, and lays its lists out again: a new index object, so the walks lay themselves out again too. |
| 1360 | 7 | `public long ownedSites(Resource r)` | The owned sites of a SITED resource on the map. |
| 1373 | 5 | `static int holdingState(double listed, double left)` | A holding's state for a resource from what remains in it: UNWORKED, WORKING or WORKED_OUT (one that listed none, unworked). |
| 1380 | 7 | `int[] holdingStates(Resource r)` | Each holding's state for a resource, the centre first. |
| 1389 | 9 | **type** `private static final class Walk` | One SITED resource's sites in the order mines and wells take them, and how far its type's count has filled them. |
| 1402 | 4 | `private int typeOn(Resource r)` | The type standing on a SITED resource's sites: the first with it as its site, or -1. |
| 1416 | 25 | `private void placeSited(long[] model)` | Puts each SITED resource's mines or wells on its sites (spec-land 2.4, 2.5): the first sites in acquisition order whose holding is not worked out, then the worked-out ones, a holding's districts nearest first; more th... |
| 1443 | 3 | `private void sitedNow()` | The mines and wells stood on their sites (placeSited()), once, before anything reads a district's counts less them: a plan's inputs and its stamp (0.7.88), as the sites' lists do. |
| 1447 | 11 | `private District home()` |  |
| 1459 | 1 | `private static int indexOf(District d)` |  |
| 1462 | 4 | `static int siteSlot(Resource r)` | Where a resource sits among SITED, or -1. |
| 1468 | 13 | `private void clearType(int t, int k)` | Takes every one of type t off the map, its sites' marks with it. |
| 1483 | 24 | `private Walk layWalk(SiteIndex ix, int[] states, int k, int t)` | The walk's order: holdings not worked out in acquisition order, then the worked-out; within each, its districts nearest first. |
| 1509 | 33 | `private void fill(Walk w, int k, long n)` | Fills or empties a walk to n: on sites from where it stood, past the last site in the founding site's district. |
| 1544 | 16 | `private void mark(District d, int k, int t, int holding, int m)` | m more (or fewer) of type t on a district's sites of one holding. |

### THE DRAWN PLANS: EVERY DISTRICT FROM ITS STREET PLAN (0.7.88, batch (lines 1561-2611)

| line | len | member | says |
|---:|---:|---|---|
| 1605 | 83 | **type** `public static final class Drawn` | A district's plan as the painter reads it (0.7.88): each tile's street codes and its buildings' boxes, and the figures a harness reads. |
| 1621 | 1 | `public int joinsOut()` _(in CityMap.Drawn)_ |  |
| 1622 | 1 | `public int partedOut()` _(in CityMap.Drawn)_ |  |
| 1623 | 1 | `public int seamsSurfaced()` _(in CityMap.Drawn)_ |  |
| 1630 | 6 | `long bytes()` _(in CityMap.Drawn)_ | The bytes it holds, about: what the view's budget counts. |
| 1638 | 5 | `public long[] buildingsByType(int types)` _(in CityMap.Drawn)_ | Its plan's buildings, by type id: the boxes counted. |
| 1645 | 6 | `public byte codeAt(int x, int y)` _(in CityMap.Drawn)_ | The street code its plan lays on plot (x, y) of its frame: the district's 256 x 256 and its east column and south row (x or y = DISTRICT); 0 for none. |
| 1653 | 1 | `static byte codeIn(byte[] t, int x, int y)` _(in CityMap.Drawn)_ | A packed tile's code at plot (x, y) of it. |
| 1656 | 3 | `static void unpack(byte[] t, byte[] out)` _(in CityMap.Drawn)_ | A packed tile's codes, a byte a plot row by row, into out. |
| 1661 | 14 | `static byte[] pack(byte[] plots)` _(in CityMap.Drawn)_ | A tile's codes (a byte a plot row by row) packed by row: each row's pattern once, in the order first met. |
| 1677 | 1 | `public double surface(int kind)` _(in CityMap.Drawn)_ | Its surface in plots of kind (BuildingVisual's GRAVEL to HIGHWAY). |
| 1679 | 1 | `public int streets()` _(in CityMap.Drawn)_ | The plots of street it lays, and those of them tracks. |
| 1680 | 1 | `public int tracks()` _(in CityMap.Drawn)_ |  |
| 1682 | 1 | `public int placed()` _(in CityMap.Drawn)_ | Buildings its plan placed, and refused (carried on). |
| 1683 | 1 | `public int refused()` _(in CityMap.Drawn)_ |  |
| 1685 | 1 | `public double surplus()` _(in CityMap.Drawn)_ | Road it has no street for, in plots; of a kind. |
| 1686 | 1 | `public double leftover(int kind)` _(in CityMap.Drawn)_ |  |
| 1690 | 8 | `static byte paintCode(short code)` | A plan's street code as the painter reads it (TilePainter's S_ codes): DistrictPlan's kind and width, its role - an arterial, a boulevard's, or a street (a cell's, along a cut, the join's) - and a bridge. |
| 1700 | 53 | `static Drawn drawnOf(DistrictPlan p, long stamp)` | A plan packed for the painter, with its stamp. |
| 1755 | 3 | `static int box(int x, int y, int w, int h, boolean packed)` | A box packed into an int: x, y, w - 1 and h - 1 at five bits each, PACKED_BIT when packed without a street. |
| 1760 | 17 | **type** `static final class Link` | A district's link in the chain (R7): the stamp of its own inputs, what was carried to it and what it carried on (by type id, null for none); for an edge district, its plan's free ground (a bit a plot of the district),... |
| 1770 | 4 | `Link(long own, int[] in, int[] out, long[] free, byte[][] margins, District[] earlier, long[] read, double surplus, long serial)` _(in CityMap.Link)_ |  |
| 1775 | 1 | `long stamp()` _(in CityMap.Link)_ | Its plan's stamp: its own inputs and what came in. |
| 1779 | 5 | `static long stampOf(long own, int[] in)` | A plan's stamp: its own inputs' and the carry in's. |
| 1789 | 16 | `static byte[][] marginsOf(DistrictPlan p)` | A plan's frame edges' street codes (M_WEST to M_SOUTH) and which of them are parted (M_PARTED). |
| 1813 | 8 | `District[] earlierOf(District d)` | The districts about d (AROUND's order) that come before it in the map's order, in its band (0.7.89; CHAIN_BAND), null for the rest: whose plans its plan reads - which of its seams they leave unlaid, and where its stre... |
| 1823 | 4 | `static byte marginAt(byte[][] m, int x, int y, int ox, int oy)` | Where district d's frame plot (x, y) lies in the frame of its neighbour at offset (ox, oy), read from that plan's margins - a plot of its frame's edge - or 0 when it is not on them. |
| 1829 | 4 | `static boolean partedAt(byte[][] m, int x, int y, int ox, int oy)` | ...whether the street there is parted from the network (M_PARTED); false where it is not on them. |
| 1835 | 9 | `private static int marginOf(int x, int y, int ox, int oy)` | ...which margin and place: the margin (M_WEST to M_SOUTH) << 16 \| the index along it, or -1. |
| 1857 | 46 | `static void readNeighbours(District d, District[] earlier, byte[][][] margins, DistrictPlan.Input in)` | What district d's plan reads of the plans before it (0.7.88), from their frames' edges (margins, AROUND's order; null for a district after it or none): ONE-SIDED SEAMS, its seams no district before it sharing them lay... |
| 1904 | 3 | `private static void open(DistrictPlan.Input in, int x, int y, byte code)` |  |
| 1909 | 5 | **type** `static final class Pack` | What the city has no room for, packed at its edge (R7): each edge district's packed boxes by tile, an int each as Drawn.boxes; how many were packed, how many drawn smaller than their own land, and how many found no gr... |
| 1943 | 1 | `static int band(int i)` | The band of the district at index i in the map's order. |
| 1946 | 1 | `int bands()` | Bands in the map's order. |
| 1960 | 3 | `private long planEpoch()` | What the plans hang on, cheaply: the map's changes, and the land's purchases (a purchase owns ground at once, before the month measures it; the land drawn again draws a new map). |
| 1965 | 4 | `boolean isEdge(District d)` | Whether district d is at the city's edge: a neighbour of its eight is not on the map - where what the city has no room for is packed (R7). |
| 1979 | 31 | `long ownStamp(District d)` | A stamp of everything district d's own plan is drawn from but what is carried to it: its buildings and road by type (its mines and wells on sites apart), the ground the city owns over its frame (ownership only grows, ... |
| 2012 | 9 | `private void sizeBands()` | The bands' arrays sized to the districts, every band to be checked again. |
| 2023 | 4 | `private void bandsStale()` | Every band to be checked again (a plan adopted). |
| 2029 | 22 | `private void checkBand(int b)` | Band b's links walked in the map's order at the current epoch: its bandValidTo is the first whose own stamp moved, whose carry in is not the one before's carry on (none for the band's first), or (an edge district) tha... |
| 2053 | 8 | `private void useBand(int b)` | Marks band b used, letting go of the bands used longest ago past BANDS_KEPT. |
| 2063 | 11 | `private void forgetBand(int b)` | Lets go of band b's links, plans and packing: drawn again when a screen asks for it. |
| 2076 | 8 | `private boolean allDrawn()` | Whether every band's links hold and its packing is current: the whole city drawn. |
| 2086 | 8 | `private int[][] packedIn(District d)` | District d's band's packing of what the band cannot hold, by tile, when current; null for none. |
| 2096 | 10 | `private boolean readHolds(Link l)` | Whether the plans a link read its neighbours' edges from are those standing: each such district's link the one it read (they are before it in the map's order, walked first). |
| 2108 | 11 | `public boolean planned(District d)` | Whether district d's plan is drawn and current, and every link before it in its band holds; for an edge district, its band's packing too: its tiles paint without planning. |
| 2121 | 16 | **type** `public abstract static class Job` | A job to run away from the screen's thread (0.7.88): run() on any one thread, in the order handed out, then adopt() on the map's. |
| 2126 | 1 | `Job(CityMap map, long epoch)` _(in CityMap.Job)_ |  |
| 2128 | 5 | `public final synchronized void run()` _(in CityMap.Job)_ | Does the work: any thread, the jobs handed out in their order on one; safe to call twice. |
| 2133 | 1 | `abstract void work()` _(in CityMap.Job)_ |  |
| 2135 | 1 | `public final synchronized boolean done()` _(in CityMap.Job)_ | Whether it has run. |
| 2139 | 56 | **type** `public static final class PlanJob extends Job` | One district's plan: its inputs gathered on the map's thread, its ground read and its plan drawn by run(), with what the job before it carried on (R7). |
| 2155 | 6 | `PlanJob(CityMap map, District d, DistrictPlan.Input in, long own, PlanJob after, int[] given, boolean edge, District[] earlier,...` _(in CityMap.PlanJob)_ |  |
| 2162 | 29 | `void work()` _(in CityMap.PlanJob)_ |  |
| 2193 | 1 | `public double ms()` _(in CityMap.PlanJob)_ | How long it took, in ms: a harness's figure. |
| 2197 | 41 | **type** `public static final class PackJob extends Job` | A band's packing at its edge (R7; the city's, in a city of one band): what its last link carried on, into its edge districts' free ground - their links', or the jobs' that make them. |
| 2207 | 4 | `PackJob(CityMap map, District[] edges, Object[] from, Object last, long[] tileOrder, long epoch)` _(in CityMap.PackJob)_ |  |
| 2212 | 25 | `void work()` _(in CityMap.PackJob)_ |  |
| 2247 | 8 | `public List<Job> planJobs(District d)` | The jobs that make district d's plan current (R7: every district before it in the map's order whose link does not hold, each after the one before it), and, for an edge district, the city's packing after them: to run i... |
| 2257 | 31 | `private PlanJob chainTo(int i, List<Job> out)` | The plan jobs up to the district at index i from its band's first, into out; the last job of the chain to it (handed out now or before), or null when its link holds. |
| 2290 | 10 | `private Object[] sources(District[] earlier)` | Each earlier district's plan as a job reads it: its job handed out at this epoch, else its link (it is before the job's district in the map's order, so one or the other holds). |
| 2302 | 24 | `private void packAfter(int b, List<Job> out)` | Band b's packing at its edge, after the chain to its last district, into out - unless it is current or handed out at this epoch. |
| 2328 | 21 | `private long[] tileOrder(List<District> edges)` | The edge districts' tiles in the order the packing spreads over them: each a district's index in `edges` and the tile, the farthest tile from the founding site first. |
| 2351 | 21 | `public boolean adopt(Job j)` | Keeps a job's work: a plan's link and its packed plan, the city's packing. |
| 2374 | 13 | `Drawn drawnOf(District d)` | District d's plan for the painter, made here when it is not current - every district before it in the map's order first where its link does not hold, and for an edge district the city's packing - on this thread: a har... |
| 2389 | 7 | `void packed()` | Every band's packing at its edge, made here when it is not current (a harness's): the whole city drawn. |
| 2398 | 11 | `private int[] packSums()` | The bands' packings summed: {packed, smaller, none}; null when one is not current. |
| 2411 | 5 | `public int[] packedCounts()` | How many buildings the city has no room for in any district's plan, packed at its edge (R7; since 0.7.89 at its band's edge) - and how many of them drawn smaller than their own land or with no ground at all: {packed, ... |
| 2418 | 3 | `public int[] packedCountsIfDrawn()` | ...the same, or null when the packing is not current: the screen's thread, which never plans. |
| 2423 | 7 | `public long[] legendFiguresIfDrawn()` | The legend's figures once every plan and the packing are drawn (never planned here: the screen's thread): {buildings packed at the city's edge, plots of road its streets do not carry (star RD2-4) - since 0.7.89 with t... |
| 2432 | 10 | `public double[] leftoverByKind()` | The road the plans have no street for, by kind [0, gravel, paved, highway], over every district (made now if need be), and (0.7.89) the Elevated Highway plots the runs could not lay: a harness's - with the surface dra... |
| 2444 | 4 | `public long[] legendFigures()` | ...the same, made now if need be: a harness's. |
| 2450 | 1 | `public Drawn drawn(District d)` | District d's plan as drawn (made now if need be): a harness's and a probe's. |
| 2453 | 5 | `public int[][] carried(District d)` | What was carried to district d and what it carried on (R7), by type id, null for none - its link's, once planned. |
| 2460 | 1 | `int linksKept()` | The links kept (0.7.89: a band's let go past BANDS_KEPT): a harness's. |
| 2463 | 5 | `public long plansBytes()` | The bytes the kept plans hold: what MapTiles' budget counts. |
| 2475 | 26 | `static long[] freeGround(DistrictPlan.Input in, DistrictPlan p)` | A plan's free ground (R7), for an edge district: a bit a plot of the district's 256 x 256, set where the city owns dry ground no street, building, highway or its verge (H5), track or mine's site takes - a field's plot... |
| 2516 | 55 | `static Pack packAtEdge(int[] carry, BuildingVisual.Type[] types, District[] edges, long[][] free, long[] tileOrder)` | PACKED AT THE CITY'S EDGE (R7; spec 2.6: "a city with no room anywhere packs them at its edge, without a street, and the map's legend says how many"). |
| 2576 | 35 | `private static int[] fitTile(long[][] free, long key, int w, int h, int[][] failW, int[][] failH, int[] fails, int ti)` | The first spot, in rows, on tile key (edge index << 32 \| tile) of the free ground where a w x h box - or h x w - fits inside the tile's interior: {edge index, tile, x, y, w, h} in the tile's plots, or null (and the sh... |

### THE RUNS: THE CITY'S HIGHWAYS AND RAILWAY ON CORRIDORS (0.7.89, batch (lines 2612-2838)

| line | len | member | says |
|---:|---:|---|---|
| 2640 | 1 | `public CityRuns runs()` | The city's runs. |
| 2655 | 87 | **type** `private final class RunGround implements CityRuns.Ground` | The ground the runs read (CityRuns.Ground): a plot's World class where the city owns it, -1 where it does not - the terrain a tile's row or column at a time (World.lineTerrain(), byte for byte the painter's tileTerrai... |
| 2671 | 35 | `public int at(long x, long y, int dir)` _(in CityMap.RunGround)_ |  |
| 2708 | 23 | `private boolean ownedAt(long tx, long ty, int ix, int iy)` _(in CityMap.RunGround)_ | Whether the city owns plot (ix, iy) of tile (tx, ty): the grid's cover of the tile, its leaves where it owns some. |
| 2732 | 3 | `public long version()` _(in CityMap.RunGround)_ |  |
| 2736 | 5 | `public long[] box()` _(in CityMap.RunGround)_ |  |
| 2746 | 1 | `CityRuns.Ground runGround()` | The ground the runs read: a harness's, to lay runs of its own on the city's ground. |
| 2749 | 1 | `long[] site()` | The founding site, {x, y} in plots. |
| 2758 | 30 | `boolean layRuns(long[] model)` | Lays the runs to the model's counts (CityRuns.layTo()): its Elevated Highways' plots, its track's, its Rail Terminals as yards - the highways from the founding site, the railway from the city's mine nearest it (the fo... |
| 2790 | 12 | `List<long[]> minedSites()` | The city's mines and wells standing on their sites, each {x0, y0, x1, y1} inclusive: where the railway starts and its yards are drawn near. |
| 2808 | 17 | `int yardsOf(District d)` | How many of district d's Rail Terminals are the runs' yards. |
| 2827 | 11 | `long runsInto(District d, byte[] fixed)` | The runs' frame for district d's plan: their plots over its 257 x 257 (DistrictPlan's codes), a hash of them returned. |

### THE SHORE: TERMINALS AND TANK FARMS AT THE WATER (0.7.97, batch O13; (lines 2839-3075)

| line | len | member | says |
|---:|---:|---|---|
| 2859 | 1 | `public CityShore shore()` | The city's works on its shore (0.7.97). |
| 2864 | 11 | `int[] shoreTypes()` |  |
| 2883 | 1 | `public int shoreShort()` | How many of the model's terminals and tank farms the shore has no place for: their districts' plans draw them. |
| 2886 | 3 | `private long groundKey()` | The ground's key, without the shore's own works: what a search that came up short waits on. |
| 2895 | 41 | `boolean layShore(long[] model)` | Lays the shore to the model's counts: one fewer of a type takes its newest work; one more is laid where shoreSpot() finds it, the first types first. |
| 2937 | 4 | `private static boolean anyOf(long[] model, int[] ids)` |  |
| 2951 | 68 | `CityShore.Work shoreSpot(int t, List<long[]> mines)` | Where the next work of type t goes (CityShore's rule), or null: the districts in the map's order, each one's tiles nearest the founding site first; on each tile with owned salt water and owned dry ground, its shore pl... |
| 3021 | 20 | `private static boolean shoreFits(CityShore.Work c, int[] ground, byte[] fixed, long wx0, long wy0, int ww)` | Whether a work fits where c puts it, on the window's ground and runs from (wx0, wy0), ww a side: its box in one cell's interior on owned dry ground no run, mine or other work takes, no highway beside it, and its quay ... |
| 3043 | 8 | `static boolean nearRing(CityShore.Work c)` | Whether a work's box lies within TilePainter.REACH of its cell's ring on a side away from the sea: where an arterial runs when the cell is open. |
| 3057 | 18 | `int shoreOf(District d, int t)` | How many of district d's buildings of type t the shore holds. |

### THE OIL AT SEA (0.7.97, batch O13; runs/spec-oil.md 2.12, the (lines 3076-3139)

| line | len | member | says |
|---:|---:|---|---|
| 3089 | 1 | **type** `public record Jacket(double x, double y, int wells, List<Long> wellPlots)` | A platform as the map draws it: its jacket's middle in plots, its wells, and the plots its wells stand on (each x << 32 \| y). |
| 3092 | 1 | **type** `public record Pipe(double x0, double y0, double x1, double y1)` | A pipe as the map draws it: from its field's middle (x0, y0) toward the founding site, to (x1, y1), in plots - its kilometres standing. |
| 3095 | 4 | **type** `public record AtSea(List<Jacket> jackets, List<Pipe> pipes)` | The oil at sea: its jackets and pipes. |
| 3103 | 1 | `public AtSea atSea()` | The oil at sea, as the game last handed it. |
| 3106 | 6 | `public void atSea(AtSea a)` | The oil at sea handed over by the game (Game.mapAtSea()); a change restamps the tiles it crosses. |
| 3114 | 25 | `private void atSeaInto(long px0, long py0, TilePainter.Input in)` | The oil at sea over tile (px0, py0)'s plots, into its inputs: the jackets' and wells' plots, the rings and pipes that may cross it. |

### THE SEA ROUTES (0.7.97, batch O13; spec-roads-and-ports.md 4.1) (lines 3140-3221)

| line | len | member | says |
|---:|---:|---|---|
| 3154 | 6 | `double cityRadius()` | The city's radius for the offing, in plots: the farthest corner of its owned box from the founding site. |
| 3162 | 5 | `private long routesKeyNow()` | The key the routes are found against: the berths, the founding site and the city's radius. |
| 3169 | 8 | `public List<BoatSchedule.Route> seaRoutes()` | The routes now, found here when they are not (a harness's way; the view hands a RoutesJob to its worker). |
| 3179 | 3 | `public List<BoatSchedule.Route> seaRoutesIfFound()` | The routes when they are found and current, else null: the screen's thread, which never finds them itself. |
| 3184 | 22 | **type** `public static final class RoutesJob implements Runnable` | The berths' routes found away from the screen's thread: what they are found from, copied here. |
| 3190 | 8 | `RoutesJob(long key, long seed, List<BoatSchedule.Berth> berths, long siteX, long siteY, double radius)` _(in CityMap.RoutesJob)_ |  |
| 3199 | 3 | `public void run()` _(in CityMap.RoutesJob)_ |  |
| 3204 | 1 | `public boolean done()` _(in CityMap.RoutesJob)_ | Whether it has run. |
| 3208 | 5 | `public RoutesJob routesJob()` | The job that finds the routes now, or null when they are current. |
| 3215 | 6 | `public boolean adoptRoutes(RoutesJob j)` | A job run: its routes kept when they are still the city's. |

### A TILE'S INPUTS FOR THE PAINTER (lines 3222-3521)

| line | len | member | says |
|---:|---:|---|---|
| 3227 | 3 | `public District districtOfTile(long tx, long ty)` | The district holding tile (tx, ty), or null. |
| 3232 | 3 | `static int tileIn(long tx, long ty)` | The tile in district d at its column and row of tiles (tile = row x 8 + column), from world tile (tx, ty). |
| 3237 | 10 | `public void tileCounts(long tx, long ty, int[] out)` | A tile's buildings by type id as its district's plan draws them (0.7.88; the deal's until 0.7.87), packed ones among them: into out, zeros where no district is. |
| 3249 | 8 | `public void tileOwnership(long tx, long ty, boolean[] out)` | Which of a tile's plots the city owns: a tile owned wholly or not at all in one test (the grid's cover at level 5), the rest from the grid's leaves under it (spec-grid 2.4). |
| 3268 | 4 | `public void tileInput(long tx, long ty, TilePainter.Input in)` | Everything the painter needs for tile (tx, ty), into in: its ground, what the city owns of it, and from its district's plan (0.7.88; the deal's counts and road plots until 0.7.87) the streets through it and every buil... |
| 3274 | 4 | `public void tileInput(long tx, long ty, TilePainter.Input in, byte[] terrain)` | ...with the tile's ground given (a view keeps it: MapTiles.terrain(), batch J4) rather than read from the world. |
| 3280 | 10 | `public List<District> tileDistricts(long tx, long ty)` | The districts a tile's picture is drawn from (0.7.88): those of the tiles about it - its own, the ones whose plans lay the street on its first column or row (west, north, north-west) and those its edges look across to... |
| 3292 | 4 | `public boolean tileReady(long tx, long ty)` | Whether tile (tx, ty) paints without planning: every plan it is drawn from current (planned()). |
| 3298 | 5 | `public List<Job> tileJobs(long tx, long ty)` | The jobs that make tile (tx, ty)'s plans current (planJobs()), in order; empty when they are. |
| 3312 | 3 | `byte streetAt(long x, long y)` | The street a plan draws on world plot (x, y) (TilePainter's S_ codes): the district's own; on its first column or row the district west or north of it (or north-west, at its corner) lays the same plot as its frame's m... |
| 3317 | 32 | `private byte streetAt(long x, long y, Map<Long, Drawn> seen)` | ...the plans looked up through `seen`, by district key (null for none), which it fills. |
| 3351 | 92 | `private void fillInput(long tx, long ty, TilePainter.Input in)` | Everything tileInput() fills but the ground, which is in `in` already. |
| 3445 | 1 | **type** `record DrawnSite(long x0, long y0, long x1, long y1, int kind, int state, int mine)` | One site as the map draws it: its square of plots (inclusive), resource, state and the mine on it (-1 for none). |
| 3459 | 49 | `List<DrawnSite> siteList(int dx, int dy)` | The sites whose centres lie in district (dx, dy) - every resource's, owned or not - each with its holding's state (its field's holding, the one holding the field's centre, since 0.7.64) and, for the SITED resources, t... |
| 3510 | 11 | `public void forgetPainted()` | Forgets the painted state of every district - the plans, their links, the packing and the site lists - after the ground's states moved (what is worked out). |

### THE DISTRICT PLAN'S INPUTS (0.7.87, batch RD1; the project's (lines 3522-3654)

| line | len | member | says |
|---:|---:|---|---|
| 3541 | 3 | `DistrictPlan plan(District d)` | District d's own plan, drawn now from planInput(d) - its own buildings alone, none carried to it (R7): not kept (THE DRAWN PLANS keep the painter's). |
| 3546 | 5 | `DistrictPlan.Input planInput(District d)` | The inputs of district d's plan (DistrictPlan.Input), its ground read from the world. |
| 3553 | 15 | `static void readGround(DistrictPlan.Input in)` | The ground over a plan's frame, a World class a plot, read from the world: any thread (World is safe), so a plan's job reads it away from the screen's (0.7.88). |
| 3570 | 84 | `DistrictPlan.Input planInputHere(District d)` | ...everything but the ground: what the map's own thread reads (the land, the counts, the sites, the runs). |

### THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 (lines 3655-3786)

| line | len | member | says |
|---:|---:|---|---|
| 3674 | 112 | **type** `public static final class Pyramid` | The districts summed two by two, level by level, to one node - level 0 is the districts themselves, each level above a grid of nodes over the districts' box, a node NODE_WIDTH numbers. |
| 3682 | 45 | `Pyramid(CityMap map)` _(in CityMap.Pyramid)_ |  |
| 3729 | 11 | `long[] vector(District d)` _(in CityMap.Pyramid)_ | A district's numbers: its buildings by class, its ground used (half square feet) and its owned dry plots. |
| 3742 | 15 | `void add(District d, int t, long k)` _(in CityMap.Pyramid)_ | k more (or fewer) of type t in a district: its ancestors take them. |
| 3759 | 1 | `public int levels()` _(in CityMap.Pyramid)_ | How many levels above the districts. |
| 3762 | 1 | `public int nodes()` _(in CityMap.Pyramid)_ | How many nodes above the districts hold anything (and the top). |
| 3765 | 11 | `public long[] root()` _(in CityMap.Pyramid)_ | The top node: the whole city's numbers. |
| 3778 | 7 | `public long[] node(int level, int dx, int dy)` _(in CityMap.Pyramid)_ | The node at a level (1 up) covering district (dx, dy), or null. |

### THE SIDECAR (spec-land 2.5) (lines 3787-3992)

| line | len | member | says |
|---:|---:|---|---|
| 3804 | 3 | `public byte[] writeSidecar(int month)` | The map as its sidecar's bytes, deflated: the header (magic, format, seed, month, stamp, the founding site, the stamp of the land's rectangles it was drawn on (CityLand.stamp(), 0.7.67) and its purchases, the type cou... |
| 3809 | 3 | `byte[] writeSidecar(int month, int format)` | ...in a given format, FORMAT or OLDEST_READ (a harness writes the older one to read it back). |
| 3814 | 49 | `byte[] writeSidecar(int month, int format, boolean withRuns)` | ...and with its runs or (a harness's, as 0.7.88 wrote FORMAT 5) a count of none. |
| 3867 | 1 | `public long lastStamp()` | The stamp of the sidecar last written: what the save carries as mapStamp. |
| 3870 | 13 | `static long stampOf(byte[] raw)` | The stamp of a sidecar's raw bytes: SplitMix64 folded over them, the stamp's own eight bytes read as zero. |
| 3885 | 7 | `public static long stampIn(byte[] deflated)` | The stamp a sidecar carries, or 0 when it is not one. |
| 3893 | 7 | `private static byte[] inflate(byte[] deflated)` |  |
| 3908 | 69 | `public static CityMap readSidecar(byte[] deflated, CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[]...` | A map read back from its sidecar, or null when it is not one, is not this city's (another seed, month or stamp, or land drawn otherwise), or does not add up. |
| 3979 | 3 | `public boolean same(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order, and (0.7.89) the same runs, and (0.7.97) the same works on the shore. |
| 3984 | 8 | `public boolean sameDistricts(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order. |

