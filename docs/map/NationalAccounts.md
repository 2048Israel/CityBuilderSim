# NationalAccounts.java - 887 lines · 78 methods · 2 constants · model

`ham/citybuildersim/NationalAccounts.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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

**Used by (14):** [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [NewGameCheck](NewGameCheck.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [TreasuryCheck](TreasuryCheck.md)

## Sections

| line | section |
|---:|---|
| 84 | · GDP components |
| 112 | · government income |
| 302 | · AND THE BOUTIQUE'S STOCKROOM IS THE FOURTH TERM (2026-09-17) |
| 611 | THE MONTH THE GOVERNMENT ACTUALLY HAD |
| 700 | · GDP |
| 737 | · growth |
| 776 | · government |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 82 | `NationalAccounts.HISTORY_MONTHS` | `120` |  |
| 639 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 86 | `private double consumptionGoods` |  |
| 87 | `private double consumptionHousing` |  |
| 88 | `private double investmentConstruction` |  |
| 89 | `private double investmentInventories` |  |
| 90 | `private double government` |  |
| 91 | `private double importsFood` |  |
| 92 | `private double importsMaterials` |  |
| 104 | `private double importsRawMaterial` | Raw material heavy industry buys abroad, and what it ships back out. |
| 105 | `private double exports` |  |
| 107 | `private double gdp` |  |
| 110 | `private final List<Double> history` | Monthly GDP, oldest first. |
| 114 | `private double taxBusiness` |  |
| 115 | `private double taxIndustrial` |  |
| 116 | `private double taxSales` |  |
| 117 | `private double taxWage` |  |
| 118 | `private double utilityIncome` |  |
| 119 | `private double landSales` |  |
| 127 | `private double propertyTax` | Property tax. |
| 129 | `private double interestExpense` |  |
| 130 | `private double capitalSpending` |  |
| 131 | `private double landPurchases` |  |
| 137 | `private double lastFoodVolume` | Stock held at the end of last month, in UNITS, so the change can be measured as a volume rather than as a value. |
| 138 | `private double lastMaterialUnits` |  |
| 139 | `private double lastLuxuryUnits` |  |
| 161 | `private double invFood` | The three parts of the inventory term, kept so a diagnostic can say which one moved rather than leaving the reader to infer it from the total. |
| 162 | `private double invMaterials` |  |
| 163 | `private double invLuxuries` |  |
| 178 | `private boolean inventoryBaselineKnown` | Whether last month's stock is actually known. |
| 514 | `private double contributions` | Pension contributions in, pensions out. |
| 515 | `private double pensions` |  |
| 518 | `private double eiPremiums` | EI premiums in; EI benefits and student grants out. |
| 519 | `private double eiBenefits` |  |
| 520 | `private double studentGrants` |  |
| 523 | `private double healthPremiums` | The health premium off every wage, in; a revenue line beside the EI premium (2026-09-19). |
| 526 | `private double studentLoanInterest` | Interest the graduates paid on their student loans, in; a revenue line beside the premiums (2026-09-21). |
| 556 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO LINES (0.7.0): its profit remitted to the treasury, in; the interest the treasury paid it on its advances, out. |
| 577 | `private double safetySpending` | The police and the prisons: payroll and upkeep. |
| 589 | `private double transitFares, transitSpending` | Fares in, the buses' and trams' wage bill out. |
| 600 | `private double healthFees` | Patient and funeral fees in, the health service's bill out. |
| 601 | `private double healthSpending` |  |
| 604 | `private double educationFees` | Tuition in, and what the schools cost. |
| 605 | `private double educationSpending` |  |
| 608 | `private double subsidies` | What the city paid to hold a loss-making sector at break-even. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 808 | **type** `public class NationalAccounts` | The city's GDP, measured properly, plus the government's own books. |

### GDP components (lines 84-111)

### government income (lines 112-610)

| line | len | member | says |
|---:|---:|---|---|
| 192 | 28 | `public void restore(double gdp, double lastFoodVolume, double consumptionGoods, double consumptionHousing, double investmentCon...` | Puts back the month a save was taken in. |
| 231 | 1 | `public double getLastFoodVolume()` | Last month's food inventory, as a VOLUME. |
| 232 | 1 | `public double getLastMaterialUnits()` |  |
| 233 | 1 | `public double getLastLuxuryUnits()` |  |
| 234 | 1 | `public double getInvFood()` |  |
| 235 | 1 | `public double getInventoryFood()` |  |
| 236 | 1 | `public double getInventoryMaterials()` |  |
| 237 | 1 | `public double getInventoryLuxuries()` |  |
| 238 | 1 | `public boolean isBaselineKnown()` |  |
| 252 | 147 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | Measures the month. |
| 410 | 7 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The city's own budget for the month. |
| 430 | 8 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same, with the pension flows. |
| 440 | 10 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again with healthcare but no schools, for the older callers. |
| 463 | 11 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again, with healthcare. |
| 486 | 26 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | ...and with what the city paid to keep a sector alive. |
| 528 | 3 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants)` |  |
| 533 | 4 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums)` | ...and with the health premium beside them (2026-09-19). |
| 539 | 8 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums, double studentLo...` | ...and the student loan interest beside those (2026-09-21). |
| 558 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 564 | 1 | `public double getCentralBankRemittance()` | What the central bank remitted: its profit, which is the interest the city paid itself less what reserves cost. |
| 566 | 1 | `public double getCentralBankInterest()` | What the treasury paid the central bank on its advances. |
| 568 | 1 | `public double getEiPremiums()` |  |
| 570 | 1 | `public double getHealthPremiums()` | What the health premium raised this month - a government revenue line, against the health service's cost. |
| 572 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month - a government revenue line; the principal is not one. |
| 573 | 1 | `public double getEiBenefits()` |  |
| 574 | 1 | `public double getStudentGrants()` |  |
| 578 | 1 | `public void setSafetySpending(double spending)` |  |
| 579 | 1 | `public double getSafetySpending()` |  |
| 591 | 4 | `public void setTransitLines(double spending, double fares)` |  |
| 596 | 1 | `public double getTransitSpending()` |  |
| 597 | 1 | `public double getTransitFares()` |  |
| 609 | 1 | `public double getSubsidies()` |  |

