# Sectors.java - 449 lines · 44 methods · 4 constants · model

`ham/citybuildersim/Sectors.java` - generated 2026-10-12 by CodeMap; line numbers are as of that run.

> Every sector in the city, in one order, by one name.
> 
> THE REGISTRY. A sector is added here and nowhere else: the credit desk,
> the register, the exchange, the savings abroad, the tax policy, the VAT,
> the books, the audit, the screens and the save all enumerate this list.
> Until 2026-09-11 the same six were named in eleven loops in
> EconomyManager, a six-way switch in SectorBooks, thirteen enumerations in
> UserInterface and a hundred and ninety reaches in the harnesses - see
> claude/the-sector-template.md.
> 
> THE ORDER IS THE ORDER. Equity's company index, the households' share
> arrays and the audit's pool list all follow it, so a sector goes on the
> END like a BuildingType. The first six are the six the game had, in the
> order BusinessDebtManager.SECTORS listed them; Materials is the seventh
> (Jerus: "a seventh sector"), Business Services the eighth and Manufacturing
> the ninth - the two whose customer is not in the city - and Agriculture the
> tenth, which is the only one whose cost is the ground it stands on.
> 
> THE UNUSUAL ONES have typed accessors, because the households pay rent to
> one and buy groceries from another, the city hands its build orders to a
> third, and the two export sectors each answer a question no other sector
> can. Everything else reaches a sector by name.

