# The order the month runs in

Generated 2026-11-15 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 13201-14151 (951 lines)

1. **L13209** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L13210** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L13211** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1232)
4. **L13212** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L13221** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L13222** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L16233)
7. **L13234** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L5722)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L13235** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L12978)
9. **L13237** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L14911)
10. **L13246** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L13247** `monthsSinceAutosave++;`
12. **L13254** `policyHearsTheMonth();` → [Game.policyHearsTheMonth](map/Game.md) (L1179)  
   _THE POLICY HEARS THE MONTH FIRST (R0): each lever opens on Canada's date, and a lever still shut is held at zero before anything in this month is taxed or paid._
13. **L13256** `basketHearsTheYear();` → [Game.basketHearsTheYear](map/Game.md) (L16293)  
   _...and the homes' lamp and stove shares of the year (R0e), in a city founded the 1900 way, before this month's fuel is struck (6d)._
14. **L13262** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L9689)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
15. **L13268** `strikeResearchPosts();` → [Game.strikeResearchPosts](map/Game.md) (L1351)  
   _THE RESEARCH POSTS ARE STRUCK NEXT (R3), AFTER THE DEMOLITIONS (R3b): what the month's grants buy within the schools' room, at the wages the labour market has now._
16. **L13271** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2327)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
17. **L13272** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L741)
18. **L13275** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L370)  
   _The register's month: nothing offered, nothing paid, until it is._
19. **L13276** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L514)
20. **L13277** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1123)
21. **L13278** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1661)
22. **L13296** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
23. **L13324** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
24. **L13325** `cityDiscountForBank = cityDiscountThisMonth;`
25. **L13326** `cityDebtRaisedThisMonth = 0;`
26. **L13327** `cityDiscountThisMonth = 0;`
27. **L13328** `cityPrincipalRepaidThisMonth = 0;`
28. **L13329** `bankPrincipalRepaidThisMonth = 0;`
29. **L13332** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
30. **L13333** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
31. **L13334** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
32. **L13335** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L477)
33. **L13336** `foreignDebtRaisedThisMonth = 0;`
34. **L13337** `foreignPrincipalRepaidThisMonth = 0;`
35. **L13338** `foreignInterestPaidThisMonth = 0;`
36. **L13343** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
37. **L13344** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
38. **L13345** `sectorInvested.clear();`
39. **L13346** `salvageThisMonth.clear();`
40. **L13347** `salvageBySector.clear();`
41. **L13348** `salvageUsedThisMonth = 0;`
42. **L13349** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L344)
43. **L13350** `double pooledBefore = 0;`
44. **L13351** `for (double p : poolsBefore) pooledBefore += p;`
45. **L13352** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2125)
46. **L13354** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1326)
47. **L13355** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L2781)
48. **L13356** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L258)
49. **L13367** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
50. **L13368** `buybackAbroad = buybackAbroadUnsettled;`
51. **L13369** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
52. **L13370** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L569)
53. **L13373** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L6124)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
54. **L13397** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1683)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
55. **L13398** `if (!isLeverOpen(PolicyDates.Lever.POLICY_RATE) \|\| foreign.isCanadaGold()) {` → [Game.isLeverOpen](map/Game.md) (L1156), [ForeignAccounts.isCanadaGold](map/ForeignAccounts.md) (L1510), [DebtManager.setPolicyRate](map/DebtManager.md) (L89), [DebtManager.worldRate](map/DebtManager.md) (L882), [DebtManager.tiedRate](map/DebtManager.md) (L238), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1785), [DebtManager.isAutopilot](map/DebtManager.md) (L126), [DebtManager.getAim](map/DebtManager.md) (L196), [DebtManager.goldRate](map/DebtManager.md) (L254), [Game.getGoldCover](map/Game.md) (L6044), [DebtManager.currencyRate](map/DebtManager.md) (L227), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L232), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L489)
56. **L13416** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L14696)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
57. **L13423** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L19303)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
58. **L13431** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L18778)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
59. **L13436** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L18404)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
60. **L13455** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L19434), [Game.studentGrantBill](map/Game.md) (L15984)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
61. **L13469** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L19459)  
   _...AND EI, THE SAME WAY (0.7.3)._
