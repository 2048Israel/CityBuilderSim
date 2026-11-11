# Game.java - 22,401 lines · 998 methods · 33 constants · model

`ham/citybuildersim/Game.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (148), [DecisionLog](DecisionLog.md) (112), [Debt](Debt.md) (101), [Auction](Auction.md) (86), [DebtQuote](DebtQuote.md) (79), [Equity](Equity.md) (71), [TreasuryLine](TreasuryLine.md) (67), [ForeignAccounts](ForeignAccounts.md) (62), [LandManager](LandManager.md) (59), [GameLog](GameLog.md) (49), [ConstructionControl](ConstructionControl.md) (40), [GameFiles](GameFiles.md) (39), [Landmasses](Landmasses.md) (39), [AgeBand](AgeBand.md) (39), [BusinessDebtManager](BusinessDebtManager.md) (36), [DebtManager](DebtManager.md) (33), [CityMap](CityMap.md) (32), [ResearchCatalog](ResearchCatalog.md) (31), [Rollover](Rollover.md) (30), [Good](Good.md) (30), [Resource](Resource.md) (30), [BusinessInvestment](BusinessInvestment.md) (28), [Markets](Markets.md) (28), [Sector](Sector.md) (26), [WorldMoneys](WorldMoneys.md) (26), [LandParcel](LandParcel.md) (26), [JobType](JobType.md) (25), [TreasuryFund](TreasuryFund.md) (25), [TaxPolicy](TaxPolicy.md) (25), [TreasuryJournal](TreasuryJournal.md) (25)... and 142 more

**Used by (277):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [ArrivalMixCheck](ArrivalMixCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BasketYearCheck](BasketYearCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [CementConcrete](CementConcrete.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityBasket](CityBasket.md), [CityNeeds](CityNeeds.md), [ClockCheck](ClockCheck.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtManager](DebtManager.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DenominationCheck](DenominationCheck.md), [DeskCheck](DeskCheck.md), [DeterminismCheck](DeterminismCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FittingCheck](FittingCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [FloatingRateNote](FloatingRateNote.md), [FloorView](FloorView.md), [FollowBench](FollowBench.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [Founding](Founding.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [GdpCheck](GdpCheck.md), [GoldStandardCheck](GoldStandardCheck.md), [GovernmentPlayer](GovernmentPlayer.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GovernmentView](GovernmentView.md), [GovernmentViewCheck](GovernmentViewCheck.md), [GroceryCheck](GroceryCheck.md), [HarbourRenders](HarbourRenders.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HomeFuel](HomeFuel.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [HydroCheck](HydroCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InflationLinkedBond](InflationLinkedBond.md), [InfrastructureCheck](InfrastructureCheck.md), [InputRecorder](InputRecorder.md), [InterestAbroadCheck](InterestAbroadCheck.md), [InterventionCheck](InterventionCheck.md), [InvestCheck](InvestCheck.md), [IslandLinkCheck](IslandLinkCheck.md), [IslandRailCheck](IslandRailCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandClassView](LandClassView.md), [LandMap](LandMap.md), [LandNationsCheck](LandNationsCheck.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Markets](Markets.md), [MaterialsCheck](MaterialsCheck.md), [MediumTermBond](MediumTermBond.md), [MiniMap](MiniMap.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MoneySignCheck](MoneySignCheck.md), [MoneyWordsCheck](MoneyWordsCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OreView](OreView.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PartnersCheck](PartnersCheck.md), [PartnersPage](PartnersPage.md), [PartnersView](PartnersView.md), [PassThroughCheck](PassThroughCheck.md), [PegAnchorCheck](PegAnchorCheck.md), [Pieces](Pieces.md), [PlanCheck](PlanCheck.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [PriceLevelCheck](PriceLevelCheck.md), [PrizeCheck](PrizeCheck.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [RealEstate](RealEstate.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [ReloadCheck](ReloadCheck.md), [ReplayCheck](ReplayCheck.md), [ReplayCity](ReplayCity.md), [ReplayMode](ReplayMode.md), [ReplayMoveProbe](ReplayMoveProbe.md), [ResearchScreen](ResearchScreen.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [RunRenders](RunRenders.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SavedCities](SavedCities.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [SpreadPlanner](SpreadPlanner.md), [StaffingDrawCheck](StaffingDrawCheck.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [UiProbe](UiProbe.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md), [VaultTable](VaultTable.md), [VaultView](VaultView.md), [WageLadderCheck](WageLadderCheck.md), [WantedPostsCheck](WantedPostsCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProducts](WoodProducts.md), [WoodProductsCheck](WoodProductsCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 1199 | THE CITY FOUNDED THE 1900 WAY, AND WHAT A NEW BUILDING COMES WITH (R1b, 2026-11-05; docs/studies/research-design.md 2... |
| 1283 | THE FITTING ORDERED, PAID, BOUGHT AND WORKED (R1b, 2026-11-05; docs/studies/research-design.md 2.2 step 5) |
| 1309 | THE CITY'S RESEARCH IN THE MONTH (R3, 2026-11-06; docs/studies/research-design.md 5.1, 5.2) |
| 1605 | THE FOUNDING RESERVE (2026-09-21) |
| 1662 | · THE FOUNDING RECORD (0.7.10) |
| 1704 | THE WORLD MARKET (WN3, 2026-10-28; docs/studies/spec-nations.md 4, 6 and D28) |
| 1944 | THE WORLD'S MONEYS IN THE MONTH (WN4, 2026-10-30; docs/studies/spec-exchange-rates.md 3.5 and 3.7) |
| 2405 | THE REAL US DOLLAR (WN4, D29; RealDollar, docs/studies/spec-exchange-rates.md 3.9) |
| 2508 | THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) |
| 2638 | · THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) |
| 2823 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 2846 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 3074 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 3207 | THE NATIONS THAT SELL THE LAND (WN5, 2026-11-01; docs/studies/spec-nations.md 6.2-6.4) |
| 3421 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 3579 | · THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) |
| 3727 | PRIVATE INVESTMENT |
| 4295 | · AND THE BALANCE SHEET |
| 4507 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 4609 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 4643 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 5197 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 5355 | · the preferred offer |
| 5474 | · what the fund is worth |
| 5543 | · the dial |
| 5721 | · the hand |
| 5838 | · an Insane founding |
| 5967 | WHAT THE D$ IS HELD TO (XR2, 2026-10-22; ForeignAccounts, THE REGIMES) |
| 6119 | THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) |
| 6229 | ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's |
| 6311 | SHRINKING |
| 6600 | THE CONSTRUCTION WARNING |
| 6650 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 6985 | THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) |
| 7380 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 7479 | · the lender's test for one home, for the build card (I13-L) |
| 7665 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 7746 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 8085 | · THE FOREIGN CREWS (FC1) |
| 8701 | THE BUILDERS' PRICE (0.7.19) |
| 9053 | · BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) |
| 9252 | MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) |
| 9346 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 9755 | · F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) |
| 9931 | · in the month |
| 10224 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 10538 | · THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) |
| 10599 | THE PORT'S LANDMASS (ET5, 2026-10-25; the ET5 design, D10) |
| 10618 | THE HYDRO STATION'S RIVER (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.2 and 1.3) |
| 10645 | THE LONG BRIDGES' CROSSING (ET6, 2026-10-26; BridgeSurvey, BridgeWorks, Bridges) |
| 10761 | DEEP WATER'S WORKS (DW1, 2026-10-31; sectors.Oil's DEEP WATER) |
| 11156 | · A RUN OF ORDERS (0.7.40) |
| 11345 | THE CITY'S PAPER, SOLD AT AUCTION (CD2, spec-city-debt.md section 4) |
| 11941 | · A BOND SIZED TO THE CASH IT BRINGS (0.7.10, CD2) |
| 11996 | THE FLOATING-RATE NOTE AND THE INFLATION-LINKED BOND (CD5, |
| 12158 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 12576 | · THE AMORTIZING BOND AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 12662 | ROLLING WHAT FALLS DUE (0.7.13) |
| 13263 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 13560 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 13782 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 14016 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 14449 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 14561 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 14924 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 15596 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 15609 | THE READINGS THE MONTH TAKES OF THE CITY |
| 15919 | · the price of a place (2026-09-21) |
| 16059 | THE ANCHOR (0.7.42): what the city expects prices to do, and the |
| 16086 | · what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) |
| 16216 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 16261 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 16681 | the save system |
| 16742 | THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) |
| 16832 | THE PLAYER'S SAVE OFF THE CLOCK (UP6, 2026-11-06) |
| 17295 | THE RECORDING (TR1, 2026-10-31) |
| 17670 | PAYING THE WORLD BACK |
| 17847 | THE HOLDERS ARE PAID (0.7.1) |
| 17973 | · the desk, for the households |
| 18075 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 18259 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 18521 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 18782 | · a buyback's holders outside the pools |
| 18807 | WHAT THE TREASURY ACTUALLY DID |
| 19016 | · FROM EARNED TO THE BUDGET (0.7.31) |
| 19162 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 19456 | BUYING YOUR OWN DEBT BACK |
| 19741 | · the call (CD4, spec-city-debt.md section 6.4; D22, D23, D24) |
| 19883 | WHY LAND IS NOT IN THE RENT FLOOR |
| 20268 | · · the three the monthly path sets and this did not |
| 22104 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3616 | `Game.LandNeed.Kind.ROOM` |  |
| 3616 | `Game.LandNeed.Kind.SHORTFALL` |  |
| 3616 | `Game.LandNeed.Kind.DEPOSIT` |  |
| 3616 | `Game.LandNeed.Kind.COAST` |  |
| 10222 | `Game.BuildResult.SUCCESS` |  |
| 10222 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 10222 | `Game.BuildResult.NO_LAND` |  |
| 10222 | `Game.BuildResult.NO_DEPOSIT` |  |
| 10222 | `Game.BuildResult.NO_LICENCE` |  |
| 10222 | `Game.BuildResult.FAILED` |  |
| 10222 | `Game.BuildResult.NO_COAST` |  |
| 10222 | `Game.BuildResult.NO_RIVER` |  |
| 10222 | `Game.BuildResult.NO_BRIDGE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 243 | `Game.MARKET_SEED_SALT` | `0x4D41524B45545852L` | Salt for the D$'s market seed, so the market's path is not the world's own seed spelled again ("MARKETXR"). |
| 791 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 919 | `Game.FOUNDING_YARD` | `"Builders' Yard"` | The builders' building a city is founded with (PT1-M4): see foundingYard(). |
| 1654 | `Game.FOUNDING_CASH` | `100_000 * PriceLevel.FACTOR` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 1657 | `Game.FOUNDING_RESERVE_USD` | `25_000 * PriceLevel.FACTOR` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 1660 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 1990 | `Game.RISK_OFF_NAMED` | `{ "JPY", "CHF", "AUD" }` | The three the log names in a risk-off: the yen and the franc rise, the Australian dollar falls. |
| 3241 | `Game.LAND_FILES` | `new java.util.HashMap<>()` | The world files opened for the land office, by path: opened once per JVM and never closed (MapView keeps its own open the same way), so the cities of a JVM share them. |
| 3920 | `Game.STATIONS_SLOT` | `"Filling stations"` | Where the forecourts' word is filed among the month's investment lines (0.7.83): apart from the shops', as the bank's is. |
| 3923 | `Game.LABS_SLOT` | `"Research labs"` | Where the Research Labs' word is filed among the month's investment lines (R4): apart from Business Services' own, as the forecourts' is. |
| 7111 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 7114 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 8100 | `Game.ForeignOffer.NONE` | `new ForeignOffer(0, 0, 0)` | No contractor at work: every figure nothing. |
| 8859 | `Game.NO_GROUND_AHEAD` | `new GroundAhead(0, 0, 0, 0)` |  |
| 9071 | `Game.BITUMEN_BINDER_SHARE` | `.05` | The share of an asphalt surface's weight that is bitumen: five per cent [R18]. |
| 10236 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 10796 | `Game.BRIDGE_FOR_GOOD` | `"A bridge stands for good: taking one down is not built yet"` | Why a bridge cannot be demolished (ET6-C): one that stands is for good until the demolition of a bridge is built. |
| 11576 | `Game.CENTS_PER_UNIT` | `100 / PriceLevel.FACTOR` | How many of the model's proceeds granules go to a unit of money: a hundred at today's prices, and a hundred over the founding's price level (PriceLevel.FACTOR) at the founding's, so a proceeds figure is rounded to the... |
| 12219 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 12389 | `Game.NO_DOLLAR_FLOAT_OR_LINKED` | `"Floating-rate notes and inflation-linked bonds are issued at home only."` | What the treasury is told when it asks for a dollar floater or linker. |
| 12433 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 12593 | `Game.BUILD_NOTE_GRANULE` | `1000 * PriceLevel.FACTOR` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 14471 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 14483 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 14486 | `Game.BUILD_BOND_GRANULE` | `100 * PriceLevel.FACTOR` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 14503 | `Game.FIXED_ISSUE_COST` | `12 * PriceLevel.FACTOR` | Bond counsel, rating and printing. |
| 14514 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003). |
| 14554 | `Game.MINIMUM_ISSUE_FLOOR` | `50 * PriceLevel.FACTOR` | The floor under minimumIssueSize(), in thousands: $50k, so a brand new city can still borrow. |
| 16284 | `Game.SHORTFALL_DUST` | `1e-9` | A shortfall smaller than this share of an order's cost is rounding, not a want of cash: buildFor() borrows nothing for it. |
| 16688 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |
| 17310 | `Game.RECORD_BACKLOG` | `6` | The most recording writes the writer may be behind by (months and histories not yet on disk): the clock then records nothing that month, and a skip waits. |
| 17313 | `Game.RECORDING_NAME` | `"Recording"` | The name a recorded month's save carries in its header (the slot name a save has): the same whether the month was filled for the recording or shared with the autosave's. |
| 17316 | `Game.RECORD_WAIT_MS` | `30_000` | The longest a skip waits for the writer to get inside RECORD_BACKLOG, in milliseconds, before it records nothing that month: a writer that cannot write must not hold the skip for ever. |

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
| 1129 | `private String loadFailure` | Why the last load did not happen, or null. |
| 1188 | `private final Recipes recipes` | The recipes each template's buildings run, chosen in a city founded the 1900 way and saved there (R1c): nothing in any other. |
| 1194 | `private final EquipmentForms equipmentForms` | The shares of the 1900 forms (steam, the farms' horses, the carts') set from the buildings still without the equipment that ends them (R1). |
| 1294 | `private final Fittings fittings` | The fittings on site: ordered and paid, not yet worked (R1b). |
| 1304 | `private final Research research` | The city's research (R3): the grants, the posts, the work piled on each open problem and the discoveries found here. |
| 1354 | `private double[] researchFill` | The job fill by JobType ordinal the month's research bill was struck at (settleResearchBill()), or null before any month; for the harnesses and the screens that show the posts filled. |
| 1363 | `private double prizePaid` | The prize the treasury paid the Research Labs this month (finalUpdateEconomy(), TreasuryLine.PRIZES), the labs' part of the finds' purses less what the ceiling refused (that is owed as arrears). |
| 1677 | `private Founding founding` |  |
| 1702 | `private transient Nations nations` | The nations on this city's path, built the first time getNations() is asked and again when the founding's path or seed differs (not saved: the path and seed are, in the founding). |
| 1740 | `private transient volatile WorldMarket worldMarket` | The market the city trades in, or null: built where a world file is there, the first time the month (or a load) needs it. |
| 1743 | `private transient boolean worldMarketTried` | Whether the market has been looked for in this city, so a file that will not open is said once and not every month. |
| 1746 | `private transient boolean onLiterals` | Whether this city plays on the goods' literals whatever world file is there (playOnLiterals()). |
| 1775 | `private transient volatile PartnerTrade partnerTrade` | The city's trade by partner, built with the market; null with none. |
| 1982 | `private transient volatile WorldMoneys worldMoneys` | The world's moneys against the US$, built with the market (null where it is null) and stepped in the month; not saved itself: DataSave.worldMoneys is its state. |
| 1985 | `private transient boolean worldRiskOffStarted` | What the month's step found, for the log after the reprice: whether a risk-off began, the yen's, the franc's and the Australian dollar's moves against the US$ in it, and the lines of the world pegs that broke. |
| 1986 | `private transient double[] worldRiskOffMoves` |  |
| 1987 | `private transient List<String> worldPegBreaks` |  |
| 1996 | `private transient volatile PolicyRates policyRates` | The eight central banks' policy rates over the world's moneys (XR4), built with them and stepped in the month; not saved itself: DataSave.policyRates is its state. |
| 2002 | `private transient boolean worldQuietForTheD` | Whether the world's moneys are kept from the D$ in this city (quietTheWorldForTheD()). |
| 2167 | `private transient GoldPrice goldPrice` | Gold's price (GD2), built from the nations the first time it is asked and again when their path or seed differs; pure, so never saved. |
| 2528 | `private CityMap cityMap` | The city map, or null until it is asked for. |
| 2531 | `private int mapFailures` | How many months the map failed to keep up and was dropped (each one logged): what a harness asserts is none. |
| 2548 | `private boolean mapAtSeaStale` | Whether the map's oil at sea is to be handed over again before it is next read (0.7.97): set by a draw and a load. |
| 2659 | `private int mapGeneration` | Bumped whenever the city's map is thrown away (a new city, a load): a draft from before it is another city's. |
| 2663 | `private final Game game` |  |
| 2664 | `private final int generation` |  |
| 2665 | `private final CityLand land` |  |
| 2666 | `private final java.util.Map<Resource, double[]> remaining` |  |
| 2667 | `private final BuildingVisual.Type[] types` |  |
| 2668 | `private final long[] counts` |  |
| 2670 | `private final int month` | The game's month when the draft was made: what the map's logs date the deal by (CL3). |
| 2672 | `private final Landmasses.Picture landmasses` | The landmasses as they stood (ET5): what the map is measured under; null for a city on one landmass. |
| 2674 | `private final java.util.List<CityMap.BridgeRun> bridges` | The long bridges standing when it was copied (ET6), null for none. |
| 2675 | `private volatile CityMap map` |  |
| 2808 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 2881 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 2882 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 3112 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 3231 | `private transient LandSellers landSellers` | The sellers of this city's ground, built the first time they are asked (landSellers()); null where none sells. |
| 3232 | `private transient boolean landSellersTried` |  |
| 3235 | `private transient int landNations` | How many nations the world file names, -1 until asked (landNationCount()), 0 for no file. |
| 3238 | `private double[] landBoughtKm2, landBoughtUsd` | What the city has bought from each nation, km2 and thousands of US dollars, by the nation's number; index 0 is a purchase with no seller named. |
| 3566 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under ON OFFER (under the toggle until 0.7.26), and a short vault says here that the rest was converted. |
| 3567 | `private int lastLandReceiptMonth` |  |
| 3672 | `private BusinessInvestment.OrderWatch orderWatch` | Harnesses only (0.7.54): what every order search decides is told here as well - see BusinessInvestment.OrderWatch. |
| 3717 | `private boolean monthRun` | WHETHER A MONTH HAS RUN SINCE THE CITY WAS FOUNDED OR LOADED (0.7.102). |
| 4525 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 4534 | `private double carriedTransitBill` | ...and the month's transit bill as 6d struck it (0.7.49), applied inside the rebuild for the same reason: the rebuild would strike it at the fill the month ended on, not the one it was paid at. |
| 4545 | `private double carriedCaptiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, because the cells are not back when the road ratio is first struck (carriedCarOwnership's reason), and a j... |
| 4548 | `private double[] carriedFuelMonth` | ...and the drivers' fuel as 6d drew it (0.7.62): bill, imported part, litres - DataSave.householdFuel; null for a save from before it. |
| 4551 | `private double[] carriedHomeFuelSale` | ...and the homes' lamp oil and coal as 6d struck them (R0e): the sale held for the next bill, DataSave.homeFuelSale; null for a month that struck none. |
| 4554 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 4560 | `private final HomeFuel homeFuel` | The homes' lamp oil and coal (R0c, the home form); both shares the year's in a city founded the 1900 way (R0e, basketHearsTheYear()), 0 in every other, and then nothing in it runs. |
| 4586 | `private final double[] rowFeesSettled` | Each row's fees as the last month's waterfall was handed them: the clinic's, the schools', the fares and the fuel (0.7.49). |
| 4614 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 4895 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 5236 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 6491 | `private int wellsWornOut` | Wells retired worn out over the run (0.7.84): a count for the playtest, not saved. |
| 6518 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 6608 | `private int constructionShedMonth` |  |
| 6609 | `private double constructionShedPoints` |  |
| 6668 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 6680 | `private final java.util.Map<String, String> landBlockedNeeds` | The ground each waiting sector asked for this month (PT1-M2): sector key to "needs 743 m², 301 m² free", the numbers its own refusal gave, kept beside landBlockedSectors and cleared with it, so the inbox's yearly noti... |
| 6701 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 6721 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 6738 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 7210 | `final BusinessInvestment.Decision decision` |  |
| 7211 | `final double cash, perUnitProfit` |  |
| 7212 | `final BusinessDebtManager credit` |  |
| 7213 | `final boolean banned` |  |
| 7214 | `boolean atPrime` |  |
| 7215 | `double firstRate` |  |
| 7216 | `int deskCalls` |  |
| 7370 | `private ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate` | What consider() tests an order on (0.7.113): the amount the interest is struck on, as sectors.SpreadPlanner's money gate is one replaceable piece - ON_ITS_WHOLE_COST, Jerus's decision A, unless a check or a probe set ... |
| 7624 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 7625 | `private final java.util.Set<String> heldForDownPayment` |  |
| 7706 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 7707 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 7708 | `private double salvageUsedThisMonth` |  |
| 7766 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 7904 | `private double bankedClearedAtLoad` | Construction points the last load cleared from stacks that carried them past what they owed (0.7.17; see the load path and BuildingManager.clearBankedProgress()). |
| 8297 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 8302 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 8501 | `public final int quantity` |  |
| 8502 | `public final double sticker` |  |
| 8503 | `public final double materialsNeeded` |  |
| 8505 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 8507 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 8508 | `public final double plantPrice` |  |
| 8509 | `public final double plantCost` |  |
| 8510 | `public final double materialsImported` |  |
| 8511 | `public final double materialsPrice` |  |
| 8512 | `public final double importCost` |  |
| 8518 | `public final double salesTax` | The sales tax in the price (0.7.19): what the builders remit on it, less what they claim back on the plant's material - passed on, as a shop's shelf price passes it on. |
| 8525 | `public final double allowance` | What the price allows for the material beyond the yard (0.7.19): the plant's and the world's at today's prices, net of the credit on the plant's and with the builders' tax on it - the part of the total an escalation c... |
| 8526 | `public final double total` |  |
| 8527 | `public final double landNeeded` |  |
| 8528 | `public final double landFree` |  |
| 8534 | `public final double months` | Months to finish at this month's shares of the site output, the planners' own reading (quoteMonths()), or NaN when the builders have no site output. |
| 8546 | `public final double bitumenNeeded, bitumenFromRefiners, bitumenImported, bitumenLocalCost, bitumenImportCos...` | The bitumen the order's paving takes (0.7.83, batch O6; spec-oil 2.5), in tonnes - none for anything but a paved road, a highway and a paving (Game.bitumenFor()) - what of it the refiners' tanks would sell and what th... |
| 8556 | `public final double prepSqFt, prepPoints, prepCost` | The foothill the order has to prepare (ET2; SitePreparation, LandManager.foothillToPrepareSqFt()), square feet; the construction points that is, owed by the order's site before its first building finishes; and its mon... |
| 8567 | `public final double worksSqFt, worksPoints, worksCost` | The cuts, viaducts and tunnels a highway or a railway order needs (ET4; RunWorks, spec-earth2-terrain 4.3): the run's land the works are priced on, square feet; the construction points they are, owed by the order's si... |
| 8568 | `public final double[] worksMix` |  |
| 8577 | `public final double bridgeLengthM, bridgePoints, bridgeCost` | The span of a long bridge order (ET6; BridgeWorks, spec-earth2-terrain 6.4): the crossing's length, metres; the construction points its deck is, owed by the order's site before its first bridge finishes, as a pad's an... |
| 8586 | `public final double deepWaterDepthM, deepWaterPoints, deepWaterCost` | The deep-water works of a floating platform or its well (DW1; Game.deepWater()): the depth of the sea the order's first unit stands in, metres (every unit of the order is priced at it); the construction points of the ... |
| 8764 | `private boolean contractsToInfer` | Set by the load path when a save carries no payers, and read once the land price is back. |
| 8779 | `private final BuildingManager.BuildersWages buildersWages` | What the builders' crews cost a point, today and at founding. |
| 8979 | `private int preparationOrders` | The foothill preparation billed over this game's life (ET2, SlopeCheck 9 and LongPlaytest's summary): the orders that carried one, the points they owed, and their money before the builders' tax (BuildQuote.prepCost). |
| 8980 | `private double preparationPointsBilled, preparationMoneyBilled` |  |
| 9035 | `private int runWorksOrders` | The cuts, viaducts and tunnels billed over this game's life (ET4, RunWorksCheck 10 and LongPlaytest's summary): the highway and railway orders that carried works, the points they owed, and their money before the build... |
| 9036 | `private double runWorksPointsBilled, runWorksMoneyBilled` |  |
| 9097 | `private double bitumenTonnes, bitumenCost, bitumenImported` | The bitumen drawn for roads since this game was founded or loaded, in tonnes, what it cost the builders and the part bought abroad (0.7.83): running totals for the checks and the playtest, never reset and not saved - ... |
| 9250 | `private double siteDrawBillable` | What the month's site draw cost the builders, as they bill it on (billableMaterial()): what the owners' escalation is struck against. |
| 9512 | `private String lastHandRefusal` | Why a demolition or a buy-out was not made, or null when it was. |
| 9934 | `private ConstructionControl.Events controlThisMonth` | This month's events, kept for the screens and the inbox: what the advance left, settled. |
| 9936 | `private double overtimePaidThisMonth` | What the treasury paid the builders this month for overtime, with their tax in it. |
| 9938 | `private double demolitionSalvageThisMonth` | What a finished demolition's material fetched this month, from the builders. |
| 10560 | `private double freshRights` | Units a month of fresh water the city may treat past what its lakes and river yield: an older city's (0.7.59). |
| 10665 | `private BridgeSurvey.Ground bridgeGroundForTest` |  |
| 10666 | `private BridgeSurvey.Result bridgeKept` |  |
| 10667 | `private int bridgeKeptFar, bridgeKeptVersion` |  |
| 10668 | `private long bridgeKeptStamp` |  |
| 10669 | `private CityLand bridgeKeptLand` |  |
| 10670 | `private BridgeSurvey.Ground bridgeKeptTest` |  |
| 11054 | `private final Investor government` | The city itself, as a payer. |
| 11727 | `private String saleHow` | How the sale being booked reads on the piece it makes or joins (Debt.getSales(), CD6): "auction", or "the rollover" while the treasury's rollover sells what falls due (issueForRollover()). |
| 12744 | `private final Rollover rollover` |  |
| 12747 | `private final java.util.List<String> rolloverShortNotes` | What the month's rollover sold short and did not raise (CD2), in words, for the inbox; the record of a month that has finished, not saved. |
| 12753 | `private final java.util.List<String> rolloverTailNotes` | What the month's rollover paid a tail past the default cap for (D29, Filled or capped higher), in words, for the inbox; not saved. |
| 12763 | `private final java.util.List<TimeSkipReport.AutoSale> autoSalesThisMonth` | Every automatic sale this month - the rollover's issues and automatic building's bonds - for the time skip's report (CD2, slice 6): a record of a month that has finished, cleared at the top of the next, not saved. |
| 14913 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 14914 | `private LandManager landManager` |  |
| 14918 | `private boolean loadingSave` | True while loadGameSave() runs initialize(), which then founds no land: the save brings its own (0.7.57). |
| 14921 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 14922 | `private BuildLog buildLog` |  |
| 14949 | `private PopulationCohorts cohorts` |  |
| 14950 | `private FamilyModel families` |  |
| 14958 | `private Migration migration` |  |
| 14964 | `private LabourMarket labourMarket` | What labour costs. |
| 14970 | `private final WantedPosts wantedPosts` | The posts the staffing gate refused for want of workers that the wage is priced on as if they were offered (LD1): see WantedPosts, and noteStaffingHold(), settleWantedPosts() and repriceLabour() below. |
| 14976 | `private Education education` | The schools. |
| 14985 | `private Health health` | How much of the workforce is off sick. |
| 14993 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 15601 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 15747 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 15754 | `private HouseholdAccounts households` | The residents' own books. |
| 15763 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 15773 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 15777 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 15781 | `private final CausesOfDeath causesOfDeath` | What people die of: the causes of 1900 and how history takes them away (R0b). |
| 15803 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 15807 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 15900 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 15906 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 15983 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 15993 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 15999 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 16006 | `private final Equity equity` | Who owns the city's companies. |
| 16010 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 16018 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 16028 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 16035 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 16057 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 16075 | `private final Expectations expectations` |  |
| 16222 | `private final Consumption consumption` |  |
| 16223 | `private boolean consumptionLoaded` |  |
| 16253 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 16271 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 16280 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 16281 | `private int rateHistoryFilled` |  |
| 16385 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 16401 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 16445 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 16458 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 16472 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 16490 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 16493 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 16495 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 16496 | `private double carriedRetailCapacity` |  |
| 16497 | `private double carriedRetailWant` |  |
| 16500 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 16501 | `private java.util.Map<String, String> lastInvestment` |  |
| 16504 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 16559 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 16690 | `private int monthsSinceAutosave` |  |
| 16778 | `private final java.util.concurrent.ConcurrentLinkedQueue<Written> savesWritten` | Results the writer's thread has finished, waiting for the FX thread to take them: see reportSaves(). |
| 16782 | `private java.util.function.LongSupplier saveClock` | The clock the floor is measured on (nanoseconds); checks give a city their own. |
| 16785 | `private long lastAutosaveAt` | When the last autosave was asked for, on saveClock; meaningless until autosavedYet. |
| 16787 | `private boolean autosavedYet` |  |
| 16855 | `private final java.util.concurrent.atomic.AtomicInteger playerSavesInWriter` | The player's Saves handed to the writer and not yet reported back by it. |
| 17319 | `private String recordingId` | The recording's id, which names its folder under recordings/; null in a city that never recorded. |
| 17321 | `private boolean recording` | Whether "Record this city" is on. |
| 17323 | `private boolean recordedSinceLoad` | False from a load or from switching on until the first month is written: that write drops the months past it (the rewind) and takes the history with it. |
| 17325 | `private boolean replayCopy` | True for the throwaway copy a replay shows (markReplayCopy): it never records and never writes the player's folder. |
| 17327 | `private boolean skipping` | True while simulateMonths() runs its months: the recorder then waits for the writer rather than skipping a month. |
| 17329 | `private long recordingStartedAt` | When the recording was started, in epoch milliseconds (its meta); 0 in a city loaded from a save, whose meta is on disk already. |
| 17331 | `private final java.util.Set<Path> recordingsWaiting` | The files the recorder has handed the writer that are not yet written, by target: a task replaced while it waited is counted once, and its replacement removes it. |
| 17332 | `private final Object recordingRoom` |  |
| 17334 | `private int monthsNotRecorded` | Months the clock did not record because the writer was behind, since the city was built or loaded. |
| 17336 | `private boolean inUnrecordedRun` | True while the clock is in a run of unrecorded months: the first of a run prints one line. |
| 17338 | `private volatile long lastRecordNanos` | What the last recorded month cost the FX thread: the fill, the snapshot and the hand-over, in nanoseconds. |
| 17350 | `private int recordingTimeline` | The recording's timeline: a load or switching on starts the next (TR2's review: a task of the one before cannot end this one). |
| 17353 | `private final java.util.Set<Integer> brokenTimelines` | The timelines whose first write could not clear the old months: added on the writer's thread (Recording.monthTask), read by every recording task as it writes (submitRecording) and by recordForReplay(), which then ends... |
| 17364 | `private int recordedFromAutosave` |  |
| 17543 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 17557 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 17693 | `private double foreignDebtRaisedThisMonth` |  |
| 17694 | `private double foreignPrincipalRepaidThisMonth` |  |
| 17695 | `private double foreignInterestPaidThisMonth` |  |
| 17783 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 17803 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 17804 | `private double cityPrincipalRepaidThisMonth` |  |
| 17818 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 17819 | `private double cityDiscountForBank` |  |
| 17822 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 17839 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 17845 | `private double bankPrincipalRepaidThisMonth` |  |
| 17883 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 17892 | `private double couponsToFirms, principalToFirms, firmsBoughtPaper` | ...and the businesses' (CD2, slice 4): the same three figures, for the playtest and the harness. |
| 17902 | `private double couponsToWorld, principalToWorld, worldBoughtPaper, paperBoughtFromWorld` | ...and the world's (CD2, slice 5), which are declared: its coupon (an income debit) and its principal (a financial outflow) leave the pools for abroad, what it paid at the settle (worldBoughtPaper) is a financial infl... |
| 18093 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 18095 | `boolean restrikeOffForTest` | HoldersCheck's twin (BK1): true books no re-strike, as main did, so the harness can hold the two cities' figures side by side. |
| 18620 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 18623 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 18795 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the world (the holders of a dollar bond among them), carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape ... |
| 18797 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 18850 | `private double treasuryOpening` |  |
| 18851 | `private double treasuryClosing` |  |
| 18852 | `private double treasuryRaised` |  |
| 18853 | `private double treasuryRepaid` |  |
| 18854 | `private double treasurySurplus` |  |
| 18855 | `private boolean treasuryRecorded` |  |
| 18874 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 18886 | `private double retiredGainSoFar` | The gain (below nothing, a loss) on paper the treasury has retired since the last strike, in local money - the carrying value less the price paid, counted as each buyback happens (repurchaseDebt()), press to press lik... |
| 18897 | `private double treasuryNotCash` | The part of the month's budget balance that is not cash (CD3): the discount amortised, which the budget charges and the treasury never pays, less the gain on paper retired, which the budget books and the treasury neve... |
| 18900 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 19123 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 19126 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 19134 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 19135 | `private String postAuditDriftPool` |  |
| 19136 | `private double postAuditDriftWorst` |  |
| 19200 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 19203 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 19206 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 19214 | `private final java.util.Map<String, Double> arrearsPaidTo` | This month's arrears paid down, by the sector whose till they reached (0.7.55): what its statement's arrears line reads (SectorBooks, arrearsPaid), struck at the settle and read when the month is recorded, both inside... |
| 19881 | `private long pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 22056 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 22108 | `private final Denomination denomination` |  |
| 22394 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 22377 | **type** `public class Game` |  |
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
| 280 | 459 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 740 | 5 | `public void run()` |  |
| 746 | 44 | `private void initialize()` |  |
| 793 | 4 | `static { ... }` |  |
| 801 | 3 | `public void newGame()` | A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. |
| 810 | 47 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 887 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 974 | 12 | `private void foundingYard()` | The city opens with the builders' yard already standing (PT1-M4). |
| 987 | 8 | `public void resumeGame()` |  |
| 995 | 72 | `public void loadGameSave(int slot)` |  |
| 1076 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 1084 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 1099 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 1101 | 6 | `public void toggleQuit()` |  |
| 1113 | 4 | `public void saveForQuit()` | The quit's autosave (UP1): asked for, and on disk when this returns - System.exit does not wait for the writer's thread, so quitting waits for it here. |
| 1123 | 4 | `public void saveForClose()` | The window's close autosave (UP6; Jerus, 2026-10-23, on UP1's star 1: "Yes, autosave on close"): Quit's, under its own name, and on disk when this returns - the JVM ends once the window's stop() returns, and the write... |
| 1131 | 1 | `public String getLoadFailure()` |  |
| 1133 | 7 | `public void toggleGraphs()` |  |
| 1140 | 5 | `public void toggleReports()` |  |
| 1146 | 3 | `public void toggleNextMonth()` |  |
| 1151 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 1153 | 3 | `public int getMonth()` |  |
| 1158 | 3 | `public boolean isLeverOpen(PolicyDates.Lever lever)` | Whether a Policy lever is open in this city (R0): from the month Canada first had it, or the month a find brought it to. |
| 1163 | 3 | `public int leverOpeningMonth(PolicyDates.Lever lever)` | The game month a Policy lever opens in this city (R0): history's (PolicyDates), earlier where a find brought it forward, 1 where it is open from the start. |
| 1173 | 13 | `private void policyHearsTheMonth()` | Tells the policy the city's month (it holds every closed lever at zero), the labour market whether the minimum wage is law yet and the households what a pensioner is paid (R0), and the sectors the 1900 forms their unf... |
| 1191 | 1 | `public Recipes getRecipes()` | The recipes' driver, for the harnesses and the screens that read which way each template runs (R1c). |
| 1197 | 1 | `public EquipmentForms getEquipmentForms()` | The 1900 forms' driver, for the harnesses that read which shares the counts hold (R1). |

