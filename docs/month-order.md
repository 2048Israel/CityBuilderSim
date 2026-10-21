# The order the month runs in

Generated 2026-10-20 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 9198-10029 (832 lines)

1. **L9206** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L9207** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L953)
3. **L9208** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
4. **L9217** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L140)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
5. **L9218** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L11935)
6. **L9230** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L3684)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
7. **L9231** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L9063)
8. **L9233** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L10692)
9. **L9242** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
10. **L9243** `monthsSinceAutosave++;`
11. **L9249** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L6822)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
12. **L9252** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2312)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
13. **L9253** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L595)
14. **L9256** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L347)  
   _The register's month: nothing offered, nothing paid, until it is._
15. **L9257** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L404)
16. **L9258** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1103)
17. **L9259** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1457)
18. **L9277** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
19. **L9305** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
20. **L9306** `cityDiscountForBank = cityDiscountThisMonth;`
21. **L9307** `cityDebtRaisedThisMonth = 0;`
22. **L9308** `cityDiscountThisMonth = 0;`
23. **L9309** `cityPrincipalRepaidThisMonth = 0;`
24. **L9310** `bankPrincipalRepaidThisMonth = 0;`
25. **L9313** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
26. **L9314** `foreignDebtRaisedThisMonth = 0;`
27. **L9315** `foreignPrincipalRepaidThisMonth = 0;`
28. **L9316** `foreignInterestPaidThisMonth = 0;`
29. **L9321** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
30. **L9322** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
31. **L9323** `sectorInvested.clear();`
32. **L9324** `salvageThisMonth.clear();`
33. **L9325** `salvageBySector.clear();`
34. **L9326** `salvageUsedThisMonth = 0;`
35. **L9327** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
36. **L9328** `double pooledBefore = 0;`
37. **L9329** `for (double p : poolsBefore) pooledBefore += p;`
38. **L9330** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L1891)
39. **L9332** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1324)
40. **L9333** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L1273)
41. **L9334** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L239)
42. **L9345** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
43. **L9346** `buybackAbroad = buybackAbroadUnsettled;`
44. **L9347** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
45. **L9348** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L534)
46. **L9357** `if (debtManager.isAutopilot() && priceIndex.hasRate()) {` → [DebtManager.isAutopilot](map/DebtManager.md) (L116), [PriceIndex.hasRate](map/PriceIndex.md) (L481), [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L347), [PriceIndex.inflation](map/PriceIndex.md) (L473)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
47. **L9366** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L13901)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
48. **L9374** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L13472)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
49. **L9379** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L13195)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
50. **L9398** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L14026), [Game.studentGrantBill](map/Game.md) (L11677)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
51. **L9412** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L14051)  
   _...AND EI, THE SAME WAY (0.7.3)._
52. **L9430** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1522), [Bank.chargeTax](map/Bank.md) (L3911), [Game.bankProfitTaxRate](map/Game.md) (L2762)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
53. **L9432** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L10031)
54. **L9433** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
55. **L9434** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L10596)
56. **L9435** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2222)
57. **L9436** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L10545)
58. **L9440** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
59. **L9441** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L13101)
60. **L9442** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
61. **L9443** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1138)
62. **L9447** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L10567)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
63. **L9458** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
64. **L9460** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper;`  
   _The residual buyer (0.7.1): what the households did not pay for._
65. **L9461** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper);` → [Bank.lend](map/Bank.md) (L2944)
66. **L9464** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3102)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
67. **L9465** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L2938)
68. **L9481** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2797), [DebtManager.getAccretedForBank](map/DebtManager.md) (L1755)  
   _THE DISCOUNT ACCRETES (0.7.1)._
69. **L9482** `legacyDiscountDue = 0;`
70. **L9486** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
71. **L9487** `cityDiscountForBank = 0;`
72. **L9489** `double sectorInterestPaid = 0;`
73. **L9490** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L1555)
74. **L9496** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2778), [BondMarket.getCouponsStruck](map/BondMarket.md) (L997)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
75. **L9497** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L567), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1004)
76. **L9499** `refreshBank();` → [Game.refreshBank](map/Game.md) (L3019)
77. **L9502** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L3185)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
78. **L9525** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L2995), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1018), [BuildingManager.countByName](map/BuildingManager.md) (L6357), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1025)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
79. **L9528** `bankMaintenanceDue = 0;`
80. **L9536** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L2743), [Bank.openBranches](map/Bank.md) (L2716), [BuildingManager.countByName](map/BuildingManager.md) (L6357)  
   _Capital for whatever branches opened this month._