62. **L13487** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1730), [Bank.chargeTax](map/Bank.md) (L4063), [Game.bankProfitTaxRate](map/Game.md) (L4702)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
63. **L13489** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L14153)
64. **L13490** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
65. **L13491** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L14771)
66. **L13492** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2497)
67. **L13493** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L14678)
68. **L13497** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
69. **L13498** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L18171)
70. **L13499** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
71. **L13500** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1568)
72. **L13504** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L14716)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
73. **L13515** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
74. **L13517** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
75. **L13518** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L3073)
76. **L13522** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3231)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
77. **L13523** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L3067)
78. **L13539** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2812), [DebtManager.getAccretedForBank](map/DebtManager.md) (L3025)  
   _THE DISCOUNT ACCRETES (0.7.1)._
79. **L13540** `legacyDiscountDue = 0;`
80. **L13544** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
81. **L13545** `cityDiscountForBank = 0;`
82. **L13547** `double sectorInterestPaid = 0;`
83. **L13548** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L3056)
84. **L13554** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2793), [BondMarket.getCouponsStruck](map/BondMarket.md) (L1017)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
85. **L13555** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L569), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1024)
86. **L13557** `refreshBank();` → [Game.refreshBank](map/Game.md) (L4979)
87. **L13558** `lender.projectLoansSettled();`
88. **L13561** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L5147)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
89. **L13584** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3124), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1199), [BuildingManager.countByName](map/BuildingManager.md) (L6895), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1206)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
90. **L13587** `bankMaintenanceDue = 0;`
91. **L13595** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L4683), [Bank.openBranches](map/Bank.md) (L2731), [BuildingManager.countByName](map/BuildingManager.md) (L6895)  
   _Capital for whatever branches opened this month._
92. **L13640** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1191), [DebtManager.getPolicyRate](map/DebtManager.md) (L87)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
93. **L13642** `hotMoney.setRiskOff(worldRiskOffLevel());` → [CapitalFlows.setRiskOff](map/CapitalFlows.md) (L707), [Game.worldRiskOffLevel](map/Game.md) (L2216)  
   _The risk-off the world is in (XR4): handed once here, it holds for the hot money's month below as well._
94. **L13643** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L542), [DebtManager.worldRate](map/DebtManager.md) (L882), [DebtManager.countryPremium](map/DebtManager.md) (L1208), [Bank.headroom](map/Bank.md) (L5546)
95. **L13649** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L605)
96. **L13650** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L613)
97. **L13651** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L630), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L608)
98. **L13655** `refreshBank();` → [Game.refreshBank](map/Game.md) (L4979)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
99. **L13665** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3429), [DebtManager.getPolicyRate](map/DebtManager.md) (L87)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
100. **L13678** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L317), [Bank.getPlacementIncome](map/Bank.md) (L3703)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
101. **L13679** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L325), [Bank.getFundingCost](map/Bank.md) (L3701)
102. **L13680** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L310), [Bank.getBranches](map/Bank.md) (L5219), [Bank.wholesaleFunding](map/Bank.md) (L3409)
103. **L13685** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
104. **L13686** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5274)
105. **L13690** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3863)  
   _The month is final, so the figure next month's tax is charged on is final too._
106. **L13694** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L395)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
107. **L13711** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L14664)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
108. **L13712** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L3215)
109. **L13722** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L4046), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3345)  
   _...AND THE SAVERS ARE PAID._
