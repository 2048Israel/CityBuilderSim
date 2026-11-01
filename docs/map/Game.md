# Game.java - 20,634 lines · 906 methods · 29 constants · model

`ham/citybuildersim/Game.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (135), [DecisionLog](DecisionLog.md) (97), [Auction](Auction.md) (86), [DebtQuote](DebtQuote.md) (79), [Debt](Debt.md) (74), [Equity](Equity.md) (69), [TreasuryLine](TreasuryLine.md) (62), [LandManager](LandManager.md) (57), [GameLog](GameLog.md) (48), [ConstructionControl](ConstructionControl.md) (40), [Landmasses](Landmasses.md) (39), [BusinessDebtManager](BusinessDebtManager.md) (36), [AgeBand](AgeBand.md) (36), [ForeignAccounts](ForeignAccounts.md) (33), [GameFiles](GameFiles.md) (32), [CityMap](CityMap.md) (32), [Rollover](Rollover.md) (30), [Resource](Resource.md) (30), [Markets](Markets.md) (27), [Good](Good.md) (26), [DebtManager](DebtManager.md) (25), [TreasuryJournal](TreasuryJournal.md) (25), [TreasuryFund](TreasuryFund.md) (24), [TaxPolicy](TaxPolicy.md) (24), [Sector](Sector.md) (23), [PriceIndex](PriceIndex.md) (23), [JobType](JobType.md) (22), [BusinessInvestment](BusinessInvestment.md) (22), [Oil](Oil.md) (22), [Bank](Bank.md) (22)... and 123 more

**Used by (233):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CementConcrete](CementConcrete.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityBasket](CityBasket.md), [CityNeeds](CityNeeds.md), [ClockCheck](ClockCheck.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConversionCheck](ConversionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtManager](DebtManager.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DenominationCheck](DenominationCheck.md), [DeskCheck](DeskCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [FloatingRateNote](FloatingRateNote.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Founding](Founding.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [HydroCheck](HydroCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InflationLinkedBond](InflationLinkedBond.md), [InfrastructureCheck](InfrastructureCheck.md), [InputRecorder](InputRecorder.md), [InterestAbroadCheck](InterestAbroadCheck.md), [InvestCheck](InvestCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandClassView](LandClassView.md), [LandMap](LandMap.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Markets](Markets.md), [MaterialsCheck](MaterialsCheck.md), [MediumTermBond](MediumTermBond.md), [MiniMap](MiniMap.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OreView](OreView.md), [OutsideCheck](OutsideCheck.md), [PassThroughCheck](PassThroughCheck.md), [Pieces](Pieces.md), [PlanCheck](PlanCheck.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [PriceLevelCheck](PriceLevelCheck.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [ReloadCheck](ReloadCheck.md), [ReplayCheck](ReplayCheck.md), [ReplayCity](ReplayCity.md), [ReplayMode](ReplayMode.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [RunRenders](RunRenders.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [SpreadPlanner](SpreadPlanner.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [UiProbe](UiProbe.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md), [WantedPostsCheck](WantedPostsCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProducts](WoodProducts.md), [WoodProductsCheck](WoodProductsCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 1095 | THE FOUNDING RESERVE (2026-09-21) |
| 1152 | · THE FOUNDING RECORD (0.7.10) |
| 1194 | THE WORLD MARKET (WN3, 2026-10-28; docs/studies/spec-nations.md 4, 6 and D28) |
| 1422 | THE WORLD'S MONEYS IN THE MONTH (WN4, 2026-10-30; docs/studies/spec-exchange-rates.md 3.5 and 3.7) |
| 1821 | THE REAL US DOLLAR (WN4, D29; RealDollar, docs/studies/spec-exchange-rates.md 3.9) |
| 1889 | THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) |
| 2019 | · THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) |
| 2202 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 2225 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 2448 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 2589 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 2747 | · THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) |
| 2895 | PRIVATE INVESTMENT |
| 3312 | · AND THE BALANCE SHEET |
| 3522 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 3608 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 3642 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 4193 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 4340 | · the preferred offer |
| 4462 | · what the fund is worth |
| 4522 | · the dial |
| 4686 | · the hand |
| 4803 | · an Insane founding |
| 4932 | WHAT THE D$ IS HELD TO (XR2, 2026-10-22; ForeignAccounts, THE REGIMES) |
| 5010 | THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) |
| 5120 | ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's |
| 5199 | SHRINKING |
| 5483 | THE CONSTRUCTION WARNING |
| 5533 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 5862 | THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) |
| 6178 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 6277 | · the lender's test for one home, for the build card (I13-L) |
| 6458 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 6539 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 7410 | THE BUILDERS' PRICE (0.7.19) |
| 7757 | · BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) |
| 7956 | MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) |
| 8050 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 8456 | · F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) |
| 8630 | · in the month |
| 8920 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 9234 | · THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) |
| 9295 | THE PORT'S LANDMASS (ET5, 2026-10-25; the ET5 design, D10) |
| 9314 | THE HYDRO STATION'S RIVER (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.2 and 1.3) |
| 9341 | THE LONG BRIDGES' CROSSING (ET6, 2026-10-26; BridgeSurvey, BridgeWorks, Bridges) |
| 9457 | DEEP WATER'S WORKS (DW1, 2026-10-31; sectors.Oil's DEEP WATER) |
| 9851 | · A RUN OF ORDERS (0.7.40) |
| 10038 | THE CITY'S PAPER, SOLD AT AUCTION (CD2, spec-city-debt.md section 4) |
| 10631 | · A BOND SIZED TO THE CASH IT BRINGS (0.7.10, CD2) |
| 10686 | THE FLOATING-RATE NOTE AND THE INFLATION-LINKED BOND (CD5, |
| 10848 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 11262 | · THE AMORTIZING BOND AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 11348 | ROLLING WHAT FALLS DUE (0.7.13) |
| 11936 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 12225 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 12447 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 12676 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 13100 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 13212 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 13523 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 14141 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 14154 | THE READINGS THE MONTH TAKES OF THE CITY |
| 14443 | · the price of a place (2026-09-21) |
| 14583 | THE ANCHOR (0.7.42): what the city expects prices to do, and the |
| 14610 | · what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) |
| 14740 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 14764 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 15190 | the save system |
| 15243 | THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) |
| 15710 | THE RECORDING (TR1, 2026-10-31) |
| 16034 | PAYING THE WORLD BACK |
| 16211 | THE HOLDERS ARE PAID (0.7.1) |
| 16337 | · the desk, for the households |
| 16433 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 16590 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 16854 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 17115 | · a buyback's holders outside the pools |
| 17140 | WHAT THE TREASURY ACTUALLY DID |
| 17349 | · FROM EARNED TO THE BUDGET (0.7.31) |
| 17495 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 17783 | BUYING YOUR OWN DEBT BACK |
| 18056 | · the call (CD4, spec-city-debt.md section 6.4; D22, D23, D24) |
| 18198 | WHY LAND IS NOT IN THE RENT FLOOR |
| 18579 | · · the three the monthly path sets and this did not |
| 20345 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 2784 | `Game.LandNeed.Kind.ROOM` |  |
| 2784 | `Game.LandNeed.Kind.SHORTFALL` |  |
| 2784 | `Game.LandNeed.Kind.DEPOSIT` |  |
| 2784 | `Game.LandNeed.Kind.COAST` |  |
| 8918 | `Game.BuildResult.SUCCESS` |  |
| 8918 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 8918 | `Game.BuildResult.NO_LAND` |  |
| 8918 | `Game.BuildResult.NO_DEPOSIT` |  |
| 8918 | `Game.BuildResult.NO_LICENCE` |  |
| 8918 | `Game.BuildResult.FAILED` |  |
| 8918 | `Game.BuildResult.NO_COAST` |  |
| 8918 | `Game.BuildResult.NO_RIVER` |  |
| 8918 | `Game.BuildResult.NO_BRIDGE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 243 | `Game.MARKET_SEED_SALT` | `0x4D41524B45545852L` | Salt for the D$'s market seed, so the market's path is not the world's own seed spelled again ("MARKETXR"). |
| 746 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 868 | `Game.FOUNDING_YARD` | `"Builders' Yard"` | The builders' building a city is founded with (PT1-M4): see foundingYard(). |
| 1144 | `Game.FOUNDING_CASH` | `100_000 * PriceLevel.FACTOR` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 1147 | `Game.FOUNDING_RESERVE_USD` | `25_000 * PriceLevel.FACTOR` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 1150 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 1468 | `Game.RISK_OFF_NAMED` | `{ "JPY", "CHF", "AUD" }` | The three the log names in a risk-off: the yen and the franc rise, the Australian dollar falls. |
| 3059 | `Game.STATIONS_SLOT` | `"Filling stations"` | Where the forecourts' word is filed among the month's investment lines (0.7.83): apart from the shops', as the bank's is. |
| 5959 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 5962 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 7568 | `Game.NO_GROUND_AHEAD` | `new GroundAhead(0, 0, 0, 0)` |  |
| 7775 | `Game.BITUMEN_BINDER_SHARE` | `.05` | The share of an asphalt surface's weight that is bitumen: five per cent [R18]. |
| 8932 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 9492 | `Game.BRIDGE_FOR_GOOD` | `"A bridge stands for good: taking one down is not built yet"` | Why a bridge cannot be demolished (ET6-C): one that stands is for good until the demolition of a bridge is built. |
| 10269 | `Game.CENTS_PER_UNIT` | `100 / PriceLevel.FACTOR` | How many of the model's proceeds granules go to a unit of money: a hundred at today's prices, and a hundred over the founding's price level (PriceLevel.FACTOR) at the founding's, so a proceeds figure is rounded to the... |
| 10909 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 11079 | `Game.NO_DOLLAR_FLOAT_OR_LINKED` | `"Floating-rate notes and inflation-linked bonds are issued at home only."` | What the treasury is told when it asks for a dollar floater or linker. |
| 11118 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 11279 | `Game.BUILD_NOTE_GRANULE` | `1000 * PriceLevel.FACTOR` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 13122 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 13134 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 13137 | `Game.BUILD_BOND_GRANULE` | `100 * PriceLevel.FACTOR` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 13154 | `Game.FIXED_ISSUE_COST` | `12 * PriceLevel.FACTOR` | Bond counsel, rating and printing. |
| 13165 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003). |
| 13205 | `Game.MINIMUM_ISSUE_FLOOR` | `50 * PriceLevel.FACTOR` | The floor under minimumIssueSize(), in thousands: $50k, so a brand new city can still borrow. |
| 14787 | `Game.SHORTFALL_DUST` | `1e-9` | A shortfall smaller than this share of an order's cost is rounding, not a want of cash: buildFor() borrows nothing for it. |
| 15197 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |
| 15725 | `Game.RECORD_BACKLOG` | `6` | The most recording writes the writer may be behind by (months and histories not yet on disk): the clock then records nothing that month, and a skip waits. |
| 15728 | `Game.RECORD_WAIT_MS` | `30_000` | The longest a skip waits for the writer to get inside RECORD_BACKLOG, in milliseconds, before it records nothing that month: a writer that cannot write must not hold the skip for ever. |

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
| 1065 | `private String loadFailure` | Why the last load did not happen, or null. |
| 1167 | `private Founding founding` |  |
| 1192 | `private transient Nations nations` | The nations on this city's path, built the first time getNations() is asked and again when the founding's path or seed differs (not saved: the path and seed are, in the founding). |
| 1230 | `private transient volatile WorldMarket worldMarket` | The market the city trades in, or null: built where a world file is there, the first time the month (or a load) needs it. |
| 1233 | `private transient boolean worldMarketTried` | Whether the market has been looked for in this city, so a file that will not open is said once and not every month. |
| 1236 | `private transient boolean onLiterals` | Whether this city plays on the goods' literals whatever world file is there (playOnLiterals()). |
| 1259 | `private transient volatile PartnerTrade partnerTrade` | The city's trade by partner, built with the market; null with none. |
| 1460 | `private transient volatile WorldMoneys worldMoneys` | The world's moneys against the US$, built with the market (null where it is null) and stepped in the month; not saved itself: DataSave.worldMoneys is its state. |
| 1463 | `private transient boolean worldRiskOffStarted` | What the month's step found, for the log after the reprice: whether a risk-off began, the yen's, the franc's and the Australian dollar's moves against the US$ in it, and the lines of the world pegs that broke. |
| 1464 | `private transient double[] worldRiskOffMoves` |  |
| 1465 | `private transient List<String> worldPegBreaks` |  |
| 1474 | `private transient volatile PolicyRates policyRates` | The eight central banks' policy rates over the world's moneys (XR4), built with them and stepped in the month; not saved itself: DataSave.policyRates is its state. |
| 1480 | `private transient boolean worldQuietForTheD` | Whether the world's moneys are kept from the D$ in this city (quietTheWorldForTheD()). |
| 1909 | `private CityMap cityMap` | The city map, or null until it is asked for. |
| 1912 | `private int mapFailures` | How many months the map failed to keep up and was dropped (each one logged): what a harness asserts is none. |
| 1929 | `private boolean mapAtSeaStale` | Whether the map's oil at sea is to be handed over again before it is next read (0.7.97): set by a draw and a load. |
| 2040 | `private int mapGeneration` | Bumped whenever the city's map is thrown away (a new city, a load): a draft from before it is another city's. |
| 2044 | `private final Game game` |  |
| 2045 | `private final int generation` |  |
| 2046 | `private final CityLand land` |  |
| 2047 | `private final java.util.Map<Resource, double[]> remaining` |  |
| 2048 | `private final BuildingVisual.Type[] types` |  |
| 2049 | `private final long[] counts` |  |
| 2051 | `private final int month` | The game's month when the draft was made: what the map's logs date the deal by (CL3). |
| 2053 | `private final Landmasses.Picture landmasses` | The landmasses as they stood (ET5): what the map is measured under; null for a city on one landmass. |
| 2055 | `private final java.util.List<CityMap.BridgeRun> bridges` | The long bridges standing when it was copied (ET6), null for none. |
| 2056 | `private volatile CityMap map` |  |
| 2189 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 2260 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 2261 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 2486 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 2734 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under ON OFFER (under the toggle until 0.7.26), and a short vault says here that the rest was converted. |
| 2735 | `private int lastLandReceiptMonth` |  |
| 2840 | `private BusinessInvestment.OrderWatch orderWatch` | Harnesses only (0.7.54): what every order search decides is told here as well - see BusinessInvestment.OrderWatch. |
| 2885 | `private boolean monthRun` | WHETHER A MONTH HAS RUN SINCE THE CITY WAS FOUNDED OR LOADED (0.7.102). |
| 3540 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 3549 | `private double carriedTransitBill` | ...and the month's transit bill as 6d struck it (0.7.49), applied inside the rebuild for the same reason: the rebuild would strike it at the fill the month ended on, not the one it was paid at. |
| 3560 | `private double carriedCaptiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, because the cells are not back when the road ratio is first struck (carriedCarOwnership's reason), and a j... |
| 3563 | `private double[] carriedFuelMonth` | ...and the drivers' fuel as 6d drew it (0.7.62): bill, imported part, litres - DataSave.householdFuel; null for a save from before it. |
| 3566 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 3585 | `private final double[] rowFeesSettled` | Each row's fees as the last month's waterfall was handed them: the clinic's, the schools', the fares and the fuel (0.7.49). |
| 3613 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 3891 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 4232 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 5374 | `private int wellsWornOut` | Wells retired worn out over the run (0.7.84): a count for the playtest, not saved. |
| 5401 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 5491 | `private int constructionShedMonth` |  |
| 5492 | `private double constructionShedPoints` |  |
| 5551 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 5563 | `private final java.util.Map<String, String> landBlockedNeeds` | The ground each waiting sector asked for this month (PT1-M2): sector key to "needs 743 m², 301 m² free", the numbers its own refusal gave, kept beside landBlockedSectors and cleared with it, so the inbox's yearly noti... |
| 5584 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 5604 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 5621 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 6022 | `final BusinessInvestment.Decision decision` |  |
| 6023 | `final double cash, perUnitProfit` |  |
| 6024 | `final BusinessDebtManager credit` |  |
| 6025 | `final boolean banned` |  |
| 6026 | `boolean atPrime` |  |
| 6027 | `double firstRate` |  |
| 6028 | `int deskCalls` |  |
| 6168 | `private ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate` | What consider() tests an order on (0.7.113): the amount the interest is struck on, as sectors.SpreadPlanner's money gate is one replaceable piece - ON_ITS_WHOLE_COST, Jerus's decision A, unless a check or a probe set ... |
| 6417 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 6418 | `private final java.util.Set<String> heldForDownPayment` |  |
| 6499 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 6500 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 6501 | `private double salvageUsedThisMonth` |  |
| 6559 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 6697 | `private double bankedClearedAtLoad` | Construction points the last load cleared from stacks that carried them past what they owed (0.7.17; see the load path and BuildingManager.clearBankedProgress()). |
| 7015 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 7020 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 7210 | `public final int quantity` |  |
| 7211 | `public final double sticker` |  |
| 7212 | `public final double materialsNeeded` |  |
| 7214 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 7216 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 7217 | `public final double plantPrice` |  |
| 7218 | `public final double plantCost` |  |
| 7219 | `public final double materialsImported` |  |
| 7220 | `public final double materialsPrice` |  |
| 7221 | `public final double importCost` |  |
| 7227 | `public final double salesTax` | The sales tax in the price (0.7.19): what the builders remit on it, less what they claim back on the plant's material - passed on, as a shop's shelf price passes it on. |
| 7234 | `public final double allowance` | What the price allows for the material beyond the yard (0.7.19): the plant's and the world's at today's prices, net of the credit on the plant's and with the builders' tax on it - the part of the total an escalation c... |
| 7235 | `public final double total` |  |
| 7236 | `public final double landNeeded` |  |
| 7237 | `public final double landFree` |  |
| 7243 | `public final double months` | Months to finish at this month's shares of the site output, the planners' own reading (quoteMonths()), or NaN when the builders have no site output. |
| 7255 | `public final double bitumenNeeded, bitumenFromRefiners, bitumenImported, bitumenLocalCost, bitumenImportCos...` | The bitumen the order's paving takes (0.7.83, batch O6; spec-oil 2.5), in tonnes - none for anything but a paved road, a highway and a paving (Game.bitumenFor()) - what of it the refiners' tanks would sell and what th... |
| 7265 | `public final double prepSqFt, prepPoints, prepCost` | The foothill the order has to prepare (ET2; SitePreparation, LandManager.foothillToPrepareSqFt()), square feet; the construction points that is, owed by the order's site before its first building finishes; and its mon... |
| 7276 | `public final double worksSqFt, worksPoints, worksCost` | The cuts, viaducts and tunnels a highway or a railway order needs (ET4; RunWorks, spec-earth2-terrain 4.3): the run's land the works are priced on, square feet; the construction points they are, owed by the order's si... |
| 7277 | `public final double[] worksMix` |  |
| 7286 | `public final double bridgeLengthM, bridgePoints, bridgeCost` | The span of a long bridge order (ET6; BridgeWorks, spec-earth2-terrain 6.4): the crossing's length, metres; the construction points its deck is, owed by the order's site before its first bridge finishes, as a pad's an... |
| 7295 | `public final double deepWaterDepthM, deepWaterPoints, deepWaterCost` | The deep-water works of a floating platform or its well (DW1; Game.deepWater()): the depth of the sea the order's first unit stands in, metres (every unit of the order is priced at it); the construction points of the ... |
| 7473 | `private boolean contractsToInfer` | Set by the load path when a save carries no payers, and read once the land price is back. |
| 7488 | `private final BuildingManager.BuildersWages buildersWages` | What the builders' crews cost a point, today and at founding. |
| 7683 | `private int preparationOrders` | The foothill preparation billed over this game's life (ET2, SlopeCheck 9 and LongPlaytest's summary): the orders that carried one, the points they owed, and their money before the builders' tax (BuildQuote.prepCost). |
| 7684 | `private double preparationPointsBilled, preparationMoneyBilled` |  |
| 7739 | `private int runWorksOrders` | The cuts, viaducts and tunnels billed over this game's life (ET4, RunWorksCheck 10 and LongPlaytest's summary): the highway and railway orders that carried works, the points they owed, and their money before the build... |
| 7740 | `private double runWorksPointsBilled, runWorksMoneyBilled` |  |
| 7801 | `private double bitumenTonnes, bitumenCost, bitumenImported` | The bitumen drawn for roads since this game was founded or loaded, in tonnes, what it cost the builders and the part bought abroad (0.7.83): running totals for the checks and the playtest, never reset and not saved - ... |
| 7954 | `private double siteDrawBillable` | What the month's site draw cost the builders, as they bill it on (billableMaterial()): what the owners' escalation is struck against. |
| 8215 | `private String lastHandRefusal` | Why a demolition or a buy-out was not made, or null when it was. |
| 8633 | `private ConstructionControl.Events controlThisMonth` | This month's events, kept for the screens and the inbox: what the advance left, settled. |
| 8635 | `private double overtimePaidThisMonth` | What the treasury paid the builders this month for overtime, with their tax in it. |
| 8637 | `private double demolitionSalvageThisMonth` | What a finished demolition's material fetched this month, from the builders. |
| 9256 | `private double freshRights` | Units a month of fresh water the city may treat past what its lakes and river yield: an older city's (0.7.59). |
| 9361 | `private BridgeSurvey.Ground bridgeGroundForTest` |  |
| 9362 | `private BridgeSurvey.Result bridgeKept` |  |
| 9363 | `private int bridgeKeptFar, bridgeKeptVersion` |  |
| 9364 | `private long bridgeKeptStamp` |  |
| 9365 | `private CityLand bridgeKeptLand` |  |
| 9366 | `private BridgeSurvey.Ground bridgeKeptTest` |  |
| 9749 | `private final Investor government` | The city itself, as a payer. |
| 10420 | `private String saleHow` | How the sale being booked reads on the piece it makes or joins (Debt.getSales(), CD6): "auction", or "the rollover" while the treasury's rollover sells what falls due (issueForRollover()). |
| 11430 | `private final Rollover rollover` |  |
| 11433 | `private final java.util.List<String> rolloverShortNotes` | What the month's rollover sold short and did not raise (CD2), in words, for the inbox; the record of a month that has finished, not saved. |
| 11439 | `private final java.util.List<String> rolloverTailNotes` | What the month's rollover paid a tail past the default cap for (D29, Filled or capped higher), in words, for the inbox; not saved. |
| 11449 | `private final java.util.List<TimeSkipReport.AutoSale> autoSalesThisMonth` | Every automatic sale this month - the rollover's issues and automatic building's bonds - for the time skip's report (CD2, slice 6): a record of a month that has finished, cleared at the top of the next, not saved. |
| 13512 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 13513 | `private LandManager landManager` |  |
| 13517 | `private boolean loadingSave` | True while loadGameSave() runs initialize(), which then founds no land: the save brings its own (0.7.57). |
| 13520 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 13521 | `private BuildLog buildLog` |  |
| 13548 | `private PopulationCohorts cohorts` |  |
| 13549 | `private FamilyModel families` |  |
| 13557 | `private Migration migration` |  |
| 13563 | `private LabourMarket labourMarket` | What labour costs. |
| 13569 | `private final WantedPosts wantedPosts` | The posts the staffing gate refused for want of workers that the wage is priced on as if they were offered (LD1): see WantedPosts, and noteStaffingHold(), settleWantedPosts() and repriceLabour() below. |
| 13575 | `private Education education` | The schools. |
| 13584 | `private Health health` | How much of the workforce is off sick. |
| 13592 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 14146 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 14292 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 14299 | `private HouseholdAccounts households` | The residents' own books. |
| 14308 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 14318 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 14322 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 14330 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 14334 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 14424 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 14430 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 14507 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 14517 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 14523 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 14530 | `private final Equity equity` | Who owns the city's companies. |
| 14534 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 14542 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 14552 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 14559 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 14581 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 14599 | `private final Expectations expectations` |  |
| 14746 | `private final Consumption consumption` |  |
| 14747 | `private boolean consumptionLoaded` |  |
| 14756 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 14774 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 14783 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 14784 | `private int rateHistoryFilled` |  |
| 14888 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 14904 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 14948 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 14961 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 14975 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 14993 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 14996 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 14998 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 14999 | `private double carriedRetailCapacity` |  |
| 15000 | `private double carriedRetailWant` |  |
| 15003 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 15004 | `private java.util.Map<String, String> lastInvestment` |  |
| 15007 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 15068 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 15199 | `private int monthsSinceAutosave` |  |
| 15272 | `private final java.util.concurrent.ConcurrentLinkedQueue<GameFiles.Result> savesWritten` | Results the writer's thread has finished, waiting for the FX thread to take them: see reportSaves(). |
| 15276 | `private java.util.function.LongSupplier saveClock` | The clock the floor is measured on (nanoseconds); checks give a city their own. |
| 15279 | `private long lastAutosaveAt` | When the last autosave was asked for, on saveClock; meaningless until autosavedYet. |
| 15281 | `private boolean autosavedYet` |  |
| 15731 | `private String recordingId` | The recording's id, which names its folder under recordings/; null in a city that never recorded. |
| 15733 | `private boolean recording` | Whether "Record this city" is on. |
| 15735 | `private boolean recordedSinceLoad` | False from a load or from switching on until the first month is written: that write drops the months past it (the rewind) and takes the history with it. |
| 15737 | `private boolean replayCopy` | True for the throwaway copy a replay shows (markReplayCopy): it never records and never writes the player's folder. |
| 15739 | `private boolean skipping` | True while simulateMonths() runs its months: the recorder then waits for the writer rather than skipping a month. |
| 15741 | `private long recordingStartedAt` | When the recording was started, in epoch milliseconds (its meta); 0 in a city loaded from a save, whose meta is on disk already. |
| 15743 | `private final java.util.Set<Path> recordingsWaiting` | The files the recorder has handed the writer that are not yet written, by target: a task replaced while it waited is counted once, and its replacement removes it. |
| 15744 | `private final Object recordingRoom` |  |
| 15746 | `private int monthsNotRecorded` | Months the clock did not record because the writer was behind, since the city was built or loaded. |
| 15748 | `private boolean inUnrecordedRun` | True while the clock is in a run of unrecorded months: the first of a run prints one line. |
| 15750 | `private volatile long lastRecordNanos` | What the last recorded month cost the FX thread: the fill, the snapshot and the hand-over, in nanoseconds. |
| 15907 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 15921 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 16057 | `private double foreignDebtRaisedThisMonth` |  |
| 16058 | `private double foreignPrincipalRepaidThisMonth` |  |
| 16059 | `private double foreignInterestPaidThisMonth` |  |
| 16147 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 16167 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 16168 | `private double cityPrincipalRepaidThisMonth` |  |
| 16182 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 16183 | `private double cityDiscountForBank` |  |
| 16186 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 16203 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 16209 | `private double bankPrincipalRepaidThisMonth` |  |
| 16247 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 16256 | `private double couponsToFirms, principalToFirms, firmsBoughtPaper` | ...and the businesses' (CD2, slice 4): the same three figures, for the playtest and the harness. |
| 16266 | `private double couponsToWorld, principalToWorld, worldBoughtPaper, paperBoughtFromWorld` | ...and the world's (CD2, slice 5), which are declared: its coupon (an income debit) and its principal (a financial outflow) leave the pools for abroad, what it paid at the settle (worldBoughtPaper) is a financial infl... |
| 16451 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 16453 | `boolean restrikeOffForTest` | HoldersCheck's twin (BK1): true books no re-strike, as main did, so the harness can hold the two cities' figures side by side. |
| 16953 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 16956 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 17128 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the world (the holders of a dollar bond among them), carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape ... |
| 17130 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 17183 | `private double treasuryOpening` |  |
| 17184 | `private double treasuryClosing` |  |
| 17185 | `private double treasuryRaised` |  |
| 17186 | `private double treasuryRepaid` |  |
| 17187 | `private double treasurySurplus` |  |
| 17188 | `private boolean treasuryRecorded` |  |
| 17207 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 17219 | `private double retiredGainSoFar` | The gain (below nothing, a loss) on paper the treasury has retired since the last strike, in local money - the carrying value less the price paid, counted as each buyback happens (repurchaseDebt()), press to press lik... |
| 17230 | `private double treasuryNotCash` | The part of the month's budget balance that is not cash (CD3): the discount amortised, which the budget charges and the treasury never pays, less the gain on paper retired, which the budget books and the treasury neve... |
| 17233 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 17456 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 17459 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 17467 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 17468 | `private String postAuditDriftPool` |  |
| 17469 | `private double postAuditDriftWorst` |  |
| 17533 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 17536 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 17539 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 17547 | `private final java.util.Map<String, Double> arrearsPaidTo` | This month's arrears paid down, by the sector whose till they reached (0.7.55): what its statement's arrears line reads (SectorBooks, arrearsPaid), struck at the settle and read when the month is recorded, both inside... |
| 18196 | `private long pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 20297 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 20349 | `private final Denomination denomination` |  |
| 20627 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 20610 | **type** `public class Game` |  |
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
| 280 | 418 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 699 | 5 | `public void run()` |  |
| 705 | 40 | `private void initialize()` |  |
| 748 | 4 | `static { ... }` |  |
| 756 | 3 | `public void newGame()` | A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. |
| 765 | 41 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 836 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 920 | 12 | `private void foundingYard()` | The city opens with the builders' yard already standing (PT1-M4). |
| 933 | 8 | `public void resumeGame()` |  |
| 941 | 72 | `public void loadGameSave(int slot)` |  |
| 1022 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 1030 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 1045 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 1047 | 6 | `public void toggleQuit()` |  |
| 1059 | 4 | `public void saveForQuit()` | The quit's autosave (UP1): asked for, and on disk when this returns - System.exit does not wait for the writer's thread, so quitting waits for it here. |
| 1067 | 1 | `public String getLoadFailure()` |  |
| 1069 | 7 | `public void toggleGraphs()` |  |
| 1076 | 5 | `public void toggleReports()` |  |
| 1082 | 3 | `public void toggleNextMonth()` |  |
| 1087 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 1089 | 3 | `public int getMonth()` |  |
| 1092 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 1095-1151)

