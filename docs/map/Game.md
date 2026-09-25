# Game.java - 10,459 lines · 364 methods · 14 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [TreasuryLine](TreasuryLine.md) (42), [AgeBand](AgeBand.md) (36), [Equity](Equity.md) (33), [GameFiles](GameFiles.md) (24), [DebtQuote](DebtQuote.md) (24), [BuildingsTemplate](BuildingsTemplate.md) (23), [CareType](CareType.md) (20), [Debt](Debt.md) (19), [BusinessDebtManager](BusinessDebtManager.md) (17), [FamilyModel](FamilyModel.md) (16), [GameLog](GameLog.md) (16), [Founding](Founding.md) (14), [HouseholdAccounts](HouseholdAccounts.md) (14), [BuildingType](BuildingType.md) (14), [JobType](JobType.md) (13), [DebtManager](DebtManager.md) (13), [Sector](Sector.md) (13), [Investor](Investor.md) (13), [TaxPolicy](TaxPolicy.md) (12), [LongTermBond](LongTermBond.md) (11), [Healthcare](Healthcare.md) (10), [BusinessInvestment](BusinessInvestment.md) (10), [PayTier](PayTier.md) (10), [Bank](Bank.md) (10), [Household](Household.md) (9), [BuildingManager](BuildingManager.md) (8), [SectorBooks](SectorBooks.md) (8), [LandManager](LandManager.md) (7), [Mortgage](Mortgage.md) (7), [MediumTermBond](MediumTermBond.md) (7)... and 58 more

