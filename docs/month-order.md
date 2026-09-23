# The order the month runs in

Generated 2026-09-22 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 3761-4449 (689 lines)

1. **L3762** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L5015)
2. **L3771** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
3. **L3772** `monthsSinceAutosave++;`
4. **L3775** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L271)  
   _The register's month: nothing offered, nothing paid, until it is._
5. **L3776** `exchange.startMonth();` → [Exchange.startMonth](map/Exchange.md) (L358)
6. **L3777** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L728)
7. **L3779** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L6365)
8. **L3796** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
9. **L3824** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
10. **L3825** `cityDiscountForBank = cityDiscountThisMonth;`
11. **L3826** `cityDebtRaisedThisMonth = 0;`
12. **L3827** `cityDiscountThisMonth = 0;`
13. **L3828** `cityPrincipalRepaidThisMonth = 0;`
14. **L3829** `bankPrincipalRepaidThisMonth = 0;`
15. **L3832** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
16. **L3833** `foreignDebtRaisedThisMonth = 0;`
17. **L3834** `foreignPrincipalRepaidThisMonth = 0;`
18. **L3835** `foreignInterestPaidThisMonth = 0;`
19. **L3840** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
20. **L3841** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
21. **L3842** `sectorInvested.clear();`
22. **L3843** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L314)
23. **L3844** `double pooledBefore = 0;`
24. **L3845** `for (double p : poolsBefore) pooledBefore += p;`
25. **L3846** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L993)
26. **L3848** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L514)
27. **L3849** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L1190)
28. **L3850** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L217)
29. **L3861** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
30. **L3862** `buybackAbroad = buybackAbroadUnsettled;`
31. **L3863** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
32. **L3864** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L444)
33. **L3873** `if (debtManager.isAutopilot() && priceIndex.hasRate()) {` → [DebtManager.isAutopilot](map/DebtManager.md) (L105), [PriceIndex.hasRate](map/PriceIndex.md) (L204), [DebtManager.setPolicyRate](map/DebtManager.md) (L68), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L151), [PriceIndex.inflation](map/PriceIndex.md) (L196)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
34. **L3882** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L7316)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
35. **L3887** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L6986)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
36. **L3906** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L7419), [Game.studentGrantBill](map/Game.md) (L5933)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
37. **L3920** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L7444)  
   _...AND EI, THE SAME WAY (0.7.3)._
38. **L3938** `economyManager.setBankTax(bank.chargeTax(` → [EconomyManager.setBankTax](map/EconomyManager.md) (L793), [Bank.chargeTax](map/Bank.md) (L2136), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54), [Game.getSectors](map/Game.md) (L844)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
39. **L3941** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L4451)
40. **L3942** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L30)
41. **L3943** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L4935)
42. **L3944** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L1215)
43. **L3945** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L4888)
44. **L3949** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
45. **L3950** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L6931)
46. **L3951** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
47. **L3952** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L849)
48. **L3956** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L4910)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
49. **L3967** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
50. **L3969** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper;`  
   _The residual buyer (0.7.1): what the households did not pay for._
51. **L3970** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper);` → [Bank.lend](map/Bank.md) (L1360)
52. **L3971** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L1354)
53. **L3987** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L1215), [DebtManager.getAccretedForBank](map/DebtManager.md) (L1244)  
   _THE DISCOUNT ACCRETES (0.7.1)._