### THE FOUNDING RECORD (0.7.10) (lines 1152-1193)

| line | len | member | says |
|---:|---:|---|---|
| 1170 | 3 | `public Founding getFounding()` | How this city was founded. |
| 1175 | 15 | `public synchronized Nations getNations()` | The nations beyond the city on this founding's path and seed, from the game's world file; null where the file is not here. |

### THE WORLD MARKET (WN3, 2026-10-28; docs/studies/spec-nations.md 4, 6 and D28) (lines 1194-1421)

| line | len | member | says |
|---:|---:|---|---|
| 1244 | 10 | `public void playOnLiterals()` | This city plays on the goods' literals, as a city with no world file does, whatever file is there: no market is built or restored, and the markets keep the literal band (WN3). |
| 1256 | 1 | `public boolean isOnLiterals()` | Whether playOnLiterals() was called. |
| 1262 | 1 | `public WorldMarket getWorldMarket()` | The world market of this city, or null where there is no world file (the city then plays on the goods' literals) or it has not been built yet: the first month builds it, a load builds it. |
| 1265 | 4 | `public PartnerTrade.Snapshot getPartnerTrade()` | The city's trade by partner as of the last month tallied - a whole, immutable snapshot, safe from any thread; null with no market. |
| 1271 | 4 | `public TradeLanes.Feed getTradeFeed()` | The map's feed over the city's trade by partner: last month's and the year to date (the snapshot is the feed); TradeLanes.Feed.NONE with no market. |
| 1277 | 1 | `PartnerTrade partnerTradeForTest()` | For a harness: the live record behind the snapshot (its history, its state), or null. |
| 1280 | 4 | `private boolean hasBerth()` | Whether any sea terminal stands: the berth the market gives the city a sea leg by. |
| 1290 | 44 | `private WorldMarket ensureWorldMarket()` | The market of this city, built the first time it is asked (once a city): over the world file, the nations on this founding's path and seed, the city at its site - an Earth city at its spot, a generated world's at the ... |
| 1339 | 26 | `private void strikeTheWorldMarket()` | Steps 1 and 2 of the month (startOfMonthUpdate(), right after the world's price level and the exchange rate): the January solve when the year has turned, and the band of every good from last month's trade. |
| 1371 | 25 | `private void tallyThePartners()` | Step 3: the month's trade with the world split to the nations (PartnerTrade.tally()), from the month's own fills - the clearing's and every draw, with the world as the other side - at the prices they were struck at, i... |
| 1403 | 18 | `private void restoreWorldMarket(DataSave loaded)` | A load's half of the market (the load path, after the buildings are back): built where a file is there, the berth read, the solved state put back as saved (WorldMarket.restore() - not solved again), and the trade by p... |

### THE WORLD'S MONEYS IN THE MONTH (WN4, 2026-10-30; docs/studies/spec-exchange-rates.md 3.5 and 3.7) (lines 1422-1820)

| line | len | member | says |
|---:|---:|---|---|
| 1471 | 1 | `public WorldMoneys getWorldMoneys()` | The world's moneys of this city, or null where there is no world file or the market was not built (the D$ then feels no world). |
| 1477 | 1 | `public PolicyRates getPolicyRates()` | The policy rates of the eight central banks, or null where the world's moneys are null. |
| 1488 | 4 | `void quietTheWorldForTheD()` | The D$ of this city is not reached by the world: it is handed zeros and no terms of trade, so it moves to the bit as it did before the world's moneys (WN4), while the moneys are built, stepped, saved and charted as ever. |
| 1494 | 10 | `private WorldMoneys buildWorldMoneys(Path file, Nations beyond, WorldMarket.Base base, int nationCount)` | The moneys over the world file and the nations of this founding's path, with each nation's commodity share of exports from the market's base; null, and a line, if they will not build. |
| 1506 | 9 | `private PolicyRates buildPolicyRates(WorldMoneys moneys)` | The eight central banks' policy rates over these moneys, standing in the first January (Nations.FIRST_YEAR); null, and a line, if they will not build (the city then reads the constant world rate). |
| 1521 | 8 | `private void handTheUsRate()` | THE US'S RATE TO THE CITY (XR4): the US's policy rate is the world's price of money to the DebtManager (worldRate()); the constant (DebtManager.WORLD_BASE_RATE) where there are no policy rates, or where the city's wor... |
| 1531 | 3 | `private static double valueMove(double before, double after)` | A money's value against the US$ in a month, a fraction: its rate before over its rate after, less one (a rate is units to the dollar, so a falling rate is a rising money). |
| 1542 | 43 | `private void stepTheWorldMoneys(WorldMarket market, boolean solved, int year)` | Steps the moneys to the month the city is in (strikeTheWorldMarket(), after the January solve): each month in turn, handing the market's terms of trade in the January it solved and null in every other month. |
| 1587 | 5 | `private void stepThePolicyRates(int target)` | Steps the eight banks' policy rates, each month in turn, to month index target (WorldMoneys.monthIndex); none ahead of it are stepped back. |
| 1598 | 21 | `private void handTheMoneysToTheVault()` | THE MONEYS TO THE VAULT (WN4, right after the step, before anything buys or sells): the eight's US$ per unit - 1 / rate(code), NaN for a money with no rate this month - and the city's trade by money, for the reserve m... |
| 1621 | 4 | `private double worldRiskOffLevel()` | The risk-off level the carry and the hot money are told this month (XR4): the moneys' own, 0 with no moneys or a world quieted for the D$. |
| 1630 | 5 | `private double[] tradeSharesByMoney(WorldMoneys moneys)` | The city's trade by money (MATCH_TRADE): each partner nation's exports plus imports with the city over the last finished year (PartnerTrade's history), else the year to date, else none - all US$ - mapped to the eight ... |
| 1641 | 27 | `private double[] tradeByNation()` | The city's trade with each nation: exports plus imports over the last finished year (PartnerTrade's history), else the year to date, else nothing (all zero); weight[n] for nation n, 1-based, index 0 unused, in thousan... |
| 1673 | 11 | `static double[] tradeSharesByMoney(WorldMoneys moneys, Nations beyond, int year, double[] weight)` | The eight's shares of a trade given by nation (weight[n] for nation n, 1-based, index 0 unused), summing to 1 - or all zero with no weight: each nation's money mapped to the eight by eightOf(). |
| 1690 | 9 | `static int eightOf(WorldMoneys moneys, String code)` | Which of the eight a money stands in for in the city's trade: itself if it is one of them; else the money it is pegged to or uses, followed through the chain (WorldMoneys.pegAnchor) if that is one of them; else the US... |
| 1707 | 24 | `private double cityExportPriceLevel(int year)` | The city's export-weighted ln world price of what it sells, each good against its normal (WN4, the D$'s terms of trade): for each good, its share of the value the city sold abroad in the last finished year (PartnerTra... |
| 1733 | 4 | `private static int goodNumber(Nations beyond, Good good)` | The number the file gives a good (1..goodCount(): its key is the Good constant's name), 0 for one the file does not price. |
| 1742 | 5 | `private void handTheWorldToTheD()` | What the world is this month, handed to the D$ right before its reprice: the moneys' dollar cycle and risk-off level and the hot money held; zeros with no moneys, and then the D$ is what it was. |
| 1749 | 17 | `private void noteTheWorldMoneys(double rateBeforeReprice)` | The log's lines about the world, after the reprice: money running to safety abroad (with where the D$ went in it) and the world pegs that broke. |
| 1771 | 18 | `private void restoreWorldMoneys(DataSave loaded)` | A load's half of the moneys (restoreWorldMarket()): the saved state put back where it fits this world file, else the moneys stand at the first January (Nations.FIRST_YEAR) and are caught up to the city's month with no... |
| 1795 | 25 | `private void restorePolicyRates(DataSave loaded)` | A load's half of the policy rates (restoreWorldMarket(), after the moneys): the saved state put back where it fits, else the rates are rebuilt by stepping from the first January (Nations.FIRST_YEAR) to the city's mont... |

### THE REAL US DOLLAR (WN4, D29; RealDollar, docs/studies/spec-exchange-rates.md 3.9) (lines 1821-1888)

| line | len | member | says |
|---:|---:|---|---|
| 1836 | 3 | `public double dollarsPer2015Usd()` | What a real 2015 US$ costs in D$ now: the rate (D$ per US$) times the US$'s price level since founding (RealDollar.dollarsPer2015Usd); the rate at founding. |
| 1844 | 4 | `public double realGdp2015Usd()` | The last finished month's GDP in real 2015 US$: the month's GDP over the city's price index (YearBook.realGdp's division) over the founding's rate in today's money (RealDollar.realGdp2015Usd). |
| 1855 | 19 | `public double realEffectiveRate()` | The D$'s real effective rate, 100 at founding (RealDollar.realEffectiveRate): the weights are the city's trade with each nation over the last finished year, else the year to date, else the US alone. |
| 1876 | 1 | `public long getWorldSeed()` | The seed of the world this city stands on (0.7.56): chosen at founding, or derived from an older save. |
| 1884 | 1 | `public World getWorld()` | The world this city stands on (0.7.56): its terrain, its founding site and river, its fields of ore and oil. |
| 1887 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and every purchase since, whole blocks of a grid since 0.7.67 (CityLand). |

### THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) (lines 1889-2018)

| line | len | member | says |
|---:|---:|---|---|
| 1915 | 12 | `public CityMap getCityMap()` | The city map: drawn canonically the first time it is asked for, then kept up month by month. |
| 1942 | 49 | `CityMap.AtSea mapAtSea()` | The oil at sea as the map draws it (0.7.97, batch O13; CityMap's THE OIL AT SEA, docs/studies/spec-oil.md 2.12): each platform's jacket in the middle of the sea sites its slots hold on its field - the platforms on a f... |
| 2003 | 6 | `CityMap.RailPicture mapRail()` | The railway as the map draws its yard (0.8.1, batch FR0; CityMap's railPicture, spec-freight 4.3): whether the railway carries goods, and the share of its track's wagon sets the fleet has standing. |
| 2011 | 4 | `private static Deposit seaFieldOf(List<LandManager.SeaField> fields, int cell, int index)` | A sea field in the list by its world cell and index, or null. |
| 2017 | 1 | `public boolean hasCityMap()` | Whether the city map has been drawn: a city never asked for it has none, and its months pay nothing for it. |

### THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) (lines 2019-2201)

| line | len | member | says |
|---:|---:|---|---|
| 2043 | 41 | **type** `public static final class MapDraft` | A map to be drawn away from the screen's thread: the city's land, remains and counts as they stood, copied. |
| 2058 | 11 | `private MapDraft(Game game)` _(in Game.MapDraft)_ |  |
| 2071 | 3 | `public void draw()` _(in Game.MapDraft)_ | Draws the map on the copy: any thread, once. |
| 2076 | 1 | `public boolean drawn()` _(in Game.MapDraft)_ | Whether it has been drawn. |
| 2079 | 1 | `CityMap map()` _(in Game.MapDraft)_ | The map drawn, or null: a harness's (MapCheck 7). |
| 2082 | 1 | `public Game game()` _(in Game.MapDraft)_ | The game it was taken from. |
| 2086 | 3 | `public MapDraft mapDraft()` | A draft of the city's map, for drawing away from the screen's thread; null when the map is drawn already. |
| 2097 | 23 | `public boolean adoptMap(MapDraft draft)` | Keeps a drawn draft as the city's map, bound to its own land and kept up to the month (reconcile()), and says whether it is the city's map now. |
| 2122 | 1 | `public int getMapFailures()` | How many months the map failed to keep up and was dropped. |
| 2125 | 1 | `public BuildingVisual.Type[] getMapTypes()` | The building types as the map draws them, by id. |
| 2128 | 7 | `public long[] getMapCounts()` | Every type's standing count, by id: what the map's districts sum to. |
| 2137 | 4 | `private CityMap drawMap()` | The map drawn canonically from the city as it stands. |
| 2149 | 17 | `private void reconcileMap()` | The month's change placed on the map (CityMap.reconcile()), after the month's construction and demolitions; the land drawn again (a restatement) draws the map again. |
| 2168 | 1 | `public String getCityName()` | The city's name. |
| 2171 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 2174 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 2177 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 2180 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 2198 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset until 0.7.20, when the screen stopped saying what the money buys; NewGameChec... |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 2202-2224)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 2225-2447)

| line | len | member | says |
|---:|---:|---|---|
| 2263 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 2264 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 2266 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 2267 | 9 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 2278 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 2279 | 1 | `public double getSubsidyPaid(String key)` |  |
| 2281 | 5 | `public double getTotalSubsidyPaid()` |  |
| 2288 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 2301 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 2304 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 2323 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 2333 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 2368 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 2385 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 2401 | 5 | `public double getIncome()` | EARNED (0.7.31's name for it): the tax take less the running programmes, plus the utilities' net, at today's dials - the header's "+$X earned a month". |
| 2407 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 2411 | 3 | `public double getWaterRatio()` |  |
| 2415 | 3 | `public double getRoadRatio()` |  |
| 2420 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 2425 | 3 | `public Markets getMarkets()` | Every goods market. |
| 2430 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 2434 | 3 | `public LandManager getLandManager()` |  |
| 2439 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 2448-2588)

| line | len | member | says |
|---:|---:|---|---|
| 2489 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 2492 | 6 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 2507 | 53 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 2562 | 3 | `private static String usdWords(double thousands)` | Thousands of US dollars as the screens write them: "US$101.8M" (Formats.amount() with the dollar's mark). |
| 2567 | 3 | `private static String localWords(String here, double thousands)` | ...and thousands of local money, with its own mark: "D$72.0M". |
| 2578 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 2585 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's price by (red when not, since 0.7.26 the only verdict on it); since 0.7.13 its button asks land... |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 2589-2746)

| line | len | member | says |
|---:|---:|---|---|
| 2622 | 5 | `public java.util.List<LandParcel> landShelf()` | The offers standing in the land office's order: cheapest ground first, per square foot of dry ground in US dollars (0.7.57: all forty) - the top-left card first. |
| 2629 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 2639 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 2649 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 2664 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 2669 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 2674 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 2686 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 2692 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 2697 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 2707 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 2738 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 2743 | 3 | `public java.util.List<LandParcel> getLandListing()` | The offers standing. |

### THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) (lines 2747-2894)

| line | len | member | says |
|---:|---:|---|---|
| 2781 | 17 | **type** `public record LandNeed(Kind kind, double drySqFt, Resource resource)` | What the city needs ground for: room in general, a shortfall of dry ground, a resource's deposit, or a coast. |
| 2784 | 1 | **type** `public enum Kind` _(in Game.LandNeed)_ | The four needs. |
| 2787 | 1 | `public static LandNeed room()` _(in Game.LandNeed)_ | Room to grow. |
| 2790 | 1 | `public static LandNeed shortfall(double drySqFt)` _(in Game.LandNeed)_ | This much more dry ground than the city has free. |
| 2793 | 1 | `public static LandNeed deposit(Resource resource)` _(in Game.LandNeed)_ | A site of this resource to stand a mine or a well on. |
| 2796 | 1 | `public static LandNeed coast()` _(in Game.LandNeed)_ | Sea, for a desalination plant (batch J2). |
| 2800 | 34 | `public LandParcel bestOffer(LandNeed need)` | The offer that best meets a need, by THE BEST OFFER's rules; null when none does. |
| 2835 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 2843 | 4 | `public void watchOrders(BusinessInvestment.OrderWatch watch)` | Harnesses only: tell this watch every order the three searches decide; null to stop. |
| 2848 | 3 | `public String getLastInvestment(String sector)` |  |
| 2864 | 11 | `private void deferBorrowingCosts()` | WHAT EACH BUSINESS'S BORROWING COST IT UP FRONT THIS MONTH (0.7.102, Jerus's A16): the fees the bank kept out of its loans, its mortgages' insurance premiums and its bonds' issuing costs - the month's own figures, the... |
| 2888 | 1 | `public boolean hasMonthRun()` | True once a month has run since the city was founded or loaded; false until then (0.7.102). |
| 2891 | 3 | `public java.util.Map<String, String> getLastInvestments()` | Every word the investors left last month, by the slot it was filed under (a sector's key, or the bank branch's label) - saved since 0.7.102 (A22); read-only. |

### PRIVATE INVESTMENT (lines 2895-3521)

| line | len | member | says |
|---:|---:|---|---|
| 2905 | 109 | `private void runPrivateInvestment()` |  |
| 3023 | 27 | `void noteStaffingHold(Sector.Staffing held)` | A planner was refused by the staffing gate (LD1; Decision.heldForStaff): sorts the hold for WantedPosts. |
| 3052 | 5 | `void settleWantedPosts()` | The month's asks settled into the posts per band next month's wage is priced on (LD1; WantedPosts.settle()). |
| 3076 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 3089 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 3101 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 3107 | 413 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 3522-3607)

| line | len | member | says |
|---:|---:|---|---|
| 3569 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 3572 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 3575 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 3576 | 1 | `public double getHouseholdCarImports()` |  |
| 3579 | 1 | `public double getHouseholdFuel()` | ...and what they paid for fuel this month (0.7.49): what the cells paid - the refiners' shelf and the world's (0.7.62). |
| 3582 | 1 | `public double getHouseholdFuelImports()` | ...and what of it they paid the world (0.7.62): every litre with no refinery; the money audit's FuelFunded and FuelImports. |
| 3588 | 1 | `public double getRowFeesSettled(int row)` | ...one row's. |
| 3591 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 3594 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 3597 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 3600 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 3603 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 3606 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 3608-3641)

