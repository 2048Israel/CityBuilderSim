# Game.java - 15,173 lines · 598 methods · 17 constants · model

`ham/citybuildersim/Game.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Equity](Equity.md) (65), [DecisionLog](DecisionLog.md) (64), [BuildingsTemplate](BuildingsTemplate.md) (59), [TreasuryLine](TreasuryLine.md) (56), [DebtQuote](DebtQuote.md) (40), [GameLog](GameLog.md) (36), [AgeBand](AgeBand.md) (36), [Debt](Debt.md) (34), [BusinessDebtManager](BusinessDebtManager.md) (33), [Rollover](Rollover.md) (27), [GameFiles](GameFiles.md) (26), [LandManager](LandManager.md) (25), [TreasuryFund](TreasuryFund.md) (24), [ConstructionControl](ConstructionControl.md) (24), [PriceIndex](PriceIndex.md) (23), [CareType](CareType.md) (22), [TaxPolicy](TaxPolicy.md) (21), [JobType](JobType.md) (20), [Bank](Bank.md) (20), [DebtManager](DebtManager.md) (19), [Founding](Founding.md) (19), [Sector](Sector.md) (19), [LandParcel](LandParcel.md) (17), [TreasuryJournal](TreasuryJournal.md) (17), [FamilyModel](FamilyModel.md) (16), [Resource](Resource.md) (16), [BusinessInvestment](BusinessInvestment.md) (15), [Investor](Investor.md) (15), [HouseholdAccounts](HouseholdAccounts.md) (14), [BuildingType](BuildingType.md) (14)... and 80 more

**Used by (133):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildAdvice](BuildAdvice.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BuildCard](BuildCard.md), [BuildCardCheck](BuildCardCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CityBasket](CityBasket.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [ConversionCheck](ConversionCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [FundLedger](FundLedger.md), [FundLedgerCheck](FundLedgerCheck.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [GdpCheck](GdpCheck.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [Oil](Oil.md), [OilCheck](OilCheck.md), [OrderSearchCheck](OrderSearchCheck.md), [OutsideCheck](OutsideCheck.md), [Pieces](Pieces.md), [PolicyCheck](PolicyCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorFlow](SectorFlow.md), [SectorFlowCheck](SectorFlowCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [WaterCheck](WaterCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 840 | THE FOUNDING RESERVE (2026-09-21) |
| 896 | · THE FOUNDING RECORD (0.7.10) |
| 933 | THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) |
| 967 | · THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) |
| 1130 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 1153 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1376 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1514 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 1672 | · THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) |
| 1770 | PRIVATE INVESTMENT |
| 2123 | · AND THE BALANCE SHEET |
| 2331 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 2417 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 2451 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 2986 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 3133 | · the preferred offer |
| 3255 | · what the fund is worth |
| 3315 | · the dial |
| 3438 | · the hand |
| 3555 | · an Insane founding |
| 3668 | SHRINKING |
| 3867 | THE CONSTRUCTION WARNING |
| 3917 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 4214 | THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) |
| 4416 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 4595 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 4673 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 5373 | THE BUILDERS' PRICE (0.7.19) |
| 5668 | MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) |
| 5762 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 6164 | · in the month |
| 6388 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 6490 | · THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) |
| 6701 | · A RUN OF ORDERS (0.7.40) |
| 7231 | · A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) |
| 7315 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 7650 | · THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 7725 | ROLLING WHAT FALLS DUE (0.7.13) |
| 8144 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 8404 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 8620 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 8813 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 9196 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 9334 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 9602 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 10207 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 10220 | THE READINGS THE MONTH TAKES OF THE CITY |
| 10509 | · the price of a place (2026-09-21) |
| 10644 | THE ANCHOR (0.7.42): what the city expects prices to do, and the |
| 10683 | · what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) |
| 10814 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 10838 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 11190 | the save system |
| 11638 | PAYING THE WORLD BACK |
| 11779 | THE HOLDERS ARE PAID (0.7.1) |
| 11855 | · the desk, for the households |
| 11881 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 11938 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 12085 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 12321 | · a buyback's holders outside the pools |
| 12343 | WHAT THE TREASURY ACTUALLY DID |
| 12498 | · FROM EARNED TO THE BUDGET (0.7.31) |
| 12620 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 12903 | BUYING YOUR OWN DEBT BACK |
| 13030 | WHY LAND IS NOT IN THE RENT FLOOR |
| 13408 | · · the three the monthly path sets and this did not |
| 14921 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 1708 | `Game.LandNeed.Kind.ROOM` |  |
| 1708 | `Game.LandNeed.Kind.SHORTFALL` |  |
| 1708 | `Game.LandNeed.Kind.DEPOSIT` |  |
| 1708 | `Game.LandNeed.Kind.COAST` |  |
| 6386 | `Game.BuildResult.SUCCESS` |  |
| 6386 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 6386 | `Game.BuildResult.NO_LAND` |  |
| 6386 | `Game.BuildResult.NO_DEPOSIT` |  |
| 6386 | `Game.BuildResult.NO_LICENCE` |  |
| 6386 | `Game.BuildResult.FAILED` |  |
| 6386 | `Game.BuildResult.NO_COAST` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 580 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 888 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 891 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 894 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 4278 | `Game.COUNTDOWN_SLICES` | `16` | The countdown's own slices consider() asks before it searches: an order trimmed by fewer is decided slice by slice, as before 0.7.54. |
| 4281 | `Game.PRIME_SCAN_SLICES` | `4096` | The largest order whose refusal asks every slice whether it would carry its interest at prime, as the countdown did; a larger one asks the four that decide it. |
| 6400 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 7373 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 7546 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 7668 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 9212 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 9224 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 9227 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 9242 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 9251 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, WHAT AN ISSUE COSTS). |
| 9259 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 11197 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 77 | `private final GameFiles gameFiles` | Where saves live and how they are written. |
| 78 | `private HistoryGrapher historyGrapher` |  |
| 79 | `private DebtManager debtManager` |  |
| 81 | `private SimulationEngine simulationEngine` |  |
| 83 | `int materialsConsumed` |  |
| 87 | `double receiptMaterials` | The last order's material, in units - drawn by the crews as they build. |
| 88 | `double totalBuildingCost` |  |
| 89 | `private boolean hasNewReceipt` |  |
| 90 | `String lastBuildingName` |  |
| 99 | `int lastBuildQuantity` | How MANY of them, so the receipt can say "3 x Walk-in Clinic". |
| 113 | `private int receiptSerial` | WHICH receipt this is, counted up forever. |
| 139 | `boolean reports` | Console output per month: seven sector income statements and three 12x60 ASCII graphs. |
| 140 | `boolean graphs` |  |
| 143 | `boolean initialized` | boolean |
| 810 | `private String loadFailure` | Why the last load did not happen, or null. |
| 911 | `private Founding founding` |  |
| 953 | `private CityMap cityMap` | The city map, or null until it is asked for. |
| 956 | `private int mapFailures` | How many months the map failed to keep up and was dropped (each one logged): what a harness asserts is none. |
| 988 | `private int mapGeneration` | Bumped whenever the city's map is thrown away (a new city, a load): a draft from before it is another city's. |
| 992 | `private final Game game` |  |
| 993 | `private final int generation` |  |
| 994 | `private final CityLand land` |  |
| 995 | `private final java.util.Map<Resource, double[]> remaining` |  |
| 996 | `private final BuildingVisual.Type[] types` |  |
| 997 | `private final long[] counts` |  |
| 998 | `private volatile CityMap map` |  |
| 1117 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 1188 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 1189 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1414 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1659 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under ON OFFER (under the toggle until 0.7.26), and a short vault says here that the rest was converted. |
| 1660 | `private int lastLandReceiptMonth` |  |
| 1758 | `private BusinessInvestment.OrderWatch orderWatch` | Harnesses only (0.7.54): what every order search decides is told here as well - see BusinessInvestment.OrderWatch. |
| 2349 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 2358 | `private double carriedTransitBill` | ...and the month's transit bill as 6d struck it (0.7.49), applied inside the rebuild for the same reason: the rebuild would strike it at the fill the month ended on, not the one it was paid at. |
| 2369 | `private double carriedCaptiveShare` | ...and the commute as 6d struck it (0.7.49): the share of the working cells' workers with no car of their own, because the cells are not back when the road ratio is first struck (carriedCarOwnership's reason), and a j... |
| 2372 | `private double[] carriedFuelMonth` | ...and the drivers' fuel as 6d drew it (0.7.62): bill, imported part, litres - DataSave.householdFuel; null for a save from before it. |
| 2375 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 2394 | `private final double[] rowFeesSettled` | Each row's fees as the last month's waterfall was handed them: the clinic's, the schools', the fares and the fuel (0.7.49). |
| 2422 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 2699 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 3025 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 3788 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 3875 | `private int constructionShedMonth` |  |
| 3876 | `private double constructionShedPoints` |  |
| 3935 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 3948 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 3968 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 3983 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 4336 | `final BusinessInvestment.Decision decision` |  |
| 4337 | `final double cash, perUnitProfit` |  |
| 4338 | `final BusinessDebtManager credit` |  |
| 4339 | `final boolean banned` |  |
| 4340 | `boolean atPrime` |  |
| 4341 | `double firstRate` |  |
| 4342 | `int deskCalls` |  |
| 4554 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 4555 | `private final java.util.Set<String> heldForDownPayment` |  |
| 4634 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 4635 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 4636 | `private double salvageUsedThisMonth` |  |
| 4693 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 4831 | `private double bankedClearedAtLoad` | Construction points the last load cleared from stacks that carried them past what they owed (0.7.17; see the load path and BuildingManager.clearBankedProgress()). |
| 5126 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 5131 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 5305 | `public final int quantity` |  |
| 5306 | `public final double sticker` |  |
| 5307 | `public final double materialsNeeded` |  |
| 5309 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 5311 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 5312 | `public final double plantPrice` |  |
| 5313 | `public final double plantCost` |  |
| 5314 | `public final double materialsImported` |  |
| 5315 | `public final double materialsPrice` |  |
| 5316 | `public final double importCost` |  |
| 5322 | `public final double salesTax` | The sales tax in the price (0.7.19): what the builders remit on it, less what they claim back on the plant's material - passed on, as a shop's shelf price passes it on. |
| 5329 | `public final double allowance` | What the price allows for the material beyond the yard (0.7.19): the plant's and the world's at today's prices, net of the credit on the plant's and with the builders' tax on it - the part of the total an escalation c... |
| 5330 | `public final double total` |  |
| 5331 | `public final double landNeeded` |  |
| 5332 | `public final double landFree` |  |
| 5338 | `public final double months` | Months to finish at this month's shares of the site output, the planners' own reading (quoteMonths()), or NaN when the builders have no site output. |
| 5436 | `private boolean contractsToInfer` | Set by the load path when a save carries no payers, and read once the land price is back. |
| 5451 | `private final BuildingManager.BuildersWages buildersWages` | What the builders' crews cost a point, today and at founding. |
| 5666 | `private double siteDrawBillable` | What the month's site draw cost the builders, as they bill it on (billableMaterial()): what the owners' escalation is struck against. |
| 5921 | `private String lastHandRefusal` | Why a demolition or a buy-out was not made, or null when it was. |
| 6167 | `private ConstructionControl.Events controlThisMonth` | This month's events, kept for the screens and the inbox: what the advance left, settled. |
| 6169 | `private double overtimePaidThisMonth` | What the treasury paid the builders this month for overtime, with their tax in it. |
| 6171 | `private double demolitionSalvageThisMonth` | What a finished demolition's material fetched this month, from the builders. |
| 6512 | `private double freshRights` | Units a month of fresh water the city may treat past what its lakes and river yield: an older city's (0.7.59). |
| 6626 | `private final Investor government` | The city itself, as a payer. |
| 7800 | `private final Rollover rollover` |  |
| 9591 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 9592 | `private LandManager landManager` |  |
| 9596 | `private boolean loadingSave` | True while loadGameSave() runs initialize(), which then founds no land: the save brings its own (0.7.57). |
| 9599 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 9600 | `private BuildLog buildLog` |  |
| 9627 | `private PopulationCohorts cohorts` |  |
| 9628 | `private FamilyModel families` |  |
| 9636 | `private Migration migration` |  |
| 9642 | `private LabourMarket labourMarket` | What labour costs. |
| 9648 | `private Education education` | The schools. |
| 9657 | `private Health health` | How much of the workforce is off sick. |
| 9665 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 10212 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 10358 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 10365 | `private HouseholdAccounts households` | The residents' own books. |
| 10374 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 10384 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 10388 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 10396 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 10400 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 10490 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 10496 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 10571 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 10581 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 10587 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 10594 | `private final Equity equity` | Who owns the city's companies. |
| 10598 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 10606 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 10616 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 10620 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 10642 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 10660 | `private final Expectations expectations` |  |
| 10820 | `private final Consumption consumption` |  |
| 10821 | `private boolean consumptionLoaded` |  |
| 10830 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 10848 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 10857 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 10858 | `private int rateHistoryFilled` |  |
| 10932 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 10945 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 10989 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 11002 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 11016 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 11034 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 11037 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 11039 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 11040 | `private double carriedRetailCapacity` |  |
| 11041 | `private double carriedRetailWant` |  |
| 11044 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 11045 | `private java.util.Map<String, String> lastInvestment` |  |
| 11048 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 11092 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 11199 | `private int monthsSinceAutosave` |  |
| 11527 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 11536 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 11661 | `private double foreignDebtRaisedThisMonth` |  |
| 11662 | `private double foreignPrincipalRepaidThisMonth` |  |
| 11663 | `private double foreignInterestPaidThisMonth` |  |
| 11715 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 11735 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 11736 | `private double cityPrincipalRepaidThisMonth` |  |
| 11750 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 11751 | `private double cityDiscountForBank` |  |
| 11754 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 11771 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 11777 | `private double bankPrincipalRepaidThisMonth` |  |
| 11805 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 11896 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 12184 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 12187 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 12331 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 12333 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 12385 | `private double treasuryOpening` |  |
| 12386 | `private double treasuryClosing` |  |
| 12387 | `private double treasuryRaised` |  |
| 12388 | `private double treasuryRepaid` |  |
| 12389 | `private double treasurySurplus` |  |
| 12390 | `private boolean treasuryRecorded` |  |
| 12409 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 12412 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 12581 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 12584 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 12592 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 12593 | `private String postAuditDriftPool` |  |
| 12594 | `private double postAuditDriftWorst` |  |
| 12658 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 12661 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 12664 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 12672 | `private final java.util.Map<String, Double> arrearsPaidTo` | This month's arrears paid down, by the sector whose till they reached (0.7.55): what its statement's arrears line reads (SectorBooks, arrearsPaid), struck at the settle and read when the month is recorded, both inside... |
| 13028 | `private long pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 14876 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 14925 | `private final Denomination denomination` |  |
| 15166 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 15149 | **type** `public class Game` |  |
| 69 | 1 | `public DecisionLog getDecisions()` | The player's decisions, oldest first (0.7.23): what the chart's flags are drawn from. |
| 145 | 3 | `public Game()` |  |
| 157 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 166 | 5 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 173 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 205 | 328 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 534 | 4 | `public void run()` |  |
| 539 | 40 | `private void initialize()` |  |
| 582 | 4 | `static { ... }` |  |
| 590 | 3 | `public void newGame()` | A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. |
| 599 | 40 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 669 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 699 | 8 | `public void resumeGame()` |  |
| 707 | 61 | `public void loadGameSave(int slot)` |  |
| 777 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 785 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 800 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 802 | 6 | `public void toggleQuit()` |  |
| 812 | 1 | `public String getLoadFailure()` |  |
| 814 | 7 | `public void toggleGraphs()` |  |
| 821 | 5 | `public void toggleReports()` |  |
| 827 | 3 | `public void toggleNextMonth()` |  |
| 832 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 834 | 3 | `public int getMonth()` |  |
| 837 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 840-895)