**Used by (97):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 707 | THE FOUNDING RESERVE (2026-09-21) |
| 763 | · THE FOUNDING RECORD (0.7.10) |
| 816 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 839 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1043 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1186 | PRIVATE INVESTMENT |
| 1500 | · AND THE BALANCE SHEET |
| 1692 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1743 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1777 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 2193 | SHRINKING |
| 2383 | THE CONSTRUCTION WARNING |
| 2433 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 2731 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 2888 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 3696 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 4307 | · A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) |
| 4391 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 4765 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 5008 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 5194 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 5374 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 5698 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 5831 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 6082 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 6663 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 6676 | THE READINGS THE MONTH TAKES OF THE CITY |
| 6965 | · the price of a place (2026-09-21) |
| 7066 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 7090 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 7386 | the save system |
| 7747 | PAYING THE WORLD BACK |
| 7888 | THE HOLDERS ARE PAID (0.7.1) |
| 7964 | · the desk, for the households |
| 7990 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 8043 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 8105 | · a buyback's holders outside the pools |
| 8127 | WHAT THE TREASURY ACTUALLY DID |
| 8332 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 8574 | BUYING YOUR OWN DEBT BACK |
| 8699 | WHY LAND IS NOT IN THE RENT FLOOR |
| 9044 | · · the three the monthly path sets and this did not |
| 10234 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 3694 | `Game.BuildResult.SUCCESS` |  |
| 3694 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 3694 | `Game.BuildResult.NO_LAND` |  |
| 3694 | `Game.BuildResult.NO_DEPOSIT` |  |
| 3694 | `Game.BuildResult.NO_LICENCE` |  |
| 3694 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 482 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 755 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 758 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 761 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 3708 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 4447 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 4602 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 5714 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 5726 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 5729 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 5744 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 5753 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face. |
| 5761 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 7393 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 677 | `private String loadFailure` | Why the last load did not happen, or null. |
| 775 | `private Founding founding` |  |
| 805 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 874 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 875 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1081 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1165 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under the toggle, and a short vault says here that the rest was converted. |
| 1166 | `private int lastLandReceiptMonth` |  |
| 1710 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1713 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1748 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 2304 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 2391 | `private int constructionShedMonth` |  |
| 2392 | `private double constructionShedPoints` |  |
| 2451 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 2464 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 2484 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 2499 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 2847 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 2848 | `private final java.util.Set<String> heldForDownPayment` |  |
| 2927 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 2928 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 2929 | `private double salvageUsedThisMonth` |  |
| 3261 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 3266 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 3435 | `public final int quantity` |  |
| 3436 | `public final double sticker` |  |
| 3437 | `public final double materialsNeeded` |  |
| 3439 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 3441 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 3442 | `public final double plantPrice` |  |
| 3443 | `public final double plantCost` |  |
| 3444 | `public final double materialsImported` |  |
| 3445 | `public final double materialsPrice` |  |
| 3446 | `public final double importCost` |  |
| 3447 | `public final double total` |  |
| 3448 | `public final double landNeeded` |  |
| 3449 | `public final double landFree` |  |
| 3451 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 3827 | `private final Investor government` | The city itself, as a payer. |
| 6075 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 6076 | `private LandManager landManager` |  |
| 6079 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 6080 | `private BuildLog buildLog` |  |
| 6107 | `private PopulationCohorts cohorts` |  |
| 6108 | `private FamilyModel families` |  |
| 6116 | `private Migration migration` |  |
| 6122 | `private LabourMarket labourMarket` | What labour costs. |
| 6128 | `private Education education` | The schools. |
| 6137 | `private Health health` | How much of the workforce is off sick. |
| 6145 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 6668 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 6814 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 6821 | `private HouseholdAccounts households` | The residents' own books. |
| 6830 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 6840 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 6844 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 6852 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 6856 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 6946 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 6952 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 7025 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 7035 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 7041 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 7048 | `private final Equity equity` | Who owns the city's companies. |
| 7052 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 7064 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 7072 | `private final Consumption consumption` |  |
| 7073 | `private boolean consumptionLoaded` |  |
| 7082 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 7100 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 7109 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 7110 | `private int rateHistoryFilled` |  |
| 7128 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 7141 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 7185 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 7198 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 7212 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 7230 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 7233 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 7235 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 7236 | `private double carriedRetailCapacity` |  |
| 7237 | `private double carriedRetailWant` |  |
| 7240 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 7241 | `private java.util.Map<String, String> lastInvestment` |  |
| 7244 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 7288 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 7395 | `private int monthsSinceAutosave` |  |
| 7652 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 7661 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 7770 | `private double foreignDebtRaisedThisMonth` |  |
| 7771 | `private double foreignPrincipalRepaidThisMonth` |  |
| 7772 | `private double foreignInterestPaidThisMonth` |  |
| 7824 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 7844 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 7845 | `private double cityPrincipalRepaidThisMonth` |  |
| 7859 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 7860 | `private double cityDiscountForBank` |  |
| 7863 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 7880 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 7886 | `private double bankPrincipalRepaidThisMonth` |  |
| 7914 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 8005 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 8115 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 8117 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 8169 | `private double treasuryOpening` |  |
| 8170 | `private double treasuryClosing` |  |
| 8171 | `private double treasuryRaised` |  |
| 8172 | `private double treasuryRepaid` |  |
| 8173 | `private double treasurySurplus` |  |
| 8174 | `private boolean treasuryRecorded` |  |
| 8193 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 8196 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 8293 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 8296 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 8304 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 8305 | `private String postAuditDriftPool` |  |
| 8306 | `private double postAuditDriftWorst` |  |
| 8370 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 8373 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 8376 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 8697 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 10189 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 10238 | `private final Denomination denomination` |  |
| 10452 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 10435 | **type** `public class Game` |  |
| 130 | 3 | `public Game()` |  |
| 142 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 151 | 4 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 157 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 189 | 248 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 438 | 4 | `public void run()` |  |
| 443 | 38 | `private void initialize()` |  |
| 484 | 4 | `static { ... }` |  |
| 492 | 3 | `public void newGame()` | A new city on the defaults: "Found with defaults". |
| 501 | 14 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 545 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 575 | 8 | `public void resumeGame()` |  |
| 583 | 54 | `public void loadGameSave(int slot)` |  |
| 646 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 654 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 667 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 669 | 6 | `public void toggleQuit()` |  |
| 679 | 1 | `public String getLoadFailure()` |  |
| 681 | 7 | `public void toggleGraphs()` |  |
| 688 | 5 | `public void toggleReports()` |  |
| 694 | 3 | `public void toggleNextMonth()` |  |
| 699 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 701 | 3 | `public int getMonth()` |  |
| 704 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 707-762)

### THE FOUNDING RECORD (0.7.10) (lines 763-815)

| line | len | member | says |
|---:|---:|---|---|
| 778 | 4 | `public Founding getFounding()` | How this city was founded. |
| 784 | 1 | `public String getCityName()` | The city's name. |
| 787 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 790 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 793 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 796 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 812 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset. |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 816-838)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 839-1042)

