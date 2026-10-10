# PlanCheck.java - 1,239 lines · 38 methods · 24 constants · harnesses

`ham/citybuildersim/PlanCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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
>      the time, each within PLAN_MS.
> 
> THE CHECKS, after every plan (spec 2.4; the prototype's, and H5):
>   - one network: its streets one piece, four-connected (a street beneath a
>     highway, a seam and a track counted) - or pieces the ground parts, no
>     way over the city's dry ground, a bridge's water or beneath a highway
>     joining them;
>   - the + floor: from a + junction (a street plot with street on its four
>     sides and none on its corners, as MapCheck 8's) the next along a street
>     is LATTICE plots or more away;
>   - every building within REACH of a street, across corners;
>   - no building on a street;
>   - the surface drawn, kind by kind, is the road plots by kind to within
>     half a plot, and with the surplus the ladder could not hold, exactly;
> ... (4 more lines in the source)

**Uses:** [DistrictPlan](DistrictPlan.md) (197), [BuildingVisual](BuildingVisual.md) (36), [MapCheck](MapCheck.md) (25), [CityMap](CityMap.md) (25), [World](World.md) (24), [LongPlaytest](LongPlaytest.md) (15), [TilePainter](TilePainter.md) (8), [Founding](Founding.md) (6), [GameFiles](GameFiles.md) (5), [Game](Game.md) (4), [BuildingCatalog](BuildingCatalog.md) (2), [Resource](Resource.md) (2), [MiningCheck](MiningCheck.md) (2), [TreasuryFund](TreasuryFund.md) (2), [BuildingType](BuildingType.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 241 | THE FIGURES OF ONE PLAN |
| 538 | 1. THE PORT: THE PROTOTYPE'S TEST DISTRICT, REPLAYED |
| 609 | 2. THE GAME'S RULES ON THE TEST DISTRICT |
| 647 | 3. THE HIGHWAYS (H5) |
| 675 | 4. HOW FULL A DISTRICT THE PLAN CAN DRAW (spec 2.10) |
| 748 | THE DISTRICTS OF A CITY: EVERY ONE PLANNED, ITS CHECKS AND FIGURES |
| 867 | 5. MAPCHECK'S FIXTURE OF JERUS'S CITY, AND THE DENSE SCREEN |
| 928 | 6. A CITY PLAYED AS THE PLAYTEST PLAYS IT |
| 982 | 7. ESTATE CELLS LAID TO FIT WHAT THEY HOLD (0.7.90, batch RD5) |
| 1074 | 8. INDUSTRY AND THE OUTER KINDS SHARE ESTATE CELLS (0.7.92, batch RD6) |
| 1161 | 9. A SAVE NAMED ON THE COMMAND LINE: EVERY DISTRICT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 89 | `PlanCheck.TIMED` | `5` | Repeats a district's plan is timed over, after one untimed: its median is its time. |
| 99 | `PlanCheck.PLAN_MS` | `MapCheck.SCREEN_MS / 2` | The most a district's plan may take, the slowest of the dense screen's districts (each its median of TIMED): half MapCheck's screen, 40 ms (star) - one plan and a screen painted from plans within SCREEN_MS, so long as... |
| 102 | `PlanCheck.TABLE` | `{ { 49, 15, 9802, 828, 8974, 0, 13, 26, 280, 2995 }, { 48, 13, 6052, 3137, 29...` | The prototype's figures of the spec's 2.5 table, for his roads, the same trips paved and half that: cells, homes cells, street plots, narrow, full, tracks, square cells, boulevard cells, + junctions, buildings touchin... |
| 108 | `PlanCheck.TABLE_BUILDINGS` | `3617, TABLE_BUILDING_PLOTS = 32217` | ...its buildings, their plots, and the road of each budget, whole plots. |
| 109 | `PlanCheck.TABLE_BUDGET` | `{ 9388, 4484, 2242 }` |  |
| 112 | `PlanCheck.TEST_HUB_X` | `66, TEST_HUB_Y = 86` | The prototype's hub on its test district, in the district's plots: (70, 90) in its frame, 4 plots in. |
| 115 | `PlanCheck.TEST_UNOWNED_ROWS` | `40, TEST_UNOWNED_FROM = 196` | The part of the test district the prototype did not own: its first 40 rows east of plot 196 (O[:44, 200:] in its frame). |
| 118 | `PlanCheck.FILLS` | `{ 0.75, 0.80, 0.85, 0.92 }` | The fills of 2.10's table: the model's buildings and roads at these shares of a flat district's ground. |
| 121 | `PlanCheck.PROTO_FRAME` | `264` | The ground 2.10's fills are shares of: the prototype's frame, the district and 4 plots about it, 264 x 264 (its n = OFF + 32 x 8 + 4) - so its 75% is 79.8% of the district's own 256 x 256. |
| 124 | `PlanCheck.GRAVEL_TRIPS` | `900, PAVED_TRIPS = 1200` | Gravel's and paved road's trips a road (buildings.json's capacities, as the prototype read them): the same city paved holds his roads' trips on Paved Roads. |
| 134 | `PlanCheck.TEST_GROUND` | `"g36f14g13f1g14f29g89u61/g36f15g11f2g14f28g90u61/g35f17g10f2g13f29g90u61/g35f...` | THE PROTOTYPE'S TEST DISTRICT (claude/roads-prototype/proto2.py and roads_proto.py, written out by batch RD1): district_ground(264, 7) - grass, forest, a river two to four plots wide, a bay of sea - its district's fra... |
| 176 | `PlanCheck.TEST_NUDGE` | `{ 0.3727494347187473, 0.03906684873305479, 0.22912251263578634, 0.52450302674...` |  |
| 193 | `PlanCheck.TEST_ACROSS` | `2483539012620168834L` |  |
| 194 | `PlanCheck.TEST_TYPES` | `{ 0, 1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 17, 18, 19, 20, 22, 23, 25, 27, 28, 31,...` |  |
| 195 | `PlanCheck.TEST_COUNTS` | `{ 899, 94, 569, 30, 2, 799, 2, 1, 2, 23, 5, 112, 54, 1, 1, 13, 6, 4, 1, 2, 22...` |  |
| 196 | `PlanCheck.TEST_DEAL` | `{ 0.8623860022826665, 0.4688361493186886, 0.49244847032280087, 0.910102558251...` |  |
| 209 | `PlanCheck.TEST_GRAVEL` | `8407.741935483871, TEST_PAVED = 980.6451612903226, TEST_PAVED_ALL = 4483.8709...` |  |
| 210 | `PlanCheck.TEST_PLACED_JERUS` | `- 1980402857778823524L, TEST_STREETS_JERUS = - 7143981648789130548L` |  |
| 211 | `PlanCheck.TEST_PLACED_PAVED` | `9000504916410474359L, TEST_STREETS_PAVED = - 4283127041626870812L` |  |
| 212 | `PlanCheck.TEST_PLACED_THIN` | `9000504916410474359L, TEST_STREETS_THIN = 4092048347058802282L` |  |
| 582 | `PlanCheck.BUDGETS` | `{ "his roads", "the same trips paved", "half that" }` |  |
| 932 | `PlanCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` |  |
| 987 | `PlanCheck.ESTATE_FILL` | `0.4` | The share of a flat district's plots each estate-band type is set at, alone: 0.4 - room to spare, so what differs is the cells they take. |
| 1079 | `PlanCheck.SPARE_FILL` | `FILLS [ 0 ], FULL_FILL = FILLS [ FILLS.length - 1 ]` | Section 4's fill with cells to spare (2.10's first, 75%) and its fullest (92%: every cell open, and outer kinds left without a place when the bands keep apart). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 80 | `static int fails` |  |
| 81 | `static PrintStream out` |  |
| 247 | `int pieces, plus, closestPlus` |  |
| 248 | `int placed, overflow, buildingPlots, streetPlots, tracks, narrow, full, seams, under, minesBeyond` |  |
| 250 | `int estatePlus, estateLinesOff` | + junctions inside an estate cell's interior, and estate cells whose streets leave ESTATE LINES' lines (0.7.90). |
| 251 | `boolean groundParts` |  |
| 253 | `double gravel, paved, highway` | Surface drawn by kind: gravel, paved, a highway's. |
| 255 | `final long[] cellStreets` | Street plots and dry ground in the open cells' tiles: homes cells of long blocks, of square blocks, estate cells; and the boulevard cells. |
| 256 | `final int[] cellCount` |  |
| 754 | `int districts, held, network, apart, floor, reach, onStreet, surface, beside, estate, mines, minesBeyond` |  |
| 755 | `long buildings, placed, overflow, overflowPlots, needPlots, streets, tracks, seams, under, surplusDistricts` |  |
| 756 | `double surplus, worstFill, worstLost` |  |
| 757 | `String worstWhere` |  |
| 758 | `final long[] cellStreets` |  |
| 759 | `final int[] cellCount` |  |
| 760 | `final List<Double> planMs` |  |
| 761 | `boolean timed` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 78 | 1162 | **type** `public class PlanCheck` | The district plan: one street network, + junctions no nearer than eight plots, every building within reach and none on a street or beside a highway, and the model's road drawn exactly as the streets' surface - on the ... |
| 83 | 4 | `static void check(String label, boolean ok)` |  |
| 214 | 26 | `public static void main(String[] args) throws Exception` |  |

### THE FIGURES OF ONE PLAN (lines 241-537)

| line | len | member | says |
|---:|---:|---|---|
| 246 | 32 | **type** `static final class Figures` | What a plan draws, and its checks. |
| 259 | 7 | `boolean surfaceHolds(DistrictPlan p, DistrictPlan.Input in)` _(in PlanCheck.Figures)_ | Whether the surface is the road by kind to within half a plot, and with the surplus exactly. |
| 268 | 4 | `boolean holds(DistrictPlan p, DistrictPlan.Input in)` _(in PlanCheck.Figures)_ | Whether every check holds: one network (or the ground parts it), the + floor, reach, none on a street, the surface, none beside a highway, T junctions only in an estate cell. |
| 274 | 3 | `boolean estateHolds()` _(in PlanCheck.Figures)_ | Whether its estate cells keep ESTATE LINES (0.7.90): no + inside one, its streets on their lines. |
| 280 | 151 | `static Figures figures(DistrictPlan p, DistrictPlan.Input in)` | A plan's figures and checks. |
| 432 | 4 | `static boolean onLattice(int q)` |  |
| 437 | 3 | `static boolean lat(boolean[] st, int q)` |  |
| 442 | 22 | `static int[] reach(boolean[] st)` | Each plot's distance from a street across corners, up to REACH; 99 beyond. |
| 473 | 26 | `static boolean groundParts(DistrictPlan p, DistrictPlan.Input in, int[] lab, List<Integer> sizes)` | Whether the ground parts a plan's street pieces: no way from its largest piece to another over the city's own ground - dry, fresh water no wider along the way than its line may bridge (an arterial's on an arterial's l... |
| 501 | 6 | `static int waterRun(DistrictPlan.Input in, int a, int d)` | Fresh water's run at plot a along direction d's axis. |
| 509 | 7 | `static long placedDigest(DistrictPlan p)` | A digest of a plan's buildings, in the order placed: x, y, across, down and type of each (FNV-1a over 64 bits). |
| 518 | 10 | `static long streetDigest(DistrictPlan p)` | ...and of its streets, row by row: each plot, its width in half plots and its kind (track 0, gravel 1, paved 2). |
| 530 | 7 | `static String line(DistrictPlan p, Figures f)` | One line of a plan's figures. |

### 1. THE PORT: THE PROTOTYPE'S TEST DISTRICT, REPLAYED (lines 538-608)

| line | len | member | says |
|---:|---:|---|---|
| 543 | 38 | `static DistrictPlan.Input testDistrict(BuildingVisual.Type[] types, int budget, boolean prototype)` | The prototype's test district: its ground (TEST_GROUND, the district's frame), its buildings, and one of its three budgets (0 his roads, 1 the same trips paved, 2 half that). |
| 584 | 24 | `static void replay(BuildingVisual.Type[] types)` |  |

### 2. THE GAME'S RULES ON THE TEST DISTRICT (lines 609-646)

| line | len | member | says |
|---:|---:|---|---|
| 613 | 33 | `static void gameRules(BuildingVisual.Type[] types)` |  |

### 3. THE HIGHWAYS (H5) (lines 647-674)

| line | len | member | says |
|---:|---:|---|---|
| 651 | 23 | `static void highways(BuildingVisual.Type[] types)` |  |

### 4. HOW FULL A DISTRICT THE PLAN CAN DRAW (spec 2.10) (lines 675-747)

| line | len | member | says |
|---:|---:|---|---|
| 680 | 25 | `static void scaled(DistrictPlan.Input in, long[] counts, int dry, double fill, boolean pavedCity)` | Jerus's mix (MapCheck.JERUS_COUNTS) scaled to a share of a district's dry plots, his roads with it - the prototype's scaled_buildings, its fractions by the world's hash. |
| 706 | 38 | `static void howFull(BuildingVisual.Type[] types)` |  |
| 745 | 3 | `static double pct(long a, long b)` |  |

### THE DISTRICTS OF A CITY: EVERY ONE PLANNED, ITS CHECKS AND FIGURES (lines 748-866)

| line | len | member | says |
|---:|---:|---|---|
| 753 | 113 | **type** `static final class Tally` | Every district of a map planned: how many keep each check, what they draw, what has no place, and what a plan costs. |
| 764 | 19 | `void add(CityMap m, CityMap.District d, boolean time)` _(in PlanCheck.Tally)_ | District d of map m planned (and timed, TIMED times after one untimed, when `time`). |
| 784 | 46 | `void add(DistrictPlan p, DistrictPlan.Input in, String where)` _(in PlanCheck.Tally)_ |  |
| 831 | 6 | `static double median(List<Double> v)` _(in PlanCheck.Tally)_ |  |
| 838 | 5 | `static double max(List<Double> v)` _(in PlanCheck.Tally)_ |  |
| 844 | 16 | `void report(String what)` _(in PlanCheck.Tally)_ |  |
| 862 | 3 | `boolean allHold()` _(in PlanCheck.Tally)_ | Whether every check holds on every district (one network where the ground does not part it). |

### 5. MAPCHECK'S FIXTURE OF JERUS'S CITY, AND THE DENSE SCREEN (lines 867-927)

| line | len | member | says |
|---:|---:|---|---|
| 872 | 16 | `static CityMap squareCity(BuildingVisual.Type[] types, long seed, long[] place, double k)` | Jerus's city x k on the design's square city at the dry place, as MapCheck's copies (MapCheck.Squares): his mines no more than its iron sites. |
| 889 | 38 | `static void fixture(BuildingVisual.Type[] types)` |  |

### 6. A CITY PLAYED AS THE PLAYTEST PLAYS IT (lines 928-981)

| line | len | member | says |
|---:|---:|---|---|
| 937 | 44 | `static void played() throws Exception` |  |

### 7. ESTATE CELLS LAID TO FIT WHAT THEY HOLD (0.7.90, batch RD5) (lines 982-1073)

| line | len | member | says |
|---:|---:|---|---|
| 990 | 11 | `static DistrictPlan.Input estateAlone(BuildingVisual.Type[] types, int type, int count, boolean prototype)` | Each estate-band type on flat owned ground, `count` of it alone, with the game's rules or the prototype's (one spine across every estate cell). |
| 1003 | 20 | `static long[] estateShare(DistrictPlan p, DistrictPlan.Input in)` | Box plots in a plan's estate cells and their dry ground (the cell's tile, its own arterials with it), and how many it opened. |
| 1024 | 49 | `static void estateLines(BuildingVisual.Type[] types)` |  |

