# The order the month runs in

Generated 2026-10-21 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 9369-10215 (847 lines)

1. **L9377** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L9378** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L9379** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1014)
4. **L9380** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L9389** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L140)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L9390** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L12103)
7. **L9402** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L3693)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L9403** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L9157)
9. **L9405** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L10857)
10. **L9414** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L9415** `monthsSinceAutosave++;`
12. **L9421** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L6833)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
13. **L9424** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2312)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
14. **L9425** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L595)
15. **L9428** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L347)  
   _The register's month: nothing offered, nothing paid, until it is._
16. **L9429** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L404)
17. **L9430** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1107)
18. **L9431** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1462)
19. **L9449** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
20. **L9477** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
21. **L9478** `cityDiscountForBank = cityDiscountThisMonth;`
22. **L9479** `cityDebtRaisedThisMonth = 0;`
23. **L9480** `cityDiscountThisMonth = 0;`
24. **L9481** `cityPrincipalRepaidThisMonth = 0;`
25. **L9482** `bankPrincipalRepaidThisMonth = 0;`
26. **L9485** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
27. **L9486** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
28. **L9487** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
29. **L9488** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L430)
30. **L9489** `foreignDebtRaisedThisMonth = 0;`
31. **L9490** `foreignPrincipalRepaidThisMonth = 0;`
32. **L9491** `foreignInterestPaidThisMonth = 0;`
33. **L9496** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
34. **L9497** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
35. **L9498** `sectorInvested.clear();`
36. **L9499** `salvageThisMonth.clear();`
37. **L9500** `salvageBySector.clear();`
38. **L9501** `salvageUsedThisMonth = 0;`
39. **L9502** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
40. **L9503** `double pooledBefore = 0;`
41. **L9504** `for (double p : poolsBefore) pooledBefore += p;`
42. **L9505** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L1896)
43. **L9507** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1324)
44. **L9508** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L1273)
45. **L9509** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L241)
46. **L9520** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
47. **L9521** `buybackAbroad = buybackAbroadUnsettled;`
48. **L9522** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
49. **L9523** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L550)
50. **L9532** `if (debtManager.isAutopilot() && priceIndex.hasRate()) {` → [DebtManager.isAutopilot](map/DebtManager.md) (L116), [PriceIndex.hasRate](map/PriceIndex.md) (L481), [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L347), [PriceIndex.inflation](map/PriceIndex.md) (L473)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
51. **L9538** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L10712)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
52. **L9545** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L14397)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
53. **L9553** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L13956)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
54. **L9558** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L13580)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
55. **L9577** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L14522), [Game.studentGrantBill](map/Game.md) (L11842)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
56. **L9591** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L14547)  
   _...AND EI, THE SAME WAY (0.7.3)._
57. **L9609** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1527), [Bank.chargeTax](map/Bank.md) (L3941), [Game.bankProfitTaxRate](map/Game.md) (L2769)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
58. **L9611** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L10217)
59. **L9612** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
60. **L9613** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L10761)
61. **L9614** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2227)
62. **L9615** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L10694)
63. **L9619** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
64. **L9620** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L13395)
65. **L9621** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
66. **L9622** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1167)
67. **L9626** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L10732)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
68. **L9637** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
69. **L9639** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
70. **L9640** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L2974)
71. **L9644** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3132)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
72. **L9645** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L2968)
73. **L9661** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2797), [DebtManager.getAccretedForBank](map/DebtManager.md) (L2013)  
   _THE DISCOUNT ACCRETES (0.7.1)._
74. **L9662** `legacyDiscountDue = 0;`
75. **L9666** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
76. **L9667** `cityDiscountForBank = 0;`
77. **L9669** `double sectorInterestPaid = 0;`
78. **L9670** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L1560)
79. **L9676** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2778), [BondMarket.getCouponsStruck](map/BondMarket.md) (L1001)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
80. **L9677** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L567), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1008)
81. **L9679** `refreshBank();` → [Game.refreshBank](map/Game.md) (L3026)
82. **L9682** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L3194)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
83. **L9705** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3025), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1018), [BuildingManager.countByName](map/BuildingManager.md) (L6357), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1025)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
84. **L9708** `bankMaintenanceDue = 0;`
85. **L9716** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L2750), [Bank.openBranches](map/Bank.md) (L2716), [BuildingManager.countByName](map/BuildingManager.md) (L6357)  
   _Capital for whatever branches opened this month._
