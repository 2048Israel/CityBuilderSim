# Game.java - 21,234 lines · 937 methods · 31 constants · model

`ham/citybuildersim/Game.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (135), [DecisionLog](DecisionLog.md) (98), [Auction](Auction.md) (86), [DebtQuote](DebtQuote.md) (79), [Debt](Debt.md) (74), [Equity](Equity.md) (69), [TreasuryLine](TreasuryLine.md) (62), [LandManager](LandManager.md) (57), [GameLog](GameLog.md) (48), [ConstructionControl](ConstructionControl.md) (40), [Landmasses](Landmasses.md) (39), [AgeBand](AgeBand.md) (39), [BusinessDebtManager](BusinessDebtManager.md) (36), [ForeignAccounts](ForeignAccounts.md) (35), [GameFiles](GameFiles.md) (32), [CityMap](CityMap.md) (32), [Rollover](Rollover.md) (30), [Resource](Resource.md) (30), [Markets](Markets.md) (27), [Good](Good.md) (26), [LandParcel](LandParcel.md) (26), [DebtManager](DebtManager.md) (25), [TreasuryJournal](TreasuryJournal.md) (25), [Sector](Sector.md) (24), [TreasuryFund](TreasuryFund.md) (24), [TaxPolicy](TaxPolicy.md) (24), [PriceIndex](PriceIndex.md) (23), [JobType](JobType.md) (22), [BusinessInvestment](BusinessInvestment.md) (22), [Oil](Oil.md) (22)... and 131 more

**Used by (248):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AuctionCheck](AuctionCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BridgeCheck](BridgeCheck.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BuildersFirstCheck](BuildersFirstCheck.md), [BuildingDataCheck](BuildingDataCheck.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [BuybackCheck](BuybackCheck.md), [CalendarCheck](CalendarCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [CementConcrete](CementConcrete.md), [CementConcreteCheck](CementConcreteCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityBasket](CityBasket.md), [CityNeeds](CityNeeds.md), [ClockCheck](ClockCheck.md), [CoalMining](CoalMining.md), [CoalMiningCheck](CoalMiningCheck.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConstructionTrapCheck](ConstructionTrapCheck.md), [ConversionCheck](ConversionCheck.md), [CopperMining](CopperMining.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefining](CopperRefining.md), [CopperRefiningCheck](CopperRefiningCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DeathCareFirstCheck](DeathCareFirstCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtManager](DebtManager.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DenominationCheck](DenominationCheck.md), [DeskCheck](DeskCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquipmentCheck](EquipmentCheck.md), [EquipmentFit](EquipmentFit.md), [EquipmentForms](EquipmentForms.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [ExtractionSector](ExtractionSector.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [FloatingRateNote](FloatingRateNote.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Forestry](Forestry.md), [ForestryCheck](ForestryCheck.md), [Forms1900Check](Forms1900Check.md), [Founding](Founding.md), [FoundingYardCheck](FoundingYardCheck.md), [Freight](Freight.md), [FreightView](FreightView.md), [FreightViewCheck](FreightViewCheck.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HaulageCheck](HaulageCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HomeFuel](HomeFuel.md), [HomeRailCheck](HomeRailCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [HydroCheck](HydroCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [IndustryPicture](IndustryPicture.md), [IndustryView](IndustryView.md), [IndustryViewCheck](IndustryViewCheck.md), [InflationLinkedBond](InflationLinkedBond.md), [InfrastructureCheck](InfrastructureCheck.md), [InputRecorder](InputRecorder.md), [InterestAbroadCheck](InterestAbroadCheck.md), [InvestCheck](InvestCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandClassCheck](LandClassCheck.md), [LandClassRenders](LandClassRenders.md), [LandClassView](LandClassView.md), [LandMap](LandMap.md), [LandNationsCheck](LandNationsCheck.md), [LandScreen](LandScreen.md), [LandlordPortCheck](LandlordPortCheck.md), [LandmassCheck](LandmassCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Markets](Markets.md), [MaterialsCheck](MaterialsCheck.md), [MediumTermBond](MediumTermBond.md), [MiniMap](MiniMap.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OilView](OilView.md), [OilViewCheck](OilViewCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OreView](OreView.md), [OutsideCheck](OutsideCheck.md), [PartnersCheck](PartnersCheck.md), [PartnersPage](PartnersPage.md), [PartnersView](PartnersView.md), [PassThroughCheck](PassThroughCheck.md), [Pieces](Pieces.md), [PlanCheck](PlanCheck.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PortCheck](PortCheck.md), [PortDivision](PortDivision.md), [PriceLevelCheck](PriceLevelCheck.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [QuarryingCheck](QuarryingCheck.md), [RailCheck](RailCheck.md), [RailDepotCheck](RailDepotCheck.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [RankingsScreen](RankingsScreen.md), [ReadPathCheck](ReadPathCheck.md), [RealDollarCheck](RealDollarCheck.md), [RealEstate](RealEstate.md), [RefineryCheck](RefineryCheck.md), [RefineryView](RefineryView.md), [RefineryViewCheck](RefineryViewCheck.md), [Refining](Refining.md), [ReloadCheck](ReloadCheck.md), [ReplayCheck](ReplayCheck.md), [ReplayCity](ReplayCity.md), [ReplayMode](ReplayMode.md), [ReplayMoveProbe](ReplayMoveProbe.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RoadCheck](RoadCheck.md), [RobustnessCheck](RobustnessCheck.md), [RunRenders](RunRenders.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [SectorStatementCheck](SectorStatementCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SlopeCheck](SlopeCheck.md), [SlopeRenders](SlopeRenders.md), [SpreadPlanner](SpreadPlanner.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeBand](TradeBand.md), [TradeBandCheck](TradeBandCheck.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [TruckCheck](TruckCheck.md), [TruckRouteCheck](TruckRouteCheck.md), [UiProbe](UiProbe.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [VaultMoneysCheck](VaultMoneysCheck.md), [VaultTable](VaultTable.md), [VaultView](VaultView.md), [WantedPostsCheck](WantedPostsCheck.md), [WaterCheck](WaterCheck.md), [WellCheck](WellCheck.md), [WoodProducts](WoodProducts.md), [WoodProductsCheck](WoodProductsCheck.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 1171 | THE FOUNDING RESERVE (2026-09-21) |
| 1228 | · THE FOUNDING RECORD (0.7.10) |
| 1270 | THE WORLD MARKET (WN3, 2026-10-28; docs/studies/spec-nations.md 4, 6 and D28) |
| 1506 | THE WORLD'S MONEYS IN THE MONTH (WN4, 2026-10-30; docs/studies/spec-exchange-rates.md 3.5 and 3.7) |
| 1905 | THE REAL US DOLLAR (WN4, D29; RealDollar, docs/studies/spec-exchange-rates.md 3.9) |
| 2002 | THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) |
| 2132 | · THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) |
| 2316 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 2339 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 2562 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 2696 | THE NATIONS THAT SELL THE LAND (WN5, 2026-11-01; docs/studies/spec-nations.md 6.2-6.4) |
| 2910 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 3068 | · THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) |
| 3216 | PRIVATE INVESTMENT |
| 3654 | · AND THE BALANCE SHEET |
| 3866 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 3965 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 3999 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 4550 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 4697 | · the preferred offer |
| 4819 | · what the fund is worth |
| 4879 | · the dial |
| 5043 | · the hand |
| 5160 | · an Insane founding |
| 5289 | WHAT THE D$ IS HELD TO (XR2, 2026-10-22; ForeignAccounts, THE REGIMES) |
| 5367 | THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) |
| 5477 | ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's |
| 5559 | SHRINKING |
| 5843 | THE CONSTRUCTION WARNING |
| 5893 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 6222 | THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) |
| 6589 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 6688 | · the lender's test for one home, for the build card (I13-L) |
| 6869 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 6950 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 7272 | · THE FOREIGN CREWS (FC1) |
| 7879 | THE BUILDERS' PRICE (0.7.19) |
| 8229 | · BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) |
| 8428 | MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) |
| 8522 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 8931 | · F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) |
| 9107 | · in the month |
| 9400 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 9714 | · THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) |
| 9775 | THE PORT'S LANDMASS (ET5, 2026-10-25; the ET5 design, D10) |
| 9794 | THE HYDRO STATION'S RIVER (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.2 and 1.3) |
| 9821 | THE LONG BRIDGES' CROSSING (ET6, 2026-10-26; BridgeSurvey, BridgeWorks, Bridges) |
| 9937 | DEEP WATER'S WORKS (DW1, 2026-10-31; sectors.Oil's DEEP WATER) |
| 10332 | · A RUN OF ORDERS (0.7.40) |
| 10521 | THE CITY'S PAPER, SOLD AT AUCTION (CD2, spec-city-debt.md section 4) |
| 11117 | · A BOND SIZED TO THE CASH IT BRINGS (0.7.10, CD2) |
| 11172 | THE FLOATING-RATE NOTE AND THE INFLATION-LINKED BOND (CD5, |
| 11334 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 11748 | · THE AMORTIZING BOND AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 11834 | ROLLING WHAT FALLS DUE (0.7.13) |
| 12429 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 12718 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 12940 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 13169 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 13593 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 13705 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 14016 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 14670 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 14683 | THE READINGS THE MONTH TAKES OF THE CITY |
| 14987 | · the price of a place (2026-09-21) |
| 15127 | THE ANCHOR (0.7.42): what the city expects prices to do, and the |
| 15154 | · what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) |
| 15284 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 15308 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 15728 | the save system |
| 15781 | THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) |
| 16258 | THE RECORDING (TR1, 2026-10-31) |
| 16582 | PAYING THE WORLD BACK |
| 16759 | THE HOLDERS ARE PAID (0.7.1) |
| 16885 | · the desk, for the households |
| 16990 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 17167 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 17431 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 17692 | · a buyback's holders outside the pools |
| 17717 | WHAT THE TREASURY ACTUALLY DID |
| 17926 | · FROM EARNED TO THE BUDGET (0.7.31) |
| 18072 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 18360 | BUYING YOUR OWN DEBT BACK |
| 18633 | · the call (CD4, spec-city-debt.md section 6.4; D22, D23, D24) |
| 18775 | WHY LAND IS NOT IN THE RENT FLOOR |
| 19156 | · · the three the monthly path sets and this did not |
| 20941 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3105 | `Game.LandNeed.Kind.ROOM` |  |
| 3105 | `Game.LandNeed.Kind.SHORTFALL` |  |
| 3105 | `Game.LandNeed.Kind.DEPOSIT` |  |
| 3105 | `Game.LandNeed.Kind.COAST` |  |
| 9398 | `Game.BuildResult.SUCCESS` |  |
| 9398 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 9398 | `Game.BuildResult.NO_LAND` |  |
| 9398 | `Game.BuildResult.NO_DEPOSIT` |  |
| 9398 | `Game.BuildResult.NO_LICENCE` |  |
| 9398 | `Game.BuildResult.FAILED` |  |
| 9398 | `Game.BuildResult.NO_COAST` |  |
| 9398 | `Game.BuildResult.NO_RIVER` |  |
| 9398 | `Game.BuildResult.NO_BRIDGE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 243 | `Game.MARKET_SEED_SALT` | `0x4D41524B45545852L` | Salt for the D$'s market seed, so the market's path is not the world's own seed spelled again ("MARKETXR"). |
| 769 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 891 | `Game.FOUNDING_YARD` | `"Builders' Yard"` | The builders' building a city is founded with (PT1-M4): see foundingYard(). |
| 1220 | `Game.FOUNDING_CASH` | `100_000 * PriceLevel.FACTOR` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 1223 | `Game.FOUNDING_RESERVE_USD` | `25_000 * PriceLevel.FACTOR` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 1226 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 1552 | `Game.RISK_OFF_NAMED` | `{ "JPY", "CHF", "AUD" }` | The three the log names in a risk-off: the yen and the franc rise, the Australian dollar falls. |
| 2730 | `Game.LAND_FILES` | `new java.util.HashMap<>()` | The world files opened for the land office, by path: opened once per JVM and never closed (MapView keeps its own open the same way), so the cities of a JVM share them. |
| 3389 | `Game.STATIONS_SLOT` | `"Filling stations"` | Where the forecourts' word is filed among the month's investment lines (0.7.83): apart from the shops', as the bank's is. |
| 6328 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 6331 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 7287 | `Game.ForeignOffer.NONE` | `new ForeignOffer(0, 0, 0)` | No contractor at work: every figure nothing. |
| 8037 | `Game.NO_GROUND_AHEAD` | `new GroundAhead(0, 0, 0, 0)` |  |
| 8247 | `Game.BITUMEN_BINDER_SHARE` | `.05` | The share of an asphalt surface's weight that is bitumen: five per cent [R18]. |
| 9412 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 9972 | `Game.BRIDGE_FOR_GOOD` | `"A bridge stands for good: taking one down is not built yet"` | Why a bridge cannot be demolished (ET6-C): one that stands is for good until the demolition of a bridge is built. |
| 10752 | `Game.CENTS_PER_UNIT` | `100 / PriceLevel.FACTOR` | How many of the model's proceeds granules go to a unit of money: a hundred at today's prices, and a hundred over the founding's price level (PriceLevel.FACTOR) at the founding's, so a proceeds figure is rounded to the... |
| 11395 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 11565 | `Game.NO_DOLLAR_FLOAT_OR_LINKED` | `"Floating-rate notes and inflation-linked bonds are issued at home only."` | What the treasury is told when it asks for a dollar floater or linker. |
| 11604 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 11765 | `Game.BUILD_NOTE_GRANULE` | `1000 * PriceLevel.FACTOR` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 13615 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 13627 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 13630 | `Game.BUILD_BOND_GRANULE` | `100 * PriceLevel.FACTOR` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 13647 | `Game.FIXED_ISSUE_COST` | `12 * PriceLevel.FACTOR` | Bond counsel, rating and printing. |
| 13658 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003). |
| 13698 | `Game.MINIMUM_ISSUE_FLOOR` | `50 * PriceLevel.FACTOR` | The floor under minimumIssueSize(), in thousands: $50k, so a brand new city can still borrow. |
| 15331 | `Game.SHORTFALL_DUST` | `1e-9` | A shortfall smaller than this share of an order's cost is rounding, not a want of cash: buildFor() borrows nothing for it. |
| 15735 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |
| 16273 | `Game.RECORD_BACKLOG` | `6` | The most recording writes the writer may be behind by (months and histories not yet on disk): the clock then records nothing that month, and a skip waits. |
| 16276 | `Game.RECORD_WAIT_MS` | `30_000` | The longest a skip waits for the writer to get inside RECORD_BACKLOG, in milliseconds, before it records nothing that month: a writer that cannot write must not hold the skip for ever. |

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
| 1091 | `private String loadFailure` | Why the last load did not happen, or null. |
| 1146 | `private final EquipmentForms equipmentForms` | The shares of the 1900 forms (steam, the farms' horses, the carts') set from the buildings still without the equipment that ends them (R1). |
| 1243 | `private Founding founding` |  |
| 1268 | `private transient Nations nations` | The nations on this city's path, built the first time getNations() is asked and again when the founding's path or seed differs (not saved: the path and seed are, in the founding). |
| 1306 | `private transient volatile WorldMarket worldMarket` | The market the city trades in, or null: built where a world file is there, the first time the month (or a load) needs it. |
| 1309 | `private transient boolean worldMarketTried` | Whether the market has been looked for in this city, so a file that will not open is said once and not every month. |
| 1312 | `private transient boolean onLiterals` | Whether this city plays on the goods' literals whatever world file is there (playOnLiterals()). |
| 1341 | `private transient volatile PartnerTrade partnerTrade` | The city's trade by partner, built with the market; null with none. |
| 1544 | `private transient volatile WorldMoneys worldMoneys` | The world's moneys against the US$, built with the market (null where it is null) and stepped in the month; not saved itself: DataSave.worldMoneys is its state. |
| 1547 | `private transient boolean worldRiskOffStarted` | What the month's step found, for the log after the reprice: whether a risk-off began, the yen's, the franc's and the Australian dollar's moves against the US$ in it, and the lines of the world pegs that broke. |
| 1548 | `private transient double[] worldRiskOffMoves` |  |
| 1549 | `private transient List<String> worldPegBreaks` |  |
| 1558 | `private transient volatile PolicyRates policyRates` | The eight central banks' policy rates over the world's moneys (XR4), built with them and stepped in the month; not saved itself: DataSave.policyRates is its state. |
| 1564 | `private transient boolean worldQuietForTheD` | Whether the world's moneys are kept from the D$ in this city (quietTheWorldForTheD()). |
| 2022 | `private CityMap cityMap` | The city map, or null until it is asked for. |
| 2025 | `private int mapFailures` | How many months the map failed to keep up and was dropped (each one logged): what a harness asserts is none. |
| 2042 | `private boolean mapAtSeaStale` | Whether the map's oil at sea is to be handed over again before it is next read (0.7.97): set by a draw and a load. |
| 2153 | `private int mapGeneration` | Bumped whenever the city's map is thrown away (a new city, a load): a draft from before it is another city's. |
| 2157 | `private final Game game` |  |
| 2158 | `private final int generation` |  |
| 2159 | `private final CityLand land` |  |
| 2160 | `private final java.util.Map<Resource, double[]> remaining` |  |
| 2161 | `private final BuildingVisual.Type[] types` |  |
| 2162 | `private final long[] counts` |  |
| 2164 | `private final int month` | The game's month when the draft was made: what the map's logs date the deal by (CL3). |
| 2166 | `private final Landmasses.Picture landmasses` | The landmasses as they stood (ET5): what the map is measured under; null for a city on one landmass. |
| 2168 | `private final java.util.List<CityMap.BridgeRun> bridges` | The long bridges standing when it was copied (ET6), null for none. |
| 2169 | `private volatile CityMap map` |  |
| 2302 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 2374 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 2375 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 2600 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 2720 | `private transient LandSellers landSellers` | The sellers of this city's ground, built the first time they are asked (landSellers()); null where none sells. |
| 2721 | `private transient boolean landSellersTried` |  |
| 2724 | `private transient int landNations` | How many nations the world file names, -1 until asked (landNationCount()), 0 for no file. |
| 2727 | `private double[] landBoughtKm2, landBoughtUsd` | What the city has bought from each nation, km2 and thousands of US dollars, by the nation's number; index 0 is a purchase with no seller named. |
| 3055 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under ON OFFER (under the toggle until 0.7.26), and a short vault says here that the rest was converted. |
| 3056 | `private int lastLandReceiptMonth` |  |
| 3161 | `private BusinessInvestment.OrderWatch orderWatch` | Harnesses only (0.7.54): what every order search decides is told here as well - see BusinessInvestment.OrderWatch. |
| 3206 | `private boolean monthRun` | WHETHER A MONTH HAS RUN SINCE THE CITY WAS FOUNDED OR LOADED (0.7.102). |
| 3884 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 3893 | `private double carriedTransitBill` | ...and the month's transit bill as 6d struck it (0.7.49), applied inside the rebuild for the same reason: the rebuild would strike it at the fill the month ended on, not the one it was paid at. |
| 3904 | `private double carriedCaptiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, because the cells are not back when the road ratio is first struck (carriedCarOwnership's reason), and a j... |
| 3907 | `private double[] carriedFuelMonth` | ...and the drivers' fuel as 6d drew it (0.7.62): bill, imported part, litres - DataSave.householdFuel; null for a save from before it. |
| 3910 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 3916 | `private final HomeFuel homeFuel` | The homes' lamp oil and coal (R0c, the home form); both shares 0 in every played city until R0e, and then nothing in it runs. |
| 3942 | `private final double[] rowFeesSettled` | Each row's fees as the last month's waterfall was handed them: the clinic's, the schools', the fares and the fuel (0.7.49). |
| 3970 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 4248 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 4589 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 5734 | `private int wellsWornOut` | Wells retired worn out over the run (0.7.84): a count for the playtest, not saved. |
| 5761 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 5851 | `private int constructionShedMonth` |  |
| 5852 | `private double constructionShedPoints` |  |
| 5911 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 5923 | `private final java.util.Map<String, String> landBlockedNeeds` | The ground each waiting sector asked for this month (PT1-M2): sector key to "needs 743 m², 301 m² free", the numbers its own refusal gave, kept beside landBlockedSectors and cleared with it, so the inbox's yearly noti... |
| 5944 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 5964 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 5981 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 6427 | `final BusinessInvestment.Decision decision` |  |
| 6428 | `final double cash, perUnitProfit` |  |
| 6429 | `final BusinessDebtManager credit` |  |
| 6430 | `final boolean banned` |  |
| 6431 | `boolean atPrime` |  |
| 6432 | `double firstRate` |  |
| 6433 | `int deskCalls` |  |
| 6579 | `private ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate` | What consider() tests an order on (0.7.113): the amount the interest is struck on, as sectors.SpreadPlanner's money gate is one replaceable piece - ON_ITS_WHOLE_COST, Jerus's decision A, unless a check or a probe set ... |
| 6828 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 6829 | `private final java.util.Set<String> heldForDownPayment` |  |
| 6910 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 6911 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 6912 | `private double salvageUsedThisMonth` |  |
| 6970 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 7108 | `private double bankedClearedAtLoad` | Construction points the last load cleared from stacks that carried them past what they owed (0.7.17; see the load path and BuildingManager.clearBankedProgress()). |
| 7484 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 7489 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 7679 | `public final int quantity` |  |
| 7680 | `public final double sticker` |  |
| 7681 | `public final double materialsNeeded` |  |
| 7683 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 7685 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 7686 | `public final double plantPrice` |  |
| 7687 | `public final double plantCost` |  |
| 7688 | `public final double materialsImported` |  |
| 7689 | `public final double materialsPrice` |  |
| 7690 | `public final double importCost` |  |
| 7696 | `public final double salesTax` | The sales tax in the price (0.7.19): what the builders remit on it, less what they claim back on the plant's material - passed on, as a shop's shelf price passes it on. |
| 7703 | `public final double allowance` | What the price allows for the material beyond the yard (0.7.19): the plant's and the world's at today's prices, net of the credit on the plant's and with the builders' tax on it - the part of the total an escalation c... |
| 7704 | `public final double total` |  |
| 7705 | `public final double landNeeded` |  |
| 7706 | `public final double landFree` |  |
| 7712 | `public final double months` | Months to finish at this month's shares of the site output, the planners' own reading (quoteMonths()), or NaN when the builders have no site output. |
| 7724 | `public final double bitumenNeeded, bitumenFromRefiners, bitumenImported, bitumenLocalCost, bitumenImportCos...` | The bitumen the order's paving takes (0.7.83, batch O6; spec-oil 2.5), in tonnes - none for anything but a paved road, a highway and a paving (Game.bitumenFor()) - what of it the refiners' tanks would sell and what th... |
| 7734 | `public final double prepSqFt, prepPoints, prepCost` | The foothill the order has to prepare (ET2; SitePreparation, LandManager.foothillToPrepareSqFt()), square feet; the construction points that is, owed by the order's site before its first building finishes; and its mon... |
| 7745 | `public final double worksSqFt, worksPoints, worksCost` | The cuts, viaducts and tunnels a highway or a railway order needs (ET4; RunWorks, spec-earth2-terrain 4.3): the run's land the works are priced on, square feet; the construction points they are, owed by the order's si... |
| 7746 | `public final double[] worksMix` |  |
| 7755 | `public final double bridgeLengthM, bridgePoints, bridgeCost` | The span of a long bridge order (ET6; BridgeWorks, spec-earth2-terrain 6.4): the crossing's length, metres; the construction points its deck is, owed by the order's site before its first bridge finishes, as a pad's an... |
| 7764 | `public final double deepWaterDepthM, deepWaterPoints, deepWaterCost` | The deep-water works of a floating platform or its well (DW1; Game.deepWater()): the depth of the sea the order's first unit stands in, metres (every unit of the order is priced at it); the construction points of the ... |
| 7942 | `private boolean contractsToInfer` | Set by the load path when a save carries no payers, and read once the land price is back. |
| 7957 | `private final BuildingManager.BuildersWages buildersWages` | What the builders' crews cost a point, today and at founding. |
| 8155 | `private int preparationOrders` | The foothill preparation billed over this game's life (ET2, SlopeCheck 9 and LongPlaytest's summary): the orders that carried one, the points they owed, and their money before the builders' tax (BuildQuote.prepCost). |
| 8156 | `private double preparationPointsBilled, preparationMoneyBilled` |  |
| 8211 | `private int runWorksOrders` | The cuts, viaducts and tunnels billed over this game's life (ET4, RunWorksCheck 10 and LongPlaytest's summary): the highway and railway orders that carried works, the points they owed, and their money before the build... |
| 8212 | `private double runWorksPointsBilled, runWorksMoneyBilled` |  |
| 8273 | `private double bitumenTonnes, bitumenCost, bitumenImported` | The bitumen drawn for roads since this game was founded or loaded, in tonnes, what it cost the builders and the part bought abroad (0.7.83): running totals for the checks and the playtest, never reset and not saved - ... |
| 8426 | `private double siteDrawBillable` | What the month's site draw cost the builders, as they bill it on (billableMaterial()): what the owners' escalation is struck against. |
| 8688 | `private String lastHandRefusal` | Why a demolition or a buy-out was not made, or null when it was. |
| 9110 | `private ConstructionControl.Events controlThisMonth` | This month's events, kept for the screens and the inbox: what the advance left, settled. |
| 9112 | `private double overtimePaidThisMonth` | What the treasury paid the builders this month for overtime, with their tax in it. |
| 9114 | `private double demolitionSalvageThisMonth` | What a finished demolition's material fetched this month, from the builders. |
| 9736 | `private double freshRights` | Units a month of fresh water the city may treat past what its lakes and river yield: an older city's (0.7.59). |
| 9841 | `private BridgeSurvey.Ground bridgeGroundForTest` |  |
| 9842 | `private BridgeSurvey.Result bridgeKept` |  |
| 9843 | `private int bridgeKeptFar, bridgeKeptVersion` |  |
| 9844 | `private long bridgeKeptStamp` |  |
| 9845 | `private CityLand bridgeKeptLand` |  |
| 9846 | `private BridgeSurvey.Ground bridgeKeptTest` |  |
| 10230 | `private final Investor government` | The city itself, as a payer. |
| 10903 | `private String saleHow` | How the sale being booked reads on the piece it makes or joins (Debt.getSales(), CD6): "auction", or "the rollover" while the treasury's rollover sells what falls due (issueForRollover()). |
| 11916 | `private final Rollover rollover` |  |
| 11919 | `private final java.util.List<String> rolloverShortNotes` | What the month's rollover sold short and did not raise (CD2), in words, for the inbox; the record of a month that has finished, not saved. |
| 11925 | `private final java.util.List<String> rolloverTailNotes` | What the month's rollover paid a tail past the default cap for (D29, Filled or capped higher), in words, for the inbox; not saved. |
| 11935 | `private final java.util.List<TimeSkipReport.AutoSale> autoSalesThisMonth` | Every automatic sale this month - the rollover's issues and automatic building's bonds - for the time skip's report (CD2, slice 6): a record of a month that has finished, cleared at the top of the next, not saved. |
| 14005 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 14006 | `private LandManager landManager` |  |
| 14010 | `private boolean loadingSave` | True while loadGameSave() runs initialize(), which then founds no land: the save brings its own (0.7.57). |
| 14013 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 14014 | `private BuildLog buildLog` |  |
| 14041 | `private PopulationCohorts cohorts` |  |
| 14042 | `private FamilyModel families` |  |
| 14050 | `private Migration migration` |  |
| 14056 | `private LabourMarket labourMarket` | What labour costs. |
| 14062 | `private final WantedPosts wantedPosts` | The posts the staffing gate refused for want of workers that the wage is priced on as if they were offered (LD1): see WantedPosts, and noteStaffingHold(), settleWantedPosts() and repriceLabour() below. |
| 14068 | `private Education education` | The schools. |
| 14077 | `private Health health` | How much of the workforce is off sick. |
| 14085 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 14675 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 14821 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 14828 | `private HouseholdAccounts households` | The residents' own books. |
| 14837 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 14847 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 14851 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 14855 | `private final CausesOfDeath causesOfDeath` | What people die of: the causes of 1900 and how history takes them away (R0b). |
| 14874 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 14878 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 14968 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 14974 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 15051 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 15061 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 15067 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 15074 | `private final Equity equity` | Who owns the city's companies. |
| 15078 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 15086 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 15096 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 15103 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 15125 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 15143 | `private final Expectations expectations` |  |
| 15290 | `private final Consumption consumption` |  |
| 15291 | `private boolean consumptionLoaded` |  |
| 15300 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 15318 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 15327 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 15328 | `private int rateHistoryFilled` |  |
| 15432 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 15448 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 15492 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 15505 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 15519 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 15537 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 15540 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 15542 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 15543 | `private double carriedRetailCapacity` |  |
| 15544 | `private double carriedRetailWant` |  |
| 15547 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 15548 | `private java.util.Map<String, String> lastInvestment` |  |
| 15551 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 15606 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 15737 | `private int monthsSinceAutosave` |  |
| 15810 | `private final java.util.concurrent.ConcurrentLinkedQueue<GameFiles.Result> savesWritten` | Results the writer's thread has finished, waiting for the FX thread to take them: see reportSaves(). |
| 15814 | `private java.util.function.LongSupplier saveClock` | The clock the floor is measured on (nanoseconds); checks give a city their own. |
| 15817 | `private long lastAutosaveAt` | When the last autosave was asked for, on saveClock; meaningless until autosavedYet. |
| 15819 | `private boolean autosavedYet` |  |
| 16279 | `private String recordingId` | The recording's id, which names its folder under recordings/; null in a city that never recorded. |
| 16281 | `private boolean recording` | Whether "Record this city" is on. |
| 16283 | `private boolean recordedSinceLoad` | False from a load or from switching on until the first month is written: that write drops the months past it (the rewind) and takes the history with it. |
| 16285 | `private boolean replayCopy` | True for the throwaway copy a replay shows (markReplayCopy): it never records and never writes the player's folder. |
| 16287 | `private boolean skipping` | True while simulateMonths() runs its months: the recorder then waits for the writer rather than skipping a month. |
| 16289 | `private long recordingStartedAt` | When the recording was started, in epoch milliseconds (its meta); 0 in a city loaded from a save, whose meta is on disk already. |
| 16291 | `private final java.util.Set<Path> recordingsWaiting` | The files the recorder has handed the writer that are not yet written, by target: a task replaced while it waited is counted once, and its replacement removes it. |
| 16292 | `private final Object recordingRoom` |  |
| 16294 | `private int monthsNotRecorded` | Months the clock did not record because the writer was behind, since the city was built or loaded. |
| 16296 | `private boolean inUnrecordedRun` | True while the clock is in a run of unrecorded months: the first of a run prints one line. |
| 16298 | `private volatile long lastRecordNanos` | What the last recorded month cost the FX thread: the fill, the snapshot and the hand-over, in nanoseconds. |
| 16455 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 16469 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 16605 | `private double foreignDebtRaisedThisMonth` |  |
| 16606 | `private double foreignPrincipalRepaidThisMonth` |  |
| 16607 | `private double foreignInterestPaidThisMonth` |  |
| 16695 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 16715 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 16716 | `private double cityPrincipalRepaidThisMonth` |  |
| 16730 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 16731 | `private double cityDiscountForBank` |  |
| 16734 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 16751 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 16757 | `private double bankPrincipalRepaidThisMonth` |  |
| 16795 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 16804 | `private double couponsToFirms, principalToFirms, firmsBoughtPaper` | ...and the businesses' (CD2, slice 4): the same three figures, for the playtest and the harness. |
| 16814 | `private double couponsToWorld, principalToWorld, worldBoughtPaper, paperBoughtFromWorld` | ...and the world's (CD2, slice 5), which are declared: its coupon (an income debit) and its principal (a financial outflow) leave the pools for abroad, what it paid at the settle (worldBoughtPaper) is a financial infl... |
| 17008 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 17010 | `boolean restrikeOffForTest` | HoldersCheck's twin (BK1): true books no re-strike, as main did, so the harness can hold the two cities' figures side by side. |
| 17530 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 17533 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 17705 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the world (the holders of a dollar bond among them), carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape ... |
| 17707 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 17760 | `private double treasuryOpening` |  |
| 17761 | `private double treasuryClosing` |  |
| 17762 | `private double treasuryRaised` |  |
| 17763 | `private double treasuryRepaid` |  |
| 17764 | `private double treasurySurplus` |  |
| 17765 | `private boolean treasuryRecorded` |  |
| 17784 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 17796 | `private double retiredGainSoFar` | The gain (below nothing, a loss) on paper the treasury has retired since the last strike, in local money - the carrying value less the price paid, counted as each buyback happens (repurchaseDebt()), press to press lik... |
| 17807 | `private double treasuryNotCash` | The part of the month's budget balance that is not cash (CD3): the discount amortised, which the budget charges and the treasury never pays, less the gain on paper retired, which the budget books and the treasury neve... |
| 17810 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 18033 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 18036 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 18044 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 18045 | `private String postAuditDriftPool` |  |
| 18046 | `private double postAuditDriftWorst` |  |
| 18110 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 18113 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 18116 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 18124 | `private final java.util.Map<String, Double> arrearsPaidTo` | This month's arrears paid down, by the sector whose till they reached (0.7.55): what its statement's arrears line reads (SectorBooks, arrearsPaid), struck at the settle and read when the month is recorded, both inside... |
| 18773 | `private long pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 20893 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 20945 | `private final Denomination denomination` |  |
| 21227 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 21210 | **type** `public class Game` |  |
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
| 280 | 437 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 718 | 5 | `public void run()` |  |
| 724 | 44 | `private void initialize()` |  |
| 771 | 4 | `static { ... }` |  |
| 779 | 3 | `public void newGame()` | A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. |
| 788 | 41 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 859 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 946 | 12 | `private void foundingYard()` | The city opens with the builders' yard already standing (PT1-M4). |
| 959 | 8 | `public void resumeGame()` |  |
| 967 | 72 | `public void loadGameSave(int slot)` |  |
| 1048 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 1056 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 1071 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 1073 | 6 | `public void toggleQuit()` |  |
| 1085 | 4 | `public void saveForQuit()` | The quit's autosave (UP1): asked for, and on disk when this returns - System.exit does not wait for the writer's thread, so quitting waits for it here. |
| 1093 | 1 | `public String getLoadFailure()` |  |
| 1095 | 7 | `public void toggleGraphs()` |  |
| 1102 | 5 | `public void toggleReports()` |  |
| 1108 | 3 | `public void toggleNextMonth()` |  |
| 1113 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 1115 | 3 | `public int getMonth()` |  |
| 1120 | 3 | `public boolean isLeverOpen(PolicyDates.Lever lever)` | Whether a Policy lever is open in this city (R0): from the month Canada first had it, or the month a find brought it to. |
| 1125 | 3 | `public int leverOpeningMonth(PolicyDates.Lever lever)` | The game month a Policy lever opens in this city (R0): history's (PolicyDates), earlier where a find brought it forward, 1 where it is open from the start. |
| 1135 | 9 | `private void policyHearsTheMonth()` | Tells the policy the city's month (it holds every closed lever at zero), the labour market whether the minimum wage is law yet and the households what a pensioner is paid (R0), and the sectors the 1900 forms their unf... |
| 1149 | 1 | `public EquipmentForms getEquipmentForms()` | The 1900 forms' driver, for the harnesses that read which shares the counts hold (R1). |
| 1156 | 5 | `public boolean setMinimumWage(double value)` | The player's hand on the minimum-wage dial (R0): refused until the lever opens (1918 on history's dates), because the dial is also the wage ladder's anchor and a floor of zero would zero every wage. |
| 1163 | 5 | `public boolean setMinimumWageAdjustment(double share)` | The player's nudge on the floor (R0): refused with the dial, for the same reason. |
| 1168 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 1171-1227)

