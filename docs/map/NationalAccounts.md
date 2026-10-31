# NationalAccounts.java - 1,520 lines · 117 methods · 6 constants · model

`ham/citybuildersim/NationalAccounts.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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

**Uses:** [Good](Good.md) (27)

**Used by (35):** [AutoBuildCheck](AutoBuildCheck.md), [AutoBuilder](AutoBuilder.md), [BankScreen](BankScreen.md), [CityNeeds](CityNeeds.md), [CopperMiningCheck](CopperMiningCheck.md), [CopperRefiningCheck](CopperRefiningCheck.md), [DeskCheck](DeskCheck.md), [EconomyManager](EconomyManager.md), [EducationCheck](EducationCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [FuelSplit](FuelSplit.md), [FundCheck](FundCheck.md), [Game](Game.md), [GdpCheck](GdpCheck.md), [GovernmentScreen](GovernmentScreen.md), [HealthCheck](HealthCheck.md), [HistoryCheck](HistoryCheck.md), [HistorySave](HistorySave.md), [HouseholdCheck](HouseholdCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [NewGameCheck](NewGameCheck.md), [OilCheck](OilCheck.md), [Pieces](Pieces.md), [PolicyScreen](PolicyScreen.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SlopeCheck](SlopeCheck.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 85 | · GDP components |
| 117 | · government income |
| 146 | EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c) |
| 518 | · AND THE BOUTIQUE'S STOCKROOM IS THE FOURTH TERM (2026-09-17) |
| 552 | · A YARD SOLD BACK FULL IS WRITTEN OFF, NOT UNPRODUCED (0.7.133, |
| 577 | · EVERY TERM ADDS ITS WRITE-OFF BACK (0.7.136, batch FX1) |
| 962 | · THE CITY'S PAPER ON AMORTISED COST (CD3, spec-city-debt.md section 5) |
| 1000 | · THE INFLATION UPLIFT ON LINKED BONDS (CD5, spec-city-debt.md 5.3-5.4) |
| 1124 | THE MONTH THE GOVERNMENT ACTUALLY HAD |
| 1251 | · GDP |
| 1288 | · growth |
| 1359 | · government |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 83 | `NationalAccounts.HISTORY_MONTHS` | `120` | How many months of GDP the rolling history keeps - ten years - and the most a load seeds it with (seedHistory()). |
| 231 | `NationalAccounts.HELD` | `{ Good.CROPS, Good.VANS, Good.ROLLING_STOCK, Good.PETROL, Good.DIESEL, Good.L...` | The goods the fifth term measures, in the order the save keeps their units (slots 15 on, EconomyManager.getNationalAccountsState()): a new one goes on the end. |
| 238 | `NationalAccounts.HELD_BEFORE_FUEL` | `3` | ...and how many of them a save from 0.7.58 to 0.7.61 carries: the three before FUEL, which such a city held none of. |
| 241 | `NationalAccounts.HELD_WITH_FUEL` | `4` | ...and how many a save from 0.7.62 to 0.7.75 carries: the three and FUEL, which FuelSplit makes PETROL and DIESEL. |
| 244 | `NationalAccounts.NOT_HELD` | `Good.CARS` | ...and the one good a sector holds that no term measures, and why: see EVERY OTHER GOOD A SECTOR HOLDS. |
| 1152 | `NationalAccounts.GOVERNMENT_SLOTS_WITH_GRANTS` | `20` | The slots a block carries once EI and the grants are on it (2026-09-11): the grant bill is saved[19]. |

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
| 246 | `private final double[] lastHeldUnits` |  |
| 249 | `private final double[] heldWrittenOffUnits` | The month's units of each good in HELD written off with the yard that held them (0.7.133): a flow, read in the month it is handed in. |
| 252 | `private double foodWrittenOffVolume, materialsWrittenOffUnits, luxuriesWrittenOffUnits` | ...and the first three terms' (0.7.136, batch FX1): the food written off, as the fixed-weight volume foodUnits is; the materials and the luxuries, in units. |
| 255 | `private final double[] writtenOffAddedBack` | What each term added back of the month's write-offs, at the price it measured the change at: food, materials, luxuries, HELD's goods (0.7.136). |
| 258 | `private boolean heldBaselineKnown` | Whether lastHeldUnits is a real baseline: false only after loading a save from before 0.7.58, whose first month books no change in these goods. |
| 280 | `private double invFood` | The three parts of the inventory term, kept so a diagnostic can say which one moved rather than leaving the reader to infer it from the total. |
| 281 | `private double invMaterials` |  |
| 282 | `private double invLuxuries` |  |
| 283 | `private double invHeld` |  |
| 298 | `private boolean inventoryBaselineKnown` | Whether last month's stock is actually known. |
| 801 | `private double contributions` | Pension contributions in, pensions out. |
| 802 | `private double pensions` |  |
| 805 | `private double eiPremiums` | EI premiums in; EI benefits and student grants out. |
| 806 | `private double eiBenefits` |  |
| 807 | `private double studentGrants` |  |
| 810 | `private double healthPremiums` | The health premium off every wage, in; a revenue line beside the EI premium (2026-09-19). |
| 813 | `private double studentLoanInterest` | Interest the graduates paid on their student loans, in; a revenue line beside the premiums (2026-09-21). |
| 843 | `private double centralBankRemittance, centralBankInterest` | THE CENTRAL BANK'S TWO LINES (0.7.0): its profit remitted to the treasury, in; the interest the treasury paid it on its advances, out. |
| 864 | `private double mortgagePremiums, mortgageClaims` | THE CITY'S MORTGAGE INSURANCE (0.7.11): the premiums the landlords paid on the mortgages written this month, in; what the insurance paid the bank on insured mortgages the month's defaults wrote down, out. |
| 885 | `private double fundTransfer` | THE TRANSFER FROM THE CITY'S FUND (0.7.14): the withdrawal dial's share of the fund's value (0.7.48; by default a twelfth of TreasuryFund.TRANSFER_RATE, Norway's fiscal rule), paid from its cash and over the default f... |
| 898 | `private double foodAssistance` | FOOD ASSISTANCE (0.7.43): the vouchers the treasury paid toward the households' groceries, a transfer like EI - a spending line, with its own setter for the fund transfer's reason: Game moves the cash where it is paid... |
| 941 | `private double cityRepairs, arrearsPaid, reserveCrudeBought, reserveCrudeSold` | EVERY MONTHLY TREASURY FLOW INSIDE THE BUDGET'S TOTALS (0.7.102, Jerus's A15: "rec plus maintenance ... |
| 986 | `private double interestNotCash, retiredGain` |  |
| 1028 | `private double inflationUplift, upliftPaid` |  |
| 1053 | `private double fuelDuty` | THE FUEL DUTY (0.7.103, batch P4; Jerus's A7): a duty a litre on the drivers' petrol at the pump (TaxPolicy, THE FUEL DUTY), a revenue line of its own - in the cash through the tax take, as the fares are, and set besi... |
| 1068 | `private double portDues` | THE PORT DUES (FR3, docs/queue/FR3.md; D9): what Freight's Ports division paid the city on the tonnes across its berths, a revenue line of its own - in the cash through the tax take, as the fuel duty is, from the divi... |
| 1086 | `private double safetySpending` | The police and the prisons: payroll and upkeep. |
| 1102 | `private double transitFares, transitSpending` | Fares in, the buses' and trams' wage bill out. |
| 1113 | `private double healthFees` | Patient and funeral fees in, the health service's bill out. |
| 1114 | `private double healthSpending` |  |
| 1117 | `private double educationFees` | Tuition in, and what the schools cost. |
| 1118 | `private double educationSpending` |  |
| 1121 | `private double subsidies` | What the city paid to hold a loss-making sector at break-even. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 80 | 1441 | **type** `public class NationalAccounts` | The city's GDP, measured properly, plus the government's own books. |

### GDP components (lines 85-116)

### government income (lines 117-145)

### EVERY OTHER GOOD A SECTOR HOLDS IS THE FIFTH TERM (0.7.58, batch J1c) (lines 146-961)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 28 | `public void restore(double gdp, double lastFoodVolume, double consumptionGoods, double consumptionHousing, double investmentCon...` | Puts back the month a save was taken in. |
| 354 | 1 | `public double getLastFoodVolume()` | Last month's food inventory, as a VOLUME. |
| 355 | 1 | `public double getLastMaterialUnits()` |  |
| 356 | 1 | `public double getLastLuxuryUnits()` |  |
| 357 | 1 | `public double getInvFood()` |  |
| 358 | 1 | `public double getInventoryFood()` |  |
| 359 | 1 | `public double getInventoryMaterials()` |  |
| 360 | 1 | `public double getInventoryLuxuries()` |  |
| 362 | 1 | `public double getInventoryHeld()` | The fifth term: the change in every good in HELD that the sectors hold, priced at what one costs to bring in (0.7.58). |
| 364 | 1 | `public double[] getLastHeldUnits()` | Last month's units of each good in HELD, in its order (0.7.58). |
| 366 | 1 | `public double[] getHeldWrittenOff()` | The month's units of each good in HELD destroyed with the yard that held them, in its order, added back before the change was measured (0.7.133). |
| 368 | 1 | `public double getFoodWrittenOff()` | The month's food destroyed with the warehouse that held it, as a volume at fixed weights, added back before the change was measured (live since 0.7.136). |
| 370 | 1 | `public double getMaterialsWrittenOff()` | ...the materials plants' units (0.7.136). |
| 372 | 1 | `public double getLuxuriesWrittenOff()` | ...the luxuries' pieces (0.7.136). |
| 374 | 1 | `public double[] getWrittenOffAddedBack()` | What the month's terms added back of what was written off, at the price each measured the change at - {food, materials, luxuries, HELD's goods} (0.7.136): none in a month that books no change. |
| 375 | 1 | `public boolean isBaselineKnown()` |  |
| 382 | 9 | `public void restoreHeld(double[] lastHeld)` | ...and the units of HELD's goods last month (0.7.58), or null for a save from before them: then the first month books no change in them, rather than booking every fleet in the city as that month's output. |
| 404 | 12 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | Measures the month. |
| 423 | 13 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | ...and with every other good the sectors hold (0.7.58): heldUnits and heldPrices in HELD's order - the units held, every sector's stock and pantry together, and what one costs to bring in today. |
| 444 | 13 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | ...and with the units of HELD's goods written off this month (0.7.133), in HELD's order, or null for none: stock destroyed with the yard that held it, added back before the change is measured as food's write-off is - ... |
| 464 | 216 | `public void update(double retailSales, double rentPaid, double constructionWorkDone, double foodUnits, double foodStockWrittenO...` | ...and with the materials and the luxuries written off this month (0.7.136, batch FX1), each in its own units beside its stock, as food's is: every term adds its write-off back - see EVERY TERM ADDS ITS WRITE-OFF BACK. |
| 682 | 4 | `private static double held(double[] values, int i)` | One good's figure from an array in HELD's order: 0 past its end, for null, or for anything not a finite number. |
| 697 | 7 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The city's own budget for the month. |
| 717 | 8 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same, with the pension flows. |
| 727 | 10 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again with healthcare but no schools, for the older callers. |
| 750 | 11 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | The same again, with healthcare. |
| 773 | 26 | `public void updateGovernment(double business, double industrial, double sales, double wage, double utilities, double land, doub...` | ...and with what the city paid to keep a sector alive. |
| 815 | 3 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants)` |  |
| 820 | 4 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums)` | ...and with the health premium beside them (2026-09-19). |
| 826 | 8 | `public void setOutsideLines(double eiPremiums, double eiBenefits, double studentGrants, double healthPremiums, double studentLo...` | ...and the student loan interest beside those (2026-09-21). |
| 845 | 4 | `public void setCentralBankLines(double remittance, double interest)` |  |
| 851 | 1 | `public double getCentralBankRemittance()` | What the central bank remitted: its profit, which is the interest the city paid itself less what reserves cost. |
| 853 | 1 | `public double getCentralBankInterest()` | What the treasury paid the central bank on its advances. |
| 866 | 4 | `public void setMortgageInsuranceLines(double premiums, double claims)` |  |
| 872 | 1 | `public double getMortgagePremiums()` | The premiums the landlords paid the city's insurance this month - a revenue line. |
| 887 | 1 | `public void setFundTransfer(double transfer)` |  |
| 890 | 1 | `public double getFundTransfer()` | What the city's fund paid the budget this month - a revenue line. |
| 900 | 1 | `public void setFoodAssistance(double paid)` |  |
| 903 | 1 | `public double getFoodAssistance()` | What the treasury paid toward the households' groceries this month - a spending line. |
| 944 | 1 | `public void setCityRepairs(double paid)` | The month's repairs to the city's own buildings, as paid - a spending line (0.7.102). |
| 946 | 1 | `public void setArrearsPaid(double paid)` | What the treasury paid down of what it owed the businesses this month - a spending line (0.7.102). |
| 948 | 4 | `public void setReserveCrude(double bought, double sold)` | The strategic reserve's crude bought and sold at the month's strike - a spending line and a revenue line (0.7.102). |
| 954 | 1 | `public double getCityRepairs()` | What the treasury paid this month to keep the city's own buildings up - a spending line since 0.7.102. |
| 956 | 1 | `public double getArrearsPaid()` | What the treasury paid down this month of what it owed the businesses - a spending line since 0.7.102. |
| 958 | 1 | `public double getReserveCrudeBought()` | The crude the treasury paid for at the month's strike, for the strategic reserve - a spending line since 0.7.102. |
| 960 | 1 | `public double getReserveCrudeSold()` | ...and what the reserve's crude sold for - a revenue line since 0.7.102. |

### THE CITY'S PAPER ON AMORTISED COST (CD3, spec-city-debt.md section 5) (lines 962-999)

| line | len | member | says |
|---:|---:|---|---|
| 989 | 4 | `public void setDebtBooks(double interestNotCash, double retiredGain)` | The month's amortisation and the gain on paper retired in its window, as the strike hands them over. |
| 995 | 1 | `public double getInterestNotCash()` | The discount the month amortised on the city's paper: the non-cash part of its interest (below nothing for a premium). |
| 998 | 1 | `public double getRetiredGain()` | The gain (below nothing, a loss) on the paper the city retired in the window: the carrying value less the price. |

### THE INFLATION UPLIFT ON LINKED BONDS (CD5, spec-city-debt.md 5.3-5.4) (lines 1000-1123)

| line | len | member | says |
|---:|---:|---|---|
| 1031 | 4 | `public void setInflationUplift(double inflationUplift, double upliftPaid)` | The month's indexation booked and the uplift paid in cash, as the strike hands them over. |
| 1037 | 1 | `public double getInflationUplift()` | The month's inflation uplift on the linked bonds, booked: an expense of the budget, not cash; below nothing in a deflation month. |
| 1040 | 1 | `public double getUpliftPaidInCash()` | The uplift the month paid in cash: a linker's last month, the indexed principal over its face. |
| 1043 | 1 | `public double getUpliftNotCash()` | The part of the uplift that is not cash: what was booked less what was paid. |
| 1055 | 1 | `public void setFuelDuty(double duty)` |  |
| 1058 | 1 | `public double getFuelDuty()` | The fuel duty the drivers paid on their petrol this month - a revenue line (0.7.103). |
| 1070 | 1 | `public void setPortDues(double dues)` |  |
| 1073 | 1 | `public double getPortDues()` | The port dues Freight paid on its berths' tonnes this month - a revenue line (FR3). |
| 1075 | 1 | `public double getMortgageClaims()` | What the city's insurance paid the bank this month on insured mortgages written down - a spending line. |
| 1077 | 1 | `public double getEiPremiums()` |  |
| 1079 | 1 | `public double getHealthPremiums()` | What the health premium raised this month - a government revenue line, against the health service's cost. |
| 1081 | 1 | `public double getStudentLoanInterest()` | What the graduates paid in interest on their student loans this month - a government revenue line; the principal is not one. |
| 1082 | 1 | `public double getEiBenefits()` |  |
| 1083 | 1 | `public double getStudentGrants()` |  |
| 1087 | 1 | `public void setSafetySpending(double spending)` |  |
| 1088 | 1 | `public double getSafetySpending()` |  |
| 1104 | 4 | `public void setTransitLines(double spending, double fares)` |  |
| 1109 | 1 | `public double getTransitSpending()` |  |
| 1110 | 1 | `public double getTransitFares()` |  |
| 1122 | 1 | `public double getSubsidies()` |  |

### THE MONTH THE GOVERNMENT ACTUALLY HAD (lines 1124-1250)

| line | len | member | says |
|---:|---:|---|---|
| 1154 | 45 | `double[] governmentToSave()` |  |
| 1200 | 43 | `void restoreGovernment(double[] saved)` |  |
| 1244 | 1 | `public double getContributions()` |  |
| 1245 | 1 | `public double getPensions()` |  |
| 1246 | 1 | `public double getHealthFees()` |  |
| 1247 | 1 | `public double getHealthSpending()` |  |
| 1248 | 1 | `public double getEducationFees()` |  |
| 1249 | 1 | `public double getEducationSpending()` |  |

### GDP (lines 1251-1287)

| line | len | member | says |
|---:|---:|---|---|
| 1253 | 1 | `public double getConsumption()` |  |
| 1254 | 1 | `public double getInvestment()` |  |
| 1255 | 3 | `public double getNetExports()` |  |
| 1259 | 1 | `public double getConsumptionGoods()` |  |
| 1260 | 1 | `public double getConsumptionHousing()` |  |
| 1261 | 1 | `public double getInvestmentConstruction()` |  |
| 1262 | 1 | `public double getInvestmentInventories()` |  |
| 1263 | 1 | `public double getGovernment()` |  |
| 1264 | 1 | `public double getImportsFood()` |  |
| 1265 | 1 | `public double getImportsMaterials()` |  |
| 1266 | 1 | `public double getImportsRawMaterial()` |  |
| 1267 | 1 | `public double getExports()` |  |
| 1268 | 3 | `public double getTotalImports()` |  |
| 1272 | 1 | `public double getGdp()` |  |
| 1275 | 8 | `public double getAnnualGdp()` | The last twelve months, or as many as there are - not gdp * 12. |
| 1284 | 3 | `public double getGdpPerCapita(long population)` |  |

### growth (lines 1288-1358)

| line | len | member | says |
|---:|---:|---|---|
| 1296 | 9 | `public double getMonthlyGrowthAnnualised()` | Month on month, as an annual rate. |
| 1307 | 8 | `public double getYearOnYearGrowth()` | This month against the same month a year ago. |
| 1317 | 5 | `public double getTrendGdp()` | Average monthly GDP over the last twelve, to smooth a lumpy month. |
| 1323 | 1 | `public int getMonthsRecorded()` |  |
| 1325 | 1 | `public List<Double> getHistory()` |  |
| 1349 | 9 | `public void seedHistory(List<Double> monthly)` | The rolling history put back after a load (0.7.31, the Government spec's B1): the last HISTORY_MONTHS of the graph history's GDP series (HistorySave.getGdp(), a month's GDP kept every month and saved), in place of the... |

### government (lines 1359-1520)

| line | len | member | says |
|---:|---:|---|---|
| 1361 | 1 | `public double getTaxBusiness()` |  |
| 1362 | 1 | `public double getTaxIndustrial()` |  |
| 1363 | 1 | `public double getTaxSales()` |  |
| 1364 | 1 | `public double getTaxWage()` |  |
| 1365 | 1 | `public double getUtilityIncome()` |  |
| 1366 | 1 | `public double getLandSales()` |  |
| 1367 | 1 | `public double getPropertyTax()` |  |
| 1369 | 24 | `public double getTotalRevenue()` |  |
| 1400 | 1 | `public double getInterestExpense()` | The budget's "Interest on the city's paper" (CD3): the effective interest - the cash coupons the month paid (interestExpense) and the discount it amortised (interestNotCash) together. |
| 1403 | 1 | `public double getInterestPaidInCash()` | The cash part of it: the coupons the month paid, every holder's, the dollar paper's at the month's rate. |
| 1404 | 1 | `public double getCapitalSpending()` |  |
| 1405 | 1 | `public double getLandPurchases()` |  |
| 1407 | 18 | `public double getTotalExpenses()` |  |
| 1434 | 3 | `public double getBalance()` | Surplus or deficit - the month's result. |
| 1439 | 4 | `public double getRevenueToGdp()` | Revenue as a share of output. |
| 1444 | 4 | `public double getDebtToGdp(double debt)` |  |
| 1449 | 10 | `public void reset()` |  |
| 1480 | 39 | `public void redenominate(double scale)` | The month's national accounts, in the new unit. |

