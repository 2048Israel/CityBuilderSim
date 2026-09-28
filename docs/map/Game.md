# Game.java - 12,379 lines · 459 methods · 15 constants · model

`ham/citybuildersim/Game.java` - generated 2026-09-28 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Equity](Equity.md) (55), [TreasuryLine](TreasuryLine.md) (43), [DebtQuote](DebtQuote.md) (40), [AgeBand](AgeBand.md) (36), [Debt](Debt.md) (34), [BusinessDebtManager](BusinessDebtManager.md) (32), [GameLog](GameLog.md) (31), [GameFiles](GameFiles.md) (25), [Rollover](Rollover.md) (25), [BuildingsTemplate](BuildingsTemplate.md) (23), [TreasuryFund](TreasuryFund.md) (20), [Bank](Bank.md) (20), [CareType](CareType.md) (20), [DebtManager](DebtManager.md) (19), [Founding](Founding.md) (19), [FamilyModel](FamilyModel.md) (16), [HouseholdAccounts](HouseholdAccounts.md) (14), [Sector](Sector.md) (14), [Investor](Investor.md) (14), [BuildingType](BuildingType.md) (14), [JobType](JobType.md) (13), [LongTermBond](LongTermBond.md) (13), [LandParcel](LandParcel.md) (12), [TaxPolicy](TaxPolicy.md) (12), [Healthcare](Healthcare.md) (10), [BusinessInvestment](BusinessInvestment.md) (10), [LandManager](LandManager.md) (10), [PayTier](PayTier.md) (10), [Household](Household.md) (9), [BuildingManager](BuildingManager.md) (8)... and 64 more

