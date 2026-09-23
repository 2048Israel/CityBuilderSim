# CurrencyCheck.java - 743 lines · 19 methods · 5 constants · harnesses

`ham/citybuildersim/CurrencyCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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
>   1. THE REAL RATE MOVES THE CURRENCY, NOT THE INFLATION DIFFERENTIAL. Past
>      SETTLING_MONTHS, trade in balance (no pressure), the vault empty
>      (nothing damped), the city inflating at 20% and the world at 2%: a
>      dial that pays a real rate over the world's strengthens the rate by
>      exactly the real gap x RATE_PULL x DRIFT_SPEED (times the openness,
>      here one) plus the parity pull; a dial under it weakens it by exactly
>      the mirror; and a dial at the world's real rate plus the city's
>      inflation moves it by the parity pull alone.
>   2. A SPIRAL NEEDS AN OUTFLOW: with the real gap at nothing and no
>      deficit, ten months of 40% inflation move the rate by the parity pull
>      and nothing else.
>   3. THE DEFENCE SPENDS. A vault of US$10M at six months' cover against a
>      month with a US$2M deficit and a push to weaken sells exactly
>      absorption() x US$2M; the vault falls by it; the push that reaches the
>      rate is damped by what was sold; cover falls. An empty vault sells and
>      damps nothing. A surplus month with the currency rising sells nothing
>      and buys nothing. A vault of US$500k against the same deficit sells
>      all of it and damps by 500k / 2M, not by absorption().
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
>      rate past the old 25% stop; and the hot money's appetite at twenty
>      points is larger than at six and stops at MAX_SPREAD.
>   6. AND SO IS THE CURRENCY'S (0.7.3). Jerus, on the guard of 100: "Better
>      to have it exceed otherwise one can just ignore once at 100." A
>      month's push that would take the rate past 100 takes it there - to
>      150 after the month, where the old guard held it at 100 - and a rate
>      under the old guard crosses it; the dollar debt's revaluation follows
>      the rate that far. The same the other way past .01. And a reform still
>      scales the guards, now a billion either way, with the unit - and the
> ... (3 more lines in the source)

**Uses:** [ForeignAccounts](ForeignAccounts.md) (48), [ForeignCheck](ForeignCheck.md) (9), [DebtManager](DebtManager.md) (8), [Game](Game.md) (8), [CentralBank](CentralBank.md) (6), [CapitalFlows](CapitalFlows.md) (5), [MoneyAudit](MoneyAudit.md) (3), [GameFiles](GameFiles.md) (3), [PriceIndex](PriceIndex.md) (3)

## Sections

| line | section |
|---:|---|
| 147 | 1. the real rate, not the inflation differential |
| 197 | 2. a spiral needs an outflow |
| 229 | 3. the defence spends |
| 340 | 4. the dollars it sells are capital, not money destroyed |
| 562 | 5. the stops are gone |
| 627 | 6. the currency's guards are a billion either way |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 97 | `CurrencyCheck.OLD_DIAL_STOP` | `.25` | The dial's stop until 0.7.2, for the assertion that it is gone. |
| 100 | `CurrencyCheck.OLD_SPREAD_STOP` | `.06` | The hot money's old stop, likewise: six points. |
| 103 | `CurrencyCheck.OLD_MAX_RATE` | `100` | The currency's guard above until 0.7.3, a hundred local dollars to one of theirs - for the assertions that a rate goes past it. |
| 106 | `CurrencyCheck.OLD_MIN_RATE` | `.01` | ...and below, a hundredth. |
| 523 | `CurrencyCheck.Before.broken` | `new int [ 5 ]` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 78 | `static PrintStream out` |  |
| 79 | `static PrintStream quiet` |  |
| 524 | `final double m0, equity, notVault, remittance, loss` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 669 | **type** `public class CurrencyCheck` | Proves the currency under a central bank (0.7.2): the rate answers to the real rate, the vault is spent defending it, and the dial and the carry appetite have lost their stops. |
| 81 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 86 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 109 | 8 | `static MoneyAudit.Result month(double exports, double imports, double capitalIn, double capitalOut)` | A synthetic month: the trade and the financial account are all takeMonth() reads. |
| 119 | 7 | `static ForeignAccounts settled(double exports, double imports, double gdp)` | A currency past its settling months on the same month, over and over. |
| 128 | 3 | `static double pulled(double rate, double parity)` | Where the parity pull alone would take the rate from here. |
| 132 | 14 | `public static void main(String[] args) throws Exception` |  |

### 1. the real rate, not the inflation differential (lines 147-196)

| line | len | member | says |
|---:|---:|---|---|
| 148 | 48 | `static void realRate()` |  |

### 2. a spiral needs an outflow (lines 197-228)

| line | len | member | says |
|---:|---:|---|---|
| 198 | 30 | `static void spiral()` |  |

### 3. the defence spends (lines 229-339)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 8 | `static ForeignAccounts defended(double exports, double imports, double gdp, double vaultUsd, double capitalOut)` | A currency past settling on a trailing deficit, a vault of this many dollars bought at 1.00, and then the month under test: the same trade and this much capital leaving. |
| 246 | 3 | `static double repriced(double r0, double total, double met, double openness, double parity)` | What the reprice should leave the rate at, on this push and this much of it met. |
| 250 | 89 | `static void defence()` |  |

### 4. the dollars it sells are capital, not money destroyed (lines 340-561)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 175 | `static void capital() throws Exception` |  |
| 522 | 39 | **type** `static final class Before` | The central bank and the vault as a month opened, and the five claims section 4 holds each month against them. |
| 526 | 7 | `Before(CentralBank cb)` _(in CurrencyCheck.Before)_ |  |
| 534 | 26 | `void monthHeld(Game city, String named)` _(in CurrencyCheck.Before)_ |  |

### 5. the stops are gone (lines 562-626)

| line | len | member | says |
|---:|---:|---|---|
| 563 | 63 | `static void uncapped() throws Exception` |  |

### 6. the currency's guards are a billion either way (lines 627-743)

| line | len | member | says |
|---:|---:|---|---|
| 630 | 7 | `static ForeignAccounts at(double rate)` | A settled currency, trade in balance and the vault empty, put at this rate the way a save would put it. |
| 639 | 3 | `static void month(ForeignAccounts fx, double parityLevel, double realGap)` | One month of it: the same balanced trade, the city's price level at this (so parity at it, in founding money), and this real gap, repriced. |
| 644 | 6 | `static void month(ForeignAccounts fx, double parityLevel, double realGap, double unit)` | ...in a unit this many of the founding's: the trade and the output are money, and a reform divides them. |
| 651 | 92 | `static void unguarded()` |  |

