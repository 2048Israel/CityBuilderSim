# The order the month runs in

Generated 2026-11-05 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 12341-13263 (923 lines)

1. **L12349** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L12350** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L12351** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1090)
4. **L12352** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L12361** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L12362** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L15253)
7. **L12374** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L5023)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L12375** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L12116)
9. **L12377** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L13960)
10. **L12386** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L12387** `monthsSinceAutosave++;`
12. **L12394** `policyHearsTheMonth();` → [Game.policyHearsTheMonth](map/Game.md) (L1135)  
   _THE POLICY HEARS THE MONTH FIRST (R0): each lever opens on Canada's date, and a lever still shut is held at zero before anything in this month is taxed or paid._
13. **L12400** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L8831)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
14. **L12403** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2327)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
15. **L12404** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L618)
16. **L12407** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L367)  
   _The register's month: nothing offered, nothing paid, until it is._
17. **L12408** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L507)
18. **L12409** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1109)
19. **L12410** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1654)
20. **L12428** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
21. **L12456** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
22. **L12457** `cityDiscountForBank = cityDiscountThisMonth;`
23. **L12458** `cityDebtRaisedThisMonth = 0;`
24. **L12459** `cityDiscountThisMonth = 0;`
25. **L12460** `cityPrincipalRepaidThisMonth = 0;`
26. **L12461** `bankPrincipalRepaidThisMonth = 0;`
27. **L12464** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
28. **L12465** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
29. **L12466** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
30. **L12467** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L477)
31. **L12468** `foreignDebtRaisedThisMonth = 0;`
32. **L12469** `foreignPrincipalRepaidThisMonth = 0;`
33. **L12470** `foreignInterestPaidThisMonth = 0;`
34. **L12475** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
35. **L12476** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
36. **L12477** `sectorInvested.clear();`
37. **L12478** `salvageThisMonth.clear();`
38. **L12479** `salvageBySector.clear();`
39. **L12480** `salvageUsedThisMonth = 0;`
40. **L12481** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
41. **L12482** `double pooledBefore = 0;`
42. **L12483** `for (double p : poolsBefore) pooledBefore += p;`
43. **L12484** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2088)
44. **L12486** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1326)
45. **L12487** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L2207)
46. **L12488** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L252)
47. **L12499** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
48. **L12500** `buybackAbroad = buybackAbroadUnsettled;`
49. **L12501** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
50. **L12502** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L562)
51. **L12505** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L5350)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
52. **L12524** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1315)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
53. **L12525** `if (fxRegime == ForeignAccounts.Regime.PEG) {` → [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.tiedRate](map/DebtManager.md) (L199), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1345), [DebtManager.isAutopilot](map/DebtManager.md) (L116), [DebtManager.getAim](map/DebtManager.md) (L163), [DebtManager.currencyRate](map/DebtManager.md) (L188), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L193), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L426)
54. **L12538** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L13792)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
55. **L12545** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L18140)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
56. **L12553** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L17615)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
57. **L12558** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L17239)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
58. **L12577** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L18265), [Game.studentGrantBill](map/Game.md) (L15004)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
59. **L12591** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L18290)  
   _...AND EI, THE SAME WAY (0.7.3)._
60. **L12609** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1719), [Bank.chargeTax](map/Bank.md) (L4058), [Game.bankProfitTaxRate](map/Game.md) (L4044)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
61. **L12611** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L13265)
62. **L12612** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
63. **L12613** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L13864)
64. **L12614** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2454)
65. **L12615** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L13774)
66. **L12619** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
67. **L12620** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L17012)
68. **L12621** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
69. **L12622** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1501)
70. **L12626** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L13812)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
71. **L12637** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
72. **L12639** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
73. **L12640** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L3068)
74. **L12644** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3226)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
75. **L12645** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L3062)
76. **L12661** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2812), [DebtManager.getAccretedForBank](map/DebtManager.md) (L2890)  
   _THE DISCOUNT ACCRETES (0.7.1)._
