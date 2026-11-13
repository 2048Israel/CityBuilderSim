# WorldMoneysCheck.java - 1,215 lines · 48 methods · 15 constants · harnesses

`ham/citybuildersim/WorldMoneysCheck.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

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
>   1. ONE SEED, ONE PATH: two WorldMoneys of one path and seed step identically for 30 years (Historical: the whole replay of the file, the
>      stand-in century's 1,200 months included, and 48 months of the rule), bit for bit; another seed differs; a save at month 150 (and, a
>      few months into the rule, at 320 on Random and 8 months after the table's end on Historical) restored into a fresh one steps on bit for
>      bit, on both paths; a save of another file is refused.
>   2. HISTORICAL IS THE FILE to December 2025: every money with a figure of the file equals it, exactly, in every month; both paths
>      begin from the file's first January (the calendar's, 1900: the file's own 2000 held through the stand-in century).
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

**Uses:** [WorldMoneys](WorldMoneys.md) (74), [Nations](Nations.md) (66), [WorldData](WorldData.md) (23), [Game](Game.md) (10), [ForeignCheck](ForeignCheck.md) (7), [WorldMarket](WorldMarket.md) (5), [ForeignAccounts](ForeignAccounts.md) (4), [Good](Good.md) (4), [PartnerTrade](PartnerTrade.md) (4), [HistorySave](HistorySave.md) (4), [Founding](Founding.md) (2), [WorldDataCheck](WorldDataCheck.md) (1), [GameFiles](GameFiles.md) (1), [YearBook](YearBook.md) (1), [CityCalendar](CityCalendar.md) (1)

**Used by (1):** [VaultMoneysCheck](VaultMoneysCheck.md)

## Sections

| line | section |
|---:|---|
| 216 | THE RUNS - helpers the sections share |
| 250 | 0. THE MONEYS |
| 283 | 1. ONE SEED, ONE PATH |
| 357 | 2. HISTORICAL IS THE FILE |
| 402 | THE PASS - seeded paths, counted once, read by sections 3, 4 and 5 |
| 618 | 3. THE SWINGS ARE THE DIALS' |
| 646 | 4. PEGS HOLD AND BREAK BY THE RULE |
| 704 | 5. A RISK-OFF |
| 761 | 6. THE TERMS OF TRADE |
| 918 | 7. CROSS RATES |
| 946 | 8. PARITY |
| 1010 | 9. WHAT IT COSTS |
| 1032 | THE FULL FILE'S OWN FINDINGS |
| 1056 | 10. IN A CITY - the moneys in the month, the D$ and the save (Game's THE WORLD'S MONEYS IN THE MONTH) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `WorldMoneysCheck.out` | `System.out` | Where the harness prints: the console. |
| 64 | `WorldMoneysCheck.BASE_SEED` | `9_101` | The seed the single-world sections use. |
| 67 | `WorldMoneysCheck.FULL_SEEDS` | `1_000, FIXTURE_SEEDS = 300` | The seeds the statistics are counted over: the full file's paths (26 years each), and the fixture's. |
| 70 | `WorldMoneysCheck.PATH_MONTHS` | `311` | The months of a Random path the statistics are counted over: the 26-year run (the first January to the November 26 years on). |
| 73 | `WorldMoneysCheck.LONG_MONTHS` | `360` | The months the identity and restore sections run on Random: thirty years. |
| 76 | `WorldMoneysCheck.RULED_MONTHS` | `48` | The months Historical's rule runs in the identity and restore sections: 48, after the table's end. |
| 84 | `WorldMoneysCheck.DIAL_TOL` | `.05` | A measured sd or persistence is within this share of the dial it is drawn from (a pooled lag-1 fit over 311 months a path is biased low by about 1.3%). |
| 87 | `WorldMoneysCheck.SIGMAS` | `3` | A count of breaks or of risk-off starts is within this many standard deviations of its expected number. |
| 90 | `WorldMoneysCheck.EXACT` | `1e-12` | Two logs or two rates that should be one differ by less than this (the rates are exp of sums of a few logs). |
| 94 | `WorldMoneysCheck.FIXTURE_PEGS` | `Map.of("CHN", "peg:USD", "COL", "peg:CNY")` | Pegs the fixture is given: the yuan on the dollar, the peso on the yuan (a chain); and the loop of the two. |
| 95 | `WorldMoneysCheck.FIXTURE_LOOP` | `Map.of("CHN", "peg:COP", "COL", "peg:CNY")` |  |
| 98 | `WorldMoneysCheck.STUDY_CODES` | `{ "EUR", "JPY", "GBP", "CHF", "CAD", "AUD" }` | The study's table (spec-exchange-rates 3.7), for the findings: volatility a year model and real, the half-life an economist measures, the worst month. |
| 99 | `WorldMoneysCheck.STUDY` | `{ { 8.9, 9.3, 4.7, 8.3 }, { 9.9, 9.4, 4.4, 10.9 }, { 8.6, 8.5, 4.5, 8.8 }, { ...` |  |
| 100 | `WorldMoneysCheck.STUDY_EMERGING` | `{ 16.6, 14, 3.5, 26.5 }` |  |
| 1061 | `WorldMoneysCheck.quiet` | `new PrintStream(java.io.OutputStream.nullOutputStream())` | The game's narration sent nowhere. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 59 | `static int fails` |  |
| 109 | `int n, bad` |  |
| 110 | `String first` |  |
| 144 | `final String name` |  |
| 145 | `final WorldData data` |  |
| 146 | `final WorldMarket.Base base` |  |
| 147 | `final double[] shares` |  |
| 408 | `double xy, xx, yy` |  |
| 409 | `long n` |  |
| 435 | `final Acc cycle` |  |
| 436 | `final Acc[] own` |  |
| 437 | `final double[] sum12, sumSq12, halfLife, worst` |  |
| 438 | `final long[] n12` |  |
| 439 | `long riskStarts, ratioChecked, ratioBad, boardMonths, boardBreaks` |  |
| 440 | `final long[] breaks` |  |
| 441 | `final double[] hazard` |  |
| 442 | `final long[] faced` |  |
| 460 | `final Scope sc` |  |
| 461 | `final int seeds` |  |
| 462 | `final Map<String, String> regimes` |  |
| 463 | `final String[] watch` |  |
| 464 | `final String[] pegs, pegTarget` |  |
| 465 | `final boolean[] board` |  |
| 466 | `final Acc cycle` |  |
| 467 | `final Acc[] own` |  |
| 468 | `final double[] sum12, sumSq12` |  |
| 469 | `final long[] n12` |  |
| 470 | `final List<List<Double>> halfLives` |  |
| 471 | `long riskStarts, ratioChecked, ratioBad, boardMonths, boardBreaks` |  |
| 472 | `final long[] breaks, faced` |  |
| 473 | `final double[] hazard` |  |
| 474 | `final double millis` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 1159 | **type** `public class WorldMoneysCheck` | The world's moneys (batch WN4; spec-exchange-rates 3.7, spec-nations 5.1): WorldMoneys' monthly rate of each money to the US dollar - its parity path, the dollar cycle, the risk-off, the terms of trade and its own swi... |
| 79 | 3 | `static int longMonths(Nations.Path path)` | The last month the identity and restore sections step a path to: Random's thirty years; Historical's whole replay and RULED_MONTHS of the rule. |
| 102 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 108 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 112 | 7 | `void see(boolean ok, Supplier<String> what)` _(in WorldMoneysCheck.Tally)_ |  |
| 121 | 6 | `static void tally(String label, Tally t)` |  |
| 128 | 1 | `static boolean same(double a, double b)` |  |
| 131 | 4 | `static void within(String label, double measured, double dial, double tol)` | A measured figure judged against the constant it is drawn from, within a share of it. |
| 137 | 4 | `static void poisson(String label, double observed, double expected, double sd)` | A count judged against its expected number, within SIGMAS standard deviations of it. |
| 143 | 24 | **type** `static final class Scope` | The world file and what is built on it: the fixture or the full file, the nations' commodity shares of exports. |
| 149 | 7 | `Scope(String name, WorldData data)` _(in WorldMoneysCheck.Scope)_ |  |
| 157 | 3 | `Nations nations(Nations.Path path, long seed, Map<String, String> regimes)` _(in WorldMoneysCheck.Scope)_ |  |
| 161 | 1 | `WorldMoneys moneys(Nations nations)` _(in WorldMoneysCheck.Scope)_ |  |
| 163 | 1 | `WorldMoneys moneys(Nations.Path path, long seed, Map<String, String> regimes)` _(in WorldMoneysCheck.Scope)_ |  |
| 165 | 1 | `int nation(String iso3)` _(in WorldMoneysCheck.Scope)_ |  |
| 168 | 32 | `public static void main(String[] args) throws Exception` |  |
| 202 | 13 | `static void sections(Scope sc, Pass floats, Pass pegs, Map<String, String> pegRegimes)` | The sections both scopes run; floats is the pass over the file's own regimes, pegs the one the pegs are counted on (the fixture's with the regimes it is given). |

### THE RUNS - helpers the sections share (lines 216-249)

| line | len | member | says |
|---:|---:|---|---|
| 221 | 5 | `static double[] synthetic(int t, int nations)` | A January's ln terms of trade for the identity and restore sections: a made-up but deterministic figure per nation and year (the market's own are section 6's). |
| 228 | 3 | `static void run(WorldMoneys w, int last, int nations)` | Steps from the month after w's to last, with a January's terms of trade handed in each January. |
| 233 | 9 | `static boolean alike(WorldMoneys a, WorldMoneys b)` | Whether two WorldMoneys are in the same state: every money's rate, parity and shadow, the cycle, the risk-off, the swings and the terms of trade, bit for bit. |
| 244 | 5 | `static List<String> floats(WorldMoneys w)` | The floats: moneys with no peg that the rule rules (a Historical lira, with no nation on it, keeps its last rate). |

### 0. THE MONEYS (lines 250-282)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 28 | `static void theMoneys(Scope sc)` |  |

### 1. ONE SEED, ONE PATH (lines 283-356)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 59 | `static void theSeed(Scope sc, Map<String, String> regimes)` |  |
| 348 | 8 | `static boolean refused(WorldMoneys w, double[] saved, String word)` | Whether restoring that save throws an IllegalArgumentException whose message holds the word. |

### 2. HISTORICAL IS THE FILE (lines 357-401)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 40 | `static void theFile(Scope sc)` |  |

### THE PASS - seeded paths, counted once, read by sections 3, 4 and 5 (lines 402-617)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 25 | **type** `static final class Acc` | A lag-1 regression's sums: the persistence and the innovation sd. |
| 411 | 6 | `void add(double prev, double now)` _(in WorldMoneysCheck.Acc)_ |  |
| 418 | 6 | `void merge(Acc o)` _(in WorldMoneysCheck.Acc)_ |  |
| 425 | 1 | `double phi()` _(in WorldMoneysCheck.Acc)_ |  |
| 427 | 4 | `double innovationSd()` _(in WorldMoneysCheck.Acc)_ |  |
| 434 | 23 | **type** `static final class Part` | One seeded path's counts. |
| 444 | 12 | `Part(int watch, int pegs)` _(in WorldMoneysCheck.Part)_ |  |
| 459 | 158 | **type** `static final class Pass` | The paths of one scope: Random, seeds seeds, PATH_MONTHS months each, merged in seed order so that the figures do not depend on the threads. |
| 476 | 59 | `Pass(Scope sc, int seeds, Map<String, String> regimes)` _(in WorldMoneysCheck.Pass)_ |  |
| 536 | 62 | `private Part runSeed(long seed)` _(in WorldMoneysCheck.Pass)_ |  |
| 600 | 4 | `double volatility(int j)` _(in WorldMoneysCheck.Pass)_ | The sd of the 12-month log changes of a watched money, pooled over the paths. |
| 605 | 5 | `double mean(List<Double> list)` _(in WorldMoneysCheck.Pass)_ |  |
| 611 | 5 | `double median(List<Double> list)` _(in WorldMoneysCheck.Pass)_ |  |

### 3. THE SWINGS ARE THE DIALS' (lines 618-645)

| line | len | member | says |
|---:|---:|---|---|
| 622 | 23 | `static void theSwings(Scope sc, Pass pass)` |  |

### 4. PEGS HOLD AND BREAK BY THE RULE (lines 646-703)

| line | len | member | says |
|---:|---:|---|---|
| 650 | 53 | `static void thePegs(Scope sc, Pass pass, boolean given)` |  |

### 5. A RISK-OFF (lines 704-760)

| line | len | member | says |
|---:|---:|---|---|
| 708 | 52 | `static void theRiskOff(Scope sc, Pass pass)` |  |

### 6. THE TERMS OF TRADE (lines 761-917)

| line | len | member | says |
|---:|---:|---|---|
| 766 | 8 | `static int findExporter(Scope sc, WorldMoneys w, Nations n)` | The nation of the most commodity-heavy exports that is the anchor of its money and floats: Canada on the full file, whatever the fixture has. |
| 775 | 56 | `static void theTermsOfTrade(Scope sc)` |  |
| 833 | 84 | `static void theMarketReads(Scope sc)` | The market's two reads held to the file's pairs: the share of exports in bulk goods and the ln terms of trade, recomputed from the flows. |

### 7. CROSS RATES (lines 918-945)

| line | len | member | says |
|---:|---:|---|---|
| 922 | 23 | `static void theCross(Scope sc)` |  |

### 8. PARITY (lines 946-1009)

| line | len | member | says |
|---:|---:|---|---|
| 951 | 7 | `static double lnRelative(Nations n, int anchor, int usa, int t)` | ln(P_a / P_US) in a month, log-linear between Januaries, from Nations itself. |
| 959 | 50 | `static void theParity(Scope sc)` |  |

### 9. WHAT IT COSTS (lines 1010-1031)

| line | len | member | says |
|---:|---:|---|---|
| 1014 | 17 | `static void theCost(Scope sc)` |  |

### THE FULL FILE'S OWN FINDINGS (lines 1032-1055)

| line | len | member | says |
|---:|---:|---|---|
| 1036 | 19 | `static void theFullOnly(Scope all, Pass pass)` |  |

### 10. IN A CITY - the moneys in the month, the D$ and the save (Game's THE WORLD'S MONEYS IN THE MONTH) (lines 1056-1215)

| line | len | member | says |
|---:|---:|---|---|
| 1064 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 1071 | 1 | `static void play(Game city)` | One month of a city, the narration sent nowhere. |
| 1074 | 16 | `static Game town(String label)` | A trading town on the default founding, as ExchangeRateCheck builds it (its own copy: that one is tied to its harness's streams), on a scratch folder. |
| 1092 | 1 | `static boolean sameArray(double[] a, double[] b)` | Two arrays the same to the bit. |
| 1094 | 121 | `static void theCity()` |  |

