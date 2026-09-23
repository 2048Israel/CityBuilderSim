# EconomyManager.java - 1,401 lines · 184 methods · 3 constants · model

`ham/citybuildersim/EconomyManager.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (41), [Good](Good.md) (11), [BuildingType](BuildingType.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (9), [Markets](Markets.md) (5), [SalesTaxLedger](SalesTaxLedger.md) (5), [BuildingManager](BuildingManager.md) (4), [Traffic](Traffic.md) (4), [SectorState](SectorState.md) (4), [Sectors](Sectors.md) (3), [BusinessDebtManager](BusinessDebtManager.md) (3), [TaxPolicy](TaxPolicy.md) (3), [NationalAccounts](NationalAccounts.md) (3), [RealEstate](RealEstate.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [Equity](Equity.md) (3), [FamilyModel](FamilyModel.md) (2), [Retail](Retail.md) (2), [GoodsMarket](GoodsMarket.md) (1), [JobType](JobType.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [Statement](Statement.md) (1), [GameLog](GameLog.md) (1), [Game](Game.md) (1), [SocialSecurity](SocialSecurity.md) (1), [HistorySave](HistorySave.md) (1)

**Used by (32):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [Materials](Materials.md), [MoneyAudit](MoneyAudit.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [OutwardInvestment](OutwardInvestment.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 31 | THE CITY'S PARTS |
| 62 | WHAT THE CITY TELLS THE ECONOMY EACH MONTH |
| 131 | · utilities |
| 220 | · the world |
| 238 | · the land |
| 260 | HOUSING - what the landlords need from the family model and the land office |
| 358 | CASH BY NAME - the seam almost everything routes through |
| 372 | THE MONTH, AT THE TOP |
| 413 | · property tax |
| 463 | · maintenance |
| 568 | · the strike |
| 635 | INSOLVENCY, at the bottom of the investment step |
| 690 | WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS |
| 707 | THE OWNERS AND THE MONEY ABROAD |
| 753 | THE MONTH, IN THE MIDDLE AND AT THE BOTTOM |
| 779 | THE CITY'S BOOKS |
| 847 | · EI and the student grant (2026-09-11) |
| 907 | · the services |
| 935 | THE TRANSIT BOOKS (2026-09-16) |
| 972 | · pensions |
| 1002 | THE NATIONAL ACCOUNTS |
| 1226 | CONVENIENCES - the prices the screens and the harnesses ask for by name |
| 1243 | SAVE AND RESTORE |
| 1276 | PRINTERS, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 473 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, ...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 485 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 1335 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 67 | `private int totalJobs` |  |
| 68 | `private int population` |  |
| 69 | `private int households` |  |
| 70 | `private double totalWage` |  |
| 71 | `private final double[] fillRate` |  |
| 74 | `private final double[] wageRates` | What ONE job of each tier costs a month, as PopulationManager last set it. |
| 77 | `private double[] staffedWagePerType` | The wage bill per job type, staffed, for the banded wage tax. |
| 80 | `private int[] bankJobs` | The bank's posts, so its tellers can be charged to the bank and not to the shops. |
| 133 | `private double pricePerWatt` |  |
| 134 | `private double pricePerWaterUnit` |  |
| 228 | `private double exchangeRate` | Every world price in the game is quoted by the world in ITS money and converted at this rate - times the world's own price level, which is how world inflation rides in. |
| 245 | `private double landPricePerSqFt` | What the city currently charges for a square foot, so assessed values track the price the player sets. |
| 264 | `private double householdCount` |  |
| 265 | `private double marginalHousingCost` |  |
| 266 | `private double occupiedHomes` |  |
| 267 | `private double studioSeekers, familySeekers, studioSeekerHeads, familySeekerHeads` |  |
| 415 | `private double totalPropertyTax` |  |
| 416 | `private final Map<String, Double> propertyTaxBySector` |  |
| 478 | `private double maintenanceBillTotal` |  |
| 479 | `private double maintenanceMaterialsTotal` |  |
| 480 | `private double maintenancePointsTotal` |  |
| 481 | `private double cityMaintenanceBill` |  |
| 482 | `private double bankMaintenanceBill` |  |
| 483 | `private final Map<String, Double> maintenanceBySector` |  |
| 639 | `private double overdraftForgivenThisMonth` |  |
| 640 | `private final Map<String, Double> overdraftForgivenBySector` |  |
| 641 | `private final Map<String, Double> overdraftForgivenThisMonthBySector` |  |
| 694 | `private final Map<String, Double> depositInterestPaid` |  |
| 711 | `private OutwardInvestment outward` |  |
| 720 | `private Equity equity` |  |
| 724 | `private final Map<String, Double> equityRaised` |  |
| 725 | `private final Map<String, Double> dividendsPaid` |  |
| 726 | `private final Map<String, Double> sharesBoughtBack` |  |
| 731 | `private final Map<String, Double> stolen` | Taken from each sector's till by thieves this month. |
| 783 | `private double salesTax` |  |
| 784 | `private double totalWageTax` |  |
| 785 | `private double totalBankTax` |  |
| 786 | `private double totalContributions` |  |
| 787 | `private double interest` |  |
| 788 | `private double utilityIncome` |  |
| 789 | `private double debt` |  |
| 790 | `private double GDP` |  |
| 791 | `private double yearGDP` |  |
| 849 | `private double totalEiPremiums` |  |
| 850 | `private double eiBenefits` |  |
| 851 | `private double studentGrants` |  |
| 854 | `private double totalHealthPremiums` | The month's health premium off every wage, struck in getTaxIncome() beside the EI premium (2026-09-19). |
| 867 | `private double studentLoanInterest` | The interest the graduates paid the treasury on their student loans this month (2026-09-21): a revenue line beside the premiums, set by Game off the household ledger the month it is struck. |
| 876 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO BUDGET LINES (0.7.0), set by Game where the central bank settles with the treasury at the top of the month: the remittance in, the interest on the advances out. |
| 909 | `private double healthcareBill, healthcareFees` |  |
| 918 | `private double educationBill, educationFees` |  |
| 931 | `private double safetyBill` | What the police and the prisons cost this month: payroll and upkeep, no fees - nobody pays to be policed or jailed. |
| 956 | `private double transitBill, transitFares` |  |
| 968 | `private double subsidiesPaid` | What the city paid this month to hold protected sectors at break-even. |
| 974 | `private double seniors` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 1373 | **type** `public class EconomyManager` | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. |

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
| 82 | 1 | `public void setTotalJobs(int jobs)` |  |
| 83 | 1 | `public void setPopulation(int pop)` |  |
| 84 | 1 | `public void setHouseholds(int houseCap)` |  |
| 85 | 1 | `public void setCash(int money)` |  |
| 86 | 1 | `public void setTotalWage(double wage)` |  |
| 87 | 3 | `public void setWageDetail(double[] staffedPerType)` |  |
| 90 | 1 | `public double[] getStaffedWagePerType()` |  |
| 91 | 1 | `public int getPopulation()` |  |
| 93 | 5 | `public void updateJobFillRate(double[] fill)` |  |
| 105 | 9 | `public void updateWages(double[] wagePerType, int[] bankPosts)` | The wage schedule, handed to every sector with its own posts. |
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

### HOUSING - what the landlords need from the family model and the land office (lines 260-357)

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
| 320 | 12 | `public double housingBuildHurdle(BuildingsTemplate t)` | What one of these has to earn a month before it is worth putting up. |
| 334 | 23 | `public void repriceHousingCosts()` | The cheapest way to house one more person, land included, per segment. |

### CASH BY NAME - the seam almost everything routes through (lines 358-371)

| line | len | member | says |
|---:|---:|---|---|
| 362 | 4 | `public double getSectorCash(String key)` |  |
| 367 | 4 | `public void setSectorCash(String key, double cash)` |  |

### THE MONTH, AT THE TOP (lines 372-412)

| line | len | member | says |
|---:|---:|---|---|
| 381 | 9 | `public void updateBusinessCredit(double governmentRate)` | Prices this month's business credit and hands every sector its interest bill BEFORE the statements run, so the figures they bank are net of it. |
| 396 | 7 | `public void refreshCreditAssets()` | Tells the credit manager what each sector is worth right now - its own sheet plus what it holds abroad, which a lender that ignored would call a rich sector broke. |
| 405 | 7 | `public void pushBalanceSheetInputs()` | Book values and outstanding debt, refreshed onto each set of books. |

### property tax (lines 413-462)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 4 | `public double getAssessedValue(Sector s)` | Land at today's price plus FINISHED buildings at replacement cost. |
| 434 | 3 | `public double getAssessedValue(BuildingType category)` | The same for a city-owned category, which the city does not tax but a screen may want to show. |
| 439 | 3 | `public double getPropertyTaxFor(Sector s)` | One month's property tax on a sector, at its own rate. |
| 448 | 10 | `public void chargePropertyTax()` | Hands each sector its property tax bill for the month. |
| 459 | 1 | `public double getTotalPropertyTax()` |  |
| 460 | 1 | `public void setTotalPropertyTax(double value)` |  |
| 461 | 1 | `public double getPropertyTaxCharged(String key)` |  |

### maintenance (lines 463-567)

| line | len | member | says |
|---:|---:|---|---|
| 488 | 6 | `public double maintenanceBillFor(Sector s, double materialPrice)` | The month's repair bill for one sector, in money - materials priced in. |
| 496 | 6 | `public double maintenanceBillFor(BuildingType category, double materialPrice)` | The same for a city-owned category. |
| 504 | 4 | `public double maintenanceMaterialsTotal()` | The material UNITS the whole city's repairs consume this month. |
| 510 | 4 | `public double maintenancePointsTotal()` | The builders' time the whole city's repairs consume this month. |
| 524 | 30 | `public void chargeMaintenance(double materialPrice)` | Hands every owner its repair bill for the month. |
| 556 | 1 | `public double getMaintenanceBillTotal()` | Every owner's repair bill added up - what the builders are owed. |
| 558 | 1 | `public double getCityMaintenanceBill()` | The share of it the treasury pays, because the city owns those buildings. |
| 560 | 1 | `public double getBankMaintenanceBill()` | The share the bank pays, for its branches. |
| 562 | 1 | `public double getMaintenanceMaterialsTotal()` | Material UNITS, not money - the yard has to find these. |
| 564 | 1 | `public double getMaintenancePointsTotal()` | Builders' time, which comes straight off what the city can put up. |
| 566 | 1 | `public double getMaintenanceCharge(String key)` | One sector's bill, for the screens. |

### the strike (lines 568-634)

| line | len | member | says |
|---:|---:|---|---|
| 575 | 7 | `public void strikeSectors()` | Strikes every sector's statement, settles the VAT from the same figures, and banks the month. |
| 592 | 26 | `public double settleSalesTax()` | The month's sales tax, STRUCK FROM THE TRADES. |
| 620 | 9 | `private double exemptSales(Sector s)` | The exempt part of a sector's local sales: what it sold of goods the tax never touches. |
| 631 | 1 | `public String getDeepestRefundSector()` | Whoever the city owes this month, or null. |
| 632 | 1 | `public double getSectorSalesTax(String key)` |  |
| 633 | 1 | `public double getSectorSalesTax(Sector s)` |  |

### INSOLVENCY, at the bottom of the investment step (lines 635-689)

| line | len | member | says |
|---:|---:|---|---|
| 651 | 22 | `public double settleInsolvency()` | End of the month: work out who is beyond saving and write their debt down to what their assets support. |
| 674 | 1 | `public double getOverdraftForgiven()` |  |
| 675 | 1 | `public double getOverdraftForgivenThisMonth(String key)` |  |
| 676 | 1 | `public double getOverdraftForgivenTotal(String key)` |  |
| 679 | 10 | `public void settleBusinessCredit(int month)` | Repay what matured, then borrow if that left the sector short. |

### WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS (lines 690-706)

| line | len | member | says |
|---:|---:|---|---|
| 696 | 1 | `public void clearDepositInterest()` |  |
| 698 | 4 | `public void recordDepositInterest(String sector, double amount)` |  |
| 703 | 3 | `public double getDepositInterestPaid(String sector)` |  |

### THE OWNERS AND THE MONEY ABROAD (lines 707-752)

| line | len | member | says |
|---:|---:|---|---|
| 712 | 1 | `public void setOutwardInvestment(OutwardInvestment outward)` |  |
| 713 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |
| 716 | 3 | `public double getForeignAssets(String sector)` | What one sector holds abroad, in the city's money at the rate it was last valued at. |
| 721 | 1 | `public void setEquity(Equity equity)` |  |
| 722 | 1 | `public Equity getEquity()` |  |
| 728 | 1 | `public void clearEquityFlows()` |  |
| 732 | 4 | `public void recordStolen(String sector, double amount)` |  |
| 736 | 1 | `public double getStolen(String sector)` |  |
| 737 | 4 | `public void recordSharesBoughtBack(String sector, double amount)` |  |
| 741 | 1 | `public double getSharesBoughtBack(String sector)` |  |
| 742 | 4 | `public void recordEquityRaised(String sector, double amount)` |  |
| 746 | 4 | `public void recordDividendPaid(String sector, double amount)` |  |
| 750 | 1 | `public double getEquityRaised(String sector)` |  |
| 751 | 1 | `public double getDividendsPaid(String sector)` |  |

### THE MONTH, IN THE MIDDLE AND AT THE BOTTOM (lines 753-778)

| line | len | member | says |
|---:|---:|---|---|
| 758 | 3 | `public void updateEcon()` | The economy's inputs for the month, set from the simulation. |
| 768 | 5 | `public void finalEconUpdate(Game game)` | The bottom of the month: every market clears, every maker produces. |
| 775 | 3 | `public void refreshEconPrices()` | What the load path needs of the bottom of the month, and nothing else: the draw the expense lines need. |

### THE CITY'S BOOKS (lines 779-846)

| line | len | member | says |
|---:|---:|---|---|
| 793 | 1 | `public void setBankTax(double amount)` |  |
| 794 | 1 | `public double getBankTax()` |  |
| 797 | 1 | `public double getProfitTax(Sector s)` | The profit tax one sector paid this month, as its statement carries it. |
| 800 | 5 | `public double getBusinessTax()` | Every sector's profit tax but the food industry's, plus the bank's - the "business tax" line. |
| 807 | 1 | `public double getIndustrialTax()` | The food industry's, on its own line, as the screens have always shown it. |
| 808 | 1 | `public double getHeavyIndustryTax()` |  |
| 809 | 1 | `public double getConstructionTax()` |  |
| 810 | 1 | `public double getSalesTax()` |  |
| 811 | 1 | `public double getWageTax()` |  |
| 812 | 1 | `public double getUtilityIncome()` |  |
| 815 | 3 | `public double wageTaxOnPayroll(double[] payrollPerType)` | The banded wage tax on one sector's payroll, asked of the policy rather than multiplied out in the UI. |
| 819 | 27 | `public double getTaxIncome()` |  |

### EI and the student grant (2026-09-11) (lines 847-906)

| line | len | member | says |
|---:|---:|---|---|
| 857 | 1 | `public double getHealthPremiums()` | What the health premium raised this month, employee side, off the whole wage bill. |
| 878 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 883 | 1 | `public double getCentralBankRemittance()` |  |
| 884 | 1 | `public double getCentralBankInterest()` |  |
| 887 | 1 | `public void setStudentLoanInterest(double interest)` | Sets the month's student-loan interest, the treasury's. |
| 890 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month. |
| 893 | 4 | `public void setOutsidePayments(double eiBenefits, double studentGrants)` | The month's EI bill and grant bill as the treasury paid them: set by Game where it pays them, at the top of the month (payEiBenefits() since 0.7.3, payStudentGrants() since 0.7.1), and on the load path. |
| 898 | 1 | `public double getEiPremiums()` |  |
| 899 | 1 | `public double getEiBenefits()` |  |
| 900 | 1 | `public double getStudentGrants()` |  |
| 903 | 3 | `public double getEiCoverage()` | What the premiums cover of the EI bill. |

### the services (lines 907-934)

| line | len | member | says |
|---:|---:|---|---|
| 910 | 4 | `public void setHealthcare(double grossCost, double fees)` |  |
| 914 | 1 | `public double getHealthcareBill()` |  |
| 915 | 1 | `public double getHealthcareFees()` |  |
| 916 | 1 | `public double getHealthcareNet()` |  |
| 919 | 4 | `public void setEducation(double grossCost, double fees)` |  |
| 923 | 1 | `public double getEducationBill()` |  |
| 924 | 1 | `public double getEducationFees()` |  |
| 925 | 1 | `public double getEducationNet()` |  |
| 932 | 1 | `public void setSafety(double grossCost)` |  |
| 933 | 1 | `public double getSafetyBill()` |  |

### THE TRANSIT BOOKS (2026-09-16) (lines 935-971)

| line | len | member | says |
|---:|---:|---|---|
| 957 | 4 | `public void setTransit(double grossCost, double fares)` |  |
| 961 | 1 | `public double getTransitBill()` |  |
| 962 | 1 | `public double getTransitFares()` |  |
| 965 | 1 | `public double getTransitNet()` | Negative when the fare more than covers the wages, which a player can arrange. |
| 969 | 1 | `public void setSubsidiesPaid(double v)` |  |
| 970 | 1 | `public double getSubsidiesPaid()` |  |

### pensions (lines 972-1001)

| line | len | member | says |
|---:|---:|---|---|
| 975 | 1 | `public void setSeniors(double seniors)` |  |
| 976 | 1 | `public double getSeniors()` |  |
| 977 | 1 | `public double getContributions()` |  |
| 978 | 1 | `public double getPensionsPaid()` |  |
| 979 | 1 | `public double getPensionShortfall()` |  |
| 980 | 5 | `public double getPensionCoverage()` |  |
| 987 | 4 | `public double getExpenses()` | What the city pays out this month. |
| 993 | 1 | `public double getInterestAccrued()` | The interest alone, which is the only part of getExpenses() that is CARRIED. |
| 994 | 1 | `public void setInterest(double value)` |  |
| 995 | 1 | `public void updateInterestExpense(double interest)` |  |
| 997 | 1 | `public double getTotalIncome()` |  |
| 999 | 1 | `public void setUtilityIncome(double income)` |  |
| 1000 | 1 | `public void setDebt(double debt)` |  |

### THE NATIONAL ACCOUNTS (lines 1002-1225)

| line | len | member | says |
|---:|---:|---|---|
| 1017 | 116 | `public void updateNationalAccounts(double constructionWorkDone, double governmentServices, double interest, double capitalSpend...` | Measures the month's output and the government's books, from the statements the sectors have just struck. |
| 1139 | 17 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid)` | Repopulates the government's revenue and expenditure block after a load, and nothing else - running the whole measure would be running a month of the economy with the calendar standing still. |
| 1157 | 1 | `public double getLastFoodVolume()` |  |
| 1169 | 17 | `public void restoreNationalAccounts(double[] a)` | A NEW SLOT RATHER THAN A CHANGED ONE, because slot 11 changed SCALE. |
| 1187 | 19 | `public double[] getNationalAccountsState()` |  |
| 1207 | 1 | `double[] governmentMonthToSave()` |  |
| 1208 | 1 | `void restoreGovernmentMonth(double[] m)` |  |
| 1210 | 1 | `public double getMonthGdp()` |  |
| 1211 | 1 | `public double getYearGdp()` |  |
| 1212 | 1 | `public double getGDP()` |  |
| 1213 | 1 | `public double getTaxRate()` |  |
| 1215 | 10 | `public void setPreviousGdp(HistorySave historySave)` |  |

### CONVENIENCES - the prices the screens and the harnesses ask for by name (lines 1226-1242)

| line | len | member | says |
|---:|---:|---|---|
| 1231 | 1 | `public double getFoodLocalPrice()` | What one person-month of food costs the shops at today's market prices. |
| 1232 | 1 | `public double getIronLocalPrice()` |  |
| 1235 | 7 | `public int getFoodUnitsHeld()` | Every warehouse and shelf of food in the city, in KILOGRAMS across the thirteen. |

### SAVE AND RESTORE (lines 1243-1275)

| line | len | member | says |
|---:|---:|---|---|
| 1247 | 1 | `public List<SectorState> getSectorStates()` |  |
| 1248 | 1 | `public void restoreSectorStates(List<SectorState> s)` |  |
| 1251 | 13 | `public void restoreSectorBills(List<SectorState> saved)` | The month's bills back over the rebuild's re-derivation, and the total they add to. |
| 1264 | 1 | `public List<Markets.State> getMarketStates()` |  |
| 1265 | 1 | `public void restoreMarketStates(List<Markets.State> s)` |  |
| 1267 | 1 | `public SalesTaxLedger.State getSalesTaxState()` |  |
| 1270 | 5 | `public boolean restoreSalesTaxState(SalesTaxLedger.State state)` | The month's VAT back, AND the total that came out of it - one fact. |

### PRINTERS, RESET, THE REFORM (lines 1276-1401)

| line | len | member | says |
|---:|---:|---|---|
| 1280 | 3 | `public void printWageTaxInfo()` |  |
| 1284 | 35 | `public void printCityStats()` |  |
| 1320 | 14 | `public void resetEconomyManager()` |  |
| 1337 | 4 | `static { ... }` |  |
| 1349 | 47 | `public void redenominate(double scale)` | Every figure the economy manager holds, and every sector and market under it, in the new unit. |
| 1397 | 4 | `private static void scaleArray(double[] values, double scale)` |  |

