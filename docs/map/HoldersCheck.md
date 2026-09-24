# HoldersCheck.java - 410 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/HoldersCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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
> 
> Each fixture causes its condition rather than finding a city in it.

**Uses:** [Game](Game.md) (12), [MoneyAudit](MoneyAudit.md) (6), [HouseholdBalance](HouseholdBalance.md) (5), [Debt](Debt.md) (5), [Household](Household.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (2), [CentralBank](CentralBank.md) (2), [WorkingHousehold](WorkingHousehold.md) (2), [Bank](Bank.md) (1), [FamilyStructure](FamilyStructure.md) (1), [PayTier](PayTier.md) (1), [OutwardInvestment](OutwardInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 125 | · 1. at the settle |
| 174 | · 2. the coupon |
| 188 | · 3. the waterfall |
| 221 | · 4. selling after the curve rose |
| 251 | · 5. a buyback |
| 288 | · 6. the save |
| 330 | · 7. an old save |
| 377 | · 8. a dollar bond bought back |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `static int fails` |  |
| 48 | `static PrintStream out` |  |
| 49 | `static PrintStream quiet` |  |
| 87 | `static int closedMonths, brokenMonths` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 45 | 366 | **type** `public class HoldersCheck` | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is bought back - every crossing declared, and every holding exactly where the... |
| 51 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 56 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 66 | 4 | `static void quietly(Runnable r)` |  |
| 71 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 78 | 1 | `static double savings(Game g)` |  |
| 81 | 5 | `static boolean booksAgree(Game g)` | The two books of the households' paper agree: the cells, and the paper. |
| 90 | 10 | `static MoneyAudit.Result play(Game g)` | A month, held to the audit. |
| 101 | 309 | `public static void main(String[] args) throws Exception` |  |