### THE FOUNDING RECORD (0.7.10) (lines 1228-1269)

| line | len | member | says |
|---:|---:|---|---|
| 1246 | 3 | `public Founding getFounding()` | How this city was founded. |
| 1251 | 15 | `public synchronized Nations getNations()` | The nations beyond the city on this founding's path and seed, from the game's world file; null where the file is not here. |

### THE WORLD MARKET (WN3, 2026-10-28; docs/studies/spec-nations.md 4, 6 and D28) (lines 1270-1505)

| line | len | member | says |
|---:|---:|---|---|
| 1320 | 16 | `public void playOnLiterals()` | This city plays on the goods' literals, as a city with no world file does, whatever file is there: no market is built or restored, and the markets keep the literal band (WN3). |
| 1338 | 1 | `public boolean isOnLiterals()` | Whether playOnLiterals() was called. |
| 1344 | 1 | `public WorldMarket getWorldMarket()` | The world market of this city, or null where there is no world file (the city then plays on the goods' literals) or it has not been built yet: the first month builds it, a load builds it. |
| 1347 | 4 | `public PartnerTrade.Snapshot getPartnerTrade()` | The city's trade by partner as of the last month tallied - a whole, immutable snapshot, safe from any thread; null with no market. |
| 1353 | 4 | `public TradeLanes.Feed getTradeFeed()` | The map's feed over the city's trade by partner: last month's and the year to date (the snapshot is the feed); TradeLanes.Feed.NONE with no market. |
| 1359 | 1 | `PartnerTrade partnerTradeForTest()` | For a harness: the live record behind the snapshot (its history, its state), or null. |
| 1362 | 4 | `private boolean hasBerth()` | Whether any sea terminal stands: the berth the market gives the city a sea leg by. |
| 1372 | 46 | `private WorldMarket ensureWorldMarket()` | The market of this city, built the first time it is asked (once a city): over the world file, the nations on this founding's path and seed, the city at its site - an Earth city at its spot, a generated world's at the ... |
| 1423 | 26 | `private void strikeTheWorldMarket()` | Steps 1 and 2 of the month (startOfMonthUpdate(), right after the world's price level and the exchange rate): the January solve when the year has turned, and the band of every good from last month's trade. |
| 1455 | 25 | `private void tallyThePartners()` | Step 3: the month's trade with the world split to the nations (PartnerTrade.tally()), from the month's own fills - the clearing's and every draw, with the world as the other side - at the prices they were struck at, i... |
| 1487 | 18 | `private void restoreWorldMarket(DataSave loaded)` | A load's half of the market (the load path, after the buildings are back): built where a file is there, the berth read, the solved state put back as saved (WorldMarket.restore() - not solved again), and the trade by p... |

### THE WORLD'S MONEYS IN THE MONTH (WN4, 2026-10-30; docs/studies/spec-exchange-rates.md 3.5 and 3.7) (lines 1506-1904)

| line | len | member | says |
|---:|---:|---|---|
| 1555 | 1 | `public WorldMoneys getWorldMoneys()` | The world's moneys of this city, or null where there is no world file or the market was not built (the D$ then feels no world). |
| 1561 | 1 | `public PolicyRates getPolicyRates()` | The policy rates of the eight central banks, or null where the world's moneys are null. |
| 1572 | 4 | `void quietTheWorldForTheD()` | The D$ of this city is not reached by the world: it is handed zeros and no terms of trade, so it moves to the bit as it did before the world's moneys (WN4), while the moneys are built, stepped, saved and charted as ever. |
| 1578 | 10 | `private WorldMoneys buildWorldMoneys(Path file, Nations beyond, WorldMarket.Base base, int nationCount)` | The moneys over the world file and the nations of this founding's path, with each nation's commodity share of exports from the market's base; null, and a line, if they will not build. |
| 1590 | 9 | `private PolicyRates buildPolicyRates(WorldMoneys moneys)` | The eight central banks' policy rates over these moneys, standing in the first January (Nations.FIRST_YEAR); null, and a line, if they will not build (the city then reads the constant world rate). |
| 1605 | 8 | `private void handTheUsRate()` | THE US'S RATE TO THE CITY (XR4): the US's policy rate is the world's price of money to the DebtManager (worldRate()); the constant (DebtManager.WORLD_BASE_RATE) where there are no policy rates, or where the city's wor... |
| 1615 | 3 | `private static double valueMove(double before, double after)` | A money's value against the US$ in a month, a fraction: its rate before over its rate after, less one (a rate is units to the dollar, so a falling rate is a rising money). |
| 1626 | 43 | `private void stepTheWorldMoneys(WorldMarket market, boolean solved, int year)` | Steps the moneys to the month the city is in (strikeTheWorldMarket(), after the January solve): each month in turn, handing the market's terms of trade in the January it solved and null in every other month. |
| 1671 | 5 | `private void stepThePolicyRates(int target)` | Steps the eight banks' policy rates, each month in turn, to month index target (WorldMoneys.monthIndex); none ahead of it are stepped back. |
| 1682 | 21 | `private void handTheMoneysToTheVault()` | THE MONEYS TO THE VAULT (WN4, right after the step, before anything buys or sells): the eight's US$ per unit - 1 / rate(code), NaN for a money with no rate this month - and the city's trade by money, for the reserve m... |
| 1705 | 4 | `private double worldRiskOffLevel()` | The risk-off level the carry and the hot money are told this month (XR4): the moneys' own, 0 with no moneys or a world quieted for the D$. |
| 1714 | 5 | `private double[] tradeSharesByMoney(WorldMoneys moneys)` | The city's trade by money (MATCH_TRADE): each partner nation's exports plus imports with the city over the last finished year (PartnerTrade's history), else the year to date, else none - all US$ - mapped to the eight ... |
| 1725 | 27 | `private double[] tradeByNation()` | The city's trade with each nation: exports plus imports over the last finished year (PartnerTrade's history), else the year to date, else nothing (all zero); weight[n] for nation n, 1-based, index 0 unused, in thousan... |
| 1757 | 11 | `static double[] tradeSharesByMoney(WorldMoneys moneys, Nations beyond, int year, double[] weight)` | The eight's shares of a trade given by nation (weight[n] for nation n, 1-based, index 0 unused), summing to 1 - or all zero with no weight: each nation's money mapped to the eight by eightOf(). |
| 1774 | 9 | `static int eightOf(WorldMoneys moneys, String code)` | Which of the eight a money stands in for in the city's trade: itself if it is one of them; else the money it is pegged to or uses, followed through the chain (WorldMoneys.pegAnchor) if that is one of them; else the US... |
| 1791 | 24 | `private double cityExportPriceLevel(int year)` | The city's export-weighted ln world price of what it sells, each good against its normal (WN4, the D$'s terms of trade): for each good, its share of the value the city sold abroad in the last finished year (PartnerTra... |
| 1817 | 4 | `private static int goodNumber(Nations beyond, Good good)` | The number the file gives a good (1..goodCount(): its key is the Good constant's name), 0 for one the file does not price. |
| 1826 | 5 | `private void handTheWorldToTheD()` | What the world is this month, handed to the D$ right before its reprice: the moneys' dollar cycle and risk-off level and the hot money held; zeros with no moneys, and then the D$ is what it was. |
| 1833 | 17 | `private void noteTheWorldMoneys(double rateBeforeReprice)` | The log's lines about the world, after the reprice: money running to safety abroad (with where the D$ went in it) and the world pegs that broke. |
| 1855 | 18 | `private void restoreWorldMoneys(DataSave loaded)` | A load's half of the moneys (restoreWorldMarket()): the saved state put back where it fits this world file, else the moneys stand at the first January (Nations.FIRST_YEAR) and are caught up to the city's month with no... |
| 1879 | 25 | `private void restorePolicyRates(DataSave loaded)` | A load's half of the policy rates (restoreWorldMarket(), after the moneys): the saved state put back where it fits, else the rates are rebuilt by stepping from the first January (Nations.FIRST_YEAR) to the city's mont... |

### THE REAL US DOLLAR (WN4, D29; RealDollar, docs/studies/spec-exchange-rates.md 3.9) (lines 1905-2001)

| line | len | member | says |
|---:|---:|---|---|
| 1920 | 3 | `public double dollarsPer2015Usd()` | What a real 2015 US$ costs in D$ now: the rate (D$ per US$) times the US$'s price level since founding (RealDollar.dollarsPer2015Usd); the rate at founding. |
| 1928 | 4 | `public double realGdp2015Usd()` | The last finished month's GDP in real 2015 US$: the month's GDP over the city's price index (YearBook.realGdp's division) over the founding's rate in today's money (RealDollar.realGdp2015Usd). |
| 1937 | 1 | `public double realGdp2015UsdYear()` | The city's GDP in real 2015 US$ a year, in whole US$: the last finished month's (realGdp2015Usd(), thousands) at that month's pace, times 12 and 1,000. |
| 1948 | 7 | `public double realEffectiveRate()` | The D$'s real effective rate, 100 at founding (RealDollar.realEffectiveRate): the weights are the city's trade with each nation over the last finished year, else the year to date, else the US alone. |
| 1962 | 25 | `public List<RealDollar.Weighed> realEffectivePartners()` | The partners the real effective rate weighs (WN5b), in nation order: every nation the city traded with over the last finished year (else the year to date) whose money, rate and prices are all there to weigh (RealDolla... |
| 1989 | 1 | `public long getWorldSeed()` | The seed of the world this city stands on (0.7.56): chosen at founding, or derived from an older save. |
| 1997 | 1 | `public World getWorld()` | The world this city stands on (0.7.56): its terrain, its founding site and river, its fields of ore and oil. |
| 2000 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and every purchase since, whole blocks of a grid since 0.7.67 (CityLand). |

### THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) (lines 2002-2131)

| line | len | member | says |
|---:|---:|---|---|
| 2028 | 12 | `public CityMap getCityMap()` | The city map: drawn canonically the first time it is asked for, then kept up month by month. |
| 2055 | 49 | `CityMap.AtSea mapAtSea()` | The oil at sea as the map draws it (0.7.97, batch O13; CityMap's THE OIL AT SEA, docs/studies/spec-oil.md 2.12): each platform's jacket in the middle of the sea sites its slots hold on its field - the platforms on a f... |
| 2116 | 6 | `CityMap.RailPicture mapRail()` | The railway as the map draws its yard (0.8.1, batch FR0; CityMap's railPicture, spec-freight 4.3): whether the railway carries goods, and the share of its track's wagon sets the fleet has standing. |
| 2124 | 4 | `private static Deposit seaFieldOf(List<LandManager.SeaField> fields, int cell, int index)` | A sea field in the list by its world cell and index, or null. |
| 2130 | 1 | `public boolean hasCityMap()` | Whether the city map has been drawn: a city never asked for it has none, and its months pay nothing for it. |

### THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) (lines 2132-2315)

| line | len | member | says |
|---:|---:|---|---|
| 2156 | 41 | **type** `public static final class MapDraft` | A map to be drawn away from the screen's thread: the city's land, remains and counts as they stood, copied. |
| 2171 | 11 | `private MapDraft(Game game)` _(in Game.MapDraft)_ |  |
| 2184 | 3 | `public void draw()` _(in Game.MapDraft)_ | Draws the map on the copy: any thread, once. |
| 2189 | 1 | `public boolean drawn()` _(in Game.MapDraft)_ | Whether it has been drawn. |
| 2192 | 1 | `CityMap map()` _(in Game.MapDraft)_ | The map drawn, or null: a harness's (MapCheck 7). |
| 2195 | 1 | `public Game game()` _(in Game.MapDraft)_ | The game it was taken from. |
| 2199 | 3 | `public MapDraft mapDraft()` | A draft of the city's map, for drawing away from the screen's thread; null when the map is drawn already. |
| 2210 | 23 | `public boolean adoptMap(MapDraft draft)` | Keeps a drawn draft as the city's map, bound to its own land and kept up to the month (reconcile()), and says whether it is the city's map now. |
| 2235 | 1 | `public int getMapFailures()` | How many months the map failed to keep up and was dropped. |
| 2238 | 1 | `public BuildingVisual.Type[] getMapTypes()` | The building types as the map draws them, by id. |
| 2241 | 7 | `public long[] getMapCounts()` | Every type's standing count, by id: what the map's districts sum to. |
| 2250 | 4 | `private CityMap drawMap()` | The map drawn canonically from the city as it stands. |
| 2262 | 17 | `private void reconcileMap()` | The month's change placed on the map (CityMap.reconcile()), after the month's construction and demolitions; the land drawn again (a restatement) draws the map again. |
| 2281 | 1 | `public String getCityName()` | The city's name. |
| 2284 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 2287 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 2290 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 2293 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 2311 | 4 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset until 0.7.20, when the screen stopped saying what the money buys; NewGameChec... |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 2316-2338)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 2339-2561)

| line | len | member | says |
|---:|---:|---|---|
| 2377 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 2378 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 2380 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 2381 | 9 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 2392 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 2393 | 1 | `public double getSubsidyPaid(String key)` |  |
| 2395 | 5 | `public double getTotalSubsidyPaid()` |  |
| 2402 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 2415 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 2418 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 2437 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 2447 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 2482 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 2499 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 2515 | 5 | `public double getIncome()` | EARNED (0.7.31's name for it): the tax take less the running programmes, plus the utilities' net, at today's dials - the header's "+$X earned a month". |
| 2521 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 2525 | 3 | `public double getWaterRatio()` |  |
| 2529 | 3 | `public double getRoadRatio()` |  |
| 2534 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 2539 | 3 | `public Markets getMarkets()` | Every goods market. |
| 2544 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 2548 | 3 | `public LandManager getLandManager()` |  |
| 2553 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 2562-2695)

| line | len | member | says |
|---:|---:|---|---|
| 2603 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 2606 | 6 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 2621 | 74 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |

### THE NATIONS THAT SELL THE LAND (WN5, 2026-11-01; docs/studies/spec-nations.md 6.2-6.4) (lines 2696-2909)

| line | len | member | says |
|---:|---:|---|---|
| 2732 | 9 | `private static synchronized WorldData landFile(Path path) throws IOException` |  |
| 2746 | 20 | `private LandSellers landSellers()` | The nations that sell this city's ground, built once and kept: null when the city plays on the literals, when the world file is not here, when its world is not the Earth, and (with one line) when they would not build. |
| 2768 | 1 | `public LandSellers getLandSellers()` | The nations that sell the city's ground, or null where none does (a generated world, no world file, the literals). |
| 2771 | 5 | `private void handLandSellers()` | Hands the land office the nations that sell its ground, before the offers are listed or restored (LandMarket.setSellers()). |
| 2778 | 9 | `private String moneyOf(int nation)` | The ISO code of the money a nation uses this month (the path's), or null where it is not known. |
| 2789 | 16 | `private int landNationCount()` | How many nations the world file names, for the tally of what was bought from whom; 0 for no file (and on the literals). |
| 2807 | 3 | `public int landSellerCount()` | How many nations the tally of the land bought counts: the world file's, 0 where there is no file. |
| 2812 | 3 | `public double getLandBoughtKm2(int nation)` | What the city has bought from a nation since founding, in square kilometres (0 for index 0, a purchase with no seller named, the host's on a generated world; 0 for a nation it has bought nothing from and for a number ... |
| 2817 | 3 | `public double getLandBoughtUsd(int nation)` | ...and what it paid them, in thousands of US dollars. |
| 2822 | 4 | `void setLandBoughtForTest(double[] km2, double[] usd)` | Harnesses only (WN5): the tally of what was bought from whom, set by hand (a nation count + 1 long each, or null for nothing bought). |
| 2828 | 18 | `private void tallyLandBought(LandParcel parcel, List<LandParcel.Sale> sold)` | A purchase added to the tally: each part to its seller, a purchase with no seller named (or a seller the tally does not count) to index 0. |
| 2848 | 11 | `private void restoreLandBought(DataSave loaded)` | The tally as a save read it: kept only when both rows are the file's nation count + 1 long, else nothing bought (zeros). |
| 2867 | 3 | `private String soldByWords(List<LandParcel.Sale> sold)` | "Sold by United States (62%, US$1.3M) and Canada (38%, C$1.1M)." - each part's share of the offer's area and its price in its own money at today's rate (WorldMoneys.rate(); in US$ where the moneys are not built or the... |
| 2872 | 9 | `private String inSellersMoney(LandParcel.Sale s)` | A part's price in the seller's own money at today's rate: "C$1.1M", "MXN 2.3M"; in US$ where the money or its rate is not known. |
| 2883 | 3 | `private static String usdWords(double thousands)` | Thousands of US dollars as the screens write them: "US$101.8M" (Formats.amount() with the dollar's mark). |
| 2888 | 3 | `private static String localWords(String here, double thousands)` | ...and thousands of local money, with its own mark: "D$72.0M". |
| 2899 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 2906 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's price by (red when not, since 0.7.26 the only verdict on it); since 0.7.13 its button asks land... |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 2910-3067)

| line | len | member | says |
|---:|---:|---|---|
| 2943 | 5 | `public java.util.List<LandParcel> landShelf()` | The offers standing in the land office's order: cheapest ground first, per square foot of dry ground in US dollars (0.7.57: all forty) - the top-left card first. |
| 2950 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 2960 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 2970 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 2985 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 2990 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 2995 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 3007 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 3013 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 3018 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 3028 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 3059 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 3064 | 3 | `public java.util.List<LandParcel> getLandListing()` | The offers standing. |

### THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) (lines 3068-3215)

| line | len | member | says |
|---:|---:|---|---|
| 3102 | 17 | **type** `public record LandNeed(Kind kind, double drySqFt, Resource resource)` | What the city needs ground for: room in general, a shortfall of dry ground, a resource's deposit, or a coast. |
| 3105 | 1 | **type** `public enum Kind` _(in Game.LandNeed)_ | The four needs. |
| 3108 | 1 | `public static LandNeed room()` _(in Game.LandNeed)_ | Room to grow. |
| 3111 | 1 | `public static LandNeed shortfall(double drySqFt)` _(in Game.LandNeed)_ | This much more dry ground than the city has free. |
| 3114 | 1 | `public static LandNeed deposit(Resource resource)` _(in Game.LandNeed)_ | A site of this resource to stand a mine or a well on. |
| 3117 | 1 | `public static LandNeed coast()` _(in Game.LandNeed)_ | Sea, for a desalination plant (batch J2). |
| 3121 | 34 | `public LandParcel bestOffer(LandNeed need)` | The offer that best meets a need, by THE BEST OFFER's rules; null when none does. |
| 3156 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 3164 | 4 | `public void watchOrders(BusinessInvestment.OrderWatch watch)` | Harnesses only: tell this watch every order the three searches decide; null to stop. |
| 3169 | 3 | `public String getLastInvestment(String sector)` |  |
| 3185 | 11 | `private void deferBorrowingCosts()` | WHAT EACH BUSINESS'S BORROWING COST IT UP FRONT THIS MONTH (0.7.102, Jerus's A16): the fees the bank kept out of its loans, its mortgages' insurance premiums and its bonds' issuing costs - the month's own figures, the... |
| 3209 | 1 | `public boolean hasMonthRun()` | True once a month has run since the city was founded or loaded; false until then (0.7.102). |
| 3212 | 3 | `public java.util.Map<String, String> getLastInvestments()` | Every word the investors left last month, by the slot it was filed under (a sector's key, or the bank branch's label) - saved since 0.7.102 (A22); read-only. |

### PRIVATE INVESTMENT (lines 3216-3865)

| line | len | member | says |
|---:|---:|---|---|
| 3226 | 110 | `private void runPrivateInvestment()` |  |
| 3345 | 27 | `void noteStaffingHold(Sector.Staffing held)` | A planner was refused by the staffing gate (LD1; Decision.heldForStaff): sorts the hold for WantedPosts. |
| 3378 | 9 | `void settleWantedPosts()` | The month's asks settled into the posts per band the same month's reprice prices the wage on (LD1; WantedPosts.settle()). |
| 3406 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 3419 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 3431 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 3437 | 427 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 3866-3964)

| line | len | member | says |
|---:|---:|---|---|
| 3913 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 3919 | 1 | `public HomeFuel getHomeFuel()` | The homes' lamp oil and coal, for the harnesses that set its shares and the screens that will read its sale. |
| 3922 | 5 | `private double homeFuelHomes()` | The homes the lamp oil and the coal are asked of: the doors the households pay rent on, the sum of the rows' doors the books split the rent and the home fuel bill by (HouseholdAccounts.rowDoors). |
| 3929 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 3932 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 3933 | 1 | `public double getHouseholdCarImports()` |  |
| 3936 | 1 | `public double getHouseholdFuel()` | ...and what they paid for fuel this month (0.7.49): what the cells paid - the refiners' shelf and the world's (0.7.62). |
| 3939 | 1 | `public double getHouseholdFuelImports()` | ...and what of it they paid the world (0.7.62): every litre with no refinery; the money audit's FuelFunded and FuelImports. |
| 3945 | 1 | `public double getRowFeesSettled(int row)` | ...one row's. |
| 3948 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 3951 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 3954 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 3957 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 3960 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 3963 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 3965-3998)

| line | len | member | says |
|---:|---:|---|---|
| 3973 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 3976 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 3979 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 3982 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 3985 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 3988 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 3991 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 3994 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 3997 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 3999-4549)

| line | len | member | says |
|---:|---:|---|---|
| 4025 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 4044 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 4055 | 3 | `public double bankTaxUnder(TaxPolicy p)` | The bank's profit tax under another policy (0.7.36, the Policy spec's M2): Bank.taxAt() at that policy's retail rate, bankProfitTaxRate()'s rule - so NEXT month's bill, the tax being in arrears. |
| 4068 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 4085 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 4093 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 4121 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 4153 | 64 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 4225 | 16 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 4315 | 98 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 4426 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 4439 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 4466 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 4483 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 4510 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 4539 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 4550-4696)

