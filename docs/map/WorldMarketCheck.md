# WorldMarketCheck.java - 1,685 lines · 52 methods · 27 constants · harnesses

`ham/citybuildersim/WorldMarketCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The world market (batch WN3; the project's spec-nations.md 4): the nations'
> January solve held to the equations it solves, the calibration held to the
> game's literals, and the city's band held to what makes it a band - the
> floor under the ceiling, a small city a price-taker and a big one not, the
> month's step near a re-solve with the city's tonnes in.
> 
> WHY. The world was a price list, and the market that replaces it can be
> wrong in ways no city would show: a solve that stops before the makers clear
> (every price off by a percent and nobody the wiser), a calibration that
> leaves Halifax a different band from the literals the whole economy was
> balanced on (D12), a floor above a ceiling (a city that makes money buying
> and selling the same tonne to itself), a band that does not move with the
> city's own tonnes, a restored save that gives another band. Each is held to
> a figure the model does not compute for itself: the residuals are
> recomputed here from the file's pairs, the fill is solved a second way, the
> literals are Good's.
> 
> THE DATA. On the fixture (WorldData.FIXTURE, twelve nations) always; on the
> full world-1.bin where it is named (-Dcbs.world=<path>, WorldData.locate()),
> for every section, and a line says when it is not.
> 
> What this has to prove:
>   1. THE CHAINED SOLVE (CU2; WorldChain, spec-world-prices 3): the January
>      solves the goods inputs first, cold at the founding (the calendar's first
>      year, and again at the file's first, 2000) and warm after, on both paths,
>      and holds three claims, each to a figure recomputed from the file's pairs
>      and the freight table: a root's reference band stands on its price path
>      (the anchor); a made good is what its inputs cost the nation landed, less
>      the copper charge, plus its own part, which clears and meets the
>      manufactures index's level to SOLVE_TOL; every maker clears (a root's sales
>      over supply, a made good's supply over its sales). The sweeps are counted.
>   2. THE CALIBRATION: the reference city (Halifax, 2025, Historical) sees
>      Good's literals on every calibrated side to 1e-9, except concentrate and
>      anode, which carry the played year's charge and not 2024's the literals
>      stand on (call star 6 (a)), and are held to the charge's difference; no
>      two-sided good goes without; a good that does not calibrate keeps the
>      freight scale the plain pass gave it, its class's median.
>   3. THE FLOOR IS NEVER ABOVE THE CEILING: every good, 2000-2040, both
>      paths, at three sites, the city's exports and imports each 0, 0.1%,
>      1%, 10% and 50% of the world's use; and the floor falls with the
>      city's exports as the ceiling rises with its imports.
>   4. A CITY MOVES ITS PRICE AS BIG AS IT IS: at 0.01% of a good's world use
>      (an ordinary city) the netback moves by under 1% of the price it is
>      sold at, at 10% by at least 5% of it, every good; at 0.1% the move is
>      printed (the re-solve agrees with it: the host's market is small).
>   5. THE MONTH'S STEP IS NEAR A RE-SOLVE (a made good's makers each supply
>      what they sell at its chained price, so its re-solve starts from a scale
>      a maker): the fill's netback (the band's floor)
>      within 3% of the price it is sold at of the nations' solve again with the
>      city's tonnes delivered. At 0.1% of world use every good is; at 1% the
>      median is and lumber, crude, iron and grains each are; 5% and 10% are
>      printed, the step's reach beyond a giant city's 1%, and so is lumber at
>      all four volumes (the study's 4.4 table).
>   6. SEEDS: two seeds differ, one seed repeats to the bit, Historical's 2025
>      is the same on any seed.
>   7. THE SAVE: prices and scales restored give the bands and allocations of
>      the market they came from, to the bit, and a wrong size is refused.
>   8. THE STAND-IN: a generated world's site is a coastal nation drawn by its
>      output, the same each time.
>   9. THE PIECES: the fill against a plain water-filling, the allocation
> ... (31 more lines in the source)

**Uses:** [WorldMarket](WorldMarket.md) (145), [Nations](Nations.md) (112), [Good](Good.md) (76), [WorldData](WorldData.md) (21), [BuildingsTemplate](BuildingsTemplate.md) (10), [Timing](Timing.md) (6), [WorldChain](WorldChain.md) (6), [WorldDataCheck](WorldDataCheck.md) (1), [BuildingCatalog](BuildingCatalog.md) (1)

## Sections

| line | section |
|---:|---|
| 321 | 1. THE CHAINED SOLVE |
| 542 | 2. THE CALIBRATION |
| 649 | 3. THE FLOOR IS NEVER ABOVE THE CEILING |
| 731 | 4. A BIG CITY MOVES ITS PRICE, A SMALL ONE BARELY |
| 776 | 5. THE MONTH'S STEP IS NEAR A RE-SOLVE |
| 860 | 6. SEEDS |
| 923 | 7. THE SAVE ROUND TRIP |
| 967 | 8. THE STAND-IN |
| 1016 | 9. THE PIECES |
| 1175 | 10. THE WOOD CASE |
| 1214 | 11. THE TIMINGS |
| 1268 | 12. THE CHAIN HOLDS |
| 1459 | 13. THE RECIPE TABLE IS buildings.json's |
| 1542 | 14. THE GUARD KEEPS THE SOLVE TO THE NUMBERS |
| 1606 | 15. THE COPPER CHARGE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 112 | `WorldMarketCheck.out` | `System.out` | Where the harness prints: the console. |
| 118 | `WorldMarketCheck.BASE_SEED` | `11` | The seed the single-world sections use. |
| 121 | `WorldMarketCheck.SHARES` | `{ 0,.001,.01,.1,.5 }` | The city's yearly exports and imports in sections 3 and 4, as shares of the good's world use. |
| 124 | `WorldMarketCheck.STEPS` | `{.001,.01,.05,.10 }` | The volumes of section 5, shares of the world's use. |
| 127 | `WorldMarketCheck.STEP_TOL` | `.03` | The month's step is within this share of the price (the served buyers' landed price, tonne-weighted) of the re-solve's figure: a freight-heavy good's netback is a small remainder of its price, so a share of the netbac... |
| 130 | `WorldMarketCheck.SMALL_MOVE` | `.01, BIG_MOVE =.05` | An ordinary city (0.01% of the world's use) moves its netback by less than this share of the price it sells at, a big one (10%) by at least BIG_MOVE of it. |
| 133 | `WorldMarketCheck.ORDINARY_SHARE` | `.0001, LARGE_SHARE =.001, BIG_SHARE =.1` | The shares of a good's world use section 4 reads: an ordinary city's, a large mill town's (printed only: the host's market is small), and a big city's. |
| 136 | `WorldMarketCheck.ONE_PERCENT` | `{ Good.LUMBER, Good.CRUDE, Good.IRON, Good.GRAINS }` | The goods whose step is asserted within STEP_TOL at 1% of world use, beside the median: the study's and the brief's four. |
| 139 | `WorldMarketCheck.SAME` | `1e-9` | Two figures that should be one are the same within this share. |
| 142 | `WorldMarketCheck.STAND_IN_SEEDS` | `2_000` | Seeds drawn for the stand-in. |
| 145 | `WorldMarketCheck.SIGMAS` | `4.5` | A frequency is judged within this many standard deviations of a binomial. |
| 148 | `WorldMarketCheck.ARITHMETIC` | `1e-12` | Two figures that are one piece of arithmetic done twice (a price that is the sum of its parts, a ratio of two charges) agree within this share. |
| 151 | `WorldMarketCheck.ANCHOR_CHECK_TOL` | `10 * WorldMarket.ANCHOR_TOL` | A root's reference band stands on its path's target within this, in logs: ten times the anchor's own stop (WorldMarket.ANCHOR_TOL), the worst measured on these years being 9.4e-11 (a band off its path by percents in m... |
| 154 | `WorldMarketCheck.CHARGE_ROOM` | `1e-12` | The played 2025 band of concentrate or anode moves off its literal by the charge's whole difference, within this share of it: the worst measured is 6.0e-14 of it. |
| 157 | `WorldMarketCheck.ROD_ON_CATHODE` | `.10` | Rod's floor over its literal export price stands within this of cathode's in every January read: the worst measured is 0.0933 (Random seed 9, the reference site, 1900), main's world stands 1.4 off (rod 2.41 and 2.68 o... |
| 160 | `WorldMarketCheck.WIRE_ON_ROD_MEDIAN` | `.05` | The median over every January read of wire's floor over its literal export price, over rod's, stands within this of 1: the measured median is 1.038, main's world stands 0.12 to 0.18 off (0.82 and 0.88, 3.2); the worst... |
| 163 | `WorldMarketCheck.WORKS_MEDIAN` | `.10` | A works' median feed over its sales, over every January read, stands within this share of its literal margin: the widest measured is 0.074 (Panel Mill and Pulp Mill), main's Rod Mill stands 0.55 off (0.46 for 1.03, 3.... |
| 166 | `WorldMarketCheck.RECIPE_TOL` | `.03` | A recipe row's share of an input is within this (absolute) of the works' uses and makes at the literals' mid-band prices: a few points, as the dial is rounded to the hundredth. |
| 781 | `WorldMarketCheck.NAMED` | `{ Good.LUMBER, Good.IRON, Good.CRUDE, Good.GRAINS, Good.MACHINERY, Good.CARS }` | The goods the study and the brief name, always printed. |
| 1273 | `WorldMarketCheck.CHAIN_GROUND` | `7` | The ground seed the stand-in site of section 12 is drawn from (setStandIn: the site a generated world's city is founded at); the single-world sections use 7 as well. |
| 1276 | `WorldMarketCheck.CHAIN_SEEDS` | `{ 1, 5, 9, Nations.DEFAULT_SEED }` | The Random seeds section 12 plays beside the Historical path: 9 is the guard's world (its crude left the numbers in 2165 on main), 48213 the game's default. |
| 1279 | `WorldMarketCheck.CHAIN_LAST` | `2200, CHAIN_EVERY = 5` | The last January section 12 reads, and the years between the Januaries it reads (from the calendar's first). |
| 1282 | `WorldMarketCheck.EXTRA_WORKS` | `{ "Electrolytic Refinery", "Electric Arc Furnace" }` | The works section 12 holds beside the recipe's own: the cathode's and the arc furnace's, which no row of the table names. |
| 1547 | `WorldMarketCheck.GUARD_SEED` | `9` | The drawn world whose crude left the numbers in 2165 on main (spec-world-prices 3.7), and the last January section 14 plays it to. |
| 1548 | `WorldMarketCheck.GUARD_LAST` | `2170` |  |
| 1611 | `WorldMarketCheck.CHARGE_FROM` | `1900, CHARGE_TO = 2230` | The years the charge's swing is read over: the calendar's first to well past the paths' end. |
| 1614 | `WorldMarketCheck.PEAKS_FROM` | `1990, PEAKS_TO = 2030` | The years round the record's peaks the local maxima are listed over (printed beside them, not asserted). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 110 | `static int fails` |  |
| 115 | `static double sink` | What the band timing adds up, so that the optimiser cannot drop the calls it is timing. |
| 180 | `int n, bad` |  |
| 181 | `String first` |  |
| 211 | `final String name` |  |
| 212 | `final WorldData data` |  |
| 213 | `final WorldMarket.Base base` |  |
| 214 | `final double baseMillis` |  |
| 327 | `double v` |  |
| 328 | `String at` |  |
| 1286 | `final String name` |  |
| 1287 | `final BuildingsTemplate template` |  |
| 1288 | `final List<Good> used` |  |
| 1290 | `double literal` | The feed (each use at the literal import price) over the sales (each make at the literal export price); NaN where a good has no such literal, and why. |
| 1291 | `String why` |  |
| 1292 | `double low` |  |
| 1293 | `String lowAt` |  |
| 1294 | `int read, unpriced` |  |
| 1295 | `final List<Double> ratios` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 108 | 1578 | **type** `public class WorldMarketCheck` | The world market (batch WN3; the project's spec-nations.md 4): the nations' January solve held to the equations it solves, the calibration held to the game's literals, and the city's band held to what makes it a band ... |
| 168 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 173 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 179 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 183 | 7 | `void see(boolean ok, Supplier<String> what)` _(in WorldMarketCheck.Tally)_ |  |
| 192 | 6 | `static void tally(String label, Tally t)` |  |
| 199 | 1 | `static boolean near(double a, double b, double rel)` |  |
| 201 | 1 | `static boolean same(double a, double b)` |  |
| 203 | 5 | `static boolean same(double[] a, double[] b)` |  |
| 210 | 15 | **type** `static final class Scope` | The world file and what is built on it: the fixture or the full file. |
| 216 | 6 | `Scope(String name, WorldData data, WorldMarket.Base base, double baseMillis)` _(in WorldMarketCheck.Scope)_ |  |
| 223 | 1 | `WorldMarket market(Nations.Path path, long seed)` _(in WorldMarketCheck.Scope)_ |  |
| 226 | 42 | `public static void main(String[] args) throws Exception` |  |
| 275 | 28 | `static void theBaseYear(Scope sc)` | EP1: the base year growth() and level() read. |
| 304 | 16 | `static void sections(Scope sc)` |  |

### 1. THE CHAINED SOLVE (lines 321-541)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 13 | **type** `static final class Worst` | The largest of a figure over many readings, and where it was read. |
| 330 | 6 | `void see(double x, Supplier<String> where)` _(in WorldMarketCheck.Worst)_ |  |
| 337 | 1 | `public String toString()` _(in WorldMarketCheck.Worst)_ |  |
| 341 | 6 | `static double[] madeIn(WorldData data, WorldMarket.Traded t, int n)` | What a good's makers made at the base year's prices (thousand US$), summed from the file's pairs. |
| 352 | 17 | `static double[] sales(WorldData data, WorldMarket.Traded t, double[] p, double[] gh)` | Each maker's sales of a good (thousand US$ of the base year) at the prices p it asks (a good's phat, over the base year's), recomputed from the file's pairs alone: p^-eps times the pair's flow times the buyer's spendi... |
| 371 | 17 | `static double[] residual(WorldData data, WorldMarket m, Good g)` | One good's solved state recomputed from the file's pairs alone: {the worst of sales over supply less 1 over the makers, the mean mill price over its path's level less 1 in magnitude}. |
| 394 | 17 | `static double[] landed(WorldMarket m, int pg)` | What a good costs each nation over what it cost in the base year, recomputed from the market's finished prices and the freight table: the cheapest maker's mill price landed there (its price over the base year's, times... |
| 417 | 14 | `static double[] inputsOf(WorldMarket m, int gi, double[][] landed)` | What a made good's inputs cost in each of its makers' nations, less the copper charge where the good carries one (never below WorldMarket.CHARGE_LEAVES of the inputs' cost): the sum over its inputs of the recipe's sha... |
| 437 | 13 | `static double anchorRead(WorldMarket m, int gi)` | The anchor's read of a root, recomputed from its makers' solved prices: the reference city's band over the band the calibration left it, a calibrated good by the geometric middle of its two sides, a one-sided good by ... |
| 451 | 90 | `static void theSolve(Scope sc)` |  |

### 2. THE CALIBRATION (lines 542-648)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 102 | `static void theCalibration(Scope sc)` |  |

### 3. THE FLOOR IS NEVER ABOVE THE CEILING (lines 649-730)

| line | len | member | says |
|---:|---:|---|---|
| 654 | 1 | `static double units(WorldMarket m, int gi, double tonnes)` | The units a month a city trades to move a good's year by tonnes. |
| 657 | 28 | `static void order(WorldMarket m, Tally order, Tally falls, Tally game, String where, int[] combos)` | Checks one solved market at its site over the grid of the city's volumes. |
| 686 | 44 | `static void theFloorUnderCeiling(Scope sc)` |  |

### 4. A BIG CITY MOVES ITS PRICE, A SMALL ONE BARELY (lines 731-775)

| line | len | member | says |
|---:|---:|---|---|
| 735 | 38 | `static void theBigCity(Scope sc)` |  |
| 774 | 1 | `static String pct(double share)` |  |

### 5. THE MONTH'S STEP IS NEAR A RE-SOLVE (lines 776-859)

| line | len | member | says |
|---:|---:|---|---|
| 783 | 58 | `static void theStep(Scope sc)` |  |
| 843 | 16 | `static void lumber(Scope sc, WorldMarket m)` | Lumber at the four volumes, as the study's 4.4 table has it: the buyers, what the city nets this month, with the world's answer, re-solved, off. |

### 6. SEEDS (lines 860-922)

| line | len | member | says |
|---:|---:|---|---|
| 865 | 12 | `static double[] bands(WorldMarket m)` | Every good's {floor, ceiling} at the site, for exports and imports of 1% and 2% of the world's use, in the game's money. |
| 878 | 44 | `static void theSeeds(Scope sc)` |  |

### 7. THE SAVE ROUND TRIP (lines 923-966)

| line | len | member | says |
|---:|---:|---|---|
| 927 | 39 | `static void theSave(Scope sc)` |  |

### 8. THE STAND-IN (lines 967-1015)

| line | len | member | says |
|---:|---:|---|---|
| 971 | 35 | `static void theStandIn(Scope sc)` |  |
| 1008 | 7 | `static Tally merged(Tally a, Tally b)` | Two tallies as one. |

### 9. THE PIECES (lines 1016-1174)

| line | len | member | says |
|---:|---:|---|---|
| 1024 | 15 | `static double bruteLevel(double[] net, double[] slope, double[] cap, double x)` | The level at which sinks take x tonnes together, each taking min(cap, slope (net - level)+), found by bisection on the total and so with none of the market's sorted events; -infinity past the caps. |
| 1041 | 3 | `static boolean sameLevel(double a, double b, double scale)` | Two levels are the same: equal infinities, or within a share of the scale of the netbacks they are among. |
| 1045 | 115 | `static void thePieces(Scope sc)` |  |
| 1162 | 5 | `static double greatCircle(double lat1, double lon1, double lat2, double lon2)` | The great circle distance between two points, km, by the haversine. |
| 1169 | 5 | `static double scaleOf(double[] a)` | The sum of an array. |

### 10. THE WOOD CASE (lines 1175-1213)

| line | len | member | says |
|---:|---:|---|---|
| 1179 | 29 | `static void theWoodCase(Scope sc)` |  |
| 1209 | 4 | `static double demandShare(WorldMarket m, int gi, int nation)` |  |

### 11. THE TIMINGS (lines 1214-1267)

| line | len | member | says |
|---:|---:|---|---|
| 1218 | 49 | `static void theTimings(Scope sc)` |  |

### 12. THE CHAIN HOLDS (lines 1268-1458)

| line | len | member | says |
|---:|---:|---|---|
| 1285 | 49 | **type** `static final class Works` | One works as section 12 reads it: its template, the goods it uses and makes, its feed over its sales at the literals, and what the Januaries read. |
| 1297 | 20 | `Works(String name, BuildingsTemplate template)` _(in WorldMarketCheck.Works)_ |  |
| 1319 | 14 | `double margin(WorldMarket m)` _(in WorldMarketCheck.Works)_ | The feed over the sales in the game's money from the market's bands (a use at the ceiling, a make at the floor), NaN where a good is not priced there. |
| 1336 | 4 | `static double floorOver(WorldMarket m, Good g)` | A good's played floor over its literal export price, NaN where the market has no positive floor for it. |
| 1342 | 7 | `static Map<String, BuildingsTemplate> buildingsByName()` | The templates of buildings.json by name, through the game's own loader; null where it cannot be read. |
| 1351 | 5 | `static String windowed(double[] worst)` | A figure's worst in each fifty years, as a line. |
| 1357 | 101 | `static void theChainHolds(Scope sc)` |  |

### 13. THE RECIPE TABLE IS buildings.json's (lines 1459-1541)

| line | len | member | says |
|---:|---:|---|---|
| 1464 | 6 | `static double mid(Good g)` | A good's mid-band literal price, (import + export) / 2 for a two-sided good, its one literal otherwise, NaN for a good with neither. |
| 1471 | 70 | `static void theRecipeTable()` |  |

### 14. THE GUARD KEEPS THE SOLVE TO THE NUMBERS (lines 1542-1605)

| line | len | member | says |
|---:|---:|---|---|
| 1550 | 55 | `static void theGuard(Scope sc)` |  |

### 15. THE COPPER CHARGE (lines 1606-1685)

| line | len | member | says |
|---:|---:|---|---|
| 1616 | 69 | `static void theCopperCharge(Scope sc)` |  |

