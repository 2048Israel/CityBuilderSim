# Game.java - 9,278 lines · 323 methods · 12 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [TreasuryLine](TreasuryLine.md) (42), [Equity](Equity.md) (36), [AgeBand](AgeBand.md) (36), [GameFiles](GameFiles.md) (23), [CareType](CareType.md) (20), [Debt](Debt.md) (19), [DebtQuote](DebtQuote.md) (19), [BuildingsTemplate](BuildingsTemplate.md) (18), [FamilyModel](FamilyModel.md) (16), [HouseholdAccounts](HouseholdAccounts.md) (14), [GameLog](GameLog.md) (14), [JobType](JobType.md) (13), [DebtManager](DebtManager.md) (13), [Sector](Sector.md) (13), [BuildingType](BuildingType.md) (13), [TaxPolicy](TaxPolicy.md) (11), [Healthcare](Healthcare.md) (10), [PayTier](PayTier.md) (10), [Bank](Bank.md) (9), [Investor](Investor.md) (9), [Household](Household.md) (8), [LongTermBond](LongTermBond.md) (8), [SectorBooks](SectorBooks.md) (7), [LandManager](LandManager.md) (7), [MediumTermBond](MediumTermBond.md) (7), [HouseholdBalance](HouseholdBalance.md) (7), [MoneyAudit](MoneyAudit.md) (7), [BuildingManager](BuildingManager.md) (6), [HistorySave](HistorySave.md) (6), [BusinessInvestment](BusinessInvestment.md) (6)... and 54 more

