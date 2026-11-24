# SicknessCheck.java - 426 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/SicknessCheck.java` - generated 2026-11-23 by CodeMap; line numbers are as of that run.

> The long sick: who stays sick, and who it kills.
> 
> Jerus, 2026-09-11: sick people who stay sick start dying; the base death
> rate halved for every band but babies and seniors; general care saves lives
> by curing people, not by scaling a death rate. Every claim below sets its
> own cause. See claude/the-long-sick.md.

**Uses:** [AgeBand](AgeBand.md) (68), [Sickness](Sickness.md) (56), [Health](Health.md) (18), [CausesOfDeath](CausesOfDeath.md) (10), [Game](Game.md) (9), [PopulationCohorts](PopulationCohorts.md) (8), [GameFiles](GameFiles.md) (3), [Healthcare](Healthcare.md) (3), [CareType](CareType.md) (3), [BuildingManager](BuildingManager.md) (2), [Founding](Founding.md) (2), [LongPlaytest](LongPlaytest.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 156 | · 1. the base rates |
| 187 | · 2. how much of each band is sick |
| 202 | · 3. the ring: recovery, and nobody dies at once |
| 235 | · 4. each age's chance, and the steady state |
| 299 | · 5. the pyramid |
| 317 | · 6. a city |
| 383 | · 7. a save, and a save from before |
| 403 | · 8. the ring in people (0.7.28) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 15 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 13 | 414 | **type** `public class SicknessCheck` | The long sick: who stays sick, and who it kills. |
| 17 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 24 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 29 | 5 | `static void quietly(Runnable work)` |  |
| 41 | 10 | `static void stock(Game g)` | A city three times the size its founding doctor was meant for, so that without hospitals its general care is thin. |
| 53 | 72 | `static void acuteBesideTheRing()` | Acute cases must be visible this month and never age into the mortality ring. |
| 127 | 26 | `static void theFoundingSeed()` | The founding has not struck a Health rate yet; its zero must not seed phantom chronic sickness. |
| 154 | 272 | `public static void main(String[] args) throws Exception` |  |

