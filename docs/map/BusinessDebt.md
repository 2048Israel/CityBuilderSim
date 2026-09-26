# BusinessDebt.java - 93 lines · 12 methods · 0 constants · model

`ham/citybuildersim/BusinessDebt.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
> Two kinds: BusinessLoan, the bullet every sector borrows on - with its
> InterimLoan, lent after a default and ranked first (0.7.12, round 5) - and
> Mortgage, the insured, amortizing loan a landlord buys a residential
> building with.

**Used by (9):** [BankCheck](BankCheck.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessLoan](BusinessLoan.md), [CreditCheck](CreditCheck.md), [DataSave](DataSave.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [Mortgage](Mortgage.md), [MortgageCheck](MortgageCheck.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `protected String sector` | Which set of books this sits on - see the constants on BusinessDebtManager. |
| 31 | `protected double faceValue` |  |
| 32 | `protected double outstandingPrincipal` |  |
| 33 | `protected int duration` |  |
| 34 | `protected int remainingMonths` |  |
| 35 | `protected int monthStarted` |  |
| 38 | `protected double annualRate` | Fixed at issue. |
| 40 | `protected String type` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 26 | 68 | **type** `public abstract class BusinessDebt` | Base class for private-sector borrowing. |
| 43 | 1 | `public abstract void processMonth()` | Advances the clock by one month. |
| 55 | 5 | `public void writeDown(double scale)` | Cuts this loan down in a restructuring - the lender takes the loss. |
| 61 | 1 | `public abstract double getMonthlyInterestExpense()` |  |
| 63 | 1 | `public abstract double getOutstandingPrincipal()` |  |
| 65 | 1 | `public abstract int getMaturityMonth()` |  |
| 67 | 1 | `public abstract boolean isMatured()` |  |
| 69 | 1 | `public abstract String getType()` |  |
| 71 | 3 | `public String getSector()` |  |
| 75 | 3 | `public double getAnnualRate()` |  |
| 79 | 3 | `public double getFaceValue()` |  |
| 83 | 3 | `public int getRemainingMonths()` |  |
| 88 | 4 | `public void redenominate(double scale)` | The loan in the new unit. |

