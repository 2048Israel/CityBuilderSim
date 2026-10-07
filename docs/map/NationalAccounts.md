# NationalAccounts.java - 1,125 lines · 91 methods · 5 constants · model

`ham/citybuildersim/NationalAccounts.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (7)

**Used by (22):** [BankScreen](BankScreen.md), [CityNeeds](CityNeeds.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [FinancesScreen](FinancesScreen.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 85 | · GDP components |
| 113 | · government income |
| 142 | EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c) |
| 402 | · AND THE BOUTIQUE'S STOCKROOM IS THE FOURTH TERM (2026-09-17) |
| 785 | THE MONTH THE GOVERNMENT ACTUALLY HAD |
| 890 | · GDP |
| 927 | · growth |
| 996 | · government |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `NationalAccounts.HISTORY_MONTHS` | `120` | How many months of GDP the rolling history keeps - ten years - and the most a load seeds it with (seedHistory()). |
| 188 | `NationalAccounts.HELD` | `{ Good.CROPS, Good.VANS, Good.ROLLING_STOCK, Good.FUEL }` | The goods the fifth term measures, in the order the save keeps their units (slots 15 on, EconomyManager.getNationalAccountsState()): a new one goes on the end. |
| 191 | `NationalAccounts.HELD_BEFORE_FUEL` | `3` | ...and how many of them a save from 0.7.58 to 0.7.61 carries: the three before FUEL, which such a city held none of. |
| 194 | `NationalAccounts.NOT_HELD` | `Good.CARS` | ...and the one good a sector holds that no term measures, and why: see EVERY OTHER GOOD A SECTOR HOLDS. |
| 813 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

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
| 196 | `private final double[] lastHeldUnits` |  |
| 199 | `private boolean heldBaselineKnown` | Whether lastHeldUnits is a real baseline: false only after loading a save from before 0.7.58, whose first month books no change in these goods. |
| 221 | `private double invFood` | The three parts of the inventory term, kept so a diagnostic can say which one moved rather than leaving the reader to infer it from the total. |
| 222 | `private double invMaterials` |  |
| 223 | `private double invLuxuries` |  |
| 224 | `private double invHeld` |  |
| 239 | `private boolean inventoryBaselineKnown` | Whether last month's stock is actually known. |
| 632 | `private double contributions` | Pension contributions in, pensions out. |
| 633 | `private double pensions` |  |
| 636 | `private double eiPremiums` | EI premiums in; EI benefits and student grants out. |
| 637 | `private double eiBenefits` |  |
| 638 | `private double studentGrants` |  |
| 641 | `private double healthPremiums` | The health premium off every wage, in; a revenue line beside the EI premium (2026-09-19). |
| 644 | `private double studentLoanInterest` | Interest the graduates paid on their student loans, in; a revenue line beside the premiums (2026-09-21). |
| 674 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO LINES (0.7.0): its profit remitted to the treasury, in; the interest the treasury paid it on its advances, out. |
| 695 | `private double mortgagePremiums, mortgageClaims` | THE CITY'S MORTGAGE INSURANCE (0.7.11): the premiums the landlords paid on the mortgages written this month, in; what the insurance paid the bank on insured mortgages the month's defaults wrote down, out. |
| 716 | `private double fundTransfer` | THE TRANSFER FROM THE CITY'S FUND (0.7.14): the withdrawal dial's share of the fund's value (0.7.48; by default a twelfth of TreasuryFund.TRANSFER_RATE, Norway's fiscal rule), paid from its cash and over the default f... |
| 729 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43): the vouchers the treasury paid toward the households' groceries, a transfer like EI - a spending line, with its own setter for the fund transfer's reason: Game moves the cash where it is paid... |
| 747 | `private double safetySpending` | The police and the prisons: payroll and upkeep. |
| 763 | `private double transitFares, transitSpending` | Fares in, the buses' and trams' wage bill out. |
| 774 | `private double healthFees` | Patient and funeral fees in, the health service's bill out. |
| 775 | `private double healthSpending` |  |
| 778 | `private double educationFees` | Tuition in, and what the schools cost. |
| 779 | `private double educationSpending` |  |
| 782 | `private double subsidies` | What the city paid to hold a loss-making sector at break-even. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 1046 | **type** `public class NationalAccounts` | The city's GDP, measured properly, plus the government's own books. |

### GDP components (lines 85-112)

### government income (lines 113-141)

### EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c) (lines 142-784)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 28 | `public void restore(double gdp, double lastFoodVolume, double consumptionGoods, double consumptionHousing, double investmentCon...` | Puts back the month a save was taken in. |
| 294 | 1 | `public double getLastFoodVolume()` | Last month's food inventory, as a VOLUME. |
| 295 | 1 | `public double getLastMaterialUnits()` |  |
| 296 | 1 | `public double getLastLuxuryUnits()` |  |
| 297 | 1 | `public double getInvFood()` |  |
| 298 | 1 | `public double getInventoryFood()` |  |
| 299 | 1 | `public double getInventoryMaterials()` |  |
| 300 | 1 | `public double getInventoryLuxuries()` |  |
| 302 | 1 | `public double getInventoryHeld()` | The fifth term: the change in every good in HELD that the sectors hold, priced at what one costs to bring in (0.7.58). |
| 304 | 1 | `public double[] getLastHeldUnits()` | Last month's units of each good in HELD, in its order (0.7.58). |
| 305 | 1 | `public boolean isBaselineKnown()` |  |
| 312 | 7 | `public void restoreHeld(double[] lastHeld)` | ...and the units of HELD's goods last month (0.7.58), or null for a save from before them: then the first month books no change in them, rather than booking every fleet in the city as that month's output. |
| 332 | 12 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | Measures the month. |
| 351 | 160 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | ...and with every other good the sectors hold (0.7.58): heldUnits and heldPrices in HELD's order - the units held, every sector's stock and pantry together, and what one costs to bring in today. |
| 513 | 4 | `private static double held(double[] values, int i)` | One good's figure from an array in HELD's order: 0 past its end, for null, or for anything not a finite number. |
| 528 | 7 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The city's own budget for the month. |
| 548 | 8 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same, with the pension flows. |
| 558 | 10 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again with healthcare but no schools, for the older callers. |
| 581 | 11 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again, with healthcare. |
| 604 | 26 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | ...and with what the city paid to keep a sector alive. |
| 646 | 3 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants)` |  |
| 651 | 4 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums)` | ...and with the health premium beside them (2026-09-19). |
| 657 | 8 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums, double studentLo...` | ...and the student loan interest beside those (2026-09-21). |
| 676 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 682 | 1 | `public double getCentralBankRemittance()` | What the central bank remitted: its profit, which is the interest the city paid itself less what reserves cost. |
| 684 | 1 | `public double getCentralBankInterest()` | What the treasury paid the central bank on its advances. |
| 697 | 4 | `public void setMortgageInsuranceLines(double premiums, double claims)` |  |
| 703 | 1 | `public double getMortgagePremiums()` | The premiums the landlords paid the city's insurance this month - a revenue line. |
| 718 | 1 | `public void setFundTransfer(double transfer)` |  |
| 721 | 1 | `public double getFundTransfer()` | What the city's fund paid the budget this month - a revenue line. |
| 731 | 1 | `public void setFoodAssistance(double paid)` |  |
| 734 | 1 | `public double getFoodAssistance()` | What the treasury paid toward the households' groceries this month - a spending line. |
| 736 | 1 | `public double getMortgageClaims()` | What the city's insurance paid the bank this month on insured mortgages written down - a spending line. |
| 738 | 1 | `public double getEiPremiums()` |  |
| 740 | 1 | `public double getHealthPremiums()` | What the health premium raised this month - a government revenue line, against the health service's cost. |
| 742 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month - a government revenue line; the principal is not one. |
| 743 | 1 | `public double getEiBenefits()` |  |
| 744 | 1 | `public double getStudentGrants()` |  |
| 748 | 1 | `public void setSafetySpending(double spending)` |  |
| 749 | 1 | `public double getSafetySpending()` |  |
| 765 | 4 | `public void setTransitLines(double spending, double fares)` |  |
| 770 | 1 | `public double getTransitSpending()` |  |
| 771 | 1 | `public double getTransitFares()` |  |
| 783 | 1 | `public double getSubsidies()` |  |

