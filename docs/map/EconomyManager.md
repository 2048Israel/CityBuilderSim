# EconomyManager.java - 2,160 lines · 226 methods · 8 constants · model

`ham/citybuildersim/EconomyManager.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (61), [BuildingsTemplate](BuildingsTemplate.md) (17), [Good](Good.md) (13), [BuildingType](BuildingType.md) (11), [TaxPolicy](TaxPolicy.md) (9), [NationalAccounts](NationalAccounts.md) (9), [Markets](Markets.md) (5), [SalesTaxLedger](SalesTaxLedger.md) (5), [BuildingManager](BuildingManager.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Traffic](Traffic.md) (4), [SectorState](SectorState.md) (4), [Sectors](Sectors.md) (3), [JobType](JobType.md) (3), [RealEstate](RealEstate.md) (3), [Statement](Statement.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [Retail](Retail.md) (3), [BondMarket](BondMarket.md) (3), [Equity](Equity.md) (3), [WageBand](WageBand.md) (3), [GoodsMarket](GoodsMarket.md) (2), [FamilyModel](FamilyModel.md) (2), [SupplierCredit](SupplierCredit.md) (2), [GameLog](GameLog.md) (2), [SocialSecurity](SocialSecurity.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [BalanceSheet](BalanceSheet.md) (1), [Game](Game.md) (1), [LuxuryRetail](LuxuryRetail.md) (1)... and 1 more

**Used by (42):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HousingCheck](HousingCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Materials](Materials.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [OutwardInvestment](OutwardInvestment.md), [PeopleScreen](PeopleScreen.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 31 | THE CITY'S PARTS |
| 62 | WHAT THE CITY TELLS THE ECONOMY EACH MONTH |
| 131 | · utilities |
| 220 | · the world |
| 238 | · the land |
| 260 | HOUSING - what the landlords need from the family model and the land office |
| 380 | CASH BY NAME - the seam almost everything routes through |
| 394 | THE MONTH, AT THE TOP |
| 474 | · property tax |
| 524 | · maintenance |
| 577 | · the builders' sales tax |
| 651 | THE REBATES ON A NEW HOME, AND THE CITY'S (0.7.19, revised) |
| 867 | · the strike |
| 1027 | INSOLVENCY, at the bottom of the investment step |
| 1156 | WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS |
| 1173 | THE OWNERS AND THE MONEY ABROAD |
| 1310 | THE MONTH, IN THE MIDDLE AND AT THE BOTTOM |
| 1336 | THE CITY'S BOOKS |
| 1424 | · WHAT THE POLICY TAB ASKS (0.7.36) |
| 1491 | · EI and the student grant (2026-09-11) |
| 1563 | · the services |
| 1591 | THE TRANSIT BOOKS (2026-09-16) |
| 1644 | · pensions |
| 1676 | THE NATIONAL ACCOUNTS |
| 1977 | CONVENIENCES - the prices the screens and the harnesses ask for by name |
| 1994 | SAVE AND RESTORE |
| 2027 | PRINTERS, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 534 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, ...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 546 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 710 | `EconomyManager.PBRH_MIN_UNITS` | `4` | A building with this many dwellings or more is purpose-built rental housing (CRA: "at least 4 residential units each with a private kitchen, a private bathroom, and a private living area"). |
| 712 | `EconomyManager.NRRP_SHARE` | `.36` | The new residential rental property rebate's share of the tax on a unit: 36% (Excise Tax Act section 256.2(3)(a); CRA RC4231). |
| 714 | `EconomyManager.NRRP_CAP` | `6.3` | ...and its most a unit, in founding thousands: $6,300. |
| 716 | `EconomyManager.NRRP_FULL_BELOW` | `350` | ...in full for a unit worth up to this, in founding thousands: $350,000. |
| 718 | `EconomyManager.NRRP_NONE_FROM` | `450` | ...and none for a unit worth this or more: $450,000. |
| 2086 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 133 | `private double pricePerWatt` |  |
| 134 | `private double pricePerWaterUnit` |  |
| 228 | `private double exchangeRate` | Every world price in the game is quoted by the world in ITS money and converted at this rate - times the world's own price level, which is how world inflation rides in. |
| 245 | `private double landPricePerSqFt` | What the city currently charges for a square foot, so assessed values track the price the player sets. |
| 264 | `private double householdCount` |  |
| 265 | `private double marginalHousingCost` |  |
| 266 | `private double occupiedHomes` |  |
| 267 | `private double studioSeekers, familySeekers, studioSeekerHeads, familySeekerHeads` |  |
| 476 | `private double totalPropertyTax` |  |
| 477 | `private final Map<String, Double> propertyTaxBySector` |  |
| 539 | `private double maintenanceBillTotal` |  |
| 540 | `private double maintenanceMaterialsTotal` |  |
| 541 | `private double maintenancePointsTotal` |  |
| 542 | `private double cityMaintenanceBill` |  |
| 543 | `private double bankMaintenanceBill` |  |
| 544 | `private final Map<String, Double> maintenanceBySector` |  |
| 721 | `private java.util.function.DoubleSupplier foundingToToday` | Today's money for a founding dollar: the month's price index over the unit. |
| 1031 | `private double overdraftForgivenThisMonth` |  |
| 1032 | `private final Map<String, Double> overdraftForgivenBySector` |  |
| 1033 | `private final Map<String, Double> overdraftForgivenThisMonthBySector` |  |
| 1160 | `private final Map<String, Double> depositInterestPaid` |  |
| 1177 | `private OutwardInvestment outward` |  |
| 1268 | `private BondMarket bondMarket` | The bond market (0.7.12), wired by Game: its maturities are paid at the credit settle, and the other sectors' bonds a company holds are among its assets. |
| 1277 | `private Equity equity` |  |
| 1281 | `private final Map<String, Double> equityRaised` |  |
| 1282 | `private final Map<String, Double> dividendsPaid` |  |
| 1283 | `private final Map<String, Double> sharesBoughtBack` |  |
| 1288 | `private final Map<String, Double> stolen` | Taken from each sector's till by thieves this month. |
| 1340 | `private double salesTax` |  |
| 1341 | `private double totalWageTax` |  |
| 1342 | `private double totalBankTax` |  |
| 1343 | `private double totalContributions` |  |
| 1344 | `private double interest` |  |
| 1345 | `private double utilityIncome` |  |
| 1346 | `private double debt` |  |
| 1347 | `private double GDP` |  |
| 1348 | `private double yearGDP` |  |
| 1493 | `private double totalEiPremiums` |  |
| 1494 | `private double eiBenefits` |  |
| 1495 | `private double studentGrants` |  |
| 1498 | `private double totalHealthPremiums` | The month's health premium off every wage, struck in getTaxIncome() beside the EI premium (2026-09-19). |
| 1511 | `private double studentLoanInterest` | The interest the graduates paid the treasury on their student loans this month (2026-09-21): a revenue line beside the premiums, set by Game off the household ledger the month it is struck. |
| 1520 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO BUDGET LINES (0.7.0), set by Game where the central bank settles with the treasury at the top of the month: the remittance in, the interest on the advances out. |
| 1531 | `private double fundTransfer` | THE TRANSFER FROM THE CITY'S FUND (0.7.14), set by Game where the treasury settles, for the central bank's lines' reason: Game moves the cash there. |
| 1537 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43), set by Game where the treasury pays it, after the month's sale - for the central bank's lines' reason: Game moves the cash there. |
| 1565 | `private double healthcareBill, healthcareFees` |  |
| 1574 | `private double educationBill, educationFees` |  |
| 1587 | `private double safetyBill` | What the police and the prisons cost this month: payroll and upkeep, no fees - nobody pays to be policed or jailed. |
| 1625 | `private double transitBill, transitFares` |  |
| 1640 | `private double subsidiesPaid` | What the city paid this month to hold protected sectors at break-even. |
| 1646 | `private double seniors` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 2132 | **type** `public class EconomyManager` | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. |

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

### WHAT THE CITY TELLS THE ECONOMY EACH MONTH (lines 62-130)

| line | len | member | says |
|---:|---:|---|---|
| 82 | 1 | `public void setTotalJobs(long jobs)` |  |
| 83 | 1 | `public void setPopulation(long pop)` |  |
| 84 | 1 | `public void setHouseholds(long houseCap)` |  |
| 85 | 1 | `public void setCash(int money)` |  |
| 86 | 1 | `public void setTotalWage(double wage)` |  |
| 87 | 3 | `public void setWageDetail(double[] staffedPerType)` |  |
| 90 | 1 | `public double[] getStaffedWagePerType()` |  |
| 91 | 1 | `public long getPopulation()` |  |
| 93 | 5 | `public void updateJobFillRate(double[] fill)` |  |
| 105 | 9 | `public void updateWages(double[] wagePerType, long[] bankPosts)` | The wage schedule, handed to every sector with its own posts. |
| 116 | 1 | `public double[] getWageRates()` | A copy, so nothing downstream can quietly rewrite the schedule. |
| 123 | 7 | `public double getBankPayroll()` | What the bank's own staff cost this month. |

### utilities (lines 131-219)

| line | len | member | says |
|---:|---:|---|---|
| 136 | 1 | `public double getPricePerWatt()` |  |
| 137 | 1 | `public double getPricePerWaterUnit()` |  |
| 139 | 4 | `public void setPricePerWatt(double price)` |  |
| 144 | 4 | `public void setPricePerWaterUnit(double price)` |  |
| 149 | 1 | `public void setEnergyRatio(double ratio)` |  |
| 150 | 1 | `public void setWaterRatio(double ratio)` |  |
| 152 | 1 | `public void setRoadRatio(double ratio)` | The road network's throughput, handed to everyone whose goods move on it. |
| 168 | 11 | `public void setRoadRatio(InfrastructureManager roads, BuildingManager buildings)` | ...and the same thing done properly, once the streams exist: each sector feels the congestion IT generates. |
| 180 | 1 | `public void setHealthRatio(double ratio)` | What is left of the workforce once this month's illness is taken off. |
| 183 | 1 | `public double getHealthRatio()` | What the sectors are currently running at, for the harnesses. |
| 197 | 8 | `public void setElectricityConsumption()` | Every sector's draw, off its own buildings. |
| 207 | 5 | `public double getSectorElectricityCharges()` | The month's power bills, as the sectors' statements booked them. |
| 214 | 5 | `public double getSectorWaterCharges()` | The month's water bills, likewise. |

### the world (lines 220-237)

| line | len | member | says |
|---:|---:|---|---|
| 230 | 5 | `public void setExchangeRate(double rate)` |  |
| 236 | 1 | `public double getExchangeRate()` |  |

### the land (lines 238-259)

| line | len | member | says |
|---:|---:|---|---|
| 247 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 248 | 1 | `public double getLandPricePerSqFt()` |  |
| 251 | 3 | `public double landValueOf(Sector s)` | What a sector's land is worth at the city's current price. |
| 256 | 3 | `public double landValueOf(BuildingType category)` | What a city-owned category's land is worth. |

### HOUSING - what the landlords need from the family model and the land office (lines 260-379)

| line | len | member | says |
|---:|---:|---|---|
| 269 | 1 | `public void setHouseholdCount(double count)` |  |
| 270 | 1 | `public void setMarginalHousingCost(double perCap)` |  |
| 271 | 1 | `public double getMarginalHousingCost()` |  |
| 272 | 1 | `public void setOccupiedHomes(double occupied)` |  |
| 273 | 6 | `public void setHousingSeekers(double studio, double family, double studioHeads, double familyHeads)` |  |
| 279 | 1 | `public double getStudioSeekers()` |  |
| 280 | 1 | `public double getFamilySeekers()` |  |
| 288 | 21 | `public void updateHousing()` | Hands the landlords and the shops what the month looks like: how many people there are, how many doors, who is chasing them and what a new home costs. |
| 322 | 3 | `public double housingBuildHurdle(BuildingsTemplate t)` | What one of these has to earn a month before it is worth putting up. |
| 334 | 17 | `public double housingCarry(BuildingsTemplate t)` | WHAT ONE OF THESE COSTS TO HOLD A MONTH, before any interest: its maintenance, RealEstate.MAINTENANCE_PER_YEAR on the structure, and its property tax, the property rate on the structure and its plot. |
| 353 | 26 | `public void repriceHousingCosts()` | The cheapest way to house one more person, land included, per segment. |

### CASH BY NAME - the seam almost everything routes through (lines 380-393)

| line | len | member | says |
|---:|---:|---|---|
| 384 | 4 | `public double getSectorCash(String key)` |  |
| 389 | 4 | `public void setSectorCash(String key, double cash)` |  |

### THE MONTH, AT THE TOP (lines 394-473)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 3 | `public void updateBusinessCredit(double primeRate)` | Prices this month's business credit and hands every sector its interest bill BEFORE the statements run, so the figures they bank are net of it. |
| 416 | 30 | `public void updateBusinessCredit(double primeRate, double insuredMortgageRate)` | ...and the insured mortgage's rate beside prime (0.7.11, Bank.insuredMortgageRate()): what a landlord's new mortgage is written at and a term that ends renews at. |
| 455 | 9 | `public void refreshCreditAssets()` | Tells the credit manager what each sector is worth right now - its own sheet plus what it holds abroad, which a lender that ignored would call a rich sector broke. |
| 466 | 7 | `public void pushBalanceSheetInputs()` | Book values and outstanding debt, refreshed onto each set of books. |

### property tax (lines 474-523)

| line | len | member | says |
|---:|---:|---|---|
| 489 | 4 | `public double getAssessedValue(Sector s)` | Land at today's price plus FINISHED buildings at replacement cost. |
| 495 | 3 | `public double getAssessedValue(BuildingType category)` | The same for a city-owned category, which the city does not tax but a screen may want to show. |
| 500 | 3 | `public double getPropertyTaxFor(Sector s)` | One month's property tax on a sector, at its own rate. |
| 509 | 10 | `public void chargePropertyTax()` | Hands each sector its property tax bill for the month. |
| 520 | 1 | `public double getTotalPropertyTax()` |  |
| 521 | 1 | `public void setTotalPropertyTax(double value)` |  |
| 522 | 1 | `public double getPropertyTaxCharged(String key)` |  |

### maintenance (lines 524-576)

| line | len | member | says |
|---:|---:|---|---|
| 562 | 6 | `public double maintenanceBillFor(Sector s, double materialPrice)` | The month's repair bill for one sector, in money - materials priced in, and the builders' sales tax. |
| 570 | 6 | `public double maintenanceBillFor(BuildingType category, double materialPrice)` | The same for a city-owned category. |

### the builders' sales tax (lines 577-650)

| line | len | member | says |
|---:|---:|---|---|
| 580 | 3 | `public double buildersSalesRate()` | What the builders charge their sales tax at. |
| 590 | 3 | `public double withBuildersTax(double beforeTax)` | A price before the builders' sales tax, with it passed on (0.7.19): the builders remit their rate on what they bill, so what they bill for a price p that is theirs to keep is p / (1 - rate). |
| 611 | 5 | `public boolean claimsTaxOnBuildings(String owner, BuildingsTemplate t)` | WHETHER AN OWNER CLAIMS THE TAX ON ITS BUILDINGS BACK (0.7.19). |
| 618 | 4 | `private static boolean makesTaxableSupplies(Sector s)` | A sector with a good the sales tax touches. |
| 631 | 7 | `public double ownersBuildCost(BuildingsTemplate t, String owner, double beforeTax)` | What a building's price before the builders' tax costs this owner in the end: the price, and whatever of the tax on it the owner does not get back - none of it for a business that claims it, none for a landlord's purp... |
| 647 | 3 | `public double ownersRepairCost(BuildingsTemplate t, String owner, double beforeTax)` | ...and a REPAIR bill's (0.7.19, revised): the bill itself when the owner claims the tax on it back as an input, the bill with the tax when it cannot. |

### THE REBATES ON A NEW HOME, AND THE CITY'S (0.7.19, revised) (lines 651-866)

| line | len | member | says |
|---:|---:|---|---|
| 722 | 1 | `public void setFoundingToToday(java.util.function.DoubleSupplier s)` |  |
| 725 | 3 | `public boolean isLandlordsHome(String owner, BuildingsTemplate t)` | Whether this building is one of the landlords' homes. |
| 730 | 3 | `public static boolean isPurposeBuiltRental(BuildingsTemplate t)` | Whether a home of this template is purpose-built rental housing. |
| 740 | 13 | `public double rentalPropertyRebate(BuildingsTemplate t, double taxPerBuilding)` | The new residential rental property rebate on one building of this template, on this much tax paid on it, in today's money: A x ($450,000 - B) / $100,000 a unit (THE REBATES ON A NEW HOME). |
| 765 | 7 | `public double taxRecoveredShare(String owner, BuildingsTemplate t, double taxPerBuilding)` | The share of the builders' tax on a new building that its owner gets back (0.7.19, revised): all of it for a business that claims it as a credit (claimsTaxOnBuildings()) and for a landlord's purpose-built rental; the ... |
| 774 | 4 | `public double maintenanceMaterialsTotal()` | The material UNITS the whole city's repairs consume this month. |
| 780 | 4 | `public double maintenancePointsTotal()` | The builders' time the whole city's repairs consume this month. |
| 794 | 31 | `public void chargeMaintenance(double materialPrice)` | Hands every owner its repair bill for the month. |
| 827 | 1 | `public double getMaintenanceBillTotal()` | Every owner's repair bill added up - what the builders are owed. |
| 829 | 1 | `public double getCityMaintenanceBill()` | The share of it the treasury pays, because the city owns those buildings. |
| 831 | 1 | `public double getBankMaintenanceBill()` | The share the bank pays, for its branches. |
| 847 | 5 | `public double bankOperatingCost(int branches)` | ...AND WHAT THESE BRANCHES COST TO RUN (0.7.19): the template's upkeep, a branch's rent, systems and supplies, which was charged to nobody until then. |
| 854 | 6 | `public double bankOperatingCostPerLaterBranch()` | ...what one branch past the charter carries of it: the template's upkeep. |
| 861 | 1 | `public double getMaintenanceMaterialsTotal()` | Material UNITS, not money - the yard has to find these. |
| 863 | 1 | `public double getMaintenancePointsTotal()` | Builders' time, which comes straight off what the city can put up. |
| 865 | 1 | `public double getMaintenanceCharge(String key)` | One sector's bill, for the screens. |

### the strike (lines 867-1026)

| line | len | member | says |
|---:|---:|---|---|
| 875 | 8 | `public void strikeSectors()` | Strikes every sector's statement, settles the VAT from the same figures, and banks the month. |
| 897 | 16 | `void settleSupplierCredit()` | THE SUPPLIERS' CREDIT, SETTLED AT THE STRIKE (0.7.44; SupplierCredit). |
| 923 | 54 | `public double settleSalesTax()` | The month's sales tax, STRUCK FROM THE TRADES. |
| 979 | 3 | `private double exemptSales(Sector s)` | The exempt part of a sector's local sales: what it sold of goods the tax never touches. |
| 984 | 9 | `private static double exemptOf(Sector s, double localSales)` | ...of these local sales: all of them for a sector making only exempt goods, none otherwise. |
| 1003 | 18 | `private double salesTaxSoFar(Sector s, Sector.Ledger p)` | The sales tax a sector's month owes SO FAR, off its ledger as it stands (0.7.12 round 6): settleSalesTax()'s arithmetic for one sector - its taxable sales at its own rate, less the credit on what it bought at each sup... |
| 1023 | 1 | `public String getDeepestRefundSector()` | Whoever the city owes this month, or null. |
| 1024 | 1 | `public double getSectorSalesTax(String key)` |  |
| 1025 | 1 | `public double getSectorSalesTax(Sector s)` |  |

### INSOLVENCY, at the bottom of the investment step (lines 1027-1155)

| line | len | member | says |
|---:|---:|---|---|
| 1049 | 36 | `public double settleInsolvency()` | End of the month: work out who is beyond saving and write their debt down to what their assets support - since 0.7.8 each sector's slice of defaulted firms, since 0.7.12 round 4 the part that could not pay its month w... |
| 1086 | 1 | `public double getOverdraftForgiven()` |  |
| 1087 | 1 | `public double getOverdraftForgivenThisMonth(String key)` |  |
| 1088 | 1 | `public double getOverdraftForgivenTotal(String key)` |  |
| 1104 | 31 | `public void settleBusinessCredit(int month)` | Repay what matured - and since 0.7.11 the principal a mortgage's payment took - then borrow if that left the sector short. |
| 1143 | 12 | `double monthObligations(Sector s, double matured)` | What the month asked a sector to pay (round 4): the costs on this month's statement - the goods it paid for this month, its payroll, utilities and repairs - its interest and its taxes, and the principal that fell due,... |

### WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS (lines 1156-1172)

| line | len | member | says |
|---:|---:|---|---|
| 1162 | 1 | `public void clearDepositInterest()` |  |
| 1164 | 4 | `public void recordDepositInterest(String sector, double amount)` |  |
| 1169 | 3 | `public double getDepositInterestPaid(String sector)` |  |

### THE OWNERS AND THE MONEY ABROAD (lines 1173-1309)

| line | len | member | says |
|---:|---:|---|---|
| 1178 | 1 | `public void setOutwardInvestment(OutwardInvestment outward)` |  |
| 1179 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |
| 1220 | 36 | `public double purchaseBudget(Sector s, double dividend, int month)` | WHAT A SECTOR CAN PAY FOR AS THE MARKETS CLEAR (0.7.12 round 6) - see Sector, BUY ONLY WHAT IT CAN PAY FOR. |
| 1258 | 3 | `private static double supplierCreditLimit(Sector s)` | What a buyer's suppliers will wait for this clearing: the grocers' (Retail.supplierCreditLimit()); nothing for anybody else. |
| 1263 | 3 | `public double getForeignAssets(String sector)` | What one sector holds abroad, in the city's money at the rate it was last valued at. |
| 1269 | 1 | `public void setBondMarket(BondMarket market)` |  |
| 1270 | 1 | `public BondMarket getBondMarket()` |  |
| 1273 | 3 | `public double getBondAssets(String sector)` | What one sector holds of the other sectors' bonds, at face - an asset beside its cash and what it holds abroad (0.7.12). |
| 1278 | 1 | `public void setEquity(Equity equity)` |  |
| 1279 | 1 | `public Equity getEquity()` |  |
| 1285 | 1 | `public void clearEquityFlows()` |  |
| 1289 | 4 | `public void recordStolen(String sector, double amount)` |  |
| 1293 | 1 | `public double getStolen(String sector)` |  |
| 1294 | 4 | `public void recordSharesBoughtBack(String sector, double amount)` |  |
| 1298 | 1 | `public double getSharesBoughtBack(String sector)` |  |
| 1299 | 4 | `public void recordEquityRaised(String sector, double amount)` |  |
| 1303 | 4 | `public void recordDividendPaid(String sector, double amount)` |  |
| 1307 | 1 | `public double getEquityRaised(String sector)` |  |
| 1308 | 1 | `public double getDividendsPaid(String sector)` |  |

### THE MONTH, IN THE MIDDLE AND AT THE BOTTOM (lines 1310-1335)

| line | len | member | says |
|---:|---:|---|---|
| 1315 | 3 | `public void updateEcon()` | The economy's inputs for the month, set from the simulation. |
| 1325 | 5 | `public void finalEconUpdate(Game game)` | The bottom of the month: every market clears, every maker produces. |
| 1332 | 3 | `public void refreshEconPrices()` | What the load path needs of the bottom of the month, and nothing else: the draw the expense lines need. |

### THE CITY'S BOOKS (lines 1336-1423)

| line | len | member | says |
|---:|---:|---|---|
| 1350 | 1 | `public void setBankTax(double amount)` |  |
| 1351 | 1 | `public double getBankTax()` |  |
| 1354 | 1 | `public double getProfitTax(Sector s)` | The profit tax one sector paid this month, as its statement carries it. |
| 1357 | 5 | `public double getBusinessTax()` | Every sector's profit tax but the food industry's, plus the bank's - the "business tax" line. |
| 1364 | 1 | `public double getIndustrialTax()` | The food industry's, on its own line, as the screens have always shown it. |
| 1365 | 1 | `public double getHeavyIndustryTax()` |  |
| 1366 | 1 | `public double getConstructionTax()` |  |
| 1367 | 1 | `public double getSalesTax()` |  |
| 1368 | 1 | `public double getWageTax()` |  |
| 1369 | 1 | `public double getUtilityIncome()` |  |
| 1372 | 3 | `public double wageTaxOnPayroll(double[] payrollPerType)` | The banded wage tax on one sector's payroll, asked of the policy rather than multiplied out in the UI. |
| 1376 | 1 | `public double getTaxIncome()` |  |
| 1387 | 1 | `public double getTaxIncomeNow()` | The same take at today's rates, WRITING NOTHING (0.7.31, the Government spec's B8): what Game.getIncome() - the header's EARNED - reads every time the header draws. |
| 1390 | 33 | `private double taxTake(boolean strike)` | The tax take, summed; struck into the month's fields only when `strike`. |

### WHAT THE POLICY TAB ASKS (0.7.36) (lines 1424-1490)

| line | len | member | says |
|---:|---:|---|---|
| 1438 | 3 | `public double profitTaxUnder(Sector s, TaxPolicy p)` | One sector's profit tax under a policy (M2): its last struck pre-tax income at that policy's rate, nothing on a loss - Sector.strike()'s rule. |
| 1451 | 6 | `public double salesTaxUnder(Sector s, TaxPolicy p)` | One sector's sales tax under a policy (M2): its net remittance scaled by the ratio of the two rates, or its taxable sales at the rate when it was charged nothing. |
| 1459 | 5 | `public double propertyTaxUnder(Sector s, TaxPolicy p)` | One sector's property tax under a policy (M2): its land at today's price on that policy's roll share, and its finished buildings, at that policy's monthly rate - getPropertyTaxFor()'s rule. |
| 1466 | 3 | `public double wageTaxUnder(WageBand band, TaxPolicy p)` | One wage band's wage tax under a policy (M2): every job type in it, staffed, at that policy's rate for the band - TaxPolicy.wageTaxPerTier()'s rule. |
| 1471 | 7 | `public double payrollIn(WageBand band)` | The staffed payroll one wage band carries this month: what its wage tax is struck on. |
| 1480 | 1 | `public double getWageBill()` | The wage bill the pension contribution and both premiums are struck on (M3): the month's total wage, as getTaxIncome() reads it. |
| 1483 | 1 | `public double premiumAt(double rate)` | A premium at this rate off that bill (M3): the EI and health premiums' own rule. |
| 1486 | 1 | `public double contributionsAt(double rate)` | Pension contributions at this rate off that bill (M3): SocialSecurity's own rule. |
| 1489 | 1 | `public double pensionsPaidUnder(TaxPolicy p)` | What the pensions would come to under a policy (M4): today's seniors at its pension. |

### EI and the student grant (2026-09-11) (lines 1491-1562)

| line | len | member | says |
|---:|---:|---|---|
| 1501 | 1 | `public double getHealthPremiums()` | What the health premium raised this month, employee side, off the whole wage bill. |
| 1522 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 1527 | 1 | `public double getCentralBankRemittance()` |  |
| 1528 | 1 | `public double getCentralBankInterest()` |  |
| 1533 | 1 | `public void setFundTransfer(double transfer)` |  |
| 1534 | 1 | `public double getFundTransfer()` |  |
| 1539 | 1 | `public void setFoodAssistance(double paid)` |  |
| 1540 | 1 | `public double getFoodAssistance()` |  |
| 1543 | 1 | `public void setStudentLoanInterest(double interest)` | Sets the month's student-loan interest, the treasury's. |
| 1546 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month. |
| 1549 | 4 | `public void setOutsidePayments(double eiBenefits, double studentGrants)` | The month's EI bill and grant bill as the treasury paid them: set by Game where it pays them, at the top of the month (payEiBenefits() since 0.7.3, payStudentGrants() since 0.7.1), and on the load path. |
| 1554 | 1 | `public double getEiPremiums()` |  |
| 1555 | 1 | `public double getEiBenefits()` |  |
| 1556 | 1 | `public double getStudentGrants()` |  |
| 1559 | 3 | `public double getEiCoverage()` | What the premiums cover of the EI bill. |

### the services (lines 1563-1590)

| line | len | member | says |
|---:|---:|---|---|
| 1566 | 4 | `public void setHealthcare(double grossCost, double fees)` |  |
| 1570 | 1 | `public double getHealthcareBill()` |  |
| 1571 | 1 | `public double getHealthcareFees()` |  |
| 1572 | 1 | `public double getHealthcareNet()` |  |
| 1575 | 4 | `public void setEducation(double grossCost, double fees)` |  |
| 1579 | 1 | `public double getEducationBill()` |  |
| 1580 | 1 | `public double getEducationFees()` |  |
| 1581 | 1 | `public double getEducationNet()` |  |
| 1588 | 1 | `public void setSafety(double grossCost)` |  |
| 1589 | 1 | `public double getSafetyBill()` |  |

### THE TRANSIT BOOKS (2026-09-16) (lines 1591-1643)

| line | len | member | says |
|---:|---:|---|---|
| 1626 | 4 | `public void setTransit(double grossCost, double fares)` |  |
| 1630 | 1 | `public double getTransitBill()` |  |
| 1631 | 1 | `public double getTransitFares()` |  |
| 1634 | 1 | `public double getTransitNet()` | Negative when the fare more than covers the wages, which a player can arrange. |
| 1637 | 1 | `public double transitNetAt(double fares)` | ...with another month of fares against this month's bill (0.7.38): the fare dial card's net, its fares InfrastructureManager.faresAt(). |
| 1641 | 1 | `public void setSubsidiesPaid(double v)` |  |
| 1642 | 1 | `public double getSubsidiesPaid()` |  |

### pensions (lines 1644-1675)

| line | len | member | says |
|---:|---:|---|---|
| 1647 | 1 | `public void setSeniors(double seniors)` |  |
| 1648 | 1 | `public double getSeniors()` |  |
| 1649 | 1 | `public double getContributions()` |  |
| 1650 | 1 | `public double getPensionsPaid()` |  |
| 1651 | 1 | `public double getPensionShortfall()` |  |
| 1652 | 5 | `public double getPensionCoverage()` |  |
| 1659 | 6 | `public double getExpenses()` | What the city pays out this month. |
| 1667 | 1 | `public double getInterestAccrued()` | The interest alone, which is the only part of getExpenses() that is CARRIED. |
| 1668 | 1 | `public void setInterest(double value)` |  |
| 1669 | 1 | `public void updateInterestExpense(double interest)` |  |
| 1671 | 1 | `public double getTotalIncome()` |  |
| 1673 | 1 | `public void setUtilityIncome(double income)` |  |
| 1674 | 1 | `public void setDebt(double debt)` |  |

### THE NATIONAL ACCOUNTS (lines 1676-1976)

| line | len | member | says |
|---:|---:|---|---|
| 1691 | 145 | `public void updateNationalAccounts(double constructionWorkDone, double governmentServices, double interest, double capitalSpend...` | Measures the month's output and the government's books, from the statements the sectors have just struck. |
| 1844 | 7 | `static double heldPrice(GoodsMarket m)` | What one unit of a held good is counted at (0.7.58): what one costs to bring in this month (GoodsMarket.landedPrice()) - the local price while somebody in the city has it on offer, the import price while nobody does -... |
| 1857 | 20 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid)` | Repopulates the government's revenue and expenditure block after a load, and nothing else - running the whole measure would be running a month of the economy with the calendar standing still. |
| 1887 | 4 | `private void setMortgageInsuranceLines()` | THE MORTGAGE INSURANCE'S TWO BUDGET LINES (0.7.11), off the lender's month: the premiums on the mortgages written, which the treasury took as each was written, and the claims - what the month's write-downs took off in... |
| 1892 | 1 | `public double getLastFoodVolume()` |  |
| 1904 | 27 | `public void restoreNationalAccounts(double[] a)` | A NEW SLOT RATHER THAN A CHANGED ONE, because slot 11 changed SCALE. |
| 1932 | 24 | `public double[] getNationalAccountsState()` |  |
| 1957 | 1 | `double[] governmentMonthToSave()` |  |
| 1958 | 1 | `void restoreGovernmentMonth(double[] m)` |  |
| 1960 | 1 | `public double getMonthGdp()` |  |
| 1961 | 1 | `public double getYearGdp()` |  |
| 1962 | 1 | `public double getGDP()` |  |
| 1964 | 1 | `public double getTaxRate()` | The income rate - the three income taxes together, TaxPolicy.getIncomeTaxRate(): the profit rate once they have parted (0.7.4). |
| 1966 | 10 | `public void setPreviousGdp(HistorySave historySave)` |  |

### CONVENIENCES - the prices the screens and the harnesses ask for by name (lines 1977-1993)

| line | len | member | says |
|---:|---:|---|---|
| 1982 | 1 | `public double getFoodLocalPrice()` | What one person-month of food costs the shops at today's market prices. |
| 1983 | 1 | `public double getIronLocalPrice()` |  |
| 1986 | 7 | `public long getFoodUnitsHeld()` | Every warehouse and shelf of food in the city, in KILOGRAMS across the thirteen. |

### SAVE AND RESTORE (lines 1994-2026)

| line | len | member | says |
|---:|---:|---|---|
| 1998 | 1 | `public List<SectorState> getSectorStates()` |  |
| 1999 | 1 | `public void restoreSectorStates(List<SectorState> s)` |  |
| 2002 | 13 | `public void restoreSectorBills(List<SectorState> saved)` | The month's bills back over the rebuild's re-derivation, and the total they add to. |
| 2015 | 1 | `public List<Markets.State> getMarketStates()` |  |
| 2016 | 1 | `public void restoreMarketStates(List<Markets.State> s)` |  |
| 2018 | 1 | `public SalesTaxLedger.State getSalesTaxState()` |  |
| 2021 | 5 | `public boolean restoreSalesTaxState(SalesTaxLedger.State state)` | The month's VAT back, AND the total that came out of it - one fact. |

### PRINTERS, RESET, THE REFORM (lines 2027-2160)

| line | len | member | says |
|---:|---:|---|---|
| 2031 | 3 | `public void printWageTaxInfo()` |  |
| 2035 | 35 | `public void printCityStats()` |  |
| 2071 | 14 | `public void resetEconomyManager()` |  |
| 2088 | 4 | `static { ... }` |  |
| 2100 | 55 | `public void redenominate(double scale)` | Every figure the economy manager holds, and every sector and market under it, in the new unit. |
| 2156 | 4 | `private static void scaleArray(double[] values, double scale)` |  |

