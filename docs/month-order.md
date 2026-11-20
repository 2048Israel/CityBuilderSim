# The order the month runs in

Generated 2026-11-20 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 13514-14476 (963 lines)

1. **L13522** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L13523** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L13524** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1249)
4. **L13525** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L13534** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L13535** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L16614)
7. **L13547** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L5860)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L13548** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L13291)
9. **L13550** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L15267)
10. **L13559** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L13560** `monthsSinceAutosave++;`
12. **L13567** `policyHearsTheMonth();` → [Game.policyHearsTheMonth](map/Game.md) (L1208)  
   _THE POLICY HEARS THE MONTH FIRST (R0): each lever opens on Canada's date, and a lever still shut is held at zero before anything in this month is taxed or paid._
13. **L13569** `basketHearsTheYear();` → [Game.basketHearsTheYear](map/Game.md) (L16674)  
   _...and the homes' lamp and stove shares of the year (R0e), in a city founded the 1900 way, before this month's fuel is struck (6d)._
14. **L13575** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L9950)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
15. **L13581** `strikeResearchPosts();` → [Game.strikeResearchPosts](map/Game.md) (L1423)  
   _THE RESEARCH POSTS ARE STRUCK NEXT (R3), AFTER THE DEMOLITIONS (R3b): what the month's grants buy within the schools' room, at the wages the labour market has now._
16. **L13584** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2327)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
17. **L13585** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L742)
18. **L13588** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L370)  
   _The register's month: nothing offered, nothing paid, until it is._
19. **L13589** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L523)
20. **L13590** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1190)
21. **L13591** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1661)
22. **L13609** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
23. **L13637** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
24. **L13638** `cityDiscountForBank = cityDiscountThisMonth;`
25. **L13639** `cityDebtRaisedThisMonth = 0;`
26. **L13640** `cityDiscountThisMonth = 0;`
27. **L13641** `cityPrincipalRepaidThisMonth = 0;`
28. **L13642** `bankPrincipalRepaidThisMonth = 0;`
29. **L13645** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
30. **L13646** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
31. **L13647** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
32. **L13648** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L477)
33. **L13649** `foreignDebtRaisedThisMonth = 0;`
34. **L13650** `foreignPrincipalRepaidThisMonth = 0;`
35. **L13651** `foreignInterestPaidThisMonth = 0;`
36. **L13656** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
37. **L13657** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
38. **L13658** `sectorInvested.clear();`
39. **L13659** `salvageThisMonth.clear();`
40. **L13660** `salvageBySector.clear();`
41. **L13661** `salvageUsedThisMonth = 0;`
42. **L13662** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L614)
43. **L13663** `double householdsBefore = MoneyAudit.householdsHeld(this);` → [MoneyAudit.householdsHeld](map/MoneyAudit.md) (L380)
44. **L13664** `double pooledBefore = 0;`
45. **L13665** `for (double p : poolsBefore) pooledBefore += p;`
46. **L13666** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2146)
47. **L13668** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1326)
48. **L13669** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L2795)
49. **L13670** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L258)
50. **L13681** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
51. **L13682** `buybackAbroad = buybackAbroadUnsettled;`
52. **L13683** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
53. **L13684** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L569)
54. **L13687** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L6263)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
55. **L13711** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1697)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
56. **L13712** `if (!isLeverOpen(PolicyDates.Lever.POLICY_RATE) \|\| foreign.isCanadaGold()) {` → [Game.isLeverOpen](map/Game.md) (L1185), [ForeignAccounts.isCanadaGold](map/ForeignAccounts.md) (L1524), [DebtManager.setPolicyRate](map/DebtManager.md) (L96), [DebtManager.worldRate](map/DebtManager.md) (L901), [DebtManager.tiedRate](map/DebtManager.md) (L256), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1799), [DebtManager.isAutopilot](map/DebtManager.md) (L133), [DebtManager.getAim](map/DebtManager.md) (L214), [DebtManager.goldRate](map/DebtManager.md) (L272), [Game.getGoldCover](map/Game.md) (L6183), [DebtManager.currencyRate](map/DebtManager.md) (L245), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L250), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L508)
57. **L13730** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L15036)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
58. **L13737** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L19716)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
59. **L13745** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L19184)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
60. **L13750** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L18810)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
61. **L13769** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L19848), [Game.studentGrantBill](map/Game.md) (L16365)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
62. **L13783** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L19873)  
   _...AND EI, THE SAME WAY (0.7.3)._
