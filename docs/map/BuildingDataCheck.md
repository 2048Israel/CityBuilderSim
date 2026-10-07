# BuildingDataCheck.java - 297 lines · 3 methods · 0 constants · harnesses

`ham/citybuildersim/BuildingDataCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> The migration's safety net: buildings.json must produce exactly the templates
> the hardcoded definitions did.
> 
> Run against the real Gson, not a stub, because the whole risk of moving data
> out of code is that the two quietly disagree - a field that silently reads
> zero, an id that lands on the wrong building, a job tier that never loads.
> Field-by-field equality against the built-ins is the only check that catches
> that.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (15), [BuildingType](BuildingType.md) (6), [Good](Good.md) (5), [BuildingManager](BuildingManager.md) (4), [EducationType](EducationType.md) (3), [SafetyType](SafetyType.md) (3), [BuildingCatalog](BuildingCatalog.md) (2), [JobType](JobType.md) (2), [Sectors](Sectors.md) (2), [CareType](CareType.md) (1), [Refining](Refining.md) (1)

## Sections

| line | section |
|---:|---|
| 34 | · · what the code says |
| 39 | · · what the file says |
| 54 | · · every field of every building |
| 142 | · · care types line up with the category |
| 195 | · · the sea is drawn by water works, and one of them (0.7.59) |
| 222 | · · fuel (0.7.62, batch K; spec-land 2.7) |
| 248 | · · and every profession has exactly one school |
| 267 | · · ids are unique, which the saves depend on |
| 280 | · · the manager actually uses the file |

## Fields (state)

| line | field | says |
|---:|---|---|
| 17 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 15 | 283 | **type** `public class BuildingDataCheck` | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. |
| 19 | 7 | `static void check(String label, double actual, double expected)` |  |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 265 | `public static void main(String[] args)` |  |

