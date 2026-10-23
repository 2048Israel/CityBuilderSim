# Game.java - 17,798 lines · 739 methods · 20 constants · model

`ham/citybuildersim/Game.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [DecisionLog](DecisionLog.md) (92), [BuildingsTemplate](BuildingsTemplate.md) (86), [Equity](Equity.md) (67), [Debt](Debt.md) (65), [Auction](Auction.md) (64), [TreasuryLine](TreasuryLine.md) (60), [DebtQuote](DebtQuote.md) (59), [GameLog](GameLog.md) (42), [LandManager](LandManager.md) (41), [ConstructionControl](ConstructionControl.md) (36), [AgeBand](AgeBand.md) (36), [BusinessDebtManager](BusinessDebtManager.md) (35), [Rollover](Rollover.md) (30), [Resource](Resource.md) (30), [DebtManager](DebtManager.md) (29), [GameFiles](GameFiles.md) (28), [TreasuryFund](TreasuryFund.md) (24), [TaxPolicy](TaxPolicy.md) (24), [PriceIndex](PriceIndex.md) (23), [TreasuryJournal](TreasuryJournal.md) (23), [BusinessInvestment](BusinessInvestment.md) (22), [CityMap](CityMap.md) (22), [Bank](Bank.md) (22), [CareType](CareType.md) (22), [JobType](JobType.md) (20), [Oil](Oil.md) (20), [Sector](Sector.md) (20), [LongTermBond](LongTermBond.md) (20), [Good](Good.md) (19), [Founding](Founding.md) (18)... and 99 more

**Used by (199):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CementConcrete](CementConcrete.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityBasket](CityBasket.md), [CityNeeds](CityNeeds.md), [ClockCheck](ClockCheck.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConversionCheck](ConversionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Markets](Markets.md), [MaterialsCheck](MaterialsCheck.md), [MediumTermBond](MediumTermBond.md), [MiniMap](MiniMap.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [PassThroughCheck](PassThroughCheck.md), [Pieces](Pieces.md), [PlanCheck](PlanCheck.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SpreadPlanner](SpreadPlanner.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [TruckCheck](TruckCheck.md), [UiProbe](UiProbe.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProducts](WoodProducts.md), [WoodProductsCheck](WoodProductsCheck.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 985 | THE FOUNDING RESERVE (2026-09-21) |
| 1042 | · THE FOUNDING RECORD (0.7.10) |
| 1078 | THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) |
| 1206 | · THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) |
| 1377 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 1400 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1623 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1761 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 1919 | · THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) |
| 2061 | PRIVATE INVESTMENT |
| 2430 | · AND THE BALANCE SHEET |
| 2640 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 2726 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 2760 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 3297 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 3444 | · the preferred offer |
| 3566 | · what the fund is worth |
| 3626 | · the dial |
| 3753 | · the hand |
| 3870 | · an Insane founding |
| 3982 | WHAT THE D$ IS HELD TO (XR2, 2026-10-22; ForeignAccounts, THE REGIMES) |
| 4060 | THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) |
| 4158 | ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's |
| 4237 | SHRINKING |
| 4512 | THE CONSTRUCTION WARNING |
| 4562 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 4890 | THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) |
| 5206 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 5424 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 5505 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 6273 | THE BUILDERS' PRICE (0.7.19) |
| 6450 | · BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) |
| 6644 | MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) |
| 6738 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 7070 | · F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) |
| 7244 | · in the month |
| 7483 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 7749 | · THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) |
| 7975 | · A RUN OF ORDERS (0.7.40) |
| 8117 | THE CITY'S PAPER, SOLD AT AUCTION (CD2, spec-city-debt.md section 4) |
| 8669 | · A BOND SIZED TO THE CASH IT BRINGS (0.7.10, CD2) |
| 8724 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 9074 | · THE AMORTIZING BOND AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 9134 | ROLLING WHAT FALLS DUE (0.7.13) |
| 9695 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 9984 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 10204 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 10425 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 10828 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 10935 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 11234 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 11846 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 11859 | THE READINGS THE MONTH TAKES OF THE CITY |
| 12148 | · the price of a place (2026-09-21) |
| 12286 | THE ANCHOR (0.7.42): what the city expects prices to do, and the |
| 12313 | · what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) |
| 12443 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 12467 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 12869 | the save system |
| 12919 | THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) |
| 13463 | PAYING THE WORLD BACK |
| 13604 | THE HOLDERS ARE PAID (0.7.1) |
| 13730 | · the desk, for the households |
| 13826 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 13959 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 14223 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 14471 | · a buyback's holders outside the pools |
| 14496 | WHAT THE TREASURY ACTUALLY DID |
| 14689 | · FROM EARNED TO THE BUDGET (0.7.31) |
| 14828 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 15116 | BUYING YOUR OWN DEBT BACK |
| 15354 | · the call (CD4, spec-city-debt.md section 6.4; D22, D23, D24) |
| 15486 | WHY LAND IS NOT IN THE RENT FLOOR |
| 15867 | · · the three the monthly path sets and this did not |
| 17509 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1956 | `Game.LandNeed.Kind.ROOM` |  |
| 1956 | `Game.LandNeed.Kind.SHORTFALL` |  |
| 1956 | `Game.LandNeed.Kind.DEPOSIT` |  |
| 1956 | `Game.LandNeed.Kind.COAST` |  |
| 7481 | `Game.BuildResult.SUCCESS` |  |
| 7481 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 7481 | `Game.BuildResult.NO_LAND` |  |
| 7481 | `Game.BuildResult.NO_DEPOSIT` |  |
| 7481 | `Game.BuildResult.NO_LICENCE` |  |
| 7481 | `Game.BuildResult.FAILED` |  |
| 7481 | `Game.BuildResult.NO_COAST` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 243 | `Game.MARKET_SEED_SALT` | `0x4D41524B45545852L` | Salt for the D$'s market seed, so the market's path is not the world's own seed spelled again ("MARKETXR"). |
| 712 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 1034 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 1037 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 1040 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 2177 | `Game.STATIONS_SLOT` | `"Filling stations"` | Where the forecourts' word is filed among the month's investment lines (0.7.83): apart from the shops', as the bank's is. |
| 4987 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 4990 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 6468 | `Game.BITUMEN_BINDER_SHARE` | `.05` | The share of an asphalt surface's weight that is bitumen: five per cent [R18]. |
| 7495 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 8785 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 8965 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 9091 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 10850 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 10862 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 10865 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 10880 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 10891 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003). |
| 12490 | `Game.SHORTFALL_DUST` | `1e-9` | A shortfall smaller than this share of an order's cost is rounding, not a want of cash: buildFor() borrows nothing for it. |
| 12876 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 202 | `boolean reports` | Console output per month: seven sector income statements and three 12x60 ASCII graphs. |
| 203 | `boolean graphs` |  |
| 206 | `boolean initialized` | boolean |
| 955 | `private String loadFailure` | Why the last load did not happen, or null. |
| 1057 | `private Founding founding` |  |
| 1098 | `private CityMap cityMap` | The city map, or null until it is asked for. |
| 1101 | `private int mapFailures` | How many months the map failed to keep up and was dropped (each one logged): what a harness asserts is none. |
| 1116 | `private boolean mapAtSeaStale` | Whether the map's oil at sea is to be handed over again before it is next read (0.7.97): set by a draw and a load. |
| 1227 | `private int mapGeneration` | Bumped whenever the city's map is thrown away (a new city, a load): a draft from before it is another city's. |
| 1231 | `private final Game game` |  |
| 1232 | `private final int generation` |  |
| 1233 | `private final CityLand land` |  |
| 1234 | `private final java.util.Map<Resource, double[]> remaining` |  |
| 1235 | `private final BuildingVisual.Type[] types` |  |
| 1236 | `private final long[] counts` |  |
| 1238 | `private final int month` | The game's month when the draft was made: what the map's logs date the deal by (CL3). |
| 1239 | `private volatile CityMap map` |  |
| 1364 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 1435 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 1436 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1661 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1906 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under ON OFFER (under the toggle until 0.7.26), and a short vault says here that the rest was converted. |
| 1907 | `private int lastLandReceiptMonth` |  |
| 2006 | `private BusinessInvestment.OrderWatch orderWatch` | Harnesses only (0.7.54): what every order search decides is told here as well - see BusinessInvestment.OrderWatch. |
| 2051 | `private boolean monthRun` | WHETHER A MONTH HAS RUN SINCE THE CITY WAS FOUNDED OR LOADED (0.7.102). |
| 2658 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 2667 | `private double carriedTransitBill` | ...and the month's transit bill as 6d struck it (0.7.49), applied inside the rebuild for the same reason: the rebuild would strike it at the fill the month ended on, not the one it was paid at. |
| 2678 | `private double carriedCaptiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, because the cells are not back when the road ratio is first struck (carriedCarOwnership's reason), and a j... |
| 2681 | `private double[] carriedFuelMonth` | ...and the drivers' fuel as 6d drew it (0.7.62): bill, imported part, litres - DataSave.householdFuel; null for a save from before it. |
| 2684 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 2703 | `private final double[] rowFeesSettled` | Each row's fees as the last month's waterfall was handed them: the clinic's, the schools', the fares and the fuel (0.7.49). |
| 2731 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 3008 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 3336 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 4403 | `private int wellsWornOut` | Wells retired worn out over the run (0.7.84): a count for the playtest, not saved. |
| 4430 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 4520 | `private int constructionShedMonth` |  |
| 4521 | `private double constructionShedPoints` |  |
| 4580 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 4592 | `private final java.util.Map<String, String> landBlockedNeeds` | The ground each waiting sector asked for this month (PT1-M2): sector key to "needs 743 m², 301 m² free", the numbers its own refusal gave, kept beside landBlockedSectors and cleared with it, so the inbox's yearly noti... |
| 4613 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 4633 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 4650 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 5050 | `final BusinessInvestment.Decision decision` |  |
| 5051 | `final double cash, perUnitProfit` |  |
| 5052 | `final BusinessDebtManager credit` |  |
| 5053 | `final boolean banned` |  |
| 5054 | `boolean atPrime` |  |
| 5055 | `double firstRate` |  |
| 5056 | `int deskCalls` |  |
| 5196 | `private ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate` | What consider() tests an order on (0.7.113): the amount the interest is struck on, as sectors.SpreadPlanner's money gate is one replaceable piece - ON_ITS_WHOLE_COST, Jerus's decision A, unless a check or a probe set ... |
| 5383 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 5384 | `private final java.util.Set<String> heldForDownPayment` |  |
| 5465 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 5466 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 5467 | `private double salvageUsedThisMonth` |  |
| 5525 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 5663 | `private double bankedClearedAtLoad` | Construction points the last load cleared from stacks that carried them past what they owed (0.7.17; see the load path and BuildingManager.clearBankedProgress()). |
| 5985 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 5990 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 6176 | `public final int quantity` |  |
| 6177 | `public final double sticker` |  |
| 6178 | `public final double materialsNeeded` |  |
| 6180 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 6182 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 6183 | `public final double plantPrice` |  |
| 6184 | `public final double plantCost` |  |
| 6185 | `public final double materialsImported` |  |
| 6186 | `public final double materialsPrice` |  |
| 6187 | `public final double importCost` |  |
| 6193 | `public final double salesTax` | The sales tax in the price (0.7.19): what the builders remit on it, less what they claim back on the plant's material - passed on, as a shop's shelf price passes it on. |
| 6200 | `public final double allowance` | What the price allows for the material beyond the yard (0.7.19): the plant's and the world's at today's prices, net of the credit on the plant's and with the builders' tax on it - the part of the total an escalation c... |
| 6201 | `public final double total` |  |
| 6202 | `public final double landNeeded` |  |
| 6203 | `public final double landFree` |  |
| 6209 | `public final double months` | Months to finish at this month's shares of the site output, the planners' own reading (quoteMonths()), or NaN when the builders have no site output. |
| 6221 | `public final double bitumenNeeded, bitumenFromRefiners, bitumenImported, bitumenLocalCost, bitumenImportCos...` | The bitumen the order's paving takes (0.7.83, batch O6; spec-oil 2.5), in tonnes - none for anything but a paved road, a highway and a paving (Game.bitumenFor()) - what of it the refiners' tanks would sell and what th... |
| 6336 | `private boolean contractsToInfer` | Set by the load path when a save carries no payers, and read once the land price is back. |
| 6351 | `private final BuildingManager.BuildersWages buildersWages` | What the builders' crews cost a point, today and at founding. |
| 6494 | `private double bitumenTonnes, bitumenCost, bitumenImported` | The bitumen drawn for roads since this game was founded or loaded, in tonnes, what it cost the builders and the part bought abroad (0.7.83): running totals for the checks and the playtest, never reset and not saved - ... |
| 6642 | `private double siteDrawBillable` | What the month's site draw cost the builders, as they bill it on (billableMaterial()): what the owners' escalation is struck against. |
| 6901 | `private String lastHandRefusal` | Why a demolition or a buy-out was not made, or null when it was. |
| 7247 | `private ConstructionControl.Events controlThisMonth` | This month's events, kept for the screens and the inbox: what the advance left, settled. |
| 7249 | `private double overtimePaidThisMonth` | What the treasury paid the builders this month for overtime, with their tax in it. |
| 7251 | `private double demolitionSalvageThisMonth` | What a finished demolition's material fetched this month, from the builders. |
| 7771 | `private double freshRights` | Units a month of fresh water the city may treat past what its lakes and river yield: an older city's (0.7.59). |
| 7900 | `private final Investor government` | The city itself, as a payer. |
| 9216 | `private final Rollover rollover` |  |
| 9219 | `private final java.util.List<String> rolloverShortNotes` | What the month's rollover sold short and did not raise (CD2), in words, for the inbox; the record of a month that has finished, not saved. |
| 9225 | `private final java.util.List<String> rolloverTailNotes` | What the month's rollover paid a tail past the default cap for (D29, Filled or capped higher), in words, for the inbox; not saved. |
| 9235 | `private final java.util.List<TimeSkipReport.AutoSale> autoSalesThisMonth` | Every automatic sale this month - the rollover's issues and automatic building's bonds - for the time skip's report (CD2, slice 6): a record of a month that has finished, cleared at the top of the next, not saved. |
| 11223 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 11224 | `private LandManager landManager` |  |
| 11228 | `private boolean loadingSave` | True while loadGameSave() runs initialize(), which then founds no land: the save brings its own (0.7.57). |
| 11231 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 11232 | `private BuildLog buildLog` |  |
| 11259 | `private PopulationCohorts cohorts` |  |
| 11260 | `private FamilyModel families` |  |
| 11268 | `private Migration migration` |  |
| 11274 | `private LabourMarket labourMarket` | What labour costs. |
| 11280 | `private Education education` | The schools. |
| 11289 | `private Health health` | How much of the workforce is off sick. |
| 11297 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 11851 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 11997 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 12004 | `private HouseholdAccounts households` | The residents' own books. |
| 12013 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 12023 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 12027 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 12035 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 12039 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 12129 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 12135 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 12210 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 12220 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 12226 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 12233 | `private final Equity equity` | Who owns the city's companies. |
| 12237 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 12245 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 12255 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 12262 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 12284 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 12302 | `private final Expectations expectations` |  |
| 12449 | `private final Consumption consumption` |  |
| 12450 | `private boolean consumptionLoaded` |  |
| 12459 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 12477 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 12486 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 12487 | `private int rateHistoryFilled` |  |
| 12591 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 12607 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 12651 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 12664 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 12678 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 12696 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 12699 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 12701 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 12702 | `private double carriedRetailCapacity` |  |
| 12703 | `private double carriedRetailWant` |  |
| 12706 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 12707 | `private java.util.Map<String, String> lastInvestment` |  |
| 12710 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 12754 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 12878 | `private int monthsSinceAutosave` |  |
| 12948 | `private final java.util.concurrent.ConcurrentLinkedQueue<GameFiles.Result> savesWritten` | Results the writer's thread has finished, waiting for the FX thread to take them: see reportSaves(). |
| 12952 | `private java.util.function.LongSupplier saveClock` | The clock the floor is measured on (nanoseconds); checks give a city their own. |
| 12955 | `private long lastAutosaveAt` | When the last autosave was asked for, on saveClock; meaningless until autosavedYet. |
| 12957 | `private boolean autosavedYet` |  |
| 13336 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 13350 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 13486 | `private double foreignDebtRaisedThisMonth` |  |
| 13487 | `private double foreignPrincipalRepaidThisMonth` |  |
| 13488 | `private double foreignInterestPaidThisMonth` |  |
| 13540 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 13560 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 13561 | `private double cityPrincipalRepaidThisMonth` |  |
| 13575 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 13576 | `private double cityDiscountForBank` |  |
| 13579 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 13596 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 13602 | `private double bankPrincipalRepaidThisMonth` |  |
| 13640 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 13649 | `private double couponsToFirms, principalToFirms, firmsBoughtPaper` | ...and the businesses' (CD2, slice 4): the same three figures, for the playtest and the harness. |
| 13659 | `private double couponsToWorld, principalToWorld, worldBoughtPaper, paperBoughtFromWorld` | ...and the world's (CD2, slice 5), which are declared: its coupon (an income debit) and its principal (a financial outflow) leave the pools for abroad, what it paid at the settle (worldBoughtPaper) is a financial infl... |
| 13844 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 14322 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 14325 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 14484 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the world (the holders of a dollar bond among them), carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape ... |
| 14486 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 14539 | `private double treasuryOpening` |  |
| 14540 | `private double treasuryClosing` |  |
| 14541 | `private double treasuryRaised` |  |
| 14542 | `private double treasuryRepaid` |  |
| 14543 | `private double treasurySurplus` |  |
| 14544 | `private boolean treasuryRecorded` |  |
| 14563 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 14575 | `private double retiredGainSoFar` | The gain (below nothing, a loss) on paper the treasury has retired since the last strike, in local money - the carrying value less the price paid, counted as each buyback happens (repurchaseDebt()), press to press lik... |
| 14584 | `private double treasuryNotCash` | The part of the month's budget balance that is not cash (CD3): the discount amortised, which the budget charges and the treasury never pays, less the gain on paper retired, which the budget books and the treasury neve... |
| 14587 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 14789 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 14792 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 14800 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 14801 | `private String postAuditDriftPool` |  |
| 14802 | `private double postAuditDriftWorst` |  |
| 14866 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 14869 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 14872 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 14880 | `private final java.util.Map<String, Double> arrearsPaidTo` | This month's arrears paid down, by the sector whose till they reached (0.7.55): what its statement's arrears line reads (SectorBooks, arrearsPaid), struck at the settle and read when the month is recorded, both inside... |
| 15484 | `private long pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 17464 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 17513 | `private final Denomination denomination` |  |
| 17791 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 17774 | **type** `public class Game` |  |
| 69 | 1 | `public DecisionLog getDecisions()` | The player's decisions, oldest first (0.7.23): what the chart's flags are drawn from. |
| 80 | 1 | `public AutoBuilder getAutoBuilder()` | Automatic building (0.7.73): its settings, its log and its last pass. |
| 92 | 1 | `public StrategicReserve getReserve()` | The city's strategic reserve (0.7.85). |
| 104 | 1 | `public OldMetal getOldMetal()` | The city's old steel (0.7.123). |
| 115 | 1 | `public Ports getPorts()` | The city's ports (0.7.86). |
| 125 | 4 | `public BoatSchedule boats(java.util.List<BoatSchedule.Berth> quays)` | The month's ships (0.7.86, batch O9; BoatSchedule): the calls the ports' month billed makes, on these quays, with the lanes run away from the founding site. |
| 208 | 3 | `public Game()` |  |
| 220 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 229 | 5 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 236 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 250 | 3 | `static long marketSeedFor(long worldSeed)` | The seed of the D$'s market swing for a world seed (XR1; ForeignAccounts, THE FLOAT): a splitmix64 of the world seed XOR a salt. |
| 280 | 385 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 666 | 4 | `public void run()` |  |
| 671 | 40 | `private void initialize()` |  |
| 714 | 4 | `static { ... }` |  |
| 722 | 3 | `public void newGame()` | A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. |
| 731 | 40 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 801 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 831 | 8 | `public void resumeGame()` |  |
| 839 | 64 | `public void loadGameSave(int slot)` |  |
| 912 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 920 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 935 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 937 | 6 | `public void toggleQuit()` |  |
| 949 | 4 | `public void saveForQuit()` | The quit's autosave (UP1): asked for, and on disk when this returns - System.exit does not wait for the writer's thread, so quitting waits for it here. |
| 957 | 1 | `public String getLoadFailure()` |  |
| 959 | 7 | `public void toggleGraphs()` |  |
| 966 | 5 | `public void toggleReports()` |  |
| 972 | 3 | `public void toggleNextMonth()` |  |
| 977 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 979 | 3 | `public int getMonth()` |  |
| 982 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 985-1041)

### THE FOUNDING RECORD (0.7.10) (lines 1042-1077)

| line | len | member | says |
|---:|---:|---|---|
| 1060 | 3 | `public Founding getFounding()` | How this city was founded. |
| 1065 | 1 | `public long getWorldSeed()` | The seed of the world this city stands on (0.7.56): chosen at founding, or derived from an older save. |
| 1073 | 1 | `public World getWorld()` | The world this city stands on (0.7.56): its terrain, its founding site and river, its fields of ore and oil. |
| 1076 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and every purchase since, whole blocks of a grid since 0.7.67 (CityLand). |

### THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) (lines 1078-1205)

| line | len | member | says |
|---:|---:|---|---|
| 1104 | 10 | `public CityMap getCityMap()` | The city map: drawn canonically the first time it is asked for, then kept up month by month. |
| 1129 | 49 | `CityMap.AtSea mapAtSea()` | The oil at sea as the map draws it (0.7.97, batch O13; CityMap's THE OIL AT SEA, docs/studies/spec-oil.md 2.12): each platform's jacket in the middle of the sea sites its slots hold on its field - the platforms on a f... |
| 1190 | 6 | `CityMap.RailPicture mapRail()` | The railway as the map draws its yard (0.8.1, batch FR0; CityMap's railPicture, spec-freight 4.3): whether the railway carries goods, and the share of its track's wagon sets the fleet has standing. |
| 1198 | 4 | `private static Deposit seaFieldOf(List<LandManager.SeaField> fields, int cell, int index)` | A sea field in the list by its world cell and index, or null. |
| 1204 | 1 | `public boolean hasCityMap()` | Whether the city map has been drawn: a city never asked for it has none, and its months pay nothing for it. |

### THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) (lines 1206-1376)

| line | len | member | says |
|---:|---:|---|---|
| 1230 | 35 | **type** `public static final class MapDraft` | A map to be drawn away from the screen's thread: the city's land, remains and counts as they stood, copied. |
| 1241 | 9 | `private MapDraft(Game game)` _(in Game.MapDraft)_ |  |
| 1252 | 3 | `public void draw()` _(in Game.MapDraft)_ | Draws the map on the copy: any thread, once. |
| 1257 | 1 | `public boolean drawn()` _(in Game.MapDraft)_ | Whether it has been drawn. |
| 1260 | 1 | `CityMap map()` _(in Game.MapDraft)_ | The map drawn, or null: a harness's (MapCheck 7). |
| 1263 | 1 | `public Game game()` _(in Game.MapDraft)_ | The game it was taken from. |
| 1267 | 3 | `public MapDraft mapDraft()` | A draft of the city's map, for drawing away from the screen's thread; null when the map is drawn already. |
| 1278 | 21 | `public boolean adoptMap(MapDraft draft)` | Keeps a drawn draft as the city's map, bound to its own land and kept up to the month (reconcile()), and says whether it is the city's map now. |
| 1301 | 1 | `public int getMapFailures()` | How many months the map failed to keep up and was dropped. |
| 1304 | 1 | `public BuildingVisual.Type[] getMapTypes()` | The building types as the map draws them, by id. |
| 1307 | 7 | `public long[] getMapCounts()` | Every type's standing count, by id: what the map's districts sum to. |
| 1316 | 4 | `private CityMap drawMap()` | The map drawn canonically from the city as it stands. |
| 1328 | 13 | `private void reconcileMap()` | The month's change placed on the map (CityMap.reconcile()), after the month's construction and demolitions; the land drawn again (a restatement) draws the map again. |
| 1343 | 1 | `public String getCityName()` | The city's name. |
| 1346 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 1349 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 1352 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 1355 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 1373 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset until 0.7.20, when the screen stopped saying what the money buys; NewGameChec... |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 1377-1399)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 1400-1622)

| line | len | member | says |
|---:|---:|---|---|
| 1438 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 1439 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 1441 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 1442 | 9 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 1453 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 1454 | 1 | `public double getSubsidyPaid(String key)` |  |
| 1456 | 5 | `public double getTotalSubsidyPaid()` |  |
| 1463 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 1476 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 1479 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 1498 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 1508 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 1543 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 1560 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 1576 | 5 | `public double getIncome()` | EARNED (0.7.31's name for it): the tax take less the running programmes, plus the utilities' net, at today's dials - the header's "+$X earned a month". |
| 1582 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1586 | 3 | `public double getWaterRatio()` |  |
| 1590 | 3 | `public double getRoadRatio()` |  |
| 1595 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1600 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1605 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1609 | 3 | `public LandManager getLandManager()` |  |
| 1614 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1623-1760)

| line | len | member | says |
|---:|---:|---|---|
| 1664 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1667 | 6 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1682 | 50 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1734 | 3 | `private static String usdWords(double thousands)` | Thousands of US dollars as the screens write them: "US$101.8M" (Formats.amount() with the dollar's mark). |
| 1739 | 3 | `private static String localWords(String here, double thousands)` | ...and thousands of local money, with its own mark: "D$72.0M". |
| 1750 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1757 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's price by (red when not, since 0.7.26 the only verdict on it); since 0.7.13 its button asks land... |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 1761-1918)

| line | len | member | says |
|---:|---:|---|---|
| 1794 | 5 | `public java.util.List<LandParcel> landShelf()` | The offers standing in the land office's order: cheapest ground first, per square foot of dry ground in US dollars (0.7.57: all forty) - the top-left card first. |
| 1801 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 1811 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 1821 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 1836 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 1841 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 1846 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 1858 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 1864 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 1869 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 1879 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 1910 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1915 | 3 | `public java.util.List<LandParcel> getLandListing()` | The offers standing. |

### THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) (lines 1919-2060)

| line | len | member | says |
|---:|---:|---|---|
| 1953 | 17 | **type** `public record LandNeed(Kind kind, double drySqFt, Resource resource)` | What the city needs ground for: room in general, a shortfall of dry ground, a resource's deposit, or a coast. |
| 1956 | 1 | **type** `public enum Kind` _(in Game.LandNeed)_ | The four needs. |
| 1959 | 1 | `public static LandNeed room()` _(in Game.LandNeed)_ | Room to grow. |
| 1962 | 1 | `public static LandNeed shortfall(double drySqFt)` _(in Game.LandNeed)_ | This much more dry ground than the city has free. |
| 1965 | 1 | `public static LandNeed deposit(Resource resource)` _(in Game.LandNeed)_ | A site of this resource to stand a mine or a well on. |
| 1968 | 1 | `public static LandNeed coast()` _(in Game.LandNeed)_ | Sea, for a desalination plant (batch J2). |
| 1972 | 28 | `public LandParcel bestOffer(LandNeed need)` | The offer that best meets a need, by THE BEST OFFER's rules; null when none does. |
| 2001 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 2009 | 4 | `public void watchOrders(BusinessInvestment.OrderWatch watch)` | Harnesses only: tell this watch every order the three searches decide; null to stop. |
| 2014 | 3 | `public String getLastInvestment(String sector)` |  |
| 2030 | 11 | `private void deferBorrowingCosts()` | WHAT EACH BUSINESS'S BORROWING COST IT UP FRONT THIS MONTH (0.7.102, Jerus's A16): the fees the bank kept out of its loans, its mortgages' insurance premiums and its bonds' issuing costs - the month's own figures, the... |
| 2054 | 1 | `public boolean hasMonthRun()` | True once a month has run since the city was founded or loaded; false until then (0.7.102). |
| 2057 | 3 | `public java.util.Map<String, String> getLastInvestments()` | Every word the investors left last month, by the slot it was filed under (a sector's key, or the bank branch's label) - saved since 0.7.102 (A22); read-only. |

### PRIVATE INVESTMENT (lines 2061-2639)

| line | len | member | says |
|---:|---:|---|---|
| 2071 | 104 | `private void runPrivateInvestment()` |  |
| 2194 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 2207 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 2219 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 2225 | 413 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 2640-2725)

| line | len | member | says |
|---:|---:|---|---|
| 2687 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 2690 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 2693 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 2694 | 1 | `public double getHouseholdCarImports()` |  |
| 2697 | 1 | `public double getHouseholdFuel()` | ...and what they paid for fuel this month (0.7.49): what the cells paid - the refiners' shelf and the world's (0.7.62). |
| 2700 | 1 | `public double getHouseholdFuelImports()` | ...and what of it they paid the world (0.7.62): every litre with no refinery; the money audit's FuelFunded and FuelImports. |
| 2706 | 1 | `public double getRowFeesSettled(int row)` | ...one row's. |
| 2709 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 2712 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 2715 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 2718 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 2721 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 2724 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 2726-2759)

| line | len | member | says |
|---:|---:|---|---|
| 2734 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 2737 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 2740 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 2743 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 2746 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 2749 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 2752 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 2755 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 2758 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 2760-3296)

| line | len | member | says |
|---:|---:|---|---|
| 2786 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 2805 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 2816 | 3 | `public double bankTaxUnder(TaxPolicy p)` | The bank's profit tax under another policy (0.7.36, the Policy spec's M2): Bank.taxAt() at that policy's retail rate, bankProfitTaxRate()'s rule - so NEXT month's bill, the tax being in arrears. |
| 2829 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 2846 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 2854 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 2882 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 2914 | 63 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 2985 | 16 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 3062 | 98 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 3173 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 3186 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 3213 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 3230 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 3257 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 3286 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 3297-3443)

| line | len | member | says |
|---:|---:|---|---|
| 3339 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 3342 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 3347 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 3352 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 3357 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 3384 | 50 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 3436 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 3442 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 3444-3565)

| line | len | member | says |
|---:|---:|---|---|
| 3452 | 12 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 3466 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 3469 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 3472 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 3475 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 3478 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 3488 | 21 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 3511 | 7 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 3531 | 29 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 3562 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 3566-3625)

| line | len | member | says |
|---:|---:|---|---|
| 3569 | 1 | `public double fundSharesValue()` | Its shares, both books, at the city's mark (Exchange.cityMark(): the last trade, fair value once that is a year old). |
| 3571 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 3573 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 3579 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 3581 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 3583 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes, on the bank's share at the city's mark. |
| 3587 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 3589 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 3593 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 3598 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands, at the withdrawal in force: TreasuryFund.withdrawalOn(fundValue()) - transferOn() at the default. |
| 3601 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the city's mark: the Holdings page's line for it until 0.7.39 (FundView reads each book itself now). |
| 3605 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 3609 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 3611 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 3616 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 3620 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 3626-3752)

| line | len | member | says |
|---:|---:|---|---|
| 3629 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 3630 | 7 | `public void setFundDial(double dial)` |  |
| 3639 | 1 | `public double getFundWithdrawal()` | The fund's withdrawal, a share of its whole value a month (0.7.48, C1): TreasuryFund.getWithdrawal(), Norway's rule by default. |
| 3641 | 8 | `public void setFundWithdrawal(double share)` | ...set by the player in whole steps of TreasuryFund.WITHDRAWAL_STEP, from nothing to 10% a month, and recorded. |
| 3651 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 3652 | 8 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 3671 | 17 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, plus the gain on debt retired that the surplus carries and sp... |
| 3690 | 9 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 3705 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 3733 | 19 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 3753-3869)

| line | len | member | says |
|---:|---:|---|---|
| 3756 | 11 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 3769 | 11 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 3786 | 1 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 3789 | 8 | `public void fundBuyShares(int company, double money, double limit)` | ...at a price a share the player names (0.7.39, the spec's D2): 0 is fair value, what the rule asks at (it bids TreasuryFund.RULE_PREMIUM over since 0.7.48). |
| 3799 | 1 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 3802 | 8 | `public void fundSellShares(int company, double shares, double limit)` | ...at a price a share the player names (0.7.39): 0 is fair value. |
| 3812 | 1 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 3815 | 6 | `public void fundBuyBond(int bondId, double money, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 3823 | 1 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |
| 3826 | 7 | `public void fundSellBond(int bondId, double face, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 3835 | 3 | `public double fundCashFree()` | The fund's cash a new buy may hold: its cash less what the hand's buys already hold (TreasuryFund.handReserve(), 0.7.39). |
| 3840 | 4 | `private static String shareCount(double n)` | A count of shares in a decision's words: whole from a hundred, else to two places, and four under one - a consolidated holding is a fraction (0.7.39). |
| 3846 | 5 | `private String atLimit(double limit, boolean bond)` | A decision's words for a named price: " at D$101.35k", " at 102.80 per 100"; nothing at fair value. |
| 3860 | 9 | `public boolean fundCancelOrder(int i)` | CANCELS ONE OF THE PLAYER'S ORDERS before the step posts it (0.7.39, the spec's D3): the order at this place in TreasuryFund.getHandOrders(). |

### an Insane founding (lines 3870-3981)

| line | len | member | says |
|---:|---:|---|---|
| 3887 | 8 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar bond (a term loan until CD2), LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.lan... |
| 3927 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 3950 | 10 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 3972 | 9 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |

### WHAT THE D$ IS HELD TO (XR2, 2026-10-22; ForeignAccounts, THE REGIMES) (lines 3982-4059)

| line | len | member | says |
|---:|---:|---|---|
| 3992 | 7 | `public void setCurrencyRegime(ForeignAccounts.Regime regime)` | Chooses how the D$ is held: Float, Lean against falls (the default), Band or Peg. |
| 4001 | 8 | `public void repeg(double rate)` | Moves the peg (a higher rate is a devaluation); nothing outside a peg. |
| 4011 | 9 | `public void setBand(double centre, double slopeAYear, double width)` | Sets the band: its centre, the log a year it crawls and the share either side its edges sit. |
| 4022 | 8 | `public void setEdgePayer(ForeignAccounts.EdgePayer payer)` | Chooses who pays for the dollars the vault buys at the strong edge. |
| 4043 | 16 | `private void settleTheEdge()` | THE STRONG EDGE'S BILL (XR2, star 6), at the top of the month, inside the audit's window, after the redemptions: the dollars the vault took at the last reprice (ForeignAccounts.edgeBuy()) are paid for here, at the loc... |

### THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) (lines 4060-4157)

| line | len | member | says |
|---:|---:|---|---|
| 4087 | 10 | `public double fillReserve(double tonnes)` | Orders crude for the strategic reserve: `tonnes`, cut to the room its tanks have left and to what the treasury could pay at crude's import price (what no tonne costs more than) - bought in the month's crude market and... |
| 4104 | 6 | `public double reserveFillMost()` | The most a fill order would take now (fillReserve()'s cut): the room the reserve's tanks have left, and what the treasury could pay at crude's import price - the reach of the Fill lever on Oil's page (0.7.96, OilView). |
| 4118 | 12 | `public double releaseReserve(double tonnesAMonth)` | Releases the strategic reserve's crude, `tonnesAMonth` a month from the next clearing on, as long as it holds any; nothing stops it. |
| 4138 | 12 | `private void settleReserve()` | The reserve's month, settled at the strike beside the businesses it traded with (StrategicReserve.settle()): the treasury pays for the crude bought - a promise, the crude having landed - and takes in what the refiners... |
| 4152 | 5 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now - and since 0.7.107 what each zone holds and at what share of the price (BusinessInvestment, THE INVESTORS' GROUND BY ZONE). |

### ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's (lines 4158-4236)

| line | len | member | says |
|---:|---:|---|---|
| 4173 | 1 | `public Zoning getZoning()` | The city's zones and its forest law. |
| 4183 | 11 | `public void setZone(String sector, Zoning.Size kind, double size, double priceShare)` | Zones a sector's ground, or re-sizes or re-prices its zone: `size` a share of the city's dry ground (Zoning.Size.SHARE, 0 to 1) or square kilometres (KM2); `priceShare` 0 to 1 of the going business price, what the nex... |
| 4196 | 4 | `static String zoneSizeWords(Zoning.Zone z)` | A zone's size as the log writes it: "2% of the dry ground", "40 km²". |
| 4202 | 3 | `public void removeZone(String sector)` | Un-zones a sector: what its zone held goes back to everyone; what was sold in it stays with its owner at what it paid. |
| 4207 | 9 | `public void setForestLaw(double protectedShare, double keptShare)` | The forest law (Z6): `protectedShare` of the city's forest ground never cut nor built on, `keptShare` - at least that - never built on. |
| 4218 | 8 | `public void setZoneTax(String sector, Zoning.Tax tax, double points)` | A zone's tax row moved (Z5): the sector's own offset, in rate points - annual points for property - exactly as Policy > Taxes moves it (TaxPolicy's setters, their clamps and their log lines). |
| 4228 | 8 | `public double zoneTaxRate(String sector, Zoning.Tax tax)` | ...and what a zone's tax row reads: the sector's effective rate (TaxPolicy.effective*(), the property rate annual). |

### SHRINKING (lines 4237-4511)

| line | len | member | says |
|---:|---:|---|---|
| 4256 | 77 | `private void runRetirement()` |  |
| 4349 | 19 | `private int retireWornOutWells()` | THE WELLS PAST THEIR LIFE (0.7.84, batch O7; spec-oil 2.6): every land well lifting under ten barrels a day (sectors.Oil.wornOut(): 263 months on land) is retired this month, oldest first, by the path any retired buil... |
| 4379 | 22 | `private int decommissionAtSea(ham.citybuildersim.sectors.Oil wells)` | THE OIL AT SEA DECOMMISSIONED (0.7.91, batch O10): once the city's oil is worked out, every platform's jacket that holds no well, and - with no jacket left standing - every kilometre of pipeline, retired by the path a... |
| 4404 | 1 | `public int getWellsWornOut()` |  |
| 4413 | 15 | `private void closeBranches(int wanted)` | Closes branches (0.7.19: as many as the rule says, at once; one at a time from 0.7.11 round 2 until then): the buildings retired by the path any retired building takes (retire()), sold by their owner, retail. |
| 4431 | 1 | `public int getBranchesClosed()` |  |
| 4444 | 67 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 4512-4561)

| line | len | member | says |
|---:|---:|---|---|
| 4532 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 4544 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 4553 | 1 | `public int getConstructionShedMonth()` |  |
| 4554 | 1 | `public double getConstructionShedPoints()` |  |
| 4557 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 4562-4889)

| line | len | member | says |
|---:|---:|---|---|
| 4582 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 4595 | 3 | `public java.util.Map<String, String> getLandBlockedNeeds()` | Sector key to the ground it asked for this month, for every sector in getLandBlockedSectors(). |
| 4600 | 5 | `private void blockOnLand(String sector, String needs)` | A sector waiting on ground, and what it needs: a land refusal's reason less its "no land - " (BusinessInvestment.landReason()). |
| 4615 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 4635 | 8 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 4645 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 4652 | 3 | `public double getLastWriteOff()` |  |
| 4695 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 4705 | 184 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) (lines 4890-5205)

| line | len | member | says |
|---:|---:|---|---|
| 4993 | 4 | `public static int deskCallsMost(int quantity)` | The most times an order of this many may ask the bond desk: the countdown's slices, then doubling down and halving, each at most once a bit of the order. |
| 5004 | 1 | **type** `public record Afford(int quantity, boolean atPrime, double firstRate, int deskCalls)` | What consider() decided of an order (0.7.54): the largest slice that carries its interest, 0 for none; whether any slice would have carried it at prime and its record, for the refusal; the rate at the whole order; and... |
| 5007 | 3 | `Afford sliceFor(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | Harnesses only (InvestCheck, 0.7.113; BuildCardCheck 11, 0.7.122): consider()'s answer to an order with `cash` in the till and `perUnitProfit` a building - the largest slice that carries the interest on its whole cost. |
| 5012 | 6 | `private Afford largestSliceThatCarries(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | The largest slice of the order that carries its interest - see THE LARGEST SLICE, WITHOUT COUNTING TO IT. |
| 5026 | 21 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes)` | The largest n from 1 to quantity that passes, 0 for none, found as THE LARGEST SLICE, WITHOUT COUNTING TO IT says: the countdown's first COUNTDOWN_SLICES, then doubling down, then halving. |
| 5049 | 61 | **type** `private final class Slices` | One order's slices, each asked as the countdown asked it (until 0.7.54, consider()'s loop). |
| 5058 | 6 | `Slices(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` _(in Game.Slices)_ |  |
| 5066 | 14 | `boolean passes(int n)` _(in Game.Slices)_ | Whether n of them carry the interest on their whole cost (0.7.113); what failed at prime and the rate at the whole order are kept for the refusal. |
| 5082 | 4 | `boolean carriesAtPrime(int n, double on)` _(in Game.Slices)_ | Whether n of them would carry the interest on `on` at prime and the sector's record - their whole cost since 0.7.113 (orderGate()). |
| 5088 | 12 | `void askPrimeOfTheRest(int q)` _(in Game.Slices)_ | When none passes: whether any of the q slices would have carried it at prime - every slice to PRIME_SCAN_SLICES, the four that decide it past that, and under a ban the last the till pays for. |
| 5102 | 5 | `void askPrime(int n)` _(in Game.Slices)_ | Whether n of them would have carried it at prime, as the countdown asked it of a slice that failed. |
| 5108 | 1 | `Afford result(int quantity)` _(in Game.Slices)_ |  |
| 5112 | 1 | **type** `private record Judged(double rate, double tested, boolean askedDesk)` | The rate a slice is judged at: the loan's, and the one tested - a bond's blended with the bank's where one is cheaper and raises it all; and whether the bond desk was asked. |
| 5115 | 25 | `private Judged judged(String sector, double borrowed)` | ...for a slice of `sector`'s that borrows `borrowed`. |
| 5152 | 4 | `public boolean carriesItsWholeCost(String sector, double earns, double cost)` | Whether an order by `sector` costing `cost` and earning `earns` a month would carry the interest on its whole cost, as consider() asks each slice (ON ITS WHOLE COST, 0.7.113): it earns something, and at least PROFIT_O... |
| 5169 | 6 | **type** `public record InterestTest(double earns, double cost, double on, double rate, double real, double interest,...` | carriesItsWholeCost()'s question with its figures (0.7.122, batch C1), for the build card's interest gate (BuildCard, INTEREST), so the card says what consider() would and works none of it out itself: what the order e... |
| 5173 | 1 | `public double needs()` _(in Game.InterestTest)_ | What it must earn a month to carry it: PROFIT_OVER_INTEREST times a month's interest, the bar servicesItsOwnDebt() holds it to. |
| 5177 | 10 | `public InterestTest interestTest(String sector, double earns, double cost)` | ...for an order by `sector` costing `cost` and earning `earns` a month, its till and what it holds abroad paying first. |
| 5199 | 1 | `public ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate()` | The rule consider() tests orders by: ON_ITS_WHOLE_COST unless a check or a probe set another. |
| 5202 | 3 | `public void setOrderGate(ham.citybuildersim.sectors.SpreadPlanner.MoneyGate gate)` | A check's or a probe's: tests every order consider() weighs by another rule (the counterfactual); null puts the whole cost back. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 5206-5423)

| line | len | member | says |
|---:|---:|---|---|
| 5263 | 3 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 5268 | 4 | `public boolean buysOnMortgage(String sector, BuildingsTemplate t)` | Whether this sector buys this building on an insured mortgage: a landlord's home (0.7.11) - the rule consider() and financingOf() share. |
| 5285 | 19 | `public double[] financingOf(String sector, BuildingsTemplate t)` | HOW ONE OF THESE WOULD BE PAID FOR (0.7.75, the sector statements' R4): consider()'s split of an order of one, read and never acted on - what it would cost (BusinessInvestment.getCostOf()); what the register would ask... |
| 5313 | 64 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, of what the planner asked for (Mortgage.decide(), which halves since 0.7.54) - and th... |
| 5386 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 5390 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 5403 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 5424-5504)

| line | len | member | says |
|---:|---:|---|---|
| 5462 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 5470 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 5475 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 5480 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 5483 | 21 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 5505-6272)

