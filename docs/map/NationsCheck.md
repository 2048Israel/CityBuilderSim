# NationsCheck.java - 680 lines · 23 methods · 10 constants · harnesses

`ham/citybuildersim/NationsCheck.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The nations beyond the city and the path they are on (batch WN1; the
> project's spec-nations.md 7): Nations' year - output, people, prices and the
> rate of the nation's money - as a pure function of (path, seed, year), the
> file's real years on Historical, and the draws of the rule held to the dials
> they are drawn from.
> 
> WHY. The world outside was one price list and the dollar's draws were seeded
> by the month alone, so every city met one world (spec-nations 1.4). A path
> that is drawn gets four ways to be wrong that no city would show: a seed
> that does not decide it (two runs of one world differ), a Historical that is
> not the file, shocks whose size is not the dial's (so the economy WN3 builds
> on them is calmer or wilder than the sources say), and a nation whose output
> can go negative a century in. The statistics are counted over seeded years,
> and each says against which constant it is judged.
> 
> THE DATA. On the fixture (WorldData.FIXTURE, twelve nations) always; the full
> world-1.bin where it is named (-Dcbs.world=<path>, WorldData.locate()), for
> the sections that read all 196 nations, and a line says when it is not.
> 
> What this has to prove:
>   1. ONE SEED, ONE PATH: two Nations of one path and seed agree on every
>      component of every year, FIRST_YEAR (1900) to 2150; one asked 2150 first agrees with one
>      asked in order; another seed differs; Historical on two seeds agrees to
>      2025 and differs after.
>   2. HISTORICAL EQUALS THE FILE TO 2025: output, people, price level, rate and
>      each good's price, with their marks; Random starts from the file's first January (the calendar's, 1900: the stand-in century's);
>      people are the file's to 2100 on both paths.
>   3. RANDOM'S SWINGS MATCH ITS DIALS over seeded years: the world factor, a
>      region's, the own shock's persistence, the frontier's swing, a poorer
>      nation's larger, crises at their rate and depth, the trend inside its
>      dials, inflation above its floor.
>   3b. GOODS: each good's price swing and persistence match their dials.
>   4. HISTORICAL'S FUTURE SWINGS ARE THE SMALLER by HISTORICAL_SHOCK_SHARE, and
>      it draws no crisis.
>   5. PEGS HOLD: a peg keeps its ratio, a use is the other money, the euro's
>      users share one rate; on the full file every peg:USD nation holds.
>   6. OUTPUT NEVER NEGATIVE: output, people, prices and rates finite and above
>      0 on both paths, 20 seeds, to 2300.
>   7. AFTER 2100: people held; Historical's growth has eased to the long run.
>   8. MISSING FIGURES: a series with none, with a gap or with an early end is
>      filled and ruled as FIGURES say (Nations.Absent causes them).
> 
> Every fixture causes its condition: a nation of the file is pegged by the
> regimes it is given, a figure is missing because the harness says it is, and
> the statistics are counted over enough seeded years that a wrong dial would
> show.

**Uses:** [Nations](Nations.md) (172), [WorldData](WorldData.md) (31), [WorldDataCheck](WorldDataCheck.md) (3), [PriceLevel](PriceLevel.md) (2)

## Sections

| line | section |
|---:|---|
| 206 | 1. ONE SEED, ONE PATH |
| 237 | 2. HISTORICAL EQUALS THE FILE TO 2025 |
| 310 | 3. RANDOM'S SWINGS MATCH ITS DIALS |
| 402 | 3b. GOODS |
| 443 | 4. HISTORICAL'S FUTURE SWINGS ARE THE SMALLER |
| 479 | 5. PEGS HOLD |
| 532 | 6. OUTPUT NEVER NEGATIVE |
| 562 | 7. AFTER 2100 |
| 595 | 8. MISSING FIGURES |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `NationsCheck.out` | `System.out` | Where the harness prints: the console. |
| 67 | `NationsCheck.BASE_SEED` | `7_001` | The seed the single-world sections use, and the one the others start from. |
| 70 | `NationsCheck.SEEDS` | `20` | The seeds the statistics are counted over, and the years each (the 100 after the first: FIRST_YEAR + 1 to FIRST_YEAR + 100). |
| 73 | `NationsCheck.GOOD_SEEDS` | `30` | The goods' statistics are counted over this many seeds of 100 years (a lag-1 autocorrelation's error is about 1 over the root of the count). |
| 76 | `NationsCheck.GOOD_BURN_IN` | `30` | The years a good's price needs to settle from the first year's ratio (the file's 2000, held through the stand-in century) to its mean before it is counted: 0.7 to the 30th is 2e-5. |
| 79 | `NationsCheck.SD_TOL` | `.10, FRONTIER_SD_TOL =.12, GOOD_SD_TOL =.12, SHARE_TOL =.15` | A swing's standard deviation is within this share of its dial (the world's, a region's, the frontier's, a good's). |
| 82 | `NationsCheck.LAG_TOL` | `.06` | An autocorrelation is within this of its dial (error of a lag-1 estimate over 2,000 years is about .02). |
| 85 | `NationsCheck.RATE_TOL` | `.004` | The crisis rate among the years a crisis could start is within this of CRISIS_RATE. |
| 88 | `NationsCheck.LONG_RUN_TOL` | `.003` | The Historical frontier's growth a head, 2160-2200 and over twelve seeds, is within this of LONG_RUN_GROWTH: tight, because FRONTIER_GROWTH is only .005 above the long run, so an ease that did not happen would pass a ... |
| 91 | `NationsCheck.HELD_TOL` | `1e-12` | A rate held, or a ratio kept, is the same within this share. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |
| 100 | `int n, bad` |  |
| 101 | `String first` |  |
| 121 | `int n` |  |
| 122 | `double sum, sumSq` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 621 | **type** `public class NationsCheck` | The nations beyond the city and the path they are on (batch WN1; the project's spec-nations.md 7): Nations' year - output, people, prices and the rate of the nation's money - as a pure function of (path, seed, year), ... |
| 93 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 99 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 103 | 7 | `void see(boolean ok, Supplier<String> what)` _(in NationsCheck.Tally)_ |  |
| 112 | 6 | `static void tally(String label, Tally t)` |  |
| 120 | 12 | **type** `static final class Moments` | A sample's count, sum and sum of squares: its standard deviation. |
| 124 | 5 | `void add(double x)` _(in NationsCheck.Moments)_ |  |
| 130 | 1 | `double sd()` _(in NationsCheck.Moments)_ |  |
| 133 | 1 | `static boolean near(double a, double b, double rel)` |  |
| 136 | 4 | `static void within(String label, double measured, double dial, double tol, boolean share)` | A measured figure judged against the constant it is drawn from, within a share of it (share) or an amount (absolute). |
| 141 | 1 | `static Nations of(WorldData data, Nations.Path path, long seed)` |  |
| 143 | 47 | `public static void main(String[] args) throws Exception` |  |
| 192 | 13 | `static void compare(Tally t, Nations a, Nations b, int nation, int last, String what)` | Whether every component of a nation's years agree between two paths, FIRST_YEAR to last, and the goods' prices. |

### 1. ONE SEED, ONE PATH (lines 206-236)

| line | len | member | says |
|---:|---:|---|---|
| 210 | 26 | `static void theSeed(String scope, WorldData data)` |  |

### 2. HISTORICAL EQUALS THE FILE TO 2025 (lines 237-309)

| line | len | member | says |
|---:|---:|---|---|
| 246 | 22 | `static void theStandInCentury(String scope, WorldData data)` | EP1: the world file begins in 2000 and the nations in FIRST_YEAR, so a Historical world stands the stand-in century (WorldData's) before the file's years: every nation's prices rise as the US's do, its people, output ... |
| 269 | 40 | `static void theFile(String scope, WorldData data)` |  |

### 3. RANDOM'S SWINGS MATCH ITS DIALS (lines 310-401)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 87 | `static void theSwings(WorldData data)` |  |

### 3b. GOODS (lines 402-442)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 36 | `static void theGoods(WorldData data)` |  |

### 4. HISTORICAL'S FUTURE SWINGS ARE THE SMALLER (lines 443-478)

| line | len | member | says |
|---:|---:|---|---|
| 447 | 31 | `static void theHistoricalFuture(WorldData data)` |  |

### 5. PEGS HOLD (lines 479-531)

| line | len | member | says |
|---:|---:|---|---|
| 483 | 34 | `static void thePegs(WorldData data)` |  |
| 518 | 13 | `static void theFullPegs(WorldData full)` |  |

### 6. OUTPUT NEVER NEGATIVE (lines 532-561)

| line | len | member | says |
|---:|---:|---|---|
| 536 | 23 | `static void theNeverNegative(String scope, WorldData data, int seeds)` |  |
| 560 | 1 | `static boolean ok(double x)` |  |

### 7. AFTER 2100 (lines 562-594)

| line | len | member | says |
|---:|---:|---|---|
| 566 | 28 | `static void theAfter2100(WorldData data)` |  |

### 8. MISSING FIGURES (lines 595-680)

| line | len | member | says |
|---:|---:|---|---|
| 599 | 62 | `static void theMissingFigures(WorldData data)` |  |
| 663 | 17 | `static void theFullCounts(WorldData full)` | What Random counts across all the nations, against the spec's one country-year in twenty that grew less than -5% (over the 100 years after the first, as the WDI's 2000-2024 gave it) and Barro's crises: printed, for th... |