**Used by (105):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [Automotive](Automotive.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildMenuCheck](BuildMenuCheck.md), [BuildScreen](BuildScreen.md), [BusinessInvestment](BusinessInvestment.md), [BusinessServices](BusinessServices.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CalendarCheck](CalendarCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarCheck](CarCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [ConservationCheck](ConservationCheck.md), [Construction](Construction.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [CurrencyCheck](CurrencyCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Debt](Debt.md), [DebtManager](DebtManager.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [FoodIndustry](FoodIndustry.md), [FoodProcessing](FoodProcessing.md), [FoodProcessingCheck](FoodProcessingCheck.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [FoundingScreen](FoundingScreen.md), [FundCheck](FundCheck.md), [GdpCheck](GdpCheck.md), [HealthCheck](HealthCheck.md), [HeavyIndustry](HeavyIndustry.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HousingCheck](HousingCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md), [InfrastructureCheck](InfrastructureCheck.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [LongTermBond](LongTermBond.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [ManufacturingCheck](ManufacturingCheck.md), [Markets](Markets.md), [MediumTermBond](MediumTermBond.md), [Mining](Mining.md), [MiningCheck](MiningCheck.md), [MonetaryCheck](MonetaryCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Motoring](Motoring.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [PolicyCheck](PolicyCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [Rail](Rail.md), [RailCheck](RailCheck.md), [ReadPathCheck](ReadPathCheck.md), [RealEstate](RealEstate.md), [Restaurants](Restaurants.md), [RestaurantsCheck](RestaurantsCheck.md), [RestructureCheck](RestructureCheck.md), [Retail](Retail.md), [RobustnessCheck](RobustnessCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorBooksCheck](SectorBooksCheck.md), [SectorScreen](SectorScreen.md), [Sectors](Sectors.md), [ShadowBasket](ShadowBasket.md), [ShortTermTBill](ShortTermTBill.md), [SicknessCheck](SicknessCheck.md), [SimulationEngine](SimulationEngine.md), [SkipReportCheck](SkipReportCheck.md), [SummaryScreen](SummaryScreen.md), [TradeCostCheck](TradeCostCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VanCheck](VanCheck.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 754 | THE FOUNDING RESERVE (2026-09-21) |
| 810 | · THE FOUNDING RECORD (0.7.10) |
| 863 | THE CONSTRUCTION SUBSIDY - removed in 0.7.1 |
| 886 | STANDING POLICY: NEVER LET THIS SECTOR SHRINK |
| 1090 | LAND IS BOUGHT IN DOLLARS (0.7.6) |
| 1206 | · WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) |
| 1371 | PRIVATE INVESTMENT |
| 1689 | · AND THE BALANCE SHEET |
| 1881 | THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. |
| 1932 | THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. |
| 1966 | THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE |
| 2484 | THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) |
| 2622 | · the preferred offer |
| 2736 | · what the fund is worth |
| 2796 | · the dial |
| 2893 | · the hand |
| 2939 | · an Insane founding |
| 3048 | SHRINKING |
| 3238 | THE CONSTRUCTION WARNING |
| 3288 | PRIVATE INVESTMENT WITH NOWHERE TO GO |
| 3616 | THE LANDLORDS' MORTGAGES (0.7.11) |
| 3773 | THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) |
| 3851 | A BOND OR THE BANK, FOR A BUILDING (0.7.12) |
| 4676 | HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) |
| 5298 | · A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) |
| 5382 | BORROWING IN SOMEBODY ELSE'S MONEY |
| 5689 | · THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) |
| 5764 | ROLLING WHAT FALLS DUE (0.7.13) |
| 6151 | · THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21) |
| 6406 | · AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD. |
| 6622 | · AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD. |
| 6805 | · NOTHING AFTER THE AUDIT MAY MOVE A POOL. |
| 7138 | WHAT IT COSTS TO GO TO MARKET AT ALL |
| 7271 | THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE |
| 7522 | DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH |
| 8103 | WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. |
| 8116 | THE READINGS THE MONTH TAKES OF THE CITY |
| 8405 | · the price of a place (2026-09-21) |
| 8538 | WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. |
| 8562 | THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS |
| 8858 | the save system |
| 9264 | PAYING THE WORLD BACK |
| 9405 | THE HOLDERS ARE PAID (0.7.1) |
| 9481 | · the desk, for the households |
| 9507 | · THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) |
| 9564 | THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) |
| 9711 | · THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) |
| 9947 | · a buyback's holders outside the pools |
| 9969 | WHAT THE TREASURY ACTUALLY DID |
| 10174 | THE CENTRAL BANK AND THE TREASURY (0.7.0) |
| 10427 | BUYING YOUR OWN DEBT BACK |
| 10552 | WHY LAND IS NOT IN THE RENT FLOOR |
| 10897 | · · the three the monthly path sets and this did not |
| 12148 | THE CURRENCY REFORM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 4674 | `Game.BuildResult.SUCCESS` |  |
| 4674 | `Game.BuildResult.NEEDS_FUNDING` |  |
| 4674 | `Game.BuildResult.NO_LAND` |  |
| 4674 | `Game.BuildResult.NO_DEPOSIT` |  |
| 4674 | `Game.BuildResult.NO_LICENCE` |  |
| 4674 | `Game.BuildResult.FAILED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 505 | `Game.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |
| 802 | `Game.FOUNDING_CASH` | `100_000` | What the founders leave in the treasury, in thousands: D$100M since 0.7.10 (D$2.5B before) - the founding village and one of the first big works; the city borrows for the rest. |
| 805 | `Game.FOUNDING_RESERVE_USD` | `25_000` | What the founders leave in the vault, in thousands of US dollars: US$25M since 0.7.10 (US$1B before), bought on day one at the opening rate - years of a young city's imports, three months of a town of 8-10k. |
| 808 | `Game.FOUNDERS_NOTE_MONTHS` | `120` | For this many months the screens say where the vault's first dollars came from; after that they are the city's own. |
| 4688 | `Game.LICENCE_COVER_TO_OPEN` | `.5` |  |
| 5438 | `Game.DEFAULT_OVERDRAFT_YEARS` | `1.0` | How deep the city may go before its foreign creditors are not paid. |
| 5593 | `Game.FOREIGN_QUOTE_ITERATIONS` | `50` | The most times the dollar quote's fixed point is walked; it settles to 1e-13 in a handful. |
| 5707 | `Game.BUILD_NOTE_GRANULE` | `1000` | The granule the build screen's note's face is rounded up to, in thousands: $1M, the step the Finances tab's notes are sold in (its Note instrument's rounding). |
| 7154 | `Game.BUILD_NOTE_MONTHS` | `6` | The term of the note the build screen offers when the treasury cannot pay for an order - the player's choice, and the only note sized to a gap since 0.7.0. |
| 7166 | `Game.BUILD_BOND_YEARS` | `20` | The term of the bond the build screen offers beside the note (0.7.10): a long-lived asset financed with long-lived debt, the matching principle, so a plant is paid for over the years the city uses it. |
| 7169 | `Game.BUILD_BOND_GRANULE` | `100` | The granule the build screen's bond's face is rounded up to, in thousands: $100k, what the playtest's own term bonds round to. |
| 7184 | `Game.FIXED_ISSUE_COST` | `12` | Bond counsel, rating and printing. |
| 7193 | `Game.UNDERWRITING_SPREAD` | `.0075` | Underwriter's spread, as a fraction of face: 0.75%, inside the 0.5-1% gross spread investment-grade issues pay (Melnik & Nissim, 2003) - the businesses' bonds pay it too since 0.7.12 (BondMarket, WHAT AN ISSUE COSTS). |
| 7201 | `Game.MIN_PROCEEDS_PER_FACE` | `1 -.95 - UNDERWRITING_SPREAD` | The least a dollar of face can ever bank, net of the discount and the spread. |
| 8865 | `Game.AUTOSAVE_MONTHS` | `12` | How many months between autosaves. |

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
| 724 | `private String loadFailure` | Why the last load did not happen, or null. |
| 822 | `private Founding founding` |  |
| 852 | `private java.util.List<BuildingsTemplate> catalogueBeforeFounding` |  |
| 921 | `private final java.util.Map<String, Boolean> autoSubsidy` | Keyed by the sector's name since the sector template - a seventh sector is a seventh key. |
| 922 | `private final java.util.Map<String, Double> subsidyPaid` |  |
| 1128 | `private boolean landPaidFromVault` | Whether the land office pays out of the vault rather than converting cash (0.7.6). |
| 1350 | `private String lastLandReceipt` | What the last land purchase cost and how it was paid, in the player's words - the land office shows it under the toggle, and a short vault says here that the rest was converted. |
| 1351 | `private int lastLandReceiptMonth` |  |
| 1899 | `private double carriedCarOwnership` | The ownership rate the save was taken at, applied inside the rebuild. |
| 1902 | `private final Motoring motoring` | The households' car market; runs once a month, after the ledger. |
| 1937 | `private final LuxuryCounter luxuryCounter` | The boutiques and the kitchens; each runs once a month, after the cars. |
| 2197 | `private final Exchange.Companies exchangeCompanies` | What the exchange reads of a company that is not on the register: its till, its sheet and its costs. |
| 2523 | `private double ownersWipedAbroadThisMonth` | The world's shares a resolution wiped out, at their last price: this month's valuation abroad, declared to MoneyAudit and cleared after the strike. |
| 3159 | `private int branchesClosed` | Branches the bank has closed over the run (0.7.11, round 2): a count for the playtest, not saved. |
| 3246 | `private int constructionShedMonth` |  |
| 3247 | `private double constructionShedPoints` |  |
| 3306 | `private final java.util.Set<String> landBlockedSectors` | Sectors that wanted to build this month and had no land to build on. |
| 3319 | `private final java.util.Set<String> refusedOnPrice` | The sectors whose plan this month was declined on its price (0.7.8): not one of it would carry its interest at the rate the loan would be written at, where it would have at prime and its record alone - so what refused... |
| 3339 | `private int landWarningAcknowledged` | Whether to warn that the private sector is out of room. |
| 3354 | `private double lastWriteOff` | Written off in the most recent insolvency sweep, for the credit screen. |
| 3732 | `private final java.util.Set<String> refusedByLender` | The sectors whose plan this month the mortgage lender declined, and those that held for the down payment (0.7.11) - the month's, cleared with the land's, for the playtest's count by reason. |
| 3733 | `private final java.util.Set<String> heldForDownPayment` |  |
| 3812 | `private final java.util.List<Salvage> salvageThisMonth` |  |
| 3813 | `private final java.util.Map<String, Double> salvageBySector` |  |
| 3814 | `private double salvageUsedThisMonth` |  |
| 3871 | `private final java.util.Map<String, BusinessDebtManager.Plan> projectFinancing` | The plan each sector's last building was financed on this month, for the advisor's line. |
| 4236 | `private double cityMaintenancePaid` | What the treasury paid this month to keep the city's own buildings up. |
| 4241 | `private double bankMaintenanceDue` | The bank's repair bill for its branches this month, charged with its running costs. |
| 4415 | `public final int quantity` |  |
| 4416 | `public final double sticker` |  |
| 4417 | `public final double materialsNeeded` |  |
| 4419 | `public final double materialsInStock` | What the city's own yard covers, free. |
| 4421 | `public final double materialsFromPlant` | What the materials plant sells the order, at the market price. |
| 4422 | `public final double plantPrice` |  |
| 4423 | `public final double plantCost` |  |
| 4424 | `public final double materialsImported` |  |
| 4425 | `public final double materialsPrice` |  |
| 4426 | `public final double importCost` |  |
| 4427 | `public final double total` |  |
| 4428 | `public final double landNeeded` |  |
| 4429 | `public final double landFree` |  |
| 4431 | `public final double months` | Months at today's construction output, or NaN when there is none. |
| 4807 | `private final Investor government` | The city itself, as a payer. |
| 5838 | `private final Rollover rollover` |  |
| 7515 | `private BusinessInvestment businessInvestment` | Needed by the JavaFX sector screens. |
| 7516 | `private LandManager landManager` |  |
| 7519 | `private DemolitionLog demolitionLog` | What businesses have scrapped, so the panel can say what went and when. |
| 7520 | `private BuildLog buildLog` |  |
| 7547 | `private PopulationCohorts cohorts` |  |
| 7548 | `private FamilyModel families` |  |
| 7556 | `private Migration migration` |  |
| 7562 | `private LabourMarket labourMarket` | What labour costs. |
| 7568 | `private Education education` | The schools. |
| 7577 | `private Health health` | How much of the workforce is off sick. |
| 7585 | `private Healthcare healthcare` | The health SERVICE - its payroll, its fees, and its cemeteries. |
| 8108 | `private final Offending offending` | Who is at risk of offending, and the thefts; runs in the crime step of the month. |
| 8254 | `private TimeSkipReport skipReport` | What happened during the last fast-forward. |
| 8261 | `private HouseholdAccounts households` | The residents' own books. |
| 8270 | `private final HouseholdBalance householdBalance` | What the households have saved and what they owe. |
| 8280 | `private final Unemployment unemployment` | THE PEOPLE OUTSIDE THE FAMILIES (2026-09-11). |
| 8284 | `private final Sickness sickness` | Who has been sick how long, and who it kills. |
| 8292 | `private final Crime crime` | Crime, the police and the prisons (2026-09-11). |
| 8296 | `private double lastOrphanDeaths, lastUnhousedDeaths` | Last month's dead who were orphans, and who had no home. |
| 8386 | `private double studentLoansLent, studentLoansRepaid, studentLoansWrittenOff` | Student loans the treasury lent and was repaid this month, and wrote off with graduates who left. |
| 8392 | `private double studentLoanInterest` | Interest the graduates paid the treasury on their loans this month (2026-09-21): revenue, beside the principal above. |
| 8465 | `private final ForeignAccounts foreign` | The city's account with the rest of the world. |
| 8475 | `private final CapitalFlows hotMoney` | Hot money, and the run on it. |
| 8481 | `private final OutwardInvestment outward` | The city's own savings abroad, the mirror of the crowd above. |
| 8488 | `private final Equity equity` | Who owns the city's companies. |
| 8492 | `private final Exchange exchange` | Where the shares change hands, with the bank as the dealer. |
| 8500 | `private final BondMarket bondMarket` | The businesses' bonds and the order book each trades on (0.7.12): who issued what, who holds it, and the rule each participant trades by. |
| 8510 | `private final TreasuryFund fund` | The city's fund (0.7.14): its cash, its dial and its rescue setting, the bank's preferred offer, its rescues and the player's orders. |
| 8514 | `private final BondMarket.Readings bondReadings` | What the bond market reads of the city, read live. |
| 8536 | `private final PriceIndex priceIndex` | What a month costs a household, against founding. |
| 8544 | `private final Consumption consumption` |  |
| 8545 | `private boolean consumptionLoaded` |  |
| 8554 | `private final CityBasket cityBasket` | The basket per head, struck from the household statements each time the shops ask. |
| 8572 | `private final WorldEconomy world` | The rest of the world, which has its own inflation. |
| 8581 | `private final double[] rateHistory` | The rate a year ago, for spotting a run. |
| 8582 | `private int rateHistoryFilled` |  |
| 8600 | `private final Bank bank` | The city's commercial bank - every loan in it, and every default. |
| 8613 | `private CentralBank centralBank` | The central bank - the balance sheet money is made on (0.7.0). |
| 8657 | `private double cityCapitalSpending` | What the CITY spent on buildings this month - its own capital budget. |
| 8670 | `private double monthlyMaterialImports` | Construction materials bought in from outside this month, in units. |
| 8684 | `private double monthlyMaterialImportBill` | The same imports in MONEY, at the price each was charged at. |
| 8702 | `private double carriedRentWeight` | What a save carried about next month's shopping and rent. |
| 8705 | `private double carriedOccupiedHomes` | Doors let, as the save recorded them. |
| 8707 | `private double carriedStudioWeight` | ...and the studio half of it. |
| 8708 | `private double carriedRetailCapacity` |  |
| 8709 | `private double carriedRetailWant` |  |
| 8712 | `private double cityInterestPaid` | Interest the city paid this month, accumulated by InterestExpense(). |
| 8713 | `private java.util.Map<String, String> lastInvestment` |  |
| 8716 | `private final java.util.Map<String, Double> sectorInvested` | What each sector spent on buildings this month, less what it sold back. |
| 8760 | `private double lastAdultMortality` | This month's adult death rate with the clinics applied. |
| 8867 | `private int monthsSinceAutosave` |  |
| 9153 | `private String skipFailure` | Why the last fast-forward stopped early, or null. |
| 9162 | `private GameFiles.Result lastSaveResult` | What the last write attempt did. |
| 9287 | `private double foreignDebtRaisedThisMonth` |  |
| 9288 | `private double foreignPrincipalRepaidThisMonth` |  |
| 9289 | `private double foreignInterestPaidThisMonth` |  |
| 9341 | `private double cityDebtRaisedThisMonth` | WHAT THE CITY HAS SOLD ITS BANK AND THE BANK HAS NOT YET PAID FOR. |
| 9361 | `private double cityDiscountThisMonth` | Face value less cash paid, on everything the city issued this month. |
| 9362 | `private double cityPrincipalRepaidThisMonth` |  |
| 9376 | `private double cityDebtRaisedForBank` | What the treasury raised, as it stood when the month began. |
| 9377 | `private double cityDiscountForBank` |  |
| 9380 | `private double legacyDiscountDue` | A 0.7.0 save's discount on paper saved between its issue and its settle, booked whole at the settle as that save's bank would have. |
| 9397 | `private double cityPaperSettled` | What the bank handed the treasury for its paper at this month's settle. |
| 9403 | `private double bankPrincipalRepaidThisMonth` |  |
| 9431 | `private double couponsToHouseholds, principalToHouseholds, householdsBoughtPaper` | Coupons and principal paid to the households on their paper, and what they paid for it at issue - this month's, for MoneyAudit. |
| 9522 | `java.util.function.Consumer<Boolean> settleProbeForTest` | A harness's look at the households either side of their share of the settle (HoldersCheck): false before, true after. |
| 9810 | `private double centralBankTender` | What the central bank rolls of its own this month: struck at the press (rollMaturities()), taken in the window (rollCentralBankAtIssue()). |
| 9813 | `private final java.util.List<ParAlone> centralBankAlone` | ...and, with none of the city's term paper sold to add it on to, its par alone, quoted at the press: one issue per paper. |
| 9957 | `private double buybackToHouseholdsUnsettled, buybackAbroadUnsettled` | What a buyback between two presses paid the households and the holders of a dollar bond, carried in the treasury's pool until the next month declares it leaving (MoneyAudit.pools()) - the shape the bank's unsettled pa... |
| 9959 | `private double buybackToHouseholds, buybackAbroad` | ...and declared this month. |
| 10011 | `private double treasuryOpening` |  |
| 10012 | `private double treasuryClosing` |  |
| 10013 | `private double treasuryRaised` |  |
| 10014 | `private double treasuryRepaid` |  |
| 10015 | `private double treasurySurplus` |  |
| 10016 | `private boolean treasuryRecorded` |  |
| 10035 | `private double treasuryRaisedSoFar` | What the treasury has raised by issuing paper since the last strike, in local money - the bridge's own counter, press to press. |
| 10038 | `private final TreasuryJournal treasuryJournal` | The named non-budget movements, this month and last. |
| 10135 | `private double[] loadedGovernmentMonth` | The government's month as the save carried it, waiting for the rebuild. |
| 10138 | `private MoneyAudit.Result lastMoneyAudit` | Last month's money-conservation residual. |
| 10146 | `private double postAuditDrift` | How much moved after the audit struck, which must be nothing. |
| 10147 | `private String postAuditDriftPool` |  |
| 10148 | `private double postAuditDriftWorst` |  |
| 10212 | `private final java.util.Map<String, Double> arrears` | What the treasury owes and has not paid, by line and by whom it is owed - "LINE" or "LINE:sector" - in thousands. |
| 10215 | `private double arrearsRefusedThisMonth, arrearsPaidThisMonth` | This month's arrears: refused and booked, and paid down. |
| 10218 | `private double arrearsRefusedLifetime, arrearsPaidLifetime` | Refused and paid down since founding, for the playtest's record. |
| 10550 | `private int pendingWorkforce` | Set by loadGame() from the save, consumed by the next rebuildSimulationState(). |
| 12103 | `private boolean bankAllowanceToOpen` | True between reading a save from before 0.7.8 and the end of its load: its bank's allowance is set up there. |
| 12152 | `private final Denomination denomination` |  |
| 12372 | `private boolean forcedReform` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 12355 | **type** `public class Game` |  |
| 130 | 3 | `public Game()` |  |
| 142 | 3 | `public Game(GameFiles gameFiles)` | Lets a test point the game at a temporary folder. |
| 151 | 4 | `public Game(GameFiles gameFiles, Founding founding)` | ...founded as the player chose (0.7.10): a name, its money, a treasury, a vault and a world. |
| 157 | 5 | `private static Founding foundable(Founding founding)` | The founding, if it can found a city; otherwise why not, as an exception - the screen never offers one that cannot. |
| 189 | 271 | `private void buildWorld(Founding founding)` | Builds the entire simulation from nothing. |
| 461 | 4 | `public void run()` |  |
| 466 | 38 | `private void initialize()` |  |
| 507 | 4 | `static { ... }` |  |
| 515 | 3 | `public void newGame()` | A new city on the defaults: "Found with defaults". |
| 524 | 38 | `public void newGame(Founding choices)` | A new city, founded as the player chose on the founding screen (0.7.10). |
| 592 | 30 | `private void foundingBank()` | The city opens with a bank already standing. |
| 622 | 8 | `public void resumeGame()` |  |
| 630 | 54 | `public void loadGameSave(int slot)` |  |
| 693 | 6 | `public GameFiles.Result saveGame(int slot, String slotName)` | Returns what actually happened rather than announcing success regardless. |
| 701 | 4 | `public GameFiles.Result saveGame(int slot)` | Saves to a slot, keeping whatever name that slot already carried. |
| 714 | 1 | `public boolean isRunning()` | True once a city exists to go back to. |
| 716 | 6 | `public void toggleQuit()` |  |
| 726 | 1 | `public String getLoadFailure()` |  |
| 728 | 7 | `public void toggleGraphs()` |  |
| 735 | 5 | `public void toggleReports()` |  |
| 741 | 3 | `public void toggleNextMonth()` |  |
| 746 | 1 | `SimulationEngine getSimulationEngineForTest()` | The month's spine, for CalendarCheck: it must not move the calendar itself - the press does. |
| 748 | 3 | `public int getMonth()` |  |
| 751 | 3 | `public double getCash()` |  |

