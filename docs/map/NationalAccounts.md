# NationalAccounts.java - 1,381 lines · 107 methods · 6 constants · model

`ham/citybuildersim/NationalAccounts.java` - generated 2026-10-14 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (26)

**Used by (30):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankScreen](BankScreen.md), [CityNeeds](CityNeeds.md), [CopperMiningCheck](CopperMiningCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [FinancesScreen](FinancesScreen.md), [FuelSplit](FuelSplit.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 85 | · GDP components |
| 117 | · government income |
| 146 | EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c) |
| 512 | · AND THE BOUTIQUE'S STOCKROOM IS THE FOURTH TERM (2026-09-17) |
| 546 | · A YARD SOLD BACK FULL IS WRITTEN OFF, NOT UNPRODUCED (0.7.133, |
| 571 | · EVERY TERM ADDS ITS WRITE-OFF BACK (0.7.136, batch FX1) |
| 1020 | THE MONTH THE GOVERNMENT ACTUALLY HAD |
| 1134 | · GDP |
| 1171 | · growth |
| 1242 | · government |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `NationalAccounts.HISTORY_MONTHS` | `120` | How many months of GDP the rolling history keeps - ten years - and the most a load seeds it with (seedHistory()). |
| 226 | `NationalAccounts.HELD` | `{ Good.CROPS, Good.VANS, Good.ROLLING_STOCK, Good.PETROL, Good.DIESEL, Good.L...` | The goods the fifth term measures, in the order the save keeps their units (slots 15 on, EconomyManager.getNationalAccountsState()): a new one goes on the end. |
| 232 | `NationalAccounts.HELD_BEFORE_FUEL` | `3` | ...and how many of them a save from 0.7.58 to 0.7.61 carries: the three before FUEL, which such a city held none of. |
| 235 | `NationalAccounts.HELD_WITH_FUEL` | `4` | ...and how many a save from 0.7.62 to 0.7.75 carries: the three and FUEL, which FuelSplit makes PETROL and DIESEL. |
| 238 | `NationalAccounts.NOT_HELD` | `Good.CARS` | ...and the one good a sector holds that no term measures, and why: see EVERY OTHER GOOD A SECTOR HOLDS. |
| 1048 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

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
| 109 | `private double importsRawMaterial` | Raw material the businesses buy abroad - every sector's imports but Retail's food and Construction's materials, and the strategic reserve's crude (EconomyManager.updateNationalAccounts()) - and what the city ships bac... |
| 110 | `private double exports` |  |
| 112 | `private double gdp` |  |
| 115 | `private final List<Double> history` | Monthly GDP, oldest first. |
| 119 | `private double taxBusiness` |  |
| 120 | `private double taxIndustrial` |  |
| 121 | `private double taxSales` |  |
| 122 | `private double taxWage` |  |
| 123 | `private double utilityIncome` |  |
| 124 | `private double landSales` |  |
| 132 | `private double propertyTax` | Property tax. |
| 134 | `private double interestExpense` |  |
| 135 | `private double capitalSpending` |  |
| 136 | `private double landPurchases` |  |
| 142 | `private double lastFoodVolume` | Stock held at the end of last month, in UNITS, so the change can be measured as a volume rather than as a value. |
| 143 | `private double lastMaterialUnits` |  |
| 144 | `private double lastLuxuryUnits` |  |
| 240 | `private final double[] lastHeldUnits` |  |
| 243 | `private final double[] heldWrittenOffUnits` | The month's units of each good in HELD written off with the yard that held them (0.7.133): a flow, read in the month it is handed in. |
| 246 | `private double foodWrittenOffVolume, materialsWrittenOffUnits, luxuriesWrittenOffUnits` | ...and the first three terms' (0.7.136, batch FX1): the food written off, as the fixed-weight volume foodUnits is; the materials and the luxuries, in units. |
| 249 | `private final double[] writtenOffAddedBack` | What each term added back of the month's write-offs, at the price it measured the change at: food, materials, luxuries, HELD's goods (0.7.136). |
| 252 | `private boolean heldBaselineKnown` | Whether lastHeldUnits is a real baseline: false only after loading a save from before 0.7.58, whose first month books no change in these goods. |
| 274 | `private double invFood` | The three parts of the inventory term, kept so a diagnostic can say which one moved rather than leaving the reader to infer it from the total. |
| 275 | `private double invMaterials` |  |
| 276 | `private double invLuxuries` |  |
| 277 | `private double invHeld` |  |
| 292 | `private boolean inventoryBaselineKnown` | Whether last month's stock is actually known. |
| 795 | `private double contributions` | Pension contributions in, pensions out. |
| 796 | `private double pensions` |  |
| 799 | `private double eiPremiums` | EI premiums in; EI benefits and student grants out. |
| 800 | `private double eiBenefits` |  |
| 801 | `private double studentGrants` |  |
| 804 | `private double healthPremiums` | The health premium off every wage, in; a revenue line beside the EI premium (2026-09-19). |
| 807 | `private double studentLoanInterest` | Interest the graduates paid on their student loans, in; a revenue line beside the premiums (2026-09-21). |
| 837 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO LINES (0.7.0): its profit remitted to the treasury, in; the interest the treasury paid it on its advances, out. |
| 858 | `private double mortgagePremiums, mortgageClaims` | THE CITY'S MORTGAGE INSURANCE (0.7.11): the premiums the landlords paid on the mortgages written this month, in; what the insurance paid the bank on insured mortgages the month's defaults wrote down, out. |
| 879 | `private double fundTransfer` | THE TRANSFER FROM THE CITY'S FUND (0.7.14): the withdrawal dial's share of the fund's value (0.7.48; by default a twelfth of TreasuryFund.TRANSFER_RATE, Norway's fiscal rule), paid from its cash and over the default f... |
| 892 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43): the vouchers the treasury paid toward the households' groceries, a transfer like EI - a spending line, with its own setter for the fund transfer's reason: Game moves the cash where it is paid... |
| 935 | `private double cityRepairs, arrearsPaid, reserveCrudeBought, reserveCrudeSold` | EVERY MONTHLY TREASURY FLOW INSIDE THE BUDGET'S TOTALS (0.7.102, Jerus's A15: "rec plus maintenance ... |
| 964 | `private double fuelDuty` | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7): a duty a litre on the drivers' petrol at the pump (TaxPolicy, THE FUEL DUTY), a revenue line of its own - in the cash through the tax take, as the fares are, and set besi... |
| 982 | `private double safetySpending` | The police and the prisons: payroll and upkeep. |
| 998 | `private double transitFares, transitSpending` | Fares in, the buses' and trams' wage bill out. |
| 1009 | `private double healthFees` | Patient and funeral fees in, the health service's bill out. |
| 1010 | `private double healthSpending` |  |
| 1013 | `private double educationFees` | Tuition in, and what the schools cost. |
| 1014 | `private double educationSpending` |  |
| 1017 | `private double subsidies` | What the city paid to hold a loss-making sector at break-even. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 1302 | **type** `public class NationalAccounts` | The city's GDP, measured properly, plus the government's own books. |