### THE CITY FOUNDED THE 1900 WAY, AND WHAT A NEW BUILDING COMES WITH (R1b, 2026-11-05; docs/studies/research-design.md 2... (lines 1199-1282)

| line | len | member | says |
|---:|---:|---|---|
| 1207 | 1 | `public boolean foundedUnfitted()` | Whether the city was founded the 1900 way, its buildings' equipment unfitted (R1b; Founding.isUnfitted()): false for every city a player or a harness founds until R1d. |
| 1216 | 9 | `void unfitTheFounding()` | Every building standing without every piece of equipment its template carries, and the forms set from it, so that the first month opens on them (R1b): on every stack, each piece's unfitted count is the stack. |
| 1234 | 4 | `private void layTheLadder(boolean atFounding)` | The wage ladder a city founded the 1900 way is paid on (R0d; research-1900 5.2): 1901's, the census's, about a quarter of today's real wage, on the labour market (its ratios, the minimum wage's bounds), the population... |
| 1244 | 15 | `void layTheLadder(WageLadder ladder, boolean atFounding)` | The same, on a ladder named rather than the founding's: package-private so that a harness whose premise is not the wages can found its 1900 city on today's ladder (PrizeCheck's lab villages, R0d ★7: an investors' orde... |
| 1270 | 12 | `void fitWhatFinished(java.util.List<BuildingManager.Completion> finished)` | What a new building comes with (R1b; research-design 2.2 step 6), at the finish and not at the order, so the order's cost stays the template's: in a city founded the 1900 way, each piece of equipment a finished buildi... |

### THE FITTING ORDERED, PAID, BOUGHT AND WORKED (R1b, 2026-11-05; docs/studies/research-design.md 2.2 step 5) (lines 1283-1308)

| line | len | member | says |
|---:|---:|---|---|
| 1297 | 1 | `public Fittings getFittings()` | The fittings on site, for the order's rule, the screens and the harnesses (R1b). |
| 1307 | 1 | `public Research getResearch()` | The city's research, for the planners' and the screens' reading of what is known, and for the harnesses (R3). |

### THE CITY'S RESEARCH IN THE MONTH (R3, 2026-11-06; docs/studies/research-design.md 5.1, 5.2) (lines 1309-1604)

| line | len | member | says |
|---:|---:|---|---|
| 1325 | 17 | `java.util.EnumMap<ResearchCatalog.Field, Double> researchStaff(double[] fill)` | The university-band staff of the standing schools by field (R3): each school's UNIV_* jobs times the stack's standing quantity, split over the fields as Research.staffByField() says. |
| 1344 | 8 | `private void strikeResearchPosts()` | The posts the month's grants buy are struck at the top of the month (nextMonth()), after the demolitions ordered close (closeDemolished(): a school pulled down gives no room), at the wages the labour market has now. |
| 1356 | 1 | `public double[] getResearchFill()` |  |
| 1366 | 1 | `public double getPrizePaid()` | The prize the treasury paid the Research Labs this month, which Business Services' till received in full (R4). |
| 1369 | 4 | `public BuildingsTemplate labTemplate(ResearchCatalog.Field f)` | The Research Lab template of a field (R4): the building whose getResearches() is the field, or null for a catalogue without one. |
| 1375 | 5 | `public int labsStanding(ResearchCatalog.Field f)` | The Research Labs of a field standing: the quantity of every stack whose template researches it, several stacks adding up (R4). |
| 1382 | 5 | `public int labsOnSite(ResearchCatalog.Field f)` | The Research Labs of a field on site, not yet standing (R4). |
| 1389 | 5 | `private void addResearchPosts(long[] jobs)` | Adds the jobs the month's research posts are to `jobs`, by JobType ordinal: a city with none held adds nothing (getJobs(), refreshJobs() and the load path's rebuild all count the posts through here). |
| 1401 | 15 | `private void settleResearchBill(double[] fill)` | What the posts cost this month, struck beside the schools' bill at the same wages and fill (R3; advanceDemographics()): the pay of the filled posts, and the laboratories' other costs on top of it (Research.bill() less... |
| 1423 | 21 | `private void workTheResearch(double[] fill)` | The research's month (R3), after the schools' (their staff are the faculty): the researchers the faculty and the filled posts give each field work the open problems in a city founded the 1900 way, and have nothing to ... |
| 1449 | 3 | `public boolean knows(BuildingsTemplate t)` | Whether a building is known in this city this month (R3): history's month for its "since", or the month the city found its code. |
| 1465 | 44 | `public String fit(Investor payer, BuildingsStacks stack, Equipment piece)` | Orders the fitting of ONE building of `stack` with `piece`, for `payer` (R1b): refuses with the reason in words and nothing drawn or paid, or returns null once it is ordered. |
| 1515 | 10 | `public String fitForCity(BuildingsTemplate t, String pieceId)` | The player's Fit (R1b): the city pays for ONE building of the template to be fitted with the piece, whatever its verdict says (it is the player's money), through fit(); more than one may be on site at a time. |
| 1527 | 3 | `private int buildingOutputAfterRepairs()` | What the builders have left of the month after the repairs, before the fittings (R1b): getBuildingOutput() as it was. |
| 1532 | 3 | `private int fittingPoints(int afterRepairs)` | The points of the builders' month the fittings on site take: what they owe, rounded up to a whole point, never more than `afterRepairs`. |
| 1547 | 15 | `public int workTheFittings()` | THE FITTINGS WORKED, and the sites' output struck (R1b; SimulationEngine.simulateMonth(), where it stands in place of getBuildingOutput()): the builders' crews work the fittings on site after the repairs and before th... |
| 1568 | 8 | `private void considerFitting(BusinessInvestment.Decision decision, Investor payer, String slot)` | What a fitting makes of a sector's month in the round (R1b): the choice between its next building and its best fitting is EquipmentFit.choose(); a decision with a piece is sent here from consider(). |
| 1583 | 5 | `public boolean setMinimumWage(double value)` | The player's hand on the minimum-wage dial (R0): refused until the lever opens (1918 on history's dates), because the law does not exist yet (WG1: it was also the wage ladder's anchor, and a floor of zero would have z... |
| 1590 | 5 | `public boolean setFloorTie(LabourMarket.FloorTie tie, double share)` | What the floor is tied to (WG1): refused with the dial, because the law does not exist yet. |
| 1597 | 5 | `public boolean setMinimumWageAdjustment(double share)` | The player's nudge on the floor (R0): refused with the dial, for the same reason. |
| 1602 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 1605-1661)

