# The order the month runs in

Generated 2026-11-22 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 13743-14720 (978 lines)

1. **L13751** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L13752** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L13753** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1249)
4. **L13754** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L13763** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L13764** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L16905)
7. **L13776** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L5915)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L13777** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L13520)
9. **L13779** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L15517)
10. **L13788** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L13789** `monthsSinceAutosave++;`
12. **L13796** `policyHearsTheMonth();` → [Game.policyHearsTheMonth](map/Game.md) (L1226)  
   _THE CITY HEARS THE MONTH FIRST (R0): the dollar its gold years, the households the pension, the buildings their recipes and forms, before anything in this month is taxed or paid (every Policy lever..._
13. **L13798** `basketHearsTheYear();` → [Game.basketHearsTheYear](map/Game.md) (L16966)  
   _...and the homes' lamp and stove shares of the year (R0e), in a city founded the 1900 way, before this month's fuel is struck (6d)._
14. **L13804** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L10179)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
15. **L13810** `strikeResearchPosts();` → [Game.strikeResearchPosts](map/Game.md) (L1482)  
   _THE RESEARCH POSTS ARE STRUCK NEXT (R3), AFTER THE DEMOLITIONS (R3b): what the month's grants buy within the schools' room, at the wages the labour market has now._
16. **L13813** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2327)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
17. **L13814** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L742)
18. **L13817** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L420)  
   _The register's month: nothing offered, nothing paid, until it is._
19. **L13818** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L535)
20. **L13819** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1263)
21. **L13820** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1694)
22. **L13838** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
23. **L13866** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
24. **L13867** `cityDiscountForBank = cityDiscountThisMonth;`
25. **L13868** `cityDebtRaisedThisMonth = 0;`
26. **L13869** `cityDiscountThisMonth = 0;`
27. **L13870** `cityPrincipalRepaidThisMonth = 0;`
28. **L13871** `bankPrincipalRepaidThisMonth = 0;`
29. **L13874** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
30. **L13875** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
31. **L13876** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
32. **L13877** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L477)
33. **L13878** `foreignDebtRaisedThisMonth = 0;`
34. **L13879** `foreignPrincipalRepaidThisMonth = 0;`
35. **L13880** `foreignInterestPaidThisMonth = 0;`
36. **L13885** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
37. **L13886** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
38. **L13887** `sectorInvested.clear();`
39. **L13888** `salvageThisMonth.clear();`
40. **L13889** `salvageBySector.clear();`
41. **L13890** `salvageUsedThisMonth = 0;`
42. **L13891** `householdBalance.beginTaxMonth();` → [HouseholdBalance.beginTaxMonth](map/HouseholdBalance.md) (L471)
43. **L13892** `estateCustody.beginMonth();` → [EstateCustody.beginMonth](map/EstateCustody.md) (L39)
44. **L13893** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L615)
45. **L13894** `double householdsBefore = MoneyAudit.householdsHeld(this);` → [MoneyAudit.householdsHeld](map/MoneyAudit.md) (L380)
46. **L13895** `double pooledBefore = 0;`
47. **L13896** `for (double p : poolsBefore) pooledBefore += p;`
48. **L13897** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2179)
49. **L13899** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1326)
50. **L13900** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L2859)
51. **L13901** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L268)
52. **L13912** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
53. **L13913** `buybackAbroad = buybackAbroadUnsettled;`
54. **L13914** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
55. **L13915** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L580)
56. **L13918** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L6328)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
57. **L13920** `centralBank.settleGoldRedeemed();` → [CentralBank.settleGoldRedeemed](map/CentralBank.md) (L753)  
   _...and the D$ the gold the mint paid out at the last reprice took back are retired, M0 down with the gold (FB14)._
58. **L13944** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1722)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
59. **L13945** `if (fxRegime == ForeignAccounts.Regime.PEG) {   // Canada's gold (R0f) is a peg to the US$ at par, so it is tied here...` → [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.tiedRate](map/DebtManager.md) (L225), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1824), [DebtManager.isAutopilot](map/DebtManager.md) (L116), [DebtManager.getAim](map/DebtManager.md) (L185), [DebtManager.goldRate](map/DebtManager.md) (L241), [Game.getGoldCover](map/Game.md) (L6242), [DebtManager.currencyRate](map/DebtManager.md) (L214), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L219), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L473)
60. **L13961** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L15285)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
61. **L13968** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L20039)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
62. **L13976** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L19490)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
63. **L13981** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L19116)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
64. **L14000** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L20171), [Game.studentGrantBill](map/Game.md) (L16654)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
65. **L14014** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L20196)  
   _...AND EI, THE SAME WAY (0.7.3)._
66. **L14032** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1763), [Bank.chargeTax](map/Bank.md) (L4065), [Game.bankProfitTaxRate](map/Game.md) (L4894)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
67. **L14036** `getMarkets().startSellerPricedMonth();` → [Game.getMarkets](map/Game.md) (L3214)  
   _FB3 (FW6-C): the seller-priced goods' markets begin their month here, as the traded goods' do in their clearing: their trades and tally grew for the city's whole life._
