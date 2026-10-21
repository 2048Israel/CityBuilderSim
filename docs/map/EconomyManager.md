# EconomyManager.java - 2,429 lines · 245 methods · 8 constants · model

`ham/citybuildersim/EconomyManager.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [Sector](Sector.md) (63), [BuildingsTemplate](BuildingsTemplate.md) (21), [Good](Good.md) (16), [BuildingType](BuildingType.md) (12), [NationalAccounts](NationalAccounts.md) (10), [TaxPolicy](TaxPolicy.md) (9), [Markets](Markets.md) (5), [SalesTaxLedger](SalesTaxLedger.md) (5), [BuildingManager](BuildingManager.md) (4), [BusinessDebtManager](BusinessDebtManager.md) (4), [Traffic](Traffic.md) (4), [Zoning](Zoning.md) (4), [SectorState](SectorState.md) (4), [Sectors](Sectors.md) (3), [JobType](JobType.md) (3), [RealEstate](RealEstate.md) (3), [Statement](Statement.md) (3), [OutwardInvestment](OutwardInvestment.md) (3), [Retail](Retail.md) (3), [BondMarket](BondMarket.md) (3), [Equity](Equity.md) (3), [WageBand](WageBand.md) (3), [GoodsMarket](GoodsMarket.md) (2), [FamilyModel](FamilyModel.md) (2), [SupplierCredit](SupplierCredit.md) (2), [GameLog](GameLog.md) (2), [SocialSecurity](SocialSecurity.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [BalanceSheet](BalanceSheet.md) (1), [Game](Game.md) (1)... and 3 more

**Used by (53):** [Agriculture](Agriculture.md), [AgricultureCheck](AgricultureCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BankCheck](BankCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BuildAdvice](BuildAdvice.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CityNeeds](CityNeeds.md), [ConservationCheck](ConservationCheck.md), [CreditCheck](CreditCheck.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HousingCheck](HousingCheck.md), [InfrastructureScreen](InfrastructureScreen.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Materials](Materials.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Offending](Offending.md), [OilCheck](OilCheck.md), [OutsideCheck](OutsideCheck.md), [OutwardInvestment](OutwardInvestment.md), [PeopleScreen](PeopleScreen.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [RailDepotCheck](RailDepotCheck.md), [ReadPathCheck](ReadPathCheck.md), [RoadCheck](RoadCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [SectorBooks](SectorBooks.md), [SectorStatementCheck](SectorStatementCheck.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [SupplierCreditCheck](SupplierCreditCheck.md), [TreasuryCheck](TreasuryCheck.md), [ZoningCheck](ZoningCheck.md), [ZoningScreen](ZoningScreen.md), [ZoningView](ZoningView.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 31 | THE CITY'S PARTS |
| 62 | WHAT THE CITY TELLS THE ECONOMY EACH MONTH |
| 146 | · utilities |
| 246 | · the world |
| 264 | · the land |
| 326 | HOUSING - what the landlords need from the family model and the land office |
| 447 | CASH BY NAME - the seam almost everything routes through |
| 461 | THE MONTH, AT THE TOP |
| 549 | · the statements' splits (0.7.74, R1 and R2) |
| 596 | · property tax |
| 647 | · maintenance |
| 748 | · the builders' sales tax |
| 822 | THE REBATES ON A NEW HOME, AND THE CITY'S (0.7.19, revised) |
| 1038 | · the strike |
| 1198 | INSOLVENCY, at the bottom of the investment step |
| 1328 | WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS |
| 1345 | THE OWNERS AND THE MONEY ABROAD |
| 1487 | THE MONTH, IN THE MIDDLE AND AT THE BOTTOM |
| 1513 | THE CITY'S BOOKS |
| 1633 | · WHAT THE POLICY TAB ASKS (0.7.36) |
| 1700 | · EI and the student grant (2026-09-11) |
| 1792 | · the services |
| 1820 | THE TRANSIT BOOKS (2026-09-16) |
| 1873 | · pensions |
| 1905 | THE NATIONAL ACCOUNTS |
| 2238 | CONVENIENCES - the prices the screens and the harnesses ask for by name |
| 2255 | SAVE AND RESTORE |
| 2288 | PRINTERS, RESET, THE REFORM |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 658 | `EconomyManager.CITY_MAINTAINED` | `{ BuildingType.ELECTRICITY, BuildingType.WATER, BuildingType.INFRASTRUCTURE, ...` | EVERY BUILDING IN THE CITY, BILLED FOR STANDING THERE. |
| 672 | `EconomyManager.MAINTENANCE_RATE` | `ham.citybuildersim.sectors.RealEstate.MAINTENANCE_PER_YEAR / 12` |  |
| 881 | `EconomyManager.PBRH_MIN_UNITS` | `4` | A building with this many dwellings or more is purpose-built rental housing (CRA: "at least 4 residential units each with a private kitchen, a private bathroom, and a private living area"). |
| 883 | `EconomyManager.NRRP_SHARE` | `.36` | The new residential rental property rebate's share of the tax on a unit: 36% (Excise Tax Act section 256.2(3)(a); CRA RC4231). |
| 885 | `EconomyManager.NRRP_CAP` | `6.3` | ...and its most a unit, in founding thousands: $6,300. |
| 887 | `EconomyManager.NRRP_FULL_BELOW` | `350` | ...in full for a unit worth up to this, in founding thousands: $350,000. |
| 889 | `EconomyManager.NRRP_NONE_FROM` | `450` | ...and none for a unit worth this or more: $450,000. |
| 2348 | `EconomyManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 254 | `private double exchangeRate` | Every world price in the game is quoted by the world in ITS money and converted at this rate - times the world's own price level, which is how world inflation rides in. |
| 271 | `private double landPricePerSqFt` | What the city currently charges for a square foot, so assessed values track the price the player sets. |
| 295 | `private Zoning zoning` | The city's zones, and what each holds of the free ground (LandManager.heldBy()); Game wires both, a bare economy has none. |
| 296 | `private java.util.function.ToDoubleFunction<String> zoneHeld` |  |
| 330 | `private double householdCount` |  |
| 331 | `private double marginalHousingCost` |  |
| 332 | `private double occupiedHomes` |  |
| 333 | `private double studioSeekers, familySeekers, studioSeekerHeads, familySeekerHeads` |  |
| 564 | `private final Map<String, double[]> interestByKind` |  |
| 565 | `private final Map<String, double[][]> debtByKind` |  |
| 568 | `private int creditMonth` | The month of the last credit settle, which the bonds' maturities are read against (settleBusinessCredit()). |
| 587 | `private final Map<String, double[][]> debtMovedAtSheet` | ...AND, SINCE 0.7.75, WHAT HAD MOVED IT AND WHAT THE SHEET WAS PRICED AT (the sector statements' R7 and R6), read at the same push: the running totals of its principal borrowed, repaid and written off by kind (Busines... |
| 588 | `private final Map<String, double[]> valuedAt` |  |
| 598 | `private double totalPropertyTax` |  |
| 599 | `private final Map<String, Double> propertyTaxBySector` |  |
| 665 | `private double maintenanceBillTotal` |  |
| 666 | `private double maintenanceMaterialsTotal` |  |
| 667 | `private double maintenancePointsTotal` |  |
| 668 | `private double cityMaintenanceBill` |  |
| 669 | `private double bankMaintenanceBill` |  |
| 670 | `private final Map<String, Double> maintenanceBySector` |  |
| 708 | `private java.util.function.DoubleSupplier streetUtilisation` | GRAVEL'S REPAIRS RISE WITH ITS TRAFFIC (FR7 2.2, Jerus's star 5, 2026-10-18): a Gravel Road is charged at the share of BuildingsTemplate.gravelRepairsAt() at the streets' utilisation as it stands (InfrastructureManage... |
| 892 | `private java.util.function.DoubleSupplier foundingToToday` | Today's money for a founding dollar: the month's price index over the unit. |
| 1202 | `private double overdraftForgivenThisMonth` |  |
| 1203 | `private final Map<String, Double> overdraftForgivenBySector` |  |
| 1204 | `private final Map<String, Double> overdraftForgivenThisMonthBySector` |  |
| 1332 | `private final Map<String, Double> depositInterestPaid` |  |
| 1349 | `private OutwardInvestment outward` |  |
| 1440 | `private BondMarket bondMarket` | The bond market (0.7.12), wired by Game: its maturities are paid at the credit settle, and the other sectors' bonds a company holds are among its assets. |
| 1454 | `private Equity equity` |  |
| 1458 | `private final Map<String, Double> equityRaised` |  |
| 1459 | `private final Map<String, Double> dividendsPaid` |  |
| 1460 | `private final Map<String, Double> sharesBoughtBack` |  |
| 1465 | `private final Map<String, Double> stolen` | Taken from each sector's till by thieves this month. |
| 1517 | `private double salesTax` |  |
| 1518 | `private double totalWageTax` |  |
| 1519 | `private double totalBankTax` |  |
| 1520 | `private double totalContributions` |  |
| 1521 | `private double interest` |  |
| 1522 | `private double utilityIncome` |  |
| 1523 | `private double debt` |  |
| 1524 | `private double GDP` |  |
| 1525 | `private double yearGDP` |  |
| 1702 | `private double totalEiPremiums` |  |
| 1703 | `private double eiBenefits` |  |
| 1704 | `private double studentGrants` |  |
| 1707 | `private double totalHealthPremiums` | The month's health premium off every wage, struck in getTaxIncome() beside the EI premium (2026-09-19). |
| 1720 | `private double studentLoanInterest` | The interest the graduates paid the treasury on their student loans this month (2026-09-21): a revenue line beside the premiums, set by Game off the household ledger the month it is struck. |
| 1729 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO BUDGET LINES (0.7.0), set by Game where the central bank settles with the treasury at the top of the month: the remittance in, the interest on the advances out. |
| 1740 | `private double fundTransfer` | THE TRANSFER FROM THE CITY'S FUND (0.7.14), set by Game where the treasury settles, for the central bank's lines' reason: Game moves the cash there. |
| 1746 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43), set by Game where the treasury pays it, after the month's sale - for the central bank's lines' reason: Game moves the cash there. |
| 1760 | `private double cityRepairsPaid, arrearsPaidDown, reserveCrudeBought, reserveCrudeSold` | THE FOUR LINES 0.7.102 TOOK INSIDE THE BUDGET'S TOTALS (A15), set by Game where the treasury pays or takes them, for the central bank's lines' reason: the repairs to the city's own buildings as paid (chargeBuildingMai... |
| 1794 | `private double healthcareBill, healthcareFees` |  |
| 1803 | `private double educationBill, educationFees` |  |
| 1816 | `private double safetyBill` | What the police and the prisons cost this month: payroll and upkeep, no fees - nobody pays to be policed or jailed. |
| 1854 | `private double transitBill, transitFares` |  |
| 1869 | `private double subsidiesPaid` | What the city paid this month to hold protected sectors at break-even. |
| 1875 | `private double seniors` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 2401 | **type** `public class EconomyManager` | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. |

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

### the world (lines 246-263)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 5 | `public void setExchangeRate(double rate)` |  |
| 262 | 1 | `public double getExchangeRate()` |  |

### the land (lines 264-325)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 1 | `public void setLandPricePerSqFt(double price)` |  |
| 274 | 1 | `public double getLandPricePerSqFt()` |  |
| 277 | 3 | `public double landValueOf(Sector s)` | What a sector's land is worth at the city's current price - on its books; the roll reads assessedLandValueOf() since 0.7.107. |
| 298 | 4 | `public void setZoning(Zoning zoning, java.util.function.ToDoubleFunction<String> held)` |  |
| 304 | 7 | `public double assessedLandValueOf(Sector s)` | A sector's land as the roll values it, before the share the roll carries: its zone's used ground at the zone's price, the rest at the going price. |
| 313 | 7 | `public double groundPriceFor(BuildingsTemplate t)` | What a square foot of ground under one more of these costs its owner and is assessed at: its zone's price while the zone holds its plot, the going price past it - never below nothing. |
| 322 | 3 | `public double landValueOf(BuildingType category)` | What a city-owned category's land is worth. |

### HOUSING - what the landlords need from the family model and the land office (lines 326-446)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 1 | `public void setHouseholdCount(double count)` |  |
| 336 | 1 | `public void setMarginalHousingCost(double perCap)` |  |
| 337 | 1 | `public double getMarginalHousingCost()` |  |
| 338 | 1 | `public void setOccupiedHomes(double occupied)` |  |
| 339 | 6 | `public void setHousingSeekers(double studio, double family, double studioHeads, double familyHeads)` |  |
| 345 | 1 | `public double getStudioSeekers()` |  |
| 346 | 1 | `public double getFamilySeekers()` |  |
| 354 | 21 | `public void updateHousing()` | Hands the landlords and the shops what the month looks like: how many people there are, how many doors, who is chasing them and what a new home costs. |
| 388 | 3 | `public double housingBuildHurdle(BuildingsTemplate t)` | What one of these has to earn a month before it is worth putting up. |
| 400 | 18 | `public double housingCarry(BuildingsTemplate t)` | WHAT ONE OF THESE COSTS TO HOLD A MONTH, before any interest: its maintenance, RealEstate.MAINTENANCE_PER_YEAR on the structure, and its property tax, the property rate on the structure and its plot. |
| 420 | 26 | `public void repriceHousingCosts()` | The cheapest way to house one more person, land included, per segment. |

### CASH BY NAME - the seam almost everything routes through (lines 447-460)

| line | len | member | says |
|---:|---:|---|---|
| 451 | 4 | `public double getSectorCash(String key)` |  |
| 456 | 4 | `public void setSectorCash(String key, double cash)` |  |

### THE MONTH, AT THE TOP (lines 461-548)

| line | len | member | says |
|---:|---:|---|---|
| 474 | 3 | `public void updateBusinessCredit(double primeRate)` | Prices this month's business credit and hands every sector its interest bill BEFORE the statements run, so the figures they bank are net of it. |
| 483 | 32 | `public void updateBusinessCredit(double primeRate, double insuredMortgageRate)` | ...and the insured mortgage's rate beside prime (0.7.11, Bank.insuredMortgageRate()): what a landlord's new mortgage is written at and a term that ends renews at. |
| 524 | 9 | `public void refreshCreditAssets()` | Tells the credit manager what each sector is worth right now - its own sheet plus what it holds abroad, which a lender that ignored would call a rich sector broke. |
| 535 | 13 | `public void pushBalanceSheetInputs()` | Book values and outstanding debt, refreshed onto each set of books. |

### the statements' splits (0.7.74, R1 and R2) (lines 549-595)

| line | len | member | says |
|---:|---:|---|---|
| 571 | 1 | `public double[] getInterestByKind(String key)` | One sector's interest bill this month by kind (BusinessDebtManager.DEBT_KINDS), or null before one was handed. |
| 574 | 1 | `public double[][] getDebtByKind(String key)` | One sector's debt by kind as its sheet was last pushed - {owed, within a year, within five} and since 0.7.75 its rate and when the last of it falls due (BusinessDebtManager.OWED to RUNS_TO) - or null before one was. |
| 591 | 1 | `public double[][] getDebtMovedAtSheet(String key)` | What had moved one sector's debt so far when its sheet was last pushed (R7): {borrowed, repaid, written off} by kind, or null before one was. |
| 594 | 1 | `public double[] getValuedAt(String key)` | ...and its land and buildings as the sheet priced them (R6): {land, sq ft; its price a sq ft; the materials in its buildings, units; their price a unit}, or null. |

### property tax (lines 596-646)

| line | len | member | says |
|---:|---:|---|---|
| 611 | 5 | `public double getAssessedValue(Sector s)` | Land at today's price plus FINISHED buildings at replacement cost. |
| 618 | 3 | `public double getAssessedValue(BuildingType category)` | The same for a city-owned category, which the city does not tax but a screen may want to show. |
| 623 | 3 | `public double getPropertyTaxFor(Sector s)` | One month's property tax on a sector, at its own rate. |
| 632 | 10 | `public void chargePropertyTax()` | Hands each sector its property tax bill for the month. |
| 643 | 1 | `public double getTotalPropertyTax()` |  |
| 644 | 1 | `public void setTotalPropertyTax(double value)` |  |
| 645 | 1 | `public double getPropertyTaxCharged(String key)` |  |

### maintenance (lines 647-747)

| line | len | member | says |
|---:|---:|---|---|
| 711 | 3 | `public void setStreetUtilisation(java.util.function.DoubleSupplier utilisation)` | Where the streets' utilisation the road repairs are charged at is read from (FR7 2.2): Game's InfrastructureManager, live. |
| 716 | 4 | `private double streetUtilisation()` | The streets' utilisation as the repairs read it: 0 when it is not a number. |
| 722 | 7 | `private double repaired(java.util.function.ToDoubleFunction<java.util.function.ToDoubleFunction<BuildingsTemplate>> total, java...` | A month's repairs of `cost` over the buildings `total` sums: the house rate on those without a share of their own, and each with one at its own over twelve. |
| 731 | 6 | `public double maintenanceBillFor(Sector s, double materialPrice)` | The month's repair bill for one sector, in money - materials priced in, and the builders' sales tax. |
| 739 | 8 | `public double maintenanceBillFor(BuildingType category, double materialPrice)` | The same for a city-owned category - since 0.7.103 each road kind at its own share. |

### the builders' sales tax (lines 748-821)

| line | len | member | says |
|---:|---:|---|---|
| 751 | 3 | `public double buildersSalesRate()` | What the builders charge their sales tax at. |
| 761 | 3 | `public double withBuildersTax(double beforeTax)` | A price before the builders' sales tax, with it passed on (0.7.19): the builders remit their rate on what they bill, so what they bill for a price p that is theirs to keep is p / (1 - rate). |
| 782 | 5 | `public boolean claimsTaxOnBuildings(String owner, BuildingsTemplate t)` | WHETHER AN OWNER CLAIMS THE TAX ON ITS BUILDINGS BACK (0.7.19). |
| 789 | 4 | `private static boolean makesTaxableSupplies(Sector s)` | A sector with a good the sales tax touches. |
| 802 | 7 | `public double ownersBuildCost(BuildingsTemplate t, String owner, double beforeTax)` | What a building's price before the builders' tax costs this owner in the end: the price, and whatever of the tax on it the owner does not get back - none of it for a business that claims it, none for a landlord's purp... |
| 818 | 3 | `public double ownersRepairCost(BuildingsTemplate t, String owner, double beforeTax)` | ...and a REPAIR bill's (0.7.19, revised): the bill itself when the owner claims the tax on it back as an input, the bill with the tax when it cannot. |

### THE REBATES ON A NEW HOME, AND THE CITY'S (0.7.19, revised) (lines 822-1037)

| line | len | member | says |
|---:|---:|---|---|
| 893 | 1 | `public void setFoundingToToday(java.util.function.DoubleSupplier s)` |  |
| 896 | 3 | `public boolean isLandlordsHome(String owner, BuildingsTemplate t)` | Whether this building is one of the landlords' homes. |
| 901 | 3 | `public static boolean isPurposeBuiltRental(BuildingsTemplate t)` | Whether a home of this template is purpose-built rental housing. |
| 911 | 13 | `public double rentalPropertyRebate(BuildingsTemplate t, double taxPerBuilding)` | The new residential rental property rebate on one building of this template, on this much tax paid on it, in today's money: A x ($450,000 - B) / $100,000 a unit (THE REBATES ON A NEW HOME). |
| 936 | 7 | `public double taxRecoveredShare(String owner, BuildingsTemplate t, double taxPerBuilding)` | The share of the builders' tax on a new building that its owner gets back (0.7.19, revised): all of it for a business that claims it as a credit (claimsTaxOnBuildings()) and for a landlord's purpose-built rental; the ... |
| 945 | 4 | `public double maintenanceMaterialsTotal()` | The material UNITS the whole city's repairs consume this month - each road kind at its own share (0.7.103). |
| 951 | 4 | `public double maintenancePointsTotal()` | The builders' time the whole city's repairs consume this month - each road kind at its own share (0.7.103). |
| 965 | 31 | `public void chargeMaintenance(double materialPrice)` | Hands every owner its repair bill for the month. |
| 998 | 1 | `public double getMaintenanceBillTotal()` | Every owner's repair bill added up - what the builders are owed. |
| 1000 | 1 | `public double getCityMaintenanceBill()` | The share of it the treasury pays, because the city owns those buildings. |
| 1002 | 1 | `public double getBankMaintenanceBill()` | The share the bank pays, for its branches. |
| 1018 | 5 | `public double bankOperatingCost(int branches)` | ...AND WHAT THESE BRANCHES COST TO RUN (0.7.19): the template's upkeep, a branch's rent, systems and supplies, which was charged to nobody until then. |
| 1025 | 6 | `public double bankOperatingCostPerLaterBranch()` | ...what one branch past the charter carries of it: the template's upkeep. |
| 1032 | 1 | `public double getMaintenanceMaterialsTotal()` | Material UNITS, not money - the yard has to find these. |
| 1034 | 1 | `public double getMaintenancePointsTotal()` | Builders' time, which comes straight off what the city can put up. |
| 1036 | 1 | `public double getMaintenanceCharge(String key)` | One sector's bill, for the screens. |

### the strike (lines 1038-1197)

| line | len | member | says |
|---:|---:|---|---|
| 1046 | 8 | `public void strikeSectors()` | Strikes every sector's statement, settles the VAT from the same figures, and banks the month. |
| 1068 | 16 | `void settleSupplierCredit()` | THE SUPPLIERS' CREDIT, SETTLED AT THE STRIKE (0.7.44; SupplierCredit). |
| 1094 | 54 | `public double settleSalesTax()` | The month's sales tax, STRUCK FROM THE TRADES. |
| 1150 | 3 | `private double exemptSales(Sector s)` | The exempt part of a sector's local sales: what it sold of goods the tax never touches. |
| 1155 | 9 | `private static double exemptOf(Sector s, double localSales)` | ...of these local sales: all of them for a sector making only exempt goods, none otherwise. |
| 1174 | 18 | `private double salesTaxSoFar(Sector s, Sector.Ledger p)` | The sales tax a sector's month owes SO FAR, off its ledger as it stands (0.7.12 round 6): settleSalesTax()'s arithmetic for one sector - its taxable sales at its own rate, less the credit on what it bought at each sup... |
| 1194 | 1 | `public String getDeepestRefundSector()` | Whoever the city owes this month, or null. |
| 1195 | 1 | `public double getSectorSalesTax(String key)` |  |
| 1196 | 1 | `public double getSectorSalesTax(Sector s)` |  |

### INSOLVENCY, at the bottom of the investment step (lines 1198-1327)

| line | len | member | says |
|---:|---:|---|---|
| 1220 | 36 | `public double settleInsolvency()` | End of the month: work out who is beyond saving and write their debt down to what their assets support - since 0.7.8 each sector's slice of defaulted firms, since 0.7.12 round 4 the part that could not pay its month w... |
| 1257 | 1 | `public double getOverdraftForgiven()` |  |
| 1258 | 1 | `public double getOverdraftForgivenThisMonth(String key)` |  |
| 1259 | 1 | `public double getOverdraftForgivenTotal(String key)` |  |
| 1275 | 32 | `public void settleBusinessCredit(int month)` | Repay what matured - and since 0.7.11 the principal a mortgage's payment took - then borrow if that left the sector short. |
| 1315 | 12 | `double monthObligations(Sector s, double matured)` | What the month asked a sector to pay (round 4): the costs on this month's statement - the goods it paid for this month, its payroll, utilities and repairs - its interest and its taxes, and the principal that fell due,... |

### WHAT THE BANK PAID THE SECTORS FOR THE MONEY IN THEIR TILLS (lines 1328-1344)

| line | len | member | says |
|---:|---:|---|---|
| 1334 | 1 | `public void clearDepositInterest()` |  |
| 1336 | 4 | `public void recordDepositInterest(String sector, double amount)` |  |
| 1341 | 3 | `public double getDepositInterestPaid(String sector)` |  |

### THE OWNERS AND THE MONEY ABROAD (lines 1345-1486)

| line | len | member | says |
|---:|---:|---|---|
| 1350 | 1 | `public void setOutwardInvestment(OutwardInvestment outward)` |  |
| 1351 | 1 | `public OutwardInvestment getOutwardInvestment()` |  |
| 1392 | 36 | `public double purchaseBudget(Sector s, double dividend, int month)` | WHAT A SECTOR CAN PAY FOR AS THE MARKETS CLEAR (0.7.12 round 6) - see Sector, BUY ONLY WHAT IT CAN PAY FOR. |
| 1430 | 3 | `private static double supplierCreditLimit(Sector s)` | What a buyer's suppliers will wait for this clearing: the grocers' (Retail.supplierCreditLimit()); nothing for anybody else. |
| 1435 | 3 | `public double getForeignAssets(String sector)` | What one sector holds abroad, in the city's money at the rate it was last valued at. |
| 1441 | 1 | `public void setBondMarket(BondMarket market)` |  |
| 1442 | 1 | `public BondMarket getBondMarket()` |  |
| 1449 | 4 | `public double getBondAssets(String sector)` | What one sector holds of the other sectors' bonds, at face - an asset beside its cash and what it holds abroad (0.7.12) - and, since CD2 (slice 4), of the city's own paper, at face: the books carry both as bonds it ho... |
| 1455 | 1 | `public void setEquity(Equity equity)` |  |
| 1456 | 1 | `public Equity getEquity()` |  |
| 1462 | 1 | `public void clearEquityFlows()` |  |
| 1466 | 4 | `public void recordStolen(String sector, double amount)` |  |
| 1470 | 1 | `public double getStolen(String sector)` |  |
| 1471 | 4 | `public void recordSharesBoughtBack(String sector, double amount)` |  |
| 1475 | 1 | `public double getSharesBoughtBack(String sector)` |  |
| 1476 | 4 | `public void recordEquityRaised(String sector, double amount)` |  |
| 1480 | 4 | `public void recordDividendPaid(String sector, double amount)` |  |
| 1484 | 1 | `public double getEquityRaised(String sector)` |  |
| 1485 | 1 | `public double getDividendsPaid(String sector)` |  |

### THE MONTH, IN THE MIDDLE AND AT THE BOTTOM (lines 1487-1512)

| line | len | member | says |
|---:|---:|---|---|
| 1492 | 3 | `public void updateEcon()` | The economy's inputs for the month, set from the simulation. |
| 1502 | 5 | `public void finalEconUpdate(Game game)` | The bottom of the month: every market clears, every maker produces. |
| 1509 | 3 | `public void refreshEconPrices()` | What the load path needs of the bottom of the month, and nothing else: the draw the expense lines need. |

### THE CITY'S BOOKS (lines 1513-1632)

| line | len | member | says |
|---:|---:|---|---|
| 1527 | 1 | `public void setBankTax(double amount)` |  |
| 1528 | 1 | `public double getBankTax()` |  |
| 1531 | 1 | `public double getProfitTax(Sector s)` | The profit tax one sector paid this month, as its statement carries it. |
| 1534 | 5 | `public double getBusinessTax()` | Every sector's profit tax but the food industry's, plus the bank's - the "business tax" line. |
| 1541 | 1 | `public double getIndustrialTax()` | The food industry's, on its own line, as the screens have always shown it. |
| 1542 | 1 | `public double getHeavyIndustryTax()` |  |
| 1543 | 1 | `public double getConstructionTax()` |  |
| 1544 | 1 | `public double getSalesTax()` |  |
| 1545 | 1 | `public double getWageTax()` |  |
| 1546 | 1 | `public double getUtilityIncome()` |  |
| 1549 | 3 | `public double wageTaxOnPayroll(double[] payrollPerType)` | The banded wage tax on one sector's payroll, asked of the policy rather than multiplied out in the UI. |
| 1553 | 1 | `public double getTaxIncome()` |  |
| 1564 | 1 | `public double getTaxIncomeNow()` | The same take at today's rates, WRITING NOTHING (0.7.31, the Government spec's B8): what Game.getIncome() - the header's EARNED - reads every time the header draws. |
| 1567 | 37 | `private double taxTake(boolean strike)` | The tax take, summed; struck into the month's fields only when `strike`. |
| 1612 | 4 | `public double getFuelDuty()` | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7): what the drivers paid the treasury a litre on their petrol this month, struck at the pump (sectors.Retail.sellFuel(), at 6d) and read from the forecourts' month - saved a... |
| 1627 | 5 | `public double getPortDues()` | THE PORT DUES (FR3, docs/queue/FR3.md 1 and 3; D9): what Freight's Ports division paid the city this month on the tonnes that crossed its berths (sectors.PortDivision.getDuesPaid()), struck in Freight.haul() before th... |