### THE FOUNDING RECORD (0.7.10) (lines 896-932)

| line | len | member | says |
|---:|---:|---|---|
| 914 | 4 | `public Founding getFounding()` | How this city was founded. |
| 920 | 1 | `public long getWorldSeed()` | The seed of the world this city stands on (0.7.56): chosen at founding, or derived from an older save. |
| 928 | 1 | `public World getWorld()` | The world this city stands on (0.7.56): its terrain, its founding site and river, its fields of ore and oil. |
| 931 | 1 | `public CityLand getCityLand()` | The city's land on the world (0.7.57): its centre and every purchase since, whole blocks of a grid since 0.7.67 (CityLand). |

### THE CITY MAP (0.7.60, batch J3; the project's spec-land.md 2.5) (lines 933-966)

| line | len | member | says |
|---:|---:|---|---|
| 959 | 4 | `public CityMap getCityMap()` | The city map: drawn canonically the first time it is asked for, then kept up month by month. |
| 965 | 1 | `public boolean hasCityMap()` | Whether the city map has been drawn: a city never asked for it has none, and its months pay nothing for it. |

### THE FIRST DRAW, AWAY FROM THE SCREEN (0.7.61, batch J4) (lines 967-1129)

| line | len | member | says |
|---:|---:|---|---|
| 991 | 32 | **type** `public static final class MapDraft` | A map to be drawn away from the screen's thread: the city's land, remains and counts as they stood, copied. |
| 1000 | 8 | `private MapDraft(Game game)` _(in Game.MapDraft)_ |  |
| 1010 | 3 | `public void draw()` _(in Game.MapDraft)_ | Draws the map on the copy: any thread, once. |
| 1015 | 1 | `public boolean drawn()` _(in Game.MapDraft)_ | Whether it has been drawn. |
| 1018 | 1 | `CityMap map()` _(in Game.MapDraft)_ | The map drawn, or null: a harness's (MapCheck 7). |
| 1021 | 1 | `public Game game()` _(in Game.MapDraft)_ | The game it was taken from. |
| 1025 | 3 | `public MapDraft mapDraft()` | A draft of the city's map, for drawing away from the screen's thread; null when the map is drawn already. |
| 1036 | 20 | `public boolean adoptMap(MapDraft draft)` | Keeps a drawn draft as the city's map, bound to its own land and kept up to the month (reconcile()), and says whether it is the city's map now. |
| 1058 | 1 | `public int getMapFailures()` | How many months the map failed to keep up and was dropped. |
| 1061 | 1 | `public BuildingVisual.Type[] getMapTypes()` | The building types as the map draws them, by id. |
| 1064 | 7 | `public long[] getMapCounts()` | Every type's standing count, by id: what the map's districts sum to. |
| 1073 | 3 | `private CityMap drawMap()` | The map drawn canonically from the city as it stands. |
| 1084 | 10 | `private void reconcileMap()` | The month's change placed on the map (CityMap.reconcile()), after the month's construction and demolitions; the land drawn again (a restatement) draws the map again. |
| 1096 | 1 | `public String getCityName()` | The city's name. |
| 1099 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 1102 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 1105 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 1108 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 1126 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset until 0.7.20, when the screen stopped saying what the money buys; NewGameChec... |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 1130-1152)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 1153-1375)

