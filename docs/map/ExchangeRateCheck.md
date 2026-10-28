# ExchangeRateCheck.java - 1,098 lines · 36 methods · 6 constants · harnesses

`ham/citybuildersim/ExchangeRateCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> Proves the D$ is a float that follows its parity and swings like a real
> currency (XR1, 2026-10-21): parity is the basket with rent held to a third,
> the trend is followed with no lag, the market is a seeded swing of a real
> float's size, a real rate gap holds a level, the balance and the vault's
> cover add theirs, interventions move the rate at the real size, one seed is
> one path across a save, and in a real city the mechanics fire. Not part of
> the game.
> 
> WHY THIS EXISTS. Jerus, 2026-10-21: "the current one is wierd". The study
> (docs/studies/spec-exchange-rates.md, section 1) found why, in four lines:
> the D$ moved 1.2% a year where a float moves 8-12%, in runs of up to 78
> months one way; a pull toward a parity that keeps moving settles at
> k / (k + g) of it - 0.55 at 4% a year - for ever; parity was 41% to 80% rent,
> which no border arbitrages, so the D$ sat 20-34% dearer than the traded
> basket; and the treasury's purchases of dollars made it dearer, not cheaper.
> What replaced the rule is ForeignAccounts, THE FLOAT: every piece a level,
> nothing a speed, each a line of its own. CurrencyCheck restates the
> old defence and real-rate premises on it; this is the float itself, from the
> design's own figures (the study's section 3, and its prototype beside it, in docs/studies/exchange-rates/).
> 
> What it has to prove, a section each, every one a fixture that causes the
> condition rather than waiting for it:
> 
>   0. PARITY IS THE BASKET WITH RENT HELD TO A THIRD. A bare PriceIndex whose
>      spending puts rent at 60% of the basket: once based, rent weighs
>      RENT_CAP in the level parity is struck from and the others their
>      weights scaled to fill the rest; rent's price doubled moves the parity
>      level by the capped weights and the index by the whole ones; at the
>      next rebasing the parity level does not jump; a basket with rent under
>      the cap has parity level and index the same.
>   1. PARITY'S TREND IS FOLLOWED WITH NO LAG. A quiet market, trade in
>      balance, the vault full and no gap: parity rising 4% a year for fifty
>      years leaves rate / parity within 2% of one from year ten (a pull of
>      .004 would have left .55); a burst of 50% in four years then 2.5% a
>      year keeps it within 5% from month twelve.
>   2. THE MARKET IS A REAL FLOAT'S. One seeded currency, the fundamentals
>      still, twelve thousand months: the monthly move's standard deviation is
>      MARKET_SIGMA x sqrt(1 + (1 - rho) / (1 + rho)) and a year's about 8%;
>      the market's own half-life, measured as an economist would, is
>      MARKET_HALF_LIFE; the monthly moves are a random walk at a month. At
>      half of OPEN_ENOUGH the rate carries half of the market, and below
>      MIN_TRADE of trade none of it.
>   3. THE REAL RATE HOLDS A LEVEL, 60% AT ONCE AND THE REST WITHIN A YEAR OR
>      TWO. A gap held 240 months puts the rate where gapLevelFor() says; a
>      step in it shows GAP_IMPACT and a share of the rest in its month and
>      halves what is still to come every GAP_BUILD_HALF_LIFE months; the
>      level is capped; slot 3's widest gap holds the D$ a quarter dearer; a
>      gap that closes gives the gain back.
>   4. THE BALANCE AND THE VAULT'S COVER. A deficit of a tenth of GDP arrives
>      at BALANCE_WEIGHT x 0.1, half in its first month; a deficit of GDP's
>      whole at BALANCE_CAP; a surplus the other way; capital sent abroad that
>      recycles a surplus leaves nothing. An empty vault costs RISK_PREMIUM, a
>      vault of THIN_COVER months nothing, of half of it half.
>   5. INTERVENTIONS MOVE THE RATE AT THE REAL SIZE. A purchase of 1% of a
>      year's GDP moves it INTERVENTION_IMPACT x 1%, half of it still felt six
>      months on; a sale the other way; the twins of the study's 1.4 - one
>      buys a year's imports of dollars - part the other way from the 7.7%
>      dearer the old rule left; and the defence's sale lifts the rate as the
>      treasury's sale of the same amount does.
>   6. ONE SEED, ONE PATH. The same seed and months are the same rates to the
> ... (22 more lines in the source)

**Uses:** [ForeignAccounts](ForeignAccounts.md) (90), [LongPlaytest](LongPlaytest.md) (35), [PriceIndex](PriceIndex.md) (27), [CurrencyCheck](CurrencyCheck.md) (24), [Game](Game.md) (23), [ForeignCheck](ForeignCheck.md) (7), [Founding](Founding.md) (5), [GameFiles](GameFiles.md) (2), [TreasuryFund](TreasuryFund.md) (2), [MoneyAudit](MoneyAudit.md) (1)

**Used by (1):** [CurrencyRegimeCheck](CurrencyRegimeCheck.md)

## Sections

| line | section |
|---:|---|
| 167 | 0. parity is the basket with rent held to a third |
| 273 | 1. parity's trend is followed with no lag |
| 326 | 2. the market is a real float's |
| 465 | 3. the real rate holds a level |
| 562 | 4. the balance and the vault's cover |
| 616 | 5. interventions move the rate at the real size |
| 684 | 6. one seed, one path |
| 789 | 7. a save mid-swing continues exactly |
| 847 | 8. in a real city the D$ moves like a float |
| 992 | 9. the game survives the spiral |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 121 | `ExchangeRateCheck.TRADE` | `3_000, GDP = 6_000` | The trade a quiet currency settles on, each way, and the output it is weighed against: openness one. |
| 124 | `ExchangeRateCheck.SEED` | `20_261_021L` | The market's seed in the sections that draw one; the others are quiet. |
| 127 | `ExchangeRateCheck.OLD_PULL` | `.004` | The old pull toward parity, REVERSION, retired by XR1 (WHAT XR1 RETIRED): a month's share of the distance it closed. |
| 170 | `ExchangeRateCheck.FEES` | `{ 1, 1, 1, 1 }` | The four fees' prices a fixture's basket is fed: all at 1. |
| 172 | `ExchangeRateCheck.RENT_HEAVY` | `{.15,.60,.10,.05, 0 }, RENT_HEAVY_FEES = {.04,.03,.02,.01 }` | Spending that puts rent at 60% of the basket (groceries 15, meals 10, luxury 5, services 10 - the fee lines add to it), and spending that puts it at 30%. |
| 173 | `ExchangeRateCheck.RENT_LIGHT` | `{.30,.30,.20,.05, 0 }, RENT_LIGHT_FEES = {.06,.04,.03,.02 }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 94 | `static int fails` |  |
| 95 | `static PrintStream out` |  |
| 96 | `static PrintStream quiet` |  |
| 851 | `int months, fromMonth60, longest, run, sign, far, defended, dealt, marketOver2, gateShut` |  |
| 852 | `double sum, sum2, worstAdd, biggest, gateSum, defendedUsd` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 1007 | **type** `public class ExchangeRateCheck` | Proves the D$ is a float that follows its parity and swings like a real currency (XR1, 2026-10-21): parity is the basket with rent held to a third, the trend is followed with no lag, the market is a seeded swing of a ... |
| 98 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 103 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 114 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 130 | 7 | `static ForeignAccounts still(double exports, double imports, double gdp, long seed)` | A currency past settling on trade in balance, its vault at twice THIN_COVER (the purchase forgotten, a set-up and not a deal), its market seeded unless the seed is 0 (quiet). |
| 139 | 1 | `static ForeignAccounts calm()` | ...at the standard trade, and quiet. |
| 142 | 5 | `static void month(ForeignAccounts fx)` | One month of the standard trade, repriced. |
| 148 | 18 | `public static void main(String[] args) throws Exception` |  |