### WHAT THE POLICY TAB ASKS (0.7.36) (lines 1633-1699)

| line | len | member | says |
|---:|---:|---|---|
| 1647 | 3 | `public double profitTaxUnder(Sector s, TaxPolicy p)` | One sector's profit tax under a policy (M2): its last struck pre-tax income at that policy's rate, nothing on a loss - Sector.strike()'s rule. |
| 1660 | 6 | `public double salesTaxUnder(Sector s, TaxPolicy p)` | One sector's sales tax under a policy (M2): its net remittance scaled by the ratio of the two rates, or its taxable sales at the rate when it was charged nothing. |
| 1668 | 5 | `public double propertyTaxUnder(Sector s, TaxPolicy p)` | One sector's property tax under a policy (M2): its land at today's price on that policy's roll share - its zone's ground at the zone's since 0.7.107 - and its finished buildings, at that policy's monthly rate - getPro... |
| 1675 | 3 | `public double wageTaxUnder(WageBand band, TaxPolicy p)` | One wage band's wage tax under a policy (M2): every job type in it, staffed, at that policy's rate for the band - TaxPolicy.wageTaxPerTier()'s rule. |
| 1680 | 7 | `public double payrollIn(WageBand band)` | The staffed payroll one wage band carries this month: what its wage tax is struck on. |
| 1689 | 1 | `public double getWageBill()` | The wage bill the pension contribution and both premiums are struck on (M3): the month's total wage, as getTaxIncome() reads it. |
| 1692 | 1 | `public double premiumAt(double rate)` | A premium at this rate off that bill (M3): the EI and health premiums' own rule. |
| 1695 | 1 | `public double contributionsAt(double rate)` | Pension contributions at this rate off that bill (M3): SocialSecurity's own rule. |
| 1698 | 1 | `public double pensionsPaidUnder(TaxPolicy p)` | What the pensions would come to under a policy (M4): today's seniors at its pension. |

