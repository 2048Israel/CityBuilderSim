# The order the month runs in

Generated 2026-11-25 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 14105-15082 (978 lines)

1. **L14113** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L14114** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L14115** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1274)
4. **L14116** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L14125** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L14126** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L17263)
7. **L14138** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L5961)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L14139** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L13882)
9. **L14141** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L15880)
10. **L14150** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L14151** `monthsSinceAutosave++;`
12. **L14158** `policyHearsTheMonth();` → [Game.policyHearsTheMonth](map/Game.md) (L1239)  
   _THE CITY HEARS THE MONTH FIRST (R0): the dollar its gold years, the households the pension, the buildings their recipes and forms, before anything in this month is taxed or paid (every Policy lever..._
13. **L14160** `basketHearsTheYear();` → [Game.basketHearsTheYear](map/Game.md) (L17324)  
   _...and the homes' lamp and stove shares of the year (R0e), in a city founded the 1900 way, before this month's fuel is struck (6d)._
14. **L14166** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L10482)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
15. **L14172** `strikeResearchPosts();` → [Game.strikeResearchPosts](map/Game.md) (L1506)  
   _THE RESEARCH POSTS ARE STRUCK NEXT (R3), AFTER THE DEMOLITIONS (R3b): what the month's grants buy within the schools' room, at the wages the labour market has now._
16. **L14175** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2372)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
17. **L14176** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L742)
18. **L14179** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L420)  
   _The register's month: nothing offered, nothing paid, until it is._
19. **L14180** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L535)
20. **L14181** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1263)
21. **L14182** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1702)
22. **L14200** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
23. **L14228** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
24. **L14229** `cityDiscountForBank = cityDiscountThisMonth;`
25. **L14230** `cityDebtRaisedThisMonth = 0;`
26. **L14231** `cityDiscountThisMonth = 0;`
27. **L14232** `cityPrincipalRepaidThisMonth = 0;`
28. **L14233** `bankPrincipalRepaidThisMonth = 0;`
29. **L14236** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
30. **L14237** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
31. **L14238** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
32. **L14239** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L477)
33. **L14240** `foreignDebtRaisedThisMonth = 0;`
34. **L14241** `foreignPrincipalRepaidThisMonth = 0;`
35. **L14242** `foreignInterestPaidThisMonth = 0;`
36. **L14247** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
37. **L14248** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
38. **L14249** `sectorInvested.clear();`
39. **L14250** `salvageThisMonth.clear();`
40. **L14251** `salvageBySector.clear();`
41. **L14252** `salvageUsedThisMonth = 0;`
42. **L14253** `householdBalance.beginTaxMonth();` → [HouseholdBalance.beginTaxMonth](map/HouseholdBalance.md) (L453)
43. **L14254** `estateCustody.beginMonth();` → [EstateCustody.beginMonth](map/EstateCustody.md) (L39)
44. **L14255** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L616)
45. **L14256** `double householdsBefore = MoneyAudit.householdsHeld(this);` → [MoneyAudit.householdsHeld](map/MoneyAudit.md) (L380)
46. **L14257** `double pooledBefore = 0;`
47. **L14258** `for (double p : poolsBefore) pooledBefore += p;`
48. **L14259** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2187)
49. **L14261** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1371)
50. **L14262** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L2859)
51. **L14263** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L268)
52. **L14274** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
53. **L14275** `buybackAbroad = buybackAbroadUnsettled;`
54. **L14276** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
55. **L14277** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L580)
56. **L14280** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L6374)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
57. **L14282** `centralBank.settleGoldRedeemed();` → [CentralBank.settleGoldRedeemed](map/CentralBank.md) (L753)  
   _...and the D$ the gold the mint paid out at the last reprice took back are retired, M0 down with the gold (FB14)._
58. **L14306** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1722)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
59. **L14307** `if (fxRegime == ForeignAccounts.Regime.PEG) {   // Canada's gold (R0f) is a peg to the US$ at par, so it is tied here...` → [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.tiedRate](map/DebtManager.md) (L225), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1824), [DebtManager.isAutopilot](map/DebtManager.md) (L116), [DebtManager.getAim](map/DebtManager.md) (L185), [DebtManager.goldRate](map/DebtManager.md) (L241), [Game.getGoldCover](map/Game.md) (L6288), [DebtManager.currencyRate](map/DebtManager.md) (L214), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L219), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L473)
60. **L14323** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L15647)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
61. **L14330** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L20406)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
62. **L14338** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L19857)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
63. **L14343** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L19483)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
64. **L14362** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L20538), [Game.studentGrantBill](map/Game.md) (L17012)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
65. **L14376** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L20563)  
   _...AND EI, THE SAME WAY (0.7.3)._
66. **L14394** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1771), [Bank.chargeTax](map/Bank.md) (L4110), [Game.bankProfitTaxRate](map/Game.md) (L4940)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
67. **L14398** `getMarkets().startSellerPricedMonth();` → [Game.getMarkets](map/Game.md) (L3250)  
   _FB3 (FW6-C): the seller-priced goods' markets begin their month here, as the traded goods' do in their clearing: their trades and tally grew for the city's whole life._
