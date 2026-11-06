# BuildingCatalog.java - 825 lines · 27 methods · 1 constants · model

`ham/citybuildersim/BuildingCatalog.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

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

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (28), [Good](Good.md) (18), [Equipment](Equipment.md) (7), [JobType](JobType.md) (7), [Recipe](Recipe.md) (6), [Ports](Ports.md) (4), [BuildingType](BuildingType.md) (2), [ResearchCatalog](ResearchCatalog.md) (2), [Resource](Resource.md) (2), [Deposit](Deposit.md) (2), [BridgeWorks](BridgeWorks.md) (2), [CareType](CareType.md) (1), [EducationType](EducationType.md) (1), [SafetyType](SafetyType.md) (1), [RefineryFlow](RefineryFlow.md) (1)

**Used by (20):** [BridgeMapCheck](BridgeMapCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [CrossingCheck](CrossingCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [EquipmentCheck](EquipmentCheck.md), [GatewayCheck](GatewayCheck.md), [IslandNetCheck](IslandNetCheck.md), [JunctionCheck](JunctionCheck.md), [MainLineCheck](MainLineCheck.md), [MapCheck](MapCheck.md), [PlanCheck](PlanCheck.md), [PortCheck](PortCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [RailDrawCheck](RailDrawCheck.md), [RecipeCheck](RecipeCheck.md), [ResearchCheck](ResearchCheck.md), [SlopeCheck](SlopeCheck.md), [WellCheck](WellCheck.md)

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
| 47 | 779 | **type** `public class BuildingCatalog` | Reads the building definitions out of buildings.json. |
| 54 | 3 | `public String getSource()` |  |
| 62 | 44 | `public List<BuildingsTemplate> load()` | the caller should use its own defaults. |
| 108 | 42 | `List<BuildingsTemplate> parse(Reader reader)` |  |
| 151 | 97 | `private BuildingsTemplate readBuilding(JsonObject o, Set<Integer> seenIds)` |  |
| 253 | 6 | `private void readSince(BuildingsTemplate template, String name)` | When a building first existed (R2): "since" is a year (1902) or a code of research.json ("PE4"), read by BuildingsTemplate.setSince(). |
| 266 | 13 | `private void readOldMetal(JsonObject o, BuildingsTemplate template, String name)` | The city's old metal a Scrap Yard takes (0.7.123, batch I4): "oldMetal": {"STEEL": 10500}, tonnes a month at nameplate by metal. |
| 291 | 63 | `private void readEquipment(JsonObject o, BuildingsTemplate template, String name)` | The equipment a building can be fitted with (R1, 2026-11-05; Equipment): "equipment": [{"id": "electric-drive", "name": "Electric motor drive", "since": "1900", "ends": "STEAM", "buys": {"MACHINERY": 10.9}, "work": 11... |
| 356 | 13 | `private String sinceText(JsonObject p)` | A "since" (a building's, a piece's or a recipe's) as text: a whole JSON number is its digits ("1900"), a string as written ("PE14"), absent or anything else empty. |
| 381 | 46 | `private void readRecipes(JsonObject o, BuildingsTemplate template, String name)` | The ways a building runs (R1c, 2026-11-05; Recipe; research-design 2.1): "recipes": [{"id": "old", "name": "Beehive ovens"}, {"id": "by-product", "since": 1905, "needsEquipment": "by-product-ovens", "uses": {"COKING_C... |
| 429 | 59 | `private Recipe readRecipe(JsonObject p, String id, BuildingsTemplate template, String name)` | One recipe of a list, its unstated figures the template's top level; null (reported) for one that is no good. |
| 490 | 18 | `private java.util.Map<Good, Double> recipeGoods(JsonObject p, String key, java.util.Map<Good, Double> inherited, String id, Str...` | What a recipe makes or uses: the template's own when the recipe does not state the key, else what it states (an empty object is nothing); null, reported, for an unknown good or a key that is not an object. |
| 510 | 13 | `private void dropIdlePieces(BuildingsTemplate template, String name)` | A piece that ends no form is there to enable a recipe: one that no recipe of the building needs is reported and dropped, once the recipes are read. |
| 533 | 29 | `private void readSite(JsonObject o, BuildingsTemplate template, String name)` | The site a mine or a well stands on (0.7.110, batch I1): "site": {"resource": "IRON", "type": "ANY", "sites": 1, "dig": 1} - the resource whose sites it takes, the type of field it needs (a Deposit.FieldType of that r... |
| 571 | 12 | `private void readOffshore(JsonObject o, BuildingsTemplate template, String name)` | One of the oil buildings at sea (0.7.91, batch O10): "offshore": {"kind": "PLATFORM", "slots": 12}, {"kind": "WELL"} or {"kind": "PIPELINE", "onshore": 3000} - a platform's jacket and its wells' slots, a well in one, ... |
| 594 | 25 | `private void readPort(JsonObject o, BuildingsTemplate template, String name)` | A sea terminal's berth (0.7.86, batch O9): "port": {"cargo": "LIQUID", "tonnes": 30000000} - the kind of cargo it handles (Ports.Cargo) and its tonnes a year. |
| 633 | 31 | `private void readSector(JsonObject o, BuildingsTemplate template, String name)` | Who owns it and what it makes - see BuildingsTemplate's note. |
| 674 | 12 | `private void readCare(JsonObject o, BuildingsTemplate template, String name)` | The care type, if there is one. |
| 688 | 12 | `private void readTeaches(JsonObject o, BuildingsTemplate template, String name)` | What a school teaches. |
| 702 | 12 | `private void readSafety(JsonObject o, BuildingsTemplate template, String name)` | What a safety building does - POLICE or PRISON. |
| 716 | 12 | `private void readSource(JsonObject o, BuildingsTemplate template, String name)` | What a water works draws, FRESH or SEA (0.7.59). |
| 738 | 12 | `private void readRefinery(JsonObject o, BuildingsTemplate template, String name)` | A refinery's conversion unit (0.7.80, batch O4): its kind and the litres a month of its feed stream it takes at nameplate, |
| 757 | 12 | `private void readRequiresLicence(JsonObject o, BuildingsTemplate template, String name)` | The licence a building's practice is built on. |
| 770 | 17 | `private void readJobs(JsonObject o, BuildingsTemplate template, String name)` |  |
| 789 | 7 | `private double number(JsonObject o, String key)` | Missing or unreadable fields read as 0, so entries only list what they use. |
| 798 | 10 | `private BridgeWorks.Kind bridgeKind(JsonObject o, String name)` | A long bridge's kind (ET6's "bridge": "HIGHWAY", "RAIL" or "ROAD"): absent, null; one the game does not know is reported and read as none. |
| 810 | 7 | `private boolean flag(JsonObject o, String key)` | A true/false key (FR7's "siding"): absent, null or not a boolean, false. |
| 818 | 7 | `private String string(JsonObject o, String key)` |  |

