# The order the month runs in

Generated 2026-09-26 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 5514-6238 (725 lines)

1. **L5521** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L5403)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
2. **L5523** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L6848)
3. **L5532** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
4. **L5533** `monthsSinceAutosave++;`
5. **L5536** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L316)  
   _The register's month: nothing offered, nothing paid, until it is._
6. **L5537** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L372)
7. **L5538** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1021)
8. **L5539** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L939)
9. **L5541** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L8253)
10. **L5558** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
11. **L5586** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
12. **L5587** `cityDiscountForBank = cityDiscountThisMonth;`
13. **L5588** `cityDebtRaisedThisMonth = 0;`
14. **L5589** `cityDiscountThisMonth = 0;`
15. **L5590** `cityPrincipalRepaidThisMonth = 0;`
16. **L5591** `bankPrincipalRepaidThisMonth = 0;`
17. **L5594** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
18. **L5595** `foreignDebtRaisedThisMonth = 0;`
19. **L5596** `foreignPrincipalRepaidThisMonth = 0;`
20. **L5597** `foreignInterestPaidThisMonth = 0;`
21. **L5602** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
22. **L5603** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
23. **L5604** `sectorInvested.clear();`
24. **L5605** `salvageThisMonth.clear();`
25. **L5606** `salvageBySector.clear();`
26. **L5607** `salvageUsedThisMonth = 0;`
27. **L5608** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L314)
28. **L5609** `double pooledBefore = 0;`
29. **L5610** `for (double p : poolsBefore) pooledBefore += p;`
30. **L5611** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L1204)
31. **L5613** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1311)
32. **L5614** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L1193)
33. **L5615** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L217)
34. **L5626** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
35. **L5627** `buybackAbroad = buybackAbroadUnsettled;`
36. **L5628** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
37. **L5629** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L444)
38. **L5638** `if (debtManager.isAutopilot() && priceIndex.hasRate()) {` → [DebtManager.isAutopilot](map/DebtManager.md) (L105), [PriceIndex.hasRate](map/PriceIndex.md) (L204), [DebtManager.setPolicyRate](map/DebtManager.md) (L68), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L185), [PriceIndex.inflation](map/PriceIndex.md) (L196)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
39. **L5647** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L9253)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
40. **L5652** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L8923)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
41. **L5671** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L9356), [Game.studentGrantBill](map/Game.md) (L7798)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
42. **L5685** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L9381)  
   _...AND EI, THE SAME WAY (0.7.3)._
43. **L5703** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1004), [Bank.chargeTax](map/Bank.md) (L3224), [Game.bankProfitTaxRate](map/Game.md) (L1987)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
44. **L5705** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L6240)
45. **L5706** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L30)
46. **L5707** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L6768)
47. **L5708** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L1448)
48. **L5709** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L6718)
49. **L5713** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
50. **L5714** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L8868)
51. **L5715** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
52. **L5716** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L882)
53. **L5720** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L6740)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
54. **L5731** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
55. **L5733** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper;`  
   _The residual buyer (0.7.1): what the households did not pay for._
56. **L5734** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper);` → [Bank.lend](map/Bank.md) (L2341)
57. **L5737** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L2450)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
58. **L5738** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L2335)
59. **L5754** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2194), [DebtManager.getAccretedForBank](map/DebtManager.md) (L1272)  
   _THE DISCOUNT ACCRETES (0.7.1)._
