# OutsideCheck.java - 783 lines · 5 methods · 6 constants · harnesses

`ham/citybuildersim/OutsideCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The people outside the families: the out of work, the students, the
> unhoused and the orphans (2026-09-11).
> 
> Jerus: "we are to add a new household structure called unemployed... these
> will just sum up by age the unemployed, or unhoused... and they will have
> their own cashflow and stuff." Twenty questions answered; this is every one
> of the answers that can be caused and measured, each with the cause set
> by the fixture rather than stood next to. See
> claude/the-people-the-books-left-out.md.
> 
> The two city sections CAUSE their conditions (FS16) where they once read
> them off a town played for sixty and a hundred and eighty months: the EI
> city is handed adults over its posts (OVERSUPPLY) and has a closing done by
> hand, and the college town is put through a university with its students
> seeded (EducationCheck.seeded()) for a few months.

**Uses:** [AgeBand](AgeBand.md) (44), [FamilyModel](FamilyModel.md) (18), [FamilyStructure](FamilyStructure.md) (17), [Unemployment](Unemployment.md) (15), [PayTier](PayTier.md) (11), [PopulationCohorts](PopulationCohorts.md) (11), [HouseholdBalance](HouseholdBalance.md) (10), [HouseholdAccounts](HouseholdAccounts.md) (8), [Game](Game.md) (7), [EducationCheck](EducationCheck.md) (7), [PriceLevel](PriceLevel.md) (6), [BuildingsTemplate](BuildingsTemplate.md) (5), [UnemployedHousehold](UnemployedHousehold.md) (5), [StudentHousehold](StudentHousehold.md) (5), [Bank](Bank.md) (5), [CellSpread](CellSpread.md) (4), [Health](Health.md) (4), [EducationType](EducationType.md) (4), [JobType](JobType.md) (3), [OrphanHousehold](OrphanHousehold.md) (3), [GameFiles](GameFiles.md) (3), [MoneyAudit](MoneyAudit.md) (3), [PopulationManager](PopulationManager.md) (2), [WageBand](WageBand.md) (2), [Household](Household.md) (2), [EconomyManager](EconomyManager.md) (2), [Sectors](Sectors.md) (2), [TaxPolicy](TaxPolicy.md) (2), [Healthcare](Healthcare.md) (1), [Founding](Founding.md) (1)... and 2 more

## Sections

| line | section |
|---:|---|
| 74 | · 1. a student is not unemployed |
| 94 | · 2. the families are the people who work |
| 131 | · 3. the out of work share doors with their own kind |
| 177 | · 3b. the children go where their parent goes |
| 264 | · 4. EI, on the inflow |
| 347 | · 5. the books |
| 453 | · 6. health |
| 478 | · 7. a real city: the treasury and the books agree |
| 630 | · 8. a city with a college: the students' money, and a save |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `OutsideCheck.COLLEGE_STAYERS` | `EducationCheck.LOAN_STAYING_STUDENTS` | The university students put into the college town with their whole course before them (FS16): the ones who stay in the students' cell and borrow at the dial's shortfall. |
| 54 | `OutsideCheck.COLLEGE_FIRST_FINISHERS` | `2 * EducationCheck.LOAN_FINISHING_STUDENTS` | The university students put into the college town in their last month (FS16): more than the hundred graduates the section asks for, who finish in its first month and carry their loans to the working families in its se... |
| 57 | `OutsideCheck.COLLEGE_LAST_FINISHERS` | `EducationCheck.LOAN_FINISHING_STUDENTS` | The university students put into the college town to finish in the last month it is read at (FS16), so somebody finished that month. |
| 60 | `OutsideCheck.COLLEGE_MONTHS` | `5` | Months the college town is read over once its students are put in (FS16): the first finishers graduate in the first, are carried to the working families in the second and repay from the third, and the last finish in t... |
| 63 | `OutsideCheck.CITY_MONTHS` | `6` | Months the EI city is lived before its adults are handed over (FS16): enough for the buildings put up at once to be crewed, where it was played fifty-nine. |
| 66 | `OutsideCheck.OVERSUPPLY` | `1.5` | The adults the EI city is handed, as a multiple of the posts it offers (FS16): half as many again, so every post is filled and a pool is left over. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 762 | **type** `public class OutsideCheck` | The people outside the families: the out of work, the students, the unhoused and the orphans (2026-09-11). |
| 26 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 32 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 37 | 5 | `static void quietly(Runnable work)` |  |
| 43 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 68 | 715 | `public static void main(String[] args)` |  |

