# SicknessCheck.java - 284 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/SicknessCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> The long sick: who stays sick, and who it kills.
> 
> Jerus, 2026-09-11: sick people who stay sick start dying; the base death
> rate halved for every band but babies and seniors; general care saves lives
> by curing people, not by scaling a death rate. Every claim below sets its
> own cause. See claude/the-long-sick.md.

**Uses:** [AgeBand](AgeBand.md) (64), [Sickness](Sickness.md) (38), [Game](Game.md) (7), [Health](Health.md) (6), [PopulationCohorts](PopulationCohorts.md) (4), [BuildingManager](BuildingManager.md) (2), [Healthcare](Healthcare.md) (2), [GameFiles](GameFiles.md) (2)

## Sections

| line | section |
|---:|---|
| 55 | · 1. the base rates |
| 86 | · 2. how much of each band is sick |
| 101 | · 3. the ring: recovery, and nobody dies at once |
| 134 | · 4. each age's chance, and the steady state |
| 198 | · 5. the pyramid |
| 216 | · 6. a city |
| 260 | · 7. a save, and a save from before |

## Fields (state)

| line | field | says |
|---:|---|---|
| 16 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 271 | **type** `public class SicknessCheck` | The long sick: who stays sick, and who it kills. |
| 18 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 25 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 30 | 5 | `static void quietly(Runnable work)` |  |
| 42 | 10 | `static void stock(Game g)` | A city three times the size its founding doctor was meant for, so that without hospitals its general care is thin. |
| 53 | 231 | `public static void main(String[] args) throws Exception` |  |

