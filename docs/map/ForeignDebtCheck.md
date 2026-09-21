# ForeignDebtCheck.java - 598 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/ForeignDebtCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (11), [DebtManager](DebtManager.md) (11), [GameFiles](GameFiles.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Debt](Debt.md) (2), [ForeignAccounts](ForeignAccounts.md) (2), [MoneyAudit](MoneyAudit.md) (1), [DebtQuote](DebtQuote.md) (1)

## Sections

| line | section |
|---:|---|
| 85 | · 1. the instrument speaks two currencies |
| 133 | · 2. and the world is cheaper, to begin with |
| 213 | · 3. the books balance with dollars on them |
| 276 | · 4. original sin |
| 340 | · 4b. and where the dollars actually went |
| 414 | · 5. the window shuts |
| 438 | · 6. and the price of walking away |
| 489 | · 7. across a reload |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 39 | `static PrintStream out` |  |
| 40 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 563 | **type** `public class ForeignDebtCheck` | Borrowing in somebody else's money. |
| 42 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 47 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 57 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 65 | 14 | `static Game tradingCity(Path dir) throws Exception` | A small city that has been going long enough to have a credit record. |
| 80 | 518 | `public static void main(String[] args) throws Exception` |  |