**Used by (93):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 617 | THE FOUNDING RESERVE (2026-09-21) |
| 651 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 674 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 901 | PRIVATE INVESTMENT |
| 1165 | · AND THE BALANCE SHEET |
| 1350 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1401 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1435 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 1728 | SHRINKING |
| 1870 | THE CONSTRUCTION WARNING |
| 1920 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 2854 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 3423 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 3797 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 4030 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 4216 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 4394 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 4702 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 4820 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 5067 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 5634 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 5647 | THE READINGS THE MONTH TAKES OF THE CITY |
| 5918 | · the price of a place (2026-09-21) |
| 6019 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 6043 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 6338 | the save system |
| 6671 | PAYING THE WORLD BACK |
| 6812 | THE HOLDERS ARE PAID (0.7.1) |
| 6888 | · the desk, for the households |
| 6914 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 6967 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 7029 | · a buyback's holders outside the pools |
| 7051 | WHAT THE TREASURY ACTUALLY DID |
| 7256 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 7498 | BUYING YOUR OWN DEBT BACK |
| 7623 | WHY LAND IS NOT IN THE RENT FLOOR |
| 7967 | · · the three the monthly path sets and this did not |
| 9057 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 2852 | `Game.BuildResult.SUCCESS` |  |
| 2852 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 2852 | `Game.BuildResult.NO_LAND` |  |
| 2852 | `Game.BuildResult.NO_DEPOSIT` |  |
| 2852 | `Game.BuildResult.NO_LICENCE` |  |
| 2852 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 406 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 643 | `Game.FOUNDING_CASH` | `2_500_000` | What the founders leave in the treasury, in thousands: D$2.5B, the endowment less the vault. |
| 646 | `Game.FOUNDING_RESERVE_USD` | `1_000_000` | What the founders leave in the vault, in thousands of US dollars: US$1B, bought on day one at the opening rate. |
| 649 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 2866 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 3479 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 3634 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 4718 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 4733 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 4742 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face. |
| 4750 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 6345 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 587 | `private String loadFailure` | Why the last load did not happen, or null. |
| 709 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 710 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1368 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1371 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1406 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 1878 | `private int constructionShedMonth` |  |
| 1879 | `private double constructionShedPoints` |  |
| 1938 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 1958 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 1973 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 2424 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 2429 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 2598 | `public final int quantity` |  |
| 2599 | `public final double sticker` |  |
| 2600 | `public final double materialsNeeded` |  |
| 2602 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 2604 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 2605 | `public final double plantPrice` |  |
| 2606 | `public final double plantCost` |  |
| 2607 | `public final double materialsImported` |  |
| 2608 | `public final double materialsPrice` |  |
| 2609 | `public final double importCost` |  |
| 2610 | `public final double total` |  |
| 2611 | `public final double landNeeded` |  |
| 2612 | `public final double landFree` |  |
| 2614 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 2985 | `private final Investor government` | The city itself, as a payer. |
| 5060 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 5061 | `private LandManager landManager` |  |
| 5064 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 5065 | `private BuildLog buildLog` |  |
| 5092 | `private PopulationCohorts cohorts` |  |
| 5093 | `private FamilyModel families` |  |
| 5101 | `private Migration migration` |  |
| 5107 | `private LabourMarket labourMarket` | What labour costs. |
| 5113 | `private Education education` | The schools. |
| 5122 | `private Health health` | How much of the workforce is off sick. |
| 5130 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 5639 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 5785 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 5792 | `private HouseholdAccounts households` | The residents' own books. |
| 5801 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 5811 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 5815 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 5823 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 5827 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 5899 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 5905 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 5978 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 5988 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 5994 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 6001 | `private final Equity equity` | Who owns the city's companies. |
| 6005 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 6017 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 6025 | `private final Consumption consumption` |  |
| 6026 | `private boolean consumptionLoaded` |  |
| 6035 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 6053 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 6062 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 6063 | `private int rateHistoryFilled` |  |
| 6081 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 6094 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 6138 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 6151 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 6165 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 6183 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 6186 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 6188 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 6189 | `private double carriedRetailCapacity` |  |
| 6190 | `private double carriedRetailWant` |  |
| 6193 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 6194 | `private java.util.Map<String, String> lastInvestment` |  |
| 6197 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 6241 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 6347 | `private int monthsSinceAutosave` |  |
| 6576 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 6585 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 6694 | `private double foreignDebtRaisedThisMonth` |  |
| 6695 | `private double foreignPrincipalRepaidThisMonth` |  |
| 6696 | `private double foreignInterestPaidThisMonth` |  |
| 6748 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 6768 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 6769 | `private double cityPrincipalRepaidThisMonth` |  |
| 6783 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 6784 | `private double cityDiscountForBank` |  |
| 6787 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 6804 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 6810 | `private double bankPrincipalRepaidThisMonth` |  |
| 6838 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 6929 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 7039 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 7041 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 7093 | `private double treasuryOpening` |  |
| 7094 | `private double treasuryClosing` |  |
| 7095 | `private double treasuryRaised` |  |
| 7096 | `private double treasuryRepaid` |  |
| 7097 | `private double treasurySurplus` |  |
| 7098 | `private boolean treasuryRecorded` |  |
| 7117 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 7120 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 7217 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 7220 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 7228 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 7229 | `private String postAuditDriftPool` |  |
| 7230 | `private double postAuditDriftWorst` |  |
| 7294 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 7297 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 7300 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 7621 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 9061 | `private final Denomination denomination` |  |
| 9271 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 9254 | **type** `public class Game` |  |
| 130 | 3 | `public Game()` |  |
| 142 | 4 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 167 | 194 | `private void buildWorld()` | Builds the entire simulation from nothing. |
| 362 | 4 | `public void run()` |  |
| 367 | 38 | `private void initialize()` |  |
| 408 | 4 | `static { ... }` |  |
| 415 | 14 | `public void newGame()` | buttons |
| 458 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 488 | 8 | `public void resumeGame()` |  |
| 496 | 51 | `public void loadGameSave(int slot)` |  |
| 556 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 564 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 577 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 579 | 6 | `public void toggleQuit()` |  |
| 589 | 1 | `public String getLoadFailure()` |  |
| 591 | 7 | `public void toggleGraphs()` |  |
| 598 | 5 | `public void toggleReports()` |  |
| 604 | 3 | `public void toggleNextMonth()` |  |
| 609 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 611 | 3 | `public int getMonth()` |  |
| 614 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 617-650)

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 651-673)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 674-900)