77. **L12662** `legacyDiscountDue = 0;`
78. **L12666** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
79. **L12667** `cityDiscountForBank = 0;`
80. **L12669** `double sectorInterestPaid = 0;`
81. **L12670** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L2534)
82. **L12676** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2793), [BondMarket.getCouponsStruck](map/BondMarket.md) (L1003)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
83. **L12677** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L569), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1010)
84. **L12679** `refreshBank();` → [Game.refreshBank](map/Game.md) (L4315)
85. **L12682** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L4483)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
86. **L12705** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3119), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1198), [BuildingManager.countByName](map/BuildingManager.md) (L6661), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1205)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
87. **L12708** `bankMaintenanceDue = 0;`
88. **L12716** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L4025), [Bank.openBranches](map/Bank.md) (L2731), [BuildingManager.countByName](map/BuildingManager.md) (L6661)  
   _Capital for whatever branches opened this month._
89. **L12761** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1191), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
90. **L12763** `hotMoney.setRiskOff(worldRiskOffLevel());` → [CapitalFlows.setRiskOff](map/CapitalFlows.md) (L707), [Game.worldRiskOffLevel](map/Game.md) (L1705)  
   _The risk-off the world is in (XR4): handed once here, it holds for the hot money's month below as well._
91. **L12764** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L542), [DebtManager.worldRate](map/DebtManager.md) (L815), [DebtManager.countryPremium](map/DebtManager.md) (L1141), [Bank.headroom](map/Bank.md) (L5539)
92. **L12770** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L605)
93. **L12771** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L613)
94. **L12772** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L630), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L608)
95. **L12776** `refreshBank();` → [Game.refreshBank](map/Game.md) (L4315)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
96. **L12786** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3424), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
97. **L12799** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L310), [Bank.getPlacementIncome](map/Bank.md) (L3698)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
98. **L12800** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L318), [Bank.getFundingCost](map/Bank.md) (L3696)
99. **L12801** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L303), [Bank.getBranches](map/Bank.md) (L5214), [Bank.wholesaleFunding](map/Bank.md) (L3404)
100. **L12806** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
101. **L12807** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L4610)
102. **L12811** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3858)  
   _The month is final, so the figure next month's tax is charged on is final too._
103. **L12815** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L388)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
104. **L12832** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L13760)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
105. **L12833** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L3080)
106. **L12843** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3904), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3340)  
   _...AND THE SAVERS ARE PAID._
