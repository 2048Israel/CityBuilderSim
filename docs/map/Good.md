# Good.java - 1,416 lines · 23 methods · 0 constants · model

`ham/citybuildersim/Good.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

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

**Used by (132):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CarCheck](CarCheck.md), [CementConcrete](CementConcrete.md), [CementConcreteCheck](CementConcreteCheck.md), [CityBasket](CityBasket.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConsumptionCheck](ConsumptionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [ExtractionSector](ExtractionSector.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Formats](Formats.md), [Founding](Founding.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FuelSplit](FuelSplit.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GoodsMarket](GoodsMarket.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [HomeRailCheck](HomeRailCheck.md), [Icons](Icons.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [Markets](Markets.md), [Materials](Materials.md), [MaterialsCheck](MaterialsCheck.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MoneyCheck](MoneyCheck.md), [Motoring](Motoring.md), [NationalAccounts](NationalAccounts.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [PortCheck](PortCheck.md), [Ports](Ports.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryFlow](RefineryFlow.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Resource](Resource.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [SpreadPlanner](SpreadPlanner.md), [StrategicReserve](StrategicReserve.md), [SummaryScreen](SummaryScreen.md), [SupplierCredit](SupplierCredit.md), [SupplierCreditCheck](SupplierCreditCheck.md), [Trade](Trade.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TruckCheck](TruckCheck.md), [VanCheck](VanCheck.md), [WellCheck](WellCheck.md), [WoodProducts](WoodProducts.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 1240 | WHAT IT TAKES TO CARRY ONE (2026-09-16) |

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
| 829 | `Good.DIESEL` | What the railway burns (Freight.haul()), and since 0.7.83 the vans (Sector.runFleet()): the ladder's 1.35 of crude since 0.7.78 (FUEL's band before), 1,180 litres a tonne. |
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
| 919 | `Good.LIMESTONE` | Limestone, by the tonne: the blast furnaces' flux and, since 0.7.131 (batch I7), the kilns' and cement plants' feed - made by Quarrying's quarries on limestone fields, and what of it no buyer here takes sold as AGGREG... |
| 922 | `Good.LIME` | Quicklime, by the tonne: the Oxygen Steel Shop's flux, about 60 kg a tonne of steel - burnt from limestone by Cement & Concrete's Lime Kiln since 0.7.131 (batch I7). |
| 950 | `Good.AGGREGATE` | Aggregate - crushed stone, sand and gravel - by the tonne: a hard-rock quarry's, and a limestone quarry's limestone no buyer takes (Q17). |
| 953 | `Good.DIMENSION_STONE` | Dimension stone - cut blocks of granite and the like - by the tonne: the Dimension Stone Quarry's, on hard rock. |
| 956 | `Good.CEMENT` | Portland cement, by the tonne: Cement & Concrete's cement plants', of limestone, aggregate and thermal coal. |
| 994 | `Good.COPPER_CONC` | Copper concentrate - sulphide ore floated to 30% copper - by the tonne: a sulphide field's mine and mill's, the smelters' feed. |
| 997 | `Good.ANODE` | Copper anode, by the tonne: a smelter's, of concentrate, the electrolytic refinery's feed. |
| 1000 | `Good.COPPER` | Copper cathode, by the tonne, LME grade A: an electrolytic refinery's of anode, a scrap refinery's of the city's old copper and scrap, and an oxide field's leach plant's at the mine; the rod mills' feed. |
| 1003 | `Good.COPPER_SCRAP` | Copper scrap, sorted, by the tonne: what the Scrap Copper Refinery buys past the city's own old copper (OldMetal). |
| 1006 | `Good.COPPER_ROD` | Copper wire rod, by the tonne: a rod mill's, of cathode, the Wire & Cable Works' feed. |
| 1009 | `Good.ACID` | Sulphuric acid, by the tonne: the smelters' co-product from their gas, about 0.9 t a tonne of concentrate [K27], sold; the leach plants' reagent, 3.5 t a tonne of copper. |
| 1012 | `Good.WIRE_CABLE` | Wire and cable, by the tonne of copper contained: the Wire & Cable Works' (Manufacturing), of rod. |
| 1037 | `Good.SAWLOGS` | Sawlogs - roundwood fit for the saw - by the cubic metre: about half a crew's cut and half the salvage cut. |
| 1040 | `Good.PULPWOOD` | Pulpwood - roundwood for chips, pulp and panels - by the cubic metre: the other half. |
| 1080 | `Good.LUMBER` | Sawn lumber, by the cubic metre: a sawmill's, of sawlogs, about half of each log. |
| 1083 | `Good.PANELS` | Wood panels - oriented strand board - by the cubic metre: the Panel Mill's, of pulpwood. |
| 1086 | `Good.RESIDUES` | Mill residues - chips and sawdust - by the cubic metre, green: a sawmill's, four tenths of each log; the pulp mill's and the pellet mill's feed. |
| 1089 | `Good.PULP` | Wood pulp - northern bleached softwood kraft - by the tonne: the Pulp Mill's, of pulpwood and residues; the paper mill's feed. |
| 1092 | `Good.PAPER` | Paper - printing and writing - by the tonne: the Paper Mill's, of pulp. |
| 1095 | `Good.WOOD_PELLETS` | Wood pellets, by the tonne: the Wood Pellet Mill's, of residues. |
| 1115 | `Good.TRUCKS` | A heavy truck - tractor and semitrailer, 25 t a load - by the unit: the Heavy Truck Plant's; Freight's Haulage division's fleet. |
| 1120 | `Good.Pricing.BAND` | Clears in GoodsMarket between the export floor and the import ceiling. |
| 1122 | `Good.Pricing.SELLER` | The selling sector strikes it; the market only records the sale. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 1125 | `private final String label` |  |
| 1126 | `private final String unit` |  |
| 1127 | `private final double worldImportPrice` |  |
| 1128 | `private final double worldExportPrice` |  |
| 1129 | `private final double baseFreight` |  |
| 1130 | `private final boolean stockable` |  |
| 1131 | `private final Pricing pricing` |  |
| 1132 | `private final boolean taxExempt` |  |
| 1135 | `private final double litresPerTonne` | Litres to the tonne for a good counted in litres (0.7.76), NaN for every other. |
| 1138 | `private final double tonnesPerCubicMetre` | Tonnes to the cubic metre for a good counted in cubic metres (0.7.138, batch I9; spec-materials 2.3): green roundwood's 0.9; NaN for every other. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 114 | 1303 | **type** `public enum Good` | A thing that can be made, bought, held, imported and exported. |
| 1118 | 6 | **type** `public enum Pricing` | How a good's price is struck. |
| 1140 | 4 | `Good(String label, String unit, double worldImportPrice, double worldExportPrice, double baseFreight, boolean stockable, Pricin...` |  |
| 1145 | 4 | `Good(String label, String unit, double worldImportPrice, double worldExportPrice, double baseFreight, boolean stockable, Pricin...` |  |
| 1150 | 14 | `Good(String label, String unit, double worldImportPrice, double worldExportPrice, double baseFreight, boolean stockable, Pricin...` |  |
| 1166 | 1 | `public double litresPerTonne()` | Litres to the tonne (0.7.76): what a litre of this weighs, inverted; NaN for a good not counted in litres. |
| 1169 | 1 | `public double tonnesPerCubicMetre()` | Tonnes to the cubic metre (0.7.138): what a cubic metre of this weighs; NaN for a good not counted in cubic metres. |
| 1171 | 1 | `public String label()` |  |
| 1172 | 1 | `public String unit()` |  |
| 1182 | 1 | `public double worldImportPrice()` | What the world charges for one DELIVERED HERE, in ITS money. |
| 1185 | 1 | `public double worldExportPrice()` | What the world pays for one DELIVERED THERE, in ITS money. |
| 1196 | 1 | `public double baseFreight()` | What it costs to move one unit between the city and the world, in the world's money - three quarters of the wedge on every good that has both ends, and zero for the things nobody ships. |
| 1205 | 1 | `public double worldBuyPrice()` | The world's own ask, before anything is moved - the import price less the freight in it. |
| 1216 | 1 | `public double worldSellPrice()` | The world's own bid, before anything is moved - the export price with the freight added back. |
| 1218 | 1 | `public boolean importable()` |  |
| 1219 | 1 | `public boolean exportable()` |  |
| 1220 | 1 | `public boolean stockable()` |  |
| 1221 | 1 | `public Pricing pricing()` |  |
| 1224 | 1 | `public boolean taxExempt()` | True for a supply the sales tax never touches. |
| 1235 | 1 | `public int planningMonths()` | How many months of the city's take a maker averages before it plans a plant against it. |
| 1238 | 1 | `public boolean traded()` | Clears in the band on scarcity, as opposed to being priced by its seller. |

### WHAT IT TAKES TO CARRY ONE (2026-09-16) (lines 1240-1416)

| line | len | member | says |
|---:|---:|---|---|
| 1266 | 34 | `public double tonnesPerUnit()` | One unit, in tonnes. |
| 1317 | 40 | `public Traffic traffic()` | Which stream of traffic a tonne of this joins, or null for the things that never take up road at all. |
| 1379 | 30 | `public Ports.Cargo cargo()` | What kind of ship carries it, and so which terminal's berth (0.7.86, batch O9; docs/studies/spec-oil.md 2.1, Ports.Cargo), or null for the things that never cross the boundary as freight. |
| 1411 | 5 | `public static Good byName(String name)` | The good with this saved name, or null - a save from a build without it loses that line, not the load. |