| line | len | member | says |
|---:|---:|---|---|
| 1191 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 1192 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 1194 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 1195 | 9 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 1206 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 1207 | 1 | `public double getSubsidyPaid(String key)` |  |
| 1209 | 5 | `public double getTotalSubsidyPaid()` |  |
| 1216 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 1229 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 1232 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 1251 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 1261 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 1296 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 1313 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 1329 | 5 | `public double getIncome()` | EARNED (0.7.31's name for it): the tax take less the running programmes, plus the utilities' net, at today's dials - the header's "+$X earned a month". |
| 1335 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1339 | 3 | `public double getWaterRatio()` |  |
| 1343 | 3 | `public double getRoadRatio()` |  |
| 1348 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1353 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1358 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1362 | 3 | `public LandManager getLandManager()` |  |
| 1367 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1376-1513)

| line | len | member | says |
|---:|---:|---|---|
| 1417 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1420 | 6 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1435 | 50 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1487 | 3 | `private static String usdWords(double thousands)` | Thousands of US dollars as the screens write them: "US$101.8M" (Formats.amount() with the dollar's mark). |
| 1492 | 3 | `private static String localWords(String here, double thousands)` | ...and thousands of local money, with its own mark: "D$72.0M". |
| 1503 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1510 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's price by (red when not, since 0.7.26 the only verdict on it); since 0.7.13 its button asks land... |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 1514-1671)

| line | len | member | says |
|---:|---:|---|---|
| 1547 | 5 | `public java.util.List<LandParcel> landShelf()` | The offers standing in the land office's order: cheapest ground first, per square foot of dry ground in US dollars (0.7.57: all forty) - the top-left card first. |
| 1554 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 1564 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 1574 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 1589 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 1594 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 1599 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 1611 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 1617 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 1622 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 1632 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 1663 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1668 | 3 | `public java.util.List<LandParcel> getLandListing()` | The offers standing. |

### THE BEST OFFER FOR WHAT THE CITY NEEDS (0.7.57, spec-land star 14) (lines 1672-1769)

| line | len | member | says |
|---:|---:|---|---|
| 1705 | 17 | **type** `public record LandNeed(Kind kind, double drySqFt, Resource resource)` | What the city needs ground for: room in general, a shortfall of dry ground, a resource's deposit, or a coast. |
| 1708 | 1 | **type** `public enum Kind` _(in Game.LandNeed)_ | The four needs. |
| 1711 | 1 | `public static LandNeed room()` _(in Game.LandNeed)_ | Room to grow. |
| 1714 | 1 | `public static LandNeed shortfall(double drySqFt)` _(in Game.LandNeed)_ | This much more dry ground than the city has free. |
| 1717 | 1 | `public static LandNeed deposit(Resource resource)` _(in Game.LandNeed)_ | A site of this resource to stand a mine or a well on. |
| 1720 | 1 | `public static LandNeed coast()` _(in Game.LandNeed)_ | Sea, for a desalination plant (batch J2). |
| 1724 | 28 | `public LandParcel bestOffer(LandNeed need)` | The offer that best meets a need, by THE BEST OFFER's rules; null when none does. |
| 1753 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1761 | 4 | `public void watchOrders(BusinessInvestment.OrderWatch watch)` | Harnesses only: tell this watch every order the three searches decide; null to stop. |
| 1766 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 1770-2330)

