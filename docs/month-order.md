# The order the month runs in

Generated 2026-10-31 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 11504-12415 (912 lines)

1. **L11512** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L11513** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L11514** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1090)
4. **L11515** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L11524** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L11525** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L14341)
7. **L11537** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L4489)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L11538** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L11284)
9. **L11540** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L13107)
10. **L11549** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L11550** `monthsSinceAutosave++;`
12. **L11556** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L8135)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
13. **L11559** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2323)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
14. **L11560** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L618)
15. **L11563** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L367)  
   _The register's month: nothing offered, nothing paid, until it is._
16. **L11564** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L505)
17. **L11565** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1109)
18. **L11566** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1642)
19. **L11584** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
20. **L11612** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
21. **L11613** `cityDiscountForBank = cityDiscountThisMonth;`
22. **L11614** `cityDebtRaisedThisMonth = 0;`
23. **L11615** `cityDiscountThisMonth = 0;`
24. **L11616** `cityPrincipalRepaidThisMonth = 0;`
25. **L11617** `bankPrincipalRepaidThisMonth = 0;`
26. **L11620** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
27. **L11621** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
28. **L11622** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
29. **L11623** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L477)
30. **L11624** `foreignDebtRaisedThisMonth = 0;`
31. **L11625** `foreignPrincipalRepaidThisMonth = 0;`
32. **L11626** `foreignInterestPaidThisMonth = 0;`
33. **L11631** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
34. **L11632** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
35. **L11633** `sectorInvested.clear();`
36. **L11634** `salvageThisMonth.clear();`
37. **L11635** `salvageBySector.clear();`
38. **L11636** `salvageUsedThisMonth = 0;`
39. **L11637** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
40. **L11638** `double pooledBefore = 0;`
41. **L11639** `for (double p : poolsBefore) pooledBefore += p;`
42. **L11640** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2076)
43. **L11642** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1324)
44. **L11643** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L2125)
45. **L11644** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L252)
46. **L11655** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
47. **L11656** `buybackAbroad = buybackAbroadUnsettled;`
48. **L11657** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
49. **L11658** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L562)
50. **L11661** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L4816)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
51. **L11680** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1315)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
52. **L11681** `if (fxRegime == ForeignAccounts.Regime.PEG) {` → [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.tiedRate](map/DebtManager.md) (L197), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1345), [DebtManager.isAutopilot](map/DebtManager.md) (L116), [DebtManager.getAim](map/DebtManager.md) (L161), [DebtManager.currencyRate](map/DebtManager.md) (L186), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L191), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L424)
53. **L11694** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L12939)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
54. **L11701** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L16919)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
55. **L11709** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L16405)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
56. **L11714** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L16029)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
57. **L11733** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L17044), [Game.studentGrantBill](map/Game.md) (L14092)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
58. **L11747** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L17069)  
   _...AND EI, THE SAME WAY (0.7.3)._
59. **L11765** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1707), [Bank.chargeTax](map/Bank.md) (L4016), [Game.bankProfitTaxRate](map/Game.md) (L3510)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
60. **L11767** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L12417)
61. **L11768** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
62. **L11769** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L13011)
63. **L11770** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2442)
64. **L11771** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L12921)
65. **L11775** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
66. **L11776** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L15822)
67. **L11777** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
68. **L11778** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1296)
69. **L11782** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L12959)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
70. **L11793** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
71. **L11795** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
72. **L11796** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L3026)
73. **L11800** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3184)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
74. **L11801** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L3020)
75. **L11817** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2808), [DebtManager.getAccretedForBank](map/DebtManager.md) (L2679)  
   _THE DISCOUNT ACCRETES (0.7.1)._
