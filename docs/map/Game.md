# Game.java - 11,365 lines · 403 methods · 15 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [TreasuryLine](TreasuryLine.md) (42), [Equity](Equity.md) (38), [AgeBand](AgeBand.md) (36), [DebtQuote](DebtQuote.md) (34), [BusinessDebtManager](BusinessDebtManager.md) (32), [GameFiles](GameFiles.md) (24), [Rollover](Rollover.md) (23), [BuildingsTemplate](BuildingsTemplate.md) (23), [Debt](Debt.md) (22), [GameLog](GameLog.md) (21), [CareType](CareType.md) (20), [Bank](Bank.md) (17), [FamilyModel](FamilyModel.md) (16), [DebtManager](DebtManager.md) (14), [Founding](Founding.md) (14), [HouseholdAccounts](HouseholdAccounts.md) (14), [Sector](Sector.md) (14), [Investor](Investor.md) (14), [BuildingType](BuildingType.md) (14), [JobType](JobType.md) (13), [LongTermBond](LongTermBond.md) (13), [LandParcel](LandParcel.md) (12), [TaxPolicy](TaxPolicy.md) (12), [Healthcare](Healthcare.md) (10), [BusinessInvestment](BusinessInvestment.md) (10), [LandManager](LandManager.md) (10), [PayTier](PayTier.md) (10), [Household](Household.md) (9), [BuildingManager](BuildingManager.md) (8), [SectorBooks](SectorBooks.md) (8)... and 62 more