63. **L13801** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1730), [Bank.chargeTax](map/Bank.md) (L4065), [Game.bankProfitTaxRate](map/Game.md) (L4839)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
64. **L13805** `getMarkets().startSellerPricedMonth();` → [Game.getMarkets](map/Game.md) (L3166)  
   _FB3 (FW6-C): the seller-priced goods' markets begin their month here, as the traded goods' do in their clearing: their trades and tally grew for the city's whole life._
65. **L13806** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L14478)
66. **L13807** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
67. **L13808** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L15111)
68. **L13809** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2529)
69. **L13810** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L15018)
70. **L13814** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
71. **L13815** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L18577)
72. **L13816** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
73. **L13817** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1587)
74. **L13821** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L15056)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
75. **L13832** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
76. **L13834** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
77. **L13835** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L3073)
78. **L13839** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3233)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
79. **L13840** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L3067)
80. **L13856** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2812), [DebtManager.getAccretedForBank](map/DebtManager.md) (L3044)  
   _THE DISCOUNT ACCRETES (0.7.1)._
81. **L13857** `legacyDiscountDue = 0;`
82. **L13861** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
83. **L13862** `cityDiscountForBank = 0;`
84. **L13864** `double sectorInterestPaid = 0;`
85. **L13865** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L3161)
86. **L13871** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2793), [BondMarket.getCouponsStruck](map/BondMarket.md) (L1078)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
87. **L13872** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L569), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1085)
88. **L13874** `refreshBank();` → [Game.refreshBank](map/Game.md) (L5116)
89. **L13875** `lender.projectLoansSettled();`
90. **L13878** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L5284)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
91. **L13901** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3124), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1199), [BuildingManager.countByName](map/BuildingManager.md) (L7044), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1206)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
92. **L13906** `getSectors().businessServices().sellBankUpkeep(bank.getOperatingCost());` → [Game.getSectors](map/Game.md) (L3161), [Bank.getOperatingCost](map/Bank.md) (L3172)  
   _...and its running cost is bought of Business Services, as the city's upkeep is (HL1; buyUpkeep()): the bank paid it out of its own cash above._
93. **L13907** `bankMaintenanceDue = 0;`
94. **L13915** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L4820), [Bank.openBranches](map/Bank.md) (L2731), [BuildingManager.countByName](map/BuildingManager.md) (L7044)  
   _Capital for whatever branches opened this month._
95. **L13960** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1191), [DebtManager.getPolicyRate](map/DebtManager.md) (L94)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
96. **L13962** `hotMoney.setRiskOff(worldRiskOffLevel());` → [CapitalFlows.setRiskOff](map/CapitalFlows.md) (L707), [Game.worldRiskOffLevel](map/Game.md) (L2321)  
   _The risk-off the world is in (XR4): handed once here, it holds for the hot money's month below as well._
97. **L13963** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L542), [DebtManager.worldRate](map/DebtManager.md) (L901), [DebtManager.countryPremium](map/DebtManager.md) (L1227), [Bank.headroom](map/Bank.md) (L5570)
98. **L13969** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L605)
99. **L13970** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L613)
100. **L13971** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L630), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L608)
101. **L13975** `refreshBank();` → [Game.refreshBank](map/Game.md) (L5116)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
102. **L13985** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3431), [DebtManager.getPolicyRate](map/DebtManager.md) (L94)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
103. **L13998** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L317), [Bank.getPlacementIncome](map/Bank.md) (L3705)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
104. **L13999** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L325), [Bank.getFundingCost](map/Bank.md) (L3703)
105. **L14000** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L310), [Bank.getBranches](map/Bank.md) (L5221), [Bank.wholesaleFunding](map/Bank.md) (L3411)
106. **L14005** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
107. **L14006** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5411)
108. **L14010** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3865)  
   _The month is final, so the figure next month's tax is charged on is final too._
109. **L14014** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L395)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
110. **L14031** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L15004)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
111. **L14032** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L3234)
112. **L14042** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L4903), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3347)  
   _...AND THE SAVERS ARE PAID._
