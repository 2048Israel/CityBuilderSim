# The order the month runs in

Generated 2026-09-24 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 4729-5429 (701 lines)

1. **L4730** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L6030)
2. **L4739** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
3. **L4740** `monthsSinceAutosave++;`
4. **L4743** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L290)  
   _The register's month: nothing offered, nothing paid, until it is._
5. **L4744** `exchange.startMonth();` → [Exchange.startMonth](map/Exchange.md) (L374)
6. **L4745** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L760)
7. **L4747** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L7413)
8. **L4764** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
9. **L4792** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
10. **L4793** `cityDiscountForBank = cityDiscountThisMonth;`
11. **L4794** `cityDebtRaisedThisMonth = 0;`
12. **L4795** `cityDiscountThisMonth = 0;`
13. **L4796** `cityPrincipalRepaidThisMonth = 0;`
14. **L4797** `bankPrincipalRepaidThisMonth = 0;`
15. **L4800** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
16. **L4801** `foreignDebtRaisedThisMonth = 0;`
17. **L4802** `foreignPrincipalRepaidThisMonth = 0;`
18. **L4803** `foreignInterestPaidThisMonth = 0;`
19. **L4808** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
20. **L4809** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
21. **L4810** `sectorInvested.clear();`
22. **L4811** `salvageThisMonth.clear();`
23. **L4812** `salvageBySector.clear();`
24. **L4813** `salvageUsedThisMonth = 0;`
25. **L4814** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L314)
26. **L4815** `double pooledBefore = 0;`
27. **L4816** `for (double p : poolsBefore) pooledBefore += p;`
28. **L4817** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L1025)
29. **L4819** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L911)
30. **L4820** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L1193)
31. **L4821** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L217)
32. **L4832** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
33. **L4833** `buybackAbroad = buybackAbroadUnsettled;`
34. **L4834** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
35. **L4835** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L444)
36. **L4844** `if (debtManager.isAutopilot() && priceIndex.hasRate()) {` → [DebtManager.isAutopilot](map/DebtManager.md) (L105), [PriceIndex.hasRate](map/PriceIndex.md) (L204), [DebtManager.setPolicyRate](map/DebtManager.md) (L68), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L185), [PriceIndex.inflation](map/PriceIndex.md) (L196)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
37. **L4853** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L8392)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
38. **L4858** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L8062)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
39. **L4877** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L8495), [Game.studentGrantBill](map/Game.md) (L6980)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
40. **L4891** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L8520)  
   _...AND EI, THE SAME WAY (0.7.3)._
41. **L4909** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L825), [Bank.chargeTax](map/Bank.md) (L2794), [Game.bankProfitTaxRate](map/Game.md) (L1812)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
42. **L4911** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L5431)
43. **L4912** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L30)
44. **L4913** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L5950)
45. **L4914** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L1264)
46. **L4915** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L5900)
47. **L4919** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
48. **L4920** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L8007)
49. **L4921** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
50. **L4922** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L882)
51. **L4926** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L5922)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
52. **L4937** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
53. **L4939** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper;`  
   _The residual buyer (0.7.1): what the households did not pay for._
54. **L4940** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper);` → [Bank.lend](map/Bank.md) (L1911)
55. **L4943** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L2020)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
56. **L4944** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L1905)
57. **L4960** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L1764), [DebtManager.getAccretedForBank](map/DebtManager.md) (L1272)  
   _THE DISCOUNT ACCRETES (0.7.1)._
