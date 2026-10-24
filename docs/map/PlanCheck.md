# PlanCheck.java - 3,859 lines · 129 methods · 47 constants · harnesses

`ham/citybuildersim/PlanCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

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
>      and a city played as the playtest plays it shows the variety firing;
>  11. the order of arrival (batch CL3; DistrictPlan's THE ORDER OF ARRIVAL):
> ... (81 more lines in the source)

**Uses:** [DistrictPlan](DistrictPlan.md) (918), [BuildingVisual](BuildingVisual.md) (84), [CityMap](CityMap.md) (43), [World](World.md) (35), [MapCheck](MapCheck.md) (30), [BuildingType](BuildingType.md) (28), [LongPlaytest](LongPlaytest.md) (16), [Founding](Founding.md) (11), [CityRuns](CityRuns.md) (9), [TilePainter](TilePainter.md) (8), [GameFiles](GameFiles.md) (5), [StreetCurves](StreetCurves.md) (5), [Timing](Timing.md) (4), [Game](Game.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [Mining](Mining.md) (3), [TempDirs](TempDirs.md) (2), [MiningCheck](MiningCheck.md) (2), [TreasuryFund](TreasuryFund.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [TrackCurves](TrackCurves.md) (2), [Resource](Resource.md) (1)

**Used by (2):** [EarthTerrainCheck](EarthTerrainCheck.md), [JunctionCheck](JunctionCheck.md)

## Sections

| line | section |
|---:|---|
| 337 | THE FIGURES OF ONE PLAN |
| 544 | THE AVENUES, THE HARNESS'S OWN WORKING (batch CL4) |
| 767 | 1. THE PORT: THE PROTOTYPE'S TEST DISTRICT, REPLAYED |
| 839 | 2. THE GAME'S RULES ON THE TEST DISTRICT |
| 877 | 3. THE HIGHWAYS (H5) |
| 905 | 4. HOW FULL A DISTRICT THE PLAN CAN DRAW (spec 2.10) |
| 978 | THE DISTRICTS OF A CITY: EVERY ONE PLANNED, ITS CHECKS AND FIGURES |
| 1130 | 5. MAPCHECK'S FIXTURE OF JERUS'S CITY, AND THE DENSE SCREEN |
| 1211 | 6. A CITY PLAYED AS THE PLAYTEST PLAYS IT |
| 1278 | 7. ESTATE CELLS LAID TO FIT WHAT THEY HOLD (0.7.90, batch RD5) |
| 1372 | 8. INDUSTRY AND THE OUTER KINDS SHARE ESTATE CELLS (0.7.92, batch RD6) |
| 1459 | 10. HOMES CELLS' STREETS BY A HASH OF THEIR PLACE (CL2) |
| 1820 | 11. THE ORDER OF ARRIVAL (batch CL3) |
| 2429 | 12. THE GROUND BOUGHT (batch FX6) |
| 2542 | 13. THE AVENUES (batch CL4) |
| 2905 | 14. THE CURVES (batch CL4, slice C) |
| 3580 | 9. A SAVE NAMED ON THE COMMAND LINE: EVERY DISTRICT |
| 3675 | 15. THE RUNS DATED (batch FX7) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 168 | `PlanCheck.TIMED` | `5` | Repeats a district's plan is timed over, after one untimed: its median is its time. |
| 179 | `PlanCheck.PLAN_MS` | `MapCheck.SCREEN_MS / 2` | The most a district's plan may take, the slowest of the dense screen's districts (each its median of TIMED): half MapCheck's screen, 40 ms (star) - one plan and a screen painted from plans within SCREEN_MS, so long as... |
| 182 | `PlanCheck.TABLE` | `{ { 49, 15, 9802, 828, 8974, 0, 13, 26, 280, 2995 }, { 48, 13, 6052, 3137, 29...` | The prototype's figures of the spec's 2.5 table, for his roads, the same trips paved and half that: cells, homes cells, street plots, narrow, full, tracks, square cells, boulevard cells, + junctions, buildings touchin... |
| 188 | `PlanCheck.TABLE_BUILDINGS` | `3617, TABLE_BUILDING_PLOTS = 32217` | ...its buildings and their plots... |
| 190 | `PlanCheck.TABLE_BUDGET` | `{ 9388, 4484, 2242 }` | ...and the road of each budget, whole plots. |
| 193 | `PlanCheck.TEST_HUB_X` | `66, TEST_HUB_Y = 86` | The prototype's hub on its test district, in the district's plots: (70, 90) in its frame, 4 plots in. |
| 196 | `PlanCheck.TEST_UNOWNED_ROWS` | `40, TEST_UNOWNED_FROM = 196` | The part of the test district the prototype did not own: its first 40 rows east of plot 196 (O[:44, 200:] in its frame). |
| 199 | `PlanCheck.FILLS` | `{ 0.75, 0.80, 0.85, 0.92 }` | The fills of 2.10's table: the model's buildings and roads at these shares of a flat district's ground. |
| 202 | `PlanCheck.PROTO_FRAME` | `264` | The ground 2.10's fills are shares of: the prototype's frame, the district and 4 plots about it, 264 x 264 (its n = OFF + 32 x 8 + 4) - so its 75% is 79.8% of the district's own 256 x 256. |
| 205 | `PlanCheck.GRAVEL_TRIPS` | `900, PAVED_TRIPS = 1200` | Gravel's and paved road's trips a road (buildings.json's capacities, as the prototype read them): the same city paved holds his roads' trips on Paved Roads. |
| 215 | `PlanCheck.TEST_GROUND` | `"g36f14g13f1g14f29g89u61/g36f15g11f2g14f28g90u61/g35f17g10f2g13f29g90u61/g35f...` | THE PROTOTYPE'S TEST DISTRICT (claude/roads-prototype/proto2.py and roads_proto.py, written out by batch RD1): district_ground(264, 7) - grass, forest, a river two to four plots wide, a bay of sea - its district's fra... |
| 258 | `PlanCheck.TEST_NUDGE` | `{ 0.3727494347187473, 0.03906684873305479, 0.22912251263578634, 0.52450302674...` | Each cell's nudge, h32(4127, 'cell', c) as a fraction, cell ci + cj x CELLS_A_SIDE. |
| 276 | `PlanCheck.TEST_ACROSS` | `2483539012620168834L` | Each cell's direction, a bit a cell: set where h32(4127, 'dir', c) is under a half (east-west), read as 0.25, else 0.75. |
| 278 | `PlanCheck.TEST_TYPES` | `{ 0, 1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 17, 18, 19, 20, 22, 23, 25, 27, 28, 31,...` | The types its buildings are, by id (BuildingVisual.table())... |
| 280 | `PlanCheck.TEST_COUNTS` | `{ 899, 94, 569, 30, 2, 799, 2, 1, 2, 23, 5, 112, 54, 1, 1, 13, 6, 4, 1, 2, 22...` | ...how many of each, scaled_buildings at 70% of its dry ground... |
| 282 | `PlanCheck.TEST_DEAL` | `{ 0.8623860022826665, 0.4688361493186886, 0.49244847032280087, 0.910102558251...` | ...and each type's start, h32(4127, 'deal', t) as a fraction. |
| 296 | `PlanCheck.TEST_GRAVEL` | `8407.741935483871, TEST_PAVED = 980.6451612903226, TEST_PAVED_ALL = 4483.8709...` | Its road plots (Input.gravel and Input.paved): his Gravel and Paved Roads, and the same trips all paved (the third budget half of it). |
| 298 | `PlanCheck.TEST_PLACED_JERUS` | `- 1980402857778823524L, TEST_STREETS_JERUS = - 7143981648789130548L` | Each budget's plan as the prototype made it, its buildings' and its streets' FNV-1a digests (placedDigest(), streetDigest()): his roads... |
| 300 | `PlanCheck.TEST_PLACED_PAVED` | `9000504916410474359L, TEST_STREETS_PAVED = - 4283127041626870812L` | ...the same trips paved... |
| 302 | `PlanCheck.TEST_PLACED_THIN` | `9000504916410474359L, TEST_STREETS_THIN = 4092048347058802282L` | ...and half that. |
| 549 | `PlanCheck.ARM_DX` | `{ 1, - 1, - 1, 1 }, ARM_DY = { 1, 1, - 1, - 1 }` | The four avenues' headings (dx, dy) by arm, CityRuns.diagCode()'s numbering: south-east 0, south-west 1, north-west 2, north-east 3. |
| 812 | `PlanCheck.BUDGETS` | `{ "his roads", "the same trips paved", "half that" }` | The three budgets' names, as the sections print them. |
| 1216 | `PlanCheck.QUIET` | `new PrintStream(new OutputStream() { @ Override public void write(int b) { } ...` | A stream that drops what is written to it: the game's and the playtest's prose while a section plays or loads a city. |
| 1276 | `PlanCheck.HOMES_ENOUGH` | `10` | The homes cells a played city's samples must hold before they are asked to show a layout other than the comb (CL2): ten cells all keeping the comb by the hash alone has a chance of 1 in 59,049. |
| 1283 | `PlanCheck.ESTATE_FILL` | `0.4` | The share of a flat district's plots each estate-band type is set at, alone: 0.4 - room to spare, so what differs is the cells they take. |
| 1377 | `PlanCheck.SPARE_FILL` | `FILLS [ 0 ], FULL_FILL = FILLS [ FILLS.length - 1 ]` | Section 4's fill with cells to spare (2.10's first, 75%) and its fullest (92%: every cell open, and outer kinds left without a place when the bands keep apart). |
| 1464 | `PlanCheck.LONG_ROADS` | `0.5` | LONG's roads, a factor of his (section 4's 75% fill): 0.5 - too little road for the ladder to square a homes cell, so all of them keep long blocks. |
| 1467 | `PlanCheck.SQUARED_ROADS` | `0.7` | SQUARED's: 0.7 - road enough to square most of the homes cells and not to make boulevards of them. |
| 1470 | `PlanCheck.BOULEVARD_ROADS` | `1` | BOULEVARD's: 1, his own - road enough for boulevards, squared cells among them. |
| 1473 | `PlanCheck.LAYOUT_FIXTURES` | `{ "LONG", "SQUARED", "BOULEVARD", "CUT0", "CUT1" }` | Section 10's fixtures: flat ground at his fill with his roads times a factor (the first three), and the test district's ground, a river and the sea cutting cells, on its first two budgets. |
| 1476 | `PlanCheck.COMB_DIGESTS` | `{ { - 8123742265030361694L, - 580478996347052330L }, { 6065673935051394403L, ...` | Each fixture's plan with Input.combOnly, its buildings' and its streets' digests (placedDigest(), streetDigest()) as 0.8.11 drew them: the switch off is that plan, box for box and street for street. |
| 1484 | `PlanCheck.SHARE_SIDE` | `200` | The cells on a side of the square of the lattice layoutOf() is asked about to measure its shares: 200, 40,000 cells. |
| 1487 | `PlanCheck.SHARE_TOLERANCE` | `0.01` | How far a layout's share of those cells may stand from its dial: 0.01. |
| 1490 | `PlanCheck.MORE_HOMES` | `0.1` | The part more of the homes band's largest type LONG is given, to see a cell keep its layout as its district grows: a tenth. |
| 1493 | `PlanCheck.OTHER_SEED` | `Founding.DEFAULT_WORLD_SEED + 1` | Another world's seed, to see the layouts read it. |
| 2018 | `PlanCheck.BATCHES` | `40` | The batches the dense screen's counts are dealt in (h): forty months. |
| 2021 | `PlanCheck.COVER_LEAST` | `0.95` | What the order of arrival must cover of the ground today's plans cover on the dense screen: 95%. |
| 2444 | `PlanCheck.HOUSES_A_MONTH` | `1000` | The houses each month from the first to the fifth brings: with the shops, works and farms beside them, enough to fill the old ground. |
| 2913 | `PlanCheck.FAR_CELLS` | `(int) DistrictPlan.CRESCENT_FROM + 4` | How many cells west of the frame section 14's far fixture puts the founding site: CRESCENT_FROM and four more, so every cell of the frame lies beyond CRESCENT_FROM. |
| 2916 | `PlanCheck.CROWDED_FILL` | `0.90, CROWDED_ROADS = 0.7` | Section 14's crowded fixture: 90% of the ground, with 0.7 of his roads - the ladder takes several builds (the later ones from its turns), squares a few homes cells and leaves a few buildings out, and the far homes cel... |
| 2919 | `PlanCheck.ROAD_RICH` | `BOULEVARD_ROADS` | The roads section 14's road-rich fixture is given, a factor of his: BOULEVARD_ROADS, 1 - road enough for the ladder to square cells and make boulevards. |
| 2922 | `PlanCheck.MID_WEST` | `1.25` | Section 14 (b)'s site, in cells west of the frame's edge short of CRESCENT_FROM: 1.25 - the frame's first column of cells lies within CRESCENT_FROM of it and its second beyond. |
| 2925 | `PlanCheck.ARM_SITE` | `21 * DistrictPlan.CELL` | Section 14 (c)'s site for an avenue across the frame: (-ARM_SITE, -ARM_SITE), 21 cells out along the diagonal, so its south-east arm runs through the frame's diagonal cells - the ones nearest it, where the homes cells... |
| 3413 | `PlanCheck.HOUSES_A_MONTH_FAR` | `3000` | The houses each month brings in (f): enough to open a homes cell or three a month beyond CRESCENT_FROM. |
| 3530 | `PlanCheck.RIM_FROM` | `3` | The rim of the dense city (rimCurves()): districts this many or more from the one the founding site lies in, by the larger of their columns and rows. |
| 3533 | `PlanCheck.RIM_EVERY` | `37` | ...and of those, one in this many is planned: 37, a stride that takes districts all round it. |
| 3718 | `PlanCheck.RUN_HOUSES` | `250` | The houses each month of the runs' fixtures brings: with the shops, works and farms beside them, a district that grows by some hundreds of boxes a month. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 159 | `static int fails` |  |
| 160 | `static PrintStream out` |  |
| 343 | `int pieces, plus, closestPlus` |  |
| 344 | `int placed, overflow, buildingPlots, streetPlots, tracks, narrow, full, seams, under, minesBeyond` |  |
| 346 | `int estatePlus, estateLinesOff` | + junctions inside an estate cell's interior, and estate cells whose streets leave ESTATE LINES' lines (0.7.90). |
| 348 | `int avenueBad, avenueOuter` | (CL4) pairs of junctions along an avenue fewer than LATTICE steps apart, and buildings with a plot on an avenue's outer plot. |
| 349 | `boolean groundParts` |  |
| 351 | `double gravel, paved, highway` | Surface drawn by kind: gravel, paved, a highway's. |
| 353 | `final long[] cellStreets` | Street plots and dry ground in the open cells' tiles: homes cells of long blocks, of square blocks, estate cells; and the boulevard cells. |
| 354 | `final int[] cellCount` |  |
| 554 | `int ox, oy` | The origin in the frame's plots: the lattice node nearest the founding site. |
| 556 | `final byte[] mark` | Per plot, the mark an avenue plot there carries (the first arm's where two share it), else 0. |
| 558 | `final boolean[] outer` | ...and the outer plots, where no building stands. |
| 560 | `final int[] from` | Each arm's first and last step to look at: those whose plots can touch the frame. |
| 984 | `int districts, held, network, apart, floor, reach, onStreet, surface, beside, estate, mines, minesBeyond` |  |
| 985 | `long buildings, placed, overflow, overflowPlots, needPlots, streets, tracks, seams, under, surplusDistricts` |  |
| 986 | `double surplus, worstFill, worstLost` |  |
| 987 | `String worstWhere` |  |
| 988 | `final long[] cellStreets` |  |
| 989 | `final int[] cellCount` |  |
| 991 | `final int[][] homes` | The open homes cells by kind (0 long blocks, 1 squared) and layout (CL2), and the merges undone in them. |
| 992 | `long mergesUndone` |  |
| 994 | `final long[] av` | The avenues' counts summed (CL4): plots laid, beneath a highway, across the railway, bridging, not laid at a yard, reserve or mine, at water too wide, stops, open cells crossed; and the floor and outer-plot breaches. |
| 995 | `int avenueBad, avenueOuter` |  |
| 997 | `final long[] cv` | The curves' counts summed (CL4, slice C): crescents, crescents the ground would not lay whole, streets along a cut drawn as curves, curves in all. |
| 998 | `final List<Double> planMs` |  |
| 999 | `boolean timed` |  |
| 1153 | `static CityMap denseCity` | The dense copy of his city (MapCheck.DENSE) and the districts its screen shows, made once and kept: section 5 times their plans and section 11 deals their counts as a log. |
| 1154 | `static List<CityMap.District> denseShown` |  |
| 1829 | `final List<int[]> rec` |  |
| 1830 | `int firstBatch` |  |
| 1831 | `int[] carry` |  |
| 1892 | `static boolean pinLattice` | Whether flat() lays no avenue (Input.latticeOnly): set for a fixture whose premise is a plan from before the avenues (CL4). |
| 2000 | `static boolean arrivalRules` | Whether every plan this section has made keeps every check (figures().holds()): each plan goes through plan(). |
| 2001 | `static int arrivalPlans` |  |
| 2547 | `static long[] x1Avenues, playedAvenues` | The avenues' counts of section 5's city x 1 and section 6's played city, summed over their districts (Tally.av), for (i). |
| 2910 | `static long[] x1Curves, playedCurves` | The curves' counts of section 5's city x 1 and section 6's played city, summed over their districts (Tally.cv): crescents, crescents the ground refused, streets along a cut drawn as curves, curves in all. |
| 2947 | `final int bulge, a` |  |
| 2948 | `final double cu, cv, r` |  |
| 2949 | `final boolean[][] stair` |  |
| 2950 | `final boolean[][] tight` |  |
| 2951 | `double firstV, lastV` |  |
| 3071 | `boolean wrongLayout, exact` |  |
| 3072 | `int homes, squared, crescents, wanted, mergedElsewhere, wideBoxes, wideInCrescent, onBand, acrossCells, alo...` |  |
| 3073 | `final int[] bulges` |  |
| 3074 | `String first` |  |
| 3684 | `final List<int[]> rec` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 157 | 3703 | **type** `public class PlanCheck` | The district plan: one street network, + junctions no nearer than eight plots, every building within reach and none on a street or beside a highway, and the model's road drawn exactly as the streets' surface - on the ... |
| 162 | 4 | `static void check(String label, boolean ok)` |  |
| 304 | 32 | `public static void main(String[] args) throws Exception` |  |

### THE FIGURES OF ONE PLAN (lines 337-543)

| line | len | member | says |
|---:|---:|---|---|
| 342 | 34 | **type** `static final class Figures` | What a plan draws, and its checks. |
| 357 | 7 | `boolean surfaceHolds(DistrictPlan p, DistrictPlan.Input in)` _(in PlanCheck.Figures)_ | Whether the surface is the road by kind to within half a plot, and with the surplus exactly. |
| 366 | 4 | `boolean holds(DistrictPlan p, DistrictPlan.Input in)` _(in PlanCheck.Figures)_ | Whether every check holds: one network (or the ground parts it), the + floor, reach, none on a street, the surface, none beside a highway, T junctions only in an estate cell. |
| 372 | 3 | `boolean estateHolds()` _(in PlanCheck.Figures)_ | Whether its estate cells keep ESTATE LINES (0.7.90): no + inside one, its streets on their lines. |
| 378 | 165 | `static Figures figures(DistrictPlan p, DistrictPlan.Input in)` | A plan's figures and checks. |

### THE AVENUES, THE HARNESS'S OWN WORKING (batch CL4) (lines 544-766)

| line | len | member | says |
|---:|---:|---|---|
| 552 | 10 | **type** `static final class Stairs` | An input's avenues as the harness works them out from the founding site (CL4) - not by calling the plan's code: the origin, each staircase plot's mark in CityRuns' bits and the outer plots. |
| 564 | 5 | `static void mark(Stairs s, int a, int x, int y, int corner)` | The harness's staircase plot at (x, y) of arm a, a corner or not; kept if the frame holds it and no earlier arm marked it. |
| 571 | 23 | `static Stairs stairs(DistrictPlan.Input in)` | An input's avenues, worked out here (CL4): step k >= 0 of an arm is S_k = (ox + dx k, oy + dy k), for k >= 1 its corner C_k = (ox + dx k, oy + dy (k - 1)) and its outer plot O_k = (ox + dx (k - 1), oy + dy k). |
| 596 | 10 | `static boolean inOpenBox(DistrictPlan p, int x, int y)` | Whether plot (x, y) lies in the box of a cell the plan opened - its interior or one of its ring lines. |
| 608 | 5 | `static boolean avenueInCell(Stairs s, int ci, int cj)` | Whether the harness's staircase has a plot in the interior of the cell (ci, cj). |
| 621 | 32 | `static int[] junctions(DistrictPlan p, Stairs s, int a, boolean[] laid, int[] cellStreets)` | The junctions of arm a (CL4's + floor), ascending and distinct, by the harness's own walk: step j has one when a street plot that is no avenue's - role ROLE_STREET, or ROLE_ARTERIAL or ROLE_BOULEVARD on the ring line ... |
| 655 | 5 | `static int floorBad(int[] steps)` | The pairs of consecutive junctions fewer than LATTICE steps apart. |
| 661 | 4 | `static boolean onLattice(int q)` |  |
| 666 | 3 | `static boolean lat(boolean[] st, int q)` |  |
| 671 | 22 | `static int[] reach(boolean[] st)` | Each plot's distance from a street across corners, up to REACH; 99 beyond. |
| 702 | 26 | `static boolean groundParts(DistrictPlan p, DistrictPlan.Input in, int[] lab, List<Integer> sizes)` | Whether the ground parts a plan's street pieces: no way from its largest piece to another over the city's own ground - dry, fresh water no wider along the way than its line may bridge (an arterial's on an arterial's l... |
| 730 | 6 | `static int waterRun(DistrictPlan.Input in, int a, int d)` | Fresh water's run at plot a along direction d's axis. |
| 738 | 7 | `static long placedDigest(DistrictPlan p)` | A digest of a plan's buildings, in the order placed: x, y, across, down and type of each (FNV-1a over 64 bits). |
| 747 | 10 | `static long streetDigest(DistrictPlan p)` | ...and of its streets, row by row: each plot, its width in half plots and its kind (track 0, gravel 1, paved 2). |
| 759 | 7 | `static String line(DistrictPlan p, Figures f)` | One line of a plan's figures. |

### 1. THE PORT: THE PROTOTYPE'S TEST DISTRICT, REPLAYED (lines 767-838)

| line | len | member | says |
|---:|---:|---|---|
| 772 | 38 | `static DistrictPlan.Input testDistrict(BuildingVisual.Type[] types, int budget, boolean prototype)` | The prototype's test district: its ground (TEST_GROUND, the district's frame), its buildings, and one of its three budgets (0 his roads, 1 the same trips paved, 2 half that). |
| 814 | 24 | `static void replay(BuildingVisual.Type[] types)` |  |

### 2. THE GAME'S RULES ON THE TEST DISTRICT (lines 839-876)

| line | len | member | says |
|---:|---:|---|---|
| 843 | 33 | `static void gameRules(BuildingVisual.Type[] types)` |  |

### 3. THE HIGHWAYS (H5) (lines 877-904)

| line | len | member | says |
|---:|---:|---|---|
| 881 | 23 | `static void highways(BuildingVisual.Type[] types)` |  |

### 4. HOW FULL A DISTRICT THE PLAN CAN DRAW (spec 2.10) (lines 905-977)

| line | len | member | says |
|---:|---:|---|---|
| 910 | 25 | `static void scaled(DistrictPlan.Input in, long[] counts, int dry, double fill, boolean pavedCity)` | Jerus's mix (MapCheck.JERUS_COUNTS) scaled to a share of a district's dry plots, his roads with it - the prototype's scaled_buildings, its fractions by the world's hash. |
| 936 | 38 | `static void howFull(BuildingVisual.Type[] types)` |  |
| 975 | 3 | `static double pct(long a, long b)` |  |

### THE DISTRICTS OF A CITY: EVERY ONE PLANNED, ITS CHECKS AND FIGURES (lines 978-1129)

| line | len | member | says |
|---:|---:|---|---|
| 983 | 146 | **type** `static final class Tally` | Every district of a map planned: how many keep each check, what they draw, what has no place, and what a plan costs. |
| 1002 | 19 | `void add(CityMap m, CityMap.District d, boolean time)` _(in PlanCheck.Tally)_ | District d of map m planned (and timed, TIMED times after one untimed, when `time`). |
| 1022 | 54 | `void add(DistrictPlan p, DistrictPlan.Input in, String where)` _(in PlanCheck.Tally)_ |  |
| 1078 | 5 | `int varied()` _(in PlanCheck.Tally)_ | The open homes cells that took a layout other than the comb or the whole square. |
| 1085 | 3 | `int homesCells()` _(in PlanCheck.Tally)_ | All the open homes cells. |
| 1089 | 6 | `static double median(List<Double> v)` _(in PlanCheck.Tally)_ |  |
| 1096 | 5 | `static double max(List<Double> v)` _(in PlanCheck.Tally)_ |  |
| 1102 | 21 | `void report(String what)` _(in PlanCheck.Tally)_ |  |
| 1125 | 3 | `boolean allHold()` _(in PlanCheck.Tally)_ | Whether every check holds on every district (one network where the ground does not part it). |

### 5. MAPCHECK'S FIXTURE OF JERUS'S CITY, AND THE DENSE SCREEN (lines 1130-1210)

| line | len | member | says |
|---:|---:|---|---|
| 1135 | 16 | `static CityMap squareCity(BuildingVisual.Type[] types, long seed, long[] place, double k)` | Jerus's city x k on the design's square city at the dry place, as MapCheck's copies (MapCheck.Squares): his mines no more than its iron sites of their type (since 0.8.0, an Iron Mine's hematite). |
| 1157 | 16 | `static void denseScreen(BuildingVisual.Type[] types)` | denseCity and denseShown made, if they are not. |
| 1174 | 36 | `static void fixture(BuildingVisual.Type[] types)` |  |

### 6. A CITY PLAYED AS THE PLAYTEST PLAYS IT (lines 1211-1277)

| line | len | member | says |
|---:|---:|---|---|
| 1221 | 53 | `static void played() throws Exception` |  |

### 7. ESTATE CELLS LAID TO FIT WHAT THEY HOLD (0.7.90, batch RD5) (lines 1278-1371)

| line | len | member | says |
|---:|---:|---|---|
| 1286 | 13 | `static DistrictPlan.Input estateAlone(BuildingVisual.Type[] types, int type, int count, boolean prototype)` | Each estate-band type on flat owned ground, `count` of it alone, with the game's rules or the prototype's (one spine across every estate cell). |
| 1301 | 20 | `static long[] estateShare(DistrictPlan p, DistrictPlan.Input in)` | Box plots in a plan's estate cells and their dry ground (the cell's tile, its own arterials with it), and how many it opened. |
| 1322 | 49 | `static void estateLines(BuildingVisual.Type[] types)` |  |

