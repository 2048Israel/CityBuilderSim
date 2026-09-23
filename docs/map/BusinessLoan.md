# BusinessLoan.java - 72 lines · 7 methods · 0 constants · model

`ham/citybuildersim/BusinessLoan.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> A fixed-term business loan: interest-only each month, principal repaid in full
> at maturity.
> 
> Bullet repayment rather than amortising, for a reason specific to how these get
> issued. They are underwritten to cover a sector that could not pay its bills,
> so a loan that demanded principal back every month would immediately push that
> sector negative again and trigger another loan, and another - a spiral driven
> by the fix rather than by the business. Interest-only keeps the monthly burden
> to what the sector can plausibly carry, and the balloon at the end simply gets
> refinanced by a new loan, which is what a distressed borrower actually does.
> 
> It also matches the government bonds, which are bullet too.
> 
> The rate is fixed at issue. BusinessDebtManager.getRate() moves with the
> sector's leverage, but that prices NEW borrowing - an existing loan keeps the
> rate it was written at, so a sector that borrowed while healthy stays cheap
> even after its credit deteriorates.

**Uses:** [BusinessDebt](BusinessDebt.md) (1)

**Used by (3):** [BusinessDebtManager](BusinessDebtManager.md), [CreditCheck](CreditCheck.md), [Game](Game.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `private double monthlyRate` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 51 | **type** `public class BusinessLoan extends BusinessDebt` | A fixed-term business loan: interest-only each month, principal repaid in full at maturity. |
| 26 | 11 | `public BusinessLoan(String sector, double faceValue, int months, int monthStarted, double annualRate)` |  |
| 44 | 3 | `public void processMonth()` | No cash movement here on purpose - see the note on BusinessDebt. |
| 49 | 3 | `public double getMonthlyInterestExpense()` |  |
| 54 | 3 | `public double getOutstandingPrincipal()` |  |
| 59 | 3 | `public int getMaturityMonth()` |  |
| 64 | 3 | `public boolean isMatured()` |  |
| 69 | 3 | `public String getType()` |  |

