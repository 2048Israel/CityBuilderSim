# Game.java - 8,230 lines · 285 methods · 11 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Equity](Equity.md) (36), [AgeBand](AgeBand.md) (36), [GameFiles](GameFiles.md) (23), [DebtQuote](DebtQuote.md) (21), [CareType](CareType.md) (20), [BuildingsTemplate](BuildingsTemplate.md) (18), [FamilyModel](FamilyModel.md) (16), [HouseholdAccounts](HouseholdAccounts.md) (14), [GameLog](GameLog.md) (14), [JobType](JobType.md) (13), [BuildingType](BuildingType.md) (13), [DebtManager](DebtManager.md) (12), [Sector](Sector.md) (11), [TaxPolicy](TaxPolicy.md) (11), [Healthcare](Healthcare.md) (10), [PayTier](PayTier.md) (10), [Bank](Bank.md) (9), [Investor](Investor.md) (9), [Household](Household.md) (8), [SectorBooks](SectorBooks.md) (7), [LandManager](LandManager.md) (7), [MoneyAudit](MoneyAudit.md) (7), [BuildingManager](BuildingManager.md) (6), [HistorySave](HistorySave.md) (6), [BusinessInvestment](BusinessInvestment.md) (6), [Markets](Markets.md) (6), [MediumTermBond](MediumTermBond.md) (6), [GameVersion](GameVersion.md) (6), [DataSave](DataSave.md) (5), [Health](Health.md) (5)... and 52 more

**Used by (90):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 592 | THE FOUNDING RESERVE (2026-09-21) |
| 626 | THE CONSTRUCTION SUBSIDY |
| 650 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 859 | PRIVATE INVESTMENT |
| 1123 | · AND THE BALANCE SHEET |
| 1292 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1343 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1377 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 1657 | SHRINKING |
| 1799 | THE CONSTRUCTION WARNING |
| 1849 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 2769 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 3316 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 3746 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 3900 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 4063 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 4371 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 4489 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 4699 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 5265 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 5278 | THE READINGS THE MONTH TAKES OF THE CITY |
| 5549 | · the price of a place (2026-09-21) |
| 5650 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 5674 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 5929 | the save system |
| 6246 | PAYING THE WORLD BACK |
| 6346 | WHAT THE TREASURY ACTUALLY DID |
| 6551 | BUYING YOUR OWN DEBT BACK |
| 6666 | WHY LAND IS NOT IN THE RENT FLOOR |
| 7007 | · · the three the monthly path sets and this did not |
| 8026 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 2767 | `Game.BuildResult.SUCCESS` |  |
| 2767 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 2767 | `Game.BuildResult.NO_LAND` |  |
| 2767 | `Game.BuildResult.NO_DEPOSIT` |  |
| 2767 | `Game.BuildResult.NO_LICENCE` |  |
| 2767 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 384 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 618 | `Game.FOUNDING_CASH` | `2_500_000` | What the founders leave in the treasury, in thousands: D$2.5B, the endowment less the vault. |
| 621 | `Game.FOUNDING_RESERVE_USD` | `1_000_000` | What the founders leave in the vault, in thousands of US dollars: US$1B, bought on day one at the opening rate. |
| 624 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 2781 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 3369 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 4387 | `Game.EMERGENCY_NOTE_MONTHS` | `6` | Term of the note the city is forced into when it cannot pay its bills. |
| 4402 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 4411 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face. |
| 4419 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 5936 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 565 | `private String loadFailure` | Why the last load did not happen, or null. |
| 642 | `private double constructionSubsidy` |  |
| 677 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 678 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1310 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1313 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1348 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 1807 | `private int constructionShedMonth` |  |
| 1808 | `private double constructionShedPoints` |  |
| 1867 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 1887 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 1902 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 2342 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 2347 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 2513 | `public final int quantity` |  |
| 2514 | `public final double sticker` |  |
| 2515 | `public final double materialsNeeded` |  |
| 2517 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 2519 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 2520 | `public final double plantPrice` |  |
| 2521 | `public final double plantCost` |  |
| 2522 | `public final double materialsImported` |  |
| 2523 | `public final double materialsPrice` |  |
| 2524 | `public final double importCost` |  |
| 2525 | `public final double total` |  |
| 2526 | `public final double landNeeded` |  |
| 2527 | `public final double landFree` |  |
| 2529 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 2900 | `private final Investor government` | The city itself, as a payer. |
| 4692 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 4693 | `private LandManager landManager` |  |
| 4696 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 4697 | `private BuildLog buildLog` |  |
| 4724 | `private PopulationCohorts cohorts` |  |
| 4725 | `private FamilyModel families` |  |
| 4733 | `private Migration migration` |  |
| 4739 | `private LabourMarket labourMarket` | What labour costs. |
| 4745 | `private Education education` | The schools. |
| 4754 | `private Health health` | How much of the workforce is off sick. |
| 4762 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 5270 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 5416 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 5423 | `private HouseholdAccounts households` | The residents' own books. |
| 5432 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 5442 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 5446 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 5454 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 5458 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 5530 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 5536 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 5609 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 5619 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 5625 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 5632 | `private final Equity equity` | Who owns the city's companies. |
| 5636 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 5648 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 5656 | `private final Consumption consumption` |  |
| 5657 | `private boolean consumptionLoaded` |  |
| 5666 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 5684 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 5693 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 5694 | `private int rateHistoryFilled` |  |
| 5712 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 5729 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 5742 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 5756 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 5774 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 5777 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 5779 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 5780 | `private double carriedRetailCapacity` |  |
| 5781 | `private double carriedRetailWant` |  |
| 5784 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 5785 | `private java.util.Map<String, String> lastInvestment` |  |
| 5788 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 5832 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 5938 | `private int monthsSinceAutosave` |  |
| 6156 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 6165 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 6269 | `private double foreignDebtRaisedThisMonth` |  |
| 6270 | `private double foreignPrincipalRepaidThisMonth` |  |
| 6271 | `private double foreignInterestPaidThisMonth` |  |
| 6318 | `private double cityDebtRaisedThisMonth` | THE WORLD'S SIDE OF THE CITY'S DEBT, for MoneyAudit. |
| 6331 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 6332 | `private double cityPrincipalRepaidThisMonth` |  |
| 6341 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 6342 | `private double cityDiscountForBank` |  |
| 6388 | `private double treasuryOpening` |  |
| 6389 | `private double treasuryClosing` |  |
| 6390 | `private double treasuryRaised` |  |
| 6391 | `private double treasuryRepaid` |  |
| 6392 | `private double treasurySurplus` |  |
| 6393 | `private boolean treasuryRecorded` |  |
| 6412 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 6415 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 6512 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 6515 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 6523 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 6524 | `private String postAuditDriftPool` |  |
| 6525 | `private double postAuditDriftWorst` |  |
| 6664 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 8030 | `private final Denomination denomination` |  |
| 8223 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 8206 | **type** `public class Game` |  |
| 130 | 3 | `public Game()` |  |
| 142 | 4 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 167 | 172 | `private void buildWorld()` | Builds the entire simulation from nothing. |
| 340 | 4 | `public void run()` |  |
| 345 | 38 | `private void initialize()` |  |
| 386 | 4 | `static { ... }` |  |
| 393 | 14 | `public void newGame()` | buttons |
| 436 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 466 | 8 | `public void resumeGame()` |  |
| 474 | 51 | `public void loadGameSave(int slot)` |  |
| 534 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 542 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 555 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 557 | 6 | `public void toggleQuit()` |  |
| 567 | 1 | `public String getLoadFailure()` |  |
| 569 | 7 | `public void toggleGraphs()` |  |
| 576 | 5 | `public void toggleReports()` |  |
| 582 | 3 | `public void toggleNextMonth()` |  |
| 586 | 3 | `public int getMonth()` |  |
| 589 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 592-625)