| line | len | member | says |
|---:|---:|---|---|
| 4592 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 4595 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 4600 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 4605 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 4610 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 4637 | 50 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 4689 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 4695 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 4697-4818)

| line | len | member | says |
|---:|---:|---|---|
| 4705 | 12 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 4719 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 4722 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 4725 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 4728 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 4731 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 4741 | 21 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 4764 | 7 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 4784 | 29 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 4815 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 4819-4878)

| line | len | member | says |
|---:|---:|---|---|
| 4822 | 1 | `public double fundSharesValue()` | Its shares, both books, at the city's mark (Exchange.cityMark(): the last trade, fair value once that is a year old). |
| 4824 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 4826 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 4832 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 4834 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 4836 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes, on the bank's share at the city's mark. |
| 4840 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 4842 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 4846 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 4851 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands, at the withdrawal in force: TreasuryFund.withdrawalOn(fundValue()) - transferOn() at the default. |
| 4854 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the city's mark: the Holdings page's line for it until 0.7.39 (FundView reads each book itself now). |
| 4858 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 4862 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 4864 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 4869 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 4873 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 4879-5042)

| line | len | member | says |
|---:|---:|---|---|
| 4882 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 4883 | 7 | `public void setFundDial(double dial)` |  |
| 4892 | 1 | `public double getFundWithdrawal()` | The fund's withdrawal, a share of its whole value a month (0.7.48, C1): TreasuryFund.getWithdrawal(), Norway's rule by default. |
| 4894 | 8 | `public void setFundWithdrawal(double share)` | ...set by the player in whole steps of TreasuryFund.WITHDRAWAL_STEP, from nothing to 10% a month, and recorded. |
| 4904 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 4905 | 8 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 4927 | 18 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, plus the gain on debt retired that the surplus carries and sp... |
| 4951 | 4 | `private static double exchangeFromEnd(double[] exchange, int n, int i)` | The exchange gain (CD7) the history kept for the month at index {@code i} of a series {@code n} long that ends on the same month: the exchange series is read from its end as the retired gain is, 0 for a month it does ... |
| 4962 | 10 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 4979 | 10 | `public double debtExchangeThisYearSoFar()` | CD7: the exchange gain (loss) on the foreign debt that {@link #surplusThisYearSoFar()} leaves out, over the same months (this calendar year's closed ones, in the months the surplus is recorded), a gain positive: what ... |
| 4995 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 5023 | 19 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 5043-5159)

