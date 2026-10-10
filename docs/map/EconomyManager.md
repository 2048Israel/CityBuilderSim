# EconomyManager.java - 2,311 lines · 238 methods · 8 constants · model

`ham/citybuildersim/EconomyManager.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (61), [BuildingsTemplate](BuildingsTemplate.md) (19), [Good](Good.md) (14), [BuildingType](BuildingType.md) (12), [TaxPolicy](TaxPolicy.md) (9), [NationalAccounts](NationalAccounts.md) (9), [Markets](Markets.md) (5), [SalesTaxLedger](SalesTaxLedger.md) (5), [BuildingManager](BuildingManager.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Traffic](Traffic.md) (4), [SectorState](SectorState.md) (4), [Sectors](Sectors.md) (3), [JobType](JobType.md) (3), [RealEstate](RealEstate.md) (3), [Statement](Statement.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [Retail](Retail.md) (3), [BondMarket](BondMarket.md) (3), [Equity](Equity.md) (3), [WageBand](WageBand.md) (3), [GoodsMarket](GoodsMarket.md) (2), [FamilyModel](FamilyModel.md) (2), [SupplierCredit](SupplierCredit.md) (2), [GameLog](GameLog.md) (2), [SocialSecurity](SocialSecurity.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [BalanceSheet](BalanceSheet.md) (1), [Game](Game.md) (1), [LuxuryRetail](LuxuryRetail.md) (1)... and 1 more

**Used by (47):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildAdvice](BuildAdvice.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HousingCheck](HousingCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Materials](Materials.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [OutwardInvestment](OutwardInvestment.md), [PeopleScreen](PeopleScreen.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorStatementCheck](SectorStatementCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 31 | THE CITY'S PARTS |
| 62 | WHAT THE CITY TELLS THE ECONOMY EACH MONTH |
| 146 | · utilities |
| 235 | · the world |
| 253 | · the land |
| 275 | HOUSING - what the landlords need from the family model and the land office |
| 395 | CASH BY NAME - the seam almost everything routes through |
| 409 | THE MONTH, AT THE TOP |
| 497 | · the statements' splits (0.7.74, R1 and R2) |
| 544 | · property tax |
| 594 | · maintenance |
| 668 | · the builders' sales tax |
| 742 | THE REBATES ON A NEW HOME, AND THE CITY'S (0.7.19, revised) |
| 958 | · the strike |
| 1118 | INSOLVENCY, at the bottom of the investment step |
| 1248 | WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS |
| 1265 | THE OWNERS AND THE MONEY ABROAD |
| 1402 | THE MONTH, IN THE MIDDLE AND AT THE BOTTOM |
| 1428 | THE CITY'S BOOKS |
| 1530 | · WHAT THE POLICY TAB ASKS (0.7.36) |
| 1597 | · EI and the student grant (2026-09-11) |
| 1689 | · the services |
| 1717 | THE TRANSIT BOOKS (2026-09-16) |
| 1770 | · pensions |
| 1802 | THE NATIONAL ACCOUNTS |
| 2120 | CONVENIENCES - the prices the screens and the harnesses ask for by name |
| 2137 | SAVE AND RESTORE |
| 2170 | PRINTERS, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 605 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, ...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 619 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 801 | `EconomyManager.PBRH_MIN_UNITS` | `4` | A building with this many dwellings or more is purpose-built rental housing (CRA: "at least 4 residential units each with a private kitchen, a private bathroom, and a private living area"). |
| 803 | `EconomyManager.NRRP_SHARE` | `.36` | The new residential rental property rebate's share of the tax on a unit: 36% (Excise Tax Act section 256.2(3)(a); CRA RC4231). |
| 805 | `EconomyManager.NRRP_CAP` | `6.3` | ...and its most a unit, in founding thousands: $6,300. |
| 807 | `EconomyManager.NRRP_FULL_BELOW` | `350` | ...in full for a unit worth up to this, in founding thousands: $350,000. |
| 809 | `EconomyManager.NRRP_NONE_FROM` | `450` | ...and none for a unit worth this or more: $450,000. |
| 2230 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 243 | `private double exchangeRate` | Every world price in the game is quoted by the world in ITS money and converted at this rate - times the world's own price level, which is how world inflation rides in. |
| 260 | `private double landPricePerSqFt` | What the city currently charges for a square foot, so assessed values track the price the player sets. |
| 279 | `private double householdCount` |  |
| 280 | `private double marginalHousingCost` |  |
| 281 | `private double occupiedHomes` |  |
| 282 | `private double studioSeekers, familySeekers, studioSeekerHeads, familySeekerHeads` |  |
| 512 | `private final Map<String, double[]> interestByKind` |  |
| 513 | `private final Map<String, double[][]> debtByKind` |  |
| 516 | `private int creditMonth` | The month of the last credit settle, which the bonds' maturities are read against (settleBusinessCredit()). |
| 535 | `private final Map<String, double[][]> debtMovedAtSheet` | ...AND, SINCE 0.7.75, WHAT HAD MOVED IT AND WHAT THE SHEET WAS PRICED AT (the sector statements' R7 and R6), read at the same push: the running totals of its principal borrowed, repaid and written off by kind (Busines... |
| 536 | `private final Map<String, double[]> valuedAt` |  |
| 546 | `private double totalPropertyTax` |  |
| 547 | `private final Map<String, Double> propertyTaxBySector` |  |
| 612 | `private double maintenanceBillTotal` |  |
| 613 | `private double maintenanceMaterialsTotal` |  |
| 614 | `private double maintenancePointsTotal` |  |
| 615 | `private double cityMaintenanceBill` |  |
| 616 | `private double bankMaintenanceBill` |  |
| 617 | `private final Map<String, Double> maintenanceBySector` |  |
| 812 | `private java.util.function.DoubleSupplier foundingToToday` | Today's money for a founding dollar: the month's price index over the unit. |
| 1122 | `private double overdraftForgivenThisMonth` |  |
| 1123 | `private final Map<String, Double> overdraftForgivenBySector` |  |
| 1124 | `private final Map<String, Double> overdraftForgivenThisMonthBySector` |  |
| 1252 | `private final Map<String, Double> depositInterestPaid` |  |
| 1269 | `private OutwardInvestment outward` |  |
| 1360 | `private BondMarket bondMarket` | The bond market (0.7.12), wired by Game: its maturities are paid at the credit settle, and the other sectors' bonds a company holds are among its assets. |
| 1369 | `private Equity equity` |  |
| 1373 | `private final Map<String, Double> equityRaised` |  |
| 1374 | `private final Map<String, Double> dividendsPaid` |  |
| 1375 | `private final Map<String, Double> sharesBoughtBack` |  |
| 1380 | `private final Map<String, Double> stolen` | Taken from each sector's till by thieves this month. |
| 1432 | `private double salesTax` |  |
| 1433 | `private double totalWageTax` |  |
| 1434 | `private double totalBankTax` |  |
| 1435 | `private double totalContributions` |  |
| 1436 | `private double interest` |  |
| 1437 | `private double utilityIncome` |  |
| 1438 | `private double debt` |  |
| 1439 | `private double GDP` |  |
| 1440 | `private double yearGDP` |  |
| 1599 | `private double totalEiPremiums` |  |
| 1600 | `private double eiBenefits` |  |
| 1601 | `private double studentGrants` |  |
| 1604 | `private double totalHealthPremiums` | The month's health premium off every wage, struck in getTaxIncome() beside the EI premium (2026-09-19). |
| 1617 | `private double studentLoanInterest` | The interest the graduates paid the treasury on their student loans this month (2026-09-21): a revenue line beside the premiums, set by Game off the household ledger the month it is struck. |
| 1626 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO BUDGET LINES (0.7.0), set by Game where the central bank settles with the treasury at the top of the month: the remittance in, the interest on the advances out. |
| 1637 | `private double fundTransfer` | THE TRANSFER FROM THE CITY'S FUND (0.7.14), set by Game where the treasury settles, for the central bank's lines' reason: Game moves the cash there. |
| 1643 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43), set by Game where the treasury pays it, after the month's sale - for the central bank's lines' reason: Game moves the cash there. |
| 1657 | `private double cityRepairsPaid, arrearsPaidDown, reserveCrudeBought, reserveCrudeSold` | THE FOUR LINES 0.7.102 TOOK INSIDE THE BUDGET'S TOTALS (A15), set by Game where the treasury pays or takes them, for the central bank's lines' reason: the repairs to the city's own buildings as paid (chargeBuildingMai... |
| 1691 | `private double healthcareBill, healthcareFees` |  |
| 1700 | `private double educationBill, educationFees` |  |
| 1713 | `private double safetyBill` | What the police and the prisons cost this month: payroll and upkeep, no fees - nobody pays to be policed or jailed. |
| 1751 | `private double transitBill, transitFares` |  |
| 1766 | `private double subsidiesPaid` | What the city paid this month to hold protected sectors at break-even. |
| 1772 | `private double seniors` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 2283 | **type** `public class EconomyManager` | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. |

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

### utilities (lines 146-234)

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
| 212 | 8 | `public void setElectricityConsumption()` | Every sector's draw, off its own buildings. |
| 222 | 5 | `public double getSectorElectricityCharges()` | The month's power bills, as the sectors' statements booked them. |
| 229 | 5 | `public double getSectorWaterCharges()` | The month's water bills, likewise. |

### the world (lines 235-252)

| line | len | member | says |
|---:|---:|---|---|
| 245 | 5 | `public void setExchangeRate(double rate)` |  |
| 251 | 1 | `public double getExchangeRate()` |  |

### the land (lines 253-274)

| line | len | member | says |
|---:|---:|---|---|
| 262 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 263 | 1 | `public double getLandPricePerSqFt()` |  |
| 266 | 3 | `public double landValueOf(Sector s)` | What a sector's land is worth at the city's current price. |
| 271 | 3 | `public double landValueOf(BuildingType category)` | What a city-owned category's land is worth. |

### HOUSING - what the landlords need from the family model and the land office (lines 275-394)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 1 | `public void setHouseholdCount(double count)` |  |
| 285 | 1 | `public void setMarginalHousingCost(double perCap)` |  |
| 286 | 1 | `public double getMarginalHousingCost()` |  |
| 287 | 1 | `public void setOccupiedHomes(double occupied)` |  |
| 288 | 6 | `public void setHousingSeekers(double studio, double family, double studioHeads, double familyHeads)` |  |
| 294 | 1 | `public double getStudioSeekers()` |  |
| 295 | 1 | `public double getFamilySeekers()` |  |
| 303 | 21 | `public void updateHousing()` | Hands the landlords and the shops what the month looks like: how many people there are, how many doors, who is chasing them and what a new home costs. |
| 337 | 3 | `public double housingBuildHurdle(BuildingsTemplate t)` | What one of these has to earn a month before it is worth putting up. |
| 349 | 17 | `public double housingCarry(BuildingsTemplate t)` | WHAT ONE OF THESE COSTS TO HOLD A MONTH, before any interest: its maintenance, RealEstate.MAINTENANCE_PER_YEAR on the structure, and its property tax, the property rate on the structure and its plot. |
| 368 | 26 | `public void repriceHousingCosts()` | The cheapest way to house one more person, land included, per segment. |

### CASH BY NAME - the seam almost everything routes through (lines 395-408)

| line | len | member | says |
|---:|---:|---|---|
| 399 | 4 | `public double getSectorCash(String key)` |  |
| 404 | 4 | `public void setSectorCash(String key, double cash)` |  |

### THE MONTH, AT THE TOP (lines 409-496)

| line | len | member | says |
|---:|---:|---|---|
| 422 | 3 | `public void updateBusinessCredit(double primeRate)` | Prices this month's business credit and hands every sector its interest bill BEFORE the statements run, so the figures they bank are net of it. |
| 431 | 32 | `public void updateBusinessCredit(double primeRate, double insuredMortgageRate)` | ...and the insured mortgage's rate beside prime (0.7.11, Bank.insuredMortgageRate()): what a landlord's new mortgage is written at and a term that ends renews at. |
| 472 | 9 | `public void refreshCreditAssets()` | Tells the credit manager what each sector is worth right now - its own sheet plus what it holds abroad, which a lender that ignored would call a rich sector broke. |
| 483 | 13 | `public void pushBalanceSheetInputs()` | Book values and outstanding debt, refreshed onto each set of books. |

### the statements' splits (0.7.74, R1 and R2) (lines 497-543)

| line | len | member | says |
|---:|---:|---|---|
| 519 | 1 | `public double[] getInterestByKind(String key)` | One sector's interest bill this month by kind (BusinessDebtManager.DEBT_KINDS), or null before one was handed. |
| 522 | 1 | `public double[][] getDebtByKind(String key)` | One sector's debt by kind as its sheet was last pushed - {owed, within a year, within five} and since 0.7.75 its rate and when the last of it falls due (BusinessDebtManager.OWED to RUNS_TO) - or null before one was. |
| 539 | 1 | `public double[][] getDebtMovedAtSheet(String key)` | What had moved one sector's debt so far when its sheet was last pushed (R7): {borrowed, repaid, written off} by kind, or null before one was. |
| 542 | 1 | `public double[] getValuedAt(String key)` | ...and its land and buildings as the sheet priced them (R6): {land, sq ft; its price a sq ft; the materials in its buildings, units; their price a unit}, or null. |

### property tax (lines 544-593)

| line | len | member | says |
|---:|---:|---|---|
| 559 | 4 | `public double getAssessedValue(Sector s)` | Land at today's price plus FINISHED buildings at replacement cost. |
| 565 | 3 | `public double getAssessedValue(BuildingType category)` | The same for a city-owned category, which the city does not tax but a screen may want to show. |
| 570 | 3 | `public double getPropertyTaxFor(Sector s)` | One month's property tax on a sector, at its own rate. |
| 579 | 10 | `public void chargePropertyTax()` | Hands each sector its property tax bill for the month. |
| 590 | 1 | `public double getTotalPropertyTax()` |  |
| 591 | 1 | `public void setTotalPropertyTax(double value)` |  |
| 592 | 1 | `public double getPropertyTaxCharged(String key)` |  |

### maintenance (lines 594-667)

| line | len | member | says |
|---:|---:|---|---|
| 643 | 6 | `private static double repaired(java.util.function.ToDoubleFunction<java.util.function.ToDoubleFunction<BuildingsTemplate>> tota...` | A month's repairs of `cost` over the buildings `total` sums: the house rate on those without a share of their own, and each with one at its own over twelve. |
| 651 | 6 | `public double maintenanceBillFor(Sector s, double materialPrice)` | The month's repair bill for one sector, in money - materials priced in, and the builders' sales tax. |
| 659 | 8 | `public double maintenanceBillFor(BuildingType category, double materialPrice)` | The same for a city-owned category - since 0.7.103 each road kind at its own share. |

### the builders' sales tax (lines 668-741)

| line | len | member | says |
|---:|---:|---|---|
| 671 | 3 | `public double buildersSalesRate()` | What the builders charge their sales tax at. |
| 681 | 3 | `public double withBuildersTax(double beforeTax)` | A price before the builders' sales tax, with it passed on (0.7.19): the builders remit their rate on what they bill, so what they bill for a price p that is theirs to keep is p / (1 - rate). |
| 702 | 5 | `public boolean claimsTaxOnBuildings(String owner, BuildingsTemplate t)` | WHETHER AN OWNER CLAIMS THE TAX ON ITS BUILDINGS BACK (0.7.19). |
| 709 | 4 | `private static boolean makesTaxableSupplies(Sector s)` | A sector with a good the sales tax touches. |
| 722 | 7 | `public double ownersBuildCost(BuildingsTemplate t, String owner, double beforeTax)` | What a building's price before the builders' tax costs this owner in the end: the price, and whatever of the tax on it the owner does not get back - none of it for a business that claims it, none for a landlord's purp... |
| 738 | 3 | `public double ownersRepairCost(BuildingsTemplate t, String owner, double beforeTax)` | ...and a REPAIR bill's (0.7.19, revised): the bill itself when the owner claims the tax on it back as an input, the bill with the tax when it cannot. |

### THE REBATES ON A NEW HOME, AND THE CITY'S (0.7.19, revised) (lines 742-957)

| line | len | member | says |
|---:|---:|---|---|
| 813 | 1 | `public void setFoundingToToday(java.util.function.DoubleSupplier s)` |  |
| 816 | 3 | `public boolean isLandlordsHome(String owner, BuildingsTemplate t)` | Whether this building is one of the landlords' homes. |
| 821 | 3 | `public static boolean isPurposeBuiltRental(BuildingsTemplate t)` | Whether a home of this template is purpose-built rental housing. |
| 831 | 13 | `public double rentalPropertyRebate(BuildingsTemplate t, double taxPerBuilding)` | The new residential rental property rebate on one building of this template, on this much tax paid on it, in today's money: A x ($450,000 - B) / $100,000 a unit (THE REBATES ON A NEW HOME). |
| 856 | 7 | `public double taxRecoveredShare(String owner, BuildingsTemplate t, double taxPerBuilding)` | The share of the builders' tax on a new building that its owner gets back (0.7.19, revised): all of it for a business that claims it as a credit (claimsTaxOnBuildings()) and for a landlord's purpose-built rental; the ... |
| 865 | 4 | `public double maintenanceMaterialsTotal()` | The material UNITS the whole city's repairs consume this month - each road kind at its own share (0.7.103). |
| 871 | 4 | `public double maintenancePointsTotal()` | The builders' time the whole city's repairs consume this month - each road kind at its own share (0.7.103). |
| 885 | 31 | `public void chargeMaintenance(double materialPrice)` | Hands every owner its repair bill for the month. |
| 918 | 1 | `public double getMaintenanceBillTotal()` | Every owner's repair bill added up - what the builders are owed. |
| 920 | 1 | `public double getCityMaintenanceBill()` | The share of it the treasury pays, because the city owns those buildings. |
| 922 | 1 | `public double getBankMaintenanceBill()` | The share the bank pays, for its branches. |
| 938 | 5 | `public double bankOperatingCost(int branches)` | ...AND WHAT THESE BRANCHES COST TO RUN (0.7.19): the template's upkeep, a branch's rent, systems and supplies, which was charged to nobody until then. |
| 945 | 6 | `public double bankOperatingCostPerLaterBranch()` | ...what one branch past the charter carries of it: the template's upkeep. |
| 952 | 1 | `public double getMaintenanceMaterialsTotal()` | Material UNITS, not money - the yard has to find these. |
| 954 | 1 | `public double getMaintenancePointsTotal()` | Builders' time, which comes straight off what the city can put up. |
| 956 | 1 | `public double getMaintenanceCharge(String key)` | One sector's bill, for the screens. |

### the strike (lines 958-1117)

| line | len | member | says |
|---:|---:|---|---|
| 966 | 8 | `public void strikeSectors()` | Strikes every sector's statement, settles the VAT from the same figures, and banks the month. |
| 988 | 16 | `void settleSupplierCredit()` | THE SUPPLIERS' CREDIT, SETTLED AT THE STRIKE (0.7.44; SupplierCredit). |
| 1014 | 54 | `public double settleSalesTax()` | The month's sales tax, STRUCK FROM THE TRADES. |
| 1070 | 3 | `private double exemptSales(Sector s)` | The exempt part of a sector's local sales: what it sold of goods the tax never touches. |
| 1075 | 9 | `private static double exemptOf(Sector s, double localSales)` | ...of these local sales: all of them for a sector making only exempt goods, none otherwise. |
| 1094 | 18 | `private double salesTaxSoFar(Sector s, Sector.Ledger p)` | The sales tax a sector's month owes SO FAR, off its ledger as it stands (0.7.12 round 6): settleSalesTax()'s arithmetic for one sector - its taxable sales at its own rate, less the credit on what it bought at each sup... |
| 1114 | 1 | `public String getDeepestRefundSector()` | Whoever the city owes this month, or null. |
| 1115 | 1 | `public double getSectorSalesTax(String key)` |  |
| 1116 | 1 | `public double getSectorSalesTax(Sector s)` |  |

### INSOLVENCY, at the bottom of the investment step (lines 1118-1247)

| line | len | member | says |
|---:|---:|---|---|
| 1140 | 36 | `public double settleInsolvency()` | End of the month: work out who is beyond saving and write their debt down to what their assets support - since 0.7.8 each sector's slice of defaulted firms, since 0.7.12 round 4 the part that could not pay its month w... |
| 1177 | 1 | `public double getOverdraftForgiven()` |  |
| 1178 | 1 | `public double getOverdraftForgivenThisMonth(String key)` |  |
| 1179 | 1 | `public double getOverdraftForgivenTotal(String key)` |  |
| 1195 | 32 | `public void settleBusinessCredit(int month)` | Repay what matured - and since 0.7.11 the principal a mortgage's payment took - then borrow if that left the sector short. |
| 1235 | 12 | `double monthObligations(Sector s, double matured)` | What the month asked a sector to pay (round 4): the costs on this month's statement - the goods it paid for this month, its payroll, utilities and repairs - its interest and its taxes, and the principal that fell due,... |

### WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS (lines 1248-1264)

| line | len | member | says |
|---:|---:|---|---|
| 1254 | 1 | `public void clearDepositInterest()` |  |
| 1256 | 4 | `public void recordDepositInterest(String sector, double amount)` |  |
| 1261 | 3 | `public double getDepositInterestPaid(String sector)` |  |

### THE OWNERS AND THE MONEY ABROAD (lines 1265-1401)

| line | len | member | says |
|---:|---:|---|---|
| 1270 | 1 | `public void setOutwardInvestment(OutwardInvestment outward)` |  |
| 1271 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |
| 1312 | 36 | `public double purchaseBudget(Sector s, double dividend, int month)` | WHAT A SECTOR CAN PAY FOR AS THE MARKETS CLEAR (0.7.12 round 6) - see Sector, BUY ONLY WHAT IT CAN PAY FOR. |
| 1350 | 3 | `private static double supplierCreditLimit(Sector s)` | What a buyer's suppliers will wait for this clearing: the grocers' (Retail.supplierCreditLimit()); nothing for anybody else. |
| 1355 | 3 | `public double getForeignAssets(String sector)` | What one sector holds abroad, in the city's money at the rate it was last valued at. |
| 1361 | 1 | `public void setBondMarket(BondMarket market)` |  |
| 1362 | 1 | `public BondMarket getBondMarket()` |  |
| 1365 | 3 | `public double getBondAssets(String sector)` | What one sector holds of the other sectors' bonds, at face - an asset beside its cash and what it holds abroad (0.7.12). |
| 1370 | 1 | `public void setEquity(Equity equity)` |  |
| 1371 | 1 | `public Equity getEquity()` |  |
| 1377 | 1 | `public void clearEquityFlows()` |  |
| 1381 | 4 | `public void recordStolen(String sector, double amount)` |  |
| 1385 | 1 | `public double getStolen(String sector)` |  |
| 1386 | 4 | `public void recordSharesBoughtBack(String sector, double amount)` |  |
| 1390 | 1 | `public double getSharesBoughtBack(String sector)` |  |
| 1391 | 4 | `public void recordEquityRaised(String sector, double amount)` |  |
| 1395 | 4 | `public void recordDividendPaid(String sector, double amount)` |  |
| 1399 | 1 | `public double getEquityRaised(String sector)` |  |
| 1400 | 1 | `public double getDividendsPaid(String sector)` |  |

### THE MONTH, IN THE MIDDLE AND AT THE BOTTOM (lines 1402-1427)

| line | len | member | says |
|---:|---:|---|---|
| 1407 | 3 | `public void updateEcon()` | The economy's inputs for the month, set from the simulation. |
| 1417 | 5 | `public void finalEconUpdate(Game game)` | The bottom of the month: every market clears, every maker produces. |
| 1424 | 3 | `public void refreshEconPrices()` | What the load path needs of the bottom of the month, and nothing else: the draw the expense lines need. |

### THE CITY'S BOOKS (lines 1428-1529)

| line | len | member | says |
|---:|---:|---|---|
| 1442 | 1 | `public void setBankTax(double amount)` |  |
| 1443 | 1 | `public double getBankTax()` |  |
| 1446 | 1 | `public double getProfitTax(Sector s)` | The profit tax one sector paid this month, as its statement carries it. |
| 1449 | 5 | `public double getBusinessTax()` | Every sector's profit tax but the food industry's, plus the bank's - the "business tax" line. |
| 1456 | 1 | `public double getIndustrialTax()` | The food industry's, on its own line, as the screens have always shown it. |
| 1457 | 1 | `public double getHeavyIndustryTax()` |  |
| 1458 | 1 | `public double getConstructionTax()` |  |
| 1459 | 1 | `public double getSalesTax()` |  |
| 1460 | 1 | `public double getWageTax()` |  |
| 1461 | 1 | `public double getUtilityIncome()` |  |
| 1464 | 3 | `public double wageTaxOnPayroll(double[] payrollPerType)` | The banded wage tax on one sector's payroll, asked of the policy rather than multiplied out in the UI. |
| 1468 | 1 | `public double getTaxIncome()` |  |
| 1479 | 1 | `public double getTaxIncomeNow()` | The same take at today's rates, WRITING NOTHING (0.7.31, the Government spec's B8): what Game.getIncome() - the header's EARNED - reads every time the header draws. |
| 1482 | 35 | `private double taxTake(boolean strike)` | The tax take, summed; struck into the month's fields only when `strike`. |
| 1525 | 4 | `public double getFuelDuty()` | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7): what the drivers paid the treasury a litre on their petrol this month, struck at the pump (sectors.Retail.sellFuel(), at 6d) and read from the forecourts' month - saved a... |

### WHAT THE POLICY TAB ASKS (0.7.36) (lines 1530-1596)

| line | len | member | says |
|---:|---:|---|---|
| 1544 | 3 | `public double profitTaxUnder(Sector s, TaxPolicy p)` | One sector's profit tax under a policy (M2): its last struck pre-tax income at that policy's rate, nothing on a loss - Sector.strike()'s rule. |
| 1557 | 6 | `public double salesTaxUnder(Sector s, TaxPolicy p)` | One sector's sales tax under a policy (M2): its net remittance scaled by the ratio of the two rates, or its taxable sales at the rate when it was charged nothing. |
| 1565 | 5 | `public double propertyTaxUnder(Sector s, TaxPolicy p)` | One sector's property tax under a policy (M2): its land at today's price on that policy's roll share, and its finished buildings, at that policy's monthly rate - getPropertyTaxFor()'s rule. |
| 1572 | 3 | `public double wageTaxUnder(WageBand band, TaxPolicy p)` | One wage band's wage tax under a policy (M2): every job type in it, staffed, at that policy's rate for the band - TaxPolicy.wageTaxPerTier()'s rule. |
| 1577 | 7 | `public double payrollIn(WageBand band)` | The staffed payroll one wage band carries this month: what its wage tax is struck on. |
| 1586 | 1 | `public double getWageBill()` | The wage bill the pension contribution and both premiums are struck on (M3): the month's total wage, as getTaxIncome() reads it. |
| 1589 | 1 | `public double premiumAt(double rate)` | A premium at this rate off that bill (M3): the EI and health premiums' own rule. |
| 1592 | 1 | `public double contributionsAt(double rate)` | Pension contributions at this rate off that bill (M3): SocialSecurity's own rule. |
| 1595 | 1 | `public double pensionsPaidUnder(TaxPolicy p)` | What the pensions would come to under a policy (M4): today's seniors at its pension. |

### EI and the student grant (2026-09-11) (lines 1597-1688)

| line | len | member | says |
|---:|---:|---|---|
| 1607 | 1 | `public double getHealthPremiums()` | What the health premium raised this month, employee side, off the whole wage bill. |
| 1628 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 1633 | 1 | `public double getCentralBankRemittance()` |  |
| 1634 | 1 | `public double getCentralBankInterest()` |  |
| 1639 | 1 | `public void setFundTransfer(double transfer)` |  |
| 1640 | 1 | `public double getFundTransfer()` |  |
| 1645 | 1 | `public void setFoodAssistance(double paid)` |  |
| 1646 | 1 | `public double getFoodAssistance()` |  |
| 1659 | 1 | `public void setCityRepairsPaid(double paid)` |  |
| 1660 | 1 | `public void setArrearsPaidDown(double paid)` |  |
| 1661 | 4 | `public void setReserveCrude(double bought, double sold)` |  |
| 1665 | 1 | `public double getCityRepairsPaid()` |  |
| 1666 | 1 | `public double getArrearsPaidDown()` |  |
| 1669 | 1 | `public void setStudentLoanInterest(double interest)` | Sets the month's student-loan interest, the treasury's. |
| 1672 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month. |
| 1675 | 4 | `public void setOutsidePayments(double eiBenefits, double studentGrants)` | The month's EI bill and grant bill as the treasury paid them: set by Game where it pays them, at the top of the month (payEiBenefits() since 0.7.3, payStudentGrants() since 0.7.1), and on the load path. |
| 1680 | 1 | `public double getEiPremiums()` |  |
| 1681 | 1 | `public double getEiBenefits()` |  |
| 1682 | 1 | `public double getStudentGrants()` |  |
| 1685 | 3 | `public double getEiCoverage()` | What the premiums cover of the EI bill. |

### the services (lines 1689-1716)

| line | len | member | says |
|---:|---:|---|---|
| 1692 | 4 | `public void setHealthcare(double grossCost, double fees)` |  |
| 1696 | 1 | `public double getHealthcareBill()` |  |
| 1697 | 1 | `public double getHealthcareFees()` |  |
| 1698 | 1 | `public double getHealthcareNet()` |  |
| 1701 | 4 | `public void setEducation(double grossCost, double fees)` |  |
| 1705 | 1 | `public double getEducationBill()` |  |
| 1706 | 1 | `public double getEducationFees()` |  |
| 1707 | 1 | `public double getEducationNet()` |  |
| 1714 | 1 | `public void setSafety(double grossCost)` |  |
| 1715 | 1 | `public double getSafetyBill()` |  |

### THE TRANSIT BOOKS (2026-09-16) (lines 1717-1769)

| line | len | member | says |
|---:|---:|---|---|
| 1752 | 4 | `public void setTransit(double grossCost, double fares)` |  |
| 1756 | 1 | `public double getTransitBill()` |  |
| 1757 | 1 | `public double getTransitFares()` |  |
| 1760 | 1 | `public double getTransitNet()` | Negative when the fare more than covers the wages, which a player can arrange. |
| 1763 | 1 | `public double transitNetAt(double fares)` | ...with another month of fares against this month's bill (0.7.38): the fare dial card's net, its fares InfrastructureManager.faresAt(). |
| 1767 | 1 | `public void setSubsidiesPaid(double v)` |  |
| 1768 | 1 | `public double getSubsidiesPaid()` |  |

### pensions (lines 1770-1801)

| line | len | member | says |
|---:|---:|---|---|
| 1773 | 1 | `public void setSeniors(double seniors)` |  |
| 1774 | 1 | `public double getSeniors()` |  |
| 1775 | 1 | `public double getContributions()` |  |
| 1776 | 1 | `public double getPensionsPaid()` |  |
| 1777 | 1 | `public double getPensionShortfall()` |  |
| 1778 | 5 | `public double getPensionCoverage()` |  |
| 1785 | 6 | `public double getExpenses()` | What the city pays out this month. |
| 1793 | 1 | `public double getInterestAccrued()` | The interest alone, which is the only part of getExpenses() that is CARRIED. |
| 1794 | 1 | `public void setInterest(double value)` |  |
| 1795 | 1 | `public void updateInterestExpense(double interest)` |  |
| 1797 | 1 | `public double getTotalIncome()` |  |
| 1799 | 1 | `public void setUtilityIncome(double income)` |  |
| 1800 | 1 | `public void setDebt(double debt)` |  |

### THE NATIONAL ACCOUNTS (lines 1802-2119)

| line | len | member | says |
|---:|---:|---|---|
| 1817 | 157 | `public void updateNationalAccounts(double constructionWorkDone, double governmentServices, double interest, double capitalSpend...` | Measures the month's output and the government's books, from the statements the sectors have just struck. |
| 1982 | 7 | `static double heldPrice(GoodsMarket m)` | What one unit of a held good is counted at (0.7.58): what one costs to bring in this month (GoodsMarket.landedPrice()) - the local price while somebody in the city has it on offer, the import price while nobody does -... |
| 1995 | 24 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid)` | Repopulates the government's revenue and expenditure block after a load, and nothing else - running the whole measure would be running a month of the economy with the calendar standing still. |
| 2029 | 4 | `private void setMortgageInsuranceLines()` | THE MORTGAGE INSURANCE'S TWO BUDGET LINES (0.7.11), off the lender's month: the premiums on the mortgages written, which the treasury took as each was written, and the claims - what the month's write-downs took off in... |
| 2034 | 1 | `public double getLastFoodVolume()` |  |
| 2046 | 28 | `public void restoreNationalAccounts(double[] a)` | A NEW SLOT RATHER THAN A CHANGED ONE, because slot 11 changed SCALE. |
| 2075 | 24 | `public double[] getNationalAccountsState()` |  |
| 2100 | 1 | `double[] governmentMonthToSave()` |  |
| 2101 | 1 | `void restoreGovernmentMonth(double[] m)` |  |
| 2103 | 1 | `public double getMonthGdp()` |  |
| 2104 | 1 | `public double getYearGdp()` |  |
| 2105 | 1 | `public double getGDP()` |  |
| 2107 | 1 | `public double getTaxRate()` | The income rate - the three income taxes together, TaxPolicy.getIncomeTaxRate(): the profit rate once they have parted (0.7.4). |
| 2109 | 10 | `public void setPreviousGdp(HistorySave historySave)` |  |

### CONVENIENCES - the prices the screens and the harnesses ask for by name (lines 2120-2136)

| line | len | member | says |
|---:|---:|---|---|
| 2125 | 1 | `public double getFoodLocalPrice()` | What one person-month of food costs the shops at today's market prices. |
| 2126 | 1 | `public double getIronLocalPrice()` |  |
| 2129 | 7 | `public long getFoodUnitsHeld()` | Every warehouse and shelf of food in the city, in KILOGRAMS across the thirteen. |

### SAVE AND RESTORE (lines 2137-2169)

| line | len | member | says |
|---:|---:|---|---|
| 2141 | 1 | `public List<SectorState> getSectorStates()` |  |
| 2142 | 1 | `public void restoreSectorStates(List<SectorState> s)` |  |
| 2145 | 13 | `public void restoreSectorBills(List<SectorState> saved)` | The month's bills back over the rebuild's re-derivation, and the total they add to. |
| 2158 | 1 | `public List<Markets.State> getMarketStates()` |  |
| 2159 | 1 | `public void restoreMarketStates(List<Markets.State> s)` |  |
| 2161 | 1 | `public SalesTaxLedger.State getSalesTaxState()` |  |
| 2164 | 5 | `public boolean restoreSalesTaxState(SalesTaxLedger.State state)` | The month's VAT back, AND the total that came out of it - one fact. |

### PRINTERS, RESET, THE REFORM (lines 2170-2311)

| line | len | member | says |
|---:|---:|---|---|
| 2174 | 3 | `public void printWageTaxInfo()` |  |
| 2178 | 35 | `public void printCityStats()` |  |
| 2214 | 15 | `public void resetEconomyManager()` |  |
| 2232 | 4 | `static { ... }` |  |
| 2244 | 62 | `public void redenominate(double scale)` | Every figure the economy manager holds, and every sector and market under it, in the new unit. |
| 2307 | 4 | `private static void scaleArray(double[] values, double scale)` |  |

