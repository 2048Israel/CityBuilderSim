# Game.java - 16,080 lines · 641 methods · 19 constants · model

`ham/citybuildersim/Game.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (80), [DecisionLog](DecisionLog.md) (68), [Equity](Equity.md) (67), [TreasuryLine](TreasuryLine.md) (58), [DebtQuote](DebtQuote.md) (40), [GameLog](GameLog.md) (39), [ConstructionControl](ConstructionControl.md) (36), [AgeBand](AgeBand.md) (36), [BusinessDebtManager](BusinessDebtManager.md) (35), [Debt](Debt.md) (34), [LandManager](LandManager.md) (30), [Resource](Resource.md) (28), [Rollover](Rollover.md) (27), [GameFiles](GameFiles.md) (26), [TreasuryFund](TreasuryFund.md) (24), [PriceIndex](PriceIndex.md) (23), [TaxPolicy](TaxPolicy.md) (22), [CareType](CareType.md) (22), [TreasuryJournal](TreasuryJournal.md) (21), [JobType](JobType.md) (20), [Oil](Oil.md) (20), [Sector](Sector.md) (20), [Bank](Bank.md) (20), [DebtManager](DebtManager.md) (19), [Founding](Founding.md) (19), [CityMap](CityMap.md) (19), [Good](Good.md) (19), [BusinessInvestment](BusinessInvestment.md) (18), [Markets](Markets.md) (18), [LandParcel](LandParcel.md) (17)... and 87 more

**Used by (148):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityBasket](CityBasket.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConversionCheck](ConversionCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [Pieces](Pieces.md), [PlanCheck](PlanCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SpreadPlanner](SpreadPlanner.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 898 | THE FOUNDING RESERVE (2026-09-21) |
| 954 | · THE FOUNDING RECORD (0.7.10) |
| 991 | THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) |
| 1100 | · THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) |
| 1267 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 1290 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1513 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1651 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 1809 | · THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) |
| 1950 | PRIVATE INVESTMENT |
| 2318 | · AND THE BALANCE SHEET |
| 2526 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 2612 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 2646 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 3181 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 3328 | · the preferred offer |
| 3450 | · what the fund is worth |
| 3510 | · the dial |
| 3633 | · the hand |
| 3750 | · an Insane founding |
| 3858 | THE STRATEGIC RESERVE (0.7.85, batch O8; runs/spec-oil.md 2.8) |
| 3955 | SHRINKING |
| 4227 | THE CONSTRUCTION WARNING |
| 4277 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 4574 | THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) |
| 4776 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 4991 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 5072 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 5817 | THE BUILDERS' PRICE (0.7.19) |
| 5993 | · BITUMEN FOR THE PAVING (0.7.83, batch O6; runs/spec-oil.md 2.5) |
| 6187 | MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) |
| 6281 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 6608 | · F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) |
| 6782 | · in the month |
| 7018 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 7203 | · THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) |
| 7416 | · A RUN OF ORDERS (0.7.40) |
| 7961 | · A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) |
| 8045 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 8380 | · THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 8455 | ROLLING WHAT FALLS DUE (0.7.13) |
| 8884 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 9144 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 9360 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 9553 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 9944 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 10082 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 10352 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 10964 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 10977 | THE READINGS THE MONTH TAKES OF THE CITY |
| 11266 | · the price of a place (2026-09-21) |
| 11401 | THE ANCHOR (0.7.42): what the city expects prices to do, and the |
| 11440 | · what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) |
| 11571 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 11595 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 11966 | the save system |
| 12428 | PAYING THE WORLD BACK |
| 12569 | THE HOLDERS ARE PAID (0.7.1) |
| 12645 | · the desk, for the households |
| 12671 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 12728 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 12875 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 13111 | · a buyback's holders outside the pools |
| 13133 | WHAT THE TREASURY ACTUALLY DID |
| 13290 | · FROM EARNED TO THE BUDGET (0.7.31) |
| 13420 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 13708 | BUYING YOUR OWN DEBT BACK |
| 13835 | WHY LAND IS NOT IN THE RENT FLOOR |
| 14216 | · · the three the monthly path sets and this did not |
| 15826 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1845 | `Game.LandNeed.Kind.ROOM` |  |
| 1845 | `Game.LandNeed.Kind.SHORTFALL` |  |
| 1845 | `Game.LandNeed.Kind.DEPOSIT` |  |
| 1845 | `Game.LandNeed.Kind.COAST` |  |
| 7016 | `Game.BuildResult.SUCCESS` |  |
| 7016 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 7016 | `Game.BuildResult.NO_LAND` |  |
| 7016 | `Game.BuildResult.NO_DEPOSIT` |  |
| 7016 | `Game.BuildResult.NO_LICENCE` |  |
| 7016 | `Game.BuildResult.FAILED` |  |
| 7016 | `Game.BuildResult.NO_COAST` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 638 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 946 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 949 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 952 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 2065 | `Game.STATIONS_SLOT` | `"Filling stations"` | Where the forecourts' word is filed among the month's investment lines (0.7.83): apart from the shops', as the bank's is. |
| 4638 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 4641 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 6011 | `Game.BITUMEN_BINDER_SHARE` | `.05` | The share of an asphalt surface's weight that is bitumen: five per cent [R18]. |
| 7030 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 8103 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 8276 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 8398 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 9960 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 9972 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 9975 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 9990 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 9999 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, WHAT AN ISSUE COSTS). |
| 10007 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 11973 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 100 | `private Ports ports` | The city's ports (0.7.86, batch O9; Ports): the berths' shares of each kind of cargo in force, and the month's tonnes by sea, struck with the railway's month (chargeFreight()). |
| 119 | `java.util.function.Consumer<Boolean> autoBuildProbeForTest` | A harness's look at the city either side of automatic building's pass (AutoBuildCheck): false before, true after. |
| 127 | `private final GameFiles gameFiles` | Where saves live and how they are written. |
| 128 | `private HistoryGrapher historyGrapher` |  |
| 129 | `private DebtManager debtManager` |  |
| 131 | `private SimulationEngine simulationEngine` |  |
| 133 | `int materialsConsumed` |  |
| 137 | `double receiptMaterials` | The last order's material, in units - drawn by the crews as they build. |
| 138 | `double totalBuildingCost` |  |
| 139 | `private boolean hasNewReceipt` |  |
| 140 | `String lastBuildingName` |  |
| 149 | `int lastBuildQuantity` | How MANY of them, so the receipt can say "3 x Walk-in Clinic". |
| 163 | `private int receiptSerial` | WHICH receipt this is, counted up forever. |
| 189 | `boolean reports` | Console output per month: seven sector income statements and three 12x60 ASCII graphs. |
| 190 | `boolean graphs` |  |
| 193 | `boolean initialized` | boolean |
| 868 | `private String loadFailure` | Why the last load did not happen, or null. |
| 969 | `private Founding founding` |  |
| 1011 | `private CityMap cityMap` | The city map, or null until it is asked for. |
| 1014 | `private int mapFailures` | How many months the map failed to keep up and was dropped (each one logged): what a harness asserts is none. |
| 1028 | `private boolean mapAtSeaStale` | Whether the map's oil at sea is to be handed over again before it is next read (0.7.97): set by a draw and a load. |
| 1121 | `private int mapGeneration` | Bumped whenever the city's map is thrown away (a new city, a load): a draft from before it is another city's. |
| 1125 | `private final Game game` |  |
| 1126 | `private final int generation` |  |
| 1127 | `private final CityLand land` |  |
| 1128 | `private final java.util.Map<Resource, double[]> remaining` |  |
| 1129 | `private final BuildingVisual.Type[] types` |  |
| 1130 | `private final long[] counts` |  |
| 1131 | `private volatile CityMap map` |  |
| 1254 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 1325 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 1326 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1551 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1796 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under ON OFFER (under the toggle until 0.7.26), and a short vault says here that the rest was converted. |
| 1797 | `private int lastLandReceiptMonth` |  |
| 1895 | `private BusinessInvestment.OrderWatch orderWatch` | Harnesses only (0.7.54): what every order search decides is told here as well - see BusinessInvestment.OrderWatch. |
| 1940 | `private boolean monthRun` | WHETHER A MONTH HAS RUN SINCE THE CITY WAS FOUNDED OR LOADED (0.7.102). |
| 2544 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 2553 | `private double carriedTransitBill` | ...and the month's transit bill as 6d struck it (0.7.49), applied inside the rebuild for the same reason: the rebuild would strike it at the fill the month ended on, not the one it was paid at. |
| 2564 | `private double carriedCaptiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, because the cells are not back when the road ratio is first struck (carriedCarOwnership's reason), and a j... |
| 2567 | `private double[] carriedFuelMonth` | ...and the drivers' fuel as 6d drew it (0.7.62): bill, imported part, litres - DataSave.householdFuel; null for a save from before it. |
| 2570 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 2589 | `private final double[] rowFeesSettled` | Each row's fees as the last month's waterfall was handed them: the clinic's, the schools', the fares and the fuel (0.7.49). |
| 2617 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 2894 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 3220 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 4121 | `private int wellsWornOut` | Wells retired worn out over the run (0.7.84): a count for the playtest, not saved. |
| 4148 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 4235 | `private int constructionShedMonth` |  |
| 4236 | `private double constructionShedPoints` |  |
| 4295 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 4308 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 4328 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 4343 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 4696 | `final BusinessInvestment.Decision decision` |  |
| 4697 | `final double cash, perUnitProfit` |  |
| 4698 | `final BusinessDebtManager credit` |  |
| 4699 | `final boolean banned` |  |
| 4700 | `boolean atPrime` |  |
| 4701 | `double firstRate` |  |
| 4702 | `int deskCalls` |  |
| 4950 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 4951 | `private final java.util.Set<String> heldForDownPayment` |  |
| 5032 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 5033 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 5034 | `private double salvageUsedThisMonth` |  |
| 5092 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 5230 | `private double bankedClearedAtLoad` | Construction points the last load cleared from stacks that carried them past what they owed (0.7.17; see the load path and BuildingManager.clearBankedProgress()). |
| 5541 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 5546 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 5720 | `public final int quantity` |  |
| 5721 | `public final double sticker` |  |
| 5722 | `public final double materialsNeeded` |  |
| 5724 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 5726 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 5727 | `public final double plantPrice` |  |
| 5728 | `public final double plantCost` |  |
| 5729 | `public final double materialsImported` |  |
| 5730 | `public final double materialsPrice` |  |
| 5731 | `public final double importCost` |  |
| 5737 | `public final double salesTax` | The sales tax in the price (0.7.19): what the builders remit on it, less what they claim back on the plant's material - passed on, as a shop's shelf price passes it on. |
| 5744 | `public final double allowance` | What the price allows for the material beyond the yard (0.7.19): the plant's and the world's at today's prices, net of the credit on the plant's and with the builders' tax on it - the part of the total an escalation c... |
| 5745 | `public final double total` |  |
| 5746 | `public final double landNeeded` |  |
| 5747 | `public final double landFree` |  |
| 5753 | `public final double months` | Months to finish at this month's shares of the site output, the planners' own reading (quoteMonths()), or NaN when the builders have no site output. |
| 5765 | `public final double bitumenNeeded, bitumenFromRefiners, bitumenImported, bitumenLocalCost, bitumenImportCos...` | The bitumen the order's paving takes (0.7.83, batch O6; spec-oil 2.5), in tonnes - none for anything but a paved road, a highway and a paving (Game.bitumenFor()) - what of it the refiners' tanks would sell and what th... |
| 5880 | `private boolean contractsToInfer` | Set by the load path when a save carries no payers, and read once the land price is back. |
| 5895 | `private final BuildingManager.BuildersWages buildersWages` | What the builders' crews cost a point, today and at founding. |
| 6037 | `private double bitumenTonnes, bitumenCost, bitumenImported` | The bitumen drawn for roads since this game was founded or loaded, in tonnes, what it cost the builders and the part bought abroad (0.7.83): running totals for the checks and the playtest, never reset and not saved - ... |
| 6185 | `private double siteDrawBillable` | What the month's site draw cost the builders, as they bill it on (billableMaterial()): what the owners' escalation is struck against. |
| 6441 | `private String lastHandRefusal` | Why a demolition or a buy-out was not made, or null when it was. |
| 6785 | `private ConstructionControl.Events controlThisMonth` | This month's events, kept for the screens and the inbox: what the advance left, settled. |
| 6787 | `private double overtimePaidThisMonth` | What the treasury paid the builders this month for overtime, with their tax in it. |
| 6789 | `private double demolitionSalvageThisMonth` | What a finished demolition's material fetched this month, from the builders. |
| 7225 | `private double freshRights` | Units a month of fresh water the city may treat past what its lakes and river yield: an older city's (0.7.59). |
| 7341 | `private final Investor government` | The city itself, as a payer. |
| 8530 | `private final Rollover rollover` |  |
| 10341 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 10342 | `private LandManager landManager` |  |
| 10346 | `private boolean loadingSave` | True while loadGameSave() runs initialize(), which then founds no land: the save brings its own (0.7.57). |
| 10349 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 10350 | `private BuildLog buildLog` |  |
| 10377 | `private PopulationCohorts cohorts` |  |
| 10378 | `private FamilyModel families` |  |
| 10386 | `private Migration migration` |  |
| 10392 | `private LabourMarket labourMarket` | What labour costs. |
| 10398 | `private Education education` | The schools. |
| 10407 | `private Health health` | How much of the workforce is off sick. |
| 10415 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 10969 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 11115 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 11122 | `private HouseholdAccounts households` | The residents' own books. |
| 11131 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 11141 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 11145 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 11153 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 11157 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 11247 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 11253 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 11328 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 11338 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 11344 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 11351 | `private final Equity equity` | Who owns the city's companies. |
| 11355 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 11363 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 11373 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 11377 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 11399 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 11417 | `private final Expectations expectations` |  |
| 11577 | `private final Consumption consumption` |  |
| 11578 | `private boolean consumptionLoaded` |  |
| 11587 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 11605 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 11614 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 11615 | `private int rateHistoryFilled` |  |
| 11691 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 11704 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 11748 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 11761 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 11775 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 11793 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 11796 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 11798 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 11799 | `private double carriedRetailCapacity` |  |
| 11800 | `private double carriedRetailWant` |  |
| 11803 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 11804 | `private java.util.Map<String, String> lastInvestment` |  |
| 11807 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 11851 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 11975 | `private int monthsSinceAutosave` |  |
| 12317 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 12326 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 12451 | `private double foreignDebtRaisedThisMonth` |  |
| 12452 | `private double foreignPrincipalRepaidThisMonth` |  |
| 12453 | `private double foreignInterestPaidThisMonth` |  |
| 12505 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 12525 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 12526 | `private double cityPrincipalRepaidThisMonth` |  |
| 12540 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 12541 | `private double cityDiscountForBank` |  |
| 12544 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 12561 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 12567 | `private double bankPrincipalRepaidThisMonth` |  |
| 12595 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 12686 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 12974 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 12977 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 13121 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 13123 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 13176 | `private double treasuryOpening` |  |
| 13177 | `private double treasuryClosing` |  |
| 13178 | `private double treasuryRaised` |  |
| 13179 | `private double treasuryRepaid` |  |
| 13180 | `private double treasurySurplus` |  |
| 13181 | `private boolean treasuryRecorded` |  |
| 13200 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 13203 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 13381 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 13384 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 13392 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 13393 | `private String postAuditDriftPool` |  |
| 13394 | `private double postAuditDriftWorst` |  |
| 13458 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 13461 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 13464 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 13472 | `private final java.util.Map<String, Double> arrearsPaidTo` | This month's arrears paid down, by the sector whose till they reached (0.7.55): what its statement's arrears line reads (SectorBooks, arrearsPaid), struck at the settle and read when the month is recorded, both inside... |
| 13833 | `private long pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 15781 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 15830 | `private final Denomination denomination` |  |
| 16073 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 16056 | **type** `public class Game` |  |
| 69 | 1 | `public DecisionLog getDecisions()` | The player's decisions, oldest first (0.7.23): what the chart's flags are drawn from. |
| 80 | 1 | `public AutoBuilder getAutoBuilder()` | Automatic building (0.7.73): its settings, its log and its last pass. |
| 92 | 1 | `public StrategicReserve getReserve()` | The city's strategic reserve (0.7.85). |
| 103 | 1 | `public Ports getPorts()` | The city's ports (0.7.86). |
| 113 | 4 | `public BoatSchedule boats(java.util.List<BoatSchedule.Berth> quays)` | The month's ships (0.7.86, batch O9; BoatSchedule): the calls the ports' month billed makes, on these quays, with the lanes run away from the founding site. |
| 195 | 3 | `public Game()` |  |
| 207 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 216 | 5 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 223 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 255 | 336 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 592 | 4 | `public void run()` |  |
| 597 | 40 | `private void initialize()` |  |
| 640 | 4 | `static { ... }` |  |
| 648 | 3 | `public void newGame()` | A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. |
| 657 | 40 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 727 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 757 | 8 | `public void resumeGame()` |  |
| 765 | 61 | `public void loadGameSave(int slot)` |  |
| 835 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 843 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 858 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 860 | 6 | `public void toggleQuit()` |  |
| 870 | 1 | `public String getLoadFailure()` |  |
| 872 | 7 | `public void toggleGraphs()` |  |
| 879 | 5 | `public void toggleReports()` |  |
| 885 | 3 | `public void toggleNextMonth()` |  |
| 890 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 892 | 3 | `public int getMonth()` |  |
| 895 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 898-953)

### THE FOUNDING RECORD (0.7.10) (lines 954-990)

| line | len | member | says |
|---:|---:|---|---|
| 972 | 4 | `public Founding getFounding()` | How this city was founded. |
| 978 | 1 | `public long getWorldSeed()` | The seed of the world this city stands on (0.7.56): chosen at founding, or derived from an older save. |
| 986 | 1 | `public World getWorld()` | The world this city stands on (0.7.56): its terrain, its founding site and river, its fields of ore and oil. |
| 989 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and every purchase since, whole blocks of a grid since 0.7.67 (CityLand). |

### THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) (lines 991-1099)

| line | len | member | says |
|---:|---:|---|---|
| 1017 | 9 | `public CityMap getCityMap()` | The city map: drawn canonically the first time it is asked for, then kept up month by month. |
| 1041 | 49 | `CityMap.AtSea mapAtSea()` | The oil at sea as the map draws it (0.7.97, batch O13; CityMap's THE OIL AT SEA, runs/spec-oil.md 2.12): each platform's jacket in the middle of the sea sites its slots hold on its field - the platforms on a field tak... |
| 1092 | 4 | `private static Deposit seaFieldOf(List<LandManager.SeaField> fields, int cell, int index)` | A sea field in the list by its world cell and index, or null. |
| 1098 | 1 | `public boolean hasCityMap()` | Whether the city map has been drawn: a city never asked for it has none, and its months pay nothing for it. |

### THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) (lines 1100-1266)

| line | len | member | says |
|---:|---:|---|---|
| 1124 | 32 | **type** `public static final class MapDraft` | A map to be drawn away from the screen's thread: the city's land, remains and counts as they stood, copied. |
| 1133 | 8 | `private MapDraft(Game game)` _(in Game.MapDraft)_ |  |
| 1143 | 3 | `public void draw()` _(in Game.MapDraft)_ | Draws the map on the copy: any thread, once. |
| 1148 | 1 | `public boolean drawn()` _(in Game.MapDraft)_ | Whether it has been drawn. |
| 1151 | 1 | `CityMap map()` _(in Game.MapDraft)_ | The map drawn, or null: a harness's (MapCheck 7). |
| 1154 | 1 | `public Game game()` _(in Game.MapDraft)_ | The game it was taken from. |
| 1158 | 3 | `public MapDraft mapDraft()` | A draft of the city's map, for drawing away from the screen's thread; null when the map is drawn already. |
| 1169 | 21 | `public boolean adoptMap(MapDraft draft)` | Keeps a drawn draft as the city's map, bound to its own land and kept up to the month (reconcile()), and says whether it is the city's map now. |
| 1192 | 1 | `public int getMapFailures()` | How many months the map failed to keep up and was dropped. |
| 1195 | 1 | `public BuildingVisual.Type[] getMapTypes()` | The building types as the map draws them, by id. |
| 1198 | 7 | `public long[] getMapCounts()` | Every type's standing count, by id: what the map's districts sum to. |
| 1207 | 4 | `private CityMap drawMap()` | The map drawn canonically from the city as it stands. |
| 1219 | 12 | `private void reconcileMap()` | The month's change placed on the map (CityMap.reconcile()), after the month's construction and demolitions; the land drawn again (a restatement) draws the map again. |
| 1233 | 1 | `public String getCityName()` | The city's name. |
| 1236 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 1239 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 1242 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 1245 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 1263 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset until 0.7.20, when the screen stopped saying what the money buys; NewGameChec... |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 1267-1289)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 1290-1512)

| line | len | member | says |
|---:|---:|---|---|
| 1328 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 1329 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 1331 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 1332 | 9 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 1343 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 1344 | 1 | `public double getSubsidyPaid(String key)` |  |
| 1346 | 5 | `public double getTotalSubsidyPaid()` |  |
| 1353 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 1366 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 1369 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 1388 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 1398 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 1433 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 1450 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 1466 | 5 | `public double getIncome()` | EARNED (0.7.31's name for it): the tax take less the running programmes, plus the utilities' net, at today's dials - the header's "+$X earned a month". |
| 1472 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1476 | 3 | `public double getWaterRatio()` |  |
| 1480 | 3 | `public double getRoadRatio()` |  |
| 1485 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1490 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1495 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1499 | 3 | `public LandManager getLandManager()` |  |
| 1504 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1513-1650)

| line | len | member | says |
|---:|---:|---|---|
| 1554 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1557 | 6 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1572 | 50 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1624 | 3 | `private static String usdWords(double thousands)` | Thousands of US dollars as the screens write them: "US$101.8M" (Formats.amount() with the dollar's mark). |
| 1629 | 3 | `private static String localWords(String here, double thousands)` | ...and thousands of local money, with its own mark: "D$72.0M". |
| 1640 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1647 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's price by (red when not, since 0.7.26 the only verdict on it); since 0.7.13 its button asks land... |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 1651-1808)

| line | len | member | says |
|---:|---:|---|---|
| 1684 | 5 | `public java.util.List<LandParcel> landShelf()` | The offers standing in the land office's order: cheapest ground first, per square foot of dry ground in US dollars (0.7.57: all forty) - the top-left card first. |
| 1691 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 1701 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 1711 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 1726 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 1731 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 1736 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 1748 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 1754 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 1759 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 1769 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 1800 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1805 | 3 | `public java.util.List<LandParcel> getLandListing()` | The offers standing. |

### THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) (lines 1809-1949)

| line | len | member | says |
|---:|---:|---|---|
| 1842 | 17 | **type** `public record LandNeed(Kind kind, double drySqFt, Resource resource)` | What the city needs ground for: room in general, a shortfall of dry ground, a resource's deposit, or a coast. |
| 1845 | 1 | **type** `public enum Kind` _(in Game.LandNeed)_ | The four needs. |
| 1848 | 1 | `public static LandNeed room()` _(in Game.LandNeed)_ | Room to grow. |
| 1851 | 1 | `public static LandNeed shortfall(double drySqFt)` _(in Game.LandNeed)_ | This much more dry ground than the city has free. |
| 1854 | 1 | `public static LandNeed deposit(Resource resource)` _(in Game.LandNeed)_ | A site of this resource to stand a mine or a well on. |
| 1857 | 1 | `public static LandNeed coast()` _(in Game.LandNeed)_ | Sea, for a desalination plant (batch J2). |
| 1861 | 28 | `public LandParcel bestOffer(LandNeed need)` | The offer that best meets a need, by THE BEST OFFER's rules; null when none does. |
| 1890 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1898 | 4 | `public void watchOrders(BusinessInvestment.OrderWatch watch)` | Harnesses only: tell this watch every order the three searches decide; null to stop. |
| 1903 | 3 | `public String getLastInvestment(String sector)` |  |
| 1919 | 11 | `private void deferBorrowingCosts()` | WHAT EACH BUSINESS'S BORROWING COST IT UP FRONT THIS MONTH (0.7.102, Jerus's A16): the fees the bank kept out of its loans, its mortgages' insurance premiums and its bonds' issuing costs - the month's own figures, the... |
| 1943 | 1 | `public boolean hasMonthRun()` | True once a month has run since the city was founded or loaded; false until then (0.7.102). |
| 1946 | 3 | `public java.util.Map<String, String> getLastInvestments()` | Every word the investors left last month, by the slot it was filed under (a sector's key, or the bank branch's label) - saved since 0.7.102 (A22); read-only. |

### PRIVATE INVESTMENT (lines 1950-2525)

| line | len | member | says |
|---:|---:|---|---|
| 1960 | 103 | `private void runPrivateInvestment()` |  |
| 2082 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 2095 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 2107 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 2113 | 411 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 2526-2611)

| line | len | member | says |
|---:|---:|---|---|
| 2573 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 2576 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 2579 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 2580 | 1 | `public double getHouseholdCarImports()` |  |
| 2583 | 1 | `public double getHouseholdFuel()` | ...and what they paid for fuel this month (0.7.49): what the cells paid - the refiners' shelf and the world's (0.7.62). |
| 2586 | 1 | `public double getHouseholdFuelImports()` | ...and what of it they paid the world (0.7.62): every litre with no refinery; the money audit's FuelFunded and FuelImports. |
| 2592 | 1 | `public double getRowFeesSettled(int row)` | ...one row's. |
| 2595 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 2598 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 2601 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 2604 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 2607 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 2610 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 2612-2645)

| line | len | member | says |
|---:|---:|---|---|
| 2620 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 2623 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 2626 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 2629 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 2632 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 2635 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 2638 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 2641 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 2644 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 2646-3180)

| line | len | member | says |
|---:|---:|---|---|
| 2672 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 2691 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 2702 | 3 | `public double bankTaxUnder(TaxPolicy p)` | The bank's profit tax under another policy (0.7.36, the Policy spec's M2): Bank.taxAt() at that policy's retail rate, bankProfitTaxRate()'s rule - so NEXT month's bill, the tax being in arrears. |
| 2715 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 2732 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 2740 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 2768 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 2800 | 63 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 2871 | 16 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 2948 | 96 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 3057 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 3070 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 3097 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 3114 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 3141 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 3170 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 3181-3327)

| line | len | member | says |
|---:|---:|---|---|
| 3223 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 3226 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 3231 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 3236 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 3241 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 3268 | 50 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 3320 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 3326 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 3328-3449)

| line | len | member | says |
|---:|---:|---|---|
| 3336 | 12 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 3350 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 3353 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 3356 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 3359 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 3362 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 3372 | 21 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 3395 | 7 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 3415 | 29 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 3446 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 3450-3509)

| line | len | member | says |
|---:|---:|---|---|
| 3453 | 1 | `public double fundSharesValue()` | Its shares, both books, at the city's mark (Exchange.cityMark(): the last trade, fair value once that is a year old). |
| 3455 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 3457 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 3463 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 3465 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 3467 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes, on the bank's share at the city's mark. |
| 3471 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 3473 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 3477 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 3482 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands, at the withdrawal in force: TreasuryFund.withdrawalOn(fundValue()) - transferOn() at the default. |
| 3485 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the city's mark: the Holdings page's line for it until 0.7.39 (FundView reads each book itself now). |
| 3489 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 3493 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 3495 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 3500 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 3504 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 3510-3632)

| line | len | member | says |
|---:|---:|---|---|
| 3513 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 3514 | 7 | `public void setFundDial(double dial)` |  |
| 3523 | 1 | `public double getFundWithdrawal()` | The fund's withdrawal, a share of its whole value a month (0.7.48, C1): TreasuryFund.getWithdrawal(), Norway's rule by default. |
| 3525 | 8 | `public void setFundWithdrawal(double share)` | ...set by the player in whole steps of TreasuryFund.WITHDRAWAL_STEP, from nothing to 10% a month, and recorded. |
| 3535 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 3536 | 8 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 3554 | 14 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, month by month), a month's worth. |
| 3570 | 9 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 3585 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 3613 | 19 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 3633-3749)

| line | len | member | says |
|---:|---:|---|---|
| 3636 | 11 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 3649 | 11 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 3666 | 1 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 3669 | 8 | `public void fundBuyShares(int company, double money, double limit)` | ...at a price a share the player names (0.7.39, the spec's D2): 0 is fair value, what the rule asks at (it bids TreasuryFund.RULE_PREMIUM over since 0.7.48). |
| 3679 | 1 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 3682 | 8 | `public void fundSellShares(int company, double shares, double limit)` | ...at a price a share the player names (0.7.39): 0 is fair value. |
| 3692 | 1 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 3695 | 6 | `public void fundBuyBond(int bondId, double money, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 3703 | 1 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |
| 3706 | 7 | `public void fundSellBond(int bondId, double face, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 3715 | 3 | `public double fundCashFree()` | The fund's cash a new buy may hold: its cash less what the hand's buys already hold (TreasuryFund.handReserve(), 0.7.39). |
| 3720 | 4 | `private static String shareCount(double n)` | A count of shares in a decision's words: whole from a hundred, else to two places, and four under one - a consolidated holding is a fraction (0.7.39). |
| 3726 | 5 | `private String atLimit(double limit, boolean bond)` | A decision's words for a named price: " at D$101.35k", " at 102.80 per 100"; nothing at fair value. |
| 3740 | 9 | `public boolean fundCancelOrder(int i)` | CANCELS ONE OF THE PLAYER'S ORDERS before the step posts it (0.7.39, the spec's D3): the order at this place in TreasuryFund.getHandOrders(). |

### an Insane founding (lines 3750-3857)

| line | len | member | says |
|---:|---:|---|---|
| 3767 | 6 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar term loan, LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.landBondUsd() - the pl... |
| 3805 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 3828 | 10 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 3848 | 9 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |

### THE STRATEGIC RESERVE (0.7.85, batch O8; runs/spec-oil.md 2.8) (lines 3858-3954)

| line | len | member | says |
|---:|---:|---|---|
| 3885 | 10 | `public double fillReserve(double tonnes)` | Orders crude for the strategic reserve: `tonnes`, cut to the room its tanks have left and to what the treasury could pay at crude's import price (what no tonne costs more than) - bought in the month's crude market and... |
| 3902 | 6 | `public double reserveFillMost()` | The most a fill order would take now (fillReserve()'s cut): the room the reserve's tanks have left, and what the treasury could pay at crude's import price - the reach of the Fill lever on Oil's page (0.7.96, OilView). |
| 3916 | 12 | `public double releaseReserve(double tonnesAMonth)` | Releases the strategic reserve's crude, `tonnesAMonth` a month from the next clearing on, as long as it holds any; nothing stops it. |
| 3936 | 12 | `private void settleReserve()` | The reserve's month, settled at the strike beside the businesses it traded with (StrategicReserve.settle()): the treasury pays for the crude bought - a promise, the crude having landed - and takes in what the refiners... |
| 3950 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 3955-4226)

| line | len | member | says |
|---:|---:|---|---|
| 3974 | 77 | `private void runRetirement()` |  |
| 4067 | 19 | `private int retireWornOutWells()` | THE WELLS PAST THEIR LIFE (0.7.84, batch O7; spec-oil 2.6): every land well lifting under ten barrels a day (sectors.Oil.wornOut(): 263 months on land) is retired this month, oldest first, by the path any retired buil... |
| 4097 | 22 | `private int decommissionAtSea(ham.citybuildersim.sectors.Oil wells)` | THE OIL AT SEA DECOMMISSIONED (0.7.91, batch O10): once the city's oil is worked out, every platform's jacket that holds no well, and - with no jacket left standing - every kilometre of pipeline, retired by the path a... |
| 4122 | 1 | `public int getWellsWornOut()` |  |
| 4131 | 15 | `private void closeBranches(int wanted)` | Closes branches (0.7.19: as many as the rule says, at once; one at a time from 0.7.11 round 2 until then): the buildings retired by the path any retired building takes (retire()), sold by their owner, retail. |
| 4149 | 1 | `public int getBranchesClosed()` |  |
| 4162 | 64 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 4227-4276)

| line | len | member | says |
|---:|---:|---|---|
| 4247 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 4259 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 4268 | 1 | `public int getConstructionShedMonth()` |  |
| 4269 | 1 | `public double getConstructionShedPoints()` |  |
| 4272 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 4277-4573)

| line | len | member | says |
|---:|---:|---|---|
| 4297 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 4310 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 4330 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 4338 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 4345 | 3 | `public double getLastWriteOff()` |  |
| 4385 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 4395 | 178 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) (lines 4574-4775)

| line | len | member | says |
|---:|---:|---|---|
| 4644 | 4 | `public static int deskCallsMost(int quantity)` | The most times an order of this many may ask the bond desk: the countdown's slices, then doubling down and halving, each at most once a bit of the order. |
| 4655 | 1 | **type** `public record Afford(int quantity, boolean atPrime, double firstRate, int deskCalls)` | What consider() decided of an order (0.7.54): the largest slice that carries its interest, 0 for none; whether any slice would have carried it at prime and its record, for the refusal; the rate at the whole order; and... |
| 4658 | 6 | `private Afford largestSliceThatCarries(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | The largest slice of the order that carries its interest - see THE LARGEST SLICE, WITHOUT COUNTING TO IT. |
| 4672 | 21 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes)` | The largest n from 1 to quantity that passes, 0 for none, found as THE LARGEST SLICE, WITHOUT COUNTING TO IT says: the countdown's first COUNTDOWN_SLICES, then doubling down, then halving. |
| 4695 | 80 | **type** `private final class Slices` | One order's slices, each asked as the countdown asked it (until 0.7.54, consider()'s loop). |
| 4704 | 6 | `Slices(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` _(in Game.Slices)_ |  |
| 4712 | 35 | `boolean passes(int n)` _(in Game.Slices)_ | Whether n of them carry their interest; what failed at prime and the rate at the whole order are kept for the refusal. |
| 4749 | 4 | `boolean carriesAtPrime(int n, double borrowed)` _(in Game.Slices)_ | Whether n of them would carry their interest at prime and the sector's record. |
| 4755 | 10 | `void askPrimeOfTheRest(int q)` _(in Game.Slices)_ | When none passes: whether any of the q slices would have carried it at prime - every slice to PRIME_SCAN_SLICES, the four that decide it past that. |
| 4767 | 5 | `void askPrime(int n)` _(in Game.Slices)_ | Whether n of them would have carried it at prime, as the countdown asked it of a slice that failed. |
| 4773 | 1 | `Afford result(int quantity)` _(in Game.Slices)_ |  |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 4776-4990)

| line | len | member | says |
|---:|---:|---|---|
| 4833 | 3 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 4838 | 4 | `public boolean buysOnMortgage(String sector, BuildingsTemplate t)` | Whether this sector buys this building on an insured mortgage: a landlord's home (0.7.11) - the rule consider() and financingOf() share. |
| 4855 | 19 | `public double[] financingOf(String sector, BuildingsTemplate t)` | HOW ONE OF THESE WOULD BE PAID FOR (0.7.75, the sector statements' R4): consider()'s split of an order of one, read and never acted on - what it would cost (BusinessInvestment.getCostOf()); what the register would ask... |
| 4883 | 61 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, of what the planner asked for (Mortgage.decide(), which halves since 0.7.54) - and th... |
| 4953 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 4957 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 4970 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 4991-5071)

| line | len | member | says |
|---:|---:|---|---|
| 5029 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 5037 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 5042 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 5047 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 5050 | 21 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 5072-5816)

| line | len | member | says |
|---:|---:|---|---|
| 5085 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 5102 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 5124 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 5129 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 5232 | 1 | `public double getBankedClearedAtLoad()` |  |
| 5235 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 5257 | 29 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 5295 | 5 | `public int getConstructionOutputAtEveryPost()` | What the builders would do this month with every post offered, at the city's fill (0.7.17): the figure getConstructionOutput() was before the builders laid idle crews off. |
| 5302 | 4 | `public int getBuildingOutputAtEveryPost()` | ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. |
| 5316 | 9 | `void strikeBuildersCrews()` | The builders strike the month's crews (0.7.17): the work ahead - the repairs of the city standing now and every point still owed on site after this month's advance - against what their depots and the city's works depa... |
| 5337 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 5354 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 5391 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 5410 | 10 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 5422 | 4 | `double portsBill(double[] fill)` | The sea terminals' crews at these fill rates and their upkeep, a month (0.7.86): paid with transit's bill (TreasuryLine.TRANSIT); nothing with no terminal. |
| 5452 | 81 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 5543 | 1 | `public double getCityMaintenancePaid()` |  |
| 5548 | 4 | `public int getConstructionMaterials()` |  |
| 5552 | 4 | `public double getInterestRate()` |  |
| 5556 | 3 | `public boolean isGraphsEnabled()` |  |
| 5559 | 3 | `public boolean isReportsEnabled()` |  |
| 5586 | 85 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 5673 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 5702 | 1 | `public boolean hasNewReceipt()` |  |
| 5703 | 1 | `public void clearReceipt()` |  |
| 5705 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 5719 | 97 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 5767 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 5776 | 33 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the bitumen its paving takes, quoted (Markets.quote()), and the refiners' sales tax rate it is credited at (0.7.83). |
| 5811 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 5814 | 1 | `public double unitsBeyondYard()` _(in Game.BuildQuote)_ | The material units the crews will draw beyond the yard, which the allowance was priced on. |

### THE BUILDERS' PRICE (0.7.19) (lines 5817-5992)

| line | len | member | says |
|---:|---:|---|---|
| 5873 | 5 | `private String ownerOfOrder(BuildingsTemplate t)` | Who ordered a building of this kind, for a save that did not say (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays for them (BusinessInvestment.planBank()); the city for the rest. |
| 5887 | 6 | `private double recoveredOnOldContract(String payer, BuildingsTemplate t)` | ...and the share of the tax on it that owner gets back, for the same save (revised 0.7.19): on the tax a building of it carries at today's price (EconomyManager.taxRecoveredShare()). |
| 5906 | 18 | `private double depotWageBillPerPoint(boolean today)` | A Construction Depot's posts - the builders' own job mix - at today's wages or at the founding ladder (PayTier, in today's unit), over the points it makes. |
| 5926 | 3 | `private double buildersSalesRate()` | The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. |
| 5929 | 3 | `private double plantSalesRate()` |  |
| 5940 | 5 | `private double billableMaterial(double salvageCost, double plantCost, double importCost)` | What a draw of material cost the builders, as they bill it on (0.7.19): the salvage at what they paid for it, the plant's net of the credit they claim on it, the world's at its landed cost (its tax is charged and cred... |
| 5956 | 3 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 5961 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds)` | ...against a yard holding `yardHolds` units (0.7.40): an order in a run, priced on the yard the orders before it leave (buildRunInvoice()). |
| 5966 | 26 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead)` | ...and with `bitumenAhead` tonnes of the refiners' bitumen taken by the orders before it in a run (0.7.83; drawBitumen()). |

### BITUMEN FOR THE PAVING (0.7.83, batch O6; runs/spec-oil.md 2.5) (lines 5993-6186)

| line | len | member | says |
|---:|---:|---|---|
| 6019 | 6 | `public double bitumenFor(BuildingsTemplate t)` | Tonnes of bitumen one of these takes (0.7.83): a road - INFRASTRUCTURE with road capacity - past a Gravel Road's material, its surface's tonnes at BITUMEN_BINDER_SHARE; none for anything else, and none in a catalogue ... |
| 6027 | 3 | `public double bitumenForPaving()` | ...and one gravel road paved (0.7.83): a Paved Road's less a Gravel Road's, which is a Paved Road's - the bed has none. |
| 6032 | 3 | `private double refinersSalesRate()` | The refiners' sales tax rate: the builders' credit on the bitumen they buy from them (0.7.83). |
| 6039 | 1 | `public double getBitumenTonnes()` |  |
| 6040 | 1 | `public double getBitumenCost()` |  |
| 6041 | 1 | `public double getBitumenImported()` |  |
| 6050 | 8 | `public Markets.Draw drawBitumen(double tonnes)` | Draws an order's bitumen (0.7.83): the builders buy it as they buy the material beyond the yard (drawMaterials()), off the refiners' tanks and the rest from the world (Markets.draw()), booked in their ledger and bille... |
| 6068 | 4 | `public double quoteMonths(BuildingsTemplate template, int quantity)` | Months an order would take to finish (0.7.17): the wait every planner reads for its lead time and order size - BuildingManager.waitFor(), the order's points over the share of the site output it would get beside everyt... |
| 6081 | 5 | `public double onSiteMonths(BuildingsTemplate template)` | Months what is on site of one building would take to finish (0.7.20): the same wait at the same output as quoteMonths(), with no order added - BuildingManager.waitOnSite(). |
| 6098 | 4 | `public double siteMonths(String key)` | ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on site would take at today's queue - a stack's by its key, or a demolition's - at the output the quote reads. |
| 6111 | 4 | `public double quoteCityMonths(BuildingsTemplate template, int quantity)` | ...and what a city order of n of these would wait if placed now (0.7.22, after the docs pass): quoteMonths() exactly with no order set and no rush on its site; otherwise where the order would land in the player's orde... |
| 6141 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 6169 | 9 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |

### MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) (lines 6187-6280)

| line | len | member | says |
|---:|---:|---|---|
| 6217 | 31 | `public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues)` | Settles the month's site work with the owners who ordered it. |
| 6254 | 7 | `private double deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 6277 | 3 | `public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 6281-6607)

| line | len | member | says |
|---:|---:|---|---|
| 6314 | 3 | `public boolean isCitysToDemolish(BuildingsTemplate t)` | What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). |
| 6319 | 3 | `public boolean isBuyOutable(BuildingsTemplate t)` | What the city may buy out and demolish: a building a sector owns. |
| 6328 | 7 | **type** `public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price, d...` | A demolition, priced and laid out BEFORE the player commits: its work, its price, what it closes, what comes back and when. |
| 6333 | 1 | `public double salvageProceeds()` _(in Game.DemolitionQuote)_ | What the builders would pay for the material today, for as much as their cash covers. |
| 6337 | 8 | **type** `public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground, d...` | A buy-out, priced: the compensation, part by part, and the demolition after it. |
| 6341 | 1 | `public double compensation()` _(in Game.BuyOutQuote)_ | What the owner is paid. |
| 6343 | 1 | `public double total()` _(in Game.BuyOutQuote)_ | ...and what it all costs the city, the demolition included. |
| 6356 | 8 | `private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months)` | D. |
| 6366 | 27 | `public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n)` | What demolishing n standing buildings of this kind would do and cost. |
| 6395 | 14 | `public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell)` | A shell's demolition: DEMOLITION_SHARE of the work it holds, the material it drew to salvage, its ground. |
| 6411 | 4 | `private double demolitionMonths(BuildingsTemplate t, int n, double points)` | Months a demolition of these points would take at today's queue, at the output the quote reads. |
| 6424 | 15 | `public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n)` | E. |
| 6442 | 1 | `public String getLastHandRefusal()` |  |
| 6445 | 13 | `public boolean demolish(BuildingsTemplate t, int n)` | D. |
| 6460 | 36 | `public boolean buyOutAndDemolish(BuildingsTemplate t, int n)` | E. |
| 6498 | 6 | `public int demolishable(BuildingsTemplate t)` | How many of this kind stand that no demolition has been ordered for - nor, a gravel road, its paving (0.7.70). |
| 6506 | 9 | `private void placeDemolition(DemolitionQuote q, String from, double groundPaid)` | The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). |
| 6526 | 11 | `private void closeDemolished()` | D. |
| 6539 | 17 | `public boolean demolishShell(int templateId)` | C. |
| 6564 | 14 | `public BuildQuote quoteRestart(ConstructionControl.Shell shell)` | C. |
| 6580 | 4 | `private double restartMonths(BuildingsTemplate t, int n, double pointsLeft)` | A restart's wait, by the one wait (BuildingManager.restartWait()). |
| 6586 | 21 | `public boolean restartShell(int templateId)` | C. |

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) (lines 6608-6781)

| line | len | member | says |
|---:|---:|---|---|
| 6611 | 1 | `private BuildingsTemplate paveFrom()` | The road the city can pave and the road it becomes, as this city's catalogue has them; null for one it has not. |
| 6612 | 1 | `private BuildingsTemplate paveTo()` |  |
| 6615 | 6 | `public int paveable()` | Gravel roads the city could pave now: standing, not ordered demolished, and not already being paved. |
| 6623 | 1 | `public int pavingNow()` | Gravel roads being paved now: their Paved Roads on site, the gravel roads still standing and carrying traffic. |
| 6626 | 4 | `public boolean isPavingSite(String key)` | Whether a site is the Paved Road site with paving on it, which is not stopped (ConstructionControl, F). |
| 6645 | 15 | `public BuildQuote quotePave(int n)` | F. |
| 6673 | 28 | `public boolean paveRoads(int n)` | F. |
| 6703 | 13 | `public boolean cancelSite(String key, boolean cancel)` | C. |
| 6724 | 8 | `public double[] cancelRefundNow(String key)` | C. |
| 6734 | 10 | `public boolean rushSite(String key, boolean on)` | B. |
| 6746 | 11 | `public boolean moveSite(String key, int by)` | A. |
| 6759 | 5 | `public void resetSiteOrder()` | A. |
| 6772 | 9 | `public double[] rushPreview(String key)` | B. |

### in the month (lines 6782-7017)

| line | len | member | says |
|---:|---:|---|---|
| 6791 | 1 | `public ConstructionControl.Events getControlThisMonth()` |  |
| 6792 | 1 | `public double getOvertimePaidThisMonth()` |  |
| 6793 | 1 | `public double getDemolitionSalvageThisMonth()` |  |
| 6800 | 73 | `public void settleConstructionControl(ConstructionControl.Events ev)` | Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. |
| 6875 | 14 | `private double[] overtimeWagesByType(double premium)` | The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). |
| 6909 | 104 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 7016 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 7018-7202)

| line | len | member | says |
|---:|---:|---|---|
| 7033 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 7045 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 7061 | 1 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 7064 | 1 | `public int wellsCommitted()` | ...and the Oil Wells standing, being built or ordered (0.7.62): one an oil site - since 0.7.91 the land wells and the platforms' wells. |
| 7067 | 1 | `public int landWellsCommitted()` | The land wells standing, being built or ordered (0.7.91): what the dry sites are counted against - every oil well until the platforms' came. |
| 7070 | 1 | `public int platformWellsCommitted()` | ...and the platforms' wells (0.7.91): what the platforms' slots are counted against. |
| 7073 | 12 | `private int committedWhere(java.util.function.Predicate<BuildingsTemplate> which)` | The buildings `which` picks, standing, on site or ordered. |
| 7093 | 12 | `public int committedOn(Resource r)` | The MINING buildings that stand on a resource's sites - standing, on site or ordered (0.7.62): every MINING template whose good is the resource's (siteOf()) - and since 0.7.91 not an offshore platform's jacket, which ... |
| 7114 | 8 | `public int committedFor(BuildingsTemplate t)` | What an order for this building is counted against its sites with (0.7.91, batch O10): a land well the land wells, a platform's well the platforms' wells, a platform's jacket the jackets on site (those standing have t... |
| 7130 | 7 | `public static Resource siteOf(BuildingsTemplate t)` | The resource whose sites a building stands on (0.7.62): a MINING template's, the resource whose good it makes - iron for an Iron Mine, oil for an Oil Well; null for anything else. |
| 7148 | 3 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 7153 | 6 | `private boolean hasDepositFor(BuildingsTemplate template, int quantity, int before)` | ...with `before` more on the same sites already put on site by the orders ahead of it in a run (0.7.40, buildRunAhead()). |
| 7168 | 7 | `public double remainingFor(BuildingsTemplate template)` | What is left in the ground for a building that stands on a resource's sites (siteOf()): its resource's remainder - and since 0.7.93 (THE TWO OIL POOLS, LandManager) a land well's the ground pool's, a platform's jacket... |
| 7191 | 11 | `public int sitesFor(BuildingsTemplate template)` | The sites the city owns that a building may stand on (0.7.84, batch O7; spec-oil 2.6): its resource's sites (siteOf()) - and for a land well (sectors.Oil.isLandWell()) only the dry ones, a field whose centre is on lan... |

### THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) (lines 7203-7415)

| line | len | member | says |
|---:|---:|---|---|
| 7228 | 3 | `public double getFreshRights()` | The city's water rights, units a month (see THE FRESH WATER LIMIT AND THE COAST). |
| 7233 | 3 | `void setFreshRights(double units)` | Harnesses and the load only: sets the rights, never below nothing. |
| 7238 | 3 | `public double getFreshCap()` | The fresh water limit, units a month: what the owned lakes and river yield plus the rights. |
| 7250 | 8 | `double derivedFreshRights()` | The rights an older save is given (spec-land 2.9): the fresh plants' nameplate standing less what the city's lakes and river yield, never below nothing - raised by the last unit in the last place when the limit they m... |
| 7260 | 3 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity)` | Whether this order can stand on the city's coast: true for everything but a desalination plant and a sea terminal (0.7.86), which need owned sea. |
| 7273 | 66 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 7349 | 1 | `public Investor getGovernmentInvestor()` |  |
| 7352 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 7354 | 46 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 7401 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 7412 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |

