# InvestCheck.java - 2,149 lines · 20 methods · 1 constants · harnesses

`ham/citybuildersim/InvestCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

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
> 
> FIXTURES (FS9). Each section stands its own town or city, put into its stated shape by the model's own doors. The builders' price (0.7.19)
> is read once the town has stood twelve months, with the wage schedule set by EconomyManager.updateWages() - the founding ladder over
> what it was by a stated rise - so the builders' index is the rise that was set, not the labour market's draw. The run of the
> playtest's city (section h, AHEAD_MONTHS) is PLAYED and COUNTED: how often a business built ahead in it is printed, and what is
> asserted is the identities over the run (the tally is consistent; no order clears for more than was asked), because that a
> business may build ahead is caused in its own section. WHY: the played city moved under both - its wages at month twelve, and the
> orders it judged in 360 months.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (93), [BuildingsTemplate](BuildingsTemplate.md) (45), [Game](Game.md) (38), [BuildingManager](BuildingManager.md) (36), [Sector](Sector.md) (22), [BuildingsStacks](BuildingsStacks.md) (21), [WageBand](WageBand.md) (18), [JobType](JobType.md) (17), [Construction](Construction.md) (16), [EconomyManager](EconomyManager.md) (13), [Good](Good.md) (9), [Sectors](Sectors.md) (8), [BuildAdvice](BuildAdvice.md) (7), [GameFiles](GameFiles.md) (6), [PopulationManager](PopulationManager.md) (6), [AutoBuilder](AutoBuilder.md) (6), [BuildingType](BuildingType.md) (5), [PayTier](PayTier.md) (4), [MoneyAudit](MoneyAudit.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Retail](Retail.md) (3), [Household](Household.md) (2), [FamilyStructure](FamilyStructure.md) (2), [GoodsMarket](GoodsMarket.md) (2), [BuildersShort](BuildersShort.md) (2), [TempDirs](TempDirs.md) (1), [HousingInventory](HousingInventory.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Mining](Mining.md) (1), [Founding](Founding.md) (1)... and 7 more

## Sections

| line | section |
|---:|---|
| 101 | · 1. the trend |
| 117 | · 2. lead time |
| 137 | · 3. real estate reads JOBS |
| 222 | · 4. retail |
| 320 | · 5. industry |
| 405 | · 6. THE BRAKE |
| 431 | · 6b. ON ITS WHOLE COST (0.7.113) |
| 434 | · 7. costing matches the build path |
| 449 | · 8. order sizing |
| 503 | · 9. construction expands itself |
| 585 | · 10. construction earns as it builds |
| 646 | · 11. an idle builder keeps a core crew, not full crews |
| 706 | · 12. land is the one thing that can say no |
| 857 | · 13. land is part of what a building costs |
| 876 | · 14. distress |
| 927 | · 15. every building gets the crew it can use |
| 1019 | · 16. the landlords' order is the gap, whatever is on site (BA1) |
| 1073 | · 17. the builders keep what their repairs and queue need |
| 1122 | · 18. a band nobody can fill (0.7.18) |
| 1185 | · 19. the builders' price (0.7.19) |
| 1714 | EVERY BUSINESS MAY BUILD AHEAD (BA1, 2026-11-02) |
| 1750 | · (a) a sector with its own order on site orders again, and counts it as supply |
| 1822 | · (b) the order's size and (c) the horizon |
| 1895 | · (d) the interest on the wait |
| 1971 | · (e) the wait counts |
| 2024 | · (f) the sector's own posts on site are spoken for |
| 2083 | · (h) how often it happens in a real run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 2086 | `InvestCheck.AHEAD_MONTHS` | `360` | Months the playtest's city is played with every order watched. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 34 | `static int fails` |  |
| 35 | `static PrintStream out, quiet` |  |
| 2089 | `final Game g` |  |
| 2090 | `int judged, cleared, clearedAhead, overAsked` |  |
| 2091 | `long largestCleared` |  |
| 2092 | `String largestSector` |  |
| 2093 | `final java.util.Set<Integer> monthsJudged` |  |
| 2094 | `final java.util.Map<String, Integer> aheadBySector` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 32 | 2118 | **type** `public class InvestCheck` | Verifies the private investment engine: forecasting, the demand tests, and the brake. |
| 37 | 6 | `static void check(String label, double actual, double expected)` |  |
| 45 | 6 | `static void check(String label, double actual, double expected, double tolerance)` | The same, within a stated tolerance - for sums of shares, whose rounding scales with the output. |
| 52 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 57 | 3 | `static BuildingsTemplate template(Game g, String name)` |  |
| 62 | 14 | `static Game city(Path root, String name)` | A fresh city, currency and world pinned, land by fiat. |
| 78 | 4 | `static void month(Game g)` | One month, quietly. |
| 83 | 1504 | `public static void main(String[] args) throws Exception` |  |
| 1607 | 106 | `static void wholeCost(Path root)` | ON ITS WHOLE COST (0.7.113, batch I2b; Jerus's decisions A and A13, "test every order on its whole cost, whoever pays"). |

### EVERY BUSINESS MAY BUILD AHEAD (BA1, 2026-11-02) (lines 1714-1749)

| line | len | member | says |
|---:|---:|---|---|
| 1733 | 8 | `static void buildAhead(Path root)` |  |
| 1743 | 6 | `static void clearSite(BuildingManager bm, BuildingsTemplate t)` | A fixture's site taken off the queue: its buildings under construction and their progress both nothing (the order is gone, as if cancelled). |

### (a) a sector with its own order on site orders again, and counts it as supply (lines 1750-1821)

| line | len | member | says |
|---:|---:|---|---|
| 1752 | 69 | `static void shopsOnSite(Path root)` |  |

### (b) the order's size and (c) the horizon (lines 1822-1894)

| line | len | member | says |
|---:|---:|---|---|
| 1824 | 70 | `static void sizeAndHorizon(Path root)` |  |

### (d) the interest on the wait (lines 1895-1970)

| line | len | member | says |
|---:|---:|---|---|
| 1897 | 73 | `static void waitCarries()` |  |

### (e) the wait counts (lines 1971-2023)

| line | len | member | says |
|---:|---:|---|---|
| 1973 | 50 | `static void theWaitCounts(Path root)` |  |

### (f) the sector's own posts on site are spoken for (lines 2024-2082)

| line | len | member | says |
|---:|---:|---|---|
| 2026 | 56 | `static void postsOnSite(Path root)` |  |

### (h) how often it happens in a real run (lines 2083-2149)

| line | len | member | says |
|---:|---:|---|---|
| 2088 | 39 | **type** `static final class AheadWatch implements BusinessInvestment.OrderWatch` |  |
| 2096 | 1 | `AheadWatch(Game g)` _(in InvestCheck.AheadWatch)_ |  |
| 2098 | 17 | `void saw(String sector, long quantity, long built, boolean mortgage)` _(in InvestCheck.AheadWatch)_ |  |
| 2117 | 3 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in InvestCheck.AheadWatch)_ |  |
| 2122 | 4 | `public void mortgaged(long asked, java.util.function.LongToDoubleFunction costOf, double cash, double noiPerUnit, double annual...` _(in InvestCheck.AheadWatch)_ |  |
| 2128 | 21 | `static void howOftenInARun(Path root)` |  |

