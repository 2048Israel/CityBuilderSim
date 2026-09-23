# WaterCheck.java - 180 lines · 3 methods · 0 constants · harnesses

`ham/citybuildersim/WaterCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> Sanity harness for water production, demand, throttling and billing. Not part of the game.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (5), [BuildingManager](BuildingManager.md) (2), [ServicesManager](ServicesManager.md) (2), [Sector](Sector.md) (2), [UtilitiesHandler](UtilitiesHandler.md) (1), [FoodIndustry](FoodIndustry.md) (1), [Retail](Retail.md) (1)

## Sections

| line | section |
|---:|---|
| 37 | · 1. template draws |
| 45 | · 2. demand = buildings + people |
| 67 | · 3. the ratio bites |
| 83 | · 4. a plant fixes it |
| 95 | · 5. split books |
| 130 | · 6. the ratio throttles output |
| 144 | · 7. billing is symmetric with power |
| 169 | · 8. household affordability |

## Fields (state)

| line | field | says |
|---:|---|---|
| 6 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 4 | 177 | **type** `public class WaterCheck` | Sanity harness for water production, demand, throttling and billing. |
| 8 | 6 | `static void check(String label, double actual, double expected)` |  |
| 15 | 5 | `static double[] wages()` |  |
| 21 | 159 | `public static void main(String[] args)` |  |

