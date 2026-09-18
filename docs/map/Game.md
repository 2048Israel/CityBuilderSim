# Game.java - 7,792 lines · 274 methods · 8 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Equity](Equity.md) (36), [AgeBand](AgeBand.md) (27), [GameFiles](GameFiles.md) (23), [DebtQuote](DebtQuote.md) (21), [BuildingsTemplate](BuildingsTemplate.md) (18), [FamilyModel](FamilyModel.md) (15), [HouseholdAccounts](HouseholdAccounts.md) (14), [GameLog](GameLog.md) (14), [JobType](JobType.md) (13), [BuildingType](BuildingType.md) (13), [DebtManager](DebtManager.md) (12), [Sector](Sector.md) (11), [PayTier](PayTier.md) (10), [CareType](CareType.md) (10), [Bank](Bank.md) (9), [Investor](Investor.md) (9), [Healthcare](Healthcare.md) (9), [SectorBooks](SectorBooks.md) (7), [LandManager](LandManager.md) (7), [MoneyAudit](MoneyAudit.md) (7), [BuildingManager](BuildingManager.md) (6), [HistorySave](HistorySave.md) (6), [BusinessInvestment](BusinessInvestment.md) (6), [Markets](Markets.md) (6), [Household](Household.md) (6), [MediumTermBond](MediumTermBond.md) (6), [GameVersion](GameVersion.md) (6), [DataSave](DataSave.md) (5), [PopulationCohorts](PopulationCohorts.md) (5), [Debt](Debt.md) (5)... and 51 more