| line | len | member | says |
|---:|---:|---|---|
| 5046 | 11 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 5059 | 11 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 5076 | 1 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 5079 | 8 | `public void fundBuyShares(int company, double money, double limit)` | ...at a price a share the player names (0.7.39, the spec's D2): 0 is fair value, what the rule asks at (it bids TreasuryFund.RULE_PREMIUM over since 0.7.48). |
| 5089 | 1 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 5092 | 8 | `public void fundSellShares(int company, double shares, double limit)` | ...at a price a share the player names (0.7.39): 0 is fair value. |
| 5102 | 1 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 5105 | 6 | `public void fundBuyBond(int bondId, double money, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 5113 | 1 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |
| 5116 | 7 | `public void fundSellBond(int bondId, double face, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 5125 | 3 | `public double fundCashFree()` | The fund's cash a new buy may hold: its cash less what the hand's buys already hold (TreasuryFund.handReserve(), 0.7.39). |
| 5130 | 4 | `private static String shareCount(double n)` | A count of shares in a decision's words: whole from a hundred, else to two places, and four under one - a consolidated holding is a fraction (0.7.39). |
| 5136 | 5 | `private String atLimit(double limit, boolean bond)` | A decision's words for a named price: " at D$101.35k", " at 102.80 per 100"; nothing at fair value. |
| 5150 | 9 | `public boolean fundCancelOrder(int i)` | CANCELS ONE OF THE PLAYER'S ORDERS before the step posts it (0.7.39, the spec's D3): the order at this place in TreasuryFund.getHandOrders(). |

### an Insane founding (lines 5160-5288)

| line | len | member | says |
|---:|---:|---|---|
| 5177 | 8 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar bond (a term loan until CD2), LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.lan... |
| 5217 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 5240 | 1 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 5246 | 13 | `public double buyForeignCurrency(double amount, String code)` | ...into one money of the eight (the Reserves page's money picker; WN4, THE VAULT IN EIGHT); null goes in by the reserve mix, as the one-argument purchase does. |
| 5271 | 1 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 5277 | 11 | `public double sellForeignCurrency(double amount, String code)` | ...out of one money of the eight (WN4, THE VAULT IN EIGHT): the clamp is what that money alone holds, and no other gives up anything; null sells from the largest holding, as the one-argument sale does. |

### WHAT THE D$ IS HELD TO (XR2, 2026-10-22; ForeignAccounts, THE REGIMES) (lines 5289-5366)

| line | len | member | says |
|---:|---:|---|---|
| 5299 | 7 | `public void setCurrencyRegime(ForeignAccounts.Regime regime)` | Chooses how the D$ is held: Float, Lean against falls (the default), Band or Peg. |
| 5308 | 8 | `public void repeg(double rate)` | Moves the peg (a higher rate is a devaluation); nothing outside a peg. |
| 5318 | 9 | `public void setBand(double centre, double slopeAYear, double width)` | Sets the band: its centre, the log a year it crawls and the share either side its edges sit. |
| 5329 | 8 | `public void setEdgePayer(ForeignAccounts.EdgePayer payer)` | Chooses who pays for the dollars the vault buys at the strong edge. |
| 5350 | 16 | `private void settleTheEdge()` | THE STRONG EDGE'S BILL (XR2, star 6), at the top of the month, inside the audit's window, after the redemptions: the dollars the vault took at the last reprice (ForeignAccounts.edgeBuy()) are paid for here, at the loc... |

### THE STRATEGIC RESERVE (0.7.85, batch O8; docs/studies/spec-oil.md 2.8) (lines 5367-5476)

| line | len | member | says |
|---:|---:|---|---|
| 5394 | 10 | `public double fillReserve(double tonnes)` | Orders crude for the strategic reserve: `tonnes`, cut to the room its tanks have left and to what the treasury could pay at crude's import price (what no tonne costs more than) - bought in the month's crude market and... |
| 5411 | 6 | `public double reserveFillMost()` | The most a fill order would take now (fillReserve()'s cut): the room the reserve's tanks have left, and what the treasury could pay at crude's import price - the reach of the Fill lever on Oil's page (0.7.96, OilView). |
| 5425 | 12 | `public double releaseReserve(double tonnesAMonth)` | Releases the strategic reserve's crude, `tonnesAMonth` a month from the next clearing on, as long as it holds any; nothing stops it. |
| 5445 | 12 | `private void settleReserve()` | The reserve's month, settled at the strike beside the businesses it traded with (StrategicReserve.settle()): the treasury pays for the crude bought - a promise, the crude having landed - and takes in what the refiners... |
| 5459 | 11 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now - and since 0.7.107 what each zone holds and at what share of the price (BusinessInvestment, THE INVESTORS' GROUND BY ZONE). |
| 5472 | 4 | `private String unlinkedClause()` | The clause the "Could not build" words end with while the city owns ground it cannot build on (ET5): "; 12.0 km² is free on Island 2, which no port links"; empty while it owns none. |

### ZONING (0.7.107, batch Z1; Jerus, 2026-10-08, the project's (lines 5477-5558)

| line | len | member | says |
|---:|---:|---|---|
| 5492 | 1 | `public Zoning getZoning()` | The city's zones and its forest law. |
| 5504 | 12 | `public void setZone(String sector, Zoning.Size kind, double size, double priceShare)` | Zones a sector's ground, or re-sizes or re-prices its zone: `size` a share of the city's dry ground (Zoning.Size.SHARE, 0 to 1) or square kilometres (KM2); `priceShare` 0 to 1 of the going business price, what the nex... |
| 5518 | 4 | `static String zoneSizeWords(Zoning.Zone z)` | A zone's size as the log writes it: "2% of the dry ground", "40 km²". |
| 5524 | 3 | `public void removeZone(String sector)` | Un-zones a sector: what its zone held goes back to everyone; what was sold in it stays with its owner at what it paid. |
| 5529 | 9 | `public void setForestLaw(double protectedShare, double keptShare)` | The forest law (Z6): `protectedShare` of the city's forest ground never cut nor built on, `keptShare` - at least that - never built on. |
| 5540 | 8 | `public void setZoneTax(String sector, Zoning.Tax tax, double points)` | A zone's tax row moved (Z5): the sector's own offset, in rate points - annual points for property - exactly as Policy > Taxes moves it (TaxPolicy's setters, their clamps and their log lines). |
| 5550 | 8 | `public double zoneTaxRate(String sector, Zoning.Tax tax)` | ...and what a zone's tax row reads: the sector's effective rate (TaxPolicy.effective*(), the property rate annual). |

### SHRINKING (lines 5559-5842)

| line | len | member | says |
|---:|---:|---|---|
| 5578 | 77 | `private void runRetirement()` |  |
| 5671 | 28 | `private int retireWornOutWells()` | THE WELLS PAST THEIR LIFE (0.7.84, batch O7; spec-oil 2.6): every land well lifting under ten barrels a day (sectors.Oil.wornOut(): 263 months on land) is retired this month, oldest first, by the path any retired buil... |
| 5710 | 22 | `private int decommissionAtSea(ham.citybuildersim.sectors.Oil wells)` | THE OIL AT SEA DECOMMISSIONED (0.7.91, batch O10): once the city's oil is worked out, every platform's jacket that holds no well, and - with no jacket left standing - every kilometre of pipeline, retired by the path a... |
| 5735 | 1 | `public int getWellsWornOut()` |  |
| 5744 | 15 | `private void closeBranches(int wanted)` | Closes branches (0.7.19: as many as the rule says, at once; one at a time from 0.7.11 round 2 until then): the buildings retired by the path any retired building takes (retire()), sold by their owner, retail. |
| 5762 | 1 | `public int getBranchesClosed()` |  |
| 5775 | 67 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 5843-5892)

| line | len | member | says |
|---:|---:|---|---|
| 5863 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 5875 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 5884 | 1 | `public int getConstructionShedMonth()` |  |
| 5885 | 1 | `public double getConstructionShedPoints()` |  |
| 5888 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 5893-6221)

| line | len | member | says |
|---:|---:|---|---|
| 5913 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 5926 | 3 | `public java.util.Map<String, String> getLandBlockedNeeds()` | Sector key to the ground it asked for this month, for every sector in getLandBlockedSectors(). |
| 5931 | 5 | `private void blockOnLand(String sector, String needs)` | A sector waiting on ground, and what it needs: a land refusal's reason less its "no land - " (BusinessInvestment.landReason()). |
| 5946 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 5966 | 8 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 5976 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 5983 | 3 | `public double getLastWriteOff()` |  |
| 6026 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 6036 | 185 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) (lines 6222-6588)

| line | len | member | says |
|---:|---:|---|---|
| 6334 | 4 | `public static int deskCallsMost(int quantity)` | The most times an order of this many may ask the bond desk: the countdown's slices, then doubling down, halving and halving again above the first slice that borrows, each at most once a bit of the order, and that slic... |
| 6345 | 1 | **type** `public record Afford(int quantity, boolean atPrime, double firstRate, int deskCalls)` | What consider() decided of an order (0.7.54): the largest slice that carries its interest, 0 for none; whether any slice would have carried it at prime and its record, for the refusal; the rate at the whole order; and... |
| 6348 | 3 | `Afford sliceFor(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | Harnesses only (InvestCheck, 0.7.113; BuildCardCheck 11, 0.7.122): consider()'s answer to an order with `cash` in the till and `perUnitProfit` a building - the largest slice that carries the interest on its whole cost. |
| 6353 | 6 | `private Afford largestSliceThatCarries(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | The largest slice of the order that carries its interest - see THE LARGEST SLICE, WITHOUT COUNTING TO IT. |
| 6361 | 3 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes)` | largestSlice() for a test with no first slice that borrows to look at: the countdown's slices, doubling down and halving. |
| 6383 | 41 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes, java.util.function.IntSupplier borrowsFrom)` | The largest n from 1 to quantity that passes, 0 for none, found as THE LARGEST SLICE, WITHOUT COUNTING TO IT says: the countdown's first COUNTDOWN_SLICES, then doubling down, then halving - and, when the caller names ... |
| 6426 | 67 | **type** `private final class Slices` | One order's slices, each asked as the countdown asked it (until 0.7.54, consider()'s loop). |
| 6435 | 6 | `Slices(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` _(in Game.Slices)_ |  |
| 6443 | 4 | `int firstThatBorrows(int q)` _(in Game.Slices)_ | The first slice the till does not pay for, which largestSlice() looks at (OS1): one more than the largest whose cost the till covers, quantity + 1 when it covers the order, 0 under a ban (no slice that borrows is built). |
| 6449 | 14 | `boolean passes(int n)` _(in Game.Slices)_ | Whether n of them carry the interest on their whole cost (0.7.113); what failed at prime and the rate at the whole order are kept for the refusal. |
| 6465 | 4 | `boolean carriesAtPrime(int n, double on)` _(in Game.Slices)_ | Whether n of them would carry the interest on `on` at prime and the sector's record - their whole cost since 0.7.113 (orderGate()). |
| 6471 | 12 | `void askPrimeOfTheRest(int q)` _(in Game.Slices)_ | When none passes: whether any of the q slices would have carried it at prime - every slice to PRIME_SCAN_SLICES, the four that decide it past that, and under a ban the last the till pays for. |
| 6485 | 5 | `void askPrime(int n)` _(in Game.Slices)_ | Whether n of them would have carried it at prime, as the countdown asked it of a slice that failed. |
| 6491 | 1 | `Afford result(int quantity)` _(in Game.Slices)_ |  |
| 6495 | 1 | **type** `private record Judged(double rate, double tested, boolean askedDesk)` | The rate a slice is judged at: the loan's, and the one tested - a bond's blended with the bank's where one is cheaper and raises it all; and whether the bond desk was asked. |
| 6498 | 25 | `private Judged judged(String sector, double borrowed)` | ...for a slice of `sector`'s that borrows `borrowed`. |
| 6535 | 4 | `public boolean carriesItsWholeCost(String sector, double earns, double cost)` | Whether an order by `sector` costing `cost` and earning `earns` a month would carry the interest on its whole cost, as consider() asks each slice (ON ITS WHOLE COST, 0.7.113): it earns something, and at least PROFIT_O... |
| 6552 | 6 | **type** `public record InterestTest(double earns, double cost, double on, double rate, double real, double interest,...` | carriesItsWholeCost()'s question with its figures (0.7.122, batch C1), for the build card's interest gate (BuildCard, INTEREST), so the card says what consider() would and works none of it out itself: what the order e... |
| 6556 | 1 | `public double needs()` _(in Game.InterestTest)_ | What it must earn a month to carry it: PROFIT_OVER_INTEREST times a month's interest, the bar servicesItsOwnDebt() holds it to. |
| 6560 | 10 | `public InterestTest interestTest(String sector, double earns, double cost)` | ...for an order by `sector` costing `cost` and earning `earns` a month, its till and what it holds abroad paying first. |
| 6582 | 1 | `public ham.citybuildersim.sectors.SpreadPlanner.MoneyGate orderGate()` | The rule consider() tests orders by: ON_ITS_WHOLE_COST unless a check or a probe set another. |
| 6585 | 3 | `public void setOrderGate(ham.citybuildersim.sectors.SpreadPlanner.MoneyGate gate)` | A check's or a probe's: tests every order consider() weighs by another rule (the counterfactual); null puts the whole cost back. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 6589-6687)