| line | len | member | says |
|---:|---:|---|---|
| 3616 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 3619 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 3622 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 3625 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 3628 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 3631 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 3634 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 3637 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 3640 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 3642-4192)

| line | len | member | says |
|---:|---:|---|---|
| 3668 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 3687 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 3698 | 3 | `public double bankTaxUnder(TaxPolicy p)` | The bank's profit tax under another policy (0.7.36, the Policy spec's M2): Bank.taxAt() at that policy's retail rate, bankProfitTaxRate()'s rule - so NEXT month's bill, the tax being in arrears. |
| 3711 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 3728 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 3736 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 3764 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 3796 | 64 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 3868 | 16 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 3958 | 98 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 4069 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 4082 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 4109 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 4126 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 4153 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 4182 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 4193-4339)

| line | len | member | says |
|---:|---:|---|---|
| 4235 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 4238 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 4243 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 4248 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 4253 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 4280 | 50 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 4332 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 4338 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 4340-4461)

| line | len | member | says |
|---:|---:|---|---|
| 4348 | 12 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 4362 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 4365 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 4368 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 4371 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 4374 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 4384 | 21 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 4407 | 7 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 4427 | 29 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 4458 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 4462-4521)

| line | len | member | says |
|---:|---:|---|---|
| 4465 | 1 | `public double fundSharesValue()` | Its shares, both books, at the city's mark (Exchange.cityMark(): the last trade, fair value once that is a year old). |
| 4467 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 4469 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 4475 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 4477 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 4479 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes, on the bank's share at the city's mark. |
| 4483 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 4485 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 4489 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 4494 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands, at the withdrawal in force: TreasuryFund.withdrawalOn(fundValue()) - transferOn() at the default. |
| 4497 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the city's mark: the Holdings page's line for it until 0.7.39 (FundView reads each book itself now). |
| 4501 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 4505 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 4507 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 4512 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 4516 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 4522-4685)

| line | len | member | says |
|---:|---:|---|---|
| 4525 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 4526 | 7 | `public void setFundDial(double dial)` |  |
| 4535 | 1 | `public double getFundWithdrawal()` | The fund's withdrawal, a share of its whole value a month (0.7.48, C1): TreasuryFund.getWithdrawal(), Norway's rule by default. |
| 4537 | 8 | `public void setFundWithdrawal(double share)` | ...set by the player in whole steps of TreasuryFund.WITHDRAWAL_STEP, from nothing to 10% a month, and recorded. |
| 4547 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 4548 | 8 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 4570 | 18 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, plus the gain on debt retired that the surplus carries and sp... |
| 4594 | 4 | `private static double exchangeFromEnd(double[] exchange, int n, int i)` | The exchange gain (CD7) the history kept for the month at index {@code i} of a series {@code n} long that ends on the same month: the exchange series is read from its end as the retired gain is, 0 for a month it does ... |
| 4605 | 10 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 4622 | 10 | `public double debtExchangeThisYearSoFar()` | CD7: the exchange gain (loss) on the foreign debt that {@link #surplusThisYearSoFar()} leaves out, over the same months (this calendar year's closed ones, in the months the surplus is recorded), a gain positive: what ... |
| 4638 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 4666 | 19 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 4686-4802)

