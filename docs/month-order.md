# The order the month runs in

Generated 2026-09-18 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 3442-3982 (541 lines)

1. **L3443** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L4503)
2. **L3445** `if (cash < 0) {` → [Game.issueEmergencyDebt](map/Game.md) (L6267)
3. **L3485** `month++;`
4. **L3486** `monthsSinceAutosave++;`
5. **L3489** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L271)  
   _The register's month: nothing offered, nothing paid, until it is._
6. **L3490** `exchange.startMonth();` → [Exchange.startMonth](map/Exchange.md) (L358)
7. **L3491** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L728)
8. **L3493** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L5661)
9. **L3510** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
10. **L3511** `cityDebtRaisedThisMonth = 0;`
11. **L3512** `cityDiscountThisMonth = 0;`
12. **L3513** `cityPrincipalRepaidThisMonth = 0;`
13. **L3514** `foreignDebtRaisedThisMonth = 0;`
14. **L3515** `foreignPrincipalRepaidThisMonth = 0;`
15. **L3516** `foreignInterestPaidThisMonth = 0;`
16. **L3521** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
17. **L3522** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
18. **L3523** `sectorInvested.clear();`
19. **L3524** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L278)
20. **L3525** `double pooledBefore = 0;`
21. **L3526** `for (double p : poolsBefore) pooledBefore += p;`
22. **L3527** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L945)
23. **L3529** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L497)
24. **L3530** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L844)
25. **L3548** `economyManager.setBankTax(bank.chargeTax(` → [EconomyManager.setBankTax](map/EconomyManager.md) (L793), [Bank.chargeTax](map/Bank.md) (L1917), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54), [Game.getSectors](map/Game.md) (L745)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
26. **L3550** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`
27. **L3551** `cityDiscountForBank = cityDiscountThisMonth;`
28. **L3553** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L3984)
29. **L3554** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L30)
30. **L3555** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L4462)
31. **L3556** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L1163)
32. **L3557** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L4421)
33. **L3558** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L683)
34. **L3562** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L4441)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
35. **L3573** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
36. **L3574** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank);` → [Bank.lend](map/Bank.md) (L1210)
37. **L3575** `bank.takeRepayment(lender.getRepaidThisMonth() + cityPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L1204)
38. **L3581** `bank.takeDiscount(cityDiscountForBank);` → [Bank.takeDiscount](map/Bank.md) (L1197)  
   _The discount on this month's issuance, recognised as it is earned._
39. **L3583** `double sectorInterestPaid = 0;`
40. **L3584** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L745)
41. **L3585** `bank.takeInterest(interestDue + sectorInterestPaid);` → [Bank.takeInterest](map/Bank.md) (L1178)
42. **L3587** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1413)
43. **L3606** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue);` → [Bank.payRunning](map/Bank.md) (L1242), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L123)  
   _Its own staff, on its own books._
44. **L3607** `bankMaintenanceDue = 0;`
45. **L3615** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L1283), [Bank.openBranches](map/Bank.md) (L1110), [BuildingManager.countByName](map/BuildingManager.md) (L2973)  
   _Capital for whatever branches opened this month._
46. **L3649** `double carryRate = bank.lendingRate(debtManager.getRate());` → [Bank.lendingRate](map/Bank.md) (L411), [DebtManager.getRate](map/DebtManager.md) (L606)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
47. **L3650** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L412), [DebtManager.countryPremium](map/DebtManager.md) (L477), [Bank.headroom](map/Bank.md) (L2102)
48. **L3656** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L363)
49. **L3657** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L371)
50. **L3658** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L387), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L465)
51. **L3662** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1413)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, the premium, and what funding costs._
52. **L3680** `final double cityRateNow = debtManager.getRate();` → [DebtManager.getRate](map/DebtManager.md) (L606)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE, handed to the bank as a function rather than as five numbers._
53. **L3681** `final double premiumNow = debtManager.countryPremium();` → [DebtManager.countryPremium](map/DebtManager.md) (L477)
54. **L3682** `final double gdpNow = economyManager.getMonthGdp();` → [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1158)
55. **L3683** `bank.setDepositMarket(rate -> hotMoney.arrivalsAt(` → [Bank.setDepositMarket](map/Bank.md) (L1575), [CapitalFlows.arrivalsAt](map/CapitalFlows.md) (L272)
56. **L3686** `bank.fundToCover(debtManager.getRate());` → [Bank.fundToCover](map/Bank.md) (L1408), [DebtManager.getRate](map/DebtManager.md) (L606)
57. **L3690** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L858)  
   _...and if that left it owing more than it owns, it has failed._