### A RUN OF ORDERS (0.7.40) (lines 7416-7960)

| line | len | member | says |
|---:|---:|---|---|
| 7456 | 14 | `public double buildRunInvoice(java.util.Map<BuildingsTemplate, Integer> run)` | What placing these orders in turn would charge altogether: each one's quote on the yard the ones before it leave. |
| 7472 | 3 | `public double buildFundingGap(java.util.Map<BuildingsTemplate, Integer> run)` | What the treasury is short of for the whole run: its invoice less the cash, never below nothing - an overdraft counted in full, as one order's buildFundingGap(template, quantity) counts it. |
| 7477 | 3 | `public int buildRunAhead(java.util.Map<BuildingsTemplate, Integer> run)` | How many of the run's orders, from the first, would pass the checks money cannot fix - ore, the coast, licences, ground - each placed after the ones before it. |
| 7482 | 5 | `public BuildResult buildRunStop(java.util.Map<BuildingsTemplate, Integer> run)` | What the first order that would not go ahead is refused for - NO_DEPOSIT, NO_COAST, NO_LICENCE or NO_LAND - or SUCCESS when every order passes. |
| 7488 | 27 | `private int runAhead(java.util.Map<BuildingsTemplate, Integer> run, BuildResult[] stop)` |  |
| 7517 | 7 | `static int sitePool(BuildingsTemplate t)` | The sites an order is counted against in a run (0.7.91): its resource's (siteOf()), a platform's well and a platform's jacket each a pool of their own after the resources; -1 for none. |
| 7526 | 3 | `public double cashShortfall()` | What the treasury is overdrawn by right now (0.7.40): the cash below nothing, or nothing - Finances' "overdrawn by" ask. |
| 7531 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). |
| 7534 | 3 | `public double getTotalBuildingCost()` |  |
| 7537 | 3 | `public String getBuildingName()` |  |
| 7540 | 3 | `public int getBuildQuantity()` |  |
| 7545 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 7558 | 111 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 7677 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 7689 | 13 | `private String issueNote(DebtQuote quote)` | The booking every note shares - handleTBillLogic()'s, and since 0.7.13 the rollover's, which books the quote it sized (issueForRollover()). |
| 7709 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 7747 | 17 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 7807 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 7819 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 7840 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 7869 | 35 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 7910 | 5 | `private double longBondNetProceeds(double faceValue, double marketRate, int duration)` | What the city actually banks for a long bond of this face: its worth at the rate struck, less the fees, to the cent. |
| 7917 | 13 | `private DebtQuote longBondQuote(double requested, int duration, double marketRate, double before, double faceValue, double rece...` | A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. |
| 7932 | 7 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |
| 7944 | 16 | `private String issueLongBond(DebtQuote quote)` | The booking both long-bond issues share. |