**Used by (88):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [TradeCostCheck](TradeCostCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 561 | THE CONSTRUCTION SUBSIDY |
| 585 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 794 | PRIVATE INVESTMENT |
| 1033 | · AND THE BALANCE SHEET |
| 1172 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1223 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1257 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 1534 | SHRINKING |
| 1676 | THE CONSTRUCTION WARNING |
| 1726 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 2636 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 3180 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 3609 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 3763 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 3919 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 4227 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 4345 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 4546 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 5071 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 5084 | THE READINGS THE MONTH TAKES OF THE CITY |
| 5337 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 5361 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 5616 | the save system |
| 5930 | PAYING THE WORLD BACK |
| 6030 | WHAT THE TREASURY ACTUALLY DID |
| 6168 | BUYING YOUR OWN DEBT BACK |
| 6279 | WHY LAND IS NOT IN THE RENT FLOOR |
| 6596 | · · the three the monthly path sets and this did not |
| 7591 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 2634 | `Game.BuildResult.SUCCESS` |  |
| 2634 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 2634 | `Game.BuildResult.NO_LAND` |  |
| 2634 | `Game.BuildResult.NO_DEPOSIT` |  |
| 2634 | `Game.BuildResult.NO_LICENCE` |  |
| 2634 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 353 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 2648 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 3233 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 4243 | `Game.EMERGENCY_NOTE_MONTHS` | `6` | Term of the note the city is forced into when it cannot pay its bills. |
| 4258 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 4267 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face. |
| 4275 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 5623 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 1190 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1193 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1228 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 1684 | `private int constructionShedMonth` |  |
| 1685 | `private double constructionShedPoints` |  |
| 1744 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 1764 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 1779 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 2209 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 2214 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 2380 | `public final int quantity` |  |
| 2381 | `public final double sticker` |  |
| 2382 | `public final double materialsNeeded` |  |
| 2384 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 2386 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 2387 | `public final double plantPrice` |  |
| 2388 | `public final double plantCost` |  |
| 2389 | `public final double materialsImported` |  |
| 2390 | `public final double materialsPrice` |  |
| 2391 | `public final double importCost` |  |
| 2392 | `public final double total` |  |
| 2393 | `public final double landNeeded` |  |
| 2394 | `public final double landFree` |  |
| 2396 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 2767 | `private final Investor government` | The city itself, as a payer. |
| 4539 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 4540 | `private LandManager landManager` |  |
| 4543 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 4544 | `private BuildLog buildLog` |  |
| 4571 | `private PopulationCohorts cohorts` |  |
| 4572 | `private FamilyModel families` |  |
| 4580 | `private Migration migration` |  |
| 4586 | `private LabourMarket labourMarket` | What labour costs. |
| 4592 | `private Education education` | The schools. |
| 4601 | `private Health health` | How much of the workforce is off sick. |
| 4609 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 5076 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 5166 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 5173 | `private HouseholdAccounts households` | The residents' own books. |
| 5182 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 5192 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 5196 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 5204 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 5208 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 5280 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 5296 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 5306 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 5312 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 5319 | `private final Equity equity` | Who owns the city's companies. |
| 5323 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 5335 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 5343 | `private final Consumption consumption` |  |
| 5344 | `private boolean consumptionLoaded` |  |
| 5353 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 5371 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 5380 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 5381 | `private int rateHistoryFilled` |  |
| 5399 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 5416 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 5429 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 5443 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 5461 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 5464 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 5466 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 5467 | `private double carriedRetailCapacity` |  |
| 5468 | `private double carriedRetailWant` |  |
| 5471 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 5472 | `private java.util.Map<String, String> lastInvestment` |  |
| 5475 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 5519 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 5625 | `private int monthsSinceAutosave` |  |
| 5840 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 5849 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 5953 | `private double foreignDebtRaisedThisMonth` |  |
| 5954 | `private double foreignPrincipalRepaidThisMonth` |  |
| 5955 | `private double foreignInterestPaidThisMonth` |  |
| 6002 | `private double cityDebtRaisedThisMonth` | THE WORLD'S SIDE OF THE CITY'S DEBT, for MoneyAudit. |
| 6015 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 6016 | `private double cityPrincipalRepaidThisMonth` |  |
| 6025 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 6026 | `private double cityDiscountForBank` |  |
| 6062 | `private double treasuryOpening` |  |
| 6063 | `private double treasuryClosing` |  |
| 6064 | `private double treasuryRaised` |  |
| 6065 | `private double treasuryRepaid` |  |
| 6066 | `private double treasurySurplus` |  |
| 6067 | `private boolean treasuryRecorded` |  |
| 6129 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 6132 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 6140 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 6141 | `private String postAuditDriftPool` |  |
| 6142 | `private double postAuditDriftWorst` |  |
| 6277 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 7595 | `private final Denomination denomination` |  |
| 7785 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 7768 | **type** `public class Game` |  |
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

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 585-793)

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
| 645 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 648 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 661 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 671 | 17 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 696 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 713 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 719 | 4 | `public double getIncome()` |  |
| 724 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 728 | 3 | `public double getWaterRatio()` |  |
| 732 | 3 | `public double getRoadRatio()` |  |
| 737 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 742 | 3 | `public Markets getMarkets()` | Every goods market. |
| 747 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 751 | 3 | `public LandManager getLandManager()` |  |
| 756 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |
| 771 | 9 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action. |
| 782 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 786 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 790 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 794-1171)

| line | len | member | says |
|---:|---:|---|---|
| 804 | 60 | `private void runPrivateInvestment()` |  |
| 880 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 893 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 897 | 273 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1172-1222)

| line | len | member | says |
|---:|---:|---|---|
| 1196 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1199 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1202 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1203 | 1 | `public double getHouseholdCarImports()` |  |
| 1206 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1209 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1212 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1215 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1218 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1221 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1223-1256)

| line | len | member | says |
|---:|---:|---|---|
| 1231 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1234 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1237 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1240 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1243 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1246 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1249 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1252 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1255 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1257-1533)

