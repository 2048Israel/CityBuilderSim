# Good.java - 1,169 lines · 21 methods · 0 constants · model

`ham/citybuildersim/Good.java` - generated 2026-10-11 by CodeMap; line numbers are as of that run.

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
> one person eats in a month, so .20 is $200 of food; ore is .085 a tonne,
> $85; steel .470, $470; a unit of building material is 18, $18,000, and a
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
> IndustrialHandler measured; ore's .085 against .115 is what a mine nets
> shipping out against what a tonne costs landed (until 0.7.120 .14 against
> .41, the mine's ore against the mill's scrap, IronMarket's two ends).
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
> ... (49 more lines in the source)

**Uses:** [Traffic](Traffic.md) (5), [Ports](Ports.md) (5)

**Used by (102):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConsumptionCheck](ConsumptionCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [ExtractionSector](ExtractionSector.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [Formats](Formats.md), [Founding](Founding.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GoodsMarket](GoodsMarket.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [Icons](Icons.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [Markets](Markets.md), [Materials](Materials.md), [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [Motoring](Motoring.md), [NationalAccounts](NationalAccounts.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [ProcessingSector](ProcessingSector.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryFlow](RefineryFlow.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Resource](Resource.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [SpreadPlanner](SpreadPlanner.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [SupplierCredit](SupplierCredit.md), [SupplierCreditCheck](SupplierCreditCheck.md), [Trade](Trade.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [VanCheck](VanCheck.md), [WellCheck](WellCheck.md)

## Sections

| line | section |
|---:|---|
| 1028 | WHAT IT TAKES TO CARRY ONE (2026-09-16) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 168 | `Good.CROPS` | What the fields grow, by the tonne, before anybody has done anything to it - grain, roots, vegetables, fruit, the milk and the feed behind the meat. |
| 221 | `Good.GRAINS` | The cheapest calorie there is, and what subsistence is measured in. |
| 224 | `Good.BREAD` | A staple with the milling and baking already done. |
| 227 | `Good.DAIRY_EGGS` | Milk, cheese and eggs - the protein a poor city can still afford. |
| 230 | `Good.VEGETABLES` | Cheap by the kilo, dear by the calorie, which is why the poor eat few. |
| 233 | `Good.FRUIT` | The most income-elastic produce in the file: the first thing a raise buys. |
| 236 | `Good.MEAT` | Bennett's law in one line - the share of this rises with every wage. |
| 239 | `Good.FISH` | Dearer than meat and healthier than it; the last thing a city learns to buy. |
| 242 | `Good.FATS` | Cooking fats. |
| 245 | `Good.PROCESSED_MEAT` | Bought for convenience, not for nutrition - see Consumption's time axis. |
| 248 | `Good.READY_MEALS` | What a household with two earners and three children eats on a Tuesday. |
| 251 | `Good.BAKERY` | Bakery goods, as distinct from bread: a treat, priced like one. |
| 254 | `Good.SNACKS` | The dearest calorie in the file, and the one a rich city buys most of. |
| 257 | `Good.DRINKS` | Mostly water, sold by the kilo, and a tenth of what the city spends. |
| 276 | `Good.IRON` | Iron ore - fines, lump and concentrate at 62-66% Fe - and nothing else since 0.7.120. |
| 320 | `Good.STEEL` | Made by the mills: the arc furnaces out of scrap (and since 0.7.123, batch I4, the oxygen shop out of pig iron and scrap). |
| 340 | `Good.MATERIALS` | One unit of building material - a House is ten of them. |
| 349 | `Good.GROCERIES` | What the shops sell: food, on a shelf, to a household. |
| 357 | `Good.HOUSING` | A home for a month. |
| 364 | `Good.BUILDING_WORK` | A point of construction work. |
| 411 | `Good.SUPPORT_WORK` |  |
| 413 | `Good.BACK_OFFICE_WORK` |  |
| 415 | `Good.ENGINEERING_WORK` |  |
| 528 | `Good.FABRICATED_STEEL` |  |
| 530 | `Good.MACHINERY` |  |
| 643 | `Good.CARS` |  |
| 645 | `Good.VANS` |  |
| 647 | `Good.ROLLING_STOCK` |  |
| 683 | `Good.LUXURIES` |  |
| 699 | `Good.LUXURY_TRADE` | ...and what a shop sells one for, which is not what it paid. |
| 727 | `Good.MEALS` |  |
| 753 | `Good.CRUDE` |  |
| 817 | `Good.LPG` | Liquefied petroleum gas - propane and butane, the lightest cut - by the litre, 1,850 to the tonne. |
| 820 | `Good.NAPHTHA` | Naphtha, the petrochemical feed and the reformer's: 1,351 litres a tonne (est., JODI). |
| 823 | `Good.PETROL` | What the drivers burn (Motoring.drawFuel()): the ladder's 1.20 of crude since 0.7.78 (FUEL's band before), 1,320 litres a tonne. |
| 826 | `Good.JET` | Kerosene for aircraft, 1,260 litres a tonne. |
| 829 | `Good.DIESEL` | What the railway burns (Rail.haul()), and since 0.7.83 the vans (Sector.runFleet()): the ladder's 1.35 of crude since 0.7.78 (FUEL's band before), 1,180 litres a tonne. |
| 832 | `Good.LUBRICANTS` | Base oils, the dearest litre in the barrel: 1,127 litres a tonne (est., JODI). |
| 835 | `Good.FUEL_OIL` | Heavy fuel oil - the gas oil and the residue no conversion unit here upgrades (none until 0.7.80) - 1,010 litres a tonne. |
| 838 | `Good.BITUMEN` | Road binder, by the tonne: an asphalt unit's, from heavy crude's residue. |
| 841 | `Good.COKE` | Petroleum coke, by the tonne: a coker's solid residue. |
| 861 | `Good.SCRAP` | Steel scrap, the arc furnaces' feed (0.7.120, batch I3; Jerus's Q4, 2026-10-08: "split"; materials-chains-research.md 2.3). |
| 898 | `Good.PELLETS` | Iron ore pellets, by the tonne: a Pelletising Plant's, of the city's own ore or another's; nothing here uses them until a DRI plant exists (the study's 2.8), so they ship. |
| 901 | `Good.PIG_IRON` | Pig iron - hot metal - by the tonne: a blast furnace's, the oxygen shop's feed. |
| 904 | `Good.MET_COKE` | Metallurgical coke, by the tonne: the Coke Ovens' out of coking coal, the blast furnaces' fuel and reductant. |
| 907 | `Good.THERMAL_COAL` | Thermal coal, by the tonne, 20 GJ: the pelletising plant's fuel and the blast furnace's injection coal. |
| 910 | `Good.COKING_COAL` | Coking coal, by the tonne, washed: what the Coke Ovens bake. |
| 913 | `Good.LIMESTONE` | Limestone, by the tonne: the blast furnaces' flux (and the kilns' feed, from batch I7). |
| 916 | `Good.LIME` | Quicklime, by the tonne: the Oxygen Steel Shop's flux, about 60 kg a tonne of steel. |
| 921 | `Good.Pricing.BAND` | Clears in GoodsMarket between the export floor and the import ceiling. |
| 923 | `Good.Pricing.SELLER` | The selling sector strikes it; the market only records the sale. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 926 | `private final String label` |  |
| 927 | `private final String unit` |  |
| 928 | `private final double worldImportPrice` |  |
| 929 | `private final double worldExportPrice` |  |
| 930 | `private final double baseFreight` |  |
| 931 | `private final boolean stockable` |  |
| 932 | `private final Pricing pricing` |  |
| 933 | `private final boolean taxExempt` |  |
| 936 | `private final double litresPerTonne` | Litres to the tonne for a good counted in litres (0.7.76), NaN for every other. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 114 | 1056 | **type** `public enum Good` | A thing that can be made, bought, held, imported and exported. |
| 919 | 6 | **type** `public enum Pricing` | How a good's price is struck. |
| 938 | 4 | `Good(String label, String unit, double worldImportPrice, double worldExportPrice, double baseFreight, boolean stockable, Pricin...` |  |
| 943 | 12 | `Good(String label, String unit, double worldImportPrice, double worldExportPrice, double baseFreight, boolean stockable, Pricin...` |  |
| 957 | 1 | `public double litresPerTonne()` | Litres to the tonne (0.7.76): what a litre of this weighs, inverted; NaN for a good not counted in litres. |
| 959 | 1 | `public String label()` |  |
| 960 | 1 | `public String unit()` |  |
| 970 | 1 | `public double worldImportPrice()` | What the world charges for one DELIVERED HERE, in ITS money. |
| 973 | 1 | `public double worldExportPrice()` | What the world pays for one DELIVERED THERE, in ITS money. |
| 984 | 1 | `public double baseFreight()` | What it costs to move one unit between the city and the world, in the world's money - three quarters of the wedge on every good that has both ends, and zero for the things nobody ships. |
| 993 | 1 | `public double worldBuyPrice()` | The world's own ask, before anything is moved - the import price less the freight in it. |
| 1004 | 1 | `public double worldSellPrice()` | The world's own bid, before anything is moved - the export price with the freight added back. |
| 1006 | 1 | `public boolean importable()` |  |
| 1007 | 1 | `public boolean exportable()` |  |
| 1008 | 1 | `public boolean stockable()` |  |
| 1009 | 1 | `public Pricing pricing()` |  |
| 1012 | 1 | `public boolean taxExempt()` | True for a supply the sales tax never touches. |
| 1023 | 1 | `public int planningMonths()` | How many months of the city's take a maker averages before it plans a plant against it. |
| 1026 | 1 | `public boolean traded()` | Clears in the band on scarcity, as opposed to being priced by its seller. |

### WHAT IT TAKES TO CARRY ONE (2026-09-16) (lines 1028-1169)

| line | len | member | says |
|---:|---:|---|---|
| 1054 | 29 | `public double tonnesPerUnit()` | One unit, in tonnes. |
| 1100 | 32 | `public Traffic traffic()` | Which stream of traffic a tonne of this joins, or null for the things that never take up road at all. |
| 1146 | 16 | `public Ports.Cargo cargo()` | What kind of ship carries it, and so which terminal's berth (0.7.86, batch O9; runs/spec-oil.md 2.1, Ports.Cargo), or null for the things that never cross the boundary as freight. |
| 1164 | 5 | `public static Good byName(String name)` | The good with this saved name, or null - a save from a build without it loses that line, not the load. |

