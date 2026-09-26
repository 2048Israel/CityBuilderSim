# Game.java - 10,834 lines · 376 methods · 14 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [TreasuryLine](TreasuryLine.md) (42), [Equity](Equity.md) (38), [AgeBand](AgeBand.md) (36), [BusinessDebtManager](BusinessDebtManager.md) (32), [GameFiles](GameFiles.md) (24), [DebtQuote](DebtQuote.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (23), [CareType](CareType.md) (20), [Debt](Debt.md) (19), [GameLog](GameLog.md) (17), [Bank](Bank.md) (17), [FamilyModel](FamilyModel.md) (16), [DebtManager](DebtManager.md) (14), [Founding](Founding.md) (14), [HouseholdAccounts](HouseholdAccounts.md) (14), [Sector](Sector.md) (14), [Investor](Investor.md) (14), [BuildingType](BuildingType.md) (14), [JobType](JobType.md) (13), [TaxPolicy](TaxPolicy.md) (12), [LongTermBond](LongTermBond.md) (11), [Healthcare](Healthcare.md) (10), [BusinessInvestment](BusinessInvestment.md) (10), [PayTier](PayTier.md) (10), [Household](Household.md) (9), [BuildingManager](BuildingManager.md) (8), [SectorBooks](SectorBooks.md) (8), [LandManager](LandManager.md) (7), [Mortgage](Mortgage.md) (7), [BondMarket](BondMarket.md) (7)... and 61 more

