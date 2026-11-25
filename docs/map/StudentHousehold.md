# StudentHousehold.java - 64 lines · 11 methods · 1 constants · model

`ham/citybuildersim/StudentHousehold.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

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

**Used by (9):** [EducationCheck](EducationCheck.md), [FamilyModel](FamilyModel.md), [Game](Game.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdCheck](HouseholdCheck.md), [HousingMarket](HousingMarket.md), [HousingMarketCheck](HousingMarketCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 32 | `StudentHousehold.UNLIMITED` | `1e15` | A loan that covers anything is the plan's to count on; large, not infinite, so the arithmetic stays finite. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `private java.util.function.DoubleSupplier moneyUnit` | Derived city context, also supplied after a load; no independent saved monetary stock. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 29 | 36 | **type** `public class StudentHousehold extends Household` | Full-time students, as one ledger. |
| 35 | 1 | `void setMoneyUnitSource(java.util.function.DoubleSupplier source)` |  |
| 37 | 3 | `public StudentHousehold()` |  |
| 41 | 1 | `public PayTier tier()` |  |
| 42 | 1 | `public int row()` |  |
| 43 | 1 | `public boolean isRetired()` |  |
| 44 | 1 | `public int grownUps()` |  |
| 45 | 1 | `public int size()` |  |
| 47 | 1 | `public String label()` |  |
| 48 | 1 | `public String key()` |  |
| 56 | 6 | `protected double fundShortfall(double still, double disposablePer)` | The student loan: whatever is still short, from the treasury, at no interest while they study. |
| 63 | 1 | `protected double planningRoom()` |  |

