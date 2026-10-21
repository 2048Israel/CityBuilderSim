# PlanCheck.java - 1,666 lines · 51 methods · 34 constants · harnesses

`ham/citybuildersim/PlanCheck.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

> The district plan: one street network, + junctions no nearer than eight plots, every building within reach and none on a street or beside a highway, and the model's road drawn exactly as the streets' surface - on the prototype's test district, MapCheck's fixture of Jerus's city, a city played as the playtest plays it, and any save named - with the prototype's own figures where they apply, how full a district it can draw, and what a plan costs.
> 
> WHY THIS EXISTS (0.7.87, batch RD1; the project's spec-roads-and-ports.md
> 2.4, 2.5, 2.9 and 2.10). The map's road checks until 0.7.86 (MapCheck 8)
> passed on the city Jerus's screenshot showed - a maze of road with a shop in
> each hole beside a tile with no road - because they measured an
> intermediate list, the road tiles, not what was drawn. DistrictPlan is what
> batch RD2 paints from, so its rules are held on the plan itself, plot by
> plot, after every plan: the spec's 2.4 checks and H5.
> 
> What it has to prove:
>   1. the port: on the prototype's own test district (proto2.py's ground,
>      Jerus's mix at 70% of its dry ground, his roads, the same trips paved
>      and half that), with the prototype's own hashes and rules, the plan is
>      the prototype's - every building's box in order, every street plot,
>      width and kind - and so is every figure of the spec's 2.5 table;
>   2. the same district with the game's rules and hashes: the checks hold,
>      and its figures stand beside the table's (the water rule, the streets
>      along a cut, other hashes); a building wider than an estate's strip
>      takes a whole estate cell, its spine closed;
>   3. the highways (H5): with a straight highway across the district no
>      building plot touches a highway plot, corners included, and the
>      streets pass beneath it in one network;
>   4. how full a district the plan can draw (spec 2.10): flat ground, every
>      plot owned, Jerus's mix and the same city built paved at 75% to 92%
>      of the ground - what has no place, and the street share by cell kind;
>   5. MapCheck's fixture of Jerus's city (his city x 1 on the design's square
>      city, at his density): every district's plan keeps the checks; and the
>      dense screen's districts (x 10,000), each planned within PLAN_MS, half
>      the screen's SCREEN_MS;
>   6. a city played as the playtest plays it (MapCheck 1's), every
>      district planned at every DRAWN_EVERY-th month: the checks hold;
>   7. estate cells laid to fit what they hold (0.7.90, batch RD5;
>      DistrictPlan's ESTATE LINES): each estate-band type alone on flat
>      ground holds at least as much in no more cells than on the
>      prototype's spine - a 4 x 4 works and a 9 x 9 plant in fewer, an
>      11 x 11 works in as many (31 = 2 x 11 + 9) - every check holding; and
>      a plan worked out afresh is the plan;
>   8. industry and the outer kinds share estate cells (0.7.92, batch RD6;
>      DistrictPlan's SHARED ESTATES): with cells to spare the plan is the
>      plan with the bands apart, box for box, and no outer kind stands in
>      an industry cell; with none left the outer kinds take industry's
>      leftover ground - fewer without a place, every homes and industry
>      box where it was - every check holding;
>   9. any save named on the command line, every district: the checks, how
>      full (its districts, and its own mix at 2.10's fills), its seams, and
>      the time, each within PLAN_MS;
>  10. homes cells' streets by a hash of their place (batch CL2;
>      DistrictPlan's LAYOUT): on flat ground whose homes cells are all long
>      blocks, on flat ground whose homes cells are squared and with
>      boulevards, and on the test district cut by water, Input.combOnly is
>      0.8.11's plan box for box and street for street, and with the variety
>      on every check holds, each of the three layouts (the comb, dropped
>      crossings, brick) appears in long-block and in squared cells and over
>      many cells in its share, a cell's layout is a function of the world's
>      seed and its place and not of what its district holds, two districts
>      agree along their seam no worse than with the comb, no cell of a
>      varied layout has a dead end and no plan has more than the comb's -
>      and a city played as the playtest plays it shows the variety firing.
> 
> ... (16 more lines in the source)

**Uses:** [DistrictPlan](DistrictPlan.md) (323), [BuildingVisual](BuildingVisual.md) (38), [CityMap](CityMap.md) (31), [World](World.md) (29), [MapCheck](MapCheck.md) (26), [LongPlaytest](LongPlaytest.md) (16), [TilePainter](TilePainter.md) (8), [Founding](Founding.md) (8), [GameFiles](GameFiles.md) (5), [Game](Game.md) (4), [Timing](Timing.md) (3), [BuildingCatalog](BuildingCatalog.md) (3), [Mining](Mining.md) (2), [MiningCheck](MiningCheck.md) (2), [TreasuryFund](TreasuryFund.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [BuildingType](BuildingType.md) (1), [Resource](Resource.md) (1)

**Used by (2):** [EarthTerrainCheck](EarthTerrainCheck.md), [JunctionCheck](JunctionCheck.md)

## Sections

| line | section |
|---:|---|
| 267 | THE FIGURES OF ONE PLAN |
| 564 | 1. THE PORT: THE PROTOTYPE'S TEST DISTRICT, REPLAYED |
| 636 | 2. THE GAME'S RULES ON THE TEST DISTRICT |
| 674 | 3. THE HIGHWAYS (H5) |
| 702 | 4. HOW FULL A DISTRICT THE PLAN CAN DRAW (spec 2.10) |
| 775 | THE DISTRICTS OF A CITY: EVERY ONE PLANNED, ITS CHECKS AND FIGURES |
| 913 | 5. MAPCHECK'S FIXTURE OF JERUS'S CITY, AND THE DENSE SCREEN |
| 974 | 6. A CITY PLAYED AS THE PLAYTEST PLAYS IT |
| 1038 | 7. ESTATE CELLS LAID TO FIT WHAT THEY HOLD (0.7.90, batch RD5) |
| 1130 | 8. INDUSTRY AND THE OUTER KINDS SHARE ESTATE CELLS (0.7.92, batch RD6) |
| 1217 | 10. HOMES CELLS' STREETS BY A HASH OF THEIR PLACE (CL2) |
| 1572 | 9. A SAVE NAMED ON THE COMMAND LINE: EVERY DISTRICT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 103 | `PlanCheck.TIMED` | `5` | Repeats a district's plan is timed over, after one untimed: its median is its time. |
| 114 | `PlanCheck.PLAN_MS` | `MapCheck.SCREEN_MS / 2` | The most a district's plan may take, the slowest of the dense screen's districts (each its median of TIMED): half MapCheck's screen, 40 ms (star) - one plan and a screen painted from plans within SCREEN_MS, so long as... |
| 117 | `PlanCheck.TABLE` | `{ { 49, 15, 9802, 828, 8974, 0, 13, 26, 280, 2995 }, { 48, 13, 6052, 3137, 29...` | The prototype's figures of the spec's 2.5 table, for his roads, the same trips paved and half that: cells, homes cells, street plots, narrow, full, tracks, square cells, boulevard cells, + junctions, buildings touchin... |
| 123 | `PlanCheck.TABLE_BUILDINGS` | `3617, TABLE_BUILDING_PLOTS = 32217` | ...its buildings and their plots... |
| 125 | `PlanCheck.TABLE_BUDGET` | `{ 9388, 4484, 2242 }` | ...and the road of each budget, whole plots. |
| 128 | `PlanCheck.TEST_HUB_X` | `66, TEST_HUB_Y = 86` | The prototype's hub on its test district, in the district's plots: (70, 90) in its frame, 4 plots in. |
| 131 | `PlanCheck.TEST_UNOWNED_ROWS` | `40, TEST_UNOWNED_FROM = 196` | The part of the test district the prototype did not own: its first 40 rows east of plot 196 (O[:44, 200:] in its frame). |
| 134 | `PlanCheck.FILLS` | `{ 0.75, 0.80, 0.85, 0.92 }` | The fills of 2.10's table: the model's buildings and roads at these shares of a flat district's ground. |
| 137 | `PlanCheck.PROTO_FRAME` | `264` | The ground 2.10's fills are shares of: the prototype's frame, the district and 4 plots about it, 264 x 264 (its n = OFF + 32 x 8 + 4) - so its 75% is 79.8% of the district's own 256 x 256. |
| 140 | `PlanCheck.GRAVEL_TRIPS` | `900, PAVED_TRIPS = 1200` | Gravel's and paved road's trips a road (buildings.json's capacities, as the prototype read them): the same city paved holds his roads' trips on Paved Roads. |
| 150 | `PlanCheck.TEST_GROUND` | `"g36f14g13f1g14f29g89u61/g36f15g11f2g14f28g90u61/g35f17g10f2g13f29g90u61/g35f...` | THE PROTOTYPE'S TEST DISTRICT (claude/roads-prototype/proto2.py and roads_proto.py, written out by batch RD1): district_ground(264, 7) - grass, forest, a river two to four plots wide, a bay of sea - its district's fra... |
| 193 | `PlanCheck.TEST_NUDGE` | `{ 0.3727494347187473, 0.03906684873305479, 0.22912251263578634, 0.52450302674...` | Each cell's nudge, h32(4127, 'cell', c) as a fraction, cell ci + cj x CELLS_A_SIDE. |
| 211 | `PlanCheck.TEST_ACROSS` | `2483539012620168834L` | Each cell's direction, a bit a cell: set where h32(4127, 'dir', c) is under a half (east-west), read as 0.25, else 0.75. |
| 213 | `PlanCheck.TEST_TYPES` | `{ 0, 1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 17, 18, 19, 20, 22, 23, 25, 27, 28, 31,...` | The types its buildings are, by id (BuildingVisual.table())... |
| 215 | `PlanCheck.TEST_COUNTS` | `{ 899, 94, 569, 30, 2, 799, 2, 1, 2, 23, 5, 112, 54, 1, 1, 13, 6, 4, 1, 2, 22...` | ...how many of each, scaled_buildings at 70% of its dry ground... |
| 217 | `PlanCheck.TEST_DEAL` | `{ 0.8623860022826665, 0.4688361493186886, 0.49244847032280087, 0.910102558251...` | ...and each type's start, h32(4127, 'deal', t) as a fraction. |
| 231 | `PlanCheck.TEST_GRAVEL` | `8407.741935483871, TEST_PAVED = 980.6451612903226, TEST_PAVED_ALL = 4483.8709...` | Its road plots (Input.gravel and Input.paved): his Gravel and Paved Roads, and the same trips all paved (the third budget half of it). |
| 233 | `PlanCheck.TEST_PLACED_JERUS` | `- 1980402857778823524L, TEST_STREETS_JERUS = - 7143981648789130548L` | Each budget's plan as the prototype made it, its buildings' and its streets' FNV-1a digests (placedDigest(), streetDigest()): his roads... |
| 235 | `PlanCheck.TEST_PLACED_PAVED` | `9000504916410474359L, TEST_STREETS_PAVED = - 4283127041626870812L` | ...the same trips paved... |
| 237 | `PlanCheck.TEST_PLACED_THIN` | `9000504916410474359L, TEST_STREETS_THIN = 4092048347058802282L` | ...and half that. |
| 609 | `PlanCheck.BUDGETS` | `{ "his roads", "the same trips paved", "half that" }` | The three budgets' names, as the sections print them. |
| 979 | `PlanCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` | A stream that drops what is written to it: the game's and the playtest's prose while a section plays or loads a city. |
| 1036 | `PlanCheck.HOMES_ENOUGH` | `10` | The homes cells a played city's samples must hold before they are asked to show a layout other than the comb (CL2): ten cells all keeping the comb by the hash alone has a chance of 1 in 59,049. |
| 1043 | `PlanCheck.ESTATE_FILL` | `0.4` | The share of a flat district's plots each estate-band type is set at, alone: 0.4 - room to spare, so what differs is the cells they take. |
| 1135 | `PlanCheck.SPARE_FILL` | `FILLS [ 0 ], FULL_FILL = FILLS [ FILLS.length - 1 ]` | Section 4's fill with cells to spare (2.10's first, 75%) and its fullest (92%: every cell open, and outer kinds left without a place when the bands keep apart). |
| 1222 | `PlanCheck.LONG_ROADS` | `0.5` | LONG's roads, a factor of his (section 4's 75% fill): 0.5 - too little road for the ladder to square a homes cell, so all of them keep long blocks. |
| 1225 | `PlanCheck.SQUARED_ROADS` | `0.7` | SQUARED's: 0.7 - road enough to square most of the homes cells and not to make boulevards of them. |
| 1228 | `PlanCheck.BOULEVARD_ROADS` | `1` | BOULEVARD's: 1, his own - road enough for boulevards, squared cells among them. |
| 1231 | `PlanCheck.LAYOUT_FIXTURES` | `{ "LONG", "SQUARED", "BOULEVARD", "CUT0", "CUT1" }` | Section 10's fixtures: flat ground at his fill with his roads times a factor (the first three), and the test district's ground, a river and the sea cutting cells, on its first two budgets. |
| 1234 | `PlanCheck.COMB_DIGESTS` | `{ { - 8123742265030361694L, - 580478996347052330L }, { 6065673935051394403L, ...` | Each fixture's plan with Input.combOnly, its buildings' and its streets' digests (placedDigest(), streetDigest()) as 0.8.11 drew them: the switch off is that plan, box for box and street for street. |
| 1242 | `PlanCheck.SHARE_SIDE` | `200` | The cells on a side of the square of the lattice layoutOf() is asked about to measure its shares: 200, 40,000 cells. |
| 1245 | `PlanCheck.SHARE_TOLERANCE` | `0.01` | How far a layout's share of those cells may stand from its dial: 0.01. |
| 1248 | `PlanCheck.MORE_HOMES` | `0.1` | The part more of the homes band's largest type LONG is given, to see a cell keep its layout as its district grows: a tenth. |
| 1251 | `PlanCheck.OTHER_SEED` | `Founding.DEFAULT_WORLD_SEED + 1` | Another world's seed, to see the layouts read it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 94 | `static int fails` |  |
| 95 | `static PrintStream out` |  |
| 273 | `int pieces, plus, closestPlus` |  |
| 274 | `int placed, overflow, buildingPlots, streetPlots, tracks, narrow, full, seams, under, minesBeyond` |  |
| 276 | `int estatePlus, estateLinesOff` | + junctions inside an estate cell's interior, and estate cells whose streets leave ESTATE LINES' lines (0.7.90). |
| 277 | `boolean groundParts` |  |
| 279 | `double gravel, paved, highway` | Surface drawn by kind: gravel, paved, a highway's. |
| 281 | `final long[] cellStreets` | Street plots and dry ground in the open cells' tiles: homes cells of long blocks, of square blocks, estate cells; and the boulevard cells. |
| 282 | `final int[] cellCount` |  |
| 781 | `int districts, held, network, apart, floor, reach, onStreet, surface, beside, estate, mines, minesBeyond` |  |
| 782 | `long buildings, placed, overflow, overflowPlots, needPlots, streets, tracks, seams, under, surplusDistricts` |  |
| 783 | `double surplus, worstFill, worstLost` |  |
| 784 | `String worstWhere` |  |
| 785 | `final long[] cellStreets` |  |
| 786 | `final int[] cellCount` |  |
| 788 | `final int[][] homes` | The open homes cells by kind (0 long blocks, 1 squared) and layout (CL2), and the merges undone in them. |
| 789 | `long mergesUndone` |  |
| 790 | `final List<Double> planMs` |  |
| 791 | `boolean timed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 1575 | **type** `public class PlanCheck` | The district plan: one street network, + junctions no nearer than eight plots, every building within reach and none on a street or beside a highway, and the model's road drawn exactly as the streets' surface - on the ... |
| 97 | 4 | `static void check(String label, boolean ok)` |  |
| 239 | 27 | `public static void main(String[] args) throws Exception` |  |

### THE FIGURES OF ONE PLAN (lines 267-563)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 32 | **type** `static final class Figures` | What a plan draws, and its checks. |
| 285 | 7 | `boolean surfaceHolds(DistrictPlan p, DistrictPlan.Input in)` _(in PlanCheck.Figures)_ | Whether the surface is the road by kind to within half a plot, and with the surplus exactly. |
| 294 | 4 | `boolean holds(DistrictPlan p, DistrictPlan.Input in)` _(in PlanCheck.Figures)_ | Whether every check holds: one network (or the ground parts it), the + floor, reach, none on a street, the surface, none beside a highway, T junctions only in an estate cell. |
| 300 | 3 | `boolean estateHolds()` _(in PlanCheck.Figures)_ | Whether its estate cells keep ESTATE LINES (0.7.90): no + inside one, its streets on their lines. |
| 306 | 151 | `static Figures figures(DistrictPlan p, DistrictPlan.Input in)` | A plan's figures and checks. |
| 458 | 4 | `static boolean onLattice(int q)` |  |
| 463 | 3 | `static boolean lat(boolean[] st, int q)` |  |
| 468 | 22 | `static int[] reach(boolean[] st)` | Each plot's distance from a street across corners, up to REACH; 99 beyond. |
| 499 | 26 | `static boolean groundParts(DistrictPlan p, DistrictPlan.Input in, int[] lab, List<Integer> sizes)` | Whether the ground parts a plan's street pieces: no way from its largest piece to another over the city's own ground - dry, fresh water no wider along the way than its line may bridge (an arterial's on an arterial's l... |
| 527 | 6 | `static int waterRun(DistrictPlan.Input in, int a, int d)` | Fresh water's run at plot a along direction d's axis. |
| 535 | 7 | `static long placedDigest(DistrictPlan p)` | A digest of a plan's buildings, in the order placed: x, y, across, down and type of each (FNV-1a over 64 bits). |
| 544 | 10 | `static long streetDigest(DistrictPlan p)` | ...and of its streets, row by row: each plot, its width in half plots and its kind (track 0, gravel 1, paved 2). |
| 556 | 7 | `static String line(DistrictPlan p, Figures f)` | One line of a plan's figures. |

### 1. THE PORT: THE PROTOTYPE'S TEST DISTRICT, REPLAYED (lines 564-635)

| line | len | member | says |
|---:|---:|---|---|
| 569 | 38 | `static DistrictPlan.Input testDistrict(BuildingVisual.Type[] types, int budget, boolean prototype)` | The prototype's test district: its ground (TEST_GROUND, the district's frame), its buildings, and one of its three budgets (0 his roads, 1 the same trips paved, 2 half that). |
| 611 | 24 | `static void replay(BuildingVisual.Type[] types)` |  |

### 2. THE GAME'S RULES ON THE TEST DISTRICT (lines 636-673)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 33 | `static void gameRules(BuildingVisual.Type[] types)` |  |

### 3. THE HIGHWAYS (H5) (lines 674-701)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 23 | `static void highways(BuildingVisual.Type[] types)` |  |

### 4. HOW FULL A DISTRICT THE PLAN CAN DRAW (spec 2.10) (lines 702-774)

| line | len | member | says |
|---:|---:|---|---|
| 707 | 25 | `static void scaled(DistrictPlan.Input in, long[] counts, int dry, double fill, boolean pavedCity)` | Jerus's mix (MapCheck.JERUS_COUNTS) scaled to a share of a district's dry plots, his roads with it - the prototype's scaled_buildings, its fractions by the world's hash. |
| 733 | 38 | `static void howFull(BuildingVisual.Type[] types)` |  |
| 772 | 3 | `static double pct(long a, long b)` |  |

### THE DISTRICTS OF A CITY: EVERY ONE PLANNED, ITS CHECKS AND FIGURES (lines 775-912)

| line | len | member | says |
|---:|---:|---|---|
| 780 | 132 | **type** `static final class Tally` | Every district of a map planned: how many keep each check, what they draw, what has no place, and what a plan costs. |
| 794 | 19 | `void add(CityMap m, CityMap.District d, boolean time)` _(in PlanCheck.Tally)_ | District d of map m planned (and timed, TIMED times after one untimed, when `time`). |
| 814 | 49 | `void add(DistrictPlan p, DistrictPlan.Input in, String where)` _(in PlanCheck.Tally)_ |  |
| 865 | 5 | `int varied()` _(in PlanCheck.Tally)_ | The open homes cells that took a layout other than the comb or the whole square. |
| 872 | 3 | `int homesCells()` _(in PlanCheck.Tally)_ | All the open homes cells. |
| 876 | 6 | `static double median(List<Double> v)` _(in PlanCheck.Tally)_ |  |
| 883 | 5 | `static double max(List<Double> v)` _(in PlanCheck.Tally)_ |  |
| 889 | 17 | `void report(String what)` _(in PlanCheck.Tally)_ |  |
| 908 | 3 | `boolean allHold()` _(in PlanCheck.Tally)_ | Whether every check holds on every district (one network where the ground does not part it). |

### 5. MAPCHECK'S FIXTURE OF JERUS'S CITY, AND THE DENSE SCREEN (lines 913-973)

| line | len | member | says |
|---:|---:|---|---|
| 918 | 16 | `static CityMap squareCity(BuildingVisual.Type[] types, long seed, long[] place, double k)` | Jerus's city x k on the design's square city at the dry place, as MapCheck's copies (MapCheck.Squares): his mines no more than its iron sites of their type (since 0.8.0, an Iron Mine's hematite). |
| 935 | 38 | `static void fixture(BuildingVisual.Type[] types)` |  |

### 6. A CITY PLAYED AS THE PLAYTEST PLAYS IT (lines 974-1037)

| line | len | member | says |
|---:|---:|---|---|
| 984 | 50 | `static void played() throws Exception` |  |

### 7. ESTATE CELLS LAID TO FIT WHAT THEY HOLD (0.7.90, batch RD5) (lines 1038-1129)

| line | len | member | says |
|---:|---:|---|---|
| 1046 | 11 | `static DistrictPlan.Input estateAlone(BuildingVisual.Type[] types, int type, int count, boolean prototype)` | Each estate-band type on flat owned ground, `count` of it alone, with the game's rules or the prototype's (one spine across every estate cell). |
| 1059 | 20 | `static long[] estateShare(DistrictPlan p, DistrictPlan.Input in)` | Box plots in a plan's estate cells and their dry ground (the cell's tile, its own arterials with it), and how many it opened. |
| 1080 | 49 | `static void estateLines(BuildingVisual.Type[] types)` |  |

### 8. INDUSTRY AND THE OUTER KINDS SHARE ESTATE CELLS (0.7.92, batch RD6) (lines 1130-1216)

| line | len | member | says |
|---:|---:|---|---|
| 1138 | 3 | `static int band(BuildingVisual.Type t)` | A building's band in the plan (spec 2.4): 0 what follows people, 1 industry, 2 the outer kinds - as DistrictPlan deals them. |
| 1143 | 11 | `static int outerInIndustry(DistrictPlan p, BuildingVisual.Type[] types)` | The outer-kind buildings standing in a cell whose first building is industry's: industry's cells they share. |
| 1156 | 11 | `static DistrictPlan.Input flatMix(BuildingVisual.Type[] types, double fill, boolean roads, boolean apart)` | Jerus's mix on a flat district at a fill (section 4's), with his roads or none, the bands sharing or apart. |
| 1168 | 48 | `static void sharedEstates(BuildingVisual.Type[] types)` |  |

### 10. HOMES CELLS' STREETS BY A HASH OF THEIR PLACE (CL2) (lines 1217-1571)

| line | len | member | says |
|---:|---:|---|---|
| 1254 | 13 | `static DistrictPlan.Input layoutInput(BuildingVisual.Type[] types, int k, boolean combOnly)` | Section 10's fixture k (LAYOUT_FIXTURES): the flat district with his roads times a factor, or the test district's ground and budget k - 3; with combOnly when asked. |
| 1269 | 5 | `static int[][] homesByLayout(DistrictPlan p)` | A plan's open homes cells counted by kind (0 long blocks, 1 squared; a boulevard's interior by whether it is squared) and layout. |
| 1276 | 3 | `static String layoutLine(int[][] n)` | Homes cells by kind and layout as a line. |
| 1281 | 11 | `static int[] homesKinds(DistrictPlan p)` | A plan's open homes cells: how many, how many squared, how many boulevards, how many of those squared, and how many squared and not boulevards. |
| 1294 | 22 | `static int deadEnds(DistrictPlan p, boolean variedOnly)` | The dead ends of a plan: street plots (any kind but none) of the frame's interior, not its first or last row or column, with at most one street among their four neighbours - only those inside the interior of a homes c... |
| 1318 | 19 | `static long[][] homesStreetShare(DistrictPlan p, DistrictPlan.Input in)` | Street plots and dry ground in the open homes cells' tiles (a cell's interior and its own west and north arterials, as Figures.cellStreets has them), by kind and layout: row kind x 3 + layout, {street plots, dry ground}. |
| 1339 | 4 | `static String shareLine(long[][] s)` | The street share of homesStreetShare() as a line, a layout of a kind at a time. |
| 1345 | 4 | `static boolean allComb(DistrictPlan p)` | Whether every cell of a plan keeps LAYOUT_TODAY: the comb, the whole square, an estate. |
| 1351 | 7 | `static boolean crossedCell(DistrictPlan.Input in, int ci, int cj)` | Whether a highway, railway, yard or reserve (Input.fixed) has a plot in the box of the cell (ci, cj), its interior and its ring: DistrictPlan's crossed(), such a homes cell keeping the comb. |
| 1360 | 5 | `static byte expectedLayout(DistrictPlan p, DistrictPlan.Input in, int c)` | The layout cell c of a plan has to have: its place's (layoutOf of the seed and the cell's north-west plot in the world) for a homes cell no highway, railway, yard or reserve crosses, else LAYOUT_TODAY. |
| 1366 | 205 | `static void streetLayouts(BuildingVisual.Type[] types)` |  |

### 9. A SAVE NAMED ON THE COMMAND LINE: EVERY DISTRICT (lines 1572-1666)

| line | len | member | says |
|---:|---:|---|---|
| 1577 | 89 | `static void save(Path dir) throws Exception` | A save folder's autosave, loaded from a copy (a game run autosaves into any city it loads): every district planned, checked and timed. |

