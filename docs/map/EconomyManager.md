# EconomyManager.java - 1,411 lines · 184 methods · 3 constants · model

`ham/citybuildersim/EconomyManager.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (41), [Good](Good.md) (11), [BuildingType](BuildingType.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (9), [Markets](Markets.md) (5), [SalesTaxLedger](SalesTaxLedger.md) (5), [BuildingManager](BuildingManager.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Traffic](Traffic.md) (4), [SectorState](SectorState.md) (4), [Sectors](Sectors.md) (3), [TaxPolicy](TaxPolicy.md) (3), [NationalAccounts](NationalAccounts.md) (3), [RealEstate](RealEstate.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [Equity](Equity.md) (3), [FamilyModel](FamilyModel.md) (2), [Retail](Retail.md) (2), [GoodsMarket](GoodsMarket.md) (1), [JobType](JobType.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [Statement](Statement.md) (1), [GameLog](GameLog.md) (1), [Game](Game.md) (1), [SocialSecurity](SocialSecurity.md) (1), [HistorySave](HistorySave.md) (1)

**Used by (33):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [BankCheck](BankCheck.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [Materials](Materials.md), [MoneyAudit](MoneyAudit.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [OutwardInvestment](OutwardInvestment.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

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
| 417 | · property tax |
| 467 | · maintenance |
| 572 | · the strike |
| 639 | INSOLVENCY, at the bottom of the investment step |
| 699 | WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS |
| 716 | THE OWNERS AND THE MONEY ABROAD |
| 762 | THE MONTH, IN THE MIDDLE AND AT THE BOTTOM |
| 788 | THE CITY'S BOOKS |
| 856 | · EI and the student grant (2026-09-11) |
| 916 | · the services |
| 944 | THE TRANSIT BOOKS (2026-09-16) |
| 981 | · pensions |
| 1011 | THE NATIONAL ACCOUNTS |
| 1236 | CONVENIENCES - the prices the screens and the harnesses ask for by name |
| 1253 | SAVE AND RESTORE |
| 1286 | PRINTERS, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 477 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, ...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 489 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 1345 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 419 | `private double totalPropertyTax` |  |
| 420 | `private final Map<String, Double> propertyTaxBySector` |  |
| 482 | `private double maintenanceBillTotal` |  |
| 483 | `private double maintenanceMaterialsTotal` |  |
| 484 | `private double maintenancePointsTotal` |  |
| 485 | `private double cityMaintenanceBill` |  |
| 486 | `private double bankMaintenanceBill` |  |
| 487 | `private final Map<String, Double> maintenanceBySector` |  |
| 643 | `private double overdraftForgivenThisMonth` |  |
| 644 | `private final Map<String, Double> overdraftForgivenBySector` |  |
| 645 | `private final Map<String, Double> overdraftForgivenThisMonthBySector` |  |
| 703 | `private final Map<String, Double> depositInterestPaid` |  |
| 720 | `private OutwardInvestment outward` |  |
| 729 | `private Equity equity` |  |
| 733 | `private final Map<String, Double> equityRaised` |  |
| 734 | `private final Map<String, Double> dividendsPaid` |  |
| 735 | `private final Map<String, Double> sharesBoughtBack` |  |
| 740 | `private final Map<String, Double> stolen` | Taken from each sector's till by thieves this month. |
| 792 | `private double salesTax` |  |
| 793 | `private double totalWageTax` |  |
| 794 | `private double totalBankTax` |  |
| 795 | `private double totalContributions` |  |
| 796 | `private double interest` |  |
| 797 | `private double utilityIncome` |  |
| 798 | `private double debt` |  |
| 799 | `private double GDP` |  |
| 800 | `private double yearGDP` |  |
| 858 | `private double totalEiPremiums` |  |
| 859 | `private double eiBenefits` |  |
| 860 | `private double studentGrants` |  |
| 863 | `private double totalHealthPremiums` | The month's health premium off every wage, struck in getTaxIncome() beside the EI premium (2026-09-19). |
| 876 | `private double studentLoanInterest` | The interest the graduates paid the treasury on their student loans this month (2026-09-21): a revenue line beside the premiums, set by Game off the household ledger the month it is struck. |
| 885 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO BUDGET LINES (0.7.0), set by Game where the central bank settles with the treasury at the top of the month: the remittance in, the interest on the advances out. |
| 918 | `private double healthcareBill, healthcareFees` |  |
| 927 | `private double educationBill, educationFees` |  |
| 940 | `private double safetyBill` | What the police and the prisons cost this month: payroll and upkeep, no fees - nobody pays to be policed or jailed. |
| 965 | `private double transitBill, transitFares` |  |
| 977 | `private double subsidiesPaid` | What the city paid this month to hold protected sectors at break-even. |
| 983 | `private double seniors` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 1383 | **type** `public class EconomyManager` | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. |

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

### THE MONTH, AT THE TOP (lines 372-416)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 9 | `public void updateBusinessCredit(double primeRate)` | Prices this month's business credit and hands every sector its interest bill BEFORE the statements run, so the figures they bank are net of it. |
| 400 | 7 | `public void refreshCreditAssets()` | Tells the credit manager what each sector is worth right now - its own sheet plus what it holds abroad, which a lender that ignored would call a rich sector broke. |
| 409 | 7 | `public void pushBalanceSheetInputs()` | Book values and outstanding debt, refreshed onto each set of books. |

### property tax (lines 417-466)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 4 | `public double getAssessedValue(Sector s)` | Land at today's price plus FINISHED buildings at replacement cost. |
| 438 | 3 | `public double getAssessedValue(BuildingType category)` | The same for a city-owned category, which the city does not tax but a screen may want to show. |
| 443 | 3 | `public double getPropertyTaxFor(Sector s)` | One month's property tax on a sector, at its own rate. |
| 452 | 10 | `public void chargePropertyTax()` | Hands each sector its property tax bill for the month. |
| 463 | 1 | `public double getTotalPropertyTax()` |  |
| 464 | 1 | `public void setTotalPropertyTax(double value)` |  |
| 465 | 1 | `public double getPropertyTaxCharged(String key)` |  |

### maintenance (lines 467-571)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 6 | `public double maintenanceBillFor(Sector s, double materialPrice)` | The month's repair bill for one sector, in money - materials priced in. |
| 500 | 6 | `public double maintenanceBillFor(BuildingType category, double materialPrice)` | The same for a city-owned category. |
| 508 | 4 | `public double maintenanceMaterialsTotal()` | The material UNITS the whole city's repairs consume this month. |
| 514 | 4 | `public double maintenancePointsTotal()` | The builders' time the whole city's repairs consume this month. |
| 528 | 30 | `public void chargeMaintenance(double materialPrice)` | Hands every owner its repair bill for the month. |
| 560 | 1 | `public double getMaintenanceBillTotal()` | Every owner's repair bill added up - what the builders are owed. |
| 562 | 1 | `public double getCityMaintenanceBill()` | The share of it the treasury pays, because the city owns those buildings. |
| 564 | 1 | `public double getBankMaintenanceBill()` | The share the bank pays, for its branches. |
| 566 | 1 | `public double getMaintenanceMaterialsTotal()` | Material UNITS, not money - the yard has to find these. |
| 568 | 1 | `public double getMaintenancePointsTotal()` | Builders' time, which comes straight off what the city can put up. |
| 570 | 1 | `public double getMaintenanceCharge(String key)` | One sector's bill, for the screens. |

### the strike (lines 572-638)

| line | len | member | says |
|---:|---:|---|---|
| 579 | 7 | `public void strikeSectors()` | Strikes every sector's statement, settles the VAT from the same figures, and banks the month. |
| 596 | 26 | `public double settleSalesTax()` | The month's sales tax, STRUCK FROM THE TRADES. |
| 624 | 9 | `private double exemptSales(Sector s)` | The exempt part of a sector's local sales: what it sold of goods the tax never touches. |
| 635 | 1 | `public String getDeepestRefundSector()` | Whoever the city owes this month, or null. |
| 636 | 1 | `public double getSectorSalesTax(String key)` |  |
| 637 | 1 | `public double getSectorSalesTax(Sector s)` |  |

### INSOLVENCY, at the bottom of the investment step (lines 639-698)

| line | len | member | says |
|---:|---:|---|---|
| 658 | 22 | `public double settleInsolvency()` | End of the month: work out who is beyond saving and write their debt down to what their assets support - since 0.7.8 each sector's slice of defaulted firms, and the whole-sector restructure only for a sector with noth... |
| 681 | 1 | `public double getOverdraftForgiven()` |  |
| 682 | 1 | `public double getOverdraftForgivenThisMonth(String key)` |  |
| 683 | 1 | `public double getOverdraftForgivenTotal(String key)` |  |
| 686 | 12 | `public void settleBusinessCredit(int month)` | Repay what matured, then borrow if that left the sector short. |

### WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS (lines 699-715)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 1 | `public void clearDepositInterest()` |  |
| 707 | 4 | `public void recordDepositInterest(String sector, double amount)` |  |
| 712 | 3 | `public double getDepositInterestPaid(String sector)` |  |

### THE OWNERS AND THE MONEY ABROAD (lines 716-761)

| line | len | member | says |
|---:|---:|---|---|
| 721 | 1 | `public void setOutwardInvestment(OutwardInvestment outward)` |  |
| 722 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |
| 725 | 3 | `public double getForeignAssets(String sector)` | What one sector holds abroad, in the city's money at the rate it was last valued at. |
| 730 | 1 | `public void setEquity(Equity equity)` |  |
| 731 | 1 | `public Equity getEquity()` |  |
| 737 | 1 | `public void clearEquityFlows()` |  |
| 741 | 4 | `public void recordStolen(String sector, double amount)` |  |
| 745 | 1 | `public double getStolen(String sector)` |  |
| 746 | 4 | `public void recordSharesBoughtBack(String sector, double amount)` |  |
| 750 | 1 | `public double getSharesBoughtBack(String sector)` |  |
| 751 | 4 | `public void recordEquityRaised(String sector, double amount)` |  |
| 755 | 4 | `public void recordDividendPaid(String sector, double amount)` |  |
| 759 | 1 | `public double getEquityRaised(String sector)` |  |
| 760 | 1 | `public double getDividendsPaid(String sector)` |  |

### THE MONTH, IN THE MIDDLE AND AT THE BOTTOM (lines 762-787)

| line | len | member | says |
|---:|---:|---|---|
| 767 | 3 | `public void updateEcon()` | The economy's inputs for the month, set from the simulation. |
| 777 | 5 | `public void finalEconUpdate(Game game)` | The bottom of the month: every market clears, every maker produces. |
| 784 | 3 | `public void refreshEconPrices()` | What the load path needs of the bottom of the month, and nothing else: the draw the expense lines need. |

### THE CITY'S BOOKS (lines 788-855)

| line | len | member | says |
|---:|---:|---|---|
| 802 | 1 | `public void setBankTax(double amount)` |  |
| 803 | 1 | `public double getBankTax()` |  |
| 806 | 1 | `public double getProfitTax(Sector s)` | The profit tax one sector paid this month, as its statement carries it. |
| 809 | 5 | `public double getBusinessTax()` | Every sector's profit tax but the food industry's, plus the bank's - the "business tax" line. |
| 816 | 1 | `public double getIndustrialTax()` | The food industry's, on its own line, as the screens have always shown it. |
| 817 | 1 | `public double getHeavyIndustryTax()` |  |
| 818 | 1 | `public double getConstructionTax()` |  |
| 819 | 1 | `public double getSalesTax()` |  |
| 820 | 1 | `public double getWageTax()` |  |
| 821 | 1 | `public double getUtilityIncome()` |  |
| 824 | 3 | `public double wageTaxOnPayroll(double[] payrollPerType)` | The banded wage tax on one sector's payroll, asked of the policy rather than multiplied out in the UI. |
| 828 | 27 | `public double getTaxIncome()` |  |

### EI and the student grant (2026-09-11) (lines 856-915)

| line | len | member | says |
|---:|---:|---|---|
| 866 | 1 | `public double getHealthPremiums()` | What the health premium raised this month, employee side, off the whole wage bill. |
| 887 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 892 | 1 | `public double getCentralBankRemittance()` |  |
| 893 | 1 | `public double getCentralBankInterest()` |  |
| 896 | 1 | `public void setStudentLoanInterest(double interest)` | Sets the month's student-loan interest, the treasury's. |
| 899 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month. |
| 902 | 4 | `public void setOutsidePayments(double eiBenefits, double studentGrants)` | The month's EI bill and grant bill as the treasury paid them: set by Game where it pays them, at the top of the month (payEiBenefits() since 0.7.3, payStudentGrants() since 0.7.1), and on the load path. |
| 907 | 1 | `public double getEiPremiums()` |  |
| 908 | 1 | `public double getEiBenefits()` |  |
| 909 | 1 | `public double getStudentGrants()` |  |
| 912 | 3 | `public double getEiCoverage()` | What the premiums cover of the EI bill. |

### the services (lines 916-943)

| line | len | member | says |
|---:|---:|---|---|
| 919 | 4 | `public void setHealthcare(double grossCost, double fees)` |  |
| 923 | 1 | `public double getHealthcareBill()` |  |
| 924 | 1 | `public double getHealthcareFees()` |  |
| 925 | 1 | `public double getHealthcareNet()` |  |
| 928 | 4 | `public void setEducation(double grossCost, double fees)` |  |
| 932 | 1 | `public double getEducationBill()` |  |
| 933 | 1 | `public double getEducationFees()` |  |
| 934 | 1 | `public double getEducationNet()` |  |
| 941 | 1 | `public void setSafety(double grossCost)` |  |
| 942 | 1 | `public double getSafetyBill()` |  |

### THE TRANSIT BOOKS (2026-09-16) (lines 944-980)

| line | len | member | says |
|---:|---:|---|---|
| 966 | 4 | `public void setTransit(double grossCost, double fares)` |  |
| 970 | 1 | `public double getTransitBill()` |  |
| 971 | 1 | `public double getTransitFares()` |  |
| 974 | 1 | `public double getTransitNet()` | Negative when the fare more than covers the wages, which a player can arrange. |
| 978 | 1 | `public void setSubsidiesPaid(double v)` |  |
| 979 | 1 | `public double getSubsidiesPaid()` |  |

### pensions (lines 981-1010)

| line | len | member | says |
|---:|---:|---|---|
| 984 | 1 | `public void setSeniors(double seniors)` |  |
| 985 | 1 | `public double getSeniors()` |  |
| 986 | 1 | `public double getContributions()` |  |
| 987 | 1 | `public double getPensionsPaid()` |  |
| 988 | 1 | `public double getPensionShortfall()` |  |
| 989 | 5 | `public double getPensionCoverage()` |  |
| 996 | 4 | `public double getExpenses()` | What the city pays out this month. |
| 1002 | 1 | `public double getInterestAccrued()` | The interest alone, which is the only part of getExpenses() that is CARRIED. |
| 1003 | 1 | `public void setInterest(double value)` |  |
| 1004 | 1 | `public void updateInterestExpense(double interest)` |  |
| 1006 | 1 | `public double getTotalIncome()` |  |
| 1008 | 1 | `public void setUtilityIncome(double income)` |  |
| 1009 | 1 | `public void setDebt(double debt)` |  |

### THE NATIONAL ACCOUNTS (lines 1011-1235)

| line | len | member | says |
|---:|---:|---|---|
| 1026 | 116 | `public void updateNationalAccounts(double constructionWorkDone, double governmentServices, double interest, double capitalSpend...` | Measures the month's output and the government's books, from the statements the sectors have just struck. |
| 1148 | 17 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid)` | Repopulates the government's revenue and expenditure block after a load, and nothing else - running the whole measure would be running a month of the economy with the calendar standing still. |
| 1166 | 1 | `public double getLastFoodVolume()` |  |
| 1178 | 17 | `public void restoreNationalAccounts(double[] a)` | A NEW SLOT RATHER THAN A CHANGED ONE, because slot 11 changed SCALE. |
| 1196 | 19 | `public double[] getNationalAccountsState()` |  |
| 1216 | 1 | `double[] governmentMonthToSave()` |  |
| 1217 | 1 | `void restoreGovernmentMonth(double[] m)` |  |
| 1219 | 1 | `public double getMonthGdp()` |  |
| 1220 | 1 | `public double getYearGdp()` |  |
| 1221 | 1 | `public double getGDP()` |  |
| 1223 | 1 | `public double getTaxRate()` | The income rate - the three income taxes together, TaxPolicy.getIncomeTaxRate(): the profit rate once they have parted (0.7.4). |
| 1225 | 10 | `public void setPreviousGdp(HistorySave historySave)` |  |

### CONVENIENCES - the prices the screens and the harnesses ask for by name (lines 1236-1252)

| line | len | member | says |
|---:|---:|---|---|
| 1241 | 1 | `public double getFoodLocalPrice()` | What one person-month of food costs the shops at today's market prices. |
| 1242 | 1 | `public double getIronLocalPrice()` |  |
| 1245 | 7 | `public int getFoodUnitsHeld()` | Every warehouse and shelf of food in the city, in KILOGRAMS across the thirteen. |

### SAVE AND RESTORE (lines 1253-1285)

| line | len | member | says |
|---:|---:|---|---|
| 1257 | 1 | `public List<SectorState> getSectorStates()` |  |
| 1258 | 1 | `public void restoreSectorStates(List<SectorState> s)` |  |
| 1261 | 13 | `public void restoreSectorBills(List<SectorState> saved)` | The month's bills back over the rebuild's re-derivation, and the total they add to. |
| 1274 | 1 | `public List<Markets.State> getMarketStates()` |  |
| 1275 | 1 | `public void restoreMarketStates(List<Markets.State> s)` |  |
| 1277 | 1 | `public SalesTaxLedger.State getSalesTaxState()` |  |
| 1280 | 5 | `public boolean restoreSalesTaxState(SalesTaxLedger.State state)` | The month's VAT back, AND the total that came out of it - one fact. |

### PRINTERS, RESET, THE REFORM (lines 1286-1411)

| line | len | member | says |
|---:|---:|---|---|
| 1290 | 3 | `public void printWageTaxInfo()` |  |
| 1294 | 35 | `public void printCityStats()` |  |
| 1330 | 14 | `public void resetEconomyManager()` |  |
| 1347 | 4 | `static { ... }` |  |
| 1359 | 47 | `public void redenominate(double scale)` | Every figure the economy manager holds, and every sector and market under it, in the new unit. |
| 1407 | 4 | `private static void scaleArray(double[] values, double scale)` |  |