| line | len | member | says |
|---:|---:|---|---|
| 4689 | 11 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 4702 | 11 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 4719 | 1 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 4722 | 8 | `public void fundBuyShares(int company, double money, double limit)` | ...at a price a share the player names (0.7.39, the spec's D2): 0 is fair value, what the rule asks at (it bids TreasuryFund.RULE_PREMIUM over since 0.7.48). |
| 4732 | 1 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 4735 | 8 | `public void fundSellShares(int company, double shares, double limit)` | ...at a price a share the player names (0.7.39): 0 is fair value. |
| 4745 | 1 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 4748 | 6 | `public void fundBuyBond(int bondId, double money, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 4756 | 1 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |
| 4759 | 7 | `public void fundSellBond(int bondId, double face, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 4768 | 3 | `public double fundCashFree()` | The fund's cash a new buy may hold: its cash less what the hand's buys already hold (TreasuryFund.handReserve(), 0.7.39). |
| 4773 | 4 | `private static String shareCount(double n)` | A count of shares in a decision's words: whole from a hundred, else to two places, and four under one - a consolidated holding is a fraction (0.7.39). |
| 4779 | 5 | `private String atLimit(double limit, boolean bond)` | A decision's words for a named price: " at D$101.35k", " at 102.80 per 100"; nothing at fair value. |
| 4793 | 9 | `public boolean fundCancelOrder(int i)` | CANCELS ONE OF THE PLAYER'S ORDERS before the step posts it (0.7.39, the spec's D3): the order at this place in TreasuryFund.getHandOrders(). |

### an Insane founding (lines 4803-4931)

| line | len | member | says |
|---:|---:|---|---|
| 4820 | 8 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar bond (a term loan until CD2), LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.lan... |
| 4860 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 4883 | 1 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 4889 | 13 | `public double buyForeignCurrency(double amount, String code)` | ...into one money of the eight (the Reserves page's money picker; WN4, THE VAULT IN EIGHT); null goes in by the reserve mix, as the one-argument purchase does. |
| 4914 | 1 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 4920 | 11 | `public double sellForeignCurrency(double amount, String code)` | ...out of one money of the eight (WN4, THE VAULT IN EIGHT): the clamp is what that money alone holds, and no other gives up anything; null sells from the largest holding, as the one-argument sale does. |

### WHAT THE D$ IS HELD TO (XR2, 2026-10-22; ForeignAccounts, THE REGIMES) (lines 4932-5009)

| line | len | member | says |
|---:|---:|---|---|
| 4942 | 7 | `public void setCurrencyRegime(ForeignAccounts.Regime regime)` | Chooses how the D$ is held: Float, Lean against falls (the default), Band or Peg. |
| 4951 | 8 | `public void repeg(double rate)` | Moves the peg (a higher rate is a devaluation); nothing outside a peg. |
| 4961 | 9 | `public void setBand(double centre, double slopeAYear, double width)` | Sets the band: its centre, the log a year it crawls and the share either side its edges sit. |
| 4972 | 8 | `public void setEdgePayer(ForeignAccounts.EdgePayer payer)` | Chooses who pays for the dollars the vault buys at the strong edge. |
| 4993 | 16 | `private void settleTheEdge()` | THE STRONG EDGE'S BILL (XR2, star 6), at the top of the month, inside the audit's window, after the redemptions: the dollars the vault took at the last reprice (ForeignAccounts.edgeBuy()) are paid for here, at the loc... |

### THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) (lines 5010-5119)

| line | len | member | says |
|---:|---:|---|---|
| 5037 | 10 | `public double fillReserve(double tonnes)` | Orders crude for the strategic reserve: `tonnes`, cut to the room its tanks have left and to what the treasury could pay at crude's import price (what no tonne costs more than) - bought in the month's crude market and... |
| 5054 | 6 | `public double reserveFillMost()` | The most a fill order would take now (fillReserve()'s cut): the room the reserve's tanks have left, and what the treasury could pay at crude's import price - the reach of the Fill lever on Oil's page (0.7.96, OilView). |
| 5068 | 12 | `public double releaseReserve(double tonnesAMonth)` | Releases the strategic reserve's crude, `tonnesAMonth` a month from the next clearing on, as long as it holds any; nothing stops it. |
| 5088 | 12 | `private void settleReserve()` | The reserve's month, settled at the strike beside the businesses it traded with (StrategicReserve.settle()): the treasury pays for the crude bought - a promise, the crude having landed - and takes in what the refiners... |
| 5102 | 11 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now - and since 0.7.107 what each zone holds and at what share of the price (BusinessInvestment, THE INVESTORS' GROUND BY ZONE). |
| 5115 | 4 | `private String unlinkedClause()` | The clause the "Could not build" words end with while the city owns ground it cannot build on (ET5): "; 12.0 km² is free on Island 2, which no port links"; empty while it owns none. |

### ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's (lines 5120-5198)

| line | len | member | says |
|---:|---:|---|---|
| 5135 | 1 | `public Zoning getZoning()` | The city's zones and its forest law. |
| 5145 | 11 | `public void setZone(String sector, Zoning.Size kind, double size, double priceShare)` | Zones a sector's ground, or re-sizes or re-prices its zone: `size` a share of the city's dry ground (Zoning.Size.SHARE, 0 to 1) or square kilometres (KM2); `priceShare` 0 to 1 of the going business price, what the nex... |
| 5158 | 4 | `static String zoneSizeWords(Zoning.Zone z)` | A zone's size as the log writes it: "2% of the dry ground", "40 km²". |
| 5164 | 3 | `public void removeZone(String sector)` | Un-zones a sector: what its zone held goes back to everyone; what was sold in it stays with its owner at what it paid. |
| 5169 | 9 | `public void setForestLaw(double protectedShare, double keptShare)` | The forest law (Z6): `protectedShare` of the city's forest ground never cut nor built on, `keptShare` - at least that - never built on. |
| 5180 | 8 | `public void setZoneTax(String sector, Zoning.Tax tax, double points)` | A zone's tax row moved (Z5): the sector's own offset, in rate points - annual points for property - exactly as Policy > Taxes moves it (TaxPolicy's setters, their clamps and their log lines). |
| 5190 | 8 | `public double zoneTaxRate(String sector, Zoning.Tax tax)` | ...and what a zone's tax row reads: the sector's effective rate (TaxPolicy.effective*(), the property rate annual). |

### SHRINKING (lines 5199-5482)

| line | len | member | says |
|---:|---:|---|---|
| 5218 | 77 | `private void runRetirement()` |  |
| 5311 | 28 | `private int retireWornOutWells()` | THE WELLS PAST THEIR LIFE (0.7.84, batch O7; spec-oil 2.6): every land well lifting under ten barrels a day (sectors.Oil.wornOut(): 263 months on land) is retired this month, oldest first, by the path any retired buil... |
| 5350 | 22 | `private int decommissionAtSea(ham.citybuildersim.sectors.Oil wells)` | THE OIL AT SEA DECOMMISSIONED (0.7.91, batch O10): once the city's oil is worked out, every platform's jacket that holds no well, and - with no jacket left standing - every kilometre of pipeline, retired by the path a... |
| 5375 | 1 | `public int getWellsWornOut()` |  |
| 5384 | 15 | `private void closeBranches(int wanted)` | Closes branches (0.7.19: as many as the rule says, at once; one at a time from 0.7.11 round 2 until then): the buildings retired by the path any retired building takes (retire()), sold by their owner, retail. |
| 5402 | 1 | `public int getBranchesClosed()` |  |
| 5415 | 67 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 5483-5532)

| line | len | member | says |
|---:|---:|---|---|
| 5503 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 5515 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 5524 | 1 | `public int getConstructionShedMonth()` |  |
| 5525 | 1 | `public double getConstructionShedPoints()` |  |
| 5528 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 5533-5861)

| line | len | member | says |
|---:|---:|---|---|
| 5553 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 5566 | 3 | `public java.util.Map<String, String> getLandBlockedNeeds()` | Sector key to the ground it asked for this month, for every sector in getLandBlockedSectors(). |
| 5571 | 5 | `private void blockOnLand(String sector, String needs)` | A sector waiting on ground, and what it needs: a land refusal's reason less its "no land - " (BusinessInvestment.landReason()). |
| 5586 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 5606 | 8 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 5616 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 5623 | 3 | `public double getLastWriteOff()` |  |
| 5666 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 5676 | 185 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) (lines 5862-6177)

| line | len | member | says |
|---:|---:|---|---|
| 5965 | 4 | `public static int deskCallsMost(int quantity)` | The most times an order of this many may ask the bond desk: the countdown's slices, then doubling down and halving, each at most once a bit of the order. |
| 5976 | 1 | **type** `public record Afford(int quantity, boolean atPrime, double firstRate, int deskCalls)` | What consider() decided of an order (0.7.54): the largest slice that carries its interest, 0 for none; whether any slice would have carried it at prime and its record, for the refusal; the rate at the whole order; and... |
| 5979 | 3 | `Afford sliceFor(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | Harnesses only (InvestCheck, 0.7.113; BuildCardCheck 11, 0.7.122): consider()'s answer to an order with `cash` in the till and `perUnitProfit` a building - the largest slice that carries the interest on its whole cost. |
| 5984 | 6 | `private Afford largestSliceThatCarries(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | The largest slice of the order that carries its interest - see THE LARGEST SLICE, WITHOUT COUNTING TO IT. |
| 5998 | 21 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes)` | The largest n from 1 to quantity that passes, 0 for none, found as THE LARGEST SLICE, WITHOUT COUNTING TO IT says: the countdown's first COUNTDOWN_SLICES, then doubling down, then halving. |
| 6021 | 61 | **type** `private final class Slices` | One order's slices, each asked as the countdown asked it (until 0.7.54, consider()'s loop). |
| 6030 | 6 | `Slices(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` _(in Game.Slices)_ |  |
| 6038 | 14 | `boolean passes(int n)` _(in Game.Slices)_ | Whether n of them carry the interest on their whole cost (0.7.113); what failed at prime and the rate at the whole order are kept for the refusal. |
| 6054 | 4 | `boolean carriesAtPrime(int n, double on)` _(in Game.Slices)_ | Whether n of them would carry the interest on `on` at prime and the sector's record - their whole cost since 0.7.113 (orderGate()). |
| 6060 | 12 | `void askPrimeOfTheRest(int q)` _(in Game.Slices)_ | When none passes: whether any of the q slices would have carried it at prime - every slice to PRIME_SCAN_SLICES, the four that decide it past that, and under a ban the last the till pays for. |
| 6074 | 5 | `void askPrime(int n)` _(in Game.Slices)_ | Whether n of them would have carried it at prime, as the countdown asked it of a slice that failed. |
| 6080 | 1 | `Afford result(int quantity)` _(in Game.Slices)_ |  |
| 6084 | 1 | **type** `private record Judged(double rate, double tested, boolean askedDesk)` | The rate a slice is judged at: the loan's, and the one tested - a bond's blended with the bank's where one is cheaper and raises it all; and whether the bond desk was asked. |
| 6087 | 25 | `private Judged judged(String sector, double borrowed)` | ...for a slice of `sector`'s that borrows `borrowed`. |
| 6124 | 4 | `public boolean carriesItsWholeCost(String sector, double earns, double cost)` | Whether an order by `sector` costing `cost` and earning `earns` a month would carry the interest on its whole cost, as consider() asks each slice (ON ITS WHOLE COST, 0.7.113): it earns something, and at least PROFIT_O... |
| 6141 | 6 | **type** `public record InterestTest(double earns, double cost, double on, double rate, double real, double interest,...` | carriesItsWholeCost()'s question with its figures (0.7.122, batch C1), for the build card's interest gate (BuildCard, INTEREST), so the card says what consider() would and works none of it out itself: what the order e... |
| 6145 | 1 | `public double needs()` _(in Game.InterestTest)_ | What it must earn a month to carry it: PROFIT_OVER_INTEREST times a month's interest, the bar servicesItsOwnDebt() holds it to. |
| 6149 | 10 | `public InterestTest interestTest(String sector, double earns, double cost)` | ...for an order by `sector` costing `cost` and earning `earns` a month, its till and what it holds abroad paying first. |
| 6171 | 1 | `public ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate()` | The rule consider() tests orders by: ON_ITS_WHOLE_COST unless a check or a probe set another. |
| 6174 | 3 | `public void setOrderGate(ham.citybuildersim.sectors.SpreadPlanner.MoneyGate gate)` | A check's or a probe's: tests every order consider() weighs by another rule (the counterfactual); null puts the whole cost back. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 6178-6276)

| line | len | member | says |
|---:|---:|---|---|
| 6235 | 3 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 6240 | 4 | `public boolean buysOnMortgage(String sector, BuildingsTemplate t)` | Whether this sector buys this building on an insured mortgage: a landlord's home (0.7.11) - the rule consider() and financingOf() share. |
| 6257 | 19 | `public double[] financingOf(String sector, BuildingsTemplate t)` | HOW ONE OF THESE WOULD BE PAID FOR (0.7.75, the sector statements' R4): consider()'s split of an order of one, read and never acted on - what it would cost (BusinessInvestment.getCostOf()); what the register would ask... |

### the lender's test for one home, for the build card (I13-L) (lines 6277-6457)

| line | len | member | says |
|---:|---:|---|---|
| 6280 | 2 | **type** `private record LenderReading(java.util.function.IntToDoubleFunction costOf, double insured, double tested, ...` | What the lender reads for an order: what n cost, the insured rate and the real rate it is tested at, one's rent net of carrying it, and Mortgage.decide()'s answer. |
| 6284 | 7 | `private LenderReading lenderReads(BuildingsTemplate t, int quantity, double cash, double perUnitRent)` | The lender's reading of an order of `quantity` with `cash` in hand - the one call the landlords' order (considerOnMortgage()) and the build card (lenderTest()) both make, so they cannot disagree. |
| 6301 | 29 | **type** `public record LenderTest(double cost, double cash, double rent, double noi, double insured, double tested, ...` | THE LENDER'S TEST FOR ONE LANDLORD'S HOME (I13-L; the card's door, as interestTest() is the INTEREST gate's): its cost, the funds the landlord would put in (financingOf(): its till, its money abroad and what its owner... |
| 6305 | 1 | `public boolean passes()` _(in Game.LenderTest)_ | Whether the lender writes it (or the landlord's funds buy it outright). |
| 6308 | 1 | `public boolean outright()` _(in Game.LenderTest)_ | The landlord's own funds cover the whole cost: no lender to ask. |
| 6311 | 1 | `public boolean shortOfDown()` _(in Game.LenderTest)_ | Not even one: the funds do not put the down payment on it. |
| 6314 | 1 | `public double down()` _(in Game.LenderTest)_ | The down payment the lender asks of the landlord's own funds on this cost (Mortgage.ownFundsFor()). |
| 6317 | 1 | `public double loan()` _(in Game.LenderTest)_ | What the lender lends against it: the shortfall the funds leave, once the fee is paid (Mortgage.loanFor()); nothing outright. |
| 6320 | 1 | `public double payment()` _(in Game.LenderTest)_ | The monthly payment the test reads, on that loan with the premium added; nothing outright. |
| 6323 | 3 | `public double coverage()` _(in Game.LenderTest)_ | The rent's cover of that payment (net operating income over it), the figure the lender asks asks() of; NaN where it is not read - outright, or short of the down payment. |
| 6328 | 1 | `public double asks()` _(in Game.LenderTest)_ | What the lender asks of that cover: Mortgage.MORTGAGE_DEBT_COVERAGE. |
| 6332 | 8 | `public LenderTest lenderTest(String sector, BuildingsTemplate t)` | The lender's test for one of this landlord sector's homes, as the order would meet it now; null for a building the sector does not buy on a mortgage (buysOnMortgage()). |
| 6349 | 62 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, of what the planner asked for (Mortgage.decide(), which halves since 0.7.54) - and th... |
| 6420 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 6424 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 6437 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 6458-6538)

| line | len | member | says |
|---:|---:|---|---|
| 6496 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 6504 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 6509 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 6514 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 6517 | 21 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 6539-7409)

| line | len | member | says |
|---:|---:|---|---|
| 6552 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 6569 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 6591 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 6596 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 6699 | 1 | `public double getBankedClearedAtLoad()` |  |
| 6702 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 6724 | 27 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 6760 | 5 | `public int getConstructionOutputAtEveryPost()` | What the builders would do this month with every post offered, at the city's fill (0.7.17): the figure getConstructionOutput() was before the builders laid idle crews off. |
| 6767 | 4 | `public int getBuildingOutputAtEveryPost()` | ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. |
| 6781 | 9 | `void strikeBuildersCrews()` | The builders strike the month's crews (0.7.17): the work ahead - the repairs of the city standing now and every point still owed on site after this month's advance - against what their depots would do with every post ... |
| 6802 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 6819 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 6856 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 6875 | 10 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 6897 | 3 | `double portsBill()` | The sea terminals' repairs, a month: their upkeep, the berths the city keeps as landlord, paid with transit's bill (TreasuryLine.TRANSIT); nothing with no terminal. |
| 6926 | 81 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 7017 | 1 | `public double getCityMaintenancePaid()` |  |
| 7022 | 4 | `public int getConstructionMaterials()` |  |
| 7026 | 4 | `public double getInterestRate()` |  |
| 7030 | 3 | `public boolean isGraphsEnabled()` |  |
| 7033 | 3 | `public boolean isReportsEnabled()` |  |
| 7060 | 101 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 7163 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 7192 | 1 | `public boolean hasNewReceipt()` |  |
| 7193 | 1 | `public void clearReceipt()` |  |
| 7195 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 7209 | 200 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 7297 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 7306 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the bitumen its paving takes, quoted (Markets.quote()), and the refiners' sales tax rate it is credited at (0.7.83). |
| 7315 | 9 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the foothill preparation an order carries (ET2): the sticker already holds prepCost, the money for prepPoints. |
| 7326 | 10 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the works a run's land needs on the city's ground (ET4): the sticker already holds worksCost, the money for worksPoints. |
| 7338 | 12 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the span of a long bridge (ET6): the sticker already holds bridgeCost, the money for bridgePoints. |
| 7352 | 50 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the deep-water works of a floating platform or its well (DW1): the sticker already holds deepWaterCost, the money for deepWaterPoints. |
| 7404 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 7407 | 1 | `public double unitsBeyondYard()` _(in Game.BuildQuote)_ | The material units the crews will draw beyond the yard, which the allowance was priced on. |

### THE BUILDERS' PRICE (0.7.19) (lines 7410-7756)

| line | len | member | says |
|---:|---:|---|---|
| 7466 | 5 | `private String ownerOfOrder(BuildingsTemplate t)` | Who ordered a building of this kind, for a save that did not say (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays for them (BusinessInvestment.planBank()); the city for the rest. |
| 7480 | 6 | `private double recoveredOnOldContract(String payer, BuildingsTemplate t)` | ...and the share of the tax on it that owner gets back, for the same save (revised 0.7.19): on the tax a building of it carries at today's price (EconomyManager.taxRecoveredShare()). |
| 7499 | 18 | `private double depotWageBillPerPoint(boolean today)` | A Construction Depot's posts - the builders' own job mix - at today's wages or at the founding ladder (PayTier, in today's unit), over the points it makes. |
| 7519 | 3 | `private double buildersSalesRate()` | The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. |
| 7522 | 3 | `private double plantSalesRate()` |  |
| 7533 | 5 | `private double billableMaterial(double salvageCost, double plantCost, double importCost)` | What a draw of material cost the builders, as they bill it on (0.7.19): the salvage at what they paid for it, the plant's net of the credit they claim on it, the world's at its landed cost (its tax is charged and cred... |
| 7549 | 3 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 7554 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds)` | ...against a yard holding `yardHolds` units (0.7.40): an order in a run, priced on the yard the orders before it leave (buildRunInvoice()). |
| 7559 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead)` | ...and with `bitumenAhead` tonnes of the refiners' bitumen taken by the orders before it in a run (0.7.83; drawBitumen()). |
| 7564 | 3 | **type** `private record GroundAhead(double ground, double works, double runs, double prepared, java.util.Map<Integer...` | What the orders before one in a run have taken of the ground (ET2): their square feet, the part of it works's (and, ET4, the part runs'), and the foothill their orders prepare (buildRunInvoice()). |
| 7565 | 1 | `GroundAhead(double ground, double works, double runs, double prepared)` _(in Game.GroundAhead)_ |  |
| 7571 | 4 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead, GroundAhead ahead)` | ...and with the ground the orders before it in a run take (ET2): the foothill it prepares is what placing it after them would ask. |
| 7582 | 3 | `public BuildQuote quoteBuildOn(BuildingsTemplate selected, int quantity, int asked)` | What an order for a landmass costs (ET5): the quote of a sea terminal ordered for `asked` - Landmasses.CHOSEN (the card's pick, else the rule), RULE or an id - priced on that landmass's pad and the room it stands on, ... |
| 7586 | 60 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead, GroundAhead ahead, ...` |  |
| 7652 | 4 | `private BuildingsTemplate preparationAnchor()` | The House the foothill's preparation is priced on (ET2; SitePreparation): the template named "House", with points to build it and a founding price. |
| 7661 | 5 | `public double preparationPoints(double sqFt)` | Construction points to prepare `sqFt` square feet of foothill: SitePreparation's dollars a m2 over the House's founding cash cost a point, so the points move with nothing but the work. |
| 7672 | 4 | `public double preparationCost(double points)` | What those points cost the builders' customer, before the builders' tax: the builders' price for house work today - the House's non-material cost over its points (BuildingManager.nonMaterialCost()) - so it keeps up wi... |
| 7686 | 5 | `private void notePreparation(BuildQuote quote)` |  |
| 7693 | 1 | `public int preparationOrders()` | How many orders carried a foothill preparation over this game's life: counted from this game's start, not saved. |
| 7696 | 1 | `public double preparationPointsBilled()` | ...the construction points they owed. |
| 7699 | 1 | `public double preparationMoneyBilled()` | ...and their money, before the builders' tax. |
| 7702 | 1 | **type** `private record RunWorksNeed(double sqFt, double points, double cost, double[] mix)` | What a run's order needs built on the city's slopes (ET4): the land priced, the construction points, their money before the builders' tax, and the shares of {flat, foothill, steep, very steep} they were priced at. |
| 7712 | 14 | `private RunWorksNeed runWorksFor(BuildingsTemplate run, int quantity)` | The cuts, viaducts and tunnels a highway or a railway order needs (ET4; RunWorks, spec-earth2-terrain 4.3): the run's land times the works a m2 needs at the city's own mix of dry ground (RunWorks.usdPerM2OfMix(): the ... |
| 7728 | 4 | `public double runWorksCost(BuildingsTemplate run, int quantity)` | What the cuts, viaducts and tunnels of an order of these would cost, before the builders' tax (runWorksFor()); 0 for every order but a run's on a city with ground above flat. |
| 7742 | 5 | `private void noteRunWorks(BuildQuote quote)` |  |
| 7749 | 1 | `public int runWorksOrders()` | How many orders carried the works of a run over a city's slopes over this game's life: counted from this game's start, not saved. |
| 7752 | 1 | `public double runWorksPointsBilled()` | ...the construction points they owed. |
| 7755 | 1 | `public double runWorksMoneyBilled()` | ...and their money, before the builders' tax. |

### BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) (lines 7757-7955)

| line | len | member | says |
|---:|---:|---|---|
| 7783 | 6 | `public double bitumenFor(BuildingsTemplate t)` | Tonnes of bitumen one of these takes (0.7.83): a road - INFRASTRUCTURE with road capacity - past a Gravel Road's material, its surface's tonnes at BITUMEN_BINDER_SHARE; none for anything else, and none in a catalogue ... |
| 7791 | 3 | `public double bitumenForPaving()` | ...and one gravel road paved (0.7.83): a Paved Road's less a Gravel Road's, which is a Paved Road's - the bed has none. |
| 7796 | 3 | `private double refinersSalesRate()` | The refiners' sales tax rate: the builders' credit on the bitumen they buy from them (0.7.83). |
| 7803 | 1 | `public double getBitumenTonnes()` |  |
| 7804 | 1 | `public double getBitumenCost()` |  |
| 7805 | 1 | `public double getBitumenImported()` |  |
| 7814 | 8 | `public Markets.Draw drawBitumen(double tonnes)` | Draws an order's bitumen (0.7.83): the builders buy it as they buy the material beyond the yard (drawMaterials()), off the refiners' tanks and the rest from the world (Markets.draw()), booked in their ledger and bille... |
| 7832 | 4 | `public double quoteMonths(BuildingsTemplate template, int quantity)` | Months an order would take to finish (0.7.17): the wait every planner reads for its lead time and order size - BuildingManager.waitFor(), the order's points over the share of the site output it would get beside everyt... |
| 7845 | 5 | `public double onSiteMonths(BuildingsTemplate template)` | Months what is on site of one building would take to finish (0.7.20): the same wait at the same output as quoteMonths(), with no order added - BuildingManager.waitOnSite(). |
| 7862 | 4 | `public double siteMonths(String key)` | ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on site would take at today's queue - a stack's by its key, or a demolition's - at the output the quote reads. |
| 7875 | 3 | `public double quoteCityMonths(BuildingsTemplate template, int quantity)` | ...and what a city order of n of these would wait if placed now (0.7.22, after the docs pass): quoteMonths() exactly with no order set and no rush on its site; otherwise where the order would land in the player's orde... |
| 7880 | 4 | `public double quoteCityMonths(BuildingsTemplate template, int quantity, double extraPoints)` | ...with `extraPoints` more owed on its site than its buildings' own (ET2: a foothill pad's preparation, BuildQuote.prepPoints); quoteCityMonths() exactly for none. |
| 7910 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 7938 | 9 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |

### MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) (lines 7956-8049)

| line | len | member | says |
|---:|---:|---|---|
| 7986 | 31 | `public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues)` | Settles the month's site work with the owners who ordered it. |
| 8023 | 7 | `private double deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 8046 | 3 | `public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 8050-8455)

| line | len | member | says |
|---:|---:|---|---|
| 8083 | 3 | `public boolean isCitysToDemolish(BuildingsTemplate t)` | What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). |
| 8088 | 3 | `public boolean isBuyOutable(BuildingsTemplate t)` | What the city may buy out and demolish: a building a sector owns. |
| 8097 | 7 | **type** `public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price, d...` | A demolition, priced and laid out BEFORE the player commits: its work, its price, what it closes, what comes back and when. |
| 8102 | 1 | `public double salvageProceeds()` _(in Game.DemolitionQuote)_ | What the builders would pay for the material today, for as much as their cash covers. |
| 8106 | 8 | **type** `public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground, d...` | A buy-out, priced: the compensation, part by part, and the demolition after it. |
| 8110 | 1 | `public double compensation()` _(in Game.BuyOutQuote)_ | What the owner is paid. |
| 8112 | 1 | `public double total()` _(in Game.BuyOutQuote)_ | ...and what it all costs the city, the demolition included. |
| 8125 | 8 | `private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months)` | D. |
| 8135 | 29 | `public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n)` | What demolishing n standing buildings of this kind would do and cost. |
| 8166 | 16 | `public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell)` | A shell's demolition: DEMOLITION_SHARE of the work it holds, SALVAGE_SHARE of the material it drew to salvage (since 0.7.106; all of it until then), its ground. |
| 8184 | 4 | `private double demolitionMonths(BuildingsTemplate t, int n, double points)` | Months a demolition of these points would take at today's queue, at the output the quote reads. |
| 8197 | 16 | `public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n)` | E. |
| 8216 | 1 | `public String getLastHandRefusal()` |  |
| 8219 | 18 | `public boolean demolish(BuildingsTemplate t, int n)` | D. |
| 8239 | 36 | `public boolean buyOutAndDemolish(BuildingsTemplate t, int n)` | E. |
| 8277 | 21 | `public int demolishable(BuildingsTemplate t)` | How many of this kind stand that no demolition has been ordered for - nor, a gravel road, its paving (0.7.70). |
| 8300 | 6 | `private String linkingWords(BuildingsTemplate t, int n)` | Why a port cannot be demolished, if it is the one that links a landmass (ET5 D11): "it is the port that links Island 2"; null when it is not. |
| 8308 | 4 | `private boolean isBuildersDepot(BuildingsTemplate t)` | Whether this template is a builders' depot: owned by Construction and making building work. |
| 8318 | 10 | `private int otherBuildersDepotsStanding(BuildingsTemplate t)` | How many builders' yards and depots of any other Construction template stand and are not closing: one bought out and ordered demolished this month still stands until the month starts (closeDemolished()), and counting ... |
| 8330 | 17 | `private void placeDemolition(DemolitionQuote q, String from, double groundPaid)` | The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). |
| 8358 | 13 | `private void closeDemolished()` | D. |
| 8373 | 24 | `public boolean demolishShell(int templateId)` | C. |
| 8405 | 14 | `public BuildQuote quoteRestart(ConstructionControl.Shell shell)` | C. |
| 8421 | 4 | `private double restartMonths(BuildingsTemplate t, int n, double pointsLeft)` | A restart's wait, by the one wait (BuildingManager.restartWait()). |
| 8427 | 28 | `public boolean restartShell(int templateId)` | C. |

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) (lines 8456-8629)

| line | len | member | says |
|---:|---:|---|---|
| 8459 | 1 | `private BuildingsTemplate paveFrom()` | The road the city can pave and the road it becomes, as this city's catalogue has them; null for one it has not. |
| 8460 | 1 | `private BuildingsTemplate paveTo()` |  |
| 8463 | 6 | `public int paveable()` | Gravel roads the city could pave now: standing, not ordered demolished, and not already being paved. |
| 8471 | 1 | `public int pavingNow()` | Gravel roads being paved now: their Paved Roads on site, the gravel roads still standing and carrying traffic. |
| 8474 | 4 | `public boolean isPavingSite(String key)` | Whether a site is the Paved Road site with paving on it, which is not stopped (ConstructionControl, F). |
| 8493 | 15 | `public BuildQuote quotePave(int n)` | F. |
| 8521 | 28 | `public boolean paveRoads(int n)` | F. |
| 8551 | 13 | `public boolean cancelSite(String key, boolean cancel)` | C. |
| 8572 | 8 | `public double[] cancelRefundNow(String key)` | C. |
| 8582 | 10 | `public boolean rushSite(String key, boolean on)` | B. |
| 8594 | 11 | `public boolean moveSite(String key, int by)` | A. |
| 8607 | 5 | `public void resetSiteOrder()` | A. |
| 8620 | 9 | `public double[] rushPreview(String key)` | B. |

### in the month (lines 8630-8919)

| line | len | member | says |
|---:|---:|---|---|
| 8639 | 1 | `public ConstructionControl.Events getControlThisMonth()` |  |
| 8640 | 1 | `public double getOvertimePaidThisMonth()` |  |
| 8641 | 1 | `public double getDemolitionSalvageThisMonth()` |  |
| 8648 | 84 | `public void settleConstructionControl(ConstructionControl.Events ev)` | Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. |
| 8734 | 14 | `private double[] overtimeWagesByType(double premium)` | The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). |
| 8768 | 147 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction, int landmass)` | them, and charge nothing for the construction. |
| 8918 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 8920-9233)

| line | len | member | says |
|---:|---:|---|---|
| 8935 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 8947 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 8965 | 1 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 8968 | 1 | `public int wellsCommitted()` | ...and the Oil Wells standing, being built or ordered (0.7.62): one an oil site - since 0.7.91 the land wells and the platforms' wells. |
| 8971 | 1 | `public int landWellsCommitted()` | The land wells standing, being built or ordered (0.7.91): what the dry sites are counted against - every oil well until the platforms' came. |
| 8974 | 1 | `public int platformWellsCommitted()` | ...and the platforms' wells (0.7.91): what the platforms' slots are counted against. |
| 8977 | 12 | `private int committedWhere(java.util.function.Predicate<BuildingsTemplate> which)` | The buildings `which` picks, standing, on site or ordered. |
| 8999 | 3 | `public int committedOn(Resource r)` | The buildings that stand on a resource's sites - standing, on site or ordered (0.7.62): every template whose site is the resource's (siteOf()) - and since 0.7.91 not an offshore platform's jacket, which stands on its ... |
| 9009 | 3 | `public int committedOn(Resource r, Deposit.FieldType type)` | ...and the sites taken by the buildings that stand on one type of the resource's fields (0.7.110, batch I1; spec-materials 2.4): `type` null counts those on ANY - the Iron Mine's until batch I5, none of iron's since (... |
| 9021 | 12 | `public java.util.List<LandManager.Claim> claimsOn(Resource r)` | The works standing on a resource's sites, by template (0.7.124, batch I5; spec-materials 2.4): each one's type (null for ANY), the sites it takes and how many stand, are on site or ordered - the standing claims LandMa... |
| 9042 | 3 | `static boolean packs(BuildingsTemplate t)` | Whether an order for this building is placed on the city's fields by packing (0.7.124, batch I5): a works on the sites of a resource whose fields have types (Resource.hasTypes() - iron, and coal, stone and copper sinc... |
| 9046 | 15 | `private int committedOn(Resource r, Deposit.FieldType type, boolean everyType)` |  |
| 9070 | 10 | `public int committedFor(BuildingsTemplate t)` | What an order for this building is counted against its sites with (0.7.91, batch O10): a land well the land wells, a platform's well the platforms' wells, a platform's jacket the jackets on site (those standing have t... |
| 9092 | 3 | `public static Resource siteOf(BuildingsTemplate t)` | The resource whose sites a building stands on (0.7.62): iron for an Iron Mine, oil for an Oil Well; null for anything else. |
| 9103 | 3 | `public static boolean standsAnywhere(BuildingsTemplate t)` | Whether a building is a works that may stand on any ground (ET2, spec-earth2-terrain 3.2 and 4.4): every template with a site (siteOf()) - the mines, the quarries, the wells and the platforms, a pit and its benches be... |
| 9115 | 5 | `public static boolean isRun(BuildingsTemplate t)` | Whether a building is a run (ET4, spec-earth2-terrain 4.3): a highway or a railway line, laid in a line across the land - the Elevated Highway, and the rail TRACK (the Rail Spur, the Freight Line), not the Rail Termin... |
| 9122 | 3 | `public static LandManager.Use groundUse(BuildingsTemplate t)` | What a template's ground is used for (ET4): a works's if it stands anywhere (standsAnywhere()), a run's if it is a highway or a railway line (isRun()), a building's otherwise. |
| 9132 | 5 | `void pushLandByRelief()` | Hands the books the land office's classes of ground (ET3, spec-earth2-terrain 5.3): the prices of a square foot of foothill and of steep ground (flat's is the land's price, pushed beside it), whether the city owns onl... |
| 9139 | 4 | `private void pushLandUse()` | ...and the ground in use alone, as the month closes (the buildings its orders put up included), the prices staying the ones the month began with. |
| 9162 | 3 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 9172 | 14 | `private boolean hasDepositFor(BuildingsTemplate template, int quantity, int before, java.util.List<LandManager.Claim> ahead)` | ...with `before` more on the same sites already put on site by the orders ahead of it in a run (0.7.40, buildRunAhead()) - and since 0.7.124 a packed works with `ahead`, the claims those orders make on its resource's ... |
| 9195 | 9 | `public double remainingFor(BuildingsTemplate template)` | What is left in the ground for a building that stands on a resource's sites (siteOf()): its resource's remainder - and since 0.7.93 (THE TWO OIL POOLS, LandManager) a land well's the ground pool's, a platform's jacket... |
| 9220 | 13 | `public int sitesFor(BuildingsTemplate template)` | The sites the city owns that a building may stand on (0.7.84, batch O7; spec-oil 2.6): its resource's sites (siteOf()) - and for a land well (sectors.Oil.isLandWell()) only the dry ones, a field whose centre is on lan... |

### THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) (lines 9234-9294)

| line | len | member | says |
|---:|---:|---|---|
| 9259 | 3 | `public double getFreshRights()` | The city's water rights, units a month (see THE FRESH WATER LIMIT AND THE COAST). |
| 9264 | 3 | `void setFreshRights(double units)` | Harnesses and the load only: sets the rights, never below nothing. |
| 9269 | 3 | `public double getFreshCap()` | The fresh water limit, units a month: what the owned lakes and river yield plus the rights. |
| 9281 | 8 | `double derivedFreshRights()` | The rights an older save is given (spec-land 2.9): the fresh plants' nameplate standing less what the city's lakes and river yield, never below nothing - raised by the last unit in the last place when the limit they m... |
| 9291 | 3 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity)` | Whether this order can stand on the city's coast: true for everything but a desalination plant and a sea terminal (0.7.86), which need owned sea. |

### THE PORT'S LANDMASS (ET5, 2026-10-25; the ET5 design, D10) (lines 9295-9313)

| line | len | member | says |
|---:|---:|---|---|
| 9306 | 7 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity, int asked)` | Whether this order can stand on the city's coast, on the landmass asked for (Landmasses.CHOSEN, RULE or an id). |

