# CityMap.java - 1,905 lines · 101 methods · 21 constants · model

`ham/citybuildersim/CityMap.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, dealt out over each district's tiles with its roads, one drawn building for each the model has, summed up a pyramid for the far view, and written beside the save as a sidecar - what the tile painter paints the city from.
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
> THE DEAL (deal(), 0.7.64) puts each of a district's buildings and road
> plots on one of its own 64 tiles, never more than a tile has free plots
> for: the roads first, ROAD_PIECE plots at a time, then the buildings
> largest first (BuildingVisual.rankedTypes()), each where its own hash
> says through a table weighted by the tile's free plots - leaning toward
> the founding site for what follows people, away from it for farms,
> utilities, mines, the railway and the car plants - and, its tile full,
> the nearest tile with room (the farthest for the outer kinds). One more
> building changes one tile by one while its tile has room. The painter
> then draws a tile's buildings one for one on their own land and grows
> its road plots exactly: a city is drawn as what it has, not more, not
> less (Jerus, 2026-10-07; J3b's homes from people and workplaces from
> jobs, and the mockup's two world highways, are gone).
> 
> THE PYRAMID sums ten classes, used plots and owned plots two by two up to
> one node, built when the map is, each district's ancestors updated as it
> changes: what the far view draws (batch J4).
> 
> THE SIDECAR (slot-NN-map.bin, writeSidecar()/readSidecar()): one record a
> district, deflated, its header the magic, the format, the world's seed, the
> month, a stamp of the content (also the save's mapStamp), the land it was
> drawn on and the type count. A map saved and read back writes the same
> bytes.
> 
> ... (8 more lines in the source)

**Uses:** [World](World.md) (49), [BuildingVisual](BuildingVisual.md) (34), [Resource](Resource.md) (26), [TilePainter](TilePainter.md) (18), [CityLand](CityLand.md) (14), [Deposit](Deposit.md) (13), [LandGrid](LandGrid.md) (4), [LandParcel](LandParcel.md) (1)

**Used by (6):** [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MapCheck](MapCheck.md), [MapTiles](MapTiles.md), [MapView](MapView.md), [ReadPathCheck](ReadPathCheck.md)

## Sections

| line | section |
|---:|---|
| 92 | THE GEOMETRY |
| 132 | A DISTRICT |
| 198 | THE STATE |
| 267 | BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE |
| 369 | MEASURING THE GROUND: OWNED DRY PLOTS |
| 572 | THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN |
| 620 | PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION |
| 874 | MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) |
| 1208 | THE DEAL: EACH DISTRICT ONTO ITS OWN TILES, AS FULL AS THEY HOLD |
| 1450 | A TILE'S INPUTS FOR THE PAINTER |
| 1617 | THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 |
| 1749 | THE SIDECAR (spec-land 2.5) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 97 | `CityMap.DISTRICT` | `World.DISTRICT` | Plots on a district's side: World.DISTRICT, 256 (7.68 km). |
| 100 | `CityMap.TILES_A_SIDE` | `DISTRICT / World.TILE` | Tiles on a district's side: 8. |
| 103 | `CityMap.TILES` | `TILES_A_SIDE * TILES_A_SIDE` | Tiles in a district: 64. |
| 106 | `CityMap.HALF_SQ_FT_PER_PLOT` | `Math.round(2 * BuildingVisual.SQ_FT_PER_PLOT)` | Half square feet in a plot: 9,687.5 sq ft twice, so a district's room and use are whole numbers and a month's change is exact however it is added up. |
| 109 | `CityMap.MIN_ROOM` | `1` | A district with less room than this many whole plots is passed by the cursors: one, the least a building is drawn on (0.7.64; half a plot of ground before). |
| 112 | `CityMap.SITED` | `{ Resource.IRON, Resource.OIL }` | The resources a district counts its owned sites of, and mines and wells stand on: iron and oil (spec-land 2.5). |
| 115 | `CityMap.SITE_LISTS_KEPT` | `64` | How many districts' lists of sites are kept: 64. |
| 118 | `CityMap.COARSE_ABOVE` | `1024` | Above this many districts under the land's box, a canonical build looks at the ground coarsely first and measures only districts with land in or beside them: 1,024 (a box 246 km across). |
| 121 | `CityMap.COARSE_STRIDE` | `32` | The coarse look's stride, in plots: 32, a sample a tile. |
| 124 | `CityMap.MAGIC` | `0x434D4150` | The sidecar's magic: "CMAP". |
| 127 | `CityMap.FORMAT` | `3` | The sidecar's format: 3 since 0.7.67, its land stamp the holdings' rectangles (CityLand.stamp()) where a centre's half-side was; 2 from 0.7.64, when a district's room became its free plots counted plot by plot. |
| 130 | `CityMap.STAMP_AT` | `4 + 4 + 8 + 4` | Where the stamp sits in the sidecar's raw bytes: after the magic, the format, the seed and the month. |
| 432 | `CityMap.NONE` | `0, SOME = 1, ALL = 2` |  |
| 1021 | `CityMap.DISTRICT_ORDER` | `Comparator.comparingDouble((District d) -> d.order).thenComparingInt(d -> d.d...` | Nearest the founding site first, then north to south, west to east. |
| 1229 | `CityMap.CORE_BOOST` | `6` | How much more of what follows people a tile at the founding site takes, before its district's share is shared out: 6 times more (J3b's star) - its middle a town's size... |
| 1232 | `CityMap.CORE_RADIUS` | `80` | ...falling off over this many plots: 80 (2.4 km, J3b's star). |
| 1235 | `CityMap.ROAD_PIECE` | `TilePainter.STEP_PAVED + TilePainter.STEP_SPAN` | A district's road is dealt to its tiles this many plots at a time: 8, the longest step a road grows (TilePainter.STEP_PAVED + STEP_SPAN) - so a tile's road is at least a run, and an Elevated Highway's 7 plots are one ... |
| 1238 | `CityMap.TILE_COUNTS_KEPT` | `64` | How many districts' deals are kept: 64 - about 1.3 MB, a screen's and its neighbours' many times over. |
| 1622 | `CityMap.NODE_WIDTH` | `BuildingVisual.CLASSES + 2` | How many numbers a node sums: the ten classes, the ground used (in half square feet, exact) and the owned dry plots. |
| 1625 | `CityMap.NODE_USED` | `BuildingVisual.CLASSES` | Where a node keeps the ground used, in half square feet. |
| 1628 | `CityMap.NODE_OWNED` | `BuildingVisual.CLASSES + 1` | ...and its owned dry plots. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 139 | `public final int dx, dy` | Its column and row, in districts from the founding site's. |
| 140 | `final long key` |  |
| 141 | `final double order` |  |
| 143 | `int owned` | Its free plots: owned and dry, counted plot by plot (0.7.64; from 32 x 32 samples before) - or, for a harness's square city, given. |
| 145 | `long usedHalf` | The ground its buildings use, in half square feet: each type's count x its land, kept exactly. |
| 147 | `long usedCells` | The whole plots its buildings and roads are drawn on (BuildingVisual.cells()), a mine or well on its site taking its own: what its room is kept in (0.7.64). |
| 149 | `int inner` | Its free plots inside its tiles' edge rings, where alone a road is laid, and the road plots it holds: its room for roads (0.7.64). |
| 150 | `long usedRoad` |  |
| 152 | `final int[] counts` | Its count of every type, by id. |
| 154 | `final int[] sites` | Its owned sites of each SITED resource. |
| 156 | `short[] tileFree` | Each tile's free plots, row by row: owned and dry; null until measured or asked for. |
| 158 | `short[] tileInner` | ...and those inside its edge ring, where alone the painter lays a road. |
| 160 | `long[][] tileSiteKey` | Each tile's sites, by key (TilePainter.Input.siteKey), and their free plots on it, all and inside the edge ring: what a mine standing on one takes of the tile. |
| 161 | `short[][] tileSiteFree, tileSiteInner` |  |
| 163 | `int[] sited` | How many of each site-bound type stand on its sites (the rest of its count has none); null when none. |
| 165 | `int[][] holdingMines` | For painting: per SITED resource, pairs of {holding, mines} - how many of each holding's sites here carry one. |
| 167 | `long version` | Bumped whenever its counts change: what its deal is cached against. |
| 169 | `int index` | Its place in the map's order, nearest first. |
| 202 | `private final long seed` |  |
| 203 | `private final long siteX, siteY` |  |
| 204 | `private final long baseDX, baseDY` |  |
| 205 | `private final BuildingVisual.Type[] types` |  |
| 206 | `private CityLand land` |  |
| 207 | `private Function<Resource, double[]> remaining` |  |
| 209 | `private final List<District> districts` |  |
| 210 | `private final Map<Long, District> byKey` |  |
| 211 | `private final long[] have` |  |
| 212 | `private final int[] lo, hi` |  |
| 213 | `private int innerCursor, outerCursor` |  |
| 214 | `private int purchasesSeen` |  |
| 215 | `private long centreSeen` |  |
| 216 | `private boolean measured` |  |
| 217 | `private Pyramid pyramid` |  |
| 218 | `private SiteIndex siteIndex` |  |
| 219 | `private boolean sitedPlaced` |  |
| 659 | `private long changes` | How many changes the map has taken since it was drawn - a district's count, a purchase measured, a site's state: what the view stamps its tiles again on (MapTiles). |
| 678 | `private int[][] lastStates` | Each resource's holdings' states at the last month: when they move, the sites are drawn again. |
| 681 | `int lastTouched` | How many district-types the last month's change touched. |
| 891 | `District[][][] where` | [resource][holding]: the districts, and the sites in each. |
| 892 | `int[][][] how` |  |
| 894 | `final List<List<Map<District, int[]>>> tally` | The tallies they are read from: [resource] then holding, district to sites. |
| 1042 | `SiteIndex ix` |  |
| 1043 | `int[] states` |  |
| 1044 | `int type` |  |
| 1045 | `District[] at` |  |
| 1046 | `int[] holding` |  |
| 1047 | `int length, pos, partial` |  |
| 1048 | `long placed, overflow` |  |
| 1051 | `private final Walk[] walks` |  |
| 1241 | `private long landVersion` | Bumped whenever free ground is measured again: what the deals are kept against. |
| 1245 | `long stamp` |  |
| 1246 | `int[][] counts` |  |
| 1247 | `int[][] roads` |  |
| 1249 | `int overDealt` | Plots of each tile the deal charged and found none for: buildings with no plot at all, roads unlaid (0 while the district has room). |
| 1252 | `private final Map<District, Dealt> dealt` |  |
| 1548 | `private final Map<Long, List<DrawnSite>> siteLists` |  |
| 1551 | `private final Map<Long, Long> siteListVersion` |  |
| 1637 | `private final CityMap map` |  |
| 1638 | `private final int minX, minY` |  |
| 1640 | `private final int[] widths, heights` | Each level's grid: its width, height and nodes (NODE_WIDTH numbers each, row by row). |
| 1641 | `private final long[][] nodes` |  |
| 1642 | `private final int count` |  |
| 1805 | `private long lastStamp` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 90 | 1816 | **type** `public final class CityMap` | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves, dealt out over each district's tiles with its roads, one drawn buildi... |

### THE GEOMETRY (lines 92-131)

### A DISTRICT (lines 132-197)

| line | len | member | says |
|---:|---:|---|---|
| 137 | 58 | **type** `public static final class District` | One district: where it is, its free plots, the ground and the plots used, its owned sites, its counts by type. |
| 171 | 7 | `District(int dx, int dy, int types, double order)` _(in CityMap.District)_ |  |
| 180 | 1 | `public int owned()` _(in CityMap.District)_ | Its free plots (owned and dry, its sites' plots among them). |
| 182 | 1 | `public double used()` _(in CityMap.District)_ | The plots of ground its buildings use: each type's count x its land in plots. |
| 184 | 1 | `public long usedCells()` _(in CityMap.District)_ | The whole plots its buildings and roads are drawn on. |
| 185 | 1 | `public int count(int t)` _(in CityMap.District)_ |  |
| 186 | 4 | `public int sites(Resource r)` _(in CityMap.District)_ |  |
| 191 | 1 | `long freeCells()` _(in CityMap.District)_ | Its room in whole plots, never below nothing (0.7.64; in ground, half square feet, before). |
| 193 | 1 | `long freeRoad()` _(in CityMap.District)_ | ...and for roads: its whole room, no more than its plots inside the edge rings less its road. |
| 196 | 1 | `static long key(int dx, int dy)` |  |

### THE STATE (lines 198-266)

| line | len | member | says |
|---:|---:|---|---|
| 221 | 11 | `private CityMap(long seed, long siteX, long siteY, BuildingVisual.Type[] types)` |  |
| 234 | 1 | `public long seed()` | The world's seed. |
| 237 | 1 | `public BuildingVisual.Type[] types()` | The types it counts, by id. |
| 240 | 1 | `public List<District> districts()` | Its districts, nearest the founding site first. |
| 243 | 1 | `public District district(int dx, int dy)` | The district at (dx, dy) from the founding site's, or null. |
| 246 | 1 | `public Pyramid pyramid()` | The pyramid. |
| 249 | 1 | `public boolean measured()` | Whether its districts were measured on the ground (false for a harness's square city, whose capacities were given). |
| 252 | 5 | `public long[] totals()` | Every type's count over all districts. |
| 259 | 1 | `public CityLand land()` | The land it was drawn on. |
| 262 | 1 | `long baseDX()` | The founding site's district's column, absolute. |
| 265 | 1 | `long baseDY()` | ...and row. |

### BUILDING ONE: CANONICALLY, FROM A SIDECAR, OR A HARNESS'S SQUARE (lines 267-368)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 9 | `public static CityMap canonical(CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[] types, long[] counts)` | A map drawn canonically (spec-land 2.5): the land's districts measured, then each type in proportion in every district, inner first, the remainders by largest remainder; mines and wells on their sites. |
| 301 | 28 | `static CityMap square(long seed, long siteX, long siteY, BuildingVisual.Type[] types, int side, int capacity, long[] counts)` | A HARNESS'S SQUARE CITY (the design's scale case): side x side districts round the founding site, every one owned and holding `capacity` plots - a city of any size at one density without measuring its ground; a distri... |
| 330 | 9 | `private District newDistrict(int dx, int dy)` |  |
| 340 | 8 | `private void sortDistricts()` |  |
| 350 | 13 | `private void rangeTypes()` | Each type's lowest and highest district index holding it. |
| 364 | 4 | `private void advanceCursors()` |  |

### MEASURING THE GROUND: OWNED DRY PLOTS (lines 369-571)

| line | len | member | says |
|---:|---:|---|---|
| 374 | 27 | `private void measureAll()` | Every district the land's owned box reaches, measured; the coarse look first when there are many. |
| 403 | 28 | `private boolean[] coarseLand(int dx0, int dy0, int nx, int ny)` | Which districts have land in or beside them, by a sample a tile (COARSE_STRIDE): a district all sea there, with all-sea neighbours, is taken as sea and not measured. |
| 435 | 3 | `int ownership(int dx, int dy)` | Whether the city owns none, some or all of a district: the grid's cover of it, a level-8 block (spec-grid 2.4; by its radii and lanes until 0.7.66). |
| 440 | 3 | `private static int cover(int c)` | LandGrid's NONE, SOME or ALL as this class's. |
| 445 | 5 | `double[] ownedBox()` | The land's owned box from the site, in plots, inclusive {dx0, dy0, dx1, dy1}: its reach each way (an empty land, the site's plot). |
| 461 | 66 | `private void measure(District d, int own)` | Counts a district's free plots, tile by tile and plot by plot (0.7.64): owned and dry - the ground the model builds on, a resource's sites with it (a mine stands on its site; another building takes a site's plot only ... |
| 529 | 18 | `private List<long[]> siteSquares(long bx0, long by0, long bx1, long by1)` | Every site's square, {x0, y0, x1, y1} in plots inclusive, of every resource in fields, owned or not, that reaches into a box of plots: the plots the painter keeps as fields. |
| 549 | 8 | `private District measureNew(int dx, int dy, int own)` | A district not on the map yet, measured, and kept only when it holds free ground: a district of water joins the map only when a site in it needs one (tallySites()). |
| 559 | 1 | `public short[] tileFreeOf(District d)` | A district's tiles' free plots (a copy), counted if they were not: for a harness and a probe. |
| 562 | 9 | `short[] tileFree(District d)` | A district's tiles' free plots, counted if they were not (after a sidecar is read, or for a harness's square city); its room stands as it was. |

### THE LAND CHANGED: A PURCHASE, OR THE LAND DRAWN AGAIN (lines 572-619)

| line | len | member | says |
|---:|---:|---|---|
| 582 | 37 | `boolean syncLand()` | Catches the map up with its land: every district a new purchase touches recounted (new ones added); true when the land was drawn again (a restatement, a conversion), which the caller answers with a canonical map. |

### PLACING: THE MONTH'S CHANGE, AND THE CANONICAL ALLOCATION (lines 620-873)

| line | len | member | says |
|---:|---:|---|---|
| 633 | 24 | `public boolean reconcile(long[] model)` | THE MONTH (spec-land 2.5, star 12): the land caught up, then each type's change placed - new buildings into the first district with room (nearest first; farms, utilities, mines and wells, the railway and the car plant... |
| 662 | 1 | `public long changes()` | How many changes the map has taken since it was drawn: when it moves, a view stamps the tiles it shows again (batch J4). |
| 671 | 5 | `void rebind(CityLand live, Function<Resource, double[]> liveRemaining)` | A map drawn away from the screen's thread, on a copy of the land (Game.MapDraft, batch J4), bound to the city's own land and what remains of its resources before it is kept: the copy was the land as it stood, field fo... |
| 684 | 23 | `private int place(int t, long n)` | Places n more of type t: into the first districts with room for its whole plots in its direction (a road's inside its tiles' edge rings), the rest into the district with the most room left (0.7.64; the outermost befor... |
| 709 | 5 | `District roomiest()` | The district with the most room left, the nearer on a tie: where what no district has room for goes, to be drawn smaller there. |
| 716 | 21 | `private int remove(int t, long n)` | Takes n of type t away: an inner type from the outermost district holding it in, an outer type from the innermost out. |
| 739 | 3 | `private void add(District d, int i, int t, long k)` | Adds k (negative to take away) of type t to the district at index i, keeping its ground and plots used, the running total, the type's range, the cursors and the pyramid. |
| 744 | 17 | `private void add(District d, int i, int t, long k, boolean sited)` | ...a mine or well on a site (sited) taking its whole plots too: its site is its ground, the land the model gives it. |
| 763 | 5 | `long usedOf(District d)` | The ground a district's buildings use, in half square feet: each type's count x its land, twice. |
| 770 | 7 | `long roadOf(District d)` | The road plots a district holds. |
| 779 | 5 | `long cellsOf(District d)` | The whole plots a district's buildings and roads are drawn on, a mine or well on its site taking its own. |
| 795 | 78 | `private void allocate(long[] counts)` | THE CANONICAL ALLOCATION (spec-land 2.5): the mines and wells on their sites first (0.7.64), then the city's footprint F, in the whole plots it is drawn on, shared inner first - each district its share of F, min(its r... |

### MINES AND WELLS ON THEIR SITES (spec-land 2.4, 2.5) (lines 874-1207)

| line | len | member | says |
|---:|---:|---|---|
| 889 | 7 | **type** `static final class SiteIndex` | Every owned site of the SITED resources: per resource and holding, the districts holding them, nearest first, and how many in each. |
| 898 | 3 | `int holdingOf(long dx, long dy)` | The holding a plot (dx, dy) from the site lies in: 0 the centre, k the k-th purchase, -1 none - the grid's owner (spec-grid 2.4). |
| 903 | 18 | `private List<Deposit> fieldsNear(Resource r, double bx0, double by0, double bx1, double by1)` | The fields of a resource that reach into a box of plots: the world's, and a converted centre's legacy iron field. |
| 923 | 4 | `Deposit legacyField()` | A converted centre's legacy iron field, as a field of its sites at its plot (cell -1). |
| 935 | 9 | `SiteIndex siteIndex()` | The site index: every owned site of the SITED resources by holding and district, built with the map and extended by each purchase (a new holding's sites are those of the fields centred on its ground, wherever they lie). |
| 950 | 60 | `private void tallySites(SiteIndex ix, int from, double bx0, double by0, double bx1, double by1)` | Adds to the index the sites of holdings `from` on that lie in a box of plots from the site, and lays its lists out again: a new index object, so the walks lay themselves out again too. |
| 1012 | 7 | `public long ownedSites(Resource r)` | The owned sites of a SITED resource on the map. |
| 1025 | 5 | `static int holdingState(double listed, double left)` | A holding's state for a resource from what remains in it: UNWORKED, WORKING or WORKED_OUT (one that listed none, unworked). |
| 1032 | 7 | `int[] holdingStates(Resource r)` | Each holding's state for a resource, the centre first. |
| 1041 | 9 | **type** `private static final class Walk` | One SITED resource's sites in the order mines and wells take them, and how far its type's count has filled them. |
| 1054 | 4 | `private int typeOn(Resource r)` | The type standing on a SITED resource's sites: the first with it as its site, or -1. |
| 1068 | 25 | `private void placeSited(long[] model)` | Puts each SITED resource's mines or wells on its sites (spec-land 2.4, 2.5): the first sites in acquisition order whose holding is not worked out, then the worked-out ones, a holding's districts nearest first; more th... |
| 1094 | 11 | `private District home()` |  |
| 1106 | 1 | `private static int indexOf(District d)` |  |
| 1109 | 4 | `static int siteSlot(Resource r)` | Where a resource sits among SITED, or -1. |
| 1115 | 13 | `private void clearType(int t, int k)` | Takes every one of type t off the map, its sites' marks with it. |
| 1130 | 24 | `private Walk layWalk(SiteIndex ix, int[] states, int k, int t)` | The walk's order: holdings not worked out in acquisition order, then the worked-out; within each, its districts nearest first. |
| 1156 | 33 | `private void fill(Walk w, int k, long n)` | Fills or empties a walk to n: on sites from where it stood, past the last site in the founding site's district. |
| 1191 | 16 | `private void mark(District d, int k, int t, int holding, int m)` | m more (or fewer) of type t on a district's sites of one holding. |

### THE DEAL: EACH DISTRICT ONTO ITS OWN TILES, AS FULL AS THEY HOLD (lines 1208-1449)

| line | len | member | says |
|---:|---:|---|---|
| 1244 | 7 | **type** `static final class Dealt` | A district's deal: each tile's counts by type id (a mine or well on a site apart), and its road plots by kind [0, gravel, paved, highway]. |
| 1257 | 3 | `static boolean leansToCore(BuildingVisual.Type t)` | Whether a type leans toward the founding site in the deal: everything but roads and the outer kinds - what follows people. |
| 1262 | 5 | `double coreLean(long tx, long ty)` | The core's lean at a tile: 1 + CORE_BOOST e^-(r / CORE_RADIUS)^2, r from the founding site to the tile's middle in plots. |
| 1269 | 21 | `static void aliasTable(double[] w, double[] prob, int[] alias)` | Vose's alias table for weights w, into prob and alias. |
| 1292 | 5 | `private static int pick(long h, double[] prob, int[] alias)` | A pick through an alias table by a hash. |
| 1304 | 3 | `public int[][] deal(District d)` | A district's tiles' counts, [tile][type] (tile = row x 8 + column): every building it holds but its mines and wells on sites, each on one of its own tiles (THE DEAL). |
| 1309 | 3 | `public int[][] roads(District d)` | ...and their road plots by kind, [tile][0, gravel, paved, highway]. |
| 1314 | 3 | `public int overDealt(District d)` | What the deal of a district could not fit: plots charged to no tile (0 while the district holds no more than its tiles have room for). |
| 1318 | 9 | `private Dealt dealtOf(District d)` |  |
| 1328 | 108 | `private Dealt dealNow(District d)` |  |
| 1438 | 4 | `private static int firstWith(int[] order, int[] spare, int need)` | The first tile in `order` with at least `need` plots spare, or -1. |
| 1444 | 5 | `private static int roomiest(int[] order, int[] spare)` | The tile in `order` with the most plots spare, the first on a tie. |

### A TILE'S INPUTS FOR THE PAINTER (lines 1450-1616)

| line | len | member | says |
|---:|---:|---|---|
| 1455 | 3 | `public District districtOfTile(long tx, long ty)` | The district holding tile (tx, ty), or null. |
| 1460 | 7 | `public void tileCounts(long tx, long ty, int[] out)` | A tile's counts of the model's types from the deal: a copy into out, zeros where no district is. |
| 1469 | 7 | `public void tileRoads(long tx, long ty, int[] out)` | A tile's road plots by kind from the deal, into out [0, gravel, paved, highway]. |
| 1478 | 6 | `boolean tileHasRoads(long tx, long ty)` | Whether a tile has road plots to grow: what gives its neighbours their ports. |
| 1486 | 8 | `public void tileOwnership(long tx, long ty, boolean[] out)` | Which of a tile's plots the city owns: a tile owned wholly or not at all in one test (the grid's cover at level 5), the rest from the grid's leaves under it (spec-grid 2.4). |
| 1504 | 4 | `public void tileInput(long tx, long ty, TilePainter.Input in)` | Everything the painter needs for tile (tx, ty), into in: its ground, what the city owns of it, the model's buildings and road plots the deal gave it, which neighbours have roads, and the sites on it with their states ... |
| 1510 | 4 | `public void tileInput(long tx, long ty, TilePainter.Input in, byte[] terrain)` | ...with the tile's ground given (a view keeps it: MapTiles.terrain(), batch J4) rather than read from the world. |
| 1516 | 28 | `private void fillInput(long tx, long ty, TilePainter.Input in)` | Everything tileInput() fills but the ground, which is in `in` already. |
| 1546 | 1 | **type** `record DrawnSite(long x0, long y0, long x1, long y1, int kind, int state, int mine)` | One site as the map draws it: its square of plots (inclusive), resource, state and the mine on it (-1 for none). |
| 1560 | 49 | `List<DrawnSite> siteList(int dx, int dy)` | The sites whose centres lie in district (dx, dy) - every resource's, owned or not - each with its holding's state (its field's holding, the one holding the field's centre, since 0.7.64) and, for the SITED resources, t... |
| 1611 | 5 | `public void forgetPainted()` | Forgets the painted state of every district - the deals and the site lists - after the ground's states moved (what is worked out). |

### THE PYRAMID (spec-land 2.5): TEN CLASSES, USED AND OWNED, 2 x 2 (lines 1617-1748)

| line | len | member | says |
|---:|---:|---|---|
| 1636 | 112 | **type** `public static final class Pyramid` | The districts summed two by two, level by level, to one node - level 0 is the districts themselves, each level above a grid of nodes over the districts' box, a node NODE_WIDTH numbers. |
| 1644 | 45 | `Pyramid(CityMap map)` _(in CityMap.Pyramid)_ |  |
| 1691 | 11 | `long[] vector(District d)` _(in CityMap.Pyramid)_ | A district's numbers: its buildings by class, its ground used (half square feet) and its owned dry plots. |
| 1704 | 15 | `void add(District d, int t, long k)` _(in CityMap.Pyramid)_ | k more (or fewer) of type t in a district: its ancestors take them. |
| 1721 | 1 | `public int levels()` _(in CityMap.Pyramid)_ | How many levels above the districts. |
| 1724 | 1 | `public int nodes()` _(in CityMap.Pyramid)_ | How many nodes above the districts hold anything (and the top). |
| 1727 | 11 | `public long[] root()` _(in CityMap.Pyramid)_ | The top node: the whole city's numbers. |
| 1740 | 7 | `public long[] node(int level, int dx, int dy)` _(in CityMap.Pyramid)_ | The node at a level (1 up) covering district (dx, dy), or null. |

### THE SIDECAR (spec-land 2.5) (lines 1749-1905)

| line | len | member | says |
|---:|---:|---|---|
| 1763 | 41 | `public byte[] writeSidecar(int month)` | The map as its sidecar's bytes, deflated: the header (magic, format, seed, month, stamp, the founding site, the stamp of the land's rectangles it was drawn on (CityLand.stamp(), 0.7.67) and its purchases, the type cou... |
| 1808 | 1 | `public long lastStamp()` | The stamp of the sidecar last written: what the save carries as mapStamp. |
| 1811 | 13 | `static long stampOf(byte[] raw)` | The stamp of a sidecar's raw bytes: SplitMix64 folded over them, the stamp's own eight bytes read as zero. |
| 1826 | 7 | `public static long stampIn(byte[] deflated)` | The stamp a sidecar carries, or 0 when it is not one. |
| 1834 | 7 | `private static byte[] inflate(byte[] deflated)` |  |
| 1849 | 46 | `public static CityMap readSidecar(byte[] deflated, CityLand land, Function<Resource, double[]> remaining, BuildingVisual.Type[]...` | A map read back from its sidecar, or null when it is not one, is not this city's (another seed, month or stamp, or land drawn otherwise), or does not add up. |
| 1897 | 8 | `public boolean same(CityMap o)` | Whether two maps hold the same districts with the same figures, in the same order. |