**Used by (100):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 720 | THE FOUNDING RESERVE (2026-09-21) |
| 776 | · THE FOUNDING RECORD (0.7.10) |
| 829 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 852 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1056 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1199 | PRIVATE INVESTMENT |
| 1517 | · AND THE BALANCE SHEET |
| 1709 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1760 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1794 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 2359 | SHRINKING |
| 2549 | THE CONSTRUCTION WARNING |
| 2599 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 2927 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 3084 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 3162 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 3982 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 4596 | · A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) |
| 4680 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 5055 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 5302 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 5499 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 5679 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 6012 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 6145 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 6396 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 6977 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 6990 | THE READINGS THE MONTH TAKES OF THE CITY |
| 7279 | · the price of a place (2026-09-21) |
| 7402 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 7426 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 7722 | the save system |
| 8092 | PAYING THE WORLD BACK |
| 8233 | THE HOLDERS ARE PAID (0.7.1) |
| 8309 | · the desk, for the households |
| 8335 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 8388 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 8450 | · a buyback's holders outside the pools |
| 8472 | WHAT THE TREASURY ACTUALLY DID |
| 8677 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 8919 | BUYING YOUR OWN DEBT BACK |
| 9044 | WHY LAND IS NOT IN THE RENT FLOOR |
| 9389 | · · the three the monthly path sets and this did not |
| 10608 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3980 | `Game.BuildResult.SUCCESS` |  |
| 3980 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 3980 | `Game.BuildResult.NO_LAND` |  |
| 3980 | `Game.BuildResult.NO_DEPOSIT` |  |
| 3980 | `Game.BuildResult.NO_LICENCE` |  |
| 3980 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 495 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 768 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 771 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 774 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 3994 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 4736 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 4891 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 6028 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 6040 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 6043 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 6058 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 6067 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, WHAT AN ISSUE COSTS). |
| 6075 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 7729 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 62 | `private final GameFiles gameFiles` | Where saves live and how they are written. |
| 63 | `private HistoryGrapher historyGrapher` |  |
| 64 | `private DebtManager debtManager` |  |
| 66 | `private SimulationEngine simulationEngine` |  |
| 68 | `int materialsConsumed` |  |
| 72 | `double receiptMaterials` | The last order's material, in units - drawn by the crews as they build. |
| 73 | `double totalBuildingCost` |  |
| 74 | `private boolean hasNewReceipt` |  |
| 75 | `String lastBuildingName` |  |
| 84 | `int lastBuildQuantity` | How MANY of them, so the receipt can say "3 x Walk-in Clinic". |
| 98 | `private int receiptSerial` | WHICH receipt this is, counted up forever. |
| 124 | `boolean reports` | Console output per month: seven sector income statements and three 12x60 ASCII graphs. |
| 125 | `boolean graphs` |  |
| 128 | `boolean initialized` | boolean |
| 690 | `private String loadFailure` | Why the last load did not happen, or null. |
| 788 | `private Founding founding` |  |
| 818 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 887 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 888 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1094 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1178 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under the toggle, and a short vault says here that the rest was converted. |
| 1179 | `private int lastLandReceiptMonth` |  |
| 1727 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1730 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1765 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 1999 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 2470 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 2557 | `private int constructionShedMonth` |  |
| 2558 | `private double constructionShedPoints` |  |
| 2617 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 2630 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 2650 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 2665 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 3043 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 3044 | `private final java.util.Set<String> heldForDownPayment` |  |
| 3123 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 3124 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 3125 | `private double salvageUsedThisMonth` |  |
| 3182 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 3547 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 3552 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 3721 | `public final int quantity` |  |
| 3722 | `public final double sticker` |  |
| 3723 | `public final double materialsNeeded` |  |
| 3725 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 3727 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 3728 | `public final double plantPrice` |  |
| 3729 | `public final double plantCost` |  |
| 3730 | `public final double materialsImported` |  |
| 3731 | `public final double materialsPrice` |  |
| 3732 | `public final double importCost` |  |
| 3733 | `public final double total` |  |
| 3734 | `public final double landNeeded` |  |
| 3735 | `public final double landFree` |  |
| 3737 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 4113 | `private final Investor government` | The city itself, as a payer. |
| 6389 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 6390 | `private LandManager landManager` |  |
| 6393 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 6394 | `private BuildLog buildLog` |  |
| 6421 | `private PopulationCohorts cohorts` |  |
| 6422 | `private FamilyModel families` |  |
| 6430 | `private Migration migration` |  |
| 6436 | `private LabourMarket labourMarket` | What labour costs. |
| 6442 | `private Education education` | The schools. |
| 6451 | `private Health health` | How much of the workforce is off sick. |
| 6459 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 6982 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 7128 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 7135 | `private HouseholdAccounts households` | The residents' own books. |
| 7144 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 7154 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 7158 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 7166 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 7170 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 7260 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 7266 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 7339 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 7349 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 7355 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 7362 | `private final Equity equity` | Who owns the city's companies. |
| 7366 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 7374 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 7378 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 7400 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 7408 | `private final Consumption consumption` |  |
| 7409 | `private boolean consumptionLoaded` |  |
| 7418 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 7436 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 7445 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 7446 | `private int rateHistoryFilled` |  |
| 7464 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 7477 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 7521 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 7534 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 7548 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 7566 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 7569 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 7571 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 7572 | `private double carriedRetailCapacity` |  |
| 7573 | `private double carriedRetailWant` |  |
| 7576 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 7577 | `private java.util.Map<String, String> lastInvestment` |  |
| 7580 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 7624 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 7731 | `private int monthsSinceAutosave` |  |
| 7997 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 8006 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 8115 | `private double foreignDebtRaisedThisMonth` |  |
| 8116 | `private double foreignPrincipalRepaidThisMonth` |  |
| 8117 | `private double foreignInterestPaidThisMonth` |  |
| 8169 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 8189 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 8190 | `private double cityPrincipalRepaidThisMonth` |  |
| 8204 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 8205 | `private double cityDiscountForBank` |  |
| 8208 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 8225 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 8231 | `private double bankPrincipalRepaidThisMonth` |  |
| 8259 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 8350 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 8460 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 8462 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 8514 | `private double treasuryOpening` |  |
| 8515 | `private double treasuryClosing` |  |
| 8516 | `private double treasuryRaised` |  |
| 8517 | `private double treasuryRepaid` |  |
| 8518 | `private double treasurySurplus` |  |
| 8519 | `private boolean treasuryRecorded` |  |
| 8538 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 8541 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 8638 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 8641 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 8649 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 8650 | `private String postAuditDriftPool` |  |
| 8651 | `private double postAuditDriftWorst` |  |
| 8715 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 8718 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 8721 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 9042 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 10563 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 10612 | `private final Denomination denomination` |  |
| 10827 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 10810 | **type** `public class Game` |  |
| 130 | 3 | `public Game()` |  |
| 142 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 151 | 4 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 157 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 189 | 261 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 451 | 4 | `public void run()` |  |
| 456 | 38 | `private void initialize()` |  |
| 497 | 4 | `static { ... }` |  |
| 505 | 3 | `public void newGame()` | A new city on the defaults: "Found with defaults". |
| 514 | 14 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 558 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 588 | 8 | `public void resumeGame()` |  |
| 596 | 54 | `public void loadGameSave(int slot)` |  |
| 659 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 667 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 680 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 682 | 6 | `public void toggleQuit()` |  |
| 692 | 1 | `public String getLoadFailure()` |  |
| 694 | 7 | `public void toggleGraphs()` |  |
| 701 | 5 | `public void toggleReports()` |  |
| 707 | 3 | `public void toggleNextMonth()` |  |
| 712 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 714 | 3 | `public int getMonth()` |  |
| 717 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 720-775)