58. **L4961** `legacyDiscountDue = 0;`
59. **L4965** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
60. **L4966** `cityDiscountForBank = 0;`
61. **L4968** `double sectorInterestPaid = 0;`
62. **L4969** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L1015)
63. **L4972** `bank.takeInterest(interestDue, sectorInterestPaid);` → [Bank.takeInterest](map/Bank.md) (L1745)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
64. **L4974** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1959)
65. **L4977** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L2050)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
66. **L4997** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue);` → [Bank.payRunning](map/Bank.md) (L1962), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L123)  
   _Its own staff, on its own books._
67. **L4998** `bankMaintenanceDue = 0;`
68. **L5006** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L1795), [Bank.openBranches](map/Bank.md) (L1684), [BuildingManager.countByName](map/BuildingManager.md) (L2976)  
   _Capital for whatever branches opened this month._
69. **L5051** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L776), [DebtManager.getPolicyRate](map/DebtManager.md) (L66)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
70. **L5052** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L427), [DebtManager.countryPremium](map/DebtManager.md) (L601), [Bank.headroom](map/Bank.md) (L4097)
71. **L5058** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L461)
72. **L5059** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L469)
73. **L5060** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L486), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L480)
74. **L5064** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1959)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
75. **L5074** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L2195), [DebtManager.getPolicyRate](map/DebtManager.md) (L66)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
76. **L5087** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L272), [Bank.getPlacementIncome](map/Bank.md) (L2455)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
77. **L5088** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L280), [Bank.getFundingCost](map/Bank.md) (L2453)
78. **L5089** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L265), [Bank.getBranches](map/Bank.md) (L3836), [Bank.wholesaleFunding](map/Bank.md) (L2175)
79. **L5093** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1413)  
   _...and if that left it owing more than it owns, it has failed._
80. **L5097** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L2615)  
   _The month is final, so the figure next month's tax is charged on is final too._
81. **L5101** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L350)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
82. **L5118** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L5886)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
83. **L5119** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L1384)
84. **L5129** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3251), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L2134)  
   _...AND THE SAVERS ARE PAID._
85. **L5130** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L2135)
86. **L5137** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L728)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
87. **L5138** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L375), [EconomyManager.setSectorCash](map/EconomyManager.md) (L380), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L730)
88. **L5169** `payDividends();` → [Game.payDividends](map/Game.md) (L1842)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
89. **L5170** `tradeShares();` → [Game.tradeShares](map/Game.md) (L1891)
90. **L5172** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L169), [Bank.depositRate](map/Bank.md) (L2127), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)
91. **L5176** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L2715), [Bank.depositRate](map/Bank.md) (L2127), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _...and the households, by the same rule, with what the owners were just paid._
92. **L5179** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3044), [DebtManager.householdBookYield](map/DebtManager.md) (L1242), [Bank.depositRate](map/Bank.md) (L2127)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
93. **L5192** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1413)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
94. **L5227** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
95. **L5228** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L536)
96. **L5229** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L294), [Bank.depositRate](map/Bank.md) (L2127), [DebtManager.getRate](map/DebtManager.md) (L796), [DebtManager.countryPremium](map/DebtManager.md) (L601), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1258), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L1139), [Game.yearlyDepreciation](map/Game.md) (L7113), [Bank.isInsolvent](map/Bank.md) (L1333), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L734)
97. **L5251** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
98. **L5252** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1228)
99. **L5253** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1242)
100. **L5254** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1216), [CapitalFlows.getStock](map/CapitalFlows.md) (L506)
101. **L5256** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L381)
102. **L5266** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L985), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1258)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
103. **L5289** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L279), [PriceIndex.getIndex](map/PriceIndex.md) (L179), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293), [PriceIndex.inflation](map/PriceIndex.md) (L196), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L340)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
104. **L5306** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L505), [Game.realRateDifferential](map/Game.md) (L4484)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
105. **L5307** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L746)
106. **L5315** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L533), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L702)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
107. **L5330** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L398), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
108. **L5331** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L1076), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L414), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)
109. **L5338** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L1394)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
110. **L5339** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L509), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L962), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L943)
111. **L5340** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L727)
112. **L5341** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L4469)
113. **L5352** `priceIndex.takeMonth(` → [PriceIndex.takeMonth](map/PriceIndex.md) (L102), [Game.getSectors](map/Game.md) (L1015)  
   _...AND WHAT THE MONTH COST A FAMILY._
114. **L5367** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _A year of the rate, so next year can tell a drift from a run._
115. **L5368** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
116. **L5370** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L8198)
117. **L5372** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L471)
118. **L5406** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
119. **L5407** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L314)
120. **L5426** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L8326)
121. **L5427** `recordMonth();` → [Game.recordMonth](map/Game.md) (L7360)

## Game.startOfMonthUpdate() - Game.java lines 5431-5668 (238 lines)

1. **L5471** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L263)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L5472** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L230), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293)
3. **L5482** `labourMarket.updateCostOfLiving(priceIndex.getIndex());` → [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L327), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _...and wages start chasing what the world now charges._
4. **L5500** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L230), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _what the next parcel costs, are both inputs to everything below._
5. **L5502** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L247), [LandManager.getPricePerSqFt](map/LandManager.md) (L194)
6. **L5512** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L5886)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L5517** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L3836), [Bank.isInsolvent](map/Bank.md) (L1333)  
   _A failed bank lends nothing._
8. **L5528** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L3610)  
   _...AND HOW FAR A STANDING ONE WILL LEND ON ITS CAPITAL (0.7.8): the bank's one rule, read once and handed to every desk that lends this month - the businesses' and the families' - before any of the..._
9. **L5529** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L3638)
10. **L5534** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L3317)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
11. **L5535** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L580)
12. **L5540** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L398), [Bank.prime](map/Bank.md) (L722), [DebtManager.getPolicyRate](map/DebtManager.md) (L66), [Bank.insuredMortgageRate](map/Bank.md) (L743)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
13. **L5546** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L475)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
14. **L5550** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L3182)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
15. **L5554** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L3151)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
16. **L5568** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L1015)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
17. **L5569** `double constructionWorkDone = construction.getRecognisedThisMonth();`
18. **L5578** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L602)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
19. **L5580** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L1049), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L211), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Crime.getGrossCost](map/Crime.md) (L449), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L196), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L198), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L486)
20. **L5629** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
21. **L5630** `monthlyMaterialImportBill = 0;`
22. **L5632** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L1299)
23. **L5640** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L91)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
24. **L5648** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L114)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
25. **L5657** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L68)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
26. **L5661** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L709)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
27. **L5665** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L1196)  
   _Businesses get their turn: look at demand, forecast it forward, and expand if the new capacity would carry its own debt._
28. **L5667** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L5669)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 30-107 (78 lines)

1. **L63** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L3132)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L64** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L6799), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L2906)
3. **L70** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L3553), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L2939)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L71** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), siteOutput);` → [Game.recogniseSiteWork](map/Game.md) (L3577), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L2948)
5. **L80** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L122)  
   _Roads, before anything reads them._