| line | len | member | says |
|---:|---:|---|---|
| 6646 | 3 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 6651 | 4 | `public boolean buysOnMortgage(String sector, BuildingsTemplate t)` | Whether this sector buys this building on an insured mortgage: a landlord's home (0.7.11) - the rule consider() and financingOf() share. |
| 6668 | 19 | `public double[] financingOf(String sector, BuildingsTemplate t)` | HOW ONE OF THESE WOULD BE PAID FOR (0.7.75, the sector statements' R4): consider()'s split of an order of one, read and never acted on - what it would cost (BusinessInvestment.getCostOf()); what the register would ask... |

### the lender's test for one home, for the build card (I13-L) (lines 6688-6868)

| line | len | member | says |
|---:|---:|---|---|
| 6691 | 2 | **type** `private record LenderReading(java.util.function.IntToDoubleFunction costOf, double insured, double tested, ...` | What the lender reads for an order: what n cost, the insured rate and the real rate it is tested at, one's rent net of carrying it, and Mortgage.decide()'s answer. |
| 6695 | 7 | `private LenderReading lenderReads(BuildingsTemplate t, int quantity, double cash, double perUnitRent)` | The lender's reading of an order of `quantity` with `cash` in hand - the one call the landlords' order (considerOnMortgage()) and the build card (lenderTest()) both make, so they cannot disagree. |
| 6712 | 29 | **type** `public record LenderTest(double cost, double cash, double rent, double noi, double insured, double tested, ...` | THE LENDER'S TEST FOR ONE LANDLORD'S HOME (I13-L; the card's door, as interestTest() is the INTEREST gate's): its cost, the funds the landlord would put in (financingOf(): its till, its money abroad and what its owner... |
| 6716 | 1 | `public boolean passes()` _(in Game.LenderTest)_ | Whether the lender writes it (or the landlord's funds buy it outright). |
| 6719 | 1 | `public boolean outright()` _(in Game.LenderTest)_ | The landlord's own funds cover the whole cost: no lender to ask. |
| 6722 | 1 | `public boolean shortOfDown()` _(in Game.LenderTest)_ | Not even one: the funds do not put the down payment on it. |
| 6725 | 1 | `public double down()` _(in Game.LenderTest)_ | The down payment the lender asks of the landlord's own funds on this cost (Mortgage.ownFundsFor()). |
| 6728 | 1 | `public double loan()` _(in Game.LenderTest)_ | What the lender lends against it: the shortfall the funds leave, once the fee is paid (Mortgage.loanFor()); nothing outright. |
| 6731 | 1 | `public double payment()` _(in Game.LenderTest)_ | The monthly payment the test reads, on that loan with the premium added; nothing outright. |
| 6734 | 3 | `public double coverage()` _(in Game.LenderTest)_ | The rent's cover of that payment (net operating income over it), the figure the lender asks asks() of; NaN where it is not read - outright, or short of the down payment. |
| 6739 | 1 | `public double asks()` _(in Game.LenderTest)_ | What the lender asks of that cover: Mortgage.MORTGAGE_DEBT_COVERAGE. |
| 6743 | 8 | `public LenderTest lenderTest(String sector, BuildingsTemplate t)` | The lender's test for one of this landlord sector's homes, as the order would meet it now; null for a building the sector does not buy on a mortgage (buysOnMortgage()). |
| 6760 | 62 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, of what the planner asked for (Mortgage.decide(), which halves since 0.7.54) - and th... |
| 6831 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 6835 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 6848 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 6869-6949)

| line | len | member | says |
|---:|---:|---|---|
| 6907 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 6915 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 6920 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 6925 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 6928 | 21 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 6950-7271)

| line | len | member | says |
|---:|---:|---|---|
| 6963 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 6980 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 7002 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 7007 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 7110 | 1 | `public double getBankedClearedAtLoad()` |  |
| 7113 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 7135 | 27 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 7171 | 5 | `public int getConstructionOutputAtEveryPost()` | What the builders would do this month with every post offered, at the city's fill (0.7.17): the figure getConstructionOutput() was before the builders laid idle crews off. |
| 7178 | 4 | `public int getBuildingOutputAtEveryPost()` | ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. |
| 7192 | 9 | `void strikeBuildersCrews()` | The builders strike the month's crews (0.7.17): the work ahead - the repairs of the city standing now and every point still owed on site after this month's advance - against what their depots would do with every post ... |
| 7213 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 7230 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 7267 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |

### THE FOREIGN CREWS (FC1) (lines 7272-7878)

| line | len | member | says |
|---:|---:|---|---|
| 7285 | 4 | **type** `public record ForeignOffer(int points, double depotPoints, double pricePerPoint)` | What the foreign contractor puts into the sites this month, as it was offered before the advance: the points (a whole number, as the sites' output is), how many of them are the builders' own depots' (served first), an... |
| 7296 | 13 | `public ForeignOffer foreignCrewsOffer()` | The contractor's month, offered: its book (cut first to what is still owed on site, Construction.capForeignBook(); a cancelled order left it when it stopped, FC1b) and, while it takes orders, the builders' own depots ... |
| 7317 | 12 | `public void settleForeignCrews(ForeignOffer offer, double pastOwnMonth)` | The contractor's month settled, after the sites advanced: it built what the sites took past the city's own month (`pastOwnMonth`, the points built less the builders' site output), never more than it offered. |
| 7344 | 10 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 7366 | 3 | `double portsBill()` | The sea terminals' repairs, a month: their upkeep, the berths the city keeps as landlord, paid with transit's bill (TreasuryLine.TRANSIT); nothing with no terminal. |
| 7395 | 81 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 7486 | 1 | `public double getCityMaintenancePaid()` |  |
| 7491 | 4 | `public int getConstructionMaterials()` |  |
| 7495 | 4 | `public double getInterestRate()` |  |
| 7499 | 3 | `public boolean isGraphsEnabled()` |  |
| 7502 | 3 | `public boolean isReportsEnabled()` |  |
| 7529 | 101 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 7632 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 7661 | 1 | `public boolean hasNewReceipt()` |  |
| 7662 | 1 | `public void clearReceipt()` |  |
| 7664 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 7678 | 200 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 7766 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 7775 | 7 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the bitumen its paving takes, quoted (Markets.quote()), and the refiners' sales tax rate it is credited at (0.7.83). |
| 7784 | 9 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the foothill preparation an order carries (ET2): the sticker already holds prepCost, the money for prepPoints. |
| 7795 | 10 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the works a run's land needs on the city's ground (ET4): the sticker already holds worksCost, the money for worksPoints. |
| 7807 | 12 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the span of a long bridge (ET6): the sticker already holds bridgeCost, the money for bridgePoints. |
| 7821 | 50 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ | ...with the deep-water works of a floating platform or its well (DW1): the sticker already holds deepWaterCost, the money for deepWaterPoints. |
| 7873 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 7876 | 1 | `public double unitsBeyondYard()` _(in Game.BuildQuote)_ | The material units the crews will draw beyond the yard, which the allowance was priced on. |

### THE BUILDERS' PRICE (0.7.19) (lines 7879-8228)

| line | len | member | says |
|---:|---:|---|---|
| 7935 | 5 | `private String ownerOfOrder(BuildingsTemplate t)` | Who ordered a building of this kind, for a save that did not say (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays for them (BusinessInvestment.planBank()); the city for the rest. |
| 7949 | 6 | `private double recoveredOnOldContract(String payer, BuildingsTemplate t)` | ...and the share of the tax on it that owner gets back, for the same save (revised 0.7.19): on the tax a building of it carries at today's price (EconomyManager.taxRecoveredShare()). |
| 7968 | 18 | `private double depotWageBillPerPoint(boolean today)` | A Construction Depot's posts - the builders' own job mix - at today's wages or at the founding ladder (PayTier, in today's unit), over the points it makes. |
| 7988 | 3 | `private double buildersSalesRate()` | The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. |
| 7991 | 3 | `private double plantSalesRate()` |  |
| 8002 | 5 | `private double billableMaterial(double salvageCost, double plantCost, double importCost)` | What a draw of material cost the builders, as they bill it on (0.7.19): the salvage at what they paid for it, the plant's net of the credit they claim on it, the world's at its landed cost (its tax is charged and cred... |
| 8018 | 3 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 8023 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds)` | ...against a yard holding `yardHolds` units (0.7.40): an order in a run, priced on the yard the orders before it leave (buildRunInvoice()). |
| 8028 | 3 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead)` | ...and with `bitumenAhead` tonnes of the refiners' bitumen taken by the orders before it in a run (0.7.83; drawBitumen()). |
| 8033 | 3 | **type** `private record GroundAhead(double ground, double works, double runs, double prepared, java.util.Map<Integer...` | What the orders before one in a run have taken of the ground (ET2): their square feet, the part of it works's (and, ET4, the part runs'), and the foothill their orders prepare (buildRunInvoice()); and (FC1) the points... |
| 8034 | 1 | `GroundAhead(double ground, double works, double runs, double prepared)` _(in Game.GroundAhead)_ |  |
| 8040 | 4 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead, GroundAhead ahead)` | ...and with the ground the orders before it in a run take (ET2): the foothill it prepares is what placing it after them would ask. |
| 8051 | 3 | `public BuildQuote quoteBuildOn(BuildingsTemplate selected, int quantity, int asked)` | What an order for a landmass costs (ET5): the quote of a sea terminal ordered for `asked` - Landmasses.CHOSEN (the card's pick, else the rule), RULE or an id - priced on that landmass's pad and the room it stands on, ... |
| 8055 | 63 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds, double bitumenAhead, GroundAhead ahead, ...` |  |
| 8124 | 4 | `private BuildingsTemplate preparationAnchor()` | The House the foothill's preparation is priced on (ET2; SitePreparation): the template named "House", with points to build it and a founding price. |
| 8133 | 5 | `public double preparationPoints(double sqFt)` | Construction points to prepare `sqFt` square feet of foothill: SitePreparation's dollars a m2 over the House's founding cash cost a point, so the points move with nothing but the work. |
| 8144 | 4 | `public double preparationCost(double points)` | What those points cost the builders' customer, before the builders' tax: the builders' price for house work today - the House's non-material cost over its points (BuildingManager.nonMaterialCost()) - so it keeps up wi... |
| 8158 | 5 | `private void notePreparation(BuildQuote quote)` |  |
| 8165 | 1 | `public int preparationOrders()` | How many orders carried a foothill preparation over this game's life: counted from this game's start, not saved. |
| 8168 | 1 | `public double preparationPointsBilled()` | ...the construction points they owed. |
| 8171 | 1 | `public double preparationMoneyBilled()` | ...and their money, before the builders' tax. |
| 8174 | 1 | **type** `private record RunWorksNeed(double sqFt, double points, double cost, double[] mix)` | What a run's order needs built on the city's slopes (ET4): the land priced, the construction points, their money before the builders' tax, and the shares of {flat, foothill, steep, very steep} they were priced at. |
| 8184 | 14 | `private RunWorksNeed runWorksFor(BuildingsTemplate run, int quantity)` | The cuts, viaducts and tunnels a highway or a railway order needs (ET4; RunWorks, spec-earth2-terrain 4.3): the run's land times the works a m2 needs at the city's own mix of dry ground (RunWorks.usdPerM2OfMix(): the ... |
| 8200 | 4 | `public double runWorksCost(BuildingsTemplate run, int quantity)` | What the cuts, viaducts and tunnels of an order of these would cost, before the builders' tax (runWorksFor()); 0 for every order but a run's on a city with ground above flat. |
| 8214 | 5 | `private void noteRunWorks(BuildQuote quote)` |  |
| 8221 | 1 | `public int runWorksOrders()` | How many orders carried the works of a run over a city's slopes over this game's life: counted from this game's start, not saved. |
| 8224 | 1 | `public double runWorksPointsBilled()` | ...the construction points they owed. |
| 8227 | 1 | `public double runWorksMoneyBilled()` | ...and their money, before the builders' tax. |

### BITUMEN FOR THE PAVING (0.7.83, batch O6; docs/studies/spec-oil.md 2.5) (lines 8229-8427)

| line | len | member | says |
|---:|---:|---|---|
| 8255 | 6 | `public double bitumenFor(BuildingsTemplate t)` | Tonnes of bitumen one of these takes (0.7.83): a road - INFRASTRUCTURE with road capacity - past a Gravel Road's material, its surface's tonnes at BITUMEN_BINDER_SHARE; none for anything else, and none in a catalogue ... |
| 8263 | 3 | `public double bitumenForPaving()` | ...and one gravel road paved (0.7.83): a Paved Road's less a Gravel Road's, which is a Paved Road's - the bed has none. |
| 8268 | 3 | `private double refinersSalesRate()` | The refiners' sales tax rate: the builders' credit on the bitumen they buy from them (0.7.83). |
| 8275 | 1 | `public double getBitumenTonnes()` |  |
| 8276 | 1 | `public double getBitumenCost()` |  |
| 8277 | 1 | `public double getBitumenImported()` |  |
| 8286 | 8 | `public Markets.Draw drawBitumen(double tonnes)` | Draws an order's bitumen (0.7.83): the builders buy it as they buy the material beyond the yard (drawMaterials()), off the refiners' tanks and the rest from the world (Markets.draw()), booked in their ledger and bille... |
| 8304 | 4 | `public double quoteMonths(BuildingsTemplate template, int quantity)` | Months an order would take to finish (0.7.17): the wait every planner reads for its lead time and order size - BuildingManager.waitFor(), the order's points over the share of the site output it would get beside everyt... |
| 8317 | 5 | `public double onSiteMonths(BuildingsTemplate template)` | Months what is on site of one building would take to finish (0.7.20): the same wait at the same output as quoteMonths(), with no order added - BuildingManager.waitOnSite(). |
| 8334 | 4 | `public double siteMonths(String key)` | ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on site would take at today's queue - a stack's by its key, or a demolition's - at the output the quote reads. |
| 8347 | 3 | `public double quoteCityMonths(BuildingsTemplate template, int quantity)` | ...and what a city order of n of these would wait if placed now (0.7.22, after the docs pass): quoteMonths() exactly with no order set and no rush on its site; otherwise where the order would land in the player's orde... |
| 8352 | 4 | `public double quoteCityMonths(BuildingsTemplate template, int quantity, double extraPoints)` | ...with `extraPoints` more owed on its site than its buildings' own (ET2: a foothill pad's preparation, BuildQuote.prepPoints); quoteCityMonths() exactly for none. |
| 8382 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 8410 | 9 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |

### MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) (lines 8428-8521)

| line | len | member | says |
|---:|---:|---|---|
| 8458 | 31 | `public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues)` | Settles the month's site work with the owners who ordered it. |
| 8495 | 7 | `private double deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 8518 | 3 | `public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 8522-8930)

| line | len | member | says |
|---:|---:|---|---|
| 8555 | 3 | `public boolean isCitysToDemolish(BuildingsTemplate t)` | What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). |
| 8560 | 3 | `public boolean isBuyOutable(BuildingsTemplate t)` | What the city may buy out and demolish: a building a sector owns. |
| 8569 | 7 | **type** `public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price, d...` | A demolition, priced and laid out BEFORE the player commits: its work, its price, what it closes, what comes back and when. |
| 8574 | 1 | `public double salvageProceeds()` _(in Game.DemolitionQuote)_ | What the builders would pay for the material today, for as much as their cash covers. |
| 8578 | 8 | **type** `public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground, d...` | A buy-out, priced: the compensation, part by part, and the demolition after it. |
| 8582 | 1 | `public double compensation()` _(in Game.BuyOutQuote)_ | What the owner is paid. |
| 8584 | 1 | `public double total()` _(in Game.BuyOutQuote)_ | ...and what it all costs the city, the demolition included. |
| 8597 | 9 | `private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months)` | D. |
| 8608 | 29 | `public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n)` | What demolishing n standing buildings of this kind would do and cost. |
| 8639 | 16 | `public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell)` | A shell's demolition: DEMOLITION_SHARE of the work it holds, SALVAGE_SHARE of the material it drew to salvage (since 0.7.106; all of it until then), its ground. |
| 8657 | 4 | `private double demolitionMonths(BuildingsTemplate t, int n, double points)` | Months a demolition of these points would take at today's queue, at the output the quote reads. |
| 8670 | 16 | `public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n)` | E. |
| 8689 | 1 | `public String getLastHandRefusal()` |  |
| 8692 | 18 | `public boolean demolish(BuildingsTemplate t, int n)` | D. |
| 8712 | 36 | `public boolean buyOutAndDemolish(BuildingsTemplate t, int n)` | E. |
| 8750 | 21 | `public int demolishable(BuildingsTemplate t)` | How many of this kind stand that no demolition has been ordered for - nor, a gravel road, its paving (0.7.70). |
| 8773 | 6 | `private String linkingWords(BuildingsTemplate t, int n)` | Why a port cannot be demolished, if it is the one that links a landmass (ET5 D11): "it is the port that links Island 2"; null when it is not. |
| 8781 | 4 | `private boolean isBuildersDepot(BuildingsTemplate t)` | Whether this template is a builders' depot: owned by Construction and making building work. |
| 8791 | 10 | `private int otherBuildersDepotsStanding(BuildingsTemplate t)` | How many builders' yards and depots of any other Construction template stand and are not closing: one bought out and ordered demolished this month still stands until the month starts (closeDemolished()), and counting ... |
| 8803 | 17 | `private void placeDemolition(DemolitionQuote q, String from, double groundPaid)` | The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). |
| 8831 | 13 | `private void closeDemolished()` | D. |
| 8846 | 24 | `public boolean demolishShell(int templateId)` | C. |
| 8878 | 16 | `public BuildQuote quoteRestart(ConstructionControl.Shell shell)` | C. |
| 8896 | 4 | `private double restartMonths(BuildingsTemplate t, int n, double pointsLeft)` | A restart's wait, by the one wait (BuildingManager.restartWait()). |
| 8902 | 28 | `public boolean restartShell(int templateId)` | C. |

### F. PAVE: A GRAVEL ROAD UPGRADED TO A PAVED ROAD (0.7.70; ConstructionControl, F) (lines 8931-9106)

| line | len | member | says |
|---:|---:|---|---|
| 8934 | 1 | `private BuildingsTemplate paveFrom()` | The road the city can pave and the road it becomes, as this city's catalogue has them; null for one it has not. |
| 8935 | 1 | `private BuildingsTemplate paveTo()` |  |
| 8938 | 6 | `public int paveable()` | Gravel roads the city could pave now: standing, not ordered demolished, and not already being paved. |
| 8946 | 1 | `public int pavingNow()` | Gravel roads being paved now: their Paved Roads on site, the gravel roads still standing and carrying traffic. |
| 8949 | 4 | `public boolean isPavingSite(String key)` | Whether a site is the Paved Road site with paving on it, which is not stopped (ConstructionControl, F). |
| 8968 | 17 | `public BuildQuote quotePave(int n)` | F. |
| 8998 | 28 | `public boolean paveRoads(int n)` | F. |
| 9028 | 13 | `public boolean cancelSite(String key, boolean cancel)` | C. |
| 9049 | 8 | `public double[] cancelRefundNow(String key)` | C. |
| 9059 | 10 | `public boolean rushSite(String key, boolean on)` | B. |
| 9071 | 11 | `public boolean moveSite(String key, int by)` | A. |
| 9084 | 5 | `public void resetSiteOrder()` | A. |
| 9097 | 9 | `public double[] rushPreview(String key)` | B. |

### in the month (lines 9107-9399)