68. **L14037** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L14722)
69. **L14038** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
70. **L14039** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L15360)
71. **L14040** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2564)
72. **L14041** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L15267)
73. **L14045** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
74. **L14046** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L18883)
75. **L14047** `estateCustody.consolidatePaper();` → [EstateCustody.consolidatePaper](map/EstateCustody.md) (L107)
76. **L14048** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
77. **L14049** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1586)
78. **L14053** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L15305)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
79. **L14064** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
80. **L14066** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
81. **L14067** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L3073)
82. **L14071** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3233)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
83. **L14072** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L3067)
84. **L14088** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2812), [DebtManager.getAccretedForBank](map/DebtManager.md) (L3043)  
   _THE DISCOUNT ACCRETES (0.7.1)._
85. **L14089** `legacyDiscountDue = 0;`
86. **L14093** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
87. **L14094** `cityDiscountForBank = 0;`
88. **L14096** `double sectorInterestPaid = 0;`
89. **L14097** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L3209)
90. **L14103** `double issuerCoupons = bondMarket.getCouponsStruck();` → [BondMarket.getCouponsStruck](map/BondMarket.md) (L1140)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
91. **L14104** `bank.takeInterest(interestDue, sectorInterestPaid - issuerCoupons);` → [Bank.takeInterest](map/Bank.md) (L2793)
92. **L14105** `bondMarket.recordCouponIssuerCharge(issuerCoupons);` → [BondMarket.recordCouponIssuerCharge](map/BondMarket.md) (L291)
93. **L14106** `double bankCoupon = bondMarket.getCouponsDueToBank();` → [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1148)
94. **L14107** `bank.takeBondCoupons(bankCoupon);` → [Bank.takeBondCoupons](map/Bank.md) (L569)
95. **L14108** `bondMarket.recordCouponBankPayment(bankCoupon);` → [BondMarket.recordCouponBankPayment](map/BondMarket.md) (L299)
96. **L14110** `refreshBank();` → [Game.refreshBank](map/Game.md) (L5171)
97. **L14111** `lender.projectLoansSettled();`
98. **L14114** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L5339)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
99. **L14137** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3124), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L145), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1232), [BuildingManager.countByName](map/BuildingManager.md) (L7098), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1239)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
100. **L14142** `getSectors().businessServices().sellBankUpkeep(bank.getOperatingCost());` → [Game.getSectors](map/Game.md) (L3209), [Bank.getOperatingCost](map/Bank.md) (L3172)  
   _...and its running cost is bought of Business Services, as the city's upkeep is (HL1; buyUpkeep()): the bank paid it out of its own cash above._
101. **L14143** `bankMaintenanceDue = 0;`
102. **L14151** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L4875), [Bank.openBranches](map/Bank.md) (L2731), [BuildingManager.countByName](map/BuildingManager.md) (L7098)  
   _Capital for whatever branches opened this month._
103. **L14196** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1191), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
104. **L14198** `hotMoney.setRiskOff(worldRiskOffLevel());` → [CapitalFlows.setRiskOff](map/CapitalFlows.md) (L707), [Game.worldRiskOffLevel](map/Game.md) (L2369)  
   _The risk-off the world is in (XR4): handed once here, it holds for the hot money's month below as well._
105. **L14199** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L542), [DebtManager.worldRate](map/DebtManager.md) (L866), [DebtManager.countryPremium](map/DebtManager.md) (L1192), [Bank.headroom](map/Bank.md) (L5576)
106. **L14205** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L605)
107. **L14206** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L613)
108. **L14207** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L630), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L608)
109. **L14211** `refreshBank();` → [Game.refreshBank](map/Game.md) (L5171)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
110. **L14221** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3431), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
111. **L14234** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L328), [Bank.getPlacementIncome](map/Bank.md) (L3705)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
112. **L14235** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L336), [Bank.getFundingCost](map/Bank.md) (L3703)
113. **L14236** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L321), [Bank.getBranches](map/Bank.md) (L5227), [Bank.wholesaleFunding](map/Bank.md) (L3411)
114. **L14241** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
115. **L14242** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5466)
116. **L14246** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3865)  
   _The month is final, so the figure next month's tax is charged on is final too._
117. **L14250** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L406)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
118. **L14267** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L15253)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
119. **L14268** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L3233)
120. **L14278** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L5360), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3347)  
   _...AND THE SAVERS ARE PAID._