### THE FOUNDING RESERVE (2026-09-21) (lines 754-809)

### THE FOUNDING RECORD (0.7.10) (lines 810-862)

| line | len | member | says |
|---:|---:|---|---|
| 825 | 4 | `public Founding getFounding()` | How this city was founded. |
| 831 | 1 | `public String getCityName()` | The city's name. |
| 834 | 1 | `public Currency getCurrency()` | The city's money: its name, code and symbols. |
| 837 | 1 | `public double getFoundingCash()` | The treasury this city was founded with, in thousands. |
| 840 | 1 | `public double getFoundingReserveUsd()` | The vault this city was founded with, in thousands of US dollars - what the founders' note says they left. |
| 843 | 9 | `private java.util.List<BuildingsTemplate> catalogue()` | The catalogue the founding is priced over: this city's, or the file's own before any city has loaded one. |
| 859 | 3 | `public Founding.Buys whatItBuys(double cash, double reserveUsd)` | What a founding of this treasury and vault buys, at a new city's invoices over the catalogue - the founding screen's line under each preset. |

### THE CONSTRUCTION SUBSIDY - removed in 0.7.1 (lines 863-885)

### STANDING POLICY: NEVER LET THIS SECTOR SHRINK (lines 886-1089)

| line | len | member | says |
|---:|---:|---|---|
| 924 | 1 | `public boolean isAutoSubsidised(Sector sector)` |  |
| 925 | 1 | `public boolean isAutoSubsidised(String key)` |  |
| 927 | 1 | `public void setAutoSubsidised(Sector sector, boolean on)` |  |
| 928 | 1 | `public void setAutoSubsidised(String key, boolean on)` |  |
| 931 | 1 | `public double getSubsidyPaid(Sector sector)` | What this sector was paid this month. |
| 932 | 1 | `public double getSubsidyPaid(String key)` |  |
| 934 | 5 | `public double getTotalSubsidyPaid()` |  |
| 941 | 5 | `public java.util.List<String> getSubsidisedSectors()` | The protected sectors, by name, for the save. |
| 954 | 3 | `double subsidiseForTest(Sector sector, double netIncome)` | One subsidy payment against a stated loss, for PolicyCheck. |
| 957 | 3 | `double subsidiseForTest(String key, double netIncome)` |  |
| 976 | 3 | `void setCashForTest(double amount)` | Puts the treasury at a stated figure, for a fixture that needs to CAUSE a condition rather than wait for one. |
| 986 | 27 | `private double paySubsidyIfOwed(Sector sector, double netIncome)` | Tops a protected sector up to break-even. |
| 1021 | 3 | `public double getSubsidisedCapacity()` | Construction capacity the current subsidy keeps alive. |
| 1038 | 5 | `public double protectedConstructionCapacity()` | Construction capacity the standing policy keeps alive. |
| 1044 | 4 | `public double getIncome()` |  |
| 1049 | 3 | `public double getEnergyRatio()` | Read-only passthrough for the city overview panel. |
| 1053 | 3 | `public double getWaterRatio()` |  |
| 1057 | 3 | `public double getRoadRatio()` |  |
| 1062 | 3 | `public Sectors getSectors()` | Every sector, in the registry's order. |
| 1067 | 3 | `public Markets getMarkets()` | Every goods market. |
| 1072 | 3 | `public InfrastructureManager getInfrastructureManager()` | The road network itself, for the infrastructure screen. |
| 1076 | 3 | `public LandManager getLandManager()` |  |
| 1081 | 8 | `public boolean buyLandBlock()` | Buys the cheapest plot on offer, if the city can afford it. |

### LAND IS BOUGHT IN DOLLARS (0.7.6) (lines 1090-1205)

| line | len | member | says |
|---:|---:|---|---|
| 1131 | 1 | `public boolean isLandPaidFromVault()` | True when land is paid for out of the vault; false - the default - converts cash. |
| 1134 | 1 | `public void setLandPaidFromVault(boolean fromVault)` | The land office's toggle, applied at once to the next purchase. |
| 1144 | 43 | `public boolean buyLandParcel(int parcelId)` | Buys one specific listed plot - the land office screen's action - in US dollars at today's rate, paid the way the toggle says. |
| 1195 | 5 | `private double landPayable(LandParcel parcel)` | The most the city can pay for this parcel today, in local money: its cash, and - paying from the vault - the vault's part of the parcel at today's rate. |
| 1202 | 3 | `public boolean canAffordParcel(LandParcel parcel)` | Whether buyLandParcel() would buy this parcel today, paid the way the toggle says - what the land office colours a plot's tile by; since 0.7.13 its button asks landNeedsFunding() instead. |

### WHEN THE CITY IS SHORT, AND SEVERAL AT ONCE (0.7.13) (lines 1206-1370)

| line | len | member | says |
|---:|---:|---|---|
| 1239 | 5 | `public java.util.List<LandParcel> landShelf()` | The plots on offer in the land office's order: cheapest ground first, per square foot in US dollars - the top-left card first. |
| 1246 | 8 | `public java.util.List<Integer> nextLandParcels(int n)` | The first n plots of landShelf(), by id: what "Buy the next N plots" buys. |
| 1256 | 8 | `public double landPriceUsd(java.util.List<Integer> ids)` | What these plots are listed at together, in US dollars; an id not on offer counts nothing. |
| 1266 | 8 | `public double landPriceLocal(java.util.List<Integer> ids)` | ...and what that is in local money at today's rate - what converting pays. |
| 1281 | 3 | `public double landCashGap(java.util.List<Integer> ids)` | Converting: what the treasury's cash is short of these plots' local price, never below nothing - an overdraft included, as the build screen's buildFundingGap() counts it, so a loan of this much leaves the cash buyLand... |
| 1286 | 3 | `public double landVaultGapUsd(java.util.List<Integer> ids)` | From the vault: what the vault is short of these plots' dollar price, never below nothing. |
| 1291 | 3 | `public boolean landNeedsFunding(java.util.List<Integer> ids)` | True when buying these the way the toggle pays needs money the city does not have: the land office's funding page. |
| 1303 | 4 | `public boolean canAffordLandParcels(java.util.List<Integer> ids)` | True when buyLandParcels() would buy every one of these today: paying from the vault, the cash covers at today's rate the dollars the vault lacks, so what the vault has goes and the rest is converted - each purchase p... |
| 1309 | 3 | `public boolean landTopUpCovers(java.util.List<Integer> ids)` | From the vault: the third way on the funding page - take what the vault has and convert the rest from cash - is on offer, the cash covering it. |
| 1314 | 3 | `public double landTopUpLocal(java.util.List<Integer> ids)` | ...and what that third way converts out of cash: the dollars the vault lacks, in local money at today's rate. |
| 1324 | 18 | `public int buyLandParcels(java.util.List<Integer> ids)` | Buys these plots in the order given, each through buyLandParcel() - paid the way the toggle says, one at a time, as the market's rule has it - and stops at the first it cannot. |
| 1354 | 3 | `public String getLastLandReceipt()` | The last land purchase's receipt, or "" once the month it was made in has turned. |
| 1359 | 3 | `public java.util.List<LandParcel> getLandListing()` | The plots on offer. |
| 1363 | 3 | `public BusinessInvestment getBusinessInvestment()` |  |
| 1367 | 3 | `public String getLastInvestment(String sector)` |  |

### PRIVATE INVESTMENT (lines 1371-1880)

| line | len | member | says |
|---:|---:|---|---|
| 1381 | 91 | `private void runPrivateInvestment()` |  |
| 1488 | 3 | `private void updateHouseholdAccounts()` | The residents' side of the month. |
| 1501 | 3 | `void refreshHouseholdAccounts()` | The same figures, for the load path, without booking a month twice. |
| 1513 | 5 | `private void tellTheSchoolsTheirPrices(TaxPolicy tax)` | Every school kind's tuition scale, from the policy to the schools (0.7.6) - at the month's education step and on the load path, where one scale was told until the nine parted. |
| 1519 | 360 | `private void syncHouseholdAccounts(boolean accrue)` |  |

### THE HOUSEHOLDS BUY CARS - its own class since 2026-09-18: Motoring.java. (lines 1881-1931)

| line | len | member | says |
|---:|---:|---|---|
| 1905 | 1 | `public Motoring getMotoring()` | The car market, for the screens and the harnesses that read past the getters below. |
| 1908 | 1 | `public double getHouseholdCarsBought()` | Cars the households bought this month. |
| 1911 | 1 | `public double getHouseholdCarSpend()` | ...what they paid for them, and what of that left the country. |
| 1912 | 1 | `public double getHouseholdCarImports()` |  |
| 1915 | 1 | `public double getHouseholdCarCredit()` | ...and what of it the bank advanced rather than the household finding. |
| 1918 | 1 | `public double getUsedCarsTraded()` | Cars that changed hands second-hand this month. |
| 1921 | 1 | `public double getUsedCarsOffered()` | ...against what was put up for sale, which in a crash is far more. |
| 1924 | 1 | `public double getUsedCarPrice()` | What one went for. |
| 1927 | 1 | `public double getUsedCarSpend()` | What the buyers paid for them, all in. |
| 1930 | 1 | `public double getUsedCarCredit()` | ...and what of that a lender advanced. |