### THE MONTH THE GOVERNMENT ACTUALLY HAD (lines 785-889)

| line | len | member | says |
|---:|---:|---|---|
| 815 | 34 | `double[] governmentToSave()` |  |
| 850 | 32 | `void restoreGovernment(double[] saved)` |  |
| 883 | 1 | `public double getContributions()` |  |
| 884 | 1 | `public double getPensions()` |  |
| 885 | 1 | `public double getHealthFees()` |  |
| 886 | 1 | `public double getHealthSpending()` |  |
| 887 | 1 | `public double getEducationFees()` |  |
| 888 | 1 | `public double getEducationSpending()` |  |

### GDP (lines 890-926)

| line | len | member | says |
|---:|---:|---|---|
| 892 | 1 | `public double getConsumption()` |  |
| 893 | 1 | `public double getInvestment()` |  |
| 894 | 3 | `public double getNetExports()` |  |
| 898 | 1 | `public double getConsumptionGoods()` |  |
| 899 | 1 | `public double getConsumptionHousing()` |  |
| 900 | 1 | `public double getInvestmentConstruction()` |  |
| 901 | 1 | `public double getInvestmentInventories()` |  |
| 902 | 1 | `public double getGovernment()` |  |
| 903 | 1 | `public double getImportsFood()` |  |
| 904 | 1 | `public double getImportsMaterials()` |  |
| 905 | 1 | `public double getImportsRawMaterial()` |  |
| 906 | 1 | `public double getExports()` |  |
| 907 | 3 | `public double getTotalImports()` |  |
| 911 | 1 | `public double getGdp()` |  |
| 914 | 8 | `public double getAnnualGdp()` | The last twelve months, or as many as there are - not gdp * 12. |
| 923 | 3 | `public double getGdpPerCapita(long population)` |  |

