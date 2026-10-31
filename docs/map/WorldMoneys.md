# WorldMoneys.java - 798 lines · 56 methods · 31 constants · model

`ham/citybuildersim/WorldMoneys.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

> The world's moneys against the US dollar, a month at a time (WN4; spec-exchange-rates 3.7, spec-nations 5.1): each money's rate is its
> purchasing-power path, plus its loading on the dollar cycle, on the risk-off and on its nation's terms of trade, plus its own swing, with
> the pegs held and the soft ones broken by the rule the D$'s peg breaks by (ForeignAccounts.hazard). A pure model: it reads the world file and
> the nations' path, and nothing in the economy reads it yet.
> 
> WHY THIS EXISTS. The moneys' rates were WN1's yearly draw: relative parity as a drift and a 10% shock a year, with no dollar cycle, no
> risk-off, no terms of trade and no month. So nothing could make the yen rise in a panic or the Australian dollar fall with iron ore, the
> rate moved once a year in a step, and the D$ had no world to move with. This is the study's rule, every money of the world each month.
> 
> THE SHAPE. ln rate_k, units of money k a US dollar, is lnPPP_k(t) + b_k C(t) + r_k R(t) + T_k(t) + S_k(t) where t is the month since
> January 2000 (the file's month 0), C the dollar cycle, R the risk-off, T the terms of trade reached and S the money's own swing. Historical
> replays the file's rates to December 2025 and runs the rule from January 2026; Random runs it from February 2000. A city begins in January
> 2000 on both, so a WorldMoneys is made at month 0 (the file's rates; Random's state opened from their gaps from parity) and stepped a month at
> a time; on Historical the first 311 steps replay the file and the 311th opens the state from December 2025's gaps. Every draw is
> new Random(Nations.scramble(seed, who, t, what)), a pure function of the seed and the month, so one seed is one path and a reload continues it.
> 
> It copies what it needs from the file and keeps no reference to it; it keeps the Nations (for the price levels, the regimes and the money of
> a nation's year).

**Uses:** [Nations](Nations.md) (23), [WorldData](WorldData.md) (2), [CityCalendar](CityCalendar.md) (2), [ForeignAccounts](ForeignAccounts.md) (2)

**Used by (13):** [ExchangeRateCheck](ExchangeRateCheck.md), [ForeignAccounts](ForeignAccounts.md), [Game](Game.md), [HistorySave](HistorySave.md), [MapView](MapView.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [SaveFileCheck](SaveFileCheck.md), [TradeScreen](TradeScreen.md), [VaultMoneysCheck](VaultMoneysCheck.md), [WorldEconomy](WorldEconomy.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBook](YearBook.md)

## Sections

| line | section |
|---:|---|
| 35 | THE DIALS - each with its source: [R..] is spec-exchange-rates' list, (est.) the game's estimate |
| 84 | · the kinds: what each money loads on |
| 136 | THE MONEYS - made once from the file and the nations |
| 157 | · the state: saved |
| 167 | · worked, not saved |
| 273 | THE FILE'S FIGURES AND THE PARITY PATH |
| 372 | THE OPENING - the state from the gaps from parity |
| 400 | THE MONTH |
| 536 | THE DRAWS - pure functions of (seed, who, month, what) |
| 544 | WHAT A CALLER READS - a code the model does not carry reads NaN, null or false |
| 682 | THE NATIONS' PRICE LEVELS BY THE MONTH (WN4, D29) |
| 718 | THE SAVE - the state, not what the file and the path give |
| 780 | FOR THE CHECK - what a harness does that the game does not |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `WorldMoneys.CYCLE_SIGMA` | `.016` | One standard deviation of the dollar cycle's monthly innovation: 1.6% (the broad dollar's swings of 35-50% over 6-11 years [R5]). |
| 43 | `WorldMoneys.CYCLE_HALF_LIFE` | `60` | The dollar cycle's half-life in months: five years, one AR(1) for the world [R5]. |
| 46 | `WorldMoneys.RISK_OFF_RATE` | `1.0 / 96` | The chance a month a risk-off starts: once in eight years [R6]. |
| 49 | `WorldMoneys.RISK_OFF_HALF_LIFE` | `3` | The risk-off's half-life in months: it fades by half in three [R6]. |
| 52 | `WorldMoneys.RISK_OFF_SIZE_MIN` | `.5` | The least a risk-off's size is, the multiple of the loadings it moves the moneys by (est.: 2024's was smaller than one). |
| 55 | `WorldMoneys.RISK_OFF_SIZE_MAX` | `1.5` | The most a risk-off's size is (est.: 2008's was larger than one). |
| 58 | `WorldMoneys.OWN_HALF_LIFE` | `42` | A money's own swing's half-life in months: three and a half years, an AR(1) around its parity [R1]. |
| 61 | `WorldMoneys.TOT_PASS` | `.5` | The share of a nation's commodity share of exports its terms of trade pass into its money [R17][R18]: a better price of what it sells makes the money dearer. |
| 64 | `WorldMoneys.TOT_MONTHS` | `3` | The months a January's change in the terms of trade takes to reach the rate, a third in each of the year's first (est.). |
| 67 | `WorldMoneys.HISTORICAL_RULE_START` | `(Nations.HISTORY_LAST_YEAR + 1 - Nations.FIRST_YEAR) * 12` | The first month the rule rules on Historical: January 2026, after the file's 312 months (the file's real rates are replayed before it). |
| 70 | `WorldMoneys.RANDOM_RULE_START` | `1` | The first month the rule rules on Random: February 2000, the month after the file's January 2000 it opens from. |
| 73 | `WorldMoneys.CURRENCY_BOARDS` | `Set.of("HKD", "BND", "DJF", "XCD")` | The boards the file's pegs include, which never break: the game keeps no nation's vault, and a board breaks only if its vault falls under its money (est., the IMF's list [R21]). |
| 79 | `WorldMoneys.EIGHT` | `List.of("USD", "EUR", "JPY", "GBP", "CNY", "CAD", "AUD", "CHF")` | The eight moneys the IMF's COFER names, the vault's (spec-nations 5.2), in this order. |
| 82 | `WorldMoneys.MAJORS` | `List.of("EUR", "JPY", "GBP", "CHF", "CAD", "AUD")` | The six floating majors the dollar cycle is fitted to at the opening: the euro, the yen, the pound, the franc, and the Canadian and Australian dollars. |
| 90 | `WorldMoneys.EURO` | `new Kind("euro",.020, 1,.03)` | The euro: own swing 2.0% a month, the cycle in full, +3% in a risk-off (the study's table 3.7, its own swing solved given the cycle [R6]). |
| 93 | `WorldMoneys.YEN` | `new Kind("yen",.023, 1, -.08)` | The yen: 2.3%, the cycle in full, -8% in a risk-off (it rises) [R6]. |
| 96 | `WorldMoneys.POUND` | `new Kind("pound",.019, 1,.06)` | The pound: 1.9%, the cycle in full, +6% [R6]. |
| 99 | `WorldMoneys.FRANC` | `new Kind("franc",.021, 1, -.05)` | The Swiss franc: 2.1%, the cycle in full, -5% (it rises) [R6]. |
| 102 | `WorldMoneys.CANADIAN` | `new Kind("Canadian dollar",.021,.6,.07)` | The Canadian dollar: 2.1%, 0.6 of the cycle (tied to the US), +7% [R6]. |
| 105 | `WorldMoneys.AUSTRALIAN` | `new Kind("Australian dollar",.028, 1,.14)` | The Australian dollar: 2.8%, the cycle in full, +14% [R6]. |
| 108 | `WorldMoneys.KIWI` | `new Kind("New Zealand dollar",.027, 1,.14)` | The New Zealand dollar: 2.7%, the cycle in full, +14% (est.: the Australian dollar's loading). |
| 111 | `WorldMoneys.YUAN` | `new Kind("yuan",.010,.3, 0)` | The yuan: 1.0%, 0.3 of the cycle, no risk-off (est.: a managed money). |
| 114 | `WorldMoneys.ADVANCED_FLOAT` | `new Kind("advanced float",.022, 1,.06)` | Another advanced float, one whose anchor nation is high income when the rule begins: 2.2%, the cycle in full, +6% (est.: the euro's and the pound's, between them). |
| 117 | `WorldMoneys.EMERGING_FLOAT` | `new Kind("emerging float",.040, 1,.20)` | An emerging float, any other money: 4.0%, the cycle in full, +20% [R6]. |
| 120 | `WorldMoneys.DOLLAR_KIND` | `new Kind("US dollar", 0, 0, 0)` | The US dollar: it moves against nothing, its rate is 1 always. |
| 122 | `WorldMoneys.NAMED` | `Map.of("USD", DOLLAR_KIND, "EUR", EURO, "JPY", YEN, "GBP", POUND, "CHF", FRAN...` |  |
| 126 | `WorldMoneys.PHI_CYCLE` | `StrictMath.pow(.5, 1 / CYCLE_HALF_LIFE), PHI_RISK_OFF = StrictMath.pow(.5, 1 ...` | The monthly persistence of each AR(1): 0.5 to the 1 over its half-life. |
| 130 | `WorldMoneys.SAVE_FORMAT` | `1` | The current format of toSaveArray. |
| 133 | `WorldMoneys.WORLD` | `9_999, MONEY_BASE = 10_000` | The draws' who and what: a world's draw sits at WORLD, a money's at MONEY_BASE plus its number in the file, and no what is a Nations draw's (1-8). |
| 134 | `WorldMoneys.CYCLE_DRAW` | `100, RISK_START_DRAW = 101, RISK_SIZE_DRAW = 102, OWN_DRAW = 103, BREAK_DRAW ...` |  |
| 722 | `WorldMoneys.HEADER` | `4, GLOBALS = 5, PER_MONEY = 11` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 140 | `private final Nations nations` |  |
| 141 | `private final long seed` |  |
| 142 | `private final boolean historical` |  |
| 143 | `private final int ruleStart, ruleYear, nationCount, m, dollar, usa` |  |
| 145 | `private final String[] code, kindName` | The moneys, sorted by code; each one's anchor nation (1-based, whose price level is its price level), who it draws as, its kind, the money it is pegged to (-1: a float). |
| 146 | `private final Map<String, Integer> index` |  |
| 147 | `private final int[] anchor, target` |  |
| 148 | `private final long[] who` |  |
| 149 | `private final double[] sigma, cycleLoad, riskLoad, constant` |  |
| 150 | `private final boolean[] board, live` |  |
| 152 | `private final double[][] fileRate` | The file's monthly rates, per money, for the file's months; NaN where it has none. |
| 154 | `private final double[] share` | The nations' commodity share of exports, index nation - 1. |
| 155 | `private final double[] startRate` |  |
| 158 | `private int month` |  |
| 159 | `private boolean opened` |  |
| 160 | `private double cycle, riskOff` |  |
| 161 | `private boolean riskStarted` |  |
| 162 | `private final double[] rate, lnRate, lnShadow, swing, tot, pending, pegRatio, gap` |  |
| 163 | `private final boolean[] held, present, broke` |  |
| 164 | `private final double[] lastTot` |  |
| 165 | `private final boolean[] hasLast` |  |
| 168 | `private final int[] mark` |  |
| 169 | `private int cacheYear` |  |
| 170 | `private double[] lnA0, lnA1` |  |
| 171 | `private double lnUs0, lnUs1` |  |
| 172 | `private boolean quiet` |  |
| 173 | `private double forced` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 766 | **type** `public final class WorldMoneys` | The world's moneys against the US dollar, a month at a time (WN4; spec-exchange-rates 3.7, spec-nations 5.1): each money's rate is its purchasing-power path, plus its loading on the dollar cycle, on the risk-off and o... |

### THE DIALS - each with its source: [R..] is spec-exchange-rates' list, (est.) the game's estimate (lines 35-83)

### the kinds: what each money loads on (lines 84-135)

| line | len | member | says |
|---:|---:|---|---|
| 87 | 1 | **type** `public record Kind(String name, double sigma, double cycle, double riskOff)` | A kind of money: its own swing's monthly innovation sd, its loading on the dollar cycle (b), and on the risk-off (r, negative for a money that rises in a panic). |

### THE MONEYS - made once from the file and the nations (lines 136-156)

### the state: saved (lines 157-166)

### worked, not saved (lines 167-272)

| line | len | member | says |
|---:|---:|---|---|
| 180 | 78 | `public WorldMoneys(WorldData file, Nations nations, double[] commodityShare)` | The moneys of a world on a path and seed, standing in January 2000 (month() is 0): the file's rates of that month, and on Random the state opened from their gaps from parity. |
| 260 | 7 | `private Kind kindOf(int k)` | The kind of money k: by code for the named ones, else an advanced float if its anchor is a high-income nation when the rule begins, else an emerging float. |
| 269 | 3 | `public static int monthIndex(int gameMonth)` | The month index of a game month: months since January 2000, the file's month 0 (game month 1 is January 2000). |

### THE FILE'S FIGURES AND THE PARITY PATH (lines 273-371)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 12 | `private double figure(int k, int t)` | A money's rate in a month the file has a figure for, else the anchor's yearly rate if the anchor was on that money that year, else 1 for the dollar; NaN where there is none (the caller holds the last one). |
| 295 | 17 | `private void replay(int t)` | A month of the file, replayed: each money takes its figure, or holds the last one it had; one that never had any is not present. |
| 318 | 20 | `private double parityConstant(int k, WorldData file)` | The constant that anchors a money's parity on its real rate over the file's years: the mean of ln(file rate) - ln(cpi of the anchor over the US's) over the years 2000-2025 the anchor was on the money and the file has ... |
| 340 | 15 | `private void prepare(int year)` | The anchor's price level and the US's, January of this year and of the next, in logs: the parity path's two ends. |
| 356 | 3 | `private double logPrice(int nation, int year)` |  |
| 361 | 5 | `private double relativePrice(int k, int t)` | ln(P_a / P_US) of money k's anchor in month t, log-linear between Januaries: ln P(t) = ln P(y) + (t mod 12)/12 (ln P(y+1) - ln P(y)). |
| 368 | 3 | `private double parity(int k, int t)` | ln of money k's parity rate in month t: its constant and the anchor's prices against the US's. |

### THE OPENING - the state from the gaps from parity (lines 372-399)

| line | len | member | says |
|---:|---:|---|---|
| 380 | 19 | `private void open(int t)` | Opens the state at month t (the month before the rule's first): C the least-squares fit of gap_k = b_k C over the floating majors, each money's own swing what is left of its gap, the risk-off and the terms of trade 0,... |

### THE MONTH (lines 400-535)

| line | len | member | says |
|---:|---:|---|---|
| 405 | 1 | `public int month()` | The month now, months since January 2000: 0 after the constructor. |
| 408 | 1 | `public int ruleStart()` | The first month the rule rules: 1 on Random, 312 on Historical. |
| 411 | 1 | `public boolean ruling()` | Whether the rule rules this month (Historical replays the file before it). |
| 417 | 4 | `public void recordTermsOfTrade(double[] lnTermsOfTrade)` | Records the terms of trade of a January the constructor could not be given (the founding's: the city begins in January 2000), as last year's for the next January's change; steps nothing. |
| 427 | 15 | `public void step(int t, double[] lnTermsOfTrade)` | Steps to month t, which has to be the next. |
| 443 | 4 | `private void checkTermsOfTrade(double[] ln)` |  |
| 449 | 10 | `private double[] deltas(double[] ln)` | Each nation's change in ln terms of trade on the last January's (0 for one with no last), the last now this one's; a figure that is no number is no news. |
| 461 | 38 | `private void rule(int t, double[] lnTermsOfTrade)` | A month of the rule (the study's 3.7), in its order: the cycle, the risk-off, the terms of trade, the swings, then the rates and the pegs' breaks. |
| 506 | 29 | `private double resolve(int k, int t)` | Money k's rate this month: a float's is its shadow; a held peg's is its ratio over the rate of the money it is pegged to (worked first: pegs chain, and a loop holds last month's), and its gap x from the shadow (positi... |

### THE DRAWS - pure functions of (seed, who, month, what) (lines 536-543)

| line | len | member | says |
|---:|---:|---|---|
| 540 | 1 | `private double gauss(long who, int t, int what)` |  |
| 542 | 1 | `private double unit(long who, int t, int what)` |  |

### WHAT A CALLER READS - a code the model does not carry reads NaN, null or false (lines 544-681)

| line | len | member | says |
|---:|---:|---|---|
| 549 | 1 | `public List<String> codes()` | The moneys it carries, sorted by code (a few may have no rate yet, or at all: NaN). |
| 551 | 4 | `private int at(String c)` |  |
| 557 | 4 | `public double rate(String c)` | A money's units to the US dollar this month: 1 for the dollar, the file's figure on Historical's replay, NaN for a money it does not carry or that has no rate. |
| 563 | 1 | `public double cross(String a, String b)` | Units of b for one a, through the dollar: rate(b) / rate(a). |
| 566 | 3 | `public double rateOfNation(int nation)` | The rate of a nation's money this year (the year of the month now; the money Nations says it is on). |
| 571 | 4 | `public double rateAtStart(String c)` | A money's rate in January 2000, which both paths begin from. |
| 577 | 1 | `public double cycle()` | The dollar cycle C, a log: positive, the dollar is strong. |
| 580 | 1 | `public double riskOff()` | The risk-off R this month: 0 in calm, the size of the last one fading by half in RISK_OFF_HALF_LIFE months. |
| 583 | 1 | `public boolean riskOffStarted()` | Whether a risk-off started this month. |
| 586 | 5 | `public List<String> breaksThisMonth()` | The moneys whose pegs broke this month. |
| 593 | 4 | `public boolean ruled(String c)` | Whether a money is ruled: carried, with a rate, and one the nations still use once the rule runs (Historical's old moneys, the lira and the drachma, keep their last rate). |
| 599 | 4 | `public boolean pegHeld(String c)` | Whether a money's peg holds now (false for a float, for a peg that has broken, and on Historical before the rule runs, when the file's rates are the rates). |
| 605 | 4 | `public String pegAnchor(String c)` | The money its regime pegs or ties it to (it stays when the peg breaks); null for a float. |
| 611 | 4 | `public boolean isBoard(String c)` | Whether it is a currency board, which never breaks. |
| 617 | 4 | `public double pegGap(String c)` | The gap x the peg faced this month, a log (positive: the shadow weaker than the peg); 0 for a money that faced no check. |
| 623 | 4 | `public double shadow(String c)` | Where the rule has the money, units a dollar: its rate if it floats; beside a held peg, the pressure on it. |
| 629 | 4 | `public double ppp(String c)` | The money's parity rate this month, units a dollar: its constant and its anchor's prices against the US's. |
| 635 | 4 | `public double swing(String c)` | The money's own swing S, a log. |
| 641 | 4 | `public double totReached(String c)` | The terms of trade reached T, a log: negative, the money dearer. |
| 647 | 4 | `public double cycleLoading(String c)` | The money's loading on the dollar cycle, b. |
| 653 | 4 | `public double riskOffLoading(String c)` | The money's loading on the risk-off, r (negative: it rises in a panic). |
| 659 | 4 | `public double sigma(String c)` | The monthly sd of the money's own swing's innovation (0 while the test hook has it quiet). |
| 665 | 4 | `public String kind(String c)` | The money's kind, by name ("euro", "advanced float", "emerging float"...). |
| 671 | 4 | `public int anchorOf(String c)` | The nation whose price level is the money's, 1-based; 0 for a money it does not carry. |
| 677 | 4 | `public double commodityShare(int nation)` | A nation's commodity share of exports, as it was given. |

### THE NATIONS' PRICE LEVELS BY THE MONTH (WN4, D29) (lines 682-717)

| line | len | member | says |
|---:|---:|---|---|
| 693 | 1 | `public int usNation()` | The nation the US dollar is the money of, the frontier (Nations.FRONTIER), 1-based: the nation whose path the game's US$ follows. |
| 696 | 5 | `public double priceLevel(int nation, int t)` | A nation's price level in month t (months since January 2000) against its level in month 0, log-linear between Januaries: 1 in month 0. |
| 703 | 1 | `public double usPriceLevel(int t)` | The US's price level in month t against its level in month 0: the in-game US$'s, whose prices follow the US's path (WorldEconomy). |
| 706 | 4 | `public double usPace(int t)` | The US's inflation in the calendar year of month t: its price level in January of the next year over January of this one, less one. |
| 712 | 5 | `private double lnPriceAt(int nation, int t)` | ln of a nation's price level in month t, in the file's index (2015 = 100): ln P(y) + (t mod 12)/12 (ln P(y+1) - ln P(y)). |

### THE SAVE - the state, not what the file and the path give (lines 718-779)

| line | len | member | says |
|---:|---:|---|---|
| 729 | 23 | `public double[] toSaveArray()` | The state as numbers: a header (the format, the money count, the nation count and a fingerprint of the money codes, so a restore against another file is refused), the month, the opening, C, R and whether a risk-off st... |
| 754 | 25 | `public void restore(double[] saved)` | Puts the state back from toSaveArray; throws IllegalArgumentException, and changes nothing, for one made on another file (another count of moneys or nations, other codes) or another format. |

### FOR THE CHECK - what a harness does that the game does not (lines 780-798)

| line | len | member | says |
|---:|---:|---|---|
| 788 | 7 | `void quiet()` | Isolates one term for a section: from now every sigma, the risk-off's chance and the pegs' hazard read 0, and the cycle, the risk-off, every swing and the terms of trade reached are cleared, so the rate of a float is ... |
| 797 | 1 | `void forceRiskOff(double size)` | A risk-off of this size starts at the next step of the rule, whatever the draw says. |