81. **L9581** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1189), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
82. **L9582** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L427), [DebtManager.countryPremium](map/DebtManager.md) (L833), [Bank.headroom](map/Bank.md) (L5388)
83. **L9588** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L603)
84. **L9589** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L611)
85. **L9590** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L628), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L480)
86. **L9594** `refreshBank();` → [Game.refreshBank](map/Game.md) (L3019)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
87. **L9604** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3277), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
88. **L9617** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L296), [Bank.getPlacementIncome](map/Bank.md) (L3551)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
89. **L9618** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L304), [Bank.getFundingCost](map/Bank.md) (L3549)
90. **L9619** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L289), [Bank.getBranches](map/Bank.md) (L5063), [Bank.wholesaleFunding](map/Bank.md) (L3257)
91. **L9624** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1896)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
92. **L9625** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L3312)
93. **L9629** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3711)  
   _The month is final, so the figure next month's tax is charged on is final too._
94. **L9633** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L374)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
95. **L9650** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L10531)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
96. **L9651** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L1920)
97. **L9661** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3879), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3216)  
   _...AND THE SAVERS ARE PAID._
98. **L9662** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3217)
99. **L9669** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1334)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
100. **L9670** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L451), [EconomyManager.setSectorCash](map/EconomyManager.md) (L456), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1336)
101. **L9701** `payDividends();` → [Game.payDividends](map/Game.md) (L2871)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
102. **L9702** `tradeShares();` → [Game.tradeShares](map/Game.md) (L2942)
103. **L9712** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L3486)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
104. **L9722** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1134), [Exchange.cityMarketValue](map/Exchange.md) (L1245)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
105. **L9723** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L3128)
106. **L9725** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L169), [Bank.depositRate](map/Bank.md) (L3209), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)
107. **L9729** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3023), [Bank.depositRate](map/Bank.md) (L3209), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)  
   _...and the households, by the same rule, with what the owners were just paid._
108. **L9732** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3362), [DebtManager.householdBookYield](map/DebtManager.md) (L1725), [Bank.depositRate](map/Bank.md) (L3209)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
109. **L9750** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1896)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
110. **L9751** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L3312)
111. **L9752** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L3407)
112. **L9787** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
113. **L9788** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L536)
114. **L9789** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L294), [Bank.depositRate](map/Bank.md) (L3209), [DebtManager.getRate](map/DebtManager.md) (L1028), [DebtManager.countryPremium](map/DebtManager.md) (L833), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2216), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L1219), [Game.yearlyDepreciation](map/Game.md) (L12014), [Bank.isInsolvent](map/Bank.md) (L1804), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L966)
115. **L9811** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
116. **L9812** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1680)
117. **L9813** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1694)
118. **L9814** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1668), [CapitalFlows.getStock](map/CapitalFlows.md) (L506)
119. **L9816** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L486)
120. **L9819** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
121. **L9829** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L1062), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2216)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
122. **L9852** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L279), [PriceIndex.getIndex](map/PriceIndex.md) (L424), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293), [PriceIndex.inflation](map/PriceIndex.md) (L473), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L340)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
123. **L9870** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L508), [Game.realRateDifferential](map/Game.md) (L8544)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
124. **L9873** `foreign.setExpectedDrift(anchoredDrift());` → [ForeignAccounts.setExpectedDrift](map/ForeignAccounts.md) (L581), [Game.anchoredDrift](map/Game.md) (L11830)  
   _...and the anchored drift, the credible part of expected inflation against the world's (0.7.42; ForeignAccounts, THE ANCHORED DRIFT)._
125. **L9874** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L816)
126. **L9882** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L626), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L772)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
127. **L9897** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L630), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
128. **L9898** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L1156), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L646), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)
129. **L9905** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L1474)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
130. **L9906** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L741), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L1039), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L1020)
131. **L9907** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L959)
132. **L9908** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L8519)
133. **L9921** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L191), [Game.indexPrices](map/Game.md) (L11839), [Game.indexSpends](map/Game.md) (L11867), [Game.indexFees](map/Game.md) (L11882), [Game.indexFeeSpends](map/Game.md) (L11898)  
   _...AND WHAT THE MONTH COST A FAMILY._
134. **L9933** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L167)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
135. **L9934** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L162), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L422), [DebtManager.holdingRate](map/DebtManager.md) (L376), [PriceIndex.hasRate](map/PriceIndex.md) (L481), [PriceIndex.inflation](map/PriceIndex.md) (L473)
136. **L9937** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L11960)
137. **L9940** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)  
   _A year of the rate, so next year can tell a drift from a run._
138. **L9941** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
139. **L9943** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L13618)
140. **L9945** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L705)
141. **L9979** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
142. **L9980** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
143. **L9999** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L13827)
144. **L10001** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L1990)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
145. **L10002** `recordMonth();` → [Game.recordMonth](map/Game.md) (L12319)
146. **L10004** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
147. **L10006** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L1288)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
148. **L10026** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L12389)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._

