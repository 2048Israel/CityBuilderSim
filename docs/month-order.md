# The order the month runs in

Generated 2026-09-30 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 6471-7240 (770 lines)

1. **L6483** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L2897)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
2. **L6484** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L6342)
3. **L6486** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L7862)
4. **L6495** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
5. **L6496** `monthsSinceAutosave++;`
6. **L6499** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2307)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
7. **L6500** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L445)
8. **L6503** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L327)  
   _The register's month: nothing offered, nothing paid, until it is._
9. **L6504** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L398)
10. **L6505** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1054)
11. **L6506** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1222)
12. **L6508** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L9282)
13. **L6525** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
14. **L6553** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
15. **L6554** `cityDiscountForBank = cityDiscountThisMonth;`
16. **L6555** `cityDebtRaisedThisMonth = 0;`
17. **L6556** `cityDiscountThisMonth = 0;`
18. **L6557** `cityPrincipalRepaidThisMonth = 0;`
19. **L6558** `bankPrincipalRepaidThisMonth = 0;`
20. **L6561** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
21. **L6562** `foreignDebtRaisedThisMonth = 0;`
22. **L6563** `foreignPrincipalRepaidThisMonth = 0;`
23. **L6564** `foreignInterestPaidThisMonth = 0;`
24. **L6569** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
25. **L6570** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
26. **L6571** `sectorInvested.clear();`
27. **L6572** `salvageThisMonth.clear();`
28. **L6573** `salvageBySector.clear();`
29. **L6574** `salvageUsedThisMonth = 0;`
30. **L6575** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L317)
31. **L6576** `double pooledBefore = 0;`
32. **L6577** `for (double p : poolsBefore) pooledBefore += p;`
33. **L6578** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L1493)
34. **L6580** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1319)
35. **L6581** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L1195)
36. **L6582** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L229)
37. **L6593** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
38. **L6594** `buybackAbroad = buybackAbroadUnsettled;`
39. **L6595** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
40. **L6596** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L524)
41. **L6605** `if (debtManager.isAutopilot() && priceIndex.hasRate()) {` → [DebtManager.isAutopilot](map/DebtManager.md) (L106), [PriceIndex.hasRate](map/PriceIndex.md) (L204), [DebtManager.setPolicyRate](map/DebtManager.md) (L69), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L187), [PriceIndex.inflation](map/PriceIndex.md) (L196)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
42. **L6614** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L10633)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
43. **L6622** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L10294)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
44. **L6627** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L10017)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
45. **L6646** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L10747), [Game.studentGrantBill](map/Game.md) (L8817)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
46. **L6660** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L10772)  
   _...AND EI, THE SAME WAY (0.7.3)._
47. **L6678** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1287), [Bank.chargeTax](map/Bank.md) (L3890), [Game.bankProfitTaxRate](map/Game.md) (L2034)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
48. **L6680** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L7242)
49. **L6681** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
50. **L6682** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L7782)
51. **L6683** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L1739)
52. **L6684** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L7732)
53. **L6688** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
54. **L6689** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L9923)
55. **L6690** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
56. **L6691** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L908)
57. **L6695** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L7754)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
58. **L6706** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
59. **L6708** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper;`  
   _The residual buyer (0.7.1): what the households did not pay for._
60. **L6709** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper);` → [Bank.lend](map/Bank.md) (L2939)
61. **L6712** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3095)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
62. **L6713** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L2933)
63. **L6729** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2792), [DebtManager.getAccretedForBank](map/DebtManager.md) (L1325)  
   _THE DISCOUNT ACCRETES (0.7.1)._
64. **L6730** `legacyDiscountDue = 0;`
65. **L6734** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
66. **L6735** `cityDiscountForBank = 0;`
67. **L6737** `double sectorInterestPaid = 0;`
68. **L6738** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L1080)
69. **L6744** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2773), [BondMarket.getCouponsStruck](map/BondMarket.md) (L956)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
70. **L6745** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L567), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L963)
71. **L6747** `refreshBank();` → [Game.refreshBank](map/Game.md) (L2274)
72. **L6750** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L2440)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
73. **L6773** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L2990), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L123), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L843), [BuildingManager.countByName](map/BuildingManager.md) (L3433), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L850)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
74. **L6776** `bankMaintenanceDue = 0;`
75. **L6784** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L2015), [Bank.openBranches](map/Bank.md) (L2711), [BuildingManager.countByName](map/BuildingManager.md) (L3433)  
   _Capital for whatever branches opened this month._