| line | len | member | says |
|---:|---:|---|---|
| 877 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 878 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 880 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 881 | 1 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 884 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 885 | 1 | `public double getSubsidyPaid(String key)` |  |
| 887 | 5 | `public double getTotalSubsidyPaid()` |  |
| 894 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 907 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 910 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 929 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 939 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 974 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 991 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 997 | 4 | `public double getIncome()` |  |
| 1002 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1006 | 3 | `public double getWaterRatio()` |  |
| 1010 | 3 | `public double getRoadRatio()` |  |
| 1015 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1020 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1025 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1029 | 3 | `public LandManager getLandManager()` |  |
| 1034 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1043-1185)

| line | len | member | says |
|---:|---:|---|---|
| 1084 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1087 | 1 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1097 | 42 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1147 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1154 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - for the land office's buttons. |
| 1169 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1174 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 1178 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1182 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 1186-1691)

| line | len | member | says |
|---:|---:|---|---|
| 1196 | 87 | `private void runPrivateInvestment()` |  |
| 1299 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 1312 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 1324 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 1330 | 360 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1692-1742)

| line | len | member | says |
|---:|---:|---|---|
| 1716 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1719 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1722 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1723 | 1 | `public double getHouseholdCarImports()` |  |
| 1726 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1729 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1732 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1735 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1738 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1741 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1743-1776)

| line | len | member | says |
|---:|---:|---|---|
| 1751 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1754 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1757 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1760 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1763 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1766 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1769 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1772 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1775 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1777-2192)

