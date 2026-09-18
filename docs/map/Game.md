# Game.java - 7,810 lines · 274 methods · 8 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Equity](Equity.md) (36), [AgeBand](AgeBand.md) (27), [GameFiles](GameFiles.md) (23), [DebtQuote](DebtQuote.md) (21), [BuildingsTemplate](BuildingsTemplate.md) (18), [FamilyModel](FamilyModel.md) (15), [HouseholdAccounts](HouseholdAccounts.md) (14), [GameLog](GameLog.md) (14), [JobType](JobType.md) (13), [BuildingType](BuildingType.md) (13), [DebtManager](DebtManager.md) (12), [Sector](Sector.md) (11), [PayTier](PayTier.md) (10), [CareType](CareType.md) (10), [Bank](Bank.md) (9), [Investor](Investor.md) (9), [Healthcare](Healthcare.md) (9), [SectorBooks](SectorBooks.md) (7), [LandManager](LandManager.md) (7), [MoneyAudit](MoneyAudit.md) (7), [BuildingManager](BuildingManager.md) (6), [HistorySave](HistorySave.md) (6), [BusinessInvestment](BusinessInvestment.md) (6), [Markets](Markets.md) (6), [Household](Household.md) (6), [MediumTermBond](MediumTermBond.md) (6), [GameVersion](GameVersion.md) (6), [DataSave](DataSave.md) (5), [PopulationCohorts](PopulationCohorts.md) (5), [Debt](Debt.md) (5)... and 51 more

**Used by (88):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 561 | THE CONSTRUCTION SUBSIDY |
| 585 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 802 | PRIVATE INVESTMENT |
| 1041 | · AND THE BALANCE SHEET |
| 1180 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1231 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1265 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 1542 | SHRINKING |
| 1684 | THE CONSTRUCTION WARNING |
| 1734 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 2644 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 3194 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 3617 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 3771 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 3927 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 4244 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 4363 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 4555 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 5089 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 5102 | THE READINGS THE MONTH TAKES OF THE CITY |
| 5355 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 5379 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 5634 | the save system |
| 5948 | PAYING THE WORLD BACK |
| 6048 | WHAT THE TREASURY ACTUALLY DID |
| 6202 | BUYING YOUR OWN DEBT BACK |
| 6319 | WHY LAND IS NOT IN THE RENT FLOOR |
| 6614 | · · the three the monthly path sets and this did not |
| 7609 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 2642 | `Game.BuildResult.SUCCESS` |  |
| 2642 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 2642 | `Game.BuildResult.NO_LAND` |  |
| 2642 | `Game.BuildResult.NO_DEPOSIT` |  |
| 2642 | `Game.BuildResult.NO_LICENCE` |  |
| 2642 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 353 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 2656 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 3247 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 4261 | `Game.EMERGENCY_NOTE_MONTHS` | `6` | Term of the note the city is forced into when it cannot pay its bills. |
| 4276 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 4285 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face. |
| 4293 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 5641 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 534 | `private String loadFailure` | Why the last load did not happen, or null. |
| 577 | `private double constructionSubsidy` |  |
| 612 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 613 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1198 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1201 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1236 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 1692 | `private int constructionShedMonth` |  |
| 1693 | `private double constructionShedPoints` |  |
| 1752 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 1772 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 1787 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 2217 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 2222 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 2388 | `public final int quantity` |  |
| 2389 | `public final double sticker` |  |
| 2390 | `public final double materialsNeeded` |  |
| 2392 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 2394 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 2395 | `public final double plantPrice` |  |
| 2396 | `public final double plantCost` |  |
| 2397 | `public final double materialsImported` |  |
| 2398 | `public final double materialsPrice` |  |
| 2399 | `public final double importCost` |  |
| 2400 | `public final double total` |  |
| 2401 | `public final double landNeeded` |  |
| 2402 | `public final double landFree` |  |
| 2404 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 2775 | `private final Investor government` | The city itself, as a payer. |
| 4548 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 4549 | `private LandManager landManager` |  |
| 4552 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 4553 | `private BuildLog buildLog` |  |
| 4580 | `private PopulationCohorts cohorts` |  |
| 4581 | `private FamilyModel families` |  |
| 4589 | `private Migration migration` |  |
| 4595 | `private LabourMarket labourMarket` | What labour costs. |
| 4601 | `private Education education` | The schools. |
| 4610 | `private Health health` | How much of the workforce is off sick. |
| 4618 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 5094 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 5184 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 5191 | `private HouseholdAccounts households` | The residents' own books. |
| 5200 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 5210 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 5214 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 5222 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 5226 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 5298 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 5314 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 5324 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 5330 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 5337 | `private final Equity equity` | Who owns the city's companies. |
| 5341 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 5353 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 5361 | `private final Consumption consumption` |  |
| 5362 | `private boolean consumptionLoaded` |  |
| 5371 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 5389 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 5398 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 5399 | `private int rateHistoryFilled` |  |
| 5417 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 5434 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 5447 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 5461 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 5479 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 5482 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 5484 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 5485 | `private double carriedRetailCapacity` |  |
| 5486 | `private double carriedRetailWant` |  |
| 5489 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 5490 | `private java.util.Map<String, String> lastInvestment` |  |
| 5493 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 5547 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 5643 | `private int monthsSinceAutosave` |  |
| 5858 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 5867 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 5971 | `private double foreignDebtRaisedThisMonth` |  |
| 5972 | `private double foreignPrincipalRepaidThisMonth` |  |
| 5973 | `private double foreignInterestPaidThisMonth` |  |
| 6020 | `private double cityDebtRaisedThisMonth` | THE WORLD'S SIDE OF THE CITY'S DEBT, for MoneyAudit. |
| 6033 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 6034 | `private double cityPrincipalRepaidThisMonth` |  |
| 6043 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 6044 | `private double cityDiscountForBank` |  |
| 6080 | `private double treasuryOpening` |  |
| 6081 | `private double treasuryClosing` |  |
| 6082 | `private double treasuryRaised` |  |
| 6083 | `private double treasuryRepaid` |  |
| 6084 | `private double treasurySurplus` |  |
| 6085 | `private boolean treasuryRecorded` |  |
| 6147 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 6150 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 6158 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 6159 | `private String postAuditDriftPool` |  |
| 6160 | `private double postAuditDriftWorst` |  |
| 6295 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 7613 | `private final Denomination denomination` |  |
| 7803 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 7786 | **type** `public class Game` |  |
| 130 | 3 | `public Game()` |  |
| 142 | 4 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 167 | 141 | `private void buildWorld()` | Builds the entire simulation from nothing. |
| 309 | 4 | `public void run()` |  |
| 314 | 38 | `private void initialize()` |  |
| 355 | 4 | `static { ... }` |  |
| 362 | 14 | `public void newGame()` | buttons |
| 405 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 435 | 8 | `public void resumeGame()` |  |
| 443 | 51 | `public void loadGameSave(int slot)` |  |
| 503 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 511 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 524 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 526 | 6 | `public void toggleQuit()` |  |
| 536 | 1 | `public String getLoadFailure()` |  |
| 538 | 7 | `public void toggleGraphs()` |  |
| 545 | 5 | `public void toggleReports()` |  |
| 551 | 3 | `public void toggleNextMonth()` |  |
| 555 | 3 | `public int getMonth()` |  |
| 558 | 3 | `public double getCash()` |  |