121. **L14279** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3348)
122. **L14286** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1554)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
123. **L14287** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L653), [EconomyManager.setSectorCash](map/EconomyManager.md) (L658), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1556)
124. **L14318** `payDividends();` → [Game.payDividends](map/Game.md) (L5003)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
125. **L14319** `tradeShares();` → [Game.tradeShares](map/Game.md) (L5075)
126. **L14329** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L5649)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
127. **L14339** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1296), [Exchange.cityMarketValue](map/Exchange.md) (L1957)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
128. **L14340** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L5282)
129. **L14345** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L452), [DebtManager.firmBookRatio](map/DebtManager.md) (L2984), [DebtManager.firmBookYield](map/DebtManager.md) (L2990)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
130. **L14346** `outward.takeMonth(bank.depositRate(), debtManager.worldRate(),` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L248), [Bank.depositRate](map/Bank.md) (L3340), [DebtManager.worldRate](map/DebtManager.md) (L866), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
131. **L14350** `householdBalance.investAbroad(bank.depositRate(), debtManager.worldRate(), foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L4444), [Bank.depositRate](map/Bank.md) (L3340), [DebtManager.worldRate](map/DebtManager.md) (L866), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)  
   _...and the households, by the same rule, with what the owners were just paid._
132. **L14351** `estateCustody.payForeignCoupon(debtManager.worldRate(), foreign.getRate());` → [EstateCustody.payForeignCoupon](map/EstateCustody.md) (L111), [DebtManager.worldRate](map/DebtManager.md) (L866), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
133. **L14354** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L4780), [DebtManager.householdBookYield](map/DebtManager.md) (L3013), [Bank.depositRate](map/Bank.md) (L3340)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
134. **L14372** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
135. **L14373** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5466)
136. **L14374** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L5572)
137. **L14409** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
138. **L14410** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L748)
139. **L14413** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L651), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1836)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
140. **L14414** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L380), [Bank.depositRate](map/Bank.md) (L3340), [DebtManager.getRate](map/DebtManager.md) (L1413), [DebtManager.worldRate](map/DebtManager.md) (L866), [DebtManager.countryPremium](map/DebtManager.md) (L1192), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2558), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L2799), [Game.yearlyDepreciation](map/Game.md) (L17032), [Bank.isInsolvent](map/Bank.md) (L1808), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1351)
141. **L14436** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
142. **L14437** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1684)
143. **L14438** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1698)
144. **L14439** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1672), [CapitalFlows.getStock](map/CapitalFlows.md) (L634)
145. **L14441** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L18856)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
146. **L14443** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L798)
147. **L14444** `lastHouseholdsAudit = MoneyAudit.households(this, householdsBefore, lastMoneyAudit);` → [MoneyAudit.households](map/MoneyAudit.md) (L453)
148. **L14447** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
149. **L14457** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L2650), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2558)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
150. **L14489** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L246), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L315), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L362)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
151. **L14508** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L375), [Game.realRateDifferential](map/Game.md) (L12841)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
152. **L14514** `handTheWorldToTheD();` → [Game.handTheWorldToTheD](map/Game.md) (L2490)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
153. **L14515** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
154. **L14516** `boolean onGoldBeforeReprice = foreign.getRegime() == ForeignAccounts.Regime.GOLD \|\| foreign.isCanadaGold();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1722), [ForeignAccounts.isCanadaGold](map/ForeignAccounts.md) (L1532)
155. **L14517** `foreign.setMoneyBase(centralBank.m0(), centralBank.goldRetirable());   // M0 is the gold cover's base, handed right b...` → [ForeignAccounts.setMoneyBase](map/ForeignAccounts.md) (L1619), [CentralBank.m0](map/CentralBank.md) (L863), [CentralBank.goldRetirable](map/CentralBank.md) (L873)
156. **L14518** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L913)  
   _M0 is the gold cover's base, handed right before the reprice (GD3), and what a sale can retire of it (FB14)_
157. **L14526** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1838), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1843), [Game.getCurrency](map/Game.md) (L2953), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259), [DebtManager.dropGoldAim](map/DebtManager.md) (L247)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
158. **L14535** `noteTheWorldMoneys(rateBeforeReprice);` → [Game.noteTheWorldMoneys](map/Game.md) (L2497)  
   _...AND THE WORLD'S NEWS (WN4): money that ran to safety abroad, a world peg that broke._
