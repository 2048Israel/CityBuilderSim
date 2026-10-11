# BuildingDataCheck.java - 659 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/BuildingDataCheck.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

> The migration's safety net: buildings.json must produce exactly the templates
> the hardcoded definitions did.
> 
> Run against the real Gson, not a stub, because the whole risk of moving data
> out of code is that the two quietly disagree - a field that silently reads
> zero, an id that lands on the wrong building, a job tier that never loads.
> Field-by-field equality against the built-ins is the only check that catches
> that.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (44), [HeavyIndustry](HeavyIndustry.md) (16), [BuildingType](BuildingType.md) (14), [Good](Good.md) (13), [JobType](JobType.md) (9), [Sectors](Sectors.md) (9), [Refining](Refining.md) (7), [Resource](Resource.md) (6), [BuildingManager](BuildingManager.md) (4), [Game](Game.md) (4), [EducationType](EducationType.md) (3), [SafetyType](SafetyType.md) (3), [BuildingCatalog](BuildingCatalog.md) (2), [RefineryFlow](RefineryFlow.md) (2), [Manufacturing](Manufacturing.md) (2), [StrategicReserve](StrategicReserve.md) (2), [CareType](CareType.md) (1), [Sector](Sector.md) (1), [Automotive](Automotive.md) (1), [Retail](Retail.md) (1), [Ports](Ports.md) (1), [Oil](Oil.md) (1), [Mining](Mining.md) (1)

## Sections

| line | section |
|---:|---|
| 40 | · · what the code says |
| 45 | · · what the file says |
| 60 | · · every field of every building |
| 195 | · · care types line up with the category |
| 248 | · · the sea is drawn by water works, and one of them (0.7.59) |
| 275 | · · fuel (0.7.62, batch K; spec-land 2.7) |
| 309 | · · the refinery's units (0.7.80, batch O4; spec-oil 2.3) |
| 358 | · · the phase-1 buyers and the forecourt (0.7.83, batch O6) |
| 411 | · · the oil storage (0.7.85, batch O8; spec-oil 2.8) |
| 444 | · · the ports (0.7.86, batch O9; spec-oil 2.9) |
| 466 | · · the oil at sea (0.7.91, batch O10; spec-oil 2.7, 2.11) |
| 510 | · · the site each stands on (0.7.110, batch I1) |
| 538 | · · a works' own power and its consumables (0.7.112, batch I2) |
| 578 | · · the old steel a yard takes, and the year each row first existed (0.7.123, batch I4) |
| 610 | · · and every profession has exactly one school |
| 629 | · · ids are unique, which the saves depend on |
| 642 | · · the manager actually uses the file |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 645 | **type** `public class BuildingDataCheck` | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. |
| 19 | 7 | `static void check(String label, double actual, double expected)` |  |
| 28 | 4 | `static BuildingsTemplate byId(List<BuildingsTemplate> data, int id)` | The building with this id in a list of templates, or null. |
| 33 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 38 | 621 | `public static void main(String[] args)` |  |

