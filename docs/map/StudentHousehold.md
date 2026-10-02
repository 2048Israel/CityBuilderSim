# StudentHousehold.java - 57 lines · 10 methods · 1 constants · model

`ham/citybuildersim/StudentHousehold.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> Full-time students, as one ledger.
> 
> Jerus, 2026-09-11, asked where the students go now the out of work have
> books of their own: "new category". They were counted as unemployed -
> PopulationManager.getUnemployed() never took them off the workforce - and
> lived in family households whose tier's wages they shared.
> 
> WHAT A STUDENT LIVES ON, per Jerus: their own savings, a grant, and a
> student loan. The grant is the Canada Student Grant ($525 a month of study,
> 2026-27), paid by the treasury - by default as a share of the unskilled
> wage until 0.7.19, and since then as that $525 in founding money kept up
> with the price index (TaxPolicy, THE GRANT FOLLOWS PRICES); since
> 2026-09-21 it is whatever TaxPolicy's grant basis and amount strike (a
> share of the wage, a fixed sum, a share of last month's surplus, a share
> of the course's tuition). The loan is the treasury's too
> and, for now, it never runs out - "can't run out, for now" - so where a
> family would go to the bank's credit line and then go without, a student
> draws a loan for whatever is still short. They pay their own tuition. The
> loan is carried into a family when they graduate, and repaid there out of
> wages, with interest at the city's rate from then and not before; see
> WorkingHousehold.studentRepayment() and Household's THE LOAN'S RATE.
> 
> They live in a home of their own, like the unemployed - a studio, or five
> to a home with other students.

**Uses:** [Household](Household.md) (1), [PayTier](PayTier.md) (1)

**Used by (6):** [EducationCheck](EducationCheck.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `StudentHousehold.UNLIMITED` | `1e15` | A loan that covers anything is the plan's to count on; large, not infinite, so the arithmetic stays finite. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 29 | **type** `public class StudentHousehold extends Household` | Full-time students, as one ledger. |
| 34 | 3 | `public StudentHousehold()` |  |
| 38 | 1 | `public PayTier tier()` |  |
| 39 | 1 | `public int row()` |  |
| 40 | 1 | `public boolean isRetired()` |  |
| 41 | 1 | `public int grownUps()` |  |
| 42 | 1 | `public int size()` |  |
| 44 | 1 | `public String label()` |  |
| 45 | 1 | `public String key()` |  |
| 49 | 6 | `protected double fundShortfall(double still, double disposablePer)` | The student loan: whatever is still short, from the treasury, at no interest while they study. |
| 56 | 1 | `protected double planningRoom()` |  |