113. **L14043** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3348)
114. **L14050** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1521)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
115. **L14051** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L620), [EconomyManager.setSectorCash](map/EconomyManager.md) (L625), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1523)
116. **L14082** `payDividends();` → [Game.payDividends](map/Game.md) (L4948)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
117. **L14083** `tradeShares();` → [Game.tradeShares](map/Game.md) (L5020)
118. **L14093** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L5594)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
119. **L14103** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1222), [Exchange.cityMarketValue](map/Exchange.md) (L1861)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
120. **L14104** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L5227)
121. **L14109** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L452), [DebtManager.firmBookRatio](map/DebtManager.md) (L2985), [DebtManager.firmBookYield](map/DebtManager.md) (L2991)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
122. **L14110** `outward.takeMonth(bank.depositRate(), debtManager.worldRate(),` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L248), [Bank.depositRate](map/Bank.md) (L3340), [DebtManager.worldRate](map/DebtManager.md) (L901), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255)
123. **L14114** `householdBalance.investAbroad(bank.depositRate(), debtManager.worldRate(), foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3988), [Bank.depositRate](map/Bank.md) (L3340), [DebtManager.worldRate](map/DebtManager.md) (L901), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255)  
   _...and the households, by the same rule, with what the owners were just paid._
124. **L14117** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L4324), [DebtManager.householdBookYield](map/DebtManager.md) (L3014), [Bank.depositRate](map/Bank.md) (L3340)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
125. **L14135** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
126. **L14136** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5411)
127. **L14137** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L5517)
128. **L14172** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
129. **L14173** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L748)
130. **L14176** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L651), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1811)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
131. **L14177** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L380), [Bank.depositRate](map/Bank.md) (L3340), [DebtManager.getRate](map/DebtManager.md) (L1448), [DebtManager.worldRate](map/DebtManager.md) (L901), [DebtManager.countryPremium](map/DebtManager.md) (L1227), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2523), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L2735), [Game.yearlyDepreciation](map/Game.md) (L16740), [Bank.isInsolvent](map/Bank.md) (L1808), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1386)
132. **L14199** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
133. **L14200** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1684)
134. **L14201** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1698)
135. **L14202** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1672), [CapitalFlows.getStock](map/CapitalFlows.md) (L634)
136. **L14204** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L18550)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
137. **L14206** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L796)
138. **L14207** `lastHouseholdsAudit = MoneyAudit.households(this, householdsBefore, lastMoneyAudit);` → [MoneyAudit.households](map/MoneyAudit.md) (L453)
139. **L14210** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
140. **L14220** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L2586), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2523)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
141. **L14252** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L246), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L315), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L362)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
142. **L14271** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L375), [Game.realRateDifferential](map/Game.md) (L12612)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
143. **L14277** `handTheWorldToTheD();` → [Game.handTheWorldToTheD](map/Game.md) (L2442)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
144. **L14278** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255)
145. **L14279** `boolean onGoldBeforeReprice = foreign.getRegime() == ForeignAccounts.Regime.GOLD \|\| foreign.isCanadaGold();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1697), [ForeignAccounts.isCanadaGold](map/ForeignAccounts.md) (L1524)
146. **L14280** `foreign.setMoneyBase(centralBank.m0());   // M0 is the gold cover's base, handed right before the reprice (GD3)` → [ForeignAccounts.setMoneyBase](map/ForeignAccounts.md) (L1600), [CentralBank.m0](map/CentralBank.md) (L802)
147. **L14281** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L913)  
   _M0 is the gold cover's base, handed right before the reprice (GD3)_
148. **L14289** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1813), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1816), [Game.getCurrency](map/Game.md) (L2905), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255), [DebtManager.dropGoldAim](map/DebtManager.md) (L278), [Game.foundedUnfitted](map/Game.md) (L1274), [DebtManager.hearMoneyMonth](map/DebtManager.md) (L81), [ForeignAccounts.isCanadaGold](map/ForeignAccounts.md) (L1524)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
149. **L14300** `noteTheWorldMoneys(rateBeforeReprice);` → [Game.noteTheWorldMoneys](map/Game.md) (L2449)  
   _...AND THE WORLD'S NEWS (WN4): money that ran to safety abroad, a world peg that broke._
150. **L14308** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L685), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L869)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
151. **L14326** `economyManager.getNationalAccounts().setDebtExchangeGain(debtManager.revalueForeign(foreign.getRate()));` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [DebtManager.revalueForeign](map/DebtManager.md) (L985), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255)  
   _CD7: the same press books the exchange gain (loss) on the dollar paper - each piece's carrying value in dollars times the move from the rate it was last valued at - on the month the strike already ..._