## Game.startOfMonthUpdate() - Game.java lines 10031-10308 (278 lines)

1. **L10071** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L263)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L10072** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L256), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293)
3. **L10086** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L426), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L365), [PriceIndex.getIndex](map/PriceIndex.md) (L424), [Expectations.monthlyExpected](map/Expectations.md) (L146)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
4. **L10110** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L533), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
5. **L10112** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L273), [LandManager.getPricePerSqFt](map/LandManager.md) (L415)
6. **L10122** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L10531)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L10127** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5063), [Bank.isInsolvent](map/Bank.md) (L1804)  
   _A failed bank lends nothing._
8. **L10140** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L3128)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
9. **L10141** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4816)
10. **L10142** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4844)
11. **L10147** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4499)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
12. **L10148** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L581)
13. **L10155** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L3168)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
14. **L10156** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L474), [Bank.prime](map/Bank.md) (L1135), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1156)
15. **L10160** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L959)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
16. **L10165** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L632)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
17. **L10169** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L5744)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
18. **L10173** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L5693)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
19. **L10187** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L1555)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
20. **L10188** `double constructionWorkDone = construction.getRecognisedThisMonth();`
21. **L10197** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1046)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
22. **L10199** `settleReserve();` → [Game.settleReserve](map/Game.md) (L4007)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
23. **L10203** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L1555)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
24. **L10209** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
25. **L10210** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L1915), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L269), [Healthcare.getGrossCost](map/Healthcare.md) (L720), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L839), [EconomyManager.getTransitBill](map/EconomyManager.md) (L1854), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L417), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L419), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L643)
26. **L10264** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
27. **L10265** `monthlyMaterialImportBill = 0;`
28. **L10270** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L2153)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
29. **L10278** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L246)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
30. **L10286** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L118)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
31. **L10295** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
32. **L10299** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1275)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
33. **L10305** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L2031)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
34. **L10307** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L10309)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-127 (96 lines)

1. **L66** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L5674)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L67** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L11496), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5124)
3. **L75** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L6462), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5200)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L79** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5252)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
5. **L80** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L6570), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6320), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5216)
6. **L86** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L6510), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L6329)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
7. **L91** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L7098), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5245)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
8. **L100** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L164)  
   _Roads, before anything reads them._
9. **L107** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L129)  
   _Recount the posts and refresh the wage arrays._
10. **L123** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L10842)  
   _THE POPULATION STEP._
11. **L125** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L143)
12. **L126** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L207)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 129-141 (13 lines)

1. **L134** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L5597)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L135** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L10714)
3. **L136** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L10726)
4. **L139** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L12234)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L140** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L332)

## Game.advanceDemographics() - Game.java lines 10842-11358 (517 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L10847** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L561), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239), [PriceIndex.getIndex](map/PriceIndex.md) (L424)  
   _In REAL terms - see Migration.recordWages()._
2. **L10861** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L10862** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L11389)
4. **L10863** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L11389)
5. **L10864** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L11389)
6. **L10865** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7089)
7. **L10867** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
8. **L10881** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L10882** `java.util.Arrays.fill(affordable, 1);`
10. **L10883** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L11408)
11. **L10886** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L10887** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L10888** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L10891** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L636)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L10894** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L10895** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7089)
17. **L10909** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L10922** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L396)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L10933** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L10934** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L10935** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L10936** `double[] inBand = new double[AgeBand.values().length];`
23. **L10937** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L94)
24. **L10941** `double[] careFactors = mortalityFactors;`
25. **L10942** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L466), [Healthcare.mortalityFactors](map/Healthcare.md) (L396)
26. **L10952** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L10955** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L10962** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L10963** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L94)
30. **L10964** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
31. **L10966** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L164), [Healthcare.birthFactor](map/Healthcare.md) (L415)
32. **L10968** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L270), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L101)
33. **L10972** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L10973** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L104)
35. **L10974** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L101)
36. **L10975** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L106)
37. **L10976** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L10977** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L502), [Healthcare.mortalityFactors](map/Healthcare.md) (L396)
39. **L10980** `lastOrphanDeaths = outsideDead[0];`
40. **L10981** `lastUnhousedDeaths = outsideDead[1];`
41. **L10988** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L368), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L10991** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L11004** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L94)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L11011** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L906), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3723), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L11020** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L302), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L443)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L11024** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L343), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
47. **L11029** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L788), [BuildingManager.homesBySize](map/BuildingManager.md) (L6643)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
48. **L11031** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L340), [Migration.monthlyNet](map/Migration.md) (L801), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L10718), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L6622), [PopulationCohorts.share](map/PopulationCohorts.md) (L122)
49. **L11046** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
50. **L11058** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L12259)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
51. **L11060** `double[] jobsByTier = new double[PayTier.values().length];`
52. **L11061** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
53. **L11062** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1118)
54. **L11064** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
55. **L11076** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
56. **L11077** `double[] wageByTier = new double[PayTier.values().length];`
57. **L11078** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239)
58. **L11079** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
59. **L11082** `for (int t = 0; t < wageByTier.length; t++) {`
60. **L11098** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7163)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
61. **L11099** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L727)
62. **L11100** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
63. **L11101** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L115), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7163), [Game.unskilledWage](map/Game.md) (L11653)
64. **L11108** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
65. **L11110** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L788), [Crime.prisoners](map/Crime.md) (L370)
66. **L11111** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
67. **L11113** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1092)
68. **L11114** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L794)
69. **L11115** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L11653), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
70. **L11119** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L456), [PopulationCohorts.share](map/PopulationCohorts.md) (L122)  
   _The month's adult arrivals look for work next month._