86. **L9761** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1189), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
87. **L9762** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L511), [DebtManager.countryPremium](map/DebtManager.md) (L833), [Bank.headroom](map/Bank.md) (L5418)
88. **L9768** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L603)
89. **L9769** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L611)
90. **L9770** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L628), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L564)
91. **L9774** `refreshBank();` → [Game.refreshBank](map/Game.md) (L3026)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
92. **L9784** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3307), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
93. **L9797** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L298), [Bank.getPlacementIncome](map/Bank.md) (L3581)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
94. **L9798** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L306), [Bank.getFundingCost](map/Bank.md) (L3579)
95. **L9799** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L291), [Bank.getBranches](map/Bank.md) (L5093), [Bank.wholesaleFunding](map/Bank.md) (L3287)
96. **L9804** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1896)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
97. **L9805** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L3321)
98. **L9809** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3741)  
   _The month is final, so the figure next month's tax is charged on is final too._
99. **L9813** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L376)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
100. **L9830** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L10680)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
101. **L9831** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L2203)
102. **L9841** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3876), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3246)  
   _...AND THE SAVERS ARE PAID._
103. **L9842** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3247)
104. **L9849** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1334)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
105. **L9850** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L451), [EconomyManager.setSectorCash](map/EconomyManager.md) (L456), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1336)
106. **L9881** `payDividends();` → [Game.payDividends](map/Game.md) (L2878)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
107. **L9882** `tradeShares();` → [Game.tradeShares](map/Game.md) (L2949)
108. **L9892** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L3495)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
109. **L9902** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1138), [Exchange.cityMarketValue](map/Exchange.md) (L1245)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
110. **L9903** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L3137)
111. **L9908** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L405), [DebtManager.firmBookRatio](map/DebtManager.md) (L1954), [DebtManager.firmBookYield](map/DebtManager.md) (L1960)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
112. **L9909** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L181), [Bank.depositRate](map/Bank.md) (L3239), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)
113. **L9913** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3023), [Bank.depositRate](map/Bank.md) (L3239), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)  
   _...and the households, by the same rule, with what the owners were just paid._
114. **L9916** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3359), [DebtManager.householdBookYield](map/DebtManager.md) (L1983), [Bank.depositRate](map/Bank.md) (L3239)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
115. **L9934** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1896)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
116. **L9935** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L3321)
117. **L9936** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L3416)
118. **L9971** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L590)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
119. **L9972** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L620)
120. **L9973** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L378), [Bank.depositRate](map/Bank.md) (L3239), [DebtManager.getRate](map/DebtManager.md) (L1028), [DebtManager.countryPremium](map/DebtManager.md) (L833), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2221), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L1219), [Game.yearlyDepreciation](map/Game.md) (L12182), [Bank.isInsolvent](map/Bank.md) (L1804), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L966)
121. **L9995** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L590)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
122. **L9996** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1680)
123. **L9997** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1694)
124. **L9998** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1668), [CapitalFlows.getStock](map/CapitalFlows.md) (L590)
125. **L10000** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L13370)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
126. **L10002** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L486)
127. **L10005** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
128. **L10015** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L1062), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2221)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
129. **L10038** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L279), [PriceIndex.getIndex](map/PriceIndex.md) (L424), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293), [PriceIndex.inflation](map/PriceIndex.md) (L473), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L340)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
130. **L10056** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L508), [Game.realRateDifferential](map/Game.md) (L8590)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
131. **L10059** `foreign.setExpectedDrift(anchoredDrift());` → [ForeignAccounts.setExpectedDrift](map/ForeignAccounts.md) (L581), [Game.anchoredDrift](map/Game.md) (L11998)  
   _...and the anchored drift, the credible part of expected inflation against the world's (0.7.42; ForeignAccounts, THE ANCHORED DRIFT)._
132. **L10060** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L816)
133. **L10068** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L642), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L772)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
134. **L10083** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L630), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
135. **L10084** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L1156), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L646), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)
136. **L10091** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L1474)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
137. **L10092** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L741), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L1039), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L1020)
138. **L10093** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L959)
139. **L10094** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L8565)
140. **L10107** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L191), [Game.indexPrices](map/Game.md) (L12007), [Game.indexSpends](map/Game.md) (L12035), [Game.indexFees](map/Game.md) (L12050), [Game.indexFeeSpends](map/Game.md) (L12066)  
   _...AND WHAT THE MONTH COST A FAMILY._
141. **L10119** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L167)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
142. **L10120** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L162), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L422), [DebtManager.holdingRate](map/DebtManager.md) (L376), [PriceIndex.hasRate](map/PriceIndex.md) (L481), [PriceIndex.inflation](map/PriceIndex.md) (L473)
143. **L10123** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L12128)
144. **L10126** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884)  
   _A year of the rate, so next year can tell a drift from a run._