| line | len | member | says |
|---:|---:|---|---|
| 1795 | 10 | `private void capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 1812 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 1842 | 41 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 1891 | 50 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 1959 | 82 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 2050 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 2077 | 8 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 2097 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |
| 2124 | 9 | `public double recapitaliseBank(double amount)` | The treasury puts capital into its bank. |
| 2135 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now. |
| 2145 | 4 | `public boolean canRecapitaliseBank()` | True when the bank needs capital and the treasury holds all of it: the rescue button's guard (0.7.9), here so the screen and anything else that offers the rescue cannot disagree about it. |
| 2159 | 9 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 2178 | 8 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 2188 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 2193-2382)

| line | len | member | says |
|---:|---:|---|---|
| 2212 | 69 | `private void runRetirement()` |  |
| 2288 | 14 | `private void closeBranch()` | Closes one of the bank's branches (0.7.11, round 2): the building retired by the path any retired building takes (retire()), sold by its owner, retail. |
| 2305 | 1 | `public int getBranchesClosed()` |  |
| 2318 | 64 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 2383-2432)

| line | len | member | says |
|---:|---:|---|---|
| 2403 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 2415 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 2424 | 1 | `public int getConstructionShedMonth()` |  |
| 2425 | 1 | `public double getConstructionShedPoints()` |  |
| 2428 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 2433-2730)

| line | len | member | says |
|---:|---:|---|---|
| 2453 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 2466 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 2486 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 2494 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 2501 | 3 | `public double getLastWriteOff()` |  |
| 2540 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 2550 | 180 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 2731-2887)

| line | len | member | says |
|---:|---:|---|---|
| 2770 | 5 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 2784 | 57 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, scanning down from what the planner asked for as consider() does - and the advisor's ... |
| 2850 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 2854 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 2867 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 2888-3695)

| line | len | member | says |
|---:|---:|---|---|
| 2924 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 2932 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 2937 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 2942 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 2945 | 20 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |
| 2967 | 60 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 3029 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 3049 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 3078 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 3095 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 3132 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 3151 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 3182 | 71 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 3263 | 1 | `public double getCityMaintenancePaid()` |  |
| 3268 | 4 | `public int getConstructionMaterials()` |  |
| 3272 | 4 | `public double getInterestRate()` |  |
| 3276 | 3 | `public boolean isGraphsEnabled()` |  |
| 3279 | 3 | `public boolean isReportsEnabled()` |  |
| 3298 | 88 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early if the treasury is empty. |
| 3388 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 3417 | 1 | `public boolean hasNewReceipt()` |  |
| 3418 | 1 | `public void clearReceipt()` |  |
| 3420 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 3434 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 3453 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 3473 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 3483 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 3525 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 3553 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 3562 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 3577 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 3600 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 3694 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 3696-4306)

| line | len | member | says |
|---:|---:|---|---|
| 3711 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 3723 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 3736 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 3756 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 3773 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 3835 | 1 | `public Investor getGovernmentInvestor()` |  |
| 3837 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 3877 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 3886 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |
| 3891 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 3894 | 3 | `public double getTotalBuildingCost()` |  |
| 3897 | 3 | `public String getBuildingName()` |  |
| 3900 | 3 | `public int getBuildQuantity()` |  |
| 3905 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 3918 | 111 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 4037 | 15 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 4059 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 4097 | 15 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 4155 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 4167 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 4188 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 4217 | 35 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 4258 | 5 | `private double longBondNetProceeds(double faceValue, double marketRate, int duration)` | What the city actually banks for a long bond of this face: its worth at the rate struck, less the fees, to the cent. |
| 4265 | 13 | `private DebtQuote longBondQuote(double requested, int duration, double marketRate, double before, double faceValue, double rece...` | A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. |
| 4280 | 7 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |
| 4292 | 14 | `private String issueLongBond(DebtQuote quote)` | The booking both long-bond issues share. |

### A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) (lines 4307-4390)

| line | len | member | says |
|---:|---:|---|---|
| 4335 | 8 | `private double longBondFaceForProceeds(double cashNeeded, int duration, double rounding, double marketRate)` | Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. |
| 4352 | 29 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a term bond whose CASH covers cashNeeded would cost. |
| 4383 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 4391-5697)

| line | len | member | says |
|---:|---:|---|---|
| 4433 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 4469 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 4484 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation, less the world's base rate less the world's realised inflation - the one definition the month hands the currenc... |
| 4496 | 3 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the year's inflation, the same PriceIndex.inflation() the real rate differential and the parity read - the one definition the month strikes the ho... |
| 4508 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 4513 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 4516 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 4523 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 4590 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 4610 | 70 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 4687 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |
| 4700 | 3 | `private void printPopulationInfo()` | printers |
| 4705 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 4718 | 3 | `private void printUtilityInfo()` |  |
| 4722 | 3 | `private void printCityStats()` |  |
| 4729 | 701 | `private void nextMonth()` |  |
| 5431 | 238 | `private void startOfMonthUpdate()` |  |
| 5669 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 5698-5830)

| line | len | member | says |
|---:|---:|---|---|
| 5747 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 5772 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 5784 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 5804 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 5826 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 5831-6081)

| line | len | member | says |
|---:|---:|---|---|
| 5845 | 16 | `public String getCreditRating()` |  |
| 5863 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 5886 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 5900 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 5922 | 27 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 5950 | 78 | `private void finalUpdateEconomy()` |  |
| 6030 | 4 | `private void updateConstructionCost()` |  |
| 6041 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 6052 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 6056 | 4 | `public int getHouseholdCapacity()` |  |
| 6060 | 4 | `public int getStoreCapacity()` |  |
| 6064 | 3 | `public int[] getJobs()` |  |
| 6069 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 6082-6662)

| line | len | member | says |
|---:|---:|---|---|
| 6111 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 6147 | 1 | `public PopulationCohorts getCohorts()` |  |
| 6148 | 1 | `public FamilyModel getFamilies()` |  |
| 6149 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 6150 | 1 | `public Migration getMigration()` |  |
| 6151 | 1 | `public Health getHealth()` |  |
| 6152 | 1 | `public Healthcare getHealthcare()` |  |
| 6176 | 486 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 6663-6675)

| line | len | member | says |
|---:|---:|---|---|
| 6671 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 6674 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 6676-6964)

| line | len | member | says |
|---:|---:|---|---|
| 6692 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 6711 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 6731 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 6773 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 6799 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 6809 | 3 | `public BuildLog getBuildLog()` |  |
| 6816 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 6841 | 1 | `public Unemployment getUnemployment()` |  |
| 6845 | 1 | `public Sickness getSickness()` |  |
| 6853 | 1 | `public Crime getCrime()` |  |
| 6857 | 1 | `public double getLastOrphanDeaths()` |  |
| 6858 | 1 | `public double getLastUnhousedDeaths()` |  |
| 6860 | 6 | `{ ... }` |  |
| 6868 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 6893 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 6902 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 6923 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 6934 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 6947 | 1 | `public double getStudentLoansLent()` |  |
| 6948 | 1 | `public double getStudentLoansRepaid()` |  |
| 6949 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 6953 | 1 | `public double getStudentLoanInterest()` |  |
| 6956 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 6963 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 6965-7065)

| line | len | member | says |
|---:|---:|---|---|
| 6980 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 6986 | 5 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 6993 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 7005 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 7049 | 1 | `public Equity getEquity()` |  |
| 7053 | 1 | `public Exchange getExchange()` |  |
| 7055 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 7066-7089)

| line | len | member | says |
|---:|---:|---|---|
| 7076 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 7085 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 7088 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 7090-7385)

| line | len | member | says |
|---:|---:|---|---|
| 7102 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 7104 | 1 | `public PriceIndex getPriceIndex()` |  |
| 7106 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 7113 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 7120 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 7129 | 1 | `public Bank getBank()` |  |
| 7142 | 1 | `public CentralBank getCentralBank()` |  |
| 7156 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 7162 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 7165 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 7172 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 7176 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 7180 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 7247 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 7251 | 3 | `public EconomyManager getEconomyManager()` |  |
| 7254 | 3 | `public PopulationManager getPopulationManager()` |  |
| 7258 | 1 | `public LabourMarket getLabourMarket()` |  |
| 7259 | 1 | `public Education getEducation()` |  |
| 7275 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 7300 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 7342 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 7360 | 23 | `public void recordMonth()` | Files this month in the graph history. |
| 7384 | 1 | `public Inbox getInbox()` |  |
| 7385 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 7386-7746)

| line | len | member | says |
|---:|---:|---|---|
| 7397 | 3 | `public int getMonthsUntilAutosave()` |  |
| 7413 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 7425 | 220 | `public void save(int slot, String slotName)` |  |
| 7654 | 5 | `public String takeSkipFailure()` |  |
| 7663 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 7665 | 1 | `public GameFiles getGameFiles()` |  |
| 7678 | 7 | `public GameFiles.Result[] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade. |
| 7686 | 18 | `public void sendBuildingSave()` |  |
| 7707 | 16 | `public void loadBuildings()` |  |
| 7737 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 7747-7887)

