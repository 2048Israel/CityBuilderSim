# Good.java - 863 lines · 18 methods · 0 constants · model

`ham/citybuildersim/Good.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> A thing that can be made, bought, held, imported and exported.
> 
> THE GOODS ECONOMY (2026-09-11). Until the sector template, the city had
> four bespoke markets - food between the mills and the shops, ore between
> the mines and the mills, a free yard of building material, and rent - and
> every one of them was written by hand inside two handlers that knew each
> other by name. A thousand sectors cannot trade that way. So a good is one
> row here, a market is one object per row (see GoodsMarket), and a sector
> says which rows it makes and which it uses (see Sector). Nothing else has
> to know that bread and ore are different.
> 
> PRICES ARE IN THE WORLD'S MONEY, per unit, in THOUSANDS like every money
> field in the game (see BuildingsTemplate's header). A unit of food is what
> one person eats in a month, so .20 is $200 of food; ore is .14 a tonne,
> $140; steel .847, $847; a unit of building material is 18, $18,000, and a
> House is ten of them. The city faces these multiplied by the exchange rate
> (city money per dollar, times the world's own price level - see
> Game.startOfMonthUpdate), which is why they are stored unconverted: a
> devaluation lifts every ceiling and every floor at once, and that is what
> a devaluation does.
> 
> TWO WORLD PRICES, NOT ONE. What the world charges the city for a unit
> (the import price, the CEILING on the local price - nobody pays more at
> home than it costs to bring one in) and what the world pays the city for
> one (the export price, the FLOOR - nobody sells at home for less than the
> ship pays). The gap between them is freight, middlemen and the buyer's
> margin, and it is the whole of what makes a local market worth having:
> inside the band a local buyer and a local seller both do better than
> trading with the world. Food's .12 against .20 is the 0.6 wedge
> IndustrialHandler measured; ore's .14 against .41 is the mine's export
> price against the mill's scrap, which IronMarket carried as its two ends.
> A good with no import price (NaN) cannot be imported and has no ceiling
> from the world; a good with no export price cannot be exported and its
> makers idle or stock what they cannot sell.
> 
> ...AND SINCE 2026-09-16 THE GAP IS ARITHMETIC RATHER THAN A SENTENCE.
> 
> The paragraph above has always said what the wedge IS - "freight, middlemen
> and the buyer's margin" - and for a year it said it in words while the code
> carried two constants nobody could move. Jerus: "i really like the idea of
> decomposing the existing import/export bands into world price +
> transportation cost... then later, transportation efficiency can make that
> freight component smaller or larger."
> 
> So each good now carries a THIRD number, the cost of moving one unit, and
> the two ends split into four:
> 
>      worldBuyPrice   the world's own ask, before anything is moved
>    + baseFreight     getting it here
>    = worldImportPrice
> 
>      worldSellPrice  the world's own bid
>    - baseFreight     getting it there
>    = worldExportPrice
> 
> THE DELIVERED PRICES ARE THE STORED ONES AND THE WORLD BAND IS DERIVED,
> which is the opposite of how it reads and is the only way round that is
> exact. Storing the world band and adding freight back gives 0.847 as
> 0.8470000000000002 for steel and 0.28 as 0.27999999999999997 for crops -
> one ulp, which in this codebase is never nothing: DenominationCheck spent
> ... (46 more lines in the source)

**Uses:** [Traffic](Traffic.md) (3)

**Used by (61):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BooksCheck](BooksCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConsumptionCheck](ConsumptionCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [Formats](Formats.md), [Game](Game.md), [GoodsMarket](GoodsMarket.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [Materials](Materials.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [ServicesScreen](ServicesScreen.md), [ShadowBasket](ShadowBasket.md), [SummaryScreen](SummaryScreen.md), [Trade](Trade.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [VanCheck](VanCheck.md)

## Sections

| line | section |
|---:|---|
| 767 | WHAT IT TAKES TO CARRY ONE (2026-09-16) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 165 | `Good.CROPS` | What the fields grow, by the tonne, before anybody has done anything to it - grain, roots, vegetables, fruit, the milk and the feed behind the meat. |
| 218 | `Good.GRAINS` | The cheapest calorie there is, and what subsistence is measured in. |
| 221 | `Good.BREAD` | A staple with the milling and baking already done. |
| 224 | `Good.DAIRY_EGGS` | Milk, cheese and eggs - the protein a poor city can still afford. |
| 227 | `Good.VEGETABLES` | Cheap by the kilo, dear by the calorie, which is why the poor eat few. |
| 230 | `Good.FRUIT` | The most income-elastic produce in the file: the first thing a raise buys. |
| 233 | `Good.MEAT` | Bennett's law in one line - the share of this rises with every wage. |
| 236 | `Good.FISH` | Dearer than meat and healthier than it; the last thing a city learns to buy. |
| 239 | `Good.FATS` | Cooking fats. |
| 242 | `Good.PROCESSED_MEAT` | Bought for convenience, not for nutrition - see Consumption's time axis. |
| 245 | `Good.READY_MEALS` | What a household with two earners and three children eats on a Tuesday. |
| 248 | `Good.BAKERY` | Bakery goods, as distinct from bread: a treat, priced like one. |
| 251 | `Good.SNACKS` | The dearest calorie in the file, and the one a rich city buys most of. |
| 254 | `Good.DRINKS` | Mostly water, sold by the kilo, and a tenth of what the city spends. |
| 261 | `Good.IRON` | Iron ore, and the scrap that stands in for it. |
| 288 | `Good.STEEL` | Smelted by the mills. |
| 308 | `Good.MATERIALS` | One unit of building material - a House is ten of them. |
| 315 | `Good.GROCERIES` | What the shops sell: food, on a shelf, to a household. |
| 323 | `Good.HOUSING` | A home for a month. |
| 330 | `Good.BUILDING_WORK` | A point of construction work. |
| 377 | `Good.SUPPORT_WORK` |  |
| 379 | `Good.BACK_OFFICE_WORK` |  |
| 381 | `Good.ENGINEERING_WORK` |  |
| 481 | `Good.FABRICATED_STEEL` |  |
| 483 | `Good.MACHINERY` |  |
| 583 | `Good.CARS` |  |
| 585 | `Good.VANS` |  |
| 587 | `Good.ROLLING_STOCK` |  |
| 623 | `Good.LUXURIES` |  |
| 639 | `Good.LUXURY_TRADE` | ...and what a shop sells one for, which is not what it paid. |
| 667 | `Good.MEALS` |  |
| 672 | `Good.Pricing.BAND` | Clears in GoodsMarket between the export floor and the import ceiling. |
| 674 | `Good.Pricing.SELLER` | The selling sector strikes it; the market only records the sale. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 677 | `private final String label` |  |
| 678 | `private final String unit` |  |
| 679 | `private final double worldImportPrice` |  |
| 680 | `private final double worldExportPrice` |  |
| 681 | `private final double baseFreight` |  |
| 682 | `private final boolean stockable` |  |
| 683 | `private final Pricing pricing` |  |
| 684 | `private final boolean taxExempt` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 111 | 753 | **type** `public enum Good` | A thing that can be made, bought, held, imported and exported. |
| 670 | 6 | **type** `public enum Pricing` | How a good's price is struck. |
| 686 | 11 | `Good(String label, String unit, double worldImportPrice, double worldExportPrice, double baseFreight, boolean stockable, Pricin...` |  |
| 698 | 1 | `public String label()` |  |
| 699 | 1 | `public String unit()` |  |
| 709 | 1 | `public double worldImportPrice()` | What the world charges for one DELIVERED HERE, in ITS money. |
| 712 | 1 | `public double worldExportPrice()` | What the world pays for one DELIVERED THERE, in ITS money. |
| 723 | 1 | `public double baseFreight()` | What it costs to move one unit between the city and the world, in the world's money - three quarters of the wedge on every good that has both ends, and zero for the things nobody ships. |
| 732 | 1 | `public double worldBuyPrice()` | The world's own ask, before anything is moved - the import price less the freight in it. |
| 743 | 1 | `public double worldSellPrice()` | The world's own bid, before anything is moved - the export price with the freight added back. |
| 745 | 1 | `public boolean importable()` |  |
| 746 | 1 | `public boolean exportable()` |  |
| 747 | 1 | `public boolean stockable()` |  |
| 748 | 1 | `public Pricing pricing()` |  |
| 751 | 1 | `public boolean taxExempt()` | True for a supply the sales tax never touches. |
| 762 | 1 | `public int planningMonths()` | How many months of the city's take a maker averages before it plans a plant against it. |
| 765 | 1 | `public boolean traded()` | Clears in the band on scarcity, as opposed to being priced by its seller. |

### WHAT IT TAKES TO CARRY ONE (2026-09-16) (lines 767-863)

| line | len | member | says |
|---:|---:|---|---|
| 793 | 25 | `public double tonnesPerUnit()` | One unit, in tonnes. |
| 835 | 21 | `public Traffic traffic()` | Which stream of traffic a tonne of this joins, or null for the things that never take up road at all. |
| 858 | 5 | `public static Good byName(String name)` | The good with this saved name, or null - a save from a build without it loses that line, not the load. |

