# PolicyRates.java - 334 lines · 24 methods · 13 constants · model

`ham/citybuildersim/PolicyRates.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The policy rates of the eight central banks of the moneys the vault holds (XR4; spec-exchange-rates 3.8, Jerus's calls 9 and 11): the Federal
> Reserve's, the ECB's, the Bank of Japan's, the Bank of England's, the People's Bank's, the Bank of Canada's, the Reserve Bank of Australia's and the
> Swiss National Bank's, a month at a time. On the Historical path their real rates January 2000 to December 2025 (policy-rates.tsv), then a rule;
> on Random the rule from February 2000. The US's rate is the world's price of money to the city (DebtManager.worldRate()). A pure model: it reads
> the table, the nations' price levels through WorldMoneys and its own state, and it is handed to the city by Game.
> 
> WHY THIS EXISTS. The world's price of money was DebtManager.WORLD_BASE_RATE, a constant 2%, against a world whose inflation is a path: the world's
> real rate swung with the path (in slot 3's world, 0.4% a year, which made the world's real rate 1.6%), and the city's real gap against the world
> with it. The D$ is a small open economy's money, and a small open economy's money is priced against the US's rate (the US$ is on one side of 89%
> of all currency trades; spec-exchange-rates 3.8, star 9). The vault earned nothing on the moneys it held, and the carry and the hot money had no
> rate abroad to compare the dial with. These are the rates, one for each of the eight.
> 
> THE RULE. From its first month a bank's target is its neutral real rate plus the inflation it reads plus TAYLOR_WEIGHT of that inflation's
> distance over its own target, never under LOWER_BOUND (Taylor, 1993). A shadow rate closes half its gap to the target in RULE_HALF_LIFE months (the
> smoothing an estimated rule shows), and the policy rate moves in whole STEPs toward the shadow, never past it: the step central banks move in.
> The inflation it reads is the anchor nation's price level over the year before (WorldMoneys.priceLevel), the year 2000's pace for the first
> twelve months. A bank whose money has no anchor nation holds its last rate.
> 
> THE SAVE is the month and each bank's rate, shadow, target and inflation (toSaveArray()), nothing NaN so it goes through JSON; a PolicyRates of
> the same moneys that restores it steps on as this one does.

**Uses:** [WorldMoneys](WorldMoneys.md) (10), [Nations](Nations.md) (1)

**Used by (8):** [Game](Game.md), [GlobeCheck](GlobeCheck.md), [HistorySave](HistorySave.md), [InterestAbroadCheck](InterestAbroadCheck.md), [NationsMap](NationsMap.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 38 | THE DIALS - each with its source, or (est.) the game's estimate |
| 79 | THE TABLE - the real rates, loaded once |
| 182 | THE BANKS - made once from the moneys |
| 192 | · the state: saved |
| 212 | THE MONTH |
| 263 | WHAT A CALLER READS - a code that is not one of the eight reads NaN |
| 287 | THE SAVE - the state, not what the table and the moneys give |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `PolicyRates.STEP` | `.0025` | The step a policy rate moves in: a quarter point, the step central banks move in. |
| 46 | `PolicyRates.LOWER_BOUND` | `0` | The least the rule's target is: the rule does not go below zero (est.: the negative rates of 2014-2024 are replayed on Historical, and the banks that ran them had left them by 2024). |
| 49 | `PolicyRates.TAYLOR_WEIGHT` | `.5` | Half a point of rate over inflation for each point inflation runs over the target (Taylor, 1993). |
| 52 | `PolicyRates.RULE_HALF_LIFE` | `9` | The months for the rate's shadow to close half its gap to the rule's target (est.: an estimated rule's quarterly smoothing of about 0.8). |
| 58 | `PolicyRates.TARGET` | `{.02,.02,.02,.02,.02,.02,.025,.01 }` | Each bank's inflation target, in WorldMoneys.EIGHT's order: 2% for all but the Reserve Bank of Australia's 2-3% band at its middle and the Swiss National Bank's 0-2% at its middle; China's "around 2%" since 2025. |
| 65 | `PolicyRates.NEUTRAL_REAL` | `{.01, 0, -.005,.01,.01,.0075,.01, 0 }` | Each bank's neutral real rate, in WorldMoneys.EIGHT's order: its own published long-run neutral nominal rate less its target (est.: the Fed's 3%, the ECB's about 2%, the Bank of Japan's 1-2.5% range low, the Bank of E... |
| 68 | `PolicyRates.CLOSE` | `1 - StrictMath.pow(.5, 1.0 / RULE_HALF_LIFE)` | The share of the gap to its target the shadow closes in a month: one less 0.5 to the 1 over RULE_HALF_LIFE. |
| 71 | `PolicyRates.MONEYS` | `8` | The banks, in WorldMoneys.EIGHT's order. |
| 74 | `PolicyRates.MONTHS` | `WorldMoneys.HISTORICAL_RULE_START` | The months the table covers: January 2000 to December 2025, the world file's (WorldMoneys.HISTORICAL_RULE_START). |
| 77 | `PolicyRates.RESOURCE` | `"/policy-rates.tsv"` | Where the table is, on the classpath. |
| 292 | `PolicyRates.SAVE_FORMAT` | `1` | The current format of toSaveArray. |
| 294 | `PolicyRates.HEADER` | `3` |  |
| 297 | `PolicyRates.NOT_READ` | `- 1e9` | What a target or an inflation not read yet is saved as (the state is no NaN, so that it goes through JSON). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 83 | `private static double[][] table` |  |
| 186 | `private final WorldMoneys moneys` |  |
| 187 | `private final int ruleStart` |  |
| 190 | `private final int[] anchor` | Each bank's anchor nation (1-based), whose price level it reads: the US's for the dollar; 0, held, for a money with none. |
| 193 | `private int month` |  |
| 194 | `private final double[] rate` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 299 | **type** `public final class PolicyRates` | The policy rates of the eight central banks of the moneys the vault holds (XR4; spec-exchange-rates 3.8, Jerus's calls 9 and 11): the Federal Reserve's, the ECB's, the Bank of Japan's, the Bank of England's, the Peopl... |

### THE DIALS - each with its source, or (est.) the game's estimate (lines 38-78)

### THE TABLE - the real rates, loaded once (lines 79-181)

| line | len | member | says |
|---:|---:|---|---|
| 86 | 4 | `private static synchronized double[][] table()` | The table: each bank's rate in each month, a fraction a year; loaded the first time it is asked, and refused, naming the line, if it is not whole. |
| 91 | 8 | `private static double[][] load()` |  |
| 104 | 39 | `static double[][] parse(java.io.Reader source) throws IOException` | A table's text read: one line for each change (code, tab, yyyy-mm, tab, percent a year), the comment lines and the blank ones skipped; each bank's rate in each of the 312 months. |
| 145 | 13 | `private static int monthOf(String text, String where)` | The month index of "yyyy-mm" (months since January 2000); refused, naming the line, if it is not a month of 2000 to 2025. |
| 160 | 4 | `public static double historical(String code, int t)` | A bank's real policy rate in a month (months since January 2000, WorldMoneys.monthIndex), a fraction a year: the last change at or before it. |
| 166 | 3 | `private static double historical(int k, int t)` | The bank k's (WorldMoneys.EIGHT's order) real rate in the month, NaN outside the table. |
| 171 | 4 | `public static double inflationTargetOf(String code)` | The inflation target of a bank, a fraction a year; NaN for a code that is not one of the eight. |
| 177 | 4 | `public static double neutralRealOf(String code)` | The neutral real rate of a bank, a fraction a year; NaN for a code that is not one of the eight. |

### THE BANKS - made once from the moneys (lines 182-191)

### the state: saved (lines 192-211)

| line | len | member | says |
|---:|---:|---|---|
| 200 | 11 | `public PolicyRates(WorldMoneys moneys)` | The banks of the moneys' world, standing in January 2000 (month() is 0): each bank's rate and shadow the table's of that month. |

### THE MONTH (lines 212-262)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 1 | `public int month()` | The month now, months since January 2000: 0 after the constructor. |
| 220 | 1 | `public int ruleStart()` | The first month the rule rules: 1 on Random, 312 on Historical (the moneys'). |
| 223 | 1 | `public boolean ruling()` | Whether the rule rules this month (Historical replays the table before it). |
| 230 | 19 | `public void step(int t)` | Steps to month t, which has to be the next. |
| 251 | 3 | `static double targetFor(int k, double inflation)` | The rule's target for bank k (WorldMoneys.EIGHT's order) reading this inflation: its neutral real rate, the inflation and TAYLOR_WEIGHT of the inflation's distance over the bank's target, not under LOWER_BOUND. |
| 256 | 6 | `private double inflationAt(int nation, int t)` | A nation's inflation at month t: its price level over the year before's, less one; for the first twelve months the year 2000's pace. |

### WHAT A CALLER READS - a code that is not one of the eight reads NaN (lines 263-286)

| line | len | member | says |
|---:|---:|---|---|
| 267 | 1 | `private static int at(String code)` |  |
| 270 | 1 | `public double rate(String code)` | A bank's policy rate this month, a fraction a year; NaN for a code that is not one of the eight. |
| 273 | 1 | `public double rate(int k)` | The policy rate of the bank numbered k in WorldMoneys.EIGHT's order, a fraction a year; NaN for a number outside it. |
| 276 | 1 | `public double usRate()` | The US's policy rate this month, a fraction a year: the world's price of money to the city. |
| 279 | 1 | `public double shadow(String code)` | Where the rule has a bank's rate, a fraction a year: its shadow, which the rate follows in whole steps. |
| 282 | 1 | `public double target(String code)` | This month's rule target for a bank, a fraction a year; NaN before the rule rules (and for a bank that has read no inflation). |
| 285 | 1 | `public double inflation(String code)` | The inflation the rule last read for a bank, a fraction a year; NaN before the rule rules. |

### THE SAVE - the state, not what the table and the moneys give (lines 287-334)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 12 | `public double[] toSaveArray()` | The state as numbers: the format, the count of banks, the month, then each bank's rate, shadow, target and inflation. |
| 314 | 20 | `public void restore(double[] saved)` | Puts the state back from toSaveArray; throws IllegalArgumentException, and changes nothing, for a save of another format, another count of banks or another length. |