### 8. INDUSTRY AND THE OUTER KINDS SHARE ESTATE CELLS (0.7.92, batch RD6) (lines 1372-1458)

| line | len | member | says |
|---:|---:|---|---|
| 1380 | 3 | `static int band(BuildingVisual.Type t)` | A building's band in the plan (spec 2.4): 0 what follows people, 1 industry, 2 the outer kinds - as DistrictPlan deals them. |
| 1385 | 11 | `static int outerInIndustry(DistrictPlan p, BuildingVisual.Type[] types)` | The outer-kind buildings standing in a cell whose first building is industry's: industry's cells they share. |
| 1398 | 11 | `static DistrictPlan.Input flatMix(BuildingVisual.Type[] types, double fill, boolean roads, boolean apart)` | Jerus's mix on a flat district at a fill (section 4's), with his roads or none, the bands sharing or apart. |
| 1410 | 48 | `static void sharedEstates(BuildingVisual.Type[] types)` |  |

### 10. HOMES CELLS' STREETS BY A HASH OF THEIR PLACE (CL2) (lines 1459-1819)

| line | len | member | says |
|---:|---:|---|---|
| 1496 | 13 | `static DistrictPlan.Input layoutInput(BuildingVisual.Type[] types, int k, boolean combOnly)` | Section 10's fixture k (LAYOUT_FIXTURES): the flat district with his roads times a factor, or the test district's ground and budget k - 3; with combOnly when asked. |
| 1511 | 5 | `static int[][] homesByLayout(DistrictPlan p)` | A plan's open homes cells counted by kind (0 long blocks, 1 squared; a boulevard's interior by whether it is squared) and layout. |
| 1518 | 3 | `static String layoutLine(int[][] n)` | Homes cells by kind and layout as a line. |
| 1523 | 11 | `static int[] homesKinds(DistrictPlan p)` | A plan's open homes cells: how many, how many squared, how many boulevards, how many of those squared, and how many squared and not boulevards. |
| 1536 | 22 | `static int deadEnds(DistrictPlan p, boolean variedOnly)` | The dead ends of a plan: street plots (any kind but none) of the frame's interior, not its first or last row or column, with at most one street among their four neighbours - only those inside the interior of a homes c... |
| 1560 | 19 | `static long[][] homesStreetShare(DistrictPlan p, DistrictPlan.Input in)` | Street plots and dry ground in the open homes cells' tiles (a cell's interior and its own west and north arterials, as Figures.cellStreets has them), by kind and layout: row kind x 3 + layout, {street plots, dry ground}. |
| 1581 | 4 | `static String shareLine(long[][] s)` | The street share of homesStreetShare() as a line, a layout of a kind at a time. |
| 1587 | 4 | `static boolean allComb(DistrictPlan p)` | Whether every cell of a plan keeps LAYOUT_TODAY: the comb, the whole square, an estate. |
| 1593 | 9 | `static boolean crossedCell(DistrictPlan.Input in, int ci, int cj)` | Whether a highway, railway, yard or reserve (Input.fixed) has a plot in the box of the cell (ci, cj), its interior and its ring, or an avenue one in its interior: DistrictPlan's crossed(), such a homes cell keeping th... |
| 1604 | 5 | `static byte expectedLayout(DistrictPlan p, DistrictPlan.Input in, int c)` | The layout cell c of a plan has to have: its place's (layoutOf of the seed and the cell's north-west plot in the world) for a homes cell no highway, railway, yard or reserve crosses, else LAYOUT_TODAY. |
| 1610 | 209 | `static void streetLayouts(BuildingVisual.Type[] types)` |  |

