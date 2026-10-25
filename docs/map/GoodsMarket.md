# GoodsMarket.java - 516 lines · 54 methods · 3 constants · model

`ham/citybuildersim/GoodsMarket.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

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

**Uses:** [Trade](Trade.md) (8), [Good](Good.md) (3)

**Used by (54):** [AgricultureCheck](AgricultureCheck.md), [BuildAdvice](BuildAdvice.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CoalMining](CoalMining.md), [CopperRefining](CopperRefining.md), [EconomyManager](EconomyManager.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [Freight](Freight.md), [GdpCheck](GdpCheck.md), [Haulage](Haulage.md), [HistoryScreen](HistoryScreen.md), [IndustryView](IndustryView.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PortCheck](PortCheck.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [Refining](Refining.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [Sector](Sector.md), [SectorFlow](SectorFlow.md), [SpreadPlanner](SpreadPlanner.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TradeScreen](TradeScreen.md), [VanCheck](VanCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 65 | · the last strike |
| 92 | · the month |
| 130 | WHAT IT COSTS TO MOVE ONE, THIS MONTH (2026-09-16) |
| 225 | ...AND WHAT THE SHIPPER IS ACTUALLY LEFT WITH |
| 254 | · the wedge, taken apart (0.7.29) |
| 430 | · readers |

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
| 106 | `private final List<Trade> draws` | The month's draws (Markets.draw()) since the month last closed: kept past the clearing among the month's fills, and put back on their sellers' rows after the strike (FW5, 2026-10-25). |
| 114 | `private double drawn` | Units taken on demand since the last clearing - the builders' orders, the repair bill - which count as demand when the month is next priced. |
| 156 | `private double freightFactor` | What a unit's freight costs today, as a share of what a lorry charges. |
| 196 | `private double railCharge` | WHAT THE SHIPPER STILL PAYS AT HOME, per unit, as a share of the lorry rate - the railway's own invoice. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 471 | **type** `public final class GoodsMarket` | Where one good clears between whoever makes it and whoever wants it. |

### the last strike (lines 65-91)

### the month (lines 92-129)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 4 | `public GoodsMarket(Good good)` |  |
| 121 | 1 | `public Good good()` |  |
| 124 | 3 | `public void setExchangeRate(double rate)` | City money per dollar, times the world's price level - since AN1 the D$ the goods are struck at (EconomyManager, THE D$ THE GOODS ARE STRUCK AT), not the D$ itself. |
| 128 | 1 | `public double getExchangeRate()` |  |

### WHAT IT COSTS TO MOVE ONE, THIS MONTH (2026-09-16) (lines 130-224)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 3 | `public void setFreightFactor(double factor)` | still carry, and therefore the share of baseFreight() left in the world-facing price. |
| 176 | 1 | `public double getFreightFactor()` |  |
| 198 | 4 | `public void setRailCharge(double shareOfLorryRate)` |  |
| 203 | 1 | `public double getRailCharge()` |  |
| 206 | 1 | `public double domesticFreight()` | The railway's charge on one unit, in city money. |
| 209 | 3 | `public double freightChange()` | What the freight on one unit has moved by, in the world's money. |
| 214 | 4 | `public double importPrice()` | What an import costs the city, in the city's money. |
| 220 | 4 | `public double exportPrice()` | What the world pays the city for one, in the city's money. |

### ...AND WHAT THE SHIPPER IS ACTUALLY LEFT WITH (lines 225-253)

| line | len | member | says |
|---:|---:|---|---|
| 249 | 1 | `public double netExportPrice()` | What an exporter nets on one, after the haulage it will be billed for. |
| 252 | 1 | `public double netImportPrice()` | ...and what an importer pays for one, landed AND hauled. |

### the wedge, taken apart (0.7.29) (lines 254-429)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 1 | `public double freightInBand()` | The freight still in the band, both ways: what the lorries are paid abroad on one unit bought and one sold (0.7.29). |
| 271 | 1 | `public double bandByLorry()` | The band with every tonne on a lorry: the world's asking less its buying price, freight and all (0.7.29). |
| 274 | 3 | `public double worldMargin()` | The world's own margin in the band: the band with no freight in it at all, which no railway narrows (0.7.29). |
| 279 | 1 | `public double railInWedge()` | The railway's charge on the wedge, both ways: what an importer pays on top and an exporter gives up (0.7.29). |
| 294 | 4 | `public double landedPrice()` | What one unit costs a buyer to bring in this month (0.7.12 round 6): the local price when somebody in the city has the good on offer, and the import price when nobody does and it can be imported. |
| 300 | 3 | `public double floor()` | The floor: the export price, or nothing. |
| 305 | 4 | `public double ceiling()` | The ceiling: the import price, or twice the floor. |
| 311 | 3 | `private double openingPrice()` | The middle of the band: where a market nobody has told about anything opens. |
| 322 | 21 | `public void strike(double productionFlow, double stock, double demand)` | Prices the month. |
| 345 | 10 | `public void startMonth()` | Wipes the month's tally, not the draws. |
| 357 | 3 | `void logDraw(Trade t)` | A draw's fill, kept with the month's (Markets.draw(); FW5, 2026-10-25). |
| 362 | 1 | `List<Trade> monthsDraws()` | The month's draws since it last closed (FW5, 2026-10-25): read-only. |
| 365 | 1 | `void closeDraws()` | The month is over: its draws were shown with its fills, and the next draw is the next month's (FW5, 2026-10-25). |
| 368 | 1 | `void noteDrawn(double units)` | A draw since the last clearing, counted toward the next strike's demand. |
| 375 | 4 | `void closeMonth()` | Closes the month's clearing: folds what the city took - the larger of what it asked for and what it got, at home or from the world - into the trend. |
| 381 | 8 | `public double getDemandTrend()` | The average take of this good a month, over the months the good plans on. |
| 391 | 5 | `public double[] getTakenHistory()` | The year, as the save carries it: oldest first. |
| 398 | 8 | `public void restoreTakenHistory(double[] saved)` | The year, put back on load. |
| 408 | 5 | `double takeDrawn()` | ...and the strike takes them. |
| 414 | 1 | `public double getDrawn()` |  |
| 417 | 9 | `public Trade record(String seller, String buyer, double units, double price)` | A fill, recorded. |
| 427 | 1 | `void noteOffered(double units)` |  |
| 428 | 1 | `void noteBid(double units)` |  |

### readers (lines 430-516)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 1 | `public double getLocalPrice()` |  |
| 433 | 1 | `public double getSupplyFlow()` |  |
| 434 | 1 | `public double getSupplyStock()` |  |
| 436 | 1 | `public double getSupply()` | Flow plus the slice of stock the month was priced on. |
| 437 | 1 | `public double getDemand()` |  |
| 439 | 1 | `public double getOffered()` |  |
| 440 | 1 | `public double getBid()` |  |
| 441 | 1 | `public double getLocalFilled()` |  |
| 442 | 1 | `public double getImported()` |  |
| 443 | 1 | `public double getExported()` |  |
| 445 | 1 | `public List<Trade> getTrades()` | The month's fills: the clearing's and, since FW5 (2026-10-25), the month's draws too, which the clearing no longer wipes. |
| 448 | 4 | `public double getPriceIndex()` | Where in the band the price sits: 0 the floor, 1 the ceiling. |
| 454 | 3 | `public boolean isShortage()` | True when the buyers wanted more than the makers brought. |
| 484 | 3 | `public void setLocalPrice(double price)` | The price a month traded at, put back on load - restored, never recomputed. |
| 489 | 5 | `public void restoreStrike(double flow, double stock, double demand)` | The last strike's inputs, put back so the screens read the saved month. |
| 495 | 11 | `public void reset()` |  |
| 512 | 4 | `public void redenominate(double scale)` | The price in the new unit. |