| line | len | member | says |
|---:|---:|---|---|
| 1275 | 10 | `private void capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 1295 | 37 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 1340 | 47 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 1405 | 58 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 1479 | 8 | `public double recapitaliseBank(double amount)` | The treasury puts capital into its bank. |
| 1489 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now. |
| 1502 | 8 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 1520 | 7 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 1529 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 1534-1675)

| line | len | member | says |
|---:|---:|---|---|
| 1553 | 57 | `private void runRetirement()` |  |
| 1617 | 58 | `private int retire(BusinessInvestment.Decision decision, Investor seller)` | Scraps what the decision named, and sells the plot back to the city. |

### THE CONSTRUCTION WARNING (lines 1676-1725)

| line | len | member | says |
|---:|---:|---|---|
| 1696 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 1708 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 1717 | 1 | `public int getConstructionShedMonth()` |  |
| 1718 | 1 | `public double getConstructionShedPoints()` |  |
| 1721 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 1726-2635)

| line | len | member | says |
|---:|---:|---|---|
| 1746 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 1766 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 1774 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 1781 | 3 | `public double getLastWriteOff()` |  |
| 1820 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 1830 | 110 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |
| 1942 | 54 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 1998 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 2018 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 2047 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 2064 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 2101 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 2120 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 2151 | 50 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 2211 | 1 | `public double getCityMaintenancePaid()` |  |
| 2216 | 4 | `public int getConstructionMaterials()` |  |
| 2220 | 4 | `public double getInterestRate()` |  |
| 2224 | 3 | `public boolean isGraphsEnabled()` |  |
| 2227 | 3 | `public boolean isReportsEnabled()` |  |
| 2246 | 85 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early if the treasury is empty. |
| 2333 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 2362 | 1 | `public boolean hasNewReceipt()` |  |
| 2363 | 1 | `public void clearReceipt()` |  |
| 2365 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 2379 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 2398 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 2418 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 2428 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 2470 | 12 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 2493 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 2502 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 2517 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 2540 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 2634 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 2636-3179)

| line | len | member | says |
|---:|---:|---|---|
| 2651 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 2663 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 2676 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 2696 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 2713 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 2775 | 1 | `public Investor getGovernmentInvestor()` |  |
| 2777 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 2817 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 2822 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 2825 | 3 | `public double getTotalBuildingCost()` |  |
| 2828 | 3 | `public String getBuildingName()` |  |
| 2831 | 3 | `public int getBuildQuantity()` |  |
| 2836 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 2849 | 99 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 2956 | 13 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 2976 | 34 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 3012 | 13 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 3068 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 3080 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 3101 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 3130 | 32 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 3164 | 15 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 3180-4226)

| line | len | member | says |
|---:|---:|---|---|
| 3219 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 3246 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 3256 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 3259 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 3266 | 44 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 3317 | 68 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 3392 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 3405 | 3 | `private void printPopulationInfo()` | printers |
| 3410 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 3423 | 3 | `private void printUtilityInfo()` |  |
| 3427 | 3 | `private void printCityStats()` |  |
| 3434 | 541 | `private void nextMonth()` |  |
| 3976 | 222 | `private void startOfMonthUpdate()` |  |
| 4198 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 4227-4344)

| line | len | member | says |
|---:|---:|---|---|
| 4261 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 4286 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 4298 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 4318 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 4340 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 4345-4545)

| line | len | member | says |
|---:|---:|---|---|
| 4359 | 16 | `public String getCreditRating()` |  |
| 4377 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 4397 | 5 | `private void pushCostOfFundsToTheDebtMarket()` | Hands both lenders what money costs the bank, from the one struck figure. |
| 4412 | 5 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 4432 | 20 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 4453 | 39 | `private void finalUpdateEconomy()` |  |
| 4494 | 4 | `private void updateConstructionCost()` |  |
| 4505 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 4516 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 4520 | 4 | `public int getHouseholdCapacity()` |  |
| 4524 | 4 | `public int getStoreCapacity()` |  |
| 4528 | 3 | `public int[] getJobs()` |  |
| 4533 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 4546-5070)

| line | len | member | says |
|---:|---:|---|---|
| 4575 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 4611 | 1 | `public PopulationCohorts getCohorts()` |  |
| 4612 | 1 | `public FamilyModel getFamilies()` |  |
| 4613 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 4614 | 1 | `public Migration getMigration()` |  |
| 4615 | 1 | `public Health getHealth()` |  |
| 4616 | 1 | `public Healthcare getHealthcare()` |  |
| 4640 | 430 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 5071-5083)

| line | len | member | says |
|---:|---:|---|---|
| 5079 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 5082 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 5084-5336)

| line | len | member | says |
|---:|---:|---|---|
| 5100 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 5125 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 5151 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 5161 | 3 | `public BuildLog getBuildLog()` |  |
| 5168 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 5193 | 1 | `public Unemployment getUnemployment()` |  |
| 5197 | 1 | `public Sickness getSickness()` |  |
| 5205 | 1 | `public Crime getCrime()` |  |
| 5209 | 1 | `public double getLastOrphanDeaths()` |  |
| 5210 | 1 | `public double getLastUnhousedDeaths()` |  |
| 5212 | 5 | `{ ... }` |  |
| 5219 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 5244 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 5253 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 5268 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 5281 | 1 | `public double getStudentLoansLent()` |  |
| 5282 | 1 | `public double getStudentLoansRepaid()` |  |
| 5283 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 5286 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 5320 | 1 | `public Equity getEquity()` |  |
| 5324 | 1 | `public Exchange getExchange()` |  |
| 5326 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 5337-5360)

| line | len | member | says |
|---:|---:|---|---|
| 5347 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 5356 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 5359 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 5361-5615)

| line | len | member | says |
|---:|---:|---|---|
| 5373 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 5375 | 1 | `public PriceIndex getPriceIndex()` |  |
| 5377 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 5384 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 5391 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 5400 | 1 | `public Bank getBank()` |  |
| 5403 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 5407 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 5411 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 5478 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 5482 | 3 | `public EconomyManager getEconomyManager()` |  |
| 5485 | 3 | `public PopulationManager getPopulationManager()` |  |
| 5489 | 1 | `public LabourMarket getLabourMarket()` |  |
| 5490 | 1 | `public Education getEducation()` |  |
| 5506 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 5531 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 5573 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 5591 | 22 | `public void recordMonth()` | Files this month in the graph history. |
| 5614 | 1 | `public Inbox getInbox()` |  |
| 5615 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 5616-5929)

| line | len | member | says |
|---:|---:|---|---|
| 5627 | 3 | `public int getMonthsUntilAutosave()` |  |
| 5643 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 5655 | 178 | `public void save(int slot, String slotName)` |  |
| 5842 | 5 | `public String takeSkipFailure()` |  |
| 5851 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 5853 | 1 | `public GameFiles getGameFiles()` |  |
| 5866 | 7 | `public GameFiles.Result[] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade. |
| 5874 | 18 | `public void sendBuildingSave()` |  |
| 5895 | 16 | `public void loadBuildings()` |  |
| 5925 | 4 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 5930-6029)

