# HealthCheck.java - 927 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/HealthCheck.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> Sickness: what it moves, and - much more importantly - what it does not.
> 
> The specification was one sentence: "it modifies the fillrate, but doesnt
> reduce workforce." Almost every assertion here is a way of saying the second
> half of that, because the second half is what a plausible implementation gets
> wrong. Cutting the workforce would have been a smaller change and would have
> looked identical on the output line - and would have quietly cut the wage
> bill, the wage tax, the households' income and the rent they can afford, none
> of which anybody asked for.
> 
> NUMBERS ARE COMPARED AGAINST THE MODEL'S OWN CONSTANTS, not against literals.
> That rule has been earned five separate times in this codebase: an assertion
> pinned to .06 or to 2.25 tests that nobody edited the harness, not that the
> mechanic works.

**Uses:** [Healthcare](Healthcare.md) (66), [Health](Health.md) (47), [CareType](CareType.md) (40), [AgeBand](AgeBand.md) (35), [PopulationCohorts](PopulationCohorts.md) (16), [Game](Game.md) (11), [BuildingManager](BuildingManager.md) (7), [Migration](Migration.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [JobType](JobType.md) (5), [Good](Good.md) (4), [TimeSkipReport](TimeSkipReport.md) (4), [GameFiles](GameFiles.md) (2), [EconomyManager](EconomyManager.md) (2), [Retail](Retail.md) (2), [FamilyModel](FamilyModel.md) (2), [PopulationManager](PopulationManager.md) (1), [PayTier](PayTier.md) (1), [NationalAccounts](NationalAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 44 | · 1. the buildings know what they treat |
| 155 | · 2. coverage sets the baseline |
| 183 | · 3. outbreaks actually happen |
| 267 | · 4. the same month rolls the same way |
| 307 | · 5. THE POINT: output falls, nobody does |
| 496 | · 6. an unstaffed hospital treats nobody |
| 536 | · 7. what care does to mortality |
| 651 | · 8. death care |
| 722 | · 9. senior care draws people in |
| 743 | · 10. and somebody pays for all of it |
| 865 | · 11. a skip cannot hide an epidemic |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 906 | **type** `public class HealthCheck` | Sickness: what it moves, and - much more importantly - what it does not. |
| 26 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 33 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 38 | 870 | `public static void main(String[] args) throws Exception` |  |
| 910 | 9 | `static void stock(Game g)` | A city with enough in it that the sectors have something to lose. |
| 920 | 7 | `static void cleanUp(Path root)` |  |