58. **L3694** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L1803)  
   _The month is final, so the figure next month's tax is charged on is final too._
59. **L3711** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L4415)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
60. **L3712** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L920)
61. **L3722** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L2529), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L1350)  
   _...AND THE SAVERS ARE PAID._
62. **L3723** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L1351)
63. **L3730** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L696)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
64. **L3731** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L362), [EconomyManager.setSectorCash](map/EconomyManager.md) (L367), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L698)
65. **L3762** `payDividends();` → [Game.payDividends](map/Game.md) (L1303)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
66. **L3763** `tradeShares();` → [Game.tradeShares](map/Game.md) (L1348)
67. **L3765** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L169), [Bank.depositRate](map/Bank.md) (L1348), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L502)
68. **L3769** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L2254), [Bank.depositRate](map/Bank.md) (L1348), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L502)  
   _...and the households, by the same rule, with what the owners were just paid._
69. **L3804** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L490)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
70. **L3805** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L520)
71. **L3806** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L278), [Bank.depositRate](map/Bank.md) (L1348), [DebtManager.getRate](map/DebtManager.md) (L606), [DebtManager.countryPremium](map/DebtManager.md) (L477), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1158), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L789), [Game.yearlyDepreciation](map/Game.md) (L5402), [Bank.isInsolvent](map/Bank.md) (L789), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L551)
72. **L3826** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L490)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
73. **L3827** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L677)
74. **L3828** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L691)
75. **L3829** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L665), [CapitalFlows.getStock](map/CapitalFlows.md) (L490)
76. **L3831** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L310)
77. **L3841** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L647), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1158)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
78. **L3863** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L226), [PriceIndex.getIndex](map/PriceIndex.md) (L179), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L268), [PriceIndex.inflation](map/PriceIndex.md) (L196), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L313)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
79. **L3873** `foreign.setRateDifferential(` → [ForeignAccounts.setRateDifferential](map/ForeignAccounts.md) (L392), [DebtManager.getPolicyRate](map/DebtManager.md) (L49)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD._
80. **L3875** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L445)
81. **L3890** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L275), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L502)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
82. **L3891** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L729), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L291), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L502)
83. **L3892** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L386), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L616), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L605)
84. **L3893** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L544)
85. **L3894** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L3260)
86. **L3905** `priceIndex.takeMonth(` → [PriceIndex.takeMonth](map/PriceIndex.md) (L102), [Game.getSectors](map/Game.md) (L745)  
   _...AND WHAT THE MONTH COST A FAMILY._
87. **L3920** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L502)  
   _A year of the rate, so next year can tell a drift from a run._
88. **L3921** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
89. **L3923** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L6087)
90. **L3925** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L337)
91. **L3959** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
92. **L3960** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L278)
93. **L3979** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L6180)
94. **L3980** `recordMonth();` → [Game.recordMonth](map/Game.md) (L5609)

## Game.startOfMonthUpdate() - Game.java lines 3984-4205 (222 lines)

1. **L4024** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L238)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L4025** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L230), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L502), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L268)
3. **L4035** `labourMarket.updateCostOfLiving(priceIndex.getIndex());` → [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L323), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _...and wages start chasing what the world now charges._
4. **L4053** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L192), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _what the next parcel costs, are both inputs to everything below._
5. **L4055** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L247), [LandManager.getPricePerSqFt](map/LandManager.md) (L165)
6. **L4064** `debtManager.setBankPremium(bank.ratePremium());` → [DebtManager.setBankPremium](map/DebtManager.md) (L619), [Bank.ratePremium](map/Bank.md) (L1164)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L4072** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L4415)  
   _...AND WHAT THE MONEY COSTS AT ALL, which is a different question from how strained the lender is._
8. **L4076** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L1927), [Bank.isInsolvent](map/Bank.md) (L789)  
   _A failed bank lends nothing._