### A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) (lines 7961-8044)

| line | len | member | says |
|---:|---:|---|---|
| 7989 | 8 | `private double longBondFaceForProceeds(double cashNeeded, int duration, double rounding, double marketRate)` | Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. |
| 8006 | 29 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a term bond whose CASH covers cashNeeded would cost. |
| 8037 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 8045-8379)

| line | len | member | says |
|---:|---:|---|---|
| 8087 | 14 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 8125 | 11 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 8150 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation - expected, since 0.7.42 (below) - less the world's base rate less the world's realised inflation - the one defi... |
| 8163 | 5 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the inflation savers expect (Expectations, since 0.7.42 - the same expected inflation the real rate differential reads; the year's, as the parity ... |
| 8170 | 3 | `public double realPolicyRate()` | The policy rate less the inflation people expect (0.7.45): the dial in real terms, ex ante as every real rate here is since 0.7.42. |
| 8182 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 8187 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 8190 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 8197 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 8264 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 8284 | 78 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 8369 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |

### THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 8380-8454)

| line | len | member | says |
|---:|---:|---|---|
| 8401 | 12 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | A serial bond whose CASH covers cashNeeded: quoteMediumBond() at the face that brings it. |
| 8419 | 14 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note, serial or term loan whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 8435 | 8 | `private double foreignProceedsPerFace(String type, double rate, int duration)` | What a unit of a dollar instrument's face banks at this rate, net of the spread - never under MIN_PROCEEDS_PER_FACE, so the search above always moves. |
| 8445 | 9 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 8455-9943)

