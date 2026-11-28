# DeathRecordCheck.java - 432 lines · 10 methods · 0 constants · harnesses

`ham/citybuildersim/DeathRecordCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The running totals of the dead, by age and for the orphans and the unhoused.
> 
> Jerus, 2026-09-11: "in history/graphs track how many of each category have
> died cumulative over time" - by age band, and the orphans and the unhoused;
> graphs only. The months are recorded, the totals derived. Every claim sets
> its own cause.

**Uses:** [Game](Game.md) (8), [FamilyModel](FamilyModel.md) (8), [PayTier](PayTier.md) (8), [PopulationCohorts](PopulationCohorts.md) (7), [Unemployment](Unemployment.md) (7), [HistorySave](HistorySave.md) (5), [AgeBand](AgeBand.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (2), [GameFiles](GameFiles.md) (1), [BuildingManager](BuildingManager.md) (1), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 217 | · 1. who among the dead |
| 247 | · 2. the running total |
| 257 | · 3. a city whose employer closes |
| 282 | · CLOSING THE MILL USED TO ORPHAN CHILDREN, AND IT NO LONGER DOES. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 13 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 422 | **type** `public class DeathRecordCheck` | The running totals of the dead, by age and for the orphans and the unhoused. |
| 15 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 21 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 26 | 5 | `static void quietly(Runnable work)` |  |
| 33 | 3 | `static Game town(java.nio.file.Path root, String data, String legacy)` | Section 3's city: a founding with its houses, shops, mill and works stood free, 48 months played; a second one is its twin (same months, same path). |
| 38 | 15 | `static Game town(java.nio.file.Path root, String data, String legacy, int month)` | The same founding at a named month, keeping the buildings and the layoff unchanged (LD3). |
| 54 | 4 | `static double last(HistorySave h, String key)` |  |
| 60 | 48 | `static void layoffsBesideTwin(java.nio.file.Path root)` | LD3 measures the twins in the same month, across the whole two-year layoff window. |
| 117 | 55 | `static void waveEndsAtOnce()` | 4. |
| 179 | 35 | `static void nobodyComesHome()` | 4b. |
| 215 | 217 | `public static void main(String[] args) throws Exception` |  |

