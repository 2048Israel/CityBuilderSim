# Game.java - 16,479 lines · 661 methods · 19 constants · model

`ham/citybuildersim/Game.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (85), [DecisionLog](DecisionLog.md) (79), [Equity](Equity.md) (67), [TreasuryLine](TreasuryLine.md) (58), [LandManager](LandManager.md) (41), [DebtQuote](DebtQuote.md) (40), [GameLog](GameLog.md) (39), [ConstructionControl](ConstructionControl.md) (36), [AgeBand](AgeBand.md) (36), [BusinessDebtManager](BusinessDebtManager.md) (35), [Debt](Debt.md) (34), [Resource](Resource.md) (30), [GameFiles](GameFiles.md) (27), [Rollover](Rollover.md) (27), [TreasuryFund](TreasuryFund.md) (24), [TaxPolicy](TaxPolicy.md) (24), [PriceIndex](PriceIndex.md) (23), [BusinessInvestment](BusinessInvestment.md) (22), [CityMap](CityMap.md) (22), [CareType](CareType.md) (22), [TreasuryJournal](TreasuryJournal.md) (21), [JobType](JobType.md) (20), [Oil](Oil.md) (20), [Sector](Sector.md) (20), [Bank](Bank.md) (20), [DebtManager](DebtManager.md) (19), [Good](Good.md) (19), [Founding](Founding.md) (18), [Markets](Markets.md) (18), [LandParcel](LandParcel.md) (17)... and 93 more

**Used by (178):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CementConcrete](CementConcrete.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityBasket](CityBasket.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConversionCheck](ConversionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [Freight](Freight.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Markets](Markets.md), [MaterialsCheck](MaterialsCheck.md), [MediumTermBond](MediumTermBond.md), [MiniMap](MiniMap.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [Pieces](Pieces.md), [PlanCheck](PlanCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SpreadPlanner](SpreadPlanner.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProducts](WoodProducts.md), [WoodProductsCheck](WoodProductsCheck.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 928 | THE FOUNDING RESERVE (2026-09-21) |
| 984 | · THE FOUNDING RECORD (0.7.10) |
| 1020 | THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) |
| 1148 | · THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) |
| 1316 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 1339 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1562 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1700 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 1858 | · THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) |
| 2000 | PRIVATE INVESTMENT |
| 2368 | · AND THE BALANCE SHEET |
| 2576 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 2662 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 2696 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 3231 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 3378 | · the preferred offer |
| 3500 | · what the fund is worth |
| 3560 | · the dial |
| 3683 | · the hand |
| 3800 | · an Insane founding |
| 3908 | THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) |
| 4006 | ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's |
| 4085 | SHRINKING |
| 4360 | THE CONSTRUCTION WARNING |
| 4410 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 4717 | THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) |
| 5033 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 5251 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 5332 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 6079 | THE BUILDERS' PRICE (0.7.19) |
| 6256 | · BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) |
| 6450 | MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) |
| 6544 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 6874 | · F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) |
| 7048 | · in the month |
| 7287 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 7553 | · THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) |
| 7770 | · A RUN OF ORDERS (0.7.40) |
| 8323 | · A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) |
| 8407 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 8742 | · THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 8817 | ROLLING WHAT FALLS DUE (0.7.13) |
| 9246 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 9506 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 9722 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 9915 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 10306 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 10444 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 10716 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 11328 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 11341 | THE READINGS THE MONTH TAKES OF THE CITY |
| 11630 | · the price of a place (2026-09-21) |
| 11765 | THE ANCHOR (0.7.42): what the city expects prices to do, and the |
| 11804 | · what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) |
| 11935 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 11959 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 12330 | the save system |
| 12798 | PAYING THE WORLD BACK |
| 12939 | THE HOLDERS ARE PAID (0.7.1) |
| 13015 | · the desk, for the households |
| 13041 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 13098 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 13245 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 13481 | · a buyback's holders outside the pools |
| 13503 | WHAT THE TREASURY ACTUALLY DID |
| 13660 | · FROM EARNED TO THE BUDGET (0.7.31) |
| 13790 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 14078 | BUYING YOUR OWN DEBT BACK |
| 14205 | WHY LAND IS NOT IN THE RENT FLOOR |
| 14586 | · · the three the monthly path sets and this did not |
| 16224 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1895 | `Game.LandNeed.Kind.ROOM` |  |
| 1895 | `Game.LandNeed.Kind.SHORTFALL` |  |
| 1895 | `Game.LandNeed.Kind.DEPOSIT` |  |
| 1895 | `Game.LandNeed.Kind.COAST` |  |
| 7285 | `Game.BuildResult.SUCCESS` |  |
| 7285 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 7285 | `Game.BuildResult.NO_LAND` |  |
| 7285 | `Game.BuildResult.NO_DEPOSIT` |  |
| 7285 | `Game.BuildResult.NO_LICENCE` |  |
| 7285 | `Game.BuildResult.FAILED` |  |
| 7285 | `Game.BuildResult.NO_COAST` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 668 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 976 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 979 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 982 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 2115 | `Game.STATIONS_SLOT` | `"Filling stations"` | Where the forecourts' word is filed among the month's investment lines (0.7.83): apart from the shops', as the bank's is. |
| 4814 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 4817 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 6274 | `Game.BITUMEN_BINDER_SHARE` | `.05` | The share of an asphalt surface's weight that is bitumen: five per cent [R18]. |
| 7299 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 8465 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 8638 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 8760 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 10322 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 10334 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 10337 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 10352 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 10361 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, WHAT AN ISSUE COSTS). |
| 10369 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 12337 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `private boolean isRunning` |  |
| 29 | `private int month` | Game state fields |
| 30 | `private double cash` |  |
| 31 | `private long population` |  |
| 32 | `private long[] jobs` |  |
| 33 | `private BuildingManager buildingManager` |  |
| 34 | `private EconomyManager economyManager` |  |
| 35 | `private PopulationManager populationManager` |  |
| 36 | `private ServicesManager servicesManager` |  |
| 37 | `private DataSave dataSave` |  |
| 38 | `private HistorySave historySave` |  |
| 47 | `private Inbox inbox` | What the city has had to say for itself. |
| 54 | `private SectorBooks sectorBooks` | The sector statements. |
| 66 | `private DecisionLog decisions` | What the player decided, and when (0.7.23; DecisionLog): every change of a policy and every spend at scale, at the month it was made. |
| 77 | `private AutoBuilder autoBuilder` | Automatic building (0.7.73; AutoBuilder): the player's switch and two sliders, the orders it placed and what held it back. |
| 89 | `private StrategicReserve reserve` | The city's strategic reserve of crude (0.7.85, batch O8; StrategicReserve): what its tanks hold and cost, the fill ordered and the release standing, and the month's trades the next strike settles. |
| 101 | `private OldMetal oldMetal` | The city's old steel (0.7.123, batch I4; OldMetal): what its scrapped cars, its demolitions, its fabricators' offcuts and its worn-out wagon sets throw out each month, which Iron & Steel's Scrap Yards buy at 70% of sc... |
| 112 | `private Ports ports` | The city's ports (0.7.86, batch O9; Ports): the berths' shares of each kind of cargo in force, and the month's tonnes by sea, struck with the railway's month (chargeFreight()). |
| 131 | `java.util.function.Consumer<Boolean> autoBuildProbeForTest` | A harness's look at the city either side of automatic building's pass (AutoBuildCheck): false before, true after. |
| 139 | `private final GameFiles gameFiles` | Where saves live and how they are written. |
| 140 | `private HistoryGrapher historyGrapher` |  |
| 141 | `private DebtManager debtManager` |  |
| 143 | `private SimulationEngine simulationEngine` |  |
| 145 | `int materialsConsumed` |  |
| 149 | `double receiptMaterials` | The last order's material, in units - drawn by the crews as they build. |
| 150 | `double totalBuildingCost` |  |
| 151 | `private boolean hasNewReceipt` |  |
| 152 | `String lastBuildingName` |  |
| 161 | `int lastBuildQuantity` | How MANY of them, so the receipt can say "3 x Walk-in Clinic". |
| 175 | `private int receiptSerial` | WHICH receipt this is, counted up forever. |
| 201 | `boolean reports` | Console output per month: seven sector income statements and three 12x60 ASCII graphs. |
| 202 | `boolean graphs` |  |
| 205 | `boolean initialized` | boolean |
| 898 | `private String loadFailure` | Why the last load did not happen, or null. |
| 999 | `private Founding founding` |  |
| 1040 | `private CityMap cityMap` | The city map, or null until it is asked for. |
| 1043 | `private int mapFailures` | How many months the map failed to keep up and was dropped (each one logged): what a harness asserts is none. |
| 1058 | `private boolean mapAtSeaStale` | Whether the map's oil at sea is to be handed over again before it is next read (0.7.97): set by a draw and a load. |
| 1169 | `private int mapGeneration` | Bumped whenever the city's map is thrown away (a new city, a load): a draft from before it is another city's. |
| 1173 | `private final Game game` |  |
| 1174 | `private final int generation` |  |
| 1175 | `private final CityLand land` |  |
| 1176 | `private final java.util.Map<Resource, double[]> remaining` |  |
| 1177 | `private final BuildingVisual.Type[] types` |  |
| 1178 | `private final long[] counts` |  |
| 1179 | `private volatile CityMap map` |  |
| 1303 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 1374 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 1375 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1600 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1845 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under ON OFFER (under the toggle until 0.7.26), and a short vault says here that the rest was converted. |
| 1846 | `private int lastLandReceiptMonth` |  |
| 1945 | `private BusinessInvestment.OrderWatch orderWatch` | Harnesses only (0.7.54): what every order search decides is told here as well - see BusinessInvestment.OrderWatch. |
| 1990 | `private boolean monthRun` | WHETHER A MONTH HAS RUN SINCE THE CITY WAS FOUNDED OR LOADED (0.7.102). |
| 2594 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 2603 | `private double carriedTransitBill` | ...and the month's transit bill as 6d struck it (0.7.49), applied inside the rebuild for the same reason: the rebuild would strike it at the fill the month ended on, not the one it was paid at. |
| 2614 | `private double carriedCaptiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, because the cells are not back when the road ratio is first struck (carriedCarOwnership's reason), and a j... |
| 2617 | `private double[] carriedFuelMonth` | ...and the drivers' fuel as 6d drew it (0.7.62): bill, imported part, litres - DataSave.householdFuel; null for a save from before it. |
| 2620 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 2639 | `private final double[] rowFeesSettled` | Each row's fees as the last month's waterfall was handed them: the clinic's, the schools', the fares and the fuel (0.7.49). |
| 2667 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 2944 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 3270 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 4251 | `private int wellsWornOut` | Wells retired worn out over the run (0.7.84): a count for the playtest, not saved. |
| 4278 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 4368 | `private int constructionShedMonth` |  |
| 4369 | `private double constructionShedPoints` |  |
| 4428 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 4441 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 4461 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 4476 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 4877 | `final BusinessInvestment.Decision decision` |  |
| 4878 | `final double cash, perUnitProfit` |  |
| 4879 | `final BusinessDebtManager credit` |  |
| 4880 | `final boolean banned` |  |
| 4881 | `boolean atPrime` |  |
| 4882 | `double firstRate` |  |
| 4883 | `int deskCalls` |  |
| 5023 | `private ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate` | What consider() tests an order on (0.7.113): the amount the interest is struck on, as sectors.SpreadPlanner's money gate is one replaceable piece - ON_ITS_WHOLE_COST, Jerus's decision A, unless a check or a probe set ... |
| 5210 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 5211 | `private final java.util.Set<String> heldForDownPayment` |  |
| 5292 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 5293 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 5294 | `private double salvageUsedThisMonth` |  |
| 5352 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 5490 | `private double bankedClearedAtLoad` | Construction points the last load cleared from stacks that carried them past what they owed (0.7.17; see the load path and BuildingManager.clearBankedProgress()). |
| 5803 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 5808 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 5982 | `public final int quantity` |  |
| 5983 | `public final double sticker` |  |
| 5984 | `public final double materialsNeeded` |  |
| 5986 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 5988 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 5989 | `public final double plantPrice` |  |
| 5990 | `public final double plantCost` |  |
| 5991 | `public final double materialsImported` |  |
| 5992 | `public final double materialsPrice` |  |
| 5993 | `public final double importCost` |  |
| 5999 | `public final double salesTax` | The sales tax in the price (0.7.19): what the builders remit on it, less what they claim back on the plant's material - passed on, as a shop's shelf price passes it on. |
| 6006 | `public final double allowance` | What the price allows for the material beyond the yard (0.7.19): the plant's and the world's at today's prices, net of the credit on the plant's and with the builders' tax on it - the part of the total an escalation c... |
| 6007 | `public final double total` |  |
| 6008 | `public final double landNeeded` |  |
| 6009 | `public final double landFree` |  |
| 6015 | `public final double months` | Months to finish at this month's shares of the site output, the planners' own reading (quoteMonths()), or NaN when the builders have no site output. |
| 6027 | `public final double bitumenNeeded, bitumenFromRefiners, bitumenImported, bitumenLocalCost, bitumenImportCos...` | The bitumen the order's paving takes (0.7.83, batch O6; spec-oil 2.5), in tonnes - none for anything but a paved road, a highway and a paving (Game.bitumenFor()) - what of it the refiners' tanks would sell and what th... |
| 6142 | `private boolean contractsToInfer` | Set by the load path when a save carries no payers, and read once the land price is back. |
| 6157 | `private final BuildingManager.BuildersWages buildersWages` | What the builders' crews cost a point, today and at founding. |
| 6300 | `private double bitumenTonnes, bitumenCost, bitumenImported` | The bitumen drawn for roads since this game was founded or loaded, in tonnes, what it cost the builders and the part bought abroad (0.7.83): running totals for the checks and the playtest, never reset and not saved - ... |
| 6448 | `private double siteDrawBillable` | What the month's site draw cost the builders, as they bill it on (billableMaterial()): what the owners' escalation is struck against. |
| 6705 | `private String lastHandRefusal` | Why a demolition or a buy-out was not made, or null when it was. |
| 7051 | `private ConstructionControl.Events controlThisMonth` | This month's events, kept for the screens and the inbox: what the advance left, settled. |
| 7053 | `private double overtimePaidThisMonth` | What the treasury paid the builders this month for overtime, with their tax in it. |
| 7055 | `private double demolitionSalvageThisMonth` | What a finished demolition's material fetched this month, from the builders. |
| 7575 | `private double freshRights` | Units a month of fresh water the city may treat past what its lakes and river yield: an older city's (0.7.59). |
| 7695 | `private final Investor government` | The city itself, as a payer. |
| 8892 | `private final Rollover rollover` |  |
| 10705 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 10706 | `private LandManager landManager` |  |
| 10710 | `private boolean loadingSave` | True while loadGameSave() runs initialize(), which then founds no land: the save brings its own (0.7.57). |
| 10713 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 10714 | `private BuildLog buildLog` |  |
| 10741 | `private PopulationCohorts cohorts` |  |
| 10742 | `private FamilyModel families` |  |
| 10750 | `private Migration migration` |  |
| 10756 | `private LabourMarket labourMarket` | What labour costs. |
| 10762 | `private Education education` | The schools. |
| 10771 | `private Health health` | How much of the workforce is off sick. |
| 10779 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 11333 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 11479 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 11486 | `private HouseholdAccounts households` | The residents' own books. |
| 11495 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 11505 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 11509 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 11517 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 11521 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 11611 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 11617 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 11692 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 11702 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 11708 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 11715 | `private final Equity equity` | Who owns the city's companies. |
| 11719 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 11727 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 11737 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 11741 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 11763 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 11781 | `private final Expectations expectations` |  |
| 11941 | `private final Consumption consumption` |  |
| 11942 | `private boolean consumptionLoaded` |  |
| 11951 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 11969 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 11978 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 11979 | `private int rateHistoryFilled` |  |
| 12055 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 12068 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 12112 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 12125 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 12139 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 12157 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 12160 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 12162 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 12163 | `private double carriedRetailCapacity` |  |
| 12164 | `private double carriedRetailWant` |  |
| 12167 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 12168 | `private java.util.Map<String, String> lastInvestment` |  |
| 12171 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 12215 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 12339 | `private int monthsSinceAutosave` |  |
| 12687 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 12696 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 12821 | `private double foreignDebtRaisedThisMonth` |  |
| 12822 | `private double foreignPrincipalRepaidThisMonth` |  |
| 12823 | `private double foreignInterestPaidThisMonth` |  |
| 12875 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 12895 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 12896 | `private double cityPrincipalRepaidThisMonth` |  |
| 12910 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 12911 | `private double cityDiscountForBank` |  |
| 12914 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 12931 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 12937 | `private double bankPrincipalRepaidThisMonth` |  |
| 12965 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 13056 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 13344 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 13347 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 13491 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 13493 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 13546 | `private double treasuryOpening` |  |
| 13547 | `private double treasuryClosing` |  |
| 13548 | `private double treasuryRaised` |  |
| 13549 | `private double treasuryRepaid` |  |
| 13550 | `private double treasurySurplus` |  |
| 13551 | `private boolean treasuryRecorded` |  |
| 13570 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 13573 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 13751 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 13754 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 13762 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 13763 | `private String postAuditDriftPool` |  |
| 13764 | `private double postAuditDriftWorst` |  |
| 13828 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 13831 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 13834 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 13842 | `private final java.util.Map<String, Double> arrearsPaidTo` | This month's arrears paid down, by the sector whose till they reached (0.7.55): what its statement's arrears line reads (SectorBooks, arrearsPaid), struck at the settle and read when the month is recorded, both inside... |
| 14203 | `private long pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 16179 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 16228 | `private final Denomination denomination` |  |
| 16472 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 16455 | **type** `public class Game` |  |
| 69 | 1 | `public DecisionLog getDecisions()` | The player's decisions, oldest first (0.7.23): what the chart's flags are drawn from. |
| 80 | 1 | `public AutoBuilder getAutoBuilder()` | Automatic building (0.7.73): its settings, its log and its last pass. |
| 92 | 1 | `public StrategicReserve getReserve()` | The city's strategic reserve (0.7.85). |
| 104 | 1 | `public OldMetal getOldMetal()` | The city's old steel (0.7.123). |
| 115 | 1 | `public Ports getPorts()` | The city's ports (0.7.86). |
| 125 | 4 | `public BoatSchedule boats(java.util.List<BoatSchedule.Berth> quays)` | The month's ships (0.7.86, batch O9; BoatSchedule): the calls the ports' month billed makes, on these quays, with the lanes run away from the founding site. |
| 207 | 3 | `public Game()` |  |
| 219 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 228 | 5 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 235 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 267 | 354 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 622 | 4 | `public void run()` |  |
| 627 | 40 | `private void initialize()` |  |
| 670 | 4 | `static { ... }` |  |
| 678 | 3 | `public void newGame()` | A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. |
| 687 | 40 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 757 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 787 | 8 | `public void resumeGame()` |  |
| 795 | 61 | `public void loadGameSave(int slot)` |  |
| 865 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 873 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 888 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 890 | 6 | `public void toggleQuit()` |  |
| 900 | 1 | `public String getLoadFailure()` |  |
| 902 | 7 | `public void toggleGraphs()` |  |
| 909 | 5 | `public void toggleReports()` |  |
| 915 | 3 | `public void toggleNextMonth()` |  |
| 920 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 922 | 3 | `public int getMonth()` |  |
| 925 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 928-983)

### THE FOUNDING RECORD (0.7.10) (lines 984-1019)

| line | len | member | says |
|---:|---:|---|---|
| 1002 | 3 | `public Founding getFounding()` | How this city was founded. |
| 1007 | 1 | `public long getWorldSeed()` | The seed of the world this city stands on (0.7.56): chosen at founding, or derived from an older save. |
| 1015 | 1 | `public World getWorld()` | The world this city stands on (0.7.56): its terrain, its founding site and river, its fields of ore and oil. |
| 1018 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and every purchase since, whole blocks of a grid since 0.7.67 (CityLand). |

### THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) (lines 1020-1147)

| line | len | member | says |
|---:|---:|---|---|
| 1046 | 10 | `public CityMap getCityMap()` | The city map: drawn canonically the first time it is asked for, then kept up month by month. |
| 1071 | 49 | `CityMap.AtSea mapAtSea()` | The oil at sea as the map draws it (0.7.97, batch O13; CityMap's THE OIL AT SEA, docs/studies/spec-oil.md 2.12): each platform's jacket in the middle of the sea sites its slots hold on its field - the platforms on a f... |
| 1132 | 6 | `CityMap.RailPicture mapRail()` | The railway as the map draws its yard (0.8.1, batch FR0; CityMap's railPicture, spec-freight 4.3): whether the railway carries goods, and the share of its track's wagon sets the fleet has standing. |
| 1140 | 4 | `private static Deposit seaFieldOf(List<LandManager.SeaField> fields, int cell, int index)` | A sea field in the list by its world cell and index, or null. |
| 1146 | 1 | `public boolean hasCityMap()` | Whether the city map has been drawn: a city never asked for it has none, and its months pay nothing for it. |

### THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) (lines 1148-1315)

| line | len | member | says |
|---:|---:|---|---|
| 1172 | 32 | **type** `public static final class MapDraft` | A map to be drawn away from the screen's thread: the city's land, remains and counts as they stood, copied. |
| 1181 | 8 | `private MapDraft(Game game)` _(in Game.MapDraft)_ |  |
| 1191 | 3 | `public void draw()` _(in Game.MapDraft)_ | Draws the map on the copy: any thread, once. |
| 1196 | 1 | `public boolean drawn()` _(in Game.MapDraft)_ | Whether it has been drawn. |
| 1199 | 1 | `CityMap map()` _(in Game.MapDraft)_ | The map drawn, or null: a harness's (MapCheck 7). |
| 1202 | 1 | `public Game game()` _(in Game.MapDraft)_ | The game it was taken from. |
| 1206 | 3 | `public MapDraft mapDraft()` | A draft of the city's map, for drawing away from the screen's thread; null when the map is drawn already. |
| 1217 | 21 | `public boolean adoptMap(MapDraft draft)` | Keeps a drawn draft as the city's map, bound to its own land and kept up to the month (reconcile()), and says whether it is the city's map now. |
| 1240 | 1 | `public int getMapFailures()` | How many months the map failed to keep up and was dropped. |
| 1243 | 1 | `public BuildingVisual.Type[] getMapTypes()` | The building types as the map draws them, by id. |
| 1246 | 7 | `public long[] getMapCounts()` | Every type's standing count, by id: what the map's districts sum to. |
| 1255 | 4 | `private CityMap drawMap()` | The map drawn canonically from the city as it stands. |
| 1267 | 13 | `private void reconcileMap()` | The month's change placed on the map (CityMap.reconcile()), after the month's construction and demolitions; the land drawn again (a restatement) draws the map again. |
| 1282 | 1 | `public String getCityName()` | The city's name. |
| 1285 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 1288 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 1291 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 1294 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 1312 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset until 0.7.20, when the screen stopped saying what the money buys; NewGameChec... |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 1316-1338)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 1339-1561)

| line | len | member | says |
|---:|---:|---|---|
| 1377 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 1378 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 1380 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 1381 | 9 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 1392 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 1393 | 1 | `public double getSubsidyPaid(String key)` |  |
| 1395 | 5 | `public double getTotalSubsidyPaid()` |  |
| 1402 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 1415 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 1418 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 1437 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 1447 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 1482 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 1499 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 1515 | 5 | `public double getIncome()` | EARNED (0.7.31's name for it): the tax take less the running programmes, plus the utilities' net, at today's dials - the header's "+$X earned a month". |
| 1521 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1525 | 3 | `public double getWaterRatio()` |  |
| 1529 | 3 | `public double getRoadRatio()` |  |
| 1534 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1539 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1544 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1548 | 3 | `public LandManager getLandManager()` |  |
| 1553 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1562-1699)

| line | len | member | says |
|---:|---:|---|---|
| 1603 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1606 | 6 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1621 | 50 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1673 | 3 | `private static String usdWords(double thousands)` | Thousands of US dollars as the screens write them: "US$101.8M" (Formats.amount() with the dollar's mark). |
| 1678 | 3 | `private static String localWords(String here, double thousands)` | ...and thousands of local money, with its own mark: "D$72.0M". |
| 1689 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1696 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's price by (red when not, since 0.7.26 the only verdict on it); since 0.7.13 its button asks land... |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 1700-1857)

| line | len | member | says |
|---:|---:|---|---|
| 1733 | 5 | `public java.util.List<LandParcel> landShelf()` | The offers standing in the land office's order: cheapest ground first, per square foot of dry ground in US dollars (0.7.57: all forty) - the top-left card first. |
| 1740 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 1750 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 1760 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 1775 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 1780 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 1785 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 1797 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 1803 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 1808 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 1818 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 1849 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1854 | 3 | `public java.util.List<LandParcel> getLandListing()` | The offers standing. |

### THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) (lines 1858-1999)

| line | len | member | says |
|---:|---:|---|---|
| 1892 | 17 | **type** `public record LandNeed(Kind kind, double drySqFt, Resource resource)` | What the city needs ground for: room in general, a shortfall of dry ground, a resource's deposit, or a coast. |
| 1895 | 1 | **type** `public enum Kind` _(in Game.LandNeed)_ | The four needs. |
| 1898 | 1 | `public static LandNeed room()` _(in Game.LandNeed)_ | Room to grow. |
| 1901 | 1 | `public static LandNeed shortfall(double drySqFt)` _(in Game.LandNeed)_ | This much more dry ground than the city has free. |
| 1904 | 1 | `public static LandNeed deposit(Resource resource)` _(in Game.LandNeed)_ | A site of this resource to stand a mine or a well on. |
| 1907 | 1 | `public static LandNeed coast()` _(in Game.LandNeed)_ | Sea, for a desalination plant (batch J2). |
| 1911 | 28 | `public LandParcel bestOffer(LandNeed need)` | The offer that best meets a need, by THE BEST OFFER's rules; null when none does. |
| 1940 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1948 | 4 | `public void watchOrders(BusinessInvestment.OrderWatch watch)` | Harnesses only: tell this watch every order the three searches decide; null to stop. |
| 1953 | 3 | `public String getLastInvestment(String sector)` |  |
| 1969 | 11 | `private void deferBorrowingCosts()` | WHAT EACH BUSINESS'S BORROWING COST IT UP FRONT THIS MONTH (0.7.102, Jerus's A16): the fees the bank kept out of its loans, its mortgages' insurance premiums and its bonds' issuing costs - the month's own figures, the... |
| 1993 | 1 | `public boolean hasMonthRun()` | True once a month has run since the city was founded or loaded; false until then (0.7.102). |
| 1996 | 3 | `public java.util.Map<String, String> getLastInvestments()` | Every word the investors left last month, by the slot it was filed under (a sector's key, or the bank branch's label) - saved since 0.7.102 (A22); read-only. |

### PRIVATE INVESTMENT (lines 2000-2575)

| line | len | member | says |
|---:|---:|---|---|
| 2010 | 103 | `private void runPrivateInvestment()` |  |
| 2132 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 2145 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 2157 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 2163 | 411 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 2576-2661)

| line | len | member | says |
|---:|---:|---|---|
| 2623 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 2626 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 2629 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 2630 | 1 | `public double getHouseholdCarImports()` |  |
| 2633 | 1 | `public double getHouseholdFuel()` | ...and what they paid for fuel this month (0.7.49): what the cells paid - the refiners' shelf and the world's (0.7.62). |
| 2636 | 1 | `public double getHouseholdFuelImports()` | ...and what of it they paid the world (0.7.62): every litre with no refinery; the money audit's FuelFunded and FuelImports. |
| 2642 | 1 | `public double getRowFeesSettled(int row)` | ...one row's. |
| 2645 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 2648 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 2651 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 2654 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 2657 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 2660 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 2662-2695)

| line | len | member | says |
|---:|---:|---|---|
| 2670 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 2673 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 2676 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 2679 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 2682 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 2685 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 2688 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 2691 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 2694 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 2696-3230)

| line | len | member | says |
|---:|---:|---|---|
| 2722 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 2741 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 2752 | 3 | `public double bankTaxUnder(TaxPolicy p)` | The bank's profit tax under another policy (0.7.36, the Policy spec's M2): Bank.taxAt() at that policy's retail rate, bankProfitTaxRate()'s rule - so NEXT month's bill, the tax being in arrears. |
| 2765 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 2782 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 2790 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 2818 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 2850 | 63 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 2921 | 16 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 2998 | 96 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 3107 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 3120 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 3147 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 3164 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 3191 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 3220 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 3231-3377)

| line | len | member | says |
|---:|---:|---|---|
| 3273 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 3276 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 3281 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 3286 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 3291 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 3318 | 50 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 3370 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 3376 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 3378-3499)

| line | len | member | says |
|---:|---:|---|---|
| 3386 | 12 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 3400 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 3403 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 3406 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 3409 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 3412 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 3422 | 21 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 3445 | 7 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 3465 | 29 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 3496 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 3500-3559)

| line | len | member | says |
|---:|---:|---|---|
| 3503 | 1 | `public double fundSharesValue()` | Its shares, both books, at the city's mark (Exchange.cityMark(): the last trade, fair value once that is a year old). |
| 3505 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 3507 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 3513 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 3515 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 3517 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes, on the bank's share at the city's mark. |
| 3521 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 3523 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 3527 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 3532 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands, at the withdrawal in force: TreasuryFund.withdrawalOn(fundValue()) - transferOn() at the default. |
| 3535 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the city's mark: the Holdings page's line for it until 0.7.39 (FundView reads each book itself now). |
| 3539 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 3543 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 3545 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 3550 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 3554 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 3560-3682)

| line | len | member | says |
|---:|---:|---|---|
| 3563 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 3564 | 7 | `public void setFundDial(double dial)` |  |
| 3573 | 1 | `public double getFundWithdrawal()` | The fund's withdrawal, a share of its whole value a month (0.7.48, C1): TreasuryFund.getWithdrawal(), Norway's rule by default. |
| 3575 | 8 | `public void setFundWithdrawal(double share)` | ...set by the player in whole steps of TreasuryFund.WITHDRAWAL_STEP, from nothing to 10% a month, and recorded. |
| 3585 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 3586 | 8 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 3604 | 14 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, month by month), a month's worth. |
| 3620 | 9 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 3635 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 3663 | 19 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 3683-3799)

| line | len | member | says |
|---:|---:|---|---|
| 3686 | 11 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 3699 | 11 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 3716 | 1 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 3719 | 8 | `public void fundBuyShares(int company, double money, double limit)` | ...at a price a share the player names (0.7.39, the spec's D2): 0 is fair value, what the rule asks at (it bids TreasuryFund.RULE_PREMIUM over since 0.7.48). |
| 3729 | 1 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 3732 | 8 | `public void fundSellShares(int company, double shares, double limit)` | ...at a price a share the player names (0.7.39): 0 is fair value. |
| 3742 | 1 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 3745 | 6 | `public void fundBuyBond(int bondId, double money, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 3753 | 1 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |
| 3756 | 7 | `public void fundSellBond(int bondId, double face, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 3765 | 3 | `public double fundCashFree()` | The fund's cash a new buy may hold: its cash less what the hand's buys already hold (TreasuryFund.handReserve(), 0.7.39). |
| 3770 | 4 | `private static String shareCount(double n)` | A count of shares in a decision's words: whole from a hundred, else to two places, and four under one - a consolidated holding is a fraction (0.7.39). |
| 3776 | 5 | `private String atLimit(double limit, boolean bond)` | A decision's words for a named price: " at D$101.35k", " at 102.80 per 100"; nothing at fair value. |
| 3790 | 9 | `public boolean fundCancelOrder(int i)` | CANCELS ONE OF THE PLAYER'S ORDERS before the step posts it (0.7.39, the spec's D3): the order at this place in TreasuryFund.getHandOrders(). |

### an Insane founding (lines 3800-3907)

| line | len | member | says |
|---:|---:|---|---|
| 3817 | 6 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar term loan, LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.landBondUsd() - the pl... |
| 3855 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 3878 | 10 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 3898 | 9 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |

### THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) (lines 3908-4005)

| line | len | member | says |
|---:|---:|---|---|
| 3935 | 10 | `public double fillReserve(double tonnes)` | Orders crude for the strategic reserve: `tonnes`, cut to the room its tanks have left and to what the treasury could pay at crude's import price (what no tonne costs more than) - bought in the month's crude market and... |
| 3952 | 6 | `public double reserveFillMost()` | The most a fill order would take now (fillReserve()'s cut): the room the reserve's tanks have left, and what the treasury could pay at crude's import price - the reach of the Fill lever on Oil's page (0.7.96, OilView). |
| 3966 | 12 | `public double releaseReserve(double tonnesAMonth)` | Releases the strategic reserve's crude, `tonnesAMonth` a month from the next clearing on, as long as it holds any; nothing stops it. |
| 3986 | 12 | `private void settleReserve()` | The reserve's month, settled at the strike beside the businesses it traded with (StrategicReserve.settle()): the treasury pays for the crude bought - a promise, the crude having landed - and takes in what the refiners... |
| 4000 | 5 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now - and since 0.7.107 what each zone holds and at what share of the price (BusinessInvestment, THE INVESTORS' GROUND BY ZONE). |

### ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's (lines 4006-4084)

| line | len | member | says |
|---:|---:|---|---|
| 4021 | 1 | `public Zoning getZoning()` | The city's zones and its forest law. |
| 4031 | 11 | `public void setZone(String sector, Zoning.Size kind, double size, double priceShare)` | Zones a sector's ground, or re-sizes or re-prices its zone: `size` a share of the city's dry ground (Zoning.Size.SHARE, 0 to 1) or square kilometres (KM2); `priceShare` 0 to 1 of the going business price, what the nex... |
| 4044 | 4 | `static String zoneSizeWords(Zoning.Zone z)` | A zone's size as the log writes it: "2% of the dry ground", "40 km²". |
| 4050 | 3 | `public void removeZone(String sector)` | Un-zones a sector: what its zone held goes back to everyone; what was sold in it stays with its owner at what it paid. |
| 4055 | 9 | `public void setForestLaw(double protectedShare, double keptShare)` | The forest law (Z6): `protectedShare` of the city's forest ground never cut nor built on, `keptShare` - at least that - never built on. |
| 4066 | 8 | `public void setZoneTax(String sector, Zoning.Tax tax, double points)` | A zone's tax row moved (Z5): the sector's own offset, in rate points - annual points for property - exactly as Policy > Taxes moves it (TaxPolicy's setters, their clamps and their log lines). |
| 4076 | 8 | `public double zoneTaxRate(String sector, Zoning.Tax tax)` | ...and what a zone's tax row reads: the sector's effective rate (TaxPolicy.effective*(), the property rate annual). |

### SHRINKING (lines 4085-4359)

| line | len | member | says |
|---:|---:|---|---|
| 4104 | 77 | `private void runRetirement()` |  |
| 4197 | 19 | `private int retireWornOutWells()` | THE WELLS PAST THEIR LIFE (0.7.84, batch O7; spec-oil 2.6): every land well lifting under ten barrels a day (sectors.Oil.wornOut(): 263 months on land) is retired this month, oldest first, by the path any retired buil... |
| 4227 | 22 | `private int decommissionAtSea(ham.citybuildersim.sectors.Oil wells)` | THE OIL AT SEA DECOMMISSIONED (0.7.91, batch O10): once the city's oil is worked out, every platform's jacket that holds no well, and - with no jacket left standing - every kilometre of pipeline, retired by the path a... |
| 4252 | 1 | `public int getWellsWornOut()` |  |
| 4261 | 15 | `private void closeBranches(int wanted)` | Closes branches (0.7.19: as many as the rule says, at once; one at a time from 0.7.11 round 2 until then): the buildings retired by the path any retired building takes (retire()), sold by their owner, retail. |
| 4279 | 1 | `public int getBranchesClosed()` |  |
| 4292 | 67 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 4360-4409)

| line | len | member | says |
|---:|---:|---|---|
| 4380 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 4392 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 4401 | 1 | `public int getConstructionShedMonth()` |  |
| 4402 | 1 | `public double getConstructionShedPoints()` |  |
| 4405 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 4410-4716)

| line | len | member | says |
|---:|---:|---|---|
| 4430 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 4443 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 4463 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 4471 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 4478 | 3 | `public double getLastWriteOff()` |  |
| 4521 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 4531 | 185 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) (lines 4717-5032)

| line | len | member | says |
|---:|---:|---|---|
| 4820 | 4 | `public static int deskCallsMost(int quantity)` | The most times an order of this many may ask the bond desk: the countdown's slices, then doubling down and halving, each at most once a bit of the order. |
| 4831 | 1 | **type** `public record Afford(int quantity, boolean atPrime, double firstRate, int deskCalls)` | What consider() decided of an order (0.7.54): the largest slice that carries its interest, 0 for none; whether any slice would have carried it at prime and its record, for the refusal; the rate at the whole order; and... |
| 4834 | 3 | `Afford sliceFor(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | Harnesses only (InvestCheck, 0.7.113; BuildCardCheck 11, 0.7.122): consider()'s answer to an order with `cash` in the till and `perUnitProfit` a building - the largest slice that carries the interest on its whole cost. |
| 4839 | 6 | `private Afford largestSliceThatCarries(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | The largest slice of the order that carries its interest - see THE LARGEST SLICE, WITHOUT COUNTING TO IT. |
| 4853 | 21 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes)` | The largest n from 1 to quantity that passes, 0 for none, found as THE LARGEST SLICE, WITHOUT COUNTING TO IT says: the countdown's first COUNTDOWN_SLICES, then doubling down, then halving. |
| 4876 | 61 | **type** `private final class Slices` | One order's slices, each asked as the countdown asked it (until 0.7.54, consider()'s loop). |
| 4885 | 6 | `Slices(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` _(in Game.Slices)_ |  |
| 4893 | 14 | `boolean passes(int n)` _(in Game.Slices)_ | Whether n of them carry the interest on their whole cost (0.7.113); what failed at prime and the rate at the whole order are kept for the refusal. |
| 4909 | 4 | `boolean carriesAtPrime(int n, double on)` _(in Game.Slices)_ | Whether n of them would carry the interest on `on` at prime and the sector's record - their whole cost since 0.7.113 (orderGate()). |
| 4915 | 12 | `void askPrimeOfTheRest(int q)` _(in Game.Slices)_ | When none passes: whether any of the q slices would have carried it at prime - every slice to PRIME_SCAN_SLICES, the four that decide it past that, and under a ban the last the till pays for. |
| 4929 | 5 | `void askPrime(int n)` _(in Game.Slices)_ | Whether n of them would have carried it at prime, as the countdown asked it of a slice that failed. |
| 4935 | 1 | `Afford result(int quantity)` _(in Game.Slices)_ |  |
| 4939 | 1 | **type** `private record Judged(double rate, double tested, boolean askedDesk)` | The rate a slice is judged at: the loan's, and the one tested - a bond's blended with the bank's where one is cheaper and raises it all; and whether the bond desk was asked. |
| 4942 | 25 | `private Judged judged(String sector, double borrowed)` | ...for a slice of `sector`'s that borrows `borrowed`. |
| 4979 | 4 | `public boolean carriesItsWholeCost(String sector, double earns, double cost)` | Whether an order by `sector` costing `cost` and earning `earns` a month would carry the interest on its whole cost, as consider() asks each slice (ON ITS WHOLE COST, 0.7.113): it earns something, and at least PROFIT_O... |
| 4996 | 6 | **type** `public record InterestTest(double earns, double cost, double on, double rate, double real, double interest,...` | carriesItsWholeCost()'s question with its figures (0.7.122, batch C1), for the build card's interest gate (BuildCard, INTEREST), so the card says what consider() would and works none of it out itself: what the order e... |
| 5000 | 1 | `public double needs()` _(in Game.InterestTest)_ | What it must earn a month to carry it: PROFIT_OVER_INTEREST times a month's interest, the bar servicesItsOwnDebt() holds it to. |
| 5004 | 10 | `public InterestTest interestTest(String sector, double earns, double cost)` | ...for an order by `sector` costing `cost` and earning `earns` a month, its till and what it holds abroad paying first. |
| 5026 | 1 | `public ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate()` | The rule consider() tests orders by: ON_ITS_WHOLE_COST unless a check or a probe set another. |
| 5029 | 3 | `public void setOrderGate(ham.citybuildersim.sectors.SpreadPlanner.MoneyGate gate)` | A check's or a probe's: tests every order consider() weighs by another rule (the counterfactual); null puts the whole cost back. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 5033-5250)

| line | len | member | says |
|---:|---:|---|---|
| 5090 | 3 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 5095 | 4 | `public boolean buysOnMortgage(String sector, BuildingsTemplate t)` | Whether this sector buys this building on an insured mortgage: a landlord's home (0.7.11) - the rule consider() and financingOf() share. |
| 5112 | 19 | `public double[] financingOf(String sector, BuildingsTemplate t)` | HOW ONE OF THESE WOULD BE PAID FOR (0.7.75, the sector statements' R4): consider()'s split of an order of one, read and never acted on - what it would cost (BusinessInvestment.getCostOf()); what the register would ask... |
| 5140 | 64 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, of what the planner asked for (Mortgage.decide(), which halves since 0.7.54) - and th... |
| 5213 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 5217 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 5230 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 5251-5331)

| line | len | member | says |
|---:|---:|---|---|
| 5289 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 5297 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 5302 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 5307 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 5310 | 21 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 5332-6078)

