# Game.java - 9,836 lines · 340 methods · 12 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [TreasuryLine](TreasuryLine.md) (41), [AgeBand](AgeBand.md) (36), [Equity](Equity.md) (32), [GameFiles](GameFiles.md) (23), [CareType](CareType.md) (20), [Debt](Debt.md) (19), [DebtQuote](DebtQuote.md) (19), [BuildingsTemplate](BuildingsTemplate.md) (18), [FamilyModel](FamilyModel.md) (16), [GameLog](GameLog.md) (15), [HouseholdAccounts](HouseholdAccounts.md) (14), [JobType](JobType.md) (13), [DebtManager](DebtManager.md) (13), [Sector](Sector.md) (13), [BuildingType](BuildingType.md) (13), [TaxPolicy](TaxPolicy.md) (12), [Healthcare](Healthcare.md) (10), [PayTier](PayTier.md) (10), [Investor](Investor.md) (10), [Household](Household.md) (9), [BusinessDebtManager](BusinessDebtManager.md) (8), [Bank](Bank.md) (8), [LongTermBond](LongTermBond.md) (8), [SectorBooks](SectorBooks.md) (7), [BusinessInvestment](BusinessInvestment.md) (7), [LandManager](LandManager.md) (7), [MediumTermBond](MediumTermBond.md) (7), [HouseholdBalance](HouseholdBalance.md) (7), [MoneyAudit](MoneyAudit.md) (7), [BuildingManager](BuildingManager.md) (6)... and 56 more

