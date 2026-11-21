# InvestCheck.java - 2,092 lines · 20 methods · 1 constants · harnesses

`ham/citybuildersim/InvestCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessInvestment](BusinessInvestment.md) (93), [BuildingsTemplate](BuildingsTemplate.md) (43), [Game](Game.md) (37), [BuildingManager](BuildingManager.md) (35), [Sector](Sector.md) (22), [BuildingsStacks](BuildingsStacks.md) (21), [WageBand](WageBand.md) (18), [Construction](Construction.md) (16), [JobType](JobType.md) (15), [EconomyManager](EconomyManager.md) (12), [Good](Good.md) (9), [Sectors](Sectors.md) (8), [BuildAdvice](BuildAdvice.md) (7), [GameFiles](GameFiles.md) (6), [PopulationManager](PopulationManager.md) (6), [AutoBuilder](AutoBuilder.md) (6), [BuildingType](BuildingType.md) (5), [MoneyAudit](MoneyAudit.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Retail](Retail.md) (3), [PayTier](PayTier.md) (3), [Household](Household.md) (2), [FamilyStructure](FamilyStructure.md) (2), [GoodsMarket](GoodsMarket.md) (2), [TempDirs](TempDirs.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Mining](Mining.md) (1), [Founding](Founding.md) (1), [Markets](Markets.md) (1), [RealEstate](RealEstate.md) (1)... and 5 more

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
| 436 | · 8. order sizing |
| 490 | · 9. construction expands itself |
| 572 | · 10. construction earns as it builds |
| 633 | · 11. an idle builder keeps a core crew, not full crews |
| 693 | · 12. land is the one thing that can say no |
| 837 | · 13. land is part of what a building costs |
| 856 | · 14. distress |
| 907 | · 15. every building gets the crew it can use |
| 999 | · 16. the landlords' order is the gap, whatever is on site (BA1) |
| 1053 | · 17. the builders keep what their repairs and queue need |
| 1102 | · 18. a band nobody can fill (0.7.18) |
| 1165 | · 19. the builders' price (0.7.19) |
| 1662 | EVERY BUSINESS MAY BUILD AHEAD (BA1, 2026-11-02) |
| 1698 | · (a) a sector with its own order on site orders again, and counts it as supply |
| 1770 | · (b) the order's size and (c) the horizon |
| 1843 | · (d) the interest on the wait |
| 1919 | · (e) the wait counts |
| 1972 | · (f) the sector's own posts on site are spoken for |
| 2031 | · (h) how often it happens in a real run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 2034 | `InvestCheck.AHEAD_MONTHS` | `360` | Months the playtest's city is played with every order watched. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 26 | `static int fails` |  |
| 27 | `static PrintStream out, quiet` |  |
| 2037 | `final Game g` |  |
| 2038 | `int judged, cleared, clearedAhead` |  |
| 2039 | `long largestCleared` |  |
| 2040 | `String largestSector` |  |
| 2041 | `final java.util.Set<Integer> monthsJudged` |  |
| 2042 | `final java.util.Map<String, Integer> aheadBySector` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 2069 | **type** `public class InvestCheck` | Verifies the private investment engine: forecasting, the demand tests, and the brake. |
| 29 | 6 | `static void check(String label, double actual, double expected)` |  |
| 37 | 6 | `static void check(String label, double actual, double expected, double tolerance)` | The same, within a stated tolerance - for sums of shares, whose rounding scales with the output. |
| 44 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 49 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 54 | 14 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat. |
| 70 | 4 | `static void month(Game g)` | One month, quietly. |
| 75 | 1460 | `public static void main(String[] args) throws Exception` |  |
| 1555 | 106 | `static void wholeCost(Path root)` | ON ITS WHOLE COST (0.7.113, batch I2b; Jerus's decisions A and A13, "test every order on its whole cost, whoever pays"). |

### EVERY BUSINESS MAY BUILD AHEAD (BA1, 2026-11-02) (lines 1662-1697)

| line | len | member | says |
|---:|---:|---|---|
| 1681 | 8 | `static void buildAhead(Path root)` |  |
| 1691 | 6 | `static void clearSite(BuildingManager bm, BuildingsTemplate t)` | A fixture's site taken off the queue: its buildings under construction and their progress both nothing (the order is gone, as if cancelled). |

### (a) a sector with its own order on site orders again, and counts it as supply (lines 1698-1769)

| line | len | member | says |
|---:|---:|---|---|
| 1700 | 69 | `static void shopsOnSite(Path root)` |  |

### (b) the order's size and (c) the horizon (lines 1770-1842)

| line | len | member | says |
|---:|---:|---|---|
| 1772 | 70 | `static void sizeAndHorizon(Path root)` |  |

### (d) the interest on the wait (lines 1843-1918)

| line | len | member | says |
|---:|---:|---|---|
| 1845 | 73 | `static void waitCarries()` |  |

### (e) the wait counts (lines 1919-1971)

| line | len | member | says |
|---:|---:|---|---|
| 1921 | 50 | `static void theWaitCounts(Path root)` |  |

### (f) the sector's own posts on site are spoken for (lines 1972-2030)

| line | len | member | says |
|---:|---:|---|---|
| 1974 | 56 | `static void postsOnSite(Path root)` |  |

### (h) how often it happens in a real run (lines 2031-2092)

| line | len | member | says |
|---:|---:|---|---|
| 2036 | 38 | **type** `static final class AheadWatch implements BusinessInvestment.OrderWatch` |  |
| 2044 | 1 | `AheadWatch(Game g)` _(in InvestCheck.AheadWatch)_ |  |
| 2046 | 16 | `void saw(String sector, long quantity, long built, boolean mortgage)` _(in InvestCheck.AheadWatch)_ |  |
| 2064 | 3 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in InvestCheck.AheadWatch)_ |  |
| 2069 | 4 | `public void mortgaged(long asked, java.util.function.LongToDoubleFunction costOf, double cash, double noiPerUnit, double annual...` _(in InvestCheck.AheadWatch)_ |  |
| 2075 | 17 | `static void howOftenInARun(Path root)` |  |

