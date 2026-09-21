# BankCheck.java - 1,202 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/BankCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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
>   4. Does the trading desk's statement add up? Jerus: "the bank, just
>      explain to me the trading desk, cause a bunch of times it's losing
>      billions of dollars due to the trading desk." The opened lines have to
>      sum to the figure above them, and the term that makes them - the
>      re-mark of what the desk holds - is measured on a fixture that trades
>      and counted on a played city.
> 
> Each of those is measured by CAUSING the condition, never by finding a city
> that happens to be in it.

**Uses:** [Bank](Bank.md) (67), [Game](Game.md) (12), [HouseholdBalance](HouseholdBalance.md) (11), [FamilyStructure](FamilyStructure.md) (10), [PayTier](PayTier.md) (7), [Exchange](Exchange.md) (5), [Equity](Equity.md) (5), [DebtManager](DebtManager.md) (5), [GameFiles](GameFiles.md) (5), [Sectors](Sectors.md) (5), [MoneyAudit](MoneyAudit.md) (4), [ExchangeCheck](ExchangeCheck.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [BusinessInvestment](BusinessInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 85 | · 1. the two limits |
| 116 | · 1b. WHAT TO PAY SAVERS IS A DECISION |
| 228 | · · ...and it does not open counters either |
| 284 | · 2. the price of strain |
| 332 | · 3. the premium reaches the borrower |
| 361 | · 4. a real city, and its money |
| 449 | · 5. the save carries the bank's cash |
| 508 | · 6. a family that cannot carry it |
| 623 | · 7. and the city opens its own |
| 874 | · 8. the accounting identities, on a played city |
| 1068 | · 8b. the trading desk's statement foots |
| 1143 | · 9. capital is the constraint, and it can run out |

## Fields (state)

| line | field | says |
|---:|---|---|
| 38 | `static int fails` |  |
| 39 | `static PrintStream out` |  |
| 40 | `static PrintStream quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 36 | 1167 | **type** `public class BankCheck` | The commercial bank, and the families it discharges. |
| 42 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 47 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 57 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 70 | 5 | `static double deskParts(Exchange exchange, Equity register, Bank bank)` | The trading desk's statement as the bank screen opens it, less the re-mark: sold to households and abroad, bought from both, dividends on the inventory, tendered into buybacks - the same getters BankScreen reads, with... |
| 76 | 3 | `static double deskParts(Game game)` |  |
| 80 | 1122 | `public static void main(String[] args) throws Exception` |  |

