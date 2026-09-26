# DeathRecordCheck.java - 206 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/DeathRecordCheck.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The running totals of the dead, by age and for the orphans and the unhoused.
> 
> Jerus, 2026-09-11: "in history/graphs track how many of each category have
> died cumulative over time" - by age band, and the orphans and the unhoused;
> graphs only. The months are recorded, the totals derived. Every claim sets
> its own cause.

**Uses:** [Unemployment](Unemployment.md) (7), [HistorySave](HistorySave.md) (5), [AgeBand](AgeBand.md) (4), [GameFiles](GameFiles.md) (2), [Game](Game.md) (2), [BuildingManager](BuildingManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

## Sections

| line | section |
|---:|---|
| 39 | · 1. who among the dead |
| 69 | · 2. the running total |
| 79 | · 3. a city whose employer closes |
| 116 | · CLOSING THE MILL USED TO ORPHAN CHILDREN, AND IT NO LONGER DOES. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 13 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 196 | **type** `public class DeathRecordCheck` | The running totals of the dead, by age and for the orphans and the unhoused. |
| 15 | 5 | `static void check(String label, double actual, double expected, double tol)` |  |
| 21 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 26 | 5 | `static void quietly(Runnable work)` |  |
| 32 | 4 | `static double last(HistorySave h, String key)` |  |
| 37 | 169 | `public static void main(String[] args) throws Exception` |  |