| line | len | member | says |
|---:|---:|---|---|
| 8533 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 8536 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 8539 | 11 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 8557 | 8 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 8567 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not, and whether it is dollar paper rolled at home. |
| 8569 | 13 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 8584 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 8598 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 8669 | 54 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 8732 | 9 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; a serial by quoteMediumBondForCash(); a term loan by quoteLo... |
| 8743 | 4 | `private double localFace(boolean abroad, DebtQuote quote)` | A rollover quote's face in local money: a dollar quote's at the day's rate. |
| 8754 | 18 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 8775 | 3 | `private void printPopulationInfo()` | printers |
| 8780 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 8793 | 3 | `private void printUtilityInfo()` |  |
| 8797 | 3 | `private void printCityStats()` |  |
| 8804 | 832 | `private void nextMonth()` |  |
| 9637 | 278 | `private void startOfMonthUpdate()` |  |
| 9915 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 9944-10081)

| line | len | member | says |
|---:|---:|---|---|
| 9993 | 4 | `private double issuanceFee()` | The fee in today's money: founding / unit, at the expected price level since 0.7.42 (THE ANCHOR, beside restrikeMoneyConstants()). |
| 10018 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 10030 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 10050 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 10072 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |
| 10078 | 3 | `public double minimumIssueSizeUsd()` | ...in US dollars at today's rate (0.7.40): Finances' "the minimum" on the ask abroad, which is asked in dollars. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 10082-10351)

