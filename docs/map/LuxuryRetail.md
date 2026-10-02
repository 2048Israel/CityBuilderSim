# LuxuryRetail.java - 493 lines · 18 methods · 3 constants · sectors

`ham/citybuildersim/sectors/LuxuryRetail.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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
| 111 | WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG |
| 179 | THE SALE |
| 337 | PLANNING - the queue at a door that is not there |
| 455 | THE SCREEN |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `LuxuryRetail.MARGIN_FLOOR` | `1.25` | What a shop charges over what the piece cost it when nobody would buy at its price: the mark-up at a position of nothing, and the lowest the margin can strike (0.7.19: the position is the buyers AT THE PRICE CHARGED -... |
| 106 | `LuxuryRetail.MARGIN_CEILING` | `4.0` | ...and what a shop charges as the buyers at its price outnumber its counters without limit: the position's limit of one. |
| 235 | `LuxuryRetail.MARGIN_STEPS` | `60` | Bisection steps for the margin's fixed point: 2.75 / 2^60 is far below a cent's grain on any landed cost. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 109 | `private double sellPrice` | What one shop's counter is worth a month, before anybody has told it anything. |
| 159 | `private double rMargin` | The month's reading, for the screen and the harness. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 72 | 422 | **type** `public class LuxuryRetail extends Sector` | The luxury shops. |

### THE MARGIN, AND WHY IT IS A MULTIPLE RATHER THAN AN AMOUNT (lines 74-110)

### WHAT A PIECE COSTS THE SHOP - AND THE SWING THAT READING THE WRONG (lines 111-178)

| line | len | member | says |
|---:|---:|---|---|
| 152 | 5 | `public static double landedCost(GoodsMarket wholesale)` | What one piece costs the shop to bring in: the local price when the city has luxuries on offer, the import price when it has none. |
| 161 | 17 | `public LuxuryRetail()` |  |

### THE SALE (lines 179-336)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 4 | `public int coverage()` | People the shops can serve a month, off their buildings. |
| 189 | 1 | `public double getMargin()` |  |
| 190 | 1 | `public double getWanted()` |  |
| 191 | 1 | `public double getServed()` |  |
| 192 | 1 | `public double getCoverage()` |  |
| 193 | 1 | `public double getLanded()` |  |
| 194 | 1 | `public double getSellPrice()` |  |
| 244 | 32 | `public double strikeMargin(Markets markets, java.util.function.DoubleUnaryOperator buyersAt)` | Strikes the margin against the buyers at the price it charges and returns what a piece will cost this month. |
| 278 | 5 | `private static double excess(java.util.function.DoubleUnaryOperator buyersAt, double landed, int cover, double margin)` | The rule's gap at a margin: the margin less what the buyers at its price would strike. |
| 294 | 18 | `public double serve(Markets markets, double pieces)` | ...and sells what the counter and the shelf can actually get through. |
| 314 | 1 | `public double onShelf()` | What the shops hold, in pieces. |
| 332 | 4 | `protected double recentUse(Good g)` | What to restock against, the month-one fallback included. |

### PLANNING - the queue at a door that is not there (lines 337-454)

| line | len | member | says |
|---:|---:|---|---|
| 365 | 62 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | Builds against the customers who came AT ITS PRICE, not against a sales record (0.7.19: the buyers at the margin it struck - see strikeMargin() - where it read the queue at the floor price until then). |
| 444 | 10 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one more counter would earn a month. |

### THE SCREEN (lines 455-493)

| line | len | member | says |
|---:|---:|---|---|
| 460 | 1 | `public boolean hasPlantBlock()` |  |
| 463 | 30 | `public List<Line> ownLines(Game game)` |  |

