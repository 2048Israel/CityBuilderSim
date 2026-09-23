# LandCheck.java - 602 lines · 3 methods · 0 constants · harnesses

`ham/citybuildersim/LandCheck.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> Verifies the land ledger: what the city owns, what it can allocate, what it
> charges, and that the three numbers never drift apart.
> 
> The one thing worth being paranoid about here is that allocated land can only
> ever go up by exactly what was built. A leak in either direction is invisible
> for a hundred months and then the city is either mysteriously full or
> mysteriously infinite.

**Uses:** [LandManager](LandManager.md) (58), [LandParcel](LandParcel.md) (15), [LandMarket](LandMarket.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (3), [BuildingManager](BuildingManager.md) (2)

## Sections

| line | section |
|---:|---|
| 30 | · 1. what the city starts with |
| 42 | · 2. allocating |
| 63 | · 3. filling up |
| 75 | · 4. releasing |
| 84 | · 5. the listing |
| 124 | · 5b. buying one |
| 152 | · 5c. a bigger city pays more |
| 172 | · 5d. supply and demand inside the city |
| 248 | · 5e. iron in the ground |
| 292 | · 5f. parcels are blocks, and they grow |
| 441 | · 6. not affording it |
| 457 | · 7. selling |
| 481 | · 8. the player's price |
| 501 | · 9. reset |
| 516 | · 10. every building fits on a starting city |
| 568 | · 11. the price is a density policy |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 12 | 591 | **type** `public class LandCheck` | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. |
| 16 | 6 | `static void check(String label, double actual, double expected)` |  |
| 23 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 28 | 574 | `public static void main(String[] args)` |  |