### THE CONSTRUCTION SUBSIDY (lines 626-649)

| line | len | member | says |
|---:|---:|---|---|
| 644 | 1 | `public double getConstructionSubsidy()` |  |
| 646 | 3 | `public void setConstructionSubsidy(double monthlyAmount)` |  |

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 650-858)

| line | len | member | says |
|---:|---:|---|---|
| 680 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 681 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 683 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 684 | 1 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 687 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 688 | 1 | `public double getSubsidyPaid(String key)` |  |
| 690 | 5 | `public double getTotalSubsidyPaid()` |  |
| 697 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 710 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 713 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 726 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 736 | 17 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 761 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 778 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 784 | 4 | `public double getIncome()` |  |
| 789 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 793 | 3 | `public double getWaterRatio()` |  |
| 797 | 3 | `public double getRoadRatio()` |  |
| 802 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 807 | 3 | `public Markets getMarkets()` | Every goods market. |
| 812 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 816 | 3 | `public LandManager getLandManager()` |  |
| 821 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |
| 836 | 9 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action. |
| 847 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 851 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 855 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 859-1291)

| line | len | member | says |
|---:|---:|---|---|
| 869 | 60 | `private void runPrivateInvestment()` |  |
| 945 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 958 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 962 | 328 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1292-1342)

| line | len | member | says |
|---:|---:|---|---|
| 1316 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1319 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1322 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1323 | 1 | `public double getHouseholdCarImports()` |  |
| 1326 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1329 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1332 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1335 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1338 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1341 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1343-1376)