| line | len | member | says |
|---:|---:|---|---|
| 5518 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 5535 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 5557 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 5562 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 5665 | 1 | `public double getBankedClearedAtLoad()` |  |
| 5668 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 5690 | 29 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 5728 | 5 | `public int getConstructionOutputAtEveryPost()` | What the builders would do this month with every post offered, at the city's fill (0.7.17): the figure getConstructionOutput() was before the builders laid idle crews off. |
| 5735 | 4 | `public int getBuildingOutputAtEveryPost()` | ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. |
| 5749 | 11 | `void strikeBuildersCrews()` | The builders strike the month's crews (0.7.17): the work ahead - the repairs of the city standing now and every point still owed on site after this month's advance - against what their depots and the city's works depa... |
| 5772 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 5789 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 5826 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 5845 | 10 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 5867 | 3 | `double portsBill()` | The sea terminals' repairs, a month: their upkeep, the berths the city keeps as landlord, paid with transit's bill (TreasuryLine.TRANSIT); nothing with no terminal. |
| 5896 | 81 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 5987 | 1 | `public double getCityMaintenancePaid()` |  |
| 5992 | 4 | `public int getConstructionMaterials()` |  |
| 5996 | 4 | `public double getInterestRate()` |  |
| 6000 | 3 | `public boolean isGraphsEnabled()` |  |
| 6003 | 3 | `public boolean isReportsEnabled()` |  |
| 6030 | 97 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 6129 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 6158 | 1 | `public boolean hasNewReceipt()` |  |
| 6159 | 1 | `public void clearReceipt()` |  |
| 6161 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 6175 | 97 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 6223 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 6232 | 33 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the bitumen its paving takes, quoted (Markets.quote()), and the refiners' sales tax rate it is credited at (0.7.83). |
| 6267 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 6270 | 1 | `public double unitsBeyondYard()` _(in Game.BuildQuote)_ | The material units the crews will draw beyond the yard, which the allowance was priced on. |