54. **L3988** `legacyDiscountDue = 0;`
55. **L3992** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
56. **L3993** `cityDiscountForBank = 0;`
57. **L3995** `double sectorInterestPaid = 0;`
58. **L3996** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L844)
59. **L3997** `bank.takeInterest(interestDue + sectorInterestPaid);` → [Bank.takeInterest](map/Bank.md) (L1196)
60. **L3999** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1583)
61. **L4019** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue);` → [Bank.payRunning](map/Bank.md) (L1392), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L123)  
   _Its own staff, on its own books._
62. **L4020** `bankMaintenanceDue = 0;`
63. **L4028** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L1453), [Bank.openBranches](map/Bank.md) (L1128), [BuildingManager.countByName](map/BuildingManager.md) (L2973)  
   _Capital for whatever branches opened this month._
64. **L4072** `double carryRate = bank.lendingRate(debtManager.getPolicyRate());` → [Bank.lendingRate](map/Bank.md) (L426), [DebtManager.getPolicyRate](map/DebtManager.md) (L66)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
65. **L4073** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L427), [DebtManager.countryPremium](map/DebtManager.md) (L555), [Bank.headroom](map/Bank.md) (L2322)
66. **L4079** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L377)
67. **L4080** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L385)
68. **L4081** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L402), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L480)
69. **L4085** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1583)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, the premium, and what funding costs._
70. **L4104** `final double cityRateNow = debtManager.getRateBeforeStrain();` → [DebtManager.getRateBeforeStrain](map/DebtManager.md) (L774)  
   _...net of the bank's strain premium (0.7.2): see DebtManager.getRateBeforeStrain()._
71. **L4105** `final double premiumNow = debtManager.countryPremium();` → [DebtManager.countryPremium](map/DebtManager.md) (L555)
72. **L4106** `final double gdpNow = economyManager.getMonthGdp();` → [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1210)
73. **L4107** `bank.setDepositMarket(rate -> hotMoney.arrivalsAt(` → [Bank.setDepositMarket](map/Bank.md) (L1787), [CapitalFlows.arrivalsAt](map/CapitalFlows.md) (L276)
74. **L4110** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L1579), [DebtManager.getPolicyRate](map/DebtManager.md) (L66)
75. **L4122** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L272), [Bank.getPlacementIncome](map/Bank.md) (L1881)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
76. **L4123** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L280), [Bank.getFundingCost](map/Bank.md) (L1879)
77. **L4124** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L265), [Bank.getBranches](map/Bank.md) (L2146), [Bank.wholesaleFunding](map/Bank.md) (L1559)
78. **L4128** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L879)  
   _...and if that left it owing more than it owns, it has failed._
79. **L4132** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L2022)  
   _The month is final, so the figure next month's tax is charged on is final too._
80. **L4136** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L350)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
81. **L4153** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L4872)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
82. **L4154** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L1354)
83. **L4164** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3156), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L1518)  
   _...AND THE SAVERS ARE PAID._
84. **L4165** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L1519)
85. **L4172** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L696)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
86. **L4173** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L362), [EconomyManager.setSectorCash](map/EconomyManager.md) (L367), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L698)
87. **L4204** `payDividends();` → [Game.payDividends](map/Game.md) (L1473)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
88. **L4205** `tradeShares();` → [Game.tradeShares](map/Game.md) (L1518)
89. **L4207** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L169), [Bank.depositRate](map/Bank.md) (L1511), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L805)
90. **L4211** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L2621), [Bank.depositRate](map/Bank.md) (L1511), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L805)  
   _...and the households, by the same rule, with what the owners were just paid._
91. **L4214** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L2950), [DebtManager.householdBookYield](map/DebtManager.md) (L1214), [Bank.depositRate](map/Bank.md) (L1511)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
92. **L4249** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
93. **L4250** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L536)
94. **L4251** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L294), [Bank.depositRate](map/Bank.md) (L1511), [DebtManager.getRateBeforeStrain](map/DebtManager.md) (L774), [DebtManager.countryPremium](map/DebtManager.md) (L555), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1210), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L1136), [Game.yearlyDepreciation](map/Game.md) (L6066), [Bank.isInsolvent](map/Bank.md) (L802), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L688)
95. **L4271** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
96. **L4272** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L714)
97. **L4273** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L728)
98. **L4274** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L702), [CapitalFlows.getStock](map/CapitalFlows.md) (L506)
99. **L4276** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L381)
100. **L4286** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L982), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1210)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
101. **L4309** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L277), [PriceIndex.getIndex](map/PriceIndex.md) (L179), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L267), [PriceIndex.inflation](map/PriceIndex.md) (L196), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L314)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
102. **L4326** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L503), [Game.realRateDifferential](map/Game.md) (L3516)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
103. **L4327** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L744)
104. **L4335** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L533), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L700)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
105. **L4350** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L352), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L805)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
106. **L4351** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L1073), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L368), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L805)
107. **L4358** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L1275)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
108. **L4359** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L463), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L959), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L940)
109. **L4360** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L681)
110. **L4361** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L3501)
111. **L4372** `priceIndex.takeMonth(` → [PriceIndex.takeMonth](map/PriceIndex.md) (L102), [Game.getSectors](map/Game.md) (L844)  
   _...AND WHAT THE MONTH COST A FAMILY._
112. **L4387** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L805)  
   _A year of the rate, so next year can tell a drift from a run._
113. **L4388** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
114. **L4390** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L7122)
115. **L4392** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L394)
116. **L4426** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
117. **L4427** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L314)
118. **L4446** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L7250)
119. **L4447** `recordMonth();` → [Game.recordMonth](map/Game.md) (L6313)

## Game.startOfMonthUpdate() - Game.java lines 4451-4672 (222 lines)

1. **L4491** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L237)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L4492** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L230), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L805), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L267)
3. **L4502** `labourMarket.updateCostOfLiving(priceIndex.getIndex());` → [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L327), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _...and wages start chasing what the world now charges._
4. **L4520** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L191), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _what the next parcel costs, are both inputs to everything below._
5. **L4522** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L247), [LandManager.getPricePerSqFt](map/LandManager.md) (L164)
6. **L4531** `debtManager.setBankPremium(bank.ratePremium());` → [DebtManager.setBankPremium](map/DebtManager.md) (L762), [Bank.ratePremium](map/Bank.md) (L1182)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L4539** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L4872)  
   _...AND WHAT THE MONEY COSTS AT ALL, which is a different question from how strained the lender is._
8. **L4543** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L2146), [Bank.isInsolvent](map/Bank.md) (L802)  
   _A failed bank lends nothing._
9. **L4545** `economyManager.updateBusinessCredit(debtManager.getRate());` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L381), [DebtManager.getRate](map/DebtManager.md) (L749)
10. **L4550** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L448)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
11. **L4554** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L2345)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
12. **L4558** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L2314)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
13. **L4572** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L844)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
14. **L4573** `double constructionWorkDone = construction.getRecognisedThisMonth();`
15. **L4582** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L575)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
16. **L4584** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L1017), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L211), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Crime.getGrossCost](map/Crime.md) (L449), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L166), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L168), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L459)
17. **L4633** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
18. **L4634** `monthlyMaterialImportBill = 0;`
19. **L4636** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L987)
20. **L4644** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L91)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
21. **L4652** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L114)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
22. **L4661** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L68)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
23. **L4665** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L679)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
24. **L4669** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L911)  
   _Businesses get their turn: look at demand, forecast it forward, and expand if the new capacity would carry its own debt._
25. **L4671** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L4673)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 30-107 (78 lines)

1. **L63** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L2295)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L64** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L5770), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L2903)
3. **L70** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L2711), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L2936)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L71** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), siteOutput);` → [Game.recogniseSiteWork](map/Game.md) (L2735), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L2945)
5. **L80** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L122)  
   _Roads, before anything reads them._
6. **L87** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L109)  
   _Recount the posts and refresh the wage arrays._