### THE FOUNDING RECORD (0.7.10) (lines 776-828)

| line | len | member | says |
|---:|---:|---|---|
| 791 | 4 | `public Founding getFounding()` | How this city was founded. |
| 797 | 1 | `public String getCityName()` | The city's name. |
| 800 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 803 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 806 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 809 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 825 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset. |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 829-851)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 852-1055)

| line | len | member | says |
|---:|---:|---|---|
| 890 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 891 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 893 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 894 | 1 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 897 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 898 | 1 | `public double getSubsidyPaid(String key)` |  |
| 900 | 5 | `public double getTotalSubsidyPaid()` |  |
| 907 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 920 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 923 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 942 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 952 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 987 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 1004 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 1010 | 4 | `public double getIncome()` |  |
| 1015 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1019 | 3 | `public double getWaterRatio()` |  |
| 1023 | 3 | `public double getRoadRatio()` |  |
| 1028 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1033 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1038 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1042 | 3 | `public LandManager getLandManager()` |  |
| 1047 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1056-1198)

| line | len | member | says |
|---:|---:|---|---|
| 1097 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1100 | 1 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1110 | 42 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1160 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1167 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - for the land office's buttons. |
| 1182 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1187 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 1191 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1195 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 1199-1708)

| line | len | member | says |
|---:|---:|---|---|
| 1209 | 91 | `private void runPrivateInvestment()` |  |
| 1316 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 1329 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 1341 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 1347 | 360 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1709-1759)

| line | len | member | says |
|---:|---:|---|---|
| 1733 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1736 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1739 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1740 | 1 | `public double getHouseholdCarImports()` |  |
| 1743 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1746 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1749 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1752 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1755 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1758 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1760-1793)

| line | len | member | says |
|---:|---:|---|---|
| 1768 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1771 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1774 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1777 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1780 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1783 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1786 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1789 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1792 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1794-2358)