152. **L14327** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L2689), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L1006), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255)
153. **L14334** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L3085)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
154. **L14335** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L1135), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L2562), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L2543)
155. **L14336** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1379)
156. **L14337** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L12586)
157. **L14350** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L16518), [Game.indexSpends](map/Game.md) (L16546), [Game.indexFees](map/Game.md) (L16561), [Game.indexFeeSpends](map/Game.md) (L16577)  
   _...AND WHAT THE MONTH COST A FAMILY._
158. **L14362** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L326)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
159. **L14363** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L94), [DebtManager.neutralRate](map/DebtManager.md) (L583), [DebtManager.holdingRate](map/DebtManager.md) (L537), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
160. **L14366** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L16639)
161. **L14369** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255)  
   _A year of the rate, so next year can tell a drift from a run._
162. **L14370** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
163. **L14372** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L19384)
164. **L14374** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L960)
165. **L14408** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
166. **L14409** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L614)
167. **L14428** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L19642)
168. **L14430** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L3811)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
169. **L14432** `tallyThePartners();` → [Game.tallyThePartners](map/Game.md) (L2006)  
   _The month's trade split to the world's nations (WN3; THE WORLD MARKET), after its last draw: before the history and the autosave._
170. **L14433** `recordMonth();` → [Game.recordMonth](map/Game.md) (L17060)
171. **L14435** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
172. **L14437** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L2883)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
173. **L14443** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L3166)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._
174. **L14464** `reportSaves();` → [Game.reportSaves](map/Game.md) (L17235)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
175. **L14465** `SaveWriter.Job autosaved = null;`
176. **L14466** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosaveJob](map/Game.md) (L17157)
177. **L14475** `recordForReplay(autosaved);` → [Game.recordForReplay](map/Game.md) (L17900)  
   _A recorded city keeps this whole month, beside the autosave (TR1; THE RECORDING), from the autosave's fill when it made one (TR2)._

## Game.startOfMonthUpdate() - Game.java lines 14478-14796 (319 lines)

1. **L14530** `ensureWorldMarket();` → [Game.ensureWorldMarket](map/Game.md) (L1918)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L14531** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L270)
3. **L14532** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L309), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L315)
4. **L14534** `strikeTheWorldMarket();` → [Game.strikeTheWorldMarket](map/Game.md) (L1971)  
   _...AND THE WORLD'S BAND BEHIND IT (WN3): the January solve when the year has turned, then every good's floor and ceiling from last month's trade; before any market clears._
5. **L14536** `double gold = goldUsdPerOunce(WorldMoneys.monthIndex(month));` → [Game.goldUsdPerOunce](map/Game.md) (L2295), [WorldMoneys.monthIndex](map/WorldMoneys.md) (L270)  
   _Bullion's nominal US$ path and spot exchange already include the dollar's price path (GD4)._
6. **L14537** `if (Double.isFinite(gold) && gold > 0) {` → [EconomyManager.getMarkets](map/EconomyManager.md) (L51), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255)
7. **L14556** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L478), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
8. **L14563** `labourMarket.followAverageWage(populationManager.getAverageWage());` → [LabourMarket.followAverageWage](map/LabourMarket.md) (L985), [PopulationManager.getAverageWage](map/PopulationManager.md) (L169)  
   _The floor's tie to the average wage (WG1) is struck once a month from last month's wages, before repriceLabour() walks them._
9. **L14583** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L753), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
10. **L14585** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L337), [LandManager.getPricePerSqFt](map/LandManager.md) (L634)
11. **L14586** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L10772)
12. **L14596** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L15004)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
13. **L14601** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5221), [Bank.isInsolvent](map/Bank.md) (L1808)  
   _A failed bank lends nothing._
14. **L14614** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L5227)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
15. **L14615** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4970)
16. **L14616** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4998)
17. **L14621** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4653)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
18. **L14622** `economyManager.getBusinessDebtManager().setProjectBudget(bank);` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
19. **L14623** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L628)
20. **L14630** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L5267)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
21. **L14631** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L643), [Bank.prime](map/Bank.md) (L1137), [DebtManager.getPolicyRate](map/DebtManager.md) (L94), [Bank.insuredMortgageRate](map/Bank.md) (L1158)
22. **L14635** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L1040)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
23. **L14640** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L813)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
24. **L14644** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L8460)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
25. **L14648** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L8409)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
26. **L14651** `outward.payCoupons(debtManager.worldRate(), foreign.getRate(), economyManager);` → [OutwardInvestment.payCoupons](map/OutwardInvestment.md) (L218), [DebtManager.worldRate](map/DebtManager.md) (L901), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1255)  
   _...and the coupon on the dollars its businesses hold abroad, struck as a line of their profit and kept abroad: the profit tax and the dividend see it, the till pays them (HD1, 2026-10-29; HD2, 2026..._
27. **L14665** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L3161)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
28. **L14666** `double constructionWorkDone = construction.getRecognisedThisMonth();`
29. **L14668** `if (construction.getFittingThisMonth() != 0) constructionWorkDone += construction.getFittingThisMonth();`  
   _...and the equipment the builders were paid to fit to standing buildings since the last strike banked its ledger (R1b): capital too, on the builders' side, once._