110. **L13723** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3346)
111. **L13730** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1521)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
112. **L13731** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L620), [EconomyManager.setSectorCash](map/EconomyManager.md) (L625), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1523)
113. **L13762** `payDividends();` → [Game.payDividends](map/Game.md) (L4811)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
114. **L13763** `tradeShares();` → [Game.tradeShares](map/Game.md) (L4883)
115. **L13773** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L5457)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
116. **L13783** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1154), [Exchange.cityMarketValue](map/Exchange.md) (L1837)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
117. **L13784** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L5090)
118. **L13789** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L452), [DebtManager.firmBookRatio](map/DebtManager.md) (L2966), [DebtManager.firmBookYield](map/DebtManager.md) (L2972)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
119. **L13790** `outward.takeMonth(bank.depositRate(), debtManager.worldRate(),` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L248), [Bank.depositRate](map/Bank.md) (L3338), [DebtManager.worldRate](map/DebtManager.md) (L882), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246)
120. **L13794** `householdBalance.investAbroad(bank.depositRate(), debtManager.worldRate(), foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3192), [Bank.depositRate](map/Bank.md) (L3338), [DebtManager.worldRate](map/DebtManager.md) (L882), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246)  
   _...and the households, by the same rule, with what the owners were just paid._
121. **L13797** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3528), [DebtManager.householdBookYield](map/DebtManager.md) (L2995), [Bank.depositRate](map/Bank.md) (L3338)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
122. **L13815** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
123. **L13816** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5274)
124. **L13817** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L5380)
125. **L13852** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
126. **L13853** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L748)
127. **L13856** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L651), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1797)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
128. **L13857** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L380), [Bank.depositRate](map/Bank.md) (L3338), [DebtManager.getRate](map/DebtManager.md) (L1429), [DebtManager.worldRate](map/DebtManager.md) (L882), [DebtManager.countryPremium](map/DebtManager.md) (L1208), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2491), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L2721), [Game.yearlyDepreciation](map/Game.md) (L16359), [Bank.isInsolvent](map/Bank.md) (L1808), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1367)
129. **L13879** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
130. **L13880** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1684)
131. **L13881** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1698)
132. **L13882** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1672), [CapitalFlows.getStock](map/CapitalFlows.md) (L634)
133. **L13884** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L18144)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
134. **L13886** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L519)
135. **L13889** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
136. **L13899** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L2572), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2491)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
137. **L13931** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L246), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L315), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L362)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
138. **L13950** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L375), [Game.realRateDifferential](map/Game.md) (L12301)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
139. **L13956** `handTheWorldToTheD();` → [Game.handTheWorldToTheD](map/Game.md) (L2337)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
140. **L13957** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246)
141. **L13958** `boolean onGoldBeforeReprice = foreign.getRegime() == ForeignAccounts.Regime.GOLD \|\| foreign.isCanadaGold();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1683), [ForeignAccounts.isCanadaGold](map/ForeignAccounts.md) (L1510)
142. **L13959** `foreign.setMoneyBase(centralBank.m0());   // M0 is the gold cover's base, handed right before the reprice (GD3)` → [ForeignAccounts.setMoneyBase](map/ForeignAccounts.md) (L1586), [CentralBank.m0](map/CentralBank.md) (L802)
143. **L13960** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L908)  
   _M0 is the gold cover's base, handed right before the reprice (GD3)_
144. **L13968** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1799), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1802), [Game.getCurrency](map/Game.md) (L2800), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246), [DebtManager.dropGoldAim](map/DebtManager.md) (L260), [Game.foundedUnfitted](map/Game.md) (L1214), [DebtManager.hearMoneyMonth](map/DebtManager.md) (L81), [ForeignAccounts.isCanadaGold](map/ForeignAccounts.md) (L1510)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
145. **L13979** `noteTheWorldMoneys(rateBeforeReprice);` → [Game.noteTheWorldMoneys](map/Game.md) (L2344)  
   _...AND THE WORLD'S NEWS (WN4): money that ran to safety abroad, a world peg that broke._
146. **L13987** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L685), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L864)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
147. **L14005** `economyManager.getNationalAccounts().setDebtExchangeGain(debtManager.revalueForeign(foreign.getRate()));` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [DebtManager.revalueForeign](map/DebtManager.md) (L966), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246)  
   _CD7: the same press books the exchange gain (loss) on the dollar paper - each piece's carrying value in dollars times the move from the rate it was last valued at - on the month the strike already ..._
148. **L14006** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L2675), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L987), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246)
149. **L14013** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L3071)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
150. **L14014** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L1116), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L2548), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L2529)
151. **L14015** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1360)
152. **L14016** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L12275)
153. **L14029** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L16137), [Game.indexSpends](map/Game.md) (L16165), [Game.indexFees](map/Game.md) (L16180), [Game.indexFeeSpends](map/Game.md) (L16196)  
   _...AND WHAT THE MONTH COST A FAMILY._
154. **L14041** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L307)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
155. **L14042** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L87), [DebtManager.neutralRate](map/DebtManager.md) (L564), [DebtManager.holdingRate](map/DebtManager.md) (L518), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
156. **L14045** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L16258)
157. **L14048** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246)  
   _A year of the rate, so next year can tell a drift from a run._
158. **L14049** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
159. **L14051** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L18975)
160. **L14053** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L960)
161. **L14087** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
162. **L14088** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L344)
163. **L14107** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L19229)
164. **L14109** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L3706)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
165. **L14111** `tallyThePartners();` → [Game.tallyThePartners](map/Game.md) (L1901)  
   _The month's trade split to the world's nations (WN3; THE WORLD MARKET), after its last draw: before the history and the autosave._