### EI and the student grant (2026-09-11) (lines 1700-1791)

| line | len | member | says |
|---:|---:|---|---|
| 1710 | 1 | `public double getHealthPremiums()` | What the health premium raised this month, employee side, off the whole wage bill. |
| 1731 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 1736 | 1 | `public double getCentralBankRemittance()` |  |
| 1737 | 1 | `public double getCentralBankInterest()` |  |
| 1742 | 1 | `public void setFundTransfer(double transfer)` |  |
| 1743 | 1 | `public double getFundTransfer()` |  |
| 1748 | 1 | `public void setFoodAssistance(double paid)` |  |
| 1749 | 1 | `public double getFoodAssistance()` |  |
| 1762 | 1 | `public void setCityRepairsPaid(double paid)` |  |
| 1763 | 1 | `public void setArrearsPaidDown(double paid)` |  |
| 1764 | 4 | `public void setReserveCrude(double bought, double sold)` |  |
| 1768 | 1 | `public double getCityRepairsPaid()` |  |
| 1769 | 1 | `public double getArrearsPaidDown()` |  |
| 1772 | 1 | `public void setStudentLoanInterest(double interest)` | Sets the month's student-loan interest, the treasury's. |
| 1775 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month. |
| 1778 | 4 | `public void setOutsidePayments(double eiBenefits, double studentGrants)` | The month's EI bill and grant bill as the treasury paid them: set by Game where it pays them, at the top of the month (payEiBenefits() since 0.7.3, payStudentGrants() since 0.7.1), and on the load path. |
| 1783 | 1 | `public double getEiPremiums()` |  |
| 1784 | 1 | `public double getEiBenefits()` |  |
| 1785 | 1 | `public double getStudentGrants()` |  |
| 1788 | 3 | `public double getEiCoverage()` | What the premiums cover of the EI bill. |

