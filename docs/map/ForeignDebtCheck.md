# ForeignDebtCheck.java - 670 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/ForeignDebtCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> Borrowing in somebody else's money.
> 
> ORIGINAL SIN, which is the name the literature gives the thing this file is
> here to prove works: a city that cannot borrow abroad in its own currency owes
> dollars and earns local money, so a devaluation makes the debt dearer without
> anybody having borrowed another cent. Domestic debt does the opposite -
> inflation and devaluation quietly shrink it.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the instrument speak two currencies HONESTLY? The contract is in
>      dollars and does not move; the city's books are in local money and must.
>      Both, at once, with neither leaking into the other.
> 
>   2. Do the books still balance? Foreign paper is the first city borrowing
>      that genuinely crosses the city's edge, so MoneyAudit has three new lines
>      and one deliberate absence - the revaluation, which is not a cash flow
>      and must not appear as one.
> 
>   3. Is the circular-capital hole actually closed? The whole point of foreign
>      paper is that it does not reach the bank's book. If it does, the city is
>      still borrowing from the institution it is recapitalising and none of
>      this was worth building.
> 
>   4. Does the window shut when it should, does a default cost what it is
>      supposed to cost, and does any of it survive a reload?
> 
>   5. Is the world's paper on the world's curve (0.7.2)? A dollar bond is
>      worth what the world would pay for it - the foreign rate plus the
>      term premium for the months it has left - not what the city's own
>      dial says; the dial moving leaves it where it is, the world's view of
>      the city moving does not; and a dollar issue is priced on the same
>      curve it is then valued on.

**Uses:** [DebtManager](DebtManager.md) (17), [Game](Game.md) (12), [Debt](Debt.md) (4), [GameFiles](GameFiles.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [DebtQuote](DebtQuote.md) (2), [MoneyAudit](MoneyAudit.md) (1)

**Used by (1):** [RestructureCheck](RestructureCheck.md)

## Sections

| line | section |
|---:|---|
| 92 | · 1. the instrument speaks two currencies |
| 140 | · 2. and the world is cheaper, to begin with |
| 220 | · 3. the books balance with dollars on them |
| 283 | · 4. original sin |
| 347 | · 4b. and where the dollars actually went |
| 421 | · 5. the window shuts |
| 445 | · 6. and the price of walking away |
| 496 | · 7. across a reload |
| 608 | 8. the world's paper on the world's curve (0.7.2) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `static int fails` |  |
| 46 | `static PrintStream out` |  |
| 47 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 43 | 628 | **type** `public class ForeignDebtCheck` | Borrowing in somebody else's money. |
| 49 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 54 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 64 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 72 | 14 | `static Game tradingCity(Path dir) throws Exception` | A small city that has been going long enough to have a credit record. |
| 87 | 520 | `public static void main(String[] args) throws Exception` |  |

### 8. the world's paper on the world's curve (0.7.2) (lines 608-670)

| line | len | member | says |
|---:|---:|---|---|
| 609 | 61 | `static void theWorldsCurve(Path dir) throws Exception` |  |