68. **L14399** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L15084)
69. **L14400** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
70. **L14401** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L15722)
71. **L14402** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2573)
72. **L14403** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L15629)
73. **L14407** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
74. **L14408** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L19250)
75. **L14409** `estateCustody.consolidatePaper();` → [EstateCustody.consolidatePaper](map/EstateCustody.md) (L107)
76. **L14410** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
77. **L14411** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1586)
78. **L14415** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L15667)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
79. **L14426** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
80. **L14428** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
81. **L14429** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L3118)
82. **L14433** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3278)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
83. **L14434** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L3112)
84. **L14450** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2857), [DebtManager.getAccretedForBank](map/DebtManager.md) (L3043)  
   _THE DISCOUNT ACCRETES (0.7.1)._
85. **L14451** `legacyDiscountDue = 0;`
86. **L14455** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
87. **L14456** `cityDiscountForBank = 0;`
88. **L14458** `double sectorInterestPaid = 0;`
89. **L14459** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L3245)
90. **L14465** `double issuerCoupons = bondMarket.getCouponsStruck();` → [BondMarket.getCouponsStruck](map/BondMarket.md) (L1140)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
91. **L14466** `bank.takeInterest(interestDue, sectorInterestPaid - issuerCoupons);` → [Bank.takeInterest](map/Bank.md) (L2838)
92. **L14467** `bondMarket.recordCouponIssuerCharge(issuerCoupons);` → [BondMarket.recordCouponIssuerCharge](map/BondMarket.md) (L291)
93. **L14468** `double bankCoupon = bondMarket.getCouponsDueToBank();` → [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1148)
94. **L14469** `bank.takeBondCoupons(bankCoupon);` → [Bank.takeBondCoupons](map/Bank.md) (L569)
95. **L14470** `bondMarket.recordCouponBankPayment(bankCoupon);` → [BondMarket.recordCouponBankPayment](map/BondMarket.md) (L299)
96. **L14472** `refreshBank();` → [Game.refreshBank](map/Game.md) (L5217)
97. **L14473** `lender.projectLoansSettled();`
98. **L14476** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L5385)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
99. **L14499** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3169), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L145), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1240), [BuildingManager.countByName](map/BuildingManager.md) (L7787), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1247)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
100. **L14504** `getSectors().businessServices().sellBankUpkeep(bank.getOperatingCost());` → [Game.getSectors](map/Game.md) (L3245), [Bank.getOperatingCost](map/Bank.md) (L3217)  
   _...and its running cost is bought of Business Services, as the city's upkeep is (HL1; buyUpkeep()): the bank paid it out of its own cash above._
101. **L14505** `bankMaintenanceDue = 0;`
102. **L14513** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L4921), [Bank.openBranches](map/Bank.md) (L2776), [BuildingManager.countByName](map/BuildingManager.md) (L7787)  
   _Capital for whatever branches opened this month._
103. **L14558** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1236), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
104. **L14560** `hotMoney.setRiskOff(worldRiskOffLevel());` → [CapitalFlows.setRiskOff](map/CapitalFlows.md) (L707), [Game.worldRiskOffLevel](map/Game.md) (L2405)  
   _The risk-off the world is in (XR4): handed once here, it holds for the hot money's month below as well._
105. **L14561** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L542), [DebtManager.worldRate](map/DebtManager.md) (L866), [DebtManager.countryPremium](map/DebtManager.md) (L1192), [Bank.headroom](map/Bank.md) (L5628)
106. **L14567** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L605)
107. **L14568** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L613)
108. **L14569** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L630), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L608)
109. **L14573** `refreshBank();` → [Game.refreshBank](map/Game.md) (L5217)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
110. **L14583** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3476), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
111. **L14596** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L328), [Bank.getPlacementIncome](map/Bank.md) (L3750)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
112. **L14597** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L336), [Bank.getFundingCost](map/Bank.md) (L3748)
113. **L14598** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L321), [Bank.getBranches](map/Bank.md) (L5279), [Bank.wholesaleFunding](map/Bank.md) (L3456)
114. **L14603** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1945)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
115. **L14604** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5512)
116. **L14608** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3910)  
   _The month is final, so the figure next month's tax is charged on is final too._
117. **L14612** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L406)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
118. **L14629** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L15615)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
119. **L14630** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L3233)
120. **L14640** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L5442), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3392)  
   _...AND THE SAVERS ARE PAID._