76. **L11818** `legacyDiscountDue = 0;`
77. **L11822** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
78. **L11823** `cityDiscountForBank = 0;`
79. **L11825** `double sectorInterestPaid = 0;`
80. **L11826** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L2291)
81. **L11832** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2789), [BondMarket.getCouponsStruck](map/BondMarket.md) (L1003)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
82. **L11833** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L567), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1010)
83. **L11835** `refreshBank();` → [Game.refreshBank](map/Game.md) (L3781)
84. **L11838** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L3949)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
85. **L11861** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3077), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1198), [BuildingManager.countByName](map/BuildingManager.md) (L6544), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1205)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
86. **L11864** `bankMaintenanceDue = 0;`
87. **L11872** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L3491), [Bank.openBranches](map/Bank.md) (L2727), [BuildingManager.countByName](map/BuildingManager.md) (L6544)  
   _Capital for whatever branches opened this month._
88. **L11917** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1189), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
89. **L11918** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L527), [DebtManager.countryPremium](map/DebtManager.md) (L962), [Bank.headroom](map/Bank.md) (L5497)
90. **L11924** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L603)
91. **L11925** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L611)
92. **L11926** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L628), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L580)
93. **L11930** `refreshBank();` → [Game.refreshBank](map/Game.md) (L3781)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
94. **L11940** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3382), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
95. **L11953** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L310), [Bank.getPlacementIncome](map/Bank.md) (L3656)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
96. **L11954** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L318), [Bank.getFundingCost](map/Bank.md) (L3654)
97. **L11955** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L303), [Bank.getBranches](map/Bank.md) (L5172), [Bank.wholesaleFunding](map/Bank.md) (L3362)
98. **L11960** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1898)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
99. **L11961** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L4076)
100. **L11965** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3816)  
   _The month is final, so the figure next month's tax is charged on is final too._
101. **L11969** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L388)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
102. **L11986** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L12907)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
103. **L11987** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L2869)
104. **L11997** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3892), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3298)  
   _...AND THE SAVERS ARE PAID._
105. **L11998** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3299)
106. **L12005** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1514)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
107. **L12006** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L620), [EconomyManager.setSectorCash](map/EconomyManager.md) (L625), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1516)
108. **L12037** `payDividends();` → [Game.payDividends](map/Game.md) (L3619)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
109. **L12038** `tradeShares();` → [Game.tradeShares](map/Game.md) (L3691)
110. **L12048** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L4250)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
111. **L12058** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1140), [Exchange.cityMarketValue](map/Exchange.md) (L1711)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
112. **L12059** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L3892)
113. **L12064** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L452), [DebtManager.firmBookRatio](map/DebtManager.md) (L2620), [DebtManager.firmBookYield](map/DebtManager.md) (L2626)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
114. **L12065** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L248), [Bank.depositRate](map/Bank.md) (L3291), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
115. **L12069** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3039), [Bank.depositRate](map/Bank.md) (L3291), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...and the households, by the same rule, with what the owners were just paid._
116. **L12072** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3375), [DebtManager.householdBookYield](map/DebtManager.md) (L2649), [Bank.depositRate](map/Bank.md) (L3291)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
117. **L12090** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1898)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
118. **L12091** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L4076)
119. **L12092** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L4171)
120. **L12127** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L606)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
121. **L12128** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L651)
122. **L12131** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L623), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1356)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
123. **L12132** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L378), [Bank.depositRate](map/Bank.md) (L3291), [DebtManager.getRate](map/DebtManager.md) (L1157), [DebtManager.countryPremium](map/DebtManager.md) (L962), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2436), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L2065), [Game.yearlyDepreciation](map/Game.md) (L14446), [Bank.isInsolvent](map/Bank.md) (L1806), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1095)
124. **L12154** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L606)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
125. **L12155** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1682)
126. **L12156** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1696)
127. **L12157** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1670), [CapitalFlows.getStock](map/CapitalFlows.md) (L606)
128. **L12159** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L15795)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
129. **L12161** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L489)
130. **L12164** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
131. **L12174** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L1914), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2436)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
132. **L12206** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L244), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L298), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L345)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
133. **L12225** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L373), [Game.realRateDifferential](map/Game.md) (L10685)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
134. **L12231** `handTheWorldToTheD();` → [Game.handTheWorldToTheD](map/Game.md) (L1650)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
135. **L12232** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
136. **L12233** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L891)
137. **L12240** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1358), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1361), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
138. **L12247** `noteTheWorldMoneys(rateBeforeReprice);` → [Game.noteTheWorldMoneys](map/Game.md) (L1657)  
   _...AND THE WORLD'S NEWS (WN4): money that ran to safety abroad, a world peg that broke._