### THE CONSTRUCTION SUBSIDY (lines 561-584)

| line | len | member | says |
|---:|---:|---|---|
| 579 | 1 | `public double getConstructionSubsidy()` |  |
| 581 | 3 | `public void setConstructionSubsidy(double monthlyAmount)` |  |

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 585-801)

| line | len | member | says |
|---:|---:|---|---|
| 615 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 616 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 618 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 619 | 1 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 622 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 623 | 1 | `public double getSubsidyPaid(String key)` |  |
| 625 | 5 | `public double getTotalSubsidyPaid()` |  |
| 632 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 651 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 654 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 667 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 671 | 17 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` |  |
| 696 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 713 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 719 | 4 | `public double getIncome()` |  |
| 731 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 735 | 3 | `public double getWaterRatio()` |  |
| 739 | 3 | `public double getRoadRatio()` |  |
| 745 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 750 | 3 | `public Markets getMarkets()` | Every goods market. |
| 755 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 759 | 3 | `public LandManager getLandManager()` |  |
| 764 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |
| 779 | 9 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action. |
| 790 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 794 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 798 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 802-1179)

| line | len | member | says |
|---:|---:|---|---|
| 812 | 60 | `private void runPrivateInvestment()` |  |
| 888 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 901 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 905 | 273 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1180-1230)

| line | len | member | says |
|---:|---:|---|---|
| 1204 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1207 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1210 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1211 | 1 | `public double getHouseholdCarImports()` |  |
| 1214 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1217 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1220 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1223 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1226 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1229 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1231-1264)

| line | len | member | says |
|---:|---:|---|---|
| 1239 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1242 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1245 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1248 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1251 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1254 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1257 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1260 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1263 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1265-1541)

| line | len | member | says |
|---:|---:|---|---|
| 1283 | 10 | `private void capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 1303 | 37 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 1348 | 47 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 1413 | 58 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 1487 | 8 | `public double recapitaliseBank(double amount)` | The treasury puts capital into its bank. |
| 1497 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now. |
| 1510 | 8 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 1528 | 7 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 1537 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 1542-1683)