| line | len | member | says |
|---:|---:|---|---|
| 5345 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 5362 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 5384 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 5389 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 5492 | 1 | `public double getBankedClearedAtLoad()` |  |
| 5495 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 5517 | 29 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 5555 | 5 | `public int getConstructionOutputAtEveryPost()` | What the builders would do this month with every post offered, at the city's fill (0.7.17): the figure getConstructionOutput() was before the builders laid idle crews off. |
| 5562 | 4 | `public int getBuildingOutputAtEveryPost()` | ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. |
| 5576 | 11 | `void strikeBuildersCrews()` | The builders strike the month's crews (0.7.17): the work ahead - the repairs of the city standing now and every point still owed on site after this month's advance - against what their depots and the city's works depa... |
| 5599 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 5616 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 5653 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 5672 | 10 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 5684 | 4 | `double portsBill(double[] fill)` | The sea terminals' crews at these fill rates and their upkeep, a month (0.7.86): paid with transit's bill (TreasuryLine.TRANSIT); nothing with no terminal. |
| 5714 | 81 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 5805 | 1 | `public double getCityMaintenancePaid()` |  |
| 5810 | 4 | `public int getConstructionMaterials()` |  |
| 5814 | 4 | `public double getInterestRate()` |  |
| 5818 | 3 | `public boolean isGraphsEnabled()` |  |
| 5821 | 3 | `public boolean isReportsEnabled()` |  |
| 5848 | 85 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 5935 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 5964 | 1 | `public boolean hasNewReceipt()` |  |
| 5965 | 1 | `public void clearReceipt()` |  |
| 5967 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 5981 | 97 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 6029 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 6038 | 33 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the bitumen its paving takes, quoted (Markets.quote()), and the refiners' sales tax rate it is credited at (0.7.83). |
| 6073 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 6076 | 1 | `public double unitsBeyondYard()` _(in Game.BuildQuote)_ | The material units the crews will draw beyond the yard, which the allowance was priced on. |

