# BankCheck.java - 1,473 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/BankCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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
>   5. Does the bank PAY for the city's paper? It held every bond the city
>      sold and, until 2026-09-21, had never handed over a dollar for one -
>      section 10. Since 0.7.1 it pays for what the households did not take,
>      and earns the discount as it accretes rather than the month it settles.
> 
> Each of those is measured by CAUSING the condition, never by finding a city
> that happens to be in it.

**Uses:** [Bank](Bank.md) (71), [Game](Game.md) (16), [HouseholdBalance](HouseholdBalance.md) (11), [FamilyStructure](FamilyStructure.md) (10), [GameFiles](GameFiles.md) (7), [PayTier](PayTier.md) (7), [DebtManager](DebtManager.md) (6), [Exchange](Exchange.md) (5), [Equity](Equity.md) (5), [MoneyAudit](MoneyAudit.md) (5), [Sectors](Sectors.md) (5), [ExchangeCheck](ExchangeCheck.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [Debt](Debt.md) (2), [BusinessInvestment](BusinessInvestment.md) (1), [ShortTermTBill](ShortTermTBill.md) (1), [MediumTermBond](MediumTermBond.md) (1)

## Sections

| line | section |
|---:|---|
| 89 | · 1. the two limits |
| 120 | · 1b. WHAT TO PAY SAVERS IS A DECISION |
| 261 | · · ...and it does not open counters either |
| 317 | · 2. the price of strain |
| 365 | · 3. the premium reaches the borrower |
| 394 | · 4. a real city, and its money |
| 493 | · 5. the save carries the bank's cash |
| 552 | · 6. a family that cannot carry it |
| 667 | · 7. and the city opens its own |
| 918 | · 8. the accounting identities, on a played city |
| 1113 | · 8b. the trading desk's statement foots |
| 1188 | · 9. capital is the constraint, and it can run out |
| 1250 | 10. the bank pays for the city's paper (2026-09-21) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `static int fails` |  |
| 43 | `static PrintStream out` |  |
| 44 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 1434 | **type** `public class BankCheck` | The commercial bank, and the families it discharges. |
| 46 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 51 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 61 | 6 | `static BuildingsTemplate template(Game game, String name)` |  |
| 74 | 5 | `static double deskParts(Exchange exchange, Equity register, Bank bank)` | The trading desk's statement as the bank screen opens it, less the re-mark: sold to households and abroad, bought from both, dividends on the inventory, tendered into buybacks - the same getters BankScreen reads, with... |
| 80 | 3 | `static double deskParts(Game game)` |  |
| 84 | 1165 | `public static void main(String[] args) throws Exception` |  |

### 10. the bank pays for the city's paper (2026-09-21) (lines 1250-1473)

| line | len | member | says |
|---:|---:|---|---|
| 1304 | 169 | `static void theBankPaysForTheCitysPaper() throws Exception` |  |