145. **L10127** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
146. **L10129** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L14114)
147. **L10131** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L705)
148. **L10165** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
149. **L10166** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
150. **L10185** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L14323)
151. **L10187** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L1995)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
152. **L10188** `recordMonth();` → [Game.recordMonth](map/Game.md) (L12488)
153. **L10190** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
154. **L10192** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L1293)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
155. **L10212** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L12558)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._

## Game.startOfMonthUpdate() - Game.java lines 10217-10494 (278 lines)

1. **L10257** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L263)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L10258** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L256), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L884), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293)
3. **L10272** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L426), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L365), [PriceIndex.getIndex](map/PriceIndex.md) (L424), [Expectations.monthlyExpected](map/Expectations.md) (L146)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
4. **L10296** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L533), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
5. **L10298** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L273), [LandManager.getPricePerSqFt](map/LandManager.md) (L415)
6. **L10308** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L10680)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L10313** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5093), [Bank.isInsolvent](map/Bank.md) (L1804)  
   _A failed bank lends nothing._
8. **L10326** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L3137)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
9. **L10327** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4846)
10. **L10328** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4874)
11. **L10333** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4529)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
12. **L10334** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L581)
13. **L10341** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L3177)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
14. **L10342** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L474), [Bank.prime](map/Bank.md) (L1135), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1156)
15. **L10346** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L963)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
16. **L10351** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L632)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
17. **L10355** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L5753)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
18. **L10359** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L5702)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
19. **L10373** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L1560)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
20. **L10374** `double constructionWorkDone = construction.getRecognisedThisMonth();`
21. **L10383** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1046)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
22. **L10385** `settleReserve();` → [Game.settleReserve](map/Game.md) (L4016)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
23. **L10389** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L1560)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
24. **L10395** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
25. **L10396** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L1920), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L269), [Healthcare.getGrossCost](map/Healthcare.md) (L720), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L839), [EconomyManager.getTransitBill](map/EconomyManager.md) (L1859), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L417), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L419), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L643)
26. **L10450** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
27. **L10451** `monthlyMaterialImportBill = 0;`
28. **L10456** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L2158)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
29. **L10464** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L246)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
30. **L10472** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L118)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
31. **L10481** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
32. **L10485** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1275)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
33. **L10491** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L2036)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
34. **L10493** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L10495)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-127 (96 lines)

1. **L66** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L5683)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L67** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L11661), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5124)
3. **L75** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L6473), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5200)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L79** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5252)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
5. **L80** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L6581), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6320), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5216)
6. **L86** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L6521), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L6329)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
7. **L91** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L7109), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5245)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
8. **L100** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L164)  
   _Roads, before anything reads them._
9. **L107** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L129)  
   _Recount the posts and refresh the wage arrays._
10. **L123** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L11007)  
   _THE POPULATION STEP._
11. **L125** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L143)
12. **L126** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L207)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 129-141 (13 lines)

1. **L134** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L5606)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L135** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L10879)
3. **L136** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L10891)
4. **L139** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L12403)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L140** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L332)

## Game.advanceDemographics() - Game.java lines 11007-11523 (517 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L11012** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L561), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239), [PriceIndex.getIndex](map/PriceIndex.md) (L424)  
   _In REAL terms - see Migration.recordWages()._
2. **L11026** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L11027** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L11554)
4. **L11028** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L11554)
5. **L11029** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L11554)
6. **L11030** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7089)
7. **L11032** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
8. **L11046** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L11047** `java.util.Arrays.fill(affordable, 1);`
10. **L11048** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L11573)
11. **L11051** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L11052** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L11053** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L11056** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L636)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L11059** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L11060** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7089)
17. **L11074** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L11087** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L396)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L11098** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L11099** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L11100** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L11101** `double[] inBand = new double[AgeBand.values().length];`
23. **L11102** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L94)
24. **L11106** `double[] careFactors = mortalityFactors;`
25. **L11107** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L466), [Healthcare.mortalityFactors](map/Healthcare.md) (L396)
26. **L11117** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L11120** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L11127** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L11128** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L94)
30. **L11129** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
31. **L11131** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L164), [Healthcare.birthFactor](map/Healthcare.md) (L415)
32. **L11133** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L270), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L101)
33. **L11137** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L11138** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L104)
35. **L11139** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L101)
36. **L11140** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L106)
37. **L11141** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L11142** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L502), [Healthcare.mortalityFactors](map/Healthcare.md) (L396)
39. **L11145** `lastOrphanDeaths = outsideDead[0];`
40. **L11146** `lastUnhousedDeaths = outsideDead[1];`
41. **L11153** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L368), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L11156** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L11169** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L94)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L11176** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L906), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3720), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L11185** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L302), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L443)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L11189** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L343), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
47. **L11194** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L788), [BuildingManager.homesBySize](map/BuildingManager.md) (L6643)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
48. **L11196** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L340), [Migration.monthlyNet](map/Migration.md) (L801), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L10883), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L6622), [PopulationCohorts.share](map/PopulationCohorts.md) (L122)
49. **L11211** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
50. **L11223** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L12428)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
51. **L11225** `double[] jobsByTier = new double[PayTier.values().length];`
52. **L11226** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
53. **L11227** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1118)
54. **L11229** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
55. **L11241** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
56. **L11242** `double[] wageByTier = new double[PayTier.values().length];`
57. **L11243** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239)
58. **L11244** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
59. **L11247** `for (int t = 0; t < wageByTier.length; t++) {`
60. **L11263** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7163)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
61. **L11264** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L727)
62. **L11265** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
63. **L11266** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L115), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7163), [Game.unskilledWage](map/Game.md) (L11818)
64. **L11273** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
65. **L11275** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L788), [Crime.prisoners](map/Crime.md) (L370)
66. **L11276** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
67. **L11278** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1092)
68. **L11279** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L794)
69. **L11280** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L11818), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
70. **L11284** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L456), [PopulationCohorts.share](map/PopulationCohorts.md) (L122)  
   _The month's adult arrivals look for work next month._