30. **L14677** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1227)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
31. **L14679** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L3166), [Game.getSectors](map/Game.md) (L3161)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
32. **L14681** `settleReserve();` → [Game.settleReserve](map/Game.md) (L6358)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
33. **L14683** `settlePublicLicences();` → [Game.settlePublicLicences](map/Game.md) (L6375)  
   _...and the licences the city's own patented works accrued last month (R2c-P), paid abroad in the same strike._
34. **L14687** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L3161)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
35. **L14693** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
36. **L14694** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2170), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L309), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L844), [EconomyManager.getResearchBill](map/EconomyManager.md) (L2043), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2084), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L636), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L638), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L824)
37. **L14752** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
38. **L14753** `monthlyMaterialImportBill = 0;`
39. **L14758** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L4188)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
40. **L14766** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L248)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
41. **L14774** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
42. **L14783** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
43. **L14787** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1456)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
44. **L14793** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L3852)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
45. **L14795** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L14797)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-157 (126 lines)

1. **L33** `game.moneyHearsTheMonth();` → [Game.moneyHearsTheMonth](map/Game.md) (L1195)
2. **L68** `int siteOutput = game.workTheFittings();` → [Game.workTheFittings](map/Game.md) (L1659)  
   _...less the fittings the crews work first (R1b; Game.workTheFittings()): after the repairs, before the sites, struck here once and used for the advance and for the work the sector is paid for below._
3. **L71** `Game.ForeignOffer foreign = game.foreignCrewsOffer();` → [Game.foreignCrewsOffer](map/Game.md) (L8361)  
   _The foreign contractor's month beside the city's own (FC1): nothing unless the repairs took the builders' points and left the sites none - see Game, THE FOREIGN CREWS._
4. **L73** `java.util.List<BuildingManager.Completion> opened = buildingManager.advanceConstruction(siteOutput + foreign.points()...` → [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5558)  
   _His points are in the month the sites take, and not on a rushed site's overtime (FC1b): his crews are not the city's._
5. **L74** `game.recordCompletions(opened);` → [Game.recordCompletions](map/Game.md) (L16157)
6. **L77** `game.fitWhatFinished(opened);` → [Game.fitWhatFinished](map/Game.md) (L1341)  
   _...and what the buildings that opened come with (R1b): in a city founded the 1900 way, only the equipment that pays for a new building; nothing in a city founded the default way, where every buildi..._
7. **L93** `game.getRecipes().apply(game);   // the ways the buildings run, at the counts the month leaves (R1c): first, the stea...` → [Game.getRecipes](map/Game.md) (L1258)  
   _...AND THE 1900 FORMS AT REST (R1b; R1's stranger's review, 2026-11-05): the shares the counts set, said again now that the month has changed the counts' weights._
8. **L94** `game.getEquipmentForms().apply(game);` → [Game.getEquipmentForms](map/Game.md) (L1264)  
   _the ways the buildings run, at the counts the month leaves (R1c): first, the steam form below weighs each building by its electricity_
9. **L102** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L9529), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5644)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
10. **L106** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5696)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
11. **L107** `double ownMonth = overtime == 0 ? siteOutput : siteOutput + overtime;`
12. **L109** `game.settleForeignCrews(foreign, buildingManager.getPointsBuilt() - Math.max(0, ownMonth));` → [Game.settleForeignCrews](map/Game.md) (L8382), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5660)  
   _...and the contractor is paid for what the sites took past the city's own month (FC1)._
13. **L110** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L9637), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6995), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5660)
14. **L116** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L9577), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L7004)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
15. **L121** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L10276), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5689)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
16. **L130** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L204)  
   _Roads, before anything reads them._
17. **L137** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L159)  
   _Recount the posts and refresh the wage arrays._
18. **L153** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L15428)  
   _THE POPULATION STEP._