### 8. INDUSTRY AND THE OUTER KINDS SHARE ESTATE CELLS (0.7.92, batch RD6) (lines 1074-1160)

| line | len | member | says |
|---:|---:|---|---|
| 1082 | 3 | `static int band(BuildingVisual.Type t)` | A building's band in the plan (spec 2.4): 0 what follows people, 1 industry, 2 the outer kinds - as DistrictPlan deals them. |
| 1087 | 11 | `static int outerInIndustry(DistrictPlan p, BuildingVisual.Type[] types)` | The outer-kind buildings standing in a cell whose first building is industry's: industry's cells they share. |
| 1100 | 11 | `static DistrictPlan.Input flatMix(BuildingVisual.Type[] types, double fill, boolean roads, boolean apart)` | Jerus's mix on a flat district at a fill (section 4's), with his roads or none, the bands sharing or apart. |
| 1112 | 48 | `static void sharedEstates(BuildingVisual.Type[] types)` |  |

### 9. A SAVE NAMED ON THE COMMAND LINE: EVERY DISTRICT (lines 1161-1239)

| line | len | member | says |
|---:|---:|---|---|
| 1166 | 73 | `static void save(Path dir) throws Exception` | A save folder's autosave, loaded from a copy (a game run autosaves into any city it loads): every district planned, checked and timed. |

