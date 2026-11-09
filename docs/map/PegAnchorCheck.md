# PegAnchorCheck.java - 583 lines · 21 methods · 5 constants · harnesses

`ham/citybuildersim/PegAnchorCheck.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The peg's anchor (batch WN4b; ForeignAccounts, THE REGIMES, THE PEG'S ANCHOR): the D$ can be pegged to any of the eight moneys, not
> only the US$. The peg is kept in the anchor's money (D$ per unit of it); the rate the hold keeps, D$ per US$, is the peg over the anchor's
> US$ price that month, so it moves with the anchor against the US$; the shadow's gap, the hazard and the lift are measured against that
> held rate; and under a peg the dial ties to the anchor's central bank rate (DebtManager.tieRate(), as Denmark's follows the ECB's).
> 
> WHY. Every peg there was is a peg to the US$, and every harness compares getRate() with getPegRate(). Giving the peg a second money could
> silently change the US$ path (a division by an anchor price that is "1", an anchor field the old saves do not carry), could measure the
> gap against the wrong rate (the peg in euros read as D$ per US$), could lose the anchor in the save or let a money with no rate anchor a
> peg, or leave the dial tied to the US's rate under a euro peg. Each section says against which expression or constant it is judged.
> 
> What this has to prove:
>   1. A US$ ANCHOR IS THE PEG AS IT WAS: a new ForeignAccounts's anchor is the US$, and two seeded US$ pegs on a deficit trade and a deep
>      vault, one handed the eight's moving rates every month and one never handed any, have the same rate, sale, support, gap and edge
>      purchase to the bit every month: the eight's moves do not reach a US$ peg; getPegRate() is the peg itself.
>   2. THE HELD RATE IS THE PEG OVER THE ANCHOR'S US$ PRICE: pegged to the euro, with the euro moving for two years on balanced trade, the
>      rate each month is the peg per euro over the euro's price, the peg per euro never moves, the D$ per euro is the peg every month, the
>      D$ per US$ took many values, and the hold dealt (the euro's swing was what it held).
>   3. THE SHADOW'S GAP IS AGAINST THE HELD RATE: two quiet twins with equal held rates deal the same; when the euro rises 10% the euro
>      twin's gap is the US$ twin's plus ln(1.1) on the same shadow, currencyGap() is the shadow over the held rate, and its hold sells
>      more; the support is the log of the held rate over the shadow.
>   4. CHOOSING THE ANCHOR: the held rate does not move on the switch, the peg per euro is the day's cross, and the rate follows the
>      euro from the next month; a money with no rate or none of the eight is refused; outside a peg the setting is kept and the next peg
>      is struck in it; the regime's words name the anchor.
>   5. A DEVALUATION IN THE ANCHOR'S MONEY: repeg() is D$ per euro; the held rate is it over the euro's price, and the rate follows it.
>   6. THE BREAK UNDER A EURO PEG: a euro peg with no vault, revalued, breaks for want of the vault, lands where the shadow plus the
>      overshoot of the gap against the HELD rate puts it, and keeps its anchor.
>   7. THE DIAL TIES TO THE ANCHOR'S RATE (a bare DebtManager): tied to the euro the dial is the euro's rate plus the spread, tied to the
>      US$ it is what it was to the bit, untied it reads the US's; the spread is bounded over the anchor's rate and its decision names it.
>   8. THE SAVE: slot 148 is the anchor; a restored euro peg is the same peg and plays the same next month; a save cut before slot 148
>      reads the US$; an anchor with no saved rate reads the US$.
>   9. IN A CITY (the full file only): a trading town pegged to the euro holds the peg per euro over the euro's price, its dial is the
>      euro area's rate plus the spread, and the decision log names the euro.
> 
> Every fixture causes its condition: rates handed by hand before each month (the eight's, with the euro moved where a section needs the
> anchor to move), a deficit trade where the hold has to sell, a vault of none where it cannot, a quiet market (seed 0) where a chance
> break would blur the arithmetic, and a seeded one where the claim is the very absence of a difference.

**Uses:** [ForeignAccounts](ForeignAccounts.md) (42), [DebtManager](DebtManager.md) (24), [CurrencyRegimeCheck](CurrencyRegimeCheck.md) (15), [DecisionLog](DecisionLog.md) (6), [InterestAbroadCheck](InterestAbroadCheck.md) (4), [WorldData](WorldData.md) (3), [Game](Game.md) (2)

## Sections

| line | section |
|---:|---|
| 141 | 1. A US$ ANCHOR IS THE PEG AS IT WAS |
| 189 | 2. THE HELD RATE IS THE PEG OVER THE ANCHOR'S US$ PRICE |
| 222 | 3. THE SHADOW'S GAP IS AGAINST THE HELD RATE |
| 266 | 4. CHOOSING THE ANCHOR |
| 329 | 5. A DEVALUATION IN THE ANCHOR'S MONEY |
| 358 | 6. THE BREAK UNDER A EURO PEG |
| 383 | 7. THE DIAL TIES TO THE ANCHOR'S RATE |
| 455 | 8. THE SAVE |
| 511 | 9. IN A CITY |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `PegAnchorCheck.out` | `System.out` |  |
| 53 | `PegAnchorCheck.LEAN` | `ForeignAccounts.Regime.LEAN, PEG = ForeignAccounts.Regime.PEG, FLOAT = Foreig...` |  |
| 55 | `PegAnchorCheck.TRADE` | `CurrencyRegimeCheck.TRADE, DEEP = CurrencyRegimeCheck.DEEP` |  |
| 58 | `PegAnchorCheck.R` | `{ 1, 1.10, 0.0070, 1.27, 0.14, 0.73, 0.65, 1.12 }` | The cross rates the sections hand: US$ per unit of the US$, euro, yen, pound, yuan, Canadian and Australian dollar and franc (VaultMoneysCheck's). |
| 61 | `PegAnchorCheck.SLOTS` | `149` | The length of the foreign accounts' array with the peg's anchor in slot 148 (WN4b). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 536 | **type** `public class PegAnchorCheck` | The peg's anchor (batch WN4b; ForeignAccounts, THE REGIMES, THE PEG'S ANCHOR): the D$ can be pegged to any of the eight moneys, not only the US$. |
| 63 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 69 | 5 | `static void same(String label, double actual, double expected)` | Equal to the bit (and so a -0.0 is not a 0.0). |
| 76 | 5 | `static void close(String label, double actual, double expected, double tol)` | Equal within a share of the expected. |
| 83 | 5 | `static void closeAbs(String label, double actual, double expected, double tol)` | Equal within an absolute amount (for the logs, which sit near zero). |
| 90 | 1 | `static boolean within(double actual, double expected, double tol)` | Whether a figure is within a share of another: the loops' accumulator. |
| 93 | 5 | `static double[] rates(double euro)` | The eight's rates with the euro at this price. |
| 100 | 6 | `static double[] moving(int m)` | The eight's rates in month m, every one moving (a sine each, out of step with the others). |
| 108 | 1 | `static double euroAt(int m)` | The euro's price in month m: 1.10 swinging 6% either side. |
| 111 | 7 | `static ForeignAccounts euroPeg(double exports, double imports, double vaultUsd, long seed, double euro)` | A settled currency pegged to the euro at today's rate in euros, the euro at this price: rates handed, the anchor chosen, then the peg struck. |
| 120 | 4 | `static void month(ForeignAccounts fx, double euro, double exports, double imports)` | One month with the euro at this price handed first. |
| 125 | 15 | `public static void main(String[] args) throws Exception` |  |

### 1. A US$ ANCHOR IS THE PEG AS IT WAS (lines 141-188)

| line | len | member | says |
|---:|---:|---|---|
| 145 | 43 | `static void aUsdAnchorIsThePegAsItWas()` |  |

### 2. THE HELD RATE IS THE PEG OVER THE ANCHOR'S US$ PRICE (lines 189-221)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 28 | `static void theHeldRateIsThePegOverTheAnchorsPrice()` |  |

### 3. THE SHADOW'S GAP IS AGAINST THE HELD RATE (lines 222-265)

| line | len | member | says |
|---:|---:|---|---|
| 226 | 39 | `static void theShadowsGapIsAgainstTheHeldRate()` |  |

### 4. CHOOSING THE ANCHOR (lines 266-328)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 58 | `static void choosingTheAnchor()` |  |

### 5. A DEVALUATION IN THE ANCHOR'S MONEY (lines 329-357)

| line | len | member | says |
|---:|---:|---|---|
| 333 | 24 | `static void aDevaluationInTheAnchorsMoney()` |  |

### 6. THE BREAK UNDER A EURO PEG (lines 358-382)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 20 | `static void theBreakUnderAEuroPeg()` |  |

### 7. THE DIAL TIES TO THE ANCHOR'S RATE (lines 383-454)

| line | len | member | says |
|---:|---:|---|---|
| 387 | 67 | `static void theDialTiesToTheAnchorsRate()` |  |

### 8. THE SAVE (lines 455-510)

| line | len | member | says |
|---:|---:|---|---|
| 459 | 51 | `static void theSave()` |  |

### 9. IN A CITY (lines 511-583)

| line | len | member | says |
|---:|---:|---|---|
| 516 | 5 | `static List<String> currencyLines(Game g)` | The decision log's currency lines, oldest first. |
| 522 | 61 | `static void inACity()` |  |

