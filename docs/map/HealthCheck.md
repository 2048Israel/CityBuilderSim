# HealthCheck.java - 1,921 lines · 9 methods · 5 constants · harnesses

`ham/citybuildersim/HealthCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

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

**Uses:** [Healthcare](Healthcare.md) (98), [CareType](CareType.md) (94), [Health](Health.md) (47), [AgeBand](AgeBand.md) (41), [TaxPolicy](TaxPolicy.md) (26), [Game](Game.md) (24), [PopulationCohorts](PopulationCohorts.md) (16), [Household](Household.md) (14), [Migration](Migration.md) (11), [PriceLevel](PriceLevel.md) (11), [BuildingManager](BuildingManager.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (8), [Good](Good.md) (6), [CellSpread](CellSpread.md) (6), [JobType](JobType.md) (5), [TimeSkipReport](TimeSkipReport.md) (4), [EconomyManager](EconomyManager.md) (3), [Retail](Retail.md) (3), [GameFiles](GameFiles.md) (2), [Founding](Founding.md) (2), [FamilyModel](FamilyModel.md) (2), [PayTier](PayTier.md) (2), [NationalAccounts](NationalAccounts.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [TempDirs](TempDirs.md) (1), [PopulationManager](PopulationManager.md) (1), [LandManager](LandManager.md) (1), [FamilyStructure](FamilyStructure.md) (1), [Sectors](Sectors.md) (1), [Observation](Observation.md) (1)... and 3 more

## Sections

| line | section |
|---:|---|
| 63 | · 1. the buildings know what they treat |
| 187 | · 2. coverage sets the baseline |
| 215 | · 3. outbreaks actually happen |
| 299 | · 4. the same month rolls the same way |
| 339 | · 5. THE POINT: output falls, nobody does |
| 555 | · 6. an unstaffed hospital treats nobody |
| 595 | · 7. what care does to mortality |
| 710 | · 8. death care |
| 781 | · 9. senior care draws people in |
| 816 | · 10. and somebody pays for all of it |
| 960 | · 11. a skip cannot hide an epidemic |
| 997 | · 12. the fee has a dial, and the funerals do not |
| 1112 | · 13. who can afford the clinic |
| 1577 | · 14. the unchanged case, at zero tolerance |
| 1616 | · 15. the premium |
| 1705 | · 16. both dials survive a save, and a reform |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1777 | `HealthCheck.BETWEEN_THE_BILLS` | `Math.sqrt(TaxPolicy.MAX_HEALTH_FEE_SCALE / TaxPolicy.DEFAULT_HEALTH_FEE_SCALE)` | Where a poor household's money is left, in bills: the geometric middle of the span from the founding fee's bill to the dial's top (HX1, RN1-F). |
| 1784 | `HealthCheck.MATERIAL_TURNED_AWAY` | `.05` | HX1: how much more of the care offered the dear twin must turn away than the founding fee's, a fraction: five points. |
| 1787 | `HealthCheck.CHARGED` | `new java.util.IdentityHashMap<>()` | What each cell was charged for care in the month just run (the bill its last plan struck), by cell: betweenTheBills() reads the month's money before that bill. |
| 1789 | `HealthCheck.LIFTED` | `new java.util.IdentityHashMap<>()` |  |
| 1853 | `HealthCheck.BEDS_AHEAD` | `1.2` | HX1: how many places the clinics keep ahead of the people: a fifth over. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 43 | `static int fails` |  |
| 1790 | `static double biggestLiftError` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 41 | 1881 | **type** `public class HealthCheck` | Sickness: what it moves, and - much more importantly - what it does not. |
| 45 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 52 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 57 | 1711 | `public static void main(String[] args) throws Exception` |  |
| 1788 | 1 | **type** `record CareLift(double bill, double walletSeed, double spreadSeed, double extraCash)` |  |
| 1793 | 12 | `static double bufferForCashAfterGap(double gap, double left)` | The buffer mean that leaves `left` after `gap`, keeping the native lognormal spread. |
| 1817 | 34 | `static double betweenTheBills(Game town)` | HX1, RN1-F: the households the drain left short of a basket are given the savings that leave them, after the month's rent, fixed bills and the shopping they planned, a basket and BETWEEN_THE_BILLS of the founding fee'... |
| 1862 | 11 | `static int keepBedsAhead(Game town, int standing, int[] plan, int month, boolean first)` | HX1, CU2's path: the Walk-in Clinics standing, more added the month the people outgrow BEDS_AHEAD of their places, so that every twin has a place for every person whatever path the city takes (a fixed twelve clinics h... |
| 1875 | 9 | `static void stock(Game g)` | A city with enough in it that the sectors have something to lose. |
| 1902 | 11 | `static void trucksKeepUp(Game town)` | THE TRUCKS KEPT AHEAD OF THE PLANT, SO THE FEE IS THE ONLY DIFFERENCE BETWEEN THE TWINS (FR2, 2026-10-18). |
| 1914 | 7 | `static void cleanUp(Path root)` |  |