### THE BUILDERS' PRICE (0.7.19) (lines 6273-6449)

| line | len | member | says |
|---:|---:|---|---|
| 6329 | 5 | `private String ownerOfOrder(BuildingsTemplate t)` | Who ordered a building of this kind, for a save that did not say (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays for them (BusinessInvestment.planBank()); the city for the rest. |
| 6343 | 6 | `private double recoveredOnOldContract(String payer, BuildingsTemplate t)` | ...and the share of the tax on it that owner gets back, for the same save (revised 0.7.19): on the tax a building of it carries at today's price (EconomyManager.taxRecoveredShare()). |
| 6362 | 18 | `private double depotWageBillPerPoint(boolean today)` | A Construction Depot's posts - the builders' own job mix - at today's wages or at the founding ladder (PayTier, in today's unit), over the points it makes. |
| 6382 | 3 | `private double buildersSalesRate()` | The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. |
| 6385 | 3 | `private double plantSalesRate()` |  |
| 6396 | 5 | `private double billableMaterial(double salvageCost, double plantCost, double importCost)` | What a draw of material cost the builders, as they bill it on (0.7.19): the salvage at what they paid for it, the plant's net of the credit they claim on it, the world's at its landed cost (its tax is charged and cred... |
| 6412 | 3 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 6417 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds)` | ...against a yard holding `yardHolds` units (0.7.40): an order in a run, priced on the yard the orders before it leave (buildRunInvoice()). |
| 6422 | 27 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead)` | ...and with `bitumenAhead` tonnes of the refiners' bitumen taken by the orders before it in a run (0.7.83; drawBitumen()). |

### BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) (lines 6450-6643)

| line | len | member | says |
|---:|---:|---|---|
| 6476 | 6 | `public double bitumenFor(BuildingsTemplate t)` | Tonnes of bitumen one of these takes (0.7.83): a road - INFRASTRUCTURE with road capacity - past a Gravel Road's material, its surface's tonnes at BITUMEN_BINDER_SHARE; none for anything else, and none in a catalogue ... |
| 6484 | 3 | `public double bitumenForPaving()` | ...and one gravel road paved (0.7.83): a Paved Road's less a Gravel Road's, which is a Paved Road's - the bed has none. |
| 6489 | 3 | `private double refinersSalesRate()` | The refiners' sales tax rate: the builders' credit on the bitumen they buy from them (0.7.83). |
| 6496 | 1 | `public double getBitumenTonnes()` |  |
| 6497 | 1 | `public double getBitumenCost()` |  |
| 6498 | 1 | `public double getBitumenImported()` |  |
| 6507 | 8 | `public Markets.Draw drawBitumen(double tonnes)` | Draws an order's bitumen (0.7.83): the builders buy it as they buy the material beyond the yard (drawMaterials()), off the refiners' tanks and the rest from the world (Markets.draw()), booked in their ledger and bille... |
| 6525 | 4 | `public double quoteMonths(BuildingsTemplate template, int quantity)` | Months an order would take to finish (0.7.17): the wait every planner reads for its lead time and order size - BuildingManager.waitFor(), the order's points over the share of the site output it would get beside everyt... |
| 6538 | 5 | `public double onSiteMonths(BuildingsTemplate template)` | Months what is on site of one building would take to finish (0.7.20): the same wait at the same output as quoteMonths(), with no order added - BuildingManager.waitOnSite(). |
| 6555 | 4 | `public double siteMonths(String key)` | ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on site would take at today's queue - a stack's by its key, or a demolition's - at the output the quote reads. |
| 6568 | 4 | `public double quoteCityMonths(BuildingsTemplate template, int quantity)` | ...and what a city order of n of these would wait if placed now (0.7.22, after the docs pass): quoteMonths() exactly with no order set and no rush on its site; otherwise where the order would land in the player's orde... |
| 6598 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 6626 | 9 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |

### MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) (lines 6644-6737)

| line | len | member | says |
|---:|---:|---|---|
| 6674 | 31 | `public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues)` | Settles the month's site work with the owners who ordered it. |
| 6711 | 7 | `private double deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 6734 | 3 | `public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 6738-7069)

| line | len | member | says |
|---:|---:|---|---|
| 6771 | 3 | `public boolean isCitysToDemolish(BuildingsTemplate t)` | What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). |
| 6776 | 3 | `public boolean isBuyOutable(BuildingsTemplate t)` | What the city may buy out and demolish: a building a sector owns. |
| 6785 | 7 | **type** `public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price, d...` | A demolition, priced and laid out BEFORE the player commits: its work, its price, what it closes, what comes back and when. |
| 6790 | 1 | `public double salvageProceeds()` _(in Game.DemolitionQuote)_ | What the builders would pay for the material today, for as much as their cash covers. |
| 6794 | 8 | **type** `public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground, d...` | A buy-out, priced: the compensation, part by part, and the demolition after it. |
| 6798 | 1 | `public double compensation()` _(in Game.BuyOutQuote)_ | What the owner is paid. |
| 6800 | 1 | `public double total()` _(in Game.BuyOutQuote)_ | ...and what it all costs the city, the demolition included. |
| 6813 | 8 | `private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months)` | D. |
| 6823 | 29 | `public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n)` | What demolishing n standing buildings of this kind would do and cost. |
| 6854 | 14 | `public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell)` | A shell's demolition: DEMOLITION_SHARE of the work it holds, SALVAGE_SHARE of the material it drew to salvage (since 0.7.106; all of it until then), its ground. |
| 6870 | 4 | `private double demolitionMonths(BuildingsTemplate t, int n, double points)` | Months a demolition of these points would take at today's queue, at the output the quote reads. |
| 6883 | 16 | `public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n)` | E. |
| 6902 | 1 | `public String getLastHandRefusal()` |  |
| 6905 | 13 | `public boolean demolish(BuildingsTemplate t, int n)` | D. |
| 6920 | 36 | `public boolean buyOutAndDemolish(BuildingsTemplate t, int n)` | E. |
| 6958 | 6 | `public int demolishable(BuildingsTemplate t)` | How many of this kind stand that no demolition has been ordered for - nor, a gravel road, its paving (0.7.70). |
| 6966 | 9 | `private void placeDemolition(DemolitionQuote q, String from, double groundPaid)` | The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). |
| 6986 | 13 | `private void closeDemolished()` | D. |
| 7001 | 17 | `public boolean demolishShell(int templateId)` | C. |
| 7026 | 14 | `public BuildQuote quoteRestart(ConstructionControl.Shell shell)` | C. |
| 7042 | 4 | `private double restartMonths(BuildingsTemplate t, int n, double pointsLeft)` | A restart's wait, by the one wait (BuildingManager.restartWait()). |
| 7048 | 21 | `public boolean restartShell(int templateId)` | C. |

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) (lines 7070-7243)

| line | len | member | says |
|---:|---:|---|---|
| 7073 | 1 | `private BuildingsTemplate paveFrom()` | The road the city can pave and the road it becomes, as this city's catalogue has them; null for one it has not. |
| 7074 | 1 | `private BuildingsTemplate paveTo()` |  |
| 7077 | 6 | `public int paveable()` | Gravel roads the city could pave now: standing, not ordered demolished, and not already being paved. |
| 7085 | 1 | `public int pavingNow()` | Gravel roads being paved now: their Paved Roads on site, the gravel roads still standing and carrying traffic. |
| 7088 | 4 | `public boolean isPavingSite(String key)` | Whether a site is the Paved Road site with paving on it, which is not stopped (ConstructionControl, F). |
| 7107 | 15 | `public BuildQuote quotePave(int n)` | F. |
| 7135 | 28 | `public boolean paveRoads(int n)` | F. |
| 7165 | 13 | `public boolean cancelSite(String key, boolean cancel)` | C. |
| 7186 | 8 | `public double[] cancelRefundNow(String key)` | C. |
| 7196 | 10 | `public boolean rushSite(String key, boolean on)` | B. |
| 7208 | 11 | `public boolean moveSite(String key, int by)` | A. |
| 7221 | 5 | `public void resetSiteOrder()` | A. |
| 7234 | 9 | `public double[] rushPreview(String key)` | B. |

### in the month (lines 7244-7482)

| line | len | member | says |
|---:|---:|---|---|
| 7253 | 1 | `public ConstructionControl.Events getControlThisMonth()` |  |
| 7254 | 1 | `public double getOvertimePaidThisMonth()` |  |
| 7255 | 1 | `public double getDemolitionSalvageThisMonth()` |  |
| 7262 | 74 | `public void settleConstructionControl(ConstructionControl.Events ev)` | Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. |
| 7338 | 14 | `private double[] overtimeWagesByType(double premium)` | The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). |
| 7372 | 106 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 7481 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 7483-7748)

| line | len | member | says |
|---:|---:|---|---|
| 7498 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 7510 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 7528 | 1 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 7531 | 1 | `public int wellsCommitted()` | ...and the Oil Wells standing, being built or ordered (0.7.62): one an oil site - since 0.7.91 the land wells and the platforms' wells. |
| 7534 | 1 | `public int landWellsCommitted()` | The land wells standing, being built or ordered (0.7.91): what the dry sites are counted against - every oil well until the platforms' came. |
| 7537 | 1 | `public int platformWellsCommitted()` | ...and the platforms' wells (0.7.91): what the platforms' slots are counted against. |
| 7540 | 12 | `private int committedWhere(java.util.function.Predicate<BuildingsTemplate> which)` | The buildings `which` picks, standing, on site or ordered. |
| 7562 | 3 | `public int committedOn(Resource r)` | The buildings that stand on a resource's sites - standing, on site or ordered (0.7.62): every template whose site is the resource's (siteOf()) - and since 0.7.91 not an offshore platform's jacket, which stands on its ... |
| 7572 | 3 | `public int committedOn(Resource r, Deposit.FieldType type)` | ...and the sites taken by the buildings that stand on one type of the resource's fields (0.7.110, batch I1; spec-materials 2.4): `type` null counts those on ANY - the Iron Mine's until batch I5, none of iron's since (... |
| 7584 | 12 | `public java.util.List<LandManager.Claim> claimsOn(Resource r)` | The works standing on a resource's sites, by template (0.7.124, batch I5; spec-materials 2.4): each one's type (null for ANY), the sites it takes and how many stand, are on site or ordered - the standing claims LandMa... |
| 7605 | 3 | `static boolean packs(BuildingsTemplate t)` | Whether an order for this building is placed on the city's fields by packing (0.7.124, batch I5): a works on the sites of a resource whose fields have types (Resource.hasTypes() - iron, and coal, stone and copper sinc... |
| 7609 | 15 | `private int committedOn(Resource r, Deposit.FieldType type, boolean everyType)` |  |
| 7633 | 10 | `public int committedFor(BuildingsTemplate t)` | What an order for this building is counted against its sites with (0.7.91, batch O10): a land well the land wells, a platform's well the platforms' wells, a platform's jacket the jackets on site (those standing have t... |
| 7655 | 3 | `public static Resource siteOf(BuildingsTemplate t)` | The resource whose sites a building stands on (0.7.62): iron for an Iron Mine, oil for an Oil Well; null for anything else. |
| 7677 | 3 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 7687 | 14 | `private boolean hasDepositFor(BuildingsTemplate template, int quantity, int before, java.util.List<LandManager.Claim> ahead)` | ...with `before` more on the same sites already put on site by the orders ahead of it in a run (0.7.40, buildRunAhead()) - and since 0.7.124 a packed works with `ahead`, the claims those orders make on its resource's ... |
| 7710 | 9 | `public double remainingFor(BuildingsTemplate template)` | What is left in the ground for a building that stands on a resource's sites (siteOf()): its resource's remainder - and since 0.7.93 (THE TWO OIL POOLS, LandManager) a land well's the ground pool's, a platform's jacket... |
| 7735 | 13 | `public int sitesFor(BuildingsTemplate template)` | The sites the city owns that a building may stand on (0.7.84, batch O7; spec-oil 2.6): its resource's sites (siteOf()) - and for a land well (sectors.Oil.isLandWell()) only the dry ones, a field whose centre is on lan... |

### THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) (lines 7749-7974)

| line | len | member | says |
|---:|---:|---|---|
| 7774 | 3 | `public double getFreshRights()` | The city's water rights, units a month (see THE FRESH WATER LIMIT AND THE COAST). |
| 7779 | 3 | `void setFreshRights(double units)` | Harnesses and the load only: sets the rights, never below nothing. |
| 7784 | 3 | `public double getFreshCap()` | The fresh water limit, units a month: what the owned lakes and river yield plus the rights. |
| 7796 | 8 | `double derivedFreshRights()` | The rights an older save is given (spec-land 2.9): the fresh plants' nameplate standing less what the city's lakes and river yield, never below nothing - raised by the last unit in the last place when the limit they m... |
| 7806 | 3 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity)` | Whether this order can stand on the city's coast: true for everything but a desalination plant and a sea terminal (0.7.86), which need owned sea. |
| 7819 | 79 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 7908 | 1 | `public Investor getGovernmentInvestor()` |  |
| 7911 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 7913 | 46 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 7960 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 7971 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |

### A RUN OF ORDERS (0.7.40) (lines 7975-8116)

| line | len | member | says |
|---:|---:|---|---|
| 8015 | 14 | `public double buildRunInvoice(java.util.Map<BuildingsTemplate, Integer> run)` | What placing these orders in turn would charge altogether: each one's quote on the yard the ones before it leave. |
| 8031 | 3 | `public double buildFundingGap(java.util.Map<BuildingsTemplate, Integer> run)` | What the treasury is short of for the whole run: its invoice less the cash, never below nothing - an overdraft counted in full, as one order's buildFundingGap(template, quantity) counts it. |
| 8036 | 3 | `public int buildRunAhead(java.util.Map<BuildingsTemplate, Integer> run)` | How many of the run's orders, from the first, would pass the checks money cannot fix - ore, the coast, licences, ground - each placed after the ones before it. |
| 8041 | 5 | `public BuildResult buildRunStop(java.util.Map<BuildingsTemplate, Integer> run)` | What the first order that would not go ahead is refused for - NO_DEPOSIT, NO_COAST, NO_LICENCE or NO_LAND - or SUCCESS when every order passes. |
| 8047 | 35 | `private int runAhead(java.util.Map<BuildingsTemplate, Integer> run, BuildResult[] stop)` |  |
| 8084 | 7 | `static int sitePool(BuildingsTemplate t)` | The sites an order is counted against in a run (0.7.91): its resource's (siteOf()), a platform's well and a platform's jacket each a pool of their own after the resources; -1 for none. |
| 8093 | 3 | `public double cashShortfall()` | What the treasury is overdrawn by right now (0.7.40): the cash below nothing, or nothing - Finances' "overdrawn by" ask. |
| 8098 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). |
| 8101 | 3 | `public double getTotalBuildingCost()` |  |
| 8104 | 3 | `public String getBuildingName()` |  |
| 8107 | 3 | `public int getBuildQuantity()` |  |
| 8112 | 3 | `public int getReceiptSerial()` | Which receipt this is. |

### THE CITY'S PAPER, SOLD AT AUCTION (CD2, spec-city-debt.md section 4) (lines 8117-8668)

| line | len | member | says |
|---:|---:|---|---|
| 8159 | 3 | `public Auction.Bidders auctionBidders()` | The bidders as they stand, for a sale of paper of any term: the figure the screen's chart of the bids reads. |
| 8196 | 3 | `public Auction.Bidders auctionBidders(int months)` | The bidders as they stand for paper of this many months (0 for none in particular): each reading the model, none holding anything of its own. |
| 8206 | 41 | `public Auction.Bidders auctionBidders(int months, double face)` | ...for a sale of this face, which only matters to a bank not yet capitalised: it then stands as the residual buyer for the whole of the face (below). |
| 8249 | 3 | `private Auction.Result runAuction(Auction.Kind kind, int months, double face, double lineCoupon, Auction.Cap cap)` | The auction for this paper, now: fair with the issue on the books, the bidders as they stand, the cap given (the default if none). |
| 8265 | 22 | `private Auction.Result runAuction(Auction.Kind kind, int months, double face, double lineCoupon, Auction.Cap cap, int firstCall...` | ...of a bond that may be called (CD4): firstCallAhead is the month, counted from now, the city may first call it - under 0 for paper that cannot be. |
| 8299 | 3 | `private boolean isTinySale(Auction.Result r)` | A sale that is no sale (CD2): the bids fell short of the issue at the city's cap and what they did buy is under the smallest deal worth arranging (minimumIssueSize()), or the fixed fee eats all it raises. |
| 8310 | 3 | `private static DebtQuote tinySaleQuote(String instrument, int duration, double requested, double before, Auction.Result r)` | The quote of a tiny sale: empty - no face, no cash, nothing to book, which every caller already reads as nothing to be had - and carrying the auction's result, so its summary can say what the bids would have bought an... |
| 8315 | 3 | `private static DebtQuote emptyQuote(String instrument, int duration, double before)` | A quote of nothing: the standing rate before and after, no face. |
| 8320 | 3 | `private static double cents(double v)` | Money to the cent, as every proceeds figure has been. |
| 8341 | 33 | `private DebtQuote solveForCash(double cash, double rounding, java.util.function.DoubleFunction<DebtQuote> at)` | The smallest face whose proceeds cover `cash`: a first face at par and the fee, then as many granules more as the shortfall needs at what a unit of that sale banked, until it covers - hard bounded (64 turns), because ... |
| 8376 | 3 | `private static boolean covers(DebtQuote q, double cash)` | A quote that raises the cash and sold what it was asked to: the test the solver's descent keeps. |
| 8387 | 3 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a bill, sold at auction for this much cash, would cost. |
| 8392 | 9 | `public DebtQuote quoteTBill(double amount, int duration, double rounding, Auction.Cap cap)` | ...with the highest yield the city will take, a cap of the caller's. |
| 8403 | 8 | `private DebtQuote billQuote(double requested, int months, double face, double before, Auction.Cap cap)` | The bill's quote at a face: the auction run on it, what it sold and raised. |
| 8419 | 3 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a bill on exactly the terms quoted. |
| 8424 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 8437 | 8 | `private String issueNote(DebtQuote quote)` | The booking every bill shares - handleTBillLogic()'s, and the rollover's (issueForRollover()). |
| 8452 | 7 | `private void bookProceeds(DebtQuote quote)` | The treasury is paid what a sale at home raised, and the market owes it until the settle: the counters the settle and the audit read. |
| 8461 | 5 | `private void bookNewPiece(Debt paper, DebtQuote quote)` | A new piece, sold: told what it raised and at what yield, given its allotment, and the treasury paid. |
| 8474 | 3 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What an amortizing bond, sold at auction for this face, would cost. |
| 8479 | 10 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 8491 | 12 | `private DebtQuote mediumQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The amortizing bond's quote at a face: the auction run on it, the coupon its stop sets, the cost of the credit over the schedule. |
| 8509 | 3 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books an amortizing bond on exactly the terms quoted. |
| 8514 | 6 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 8522 | 8 | `private String issueMediumBond(DebtQuote quote)` | The booking an amortizing bond shares: a new line at the quote's coupon. |
| 8541 | 3 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a bond, sold at auction for this face, would cost. |
| 8546 | 3 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 8561 | 13 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...sold callable if asked (CD4, spec-city-debt.md section 2.6): a bond of ten years or more with the city's right to call it whole at 100 from its first call date (CallTree.firstCallYears()). |
| 8584 | 7 | `public double callSpread(DebtQuote quote)` | What the call adds to a callable sale's yield (CD4, spec-city-debt.md section 2.6): the price of the option on the tree, which runAuction() puts on the fair yield of the same bullet without it - read off the quote's o... |
| 8593 | 3 | `private DebtQuote bondQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The bond's quote at a face: the benchmark line it reopens, or the new line it opens, and what the auction made of it. |
| 8598 | 22 | `private DebtQuote bondQuote(double requested, double face, int years, double before, Auction.Cap cap, boolean callable)` | ...of paper that may be called, if it is (CD4): priced from the months the line, or the new line, has to its first call. |
| 8622 | 3 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a bond on exactly the terms quoted. |
| 8627 | 3 | `public String handleLongBondLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 8632 | 8 | `public String handleLongBondLogic(double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...sold callable if asked (quoteLongBond()): refused with CallTree.REFUSAL for a term that may not be. |
| 8647 | 21 | `private String issueLongBond(DebtQuote quote)` | The booking both bond issues share. |

### A BOND SIZED TO THE CASH IT BRINGS (0.7.10, CD2) (lines 8669-8723)

| line | len | member | says |
|---:|---:|---|---|
| 8695 | 3 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a bond whose CASH covers cashNeeded would cost. |
| 8700 | 9 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 8711 | 3 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a bond sized to the cash, on exactly the terms quoted - the build screen's bond. |
| 8716 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 8724-9073)

| line | len | member | says |
|---:|---:|---|---|
| 8769 | 14 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 8807 | 11 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 8832 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation - expected, since 0.7.42 (below) - less the world's base rate less the world's realised inflation - the one defi... |
| 8845 | 5 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the inflation savers expect (Expectations, since 0.7.42 - the same expected inflation the real rate differential reads; the year's, as the parity ... |
| 8852 | 3 | `public double realPolicyRate()` | The policy rate less the inflation people expect (0.7.45): the dial in real terms, ex ante as every real rate here is since 0.7.42. |
| 8864 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 8869 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 8872 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 8887 | 43 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bill or bond of this size would cost. |
| 8932 | 3 | `private static double dollarCoupon(YieldCurve world, int months)` | A dollar bond's coupon on a world curve: its par yield at the term, rounded down to Auction.COUPON_STEP. |
| 8951 | 12 | `private YieldCurve selfPricedWorldCurve(double face, int months, java.util.function.Function<YieldCurve, Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT ON THE WORLD'S CURVE (CD1; the walk of 0.7.2 on a single rate before it): the world's price of paper that is priced payment by payment. |
| 8973 | 78 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 9058 | 3 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 9063 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's (CD2). |

### THE AMORTIZING BOND AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 9074-9133)

| line | len | member | says |
|---:|---:|---|---|
| 9098 | 3 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | An amortizing bond whose CASH covers cashNeeded: the quote at the face that brings it. |
| 9103 | 9 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 9118 | 4 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note or bond whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 9124 | 9 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 9134-10827)

| line | len | member | says |
|---:|---:|---|---|
| 9222 | 1 | `public java.util.List<String> getRolloverShortNotes()` | The rollover's sales that came up short this month, one sentence each; empty when none did. |
| 9228 | 1 | `public java.util.List<String> getRolloverTailNotes()` | The rollover's sales that cleared past the default cap this month, one sentence each; empty when none did. |
| 9238 | 1 | `public java.util.List<TimeSkipReport.AutoSale> getAutoSalesThisMonth()` | The month's automatic sales, in the order they were made. |
| 9241 | 3 | `void noteAutoSale(TimeSkipReport.AutoSale sale)` | Automatic building enters its bond sales here as it makes them. |
| 9246 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 9249 | 1 | `public Auction.Cap getRolloverCap()` | The cap its auctions take (CD2, D28), as the rollover card's chips read it. |
| 9252 | 7 | `public void setRolloverCap(Auction.Cap cap)` | ...and as they set it, applied at the next press; the change is a decision. |
| 9261 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 9264 | 11 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 9282 | 8 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 9292 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not, and whether it is dollar paper rolled at home. |
| 9294 | 21 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 9317 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 9331 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 9402 | 62 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 9473 | 11 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; an amortizing bond (the serial until CD2) by quoteMediumBond... |
| 9486 | 1 | **type** `private record RolloverSale(Rollover.Issue issue, DebtQuote quote, double brought)` | One of the rollover's issues sold at auction, with the cash it brought. |
| 9489 | 7 | `private static String saleName(Rollover.Issue issue)` | An issue in the words of a sale: "10-year bond", "6-month bill", "10-year amortizing bond". |
| 9514 | 37 | `private void noteRolloverSales(java.util.List<RolloverSale> sales, Rollover.Plan plan)` | WHAT THE ROLLOVER'S SALES DID, said (CD2, D29). |
| 9553 | 4 | `private double localFace(boolean abroad, DebtQuote quote)` | A rollover quote's face in local money: a dollar quote's at the day's rate. |
| 9564 | 18 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 9585 | 3 | `private void printPopulationInfo()` | printers |
| 9590 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 9603 | 3 | `private void printUtilityInfo()` |  |
| 9607 | 3 | `private void printCityStats()` |  |
| 9614 | 900 | `private void nextMonth()` |  |
| 10515 | 284 | `private void startOfMonthUpdate()` |  |
| 10799 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 10828-10934)