76. **L6829** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1184), [DebtManager.getPolicyRate](map/DebtManager.md) (L67)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
77. **L6830** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L427), [DebtManager.countryPremium](map/DebtManager.md) (L603), [Bank.headroom](map/Bank.md) (L5356)
78. **L6836** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L603)
79. **L6837** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L611)
80. **L6838** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L628), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L480)
81. **L6842** `refreshBank();` → [Game.refreshBank](map/Game.md) (L2274)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
82. **L6852** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3270), [DebtManager.getPolicyRate](map/DebtManager.md) (L67)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
83. **L6865** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L286), [Bank.getPlacementIncome](map/Bank.md) (L3530)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
84. **L6866** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L294), [Bank.getFundingCost](map/Bank.md) (L3528)
85. **L6867** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L279), [Bank.getBranches](map/Bank.md) (L5031), [Bank.wholesaleFunding](map/Bank.md) (L3250)
86. **L6872** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1891)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
87. **L6873** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L2567)
88. **L6877** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3690)  
   _The month is final, so the figure next month's tax is charged on is final too._
89. **L6881** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L364)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
90. **L6898** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L7718)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
91. **L6899** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L1484)
92. **L6909** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3485), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3209)  
   _...AND THE SAVERS ARE PAID._
93. **L6910** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3210)
94. **L6917** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1123)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
95. **L6918** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L384), [EconomyManager.setSectorCash](map/EconomyManager.md) (L389), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1125)
96. **L6949** `payDividends();` → [Game.payDividends](map/Game.md) (L2132)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
97. **L6950** `tradeShares();` → [Game.tradeShares](map/Game.md) (L2197)
98. **L6960** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L2729)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
99. **L6970** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1085), [Exchange.cityMarketValue](map/Exchange.md) (L1093)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
100. **L6971** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L2383)
101. **L6973** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L169), [Bank.depositRate](map/Bank.md) (L3202), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)
102. **L6977** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L2680), [Bank.depositRate](map/Bank.md) (L3202), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _...and the households, by the same rule, with what the owners were just paid._
103. **L6980** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3019), [DebtManager.householdBookYield](map/DebtManager.md) (L1295), [Bank.depositRate](map/Bank.md) (L3202)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
104. **L6998** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1891)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
105. **L6999** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L2567)
106. **L7000** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L2653)
107. **L7035** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
108. **L7036** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L536)
109. **L7037** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L294), [Bank.depositRate](map/Bank.md) (L3202), [DebtManager.getRate](map/DebtManager.md) (L798), [DebtManager.countryPremium](map/DebtManager.md) (L603), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1733), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L1141), [Game.yearlyDepreciation](map/Game.md) (L8982), [Bank.isInsolvent](map/Bank.md) (L1799), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L736)
110. **L7059** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L506)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
111. **L7060** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1675)
112. **L7061** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1689)
113. **L7062** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1663), [CapitalFlows.getStock](map/CapitalFlows.md) (L506)
114. **L7064** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L395)
115. **L7067** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
116. **L7077** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L985), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1733)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
117. **L7100** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L279), [PriceIndex.getIndex](map/PriceIndex.md) (L179), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293), [PriceIndex.inflation](map/PriceIndex.md) (L196), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L340)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
118. **L7117** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L505), [Game.realRateDifferential](map/Game.md) (L5850)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
119. **L7118** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L746)
120. **L7126** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L613), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L702)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
121. **L7141** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L400), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
122. **L7142** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L1078), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L416), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)
123. **L7149** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L1396)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
124. **L7150** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L511), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L962), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L943)
125. **L7151** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L729)
126. **L7152** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L5835)
127. **L7163** `priceIndex.takeMonth(` → [PriceIndex.takeMonth](map/PriceIndex.md) (L102), [Game.getSectors](map/Game.md) (L1080)  
   _...AND WHAT THE MONTH COST A FAMILY._
128. **L7178** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807)  
   _A year of the rate, so next year can tell a drift from a run._
129. **L7179** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
130. **L7181** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L10439)
131. **L7183** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L474)
132. **L7217** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
133. **L7218** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L317)
134. **L7237** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L10567)
135. **L7238** `recordMonth();` → [Game.recordMonth](map/Game.md) (L9229)

## Game.startOfMonthUpdate() - Game.java lines 7242-7500 (259 lines)