### the services (lines 1792-1819)

| line | len | member | says |
|---:|---:|---|---|
| 1795 | 4 | `public void setHealthcare(double grossCost, double fees)` |  |
| 1799 | 1 | `public double getHealthcareBill()` |  |
| 1800 | 1 | `public double getHealthcareFees()` |  |
| 1801 | 1 | `public double getHealthcareNet()` |  |
| 1804 | 4 | `public void setEducation(double grossCost, double fees)` |  |
| 1808 | 1 | `public double getEducationBill()` |  |
| 1809 | 1 | `public double getEducationFees()` |  |
| 1810 | 1 | `public double getEducationNet()` |  |
| 1817 | 1 | `public void setSafety(double grossCost)` |  |
| 1818 | 1 | `public double getSafetyBill()` |  |

### THE TRANSIT BOOKS (2026-09-16) (lines 1820-1872)

| line | len | member | says |
|---:|---:|---|---|
| 1855 | 4 | `public void setTransit(double grossCost, double fares)` |  |
| 1859 | 1 | `public double getTransitBill()` |  |
| 1860 | 1 | `public double getTransitFares()` |  |
| 1863 | 1 | `public double getTransitNet()` | Negative when the fare more than covers the wages, which a player can arrange. |
| 1866 | 1 | `public double transitNetAt(double fares)` | ...with another month of fares against this month's bill (0.7.38): the fare dial card's net, its fares InfrastructureManager.faresAt(). |
| 1870 | 1 | `public void setSubsidiesPaid(double v)` |  |
| 1871 | 1 | `public double getSubsidiesPaid()` |  |