71. **L11129** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L370)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
72. **L11130** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
73. **L11134** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3687), [Education.getFinished](map/Education.md) (L898)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
74. **L11146** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L6643)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
75. **L11147** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
76. **L11148** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1297)
77. **L11149** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1384), [FamilyModel.house](map/FamilyModel.md) (L1128)
78. **L11154** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L1555), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
79. **L11158** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1111)  
   _And the advisor prices a new home on who would move into it._
80. **L11159** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1116)
81. **L11175** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1631), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L863)  
   _...and the ones who cannot afford one either._
82. **L11177** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L970), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
83. **L11216** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L153), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
84. **L11217** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L473), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7328), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L96), [Game.burialShare](map/Game.md) (L11470), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7057), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7089)
85. **L11227** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1790), [Healthcare.getGrossCost](map/Healthcare.md) (L720), [Healthcare.getFees](map/Healthcare.md) (L722)
86. **L11242** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L2178), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
87. **L11243** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L398), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7127), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7328), [Migration.getLastDepartures](map/Migration.md) (L461)
88. **L11262** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L873), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L7290), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L7303)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
89. **L11269** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1014), [Education.getGraduates](map/Education.md) (L776)
90. **L11270** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L940), [Education.getLicences](map/Education.md) (L792)
91. **L11273** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L793), [Education.getStudying](map/Education.md) (L585)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
92. **L11276** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L982)  
   _A licence holder is a graduate first, and the graduate count has just moved._
93. **L11278** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1799), [Education.getGrossCost](map/Education.md) (L839), [Education.getFees](map/Education.md) (L847)
94. **L11284** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7328)  
   _6c._
95. **L11288** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L1812), [Crime.getGrossCost](map/Crime.md) (L452)
96. **L11321** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L1565), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L1919), [Motoring.journeyFuel](map/Motoring.md) (L173)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
97. **L11323** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L220), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
98. **L11324** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L198), [Game.getInfrastructureManager](map/Game.md) (L1565)
99. **L11328** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L1850), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7328), [Game.portsBill](map/Game.md) (L5715), [Game.getInfrastructureManager](map/Game.md) (L1565), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
100. **L11340** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L747), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3826), [Game.unhousedShareOfCity](map/Game.md) (L11371), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
101. **L11356** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 143-205 (63 lines)

1. **L146** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L147** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L148** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L6680)
4. **L149** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L338), [Game.getFamilies](map/Game.md) (L10814)
5. **L155** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L335), [Game.getFamilies](map/Game.md) (L10814)  
   _The two inputs to the rent price, which is a market now._
6. **L158** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L339), [Game.getFamilies](map/Game.md) (L10814)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L162** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L336), [Game.marginalHousingCost](map/Game.md) (L14316)
8. **L164** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L1871), [Game.getCohorts](map/Game.md) (L10813)
9. **L170** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L171** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L7416)
11. **L174** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L165)
12. **L181** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L213)  
   _The split behind that total._
13. **L182** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L277)
14. **L183** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L281)
15. **L198** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L1565)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L201** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L10817)  
   _The fourth ratio._
17. **L202** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1487)

## SimulationEngine.updateServices() - SimulationEngine.java lines 207-214 (8 lines)

1. **L208** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L243), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197)
2. **L209** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L261), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
3. **L212** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L318), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L213** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L48)

## Game.run() - Game.java lines 643-646 (4 lines)

1. **L644** `initialize();` → [Game.initialize](map/Game.md) (L648)
2. **L645** `foundingBank();` → [Game.foundingBank](map/Game.md) (L778)