### THE BUILDERS' PRICE (0.7.19) (lines 6079-6255)

| line | len | member | says |
|---:|---:|---|---|
| 6135 | 5 | `private String ownerOfOrder(BuildingsTemplate t)` | Who ordered a building of this kind, for a save that did not say (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays for them (BusinessInvestment.planBank()); the city for the rest. |
| 6149 | 6 | `private double recoveredOnOldContract(String payer, BuildingsTemplate t)` | ...and the share of the tax on it that owner gets back, for the same save (revised 0.7.19): on the tax a building of it carries at today's price (EconomyManager.taxRecoveredShare()). |
| 6168 | 18 | `private double depotWageBillPerPoint(boolean today)` | A Construction Depot's posts - the builders' own job mix - at today's wages or at the founding ladder (PayTier, in today's unit), over the points it makes. |
| 6188 | 3 | `private double buildersSalesRate()` | The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. |
| 6191 | 3 | `private double plantSalesRate()` |  |
| 6202 | 5 | `private double billableMaterial(double salvageCost, double plantCost, double importCost)` | What a draw of material cost the builders, as they bill it on (0.7.19): the salvage at what they paid for it, the plant's net of the credit they claim on it, the world's at its landed cost (its tax is charged and cred... |
| 6218 | 3 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 6223 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds)` | ...against a yard holding `yardHolds` units (0.7.40): an order in a run, priced on the yard the orders before it leave (buildRunInvoice()). |
| 6228 | 27 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead)` | ...and with `bitumenAhead` tonnes of the refiners' bitumen taken by the orders before it in a run (0.7.83; drawBitumen()). |

### BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) (lines 6256-6449)

| line | len | member | says |
|---:|---:|---|---|
| 6282 | 6 | `public double bitumenFor(BuildingsTemplate t)` | Tonnes of bitumen one of these takes (0.7.83): a road - INFRASTRUCTURE with road capacity - past a Gravel Road's material, its surface's tonnes at BITUMEN_BINDER_SHARE; none for anything else, and none in a catalogue ... |
| 6290 | 3 | `public double bitumenForPaving()` | ...and one gravel road paved (0.7.83): a Paved Road's less a Gravel Road's, which is a Paved Road's - the bed has none. |
| 6295 | 3 | `private double refinersSalesRate()` | The refiners' sales tax rate: the builders' credit on the bitumen they buy from them (0.7.83). |
| 6302 | 1 | `public double getBitumenTonnes()` |  |
| 6303 | 1 | `public double getBitumenCost()` |  |
| 6304 | 1 | `public double getBitumenImported()` |  |
| 6313 | 8 | `public Markets.Draw drawBitumen(double tonnes)` | Draws an order's bitumen (0.7.83): the builders buy it as they buy the material beyond the yard (drawMaterials()), off the refiners' tanks and the rest from the world (Markets.draw()), booked in their ledger and bille... |
| 6331 | 4 | `public double quoteMonths(BuildingsTemplate template, int quantity)` | Months an order would take to finish (0.7.17): the wait every planner reads for its lead time and order size - BuildingManager.waitFor(), the order's points over the share of the site output it would get beside everyt... |
| 6344 | 5 | `public double onSiteMonths(BuildingsTemplate template)` | Months what is on site of one building would take to finish (0.7.20): the same wait at the same output as quoteMonths(), with no order added - BuildingManager.waitOnSite(). |
| 6361 | 4 | `public double siteMonths(String key)` | ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on site would take at today's queue - a stack's by its key, or a demolition's - at the output the quote reads. |
| 6374 | 4 | `public double quoteCityMonths(BuildingsTemplate template, int quantity)` | ...and what a city order of n of these would wait if placed now (0.7.22, after the docs pass): quoteMonths() exactly with no order set and no rush on its site; otherwise where the order would land in the player's orde... |
| 6404 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 6432 | 9 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |

### MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) (lines 6450-6543)

| line | len | member | says |
|---:|---:|---|---|
| 6480 | 31 | `public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues)` | Settles the month's site work with the owners who ordered it. |
| 6517 | 7 | `private double deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 6540 | 3 | `public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 6544-6873)

| line | len | member | says |
|---:|---:|---|---|
| 6577 | 3 | `public boolean isCitysToDemolish(BuildingsTemplate t)` | What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). |
| 6582 | 3 | `public boolean isBuyOutable(BuildingsTemplate t)` | What the city may buy out and demolish: a building a sector owns. |
| 6591 | 7 | **type** `public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price, d...` | A demolition, priced and laid out BEFORE the player commits: its work, its price, what it closes, what comes back and when. |
| 6596 | 1 | `public double salvageProceeds()` _(in Game.DemolitionQuote)_ | What the builders would pay for the material today, for as much as their cash covers. |
| 6600 | 8 | **type** `public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground, d...` | A buy-out, priced: the compensation, part by part, and the demolition after it. |
| 6604 | 1 | `public double compensation()` _(in Game.BuyOutQuote)_ | What the owner is paid. |
| 6606 | 1 | `public double total()` _(in Game.BuyOutQuote)_ | ...and what it all costs the city, the demolition included. |
| 6619 | 8 | `private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months)` | D. |
| 6629 | 27 | `public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n)` | What demolishing n standing buildings of this kind would do and cost. |
| 6658 | 14 | `public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell)` | A shell's demolition: DEMOLITION_SHARE of the work it holds, SALVAGE_SHARE of the material it drew to salvage (since 0.7.106; all of it until then), its ground. |
| 6674 | 4 | `private double demolitionMonths(BuildingsTemplate t, int n, double points)` | Months a demolition of these points would take at today's queue, at the output the quote reads. |
| 6687 | 16 | `public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n)` | E. |
| 6706 | 1 | `public String getLastHandRefusal()` |  |
| 6709 | 13 | `public boolean demolish(BuildingsTemplate t, int n)` | D. |
| 6724 | 36 | `public boolean buyOutAndDemolish(BuildingsTemplate t, int n)` | E. |
| 6762 | 6 | `public int demolishable(BuildingsTemplate t)` | How many of this kind stand that no demolition has been ordered for - nor, a gravel road, its paving (0.7.70). |
| 6770 | 9 | `private void placeDemolition(DemolitionQuote q, String from, double groundPaid)` | The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). |
| 6790 | 13 | `private void closeDemolished()` | D. |
| 6805 | 17 | `public boolean demolishShell(int templateId)` | C. |
| 6830 | 14 | `public BuildQuote quoteRestart(ConstructionControl.Shell shell)` | C. |
| 6846 | 4 | `private double restartMonths(BuildingsTemplate t, int n, double pointsLeft)` | A restart's wait, by the one wait (BuildingManager.restartWait()). |
| 6852 | 21 | `public boolean restartShell(int templateId)` | C. |

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) (lines 6874-7047)