159. **L14546** `if (foreign.getRegime() == ForeignAccounts.Regime.GOLD) centralBank.goldPaidOut(foreign.getDefenceLocal());` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1722), [CentralBank.goldPaidOut](map/CentralBank.md) (L744), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L869)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
160. **L14547** `else centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L687), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L869)
161. **L14565** `economyManager.getNationalAccounts().setDebtExchangeGain(debtManager.revalueForeign(foreign.getRate()));` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [DebtManager.revalueForeign](map/DebtManager.md) (L950), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)  
   _CD7: the same press books the exchange gain (loss) on the dollar paper - each piece's carrying value in dollars times the move from the rate it was last valued at - on the month the strike already ..._
162. **L14566** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L2753), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L971), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
163. **L14573** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L3149)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
164. **L14574** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L1100), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L2626), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L2607)
165. **L14575** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1344)
166. **L14576** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L12815)
167. **L14589** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L16809), [Game.indexSpends](map/Game.md) (L16837), [Game.indexFees](map/Game.md) (L16852), [Game.indexFeeSpends](map/Game.md) (L16868)  
   _...AND WHAT THE MONTH COST A FAMILY._
168. **L14601** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L293)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
169. **L14602** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L548), [DebtManager.holdingRate](map/DebtManager.md) (L502), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
170. **L14605** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L16931)
171. **L14608** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)  
   _A year of the rate, so next year can tell a drift from a run._
172. **L14609** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
173. **L14611** `economyManager.getNationalAccounts().setInvestmentTaxes(householdBalance.getCapitalGainsTax(), householdBalance.getDi...` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [HouseholdBalance.getCapitalGainsTax](map/HouseholdBalance.md) (L555), [HouseholdBalance.getDividendTax](map/HouseholdBalance.md) (L556)
174. **L14612** `economyManager.getNationalAccounts().setCapitalTaxClaims(householdBalance.getCapitalTaxAssessed(),` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [HouseholdBalance.getCapitalTaxAssessed](map/HouseholdBalance.md) (L559), [HouseholdBalance.getCapitalTaxOutstanding](map/HouseholdBalance.md) (L564), [HouseholdBalance.getCapitalTaxWrittenOff](map/HouseholdBalance.md) (L560), [HouseholdBalance.getCapitalTaxConsolidated](map/HouseholdBalance.md) (L561)
175. **L14614** `economyManager.getNationalAccounts().setEstateCustody(estateCustody);` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56)
176. **L14615** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L19702)
177. **L14616** `householdBalance.closeTaxMonth(); estateCustody.closeMonth();` → [HouseholdBalance.closeTaxMonth](map/HouseholdBalance.md) (L476)
178. **L14616** `householdBalance.closeTaxMonth(); estateCustody.closeMonth();` → [EstateCustody.closeMonth](map/EstateCustody.md) (L43)
179. **L14618** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L979)
180. **L14652** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
181. **L14653** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L615)
182. **L14672** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L19965)
183. **L14674** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L3859)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
184. **L14676** `tallyThePartners();` → [Game.tallyThePartners](map/Game.md) (L2056)  
   _The month's trade split to the world's nations (WN3; THE WORLD MARKET), after its last draw: before the history and the autosave._
185. **L14677** `recordMonth();` → [Game.recordMonth](map/Game.md) (L17355)
186. **L14679** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
187. **L14681** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L2931)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
188. **L14687** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L3214)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._
189. **L14708** `reportSaves();` → [Game.reportSaves](map/Game.md) (L17530)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
190. **L14709** `SaveWriter.Job autosaved = null;`
191. **L14710** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosaveJob](map/Game.md) (L17452)
192. **L14719** `recordForReplay(autosaved);` → [Game.recordForReplay](map/Game.md) (L18206)  
   _A recorded city keeps this whole month, beside the autosave (TR1; THE RECORDING), from the autosave's fill when it made one (TR2)._

## Game.startOfMonthUpdate() - Game.java lines 14722-15045 (324 lines)

1. **L14774** `ensureWorldMarket();` → [Game.ensureWorldMarket](map/Game.md) (L1968)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L14775** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L270)
3. **L14776** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L338), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L315)
4. **L14778** `strikeTheWorldMarket();` → [Game.strikeTheWorldMarket](map/Game.md) (L2021)  
   _...AND THE WORLD'S BAND BEHIND IT (WN3): the January solve when the year has turned, then every good's floor and ceiling from last month's trade; before any market clears._
5. **L14780** `double gold = goldUsdPerOunce(WorldMoneys.monthIndex(month));` → [Game.goldUsdPerOunce](map/Game.md) (L2343), [WorldMoneys.monthIndex](map/WorldMoneys.md) (L270)  
   _Bullion's nominal US$ path and spot exchange already include the dollar's price path (GD4)._
6. **L14781** `if (Double.isFinite(gold) && gold > 0) {` → [EconomyManager.getMarkets](map/EconomyManager.md) (L51), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
7. **L14800** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L476), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
8. **L14807** `labourMarket.followAverageWage(populationManager.getAverageWage());` → [LabourMarket.followAverageWage](map/LabourMarket.md) (L983), [PopulationManager.getAverageWage](map/PopulationManager.md) (L169)  
   _The floor's tie to the average wage (WG1) is struck once a month from last month's wages, before repriceLabour() walks them._
9. **L14827** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L753), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
10. **L14829** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L366), [LandManager.getPricePerSqFt](map/LandManager.md) (L634)
11. **L14830** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L11001)
12. **L14840** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L15253)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
13. **L14845** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5227), [Bank.isInsolvent](map/Bank.md) (L1808)  
   _A failed bank lends nothing._
14. **L14858** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L5282)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
15. **L14859** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4970)
16. **L14860** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L5004)
17. **L14865** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4653)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
18. **L14866** `economyManager.getBusinessDebtManager().setProjectBudget(bank);` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
19. **L14867** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L868)
20. **L14874** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L5322)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
21. **L14875** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L676), [Bank.prime](map/Bank.md) (L1137), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1158)
22. **L14879** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L1093)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
23. **L14884** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L846)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
24. **L14888** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L8683)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
25. **L14892** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L8632)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
26. **L14895** `outward.payCoupons(debtManager.worldRate(), foreign.getRate(), economyManager);` → [OutwardInvestment.payCoupons](map/OutwardInvestment.md) (L218), [DebtManager.worldRate](map/DebtManager.md) (L866), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)  
   _...and the coupon on the dollars its businesses hold abroad, struck as a line of their profit and kept abroad: the profit tax and the dividend see it, the till pays them (HD1, 2026-10-29; HD2, 2026..._
27. **L14909** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L3209)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
28. **L14910** `double constructionWorkDone = construction.getRecognisedThisMonth();`
29. **L14912** `if (construction.getFittingThisMonth() != 0) constructionWorkDone += construction.getFittingThisMonth();`  
   _...and the equipment the builders were paid to fit to standing buildings since the last strike banked its ledger (R1b): capital too, on the builders' side, once._
30. **L14915** `buyPowerFuel();` → [Game.buyPowerFuel](map/Game.md) (L6424)  
   _The coal the utility burned last month, bought from the market before the strike banks its sellers' ledgers (PW1; buyPowerFuel())._
31. **L14924** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1260)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
32. **L14926** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L3214), [Game.getSectors](map/Game.md) (L3209)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
33. **L14928** `settleReserve();` → [Game.settleReserve](map/Game.md) (L6447)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
34. **L14930** `settlePublicLicences();` → [Game.settlePublicLicences](map/Game.md) (L6464)  
   _...and the licences the city's own patented works accrued last month (R2c-P), paid abroad in the same strike._
35. **L14934** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L3209)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
36. **L14940** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
37. **L14942** `economyManager.setUtilityImports(servicesManager.getUtilitiesHandler().getFuelImported() + servicesManager.getUtiliti...` → [EconomyManager.setUtilityImports](map/EconomyManager.md) (L106), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L321)  
   _...and the coal the utility bought from the world and the upkeep it paid abroad (PW1; buyPowerFuel())._
38. **L14943** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2203), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L321), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L844), [EconomyManager.getResearchBill](map/EconomyManager.md) (L2076), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2117), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L636), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L638), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L857)
39. **L15001** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
40. **L15002** `monthlyMaterialImportBill = 0;`
41. **L15007** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L4237)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
42. **L15015** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L248)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
43. **L15023** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
44. **L15032** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
45. **L15036** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1489)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
46. **L15042** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L3900)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
47. **L15044** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L15046)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-161 (130 lines)

1. **L33** `game.moneyHearsTheMonth();` → [Game.moneyHearsTheMonth](map/Game.md) (L1215)
2. **L68** `int siteOutput = game.workTheFittings();` → [Game.workTheFittings](map/Game.md) (L1714)  
   _...less the fittings the crews work first (R1b; Game.workTheFittings()): after the repairs, before the sites, struck here once and used for the advance and for the work the sector is paid for below._
3. **L71** `Game.ForeignOffer foreign = game.foreignCrewsOffer();` → [Game.foreignCrewsOffer](map/Game.md) (L8584)  
   _The foreign contractor's month beside the city's own (FC1): nothing unless the repairs took the builders' points and left the sites none - see Game, THE FOREIGN CREWS._
4. **L75** `game.getBusinessInvestment().beforeSitesAdvance();` → [Game.getBusinessInvestment](map/Game.md) (L3830)  
   _His points are in the month the sites take, and not on a rushed site's overtime (FC1b): his crews are not the city's._
5. **L76** `java.util.List<BuildingManager.Completion> opened = buildingManager.advanceConstruction(siteOutput + foreign.points()...` → [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5568)
6. **L77** `game.getBusinessInvestment().afterSitesAdvance();` → [Game.getBusinessInvestment](map/Game.md) (L3830)
7. **L78** `game.recordCompletions(opened);` → [Game.recordCompletions](map/Game.md) (L16399)
8. **L81** `game.fitWhatFinished(opened);` → [Game.fitWhatFinished](map/Game.md) (L1400)  
   _...and what the buildings that opened come with (R1b): in a city founded the 1900 way, only the equipment that pays for a new building; nothing in a city founded the default way, where every buildi..._
9. **L97** `game.getRecipes().apply(game);   // the ways the buildings run, at the counts the month leaves (R1c): first, the stea...` → [Game.getRecipes](map/Game.md) (L1274)  
   _...AND THE 1900 FORMS AT REST (R1b; R1's stranger's review, 2026-11-05): the shares the counts set, said again now that the month has changed the counts' weights._
10. **L98** `game.getEquipmentForms().apply(game);` → [Game.getEquipmentForms](map/Game.md) (L1280)  
   _the ways the buildings run, at the counts the month leaves (R1c): first, the steam form below weighs each building by its electricity_
11. **L106** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L9758), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5654)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
12. **L110** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5706)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
13. **L111** `double ownMonth = overtime == 0 ? siteOutput : siteOutput + overtime;`
14. **L113** `game.settleForeignCrews(foreign, buildingManager.getPointsBuilt() - Math.max(0, ownMonth));` → [Game.settleForeignCrews](map/Game.md) (L8605), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5670)  
   _...and the contractor is paid for what the sites took past the city's own month (FC1)._
15. **L114** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L9866), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L7042), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5670)
16. **L120** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L9806), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L7051)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
17. **L125** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L10505), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5699)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
18. **L134** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L216)  
   _Roads, before anything reads them._