9. **L4078** `economyManager.updateBusinessCredit(debtManager.getRate());` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L381), [DebtManager.getRate](map/DebtManager.md) (L606)
10. **L4083** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L448)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
11. **L4087** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L2159)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
12. **L4091** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L2153)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
13. **L4105** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L745)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
14. **L4106** `double constructionWorkDone = construction.getRecognisedThisMonth();`
15. **L4115** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L575)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
16. **L4117** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L969), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L211), [Healthcare.getGrossCost](map/Healthcare.md) (L513), [Crime.getGrossCost](map/Crime.md) (L449), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L167), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L169), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L459)
17. **L4166** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
18. **L4167** `monthlyMaterialImportBill = 0;`
19. **L4169** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L888)
20. **L4177** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L91)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
21. **L4185** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L114)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
22. **L4194** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L68)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
23. **L4198** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L679)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
24. **L4202** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L812)  
   _Businesses get their turn: look at demand, forecast it forward, and expand if the new capacity would carry its own debt._
25. **L4204** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L4206)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 30-107 (78 lines)

1. **L63** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L2109)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L64** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L5169), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L2903)
3. **L70** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L2501), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L2936)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L71** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), siteOutput);` → [Game.recogniseSiteWork](map/Game.md) (L2525), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L2945)
5. **L80** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L122)  
   _Roads, before anything reads them._
6. **L87** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L109)  
   _Recount the posts and refresh the wage arrays._
7. **L103** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L4658)  
   _THE POPULATION STEP._
8. **L105** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L118)
9. **L106** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L182)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 109-116 (8 lines)

1. **L110** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L4525)
2. **L111** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L122), [Game.getJobs](map/Game.md) (L4537)
3. **L114** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L5524)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
4. **L115** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L302)

## Game.advanceDemographics() - Game.java lines 4658-5087 (430 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L4663** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L526), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _In REAL terms - see Migration.recordWages()._
2. **L4677** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L4678** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L5118)
4. **L4679** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L5118)
5. **L4680** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L5118)
6. **L4681** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3610)
7. **L4683** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
8. **L4685** `double[] served = new double[CareType.values().length];`
9. **L4686** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3610)
10. **L4703** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L339)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
11. **L4714** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
12. **L4715** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
13. **L4716** `double[] orphansByBand = new double[AgeBand.values().length];`
14. **L4717** `double[] inBand = new double[AgeBand.values().length];`
15. **L4718** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
16. **L4722** `double[] careFactors = mortalityFactors;`
17. **L4723** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L429), [Healthcare.mortalityFactors](map/Healthcare.md) (L339)
18. **L4733** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L296)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
19. **L4736** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
20. **L4743** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
21. **L4744** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
22. **L4745** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L410)
23. **L4747** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L144), [Healthcare.birthFactor](map/Healthcare.md) (L358)
24. **L4749** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L258), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
25. **L4753** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
26. **L4754** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L90)
27. **L4755** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
28. **L4756** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L92)
29. **L4757** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
30. **L4758** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L465), [Healthcare.mortalityFactors](map/Healthcare.md) (L339)
31. **L4761** `lastOrphanDeaths = outsideDead[0];`
32. **L4762** `lastUnhousedDeaths = outsideDead[1];`
33. **L4769** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L342), [Unemployment.takeEvicted](map/Unemployment.md) (L208)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
34. **L4772** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
35. **L4785** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
36. **L4792** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L812), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L2410), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
37. **L4801** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L299), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L313)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
38. **L4805** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L340), [Crime.getRateVsCanada](map/Crime.md) (L433)  
   _...and what last month's crime says about living here._
39. **L4807** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L314), [Migration.monthlyNet](map/Migration.md) (L720), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135), [Game.getHouseholdCapacity](map/Game.md) (L4529), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L3193), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)
40. **L4822** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L117), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
41. **L4834** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L5549)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
42. **L4836** `double[] jobsByTier = new double[PayTier.values().length];`
43. **L4837** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
44. **L4838** `int[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L814)
45. **L4840** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
46. **L4852** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
47. **L4853** `double[] wageByTier = new double[PayTier.values().length];`
48. **L4854** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209)
49. **L4855** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
50. **L4858** `for (int t = 0; t < wageByTier.length; t++) {`
51. **L4874** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3684)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
52. **L4875** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L484)
53. **L4876** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L242), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
54. **L4877** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L284), [PopulationCohorts.total](map/PopulationCohorts.md) (L95), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3684), [Game.unskilledWage](map/Game.md) (L5304)
55. **L4884** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L234), [Crime.getPressure](map/Crime.md) (L382), [Crime.getAdmitted](map/Crime.md) (L422)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
56. **L4886** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L487), [Crime.prisoners](map/Crime.md) (L367)
57. **L4887** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
58. **L4889** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L788)
59. **L4890** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L493)
60. **L4891** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L291), [Game.unskilledWage](map/Game.md) (L5304), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
61. **L4895** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L275), [Migration.getLastArrivals](map/Migration.md) (L435), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)  
   _The month's adult arrivals look for work next month._