19. **L155** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L173)
20. **L156** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L237)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 159-171 (13 lines)

1. **L164** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L8254)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L165** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L15291)
3. **L166** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L15304)
4. **L169** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L16965)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L170** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L370)

## Game.advanceDemographics() - Game.java lines 15428-16019 (592 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L15433** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L701), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L274), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L15447** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L15448** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L16050)
4. **L15449** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L16050)
5. **L15450** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L16050)
6. **L15451** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7882)
7. **L15453** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
8. **L15467** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L15468** `java.util.Arrays.fill(affordable, 1);`
10. **L15469** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L16069)
11. **L15472** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L15473** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L15474** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L15477** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L646)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L15489** `MedicalRows medicalRows = medicalRows();` → [Game.medicalRows](map/Game.md) (L1534)  
   _...AND THE MEDICINE THE CITY KNOWS (R2f, research-design Q17)._
16. **L15490** `sickness.setMedicine(medicalRows, month, generalCoverage);` → [Sickness.setMedicine](map/Sickness.md) (L210)
17. **L15493** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
18. **L15494** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7882)
19. **L15508** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
20. **L15521** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L406)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
21. **L15532** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L345)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
22. **L15533** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L152)
23. **L15534** `double[] orphansByBand = new double[AgeBand.values().length];`
24. **L15535** `double[] inBand = new double[AgeBand.values().length];`
25. **L15536** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L262), [PopulationCohorts.get](map/PopulationCohorts.md) (L133)
26. **L15540** `double[] careFactors = mortalityFactors;`
27. **L15541** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L473), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
28. **L15551** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L385), [Sickness.seed](map/Sickness.md) (L311), [Health.getSickRate](map/Health.md) (L334), [Health.getSickRateWithoutInfluenza](map/Health.md) (L337)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
29. **L15555** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L245)
30. **L15562** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
31. **L15563** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L133)
32. **L15564** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
33. **L15584** `double[][] causeRates = causesOfDeath.rates(month, inBand, mortalityFactors,` → [CausesOfDeath.rates](map/CausesOfDeath.md) (L558), [Game.getWaterRatio](map/Game.md) (L3152), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L4850), [Health.getOutbreakSeverity](map/Health.md) (L364), [PopulationCohorts.getLastBirths](map/PopulationCohorts.md) (L134)  
   _...AND WHAT THEY DIE OF (R0b)._
34. **L15587** `double[] causeTotals = CausesOfDeath.bandTotals(causeRates);` → [CausesOfDeath.bandTotals](map/CausesOfDeath.md) (L492)
35. **L15589** `for (AgeBand b : AgeBand.values()) {` → [CausesOfDeath.isHeldOff](map/CausesOfDeath.md) (L533), [CausesOfDeath.keptFactor](map/CausesOfDeath.md) (L469)  
   _...held off for a harness fixture (R0b call 7): no cause, and the swing whole._
36. **L15594** `cohorts.advanceMonth(mortalityFactors, illness, violence, causeTotals,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L242), [Healthcare.birthFactor](map/Healthcare.md) (L425)
37. **L15596** `causesOfDeath.book(month, causeRates, cohorts.getCauseDeaths());` → [CausesOfDeath.book](map/CausesOfDeath.md) (L635), [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L154)
38. **L15597** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L417), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L140)
39. **L15601** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
40. **L15602** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L143)
41. **L15603** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L140)
42. **L15604** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L145)
43. **L15605** `double[] causedDead = cohorts.getCauseDeaths();` → [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L154)
44. **L15606** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b] + causedDead[b];`
45. **L15607** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L509), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
46. **L15610** `lastOrphanDeaths = outsideDead[0];`
47. **L15611** `lastUnhousedDeaths = outsideDead[1];`
48. **L15618** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L512), [Unemployment.takeEvicted](map/Unemployment.md) (L215)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
49. **L15621** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
50. **L15635** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L133)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
51. **L15642** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L1048), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L4719), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L458)  
   _The families the bank discharged last month are leaving._
52. **L15651** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L325), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L479)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
53. **L15655** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L366), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
54. **L15660** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L928), [BuildingManager.homesBySize](map/BuildingManager.md) (L7360)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
55. **L15664** `migration.setYearMix(foundedUnfitted() ? ArrivalMix.at(month) : null);` → [Migration.setYearMix](map/Migration.md) (L1273), [Game.foundedUnfitted](map/Game.md) (L1274), [ArrivalMix.at](map/ArrivalMix.md) (L145)  
   _...and, in a city founded the 1900 way, what the world's movers hold this year (R0d; ArrivalMix): the skills the month's arrivals are drawn from._