| line | len | member | says |
|---:|---:|---|---|
| 6877 | 1 | `private BuildingsTemplate paveFrom()` | The road the city can pave and the road it becomes, as this city's catalogue has them; null for one it has not. |
| 6878 | 1 | `private BuildingsTemplate paveTo()` |  |
| 6881 | 6 | `public int paveable()` | Gravel roads the city could pave now: standing, not ordered demolished, and not already being paved. |
| 6889 | 1 | `public int pavingNow()` | Gravel roads being paved now: their Paved Roads on site, the gravel roads still standing and carrying traffic. |
| 6892 | 4 | `public boolean isPavingSite(String key)` | Whether a site is the Paved Road site with paving on it, which is not stopped (ConstructionControl, F). |
| 6911 | 15 | `public BuildQuote quotePave(int n)` | F. |
| 6939 | 28 | `public boolean paveRoads(int n)` | F. |
| 6969 | 13 | `public boolean cancelSite(String key, boolean cancel)` | C. |
| 6990 | 8 | `public double[] cancelRefundNow(String key)` | C. |
| 7000 | 10 | `public boolean rushSite(String key, boolean on)` | B. |
| 7012 | 11 | `public boolean moveSite(String key, int by)` | A. |
| 7025 | 5 | `public void resetSiteOrder()` | A. |
| 7038 | 9 | `public double[] rushPreview(String key)` | B. |

### in the month (lines 7048-7286)

| line | len | member | says |
|---:|---:|---|---|
| 7057 | 1 | `public ConstructionControl.Events getControlThisMonth()` |  |
| 7058 | 1 | `public double getOvertimePaidThisMonth()` |  |
| 7059 | 1 | `public double getDemolitionSalvageThisMonth()` |  |
| 7066 | 74 | `public void settleConstructionControl(ConstructionControl.Events ev)` | Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. |
| 7142 | 14 | `private double[] overtimeWagesByType(double premium)` | The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). |
| 7176 | 106 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 7285 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 7287-7552)

| line | len | member | says |
|---:|---:|---|---|
| 7302 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 7314 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 7332 | 1 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 7335 | 1 | `public int wellsCommitted()` | ...and the Oil Wells standing, being built or ordered (0.7.62): one an oil site - since 0.7.91 the land wells and the platforms' wells. |
| 7338 | 1 | `public int landWellsCommitted()` | The land wells standing, being built or ordered (0.7.91): what the dry sites are counted against - every oil well until the platforms' came. |
| 7341 | 1 | `public int platformWellsCommitted()` | ...and the platforms' wells (0.7.91): what the platforms' slots are counted against. |
| 7344 | 12 | `private int committedWhere(java.util.function.Predicate<BuildingsTemplate> which)` | The buildings `which` picks, standing, on site or ordered. |
| 7366 | 3 | `public int committedOn(Resource r)` | The buildings that stand on a resource's sites - standing, on site or ordered (0.7.62): every template whose site is the resource's (siteOf()) - and since 0.7.91 not an offshore platform's jacket, which stands on its ... |
| 7376 | 3 | `public int committedOn(Resource r, Deposit.FieldType type)` | ...and the sites taken by the buildings that stand on one type of the resource's fields (0.7.110, batch I1; spec-materials 2.4): `type` null counts those on ANY - the Iron Mine's until batch I5, none of iron's since (... |
| 7388 | 12 | `public java.util.List<LandManager.Claim> claimsOn(Resource r)` | The works standing on a resource's sites, by template (0.7.124, batch I5; spec-materials 2.4): each one's type (null for ANY), the sites it takes and how many stand, are on site or ordered - the standing claims LandMa... |
| 7409 | 3 | `static boolean packs(BuildingsTemplate t)` | Whether an order for this building is placed on the city's fields by packing (0.7.124, batch I5): a works on the sites of a resource whose fields have types (Resource.hasTypes() - iron, and coal, stone and copper sinc... |
| 7413 | 15 | `private int committedOn(Resource r, Deposit.FieldType type, boolean everyType)` |  |
| 7437 | 10 | `public int committedFor(BuildingsTemplate t)` | What an order for this building is counted against its sites with (0.7.91, batch O10): a land well the land wells, a platform's well the platforms' wells, a platform's jacket the jackets on site (those standing have t... |
| 7459 | 3 | `public static Resource siteOf(BuildingsTemplate t)` | The resource whose sites a building stands on (0.7.62): iron for an Iron Mine, oil for an Oil Well; null for anything else. |
| 7481 | 3 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 7491 | 14 | `private boolean hasDepositFor(BuildingsTemplate template, int quantity, int before, java.util.List<LandManager.Claim> ahead)` | ...with `before` more on the same sites already put on site by the orders ahead of it in a run (0.7.40, buildRunAhead()) - and since 0.7.124 a packed works with `ahead`, the claims those orders make on its resource's ... |
| 7514 | 9 | `public double remainingFor(BuildingsTemplate template)` | What is left in the ground for a building that stands on a resource's sites (siteOf()): its resource's remainder - and since 0.7.93 (THE TWO OIL POOLS, LandManager) a land well's the ground pool's, a platform's jacket... |
| 7539 | 13 | `public int sitesFor(BuildingsTemplate template)` | The sites the city owns that a building may stand on (0.7.84, batch O7; spec-oil 2.6): its resource's sites (siteOf()) - and for a land well (sectors.Oil.isLandWell()) only the dry ones, a field whose centre is on lan... |

### THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) (lines 7553-7769)

