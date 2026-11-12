# InvestCheck.java - 2,111 lines · 21 methods · 1 constants · harnesses

`ham/citybuildersim/InvestCheck.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

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

**Uses:** [BusinessInvestment](BusinessInvestment.md) (103), [BuildingsTemplate](BuildingsTemplate.md) (43), [Game](Game.md) (37), [BuildingManager](BuildingManager.md) (35), [Sector](Sector.md) (23), [BuildingsStacks](BuildingsStacks.md) (21), [WageBand](WageBand.md) (18), [Construction](Construction.md) (16), [JobType](JobType.md) (15), [EconomyManager](EconomyManager.md) (14), [Good](Good.md) (9), [Sectors](Sectors.md) (8), [BuildAdvice](BuildAdvice.md) (7), [GameFiles](GameFiles.md) (6), [PopulationManager](PopulationManager.md) (6), [AutoBuilder](AutoBuilder.md) (6), [BuildingType](BuildingType.md) (5), [MoneyAudit](MoneyAudit.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Retail](Retail.md) (3), [PayTier](PayTier.md) (3), [LongPlaytest](LongPlaytest.md) (3), [Household](Household.md) (2), [FamilyStructure](FamilyStructure.md) (2), [GoodsMarket](GoodsMarket.md) (2), [TempDirs](TempDirs.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Mining](Mining.md) (1), [Founding](Founding.md) (1), [Markets](Markets.md) (1)... and 5 more

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
| 909 | · 15. every building gets the crew it can use |
| 1001 | · 16. the landlords' order is the gap, whatever is on site (BA1) |
| 1055 | · 17. the builders keep what their repairs and queue need |
| 1104 | · 18. a band nobody can fill (0.7.18) |
| 1167 | · 19. the builders' price (0.7.19) |
| 1681 | EVERY BUSINESS MAY BUILD AHEAD (BA1, 2026-11-02) |
| 1717 | · (a) a sector with its own order on site orders again, and counts it as supply |
| 1789 | · (b) the order's size and (c) the horizon |
| 1862 | · (d) the interest on the wait |
| 1938 | · (e) the wait counts |
| 1991 | · (f) the sector's own posts on site are spoken for |
| 2050 | · (h) how often it happens in a real run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 2053 | `InvestCheck.AHEAD_MONTHS` | `360` | Months the playtest's city is played with every order watched. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 26 | `static int fails` |  |
| 27 | `static PrintStream out, quiet` |  |
| 2056 | `final Game g` |  |
| 2057 | `int judged, cleared, clearedAhead` |  |
| 2058 | `int largestCleared` |  |
| 2059 | `String largestSector` |  |
| 2060 | `final java.util.Set<Integer> monthsJudged` |  |
| 2061 | `final java.util.Map<String, Integer> aheadBySector` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 24 | 2088 | **type** `public class InvestCheck` | Verifies the private investment engine: forecasting, the demand tests, and the brake. |
| 29 | 6 | `static void check(String label, double actual, double expected)` |  |
| 37 | 6 | `static void check(String label, double actual, double expected, double tolerance)` | The same, within a stated tolerance - for sums of shares, whose rounding scales with the output. |
| 44 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 49 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 54 | 14 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat. |
| 70 | 4 | `static void month(Game g)` | One month, quietly. |
| 75 | 1443 | `public static void main(String[] args) throws Exception` |  |
| 1520 | 34 | `static void retirementPace()` | A modest surplus paces the excess and the watch counts the full trailing year (RT3). |
| 1574 | 106 | `static void wholeCost(Path root)` | ON ITS WHOLE COST (0.7.113, batch I2b; Jerus's decisions A and A13, "test every order on its whole cost, whoever pays"). |

### EVERY BUSINESS MAY BUILD AHEAD (BA1, 2026-11-02) (lines 1681-1716)

| line | len | member | says |
|---:|---:|---|---|
| 1700 | 8 | `static void buildAhead(Path root)` |  |
| 1710 | 6 | `static void clearSite(BuildingManager bm, BuildingsTemplate t)` | A fixture's site taken off the queue: its buildings under construction and their progress both nothing (the order is gone, as if cancelled). |

### (a) a sector with its own order on site orders again, and counts it as supply (lines 1717-1788)

| line | len | member | says |
|---:|---:|---|---|
| 1719 | 69 | `static void shopsOnSite(Path root)` |  |

### (b) the order's size and (c) the horizon (lines 1789-1861)

| line | len | member | says |
|---:|---:|---|---|
| 1791 | 70 | `static void sizeAndHorizon(Path root)` |  |

### (d) the interest on the wait (lines 1862-1937)

| line | len | member | says |
|---:|---:|---|---|
| 1864 | 73 | `static void waitCarries()` |  |

### (e) the wait counts (lines 1938-1990)

| line | len | member | says |
|---:|---:|---|---|
| 1940 | 50 | `static void theWaitCounts(Path root)` |  |

### (f) the sector's own posts on site are spoken for (lines 1991-2049)

| line | len | member | says |
|---:|---:|---|---|
| 1993 | 56 | `static void postsOnSite(Path root)` |  |

### (h) how often it happens in a real run (lines 2050-2111)

| line | len | member | says |
|---:|---:|---|---|
| 2055 | 38 | **type** `static final class AheadWatch implements BusinessInvestment.OrderWatch` |  |
| 2063 | 1 | `AheadWatch(Game g)` _(in InvestCheck.AheadWatch)_ |  |
| 2065 | 16 | `void saw(String sector, int quantity, int built, boolean mortgage)` _(in InvestCheck.AheadWatch)_ |  |
| 2083 | 3 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in InvestCheck.AheadWatch)_ |  |
| 2088 | 4 | `public void mortgaged(int asked, java.util.function.IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRa...` _(in InvestCheck.AheadWatch)_ |  |
| 2094 | 17 | `static void howOftenInARun(Path root)` |  |