121. **L14641** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3393)
122. **L14648** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1562)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
123. **L14649** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L661), [EconomyManager.setSectorCash](map/EconomyManager.md) (L666), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1564)
124. **L14680** `payDividends();` → [Game.payDividends](map/Game.md) (L5049)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
125. **L14681** `tradeShares();` → [Game.tradeShares](map/Game.md) (L5121)
126. **L14691** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L5695)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
127. **L14701** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1296), [Exchange.cityMarketValue](map/Exchange.md) (L1964)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
128. **L14702** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L5328)
129. **L14707** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L452), [DebtManager.firmBookRatio](map/DebtManager.md) (L2984), [DebtManager.firmBookYield](map/DebtManager.md) (L2990)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
130. **L14708** `outward.takeMonth(bank.depositRate(), debtManager.worldRate(),` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L248), [Bank.depositRate](map/Bank.md) (L3385), [DebtManager.worldRate](map/DebtManager.md) (L866), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
131. **L14712** `householdBalance.investAbroad(bank.depositRate(), debtManager.worldRate(), foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L4477), [Bank.depositRate](map/Bank.md) (L3385), [DebtManager.worldRate](map/DebtManager.md) (L866), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)  
   _...and the households, by the same rule, with what the owners were just paid._
132. **L14713** `estateCustody.payForeignCoupon(debtManager.worldRate(), foreign.getRate());` → [EstateCustody.payForeignCoupon](map/EstateCustody.md) (L111), [DebtManager.worldRate](map/DebtManager.md) (L866), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
133. **L14716** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L4815), [DebtManager.householdBookYield](map/DebtManager.md) (L3013), [Bank.depositRate](map/Bank.md) (L3385)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
134. **L14734** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1945)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
135. **L14735** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5512)
136. **L14736** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L5618)
137. **L14771** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
138. **L14772** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L748)
139. **L14775** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L651), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1836)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
140. **L14776** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L380), [Bank.depositRate](map/Bank.md) (L3385), [DebtManager.getRate](map/DebtManager.md) (L1413), [DebtManager.worldRate](map/DebtManager.md) (L866), [DebtManager.countryPremium](map/DebtManager.md) (L1192), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2567), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L2799), [Game.yearlyDepreciation](map/Game.md) (L17390), [Bank.isInsolvent](map/Bank.md) (L1853), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1351)
141. **L14798** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
142. **L14799** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1729)
143. **L14800** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1743)
144. **L14801** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1717), [CapitalFlows.getStock](map/CapitalFlows.md) (L634)
145. **L14803** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L19223)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
146. **L14805** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L799)
147. **L14806** `lastHouseholdsAudit = MoneyAudit.households(this, householdsBefore, lastMoneyAudit);` → [MoneyAudit.households](map/MoneyAudit.md) (L454)
148. **L14809** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
149. **L14819** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L2650), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2567)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
150. **L14851** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L246), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L315), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L362)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
151. **L14870** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L375), [Game.realRateDifferential](map/Game.md) (L13203)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
152. **L14876** `handTheWorldToTheD();` → [Game.handTheWorldToTheD](map/Game.md) (L2526)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
153. **L14877** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
154. **L14878** `boolean onGoldBeforeReprice = foreign.getRegime() == ForeignAccounts.Regime.GOLD \|\| foreign.isCanadaGold();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1722), [ForeignAccounts.isCanadaGold](map/ForeignAccounts.md) (L1532)
155. **L14879** `foreign.setMoneyBase(centralBank.m0(), centralBank.goldRetirable());   // M0 is the gold cover's base, handed right b...` → [ForeignAccounts.setMoneyBase](map/ForeignAccounts.md) (L1619), [CentralBank.m0](map/CentralBank.md) (L863), [CentralBank.goldRetirable](map/CentralBank.md) (L873)
156. **L14880** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L913)  
   _M0 is the gold cover's base, handed right before the reprice (GD3), and what a sale can retire of it (FB14)_
157. **L14888** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1838), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1843), [Game.getCurrency](map/Game.md) (L2989), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259), [DebtManager.dropGoldAim](map/DebtManager.md) (L247)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
158. **L14897** `noteTheWorldMoneys(rateBeforeReprice);` → [Game.noteTheWorldMoneys](map/Game.md) (L2533)  
   _...AND THE WORLD'S NEWS (WN4): money that ran to safety abroad, a world peg that broke._
