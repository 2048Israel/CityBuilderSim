# WorldMarketCheck.java - 979 lines · 34 methods · 12 constants · harnesses

`ham/citybuildersim/WorldMarketCheck.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

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
>   1. THE SOLVE CONVERGES: every good clears to SOLVE_TOL (sales over supply,
>      recomputed from the file's pairs) and meets the price path's level,
>      cold in 2000 and warm after, on both paths; the sweeps are counted.
>   2. THE CALIBRATION: the reference city (Halifax, 2025, Historical) sees
>      Good's literals on every calibrated side to 1e-9; no two-sided good
>      goes without; a one-sided good takes its class's median freight scale.
>   3. THE FLOOR IS NEVER ABOVE THE CEILING: every good, 2000-2040, both
>      paths, at three sites, the city's exports and imports each 0, 0.1%,
>      1%, 10% and 50% of the world's use; and the floor falls with the
>      city's exports as the ceiling rises with its imports.
>   4. A CITY MOVES ITS PRICE AS BIG AS IT IS: at 0.01% of a good's world use
>      (an ordinary city) the netback moves by under 1% of the price it is
>      sold at, at 10% by at least 5% of it, every good; at 0.1% the move is
>      printed (the re-solve agrees with it: the host's market is small).
>   5. THE MONTH'S STEP IS NEAR A RE-SOLVE: the fill's netback (the band's floor)
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
>      against its volumes, a berth never dearer, a nation's buyer price never
>      above its own mill price.
>  10. THE WOOD CASE (full file; printed, not asserted): a Halifax city's
>      lumber buyers and where the others join.
>  11. THE TIMINGS: the Base, the founding solve, a January, a month's bands.
> 
> Every fixture causes its condition: the city's volumes are shares of the
> good's own world use, a seed is drawn so that two worlds differ, a save is
> restored into a market that has solved nothing.

**Uses:** [WorldMarket](WorldMarket.md) (89), [Nations](Nations.md) (43), [Good](Good.md) (36), [WorldData](WorldData.md) (15), [Timing](Timing.md) (5), [WorldDataCheck](WorldDataCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 227 | 1. THE SOLVE CONVERGES |
| 296 | 2. THE CALIBRATION |
| 362 | 3. THE FLOOR IS NEVER ABOVE THE CEILING |
| 444 | 4. A BIG CITY MOVES ITS PRICE, A SMALL ONE BARELY |
| 489 | 5. THE MONTH'S STEP IS NEAR A RE-SOLVE |
| 573 | 6. SEEDS |
| 636 | 7. THE SAVE ROUND TRIP |
| 680 | 8. THE STAND-IN |
| 729 | 9. THE PIECES |
| 888 | 10. THE WOOD CASE |
| 927 | 11. THE TIMINGS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 79 | `WorldMarketCheck.out` | `System.out` | Where the harness prints: the console. |
| 85 | `WorldMarketCheck.BASE_SEED` | `11` | The seed the single-world sections use. |
| 88 | `WorldMarketCheck.SHARES` | `{ 0,.001,.01,.1,.5 }` | The city's yearly exports and imports in sections 3 and 4, as shares of the good's world use. |
| 91 | `WorldMarketCheck.STEPS` | `{.001,.01,.05,.10 }` | The volumes of section 5, shares of the world's use. |
| 94 | `WorldMarketCheck.STEP_TOL` | `.03` | The month's step is within this share of the price (the served buyers' landed price, tonne-weighted) of the re-solve's figure: a freight-heavy good's netback is a small remainder of its price, so a share of the netbac... |
| 97 | `WorldMarketCheck.SMALL_MOVE` | `.01, BIG_MOVE =.05` | An ordinary city (0.01% of the world's use) moves its netback by less than this share of the price it sells at, a big one (10%) by at least BIG_MOVE of it. |
| 100 | `WorldMarketCheck.ORDINARY_SHARE` | `.0001, LARGE_SHARE =.001, BIG_SHARE =.1` | The shares of a good's world use section 4 reads: an ordinary city's, a large mill town's (printed only: the host's market is small), and a big city's. |
| 103 | `WorldMarketCheck.ONE_PERCENT` | `{ Good.LUMBER, Good.CRUDE, Good.IRON, Good.GRAINS }` | The goods whose step is asserted within STEP_TOL at 1% of world use, beside the median: the study's and the brief's four. |
| 106 | `WorldMarketCheck.SAME` | `1e-9` | Two figures that should be one are the same within this share. |
| 109 | `WorldMarketCheck.STAND_IN_SEEDS` | `2_000` | Seeds drawn for the stand-in. |
| 112 | `WorldMarketCheck.SIGMAS` | `4.5` | A frequency is judged within this many standard deviations of a binomial. |
| 494 | `WorldMarketCheck.NAMED` | `{ Good.LUMBER, Good.IRON, Good.CRUDE, Good.GRAINS, Good.MACHINERY, Good.CARS }` | The goods the study and the brief name, always printed. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 82 | `static double sink` | What the band timing adds up, so that the optimiser cannot drop the calls it is timing. |
| 126 | `int n, bad` |  |
| 127 | `String first` |  |
| 157 | `final String name` |  |
| 158 | `final WorldData data` |  |
| 159 | `final WorldMarket.Base base` |  |
| 160 | `final double baseMillis` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 905 | **type** `public class WorldMarketCheck` | The world market (batch WN3; the project's spec-nations.md 4): the nations' January solve held to the equations it solves, the calibration held to the game's literals, and the city's band held to what makes it a band ... |
| 114 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 119 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 125 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 129 | 7 | `void see(boolean ok, Supplier<String> what)` _(in WorldMarketCheck.Tally)_ |  |
| 138 | 6 | `static void tally(String label, Tally t)` |  |
| 145 | 1 | `static boolean near(double a, double b, double rel)` |  |
| 147 | 1 | `static boolean same(double a, double b)` |  |
| 149 | 5 | `static boolean same(double[] a, double[] b)` |  |
| 156 | 15 | **type** `static final class Scope` | The world file and what is built on it: the fixture or the full file. |
| 162 | 6 | `Scope(String name, WorldData data, WorldMarket.Base base, double baseMillis)` _(in WorldMarketCheck.Scope)_ |  |
| 169 | 1 | `WorldMarket market(Nations.Path path, long seed)` _(in WorldMarketCheck.Scope)_ |  |
| 172 | 41 | `public static void main(String[] args) throws Exception` |  |
| 214 | 12 | `static void sections(Scope sc)` |  |

### 1. THE SOLVE CONVERGES (lines 227-295)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 32 | `static double[] residual(WorldData data, WorldMarket m, Good g)` | One good's solved state recomputed from the file's pairs alone: {the worst of sales over supply less 1 over the makers, the mean mill price over its path's level less 1 in magnitude}. |
| 265 | 30 | `static void theSolve(Scope sc)` |  |

### 2. THE CALIBRATION (lines 296-361)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 61 | `static void theCalibration(Scope sc)` |  |

### 3. THE FLOOR IS NEVER ABOVE THE CEILING (lines 362-443)

| line | len | member | says |
|---:|---:|---|---|
| 367 | 1 | `static double units(WorldMarket m, int gi, double tonnes)` | The units a month a city trades to move a good's year by tonnes. |
| 370 | 28 | `static void order(WorldMarket m, Tally order, Tally falls, Tally game, String where, int[] combos)` | Checks one solved market at its site over the grid of the city's volumes. |
| 399 | 44 | `static void theFloorUnderCeiling(Scope sc)` |  |

### 4. A BIG CITY MOVES ITS PRICE, A SMALL ONE BARELY (lines 444-488)

| line | len | member | says |
|---:|---:|---|---|
| 448 | 38 | `static void theBigCity(Scope sc)` |  |
| 487 | 1 | `static String pct(double share)` |  |

### 5. THE MONTH'S STEP IS NEAR A RE-SOLVE (lines 489-572)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 58 | `static void theStep(Scope sc)` |  |
| 556 | 16 | `static void lumber(Scope sc, WorldMarket m)` | Lumber at the four volumes, as the study's 4.4 table has it: the buyers, what the city nets this month, with the world's answer, re-solved, off. |

### 6. SEEDS (lines 573-635)

| line | len | member | says |
|---:|---:|---|---|
| 578 | 12 | `static double[] bands(WorldMarket m)` | Every good's {floor, ceiling} at the site, for exports and imports of 1% and 2% of the world's use, in the game's money. |
| 591 | 44 | `static void theSeeds(Scope sc)` |  |

### 7. THE SAVE ROUND TRIP (lines 636-679)

| line | len | member | says |
|---:|---:|---|---|
| 640 | 39 | `static void theSave(Scope sc)` |  |

### 8. THE STAND-IN (lines 680-728)

| line | len | member | says |
|---:|---:|---|---|
| 684 | 35 | `static void theStandIn(Scope sc)` |  |
| 721 | 7 | `static Tally merged(Tally a, Tally b)` | Two tallies as one. |

### 9. THE PIECES (lines 729-887)

| line | len | member | says |
|---:|---:|---|---|
| 737 | 15 | `static double bruteLevel(double[] net, double[] slope, double[] cap, double x)` | The level at which sinks take x tonnes together, each taking min(cap, slope (net - level)+), found by bisection on the total and so with none of the market's sorted events; -infinity past the caps. |
| 754 | 3 | `static boolean sameLevel(double a, double b, double scale)` | Two levels are the same: equal infinities, or within a share of the scale of the netbacks they are among. |
| 758 | 115 | `static void thePieces(Scope sc)` |  |
| 875 | 5 | `static double greatCircle(double lat1, double lon1, double lat2, double lon2)` | The great circle distance between two points, km, by the haversine. |
| 882 | 5 | `static double scaleOf(double[] a)` | The sum of an array. |

### 10. THE WOOD CASE (lines 888-926)

| line | len | member | says |
|---:|---:|---|---|
| 892 | 29 | `static void theWoodCase(Scope sc)` |  |
| 922 | 4 | `static double demandShare(WorldMarket m, int gi, int nation)` |  |

### 11. THE TIMINGS (lines 927-979)

| line | len | member | says |
|---:|---:|---|---|
| 931 | 48 | `static void theTimings(Scope sc)` |  |