166. **L14112** `recordMonth();` → [Game.recordMonth](map/Game.md) (L16678)
167. **L14114** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
168. **L14116** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L2778)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
169. **L14137** `reportSaves();` → [Game.reportSaves](map/Game.md) (L16845)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
170. **L14138** `SaveWriter.Job autosaved = null;`
171. **L14139** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosaveJob](map/Game.md) (L16767)
172. **L14148** `recordForReplay(autosaved);` → [Game.recordForReplay](map/Game.md) (L17499)  
   _A recorded city keeps this whole month, beside the autosave (TR1; THE RECORDING), from the autosave's fill when it made one (TR2)._
173. **L14150** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L3061)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._

## Game.startOfMonthUpdate() - Game.java lines 14153-14467 (315 lines)

1. **L14205** `ensureWorldMarket();` → [Game.ensureWorldMarket](map/Game.md) (L1813)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L14206** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L270)
3. **L14207** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L309), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L315)
4. **L14209** `strikeTheWorldMarket();` → [Game.strikeTheWorldMarket](map/Game.md) (L1866)  
   _...AND THE WORLD'S BAND BEHIND IT (WN3): the January solve when the year has turned, then every good's floor and ceiling from last month's trade; before any market clears._
5. **L14211** `double gold = goldUsdPerOunce(WorldMoneys.monthIndex(month));` → [Game.goldUsdPerOunce](map/Game.md) (L2190), [WorldMoneys.monthIndex](map/WorldMoneys.md) (L270)  
   _Bullion's nominal US$ path and spot exchange already include the dollar's price path (GD4)._
6. **L14212** `if (Double.isFinite(gold) && gold > 0) {` → [EconomyManager.getMarkets](map/EconomyManager.md) (L51), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246)
7. **L14231** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L478), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
8. **L14238** `labourMarket.followAverageWage(populationManager.getAverageWage());` → [LabourMarket.followAverageWage](map/LabourMarket.md) (L985), [PopulationManager.getAverageWage](map/PopulationManager.md) (L169)  
   _The floor's tie to the average wage (WG1) is struck once a month from last month's wages, before repriceLabour() walks them._
9. **L14258** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L752), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
10. **L14260** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L337), [LandManager.getPricePerSqFt](map/LandManager.md) (L633)
11. **L14261** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L10470)
12. **L14271** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L14664)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
13. **L14276** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5219), [Bank.isInsolvent](map/Bank.md) (L1808)  
   _A failed bank lends nothing._
14. **L14289** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L5090)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
15. **L14290** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4968)
16. **L14291** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4996)
17. **L14296** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4651)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
18. **L14297** `economyManager.getBusinessDebtManager().setProjectBudget(bank);` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
19. **L14298** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L614)
20. **L14305** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L5130)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
21. **L14306** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L643), [Bank.prime](map/Bank.md) (L1137), [DebtManager.getPolicyRate](map/DebtManager.md) (L87), [Bank.insuredMortgageRate](map/Bank.md) (L1158)
22. **L14310** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L979)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
23. **L14315** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L813)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
24. **L14319** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L8242)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
25. **L14323** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L8191)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
26. **L14326** `outward.payCoupons(debtManager.worldRate(), foreign.getRate(), economyManager);` → [OutwardInvestment.payCoupons](map/OutwardInvestment.md) (L218), [DebtManager.worldRate](map/DebtManager.md) (L882), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1246)  
   _...and the coupon on the dollars its businesses hold abroad, struck as a line of their profit and kept abroad: the profit tax and the dividend see it, the till pays them (HD1, 2026-10-29; HD2, 2026..._
27. **L14340** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L3056)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
28. **L14341** `double constructionWorkDone = construction.getRecognisedThisMonth();`
29. **L14343** `if (construction.getFittingThisMonth() != 0) constructionWorkDone += construction.getFittingThisMonth();`  
   _...and the equipment the builders were paid to fit to standing buildings since the last strike banked its ledger (R1b): capital too, on the builders' side, once._
30. **L14352** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1227)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
31. **L14354** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L3061), [Game.getSectors](map/Game.md) (L3056)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
32. **L14356** `settleReserve();` → [Game.settleReserve](map/Game.md) (L6219)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
33. **L14360** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L3056)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
34. **L14366** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
35. **L14367** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2149), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L292), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L844), [EconomyManager.getResearchBill](map/EconomyManager.md) (L2036), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2077), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L635), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L637), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L824)
36. **L14423** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
37. **L14424** `monthlyMaterialImportBill = 0;`
38. **L14429** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L4051)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
39. **L14437** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L248)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
40. **L14445** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
41. **L14454** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
42. **L14458** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1456)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
43. **L14464** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L3747)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
44. **L14466** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L14468)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-157 (126 lines)