60. **L5755** `legacyDiscountDue = 0;`
61. **L5759** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
62. **L5760** `cityDiscountForBank = 0;`
63. **L5762** `double sectorInterestPaid = 0;`
64. **L5763** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L1048)
65. **L5769** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2175), [BondMarket.getCouponsStruck](map/BondMarket.md) (L927)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
66. **L5770** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L559), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L934)
67. **L5772** `refreshBank();` → [Game.refreshBank](map/Game.md) (L2211)
68. **L5775** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L2377)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
69. **L5795** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue);` → [Bank.payRunning](map/Bank.md) (L2392), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L123)  
   _Its own staff, on its own books._
70. **L5796** `bankMaintenanceDue = 0;`
71. **L5804** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L1970), [Bank.openBranches](map/Bank.md) (L2114), [BuildingManager.countByName](map/BuildingManager.md) (L2976)  
   _Capital for whatever branches opened this month._
72. **L5849** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1176), [DebtManager.getPolicyRate](map/DebtManager.md) (L66)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
73. **L5850** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L427), [DebtManager.countryPremium](map/DebtManager.md) (L601), [Bank.headroom](map/Bank.md) (L4586)
74. **L5856** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L595)
75. **L5857** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L603)
76. **L5858** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L620), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L480)
77. **L5862** `refreshBank();` → [Game.refreshBank](map/Game.md) (L2211)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
78. **L5872** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L2625), [DebtManager.getPolicyRate](map/DebtManager.md) (L66)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
79. **L5885** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L272), [Bank.getPlacementIncome](map/Bank.md) (L2885)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
80. **L5886** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L280), [Bank.getFundingCost](map/Bank.md) (L2883)
81. **L5887** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L265), [Bank.getBranches](map/Bank.md) (L4325), [Bank.wholesaleFunding](map/Bank.md) (L2605)
82. **L5891** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1843)  
   _...and if that left it owing more than it owns, it has failed._
83. **L5895** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3045)  
   _The month is final, so the figure next month's tax is charged on is final too._
84. **L5899** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L350)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
85. **L5916** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L6704)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
86. **L5917** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L1384)
87. **L5927** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3475), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L2564)  
   _...AND THE SAVERS ARE PAID._
88. **L5928** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L2565)
89. **L5935** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L840)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
90. **L5936** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L375), [EconomyManager.setSectorCash](map/EconomyManager.md) (L380), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L842)
91. **L5967** `payDividends();` → [Game.payDividends](map/Game.md) (L2085)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
92. **L5968** `tradeShares();` → [Game.tradeShares](map/Game.md) (L2136)
93. **L5978** `bondMarket.takeMonth(month);` → [BondMarket.takeMonth](map/BondMarket.md) (L1052)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
94. **L5979** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L2320)
95. **L5981** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L169), [Bank.depositRate](map/Bank.md) (L2557), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)
96. **L5985** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L2680), [Bank.depositRate](map/Bank.md) (L2557), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _...and the households, by the same rule, with what the owners were just paid._
97. **L5988** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3009), [DebtManager.householdBookYield](map/DebtManager.md) (L1242), [Bank.depositRate](map/Bank.md) (L2557)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
98. **L6001** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1843)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
99. **L6036** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
100. **L6037** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L536)
101. **L6038** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L294), [Bank.depositRate](map/Bank.md) (L2557), [DebtManager.getRate](map/DebtManager.md) (L796), [DebtManager.countryPremium](map/DebtManager.md) (L601), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1442), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L1139), [Game.yearlyDepreciation](map/Game.md) (L7953), [Bank.isInsolvent](map/Bank.md) (L1763), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L734)
102. **L6060** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
103. **L6061** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1658)
104. **L6062** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1672)
105. **L6063** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1646), [CapitalFlows.getStock](map/CapitalFlows.md) (L506)
106. **L6065** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L381)
107. **L6075** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L985), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1442)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
108. **L6098** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L279), [PriceIndex.getIndex](map/PriceIndex.md) (L179), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293), [PriceIndex.inflation](map/PriceIndex.md) (L196), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L340)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
109. **L6115** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L505), [Game.realRateDifferential](map/Game.md) (L4951)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
110. **L6116** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L746)
111. **L6124** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L533), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L702)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
112. **L6139** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L398), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
113. **L6140** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L1076), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L414), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)
114. **L6147** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L1394)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
115. **L6148** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L509), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L962), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L943)
116. **L6149** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L727)
117. **L6150** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L4936)
118. **L6161** `priceIndex.takeMonth(` → [PriceIndex.takeMonth](map/PriceIndex.md) (L102), [Game.getSectors](map/Game.md) (L1048)  
   _...AND WHAT THE MONTH COST A FAMILY._
119. **L6176** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _A year of the rate, so next year can tell a drift from a run._
120. **L6177** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
121. **L6179** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L9059)
122. **L6181** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L472)
123. **L6215** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
124. **L6216** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L314)
125. **L6235** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L9187)
126. **L6236** `recordMonth();` → [Game.recordMonth](map/Game.md) (L8200)

## Game.startOfMonthUpdate() - Game.java lines 6240-6486 (247 lines)

1. **L6280** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L263)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L6281** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L230), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293)
3. **L6291** `labourMarket.updateCostOfLiving(priceIndex.getIndex());` → [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L327), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _...and wages start chasing what the world now charges._
4. **L6309** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L257), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _what the next parcel costs, are both inputs to everything below._
5. **L6311** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L247), [LandManager.getPricePerSqFt](map/LandManager.md) (L221)
6. **L6321** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L6704)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L6326** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L4325), [Bank.isInsolvent](map/Bank.md) (L1763)  
   _A failed bank lends nothing._
8. **L6339** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L2320)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
9. **L6340** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4094)
10. **L6341** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4122)
11. **L6346** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L3801)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
12. **L6347** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L580)
13. **L6354** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L2360)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
14. **L6355** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L398), [Bank.prime](map/Bank.md) (L1122), [DebtManager.getPolicyRate](map/DebtManager.md) (L66), [Bank.insuredMortgageRate](map/Bank.md) (L1143)
15. **L6359** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L892)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
16. **L6364** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L496)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
17. **L6368** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L3638)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
18. **L6372** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L3607)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
19. **L6386** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L1048)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
20. **L6387** `double constructionWorkDone = construction.getRecognisedThisMonth();`
21. **L6396** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L623)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
22. **L6398** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L1228), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L211), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Crime.getGrossCost](map/Crime.md) (L449), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L223), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L225), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L507)
23. **L6447** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
24. **L6448** `monthlyMaterialImportBill = 0;`
25. **L6450** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L1474)
26. **L6458** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L91)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
27. **L6466** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L114)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
28. **L6475** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L68)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
29. **L6479** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L785)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
30. **L6483** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L1367)  
   _Businesses get their turn: look at demand, forecast it forward, and expand if the new capacity would carry its own debt._
31. **L6485** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L6487)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 30-107 (78 lines)

1. **L63** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L3588)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L64** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L7617), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L2906)
3. **L70** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L4009), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L2939)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L71** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), siteOutput);` → [Game.recogniseSiteWork](map/Game.md) (L4033), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L2948)
5. **L80** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L122)  
   _Roads, before anything reads them._
