# ExchangeRateCheck.java - 1,709 lines · 59 methods · 14 constants · harnesses

`ham/citybuildersim/ExchangeRateCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

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
> ... (57 more lines in the source)

**Uses:** [ForeignAccounts](ForeignAccounts.md) (178), [LongPlaytest](LongPlaytest.md) (36), [CurrencyCheck](CurrencyCheck.md) (29), [Game](Game.md) (28), [PriceIndex](PriceIndex.md) (27), [PriceLevel](PriceLevel.md) (12), [WorldMoneys](WorldMoneys.md) (12), [ForeignCheck](ForeignCheck.md) (7), [Founding](Founding.md) (6), [GoldStandardCheck](GoldStandardCheck.md) (4), [PolicyDates](PolicyDates.md) (3), [GameFiles](GameFiles.md) (2), [TreasuryFund](TreasuryFund.md) (2), [MoneyAudit](MoneyAudit.md) (2)

**Used by (2):** [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [InterventionCheck](InterventionCheck.md)

## Sections

| line | section |
|---:|---|
| 208 | 0. parity is the basket with rent held to a third |
| 314 | 1. parity's trend is followed with no lag |
| 367 | 2. the market is a real float's |
| 506 | 3. the real rate holds a level |
| 603 | 4. the balance and the vault's cover |
| 657 | 5. interventions move the rate at the real size |
| 725 | 6. one seed, one path |
| 895 | 7. a save mid-swing continues exactly |
| 953 | 8. in a real city the D$ moves like a float |
| 1264 | 9. the game survives the spiral |
| 1371 | 10-14. THE WORLD REACHES THE D$ (WN4, 2026-10-30; ForeignAccounts, THE WORLD REACHES THE D$) |
| 1406 | 10. no world, main's rate |
| 1451 | 11. the cycle |
| 1489 | 12. the risk-off loading |
| 1585 | 13. the terms of trade |
| 1640 | 14. the moves add up |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 156 | `ExchangeRateCheck.TRADE` | `3_000 * PriceLevel.FACTOR, GDP = 6_000 * PriceLevel.FACTOR` | The trade a quiet currency settles on, each way, and the output it is weighed against: openness one. |
| 159 | `ExchangeRateCheck.SEED` | `20_261_021L` | The market's seed in the sections that draw one; the others are quiet. |
| 162 | `ExchangeRateCheck.OLD_PULL` | `.004` | The old pull toward parity, REVERSION, retired by XR1 (WHAT XR1 RETIRED): a month's share of the distance it closed. |
| 211 | `ExchangeRateCheck.FEES` | `{ 1, 1, 1, 1 }` | The four fees' prices a fixture's basket is fed: all at 1. |
| 213 | `ExchangeRateCheck.RENT_HEAVY` | `{.15,.60,.10,.05, 0 }, RENT_HEAVY_FEES = {.04,.03,.02,.01 }` | Spending that puts rent at 60% of the basket (groceries 15, meals 10, luxury 5, services 10 - the fee lines add to it), and spending that puts it at 30%. |
| 214 | `ExchangeRateCheck.RENT_LIGHT` | `{.30,.30,.20,.05, 0 }, RENT_LIGHT_FEES = {.06,.04,.03,.02 }` |  |
| 799 | `ExchangeRateCheck.SAMPLING_SES` | `5` | FS10: standard errors of sampling a bound allows, so that a currency inside the band its dial gives fails it by chance about once in a million. |
| 802 | `ExchangeRateCheck.SWING_LIMIT` | `6` | ...and the sigmas of a normal draw (and of the market's own spread) a path may not pass: six, a part in a billion a draw. |
| 1084 | `ExchangeRateCheck.RUN_CHANCE` | `1e-6` | FS10 (section 8): the chance a run one way, in a path whose moves are a coin's at a month, has of passing the bound the section holds it to: a part in a million. |
| 1184 | `ExchangeRateCheck.GOLD_HELD_VAULT_YEARS` | `2 * ForeignAccounts.INTERVENTION_LINEAR` | FS10 (section 8b): the vault the dated gold peg is held at, in years of GDP: twice INTERVENTION_LINEAR, because the hold's sale in a month is at most INTERVENTION_LINEAR of a year's GDP while the lift it makes stays i... |
| 1187 | `ExchangeRateCheck.RELEASED_GAP` | `8 * ForeignAccounts.MARKET_SIGMA` | ...the log gap the peg's shadow stands over the peg when August 1914 releases it: eight of the market's monthly swings, more than one month's float news can be. |
| 1190 | `ExchangeRateCheck.RETURN_NEWS` | `6 * ForeignAccounts.MARKET_SIGMA` | ...and the lift July 1926's vault is bought for is this much past the eve's, six of the market's swings, so that a month's news cannot leave the return's sale short of it. |
| 1193 | `ExchangeRateCheck.MARKET_SLOT` | `52, SHADOW_SLOT = 78, HELD_SLOT = 79` | ForeignAccounts.toSaveArray()'s slots for the market's level, the log of the last reprice's shadow rate and whether that reprice held the rate away from it (restore() reads them so). |
| 1654 | `ExchangeRateCheck.FOURTEEN_GOLD_COVER` | `10 * ForeignAccounts.COVER_RATIO` | Gold's cover in section 14's Gold city: ten times the law's, so that 120 months of the hold's sales (the deficit, the risk-off's two spells, the hot money leaving) leave it over a full cover, where doubt asks nothing ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 129 | `static int fails` |  |
| 130 | `static PrintStream out` |  |
| 131 | `static PrintStream quiet` |  |
| 957 | `int months, fromMonth60, longest, run, sign, far, defended, dealt, marketOver2, gateShut` |  |
| 958 | `double sum, sum2, worstAdd, biggest, gateSum, defendedUsd` |  |
| 961 | `double heldYears` | FS10 (section 8b): the dollars the dated gold peg's vault is held at or over, between months, in years of GDP (bought by name; 0: nothing held, as on main). |
| 963 | `int toppedUp` | The months the vault was topped up in, and the dollars (US$ thousands) bought to do it. |
| 964 | `double toppedUpUsd` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 127 | 1583 | **type** `public class ExchangeRateCheck` | Proves the D$ is a float that follows its parity and swings like a real currency (XR1, 2026-10-21): parity is the basket with rent held to a third, the trend is followed with no lag, the market is a seeded swing of a ... |
| 133 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 138 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 149 | 5 | `static void quietly(Runnable work)` | Runs a piece of work with the game's narration sent nowhere. |
| 165 | 7 | `static ForeignAccounts still(double exports, double imports, double gdp, long seed)` | A currency past settling on trade in balance, its vault at twice THIN_COVER (the purchase forgotten, a set-up and not a deal), its market seeded unless the seed is 0 (quiet). |
| 174 | 1 | `static ForeignAccounts calm()` | ...at the standard trade, and quiet. |
| 177 | 5 | `static void month(ForeignAccounts fx)` | One month of the standard trade, repriced. |
| 183 | 24 | `public static void main(String[] args) throws Exception` |  |

### 0. parity is the basket with rent held to a third (lines 208-313)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 3 | `static void feed(PriceIndex px, double[] prices, double[] spends, double[] feeSpends, int month)` |  |
| 220 | 93 | `static void parityIsTheCappedBasket()` |  |

### 1. parity's trend is followed with no lag (lines 314-366)

| line | len | member | says |
|---:|---:|---|---|
| 316 | 50 | `static void theTrendIsFollowed()` |  |

### 2. the market is a real float's (lines 367-505)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 5 | `static double[] moves(double[] lnRate)` | A path's monthly log moves of the rate: the first from the founding rate of one. |
| 376 | 5 | `static double mean(double[] x, int from)` |  |
| 383 | 5 | `static double sd(double[] x, int from)` | The population standard deviation of x from `from` on. |
| 390 | 8 | `static double autocorrelation(double[] x, int from)` | The first autocorrelation of x from `from` on. |
| 400 | 13 | `static double[] arSums(double[] x, int from)` | One path's AR(1) sums, [cross-product, lag's squares], both about the path's own means: pooled across paths they give a slope with a smaller error than one path's. |
| 415 | 11 | `static double[][] swing(ForeignAccounts fx, double exports, double imports, double gdp, int months)` | A still currency's path under a market: [ln rate, the market, ln of parity's trend], a month each. |
| 427 | 78 | `static void theMarketIsAFloats()` |  |

### 3. the real rate holds a level (lines 506-602)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 94 | `static void theRealRateHoldsALevel()` |  |

### 4. the balance and the vault's cover (lines 603-656)

| line | len | member | says |
|---:|---:|---|---|
| 606 | 10 | `static ForeignAccounts held(double exports, double imports, double capitalOut, int months, double[] firstMonth)` | A currency with the trade and the capital of its months, settled and then repriced for `months` months, its vault empty. |
| 617 | 39 | `static void theBalanceAndTheCover()` |  |

### 5. interventions move the rate at the real size (lines 657-724)

| line | len | member | says |
|---:|---:|---|---|
| 659 | 65 | `static void interventionsAtTheRealSize()` |  |

### 6. one seed, one path (lines 725-894)

| line | len | member | says |
|---:|---:|---|---|
| 728 | 19 | `static void drive(ForeignAccounts fx, int from, int to, java.util.List<double[]> trace)` | A currency fed a month of a city's weather, deterministic in the month's number: trade that swings, capital that comes and goes, a parity that drifts, a real gap that wanders, and the treasury's deals. |
| 748 | 9 | `static int differences(java.util.List<double[]> a, java.util.List<double[]> b)` |  |
| 759 | 16 | `static Game town(String label, Founding founding)` | A trading town on the default founding, as CurrencyCheck builds it; the rate it plays is what the founding's seed makes. |
| 777 | 8 | `static double[] play(Game city, int months)` | The town's rate, month by month, for this many months. |
| 787 | 10 | `static double[][] playMarket(Game city, int months)` | The town's rate and the market's level, month by month, for this many months ([0] the rates, [1] the market's levels). |
| 805 | 4 | `static double marketMoveSd()` | The market's monthly move's standard deviation, as section 2 derives it: MARKET_SIGMA x sqrt(1 + (1 - rho) / (1 + rho)), the swing fading as well as arriving. |
| 811 | 6 | `static double[] innovations(double[] market)` | The market's innovations read back off a path of its levels: m(t) - rho m(t-1), each MARKET_SIGMA times a standard normal draw (the first from a market of nothing). |
| 818 | 76 | `static void oneSeedOnePath()` |  |

### 7. a save mid-swing continues exactly (lines 895-952)

| line | len | member | says |
|---:|---:|---|---|
| 897 | 55 | `static void aSaveMidSwing()` |  |

### 8. in a real city the D$ moves like a float (lines 953-1263)

| line | len | member | says |
|---:|---:|---|---|
| 956 | 66 | **type** `static final class Tally` | What a real city's D$ did, counted a month at a time the way the playtest counts it (LongPlaytest.countTheFloat()), and checked against it. |
| 967 | 7 | `void step(Game g)` _(in ExchangeRateCheck.Tally)_ | Plays one month as the playtest plays it and counts it, then holds the vault where the section asked. |
| 976 | 13 | `double topUpTo(Game g, double wantUsd)` _(in ExchangeRateCheck.Tally)_ | The vault brought up to this many dollars by dollars bought by name (the treasury handed the cash it is short, as section 8's deal does), the purchase forgotten as a set-up and not the month's dealing; what it took, i... |
| 991 | 25 | `void count(Game g)` _(in ExchangeRateCheck.Tally)_ | Counts one month as the playtest counts it. |
| 1017 | 4 | `double volatility()` _(in ExchangeRateCheck.Tally)_ |  |
| 1023 | 6 | `static void play(Game g, int months, Tally tally)` |  |
| 1035 | 3 | `static Game playtestCity(String label, int target, Tally tally)` | The default playtest's city, founded and played as LongPlaytest.main plays it (its founding by hand, then its rhythm of skips and looks - the fixture AuctionCheck's real run borrows), up to this month. |
| 1039 | 43 | `static Game playtestCity(String label, int target, Tally tally, Founding founding)` |  |
| 1087 | 10 | `static int longestRun(double[] moves)` | The longest run of months one way in these moves, counted as the playtest counts it (a move under a billionth of a log goes neither way). |
| 1099 | 3 | `static int runBound(int months)` | A run of L months one way from a given month has the chance 2^(1-L) in coin-flip moves, so n months have n x 2^(1-L): the L where that is RUN_CHANCE, a run must be shorter. |
| 1104 | 5 | `static double beyond(double z)` | The chance a standard normal draw is past z in size, either way (Abramowitz and Stegun 7.1.26: the error is under 1.5e-7). |
| 1110 | 68 | `static void inARealCity()` |  |
| 1200 | 8 | `static void setTheShadowGap(ForeignAccounts fx, double gap)` | The peg's shadow set to stand this log gap over the rate, through the saved array: the last reprice's shadow, and the market's level moved the same distance (over the gate, which weighs it) so the float's rule - the s... |
| 1210 | 45 | `static void inThe1900City()` | R1f: the dated peg's release is a hold move, already recorded by the model, not unexplained market noise. |
| 1257 | 6 | `static void stepQuietly(Tally t, Game g)` | One counted month with the narration sent nowhere (LongPlaytest prints through a stream of its own). |

### 9. the game survives the spiral (lines 1264-1370)

| line | len | member | says |
|---:|---:|---|---|
| 1267 | 10 | `static String notANumber(Game g)` | The first figure of the city that is not a number, or null: the rate, the price index, the treasury's cash, GDP, M0 and the vault. |
| 1279 | 4 | `static boolean auditCloses(Game g)` | The audit closes as it does in the playtest (LongPlaytest.audit(), CurrencyCheck 4): no residual over a cent, or none over a ten-millionth of the money moved. |
| 1284 | 86 | `static void theGameSurvivesTheSpiral()` |  |

### 10-14. THE WORLD REACHES THE D$ (WN4, 2026-10-30; ForeignAccounts, THE WORLD REACHES THE D$) (lines 1371-1405)

| line | len | member | says |
|---:|---:|---|---|
| 1376 | 5 | `static void monthOf(ForeignAccounts fx, double trade, double gdp)` | One month of this trade against this output, repriced: the world, if any, was handed before. |
| 1383 | 5 | `static ForeignAccounts settledStill(double trade, double gdp)` | A currency settled on balanced trade, its market quiet and its fundamentals still: sixty months repriced with no world, so a month of nothing moves nothing. |
| 1390 | 3 | `static double lnMove(ForeignAccounts fx, double trade, double gdp)` | The rate's move this month in logs, the world handed before it. |
| 1395 | 7 | `static double lnMove(ForeignAccounts fx, double exports, double imports, double gdp)` | ...on this trade each way. |
| 1404 | 1 | `static boolean bits(double a, double b)` | Both the same double down to its bits (a -0.0 is not a 0.0). |

### 10. no world, main's rate (lines 1406-1450)

| line | len | member | says |
|---:|---:|---|---|
| 1408 | 42 | `static void noWorldIsMains()` |  |

### 11. the cycle (lines 1451-1488)

| line | len | member | says |
|---:|---:|---|---|
| 1453 | 35 | `static void theCycleMovesTheRate()` |  |

### 12. the risk-off loading (lines 1489-1584)

| line | len | member | says |
|---:|---:|---|---|
| 1492 | 6 | `static ForeignAccounts withCover(double exports, double imports, double months)` | A currency settled on trade of this much exports and imports with a vault of this many months of the imports, the purchase forgotten as a deal. |
| 1499 | 85 | `static void theLoadingIsTheTraits()` |  |

### 13. the terms of trade (lines 1585-1639)

| line | len | member | says |
|---:|---:|---|---|
| 1587 | 52 | `static void theTermsOfTrade()` |  |

### 14. the moves add up (lines 1640-1709)

| line | len | member | says |
|---:|---:|---|---|
| 1643 | 6 | `static void world(ForeignAccounts fx, int t)` | The world a section plays month t: the cycle swinging, a risk-off in two spells, hot money coming and going, a January's terms of trade. |
| 1656 | 53 | `static void theMovesAddUp()` |  |

