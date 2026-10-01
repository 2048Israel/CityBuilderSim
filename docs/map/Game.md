# Game.java - 13,550 lines · 508 methods · 15 constants · model

`ham/citybuildersim/Game.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [DecisionLog](DecisionLog.md) (57), [Equity](Equity.md) (57), [TreasuryLine](TreasuryLine.md) (52), [BuildingsTemplate](BuildingsTemplate.md) (46), [DebtQuote](DebtQuote.md) (40), [GameLog](GameLog.md) (36), [AgeBand](AgeBand.md) (36), [Debt](Debt.md) (34), [BusinessDebtManager](BusinessDebtManager.md) (32), [Rollover](Rollover.md) (27), [GameFiles](GameFiles.md) (25), [ConstructionControl](ConstructionControl.md) (24), [TreasuryFund](TreasuryFund.md) (23), [CareType](CareType.md) (21), [JobType](JobType.md) (20), [Bank](Bank.md) (20), [DebtManager](DebtManager.md) (19), [Founding](Founding.md) (19), [TaxPolicy](TaxPolicy.md) (18), [Sector](Sector.md) (17), [FamilyModel](FamilyModel.md) (16), [Investor](Investor.md) (15), [HouseholdAccounts](HouseholdAccounts.md) (14), [BuildingType](BuildingType.md) (14), [LongTermBond](LongTermBond.md) (13), [BuildingManager](BuildingManager.md) (12), [LandParcel](LandParcel.md) (12), [Good](Good.md) (12), [PayTier](PayTier.md) (11), [Healthcare](Healthcare.md) (10)... and 68 more

**Used by (107):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [ChartCheck](ChartCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [ConstructionControlCheck](ConstructionControlCheck.md), [ConstructionScreen](ConstructionScreen.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FundCheck](FundCheck.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 814 | THE FOUNDING RESERVE (2026-09-21) |
| 870 | · THE FOUNDING RECORD (0.7.10) |
| 925 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 948 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1160 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1281 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 1446 | PRIVATE INVESTMENT |
| 1764 | · AND THE BALANCE SHEET |
| 1961 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 2012 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 2046 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 2564 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 2706 | · the preferred offer |
| 2822 | · what the fund is worth |
| 2882 | · the dial |
| 2992 | · the hand |
| 3053 | · an Insane founding |
| 3166 | SHRINKING |
| 3365 | THE CONSTRUCTION WARNING |
| 3415 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 3743 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 3900 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 3978 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 4676 | THE BUILDERS' PRICE (0.7.19) |
| 4966 | MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) |
| 5060 | THE PLAYER'S HAND ON THE QUEUE (0.7.22) |
| 5462 | · in the month |
| 5685 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 6321 | · A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) |
| 6405 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 6725 | · THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 6800 | ROLLING WHAT FALLS DUE (0.7.13) |
| 7209 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 7469 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 7685 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 7868 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 8213 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 8346 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 8597 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 9183 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 9196 | THE READINGS THE MONTH TAKES OF THE CITY |
| 9485 | · the price of a place (2026-09-21) |
| 9618 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 9642 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 9938 | the save system |
| 10352 | PAYING THE WORLD BACK |
| 10493 | THE HOLDERS ARE PAID (0.7.1) |
| 10569 | · the desk, for the households |
| 10595 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 10652 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 10799 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 11035 | · a buyback's holders outside the pools |
| 11057 | WHAT THE TREASURY ACTUALLY DID |
| 11262 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 11516 | BUYING YOUR OWN DEBT BACK |
| 11643 | WHY LAND IS NOT IN THE RENT FLOOR |
| 11991 | · · the three the monthly path sets and this did not |
| 13317 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 5683 | `Game.BuildResult.SUCCESS` |  |
| 5683 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 5683 | `Game.BuildResult.NO_LAND` |  |
| 5683 | `Game.BuildResult.NO_DEPOSIT` |  |
| 5683 | `Game.BuildResult.NO_LICENCE` |  |
| 5683 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 559 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 862 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 865 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 868 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 5697 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 6463 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 6621 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 6743 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 8229 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 8241 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 8244 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 8259 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 8268 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, WHAT AN ISSUE COSTS). |
| 8276 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 9945 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `private boolean isRunning` |  |
| 29 | `private int month` | Game state fields |
| 30 | `private double cash` |  |
| 31 | `private int population` |  |
| 32 | `private int[] jobs` |  |
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
| 784 | `private String loadFailure` | Why the last load did not happen, or null. |
| 882 | `private Founding founding` |  |
| 912 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 983 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 984 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1198 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1425 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under the toggle, and a short vault says here that the rest was converted. |
| 1426 | `private int lastLandReceiptMonth` |  |
| 1979 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1982 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 2017 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 2277 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 2603 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 3286 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 3373 | `private int constructionShedMonth` |  |
| 3374 | `private double constructionShedPoints` |  |
| 3433 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 3446 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 3466 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 3481 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 3859 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 3860 | `private final java.util.Set<String> heldForDownPayment` |  |
| 3939 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 3940 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 3941 | `private double salvageUsedThisMonth` |  |
| 3998 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 4136 | `private double bankedClearedAtLoad` | Construction points the last load cleared from stacks that carried them past what they owed (0.7.17; see the load path and BuildingManager.clearBankedProgress()). |
| 4429 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 4434 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 4608 | `public final int quantity` |  |
| 4609 | `public final double sticker` |  |
| 4610 | `public final double materialsNeeded` |  |
| 4612 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 4614 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 4615 | `public final double plantPrice` |  |
| 4616 | `public final double plantCost` |  |
| 4617 | `public final double materialsImported` |  |
| 4618 | `public final double materialsPrice` |  |
| 4619 | `public final double importCost` |  |
| 4625 | `public final double salesTax` | The sales tax in the price (0.7.19): what the builders remit on it, less what they claim back on the plant's material - passed on, as a shop's shelf price passes it on. |
| 4632 | `public final double allowance` | What the price allows for the material beyond the yard (0.7.19): the plant's and the world's at today's prices, net of the credit on the plant's and with the builders' tax on it - the part of the total an escalation c... |
| 4633 | `public final double total` |  |
| 4634 | `public final double landNeeded` |  |
| 4635 | `public final double landFree` |  |
| 4641 | `public final double months` | Months to finish at this month's shares of the site output, the planners' own reading (quoteMonths()), or NaN when the builders have no site output. |
| 4739 | `private boolean contractsToInfer` | Set by the load path when a save carries no payers, and read once the land price is back. |
| 4754 | `private final BuildingManager.BuildersWages buildersWages` | What the builders' crews cost a point, today and at founding. |
| 4964 | `private double siteDrawBillable` | What the month's site draw cost the builders, as they bill it on (billableMaterial()): what the owners' escalation is struck against. |
| 5219 | `private String lastHandRefusal` | Why a demolition or a buy-out was not made, or null when it was. |
| 5465 | `private ConstructionControl.Events controlThisMonth` | This month's events, kept for the screens and the inbox: what the advance left, settled. |
| 5467 | `private double overtimePaidThisMonth` | What the treasury paid the builders this month for overtime, with their tax in it. |
| 5469 | `private double demolitionSalvageThisMonth` | What a finished demolition's material fetched this month, from the builders. |
| 5824 | `private final Investor government` | The city itself, as a payer. |
| 6874 | `private final Rollover rollover` |  |
| 8590 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 8591 | `private LandManager landManager` |  |
| 8594 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 8595 | `private BuildLog buildLog` |  |
| 8622 | `private PopulationCohorts cohorts` |  |
| 8623 | `private FamilyModel families` |  |
| 8631 | `private Migration migration` |  |
| 8637 | `private LabourMarket labourMarket` | What labour costs. |
| 8643 | `private Education education` | The schools. |
| 8652 | `private Health health` | How much of the workforce is off sick. |
| 8660 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 9188 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 9334 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 9341 | `private HouseholdAccounts households` | The residents' own books. |
| 9350 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 9360 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 9364 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 9372 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 9376 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 9466 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 9472 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 9545 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 9555 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 9561 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 9568 | `private final Equity equity` | Who owns the city's companies. |
| 9572 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 9580 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 9590 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 9594 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 9616 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 9624 | `private final Consumption consumption` |  |
| 9625 | `private boolean consumptionLoaded` |  |
| 9634 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 9652 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 9661 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 9662 | `private int rateHistoryFilled` |  |
| 9680 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 9693 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 9737 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 9750 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 9764 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 9782 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 9785 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 9787 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 9788 | `private double carriedRetailCapacity` |  |
| 9789 | `private double carriedRetailWant` |  |
| 9792 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 9793 | `private java.util.Map<String, String> lastInvestment` |  |
| 9796 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 9840 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 9947 | `private int monthsSinceAutosave` |  |
| 10241 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 10250 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 10375 | `private double foreignDebtRaisedThisMonth` |  |
| 10376 | `private double foreignPrincipalRepaidThisMonth` |  |
| 10377 | `private double foreignInterestPaidThisMonth` |  |
| 10429 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 10449 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 10450 | `private double cityPrincipalRepaidThisMonth` |  |
| 10464 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 10465 | `private double cityDiscountForBank` |  |
| 10468 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 10485 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 10491 | `private double bankPrincipalRepaidThisMonth` |  |
| 10519 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 10610 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 10898 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 10901 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 11045 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 11047 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 11099 | `private double treasuryOpening` |  |
| 11100 | `private double treasuryClosing` |  |
| 11101 | `private double treasuryRaised` |  |
| 11102 | `private double treasuryRepaid` |  |
| 11103 | `private double treasurySurplus` |  |
| 11104 | `private boolean treasuryRecorded` |  |
| 11123 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 11126 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 11223 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 11226 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 11234 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 11235 | `private String postAuditDriftPool` |  |
| 11236 | `private double postAuditDriftWorst` |  |
| 11300 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 11303 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 11306 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 11641 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 13272 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 13321 | `private final Denomination denomination` |  |
| 13543 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 13526 | **type** `public class Game` |  |
| 69 | 1 | `public DecisionLog getDecisions()` | The player's decisions, oldest first (0.7.23): what the chart's flags are drawn from. |
| 145 | 3 | `public Game()` |  |
| 157 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 166 | 5 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 173 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 205 | 309 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 515 | 4 | `public void run()` |  |
| 520 | 38 | `private void initialize()` |  |
| 561 | 4 | `static { ... }` |  |
| 569 | 3 | `public void newGame()` | A new city on the defaults, as "Found with defaults" founded one until 0.7.21; the harnesses' new game since. |
| 578 | 40 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 648 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 678 | 8 | `public void resumeGame()` |  |
| 686 | 56 | `public void loadGameSave(int slot)` |  |
| 751 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 759 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 774 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 776 | 6 | `public void toggleQuit()` |  |
| 786 | 1 | `public String getLoadFailure()` |  |
| 788 | 7 | `public void toggleGraphs()` |  |
| 795 | 5 | `public void toggleReports()` |  |
| 801 | 3 | `public void toggleNextMonth()` |  |
| 806 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 808 | 3 | `public int getMonth()` |  |
| 811 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 814-869)

### THE FOUNDING RECORD (0.7.10) (lines 870-924)

| line | len | member | says |
|---:|---:|---|---|
| 885 | 4 | `public Founding getFounding()` | How this city was founded. |
| 891 | 1 | `public String getCityName()` | The city's name. |
| 894 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 897 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 900 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 903 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 921 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset until 0.7.20, when the screen stopped saying what the money buys; NewGameChec... |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 925-947)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 948-1159)

| line | len | member | says |
|---:|---:|---|---|
| 986 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 987 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 989 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 990 | 9 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 1001 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 1002 | 1 | `public double getSubsidyPaid(String key)` |  |
| 1004 | 5 | `public double getTotalSubsidyPaid()` |  |
| 1011 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 1024 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 1027 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 1046 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 1056 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 1091 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 1108 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 1114 | 4 | `public double getIncome()` |  |
| 1119 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1123 | 3 | `public double getWaterRatio()` |  |
| 1127 | 3 | `public double getRoadRatio()` |  |
| 1132 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1137 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1142 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1146 | 3 | `public LandManager getLandManager()` |  |
| 1151 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1160-1280)

| line | len | member | says |
|---:|---:|---|---|
| 1201 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1204 | 6 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1219 | 43 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1270 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1277 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's tile by; since 0.7.13 its button asks landNeedsFunding() instead. |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 1281-1445)

| line | len | member | says |
|---:|---:|---|---|
| 1314 | 5 | `public java.util.List<LandParcel> landShelf()` | The plots on offer in the land office's order: cheapest ground first, per square foot in US dollars - the top-left card first. |
| 1321 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 1331 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 1341 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 1356 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 1361 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 1366 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 1378 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 1384 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 1389 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 1399 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 1429 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1434 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 1438 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1442 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 1446-1960)

| line | len | member | says |
|---:|---:|---|---|
| 1456 | 91 | `private void runPrivateInvestment()` |  |
| 1563 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 1576 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 1588 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 1594 | 365 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1961-2011)

| line | len | member | says |
|---:|---:|---|---|
| 1985 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1988 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1991 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1992 | 1 | `public double getHouseholdCarImports()` |  |
| 1995 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1998 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 2001 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 2004 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 2007 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 2010 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 2012-2045)

| line | len | member | says |
|---:|---:|---|---|
| 2020 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 2023 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 2026 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 2029 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 2032 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 2035 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 2038 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 2041 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 2044 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 2046-2563)

| line | len | member | says |
|---:|---:|---|---|
| 2072 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 2091 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 2104 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 2121 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 2129 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 2157 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 2189 | 57 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 2254 | 16 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 2331 | 96 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 2440 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 2453 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 2480 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 2497 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 2524 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 2553 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 2564-2705)

| line | len | member | says |
|---:|---:|---|---|
| 2606 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 2609 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 2614 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 2619 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 2624 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 2651 | 45 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 2698 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 2704 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 2706-2821)

| line | len | member | says |
|---:|---:|---|---|
| 2714 | 12 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 2728 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 2731 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 2734 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 2737 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 2740 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 2750 | 20 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 2772 | 7 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 2792 | 24 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 2818 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 2822-2881)

| line | len | member | says |
|---:|---:|---|---|
| 2825 | 1 | `public double fundSharesValue()` | Its shares, both books, at the exchange's price. |
| 2827 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 2829 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 2835 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 2837 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 2839 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes. |
| 2843 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 2845 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 2849 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 2854 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands: TreasuryFund.transferOn(fundValue()). |
| 2857 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the exchange's price: the Fund page's line for it. |
| 2861 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 2865 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 2867 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 2872 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 2876 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 2882-2991)

| line | len | member | says |
|---:|---:|---|---|
| 2885 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 2886 | 7 | `public void setFundDial(double dial)` |  |
| 2895 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 2896 | 8 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 2914 | 14 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, month by month), a month's worth. |
| 2930 | 9 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 2945 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 2973 | 18 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 2992-3052)

| line | len | member | says |
|---:|---:|---|---|
| 2995 | 10 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 3007 | 10 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 3019 | 8 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 3029 | 8 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 3039 | 5 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 3046 | 6 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |

### an Insane founding (lines 3053-3165)

| line | len | member | says |
|---:|---:|---|---|
| 3069 | 6 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar term loan, LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.landBondUsd() - STARTI... |
| 3107 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 3130 | 10 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 3150 | 9 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 3161 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 3166-3364)

| line | len | member | says |
|---:|---:|---|---|
| 3185 | 76 | `private void runRetirement()` |  |
| 3269 | 15 | `private void closeBranches(int wanted)` | Closes branches (0.7.19: as many as the rule says, at once; one at a time from 0.7.11 round 2 until then): the buildings retired by the path any retired building takes (retire()), sold by their owner, retail. |
| 3287 | 1 | `public int getBranchesClosed()` |  |
| 3300 | 64 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 3365-3414)

| line | len | member | says |
|---:|---:|---|---|
| 3385 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 3397 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 3406 | 1 | `public int getConstructionShedMonth()` |  |
| 3407 | 1 | `public double getConstructionShedPoints()` |  |
| 3410 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 3415-3742)

| line | len | member | says |
|---:|---:|---|---|
| 3435 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 3448 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 3468 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 3476 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 3483 | 3 | `public double getLastWriteOff()` |  |
| 3522 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 3532 | 210 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 3743-3899)

| line | len | member | says |
|---:|---:|---|---|
| 3782 | 5 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 3796 | 57 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, scanning down from what the planner asked for as consider() does - and the advisor's ... |
| 3862 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 3866 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 3879 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 3900-3977)

| line | len | member | says |
|---:|---:|---|---|
| 3936 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 3944 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 3949 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 3954 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 3957 | 20 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 3978-4675)

| line | len | member | says |
|---:|---:|---|---|
| 3991 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 4008 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 4030 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 4035 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 4138 | 1 | `public double getBankedClearedAtLoad()` |  |
| 4141 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 4163 | 29 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 4201 | 5 | `public int getConstructionOutputAtEveryPost()` | What the builders would do this month with every post offered, at the city's fill (0.7.17): the figure getConstructionOutput() was before the builders laid idle crews off. |
| 4208 | 4 | `public int getBuildingOutputAtEveryPost()` | ...and what that leaves for the sites after the repairs: getBuildingOutput() with every post offered. |
| 4222 | 9 | `void strikeBuildersCrews()` | The builders strike the month's crews (0.7.17): the work ahead - the repairs of the city standing now and every point still owed on site after this month's advance - against what their depots and the city's works depa... |
| 4243 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 4260 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 4297 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 4316 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 4347 | 74 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 4431 | 1 | `public double getCityMaintenancePaid()` |  |
| 4436 | 4 | `public int getConstructionMaterials()` |  |
| 4440 | 4 | `public double getInterestRate()` |  |
| 4444 | 3 | `public boolean isGraphsEnabled()` |  |
| 4447 | 3 | `public boolean isReportsEnabled()` |  |
| 4474 | 85 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 4561 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 4590 | 1 | `public boolean hasNewReceipt()` |  |
| 4591 | 1 | `public void clearReceipt()` |  |
| 4593 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 4607 | 68 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 4643 | 25 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 4670 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 4673 | 1 | `public double unitsBeyondYard()` _(in Game.BuildQuote)_ | The material units the crews will draw beyond the yard, which the allowance was priced on. |

### THE BUILDERS' PRICE (0.7.19) (lines 4676-4965)

| line | len | member | says |
|---:|---:|---|---|
| 4732 | 5 | `private String ownerOfOrder(BuildingsTemplate t)` | Who ordered a building of this kind, for a save that did not say (OLD CONTRACTS): its sector; retail for the bank's branch, as it pays for them (BusinessInvestment.planBank()); the city for the rest. |
| 4746 | 6 | `private double recoveredOnOldContract(String payer, BuildingsTemplate t)` | ...and the share of the tax on it that owner gets back, for the same save (revised 0.7.19): on the tax a building of it carries at today's price (EconomyManager.taxRecoveredShare()). |
| 4765 | 18 | `private double depotWageBillPerPoint(boolean today)` | A Construction Depot's posts - the builders' own job mix - at today's wages or at the founding ladder (PayTier, in today's unit), over the points it makes. |
| 4785 | 3 | `private double buildersSalesRate()` | The builders' sales tax rate, and the materials plant's: the two the quote is grossed up and credited at. |
| 4788 | 3 | `private double plantSalesRate()` |  |
| 4799 | 5 | `private double billableMaterial(double salvageCost, double plantCost, double importCost)` | What a draw of material cost the builders, as they bill it on (0.7.19): the salvage at what they paid for it, the plant's net of the credit they claim on it, the world's at its landed cost (its tax is charged and cred... |
| 4815 | 22 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 4847 | 4 | `public double quoteMonths(BuildingsTemplate template, int quantity)` | Months an order would take to finish (0.7.17): the wait every planner reads for its lead time and order size - BuildingManager.waitFor(), the order's points over the share of the site output it would get beside everyt... |
| 4860 | 5 | `public double onSiteMonths(BuildingsTemplate template)` | Months what is on site of one building would take to finish (0.7.20): the same wait at the same output as quoteMonths(), with no order added - BuildingManager.waitOnSite(). |
| 4877 | 4 | `public double siteMonths(String key)` | ONE WAIT FOR A SITE (0.7.22, after the docs pass): months a site on site would take at today's queue - a stack's by its key, or a demolition's - at the output the quote reads. |
| 4890 | 4 | `public double quoteCityMonths(BuildingsTemplate template, int quantity)` | ...and what a city order of n of these would wait if placed now (0.7.22, after the docs pass): quoteMonths() exactly with no order set and no rush on its site; otherwise where the order would land in the player's orde... |
| 4920 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 4948 | 9 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |

### MATERIAL AT THE PRICE WHEN IT IS USED (0.7.19) (lines 4966-5059)

| line | len | member | says |
|---:|---:|---|---|
| 4996 | 31 | `public void settleSiteContracts(java.util.List<BuildingsStacks.Due> dues)` | Settles the month's site work with the owners who ordered it. |
| 5033 | 7 | `private double deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 5056 | 3 | `public void recogniseSiteWork(double earned, double pointsBuilt, double pointsAvailable)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |

### THE PLAYER'S HAND ON THE QUEUE (0.7.22) (lines 5060-5461)

| line | len | member | says |
|---:|---:|---|---|
| 5093 | 3 | `public boolean isCitysToDemolish(BuildingsTemplate t)` | What can be demolished as the city's own: a building nobody in the private sector owns - but not the bank's branch, which retail paid for and the bank closes by its own rule (closeBranches()). |
| 5098 | 3 | `public boolean isBuyOutable(BuildingsTemplate t)` | What the city may buy out and demolish: a building a sector owns. |
| 5107 | 7 | **type** `public record DemolitionQuote(BuildingsTemplate template, int buildings, double points, BuildQuote price, d...` | A demolition, priced and laid out BEFORE the player commits: its work, its price, what it closes, what comes back and when. |
| 5112 | 1 | `public double salvageProceeds()` _(in Game.DemolitionQuote)_ | What the builders would pay for the material today, for as much as their cash covers. |
| 5116 | 8 | **type** `public record BuyOutQuote(DemolitionQuote demolition, String sector, double buildingValue, double ground, d...` | A buy-out, priced: the compensation, part by part, and the demolition after it. |
| 5120 | 1 | `public double compensation()` _(in Game.BuyOutQuote)_ | What the owner is paid. |
| 5122 | 1 | `public double total()` _(in Game.BuyOutQuote)_ | ...and what it all costs the city, the demolition included. |
| 5135 | 8 | `private BuildQuote demolitionPrice(BuildingsTemplate t, double workPoints, double points, double months)` | D. |
| 5145 | 27 | `public DemolitionQuote quoteDemolition(BuildingsTemplate t, int n)` | What demolishing n standing buildings of this kind would do and cost. |
| 5174 | 13 | `public DemolitionQuote quoteShellDemolition(ConstructionControl.Shell shell)` | A shell's demolition: DEMOLITION_SHARE of the work it holds, the material it drew to salvage, its ground. |
| 5189 | 4 | `private double demolitionMonths(BuildingsTemplate t, int n, double points)` | Months a demolition of these points would take at today's queue, at the output the quote reads. |
| 5202 | 15 | `public BuyOutQuote quoteBuyOut(BuildingsTemplate t, int n)` | E. |
| 5220 | 1 | `public String getLastHandRefusal()` |  |
| 5223 | 13 | `public boolean demolish(BuildingsTemplate t, int n)` | D. |
| 5238 | 36 | `public boolean buyOutAndDemolish(BuildingsTemplate t, int n)` | E. |
| 5276 | 4 | `public int demolishable(BuildingsTemplate t)` | How many of this kind stand that no demolition has been ordered for. |
| 5282 | 9 | `private void placeDemolition(DemolitionQuote q, String from, double groundPaid)` | The demolition order every path shares: paid as a building order is, and the site put up, its buildings to close as the month starts (closeDemolished()). |
| 5302 | 11 | `private void closeDemolished()` | D. |
| 5315 | 17 | `public boolean demolishShell(int templateId)` | C. |
| 5340 | 14 | `public BuildQuote quoteRestart(ConstructionControl.Shell shell)` | C. |
| 5356 | 4 | `private double restartMonths(BuildingsTemplate t, int n, double pointsLeft)` | A restart's wait, by the one wait (BuildingManager.restartWait()). |
| 5362 | 21 | `public boolean restartShell(int templateId)` | C. |
| 5385 | 11 | `public boolean cancelSite(String key, boolean cancel)` | C. |
| 5404 | 8 | `public double[] cancelRefundNow(String key)` | C. |
| 5414 | 10 | `public boolean rushSite(String key, boolean on)` | B. |
| 5426 | 11 | `public boolean moveSite(String key, int by)` | A. |
| 5439 | 5 | `public void resetSiteOrder()` | A. |
| 5452 | 9 | `public double[] rushPreview(String key)` | B. |

### in the month (lines 5462-5684)

| line | len | member | says |
|---:|---:|---|---|
| 5471 | 1 | `public ConstructionControl.Events getControlThisMonth()` |  |
| 5472 | 1 | `public double getOvertimePaidThisMonth()` |  |
| 5473 | 1 | `public double getDemolitionSalvageThisMonth()` |  |
| 5480 | 64 | `public void settleConstructionControl(ConstructionControl.Events ev)` | Settles the month's events of THE PLAYER'S HAND ON THE QUEUE. |
| 5546 | 14 | `private double[] overtimeWagesByType(double premium)` | The overtime's wages by job type: the premium in the mix of a Construction Depot's posts at today's wages - the bill the premium was struck on (depotWageBillPerPoint()). |
| 5580 | 100 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 5683 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 5685-6320)

| line | len | member | says |
|---:|---:|---|---|
| 5700 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 5712 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 5725 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 5745 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 5762 | 60 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 5832 | 1 | `public Investor getGovernmentInvestor()` |  |
| 5835 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 5837 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 5877 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 5886 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |
| 5891 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt (the build screen's showed it until 0.7.20; NewGameCheck reads it). |
| 5894 | 3 | `public double getTotalBuildingCost()` |  |
| 5897 | 3 | `public String getBuildingName()` |  |
| 5900 | 3 | `public int getBuildQuantity()` |  |
| 5905 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 5918 | 111 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 6037 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 6049 | 13 | `private String issueNote(DebtQuote quote)` | The booking every note shares - handleTBillLogic()'s, and since 0.7.13 the rollover's, which books the quote it sized (issueForRollover()). |
| 6069 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 6107 | 17 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 6167 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 6179 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 6200 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 6229 | 35 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 6270 | 5 | `private double longBondNetProceeds(double faceValue, double marketRate, int duration)` | What the city actually banks for a long bond of this face: its worth at the rate struck, less the fees, to the cent. |
| 6277 | 13 | `private DebtQuote longBondQuote(double requested, int duration, double marketRate, double before, double faceValue, double rece...` | A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. |
| 6292 | 7 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |
| 6304 | 16 | `private String issueLongBond(DebtQuote quote)` | The booking both long-bond issues share. |

### A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) (lines 6321-6404)

| line | len | member | says |
|---:|---:|---|---|
| 6349 | 8 | `private double longBondFaceForProceeds(double cashNeeded, int duration, double rounding, double marketRate)` | Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. |
| 6366 | 29 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a term bond whose CASH covers cashNeeded would cost. |
| 6397 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 6405-6724)

| line | len | member | says |
|---:|---:|---|---|
| 6447 | 14 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 6485 | 11 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 6503 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation, less the world's base rate less the world's realised inflation - the one definition the month hands the currenc... |
| 6515 | 3 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the year's inflation, the same PriceIndex.inflation() the real rate differential and the parity read - the one definition the month strikes the ho... |
| 6527 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 6532 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 6535 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 6542 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 6609 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 6629 | 78 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 6714 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |

### THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 6725-6799)

| line | len | member | says |
|---:|---:|---|---|
| 6746 | 12 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | A serial bond whose CASH covers cashNeeded: quoteMediumBond() at the face that brings it. |
| 6764 | 14 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note, serial or term loan whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 6780 | 8 | `private double foreignProceedsPerFace(String type, double rate, int duration)` | What a unit of a dollar instrument's face banks at this rate, net of the spread - never under MIN_PROCEEDS_PER_FACE, so the search above always moves. |
| 6790 | 9 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 6800-8212)

| line | len | member | says |
|---:|---:|---|---|
| 6877 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 6880 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 6883 | 11 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 6901 | 8 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 6911 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not, and whether it is dollar paper rolled at home. |
| 6913 | 13 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 6928 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 6942 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 7013 | 54 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 7076 | 9 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; a serial by quoteMediumBondForCash(); a term loan by quoteLo... |
| 7087 | 4 | `private double localFace(boolean abroad, DebtQuote quote)` | A rollover quote's face in local money: a dollar quote's at the day's rate. |
| 7098 | 18 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 7119 | 3 | `private void printPopulationInfo()` | printers |
| 7124 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 7137 | 3 | `private void printUtilityInfo()` |  |
| 7141 | 3 | `private void printCityStats()` |  |
| 7148 | 776 | `private void nextMonth()` |  |
| 7925 | 259 | `private void startOfMonthUpdate()` |  |
| 8184 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 8213-8345)

| line | len | member | says |
|---:|---:|---|---|
| 8262 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 8287 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 8299 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 8319 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 8341 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 8346-8596)

| line | len | member | says |
|---:|---:|---|---|
| 8360 | 16 | `public String getCreditRating()` |  |
| 8378 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 8401 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 8415 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 8437 | 27 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 8465 | 78 | `private void finalUpdateEconomy()` |  |
| 8545 | 4 | `private void updateConstructionCost()` |  |
| 8556 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 8567 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 8571 | 4 | `public int getHouseholdCapacity()` |  |
| 8575 | 4 | `public int getStoreCapacity()` |  |
| 8579 | 3 | `public int[] getJobs()` |  |
| 8584 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 8597-9182)

| line | len | member | says |
|---:|---:|---|---|
| 8626 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 8662 | 1 | `public PopulationCohorts getCohorts()` |  |
| 8663 | 1 | `public FamilyModel getFamilies()` |  |
| 8664 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 8665 | 1 | `public Migration getMigration()` |  |
| 8666 | 1 | `public Health getHealth()` |  |
| 8667 | 1 | `public Healthcare getHealthcare()` |  |
| 8691 | 491 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 9183-9195)

| line | len | member | says |
|---:|---:|---|---|
| 9191 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 9194 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 9196-9484)

| line | len | member | says |
|---:|---:|---|---|
| 9212 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 9231 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 9251 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 9293 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 9319 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 9329 | 3 | `public BuildLog getBuildLog()` |  |
| 9336 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 9361 | 1 | `public Unemployment getUnemployment()` |  |
| 9365 | 1 | `public Sickness getSickness()` |  |
| 9373 | 1 | `public Crime getCrime()` |  |
| 9377 | 1 | `public double getLastOrphanDeaths()` |  |
| 9378 | 1 | `public double getLastUnhousedDeaths()` |  |
| 9380 | 6 | `{ ... }` |  |
| 9388 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 9413 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 9422 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 9443 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 9454 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 9467 | 1 | `public double getStudentLoansLent()` |  |
| 9468 | 1 | `public double getStudentLoansRepaid()` |  |
| 9469 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 9473 | 1 | `public double getStudentLoanInterest()` |  |
| 9476 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 9483 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 9485-9617)

| line | len | member | says |
|---:|---:|---|---|
| 9500 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 9506 | 5 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 9513 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 9525 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 9569 | 1 | `public Equity getEquity()` |  |
| 9573 | 1 | `public Exchange getExchange()` |  |
| 9581 | 1 | `public BondMarket getBondMarket()` |  |
| 9591 | 1 | `public TreasuryFund getFund()` |  |
| 9607 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 9618-9641)

| line | len | member | says |
|---:|---:|---|---|
| 9628 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 9637 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 9640 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 9642-9937)

| line | len | member | says |
|---:|---:|---|---|
| 9654 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 9656 | 1 | `public PriceIndex getPriceIndex()` |  |
| 9658 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 9665 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 9672 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 9681 | 1 | `public Bank getBank()` |  |
| 9694 | 1 | `public CentralBank getCentralBank()` |  |
| 9708 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 9714 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 9717 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 9724 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 9728 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 9732 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 9799 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 9803 | 3 | `public EconomyManager getEconomyManager()` |  |
| 9806 | 3 | `public PopulationManager getPopulationManager()` |  |
| 9810 | 1 | `public LabourMarket getLabourMarket()` |  |
| 9811 | 1 | `public Education getEducation()` |  |
| 9827 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 9852 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 9894 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 9912 | 23 | `public void recordMonth()` | Files this month in the graph history. |
| 9936 | 1 | `public Inbox getInbox()` |  |
| 9937 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 9938-10351)

| line | len | member | says |
|---:|---:|---|---|
| 9949 | 3 | `public int getMonthsUntilAutosave()` |  |
| 9965 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 9977 | 257 | `public void save(int slot, String slotName)` |  |
| 10243 | 5 | `public String takeSkipFailure()` |  |
| 10252 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 10254 | 1 | `public GameFiles getGameFiles()` |  |
| 10273 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 10291 | 18 | `public void sendBuildingSave()` |  |
| 10312 | 16 | `public void loadBuildings()` |  |
| 10342 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 10352-10492)

| line | len | member | says |
|---:|---:|---|---|
| 10379 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 10380 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 10381 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 10388 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 10410 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 10477 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 10486 | 1 | `public double getCityPaperSettled()` |  |
| 10487 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 10488 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 10490 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 10493-10568)

| line | len | member | says |
|---:|---:|---|---|
| 10521 | 1 | `public double getCouponsToHouseholds()` |  |
| 10522 | 1 | `public double getPrincipalToHouseholds()` |  |
| 10523 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 10534 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 10542 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 10555 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 10569-10594)

| line | len | member | says |
|---:|---:|---|---|
| 10579 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 10595-10651)

| line | len | member | says |
|---:|---:|---|---|
| 10612 | 39 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 10652-10798)

| line | len | member | says |
|---:|---:|---|---|
| 10706 | 56 | `private void openMarketOperation()` |  |
| 10770 | 28 | `private void buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 10799-11034)

| line | len | member | says |
|---:|---:|---|---|
| 10904 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 10907 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 10914 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 10926 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 10933 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 10939 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 10952 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 10983 | 51 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 11035-11056)

| line | len | member | says |
|---:|---:|---|---|
| 11050 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 11054 | 1 | `public double getBuybackToHouseholds()` |  |
| 11055 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 11057-11261)

