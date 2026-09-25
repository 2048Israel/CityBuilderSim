# BusinessDebt.java - 91 lines · 12 methods · 0 constants · model

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
> The single exception is principal as it falls due - a loan's at maturity, a
> Mortgage's a little every month (0.7.11) - which is a cash movement and not
> an expense; the manager hands that back to the sector to settle.
> 
> Two kinds: BusinessLoan, the bullet every sector borrows on, and Mortgage,
> the insured, amortizing loan a landlord buys a residential building with.

**Used by (7):** [BankCheck](BankCheck.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessLoan](BusinessLoan.md), [DataSave](DataSave.md), [Game](Game.md), [Mortgage](Mortgage.md), [MortgageCheck](MortgageCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 27 | `protected String sector` | Which set of books this sits on - see the constants on BusinessDebtManager. |
| 29 | `protected double faceValue` |  |
| 30 | `protected double outstandingPrincipal` |  |
| 31 | `protected int duration` |  |
| 32 | `protected int remainingMonths` |  |
| 33 | `protected int monthStarted` |  |
| 36 | `protected double annualRate` | Fixed at issue. |
| 38 | `protected String type` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 24 | 68 | **type** `public abstract class BusinessDebt` | Base class for private-sector borrowing. |
| 41 | 1 | `public abstract void processMonth()` | Advances the clock by one month. |
| 53 | 5 | `public void writeDown(double scale)` | Cuts this loan down in a restructuring - the lender takes the loss. |
| 59 | 1 | `public abstract double getMonthlyInterestExpense()` |  |
| 61 | 1 | `public abstract double getOutstandingPrincipal()` |  |
| 63 | 1 | `public abstract int getMaturityMonth()` |  |
| 65 | 1 | `public abstract boolean isMatured()` |  |
| 67 | 1 | `public abstract String getType()` |  |
| 69 | 3 | `public String getSector()` |  |
| 73 | 3 | `public double getAnnualRate()` |  |
| 77 | 3 | `public double getFaceValue()` |  |
| 81 | 3 | `public int getRemainingMonths()` |  |
| 86 | 4 | `public void redenominate(double scale)` | The loan in the new unit. |

