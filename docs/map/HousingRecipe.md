# HousingRecipe.java - 55 lines · 6 methods · 1 constants · model

`ham/citybuildersim/HousingRecipe.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The fixed dwelling mix and rights delivered by one residential structure.
> WHY (RES2): a structure, a home and its title used to be the same capacity
> counter. These are recipe classes, never individual homes or buildings.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (6):** [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [HousingInventory](HousingInventory.md), [HousingInventoryCheck](HousingInventoryCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 13 | `HousingRecipe.MAX_CLASSES` | `32` | Initial catalogue budget, independent of the population represented. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 45 | **type** `public record HousingRecipe(String form, List<Dwelling> mix, double sharedParking)` | The fixed dwelling mix and rights delivered by one residential structure. |
| 16 | 10 | **type** `public record Dwelling(String id, int count, int capacity, double area, int bedrooms, double quality, doubl...` | Fixed quantities per dwelling; weights allocate actual books, not new costs. |
| 19 | 6 | `{ ... }` _(in HousingRecipe.Dwelling)_ |  |
| 27 | 13 | `{ ... }` |  |
| 41 | 1 | `private static boolean positive(double n)` |  |
| 42 | 1 | `private static boolean nonnegative(double n)` |  |
| 44 | 1 | `public long doors()` |  |
| 45 | 1 | `public long capacity()` |  |
| 46 | 1 | `public double costWeight()` |  |
| 49 | 6 | `public static HousingRecipe compatible(BuildingsTemplate t)` | Compatibility metadata for hand-built/mod templates; no new form is activated. |

