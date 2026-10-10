# NationalAccounts.java - 1,238 lines · 100 methods · 6 constants · model

`ham/citybuildersim/NationalAccounts.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The city's GDP, measured properly, plus the government's own books.
> 
> WHY THE OLD FIGURE WENT NEGATIVE
> 
> getMonthGdp() did two wrong things at once:
> 
>     GDP = totalWage + industrialHandler.getNetIncome() + commercialHandler.getNetIncome();
>     return Math.round((yearGDP / 12) * 100) / 100;
> 
> It ASSIGNED one thing and RETURNED another - the annualised figure divided by
> twelve, which is a different number that had been computed a month earlier.
> And what it assigned was an income-approach GDP with only two of its terms:
> wages plus profits. Once businesses started paying interest their profits went
> negative, swamped the wage bill, and the whole economy read as negative output
> while its shops were plainly full and its builders busy.
> 
> Profit is not production. A city where every firm loses money still grows food,
> builds houses and houses people, and GDP has to say so.
> 
> HOW IT IS MEASURED NOW
> 
> The expenditure approach - what everything produced actually gets spent on:
> 
>     GDP = C + I + G + NX
> 
>   C   households buying goods from shops, and paying rent for somewhere to live
>   I   construction work put in place, plus the change in stock held
>   G   what the city spends running the services it owns
>   NX  exports less imports; nothing is exported yet, so this is negative
> 
> Only FINAL spending is counted, which is what keeps intermediate trade from
> being counted twice: the food a store buys from a mill is not in C - only the
> food the store then sells to a household is. Imports are subtracted for the
> same reason, because they were produced somewhere else.
> 
> It cannot go negative for a bad month of trading, which is the whole point.
> 
> WHY IT WENT NEGATIVE ANYWAY, AND THE TWO ARITHMETIC ERRORS BEHIND IT
> 
> A hand-played city of 37,730 people reported monthly GDP of -$446,424 for a
> hundred months while its shops were full, its builders busy and its net income
> positive. The line above claimed only net exports could be negative. It was
> wrong twice.
> 
> 1. IMPORTED MATERIALS WERE SUBTRACTED AND NEVER ADDED BACK.
> 
>    Construction materials bought abroad were counted as an import the month
>    they were bought, but the stockpile they went into was not counted as
>    anything - inventory only ever meant food. So ordering 8,000 houses took
>    $480,000 straight off GDP on the spot, and the houses those materials
>    became were recognised gradually over the following years as construction
>    work. Buy in bulk and the city's measured output collapses in the month it
>    invests the most, which is exactly backwards.
> 
>    Materials in the yard are inventory. They are counted as such now, so the
>    import and the stock-build cancel in the month of purchase and the value
>    appears as output only when the work is actually put in place.
> 
> 2. INVENTORY WAS VALUED, NOT MEASURED.
> 
> ... (12 more lines in the source)

**Uses:** [Good](Good.md) (16)

**Used by (26):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankScreen](BankScreen.md), [CityNeeds](CityNeeds.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [FinancesScreen](FinancesScreen.md), [FuelSplit](FuelSplit.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 85 | · GDP components |
| 113 | · government income |
| 142 | EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c) |
| 422 | · AND THE BOUTIQUE'S STOCKROOM IS THE FOURTH TERM (2026-09-17) |
| 877 | THE MONTH THE GOVERNMENT ACTUALLY HAD |
| 991 | · GDP |
| 1028 | · growth |
| 1099 | · government |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `NationalAccounts.HISTORY_MONTHS` | `120` | How many months of GDP the rolling history keeps - ten years - and the most a load seeds it with (seedHistory()). |
| 201 | `NationalAccounts.HELD` | `{ Good.CROPS, Good.VANS, Good.ROLLING_STOCK, Good.PETROL, Good.DIESEL, Good.L...` | The goods the fifth term measures, in the order the save keeps their units (slots 15 on, EconomyManager.getNationalAccountsState()): a new one goes on the end. |
| 205 | `NationalAccounts.HELD_BEFORE_FUEL` | `3` | ...and how many of them a save from 0.7.58 to 0.7.61 carries: the three before FUEL, which such a city held none of. |
| 208 | `NationalAccounts.HELD_WITH_FUEL` | `4` | ...and how many a save from 0.7.62 to 0.7.75 carries: the three and FUEL, which FuelSplit makes PETROL and DIESEL. |
| 211 | `NationalAccounts.NOT_HELD` | `Good.CARS` | ...and the one good a sector holds that no term measures, and why: see EVERY OTHER GOOD A SECTOR HOLDS. |
| 905 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 87 | `private double consumptionGoods` |  |
| 88 | `private double consumptionHousing` |  |
| 89 | `private double investmentConstruction` |  |
| 90 | `private double investmentInventories` |  |
| 91 | `private double government` |  |
| 92 | `private double importsFood` |  |
| 93 | `private double importsMaterials` |  |
| 105 | `private double importsRawMaterial` | Raw material heavy industry buys abroad, and what it ships back out. |
| 106 | `private double exports` |  |
| 108 | `private double gdp` |  |
| 111 | `private final List<Double> history` | Monthly GDP, oldest first. |
| 115 | `private double taxBusiness` |  |
| 116 | `private double taxIndustrial` |  |
| 117 | `private double taxSales` |  |
| 118 | `private double taxWage` |  |
| 119 | `private double utilityIncome` |  |
| 120 | `private double landSales` |  |
| 128 | `private double propertyTax` | Property tax. |
| 130 | `private double interestExpense` |  |
| 131 | `private double capitalSpending` |  |
| 132 | `private double landPurchases` |  |
| 138 | `private double lastFoodVolume` | Stock held at the end of last month, in UNITS, so the change can be measured as a volume rather than as a value. |
| 139 | `private double lastMaterialUnits` |  |
| 140 | `private double lastLuxuryUnits` |  |
| 213 | `private final double[] lastHeldUnits` |  |
| 216 | `private boolean heldBaselineKnown` | Whether lastHeldUnits is a real baseline: false only after loading a save from before 0.7.58, whose first month books no change in these goods. |
| 238 | `private double invFood` | The three parts of the inventory term, kept so a diagnostic can say which one moved rather than leaving the reader to infer it from the total. |
| 239 | `private double invMaterials` |  |
| 240 | `private double invLuxuries` |  |
| 241 | `private double invHeld` |  |
| 256 | `private boolean inventoryBaselineKnown` | Whether last month's stock is actually known. |
| 652 | `private double contributions` | Pension contributions in, pensions out. |
| 653 | `private double pensions` |  |
| 656 | `private double eiPremiums` | EI premiums in; EI benefits and student grants out. |
| 657 | `private double eiBenefits` |  |
| 658 | `private double studentGrants` |  |
| 661 | `private double healthPremiums` | The health premium off every wage, in; a revenue line beside the EI premium (2026-09-19). |
| 664 | `private double studentLoanInterest` | Interest the graduates paid on their student loans, in; a revenue line beside the premiums (2026-09-21). |
| 694 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO LINES (0.7.0): its profit remitted to the treasury, in; the interest the treasury paid it on its advances, out. |
| 715 | `private double mortgagePremiums, mortgageClaims` | THE CITY'S MORTGAGE INSURANCE (0.7.11): the premiums the landlords paid on the mortgages written this month, in; what the insurance paid the bank on insured mortgages the month's defaults wrote down, out. |
| 736 | `private double fundTransfer` | THE TRANSFER FROM THE CITY'S FUND (0.7.14): the withdrawal dial's share of the fund's value (0.7.48; by default a twelfth of TreasuryFund.TRANSFER_RATE, Norway's fiscal rule), paid from its cash and over the default f... |
| 749 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43): the vouchers the treasury paid toward the households' groceries, a transfer like EI - a spending line, with its own setter for the fund transfer's reason: Game moves the cash where it is paid... |
| 792 | `private double cityRepairs, arrearsPaid, reserveCrudeBought, reserveCrudeSold` | EVERY MONTHLY TREASURY FLOW INSIDE THE BUDGET'S TOTALS (0.7.102, Jerus's A15: "rec plus maintenance ... |
| 821 | `private double fuelDuty` | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7): a duty a litre on the drivers' petrol at the pump (TaxPolicy, THE FUEL DUTY), a revenue line of its own - in the cash through the tax take, as the fares are, and set besi... |
| 839 | `private double safetySpending` | The police and the prisons: payroll and upkeep. |
| 855 | `private double transitFares, transitSpending` | Fares in, the buses' and trams' wage bill out. |
| 866 | `private double healthFees` | Patient and funeral fees in, the health service's bill out. |
| 867 | `private double healthSpending` |  |
| 870 | `private double educationFees` | Tuition in, and what the schools cost. |
| 871 | `private double educationSpending` |  |
| 874 | `private double subsidies` | What the city paid to hold a loss-making sector at break-even. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 1159 | **type** `public class NationalAccounts` | The city's GDP, measured properly, plus the government's own books. |

### GDP components (lines 85-112)

### government income (lines 113-141)

### EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c) (lines 142-876)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 28 | `public void restore(double gdp, double lastFoodVolume, double consumptionGoods, double consumptionHousing, double investmentCon...` | Puts back the month a save was taken in. |
| 312 | 1 | `public double getLastFoodVolume()` | Last month's food inventory, as a VOLUME. |
| 313 | 1 | `public double getLastMaterialUnits()` |  |
| 314 | 1 | `public double getLastLuxuryUnits()` |  |
| 315 | 1 | `public double getInvFood()` |  |
| 316 | 1 | `public double getInventoryFood()` |  |
| 317 | 1 | `public double getInventoryMaterials()` |  |
| 318 | 1 | `public double getInventoryLuxuries()` |  |
| 320 | 1 | `public double getInventoryHeld()` | The fifth term: the change in every good in HELD that the sectors hold, priced at what one costs to bring in (0.7.58). |
| 322 | 1 | `public double[] getLastHeldUnits()` | Last month's units of each good in HELD, in its order (0.7.58). |
| 323 | 1 | `public boolean isBaselineKnown()` |  |
| 330 | 9 | `public void restoreHeld(double[] lastHeld)` | ...and the units of HELD's goods last month (0.7.58), or null for a save from before them: then the first month books no change in them, rather than booking every fleet in the city as that month's output. |
| 352 | 12 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | Measures the month. |
| 371 | 160 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | ...and with every other good the sectors hold (0.7.58): heldUnits and heldPrices in HELD's order - the units held, every sector's stock and pantry together, and what one costs to bring in today. |
| 533 | 4 | `private static double held(double[] values, int i)` | One good's figure from an array in HELD's order: 0 past its end, for null, or for anything not a finite number. |
| 548 | 7 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The city's own budget for the month. |
| 568 | 8 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same, with the pension flows. |
| 578 | 10 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again with healthcare but no schools, for the older callers. |
| 601 | 11 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again, with healthcare. |
| 624 | 26 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | ...and with what the city paid to keep a sector alive. |
| 666 | 3 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants)` |  |
| 671 | 4 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums)` | ...and with the health premium beside them (2026-09-19). |
| 677 | 8 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums, double studentLo...` | ...and the student loan interest beside those (2026-09-21). |
| 696 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 702 | 1 | `public double getCentralBankRemittance()` | What the central bank remitted: its profit, which is the interest the city paid itself less what reserves cost. |
| 704 | 1 | `public double getCentralBankInterest()` | What the treasury paid the central bank on its advances. |
| 717 | 4 | `public void setMortgageInsuranceLines(double premiums, double claims)` |  |
| 723 | 1 | `public double getMortgagePremiums()` | The premiums the landlords paid the city's insurance this month - a revenue line. |
| 738 | 1 | `public void setFundTransfer(double transfer)` |  |
| 741 | 1 | `public double getFundTransfer()` | What the city's fund paid the budget this month - a revenue line. |
| 751 | 1 | `public void setFoodAssistance(double paid)` |  |
| 754 | 1 | `public double getFoodAssistance()` | What the treasury paid toward the households' groceries this month - a spending line. |
| 795 | 1 | `public void setCityRepairs(double paid)` | The month's repairs to the city's own buildings, as paid - a spending line (0.7.102). |
| 797 | 1 | `public void setArrearsPaid(double paid)` | What the treasury paid down of what it owed the businesses this month - a spending line (0.7.102). |
| 799 | 4 | `public void setReserveCrude(double bought, double sold)` | The strategic reserve's crude bought and sold at the month's strike - a spending line and a revenue line (0.7.102). |
| 805 | 1 | `public double getCityRepairs()` | What the treasury paid this month to keep the city's own buildings up - a spending line since 0.7.102. |
| 807 | 1 | `public double getArrearsPaid()` | What the treasury paid down this month of what it owed the businesses - a spending line since 0.7.102. |
| 809 | 1 | `public double getReserveCrudeBought()` | The crude the treasury paid for at the month's strike, for the strategic reserve - a spending line since 0.7.102. |
| 811 | 1 | `public double getReserveCrudeSold()` | ...and what the reserve's crude sold for - a revenue line since 0.7.102. |
| 823 | 1 | `public void setFuelDuty(double duty)` |  |
| 826 | 1 | `public double getFuelDuty()` | The fuel duty the drivers paid on their petrol this month - a revenue line (0.7.103). |
| 828 | 1 | `public double getMortgageClaims()` | What the city's insurance paid the bank this month on insured mortgages written down - a spending line. |
| 830 | 1 | `public double getEiPremiums()` |  |
| 832 | 1 | `public double getHealthPremiums()` | What the health premium raised this month - a government revenue line, against the health service's cost. |
| 834 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month - a government revenue line; the principal is not one. |
| 835 | 1 | `public double getEiBenefits()` |  |
| 836 | 1 | `public double getStudentGrants()` |  |
| 840 | 1 | `public void setSafetySpending(double spending)` |  |
| 841 | 1 | `public double getSafetySpending()` |  |
| 857 | 4 | `public void setTransitLines(double spending, double fares)` |  |
| 862 | 1 | `public double getTransitSpending()` |  |
| 863 | 1 | `public double getTransitFares()` |  |
| 875 | 1 | `public double getSubsidies()` |  |

### THE MONTH THE GOVERNMENT ACTUALLY HAD (lines 877-990)

| line | len | member | says |
|---:|---:|---|---|
| 907 | 38 | `double[] governmentToSave()` |  |
| 946 | 37 | `void restoreGovernment(double[] saved)` |  |
| 984 | 1 | `public double getContributions()` |  |
| 985 | 1 | `public double getPensions()` |  |
| 986 | 1 | `public double getHealthFees()` |  |
| 987 | 1 | `public double getHealthSpending()` |  |
| 988 | 1 | `public double getEducationFees()` |  |
| 989 | 1 | `public double getEducationSpending()` |  |

### GDP (lines 991-1027)

| line | len | member | says |
|---:|---:|---|---|
| 993 | 1 | `public double getConsumption()` |  |
| 994 | 1 | `public double getInvestment()` |  |
| 995 | 3 | `public double getNetExports()` |  |
| 999 | 1 | `public double getConsumptionGoods()` |  |
| 1000 | 1 | `public double getConsumptionHousing()` |  |
| 1001 | 1 | `public double getInvestmentConstruction()` |  |
| 1002 | 1 | `public double getInvestmentInventories()` |  |
| 1003 | 1 | `public double getGovernment()` |  |
| 1004 | 1 | `public double getImportsFood()` |  |
| 1005 | 1 | `public double getImportsMaterials()` |  |
| 1006 | 1 | `public double getImportsRawMaterial()` |  |
| 1007 | 1 | `public double getExports()` |  |
| 1008 | 3 | `public double getTotalImports()` |  |
| 1012 | 1 | `public double getGdp()` |  |
| 1015 | 8 | `public double getAnnualGdp()` | The last twelve months, or as many as there are - not gdp * 12. |
| 1024 | 3 | `public double getGdpPerCapita(long population)` |  |

### growth (lines 1028-1098)

| line | len | member | says |
|---:|---:|---|---|
| 1036 | 9 | `public double getMonthlyGrowthAnnualised()` | Month on month, as an annual rate. |
| 1047 | 8 | `public double getYearOnYearGrowth()` | This month against the same month a year ago. |
| 1057 | 5 | `public double getTrendGdp()` | Average monthly GDP over the last twelve, to smooth a lumpy month. |
| 1063 | 1 | `public int getMonthsRecorded()` |  |
| 1065 | 1 | `public List<Double> getHistory()` |  |
| 1089 | 9 | `public void seedHistory(List<Double> monthly)` | The rolling history put back after a load (0.7.31, the Government spec's B1): the last HISTORY_MONTHS of the graph history's GDP series (HistorySave.getGdp(), a month's GDP kept every month and saved), in place of the... |

### government (lines 1099-1238)

| line | len | member | says |
|---:|---:|---|---|
| 1101 | 1 | `public double getTaxBusiness()` |  |
| 1102 | 1 | `public double getTaxIndustrial()` |  |
| 1103 | 1 | `public double getTaxSales()` |  |
| 1104 | 1 | `public double getTaxWage()` |  |
| 1105 | 1 | `public double getUtilityIncome()` |  |
| 1106 | 1 | `public double getLandSales()` |  |
| 1107 | 1 | `public double getPropertyTax()` |  |
| 1109 | 22 | `public double getTotalRevenue()` |  |
| 1132 | 1 | `public double getInterestExpense()` |  |
| 1133 | 1 | `public double getCapitalSpending()` |  |
| 1134 | 1 | `public double getLandPurchases()` |  |
| 1136 | 16 | `public double getTotalExpenses()` |  |
| 1154 | 3 | `public double getBalance()` | Surplus or deficit - what actually moves the city's cash this month. |
| 1159 | 4 | `public double getRevenueToGdp()` | Revenue as a share of output. |
| 1164 | 4 | `public double getDebtToGdp(double debt)` |  |
| 1169 | 10 | `public void reset()` |  |
| 1200 | 37 | `public void redenominate(double scale)` | The month's national accounts, in the new unit. |

