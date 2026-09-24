# OutsideCheck.java - 674 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/OutsideCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> The people outside the families: the out of work, the students, the
> unhoused and the orphans (2026-09-11).
> 
> Jerus: "we are to add a new household structure called unemployed... these
> will just sum up by age the unemployed, or unhoused... and they will have
> their own cashflow and stuff." Twenty questions answered; this is every one
> of the answers that can be caused and measured, each with the cause set
> by the fixture rather than stood next to. See
> claude/the-people-the-books-left-out.md.

**Uses:** [AgeBand](AgeBand.md) (44), [FamilyModel](FamilyModel.md) (18), [FamilyStructure](FamilyStructure.md) (17), [Unemployment](Unemployment.md) (15), [PayTier](PayTier.md) (11), [PopulationCohorts](PopulationCohorts.md) (11), [HouseholdBalance](HouseholdBalance.md) (9), [HouseholdAccounts](HouseholdAccounts.md) (8), [Game](Game.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (5), [UnemployedHousehold](UnemployedHousehold.md) (5), [StudentHousehold](StudentHousehold.md) (5), [Health](Health.md) (4), [JobType](JobType.md) (3), [OrphanHousehold](OrphanHousehold.md) (3), [GameFiles](GameFiles.md) (3), [MoneyAudit](MoneyAudit.md) (3), [PopulationManager](PopulationManager.md) (2), [WageBand](WageBand.md) (2), [Household](Household.md) (2), [EconomyManager](EconomyManager.md) (2), [Healthcare](Healthcare.md) (1)

## Sections

| line | section |
|---:|---|
| 50 | · 1. a student is not unemployed |
| 70 | · 2. the families are the people who work |
| 107 | · 3. the out of work share doors with their own kind |
| 153 | · 3b. the children go where their parent goes |
| 240 | · 4. EI, on the inflow |
| 323 | · 5. the books |
| 413 | · 6. health |
| 438 | · 7. a real city: the treasury and the books agree |
| 554 | · 8. a city with a college: the students' money, and a save |

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 16 | 659 | **type** `public class OutsideCheck` | The people outside the families: the out of work, the students, the unhoused and the orphans (2026-09-11). |
| 20 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 26 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 31 | 5 | `static void quietly(Runnable work)` |  |
| 37 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 44 | 630 | `public static void main(String[] args)` |  |