159. **L14908** `if (foreign.getRegime() == ForeignAccounts.Regime.GOLD) centralBank.goldPaidOut(foreign.getDefenceLocal());` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1722), [CentralBank.goldPaidOut](map/CentralBank.md) (L744), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L869)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
160. **L14909** `else centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L687), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L869)
161. **L14927** `economyManager.getNationalAccounts().setDebtExchangeGain(debtManager.revalueForeign(foreign.getRate()));` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [DebtManager.revalueForeign](map/DebtManager.md) (L950), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)  
   _CD7: the same press books the exchange gain (loss) on the dollar paper - each piece's carrying value in dollars times the move from the rate it was last valued at - on the month the strike already ..._
162. **L14928** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L2753), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L971), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
163. **L14935** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L3149)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
164. **L14936** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L1100), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L2626), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L2607)
165. **L14937** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1344)
166. **L14938** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L13177)
167. **L14951** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L17167), [Game.indexSpends](map/Game.md) (L17195), [Game.indexFees](map/Game.md) (L17210), [Game.indexFeeSpends](map/Game.md) (L17226)  
   _...AND WHAT THE MONTH COST A FAMILY._
168. **L14963** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L293)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
169. **L14964** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L548), [DebtManager.holdingRate](map/DebtManager.md) (L502), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
170. **L14967** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L17289)
171. **L14970** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)  
   _A year of the rate, so next year can tell a drift from a run._
172. **L14971** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
173. **L14973** `economyManager.getNationalAccounts().setInvestmentTaxes(householdBalance.getCapitalGainsTax(), householdBalance.getDi...` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [HouseholdBalance.getCapitalGainsTax](map/HouseholdBalance.md) (L537), [HouseholdBalance.getDividendTax](map/HouseholdBalance.md) (L538)
174. **L14974** `economyManager.getNationalAccounts().setCapitalTaxClaims(householdBalance.getCapitalTaxAssessed(),` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [HouseholdBalance.getCapitalTaxAssessed](map/HouseholdBalance.md) (L541), [HouseholdBalance.getCapitalTaxOutstanding](map/HouseholdBalance.md) (L546), [HouseholdBalance.getCapitalTaxWrittenOff](map/HouseholdBalance.md) (L542), [HouseholdBalance.getCapitalTaxConsolidated](map/HouseholdBalance.md) (L543)
175. **L14976** `economyManager.getNationalAccounts().setEstateCustody(estateCustody);` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56)
176. **L14977** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L20069)
177. **L14978** `householdBalance.closeTaxMonth(); estateCustody.closeMonth();` → [HouseholdBalance.closeTaxMonth](map/HouseholdBalance.md) (L458)
178. **L14978** `householdBalance.closeTaxMonth(); estateCustody.closeMonth();` → [EstateCustody.closeMonth](map/EstateCustody.md) (L43)
179. **L14980** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L972)
180. **L15014** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
181. **L15015** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L616)
182. **L15034** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L20332)
183. **L15036** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L3910)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
184. **L15038** `tallyThePartners();` → [Game.tallyThePartners](map/Game.md) (L2092)  
   _The month's trade split to the world's nations (WN3; THE WORLD MARKET), after its last draw: before the history and the autosave._
185. **L15039** `recordMonth();` → [Game.recordMonth](map/Game.md) (L17716)
186. **L15041** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
187. **L15043** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L2967)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
188. **L15049** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L3250)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._
189. **L15070** `reportSaves();` → [Game.reportSaves](map/Game.md) (L17891)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
190. **L15071** `SaveWriter.Job autosaved = null;`
191. **L15072** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosaveJob](map/Game.md) (L17813)
192. **L15081** `recordForReplay(autosaved);` → [Game.recordForReplay](map/Game.md) (L18569)  
   _A recorded city keeps this whole month, beside the autosave (TR1; THE RECORDING), from the autosave's fill when it made one (TR2)._

## Game.startOfMonthUpdate() - Game.java lines 15084-15407 (324 lines)

1. **L15136** `ensureWorldMarket();` → [Game.ensureWorldMarket](map/Game.md) (L2004)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L15137** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L270)
3. **L15138** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L350), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L315)
4. **L15140** `strikeTheWorldMarket();` → [Game.strikeTheWorldMarket](map/Game.md) (L2057)  
   _...AND THE WORLD'S BAND BEHIND IT (WN3): the January solve when the year has turned, then every good's floor and ceiling from last month's trade; before any market clears._
5. **L15142** `double gold = goldUsdPerOunce(WorldMoneys.monthIndex(month));` → [Game.goldUsdPerOunce](map/Game.md) (L2379), [WorldMoneys.monthIndex](map/WorldMoneys.md) (L270)  
   _Bullion's nominal US$ path and spot exchange already include the dollar's price path (GD4)._
6. **L15143** `if (Double.isFinite(gold) && gold > 0) {` → [EconomyManager.getMarkets](map/EconomyManager.md) (L51), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)
7. **L15162** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L476), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
8. **L15169** `labourMarket.followAverageWage(populationManager.getAverageWage());` → [LabourMarket.followAverageWage](map/LabourMarket.md) (L983), [PopulationManager.getAverageWage](map/PopulationManager.md) (L169)  
   _The floor's tie to the average wage (WG1) is struck once a month from last month's wages, before repriceLabour() walks them._
9. **L15189** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L753), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
10. **L15191** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L378), [LandManager.getPricePerSqFt](map/LandManager.md) (L634)
11. **L15192** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L11318)
12. **L15202** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L15615)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
13. **L15207** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5279), [Bank.isInsolvent](map/Bank.md) (L1853)  
   _A failed bank lends nothing._
14. **L15220** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L5328)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
15. **L15221** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L5022)
16. **L15222** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L5056)
17. **L15227** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4705)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
18. **L15228** `economyManager.getBusinessDebtManager().setProjectBudget(bank);` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
19. **L15229** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L803)
20. **L15236** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L5368)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
21. **L15237** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L684), [Bank.prime](map/Bank.md) (L1137), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1158)
22. **L15241** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L1093)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
23. **L15246** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L854)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
24. **L15250** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L8937)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
25. **L15254** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L8886)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
26. **L15257** `outward.payCoupons(debtManager.worldRate(), foreign.getRate(), economyManager);` → [OutwardInvestment.payCoupons](map/OutwardInvestment.md) (L218), [DebtManager.worldRate](map/DebtManager.md) (L866), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1259)  
   _...and the coupon on the dollars its businesses hold abroad, struck as a line of their profit and kept abroad: the profit tax and the dividend see it, the till pays them (HD1, 2026-10-29; HD2, 2026..._
27. **L15271** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L3245)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
28. **L15272** `double constructionWorkDone = construction.getRecognisedThisMonth();`
29. **L15274** `if (construction.getFittingThisMonth() != 0) constructionWorkDone += construction.getFittingThisMonth();`  
   _...and the equipment the builders were paid to fit to standing buildings since the last strike banked its ledger (R1b): capital too, on the builders' side, once._
30. **L15277** `buyPowerFuel();` → [Game.buyPowerFuel](map/Game.md) (L6472)  
   _The coal the utility burned last month, bought from the market before the strike banks its sellers' ledgers (PW1; buyPowerFuel())._
31. **L15286** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1268)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
32. **L15288** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L3250), [Game.getSectors](map/Game.md) (L3245)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
33. **L15290** `settleReserve();` → [Game.settleReserve](map/Game.md) (L6502)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
34. **L15292** `settlePublicLicences();` → [Game.settlePublicLicences](map/Game.md) (L6519)  
   _...and the licences the city's own patented works accrued last month (R2c-P), paid abroad in the same strike._
35. **L15296** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L3245)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
36. **L15302** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
37. **L15304** `economyManager.setUtilityImports(servicesManager.getUtilitiesHandler().getFuelImported() + servicesManager.getUtiliti...` → [EconomyManager.setUtilityImports](map/EconomyManager.md) (L106), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L324)  
   _...and the coal the utility bought from the world and the upkeep it paid abroad (PW1; buyPowerFuel())._
38. **L15305** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2211), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L324), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L838), [EconomyManager.getResearchBill](map/EconomyManager.md) (L2084), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2125), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L636), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L638), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L865)
39. **L15363** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
40. **L15364** `monthlyMaterialImportBill = 0;`
41. **L15369** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L4288)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
42. **L15377** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L251)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
43. **L15385** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
44. **L15394** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
45. **L15398** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1497)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
46. **L15404** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L3951)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
47. **L15406** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L15408)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-166 (135 lines)

1. **L33** `game.moneyHearsTheMonth();` → [Game.moneyHearsTheMonth](map/Game.md) (L1228)
2. **L68** `int siteOutput = game.workTheFittings();` → [Game.workTheFittings](map/Game.md) (L1750)  
   _...less the fittings the crews work first (R1b; Game.workTheFittings()): after the repairs, before the sites, struck here once and used for the advance and for the work the sector is paid for below._
3. **L73** `game.investorsHandAbroad();` → [Game.investorsHandAbroad](map/Game.md) (L8721)  
   _The foreign contractor's month beside the city's own (FC1; FC2): the queue at home past the threshold handed to him, and his month on his book - see Game, THE FOREIGN CREWS._
4. **L74** `Game.ForeignOffer foreign = game.foreignCrewsOffer();` → [Game.foreignCrewsOffer](map/Game.md) (L8657)
5. **L78** `game.getBusinessInvestment().beforeSitesAdvance();` → [Game.getBusinessInvestment](map/Game.md) (L3881)  
   _His points are in the month the sites take, and not on a rushed site's overtime (FC1b): his crews are not the city's._
6. **L79** `java.util.List<BuildingManager.Completion> opened = buildingManager.advanceConstruction(siteOutput + foreign.points()...` → [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5812)
7. **L80** `game.getBusinessInvestment().afterSitesAdvance();` → [Game.getBusinessInvestment](map/Game.md) (L3881)
8. **L81** `game.recordCompletions(opened);` → [Game.recordCompletions](map/Game.md) (L16801)
9. **L84** `game.fitWhatFinished(opened);` → [Game.fitWhatFinished](map/Game.md) (L1424)  
   _...and what the buildings that opened come with (R1b): in a city founded the 1900 way, only the equipment that pays for a new building; nothing in a city founded the default way, where every buildi..._
10. **L100** `game.getRecipes().apply(game);   // the ways the buildings run, at the counts the month leaves (R1c): first, the stea...` → [Game.getRecipes](map/Game.md) (L1298)  
   _...AND THE 1900 FORMS AT REST (R1b; R1's stranger's review, 2026-11-05): the shares the counts set, said again now that the month has changed the counts' weights._
11. **L101** `game.getEquipmentForms().apply(game);` → [Game.getEquipmentForms](map/Game.md) (L1304)  
   _the ways the buildings run, at the counts the month leaves (R1c): first, the steam form below weighs each building by its electricity_
12. **L109** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L10060), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5922)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
13. **L113** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5987)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
14. **L114** `double ownMonth = overtime == 0 ? siteOutput : siteOutput + overtime;`
15. **L116** `game.settleForeignCrews(foreign, buildingManager.getPointsBuilt() - Math.max(0, ownMonth));` → [Game.settleForeignCrews](map/Game.md) (L8680), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5938)  
   _...and the contractor is paid for what the sites took past the city's own month (FC1)._
16. **L118** `game.settleCrewsOnSites();` → [Game.settleCrewsOnSites](map/Game.md) (L8863)  
   _...and the crews on the sites handed them (FC2b), billed as FC1's are; their points are not in getPointsBuilt()._
17. **L119** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L10168), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L7731), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5938)
18. **L125** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L10108), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L7740)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
19. **L130** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L10814), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5980)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
20. **L139** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L219)  
   _Roads, before anything reads them._
