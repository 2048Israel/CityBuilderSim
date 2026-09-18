# Sectors.java - 261 lines · 31 methods · 2 constants · model

`ham/citybuildersim/Sectors.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (14), [SectorState](SectorState.md) (4), [Retail](Retail.md) (3), [RealEstate](RealEstate.md) (3), [FoodIndustry](FoodIndustry.md) (3), [Construction](Construction.md) (3), [HeavyIndustry](HeavyIndustry.md) (3), [Mining](Mining.md) (3), [Materials](Materials.md) (3), [BusinessServices](BusinessServices.md) (3), [Manufacturing](Manufacturing.md) (3), [Agriculture](Agriculture.md) (3), [FoodProcessing](FoodProcessing.md) (3), [Rail](Rail.md) (3), [Automotive](Automotive.md) (3), [LuxuryRetail](LuxuryRetail.md) (3), [Restaurants](Restaurants.md) (3), [BuildingManager](BuildingManager.md) (2), [Markets](Markets.md) (2), [Game](Game.md) (1), [BuildingsTemplate](BuildingsTemplate.md) (1)

**Used by (36):** [AgricultureCheck](AgricultureCheck.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BooksCheck](BooksCheck.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ForeignCheck](ForeignCheck.md), [Game](Game.md), [HouseholdCheck](HouseholdCheck.md), [HousingCheck](HousingCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MiningCheck](MiningCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [Offending](Offending.md), [OutwardInvestment](OutwardInvestment.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorBooksCheck](SectorBooksCheck.md), [TaxPolicy](TaxPolicy.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 225 | · the loops |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `Sectors.RETAIL` | `"Retail", REAL_ESTATE = "Real Estate", INDUSTRY = "Industry", CONSTRUCTION = ...` | The names, in the order, known before any instance exists - for the things that size an array by the count at construction (Equity's company list, the households' share cells) and cannot wait for a registry to be built. |
| 75 | `Sectors.KEYS` | `{ RETAIL, REAL_ESTATE, INDUSTRY, CONSTRUCTION, HEAVY_INDUSTRY, MINING, MATERI...` | ON THE END, AND IT HAS TO STAY THAT WAY - but for a softer reason than BuildingType's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `private final List<Sector> all` |  |
| 82 | `private final Map<String, Sector> byKey` |  |
| 84 | `private final Retail retail` |  |
| 85 | `private final RealEstate realEstate` |  |
| 86 | `private final FoodIndustry industry` |  |
| 87 | `private final Construction construction` |  |
| 88 | `private final HeavyIndustry heavyIndustry` |  |
| 89 | `private final Mining mining` |  |
| 90 | `private final Materials materials` |  |
| 91 | `private final BusinessServices businessServices` |  |
| 92 | `private final Manufacturing manufacturing` |  |
| 93 | `private final Agriculture agriculture` |  |
| 94 | `private final FoodProcessing foodProcessing` |  |
| 95 | `private final Rail rail` |  |
| 96 | `private final Automotive automotive` |  |
| 97 | `private final LuxuryRetail luxuryRetail` |  |
| 98 | `private final Restaurants restaurants` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 213 | **type** `public final class Sectors` | Every sector in the city, in one order, by one name. |
| 100 | 25 | `public Sectors(BuildingManager buildings, Markets markets)` |  |
| 126 | 9 | `private<T extends Sector> T add(T sector, BuildingManager buildings, Markets markets)` |  |
| 136 | 1 | `public List<Sector> all()` |  |
| 137 | 1 | `public int size()` |  |
| 138 | 1 | `public Sector get(int i)` |  |
| 141 | 1 | `public Sector byKey(String key)` | The sector with this saved name, or null - a name from a save this build does not have loses that line, not the load. |
| 143 | 4 | `public int indexOf(String key)` |  |
| 149 | 5 | `public String[] keys()` | The keys, in order. |
| 155 | 1 | `public Retail retail()` |  |
| 156 | 1 | `public RealEstate realEstate()` |  |
| 157 | 1 | `public FoodIndustry industry()` |  |
| 158 | 1 | `public Construction construction()` |  |
| 159 | 1 | `public HeavyIndustry heavyIndustry()` |  |
| 160 | 1 | `public Mining mining()` |  |
| 161 | 1 | `public Materials materials()` |  |
| 168 | 1 | `public BusinessServices businessServices()` | Typed, unlike the rest of the ordinary six, because the labour market and the playtest both want to ask it a question no other sector answers: what share of what the world pays is going out in wages. |
| 176 | 1 | `public Manufacturing manufacturing()` | Typed for the same reason, and for one more: it is the only sector that BUYS a traded good another sector makes, so the mills' screen and the playtest both want to ask it what it is paying for steel. |
| 183 | 1 | `public Agriculture agriculture()` | Typed, because the fields answer a question no other sector can: what share of its own dinner the city grows, and what the ground under it is costing. |
| 190 | 1 | `public FoodProcessing foodProcessing()` | Typed, because it is the second sector that BUYS a traded good another sector makes - the Livestock Farm's meat - so the farms' screen and the playtest both want to ask it what it is paying for it. |
| 199 | 1 | `public Rail rail()` | Typed, and for a reason none of the others have: it is the only sector whose price is a fact about every OTHER sector's trade. |
| 207 | 1 | `public Automotive automotive()` | Typed, because it is the only sector that buys what Manufacturing makes, and the mills' screen and the planner both want to ask what the parts are costing. |
| 210 | 1 | `public LuxuryRetail luxuryRetail()` | ...and the shops that sell the city its watches. |
| 213 | 1 | `public Restaurants restaurants()` | ...and the kitchens, which sell the city its own food cooked. |
| 216 | 3 | `public void attachGame(Game game)` | The city, handed to every sector once it exists. |
| 221 | 3 | `public Sector ownerOf(BuildingsTemplate t)` | The sector that owns a building, or null for the city's own. |

### the loops (lines 225-261)

| line | len | member | says |
|---:|---:|---|---|
| 227 | 5 | `public double totalCash()` |  |
| 233 | 5 | `public double totalPayroll()` |  |
| 239 | 5 | `public List<SectorState> toState()` |  |
| 245 | 8 | `public void restore(List<SectorState> saved)` |  |
| 254 | 3 | `public void reset()` |  |
| 258 | 3 | `public void redenominate(double scale)` |  |

