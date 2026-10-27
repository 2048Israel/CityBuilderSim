# CurrencyCheck.java - 939 lines · 22 methods · 6 constants · harnesses

`ham/citybuildersim/CurrencyCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> Proves the currency under a central bank (0.7.2): the rate answers to the
> real rate, the vault is spent defending it, and the dial and the carry
> appetite have lost their stops. Not part of the game.
> 
> WHY THIS EXISTS. Jerus: "i think we need to uncap the rate... but if we
> do... what happens to everyone?" Until 0.7.2 repriceCurrency() moved the
> rate by the whole inflation differential every month, unbounded, while the
> support a high rate gave it saturated at 13 points over the world (the
> old MAX_RATE_PRESSURE .8 over the old ForeignAccounts.RATE_PULL 6), the
> hot money's appetite at six (CapitalFlows.MAX_SPREAD) and the dial at 25%
> (DebtManager.MAX_POLICY_RATE). So a city whose prices rose 40% a year saw
> its currency fall 40% a year by rule, whatever its central bank did - the
> 0.6.9 year book, a-reserve-defends-a-currency.md section 1 - and the
> vault's absorption cost nothing: no dollar ever left it. The design is
> the-central-bank.md section 8; the batch is its D.
> 
> What it has to prove, a section each, every one a fixture that causes the
> condition rather than waiting for it:
> 
>   1. THE REAL RATE HOLDS THE CURRENCY AT A LEVEL, AND THE INFLATION
>      DIFFERENTIAL REACHES IT ONLY THROUGH PARITY'S TREND (restated for XR1,
>      docs/studies/spec-exchange-rates.md 3.2-3.3: the 0.7.2 premise that
>      inflation moves no rate by rule is retired, parity's trend is followed
>      one for one). A quiet-market currency, trade in balance, the vault
>      empty (so the cover's premium is RISK_PREMIUM), parity held at 1.2
>      with the city at 20% and the world at 2%: after 120 months under each
>      of three dials the rate is the trend and the real gap's, the balance's
>      and the cover's levels reached, to the model's own getters; a dial that
>      pays over the world's real rate holds it under the neutral dial's, one
>      that pays under it over it, and the neutral dial's is parity's trend
>      times the cover's premium alone.
>   2. INFLATION MOVES THE RATE WITH PARITY'S TREND AND NO FURTHER: with the
>      real gap at nothing, no deficit, the vault at THIN_COVER or more and
>      the market quiet, ten months of 40% inflation put each month's rate on
>      getParityTrend(), and the rate ends above where it began.
>   3. THE DEFENCE SPENDS. A vault of US$10M at six months' cover against a
>      month with a US$2M deficit and a month the D$ would fall by more than
>      the deficit's cap sells exactly absorption() x US$2M; the vault falls
>      by it; the sale lifts the D$ by INTERVENTION_IMPACT x what it fetched
>      over a year of GDP (the intervention's move, which replaces the old
>      damping of a push); cover falls. An empty vault sells nothing and
>      lifts nothing. A surplus month with the currency rising sells nothing
>      and buys nothing. A vault of US$500k against the same deficit sells
>      all of it and meets 500k / 2M of it, not absorption(). New in XR1
>      (the lean smooths): a small fall sells only what takes back
>      absorption() of it.
>   4. WHAT IT FETCHES IS THE CENTRAL BANK'S CAPITAL, NOT MONEY DESTROYED.
>      On a bare balance sheet: the sale leaves M0, issued and retired
>      exactly where they were, takes equity down by exactly what the dollars
>      fetched at the rate they were sold at (the vault's fall), and leaves
>      the remittance, the loss carried and the month's profit alone. In a
>      real city, the month it is defended and every month of ten years
>      after: M0 moves by the money made and nothing else, nothing is retired
>      but what the named operations take back, the audit closes and
>      declares nothing for the defence, the remittance is the month's
>      profit, and equity less the vault is what the bank owes; a save keeps
>      the sale and the equity line. And every month Game hands the currency
>      the real rate differential, struck on the same two inflations parity
>      is.
>   5. THE STOPS ARE GONE: the dial takes 60%; the autopilot sets a rule's
> ... (12 more lines in the source)

**Uses:** [ForeignAccounts](ForeignAccounts.md) (52), [ForeignCheck](ForeignCheck.md) (9), [DebtManager](DebtManager.md) (8), [Game](Game.md) (8), [CentralBank](CentralBank.md) (6), [CapitalFlows](CapitalFlows.md) (5), [MoneyAudit](MoneyAudit.md) (3), [GameFiles](GameFiles.md) (3), [PriceIndex](PriceIndex.md) (3), [TempDirs](TempDirs.md) (2), [Founding](Founding.md) (1)

**Used by (4):** [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ForeignCheck](ForeignCheck.md)

## Sections

| line | section |
|---:|---|
| 158 | 1. the real rate holds a level; inflation arrives through parity's trend |
| 232 | 2. inflation moves the rate with parity's trend, and no further |
| 274 | 3. the defence spends |
| 495 | 4. the dollars it sells are capital, not money destroyed |
| 738 | 5. the stops are gone |
| 812 | 6. the currency's guards are a billion either way |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 105 | `CurrencyCheck.OLD_DIAL_STOP` | `.25` | The dial's stop until 0.7.2, for the assertion that it is gone. |
| 108 | `CurrencyCheck.OLD_SPREAD_STOP` | `.06` | The hot money's old stop, likewise: six points. |
| 111 | `CurrencyCheck.OLD_MAX_RATE` | `100` | The currency's guard above until 0.7.3, a hundred local dollars to one of theirs - for the assertions that a rate goes past it. |
| 114 | `CurrencyCheck.OLD_MIN_RATE` | `.01` | ...and below, a hundredth. |
| 127 | `CurrencyCheck.TRADING_MONTHS` | `24 + 12` | The months a fixture trades before its test month: 24, the currency's settling months until XR1, and a year more for the trailing figures to settle. |
| 698 | `CurrencyCheck.Before.broken` | `new int [ 5 ]` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `static int fails` |  |
| 86 | `static PrintStream out` |  |
| 87 | `static PrintStream quiet` |  |
| 699 | `final double m0, equity, notVault, remittance, loss` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 857 | **type** `public class CurrencyCheck` | Proves the currency under a central bank (0.7.2): the rate answers to the real rate, the vault is spent defending it, and the dial and the carry appetite have lost their stops. |
| 89 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 94 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 117 | 8 | `static MoneyAudit.Result month(double exports, double imports, double capitalIn, double capitalOut)` | A synthetic month: the trade and the financial account are all takeMonth() reads. |
| 130 | 7 | `static ForeignAccounts settled(double exports, double imports, double gdp)` | A currency that has traded the same month, over and over, for TRADING_MONTHS: its market quiet (a bare ForeignAccounts is unseeded) and not yet repriced. |
| 139 | 3 | `static double lift(ForeignAccounts fx)` | What a sale of what the defence fetched lifts the D$ by, in logs (ForeignAccounts, THE FLOAT, step 7): INTERVENTION_IMPACT x the local money over a year of the GDP the model weighs it against. |
| 143 | 14 | `public static void main(String[] args) throws Exception` |  |

### 1. the real rate holds a level; inflation arrives through parity's trend (lines 158-231)

| line | len | member | says |
|---:|---:|---|---|
| 159 | 72 | `static void realRate()` |  |

### 2. inflation moves the rate with parity's trend, and no further (lines 232-273)

| line | len | member | says |
|---:|---:|---|---|
| 233 | 40 | `static void spiral()` |  |

### 3. the defence spends (lines 274-494)

| line | len | member | says |
|---:|---:|---|---|
| 281 | 4 | `static ForeignAccounts defended(double exports, double imports, double gdp, double vaultUsd, double capitalOut)` | A currency past settling on a trailing deficit, a vault of this many dollars bought at 1.00, and then the month under test: the same trade and this much capital leaving. |
| 287 | 14 | `static ForeignAccounts defended(double exports, double imports, double gdp, double vaultUsd, double capitalOut, double settling...` | ...and, when `settlingOut` is more than nothing, that much capital leaving every month of the settling too: a chronic deficit, so the trailing balance is the month's own. |
| 303 | 3 | `static double need(double absorption, double fall, double gdpTrailing, double rate)` | The dollars it takes to take back `absorption` of a fall of `fall` (logs) at INTERVENTION_IMPACT, on a GDP and a rate (ForeignAccounts, THE FLOAT, step 7): the need. |
| 308 | 4 | `static double expectedSale(double absorption, double fall, double gdpTrailing, double rate, double deficitUsd, double vaultUsd)` | What the lean sells in a month the D$ would fall by `fall`: the need, at most absorption() x the month's deficit, at most the vault; nothing on a rise or with no deficit. |
| 320 | 7 | `static double firstFall(ForeignAccounts fx, double gap)` | The fall the FIRST reprice of a fixture from defended() makes, from its figures alone (the rate at 1.00, nothing repriced before, the market quiet, parity at 1): the gated gap, balance and cover levels reached in one ... |
| 328 | 166 | `static void defence()` |  |

### 4. the dollars it sells are capital, not money destroyed (lines 495-737)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 195 | `static void capital() throws Exception` |  |
| 697 | 40 | **type** `static final class Before` | The central bank and the vault as a month opened, and the five claims section 4 holds each month against them. |
| 701 | 7 | `Before(CentralBank cb)` _(in CurrencyCheck.Before)_ |  |
| 709 | 27 | `void monthHeld(Game city, String named)` _(in CurrencyCheck.Before)_ |  |

### 5. the stops are gone (lines 738-811)

| line | len | member | says |
|---:|---:|---|---|
| 739 | 72 | `static void uncapped() throws Exception` |  |

### 6. the currency's guards are a billion either way (lines 812-939)

| line | len | member | says |
|---:|---:|---|---|
| 815 | 7 | `static ForeignAccounts at(double rate)` | A settled currency, trade in balance and the vault empty, put at this rate the way a save would put it. |
| 824 | 3 | `static void month(ForeignAccounts fx, double parityLevel, double realGap)` | One month of it: the same balanced trade, the city's price level at this (so parity at it, in founding money), and this real gap, repriced. |
| 829 | 6 | `static void month(ForeignAccounts fx, double parityLevel, double realGap, double unit)` | ...in a unit this many of the founding's: the trade and the output are money, and a reform divides them. |
| 836 | 103 | `static void unguarded()` |  |