1. **L33** `game.moneyHearsTheMonth();` → [Game.moneyHearsTheMonth](map/Game.md) (L1166)
2. **L68** `int siteOutput = game.workTheFittings();` → [Game.workTheFittings](map/Game.md) (L1554)  
   _...less the fittings the crews work first (R1b; Game.workTheFittings()): after the repairs, before the sites, struck here once and used for the advance and for the work the sector is paid for below._
3. **L71** `Game.ForeignOffer foreign = game.foreignCrewsOffer();` → [Game.foreignCrewsOffer](map/Game.md) (L8143)  
   _The foreign contractor's month beside the city's own (FC1): nothing unless the repairs took the builders' points and left the sites none - see Game, THE FOREIGN CREWS._
4. **L73** `java.util.List<BuildingManager.Completion> opened = buildingManager.advanceConstruction(siteOutput + foreign.points()...` → [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5446)  
   _His points are in the month the sites take, and not on a rushed site's overtime (FC1b): his crews are not the city's._
5. **L74** `game.recordCompletions(opened);` → [Game.recordCompletions](map/Game.md) (L15780)
6. **L77** `game.fitWhatFinished(opened);` → [Game.fitWhatFinished](map/Game.md) (L1277)  
   _...and what the buildings that opened come with (R1b): in a city founded the 1900 way, only the equipment that pays for a new building; nothing in a city founded the default way, where every buildi..._
7. **L93** `game.getRecipes().apply(game);   // the ways the buildings run, at the counts the month leaves (R1c): first, the stea...` → [Game.getRecipes](map/Game.md) (L1198)  
   _...AND THE 1900 FORMS AT REST (R1b; R1's stranger's review, 2026-11-05): the shares the counts set, said again now that the month has changed the counts' weights._
8. **L94** `game.getEquipmentForms().apply(game);` → [Game.getEquipmentForms](map/Game.md) (L1204)  
   _the ways the buildings run, at the counts the month leaves (R1c): first, the steam form below weighs each building by its electricity_
9. **L102** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L9268), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5531)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
10. **L106** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5583)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
11. **L107** `double ownMonth = overtime == 0 ? siteOutput : siteOutput + overtime;`
12. **L109** `game.settleForeignCrews(foreign, buildingManager.getPointsBuilt() - Math.max(0, ownMonth));` → [Game.settleForeignCrews](map/Game.md) (L8164), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5547)  
   _...and the contractor is paid for what the sites took past the city's own month (FC1)._
13. **L110** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L9376), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6858), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5547)
14. **L116** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L9316), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L6867)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
15. **L121** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L9983), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5576)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
16. **L130** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L187)  
   _Roads, before anything reads them._
17. **L137** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L159)  
   _Recount the posts and refresh the wage arrays._
18. **L153** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L15072)  
   _THE POPULATION STEP._
19. **L155** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L173)
20. **L156** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L237)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 159-171 (13 lines)

1. **L164** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L8036)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L165** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L14935)
3. **L166** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L14948)
4. **L169** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L16584)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L170** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L370)

## Game.advanceDemographics() - Game.java lines 15072-15642 (571 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L15077** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L693), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L274), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L15091** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L15092** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L15673)
4. **L15093** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L15673)
5. **L15094** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L15673)
6. **L15095** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7706)
7. **L15097** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
8. **L15111** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L15112** `java.util.Arrays.fill(affordable, 1);`
10. **L15113** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L15692)
11. **L15116** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L15117** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L15118** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L15121** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L646)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L15124** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L15125** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7706)
17. **L15139** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L15152** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L406)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L15163** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L15164** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L15165** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L15166** `double[] inBand = new double[AgeBand.values().length];`
23. **L15167** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L102)
24. **L15171** `double[] careFactors = mortalityFactors;`
25. **L15172** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L466), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
26. **L15182** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L334)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L15185** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L15192** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L15193** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L102)
30. **L15194** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
31. **L15214** `double[][] causeRates = causesOfDeath.rates(month, inBand, mortalityFactors,` → [CausesOfDeath.rates](map/CausesOfDeath.md) (L450), [Game.getWaterRatio](map/Game.md) (L3047), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3993), [Health.getOutbreakSeverity](map/Health.md) (L356), [PopulationCohorts.getLastBirths](map/PopulationCohorts.md) (L103)  
   _...AND WHAT THEY DIE OF (R0b)._