21. **L146** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L168)  
   _Recount the posts and refresh the wage arrays._
22. **L162** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L16041)  
   _THE POPULATION STEP._
23. **L164** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L182)
24. **L165** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L255)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 168-180 (13 lines)

1. **L173** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L8528)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L174** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L15904)
3. **L175** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L15917)
4. **L178** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L17621)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L179** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L370)

## Game.advanceDemographics() - Game.java lines 16041-16635 (595 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L16046** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L701), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L274), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L16060** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L16061** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L16694)
4. **L16062** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L16694)
5. **L16063** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L16694)
6. **L16064** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L8587)
7. **L16066** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
8. **L16080** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L16081** `java.util.Arrays.fill(affordable, 1);`
10. **L16082** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L16713)
11. **L16085** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L16086** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L16087** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L16090** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L646)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L16102** `MedicalRows medicalRows = medicalRows();` → [Game.medicalRows](map/Game.md) (L1625)  
   _...AND THE MEDICINE THE CITY KNOWS (R2f, research-design Q17)._
16. **L16103** `sickness.setMedicine(medicalRows, month, generalCoverage);` → [Sickness.setMedicine](map/Sickness.md) (L241)
17. **L16106** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
18. **L16107** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L8587)
19. **L16121** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
20. **L16134** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L406)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
21. **L16145** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L345)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
22. **L16146** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L152)
23. **L16147** `double[] orphansByBand = new double[AgeBand.values().length];`
24. **L16148** `double[] inBand = new double[AgeBand.values().length];`
25. **L16149** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L262), [PopulationCohorts.get](map/PopulationCohorts.md) (L133)
26. **L16153** `double[] careFactors = mortalityFactors;`
27. **L16154** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L473), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
28. **L16164** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L416), [Sickness.seed](map/Sickness.md) (L342), [Health.getSickRate](map/Health.md) (L334), [Health.getSickRateWithoutInfluenza](map/Health.md) (L337)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
29. **L16168** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L276)
30. **L16175** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
31. **L16176** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L133)
32. **L16177** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
33. **L16197** `double[][] causeRates = causesOfDeath.rates(month, inBand, mortalityFactors,` → [CausesOfDeath.rates](map/CausesOfDeath.md) (L558), [Game.getWaterRatio](map/Game.md) (L3236), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L5389), [Health.getOutbreakSeverity](map/Health.md) (L364), [PopulationCohorts.getLastBirths](map/PopulationCohorts.md) (L134)  
   _...AND WHAT THEY DIE OF (R0b)._