### THE FOUNDING RECORD (0.7.10) (lines 1662-1703)

| line | len | member | says |
|---:|---:|---|---|
| 1680 | 3 | `public Founding getFounding()` | How this city was founded. |
| 1685 | 15 | `public synchronized Nations getNations()` | The nations beyond the city on this founding's path and seed, from the game's world file; null where the file is not here. |

### THE WORLD MARKET (WN3, 2026-10-28; docs/studies/spec-nations.md 4, 6 and D28) (lines 1704-1943)

| line | len | member | says |
|---:|---:|---|---|
| 1754 | 16 | `public void playOnLiterals()` | This city plays on the goods' literals, as a city with no world file does, whatever file is there: no market is built or restored, and the markets keep the literal band (WN3). |
| 1772 | 1 | `public boolean isOnLiterals()` | Whether playOnLiterals() was called. |
| 1778 | 1 | `public WorldMarket getWorldMarket()` | The world market of this city, or null where there is no world file (the city then plays on the goods' literals) or it has not been built yet: the first month builds it, a load builds it. |
| 1781 | 4 | `public PartnerTrade.Snapshot getPartnerTrade()` | The city's trade by partner as of the last month tallied - a whole, immutable snapshot, safe from any thread; null with no market. |
| 1787 | 4 | `public TradeLanes.Feed getTradeFeed()` | The map's feed over the city's trade by partner: last month's and the year to date (the snapshot is the feed); TradeLanes.Feed.NONE with no market. |
| 1793 | 1 | `PartnerTrade partnerTradeForTest()` | For a harness: the live record behind the snapshot (its history, its state), or null. |
| 1796 | 4 | `private boolean hasBerth()` | Whether any sea terminal stands: the berth the market gives the city a sea leg by. |
| 1806 | 48 | `private WorldMarket ensureWorldMarket()` | The market of this city, built the first time it is asked (once a city): over the world file, the nations on this founding's path and seed, the city at its site - an Earth city at its spot, a generated world's at the ... |
| 1859 | 28 | `private void strikeTheWorldMarket()` | Steps 1 and 2 of the month (startOfMonthUpdate(), right after the world's price level and the exchange rate): the January solve when the year has turned, and the band of every good from last month's trade. |
| 1893 | 25 | `private void tallyThePartners()` | Step 3: the month's trade with the world split to the nations (PartnerTrade.tally()), from the month's own fills - the clearing's and every draw, with the world as the other side - at the prices they were struck at, i... |
| 1925 | 18 | `private void restoreWorldMarket(DataSave loaded)` | A load's half of the market (the load path, after the buildings are back): built where a file is there, the berth read, the solved state put back as saved (WorldMarket.restore() - not solved again), and the trade by p... |

### THE WORLD'S MONEYS IN THE MONTH (WN4, 2026-10-30; docs/studies/spec-exchange-rates.md 3.5 and 3.7) (lines 1944-2404)

| line | len | member | says |
|---:|---:|---|---|
| 1993 | 1 | `public WorldMoneys getWorldMoneys()` | The world's moneys of this city, or null where there is no world file or the market was not built (the D$ then feels no world). |
| 1999 | 1 | `public PolicyRates getPolicyRates()` | The policy rates of the eight central banks, or null where the world's moneys are null. |
| 2010 | 4 | `void quietTheWorldForTheD()` | The D$ of this city is not reached by the world: it is handed zeros and no terms of trade, so it moves to the bit as it did before the world's moneys (WN4), while the moneys are built, stepped, saved and charted as ever. |
| 2016 | 10 | `private WorldMoneys buildWorldMoneys(Path file, Nations beyond, WorldMarket.Base base, int nationCount)` | The moneys over the world file and the nations of this founding's path, with each nation's commodity share of exports from the market's base; null, and a line, if they will not build. |
| 2028 | 9 | `private PolicyRates buildPolicyRates(WorldMoneys moneys)` | The eight central banks' policy rates over these moneys, standing in the first January (Nations.FIRST_YEAR); null, and a line, if they will not build (the city then reads the constant world rate). |
| 2043 | 8 | `private void handTheUsRate()` | THE US'S RATE TO THE CITY (XR4): the US's policy rate is the world's price of money to the DebtManager (worldRate()); the constant (DebtManager.WORLD_BASE_RATE) where there are no policy rates, or where the city's wor... |
| 2053 | 3 | `private static double valueMove(double before, double after)` | A money's value against the US$ in a month, a fraction: its rate before over its rate after, less one (a rate is units to the dollar, so a falling rate is a rising money). |
| 2064 | 43 | `private void stepTheWorldMoneys(WorldMarket market, boolean solved, int year)` | Steps the moneys to the month the city is in (strikeTheWorldMarket(), after the January solve): each month in turn, handing the market's terms of trade in the January it solved and null in every other month. |
| 2109 | 5 | `private void stepThePolicyRates(int target)` | Steps the eight banks' policy rates, each month in turn, to month index target (WorldMoneys.monthIndex); none ahead of it are stepped back. |
| 2121 | 24 | `private void handTheMoneysToTheVault()` | THE MONEYS TO THE VAULT (WN4, right after the step, before anything buys or sells): the eight's US$ per unit - 1 / rate(code), NaN for a money with no rate this month - and, as the ninth, gold's US$ an ounce (GD2: Gol... |
| 2153 | 12 | `private void takeTheDollarsGoldDates()` | THE US$'S GOLD DATES (GD3, spec-gold-money 4.3), right after the moneys are handed to the vault: a city on Gold has a decision opened in the month the US$ left gold (April 1933, GoldPrice.FLOAT_START), in the month it... |
| 2169 | 9 | `private synchronized GoldPrice goldPrice()` |  |
| 2180 | 3 | `public double goldUsdPerOunce(int month)` | Gold's price in a month, US$ a troy ounce (GoldPrice): the month is an index as the moneys' are, months since January of Nations.FIRST_YEAR (WorldMoneys.monthIndex). |
| 2188 | 8 | `static double priceOrNaN(GoldPrice gold, int month)` | Gold's price in a month, or NaN where there is none (GD2's review, finding 1): no price (null), a month before the calendar, or a world whose US price level is not positive and finite (GoldPrice.level() throws Illegal... |
| 2198 | 6 | `public double getGoldMove()` | Gold's move against the US$ in the last month, at the month the vault's rates stand at: its price over last month's, less one (a rise is +, as VaultView.move's). |
| 2206 | 4 | `private double worldRiskOffLevel()` | The risk-off level the carry and the hot money are told this month (XR4): the moneys' own, 0 with no moneys or a world quieted for the D$. |
| 2215 | 5 | `private double[] tradeSharesByMoney(WorldMoneys moneys)` | The city's trade by money (MATCH_TRADE): each partner nation's exports plus imports with the city over the last finished year (PartnerTrade's history), else the year to date, else none - all US$ - mapped to the eight ... |
| 2226 | 27 | `private double[] tradeByNation()` | The city's trade with each nation: exports plus imports over the last finished year (PartnerTrade's history), else the year to date, else nothing (all zero); weight[n] for nation n, 1-based, index 0 unused, in thousan... |
| 2258 | 11 | `static double[] tradeSharesByMoney(WorldMoneys moneys, Nations beyond, int year, double[] weight)` | The eight's shares of a trade given by nation (weight[n] for nation n, 1-based, index 0 unused), summing to 1 - or all zero with no weight: each nation's money mapped to the eight by eightOf(). |
| 2275 | 9 | `static int eightOf(WorldMoneys moneys, String code)` | Which of the eight a money stands in for in the city's trade: itself if it is one of them; else the money it is pegged to or uses, followed through the chain (WorldMoneys.pegAnchor) if that is one of them; else the US... |
| 2292 | 24 | `private double cityExportPriceLevel(int year)` | The city's export-weighted ln world price of what it sells, each good against its normal (WN4, the D$'s terms of trade): for each good, its share of the value the city sold abroad in the last finished year (PartnerTra... |
| 2318 | 4 | `private static int goodNumber(Nations beyond, Good good)` | The number the file gives a good (1..goodCount(): its key is the Good constant's name), 0 for one the file does not price. |
| 2327 | 5 | `private void handTheWorldToTheD()` | What the world is this month, handed to the D$ right before its reprice: the moneys' dollar cycle and risk-off level and the hot money held; zeros with no moneys, and then the D$ is what it was. |
| 2334 | 16 | `private void noteTheWorldMoneys(double rateBeforeReprice)` | The log's lines about the world, after the reprice: money running to safety abroad (with where the D$ went in it) and the world pegs that broke. |
| 2355 | 18 | `private void restoreWorldMoneys(DataSave loaded)` | A load's half of the moneys (restoreWorldMarket()): the saved state put back where it fits this world file, else the moneys stand at the first January (Nations.FIRST_YEAR) and are caught up to the city's month with no... |
| 2379 | 25 | `private void restorePolicyRates(DataSave loaded)` | A load's half of the policy rates (restoreWorldMarket(), after the moneys): the saved state put back where it fits, else the rates are rebuilt by stepping from the first January (Nations.FIRST_YEAR) to the city's mont... |

### THE REAL US DOLLAR (WN4, D29; RealDollar, docs/studies/spec-exchange-rates.md 3.9) (lines 2405-2507)

| line | len | member | says |
|---:|---:|---|---|
| 2420 | 3 | `public double dollarsPer2015Usd()` | What a real 2015 US$ costs in D$ now: the rate (D$ per US$) times the US$'s price level since founding (RealDollar.dollarsPer2015Usd); the rate at founding. |
| 2425 | 4 | `public double struckFromRate()` | How far the D$ the goods are struck at (EconomyManager.getStruckRate(), AN1) sits from today's D$: the struck rate over the D$, less one - below nothing while the goods trail a D$ that has weakened, above while they t... |
| 2434 | 4 | `public double realGdp2015Usd()` | The last finished month's GDP in real 2015 US$: the month's GDP over the city's price index (YearBook.realGdp's division) over the founding's rate in today's money (RealDollar.realGdp2015Usd). |
| 2443 | 1 | `public double realGdp2015UsdYear()` | The city's GDP in real 2015 US$ a year, in whole US$: the last finished month's (realGdp2015Usd(), thousands) at that month's pace, times 12 and 1,000. |
| 2454 | 7 | `public double realEffectiveRate()` | The D$'s real effective rate, 100 at founding (RealDollar.realEffectiveRate): the weights are the city's trade with each nation over the last finished year, else the year to date, else the US alone. |
| 2468 | 25 | `public List<RealDollar.Weighed> realEffectivePartners()` | The partners the real effective rate weighs (WN5b), in nation order: every nation the city traded with over the last finished year (else the year to date) whose money, rate and prices are all there to weigh (RealDolla... |
| 2495 | 1 | `public long getWorldSeed()` | The seed of the world this city stands on (0.7.56): chosen at founding, or derived from an older save. |
| 2503 | 1 | `public World getWorld()` | The world this city stands on (0.7.56): its terrain, its founding site and river, its fields of ore and oil. |
| 2506 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and every purchase since, whole blocks of a grid since 0.7.67 (CityLand). |

### THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) (lines 2508-2637)

| line | len | member | says |
|---:|---:|---|---|
| 2534 | 12 | `public CityMap getCityMap()` | The city map: drawn canonically the first time it is asked for, then kept up month by month. |
| 2561 | 49 | `CityMap.AtSea mapAtSea()` | The oil at sea as the map draws it (0.7.97, batch O13; CityMap's THE OIL AT SEA, docs/studies/spec-oil.md 2.12): each platform's jacket in the middle of the sea sites its slots hold on its field - the platforms on a f... |
| 2622 | 6 | `CityMap.RailPicture mapRail()` | The railway as the map draws its yard (0.8.1, batch FR0; CityMap's railPicture, spec-freight 4.3): whether the railway carries goods, and the share of its track's wagon sets the fleet has standing. |
| 2630 | 4 | `private static Deposit seaFieldOf(List<LandManager.SeaField> fields, int cell, int index)` | A sea field in the list by its world cell and index, or null. |
| 2636 | 1 | `public boolean hasCityMap()` | Whether the city map has been drawn: a city never asked for it has none, and its months pay nothing for it. |

### THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) (lines 2638-2822)

| line | len | member | says |
|---:|---:|---|---|
| 2662 | 41 | **type** `public static final class MapDraft` | A map to be drawn away from the screen's thread: the city's land, remains and counts as they stood, copied. |
| 2677 | 11 | `private MapDraft(Game game)` _(in Game.MapDraft)_ |  |
| 2690 | 3 | `public void draw()` _(in Game.MapDraft)_ | Draws the map on the copy: any thread, once. |
| 2695 | 1 | `public boolean drawn()` _(in Game.MapDraft)_ | Whether it has been drawn. |
| 2698 | 1 | `CityMap map()` _(in Game.MapDraft)_ | The map drawn, or null: a harness's (MapCheck 7). |
| 2701 | 1 | `public Game game()` _(in Game.MapDraft)_ | The game it was taken from. |
| 2705 | 3 | `public MapDraft mapDraft()` | A draft of the city's map, for drawing away from the screen's thread; null when the map is drawn already. |
| 2716 | 23 | `public boolean adoptMap(MapDraft draft)` | Keeps a drawn draft as the city's map, bound to its own land and kept up to the month (reconcile()), and says whether it is the city's map now. |
| 2741 | 1 | `public int getMapFailures()` | How many months the map failed to keep up and was dropped. |
| 2744 | 1 | `public BuildingVisual.Type[] getMapTypes()` | The building types as the map draws them, by id. |
| 2747 | 7 | `public long[] getMapCounts()` | Every type's standing count, by id: what the map's districts sum to. |
| 2756 | 4 | `private CityMap drawMap()` | The map drawn canonically from the city as it stands. |
| 2768 | 17 | `private void reconcileMap()` | The month's change placed on the map (CityMap.reconcile()), after the month's construction and demolitions; the land drawn again (a restatement) draws the map again. |
| 2787 | 1 | `public String getCityName()` | The city's name. |
| 2790 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 2793 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 2796 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 2799 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 2817 | 5 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset until 0.7.20, when the screen stopped saying what the money buys; NewGameChec... |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 2823-2845)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 2846-3073)

| line | len | member | says |
|---:|---:|---|---|
| 2884 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 2885 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 2887 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 2888 | 9 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 2899 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 2900 | 1 | `public double getSubsidyPaid(String key)` |  |
| 2903 | 3 | `public double getPrizePaidTo(String key)` | What the treasury paid this sector this month of the research prizes (R4): the month's prizePaid for Business Services, whose Research Labs earned it; 0 for every other. |
| 2907 | 5 | `public double getTotalSubsidyPaid()` |  |
| 2914 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 2927 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 2930 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 2949 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 2959 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 2994 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 3011 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 3027 | 5 | `public double getIncome()` | EARNED (0.7.31's name for it): the tax take less the running programmes, plus the utilities' net, at today's dials - the header's "+$X earned a month". |
| 3033 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 3037 | 3 | `public double getWaterRatio()` |  |
| 3041 | 3 | `public double getRoadRatio()` |  |
| 3046 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 3051 | 3 | `public Markets getMarkets()` | Every goods market. |
| 3056 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 3060 | 3 | `public LandManager getLandManager()` |  |
| 3065 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 3074-3206)

| line | len | member | says |
|---:|---:|---|---|
| 3115 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 3118 | 6 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 3133 | 73 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |

### THE NATIONS THAT SELL THE LAND (WN5, 2026-11-01; docs/studies/spec-nations.md 6.2-6.4) (lines 3207-3420)

| line | len | member | says |
|---:|---:|---|---|
| 3243 | 9 | `private static synchronized WorldData landFile(Path path) throws IOException` |  |
| 3257 | 20 | `private LandSellers landSellers()` | The nations that sell this city's ground, built once and kept: null when the city plays on the literals, when the world file is not here, when its world is not the Earth, and (with one line) when they would not build. |
| 3279 | 1 | `public LandSellers getLandSellers()` | The nations that sell the city's ground, or null where none does (a generated world, no world file, the literals). |
| 3282 | 5 | `private void handLandSellers()` | Hands the land office the nations that sell its ground, before the offers are listed or restored (LandMarket.setSellers()). |
| 3289 | 9 | `private String moneyOf(int nation)` | The ISO code of the money a nation uses this month (the path's), or null where it is not known. |
| 3300 | 16 | `private int landNationCount()` | How many nations the world file names, for the tally of what was bought from whom; 0 for no file (and on the literals). |
| 3318 | 3 | `public int landSellerCount()` | How many nations the tally of the land bought counts: the world file's, 0 where there is no file. |
| 3323 | 3 | `public double getLandBoughtKm2(int nation)` | What the city has bought from a nation since founding, in square kilometres (0 for index 0, a purchase with no seller named, the host's on a generated world; 0 for a nation it has bought nothing from and for a number ... |
| 3328 | 3 | `public double getLandBoughtUsd(int nation)` | ...and what it paid them, in thousands of US dollars. |
| 3333 | 4 | `void setLandBoughtForTest(double[] km2, double[] usd)` | Harnesses only (WN5): the tally of what was bought from whom, set by hand (a nation count + 1 long each, or null for nothing bought). |
| 3339 | 18 | `private void tallyLandBought(LandParcel parcel, List<LandParcel.Sale> sold)` | A purchase added to the tally: each part to its seller, a purchase with no seller named (or a seller the tally does not count) to index 0. |
| 3359 | 11 | `private void restoreLandBought(DataSave loaded)` | The tally as a save read it: kept only when both rows are the file's nation count + 1 long, else nothing bought (zeros). |
| 3378 | 3 | `private String soldByWords(List<LandParcel.Sale> sold)` | "Sold by United States (62%, US$1.3M) and Canada (38%, C$1.1M)." - each part's share of the offer's area and its price in its own money at today's rate (WorldMoneys.rate(); in US$ where the moneys are not built or the... |
| 3383 | 9 | `private String inSellersMoney(LandParcel.Sale s)` | A part's price in the seller's own money at today's rate: "C$1.1M", "MXN 2.3M"; in US$ where the money or its rate is not known. |
| 3394 | 3 | `private static String usdWords(double thousands)` | Thousands of US dollars as the screens write them: "US$101.8M" (Formats.amount() with the dollar's mark). |
| 3399 | 3 | `private String localWords(double thousands)` | ...and thousands of local money, with its own mark: "D$72.0M". |
| 3410 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 3417 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's price by (red when not, since 0.7.26 the only verdict on it); since 0.7.13 its button asks land... |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 3421-3578)

| line | len | member | says |
|---:|---:|---|---|
| 3454 | 5 | `public java.util.List<LandParcel> landShelf()` | The offers standing in the land office's order: cheapest ground first, per square foot of dry ground in US dollars (0.7.57: all forty) - the top-left card first. |
| 3461 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 3471 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 3481 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 3496 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 3501 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 3506 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 3518 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 3524 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 3529 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 3539 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 3570 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 3575 | 3 | `public java.util.List<LandParcel> getLandListing()` | The offers standing. |

### THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) (lines 3579-3726)

| line | len | member | says |
|---:|---:|---|---|
| 3613 | 17 | **type** `public record LandNeed(Kind kind, double drySqFt, Resource resource)` | What the city needs ground for: room in general, a shortfall of dry ground, a resource's deposit, or a coast. |
| 3616 | 1 | **type** `public enum Kind` _(in Game.LandNeed)_ | The four needs. |
| 3619 | 1 | `public static LandNeed room()` _(in Game.LandNeed)_ | Room to grow. |
| 3622 | 1 | `public static LandNeed shortfall(double drySqFt)` _(in Game.LandNeed)_ | This much more dry ground than the city has free. |
| 3625 | 1 | `public static LandNeed deposit(Resource resource)` _(in Game.LandNeed)_ | A site of this resource to stand a mine or a well on. |
| 3628 | 1 | `public static LandNeed coast()` _(in Game.LandNeed)_ | Sea, for a desalination plant (batch J2). |
| 3632 | 34 | `public LandParcel bestOffer(LandNeed need)` | The offer that best meets a need, by THE BEST OFFER's rules; null when none does. |
| 3667 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 3675 | 4 | `public void watchOrders(BusinessInvestment.OrderWatch watch)` | Harnesses only: tell this watch every order the three searches decide; null to stop. |
| 3680 | 3 | `public String getLastInvestment(String sector)` |  |
| 3696 | 11 | `private void deferBorrowingCosts()` | WHAT EACH BUSINESS'S BORROWING COST IT UP FRONT THIS MONTH (0.7.102, Jerus's A16): the fees the bank kept out of its loans, its mortgages' insurance premiums and its bonds' issuing costs - the month's own figures, the... |
| 3720 | 1 | `public boolean hasMonthRun()` | True once a month has run since the city was founded or loaded; false until then (0.7.102). |
| 3723 | 3 | `public java.util.Map<String, String> getLastInvestments()` | Every word the investors left last month, by the slot it was filed under (a sector's key, or the bank branch's label) - saved since 0.7.102 (A22); read-only. |

### PRIVATE INVESTMENT (lines 3727-4506)

| line | len | member | says |
|---:|---:|---|---|
| 3737 | 128 | `private void runPrivateInvestment()` |  |
| 3874 | 29 | `void noteStaffingHold(Sector.Staffing held)` | A planner was refused by the staffing gate (LD1; Decision.heldForStaff): sorts the hold for WantedPosts. |
| 3909 | 9 | `void settleWantedPosts()` | The month's asks settled into the posts per band the same month's reprice prices the wage on (LD1; WantedPosts.settle()). |
| 3931 | 15 | `private void considerLabs()` | THE INVESTORS BUILD A LAB (R4; research-design 5.3: "Investors build one while the purse pays its posts"). |
| 3955 | 48 | `private String orderLab(BuildingsTemplate lab, ResearchCatalog.Entry target, double stake)` | One lab ordered for Business Services, and the word on it. |
| 4009 | 12 | `private void shedIdleLabs()` | THE LABS NOTHING IS LEFT FOR ARE SHED (R4; runRetirement()): a field that has had a lab too many for Research.LAB_IDLE_MONTHS months running (Research.labTooMany()) sells one lab back, through the path the distress ru... |
| 4037 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 4050 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 4062 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 4068 | 437 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 4507-4608)

| line | len | member | says |
|---:|---:|---|---|
| 4557 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 4563 | 1 | `public HomeFuel getHomeFuel()` | The homes' lamp oil and coal, for the harnesses that set its shares and the screens that will read its sale. |
| 4566 | 5 | `private double homeFuelHomes()` | The homes the lamp oil and the coal are asked of: the doors the households pay rent on, the sum of the rows' doors the books split the rent and the home fuel bill by (HouseholdAccounts.rowDoors). |
| 4573 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 4576 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 4577 | 1 | `public double getHouseholdCarImports()` |  |
| 4580 | 1 | `public double getHouseholdFuel()` | ...and what they paid for fuel this month (0.7.49): what the cells paid - the refiners' shelf and the world's (0.7.62). |
| 4583 | 1 | `public double getHouseholdFuelImports()` | ...and what of it they paid the world (0.7.62): every litre with no refinery; the money audit's FuelFunded and FuelImports. |
| 4589 | 1 | `public double getRowFeesSettled(int row)` | ...one row's. |
| 4592 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 4595 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 4598 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 4601 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 4604 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 4607 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 4609-4642)

| line | len | member | says |
|---:|---:|---|---|
| 4617 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 4620 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 4623 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 4626 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 4629 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 4632 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 4635 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 4638 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 4641 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 4643-5196)

| line | len | member | says |
|---:|---:|---|---|
| 4669 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 4688 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 4699 | 3 | `public double bankTaxUnder(TaxPolicy p)` | The bank's profit tax under another policy (0.7.36, the Policy spec's M2): Bank.taxAt() at that policy's retail rate, bankProfitTaxRate()'s rule - so NEXT month's bill, the tax being in arrears. |
| 4712 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 4729 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 4737 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 4765 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 4797 | 64 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 4869 | 19 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 4962 | 98 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 5073 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 5086 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 5113 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 5130 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 5157 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 5186 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 5197-5354)