107. **L12844** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3341)
108. **L12851** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1514)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
109. **L12852** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L620), [EconomyManager.setSectorCash](map/EconomyManager.md) (L625), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1516)
110. **L12883** `payDividends();` → [Game.payDividends](map/Game.md) (L4153)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
111. **L12884** `tradeShares();` → [Game.tradeShares](map/Game.md) (L4225)
112. **L12894** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L4784)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
113. **L12904** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1140), [Exchange.cityMarketValue](map/Exchange.md) (L1713)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
114. **L12905** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L4426)
115. **L12910** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L452), [DebtManager.firmBookRatio](map/DebtManager.md) (L2831), [DebtManager.firmBookYield](map/DebtManager.md) (L2837)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
116. **L12911** `outward.takeMonth(bank.depositRate(), debtManager.worldRate(),` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L248), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
117. **L12915** `householdBalance.investAbroad(bank.depositRate(), debtManager.worldRate(), foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3050), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...and the households, by the same rule, with what the owners were just paid._
118. **L12918** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3386), [DebtManager.householdBookYield](map/DebtManager.md) (L2860), [Bank.depositRate](map/Bank.md) (L3333)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
119. **L12936** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
120. **L12937** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L4610)
121. **L12938** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L4705)
122. **L12973** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
123. **L12974** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L748)
124. **L12977** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L651), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1356)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
125. **L12978** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L380), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.getRate](map/DebtManager.md) (L1362), [DebtManager.worldRate](map/DebtManager.md) (L815), [DebtManager.countryPremium](map/DebtManager.md) (L1141), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2448), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L2147), [Game.yearlyDepreciation](map/Game.md) (L15358), [Bank.isInsolvent](map/Bank.md) (L1808), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1300)
126. **L13000** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
127. **L13001** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1684)
128. **L13002** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1698)
129. **L13003** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1672), [CapitalFlows.getStock](map/CapitalFlows.md) (L634)
130. **L13005** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L16985)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
131. **L13007** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L497)
132. **L13010** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
133. **L13020** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L1996), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2448)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
134. **L13052** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L244), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L298), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L345)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
135. **L13071** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L373), [Game.realRateDifferential](map/Game.md) (L11443)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
136. **L13077** `handTheWorldToTheD();` → [Game.handTheWorldToTheD](map/Game.md) (L1826)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
137. **L13078** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
138. **L13079** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L891)
139. **L13086** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1358), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1361), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
140. **L13093** `noteTheWorldMoneys(rateBeforeReprice);` → [Game.noteTheWorldMoneys](map/Game.md) (L1833)  
   _...AND THE WORLD'S NEWS (WN4): money that ran to safety abroad, a world peg that broke._
141. **L13101** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L668), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L847)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
142. **L13119** `economyManager.getNationalAccounts().setDebtExchangeGain(debtManager.revalueForeign(foreign.getRate()));` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [DebtManager.revalueForeign](map/DebtManager.md) (L899), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _CD7: the same press books the exchange gain (loss) on the dollar paper - each piece's carrying value in dollars times the move from the rate it was last valued at - on the month the strike already ..._
143. **L13120** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L2094), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L920), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
144. **L13127** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L2486)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
145. **L13128** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L1049), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L1972), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L1953)
146. **L13129** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1293)
147. **L13130** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L11417)
148. **L13143** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L15157), [Game.indexSpends](map/Game.md) (L15185), [Game.indexFees](map/Game.md) (L15200), [Game.indexFeeSpends](map/Game.md) (L15216)  
   _...AND WHAT THE MONTH COST A FAMILY._
149. **L13155** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L246)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
150. **L13156** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L501), [DebtManager.holdingRate](map/DebtManager.md) (L455), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
151. **L13159** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L15278)
152. **L13162** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _A year of the rate, so next year can tell a drift from a run._
153. **L13163** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
154. **L13165** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L17812)
155. **L13167** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L929)
156. **L13201** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
157. **L13202** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
158. **L13221** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L18066)
159. **L13223** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L3185)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
160. **L13225** `tallyThePartners();` → [Game.tallyThePartners](map/Game.md) (L1455)  
   _The month's trade split to the world's nations (WN3; THE WORLD MARKET), after its last draw: before the history and the autosave._
161. **L13226** `recordMonth();` → [Game.recordMonth](map/Game.md) (L15678)
162. **L13228** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
163. **L13230** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L2262)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
164. **L13250** `reportSaves();` → [Game.reportSaves](map/Game.md) (L15830)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
165. **L13251** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L15760)
166. **L13260** `recordForReplay();` → [Game.recordForReplay](map/Game.md) (L16384)  
   _A recorded city keeps this whole month, beside the autosave (TR1; THE RECORDING)._
167. **L13262** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L2539)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._

## Game.startOfMonthUpdate() - Game.java lines 13265-13563 (299 lines)

1. **L13317** `ensureWorldMarket();` → [Game.ensureWorldMarket](map/Game.md) (L1372)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L13318** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L260)
3. **L13319** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L309), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L298)
4. **L13321** `strikeTheWorldMarket();` → [Game.strikeTheWorldMarket](map/Game.md) (L1423)  
   _...AND THE WORLD'S BAND BEHIND IT (WN3): the January solve when the year has turned, then every good's floor and ceiling from last month's trade; before any market clears._
5. **L13335** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L381), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
6. **L13359** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L749), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
7. **L13361** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L337), [LandManager.getPricePerSqFt](map/LandManager.md) (L630)
8. **L13362** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L9612)
9. **L13372** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L13760)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
10. **L13377** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5214), [Bank.isInsolvent](map/Bank.md) (L1808)  
   _A failed bank lends nothing._
11. **L13390** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L4426)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
12. **L13391** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4963)
13. **L13392** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4991)
14. **L13397** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4646)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
15. **L13398** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L591)
16. **L13405** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L4466)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
17. **L13406** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L643), [Bank.prime](map/Bank.md) (L1137), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1158)
18. **L13410** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L965)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
19. **L13415** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L812)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
20. **L13419** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L7395)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
21. **L13423** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L7344)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
22. **L13426** `outward.payCoupons(debtManager.worldRate(), foreign.getRate(), economyManager);` → [OutwardInvestment.payCoupons](map/OutwardInvestment.md) (L218), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...and the coupon on the dollars its businesses hold abroad, struck as a line of their profit and kept abroad: the profit tax and the dividend see it, the till pays them (HD1, 2026-10-29; HD2, 2026..._
23. **L13440** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L2534)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
24. **L13441** `double constructionWorkDone = construction.getRecognisedThisMonth();`
25. **L13450** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1226)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
26. **L13452** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L2539), [Game.getSectors](map/Game.md) (L2534)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
27. **L13454** `settleReserve();` → [Game.settleReserve](map/Game.md) (L5445)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
28. **L13458** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L2534)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
29. **L13464** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
30. **L13465** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2112), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L292), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L844), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2051), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L632), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L634), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L823)
31. **L13519** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
32. **L13520** `monthlyMaterialImportBill = 0;`
33. **L13525** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L3406)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
34. **L13533** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L248)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
35. **L13541** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
36. **L13550** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
37. **L13554** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1455)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
38. **L13560** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L3226)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
39. **L13562** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L13564)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-134 (103 lines)

1. **L66** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L7267)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L69** `Game.ForeignOffer foreign = game.foreignCrewsOffer();` → [Game.foreignCrewsOffer](map/Game.md) (L7296)  
   _The foreign contractor's month beside the city's own (FC1): nothing unless the repairs took the builders' points and left the sites none - see Game, THE FOREIGN CREWS._
3. **L71** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput + foreign.points(), foreign.points()));` → [Game.recordCompletions](map/Game.md) (L14806), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5322)  
   _His points are in the month the sites take, and not on a rushed site's overtime (FC1b): his crews are not the city's._