**Used by (102):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 740 | THE FOUNDING RESERVE (2026-09-21) |
| 796 | · THE FOUNDING RECORD (0.7.10) |
| 849 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 872 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1076 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1192 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 1357 | PRIVATE INVESTMENT |
| 1675 | · AND THE BALANCE SHEET |
| 1867 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1918 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1952 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 2529 | SHRINKING |
| 2719 | THE CONSTRUCTION WARNING |
| 2769 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 3097 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 3254 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 3332 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 4152 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 4774 | · A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) |
| 4858 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 5165 | · THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 5240 | ROLLING WHAT FALLS DUE (0.7.13) |
| 5559 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 5806 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 6003 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 6183 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 6516 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 6649 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 6900 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 7481 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 7494 | THE READINGS THE MONTH TAKES OF THE CITY |
| 7783 | · the price of a place (2026-09-21) |
| 7906 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 7930 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 8226 | the save system |
| 8608 | PAYING THE WORLD BACK |
| 8749 | THE HOLDERS ARE PAID (0.7.1) |
| 8825 | · the desk, for the households |
| 8851 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 8904 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 8966 | · a buyback's holders outside the pools |
| 8988 | WHAT THE TREASURY ACTUALLY DID |
| 9193 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 9435 | BUYING YOUR OWN DEBT BACK |
| 9560 | WHY LAND IS NOT IN THE RENT FLOOR |
| 9905 | · · the three the monthly path sets and this did not |
| 11137 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 4150 | `Game.BuildResult.SUCCESS` |  |
| 4150 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 4150 | `Game.BuildResult.NO_LAND` |  |
| 4150 | `Game.BuildResult.NO_DEPOSIT` |  |
| 4150 | `Game.BuildResult.NO_LICENCE` |  |
| 4150 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 498 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 788 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 791 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 794 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 4164 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 4914 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 5069 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 5183 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 6532 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 6544 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 6547 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 6562 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 6571 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, WHAT AN ISSUE COSTS). |
| 6579 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 8233 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 710 | `private String loadFailure` | Why the last load did not happen, or null. |
| 808 | `private Founding founding` |  |
| 838 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 907 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 908 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1114 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1336 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under the toggle, and a short vault says here that the rest was converted. |
| 1337 | `private int lastLandReceiptMonth` |  |
| 1885 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1888 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1923 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 2157 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 2640 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 2727 | `private int constructionShedMonth` |  |
| 2728 | `private double constructionShedPoints` |  |
| 2787 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 2800 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 2820 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 2835 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 3213 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 3214 | `private final java.util.Set<String> heldForDownPayment` |  |
| 3293 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 3294 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 3295 | `private double salvageUsedThisMonth` |  |
| 3352 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 3717 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 3722 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 3891 | `public final int quantity` |  |
| 3892 | `public final double sticker` |  |
| 3893 | `public final double materialsNeeded` |  |
| 3895 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 3897 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 3898 | `public final double plantPrice` |  |
| 3899 | `public final double plantCost` |  |
| 3900 | `public final double materialsImported` |  |
| 3901 | `public final double materialsPrice` |  |
| 3902 | `public final double importCost` |  |
| 3903 | `public final double total` |  |
| 3904 | `public final double landNeeded` |  |
| 3905 | `public final double landFree` |  |
| 3907 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 4283 | `private final Investor government` | The city itself, as a payer. |
| 5300 | `private final Rollover rollover` |  |
| 6893 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 6894 | `private LandManager landManager` |  |
| 6897 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 6898 | `private BuildLog buildLog` |  |
| 6925 | `private PopulationCohorts cohorts` |  |
| 6926 | `private FamilyModel families` |  |
| 6934 | `private Migration migration` |  |
| 6940 | `private LabourMarket labourMarket` | What labour costs. |
| 6946 | `private Education education` | The schools. |
| 6955 | `private Health health` | How much of the workforce is off sick. |
| 6963 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 7486 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 7632 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 7639 | `private HouseholdAccounts households` | The residents' own books. |
| 7648 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 7658 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 7662 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 7670 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 7674 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 7764 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 7770 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 7843 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 7853 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 7859 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 7866 | `private final Equity equity` | Who owns the city's companies. |
| 7870 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 7878 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 7882 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 7904 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 7912 | `private final Consumption consumption` |  |
| 7913 | `private boolean consumptionLoaded` |  |
| 7922 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 7940 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 7949 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 7950 | `private int rateHistoryFilled` |  |
| 7968 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 7981 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 8025 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 8038 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 8052 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 8070 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 8073 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 8075 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 8076 | `private double carriedRetailCapacity` |  |
| 8077 | `private double carriedRetailWant` |  |
| 8080 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 8081 | `private java.util.Map<String, String> lastInvestment` |  |
| 8084 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 8128 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 8235 | `private int monthsSinceAutosave` |  |
| 8513 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 8522 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 8631 | `private double foreignDebtRaisedThisMonth` |  |
| 8632 | `private double foreignPrincipalRepaidThisMonth` |  |
| 8633 | `private double foreignInterestPaidThisMonth` |  |
| 8685 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 8705 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 8706 | `private double cityPrincipalRepaidThisMonth` |  |
| 8720 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 8721 | `private double cityDiscountForBank` |  |
| 8724 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 8741 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 8747 | `private double bankPrincipalRepaidThisMonth` |  |
| 8775 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 8866 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 8976 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 8978 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 9030 | `private double treasuryOpening` |  |
| 9031 | `private double treasuryClosing` |  |
| 9032 | `private double treasuryRaised` |  |
| 9033 | `private double treasuryRepaid` |  |
| 9034 | `private double treasurySurplus` |  |
| 9035 | `private boolean treasuryRecorded` |  |
| 9054 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 9057 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 9154 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 9157 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 9165 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 9166 | `private String postAuditDriftPool` |  |
| 9167 | `private double postAuditDriftWorst` |  |
| 9231 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 9234 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 9237 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 9558 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 11092 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 11141 | `private final Denomination denomination` |  |
| 11358 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 11341 | **type** `public class Game` |  |
| 130 | 3 | `public Game()` |  |
| 142 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 151 | 4 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 157 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 189 | 264 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 454 | 4 | `public void run()` |  |
| 459 | 38 | `private void initialize()` |  |
| 500 | 4 | `static { ... }` |  |
| 508 | 3 | `public void newGame()` | A new city on the defaults: "Found with defaults". |
| 517 | 31 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 578 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 608 | 8 | `public void resumeGame()` |  |
| 616 | 54 | `public void loadGameSave(int slot)` |  |
| 679 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 687 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 700 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 702 | 6 | `public void toggleQuit()` |  |
| 712 | 1 | `public String getLoadFailure()` |  |
| 714 | 7 | `public void toggleGraphs()` |  |
| 721 | 5 | `public void toggleReports()` |  |
| 727 | 3 | `public void toggleNextMonth()` |  |
| 732 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 734 | 3 | `public int getMonth()` |  |
| 737 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 740-795)