| line | len | member | says |
|---:|---:|---|---|
| 10096 | 16 | `public String getCreditRating()` |  |
| 10114 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 10137 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 10151 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 10173 | 28 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 10202 | 92 | `private void finalUpdateEconomy()` |  |
| 10296 | 4 | `private void updateConstructionCost()` |  |
| 10307 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 10318 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 10322 | 4 | `public long getHouseholdCapacity()` |  |
| 10326 | 4 | `public long getStoreCapacity()` |  |
| 10330 | 3 | `public long[] getJobs()` |  |
| 10335 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 10352-10963)

| line | len | member | says |
|---:|---:|---|---|
| 10381 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 10417 | 1 | `public PopulationCohorts getCohorts()` |  |
| 10418 | 1 | `public FamilyModel getFamilies()` |  |
| 10419 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 10420 | 1 | `public Migration getMigration()` |  |
| 10421 | 1 | `public Health getHealth()` |  |
| 10422 | 1 | `public Healthcare getHealthcare()` |  |
| 10446 | 517 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 10964-10976)

| line | len | member | says |
|---:|---:|---|---|
| 10972 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 10975 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 10977-11265)

| line | len | member | says |
|---:|---:|---|---|
| 10993 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 11012 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 11032 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 11074 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 11100 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 11110 | 3 | `public BuildLog getBuildLog()` |  |
| 11117 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 11142 | 1 | `public Unemployment getUnemployment()` |  |
| 11146 | 1 | `public Sickness getSickness()` |  |
| 11154 | 1 | `public Crime getCrime()` |  |
| 11158 | 1 | `public double getLastOrphanDeaths()` |  |
| 11159 | 1 | `public double getLastUnhousedDeaths()` |  |
| 11161 | 6 | `{ ... }` |  |
| 11169 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 11194 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 11203 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 11224 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 11235 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 11248 | 1 | `public double getStudentLoansLent()` |  |
| 11249 | 1 | `public double getStudentLoansRepaid()` |  |
| 11250 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 11254 | 1 | `public double getStudentLoanInterest()` |  |
| 11257 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 11264 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 11266-11400)