### pensions (lines 1873-1904)

| line | len | member | says |
|---:|---:|---|---|
| 1876 | 1 | `public void setSeniors(double seniors)` |  |
| 1877 | 1 | `public double getSeniors()` |  |
| 1878 | 1 | `public double getContributions()` |  |
| 1879 | 1 | `public double getPensionsPaid()` |  |
| 1880 | 1 | `public double getPensionShortfall()` |  |
| 1881 | 5 | `public double getPensionCoverage()` |  |
| 1888 | 6 | `public double getExpenses()` | What the city pays out this month. |
| 1896 | 1 | `public double getInterestAccrued()` | The interest alone, which is the only part of getExpenses() that is CARRIED. |
| 1897 | 1 | `public void setInterest(double value)` |  |
| 1898 | 1 | `public void updateInterestExpense(double interest)` |  |
| 1900 | 1 | `public double getTotalIncome()` |  |
| 1902 | 1 | `public void setUtilityIncome(double income)` |  |
| 1903 | 1 | `public void setDebt(double debt)` |  |

### THE NATIONAL ACCOUNTS (lines 1905-2237)

| line | len | member | says |
|---:|---:|---|---|
| 1920 | 171 | `public void updateNationalAccounts(double constructionWorkDone, double governmentServices, double interest, double capitalSpend...` | Measures the month's output and the government's books, from the statements the sectors have just struck. |
| 2099 | 7 | `static double heldPrice(GoodsMarket m)` | What one unit of a held good is counted at (0.7.58): what one costs to bring in this month (GoodsMarket.landedPrice()) - the local price while somebody in the city has it on offer, the import price while nobody does -... |
| 2112 | 25 | `public void refreshGovernmentAccounts(double landSales, double capitalSpending, double landPurchases, double interestPaid)` | Repopulates the government's revenue and expenditure block after a load, and nothing else - running the whole measure would be running a month of the economy with the calendar standing still. |
| 2147 | 4 | `private void setMortgageInsuranceLines()` | THE MORTGAGE INSURANCE'S TWO BUDGET LINES (0.7.11), off the lender's month: the premiums on the mortgages written, which the treasury took as each was written, and the claims - what the month's write-downs took off in... |
| 2152 | 1 | `public double getLastFoodVolume()` |  |
| 2164 | 28 | `public void restoreNationalAccounts(double[] a)` | A NEW SLOT RATHER THAN A CHANGED ONE, because slot 11 changed SCALE. |
| 2193 | 24 | `public double[] getNationalAccountsState()` |  |
| 2218 | 1 | `double[] governmentMonthToSave()` |  |
| 2219 | 1 | `void restoreGovernmentMonth(double[] m)` |  |
| 2221 | 1 | `public double getMonthGdp()` |  |
| 2222 | 1 | `public double getYearGdp()` |  |
| 2223 | 1 | `public double getGDP()` |  |
| 2225 | 1 | `public double getTaxRate()` | The income rate - the three income taxes together, TaxPolicy.getIncomeTaxRate(): the profit rate once they have parted (0.7.4). |
| 2227 | 10 | `public void setPreviousGdp(HistorySave historySave)` |  |