### THE HYDRO STATION'S RIVER (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.2 and 1.3) (lines 9314-9340)

| line | len | member | says |
|---:|---:|---|---|
| 9324 | 3 | `public int hydroSlots(BuildingsTemplate t)` | How many stations of this template the city's linked hydro sites hold in all (HydroSites.slots()); 0 for a template that is not a hydro station. |
| 9329 | 1 | `public int hydroCommitted()` | The hydro stations standing, on site or ordered: what the slots are counted against. |
| 9332 | 3 | `public int hydroSlotsFree(BuildingsTemplate t)` | The slots left for this template: the slots less the stations committed, never below nothing - a site lost never stops a station standing, it only holds new orders. |
| 9337 | 3 | `public boolean hasRiverFor(BuildingsTemplate t, int n)` | Whether this order has a river: true for every building but a hydro station, whose order of n needs n slots free. |

### THE LONG BRIDGES' CROSSING (ET6, 2026-10-26; BridgeSurvey, BridgeWorks, Bridges) (lines 9341-9456)

| line | len | member | says |
|---:|---:|---|---|
| 9359 | 1 | **type** `public record BridgeSpan(double lengthM, double usd, double points, double cost)` | What a long bridge order's span is: the crossing's length (metres, one bridge's), the span's dollars at the founding for the whole order, the construction points that is, and their money before the builders' tax, at t... |
| 9369 | 4 | `public void setBridgeGroundForTest(BridgeSurvey.Ground ground)` | Harnesses only: hands the survey a ground of the harness's making in place of the World and the land (null restores the real one), so that a crossing can be made to exist where the map holds none. |
| 9374 | 5 | `private BridgeSurvey.Ground bridgeGround()` |  |
| 9381 | 7 | `public java.util.List<Landmasses.Row> bridgeChoices(BuildingsTemplate t)` | The landmasses the card offers a bridge to be ordered toward: those the city owns but home, and home alone where it owns no other; empty for a building that is no bridge. |
| 9390 | 3 | `public void chooseBridgeLandmass(BuildingsTemplate t, int landmass)` | The card's pick of the far landmass for the next order of this bridge (ONE order's: spent by it, forgotten at the month's settle); a pick that is not among the choices is passed over by the rule. |
| 9395 | 9 | `public int bridgeLandmass(BuildingsTemplate t)` | The landmass this bridge's order is for now: the card's pick if it is among the choices, else the rule - the first landmass no port links, else the first that is not home, else home. |
| 9411 | 18 | `public BridgeSurvey.Result bridgeSurvey(BuildingsTemplate t)` | The crossing this bridge's next order would be built over (BridgeSurvey): the shortest chain of fresh or shallow water from the city's own dry ground on the far landmass to its own buildable ground on a linked one, or... |
| 9431 | 5 | `public boolean hasBridgeFor(BuildingsTemplate t, int n)` | Whether this order has a crossing: true for every building but a bridge, whose order needs the survey to find one (before land and money: no bond for an impossible bridge). |
| 9443 | 3 | `public BridgeSpan bridgeSpan(BuildingsTemplate t, int quantity)` | What the span of an order of these costs (BridgeWorks): the surveyed crossing's dollars for each bridge of the order, as construction points at the template's own founding price a point, and their money at the builder... |
| 9447 | 9 | `private BridgeSpan bridgeWorksFor(BuildingsTemplate t, int quantity)` |  |

### DEEP WATER'S WORKS (DW1, 2026-10-31; sectors.Oil's DEEP WATER) (lines 9457-9850)

| line | len | member | says |
|---:|---:|---|---|
| 9468 | 1 | **type** `public record DeepWater(double depthM, double factor, double points, double cost)` | What the deep-water works of an order are: the sea the first unit stands in (metres), the price factor against a jacket's, the construction points of the premium, and their money before the builders' tax at the builde... |
| 9476 | 5 | `public DeepWater deepWater(BuildingsTemplate t, int quantity)` | The deep-water works of an order of these: (factor - 1) times the template's construction points for each unit, their money at the builders' price a point today (the template's non-material cost over its points, so it... |
| 9482 | 5 | `private DeepWater deepWaterAt(BuildingsTemplate t, int quantity, double depthM, double factor)` |  |
| 9489 | 1 | `public Bridges getBridges()` | The city's long bridges (ET6): each one built or building, where it runs and whether it stands. |
| 9495 | 19 | `private Bridges.Stock bridgeStock()` | What Bridges reads of the buildings (ET6): the counts of each bridge's template - standing, on site, stopped. |
| 9520 | 7 | `private int islandPortLandmass(BuildingsTemplate t, int asked)` | The landmass a sea terminal's order stands on when it is an island no port links (so that it takes that island's own room, not the linked pool's), else Landmasses.NO_LANDMASS: for every other building, for a city on o... |
| 9529 | 4 | `private int portLandmassOf(BuildingsTemplate t, int asked)` | The landmass a sea terminal's order stands on, linked or not: home for a city on one landmass; NO_LANDMASS for a building that is no sea terminal or when none will do. |
| 9535 | 3 | `public void choosePortLandmass(BuildingsTemplate t, int landmass)` | The card's pick of a landmass for the next order of this sea terminal (ONE order's: spent by it, forgotten at the month's settle); the order then stands there if the city owns shore on it (hasCoastFor()). |
| 9540 | 3 | `public java.util.List<Landmasses.Row> portLandmassChoices(BuildingsTemplate t)` | The landmasses the card offers a sea terminal: those the city owns shore on, home first; empty for a building that is no sea terminal and for a city on one landmass. |
| 9545 | 3 | `public double freeGroundFor(BuildingsTemplate t)` | The ground this building's order has to stand on, square feet: an island's room for a sea terminal ordered for an island no port links, else what its sector may build on (LandManager.freeFor()). |
| 9550 | 5 | `public double freeGroundFor(BuildingsTemplate t, int asked)` | ...for the landmass asked (Landmasses.CHOSEN, RULE or an id): the room an order placed there stands on, as quoteBuildOn() reads it. |
| 9561 | 3 | `public boolean isIslandPortOrder(BuildingsTemplate t)` | Whether this building's next order is a sea terminal for an island no port links (ET5): the order that takes that island's own room (freeGroundFor()), not the linked pool's, and that the not-enough-land page measures so. |
| 9570 | 14 | `public java.util.List<CityMap.BridgeRun> getBridgePicture()` | The long bridges that stand, as the map is handed them (ET6; CityMap's LONG BRIDGES ON THE MAP): each its kind and its path's corner plots, the two landings first and last; null while none stands - every generated wor... |
| 9586 | 3 | `public Landmasses.Picture getLandmassPicture()` | The city's ground by landmass and its linked set as the map is handed them; null while the city owns nothing off its home landmass (Landmasses.picture()). |
| 9591 | 41 | `private Landmasses.Stock portStock()` | What Landmasses reads of the buildings (ET5): the counts of each sea terminal's template - standing, on site, stopped, being demolished. |
| 9642 | 105 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 9757 | 1 | `public Investor getGovernmentInvestor()` |  |
| 9760 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 9762 | 3 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 9772 | 63 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction, int asked)` | ...for a landmass (ET5): a sea terminal stands on `asked` - Landmasses.CHOSEN (the card's pick, else the rule), RULE (the rule alone: the automatic builder never links an island) or an id - resolved once here, after t... |
| 9836 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 9847 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |

### A RUN OF ORDERS (0.7.40) (lines 9851-10037)

| line | len | member | says |
|---:|---:|---|---|
| 9891 | 35 | `public double buildRunInvoice(java.util.Map<BuildingsTemplate, Integer> run)` | What placing these orders in turn would charge altogether: each one's quote on the yard the ones before it leave. |
| 9928 | 3 | `public double buildFundingGap(java.util.Map<BuildingsTemplate, Integer> run)` | What the treasury is short of for the whole run: its invoice less the cash, never below nothing - an overdraft counted in full, as one order's buildFundingGap(template, quantity) counts it. |
| 9933 | 3 | `public int buildRunAhead(java.util.Map<BuildingsTemplate, Integer> run)` | How many of the run's orders, from the first, would pass the checks money cannot fix - ore, the coast, the river (HY1), licences, ground - each placed after the ones before it. |
| 9938 | 5 | `public BuildResult buildRunStop(java.util.Map<BuildingsTemplate, Integer> run)` | What the first order that would not go ahead is refused for - NO_DEPOSIT, NO_COAST, NO_RIVER, NO_BRIDGE (ET6), NO_LICENCE or NO_LAND - or SUCCESS when every order passes. |
| 9944 | 59 | `private int runAhead(java.util.Map<BuildingsTemplate, Integer> run, BuildResult[] stop)` |  |
| 10005 | 7 | `static int sitePool(BuildingsTemplate t)` | The sites an order is counted against in a run (0.7.91): its resource's (siteOf()), a platform's well and a platform's jacket each a pool of their own after the resources; -1 for none. |
| 10014 | 3 | `public double cashShortfall()` | What the treasury is overdrawn by right now (0.7.40): the cash below nothing, or nothing - Finances' "overdrawn by" ask. |
| 10019 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). |
| 10022 | 3 | `public double getTotalBuildingCost()` |  |
| 10025 | 3 | `public String getBuildingName()` |  |
| 10028 | 3 | `public int getBuildQuantity()` |  |
| 10033 | 3 | `public int getReceiptSerial()` | Which receipt this is. |

### THE CITY'S PAPER, SOLD AT AUCTION (CD2, spec-city-debt.md section 4) (lines 10038-10630)

| line | len | member | says |
|---:|---:|---|---|
| 10080 | 3 | `public Auction.Bidders auctionBidders()` | The bidders as they stand, for a sale of paper of any term: the figure the screen's chart of the bids reads. |
| 10117 | 3 | `public Auction.Bidders auctionBidders(int months)` | The bidders as they stand for paper of this many months (0 for none in particular): each reading the model, none holding anything of its own. |
| 10127 | 41 | `public Auction.Bidders auctionBidders(int months, double face)` | ...for a sale of this face, which only matters to a bank not yet capitalised: it then stands as the residual buyer for the whole of the face (below). |
| 10170 | 3 | `private Auction.Result runAuction(Auction.Kind kind, int months, double face, double lineCoupon, Auction.Cap cap)` | The auction for this paper, now: fair with the issue on the books, the bidders as they stand, the cap given (the default if none). |
| 10186 | 45 | `private Auction.Result runAuction(Auction.Kind kind, int months, double face, double lineCoupon, Auction.Cap cap, int firstCall...` | ...of a bond that may be called (CD4): firstCallAhead is the month, counted from now, the city may first call it - under 0 for paper that cannot be. |
| 10243 | 3 | `private boolean isTinySale(Auction.Result r)` | A sale that is no sale (CD2): the bids fell short of the issue at the city's cap and what they did buy is under the smallest deal worth arranging (minimumIssueSize()), or the fixed fee eats all it raises. |
| 10254 | 3 | `private static DebtQuote tinySaleQuote(String instrument, int duration, double requested, double before, Auction.Result r)` | The quote of a tiny sale: empty - no face, no cash, nothing to book, which every caller already reads as nothing to be had - and carrying the auction's result, so its summary can say what the bids would have bought an... |
| 10259 | 3 | `private static DebtQuote emptyQuote(String instrument, int duration, double before)` | A quote of nothing: the standing rate before and after, no face. |
| 10272 | 3 | `private static double cents(double v)` | Money to the cent, as every proceeds figure has been (the cent of today's money, at the founding's price level). |
| 10293 | 36 | `private DebtQuote solveForCash(double cash, double rounding, java.util.function.DoubleFunction<DebtQuote> at)` | The smallest face whose proceeds cover `cash`: a first face at par and the fee, then as many granules more as the shortfall needs at what a unit of that sale banked, until it covers - hard bounded (64 turns), because ... |
| 10331 | 3 | `private static boolean covers(DebtQuote q, double cash)` | A quote that raises the cash and sold what it was asked to: the test the solver's descent keeps. |
| 10342 | 3 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a bill, sold at auction for this much cash, would cost. |
| 10347 | 9 | `public DebtQuote quoteTBill(double amount, int duration, double rounding, Auction.Cap cap)` | ...with the highest yield the city will take, a cap of the caller's. |
| 10358 | 8 | `private DebtQuote billQuote(double requested, int months, double face, double before, Auction.Cap cap)` | The bill's quote at a face: the auction run on it, what it sold and raised. |
| 10374 | 3 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a bill on exactly the terms quoted. |
| 10379 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10392 | 8 | `private String issueNote(DebtQuote quote)` | The booking every bill shares - handleTBillLogic()'s, and the rollover's (issueForRollover()). |
| 10407 | 7 | `private void bookProceeds(DebtQuote quote)` | The treasury is paid what a sale at home raised, and the market owes it until the settle: the counters the settle and the audit read. |
| 10423 | 5 | `private void bookNewPiece(Debt paper, DebtQuote quote)` | A new piece, sold: told what it raised and at what yield, given its allotment, and the treasury paid. |
| 10436 | 3 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What an amortizing bond, sold at auction for this face, would cost. |
| 10441 | 10 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10453 | 12 | `private DebtQuote mediumQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The amortizing bond's quote at a face: the auction run on it, the coupon its stop sets, the cost of the credit over the schedule. |
| 10471 | 3 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books an amortizing bond on exactly the terms quoted. |
| 10476 | 6 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10484 | 8 | `private String issueMediumBond(DebtQuote quote)` | The booking an amortizing bond shares: a new line at the quote's coupon. |
| 10503 | 3 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a bond, sold at auction for this face, would cost. |
| 10508 | 3 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10523 | 13 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...sold callable if asked (CD4, spec-city-debt.md section 2.6): a bond of ten years or more with the city's right to call it whole at 100 from its first call date (CallTree.firstCallYears()). |
| 10546 | 7 | `public double callSpread(DebtQuote quote)` | What the call adds to a callable sale's yield (CD4, spec-city-debt.md section 2.6): the price of the option on the tree, which runAuction() puts on the fair yield of the same bullet without it - read off the quote's o... |
| 10555 | 3 | `private DebtQuote bondQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The bond's quote at a face: the benchmark line it reopens, or the new line it opens, and what the auction made of it. |
| 10560 | 22 | `private DebtQuote bondQuote(double requested, double face, int years, double before, Auction.Cap cap, boolean callable)` | ...of paper that may be called, if it is (CD4): priced from the months the line, or the new line, has to its first call. |
| 10584 | 3 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a bond on exactly the terms quoted. |
| 10589 | 3 | `public String handleLongBondLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10594 | 8 | `public String handleLongBondLogic(double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...sold callable if asked (quoteLongBond()): refused with CallTree.REFUSAL for a term that may not be. |
| 10609 | 21 | `private String issueLongBond(DebtQuote quote)` | The booking both bond issues share. |

### A BOND SIZED TO THE CASH IT BRINGS (0.7.10, CD2) (lines 10631-10685)

| line | len | member | says |
|---:|---:|---|---|
| 10657 | 3 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a bond whose CASH covers cashNeeded would cost. |
| 10662 | 9 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10673 | 3 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a bond sized to the cash, on exactly the terms quoted - the build screen's bond. |
| 10678 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |

### THE FLOATING-RATE NOTE AND THE INFLATION-LINKED BOND (CD5, (lines 10686-10847)

| line | len | member | says |
|---:|---:|---|---|
| 10709 | 3 | `public DebtQuote quoteFloatingNote(double amount, int duration, double rounding)` | What a floating-rate note, sold at auction for this face, would cost. |
| 10714 | 10 | `public DebtQuote quoteFloatingNote(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10726 | 10 | `private DebtQuote floatingQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The floater's quote at a face: the auction run on it, the margin its stop sets, the monthly coupon at today's dial. |
| 10738 | 3 | `public String handleFloatingNoteLogic(double amount, int duration, double rounding)` | Books a floating-rate note on exactly the terms quoted. |
| 10743 | 7 | `public String handleFloatingNoteLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's: refused with FloatingRateNote.REFUSAL for any term but 2 or 5 years. |
| 10752 | 9 | `private String issueFloatingNote(DebtQuote quote)` | The booking a floating-rate note shares with the rollover: a new line at the margin the quote carries, the policy rate now its first index. |
| 10763 | 3 | `public DebtQuote quoteFloatingNoteForCash(double cashNeeded, int duration, double rounding)` | What a floating-rate note whose CASH covers cashNeeded would cost: the quote at the face that brings it (solveForCash()). |
| 10768 | 9 | `public DebtQuote quoteFloatingNoteForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10779 | 3 | `public DebtQuote quoteLinkedBond(double amount, int duration, double rounding)` | What an inflation-linked bond, sold at auction for this face, would cost. |
| 10784 | 10 | `public DebtQuote quoteLinkedBond(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10796 | 10 | `private DebtQuote linkedQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The linker's quote at a face: the auction run on it, the real coupon its stop sets, the monthly coupon on the face as the index stands. |
| 10808 | 3 | `public String handleLinkedBondLogic(double amount, int duration, double rounding)` | Books an inflation-linked bond on exactly the terms quoted. |
| 10813 | 7 | `public String handleLinkedBondLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's: refused with InflationLinkedBond.REFUSAL for any term but 10 or 30 years. |
| 10822 | 9 | `private String issueLinkedBond(DebtQuote quote)` | The booking an inflation-linked bond shares with the rollover: a new line at the real coupon the quote carries, the price index now its base. |
| 10833 | 3 | `public DebtQuote quoteLinkedBondForCash(double cashNeeded, int duration, double rounding)` | What an inflation-linked bond whose CASH covers cashNeeded would cost: the quote at the face that brings it (solveForCash()). |
| 10838 | 9 | `public DebtQuote quoteLinkedBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 10848-11261)

| line | len | member | says |
|---:|---:|---|---|
| 10893 | 14 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 10931 | 11 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 10957 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation - expected, since 0.7.42 (below) - less the world's rate (the US's policy rate where the city has the world file... |
| 10970 | 5 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the inflation savers expect (Expectations, since 0.7.42 - the same expected inflation the real rate differential reads; the year's, as the parity ... |
| 10977 | 3 | `public double realPolicyRate()` | The policy rate less the inflation people expect (0.7.45): the dial in real terms, ex ante as every real rate here is since 0.7.42. |
| 10989 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 10994 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 10997 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 11012 | 3 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bill or bond of this size would cost. |
| 11025 | 47 | `public DebtQuote quoteForeign(String type, String money, double requested, int duration, double rounding)` | ...in any money of the eight (CD8, Jerus 2026-10-23): the same quote on THAT money's curve - its central bank's policy rate (the world's price of money in it, DebtManager.worldRate(code)) plus the city's country premi... |
| 11074 | 3 | `private static boolean isFloatOrLinked(String type)` | True for the two instruments that are written at home only (CD5): the floating-rate note and the inflation-linked bond. |
| 11082 | 3 | `private static double dollarCoupon(YieldCurve world, int months)` | A dollar bond's coupon on a world curve: its par yield at the term, rounded down to Auction.COUPON_STEP. |
| 11104 | 12 | `private YieldCurve selfPricedWorldCurve(String code, double face, int months, java.util.function.Function<YieldCurve, Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT ON THE WORLD'S CURVE (CD1; the walk of 0.7.2 on a single rate before it): the world's price of paper that is priced payment by payment. |
| 11121 | 4 | `private static Debt inMoney(Debt shape, String code)` | A piece not yet booked, told the money it is written in (CD8) so the world's quote prices it at that money's rate: the shape of selfPricedWorldCurve(). |
| 11127 | 4 | `private double issueCostIn(String code, double face)` | The fixed cost of an issue (costOfIssuance()) in the money the paper is written in: the figure itself for the US$, as a dollar quote has always taken it, and local money over localPer() for another (CD8). |
| 11138 | 4 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 11149 | 88 | `public String handleForeignLogic(String type, String money, double requested, int duration, double rounding, boolean holdAsRese...` | ...in any money of the eight (CD8): the piece is booked in {@code money} (Debt.getMoney()), opens its books and notes its sale in that money's units, and what it raised reaches the treasury in local money at that mone... |
| 11244 | 3 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 11249 | 12 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's (CD2). |

### THE AMORTIZING BOND AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 11262-11347)

| line | len | member | says |
|---:|---:|---|---|
| 11286 | 3 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | An amortizing bond whose CASH covers cashNeeded: the quote at the face that brings it. |
| 11291 | 9 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11306 | 3 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note or bond whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 11311 | 4 | `public DebtQuote quoteForeignForCash(String type, String money, double cashNeeded, int duration, double rounding)` | ...in any money of the eight (CD8): the cash needed and the face are in that money's units, the quote on its curve (quoteForeign(type, money, ...)). |
| 11317 | 4 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |
| 11323 | 9 | `public String handleForeignForCash(String type, String money, double cashNeeded, int duration, double rounding, boolean holdAsR...` | ...in any money of the eight (CD8). |
| 11334 | 4 | `private static String noLenderIn(String code)` | What the treasury is told when it asks to sell paper in a money the world has no rate for this month (CD8). |
| 11340 | 7 | `public double minimumIssueSizeIn(String money)` | The smallest deal worth arranging in one money's own units (CD8): minimumIssueSizeUsd() over its US$ per unit - the US$ figure itself for the US$. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 11348-13099)

| line | len | member | says |
|---:|---:|---|---|
| 11436 | 1 | `public java.util.List<String> getRolloverShortNotes()` | The rollover's sales that came up short this month, one sentence each; empty when none did. |
| 11442 | 1 | `public java.util.List<String> getRolloverTailNotes()` | The rollover's sales that cleared past the default cap this month, one sentence each; empty when none did. |
| 11452 | 1 | `public java.util.List<TimeSkipReport.AutoSale> getAutoSalesThisMonth()` | The month's automatic sales, in the order they were made. |
| 11455 | 3 | `void noteAutoSale(TimeSkipReport.AutoSale sale)` | Automatic building enters its bond sales here as it makes them. |
| 11460 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 11463 | 1 | `public Auction.Cap getRolloverCap()` | The cap its auctions take (CD2, D28), as the rollover card's chips read it. |
| 11466 | 7 | `public void setRolloverCap(Auction.Cap cap)` | ...and as they set it, applied at the next press; the change is a decision. |
| 11475 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 11478 | 11 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 11500 | 9 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 11511 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, String money, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not and in which money (null at home), and whether it is foreign paper rolled at home. |
| 11513 | 30 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 11545 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 11559 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 11630 | 62 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 11701 | 3 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; an amortizing bond (the serial until CD2) by quoteMediumBond... |
| 11706 | 13 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, String money, double cash)` | ...abroad in a money of the eight (CD8): the share in that money's units at the day's rate (localPer()), the quote on its curve. |
| 11721 | 1 | **type** `private record RolloverSale(Rollover.Issue issue, DebtQuote quote, double brought)` | One of the rollover's issues sold at auction, with the cash it brought. |
| 11724 | 9 | `private static String saleName(Rollover.Issue issue)` | An issue in the words of a sale: "10-year bond", "6-month bill", "10-year amortizing bond". |
| 11751 | 37 | `private void noteRolloverSales(java.util.List<RolloverSale> sales, Rollover.Plan plan)` | WHAT THE ROLLOVER'S SALES DID, said (CD2, D29). |
| 11790 | 4 | `private double localFace(boolean abroad, String money, DebtQuote quote)` | A rollover quote's face in local money: a foreign quote's at the day's rate for its money (localPer(); the D$'s rate to the US$ for a dollar quote). |
| 11801 | 22 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 11826 | 3 | `private void printPopulationInfo()` | printers |
| 11831 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 11844 | 3 | `private void printUtilityInfo()` |  |
| 11848 | 3 | `private void printCityStats()` |  |
| 11855 | 916 | `private void nextMonth()` |  |
| 12772 | 299 | `private void startOfMonthUpdate()` |  |
| 13071 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 13100-13211)

| line | len | member | says |
|---:|---:|---|---|
| 13157 | 4 | `private double issuanceFee()` | The fee in today's money: founding / unit, at the expected price level since 0.7.42 (THE ANCHOR, beside restrikeMoneyConstants()). |
| 13181 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 13199 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |
| 13208 | 3 | `public double minimumIssueSizeUsd()` | ...in US dollars at today's rate (0.7.40): Finances' "the minimum" on the ask abroad, which is asked in dollars. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 13212-13522)

| line | len | member | says |
|---:|---:|---|---|
| 13226 | 16 | `public String getCreditRating()` |  |
| 13244 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 13267 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 13281 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 13299 | 5 | `void strikeTheCurve()` | THE CURVE'S EXPECTATIONS (CD1, spec-city-debt section 3.1): the inflation the rule reads - the year's, or the target before there is one - and the inflation the city expects to settle at, handed to the debt market, wh... |
| 13319 | 51 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 13371 | 94 | `private void finalUpdateEconomy()` |  |
| 13467 | 4 | `private void updateConstructionCost()` |  |
| 13478 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 13489 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 13493 | 4 | `public long getHouseholdCapacity()` |  |
| 13497 | 4 | `public long getStoreCapacity()` |  |
| 13501 | 3 | `public long[] getJobs()` |  |
| 13506 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 13523-14140)

| line | len | member | says |
|---:|---:|---|---|
| 13552 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 13594 | 1 | `public PopulationCohorts getCohorts()` |  |
| 13595 | 1 | `public FamilyModel getFamilies()` |  |
| 13596 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 13597 | 1 | `public Migration getMigration()` |  |
| 13598 | 1 | `public Health getHealth()` |  |
| 13599 | 1 | `public Healthcare getHealthcare()` |  |
| 13623 | 517 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 14141-14153)

| line | len | member | says |
|---:|---:|---|---|
| 14149 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 14152 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 14154-14442)

| line | len | member | says |
|---:|---:|---|---|
| 14170 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 14189 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 14209 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 14251 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 14277 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 14287 | 3 | `public BuildLog getBuildLog()` |  |
| 14294 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 14319 | 1 | `public Unemployment getUnemployment()` |  |
| 14323 | 1 | `public Sickness getSickness()` |  |
| 14331 | 1 | `public Crime getCrime()` |  |
| 14335 | 1 | `public double getLastOrphanDeaths()` |  |
| 14336 | 1 | `public double getLastUnhousedDeaths()` |  |
| 14338 | 6 | `{ ... }` |  |
| 14346 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 14371 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 14380 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 14401 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 14412 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 14425 | 1 | `public double getStudentLoansLent()` |  |
| 14426 | 1 | `public double getStudentLoansRepaid()` |  |
| 14427 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 14431 | 1 | `public double getStudentLoanInterest()` |  |
| 14434 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 14441 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 14443-14582)

| line | len | member | says |
|---:|---:|---|---|
| 14460 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 14466 | 7 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 14475 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 14487 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 14531 | 1 | `public Equity getEquity()` |  |
| 14535 | 1 | `public Exchange getExchange()` |  |
| 14543 | 1 | `public BondMarket getBondMarket()` |  |
| 14553 | 1 | `public TreasuryFund getFund()` |  |
| 14556 | 1 | `BondMarket.Readings bondReadings()` | The readings the bond market is attached to, for a harness (CurveCheck, CD1). |
| 14572 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### THE ANCHOR (0.7.42): what the city expects prices to do, and the (lines 14583-14609)

| line | len | member | says |
|---:|---:|---|---|
| 14602 | 1 | `public Expectations getExpectations()` | Expected inflation, credibility and the expected price level. |
| 14605 | 1 | `public double getExpectedInflation()` | What the city expects inflation to be, a fraction a year. |
| 14608 | 1 | `public double getCredibility()` | How far the city believes the central bank, Expectations.KMIN to KMAX. |

### what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) (lines 14610-14739)

| line | len | member | says |
|---:|---:|---|---|
| 14613 | 26 | `double[] indexPrices()` | Each component's price this month, in PriceIndex's order: the shelf, the average rent paid, a meal, a luxury piece; the services' price is struck by the index from the fees. |
| 14641 | 8 | `double[] indexSpends()` | ...what the households spent on each: groceries and rent off the sectors' statements, meals and luxury off their own purchases; the services' is the index's sum of the fee lines. |
| 14656 | 14 | `double[] indexFees()` | ...each fee line's price: the clinic's fee for a general visit at the player's scale, a seat's tuition (the adult kinds' fees averaged - a price, so a shift in who studies what is not inflation), the fare a ride is ch... |
| 14672 | 8 | `double[] indexFeeSpends()` | ...and what the households paid on each fee line, off their books. |
| 14709 | 23 | `void restrikeMoneyConstants()` | Every money constant that prices something, struck at the expected price level: founding / unit x Expectations.getStruckLevel(), the level the last month ended on. |
| 14734 | 5 | `private void handOnExpectations()` | ...and what the month hands on from the anchor that is not a constant: the investors' expected inflation (0 before the basket is based) and - since 0.7.43 - the drift every sticky seller's price carries (Retail.sticky... |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 14740-14763)

| line | len | member | says |
|---:|---:|---|---|
| 14750 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 14759 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 14762 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 14764-15189)

| line | len | member | says |
|---:|---:|---|---|
| 14776 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 14778 | 1 | `public PriceIndex getPriceIndex()` |  |
| 14780 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 14797 | 6 | `double[] rateHistoryToSave()` | The ring and how full it is, for the save (XR1, found by AutoBuildCheck's reload twin): a FLOW, twelve months of rates the state a month ended in cannot give back. |
| 14805 | 7 | `private void restoreRateHistory(double[] saved)` | ...and back: an array of any other length (an older save) leaves the ring empty, as it always loaded. |
| 14814 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 14821 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 14832 | 5 | `public Sectors.TradeByGood getTradeByGood()` | The month across the city's edge, good by good (0.7.35): the businesses' struck statements split by good, an import with no good behind it by the sector that bought it, the households' cars among the cars bought and, ... |
| 14848 | 3 | `public double getOwnReserves()` | What is left of the vault once everything owed against it is taken off: the dollar paper outstanding and the foreign money parked in the bank, both claims on the one pot (0.7.35; the Trade tab's own subtraction until ... |
| 14853 | 5 | `public double getSharesHeldAbroad()` | The city's shares in foreign hands, at each company's price - its last trade, or its fair value before one (0.7.35; the Trade tab's own sum until then). |
| 14867 | 3 | `public double getHeldAbroadPrivately()` | The businesses' and the households' paper abroad, at today's rate: what is held abroad outside the vault. |
| 14872 | 3 | `public double getHeldAbroad()` | ...and the vault with it: everything the city holds abroad. |
| 14877 | 4 | `public double getHeldHereByTheWorld()` | What the world holds here: the city's shares at their price, the businesses' bonds at face, the foreign money parked in the bank, the city's own paper it bought at the auction (CD2, slice 5; at face) and the city's do... |
| 14889 | 1 | `public Bank getBank()` |  |
| 14892 | 1 | `public int getBankBranchesStanding()` | Commercial Banks standing now, counted from the buildings: a founded city has one before its first month opens it (bank.getBranches() is 0 until the month runs). |
| 14905 | 1 | `public CentralBank getCentralBank()` |  |
| 14919 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 14925 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 14928 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 14935 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 14939 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 14943 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 15010 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 15014 | 3 | `public EconomyManager getEconomyManager()` |  |
| 15017 | 3 | `public PopulationManager getPopulationManager()` |  |
| 15021 | 1 | `public LabourMarket getLabourMarket()` |  |
| 15022 | 1 | `public WantedPosts getWantedPosts()` |  |
| 15023 | 1 | `public Education getEducation()` |  |
| 15039 | 27 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 15080 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 15122 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 15140 | 47 | `public void recordMonth()` | Files this month in the graph history. |
| 15188 | 1 | `public Inbox getInbox()` |  |
| 15189 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 15190-15242)

| line | len | member | says |
|---:|---:|---|---|
| 15201 | 3 | `public int getMonthsUntilAutosave()` |  |
| 15222 | 20 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |

### THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) (lines 15243-15709)

| line | len | member | says |
|---:|---:|---|---|
| 15283 | 3 | `void useSaveClockForChecks(java.util.function.LongSupplier nanos)` |  |
| 15292 | 9 | `private void reportSaves()` | Takes what the writer has finished onto the calling thread: each result becomes the last save result, and a failed one is said as an autosave has always said it. |
| 15307 | 4 | `public void awaitSaves()` | Waits for the save writer, then reports what it wrote. |
| 15317 | 4 | `public void save(int slot, String slotName)` | Saves to a slot, now, on the calling thread: the live objects, after any background write has finished. |
| 15327 | 3 | `SaveWriter.Job prepareSaveForChecks(int slot, String slotName)` | For SaveSlotCheck (UP1): the save filled as an autosave fills it, the snapshot taken and nothing written, so a check can read what the writer thread would be handed. |
| 15332 | 3 | `DataSave liveSaveForChecks()` | For SaveSlotCheck (UP1): the persistent save the setters fill, to measure what a snapshot shares with it. |
| 15342 | 11 | `private SaveWriter.Job prepareSave(int slot, String slotName, boolean snapshot)` | Fills the save from the city as it stands, asks the map for its bytes and stamps it; everything save() did before it wrote anything. |
| 15360 | 349 | `private byte[] fillSave(String slotName)` | Fills the save from the city as it stands, asks the map for its bytes and stamps it (TR1: split out of prepareSave(), so the recording fills the same save and snapshots it without the history). |

### THE RECORDING (TR1, 2026-10-31) (lines 15710-16033)

| line | len | member | says |
|---:|---:|---|---|
| 15752 | 1 | `public boolean isRecording()` |  |
| 15754 | 1 | `public String getRecordingId()` |  |
| 15757 | 3 | `public Path recordingFolder()` | The recording's folder, or null in a city with no id. |
| 15762 | 1 | `public int getMonthsNotRecorded()` | Months the clock left out of the recording because the writer was behind (RECORD_BACKLOG). |
| 15765 | 1 | `long lastRecordNanos()` | For ReplayCheck: the FX-thread cost of the last recorded month, in nanoseconds. |
| 15771 | 20 | `public void setRecording(boolean on)` | Switches "Record this city" on or off. |
| 15793 | 4 | `private Recording.Meta recordingMeta()` | The meta of the recording being written: who the city is, and what wrote it. |
| 15799 | 11 | `private void submitRecording(SaveWriter.Task task)` | Hands the writer a recording file and counts it until it is written (a failure is printed there). |
| 15812 | 16 | `private boolean awaitRecordingRoom()` | Waits, at most RECORD_WAIT_MS, for the writer to get inside RECORD_BACKLOG; true when it has. |
| 15836 | 26 | `private void recordForReplay()` | Keeps the month that has just ended, when "Record this city" is on (called by nextMonth(), after the autosave: the counter and every month-end record agree). |
| 15864 | 4 | `private void recordHistoryFrom(SaveWriter.Job job)` | The autosave's history, handed to the recording from the snapshot the save made (autosave()); only once a month of this timeline is on disk. |
| 15870 | 4 | `public void markReplayCopy()` | Makes this game the replay's throwaway copy: recording off, and never on again. |
| 15875 | 1 | `public boolean isReplayCopy()` |  |
| 15882 | 10 | `public boolean replayMap(byte[] sidecar, int month)` | Replay's quick step: the map of a recorded month read from its sidecar and put in place of the city's, nothing else moved. |
| 15894 | 6 | `public void replayHistory(HistorySave h)` | Replay's history: a parsed history put in place of the copy's, as loadHistory() does with the file's, and the partner trade's years with it. |
| 15909 | 5 | `public String takeSkipFailure()` |  |
| 15923 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 15925 | 1 | `public GameFiles getGameFiles()` |  |
| 15944 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 15972 | 18 | `public void sendBuildingSave()` | EVERY ID TO THE HIGHEST, NOT AS MANY AS THERE ARE (FR3, 2026-10-19). |
| 15993 | 17 | `public void loadBuildings()` |  |
| 16024 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 16034-16210)

| line | len | member | says |
|---:|---:|---|---|
| 16061 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 16062 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 16063 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 16070 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 16081 | 8 | `public double localPer(String money)` | Local money per unit of one of the eight (CD8): the D$'s rate to the US$ for the US$ - the vault's, the figure every dollar path has always read - and that times the money's US$ per unit (the vault's cross rate, the d... |
| 16091 | 9 | `public void repayForeignPrincipal(double own, String money)` | A slice of principal repaid, in the money it is written in (CD8: the US$ goes through the one-argument form, as it always did). |
| 16117 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 16124 | 9 | `public void payForeignInterest(double own, String money)` | A coupon, likewise, in the money it is written in (CD8: the US$ goes through the one-argument form, as it always did). |
| 16195 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 16204 | 1 | `public double getCityPaperSettled()` |  |
| 16205 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 16206 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 16208 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 16211-16336)

| line | len | member | says |
|---:|---:|---|---|
| 16268 | 1 | `public double getCouponsToHouseholds()` |  |
| 16269 | 1 | `public double getPrincipalToHouseholds()` |  |
| 16270 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 16271 | 1 | `public double getCouponsToFirms()` |  |
| 16272 | 1 | `public double getPrincipalToFirms()` |  |
| 16273 | 1 | `public double getFirmsBoughtPaper()` |  |
| 16274 | 1 | `public double getCouponsToWorld()` |  |
| 16275 | 1 | `public double getPrincipalToWorld()` |  |
| 16276 | 1 | `public double getWorldBoughtPaper()` |  |
| 16277 | 1 | `public double getPaperBoughtFromWorld()` |  |
| 16288 | 7 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households', the central bank's and (CD2, slice 4) the businesses' and (slice 5) the world's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 16297 | 16 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 16315 | 21 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 16337-16432)

| line | len | member | says |
|---:|---:|---|---|
| 16347 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |
| 16371 | 15 | `private void desksBuysFirmPaper(double face, double cash)` | THE BANK BUYS THE BUSINESSES' PAPER (CD2, slice 4), for the spread gone, a sector that borrows, or one that wants the money: this much face for this much cash, off every piece's businesses' share pro rata, onto the ba... |
| 16400 | 19 | `private double desksBuysWorldPaper(double face)` | THE BANK BUYS THE WORLD'S PAPER (CD2, slice 5), when the hot money leaves and takes it with it (worldPaperLeavesWithTheMoney()): this much face, off every piece's world share pro rata, onto the bank's book with the un... |
| 16428 | 4 | `private void worldPaperLeavesWithTheMoney(double stockBefore)` | ...AND THE PAPER LEAVES WITH THE MONEY (CD2, slice 5, D7). |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 16433-16589)

| line | len | member | says |
|---:|---:|---|---|
| 16455 | 38 | `private double householdsTakeTheirShare()` |  |
| 16501 | 8 | `private void restrikeBankDiscount(double unearnedBefore, double faceBefore, double bankPaid)` | BK1: books the bank's gain or loss from a line's pooled discount re-blending (Bank.restrikeDiscount()): what its paper gained in face since the snapshot, less what it paid for it, less what its unearned discount rose ... |
| 16526 | 34 | `private double firmsTakeTheirShare(java.util.List<Debt> settling)` | THE BUSINESSES TAKE THEIR SHARE (CD2, slice 4, D33), at the same settle and from the same allotment (Debt.allotFirmsCash(), the auction's fill for them at the clearing yield). |
| 16577 | 12 | `private double worldTakesItsShare(java.util.List<Debt> settling)` | THE WORLD TAKES ITS SHARE (CD2, slice 5, D7; star 2), at the same settle and from the same allotment (Debt.allotWorldCash(), the auction's fill for the world at the clearing yield - CapitalFlows' rule, the stock it wo... |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 16590-16853)

| line | len | member | says |
|---:|---:|---|---|
| 16662 | 67 | `private void openMarketOperation()` |  |
| 16739 | 29 | `private double buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |
| 16781 | 29 | `private double buyPaperFromFirms(double wanted)` | The rest of a purchase the bank and the households could not fill, from the businesses' term paper (CD2, slice 4): the households' path with the businesses' share in its place - pro rata across the pieces they hold th... |
| 16824 | 29 | `private double buyPaperFromWorld(double wanted)` | The rest of a purchase the others could not fill, from the world (CD2, slice 5): the businesses' path with the world's share in its place - pro rata across the pieces it holds that are settled and pay no principal thi... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 16854-17114)

| line | len | member | says |
|---:|---:|---|---|
| 16959 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 16962 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 16969 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 16981 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 16988 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 16994 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 17007 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 17038 | 76 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 17115-17139)

| line | len | member | says |
|---:|---:|---|---|
| 17133 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 17137 | 1 | `public double getBuybackToHouseholds()` |  |
| 17138 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 17140-17348)

| line | len | member | says |
|---:|---:|---|---|
| 17235 | 18 | `private void takeTreasuryMonth()` |  |
| 17255 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 17257 | 1 | `public double getTreasuryOpening()` |  |
| 17258 | 1 | `public double getTreasuryClosing()` |  |
| 17259 | 1 | `public double getTreasuryRaised()` |  |
| 17260 | 1 | `public double getTreasuryRepaid()` |  |
| 17261 | 1 | `public double getTreasurySurplus()` |  |
| 17264 | 1 | `public double getTreasuryNotCash()` | What the balance charged or booked that the cash did not (CD3, CD7): the discount amortised, less the gain on paper retired and the exchange gain on the foreign debt. |
| 17275 | 1 | `public double getTreasuryCashSurplus()` | The part of last month's surplus that moved cash (SG1): the balance and the bridge's not-cash row, which is the bridge's own identity (surplus + not cash + raised - repaid + the rest = the cash's change). |
| 17278 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 17288 | 3 | `public double getNetPosition()` | The city's net position (0.7.32, the Finances tab's THE BALANCE): its cash - below nothing when it is overdrawn - less the paper it owes and what its central bank has advanced it. |
| 17298 | 3 | `public double getNetPositionAtCarrying()` | The same net position with the paper at what the books carry it at (CD3, spec-city-debt.md 5.4): its face less the discount not yet worn off, plus any premium. |
| 17320 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 17332 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 17337 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 17345 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |

### FROM EARNED TO THE BUDGET (0.7.31) (lines 17349-17494)

| line | len | member | says |
|---:|---:|---|---|
| 17387 | 34 | `public java.util.List<TreasuryJournal.Entry> getEarnedToBudget()` | The steps from EARNED to the budget's balance, in the Government bridge's order, signed as they move EARNED (+ adds, - takes away); every line, at nothing too. |
| 17423 | 5 | `public double getEarnedResidual()` | What the steps leave between EARNED and the budget: the dials moved since the month was struck, and nothing in a month nobody moved one. |
| 17429 | 7 | `double[] treasuryMonthToSave()` |  |
| 17437 | 10 | `void restoreTreasuryMonth(double[] saved)` |  |
| 17472 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 17475 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 17477 | 1 | `public double getPostAuditDriftWorst()` |  |
| 17478 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 17479 | 4 | `public void InterestExpense(double amount)` |  |
| 17484 | 3 | `public DebtManager getDebtManager()` |  |
| 17489 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 17495-17782)

| line | len | member | says |
|---:|---:|---|---|
| 17563 | 46 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 17621 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 17626 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 17648 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 17660 | 19 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 17688 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 17713 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 17720 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 17724 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 17734 | 8 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 17744 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 17747 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 17754 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 17764 | 10 | `public double getArrearsOwedTo(String sectorKey)` | Owed and unpaid to one sector's till (0.7.55): every line payDownArrears() would pay it, by the payee it would pay. |
| 17776 | 1 | `public double getArrearsPaidTo(String sectorKey)` | What the treasury paid this sector's till of its arrears this month (0.7.55) - its statement's arrears line. |
| 17778 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 17779 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 17780 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 17781 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 17783-18055)