19. **L141** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L163)  
   _Recount the posts and refresh the wage arrays._
20. **L157** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L15678)  
   _THE POPULATION STEP._
21. **L159** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L177)
22. **L160** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L246)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 163-175 (13 lines)

1. **L168** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L8477)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L169** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L15541)
3. **L170** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L15554)
4. **L173** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L17260)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L174** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L370)

## Game.advanceDemographics() - Game.java lines 15678-16233 (556 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L15683** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L701), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L274), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L15697** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L15698** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L16292)
4. **L15699** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L16292)
5. **L15700** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L16292)
6. **L15701** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7898)
7. **L15703** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
8. **L15717** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L15718** `java.util.Arrays.fill(affordable, 1);`
10. **L15719** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L16311)
11. **L15722** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L15723** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L15724** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L15727** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L646)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L15739** `MedicalRows medicalRows = medicalRows();` → [Game.medicalRows](map/Game.md) (L1589)  
   _...AND THE MEDICINE THE CITY KNOWS (R2f, research-design Q17)._
16. **L15740** `sickness.setMedicine(medicalRows, month, generalCoverage);` → [Sickness.setMedicine](map/Sickness.md) (L210)
17. **L15743** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
18. **L15744** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7898)
19. **L15758** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
20. **L15771** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L406)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
21. **L15782** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L345)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
22. **L15783** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L152)
23. **L15784** `double[] orphansByBand = new double[AgeBand.values().length];`
24. **L15785** `double[] inBand = new double[AgeBand.values().length];`
25. **L15786** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L262), [PopulationCohorts.get](map/PopulationCohorts.md) (L133)
26. **L15790** `double[] careFactors = mortalityFactors;`
27. **L15791** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L473), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
28. **L15801** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L385), [Sickness.seed](map/Sickness.md) (L311), [Health.getSickRate](map/Health.md) (L334), [Health.getSickRateWithoutInfluenza](map/Health.md) (L337)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
29. **L15805** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L245)
30. **L15812** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
31. **L15813** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L133)
32. **L15814** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
33. **L15834** `double[][] causeRates = causesOfDeath.rates(month, inBand, mortalityFactors,` → [CausesOfDeath.rates](map/CausesOfDeath.md) (L558), [Game.getWaterRatio](map/Game.md) (L3200), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L5307), [Health.getOutbreakSeverity](map/Health.md) (L364), [PopulationCohorts.getLastBirths](map/PopulationCohorts.md) (L134)  
   _...AND WHAT THEY DIE OF (R0b)._
