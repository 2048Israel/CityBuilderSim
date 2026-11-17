# LuxuryRetail.java - 586 lines · 24 methods · 6 constants · sectors

`ham/citybuildersim/sectors/LuxuryRetail.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

> The luxury shops. THE FOURTEENTH SECTOR (2026-09-17, Jerus's call).
> 
> Jerus: "lets add restuarants as well as luxury stores, these two will absorb
> some spending as well." And, on how they should price: "supply v demand,
> thats the most important thing."
> 
> =========================================================================
> WHY THIS EXISTS, WHICH IS A MEASUREMENT AND NOT A THEME
> =========================================================================
> 
> Until today a household in this game could buy exactly four things: food, a
> home, a car, and the city's fees. Food is capped by APPETITE - Consumption
> scales every basket by the lesser of what a household can afford and what it
> can eat - and a home is one home. So a city whose ordinary unskilled couple
> earns two hundred and sixty times what subsistence costs had nowhere to put
> the other ninety-four per cent of its money.
> 
> It saved it. Household net worth reached FIVE THOUSAND MONTHS of the city's
> entire output and was still climbing at the end of every run. Three separate
> attempts to fix that inside the consumption function - paying the foreign
> coupon home, letting investment income into the plan, a wealth term in the
> plan itself - moved it and did not stop it, because a drain cannot empty a
> sealed pipe. The pipe is sealed here.
> 
> =========================================================================
> THE SCARCE THING IS THE SHOP, NOT THE WATCH
> =========================================================================
> 
> The world has no shortage of watches: LUXURIES is importable at a world
> price and the city can have as many as it will pay for. What a city can be
> short of is somewhere to buy one - a counter, a Tuesday, somebody to serve
> you - and that is a thing a PLAYER BUILDS.
> 
> So the margin is what gets struck, on the same rule GoodsMarket.strike()
> uses and the second-hand car market before it: a floor, a ceiling, and a
> position between them off demand against supply. Which closes the loop
> Jerus asked for:
> 
>     crowded shops -> the margin widens -> luxury retail is worth building
>       -> the investor builds boutiques -> coverage rises -> the margin falls
> 
> A city that will not build shops cannot spend its money, and will see
> exactly why on the sector's own screen. That is the mechanic; the absorption
> is what it is for.
> 
> =========================================================================
> AND IT IS AN IMPORT, DELIBERATELY
> =========================================================================
> 
> The money LEAVES rather than circulating. A rich city finally runs a
> current-account deficit instead of the three-century surplus that four
> addenda of `why-there-is-no-inflation.md` could not shift - and the hoard
> falls rather than going round again. If a domestic maker ever appears, that
> is import substitution, and it is something a player should have to earn.

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (10), [BuildingsTemplate](BuildingsTemplate.md) (3), [Markets](Markets.md) (2), [Trade](Trade.md) (2), [Game](Game.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingType](BuildingType.md) (1), [Retail](Retail.md) (1)

**Used by (6):** [EconomyManager](EconomyManager.md), [Game](Game.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetailCheck](LuxuryRetailCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 74 | THE MARGIN, AND WHY IT IS A MULTIPLE RATHER THAN AN AMOUNT |
| 117 | WHAT A POST SELLS (LX1, 2026-11-17) |
| 155 | WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG |
| 223 | THE SALE |
| 406 | PLANNING - the queue at a door that is not there |
| 519 | THE SCREEN |
| 561 | SAVE, RESET (0.7.43) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `LuxuryRetail.MARGIN_FLOOR` | `1.25` | What a shop charges over what the piece cost it when nobody would buy at its price: the mark-up at a position of nothing, and the lowest the margin can strike (0.7.19: the position is the buyers AT THE PRICE CHARGED -... |
| 106 | `LuxuryRetail.MARGIN_CEILING` | `4.0` | ...and what a shop charges as the buyers at its price outnumber its counters without limit: the position's limit of one. |
| 115 | `LuxuryRetail.MARGIN_SPEED` | `1.0 / 6` | The share of the way, in logs, a shop's charged margin moves toward the fixed point its buyers strike in a month: a sixth (0.7.43; spec-inflation.md 2.7). |
| 144 | `LuxuryRetail.LABOUR_SHARE` | `.417` | BEA's compensation of employees over value added for other retail (4A0), the mean of 2023-2025 (LS1's bea-compensation-share.csv): what a luxury shop's payroll is of its margin less power, water and upkeep, the share ... |
| 147 | `LuxuryRetail.LABOUR_SHARE_FACTOR` | `2` | ...and how far either way of it a shop's payroll over its value added may read and still be on BEA's mark: a factor of two, what a piece's landed cost over the mean wage alone moves through one run (1.6 to 3.4 mean mo... |
| 294 | `LuxuryRetail.MARGIN_STEPS` | `60` | Bisection steps for the margin's fixed point: 2.75 / 2^60 is far below a cent's grain on any landed cost. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 150 | `private double sellPrice` | What one shop's counter is worth a month, before anybody has told it anything. |
| 153 | `private double chargedMargin` | The margin the shops charge, carried month to month (0.7.43); NaN until the first strike, and on a save from before, which opens at the target. |
| 203 | `private double rMargin` | The month's reading, for the screen and the harness. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 515 | **type** `public class LuxuryRetail extends Sector` | The luxury shops. |

### THE MARGIN, AND WHY IT IS A MULTIPLE RATHER THAN AN AMOUNT (lines 74-116)

### WHAT A POST SELLS (LX1, 2026-11-17) (lines 117-154)

### WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG (lines 155-222)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 5 | `public static double landedCost(GoodsMarket wholesale)` | What one piece costs the shop to bring in: the local price when the city has luxuries on offer, the import price when it has none. |
| 205 | 17 | `public LuxuryRetail()` |  |

### THE SALE (lines 223-405)

| line | len | member | says |
|---:|---:|---|---|
| 228 | 4 | `public long coverage()` | People the shops can serve a month, off their buildings. |
| 240 | 4 | `public long coverageOnSite()` | ...and the people the shops on site will serve once they open (BA1, 2026-11-02): plan() counted the counters standing only while a sector could have one order on site; with no cap on orders it would order the same que... |
| 246 | 1 | `public double getMargin()` | The margin charged this month: a sixth of the way from last month's to the fixed point, in logs (0.7.43). |
| 248 | 1 | `public double getTargetMargin()` | The fixed point the buyers struck this month (strikeMargin()), which the charged margin chases. |
| 249 | 1 | `public double getWanted()` |  |
| 250 | 1 | `public double getServed()` |  |
| 251 | 1 | `public double getCoverage()` |  |
| 252 | 1 | `public double getLanded()` |  |
| 253 | 1 | `public double getSellPrice()` |  |
| 303 | 42 | `public double strikeMargin(Markets markets, java.util.function.DoubleUnaryOperator buyersAt)` | Strikes the margin against the buyers at the price it charges and returns what a piece will cost this month. |
| 347 | 5 | `private static double excess(java.util.function.DoubleUnaryOperator buyersAt, double landed, long cover, double margin)` | The rule's gap at a margin: the margin less what the buyers at its price would strike. |
| 363 | 18 | `public double serve(Markets markets, double pieces)` | ...and sells what the counter and the shelf can actually get through. |
| 383 | 1 | `public double onShelf()` | What the shops hold, in pieces. |
| 401 | 4 | `protected double recentUse(Good g)` | What to restock against, the month-one fallback included. |

### PLANNING - the queue at a door that is not there (lines 406-518)

| line | len | member | says |
|---:|---:|---|---|
| 434 | 57 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Builds against the customers who came AT ITS PRICE, not against a sales record (0.7.19: the buyers at the margin it struck - see strikeMargin() - where it read the queue at the floor price until then). |
| 508 | 10 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more counter would earn a month. |

### THE SCREEN (lines 519-560)

| line | len | member | says |
|---:|---:|---|---|
| 525 | 1 | `public ham.citybuildersim.SectorStatements.Format statementFormat()` | Its formal statements' format (0.7.74, spec-sector-statements 4.6): a merchant, whose middle line is its gross margin. |
| 528 | 1 | `public boolean hasPlantBlock()` |  |
| 531 | 30 | `public List<Line> ownLines(Game game)` |  |

### SAVE, RESET (0.7.43) (lines 561-586)

| line | len | member | says |
|---:|---:|---|---|
| 567 | 4 | `protected void saveExtras(java.util.Map<String, Double> extras)` | The charged margin, which next month's strike moves from; not written while it is NaN (a fresh sector), since a save carries no NaN. |
| 574 | 6 | `protected void restoreExtras(java.util.Map<String, Double> extras)` | A save from before 0.7.43 has none, and the first strike opens at its fixed point. |
| 582 | 4 | `protected void resetExtras()` |  |