| line | len | member | says |
|---:|---:|---|---|
| 17800 | 6 | `public double fairValueOf(Debt debt)` | What one piece is worth to somebody who does not care to hold it, right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on. |
| 17812 | 5 | `public BookDesk bookDesk()` | THE DEBT DESK (CD6): the book page's figures, a line a piece (BookDesk). |
| 17819 | 5 | `public BookDesk.Line bookLine(Debt debt)` | ...and one line of it, for a line's page (CD6): the market struck once, as bookDesk() strikes it, and the line read (BookDesk.line()). |
| 17835 | 3 | `public double quoteRepurchase(Debt debt)` | What clearing the whole of one piece that can be cleared would cost right now (CD4): the reverse auction's price for everything its holders offer, the central bank's face excepted (D18), at any price - the sum of the ... |
| 17847 | 3 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative) from clearing what quoteRepurchase() clears: the carrying value of that face less the price (CD3) - what the books hold it at against what buying it bac... |
| 17852 | 5 | `public double buybackOfferable(Debt debt)` | The face of a piece its holders can offer: the principal less the central bank's, which does not sell (D18); a dollar piece's all of it, the world's alone (D26). |
| 17881 | 23 | `public Buyback.Result quoteBuyback(Debt debt, double face, double maxPer100)` | THE REVERSE AUCTION, QUOTED (CD4, spec-city-debt.md section 6.2): what buying back this much face of one piece, at no more than this per 100, would take and cost, struck once on the market as it stands - nothing is mo... |
| 17912 | 7 | `public Buyback.HonestLine honestLine(Debt debt, Buyback.Result q)` | THE HONEST LINE (CD6, spec-city-debt.md section 6.3), struck from a quote: a buyback's gain is real on the statement, but the cash to pay for it costs today's rate. |
| 17921 | 3 | `private static boolean takesTheWholePiece(double taken, double principal)` | True when this much face is the whole of a piece's principal: all of it but the dust that slices of a holding leave. |
| 17950 | 95 | `public Buyback.Result buyBack(Debt debt, double face, double maxPer100)` | Buys one bond back, in whole or in part, and takes what it bought off the books: the reverse auction of quoteBuyback(), struck and paid. |
| 18052 | 3 | `public double repurchaseDebt(Debt debt)` | Buys back everything the holders offer of one bond, at any price: the thin form of buyBack() the buttons before CD4 knew. |

