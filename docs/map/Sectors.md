# Sectors.java - 355 lines · 36 methods · 3 constants · model

`ham/citybuildersim/Sectors.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (18), [Good](Good.md) (15), [SectorState](SectorState.md) (4), [Retail](Retail.md) (3), [RealEstate](RealEstate.md) (3), [FoodIndustry](FoodIndustry.md) (3), [Construction](Construction.md) (3), [HeavyIndustry](HeavyIndustry.md) (3), [Mining](Mining.md) (3), [Materials](Materials.md) (3), [BusinessServices](BusinessServices.md) (3), [Manufacturing](Manufacturing.md) (3), [Agriculture](Agriculture.md) (3), [FoodProcessing](FoodProcessing.md) (3), [Rail](Rail.md) (3), [Automotive](Automotive.md) (3), [LuxuryRetail](LuxuryRetail.md) (3), [Restaurants](Restaurants.md) (3), [BuildingManager](BuildingManager.md) (2), [Markets](Markets.md) (2), [Game](Game.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1), [Statement](Statement.md) (1)

**Used by (52):** [AgricultureCheck](AgricultureCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BooksCheck](BooksCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [ChartCheck](ChartCheck.md), [ConservationCheck](ConservationCheck.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ForeignCheck](ForeignCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [HistoryScreen](HistoryScreen.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [Icons](Icons.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MiningCheck](MiningCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Offending](Offending.md), [OutwardInvestment](OutwardInvestment.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [TaxPolicy](TaxPolicy.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 226 | · the loops |
| 240 | · the month across the edge, by good |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `Sectors.RETAIL` | `"Retail", REAL_ESTATE = "Real Estate", INDUSTRY = "Industry", CONSTRUCTION = ...` | The names, in the order, known before any instance exists - for the things that size an array by the count at construction (Equity's company list, the households' share cells) and cannot wait for a registry to be built. |
| 76 | `Sectors.KEYS` | `{ RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERI...` | ON THE END, AND IT HAS TO STAY THAT WAY - but for a softer reason than BuildingType's. |
| 243 | `Sectors.HOUSEHOLDS` | `"Households"` | The name the households' own imports are kept under among a good's buyers: the cars they buy from the world (Game.getHouseholdCarImports()). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 82 | `private final List<Sector> all` |  |
| 83 | `private final Map<String, Sector> byKey` |  |
| 85 | `private final Retail retail` |  |
| 86 | `private final RealEstate realEstate` |  |
| 87 | `private final FoodIndustry industry` |  |
| 88 | `private final Construction construction` |  |
| 89 | `private final HeavyIndustry heavyIndustry` |  |
| 90 | `private final Mining mining` |  |
| 91 | `private final Materials materials` |  |
| 92 | `private final BusinessServices businessServices` |  |
| 93 | `private final Manufacturing manufacturing` |  |
| 94 | `private final Agriculture agriculture` |  |
| 95 | `private final FoodProcessing foodProcessing` |  |
| 96 | `private final Rail rail` |  |
| 97 | `private final Automotive automotive` |  |
| 98 | `private final LuxuryRetail luxuryRetail` |  |
| 99 | `private final Restaurants restaurants` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 306 | **type** `public final class Sectors` | Every sector in the city, in one order, by one name. |
| 101 | 25 | `public Sectors(BuildingManager buildings, Markets markets)` |  |
| 127 | 9 | `private<T extends Sector> T add(T sector, BuildingManager buildings, Markets markets)` |  |
| 137 | 1 | `public List<Sector> all()` |  |
| 138 | 1 | `public int size()` |  |
| 139 | 1 | `public Sector get(int i)` |  |
| 142 | 1 | `public Sector byKey(String key)` | The sector with this saved name, or null - a name from a save this build does not have loses that line, not the load. |
| 144 | 4 | `public int indexOf(String key)` |  |
| 150 | 5 | `public String[] keys()` | The keys, in order. |
| 156 | 1 | `public Retail retail()` |  |
| 157 | 1 | `public RealEstate realEstate()` |  |
| 158 | 1 | `public FoodIndustry industry()` |  |
| 159 | 1 | `public Construction construction()` |  |
| 160 | 1 | `public HeavyIndustry heavyIndustry()` |  |
| 161 | 1 | `public Mining mining()` |  |
| 162 | 1 | `public Materials materials()` |  |
| 169 | 1 | `public BusinessServices businessServices()` | Typed, unlike the rest of the ordinary six, because the labour market and the playtest both want to ask it a question no other sector answers: what share of what the world pays is going out in wages. |
| 177 | 1 | `public Manufacturing manufacturing()` | Typed for the same reason, and for one more: it is the only sector that BUYS a traded good another sector makes, so the mills' screen and the playtest both want to ask it what it is paying for steel. |
| 184 | 1 | `public Agriculture agriculture()` | Typed, because the fields answer a question no other sector can: what share of its own dinner the city grows, and what the ground under it is costing. |
| 191 | 1 | `public FoodProcessing foodProcessing()` | Typed, because it is the second sector that BUYS a traded good another sector makes - the Livestock Farm's meat - so the farms' screen and the playtest both want to ask it what it is paying for it. |
| 200 | 1 | `public Rail rail()` | Typed, and for a reason none of the others have: it is the only sector whose price is a fact about every OTHER sector's trade. |
| 208 | 1 | `public Automotive automotive()` | Typed, because it is the only sector that buys what Manufacturing makes, and the mills' screen and the planner both want to ask what the parts are costing. |
| 211 | 1 | `public LuxuryRetail luxuryRetail()` | ...and the shops that sell the city its watches. |
| 214 | 1 | `public Restaurants restaurants()` | ...and the kitchens, which sell the city its own food cooked. |
| 217 | 3 | `public void attachGame(Game game)` | The city, handed to every sector once it exists. |
| 222 | 3 | `public Sector ownerOf(BuildingsTemplate t)` | The sector that owns a building, or null for the city's own. |

### the loops (lines 226-239)

| line | len | member | says |
|---:|---:|---|---|
| 228 | 5 | `public double totalCash()` |  |
| 234 | 5 | `public double totalPayroll()` |  |

### the month across the edge, by good (lines 240-355)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 5 | **type** `public record GoodTrade(Good good, double sold, double bought, Map<String, Double> sellers, Map<String, Dou...` | One good across the city's edge in the month the books last struck: what was sold of it abroad and bought of it abroad, in money, and by whom - sector keys, and HOUSEHOLDS among the buyers of cars. |
| 253 | 1 | `public double net()` _(in Sectors.GoodTrade)_ | What the city sold of it abroad less what it bought: its row's net. |
| 262 | 17 | **type** `public record TradeByGood(Map<Good, GoodTrade> goods, Map<String, Double> services, double householdCars)` | The month across the edge by good (0.7.35): every good that crossed it, the imports with no good behind them by the sector that bought them (the railway's fuel - Sector.bookImportedService()), and the households' cars... |
| 264 | 5 | `public double sold()` _(in Sectors.TradeByGood)_ | Everything sold abroad: the balance of payments' exports, by its own construction. |
| 270 | 6 | `public double bought()` _(in Sectors.TradeByGood)_ | Everything bought abroad - the goods, the households' cars among them, and the services with no good: its imports. |
| 277 | 1 | `public double balance()` _(in Sectors.TradeByGood)_ | ...the one less the other: the trade balance. |
| 294 | 38 | `public TradeByGood tradeByGood(double householdCars)` | WHAT THE CITY SOLD AND BOUGHT ABROAD, GOOD BY GOOD (0.7.35), read off the statements every sector struck - each line of revenue and of cost split home and abroad as the trade was booked (Sector.Split) - so it foots to... |
| 333 | 5 | `public List<SectorState> toState()` |  |
| 339 | 8 | `public void restore(List<SectorState> saved)` |  |
| 348 | 3 | `public void reset()` |  |
| 352 | 3 | `public void redenominate(double scale)` |  |