6. **L87** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L109)  
   _Recount the posts and refresh the wage arrays._
7. **L103** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L6176)  
   _THE POPULATION STEP._
8. **L105** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L118)
9. **L106** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L182)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 109-116 (8 lines)

1. **L110** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L6052)
2. **L111** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L122), [Game.getJobs](map/Game.md) (L6064)
3. **L114** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L7275)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
4. **L115** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L302)

## Game.advanceDemographics() - Game.java lines 6176-6661 (486 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L6181** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L526), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _In REAL terms - see Migration.recordWages()._
2. **L6195** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L6196** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L6692)
4. **L6197** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L6692)
5. **L6198** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L6692)
6. **L6199** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3596)
7. **L6201** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
8. **L6215** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L6216** `java.util.Arrays.fill(affordable, 1);`
10. **L6217** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L6711)
11. **L6220** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L6221** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L6222** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L6225** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L627)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L6228** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L6229** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3596)
17. **L6243** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L6256** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L388)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L6267** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L6268** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L6269** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L6270** `double[] inBand = new double[AgeBand.values().length];`
23. **L6271** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
24. **L6275** `double[] careFactors = mortalityFactors;`
25. **L6276** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L446), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
26. **L6286** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L6289** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L6296** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L6297** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
30. **L6298** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L410)
31. **L6300** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L144), [Healthcare.birthFactor](map/Healthcare.md) (L407)
32. **L6302** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L258), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
33. **L6306** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L6307** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L90)
35. **L6308** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
36. **L6309** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L92)
37. **L6310** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L6311** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L482), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
39. **L6314** `lastOrphanDeaths = outsideDead[0];`
40. **L6315** `lastUnhousedDeaths = outsideDead[1];`
41. **L6322** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L342), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L6325** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L6338** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L6345** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L803), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3125), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L6354** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L299), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L358)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L6358** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L340), [Crime.getRateVsCanada](map/Crime.md) (L433)  
   _...and what last month's crime says about living here._
