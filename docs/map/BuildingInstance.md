# BuildingInstance.java - 40 lines · 4 methods · 0 constants · model

`ham/citybuildersim/BuildingInstance.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (3), [JobType](JobType.md) (1)

**Used by (1):** [BuildingManager](BuildingManager.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 9 | `BuildingsTemplate template` |  |
| 10 | `int constructionProgress` |  |
| 11 | `boolean paused` |  |
| 12 | `boolean completed` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 7 | 34 | **type** `public class BuildingInstance` |  |
| 14 | 6 | `public BuildingInstance(BuildingsTemplate template)` |  |
| 21 | 9 | `public void advanceConstruction()` |  |
| 34 | 3 | `public int getJobs(JobType type)` | getters |
| 37 | 3 | `public int getTotalJobs()` |  |