### THE LUXURY COUNTER (2026-09-17) - its own class since 2026-09-18: LuxuryCounter.java. (lines 1932-1965)

| line | len | member | says |
|---:|---:|---|---|
| 1940 | 1 | `public LuxuryCounter getLuxuryCounter()` | The luxury counter, for the screens and the harnesses that read past the getters below. |
| 1943 | 1 | `public double getMealsServed()` | Meals the kitchens served the households this month. |
| 1946 | 1 | `public double getMealSpend()` | ...and what the households paid for them. |
| 1949 | 1 | `public double getMealPrice()` | ...at this price a meal, struck against the queue at the door. |
| 1952 | 1 | `public double getMealsWanted()` | ...against this many meals they came for. |
| 1955 | 1 | `public double getLuxuriesSold()` | Pieces the households bought over a counter this month. |
| 1958 | 1 | `public double getLuxurySpend()` | ...what they paid for them... |
| 1961 | 1 | `public double getLuxuryPrice()` | ...what one went for... |
| 1964 | 1 | `public double getLuxuryWanted()` | ...and how many they came for, which in a city short of shops is more. |

### THE BANK AND THE EXCHANGE, FROM THE MONTH'S SIDE (lines 1966-2483)

| line | len | member | says |
|---:|---:|---|---|
| 1992 | 12 | `private double capitaliseBank(double wanted)` | Sells the bank's paid-in capital as shares. |
| 2011 | 3 | `private double bankProfitTaxRate()` | What the bank's profit is taxed at: a Commercial Bank is a commercial building, so retail's rate. |
| 2024 | 8 | `private void restoreCellBonds(DataSave loaded)` | Each household cell's own bonds, by the cell's name (0.7.12 round 2). |
| 2041 | 6 | `public double dividendDueFor(String sector)` | What a company's owners are due this month off its books, before its till is asked: payDividends()'s figure, which the clearing reads ahead of it (0.7.12 round 6) - the owners are paid out of the till after the market... |
| 2049 | 22 | `private double dividendDue(int c)` | One company's (not the bank's): see payDividends(). |
| 2077 | 3 | `public double purchaseBudget(Sector s)` | What a sector can pay for as the markets clear this month (0.7.12 round 6): EconomyManager.purchaseBudget(), with the dividend the month will pay its owners first. |
| 2109 | 57 | `private void payDividends()` | Pays every company's owners on its last closed month. |
| 2174 | 16 | `private void tradeShares()` | The exchange's month: the desk quotes, the leavers, the world, the households and the companies trade, and the bank carries what is left at the closing mark. |
| 2251 | 96 | `private void refreshBank()` | Re-reads the bank off the city it is banking. |
| 2360 | 11 | `private void strikeBankBonds()` | THE BANK'S BONDS AND ITS BOOK'S CONCENTRATION (0.7.12), re-read off the market and the lender: the bonds at what they cost it, weighed as loans to their issuers for the months left (RISK_BUSINESS, Bank.maturityWeight(... |
| 2373 | 25 | `private java.util.Map<String, Bank.Exposure> bankExposures()` | Every sector's exposure as the bank's concentration reads it. |
| 2400 | 8 | `private java.util.Map<String, Double> concentrationCharges()` | What the book's concentration adds to each sector's loans this month, a year (Bank.concentrationCharge() at a business loan's term). |
| 2417 | 16 | `private void provideForLosses()` | THE BANK SETS ASIDE FOR WHAT IT WILL LOSE (0.7.8): every book's allowance struck from its borrowers as they stand at the month's end, and the month's write-offs drawn against what each book held - see Bank, THE ALLOWA... |
| 2444 | 17 | `private java.util.Map<String, double[]> bankReadings()` | What the bank provides on, per sector (0.7.8): {what the sector owed over its last quarter, what it owned over it, what it owes now} - the quarter's leverage, the curve its allowance and stage are read at, and the deb... |
| 2473 | 10 | `private java.util.Map<String, double[]> sectorPositions()` | Each sector's name to {what it owes, its assets} - the month-end reading the bank files (provideForLosses()): the figures the restructure rule judged it on this month (BusinessDebtManager.getAssets(), struck at the in... |

### THE CITY'S FUND AND THE BANK'S RESCUE (0.7.14) (lines 2484-2621)

| line | len | member | says |
|---:|---:|---|---|
| 2526 | 1 | `public double getOwnersWipedAbroadThisMonth()` | What the audit declares as the world's shares wiped out since its last strike (0.7.14). |
| 2529 | 3 | `public double bankRecapitalisationNeeded()` | What it would cost to put the bank back on its feet, right now: the hole and the capital to reopen for a failed bank; what takes a standing one under its minimum back to its target. |
| 2534 | 3 | `public double bankResolutionAdvance()` | What the central bank would advance of a resolution now: what the treasury's cash does not cover of it. |
| 2539 | 3 | `public boolean canResolveBank()` | True while the bank is failed and waiting for the city: what the Bank tab's button is shown on, and what resolveBank() resolves. |
| 2544 | 3 | `private void resolveIfAutomatic()` | When the treasury's setting is automatic and the bank has failed, the city resolves it now. |
| 2571 | 41 | `public double resolveBank()` | THE CITY RESOLVES A FAILED BANK FOR ITS SHARES: the Bank tab's button, and the automatic setting's month. |
| 2614 | 4 | `public TreasuryFund.Resolution getLastResolution()` | The city's last resolution, or null if it has never resolved the bank. |
| 2620 | 1 | `public double cityStakeInBank()` | The city's stake in its bank, 0-1: both of the fund's books over the shares in issue. |

### the preferred offer (lines 2622-2735)

| line | len | member | says |
|---:|---:|---|---|
| 2630 | 12 | `private void considerPreferredOffer()` | A STANDING BANK UNDER ITS MINIMUM ASKS, AND AN ANSWERED OFFER WAITS A QUARTER: at the bottom of the month, once the bank's month is final. |
| 2644 | 1 | `public boolean isPreferredOfferPending()` | True while the bank's offer waits for the player's answer. |
| 2647 | 1 | `public double preferredOfferSize()` | What the bank asks for now: Bank.preferredOfferSize(). |
| 2650 | 1 | `public double preferredOfferWarrantValue()` | The warrants' reach, in money at the strike: Bank.WARRANT_SHARE of the offer - "warrants on D$Y of its shares". |
| 2653 | 1 | `public double preferredOfferStrike()` | ...and their strike: a share at last month's price, the exchange's. |
| 2656 | 1 | `public double preferredOfferShortBy()` | What the treasury is short of the offer: what the funding page raises first. |
| 2666 | 19 | `public boolean acceptPreferredOffer()` | THE CITY ACCEPTS: the treasury buys the preferred, a purchase (TreasuryLine.BANK_CAPITAL), into the fund's rescue book. |
| 2687 | 6 | `public void declinePreferredOffer()` | THE CITY DECLINES: the offer comes back after a quarter while the bank is still under its minimum. |
| 2706 | 24 | `void settleThePreferred()` | THE PREFERRED'S MONTH, after the shares have traded (0.7.14; Jerus: "Sell new shares to repay"): every block at its third anniversary redeemed whole at par with its arrears (Bank.redeemDuePreferred()), from the bank's... |
| 2732 | 3 | `public double bankVolatility()` | The bank share's volatility a year, off the history's monthly prices (TreasuryFund.annualVolatility()): the warrants' value reads it. |

### what the fund is worth (lines 2736-2795)

| line | len | member | says |
|---:|---:|---|---|
| 2739 | 1 | `public double fundSharesValue()` | Its shares, both books, at the exchange's price. |
| 2741 | 1 | `public double fundMarketSharesValue()` | ...its market book's alone. |
| 2743 | 5 | `public double fundRescueSharesValue()` | ...its rescue book's shares. |
| 2749 | 1 | `public double fundBondsValue()` | Its bonds, at the market's valuation. |
| 2751 | 1 | `public double fundPreferredValue()` | Its preferred, at par. |
| 2753 | 3 | `public double fundWarrantsValue()` | Its warrants, at Black-Scholes. |
| 2757 | 1 | `public double fundRescueValue()` | Its rescue book: the rescue shares, the preferred and the warrants. |
| 2759 | 3 | `public double fundValue()` | Everything it holds, both books, and its cash: what its transfer is struck on. |
| 2763 | 4 | `public double fundEquityShare()` | Its market book's equity share, 0-1, of its market book and cash: what its rebalancing band reads. |
| 2768 | 1 | `public double fundTransferDue()` | What this month's transfer is on the fund as it stands: TreasuryFund.transferOn(fundValue()). |
| 2771 | 3 | `public double fundCompanyValue(int company)` | One company's shares in the fund, both books, at the exchange's price: the Fund page's line for it. |
| 2775 | 3 | `public double fundCompanyRescueValue(int company)` | ...its rescue book's part. |
| 2779 | 1 | `public double fundCompanyShare(int company)` | The share of a company the city owns, 0-1, both books over the shares in issue. |
| 2781 | 4 | `public double fundCompanyMarketShare(int company)` | ...its market book's alone: what the rule's cap, TreasuryFund.OWNERSHIP_LIMIT, reads. |
| 2786 | 3 | `public double fundBondValue(CorporateBond b)` | One bond's face in the fund, at the market's valuation (BondMarket.modelPrice()). |
| 2790 | 5 | `public double fundBondsValueOf(String issuer)` | ...and one issuer's bonds in the fund, all of them. |

### the dial (lines 2796-2892)

| line | len | member | says |
|---:|---:|---|---|
| 2799 | 1 | `public double getFundDial()` | The fund's dial, 0 to TreasuryFund.MAX_DIAL of the year's surplus. |
| 2800 | 1 | `public void setFundDial(double dial)` |  |
| 2803 | 1 | `public TreasuryFund.RescueMode getRescueMode()` | The treasury's setting for a failed bank. |
| 2804 | 1 | `public void setRescueMode(TreasuryFund.RescueMode mode)` |  |
| 2815 | 14 | `public double monthOfSpending()` | ONE MONTH OF THE TREASURY'S OWN SPENDING: the budget's expenses over the last Rollover.NETTING_MONTHS, as the history keeps them (revenue less the surplus, month by month), a month's worth. |
| 2831 | 9 | `public double surplusThisYearSoFar()` | The budget surplus of this calendar year's closed months so far, as the history keeps it: what the fund's year-end pay-in will read. |
| 2846 | 4 | `public double fundReservation()` | WHAT THE ROLLOVER LEAVES FOR THE FUND, now: the dial's share of this calendar year's surplus so far (TreasuryFund.reservedFor()) - nothing once this year's pay-in is made, and nothing at a dial of 0. |
| 2874 | 18 | `private void fundYearEnd()` | THE DIAL'S PAY-IN, ONCE A YEAR, AT THE CALENDAR'S YEAR END: the first press after December has closed, before the rollover runs. |

### the hand (lines 2893-2938)

| line | len | member | says |
|---:|---:|---|---|
| 2896 | 9 | `public double fundPayIn(double amount)` | The player pays into the fund from the treasury's cash: a transfer, journalled, not spending. |
| 2907 | 9 | `public double fundDrawOut(double amount)` | ...and draws out of it, what its cash holds: a transfer, journalled, not revenue. |
| 2918 | 3 | `public void fundBuyShares(int company, double money)` | Buys a company's shares with this much of the fund's cash, at fair value, at the next step: good for the month. |
| 2923 | 4 | `public void fundSellShares(int company, double shares)` | Sells this many of the fund's shares of a company - its market book first, then its rescue book - at fair value, at the next step. |
| 2929 | 3 | `public void fundBuyBond(int bondId, double money)` | Buys a bond with this much of the fund's cash, at its value, at the next step. |
| 2934 | 4 | `public void fundSellBond(int bondId, double face)` | Sells this much face of a bond the fund holds, at its value, at the next step. |

### an Insane founding (lines 2939-3047)

| line | len | member | says |
|---:|---:|---|---|
| 2955 | 6 | `private void foundTheLandBond()` | AN INSANE CITY OWES THE WORLD FOR ITS GROUND (0.7.14): the model's own twenty-year dollar term loan, LongTermBond abroad, its coupon fixed at Founding.INSANE_LAND_COUPON (Jerus's 3%) on Founding.landBondUsd() - STARTI... |
| 2991 | 13 | `public static DebtQuote[] dayZeroQuotes(Founding founding, double cashNeeded)` | WHAT A FIRST BOND COSTS A CITY WITH NOTHING (0.7.14): the build screen's two offers - the Game.BUILD_BOND_YEARS bond and the Game.BUILD_NOTE_MONTHS note - for `cashNeeded`, quoted on a city founded as given and not ye... |
| 3014 | 9 | `public double buyForeignCurrency(double amount)` | The treasury buys foreign currency, adding to the city's reserves. |
| 3033 | 8 | `public double sellForeignCurrency(double amount)` | ...and sells it, which is what defending a currency actually consists of. |
| 3043 | 4 | `private void refreshLand()` | Tells the investment engine what land is left to sell, right now. |

### SHRINKING (lines 3048-3237)

| line | len | member | says |
|---:|---:|---|---|
| 3067 | 69 | `private void runRetirement()` |  |
| 3143 | 14 | `private void closeBranch()` | Closes one of the bank's branches (0.7.11, round 2): the building retired by the path any retired building takes (retire()), sold by its owner, retail. |
| 3160 | 1 | `public int getBranchesClosed()` |  |
| 3173 | 64 | `private int retire(BusinessInvestment.Decision decision, Investor seller, boolean distress)` | Scraps what the decision named, sells the plot back to the city, and sells the building's material to the builders (0.7.8 - see THE PLANT'S MATERIAL, TO THE BUILDERS). |

### THE CONSTRUCTION WARNING (lines 3238-3287)

| line | len | member | says |
|---:|---:|---|---|
| 3258 | 4 | `public void restoreConstructionShedding(int month, double points)` | Puts the warning back where it stood. |
| 3270 | 8 | `public boolean isConstructionShedding()` | Whether the city should be told construction is dismantling itself. |
| 3279 | 1 | `public int getConstructionShedMonth()` |  |
| 3280 | 1 | `public double getConstructionShedPoints()` |  |
| 3283 | 4 | `public void acknowledgeConstructionShedding()` | The player has seen it. |

### PRIVATE INVESTMENT WITH NOWHERE TO GO (lines 3288-3615)

| line | len | member | says |
|---:|---:|---|---|
| 3308 | 3 | `public java.util.Set<String> getLandBlockedSectors()` |  |
| 3321 | 3 | `public java.util.Set<String> getRefusedOnPrice()` |  |
| 3341 | 6 | `public boolean isPrivateInvestmentLandLocked()` |  |
| 3349 | 3 | `public void acknowledgeLandLock()` | The player has seen it. |
| 3356 | 3 | `public double getLastWriteOff()` |  |
| 3395 | 3 | `private void consider(BusinessInvestment.Decision decision, Investor payer)` | Applies the brake, then builds. |
| 3405 | 210 | `private void consider(BusinessInvestment.Decision decision, Investor payer, String label)` | more than one question a month. |

### THE LANDLORDS' MORTGAGES (0.7.11) (lines 3616-3772)

| line | len | member | says |
|---:|---:|---|---|
| 3655 | 5 | `private boolean buysOnMortgage(BusinessInvestment.Decision decision)` | True for an order bought on an insured mortgage: a residential building the landlords order. |
| 3669 | 57 | `private void considerOnMortgage(BusinessInvestment.Decision decision, String slot, double cash, double perUnitRent)` | The landlords' order, on a mortgage: the largest slice of it the landlord's own funds can put down on and the lender's test passes, scanning down from what the planner asked for as consider() does - and the advisor's ... |
| 3735 | 3 | `public java.util.Set<String> getRefusedByLender()` |  |
| 3739 | 3 | `public java.util.Set<String> getHeldForDownPayment()` |  |
| 3752 | 20 | `private Investor mortgageInvestor(final String sector, final String[] refusal)` | One sector's cash and its insured-mortgage lender, as a payer: what sectorInvestor() does with its till, and a mortgage where that would borrow a loan. |

### THE PLANT'S MATERIAL, TO THE BUILDERS (0.7.8) (lines 3773-3850)

| line | len | member | says |
|---:|---:|---|---|
| 3809 | 2 | **type** `public record Salvage(String seller, String building, int buildings, double units, double price, double pai...` | One building type's material, sold this month: whose, how many buildings, the units, the price a unit, what the builders paid for what they could afford, the units they could not pay for, and which rule retired it. |
| 3817 | 3 | `public java.util.List<Salvage> getSalvageThisMonth()` | Every sale of scrapped plant's material this month. |
| 3822 | 3 | `public double getSalvageThisMonth(String sector)` | What this sector's cash moved by on scrapped plant's material this month: paid out by the builders, received by the seller as a negative - signed like what it spent on premises, of which it is a part. |
| 3827 | 1 | `public double getSalvageUsedThisMonth()` | Units of material the builders drew from their salvage this month instead of buying. |
| 3830 | 20 | `private double sellMaterialToTheBuilders(BusinessInvestment.Decision decision, int scrapped, Investor seller, boolean distress)` |  |

### A BOND OR THE BANK, FOR A BUILDING (0.7.12) (lines 3851-4675)

| line | len | member | says |
|---:|---:|---|---|
| 3864 | 5 | `private BusinessDebtManager.Plan financeProject(String sector, double amount, double loanRate)` | How a building's borrowing would be financed now: a bond, the bank, or both. |
| 3881 | 20 | `public static String financingWords(BusinessDebtManager.Plan plan)` | THE ADVISOR'S WORDS FOR HOW A BUILDING WAS FINANCED (0.7.12): the bond and its coupon against the bank's rate when a bond was no dearer, and the bank's part beside it; the bank, and what the book would have cleared at... |
| 3903 | 3 | `public static double grossedForFee(double purpose)` | The loan that hands `purpose` once its fee is kept back (0.7.12, round 5): the shortfall desk's gross-up (0.7.7), for a building's loan. |
| 3908 | 94 | `private Investor sectorInvestor(final String sector)` | Wraps one sector's cash and credit line as a payer. |
| 4004 | 3 | `public ServicesManager getServicesManager()` | Read-only access for the utilities and construction screens. |
| 4024 | 17 | `public int getConstructionOutput()` | This month's effective construction output: the sector's capacity scaled by how well it is staffed and by what the roads will carry. |
| 4053 | 5 | `public double getHousingMaintenancePoints()` | Construction points the housing stock consumes just standing there. |
| 4070 | 3 | `public double getMaintenancePoints()` | The same figure for EVERY category, which is what comes off the month. |
| 4107 | 4 | `public int getBuildingOutput()` | What is left of the month's output for the SITES. |
| 4126 | 5 | `private void chargeFreight()` | THE RAILWAY'S MONTH, and the band it leaves behind. |
| 4157 | 71 | `private void chargeBuildingMaintenance()` | The month's repairs: real estate pays, construction is paid, and the materials are actually consumed. |
| 4238 | 1 | `public double getCityMaintenancePaid()` |  |
| 4243 | 4 | `public int getConstructionMaterials()` |  |
| 4247 | 4 | `public double getInterestRate()` |  |
| 4251 | 3 | `public boolean isGraphsEnabled()` |  |
| 4254 | 3 | `public boolean isReportsEnabled()` |  |
| 4281 | 85 | `public int simulateMonths(int months)` | Runs up to {@code months} monthly cycles, stopping early only if a month throws (below). |
| 4368 | 29 | `private void captureSkipSnapshot(boolean atStart)` | One end of the fast-forward diff. |
| 4397 | 1 | `public boolean hasNewReceipt()` |  |
| 4398 | 1 | `public void clearReceipt()` |  |
| 4400 | 3 | `public double calculateTotalCost(BuildingsTemplate selected, int quantity)` |  |
| 4414 | 41 | **type** `public static final class BuildQuote` | Everything the build screen shows about an order, worked out ONCE. |
| 4433 | 18 | `BuildQuote(int quantity, double sticker, double materialsNeeded, double materialsInStock, Markets.Draw boughtIn, double plantPr...` _(in Game.BuildQuote)_ |  |
| 4453 | 1 | `public double boughtInCost()` _(in Game.BuildQuote)_ | What had to be bought beyond the yard - the plant's and the world's together. |
| 4463 | 18 | `public BuildQuote quoteBuild(BuildingsTemplate selected, int quantity)` | THE INVOICE. |
| 4505 | 17 | `private Markets.Draw drawMaterials(double units, boolean yardFirst)` | Takes material for the month's building work or a repair: the city's yard first, for free; then the materials plant, at the market price; then the world. |
| 4533 | 3 | `public void drawSiteMaterials(double units)` | The month's draw for the sites: what the work the crews just delivered was owed in material, summed by BuildingManager.advanceConstruction() over every stack in proportion to the points it advanced. |
| 4542 | 6 | `private void deliverYardToSites(BuildingsTemplate template, double needed)` | The yard's share of a new order, delivered to the sites the day it is placed - the units the quote priced as free (see quoteBuild), taken off what the sites still owe so the monthly draws buy only the rest. |
| 4557 | 3 | `public void recogniseSiteWork(double earned, double pointsDelivered)` | ...and the work those sites delivered is recognised in the same breath, into the same ledger. |
| 4580 | 91 | `private void processBuildOrder(BuildingsTemplate selected, int quantity, boolean noConstruction)` | them, and charge nothing for the construction. |
| 4674 | 1 | **type** `public enum BuildResult` |  |

### HALF THE PRACTICE, BEFORE THE DOORS OPEN (2026-09-12) (lines 4676-5297)

| line | len | member | says |
|---:|---:|---|---|
| 4691 | 4 | `public double licencesNeededFor(BuildingsTemplate template, int quantity)` | Spare licences the city holds against what this order would need staffed. |
| 4703 | 5 | `public boolean hasLicencesFor(BuildingsTemplate template, int quantity)` | Can the city staff the core of this building's practice? |
| 4716 | 12 | `public int minesCommitted()` | Mines standing, being built, or already ordered. |
| 4736 | 7 | `public boolean hasDepositFor(BuildingsTemplate template, int quantity)` | Whether this order can go ahead on the ore the city owns. |
| 4753 | 52 | `public boolean buildFor(Investor payer, BuildingsTemplate template, int quantity)` | A build order paid for by someone other than the city. |
| 4815 | 1 | `public Investor getGovernmentInvestor()` |  |
| 4818 | 1 | `public Investor getSectorInvestor(String sector)` | One sector's till and credit as a payer, as the month's investment builds with it (sectorInvestor()): what a harness borrows a building's loan through. |
| 4820 | 39 | `public BuildResult buildStack(BuildingsTemplate template, int quantity, boolean noConstruction)` |  |
| 4860 | 3 | `public double landNeededFor(BuildingsTemplate template, int quantity)` | Square feet a build order of this size would need. |
| 4869 | 3 | `public double buildFundingGap(BuildingsTemplate template, int quantity)` | What the treasury is short of for this order: its invoice less the cash on hand, never below nothing. |
| 4874 | 3 | `public double getMaterialsUsed()` | Units of material the last order will draw, for the receipt. |
| 4877 | 3 | `public double getTotalBuildingCost()` |  |
| 4880 | 3 | `public String getBuildingName()` |  |
| 4883 | 3 | `public int getBuildQuantity()` |  |
| 4888 | 3 | `public int getReceiptSerial()` | Which receipt this is. |
| 4901 | 111 | `public DebtQuote quoteTBill(double amount, int duration, double rounding)` | What a T-Bill of this size would cost. |
| 5020 | 6 | `public String handleTBillLogic(double amount, int duration, double rounding)` | Books a T-Bill on exactly the terms quoted. |
| 5032 | 11 | `private String issueNote(DebtQuote quote)` | The booking every note shares - handleTBillLogic()'s, and since 0.7.13 the rollover's, which books the quote it sized (issueForRollover()). |
| 5050 | 36 | `public DebtQuote quoteMediumBond(double requestedAmount, int duration, double rounding)` | What a medium-term bond of this size would cost. |
| 5088 | 15 | `public String handleMediumBondLogic(double requestedAmount, int duration, double rounding)` | Books a medium-term bond on exactly the terms quoted. |
| 5146 | 3 | `private static double longBondCouponYield(double marketRate, int duration)` | Long bonds pair a LOW monthly coupon with a redemption premium: you repay more than you borrowed, but your monthly payment is well below a medium bond's. |
| 5158 | 13 | `private double longBondPvPerFace(double marketRate, int duration)` | The present value of one dollar of long-bond face, at a given rate. |
| 5179 | 16 | `private double faceValueOfLongBond(double amount, int duration, double rounding, double marketRate)` | What a long bond of this size would put on the books at a given rate. |
| 5208 | 35 | `public DebtQuote quoteLongBond(double amount, int duration, double rounding)` | What a long bond of this size would cost. |
| 5249 | 5 | `private double longBondNetProceeds(double faceValue, double marketRate, int duration)` | What the city actually banks for a long bond of this face: its worth at the rate struck, less the fees, to the cent. |
| 5256 | 13 | `private DebtQuote longBondQuote(double requested, int duration, double marketRate, double before, double faceValue, double rece...` | A long bond's quote, once its face and proceeds are known: the coupon, the monthly bill and the cost of the credit, for both quotes. |
| 5271 | 7 | `public String handleLongBondLogic(double amount, int duration, double rounding)` | Books a long bond on exactly the terms quoted. |
| 5283 | 14 | `private String issueLongBond(DebtQuote quote)` | The booking both long-bond issues share. |

### A TERM BOND SIZED TO THE CASH IT BRINGS (0.7.10) (lines 5298-5381)

| line | len | member | says |
|---:|---:|---|---|
| 5326 | 8 | `private double longBondFaceForProceeds(double cashNeeded, int duration, double rounding, double marketRate)` | Face value whose net proceeds cover cashNeeded at this rate, fees included, rounded up to the granule. |
| 5343 | 29 | `public DebtQuote quoteLongBondForCash(double cashNeeded, int duration, double rounding)` | What a term bond whose CASH covers cashNeeded would cost. |
| 5374 | 7 | `public String handleLongBondForCash(double cashNeeded, int duration, double rounding)` | Books a term bond sized to the cash, on exactly the terms quoted - the build screen's bond. |

### BORROWING IN SOMEBODY ELSE'S MONEY (lines 5382-5688)

| line | len | member | says |
|---:|---:|---|---|
| 5424 | 12 | `public double defaultOnForeignDebt(String because)` | WALKING AWAY FROM THE DOLLARS. |
| 5460 | 8 | `private void checkForeignSolvency()` | The city cannot pay, so it does not. |
| 5475 | 4 | `public double realRateDifferential()` | The city's real rate against the world's, on today's figures (0.7.2): the dial less the city's inflation, less the world's base rate less the world's realised inflation - the one definition the month hands the currenc... |
| 5487 | 3 | `public double realDepositRate()` | What savers earn after inflation (0.7.3): the bank's deposit rate less the year's inflation, the same PriceIndex.inflation() the real rate differential and the parity read - the one definition the month strikes the ho... |
| 5499 | 3 | `public double spendFactor()` | The share of their spending above subsistence the households plan at today's real deposit rate (0.7.3): HouseholdBalance.spendFactor() on realDepositRate(). |
| 5504 | 1 | `public boolean foreignWindowOpen()` | True if anybody abroad will lend the city a dollar today. |
| 5507 | 1 | `public String foreignWindowReason()` | Why not, in words, or null. |
| 5514 | 55 | `public DebtQuote quoteForeign(String type, double requestedUsd, int duration, double rounding)` | What a USD bond of this size would cost. |
| 5581 | 10 | `private double selfPricedForeignRate(double face, int months, java.util.function.DoubleFunction<Debt> shape)` | THE DOLLAR QUOTE'S FIXED POINT (0.7.2): the rate at which the world's curve, with this paper booked at that rate's coupon, reads that rate back - DebtManager.quoteForeignRate(Debt, months), walked from the principal-o... |
| 5601 | 70 | `public String handleForeignLogic(String type, double requestedUsd, int duration, double rounding, boolean holdAsReserves)` | Books a USD bond on exactly the terms quoted. |
| 5678 | 10 | `public DebtQuote quoteDebt(String type, double amount, int duration, double rounding)` | The quote for whichever instrument, by the name the menus use. |

### THE SERIAL AND THE DOLLAR PAPER, SIZED TO THE CASH THEY BRING (0.7.13) (lines 5689-5763)

| line | len | member | says |
|---:|---:|---|---|
| 5710 | 12 | `public DebtQuote quoteMediumBondForCash(double cashNeeded, int duration, double rounding)` | A serial bond whose CASH covers cashNeeded: quoteMediumBond() at the face that brings it. |
| 5728 | 14 | `public DebtQuote quoteForeignForCash(String type, double cashNeededUsd, int duration, double rounding)` | A dollar note, serial or term loan whose CASH covers cashNeededUsd: quoteForeign() at the face that brings it, on the world's curve at its maturity. |
| 5744 | 8 | `private double foreignProceedsPerFace(String type, double rate, int duration)` | What a unit of a dollar instrument's face banks at this rate, net of the spread - never under MIN_PROCEEDS_PER_FACE, so the search above always moves. |
| 5754 | 9 | `public String handleForeignForCash(String type, double cashNeededUsd, int duration, double rounding, boolean holdAsReserves)` | Books the dollar paper quoteForeignForCash() quotes, on exactly its terms - the land office's dollar offers. |

### ROLLING WHAT FALLS DUE (0.7.13) (lines 5764-7137)

| line | len | member | says |
|---:|---:|---|---|
| 5841 | 1 | `public Rollover getRollover()` | The treasury's rollover: its setting, the ledger of what it netted, and its record. |
| 5844 | 1 | `public Rollover.Mode getRolloverMode()` | The setting, as the Finances tab's chips read it. |
| 5847 | 1 | `public void setRolloverMode(Rollover.Mode mode)` | ...and as they set it, applied at the next press. |
| 5855 | 8 | `public double surplusOverLastYear()` | The budget surplus the city ran over the last Rollover.NETTING_MONTHS, in local money: the national accounts' balance month by month, as the history keeps it (HistorySave's "surplus"), over as many months as the city ... |
| 5865 | 1 | **type** `private record RollsInto(String type, int term, boolean foreign, boolean atHomeForDollars)` | What a piece falling due rolls into: the quote functions' instrument, its term in their units, abroad or not, and whether it is dollar paper rolled at home. |
| 5867 | 13 | `private RollsInto rollsInto(Debt paper, Rollover.Mode mode, boolean windowOpen)` |  |
| 5882 | 7 | `static int nearestTermMaturity(int years)` | The one of LongTermBond.MATURITIES nearest this many years; the shorter on a tie. |
| 5896 | 69 | `public Rollover.Plan rolloverPlan()` | What the rollover will do at the next press, on the city as it stands: what falls due, the year's surplus and what earlier rollovers netted of it, S, and the issues. |
| 5967 | 54 | `private void rollMaturities()` | The rollover, at the press: rolloverPlan() booked, issue by issue, through the existing quotes, and printed to the log. |
| 6030 | 9 | `private DebtQuote rolloverQuote(String type, int term, boolean abroad, double cash)` | The paper whose CASH covers a rollover issue's share, on the existing quotes, at the build screen's granules: a note by quoteTBill(), whose ask is the cash; a serial by quoteMediumBondForCash(); a term loan by quoteLo... |
| 6041 | 4 | `private double localFace(boolean abroad, DebtQuote quote)` | A rollover quote's face in local money: a dollar quote's at the day's rate. |
| 6052 | 12 | `private String issueForRollover(Rollover.Issue issue, DebtQuote quote)` | One of the rollover's issues, booked on exactly the terms of its quote (rolloverQuote()). |
| 6067 | 3 | `private void printPopulationInfo()` | printers |
| 6072 | 12 | `private void printSectorInfo()` | Every sector's operations page, as text - the same lines the screen draws. |
| 6085 | 3 | `private void printUtilityInfo()` |  |
| 6089 | 3 | `private void printCityStats()` |  |
| 6096 | 765 | `private void nextMonth()` |  |
| 6862 | 247 | `private void startOfMonthUpdate()` |  |
| 7109 | 26 | `private void printStartOfMonth()` |  |