### THE FOUNDING RECORD (0.7.10) (lines 796-848)

| line | len | member | says |
|---:|---:|---|---|
| 811 | 4 | `public Founding getFounding()` | How this city was founded. |
| 817 | 1 | `public String getCityName()` | The city's name. |
| 820 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 823 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 826 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 829 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 845 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset. |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 849-871)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 872-1075)

| line | len | member | says |
|---:|---:|---|---|
| 910 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 911 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 913 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 914 | 1 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 917 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 918 | 1 | `public double getSubsidyPaid(String key)` |  |
| 920 | 5 | `public double getTotalSubsidyPaid()` |  |
| 927 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 940 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 943 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 962 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 972 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 1007 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 1024 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 1030 | 4 | `public double getIncome()` |  |
| 1035 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1039 | 3 | `public double getWaterRatio()` |  |
| 1043 | 3 | `public double getRoadRatio()` |  |
| 1048 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1053 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1058 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1062 | 3 | `public LandManager getLandManager()` |  |
| 1067 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1076-1191)

| line | len | member | says |
|---:|---:|---|---|
| 1117 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1120 | 1 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1130 | 43 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1181 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1188 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's tile by; since 0.7.13 its button asks landNeedsFunding() instead. |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 1192-1356)

| line | len | member | says |
|---:|---:|---|---|
| 1225 | 5 | `public java.util.List<LandParcel> landShelf()` | The plots on offer in the land office's order: cheapest ground first, per square foot in US dollars - the top-left card first. |
| 1232 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 1242 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 1252 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 1267 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 1272 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 1277 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 1289 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 1295 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 1300 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 1310 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 1340 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1345 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 1349 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1353 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 1357-1866)

| line | len | member | says |
|---:|---:|---|---|
| 1367 | 91 | `private void runPrivateInvestment()` |  |
| 1474 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 1487 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 1499 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 1505 | 360 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1867-1917)

| line | len | member | says |
|---:|---:|---|---|
| 1891 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1894 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1897 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1898 | 1 | `public double getHouseholdCarImports()` |  |
| 1901 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1904 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1907 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1910 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1913 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1916 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1918-1951)

| line | len | member | says |
|---:|---:|---|---|
| 1926 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1929 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1932 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1935 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1938 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1941 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1944 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1947 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1950 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1952-2528)