| line | len | member | says |
|---:|---:|---|---|
| 11281 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 11287 | 7 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 11296 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 11308 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 11352 | 1 | `public Equity getEquity()` |  |
| 11356 | 1 | `public Exchange getExchange()` |  |
| 11364 | 1 | `public BondMarket getBondMarket()` |  |
| 11374 | 1 | `public TreasuryFund getFund()` |  |
| 11390 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### THE ANCHOR (0.7.42): what the city expects prices to do, and the (lines 11401-11439)

| line | len | member | says |
|---:|---:|---|---|
| 11420 | 1 | `public Expectations getExpectations()` | Expected inflation, credibility and the expected price level. |
| 11423 | 1 | `public double getExpectedInflation()` | What the city expects inflation to be, a fraction a year. |
| 11426 | 1 | `public double getCredibility()` | How far the city believes the central bank, Expectations.KMIN to KMAX. |
| 11434 | 5 | `public double anchoredDrift()` | The currency's anchored drift, a fraction a year (ForeignAccounts, THE ANCHORED DRIFT): the credible part of expected inflation - credibility times the target - against the world's realised inflation, and nothing befo... |

### what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) (lines 11440-11570)

| line | len | member | says |
|---:|---:|---|---|
| 11443 | 26 | `double[] indexPrices()` | Each component's price this month, in PriceIndex's order: the shelf, the average rent paid, a meal, a luxury piece; the services' price is struck by the index from the fees. |
| 11471 | 8 | `double[] indexSpends()` | ...what the households spent on each: groceries and rent off the sectors' statements, meals and luxury off their own purchases; the services' is the index's sum of the fee lines. |
| 11486 | 14 | `double[] indexFees()` | ...each fee line's price: the clinic's fee for a general visit at the player's scale, a seat's tuition (the adult kinds' fees averaged - a price, so a shift in who studies what is not inflation), the fare a ride is ch... |
| 11502 | 8 | `double[] indexFeeSpends()` | ...and what the households paid on each fee line, off their books. |
| 11539 | 23 | `void restrikeMoneyConstants()` | Every money constant that prices something, struck at the expected price level: founding / unit x Expectations.getStruckLevel(), the level the last month ended on. |
| 11564 | 6 | `private void handOnExpectations()` | ...and what the month hands on from the anchor that is not a constant: the investors' expected inflation (0 before the basket is based), the currency's drift, and - since 0.7.43 - the drift every sticky seller's price... |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 11571-11594)

| line | len | member | says |
|---:|---:|---|---|
| 11581 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 11590 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 11593 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 11595-11965)