| line | len | member | says |
|---:|---:|---|---|
| 1812 | 10 | `private void capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 1829 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 1842 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 1859 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 1867 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 1895 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 1927 | 43 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 1978 | 14 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 2053 | 84 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 2150 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 2163 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 2190 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 2207 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 2234 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 2263 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |
| 2290 | 9 | `public double recapitaliseBank(double amount)` | The treasury puts capital into its bank. |
| 2301 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now. |
| 2311 | 4 | `public boolean canRecapitaliseBank()` | True when the bank needs capital and the treasury holds all of it: the rescue button's guard (0.7.9), here so the screen and anything else that offers the rescue cannot disagree about it. |
| 2325 | 9 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 2344 | 8 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 2354 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 2359-2548)

| line | len | member | says |
|---:|---:|---|---|
| 2378 | 69 | `private void runRetirement()` |  |
| 2454 | 14 | `private void closeBranch()` | Closes one of the bank's branches (0.7.11, round 2): the building retired by the path any retired building takes (retire()), sold by its owner, retail. |
| 2471 | 1 | `public int getBranchesClosed()` |  |
| 2484 | 64 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 2549-2598)

| line | len | member | says |
|---:|---:|---|---|
| 2569 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 2581 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 2590 | 1 | `public int getConstructionShedMonth()` |  |
| 2591 | 1 | `public double getConstructionShedPoints()` |  |
| 2594 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 2599-2926)

| line | len | member | says |
|---:|---:|---|---|
| 2619 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 2632 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 2652 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 2660 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 2667 | 3 | `public double getLastWriteOff()` |  |
| 2706 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 2716 | 210 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 2927-3083)

| line | len | member | says |
|---:|---:|---|---|
| 2966 | 5 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 2980 | 57 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, scanning down from what the planner asked for as consider() does - and the advisor's ... |
| 3046 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 3050 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 3063 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 3084-3161)

| line | len | member | says |
|---:|---:|---|---|
| 3120 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 3128 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 3133 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 3138 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 3141 | 20 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 3162-3981)

| line | len | member | says |
|---:|---:|---|---|
| 3175 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 3192 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 3214 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 3219 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 3315 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 3335 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 3364 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 3381 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 3418 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 3437 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 3468 | 71 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 3549 | 1 | `public double getCityMaintenancePaid()` |  |
| 3554 | 4 | `public int getConstructionMaterials()` |  |
| 3558 | 4 | `public double getInterestRate()` |  |
| 3562 | 3 | `public boolean isGraphsEnabled()` |  |
| 3565 | 3 | `public boolean isReportsEnabled()` |  |
| 3584 | 88 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early if the treasury is empty. |
| 3674 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 3703 | 1 | `public boolean hasNewReceipt()` |  |
| 3704 | 1 | `public void clearReceipt()` |  |
| 3706 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 3720 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 3739 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 3759 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 3769 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 3811 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 3839 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 3848 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 3863 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 3886 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 3980 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 3982-4595)

| line | len | member | says |
|---:|---:|---|---|
| 3997 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 4009 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 4022 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 4042 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 4059 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 4121 | 1 | `public Investor getGovernmentInvestor()` |  |
| 4124 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 4126 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 4166 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 4175 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |
| 4180 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 4183 | 3 | `public double getTotalBuildingCost()` |  |
| 4186 | 3 | `public String getBuildingName()` |  |
| 4189 | 3 | `public int getBuildQuantity()` |  |
| 4194 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 4207 | 111 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 4326 | 15 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 4348 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 4386 | 15 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 4444 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 4456 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 4477 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 4506 | 35 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 4547 | 5 | `private double longBondNetProceeds(double faceValue, double marketRate, int duration)` | What the city actually banks for a long bond of this face: its worth at the rate struck, less the fees, to the cent. |
| 4554 | 13 | `private DebtQuote longBondQuote(double requested, int duration, double marketRate, double before, double faceValue, double rece...` | A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. |
| 4569 | 7 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |
| 4581 | 14 | `private String issueLongBond(DebtQuote quote)` | The booking both long-bond issues share. |

### A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) (lines 4596-4679)

| line | len | member | says |
|---:|---:|---|---|
| 4624 | 8 | `private double longBondFaceForProceeds(double cashNeeded, int duration, double rounding, double marketRate)` | Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. |
| 4641 | 29 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a term bond whose CASH covers cashNeeded would cost. |
| 4672 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 4680-6011)

| line | len | member | says |
|---:|---:|---|---|
| 4722 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 4758 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 4773 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation, less the world's base rate less the world's realised inflation - the one definition the month hands the currenc... |
| 4785 | 3 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the year's inflation, the same PriceIndex.inflation() the real rate differential and the parity read - the one definition the month strikes the ho... |
| 4797 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 4802 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 4805 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 4812 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 4879 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 4899 | 70 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 4976 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 4989 | 3 | `private void printPopulationInfo()` | printers |
| 4994 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 5007 | 3 | `private void printUtilityInfo()` |  |
| 5011 | 3 | `private void printCityStats()` |  |
| 5018 | 717 | `private void nextMonth()` |  |
| 5736 | 247 | `private void startOfMonthUpdate()` |  |
| 5983 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 6012-6144)

| line | len | member | says |
|---:|---:|---|---|
| 6061 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 6086 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 6098 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 6118 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 6140 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 6145-6395)

| line | len | member | says |
|---:|---:|---|---|
| 6159 | 16 | `public String getCreditRating()` |  |
| 6177 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 6200 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 6214 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 6236 | 27 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 6264 | 78 | `private void finalUpdateEconomy()` |  |
| 6344 | 4 | `private void updateConstructionCost()` |  |
| 6355 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 6366 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 6370 | 4 | `public int getHouseholdCapacity()` |  |
| 6374 | 4 | `public int getStoreCapacity()` |  |
| 6378 | 3 | `public int[] getJobs()` |  |
| 6383 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 6396-6976)

| line | len | member | says |
|---:|---:|---|---|
| 6425 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 6461 | 1 | `public PopulationCohorts getCohorts()` |  |
| 6462 | 1 | `public FamilyModel getFamilies()` |  |
| 6463 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 6464 | 1 | `public Migration getMigration()` |  |
| 6465 | 1 | `public Health getHealth()` |  |
| 6466 | 1 | `public Healthcare getHealthcare()` |  |
| 6490 | 486 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 6977-6989)

| line | len | member | says |
|---:|---:|---|---|
| 6985 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 6988 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 6990-7278)

| line | len | member | says |
|---:|---:|---|---|
| 7006 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 7025 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 7045 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 7087 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 7113 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 7123 | 3 | `public BuildLog getBuildLog()` |  |
| 7130 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 7155 | 1 | `public Unemployment getUnemployment()` |  |
| 7159 | 1 | `public Sickness getSickness()` |  |
| 7167 | 1 | `public Crime getCrime()` |  |
| 7171 | 1 | `public double getLastOrphanDeaths()` |  |
| 7172 | 1 | `public double getLastUnhousedDeaths()` |  |
| 7174 | 6 | `{ ... }` |  |
| 7182 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 7207 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 7216 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 7237 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 7248 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 7261 | 1 | `public double getStudentLoansLent()` |  |
| 7262 | 1 | `public double getStudentLoansRepaid()` |  |
| 7263 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 7267 | 1 | `public double getStudentLoanInterest()` |  |
| 7270 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 7277 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 7279-7401)

| line | len | member | says |
|---:|---:|---|---|
| 7294 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 7300 | 5 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 7307 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 7319 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 7363 | 1 | `public Equity getEquity()` |  |
| 7367 | 1 | `public Exchange getExchange()` |  |
| 7375 | 1 | `public BondMarket getBondMarket()` |  |
| 7391 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 7402-7425)

| line | len | member | says |
|---:|---:|---|---|
| 7412 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 7421 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 7424 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 7426-7721)

| line | len | member | says |
|---:|---:|---|---|
| 7438 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 7440 | 1 | `public PriceIndex getPriceIndex()` |  |
| 7442 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 7449 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 7456 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 7465 | 1 | `public Bank getBank()` |  |
| 7478 | 1 | `public CentralBank getCentralBank()` |  |
| 7492 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 7498 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 7501 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 7508 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 7512 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 7516 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 7583 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 7587 | 3 | `public EconomyManager getEconomyManager()` |  |
| 7590 | 3 | `public PopulationManager getPopulationManager()` |  |
| 7594 | 1 | `public LabourMarket getLabourMarket()` |  |
| 7595 | 1 | `public Education getEducation()` |  |
| 7611 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 7636 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 7678 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 7696 | 23 | `public void recordMonth()` | Files this month in the graph history. |
| 7720 | 1 | `public Inbox getInbox()` |  |
| 7721 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 7722-8091)

| line | len | member | says |
|---:|---:|---|---|
| 7733 | 3 | `public int getMonthsUntilAutosave()` |  |
| 7749 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 7761 | 229 | `public void save(int slot, String slotName)` |  |
| 7999 | 5 | `public String takeSkipFailure()` |  |
| 8008 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 8010 | 1 | `public GameFiles getGameFiles()` |  |
| 8023 | 7 | `public GameFiles.Result[] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade. |
| 8031 | 18 | `public void sendBuildingSave()` |  |
| 8052 | 16 | `public void loadBuildings()` |  |
| 8082 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 8092-8232)