### WHAT IT COSTS TO GO TO MARKET AT ALL (lines 7138-7270)

| line | len | member | says |
|---:|---:|---|---|
| 7187 | 4 | `private double issuanceFee()` | The fee in today's money. |
| 7212 | 4 | `public double costOfIssuance(double faceValue)` | Taken off the proceeds at issue, never added to what is owed. |
| 7224 | 4 | `private double netProceeds(double faceValue, double annualRate, int months)` | What the city actually banks for a note of this face. |
| 7244 | 8 | `private double faceForNetProceeds(double cashNeeded, double annualRate, int months)` | Face value whose NET proceeds cover cashNeeded - fees included. |
| 7266 | 4 | `public double minimumIssueSize()` | The smallest deal worth doing, which grows with the city. |

### THE CITY'S CREDIT, IN THE LANGUAGE PEOPLE USE (lines 7271-7521)

| line | len | member | says |
|---:|---:|---|---|
| 7285 | 16 | `public String getCreditRating()` |  |
| 7303 | 11 | `public String getCreditOutlook()` | What the rating means, in one line, for the screen. |
| 7326 | 3 | `private void pushCostOfFundsToTheDebtMarket()` | Hands the debt market what money costs the bank, from the one struck figure - the floor under the city's paper. |
| 7340 | 7 | `private void priceTheDebtMarket()` | Hands the debt market everything it prices against. |
| 7362 | 27 | `private void strikeGovernmentBooks()` | The government's books, struck once, at the bottom of the month. |
| 7390 | 78 | `private void finalUpdateEconomy()` |  |
| 7470 | 4 | `private void updateConstructionCost()` |  |
| 7481 | 5 | `void refreshJobs()` | Recounts the jobs the city's buildings offer. |
| 7492 | 3 | `void updatePopulation()` | Recounts the posts on offer. |
| 7496 | 4 | `public int getHouseholdCapacity()` |  |
| 7500 | 4 | `public int getStoreCapacity()` |  |
| 7504 | 3 | `public int[] getJobs()` |  |
| 7509 | 3 | `public BuildingManager getBuildingManager()` |  |