### THE MONTH THE GOVERNMENT ACTUALLY HAD (lines 611-699)

| line | len | member | says |
|---:|---:|---|---|
| 641 | 24 | `double[] governmentToSave()` |  |
| 666 | 26 | `void restoreGovernment(double[] saved)` |  |
| 693 | 1 | `public double getContributions()` |  |
| 694 | 1 | `public double getPensions()` |  |
| 695 | 1 | `public double getHealthFees()` |  |
| 696 | 1 | `public double getHealthSpending()` |  |
| 697 | 1 | `public double getEducationFees()` |  |
| 698 | 1 | `public double getEducationSpending()` |  |

### GDP (lines 700-736)

| line | len | member | says |
|---:|---:|---|---|
| 702 | 1 | `public double getConsumption()` |  |
| 703 | 1 | `public double getInvestment()` |  |
| 704 | 3 | `public double getNetExports()` |  |
| 708 | 1 | `public double getConsumptionGoods()` |  |
| 709 | 1 | `public double getConsumptionHousing()` |  |
| 710 | 1 | `public double getInvestmentConstruction()` |  |
| 711 | 1 | `public double getInvestmentInventories()` |  |
| 712 | 1 | `public double getGovernment()` |  |
| 713 | 1 | `public double getImportsFood()` |  |
| 714 | 1 | `public double getImportsMaterials()` |  |
| 715 | 1 | `public double getImportsRawMaterial()` |  |
| 716 | 1 | `public double getExports()` |  |
| 717 | 3 | `public double getTotalImports()` |  |
| 721 | 1 | `public double getGdp()` |  |
| 724 | 8 | `public double getAnnualGdp()` | The last twelve months, or as many as there are - not gdp * 12. |
| 733 | 3 | `public double getGdpPerCapita(int population)` |  |

### growth (lines 737-775)

| line | len | member | says |
|---:|---:|---|---|
| 745 | 9 | `public double getMonthlyGrowthAnnualised()` | Month on month, as an annual rate. |
| 756 | 8 | `public double getYearOnYearGrowth()` | This month against the same month a year ago. |
| 766 | 5 | `public double getTrendGdp()` | Average monthly GDP over the last twelve, to smooth a lumpy month. |
| 772 | 1 | `public int getMonthsRecorded()` |  |
| 774 | 1 | `public List<Double> getHistory()` |  |

### government (lines 776-887)

| line | len | member | says |
|---:|---:|---|---|
| 778 | 1 | `public double getTaxBusiness()` |  |
| 779 | 1 | `public double getTaxIndustrial()` |  |
| 780 | 1 | `public double getTaxSales()` |  |
| 781 | 1 | `public double getTaxWage()` |  |
| 782 | 1 | `public double getUtilityIncome()` |  |
| 783 | 1 | `public double getLandSales()` |  |
| 784 | 1 | `public double getPropertyTax()` |  |
| 786 | 11 | `public double getTotalRevenue()` |  |
| 798 | 1 | `public double getInterestExpense()` |  |
| 799 | 1 | `public double getCapitalSpending()` |  |
| 800 | 1 | `public double getLandPurchases()` |  |
| 802 | 7 | `public double getTotalExpenses()` |  |
| 811 | 3 | `public double getBalance()` | Surplus or deficit - what actually moves the city's cash this month. |
| 816 | 4 | `public double getRevenueToGdp()` | Revenue as a share of output. |
| 821 | 4 | `public double getDebtToGdp(double debt)` |  |
| 826 | 8 | `public void reset()` |  |
| 855 | 31 | `public void redenominate(double scale)` | The month's national accounts, in the new unit. |