32. **L15217** `double[] causeTotals = CausesOfDeath.bandTotals(causeRates);` → [CausesOfDeath.bandTotals](map/CausesOfDeath.md) (L384)
33. **L15219** `for (AgeBand b : AgeBand.values()) {` → [CausesOfDeath.isHeldOff](map/CausesOfDeath.md) (L425), [CausesOfDeath.keptFactor](map/CausesOfDeath.md) (L361)  
   _...held off for a harness fixture (R0b call 7): no cause, and the swing whole._
34. **L15224** `cohorts.advanceMonth(mortalityFactors, illness, violence, causeTotals,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L177), [Healthcare.birthFactor](map/Healthcare.md) (L425)
35. **L15226** `causesOfDeath.book(month, causeRates, cohorts.getCauseDeaths());` → [CausesOfDeath.book](map/CausesOfDeath.md) (L513), [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L123)
36. **L15227** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L270), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L109)
37. **L15231** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
38. **L15232** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L112)
39. **L15233** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L109)
40. **L15234** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L114)
41. **L15235** `double[] causedDead = cohorts.getCauseDeaths();` → [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L123)
42. **L15236** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b] + causedDead[b];`
43. **L15237** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L502), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
44. **L15240** `lastOrphanDeaths = outsideDead[0];`
45. **L15241** `lastUnhousedDeaths = outsideDead[1];`
46. **L15248** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L404), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
47. **L15251** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
48. **L15265** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L102)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
49. **L15272** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L1039), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3889), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
50. **L15281** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L322), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L479)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
51. **L15285** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L363), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
52. **L15290** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L920), [BuildingManager.homesBySize](map/BuildingManager.md) (L7212)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
53. **L15294** `migration.setYearMix(foundedUnfitted() ? ArrivalMix.at(month) : null);` → [Migration.setYearMix](map/Migration.md) (L1258), [Game.foundedUnfitted](map/Game.md) (L1214), [ArrivalMix.at](map/ArrivalMix.md) (L145)  
   _...and, in a city founded the 1900 way, what the world's movers hold this year (R0d; ArrivalMix): the skills the month's arrivals are drawn from._
54. **L15296** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L376), [Migration.monthlyNet](map/Migration.md) (L933), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L14939), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L7191), [PopulationCohorts.share](map/PopulationCohorts.md) (L135)
55. **L15311** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
56. **L15323** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L16618)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
57. **L15325** `double[] jobsByTier = new double[PayTier.values().length];`
58. **L15326** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)
59. **L15327** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1193)
60. **L15329** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
61. **L15341** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
62. **L15342** `double[] wageByTier = new double[PayTier.values().length];`
63. **L15343** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L274)
64. **L15344** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
65. **L15347** `for (int t = 0; t < wageByTier.length; t++) {`
66. **L15363** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7782)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
67. **L15364** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L855)
68. **L15365** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
69. **L15366** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L128), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7782), [Game.unskilledWage](map/Game.md) (L15958)
70. **L15373** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
71. **L15375** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L857), [Crime.prisoners](map/Crime.md) (L370)
72. **L15377** `populationManager.setOwners(2.0 * householdBalance.ownerFamilies());` → [PopulationManager.setOwners](map/PopulationManager.md) (L862), [HouseholdBalance.ownerFamilies](map/HouseholdBalance.md) (L769)  
   _The owner families take no job (BO1): two adults a company with a family._
73. **L15378** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
74. **L15380** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1166)
75. **L15381** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L868)
76. **L15382** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L15958), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
77. **L15386** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L584), [PopulationCohorts.share](map/PopulationCohorts.md) (L135)  
   _The month's adult arrivals look for work next month._
