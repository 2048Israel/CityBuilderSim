# GoodsMarket.java - 466 lines · 47 methods · 3 constants · model

`ham/citybuildersim/GoodsMarket.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> Where one good clears between whoever makes it and whoever wants it.
> 
> THE GENERALISATION OF FoodMarket AND IronMarket, which it replaces. Both of
> those priced a month off scarcity inside a band the world set; they
> differed in the shape of the band and the formula in it, and the ore
> market's version won on both counts:
> 
>     floor    = the export price   (what a seller gets shipping it out)
>     ceiling  = the import price   (what a buyer pays bringing one in)
>     price    = floor + (ceiling - floor) x demand / (demand + supply)
> 
> The formula is symmetric and needs no clamping: a matched pair splits the
> difference and both make money, which is the property the ore band was
> designed around and the food market never had - it priced a balanced
> market at the buyer's worst price, and floored itself at a quarter of the
> ceiling while the mills were already exporting at six tenths of it. One
> rule for every good now; if the eight seeds say food needs its old floor
> back it becomes a per-good setting, not a second market (Jerus: "onto the
> band, measure").
> 
> SUPPLY IS FLOW PLUS A SLICE OF THE STOCK, not the raw stockpile. Pricing
> off inventory alone is circular - a low price makes a maker withhold,
> withholding grows the stock, the bigger stock reads as more supply and
> pushes the price down again - and measured that way the food price sat on
> its floor in every scenario including an actual shortage. A warehouse can
> be drawn down, but not all at once: a sixth of it a month counts.
> 
> A GOOD WITH NO CEILING - one the world will not sell the city - has none
> from this class either; it is priced between the floor and twice the
> floor, which is a guess that matters for no good in the game today (steel
> is the only one, and nothing in the city buys steel). A good with no floor
> either is a seller-priced good and never comes here; see Good.Pricing.
> 
> THE ALLOCATION IS IN Markets, which can see every seller's offer and every
> buyer's bid at once. This class is one price and one month's tally.
> 
> NOTHING HERE MOVES MONEY. It strikes a price, records what was filled, and
> the sectors' own ledgers carry the trades to their statements.

**Uses:** [Trade](Trade.md) (5), [Good](Good.md) (3)

**Used by (26):** [AgricultureCheck](AgricultureCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [EconomyManager](EconomyManager.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [HistoryScreen](HistoryScreen.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MiningCheck](MiningCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Retail](Retail.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [VanCheck](VanCheck.md)

## Sections

| line | section |
|---:|---|
| 65 | · the last strike |
| 92 | · the month |
| 122 | WHAT IT COSTS TO MOVE ONE, THIS MONTH (2026-09-16) |
| 217 | ...AND WHAT THE SHIPPER IS ACTUALLY LEFT WITH |
| 382 | · readers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `GoodsMarket.STOCK_RELEASE_MONTHS` | `6` | How many months it would take to release the whole stockpile into the market. |
| 55 | `GoodsMarket.NO_CEILING_MULTIPLE` | `2` | Where the price sits in a band with no ceiling: up to this multiple of the floor. |
| 90 | `GoodsMarket.TREND_MONTHS` | `36` | The longest window any good plans over - see Good.planningMonths() for how many of these months a good actually reads. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private final Good good` |  |
| 60 | `private double exchangeRate` | City money per world unit - the exchange rate times the world's price level. |
| 63 | `private double localPrice` | The price the month trades at, in the city's money. |
| 66 | `private double rSupplyFlow` |  |
| 67 | `private double rSupplyStock` |  |
| 68 | `private double rDemand` |  |
| 83 | `private final double[] taken` | What the city has been taking of this good, month by month for the last year - the figure a maker PLANS against is the average. |
| 84 | `private int takenAt` |  |
| 93 | `private double offered` |  |
| 94 | `private double bid` |  |
| 95 | `private double localFilled` |  |
| 96 | `private double imported` |  |
| 97 | `private double exported` |  |
| 98 | `private final List<Trade> trades` |  |
| 106 | `private double drawn` | Units taken on demand since the last clearing - the builders' orders, the repair bill - which count as demand when the month is next priced. |
| 148 | `private double freightFactor` | What a unit's freight costs today, as a share of what a lorry charges. |
| 188 | `private double railCharge` | WHAT THE SHIPPER STILL PAYS AT HOME, per unit, as a share of the lorry rate - the railway's own invoice. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 421 | **type** `public final class GoodsMarket` | Where one good clears between whoever makes it and whoever wants it. |

### the last strike (lines 65-91)