### 11. THE ORDER OF ARRIVAL (batch CL3) (lines 1820-2428)

| line | len | member | says |
|---:|---:|---|---|
| 1828 | 62 | **type** `static final class Log` | A log built by hand (CL3): records {month, type, change} in order, how many of the first are one batch, and a carry of {month, type} pairs - what CityMap.District's log is to a plan, written down in a fixture. |
| 1833 | 4 | `Log add(int month, int type, int change)` _(in PlanCheck.Log)_ |  |
| 1839 | 4 | `Log batch()` _(in PlanCheck.Log)_ | The records so far are the first batch. |
| 1845 | 7 | `Log until(int m)` _(in PlanCheck.Log)_ | The log as it stood at the end of month m: its records of that month or before (the first batch kept), the same carry. |
| 1854 | 5 | `Log copy()` _(in PlanCheck.Log)_ | A copy that goes on from here. |
| 1861 | 12 | `DistrictPlan.Input into(DistrictPlan.Input in)` _(in PlanCheck.Log)_ | Input `in` given this log, as its arrays and as the counts and road they come to (the drawn types' sums; the road types' plots by kind). |
| 1875 | 14 | `DistrictPlan.Input attach(DistrictPlan.Input in)` _(in PlanCheck.Log)_ | Input `in` given this log's arrays alone: its counts and road are what it has. |
| 1895 | 10 | `static DistrictPlan.Input flat(BuildingVisual.Type[] types)` | A flat district, every plot owned, the hub at its middle: the order of arrival's fixture. |
| 1907 | 8 | `static DistrictPlan.Input input(BuildingVisual.Type[] types, Log log, boolean withLog)` | The flat district with a log, or (`withLog` false) with the counts and road it comes to and no log - today's plan from them, the carry joined. |
| 1917 | 9 | `static int pick(BuildingVisual.Type[] types, int least, java.util.function.Predicate<BuildingVisual.Type> ok)` | The first type, by id, of the smallest footprint no smaller than `least` plots that `ok` holds of; -1 for none. |
| 1928 | 9 | `static int pickLargest(BuildingVisual.Type[] types, java.util.function.Predicate<BuildingVisual.Type> ok)` | The first type, by id, of the largest footprint that `ok` holds of. |
| 1939 | 4 | `static int pavedRoad(BuildingVisual.Type[] types)` | The paved road type: the first whose road is paved. |
| 1945 | 5 | `static java.util.Set<Long> boxes(DistrictPlan p)` | A plan's boxes, each packed from its place, size and type. |
| 1951 | 3 | `static long boxKey(DistrictPlan p, int i)` |  |
| 1956 | 6 | `static int missing(DistrictPlan a, DistrictPlan b)` | How many of a's boxes b does not have: those that moved or went. |
| 1964 | 5 | `static long boxPlots(DistrictPlan p)` | A plan's box total: its buildings' plots. |
| 1971 | 4 | `static int cellOfRank(DistrictPlan p, int r)` | The cell with rank r among those a plan opened, or -1. |
| 1977 | 5 | `static int cellsOpened(DistrictPlan p)` | The cells a plan opened (CLOSED aside). |
| 1984 | 3 | `static int cellOfBox(DistrictPlan p, int i)` | The cell a box of a plan stands in. |
| 1989 | 3 | `static int cellApart(int a, int b)` | How many cells apart two cells are, the larger of their columns' and rows' (the eight about a cell are 1). |
| 1994 | 4 | `static boolean cornerCell(int c)` | Whether cell c is at the corner of the district's grid of cells. |
| 2004 | 6 | `static DistrictPlan plan(DistrictPlan.Input in)` | The plan of an input, its checks held to account. |
| 2012 | 4 | `static void siteIn(DistrictPlan.Input in, int c)` | A site plot in the middle of cell c's interior (a resource's field, SITE_FIELD). |
| 2024 | 3 | `static double slowest(List<Double> ms)` | The slowest of a list of times. |
| 2028 | 354 | `static void arrivals(BuildingVisual.Type[] types)` |  |
| 2384 | 5 | `static Log chained(Log own, int[] carry)` | A copy of a log with the carry `carry` in. |
| 2391 | 6 | `static int[] withoutPair(int[] list, int k)` | A carry with its k-th pair taken out of the list's middle, as a departure of a building passed on once did (before the carry was append-only). |
| 2399 | 5 | `static int boxesOfType(DistrictPlan p, int t)` | How many of a plan's boxes are of type t. |
| 2406 | 7 | `static String monthsOf(int[] log)` | The months of an overflow log, as counts: "month x count, ...". |
| 2415 | 13 | `static Log dealt(DistrictPlan.Input in, CityMap.District d, int batches)` | Each dense-screen district's counts and road dealt over BATCHES months: batch k a share of each type, the first batch the first (CL3). |