139. **L12255** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L668), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L847)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
140. **L12273** `economyManager.getNationalAccounts().setDebtExchangeGain(debtManager.revalueForeign(foreign.getRate()));` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [DebtManager.revalueForeign](map/DebtManager.md) (L757), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _CD7: the same press books the exchange gain (loss) on the dollar paper - each piece's carrying value in dollars times the move from the rate it was last valued at - on the month the strike already ..._
141. **L12274** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L2012), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L775), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
142. **L12281** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L2373)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
143. **L12282** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L870), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L1890), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L1871)
144. **L12283** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1088)
145. **L12284** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L10660)
146. **L12297** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L14245), [Game.indexSpends](map/Game.md) (L14273), [Game.indexFees](map/Game.md) (L14288), [Game.indexFeeSpends](map/Game.md) (L14304)  
   _...AND WHAT THE MONTH COST A FAMILY._
147. **L12309** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L244)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
148. **L12310** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L499), [DebtManager.holdingRate](map/DebtManager.md) (L453), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
149. **L12313** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L14366)
150. **L12316** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _A year of the rate, so next year can tell a drift from a run._
151. **L12317** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
152. **L12319** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L16602)
153. **L12321** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L898)
154. **L12355** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
155. **L12356** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
156. **L12375** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L16845)
157. **L12377** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L2735)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
158. **L12379** `tallyThePartners();` → [Game.tallyThePartners](map/Game.md) (L1347)  
   _The month's trade split to the world's nations (WN3; THE WORLD MARKET), after its last draw: before the history and the autosave._
159. **L12380** `recordMonth();` → [Game.recordMonth](map/Game.md) (L14755)
160. **L12382** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
161. **L12384** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L2020)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
162. **L12404** `reportSaves();` → [Game.reportSaves](map/Game.md) (L14904)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
163. **L12405** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L14837)
164. **L12414** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L2296)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._

## Game.startOfMonthUpdate() - Game.java lines 12417-12715 (299 lines)

1. **L12469** `ensureWorldMarket();` → [Game.ensureWorldMarket](map/Game.md) (L1273)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L12470** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L260)
3. **L12471** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L309), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L298)
4. **L12473** `strikeTheWorldMarket();` → [Game.strikeTheWorldMarket](map/Game.md) (L1317)  
   _...AND THE WORLD'S BAND BEHIND IT (WN3): the January solve when the year has turned, then every good's floor and ceiling from last month's trade; before any market clears._
5. **L12487** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L365), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
6. **L12511** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L747), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
7. **L12513** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L337), [LandManager.getPricePerSqFt](map/LandManager.md) (L628)
8. **L12514** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L8906)
9. **L12524** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L12907)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
10. **L12529** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5172), [Bank.isInsolvent](map/Bank.md) (L1806)  
   _A failed bank lends nothing._
11. **L12542** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L3892)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
12. **L12543** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4921)
13. **L12544** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4949)
14. **L12549** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4604)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
15. **L12550** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L581)
16. **L12557** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L3932)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
17. **L12558** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L643), [Bank.prime](map/Bank.md) (L1135), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1156)
18. **L12562** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L965)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
19. **L12567** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L812)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
20. **L12571** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L6739)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
21. **L12575** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L6688)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
22. **L12578** `outward.payCoupons(DebtManager.WORLD_BASE_RATE, foreign.getRate(), economyManager);` → [OutwardInvestment.payCoupons](map/OutwardInvestment.md) (L218), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...and the coupon on the dollars its businesses hold abroad, struck as a line of their profit and kept abroad: the profit tax and the dividend see it, the till pays them (HD1, 2026-10-29; HD2, 2026..._
23. **L12592** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L2291)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
24. **L12593** `double constructionWorkDone = construction.getRecognisedThisMonth();`
25. **L12602** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1226)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
26. **L12604** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L2296), [Game.getSectors](map/Game.md) (L2291)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
27. **L12606** `settleReserve();` → [Game.settleReserve](map/Game.md) (L4911)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
28. **L12610** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L2291)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
29. **L12616** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
30. **L12617** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2100), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L272), [Healthcare.getGrossCost](map/Healthcare.md) (L725), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L839), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2039), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L630), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L632), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L823)
31. **L12671** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
32. **L12672** `monthlyMaterialImportBill = 0;`
33. **L12677** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L2899)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
34. **L12685** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L246)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
35. **L12693** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
36. **L12702** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
37. **L12706** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1455)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
38. **L12712** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L2776)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
39. **L12714** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L12716)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-127 (96 lines)