6. **L87** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L109)  
   _Recount the posts and refresh the wage arrays._
7. **L103** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L6994)  
   _THE POPULATION STEP._
8. **L105** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L118)
9. **L106** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L182)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 109-116 (8 lines)

1. **L110** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L6870)
2. **L111** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L122), [Game.getJobs](map/Game.md) (L6882)
3. **L114** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L8115)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
4. **L115** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L302)

## Game.advanceDemographics() - Game.java lines 6994-7479 (486 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L6999** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L526), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _In REAL terms - see Migration.recordWages()._
2. **L7013** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L7014** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L7510)
4. **L7015** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L7510)
5. **L7016** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L7510)
6. **L7017** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3596)
7. **L7019** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
8. **L7033** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L7034** `java.util.Arrays.fill(affordable, 1);`
10. **L7035** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L7529)
11. **L7038** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L7039** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L7040** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L7043** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L627)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L7046** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L7047** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3596)
17. **L7061** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L7074** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L388)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L7085** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L7086** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L7087** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L7088** `double[] inBand = new double[AgeBand.values().length];`
23. **L7089** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
24. **L7093** `double[] careFactors = mortalityFactors;`
25. **L7094** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L446), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
26. **L7104** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L7107** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L7114** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L7115** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
30. **L7116** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L410)
31. **L7118** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L144), [Healthcare.birthFactor](map/Healthcare.md) (L407)
32. **L7120** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L258), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
33. **L7124** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L7125** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L90)
35. **L7126** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
36. **L7127** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L92)
37. **L7128** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L7129** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L482), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
39. **L7132** `lastOrphanDeaths = outsideDead[0];`
40. **L7133** `lastUnhousedDeaths = outsideDead[1];`
41. **L7140** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L342), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L7143** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L7156** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L7163** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L803), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3349), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L7172** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L299), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L358)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L7176** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L340), [Crime.getRateVsCanada](map/Crime.md) (L433)  
   _...and what last month's crime says about living here._