| line | len | member | says |
|---:|---:|---|---|
| 5239 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 5242 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 5247 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 5252 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 5257 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 5284 | 51 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 5337 | 3 | `private String hereK(String format, double figure)` | A figure of the city's money as the log writes it where a foreign one may be in sight: the format given the number, in the city's qualified sign - hereK("%,.0fk", 12) is "D$12k" (FM1). |
| 5342 | 3 | `private String cityK(String format, double figure)` | ...and in the city's own sign where no foreign figure is in sight: cityK("%,.0fk", 12) is "$12k" (FM1). |
| 5347 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 5353 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 5355-5473)

| line | len | member | says |
|---:|---:|---|---|
| 5363 | 11 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 5376 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 5379 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 5382 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 5385 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 5388 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 5398 | 20 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 5420 | 7 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 5440 | 28 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 5470 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 5474-5542)

| line | len | member | says |
|---:|---:|---|---|
| 5477 | 1 | `public double fundSharesValue()` | Its shares, both books, at the city's mark (Exchange.cityMark(): the last trade, fair value once that is a year old). |
| 5479 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 5481 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 5487 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 5489 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 5491 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes, on the bank's share at the city's mark. |
| 5495 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 5497 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 5501 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 5510 | 4 | `public double fundIdleCash()` | The fund's idle cash (RB1): what its rule cannot invest under its caps and its mix - TreasuryFund.idleCash() on its cash less the hand's reserve, its market book, and the room its share bids (Exchange.fundShareRoom())... |
| 5515 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands, at the withdrawal in force and the idle cash it holds: TreasuryFund.dueOn(fundValue(), fundIdleCash()) - transferOn() at the default with none idle (RB1). |
| 5518 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the city's mark: the Holdings page's line for it until 0.7.39 (FundView reads each book itself now). |
| 5522 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 5526 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 5528 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 5533 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 5537 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 5543-5720)

| line | len | member | says |
|---:|---:|---|---|
| 5546 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 5547 | 7 | `public void setFundDial(double dial)` |  |
| 5556 | 1 | `public double getFundWithdrawal()` | The fund's withdrawal, a share of its whole value a month (0.7.48, C1): TreasuryFund.getWithdrawal(), Norway's rule by default. |
| 5558 | 8 | `public void setFundWithdrawal(double share)` | ...set by the player in whole steps of TreasuryFund.WITHDRAWAL_STEP, from nothing to 10% a month, and recorded. |
| 5568 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 5569 | 8 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 5591 | 18 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, plus the gain on debt retired that the surplus carries and sp... |
| 5615 | 4 | `private static double exchangeFromEnd(double[] exchange, int n, int i)` | The exchange gain (CD7) the history kept for the month at index {@code i} of a series {@code n} long that ends on the same month: the exchange series is read from its end as the retired gain is, 0 for a month it does ... |
| 5626 | 10 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 5643 | 10 | `public double debtExchangeThisYearSoFar()` | CD7: the exchange gain (loss) on the foreign debt that {@link #surplusThisYearSoFar()} leaves out, over the same months (this calendar year's closed ones, in the months the surplus is recorded), a gain positive: what ... |
| 5661 | 7 | `public double debtExchangeSinceFounding()` | CD9 (CD8-R, 2026-11-08): what the currency has done to the city's foreign debt since founding, a gain positive, a loss below nothing: the sum of the history's "debtExchange" series, CD7's booked line month by month, t... |
| 5674 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 5702 | 18 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 5721-5837)