| line | len | member | says |
|---:|---:|---|---|
| 1351 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1354 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1357 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1360 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1363 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1366 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1369 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1372 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1375 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1377-1656)

| line | len | member | says |
|---:|---:|---|---|
| 1395 | 10 | `private void capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 1415 | 37 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 1460 | 47 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 1525 | 58 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 1599 | 9 | `public double recapitaliseBank(double amount)` | The treasury puts capital into its bank. |
| 1610 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now. |
| 1623 | 9 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 1642 | 8 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 1652 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 1657-1798)

| line | len | member | says |
|---:|---:|---|---|
| 1676 | 57 | `private void runRetirement()` |  |
| 1740 | 58 | `private int retire(BusinessInvestment.Decision decision, Investor seller)` | Scraps what the decision named, and sells the plot back to the city. |

### THE CONSTRUCTION WARNING (lines 1799-1848)

| line | len | member | says |
|---:|---:|---|---|
| 1819 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 1831 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 1840 | 1 | `public int getConstructionShedMonth()` |  |
| 1841 | 1 | `public double getConstructionShedPoints()` |  |
| 1844 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 1849-2768)

| line | len | member | says |
|---:|---:|---|---|
| 1869 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 1889 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 1897 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 1904 | 3 | `public double getLastWriteOff()` |  |
| 1943 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 1953 | 110 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |
| 2065 | 54 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 2121 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 2141 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 2170 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 2187 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 2224 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 2243 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 2274 | 60 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 2344 | 1 | `public double getCityMaintenancePaid()` |  |
| 2349 | 4 | `public int getConstructionMaterials()` |  |
| 2353 | 4 | `public double getInterestRate()` |  |
| 2357 | 3 | `public boolean isGraphsEnabled()` |  |
| 2360 | 3 | `public boolean isReportsEnabled()` |  |
| 2379 | 85 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early if the treasury is empty. |
| 2466 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 2495 | 1 | `public boolean hasNewReceipt()` |  |
| 2496 | 1 | `public void clearReceipt()` |  |
| 2498 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 2512 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 2531 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 2551 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 2561 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 2603 | 12 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 2626 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 2635 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 2650 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 2673 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 2767 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 2769-3315)

| line | len | member | says |
|---:|---:|---|---|
| 2784 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 2796 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 2809 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 2829 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 2846 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 2908 | 1 | `public Investor getGovernmentInvestor()` |  |
| 2910 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 2950 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 2955 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 2958 | 3 | `public double getTotalBuildingCost()` |  |
| 2961 | 3 | `public String getBuildingName()` |  |
| 2964 | 3 | `public int getBuildQuantity()` |  |
| 2969 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 2982 | 99 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 3089 | 14 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 3110 | 34 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 3146 | 14 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 3203 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 3215 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 3236 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 3265 | 32 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 3299 | 16 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 3316-4370)

| line | len | member | says |
|---:|---:|---|---|
| 3355 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 3382 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 3392 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 3395 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 3402 | 44 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 3453 | 69 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 3529 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 3542 | 3 | `private void printPopulationInfo()` | printers |
| 3547 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 3560 | 3 | `private void printUtilityInfo()` |  |
| 3564 | 3 | `private void printCityStats()` |  |
| 3571 | 548 | `private void nextMonth()` |  |
| 4120 | 222 | `private void startOfMonthUpdate()` |  |
| 4342 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 4371-4488)

| line | len | member | says |
|---:|---:|---|---|
| 4405 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 4430 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 4442 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 4462 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 4484 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 4489-4698)

| line | len | member | says |
|---:|---:|---|---|
| 4503 | 16 | `public String getCreditRating()` |  |
| 4521 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 4541 | 5 | `private void pushCostOfFundsToTheDebtMarket()` | Hands both lenders what money costs the bank, from the one struck figure. |
| 4556 | 5 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 4576 | 20 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 4597 | 48 | `private void finalUpdateEconomy()` |  |
| 4647 | 4 | `private void updateConstructionCost()` |  |
| 4658 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 4669 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 4673 | 4 | `public int getHouseholdCapacity()` |  |
| 4677 | 4 | `public int getStoreCapacity()` |  |
| 4681 | 3 | `public int[] getJobs()` |  |
| 4686 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 4699-5264)

| line | len | member | says |
|---:|---:|---|---|
| 4728 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 4764 | 1 | `public PopulationCohorts getCohorts()` |  |
| 4765 | 1 | `public FamilyModel getFamilies()` |  |
| 4766 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 4767 | 1 | `public Migration getMigration()` |  |
| 4768 | 1 | `public Health getHealth()` |  |
| 4769 | 1 | `public Healthcare getHealthcare()` |  |
| 4793 | 471 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 5265-5277)

| line | len | member | says |
|---:|---:|---|---|
| 5273 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 5276 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 5278-5548)

| line | len | member | says |
|---:|---:|---|---|
| 5294 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 5313 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 5333 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 5375 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 5401 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 5411 | 3 | `public BuildLog getBuildLog()` |  |
| 5418 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 5443 | 1 | `public Unemployment getUnemployment()` |  |
| 5447 | 1 | `public Sickness getSickness()` |  |
| 5455 | 1 | `public Crime getCrime()` |  |
| 5459 | 1 | `public double getLastOrphanDeaths()` |  |
| 5460 | 1 | `public double getLastUnhousedDeaths()` |  |
| 5462 | 5 | `{ ... }` |  |
| 5469 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 5494 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 5503 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 5518 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 5531 | 1 | `public double getStudentLoansLent()` |  |
| 5532 | 1 | `public double getStudentLoansRepaid()` |  |
| 5533 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 5537 | 1 | `public double getStudentLoanInterest()` |  |
| 5540 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 5547 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 5549-5649)

| line | len | member | says |
|---:|---:|---|---|
| 5564 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 5570 | 5 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 5577 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 5589 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 5633 | 1 | `public Equity getEquity()` |  |
| 5637 | 1 | `public Exchange getExchange()` |  |
| 5639 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 5650-5673)

| line | len | member | says |
|---:|---:|---|---|
| 5660 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 5669 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 5672 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 5674-5928)

| line | len | member | says |
|---:|---:|---|---|
| 5686 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 5688 | 1 | `public PriceIndex getPriceIndex()` |  |
| 5690 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 5697 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 5704 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 5713 | 1 | `public Bank getBank()` |  |
| 5716 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 5720 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 5724 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 5791 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 5795 | 3 | `public EconomyManager getEconomyManager()` |  |
| 5798 | 3 | `public PopulationManager getPopulationManager()` |  |
| 5802 | 1 | `public LabourMarket getLabourMarket()` |  |
| 5803 | 1 | `public Education getEducation()` |  |
| 5819 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 5844 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 5886 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 5904 | 22 | `public void recordMonth()` | Files this month in the graph history. |
| 5927 | 1 | `public Inbox getInbox()` |  |
| 5928 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 5929-6245)

| line | len | member | says |
|---:|---:|---|---|
| 5940 | 3 | `public int getMonthsUntilAutosave()` |  |
| 5956 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 5968 | 181 | `public void save(int slot, String slotName)` |  |
| 6158 | 5 | `public String takeSkipFailure()` |  |
| 6167 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 6169 | 1 | `public GameFiles getGameFiles()` |  |
| 6182 | 7 | `public GameFiles.Result[] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade. |
| 6190 | 18 | `public void sendBuildingSave()` |  |
| 6211 | 16 | `public void loadBuildings()` |  |
| 6241 | 4 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 6246-6345)