### 0. parity is the basket with rent held to a third (lines 167-272)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 3 | `static void feed(PriceIndex px, double[] prices, double[] spends, double[] feeSpends, int month)` |  |
| 179 | 93 | `static void parityIsTheCappedBasket()` |  |

### 1. parity's trend is followed with no lag (lines 273-325)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 50 | `static void theTrendIsFollowed()` |  |

### 2. the market is a real float's (lines 326-464)

| line | len | member | says |
|---:|---:|---|---|
| 329 | 5 | `static double[] moves(double[] lnRate)` | A path's monthly log moves of the rate: the first from the founding rate of one. |
| 335 | 5 | `static double mean(double[] x, int from)` |  |
| 342 | 5 | `static double sd(double[] x, int from)` | The population standard deviation of x from `from` on. |
| 349 | 8 | `static double autocorrelation(double[] x, int from)` | The first autocorrelation of x from `from` on. |
| 359 | 13 | `static double[] arSums(double[] x, int from)` | One path's AR(1) sums, [cross-product, lag's squares], both about the path's own means: pooled across paths they give a slope with a smaller error than one path's. |
| 374 | 11 | `static double[][] swing(ForeignAccounts fx, double exports, double imports, double gdp, int months)` | A still currency's path under a market: [ln rate, the market, ln of parity's trend], a month each. |
| 386 | 78 | `static void theMarketIsAFloats()` |  |

