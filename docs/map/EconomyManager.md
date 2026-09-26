# EconomyManager.java - 1,634 lines · 194 methods · 3 constants · model

`ham/citybuildersim/EconomyManager.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (50), [Good](Good.md) (11), [BuildingType](BuildingType.md) (11), [BuildingsTemplate](BuildingsTemplate.md) (10), [Markets](Markets.md) (5), [SalesTaxLedger](SalesTaxLedger.md) (5), [BuildingManager](BuildingManager.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Traffic](Traffic.md) (4), [SectorState](SectorState.md) (4), [Sectors](Sectors.md) (3), [TaxPolicy](TaxPolicy.md) (3), [NationalAccounts](NationalAccounts.md) (3), [RealEstate](RealEstate.md) (3), [Statement](Statement.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [BondMarket](BondMarket.md) (3), [Equity](Equity.md) (3), [FamilyModel](FamilyModel.md) (2), [GameLog](GameLog.md) (2), [Retail](Retail.md) (2), [GoodsMarket](GoodsMarket.md) (1), [JobType](JobType.md) (1), [InfrastructureManager](InfrastructureManager.md) (1), [Game](Game.md) (1), [SocialSecurity](SocialSecurity.md) (1), [LuxuryRetail](LuxuryRetail.md) (1), [HistorySave](HistorySave.md) (1)

**Used by (36):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HousingCheck](HousingCheck.md), [InvestCheck](InvestCheck.md), [LongPlaytest](LongPlaytest.md), [Materials](Materials.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [OutwardInvestment](OutwardInvestment.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md)

## Sections

| line | section |
|---:|---|
| 31 | THE CITY'S PARTS |
| 62 | WHAT THE CITY TELLS THE ECONOMY EACH MONTH |
| 131 | · utilities |
| 220 | · the world |
| 238 | · the land |
| 260 | HOUSING - what the landlords need from the family model and the land office |
| 371 | CASH BY NAME - the seam almost everything routes through |
| 385 | THE MONTH, AT THE TOP |
| 461 | · property tax |
| 511 | · maintenance |
| 616 | · the strike |
| 708 | INSOLVENCY, at the bottom of the investment step |
| 834 | WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS |
| 851 | THE OWNERS AND THE MONEY ABROAD |
| 964 | THE MONTH, IN THE MIDDLE AND AT THE BOTTOM |
| 990 | THE CITY'S BOOKS |
| 1058 | · EI and the student grant (2026-09-11) |
| 1118 | · the services |
| 1146 | THE TRANSIT BOOKS (2026-09-16) |
| 1183 | · pensions |
| 1213 | THE NATIONAL ACCOUNTS |
| 1459 | CONVENIENCES - the prices the screens and the harnesses ask for by name |
| 1476 | SAVE AND RESTORE |
| 1509 | PRINTERS, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 521 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, ...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 533 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 1568 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 463 | `private double totalPropertyTax` |  |
| 464 | `private final Map<String, Double> propertyTaxBySector` |  |
| 526 | `private double maintenanceBillTotal` |  |
| 527 | `private double maintenanceMaterialsTotal` |  |
| 528 | `private double maintenancePointsTotal` |  |
| 529 | `private double cityMaintenanceBill` |  |
| 530 | `private double bankMaintenanceBill` |  |
| 531 | `private final Map<String, Double> maintenanceBySector` |  |
| 712 | `private double overdraftForgivenThisMonth` |  |
| 713 | `private final Map<String, Double> overdraftForgivenBySector` |  |
| 714 | `private final Map<String, Double> overdraftForgivenThisMonthBySector` |  |
| 838 | `private final Map<String, Double> depositInterestPaid` |  |
| 855 | `private OutwardInvestment outward` |  |
| 922 | `private BondMarket bondMarket` | The bond market (0.7.12), wired by Game: its maturities are paid at the credit settle, and the other sectors' bonds a company holds are among its assets. |
| 931 | `private Equity equity` |  |
| 935 | `private final Map<String, Double> equityRaised` |  |
| 936 | `private final Map<String, Double> dividendsPaid` |  |
| 937 | `private final Map<String, Double> sharesBoughtBack` |  |
| 942 | `private final Map<String, Double> stolen` | Taken from each sector's till by thieves this month. |
| 994 | `private double salesTax` |  |
| 995 | `private double totalWageTax` |  |
| 996 | `private double totalBankTax` |  |
| 997 | `private double totalContributions` |  |
| 998 | `private double interest` |  |
| 999 | `private double utilityIncome` |  |
| 1000 | `private double debt` |  |
| 1001 | `private double GDP` |  |
| 1002 | `private double yearGDP` |  |
| 1060 | `private double totalEiPremiums` |  |
| 1061 | `private double eiBenefits` |  |
| 1062 | `private double studentGrants` |  |
| 1065 | `private double totalHealthPremiums` | The month's health premium off every wage, struck in getTaxIncome() beside the EI premium (2026-09-19). |
| 1078 | `private double studentLoanInterest` | The interest the graduates paid the treasury on their student loans this month (2026-09-21): a revenue line beside the premiums, set by Game off the household ledger the month it is struck. |
| 1087 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO BUDGET LINES (0.7.0), set by Game where the central bank settles with the treasury at the top of the month: the remittance in, the interest on the advances out. |
| 1120 | `private double healthcareBill, healthcareFees` |  |
| 1129 | `private double educationBill, educationFees` |  |
| 1142 | `private double safetyBill` | What the police and the prisons cost this month: payroll and upkeep, no fees - nobody pays to be policed or jailed. |
| 1167 | `private double transitBill, transitFares` |  |
| 1179 | `private double subsidiesPaid` | What the city paid this month to hold protected sectors at break-even. |
| 1185 | `private double seniors` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 1606 | **type** `public class EconomyManager` | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. |

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

### HOUSING - what the landlords need from the family model and the land office (lines 260-370)

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
| 334 | 11 | `public double housingCarry(BuildingsTemplate t)` | WHAT ONE OF THESE COSTS TO HOLD A MONTH, before any interest: its maintenance, RealEstate.MAINTENANCE_PER_YEAR on the structure, and its property tax, the property rate on the structure and its plot. |
| 347 | 23 | `public void repriceHousingCosts()` | The cheapest way to house one more person, land included, per segment. |

### CASH BY NAME - the seam almost everything routes through (lines 371-384)

| line | len | member | says |
|---:|---:|---|---|
| 375 | 4 | `public double getSectorCash(String key)` |  |
| 380 | 4 | `public void setSectorCash(String key, double cash)` |  |

### THE MONTH, AT THE TOP (lines 385-460)

| line | len | member | says |
|---:|---:|---|---|
| 398 | 3 | `public void updateBusinessCredit(double primeRate)` | Prices this month's business credit and hands every sector its interest bill BEFORE the statements run, so the figures they bank are net of it. |
| 407 | 30 | `public void updateBusinessCredit(double primeRate, double insuredMortgageRate)` | ...and the insured mortgage's rate beside prime (0.7.11, Bank.insuredMortgageRate()): what a landlord's new mortgage is written at and a term that ends renews at. |
| 443 | 8 | `public void refreshCreditAssets()` | Tells the credit manager what each sector is worth right now - its own sheet plus what it holds abroad, which a lender that ignored would call a rich sector broke. |
| 453 | 7 | `public void pushBalanceSheetInputs()` | Book values and outstanding debt, refreshed onto each set of books. |

### property tax (lines 461-510)

| line | len | member | says |
|---:|---:|---|---|
| 476 | 4 | `public double getAssessedValue(Sector s)` | Land at today's price plus FINISHED buildings at replacement cost. |
| 482 | 3 | `public double getAssessedValue(BuildingType category)` | The same for a city-owned category, which the city does not tax but a screen may want to show. |
| 487 | 3 | `public double getPropertyTaxFor(Sector s)` | One month's property tax on a sector, at its own rate. |
| 496 | 10 | `public void chargePropertyTax()` | Hands each sector its property tax bill for the month. |
| 507 | 1 | `public double getTotalPropertyTax()` |  |
| 508 | 1 | `public void setTotalPropertyTax(double value)` |  |
| 509 | 1 | `public double getPropertyTaxCharged(String key)` |  |

### maintenance (lines 511-615)

| line | len | member | says |
|---:|---:|---|---|
| 536 | 6 | `public double maintenanceBillFor(Sector s, double materialPrice)` | The month's repair bill for one sector, in money - materials priced in. |
| 544 | 6 | `public double maintenanceBillFor(BuildingType category, double materialPrice)` | The same for a city-owned category. |
| 552 | 4 | `public double maintenanceMaterialsTotal()` | The material UNITS the whole city's repairs consume this month. |
| 558 | 4 | `public double maintenancePointsTotal()` | The builders' time the whole city's repairs consume this month. |
| 572 | 30 | `public void chargeMaintenance(double materialPrice)` | Hands every owner its repair bill for the month. |
| 604 | 1 | `public double getMaintenanceBillTotal()` | Every owner's repair bill added up - what the builders are owed. |
| 606 | 1 | `public double getCityMaintenanceBill()` | The share of it the treasury pays, because the city owns those buildings. |
| 608 | 1 | `public double getBankMaintenanceBill()` | The share the bank pays, for its branches. |
| 610 | 1 | `public double getMaintenanceMaterialsTotal()` | Material UNITS, not money - the yard has to find these. |
| 612 | 1 | `public double getMaintenancePointsTotal()` | Builders' time, which comes straight off what the city can put up. |
| 614 | 1 | `public double getMaintenanceCharge(String key)` | One sector's bill, for the screens. |

### the strike (lines 616-707)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 7 | `public void strikeSectors()` | Strikes every sector's statement, settles the VAT from the same figures, and banks the month. |
| 640 | 26 | `public double settleSalesTax()` | The month's sales tax, STRUCK FROM THE TRADES. |
| 668 | 3 | `private double exemptSales(Sector s)` | The exempt part of a sector's local sales: what it sold of goods the tax never touches. |
| 673 | 9 | `private static double exemptOf(Sector s, double localSales)` | ...of these local sales: all of them for a sector making only exempt goods, none otherwise. |
| 692 | 10 | `private double salesTaxSoFar(Sector s, Sector.Ledger p)` | The sales tax a sector's month owes SO FAR, off its ledger as it stands (0.7.12 round 6): settleSalesTax()'s arithmetic for one sector - its taxable sales at its own rate, less the credit on what it bought at each sup... |
| 704 | 1 | `public String getDeepestRefundSector()` | Whoever the city owes this month, or null. |
| 705 | 1 | `public double getSectorSalesTax(String key)` |  |
| 706 | 1 | `public double getSectorSalesTax(Sector s)` |  |

### INSOLVENCY, at the bottom of the investment step (lines 708-833)

| line | len | member | says |
|---:|---:|---|---|
| 730 | 36 | `public double settleInsolvency()` | End of the month: work out who is beyond saving and write their debt down to what their assets support - since 0.7.8 each sector's slice of defaulted firms, since 0.7.12 round 4 the part that could not pay its month w... |
| 767 | 1 | `public double getOverdraftForgiven()` |  |
| 768 | 1 | `public double getOverdraftForgivenThisMonth(String key)` |  |
| 769 | 1 | `public double getOverdraftForgivenTotal(String key)` |  |
| 785 | 31 | `public void settleBusinessCredit(int month)` | Repay what matured - and since 0.7.11 the principal a mortgage's payment took - then borrow if that left the sector short. |
| 824 | 9 | `double monthObligations(Sector s, double matured)` | What the month asked a sector to pay (round 4): the costs on this month's statement - the goods it paid for this month, its payroll, utilities and repairs - its interest and its taxes, and the principal that fell due,... |

### WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS (lines 834-850)

| line | len | member | says |
|---:|---:|---|---|
| 840 | 1 | `public void clearDepositInterest()` |  |
| 842 | 4 | `public void recordDepositInterest(String sector, double amount)` |  |
| 847 | 3 | `public double getDepositInterestPaid(String sector)` |  |

### THE OWNERS AND THE MONEY ABROAD (lines 851-963)

| line | len | member | says |
|---:|---:|---|---|
| 856 | 1 | `public void setOutwardInvestment(OutwardInvestment outward)` |  |
| 857 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |
| 892 | 23 | `public double purchaseBudget(Sector s, double dividend, int month)` | WHAT A SECTOR CAN PAY FOR AS THE MARKETS CLEAR (0.7.12 round 6) - see Sector, BUY ONLY WHAT IT CAN PAY FOR. |
| 917 | 3 | `public double getForeignAssets(String sector)` | What one sector holds abroad, in the city's money at the rate it was last valued at. |
| 923 | 1 | `public void setBondMarket(BondMarket market)` |  |
| 924 | 1 | `public BondMarket getBondMarket()` |  |
| 927 | 3 | `public double getBondAssets(String sector)` | What one sector holds of the other sectors' bonds, at face - an asset beside its cash and what it holds abroad (0.7.12). |
| 932 | 1 | `public void setEquity(Equity equity)` |  |
| 933 | 1 | `public Equity getEquity()` |  |
| 939 | 1 | `public void clearEquityFlows()` |  |
| 943 | 4 | `public void recordStolen(String sector, double amount)` |  |
| 947 | 1 | `public double getStolen(String sector)` |  |
| 948 | 4 | `public void recordSharesBoughtBack(String sector, double amount)` |  |
| 952 | 1 | `public double getSharesBoughtBack(String sector)` |  |
| 953 | 4 | `public void recordEquityRaised(String sector, double amount)` |  |
| 957 | 4 | `public void recordDividendPaid(String sector, double amount)` |  |
| 961 | 1 | `public double getEquityRaised(String sector)` |  |
| 962 | 1 | `public double getDividendsPaid(String sector)` |  |

### THE MONTH, IN THE MIDDLE AND AT THE BOTTOM (lines 964-989)

| line | len | member | says |
|---:|---:|---|---|
| 969 | 3 | `public void updateEcon()` | The economy's inputs for the month, set from the simulation. |
| 979 | 5 | `public void finalEconUpdate(Game game)` | The bottom of the month: every market clears, every maker produces. |
| 986 | 3 | `public void refreshEconPrices()` | What the load path needs of the bottom of the month, and nothing else: the draw the expense lines need. |

### THE CITY'S BOOKS (lines 990-1057)

| line | len | member | says |
|---:|---:|---|---|
| 1004 | 1 | `public void setBankTax(double amount)` |  |
| 1005 | 1 | `public double getBankTax()` |  |
| 1008 | 1 | `public double getProfitTax(Sector s)` | The profit tax one sector paid this month, as its statement carries it. |
| 1011 | 5 | `public double getBusinessTax()` | Every sector's profit tax but the food industry's, plus the bank's - the "business tax" line. |
| 1018 | 1 | `public double getIndustrialTax()` | The food industry's, on its own line, as the screens have always shown it. |
| 1019 | 1 | `public double getHeavyIndustryTax()` |  |
| 1020 | 1 | `public double getConstructionTax()` |  |
| 1021 | 1 | `public double getSalesTax()` |  |
| 1022 | 1 | `public double getWageTax()` |  |
| 1023 | 1 | `public double getUtilityIncome()` |  |
| 1026 | 3 | `public double wageTaxOnPayroll(double[] payrollPerType)` | The banded wage tax on one sector's payroll, asked of the policy rather than multiplied out in the UI. |
| 1030 | 27 | `public double getTaxIncome()` |  |

### EI and the student grant (2026-09-11) (lines 1058-1117)

| line | len | member | says |
|---:|---:|---|---|
| 1068 | 1 | `public double getHealthPremiums()` | What the health premium raised this month, employee side, off the whole wage bill. |
| 1089 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 1094 | 1 | `public double getCentralBankRemittance()` |  |
| 1095 | 1 | `public double getCentralBankInterest()` |  |
| 1098 | 1 | `public void setStudentLoanInterest(double interest)` | Sets the month's student-loan interest, the treasury's. |
| 1101 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month. |
| 1104 | 4 | `public void setOutsidePayments(double eiBenefits, double studentGrants)` | The month's EI bill and grant bill as the treasury paid them: set by Game where it pays them, at the top of the month (payEiBenefits() since 0.7.3, payStudentGrants() since 0.7.1), and on the load path. |
| 1109 | 1 | `public double getEiPremiums()` |  |
| 1110 | 1 | `public double getEiBenefits()` |  |
| 1111 | 1 | `public double getStudentGrants()` |  |
| 1114 | 3 | `public double getEiCoverage()` | What the premiums cover of the EI bill. |

### the services (lines 1118-1145)

| line | len | member | says |
|---:|---:|---|---|
| 1121 | 4 | `public void setHealthcare(double grossCost, double fees)` |  |
| 1125 | 1 | `public double getHealthcareBill()` |  |
| 1126 | 1 | `public double getHealthcareFees()` |  |
| 1127 | 1 | `public double getHealthcareNet()` |  |
| 1130 | 4 | `public void setEducation(double grossCost, double fees)` |  |
| 1134 | 1 | `public double getEducationBill()` |  |
| 1135 | 1 | `public double getEducationFees()` |  |
| 1136 | 1 | `public double getEducationNet()` |  |
| 1143 | 1 | `public void setSafety(double grossCost)` |  |
| 1144 | 1 | `public double getSafetyBill()` |  |

### THE TRANSIT BOOKS (2026-09-16) (lines 1146-1182)

| line | len | member | says |
|---:|---:|---|---|
| 1168 | 4 | `public void setTransit(double grossCost, double fares)` |  |
| 1172 | 1 | `public double getTransitBill()` |  |
| 1173 | 1 | `public double getTransitFares()` |  |
| 1176 | 1 | `public double getTransitNet()` | Negative when the fare more than covers the wages, which a player can arrange. |
| 1180 | 1 | `public void setSubsidiesPaid(double v)` |  |
| 1181 | 1 | `public double getSubsidiesPaid()` |  |

### pensions (lines 1183-1212)

| line | len | member | says |
|---:|---:|---|---|
| 1186 | 1 | `public void setSeniors(double seniors)` |  |
| 1187 | 1 | `public double getSeniors()` |  |
| 1188 | 1 | `public double getContributions()` |  |
| 1189 | 1 | `public double getPensionsPaid()` |  |
| 1190 | 1 | `public double getPensionShortfall()` |  |
| 1191 | 5 | `public double getPensionCoverage()` |  |
| 1198 | 4 | `public double getExpenses()` | What the city pays out this month. |
| 1204 | 1 | `public double getInterestAccrued()` | The interest alone, which is the only part of getExpenses() that is CARRIED. |
| 1205 | 1 | `public void setInterest(double value)` |  |
| 1206 | 1 | `public void updateInterestExpense(double interest)` |  |
| 1208 | 1 | `public double getTotalIncome()` |  |
| 1210 | 1 | `public void setUtilityIncome(double income)` |  |
| 1211 | 1 | `public void setDebt(double debt)` |  |

### THE NATIONAL ACCOUNTS (lines 1213-1458)

| line | len | member | says |
|---:|---:|---|---|
| 1228 | 122 | `public void updateNationalAccounts(double constructionWorkDone, double governmentServices, double interest, double capitalSpend...` | Measures the month's output and the government's books, from the statements the sectors have just struck. |
| 1356 | 18 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid)` | Repopulates the government's revenue and expenditure block after a load, and nothing else - running the whole measure would be running a month of the economy with the calendar standing still. |
| 1384 | 4 | `private void setMortgageInsuranceLines()` | THE MORTGAGE INSURANCE'S TWO BUDGET LINES (0.7.11), off the lender's month: the premiums on the mortgages written, which the treasury took as each was written, and the claims - what the month's write-downs took off in... |
| 1389 | 1 | `public double getLastFoodVolume()` |  |
| 1401 | 17 | `public void restoreNationalAccounts(double[] a)` | A NEW SLOT RATHER THAN A CHANGED ONE, because slot 11 changed SCALE. |
| 1419 | 19 | `public double[] getNationalAccountsState()` |  |
| 1439 | 1 | `double[] governmentMonthToSave()` |  |
| 1440 | 1 | `void restoreGovernmentMonth(double[] m)` |  |
| 1442 | 1 | `public double getMonthGdp()` |  |
| 1443 | 1 | `public double getYearGdp()` |  |
| 1444 | 1 | `public double getGDP()` |  |
| 1446 | 1 | `public double getTaxRate()` | The income rate - the three income taxes together, TaxPolicy.getIncomeTaxRate(): the profit rate once they have parted (0.7.4). |
| 1448 | 10 | `public void setPreviousGdp(HistorySave historySave)` |  |

### CONVENIENCES - the prices the screens and the harnesses ask for by name (lines 1459-1475)

| line | len | member | says |
|---:|---:|---|---|
| 1464 | 1 | `public double getFoodLocalPrice()` | What one person-month of food costs the shops at today's market prices. |
| 1465 | 1 | `public double getIronLocalPrice()` |  |
| 1468 | 7 | `public int getFoodUnitsHeld()` | Every warehouse and shelf of food in the city, in KILOGRAMS across the thirteen. |

### SAVE AND RESTORE (lines 1476-1508)

| line | len | member | says |
|---:|---:|---|---|
| 1480 | 1 | `public List<SectorState> getSectorStates()` |  |
| 1481 | 1 | `public void restoreSectorStates(List<SectorState> s)` |  |
| 1484 | 13 | `public void restoreSectorBills(List<SectorState> saved)` | The month's bills back over the rebuild's re-derivation, and the total they add to. |
| 1497 | 1 | `public List<Markets.State> getMarketStates()` |  |
| 1498 | 1 | `public void restoreMarketStates(List<Markets.State> s)` |  |
| 1500 | 1 | `public SalesTaxLedger.State getSalesTaxState()` |  |
| 1503 | 5 | `public boolean restoreSalesTaxState(SalesTaxLedger.State state)` | The month's VAT back, AND the total that came out of it - one fact. |

### PRINTERS, RESET, THE REFORM (lines 1509-1634)

| line | len | member | says |
|---:|---:|---|---|
| 1513 | 3 | `public void printWageTaxInfo()` |  |
| 1517 | 35 | `public void printCityStats()` |  |
| 1553 | 14 | `public void resetEconomyManager()` |  |
| 1570 | 4 | `static { ... }` |  |
| 1582 | 47 | `public void redenominate(double scale)` | Every figure the economy manager holds, and every sector and market under it, in the new unit. |
| 1630 | 4 | `private static void scaleArray(double[] values, double scale)` |  |