78. **L15397** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying,` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L370), [HouseholdBalance.ownerFamilies](map/HouseholdBalance.md) (L769)  
   _...and the owner families: two adults a company with a family, outside the families too (BO1)._
79. **L15399** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
80. **L15403** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3853), [Education.getFinished](map/Education.md) (L903)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
81. **L15415** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L7212)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
82. **L15416** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1159)
83. **L15417** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1328)
84. **L15418** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1415), [FamilyModel.house](map/FamilyModel.md) (L1159)
85. **L15423** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L3056), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L1018), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L1019)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
86. **L15427** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1361)  
   _And the advisor prices a new home on who would move into it._
87. **L15428** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1366)
88. **L15444** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L915)  
   _...and the ones who cannot afford one either._
89. **L15446** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1693), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L1022), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
90. **L15485** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L161), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
91. **L15486** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L483), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7858), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7947), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L104), [Game.burialShare](map/Game.md) (L15754), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7669), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7706)
92. **L15496** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1998), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Healthcare.getFees](map/Healthcare.md) (L732)
93. **L15511** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L4076), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
94. **L15512** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L403), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7745), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7858), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7947), [Migration.getLastDepartures](map/Migration.md) (L589)
95. **L15531** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L878), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L7909), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L7922)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
96. **L15538** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1088), [Education.getGraduates](map/Education.md) (L781)
97. **L15539** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L1014), [Education.getLicences](map/Education.md) (L797)
98. **L15542** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L867), [Education.getStudying](map/Education.md) (L590)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
99. **L15545** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L1056)  
   _A licence holder is a graduate first, and the graduate count has just moved._
100. **L15547** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L2007), [Education.getGrossCost](map/Education.md) (L844), [Education.getFees](map/Education.md) (L852)
101. **L15556** `settleResearchBill(fill);` → [Game.settleResearchBill](map/Game.md) (L1408)  
   _...AND THE RESEARCH (R3): beside the schools because their staff are its faculty and its room, at the same wages and fill they were paid at._
102. **L15557** `workTheResearch(fill);` → [Game.workTheResearch](map/Game.md) (L1430)
103. **L15563** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7858), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7947)  
   _6c._
104. **L15567** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L2020), [Crime.getGrossCost](map/Crime.md) (L452)
105. **L15600** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L3066), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L2072), [Motoring.journeyFuel](map/Motoring.md) (L175)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
106. **L15602** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L243), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
107. **L15603** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L200), [Game.getInfrastructureManager](map/Game.md) (L3066)
108. **L15608** `if (homeFuel.isOn() \|\| homeFuel.hasSale()) homeFuel.strike(this, homeFuelHomes());` → [HomeFuel.isOn](map/HomeFuel.md) (L78), [HomeFuel.hasSale](map/HomeFuel.md) (L81), [HomeFuel.strike](map/HomeFuel.md) (L98), [Game.homeFuelHomes](map/Game.md) (L4580)  
   _...and the homes' lamp oil and coal (R0c, the home form; HomeFuel): the homes that light with a lamp and heat with a stove ask Retail for their month's JET and THERMAL_COAL, drawn and sold at the p..._
109. **L15612** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2073), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7858), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7947), [Game.portsBill](map/Game.md) (L8213), [Game.getInfrastructureManager](map/Game.md) (L3066), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
110. **L15624** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L757), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3993), [Game.unhousedShareOfCity](map/Game.md) (L15655), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
111. **L15640** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L334)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 173-235 (63 lines)

1. **L176** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L177** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L178** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L7256)
4. **L179** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L507), [Game.getFamilies](map/Game.md) (L15044)
5. **L185** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L504), [Game.getFamilies](map/Game.md) (L15044)  
   _The two inputs to the rent price, which is a market now._
6. **L188** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L508), [Game.getFamilies](map/Game.md) (L15044)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L192** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L505), [Game.marginalHousingCost](map/Game.md) (L20031)
8. **L194** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2094), [Game.getCohorts](map/Game.md) (L15043)
9. **L200** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L201** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L8035)
11. **L204** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L200)
12. **L211** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L248)  
   _The split behind that total._
13. **L212** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L300)
14. **L213** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L304)
15. **L228** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L3066)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L231** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L15047)  
   _The fourth ratio._
17. **L232** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1691)

## SimulationEngine.updateServices() - SimulationEngine.java lines 237-244 (8 lines)

1. **L238** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L266), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232)
2. **L239** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L284), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)
3. **L242** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L341), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L243** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L67)

## Game.run() - Game.java lines 740-744 (5 lines)

1. **L741** `initialize();` → [Game.initialize](map/Game.md) (L746)
2. **L742** `foundingBank();` → [Game.foundingBank](map/Game.md) (L887)
3. **L743** `foundingYard();` → [Game.foundingYard](map/Game.md) (L973)