### DEMOGRAPHICS - LOAD-BEARING SINCE THE SWITCH (lines 7522-8102)

| line | len | member | says |
|---:|---:|---|---|
| 7551 | 5 | `private static FamilyModel rememberingFamilies()` | The game's families keep what still fits from month to month; a bare model does not. |
| 7587 | 1 | `public PopulationCohorts getCohorts()` |  |
| 7588 | 1 | `public FamilyModel getFamilies()` |  |
| 7589 | 1 | `public HouseholdBalance getHouseholdBalance()` |  |
| 7590 | 1 | `public Migration getMigration()` |  |
| 7591 | 1 | `public Health getHealth()` |  |
| 7592 | 1 | `public Healthcare getHealthcare()` |  |
| 7616 | 486 | `void advanceDemographics()` | Ages the city, moves people in and out, and rebuilds the households. |

### WHO IS AT RISK OF OFFENDING (2026-09-11) - its own class since 2026-09-18: Offending.java. (lines 8103-8115)

| line | len | member | says |
|---:|---:|---|---|
| 8111 | 1 | `public Offending getOffending()` | The offenders' sort, for the harnesses that read past the getter below. |
| 8114 | 1 | `public double unhousedShareOfCity()` | The share of the city with no home: the unhoused, and the orphans. |