47. **L6360** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L314), [Migration.monthlyNet](map/Migration.md) (L720), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135), [Game.getHouseholdCapacity](map/Game.md) (L6056), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L3196), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)
48. **L6375** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L117), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
49. **L6387** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L7300)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
50. **L6389** `double[] jobsByTier = new double[PayTier.values().length];`
51. **L6390** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
52. **L6391** `int[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L814)
53. **L6393** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
54. **L6405** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
55. **L6406** `double[] wageByTier = new double[PayTier.values().length];`
56. **L6407** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209)
57. **L6408** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
58. **L6411** `for (int t = 0; t < wageByTier.length; t++) {`
59. **L6427** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3670)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
60. **L6428** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L724)
61. **L6429** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L242), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
62. **L6430** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L284), [PopulationCohorts.total](map/PopulationCohorts.md) (L95), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3670), [Game.unskilledWage](map/Game.md) (L6956)
63. **L6437** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L382), [Crime.getAdmitted](map/Crime.md) (L422)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
64. **L6439** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L487), [Crime.prisoners](map/Crime.md) (L367)
65. **L6440** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
66. **L6442** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L788)
67. **L6443** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L493)
68. **L6444** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L6956), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
69. **L6448** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L435), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)  
   _The month's adult arrivals look for work next month._
70. **L6458** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L367)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
71. **L6459** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
72. **L6463** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3110), [Education.getFinished](map/Education.md) (L832)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
73. **L6475** `int[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L3217)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
74. **L6476** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
75. **L6477** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1278)
76. **L6478** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1365), [FamilyModel.house](map/FamilyModel.md) (L1128)
77. **L6483** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L1015), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
78. **L6487** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L780)  
   _And the advisor prices a new home on who would move into it._
79. **L6488** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L785)
80. **L6504** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1492), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L765)  
   _...and the ones who cannot afford one either._
81. **L6506** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1523), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L816), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
82. **L6545** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L145), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
83. **L6546** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L465), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3746), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3835), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L82), [Game.burialShare](map/Game.md) (L6773), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L3564), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3596)
84. **L6556** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L942), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Healthcare.getFees](map/Healthcare.md) (L713)
85. **L6571** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L1324), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
86. **L6572** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L370), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L3634), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3746), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3835), [Migration.getLastDepartures](map/Migration.md) (L440)
87. **L6591** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L807), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L3797), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L3810)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
88. **L6598** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L710), [Education.getGraduates](map/Education.md) (L730)
89. **L6599** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L636), [Education.getLicences](map/Education.md) (L733)
90. **L6602** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L492), [Education.getStudying](map/Education.md) (L539)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
91. **L6605** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L678)  
   _A licence holder is a graduate first, and the graduate count has just moved._
92. **L6607** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L951), [Education.getGrossCost](map/Education.md) (L773), [Education.getFees](map/Education.md) (L781)
93. **L6613** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L359), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3746), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3835)  
   _6c._
94. **L6617** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L964), [Crime.getGrossCost](map/Crime.md) (L449)
95. **L6631** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L162), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _6d._
96. **L6632** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L989), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3746), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3835), [Game.getInfrastructureManager](map/Game.md) (L1025), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
97. **L6643** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L738), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3223), [Game.unhousedShareOfCity](map/Game.md) (L6674), [Crime.getInjuredShare](map/Crime.md) (L413)  
   _7._
98. **L6659** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 118-180 (63 lines)

1. **L121** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135)  
   _TBE_
2. **L122** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)
3. **L123** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L3254)
4. **L124** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L272), [Game.getFamilies](map/Game.md) (L6148)
5. **L130** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L269), [Game.getFamilies](map/Game.md) (L6148)  
   _The two inputs to the rent price, which is a market now._
6. **L133** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L273), [Game.getFamilies](map/Game.md) (L6148)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L137** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L270), [Game.marginalHousingCost](map/Game.md) (L8767)
8. **L139** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L1007), [Game.getCohorts](map/Game.md) (L6147)
9. **L145** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L93), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L146** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L105), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L3906)
11. **L149** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L163)
12. **L156** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L87), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L188)  
   _The split behind that total._
13. **L157** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L149), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L219)
14. **L158** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L150), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L223)
15. **L173** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L152), [Game.getInfrastructureManager](map/Game.md) (L1025)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L176** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L180), [Game.getHealth](map/Game.md) (L6151)  
   _The fourth ratio._
17. **L177** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L790)

## SimulationEngine.updateServices() - SimulationEngine.java lines 182-189 (8 lines)

1. **L183** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L185), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172)
2. **L184** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L203), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
3. **L187** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L260), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L188** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L33)

## Game.run() - Game.java lines 438-441 (4 lines)

1. **L439** `initialize();` → [Game.initialize](map/Game.md) (L443)
2. **L440** `foundingBank();` → [Game.foundingBank](map/Game.md) (L545)

