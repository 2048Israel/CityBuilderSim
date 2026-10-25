# BuildingDataCheck.java - 833 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/BuildingDataCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The migration's safety net: buildings.json must produce exactly the templates
> the hardcoded definitions did.
> 
> Run against the real Gson, not a stub, because the whole risk of moving data
> out of code is that the two quietly disagree - a field that silently reads
> zero, an id that lands on the wrong building, a job tier that never loads.
> Field-by-field equality against the built-ins is the only check that catches
> that.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (48), [Good](Good.md) (22), [HeavyIndustry](HeavyIndustry.md) (16), [BuildingType](BuildingType.md) (14), [Resource](Resource.md) (13), [Sectors](Sectors.md) (10), [Mining](Mining.md) (10), [CoalMining](CoalMining.md) (10), [CopperMining](CopperMining.md) (10), [JobType](JobType.md) (9), [Refining](Refining.md) (7), [Game](Game.md) (7), [WoodProducts](WoodProducts.md) (7), [Quarrying](Quarrying.md) (6), [Forestry](Forestry.md) (6), [CementConcrete](CementConcrete.md) (6), [Manufacturing](Manufacturing.md) (5), [BuildingManager](BuildingManager.md) (4), [EducationType](EducationType.md) (3), [SafetyType](SafetyType.md) (3), [CopperRefining](CopperRefining.md) (3), [BuildingCatalog](BuildingCatalog.md) (2), [RefineryFlow](RefineryFlow.md) (2), [Automotive](Automotive.md) (2), [StrategicReserve](StrategicReserve.md) (2), [CareType](CareType.md) (1), [Sector](Sector.md) (1), [Retail](Retail.md) (1), [Ports](Ports.md) (1), [Oil](Oil.md) (1)

## Sections

| line | section |
|---:|---|
| 40 | · · what the code says |
| 45 | · · what the file says |
| 60 | · · every field of every building |
| 206 | · · care types line up with the category |
| 259 | · · the sea is drawn by water works, and one of them (0.7.59) |
| 286 | · · fuel (0.7.62, batch K; spec-land 2.7) |
| 320 | · · the refinery's units (0.7.80, batch O4; spec-oil 2.3) |
| 369 | · · the phase-1 buyers and the forecourt (0.7.83, batch O6) |
| 426 | · · the oil storage (0.7.85, batch O8; spec-oil 2.8) |
| 459 | · · the ports (0.7.86, batch O9; spec-oil 2.9) |
| 481 | · · the oil at sea (0.7.91, batch O10; spec-oil 2.7, 2.11) |
| 525 | · · the site each stands on (0.7.110, batch I1) |
| 639 | · · a works' own power and its consumables (0.7.112, batch I2) |
| 695 | · · the old steel a yard takes, and the year each row first existed (0.7.123, batch I4) |
| 767 | · · the raw coal a preparation plant washes (0.7.126, batch I6) |
| 784 | · · and every profession has exactly one school |
| 803 | · · ids are unique, which the saves depend on |
| 816 | · · the manager actually uses the file |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 819 | **type** `public class BuildingDataCheck` | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. |
| 19 | 7 | `static void check(String label, double actual, double expected)` |  |
| 28 | 4 | `static BuildingsTemplate byId(List<BuildingsTemplate> data, int id)` | The building with this id in a list of templates, or null. |
| 33 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 38 | 795 | `public static void main(String[] args)` |  |