### growth (lines 927-995)

| line | len | member | says |
|---:|---:|---|---|
| 935 | 9 | `public double getMonthlyGrowthAnnualised()` | Month on month, as an annual rate. |
| 946 | 8 | `public double getYearOnYearGrowth()` | This month against the same month a year ago. |
| 956 | 5 | `public double getTrendGdp()` | Average monthly GDP over the last twelve, to smooth a lumpy month. |
| 962 | 1 | `public int getMonthsRecorded()` |  |
| 964 | 1 | `public List<Double> getHistory()` |  |
| 986 | 9 | `public void seedHistory(List<Double> monthly)` | The rolling history put back after a load (0.7.31, the Government spec's B1): the last HISTORY_MONTHS of the graph history's GDP series (HistorySave.getGdp(), a month's GDP kept every month and saved), in place of the... |

### government (lines 996-1125)

| line | len | member | says |
|---:|---:|---|---|
| 998 | 1 | `public double getTaxBusiness()` |  |
| 999 | 1 | `public double getTaxIndustrial()` |  |
| 1000 | 1 | `public double getTaxSales()` |  |
| 1001 | 1 | `public double getTaxWage()` |  |
| 1002 | 1 | `public double getUtilityIncome()` |  |
| 1003 | 1 | `public double getLandSales()` |  |
| 1004 | 1 | `public double getPropertyTax()` |  |
| 1006 | 18 | `public double getTotalRevenue()` |  |
| 1025 | 1 | `public double getInterestExpense()` |  |
| 1026 | 1 | `public double getCapitalSpending()` |  |
| 1027 | 1 | `public double getLandPurchases()` |  |
| 1029 | 13 | `public double getTotalExpenses()` |  |
| 1044 | 3 | `public double getBalance()` | Surplus or deficit - what actually moves the city's cash this month. |
| 1049 | 4 | `public double getRevenueToGdp()` | Revenue as a share of output. |
| 1054 | 4 | `public double getDebtToGdp(double debt)` |  |
| 1059 | 10 | `public void reset()` |  |
| 1090 | 34 | `public void redenominate(double scale)` | The month's national accounts, in the new unit. |