1. **L66** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L6669)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L67** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L13911), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5305)
3. **L75** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L7715), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5381)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L79** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5433)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
5. **L80** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L7823), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6507), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5397)
6. **L86** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L7763), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L6516)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
7. **L91** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L8425), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5426)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
8. **L100** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L167)  
   _Roads, before anything reads them._
9. **L107** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L129)  
   _Recount the posts and refresh the wage arrays._
10. **L123** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L13257)  
   _THE POPULATION STEP._
11. **L125** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L143)
12. **L126** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L207)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 129-141 (13 lines)

1. **L134** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L6594)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L135** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L13129)
3. **L136** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L13141)
4. **L139** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L14670)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L140** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L332)

## Game.advanceDemographics() - Game.java lines 13257-13773 (517 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L13262** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L561), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L13276** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L13277** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L13804)
4. **L13278** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L13804)
5. **L13279** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L13804)
6. **L13280** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7315)
7. **L13282** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
8. **L13296** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L13297** `java.util.Arrays.fill(affordable, 1);`
10. **L13298** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L13823)
11. **L13301** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L13302** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L13303** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L13306** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L641)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L13309** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L13310** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7315)
17. **L13324** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L13337** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L401)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L13348** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L13349** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L13350** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L13351** `double[] inBand = new double[AgeBand.values().length];`
23. **L13352** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L94)
24. **L13356** `double[] careFactors = mortalityFactors;`
25. **L13357** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L466), [Healthcare.mortalityFactors](map/Healthcare.md) (L401)
26. **L13367** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L13370** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L13377** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L13378** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L94)
30. **L13379** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
31. **L13381** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L164), [Healthcare.birthFactor](map/Healthcare.md) (L420)
32. **L13383** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L270), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L101)
33. **L13387** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L13388** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L104)
35. **L13389** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L101)
36. **L13390** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L106)
37. **L13391** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L13392** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L502), [Healthcare.mortalityFactors](map/Healthcare.md) (L401)
39. **L13395** `lastOrphanDeaths = outsideDead[0];`
40. **L13396** `lastUnhousedDeaths = outsideDead[1];`
41. **L13403** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L368), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L13406** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L13419** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L94)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L13426** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L906), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3736), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L13435** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L302), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L443)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L13439** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L343), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
47. **L13444** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L788), [BuildingManager.homesBySize](map/BuildingManager.md) (L6830)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
48. **L13446** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L340), [Migration.monthlyNet](map/Migration.md) (L801), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L13133), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L6809), [PopulationCohorts.share](map/PopulationCohorts.md) (L122)
49. **L13461** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
50. **L13473** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L14695)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
51. **L13475** `double[] jobsByTier = new double[PayTier.values().length];`
52. **L13476** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
53. **L13477** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1118)
54. **L13479** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
55. **L13491** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
56. **L13492** `double[] wageByTier = new double[PayTier.values().length];`
57. **L13493** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239)
58. **L13494** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
59. **L13497** `for (int t = 0; t < wageByTier.length; t++) {`
60. **L13513** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7391)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
61. **L13514** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L727)
62. **L13515** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
63. **L13516** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L115), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7391), [Game.unskilledWage](map/Game.md) (L14068)
64. **L13523** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
65. **L13525** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L788), [Crime.prisoners](map/Crime.md) (L370)
66. **L13526** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
67. **L13528** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1092)
68. **L13529** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L794)
69. **L13530** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L14068), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
70. **L13534** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L456), [PopulationCohorts.share](map/PopulationCohorts.md) (L122)  
   _The month's adult arrivals look for work next month._