34. **L15837** `double[] causeTotals = CausesOfDeath.bandTotals(causeRates);` → [CausesOfDeath.bandTotals](map/CausesOfDeath.md) (L492)
35. **L15839** `for (AgeBand b : AgeBand.values()) {` → [CausesOfDeath.isHeldOff](map/CausesOfDeath.md) (L533), [CausesOfDeath.keptFactor](map/CausesOfDeath.md) (L469)  
   _...held off for a harness fixture (R0b call 7): no cause, and the swing whole._
36. **L15844** `cohorts.advanceMonth(mortalityFactors, illness, violence, causeTotals,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L242), [Healthcare.birthFactor](map/Healthcare.md) (L425)
37. **L15846** `causesOfDeath.book(month, causeRates, cohorts.getCauseDeaths());` → [CausesOfDeath.book](map/CausesOfDeath.md) (L635), [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L154)
38. **L15847** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L417), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L140)
39. **L15851** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
40. **L15852** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L143)
41. **L15853** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L140)
42. **L15854** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L145)
43. **L15855** `double[] causedDead = cohorts.getCauseDeaths();` → [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L154)
44. **L15856** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b] + causedDead[b];`
45. **L15857** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L509), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
46. **L15860** `lastOrphanDeaths = outsideDead[0];`
47. **L15861** `lastUnhousedDeaths = outsideDead[1];`
48. **L15868** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L512), [Unemployment.takeEvicted](map/Unemployment.md) (L215)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
49. **L15871** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
50. **L15885** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L133)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
51. **L15892** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L1048), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L5175), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L458)  
   _The families the bank discharged last month are leaving._