| line | len | member | says |
|---:|---:|---|---|
| 11128 | 13 | `private void takeTreasuryMonth()` |  |
| 11143 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 11145 | 1 | `public double getTreasuryOpening()` |  |
| 11146 | 1 | `public double getTreasuryClosing()` |  |
| 11147 | 1 | `public double getTreasuryRaised()` |  |
| 11148 | 1 | `public double getTreasuryRepaid()` |  |
| 11149 | 1 | `public double getTreasurySurplus()` |  |
| 11152 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 11170 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 11182 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 11187 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 11195 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |
| 11199 | 5 | `double[] treasuryMonthToSave()` |  |
| 11205 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 11239 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 11242 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 11244 | 1 | `public double getPostAuditDriftWorst()` |  |
| 11245 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 11246 | 4 | `public void InterestExpense(double amount)` |  |
| 11251 | 3 | `public DebtManager getDebtManager()` |  |
| 11256 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 11262-11515)

| line | len | member | says |
|---:|---:|---|---|
| 11322 | 37 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 11371 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 11376 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 11398 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 11410 | 17 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 11436 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 11461 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 11468 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 11472 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 11482 | 8 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 11492 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 11495 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 11502 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 11511 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 11512 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 11513 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 11514 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 11516-11642)

| line | len | member | says |
|---:|---:|---|---|
| 11529 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 11537 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 11562 | 72 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 11643-13316)

| line | len | member | says |
|---:|---:|---|---|
| 11711 | 20 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 11732 | 296 | `private void rebuildSimulationState()` |  |
| 12070 | 10 | `public void loadGame(int slot)` | Load game |
| 12082 | 1188 | `private void readTheSave(int slot)` | The load itself; see loadGame(). |
| 13275 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 13317-13550)

| line | len | member | says |
|---:|---:|---|---|
| 13323 | 1 | `public Denomination getDenomination()` |  |
| 13326 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 13357 | 170 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 13538 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