### the call (CD4, spec-city-debt.md section 6.4; D22, D23, D24) (lines 18056-18197)

| line | len | member | says |
|---:|---:|---|---|
| 18069 | 3 | `public double rateVolatility()` | The volatility of the city's own short rate, a year (D23): the standard deviation of the monthly changes of History's "interestRate" series over the last CallTree.VOL_MONTHS, times the square root of twelve, never und... |
| 18080 | 7 | `public boolean canCall(Debt debt)` | True when this piece can be called now: a callable bond of the city's own, on the books, whose first call month has come (LongTermBond .isCallableNow()) and which has nothing unsettled - a line sold last month has buy... |
| 18089 | 3 | `public double callCost(Debt debt)` | What calling this piece costs: its outstanding principal, 100 per 100 (CallTree.CALL_PRICE), paid to every holder, the central bank's too. |
| 18099 | 3 | `public double callGain(Debt debt)` | What calling the line books as a gain (a loss below nothing) against its carrying value (CD6): what it is carried at less callCost() - the figure callLine() adds to the month's gain on debt retired (NationalAccounts.g... |
| 18110 | 5 | `public double callSaving(Debt debt)` | The present value a call would save (D24): the line's remaining payments at today's curve, as a bond that cannot be called, less what the call pays and less what selling the paper again costs - the auction's fee (issu... |
| 18122 | 4 | `public boolean worthCalling(Debt debt)` | True when calling this callable line is worth it (D24): the saving (callSaving()) passes CallTree.WORTH_CALLING of the face, 3%. |
| 18149 | 40 | `public double callLine(Debt debt)` | Calls one line whole at 100 (CD4): every holder is paid its face - the bank, the households, the businesses, the world and the central bank, which has no say in it (a call is the paper's own term, not a sale a holder ... |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 18198-20344)

| line | len | member | says |
|---:|---:|---|---|
| 18266 | 20 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 18287 | 330 | `private void rebuildSimulationState()` |  |
| 18681 | 26 | `private static String refusal(int slot, DataSave loaded)` | Why a save would not load, or null when it would: the three refusals below, each logged, its file untouched. |
| 18717 | 21 | `private String refusalBeforeRebuild(int slot)` | Why loadGameSave() must not rebuild the world for this slot, or null when it may (FW1, 0.8.37). |
| 18740 | 10 | `public void loadGame(int slot)` | Load game |
| 18758 | 22 | `private void readTheMap(int slot, Long stamp)` | THE CITY MAP, READ BACK (0.7.60): from the slot's sidecar when the save's stamp is its stamp and it was drawn on this land in this month (CityMap.readSidecar()); a save that had a map whose sidecar is missing or stale... |
| 18817 | 11 | `private void restoreOilPools(DataSave loaded)` | THE TWO OIL POOLS ON A LOAD (0.7.93, batch O10b; LandManager's THE TWO OIL POOLS): once the land is in place, the offshore pool's E as saved - or, on a save from before SAVE_FORMAT 35, the one pool's E charged to the ... |
| 18829 | 60 | `private boolean restoreLandOnTheWorld(DataSave loaded, double owned)` |  |
| 18891 | 1393 | `private void readTheSave(int slot)` | The load itself; see loadGame(). |
| 20291 | 4 | `void seedFundLedger()` | THE FUND'S COST BASIS FOR A SAVE FROM BEFORE IT (0.7.39; the project's spec-fund-0739.md, 3.5): each market lot and bond at its market value this month, flagged as such, the rescue lot exact from the counters, one TRA... |
| 20300 | 44 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 20345-20634)

| line | len | member | says |
|---:|---:|---|---|
| 20356 | 17 | `private void seedMoneyConstants(double unit)` | Seeds every money constant struck in founding dollars at a unit: the load path's list since the reform, and since FX4 also buildWorld()'s at 1, so a new city does not keep the last loaded city's unit. |
| 20374 | 1 | `public Denomination getDenomination()` |  |
| 20377 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 20408 | 203 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 20622 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