7. **L103** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L5161)  
   _THE POPULATION STEP._
8. **L105** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L118)
9. **L106** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L182)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 109-116 (8 lines)

1. **L110** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L5037)
2. **L111** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L122), [Game.getJobs](map/Game.md) (L5049)
3. **L114** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L6228)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
4. **L115** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L302)

## Game.advanceDemographics() - Game.java lines 5161-5632 (472 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L5166** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L526), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _In REAL terms - see Migration.recordWages()._
2. **L5180** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L5181** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L5663)
4. **L5182** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L5663)
5. **L5183** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L5663)
6. **L5184** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)
7. **L5186** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
8. **L5200** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L5201** `java.util.Arrays.fill(affordable, 1);`
10. **L5202** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L5682)
11. **L5205** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L5206** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L5207** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L5210** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L627)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L5213** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L5214** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)
17. **L5228** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L5241** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L388)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L5252** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L5253** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L5254** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L5255** `double[] inBand = new double[AgeBand.values().length];`
23. **L5256** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
24. **L5260** `double[] careFactors = mortalityFactors;`
25. **L5261** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L446), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
26. **L5271** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L5274** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L5281** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L5282** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
30. **L5283** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L410)
31. **L5285** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L144), [Healthcare.birthFactor](map/Healthcare.md) (L407)
32. **L5287** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L258), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
33. **L5291** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L5292** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L90)
35. **L5293** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
36. **L5294** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L92)
37. **L5295** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L5296** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L482), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
39. **L5299** `lastOrphanDeaths = outsideDead[0];`
40. **L5300** `lastUnhousedDeaths = outsideDead[1];`
41. **L5307** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L342), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L5310** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L5323** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L5330** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L803), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3031), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L5339** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L299), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L332)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L5343** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L340), [Crime.getRateVsCanada](map/Crime.md) (L433)  
   _...and what last month's crime says about living here._