| line | len | member | says |
|---:|---:|---|---|
| 1780 | 91 | `private void runPrivateInvestment()` |  |
| 1887 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 1900 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 1912 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 1918 | 411 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 2331-2416)

| line | len | member | says |
|---:|---:|---|---|
| 2378 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 2381 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 2384 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 2385 | 1 | `public double getHouseholdCarImports()` |  |
| 2388 | 1 | `public double getHouseholdFuel()` | ...and what they paid for fuel this month (0.7.49): what the cells paid - the refiners' shelf and the world's (0.7.62). |
| 2391 | 1 | `public double getHouseholdFuelImports()` | ...and what of it they paid the world (0.7.62): every litre with no refinery; the money audit's FuelFunded and FuelImports. |
| 2397 | 1 | `public double getRowFeesSettled(int row)` | ...one row's. |
| 2400 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 2403 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 2406 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 2409 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 2412 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 2415 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 2417-2450)

| line | len | member | says |
|---:|---:|---|---|
| 2425 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 2428 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 2431 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 2434 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 2437 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 2440 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 2443 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 2446 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 2449 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 2451-2985)

| line | len | member | says |
|---:|---:|---|---|
| 2477 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 2496 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 2507 | 3 | `public double bankTaxUnder(TaxPolicy p)` | The bank's profit tax under another policy (0.7.36, the Policy spec's M2): Bank.taxAt() at that policy's retail rate, bankProfitTaxRate()'s rule - so NEXT month's bill, the tax being in arrears. |
| 2520 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 2537 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 2545 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 2573 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 2605 | 63 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 2676 | 16 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 2753 | 96 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 2862 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 2875 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 2902 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 2919 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 2946 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 2975 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 2986-3132)

| line | len | member | says |
|---:|---:|---|---|
| 3028 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 3031 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 3036 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 3041 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 3046 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 3073 | 50 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 3125 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 3131 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 3133-3254)

| line | len | member | says |
|---:|---:|---|---|
| 3141 | 12 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 3155 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 3158 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 3161 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 3164 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 3167 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 3177 | 21 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 3200 | 7 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 3220 | 29 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 3251 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 3255-3314)

| line | len | member | says |
|---:|---:|---|---|
| 3258 | 1 | `public double fundSharesValue()` | Its shares, both books, at the city's mark (Exchange.cityMark(): the last trade, fair value once that is a year old). |
| 3260 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 3262 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 3268 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 3270 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 3272 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes, on the bank's share at the city's mark. |
| 3276 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 3278 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 3282 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 3287 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands, at the withdrawal in force: TreasuryFund.withdrawalOn(fundValue()) - transferOn() at the default. |
| 3290 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the city's mark: the Holdings page's line for it until 0.7.39 (FundView reads each book itself now). |
| 3294 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 3298 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 3300 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 3305 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 3309 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 3315-3437)

| line | len | member | says |
|---:|---:|---|---|
| 3318 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 3319 | 7 | `public void setFundDial(double dial)` |  |
| 3328 | 1 | `public double getFundWithdrawal()` | The fund's withdrawal, a share of its whole value a month (0.7.48, C1): TreasuryFund.getWithdrawal(), Norway's rule by default. |
| 3330 | 8 | `public void setFundWithdrawal(double share)` | ...set by the player in whole steps of TreasuryFund.WITHDRAWAL_STEP, from nothing to 10% a month, and recorded. |
| 3340 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 3341 | 8 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 3359 | 14 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, month by month), a month's worth. |
| 3375 | 9 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 3390 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 3418 | 19 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 3438-3554)