52. **L15901** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L325), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L479)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
53. **L15905** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L366), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
54. **L15910** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L928), [BuildingManager.homesBySize](map/BuildingManager.md) (L7404)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
55. **L15911** `getSectors().realEstate().housingMarket().prepareMigration(this);` → [Game.getSectors](map/Game.md) (L3209)
56. **L15915** `migration.setYearMix(foundedUnfitted() ? ArrivalMix.at(month) : null);` → [Migration.setYearMix](map/Migration.md) (L1273), [Game.foundedUnfitted](map/Game.md) (L1290), [ArrivalMix.at](map/ArrivalMix.md) (L145)  
   _...and, in a city founded the 1900 way, what the world's movers hold this year (R0d; ArrivalMix): the skills the month's arrivals are drawn from._
57. **L15917** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L478), [Migration.monthlyNet](map/Migration.md) (L941), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L15545), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L7393), [PopulationCohorts.share](map/PopulationCohorts.md) (L169)
58. **L15932** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
59. **L15944** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L17294)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
60. **L15946** `double[] jobsByTier = new double[PayTier.values().length];`
61. **L15947** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)
62. **L15948** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1193)
63. **L15950** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
64. **L15962** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
65. **L15963** `double[] wageByTier = new double[PayTier.values().length];`
66. **L15964** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L274)
67. **L15965** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
68. **L15968** `for (int t = 0; t < wageByTier.length; t++) {`
69. **L15984** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7974)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
70. **L15985** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L1183)
71. **L15986** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
72. **L15987** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L193), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7974), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161), [Game.unskilledWage](map/Game.md) (L16628)
73. **L15995** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L241), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
74. **L15997** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L857), [Crime.prisoners](map/Crime.md) (L370)
75. **L15999** `double ownerAdults = ownerAdultsWanted();` → [Game.ownerAdultsWanted](map/Game.md) (L16245)  
   _The owner families take no job (BO1): two adults a company with a family, at most a share of the adults (BO1-S)._
76. **L16000** `populationManager.setOwners(ownerAdults);` → [PopulationManager.setOwners](map/PopulationManager.md) (L862)
77. **L16001** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L140)
78. **L16003** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1166)
79. **L16004** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L868)
80. **L16005** `unemployment.setRetiringShare(cohorts.promotedShare(AgeBand.ADULT));   // HS2` → [Unemployment.setRetiringShare](map/Unemployment.md) (L287), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161)
81. **L16006** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L303), [Game.unskilledWage](map/Game.md) (L16628), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _HS2_
82. **L16010** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L282), [Migration.getLastArrivals](map/Migration.md) (L590), [PopulationCohorts.share](map/PopulationCohorts.md) (L169)  
   _The month's adult arrivals look for work next month._