47. **L5345** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L314), [Migration.monthlyNet](map/Migration.md) (L720), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135), [Game.getHouseholdCapacity](map/Game.md) (L5041), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L3193), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)
48. **L5360** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L117), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
49. **L5372** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L6253)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
50. **L5374** `double[] jobsByTier = new double[PayTier.values().length];`
51. **L5375** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
52. **L5376** `int[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L814)
53. **L5378** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
54. **L5390** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
55. **L5391** `double[] wageByTier = new double[PayTier.values().length];`
56. **L5392** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209)
57. **L5393** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
58. **L5396** `for (int t = 0; t < wageByTier.length; t++) {`
59. **L5412** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3667)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
60. **L5413** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L651)
61. **L5414** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L242), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
62. **L5415** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L284), [PopulationCohorts.total](map/PopulationCohorts.md) (L95), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3667), [Game.unskilledWage](map/Game.md) (L5909)
63. **L5422** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L382), [Crime.getAdmitted](map/Crime.md) (L422)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
64. **L5424** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L487), [Crime.prisoners](map/Crime.md) (L367)
65. **L5425** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
66. **L5427** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L788)
67. **L5428** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L493)
68. **L5429** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L5909), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
69. **L5433** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L435), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)  
   _The month's adult arrivals look for work next month._
70. **L5443** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L367)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
71. **L5444** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
72. **L5448** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3016), [Education.getFinished](map/Education.md) (L757)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
73. **L5460** `int[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L3214)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
74. **L5461** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
75. **L5462** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1278)
76. **L5463** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1365), [FamilyModel.house](map/FamilyModel.md) (L1128)
77. **L5468** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L844), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
78. **L5472** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L765)  
   _And the advisor prices a new home on who would move into it._
79. **L5473** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L770)
80. **L5489** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1492), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L739)  
   _...and the ones who cannot afford one either._
81. **L5491** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1523), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L790), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
82. **L5530** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L145), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
83. **L5531** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L465), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L82), [Game.burialShare](map/Game.md) (L5744), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L3561), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)
84. **L5541** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L910), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Healthcare.getFees](map/Healthcare.md) (L713)
85. **L5555** `education.setTuitionScale(economyManager.getTaxPolicy().getTuitionScale());` → [Education.setTuitionScale](map/Education.md) (L132), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's._
86. **L5556** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L338), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L3631), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [Migration.getLastDepartures](map/Migration.md) (L440)
87. **L5569** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L710), [Education.getGraduates](map/Education.md) (L695)
88. **L5570** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L636), [Education.getLicences](map/Education.md) (L698)
89. **L5573** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L492), [Education.getStudying](map/Education.md) (L505)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
90. **L5576** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L678)  
   _A licence holder is a graduate first, and the graduate count has just moved._
91. **L5578** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L919), [Education.getGrossCost](map/Education.md) (L738), [Education.getFees](map/Education.md) (L746)
92. **L5584** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L359), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832)  
   _6c._
93. **L5588** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L932), [Crime.getGrossCost](map/Crime.md) (L449)
94. **L5602** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L162), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _6d._
95. **L5603** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L957), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [Game.getInfrastructureManager](map/Game.md) (L854), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
96. **L5614** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L738), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3128), [Game.unhousedShareOfCity](map/Game.md) (L5645), [Crime.getInjuredShare](map/Crime.md) (L413)  
   _7._
97. **L5630** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 118-180 (63 lines)

1. **L121** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135)  
   _TBE_
2. **L122** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)
3. **L123** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L3251)
4. **L124** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L272), [Game.getFamilies](map/Game.md) (L5133)
5. **L130** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L269), [Game.getFamilies](map/Game.md) (L5133)  
   _The two inputs to the rent price, which is a market now._
6. **L133** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L273), [Game.getFamilies](map/Game.md) (L5133)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L137** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L270), [Game.marginalHousingCost](map/Game.md) (L7691)
8. **L139** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L975), [Game.getCohorts](map/Game.md) (L5132)
9. **L145** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L93), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L146** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L105), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L3903)
11. **L149** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L163)
12. **L156** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L87), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L188)  
   _The split behind that total._
13. **L157** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L149), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L219)
14. **L158** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L150), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L223)
15. **L173** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L152), [Game.getInfrastructureManager](map/Game.md) (L854)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L176** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L180), [Game.getHealth](map/Game.md) (L5136)  
   _The fourth ratio._
17. **L177** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L758)

## SimulationEngine.updateServices() - SimulationEngine.java lines 182-189 (8 lines)

1. **L183** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L185), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172)
2. **L184** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L203), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
3. **L187** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L260), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L188** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L33)

## Game.run() - Game.java lines 362-365 (4 lines)

1. **L363** `initialize();` → [Game.initialize](map/Game.md) (L367)
2. **L364** `foundingBank();` → [Game.foundingBank](map/Game.md) (L458)

