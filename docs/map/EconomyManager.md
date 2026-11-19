# EconomyManager.java - 2,747 lines · 274 methods · 10 constants · model

`ham/citybuildersim/EconomyManager.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The private economy: every sector, every market, the credit desk, the
> tax policy and the national accounts, and the month they run in.
> 
> SINCE THE SECTOR TEMPLATE (2026-09-11) this class holds a REGISTRY and
> loops it. It used to hold five handlers by name and loop them by hand in
> eleven places - pricing credit, charging property tax, pushing balance
> sheets, settling loans, striking VAT, banking the month, restoring a save
> - and each of those was a place a seventh sector would have been
> forgotten. See Sectors, Sector and claude/the-sector-template.md.
> 
> WHAT IS STILL HERE BY NAME: the two housing figures the landlords need
> from the family model and the land office (see updateHousing()), and
> the shops' budget constraint, which Game hands in from the households.
> Both are inputs to a sector that is not a factory, and they arrive here
> because this class already talks to everything that produces them.

**Uses:** [Sector](Sector.md) (64), [BuildingsTemplate](BuildingsTemplate.md) (27), [Good](Good.md) (16), [Currency](Currency.md) (16), [BuildingType](BuildingType.md) (13), [NationalAccounts](NationalAccounts.md) (11), [TaxPolicy](TaxPolicy.md) (9), [Markets](Markets.md) (5), [SalesTaxLedger](SalesTaxLedger.md) (5), [BuildingManager](BuildingManager.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Traffic](Traffic.md) (4), [Zoning](Zoning.md) (4), [SectorState](SectorState.md) (4), [Sectors](Sectors.md) (3), [JobType](JobType.md) (3), [LandManager](LandManager.md) (3), [RealEstate](RealEstate.md) (3), [PriceLevel](PriceLevel.md) (3), [Statement](Statement.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [Retail](Retail.md) (3), [BondMarket](BondMarket.md) (3), [Equity](Equity.md) (3), [WageBand](WageBand.md) (3), [GoodsMarket](GoodsMarket.md) (2), [FamilyModel](FamilyModel.md) (2), [SupplierCredit](SupplierCredit.md) (2), [GameLog](GameLog.md) (2), [SocialSecurity](SocialSecurity.md) (2)... and 8 more

**Used by (70):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BankCheck](BankCheck.md), [BankFailureCheck](BankFailureCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildAdvice](BuildAdvice.md), [BuildCardCheck](BuildCardCheck.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CityNeeds](CityNeeds.md), [CopperBufferCheck](CopperBufferCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DiscoveryCheck](DiscoveryCheck.md), [EducationCheck](EducationCheck.md), [FoodProcessingCheck](FoodProcessingCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [GovernmentView](GovernmentView.md), [GovernmentViewCheck](GovernmentViewCheck.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HousingCheck](HousingCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LandClassCheck](LandClassCheck.md), [LongPlaytest](LongPlaytest.md), [Materials](Materials.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [OutwardInvestment](OutwardInvestment.md), [PassThroughCheck](PassThroughCheck.md), [PeopleScreen](PeopleScreen.md), [PlaytestFixCheck](PlaytestFixCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PrizeCheck](PrizeCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [Recipes](Recipes.md), [ResearchViewCheck](ResearchViewCheck.md), [RoadCheck](RoadCheck.md), [RunWorksCheck](RunWorksCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorStatementCheck](SectorStatementCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TradeScreen](TradeScreen.md), [TreasuryCheck](TreasuryCheck.md), [WellCheck](WellCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 31 | THE CITY'S PARTS |
| 62 | WHAT THE CITY TELLS THE ECONOMY EACH MONTH |
| 146 | · utilities |
| 246 | · the world |
| 265 | THE D$ THE GOODS ARE STRUCK AT (AN1, 2026-10-22) |
| 328 | · the land |
| 403 | · the land by class of ground (ET3, 2026-10-25) |
| 495 | HOUSING - what the landlords need from the family model and the land office |
| 616 | CASH BY NAME - the seam almost everything routes through |
| 630 | THE MONTH, AT THE TOP |
| 726 | · the statements' splits (0.7.74, R1 and R2) |
| 777 | · property tax |
| 828 | · maintenance |
| 929 | · the builders' sales tax |
| 1003 | THE REBATES ON A NEW HOME, AND THE CITY'S (0.7.19, revised) |
| 1219 | · the strike |
| 1379 | INSOLVENCY, at the bottom of the investment step |
| 1515 | WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS |
| 1532 | THE OWNERS AND THE MONEY ABROAD |
| 1686 | THE MONTH, IN THE MIDDLE AND AT THE BOTTOM |
| 1712 | THE CITY'S BOOKS |
| 1836 | · WHAT THE POLICY TAB ASKS (0.7.36) |
| 1903 | · EI and the student grant (2026-09-11) |
| 2002 | · the services |
| 2045 | THE TRANSIT BOOKS (2026-09-16) |
| 2112 | · pensions |
| 2155 | THE NATIONAL ACCOUNTS |
| 2540 | CONVENIENCES - the prices the screens and the harnesses ask for by name |
| 2557 | SAVE AND RESTORE |
| 2590 | PRINTERS, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 295 | `EconomyManager.PASS_THROUGH` | `1 / 36.0` | How much of the gap, in logs, between the D$ the city's goods were last struck at and today's D$ they close in a month: a thirty-sixth, so half of a move in two years and 29% of it in the first - the dock's 64% in a y... |
| 839 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, ...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 853 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 1062 | `EconomyManager.PBRH_MIN_UNITS` | `4` | A building with this many dwellings or more is purpose-built rental housing (CRA: "at least 4 residential units each with a private kitchen, a private bathroom, and a private living area"). |
| 1064 | `EconomyManager.NRRP_SHARE` | `.36` | The new residential rental property rebate's share of the tax on a unit: 36% (Excise Tax Act section 256.2(3)(a); CRA RC4231). |
| 1066 | `EconomyManager.NRRP_CAP` | `6.3 * PriceLevel.FACTOR` | ...and its most a unit, in founding thousands: $6,300. |
| 1068 | `EconomyManager.NRRP_FULL_BELOW` | `350 * PriceLevel.FACTOR` | ...in full for a unit worth up to this, in founding thousands: $350,000. |
| 1070 | `EconomyManager.NRRP_NONE_FROM` | `450 * PriceLevel.FACTOR` | ...and none for a unit worth this or more: $450,000. |
| 1541 | `EconomyManager.BUDGET_NOISE` | `1e-12` | The share of its terms (what the settle leaves it, what it could raise) under which a purchase budget is arithmetic noise and reads nothing (est.: a double carries sixteen digits, and a budget is the sum of two figure... |
| 2658 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `private final BuildingManager buildingManager` |  |
| 36 | `private final Markets markets` |  |
| 37 | `private final Sectors sectors` |  |
| 38 | `private final BusinessDebtManager businessDebtManager` |  |
| 39 | `private final TaxPolicy taxPolicy` |  |
| 40 | `private final SalesTaxLedger salesTaxLedger` |  |
| 41 | `private final NationalAccounts nationalAccounts` |  |
| 66 | `private double cash` |  |
| 67 | `private long totalJobs` |  |
| 68 | `private long population` |  |
| 69 | `private long households` |  |
| 70 | `private double totalWage` |  |
| 71 | `private final double[] fillRate` |  |
| 74 | `private final double[] wageRates` | What ONE job of each tier costs a month, as PopulationManager last set it. |
| 77 | `private double[] staffedWagePerType` | The wage bill per job type, staffed, for the banded wage tax. |
| 80 | `private long[] bankJobs` | The bank's posts, so its tellers can be charged to the bank and not to the shops. |
| 95 | `private double cityCrudeTonnes, cityCrudeImports, cityCrudeExports` | The city's own crude for the month's accounts (0.7.85; StrategicReserve): the tonnes its strategic reserve holds - among the goods held, beside the sectors' - and what the strike settled of its fill bought abroad and ... |
| 148 | `private double pricePerWatt` |  |
| 149 | `private double pricePerWaterUnit` |  |
| 255 | `private double exchangeRate` | Every world price in the game is quoted by the world in ITS money and converted at this rate - times the world's own price level, which is how world inflation rides in. |
| 298 | `private double struckRate` | The D$ per US$ the goods were last struck at; 0 until the first strike, which strikes at the D$ itself. |
| 301 | `private double passThroughSpeed` | The speed the goods follow the D$ at: PASS_THROUGH, unless a harness's twin is told otherwise (not saved; a reload strikes at PASS_THROUGH). |
| 335 | `private double landPricePerSqFt` | What the city currently charges for a square foot, so assessed values track the price the player sets. |
| 364 | `private Zoning zoning` | The city's zones, and what each holds of the free ground (LandManager.heldBy()); Game wires both, a bare economy has none. |
| 365 | `private java.util.function.ToDoubleFunction<String> zoneHeld` |  |
| 416 | `private boolean landFlatOnly` |  |
| 417 | `private double foothillPricePerSqFt, steepPricePerSqFt` |  |
| 418 | `private LandManager.ReliefUse landUse` |  |
| 419 | `private java.util.function.Predicate<BuildingsTemplate> worksTest` |  |
| 420 | `private java.util.function.Predicate<BuildingsTemplate> runsTest` |  |
| 421 | `private java.util.function.ToDoubleFunction<BuildingsTemplate> plotPrice` |  |
| 499 | `private double householdCount` |  |
| 500 | `private double marginalHousingCost` |  |
| 501 | `private double occupiedHomes` |  |
| 502 | `private double studioSeekers, familySeekers, studioSeekerHeads, familySeekerHeads` |  |
| 741 | `private final Map<String, double[]> interestByKind` |  |
| 742 | `private final Map<String, double[][]> debtByKind` |  |
| 745 | `private int creditMonth` | The month of the last credit settle, which the bonds' maturities are read against (settleBusinessCredit()). |
| 764 | `private final Map<String, double[][]> debtMovedAtSheet` | ...AND, SINCE 0.7.75, WHAT HAD MOVED IT AND WHAT THE SHEET WAS PRICED AT (the sector statements' R7 and R6), read at the same push: the running totals of its principal borrowed, repaid and written off by kind (Busines... |
| 765 | `private final Map<String, double[]> valuedAt` |  |
| 766 | `private final Map<String, double[]> valuedByClass` |  |
| 779 | `private double totalPropertyTax` |  |
| 780 | `private final Map<String, Double> propertyTaxBySector` |  |
| 846 | `private double maintenanceBillTotal` |  |
| 847 | `private double maintenanceMaterialsTotal` |  |
| 848 | `private double maintenancePointsTotal` |  |
| 849 | `private double cityMaintenanceBill` |  |
| 850 | `private double bankMaintenanceBill` |  |
| 851 | `private final Map<String, Double> maintenanceBySector` |  |
| 889 | `private java.util.function.DoubleSupplier streetUtilisation` | GRAVEL'S REPAIRS RISE WITH ITS TRAFFIC (FR7 2.2, Jerus's star 5, 2026-10-18): a Gravel Road is charged at the share of BuildingsTemplate.gravelRepairsAt() at the streets' utilisation as it stands (InfrastructureManage... |
| 1073 | `private java.util.function.DoubleSupplier foundingToToday` | Today's money for a founding dollar: the month's price index over the unit. |
| 1383 | `private double overdraftForgivenThisMonth` |  |
| 1384 | `private final Map<String, Double> overdraftForgivenBySector` |  |
| 1385 | `private final Map<String, Double> overdraftForgivenThisMonthBySector` |  |
| 1519 | `private final Map<String, Double> depositInterestPaid` |  |
| 1536 | `private OutwardInvestment outward` |  |
| 1639 | `private BondMarket bondMarket` | The bond market (0.7.12), wired by Game: its maturities are paid at the credit settle, and the other sectors' bonds a company holds are among its assets. |
| 1653 | `private Equity equity` |  |
| 1657 | `private final Map<String, Double> equityRaised` |  |
| 1658 | `private final Map<String, Double> dividendsPaid` |  |
| 1659 | `private final Map<String, Double> sharesBoughtBack` |  |
| 1664 | `private final Map<String, Double> stolen` | Taken from each sector's till by thieves this month. |
| 1716 | `private double salesTax` |  |
| 1717 | `private double totalWageTax` |  |
| 1718 | `private double totalBankTax` |  |
| 1719 | `private double totalContributions` |  |
| 1720 | `private double interest` |  |
| 1724 | `private double foreignInterest` | CD9 (CD7-E, 2026-11-08): the month's coupons abroad, every money's, in D$: the foreign paper's interest, paid at once (Game.payForeignInterest through TreasuryLine.FOREIGN_INTEREST) and handed here where the budget's ... |
| 1725 | `private double utilityIncome` |  |
| 1726 | `private double debt` |  |
| 1727 | `private double GDP` |  |
| 1728 | `private double yearGDP` |  |
| 1905 | `private double totalEiPremiums` |  |
| 1906 | `private double eiBenefits` |  |
| 1907 | `private double studentGrants` |  |
| 1910 | `private double totalHealthPremiums` | The month's health premium off every wage, struck in getTaxIncome() beside the EI premium (2026-09-19). |
| 1923 | `private double studentLoanInterest` | The interest the graduates paid the treasury on their student loans this month (2026-09-21): a revenue line beside the premiums, set by Game off the household ledger the month it is struck. |
| 1932 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO BUDGET LINES (0.7.0), set by Game where the central bank settles with the treasury at the top of the month: the remittance in, the interest on the advances out. |
| 1943 | `private double fundTransfer` | THE TRANSFER FROM THE CITY'S FUND (0.7.14), set by Game where the treasury settles, for the central bank's lines' reason: Game moves the cash there. |
| 1949 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43), set by Game where the treasury pays it, after the month's sale - for the central bank's lines' reason: Game moves the cash there. |
| 1963 | `private double cityRepairsPaid, arrearsPaidDown, reserveCrudeBought, reserveCrudeSold` | THE FOUR LINES 0.7.102 TOOK INSIDE THE BUDGET'S TOTALS (A15), set by Game where the treasury pays or takes them, for the central bank's lines' reason: the repairs to the city's own buildings as paid (chargeBuildingMai... |
| 1975 | `private double publicIpCharges` | The licence the city's own patented works paid abroad at the last strike (R2c-P; Game.settlePublicLicences()), a budget line, an import and part of the services valued at cost. |
| 2004 | `private double healthcareBill, healthcareFees` |  |
| 2013 | `private double educationBill, educationFees` |  |
| 2026 | `private double safetyBill` | What the police and the prisons cost this month: payroll and upkeep, no fees - nobody pays to be policed or jailed. |
| 2036 | `private double researchPayroll, researchCosts` | What the city's research grants cost this month (R3; Game.settleResearchBill()): the pay of the posts they bought (inside the households' wage bill already, PopulationManager.getTotalWage() counts the posts as jobs) a... |
| 2079 | `private double transitBill, transitFares` |  |
| 2087 | `private double transitUpkeep` | The part of the transit bill that is upkeep (HL1): the infrastructure's and the ports', bought of Business Services; the rest is the crews' wages. |
| 2108 | `private double subsidiesPaid` | What the city paid this month to hold protected sectors at break-even. |
| 2114 | `private double seniors` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 2719 | **type** `public class EconomyManager` | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. |

### THE CITY'S PARTS (lines 31-61)

| line | len | member | says |
|---:|---:|---|---|
| 43 | 6 | `public EconomyManager(BuildingManager buildingManager)` |  |
| 50 | 1 | `public Sectors getSectors()` |  |
| 51 | 1 | `public Markets getMarkets()` |  |
| 52 | 1 | `public GoodsMarket getMarket(Good g)` |  |
| 53 | 1 | `public BusinessDebtManager getBusinessDebtManager()` |  |
| 54 | 1 | `public TaxPolicy getTaxPolicy()` |  |
| 55 | 1 | `public SalesTaxLedger getSalesTaxLedger()` |  |
| 56 | 1 | `public NationalAccounts getNationalAccounts()` |  |
| 57 | 1 | `public BuildingManager getBuildingManager()` |  |
| 60 | 1 | `public Sector sector(String key)` | A sector by its saved name, or null. |

### WHAT THE CITY TELLS THE ECONOMY EACH MONTH (lines 62-145)

| line | len | member | says |
|---:|---:|---|---|
| 82 | 1 | `public void setTotalJobs(long jobs)` |  |
| 83 | 1 | `public void setPopulation(long pop)` |  |
| 84 | 1 | `public void setHouseholds(long houseCap)` |  |
| 85 | 1 | `public void setCash(int money)` |  |
| 86 | 1 | `public void setTotalWage(double wage)` |  |
| 97 | 5 | `public void setCityCrude(double tonnes, double imports, double exports)` |  |
| 102 | 3 | `public void setWageDetail(double[] staffedPerType)` |  |
| 105 | 1 | `public double[] getStaffedWagePerType()` |  |
| 106 | 1 | `public long getPopulation()` |  |
| 108 | 5 | `public void updateJobFillRate(double[] fill)` |  |
| 120 | 9 | `public void updateWages(double[] wagePerType, long[] bankPosts)` | The wage schedule, handed to every sector with its own posts. |
| 131 | 1 | `public double[] getWageRates()` | A copy, so nothing downstream can quietly rewrite the schedule. |
| 138 | 7 | `public double getBankPayroll()` | What the bank's own staff cost this month. |

### utilities (lines 146-245)

| line | len | member | says |
|---:|---:|---|---|
| 151 | 1 | `public double getPricePerWatt()` |  |
| 152 | 1 | `public double getPricePerWaterUnit()` |  |
| 154 | 4 | `public void setPricePerWatt(double price)` |  |
| 159 | 4 | `public void setPricePerWaterUnit(double price)` |  |
| 164 | 1 | `public void setEnergyRatio(double ratio)` |  |
| 165 | 1 | `public void setWaterRatio(double ratio)` |  |
| 167 | 1 | `public void setRoadRatio(double ratio)` | The road network's throughput, handed to everyone whose goods move on it. |
| 183 | 11 | `public void setRoadRatio(InfrastructureManager roads, BuildingManager buildings)` | ...and the same thing done properly, once the streams exist: each sector feels the congestion IT generates. |
| 195 | 1 | `public void setHealthRatio(double ratio)` | What is left of the workforce once this month's illness is taken off. |
| 198 | 1 | `public double getHealthRatio()` | What the sectors are currently running at, for the harnesses. |
| 212 | 12 | `public void setElectricityConsumption()` | Every sector's draw, off its own buildings. |
| 226 | 5 | `public double getSectorPowerSold()` | The month's power the works sold the grid, as the sectors' statements booked it (0.7.112): what the city's utility pays them. |
| 233 | 5 | `public double getSectorElectricityCharges()` | The month's power bills, as the sectors' statements booked them. |
| 240 | 5 | `public double getSectorWaterCharges()` | The month's water bills, likewise. |

### the world (lines 246-264)

| line | len | member | says |
|---:|---:|---|---|
| 257 | 5 | `public void setExchangeRate(double rate)` |  |
| 263 | 1 | `public double getExchangeRate()` |  |

### THE D$ THE GOODS ARE STRUCK AT (AN1, 2026-10-22) (lines 265-327)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 4 | `public void strikeExchangeRate(double rate, double worldLevel)` | The month's strike: the goods' D$ moves PASS_THROUGH of the way to `rate` in logs (at once on the first strike, or after a save that did not carry it), and every world price is struck at it times the world's own price... |
| 315 | 3 | `public static double passThrough(double struck, double rate, double speed)` | The rule alone: `struck` moved `speed` of the way to `rate`, in logs. |
| 320 | 1 | `public double getStruckRate()` | The D$ per US$ the goods were last struck at (0 before the first strike). |
| 323 | 1 | `public void setStruckRate(double rate)` | The load path's half: the D$ the goods were struck at when the city was saved (0 on a save without it: the next strike is at the D$). |
| 326 | 1 | `void setPassThroughForTest(double speed)` | For a harness's twin only: the speed its goods follow the D$ at (one is the rule before AN1). |

### the land (lines 328-402)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 338 | 1 | `public double getLandPricePerSqFt()` |  |
| 345 | 4 | `public double landValueOf(Sector s)` | What a sector's land is worth at the city's current price - on its books; the roll reads assessedLandValueOf() since 0.7.107. |
| 367 | 4 | `public void setZoning(Zoning zoning, java.util.function.ToDoubleFunction<String> held)` |  |
| 373 | 9 | `public double assessedLandValueOf(Sector s)` | A sector's land as the roll values it, before the share the roll carries: its zone's used ground at the zone's price, the rest at the going price. |
| 384 | 12 | `public double groundPriceFor(BuildingsTemplate t)` | What a square foot of ground under one more of these costs its owner and is assessed at: its zone's price while the zone holds its plot, the going price past it - never below nothing. |
| 398 | 4 | `public double landValueOf(BuildingType category)` | What a city-owned category's land is worth (by the ground it stands on in a city with foothill or steep ground, ET3). |

### the land by class of ground (ET3, 2026-10-25) (lines 403-494)

| line | len | member | says |
|---:|---:|---|---|
| 424 | 7 | `public void wireLandByRelief(java.util.function.Predicate<BuildingsTemplate> works, java.util.function.Predicate<BuildingsTempl...` | Wired once by Game: which templates are works (Game.standsAnywhere()) and which are runs (Game.isRun(), ET4), and what a square foot of one more of a template's plot costs where it would fall (LandManager.priceFor() o... |
| 437 | 5 | `public void setLandByRelief(boolean flatOnly, double foothillPrice, double steepPrice, LandManager.ReliefUse use)` | The land office's classes of ground as the books are to read them: whether the city owns only flat ground, what a square foot of foothill and of steep ground sells at (LandManager.getFoothillPricePerSqFt(), getSteepPr... |
| 444 | 4 | `public void setLandUse(boolean flatOnly, LandManager.ReliefUse use)` | ...the ground alone, the prices as they were pushed: Game reads it again as the month closes, when the month's orders have put up their buildings. |
| 450 | 1 | `public boolean valuesByRelief()` | Whether the books value land by class: the city owns foothill or steep ground and its use has been pushed. |
| 453 | 3 | `public double priceOfClass(int c)` | The price a square foot of one class of ground is valued at: 0 flat (getLandPricePerSqFt()), 1 foothill, 2 steep and very steep. |
| 458 | 13 | `private double[] landByRelief(double buildings, double works, double runs)` | The class areas {flat, foothill, slope} of `buildings` square feet of buildings' ground, `works` of works' and `runs` of runs' (ET4), pro rata to the city's use of each. |
| 473 | 7 | `public double[] landByReliefOf(Sector s)` | A sector's land, square feet, by the ground it stands on: {flat, foothill, slope} - its buildings' ground at the city's buildings' mix, its works' at the city's works', its runs' (ET4) at the city's runs' (above). |
| 482 | 7 | `public double[] landByReliefOf(BuildingType category)` | ...and a city-owned category's, the same way. |
| 491 | 3 | `private double valueOfAreas(double[] a)` | Areas {flat, foothill, slope} at the three class prices. |

### HOUSING - what the landlords need from the family model and the land office (lines 495-615)

| line | len | member | says |
|---:|---:|---|---|
| 504 | 1 | `public void setHouseholdCount(double count)` |  |
| 505 | 1 | `public void setMarginalHousingCost(double perCap)` |  |
| 506 | 1 | `public double getMarginalHousingCost()` |  |
| 507 | 1 | `public void setOccupiedHomes(double occupied)` |  |
| 508 | 6 | `public void setHousingSeekers(double studio, double family, double studioHeads, double familyHeads)` |  |
| 514 | 1 | `public double getStudioSeekers()` |  |
| 515 | 1 | `public double getFamilySeekers()` |  |
| 523 | 21 | `public void updateHousing()` | Hands the landlords and the shops what the month looks like: how many people there are, how many doors, who is chasing them and what a new home costs. |
| 557 | 3 | `public double housingBuildHurdle(BuildingsTemplate t)` | What one of these has to earn a month before it is worth putting up. |
| 569 | 18 | `public double housingCarry(BuildingsTemplate t)` | WHAT ONE OF THESE COSTS TO HOLD A MONTH, before any interest: its maintenance, RealEstate.MAINTENANCE_PER_YEAR on the structure, and its property tax, the property rate on the structure and its plot. |
| 589 | 26 | `public void repriceHousingCosts()` | The cheapest way to house one more person, land included, per segment. |

### CASH BY NAME - the seam almost everything routes through (lines 616-629)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 4 | `public double getSectorCash(String key)` |  |
| 625 | 4 | `public void setSectorCash(String key, double cash)` |  |

### THE MONTH, AT THE TOP (lines 630-725)

| line | len | member | says |
|---:|---:|---|---|
| 643 | 3 | `public void updateBusinessCredit(double primeRate)` | Prices this month's business credit and hands every sector its interest bill BEFORE the statements run, so the figures they bank are net of it. |
| 652 | 32 | `public void updateBusinessCredit(double primeRate, double insuredMortgageRate)` | ...and the insured mortgage's rate beside prime (0.7.11, Bank.insuredMortgageRate()): what a landlord's new mortgage is written at and a term that ends renews at. |
| 693 | 10 | `public void refreshCreditAssets()` | Tells the credit manager what each sector is worth right now - its own sheet plus what it holds abroad, which a lender that ignored would call a rich sector broke. |
| 705 | 20 | `public void pushBalanceSheetInputs()` | Book values and outstanding debt, refreshed onto each set of books. |

### the statements' splits (0.7.74, R1 and R2) (lines 726-776)

| line | len | member | says |
|---:|---:|---|---|
| 748 | 1 | `public double[] getInterestByKind(String key)` | One sector's interest bill this month by kind (BusinessDebtManager.DEBT_KINDS), or null before one was handed. |
| 751 | 1 | `public double[][] getDebtByKind(String key)` | One sector's debt by kind as its sheet was last pushed - {owed, within a year, within five} and since 0.7.75 its rate and when the last of it falls due (BusinessDebtManager.OWED to RUNS_TO) - or null before one was. |
| 769 | 1 | `public double[][] getDebtMovedAtSheet(String key)` | What had moved one sector's debt so far when its sheet was last pushed (R7): {borrowed, repaid, written off} by kind, or null before one was. |
| 772 | 1 | `public double[] getValuedAt(String key)` | ...and its land and buildings as the sheet priced them (R6): {land, sq ft; its price a sq ft; the materials in its buildings, units; their price a unit}, or null. |
| 775 | 1 | `public double[] getValuedByClass(String key)` | ...and, where the city has foothill or steep ground (ET3), the same land by class as the sheet valued it: {flat, foothill, slope square feet; flat, foothill, slope price a square foot}, or null. |

### property tax (lines 777-827)

| line | len | member | says |
|---:|---:|---|---|
| 792 | 5 | `public double getAssessedValue(Sector s)` | Land at today's price plus FINISHED buildings at replacement cost. |
| 799 | 3 | `public double getAssessedValue(BuildingType category)` | The same for a city-owned category, which the city does not tax but a screen may want to show. |
| 804 | 3 | `public double getPropertyTaxFor(Sector s)` | One month's property tax on a sector, at its own rate. |
| 813 | 10 | `public void chargePropertyTax()` | Hands each sector its property tax bill for the month. |
| 824 | 1 | `public double getTotalPropertyTax()` |  |
| 825 | 1 | `public void setTotalPropertyTax(double value)` |  |
| 826 | 1 | `public double getPropertyTaxCharged(String key)` |  |

### maintenance (lines 828-928)

| line | len | member | says |
|---:|---:|---|---|
| 892 | 3 | `public void setStreetUtilisation(java.util.function.DoubleSupplier utilisation)` | Where the streets' utilisation the road repairs are charged at is read from (FR7 2.2): Game's InfrastructureManager, live. |
| 897 | 4 | `private double streetUtilisation()` | The streets' utilisation as the repairs read it: 0 when it is not a number. |
| 903 | 7 | `private double repaired(java.util.function.ToDoubleFunction<java.util.function.ToDoubleFunction<BuildingsTemplate>> total, java...` | A month's repairs of `cost` over the buildings `total` sums: the house rate on those without a share of their own, and each with one at its own over twelve. |
| 912 | 6 | `public double maintenanceBillFor(Sector s, double materialPrice)` | The month's repair bill for one sector, in money - materials priced in, and the builders' sales tax. |
| 920 | 8 | `public double maintenanceBillFor(BuildingType category, double materialPrice)` | The same for a city-owned category - since 0.7.103 each road kind at its own share. |

### the builders' sales tax (lines 929-1002)

| line | len | member | says |
|---:|---:|---|---|
| 932 | 3 | `public double buildersSalesRate()` | What the builders charge their sales tax at. |
| 942 | 3 | `public double withBuildersTax(double beforeTax)` | A price before the builders' sales tax, with it passed on (0.7.19): the builders remit their rate on what they bill, so what they bill for a price p that is theirs to keep is p / (1 - rate). |
| 963 | 5 | `public boolean claimsTaxOnBuildings(String owner, BuildingsTemplate t)` | WHETHER AN OWNER CLAIMS THE TAX ON ITS BUILDINGS BACK (0.7.19). |
| 970 | 4 | `private static boolean makesTaxableSupplies(Sector s)` | A sector with a good the sales tax touches. |
| 983 | 7 | `public double ownersBuildCost(BuildingsTemplate t, String owner, double beforeTax)` | What a building's price before the builders' tax costs this owner in the end: the price, and whatever of the tax on it the owner does not get back - none of it for a business that claims it, none for a landlord's purp... |
| 999 | 3 | `public double ownersRepairCost(BuildingsTemplate t, String owner, double beforeTax)` | ...and a REPAIR bill's (0.7.19, revised): the bill itself when the owner claims the tax on it back as an input, the bill with the tax when it cannot. |

### THE REBATES ON A NEW HOME, AND THE CITY'S (0.7.19, revised) (lines 1003-1218)

| line | len | member | says |
|---:|---:|---|---|
| 1074 | 1 | `public void setFoundingToToday(java.util.function.DoubleSupplier s)` |  |
| 1077 | 3 | `public boolean isLandlordsHome(String owner, BuildingsTemplate t)` | Whether this building is one of the landlords' homes. |
| 1082 | 3 | `public static boolean isPurposeBuiltRental(BuildingsTemplate t)` | Whether a home of this template is purpose-built rental housing. |
| 1092 | 13 | `public double rentalPropertyRebate(BuildingsTemplate t, double taxPerBuilding)` | The new residential rental property rebate on one building of this template, on this much tax paid on it, in today's money: A x ($450,000 - B) / $100,000 a unit (THE REBATES ON A NEW HOME). |
| 1117 | 7 | `public double taxRecoveredShare(String owner, BuildingsTemplate t, double taxPerBuilding)` | The share of the builders' tax on a new building that its owner gets back (0.7.19, revised): all of it for a business that claims it as a credit (claimsTaxOnBuildings()) and for a landlord's purpose-built rental; the ... |
| 1126 | 4 | `public double maintenanceMaterialsTotal()` | The material UNITS the whole city's repairs consume this month - each road kind at its own share (0.7.103). |
| 1132 | 4 | `public double maintenancePointsTotal()` | The builders' time the whole city's repairs consume this month - each road kind at its own share (0.7.103). |
| 1146 | 31 | `public void chargeMaintenance(double materialPrice)` | Hands every owner its repair bill for the month. |
| 1179 | 1 | `public double getMaintenanceBillTotal()` | Every owner's repair bill added up - what the builders are owed. |
| 1181 | 1 | `public double getCityMaintenanceBill()` | The share of it the treasury pays, because the city owns those buildings. |
| 1183 | 1 | `public double getBankMaintenanceBill()` | The share the bank pays, for its branches. |
| 1199 | 5 | `public double bankOperatingCost(long branches)` | ...AND WHAT THESE BRANCHES COST TO RUN (0.7.19): the template's upkeep, a branch's rent, systems and supplies, which was charged to nobody until then. |
| 1206 | 6 | `public double bankOperatingCostPerLaterBranch()` | ...what one branch past the charter carries of it: the template's upkeep. |
| 1213 | 1 | `public double getMaintenanceMaterialsTotal()` | Material UNITS, not money - the yard has to find these. |
| 1215 | 1 | `public double getMaintenancePointsTotal()` | Builders' time, which comes straight off what the city can put up. |
| 1217 | 1 | `public double getMaintenanceCharge(String key)` | One sector's bill, for the screens. |

### the strike (lines 1219-1378)

| line | len | member | says |
|---:|---:|---|---|
| 1227 | 8 | `public void strikeSectors()` | Strikes every sector's statement, settles the VAT from the same figures, and banks the month. |
| 1249 | 16 | `void settleSupplierCredit()` | THE SUPPLIERS' CREDIT, SETTLED AT THE STRIKE (0.7.44; SupplierCredit). |
| 1275 | 54 | `public double settleSalesTax()` | The month's sales tax, STRUCK FROM THE TRADES. |
| 1331 | 3 | `private double exemptSales(Sector s)` | The exempt part of a sector's local sales: what it sold of goods the tax never touches. |
| 1336 | 9 | `private static double exemptOf(Sector s, double localSales)` | ...of these local sales: all of them for a sector making only exempt goods, none otherwise. |
| 1355 | 18 | `private double salesTaxSoFar(Sector s, Sector.Ledger p)` | The sales tax a sector's month owes SO FAR, off its ledger as it stands (0.7.12 round 6): settleSalesTax()'s arithmetic for one sector - its taxable sales at its own rate, less the credit on what it bought at each sup... |
| 1375 | 1 | `public String getDeepestRefundSector()` | Whoever the city owes this month, or null. |
| 1376 | 1 | `public double getSectorSalesTax(String key)` |  |
| 1377 | 1 | `public double getSectorSalesTax(Sector s)` |  |

### INSOLVENCY, at the bottom of the investment step (lines 1379-1514)

| line | len | member | says |
|---:|---:|---|---|
| 1401 | 36 | `public double settleInsolvency()` | End of the month: work out who is beyond saving and write their debt down to what their assets support - since 0.7.8 each sector's slice of defaulted firms, since 0.7.12 round 4 the part that could not pay its month w... |
| 1438 | 1 | `public double getOverdraftForgiven()` |  |
| 1439 | 1 | `public double getOverdraftForgivenThisMonth(String key)` |  |
| 1440 | 1 | `public double getOverdraftForgivenTotal(String key)` |  |
| 1456 | 38 | `public void settleBusinessCredit(int month)` | Repay what matured - and since 0.7.11 the principal a mortgage's payment took - then borrow if that left the sector short. |
| 1502 | 12 | `double monthObligations(Sector s, double matured)` | What the month asked a sector to pay (round 4): the costs on this month's statement - the goods it paid for this month, its payroll, utilities and repairs - its interest and its taxes, and the principal that fell due,... |

### WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS (lines 1515-1531)

| line | len | member | says |
|---:|---:|---|---|
| 1521 | 1 | `public void clearDepositInterest()` |  |
| 1523 | 4 | `public void recordDepositInterest(String sector, double amount)` |  |
| 1528 | 3 | `public double getDepositInterestPaid(String sector)` |  |

### THE OWNERS AND THE MONEY ABROAD (lines 1532-1685)

| line | len | member | says |
|---:|---:|---|---|
| 1537 | 1 | `public void setOutwardInvestment(OutwardInvestment outward)` |  |
| 1538 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |
| 1582 | 45 | `public double purchaseBudget(Sector s, double dividend, int month)` | WHAT A SECTOR CAN PAY FOR AS THE MARKETS CLEAR (0.7.12 round 6) - see Sector, BUY ONLY WHAT IT CAN PAY FOR. |
| 1629 | 3 | `private static double supplierCreditLimit(Sector s)` | What a buyer's suppliers will wait for this clearing: the grocers' (Retail.supplierCreditLimit()); nothing for anybody else. |
| 1634 | 3 | `public double getForeignAssets(String sector)` | What one sector holds abroad, in the city's money at the rate it was last valued at. |
| 1640 | 1 | `public void setBondMarket(BondMarket market)` |  |
| 1641 | 1 | `public BondMarket getBondMarket()` |  |
| 1648 | 4 | `public double getBondAssets(String sector)` | What one sector holds of the other sectors' bonds, at face - an asset beside its cash and what it holds abroad (0.7.12) - and, since CD2 (slice 4), of the city's own paper, at face: the books carry both as bonds it ho... |
| 1654 | 1 | `public void setEquity(Equity equity)` |  |
| 1655 | 1 | `public Equity getEquity()` |  |
| 1661 | 1 | `public void clearEquityFlows()` |  |
| 1665 | 4 | `public void recordStolen(String sector, double amount)` |  |
| 1669 | 1 | `public double getStolen(String sector)` |  |
| 1670 | 4 | `public void recordSharesBoughtBack(String sector, double amount)` |  |
| 1674 | 1 | `public double getSharesBoughtBack(String sector)` |  |
| 1675 | 4 | `public void recordEquityRaised(String sector, double amount)` |  |
| 1679 | 4 | `public void recordDividendPaid(String sector, double amount)` |  |
| 1683 | 1 | `public double getEquityRaised(String sector)` |  |
| 1684 | 1 | `public double getDividendsPaid(String sector)` |  |

### THE MONTH, IN THE MIDDLE AND AT THE BOTTOM (lines 1686-1711)

| line | len | member | says |
|---:|---:|---|---|
| 1691 | 3 | `public void updateEcon()` | The economy's inputs for the month, set from the simulation. |
| 1701 | 5 | `public void finalEconUpdate(Game game)` | The bottom of the month: every market clears, every maker produces. |
| 1708 | 3 | `public void refreshEconPrices()` | What the load path needs of the bottom of the month, and nothing else: the draw the expense lines need. |

### THE CITY'S BOOKS (lines 1712-1835)

| line | len | member | says |
|---:|---:|---|---|
| 1730 | 1 | `public void setBankTax(double amount)` |  |
| 1731 | 1 | `public double getBankTax()` |  |
| 1734 | 1 | `public double getProfitTax(Sector s)` | The profit tax one sector paid this month, as its statement carries it. |
| 1737 | 5 | `public double getBusinessTax()` | Every sector's profit tax but the food industry's, plus the bank's - the "business tax" line. |
| 1744 | 1 | `public double getIndustrialTax()` | The food industry's, on its own line, as the screens have always shown it. |
| 1745 | 1 | `public double getHeavyIndustryTax()` |  |
| 1746 | 1 | `public double getConstructionTax()` |  |
| 1747 | 1 | `public double getSalesTax()` |  |
| 1748 | 1 | `public double getWageTax()` |  |
| 1749 | 1 | `public double getUtilityIncome()` |  |
| 1752 | 3 | `public double wageTaxOnPayroll(double[] payrollPerType)` | The banded wage tax on one sector's payroll, asked of the policy rather than multiplied out in the UI. |
| 1756 | 1 | `public double getTaxIncome()` |  |
| 1767 | 1 | `public double getTaxIncomeNow()` | The same take at today's rates, WRITING NOTHING (0.7.31, the Government spec's B8): what Game.getIncome() - the header's EARNED - reads every time the header draws. |
| 1770 | 37 | `private double taxTake(boolean strike)` | The tax take, summed; struck into the month's fields only when `strike`. |
| 1815 | 4 | `public double getFuelDuty()` | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7): what the drivers paid the treasury a litre on their petrol this month, struck at the pump (sectors.Retail.sellFuel(), at 6d) and read from the forecourts' month - saved a... |
| 1830 | 5 | `public double getPortDues()` | THE PORT DUES (FR3, docs/queue/FR3.md 1 and 3; D9): what Freight's Ports division paid the city this month on the tonnes that crossed its berths (sectors.PortDivision.getDuesPaid()), struck in Freight.haul() before th... |

### WHAT THE POLICY TAB ASKS (0.7.36) (lines 1836-1902)

| line | len | member | says |
|---:|---:|---|---|
| 1850 | 3 | `public double profitTaxUnder(Sector s, TaxPolicy p)` | One sector's profit tax under a policy (M2): its last struck pre-tax income at that policy's rate, nothing on a loss - Sector.strike()'s rule. |
| 1863 | 6 | `public double salesTaxUnder(Sector s, TaxPolicy p)` | One sector's sales tax under a policy (M2): its net remittance scaled by the ratio of the two rates, or its taxable sales at the rate when it was charged nothing. |
| 1871 | 5 | `public double propertyTaxUnder(Sector s, TaxPolicy p)` | One sector's property tax under a policy (M2): its land at today's price on that policy's roll share - its zone's ground at the zone's since 0.7.107 - and its finished buildings, at that policy's monthly rate - getPro... |
| 1878 | 3 | `public double wageTaxUnder(WageBand band, TaxPolicy p)` | One wage band's wage tax under a policy (M2): every job type in it, staffed, at that policy's rate for the band - TaxPolicy.wageTaxPerTier()'s rule. |
| 1883 | 7 | `public double payrollIn(WageBand band)` | The staffed payroll one wage band carries this month: what its wage tax is struck on. |
| 1892 | 1 | `public double getWageBill()` | The wage bill the pension contribution and both premiums are struck on (M3): the month's total wage, as getTaxIncome() reads it. |
| 1895 | 1 | `public double premiumAt(double rate)` | A premium at this rate off that bill (M3): the EI and health premiums' own rule. |
| 1898 | 1 | `public double contributionsAt(double rate)` | Pension contributions at this rate off that bill (M3): SocialSecurity's own rule. |
| 1901 | 1 | `public double pensionsPaidUnder(TaxPolicy p)` | What the pensions would come to under a policy (M4): today's seniors at its pension. |

### EI and the student grant (2026-09-11) (lines 1903-2001)

| line | len | member | says |
|---:|---:|---|---|
| 1913 | 1 | `public double getHealthPremiums()` | What the health premium raised this month, employee side, off the whole wage bill. |
| 1934 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 1939 | 1 | `public double getCentralBankRemittance()` |  |
| 1940 | 1 | `public double getCentralBankInterest()` |  |
| 1945 | 1 | `public void setFundTransfer(double transfer)` |  |
| 1946 | 1 | `public double getFundTransfer()` |  |
| 1951 | 1 | `public void setFoodAssistance(double paid)` |  |
| 1952 | 1 | `public double getFoodAssistance()` |  |
| 1965 | 1 | `public void setCityRepairsPaid(double paid)` |  |
| 1966 | 1 | `public void setArrearsPaidDown(double paid)` |  |
| 1967 | 4 | `public void setReserveCrude(double bought, double sold)` |  |
| 1971 | 1 | `public double getCityRepairsPaid()` |  |
| 1972 | 1 | `public double getArrearsPaidDown()` |  |
| 1978 | 1 | `public void setPublicIpCharges(double charges)` | Sets the month's public works' licence (R2c-P); a figure that is not finite or is below 0 is 0. |
| 1979 | 1 | `public double getPublicIpCharges()` |  |
| 1982 | 1 | `public void setStudentLoanInterest(double interest)` | Sets the month's student-loan interest, the treasury's. |
| 1985 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month. |
| 1988 | 4 | `public void setOutsidePayments(double eiBenefits, double studentGrants)` | The month's EI bill and grant bill as the treasury paid them: set by Game where it pays them, at the top of the month (payEiBenefits() since 0.7.3, payStudentGrants() since 0.7.1), and on the load path. |
| 1993 | 1 | `public double getEiPremiums()` |  |
| 1994 | 1 | `public double getEiBenefits()` |  |
| 1995 | 1 | `public double getStudentGrants()` |  |
| 1998 | 3 | `public double getEiCoverage()` | What the premiums cover of the EI bill. |

### the services (lines 2002-2044)

| line | len | member | says |
|---:|---:|---|---|
| 2005 | 4 | `public void setHealthcare(double grossCost, double fees)` |  |
| 2009 | 1 | `public double getHealthcareBill()` |  |
| 2010 | 1 | `public double getHealthcareFees()` |  |
| 2011 | 1 | `public double getHealthcareNet()` |  |
| 2014 | 4 | `public void setEducation(double grossCost, double fees)` |  |
| 2018 | 1 | `public double getEducationBill()` |  |
| 2019 | 1 | `public double getEducationFees()` |  |
| 2020 | 1 | `public double getEducationNet()` |  |
| 2027 | 1 | `public void setSafety(double grossCost)` |  |
| 2028 | 1 | `public double getSafetyBill()` |  |
| 2037 | 4 | `public void setResearch(double payroll, double costs)` |  |
| 2041 | 1 | `public double getResearchPayroll()` |  |
| 2042 | 1 | `public double getResearchCosts()` |  |
| 2043 | 1 | `public double getResearchBill()` |  |

### THE TRANSIT BOOKS (2026-09-16) (lines 2045-2111)

| line | len | member | says |
|---:|---:|---|---|
| 2080 | 4 | `public void setTransit(double grossCost, double fares)` |  |
| 2084 | 1 | `public double getTransitBill()` |  |
| 2090 | 3 | `public void setTransitUpkeep(double upkeep)` | Sets the month's transit upkeep, clamped to [0, the bill] (HL1). |
| 2095 | 1 | `public double getTransitUpkeep()` | The transit bill's upkeep (HL1), never more than the bill. |
| 2098 | 1 | `public double getTransitWages()` | The transit bill's wages (HL1): what is left of the bill once the upkeep is taken out. |
| 2099 | 1 | `public double getTransitFares()` |  |
| 2102 | 1 | `public double getTransitNet()` | Negative when the fare more than covers the wages, which a player can arrange. |
| 2105 | 1 | `public double transitNetAt(double fares)` | ...with another month of fares against this month's bill (0.7.38): the fare dial card's net, its fares InfrastructureManager.faresAt(). |
| 2109 | 1 | `public void setSubsidiesPaid(double v)` |  |
| 2110 | 1 | `public double getSubsidiesPaid()` |  |

### pensions (lines 2112-2154)

| line | len | member | says |
|---:|---:|---|---|
| 2115 | 1 | `public void setSeniors(double seniors)` |  |
| 2116 | 1 | `public double getSeniors()` |  |
| 2117 | 1 | `public double getContributions()` |  |
| 2118 | 1 | `public double getPensionsPaid()` |  |
| 2119 | 1 | `public double getPensionShortfall()` |  |
| 2120 | 5 | `public double getPensionCoverage()` |  |
| 2127 | 8 | `public double getExpenses()` | What the city pays out this month. |
| 2142 | 1 | `public double getForeignInterest()` | The month's coupons abroad, every money's, in D$ (CD9, CD7-E): in getExpenses(), so EARNED carries the interest the budget does - the foreign coupon is in the budget's interest and was not in EARNED, which left getEar... |
| 2143 | 1 | `public void setForeignInterest(double value)` |  |
| 2146 | 1 | `public double getInterestAccrued()` | The interest alone, which is the only part of getExpenses() that is CARRIED (CD9: the domestic coupon's, not the foreign one in getForeignInterest()). |
| 2147 | 1 | `public void setInterest(double value)` |  |
| 2148 | 1 | `public void updateInterestExpense(double interest)` |  |
| 2150 | 1 | `public double getTotalIncome()` |  |
| 2152 | 1 | `public void setUtilityIncome(double income)` |  |
| 2153 | 1 | `public void setDebt(double debt)` |  |

### THE NATIONAL ACCOUNTS (lines 2155-2539)

| line | len | member | says |
|---:|---:|---|---|
| 2170 | 183 | `public void updateNationalAccounts(double constructionWorkDone, double governmentServices, double interest, double capitalSpend...` | Measures the month's output and the government's books, from the statements the sectors have just struck. |
| 2361 | 7 | `static double heldPrice(GoodsMarket m)` | What one unit of a held good is counted at (0.7.58): what one costs to bring in this month (GoodsMarket.landedPrice()) - the local price while somebody in the city has it on offer, the import price while nobody does -... |
| 2377 | 6 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid, doub...` | The month's strike, with the debt's two lines (CD3): the discount the city's paper amortised (DebtManager.getAmortisedBooked()), which is the non-cash part of the budget's interest, and the gain on paper retired in th... |
| 2391 | 7 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid, doub...` | ...and the inflation uplift on linked bonds (CD5): the month's indexation booked (DebtManager.getUpliftBooked()), an expense that is not cash, and the part of it paid in cash in a linker's last month (getUpliftPaid()). |
| 2409 | 27 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid)` | Repopulates the government's revenue and expenditure block after a load, and nothing else - running the whole measure would be running a month of the economy with the calendar standing still. |
| 2446 | 4 | `private void setMortgageInsuranceLines()` | THE MORTGAGE INSURANCE'S TWO BUDGET LINES (0.7.11), off the lender's month: the premiums on the mortgages written, which the treasury took as each was written, and the claims - what the month's write-downs took off in... |
| 2451 | 1 | `public double getLastFoodVolume()` |  |
| 2463 | 30 | `public void restoreNationalAccounts(double[] a)` | A NEW SLOT RATHER THAN A CHANGED ONE, because slot 11 changed SCALE. |
| 2494 | 25 | `public double[] getNationalAccountsState()` |  |
| 2520 | 1 | `double[] governmentMonthToSave()` |  |
| 2521 | 1 | `void restoreGovernmentMonth(double[] m)` |  |
| 2523 | 1 | `public double getMonthGdp()` |  |
| 2524 | 1 | `public double getYearGdp()` |  |
| 2525 | 1 | `public double getGDP()` |  |
| 2527 | 1 | `public double getTaxRate()` | The income rate - the three income taxes together, TaxPolicy.getIncomeTaxRate(): the profit rate once they have parted (0.7.4). |
| 2529 | 10 | `public void setPreviousGdp(HistorySave historySave)` |  |

### CONVENIENCES - the prices the screens and the harnesses ask for by name (lines 2540-2556)

| line | len | member | says |
|---:|---:|---|---|
| 2545 | 1 | `public double getFoodLocalPrice()` | What one person-month of food costs the shops at today's market prices. |
| 2546 | 1 | `public double getIronLocalPrice()` |  |
| 2549 | 7 | `public long getFoodUnitsHeld()` | Every warehouse and shelf of food in the city, in KILOGRAMS across the thirteen. |

### SAVE AND RESTORE (lines 2557-2589)

| line | len | member | says |
|---:|---:|---|---|
| 2561 | 1 | `public List<SectorState> getSectorStates()` |  |
| 2562 | 1 | `public void restoreSectorStates(List<SectorState> s)` |  |
| 2565 | 13 | `public void restoreSectorBills(List<SectorState> saved)` | The month's bills back over the rebuild's re-derivation, and the total they add to. |
| 2578 | 1 | `public List<Markets.State> getMarketStates()` |  |
| 2579 | 1 | `public void restoreMarketStates(List<Markets.State> s)` |  |
| 2581 | 1 | `public SalesTaxLedger.State getSalesTaxState()` |  |
| 2584 | 5 | `public boolean restoreSalesTaxState(SalesTaxLedger.State state)` | The month's VAT back, AND the total that came out of it - one fact. |

### PRINTERS, RESET, THE REFORM (lines 2590-2747)

| line | len | member | says |
|---:|---:|---|---|
| 2594 | 3 | `public void printWageTaxInfo()` |  |
| 2598 | 35 | `public void printCityStats()` |  |
| 2635 | 4 | `static String debtToGdpLine(double debt, double yearGdp)` | The report's debt-to-GDP line: a dash, not "NaN%", while there is no GDP to divide by (months 1 and 2). |
| 2640 | 17 | `public void resetEconomyManager()` |  |
| 2660 | 4 | `static { ... }` |  |
| 2672 | 70 | `public void redenominate(double scale)` | Every figure the economy manager holds, and every sector and market under it, in the new unit. |
| 2743 | 4 | `private static void scaleArray(double[] values, double scale)` |  |

