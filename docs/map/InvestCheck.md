# InvestCheck.java - 546 lines · 6 methods · 0 constants · harnesses

`ham/citybuildersim/InvestCheck.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> Verifies the private investment engine: forecasting, the demand tests, and
> the brake. Not part of the game.
> 
> REWRITTEN FOR THE SECTOR TEMPLATE (2026-09-11). The planners used to be
> four methods on BusinessInvestment that took the city's figures as
> arguments - planRealEstate(jobs, homes, burden, output, orders) - so the
> fixture fed them numbers. Each sector plans for itself now, off its own
> buildings and the city it is attached to (Sector.plan), and the numbers
> come from a real Game. So the fixture is a city put into a stated shape
> with instant builds, asked what it would do. The arithmetic the old
> fixture pinned - the trend, the lead time, the order size, the brake, the
> costing, the land cap - is still asked of BusinessInvestment directly.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (28), [BuildingsTemplate](BuildingsTemplate.md) (8), [Game](Game.md) (8), [BuildingManager](BuildingManager.md) (7), [Construction](Construction.md) (7), [Sector](Sector.md) (5), [EconomyManager](EconomyManager.md) (4), [BuildingType](BuildingType.md) (4), [Good](Good.md) (3), [GameFiles](GameFiles.md) (2), [GoodsMarket](GoodsMarket.md) (2), [BuildingsStacks](BuildingsStacks.md) (2), [Retail](Retail.md) (1)

## Sections

| line | section |
|---:|---|
| 83 | · 1. the trend |
| 99 | · 2. lead time |
| 119 | · 3. real estate reads JOBS |
| 160 | · 4. retail |
| 181 | · 5. industry |
| 231 | · 6. THE BRAKE |
| 256 | · 7. costing matches the build path |
| 265 | · 8. order sizing |
| 305 | · 9. construction expands itself |
| 343 | · 10. construction earns as it builds |
| 404 | · 11. idle payroll is floored, not full |
| 428 | · 12. land is the one thing that can say no |
| 476 | · 13. land is part of what a building costs |
| 492 | · 14. distress |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `static int fails` |  |
| 25 | `static PrintStream out, quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 525 | **type** `public class InvestCheck` | Verifies the private investment engine: forecasting, the demand tests, and the brake. |
| 27 | 6 | `static void check(String label, double actual, double expected)` |  |
| 34 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 39 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 44 | 14 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat. |
| 60 | 4 | `static void month(Game g)` | One month, quietly. |
| 65 | 481 | `public static void main(String[] args) throws Exception` |  |