### 12. THE GROUND BOUGHT (batch FX6) (lines 2429-2541)

| line | len | member | says |
|---:|---:|---|---|
| 2434 | 8 | `static DistrictPlan.Input cellsOwned(BuildingVisual.Type[] types, int ci0, int ci1, int cj0, int cj1)` | A flat district that owns whole cells ci0 to ci1 across and cj0 to cj1 down, their rings too, the hub at its middle: the ground bought's fixture. |
| 2447 | 5 | `static int countOwned(DistrictPlan.Input in)` | The plots an input owns. |
| 2454 | 4 | `static DistrictPlan.Input westOf(DistrictPlan.Input in, int x)` | The input with the ground east of plot column x not owned. |
| 2460 | 5 | `static int[] boughtOn(DistrictPlan.Input grown, DistrictPlan.Input old, int month)` | The months the ground of `grown` was bought in: `month` for each plot it owns that `old` does not, 0 for the rest. |
| 2467 | 4 | `static boolean samePlan(DistrictPlan a, DistrictPlan b)` | Whether two plans are the same plan: every box in the order placed, every street, what was left out. |
| 2473 | 5 | `static int boxesEastOf(DistrictPlan p, int x)` | How many of a plan's boxes lie wholly east of plot column x (the cells bought are east of the old ground). |
| 2479 | 62 | `static void groundBought(BuildingVisual.Type[] types)` |  |