| line | len | member | says |
|---:|---:|---|---|
| 5724 | 11 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 5737 | 11 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 5754 | 1 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 5757 | 8 | `public void fundBuyShares(int company, double money, double limit)` | ...at a price a share the player names (0.7.39, the spec's D2): 0 is fair value, what the rule asks at (it bids TreasuryFund.RULE_PREMIUM over since 0.7.48). |
| 5767 | 1 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 5770 | 8 | `public void fundSellShares(int company, double shares, double limit)` | ...at a price a share the player names (0.7.39): 0 is fair value. |
| 5780 | 1 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 5783 | 6 | `public void fundBuyBond(int bondId, double money, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 5791 | 1 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |
| 5794 | 7 | `public void fundSellBond(int bondId, double face, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 5803 | 3 | `public double fundCashFree()` | The fund's cash a new buy may hold: its cash less what the hand's buys already hold (TreasuryFund.handReserve(), 0.7.39). |
| 5808 | 4 | `private static String shareCount(double n)` | A count of shares in a decision's words: whole from a hundred, else to two places, and four under one - a consolidated holding is a fraction (0.7.39). |
| 5814 | 5 | `private String atLimit(double limit, boolean bond)` | A decision's words for a named price: " at D$101.35k", " at 102.80 per 100"; nothing at fair value. |
| 5828 | 9 | `public boolean fundCancelOrder(int i)` | CANCELS ONE OF THE PLAYER'S ORDERS before the step posts it (0.7.39, the spec's D3): the order at this place in TreasuryFund.getHandOrders(). |

### an Insane founding (lines 5838-5966)

| line | len | member | says |
|---:|---:|---|---|
| 5855 | 8 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar bond (a term loan until CD2), LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.lan... |
| 5895 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 5918 | 1 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 5924 | 13 | `public double buyForeignCurrency(double amount, String code)` | ...into one money of the eight (the Reserves page's money picker; WN4, THE VAULT IN EIGHT); null goes in by the reserve mix, as the one-argument purchase does. |
| 5949 | 1 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 5955 | 11 | `public double sellForeignCurrency(double amount, String code)` | ...out of one money of the eight (WN4, THE VAULT IN EIGHT): the clamp is what that money alone holds, and no other gives up anything; null sells from the largest holding, as the one-argument sale does. |

### WHAT THE D$ IS HELD TO (XR2, 2026-10-22; ForeignAccounts, THE REGIMES) (lines 5967-6118)

| line | len | member | says |
|---:|---:|---|---|
| 5981 | 14 | `public void setCurrencyRegime(ForeignAccounts.Regime regime)` | Chooses how the D$ is held: Float, Lean against falls (the default), Band, Peg or Gold. |
| 5997 | 7 | `public void setMintPrice(double price)` | Sets the mint price under Gold, D$ a troy ounce (Britain's 1925 "old par"); nothing outside Gold. |
| 6009 | 11 | `public void answerGoldEvent(ForeignAccounts.GoldChoice choice)` | Answers the open gold date (GD3): stay on gold at the mint price, move the mint price with the dollar, or leave gold (the regime goes to Lean, through setCurrencyRegime() so the central bank's aim follows). |
| 6022 | 1 | `public double getGoldCover()` | The gold cover: the vault's gold at the mint price over M0 (ForeignAccounts.goldCover()); infinite with gold and no M0, 0 without gold. |
| 6025 | 1 | `public double getGoldCoverAt(double price)` | The cover the vault's gold would make at this par, D$ an ounce, over M0 (ForeignAccounts.goldCoverAt()): what entering Gold at a staged par would give. |
| 6028 | 1 | `public double getFreeGoldUsd()` | The gold above the law's cover, in US$ at gold's price (ForeignAccounts.freeGoldUsd()); 0 outside Gold. |
| 6031 | 1 | `public ForeignAccounts.GoldEvent getGoldEvent()` | The US$ gold date open for the city to answer; NONE when none is. |
| 6034 | 8 | `public void repeg(double rate)` | Moves the peg (a higher rate is a devaluation); nothing outside a peg. |
| 6044 | 4 | `private String anchorMark()` | The mark the peg's anchor is written with in a decision's line: "US$" for the US$, the money's own mark ("€") for another (WN4b). |
| 6054 | 14 | `public void setPegAnchor(String code)` | Chooses the money the peg is kept in (WN4b): one of the eight, by code ("EUR"); nothing for a code that is none of them or a money with no rate this month. |
| 6070 | 9 | `public void setBand(double centre, double slopeAYear, double width)` | Sets the band: its centre, the log a year it crawls and the share either side its edges sit. |
| 6081 | 8 | `public void setEdgePayer(ForeignAccounts.EdgePayer payer)` | Chooses who pays for the dollars the vault buys at the strong edge. |
| 6102 | 16 | `private void settleTheEdge()` | THE STRONG EDGE'S BILL (XR2, star 6), at the top of the month, inside the audit's window, after the redemptions: the dollars the vault took at the last reprice (ForeignAccounts.edgeBuy()) are paid for here, at the loc... |

### THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) (lines 6119-6228)

| line | len | member | says |
|---:|---:|---|---|
| 6146 | 10 | `public double fillReserve(double tonnes)` | Orders crude for the strategic reserve: `tonnes`, cut to the room its tanks have left and to what the treasury could pay at crude's import price (what no tonne costs more than) - bought in the month's crude market and... |
| 6163 | 6 | `public double reserveFillMost()` | The most a fill order would take now (fillReserve()'s cut): the room the reserve's tanks have left, and what the treasury could pay at crude's import price - the reach of the Fill lever on Oil's page (0.7.96, OilView). |
| 6177 | 12 | `public double releaseReserve(double tonnesAMonth)` | Releases the strategic reserve's crude, `tonnesAMonth` a month from the next clearing on, as long as it holds any; nothing stops it. |
| 6197 | 12 | `private void settleReserve()` | The reserve's month, settled at the strike beside the businesses it traded with (StrategicReserve.settle()): the treasury pays for the crude bought - a promise, the crude having landed - and takes in what the refiners... |
| 6211 | 11 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now - and since 0.7.107 what each zone holds and at what share of the price (BusinessInvestment, THE INVESTORS' GROUND BY ZONE). |
| 6224 | 4 | `private String unlinkedClause()` | The clause the "Could not build" words end with while the city owns ground it cannot build on (ET5): "; 12.0 km² is free on Island 2, which no port links"; empty while it owns none. |

### ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's (lines 6229-6310)

| line | len | member | says |
|---:|---:|---|---|
| 6244 | 1 | `public Zoning getZoning()` | The city's zones and its forest law. |
| 6256 | 12 | `public void setZone(String sector, Zoning.Size kind, double size, double priceShare)` | Zones a sector's ground, or re-sizes or re-prices its zone: `size` a share of the city's dry ground (Zoning.Size.SHARE, 0 to 1) or square kilometres (KM2); `priceShare` 0 to 1 of the going business price, what the nex... |
| 6270 | 4 | `static String zoneSizeWords(Zoning.Zone z)` | A zone's size as the log writes it: "2% of the dry ground", "40 km²". |
| 6276 | 3 | `public void removeZone(String sector)` | Un-zones a sector: what its zone held goes back to everyone; what was sold in it stays with its owner at what it paid. |
| 6281 | 9 | `public void setForestLaw(double protectedShare, double keptShare)` | The forest law (Z6): `protectedShare` of the city's forest ground never cut nor built on, `keptShare` - at least that - never built on. |
| 6292 | 8 | `public void setZoneTax(String sector, Zoning.Tax tax, double points)` | A zone's tax row moved (Z5): the sector's own offset, in rate points - annual points for property - exactly as Policy > Taxes moves it (TaxPolicy's setters, their clamps and their log lines). |
| 6302 | 8 | `public double zoneTaxRate(String sector, Zoning.Tax tax)` | ...and what a zone's tax row reads: the sector's effective rate (TaxPolicy.effective*(), the property rate annual). |

### SHRINKING (lines 6311-6599)

| line | len | member | says |
|---:|---:|---|---|
| 6330 | 82 | `private void runRetirement()` |  |
| 6428 | 28 | `private int retireWornOutWells()` | THE WELLS PAST THEIR LIFE (0.7.84, batch O7; spec-oil 2.6): every land well lifting under ten barrels a day (sectors.Oil.wornOut(): 263 months on land) is retired this month, oldest first, by the path any retired buil... |
| 6467 | 22 | `private int decommissionAtSea(ham.citybuildersim.sectors.Oil wells)` | THE OIL AT SEA DECOMMISSIONED (0.7.91, batch O10): once the city's oil is worked out, every platform's jacket that holds no well, and - with no jacket left standing - every kilometre of pipeline, retired by the path a... |
| 6492 | 1 | `public int getWellsWornOut()` |  |
| 6501 | 15 | `private void closeBranches(int wanted)` | Closes branches (0.7.19: as many as the rule says, at once; one at a time from 0.7.11 round 2 until then): the buildings retired by the path any retired building takes (retire()), sold by their owner, retail. |
| 6519 | 1 | `public int getBranchesClosed()` |  |
| 6532 | 67 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 6600-6649)

| line | len | member | says |
|---:|---:|---|---|
| 6620 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 6632 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 6641 | 1 | `public int getConstructionShedMonth()` |  |
| 6642 | 1 | `public double getConstructionShedPoints()` |  |
| 6645 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 6650-6984)

| line | len | member | says |
|---:|---:|---|---|
| 6670 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 6683 | 3 | `public java.util.Map<String, String> getLandBlockedNeeds()` | Sector key to the ground it asked for this month, for every sector in getLandBlockedSectors(). |
| 6688 | 5 | `private void blockOnLand(String sector, String needs)` | A sector waiting on ground, and what it needs: a land refusal's reason less its "no land - " (BusinessInvestment.landReason()). |
| 6703 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 6723 | 8 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 6733 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 6740 | 3 | `public double getLastWriteOff()` |  |
| 6783 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 6793 | 191 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) (lines 6985-7379)

| line | len | member | says |
|---:|---:|---|---|
| 7106 | 3 | `private static double grownOver(double on, double carry)` | `on` grown by an order's carry; `on` itself where nothing is struck on it (a till that pays, under ON_ITS_BORROWING), so an endless carry never makes NaN of nothing. |
| 7117 | 4 | `public static int deskCallsMost(int quantity)` | The most times an order of this many may ask the bond desk: the countdown's slices, then doubling down, halving and halving again above the first slice that borrows, each at most once a bit of the order, and that slic... |
| 7128 | 1 | **type** `public record Afford(int quantity, boolean atPrime, double firstRate, int deskCalls)` | What consider() decided of an order (0.7.54): the largest slice that carries its interest, 0 for none; whether any slice would have carried it at prime and its record, for the refusal; the rate at the whole order; and... |
| 7131 | 3 | `Afford sliceFor(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | Harnesses only (InvestCheck, 0.7.113; BuildCardCheck 11, 0.7.122): consider()'s answer to an order with `cash` in the till and `perUnitProfit` a building - the largest slice that carries the interest on its whole cost. |
| 7136 | 6 | `private Afford largestSliceThatCarries(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | The largest slice of the order that carries its interest - see THE LARGEST SLICE, WITHOUT COUNTING TO IT. |
| 7144 | 3 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes)` | largestSlice() for a test with no first slice that borrows to look at: the countdown's slices, doubling down and halving. |
| 7166 | 41 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes, java.util.function.IntSupplier borrowsFrom)` | The largest n from 1 to quantity that passes, 0 for none, found as THE LARGEST SLICE, WITHOUT COUNTING TO IT says: the countdown's first COUNTDOWN_SLICES, then doubling down, then halving - and, when the caller names ... |
| 7209 | 70 | **type** `private final class Slices` | One order's slices, each asked as the countdown asked it (until 0.7.54, consider()'s loop). |
| 7218 | 6 | `Slices(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` _(in Game.Slices)_ |  |
| 7226 | 4 | `int firstThatBorrows(int q)` _(in Game.Slices)_ | The first slice the till does not pay for, which largestSlice() looks at (OS1): one more than the largest whose cost the till covers, quantity + 1 when it covers the order, 0 under a ban (no slice that borrows is built). |
| 7232 | 16 | `boolean passes(int n)` _(in Game.Slices)_ | Whether n of them carry the interest on their whole cost (0.7.113); what failed at prime and the rate at the whole order are kept for the refusal. |
| 7250 | 5 | `boolean carriesAtPrime(int n, double on)` _(in Game.Slices)_ | Whether n of them would carry the interest on `on` at prime and the sector's record - their whole cost since 0.7.113 (orderGate()), grown over the wait at that rate since BA1. |
| 7257 | 12 | `void askPrimeOfTheRest(int q)` _(in Game.Slices)_ | When none passes: whether any of the q slices would have carried it at prime - every slice to PRIME_SCAN_SLICES, the four that decide it past that, and under a ban the last the till pays for. |
| 7271 | 5 | `void askPrime(int n)` _(in Game.Slices)_ | Whether n of them would have carried it at prime, as the countdown asked it of a slice that failed. |
| 7277 | 1 | `Afford result(int quantity)` _(in Game.Slices)_ |  |
| 7281 | 1 | **type** `private record Judged(double rate, double tested, boolean askedDesk)` | The rate a slice is judged at: the loan's, and the one tested - a bond's blended with the bank's where one is cheaper and raises it all; and whether the bond desk was asked. |
| 7284 | 25 | `private Judged judged(String sector, double borrowed)` | ...for a slice of `sector`'s that borrows `borrowed`. |
| 7324 | 4 | `public boolean carriesItsWholeCost(String sector, BuildingsTemplate t, int n, double earns, double cost)` | Whether an order by `sector` costing `cost` and earning `earns` a month would carry the interest on its whole cost, as consider() asks each slice (ON ITS WHOLE COST, 0.7.113): it earns something, and at least PROFIT_O... |
| 7342 | 6 | **type** `public record InterestTest(double earns, double cost, double on, double carry, double rate, double real, do...` | carriesItsWholeCost()'s question with its figures (0.7.122, batch C1), for the build card's interest gate (BuildCard, INTEREST), so the card says what consider() would and works none of it out itself: what the order e... |
| 7346 | 1 | `public double needs()` _(in Game.InterestTest)_ | What it must earn a month to carry it: PROFIT_OVER_INTEREST times a month's interest (on the cost grown over the wait, BA1), the bar servicesItsOwnDebt() holds it to. |
| 7350 | 11 | `public InterestTest interestTest(String sector, BuildingsTemplate t, int n, double earns, double cost)` | ...for an order of `n` of `t` by `sector` costing `cost` and earning `earns` a month, its till and what it holds abroad paying first; the cost grown over that order's wait (BA1). |
| 7373 | 1 | `public ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate()` | The rule consider() tests orders by: ON_ITS_WHOLE_COST unless a check or a probe set another. |
| 7376 | 3 | `public void setOrderGate(ham.citybuildersim.sectors.SpreadPlanner.MoneyGate gate)` | A check's or a probe's: tests every order consider() weighs by another rule (the counterfactual); null puts the whole cost back. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 7380-7478)

| line | len | member | says |
|---:|---:|---|---|
| 7437 | 3 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 7442 | 4 | `public boolean buysOnMortgage(String sector, BuildingsTemplate t)` | Whether this sector buys this building on an insured mortgage: a landlord's home (0.7.11) - the rule consider() and financingOf() share. |
| 7459 | 19 | `public double[] financingOf(String sector, BuildingsTemplate t)` | HOW ONE OF THESE WOULD BE PAID FOR (0.7.75, the sector statements' R4): consider()'s split of an order of one, read and never acted on - what it would cost (BusinessInvestment.getCostOf()); what the register would ask... |

### the lender's test for one home, for the build card (I13-L) (lines 7479-7664)

| line | len | member | says |
|---:|---:|---|---|
| 7482 | 2 | **type** `private record LenderReading(java.util.function.IntToDoubleFunction costOf, double insured, double tested, ...` | What the lender reads for an order: what n cost, the insured rate and the real rate it is tested at, one's rent net of carrying it, and Mortgage.decide()'s answer. |
| 7486 | 9 | `private LenderReading lenderReads(BuildingsTemplate t, int quantity, double cash, double perUnitRent)` | The lender's reading of an order of `quantity` with `cash` in hand - the one call the landlords' order (considerOnMortgage()) and the build card (lenderTest()) both make, so they cannot disagree. |
| 7507 | 29 | **type** `public record LenderTest(double cost, double cash, double rent, double noi, double insured, double tested, ...` | THE LENDER'S TEST FOR ONE LANDLORD'S HOME (I13-L; the card's door, as interestTest() is the INTEREST gate's): its cost, the funds the landlord would put in (financingOf(): its till, its money abroad and what its owner... |
| 7511 | 1 | `public boolean passes()` _(in Game.LenderTest)_ | Whether the lender writes it (or the landlord's funds buy it outright). |
| 7514 | 1 | `public boolean outright()` _(in Game.LenderTest)_ | The landlord's own funds cover the whole cost: no lender to ask. |
| 7517 | 1 | `public boolean shortOfDown()` _(in Game.LenderTest)_ | Not even one: the funds do not put the down payment on it. |
| 7520 | 1 | `public double down()` _(in Game.LenderTest)_ | The down payment the lender asks of the landlord's own funds on this cost (Mortgage.ownFundsFor()). |
| 7523 | 1 | `public double loan()` _(in Game.LenderTest)_ | What the lender lends against it: the shortfall the funds leave, once the fee is paid (Mortgage.loanFor()); nothing outright. |
| 7526 | 1 | `public double payment()` _(in Game.LenderTest)_ | The monthly payment the test reads, on that loan with the premium added, the shortfall grown over the wait by the carry (BA1); nothing outright. |
| 7529 | 3 | `public double coverage()` _(in Game.LenderTest)_ | The rent's cover of that payment (net operating income over it), the figure the lender asks asks() of; NaN where it is not read - outright, or short of the down payment. |
| 7534 | 1 | `public double asks()` _(in Game.LenderTest)_ | What the lender asks of that cover: Mortgage.MORTGAGE_DEBT_COVERAGE. |
| 7538 | 9 | `public LenderTest lenderTest(String sector, BuildingsTemplate t)` | The lender's test for one of this landlord sector's homes, as the order would meet it now; null for a building the sector does not buy on a mortgage (buysOnMortgage()). |
| 7556 | 62 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, of what the planner asked for (Mortgage.decide(), which halves since 0.7.54) - and th... |
| 7627 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 7631 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 7644 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 7665-7745)

| line | len | member | says |
|---:|---:|---|---|
| 7703 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 7711 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 7716 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 7721 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 7724 | 21 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 7746-8084)

| line | len | member | says |
|---:|---:|---|---|
| 7759 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 7776 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 7798 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 7803 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 7906 | 1 | `public double getBankedClearedAtLoad()` |  |
| 7909 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 7931 | 27 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 7967 | 5 | `public int getConstructionOutputAtEveryPost()` | What the builders would do this month with every post offered, at the city's fill (0.7.17): the figure getConstructionOutput() was before the builders laid idle crews off. |
| 7974 | 4 | `public int getBuildingOutputAtEveryPost()` | ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. |
| 7987 | 5 | `public double orderWaitOutput()` | The site output an order's wait is read at, for the interest on it (BA1; BusinessInvestment.carryOf(), THE INTEREST ON THE WAIT): the builders' output for the sites after the repairs, every post offered, plus the fore... |
| 8002 | 11 | `void strikeBuildersCrews()` | The builders strike the month's crews (0.7.17): the work ahead - the repairs of the city standing now and every point still owed on site after this month's advance - against what their depots would do with every post ... |
| 8025 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 8042 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 8079 | 5 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |

### THE FOREIGN CREWS (FC1) (lines 8085-8700)

| line | len | member | says |
|---:|---:|---|---|
| 8098 | 4 | **type** `public record ForeignOffer(int points, double depotPoints, double pricePerPoint)` | What the foreign contractor puts into the sites this month, as it was offered before the advance: the points (a whole number, as the sites' output is), how many of them are the builders' own depots' (served first), an... |
| 8109 | 13 | `public ForeignOffer foreignCrewsOffer()` | The contractor's month, offered: its book (cut first to what is still owed on site, Construction.capForeignBook(); a cancelled order left it when it stopped, FC1b) and, while it takes orders, the builders' own depots ... |
| 8130 | 12 | `public void settleForeignCrews(ForeignOffer offer, double pastOwnMonth)` | The contractor's month settled, after the sites advanced: it built what the sites took past the city's own month (`pastOwnMonth`, the points built less the builders' site output), never more than it offered. |
| 8157 | 10 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 8179 | 3 | `double portsBill()` | The sea terminals' repairs, a month: their upkeep, the berths the city keeps as landlord, paid with transit's bill (TreasuryLine.TRANSIT); nothing with no terminal. |
| 8208 | 81 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 8299 | 1 | `public double getCityMaintenancePaid()` |  |
| 8304 | 4 | `public int getConstructionMaterials()` |  |
| 8308 | 4 | `public double getInterestRate()` |  |
| 8312 | 3 | `public boolean isGraphsEnabled()` |  |
| 8315 | 3 | `public boolean isReportsEnabled()` |  |
| 8342 | 110 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 8454 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 8483 | 1 | `public boolean hasNewReceipt()` |  |
| 8484 | 1 | `public void clearReceipt()` |  |
| 8486 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 8500 | 200 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 8588 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 8597 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the bitumen its paving takes, quoted (Markets.quote()), and the refiners' sales tax rate it is credited at (0.7.83). |
| 8606 | 9 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the foothill preparation an order carries (ET2): the sticker already holds prepCost, the money for prepPoints. |
| 8617 | 10 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the works a run's land needs on the city's ground (ET4): the sticker already holds worksCost, the money for worksPoints. |
| 8629 | 12 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the span of a long bridge (ET6): the sticker already holds bridgeCost, the money for bridgePoints. |
| 8643 | 50 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the deep-water works of a floating platform or its well (DW1): the sticker already holds deepWaterCost, the money for deepWaterPoints. |
| 8695 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 8698 | 1 | `public double unitsBeyondYard()` _(in Game.BuildQuote)_ | The material units the crews will draw beyond the yard, which the allowance was priced on. |

### THE BUILDERS' PRICE (0.7.19) (lines 8701-9052)

| line | len | member | says |
|---:|---:|---|---|
| 8757 | 5 | `private String ownerOfOrder(BuildingsTemplate t)` | Who ordered a building of this kind, for a save that did not say (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays for them (BusinessInvestment.planBank()); the city for the rest. |
| 8771 | 6 | `private double recoveredOnOldContract(String payer, BuildingsTemplate t)` | ...and the share of the tax on it that owner gets back, for the same save (revised 0.7.19): on the tax a building of it carries at today's price (EconomyManager.taxRecoveredShare()). |
| 8790 | 18 | `private double depotWageBillPerPoint(boolean today)` | A Construction Depot's posts - the builders' own job mix - at today's wages or at the founding ladder (PayTier, in today's unit), over the points it makes. |
| 8810 | 3 | `private double buildersSalesRate()` | The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. |
| 8813 | 3 | `private double plantSalesRate()` |  |
| 8824 | 5 | `private double billableMaterial(double salvageCost, double plantCost, double importCost)` | What a draw of material cost the builders, as they bill it on (0.7.19): the salvage at what they paid for it, the plant's net of the credit they claim on it, the world's at its landed cost (its tax is charged and cred... |
| 8840 | 3 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 8845 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds)` | ...against a yard holding `yardHolds` units (0.7.40): an order in a run, priced on the yard the orders before it leave (buildRunInvoice()). |
| 8850 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead)` | ...and with `bitumenAhead` tonnes of the refiners' bitumen taken by the orders before it in a run (0.7.83; drawBitumen()). |
| 8855 | 3 | **type** `private record GroundAhead(double ground, double works, double runs, double prepared, java.util.Map<Integer...` | What the orders before one in a run have taken of the ground (ET2): their square feet, the part of it works's (and, ET4, the part runs'), and the foothill their orders prepare (buildRunInvoice()); and (FC1) the points... |
| 8856 | 1 | `GroundAhead(double ground, double works, double runs, double prepared)` _(in Game.GroundAhead)_ |  |
| 8862 | 4 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead, GroundAhead ahead)` | ...and with the ground the orders before it in a run take (ET2): the foothill it prepares is what placing it after them would ask. |
| 8873 | 3 | `public BuildQuote quoteBuildOn(BuildingsTemplate selected, int quantity, int asked)` | What an order for a landmass costs (ET5): the quote of a sea terminal ordered for `asked` - Landmasses.CHOSEN (the card's pick, else the rule), RULE or an id - priced on that landmass's pad and the room it stands on, ... |
| 8877 | 65 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead, GroundAhead ahead, ...` |  |
| 8948 | 4 | `private BuildingsTemplate preparationAnchor()` | The House the foothill's preparation is priced on (ET2; SitePreparation): the template named "House", with points to build it and a founding price. |
| 8957 | 5 | `public double preparationPoints(double sqFt)` | Construction points to prepare `sqFt` square feet of foothill: SitePreparation's dollars a m2 over the House's founding cash cost a point, so the points move with nothing but the work. |
| 8968 | 4 | `public double preparationCost(double points)` | What those points cost the builders' customer, before the builders' tax: the builders' price for house work today - the House's non-material cost over its points (BuildingManager.nonMaterialCost()) - so it keeps up wi... |
| 8982 | 5 | `private void notePreparation(BuildQuote quote)` |  |
| 8989 | 1 | `public int preparationOrders()` | How many orders carried a foothill preparation over this game's life: counted from this game's start, not saved. |
| 8992 | 1 | `public double preparationPointsBilled()` | ...the construction points they owed. |
| 8995 | 1 | `public double preparationMoneyBilled()` | ...and their money, before the builders' tax. |
| 8998 | 1 | **type** `private record RunWorksNeed(double sqFt, double points, double cost, double[] mix)` | What a run's order needs built on the city's slopes (ET4): the land priced, the construction points, their money before the builders' tax, and the shares of {flat, foothill, steep, very steep} they were priced at. |
| 9008 | 14 | `private RunWorksNeed runWorksFor(BuildingsTemplate run, int quantity)` | The cuts, viaducts and tunnels a highway or a railway order needs (ET4; RunWorks, spec-earth2-terrain 4.3): the run's land times the works a m2 needs at the city's own mix of dry ground (RunWorks.usdPerM2OfMix(): the ... |
| 9024 | 4 | `public double runWorksCost(BuildingsTemplate run, int quantity)` | What the cuts, viaducts and tunnels of an order of these would cost, before the builders' tax (runWorksFor()); 0 for every order but a run's on a city with ground above flat. |
| 9038 | 5 | `private void noteRunWorks(BuildQuote quote)` |  |
| 9045 | 1 | `public int runWorksOrders()` | How many orders carried the works of a run over a city's slopes over this game's life: counted from this game's start, not saved. |
| 9048 | 1 | `public double runWorksPointsBilled()` | ...the construction points they owed. |
| 9051 | 1 | `public double runWorksMoneyBilled()` | ...and their money, before the builders' tax. |

### BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) (lines 9053-9251)

| line | len | member | says |
|---:|---:|---|---|
| 9079 | 6 | `public double bitumenFor(BuildingsTemplate t)` | Tonnes of bitumen one of these takes (0.7.83): a road - INFRASTRUCTURE with road capacity - past a Gravel Road's material, its surface's tonnes at BITUMEN_BINDER_SHARE; none for anything else, and none in a catalogue ... |
| 9087 | 3 | `public double bitumenForPaving()` | ...and one gravel road paved (0.7.83): a Paved Road's less a Gravel Road's, which is a Paved Road's - the bed has none. |
| 9092 | 3 | `private double refinersSalesRate()` | The refiners' sales tax rate: the builders' credit on the bitumen they buy from them (0.7.83). |
| 9099 | 1 | `public double getBitumenTonnes()` |  |
| 9100 | 1 | `public double getBitumenCost()` |  |
| 9101 | 1 | `public double getBitumenImported()` |  |
| 9110 | 8 | `public Markets.Draw drawBitumen(double tonnes)` | Draws an order's bitumen (0.7.83): the builders buy it as they buy the material beyond the yard (drawMaterials()), off the refiners' tanks and the rest from the world (Markets.draw()), booked in their ledger and bille... |
| 9128 | 4 | `public double quoteMonths(BuildingsTemplate template, int quantity)` | Months an order would take to finish (0.7.17): the wait every planner reads for its lead time and order size - BuildingManager.waitFor(), the order's points over the share of the site output it would get beside everyt... |
| 9141 | 5 | `public double onSiteMonths(BuildingsTemplate template)` | Months what is on site of one building would take to finish (0.7.20): the same wait at the same output as quoteMonths(), with no order added - BuildingManager.waitOnSite(). |
| 9158 | 4 | `public double siteMonths(String key)` | ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on site would take at today's queue - a stack's by its key, or a demolition's - at the output the quote reads. |
| 9171 | 3 | `public double quoteCityMonths(BuildingsTemplate template, int quantity)` | ...and what a city order of n of these would wait if placed now (0.7.22, after the docs pass): quoteMonths() exactly with no order set and no rush on its site; otherwise where the order would land in the player's orde... |
| 9176 | 4 | `public double quoteCityMonths(BuildingsTemplate template, int quantity, double extraPoints)` | ...with `extraPoints` more owed on its site than its buildings' own (ET2: a foothill pad's preparation, BuildQuote.prepPoints); quoteCityMonths() exactly for none. |
| 9206 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 9234 | 9 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |

### MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) (lines 9252-9345)

| line | len | member | says |
|---:|---:|---|---|
| 9282 | 31 | `public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues)` | Settles the month's site work with the owners who ordered it. |
| 9319 | 7 | `private double deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 9342 | 3 | `public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 9346-9754)

| line | len | member | says |
|---:|---:|---|---|
| 9379 | 3 | `public boolean isCitysToDemolish(BuildingsTemplate t)` | What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). |
| 9384 | 3 | `public boolean isBuyOutable(BuildingsTemplate t)` | What the city may buy out and demolish: a building a sector owns. |
| 9393 | 7 | **type** `public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price, d...` | A demolition, priced and laid out BEFORE the player commits: its work, its price, what it closes, what comes back and when. |
| 9398 | 1 | `public double salvageProceeds()` _(in Game.DemolitionQuote)_ | What the builders would pay for the material today, for as much as their cash covers. |
| 9402 | 8 | **type** `public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground, d...` | A buy-out, priced: the compensation, part by part, and the demolition after it. |
| 9406 | 1 | `public double compensation()` _(in Game.BuyOutQuote)_ | What the owner is paid. |
| 9408 | 1 | `public double total()` _(in Game.BuyOutQuote)_ | ...and what it all costs the city, the demolition included. |
| 9421 | 9 | `private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months)` | D. |
| 9432 | 29 | `public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n)` | What demolishing n standing buildings of this kind would do and cost. |
| 9463 | 16 | `public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell)` | A shell's demolition: DEMOLITION_SHARE of the work it holds, SALVAGE_SHARE of the material it drew to salvage (since 0.7.106; all of it until then), its ground. |
| 9481 | 4 | `private double demolitionMonths(BuildingsTemplate t, int n, double points)` | Months a demolition of these points would take at today's queue, at the output the quote reads. |
| 9494 | 16 | `public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n)` | E. |
| 9513 | 1 | `public String getLastHandRefusal()` |  |
| 9516 | 18 | `public boolean demolish(BuildingsTemplate t, int n)` | D. |
| 9536 | 36 | `public boolean buyOutAndDemolish(BuildingsTemplate t, int n)` | E. |
| 9574 | 21 | `public int demolishable(BuildingsTemplate t)` | How many of this kind stand that no demolition has been ordered for - nor, a gravel road, its paving (0.7.70). |
| 9597 | 6 | `private String linkingWords(BuildingsTemplate t, int n)` | Why a port cannot be demolished, if it is the one that links a landmass (ET5 D11): "it is the port that links Island 2"; null when it is not. |
| 9605 | 4 | `private boolean isBuildersDepot(BuildingsTemplate t)` | Whether this template is a builders' depot: owned by Construction and making building work. |
| 9615 | 10 | `private int otherBuildersDepotsStanding(BuildingsTemplate t)` | How many builders' yards and depots of any other Construction template stand and are not closing: one bought out and ordered demolished this month still stands until the month starts (closeDemolished()), and counting ... |
| 9627 | 17 | `private void placeDemolition(DemolitionQuote q, String from, double groundPaid)` | The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). |
| 9655 | 13 | `private void closeDemolished()` | D. |
| 9670 | 24 | `public boolean demolishShell(int templateId)` | C. |
| 9702 | 16 | `public BuildQuote quoteRestart(ConstructionControl.Shell shell)` | C. |
| 9720 | 4 | `private double restartMonths(BuildingsTemplate t, int n, double pointsLeft)` | A restart's wait, by the one wait (BuildingManager.restartWait()). |
| 9726 | 28 | `public boolean restartShell(int templateId)` | C. |

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) (lines 9755-9930)

