# Good.java - 1,027 lines · 21 methods · 0 constants · model

`ham/citybuildersim/Good.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Traffic](Traffic.md) (5), [Ports](Ports.md) (5)

**Used by (97):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConsumptionCheck](ConsumptionCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [Formats](Formats.md), [Founding](Founding.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GoodsMarket](GoodsMarket.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [Icons](Icons.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [Markets](Markets.md), [Materials](Materials.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [Motoring](Motoring.md), [NationalAccounts](NationalAccounts.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryFlow](RefineryFlow.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Resource](Resource.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [SupplierCredit](SupplierCredit.md), [SupplierCreditCheck](SupplierCreditCheck.md), [Trade](Trade.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [VanCheck](VanCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 895 | WHAT IT TAKES TO CARRY ONE (2026-09-16) |

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
| 317 | `Good.GROCERIES` | What the shops sell: food, on a shelf, to a household. |
| 325 | `Good.HOUSING` | A home for a month. |
| 332 | `Good.BUILDING_WORK` | A point of construction work. |
| 379 | `Good.SUPPORT_WORK` |  |
| 381 | `Good.BACK_OFFICE_WORK` |  |
| 383 | `Good.ENGINEERING_WORK` |  |
| 483 | `Good.FABRICATED_STEEL` |  |
| 485 | `Good.MACHINERY` |  |
| 585 | `Good.CARS` |  |
| 587 | `Good.VANS` |  |
| 589 | `Good.ROLLING_STOCK` |  |
| 625 | `Good.LUXURIES` |  |
| 641 | `Good.LUXURY_TRADE` | ...and what a shop sells one for, which is not what it paid. |
| 669 | `Good.MEALS` |  |
| 695 | `Good.CRUDE` |  |
| 759 | `Good.LPG` | Liquefied petroleum gas - propane and butane, the lightest cut - by the litre, 1,850 to the tonne. |
| 762 | `Good.NAPHTHA` | Naphtha, the petrochemical feed and the reformer's: 1,351 litres a tonne (est., JODI). |
| 765 | `Good.PETROL` | What the drivers burn (Motoring.drawFuel()): the ladder's 1.20 of crude since 0.7.78 (FUEL's band before), 1,320 litres a tonne. |
| 768 | `Good.JET` | Kerosene for aircraft, 1,260 litres a tonne. |
| 771 | `Good.DIESEL` | What the railway burns (Rail.haul()), and since 0.7.83 the vans (Sector.runFleet()): the ladder's 1.35 of crude since 0.7.78 (FUEL's band before), 1,180 litres a tonne. |
| 774 | `Good.LUBRICANTS` | Base oils, the dearest litre in the barrel: 1,127 litres a tonne (est., JODI). |
| 777 | `Good.FUEL_OIL` | Heavy fuel oil - the gas oil and the residue no conversion unit here upgrades (none until 0.7.80) - 1,010 litres a tonne. |
| 780 | `Good.BITUMEN` | Road binder, by the tonne: an asphalt unit's, from heavy crude's residue. |
| 783 | `Good.COKE` | Petroleum coke, by the tonne: a coker's solid residue. |
| 788 | `Good.Pricing.BAND` | Clears in GoodsMarket between the export floor and the import ceiling. |
| 790 | `Good.Pricing.SELLER` | The selling sector strikes it; the market only records the sale. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 793 | `private final String label` |  |
| 794 | `private final String unit` |  |
| 795 | `private final double worldImportPrice` |  |
| 796 | `private final double worldExportPrice` |  |
| 797 | `private final double baseFreight` |  |
| 798 | `private final boolean stockable` |  |
| 799 | `private final Pricing pricing` |  |
| 800 | `private final boolean taxExempt` |  |
| 803 | `private final double litresPerTonne` | Litres to the tonne for a good counted in litres (0.7.76), NaN for every other. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 111 | 917 | **type** `public enum Good` | A thing that can be made, bought, held, imported and exported. |
| 786 | 6 | **type** `public enum Pricing` | How a good's price is struck. |
| 805 | 4 | `Good(String label, String unit, double worldImportPrice, double worldExportPrice, double baseFreight, boolean stockable, Pricin...` |  |
| 810 | 12 | `Good(String label, String unit, double worldImportPrice, double worldExportPrice, double baseFreight, boolean stockable, Pricin...` |  |
| 824 | 1 | `public double litresPerTonne()` | Litres to the tonne (0.7.76): what a litre of this weighs, inverted; NaN for a good not counted in litres. |
| 826 | 1 | `public String label()` |  |
| 827 | 1 | `public String unit()` |  |
| 837 | 1 | `public double worldImportPrice()` | What the world charges for one DELIVERED HERE, in ITS money. |
| 840 | 1 | `public double worldExportPrice()` | What the world pays for one DELIVERED THERE, in ITS money. |
| 851 | 1 | `public double baseFreight()` | What it costs to move one unit between the city and the world, in the world's money - three quarters of the wedge on every good that has both ends, and zero for the things nobody ships. |
| 860 | 1 | `public double worldBuyPrice()` | The world's own ask, before anything is moved - the import price less the freight in it. |
| 871 | 1 | `public double worldSellPrice()` | The world's own bid, before anything is moved - the export price with the freight added back. |
| 873 | 1 | `public boolean importable()` |  |
| 874 | 1 | `public boolean exportable()` |  |
| 875 | 1 | `public boolean stockable()` |  |
| 876 | 1 | `public Pricing pricing()` |  |
| 879 | 1 | `public boolean taxExempt()` | True for a supply the sales tax never touches. |
| 890 | 1 | `public int planningMonths()` | How many months of the city's take a maker averages before it plans a plant against it. |
| 893 | 1 | `public boolean traded()` | Clears in the band on scarcity, as opposed to being priced by its seller. |

### WHAT IT TAKES TO CARRY ONE (2026-09-16) (lines 895-1027)

| line | len | member | says |
|---:|---:|---|---|
| 921 | 29 | `public double tonnesPerUnit()` | One unit, in tonnes. |
| 967 | 26 | `public Traffic traffic()` | Which stream of traffic a tonne of this joins, or null for the things that never take up road at all. |
| 1006 | 14 | `public Ports.Cargo cargo()` | What kind of ship carries it, and so which terminal's berth (0.7.86, batch O9; runs/spec-oil.md 2.1, Ports.Cargo), or null for the things that never cross the boundary as freight. |
| 1022 | 5 | `public static Good byName(String name)` | The good with this saved name, or null - a save from a build without it loses that line, not the load. |