56. **L15666** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L478), [Migration.monthlyNet](map/Migration.md) (L941), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L15295), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L7339), [PopulationCohorts.share](map/PopulationCohorts.md) (L169)
57. **L15681** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
58. **L15693** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L16999)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
59. **L15695** `double[] jobsByTier = new double[PayTier.values().length];`
60. **L15696** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)
61. **L15697** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1193)
62. **L15699** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
63. **L15711** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
64. **L15712** `double[] wageByTier = new double[PayTier.values().length];`
65. **L15713** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L274)
66. **L15714** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
67. **L15717** `for (int t = 0; t < wageByTier.length; t++) {`
68. **L15733** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7958)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
69. **L15734** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L943)
70. **L15735** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L193)
71. **L15736** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L193), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7958), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161), [Game.unskilledWage](map/Game.md) (L16339)
72. **L15744** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L241), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
73. **L15746** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L857), [Crime.prisoners](map/Crime.md) (L370)
74. **L15748** `populationManager.setOwners(2.0 * householdBalance.ownerFamilies());` → [PopulationManager.setOwners](map/PopulationManager.md) (L862), [HouseholdBalance.ownerFamilies](map/HouseholdBalance.md) (L857)  
   _The owner families take no job (BO1): two adults a company with a family._
75. **L15749** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
76. **L15751** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1166)
77. **L15752** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L868)
78. **L15753** `unemployment.setRetiringShare(cohorts.promotedShare(AgeBand.ADULT));   // HS2` → [Unemployment.setRetiringShare](map/Unemployment.md) (L287), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161)
79. **L15754** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L303), [Game.unskilledWage](map/Game.md) (L16339), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _HS2_
80. **L15758** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L282), [Migration.getLastArrivals](map/Migration.md) (L590), [PopulationCohorts.share](map/PopulationCohorts.md) (L169)  
   _The month's adult arrivals look for work next month._