| line | len | member | says |
|---:|---:|---|---|
| 3441 | 11 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 3454 | 11 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 3471 | 1 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 3474 | 8 | `public void fundBuyShares(int company, double money, double limit)` | ...at a price a share the player names (0.7.39, the spec's D2): 0 is fair value, what the rule asks at (it bids TreasuryFund.RULE_PREMIUM over since 0.7.48). |
| 3484 | 1 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 3487 | 8 | `public void fundSellShares(int company, double shares, double limit)` | ...at a price a share the player names (0.7.39): 0 is fair value. |
| 3497 | 1 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 3500 | 6 | `public void fundBuyBond(int bondId, double money, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 3508 | 1 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |
| 3511 | 7 | `public void fundSellBond(int bondId, double face, double limit)` | ...at a price a unit of face the player names (0.7.39): 0 is the bond's value. |
| 3520 | 3 | `public double fundCashFree()` | The fund's cash a new buy may hold: its cash less what the hand's buys already hold (TreasuryFund.handReserve(), 0.7.39). |
| 3525 | 4 | `private static String shareCount(double n)` | A count of shares in a decision's words: whole from a hundred, else to two places, and four under one - a consolidated holding is a fraction (0.7.39). |
| 3531 | 5 | `private String atLimit(double limit, boolean bond)` | A decision's words for a named price: " at D$101.35k", " at 102.80 per 100"; nothing at fair value. |
| 3545 | 9 | `public boolean fundCancelOrder(int i)` | CANCELS ONE OF THE PLAYER'S ORDERS before the step posts it (0.7.39, the spec's D3): the order at this place in TreasuryFund.getHandOrders(). |

### an Insane founding (lines 3555-3667)

| line | len | member | says |
|---:|---:|---|---|
| 3571 | 6 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar term loan, LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.landBondUsd() - STARTI... |
| 3609 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 3632 | 10 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 3652 | 9 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 3663 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 3668-3866)

| line | len | member | says |
|---:|---:|---|---|
| 3687 | 76 | `private void runRetirement()` |  |
| 3771 | 15 | `private void closeBranches(int wanted)` | Closes branches (0.7.19: as many as the rule says, at once; one at a time from 0.7.11 round 2 until then): the buildings retired by the path any retired building takes (retire()), sold by their owner, retail. |
| 3789 | 1 | `public int getBranchesClosed()` |  |
| 3802 | 64 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 3867-3916)

| line | len | member | says |
|---:|---:|---|---|
| 3887 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 3899 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 3908 | 1 | `public int getConstructionShedMonth()` |  |
| 3909 | 1 | `public double getConstructionShedPoints()` |  |
| 3912 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 3917-4213)

| line | len | member | says |
|---:|---:|---|---|
| 3937 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 3950 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 3970 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 3978 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 3985 | 3 | `public double getLastWriteOff()` |  |
| 4025 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 4035 | 178 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LARGEST SLICE, WITHOUT COUNTING TO IT (0.7.54) (lines 4214-4415)

| line | len | member | says |
|---:|---:|---|---|
| 4284 | 4 | `public static int deskCallsMost(int quantity)` | The most times an order of this many may ask the bond desk: the countdown's slices, then doubling down and halving, each at most once a bit of the order. |
| 4295 | 1 | **type** `public record Afford(int quantity, boolean atPrime, double firstRate, int deskCalls)` | What consider() decided of an order (0.7.54): the largest slice that carries its interest, 0 for none; whether any slice would have carried it at prime and its record, for the refusal; the rate at the whole order; and... |
| 4298 | 6 | `private Afford largestSliceThatCarries(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` | The largest slice of the order that carries its interest - see THE LARGEST SLICE, WITHOUT COUNTING TO IT. |
| 4312 | 21 | `static int largestSlice(int quantity, java.util.function.IntPredicate passes)` | The largest n from 1 to quantity that passes, 0 for none, found as THE LARGEST SLICE, WITHOUT COUNTING TO IT says: the countdown's first COUNTDOWN_SLICES, then doubling down, then halving. |
| 4335 | 80 | **type** `private final class Slices` | One order's slices, each asked as the countdown asked it (until 0.7.54, consider()'s loop). |
| 4344 | 6 | `Slices(BusinessInvestment.Decision decision, double cash, double perUnitProfit)` _(in Game.Slices)_ |  |
| 4352 | 35 | `boolean passes(int n)` _(in Game.Slices)_ | Whether n of them carry their interest; what failed at prime and the rate at the whole order are kept for the refusal. |
| 4389 | 4 | `boolean carriesAtPrime(int n, double borrowed)` _(in Game.Slices)_ | Whether n of them would carry their interest at prime and the sector's record. |
| 4395 | 10 | `void askPrimeOfTheRest(int q)` _(in Game.Slices)_ | When none passes: whether any of the q slices would have carried it at prime - every slice to PRIME_SCAN_SLICES, the four that decide it past that. |
| 4407 | 5 | `void askPrime(int n)` _(in Game.Slices)_ | Whether n of them would have carried it at prime, as the countdown asked it of a slice that failed. |
| 4413 | 1 | `Afford result(int quantity)` _(in Game.Slices)_ |  |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 4416-4594)

| line | len | member | says |
|---:|---:|---|---|
| 4473 | 5 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 4487 | 61 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, of what the planner asked for (Mortgage.decide(), which halves since 0.7.54) - and th... |
| 4557 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 4561 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 4574 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 4595-4672)

| line | len | member | says |
|---:|---:|---|---|
| 4631 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 4639 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 4644 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 4649 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 4652 | 20 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 4673-5372)

| line | len | member | says |
|---:|---:|---|---|
| 4686 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 4703 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 4725 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 4730 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 4833 | 1 | `public double getBankedClearedAtLoad()` |  |
| 4836 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 4858 | 29 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 4896 | 5 | `public int getConstructionOutputAtEveryPost()` | What the builders would do this month with every post offered, at the city's fill (0.7.17): the figure getConstructionOutput() was before the builders laid idle crews off. |
| 4903 | 4 | `public int getBuildingOutputAtEveryPost()` | ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. |
| 4917 | 9 | `void strikeBuildersCrews()` | The builders strike the month's crews (0.7.17): the work ahead - the repairs of the city standing now and every point still owed on site after this month's advance - against what their depots and the city's works depa... |
| 4938 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 4955 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 4992 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 5011 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 5042 | 76 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 5128 | 1 | `public double getCityMaintenancePaid()` |  |
| 5133 | 4 | `public int getConstructionMaterials()` |  |
| 5137 | 4 | `public double getInterestRate()` |  |
| 5141 | 3 | `public boolean isGraphsEnabled()` |  |
| 5144 | 3 | `public boolean isReportsEnabled()` |  |
| 5171 | 85 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 5258 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 5287 | 1 | `public boolean hasNewReceipt()` |  |
| 5288 | 1 | `public void clearReceipt()` |  |
| 5290 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 5304 | 68 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 5340 | 25 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 5367 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 5370 | 1 | `public double unitsBeyondYard()` _(in Game.BuildQuote)_ | The material units the crews will draw beyond the yard, which the allowance was priced on. |

### THE BUILDERS' PRICE (0.7.19) (lines 5373-5667)

| line | len | member | says |
|---:|---:|---|---|
| 5429 | 5 | `private String ownerOfOrder(BuildingsTemplate t)` | Who ordered a building of this kind, for a save that did not say (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays for them (BusinessInvestment.planBank()); the city for the rest. |
| 5443 | 6 | `private double recoveredOnOldContract(String payer, BuildingsTemplate t)` | ...and the share of the tax on it that owner gets back, for the same save (revised 0.7.19): on the tax a building of it carries at today's price (EconomyManager.taxRecoveredShare()). |
| 5462 | 18 | `private double depotWageBillPerPoint(boolean today)` | A Construction Depot's posts - the builders' own job mix - at today's wages or at the founding ladder (PayTier, in today's unit), over the points it makes. |
| 5482 | 3 | `private double buildersSalesRate()` | The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. |
| 5485 | 3 | `private double plantSalesRate()` |  |
| 5496 | 5 | `private double billableMaterial(double salvageCost, double plantCost, double importCost)` | What a draw of material cost the builders, as they bill it on (0.7.19): the salvage at what they paid for it, the plant's net of the credit they claim on it, the world's at its landed cost (its tax is charged and cred... |
| 5512 | 3 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 5517 | 22 | `private BuildQuote quoteBuild(BuildingsTemplate selected, int quantity, int yardHolds)` | ...against a yard holding `yardHolds` units (0.7.40): an order in a run, priced on the yard the orders before it leave (buildRunInvoice()). |
| 5549 | 4 | `public double quoteMonths(BuildingsTemplate template, int quantity)` | Months an order would take to finish (0.7.17): the wait every planner reads for its lead time and order size - BuildingManager.waitFor(), the order's points over the share of the site output it would get beside everyt... |
| 5562 | 5 | `public double onSiteMonths(BuildingsTemplate template)` | Months what is on site of one building would take to finish (0.7.20): the same wait at the same output as quoteMonths(), with no order added - BuildingManager.waitOnSite(). |
| 5579 | 4 | `public double siteMonths(String key)` | ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on site would take at today's queue - a stack's by its key, or a demolition's - at the output the quote reads. |
| 5592 | 4 | `public double quoteCityMonths(BuildingsTemplate template, int quantity)` | ...and what a city order of n of these would wait if placed now (0.7.22, after the docs pass): quoteMonths() exactly with no order set and no rush on its site; otherwise where the order would land in the player's orde... |
| 5622 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 5650 | 9 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |

### MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) (lines 5668-5761)

| line | len | member | says |
|---:|---:|---|---|
| 5698 | 31 | `public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues)` | Settles the month's site work with the owners who ordered it. |
| 5735 | 7 | `private double deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 5758 | 3 | `public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 5762-6163)

| line | len | member | says |
|---:|---:|---|---|
| 5795 | 3 | `public boolean isCitysToDemolish(BuildingsTemplate t)` | What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). |
| 5800 | 3 | `public boolean isBuyOutable(BuildingsTemplate t)` | What the city may buy out and demolish: a building a sector owns. |
| 5809 | 7 | **type** `public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price, d...` | A demolition, priced and laid out BEFORE the player commits: its work, its price, what it closes, what comes back and when. |
| 5814 | 1 | `public double salvageProceeds()` _(in Game.DemolitionQuote)_ | What the builders would pay for the material today, for as much as their cash covers. |
| 5818 | 8 | **type** `public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground, d...` | A buy-out, priced: the compensation, part by part, and the demolition after it. |
| 5822 | 1 | `public double compensation()` _(in Game.BuyOutQuote)_ | What the owner is paid. |
| 5824 | 1 | `public double total()` _(in Game.BuyOutQuote)_ | ...and what it all costs the city, the demolition included. |
| 5837 | 8 | `private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months)` | D. |
| 5847 | 27 | `public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n)` | What demolishing n standing buildings of this kind would do and cost. |
| 5876 | 13 | `public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell)` | A shell's demolition: DEMOLITION_SHARE of the work it holds, the material it drew to salvage, its ground. |
| 5891 | 4 | `private double demolitionMonths(BuildingsTemplate t, int n, double points)` | Months a demolition of these points would take at today's queue, at the output the quote reads. |
| 5904 | 15 | `public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n)` | E. |
| 5922 | 1 | `public String getLastHandRefusal()` |  |
| 5925 | 13 | `public boolean demolish(BuildingsTemplate t, int n)` | D. |
| 5940 | 36 | `public boolean buyOutAndDemolish(BuildingsTemplate t, int n)` | E. |
| 5978 | 4 | `public int demolishable(BuildingsTemplate t)` | How many of this kind stand that no demolition has been ordered for. |
| 5984 | 9 | `private void placeDemolition(DemolitionQuote q, String from, double groundPaid)` | The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). |
| 6004 | 11 | `private void closeDemolished()` | D. |
| 6017 | 17 | `public boolean demolishShell(int templateId)` | C. |
| 6042 | 14 | `public BuildQuote quoteRestart(ConstructionControl.Shell shell)` | C. |
| 6058 | 4 | `private double restartMonths(BuildingsTemplate t, int n, double pointsLeft)` | A restart's wait, by the one wait (BuildingManager.restartWait()). |
| 6064 | 21 | `public boolean restartShell(int templateId)` | C. |
| 6087 | 11 | `public boolean cancelSite(String key, boolean cancel)` | C. |
| 6106 | 8 | `public double[] cancelRefundNow(String key)` | C. |
| 6116 | 10 | `public boolean rushSite(String key, boolean on)` | B. |
| 6128 | 11 | `public boolean moveSite(String key, int by)` | A. |
| 6141 | 5 | `public void resetSiteOrder()` | A. |
| 6154 | 9 | `public double[] rushPreview(String key)` | B. |

### in the month (lines 6164-6387)

| line | len | member | says |
|---:|---:|---|---|
| 6173 | 1 | `public ConstructionControl.Events getControlThisMonth()` |  |
| 6174 | 1 | `public double getOvertimePaidThisMonth()` |  |
| 6175 | 1 | `public double getDemolitionSalvageThisMonth()` |  |
| 6182 | 64 | `public void settleConstructionControl(ConstructionControl.Events ev)` | Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. |
| 6248 | 14 | `private double[] overtimeWagesByType(double premium)` | The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). |
| 6282 | 101 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 6386 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 6388-6489)

| line | len | member | says |
|---:|---:|---|---|
| 6403 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 6415 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 6431 | 1 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 6434 | 1 | `public int wellsCommitted()` | ...and the Oil Wells standing, being built or ordered (0.7.62): one an oil site. |
| 6441 | 12 | `public int committedOn(Resource r)` | The MINING buildings that stand on a resource's sites - standing, on site or ordered (0.7.62): every MINING template whose good is the resource's (siteOf()). |
| 6460 | 7 | `public static Resource siteOf(BuildingsTemplate t)` | The resource whose sites a building stands on (0.7.62): a MINING template's, the resource whose good it makes - iron for an Iron Mine, oil for an Oil Well; null for anything else. |
| 6478 | 3 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 6483 | 6 | `private boolean hasDepositFor(BuildingsTemplate template, int quantity, int before)` | ...with `before` more on the same resource's sites already put on site by the orders ahead of it in a run (0.7.40, buildRunAhead()). |

### THE FRESH WATER LIMIT AND THE COAST (0.7.59, batch J2; spec-land 2.3) (lines 6490-6700)

| line | len | member | says |
|---:|---:|---|---|
| 6515 | 3 | `public double getFreshRights()` | The city's water rights, units a month (see THE FRESH WATER LIMIT AND THE COAST). |
| 6520 | 3 | `void setFreshRights(double units)` | Harnesses and the load only: sets the rights, never below nothing. |
| 6525 | 3 | `public double getFreshCap()` | The fresh water limit, units a month: what the owned lakes and river yield plus the rights. |
| 6537 | 8 | `double derivedFreshRights()` | The rights an older save is given (spec-land 2.9): the fresh plants' nameplate standing less what the city's lakes and river yield, never below nothing - raised by the last unit in the last place when the limit they m... |
| 6547 | 3 | `public boolean hasCoastFor(BuildingsTemplate template, int quantity)` | Whether this order can stand on the city's coast: true for everything but a desalination plant, which needs owned sea. |
| 6560 | 64 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 6634 | 1 | `public Investor getGovernmentInvestor()` |  |
| 6637 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 6639 | 46 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 6686 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 6697 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |

### A RUN OF ORDERS (0.7.40) (lines 6701-7230)

| line | len | member | says |
|---:|---:|---|---|
| 6738 | 12 | `public double buildRunInvoice(java.util.Map<BuildingsTemplate, Integer> run)` | What placing these orders in turn would charge altogether: each one's quote on the yard the ones before it leave. |
| 6752 | 3 | `public double buildFundingGap(java.util.Map<BuildingsTemplate, Integer> run)` | What the treasury is short of for the whole run: its invoice less the cash, never below nothing - an overdraft counted in full, as one order's buildFundingGap(template, quantity) counts it. |
| 6757 | 3 | `public int buildRunAhead(java.util.Map<BuildingsTemplate, Integer> run)` | How many of the run's orders, from the first, would pass the checks money cannot fix - ore, the coast, licences, ground - each placed after the ones before it. |
| 6762 | 5 | `public BuildResult buildRunStop(java.util.Map<BuildingsTemplate, Integer> run)` | What the first order that would not go ahead is refused for - NO_DEPOSIT, NO_COAST, NO_LICENCE or NO_LAND - or SUCCESS when every order passes. |
| 6768 | 26 | `private int runAhead(java.util.Map<BuildingsTemplate, Integer> run, BuildResult[] stop)` |  |
| 6796 | 3 | `public double cashShortfall()` | What the treasury is overdrawn by right now (0.7.40): the cash below nothing, or nothing - Finances' "overdrawn by" ask. |
| 6801 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). |
| 6804 | 3 | `public double getTotalBuildingCost()` |  |
| 6807 | 3 | `public String getBuildingName()` |  |
| 6810 | 3 | `public int getBuildQuantity()` |  |
| 6815 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 6828 | 111 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 6947 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 6959 | 13 | `private String issueNote(DebtQuote quote)` | The booking every note shares - handleTBillLogic()'s, and since 0.7.13 the rollover's, which books the quote it sized (issueForRollover()). |
| 6979 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 7017 | 17 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 7077 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 7089 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 7110 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 7139 | 35 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 7180 | 5 | `private double longBondNetProceeds(double faceValue, double marketRate, int duration)` | What the city actually banks for a long bond of this face: its worth at the rate struck, less the fees, to the cent. |
| 7187 | 13 | `private DebtQuote longBondQuote(double requested, int duration, double marketRate, double before, double faceValue, double rece...` | A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. |
| 7202 | 7 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |
| 7214 | 16 | `private String issueLongBond(DebtQuote quote)` | The booking both long-bond issues share. |

### A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) (lines 7231-7314)

| line | len | member | says |
|---:|---:|---|---|
| 7259 | 8 | `private double longBondFaceForProceeds(double cashNeeded, int duration, double rounding, double marketRate)` | Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. |
| 7276 | 29 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a term bond whose CASH covers cashNeeded would cost. |
| 7307 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 7315-7649)

| line | len | member | says |
|---:|---:|---|---|
| 7357 | 14 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 7395 | 11 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 7420 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation - expected, since 0.7.42 (below) - less the world's base rate less the world's realised inflation - the one defi... |
| 7433 | 5 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the inflation savers expect (Expectations, since 0.7.42 - the same expected inflation the real rate differential reads; the year's, as the parity ... |
| 7440 | 3 | `public double realPolicyRate()` | The policy rate less the inflation people expect (0.7.45): the dial in real terms, ex ante as every real rate here is since 0.7.42. |
| 7452 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 7457 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 7460 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 7467 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 7534 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 7554 | 78 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 7639 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |

### THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 7650-7724)

| line | len | member | says |
|---:|---:|---|---|
| 7671 | 12 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | A serial bond whose CASH covers cashNeeded: quoteMediumBond() at the face that brings it. |
| 7689 | 14 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note, serial or term loan whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 7705 | 8 | `private double foreignProceedsPerFace(String type, double rate, int duration)` | What a unit of a dollar instrument's face banks at this rate, net of the spread - never under MIN_PROCEEDS_PER_FACE, so the search above always moves. |
| 7715 | 9 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 7725-9195)

| line | len | member | says |
|---:|---:|---|---|
| 7803 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 7806 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 7809 | 11 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 7827 | 8 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 7837 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not, and whether it is dollar paper rolled at home. |
| 7839 | 13 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 7854 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 7868 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 7939 | 54 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 8002 | 9 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; a serial by quoteMediumBondForCash(); a term loan by quoteLo... |
| 8013 | 4 | `private double localFace(boolean abroad, DebtQuote quote)` | A rollover quote's face in local money: a dollar quote's at the day's rate. |
| 8024 | 18 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 8045 | 3 | `private void printPopulationInfo()` | printers |
| 8050 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 8063 | 3 | `private void printUtilityInfo()` |  |
| 8067 | 3 | `private void printCityStats()` |  |
| 8074 | 818 | `private void nextMonth()` |  |
| 8893 | 274 | `private void startOfMonthUpdate()` |  |
| 9167 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 9196-9333)

| line | len | member | says |
|---:|---:|---|---|
| 9245 | 4 | `private double issuanceFee()` | The fee in today's money: founding / unit, at the expected price level since 0.7.42 (THE ANCHOR, beside restrikeMoneyConstants()). |
| 9270 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 9282 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 9302 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 9324 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |
| 9330 | 3 | `public double minimumIssueSizeUsd()` | ...in US dollars at today's rate (0.7.40): Finances' "the minimum" on the ask abroad, which is asked in dollars. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 9334-9601)

| line | len | member | says |
|---:|---:|---|---|
| 9348 | 16 | `public String getCreditRating()` |  |
| 9366 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 9389 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 9403 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 9425 | 28 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 9454 | 90 | `private void finalUpdateEconomy()` |  |
| 9546 | 4 | `private void updateConstructionCost()` |  |
| 9557 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 9568 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 9572 | 4 | `public long getHouseholdCapacity()` |  |
| 9576 | 4 | `public long getStoreCapacity()` |  |
| 9580 | 3 | `public long[] getJobs()` |  |
| 9585 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 9602-10206)

| line | len | member | says |
|---:|---:|---|---|
| 9631 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 9667 | 1 | `public PopulationCohorts getCohorts()` |  |
| 9668 | 1 | `public FamilyModel getFamilies()` |  |
| 9669 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 9670 | 1 | `public Migration getMigration()` |  |
| 9671 | 1 | `public Health getHealth()` |  |
| 9672 | 1 | `public Healthcare getHealthcare()` |  |
| 9696 | 510 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 10207-10219)

| line | len | member | says |
|---:|---:|---|---|
| 10215 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 10218 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 10220-10508)

| line | len | member | says |
|---:|---:|---|---|
| 10236 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 10255 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 10275 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 10317 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 10343 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 10353 | 3 | `public BuildLog getBuildLog()` |  |
| 10360 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 10385 | 1 | `public Unemployment getUnemployment()` |  |
| 10389 | 1 | `public Sickness getSickness()` |  |
| 10397 | 1 | `public Crime getCrime()` |  |
| 10401 | 1 | `public double getLastOrphanDeaths()` |  |
| 10402 | 1 | `public double getLastUnhousedDeaths()` |  |
| 10404 | 6 | `{ ... }` |  |
| 10412 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 10437 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 10446 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 10467 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 10478 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 10491 | 1 | `public double getStudentLoansLent()` |  |
| 10492 | 1 | `public double getStudentLoansRepaid()` |  |
| 10493 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 10497 | 1 | `public double getStudentLoanInterest()` |  |
| 10500 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 10507 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 10509-10643)

| line | len | member | says |
|---:|---:|---|---|
| 10524 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 10530 | 7 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 10539 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 10551 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 10595 | 1 | `public Equity getEquity()` |  |
| 10599 | 1 | `public Exchange getExchange()` |  |
| 10607 | 1 | `public BondMarket getBondMarket()` |  |
| 10617 | 1 | `public TreasuryFund getFund()` |  |
| 10633 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### THE ANCHOR (0.7.42): what the city expects prices to do, and the (lines 10644-10682)

| line | len | member | says |
|---:|---:|---|---|
| 10663 | 1 | `public Expectations getExpectations()` | Expected inflation, credibility and the expected price level. |
| 10666 | 1 | `public double getExpectedInflation()` | What the city expects inflation to be, a fraction a year. |
| 10669 | 1 | `public double getCredibility()` | How far the city believes the central bank, Expectations.KMIN to KMAX. |
| 10677 | 5 | `public double anchoredDrift()` | The currency's anchored drift, a fraction a year (ForeignAccounts, THE ANCHORED DRIFT): the credible part of expected inflation - credibility times the target - against the world's realised inflation, and nothing befo... |

### what the price index is handed (0.7.43; PriceIndex, WHAT IS IN THE BASKET) (lines 10683-10813)

| line | len | member | says |
|---:|---:|---|---|
| 10686 | 26 | `double[] indexPrices()` | Each component's price this month, in PriceIndex's order: the shelf, the average rent paid, a meal, a luxury piece; the services' price is struck by the index from the fees. |
| 10714 | 8 | `double[] indexSpends()` | ...what the households spent on each: groceries and rent off the sectors' statements, meals and luxury off their own purchases; the services' is the index's sum of the fee lines. |
| 10729 | 14 | `double[] indexFees()` | ...each fee line's price: the clinic's fee for a general visit at the player's scale, a seat's tuition (the adult kinds' fees averaged - a price, so a shift in who studies what is not inflation), the fare a ride is ch... |
| 10745 | 8 | `double[] indexFeeSpends()` | ...and what the households paid on each fee line, off their books. |
| 10782 | 23 | `void restrikeMoneyConstants()` | Every money constant that prices something, struck at the expected price level: founding / unit x Expectations.getStruckLevel(), the level the last month ended on. |
| 10807 | 6 | `private void handOnExpectations()` | ...and what the month hands on from the anchor that is not a constant: the investors' expected inflation (0 before the basket is based), the currency's drift, and - since 0.7.43 - the drift every sticky seller's price... |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 10814-10837)

| line | len | member | says |
|---:|---:|---|---|
| 10824 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 10833 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 10836 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 10838-11189)

| line | len | member | says |
|---:|---:|---|---|
| 10850 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 10852 | 1 | `public PriceIndex getPriceIndex()` |  |
| 10854 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 10861 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 10868 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 10879 | 3 | `public Sectors.TradeByGood getTradeByGood()` | The month across the city's edge, good by good (0.7.35): the businesses' struck statements split by good, an import with no good behind it by the sector that bought it, the households' cars among the cars bought and, ... |
| 10893 | 3 | `public double getOwnReserves()` | What is left of the vault once everything owed against it is taken off: the dollar paper outstanding and the foreign money parked in the bank, both claims on the one pot (0.7.35; the Trade tab's own subtraction until ... |
| 10898 | 5 | `public double getSharesHeldAbroad()` | The city's shares in foreign hands, at each company's price - its last trade, or its fair value before one (0.7.35; the Trade tab's own sum until then). |
| 10912 | 3 | `public double getHeldAbroadPrivately()` | The businesses' and the households' paper abroad, at today's rate: what is held abroad outside the vault. |
| 10917 | 3 | `public double getHeldAbroad()` | ...and the vault with it: everything the city holds abroad. |
| 10922 | 3 | `public double getHeldHereByTheWorld()` | What the world holds here: the city's shares at their price, the businesses' bonds at face, the foreign money parked in the bank and the city's dollar paper at today's rate. |
| 10933 | 1 | `public Bank getBank()` |  |
| 10946 | 1 | `public CentralBank getCentralBank()` |  |
| 10960 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 10966 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 10969 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 10976 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 10980 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 10984 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 11051 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 11055 | 3 | `public EconomyManager getEconomyManager()` |  |
| 11058 | 3 | `public PopulationManager getPopulationManager()` |  |
| 11062 | 1 | `public LabourMarket getLabourMarket()` |  |
| 11063 | 1 | `public Education getEducation()` |  |
| 11079 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 11104 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 11146 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 11164 | 23 | `public void recordMonth()` | Files this month in the graph history. |
| 11188 | 1 | `public Inbox getInbox()` |  |
| 11189 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 11190-11637)

| line | len | member | says |
|---:|---:|---|---|
| 11201 | 3 | `public int getMonthsUntilAutosave()` |  |
| 11217 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 11229 | 291 | `public void save(int slot, String slotName)` |  |
| 11529 | 5 | `public String takeSkipFailure()` |  |
| 11538 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 11540 | 1 | `public GameFiles getGameFiles()` |  |
| 11559 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 11577 | 18 | `public void sendBuildingSave()` |  |
| 11598 | 16 | `public void loadBuildings()` |  |
| 11628 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 11638-11778)

| line | len | member | says |
|---:|---:|---|---|
| 11665 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 11666 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 11667 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 11674 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 11696 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 11763 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 11772 | 1 | `public double getCityPaperSettled()` |  |
| 11773 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 11774 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 11776 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 11779-11854)

| line | len | member | says |
|---:|---:|---|---|
| 11807 | 1 | `public double getCouponsToHouseholds()` |  |
| 11808 | 1 | `public double getPrincipalToHouseholds()` |  |
| 11809 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 11820 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 11828 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 11841 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 11855-11880)

| line | len | member | says |
|---:|---:|---|---|
| 11865 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 11881-11937)

| line | len | member | says |
|---:|---:|---|---|
| 11898 | 39 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 11938-12084)

| line | len | member | says |
|---:|---:|---|---|
| 11992 | 56 | `private void openMarketOperation()` |  |
| 12056 | 28 | `private void buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 12085-12320)