### THE READINGS THE MONTH TAKES OF THE CITY (lines 8116-8404)

| line | len | member | says |
|---:|---:|---|---|
| 8132 | 5 | `private double careCoverage(CareType care, double[] fill)` | How much of the people who need a kind of care the city can actually give. |
| 8151 | 11 | `public double careAffordability(CareType care)` | Of the people a kind of care would serve, the share who live in a household that can pay its fee (2026-09-19). |
| 8171 | 22 | `private double careHeads(Household c, CareType care)` | The places one household of a cell needs of a kind of care: its shape's members in the bands the care serves, at the care's places per head. |
| 8213 | 15 | `private double burialShare()` | The share of the dead whose families would choose a plot over an urn. |
| 8239 | 9 | `public void recordCompletions(java.util.List<BuildingManager.Completion> finished)` | Files the month's finished buildings. |
| 8249 | 3 | `public BuildLog getBuildLog()` |  |
| 8256 | 3 | `public TimeSkipReport getSkipReport()` |  |
| 8281 | 1 | `public Unemployment getUnemployment()` |  |
| 8285 | 1 | `public Sickness getSickness()` |  |
| 8293 | 1 | `public Crime getCrime()` |  |
| 8297 | 1 | `public double getLastOrphanDeaths()` |  |
| 8298 | 1 | `public double getLastUnhousedDeaths()` |  |
| 8300 | 6 | `{ ... }` |  |
| 8308 | 15 | `private double outsideHouseholds(Household c)` | How many households are in a cell the family matrix does not hold. |
| 8333 | 7 | `private double outsideDependantsOf(Household c)` | Dependants in one household of a cell the family matrix does not hold. |
| 8342 | 13 | `private double rentShareOf(Household c)` | The share of a door's rent one household of a cell pays: see HouseholdBalance.setRentShares(). |
| 8363 | 9 | `private double housedShareOf(Household c)` | How much of a cell has a home, for the bank's account fee (0.7.7): all of a household that has one - alone or sharing - and none of the orphans, the prisoners or the unhoused. |
| 8374 | 10 | `private double[] rowDoors()` | The doors each row of the household books pays rent on. |
| 8387 | 1 | `public double getStudentLoansLent()` |  |
| 8388 | 1 | `public double getStudentLoansRepaid()` |  |
| 8389 | 1 | `public double getStudentLoansWrittenOff()` |  |
| 8393 | 1 | `public double getStudentLoanInterest()` |  |
| 8396 | 5 | `private double unskilledWage()` | What an unskilled post pays a month, today: EI's cap and the grant are struck against it. |
| 8403 | 1 | `public double getUnskilledWage()` | The same wage, for the Schools page to name what a share of it is. |

### the price of a place (2026-09-21) (lines 8405-8537)

| line | len | member | says |
|---:|---:|---|---|
| 8420 | 4 | `public double studentGrantBill()` | The month's grant bill under the city's basis and amount. |
| 8426 | 5 | `public double studentGrantBillUnder(TaxPolicy.GrantBasis basis, double amount)` | ...and under any basis and amount, for a preview: the same rule, the same four figures. |
| 8433 | 4 | `public double grantPerStudentUnder(TaxPolicy.GrantBasis basis, double amount)` | What that comes to per student - the bill over this month's students, or nothing with none. |
| 8445 | 15 | `public double grantAmountAs(TaxPolicy.GrantBasis basis)` | Today's grant per student, re-expressed as an amount under another basis: the number the Schools page starts the amount dial at when a basis is picked, so picking one changes nothing until the amount is moved. |
| 8489 | 1 | `public Equity getEquity()` |  |
| 8493 | 1 | `public Exchange getExchange()` |  |
| 8501 | 1 | `public BondMarket getBondMarket()` |  |
| 8511 | 1 | `public TreasuryFund getFund()` |  |
| 8527 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |

### WHAT THE CITY EATS - its own class since 2026-09-18: CityBasket.java. (lines 8538-8561)

| line | len | member | says |
|---:|---:|---|---|
| 8548 | 4 | `public Consumption getConsumption()` | Loaded on first ask, so a caller never gets an empty model by arriving early. |
| 8557 | 1 | `public CityBasket getCityBasket()` | The basket's maker, for the harnesses that read past the getter below. |
| 8560 | 1 | `public java.util.Map<Good, Double> cityBasketPerHead()` | Kilograms of each of the thirteen in ONE person-month, averaged over the city by headcount. |

### THE WORLD, THE BANK, AND THE FIELDS THE MONTH KEEPS (lines 8562-8857)

| line | len | member | says |
|---:|---:|---|---|
| 8574 | 1 | `public WorldEconomy getWorldEconomy()` |  |
| 8576 | 1 | `public PriceIndex getPriceIndex()` |  |
| 8578 | 1 | `public CapitalFlows getCapitalFlows()` |  |
| 8585 | 6 | `public double yearlyDepreciation()` | How far the currency has fallen over the last year, as a share. |
| 8592 | 1 | `public ForeignAccounts getForeignAccounts()` |  |
| 8601 | 1 | `public Bank getBank()` |  |
| 8614 | 1 | `public CentralBank getCentralBank()` |  |
| 8628 | 4 | `public double getM2()` | M2: what the public holds - the bank's deposits, the households', the sectors' and the world's, plus currency, which is none. |
| 8634 | 1 | `public double getHouseholdDeposits()` | What the households have banked - their savings, which are their deposits. |
| 8637 | 5 | `public double getSectorDeposits()` | What the businesses have banked: each sector's cash, counted only when in credit (an overdraft is a loan, not a negative deposit). |
| 8644 | 3 | `public HistorySave getHistorySave()` | For tests and for the graph screen. |
| 8648 | 3 | `public DemolitionLog getDemolitionLog()` |  |
| 8652 | 3 | `public HouseholdAccounts getHouseholds()` |  |
| 8719 | 3 | `public double getInvestedThisMonth(String sector)` |  |
| 8723 | 3 | `public EconomyManager getEconomyManager()` |  |
| 8726 | 3 | `public PopulationManager getPopulationManager()` |  |
| 8730 | 1 | `public LabourMarket getLabourMarket()` |  |
| 8731 | 1 | `public Education getEducation()` |  |
| 8747 | 11 | `public void repriceLabour()` | Re-prices labour, then hands the new wages to everyone who pays them. |
| 8772 | 41 | `private void applyMigrationSkills()` | Moves the workforce's skill mix by who arrived and who left. |
| 8814 | 6 | `private static double[] scaled(double[] values, double by)` |  |
| 8832 | 23 | `public void recordMonth()` | Files this month in the graph history. |
| 8856 | 1 | `public Inbox getInbox()` |  |
| 8857 | 1 | `public SectorBooks getSectorBooks()` |  |

### the save system (lines 8858-9263)

| line | len | member | says |
|---:|---:|---|---|
| 8869 | 3 | `public int getMonthsUntilAutosave()` |  |
| 8885 | 11 | `public void autosave(String reason)` | Writes the autosave slot, if there is a city to write. |
| 8897 | 249 | `public void save(int slot, String slotName)` |  |
| 9155 | 5 | `public String takeSkipFailure()` |  |
| 9164 | 1 | `public GameFiles.Result getLastSaveResult()` |  |
| 9166 | 1 | `public GameFiles getGameFiles()` |  |
| 9185 | 17 | `public GameFiles.Result[][] writeBooks()` | Writes the run out as plain text, one row a year and one row a decade, and each book's two tables again as CSV beside it (0.7.16). |
| 9203 | 18 | `public void sendBuildingSave()` |  |
| 9224 | 16 | `public void loadBuildings()` |  |
| 9254 | 9 | `public void subtractCash(double amount)` | calculations |

### PAYING THE WORLD BACK (lines 9264-9404)

