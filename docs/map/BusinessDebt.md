# BusinessDebt.java - 87 lines · 12 methods · 0 constants · model

`ham/citybuildersim/BusinessDebt.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Base class for private-sector borrowing.
> 
> Deliberately a separate hierarchy from {@link Debt} rather than a subclass of
> it, because the two behave differently in one way that matters:
> 
>   Debt.processMonth(Game) charges interest straight to the CITY's cash via
>   game.InterestExpense(). A business loan must not do that. Its interest is an
>   operating expense on that sector's income statement, and the sector's cash
>   already moves by its net income - so charging cash here as well would take
>   the money twice.
> 
> So a BusinessDebt only ever advances its own clock. The manager reports the
> interest, the income statement expenses it, and cash follows from net income.
> The single exception is principal at maturity, which is a cash movement and
> not an expense; the manager hands that back to the sector to settle.

**Used by (5):** [BankCheck](BankCheck.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessLoan](BusinessLoan.md), [DataSave](DataSave.md), [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `protected String sector` | Which set of books this sits on - see the constants on BusinessDebtManager. |
| 25 | `protected double faceValue` |  |
| 26 | `protected double outstandingPrincipal` |  |
| 27 | `protected int duration` |  |
| 28 | `protected int remainingMonths` |  |
| 29 | `protected int monthStarted` |  |
| 32 | `protected double annualRate` | Fixed at issue. |
| 34 | `protected String type` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 68 | **type** `public abstract class BusinessDebt` | Base class for private-sector borrowing. |
| 37 | 1 | `public abstract void processMonth()` | Advances the clock by one month. |
| 49 | 5 | `public void writeDown(double scale)` | Cuts this loan down in a restructuring - the lender takes the loss. |
| 55 | 1 | `public abstract double getMonthlyInterestExpense()` |  |
| 57 | 1 | `public abstract double getOutstandingPrincipal()` |  |
| 59 | 1 | `public abstract int getMaturityMonth()` |  |
| 61 | 1 | `public abstract boolean isMatured()` |  |
| 63 | 1 | `public abstract String getType()` |  |
| 65 | 3 | `public String getSector()` |  |
| 69 | 3 | `public double getAnnualRate()` |  |
| 73 | 3 | `public double getFaceValue()` |  |
| 77 | 3 | `public int getRemainingMonths()` |  |
| 82 | 4 | `public void redenominate(double scale)` | The loan in the new unit. |