| line | len | member | says |
|---:|---:|---|---|
| 9116 | 1 | `public ConstructionControl.Events getControlThisMonth()` |  |
| 9117 | 1 | `public double getOvertimePaidThisMonth()` |  |
| 9118 | 1 | `public double getDemolitionSalvageThisMonth()` |  |
| 9125 | 86 | `public void settleConstructionControl(ConstructionControl.Events ev)` | Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. |
| 9213 | 14 | `private double[] overtimeWagesByType(double premium)` | The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). |
| 9247 | 148 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction, int landmass)` | them, and charge nothing for the construction. |
| 9398 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 9400-9713)

| line | len | member | says |
|---:|---:|---|---|
| 9415 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 9427 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 9445 | 1 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 9448 | 1 | `public int wellsCommitted()` | ...and the Oil Wells standing, being built or ordered (0.7.62): one an oil site - since 0.7.91 the land wells and the platforms' wells. |
| 9451 | 1 | `public int landWellsCommitted()` | The land wells standing, being built or ordered (0.7.91): what the dry sites are counted against - every oil well until the platforms' came. |
| 9454 | 1 | `public int platformWellsCommitted()` | ...and the platforms' wells (0.7.91): what the platforms' slots are counted against. |
| 9457 | 12 | `private int committedWhere(java.util.function.Predicate<BuildingsTemplate> which)` | The buildings `which` picks, standing, on site or ordered. |
| 9479 | 3 | `public int committedOn(Resource r)` | The buildings that stand on a resource's sites - standing, on site or ordered (0.7.62): every template whose site is the resource's (siteOf()) - and since 0.7.91 not an offshore platform's jacket, which stands on its ... |
| 9489 | 3 | `public int committedOn(Resource r, Deposit.FieldType type)` | ...and the sites taken by the buildings that stand on one type of the resource's fields (0.7.110, batch I1; spec-materials 2.4): `type` null counts those on ANY - the Iron Mine's until batch I5, none of iron's since (... |
| 9501 | 12 | `public java.util.List<LandManager.Claim> claimsOn(Resource r)` | The works standing on a resource's sites, by template (0.7.124, batch I5; spec-materials 2.4): each one's type (null for ANY), the sites it takes and how many stand, are on site or ordered - the standing claims LandMa... |
| 9522 | 3 | `static boolean packs(BuildingsTemplate t)` | Whether an order for this building is placed on the city's fields by packing (0.7.124, batch I5): a works on the sites of a resource whose fields have types (Resource.hasTypes() - iron, and coal, stone and copper sinc... |
| 9526 | 15 | `private int committedOn(Resource r, Deposit.FieldType type, boolean everyType)` |  |
| 9550 | 10 | `public int committedFor(BuildingsTemplate t)` | What an order for this building is counted against its sites with (0.7.91, batch O10): a land well the land wells, a platform's well the platforms' wells, a platform's jacket the jackets on site (those standing have t... |
| 9572 | 3 | `public static Resource siteOf(BuildingsTemplate t)` | The resource whose sites a building stands on (0.7.62): iron for an Iron Mine, oil for an Oil Well; null for anything else. |
| 9583 | 3 | `public static boolean standsAnywhere(BuildingsTemplate t)` | Whether a building is a works that may stand on any ground (ET2, spec-earth2-terrain 3.2 and 4.4): every template with a site (siteOf()) - the mines, the quarries, the wells and the platforms, a pit and its benches be... |
| 9595 | 5 | `public static boolean isRun(BuildingsTemplate t)` | Whether a building is a run (ET4, spec-earth2-terrain 4.3): a highway or a railway line, laid in a line across the land - the Elevated Highway, and the rail TRACK (the Rail Spur, the Freight Line), not the Rail Termin... |
| 9602 | 3 | `public static LandManager.Use groundUse(BuildingsTemplate t)` | What a template's ground is used for (ET4): a works's if it stands anywhere (standsAnywhere()), a run's if it is a highway or a railway line (isRun()), a building's otherwise. |
| 9612 | 5 | `void pushLandByRelief()` | Hands the books the land office's classes of ground (ET3, spec-earth2-terrain 5.3): the prices of a square foot of foothill and of steep ground (flat's is the land's price, pushed beside it), whether the city owns onl... |
| 9619 | 4 | `private void pushLandUse()` | ...and the ground in use alone, as the month closes (the buildings its orders put up included), the prices staying the ones the month began with. |
| 9642 | 3 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 9652 | 14 | `private boolean hasDepositFor(BuildingsTemplate template, int quantity, int before, java.util.List<LandManager.Claim> ahead)` | ...with `before` more on the same sites already put on site by the orders ahead of it in a run (0.7.40, buildRunAhead()) - and since 0.7.124 a packed works with `ahead`, the claims those orders make on its resource's ... |
| 9675 | 9 | `public double remainingFor(BuildingsTemplate template)` | What is left in the ground for a building that stands on a resource's sites (siteOf()): its resource's remainder - and since 0.7.93 (THE TWO OIL POOLS, LandManager) a land well's the ground pool's, a platform's jacket... |
| 9700 | 13 | `public int sitesFor(BuildingsTemplate template)` | The sites the city owns that a building may stand on (0.7.84, batch O7; spec-oil 2.6): its resource's sites (siteOf()) - and for a land well (sectors.Oil.isLandWell()) only the dry ones, a field whose centre is on lan... |

### THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) (lines 9714-9774)

| line | len | member | says |
|---:|---:|---|---|
| 9739 | 3 | `public double getFreshRights()` | The city's water rights, units a month (see THE FRESH WATER LIMIT AND THE COAST). |
| 9744 | 3 | `void setFreshRights(double units)` | Harnesses and the load only: sets the rights, never below nothing. |
| 9749 | 3 | `public double getFreshCap()` | The fresh water limit, units a month: what the owned lakes and river yield plus the rights. |
| 9761 | 8 | `double derivedFreshRights()` | The rights an older save is given (spec-land 2.9): the fresh plants' nameplate standing less what the city's lakes and river yield, never below nothing - raised by the last unit in the last place when the limit they m... |
| 9771 | 3 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity)` | Whether this order can stand on the city's coast: true for everything but a desalination plant and a sea terminal (0.7.86), which need owned sea. |

### THE PORT'S LANDMASS (ET5, 2026-10-25; the ET5 design, D10) (lines 9775-9793)

| line | len | member | says |
|---:|---:|---|---|
| 9786 | 7 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity, int asked)` | Whether this order can stand on the city's coast, on the landmass asked for (Landmasses.CHOSEN, RULE or an id). |

### THE HYDRO STATION'S RIVER (HY1, 2026-10-26; docs/queue/HY1-notes.md 1.2 and 1.3) (lines 9794-9820)

| line | len | member | says |
|---:|---:|---|---|
| 9804 | 3 | `public int hydroSlots(BuildingsTemplate t)` | How many stations of this template the city's linked hydro sites hold in all (HydroSites.slots()); 0 for a template that is not a hydro station. |
| 9809 | 1 | `public int hydroCommitted()` | The hydro stations standing, on site or ordered: what the slots are counted against. |
| 9812 | 3 | `public int hydroSlotsFree(BuildingsTemplate t)` | The slots left for this template: the slots less the stations committed, never below nothing - a site lost never stops a station standing, it only holds new orders. |
| 9817 | 3 | `public boolean hasRiverFor(BuildingsTemplate t, int n)` | Whether this order has a river: true for every building but a hydro station, whose order of n needs n slots free. |

### THE LONG BRIDGES' CROSSING (ET6, 2026-10-26; BridgeSurvey, BridgeWorks, Bridges) (lines 9821-9936)

| line | len | member | says |
|---:|---:|---|---|
| 9839 | 1 | **type** `public record BridgeSpan(double lengthM, double usd, double points, double cost)` | What a long bridge order's span is: the crossing's length (metres, one bridge's), the span's dollars at the founding for the whole order, the construction points that is, and their money before the builders' tax, at t... |
| 9849 | 4 | `public void setBridgeGroundForTest(BridgeSurvey.Ground ground)` | Harnesses only: hands the survey a ground of the harness's making in place of the World and the land (null restores the real one), so that a crossing can be made to exist where the map holds none. |
| 9854 | 5 | `private BridgeSurvey.Ground bridgeGround()` |  |
| 9861 | 7 | `public java.util.List<Landmasses.Row> bridgeChoices(BuildingsTemplate t)` | The landmasses the card offers a bridge to be ordered toward: those the city owns but home, and home alone where it owns no other; empty for a building that is no bridge. |
| 9870 | 3 | `public void chooseBridgeLandmass(BuildingsTemplate t, int landmass)` | The card's pick of the far landmass for the next order of this bridge (ONE order's: spent by it, forgotten at the month's settle); a pick that is not among the choices is passed over by the rule. |
| 9875 | 9 | `public int bridgeLandmass(BuildingsTemplate t)` | The landmass this bridge's order is for now: the card's pick if it is among the choices, else the rule - the first landmass no port links, else the first that is not home, else home. |
| 9891 | 18 | `public BridgeSurvey.Result bridgeSurvey(BuildingsTemplate t)` | The crossing this bridge's next order would be built over (BridgeSurvey): the shortest chain of fresh or shallow water from the city's own dry ground on the far landmass to its own buildable ground on a linked one, or... |
| 9911 | 5 | `public boolean hasBridgeFor(BuildingsTemplate t, int n)` | Whether this order has a crossing: true for every building but a bridge, whose order needs the survey to find one (before land and money: no bond for an impossible bridge). |
| 9923 | 3 | `public BridgeSpan bridgeSpan(BuildingsTemplate t, int quantity)` | What the span of an order of these costs (BridgeWorks): the surveyed crossing's dollars for each bridge of the order, as construction points at the template's own founding price a point, and their money at the builder... |
| 9927 | 9 | `private BridgeSpan bridgeWorksFor(BuildingsTemplate t, int quantity)` |  |

### DEEP WATER'S WORKS (DW1, 2026-10-31; sectors.Oil's DEEP WATER) (lines 9937-10331)

| line | len | member | says |
|---:|---:|---|---|
| 9948 | 1 | **type** `public record DeepWater(double depthM, double factor, double points, double cost)` | What the deep-water works of an order are: the sea the first unit stands in (metres), the price factor against a jacket's, the construction points of the premium, and their money before the builders' tax at the builde... |
| 9956 | 5 | `public DeepWater deepWater(BuildingsTemplate t, int quantity)` | The deep-water works of an order of these: (factor - 1) times the template's construction points for each unit, their money at the builders' price a point today (the template's non-material cost over its points, so it... |
| 9962 | 5 | `private DeepWater deepWaterAt(BuildingsTemplate t, int quantity, double depthM, double factor)` |  |
| 9969 | 1 | `public Bridges getBridges()` | The city's long bridges (ET6): each one built or building, where it runs and whether it stands. |
| 9975 | 19 | `private Bridges.Stock bridgeStock()` | What Bridges reads of the buildings (ET6): the counts of each bridge's template - standing, on site, stopped. |
| 10000 | 7 | `private int islandPortLandmass(BuildingsTemplate t, int asked)` | The landmass a sea terminal's order stands on when it is an island no port links (so that it takes that island's own room, not the linked pool's), else Landmasses.NO_LANDMASS: for every other building, for a city on o... |
| 10009 | 4 | `private int portLandmassOf(BuildingsTemplate t, int asked)` | The landmass a sea terminal's order stands on, linked or not: home for a city on one landmass; NO_LANDMASS for a building that is no sea terminal or when none will do. |
| 10015 | 3 | `public void choosePortLandmass(BuildingsTemplate t, int landmass)` | The card's pick of a landmass for the next order of this sea terminal (ONE order's: spent by it, forgotten at the month's settle); the order then stands there if the city owns shore on it (hasCoastFor()). |
| 10020 | 3 | `public java.util.List<Landmasses.Row> portLandmassChoices(BuildingsTemplate t)` | The landmasses the card offers a sea terminal: those the city owns shore on, home first; empty for a building that is no sea terminal and for a city on one landmass. |
| 10025 | 3 | `public double freeGroundFor(BuildingsTemplate t)` | The ground this building's order has to stand on, square feet: an island's room for a sea terminal ordered for an island no port links, else what its sector may build on (LandManager.freeFor()). |
| 10030 | 5 | `public double freeGroundFor(BuildingsTemplate t, int asked)` | ...for the landmass asked (Landmasses.CHOSEN, RULE or an id): the room an order placed there stands on, as quoteBuildOn() reads it. |
| 10041 | 3 | `public boolean isIslandPortOrder(BuildingsTemplate t)` | Whether this building's next order is a sea terminal for an island no port links (ET5): the order that takes that island's own room (freeGroundFor()), not the linked pool's, and that the not-enough-land page measures so. |
| 10050 | 14 | `public java.util.List<CityMap.BridgeRun> getBridgePicture()` | The long bridges that stand, as the map is handed them (ET6; CityMap's LONG BRIDGES ON THE MAP): each its kind and its path's corner plots, the two landings first and last; null while none stands - every generated wor... |
| 10066 | 3 | `public Landmasses.Picture getLandmassPicture()` | The city's ground by landmass and its linked set as the map is handed them; null while the city owns nothing off its home landmass (Landmasses.picture()). |
| 10071 | 41 | `private Landmasses.Stock portStock()` | What Landmasses reads of the buildings (ET5): the counts of each sea terminal's template - standing, on site, stopped, being demolished. |
| 10122 | 106 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 10238 | 1 | `public Investor getGovernmentInvestor()` |  |
| 10241 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 10243 | 3 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 10253 | 63 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction, int asked)` | ...for a landmass (ET5): a sea terminal stands on `asked` - Landmasses.CHOSEN (the card's pick, else the rule), RULE (the rule alone: the automatic builder never links an island) or an id - resolved once here, after t... |
| 10317 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 10328 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |

### A RUN OF ORDERS (0.7.40) (lines 10332-10520)

| line | len | member | says |
|---:|---:|---|---|
| 10372 | 37 | `public double buildRunInvoice(java.util.Map<BuildingsTemplate, Integer> run)` | What placing these orders in turn would charge altogether: each one's quote on the yard the ones before it leave. |
| 10411 | 3 | `public double buildFundingGap(java.util.Map<BuildingsTemplate, Integer> run)` | What the treasury is short of for the whole run: its invoice less the cash, never below nothing - an overdraft counted in full, as one order's buildFundingGap(template, quantity) counts it. |
| 10416 | 3 | `public int buildRunAhead(java.util.Map<BuildingsTemplate, Integer> run)` | How many of the run's orders, from the first, would pass the checks money cannot fix - ore, the coast, the river (HY1), licences, ground - each placed after the ones before it. |
| 10421 | 5 | `public BuildResult buildRunStop(java.util.Map<BuildingsTemplate, Integer> run)` | What the first order that would not go ahead is refused for - NO_DEPOSIT, NO_COAST, NO_RIVER, NO_BRIDGE (ET6), NO_LICENCE or NO_LAND - or SUCCESS when every order passes. |
| 10427 | 59 | `private int runAhead(java.util.Map<BuildingsTemplate, Integer> run, BuildResult[] stop)` |  |
| 10488 | 7 | `static int sitePool(BuildingsTemplate t)` | The sites an order is counted against in a run (0.7.91): its resource's (siteOf()), a platform's well and a platform's jacket each a pool of their own after the resources; -1 for none. |
| 10497 | 3 | `public double cashShortfall()` | What the treasury is overdrawn by right now (0.7.40): the cash below nothing, or nothing - Finances' "overdrawn by" ask. |
| 10502 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). |
| 10505 | 3 | `public double getTotalBuildingCost()` |  |
| 10508 | 3 | `public String getBuildingName()` |  |
| 10511 | 3 | `public int getBuildQuantity()` |  |
| 10516 | 3 | `public int getReceiptSerial()` | Which receipt this is. |

### THE CITY'S PAPER, SOLD AT AUCTION (CD2, spec-city-debt.md section 4) (lines 10521-11116)