| line | len | member | says |
|---:|---:|---|---|
| 9291 | 1 | `double getForeignDebtRaisedThisMonth()` |  |
| 9292 | 1 | `public double getForeignPrincipalRepaidThisMonth()` |  |
| 9293 | 1 | `public double getForeignInterestPaidThisMonth()` |  |
| 9300 | 5 | `public void repayForeignPrincipal(double usd)` | A slice of USD principal, repaid. |
| 9322 | 5 | `public void payForeignInterest(double usd)` | A USD coupon. |
| 9389 | 1 | `public double getCityPaperUnsettled()` | What the bank owes the treasury for paper it has taken and not yet settled: sold between the presses and not yet paid for at the bottom of a month. |
| 9398 | 1 | `public double getCityPaperSettled()` |  |
| 9399 | 1 | `double getCityDebtRaisedThisMonth()` |  |
| 9400 | 1 | `public double getCityPrincipalRepaidThisMonth()` |  |
| 9402 | 1 | `public double getBankPrincipalRepaidThisMonth()` | ...of which the commercial bank's share, which is what it takes at the settle (0.7.1). |

### THE HOLDERS ARE PAID (0.7.1) (lines 9405-9480)

| line | len | member | says |
|---:|---:|---|---|
| 9433 | 1 | `public double getCouponsToHouseholds()` |  |
| 9434 | 1 | `public double getPrincipalToHouseholds()` |  |
| 9435 | 1 | `public double getHouseholdsBoughtPaper()` |  |
| 9446 | 6 | `private double[] holderShares(Debt paper, double owed)` | Of a payment on this paper, the households' and the central bank's shares, struck before it: each holder's principal over what is outstanding, times the payment. |
| 9454 | 11 | `public void payDomesticCoupon(Debt paper, double owed)` | A coupon on the city's own paper, split by holder. |
| 9467 | 13 | `public void payDomesticPrincipal(Debt paper, double owed)` | Principal on the city's own paper, split by holder: the bank's through subtractCash(), the rest paid now and taken off their holdings. |

### the desk, for the households (lines 9481-9506)

| line | len | member | says |
|---:|---:|---|---|
| 9491 | 15 | `private void desksBuysHouseholdPaper(double face, double cash)` | THE BANK BUYS THE HOUSEHOLDS' PAPER (0.7.1), for the waterfall, the spread gone, or a household on its way out of the city: this much face for this much cash, off every piece's household share pro rata, onto the bank'... |

### THE HOUSEHOLDS TAKE THEIR SHARE (0.7.1) (lines 9507-9563)

| line | len | member | says |
|---:|---:|---|---|
| 9524 | 39 | `private double householdsTakeTheirShare()` |  |

### THE HOLDINGS DIAL, AT THE TOP OF THE MONTH (0.7.1) (lines 9564-9710)

| line | len | member | says |
|---:|---:|---|---|
| 9618 | 56 | `private void openMarketOperation()` |  |
| 9682 | 28 | `private void buyPaperFromHouseholds(double wanted)` | The rest of a purchase the bank could not fill, from the households' term paper (0.7.15; see THE HOLDINGS DIAL, AT THE TOP OF THE MONTH): pro rata across the pieces they hold that are settled and pay no principal this... |

### THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) (lines 9711-9946)

| line | len | member | says |
|---:|---:|---|---|
| 9816 | 1 | **type** `private record ParAlone(RollsInto into, double par, DebtQuote quote)` | Its par alone in one paper, on the terms of the rollover's quote for it. |
| 9819 | 5 | `private static double centralBankShareOf(Debt paper, double principal)` | Its share of a payment of this much principal on this paper: the principal times what it holds over what is outstanding - the split the payment makes (holderShares()). |
| 9826 | 6 | `public double centralBankParFallingDue()` | The central bank's par in what falls due next month, whether it rolls it or not. |
| 9838 | 5 | `public double centralBankOverItsDial()` | How far the central bank's holding is over its dial: what it holds past the dial's share of the term paper, or nothing within the holdings step's own tolerance (openMarketOperation()). |
| 9845 | 4 | `private double centralBankRolls(double par)` | Of this much par of its own falling due, what it rolls at issue: all of it, less what it holds over its dial. |
| 9851 | 4 | `private boolean termPaperSoldBetweenPresses()` | True if any of the city's own term paper has been sold between the presses and not yet settled: what the central bank's par is added on to. |
| 9864 | 23 | `private void strikeParAlone()` | At the press, with nothing to add its par on to: the paper each maturing piece it holds part of rolls into in the same structure (rollsInto(), at home), its par in each, priced at the rollover's quote for that paper a... |
| 9895 | 51 | `private void rollCentralBankAtIssue()` | THE CENTRAL BANK'S ADD-ON, inside the month's window: the par struck at the press, added on to the city's term paper sold between the presses, pro rata to its face, each at its issue's price on each unit of face; or i... |

### a buyback's holders outside the pools (lines 9947-9968)

| line | len | member | says |
|---:|---:|---|---|
| 9962 | 3 | `public double getBuybackUnsettled()` | What the treasury has paid out of the pools for a buyback and the audit has not yet seen leave. |
| 9966 | 1 | `public double getBuybackToHouseholds()` |  |
| 9967 | 1 | `public double getBuybackAbroad()` |  |

### WHAT THE TREASURY ACTUALLY DID (lines 9969-10173)

| line | len | member | says |
|---:|---:|---|---|
| 10040 | 13 | `private void takeTreasuryMonth()` |  |
| 10055 | 1 | `public boolean hasTreasuryMonth()` | True once a month has closed. |
| 10057 | 1 | `public double getTreasuryOpening()` |  |
| 10058 | 1 | `public double getTreasuryClosing()` |  |
| 10059 | 1 | `public double getTreasuryRaised()` |  |
| 10060 | 1 | `public double getTreasuryRepaid()` |  |
| 10061 | 1 | `public double getTreasurySurplus()` |  |
| 10064 | 1 | `public double getTreasuryChange()` | What the balance actually did, which is the figure a player watches. |
| 10082 | 4 | `public double getTreasuryUnexplained()` | Everything the three named flows do not explain - the whole of the bridge's last row, "Everything else the treasury did". |
| 10094 | 3 | `public java.util.List<TreasuryJournal.Entry> getTreasuryJournal()` | Last month's journal: the non-budget movements by name, in the order they happened, signed as the treasury sees them. |
| 10099 | 1 | `public TreasuryJournal getTreasuryJournalBook()` | The journal itself, for the harnesses that read past the getter above. |
| 10107 | 3 | `public double getTreasuryResidual()` | What the journal does not explain: the residual after the three named rows AND the journal's lines. |
| 10111 | 5 | `double[] treasuryMonthToSave()` |  |
| 10117 | 9 | `void restoreTreasuryMonth(double[] saved)` |  |
| 10151 | 1 | `public double getPostAuditDrift()` | Money that moved after the audit struck. |
| 10154 | 1 | `public String getPostAuditDriftPool()` | Which pool moved most after the strike, for naming the culprit. |
| 10156 | 1 | `public double getPostAuditDriftWorst()` |  |
| 10157 | 1 | `public MoneyAudit.Result getLastMoneyAudit()` |  |
| 10158 | 4 | `public void InterestExpense(double amount)` |  |
| 10163 | 3 | `public DebtManager getDebtManager()` |  |
| 10168 | 4 | `public void printEndOfTurn()` |  |

### THE CENTRAL BANK AND THE TREASURY (0.7.0) (lines 10174-10426)

| line | len | member | says |
|---:|---:|---|---|
| 10234 | 37 | `private void settleTreasury()` | The treasury's month with its central bank, first thing - inside the audit's window, so every dollar made or destroyed here is one the month declares. |
| 10283 | 3 | `public double treasuryPays(TreasuryLine line, double amount)` | EVERY PAYMENT THE TREASURY MAKES, through one door (0.7.0). |
| 10288 | 13 | `double treasuryPays(TreasuryLine line, double amount, String payee)` | ...and with whom a refusal is owed to - a sector's key, or null. |
| 10310 | 4 | `public double discretionaryRoom()` | What the treasury may spend on something that is not a promise, now. |
| 10322 | 17 | `private void payDownArrears()` | Pays down what is owed, oldest first, out of cash above zero. |
| 10348 | 15 | `private double payStudentGrants(double bill)` | The month's student grants, arrears first. |
| 10373 | 6 | `private double payEiBenefits()` | The month's EI, struck again on the pool the month opens with at the dial as the player left it (Unemployment.restrikeBenefits()) and paid in full - a promise - at the top of the month, where the out of work are credi... |
| 10380 | 3 | `private static String arrearsKey(TreasuryLine line, String payee)` |  |
| 10384 | 8 | `private static TreasuryLine arrearsLine(String key)` |  |
| 10394 | 7 | `private Sector arrearsPayee(String key)` | Whose till an arrear is owed to: the named sector, or the builders for the construction lines. |
| 10403 | 1 | `public boolean hasArrears()` | True while anything is owed and unpaid. |
| 10406 | 5 | `public double getArrearsTotal()` | Everything owed and unpaid. |
| 10413 | 8 | `public java.util.Map<TreasuryLine, Double> getArrearsByLine()` | Owed and unpaid, by line - the Government tab's list, in TreasuryLine's order. |
| 10422 | 1 | `public double getArrearsRefusedThisMonth()` |  |
| 10423 | 1 | `public double getArrearsPaidThisMonth()` |  |
| 10424 | 1 | `public double getArrearsRefusedLifetime()` |  |
| 10425 | 1 | `public double getArrearsPaidLifetime()` |  |

### BUYING YOUR OWN DEBT BACK (lines 10427-10551)

| line | len | member | says |
|---:|---:|---|---|
| 10440 | 6 | `public double quoteRepurchase(Debt debt)` | What one bond would cost to clear right now: at the curve's rate for the months it has left (0.7.1) - DebtManager.marketValue(), the same curve it was issued on, which is what keeps a round trip neutral. |
| 10448 | 4 | `public double repurchaseGain(Debt debt)` | What the city would book as a gain (positive) or loss (negative). |
| 10473 | 70 | `public double repurchaseDebt(Debt debt)` | Buys one bond back and takes it off the books. |

### WHY LAND IS NOT IN THE RENT FLOOR (lines 10552-12147)

| line | len | member | says |
|---:|---:|---|---|
| 10620 | 17 | `public double marginalHousingCost()` | What it costs to supply one more person of dwelling capacity, today. |
| 10638 | 296 | `private void rebuildSimulationState()` |  |
| 10976 | 1125 | `public void loadGame(int slot)` | Load game |
| 12106 | 41 | `public void loadHistory(int slot)` |  |

### THE CURRENCY REFORM (lines 12148-12379)

| line | len | member | says |
|---:|---:|---|---|
| 12154 | 1 | `public Denomination getDenomination()` |  |
| 12157 | 3 | `public boolean canReformCurrency()` | Whether the reform button should be showing. |
| 12188 | 168 | `public boolean reformCurrency(double factor)` | Lops zeros off the currency: one new dollar for `factor` old ones. |
| 12367 | 4 | `boolean reformCurrencyForTest(double factor)` | The reform without the price-level gate, for a harness. |