4. **L79** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L8410), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5407)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
5. **L83** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5459)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
6. **L84** `double ownMonth = overtime == 0 ? siteOutput : siteOutput + overtime;`
7. **L86** `game.settleForeignCrews(foreign, buildingManager.getPointsBuilt() - Math.max(0, ownMonth));` → [Game.settleForeignCrews](map/Game.md) (L7317), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5423)  
   _...and the contractor is paid for what the sites took past the city's own month (FC1)._
8. **L87** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L8518), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6624), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5423)
9. **L93** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L8458), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L6633)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
10. **L98** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L9125), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5452)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
11. **L107** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L187)  
   _Roads, before anything reads them._
12. **L114** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L136)  
   _Recount the posts and refresh the wage arrays._
13. **L130** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L14116)  
   _THE POPULATION STEP._
14. **L132** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L150)
15. **L133** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L214)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 136-148 (13 lines)

1. **L141** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L7192)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L142** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L13982)
3. **L143** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L13994)
4. **L146** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L15583)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L147** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L332)

## Game.advanceDemographics() - Game.java lines 14116-14668 (553 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L14121** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L561), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L14135** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L14136** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L14699)
4. **L14137** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L14699)
5. **L14138** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L14699)
6. **L14139** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7463)
7. **L14141** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
8. **L14155** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L14156** `java.util.Arrays.fill(affordable, 1);`
10. **L14157** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L14718)
11. **L14160** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L14161** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L14162** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L14165** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L646)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L14168** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L14169** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7463)
17. **L14183** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L14196** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L406)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L14207** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L14208** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L14209** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L14210** `double[] inBand = new double[AgeBand.values().length];`
23. **L14211** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L102)
24. **L14215** `double[] careFactors = mortalityFactors;`
25. **L14216** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L466), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
26. **L14226** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L316)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L14229** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L14236** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L14237** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L102)
30. **L14238** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
31. **L14258** `double[][] causeRates = causesOfDeath.rates(month, inBand, mortalityFactors,` → [CausesOfDeath.rates](map/CausesOfDeath.md) (L447), [Game.getWaterRatio](map/Game.md) (L2525), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3851), [Health.getOutbreakSeverity](map/Health.md) (L338), [PopulationCohorts.getLastBirths](map/PopulationCohorts.md) (L103)  
   _...AND WHAT THEY DIE OF (R0b)._