| line | len | member | says |
|---:|---:|---|---|
| 1561 | 57 | `private void runRetirement()` |  |
| 1625 | 58 | `private int retire(BusinessInvestment.Decision decision, Investor seller)` | Scraps what the decision named, and sells the plot back to the city. |

### THE CONSTRUCTION WARNING (lines 1684-1733)

| line | len | member | says |
|---:|---:|---|---|
| 1711 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 1716 | 8 | `public boolean isConstructionShedding()` |  |
| 1725 | 1 | `public int getConstructionShedMonth()` |  |
| 1726 | 1 | `public double getConstructionShedPoints()` |  |
| 1729 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 1734-2643)

| line | len | member | says |
|---:|---:|---|---|
| 1754 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 1774 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 1782 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 1789 | 3 | `public double getLastWriteOff()` |  |
| 1828 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 1838 | 110 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |
| 1950 | 54 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 2006 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 2026 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 2055 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 2072 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 2109 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 2153 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 2159 | 50 | `private void chargeBuildingMaintenance()` |  |
| 2219 | 1 | `public double getCityMaintenancePaid()` |  |
| 2224 | 4 | `public int getConstructionMaterials()` |  |
| 2228 | 4 | `public double getInterestRate()` |  |
| 2232 | 3 | `public boolean isGraphsEnabled()` |  |
| 2235 | 3 | `public boolean isReportsEnabled()` |  |
| 2254 | 85 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early if the treasury is empty. |
| 2341 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 2370 | 1 | `public boolean hasNewReceipt()` |  |
| 2371 | 1 | `public void clearReceipt()` |  |
| 2373 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 2387 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 2406 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 2426 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 2436 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 2478 | 12 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 2501 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 2510 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 2525 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 2548 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 2642 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 2644-3193)

| line | len | member | says |
|---:|---:|---|---|
| 2659 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 2671 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 2684 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 2704 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 2721 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 2783 | 1 | `public Investor getGovernmentInvestor()` |  |
| 2785 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 2825 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 2830 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 2833 | 3 | `public double getTotalBuildingCost()` |  |
| 2836 | 3 | `public String getBuildingName()` |  |
| 2839 | 3 | `public int getBuildQuantity()` |  |
| 2844 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 2857 | 99 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 2964 | 13 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 2984 | 34 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 3020 | 13 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 3076 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 3088 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 3109 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 3138 | 32 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 3172 | 15 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 3194-4243)