| line | len | member | says |
|---:|---:|---|---|
| 8119 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 8120 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 8121 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 8128 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 8150 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 8217 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 8226 | 1 | `public double getCityPaperSettled()` |  |
| 8227 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 8228 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 8230 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 8233-8308)

| line | len | member | says |
|---:|---:|---|---|
| 8261 | 1 | `public double getCouponsToHouseholds()` |  |
| 8262 | 1 | `public double getPrincipalToHouseholds()` |  |
| 8263 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 8274 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 8282 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 8295 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 8309-8334)

| line | len | member | says |
|---:|---:|---|---|
| 8319 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 8335-8387)

| line | len | member | says |
|---:|---:|---|---|
| 8352 | 35 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 8388-8449)

| line | len | member | says |
|---:|---:|---|---|
| 8407 | 42 | `private void openMarketOperation()` |  |

### a buyback's holders outside the pools (lines 8450-8471)

| line | len | member | says |
|---:|---:|---|---|
| 8465 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 8469 | 1 | `public double getBuybackToHouseholds()` |  |
| 8470 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 8472-8676)

| line | len | member | says |
|---:|---:|---|---|
| 8543 | 13 | `private void takeTreasuryMonth()` |  |
| 8558 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 8560 | 1 | `public double getTreasuryOpening()` |  |
| 8561 | 1 | `public double getTreasuryClosing()` |  |
| 8562 | 1 | `public double getTreasuryRaised()` |  |
| 8563 | 1 | `public double getTreasuryRepaid()` |  |
| 8564 | 1 | `public double getTreasurySurplus()` |  |
| 8567 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 8585 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 8597 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 8602 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 8610 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |
| 8614 | 5 | `double[] treasuryMonthToSave()` |  |
| 8620 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 8654 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 8657 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 8659 | 1 | `public double getPostAuditDriftWorst()` |  |
| 8660 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 8661 | 4 | `public void InterestExpense(double amount)` |  |
| 8666 | 3 | `public DebtManager getDebtManager()` |  |
| 8671 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 8677-8918)

| line | len | member | says |
|---:|---:|---|---|
| 8737 | 26 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 8775 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 8780 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 8802 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 8814 | 17 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 8840 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 8865 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 8872 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 8876 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 8886 | 7 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 8895 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 8898 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 8905 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 8914 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 8915 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 8916 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 8917 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 8919-9043)

| line | len | member | says |
|---:|---:|---|---|
| 8932 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 8940 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 8965 | 70 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 9044-10607)

| line | len | member | says |
|---:|---:|---|---|
| 9112 | 17 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 9130 | 296 | `private void rebuildSimulationState()` |  |
| 9468 | 1093 | `public void loadGame(int slot)` | Load game |
| 10566 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 10608-10834)

| line | len | member | says |
|---:|---:|---|---|
| 10614 | 1 | `public Denomination getDenomination()` |  |
| 10617 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 10648 | 163 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 10822 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