32. **L14261** `double[] causeTotals = CausesOfDeath.bandTotals(causeRates);` → [CausesOfDeath.bandTotals](map/CausesOfDeath.md) (L384)
33. **L14263** `for (AgeBand b : AgeBand.values()) {` → [CausesOfDeath.isHeldOff](map/CausesOfDeath.md) (L422), [CausesOfDeath.keptFactor](map/CausesOfDeath.md) (L361)  
   _...held off for a harness fixture (R0b call 7): no cause, and the swing whole._
34. **L14268** `cohorts.advanceMonth(mortalityFactors, illness, violence, causeTotals,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L177), [Healthcare.birthFactor](map/Healthcare.md) (L425)
35. **L14270** `causesOfDeath.book(month, causeRates, cohorts.getCauseDeaths());` → [CausesOfDeath.book](map/CausesOfDeath.md) (L510), [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L123)
36. **L14271** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L270), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L109)
37. **L14275** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
38. **L14276** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L112)
39. **L14277** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L109)
40. **L14278** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L114)
41. **L14279** `double[] causedDead = cohorts.getCauseDeaths();` → [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L123)
42. **L14280** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b] + causedDead[b];`
43. **L14281** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L502), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
44. **L14284** `lastOrphanDeaths = outsideDead[0];`
45. **L14285** `lastUnhousedDeaths = outsideDead[1];`
46. **L14292** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L404), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
47. **L14295** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
48. **L14309** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L102)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
49. **L14316** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L906), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3747), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
50. **L14325** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L302), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L479)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
51. **L14329** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L343), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
52. **L14334** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L788), [BuildingManager.homesBySize](map/BuildingManager.md) (L6978)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
53. **L14336** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L376), [Migration.monthlyNet](map/Migration.md) (L801), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L13986), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L6957), [PopulationCohorts.share](map/PopulationCohorts.md) (L135)
54. **L14351** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
55. **L14363** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L15618)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
56. **L14365** `double[] jobsByTier = new double[PayTier.values().length];`
57. **L14366** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
58. **L14367** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1118)
59. **L14369** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
60. **L14381** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
61. **L14382** `double[] wageByTier = new double[PayTier.values().length];`
62. **L14383** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239)
63. **L14384** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
64. **L14387** `for (int t = 0; t < wageByTier.length; t++) {`
65. **L14403** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7539)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
66. **L14404** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L737)
67. **L14405** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
68. **L14406** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L128), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7539), [Game.unskilledWage](map/Game.md) (L14978)
69. **L14413** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
70. **L14415** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L788), [Crime.prisoners](map/Crime.md) (L370)
71. **L14416** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
72. **L14418** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1092)
73. **L14419** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L794)
74. **L14420** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L14978), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
75. **L14424** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L456), [PopulationCohorts.share](map/PopulationCohorts.md) (L135)  
   _The month's adult arrivals look for work next month._
76. **L14434** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L370)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
77. **L14435** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
78. **L14439** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3711), [Education.getFinished](map/Education.md) (L903)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
79. **L14451** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L6978)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
80. **L14452** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
81. **L14453** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1297)
82. **L14454** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1384), [FamilyModel.house](map/FamilyModel.md) (L1128)
83. **L14459** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L2534), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
84. **L14463** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1190)  
   _And the advisor prices a new home on who would move into it._