34. **L16200** `double[] causeTotals = CausesOfDeath.bandTotals(causeRates);` → [CausesOfDeath.bandTotals](map/CausesOfDeath.md) (L492)
35. **L16202** `for (AgeBand b : AgeBand.values()) {` → [CausesOfDeath.isHeldOff](map/CausesOfDeath.md) (L533), [CausesOfDeath.keptFactor](map/CausesOfDeath.md) (L469)  
   _...held off for a harness fixture (R0b call 7): no cause, and the swing whole._
36. **L16207** `cohorts.advanceMonth(mortalityFactors, illness, violence, causeTotals,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L242), [Healthcare.birthFactor](map/Healthcare.md) (L425)
37. **L16209** `causesOfDeath.book(month, causeRates, cohorts.getCauseDeaths());` → [CausesOfDeath.book](map/CausesOfDeath.md) (L635), [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L154)
38. **L16210** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L448), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L140)
39. **L16214** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
40. **L16215** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L143)
41. **L16216** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L140)
42. **L16217** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L145)
43. **L16218** `double[] causedDead = cohorts.getCauseDeaths();` → [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L154)
44. **L16219** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b] + causedDead[b];`
45. **L16220** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L509), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
46. **L16223** `lastOrphanDeaths = outsideDead[0];`
47. **L16224** `lastUnhousedDeaths = outsideDead[1];`
48. **L16231** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L522), [Unemployment.takeEvicted](map/Unemployment.md) (L215)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
49. **L16234** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
50. **L16248** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L133)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
51. **L16255** `HouseholdBalance.BankruptcyDepartures discharged = householdBalance.prepareBankruptcyDepartures(cohorts);` → [HouseholdBalance.prepareBankruptcyDepartures](map/HouseholdBalance.md) (L5219)  
   _The families the bank discharged last month are leaving._
52. **L16256** `migration.setBankruptcyDepartures(discharged.people());` → [Migration.setBankruptcyDepartures](map/Migration.md) (L1048)
53. **L16264** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L325), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L479)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
54. **L16268** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L366), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
55. **L16273** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L928), [BuildingManager.homesBySize](map/BuildingManager.md) (L8093)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
56. **L16277** `migration.setYearMix(foundedUnfitted() ? ArrivalMix.at(month) : null);` → [Migration.setYearMix](map/Migration.md) (L1273), [Game.foundedUnfitted](map/Game.md) (L1314), [ArrivalMix.at](map/ArrivalMix.md) (L145)  
   _...and, in a city founded the 1900 way, what the world's movers hold this year (R0d; ArrivalMix): the skills the month's arrivals are drawn from._