### 13. THE AVENUES (batch CL4) (lines 2542-2904)

| line | len | member | says |
|---:|---:|---|---|
| 2550 | 9 | `static boolean allArterial(DistrictPlan p)` | Whether the plan's avenue plot q is a street of an arterial's role (ROLE_ARTERIAL, or ROLE_BOULEVARD on a boulevard's second row). |
| 2561 | 8 | `static boolean headingsOk()` | Whether CityRuns' heading numbering gives each arm the (dx, dy) and diagCode the harness uses: the arm in a mark is the one CityRuns' own stretches carry. |
| 2571 | 15 | `static int[] avenueAgainst(DistrictPlan p, Stairs s, java.util.function.IntPredicate allowed)` | The plan's avenue laid against the harness's own: {expected, laid, wrong}, a staircase plot expected where it lies in an open cell's box and `allowed` says the rules let it be laid; wrong counts the plots laid where n... |
| 2588 | 5 | `static boolean[] avenueMask(DistrictPlan p)` | The plan's avenue plots laid, by plot (a mask of avenue[]), or none. |
| 2595 | 4 | `static boolean stepLaid(DistrictPlan p, Stairs s, int a, int k)` | Whether the avenue plot of arm a at step k (its S_k) is laid in plan p. |
| 2600 | 304 | `static void avenues(BuildingVisual.Type[] types)` |  |

### 14. THE CURVES (batch CL4, slice C) (lines 2905-3579)

| line | len | member | says |
|---:|---:|---|---|
| 2928 | 6 | `static DistrictPlan.Input farHomes(BuildingVisual.Type[] types, double hubX, double hubY)` | Section 10's LONG fixture (flat ground at his fill, his roads times LONG_ROADS: every homes cell long blocks) with the founding site at (hubX, hubY) in the frame's plots - outside the frame, for a far one. |
| 2936 | 3 | `static DistrictPlan.Input farHomes(BuildingVisual.Type[] types)` | The far fixture: the founding site FAR_CELLS cells west of the frame, level with its middle. |
| 2941 | 3 | `static int cellPlot(int x0, int y0, boolean across, int u, int v)` | The frame plot of the cell-axes plot (u, v) of the cell whose interior starts at (x0, y0): u along the long streets, v across them, the long streets running east-west where `across`. |
| 2946 | 52 | **type** `static final class Crescent` | The harness's own working of a crescent for one bulge (CL4), in the cell's axes, by walking the circle in small steps rather than by DistrictPlan's rows: the plots (u, v) the arc passes through (stair), and those outs... |
| 2953 | 38 | `Crescent(int s)` _(in PlanCheck.Crescent)_ |  |
| 2993 | 4 | `static boolean longRow(int v)` _(in PlanCheck.Crescent)_ | Whether (u, v) is on one of the cell's three long streets' rows. |
| 3000 | 16 | `static int bulgeOf(DistrictPlan p, int c, Crescent[] arcs)` | The cell c's bulge as its streets show it: +1 or -1 where the street plots of its interior are exactly its three long streets and the harness's arc of that bulge (and a boulevard cell's second row of ring), 0 where th... |
| 3018 | 5 | `static boolean farEnough(DistrictPlan.Input in, int c)` | Whether the middle of cell c of an input lies DistrictPlan.CRESCENT_FROM cells or more from its founding site, in a straight line, as the harness measures it (the squares in plots, against CRESCENT_FROM x CELL squared). |
| 3025 | 3 | `static boolean hashWants(DistrictPlan.Input in, int c)` | Whether the place's hash and CL2 layout make cell c a crescent, whatever its distance and its ground (DistrictPlan.crescentOf at the cell's place in the world). |
| 3030 | 4 | `static boolean expectedCrescent(DistrictPlan p, DistrictPlan.Input in, int c)` | The crescent cell a plan opened, as the harness expects it of a long-block homes cell: the place's hash and layout, beyond CRESCENT_FROM, no highway, railway, yard, reserve or avenue in it - and the ground laying the ... |
| 3036 | 4 | `static DistrictPlan.Curve crescentCurve(DistrictPlan p, int first)` | A crescent curve of plan p whose first plot is the frame plot `first`, or null. |
| 3042 | 5 | `static boolean[] bandMask(DistrictPlan p)` | The plots every curve of the plan reaches with its band, by plot. |
| 3049 | 9 | `static int boxesOn(DistrictPlan p, boolean[] mask)` | How many of a plan's boxes stand on a plot of mask. |
| 3060 | 8 | `static boolean sameCurves(DistrictPlan a, DistrictPlan b)` | Whether two plans have the same curves: kind, circle or vertices, radius, plots and band, in order. |
| 3070 | 6 | **type** `static final class CrescentStats` | What section 14 finds of a plan's homes cells and crescents (crescentStats()). |
| 3078 | 69 | `static CrescentStats crescentStats(DistrictPlan p, DistrictPlan.Input in, Crescent[] arcs)` | A plan's homes cells held to what the harness expects of them: each crescent exactly the cell the place, the distance and the crossings make one, its streets its three long streets and the arc the harness walks out, i... |
| 3149 | 3 | `static boolean holdsApartFromNetwork(Figures f, DistrictPlan p, DistrictPlan.Input in)` | Every check but the one network, which holds on a district planned alone only where the ground leaves it one piece: the map joins a district's pieces across the districts before it, and a district of the dense city's ... |
| 3153 | 258 | `static void curves(BuildingVisual.Type[] types)` |  |
| 3416 | 5 | `static int homesIn(DistrictPlan p)` | The homes cells a plan opened. |
| 3423 | 5 | `static int countTrue(boolean[] mask)` | The plots a mask holds. |
| 3430 | 67 | `static void shoreCurves(BuildingVisual.Type[] types)` | Section 14 (e): the test district cut by its river and the sea, the game's rules - each street along a cut a curve whose line starts and ends where its plots do, with corners no tighter than their radius, and no box o... |
| 3499 | 29 | `static void rimCurves(BuildingVisual.Type[] types)` | Section 14 (g), the rim of the dense city: the plans of districts far from its founding site, where crescents are laid in a real city (the screen and the samples of sections 5 and 6 lie within CRESCENT_FROM cells of it). |
| 3535 | 44 | `static void packing(BuildingVisual.Type[] types)` |  |

### 9. A SAVE NAMED ON THE COMMAND LINE: EVERY DISTRICT (lines 3580-3674)

| line | len | member | says |
|---:|---:|---|---|
| 3585 | 89 | `static void save(Path dir) throws Exception` | A save folder's autosave, loaded from a copy (a game run autosaves into any city it loads): every district planned, checked and timed. |

### 15. THE RUNS DATED (batch FX7) (lines 3675-3859)

| line | len | member | says |
|---:|---:|---|---|
| 3683 | 33 | **type** `static final class Runs` | The runs of a fixture, dated (FX7): a record {month, plot, before, after} for each plot whose kind of fixed ground a month changes, in the order laid - what CityMap's journal hands a plan as Input.fixedLog, written do... |
| 3687 | 4 | `Runs lay(int month, int x, int y, int w, int h, byte kind)` _(in PlanCheck.Runs)_ | A w x h rectangle from plot (x, y) laid as `kind` at `month`, on ground that held no run. |
| 3693 | 4 | `Runs lift(int month, int x, int y, int w, int h, byte kind)` _(in PlanCheck.Runs)_ | ...and the same rectangle taken away again at `month`. |
| 3699 | 16 | `DistrictPlan.Input into(DistrictPlan.Input in, int through, boolean dated)` _(in PlanCheck.Runs)_ | `in` given the runs as they stand after the records of the months up to `through` as its fixed ground, and with `dated` those records as its Input.fixedLog. |
| 3721 | 6 | `static Log runsLog(int house, int shop, int works, int farm, int paved, int months)` | A log of a first batch in month 0 and `months` months of arrivals after it. |
| 3729 | 3 | `static boolean boxIn(DistrictPlan p, int i, int x, int y, int w, int h)` | Whether box i of plan p has a plot in the w x h rectangle from (x, y). |
| 3734 | 6 | `static int boxesIn(DistrictPlan p, DistrictPlan old, int x, int y, int w, int h)` | How many of plan p's boxes have a plot in the rectangle; with `old`, only those that plan does not have (the arrivals since it). |
| 3741 | 108 | `static void runsDated(BuildingVisual.Type[] types)` |  |
| 3851 | 8 | `static int crossingsOn(DistrictPlan p, int x)` | How many plots of column x a plan's streets cross a railway on (the surface's CROSSING flag). |

