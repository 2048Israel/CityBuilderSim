# HouseholdMemoryCheck.java - 278 lines · 6 methods · 1 constants · harnesses

`ham/citybuildersim/HouseholdMemoryCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The households remember: the builder keeps what still fits.
> 
> Jerus, 2026-09-11: a record of the households of each type, "so when the
> model rebuilds it has a reference to try and keep but still allow change".
> Keep what still fits, 1% a month re-forming on its own, every cell, in the
> save and on the graphs. Every claim sets its own cause. See
> claude/the-households-remember.md.

**Uses:** [FamilyStructure](FamilyStructure.md) (36), [FamilyModel](FamilyModel.md) (32), [PayTier](PayTier.md) (14), [AgeBand](AgeBand.md) (10), [PopulationCohorts](PopulationCohorts.md) (8), [Game](Game.md) (4), [GameFiles](GameFiles.md) (2), [HistorySave](HistorySave.md) (2), [BuildingManager](BuildingManager.md) (1)

## Sections

| line | section |
|---:|---|
| 60 | · 1. a bare model does not remember |
| 74 | · 2. the same city a month later |
| 89 | · 3. kept, and the rest built from the people left over |
| 132 | · 4. a child grows up |
| 158 | · 5. the tiers follow the jobs |
| 186 | · 6. a save |
| 229 | · 7. a city |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `HouseholdMemoryCheck.WORKING` | `java.util.Arrays.stream(FamilyStructure.values()).filter(s -> ! s.isRetired()...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 267 | **type** `public class HouseholdMemoryCheck` | The households remember: the builder keeps what still fits. |
| 16 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 22 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 27 | 5 | `static void quietly(Runnable work)` |  |
| 34 | 7 | `static PopulationCohorts pyramid(double babies, double children, double teens, double adults, double seniors)` | A pyramid from the five bands this fixture cares about. |
| 46 | 5 | `static double total(FamilyModel f)` |  |
| 52 | 226 | `public static void main(String[] args) throws Exception` |  |