### 3. the real rate holds a level (lines 465-561)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 94 | `static void theRealRateHoldsALevel()` |  |

### 4. the balance and the vault's cover (lines 562-615)

| line | len | member | says |
|---:|---:|---|---|
| 565 | 10 | `static ForeignAccounts held(double exports, double imports, double capitalOut, int months, double[] firstMonth)` | A currency with the trade and the capital of its months, settled and then repriced for `months` months, its vault empty. |
| 576 | 39 | `static void theBalanceAndTheCover()` |  |

### 5. interventions move the rate at the real size (lines 616-683)

| line | len | member | says |
|---:|---:|---|---|
| 618 | 65 | `static void interventionsAtTheRealSize()` |  |

### 6. one seed, one path (lines 684-788)

| line | len | member | says |
|---:|---:|---|---|
| 687 | 19 | `static void drive(ForeignAccounts fx, int from, int to, java.util.List<double[]> trace)` | A currency fed a month of a city's weather, deterministic in the month's number: trade that swings, capital that comes and goes, a parity that drifts, a real gap that wanders, and the treasury's deals. |
| 707 | 9 | `static int differences(java.util.List<double[]> a, java.util.List<double[]> b)` |  |
| 718 | 16 | `static Game town(String label, Founding founding)` | A trading town on the default founding, as CurrencyCheck builds it; the rate it plays is what the founding's seed makes. |
| 736 | 8 | `static double[] play(Game city, int months)` | The town's rate, month by month, for this many months. |
| 745 | 43 | `static void oneSeedOnePath()` |  |

### 7. a save mid-swing continues exactly (lines 789-846)

| line | len | member | says |
|---:|---:|---|---|
| 791 | 55 | `static void aSaveMidSwing()` |  |

### 8. in a real city the D$ moves like a float (lines 847-991)

| line | len | member | says |
|---:|---:|---|---|
| 850 | 35 | **type** `static final class Tally` | What a real city's D$ did, counted a month at a time the way the playtest counts it (LongPlaytest.countTheFloat()), and checked against it. |
| 855 | 24 | `void step(Game g)` _(in ExchangeRateCheck.Tally)_ | Plays one month as the playtest plays it and counts it. |
| 880 | 4 | `double volatility()` _(in ExchangeRateCheck.Tally)_ |  |
| 886 | 6 | `static void play(Game g, int months, Tally tally)` |  |
| 898 | 43 | `static Game playtestCity(String label, int target, Tally tally)` | The default playtest's city, founded and played as LongPlaytest.main plays it (its founding by hand, then its rhythm of skips and looks - the fixture AuctionCheck's real run borrows), up to this month. |
| 942 | 41 | `static void inARealCity()` |  |
| 985 | 6 | `static void stepQuietly(Tally t, Game g)` | One counted month with the narration sent nowhere (LongPlaytest prints through a stream of its own). |

### 9. the game survives the spiral (lines 992-1098)

| line | len | member | says |
|---:|---:|---|---|
| 995 | 10 | `static String notANumber(Game g)` | The first figure of the city that is not a number, or null: the rate, the price index, the treasury's cash, GDP, M0 and the vault. |
| 1007 | 4 | `static boolean auditCloses(Game g)` | The audit closes as it does in the playtest (LongPlaytest.audit(), CurrencyCheck 4): no residual over a cent, or none over a ten-millionth of the money moved. |
| 1012 | 86 | `static void theGameSurvivesTheSpiral()` |  |

