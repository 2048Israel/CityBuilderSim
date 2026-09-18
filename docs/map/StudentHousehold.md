# StudentHousehold.java - 51 lines · 10 methods · 1 constants · model

`ham/citybuildersim/StudentHousehold.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Full-time students, as one ledger.
> 
> Jerus, 2026-09-11, asked where the students go now the out of work have
> books of their own: "new category". They were counted as unemployed -
> PopulationManager.getUnemployed() never took them off the workforce - and
> lived in family households whose tier's wages they shared.
> 
> WHAT A STUDENT LIVES ON, per Jerus: their own savings, a grant, and a
> student loan. The grant is the Canada Student Grant ($525 a month of study,
> 2026-27) as a share of the unskilled wage, paid by the treasury. The loan is
> the treasury's too and, for now, it never runs out - "can't run out, for
> now" - so where a family would go to the bank's credit line and then go
> without, a student draws a loan for whatever is still short. They pay their
> own tuition. The loan is carried into a family when they graduate, and
> repaid there out of wages; see WorkingHousehold.studentRepayment().
> 
> They live in a home of their own, like the unemployed - a studio, or five
> to a home with other students.

**Uses:** [Household](Household.md) (1), [PayTier](PayTier.md) (1)

**Used by (5):** [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 26 | `StudentHousehold.UNLIMITED` | `1e15` | A loan that covers anything is the plan's to count on; large, not infinite, so the arithmetic stays finite. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 29 | **type** `public class StudentHousehold extends Household` | Full-time students, as one ledger. |
| 28 | 3 | `public StudentHousehold()` |  |
| 32 | 1 | `public PayTier tier()` |  |
| 33 | 1 | `public int row()` |  |
| 34 | 1 | `public boolean isRetired()` |  |
| 35 | 1 | `public int grownUps()` |  |
| 36 | 1 | `public int size()` |  |
| 38 | 1 | `public String label()` |  |
| 39 | 1 | `public String key()` |  |
| 43 | 6 | `protected double fundShortfall(double still, double disposablePer)` | The student loan: whatever is still short, from the treasury, at no interest. |
| 50 | 1 | `protected double planningRoom()` |  |

