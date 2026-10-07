# LuxuryRetail.java - 543 lines · 22 methods · 4 constants · sectors

`ham/citybuildersim/sectors/LuxuryRetail.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (3), [Markets](Markets.md) (2), [Trade](Trade.md) (2), [Game](Game.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingType](BuildingType.md) (1), [Retail](Retail.md) (1)

**Used by (5):** [EconomyManager](EconomyManager.md), [Game](Game.md), [LuxuryCounter](LuxuryCounter.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 74 | THE MARGIN, AND WHY IT IS A MULTIPLE RATHER THAN AN AMOUNT |
| 123 | WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG |
| 191 | THE SALE |
| 362 | PLANNING - the queue at a door that is not there |
| 480 | THE SCREEN |
| 518 | SAVE, RESET (0.7.43) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `LuxuryRetail.MARGIN_FLOOR` | `1.25` | What a shop charges over what the piece cost it when nobody would buy at its price: the mark-up at a position of nothing, and the lowest the margin can strike (0.7.19: the position is the buyers AT THE PRICE CHARGED -... |
| 106 | `LuxuryRetail.MARGIN_CEILING` | `4.0` | ...and what a shop charges as the buyers at its price outnumber its counters without limit: the position's limit of one. |
| 115 | `LuxuryRetail.MARGIN_SPEED` | `1.0 / 6` | The share of the way, in logs, a shop's charged margin moves toward the fixed point its buyers strike in a month: a sixth (0.7.43; spec-inflation.md 2.7). |
| 250 | `LuxuryRetail.MARGIN_STEPS` | `60` | Bisection steps for the margin's fixed point: 2.75 / 2^60 is far below a cent's grain on any landed cost. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 118 | `private double sellPrice` | What one shop's counter is worth a month, before anybody has told it anything. |
| 121 | `private double chargedMargin` | The margin the shops charge, carried month to month (0.7.43); NaN until the first strike, and on a save from before, which opens at the target. |
| 171 | `private double rMargin` | The month's reading, for the screen and the harness. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 472 | **type** `public class LuxuryRetail extends Sector` | The luxury shops. |

### THE MARGIN, AND WHY IT IS A MULTIPLE RATHER THAN AN AMOUNT (lines 74-122)

### WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG (lines 123-190)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 5 | `public static double landedCost(GoodsMarket wholesale)` | What one piece costs the shop to bring in: the local price when the city has luxuries on offer, the import price when it has none. |
| 173 | 17 | `public LuxuryRetail()` |  |

### THE SALE (lines 191-361)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 4 | `public long coverage()` | People the shops can serve a month, off their buildings. |
| 202 | 1 | `public double getMargin()` | The margin charged this month: a sixth of the way from last month's to the fixed point, in logs (0.7.43). |
| 204 | 1 | `public double getTargetMargin()` | The fixed point the buyers struck this month (strikeMargin()), which the charged margin chases. |
| 205 | 1 | `public double getWanted()` |  |
| 206 | 1 | `public double getServed()` |  |
| 207 | 1 | `public double getCoverage()` |  |
| 208 | 1 | `public double getLanded()` |  |
| 209 | 1 | `public double getSellPrice()` |  |
| 259 | 42 | `public double strikeMargin(Markets markets, java.util.function.DoubleUnaryOperator buyersAt)` | Strikes the margin against the buyers at the price it charges and returns what a piece will cost this month. |
| 303 | 5 | `private static double excess(java.util.function.DoubleUnaryOperator buyersAt, double landed, long cover, double margin)` | The rule's gap at a margin: the margin less what the buyers at its price would strike. |
| 319 | 18 | `public double serve(Markets markets, double pieces)` | ...and sells what the counter and the shelf can actually get through. |
| 339 | 1 | `public double onShelf()` | What the shops hold, in pieces. |
| 357 | 4 | `protected double recentUse(Good g)` | What to restock against, the month-one fallback included. |

### PLANNING - the queue at a door that is not there (lines 362-479)

| line | len | member | says |
|---:|---:|---|---|
| 390 | 62 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Builds against the customers who came AT ITS PRICE, not against a sales record (0.7.19: the buyers at the margin it struck - see strikeMargin() - where it read the queue at the floor price until then). |
| 469 | 10 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more counter would earn a month. |

### THE SCREEN (lines 480-517)

| line | len | member | says |
|---:|---:|---|---|
| 485 | 1 | `public boolean hasPlantBlock()` |  |
| 488 | 30 | `public List<Line> ownLines(Game game)` |  |

### SAVE, RESET (0.7.43) (lines 518-543)

| line | len | member | says |
|---:|---:|---|---|
| 524 | 4 | `protected void saveExtras(java.util.Map<String, Double> extras)` | The charged margin, which next month's strike moves from; not written while it is NaN (a fresh sector), since a save carries no NaN. |
| 531 | 6 | `protected void restoreExtras(java.util.Map<String, Double> extras)` | A save from before 0.7.43 has none, and the first strike opens at its fixed point. |
| 539 | 4 | `protected void resetExtras()` |  |

