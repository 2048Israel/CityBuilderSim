# HealthCheck.java - 1,476 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/HealthCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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
> 
> WHAT THIS HAS TO PROVE, section by section: (1) the buildings know what
> they treat and the founding endowment is the pyramid's; (2) coverage sets
> the baseline sick rate; (3) outbreaks happen and end; (4) the same month
> rolls the same way; (5) output falls and the workforce does not; (6) an
> unstaffed hospital treats nobody; (7) what care does to mortality and
> births; (8) burial, cremation and the backlog; (9) senior care draws people
> in; (10) somebody pays for all of it, and the households paid what the city
> collected; (11) a skip reports the epidemic it lived through. And since the
> clinic had a price (2026-09-19): (12) the fee scale scales the three care
> fees and not the funerals, nobody pays at 0, and the break-even scale is
> struck from the city's own figures; (13) a household that cannot pay goes
> without care rather than without food, the rule in both directions, and a
> poor city at a high fee is a sicker city that buries a larger share of its
> people, and of its old, than at the founding fee;
> (14) a city that can pay is served exactly as it was, at zero tolerance;
> (15) the premium raises rate times the wage bill into the treasury, shows on
> the households' statement, and fees 0 with a premium serves the same people
> as fees 1x without one; (16) both dials survive a save and a reform.

**Uses:** [Healthcare](Healthcare.md) (97), [CareType](CareType.md) (89), [Health](Health.md) (47), [AgeBand](AgeBand.md) (39), [TaxPolicy](TaxPolicy.md) (24), [Game](Game.md) (21), [PopulationCohorts](PopulationCohorts.md) (16), [BuildingManager](BuildingManager.md) (8), [Migration](Migration.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [JobType](JobType.md) (5), [Good](Good.md) (4), [TimeSkipReport](TimeSkipReport.md) (4), [Household](Household.md) (4), [EconomyManager](EconomyManager.md) (3), [HouseholdBalance](HouseholdBalance.md) (3), [GameFiles](GameFiles.md) (2), [Retail](Retail.md) (2), [FamilyModel](FamilyModel.md) (2), [PayTier](PayTier.md) (2), [NationalAccounts](NationalAccounts.md) (2), [PopulationManager](PopulationManager.md) (1), [FamilyStructure](FamilyStructure.md) (1), [HouseholdAccounts](HouseholdAccounts.md) (1)

## Sections

| line | section |
|---:|---|
| 63 | · 1. the buildings know what they treat |
| 174 | · 2. coverage sets the baseline |
| 202 | · 3. outbreaks actually happen |
| 286 | · 4. the same month rolls the same way |
| 326 | · 5. THE POINT: output falls, nobody does |
| 515 | · 6. an unstaffed hospital treats nobody |
| 555 | · 7. what care does to mortality |
| 670 | · 8. death care |
| 741 | · 9. senior care draws people in |
| 762 | · 10. and somebody pays for all of it |
| 884 | · 11. a skip cannot hide an epidemic |
| 921 | · 12. the fee has a dial, and the funerals do not |
| 1031 | · 13. who can afford the clinic |
| 1266 | · 14. the unchanged case, at zero tolerance |
| 1305 | · 15. the premium |
| 1394 | · 16. both dials survive a save, and a reform |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 41 | 1436 | **type** `public class HealthCheck` | Sickness: what it moves, and - much more importantly - what it does not. |
| 45 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 52 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 57 | 1400 | `public static void main(String[] args) throws Exception` |  |
| 1459 | 9 | `static void stock(Game g)` | A city with enough in it that the sectors have something to lose. |
| 1469 | 7 | `static void cleanUp(Path root)` |  |