| line | len | member | says |
|---:|---:|---|---|
| 1970 | 10 | `private void capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 1987 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 2000 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 2017 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 2025 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 2053 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 2085 | 43 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 2136 | 14 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 2211 | 96 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 2320 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 2333 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 2360 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 2377 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 2404 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 2433 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |
| 2460 | 9 | `public double recapitaliseBank(double amount)` | The treasury puts capital into its bank. |
| 2471 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now. |
| 2481 | 4 | `public boolean canRecapitaliseBank()` | True when the bank needs capital and the treasury holds all of it: the rescue button's guard (0.7.9), here so the screen and anything else that offers the rescue cannot disagree about it. |
| 2495 | 9 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 2514 | 8 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 2524 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 2529-2718)

| line | len | member | says |
|---:|---:|---|---|
| 2548 | 69 | `private void runRetirement()` |  |
| 2624 | 14 | `private void closeBranch()` | Closes one of the bank's branches (0.7.11, round 2): the building retired by the path any retired building takes (retire()), sold by its owner, retail. |
| 2641 | 1 | `public int getBranchesClosed()` |  |
| 2654 | 64 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 2719-2768)

| line | len | member | says |
|---:|---:|---|---|
| 2739 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 2751 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 2760 | 1 | `public int getConstructionShedMonth()` |  |
| 2761 | 1 | `public double getConstructionShedPoints()` |  |
| 2764 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 2769-3096)

| line | len | member | says |
|---:|---:|---|---|
| 2789 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 2802 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 2822 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 2830 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 2837 | 3 | `public double getLastWriteOff()` |  |
| 2876 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 2886 | 210 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 3097-3253)

| line | len | member | says |
|---:|---:|---|---|
| 3136 | 5 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 3150 | 57 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, scanning down from what the planner asked for as consider() does - and the advisor's ... |
| 3216 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 3220 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 3233 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 3254-3331)

| line | len | member | says |
|---:|---:|---|---|
| 3290 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 3298 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 3303 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 3308 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 3311 | 20 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 3332-4151)

| line | len | member | says |
|---:|---:|---|---|
| 3345 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 3362 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 3384 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 3389 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 3485 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 3505 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 3534 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 3551 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 3588 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 3607 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 3638 | 71 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 3719 | 1 | `public double getCityMaintenancePaid()` |  |
| 3724 | 4 | `public int getConstructionMaterials()` |  |
| 3728 | 4 | `public double getInterestRate()` |  |
| 3732 | 3 | `public boolean isGraphsEnabled()` |  |
| 3735 | 3 | `public boolean isReportsEnabled()` |  |
| 3754 | 88 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early if the treasury is empty. |
| 3844 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 3873 | 1 | `public boolean hasNewReceipt()` |  |
| 3874 | 1 | `public void clearReceipt()` |  |
| 3876 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 3890 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 3909 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 3929 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 3939 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 3981 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 4009 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 4018 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 4033 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 4056 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 4150 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 4152-4773)

| line | len | member | says |
|---:|---:|---|---|
| 4167 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 4179 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 4192 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 4212 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 4229 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 4291 | 1 | `public Investor getGovernmentInvestor()` |  |
| 4294 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 4296 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 4336 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 4345 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |
| 4350 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 4353 | 3 | `public double getTotalBuildingCost()` |  |
| 4356 | 3 | `public String getBuildingName()` |  |
| 4359 | 3 | `public int getBuildQuantity()` |  |
| 4364 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 4377 | 111 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 4496 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 4508 | 11 | `private String issueNote(DebtQuote quote)` | The booking every note shares - handleTBillLogic()'s, and since 0.7.13 the rollover's, which books the quote it sized (issueForRollover()). |
| 4526 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 4564 | 15 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 4622 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 4634 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 4655 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 4684 | 35 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 4725 | 5 | `private double longBondNetProceeds(double faceValue, double marketRate, int duration)` | What the city actually banks for a long bond of this face: its worth at the rate struck, less the fees, to the cent. |
| 4732 | 13 | `private DebtQuote longBondQuote(double requested, int duration, double marketRate, double before, double faceValue, double rece...` | A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. |
| 4747 | 7 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |
| 4759 | 14 | `private String issueLongBond(DebtQuote quote)` | The booking both long-bond issues share. |

### A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) (lines 4774-4857)

| line | len | member | says |
|---:|---:|---|---|
| 4802 | 8 | `private double longBondFaceForProceeds(double cashNeeded, int duration, double rounding, double marketRate)` | Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. |
| 4819 | 29 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a term bond whose CASH covers cashNeeded would cost. |
| 4850 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 4858-5164)

| line | len | member | says |
|---:|---:|---|---|
| 4900 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 4936 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 4951 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation, less the world's base rate less the world's realised inflation - the one definition the month hands the currenc... |
| 4963 | 3 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the year's inflation, the same PriceIndex.inflation() the real rate differential and the parity read - the one definition the month strikes the ho... |
| 4975 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 4980 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 4983 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 4990 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 5057 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 5077 | 70 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 5154 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |

### THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 5165-5239)

| line | len | member | says |
|---:|---:|---|---|
| 5186 | 12 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | A serial bond whose CASH covers cashNeeded: quoteMediumBond() at the face that brings it. |
| 5204 | 14 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note, serial or term loan whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 5220 | 8 | `private double foreignProceedsPerFace(String type, double rate, int duration)` | What a unit of a dollar instrument's face banks at this rate, net of the spread - never under MIN_PROCEEDS_PER_FACE, so the search above always moves. |
| 5230 | 9 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 5240-6515)

| line | len | member | says |
|---:|---:|---|---|
| 5303 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 5306 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 5309 | 1 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 5317 | 8 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 5327 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not, and whether it is dollar paper rolled at home. |
| 5329 | 13 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 5344 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 5358 | 43 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 5403 | 36 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 5448 | 9 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; a serial by quoteMediumBondForCash(); a term loan by quoteLo... |
| 5459 | 4 | `private double localFace(boolean abroad, DebtQuote quote)` | A rollover quote's face in local money: a dollar quote's at the day's rate. |
| 5470 | 12 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 5485 | 3 | `private void printPopulationInfo()` | printers |
| 5490 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 5503 | 3 | `private void printUtilityInfo()` |  |
| 5507 | 3 | `private void printCityStats()` |  |
| 5514 | 725 | `private void nextMonth()` |  |
| 6240 | 247 | `private void startOfMonthUpdate()` |  |
| 6487 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 6516-6648)

| line | len | member | says |
|---:|---:|---|---|
| 6565 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 6590 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 6602 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 6622 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 6644 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 6649-6899)

| line | len | member | says |
|---:|---:|---|---|
| 6663 | 16 | `public String getCreditRating()` |  |
| 6681 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 6704 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 6718 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 6740 | 27 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 6768 | 78 | `private void finalUpdateEconomy()` |  |
| 6848 | 4 | `private void updateConstructionCost()` |  |
| 6859 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 6870 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 6874 | 4 | `public int getHouseholdCapacity()` |  |
| 6878 | 4 | `public int getStoreCapacity()` |  |
| 6882 | 3 | `public int[] getJobs()` |  |
| 6887 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 6900-7480)

| line | len | member | says |
|---:|---:|---|---|
| 6929 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 6965 | 1 | `public PopulationCohorts getCohorts()` |  |
| 6966 | 1 | `public FamilyModel getFamilies()` |  |
| 6967 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 6968 | 1 | `public Migration getMigration()` |  |
| 6969 | 1 | `public Health getHealth()` |  |
| 6970 | 1 | `public Healthcare getHealthcare()` |  |
| 6994 | 486 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 7481-7493)

| line | len | member | says |
|---:|---:|---|---|
| 7489 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 7492 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 7494-7782)

| line | len | member | says |
|---:|---:|---|---|
| 7510 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 7529 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 7549 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 7591 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 7617 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 7627 | 3 | `public BuildLog getBuildLog()` |  |
| 7634 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 7659 | 1 | `public Unemployment getUnemployment()` |  |
| 7663 | 1 | `public Sickness getSickness()` |  |
| 7671 | 1 | `public Crime getCrime()` |  |
| 7675 | 1 | `public double getLastOrphanDeaths()` |  |
| 7676 | 1 | `public double getLastUnhousedDeaths()` |  |
| 7678 | 6 | `{ ... }` |  |
| 7686 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 7711 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 7720 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 7741 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 7752 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 7765 | 1 | `public double getStudentLoansLent()` |  |
| 7766 | 1 | `public double getStudentLoansRepaid()` |  |
| 7767 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 7771 | 1 | `public double getStudentLoanInterest()` |  |
| 7774 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 7781 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 7783-7905)

| line | len | member | says |
|---:|---:|---|---|
| 7798 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 7804 | 5 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 7811 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 7823 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 7867 | 1 | `public Equity getEquity()` |  |
| 7871 | 1 | `public Exchange getExchange()` |  |
| 7879 | 1 | `public BondMarket getBondMarket()` |  |
| 7895 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 7906-7929)

| line | len | member | says |
|---:|---:|---|---|
| 7916 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 7925 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 7928 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 7930-8225)

| line | len | member | says |
|---:|---:|---|---|
| 7942 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 7944 | 1 | `public PriceIndex getPriceIndex()` |  |
| 7946 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 7953 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 7960 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 7969 | 1 | `public Bank getBank()` |  |
| 7982 | 1 | `public CentralBank getCentralBank()` |  |
| 7996 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 8002 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 8005 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 8012 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 8016 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 8020 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 8087 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 8091 | 3 | `public EconomyManager getEconomyManager()` |  |
| 8094 | 3 | `public PopulationManager getPopulationManager()` |  |
| 8098 | 1 | `public LabourMarket getLabourMarket()` |  |
| 8099 | 1 | `public Education getEducation()` |  |
| 8115 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 8140 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 8182 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 8200 | 23 | `public void recordMonth()` | Files this month in the graph history. |
| 8224 | 1 | `public Inbox getInbox()` |  |
| 8225 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 8226-8607)

| line | len | member | says |
|---:|---:|---|---|
| 8237 | 3 | `public int getMonthsUntilAutosave()` |  |
| 8253 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 8265 | 241 | `public void save(int slot, String slotName)` |  |
| 8515 | 5 | `public String takeSkipFailure()` |  |
| 8524 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 8526 | 1 | `public GameFiles getGameFiles()` |  |
| 8539 | 7 | `public GameFiles.Result[] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade. |
| 8547 | 18 | `public void sendBuildingSave()` |  |
| 8568 | 16 | `public void loadBuildings()` |  |
| 8598 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 8608-8748)