| line | len | member | says |
|---:|---:|---|---|
| 9758 | 1 | `private BuildingsTemplate paveFrom()` | The road the city can pave and the road it becomes, as this city's catalogue has them; null for one it has not. |
| 9759 | 1 | `private BuildingsTemplate paveTo()` |  |
| 9762 | 6 | `public int paveable()` | Gravel roads the city could pave now: standing, not ordered demolished, and not already being paved. |
| 9770 | 1 | `public int pavingNow()` | Gravel roads being paved now: their Paved Roads on site, the gravel roads still standing and carrying traffic. |
| 9773 | 4 | `public boolean isPavingSite(String key)` | Whether a site is the Paved Road site with paving on it, which is not stopped (ConstructionControl, F). |
| 9792 | 17 | `public BuildQuote quotePave(int n)` | F. |
| 9822 | 28 | `public boolean paveRoads(int n)` | F. |
| 9852 | 13 | `public boolean cancelSite(String key, boolean cancel)` | C. |
| 9873 | 8 | `public double[] cancelRefundNow(String key)` | C. |
| 9883 | 10 | `public boolean rushSite(String key, boolean on)` | B. |
| 9895 | 11 | `public boolean moveSite(String key, int by)` | A. |
| 9908 | 5 | `public void resetSiteOrder()` | A. |
| 9921 | 9 | `public double[] rushPreview(String key)` | B. |

### in the month (lines 9931-10223)

| line | len | member | says |
|---:|---:|---|---|
| 9940 | 1 | `public ConstructionControl.Events getControlThisMonth()` |  |
| 9941 | 1 | `public double getOvertimePaidThisMonth()` |  |
| 9942 | 1 | `public double getDemolitionSalvageThisMonth()` |  |
| 9949 | 86 | `public void settleConstructionControl(ConstructionControl.Events ev)` | Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. |
| 10037 | 14 | `private double[] overtimeWagesByType(double premium)` | The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). |
| 10071 | 148 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction, int landmass)` | them, and charge nothing for the construction. |
| 10222 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 10224-10537)

| line | len | member | says |
|---:|---:|---|---|
| 10239 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 10251 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 10269 | 1 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 10272 | 1 | `public int wellsCommitted()` | ...and the Oil Wells standing, being built or ordered (0.7.62): one an oil site - since 0.7.91 the land wells and the platforms' wells. |
| 10275 | 1 | `public int landWellsCommitted()` | The land wells standing, being built or ordered (0.7.91): what the dry sites are counted against - every oil well until the platforms' came. |
| 10278 | 1 | `public int platformWellsCommitted()` | ...and the platforms' wells (0.7.91): what the platforms' slots are counted against. |
| 10281 | 12 | `private int committedWhere(java.util.function.Predicate<BuildingsTemplate> which)` | The buildings `which` picks, standing, on site or ordered. |
| 10303 | 3 | `public int committedOn(Resource r)` | The buildings that stand on a resource's sites - standing, on site or ordered (0.7.62): every template whose site is the resource's (siteOf()) - and since 0.7.91 not an offshore platform's jacket, which stands on its ... |
| 10313 | 3 | `public int committedOn(Resource r, Deposit.FieldType type)` | ...and the sites taken by the buildings that stand on one type of the resource's fields (0.7.110, batch I1; spec-materials 2.4): `type` null counts those on ANY - the Iron Mine's until batch I5, none of iron's since (... |
| 10325 | 12 | `public java.util.List<LandManager.Claim> claimsOn(Resource r)` | The works standing on a resource's sites, by template (0.7.124, batch I5; spec-materials 2.4): each one's type (null for ANY), the sites it takes and how many stand, are on site or ordered - the standing claims LandMa... |
| 10346 | 3 | `static boolean packs(BuildingsTemplate t)` | Whether an order for this building is placed on the city's fields by packing (0.7.124, batch I5): a works on the sites of a resource whose fields have types (Resource.hasTypes() - iron, and coal, stone and copper sinc... |
| 10350 | 15 | `private int committedOn(Resource r, Deposit.FieldType type, boolean everyType)` |  |
| 10374 | 10 | `public int committedFor(BuildingsTemplate t)` | What an order for this building is counted against its sites with (0.7.91, batch O10): a land well the land wells, a platform's well the platforms' wells, a platform's jacket the jackets on site (those standing have t... |
| 10396 | 3 | `public static Resource siteOf(BuildingsTemplate t)` | The resource whose sites a building stands on (0.7.62): iron for an Iron Mine, oil for an Oil Well; null for anything else. |
| 10407 | 3 | `public static boolean standsAnywhere(BuildingsTemplate t)` | Whether a building is a works that may stand on any ground (ET2, spec-earth2-terrain 3.2 and 4.4): every template with a site (siteOf()) - the mines, the quarries, the wells and the platforms, a pit and its benches be... |
| 10419 | 5 | `public static boolean isRun(BuildingsTemplate t)` | Whether a building is a run (ET4, spec-earth2-terrain 4.3): a highway or a railway line, laid in a line across the land - the Elevated Highway, and the rail TRACK (the Rail Spur, the Freight Line), not the Rail Termin... |
| 10426 | 3 | `public static LandManager.Use groundUse(BuildingsTemplate t)` | What a template's ground is used for (ET4): a works's if it stands anywhere (standsAnywhere()), a run's if it is a highway or a railway line (isRun()), a building's otherwise. |
| 10436 | 5 | `void pushLandByRelief()` | Hands the books the land office's classes of ground (ET3, spec-earth2-terrain 5.3): the prices of a square foot of foothill and of steep ground (flat's is the land's price, pushed beside it), whether the city owns onl... |
| 10443 | 4 | `private void pushLandUse()` | ...and the ground in use alone, as the month closes (the buildings its orders put up included), the prices staying the ones the month began with. |
| 10466 | 3 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 10476 | 14 | `private boolean hasDepositFor(BuildingsTemplate template, int quantity, int before, java.util.List<LandManager.Claim> ahead)` | ...with `before` more on the same sites already put on site by the orders ahead of it in a run (0.7.40, buildRunAhead()) - and since 0.7.124 a packed works with `ahead`, the claims those orders make on its resource's ... |
| 10499 | 9 | `public double remainingFor(BuildingsTemplate template)` | What is left in the ground for a building that stands on a resource's sites (siteOf()): its resource's remainder - and since 0.7.93 (THE TWO OIL POOLS, LandManager) a land well's the ground pool's, a platform's jacket... |
| 10524 | 13 | `public int sitesFor(BuildingsTemplate template)` | The sites the city owns that a building may stand on (0.7.84, batch O7; spec-oil 2.6): its resource's sites (siteOf()) - and for a land well (sectors.Oil.isLandWell()) only the dry ones, a field whose centre is on lan... |

### THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) (lines 10538-10598)

| line | len | member | says |
|---:|---:|---|---|
| 10563 | 3 | `public double getFreshRights()` | The city's water rights, units a month (see THE FRESH WATER LIMIT AND THE COAST). |
| 10568 | 3 | `void setFreshRights(double units)` | Harnesses and the load only: sets the rights, never below nothing. |
| 10573 | 3 | `public double getFreshCap()` | The fresh water limit, units a month: what the owned lakes and river yield plus the rights. |
| 10585 | 8 | `double derivedFreshRights()` | The rights an older save is given (spec-land 2.9): the fresh plants' nameplate standing less what the city's lakes and river yield, never below nothing - raised by the last unit in the last place when the limit they m... |
| 10595 | 3 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity)` | Whether this order can stand on the city's coast: true for everything but a desalination plant and a sea terminal (0.7.86), which need owned sea. |

### THE PORT'S LANDMASS (ET5, 2026-10-25; the ET5 design, D10) (lines 10599-10617)

| line | len | member | says |
|---:|---:|---|---|
| 10610 | 7 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity, int asked)` | Whether this order can stand on the city's coast, on the landmass asked for (Landmasses.CHOSEN, RULE or an id). |

### THE HYDRO STATION'S RIVER (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.2 and 1.3) (lines 10618-10644)

| line | len | member | says |
|---:|---:|---|---|
| 10628 | 3 | `public int hydroSlots(BuildingsTemplate t)` | How many stations of this template the city's linked hydro sites hold in all (HydroSites.slots()); 0 for a template that is not a hydro station. |
| 10633 | 1 | `public int hydroCommitted()` | The hydro stations standing, on site or ordered: what the slots are counted against. |
| 10636 | 3 | `public int hydroSlotsFree(BuildingsTemplate t)` | The slots left for this template: the slots less the stations committed, never below nothing - a site lost never stops a station standing, it only holds new orders. |
| 10641 | 3 | `public boolean hasRiverFor(BuildingsTemplate t, int n)` | Whether this order has a river: true for every building but a hydro station, whose order of n needs n slots free. |

### THE LONG BRIDGES' CROSSING (ET6, 2026-10-26; BridgeSurvey, BridgeWorks, Bridges) (lines 10645-10760)

| line | len | member | says |
|---:|---:|---|---|
| 10663 | 1 | **type** `public record BridgeSpan(double lengthM, double usd, double points, double cost)` | What a long bridge order's span is: the crossing's length (metres, one bridge's), the span's dollars at the founding for the whole order, the construction points that is, and their money before the builders' tax, at t... |
| 10673 | 4 | `public void setBridgeGroundForTest(BridgeSurvey.Ground ground)` | Harnesses only: hands the survey a ground of the harness's making in place of the World and the land (null restores the real one), so that a crossing can be made to exist where the map holds none. |
| 10678 | 5 | `private BridgeSurvey.Ground bridgeGround()` |  |
| 10685 | 7 | `public java.util.List<Landmasses.Row> bridgeChoices(BuildingsTemplate t)` | The landmasses the card offers a bridge to be ordered toward: those the city owns but home, and home alone where it owns no other; empty for a building that is no bridge. |
| 10694 | 3 | `public void chooseBridgeLandmass(BuildingsTemplate t, int landmass)` | The card's pick of the far landmass for the next order of this bridge (ONE order's: spent by it, forgotten at the month's settle); a pick that is not among the choices is passed over by the rule. |
| 10699 | 9 | `public int bridgeLandmass(BuildingsTemplate t)` | The landmass this bridge's order is for now: the card's pick if it is among the choices, else the rule - the first landmass no port links, else the first that is not home, else home. |
| 10715 | 18 | `public BridgeSurvey.Result bridgeSurvey(BuildingsTemplate t)` | The crossing this bridge's next order would be built over (BridgeSurvey): the shortest chain of fresh or shallow water from the city's own dry ground on the far landmass to its own buildable ground on a linked one, or... |
| 10735 | 5 | `public boolean hasBridgeFor(BuildingsTemplate t, int n)` | Whether this order has a crossing: true for every building but a bridge, whose order needs the survey to find one (before land and money: no bond for an impossible bridge). |
| 10747 | 3 | `public BridgeSpan bridgeSpan(BuildingsTemplate t, int quantity)` | What the span of an order of these costs (BridgeWorks): the surveyed crossing's dollars for each bridge of the order, as construction points at the template's own founding price a point, and their money at the builder... |
| 10751 | 9 | `private BridgeSpan bridgeWorksFor(BuildingsTemplate t, int quantity)` |  |

### DEEP WATER'S WORKS (DW1, 2026-10-31; sectors.Oil's DEEP WATER) (lines 10761-11155)

| line | len | member | says |
|---:|---:|---|---|
| 10772 | 1 | **type** `public record DeepWater(double depthM, double factor, double points, double cost)` | What the deep-water works of an order are: the sea the first unit stands in (metres), the price factor against a jacket's, the construction points of the premium, and their money before the builders' tax at the builde... |
| 10780 | 5 | `public DeepWater deepWater(BuildingsTemplate t, int quantity)` | The deep-water works of an order of these: (factor - 1) times the template's construction points for each unit, their money at the builders' price a point today (the template's non-material cost over its points, so it... |
| 10786 | 5 | `private DeepWater deepWaterAt(BuildingsTemplate t, int quantity, double depthM, double factor)` |  |
| 10793 | 1 | `public Bridges getBridges()` | The city's long bridges (ET6): each one built or building, where it runs and whether it stands. |
| 10799 | 19 | `private Bridges.Stock bridgeStock()` | What Bridges reads of the buildings (ET6): the counts of each bridge's template - standing, on site, stopped. |
| 10824 | 7 | `private int islandPortLandmass(BuildingsTemplate t, int asked)` | The landmass a sea terminal's order stands on when it is an island no port links (so that it takes that island's own room, not the linked pool's), else Landmasses.NO_LANDMASS: for every other building, for a city on o... |
| 10833 | 4 | `private int portLandmassOf(BuildingsTemplate t, int asked)` | The landmass a sea terminal's order stands on, linked or not: home for a city on one landmass; NO_LANDMASS for a building that is no sea terminal or when none will do. |
| 10839 | 3 | `public void choosePortLandmass(BuildingsTemplate t, int landmass)` | The card's pick of a landmass for the next order of this sea terminal (ONE order's: spent by it, forgotten at the month's settle); the order then stands there if the city owns shore on it (hasCoastFor()). |
| 10844 | 3 | `public java.util.List<Landmasses.Row> portLandmassChoices(BuildingsTemplate t)` | The landmasses the card offers a sea terminal: those the city owns shore on, home first; empty for a building that is no sea terminal and for a city on one landmass. |
| 10849 | 3 | `public double freeGroundFor(BuildingsTemplate t)` | The ground this building's order has to stand on, square feet: an island's room for a sea terminal ordered for an island no port links, else what its sector may build on (LandManager.freeFor()). |
| 10854 | 5 | `public double freeGroundFor(BuildingsTemplate t, int asked)` | ...for the landmass asked (Landmasses.CHOSEN, RULE or an id): the room an order placed there stands on, as quoteBuildOn() reads it. |
| 10865 | 3 | `public boolean isIslandPortOrder(BuildingsTemplate t)` | Whether this building's next order is a sea terminal for an island no port links (ET5): the order that takes that island's own room (freeGroundFor()), not the linked pool's, and that the not-enough-land page measures so. |
| 10874 | 14 | `public java.util.List<CityMap.BridgeRun> getBridgePicture()` | The long bridges that stand, as the map is handed them (ET6; CityMap's LONG BRIDGES ON THE MAP): each its kind and its path's corner plots, the two landings first and last; null while none stands - every generated wor... |
| 10890 | 3 | `public Landmasses.Picture getLandmassPicture()` | The city's ground by landmass and its linked set as the map is handed them; null while the city owns nothing off its home landmass (Landmasses.picture()). |
| 10895 | 41 | `private Landmasses.Stock portStock()` | What Landmasses reads of the buildings (ET5): the counts of each sea terminal's template - standing, on site, stopped, being demolished. |
| 10946 | 106 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 11062 | 1 | `public Investor getGovernmentInvestor()` |  |
| 11065 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 11067 | 3 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 11077 | 63 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction, int asked)` | ...for a landmass (ET5): a sea terminal stands on `asked` - Landmasses.CHOSEN (the card's pick, else the rule), RULE (the rule alone: the automatic builder never links an island) or an id - resolved once here, after t... |
| 11141 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 11152 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |

### A RUN OF ORDERS (0.7.40) (lines 11156-11344)

| line | len | member | says |
|---:|---:|---|---|
| 11196 | 37 | `public double buildRunInvoice(java.util.Map<BuildingsTemplate, Integer> run)` | What placing these orders in turn would charge altogether: each one's quote on the yard the ones before it leave. |
| 11235 | 3 | `public double buildFundingGap(java.util.Map<BuildingsTemplate, Integer> run)` | What the treasury is short of for the whole run: its invoice less the cash, never below nothing - an overdraft counted in full, as one order's buildFundingGap(template, quantity) counts it. |
| 11240 | 3 | `public int buildRunAhead(java.util.Map<BuildingsTemplate, Integer> run)` | How many of the run's orders, from the first, would pass the checks money cannot fix - ore, the coast, the river (HY1), licences, ground - each placed after the ones before it. |
| 11245 | 5 | `public BuildResult buildRunStop(java.util.Map<BuildingsTemplate, Integer> run)` | What the first order that would not go ahead is refused for - NO_DEPOSIT, NO_COAST, NO_RIVER, NO_BRIDGE (ET6), NO_LICENCE or NO_LAND - or SUCCESS when every order passes. |
| 11251 | 59 | `private int runAhead(java.util.Map<BuildingsTemplate, Integer> run, BuildResult[] stop)` |  |
| 11312 | 7 | `static int sitePool(BuildingsTemplate t)` | The sites an order is counted against in a run (0.7.91): its resource's (siteOf()), a platform's well and a platform's jacket each a pool of their own after the resources; -1 for none. |
| 11321 | 3 | `public double cashShortfall()` | What the treasury is overdrawn by right now (0.7.40): the cash below nothing, or nothing - Finances' "overdrawn by" ask. |
| 11326 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). |
| 11329 | 3 | `public double getTotalBuildingCost()` |  |
| 11332 | 3 | `public String getBuildingName()` |  |
| 11335 | 3 | `public int getBuildQuantity()` |  |
| 11340 | 3 | `public int getReceiptSerial()` | Which receipt this is. |

### THE CITY'S PAPER, SOLD AT AUCTION (CD2, spec-city-debt.md section 4) (lines 11345-11940)