| line | len | member | says |
|---:|---:|---|---|
| 12190 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 12193 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 12200 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 12212 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 12219 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 12225 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 12238 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 12269 | 51 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 12321-12342)

| line | len | member | says |
|---:|---:|---|---|
| 12336 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 12340 | 1 | `public double getBuybackToHouseholds()` |  |
| 12341 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 12343-12497)

| line | len | member | says |
|---:|---:|---|---|
| 12414 | 13 | `private void takeTreasuryMonth()` |  |
| 12429 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 12431 | 1 | `public double getTreasuryOpening()` |  |
| 12432 | 1 | `public double getTreasuryClosing()` |  |
| 12433 | 1 | `public double getTreasuryRaised()` |  |
| 12434 | 1 | `public double getTreasuryRepaid()` |  |
| 12435 | 1 | `public double getTreasurySurplus()` |  |
| 12438 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 12448 | 3 | `public double getNetPosition()` | The city's net position (0.7.32, the Finances tab's THE BALANCE): its cash - below nothing when it is overdrawn - less the paper it owes and what its central bank has advanced it. |
| 12469 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 12481 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 12486 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 12494 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |

### FROM EARNED TO THE BUDGET (0.7.31) (lines 12498-12619)

| line | len | member | says |
|---:|---:|---|---|
| 12532 | 17 | `public java.util.List<TreasuryJournal.Entry> getEarnedToBudget()` | The steps from EARNED to the budget's balance, in the Government bridge's order, signed as they move EARNED (+ adds, - takes away); every line, at nothing too. |
| 12551 | 5 | `public double getEarnedResidual()` | What the steps leave between EARNED and the budget: the dials moved since the month was struck, and nothing in a month nobody moved one. |
| 12557 | 5 | `double[] treasuryMonthToSave()` |  |
| 12563 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 12597 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 12600 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 12602 | 1 | `public double getPostAuditDriftWorst()` |  |
| 12603 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 12604 | 4 | `public void InterestExpense(double amount)` |  |
| 12609 | 3 | `public DebtManager getDebtManager()` |  |
| 12614 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 12620-12902)