85. **L14464** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1195)
86. **L14480** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1631), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L907)  
   _...and the ones who cannot afford one either._
87. **L14482** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L1014), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
88. **L14521** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L161), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
89. **L14522** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L483), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7615), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7704), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L104), [Game.burialShare](map/Game.md) (L14780), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7426), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7463)
90. **L14532** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1987), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Healthcare.getFees](map/Healthcare.md) (L732)
91. **L14547** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L3431), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
92. **L14548** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L403), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7502), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7615), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7704), [Migration.getLastDepartures](map/Migration.md) (L461)
93. **L14567** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L878), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L7666), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L7679)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
94. **L14574** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1014), [Education.getGraduates](map/Education.md) (L781)
95. **L14575** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L940), [Education.getLicences](map/Education.md) (L797)
96. **L14578** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L793), [Education.getStudying](map/Education.md) (L590)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
97. **L14581** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L982)  
   _A licence holder is a graduate first, and the graduate count has just moved._
98. **L14583** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1996), [Education.getGrossCost](map/Education.md) (L844), [Education.getFees](map/Education.md) (L852)
99. **L14589** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7615), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7704)  
   _6c._
100. **L14593** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L2009), [Crime.getGrossCost](map/Crime.md) (L452)
101. **L14626** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L2544), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L1930), [Motoring.journeyFuel](map/Motoring.md) (L175)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
102. **L14628** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L243), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
103. **L14629** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L200), [Game.getInfrastructureManager](map/Game.md) (L2544)
104. **L14634** `if (homeFuel.isOn() \|\| homeFuel.hasSale()) homeFuel.strike(this, homeFuelHomes());` → [HomeFuel.isOn](map/HomeFuel.md) (L74), [HomeFuel.hasSale](map/HomeFuel.md) (L77), [HomeFuel.strike](map/HomeFuel.md) (L94), [Game.homeFuelHomes](map/Game.md) (L3922)  
   _...and the homes' lamp oil and coal (R0c, the home form; HomeFuel): the homes that light with a lamp and heat with a stove ask Retail for their month's JET and THERMAL_COAL, drawn and sold at the p..._
105. **L14638** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2047), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7615), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7704), [Game.portsBill](map/Game.md) (L7366), [Game.getInfrastructureManager](map/Game.md) (L2544), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
106. **L14650** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L757), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3851), [Game.unhousedShareOfCity](map/Game.md) (L14681), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
107. **L14666** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L316)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 150-212 (63 lines)

1. **L153** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L154** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L155** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L7022)
4. **L156** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L507), [Game.getFamilies](map/Game.md) (L14088)
5. **L162** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L504), [Game.getFamilies](map/Game.md) (L14088)  
   _The two inputs to the rent price, which is a market now._
6. **L165** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L508), [Game.getFamilies](map/Game.md) (L14088)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L169** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L505), [Game.marginalHousingCost](map/Game.md) (L18843)
8. **L171** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2068), [Game.getCohorts](map/Game.md) (L14087)
9. **L177** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L178** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L7792)
11. **L181** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L165)
12. **L188** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L213)  
   _The split behind that total._
13. **L189** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L300)
14. **L190** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L304)
15. **L205** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L2544)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L208** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L14091)  
   _The fourth ratio._
17. **L209** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1684)

## SimulationEngine.updateServices() - SimulationEngine.java lines 214-221 (8 lines)

1. **L215** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L266), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197)
2. **L216** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L284), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
3. **L219** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L341), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L220** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L67)

## Game.run() - Game.java lines 718-722 (5 lines)

1. **L719** `initialize();` → [Game.initialize](map/Game.md) (L724)
2. **L720** `foundingBank();` → [Game.foundingBank](map/Game.md) (L859)
3. **L721** `foundingYard();` → [Game.foundingYard](map/Game.md) (L946)