57. **L16279** `double netMigration = migration.monthlyNet(` → [Migration.monthlyNet](map/Migration.md) (L941), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L15908), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L8082), [PopulationCohorts.share](map/PopulationCohorts.md) (L169)
58. **L16293** `discharged = householdBalance.limitBankruptcyDepartures(discharged, migration.getLastBankruptcyDepartures());` → [HouseholdBalance.limitBankruptcyDepartures](map/HouseholdBalance.md) (L5249), [Migration.getLastBankruptcyDepartures](map/Migration.md) (L1052)
59. **L16294** `cohorts.migrate(netMigration, discharged);` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L463)
60. **L16296** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
61. **L16308** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L17655)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
62. **L16310** `double[] jobsByTier = new double[PayTier.values().length];`
63. **L16311** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)
64. **L16312** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1193)
65. **L16314** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
66. **L16326** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
67. **L16327** `double[] wageByTier = new double[PayTier.values().length];`
68. **L16328** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L274)
69. **L16329** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
70. **L16332** `for (int t = 0; t < wageByTier.length; t++) {`
71. **L16348** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L8663)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
72. **L16349** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L1129)
73. **L16350** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
74. **L16351** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L193), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L8663), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161), [Game.unskilledWage](map/Game.md) (L16986)
75. **L16359** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L241), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
76. **L16361** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L857), [Crime.prisoners](map/Crime.md) (L370)
77. **L16363** `double ownerAdults = ownerAdultsWanted();` → [Game.ownerAdultsWanted](map/Game.md) (L16647)  
   _The owner families take no job (BO1): two adults a company with a family, at most a share of the adults (BO1-S)._
78. **L16364** `populationManager.setOwners(ownerAdults);` → [PopulationManager.setOwners](map/PopulationManager.md) (L862)
79. **L16365** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L140)
80. **L16367** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1166)
81. **L16368** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L868)
82. **L16369** `unemployment.setRetiringShare(cohorts.promotedShare(AgeBand.ADULT));   // HS2` → [Unemployment.setRetiringShare](map/Unemployment.md) (L287), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161)
83. **L16370** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L303), [Game.unskilledWage](map/Game.md) (L16986), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _HS2_
84. **L16374** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L282), [Migration.getLastArrivals](map/Migration.md) (L590), [PopulationCohorts.share](map/PopulationCohorts.md) (L169)  
   _The month's adult arrivals look for work next month._