| line | len | member | says |
|---:|---:|---|---|
| 712 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 713 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 715 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 716 | 1 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 719 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 720 | 1 | `public double getSubsidyPaid(String key)` |  |
| 722 | 5 | `public double getTotalSubsidyPaid()` |  |
| 729 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 742 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 745 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 758 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 768 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 803 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 820 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 826 | 4 | `public double getIncome()` |  |
| 831 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 835 | 3 | `public double getWaterRatio()` |  |
| 839 | 3 | `public double getRoadRatio()` |  |
| 844 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 849 | 3 | `public Markets getMarkets()` | Every goods market. |
| 854 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 858 | 3 | `public LandManager getLandManager()` |  |
| 863 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |
| 878 | 9 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action. |
| 889 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 893 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 897 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 901-1349)

| line | len | member | says |
|---:|---:|---|---|
| 911 | 60 | `private void runPrivateInvestment()` |  |
| 987 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 1000 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 1004 | 344 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1350-1400)

| line | len | member | says |
|---:|---:|---|---|
| 1374 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1377 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1380 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1381 | 1 | `public double getHouseholdCarImports()` |  |
| 1384 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1387 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1390 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1393 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1396 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1399 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1401-1434)

| line | len | member | says |
|---:|---:|---|---|
| 1409 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1412 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1415 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1418 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1421 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1424 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1427 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1430 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1433 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1435-1727)