**Uses:** [Good](Good.md) (21), [Sector](Sector.md) (18), [SectorState](SectorState.md) (4), [Retail](Retail.md) (3), [RealEstate](RealEstate.md) (3), [FoodIndustry](FoodIndustry.md) (3), [Construction](Construction.md) (3), [HeavyIndustry](HeavyIndustry.md) (3), [Mining](Mining.md) (3), [Materials](Materials.md) (3), [BusinessServices](BusinessServices.md) (3), [Manufacturing](Manufacturing.md) (3), [Agriculture](Agriculture.md) (3), [FoodProcessing](FoodProcessing.md) (3), [Rail](Rail.md) (3), [Automotive](Automotive.md) (3), [LuxuryRetail](LuxuryRetail.md) (3), [Restaurants](Restaurants.md) (3), [Oil](Oil.md) (3), [Refining](Refining.md) (3), [CoalMining](CoalMining.md) (3), [Quarrying](Quarrying.md) (3), [CementConcrete](CementConcrete.md) (3), [CopperMining](CopperMining.md) (3), [CopperRefining](CopperRefining.md) (3), [BuildingManager](BuildingManager.md) (2), [Markets](Markets.md) (2), [Game](Game.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [Statement](Statement.md) (1)

**Used by (92):** [AgricultureCheck](AgricultureCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CementConcrete](CementConcrete.md), [CementConcreteCheck](CementConcreteCheck.md), [ChartCheck](ChartCheck.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ForeignCheck](ForeignCheck.md), [FuelSplit](FuelSplit.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [HistoryScreen](HistoryScreen.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [Icons](Icons.md), [InvestCheck](InvestCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Offending](Offending.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PortCheck](PortCheck.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [RestaurantsCheck](RestaurantsCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TaxPolicy](TaxPolicy.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [VanCheck](VanCheck.md), [WellCheck](WellCheck.md), [Zoning](Zoning.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 286 | · the loops |
| 300 | · the month across the edge, by good |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 65 | `Sectors.RETAIL` | `"Retail", REAL_ESTATE = "Real Estate", INDUSTRY = "Industry", CONSTRUCTION = ...` | The names, in the order, known before any instance exists - for the things that size an array by the count at construction (Equity's company list, the households' share cells) and cannot wait for a registry to be built. |
| 89 | `Sectors.KEYS` | `{ RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERI...` | ON THE END, AND IT HAS TO STAY THAT WAY - but for a softer reason than BuildingType's. |
| 303 | `Sectors.HOUSEHOLDS` | `"Households"` | The name the households' own imports are kept under among a good's buyers: the cars they buy from the world (Game.getHouseholdCarImports()). |
| 306 | `Sectors.CITY` | `"City"` | ...and the city's, among crude's buyers and sellers: its strategic reserve (0.7.85; StrategicReserve). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 103 | `private final List<Sector> all` |  |
| 104 | `private final Map<String, Sector> byKey` |  |
| 106 | `private final Retail retail` |  |
| 107 | `private final RealEstate realEstate` |  |
| 108 | `private final FoodIndustry industry` |  |
| 109 | `private final Construction construction` |  |
| 110 | `private final HeavyIndustry heavyIndustry` |  |
| 111 | `private final Mining mining` |  |
| 112 | `private final Materials materials` |  |
| 113 | `private final BusinessServices businessServices` |  |
| 114 | `private final Manufacturing manufacturing` |  |
| 115 | `private final Agriculture agriculture` |  |
| 116 | `private final FoodProcessing foodProcessing` |  |
| 117 | `private final Rail rail` |  |
| 118 | `private final Automotive automotive` |  |
| 119 | `private final LuxuryRetail luxuryRetail` |  |
| 120 | `private final Restaurants restaurants` |  |
| 121 | `private final Oil oil` |  |
| 122 | `private final Refining refining` |  |
| 123 | `private final CoalMining coalMining` |  |
| 124 | `private final Quarrying quarrying` |  |
| 125 | `private final CementConcrete cementConcrete` |  |
| 126 | `private final CopperMining copperMining` |  |
| 127 | `private final CopperRefining copperRefining` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 57 | 393 | **type** `public final class Sectors` | Every sector in the city, in one order, by one name. |
| 129 | 32 | `public Sectors(BuildingManager buildings, Markets markets)` |  |
| 162 | 9 | `private<T extends Sector> T add(T sector, BuildingManager buildings, Markets markets)` |  |
| 172 | 1 | `public List<Sector> all()` |  |
| 173 | 1 | `public int size()` |  |
| 174 | 1 | `public Sector get(int i)` |  |
| 177 | 1 | `public Sector byKey(String key)` | The sector with this saved name, or null - a name from a save this build does not have loses that line, not the load. |
| 179 | 4 | `public int indexOf(String key)` |  |
| 185 | 5 | `public String[] keys()` | The keys, in order. |
| 191 | 1 | `public Retail retail()` |  |
| 192 | 1 | `public RealEstate realEstate()` |  |
| 193 | 1 | `public FoodIndustry industry()` |  |
| 194 | 1 | `public Construction construction()` |  |
| 195 | 1 | `public HeavyIndustry heavyIndustry()` |  |
| 196 | 1 | `public Mining mining()` |  |
| 197 | 1 | `public Materials materials()` |  |
| 204 | 1 | `public BusinessServices businessServices()` | Typed, unlike the rest of the ordinary six, because the labour market and the playtest both want to ask it a question no other sector answers: what share of what the world pays is going out in wages. |
| 212 | 1 | `public Manufacturing manufacturing()` | Typed for the same reason, and for one more: it is the only sector that BUYS a traded good another sector makes, so the mills' screen and the playtest both want to ask it what it is paying for steel. |
| 219 | 1 | `public Agriculture agriculture()` | Typed, because the fields answer a question no other sector can: what share of its own dinner the city grows, and what the ground under it is costing. |
| 226 | 1 | `public FoodProcessing foodProcessing()` | Typed, because it is the second sector that BUYS a traded good another sector makes - the Livestock Farm's meat - so the farms' screen and the playtest both want to ask it what it is paying for it. |
| 235 | 1 | `public Rail rail()` | Typed, and for a reason none of the others have: it is the only sector whose price is a fact about every OTHER sector's trade. |
| 243 | 1 | `public Automotive automotive()` | Typed, because it is the only sector that buys what Manufacturing makes, and the mills' screen and the planner both want to ask what the parts are costing. |
| 246 | 1 | `public LuxuryRetail luxuryRetail()` | ...and the shops that sell the city its watches. |
| 249 | 1 | `public Restaurants restaurants()` | ...and the kitchens, which sell the city its own food cooked. |
| 256 | 1 | `public Oil oil()` | Typed, as Mining is, because the ground is its limit: the wells ask the land for the oil they lift, and the refinery's crude comes from them first (0.7.62). |
| 259 | 1 | `public Refining refining()` | ...and the refinery, whose tanks the drivers and the railway draw their fuel from before the world (0.7.62). |
| 262 | 1 | `public CoalMining coalMining()` | ...and the coal mines (0.7.126, batch I6), whose two coals come off one flow - the surface mines', and the underground mines' raw coal through the preparation plants. |
| 265 | 1 | `public Quarrying quarrying()` | ...and the quarries (0.7.131, batch I7), whose limestone the kilns and furnaces buy and the rest sells as aggregate. |
| 268 | 1 | `public CementConcrete cementConcrete()` | ...and the kilns and cement plants (0.7.131, batch I7), which burn limestone into lime and cement. |
| 271 | 1 | `public CopperMining copperMining()` | ...and the copper mines (0.7.133, batch I8): a sulphide field's concentrate floated at the mine, an oxide field's cathode leached there. |
| 274 | 1 | `public CopperRefining copperRefining()` | ...and the smelters, refineries and rod mills (0.7.133, batch I8), which take the mines' concentrate and the city's old copper to cathode and rod. |
| 277 | 3 | `public void attachGame(Game game)` | The city, handed to every sector once it exists. |
| 282 | 3 | `public Sector ownerOf(BuildingsTemplate t)` | The sector that owns a building, or null for the city's own. |

### the loops (lines 286-299)

| line | len | member | says |
|---:|---:|---|---|
| 288 | 5 | `public double totalCash()` |  |
| 294 | 5 | `public double totalPayroll()` |  |

### the month across the edge, by good (lines 300-449)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 5 | **type** `public record GoodTrade(Good good, double sold, double bought, Map<String, Double> sellers, Map<String, Dou...` | One good across the city's edge in the month the books last struck: what was sold of it abroad and bought of it abroad, in money, and by whom - sector keys, and HOUSEHOLDS among the buyers of cars. |
| 316 | 1 | `public double net()` _(in Sectors.GoodTrade)_ | What the city sold of it abroad less what it bought: its row's net. |
| 326 | 18 | **type** `public record TradeByGood(Map<Good, GoodTrade> goods, Map<String, Double> services, double householdCars, d...` | The month across the edge by good (0.7.35): every good that crossed it, the imports with no good behind them by the sector that bought them, and the households' cars and - since 0.7.62, when fuel became a good - their... |
| 329 | 5 | `public double sold()` _(in Sectors.TradeByGood)_ | Everything sold abroad: the balance of payments' exports, by its own construction. |
| 335 | 6 | `public double bought()` _(in Sectors.TradeByGood)_ | Everything bought abroad - the goods, the households' cars among them, and the services with no good: its imports. |
| 342 | 1 | `public double balance()` _(in Sectors.TradeByGood)_ | ...the one less the other: the trade balance. |
| 363 | 3 | `public TradeByGood tradeByGood(double householdCars, double householdFuel)` | WHAT THE CITY SOLD AND BOUGHT ABROAD, GOOD BY GOOD (0.7.35), read off the statements every sector struck - each line of revenue and of cost split home and abroad as the trade was booked (Sector.Split) - so it foots to... |
| 375 | 51 | `public TradeByGood tradeByGood(double householdCars, double householdFuel, double cityCrudeBought, double cityCrudeSold)` | ...and the city's own crude across the edge (0.7.85): its strategic reserve's fill bought from the world and - until 0.7.103, when a release the refiners do not take stays in the tanks (Jerus's A8) - its release shipp... |
| 427 | 5 | `public List<SectorState> toState()` |  |
| 433 | 8 | `public void restore(List<SectorState> saved)` |  |
| 442 | 3 | `public void reset()` |  |
| 446 | 3 | `public void redenominate(double scale)` |  |

