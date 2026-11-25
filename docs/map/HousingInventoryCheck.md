# HousingInventoryCheck.java - 288 lines · 11 methods · 0 constants · harnesses

`ham/citybuildersim/HousingInventoryCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Aggregate housing inventory, caused through recipes, construction and transfers.
> WHY (RES2): title/listing quantities must conserve homes independently of the
> represented population, and a load must restore them without a second completion.

**Uses:** [HousingInventory](HousingInventory.md) (20), [BuildingManager](BuildingManager.md) (11), [Game](Game.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [HousingRecipe](HousingRecipe.md) (6), [DataSave](DataSave.md) (4), [BuildingType](BuildingType.md) (3), [Sectors](Sectors.md) (2), [GameFiles](GameFiles.md) (2)

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |
| 15 | `static PrintStream out` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 13 | 276 | **type** `public final class HousingInventoryCheck` | Aggregate housing inventory, caused through recipes, construction and transfers. |
| 16 | 4 | `static void check(String label, boolean ok)` |  |
| 20 | 4 | `static void near(String label, double actual, double expected)` |  |
| 24 | 5 | `static void refused(String label, Runnable action)` |  |
| 29 | 10 | `static BuildingManager mixed(long count, boolean standing)` |  |
| 39 | 1 | `static HousingInventory.Key key(String dwelling)` |  |
| 40 | 5 | `static double sumOwned(HousingInventory h, HousingInventory.Key k)` |  |
| 45 | 5 | `static double sumOccupied(HousingInventory h, HousingInventory.Key k)` |  |
| 51 | 22 | `static void equalSizeMatch()` |  |
| 74 | 27 | `static java.util.Map<String, Double> figures(Game city)` |  |
| 102 | 7 | `static void twins(String when, Game live, Game loaded)` |  |
| 110 | 178 | `public static void main(String[] args) throws Exception` |  |

