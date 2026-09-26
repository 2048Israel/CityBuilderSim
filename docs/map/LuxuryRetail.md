# LuxuryRetail.java - 408 lines · 16 methods · 2 constants · sectors

`ham/citybuildersim/sectors/LuxuryRetail.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (12), [BusinessInvestment](BusinessInvestment.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (3), [Markets](Markets.md) (2), [Trade](Trade.md) (2), [Game](Game.md) (2), [Formats](Formats.md) (2), [Sector](Sector.md) (1), [GoodsMarket](GoodsMarket.md) (1), [BuildingType](BuildingType.md) (1)

**Used by (3):** [EconomyManager](EconomyManager.md), [LuxuryCounter](LuxuryCounter.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 74 | THE MARGIN, AND WHY IT IS A MULTIPLE RATHER THAN AN AMOUNT |
| 101 | WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG |
| 169 | THE SALE |
| 275 | PLANNING - the queue at a door that is not there |
| 374 | THE SCREEN |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 86 | `LuxuryRetail.MARGIN_FLOOR` | `1.25` | What a shop with nobody in it charges over what the piece cost it. |
| 96 | `LuxuryRetail.MARGIN_CEILING` | `4.0` | ...and what a shop with a queue charges. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 99 | `private double sellPrice` | What one shop's counter is worth a month, before anybody has told it anything. |
| 149 | `private double rMargin` | The month's reading, for the screen and the harness. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 337 | **type** `public class LuxuryRetail extends Sector` | The luxury shops. |

### THE MARGIN, AND WHY IT IS A MULTIPLE RATHER THAN AN AMOUNT (lines 74-100)

### WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG (lines 101-168)

| line | len | member | says |
|---:|---:|---|---|
| 142 | 5 | `public static double landedCost(GoodsMarket wholesale)` | What one piece costs the shop to bring in: the local price when the city has luxuries on offer, the import price when it has none. |
| 151 | 17 | `public LuxuryRetail()` |  |

### THE SALE (lines 169-274)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 4 | `public int coverage()` | People the shops can serve a month, off their buildings. |
| 179 | 1 | `public double getMargin()` |  |
| 180 | 1 | `public double getWanted()` |  |
| 181 | 1 | `public double getServed()` |  |
| 182 | 1 | `public double getCoverage()` |  |
| 183 | 1 | `public double getLanded()` |  |
| 184 | 1 | `public double getSellPrice()` |  |
| 199 | 22 | `public double strikeMargin(Markets markets, double wanted)` | Strikes the margin against the queue and returns what a piece will cost this month. |
| 232 | 18 | `public double serve(Markets markets, double pieces)` | ...and sells what the counter and the shelf can actually get through. |
| 252 | 1 | `public double onShelf()` | What the shops hold, in pieces. |
| 270 | 4 | `protected double recentUse(Good g)` | What to restock against, the month-one fallback included. |

### PLANNING - the queue at a door that is not there (lines 275-373)

| line | len | member | says |
|---:|---:|---|---|
| 300 | 46 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Builds against the customers who CAME, not against a sales record. |
| 363 | 10 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more counter would earn a month. |

### THE SCREEN (lines 374-408)

| line | len | member | says |
|---:|---:|---|---|
| 379 | 29 | `public List<Line> operations(Game game)` |  |