| line | len | member | says |
|---:|---:|---|---|
| 11387 | 3 | `public Auction.Bidders auctionBidders()` | The bidders as they stand, for a sale of paper of any term: the figure the screen's chart of the bids reads. |
| 11424 | 3 | `public Auction.Bidders auctionBidders(int months)` | The bidders as they stand for paper of this many months (0 for none in particular): each reading the model, none holding anything of its own. |
| 11434 | 41 | `public Auction.Bidders auctionBidders(int months, double face)` | ...for a sale of this face, which only matters to a bank not yet capitalised: it then stands as the residual buyer for the whole of the face (below). |
| 11477 | 3 | `private Auction.Result runAuction(Auction.Kind kind, int months, double face, double lineCoupon, Auction.Cap cap)` | The auction for this paper, now: fair with the issue on the books, the bidders as they stand, the cap given (the default if none). |
| 11493 | 45 | `private Auction.Result runAuction(Auction.Kind kind, int months, double face, double lineCoupon, Auction.Cap cap, int firstCall...` | ...of a bond that may be called (CD4): firstCallAhead is the month, counted from now, the city may first call it - under 0 for paper that cannot be. |
| 11550 | 3 | `private boolean isTinySale(Auction.Result r)` | A sale that is no sale (CD2): the bids fell short of the issue at the city's cap and what they did buy is under the smallest deal worth arranging (minimumIssueSize()), or the fixed fee eats all it raises. |
| 11561 | 3 | `private static DebtQuote tinySaleQuote(String instrument, int duration, double requested, double before, Auction.Result r)` | The quote of a tiny sale: empty - no face, no cash, nothing to book, which every caller already reads as nothing to be had - and carrying the auction's result, so its summary can say what the bids would have bought an... |
| 11566 | 3 | `private static DebtQuote emptyQuote(String instrument, int duration, double before)` | A quote of nothing: the standing rate before and after, no face. |
| 11579 | 3 | `private static double cents(double v)` | Money to the cent, as every proceeds figure has been (the cent of today's money, at the founding's price level). |
| 11600 | 36 | `private DebtQuote solveForCash(double cash, double rounding, java.util.function.DoubleFunction<DebtQuote> at)` | The smallest face whose proceeds cover `cash`: a first face at par and the fee, then as many granules more as the shortfall needs at what a unit of that sale banked, until it covers - hard bounded (64 turns), because ... |
| 11638 | 3 | `private static boolean covers(DebtQuote q, double cash)` | A quote that raises the cash and sold what it was asked to: the test the solver's descent keeps. |
| 11649 | 3 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a bill, sold at auction for this much cash, would cost. |
| 11654 | 9 | `public DebtQuote quoteTBill(double amount, int duration, double rounding, Auction.Cap cap)` | ...with the highest yield the city will take, a cap of the caller's. |
| 11665 | 8 | `private DebtQuote billQuote(double requested, int months, double face, double before, Auction.Cap cap)` | The bill's quote at a face: the auction run on it, what it sold and raised. |
| 11681 | 3 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a bill on exactly the terms quoted. |
| 11686 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11699 | 8 | `private String issueNote(DebtQuote quote)` | The booking every bill shares - handleTBillLogic()'s, and the rollover's (issueForRollover()). |
| 11714 | 7 | `private void bookProceeds(DebtQuote quote)` | The treasury is paid what a sale at home raised, and the market owes it until the settle: the counters the settle and the audit read. |
| 11730 | 5 | `private void bookNewPiece(Debt paper, DebtQuote quote)` | A new piece, sold: told what it raised and at what yield, given its allotment, and the treasury paid. |
| 11743 | 3 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What an amortizing bond, sold at auction for this face, would cost. |
| 11748 | 10 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11760 | 12 | `private DebtQuote mediumQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The amortizing bond's quote at a face: the auction run on it, the coupon its stop sets, the cost of the credit over the schedule. |
| 11778 | 3 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books an amortizing bond on exactly the terms quoted. |
| 11783 | 6 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11791 | 8 | `private String issueMediumBond(DebtQuote quote)` | The booking an amortizing bond shares: a new line at the quote's coupon. |
| 11810 | 3 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a bond, sold at auction for this face, would cost. |
| 11815 | 3 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11830 | 13 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...sold callable if asked (CD4, spec-city-debt.md section 2.6): a bond of ten years or more with the city's right to call it whole at 100 from its first call date (CallTree.firstCallYears()). |
| 11853 | 7 | `public double callSpread(DebtQuote quote)` | What the call adds to a callable sale's yield (CD4, spec-city-debt.md section 2.6): the price of the option on the tree, which runAuction() puts on the fair yield of the same bullet without it - read off the quote's o... |
| 11862 | 3 | `private DebtQuote bondQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The bond's quote at a face: the benchmark line it reopens, or the new line it opens, and what the auction made of it. |
| 11867 | 22 | `private DebtQuote bondQuote(double requested, double face, int years, double before, Auction.Cap cap, boolean callable)` | ...of paper that may be called, if it is (CD4): priced from the months the line, or the new line, has to its first call. |
| 11891 | 3 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a bond on exactly the terms quoted. |
| 11896 | 3 | `public String handleLongBondLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11901 | 8 | `public String handleLongBondLogic(double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...sold callable if asked (quoteLongBond()): refused with CallTree.REFUSAL for a term that may not be. |
| 11916 | 24 | `private String issueLongBond(DebtQuote quote)` | The booking both bond issues share. |

### A BOND SIZED TO THE CASH IT BRINGS (0.7.10, CD2) (lines 11941-11995)

| line | len | member | says |
|---:|---:|---|---|
| 11967 | 3 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a bond whose CASH covers cashNeeded would cost. |
| 11972 | 9 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11983 | 3 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a bond sized to the cash, on exactly the terms quoted - the build screen's bond. |
| 11988 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |

### THE FLOATING-RATE NOTE AND THE INFLATION-LINKED BOND (CD5, (lines 11996-12157)

| line | len | member | says |
|---:|---:|---|---|
| 12019 | 3 | `public DebtQuote quoteFloatingNote(double amount, int duration, double rounding)` | What a floating-rate note, sold at auction for this face, would cost. |
| 12024 | 10 | `public DebtQuote quoteFloatingNote(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 12036 | 10 | `private DebtQuote floatingQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The floater's quote at a face: the auction run on it, the margin its stop sets, the monthly coupon at today's dial. |
| 12048 | 3 | `public String handleFloatingNoteLogic(double amount, int duration, double rounding)` | Books a floating-rate note on exactly the terms quoted. |
| 12053 | 7 | `public String handleFloatingNoteLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's: refused with FloatingRateNote.REFUSAL for any term but 2 or 5 years. |
| 12062 | 9 | `private String issueFloatingNote(DebtQuote quote)` | The booking a floating-rate note shares with the rollover: a new line at the margin the quote carries, the policy rate now its first index. |
| 12073 | 3 | `public DebtQuote quoteFloatingNoteForCash(double cashNeeded, int duration, double rounding)` | What a floating-rate note whose CASH covers cashNeeded would cost: the quote at the face that brings it (solveForCash()). |
| 12078 | 9 | `public DebtQuote quoteFloatingNoteForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 12089 | 3 | `public DebtQuote quoteLinkedBond(double amount, int duration, double rounding)` | What an inflation-linked bond, sold at auction for this face, would cost. |
| 12094 | 10 | `public DebtQuote quoteLinkedBond(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 12106 | 10 | `private DebtQuote linkedQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The linker's quote at a face: the auction run on it, the real coupon its stop sets, the monthly coupon on the face as the index stands. |
| 12118 | 3 | `public String handleLinkedBondLogic(double amount, int duration, double rounding)` | Books an inflation-linked bond on exactly the terms quoted. |
| 12123 | 7 | `public String handleLinkedBondLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's: refused with InflationLinkedBond.REFUSAL for any term but 10 or 30 years. |
| 12132 | 9 | `private String issueLinkedBond(DebtQuote quote)` | The booking an inflation-linked bond shares with the rollover: a new line at the real coupon the quote carries, the price index now its base. |
| 12143 | 3 | `public DebtQuote quoteLinkedBondForCash(double cashNeeded, int duration, double rounding)` | What an inflation-linked bond whose CASH covers cashNeeded would cost: the quote at the face that brings it (solveForCash()). |
| 12148 | 9 | `public DebtQuote quoteLinkedBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 12158-12575)

| line | len | member | says |
|---:|---:|---|---|
| 12203 | 14 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 12241 | 11 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 12267 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation - expected, since 0.7.42 (below) - less the world's rate (the US's policy rate where the city has the world file... |
| 12280 | 5 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the inflation savers expect (Expectations, since 0.7.42 - the same expected inflation the real rate differential reads; the year's, as the parity ... |
| 12287 | 3 | `public double realPolicyRate()` | The policy rate less the inflation people expect (0.7.45): the dial in real terms, ex ante as every real rate here is since 0.7.42. |
| 12299 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 12304 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 12307 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 12322 | 3 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bill or bond of this size would cost. |
| 12335 | 47 | `public DebtQuote quoteForeign(String type, String money, double requested, int duration, double rounding)` | ...in any money of the eight (CD8, Jerus 2026-10-23): the same quote on THAT money's curve - its central bank's policy rate (the world's price of money in it, DebtManager.worldRate(code)) plus the city's country premi... |
| 12384 | 3 | `private static boolean isFloatOrLinked(String type)` | True for the two instruments that are written at home only (CD5): the floating-rate note and the inflation-linked bond. |
| 12396 | 4 | `private static double dollarCoupon(YieldCurve world, int months)` | A foreign bond's coupon on a world curve: its par yield at the term, rounded down to Auction.COUPON_STEP, and not under DebtManager.MIN_FOREIGN_COUPON (CD9, 2026-11-08): a yield under zero is paid in the price, the bo... |
| 12419 | 12 | `private YieldCurve selfPricedWorldCurve(String code, double face, int months, java.util.function.Function<YieldCurve, Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT ON THE WORLD'S CURVE (CD1; the walk of 0.7.2 on a single rate before it): the world's price of paper that is priced payment by payment. |
| 12436 | 4 | `private static Debt inMoney(Debt shape, String code)` | A piece not yet booked, told the money it is written in (CD8) so the world's quote prices it at that money's rate: the shape of selfPricedWorldCurve(). |
| 12442 | 3 | `private double issueCostIn(String code, double face)` | The fixed cost of an issue (costOfIssuance(), local money) in the money the paper is written in: over localPer(code), the US$ included - localPer("USD") is the D$'s rate (CD9, 2026-11-08; the US$ took the local figure... |
| 12452 | 4 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 12463 | 88 | `public String handleForeignLogic(String type, String money, double requested, int duration, double rounding, boolean holdAsRese...` | ...in any money of the eight (CD8): the piece is booked in {@code money} (Debt.getMoney()), opens its books and notes its sale in that money's units, and what it raised reaches the treasury in local money at that mone... |
| 12558 | 3 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 12563 | 12 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's (CD2). |

### THE AMORTIZING BOND AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 12576-12661)

| line | len | member | says |
|---:|---:|---|---|
| 12600 | 3 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | An amortizing bond whose CASH covers cashNeeded: the quote at the face that brings it. |
| 12605 | 9 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 12620 | 3 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note or bond whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 12625 | 4 | `public DebtQuote quoteForeignForCash(String type, String money, double cashNeeded, int duration, double rounding)` | ...in any money of the eight (CD8): the cash needed and the face are in that money's units, the quote on its curve (quoteForeign(type, money, ...)). |
| 12631 | 4 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |
| 12637 | 9 | `public String handleForeignForCash(String type, String money, double cashNeeded, int duration, double rounding, boolean holdAsR...` | ...in any money of the eight (CD8). |
| 12648 | 4 | `private static String noLenderIn(String code)` | What the treasury is told when it asks to sell paper in a money the world has no rate for this month (CD8). |
| 12654 | 7 | `public double minimumIssueSizeIn(String money)` | The smallest deal worth arranging in one money's own units (CD8): minimumIssueSizeUsd() over its US$ per unit - the US$ figure itself for the US$. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 12662-14448)

| line | len | member | says |
|---:|---:|---|---|
| 12750 | 1 | `public java.util.List<String> getRolloverShortNotes()` | The rollover's sales that came up short this month, one sentence each; empty when none did. |
| 12756 | 1 | `public java.util.List<String> getRolloverTailNotes()` | The rollover's sales that cleared past the default cap this month, one sentence each; empty when none did. |
| 12766 | 1 | `public java.util.List<TimeSkipReport.AutoSale> getAutoSalesThisMonth()` | The month's automatic sales, in the order they were made. |
| 12769 | 3 | `void noteAutoSale(TimeSkipReport.AutoSale sale)` | Automatic building enters its bond sales here as it makes them. |
| 12774 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 12777 | 1 | `public Auction.Cap getRolloverCap()` | The cap its auctions take (CD2, D28), as the rollover card's chips read it. |
| 12780 | 7 | `public void setRolloverCap(Auction.Cap cap)` | ...and as they set it, applied at the next press; the change is a decision. |
| 12789 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 12792 | 11 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 12814 | 9 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 12825 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, String money, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not and in which money (null at home), and whether it is foreign paper rolled at home. |
| 12827 | 30 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 12859 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 12873 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 12944 | 61 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 13014 | 3 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; an amortizing bond (the serial until CD2) by quoteMediumBond... |
| 13019 | 13 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, String money, double cash)` | ...abroad in a money of the eight (CD8): the share in that money's units at the day's rate (localPer()), the quote on its curve. |
| 13034 | 1 | **type** `private record RolloverSale(Rollover.Issue issue, DebtQuote quote, double brought)` | One of the rollover's issues sold at auction, with the cash it brought. |
| 13037 | 9 | `private static String saleName(Rollover.Issue issue)` | An issue in the words of a sale: "10-year bond", "6-month bill", "10-year amortizing bond". |
| 13064 | 36 | `private void noteRolloverSales(java.util.List<RolloverSale> sales, Rollover.Plan plan)` | WHAT THE ROLLOVER'S SALES DID, said (CD2, D29). |
| 13102 | 4 | `private double localFace(boolean abroad, String money, DebtQuote quote)` | A rollover quote's face in local money: a foreign quote's at the day's rate for its money (localPer(); the D$'s rate to the US$ for a dollar quote). |
| 13113 | 22 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 13138 | 3 | `private void printPopulationInfo()` | printers |
| 13143 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 13156 | 3 | `private void printUtilityInfo()` |  |
| 13160 | 3 | `private void printCityStats()` |  |
| 13167 | 946 | `private void nextMonth()` |  |
| 14114 | 306 | `private void startOfMonthUpdate()` |  |
| 14420 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 14449-14560)

| line | len | member | says |
|---:|---:|---|---|
| 14506 | 4 | `private double issuanceFee()` | The fee in today's money: founding / unit, at the expected price level since 0.7.42 (THE ANCHOR, beside restrikeMoneyConstants()). |
| 14530 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 14548 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |
| 14557 | 3 | `public double minimumIssueSizeUsd()` | ...in US dollars at today's rate (0.7.40): Finances' "the minimum" on the ask abroad, which is asked in dollars. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 14561-14923)

| line | len | member | says |
|---:|---:|---|---|
| 14575 | 16 | `public String getCreditRating()` |  |
| 14593 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 14616 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 14630 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 14648 | 5 | `void strikeTheCurve()` | THE CURVE'S EXPECTATIONS (CD1, spec-city-debt section 3.1): the inflation the rule reads - the year's, or the target before there is one - and the inflation the city expects to settle at, handed to the debt market, wh... |
| 14668 | 54 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 14723 | 108 | `private void finalUpdateEconomy()` |  |
| 14834 | 5 | `private double prizesOwedToLabs()` | The labs' share of the month's prizes, the sum of Research.toLabs() over the fields (R4): what the finds of the month owe the Research Labs that worked them. |
| 14841 | 9 | `private void payPrizes()` | The labs' share of the month's prizes, paid to Business Services' till (finalUpdateEconomy(); R4): the sum of Research.toLabs() over the fields, through the prizes' line. |
| 14857 | 5 | `private void owePrizes()` | THE LABS' SHARE IN A MONTH THE BOOKS DID NOT CLOSE (R3b; finalUpdateEconomy()'s guard): the month's cash update is blocked when its figure is not finite, and payPrizes() sat inside it, so a find that month was neither... |
| 14863 | 4 | `private void updateConstructionCost()` |  |
| 14874 | 7 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 14887 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 14891 | 4 | `public long getHouseholdCapacity()` |  |
| 14895 | 4 | `public long getStoreCapacity()` |  |
| 14900 | 5 | `public long[] getJobs()` | Every job the city offers this month by JobType ordinal: its buildings' and the research posts' (R3). |
| 14907 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 14924-15595)

| line | len | member | says |
|---:|---:|---|---|
| 14953 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 14995 | 1 | `public PopulationCohorts getCohorts()` |  |
| 14996 | 1 | `public FamilyModel getFamilies()` |  |
| 14997 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 14998 | 1 | `public Migration getMigration()` |  |
| 14999 | 1 | `public Health getHealth()` |  |
| 15000 | 1 | `public Healthcare getHealthcare()` |  |
| 15024 | 571 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 15596-15608)

| line | len | member | says |
|---:|---:|---|---|
| 15604 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 15607 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 15609-15918)

| line | len | member | says |
|---:|---:|---|---|
| 15625 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 15644 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 15664 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 15706 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 15732 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 15742 | 3 | `public BuildLog getBuildLog()` |  |
| 15749 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 15774 | 1 | `public Unemployment getUnemployment()` |  |
| 15778 | 1 | `public Sickness getSickness()` |  |
| 15782 | 1 | `public CausesOfDeath getCausesOfDeath()` |  |
| 15793 | 4 | `public void holdCausesOfDeathOff()` | Today's deaths, as before R0b: no cause of 1900 kills, the swing stays whole and the influenza takes nobody off work. |
| 15804 | 1 | `public Crime getCrime()` |  |
| 15808 | 1 | `public double getLastOrphanDeaths()` |  |
| 15809 | 1 | `public double getLastUnhousedDeaths()` |  |
| 15811 | 8 | `{ ... }` |  |
| 15821 | 16 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 15847 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 15856 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 15877 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 15888 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 15901 | 1 | `public double getStudentLoansLent()` |  |
| 15902 | 1 | `public double getStudentLoansRepaid()` |  |
| 15903 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 15907 | 1 | `public double getStudentLoanInterest()` |  |
| 15910 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 15917 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 15919-16058)

| line | len | member | says |
|---:|---:|---|---|
| 15936 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 15942 | 7 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 15951 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 15963 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 16007 | 1 | `public Equity getEquity()` |  |
| 16011 | 1 | `public Exchange getExchange()` |  |
| 16019 | 1 | `public BondMarket getBondMarket()` |  |
| 16029 | 1 | `public TreasuryFund getFund()` |  |
| 16032 | 1 | `BondMarket.Readings bondReadings()` | The readings the bond market is attached to, for a harness (CurveCheck, CD1). |
| 16048 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### THE ANCHOR (0.7.42): what the city expects prices to do, and the (lines 16059-16085)

| line | len | member | says |
|---:|---:|---|---|
| 16078 | 1 | `public Expectations getExpectations()` | Expected inflation, credibility and the expected price level. |
| 16081 | 1 | `public double getExpectedInflation()` | What the city expects inflation to be, a fraction a year. |
| 16084 | 1 | `public double getCredibility()` | How far the city believes the central bank, Expectations.KMIN to KMAX. |

### what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) (lines 16086-16215)

| line | len | member | says |
|---:|---:|---|---|
| 16089 | 26 | `double[] indexPrices()` | Each component's price this month, in PriceIndex's order: the shelf, the average rent paid, a meal, a luxury piece; the services' price is struck by the index from the fees. |
| 16117 | 8 | `double[] indexSpends()` | ...what the households spent on each: groceries and rent off the sectors' statements, meals and luxury off their own purchases; the services' is the index's sum of the fee lines. |
| 16132 | 14 | `double[] indexFees()` | ...each fee line's price: the clinic's fee for a general visit at the player's scale, a seat's tuition (the adult kinds' fees averaged - a price, so a shift in who studies what is not inflation), the fare a ride is ch... |
| 16148 | 8 | `double[] indexFeeSpends()` | ...and what the households paid on each fee line, off their books. |
| 16185 | 23 | `void restrikeMoneyConstants()` | Every money constant that prices something, struck at the expected price level: founding / unit x Expectations.getStruckLevel(), the level the last month ended on. |
| 16210 | 5 | `private void handOnExpectations()` | ...and what the month hands on from the anchor that is not a constant: the investors' expected inflation (0 before the basket is based) and - since 0.7.43 - the drift every sticky seller's price carries (Retail.sticky... |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 16216-16260)

| line | len | member | says |
|---:|---:|---|---|
| 16226 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 16236 | 3 | `public Consumption.YearPoint basketYear()` | The household's basket of this month (R0e; research-1900 5.4 item 5): the year's figures in a city founded the 1900 way, null in every other city and in a 1900 one from the last knot's January on, where the basket is ... |
| 16245 | 6 | `private void basketHearsTheYear()` | The homes' lamp and stove shares of the year (R0e): in a city founded the 1900 way HomeFuel's two shares are the year's (0 and 0 once the year is today's), set at the founding, at the top of every month and after a lo... |
| 16256 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 16259 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 16261-16680)

