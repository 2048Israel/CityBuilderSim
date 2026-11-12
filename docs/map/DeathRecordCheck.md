# DeathRecordCheck.java - 275 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/DeathRecordCheck.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> The running totals of the dead, by age and for the orphans and the unhoused.
> 
> Jerus, 2026-09-11: "in history/graphs track how many of each category have
> died cumulative over time" - by age band, and the orphans and the unhoused;
> graphs only. The months are recorded, the totals derived. Every claim sets
> its own cause.

**Uses:** [Unemployment](Unemployment.md) (7), [Game](Game.md) (5), [HistorySave](HistorySave.md) (5), [AgeBand](AgeBand.md) (4), [GameFiles](GameFiles.md) (1), [BuildingManager](BuildingManager.md) (1), [WantedPosts](WantedPosts.md) (1), [TempDirs](TempDirs.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 64 | · 1. who among the dead |
| 94 | · 2. the running total |
| 104 | · 3. a city whose employer closes |
| 129 | · CLOSING THE MILL USED TO ORPHAN CHILDREN, AND IT NO LONGER DOES. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 13 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 265 | **type** `public class DeathRecordCheck` | The running totals of the dead, by age and for the orphans and the unhoused. |
| 15 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 21 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 26 | 5 | `static void quietly(Runnable work)` |  |
| 33 | 15 | `static Game town(java.nio.file.Path root, String data, String legacy)` | Section 3's city: a founding with its houses, shops, mill and works stood free, 48 months played; a second one is its twin (same months, same path). |
| 49 | 4 | `static double last(HistorySave h, String key)` |  |
| 54 | 221 | `public static void main(String[] args) throws Exception` |  |