| line | len | member | says |
|---:|---:|---|---|
| 12688 | 41 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 12741 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 12746 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 12768 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 12780 | 19 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 12808 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 12833 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 12840 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 12844 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 12854 | 8 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 12864 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 12867 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 12874 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 12884 | 10 | `public double getArrearsOwedTo(String sectorKey)` | Owed and unpaid to one sector's till (0.7.55): every line payDownArrears() would pay it, by the payee it would pay. |
| 12896 | 1 | `public double getArrearsPaidTo(String sectorKey)` | What the treasury paid this sector's till of its arrears this month (0.7.55) - its statement's arrears line. |
| 12898 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 12899 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 12900 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 12901 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 12903-13029)

| line | len | member | says |
|---:|---:|---|---|
| 12916 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 12924 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 12949 | 72 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 13030-14920)

| line | len | member | says |
|---:|---:|---|---|
| 13098 | 20 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 13119 | 326 | `private void rebuildSimulationState()` |  |
| 13487 | 10 | `public void loadGame(int slot)` | Load game |
| 13505 | 21 | `private void readTheMap(int slot, Long stamp)` | THE CITY MAP, READ BACK (0.7.60): from the slot's sidecar when the save's stamp is its stamp and it was drawn on this land in this month (CityMap.readSidecar()); a save that had a map whose sidecar is missing or stale... |
| 13554 | 43 | `private boolean restoreLandOnTheWorld(DataSave loaded, double owned)` | THE LAND ON THE WORLD, PUT BACK OR CONVERTED (0.7.57, SAVE_FORMAT 31; on the block grid since 0.7.67, SAVE_FORMAT 32). |
| 13599 | 1264 | `private void readTheSave(int slot)` | The load itself; see loadGame(). |
| 14870 | 4 | `void seedFundLedger()` | THE FUND'S COST BASIS FOR A SAVE FROM BEFORE IT (0.7.39; the project's spec-fund-0739.md, 3.5): each market lot and bond at its market value this month, flagged as such, the rescue lot exact from the counters, one TRA... |
| 14879 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 14921-15173)

| line | len | member | says |
|---:|---:|---|---|
| 14927 | 1 | `public Denomination getDenomination()` |  |
| 14930 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 14961 | 189 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 15161 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