47. **L7178** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L314), [Migration.monthlyNet](map/Migration.md) (L720), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135), [Game.getHouseholdCapacity](map/Game.md) (L6874), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L3196), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)
48. **L7193** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L117), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
49. **L7205** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L8140)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
50. **L7207** `double[] jobsByTier = new double[PayTier.values().length];`
51. **L7208** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
52. **L7209** `int[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L814)
53. **L7211** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
54. **L7223** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
55. **L7224** `double[] wageByTier = new double[PayTier.values().length];`
56. **L7225** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209)
57. **L7226** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
58. **L7229** `for (int t = 0; t < wageByTier.length; t++) {`
59. **L7245** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3670)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
60. **L7246** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L724)
61. **L7247** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L242), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
62. **L7248** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L284), [PopulationCohorts.total](map/PopulationCohorts.md) (L95), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3670), [Game.unskilledWage](map/Game.md) (L7774)
63. **L7255** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L382), [Crime.getAdmitted](map/Crime.md) (L422)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
64. **L7257** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L487), [Crime.prisoners](map/Crime.md) (L367)
65. **L7258** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
66. **L7260** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L788)
67. **L7261** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L493)
68. **L7262** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L7774), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
69. **L7266** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L435), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)  
   _The month's adult arrivals look for work next month._
70. **L7276** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L367)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
71. **L7277** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
72. **L7281** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3334), [Education.getFinished](map/Education.md) (L832)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
73. **L7293** `int[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L3217)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
74. **L7294** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
75. **L7295** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1278)
76. **L7296** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1365), [FamilyModel.house](map/FamilyModel.md) (L1128)
77. **L7301** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L1048), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
78. **L7305** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L805)  
   _And the advisor prices a new home on who would move into it._
79. **L7306** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L810)
80. **L7322** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1492), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L765)  
   _...and the ones who cannot afford one either._
81. **L7324** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1523), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L816), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
82. **L7363** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L145), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
83. **L7364** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L465), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3746), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3835), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L82), [Game.burialShare](map/Game.md) (L7591), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L3564), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3596)
84. **L7374** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1121), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Healthcare.getFees](map/Healthcare.md) (L713)
85. **L7389** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L1499), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
86. **L7390** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L370), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L3634), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3746), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3835), [Migration.getLastDepartures](map/Migration.md) (L440)
87. **L7409** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L807), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L3797), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L3810)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
88. **L7416** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L710), [Education.getGraduates](map/Education.md) (L730)
89. **L7417** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L636), [Education.getLicences](map/Education.md) (L733)
90. **L7420** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L492), [Education.getStudying](map/Education.md) (L539)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
91. **L7423** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L678)  
   _A licence holder is a graduate first, and the graduate count has just moved._
92. **L7425** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1130), [Education.getGrossCost](map/Education.md) (L773), [Education.getFees](map/Education.md) (L781)
93. **L7431** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L359), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3746), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3835)  
   _6c._
94. **L7435** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L1143), [Crime.getGrossCost](map/Crime.md) (L449)
95. **L7449** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L162), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _6d._
96. **L7450** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L1168), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3746), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3835), [Game.getInfrastructureManager](map/Game.md) (L1058), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
97. **L7461** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L738), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3447), [Game.unhousedShareOfCity](map/Game.md) (L7492), [Crime.getInjuredShare](map/Crime.md) (L413)  
   _7._
98. **L7477** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 118-180 (63 lines)

1. **L121** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135)  
   _TBE_
2. **L122** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)
3. **L123** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L3254)
4. **L124** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L272), [Game.getFamilies](map/Game.md) (L6966)
5. **L130** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L269), [Game.getFamilies](map/Game.md) (L6966)  
   _The two inputs to the rent price, which is a market now._
6. **L133** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L273), [Game.getFamilies](map/Game.md) (L6966)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L137** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L270), [Game.marginalHousingCost](map/Game.md) (L9628)
8. **L139** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L1186), [Game.getCohorts](map/Game.md) (L6965)
9. **L145** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L93), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L146** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L105), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L3906)
11. **L149** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L163)
12. **L156** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L87), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L188)  
   _The split behind that total._
13. **L157** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L149), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L219)
14. **L158** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L150), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L223)
15. **L173** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L152), [Game.getInfrastructureManager](map/Game.md) (L1058)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L176** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L180), [Game.getHealth](map/Game.md) (L6969)  
   _The fourth ratio._
17. **L177** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L969)

## SimulationEngine.updateServices() - SimulationEngine.java lines 182-189 (8 lines)

1. **L183** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L185), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172)
2. **L184** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L203), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
3. **L187** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L260), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L188** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L33)

## Game.run() - Game.java lines 454-457 (4 lines)

1. **L455** `initialize();` → [Game.initialize](map/Game.md) (L459)
2. **L456** `foundingBank();` → [Game.foundingBank](map/Game.md) (L578)