1. **L7282** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L263)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L7283** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L230), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L807), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293)
3. **L7293** `labourMarket.updateCostOfLiving(priceIndex.getIndex());` → [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L348), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _...and wages start chasing what the world now charges._
4. **L7311** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L257), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
5. **L7313** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L247), [LandManager.getPricePerSqFt](map/LandManager.md) (L221)
6. **L7323** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L7718)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L7328** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5031), [Bank.isInsolvent](map/Bank.md) (L1799)  
   _A failed bank lends nothing._
8. **L7341** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L2383)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
9. **L7342** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4784)
10. **L7343** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4812)
11. **L7348** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4467)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
12. **L7349** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L580)
13. **L7356** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L2423)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
14. **L7357** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L407), [Bank.prime](map/Bank.md) (L1130), [DebtManager.getPolicyRate](map/DebtManager.md) (L67), [Bank.insuredMortgageRate](map/Bank.md) (L1151)
15. **L7361** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L920)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
16. **L7366** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L505)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
17. **L7370** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L4252)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
18. **L7374** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L4221)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
19. **L7388** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L1080)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
20. **L7389** `double constructionWorkDone = construction.getRecognisedThisMonth();`
21. **L7398** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L870)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
22. **L7402** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L1080)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
23. **L7407** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L1517), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L211), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Crime.getGrossCost](map/Crime.md) (L449), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L223), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L225), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L516)
24. **L7456** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
25. **L7457** `monthlyMaterialImportBill = 0;`
26. **L7462** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L1506)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
27. **L7470** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L91)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
28. **L7478** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L118)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
29. **L7487** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
30. **L7491** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1068)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
31. **L7497** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L1399)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
32. **L7499** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L7501)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-117 (86 lines)

1. **L66** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L4202)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L67** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L8636), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L3277)
3. **L75** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L4807), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L3317)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L76** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(), siteOutput);` → [Game.recogniseSiteWork](map/Game.md) (L4915), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L3396), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L3333)
5. **L81** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L4855), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L3405)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
6. **L90** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L122)  
   _Roads, before anything reads them._
7. **L97** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L119)  
   _Recount the posts and refresh the wage arrays._
8. **L113** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L8008)  
   _THE POPULATION STEP._
9. **L115** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L133)
10. **L116** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L197)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 119-131 (13 lines)

1. **L124** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L4127)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L125** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L7884)
3. **L126** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L7896)
4. **L129** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L9144)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L130** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L304)

## Game.advanceDemographics() - Game.java lines 8008-8498 (491 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L8013** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L534), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L211), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _In REAL terms - see Migration.recordWages()._
2. **L8027** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1031)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L8028** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L8529)
4. **L8029** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L8529)
5. **L8030** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L8529)
6. **L8031** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L4089)
7. **L8033** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
8. **L8047** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L8048** `java.util.Arrays.fill(affordable, 1);`
10. **L8049** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L8548)
11. **L8052** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L8053** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L8054** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L8057** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L627)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L8060** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L8061** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L4089)
17. **L8075** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L8088** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L388)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L8099** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L8100** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L8101** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L8102** `double[] inBand = new double[AgeBand.values().length];`
23. **L8103** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
24. **L8107** `double[] careFactors = mortalityFactors;`
25. **L8108** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L446), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
26. **L8118** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L8121** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L8128** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L8129** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
30. **L8130** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L410)
31. **L8132** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L144), [Healthcare.birthFactor](map/Healthcare.md) (L407)
32. **L8134** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L258), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
33. **L8138** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L8139** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L90)
35. **L8140** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
36. **L8141** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L92)
37. **L8142** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L8143** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L482), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
39. **L8146** `lastOrphanDeaths = outsideDead[0];`
40. **L8147** `lastUnhousedDeaths = outsideDead[1];`
41. **L8154** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L342), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L8157** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L8170** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L8177** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L873), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3359), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L8186** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L302), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L358)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L8190** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L343), [Crime.getRateVsCanada](map/Crime.md) (L433)  
   _...and what last month's crime says about living here._
47. **L8195** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L761), [BuildingManager.homesBySize](map/BuildingManager.md) (L3710)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
48. **L8197** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L314), [Migration.monthlyNet](map/Migration.md) (L774), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L7888), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L3689), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)
49. **L8212** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
50. **L8224** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L9169)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
51. **L8226** `double[] jobsByTier = new double[PayTier.values().length];`
52. **L8227** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1031)
53. **L8228** `int[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1090)
54. **L8230** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
55. **L8242** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
56. **L8243** `double[] wageByTier = new double[PayTier.values().length];`
57. **L8244** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L211)
58. **L8245** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
59. **L8248** `for (int t = 0; t < wageByTier.length; t++) {`
60. **L8264** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L4163)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
61. **L8265** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L724)
62. **L8266** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L242), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
63. **L8267** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L284), [PopulationCohorts.total](map/PopulationCohorts.md) (L95), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L4163), [Game.unskilledWage](map/Game.md) (L8793)
64. **L8274** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L382), [Crime.getAdmitted](map/Crime.md) (L422)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
65. **L8276** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L760), [Crime.prisoners](map/Crime.md) (L367)
66. **L8277** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
67. **L8279** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1064)
68. **L8280** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L766)
69. **L8281** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L8793), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
70. **L8285** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L441), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)  
   _The month's adult arrivals look for work next month._