83. **L16021** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying, ownerAdults);` → [FamilyModel.rebuild](map/FamilyModel.md) (L477), [Crime.prisoners](map/Crime.md) (L370)  
   _...and the owner families: two adults a company with a family, outside the families too (BO1), the same adults as above._
84. **L16022** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L277), [Unemployment.getHoused](map/Unemployment.md) (L155)
85. **L16026** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L5112), [Education.getFinished](map/Education.md) (L903)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
86. **L16029** `householdBalance.setFlows(cohorts);` → [HouseholdBalance.setFlows](map/HouseholdBalance.md) (L2354)  
   _...and the month's flows, the pyramid's own (HS1): who retired, died and left carries their money at this month's census, before the pool._
87. **L16033** `families.beginTypedHousing(buildingManager.homesBySize());` → [FamilyModel.beginTypedHousing](map/FamilyModel.md) (L1698), [BuildingManager.homesBySize](map/BuildingManager.md) (L7404)  
   _One quote and clearing: suitable affordable moves first, then explicit emergency crowding and actually placed sharing groups (RES3)._
88. **L16034** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1582)
89. **L16035** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1587)
90. **L16036** `clearTypedHousing();` → [Game.clearTypedHousing](map/Game.md) (L16550)
91. **L16074** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L161), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
92. **L16075** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L483), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8050), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8139), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L135), [Game.burialShare](map/Game.md) (L16373), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7861), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7898)
93. **L16085** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L2038), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Healthcare.getFees](map/Healthcare.md) (L732)
94. **L16100** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L4270), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
95. **L16101** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L403), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7937), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8050), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8139), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161), [Migration.getLastDepartures](map/Migration.md) (L597)
96. **L16120** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L878), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L8101), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L8114)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
97. **L16127** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1088), [Education.getGraduates](map/Education.md) (L781)
98. **L16128** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L1014), [Education.getLicences](map/Education.md) (L797)
99. **L16131** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L867), [Education.getStudying](map/Education.md) (L590)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
100. **L16134** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L1056)  
   _A licence holder is a graduate first, and the graduate count has just moved._
101. **L16136** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L2047), [Education.getGrossCost](map/Education.md) (L844), [Education.getFees](map/Education.md) (L852)
102. **L16145** `settleResearchBill(fill);` → [Game.settleResearchBill](map/Game.md) (L1539)  
   _...AND THE RESEARCH (R3): beside the schools because their staff are its faculty and its room, at the same wages and fill they were paid at._
103. **L16146** `workTheResearch(fill);` → [Game.workTheResearch](map/Game.md) (L1561)
104. **L16152** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8050), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8139)  
   _6c._
105. **L16156** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L2060), [Crime.getGrossCost](map/Crime.md) (L452)
106. **L16189** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L3219), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L3288), [Motoring.journeyFuel](map/Motoring.md) (L175)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
107. **L16191** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L272), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
108. **L16192** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L200), [Game.getInfrastructureManager](map/Game.md) (L3219)
109. **L16197** `if (homeFuel.isOn() \|\| homeFuel.hasSale()) homeFuel.strike(this, homeFuelHomes());` → [HomeFuel.isOn](map/HomeFuel.md) (L78), [HomeFuel.hasSale](map/HomeFuel.md) (L81), [HomeFuel.strike](map/HomeFuel.md) (L98), [Game.homeFuelHomes](map/Game.md) (L4772)  
   _...and the homes' lamp oil and coal (R0c, the home form; HomeFuel): the homes that light with a lamp and heat with a stove ask Retail for their month's JET and THERMAL_COAL, drawn and sold at the p..._
110. **L16201** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2113), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8050), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8139), [Game.portsBill](map/Game.md) (L8654), [Game.getInfrastructureManager](map/Game.md) (L3219), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
111. **L16209** `economyManager.setTransitUpkeep(buildingManager.getUpkeepByCategory(BuildingType.INFRASTRUCTURE) + portsBill());` → [EconomyManager.setTransitUpkeep](map/EconomyManager.md) (L2123), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8139), [Game.portsBill](map/Game.md) (L8654)  
   _...and the upkeep part of that bill, which the treasury pays Business Services (HL1; buyUpkeep()), the rest being the crews' wages._
112. **L16215** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L757), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L5307), [Game.unhousedShareOfCity](map/Game.md) (L16274), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
113. **L16231** `sickness.advanceMonth(health.getSickRate(), health.getSickRateWithoutInfluenza(),` → [Sickness.advanceMonth](map/Sickness.md) (L261), [Health.getSickRate](map/Health.md) (L334), [Health.getSickRateWithoutInfluenza](map/Health.md) (L337)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 177-244 (68 lines)

1. **L180** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L181** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L182** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L7420)
4. **L183** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L536), [Game.getFamilies](map/Game.md) (L15650)
5. **L189** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L533), [Game.getFamilies](map/Game.md) (L15650)  
   _The two inputs to the rent price, which is a market now._
6. **L192** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L537), [Game.getFamilies](map/Game.md) (L15650)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L196** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L534), [Game.marginalHousingCost](map/Game.md) (L20768)
8. **L198** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2148), [Game.getCohorts](map/Game.md) (L15649)
9. **L204** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L115), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L205** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L127), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L8227)
11. **L208** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L200)
12. **L215** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L109), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L248)  
   _The split behind that total._
13. **L218** `UtilitiesHandler utility = servicesManager.getUtilitiesHandler();` → [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L321)  
   _The struck book (PW1): this month's ratio, the prices its bills strike at when the statements are struck at the top of next month, and the fuel the utility buys then._
14. **L219** `utility.strikePower();`
15. **L220** `economyManager.setPowerPrices(utility.getPricePerWatt(), utility.getIndustryPricePerWatt(), utility.getWorksPricePerW...` → [EconomyManager.setPowerPrices](map/EconomyManager.md) (L176)
16. **L221** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L188), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L329)
17. **L222** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L189), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L333)
18. **L237** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L191), [Game.getInfrastructureManager](map/Game.md) (L3219)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
19. **L240** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L219), [Game.getHealth](map/Game.md) (L15653)  
   _The fourth ratio._
20. **L241** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1724)

## SimulationEngine.updateServices() - SimulationEngine.java lines 246-253 (8 lines)

1. **L247** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L295), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232)
2. **L248** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L313), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)
3. **L251** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L385), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L252** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L89)

## Game.run() - Game.java lines 777-788 (12 lines)

1. **L778** `boolean starting = !initialized;`
2. **L779** `if (starting) {` → [Game.layTheLadder](map/Game.md) (L1360), [Game.basketHearsTheYear](map/Game.md) (L16966)
3. **L783** `initialize();` → [Game.initialize](map/Game.md) (L790)
4. **L784** `foundingBank();` → [Game.foundingBank](map/Game.md) (L935)
5. **L785** `foundingYard();` → [Game.foundingYard](map/Game.md) (L1021)
6. **L786** `if (starting && founding.isUnfitted()) unfitTheFounding();` → [Founding.isUnfitted](map/Founding.md) (L480), [Game.unfitTheFounding](map/Game.md) (L1299)
7. **L787** `if (starting) foundOnGold();` → [Game.foundOnGold](map/Game.md) (L1324)

