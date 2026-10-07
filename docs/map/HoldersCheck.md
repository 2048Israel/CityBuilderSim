# HoldersCheck.java - 590 lines · 10 methods · 0 constants · harnesses

`ham/citybuildersim/HoldersCheck.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> Proves who holds the city's own paper (0.7.1): that the households buy it at
> the settle, are paid on it, sell it back, and are paid when it is bought
> back - every crossing declared, and every holding exactly where the paper
> says it is. Not part of the game.
> 
> WHY THIS EXISTS. Jerus: "yes households should be able to hold." Until
> 0.7.1 the commercial bank held every dollar of the city's paper, so a bond
> was a loan from the city's own bank with extra steps. Now a piece of paper
> carries what the households and the central bank hold of it, and the
> households carry their paper as a fourth asset. Two books that must agree
> to the dollar, and a set of crossings - households are outside the money
> audit's pools - each of which is a dollar from nowhere if it is not
> declared. So each is caused and asserted:
> 
>   1. At the settle the households take the share the dials give them of an
>      issue yielding well above the deposit rate; their savings fall by
>      exactly what they paid, the bank pays exactly the rest, the paper's
>      household share is the cells' paper summed, and the audit closes.
>   2. The next month's coupon reaches them in their share, the bank's in
>      its, and the treasury's books carry the whole of it.
>   3. A household short of money sells its paper before its shares, and its
>      shares only once the paper is gone.
>   4. A household selling after the curve has risen gets less than face,
>      and the bank books the gain against what it carries the paper at.
>   5. A buyback pays every holder its share - the bank, the households, the
>      central bank - and the next month declares what left the pools.
>   6. Save and reload, and every holding is exactly where it was.
>   7. A save from before the holders (no cell slot, no fields on the paper)
>      loads with the bank holding everything, and runs.
>   8. A dollar bond bought back is money leaving the country: none of the
>      price reaches the bank or the households, and the next month declares
>      all of it abroad (until 0.7.1 it left the treasury for nowhere).
>   9. The holdings dial at the whole of the paper (0.7.15, Jerus: "central
>      bank bond holding can ve 100% if one wants"): the central bank buys
>      the bank's term paper first, and only once the bank has none left
>      the households', at the curve's market value, their face onto its
>      book and the price made for them - the audit closing and M0 moving by
>      exactly the money made - until it holds all of it; a save and load
>      in the middle keeps the dial, the holdings and the pace, and both
>      cities buy the same from the households the month after.
> 
> Each fixture causes its condition rather than finding a city in it.

**Uses:** [Game](Game.md) (18), [MoneyAudit](MoneyAudit.md) (13), [Debt](Debt.md) (10), [HouseholdBalance](HouseholdBalance.md) (7), [DebtManager](DebtManager.md) (5), [CentralBank](CentralBank.md) (5), [Household](Household.md) (5), [GameFiles](GameFiles.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [Founding](Founding.md) (2), [Bank](Bank.md) (2), [WorkingHousehold](WorkingHousehold.md) (2), [FamilyStructure](FamilyStructure.md) (1), [PayTier](PayTier.md) (1), [OutwardInvestment](OutwardInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 151 | · 1. at the settle |
| 200 | · 2. the coupon |
| 214 | · 3. the waterfall |
| 247 | · 4. selling after the curve rose |
| 277 | · 5. a buyback |
| 314 | · 6. the save |
| 357 | · 7. an old save |
| 410 | · 8. a dollar bond bought back |
| 446 | 9. the whole of the paper (0.7.15) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 55 | `static int fails` |  |
| 56 | `static PrintStream out` |  |
| 57 | `static PrintStream quiet` |  |
| 95 | `static int closedMonths, brokenMonths` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 53 | 538 | **type** `public class HoldersCheck` | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is bought back - every crossing declared, and every holding exactly where the... |
| 59 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 64 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 74 | 4 | `static void quietly(Runnable r)` |  |
| 79 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 86 | 1 | `static double savings(Game g)` |  |
| 89 | 5 | `static boolean booksAgree(Game g)` | The two books of the households' paper agree: the cells, and the paper. |
| 98 | 10 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit. |
| 109 | 336 | `public static void main(String[] args) throws Exception` |  |

### 9. the whole of the paper (0.7.15) (lines 446-590)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 5 | `static Debt termPiece(DebtManager ledger)` | The one piece of term paper a city holds, or null. |
| 455 | 135 | `static void theWholeBook() throws Exception` |  |