| line | len | member | says |
|---:|---:|---|---|
| 7578 | 3 | `public double getFreshRights()` | The city's water rights, units a month (see THE FRESH WATER LIMIT AND THE COAST). |
| 7583 | 3 | `void setFreshRights(double units)` | Harnesses and the load only: sets the rights, never below nothing. |
| 7588 | 3 | `public double getFreshCap()` | The fresh water limit, units a month: what the owned lakes and river yield plus the rights. |
| 7600 | 8 | `double derivedFreshRights()` | The rights an older save is given (spec-land 2.9): the fresh plants' nameplate standing less what the city's lakes and river yield, never below nothing - raised by the last unit in the last place when the limit they m... |
| 7610 | 3 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity)` | Whether this order can stand on the city's coast: true for everything but a desalination plant and a sea terminal (0.7.86), which need owned sea. |
| 7623 | 70 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 7703 | 1 | `public Investor getGovernmentInvestor()` |  |
| 7706 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 7708 | 46 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 7755 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 7766 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |

### A RUN OF ORDERS (0.7.40) (lines 7770-8322)

| line | len | member | says |
|---:|---:|---|---|
| 7810 | 14 | `public double buildRunInvoice(java.util.Map<BuildingsTemplate, Integer> run)` | What placing these orders in turn would charge altogether: each one's quote on the yard the ones before it leave. |
| 7826 | 3 | `public double buildFundingGap(java.util.Map<BuildingsTemplate, Integer> run)` | What the treasury is short of for the whole run: its invoice less the cash, never below nothing - an overdraft counted in full, as one order's buildFundingGap(template, quantity) counts it. |
| 7831 | 3 | `public int buildRunAhead(java.util.Map<BuildingsTemplate, Integer> run)` | How many of the run's orders, from the first, would pass the checks money cannot fix - ore, the coast, licences, ground - each placed after the ones before it. |
| 7836 | 5 | `public BuildResult buildRunStop(java.util.Map<BuildingsTemplate, Integer> run)` | What the first order that would not go ahead is refused for - NO_DEPOSIT, NO_COAST, NO_LICENCE or NO_LAND - or SUCCESS when every order passes. |
| 7842 | 35 | `private int runAhead(java.util.Map<BuildingsTemplate, Integer> run, BuildResult[] stop)` |  |
| 7879 | 7 | `static int sitePool(BuildingsTemplate t)` | The sites an order is counted against in a run (0.7.91): its resource's (siteOf()), a platform's well and a platform's jacket each a pool of their own after the resources; -1 for none. |
| 7888 | 3 | `public double cashShortfall()` | What the treasury is overdrawn by right now (0.7.40): the cash below nothing, or nothing - Finances' "overdrawn by" ask. |
| 7893 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). |
| 7896 | 3 | `public double getTotalBuildingCost()` |  |
| 7899 | 3 | `public String getBuildingName()` |  |
| 7902 | 3 | `public int getBuildQuantity()` |  |
| 7907 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 7920 | 111 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 8039 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 8051 | 13 | `private String issueNote(DebtQuote quote)` | The booking every note shares - handleTBillLogic()'s, and since 0.7.13 the rollover's, which books the quote it sized (issueForRollover()). |
| 8071 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 8109 | 17 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 8169 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 8181 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 8202 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 8231 | 35 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 8272 | 5 | `private double longBondNetProceeds(double faceValue, double marketRate, int duration)` | What the city actually banks for a long bond of this face: its worth at the rate struck, less the fees, to the cent. |
| 8279 | 13 | `private DebtQuote longBondQuote(double requested, int duration, double marketRate, double before, double faceValue, double rece...` | A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. |
| 8294 | 7 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |
| 8306 | 16 | `private String issueLongBond(DebtQuote quote)` | The booking both long-bond issues share. |

### A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) (lines 8323-8406)

| line | len | member | says |
|---:|---:|---|---|
| 8351 | 8 | `private double longBondFaceForProceeds(double cashNeeded, int duration, double rounding, double marketRate)` | Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. |
| 8368 | 29 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a term bond whose CASH covers cashNeeded would cost. |
| 8399 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 8407-8741)

| line | len | member | says |
|---:|---:|---|---|
| 8449 | 14 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 8487 | 11 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 8512 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation - expected, since 0.7.42 (below) - less the world's base rate less the world's realised inflation - the one defi... |
| 8525 | 5 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the inflation savers expect (Expectations, since 0.7.42 - the same expected inflation the real rate differential reads; the year's, as the parity ... |
| 8532 | 3 | `public double realPolicyRate()` | The policy rate less the inflation people expect (0.7.45): the dial in real terms, ex ante as every real rate here is since 0.7.42. |
| 8544 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 8549 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 8552 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 8559 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 8626 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 8646 | 78 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 8731 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |

### THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 8742-8816)

| line | len | member | says |
|---:|---:|---|---|
| 8763 | 12 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | A serial bond whose CASH covers cashNeeded: quoteMediumBond() at the face that brings it. |
| 8781 | 14 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note, serial or term loan whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 8797 | 8 | `private double foreignProceedsPerFace(String type, double rate, int duration)` | What a unit of a dollar instrument's face banks at this rate, net of the spread - never under MIN_PROCEEDS_PER_FACE, so the search above always moves. |
| 8807 | 9 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 8817-10305)

| line | len | member | says |
|---:|---:|---|---|
| 8895 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 8898 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 8901 | 11 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 8919 | 8 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 8929 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not, and whether it is dollar paper rolled at home. |
| 8931 | 13 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 8946 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 8960 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 9031 | 54 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 9094 | 9 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; a serial by quoteMediumBondForCash(); a term loan by quoteLo... |
| 9105 | 4 | `private double localFace(boolean abroad, DebtQuote quote)` | A rollover quote's face in local money: a dollar quote's at the day's rate. |
| 9116 | 18 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 9137 | 3 | `private void printPopulationInfo()` | printers |
| 9142 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 9155 | 3 | `private void printUtilityInfo()` |  |
| 9159 | 3 | `private void printCityStats()` |  |
| 9166 | 832 | `private void nextMonth()` |  |
| 9999 | 278 | `private void startOfMonthUpdate()` |  |
| 10277 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 10306-10443)

| line | len | member | says |
|---:|---:|---|---|
| 10355 | 4 | `private double issuanceFee()` | The fee in today's money: founding / unit, at the expected price level since 0.7.42 (THE ANCHOR, beside restrikeMoneyConstants()). |
| 10380 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 10392 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 10412 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 10434 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |
| 10440 | 3 | `public double minimumIssueSizeUsd()` | ...in US dollars at today's rate (0.7.40): Finances' "the minimum" on the ask abroad, which is asked in dollars. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 10444-10715)

| line | len | member | says |
|---:|---:|---|---|
| 10458 | 16 | `public String getCreditRating()` |  |
| 10476 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 10499 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 10513 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 10535 | 28 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 10564 | 94 | `private void finalUpdateEconomy()` |  |
| 10660 | 4 | `private void updateConstructionCost()` |  |
| 10671 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 10682 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 10686 | 4 | `public long getHouseholdCapacity()` |  |
| 10690 | 4 | `public long getStoreCapacity()` |  |
| 10694 | 3 | `public long[] getJobs()` |  |
| 10699 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 10716-11327)

| line | len | member | says |
|---:|---:|---|---|
| 10745 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 10781 | 1 | `public PopulationCohorts getCohorts()` |  |
| 10782 | 1 | `public FamilyModel getFamilies()` |  |
| 10783 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 10784 | 1 | `public Migration getMigration()` |  |
| 10785 | 1 | `public Health getHealth()` |  |
| 10786 | 1 | `public Healthcare getHealthcare()` |  |
| 10810 | 517 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 11328-11340)

| line | len | member | says |
|---:|---:|---|---|
| 11336 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 11339 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 11341-11629)

| line | len | member | says |
|---:|---:|---|---|
| 11357 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 11376 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 11396 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 11438 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 11464 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 11474 | 3 | `public BuildLog getBuildLog()` |  |
| 11481 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 11506 | 1 | `public Unemployment getUnemployment()` |  |
| 11510 | 1 | `public Sickness getSickness()` |  |
| 11518 | 1 | `public Crime getCrime()` |  |
| 11522 | 1 | `public double getLastOrphanDeaths()` |  |
| 11523 | 1 | `public double getLastUnhousedDeaths()` |  |
| 11525 | 6 | `{ ... }` |  |
| 11533 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 11558 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 11567 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 11588 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 11599 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 11612 | 1 | `public double getStudentLoansLent()` |  |
| 11613 | 1 | `public double getStudentLoansRepaid()` |  |
| 11614 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 11618 | 1 | `public double getStudentLoanInterest()` |  |
| 11621 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 11628 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 11630-11764)

| line | len | member | says |
|---:|---:|---|---|
| 11645 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 11651 | 7 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 11660 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 11672 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 11716 | 1 | `public Equity getEquity()` |  |
| 11720 | 1 | `public Exchange getExchange()` |  |
| 11728 | 1 | `public BondMarket getBondMarket()` |  |
| 11738 | 1 | `public TreasuryFund getFund()` |  |
| 11754 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### THE ANCHOR (0.7.42): what the city expects prices to do, and the (lines 11765-11803)

| line | len | member | says |
|---:|---:|---|---|
| 11784 | 1 | `public Expectations getExpectations()` | Expected inflation, credibility and the expected price level. |
| 11787 | 1 | `public double getExpectedInflation()` | What the city expects inflation to be, a fraction a year. |
| 11790 | 1 | `public double getCredibility()` | How far the city believes the central bank, Expectations.KMIN to KMAX. |
| 11798 | 5 | `public double anchoredDrift()` | The currency's anchored drift, a fraction a year (ForeignAccounts, THE ANCHORED DRIFT): the credible part of expected inflation - credibility times the target - against the world's realised inflation, and nothing befo... |

### what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) (lines 11804-11934)

| line | len | member | says |
|---:|---:|---|---|
| 11807 | 26 | `double[] indexPrices()` | Each component's price this month, in PriceIndex's order: the shelf, the average rent paid, a meal, a luxury piece; the services' price is struck by the index from the fees. |
| 11835 | 8 | `double[] indexSpends()` | ...what the households spent on each: groceries and rent off the sectors' statements, meals and luxury off their own purchases; the services' is the index's sum of the fee lines. |
| 11850 | 14 | `double[] indexFees()` | ...each fee line's price: the clinic's fee for a general visit at the player's scale, a seat's tuition (the adult kinds' fees averaged - a price, so a shift in who studies what is not inflation), the fare a ride is ch... |
| 11866 | 8 | `double[] indexFeeSpends()` | ...and what the households paid on each fee line, off their books. |
| 11903 | 23 | `void restrikeMoneyConstants()` | Every money constant that prices something, struck at the expected price level: founding / unit x Expectations.getStruckLevel(), the level the last month ended on. |
| 11928 | 6 | `private void handOnExpectations()` | ...and what the month hands on from the anchor that is not a constant: the investors' expected inflation (0 before the basket is based), the currency's drift, and - since 0.7.43 - the drift every sticky seller's price... |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 11935-11958)

| line | len | member | says |
|---:|---:|---|---|
| 11945 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 11954 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 11957 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 11959-12329)

| line | len | member | says |
|---:|---:|---|---|
| 11971 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 11973 | 1 | `public PriceIndex getPriceIndex()` |  |
| 11975 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 11982 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 11989 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 12000 | 5 | `public Sectors.TradeByGood getTradeByGood()` | The month across the city's edge, good by good (0.7.35): the businesses' struck statements split by good, an import with no good behind it by the sector that bought it, the households' cars among the cars bought and, ... |
| 12016 | 3 | `public double getOwnReserves()` | What is left of the vault once everything owed against it is taken off: the dollar paper outstanding and the foreign money parked in the bank, both claims on the one pot (0.7.35; the Trade tab's own subtraction until ... |
| 12021 | 5 | `public double getSharesHeldAbroad()` | The city's shares in foreign hands, at each company's price - its last trade, or its fair value before one (0.7.35; the Trade tab's own sum until then). |
| 12035 | 3 | `public double getHeldAbroadPrivately()` | The businesses' and the households' paper abroad, at today's rate: what is held abroad outside the vault. |
| 12040 | 3 | `public double getHeldAbroad()` | ...and the vault with it: everything the city holds abroad. |
| 12045 | 3 | `public double getHeldHereByTheWorld()` | What the world holds here: the city's shares at their price, the businesses' bonds at face, the foreign money parked in the bank and the city's dollar paper at today's rate. |
| 12056 | 1 | `public Bank getBank()` |  |
| 12069 | 1 | `public CentralBank getCentralBank()` |  |
| 12083 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 12089 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 12092 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 12099 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 12103 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 12107 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 12174 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 12178 | 3 | `public EconomyManager getEconomyManager()` |  |
| 12181 | 3 | `public PopulationManager getPopulationManager()` |  |
| 12185 | 1 | `public LabourMarket getLabourMarket()` |  |
| 12186 | 1 | `public Education getEducation()` |  |
| 12202 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 12227 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 12269 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 12287 | 40 | `public void recordMonth()` | Files this month in the graph history. |
| 12328 | 1 | `public Inbox getInbox()` |  |
| 12329 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 12330-12797)

| line | len | member | says |
|---:|---:|---|---|
| 12341 | 3 | `public int getMonthsUntilAutosave()` |  |
| 12357 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 12369 | 311 | `public void save(int slot, String slotName)` |  |
| 12689 | 5 | `public String takeSkipFailure()` |  |
| 12698 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 12700 | 1 | `public GameFiles getGameFiles()` |  |
| 12719 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 12737 | 18 | `public void sendBuildingSave()` |  |
| 12758 | 16 | `public void loadBuildings()` |  |
| 12788 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 12798-12938)

| line | len | member | says |
|---:|---:|---|---|
| 12825 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 12826 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 12827 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 12834 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 12856 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 12923 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 12932 | 1 | `public double getCityPaperSettled()` |  |
| 12933 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 12934 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 12936 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 12939-13014)

| line | len | member | says |
|---:|---:|---|---|
| 12967 | 1 | `public double getCouponsToHouseholds()` |  |
| 12968 | 1 | `public double getPrincipalToHouseholds()` |  |
| 12969 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 12980 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 12988 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 13001 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 13015-13040)

| line | len | member | says |
|---:|---:|---|---|
| 13025 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 13041-13097)

| line | len | member | says |
|---:|---:|---|---|
| 13058 | 39 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 13098-13244)

| line | len | member | says |
|---:|---:|---|---|
| 13152 | 56 | `private void openMarketOperation()` |  |
| 13216 | 28 | `private void buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 13245-13480)