71. **L8295** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L367)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
72. **L8296** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
73. **L8300** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3344), [Education.getFinished](map/Education.md) (L832)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
74. **L8312** `int[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L3710)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
75. **L8313** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
76. **L8314** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1297)
77. **L8315** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1384), [FamilyModel.house](map/FamilyModel.md) (L1128)
78. **L8320** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L1080), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
79. **L8324** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L923)  
   _And the advisor prices a new home on who would move into it._
80. **L8325** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L928)
81. **L8341** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1631), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L765)  
   _...and the ones who cannot afford one either._
82. **L8343** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L816), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
83. **L8382** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L145), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
84. **L8383** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L465), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L4239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L174), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L4328), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L82), [Game.burialShare](map/Game.md) (L8610), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L4057), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L4089)
85. **L8393** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1410), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Healthcare.getFees](map/Healthcare.md) (L713)
86. **L8408** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L1531), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
87. **L8409** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L370), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L4127), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L4239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L174), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L4328), [Migration.getLastDepartures](map/Migration.md) (L446)
88. **L8428** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L807), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L4290), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L174), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L4303)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
89. **L8435** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L986), [Education.getGraduates](map/Education.md) (L730)
90. **L8436** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L912), [Education.getLicences](map/Education.md) (L733)
91. **L8439** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L765), [Education.getStudying](map/Education.md) (L539)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
92. **L8442** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L954)  
   _A licence holder is a graduate first, and the graduate count has just moved._
93. **L8444** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1419), [Education.getGrossCost](map/Education.md) (L773), [Education.getFees](map/Education.md) (L781)
94. **L8450** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L359), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L4239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L174), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L4328)  
   _6c._
95. **L8454** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L1432), [Crime.getGrossCost](map/Crime.md) (L449)
96. **L8468** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L162), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _6d._
97. **L8469** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L1457), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L4239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L174), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L4328), [Game.getInfrastructureManager](map/Game.md) (L1090), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
98. **L8480** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L738), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3457), [Game.unhousedShareOfCity](map/Game.md) (L8511), [Crime.getInjuredShare](map/Crime.md) (L413)  
   _7._
99. **L8496** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 133-195 (63 lines)

1. **L136** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L137** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L138** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L3747)
4. **L139** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L272), [Game.getFamilies](map/Game.md) (L7980)
5. **L145** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L269), [Game.getFamilies](map/Game.md) (L7980)  
   _The two inputs to the rent price, which is a market now._
6. **L148** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L273), [Game.getFamilies](map/Game.md) (L7980)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L152** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L270), [Game.marginalHousingCost](map/Game.md) (L11020)
8. **L154** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L1475), [Game.getCohorts](map/Game.md) (L7979)
9. **L160** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L93), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1031)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L161** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L105), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L174), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L4399)
11. **L164** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L165)
12. **L171** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L87), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L190)  
   _The split behind that total._
13. **L172** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L149), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L219)
14. **L173** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L150), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L223)
15. **L188** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L152), [Game.getInfrastructureManager](map/Game.md) (L1090)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L191** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L180), [Game.getHealth](map/Game.md) (L7983)  
   _The fourth ratio._
17. **L192** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1252)

## SimulationEngine.updateServices() - SimulationEngine.java lines 197-204 (8 lines)

1. **L198** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L185), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L174)
2. **L199** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L203), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1031)
3. **L202** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L260), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L203** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L33)

## Game.run() - Game.java lines 479-482 (4 lines)

1. **L480** `initialize();` → [Game.initialize](map/Game.md) (L484)
2. **L481** `foundingBank();` → [Game.foundingBank](map/Game.md) (L610)

