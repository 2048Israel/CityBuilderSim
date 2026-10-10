# BuildingDataCheck.java - 526 lines · 3 methods · 0 constants · harnesses

`ham/citybuildersim/BuildingDataCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The migration's safety net: buildings.json must produce exactly the templates
> the hardcoded definitions did.
> 
> Run against the real Gson, not a stub, because the whole risk of moving data
> out of code is that the two quietly disagree - a field that silently reads
> zero, an id that lands on the wrong building, a job tier that never loads.
> Field-by-field equality against the built-ins is the only check that catches
> that.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (34), [BuildingType](BuildingType.md) (13), [Good](Good.md) (11), [JobType](JobType.md) (9), [Sectors](Sectors.md) (9), [Refining](Refining.md) (7), [BuildingManager](BuildingManager.md) (4), [EducationType](EducationType.md) (3), [SafetyType](SafetyType.md) (3), [Game](Game.md) (3), [BuildingCatalog](BuildingCatalog.md) (2), [RefineryFlow](RefineryFlow.md) (2), [Manufacturing](Manufacturing.md) (2), [StrategicReserve](StrategicReserve.md) (2), [Resource](Resource.md) (2), [CareType](CareType.md) (1), [Sector](Sector.md) (1), [Automotive](Automotive.md) (1), [Retail](Retail.md) (1), [Ports](Ports.md) (1), [Oil](Oil.md) (1)

## Sections

| line | section |
|---:|---|
| 34 | · · what the code says |
| 39 | · · what the file says |
| 54 | · · every field of every building |
| 171 | · · care types line up with the category |
| 224 | · · the sea is drawn by water works, and one of them (0.7.59) |
| 251 | · · fuel (0.7.62, batch K; spec-land 2.7) |
| 285 | · · the refinery's units (0.7.80, batch O4; spec-oil 2.3) |
| 334 | · · the phase-1 buyers and the forecourt (0.7.83, batch O6) |
| 381 | · · the oil storage (0.7.85, batch O8; spec-oil 2.8) |
| 413 | · · the ports (0.7.86, batch O9; spec-oil 2.9) |
| 434 | · · the oil at sea (0.7.91, batch O10; spec-oil 2.7, 2.11) |
| 477 | · · and every profession has exactly one school |
| 496 | · · ids are unique, which the saves depend on |
| 509 | · · the manager actually uses the file |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 512 | **type** `public class BuildingDataCheck` | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. |
| 19 | 7 | `static void check(String label, double actual, double expected)` |  |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 494 | `public static void main(String[] args)` |  |