62. **L4905** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L367)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
63. **L4906** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
64. **L4910** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L2395), [Education.getFinished](map/Education.md) (L696)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
65. **L4922** `int[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L3214)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
66. **L4923** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1133)
67. **L4924** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1278)
68. **L4925** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1365), [FamilyModel.house](map/FamilyModel.md) (L1133)
69. **L4930** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L745), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L992), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L993)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
70. **L4934** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L765)  
   _And the advisor prices a new home on who would move into it._
71. **L4935** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L770)
72. **L4951** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1492), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L633)  
   _...and the ones who cannot afford one either._
73. **L4953** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1523), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L690), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
74. **L4957** `economyManager.setOutsidePayments(unemployment.getBenefitsPaid(),` → [EconomyManager.setOutsidePayments](map/EconomyManager.md) (L845), [Unemployment.getBenefitsPaid](map/Unemployment.md) (L183), [FamilyModel.getSeekers](map/FamilyModel.md) (L209), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54), [Game.unskilledWage](map/Game.md) (L5304)  
   _What EI and the grants cost the treasury this month, before the cash moves._
75. **L4989** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L416), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3760), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3849), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L82), [Game.burialShare](map/Game.md) (L5143), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L3578), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3610)  
   _6._
76. **L4999** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L862), [Healthcare.getGrossCost](map/Healthcare.md) (L513), [Healthcare.getFees](map/Healthcare.md) (L515)
77. **L5011** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L277), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L3648), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3760), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3849), [Migration.getLastDepartures](map/Migration.md) (L440)  
   _6b._
78. **L5024** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L718), [Education.getGraduates](map/Education.md) (L634)
79. **L5025** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L636), [Education.getLicences](map/Education.md) (L637)
80. **L5028** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L492), [Education.getStudying](map/Education.md) (L444)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
81. **L5031** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L678)  
   _A licence holder is a graduate first, and the graduate count has just moved._
82. **L5033** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L871), [Education.getGrossCost](map/Education.md) (L677), [Education.getFees](map/Education.md) (L685)
83. **L5039** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L359), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3760), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3849)  
   _6c._
84. **L5043** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L884), [Crime.getGrossCost](map/Crime.md) (L449)
85. **L5057** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L162), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _6d._
86. **L5058** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L909), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3760), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3849), [Game.getInfrastructureManager](map/Game.md) (L755), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
87. **L5069** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L540), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L2501), [Game.unhousedShareOfCity](map/Game.md) (L5100), [Crime.getInjuredShare](map/Crime.md) (L413)  
   _7._
88. **L5085** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L296)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 118-180 (63 lines)

1. **L121** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135)  
   _TBE_
2. **L122** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)
3. **L123** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L3251)
4. **L124** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L272), [Game.getFamilies](map/Game.md) (L4621)
5. **L130** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L269), [Game.getFamilies](map/Game.md) (L4621)  
   _The two inputs to the rent price, which is a market now._
6. **L133** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L273), [Game.getFamilies](map/Game.md) (L4621)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L137** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L270), [Game.marginalHousingCost](map/Game.md) (L6365)
8. **L139** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L927), [Game.getCohorts](map/Game.md) (L4620)
9. **L145** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L93), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L146** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L105), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L3908)
11. **L149** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L163)
12. **L156** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L87), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L188)  
   _The split behind that total._
13. **L157** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L149), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L219)
14. **L158** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L150), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L223)
15. **L173** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L152), [Game.getInfrastructureManager](map/Game.md) (L755)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L176** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L180), [Game.getHealth](map/Game.md) (L4624)  
   _The fourth ratio._
17. **L177** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L758)

## SimulationEngine.updateServices() - SimulationEngine.java lines 182-189 (8 lines)

1. **L183** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L185), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172)
2. **L184** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L203), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
3. **L187** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L260), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L188** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L33)

## Game.run() - Game.java lines 309-312 (4 lines)

1. **L310** `initialize();` → [Game.initialize](map/Game.md) (L314)
2. **L311** `foundingBank();` → [Game.foundingBank](map/Game.md) (L405)