71. **L13544** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L370)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
72. **L13545** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
73. **L13549** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3700), [Education.getFinished](map/Education.md) (L898)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
74. **L13561** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L6830)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
75. **L13562** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
76. **L13563** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1297)
77. **L13564** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1384), [FamilyModel.house](map/FamilyModel.md) (L1128)
78. **L13569** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L2291), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
79. **L13573** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1161)  
   _And the advisor prices a new home on who would move into it._
80. **L13574** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1166)
81. **L13590** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1631), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L863)  
   _...and the ones who cannot afford one either._
82. **L13592** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L970), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
83. **L13631** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L158), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
84. **L13632** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L478), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7467), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7556), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L96), [Game.burialShare](map/Game.md) (L13885), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7278), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7315)
85. **L13642** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1975), [Healthcare.getGrossCost](map/Healthcare.md) (L725), [Healthcare.getFees](map/Healthcare.md) (L727)
86. **L13657** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L2924), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
87. **L13658** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L398), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7354), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7467), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7556), [Migration.getLastDepartures](map/Migration.md) (L461)
88. **L13677** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L873), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L7518), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L7531)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
89. **L13684** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1014), [Education.getGraduates](map/Education.md) (L776)
90. **L13685** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L940), [Education.getLicences](map/Education.md) (L792)
91. **L13688** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L793), [Education.getStudying](map/Education.md) (L585)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
92. **L13691** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L982)  
   _A licence holder is a graduate first, and the graduate count has just moved._
93. **L13693** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1984), [Education.getGrossCost](map/Education.md) (L839), [Education.getFees](map/Education.md) (L847)
94. **L13699** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7467), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7556)  
   _6c._
95. **L13703** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L1997), [Crime.getGrossCost](map/Crime.md) (L452)
96. **L13736** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L2301), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L1919), [Motoring.journeyFuel](map/Motoring.md) (L173)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
97. **L13738** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L223), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
98. **L13739** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L198), [Game.getInfrastructureManager](map/Game.md) (L2301)
99. **L13743** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2035), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7467), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7556), [Game.portsBill](map/Game.md) (L6710), [Game.getInfrastructureManager](map/Game.md) (L2301), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
100. **L13755** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L752), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3839), [Game.unhousedShareOfCity](map/Game.md) (L13786), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
101. **L13771** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 143-205 (63 lines)

1. **L146** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L147** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L148** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L6874)
4. **L149** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L507), [Game.getFamilies](map/Game.md) (L13229)
5. **L155** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L504), [Game.getFamilies](map/Game.md) (L13229)  
   _The two inputs to the rent price, which is a market now._
6. **L158** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L508), [Game.getFamilies](map/Game.md) (L13229)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L162** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L505), [Game.marginalHousingCost](map/Game.md) (L17622)
8. **L164** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2056), [Game.getCohorts](map/Game.md) (L13228)
9. **L170** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L171** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L7644)
11. **L174** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L165)
12. **L181** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L213)  
   _The split behind that total._
13. **L182** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L280)
14. **L183** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L284)
15. **L198** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L2301)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L201** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L13232)  
   _The fourth ratio._
17. **L202** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1672)

## SimulationEngine.updateServices() - SimulationEngine.java lines 207-214 (8 lines)

1. **L208** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L246), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197)
2. **L209** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L264), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
3. **L212** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L321), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L213** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L48)

## Game.run() - Game.java lines 686-690 (5 lines)

1. **L687** `initialize();` → [Game.initialize](map/Game.md) (L692)
2. **L688** `foundingBank();` → [Game.foundingBank](map/Game.md) (L823)
3. **L689** `foundingYard();` → [Game.foundingYard](map/Game.md) (L907)