71. **L11294** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L370)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
72. **L11295** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
73. **L11299** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3684), [Education.getFinished](map/Education.md) (L898)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
74. **L11311** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L6643)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
75. **L11312** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
76. **L11313** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1297)
77. **L11314** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1384), [FamilyModel.house](map/FamilyModel.md) (L1128)
78. **L11319** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L1560), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
79. **L11323** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1111)  
   _And the advisor prices a new home on who would move into it._
80. **L11324** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1116)
81. **L11340** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1631), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L863)  
   _...and the ones who cannot afford one either._
82. **L11342** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L970), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
83. **L11381** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L153), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
84. **L11382** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L473), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7328), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L96), [Game.burialShare](map/Game.md) (L11635), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7057), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7089)
85. **L11392** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1795), [Healthcare.getGrossCost](map/Healthcare.md) (L720), [Healthcare.getFees](map/Healthcare.md) (L722)
86. **L11407** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L2183), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
87. **L11408** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L398), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7127), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7328), [Migration.getLastDepartures](map/Migration.md) (L461)
88. **L11427** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L873), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L7290), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L7303)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
89. **L11434** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1014), [Education.getGraduates](map/Education.md) (L776)
90. **L11435** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L940), [Education.getLicences](map/Education.md) (L792)
91. **L11438** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L793), [Education.getStudying](map/Education.md) (L585)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
92. **L11441** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L982)  
   _A licence holder is a graduate first, and the graduate count has just moved._
93. **L11443** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1804), [Education.getGrossCost](map/Education.md) (L839), [Education.getFees](map/Education.md) (L847)
94. **L11449** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7328)  
   _6c._
95. **L11453** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L1817), [Crime.getGrossCost](map/Crime.md) (L452)
96. **L11486** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L1570), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L1919), [Motoring.journeyFuel](map/Motoring.md) (L173)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
97. **L11488** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L220), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
98. **L11489** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L198), [Game.getInfrastructureManager](map/Game.md) (L1570)
99. **L11493** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L1855), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7239), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7328), [Game.portsBill](map/Game.md) (L5724), [Game.getInfrastructureManager](map/Game.md) (L1570), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
100. **L11505** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L747), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3823), [Game.unhousedShareOfCity](map/Game.md) (L11536), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
101. **L11521** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 143-205 (63 lines)

1. **L146** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L147** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L148** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L6680)
4. **L149** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L338), [Game.getFamilies](map/Game.md) (L10979)
5. **L155** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L335), [Game.getFamilies](map/Game.md) (L10979)  
   _The two inputs to the rent price, which is a market now._
6. **L158** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L339), [Game.getFamilies](map/Game.md) (L10979)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L162** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L336), [Game.marginalHousingCost](map/Game.md) (L14830)
8. **L164** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L1876), [Game.getCohorts](map/Game.md) (L10978)
9. **L170** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L171** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L7416)
11. **L174** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L165)
12. **L181** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L213)  
   _The split behind that total._
13. **L182** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L277)
14. **L183** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L281)
15. **L198** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L1570)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L201** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L10982)  
   _The fourth ratio._
17. **L202** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1492)

## SimulationEngine.updateServices() - SimulationEngine.java lines 207-214 (8 lines)

1. **L208** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L243), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197)
2. **L209** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L261), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
3. **L212** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L318), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L213** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L48)

## Game.run() - Game.java lines 648-651 (4 lines)

1. **L649** `initialize();` → [Game.initialize](map/Game.md) (L653)
2. **L650** `foundingBank();` → [Game.foundingBank](map/Game.md) (L783)