**Used by (94):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 623 | THE FOUNDING RESERVE (2026-09-21) |
| 657 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 680 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 878 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1020 | PRIVATE INVESTMENT |
| 1318 | · AND THE BALANCE SHEET |
| 1510 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1561 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1595 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 1980 | SHRINKING |
| 2133 | THE CONSTRUCTION WARNING |
| 2183 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 2445 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 3253 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 3822 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 4196 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 4439 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 4625 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 4805 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 5122 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 5240 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 5491 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 6072 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 6085 | THE READINGS THE MONTH TAKES OF THE CITY |
| 6374 | · the price of a place (2026-09-21) |
| 6475 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 6499 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 6795 | the save system |
| 7146 | PAYING THE WORLD BACK |
| 7287 | THE HOLDERS ARE PAID (0.7.1) |
| 7363 | · the desk, for the households |
| 7389 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 7442 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 7504 | · a buyback's holders outside the pools |
| 7526 | WHAT THE TREASURY ACTUALLY DID |
| 7731 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 7973 | BUYING YOUR OWN DEBT BACK |
| 8098 | WHY LAND IS NOT IN THE RENT FLOOR |
| 8442 | · · the three the monthly path sets and this did not |
| 9611 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3251 | `Game.BuildResult.SUCCESS` |  |
| 3251 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 3251 | `Game.BuildResult.NO_LAND` |  |
| 3251 | `Game.BuildResult.NO_DEPOSIT` |  |
| 3251 | `Game.BuildResult.NO_LICENCE` |  |
| 3251 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 411 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 649 | `Game.FOUNDING_CASH` | `2_500_000` | What the founders leave in the treasury, in thousands: D$2.5B, the endowment less the vault. |
| 652 | `Game.FOUNDING_RESERVE_USD` | `1_000_000` | What the founders leave in the vault, in thousands of US dollars: US$1B, bought on day one at the opening rate. |
| 655 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 3265 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 3878 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 4033 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 5138 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 5153 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 5162 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face. |
| 5170 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 6802 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 593 | `private String loadFailure` | Why the last load did not happen, or null. |
| 715 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 716 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 916 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 999 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under the toggle, and a short vault says here that the rest was converted. |
| 1000 | `private int lastLandReceiptMonth` |  |
| 1528 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1531 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1566 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 2141 | `private int constructionShedMonth` |  |
| 2142 | `private double constructionShedPoints` |  |
| 2201 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 2214 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 2234 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 2249 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 2484 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 2485 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 2486 | `private double salvageUsedThisMonth` |  |
| 2818 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 2823 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 2992 | `public final int quantity` |  |
| 2993 | `public final double sticker` |  |
| 2994 | `public final double materialsNeeded` |  |
| 2996 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 2998 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 2999 | `public final double plantPrice` |  |
| 3000 | `public final double plantCost` |  |
| 3001 | `public final double materialsImported` |  |
| 3002 | `public final double materialsPrice` |  |
| 3003 | `public final double importCost` |  |
| 3004 | `public final double total` |  |
| 3005 | `public final double landNeeded` |  |
| 3006 | `public final double landFree` |  |
| 3008 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 3384 | `private final Investor government` | The city itself, as a payer. |
| 5484 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 5485 | `private LandManager landManager` |  |
| 5488 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 5489 | `private BuildLog buildLog` |  |
| 5516 | `private PopulationCohorts cohorts` |  |
| 5517 | `private FamilyModel families` |  |
| 5525 | `private Migration migration` |  |
| 5531 | `private LabourMarket labourMarket` | What labour costs. |
| 5537 | `private Education education` | The schools. |
| 5546 | `private Health health` | How much of the workforce is off sick. |
| 5554 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 6077 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 6223 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 6230 | `private HouseholdAccounts households` | The residents' own books. |
| 6239 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 6249 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 6253 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 6261 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 6265 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 6355 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 6361 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 6434 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 6444 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 6450 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 6457 | `private final Equity equity` | Who owns the city's companies. |
| 6461 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 6473 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 6481 | `private final Consumption consumption` |  |
| 6482 | `private boolean consumptionLoaded` |  |
| 6491 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 6509 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 6518 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 6519 | `private int rateHistoryFilled` |  |
| 6537 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 6550 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 6594 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 6607 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 6621 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 6639 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 6642 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 6644 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 6645 | `private double carriedRetailCapacity` |  |
| 6646 | `private double carriedRetailWant` |  |
| 6649 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 6650 | `private java.util.Map<String, String> lastInvestment` |  |
| 6653 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 6697 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 6804 | `private int monthsSinceAutosave` |  |
| 7051 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 7060 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 7169 | `private double foreignDebtRaisedThisMonth` |  |
| 7170 | `private double foreignPrincipalRepaidThisMonth` |  |
| 7171 | `private double foreignInterestPaidThisMonth` |  |
| 7223 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 7243 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 7244 | `private double cityPrincipalRepaidThisMonth` |  |
| 7258 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 7259 | `private double cityDiscountForBank` |  |
| 7262 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 7279 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 7285 | `private double bankPrincipalRepaidThisMonth` |  |
| 7313 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 7404 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 7514 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 7516 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 7568 | `private double treasuryOpening` |  |
| 7569 | `private double treasuryClosing` |  |
| 7570 | `private double treasuryRaised` |  |
| 7571 | `private double treasuryRepaid` |  |
| 7572 | `private double treasurySurplus` |  |
| 7573 | `private boolean treasuryRecorded` |  |
| 7592 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 7595 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 7692 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 7695 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 7703 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 7704 | `private String postAuditDriftPool` |  |
| 7705 | `private double postAuditDriftWorst` |  |
| 7769 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 7772 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 7775 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 8096 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 9566 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 9615 | `private final Denomination denomination` |  |
| 9829 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 9812 | **type** `public class Game` |  |
| 130 | 3 | `public Game()` |  |
| 142 | 4 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 167 | 199 | `private void buildWorld()` | Builds the entire simulation from nothing. |
| 367 | 4 | `public void run()` |  |
| 372 | 38 | `private void initialize()` |  |
| 413 | 4 | `static { ... }` |  |
| 420 | 14 | `public void newGame()` | buttons |
| 464 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 494 | 8 | `public void resumeGame()` |  |
| 502 | 51 | `public void loadGameSave(int slot)` |  |
| 562 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 570 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 583 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 585 | 6 | `public void toggleQuit()` |  |
| 595 | 1 | `public String getLoadFailure()` |  |
| 597 | 7 | `public void toggleGraphs()` |  |
| 604 | 5 | `public void toggleReports()` |  |
| 610 | 3 | `public void toggleNextMonth()` |  |
| 615 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 617 | 3 | `public int getMonth()` |  |
| 620 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 623-656)

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 657-679)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 680-877)

