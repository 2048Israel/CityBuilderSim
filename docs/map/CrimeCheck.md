# CrimeCheck.java - 340 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/CrimeCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> Crime, the police and the prisons: every claim in
> claude/crime-has-reasons.md, each with its own cause.
> 
> Jerus, 2026-09-11: "crime is a function of unemployment, and tight or under
> households, we need police, and also prison... alot of police drastically
> reduces it but never eliminates it... if there is a reason for crime there
> is no way to actually remove it without changing the underlying reason."

**Uses:** [Crime](Crime.md) (54), [SafetyType](SafetyType.md) (8), [Game](Game.md) (8), [AgeBand](AgeBand.md) (5), [Migration](Migration.md) (3), [BuildingManager](BuildingManager.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (2), [SectorBooks](SectorBooks.md) (2), [FamilyStructure](FamilyStructure.md) (2), [PopulationCohorts](PopulationCohorts.md) (1), [BuildingType](BuildingType.md) (1), [JobType](JobType.md) (1), [Founding](Founding.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [PopulationManager](PopulationManager.md) (1), [FamilyModel](FamilyModel.md) (1), [PrisonerHousehold](PrisonerHousehold.md) (1), [EconomyManager](EconomyManager.md) (1)

## Sections

| line | section |
|---:|---|
| 65 | · 1. K is Canada |
| 79 | · 2. the police |
| 132 | · 3. who is caught, and who is held |
| 183 | · 4. migration |
| 188 | · 5. the buildings |
| 216 | · 6. a played city |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 329 | **type** `public class CrimeCheck` | Crime, the police and the prisons: every claim in claude/crime-has-reasons.md, each with its own cause. |
| 16 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 22 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 27 | 5 | `static void quietly(Runnable work)` |  |
| 34 | 3 | `static double peopleFor(double adults)` | People for this many adults at the game's equilibrium share. |
| 39 | 3 | `static double officersFor(double coverage, double people)` | Officers for this coverage of this many people. |
| 44 | 5 | `static Crime month(Crime.Causes k, double people, double coverage, double cells)` | One month of a fresh Crime with these reasons, returned. |
| 50 | 9 | `static Crime.Causes bust(double adults)` |  |
| 60 | 280 | `public static void main(String[] args) throws Exception` |  |