85. **L16385** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying, ownerAdults);` → [FamilyModel.rebuild](map/FamilyModel.md) (L477), [Crime.prisoners](map/Crime.md) (L370)  
   _...and the owner families: two adults a company with a family, outside the families too (BO1), the same adults as above._
86. **L16386** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L277), [Unemployment.getHoused](map/Unemployment.md) (L155)
87. **L16390** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L5148), [Education.getFinished](map/Education.md) (L897)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
88. **L16393** `householdBalance.setFlows(cohorts);` → [HouseholdBalance.setFlows](map/HouseholdBalance.md) (L2341)  
   _...and the month's flows, the pyramid's own (HS1): who retired, died and left carries their money at this month's census, before the pool._
89. **L16405** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L8093)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
90. **L16406** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1527)
91. **L16407** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1725)
92. **L16408** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1811), [FamilyModel.house](map/FamilyModel.md) (L1527)
93. **L16409** `buildingManager.getHousingInventory().match(families.occupiedBySize());` → [BuildingManager.getHousingInventory](map/BuildingManager.md) (L5241), [FamilyModel.occupiedBySize](map/FamilyModel.md) (L1695)
94. **L16414** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L3245), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L1386), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L1387)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
95. **L16418** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1585)  
   _And the advisor prices a new home on who would move into it._
96. **L16419** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1590)
97. **L16435** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L2060), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L915)  
   _...and the ones who cannot afford one either._
98. **L16437** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L2102), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L1022), [Unemployment.getHoused](map/Unemployment.md) (L155), [FamilyModel.getSeekers](map/FamilyModel.md) (L286)  
   _...and the people outside the families, with their own kind._
99. **L16476** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L161), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
100. **L16477** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L483), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8739), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8828), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L135), [Game.burialShare](map/Game.md) (L16775), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L8550), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L8587)
101. **L16487** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L2046), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Healthcare.getFees](map/Healthcare.md) (L732)
102. **L16502** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L4313), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
103. **L16503** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L425), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L8626), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8739), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8828), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161), [Migration.getLastDepartures](map/Migration.md) (L597)
104. **L16522** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L872), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L8790), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L8803)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
105. **L16529** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1088), [Education.getGraduates](map/Education.md) (L775)
106. **L16530** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L1014), [Education.getLicences](map/Education.md) (L791)
107. **L16533** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L867), [Education.getStudying](map/Education.md) (L614)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
108. **L16536** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L1056)  
   _A licence holder is a graduate first, and the graduate count has just moved._
109. **L16538** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L2055), [Education.getGrossCost](map/Education.md) (L838), [Education.getFees](map/Education.md) (L846)
110. **L16547** `settleResearchBill(fill);` → [Game.settleResearchBill](map/Game.md) (L1563)  
   _...AND THE RESEARCH (R3): beside the schools because their staff are its faculty and its room, at the same wages and fill they were paid at._
111. **L16548** `workTheResearch(fill);` → [Game.workTheResearch](map/Game.md) (L1585)
112. **L16554** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8739), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8828)  
   _6c._
113. **L16558** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L2068), [Crime.getGrossCost](map/Crime.md) (L452)
114. **L16591** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L3255), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L3311), [Motoring.journeyFuel](map/Motoring.md) (L175)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
115. **L16593** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L275), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
116. **L16594** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L200), [Game.getInfrastructureManager](map/Game.md) (L3255)
117. **L16599** `if (homeFuel.isOn() \|\| homeFuel.hasSale()) homeFuel.strike(this, homeFuelHomes());` → [HomeFuel.isOn](map/HomeFuel.md) (L78), [HomeFuel.hasSale](map/HomeFuel.md) (L81), [HomeFuel.strike](map/HomeFuel.md) (L98), [Game.homeFuelHomes](map/Game.md) (L4818)  
   _...and the homes' lamp oil and coal (R0c, the home form; HomeFuel): the homes that light with a lamp and heat with a stove ask Retail for their month's JET and THERMAL_COAL, drawn and sold at the p..._
118. **L16603** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2121), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8739), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8828), [Game.portsBill](map/Game.md) (L8908), [Game.getInfrastructureManager](map/Game.md) (L3255), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
119. **L16611** `economyManager.setTransitUpkeep(buildingManager.getUpkeepByCategory(BuildingType.INFRASTRUCTURE) + portsBill());` → [EconomyManager.setTransitUpkeep](map/EconomyManager.md) (L2131), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8828), [Game.portsBill](map/Game.md) (L8908)  
   _...and the upkeep part of that bill, which the treasury pays Business Services (HL1; buyUpkeep()), the rest being the crews' wages._
120. **L16617** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L757), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L5389), [Game.unhousedShareOfCity](map/Game.md) (L16676), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
121. **L16633** `sickness.advanceMonth(health.getSickRate(), health.getSickRateWithoutInfluenza(),` → [Sickness.advanceMonth](map/Sickness.md) (L292), [Health.getSickRate](map/Health.md) (L334), [Health.getSickRateWithoutInfluenza](map/Health.md) (L337)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 182-253 (72 lines)

1. **L185** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L186** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L187** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L8109)
4. **L188** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L548), [Game.getFamilies](map/Game.md) (L16013)
5. **L194** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L545), [Game.getFamilies](map/Game.md) (L16013)  
   _The two inputs to the rent price, which is a market now._
6. **L197** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L549), [Game.getFamilies](map/Game.md) (L16013)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L201** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L546), [Game.marginalHousingCost](map/Game.md) (L21135)
8. **L203** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2156), [Game.getCohorts](map/Game.md) (L16012)
9. **L209** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L115), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L210** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L127), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L8916)
11. **L213** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L200)
12. **L220** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L109), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L248)  
   _The split behind that total._
13. **L223** `UtilitiesHandler utility = servicesManager.getUtilitiesHandler();` → [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L324)  
   _The struck book (PW1): this month's ratio, the prices its bills strike at when the statements are struck at the top of next month, and the fuel the utility buys then._
14. **L224** `utility.strikePower();`
15. **L225** `economyManager.setPowerPrices(utility.getPricePerWatt(), utility.getIndustryPricePerWatt(), utility.getWorksPricePerW...` → [EconomyManager.setPowerPrices](map/EconomyManager.md) (L177)
16. **L227** `economyManager.setPowerSeason(utility.getPowerSeason());` → [EconomyManager.setPowerSeason](map/EconomyManager.md) (L185)  
   _...and its season, the kWh a kW drawn is billed on (PW1-R)._
17. **L229** `economyManager.setPricePerWaterUnit(utility.getPricePerWaterUnit(), utility.getMeanPricePerWaterUnit());` → [EconomyManager.setPricePerWaterUnit](map/EconomyManager.md) (L190)  
   _...and the water's, its tariff and the works' power the same book struck (PW1-W): the sectors billed at the season, the planners at the mean (PW1-R)._
18. **L230** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L200), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L332)
19. **L231** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L201), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L336)
20. **L246** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L203), [Game.getInfrastructureManager](map/Game.md) (L3255)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
21. **L249** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L231), [Game.getHealth](map/Game.md) (L16016)  
   _The fourth ratio._
22. **L250** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1732)

## SimulationEngine.updateServices() - SimulationEngine.java lines 255-262 (8 lines)

1. **L256** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L298), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232)
2. **L257** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L316), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)
3. **L260** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L393), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L261** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L89)

## Game.run() - Game.java lines 790-801 (12 lines)

1. **L791** `boolean starting = !initialized;`
2. **L792** `if (starting) {` → [Game.layTheLadder](map/Game.md) (L1384), [Game.basketHearsTheYear](map/Game.md) (L17324)
3. **L796** `initialize();` → [Game.initialize](map/Game.md) (L803)
4. **L797** `foundingBank();` → [Game.foundingBank](map/Game.md) (L948)
5. **L798** `foundingYard();` → [Game.foundingYard](map/Game.md) (L1034)
6. **L799** `if (starting && founding.isUnfitted()) unfitTheFounding();` → [Founding.isUnfitted](map/Founding.md) (L480), [Game.unfitTheFounding](map/Game.md) (L1323)
7. **L800** `if (starting) foundOnGold();` → [Game.foundOnGold](map/Game.md) (L1348)

