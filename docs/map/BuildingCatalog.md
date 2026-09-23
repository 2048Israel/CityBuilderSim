# BuildingCatalog.java - 365 lines · 12 methods · 1 constants · model

`ham/citybuildersim/BuildingCatalog.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> Reads the building definitions out of buildings.json.
> 
> Every building used to be forty lines of setter calls in
> BuildingManager.initializeTemplates(), so adding one - or nudging a number
> during a balance pass - meant editing Java and rebuilding. At eleven buildings
> that was tolerable. At forty it would not be, and tuning is exactly the work
> where you want to change a figure, restart, and look.
> 
> WHERE IT LOOKS, IN ORDER
> 
>   1. buildings.json in the working directory - the copy you edit while tuning,
>      and the one a player would edit to mod the game.
>   2. buildings.json packaged inside the jar - the shipped defaults.
> 
> Loaded through getResourceAsStream rather than a File for step 2, because
> Maven puts src/main/resources inside the jar: a File path works perfectly
> running from target/classes in the IDE and fails the moment the game is
> distributed, which is the worst possible time to find out.
> 
> NOTHING HERE CAN STOP THE GAME STARTING
> 
> Any failure - missing file, bad JSON, unknown category, duplicate id - is
> reported and returns null, and BuildingManager falls back to the built-in
> definitions. A typo in a data file should cost you the data file, not the
> game.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (15), [Good](Good.md) (4), [BuildingType](BuildingType.md) (2), [JobType](JobType.md) (2), [CareType](CareType.md) (1), [EducationType](EducationType.md) (1), [SafetyType](SafetyType.md) (1)

**Used by (2):** [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 49 | `BuildingCatalog.FILE_NAME` | `"buildings.json"` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `private String source` | Where the definitions actually came from, for the log line. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 47 | 319 | **type** `public class BuildingCatalog` | Reads the building definitions out of buildings.json. |
| 54 | 3 | `public String getSource()` |  |
| 62 | 44 | `public List<BuildingsTemplate> load()` | the caller should use its own defaults. |
| 108 | 42 | `private List<BuildingsTemplate> parse(Reader reader)` |  |
| 151 | 65 | `private BuildingsTemplate readBuilding(JsonObject o, Set<Integer> seenIds)` |  |
| 230 | 31 | `private void readSector(JsonObject o, BuildingsTemplate template, String name)` | Who owns it and what it makes - see BuildingsTemplate's note. |
| 271 | 12 | `private void readCare(JsonObject o, BuildingsTemplate template, String name)` | The care type, if there is one. |
| 285 | 12 | `private void readTeaches(JsonObject o, BuildingsTemplate template, String name)` | What a school teaches. |
| 299 | 12 | `private void readSafety(JsonObject o, BuildingsTemplate template, String name)` | What a safety building does - POLICE or PRISON. |
| 318 | 12 | `private void readRequiresLicence(JsonObject o, BuildingsTemplate template, String name)` | The licence a building's practice is built on. |
| 331 | 17 | `private void readJobs(JsonObject o, BuildingsTemplate template, String name)` |  |
| 350 | 7 | `private double number(JsonObject o, String key)` | Missing or unreadable fields read as 0, so entries only list what they use. |
| 358 | 7 | `private String string(JsonObject o, String key)` |  |