| line | len | member | says |
|---:|---:|---|---|
| 5957 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 5958 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 5959 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 5966 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 5988 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 6027 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 6028 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 6030-6167)

| line | len | member | says |
|---:|---:|---|---|
| 6069 | 7 | `private void takeTreasuryMonth()` |  |
| 6078 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 6080 | 1 | `public double getTreasuryOpening()` |  |
| 6081 | 1 | `public double getTreasuryClosing()` |  |
| 6082 | 1 | `public double getTreasuryRaised()` |  |
| 6083 | 1 | `public double getTreasuryRepaid()` |  |
| 6084 | 1 | `public double getTreasurySurplus()` |  |
| 6087 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 6100 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain. |
| 6105 | 5 | `double[] treasuryMonthToSave()` |  |
| 6111 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 6145 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 6148 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 6150 | 1 | `public double getPostAuditDriftWorst()` |  |
| 6151 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 6152 | 4 | `public void InterestExpense(double amount)` |  |
| 6157 | 3 | `public DebtManager getDebtManager()` |  |
| 6162 | 4 | `public void printEndOfTurn()` |  |

### BUYING YOUR OWN DEBT BACK (lines 6168-6278)

| line | len | member | says |
|---:|---:|---|---|
| 6177 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now, at the standing rate. |
| 6185 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 6209 | 23 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |
| 6249 | 21 | `public DebtQuote issueEmergencyDebt(double cashNeeded, int duration)` | The build-funding bill: raises a stated amount of CASH, not face value. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 6279-7590)

| line | len | member | says |
|---:|---:|---|---|
| 6347 | 17 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 6365 | 268 | `private void rebuildSimulationState()` |  |
| 6675 | 872 | `public void loadGame(int slot)` | Load game |
| 7549 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 7591-7792)

| line | len | member | says |
|---:|---:|---|---|
| 7597 | 1 | `public Denomination getDenomination()` |  |
| 7600 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 7631 | 138 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 7780 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