| line | len | member | says |
|---:|---:|---|---|
| 1453 | 10 | `private void capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 1473 | 37 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 1518 | 47 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 1583 | 71 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 1670 | 9 | `public double recapitaliseBank(double amount)` | The treasury puts capital into its bank. |
| 1681 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now. |
| 1694 | 9 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 1713 | 8 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 1723 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 1728-1869)

| line | len | member | says |
|---:|---:|---|---|
| 1747 | 57 | `private void runRetirement()` |  |
| 1811 | 58 | `private int retire(BusinessInvestment.Decision decision, Investor seller)` | Scraps what the decision named, and sells the plot back to the city. |

### THE CONSTRUCTION WARNING (lines 1870-1919)

| line | len | member | says |
|---:|---:|---|---|
| 1890 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 1902 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 1911 | 1 | `public int getConstructionShedMonth()` |  |
| 1912 | 1 | `public double getConstructionShedPoints()` |  |
| 1915 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 1920-2853)

| line | len | member | says |
|---:|---:|---|---|
| 1940 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 1960 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 1968 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 1975 | 3 | `public double getLastWriteOff()` |  |
| 2014 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 2024 | 110 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |
| 2136 | 54 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 2192 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 2212 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 2241 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 2258 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 2295 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 2314 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 2345 | 71 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 2426 | 1 | `public double getCityMaintenancePaid()` |  |
| 2431 | 4 | `public int getConstructionMaterials()` |  |
| 2435 | 4 | `public double getInterestRate()` |  |
| 2439 | 3 | `public boolean isGraphsEnabled()` |  |
| 2442 | 3 | `public boolean isReportsEnabled()` |  |
| 2461 | 88 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early if the treasury is empty. |
| 2551 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 2580 | 1 | `public boolean hasNewReceipt()` |  |
| 2581 | 1 | `public void clearReceipt()` |  |
| 2583 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 2597 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 2616 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 2636 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 2646 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 2688 | 12 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 2711 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 2720 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 2735 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 2758 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 2852 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 2854-3422)

| line | len | member | says |
|---:|---:|---|---|
| 2869 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 2881 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 2894 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 2914 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 2931 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 2993 | 1 | `public Investor getGovernmentInvestor()` |  |
| 2995 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 3035 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 3040 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 3043 | 3 | `public double getTotalBuildingCost()` |  |
| 3046 | 3 | `public String getBuildingName()` |  |
| 3049 | 3 | `public int getBuildQuantity()` |  |
| 3054 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 3067 | 103 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 3178 | 15 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 3200 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 3238 | 15 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 3296 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 3308 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 3329 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 3358 | 44 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 3404 | 18 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 3423-4701)

| line | len | member | says |
|---:|---:|---|---|
| 3465 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 3501 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 3516 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation, less the world's base rate less the world's realised inflation - the one definition the month hands the currenc... |
| 3528 | 3 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the year's inflation, the same PriceIndex.inflation() the real rate differential and the parity read - the one definition the month strikes the ho... |
| 3540 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 3545 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 3548 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 3555 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 3622 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 3642 | 70 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 3719 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 3732 | 3 | `private void printPopulationInfo()` | printers |
| 3737 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 3750 | 3 | `private void printUtilityInfo()` |  |
| 3754 | 3 | `private void printCityStats()` |  |
| 3761 | 689 | `private void nextMonth()` |  |
| 4451 | 222 | `private void startOfMonthUpdate()` |  |
| 4673 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 4702-4819)

| line | len | member | says |
|---:|---:|---|---|
| 4736 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 4761 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 4773 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 4793 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 4815 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 4820-5066)

| line | len | member | says |
|---:|---:|---|---|
| 4834 | 16 | `public String getCreditRating()` |  |
| 4852 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 4872 | 5 | `private void pushCostOfFundsToTheDebtMarket()` | Hands both lenders what money costs the bank, from the one struck figure. |
| 4888 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 4910 | 24 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 4935 | 78 | `private void finalUpdateEconomy()` |  |
| 5015 | 4 | `private void updateConstructionCost()` |  |
| 5026 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 5037 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 5041 | 4 | `public int getHouseholdCapacity()` |  |
| 5045 | 4 | `public int getStoreCapacity()` |  |
| 5049 | 3 | `public int[] getJobs()` |  |
| 5054 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 5067-5633)

| line | len | member | says |
|---:|---:|---|---|
| 5096 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 5132 | 1 | `public PopulationCohorts getCohorts()` |  |
| 5133 | 1 | `public FamilyModel getFamilies()` |  |
| 5134 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 5135 | 1 | `public Migration getMigration()` |  |
| 5136 | 1 | `public Health getHealth()` |  |
| 5137 | 1 | `public Healthcare getHealthcare()` |  |
| 5161 | 472 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 5634-5646)

| line | len | member | says |
|---:|---:|---|---|
| 5642 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 5645 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 5647-5917)

| line | len | member | says |
|---:|---:|---|---|
| 5663 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 5682 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 5702 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 5744 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 5770 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 5780 | 3 | `public BuildLog getBuildLog()` |  |
| 5787 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 5812 | 1 | `public Unemployment getUnemployment()` |  |
| 5816 | 1 | `public Sickness getSickness()` |  |
| 5824 | 1 | `public Crime getCrime()` |  |
| 5828 | 1 | `public double getLastOrphanDeaths()` |  |
| 5829 | 1 | `public double getLastUnhousedDeaths()` |  |
| 5831 | 5 | `{ ... }` |  |
| 5838 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 5863 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 5872 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 5887 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 5900 | 1 | `public double getStudentLoansLent()` |  |
| 5901 | 1 | `public double getStudentLoansRepaid()` |  |
| 5902 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 5906 | 1 | `public double getStudentLoanInterest()` |  |
| 5909 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 5916 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 5918-6018)

| line | len | member | says |
|---:|---:|---|---|
| 5933 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 5939 | 5 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 5946 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 5958 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 6002 | 1 | `public Equity getEquity()` |  |
| 6006 | 1 | `public Exchange getExchange()` |  |
| 6008 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 6019-6042)

| line | len | member | says |
|---:|---:|---|---|
| 6029 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 6038 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 6041 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 6043-6337)

| line | len | member | says |
|---:|---:|---|---|
| 6055 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 6057 | 1 | `public PriceIndex getPriceIndex()` |  |
| 6059 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 6066 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 6073 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 6082 | 1 | `public Bank getBank()` |  |
| 6095 | 1 | `public CentralBank getCentralBank()` |  |
| 6109 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 6115 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 6118 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 6125 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 6129 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 6133 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 6200 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 6204 | 3 | `public EconomyManager getEconomyManager()` |  |
| 6207 | 3 | `public PopulationManager getPopulationManager()` |  |
| 6211 | 1 | `public LabourMarket getLabourMarket()` |  |
| 6212 | 1 | `public Education getEducation()` |  |
| 6228 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 6253 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 6295 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 6313 | 22 | `public void recordMonth()` | Files this month in the graph history. |
| 6336 | 1 | `public Inbox getInbox()` |  |
| 6337 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 6338-6670)

| line | len | member | says |
|---:|---:|---|---|
| 6349 | 3 | `public int getMonthsUntilAutosave()` |  |
| 6365 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 6377 | 192 | `public void save(int slot, String slotName)` |  |
| 6578 | 5 | `public String takeSkipFailure()` |  |
| 6587 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 6589 | 1 | `public GameFiles getGameFiles()` |  |
| 6602 | 7 | `public GameFiles.Result[] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade. |
| 6610 | 18 | `public void sendBuildingSave()` |  |
| 6631 | 16 | `public void loadBuildings()` |  |
| 6661 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 6671-6811)

