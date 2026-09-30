# InvestCheck.java - 1,386 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/InvestCheck.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessInvestment](BusinessInvestment.md) (44), [BuildingsTemplate](BuildingsTemplate.md) (28), [BuildingManager](BuildingManager.md) (27), [Sector](Sector.md) (20), [BuildingsStacks](BuildingsStacks.md) (20), [Game](Game.md) (18), [Construction](Construction.md) (15), [Good](Good.md) (9), [EconomyManager](EconomyManager.md) (8), [WageBand](WageBand.md) (5), [GameFiles](GameFiles.md) (4), [BuildingType](BuildingType.md) (4), [PopulationManager](PopulationManager.md) (4), [GoodsMarket](GoodsMarket.md) (2), [JobType](JobType.md) (2), [MoneyAudit](MoneyAudit.md) (2), [Retail](Retail.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Sectors](Sectors.md) (1), [Founding](Founding.md) (1), [PayTier](PayTier.md) (1), [Markets](Markets.md) (1), [RealEstate](RealEstate.md) (1)

## Sections

| line | section |
|---:|---|
| 91 | · 1. the trend |
| 107 | · 2. lead time |
| 127 | · 3. real estate reads JOBS |
| 186 | · 4. retail |
| 226 | · 5. industry |
| 288 | · 6. THE BRAKE |
| 313 | · 7. costing matches the build path |
| 329 | · 8. order sizing |
| 372 | · 9. construction expands itself |
| 450 | · 10. construction earns as it builds |
| 511 | · 11. an idle builder keeps a core crew, not full crews |
| 571 | · 12. land is the one thing that can say no |
| 718 | · 13. land is part of what a building costs |
| 737 | · 14. distress |
| 788 | · 15. every building gets the crew it can use |
| 880 | · 16. the landlords hold months of work |
| 940 | · 17. the builders keep what their repairs and queue need |
| 989 | · 18. a band nobody can fill (0.7.18) |
| 1052 | · 19. the builders' price (0.7.19) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 24 | `static int fails` |  |
| 25 | `static PrintStream out, quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 1365 | **type** `public class InvestCheck` | Verifies the private investment engine: forecasting, the demand tests, and the brake. |
| 27 | 6 | `static void check(String label, double actual, double expected)` |  |
| 35 | 6 | `static void check(String label, double actual, double expected, double tolerance)` | The same, within a stated tolerance - for sums of shares, whose rounding scales with the output. |
| 42 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 47 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 52 | 14 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat. |
| 68 | 4 | `static void month(Game g)` | One month, quietly. |
| 73 | 1313 | `public static void main(String[] args) throws Exception` |  |