### CONVENIENCES - the prices the screens and the harnesses ask for by name (lines 2238-2254)

| line | len | member | says |
|---:|---:|---|---|
| 2243 | 1 | `public double getFoodLocalPrice()` | What one person-month of food costs the shops at today's market prices. |
| 2244 | 1 | `public double getIronLocalPrice()` |  |
| 2247 | 7 | `public long getFoodUnitsHeld()` | Every warehouse and shelf of food in the city, in KILOGRAMS across the thirteen. |

### SAVE AND RESTORE (lines 2255-2287)

| line | len | member | says |
|---:|---:|---|---|
| 2259 | 1 | `public List<SectorState> getSectorStates()` |  |
| 2260 | 1 | `public void restoreSectorStates(List<SectorState> s)` |  |
| 2263 | 13 | `public void restoreSectorBills(List<SectorState> saved)` | The month's bills back over the rebuild's re-derivation, and the total they add to. |
| 2276 | 1 | `public List<Markets.State> getMarketStates()` |  |
| 2277 | 1 | `public void restoreMarketStates(List<Markets.State> s)` |  |
| 2279 | 1 | `public SalesTaxLedger.State getSalesTaxState()` |  |
| 2282 | 5 | `public boolean restoreSalesTaxState(SalesTaxLedger.State state)` | The month's VAT back, AND the total that came out of it - one fact. |

### PRINTERS, RESET, THE REFORM (lines 2288-2429)

| line | len | member | says |
|---:|---:|---|---|
| 2292 | 3 | `public void printWageTaxInfo()` |  |
| 2296 | 35 | `public void printCityStats()` |  |
| 2332 | 15 | `public void resetEconomyManager()` |  |
| 2350 | 4 | `static { ... }` |  |
| 2362 | 62 | `public void redenominate(double scale)` | Every figure the economy manager holds, and every sector and market under it, in the new unit. |
| 2425 | 4 | `private static void scaleArray(double[] values, double scale)` |  |

