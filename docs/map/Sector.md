# Sector.java - 4,382 lines · 311 methods · 22 constants · model

`ham/citybuildersim/Sector.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (202), [BuildingsTemplate](BuildingsTemplate.md) (29), [GoodsMarket](GoodsMarket.md) (19), [WageBand](WageBand.md) (11), [NewUnit](NewUnit.md) (10), [SupplierCredit](SupplierCredit.md) (10), [Game](Game.md) (9), [Statement](Statement.md) (9), [JobType](JobType.md) (8), [Traffic](Traffic.md) (8), [Trade](Trade.md) (7), [SectorState](SectorState.md) (7), [Markets](Markets.md) (5), [BusinessInvestment](BusinessInvestment.md) (5), [Haulage](Haulage.md) (5), [BuildingManager](BuildingManager.md) (4), [BuildingType](BuildingType.md) (3), [BalanceSheet](BalanceSheet.md) (3), [Freight](Freight.md) (2), [Sectors](Sectors.md) (2), [SectorStatements](SectorStatements.md) (2), [Formats](Formats.md) (2), [Recipe](Recipe.md) (1), [PopulationManager](PopulationManager.md) (1), [Construction](Construction.md) (1), [BuildingsStacks](BuildingsStacks.md) (1), [Migration](Migration.md) (1), [ArrivalMix](ArrivalMix.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [HydroSites](HydroSites.md) (1)... and 5 more

**Used by (185):** [AdviceValueOracle](AdviceValueOracle.md), [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankFailureCheck](BankFailureCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BooksCheck](BooksCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CardCheck](CardCheck.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [CoalMiningCheck](CoalMiningCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [CopperBufferCheck](CopperBufferCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DenominationCheck](DenominationCheck.md), [DeterminismCheck](DeterminismCheck.md), [DistressBleedCheck](DistressBleedCheck.md), [DriverlessTruckCheck](DriverlessTruckCheck.md), [EconomyManager](EconomyManager.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md), [EssentialWorksCheck](EssentialWorksCheck.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FitoutCheck](FitoutCheck.md), [FittingCheck](FittingCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [ForeignCheck](ForeignCheck.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [Game](Game.md), [GapOrderCheck](GapOrderCheck.md), [GdpCheck](GdpCheck.md), [GoldMiningCheck](GoldMiningCheck.md), [GovernmentScreen](GovernmentScreen.md), [GroceryCheck](GroceryCheck.md), [GroundInputCheck](GroundInputCheck.md), [Haulage](Haulage.md), [HaulageCheck](HaulageCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [HomeRailCheck](HomeRailCheck.md), [HousingCheck](HousingCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [Icons](Icons.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [KeptTimingCheck](KeptTimingCheck.md), [LabourCheck](LabourCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LicenceFees](LicenceFees.md), [LicenceFeesCheck](LicenceFeesCheck.md), [LongPlaytest](LongPlaytest.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapView](MapView.md), [Markets](Markets.md), [Materials](Materials.md), [MaterialsCheck](MaterialsCheck.md), [MiningCheck](MiningCheck.md), [ModernEngineeringCheck](ModernEngineeringCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyWordsCheck](MoneyWordsCheck.md), [NewGameCheck](NewGameCheck.md), [NewUnitCheck](NewUnitCheck.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilViewCheck](OilViewCheck.md), [OreView](OreView.md), [OutwardInvestment](OutwardInvestment.md), [PassThroughCheck](PassThroughCheck.md), [Pieces](Pieces.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [PowerPriceCheck](PowerPriceCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PrizeCheck](PrizeCheck.md), [ProcessingSector](ProcessingSector.md), [PublicLicenceCheck](PublicLicenceCheck.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RecipeChanges](RecipeChanges.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [ReloadCheck](ReloadCheck.md), [ResearchScreen](ResearchScreen.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SectorBooks](SectorBooks.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorState](SectorState.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [SlopeCheck](SlopeCheck.md), [SpreadPlanner](SpreadPlanner.md), [StaffingDrawCheck](StaffingDrawCheck.md), [StaffingTieCheck](StaffingTieCheck.md), [SulphideLeachCheck](SulphideLeachCheck.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TaxPolicy](TaxPolicy.md), [TradeBandCheck](TradeBandCheck.md), [TradeScreen](TradeScreen.md), [TruckRouteCheck](TruckRouteCheck.md), [UndemandedFeedCheck](UndemandedFeedCheck.md), [VanCheck](VanCheck.md), [WantedPostsCheck](WantedPostsCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProductsCheck](WoodProductsCheck.md), [WorksFinanceCheck](WorksFinanceCheck.md), [WorksValue](WorksValue.md), [YoungBankCheck](YoungBankCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md)

## Sections

| line | section |
|---:|---|
| 81 | IDENTITY AND DECLARATION |
| 164 | WIRING - set once by Sectors, through attach() |
| 184 | LABOUR |
| 251 | A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved |
| 325 | EVERY PLANNER THAT BUILDS POSTS ASKS, AND THE FIFTH IT LEAVES IS FRICTION |
| 503 | · A TIE AT THE GATE IS SHARED (TQ1, 2026-11-16) |
| 566 | · AN IDLE BUILDER'S CREWS ARE SPARE (IC1, {next}, 2026-11-01) |
| 582 | · THE GATE COUNTS THE DRAW (GW1, {next}, 2026-11-10) |
| 876 | UTILISATION |
| 947 | · A FIRST PLANT IS WEIGHED AT THE RATE IT WILL RUN AT (OE1, 2026-11-20) |
| 1057 | · THE POWER ITS WORKS MAKE THEMSELVES (0.7.112, batch I2; runs/spec- |
| 1085 | THE STEAM FORM (R0c, 2026-11-05; research-1900 section 4, [P2.26], [P2.27]) |
| 1157 | · WHAT ITS WORKS BUY ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; |
| 1196 | THE FLEET (2026-09-17) |
| 1269 | · AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL |
| 1312 | · AND THE DIESEL THEY BURN (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) |
| 1566 | MONEY AND THE BILLS |
| 1610 | · WHAT ITS BORROWING COST IT UP FRONT, EXPENSED AND DEDUCTED (0.7.102) |
| 1755 | STOCK |
| 1776 | CAPACITY, off the buildings |
| 1800 | THE MONTH'S PRODUCTION FIGURES, per output good |
| 1881 | THE MONTH'S TRADE ACROSS A SAVE (A1, 2026-10-05) |
| 1918 | WHAT A MONTH WROTE OFF, PAST ITS STRIKE (0.7.133, batch I8) |
| 1990 | THE LEDGER |
| 2209 | BUY ONLY WHAT IT CAN PAY FOR (0.7.12, round 6) |
| 2321 | ITS SUPPLIERS' CREDIT (0.7.44) |
| 2602 | THE STATEMENT |
| 2868 | THE MONTH AT THE BOTTOM - HOOKS Markets CALLS |
| 2998 | WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS |
| 3199 | CAPTIVE USE (0.7.112, batch I2; docs/studies/spec-materials.md 2.7) |
| 3242 | THE ORDER (WN3, 2026-10-29; the ensemble's negative GDP in months |
| 3597 | · THE GROUND'S SHARE OF THE ORDER (NG1, 2026-11-20) |
| 3797 | PLANNING - the decision to grow, and to shrink |
| 3918 | THE SCREENS |
| 4138 | SAVE AND RESTORE |
| 4354 | THE POWER, FOR THE PICTOGRAMS (0.7.141, batch I11; IndustryView) |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3928 | `Sector.Line.Kind.HEAD` |  |
| 3928 | `Sector.Line.Kind.LINE` |  |
| 3928 | `Sector.Line.Kind.NOTE` |  |
| 3929 | `Sector.Line.Tone.NONE` |  |
| 3929 | `Sector.Line.Tone.GOOD` |  |
| 3929 | `Sector.Line.Tone.WARN` |  |
| 3929 | `Sector.Line.Tone.BAD` |  |
| 3929 | `Sector.Line.Tone.MUTED` |  |
| 3929 | `Sector.Line.Tone.HEAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 283 | `Sector.MIN_STAFFABLE_TO_ORDER` | `.80` |  |
| 286 | `Sector.GATE_DRAW_MONTHS` | `12` | The months of arrivals the staffing gate counts beside the spare workers: the longest window in which none of the study's 16 seeds shrank (GW0; 24 months shrank two by a third). |
| 391 | `Sector.Staffing.ANY` | `new Staffing(1, 1, new double [ 0 ], new double [ 0 ], new double [ 0 ], new ...` | Always staffable: a building with no posts, or nobody to ask. |
| 636 | `Sector.SHARE_ROUNDING` | `1e-12` | The share of a building's posts the city could staff at or under which staffing() reads none: the subtraction's rounding, not a worker (BA1, 2026-11-04). |
| 645 | `Sector.DRAWN_SHARE_GRID` | `1e-9` | The grid a share the drawn workers filled is snapped to (GW1, 2026-11-10): where every band's chance is whole (Migration.opportunity() at 1) the draw grows with the posts, so two templates whose posts only the draw fi... |
| 1107 | `Sector.STEAM_COAL_KG_A_KWH` | `1.8` | Coal a 1900 factory's steam engine burned for a kWh at the shaft: 1.8 kg (research-1900 section 4, [P2.26], [P2.27], est.). |
| 1174 | `Sector.CONSUMABLES` | `"Consumables"` | The consumables' name on a sector's input line. |
| 1261 | `Sector.TONNES_PER_VAN` | `120` | What one vehicle in a sector's fleet moves in a month. |
| 1267 | `Sector.VAN_LIFE_MONTHS` | `120` | How long a working vehicle lasts. |
| 1296 | `Sector.FLEET_DELIVERY_MONTHS` | `8` | The fastest a sector can put vehicles on the road: this much of the fleet it needs, a month. |
| 1310 | `Sector.MIN_VAN_RATE` | `.6` | What a sector with no lorries of its own still gets done. |
| 1324 | `Sector.LOADS_A_VAN_MONTH` | `TONNES_PER_VAN / Good.VANS.tonnesPerUnit()` | The loads a van-month carries: TONNES_PER_VAN at a van's own weight, three tonnes (Good.VANS.tonnesPerUnit()) - forty. |
| 1327 | `Sector.KM_A_LOAD` | `50` | How far a load goes, there and back: fifty kilometres (docs/studies/spec-oil.md 2.5, est.; to confirm). |
| 1330 | `Sector.DIESEL_LITRES_PER_100_KM` | `12` | What a laden van or truck burns: twelve litres of diesel a hundred kilometres (docs/studies/spec-oil.md 2.5, est.; to confirm). |
| 1333 | `Sector.DIESEL_LITRES_A_VAN_MONTH` | `LOADS_A_VAN_MONTH * KM_A_LOAD * DIESEL_LITRES_PER_100_KM / 100` | ...so a van-month burns LOADS_A_VAN_MONTH x KM_A_LOAD x DIESEL_LITRES_PER_100_KM / 100 litres: 240. |
| 1653 | `Sector.BORROWING_COST_MOST_MONTHS` | `Mortgage.MORTGAGE_AMORTIZATION_MONTHS` | The longest life a cost of borrowing is expensed over, in months: a mortgage's amortization, forty years (0.7.102, A16) - the clock's length. |
| 2451 | `Sector.UPKEEP_SOLD` | `"Upkeep work"` | The name of the upkeep sold, in a statement's otherParts (HL1). |
| 2454 | `Sector.HOUSEHOLD_SERVICES` | `"Household emergency services"` | Named household service receipts, with no physical seat-service good sold (HC1). |
| 2882 | `Sector.STOCK_MONTHS` | `2` | How many months of local demand a maker holds in stock before it idles. |
| 2885 | `Sector.DUMP_THRESHOLD` | `.8` | Above this share of warehouse room a maker clears stock even at a loss. |
| 3321 | `Sector.FLOW_EXPORT_HEADROOM` | `.02` | The margin over its inputs' cost a works aims next month's export price at on the world's slope (FL1): a month's drift in the band then leaves this month's price over the cost, so the line does not stop every other mo... |
| 3652 | `Sector.UNSOLD_DUST` | `1e-9` | What of a flow good's output may go unsold as rounding, a share of what was made: under it the remainder is idled, not exported (EM2). |

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
| 168 | `protected BuildingManager buildings` |  |
| 169 | `protected Markets markets` |  |
| 172 | `protected Game game` | The city, for the few hooks that need more than the buildings and the markets. |
| 189 | `protected final double[] wages` | Payroll per tier at full staffing: wage x posts. |
| 190 | `protected final long[] jobs` |  |
| 191 | `protected final double[] fill` |  |
| 194 | `protected double averageFill` | Share of its posts that are filled. |
| 374 | `public final double share` | The share of its posts the city's spare workers, and the workers its posts would draw within GATE_DRAW_MONTHS (Migration.drawFor()), could fill: the one passes() reads. |
| 376 | `public final double shareNow` | The share of its posts the spare workers alone could fill: today's count, before GW1. |
| 378 | `public final WageBand unfillable` | A band it has posts in that nobody can fill (see passes()), or null. |
| 383 | `private final Sector owner` | Who asked and for what, kept so a planner's hold can say which building it was for and a refusal for want of workers can be priced (LD1, WantedPosts): the owner, the template, how many of it, and the posts and the spa... |
| 384 | `private final BuildingsTemplate template` |  |
| 385 | `private final long count` |  |
| 386 | `private final double[] wantedCopy, spareCopy, drawnCopy` |  |
| 388 | `private final List<Staffing> tiedWith` | The refusals tied with this one at its share, in the order the planner met them (TQ1; held()); empty for none. |
| 890 | `protected double energyRatio` |  |
| 891 | `protected double electricity, water` |  |
| 892 | `protected double pricePerWatt, pricePerWaterUnit` |  |
| 894 | `protected double pricePerWattIndustry, pricePerWattWorks` | The struck book's other two prices a kW a month (PW1, the merit order): Heavy Industry and Mining (the energy and the long wires), and what a works' surplus is paid. |
| 896 | `protected double highVoltageShare` | The share, 0..1, of its billed draw that its Heavy Industry and Mining buildings take: billed at the industry price, the rest at the commerce price. |
| 997 | `private boolean weighStaffed` | Set only inside staffed(): a first plant's staffing read whole. |
| 1075 | `protected double powerSurplus` | kW its buildings make past their own draws, at nameplate - what it sells the grid. |
| 1110 | `private double steamShare` | The share of its draw it turns with steam on site (the steam form): 0 until a harness or R1 sets it; not saved. |
| 1113 | `private double electricityBeforeSteam` | The draw EconomyManager gave it last, before the steam share took its part off: electricity is this when no share is set. |
| 1336 | `private double fleetDieselLitres, fleetDieselCost, fleetDieselImported` | The month's diesel for the fleet (0.7.83): the litres drawn, what they cost, and the part bought abroad. |
| 1355 | `protected boolean vansKnown` | Whether this sector's fleet is a fact about the sector rather than a fact about the version it was saved from. |
| 1570 | `private double cash` |  |
| 1573 | `private double interestExpense` | Set for the month at the top of it, before the statement runs. |
| 1574 | `private double propertyTaxExpense` |  |
| 1575 | `private double maintenanceExpense` |  |
| 1577 | `private double foreignIncome` | The coupon on its dollars abroad this month, local money (HD1, HD2). |
| 1580 | `private double taxRate` | The profit rate in force this month. |
| 1582 | `private double landValue, buildingsValue, bondsPayable` |  |
| 1656 | `private final double[] borrowingChargesLeft` | The month's charge by how many months it has still to run: index k, k months; nothing at 0. |
| 1760 | `protected final Map<Good, Double> stock` | Output on hand, per stockable good it makes. |
| 1763 | `protected final Map<Good, Double> pantry` | Input on hand, per good it keeps a pantry of. |
| 1806 | `public double capacity` | nameplate a month |
| 1807 | `public double planned` | nameplate a month |
| 1808 | `public double produced` | what it decided to make for home |
| 1809 | `public double idled` | what went into stock or to market |
| 1810 | `public double exportBound` | nameplate neither the city nor the world would take |
| 1811 | `public double offered` | made straight for the ship |
| 1812 | `public double withheld` | released to the market at the price |
| 1813 | `public double soldLocal` | kept back below cost |
| 1814 | `public double exported` | units taken by local buyers |
| 1815 | `public double writtenOff` | units shipped, from the line or the shed |
| 1816 | `public double costPerUnit` | stock lost to a demolished warehouse |
| 1817 | `public double captive` | the month's break-even, for the screen |
| 1822 | `public double needed` | at this month's operating rate |
| 1823 | `public double bid` | at this month's operating rate |
| 1824 | `public double boughtLocal` | what it asked the market for |
| 1825 | `public double imported` | filled at home |
| 1828 | `protected final Map<Good, Output> outputs` |  |
| 1829 | `protected final Map<Good, Input> inputs` |  |
| 1900 | `private final Map<Good, Double> carriedExports` | Units shipped in the month a save was taken, by good: read by unitsExported() until the first strike after the load. |
| 1903 | `private final Map<Good, Double> carriedImports` | ...and units landed, read by unitsImported(). |
| 1906 | `private final Map<Good, Double> carriedBoughtLocal` | ...and units bought at home (FR2), read by unitsBoughtLocal(): the city-leg tonnes Haulage bills at the top of the next month. |
| 1935 | `private final Map<Good, Double> carriedWrittenOff` | Units written off in the month a save was taken, by good: read by unitsWrittenOff() until the first strike after the load. |
| 1938 | `private final Map<Good, Double> struckWrittenOff` | ...and the units written off in the month the last strike banked, by good: what the accounts add back. |
| 1955 | `private boolean tradeToDerive` | True from the load of a save that carried the month's trade in money alone until deriveCarriedTrade() has read it. |
| 2018 | `public double atHome` |  |
| 2019 | `public double abroad` |  |
| 2025 | `public double localSales` | Sold to local buyers - sectors, households, the city - in money. |
| 2027 | `public double exports` | Sold to the world. |
| 2029 | `public double otherRevenue` | Revenue that is not a sale of a good: recognised building work, repairs billed. |
| 2031 | `public final Map<String, Double> purchasesBySupplier` | Bought from each local supplier, by the supplier's key: the input tax credit is at THEIR rate. |
| 2033 | `public double imports` | Bought from the world. |
| 2035 | `public final Map<Good, Double> unitsSold` | Units sold and bought, per good, for the accounts and the screens. |
| 2036 | `public final Map<Good, Double> unitsBought` |  |
| 2044 | `public final Map<Good, Split> sold` | And the same two in MONEY, split home and abroad. |
| 2045 | `public final Map<Good, Split> bought` |  |
| 2064 | `public final Map<String, Double> otherInputs` | ...and the part of the input line that is NOT a good, by name. |
| 2076 | `public double paidEarlier` | ...AND STOCK IT PAID FOR EARLIER (0.7.8): an input drawn this month out of stock the sector bought in an earlier month and paid cash for then - the builders' material from scrapped plant, booked at what they paid for ... |
| 2089 | `public final Map<String, Double> capitalBySupplier` | ...AND BUILDINGS IT BOUGHT FROM ANOTHER BUSINESS, by the supplier's key (0.7.19): the builders' contract work on its own premises and the material escalation on it, as the builders bill them. |
| 2094 | `public double salesToHouseholds` | Sold to households in particular - consumption, in the national accounts. |
| 2096 | `public double oldMetalSold` | The part of otherRevenue that is its old steel sold to the Scrap Yards (0.7.123, batch I4; sellOldMetal()), named apart at the strike. |
| 2101 | `public double upkeepSold` | The part of otherRevenue that is upkeep sold to the city's services and the bank (HL1; sellUpkeep()), named apart at the strike; its cash left the buyer's pool when it was booked and reaches this till at bank(), and M... |
| 2103 | `public double householdServices` | Household emergency-service receipts inside otherRevenue and salesToHouseholds (HC1). |
| 2111 | `public double paidToCity` | What it pays the city this month for a service the city sells and no business does (FR3, payTheCity()): the port dues. |
| 2169 | `private Ledger pending` |  |
| 2271 | `private double purchasesLeft` | What the clearing lets it spend, what is left of it, and the share of each order it funds - and of each order its suppliers' credit covers (0.7.44). |
| 2272 | `private double purchaseShare` |  |
| 2274 | `private double rPurchaseBudget` | The clearing's reading, for the screens and the playtest. |
| 2275 | `private final Map<Good, Double> rForgoneUnits` |  |
| 2276 | `private final Map<Good, Double> rForgoneValue` |  |
| 2421 | `private double rShelfShort, rShelfShortValue` | What the shelf could not sell this month (0.7.12 round 6): customers the counter and the staff could have served and the stock could not, in what the sector sells, and their value at its price. |
| 2611 | `public double revenue, inputs, payroll, electricity, water, maintenance` |  |
| 2612 | `public double operatingIncome, interest, propertyTax, salesTax, preTaxIncome, profitTax, netIncome` |  |
| 2614 | `public double localSales, exports, otherRevenue, salesToHouseholds` | The two halves of revenue, for the accounts. |
| 2616 | `public double localPurchases, imports` | The two halves of inputs, for the accounts and the audit. |
| 2617 | `public Map<String, Double> purchasesBySupplier` |  |
| 2624 | `public Map<Good, Split> sold` | Revenue and the cost of sales BY GOOD, each split home and abroad - what the income statement's two biggest lines open into. |
| 2625 | `public Map<Good, Split> bought` |  |
| 2632 | `public Map<String, Double> otherParts` | And the parts of otherRevenue that have names - the builders' work recognised and repairs billed. |
| 2638 | `public Map<String, Double> otherInputs` | ...and the parts of the INPUT line that are not a good: haulage on the shippers' books, fuel on the railway's. |
| 2640 | `public double paidEarlier` | The part of inputs drawn from stock paid for in an earlier month - see Ledger.paidEarlier. |
| 2642 | `public double borrowingCosts` | What its borrowing cost it up front, the month's share of each over its debt's life (0.7.102, A16): a cost before the profit tax, paid when it borrowed, so added back to the cash at bank(). |
| 2644 | `public Map<String, Double> capitalBySupplier` | Buildings bought from other businesses this month, by supplier (0.7.19) - see Ledger.capitalBySupplier. |
| 2652 | `public double capitalTaxCredit` | The sales tax credited back on them (0.7.19): inside the net the ledger settled, and NOT in salesTax - it was capitalised with the building, not expensed, so crediting it through the tax line would have made it profit... |
| 2662 | `public double powerSold` | What it sold the grid of the power its works make past their own draws (0.7.112, batch I2; spec-materials 2.8): the surplus at the tariff, IN REVENUE - so every line the books strike from revenue adds up with it - and... |
| 2664 | `public double foreignIncome` | The coupon on its dollars abroad (HD1, HD2): a line of finance income BELOW the operating line and inside preTaxIncome, so the profit tax and the dividend see it. |
| 2685 | `private final Statement statement` |  |
| 3121 | `protected final Map<Good, Double> pantryUsedLastMonth` |  |
| 3324 | `private final Map<Good, Double> orderShares` | The share of each traded good's nameplate this month's inputs are ordered for (THE ORDER; a flow good's since FL1): empty outside the market pass, never saved - it is decided afresh each month. |
| 3327 | `private final Map<Good, Double> flowCut` | The units of a flow good this month's order cut from its nameplate at the rate (FL1): only a cut over 0 is here, empty outside the market pass and never saved. |
| 3330 | `private boolean ordering` | Whether the market pass is on and its shares apply (THE ORDER). |
| 3333 | `private final Set<Good> planning` | The goods whose share is being worked out, so a chain that loops back to one reads 1 for it (THE ORDER). |
| 3336 | `private boolean deciding` | Whether planOrders() is at work: only then is a share worked out, so a screen reading the month never touches the shed (THE ORDER). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 4304 | **type** `public abstract class Sector` | One business in the city, and the template every sector extends. |

### IDENTITY AND DECLARATION (lines 81-163)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 14 | `protected Sector(String key, String label, BuildingType group)` | "Construction", "Heavy Industry", "Mining" are what every save already calls the six; a new sector picks a name and keeps it for ever. |
| 120 | 1 | `protected final void makes(Good good)` |  |
| 121 | 1 | `protected final void uses(Good good)` |  |
| 129 | 5 | `public final void declareRecipeGoods(Recipe recipe)` | Declares every good a recipe makes and uses that the constructor did not (R1c; Recipes.apply() calls it where a template of this sector first runs the recipe): the sector then sells what it makes and buys what it uses. |
| 136 | 4 | `protected final void pantry(Good good, double coverMonths)` | It keeps this many months of use of an input on hand, buying the difference each month. |
| 141 | 1 | `protected final void blurb(String text)` |  |
| 143 | 1 | `public final String key()` |  |
| 144 | 1 | `public final String label()` |  |
| 145 | 1 | `public final BuildingType group()` |  |
| 146 | 1 | `public final String blurb()` |  |
| 147 | 1 | `public final Set<Good> goodsMade()` |  |
| 148 | 1 | `public final Set<Good> goodsUsed()` |  |
| 149 | 1 | `public final boolean isMaker(Good g)` |  |
| 150 | 1 | `public final boolean isUser(Good g)` |  |
| 152 | 1 | `public final boolean hasPantry(Good g)` | True for a good it keeps on hand and restocks - a shelf, a larder, a fleet - and so, since 0.7.12 round 6, what a budget can cut (BUY ONLY WHAT IT CAN PAY FOR); false for a maker's input, consumed as it makes. |
| 162 | 1 | `public boolean buysAhead(Good g)` | Whether its order of a good is for stock it keeps - cut, with the rest of its stock, to what it can pay for (BUY ONLY WHAT IT CAN PAY FOR) - rather than a maker's input bought whole (0.7.85): its pantry goods (hasPant... |

### WIRING - set once by Sectors, through attach() (lines 164-183)

| line | len | member | says |
|---:|---:|---|---|
| 174 | 4 | `void attach(BuildingManager buildings, Markets markets)` |  |
| 179 | 1 | `void attachGame(Game game)` |  |
| 181 | 1 | `public BuildingManager buildings()` |  |
| 182 | 1 | `public Markets markets()` |  |

### LABOUR (lines 184-250)

| line | len | member | says |
|---:|---:|---|---|
| 197 | 4 | `public void updateJobFillRate(double[] fillRate)` | The fill rate per tier, as PopulationManager last set it. |
| 208 | 9 | `public void updateWages(double[] wagePerType, long[] posts)` | The wage bill, from the schedule and this sector's own posts. |
| 223 | 9 | `private double fillOf(long[] posts, int n)` | The share of `posts` (per tier, the first n) the city's fill rates staff: the sector's staffing over its own posts (updateWages()), and since OE1 over a first building's (firstPlantRate()); 1 with no posts. |
| 238 | 4 | `public boolean runsNothing()` | Whether it had no posts at the last wage bill (updateWages()): nothing running, its staffing read 1 for want of anyone to staff and its operating rate the city's around it (OE1; plannedRate()). |
| 244 | 3 | `public long[] postsPerTier()` | Posts per tier, off the buildings it operates: its own and, for Freight, the city's sea terminals (BuildingsTemplate.getOperator(), FR3). |
| 249 | 1 | `public long[] postsPastBuildings()` | The posts per tier it runs past what postsPerTier() counts off its buildings' - for the city's own count of posts (BuildingManager.setPostsPast()); null for none, which is every sector but a farm run on horses (R0c). |

### A FIRM DOES NOT OPEN A BUILDING IT CANNOT STAFF (2026-09-12, moved (lines 251-324)

| line | len | member | says |
|---:|---:|---|---|
| 321 | 3 | `public double staffableShare(BuildingsTemplate t)` | The share of a building's posts this city could actually fill today, and since GW1 with a year's draw (Staffing.share). |

### EVERY PLANNER THAT BUILDS POSTS ASKS, AND THE FIFTH IT LEAVES IS FRICTION (lines 325-565)

| line | len | member | says |
|---:|---:|---|---|
| 369 | 178 | **type** `public static final class Staffing` | Whether the city could staff one of a building, and if not, why not (0.7.18). |
| 400 | 3 | `Staffing(double share, double[] wanted, double[] spare, double[] above, boolean[] comeFor)` _(in Sector.Staffing)_ |  |
| 408 | 17 | `Staffing(double share, double shareNow, double[] wanted, double[] spare, double[] above, boolean[] comeFor, double[] drawn, Sec...` _(in Sector.Staffing)_ |  |
| 427 | 14 | `private Staffing(Staffing kept, Staffing tied)` _(in Sector.Staffing)_ | This refusal again, with {@code tied} met at the same share added to the refusals tied with it (TQ1; held()). |
| 443 | 1 | `public Sector owner()` _(in Sector.Staffing)_ | The sector that asked, or null for the always-staffable answer. |
| 446 | 1 | `public BuildingsTemplate template()` _(in Sector.Staffing)_ | The building asked about, or null for the always-staffable answer. |
| 449 | 1 | `public long count()` _(in Sector.Staffing)_ | How many of it were asked about at once (0 for the always-staffable answer). |
| 452 | 1 | `public double[] wanted()` _(in Sector.Staffing)_ | The posts the order has per band (a copy). |
| 455 | 1 | `public double[] spare()` _(in Sector.Staffing)_ | Each band's supply less its posts, from the fill (a copy). |
| 458 | 1 | `public double[] drawn()` _(in Sector.Staffing)_ | The workers per band migration would bring for the order within GATE_DRAW_MONTHS, Migration.drawFor() (a copy). |
| 469 | 3 | `static boolean nobodyCanFill(double spare, double workersAbove, boolean migrantsComeForIt)` _(in Sector.Staffing)_ | THE ONE DEFINITION OF "NOBODY CAN FILL" (Jerus, "Truly unfillable only"): "A band counts as 'nobody can fill' only when it has no spare workers, nobody above who can step down into it, and no migrants who come for it.... |
| 479 | 3 | `public boolean passes()` _(in Sector.Staffing)_ | Staffable enough to order: MIN_STAFFABLE_TO_ORDER of its posts fillable from spare workers and a GATE_DRAW_MONTHS draw, and none of the rest in a band nobody can fill. |
| 484 | 13 | `public String why(String building)` _(in Sector.Staffing)_ | Why not, as the advisor's hold reason, for a building of this name. |
| 499 | 3 | `public String why()` _(in Sector.Staffing)_ | Why not, for the building it was asked about (TQ1: a hold names its own template). |
| 526 | 6 | `public static Staffing held(Staffing kept, Staffing refused)` _(in Sector.Staffing)_ | Of the refusals a planner has met so far and the one it meets now, the one it keeps as its hold: the larger share, and at the same share both, tied (above). |
| 534 | 7 | `public List<Staffing> alike()` _(in Sector.Staffing)_ | This refusal and those tied with it at its share (held()), this one first: a list of one for no tie. |
| 543 | 3 | `public static String arrivalsWords()` _(in Sector.Staffing)_ | The window the gate counts, in words: "a year's arrivals" at GATE_DRAW_MONTHS of 12 (GW1). |
| 549 | 8 | `static String postsWord(WageBand band)` | What the advisor calls a band's posts. |
| 564 | 1 | `public boolean ordersOnlyWhatItCanStaff()` | Whether this sector's orders and the investors' build card hold a building to staffing() (HD1, 2026-10-29): true for every sector but the landlords, whose homes come before the workers - population is min(housing, job... |

### AN IDLE BUILDER'S CREWS ARE SPARE (IC1, {next}, 2026-11-01) (lines 566-581)

### THE GATE COUNTS THE DRAW (GW1, {next}, 2026-11-10) (lines 582-875)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 3 | `public Staffing staffing(BuildingsTemplate t)` | The staffing test every planner that builds posts asks before ordering one (0.7.18); see above. |
| 611 | 4 | `public int staffableCount(BuildingsTemplate t, int wanted)` | The largest order of up to {@code wanted} of these that still passes, counted as one building of that many times the posts; 0 when not even one does (0.7.18). |
| 617 | 10 | `public long staffableCount(BuildingsTemplate t, long wanted)` | ...for an order or a stack that may pass 2^31 (SQ1). |
| 648 | 3 | `public Staffing staffing(BuildingsTemplate t, long count)` | The same test for {@code count} of these at once. |
| 653 | 3 | `public Staffing staffing(NewUnit u, long count)` | ...for one new building already resolved (QN1): its own posts, the template it is of in the answer. |
| 665 | 3 | `public Staffing staffingWithOwnSites(BuildingsTemplate t, long count)` | The same test for {@code count} of these of which the ones on site are among them (BA1, 2026-11-05): their own posts are what is asked, so they are not also spoken for (THE POSTS OF ITS OWN SITES ARE SPOKEN FOR, which... |
| 680 | 3 | `public Staffing staffingWithCrewsSpare(BuildingsTemplate t, long count)` | The same test with the builders' laid-off crews counted spare (LD1): as staffing(t, count) is asked, without the posts BuildingManager.getPostsWithheld() adds. |
| 685 | 1 | `protected NewUnit newUnit(BuildingsTemplate t)` | One new building of `t` as it would open now (QN1, NewUnit.of()): what this sector's estimates and gates read of it. |
| 687 | 98 | `private Staffing staffing(NewUnit u, long count, boolean crewsSpokenFor, boolean ownSitesInCount)` |  |
| 805 | 5 | `public double getPayroll()` | What the staffed posts cost this month: each job type's wage for the posts of that type actually filled. |
| 817 | 5 | `public double getPayrollAtFullStaffing()` | The same bill with every post it offers filled - what the unfilled posts would cost as well, for the operations page's note (0.7.17); the builders' laid-off posts are not offered and not in it. |
| 839 | 5 | `public double[] getStaffedPayrollPerType()` | The staffed payroll by tier, for the banded wage tax: each tier's wage bill at that tier's own fill (0.7.17 - it was the average fill, see getPayroll()). |
| 845 | 1 | `public double getAverageFill()` |  |
| 852 | 5 | `public long getPostsOffered()` | The posts its buildings offer, as the month's wage pass last counted them (0.7.4, for the sector list): the total the "Staffed" share on the operations page is a share of. |
| 864 | 1 | `public long[] postsOfferedPerTier()` | ...per job type: its buildings' posts (postsPerTier()) less any it has laid off this month - only the builders do (0.7.17; sectors .Construction, THE CREWS THE WORK NEEDS). |
| 872 | 3 | `public double getWorkers()` | The posts filled - its workers (0.7.4, for the sector list): the posts at the operations page's "Staffed" share, getAverageFill(), which is the filled posts over the posts, so the two cannot disagree. |

### UTILISATION (lines 876-946)

| line | len | member | says |
|---:|---:|---|---|
| 898 | 1 | `public void setEnergyRatio(double r)` |  |
| 899 | 1 | `public void setWaterRatio(double r)` |  |
| 900 | 1 | `public void setRoadRatio(double r)` |  |
| 901 | 1 | `public void setHealthRatio(double r)` |  |
| 903 | 1 | `public void setPricePerWatt(double p)` | One price for all three (a fixture's, and the founding tariff): commerce, industry and the works' surplus. |
| 905 | 3 | `public void setPowerPrices(double commerce, double industry, double works)` | The struck book's three prices a kW a month: commerce and light industry, Heavy Industry and Mining, and what a works' surplus is paid. |
| 908 | 1 | `public void setHighVoltageShare(double share)` |  |
| 909 | 1 | `public double getHighVoltageShare()` |  |
| 911 | 3 | `public double powerPriceFor(BuildingsTemplate t)` | What a planner values a building's power draw at: the industry price for a high-voltage class, the commerce price for the rest. |
| 914 | 1 | `public void setPricePerWaterUnit(double p)` |  |
| 916 | 4 | `public void setElectricityConsumption(double kw)` | Draw at nameplate, off its own buildings. |
| 920 | 1 | `public void setWaterConsumption(double units)` |  |
| 922 | 1 | `public double getEnergyRatio()` |  |
| 923 | 1 | `public double getWaterRatio()` |  |
| 924 | 1 | `public double getRoadRatio()` |  |
| 925 | 1 | `public double getHealthRatio()` |  |
| 928 | 3 | `public long buildingsStanding()` | How many of its buildings stand, finished (0.7.30): none is "no plant standing" on its pages, not a rate. |
| 933 | 3 | `public long buildingsOnSite()` | ...and how many are on site, for anyone's order (0.7.30). |
| 938 | 3 | `public double getOperatingRate()` | How much of nameplate actually runs: staffing times the five ratios, the fifth being its freight (getFreightRatio()). |
| 943 | 3 | `private double rateOf(double staffing, double roads, double freight)` | The operating rate at a staffing, a roads' ratio and a freight ratio, with the sector's power, water and the city's health: getOperatingRate()'s product, and firstPlantRate()'s. |

### A FIRST PLANT IS WEIGHED AT THE RATE IT WILL RUN AT (OE1, 2026-11-20) (lines 947-1056)

| line | len | member | says |
|---:|---:|---|---|
| 971 | 3 | `public double plannedRate(NewUnit u)` | The operating rate an order weighs one more building of `u` at (OE1): the sector's own, getOperatingRate(), while it runs anything; with nothing running (runsNothing()), the rate the building would run at as the first... |
| 976 | 3 | `public double plannedRate(NewUnit u, double running)` | ...with the sector's own rate as the reader takes it: the refiners' planner weighs theirs without the run (Refining.plantRate()). |
| 989 | 6 | `public double firstPlantRate(NewUnit u)` | What one building of `u` would run at as the first of its sector: getOperatingRate()'s product (rateOf()), its staffing and its roads read for the building, the city's fill over its own posts (fillOf()) and the roads'... |
| 1009 | 9 | `public double staffed(java.util.function.DoubleSupplier estimate)` | `estimate` read with a first plant weighed staffed (OE1): its staffing read whole, the other five ratios as firstPlantRate() reads them. |
| 1020 | 7 | `private double roadsFor(BuildingsTemplate t)` | The roads' ratio for one building of `t`'s traffic: what EconomyManager.setRoadRatio() reads for a sector's own buildings, here for this one; the sector's ratio with no roads to ask. |
| 1032 | 3 | `public double getPowerRatio()` | The share of the plant its power lets run (R1d, R0c star 8): the grid's ratio on the electric share, and the steam share whole, since a boiler burning its own coal does not wait on the grid. |
| 1037 | 1 | `public double getElectricShare()` | The share of the plant on the grid, 1 - getSteamShare(): what a point more of the grid's ratio adds to getPowerRatio() (R1d; WorksValue's power). |
| 1043 | 5 | `public double perPointOfGrid(double valueAdded)` | What the plant's value added would be at a whole grid, per point of the grid's ratio (R1d): value added over the power ratio, times the electric share; valueAdded / getEnergyRatio() with no steam, to the bit. |
| 1050 | 5 | `public double getElectricityCost()` | Charged for what was DELIVERED, not asked for - the utility books the same slice. |
| 1055 | 1 | `public double getWaterCost()` |  |

### THE POWER ITS WORKS MAKE THEMSELVES (0.7.112, batch I2; runs/spec- (lines 1057-1084)

| line | len | member | says |
|---:|---:|---|---|
| 1077 | 1 | `public void setPowerSurplus(double kw)` |  |
| 1080 | 1 | `public double getPowerSurplus()` | kW its buildings make past their own draws (0 for every sector until a works makes power). |
| 1083 | 1 | `public double getPowerSoldValue()` | What the surplus sells for a month, at the works' price (the struck book's): nothing with none. |

### THE STEAM FORM (R0c, 2026-11-05; research-1900 section 4, [P2.26], [P2.27]) (lines 1085-1156)

| line | len | member | says |
|---:|---:|---|---|
| 1116 | 8 | `public void setSteamShare(double s)` | Sets the steam share, clamped to [0, 1] (not a number reads 0); above 0 it registers THERMAL_COAL as an input, once, and the grid draw it is billed on follows at once. |
| 1125 | 1 | `public double getSteamShare()` |  |
| 1132 | 3 | `public double getSteamCoalAtCapacity()` | The tonnes of THERMAL_COAL its steam engines burn a month at nameplate: the steam share of what its buildings draw (the units of electricity, HydroSites.KWH_PER_UNIT kWh a month each) in kWh, at STEAM_COAL_KG_A_KWH, i... |
| 1137 | 9 | `private double steamCoal(java.util.function.ToDoubleFunction<BuildingsTemplate> order)` | The steam coal of its buildings, each at the share its inputs are ordered for when `order` says (THE ORDER), else at nameplate. |
| 1152 | 4 | `protected double formInput(Good g, boolean ordered)` | Units of an input a form of the sector (the steam engines' coal here; the farm's horses' feed in sectors.Agriculture) uses past what its buildings use, a month at nameplate - or, when `ordered`, what the month orders ... |

### WHAT ITS WORKS BUY ABROAD THAT IS NOT A GOOD (0.7.112, batch I2; (lines 1157-1195)

| line | len | member | says |
|---:|---:|---|---|
| 1177 | 3 | `public double getConsumablesAtNameplate()` | Its buildings' consumables a month at nameplate, thousands of the world's money: nothing with none standing. |
| 1182 | 6 | `public double consumablesBill()` | What the month's consumables cost it, in the city's money: their nameplate at the operating rate and the exchange factor; nothing with none, or no markets to read the factor off. |
| 1190 | 4 | `public void buyConsumables()` | The month's consumables, bought from the world (above): an import named CONSUMABLES on the input line. |

### THE FLEET (2026-09-17) (lines 1196-1268)

### AND THE TWO THAT MAKE IT A CONSTRAINT RATHER THAN A BILL (lines 1269-1311)

### AND THE DIESEL THEY BURN (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) (lines 1312-1565)

| line | len | member | says |
|---:|---:|---|---|
| 1338 | 1 | `public double getFleetDieselLitres()` |  |
| 1339 | 1 | `public double getFleetDieselCost()` |  |
| 1340 | 1 | `public double getFleetDieselImported()` |  |
| 1357 | 1 | `public boolean isFleetKnown()` |  |
| 1373 | 25 | `public double tonnesMoved()` | Tonnes a month of GOODS this sector's standing plant moves, in and out: what its vans carry (FR2: since then a sector's BULK goes by Freight's trucks, its city leg billed by Haulage). |
| 1399 | 1 | `public double vansNeeded()` |  |
| 1407 | 4 | `public ham.citybuildersim.sectors.Haulage.LegPrice legPrice()` | What the city leg of a bulk tonne costs one more works of this sector, at Haulage's quote in force and the city's trade (FR2; sectors.Haulage.legPrice()). |
| 1413 | 1 | `public double vanFleet()` | The vehicles it owns. |
| 1426 | 8 | `public double getVanRatio()` | The fifth ratio. |
| 1441 | 3 | `public boolean hasBulkLegs()` | Whether any good this sector makes or uses at its standing plant travels in the BULK stream (FR2): the test for whether Haulage's trucks are part of its freight at all. |
| 1446 | 10 | `private boolean bulkLegs(java.util.function.ToDoubleFunction<Good> made, java.util.function.ToDoubleFunction<Good> used)` | ...the same test for a plant that makes `made` and uses `used` of each good a month: its standing plant's (hasBulkLegs()) or one new building's (firstPlantFreight(), OE1). |
| 1474 | 8 | `public double getHaulageRatio()` | The haulage half of the fifth ratio (FR2, 2026-10-18; spec-freight-and- infrastructure 3.2): the van rule moved across to the trucks. |
| 1484 | 6 | `private ham.citybuildersim.sectors.Haulage haulage()` | The city's trucks this sector's bulk would ride (sectors.Freight's Haulage): none for Freight itself, the carrier, or with no game to ask. |
| 1492 | 3 | `private static double haulageAt(double share)` | The haulage half at a share of its legs carried: MIN_VAN_RATE, and the rest of the way by the share. |
| 1506 | 8 | `double firstPlantFreight(NewUnit u)` | The freight ratio of one building of `u` as the first of its sector (OE1; firstPlantRate()): the lesser of its two halves, as getFreightRatio() takes them. |
| 1521 | 3 | `public double getFreightRatio()` | The fifth throttle (FR2): its freight, the lesser of the vans' ratio (its own vans for its goods) and Haulage's (Freight's trucks for its bulk), whichever binds. |
| 1534 | 3 | `public void runFleet()` | A month of wear, and the seeding. |
| 1547 | 18 | `public void runFleet(Sectors sectors)` | ...and the month's diesel (0.7.83, batch O6; spec-oil 2.5): the van-months the sector works this month - what its standing plant moves at the operating rate, over TONNES_PER_VAN, the same measure its fleet is sized by... |

### MONEY AND THE BILLS (lines 1566-1609)

| line | len | member | says |
|---:|---:|---|---|
| 1584 | 1 | `public final double getCash()` |  |
| 1585 | 1 | `public final void setCash(double cash)` |  |
| 1586 | 1 | `public final void addCash(double amount)` |  |
| 1588 | 1 | `public void setInterestExpense(double v)` |  |
| 1589 | 1 | `public void setPropertyTaxExpense(double v)` |  |
| 1590 | 1 | `public void setMaintenanceExpense(double v)` |  |
| 1591 | 1 | `public void setForeignIncome(double v)` |  |
| 1592 | 1 | `public void setTaxRate(double rate)` |  |
| 1594 | 1 | `public double getInterestExpense()` |  |
| 1595 | 1 | `public double getPropertyTaxExpense()` |  |
| 1596 | 1 | `public double getMaintenanceExpense()` |  |
| 1597 | 1 | `public double getForeignIncome()` |  |
| 1598 | 1 | `public double getTaxRate()` |  |
| 1600 | 5 | `public void setBalanceSheetInputs(double land, double buildingsWorth, double bonds)` |  |
| 1606 | 1 | `public double getLandValue()` |  |
| 1607 | 1 | `public double getBuildingsValue()` |  |
| 1608 | 1 | `public double getBondsPayable()` |  |

### WHAT ITS BORROWING COST IT UP FRONT, EXPENSED AND DEDUCTED (0.7.102) (lines 1610-1754)

| line | len | member | says |
|---:|---:|---|---|
| 1659 | 5 | `public void deferBorrowingCosts(double cost, int lifeMonths)` | What its borrowing cost it up front this month on debts of this life, in months: expensed in equal months from the next strike. |
| 1666 | 7 | `public double getBorrowingCostsToExpense()` | What its borrowing cost it up front and is still to expense: the sheet's asset (SectorBooks), as of now. |
| 1675 | 10 | `private double expenseBorrowingCosts()` | The strike's charge: every charge with a month to run, then each a month nearer its end. |
| 1687 | 10 | `public double getInventoryValue()` | Stock on hand at the price it would fetch today, every good together. |
| 1704 | 10 | `public Map<Good, double[]> stockAtPrices()` | Its stock and its pantry by good: the units, and the price each is valued at today - the two getInventoryValue() multiplies (0.7.75, the sector statements' R6, where the books price last month's stock at this month's ... |
| 1716 | 1 | `public double stockPrice(Good g)` | The price a good of its stock is valued at today (priceOf()): for last month's stock at this month's prices (R6). |
| 1719 | 5 | `protected double priceOf(Good g)` | Today's price of a good, for valuing stock: the market's, or nothing for a seller-priced good. |
| 1732 | 5 | `protected double inputPrice(Good g)` | What a unit of an input costs it to bring in when the month's inputs are costed (inputCostAtRate()): the local price where somebody here offers it, the import price where nobody does (GoodsMarket.currentLandedPrice())... |
| 1742 | 12 | `public BalanceSheet getBalanceSheet()` | Position as of right now - a balance sheet is an instant, an income statement a period. |

### STOCK (lines 1755-1775)

| line | len | member | says |
|---:|---:|---|---|
| 1765 | 1 | `public double getStock(Good g)` |  |
| 1766 | 1 | `public double getPantry(Good g)` |  |
| 1768 | 1 | `public void setStock(Good g, double units)` |  |
| 1769 | 1 | `public void setPantry(Good g, double units)` |  |
| 1772 | 3 | `public double getStockCapacity(Good g)` | Warehouse room for a good, off its buildings' `stock` field. |

### CAPACITY, off the buildings (lines 1776-1799)

| line | len | member | says |
|---:|---:|---|---|
| 1781 | 3 | `public double getCapacity(Good g)` | Nameplate output of a good a month, finished buildings only. |
| 1786 | 4 | `public double getPipeline(Good g)` | ...and what is on site, which counts as supply for the planner. |
| 1792 | 7 | `public double getInputAtCapacity(Good g)` | Input a good needs a month at nameplate, across its buildings. |

### THE MONTH'S PRODUCTION FIGURES, per output good (lines 1800-1880)

| line | len | member | says |
|---:|---:|---|---|
| 1805 | 14 | **type** `public static final class Output` | What the month did with each good it makes. |
| 1821 | 6 | **type** `public static final class Input` | What the month did with each good it uses. |
| 1831 | 1 | `public Output output(Good g)` |  |
| 1832 | 1 | `public Input input(Good g)` |  |
| 1835 | 1 | `public Output outputRow(Good g)` | The same rows read-only (0.7.30, for SectorFlow): null when the month has none for the good, and asking makes none. |
| 1836 | 1 | `public Input inputRow(Good g)` |  |
| 1853 | 6 | `public double unitsExported(Good g)` | What CROSSED THE CITY BOUNDARY this month, in units, by good - read-only, so asking does not create a row. |
| 1860 | 6 | `public double unitsImported(Good g)` |  |
| 1874 | 6 | `public double unitsBoughtLocal(Good g)` | ...and what it bought AT HOME in the month, in units, by good (FR2, 2026-10-18): the Input row's boughtLocal, and in a city just loaded what the save carried, as the two above are. |

### THE MONTH'S TRADE ACROSS A SAVE (A1, 2026-10-05) (lines 1881-1917)

| line | len | member | says |
|---:|---:|---|---|
| 1909 | 8 | `private static void carry(Map<String, Double> saved, Map<Good, Double> into)` | A save's units by good name into a carried map; a good this build does not know, or nothing, is left out. |

### WHAT A MONTH WROTE OFF, PAST ITS STRIKE (0.7.133, batch I8) (lines 1918-1989)

| line | len | member | says |
|---:|---:|---|---|
| 1941 | 6 | `public double unitsWrittenOff(Good g)` | Units of a good written off in the month in progress: its row, and in a city just loaded what the save carried. |
| 1949 | 4 | `public double writtenOffStruck(Good g)` | ...and in the month the last strike banked (0.7.133): what NationalAccounts' fifth term adds back, and since 0.7.136 the food, materials and luxuries terms too. |
| 1974 | 15 | `public void deriveCarriedTrade()` | ...AND FROM A SAVE BEFORE THE UNITS (0.7.63): the units out of the money. |

### THE LEDGER (lines 1990-2208)

| line | len | member | says |
|---:|---:|---|---|
| 2017 | 5 | **type** `public static final class Split` | One good's side of the month, in money, split by which side of the border it cleared on. |
| 2020 | 1 | `public double total()` _(in Sector.Split)_ |  |
| 2023 | 133 | **type** `public static final class Ledger` |  |
| 2091 | 1 | `public Split soldOf(Good g)` _(in Sector.Ledger)_ |  |
| 2092 | 1 | `public Split boughtOf(Good g)` _(in Sector.Ledger)_ |  |
| 2113 | 5 | `public double purchases()` _(in Sector.Ledger)_ |  |
| 2119 | 1 | `public double revenue()` _(in Sector.Ledger)_ |  |
| 2121 | 14 | `void clear()` _(in Sector.Ledger)_ |  |
| 2142 | 13 | `void scale(double s)` _(in Sector.Ledger)_ | AND THE PER-GOOD MONEY SCALES WITH EVERYTHING ELSE. |
| 2158 | 10 | `public static Map<Good, Split> copyOf(Map<Good, Split> from)` | A deep copy, because a Split is mutable and the ledger is cleared under it. |
| 2171 | 1 | `public Ledger pending()` |  |
| 2174 | 14 | `protected void bookSale(Trade t)` | A sale of this sector's, booked. |
| 2190 | 18 | `protected void bookPurchase(Trade t)` | A purchase of this sector's, booked. |

### BUY ONLY WHAT IT CAN PAY FOR (0.7.12, round 6) (lines 2209-2320)

| line | len | member | says |
|---:|---:|---|---|
| 2284 | 3 | `public void openPurchases(double budget, double orderValue)` | Opens the clearing's purchases: what it can pay for, against what its orders would come to at what a unit costs to bring in (GoodsMarket.landedPrice()). |
| 2296 | 16 | `public void openPurchases(double budget, double orderValue, double coveredValue)` | ...and with what the orders its suppliers' credit covers would come to (0.7.44; SupplierCredit), when it has one. |
| 2314 | 6 | `public void closePurchases()` | ...and closes it: a purchase outside the clearing is not read against it. |

### ITS SUPPLIERS' CREDIT (0.7.44) (lines 2321-2601)

| line | len | member | says |
|---:|---:|---|---|
| 2334 | 1 | `public SupplierCredit supplierCredit()` | The ledger of what this sector owes its suppliers for stock they let it have on credit, or null for a sector whose suppliers are paid at the strike. |
| 2337 | 4 | `public double getTradePayable()` | What it owes its suppliers now: the next strike repays it. |
| 2343 | 9 | `public double getTradeReceivable()` | What the buyers it supplied owe it now, for stock it let them have on credit: the next strike collects it. |
| 2354 | 4 | `public double getTradeCreditTaken()` | What its suppliers' credit added to its till at the last strike: the stock it took on credit (what it repaid there is getTradeCreditRepaid()). |
| 2360 | 4 | `public double getTradeCreditRepaid()` | ...what it repaid there. |
| 2371 | 9 | `public double getTradeCreditCash()` | What trade credit moved its till by at the last strike, net, in: the credit it took less what it repaid, as a buyer; what it was repaid less what it let its buyers have, as a supplier. |
| 2382 | 1 | `public double purchaseShare()` | The share of every order for stock the budget funds this clearing: 1 when they all fit. |
| 2384 | 4 | `public double purchaseShare(Good g)` | ...of an order for this good: more, for one its suppliers' credit covers (0.7.44). |
| 2389 | 1 | `public double purchasesLeft()` | What is left of what it can pay for, in money. |
| 2392 | 8 | `void noteForgone(Good g, double units, double value)` | Units of an order it did not place because it could not pay for them, and what they would have cost. |
| 2402 | 1 | `public double getPurchaseBudget()` | What the last clearing said it could pay for (infinite with nobody asking). |
| 2404 | 1 | `public double getOrderValue()` | ...what its orders came to, before the budget. |
| 2406 | 1 | `public double getPurchasesForgone()` | ...and what it did not buy for want of cash and credit, in money. |
| 2408 | 1 | `public double getUnitsForgone(Good g)` | ...of one good, in units. |
| 2410 | 1 | `public double getPurchasesForgone(Good g)` | ...and in money. |
| 2412 | 1 | `public boolean wasPurchaseLimited()` | True when the last clearing's budget cut an order. |
| 2423 | 4 | `protected final void noteShelfShort(double units, double price)` |  |
| 2428 | 1 | `public double getShelfShort()` |  |
| 2429 | 1 | `public double getShelfShortValue()` |  |
| 2432 | 3 | `protected final void bookOtherRevenue(double amount)` | Revenue that is not a sale of a good, booked into the month. |
| 2444 | 5 | `public final void sellOldMetal(double amount)` | ...and its old steel, sold to the Scrap Yards (0.7.123, batch I4; OldMetal): other revenue like any, the yards' input from it at the same strike (they bill it to themselves - billForService() - so the sales tax nets a... |
| 2457 | 6 | `protected final void bookHouseholdServices(double amount)` | Non-good household receipts: one revenue entry, tagged as consumption and named at the strike. |
| 2471 | 5 | `protected final void bookUpkeepSold(double amount)` | ...and upkeep sold (HL1; BusinessServices.sellUpkeep()): other revenue like any, named apart on the income statement as UPKEEP_SOLD whatever the sector calls the rest of its other revenue (strike()). |
| 2483 | 3 | `protected final void bookRevenueRefund(double amount)` | ...and a price given back (0.7.19): the builders' refund of material escalation to an owner whose material cost less when it was drawn than its quote allowed. |
| 2513 | 5 | `public final void billForService(String supplier, String what, double amount)` | A SERVICE BOUGHT FROM ANOTHER BUSINESS IN THE CITY, billed by the business that performed it. |
| 2531 | 5 | `protected final void payTheCity(String what, double amount)` | A DUE PAID TO THE CITY (FR3, docs/queue/FR3.md 3; the port dues): an operating cost on the input line, named, whose cash goes to the treasury and not to a business. |
| 2544 | 4 | `public final void recordCapitalPurchase(String supplier, double amount)` | A building bought from another business in the city (0.7.19): the builders' work on this sector's own premises, billed as they do it, and any escalation on it - negative for a refund. |
| 2554 | 5 | `protected final void drawPaidStock(String what, double cost)` | An input drawn from stock the sector paid for in an earlier month, at what it paid (0.7.8): a cost this month, named, and no cash - see Ledger.paidEarlier. |
| 2572 | 3 | `public final void bookLicenceFee(double amount)` | Imported patent rent, banked with this month's other service inputs (R2c). |
| 2585 | 5 | `protected final void bookImportedService(String what, double amount)` | ...AND ONE AGAIN SINCE 0.7.91 (batch O10; spec-oil 2.7): the shuttle tankers that take a platform's crude ashore, a service the world's ships sell with no good behind it. |
| 2597 | 4 | `protected final void bookExportedService(double amount)` | ...and one SOLD to the world with no good behind it (FC2): the builders' spare points worked abroad (sectors.Construction.sellPointsAbroad()). |

### THE STATEMENT (lines 2602-2867)

| line | len | member | says |
|---:|---:|---|---|
| 2610 | 74 | **type** `public static final class Statement` | The month's figures, as the statement was struck. |
| 2666 | 17 | `void scale(double s)` _(in Sector.Statement)_ |  |
| 2687 | 1 | `public Statement statement()` |  |
| 2690 | 1 | `public double getNetIncome()` | Pre-tax, before the profit tax. |
| 2693 | 1 | `public double getProfitTax()` | What the city collects from it this month. |
| 2700 | 52 | `public void strike()` | Strikes the month WITHOUT the sales tax and without banking: the ledger is read into the statement, and the VAT is struck from these figures next, by SalesTaxLedger, and cannot be known while they are written. |
| 2761 | 3 | `public void bank(double salesTaxRemitted)` | ...and banks it, once the sales tax is known. |
| 2771 | 54 | `public void bank(double salesTaxRemitted, double capitalTaxCredit)` | ...with the part of that net which is the tax credited back on buildings it bought (0.7.19): remitted net of it, but charged on the statement without it, and handed to the cash apart - see Statement.capitalTaxCredit. |
| 2827 | 1 | `protected void afterBank()` | A sector with something to clear when its month is banked says so here. |
| 2834 | 1 | `protected void beforeBank()` | ...and one with something to keep of the month's rows before they are cleared (A5, 0.7.46): called first in bank(), while every row is still the month being struck. |
| 2840 | 27 | `public void restoreStatement(Statement saved)` | The struck month, put back on load, so the first month back reads the same as the one before it and the loss counter sees what it saw. |

### THE MONTH AT THE BOTTOM - HOOKS Markets CALLS (lines 2868-2997)

| line | len | member | says |
|---:|---:|---|---|
| 2897 | 4 | `protected double plannedDemand(Good g)` | What the city will want of a good this month, for planning output. |
| 2908 | 19 | `public double getPlannedOutput(Good g)` | What it will make of a stockable good this month for the home market: nameplate at today's rate, or what brings the stock to STOCK_MONTHS of demand, whichever is less. |
| 2940 | 4 | `static double bufferSalePrice(GoodsMarket market)` | What a unit of an undemanded buffer can sell for, for the buffer gate in getPlannedOutput() (CPB2): its net export price where the world buys the good, and nothing where it does not. |
| 2954 | 3 | `protected double madeAtRate(Good g)` | What its plant could make of a stockable good this month: its nameplate at today's rate - the figure getPlannedOutput(), getExportBoundOutput() and produceStock() start from. |
| 2963 | 23 | `public double getExportBoundOutput(Good g)` | Nameplate the city cannot eat, made for export instead - if the export price clears the marginal cost of running the line, which is the energy and water and nothing else: the staff are paid either way. |
| 2991 | 6 | `public double getCostPerUnit(Good g)` | Break-even per unit this month: everything the line costs over what it makes. |

### WHOSE COST IS IT, WHEN ONE LINE MAKES TWO THINGS (lines 2998-3198)

| line | len | member | says |
|---:|---:|---|---|
| 3031 | 19 | `protected double costShareOf(Good g)` |  |
| 3058 | 6 | `public double getMarginalCostPerUnit(Good g)` | What it costs to SELL a unit already made: the energy and water, and the inputs, and not the payroll, which is paid whether or not a unit leaves the shed. |
| 3066 | 13 | `protected double inputCostAtRate()` | What the month's inputs cost at the operating rate, at today's prices. |
| 3089 | 21 | `public double bid(Good g)` | What it wants of an input this month. |
| 3112 | 5 | `protected double pantryTarget(Good g)` | Units of a pantry good the sector aims to hold: months of recent use. |
| 3119 | 1 | `protected double recentUse(Good g)` | What it used of a pantry good last month. |
| 3131 | 19 | `public double offer(Good g, double price)` | Units it will release to the market at the price. |
| 3156 | 20 | `public void produceFlow(Good g)` | Lifts, brews or smelts a flow good for the month - what the makers bring to a market that has no stock behind it. |
| 3184 | 5 | `private double flowRunShare(Good g)` | The share of its nameplate a good is made at this month (THE ORDER, FL1; stock too since CPB1): the share of the run of the buildings that make it, nameplate-weighted - what buildingOrder() orders their inputs for - a... |
| 3191 | 4 | `double productionWeight(BuildingsTemplate t, Good g)` | This template's native joint-run weight relative to its good's fleet average; read during capture, before endOrders(). |
| 3197 | 1 | `protected double groundLimit(Good g, double asked)` | A sector whose output is limited by what is in the ground says so here. |

### CAPTIVE USE (0.7.112, batch I2; docs/studies/spec-materials.md 2.7) (lines 3199-3241)

| line | len | member | says |
|---:|---:|---|---|
| 3223 | 1 | `public boolean captive(Good g)` | Whether it uses what it makes of a good first, itself (CAPTIVE USE): false here; sectors.ProcessingSector says yes for every good it both makes and uses. |
| 3232 | 9 | `public double captiveUnits(Good g)` | The units of a captive good it makes and uses itself this month: its need at the operating rate, for the works that will run (THE ORDER: inputToMake()), as far as what it makes at the rate covers it - for a flow good ... |

### THE ORDER (WN3, 2026-10-29; the ensemble's negative GDP in months (lines 3242-3596)

| line | len | member | says |
|---:|---:|---|---|
| 3339 | 1 | `protected boolean ordersForWhatItMakes()` | Whether the sector orders its inputs for what it will make (THE ORDER): yes here; sectors.Refining says no, its crude is the run's (THE RUN, 0.7.105). |
| 3342 | 16 | `public void planOrders()` | Decides the month's order shares, before the flow goods are made (Markets.clearMonth(), after the fleets): nothing to decide for a sector that makes no traded good (THE ORDER; a flow good's since FL1). |
| 3360 | 5 | `public void endOrders()` | The market pass is over: nothing is planned, every share reads 1 again (THE ORDER). |
| 3367 | 14 | `public double orderShare(Good g)` | What share of a traded good's nameplate this month's inputs are ordered for (THE ORDER; a flow good's is FL1's): 1 for a good that is not traded and outside the market pass. |
| 3388 | 11 | `private double forecastOrder(Good g)` | What the month will make of a stockable good over its nameplate past what the sector keeps for its own works (THE ORDER): the plan for the home market and the ship's line, asked at the stock it expects to hold after t... |
| 3407 | 34 | `private double shareAboveFloors(Good g, double fraction)` | The order share at which the buildings that make a stockable good, each at the greater of the share and its committed floor (buildingOrder(), committedFloor()), make `fraction` of the good's nameplate (CPB2): exactly ... |
| 3447 | 11 | `private double forecastFlow(Good g)` | The share of a traded flow good's nameplate at the rate this month's inputs are ordered for (THE ORDER, FL1): what its own works need of it and what the home market demanded at the last strike are made at any price; o... |
| 3465 | 18 | `private double exportsAtNoLoss(Good g, double spare)` | Of `spare` units of a flow good that can only be exported, the most that sells at no loss on the world's band (THE ORDER, FL1): all of it when nothing bought makes it, or when the whole of it nets over the inputs' cos... |
| 3490 | 3 | `public double flowCostPerUnit(Good g)` | What the inputs of a unit of a flow good cost the sector (THE ORDER, FL1): over its standing buildings that make it, nameplate-weighted, each one's traded inputs at inputCost() less what its other traded goods are wor... |
| 3495 | 12 | `private double unitCost(Good g, Set<Good> costing)` | What the inputs of a unit of any good cost the sector, flow or stockable (flowCostPerUnit()'s body); `costing` holds the goods being costed up the chain, so one that loops back is priced as an input is. |
| 3509 | 14 | `private double inputsLessCoProducts(BuildingsTemplate t, Good made, Set<Good> costing)` | What a month of one building's traded inputs costs here, less its other traded goods at their worth, to no less than nothing (flowCostPerUnit()). |
| 3534 | 9 | `private double inputCost(Good i, Set<Good> costing)` | What a unit of an input costs the works that use it, for flowCostPerUnit() (THE ORDER, FL1): inputPrice() for one the sector buys; for one it makes itself (captive) and makes enough of to cover its own works' need, wh... |
| 3545 | 7 | `private double coProductValue(Good o)` | What a unit of a good a building makes beside the one asked about is worth: its net export price where the world will buy it, else its price here; 0 if neither is a number. |
| 3554 | 10 | `private double whenHolding(Good g, double units, java.util.function.DoubleSupplier ask)` | Asks what the sector's own rules say with some units of a good in the shed instead of what is there, and puts the shed back (THE ORDER). |
| 3566 | 15 | `private double buildingOrder(BuildingsTemplate t)` | The share of its nameplate a building's inputs are ordered for: the greatest of its traded goods' order shares, flow or stockable; 1 for one that makes an untraded good, or nothing (THE ORDER, FL1). |
| 3583 | 10 | `private double committedFloor(BuildingsTemplate t)` | The least share of its nameplate a building runs at whatever is ordered (CPB1): what its part of a feed committed outside the goods market needs, to no more than 1; 0 for one that uses none. |
| 3595 | 1 | `protected double committedInput(Good g)` | Feed committed before the goods orders, which the month's run must use (CPB1); none unless a sector buys outside the market. |

### THE GROUND'S SHARE OF THE ORDER (NG1, 2026-11-20) (lines 3597-3796)

| line | len | member | says |
|---:|---:|---|---|
| 3621 | 1 | `protected double groundShare(BuildingsTemplate t)` | The share of a building's run at the rate that its ground lets it make this month (NG1): exactly 1 for a building no ground limits and wherever the ground holds its whole ask, so the order is what it was to the bit; u... |
| 3627 | 8 | `protected double inputToMake(Good g)` | Units of an input its buildings use a month at the shares they run at (THE ORDER), each held to what its ground gives it (groundShare(), NG1): getInputAtCapacity(), to the bit, while every share is 1 and outside the m... |
| 3641 | 9 | `private double captiveAtFullRun(Good g)` | Units of a captive good its own works would take at their full rate (THE ORDER): captiveUnits() outside the market pass. |
| 3658 | 15 | `public void shipUnsoldFlow(Good g, GoodsMarket market)` | A flow good's unsold units, once the market has taken what it wants: shipped abroad at the export price, or lost. |
| 3675 | 1 | `public void shipUnsoldStock(Good g, GoodsMarket market)` | A processor's wholesale shed can ship after local fills (BF1); other sectors keep their own selling rules. |
| 3682 | 10 | `public void writeOffUnhousedStock()` | Losing a warehouse loses the stock it cannot hold before any sale or credit reading can use it (BF1). |
| 3694 | 1 | `public double retirementRecovery()` | Liquid stock carried after the last plant closes: a recovery floor at realizable proceeds, otherwise zero. |
| 3710 | 40 | `public void produceStock(Good g, GoodsMarket market)` | Runs the month's production of a stockable good into the warehouse, after the market has taken what it wanted from last month's stock. |
| 3752 | 3 | `protected void sellDirectOutput(Good g, GoodsMarket market, double units)` | Sells the line's surplus. |
| 3757 | 4 | `protected void takeFromStock(Good g, double units)` | Units of a good taken out of stock by a local sale or an export from the shed. |
| 3763 | 5 | `void receiveInput(Good g, double units)` | Units of an input received into the pantry, or consumed on the spot. |
| 3770 | 6 | `protected final void usePantry(Good g, double units)` | A pantry good used up this month, recorded for next month's cover. |
| 3783 | 1 | `public void sellOwnPriced(Markets markets, Game game)` | The seller-priced goods, sold: the shops to the households, the landlords' doors to the families. |
| 3786 | 1 | `public void endOfMonth(Game game)` | After every market has cleared and every good is made: anything the month still needs. |
| 3795 | 1 | `protected void afterClearing(Good g)` | One good's market has cleared this month and its fills are booked (0.7.85; Markets.clear()), told to each of its buyers: a sector whose month reads what it bought says so here - the refiners' run, held to their crude ... |

### PLANNING - the decision to grow, and to shrink (lines 3797-3917)

| line | len | member | says |
|---:|---:|---|---|
| 3814 | 3 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` | What this sector would like to build this month, or why not. |
| 3822 | 5 | `public Good planningGood()` | The good the generic planner sizes the sector by: its first stockable output, else its first output. |
| 3835 | 1 | `public double firstPlantUtilisation()` | The share of a plant's nameplate the city has to be taking, a month, before this sector sinks its FIRST plant. |
| 3844 | 1 | `public double visibleDemandOver(double months, EconomyManager economy)` | What the sector can SEE it will sell a month over the next `months`, when its customers' orders are on a book - a ceiling on the demand the maker's rule reads off the trend, since a trend read off a boom runs on after... |
| 3847 | 3 | `public final double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` | What one of its templates would clear a month, for the interest test. |
| 3855 | 17 | `public double estimatedMonthlyProfit(NewUnit t, BusinessInvestment plans)` | ...for one new building already resolved (QN1; NewUnit): every sector's estimate reads its figures off the unit and its identity off the template the unit is of. |
| 3887 | 7 | `public double[] retirementDemandAndCapacity(Game game)` | The demand the spare-capacity rule measures the sector against, and the capacity it measures. |
| 3896 | 4 | `public double unitsOf(BuildingsTemplate t)` | What one of these contributes to the measure the sector is judged on. |
| 3902 | 1 | `public boolean mayRetire(BuildingsTemplate t)` | Whether a holding of this template may be sold back this month. |
| 3908 | 1 | `public boolean bleedsWhileLent()` | Whether this sector sells plant it is using while a lender still covers its losses (AQ1): a price taker with no demand reading, whose only shrink is the distress rule (BusinessInvestment.BLEED_MONTHS). |
| 3911 | 1 | `public long limitSpareRetirement(BuildingsTemplate t, long quantity, double keepAtLeast)` | Limit a spare-capacity sale to what still leaves keepAtLeast units usable; additive sectors need no further limit. |
| 3914 | 3 | `public String noRetirementReason(boolean distress)` | Why nothing could be sold, when mayRetire() refused everything. |

### THE SCREENS (lines 3918-4137)

| line | len | member | says |
|---:|---:|---|---|
| 3927 | 11 | **type** `public record Line(Kind kind, String label, String value, Tone tone)` | One line of the operations page. |
| 3928 | 1 | **type** `public enum Kind` _(in Sector.Line)_ |  |
| 3929 | 1 | **type** `public enum Tone` _(in Sector.Line)_ |  |
| 3931 | 1 | `public static Line head(String text)` _(in Sector.Line)_ |  |
| 3932 | 1 | `public static Line note(String text)` _(in Sector.Line)_ |  |
| 3934 | 1 | `public static Line note(String shown, String whole)` _(in Sector.Line)_ | A note in two layers (0.7.21): `shown` on the page, and `whole` one click away, behind an (i). |
| 3935 | 1 | `public static Line of(String label, String value)` _(in Sector.Line)_ |  |
| 3936 | 1 | `public static Line of(String label, String value, Tone tone)` _(in Sector.Line)_ |  |
| 3945 | 1 | `public SectorStatements.Format statementFormat()` | What kind of business its formal statements read as (0.7.74; the project's spec-sector-statements.md, 4.6): the words of its revenue, its cost of sales and its middle line - the bottom three are the model's in every f... |
| 3948 | 9 | `public String inputLabel()` | What the sector's direct-cost line is called on its income statement. |
| 3974 | 1 | `public Map<String, Double> otherRevenueParts()` | The parts of this sector's revenue that are not the sale of a good, named, for the lines inside an opened Revenue. |
| 3984 | 1 | `public Map<String, Double> otherInputParts()` | ...and the same for the cost line: what is in Inputs that is not a good. |
| 4004 | 5 | `protected Map<String, Double> nameOtherRevenue()` | ...and where those names come from, read off the sector's LIVE fields at the moment the month is struck. |
| 4025 | 5 | `public List<Line> operations(Game game)` | The operations page, as data: the factory's block (plantLines()) when the sector keeps it, then the sector's own lines (ownLines()). |
| 4032 | 1 | `public boolean hasPlantBlock()` | Whether its operations page opens on the factory's block (0.7.30): false for the seven sectors whose page is all their own. |
| 4035 | 1 | `public List<Line> ownLines(Game game)` | The sector's own lines, after the factory's block (0.7.30): none unless it says more. |
| 4043 | 94 | `public List<Line> plantLines(Game game)` | The factory's block (0.7.30; operations() itself until then): the plant - staffed, running at, and the five ratios - then a block per good it makes and per good it buys. |

### SAVE AND RESTORE (lines 4138-4353)

| line | len | member | says |
|---:|---:|---|---|
| 4143 | 56 | `public SectorState toState()` | Everything about this sector that a save has to carry. |
| 4200 | 67 | `public void restore(SectorState s)` |  |
| 4269 | 14 | `public void restoreBills(SectorState s)` | The bills of the month back, over whatever a rebuild re-derived. |
| 4285 | 1 | `private static Double ratioToSave(double r)` | A ratio for the save: itself, or null when it is not a number - Gson writes no NaN, and the load then derives it. |
| 4288 | 1 | `protected void saveExtras(Map<String, Double> extras)` | A sector with state of its own - a price it walks, an order book - writes it here by name. |
| 4291 | 1 | `protected void restoreExtras(Map<String, Double> extras)` | ...and reads it back. |
| 4294 | 27 | `public void reset()` | Everything back to a founding sector. |
| 4322 | 1 | `protected void resetExtras()` |  |
| 4328 | 20 | `public void redenominate(double scale)` | Its money in the new unit. |
| 4349 | 1 | `protected void redenominateExtras(double scale)` |  |
| 4352 | 1 | `public String toString()` |  |

### THE POWER, FOR THE PICTOGRAMS (0.7.141, batch I11; IndustryView) (lines 4354-4382)

| line | len | member | says |
|---:|---:|---|---|
| 4367 | 4 | `public double getPowerMade()` | kW its buildings make themselves, at nameplate: every billed building's gen (BuildingsTemplate.getGen()) - nothing until a works makes power. |
| 4373 | 4 | `public double getPowerDrawn()` | ...and kW they draw, at nameplate, before what they make: every billed building's whole draw. |
| 4379 | 3 | `public double getGridDraw()` | ...and kW they draw from the grid past what they make: the net draw its bill is struck on (setElectricityConsumption()), beside the surplus it sells (getPowerSurplus()). |