### the month (lines 92-121)

| line | len | member | says |
|---:|---:|---|---|
| 108 | 4 | `public GoodsMarket(Good good)` |  |
| 113 | 1 | `public Good good()` |  |
| 116 | 3 | `public void setExchangeRate(double rate)` | City money per dollar. |
| 120 | 1 | `public double getExchangeRate()` |  |

### WHAT IT COSTS TO MOVE ONE, THIS MONTH (2026-09-16) (lines 122-216)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 3 | `public void setFreightFactor(double factor)` | still carry, and therefore the share of baseFreight() left in the world-facing price. |
| 168 | 1 | `public double getFreightFactor()` |  |
| 190 | 4 | `public void setRailCharge(double shareOfLorryRate)` |  |
| 195 | 1 | `public double getRailCharge()` |  |
| 198 | 1 | `public double domesticFreight()` | The railway's charge on one unit, in city money. |
| 201 | 3 | `public double freightChange()` | What the freight on one unit has moved by, in the world's money. |
| 206 | 4 | `public double importPrice()` | What an import costs the city, in the city's money. |
| 212 | 4 | `public double exportPrice()` | What the world pays the city for one, in the city's money. |

### ...AND WHAT THE SHIPPER IS ACTUALLY LEFT WITH (lines 217-381)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 1 | `public double netExportPrice()` | What an exporter nets on one, after the haulage it will be billed for. |
| 244 | 1 | `public double netImportPrice()` | ...and what an importer pays for one, landed AND hauled. |
| 259 | 4 | `public double landedPrice()` | What one unit costs a buyer to bring in this month (0.7.12 round 6): the local price when somebody in the city has the good on offer, and the import price when nobody does and it can be imported. |
| 265 | 3 | `public double floor()` | The floor: the export price, or nothing. |
| 270 | 4 | `public double ceiling()` | The ceiling: the import price, or twice the floor. |
| 276 | 3 | `private double openingPrice()` | The middle of the band: where a market nobody has told about anything opens. |
| 287 | 21 | `public void strike(double productionFlow, double stock, double demand)` | Prices the month. |
| 310 | 8 | `public void startMonth()` | Wipes the month's tally, not the draws. |
| 320 | 1 | `void noteDrawn(double units)` | A draw since the last clearing, counted toward the next strike's demand. |
| 327 | 4 | `void closeMonth()` | Closes the month's clearing: folds what the city took - the larger of what it asked for and what it got, at home or from the world - into the trend. |
| 333 | 8 | `public double getDemandTrend()` | The average take of this good a month, over the months the good plans on. |
| 343 | 5 | `public double[] getTakenHistory()` | The year, as the save carries it: oldest first. |
| 350 | 8 | `public void restoreTakenHistory(double[] saved)` | The year, put back on load. |
| 360 | 5 | `double takeDrawn()` | ...and the strike takes them. |
| 366 | 1 | `public double getDrawn()` |  |
| 369 | 9 | `public Trade record(String seller, String buyer, double units, double price)` | A fill, recorded. |
| 379 | 1 | `void noteOffered(double units)` |  |
| 380 | 1 | `void noteBid(double units)` |  |

### readers (lines 382-466)

| line | len | member | says |
|---:|---:|---|---|
| 384 | 1 | `public double getLocalPrice()` |  |
| 385 | 1 | `public double getSupplyFlow()` |  |
| 386 | 1 | `public double getSupplyStock()` |  |
| 388 | 1 | `public double getSupply()` | Flow plus the slice of stock the month was priced on. |
| 389 | 1 | `public double getDemand()` |  |
| 391 | 1 | `public double getOffered()` |  |
| 392 | 1 | `public double getBid()` |  |
| 393 | 1 | `public double getLocalFilled()` |  |
| 394 | 1 | `public double getImported()` |  |
| 395 | 1 | `public double getExported()` |  |
| 396 | 1 | `public List<Trade> getTrades()` |  |
| 399 | 4 | `public double getPriceIndex()` | Where in the band the price sits: 0 the floor, 1 the ceiling. |
| 405 | 3 | `public boolean isShortage()` | True when the buyers wanted more than the makers brought. |
| 435 | 3 | `public void setLocalPrice(double price)` | The price a month traded at, put back on load - restored, never recomputed. |
| 440 | 5 | `public void restoreStrike(double flow, double stock, double demand)` | The last strike's inputs, put back so the screens read the saved month. |
| 446 | 10 | `public void reset()` |  |
| 462 | 4 | `public void redenominate(double scale)` | The price in the new unit. |