81. **L15769** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying,` → [FamilyModel.rebuild](map/FamilyModel.md) (L477), [Crime.prisoners](map/Crime.md) (L370), [HouseholdBalance.ownerFamilies](map/HouseholdBalance.md) (L857)  
   _...and the owner families: two adults a company with a family, outside the families too (BO1)._
82. **L15771** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L277), [Unemployment.getHoused](map/Unemployment.md) (L155)
83. **L15775** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L4656), [Education.getFinished](map/Education.md) (L903)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
84. **L15778** `householdBalance.setFlows(cohorts);` → [HouseholdBalance.setFlows](map/HouseholdBalance.md) (L1973)  
   _...and the month's flows, the pyramid's own (HS1): who retired, died and left carries their money at this month's census, before the pool._
85. **L15790** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L7360)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
86. **L15791** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1527)
87. **L15792** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1696)
88. **L15793** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1782), [FamilyModel.house](map/FamilyModel.md) (L1527)
89. **L15798** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L3161), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L1386), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L1387)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
90. **L15802** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1457)  
   _And the advisor prices a new home on who would move into it._
91. **L15803** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1462)
92. **L15819** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L2029), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L915)  
   _...and the ones who cannot afford one either._
93. **L15821** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L2071), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L1022), [Unemployment.getHoused](map/Unemployment.md) (L155), [FamilyModel.getSeekers](map/FamilyModel.md) (L286)  
   _...and the people outside the families, with their own kind._
94. **L15860** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L161), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
95. **L15861** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L483), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8034), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8123), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L135), [Game.burialShare](map/Game.md) (L16131), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7845), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7882)
96. **L15871** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L2005), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Healthcare.getFees](map/Healthcare.md) (L732)
97. **L15886** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L4213), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
98. **L15887** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L403), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7921), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8034), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8123), [PopulationCohorts.promotedShare](map/PopulationCohorts.md) (L161), [Migration.getLastDepartures](map/Migration.md) (L597)
99. **L15906** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L878), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L8085), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L8098)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
100. **L15913** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1088), [Education.getGraduates](map/Education.md) (L781)
101. **L15914** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L1014), [Education.getLicences](map/Education.md) (L797)
102. **L15917** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L867), [Education.getStudying](map/Education.md) (L590)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
103. **L15920** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L1056)  
   _A licence holder is a graduate first, and the graduate count has just moved._
104. **L15922** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L2014), [Education.getGrossCost](map/Education.md) (L844), [Education.getFees](map/Education.md) (L852)
105. **L15931** `settleResearchBill(fill);` → [Game.settleResearchBill](map/Game.md) (L1480)  
   _...AND THE RESEARCH (R3): beside the schools because their staff are its faculty and its room, at the same wages and fill they were paid at._
106. **L15932** `workTheResearch(fill);` → [Game.workTheResearch](map/Game.md) (L1502)
107. **L15938** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8034), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8123)  
   _6c._
108. **L15942** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L2027), [Crime.getGrossCost](map/Crime.md) (L452)
109. **L15975** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L3171), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L2868), [Motoring.journeyFuel](map/Motoring.md) (L175)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
110. **L15977** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L260), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
111. **L15978** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L200), [Game.getInfrastructureManager](map/Game.md) (L3171)
112. **L15983** `if (homeFuel.isOn() \|\| homeFuel.hasSale()) homeFuel.strike(this, homeFuelHomes());` → [HomeFuel.isOn](map/HomeFuel.md) (L78), [HomeFuel.hasSale](map/HomeFuel.md) (L81), [HomeFuel.strike](map/HomeFuel.md) (L98), [Game.homeFuelHomes](map/Game.md) (L4717)  
   _...and the homes' lamp oil and coal (R0c, the home form; HomeFuel): the homes that light with a lamp and heat with a stove ask Retail for their month's JET and THERMAL_COAL, drawn and sold at the p..._
113. **L15987** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2080), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L8034), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8123), [Game.portsBill](map/Game.md) (L8431), [Game.getInfrastructureManager](map/Game.md) (L3171), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
114. **L15995** `economyManager.setTransitUpkeep(buildingManager.getUpkeepByCategory(BuildingType.INFRASTRUCTURE) + portsBill());` → [EconomyManager.setTransitUpkeep](map/EconomyManager.md) (L2090), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L8123), [Game.portsBill](map/Game.md) (L8431)  
   _...and the upkeep part of that bill, which the treasury pays Business Services (HL1; buyUpkeep()), the rest being the crews' wages._
115. **L16001** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L757), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L4850), [Game.unhousedShareOfCity](map/Game.md) (L16032), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
116. **L16017** `sickness.advanceMonth(health.getSickRate(), health.getSickRateWithoutInfluenza(),` → [Sickness.advanceMonth](map/Sickness.md) (L261), [Health.getSickRate](map/Health.md) (L334), [Health.getSickRateWithoutInfluenza](map/Health.md) (L337)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 173-235 (63 lines)

1. **L176** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L177** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L178** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L7404)
4. **L179** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L507), [Game.getFamilies](map/Game.md) (L15400)
5. **L185** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L504), [Game.getFamilies](map/Game.md) (L15400)  
   _The two inputs to the rent price, which is a market now._
6. **L188** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L508), [Game.getFamilies](map/Game.md) (L15400)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L192** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L505), [Game.marginalHousingCost](map/Game.md) (L20445)
8. **L194** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2115), [Game.getCohorts](map/Game.md) (L15399)
9. **L200** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L201** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L8211)
11. **L204** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L200)
12. **L211** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L248)  
   _The split behind that total._
13. **L212** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L317)
14. **L213** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L321)
15. **L228** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L3171)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L231** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L15403)  
   _The fourth ratio._
17. **L232** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1691)

## SimulationEngine.updateServices() - SimulationEngine.java lines 237-244 (8 lines)

1. **L238** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L283), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L232)
2. **L239** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L301), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1133)
3. **L242** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L358), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L243** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L86)

## Game.run() - Game.java lines 755-765 (11 lines)

1. **L756** `boolean starting = !initialized;`
2. **L757** `if (starting) {` → [Game.layTheLadder](map/Game.md) (L1301), [Game.basketHearsTheYear](map/Game.md) (L16674)
3. **L761** `initialize();` → [Game.initialize](map/Game.md) (L767)
4. **L762** `foundingBank();` → [Game.foundingBank](map/Game.md) (L910)
5. **L763** `foundingYard();` → [Game.foundingYard](map/Game.md) (L996)
6. **L764** `if (starting && founding.isUnfitted()) unfitTheFounding();` → [Founding.isUnfitted](map/Founding.md) (L420), [Game.unfitTheFounding](map/Game.md) (L1283)