| line | len | member | says |
|---:|---:|---|---|
| 10563 | 3 | `public Auction.Bidders auctionBidders()` | The bidders as they stand, for a sale of paper of any term: the figure the screen's chart of the bids reads. |
| 10600 | 3 | `public Auction.Bidders auctionBidders(int months)` | The bidders as they stand for paper of this many months (0 for none in particular): each reading the model, none holding anything of its own. |
| 10610 | 41 | `public Auction.Bidders auctionBidders(int months, double face)` | ...for a sale of this face, which only matters to a bank not yet capitalised: it then stands as the residual buyer for the whole of the face (below). |
| 10653 | 3 | `private Auction.Result runAuction(Auction.Kind kind, int months, double face, double lineCoupon, Auction.Cap cap)` | The auction for this paper, now: fair with the issue on the books, the bidders as they stand, the cap given (the default if none). |
| 10669 | 45 | `private Auction.Result runAuction(Auction.Kind kind, int months, double face, double lineCoupon, Auction.Cap cap, int firstCall...` | ...of a bond that may be called (CD4): firstCallAhead is the month, counted from now, the city may first call it - under 0 for paper that cannot be. |
| 10726 | 3 | `private boolean isTinySale(Auction.Result r)` | A sale that is no sale (CD2): the bids fell short of the issue at the city's cap and what they did buy is under the smallest deal worth arranging (minimumIssueSize()), or the fixed fee eats all it raises. |
| 10737 | 3 | `private static DebtQuote tinySaleQuote(String instrument, int duration, double requested, double before, Auction.Result r)` | The quote of a tiny sale: empty - no face, no cash, nothing to book, which every caller already reads as nothing to be had - and carrying the auction's result, so its summary can say what the bids would have bought an... |
| 10742 | 3 | `private static DebtQuote emptyQuote(String instrument, int duration, double before)` | A quote of nothing: the standing rate before and after, no face. |
| 10755 | 3 | `private static double cents(double v)` | Money to the cent, as every proceeds figure has been (the cent of today's money, at the founding's price level). |
| 10776 | 36 | `private DebtQuote solveForCash(double cash, double rounding, java.util.function.DoubleFunction<DebtQuote> at)` | The smallest face whose proceeds cover `cash`: a first face at par and the fee, then as many granules more as the shortfall needs at what a unit of that sale banked, until it covers - hard bounded (64 turns), because ... |
| 10814 | 3 | `private static boolean covers(DebtQuote q, double cash)` | A quote that raises the cash and sold what it was asked to: the test the solver's descent keeps. |
| 10825 | 3 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a bill, sold at auction for this much cash, would cost. |
| 10830 | 9 | `public DebtQuote quoteTBill(double amount, int duration, double rounding, Auction.Cap cap)` | ...with the highest yield the city will take, a cap of the caller's. |
| 10841 | 8 | `private DebtQuote billQuote(double requested, int months, double face, double before, Auction.Cap cap)` | The bill's quote at a face: the auction run on it, what it sold and raised. |
| 10857 | 3 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a bill on exactly the terms quoted. |
| 10862 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10875 | 8 | `private String issueNote(DebtQuote quote)` | The booking every bill shares - handleTBillLogic()'s, and the rollover's (issueForRollover()). |
| 10890 | 7 | `private void bookProceeds(DebtQuote quote)` | The treasury is paid what a sale at home raised, and the market owes it until the settle: the counters the settle and the audit read. |
| 10906 | 5 | `private void bookNewPiece(Debt paper, DebtQuote quote)` | A new piece, sold: told what it raised and at what yield, given its allotment, and the treasury paid. |
| 10919 | 3 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What an amortizing bond, sold at auction for this face, would cost. |
| 10924 | 10 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10936 | 12 | `private DebtQuote mediumQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The amortizing bond's quote at a face: the auction run on it, the coupon its stop sets, the cost of the credit over the schedule. |
| 10954 | 3 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books an amortizing bond on exactly the terms quoted. |
| 10959 | 6 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 10967 | 8 | `private String issueMediumBond(DebtQuote quote)` | The booking an amortizing bond shares: a new line at the quote's coupon. |
| 10986 | 3 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a bond, sold at auction for this face, would cost. |
| 10991 | 3 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11006 | 13 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...sold callable if asked (CD4, spec-city-debt.md section 2.6): a bond of ten years or more with the city's right to call it whole at 100 from its first call date (CallTree.firstCallYears()). |
| 11029 | 7 | `public double callSpread(DebtQuote quote)` | What the call adds to a callable sale's yield (CD4, spec-city-debt.md section 2.6): the price of the option on the tree, which runAuction() puts on the fair yield of the same bullet without it - read off the quote's o... |
| 11038 | 3 | `private DebtQuote bondQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The bond's quote at a face: the benchmark line it reopens, or the new line it opens, and what the auction made of it. |
| 11043 | 22 | `private DebtQuote bondQuote(double requested, double face, int years, double before, Auction.Cap cap, boolean callable)` | ...of paper that may be called, if it is (CD4): priced from the months the line, or the new line, has to its first call. |
| 11067 | 3 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a bond on exactly the terms quoted. |
| 11072 | 3 | `public String handleLongBondLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11077 | 8 | `public String handleLongBondLogic(double amount, int duration, double rounding, Auction.Cap cap, boolean callable)` | ...sold callable if asked (quoteLongBond()): refused with CallTree.REFUSAL for a term that may not be. |
| 11092 | 24 | `private String issueLongBond(DebtQuote quote)` | The booking both bond issues share. |

### A BOND SIZED TO THE CASH IT BRINGS (0.7.10, CD2) (lines 11117-11171)

| line | len | member | says |
|---:|---:|---|---|
| 11143 | 3 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a bond whose CASH covers cashNeeded would cost. |
| 11148 | 9 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11159 | 3 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a bond sized to the cash, on exactly the terms quoted - the build screen's bond. |
| 11164 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |

### THE FLOATING-RATE NOTE AND THE INFLATION-LINKED BOND (CD5, (lines 11172-11333)

| line | len | member | says |
|---:|---:|---|---|
| 11195 | 3 | `public DebtQuote quoteFloatingNote(double amount, int duration, double rounding)` | What a floating-rate note, sold at auction for this face, would cost. |
| 11200 | 10 | `public DebtQuote quoteFloatingNote(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11212 | 10 | `private DebtQuote floatingQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The floater's quote at a face: the auction run on it, the margin its stop sets, the monthly coupon at today's dial. |
| 11224 | 3 | `public String handleFloatingNoteLogic(double amount, int duration, double rounding)` | Books a floating-rate note on exactly the terms quoted. |
| 11229 | 7 | `public String handleFloatingNoteLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's: refused with FloatingRateNote.REFUSAL for any term but 2 or 5 years. |
| 11238 | 9 | `private String issueFloatingNote(DebtQuote quote)` | The booking a floating-rate note shares with the rollover: a new line at the margin the quote carries, the policy rate now its first index. |
| 11249 | 3 | `public DebtQuote quoteFloatingNoteForCash(double cashNeeded, int duration, double rounding)` | What a floating-rate note whose CASH covers cashNeeded would cost: the quote at the face that brings it (solveForCash()). |
| 11254 | 9 | `public DebtQuote quoteFloatingNoteForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11265 | 3 | `public DebtQuote quoteLinkedBond(double amount, int duration, double rounding)` | What an inflation-linked bond, sold at auction for this face, would cost. |
| 11270 | 10 | `public DebtQuote quoteLinkedBond(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11282 | 10 | `private DebtQuote linkedQuote(double requested, double face, int years, double before, Auction.Cap cap)` | The linker's quote at a face: the auction run on it, the real coupon its stop sets, the monthly coupon on the face as the index stands. |
| 11294 | 3 | `public String handleLinkedBondLogic(double amount, int duration, double rounding)` | Books an inflation-linked bond on exactly the terms quoted. |
| 11299 | 7 | `public String handleLinkedBondLogic(double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's: refused with InflationLinkedBond.REFUSAL for any term but 10 or 30 years. |
| 11308 | 9 | `private String issueLinkedBond(DebtQuote quote)` | The booking an inflation-linked bond shares with the rollover: a new line at the real coupon the quote carries, the price index now its base. |
| 11319 | 3 | `public DebtQuote quoteLinkedBondForCash(double cashNeeded, int duration, double rounding)` | What an inflation-linked bond whose CASH covers cashNeeded would cost: the quote at the face that brings it (solveForCash()). |
| 11324 | 9 | `public DebtQuote quoteLinkedBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 11334-11747)

| line | len | member | says |
|---:|---:|---|---|
| 11379 | 14 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 11417 | 11 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 11443 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation - expected, since 0.7.42 (below) - less the world's rate (the US's policy rate where the city has the world file... |
| 11456 | 5 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the inflation savers expect (Expectations, since 0.7.42 - the same expected inflation the real rate differential reads; the year's, as the parity ... |
| 11463 | 3 | `public double realPolicyRate()` | The policy rate less the inflation people expect (0.7.45): the dial in real terms, ex ante as every real rate here is since 0.7.42. |
| 11475 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 11480 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 11483 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 11498 | 3 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bill or bond of this size would cost. |
| 11511 | 47 | `public DebtQuote quoteForeign(String type, String money, double requested, int duration, double rounding)` | ...in any money of the eight (CD8, Jerus 2026-10-23): the same quote on THAT money's curve - its central bank's policy rate (the world's price of money in it, DebtManager.worldRate(code)) plus the city's country premi... |
| 11560 | 3 | `private static boolean isFloatOrLinked(String type)` | True for the two instruments that are written at home only (CD5): the floating-rate note and the inflation-linked bond. |
| 11568 | 3 | `private static double dollarCoupon(YieldCurve world, int months)` | A dollar bond's coupon on a world curve: its par yield at the term, rounded down to Auction.COUPON_STEP. |
| 11590 | 12 | `private YieldCurve selfPricedWorldCurve(String code, double face, int months, java.util.function.Function<YieldCurve, Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT ON THE WORLD'S CURVE (CD1; the walk of 0.7.2 on a single rate before it): the world's price of paper that is priced payment by payment. |
| 11607 | 4 | `private static Debt inMoney(Debt shape, String code)` | A piece not yet booked, told the money it is written in (CD8) so the world's quote prices it at that money's rate: the shape of selfPricedWorldCurve(). |
| 11613 | 4 | `private double issueCostIn(String code, double face)` | The fixed cost of an issue (costOfIssuance()) in the money the paper is written in: the figure itself for the US$, as a dollar quote has always taken it, and local money over localPer() for another (CD8). |
| 11624 | 4 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 11635 | 88 | `public String handleForeignLogic(String type, String money, double requested, int duration, double rounding, boolean holdAsRese...` | ...in any money of the eight (CD8): the piece is booked in {@code money} (Debt.getMoney()), opens its books and notes its sale in that money's units, and what it raised reaches the treasury in local money at that mone... |
| 11730 | 3 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 11735 | 12 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's (CD2). |

### THE AMORTIZING BOND AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 11748-11833)

| line | len | member | says |
|---:|---:|---|---|
| 11772 | 3 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | An amortizing bond whose CASH covers cashNeeded: the quote at the face that brings it. |
| 11777 | 9 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding, Auction.Cap cap)` | ...at a cap of the caller's. |
| 11792 | 3 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note or bond whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 11797 | 4 | `public DebtQuote quoteForeignForCash(String type, String money, double cashNeeded, int duration, double rounding)` | ...in any money of the eight (CD8): the cash needed and the face are in that money's units, the quote on its curve (quoteForeign(type, money, ...)). |
| 11803 | 4 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |
| 11809 | 9 | `public String handleForeignForCash(String type, String money, double cashNeeded, int duration, double rounding, boolean holdAsR...` | ...in any money of the eight (CD8). |
| 11820 | 4 | `private static String noLenderIn(String code)` | What the treasury is told when it asks to sell paper in a money the world has no rate for this month (CD8). |
| 11826 | 7 | `public double minimumIssueSizeIn(String money)` | The smallest deal worth arranging in one money's own units (CD8): minimumIssueSizeUsd() over its US$ per unit - the US$ figure itself for the US$. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 11834-13592)

| line | len | member | says |
|---:|---:|---|---|
| 11922 | 1 | `public java.util.List<String> getRolloverShortNotes()` | The rollover's sales that came up short this month, one sentence each; empty when none did. |
| 11928 | 1 | `public java.util.List<String> getRolloverTailNotes()` | The rollover's sales that cleared past the default cap this month, one sentence each; empty when none did. |
| 11938 | 1 | `public java.util.List<TimeSkipReport.AutoSale> getAutoSalesThisMonth()` | The month's automatic sales, in the order they were made. |
| 11941 | 3 | `void noteAutoSale(TimeSkipReport.AutoSale sale)` | Automatic building enters its bond sales here as it makes them. |
| 11946 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 11949 | 1 | `public Auction.Cap getRolloverCap()` | The cap its auctions take (CD2, D28), as the rollover card's chips read it. |
| 11952 | 7 | `public void setRolloverCap(Auction.Cap cap)` | ...and as they set it, applied at the next press; the change is a decision. |
| 11961 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 11964 | 11 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 11986 | 9 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 11997 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, String money, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not and in which money (null at home), and whether it is foreign paper rolled at home. |
| 11999 | 30 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 12031 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 12045 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 12116 | 62 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 12187 | 3 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; an amortizing bond (the serial until CD2) by quoteMediumBond... |
| 12192 | 13 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, String money, double cash)` | ...abroad in a money of the eight (CD8): the share in that money's units at the day's rate (localPer()), the quote on its curve. |
| 12207 | 1 | **type** `private record RolloverSale(Rollover.Issue issue, DebtQuote quote, double brought)` | One of the rollover's issues sold at auction, with the cash it brought. |
| 12210 | 9 | `private static String saleName(Rollover.Issue issue)` | An issue in the words of a sale: "10-year bond", "6-month bill", "10-year amortizing bond". |
| 12237 | 37 | `private void noteRolloverSales(java.util.List<RolloverSale> sales, Rollover.Plan plan)` | WHAT THE ROLLOVER'S SALES DID, said (CD2, D29). |
| 12276 | 4 | `private double localFace(boolean abroad, String money, DebtQuote quote)` | A rollover quote's face in local money: a foreign quote's at the day's rate for its money (localPer(); the D$'s rate to the US$ for a dollar quote). |
| 12287 | 22 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 12312 | 3 | `private void printPopulationInfo()` | printers |
| 12317 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 12330 | 3 | `private void printUtilityInfo()` |  |
| 12334 | 3 | `private void printCityStats()` |  |
| 12341 | 923 | `private void nextMonth()` |  |
| 13265 | 299 | `private void startOfMonthUpdate()` |  |
| 13564 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 13593-13704)

| line | len | member | says |
|---:|---:|---|---|
| 13650 | 4 | `private double issuanceFee()` | The fee in today's money: founding / unit, at the expected price level since 0.7.42 (THE ANCHOR, beside restrikeMoneyConstants()). |
| 13674 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 13692 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |
| 13701 | 3 | `public double minimumIssueSizeUsd()` | ...in US dollars at today's rate (0.7.40): Finances' "the minimum" on the ask abroad, which is asked in dollars. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 13705-14015)

| line | len | member | says |
|---:|---:|---|---|
| 13719 | 16 | `public String getCreditRating()` |  |
| 13737 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 13760 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 13774 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 13792 | 5 | `void strikeTheCurve()` | THE CURVE'S EXPECTATIONS (CD1, spec-city-debt section 3.1): the inflation the rule reads - the year's, or the target before there is one - and the inflation the city expects to settle at, handed to the debt market, wh... |
| 13812 | 51 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 13864 | 94 | `private void finalUpdateEconomy()` |  |
| 13960 | 4 | `private void updateConstructionCost()` |  |
| 13971 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 13982 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 13986 | 4 | `public long getHouseholdCapacity()` |  |
| 13990 | 4 | `public long getStoreCapacity()` |  |
| 13994 | 3 | `public long[] getJobs()` |  |
| 13999 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 14016-14669)

| line | len | member | says |
|---:|---:|---|---|
| 14045 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 14087 | 1 | `public PopulationCohorts getCohorts()` |  |
| 14088 | 1 | `public FamilyModel getFamilies()` |  |
| 14089 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 14090 | 1 | `public Migration getMigration()` |  |
| 14091 | 1 | `public Health getHealth()` |  |
| 14092 | 1 | `public Healthcare getHealthcare()` |  |
| 14116 | 553 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 14670-14682)

| line | len | member | says |
|---:|---:|---|---|
| 14678 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 14681 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 14683-14986)

| line | len | member | says |
|---:|---:|---|---|
| 14699 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 14718 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 14738 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 14780 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 14806 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 14816 | 3 | `public BuildLog getBuildLog()` |  |
| 14823 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 14848 | 1 | `public Unemployment getUnemployment()` |  |
| 14852 | 1 | `public Sickness getSickness()` |  |
| 14856 | 1 | `public CausesOfDeath getCausesOfDeath()` |  |
| 14864 | 4 | `public void holdCausesOfDeathOff()` | Today's deaths, as before R0b: no cause of 1900 kills, the swing stays whole and the influenza takes nobody off work. |
| 14875 | 1 | `public Crime getCrime()` |  |
| 14879 | 1 | `public double getLastOrphanDeaths()` |  |
| 14880 | 1 | `public double getLastUnhousedDeaths()` |  |
| 14882 | 6 | `{ ... }` |  |
| 14890 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 14915 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 14924 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 14945 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 14956 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 14969 | 1 | `public double getStudentLoansLent()` |  |
| 14970 | 1 | `public double getStudentLoansRepaid()` |  |
| 14971 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 14975 | 1 | `public double getStudentLoanInterest()` |  |
| 14978 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 14985 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 14987-15126)

| line | len | member | says |
|---:|---:|---|---|
| 15004 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 15010 | 7 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 15019 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 15031 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 15075 | 1 | `public Equity getEquity()` |  |
| 15079 | 1 | `public Exchange getExchange()` |  |
| 15087 | 1 | `public BondMarket getBondMarket()` |  |
| 15097 | 1 | `public TreasuryFund getFund()` |  |
| 15100 | 1 | `BondMarket.Readings bondReadings()` | The readings the bond market is attached to, for a harness (CurveCheck, CD1). |
| 15116 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### THE ANCHOR (0.7.42): what the city expects prices to do, and the (lines 15127-15153)

| line | len | member | says |
|---:|---:|---|---|
| 15146 | 1 | `public Expectations getExpectations()` | Expected inflation, credibility and the expected price level. |
| 15149 | 1 | `public double getExpectedInflation()` | What the city expects inflation to be, a fraction a year. |
| 15152 | 1 | `public double getCredibility()` | How far the city believes the central bank, Expectations.KMIN to KMAX. |

### what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) (lines 15154-15283)

| line | len | member | says |
|---:|---:|---|---|
| 15157 | 26 | `double[] indexPrices()` | Each component's price this month, in PriceIndex's order: the shelf, the average rent paid, a meal, a luxury piece; the services' price is struck by the index from the fees. |
| 15185 | 8 | `double[] indexSpends()` | ...what the households spent on each: groceries and rent off the sectors' statements, meals and luxury off their own purchases; the services' is the index's sum of the fee lines. |
| 15200 | 14 | `double[] indexFees()` | ...each fee line's price: the clinic's fee for a general visit at the player's scale, a seat's tuition (the adult kinds' fees averaged - a price, so a shift in who studies what is not inflation), the fare a ride is ch... |
| 15216 | 8 | `double[] indexFeeSpends()` | ...and what the households paid on each fee line, off their books. |
| 15253 | 23 | `void restrikeMoneyConstants()` | Every money constant that prices something, struck at the expected price level: founding / unit x Expectations.getStruckLevel(), the level the last month ended on. |
| 15278 | 5 | `private void handOnExpectations()` | ...and what the month hands on from the anchor that is not a constant: the investors' expected inflation (0 before the basket is based) and - since 0.7.43 - the drift every sticky seller's price carries (Retail.sticky... |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 15284-15307)

| line | len | member | says |
|---:|---:|---|---|
| 15294 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 15303 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 15306 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 15308-15727)

| line | len | member | says |
|---:|---:|---|---|
| 15320 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 15322 | 1 | `public PriceIndex getPriceIndex()` |  |
| 15324 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 15341 | 6 | `double[] rateHistoryToSave()` | The ring and how full it is, for the save (XR1, found by AutoBuildCheck's reload twin): a FLOW, twelve months of rates the state a month ended in cannot give back. |
| 15349 | 7 | `private void restoreRateHistory(double[] saved)` | ...and back: an array of any other length (an older save) leaves the ring empty, as it always loaded. |
| 15358 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 15365 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 15376 | 5 | `public Sectors.TradeByGood getTradeByGood()` | The month across the city's edge, good by good (0.7.35): the businesses' struck statements split by good, an import with no good behind it by the sector that bought it, the households' cars among the cars bought and, ... |
| 15392 | 3 | `public double getOwnReserves()` | What is left of the vault once everything owed against it is taken off: the dollar paper outstanding and the foreign money parked in the bank, both claims on the one pot (0.7.35; the Trade tab's own subtraction until ... |
| 15397 | 5 | `public double getSharesHeldAbroad()` | The city's shares in foreign hands, at each company's price - its last trade, or its fair value before one (0.7.35; the Trade tab's own sum until then). |
| 15411 | 3 | `public double getHeldAbroadPrivately()` | The businesses' and the households' paper abroad, at today's rate: what is held abroad outside the vault. |
| 15416 | 3 | `public double getHeldAbroad()` | ...and the vault with it: everything the city holds abroad. |
| 15421 | 4 | `public double getHeldHereByTheWorld()` | What the world holds here: the city's shares at their price, the businesses' bonds at face, the foreign money parked in the bank, the city's own paper it bought at the auction (CD2, slice 5; at face) and the city's do... |
| 15433 | 1 | `public Bank getBank()` |  |
| 15436 | 1 | `public int getBankBranchesStanding()` | Commercial Banks standing now, counted from the buildings: a founded city has one before its first month opens it (bank.getBranches() is 0 until the month runs). |
| 15449 | 1 | `public CentralBank getCentralBank()` |  |
| 15463 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 15469 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 15472 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 15479 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 15483 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 15487 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 15554 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 15558 | 3 | `public EconomyManager getEconomyManager()` |  |
| 15561 | 3 | `public PopulationManager getPopulationManager()` |  |
| 15565 | 1 | `public LabourMarket getLabourMarket()` |  |
| 15566 | 1 | `public WantedPosts getWantedPosts()` |  |
| 15567 | 1 | `public Education getEducation()` |  |
| 15583 | 21 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 15618 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 15660 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 15678 | 47 | `public void recordMonth()` | Files this month in the graph history. |
| 15726 | 1 | `public Inbox getInbox()` |  |
| 15727 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 15728-15780)

| line | len | member | says |
|---:|---:|---|---|
| 15739 | 3 | `public int getMonthsUntilAutosave()` |  |
| 15760 | 20 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |

### THE AUTOSAVE OFF THE CLOCK (UP1, 2026-10-22) (lines 15781-16257)

| line | len | member | says |
|---:|---:|---|---|
| 15821 | 3 | `void useSaveClockForChecks(java.util.function.LongSupplier nanos)` |  |
| 15830 | 9 | `private void reportSaves()` | Takes what the writer has finished onto the calling thread: each result becomes the last save result, and a failed one is said as an autosave has always said it. |
| 15845 | 4 | `public void awaitSaves()` | Waits for the save writer, then reports what it wrote. |
| 15855 | 4 | `public void save(int slot, String slotName)` | Saves to a slot, now, on the calling thread: the live objects, after any background write has finished. |
| 15865 | 3 | `SaveWriter.Job prepareSaveForChecks(int slot, String slotName)` | For SaveSlotCheck (UP1): the save filled as an autosave fills it, the snapshot taken and nothing written, so a check can read what the writer thread would be handed. |
| 15870 | 3 | `DataSave liveSaveForChecks()` | For SaveSlotCheck (UP1): the persistent save the setters fill, to measure what a snapshot shares with it. |
| 15881 | 11 | `private SaveWriter.Job prepareSave(int slot, String slotName, boolean snapshot)` | Fills the save from the city as it stands, asks the map for its bytes and stamps it; everything save() did before it wrote anything. |
| 15899 | 358 | `private byte[] fillSave(String slotName)` | Fills the save from the city as it stands, asks the map for its bytes and stamps it (TR1: split out of prepareSave(), so the recording fills the same save and snapshots it without the history). |

### THE RECORDING (TR1, 2026-10-31) (lines 16258-16581)

| line | len | member | says |
|---:|---:|---|---|
| 16300 | 1 | `public boolean isRecording()` |  |
| 16302 | 1 | `public String getRecordingId()` |  |
| 16305 | 3 | `public Path recordingFolder()` | The recording's folder, or null in a city with no id. |
| 16310 | 1 | `public int getMonthsNotRecorded()` | Months the clock left out of the recording because the writer was behind (RECORD_BACKLOG). |
| 16313 | 1 | `long lastRecordNanos()` | For ReplayCheck: the FX-thread cost of the last recorded month, in nanoseconds. |
| 16319 | 20 | `public void setRecording(boolean on)` | Switches "Record this city" on or off. |
| 16341 | 4 | `private Recording.Meta recordingMeta()` | The meta of the recording being written: who the city is, and what wrote it. |
| 16347 | 11 | `private void submitRecording(SaveWriter.Task task)` | Hands the writer a recording file and counts it until it is written (a failure is printed there). |
| 16360 | 16 | `private boolean awaitRecordingRoom()` | Waits, at most RECORD_WAIT_MS, for the writer to get inside RECORD_BACKLOG; true when it has. |
| 16384 | 26 | `private void recordForReplay()` | Keeps the month that has just ended, when "Record this city" is on (called by nextMonth(), after the autosave: the counter and every month-end record agree). |
| 16412 | 4 | `private void recordHistoryFrom(SaveWriter.Job job)` | The autosave's history, handed to the recording from the snapshot the save made (autosave()); only once a month of this timeline is on disk. |
| 16418 | 4 | `public void markReplayCopy()` | Makes this game the replay's throwaway copy: recording off, and never on again. |
| 16423 | 1 | `public boolean isReplayCopy()` |  |
| 16430 | 10 | `public boolean replayMap(byte[] sidecar, int month)` | Replay's quick step: the map of a recorded month read from its sidecar and put in place of the city's, nothing else moved. |
| 16442 | 6 | `public void replayHistory(HistorySave h)` | Replay's history: a parsed history put in place of the copy's, as loadHistory() does with the file's, and the partner trade's years with it. |
| 16457 | 5 | `public String takeSkipFailure()` |  |
| 16471 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 16473 | 1 | `public GameFiles getGameFiles()` |  |
| 16492 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 16520 | 18 | `public void sendBuildingSave()` | EVERY ID TO THE HIGHEST, NOT AS MANY AS THERE ARE (FR3, 2026-10-19). |
| 16541 | 17 | `public void loadBuildings()` |  |
| 16572 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 16582-16758)

| line | len | member | says |
|---:|---:|---|---|
| 16609 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 16610 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 16611 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 16618 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 16629 | 8 | `public double localPer(String money)` | Local money per unit of one of the eight (CD8): the D$'s rate to the US$ for the US$ - the vault's, the figure every dollar path has always read - and that times the money's US$ per unit (the vault's cross rate, the d... |
| 16639 | 9 | `public void repayForeignPrincipal(double own, String money)` | A slice of principal repaid, in the money it is written in (CD8: the US$ goes through the one-argument form, as it always did). |
| 16665 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 16672 | 9 | `public void payForeignInterest(double own, String money)` | A coupon, likewise, in the money it is written in (CD8: the US$ goes through the one-argument form, as it always did). |
| 16743 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 16752 | 1 | `public double getCityPaperSettled()` |  |
| 16753 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 16754 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 16756 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 16759-16884)

| line | len | member | says |
|---:|---:|---|---|
| 16816 | 1 | `public double getCouponsToHouseholds()` |  |
| 16817 | 1 | `public double getPrincipalToHouseholds()` |  |
| 16818 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 16819 | 1 | `public double getCouponsToFirms()` |  |
| 16820 | 1 | `public double getPrincipalToFirms()` |  |
| 16821 | 1 | `public double getFirmsBoughtPaper()` |  |
| 16822 | 1 | `public double getCouponsToWorld()` |  |
| 16823 | 1 | `public double getPrincipalToWorld()` |  |
| 16824 | 1 | `public double getWorldBoughtPaper()` |  |
| 16825 | 1 | `public double getPaperBoughtFromWorld()` |  |
| 16836 | 7 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households', the central bank's and (CD2, slice 4) the businesses' and (slice 5) the world's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 16845 | 16 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 16863 | 21 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 16885-16989)

| line | len | member | says |
|---:|---:|---|---|
| 16895 | 18 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |
| 16922 | 18 | `private void desksBuysFirmPaper(double face, double cash)` | THE BANK BUYS THE BUSINESSES' PAPER (CD2, slice 4), for the spread gone, a sector that borrows, or one that wants the money: this much face for this much cash, off every piece's businesses' share pro rata, onto the ba... |
| 16954 | 22 | `private double desksBuysWorldPaper(double face)` | THE BANK BUYS THE WORLD'S PAPER (CD2, slice 5), when the hot money leaves and takes it with it (worldPaperLeavesWithTheMoney()): this much face, off every piece's world share pro rata, onto the bank's book with the un... |
| 16985 | 4 | `private void worldPaperLeavesWithTheMoney(double stockBefore)` | ...AND THE PAPER LEAVES WITH THE MONEY (CD2, slice 5, D7). |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 16990-17166)

| line | len | member | says |
|---:|---:|---|---|
| 17012 | 38 | `private double householdsTakeTheirShare()` |  |
| 17058 | 8 | `private void restrikeBankDiscount(double unearnedBefore, double faceBefore, double bankPaid)` | BK1: books the bank's gain or loss from a line's pooled discount re-blending (Bank.restrikeDiscount()): what its paper gained in face since the snapshot, less what it paid for it, less what its unearned discount rose ... |
| 17078 | 8 | `private void restrikeBankDiscountCarried(double carriedBefore, double bookBefore)` | BD1: books the bank's gain or loss from a line's discount moving the book's figure outside the settle (Bank.restrikeCarriedDiscount()): a reopening at a premium, or the desk handing the bank the whole line's discount ... |
| 17103 | 34 | `private double firmsTakeTheirShare(java.util.List<Debt> settling)` | THE BUSINESSES TAKE THEIR SHARE (CD2, slice 4, D33), at the same settle and from the same allotment (Debt.allotFirmsCash(), the auction's fill for them at the clearing yield). |
| 17154 | 12 | `private double worldTakesItsShare(java.util.List<Debt> settling)` | THE WORLD TAKES ITS SHARE (CD2, slice 5, D7; star 2), at the same settle and from the same allotment (Debt.allotWorldCash(), the auction's fill for the world at the clearing yield - CapitalFlows' rule, the stock it wo... |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 17167-17430)

| line | len | member | says |
|---:|---:|---|---|
| 17239 | 67 | `private void openMarketOperation()` |  |
| 17316 | 29 | `private double buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |
| 17358 | 29 | `private double buyPaperFromFirms(double wanted)` | The rest of a purchase the bank and the households could not fill, from the businesses' term paper (CD2, slice 4): the households' path with the businesses' share in its place - pro rata across the pieces they hold th... |
| 17401 | 29 | `private double buyPaperFromWorld(double wanted)` | The rest of a purchase the others could not fill, from the world (CD2, slice 5): the businesses' path with the world's share in its place - pro rata across the pieces it holds that are settled and pay no principal thi... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 17431-17691)

| line | len | member | says |
|---:|---:|---|---|
| 17536 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 17539 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 17546 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 17558 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 17565 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 17571 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 17584 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 17615 | 76 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 17692-17716)

| line | len | member | says |
|---:|---:|---|---|
| 17710 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 17714 | 1 | `public double getBuybackToHouseholds()` |  |
| 17715 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 17717-17925)