| line | len | member | says |
|---:|---:|---|---|
| 7774 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 7775 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 7776 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 7783 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 7805 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 7872 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 7881 | 1 | `public double getCityPaperSettled()` |  |
| 7882 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 7883 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 7885 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 7888-7963)

| line | len | member | says |
|---:|---:|---|---|
| 7916 | 1 | `public double getCouponsToHouseholds()` |  |
| 7917 | 1 | `public double getPrincipalToHouseholds()` |  |
| 7918 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 7929 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 7937 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 7950 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 7964-7989)

| line | len | member | says |
|---:|---:|---|---|
| 7974 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 7990-8042)

| line | len | member | says |
|---:|---:|---|---|
| 8007 | 35 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 8043-8104)

| line | len | member | says |
|---:|---:|---|---|
| 8062 | 42 | `private void openMarketOperation()` |  |

### a buyback's holders outside the pools (lines 8105-8126)

| line | len | member | says |
|---:|---:|---|---|
| 8120 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 8124 | 1 | `public double getBuybackToHouseholds()` |  |
| 8125 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 8127-8331)

| line | len | member | says |
|---:|---:|---|---|
| 8198 | 13 | `private void takeTreasuryMonth()` |  |
| 8213 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 8215 | 1 | `public double getTreasuryOpening()` |  |
| 8216 | 1 | `public double getTreasuryClosing()` |  |
| 8217 | 1 | `public double getTreasuryRaised()` |  |
| 8218 | 1 | `public double getTreasuryRepaid()` |  |
| 8219 | 1 | `public double getTreasurySurplus()` |  |
| 8222 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 8240 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 8252 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 8257 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 8265 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |
| 8269 | 5 | `double[] treasuryMonthToSave()` |  |
| 8275 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 8309 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 8312 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 8314 | 1 | `public double getPostAuditDriftWorst()` |  |
| 8315 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 8316 | 4 | `public void InterestExpense(double amount)` |  |
| 8321 | 3 | `public DebtManager getDebtManager()` |  |
| 8326 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 8332-8573)

| line | len | member | says |
|---:|---:|---|---|
| 8392 | 26 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 8430 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 8435 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 8457 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 8469 | 17 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 8495 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 8520 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 8527 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 8531 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 8541 | 7 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 8550 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 8553 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 8560 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 8569 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 8570 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 8571 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 8572 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 8574-8698)

| line | len | member | says |
|---:|---:|---|---|
| 8587 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 8595 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 8620 | 70 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 8699-10233)

| line | len | member | says |
|---:|---:|---|---|
| 8767 | 17 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 8785 | 296 | `private void rebuildSimulationState()` |  |
| 9123 | 1064 | `public void loadGame(int slot)` | Load game |
| 10192 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 10234-10459)

| line | len | member | says |
|---:|---:|---|---|
| 10240 | 1 | `public Denomination getDenomination()` |  |
| 10243 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 10274 | 162 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 10447 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

