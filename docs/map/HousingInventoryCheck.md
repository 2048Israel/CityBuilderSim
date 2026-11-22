# HousingInventoryCheck.java - 209 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/HousingInventoryCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Aggregate housing inventory, caused through recipes, construction and transfers.
> WHY (RES2): title/listing quantities must conserve homes independently of the
> represented population, and a load must restore them without a second completion.

**Uses:** [HousingInventory](HousingInventory.md) (16), [BuildingManager](BuildingManager.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (4), [DataSave](DataSave.md) (4), [Game](Game.md) (4), [HousingRecipe](HousingRecipe.md) (3), [BuildingType](BuildingType.md) (2), [GameFiles](GameFiles.md) (2), [Sectors](Sectors.md) (1)

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |
| 15 | `static PrintStream out` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 13 | 197 | **type** `public final class HousingInventoryCheck` | Aggregate housing inventory, caused through recipes, construction and transfers. |
| 16 | 4 | `static void check(String label, boolean ok)` |  |
| 20 | 4 | `static void near(String label, double actual, double expected)` |  |
| 24 | 5 | `static void refused(String label, Runnable action)` |  |
| 29 | 10 | `static BuildingManager mixed(long count, boolean standing)` |  |
| 39 | 1 | `static HousingInventory.Key key(String dwelling)` |  |
| 40 | 5 | `static double sumOwned(HousingInventory h, HousingInventory.Key k)` |  |
| 45 | 5 | `static double sumOccupied(HousingInventory h, HousingInventory.Key k)` |  |
| 51 | 158 | `public static void main(String[] args) throws Exception` |  |