| line | len | member | says |
|---:|---:|---|---|
| 10883 | 4 | `private double issuanceFee()` | The fee in today's money: founding / unit, at the expected price level since 0.7.42 (THE ANCHOR, beside restrikeMoneyConstants()). |
| 10907 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 10925 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |
| 10931 | 3 | `public double minimumIssueSizeUsd()` | ...in US dollars at today's rate (0.7.40): Finances' "the minimum" on the ask abroad, which is asked in dollars. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 10935-11233)

| line | len | member | says |
|---:|---:|---|---|
| 10949 | 16 | `public String getCreditRating()` |  |
| 10967 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 10990 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 11004 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 11022 | 5 | `void strikeTheCurve()` | THE CURVE'S EXPECTATIONS (CD1, spec-city-debt section 3.1): the inflation the rule reads - the year's, or the target before there is one - and the inflation the city expects to settle at, handed to the debt market, wh... |
| 11042 | 39 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 11082 | 94 | `private void finalUpdateEconomy()` |  |
| 11178 | 4 | `private void updateConstructionCost()` |  |
| 11189 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 11200 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 11204 | 4 | `public long getHouseholdCapacity()` |  |
| 11208 | 4 | `public long getStoreCapacity()` |  |
| 11212 | 3 | `public long[] getJobs()` |  |
| 11217 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 11234-11845)

| line | len | member | says |
|---:|---:|---|---|
| 11263 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 11299 | 1 | `public PopulationCohorts getCohorts()` |  |
| 11300 | 1 | `public FamilyModel getFamilies()` |  |
| 11301 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 11302 | 1 | `public Migration getMigration()` |  |
| 11303 | 1 | `public Health getHealth()` |  |
| 11304 | 1 | `public Healthcare getHealthcare()` |  |
| 11328 | 517 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 11846-11858)

| line | len | member | says |
|---:|---:|---|---|
| 11854 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 11857 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 11859-12147)

| line | len | member | says |
|---:|---:|---|---|
| 11875 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 11894 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 11914 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 11956 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 11982 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 11992 | 3 | `public BuildLog getBuildLog()` |  |
| 11999 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 12024 | 1 | `public Unemployment getUnemployment()` |  |
| 12028 | 1 | `public Sickness getSickness()` |  |
| 12036 | 1 | `public Crime getCrime()` |  |
| 12040 | 1 | `public double getLastOrphanDeaths()` |  |
| 12041 | 1 | `public double getLastUnhousedDeaths()` |  |
| 12043 | 6 | `{ ... }` |  |
| 12051 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 12076 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 12085 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 12106 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 12117 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 12130 | 1 | `public double getStudentLoansLent()` |  |
| 12131 | 1 | `public double getStudentLoansRepaid()` |  |
| 12132 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 12136 | 1 | `public double getStudentLoanInterest()` |  |
| 12139 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 12146 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 12148-12285)

| line | len | member | says |
|---:|---:|---|---|
| 12163 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 12169 | 7 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 12178 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 12190 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 12234 | 1 | `public Equity getEquity()` |  |
| 12238 | 1 | `public Exchange getExchange()` |  |
| 12246 | 1 | `public BondMarket getBondMarket()` |  |
| 12256 | 1 | `public TreasuryFund getFund()` |  |
| 12259 | 1 | `BondMarket.Readings bondReadings()` | The readings the bond market is attached to, for a harness (CurveCheck, CD1). |
| 12275 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### THE ANCHOR (0.7.42): what the city expects prices to do, and the (lines 12286-12312)

| line | len | member | says |
|---:|---:|---|---|
| 12305 | 1 | `public Expectations getExpectations()` | Expected inflation, credibility and the expected price level. |
| 12308 | 1 | `public double getExpectedInflation()` | What the city expects inflation to be, a fraction a year. |
| 12311 | 1 | `public double getCredibility()` | How far the city believes the central bank, Expectations.KMIN to KMAX. |

### what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) (lines 12313-12442)

| line | len | member | says |
|---:|---:|---|---|
| 12316 | 26 | `double[] indexPrices()` | Each component's price this month, in PriceIndex's order: the shelf, the average rent paid, a meal, a luxury piece; the services' price is struck by the index from the fees. |
| 12344 | 8 | `double[] indexSpends()` | ...what the households spent on each: groceries and rent off the sectors' statements, meals and luxury off their own purchases; the services' is the index's sum of the fee lines. |
| 12359 | 14 | `double[] indexFees()` | ...each fee line's price: the clinic's fee for a general visit at the player's scale, a seat's tuition (the adult kinds' fees averaged - a price, so a shift in who studies what is not inflation), the fare a ride is ch... |
| 12375 | 8 | `double[] indexFeeSpends()` | ...and what the households paid on each fee line, off their books. |
| 12412 | 23 | `void restrikeMoneyConstants()` | Every money constant that prices something, struck at the expected price level: founding / unit x Expectations.getStruckLevel(), the level the last month ended on. |
| 12437 | 5 | `private void handOnExpectations()` | ...and what the month hands on from the anchor that is not a constant: the investors' expected inflation (0 before the basket is based) and - since 0.7.43 - the drift every sticky seller's price carries (Retail.sticky... |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 12443-12466)

| line | len | member | says |
|---:|---:|---|---|
| 12453 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 12462 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 12465 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 12467-12868)

| line | len | member | says |
|---:|---:|---|---|
| 12479 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 12481 | 1 | `public PriceIndex getPriceIndex()` |  |
| 12483 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 12500 | 6 | `double[] rateHistoryToSave()` | The ring and how full it is, for the save (XR1, found by AutoBuildCheck's reload twin): a FLOW, twelve months of rates the state a month ended in cannot give back. |
| 12508 | 7 | `private void restoreRateHistory(double[] saved)` | ...and back: an array of any other length (an older save) leaves the ring empty, as it always loaded. |
| 12517 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 12524 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 12535 | 5 | `public Sectors.TradeByGood getTradeByGood()` | The month across the city's edge, good by good (0.7.35): the businesses' struck statements split by good, an import with no good behind it by the sector that bought it, the households' cars among the cars bought and, ... |
| 12551 | 3 | `public double getOwnReserves()` | What is left of the vault once everything owed against it is taken off: the dollar paper outstanding and the foreign money parked in the bank, both claims on the one pot (0.7.35; the Trade tab's own subtraction until ... |
| 12556 | 5 | `public double getSharesHeldAbroad()` | The city's shares in foreign hands, at each company's price - its last trade, or its fair value before one (0.7.35; the Trade tab's own sum until then). |
| 12570 | 3 | `public double getHeldAbroadPrivately()` | The businesses' and the households' paper abroad, at today's rate: what is held abroad outside the vault. |
| 12575 | 3 | `public double getHeldAbroad()` | ...and the vault with it: everything the city holds abroad. |
| 12580 | 4 | `public double getHeldHereByTheWorld()` | What the world holds here: the city's shares at their price, the businesses' bonds at face, the foreign money parked in the bank, the city's own paper it bought at the auction (CD2, slice 5; at face) and the city's do... |
| 12592 | 1 | `public Bank getBank()` |  |
| 12595 | 1 | `public int getBankBranchesStanding()` | Commercial Banks standing now, counted from the buildings: a founded city has one before its first month opens it (bank.getBranches() is 0 until the month runs). |
| 12608 | 1 | `public CentralBank getCentralBank()` |  |
| 12622 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 12628 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 12631 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 12638 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 12642 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 12646 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 12713 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 12717 | 3 | `public EconomyManager getEconomyManager()` |  |
| 12720 | 3 | `public PopulationManager getPopulationManager()` |  |
| 12724 | 1 | `public LabourMarket getLabourMarket()` |  |
| 12725 | 1 | `public Education getEducation()` |  |
| 12741 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 12766 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 12808 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 12826 | 40 | `public void recordMonth()` | Files this month in the graph history. |
| 12867 | 1 | `public Inbox getInbox()` |  |
| 12868 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 12869-12918)

| line | len | member | says |
|---:|---:|---|---|
| 12880 | 3 | `public int getMonthsUntilAutosave()` |  |
| 12901 | 17 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |

### THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) (lines 12919-13462)

| line | len | member | says |
|---:|---:|---|---|
| 12959 | 3 | `void useSaveClockForChecks(java.util.function.LongSupplier nanos)` |  |
| 12968 | 9 | `private void reportSaves()` | Takes what the writer has finished onto the calling thread: each result becomes the last save result, and a failed one is said as an autosave has always said it. |
| 12983 | 4 | `public void awaitSaves()` | Waits for the save writer, then reports what it wrote. |
| 12993 | 4 | `public void save(int slot, String slotName)` | Saves to a slot, now, on the calling thread: the live objects, after any background write has finished. |
| 13003 | 3 | `SaveWriter.Job prepareSaveForChecks(int slot, String slotName)` | For SaveSlotCheck (UP1): the save filled as an autosave fills it, the snapshot taken and nothing written, so a check can read what the writer thread would be handed. |
| 13008 | 3 | `DataSave liveSaveForChecks()` | For SaveSlotCheck (UP1): the persistent save the setters fill, to measure what a snapshot shares with it. |
| 13018 | 311 | `private SaveWriter.Job prepareSave(int slot, String slotName, boolean snapshot)` | Fills the save from the city as it stands, asks the map for its bytes and stamps it; everything save() did before it wrote anything. |
| 13338 | 5 | `public String takeSkipFailure()` |  |
| 13352 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 13354 | 1 | `public GameFiles getGameFiles()` |  |
| 13373 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 13401 | 18 | `public void sendBuildingSave()` | EVERY ID TO THE HIGHEST, NOT AS MANY AS THERE ARE (FR3, 2026-10-19). |
| 13422 | 17 | `public void loadBuildings()` |  |
| 13453 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 13463-13603)

| line | len | member | says |
|---:|---:|---|---|
| 13490 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 13491 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 13492 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 13499 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 13521 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 13588 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 13597 | 1 | `public double getCityPaperSettled()` |  |
| 13598 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 13599 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 13601 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 13604-13729)