| line | len | member | says |
|---:|---:|---|---|
| 11607 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 11609 | 1 | `public PriceIndex getPriceIndex()` |  |
| 11611 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 11618 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 11625 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 11636 | 5 | `public Sectors.TradeByGood getTradeByGood()` | The month across the city's edge, good by good (0.7.35): the businesses' struck statements split by good, an import with no good behind it by the sector that bought it, the households' cars among the cars bought and, ... |
| 11652 | 3 | `public double getOwnReserves()` | What is left of the vault once everything owed against it is taken off: the dollar paper outstanding and the foreign money parked in the bank, both claims on the one pot (0.7.35; the Trade tab's own subtraction until ... |
| 11657 | 5 | `public double getSharesHeldAbroad()` | The city's shares in foreign hands, at each company's price - its last trade, or its fair value before one (0.7.35; the Trade tab's own sum until then). |
| 11671 | 3 | `public double getHeldAbroadPrivately()` | The businesses' and the households' paper abroad, at today's rate: what is held abroad outside the vault. |
| 11676 | 3 | `public double getHeldAbroad()` | ...and the vault with it: everything the city holds abroad. |
| 11681 | 3 | `public double getHeldHereByTheWorld()` | What the world holds here: the city's shares at their price, the businesses' bonds at face, the foreign money parked in the bank and the city's dollar paper at today's rate. |
| 11692 | 1 | `public Bank getBank()` |  |
| 11705 | 1 | `public CentralBank getCentralBank()` |  |
| 11719 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 11725 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 11728 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 11735 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 11739 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 11743 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 11810 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 11814 | 3 | `public EconomyManager getEconomyManager()` |  |
| 11817 | 3 | `public PopulationManager getPopulationManager()` |  |
| 11821 | 1 | `public LabourMarket getLabourMarket()` |  |
| 11822 | 1 | `public Education getEducation()` |  |
| 11838 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 11863 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 11905 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 11923 | 40 | `public void recordMonth()` | Files this month in the graph history. |
| 11964 | 1 | `public Inbox getInbox()` |  |
| 11965 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 11966-12427)

| line | len | member | says |
|---:|---:|---|---|
| 11977 | 3 | `public int getMonthsUntilAutosave()` |  |
| 11993 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 12005 | 305 | `public void save(int slot, String slotName)` |  |
| 12319 | 5 | `public String takeSkipFailure()` |  |
| 12328 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 12330 | 1 | `public GameFiles getGameFiles()` |  |
| 12349 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 12367 | 18 | `public void sendBuildingSave()` |  |
| 12388 | 16 | `public void loadBuildings()` |  |
| 12418 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 12428-12568)