| line | len | member | says |
|---:|---:|---|---|
| 718 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 719 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 721 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 722 | 1 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 725 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 726 | 1 | `public double getSubsidyPaid(String key)` |  |
| 728 | 5 | `public double getTotalSubsidyPaid()` |  |
| 735 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 748 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 751 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 764 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 774 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 809 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 826 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 832 | 4 | `public double getIncome()` |  |
| 837 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 841 | 3 | `public double getWaterRatio()` |  |
| 845 | 3 | `public double getRoadRatio()` |  |
| 850 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 855 | 3 | `public Markets getMarkets()` | Every goods market. |
| 860 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 864 | 3 | `public LandManager getLandManager()` |  |
| 869 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 878-1019)

| line | len | member | says |
|---:|---:|---|---|
| 919 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 922 | 1 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 932 | 41 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 981 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 988 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - for the land office's buttons. |
| 1003 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1008 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 1012 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1016 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 1020-1509)

| line | len | member | says |
|---:|---:|---|---|
| 1030 | 71 | `private void runPrivateInvestment()` |  |
| 1117 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 1130 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 1142 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 1148 | 360 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1510-1560)

| line | len | member | says |
|---:|---:|---|---|
| 1534 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1537 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1540 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1541 | 1 | `public double getHouseholdCarImports()` |  |
| 1544 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1547 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1550 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1553 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1556 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1559 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1561-1594)

| line | len | member | says |
|---:|---:|---|---|
| 1569 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1572 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1575 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1578 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1581 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1584 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1587 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1590 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1593 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1595-1979)