| line | len | member | says |
|---:|---:|---|---|
| 6698 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 6699 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 6700 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 6707 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 6729 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 6796 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 6805 | 1 | `public double getCityPaperSettled()` |  |
| 6806 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 6807 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 6809 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 6812-6887)

| line | len | member | says |
|---:|---:|---|---|
| 6840 | 1 | `public double getCouponsToHouseholds()` |  |
| 6841 | 1 | `public double getPrincipalToHouseholds()` |  |
| 6842 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 6853 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 6861 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 6874 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 6888-6913)

| line | len | member | says |
|---:|---:|---|---|
| 6898 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 6914-6966)

| line | len | member | says |
|---:|---:|---|---|
| 6931 | 35 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 6967-7028)

| line | len | member | says |
|---:|---:|---|---|
| 6986 | 42 | `private void openMarketOperation()` |  |

### a buyback's holders outside the pools (lines 7029-7050)

| line | len | member | says |
|---:|---:|---|---|
| 7044 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 7048 | 1 | `public double getBuybackToHouseholds()` |  |
| 7049 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 7051-7255)

| line | len | member | says |
|---:|---:|---|---|
| 7122 | 13 | `private void takeTreasuryMonth()` |  |
| 7137 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 7139 | 1 | `public double getTreasuryOpening()` |  |
| 7140 | 1 | `public double getTreasuryClosing()` |  |
| 7141 | 1 | `public double getTreasuryRaised()` |  |
| 7142 | 1 | `public double getTreasuryRepaid()` |  |
| 7143 | 1 | `public double getTreasurySurplus()` |  |
| 7146 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 7164 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 7176 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 7181 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 7189 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |
| 7193 | 5 | `double[] treasuryMonthToSave()` |  |
| 7199 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 7233 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 7236 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 7238 | 1 | `public double getPostAuditDriftWorst()` |  |
| 7239 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 7240 | 4 | `public void InterestExpense(double amount)` |  |
| 7245 | 3 | `public DebtManager getDebtManager()` |  |
| 7250 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 7256-7497)

| line | len | member | says |
|---:|---:|---|---|
| 7316 | 26 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 7354 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 7359 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 7381 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 7393 | 17 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 7419 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 7444 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 7451 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 7455 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 7465 | 7 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 7474 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 7477 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 7484 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 7493 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 7494 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 7495 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 7496 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 7498-7622)

| line | len | member | says |
|---:|---:|---|---|
| 7511 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 7519 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 7544 | 70 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 7623-9056)

| line | len | member | says |
|---:|---:|---|---|
| 7691 | 17 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 7709 | 295 | `private void rebuildSimulationState()` |  |
| 8046 | 967 | `public void loadGame(int slot)` | Load game |
| 9015 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 9057-9278)

| line | len | member | says |
|---:|---:|---|---|
| 9063 | 1 | `public Denomination getDenomination()` |  |
| 9066 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 9097 | 158 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 9266 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