| line | len | member | says |
|---:|---:|---|---|
| 13350 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 13353 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 13360 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 13372 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 13379 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 13385 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 13398 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 13429 | 51 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 13481-13502)

| line | len | member | says |
|---:|---:|---|---|
| 13496 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 13500 | 1 | `public double getBuybackToHouseholds()` |  |
| 13501 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 13503-13659)

| line | len | member | says |
|---:|---:|---|---|
| 13575 | 13 | `private void takeTreasuryMonth()` |  |
| 13590 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 13592 | 1 | `public double getTreasuryOpening()` |  |
| 13593 | 1 | `public double getTreasuryClosing()` |  |
| 13594 | 1 | `public double getTreasuryRaised()` |  |
| 13595 | 1 | `public double getTreasuryRepaid()` |  |
| 13596 | 1 | `public double getTreasurySurplus()` |  |
| 13599 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 13609 | 3 | `public double getNetPosition()` | The city's net position (0.7.32, the Finances tab's THE BALANCE): its cash - below nothing when it is overdrawn - less the paper it owes and what its central bank has advanced it. |
| 13631 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 13643 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 13648 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 13656 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |

### FROM EARNED TO THE BUDGET (0.7.31) (lines 13660-13789)

| line | len | member | says |
|---:|---:|---|---|
| 13696 | 23 | `public java.util.List<TreasuryJournal.Entry> getEarnedToBudget()` | The steps from EARNED to the budget's balance, in the Government bridge's order, signed as they move EARNED (+ adds, - takes away); every line, at nothing too. |
| 13721 | 5 | `public double getEarnedResidual()` | What the steps leave between EARNED and the budget: the dials moved since the month was struck, and nothing in a month nobody moved one. |
| 13727 | 5 | `double[] treasuryMonthToSave()` |  |
| 13733 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 13767 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 13770 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 13772 | 1 | `public double getPostAuditDriftWorst()` |  |
| 13773 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 13774 | 4 | `public void InterestExpense(double amount)` |  |
| 13779 | 3 | `public DebtManager getDebtManager()` |  |
| 13784 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 13790-14077)

| line | len | member | says |
|---:|---:|---|---|
| 13858 | 46 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 13916 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 13921 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 13943 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 13955 | 19 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 13983 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 14008 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 14015 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 14019 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 14029 | 8 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 14039 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 14042 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 14049 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 14059 | 10 | `public double getArrearsOwedTo(String sectorKey)` | Owed and unpaid to one sector's till (0.7.55): every line payDownArrears() would pay it, by the payee it would pay. |
| 14071 | 1 | `public double getArrearsPaidTo(String sectorKey)` | What the treasury paid this sector's till of its arrears this month (0.7.55) - its statement's arrears line. |
| 14073 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 14074 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 14075 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 14076 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 14078-14204)

| line | len | member | says |
|---:|---:|---|---|
| 14091 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 14099 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 14124 | 72 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 14205-16223)

| line | len | member | says |
|---:|---:|---|---|
| 14273 | 20 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 14294 | 330 | `private void rebuildSimulationState()` |  |
| 14666 | 10 | `public void loadGame(int slot)` | Load game |
| 14684 | 22 | `private void readTheMap(int slot, Long stamp)` | THE CITY MAP, READ BACK (0.7.60): from the slot's sidecar when the save's stamp is its stamp and it was drawn on this land in this month (CityMap.readSidecar()); a save that had a map whose sidecar is missing or stale... |
| 14743 | 11 | `private void restoreOilPools(DataSave loaded)` | THE TWO OIL POOLS ON A LOAD (0.7.93, batch O10b; LandManager's THE TWO OIL POOLS): once the land is in place, the offshore pool's E as saved - or, on a save from before SAVE_FORMAT 35, the one pool's E charged to the ... |
| 14755 | 60 | `private boolean restoreLandOnTheWorld(DataSave loaded, double owned)` |  |
| 14817 | 1349 | `private void readTheSave(int slot)` | The load itself; see loadGame(). |
| 16173 | 4 | `void seedFundLedger()` | THE FUND'S COST BASIS FOR A SAVE FROM BEFORE IT (0.7.39; the project's spec-fund-0739.md, 3.5): each market lot and bond at its market value this month, flagged as such, the rescue lot exact from the counters, one TRA... |
| 16182 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 16224-16479)

| line | len | member | says |
|---:|---:|---|---|
| 16230 | 1 | `public Denomination getDenomination()` |  |
| 16233 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 16264 | 192 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 16467 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