| line | len | member | says |
|---:|---:|---|---|
| 6273 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 6274 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 6275 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 6282 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 6304 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 6343 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 6344 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 6346-6550)

| line | len | member | says |
|---:|---:|---|---|
| 6417 | 13 | `private void takeTreasuryMonth()` |  |
| 6432 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 6434 | 1 | `public double getTreasuryOpening()` |  |
| 6435 | 1 | `public double getTreasuryClosing()` |  |
| 6436 | 1 | `public double getTreasuryRaised()` |  |
| 6437 | 1 | `public double getTreasuryRepaid()` |  |
| 6438 | 1 | `public double getTreasurySurplus()` |  |
| 6441 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 6459 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 6471 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 6476 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 6484 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |
| 6488 | 5 | `double[] treasuryMonthToSave()` |  |
| 6494 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 6528 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 6531 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 6533 | 1 | `public double getPostAuditDriftWorst()` |  |
| 6534 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 6535 | 4 | `public void InterestExpense(double amount)` |  |
| 6540 | 3 | `public DebtManager getDebtManager()` |  |
| 6545 | 4 | `public void printEndOfTurn()` |  |

### BUYING YOUR OWN DEBT BACK (lines 6551-6665)

| line | len | member | says |
|---:|---:|---|---|
| 6560 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now, at the standing rate. |
| 6568 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 6592 | 26 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |
| 6635 | 22 | `public DebtQuote issueEmergencyDebt(double cashNeeded, int duration)` | The build-funding bill: raises a stated amount of CASH, not face value. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 6666-8025)

| line | len | member | says |
|---:|---:|---|---|
| 6734 | 17 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 6752 | 292 | `private void rebuildSimulationState()` |  |
| 7086 | 896 | `public void loadGame(int slot)` | Load game |
| 7984 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 8026-8230)

| line | len | member | says |
|---:|---:|---|---|
| 8032 | 1 | `public Denomination getDenomination()` |  |
| 8035 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 8066 | 141 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 8218 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