| line | len | member | says |
|---:|---:|---|---|
| 16273 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 16275 | 1 | `public PriceIndex getPriceIndex()` |  |
| 16277 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 16294 | 6 | `double[] rateHistoryToSave()` | The ring and how full it is, for the save (XR1, found by AutoBuildCheck's reload twin): a FLOW, twelve months of rates the state a month ended in cannot give back. |
| 16302 | 7 | `private void restoreRateHistory(double[] saved)` | ...and back: an array of any other length (an older save) leaves the ring empty, as it always loaded. |
| 16311 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 16318 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 16329 | 5 | `public Sectors.TradeByGood getTradeByGood()` | The month across the city's edge, good by good (0.7.35): the businesses' struck statements split by good, an import with no good behind it by the sector that bought it, the households' cars among the cars bought and, ... |
| 16345 | 3 | `public double getOwnReserves()` | What is left of the vault once everything owed against it is taken off: the dollar paper outstanding and the foreign money parked in the bank, both claims on the one pot (0.7.35; the Trade tab's own subtraction until ... |
| 16350 | 5 | `public double getSharesHeldAbroad()` | The city's shares in foreign hands, at each company's price - its last trade, or its fair value before one (0.7.35; the Trade tab's own sum until then). |
| 16364 | 3 | `public double getHeldAbroadPrivately()` | The businesses' and the households' paper abroad, at today's rate: what is held abroad outside the vault. |
| 16369 | 3 | `public double getHeldAbroad()` | ...and the vault with it: everything the city holds abroad. |
| 16374 | 4 | `public double getHeldHereByTheWorld()` | What the world holds here: the city's shares at their price, the businesses' bonds at face, the foreign money parked in the bank, the city's own paper it bought at the auction (CD2, slice 5; at face) and the city's do... |
| 16386 | 1 | `public Bank getBank()` |  |
| 16389 | 1 | `public int getBankBranchesStanding()` | Commercial Banks standing now, counted from the buildings: a founded city has one before its first month opens it (bank.getBranches() is 0 until the month runs). |
| 16402 | 1 | `public CentralBank getCentralBank()` |  |
| 16416 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 16422 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 16425 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 16432 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 16436 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 16440 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 16507 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 16511 | 3 | `public EconomyManager getEconomyManager()` |  |
| 16514 | 3 | `public PopulationManager getPopulationManager()` |  |
| 16518 | 1 | `public LabourMarket getLabourMarket()` |  |
| 16519 | 1 | `public WantedPosts getWantedPosts()` |  |
| 16520 | 1 | `public Education getEducation()` |  |
| 16536 | 21 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 16571 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 16613 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 16631 | 47 | `public void recordMonth()` | Files this month in the graph history. |
| 16679 | 1 | `public Inbox getInbox()` |  |
| 16680 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 16681-16741)

| line | len | member | says |
|---:|---:|---|---|
| 16692 | 3 | `public int getMonthsUntilAutosave()` |  |
| 16715 | 3 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 16720 | 21 | `private SaveWriter.Job autosaveJob(String reason)` | The autosave, returning the job it made (null when the city is not initialised) so the month that autosaves can hand its fill to the recording (TR2). |

### THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) (lines 16742-16831)

| line | len | member | says |
|---:|---:|---|---|
| 16775 | 1 | **type** `private record Written(int slot, GameFiles.Result result)` | A result the writer finished, and the slot it was for (UP6): an autosave's goes to the inbox, the player's Save is told by its page. |
| 16789 | 3 | `void useSaveClockForChecks(java.util.function.LongSupplier nanos)` |  |
| 16798 | 13 | `private void reportSaves()` | Takes what the writer has finished onto the calling thread: each result becomes the last save result, and a failed one is said as an autosave has always said it. |
| 16817 | 4 | `public void awaitSaves()` | Waits for the save writer, then reports what it wrote. |
| 16827 | 4 | `public void save(int slot, String slotName)` | Saves to a slot, now, on the calling thread: the live objects, after any background write has finished. |

### THE PLAYER'S SAVE OFF THE CLOCK (UP6, 2026-11-06) (lines 16832-17294)

| line | len | member | says |
|---:|---:|---|---|
| 16865 | 18 | `public void saveInBackground(int slot, String slotName, java.util.function.Consumer<GameFiles.Result> done)` | Saves to a numbered slot without holding the caller (UP6): filled now, on the calling thread, written on the writer's. |
| 16889 | 3 | `SaveWriter.Job prepareSaveForChecks(int slot, String slotName)` | For SaveSlotCheck (UP1): the save filled as an autosave fills it, the snapshot taken and nothing written, so a check can read what the writer thread would be handed. |
| 16894 | 3 | `DataSave liveSaveForChecks()` | For SaveSlotCheck (UP1): the persistent save the setters fill, to measure what a snapshot shares with it. |
| 16905 | 11 | `private SaveWriter.Job prepareSave(int slot, String slotName, boolean snapshot)` | Fills the save from the city as it stands, asks the map for its bytes and stamps it; everything save() did before it wrote anything. |
| 16923 | 371 | `private byte[] fillSave(String slotName)` | Fills the save from the city as it stands, asks the map for its bytes and stamps it (TR1: split out of prepareSave(), so the recording fills the same save and snapshots it without the history). |

### THE RECORDING (TR1, 2026-10-31) (lines 17295-17669)

| line | len | member | says |
|---:|---:|---|---|
| 17340 | 1 | `public boolean isRecording()` |  |
| 17342 | 1 | `public String getRecordingId()` |  |
| 17345 | 3 | `public Path recordingFolder()` | The recording's folder, or null in a city with no id (or one that is not the shape Recording.newId writes: a hand-edited save cannot point outside recordings/). |
| 17356 | 1 | `public int getMonthsNotRecorded()` | Months the clock left out of the recording because the writer was behind (RECORD_BACKLOG). |
| 17359 | 1 | `long lastRecordNanos()` | For ReplayCheck: the FX-thread cost of the last recorded month, in nanoseconds. |
| 17362 | 1 | `int recordedFromAutosave()` | For ReplayCheck: how many months the recording took from the autosave's fill instead of making a second one (TR2). |
| 17370 | 21 | `public void setRecording(boolean on)` | Switches "Record this city" on or off. |
| 17393 | 4 | `private Recording.Meta recordingMeta()` | The meta of the recording being written: who the city is, and what wrote it. |
| 17403 | 19 | `private void submitRecording(int timeline, SaveWriter.Task task)` | Hands the writer a recording file of {@code timeline} and counts it until it is written (a failure is printed there). |
| 17424 | 16 | `private boolean awaitRecordingRoom()` | Waits, at most RECORD_WAIT_MS, for the writer to get inside RECORD_BACKLOG; true when it has. |
| 17450 | 48 | `private void recordForReplay(SaveWriter.Job autosaved)` | Keeps the month that has just ended, when "Record this city" is on (called by nextMonth(), after the autosave: the counter and every month-end record agree). |
| 17500 | 4 | `private void recordHistoryFrom(SaveWriter.Job job)` | The autosave's history, handed to the recording from the snapshot the save made (autosave()); only once a month of this timeline is on disk. |
| 17506 | 4 | `public void markReplayCopy()` | Makes this game the replay's throwaway copy: recording off, and never on again. |
| 17511 | 1 | `public boolean isReplayCopy()` |  |
| 17518 | 10 | `public boolean replayMap(byte[] sidecar, int month)` | Replay's quick step: the map of a recorded month read from its sidecar and put in place of the city's, nothing else moved. |
| 17530 | 6 | `public void replayHistory(HistorySave h)` | Replay's history: a parsed history put in place of the copy's, as loadHistory() does with the file's, and the partner trade's years with it. |
| 17545 | 5 | `public String takeSkipFailure()` |  |
| 17559 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 17561 | 1 | `public GameFiles getGameFiles()` |  |
| 17580 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 17608 | 18 | `public void sendBuildingSave()` | EVERY ID TO THE HIGHEST, NOT AS MANY AS THERE ARE (FR3, 2026-10-19). |
| 17629 | 17 | `public void loadBuildings()` |  |
| 17660 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 17670-17846)

| line | len | member | says |
|---:|---:|---|---|
| 17697 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 17698 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 17699 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 17706 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 17717 | 8 | `public double localPer(String money)` | Local money per unit of one of the eight (CD8): the D$'s rate to the US$ for the US$ - the vault's, the figure every dollar path has always read - and that times the money's US$ per unit (the vault's cross rate, the d... |
| 17727 | 9 | `public void repayForeignPrincipal(double own, String money)` | A slice of principal repaid, in the money it is written in (CD8: the US$ goes through the one-argument form, as it always did). |
| 17753 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 17760 | 9 | `public void payForeignInterest(double own, String money)` | A coupon, likewise, in the money it is written in (CD8: the US$ goes through the one-argument form, as it always did). |
| 17831 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 17840 | 1 | `public double getCityPaperSettled()` |  |
| 17841 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 17842 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 17844 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 17847-17972)

| line | len | member | says |
|---:|---:|---|---|
| 17904 | 1 | `public double getCouponsToHouseholds()` |  |
| 17905 | 1 | `public double getPrincipalToHouseholds()` |  |
| 17906 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 17907 | 1 | `public double getCouponsToFirms()` |  |
| 17908 | 1 | `public double getPrincipalToFirms()` |  |
| 17909 | 1 | `public double getFirmsBoughtPaper()` |  |
| 17910 | 1 | `public double getCouponsToWorld()` |  |
| 17911 | 1 | `public double getPrincipalToWorld()` |  |
| 17912 | 1 | `public double getWorldBoughtPaper()` |  |
| 17913 | 1 | `public double getPaperBoughtFromWorld()` |  |
| 17924 | 7 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households', the central bank's and (CD2, slice 4) the businesses' and (slice 5) the world's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 17933 | 16 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 17951 | 21 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 17973-18074)

| line | len | member | says |
|---:|---:|---|---|
| 17983 | 17 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |
| 18009 | 17 | `private void desksBuysFirmPaper(double face, double cash)` | THE BANK BUYS THE BUSINESSES' PAPER (CD2, slice 4), for the spread gone, a sector that borrows, or one that wants the money: this much face for this much cash, off every piece's businesses' share pro rata, onto the ba... |
| 18040 | 21 | `private double desksBuysWorldPaper(double face)` | THE BANK BUYS THE WORLD'S PAPER (CD2, slice 5), when the hot money leaves and takes it with it (worldPaperLeavesWithTheMoney()): this much face, off every piece's world share pro rata, onto the bank's book with the un... |
| 18070 | 4 | `private void worldPaperLeavesWithTheMoney(double stockBefore)` | ...AND THE PAPER LEAVES WITH THE MONEY (CD2, slice 5, D7). |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 18075-18258)

| line | len | member | says |
|---:|---:|---|---|
| 18097 | 42 | `private double householdsTakeTheirShare()` |  |
| 18148 | 8 | `private void restrikeBankDiscount(double unearnedBefore, double faceBefore, double bankPaid)` | BK1: books the bank's gain or loss from a line's pooled discount re-blending (Bank.restrikeDiscount()): what its paper gained in face since the snapshot, less what it paid for it, less what its unearned discount rose ... |
| 18170 | 8 | `private void restrikeBankDiscountCarried(double carriedBefore, double bookBefore)` | BD1: books the bank's gain or loss from a line's discount moving the book's figure outside the settle (Bank.restrikeCarriedDiscount()): a reopening at a premium, or the desk handing the bank the whole line's discount ... |
| 18195 | 34 | `private double firmsTakeTheirShare(java.util.List<Debt> settling)` | THE BUSINESSES TAKE THEIR SHARE (CD2, slice 4, D33), at the same settle and from the same allotment (Debt.allotFirmsCash(), the auction's fill for them at the clearing yield). |
| 18246 | 12 | `private double worldTakesItsShare(java.util.List<Debt> settling)` | THE WORLD TAKES ITS SHARE (CD2, slice 5, D7; star 2), at the same settle and from the same allotment (Debt.allotWorldCash(), the auction's fill for the world at the clearing yield - CapitalFlows' rule, the stock it wo... |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 18259-18520)

| line | len | member | says |
|---:|---:|---|---|
| 18331 | 68 | `private void openMarketOperation()` |  |
| 18409 | 28 | `private double buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |
| 18450 | 28 | `private double buyPaperFromFirms(double wanted)` | The rest of a purchase the bank and the households could not fill, from the businesses' term paper (CD2, slice 4): the households' path with the businesses' share in its place - pro rata across the pieces they hold th... |
| 18492 | 28 | `private double buyPaperFromWorld(double wanted)` | The rest of a purchase the others could not fill, from the world (CD2, slice 5): the businesses' path with the world's share in its place - pro rata across the pieces it holds that are settled and pay no principal thi... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 18521-18781)

| line | len | member | says |
|---:|---:|---|---|
| 18626 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 18629 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 18636 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 18648 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 18655 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 18661 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 18674 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 18705 | 76 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 18782-18806)

| line | len | member | says |
|---:|---:|---|---|
| 18800 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 18804 | 1 | `public double getBuybackToHouseholds()` |  |
| 18805 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 18807-19015)

| line | len | member | says |
|---:|---:|---|---|
| 18902 | 18 | `private void takeTreasuryMonth()` |  |
| 18922 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 18924 | 1 | `public double getTreasuryOpening()` |  |
| 18925 | 1 | `public double getTreasuryClosing()` |  |
| 18926 | 1 | `public double getTreasuryRaised()` |  |
| 18927 | 1 | `public double getTreasuryRepaid()` |  |
| 18928 | 1 | `public double getTreasurySurplus()` |  |
| 18931 | 1 | `public double getTreasuryNotCash()` | What the balance charged or booked that the cash did not (CD3, CD7): the discount amortised, less the gain on paper retired and the exchange gain on the foreign debt. |
| 18942 | 1 | `public double getTreasuryCashSurplus()` | The part of last month's surplus that moved cash (SG1): the balance and the bridge's not-cash row, which is the bridge's own identity (surplus + not cash + raised - repaid + the rest = the cash's change). |
| 18945 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 18955 | 3 | `public double getNetPosition()` | The city's net position (0.7.32, the Finances tab's THE BALANCE): its cash - below nothing when it is overdrawn - less the paper it owes and what its central bank has advanced it. |
| 18965 | 3 | `public double getNetPositionAtCarrying()` | The same net position with the paper at what the books carry it at (CD3, spec-city-debt.md 5.4): its face less the discount not yet worn off, plus any premium. |
| 18987 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 18999 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 19004 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 19012 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |

### FROM EARNED TO THE BUDGET (0.7.31) (lines 19016-19161)

| line | len | member | says |
|---:|---:|---|---|
| 19054 | 34 | `public java.util.List<TreasuryJournal.Entry> getEarnedToBudget()` | The steps from EARNED to the budget's balance, in the Government bridge's order, signed as they move EARNED (+ adds, - takes away); every line, at nothing too. |
| 19090 | 5 | `public double getEarnedResidual()` | What the steps leave between EARNED and the budget: the dials moved since the month was struck, and nothing in a month nobody moved one. |
| 19096 | 7 | `double[] treasuryMonthToSave()` |  |
| 19104 | 10 | `void restoreTreasuryMonth(double[] saved)` |  |
| 19139 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 19142 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 19144 | 1 | `public double getPostAuditDriftWorst()` |  |
| 19145 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 19146 | 4 | `public void InterestExpense(double amount)` |  |
| 19151 | 3 | `public DebtManager getDebtManager()` |  |
| 19156 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 19162-19455)

| line | len | member | says |
|---:|---:|---|---|
| 19230 | 49 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 19291 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 19296 | 9 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 19307 | 5 | `private void oweArrears(TreasuryLine line, String payee, double refused)` | Books what a discretionary line refused as owed on its key, and in the month's and the lifetime's refused counters: the one place arrears are made (treasuryPays(); owePrizes() in a month the books did not close). |
| 19321 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 19333 | 19 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 19361 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 19386 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 19393 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 19397 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 19407 | 8 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 19417 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 19420 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 19427 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 19437 | 10 | `public double getArrearsOwedTo(String sectorKey)` | Owed and unpaid to one sector's till (0.7.55): every line payDownArrears() would pay it, by the payee it would pay. |
| 19449 | 1 | `public double getArrearsPaidTo(String sectorKey)` | What the treasury paid this sector's till of its arrears this month (0.7.55) - its statement's arrears line. |
| 19451 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 19452 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 19453 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 19454 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 19456-19740)

| line | len | member | says |
|---:|---:|---|---|
| 19473 | 6 | `public double fairValueOf(Debt debt)` | What one piece is worth to somebody who does not care to hold it, right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on. |
| 19485 | 5 | `public BookDesk bookDesk()` | THE DEBT DESK (CD6): the book page's figures, a line a piece (BookDesk). |
| 19492 | 5 | `public BookDesk.Line bookLine(Debt debt)` | ...and one line of it, for a line's page (CD6): the market struck once, as bookDesk() strikes it, and the line read (BookDesk.line()). |
| 19508 | 3 | `public double quoteRepurchase(Debt debt)` | What clearing the whole of one piece that can be cleared would cost right now (CD4): the reverse auction's price for everything its holders offer, the central bank's face excepted (D18), at any price - the sum of the ... |
| 19520 | 3 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative) from clearing what quoteRepurchase() clears: the carrying value of that face less the price (CD3) - what the books hold it at against what buying it bac... |
| 19525 | 5 | `public double buybackOfferable(Debt debt)` | The face of a piece its holders can offer: the principal less the central bank's, which does not sell (D18); a dollar piece's all of it, the world's alone (D26). |
| 19554 | 6 | `public Buyback.Result quoteBuyback(Debt debt, double face, double maxPer100)` | THE REVERSE AUCTION, QUOTED (CD4, spec-city-debt.md section 6.2): what buying back this much face of one piece, at no more than this per 100, would take and cost, struck once on the market as it stands - nothing is mo... |
| 19566 | 23 | `public Buyback.Result quoteBuybackAtRest(Debt debt, double face, double maxPer100)` | The same CD4 quote on the market already struck, without repricing it (GV1). |
| 19597 | 7 | `public Buyback.HonestLine honestLine(Debt debt, Buyback.Result q)` | THE HONEST LINE (CD6, spec-city-debt.md section 6.3), struck from a quote: a buyback's gain is real on the statement, but the cash to pay for it costs today's rate. |
| 19606 | 3 | `private static boolean takesTheWholePiece(double taken, double principal)` | True when this much face is the whole of a piece's principal: all of it but the dust that slices of a holding leave. |
| 19635 | 95 | `public Buyback.Result buyBack(Debt debt, double face, double maxPer100)` | Buys one bond back, in whole or in part, and takes what it bought off the books: the reverse auction of quoteBuyback(), struck and paid. |
| 19737 | 3 | `public double repurchaseDebt(Debt debt)` | Buys back everything the holders offer of one bond, at any price: the thin form of buyBack() the buttons before CD4 knew. |

### the call (CD4, spec-city-debt.md section 6.4; D22, D23, D24) (lines 19741-19882)

| line | len | member | says |
|---:|---:|---|---|
| 19754 | 3 | `public double rateVolatility()` | The volatility of the city's own short rate, a year (D23): the standard deviation of the monthly changes of History's "interestRate" series over the last CallTree.VOL_MONTHS, times the square root of twelve, never und... |
| 19765 | 7 | `public boolean canCall(Debt debt)` | True when this piece can be called now: a callable bond of the city's own, on the books, whose first call month has come (LongTermBond .isCallableNow()) and which has nothing unsettled - a line sold last month has buy... |
| 19774 | 3 | `public double callCost(Debt debt)` | What calling this piece costs: its outstanding principal, 100 per 100 (CallTree.CALL_PRICE), paid to every holder, the central bank's too. |
| 19784 | 3 | `public double callGain(Debt debt)` | What calling the line books as a gain (a loss below nothing) against its carrying value (CD6): what it is carried at less callCost() - the figure callLine() adds to the month's gain on debt retired (NationalAccounts.g... |
| 19795 | 5 | `public double callSaving(Debt debt)` | The present value a call would save (D24): the line's remaining payments at today's curve, as a bond that cannot be called, less what the call pays and less what selling the paper again costs - the auction's fee (issu... |
| 19807 | 4 | `public boolean worthCalling(Debt debt)` | True when calling this callable line is worth it (D24): the saving (callSaving()) passes CallTree.WORTH_CALLING of the face, 3%. |
| 19834 | 40 | `public double callLine(Debt debt)` | Calls one line whole at 100 (CD4): every holder is paid its face - the bank, the households, the businesses, the world and the central bank, which has no say in it (a call is the paper's own term, not a sale a holder ... |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 19883-22103)

| line | len | member | says |
|---:|---:|---|---|
| 19951 | 20 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 19972 | 334 | `private void rebuildSimulationState()` |  |
| 20370 | 26 | `private static String refusal(int slot, DataSave loaded)` | Why a save would not load, or null when it would: the three refusals below, each logged, its file untouched. |
| 20406 | 21 | `private String refusalBeforeRebuild(int slot)` | Why loadGameSave() must not rebuild the world for this slot, or null when it may (FW1, 0.8.37). |
| 20429 | 10 | `public void loadGame(int slot)` | Load game |
| 20447 | 22 | `private void readTheMap(int slot, Long stamp)` | THE CITY MAP, READ BACK (0.7.60): from the slot's sidecar when the save's stamp is its stamp and it was drawn on this land in this month (CityMap.readSidecar()); a save that had a map whose sidecar is missing or stale... |
| 20506 | 11 | `private void restoreOilPools(DataSave loaded)` | THE TWO OIL POOLS ON A LOAD (0.7.93, batch O10b; LandManager's THE TWO OIL POOLS): once the land is in place, the offshore pool's E as saved - or, on a save from before SAVE_FORMAT 35, the one pool's E charged to the ... |
| 20518 | 63 | `private boolean restoreLandOnTheWorld(DataSave loaded, double owned)` |  |
| 20583 | 1460 | `private void readTheSave(int slot)` | The load itself; see loadGame(). |
| 22050 | 4 | `void seedFundLedger()` | THE FUND'S COST BASIS FOR A SAVE FROM BEFORE IT (0.7.39; the project's spec-fund-0739.md, 3.5): each market lot and bond at its market value this month, flagged as such, the rescue lot exact from the counters, one TRA... |
| 22059 | 44 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 22104-22401)

| line | len | member | says |
|---:|---:|---|---|
| 22115 | 17 | `private void seedMoneyConstants(double unit)` | Seeds every money constant struck in founding dollars at a unit: the load path's list since the reform, and since FX4 also buildWorld()'s at 1, so a new city does not keep the last loaded city's unit. |
| 22133 | 1 | `public Denomination getDenomination()` |  |
| 22136 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 22167 | 211 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 22389 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

