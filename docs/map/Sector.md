# Sector.java - 3,553 lines · 249 methods · 16 constants · model

`ham/citybuildersim/Sector.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> One business in the city, and the template every sector extends.
> 
> WHY (2026-09-11). Jerus: "we should make a single template, and every
> single sector just extends it, and adds or overrides, right? cause in the
> future there will be over a thousand or near a thousand sectors." Until
> this class the six sectors were five handlers in five shapes, named by hand
> in a hundred places; a seventh would have been a week of finding them. See
> claude/the-sector-template.md for the design and the decisions.
> 
> WHAT EVERY SECTOR HAS, written once here:
> 
>   identity      a key (its saved name), a label, the build-menu group its
>                 buildings sit under
>   buildings     every template in buildings.json names its owner; the
>                 sector reads capacity, jobs, land, book value, power, water
>                 and road load off its own stacks
>   production    the goods it makes and the goods it uses, in units per
>                 building per month from the templates; the operating rate
>                 (staffing x power x water x roads x sickness) that scales
>                 output, and payroll that scales with staffing alone; what
>                 its plant makes of a stockable good this month,
>                 madeAtRate() (0.7.126), a hook a sector whose ground
>                 limits that good overrides (Coal Mining's coals, Copper
>                 Mining's cathode)
>   stock         a warehouse per stockable output, a pantry per input it
>                 buys ahead of using
>   books         one income statement in one order for everyone; the cash
>                 it keeps; the balance-sheet inputs the city pushes in
>   the ledger    every sale and every purchase of the month, by counterparty,
>                 which is what the statement, the VAT and the accounts read
>   credit, listing, savings abroad - all keyed by the sector's name in
>                 BusinessDebtManager, Equity, Exchange and OutwardInvestment,
>                 none of which know or care what the sector makes
>   planning      the generic expansion decision, and the shrinking rules
> 
> A SECTOR CLASS IS A DECLARATION. Twenty lines, in ham.citybuildersim.sectors,
> one file each: what it makes, what it uses, what it stocks, and any hook it
> overrides. The quantities come from the buildings and the prices from the
> goods, so a sector that is nothing but a factory needs no code of its own
> at all - see sectors.CementConcrete for the smallest one (0.7.131; a works
> on ProcessingSector), sectors.Mining for the smallest mine and
> sectors.Refining for the largest.
> 
> THE MONTH, in the order Sectors runs it (the same order the handlers ran
> in, so nothing about when money moves has changed):
> 
>   top of the month   bill(): interest, property tax and maintenance are
>                      set for the month; strike(): the statement is struck
>                      from LAST month's trades and THIS month's bills, the
>                      VAT is settled from the same figures, and the month
>                      is banked to cash. That is what the handlers did -
>                      they struck at the top, off the month before.
>   middle             the simulation refreshes wages, ratios, capacities.
>   bottom             Markets.clearMonth(): the seller-priced goods are sold
>                      (the shops to the households, the landlords' doors),
>                      then every traded good is priced, offered, bid for,
>                      allocated, imported and exported, and the makers
>                      produce for next month. Every fill lands in a ledger.
> 
> MONEY MOVES AT THE STRIKE AND NOWHERE ELSE in this class. A trade is a
> ... (4 more lines in the source)

**Uses:** [Good](Good.md) (182), [GoodsMarket](GoodsMarket.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (12), [WageBand](WageBand.md) (10), [SupplierCredit](SupplierCredit.md) (10), [Game](Game.md) (9), [Statement](Statement.md) (9), [Trade](Trade.md) (8), [SectorState](SectorState.md) (7), [Markets](Markets.md) (5), [Traffic](Traffic.md) (5), [BuildingManager](BuildingManager.md) (4), [JobType](JobType.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [BuildingType](BuildingType.md) (3), [BalanceSheet](BalanceSheet.md) (3), [Haulage](Haulage.md) (2), [Freight](Freight.md) (2), [Sectors](Sectors.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [PopulationManager](PopulationManager.md) (1), [Migration](Migration.md) (1), [Mortgage](Mortgage.md) (1), [OldMetal](OldMetal.md) (1), [EconomyManager](EconomyManager.md) (1), [ProcessingSector](ProcessingSector.md) (1)

**Used by (135):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [ForeignCheck](ForeignCheck.md), [ForestryCheck](ForestryCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [HomeRailCheck](HomeRailCheck.md), [HousingCheck](HousingCheck.md), [Icons](Icons.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapView](MapView.md), [Markets](Markets.md), [Materials](Materials.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [NewGameCheck](NewGameCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilViewCheck](OilViewCheck.md), [OreView](OreView.md), [OutwardInvestment](OutwardInvestment.md), [Pieces](Pieces.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [ProcessingSector](ProcessingSector.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [ReloadCheck](ReloadCheck.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [SlopeCheck](SlopeCheck.md), [SpreadPlanner](SpreadPlanner.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TaxPolicy](TaxPolicy.md), [TradeScreen](TradeScreen.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md)

## Sections

| line | section |
|---:|---|
| 81 | IDENTITY AND DECLARATION |
| 152 | WIRING - set once by Sectors, through attach() |
| 172 | LABOUR |
| 215 | A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved |
| 286 | EVERY PLANNER THAT BUILDS POSTS ASKS, AND THE FIFTH IT LEAVES IS FRICTION |
| 557 | UTILISATION |
| 609 | · THE POWER ITS WORKS MAKE THEMSELVES (0.7.112, batch I2; runs/spec- |
| 637 | · WHAT ITS WORKS BUY ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; |
| 676 | THE FLEET (2026-09-17) |
| 749 | · AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL |
| 792 | · AND THE DIESEL THEY BURN (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) |
| 1011 | MONEY AND THE BILLS |
| 1055 | · WHAT ITS BORROWING COST IT UP FRONT, EXPENSED AND DEDUCTED (0.7.102) |
| 1199 | STOCK |
| 1220 | CAPACITY, off the buildings |
| 1239 | THE MONTH'S PRODUCTION FIGURES, per output good |
| 1320 | THE MONTH'S TRADE ACROSS A SAVE (A1, 2026-10-05) |
| 1357 | WHAT A MONTH WROTE OFF, PAST ITS STRIKE (0.7.133, batch I8) |
| 1429 | THE LEDGER |
| 1637 | BUY ONLY WHAT IT CAN PAY FOR (0.7.12, round 6) |
| 1749 | ITS SUPPLIERS' CREDIT (0.7.44) |
| 1987 | THE STATEMENT |
| 2239 | THE MONTH AT THE BOTTOM - HOOKS Markets CALLS |
| 2328 | WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS |
| 2523 | CAPTIVE USE (0.7.112, batch I2; docs/studies/spec-materials.md 2.7) |
| 2566 | THE ORDER (WN3, 2026-10-29; the ensemble's negative GDP in months |
| 2996 | PLANNING - the decision to grow, and to shrink |
| 3100 | THE SCREENS |
| 3320 | SAVE AND RESTORE |
| 3525 | THE POWER, FOR THE PICTOGRAMS (0.7.141, batch I11; IndustryView) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3110 | `Sector.Line.Kind.HEAD` |  |
| 3110 | `Sector.Line.Kind.LINE` |  |
| 3110 | `Sector.Line.Kind.NOTE` |  |
| 3111 | `Sector.Line.Tone.NONE` |  |
| 3111 | `Sector.Line.Tone.GOOD` |  |
| 3111 | `Sector.Line.Tone.WARN` |  |
| 3111 | `Sector.Line.Tone.BAD` |  |
| 3111 | `Sector.Line.Tone.MUTED` |  |
| 3111 | `Sector.Line.Tone.HEAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 247 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 337 | `Sector.Staffing.ANY` | `new Staffing(1, new double [ 0 ], new double [ 0 ], new double [ 0 ], new boo...` | Always staffable: a building with no posts, or nobody to ask. |
| 654 | `Sector.CONSUMABLES` | `"Consumables"` | The consumables' name on a sector's input line. |
| 741 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 747 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 776 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 790 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 804 | `Sector.LOADS_A_VAN_MONTH` | `TONNES_PER_VAN / Good.VANS.tonnesPerUnit()` | The loads a van-month carries: TONNES_PER_VAN at a van's own weight, three tonnes (Good.VANS.tonnesPerUnit()) - forty. |
| 807 | `Sector.KM_A_LOAD` | `50` | How far a load goes, there and back: fifty kilometres (docs/studies/spec-oil.md 2.5, est.; to confirm). |
| 810 | `Sector.DIESEL_LITRES_PER_100_KM` | `12` | What a laden van or truck burns: twelve litres of diesel a hundred kilometres (docs/studies/spec-oil.md 2.5, est.; to confirm). |
| 813 | `Sector.DIESEL_LITRES_A_VAN_MONTH` | `LOADS_A_VAN_MONTH * KM_A_LOAD * DIESEL_LITRES_PER_100_KM / 100` | ...so a van-month burns LOADS_A_VAN_MONTH x KM_A_LOAD x DIESEL_LITRES_PER_100_KM / 100 litres: 240. |
| 1098 | `Sector.BORROWING_COST_MOST_MONTHS` | `Mortgage.MORTGAGE_AMORTIZATION_MONTHS` | The longest life a cost of borrowing is expensed over, in months: a mortgage's amortization, forty years (0.7.102, A16) - the clock's length. |
| 2253 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 2256 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |
| 2644 | `Sector.FLOW_EXPORT_HEADROOM` | `.02` | The margin over its inputs' cost a works aims next month's export price at on the world's slope (FL1): a month's drift in the band then leaves this month's price over the cost, so the line does not stop every other mo... |
| 2880 | `Sector.UNSOLD_DUST` | `1e-9` | What of a flow good's output may go unsold as rounding, a share of what was made: under it the remainder is idled, not exported (EM2). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 85 | `private final String key` |  |
| 86 | `private final String label` |  |
| 87 | `private final BuildingType group` |  |
| 88 | `private String blurb` |  |
| 90 | `private final Set<Good> makes` |  |
| 91 | `private final Set<Good> uses` |  |
| 94 | `private final Map<Good, Double> pantryMonths` | Inputs it buys ahead of using, and how many months of use it keeps. |
| 156 | `protected BuildingManager buildings` |  |
| 157 | `protected Markets markets` |  |
| 160 | `protected Game game` | The city, for the few hooks that need more than the buildings and the markets. |
| 177 | `protected final double[] wages` | Payroll per tier at full staffing: wage x posts. |
| 178 | `protected final long[] jobs` |  |
| 179 | `protected final double[] fill` |  |
| 182 | `protected double averageFill` | Share of its posts that are filled. |
| 332 | `public final double share` | The share of its posts the city's spare workers could fill. |
| 334 | `public final WageBand unfillable` | A band it has posts in that nobody can fill (see passes()), or null. |
| 571 | `protected double energyRatio` |  |
| 572 | `protected double electricity, water` |  |
| 573 | `protected double pricePerWatt, pricePerWaterUnit` |  |
| 627 | `protected double powerSurplus` | kW its buildings make past their own draws, at nameplate - what it sells the grid. |
| 816 | `private double fleetDieselLitres, fleetDieselCost, fleetDieselImported` | The month's diesel for the fleet (0.7.83): the litres drawn, what they cost, and the part bought abroad. |
| 835 | `protected boolean vansKnown` | Whether this sector's fleet is a fact about the sector rather than a fact about the version it was saved from. |
| 1015 | `private double cash` |  |
| 1018 | `private double interestExpense` | Set for the month at the top of it, before the statement runs. |
| 1019 | `private double propertyTaxExpense` |  |
| 1020 | `private double maintenanceExpense` |  |
| 1022 | `private double foreignIncome` | The coupon on its dollars abroad this month, local money (HD1, HD2). |
| 1025 | `private double taxRate` | The profit rate in force this month. |
| 1027 | `private double landValue, buildingsValue, bondsPayable` |  |
| 1101 | `private final double[] borrowingChargesLeft` | The month's charge by how many months it has still to run: index k, k months; nothing at 0. |
| 1204 | `protected final Map<Good, Double> stock` | Output on hand, per stockable good it makes. |
| 1207 | `protected final Map<Good, Double> pantry` | Input on hand, per good it keeps a pantry of. |
| 1245 | `public double capacity` | nameplate a month |
| 1246 | `public double planned` | nameplate a month |
| 1247 | `public double produced` | what it decided to make for home |
| 1248 | `public double idled` | what went into stock or to market |
| 1249 | `public double exportBound` | nameplate neither the city nor the world would take |
| 1250 | `public double offered` | made straight for the ship |
| 1251 | `public double withheld` | released to the market at the price |
| 1252 | `public double soldLocal` | kept back below cost |
| 1253 | `public double exported` | units taken by local buyers |
| 1254 | `public double writtenOff` | units shipped, from the line or the shed |
| 1255 | `public double costPerUnit` | stock lost to a demolished warehouse |
| 1256 | `public double captive` | the month's break-even, for the screen |
| 1261 | `public double needed` | at this month's operating rate |
| 1262 | `public double bid` | at this month's operating rate |
| 1263 | `public double boughtLocal` | what it asked the market for |
| 1264 | `public double imported` | filled at home |
| 1267 | `protected final Map<Good, Output> outputs` |  |
| 1268 | `protected final Map<Good, Input> inputs` |  |
| 1339 | `private final Map<Good, Double> carriedExports` | Units shipped in the month a save was taken, by good: read by unitsExported() until the first strike after the load. |
| 1342 | `private final Map<Good, Double> carriedImports` | ...and units landed, read by unitsImported(). |
| 1345 | `private final Map<Good, Double> carriedBoughtLocal` | ...and units bought at home (FR2), read by unitsBoughtLocal(): the city-leg tonnes Haulage bills at the top of the next month. |
| 1374 | `private final Map<Good, Double> carriedWrittenOff` | Units written off in the month a save was taken, by good: read by unitsWrittenOff() until the first strike after the load. |
| 1377 | `private final Map<Good, Double> struckWrittenOff` | ...and the units written off in the month the last strike banked, by good: what the accounts add back. |
| 1394 | `private boolean tradeToDerive` | True from the load of a save that carried the month's trade in money alone until deriveCarriedTrade() has read it. |
| 1457 | `public double atHome` |  |
| 1458 | `public double abroad` |  |
| 1464 | `public double localSales` | Sold to local buyers - sectors, households, the city - in money. |
| 1466 | `public double exports` | Sold to the world. |
| 1468 | `public double otherRevenue` | Revenue that is not a sale of a good: recognised building work, repairs billed. |
| 1470 | `public final Map<String, Double> purchasesBySupplier` | Bought from each local supplier, by the supplier's key: the input tax credit is at THEIR rate. |
| 1472 | `public double imports` | Bought from the world. |
| 1474 | `public final Map<Good, Double> unitsSold` | Units sold and bought, per good, for the accounts and the screens. |
| 1475 | `public final Map<Good, Double> unitsBought` |  |
| 1483 | `public final Map<Good, Split> sold` | And the same two in MONEY, split home and abroad. |
| 1484 | `public final Map<Good, Split> bought` |  |
| 1503 | `public final Map<String, Double> otherInputs` | ...and the part of the input line that is NOT a good, by name. |
| 1515 | `public double paidEarlier` | ...AND STOCK IT PAID FOR EARLIER (0.7.8): an input drawn this month out of stock the sector bought in an earlier month and paid cash for then - the builders' material from scrapped plant, booked at what they paid for ... |
| 1528 | `public final Map<String, Double> capitalBySupplier` | ...AND BUILDINGS IT BOUGHT FROM ANOTHER BUSINESS, by the supplier's key (0.7.19): the builders' contract work on its own premises and the material escalation on it, as the builders bill them. |
| 1533 | `public double salesToHouseholds` | Sold to households in particular - consumption, in the national accounts. |
| 1535 | `public double oldMetalSold` | The part of otherRevenue that is its old steel sold to the Scrap Yards (0.7.123, batch I4; sellOldMetal()), named apart at the strike. |
| 1543 | `public double paidToCity` | What it pays the city this month for a service the city sells and no business does (FR3, payTheCity()): the port dues. |
| 1597 | `private Ledger pending` |  |
| 1699 | `private double purchasesLeft` | What the clearing lets it spend, what is left of it, and the share of each order it funds - and of each order its suppliers' credit covers (0.7.44). |
| 1700 | `private double purchaseShare` |  |
| 1702 | `private double rPurchaseBudget` | The clearing's reading, for the screens and the playtest. |
| 1703 | `private final Map<Good, Double> rForgoneUnits` |  |
| 1704 | `private final Map<Good, Double> rForgoneValue` |  |
| 1849 | `private double rShelfShort, rShelfShortValue` | What the shelf could not sell this month (0.7.12 round 6): customers the counter and the staff could have served and the stock could not, in what the sector sells, and their value at its price. |
| 1996 | `public double revenue, inputs, payroll, electricity, water, maintenance` |  |
| 1997 | `public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome` |  |
| 1999 | `public double localSales, exports, otherRevenue, salesToHouseholds` | The two halves of revenue, for the accounts. |
| 2001 | `public double localPurchases, imports` | The two halves of inputs, for the accounts and the audit. |
| 2002 | `public Map<String, Double> purchasesBySupplier` |  |
| 2009 | `public Map<Good, Split> sold` | Revenue and the cost of sales BY GOOD, each split home and abroad - what the income statement's two biggest lines open into. |
| 2010 | `public Map<Good, Split> bought` |  |
| 2017 | `public Map<String, Double> otherParts` | And the parts of otherRevenue that have names - the builders' work recognised and repairs billed. |
| 2023 | `public Map<String, Double> otherInputs` | ...and the parts of the INPUT line that are not a good: haulage on the shippers' books, fuel on the railway's. |
| 2025 | `public double paidEarlier` | The part of inputs drawn from stock paid for in an earlier month - see Ledger.paidEarlier. |
| 2027 | `public double borrowingCosts` | What its borrowing cost it up front, the month's share of each over its debt's life (0.7.102, A16): a cost before the profit tax, paid when it borrowed, so added back to the cash at bank(). |
| 2029 | `public Map<String, Double> capitalBySupplier` | Buildings bought from other businesses this month, by supplier (0.7.19) - see Ledger.capitalBySupplier. |
| 2037 | `public double capitalTaxCredit` | The sales tax credited back on them (0.7.19): inside the net the ledger settled, and NOT in salesTax - it was capitalised with the building, not expensed, so crediting it through the tax line would have made it profit... |
| 2047 | `public double powerSold` | What it sold the grid of the power its works make past their own draws (0.7.112, batch I2; spec-materials 2.8): the surplus at the tariff, IN REVENUE - so every line the books strike from revenue adds up with it - and... |
| 2049 | `public double foreignIncome` | The coupon on its dollars abroad (HD1, HD2): a line of finance income BELOW the operating line and inside preTaxIncome, so the profit tax and the dividend see it. |
| 2070 | `private final Statement statement` |  |
| 2451 | `protected final Map<Good, Double> pantryUsedLastMonth` |  |
| 2647 | `private final Map<Good, Double> orderShares` | The share of each traded good's nameplate this month's inputs are ordered for (THE ORDER; a flow good's since FL1): empty outside the market pass, never saved - it is decided afresh each month. |
| 2650 | `private final Map<Good, Double> flowCut` | The units of a flow good this month's order cut from its nameplate at the rate (FL1): only a cut over 0 is here, empty outside the market pass and never saved. |
| 2653 | `private boolean ordering` | Whether the market pass is on and its shares apply (THE ORDER). |
| 2656 | `private final Set<Good> planning` | The goods whose share is being worked out, so a chain that loops back to one reads 1 for it (THE ORDER). |
| 2659 | `private boolean deciding` | Whether planOrders() is at work: only then is a share worked out, so a screen reading the month never touches the shed (THE ORDER). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 3475 | **type** `public abstract class Sector` | One business in the city, and the template every sector extends. |

### IDENTITY AND DECLARATION (lines 81-151)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 14 | `protected Sector(String key, String label, BuildingType group)` | "Construction", "Heavy Industry", "Mining" are what every save already calls the six; a new sector picks a name and keeps it for ever. |
| 120 | 1 | `protected final void makes(Good good)` |  |
| 121 | 1 | `protected final void uses(Good good)` |  |
| 124 | 4 | `protected final void pantry(Good good, double coverMonths)` | It keeps this many months of use of an input on hand, buying the difference each month. |
| 129 | 1 | `protected final void blurb(String text)` |  |
| 131 | 1 | `public final String key()` |  |
| 132 | 1 | `public final String label()` |  |
| 133 | 1 | `public final BuildingType group()` |  |
| 134 | 1 | `public final String blurb()` |  |
| 135 | 1 | `public final Set<Good> goodsMade()` |  |
| 136 | 1 | `public final Set<Good> goodsUsed()` |  |
| 137 | 1 | `public final boolean isMaker(Good g)` |  |
| 138 | 1 | `public final boolean isUser(Good g)` |  |
| 140 | 1 | `public final boolean hasPantry(Good g)` | True for a good it keeps on hand and restocks - a shelf, a larder, a fleet - and so, since 0.7.12 round 6, what a budget can cut (BUY ONLY WHAT IT CAN PAY FOR); false for a maker's input, consumed as it makes. |
| 150 | 1 | `public boolean buysAhead(Good g)` | Whether its order of a good is for stock it keeps - cut, with the rest of its stock, to what it can pay for (BUY ONLY WHAT IT CAN PAY FOR) - rather than a maker's input bought whole (0.7.85): its pantry goods (hasPant... |

### WIRING - set once by Sectors, through attach() (lines 152-171)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 4 | `void attach(BuildingManager buildings, Markets markets)` |  |
| 167 | 1 | `void attachGame(Game game)` |  |
| 169 | 1 | `public BuildingManager buildings()` |  |
| 170 | 1 | `public Markets markets()` |  |

### LABOUR (lines 172-214)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 4 | `public void updateJobFillRate(double[] fillRate)` | The fill rate per tier, as PopulationManager last set it. |
| 196 | 13 | `public void updateWages(double[] wagePerType, long[] posts)` | The wage bill, from the schedule and this sector's own posts. |
| 211 | 3 | `public long[] postsPerTier()` | Posts per tier, off the buildings it operates: its own and, for Freight, the city's sea terminals (BuildingsTemplate.getOperator(), FR3). |

### A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved (lines 215-285)

| line | len | member | says |
|---:|---:|---|---|
| 282 | 3 | `public double staffableShare(BuildingsTemplate t)` | The share of a building's posts this city could actually fill today. |

### EVERY PLANNER THAT BUILDS POSTS ASKS, AND THE FIFTH IT LEAVES IS FRICTION (lines 286-556)

| line | len | member | says |
|---:|---:|---|---|
| 330 | 56 | **type** `public static final class Staffing` | Whether the city could staff one of a building, and if not, why not (0.7.18). |
| 345 | 8 | `Staffing(double share, double[] wanted, double[] spare, double[] above, boolean[] comeFor)` _(in Sector.Staffing)_ |  |
| 363 | 3 | `static boolean nobodyCanFill(double spare, double workersAbove, boolean migrantsComeForIt)` _(in Sector.Staffing)_ | THE ONE DEFINITION OF "NOBODY CAN FILL" (Jerus, "Truly unfillable only"): "A band counts as 'nobody can fill' only when it has no spare workers, nobody above who can step down into it, and no migrants who come for it.... |
| 372 | 3 | `public boolean passes()` _(in Sector.Staffing)_ | Staffable enough to order: MIN_STAFFABLE_TO_ORDER of its posts fillable from spare workers, and none of the rest in a band nobody can fill. |
| 377 | 8 | `public String why(String building)` _(in Sector.Staffing)_ | Why not, as the advisor's hold reason, for a building of this name. |
| 388 | 8 | `static String postsWord(WageBand band)` | What the advisor calls a band's posts. |
| 398 | 3 | `public Staffing staffing(BuildingsTemplate t)` | The staffing test every planner that builds posts asks before ordering one (0.7.18); see above. |
| 411 | 9 | `public int staffableCount(BuildingsTemplate t, int wanted)` | The largest order of up to {@code wanted} of these that still passes, counted as one building of that many times the posts; 0 when not even one does (0.7.18). |
| 422 | 44 | `public Staffing staffing(BuildingsTemplate t, int count)` | The same test for {@code count} of these at once. |
| 486 | 5 | `public double getPayroll()` | What the staffed posts cost this month: each job type's wage for the posts of that type actually filled. |
| 498 | 5 | `public double getPayrollAtFullStaffing()` | The same bill with every post it offers filled - what the unfilled posts would cost as well, for the operations page's note (0.7.17); the builders' laid-off posts are not offered and not in it. |
| 520 | 5 | `public double[] getStaffedPayrollPerType()` | The staffed payroll by tier, for the banded wage tax: each tier's wage bill at that tier's own fill (0.7.17 - it was the average fill, see getPayroll()). |
| 526 | 1 | `public double getAverageFill()` |  |
| 533 | 5 | `public long getPostsOffered()` | The posts its buildings offer, as the month's wage pass last counted them (0.7.4, for the sector list): the total the "Staffed" share on the operations page is a share of. |
| 545 | 1 | `public long[] postsOfferedPerTier()` | ...per job type: its buildings' posts (postsPerTier()) less any it has laid off this month - only the builders do (0.7.17; sectors .Construction, THE CREWS THE WORK NEEDS). |
| 553 | 3 | `public double getWorkers()` | The posts filled - its workers (0.7.4, for the sector list): the posts at the operations page's "Staffed" share, getAverageFill(), which is the filled posts over the posts, so the two cannot disagree. |

### UTILISATION (lines 557-608)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 1 | `public void setEnergyRatio(double r)` |  |
| 576 | 1 | `public void setWaterRatio(double r)` |  |
| 577 | 1 | `public void setRoadRatio(double r)` |  |
| 578 | 1 | `public void setHealthRatio(double r)` |  |
| 579 | 1 | `public void setPricePerWatt(double p)` |  |
| 580 | 1 | `public void setPricePerWaterUnit(double p)` |  |
| 582 | 1 | `public void setElectricityConsumption(double kw)` | Draw at nameplate, off its own buildings. |
| 583 | 1 | `public void setWaterConsumption(double units)` |  |
| 585 | 1 | `public double getEnergyRatio()` |  |
| 586 | 1 | `public double getWaterRatio()` |  |
| 587 | 1 | `public double getRoadRatio()` |  |
| 588 | 1 | `public double getHealthRatio()` |  |
| 591 | 3 | `public int buildingsStanding()` | How many of its buildings stand, finished (0.7.30): none is "no plant standing" on its pages, not a rate. |
| 596 | 3 | `public int buildingsOnSite()` | ...and how many are on site, for anyone's order (0.7.30). |
| 601 | 3 | `public double getOperatingRate()` | How much of nameplate actually runs: staffing times the five ratios, the fifth being its freight (getFreightRatio()). |
| 606 | 1 | `public double getElectricityCost()` | Charged for what was DELIVERED, not asked for - the utility books the same slice. |
| 607 | 1 | `public double getWaterCost()` |  |

### THE POWER ITS WORKS MAKE THEMSELVES (0.7.112, batch I2; runs/spec- (lines 609-636)

| line | len | member | says |
|---:|---:|---|---|
| 629 | 1 | `public void setPowerSurplus(double kw)` |  |
| 632 | 1 | `public double getPowerSurplus()` | kW its buildings make past their own draws (0 for every sector until a works makes power). |
| 635 | 1 | `public double getPowerSoldValue()` | What the surplus sells for a month, at the tariff: nothing with none. |

### WHAT ITS WORKS BUY ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; (lines 637-675)

| line | len | member | says |
|---:|---:|---|---|
| 657 | 3 | `public double getConsumablesAtNameplate()` | Its buildings' consumables a month at nameplate, thousands of the world's money: nothing with none standing. |
| 662 | 6 | `public double consumablesBill()` | What the month's consumables cost it, in the city's money: their nameplate at the operating rate and the exchange factor; nothing with none, or no markets to read the factor off. |
| 670 | 4 | `public void buyConsumables()` | The month's consumables, bought from the world (above): an import named CONSUMABLES on the input line. |

### THE FLEET (2026-09-17) (lines 676-748)

### AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL (lines 749-791)

### AND THE DIESEL THEY BURN (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) (lines 792-1010)

| line | len | member | says |
|---:|---:|---|---|
| 818 | 1 | `public double getFleetDieselLitres()` |  |
| 819 | 1 | `public double getFleetDieselCost()` |  |
| 820 | 1 | `public double getFleetDieselImported()` |  |
| 837 | 1 | `public boolean isFleetKnown()` |  |
| 853 | 25 | `public double tonnesMoved()` | Tonnes a month of GOODS this sector's standing plant moves, in and out: what its vans carry (FR2: since then a sector's BULK goes by Freight's trucks, its city leg billed by Haulage). |
| 879 | 1 | `public double vansNeeded()` |  |
| 887 | 4 | `public ham.citybuildersim.sectors.Haulage.LegPrice legPrice()` | What the city leg of a bulk tonne costs one more works of this sector, at Haulage's quote in force and the city's trade (FR2; sectors.Haulage.legPrice()). |
| 893 | 1 | `public double vanFleet()` | The vehicles it owns. |
| 906 | 8 | `public double getVanRatio()` | The fifth ratio. |
| 921 | 10 | `public boolean hasBulkLegs()` | Whether any good this sector makes or uses at its standing plant travels in the BULK stream (FR2): the test for whether Haulage's trucks are part of its freight at all. |
| 949 | 10 | `public double getHaulageRatio()` | The haulage half of the fifth ratio (FR2, 2026-10-18; spec-freight-and- infrastructure 3.2): the van rule moved across to the trucks. |
| 966 | 3 | `public double getFreightRatio()` | The fifth throttle (FR2): its freight, the lesser of the vans' ratio (its own vans for its goods) and Haulage's (Freight's trucks for its bulk), whichever binds. |
| 979 | 3 | `public void runFleet()` | A month of wear, and the seeding. |
| 992 | 18 | `public void runFleet(Sectors sectors)` | ...and the month's diesel (0.7.83, batch O6; spec-oil 2.5): the van-months the sector works this month - what its standing plant moves at the operating rate, over TONNES_PER_VAN, the same measure its fleet is sized by... |

### MONEY AND THE BILLS (lines 1011-1054)

| line | len | member | says |
|---:|---:|---|---|
| 1029 | 1 | `public final double getCash()` |  |
| 1030 | 1 | `public final void setCash(double cash)` |  |
| 1031 | 1 | `public final void addCash(double amount)` |  |
| 1033 | 1 | `public void setInterestExpense(double v)` |  |
| 1034 | 1 | `public void setPropertyTaxExpense(double v)` |  |
| 1035 | 1 | `public void setMaintenanceExpense(double v)` |  |
| 1036 | 1 | `public void setForeignIncome(double v)` |  |
| 1037 | 1 | `public void setTaxRate(double rate)` |  |
| 1039 | 1 | `public double getInterestExpense()` |  |
| 1040 | 1 | `public double getPropertyTaxExpense()` |  |
| 1041 | 1 | `public double getMaintenanceExpense()` |  |
| 1042 | 1 | `public double getForeignIncome()` |  |
| 1043 | 1 | `public double getTaxRate()` |  |
| 1045 | 5 | `public void setBalanceSheetInputs(double land, double buildingsWorth, double bonds)` |  |
| 1051 | 1 | `public double getLandValue()` |  |
| 1052 | 1 | `public double getBuildingsValue()` |  |
| 1053 | 1 | `public double getBondsPayable()` |  |

### WHAT ITS BORROWING COST IT UP FRONT, EXPENSED AND DEDUCTED (0.7.102) (lines 1055-1198)

| line | len | member | says |
|---:|---:|---|---|
| 1104 | 5 | `public void deferBorrowingCosts(double cost, int lifeMonths)` | What its borrowing cost it up front this month on debts of this life, in months: expensed in equal months from the next strike. |
| 1111 | 7 | `public double getBorrowingCostsToExpense()` | What its borrowing cost it up front and is still to expense: the sheet's asset (SectorBooks), as of now. |
| 1120 | 10 | `private double expenseBorrowingCosts()` | The strike's charge: every charge with a month to run, then each a month nearer its end. |
| 1132 | 10 | `public double getInventoryValue()` | Stock on hand at the price it would fetch today, every good together. |
| 1149 | 10 | `public Map<Good, double[]> stockAtPrices()` | Its stock and its pantry by good: the units, and the price each is valued at today - the two getInventoryValue() multiplies (0.7.75, the sector statements' R6, where the books price last month's stock at this month's ... |
| 1161 | 1 | `public double stockPrice(Good g)` | The price a good of its stock is valued at today (priceOf()): for last month's stock at this month's prices (R6). |
| 1164 | 5 | `protected double priceOf(Good g)` | Today's price of a good, for valuing stock: the market's, or nothing for a seller-priced good. |
| 1176 | 5 | `protected double inputPrice(Good g)` | What a unit of an input costs it to bring in when the month's inputs are costed (inputCostAtRate()): the local price where somebody here offers it, the import price where nobody does (GoodsMarket.landedPrice()) - not ... |
| 1186 | 12 | `public BalanceSheet getBalanceSheet()` | Position as of right now - a balance sheet is an instant, an income statement a period. |

### STOCK (lines 1199-1219)

| line | len | member | says |
|---:|---:|---|---|
| 1209 | 1 | `public double getStock(Good g)` |  |
| 1210 | 1 | `public double getPantry(Good g)` |  |
| 1212 | 1 | `public void setStock(Good g, double units)` |  |
| 1213 | 1 | `public void setPantry(Good g, double units)` |  |
| 1216 | 3 | `public double getStockCapacity(Good g)` | Warehouse room for a good, off its buildings' `stock` field. |

### CAPACITY, off the buildings (lines 1220-1238)

| line | len | member | says |
|---:|---:|---|---|
| 1225 | 3 | `public double getCapacity(Good g)` | Nameplate output of a good a month, finished buildings only. |
| 1230 | 3 | `public double getPipeline(Good g)` | ...and what is on site, which counts as supply for the planner. |
| 1235 | 3 | `public double getInputAtCapacity(Good g)` | Input a good needs a month at nameplate, across its buildings. |

### THE MONTH'S PRODUCTION FIGURES, per output good (lines 1239-1319)

| line | len | member | says |
|---:|---:|---|---|
| 1244 | 14 | **type** `public static final class Output` | What the month did with each good it makes. |
| 1260 | 6 | **type** `public static final class Input` | What the month did with each good it uses. |
| 1270 | 1 | `public Output output(Good g)` |  |
| 1271 | 1 | `public Input input(Good g)` |  |
| 1274 | 1 | `public Output outputRow(Good g)` | The same rows read-only (0.7.30, for SectorFlow): null when the month has none for the good, and asking makes none. |
| 1275 | 1 | `public Input inputRow(Good g)` |  |
| 1292 | 6 | `public double unitsExported(Good g)` | What CROSSED THE CITY BOUNDARY this month, in units, by good - read-only, so asking does not create a row. |
| 1299 | 6 | `public double unitsImported(Good g)` |  |
| 1313 | 6 | `public double unitsBoughtLocal(Good g)` | ...and what it bought AT HOME in the month, in units, by good (FR2, 2026-10-18): the Input row's boughtLocal, and in a city just loaded what the save carried, as the two above are. |

### THE MONTH'S TRADE ACROSS A SAVE (A1, 2026-10-05) (lines 1320-1356)

| line | len | member | says |
|---:|---:|---|---|
| 1348 | 8 | `private static void carry(Map<String, Double> saved, Map<Good, Double> into)` | A save's units by good name into a carried map; a good this build does not know, or nothing, is left out. |

### WHAT A MONTH WROTE OFF, PAST ITS STRIKE (0.7.133, batch I8) (lines 1357-1428)

| line | len | member | says |
|---:|---:|---|---|
| 1380 | 6 | `public double unitsWrittenOff(Good g)` | Units of a good written off in the month in progress: its row, and in a city just loaded what the save carried. |
| 1388 | 4 | `public double writtenOffStruck(Good g)` | ...and in the month the last strike banked (0.7.133): what NationalAccounts' fifth term adds back, and since 0.7.136 the food, materials and luxuries terms too. |
| 1413 | 15 | `public void deriveCarriedTrade()` | ...AND FROM A SAVE BEFORE THE UNITS (0.7.63): the units out of the money. |

### THE LEDGER (lines 1429-1636)

| line | len | member | says |
|---:|---:|---|---|
| 1456 | 5 | **type** `public static final class Split` | One good's side of the month, in money, split by which side of the border it cleared on. |
| 1459 | 1 | `public double total()` _(in Sector.Split)_ |  |
| 1462 | 122 | **type** `public static final class Ledger` |  |
| 1530 | 1 | `public Split soldOf(Good g)` _(in Sector.Ledger)_ |  |
| 1531 | 1 | `public Split boughtOf(Good g)` _(in Sector.Ledger)_ |  |
| 1545 | 5 | `public double purchases()` _(in Sector.Ledger)_ |  |
| 1551 | 1 | `public double revenue()` _(in Sector.Ledger)_ |  |
| 1553 | 12 | `void clear()` _(in Sector.Ledger)_ |  |
| 1572 | 11 | `void scale(double s)` _(in Sector.Ledger)_ | AND THE PER-GOOD MONEY SCALES WITH EVERYTHING ELSE. |
| 1586 | 10 | `public static Map<Good, Split> copyOf(Map<Good, Split> from)` | A deep copy, because a Split is mutable and the ledger is cleared under it. |
| 1599 | 1 | `public Ledger pending()` |  |
| 1602 | 14 | `protected void bookSale(Trade t)` | A sale of this sector's, booked. |
| 1618 | 18 | `protected void bookPurchase(Trade t)` | A purchase of this sector's, booked. |

### BUY ONLY WHAT IT CAN PAY FOR (0.7.12, round 6) (lines 1637-1748)

| line | len | member | says |
|---:|---:|---|---|
| 1712 | 3 | `public void openPurchases(double budget, double orderValue)` | Opens the clearing's purchases: what it can pay for, against what its orders would come to at what a unit costs to bring in (GoodsMarket.landedPrice()). |
| 1724 | 16 | `public void openPurchases(double budget, double orderValue, double coveredValue)` | ...and with what the orders its suppliers' credit covers would come to (0.7.44; SupplierCredit), when it has one. |
| 1742 | 6 | `public void closePurchases()` | ...and closes it: a purchase outside the clearing is not read against it. |

### ITS SUPPLIERS' CREDIT (0.7.44) (lines 1749-1986)

| line | len | member | says |
|---:|---:|---|---|
| 1762 | 1 | `public SupplierCredit supplierCredit()` | The ledger of what this sector owes its suppliers for stock they let it have on credit, or null for a sector whose suppliers are paid at the strike. |
| 1765 | 4 | `public double getTradePayable()` | What it owes its suppliers now: the next strike repays it. |
| 1771 | 9 | `public double getTradeReceivable()` | What the buyers it supplied owe it now, for stock it let them have on credit: the next strike collects it. |
| 1782 | 4 | `public double getTradeCreditTaken()` | What its suppliers' credit added to its till at the last strike: the stock it took on credit (what it repaid there is getTradeCreditRepaid()). |
| 1788 | 4 | `public double getTradeCreditRepaid()` | ...what it repaid there. |
| 1799 | 9 | `public double getTradeCreditCash()` | What trade credit moved its till by at the last strike, net, in: the credit it took less what it repaid, as a buyer; what it was repaid less what it let its buyers have, as a supplier. |
| 1810 | 1 | `public double purchaseShare()` | The share of every order for stock the budget funds this clearing: 1 when they all fit. |
| 1812 | 4 | `public double purchaseShare(Good g)` | ...of an order for this good: more, for one its suppliers' credit covers (0.7.44). |
| 1817 | 1 | `public double purchasesLeft()` | What is left of what it can pay for, in money. |
| 1820 | 8 | `void noteForgone(Good g, double units, double value)` | Units of an order it did not place because it could not pay for them, and what they would have cost. |
| 1830 | 1 | `public double getPurchaseBudget()` | What the last clearing said it could pay for (infinite with nobody asking). |
| 1832 | 1 | `public double getOrderValue()` | ...what its orders came to, before the budget. |
| 1834 | 1 | `public double getPurchasesForgone()` | ...and what it did not buy for want of cash and credit, in money. |
| 1836 | 1 | `public double getUnitsForgone(Good g)` | ...of one good, in units. |
| 1838 | 1 | `public double getPurchasesForgone(Good g)` | ...and in money. |
| 1840 | 1 | `public boolean wasPurchaseLimited()` | True when the last clearing's budget cut an order. |
| 1851 | 4 | `protected final void noteShelfShort(double units, double price)` |  |
| 1856 | 1 | `public double getShelfShort()` |  |
| 1857 | 1 | `public double getShelfShortValue()` |  |
| 1860 | 3 | `protected final void bookOtherRevenue(double amount)` | Revenue that is not a sale of a good, booked into the month. |
| 1872 | 5 | `public final void sellOldMetal(double amount)` | ...and its old steel, sold to the Scrap Yards (0.7.123, batch I4; OldMetal): other revenue like any, the yards' input from it at the same strike (they bill it to themselves - billForService() - so the sales tax nets a... |
| 1884 | 3 | `protected final void bookRevenueRefund(double amount)` | ...and a price given back (0.7.19): the builders' refund of material escalation to an owner whose material cost less when it was drawn than its quote allowed. |
| 1914 | 5 | `public final void billForService(String supplier, String what, double amount)` | A SERVICE BOUGHT FROM ANOTHER BUSINESS IN THE CITY, billed by the business that performed it. |
| 1932 | 5 | `protected final void payTheCity(String what, double amount)` | A DUE PAID TO THE CITY (FR3, docs/queue/FR3.md 3; the port dues): an operating cost on the input line, named, whose cash goes to the treasury and not to a business. |
| 1945 | 4 | `public final void recordCapitalPurchase(String supplier, double amount)` | A building bought from another business in the city (0.7.19): the builders' work on this sector's own premises, billed as they do it, and any escalation on it - negative for a refund. |
| 1955 | 5 | `protected final void drawPaidStock(String what, double cost)` | An input drawn from stock the sector paid for in an earlier month, at what it paid (0.7.8): a cost this month, named, and no cash - see Ledger.paidEarlier. |
| 1981 | 5 | `protected final void bookImportedService(String what, double amount)` | ...AND ONE AGAIN SINCE 0.7.91 (batch O10; spec-oil 2.7): the shuttle tankers that take a platform's crude ashore, a service the world's ships sell with no good behind it. |

### THE STATEMENT (lines 1987-2238)

| line | len | member | says |
|---:|---:|---|---|
| 1995 | 74 | **type** `public static final class Statement` | The month's figures, as the statement was struck. |
| 2051 | 17 | `void scale(double s)` _(in Sector.Statement)_ |  |
| 2072 | 1 | `public Statement statement()` |  |
| 2075 | 1 | `public double getNetIncome()` | Pre-tax, before the profit tax. |
| 2078 | 1 | `public double getProfitTax()` | What the city collects from it this month. |
| 2085 | 50 | `public void strike()` | Strikes the month WITHOUT the sales tax and without banking: the ledger is read into the statement, and the VAT is struck from these figures next, by SalesTaxLedger, and cannot be known while they are written. |
| 2144 | 3 | `public void bank(double salesTaxRemitted)` | ...and banks it, once the sales tax is known. |
| 2154 | 42 | `public void bank(double salesTaxRemitted, double capitalTaxCredit)` | ...with the part of that net which is the tax credited back on buildings it bought (0.7.19): remitted net of it, but charged on the statement without it, and handed to the cash apart - see Statement.capitalTaxCredit. |
| 2198 | 1 | `protected void afterBank()` | A sector with something to clear when its month is banked says so here. |
| 2205 | 1 | `protected void beforeBank()` | ...and one with something to keep of the month's rows before they are cleared (A5, 0.7.46): called first in bank(), while every row is still the month being struck. |
| 2211 | 27 | `public void restoreStatement(Statement saved)` | The struck month, put back on load, so the first month back reads the same as the one before it and the loss counter sees what it saw. |

### THE MONTH AT THE BOTTOM - HOOKS Markets CALLS (lines 2239-2327)

| line | len | member | says |
|---:|---:|---|---|
| 2267 | 4 | `protected double plannedDemand(Good g)` | What the city will want of a good this month, for planning output. |
| 2278 | 11 | `public double getPlannedOutput(Good g)` | What it will make of a stockable good this month for the home market: nameplate at today's rate, or what brings the stock to STOCK_MONTHS of demand, whichever is less. |
| 2299 | 3 | `protected double madeAtRate(Good g)` | What its plant could make of a stockable good this month: its nameplate at today's rate - the figure getPlannedOutput(), getExportBoundOutput() and produceStock() start from. |
| 2308 | 8 | `public double getExportBoundOutput(Good g)` | Nameplate the city cannot eat, made for export instead - if the export price clears the marginal cost of running the line, which is the energy and water and nothing else: the staff are paid either way. |
| 2321 | 6 | `public double getCostPerUnit(Good g)` | Break-even per unit this month: everything the line costs over what it makes. |

### WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS (lines 2328-2522)

| line | len | member | says |
|---:|---:|---|---|
| 2361 | 19 | `protected double costShareOf(Good g)` |  |
| 2388 | 6 | `public double getMarginalCostPerUnit(Good g)` | What it costs to SELL a unit already made: the energy and water, and the inputs, and not the payroll, which is paid whether or not a unit leaves the shed. |
| 2396 | 13 | `protected double inputCostAtRate()` | What the month's inputs cost at the operating rate, at today's prices. |
| 2419 | 21 | `public double bid(Good g)` | What it wants of an input this month. |
| 2442 | 5 | `protected double pantryTarget(Good g)` | Units of a pantry good the sector aims to hold: months of recent use. |
| 2449 | 1 | `protected double recentUse(Good g)` | What it used of a pantry good last month. |
| 2461 | 19 | `public double offer(Good g, double price)` | Units it will release to the market at the price. |
| 2486 | 20 | `public void produceFlow(Good g)` | Lifts, brews or smelts a flow good for the month - what the makers bring to a market that has no stock behind it. |
| 2514 | 5 | `private double flowRunShare(Good g)` | The share of its nameplate a flow good is made at this month (THE ORDER, FL1): the share of the run of the buildings that make it, nameplate-weighted - what buildingOrder() orders their inputs for - and not the good's... |
| 2521 | 1 | `protected double groundLimit(Good g, double asked)` | A sector whose output is limited by what is in the ground says so here. |

### CAPTIVE USE (0.7.112, batch I2; docs/studies/spec-materials.md 2.7) (lines 2523-2565)

| line | len | member | says |
|---:|---:|---|---|
| 2547 | 1 | `public boolean captive(Good g)` | Whether it uses what it makes of a good first, itself (CAPTIVE USE): false here; sectors.ProcessingSector says yes for every good it both makes and uses. |
| 2556 | 9 | `public double captiveUnits(Good g)` | The units of a captive good it makes and uses itself this month: its need at the operating rate, for the works that will run (THE ORDER: inputToMake()), as far as what it makes at the rate covers it - for a flow good ... |

### THE ORDER (WN3, 2026-10-29; the ensemble's negative GDP in months (lines 2566-2995)

| line | len | member | says |
|---:|---:|---|---|
| 2662 | 1 | `protected boolean ordersForWhatItMakes()` | Whether the sector orders its inputs for what it will make (THE ORDER): yes here; sectors.Refining says no, its crude is the run's (THE RUN, 0.7.105). |
| 2665 | 16 | `public void planOrders()` | Decides the month's order shares, before the flow goods are made (Markets.clearMonth(), after the fleets): nothing to decide for a sector that makes no traded good (THE ORDER; a flow good's since FL1). |
| 2683 | 5 | `public void endOrders()` | The market pass is over: nothing is planned, every share reads 1 again (THE ORDER). |
| 2690 | 14 | `public double orderShare(Good g)` | What share of a traded good's nameplate this month's inputs are ordered for (THE ORDER; a flow good's is FL1's): 1 for a good that is not traded and outside the market pass. |
| 2711 | 9 | `private double forecastOrder(Good g)` | What the month will make of a stockable good over its nameplate past what the sector keeps for its own works (THE ORDER): the plan for the home market and the ship's line, asked at the stock it expects to hold after t... |
| 2726 | 11 | `private double forecastFlow(Good g)` | The share of a traded flow good's nameplate at the rate this month's inputs are ordered for (THE ORDER, FL1): what its own works need of it and what the home market demanded at the last strike are made at any price; o... |
| 2744 | 18 | `private double exportsAtNoLoss(Good g, double spare)` | Of `spare` units of a flow good that can only be exported, the most that sells at no loss on the world's band (THE ORDER, FL1): all of it when nothing bought makes it, or when the whole of it nets over the inputs' cos... |
| 2769 | 3 | `public double flowCostPerUnit(Good g)` | What the inputs of a unit of a flow good cost the sector (THE ORDER, FL1): over its standing buildings that make it, nameplate-weighted, each one's traded inputs at inputCost() less what its other traded goods are wor... |
| 2774 | 12 | `private double unitCost(Good g, Set<Good> costing)` | What the inputs of a unit of any good cost the sector, flow or stockable (flowCostPerUnit()'s body); `costing` holds the goods being costed up the chain, so one that loops back is priced as an input is. |
| 2788 | 14 | `private double inputsLessCoProducts(BuildingsTemplate t, Good made, Set<Good> costing)` | What a month of one building's traded inputs costs here, less its other traded goods at their worth, to no less than nothing (flowCostPerUnit()). |
| 2813 | 9 | `private double inputCost(Good i, Set<Good> costing)` | What a unit of an input costs the works that use it, for flowCostPerUnit() (THE ORDER, FL1): inputPrice() for one the sector buys; for one it makes itself (captive) and makes enough of to cover its own works' need, wh... |
| 2824 | 7 | `private double coProductValue(Good o)` | What a unit of a good a building makes beside the one asked about is worth: its net export price where the world will buy it, else its price here; 0 if neither is a number. |
| 2833 | 10 | `private double whenHolding(Good g, double units, java.util.function.DoubleSupplier ask)` | Asks what the sector's own rules say with some units of a good in the shed instead of what is there, and puts the shed back (THE ORDER). |
| 2845 | 12 | `private double buildingOrder(BuildingsTemplate t)` | The share of its nameplate a building's inputs are ordered for: the greatest of its traded goods' order shares, flow or stockable; 1 for one that makes an untraded good, or nothing (THE ORDER, FL1). |
| 2859 | 4 | `protected double inputToMake(Good g)` | Units of an input its buildings use a month at the shares they run at (THE ORDER): getInputAtCapacity(), to the bit, while every share is 1 and outside the market pass. |
| 2869 | 9 | `private double captiveAtFullRun(Good g)` | Units of a captive good its own works would take at their full rate (THE ORDER): captiveUnits() outside the market pass. |
| 2886 | 15 | `public void shipUnsoldFlow(Good g, GoodsMarket market)` | A flow good's unsold units, once the market has taken what it wants: shipped abroad at the export price, or lost. |
| 2916 | 38 | `public void produceStock(Good g, GoodsMarket market)` | Runs the month's production of a stockable good into the warehouse, after the market has taken what it wanted from last month's stock. |
| 2956 | 4 | `void takeFromStock(Good g, double units)` | Units of a good taken out of stock by a local sale or an export from the shed. |
| 2962 | 5 | `void receiveInput(Good g, double units)` | Units of an input received into the pantry, or consumed on the spot. |
| 2969 | 6 | `protected final void usePantry(Good g, double units)` | A pantry good used up this month, recorded for next month's cover. |
| 2982 | 1 | `public void sellOwnPriced(Markets markets, Game game)` | The seller-priced goods, sold: the shops to the households, the landlords' doors to the families. |
| 2985 | 1 | `public void endOfMonth(Game game)` | After every market has cleared and every good is made: anything the month still needs. |
| 2994 | 1 | `protected void afterClearing(Good g)` | One good's market has cleared this month and its fills are booked (0.7.85; Markets.clear()), told to each of its buyers: a sector whose month reads what it bought says so here - the refiners' run, held to their crude ... |

### PLANNING - the decision to grow, and to shrink (lines 2996-3099)

| line | len | member | says |
|---:|---:|---|---|
| 3013 | 3 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | What this sector would like to build this month, or why not. |
| 3021 | 5 | `public Good planningGood()` | The good the generic planner sizes the sector by: its first stockable output, else its first output. |
| 3034 | 1 | `public double firstPlantUtilisation()` | The share of a plant's nameplate the city has to be taking, a month, before this sector sinks its FIRST plant. |
| 3043 | 1 | `public double visibleDemandOver(double months, EconomyManager economy)` | What the sector can SEE it will sell a month over the next `months`, when its customers' orders are on a book - a ceiling on the demand the maker's rule reads off the trend, since a trend read off a boom runs on after... |
| 3046 | 17 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one of its templates would clear a month, for the interest test. |
| 3078 | 7 | `public double[] retirementDemandAndCapacity(Game game)` | The demand the spare-capacity rule measures the sector against, and the capacity it measures. |
| 3087 | 4 | `public double unitsOf(BuildingsTemplate t)` | What one of these contributes to the measure the sector is judged on. |
| 3093 | 1 | `public boolean mayRetire(BuildingsTemplate t)` | Whether a holding of this template may be sold back this month. |
| 3096 | 3 | `public String noRetirementReason(boolean distress)` | Why nothing could be sold, when mayRetire() refused everything. |

### THE SCREENS (lines 3100-3319)

| line | len | member | says |
|---:|---:|---|---|
| 3109 | 11 | **type** `public record Line(Kind kind, String label, String value, Tone tone)` | One line of the operations page. |
| 3110 | 1 | **type** `public enum Kind` _(in Sector.Line)_ |  |
| 3111 | 1 | **type** `public enum Tone` _(in Sector.Line)_ |  |
| 3113 | 1 | `public static Line head(String text)` _(in Sector.Line)_ |  |
| 3114 | 1 | `public static Line note(String text)` _(in Sector.Line)_ |  |
| 3116 | 1 | `public static Line note(String shown, String whole)` _(in Sector.Line)_ | A note in two layers (0.7.21): `shown` on the page, and `whole` one click away, behind an (i). |
| 3117 | 1 | `public static Line of(String label, String value)` _(in Sector.Line)_ |  |
| 3118 | 1 | `public static Line of(String label, String value, Tone tone)` _(in Sector.Line)_ |  |
| 3127 | 1 | `public SectorStatements.Format statementFormat()` | What kind of business its formal statements read as (0.7.74; the project's spec-sector-statements.md, 4.6): the words of its revenue, its cost of sales and its middle line - the bottom three are the model's in every f... |
| 3130 | 9 | `public String inputLabel()` | What the sector's direct-cost line is called on its income statement. |
| 3156 | 1 | `public Map<String, Double> otherRevenueParts()` | The parts of this sector's revenue that are not the sale of a good, named, for the lines inside an opened Revenue. |
| 3166 | 1 | `public Map<String, Double> otherInputParts()` | ...and the same for the cost line: what is in Inputs that is not a good. |
| 3186 | 5 | `protected Map<String, Double> nameOtherRevenue()` | ...and where those names come from, read off the sector's LIVE fields at the moment the month is struck. |
| 3207 | 5 | `public List<Line> operations(Game game)` | The operations page, as data: the factory's block (plantLines()) when the sector keeps it, then the sector's own lines (ownLines()). |
| 3214 | 1 | `public boolean hasPlantBlock()` | Whether its operations page opens on the factory's block (0.7.30): false for the seven sectors whose page is all their own. |
| 3217 | 1 | `public List<Line> ownLines(Game game)` | The sector's own lines, after the factory's block (0.7.30): none unless it says more. |
| 3225 | 94 | `public List<Line> plantLines(Game game)` | The factory's block (0.7.30; operations() itself until then): the plant - staffed, running at, and the five ratios - then a block per good it makes and per good it buys. |

### SAVE AND RESTORE (lines 3320-3524)

| line | len | member | says |
|---:|---:|---|---|
| 3325 | 47 | `public SectorState toState()` | Everything about this sector that a save has to carry. |
| 3373 | 67 | `public void restore(SectorState s)` |  |
| 3442 | 14 | `public void restoreBills(SectorState s)` | The bills of the month back, over whatever a rebuild re-derived. |
| 3458 | 1 | `private static Double ratioToSave(double r)` | A ratio for the save: itself, or null when it is not a number - Gson writes no NaN, and the load then derives it. |
| 3461 | 1 | `protected void saveExtras(Map<String, Double> extras)` | A sector with state of its own - a price it walks, an order book - writes it here by name. |
| 3464 | 1 | `protected void restoreExtras(Map<String, Double> extras)` | ...and reads it back. |
| 3467 | 27 | `public void reset()` | Everything back to a founding sector. |
| 3495 | 1 | `protected void resetExtras()` |  |
| 3501 | 18 | `public void redenominate(double scale)` | Its money in the new unit. |
| 3520 | 1 | `protected void redenominateExtras(double scale)` |  |
| 3523 | 1 | `public String toString()` |  |

### THE POWER, FOR THE PICTOGRAMS (0.7.141, batch I11; IndustryView) (lines 3525-3553)

| line | len | member | says |
|---:|---:|---|---|
| 3538 | 4 | `public double getPowerMade()` | kW its buildings make themselves, at nameplate: every billed building's gen (BuildingsTemplate.getGen()) - nothing until a works makes power. |
| 3544 | 4 | `public double getPowerDrawn()` | ...and kW they draw, at nameplate, before what they make: every billed building's whole draw. |
| 3550 | 3 | `public double getGridDraw()` | ...and kW they draw from the grid past what they make: the net draw its bill is struck on (setElectricityConsumption()), beside the surplus it sells (getPowerSurplus()). |