| line | len | member | says |
|---:|---:|---|---|
| 3233 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 3260 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 3270 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 3273 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 3280 | 44 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 3331 | 68 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 3400 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` |  |
| 3413 | 3 | `private void printPopulationInfo()` | printers |
| 3418 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 3431 | 3 | `private void printUtilityInfo()` |  |
| 3435 | 3 | `private void printCityStats()` |  |
| 3442 | 541 | `private void nextMonth()` |  |
| 3984 | 222 | `private void startOfMonthUpdate()` |  |
| 4206 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 4244-4362)

| line | len | member | says |
|---:|---:|---|---|
| 4279 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 4304 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 4316 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 4336 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 4358 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 4363-4554)

| line | len | member | says |
|---:|---:|---|---|
| 4377 | 16 | `public String getCreditRating()` |  |
| 4395 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 4415 | 5 | `private void pushCostOfFundsToTheDebtMarket()` | Hands both lenders what money costs the bank, from the one struck figure. |
| 4421 | 5 | `private void priceTheDebtMarket()` |  |
| 4441 | 20 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 4462 | 39 | `private void finalUpdateEconomy()` |  |
| 4503 | 4 | `private void updateConstructionCost()` |  |
| 4514 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 4525 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 4529 | 4 | `public int getHouseholdCapacity()` |  |
| 4533 | 4 | `public int getStoreCapacity()` |  |
| 4537 | 3 | `public int[] getJobs()` |  |
| 4542 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 4555-5088)

| line | len | member | says |
|---:|---:|---|---|
| 4584 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 4620 | 1 | `public PopulationCohorts getCohorts()` |  |
| 4621 | 1 | `public FamilyModel getFamilies()` |  |
| 4622 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 4623 | 1 | `public Migration getMigration()` |  |
| 4624 | 1 | `public Health getHealth()` |  |
| 4625 | 1 | `public Healthcare getHealthcare()` |  |
| 4658 | 430 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 5089-5101)

| line | len | member | says |
|---:|---:|---|---|
| 5097 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 5100 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 5102-5354)

| line | len | member | says |
|---:|---:|---|---|
| 5118 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 5143 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 5169 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 5179 | 3 | `public BuildLog getBuildLog()` |  |
| 5186 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 5211 | 1 | `public Unemployment getUnemployment()` |  |
| 5215 | 1 | `public Sickness getSickness()` |  |
| 5223 | 1 | `public Crime getCrime()` |  |
| 5227 | 1 | `public double getLastOrphanDeaths()` |  |
| 5228 | 1 | `public double getLastUnhousedDeaths()` |  |
| 5230 | 5 | `{ ... }` |  |
| 5237 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 5262 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 5271 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 5286 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 5299 | 1 | `public double getStudentLoansLent()` |  |
| 5300 | 1 | `public double getStudentLoansRepaid()` |  |
| 5301 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 5304 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 5338 | 1 | `public Equity getEquity()` |  |
| 5342 | 1 | `public Exchange getExchange()` |  |
| 5344 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 5355-5378)

| line | len | member | says |
|---:|---:|---|---|
| 5365 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 5374 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 5377 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 5379-5633)

| line | len | member | says |
|---:|---:|---|---|
| 5391 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 5393 | 1 | `public PriceIndex getPriceIndex()` |  |
| 5395 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 5402 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 5409 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 5418 | 1 | `public Bank getBank()` |  |
| 5421 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 5425 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 5429 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 5496 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 5500 | 3 | `public EconomyManager getEconomyManager()` |  |
| 5503 | 3 | `public PopulationManager getPopulationManager()` |  |
| 5507 | 1 | `public LabourMarket getLabourMarket()` |  |
| 5508 | 1 | `public Education getEducation()` |  |
| 5524 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 5549 | 41 | `private void applyMigrationSkills()` |  |
| 5591 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 5609 | 22 | `public void recordMonth()` | Files this month in the graph history. |
| 5632 | 1 | `public Inbox getInbox()` |  |
| 5633 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 5634-5947)

| line | len | member | says |
|---:|---:|---|---|
| 5645 | 3 | `public int getMonthsUntilAutosave()` |  |
| 5661 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 5673 | 178 | `public void save(int slot, String slotName)` |  |
| 5860 | 5 | `public String takeSkipFailure()` |  |
| 5869 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 5871 | 1 | `public GameFiles getGameFiles()` |  |
| 5884 | 7 | `public GameFiles.Result[] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade. |
| 5892 | 18 | `public void sendBuildingSave()` |  |
| 5913 | 16 | `public void loadBuildings()` |  |
| 5943 | 4 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 5948-6047)

| line | len | member | says |
|---:|---:|---|---|
| 5975 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 5976 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 5977 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 5984 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 6006 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 6045 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 6046 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 6048-6201)

| line | len | member | says |
|---:|---:|---|---|
| 6087 | 7 | `private void takeTreasuryMonth()` |  |
| 6096 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 6098 | 1 | `public double getTreasuryOpening()` |  |
| 6099 | 1 | `public double getTreasuryClosing()` |  |
| 6100 | 1 | `public double getTreasuryRaised()` |  |
| 6101 | 1 | `public double getTreasuryRepaid()` |  |
| 6102 | 1 | `public double getTreasurySurplus()` |  |
| 6105 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 6118 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain. |
| 6123 | 5 | `double[] treasuryMonthToSave()` |  |
| 6129 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 6163 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 6166 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 6168 | 1 | `public double getPostAuditDriftWorst()` |  |
| 6169 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 6170 | 4 | `public void InterestExpense(double amount)` |  |
| 6175 | 3 | `public DebtManager getDebtManager()` |  |
| 6180 | 4 | `public void printEndOfTurn()` |  |

### BUYING YOUR OWN DEBT BACK (lines 6202-6318)

| line | len | member | says |
|---:|---:|---|---|
| 6211 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now, at the standing rate. |
| 6219 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 6243 | 23 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |
| 6267 | 21 | `public DebtQuote issueEmergencyDebt(double cashNeeded, int duration)` |  |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 6319-7608)

| line | len | member | says |
|---:|---:|---|---|
| 6365 | 17 | `public double marginalHousingCost()` |  |
| 6383 | 268 | `private void rebuildSimulationState()` |  |
| 6693 | 872 | `public void loadGame(int slot)` | Load game |
| 7567 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 7609-7810)

| line | len | member | says |
|---:|---:|---|---|
| 7615 | 1 | `public Denomination getDenomination()` |  |
| 7618 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 7649 | 138 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 7798 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