| line | len | member | says |
|---:|---:|---|---|
| 12455 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 12456 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 12457 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 12464 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 12486 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 12553 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 12562 | 1 | `public double getCityPaperSettled()` |  |
| 12563 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 12564 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 12566 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 12569-12644)

| line | len | member | says |
|---:|---:|---|---|
| 12597 | 1 | `public double getCouponsToHouseholds()` |  |
| 12598 | 1 | `public double getPrincipalToHouseholds()` |  |
| 12599 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 12610 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 12618 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 12631 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 12645-12670)

| line | len | member | says |
|---:|---:|---|---|
| 12655 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 12671-12727)

| line | len | member | says |
|---:|---:|---|---|
| 12688 | 39 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 12728-12874)

| line | len | member | says |
|---:|---:|---|---|
| 12782 | 56 | `private void openMarketOperation()` |  |
| 12846 | 28 | `private void buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 12875-13110)

| line | len | member | says |
|---:|---:|---|---|
| 12980 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 12983 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 12990 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 13002 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 13009 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 13015 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 13028 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 13059 | 51 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 13111-13132)

| line | len | member | says |
|---:|---:|---|---|
| 13126 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 13130 | 1 | `public double getBuybackToHouseholds()` |  |
| 13131 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 13133-13289)

| line | len | member | says |
|---:|---:|---|---|
| 13205 | 13 | `private void takeTreasuryMonth()` |  |
| 13220 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 13222 | 1 | `public double getTreasuryOpening()` |  |
| 13223 | 1 | `public double getTreasuryClosing()` |  |
| 13224 | 1 | `public double getTreasuryRaised()` |  |
| 13225 | 1 | `public double getTreasuryRepaid()` |  |
| 13226 | 1 | `public double getTreasurySurplus()` |  |
| 13229 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 13239 | 3 | `public double getNetPosition()` | The city's net position (0.7.32, the Finances tab's THE BALANCE): its cash - below nothing when it is overdrawn - less the paper it owes and what its central bank has advanced it. |
| 13261 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 13273 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 13278 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 13286 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |

### FROM EARNED TO THE BUDGET (0.7.31) (lines 13290-13419)

| line | len | member | says |
|---:|---:|---|---|
| 13326 | 23 | `public java.util.List<TreasuryJournal.Entry> getEarnedToBudget()` | The steps from EARNED to the budget's balance, in the Government bridge's order, signed as they move EARNED (+ adds, - takes away); every line, at nothing too. |
| 13351 | 5 | `public double getEarnedResidual()` | What the steps leave between EARNED and the budget: the dials moved since the month was struck, and nothing in a month nobody moved one. |
| 13357 | 5 | `double[] treasuryMonthToSave()` |  |
| 13363 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 13397 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 13400 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 13402 | 1 | `public double getPostAuditDriftWorst()` |  |
| 13403 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 13404 | 4 | `public void InterestExpense(double amount)` |  |
| 13409 | 3 | `public DebtManager getDebtManager()` |  |
| 13414 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 13420-13707)

| line | len | member | says |
|---:|---:|---|---|
| 13488 | 46 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 13546 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 13551 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 13573 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 13585 | 19 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 13613 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 13638 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 13645 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 13649 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 13659 | 8 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 13669 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 13672 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 13679 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 13689 | 10 | `public double getArrearsOwedTo(String sectorKey)` | Owed and unpaid to one sector's till (0.7.55): every line payDownArrears() would pay it, by the payee it would pay. |
| 13701 | 1 | `public double getArrearsPaidTo(String sectorKey)` | What the treasury paid this sector's till of its arrears this month (0.7.55) - its statement's arrears line. |
| 13703 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 13704 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 13705 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 13706 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 13708-13834)

| line | len | member | says |
|---:|---:|---|---|
| 13721 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 13729 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 13754 | 72 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 13835-15825)

| line | len | member | says |
|---:|---:|---|---|
| 13903 | 20 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 13924 | 329 | `private void rebuildSimulationState()` |  |
| 14295 | 10 | `public void loadGame(int slot)` | Load game |
| 14313 | 22 | `private void readTheMap(int slot, Long stamp)` | THE CITY MAP, READ BACK (0.7.60): from the slot's sidecar when the save's stamp is its stamp and it was drawn on this land in this month (CityMap.readSidecar()); a save that had a map whose sidecar is missing or stale... |
| 14372 | 11 | `private void restoreOilPools(DataSave loaded)` | THE TWO OIL POOLS ON A LOAD (0.7.93, batch O10b; LandManager's THE TWO OIL POOLS): once the land is in place, the offshore pool's E as saved - or, on a save from before SAVE_FORMAT 35, the one pool's E charged to the ... |
| 14384 | 60 | `private boolean restoreLandOnTheWorld(DataSave loaded, double owned)` |  |
| 14446 | 1322 | `private void readTheSave(int slot)` | The load itself; see loadGame(). |
| 15775 | 4 | `void seedFundLedger()` | THE FUND'S COST BASIS FOR A SAVE FROM BEFORE IT (0.7.39; the project's spec-fund-0739.md, 3.5): each market lot and bond at its market value this month, flagged as such, the rescue lot exact from the counters, one TRA... |
| 15784 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 15826-16080)

| line | len | member | says |
|---:|---:|---|---|
| 15832 | 1 | `public Denomination getDenomination()` |  |
| 15835 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 15866 | 191 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 16068 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