| line | len | member | says |
|---:|---:|---|---|
| 1613 | 10 | `private void capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 1630 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 1652 | 39 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 1699 | 43 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 1760 | 71 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 1840 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 1864 | 8 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 1884 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |
| 1911 | 9 | `public double recapitaliseBank(double amount)` | The treasury puts capital into its bank. |
| 1922 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now. |
| 1932 | 4 | `public boolean canRecapitaliseBank()` | True when the bank needs capital and the treasury holds all of it: the rescue button's guard (0.7.9), here so the screen and anything else that offers the rescue cannot disagree about it. |
| 1946 | 9 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 1965 | 8 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 1975 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 1980-2132)

| line | len | member | says |
|---:|---:|---|---|
| 1999 | 57 | `private void runRetirement()` |  |
| 2068 | 64 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 2133-2182)

| line | len | member | says |
|---:|---:|---|---|
| 2153 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 2165 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 2174 | 1 | `public int getConstructionShedMonth()` |  |
| 2175 | 1 | `public double getConstructionShedPoints()` |  |
| 2178 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 2183-2444)

| line | len | member | says |
|---:|---:|---|---|
| 2203 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 2216 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 2236 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 2244 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 2251 | 3 | `public double getLastWriteOff()` |  |
| 2290 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 2300 | 144 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 2445-3252)

| line | len | member | says |
|---:|---:|---|---|
| 2481 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 2489 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 2494 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 2499 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 2502 | 20 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |
| 2524 | 60 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 2586 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 2606 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 2635 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 2652 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 2689 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 2708 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 2739 | 71 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 2820 | 1 | `public double getCityMaintenancePaid()` |  |
| 2825 | 4 | `public int getConstructionMaterials()` |  |
| 2829 | 4 | `public double getInterestRate()` |  |
| 2833 | 3 | `public boolean isGraphsEnabled()` |  |
| 2836 | 3 | `public boolean isReportsEnabled()` |  |
| 2855 | 88 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early if the treasury is empty. |
| 2945 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 2974 | 1 | `public boolean hasNewReceipt()` |  |
| 2975 | 1 | `public void clearReceipt()` |  |
| 2977 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 2991 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 3010 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 3030 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 3040 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 3082 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 3110 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 3119 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 3134 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 3157 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 3251 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 3253-3821)

| line | len | member | says |
|---:|---:|---|---|
| 3268 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 3280 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 3293 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 3313 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 3330 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 3392 | 1 | `public Investor getGovernmentInvestor()` |  |
| 3394 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 3434 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 3439 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 3442 | 3 | `public double getTotalBuildingCost()` |  |
| 3445 | 3 | `public String getBuildingName()` |  |
| 3448 | 3 | `public int getBuildQuantity()` |  |
| 3453 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 3466 | 103 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 3577 | 15 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 3599 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 3637 | 15 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 3695 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 3707 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 3728 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 3757 | 44 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 3803 | 18 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 3822-5121)

| line | len | member | says |
|---:|---:|---|---|
| 3864 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 3900 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 3915 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation, less the world's base rate less the world's realised inflation - the one definition the month hands the currenc... |
| 3927 | 3 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the year's inflation, the same PriceIndex.inflation() the real rate differential and the parity read - the one definition the month strikes the ho... |
| 3939 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 3944 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 3947 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 3954 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 4021 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 4041 | 70 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 4118 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 4131 | 3 | `private void printPopulationInfo()` | printers |
| 4136 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 4149 | 3 | `private void printUtilityInfo()` |  |
| 4153 | 3 | `private void printCityStats()` |  |
| 4160 | 701 | `private void nextMonth()` |  |
| 4862 | 231 | `private void startOfMonthUpdate()` |  |
| 5093 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 5122-5239)

| line | len | member | says |
|---:|---:|---|---|
| 5156 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 5181 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 5193 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 5213 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 5235 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 5240-5490)

| line | len | member | says |
|---:|---:|---|---|
| 5254 | 16 | `public String getCreditRating()` |  |
| 5272 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 5295 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 5309 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 5331 | 27 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 5359 | 78 | `private void finalUpdateEconomy()` |  |
| 5439 | 4 | `private void updateConstructionCost()` |  |
| 5450 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 5461 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 5465 | 4 | `public int getHouseholdCapacity()` |  |
| 5469 | 4 | `public int getStoreCapacity()` |  |
| 5473 | 3 | `public int[] getJobs()` |  |
| 5478 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 5491-6071)

| line | len | member | says |
|---:|---:|---|---|
| 5520 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 5556 | 1 | `public PopulationCohorts getCohorts()` |  |
| 5557 | 1 | `public FamilyModel getFamilies()` |  |
| 5558 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 5559 | 1 | `public Migration getMigration()` |  |
| 5560 | 1 | `public Health getHealth()` |  |
| 5561 | 1 | `public Healthcare getHealthcare()` |  |
| 5585 | 486 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 6072-6084)

| line | len | member | says |
|---:|---:|---|---|
| 6080 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 6083 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 6085-6373)

| line | len | member | says |
|---:|---:|---|---|
| 6101 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 6120 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 6140 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 6182 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 6208 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 6218 | 3 | `public BuildLog getBuildLog()` |  |
| 6225 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 6250 | 1 | `public Unemployment getUnemployment()` |  |
| 6254 | 1 | `public Sickness getSickness()` |  |
| 6262 | 1 | `public Crime getCrime()` |  |
| 6266 | 1 | `public double getLastOrphanDeaths()` |  |
| 6267 | 1 | `public double getLastUnhousedDeaths()` |  |
| 6269 | 6 | `{ ... }` |  |
| 6277 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 6302 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 6311 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 6332 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 6343 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 6356 | 1 | `public double getStudentLoansLent()` |  |
| 6357 | 1 | `public double getStudentLoansRepaid()` |  |
| 6358 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 6362 | 1 | `public double getStudentLoanInterest()` |  |
| 6365 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 6372 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 6374-6474)

| line | len | member | says |
|---:|---:|---|---|
| 6389 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 6395 | 5 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 6402 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 6414 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 6458 | 1 | `public Equity getEquity()` |  |
| 6462 | 1 | `public Exchange getExchange()` |  |
| 6464 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 6475-6498)

| line | len | member | says |
|---:|---:|---|---|
| 6485 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 6494 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 6497 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 6499-6794)

| line | len | member | says |
|---:|---:|---|---|
| 6511 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 6513 | 1 | `public PriceIndex getPriceIndex()` |  |
| 6515 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 6522 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 6529 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 6538 | 1 | `public Bank getBank()` |  |
| 6551 | 1 | `public CentralBank getCentralBank()` |  |
| 6565 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 6571 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 6574 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 6581 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 6585 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 6589 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 6656 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 6660 | 3 | `public EconomyManager getEconomyManager()` |  |
| 6663 | 3 | `public PopulationManager getPopulationManager()` |  |
| 6667 | 1 | `public LabourMarket getLabourMarket()` |  |
| 6668 | 1 | `public Education getEducation()` |  |
| 6684 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 6709 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 6751 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 6769 | 23 | `public void recordMonth()` | Files this month in the graph history. |
| 6793 | 1 | `public Inbox getInbox()` |  |
| 6794 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 6795-7145)

| line | len | member | says |
|---:|---:|---|---|
| 6806 | 3 | `public int getMonthsUntilAutosave()` |  |
| 6822 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 6834 | 210 | `public void save(int slot, String slotName)` |  |
| 7053 | 5 | `public String takeSkipFailure()` |  |
| 7062 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 7064 | 1 | `public GameFiles getGameFiles()` |  |
| 7077 | 7 | `public GameFiles.Result[] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade. |
| 7085 | 18 | `public void sendBuildingSave()` |  |
| 7106 | 16 | `public void loadBuildings()` |  |
| 7136 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 7146-7286)

