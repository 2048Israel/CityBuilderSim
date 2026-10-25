# InvestCheck.java - 1,606 lines · 8 methods · 0 constants · harnesses

`ham/citybuildersim/InvestCheck.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessInvestment](BusinessInvestment.md) (66), [BuildingsTemplate](BuildingsTemplate.md) (32), [Game](Game.md) (26), [BuildingManager](BuildingManager.md) (25), [Sector](Sector.md) (20), [BuildingsStacks](BuildingsStacks.md) (20), [Construction](Construction.md) (15), [Good](Good.md) (9), [EconomyManager](EconomyManager.md) (8), [Sectors](Sectors.md) (6), [WageBand](WageBand.md) (5), [GameFiles](GameFiles.md) (4), [BuildingType](BuildingType.md) (4), [PopulationManager](PopulationManager.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (3), [PayTier](PayTier.md) (2), [GoodsMarket](GoodsMarket.md) (2), [JobType](JobType.md) (2), [MoneyAudit](MoneyAudit.md) (2), [TempDirs](TempDirs.md) (1), [Retail](Retail.md) (1), [Household](Household.md) (1), [FamilyStructure](FamilyStructure.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Mining](Mining.md) (1), [Founding](Founding.md) (1), [Markets](Markets.md) (1), [RealEstate](RealEstate.md) (1)

## Sections

| line | section |
|---:|---|
| 93 | · 1. the trend |
| 109 | · 2. lead time |
| 129 | · 3. real estate reads JOBS |
| 209 | · 4. retail |
| 307 | · 5. industry |
| 392 | · 6. THE BRAKE |
| 418 | · 6b. ON ITS WHOLE COST (0.7.113) |
| 421 | · 7. costing matches the build path |
| 437 | · 8. order sizing |
| 480 | · 9. construction expands itself |
| 558 | · 10. construction earns as it builds |
| 619 | · 11. an idle builder keeps a core crew, not full crews |
| 679 | · 12. land is the one thing that can say no |
| 826 | · 13. land is part of what a building costs |
| 845 | · 14. distress |
| 896 | · 15. every building gets the crew it can use |
| 988 | · 16. the landlords hold months of work |
| 1048 | · 17. the builders keep what their repairs and queue need |
| 1097 | · 18. a band nobody can fill (0.7.18) |
| 1160 | · 19. the builders' price (0.7.19) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 26 | `static int fails` |  |
| 27 | `static PrintStream out, quiet` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 24 | 1583 | **type** `public class InvestCheck` | Verifies the private investment engine: forecasting, the demand tests, and the brake. |
| 29 | 6 | `static void check(String label, double actual, double expected)` |  |
| 37 | 6 | `static void check(String label, double actual, double expected, double tolerance)` | The same, within a stated tolerance - for sums of shares, whose rounding scales with the output. |
| 44 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 49 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 54 | 14 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat. |
| 70 | 4 | `static void month(Game g)` | One month, quietly. |
| 75 | 1419 | `public static void main(String[] args) throws Exception` |  |
| 1510 | 96 | `static void wholeCost(Path root)` | ON ITS WHOLE COST (0.7.113, batch I2b; Jerus's decisions A and A13, "test every order on its whole cost, whoever pays"). |