| line | len | member | says |
|---:|---:|---|---|
| 17812 | 18 | `private void takeTreasuryMonth()` |  |
| 17832 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 17834 | 1 | `public double getTreasuryOpening()` |  |
| 17835 | 1 | `public double getTreasuryClosing()` |  |
| 17836 | 1 | `public double getTreasuryRaised()` |  |
| 17837 | 1 | `public double getTreasuryRepaid()` |  |
| 17838 | 1 | `public double getTreasurySurplus()` |  |
| 17841 | 1 | `public double getTreasuryNotCash()` | What the balance charged or booked that the cash did not (CD3, CD7): the discount amortised, less the gain on paper retired and the exchange gain on the foreign debt. |
| 17852 | 1 | `public double getTreasuryCashSurplus()` | The part of last month's surplus that moved cash (SG1): the balance and the bridge's not-cash row, which is the bridge's own identity (surplus + not cash + raised - repaid + the rest = the cash's change). |
| 17855 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 17865 | 3 | `public double getNetPosition()` | The city's net position (0.7.32, the Finances tab's THE BALANCE): its cash - below nothing when it is overdrawn - less the paper it owes and what its central bank has advanced it. |
| 17875 | 3 | `public double getNetPositionAtCarrying()` | The same net position with the paper at what the books carry it at (CD3, spec-city-debt.md 5.4): its face less the discount not yet worn off, plus any premium. |
| 17897 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 17909 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 17914 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 17922 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |

### FROM EARNED TO THE BUDGET (0.7.31) (lines 17926-18071)

| line | len | member | says |
|---:|---:|---|---|
| 17964 | 34 | `public java.util.List<TreasuryJournal.Entry> getEarnedToBudget()` | The steps from EARNED to the budget's balance, in the Government bridge's order, signed as they move EARNED (+ adds, - takes away); every line, at nothing too. |
| 18000 | 5 | `public double getEarnedResidual()` | What the steps leave between EARNED and the budget: the dials moved since the month was struck, and nothing in a month nobody moved one. |
| 18006 | 7 | `double[] treasuryMonthToSave()` |  |
| 18014 | 10 | `void restoreTreasuryMonth(double[] saved)` |  |
| 18049 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 18052 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 18054 | 1 | `public double getPostAuditDriftWorst()` |  |
| 18055 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 18056 | 4 | `public void InterestExpense(double amount)` |  |
| 18061 | 3 | `public DebtManager getDebtManager()` |  |
| 18066 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 18072-18359)

| line | len | member | says |
|---:|---:|---|---|
| 18140 | 46 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 18198 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 18203 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 18225 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 18237 | 19 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 18265 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 18290 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 18297 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 18301 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 18311 | 8 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 18321 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 18324 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 18331 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 18341 | 10 | `public double getArrearsOwedTo(String sectorKey)` | Owed and unpaid to one sector's till (0.7.55): every line payDownArrears() would pay it, by the payee it would pay. |
| 18353 | 1 | `public double getArrearsPaidTo(String sectorKey)` | What the treasury paid this sector's till of its arrears this month (0.7.55) - its statement's arrears line. |
| 18355 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 18356 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 18357 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 18358 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 18360-18632)

| line | len | member | says |
|---:|---:|---|---|
| 18377 | 6 | `public double fairValueOf(Debt debt)` | What one piece is worth to somebody who does not care to hold it, right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on. |
| 18389 | 5 | `public BookDesk bookDesk()` | THE DEBT DESK (CD6): the book page's figures, a line a piece (BookDesk). |
| 18396 | 5 | `public BookDesk.Line bookLine(Debt debt)` | ...and one line of it, for a line's page (CD6): the market struck once, as bookDesk() strikes it, and the line read (BookDesk.line()). |
| 18412 | 3 | `public double quoteRepurchase(Debt debt)` | What clearing the whole of one piece that can be cleared would cost right now (CD4): the reverse auction's price for everything its holders offer, the central bank's face excepted (D18), at any price - the sum of the ... |
| 18424 | 3 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative) from clearing what quoteRepurchase() clears: the carrying value of that face less the price (CD3) - what the books hold it at against what buying it bac... |
| 18429 | 5 | `public double buybackOfferable(Debt debt)` | The face of a piece its holders can offer: the principal less the central bank's, which does not sell (D18); a dollar piece's all of it, the world's alone (D26). |
| 18458 | 23 | `public Buyback.Result quoteBuyback(Debt debt, double face, double maxPer100)` | THE REVERSE AUCTION, QUOTED (CD4, spec-city-debt.md section 6.2): what buying back this much face of one piece, at no more than this per 100, would take and cost, struck once on the market as it stands - nothing is mo... |
| 18489 | 7 | `public Buyback.HonestLine honestLine(Debt debt, Buyback.Result q)` | THE HONEST LINE (CD6, spec-city-debt.md section 6.3), struck from a quote: a buyback's gain is real on the statement, but the cash to pay for it costs today's rate. |
| 18498 | 3 | `private static boolean takesTheWholePiece(double taken, double principal)` | True when this much face is the whole of a piece's principal: all of it but the dust that slices of a holding leave. |
| 18527 | 95 | `public Buyback.Result buyBack(Debt debt, double face, double maxPer100)` | Buys one bond back, in whole or in part, and takes what it bought off the books: the reverse auction of quoteBuyback(), struck and paid. |
| 18629 | 3 | `public double repurchaseDebt(Debt debt)` | Buys back everything the holders offer of one bond, at any price: the thin form of buyBack() the buttons before CD4 knew. |

### the call (CD4, spec-city-debt.md section 6.4; D22, D23, D24) (lines 18633-18774)

| line | len | member | says |
|---:|---:|---|---|
| 18646 | 3 | `public double rateVolatility()` | The volatility of the city's own short rate, a year (D23): the standard deviation of the monthly changes of History's "interestRate" series over the last CallTree.VOL_MONTHS, times the square root of twelve, never und... |
| 18657 | 7 | `public boolean canCall(Debt debt)` | True when this piece can be called now: a callable bond of the city's own, on the books, whose first call month has come (LongTermBond .isCallableNow()) and which has nothing unsettled - a line sold last month has buy... |
| 18666 | 3 | `public double callCost(Debt debt)` | What calling this piece costs: its outstanding principal, 100 per 100 (CallTree.CALL_PRICE), paid to every holder, the central bank's too. |
| 18676 | 3 | `public double callGain(Debt debt)` | What calling the line books as a gain (a loss below nothing) against its carrying value (CD6): what it is carried at less callCost() - the figure callLine() adds to the month's gain on debt retired (NationalAccounts.g... |
| 18687 | 5 | `public double callSaving(Debt debt)` | The present value a call would save (D24): the line's remaining payments at today's curve, as a bond that cannot be called, less what the call pays and less what selling the paper again costs - the auction's fee (issu... |
| 18699 | 4 | `public boolean worthCalling(Debt debt)` | True when calling this callable line is worth it (D24): the saving (callSaving()) passes CallTree.WORTH_CALLING of the face, 3%. |
| 18726 | 40 | `public double callLine(Debt debt)` | Calls one line whole at 100 (CD4): every holder is paid its face - the bank, the households, the businesses, the world and the central bank, which has no say in it (a call is the paper's own term, not a sale a holder ... |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 18775-20940)

| line | len | member | says |
|---:|---:|---|---|
| 18843 | 20 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 18864 | 330 | `private void rebuildSimulationState()` |  |
| 19258 | 26 | `private static String refusal(int slot, DataSave loaded)` | Why a save would not load, or null when it would: the three refusals below, each logged, its file untouched. |
| 19294 | 21 | `private String refusalBeforeRebuild(int slot)` | Why loadGameSave() must not rebuild the world for this slot, or null when it may (FW1, 0.8.37). |
| 19317 | 10 | `public void loadGame(int slot)` | Load game |
| 19335 | 22 | `private void readTheMap(int slot, Long stamp)` | THE CITY MAP, READ BACK (0.7.60): from the slot's sidecar when the save's stamp is its stamp and it was drawn on this land in this month (CityMap.readSidecar()); a save that had a map whose sidecar is missing or stale... |
| 19394 | 11 | `private void restoreOilPools(DataSave loaded)` | THE TWO OIL POOLS ON A LOAD (0.7.93, batch O10b; LandManager's THE TWO OIL POOLS): once the land is in place, the offshore pool's E as saved - or, on a save from before SAVE_FORMAT 35, the one pool's E charged to the ... |
| 19406 | 63 | `private boolean restoreLandOnTheWorld(DataSave loaded, double owned)` |  |
| 19471 | 1409 | `private void readTheSave(int slot)` | The load itself; see loadGame(). |
| 20887 | 4 | `void seedFundLedger()` | THE FUND'S COST BASIS FOR A SAVE FROM BEFORE IT (0.7.39; the project's spec-fund-0739.md, 3.5): each market lot and bond at its market value this month, flagged as such, the rescue lot exact from the counters, one TRA... |
| 20896 | 44 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 20941-21234)

| line | len | member | says |
|---:|---:|---|---|
| 20952 | 17 | `private void seedMoneyConstants(double unit)` | Seeds every money constant struck in founding dollars at a unit: the load path's list since the reform, and since FX4 also buildWorld()'s at 1, so a new city does not keep the last loaded city's unit. |
| 20970 | 1 | `public Denomination getDenomination()` |  |
| 20973 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 21004 | 207 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 21222 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

