# WorldMoneysCheck.java - 1,201 lines · 47 methods · 14 constants · harnesses

`ham/citybuildersim/WorldMoneysCheck.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The world's moneys (batch WN4; spec-exchange-rates 3.7, spec-nations 5.1): WorldMoneys' monthly rate of each money to the US dollar -
> its parity path, the dollar cycle, the risk-off, the terms of trade and its own swing, with the pegs held and the soft ones broken by
> the rule - held to the dials it is drawn from, to the file where the file is the truth, and to the rule where a draw cannot be seen.
> 
> WHY. The rates were WN1's yearly draw, and a monthly model with five terms and a break rule can be wrong in ways no city would show: a
> seed that does not decide the path (a reload that continues another world), a Historical that is not the file, a cycle or a swing whose size
> or persistence is not the dial's (a world calmer or wilder than the sources say), a peg that drifts from its anchor or never breaks, a
> risk-off that moves a money by someone else's loading, a terms-of-trade step that lands in the wrong month or on the wrong money. The
> statistics are counted over seeded 26-year paths and each says against which constant it is judged; what has no constant to be judged by
> (the volatility a year, the half-life an economist fits) is printed beside the study's table.
> 
> THE DATA. On the fixture (WorldData.FIXTURE, twelve nations) always; the full world-1.bin where it is named (-Dcbs.world=<path>,
> WorldData.locate()), for the sections that need its 196 nations, 158 moneys and pegs, and a line says when it is not. The fixture has no pegs, so a
> peg is caused by giving Nations a regime (the yuan on the dollar, the peso on the yuan, a loop of the two).
> 
> What this has to prove:
>   0. THE MONEYS: every money a nation's year names on the path is carried, each with an anchor nation, and a nation's rate is a number.
>   1. ONE SEED, ONE PATH: two WorldMoneys of one path and seed step identically for 30 years, bit for bit; another seed differs; a save
>      at month 150 (and 320) restored into a fresh one steps on bit for bit, on both paths; a save of another file is refused.
>   2. HISTORICAL IS THE FILE to December 2025: every money with a figure of the file equals it, exactly, in every month; both paths
>      begin from the file's January 2000.
>   3. THE SWINGS ARE THE DIALS' (Random, seeded paths, the terms of trade null): the dollar cycle's innovation sd and persistence, and each
>      major's and an emerging float's own swing's, within 5% of CYCLE_SIGMA / CYCLE_HALF_LIFE and the money's sigma / OWN_HALF_LIFE; printed:
>      each one's volatility a year and the half-life an economist would fit, beside the study's table.
>   4. PEGS HOLD AND BREAK BY THE RULE: a held peg's rate over its anchor's is its ratio; a loop holds; no board ever breaks; the breaks
>      over the paths are the sum of ForeignAccounts.hazard(gap) over the held months, within 3 sd (Poisson).
>   5. A RISK-OFF moves the havens up and the high yielders down by their loadings, and fades by its half-life; starts counted against RISK_OFF_RATE.
>   6. THE TERMS OF TRADE: a January's rise of 0.1 in a commodity exporter's ln terms of trade moves its money by TOT_PASS x its commodity share
>      x 0.1, a third in each of January, February and March, and no other money; the market's two reads (commodityShare, lnTermsOfTrade)
>      are held to the file's pairs.
>   7. CROSS RATES go through the dollar; the dollar's rate is 1 always.
>   8. PARITY: with every other term quiet, a float's ln rate moves by the change in ln(P_anchor / P_US), and its constant is the mean
>      real gap of the file's years.
>   9. WHAT IT COSTS: the median month's step, printed (a finding: a harness judges a time only through Timing, and the timing harnesses are the gate's).
>  10. IN A CITY (the full file only): a trading town on the file runs 24 months with its moneys in the city's month every one and the D$
>      moved by the world; saved after 13 and reloaded, it continues the moneys and the D$ to the bit to month 24; the history keeps the
>      seven majors' rates, the cycle and the risk-off; a city on the literals has no moneys.
> 
> Every fixture causes its condition: a quiet WorldMoneys has no term but the one the section adds, a peg is given by a regime, a risk-off is
> forced, and the statistics are counted over enough seeded paths that a wrong dial would show.

**Uses:** [WorldMoneys](WorldMoneys.md) (72), [Nations](Nations.md) (57), [WorldData](WorldData.md) (20), [Game](Game.md) (10), [ForeignCheck](ForeignCheck.md) (7), [WorldMarket](WorldMarket.md) (5), [ForeignAccounts](ForeignAccounts.md) (4), [Good](Good.md) (4), [PartnerTrade](PartnerTrade.md) (4), [HistorySave](HistorySave.md) (4), [Founding](Founding.md) (2), [WorldDataCheck](WorldDataCheck.md) (1), [GameFiles](GameFiles.md) (1), [YearBook](YearBook.md) (1), [CityCalendar](CityCalendar.md) (1)

**Used by (1):** [VaultMoneysCheck](VaultMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 206 | THE RUNS - helpers the sections share |
| 240 | 0. THE MONEYS |
| 272 | 1. ONE SEED, ONE PATH |
| 345 | 2. HISTORICAL IS THE FILE |
| 388 | THE PASS - seeded paths, counted once, read by sections 3, 4 and 5 |
| 604 | 3. THE SWINGS ARE THE DIALS' |
| 632 | 4. PEGS HOLD AND BREAK BY THE RULE |
| 690 | 5. A RISK-OFF |
| 747 | 6. THE TERMS OF TRADE |
| 904 | 7. CROSS RATES |
| 932 | 8. PARITY |
| 996 | 9. WHAT IT COSTS |
| 1018 | THE FULL FILE'S OWN FINDINGS |
| 1042 | 10. IN A CITY - the moneys in the month, the D$ and the save (Game's THE WORLD'S MONEYS IN THE MONTH) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `WorldMoneysCheck.out` | `System.out` | Where the harness prints: the console. |
| 62 | `WorldMoneysCheck.BASE_SEED` | `9_101` | The seed the single-world sections use. |
| 65 | `WorldMoneysCheck.FULL_SEEDS` | `1_000, FIXTURE_SEEDS = 300` | The seeds the statistics are counted over: the full file's paths (26 years each), and the fixture's. |
| 68 | `WorldMoneysCheck.PATH_MONTHS` | `311` | The months of a path the statistics are counted over: January 2000 to November 2025, the 26-year run. |
| 71 | `WorldMoneysCheck.LONG_MONTHS` | `360` | The months the identity and restore sections run: thirty years, so Historical's rule runs 48 months. |
| 74 | `WorldMoneysCheck.DIAL_TOL` | `.05` | A measured sd or persistence is within this share of the dial it is drawn from (a pooled lag-1 fit over 311 months a path is biased low by about 1.3%). |
| 77 | `WorldMoneysCheck.SIGMAS` | `3` | A count of breaks or of risk-off starts is within this many standard deviations of its expected number. |
| 80 | `WorldMoneysCheck.EXACT` | `1e-12` | Two logs or two rates that should be one differ by less than this (the rates are exp of sums of a few logs). |
| 84 | `WorldMoneysCheck.FIXTURE_PEGS` | `Map.of("CHN", "peg:USD", "COL", "peg:CNY")` | Pegs the fixture is given: the yuan on the dollar, the peso on the yuan (a chain); and the loop of the two. |
| 85 | `WorldMoneysCheck.FIXTURE_LOOP` | `Map.of("CHN", "peg:COP", "COL", "peg:CNY")` |  |
| 88 | `WorldMoneysCheck.STUDY_CODES` | `{ "EUR", "JPY", "GBP", "CHF", "CAD", "AUD" }` | The study's table (spec-exchange-rates 3.7), for the findings: volatility a year model and real, the half-life an economist measures, the worst month. |
| 89 | `WorldMoneysCheck.STUDY` | `{ { 8.9, 9.3, 4.7, 8.3 }, { 9.9, 9.4, 4.4, 10.9 }, { 8.6, 8.5, 4.5, 8.8 }, { ...` |  |
| 90 | `WorldMoneysCheck.STUDY_EMERGING` | `{ 16.6, 14, 3.5, 26.5 }` |  |
| 1047 | `WorldMoneysCheck.quiet` | `new PrintStream(java.io.OutputStream.nullOutputStream())` | The game's narration sent nowhere. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `static int fails` |  |
| 99 | `int n, bad` |  |
| 100 | `String first` |  |
| 134 | `final String name` |  |
| 135 | `final WorldData data` |  |
| 136 | `final WorldMarket.Base base` |  |
| 137 | `final double[] shares` |  |
| 394 | `double xy, xx, yy` |  |
| 395 | `long n` |  |
| 421 | `final Acc cycle` |  |
| 422 | `final Acc[] own` |  |
| 423 | `final double[] sum12, sumSq12, halfLife, worst` |  |
| 424 | `final long[] n12` |  |
| 425 | `long riskStarts, ratioChecked, ratioBad, boardMonths, boardBreaks` |  |
| 426 | `final long[] breaks` |  |
| 427 | `final double[] hazard` |  |
| 428 | `final long[] faced` |  |
| 446 | `final Scope sc` |  |
| 447 | `final int seeds` |  |
| 448 | `final Map<String, String> regimes` |  |
| 449 | `final String[] watch` |  |
| 450 | `final String[] pegs, pegTarget` |  |
| 451 | `final boolean[] board` |  |
| 452 | `final Acc cycle` |  |
| 453 | `final Acc[] own` |  |
| 454 | `final double[] sum12, sumSq12` |  |
| 455 | `final long[] n12` |  |
| 456 | `final List<List<Double>> halfLives` |  |
| 457 | `long riskStarts, ratioChecked, ratioBad, boardMonths, boardBreaks` |  |
| 458 | `final long[] breaks, faced` |  |
| 459 | `final double[] hazard` |  |
| 460 | `final double millis` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 1147 | **type** `public class WorldMoneysCheck` | The world's moneys (batch WN4; spec-exchange-rates 3.7, spec-nations 5.1): WorldMoneys' monthly rate of each money to the US dollar - its parity path, the dollar cycle, the risk-off, the terms of trade and its own swi... |
| 92 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 98 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 102 | 7 | `void see(boolean ok, Supplier<String> what)` _(in WorldMoneysCheck.Tally)_ |  |
| 111 | 6 | `static void tally(String label, Tally t)` |  |
| 118 | 1 | `static boolean same(double a, double b)` |  |
| 121 | 4 | `static void within(String label, double measured, double dial, double tol)` | A measured figure judged against the constant it is drawn from, within a share of it. |
| 127 | 4 | `static void poisson(String label, double observed, double expected, double sd)` | A count judged against its expected number, within SIGMAS standard deviations of it. |
| 133 | 24 | **type** `static final class Scope` | The world file and what is built on it: the fixture or the full file, the nations' commodity shares of exports. |
| 139 | 7 | `Scope(String name, WorldData data)` _(in WorldMoneysCheck.Scope)_ |  |
| 147 | 3 | `Nations nations(Nations.Path path, long seed, Map<String, String> regimes)` _(in WorldMoneysCheck.Scope)_ |  |
| 151 | 1 | `WorldMoneys moneys(Nations nations)` _(in WorldMoneysCheck.Scope)_ |  |
| 153 | 1 | `WorldMoneys moneys(Nations.Path path, long seed, Map<String, String> regimes)` _(in WorldMoneysCheck.Scope)_ |  |
| 155 | 1 | `int nation(String iso3)` _(in WorldMoneysCheck.Scope)_ |  |
| 158 | 32 | `public static void main(String[] args) throws Exception` |  |
| 192 | 13 | `static void sections(Scope sc, Pass floats, Pass pegs, Map<String, String> pegRegimes)` | The sections both scopes run; floats is the pass over the file's own regimes, pegs the one the pegs are counted on (the fixture's with the regimes it is given). |

### THE RUNS - helpers the sections share (lines 206-239)

| line | len | member | says |
|---:|---:|---|---|
| 211 | 5 | `static double[] synthetic(int t, int nations)` | A January's ln terms of trade for the identity and restore sections: a made-up but deterministic figure per nation and year (the market's own are section 6's). |
| 218 | 3 | `static void run(WorldMoneys w, int last, int nations)` | Steps from the month after w's to last, with a January's terms of trade handed in each January. |
| 223 | 9 | `static boolean alike(WorldMoneys a, WorldMoneys b)` | Whether two WorldMoneys are in the same state: every money's rate, parity and shadow, the cycle, the risk-off, the swings and the terms of trade, bit for bit. |
| 234 | 5 | `static List<String> floats(WorldMoneys w)` | The floats: moneys with no peg that the rule rules (a Historical lira, with no nation on it, keeps its last rate). |

### 0. THE MONEYS (lines 240-271)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 27 | `static void theMoneys(Scope sc)` |  |

### 1. ONE SEED, ONE PATH (lines 272-344)

| line | len | member | says |
|---:|---:|---|---|
| 276 | 58 | `static void theSeed(Scope sc, Map<String, String> regimes)` |  |
| 336 | 8 | `static boolean refused(WorldMoneys w, double[] saved, String word)` | Whether restoring that save throws an IllegalArgumentException whose message holds the word. |

### 2. HISTORICAL IS THE FILE (lines 345-387)

| line | len | member | says |
|---:|---:|---|---|
| 349 | 38 | `static void theFile(Scope sc)` |  |

### THE PASS - seeded paths, counted once, read by sections 3, 4 and 5 (lines 388-603)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 25 | **type** `static final class Acc` | A lag-1 regression's sums: the persistence and the innovation sd. |
| 397 | 6 | `void add(double prev, double now)` _(in WorldMoneysCheck.Acc)_ |  |
| 404 | 6 | `void merge(Acc o)` _(in WorldMoneysCheck.Acc)_ |  |
| 411 | 1 | `double phi()` _(in WorldMoneysCheck.Acc)_ |  |
| 413 | 4 | `double innovationSd()` _(in WorldMoneysCheck.Acc)_ |  |
| 420 | 23 | **type** `static final class Part` | One seeded path's counts. |
| 430 | 12 | `Part(int watch, int pegs)` _(in WorldMoneysCheck.Part)_ |  |
| 445 | 158 | **type** `static final class Pass` | The paths of one scope: Random, seeds seeds, PATH_MONTHS months each, merged in seed order so that the figures do not depend on the threads. |
| 462 | 59 | `Pass(Scope sc, int seeds, Map<String, String> regimes)` _(in WorldMoneysCheck.Pass)_ |  |
| 522 | 62 | `private Part runSeed(long seed)` _(in WorldMoneysCheck.Pass)_ |  |
| 586 | 4 | `double volatility(int j)` _(in WorldMoneysCheck.Pass)_ | The sd of the 12-month log changes of a watched money, pooled over the paths. |
| 591 | 5 | `double mean(List<Double> list)` _(in WorldMoneysCheck.Pass)_ |  |
| 597 | 5 | `double median(List<Double> list)` _(in WorldMoneysCheck.Pass)_ |  |

### 3. THE SWINGS ARE THE DIALS' (lines 604-631)

| line | len | member | says |
|---:|---:|---|---|
| 608 | 23 | `static void theSwings(Scope sc, Pass pass)` |  |

### 4. PEGS HOLD AND BREAK BY THE RULE (lines 632-689)

| line | len | member | says |
|---:|---:|---|---|
| 636 | 53 | `static void thePegs(Scope sc, Pass pass, boolean given)` |  |

### 5. A RISK-OFF (lines 690-746)

| line | len | member | says |
|---:|---:|---|---|
| 694 | 52 | `static void theRiskOff(Scope sc, Pass pass)` |  |

### 6. THE TERMS OF TRADE (lines 747-903)

| line | len | member | says |
|---:|---:|---|---|
| 752 | 8 | `static int findExporter(Scope sc, WorldMoneys w, Nations n)` | The nation of the most commodity-heavy exports that is the anchor of its money and floats: Canada on the full file, whatever the fixture has. |
| 761 | 56 | `static void theTermsOfTrade(Scope sc)` |  |
| 819 | 84 | `static void theMarketReads(Scope sc)` | The market's two reads held to the file's pairs: the share of exports in bulk goods and the ln terms of trade, recomputed from the flows. |

### 7. CROSS RATES (lines 904-931)

| line | len | member | says |
|---:|---:|---|---|
| 908 | 23 | `static void theCross(Scope sc)` |  |

### 8. PARITY (lines 932-995)

| line | len | member | says |
|---:|---:|---|---|
| 937 | 7 | `static double lnRelative(Nations n, int anchor, int usa, int t)` | ln(P_a / P_US) in a month, log-linear between Januaries, from Nations itself. |
| 945 | 50 | `static void theParity(Scope sc)` |  |

### 9. WHAT IT COSTS (lines 996-1017)

| line | len | member | says |
|---:|---:|---|---|
| 1000 | 17 | `static void theCost(Scope sc)` |  |

### THE FULL FILE'S OWN FINDINGS (lines 1018-1041)

| line | len | member | says |
|---:|---:|---|---|
| 1022 | 19 | `static void theFullOnly(Scope all, Pass pass)` |  |

### 10. IN A CITY - the moneys in the month, the D$ and the save (Game's THE WORLD'S MONEYS IN THE MONTH) (lines 1042-1201)

| line | len | member | says |
|---:|---:|---|---|
| 1050 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 1057 | 1 | `static void play(Game city)` | One month of a city, the narration sent nowhere. |
| 1060 | 16 | `static Game town(String label)` | A trading town on the default founding, as ExchangeRateCheck builds it (its own copy: that one is tied to its harness's streams), on a scratch folder. |
| 1078 | 1 | `static boolean sameArray(double[] a, double[] b)` | Two arrays the same to the bit. |
| 1080 | 121 | `static void theCity()` |  |