| line | len | member | says |
|---:|---:|---|---|
| 13661 | 1 | `public double getCouponsToHouseholds()` |  |
| 13662 | 1 | `public double getPrincipalToHouseholds()` |  |
| 13663 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 13664 | 1 | `public double getCouponsToFirms()` |  |
| 13665 | 1 | `public double getPrincipalToFirms()` |  |
| 13666 | 1 | `public double getFirmsBoughtPaper()` |  |
| 13667 | 1 | `public double getCouponsToWorld()` |  |
| 13668 | 1 | `public double getPrincipalToWorld()` |  |
| 13669 | 1 | `public double getWorldBoughtPaper()` |  |
| 13670 | 1 | `public double getPaperBoughtFromWorld()` |  |
| 13681 | 7 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households', the central bank's and (CD2, slice 4) the businesses' and (slice 5) the world's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 13690 | 16 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 13708 | 21 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 13730-13825)

| line | len | member | says |
|---:|---:|---|---|
| 13740 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |
| 13764 | 15 | `private void desksBuysFirmPaper(double face, double cash)` | THE BANK BUYS THE BUSINESSES' PAPER (CD2, slice 4), for the spread gone, a sector that borrows, or one that wants the money: this much face for this much cash, off every piece's businesses' share pro rata, onto the ba... |
| 13793 | 19 | `private double desksBuysWorldPaper(double face)` | THE BANK BUYS THE WORLD'S PAPER (CD2, slice 5), when the hot money leaves and takes it with it (worldPaperLeavesWithTheMoney()): this much face, off every piece's world share pro rata, onto the bank's book with the un... |
| 13821 | 4 | `private void worldPaperLeavesWithTheMoney(double stockBefore)` | ...AND THE PAPER LEAVES WITH THE MONEY (CD2, slice 5, D7). |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 13826-13958)

| line | len | member | says |
|---:|---:|---|---|
| 13846 | 32 | `private double householdsTakeTheirShare()` |  |
| 13895 | 34 | `private double firmsTakeTheirShare(java.util.List<Debt> settling)` | THE BUSINESSES TAKE THEIR SHARE (CD2, slice 4, D33), at the same settle and from the same allotment (Debt.allotFirmsCash(), the auction's fill for them at the clearing yield). |
| 13946 | 12 | `private double worldTakesItsShare(java.util.List<Debt> settling)` | THE WORLD TAKES ITS SHARE (CD2, slice 5, D7; star 2), at the same settle and from the same allotment (Debt.allotWorldCash(), the auction's fill for the world at the clearing yield - CapitalFlows' rule, the stock it wo... |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 13959-14222)

| line | len | member | says |
|---:|---:|---|---|
| 14031 | 67 | `private void openMarketOperation()` |  |
| 14108 | 29 | `private double buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |
| 14150 | 29 | `private double buyPaperFromFirms(double wanted)` | The rest of a purchase the bank and the households could not fill, from the businesses' term paper (CD2, slice 4): the households' path with the businesses' share in its place - pro rata across the pieces they hold th... |
| 14193 | 29 | `private double buyPaperFromWorld(double wanted)` | The rest of a purchase the others could not fill, from the world (CD2, slice 5): the businesses' path with the world's share in its place - pro rata across the pieces it holds that are settled and pay no principal thi... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 14223-14470)

| line | len | member | says |
|---:|---:|---|---|
| 14328 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 14331 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 14338 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 14350 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 14357 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 14363 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 14376 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 14407 | 63 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 14471-14495)

| line | len | member | says |
|---:|---:|---|---|
| 14489 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 14493 | 1 | `public double getBuybackToHouseholds()` |  |
| 14494 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 14496-14688)

| line | len | member | says |
|---:|---:|---|---|
| 14589 | 15 | `private void takeTreasuryMonth()` |  |
| 14606 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 14608 | 1 | `public double getTreasuryOpening()` |  |
| 14609 | 1 | `public double getTreasuryClosing()` |  |
| 14610 | 1 | `public double getTreasuryRaised()` |  |
| 14611 | 1 | `public double getTreasuryRepaid()` |  |
| 14612 | 1 | `public double getTreasurySurplus()` |  |
| 14615 | 1 | `public double getTreasuryNotCash()` | What the balance charged or booked that the cash did not (CD3): the discount amortised, less the gain on paper retired. |
| 14618 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 14628 | 3 | `public double getNetPosition()` | The city's net position (0.7.32, the Finances tab's THE BALANCE): its cash - below nothing when it is overdrawn - less the paper it owes and what its central bank has advanced it. |
| 14638 | 3 | `public double getNetPositionAtCarrying()` | The same net position with the paper at what the books carry it at (CD3, spec-city-debt.md 5.4): its face less the discount not yet worn off, plus any premium. |
| 14660 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 14672 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 14677 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 14685 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |

### FROM EARNED TO THE BUDGET (0.7.31) (lines 14689-14827)

| line | len | member | says |
|---:|---:|---|---|
| 14726 | 28 | `public java.util.List<TreasuryJournal.Entry> getEarnedToBudget()` | The steps from EARNED to the budget's balance, in the Government bridge's order, signed as they move EARNED (+ adds, - takes away); every line, at nothing too. |
| 14756 | 5 | `public double getEarnedResidual()` | What the steps leave between EARNED and the budget: the dials moved since the month was struck, and nothing in a month nobody moved one. |
| 14762 | 7 | `double[] treasuryMonthToSave()` |  |
| 14770 | 10 | `void restoreTreasuryMonth(double[] saved)` |  |
| 14805 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 14808 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 14810 | 1 | `public double getPostAuditDriftWorst()` |  |
| 14811 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 14812 | 4 | `public void InterestExpense(double amount)` |  |
| 14817 | 3 | `public DebtManager getDebtManager()` |  |
| 14822 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 14828-15115)

| line | len | member | says |
|---:|---:|---|---|
| 14896 | 46 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 14954 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 14959 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 14981 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 14993 | 19 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 15021 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 15046 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 15053 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 15057 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 15067 | 8 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 15077 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 15080 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 15087 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 15097 | 10 | `public double getArrearsOwedTo(String sectorKey)` | Owed and unpaid to one sector's till (0.7.55): every line payDownArrears() would pay it, by the payee it would pay. |
| 15109 | 1 | `public double getArrearsPaidTo(String sectorKey)` | What the treasury paid this sector's till of its arrears this month (0.7.55) - its statement's arrears line. |
| 15111 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 15112 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 15113 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 15114 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 15116-15353)

| line | len | member | says |
|---:|---:|---|---|
| 15133 | 6 | `public double fairValueOf(Debt debt)` | What one piece is worth to somebody who does not care to hold it, right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on. |
| 15150 | 3 | `public double quoteRepurchase(Debt debt)` | What clearing the whole of one piece that can be cleared would cost right now (CD4): the reverse auction's price for everything its holders offer, the central bank's face excepted (D18), at any price - the sum of the ... |
| 15162 | 3 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative) from clearing what quoteRepurchase() clears: the carrying value of that face less the price (CD3) - what the books hold it at against what buying it bac... |
| 15167 | 5 | `public double buybackOfferable(Debt debt)` | The face of a piece its holders can offer: the principal less the central bank's, which does not sell (D18); a dollar piece's all of it, the world's alone (D26). |
| 15196 | 21 | `public Buyback.Result quoteBuyback(Debt debt, double face, double maxPer100)` | THE REVERSE AUCTION, QUOTED (CD4, spec-city-debt.md section 6.2): what buying back this much face of one piece, at no more than this per 100, would take and cost, struck once on the market as it stands - nothing is mo... |
| 15219 | 3 | `private static boolean takesTheWholePiece(double taken, double principal)` | True when this much face is the whole of a piece's principal: all of it but the dust that slices of a holding leave. |
| 15248 | 95 | `public Buyback.Result buyBack(Debt debt, double face, double maxPer100)` | Buys one bond back, in whole or in part, and takes what it bought off the books: the reverse auction of quoteBuyback(), struck and paid. |
| 15350 | 3 | `public double repurchaseDebt(Debt debt)` | Buys back everything the holders offer of one bond, at any price: the thin form of buyBack() the buttons before CD4 knew. |

### the call (CD4, spec-city-debt.md section 6.4; D22, D23, D24) (lines 15354-15485)

| line | len | member | says |
|---:|---:|---|---|
| 15367 | 3 | `public double rateVolatility()` | The volatility of the city's own short rate, a year (D23): the standard deviation of the monthly changes of History's "interestRate" series over the last CallTree.VOL_MONTHS, times the square root of twelve, never und... |
| 15378 | 7 | `public boolean canCall(Debt debt)` | True when this piece can be called now: a callable bond of the city's own, on the books, whose first call month has come (LongTermBond .isCallableNow()) and which has nothing unsettled - a line sold last month has buy... |
| 15387 | 3 | `public double callCost(Debt debt)` | What calling this piece costs: its outstanding principal, 100 per 100 (CallTree.CALL_PRICE), paid to every holder, the central bank's too. |
| 15398 | 5 | `public double callSaving(Debt debt)` | The present value a call would save (D24): the line's remaining payments at today's curve, as a bond that cannot be called, less what the call pays and less what selling the paper again costs - the auction's fee (issu... |
| 15410 | 4 | `public boolean worthCalling(Debt debt)` | True when calling this callable line is worth it (D24): the saving (callSaving()) passes CallTree.WORTH_CALLING of the face, 3%. |
| 15437 | 40 | `public double callLine(Debt debt)` | Calls one line whole at 100 (CD4): every holder is paid its face - the bank, the households, the businesses, the world and the central bank, which has no say in it (a call is the paper's own term, not a sale a holder ... |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 15486-17508)

| line | len | member | says |
|---:|---:|---|---|
| 15554 | 20 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 15575 | 330 | `private void rebuildSimulationState()` |  |
| 15947 | 10 | `public void loadGame(int slot)` | Load game |
| 15965 | 22 | `private void readTheMap(int slot, Long stamp)` | THE CITY MAP, READ BACK (0.7.60): from the slot's sidecar when the save's stamp is its stamp and it was drawn on this land in this month (CityMap.readSidecar()); a save that had a map whose sidecar is missing or stale... |
| 16024 | 11 | `private void restoreOilPools(DataSave loaded)` | THE TWO OIL POOLS ON A LOAD (0.7.93, batch O10b; LandManager's THE TWO OIL POOLS): once the land is in place, the offshore pool's E as saved - or, on a save from before SAVE_FORMAT 35, the one pool's E charged to the ... |
| 16036 | 60 | `private boolean restoreLandOnTheWorld(DataSave loaded, double owned)` |  |
| 16098 | 1353 | `private void readTheSave(int slot)` | The load itself; see loadGame(). |
| 17458 | 4 | `void seedFundLedger()` | THE FUND'S COST BASIS FOR A SAVE FROM BEFORE IT (0.7.39; the project's spec-fund-0739.md, 3.5): each market lot and bond at its market value this month, flagged as such, the rescue lot exact from the counters, one TRA... |
| 17467 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 17509-17798)

| line | len | member | says |
|---:|---:|---|---|
| 17520 | 17 | `private void seedMoneyConstants(double unit)` | Seeds every money constant struck in founding dollars at a unit: the load path's list since the reform, and since FX4 also buildWorld()'s at 1, so a new city does not keep the last loaded city's unit. |
| 17538 | 1 | `public Denomination getDenomination()` |  |
| 17541 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 17572 | 203 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 17786 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