### GDP components (lines 85-116)

### government income (lines 117-145)

### EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c) (lines 146-1019)

| line | len | member | says |
|---:|---:|---|---|
| 309 | 28 | `public void restore(double gdp, double lastFoodVolume, double consumptionGoods, double consumptionHousing, double investmentCon...` | Puts back the month a save was taken in. |
| 348 | 1 | `public double getLastFoodVolume()` | Last month's food inventory, as a VOLUME. |
| 349 | 1 | `public double getLastMaterialUnits()` |  |
| 350 | 1 | `public double getLastLuxuryUnits()` |  |
| 351 | 1 | `public double getInvFood()` |  |
| 352 | 1 | `public double getInventoryFood()` |  |
| 353 | 1 | `public double getInventoryMaterials()` |  |
| 354 | 1 | `public double getInventoryLuxuries()` |  |
| 356 | 1 | `public double getInventoryHeld()` | The fifth term: the change in every good in HELD that the sectors hold, priced at what one costs to bring in (0.7.58). |
| 358 | 1 | `public double[] getLastHeldUnits()` | Last month's units of each good in HELD, in its order (0.7.58). |
| 360 | 1 | `public double[] getHeldWrittenOff()` | The month's units of each good in HELD destroyed with the yard that held them, in its order, added back before the change was measured (0.7.133). |
| 362 | 1 | `public double getFoodWrittenOff()` | The month's food destroyed with the warehouse that held it, as a volume at fixed weights, added back before the change was measured (live since 0.7.136). |
| 364 | 1 | `public double getMaterialsWrittenOff()` | ...the materials plants' units (0.7.136). |
| 366 | 1 | `public double getLuxuriesWrittenOff()` | ...the luxuries' pieces (0.7.136). |
| 368 | 1 | `public double[] getWrittenOffAddedBack()` | What the month's terms added back of what was written off, at the price each measured the change at - {food, materials, luxuries, HELD's goods} (0.7.136): none in a month that books no change. |
| 369 | 1 | `public boolean isBaselineKnown()` |  |
| 376 | 9 | `public void restoreHeld(double[] lastHeld)` | ...and the units of HELD's goods last month (0.7.58), or null for a save from before them: then the first month books no change in them, rather than booking every fleet in the city as that month's output. |
| 398 | 12 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | Measures the month. |
| 417 | 13 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | ...and with every other good the sectors hold (0.7.58): heldUnits and heldPrices in HELD's order - the units held, every sector's stock and pantry together, and what one costs to bring in today. |
| 438 | 13 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | ...and with the units of HELD's goods written off this month (0.7.133), in HELD's order, or null for none: stock destroyed with the yard that held it, added back before the change is measured as food's write-off is - ... |
| 458 | 216 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | ...and with the materials and the luxuries written off this month (0.7.136, batch FX1), each in its own units beside its stock, as food's is: every term adds its write-off back - see EVERY TERM ADDS ITS WRITE-OFF BACK. |
| 676 | 4 | `private static double held(double[] values, int i)` | One good's figure from an array in HELD's order: 0 past its end, for null, or for anything not a finite number. |
| 691 | 7 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The city's own budget for the month. |
| 711 | 8 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same, with the pension flows. |
| 721 | 10 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again with healthcare but no schools, for the older callers. |
| 744 | 11 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again, with healthcare. |
| 767 | 26 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | ...and with what the city paid to keep a sector alive. |
| 809 | 3 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants)` |  |
| 814 | 4 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums)` | ...and with the health premium beside them (2026-09-19). |
| 820 | 8 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums, double studentLo...` | ...and the student loan interest beside those (2026-09-21). |
| 839 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 845 | 1 | `public double getCentralBankRemittance()` | What the central bank remitted: its profit, which is the interest the city paid itself less what reserves cost. |
| 847 | 1 | `public double getCentralBankInterest()` | What the treasury paid the central bank on its advances. |
| 860 | 4 | `public void setMortgageInsuranceLines(double premiums, double claims)` |  |
| 866 | 1 | `public double getMortgagePremiums()` | The premiums the landlords paid the city's insurance this month - a revenue line. |
| 881 | 1 | `public void setFundTransfer(double transfer)` |  |
| 884 | 1 | `public double getFundTransfer()` | What the city's fund paid the budget this month - a revenue line. |
| 894 | 1 | `public void setFoodAssistance(double paid)` |  |
| 897 | 1 | `public double getFoodAssistance()` | What the treasury paid toward the households' groceries this month - a spending line. |
| 938 | 1 | `public void setCityRepairs(double paid)` | The month's repairs to the city's own buildings, as paid - a spending line (0.7.102). |
| 940 | 1 | `public void setArrearsPaid(double paid)` | What the treasury paid down of what it owed the businesses this month - a spending line (0.7.102). |
| 942 | 4 | `public void setReserveCrude(double bought, double sold)` | The strategic reserve's crude bought and sold at the month's strike - a spending line and a revenue line (0.7.102). |
| 948 | 1 | `public double getCityRepairs()` | What the treasury paid this month to keep the city's own buildings up - a spending line since 0.7.102. |
| 950 | 1 | `public double getArrearsPaid()` | What the treasury paid down this month of what it owed the businesses - a spending line since 0.7.102. |
| 952 | 1 | `public double getReserveCrudeBought()` | The crude the treasury paid for at the month's strike, for the strategic reserve - a spending line since 0.7.102. |
| 954 | 1 | `public double getReserveCrudeSold()` | ...and what the reserve's crude sold for - a revenue line since 0.7.102. |
| 966 | 1 | `public void setFuelDuty(double duty)` |  |
| 969 | 1 | `public double getFuelDuty()` | The fuel duty the drivers paid on their petrol this month - a revenue line (0.7.103). |
| 971 | 1 | `public double getMortgageClaims()` | What the city's insurance paid the bank this month on insured mortgages written down - a spending line. |
| 973 | 1 | `public double getEiPremiums()` |  |
| 975 | 1 | `public double getHealthPremiums()` | What the health premium raised this month - a government revenue line, against the health service's cost. |
| 977 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month - a government revenue line; the principal is not one. |
| 978 | 1 | `public double getEiBenefits()` |  |
| 979 | 1 | `public double getStudentGrants()` |  |
| 983 | 1 | `public void setSafetySpending(double spending)` |  |
| 984 | 1 | `public double getSafetySpending()` |  |
| 1000 | 4 | `public void setTransitLines(double spending, double fares)` |  |
| 1005 | 1 | `public double getTransitSpending()` |  |
| 1006 | 1 | `public double getTransitFares()` |  |
| 1018 | 1 | `public double getSubsidies()` |  |

### THE MONTH THE GOVERNMENT ACTUALLY HAD (lines 1020-1133)

| line | len | member | says |
|---:|---:|---|---|
| 1050 | 38 | `double[] governmentToSave()` |  |
| 1089 | 37 | `void restoreGovernment(double[] saved)` |  |
| 1127 | 1 | `public double getContributions()` |  |
| 1128 | 1 | `public double getPensions()` |  |
| 1129 | 1 | `public double getHealthFees()` |  |
| 1130 | 1 | `public double getHealthSpending()` |  |
| 1131 | 1 | `public double getEducationFees()` |  |
| 1132 | 1 | `public double getEducationSpending()` |  |

### GDP (lines 1134-1170)

| line | len | member | says |
|---:|---:|---|---|
| 1136 | 1 | `public double getConsumption()` |  |
| 1137 | 1 | `public double getInvestment()` |  |
| 1138 | 3 | `public double getNetExports()` |  |
| 1142 | 1 | `public double getConsumptionGoods()` |  |
| 1143 | 1 | `public double getConsumptionHousing()` |  |
| 1144 | 1 | `public double getInvestmentConstruction()` |  |
| 1145 | 1 | `public double getInvestmentInventories()` |  |
| 1146 | 1 | `public double getGovernment()` |  |
| 1147 | 1 | `public double getImportsFood()` |  |
| 1148 | 1 | `public double getImportsMaterials()` |  |
| 1149 | 1 | `public double getImportsRawMaterial()` |  |
| 1150 | 1 | `public double getExports()` |  |
| 1151 | 3 | `public double getTotalImports()` |  |
| 1155 | 1 | `public double getGdp()` |  |
| 1158 | 8 | `public double getAnnualGdp()` | The last twelve months, or as many as there are - not gdp * 12. |
| 1167 | 3 | `public double getGdpPerCapita(long population)` |  |

### growth (lines 1171-1241)

| line | len | member | says |
|---:|---:|---|---|
| 1179 | 9 | `public double getMonthlyGrowthAnnualised()` | Month on month, as an annual rate. |
| 1190 | 8 | `public double getYearOnYearGrowth()` | This month against the same month a year ago. |
| 1200 | 5 | `public double getTrendGdp()` | Average monthly GDP over the last twelve, to smooth a lumpy month. |
| 1206 | 1 | `public int getMonthsRecorded()` |  |
| 1208 | 1 | `public List<Double> getHistory()` |  |
| 1232 | 9 | `public void seedHistory(List<Double> monthly)` | The rolling history put back after a load (0.7.31, the Government spec's B1): the last HISTORY_MONTHS of the graph history's GDP series (HistorySave.getGdp(), a month's GDP kept every month and saved), in place of the... |

### government (lines 1242-1381)

| line | len | member | says |
|---:|---:|---|---|
| 1244 | 1 | `public double getTaxBusiness()` |  |
| 1245 | 1 | `public double getTaxIndustrial()` |  |
| 1246 | 1 | `public double getTaxSales()` |  |
| 1247 | 1 | `public double getTaxWage()` |  |
| 1248 | 1 | `public double getUtilityIncome()` |  |
| 1249 | 1 | `public double getLandSales()` |  |
| 1250 | 1 | `public double getPropertyTax()` |  |
| 1252 | 22 | `public double getTotalRevenue()` |  |
| 1275 | 1 | `public double getInterestExpense()` |  |
| 1276 | 1 | `public double getCapitalSpending()` |  |
| 1277 | 1 | `public double getLandPurchases()` |  |
| 1279 | 16 | `public double getTotalExpenses()` |  |
| 1297 | 3 | `public double getBalance()` | Surplus or deficit - what actually moves the city's cash this month. |
| 1302 | 4 | `public double getRevenueToGdp()` | Revenue as a share of output. |
| 1307 | 4 | `public double getDebtToGdp(double debt)` |  |
| 1312 | 10 | `public void reset()` |  |
| 1343 | 37 | `public void redenominate(double scale)` | The month's national accounts, in the new unit. |