| line | len | member | says |
|---:|---:|---|---|
| 8635 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 8636 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 8637 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 8644 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 8666 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 8733 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 8742 | 1 | `public double getCityPaperSettled()` |  |
| 8743 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 8744 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 8746 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 8749-8824)

| line | len | member | says |
|---:|---:|---|---|
| 8777 | 1 | `public double getCouponsToHouseholds()` |  |
| 8778 | 1 | `public double getPrincipalToHouseholds()` |  |
| 8779 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 8790 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 8798 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 8811 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 8825-8850)

| line | len | member | says |
|---:|---:|---|---|
| 8835 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 8851-8903)

| line | len | member | says |
|---:|---:|---|---|
| 8868 | 35 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 8904-8965)

| line | len | member | says |
|---:|---:|---|---|
| 8923 | 42 | `private void openMarketOperation()` |  |

### a buyback's holders outside the pools (lines 8966-8987)

| line | len | member | says |
|---:|---:|---|---|
| 8981 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 8985 | 1 | `public double getBuybackToHouseholds()` |  |
| 8986 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 8988-9192)

| line | len | member | says |
|---:|---:|---|---|
| 9059 | 13 | `private void takeTreasuryMonth()` |  |
| 9074 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 9076 | 1 | `public double getTreasuryOpening()` |  |
| 9077 | 1 | `public double getTreasuryClosing()` |  |
| 9078 | 1 | `public double getTreasuryRaised()` |  |
| 9079 | 1 | `public double getTreasuryRepaid()` |  |
| 9080 | 1 | `public double getTreasurySurplus()` |  |
| 9083 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 9101 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 9113 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 9118 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 9126 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |
| 9130 | 5 | `double[] treasuryMonthToSave()` |  |
| 9136 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 9170 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 9173 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 9175 | 1 | `public double getPostAuditDriftWorst()` |  |
| 9176 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 9177 | 4 | `public void InterestExpense(double amount)` |  |
| 9182 | 3 | `public DebtManager getDebtManager()` |  |
| 9187 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 9193-9434)

| line | len | member | says |
|---:|---:|---|---|
| 9253 | 26 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 9291 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 9296 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 9318 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 9330 | 17 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 9356 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 9381 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 9388 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 9392 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 9402 | 7 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 9411 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 9414 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 9421 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 9430 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 9431 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 9432 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 9433 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 9435-9559)

| line | len | member | says |
|---:|---:|---|---|
| 9448 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 9456 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 9481 | 70 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 9560-11136)

| line | len | member | says |
|---:|---:|---|---|
| 9628 | 17 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 9646 | 296 | `private void rebuildSimulationState()` |  |
| 9984 | 1106 | `public void loadGame(int slot)` | Load game |
| 11095 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 11137-11365)

| line | len | member | says |
|---:|---:|---|---|
| 11143 | 1 | `public Denomination getDenomination()` |  |
| 11146 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 11177 | 165 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 11353 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

