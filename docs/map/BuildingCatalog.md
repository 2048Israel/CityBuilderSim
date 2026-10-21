# BuildingCatalog.java - 548 lines · 19 methods · 1 constants · model

`ham/citybuildersim/BuildingCatalog.java` - generated 2026-10-20 by CodeMap; line numbers are as of that run.

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

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (23), [Good](Good.md) (6), [Ports](Ports.md) (4), [BuildingType](BuildingType.md) (2), [Resource](Resource.md) (2), [Deposit](Deposit.md) (2), [JobType](JobType.md) (2), [CareType](CareType.md) (1), [EducationType](EducationType.md) (1), [SafetyType](SafetyType.md) (1), [RefineryFlow](RefineryFlow.md) (1)

**Used by (10):** [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GatewayCheck](GatewayCheck.md), [JunctionCheck](JunctionCheck.md), [MapCheck](MapCheck.md), [PlanCheck](PlanCheck.md), [PortCheck](PortCheck.md), [RailDrawCheck](RailDrawCheck.md), [WellCheck](WellCheck.md)

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
| 47 | 502 | **type** `public class BuildingCatalog` | Reads the building definitions out of buildings.json. |
| 54 | 3 | `public String getSource()` |  |
| 62 | 44 | `public List<BuildingsTemplate> load()` | the caller should use its own defaults. |
| 108 | 42 | `private List<BuildingsTemplate> parse(Reader reader)` |  |
| 151 | 87 | `private BuildingsTemplate readBuilding(JsonObject o, Set<Integer> seenIds)` |  |
| 245 | 13 | `private void readOldMetal(JsonObject o, BuildingsTemplate template, String name)` | The city's old metal a Scrap Yard takes (0.7.123, batch I4): "oldMetal": {"STEEL": 10500}, tonnes a month at nameplate by metal. |
| 268 | 29 | `private void readSite(JsonObject o, BuildingsTemplate template, String name)` | The site a mine or a well stands on (0.7.110, batch I1): "site": {"resource": "IRON", "type": "ANY", "sites": 1, "dig": 1} - the resource whose sites it takes, the type of field it needs (a Deposit.FieldType of that r... |
| 306 | 12 | `private void readOffshore(JsonObject o, BuildingsTemplate template, String name)` | One of the oil buildings at sea (0.7.91, batch O10): "offshore": {"kind": "PLATFORM", "slots": 12}, {"kind": "WELL"} or {"kind": "PIPELINE", "onshore": 3000} - a platform's jacket and its wells' slots, a well in one, ... |
| 329 | 25 | `private void readPort(JsonObject o, BuildingsTemplate template, String name)` | A sea terminal's berth (0.7.86, batch O9): "port": {"cargo": "LIQUID", "tonnes": 3250000} - the kind of cargo it handles (Ports.Cargo) and its tonnes a year. |
| 368 | 31 | `private void readSector(JsonObject o, BuildingsTemplate template, String name)` | Who owns it and what it makes - see BuildingsTemplate's note. |
| 409 | 12 | `private void readCare(JsonObject o, BuildingsTemplate template, String name)` | The care type, if there is one. |
| 423 | 12 | `private void readTeaches(JsonObject o, BuildingsTemplate template, String name)` | What a school teaches. |
| 437 | 12 | `private void readSafety(JsonObject o, BuildingsTemplate template, String name)` | What a safety building does - POLICE or PRISON. |
| 451 | 12 | `private void readSource(JsonObject o, BuildingsTemplate template, String name)` | What a water works draws, FRESH or SEA (0.7.59). |
| 473 | 12 | `private void readRefinery(JsonObject o, BuildingsTemplate template, String name)` | A refinery's conversion unit (0.7.80, batch O4): its kind and the litres a month of its feed stream it takes at nameplate, |
| 492 | 12 | `private void readRequiresLicence(JsonObject o, BuildingsTemplate template, String name)` | The licence a building's practice is built on. |
| 505 | 17 | `private void readJobs(JsonObject o, BuildingsTemplate template, String name)` |  |
| 524 | 7 | `private double number(JsonObject o, String key)` | Missing or unreadable fields read as 0, so entries only list what they use. |
| 533 | 7 | `private boolean flag(JsonObject o, String key)` | A true/false key (FR7's "siding"): absent, null or not a boolean, false. |
| 541 | 7 | `private String string(JsonObject o, String key)` |  |

