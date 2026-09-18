# BankCheck.java - 1,095 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/BankCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The commercial bank, and the families it discharges.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
> Before this went in there were three lenders in the game and none of them was
> anybody: a sector borrowed from BusinessDebtManager, the city sold bonds to a
> market, and a family short of the shop borrowed from nothing at all. Money
> arrived from outside the city and interest disappeared out of it.
> 
> So the questions here are not "does the class compile". They are:
> 
>   1. Does the price of credit actually depend on the bank? A premium that no
>      borrower pays is a number on a screen.
>   2. Does the money still add up, now that four flows that used to run to and
>      from nowhere run between two pools inside the city?
>   3. Does a family that cannot carry its debt get discharged - and does the
>      bank, not thin air, eat the loss?
> 
> Each of those is measured by CAUSING the condition, never by finding a city
> that happens to be in it.

**Uses:** [Bank](Bank.md) (65), [Game](Game.md) (11), [HouseholdBalance](HouseholdBalance.md) (8), [FamilyStructure](FamilyStructure.md) (8), [GameFiles](GameFiles.md) (5), [DebtManager](DebtManager.md) (4), [MoneyAudit](MoneyAudit.md) (4), [PayTier](PayTier.md) (4), [Sectors](Sectors.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [BusinessInvestment](BusinessInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 63 | · 1. the two limits |
| 94 | · 1b. WHAT TO PAY SAVERS IS A DECISION |
| 206 | · · ...and it does not open counters either |
| 262 | · 2. the price of strain |
| 310 | · 3. the premium reaches the borrower |
| 339 | · 4. a real city, and its money |
| 427 | · 5. the save carries the bank's cash |
| 486 | · 6. a family that cannot carry it |
| 601 | · 7. and the city opens its own |
| 852 | · 8. the accounting identities, on a played city |
| 1036 | · 9. capital is the constraint, and it can run out |

## Fields (state)

| line | field | says |
|---:|---|---|
| 32 | `static int fails` |  |
| 33 | `static PrintStream out` |  |
| 34 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 30 | 1066 | **type** `public class BankCheck` | The commercial bank, and the families it discharges. |
| 36 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 41 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 51 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 58 | 1037 | `public static void main(String[] args) throws Exception` |  |

