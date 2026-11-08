# PolicyRates.java - 357 lines · 25 methods · 15 constants · model

`ham/citybuildersim/PolicyRates.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The policy rates of the eight central banks of the moneys the vault holds (XR4; spec-exchange-rates 3.8, Jerus's calls 9 and 11): the Federal
> Reserve's, the ECB's, the Bank of Japan's, the Bank of England's, the People's Bank's, the Bank of Canada's, the Reserve Bank of Australia's and the
> Swiss National Bank's, a month at a time. On the Historical path their real rates January 2000 to December 2025 (policy-rates.tsv), and the rule in
> the months on either side of them: the stand-in century before January 2000 (EP1) and the months after December 2025; on Random the rule from the
> second month of the calendar (February 1900). The US's rate is the world's price of money to the city (DebtManager.worldRate()). A pure model: it reads
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
> The inflation it reads is the anchor nation's price level over the year before (WorldMoneys.priceLevel), the first year's pace for the first
> twelve months. A bank whose money has no anchor nation holds its last rate.
> 
> THE STAND-IN CENTURY (EP1, 2026-10-31). The table begins in January 2000 and the calendar in January 1900. A flat January 2000 rate (the Fed's 5.5%)
> held for a century against the US CPI's own path (-11% in 1921, +17% in 1918) would give real rates of -12% to +16% and kill the carry and the hot
> money, so before TABLE_FIRST_MONTH a Historical bank runs the rule exactly as Random's does, reading its anchor's inflation, which in the stand-in is
> the US CPI's (WorldData); the table replays from TABLE_FIRST_MONTH to the end of the world file's months, and the rule rules again after. Only the
> opening, month 0, is the table's first line. EP1b replays the real century's rates (LC1 cleared their sources), and the first stretch goes with it.
> 
> THE SAVE is the month and each bank's rate, shadow, target and inflation (toSaveArray()), nothing NaN so it goes through JSON; a PolicyRates of
> the same moneys that restores it steps on as this one does.

**Uses:** [WorldMoneys](WorldMoneys.md) (10), [Nations](Nations.md) (3)

**Used by (12):** [DebtMoneysCheck](DebtMoneysCheck.md), [Game](Game.md), [GlobeCheck](GlobeCheck.md), [HistorySave](HistorySave.md), [InterestAbroadCheck](InterestAbroadCheck.md), [NationsMap](NationsMap.md), [PartnersCheck](PartnersCheck.md), [PartnersView](PartnersView.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [TradeScreen](TradeScreen.md), [VaultView](VaultView.md)

## Sections

| line | section |
|---:|---|
| 45 | THE DIALS - each with its source, or (est.) the game's estimate |
| 97 | THE TABLE - the real rates, loaded once |
| 201 | THE BANKS - made once from the moneys |
| 211 | · the state: saved |
| 231 | THE MONTH |
| 286 | WHAT A CALLER READS - a code that is not one of the eight reads NaN |
| 310 | THE SAVE - the state, not what the table and the moneys give |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `PolicyRates.STEP` | `.0025` | The step a policy rate moves in: a quarter point, the step central banks move in. |
| 53 | `PolicyRates.LOWER_BOUND` | `0` | The least the rule's target is: the rule does not go below zero (est.: the negative rates of 2014-2024 are replayed on Historical, and the banks that ran them had left them by 2024). |
| 56 | `PolicyRates.TAYLOR_WEIGHT` | `.5` | Half a point of rate over inflation for each point inflation runs over the target (Taylor, 1993). |
| 59 | `PolicyRates.RULE_HALF_LIFE` | `9` | The months for the rate's shadow to close half its gap to the rule's target (est.: an estimated rule's quarterly smoothing of about 0.8). |
| 65 | `PolicyRates.TARGET` | `{.02,.02,.02,.02,.02,.02,.025,.01 }` | Each bank's inflation target, in WorldMoneys.EIGHT's order: 2% for all but the Reserve Bank of Australia's 2-3% band at its middle and the Swiss National Bank's 0-2% at its middle; China's "around 2%" since 2025. |
| 72 | `PolicyRates.NEUTRAL_REAL` | `{.01, 0, -.005,.01,.01,.0075,.01, 0 }` | Each bank's neutral real rate, in WorldMoneys.EIGHT's order: its own published long-run neutral nominal rate less its target (est.: the Fed's 3%, the ECB's about 2%, the Bank of Japan's 1-2.5% range low, the Bank of E... |
| 75 | `PolicyRates.CLOSE` | `1 - StrictMath.pow(.5, 1.0 / RULE_HALF_LIFE)` | The share of the gap to its target the shadow closes in a month: one less 0.5 to the 1 over RULE_HALF_LIFE. |
| 78 | `PolicyRates.MONEYS` | `8` | The banks, in WorldMoneys.EIGHT's order. |
| 86 | `PolicyRates.TABLE_FIRST_YEAR` | `2000` | The year the table's own lines begin in: January 2000, the first month of the BIS's CBPOL as transcribed. |
| 89 | `PolicyRates.TABLE_FIRST_MONTH` | `(TABLE_FIRST_YEAR - Nations.FIRST_YEAR) * 12` | The month the table's own lines begin in, counted from Nations.FIRST_YEAR: January of TABLE_FIRST_YEAR. |
| 92 | `PolicyRates.MONTHS` | `WorldMoneys.HISTORICAL_RULE_START` | The months the table covers: January of Nations.FIRST_YEAR to December 2025 (Nations.HISTORY_LAST_YEAR), the world file's reader's (WorldMoneys.HISTORICAL_RULE_START). |
| 95 | `PolicyRates.RESOURCE` | `"/policy-rates.tsv"` | Where the table is, on the classpath. |
| 315 | `PolicyRates.SAVE_FORMAT` | `1` | The current format of toSaveArray. |
| 317 | `PolicyRates.HEADER` | `3` |  |
| 320 | `PolicyRates.NOT_READ` | `- 1e9` | What a target or an inflation not read yet is saved as (the state is no NaN, so that it goes through JSON). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 101 | `private static double[][] table` |  |
| 205 | `private final WorldMoneys moneys` |  |
| 206 | `private final int ruleStart` |  |
| 209 | `private final int[] anchor` | Each bank's anchor nation (1-based), whose price level it reads: the US's for the dollar; 0, held, for a money with none. |
| 212 | `private int month` |  |
| 213 | `private final double[] rate` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 315 | **type** `public final class PolicyRates` | The policy rates of the eight central banks of the moneys the vault holds (XR4; spec-exchange-rates 3.8, Jerus's calls 9 and 11): the Federal Reserve's, the ECB's, the Bank of Japan's, the Bank of England's, the Peopl... |

### THE DIALS - each with its source, or (est.) the game's estimate (lines 45-96)

### THE TABLE - the real rates, loaded once (lines 97-200)

| line | len | member | says |
|---:|---:|---|---|
| 104 | 4 | `private static synchronized double[][] table()` | The table: each bank's rate in each month, a fraction a year; loaded the first time it is asked, and refused, naming the line, if it is not whole. |
| 109 | 8 | `private static double[][] load()` |  |
| 122 | 39 | `static double[][] parse(java.io.Reader source) throws IOException` | A table's text read: one line for each change (code, tab, yyyy-mm, tab, percent a year), the comment lines and the blank ones skipped; each bank's rate in each of the MONTHS months (a bank holds its first line's rate ... |
| 163 | 14 | `private static int monthOf(String text, String where)` | The month index of "yyyy-mm" (months since January of Nations.FIRST_YEAR); refused, naming the line, if it is not a month of TABLE_FIRST_YEAR to Nations.HISTORY_LAST_YEAR. |
| 179 | 4 | `public static double historical(String code, int t)` | A bank's real policy rate in a month (months since January of Nations.FIRST_YEAR, WorldMoneys.monthIndex), a fraction a year: the last change at or before it, its first line's before the table begins. |
| 185 | 3 | `private static double historical(int k, int t)` | The bank k's (WorldMoneys.EIGHT's order) real rate in the month, NaN outside the table. |
| 190 | 4 | `public static double inflationTargetOf(String code)` | The inflation target of a bank, a fraction a year; NaN for a code that is not one of the eight. |
| 196 | 4 | `public static double neutralRealOf(String code)` | The neutral real rate of a bank, a fraction a year; NaN for a code that is not one of the eight. |

### THE BANKS - made once from the moneys (lines 201-210)

### the state: saved (lines 211-230)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 11 | `public PolicyRates(WorldMoneys moneys)` | The banks of the moneys' world, standing in January of Nations.FIRST_YEAR (month() is 0): each bank's rate and shadow the table's of that month. |

### THE MONTH (lines 231-285)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 1 | `public int month()` | The month now, months since January of Nations.FIRST_YEAR: 0 after the constructor. |
| 239 | 1 | `public int ruleStart()` | The month the rule rules again after the table on Historical: MONTHS (1,512, January 2026); 1 on Random, which has no table (the moneys'). |
| 242 | 1 | `boolean replays(int t)` | Whether the table, not the rule, rules a month: Historical's real rates, TABLE_FIRST_MONTH to the month before ruleStart(). |
| 245 | 1 | `public boolean ruling()` | Whether the rule rules this month: every month but the opening (0) and Historical's replay of the table, so the stand-in century and the months after the table. |
| 253 | 19 | `public void step(int t)` | Steps to month t, which has to be the next. |
| 274 | 3 | `static double targetFor(int k, double inflation)` | The rule's target for bank k (WorldMoneys.EIGHT's order) reading this inflation: its neutral real rate, the inflation and TAYLOR_WEIGHT of the inflation's distance over the bank's target, not under LOWER_BOUND. |
| 279 | 6 | `private double inflationAt(int nation, int t)` | A nation's inflation at month t: its price level over the year before's, less one; for the first twelve months the year 2000's pace. |

### WHAT A CALLER READS - a code that is not one of the eight reads NaN (lines 286-309)

| line | len | member | says |
|---:|---:|---|---|
| 290 | 1 | `private static int at(String code)` |  |
| 293 | 1 | `public double rate(String code)` | A bank's policy rate this month, a fraction a year; NaN for a code that is not one of the eight. |
| 296 | 1 | `public double rate(int k)` | The policy rate of the bank numbered k in WorldMoneys.EIGHT's order, a fraction a year; NaN for a number outside it. |
| 299 | 1 | `public double usRate()` | The US's policy rate this month, a fraction a year: the world's price of money to the city. |
| 302 | 1 | `public double shadow(String code)` | Where the rule has a bank's rate, a fraction a year: its shadow, which the rate follows in whole steps. |
| 305 | 1 | `public double target(String code)` | This month's rule target for a bank, a fraction a year; NaN before the rule rules (and for a bank that has read no inflation). |
| 308 | 1 | `public double inflation(String code)` | The inflation the rule last read for a bank, a fraction a year; NaN before the rule rules. |

### THE SAVE - the state, not what the table and the moneys give (lines 310-357)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 12 | `public double[] toSaveArray()` | The state as numbers: the format, the count of banks, the month, then each bank's rate, shadow, target and inflation. |
| 337 | 20 | `public void restore(double[] saved)` | Puts the state back from toSaveArray; throws IllegalArgumentException, and changes nothing, for a save of another format, another count of banks or another length. |