| line | len | member | says |
|---:|---:|---|---|
| 7173 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 7174 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 7175 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 7182 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 7204 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 7271 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 7280 | 1 | `public double getCityPaperSettled()` |  |
| 7281 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 7282 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 7284 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 7287-7362)

| line | len | member | says |
|---:|---:|---|---|
| 7315 | 1 | `public double getCouponsToHouseholds()` |  |
| 7316 | 1 | `public double getPrincipalToHouseholds()` |  |
| 7317 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 7328 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 7336 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 7349 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 7363-7388)

| line | len | member | says |
|---:|---:|---|---|
| 7373 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 7389-7441)

| line | len | member | says |
|---:|---:|---|---|
| 7406 | 35 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 7442-7503)

| line | len | member | says |
|---:|---:|---|---|
| 7461 | 42 | `private void openMarketOperation()` |  |

### a buyback's holders outside the pools (lines 7504-7525)

| line | len | member | says |
|---:|---:|---|---|
| 7519 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 7523 | 1 | `public double getBuybackToHouseholds()` |  |
| 7524 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 7526-7730)

| line | len | member | says |
|---:|---:|---|---|
| 7597 | 13 | `private void takeTreasuryMonth()` |  |
| 7612 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 7614 | 1 | `public double getTreasuryOpening()` |  |
| 7615 | 1 | `public double getTreasuryClosing()` |  |
| 7616 | 1 | `public double getTreasuryRaised()` |  |
| 7617 | 1 | `public double getTreasuryRepaid()` |  |
| 7618 | 1 | `public double getTreasurySurplus()` |  |
| 7621 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 7639 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 7651 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 7656 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 7664 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |
| 7668 | 5 | `double[] treasuryMonthToSave()` |  |
| 7674 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 7708 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 7711 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 7713 | 1 | `public double getPostAuditDriftWorst()` |  |
| 7714 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 7715 | 4 | `public void InterestExpense(double amount)` |  |
| 7720 | 3 | `public DebtManager getDebtManager()` |  |
| 7725 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 7731-7972)

| line | len | member | says |
|---:|---:|---|---|
| 7791 | 26 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 7829 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 7834 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 7856 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 7868 | 17 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 7894 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 7919 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 7926 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 7930 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 7940 | 7 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 7949 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 7952 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 7959 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 7968 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 7969 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 7970 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 7971 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 7973-8097)

| line | len | member | says |
|---:|---:|---|---|
| 7986 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 7994 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 8019 | 70 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 8098-9610)

| line | len | member | says |
|---:|---:|---|---|
| 8166 | 17 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 8184 | 295 | `private void rebuildSimulationState()` |  |
| 8521 | 1043 | `public void loadGame(int slot)` | Load game |
| 9569 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 9611-9836)

| line | len | member | says |
|---:|---:|---|---|
| 9617 | 1 | `public Denomination getDenomination()` |  |
| 9620 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 9651 | 162 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 9824 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

