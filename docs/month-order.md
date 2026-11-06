# The order the month runs in

Generated 2026-11-05 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 12607-13529 (923 lines)

1. **L12615** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L12616** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L12617** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1097)
4. **L12618** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L12627** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L12628** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L15521)
7. **L12640** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L5231)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L12641** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L12382)
9. **L12643** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L14228)
10. **L12652** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L12653** `monthsSinceAutosave++;`
12. **L12660** `policyHearsTheMonth();` → [Game.policyHearsTheMonth](map/Game.md) (L1144)  
   _THE POLICY HEARS THE MONTH FIRST (R0): each lever opens on Canada's date, and a lever still shut is held at zero before anything in this month is taxed or paid._
13. **L12666** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L9097)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
14. **L12669** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2327)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
15. **L12670** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L618)
16. **L12673** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L367)  
   _The register's month: nothing offered, nothing paid, until it is._
17. **L12674** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L507)
18. **L12675** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1109)
19. **L12676** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1654)
20. **L12694** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
21. **L12722** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
22. **L12723** `cityDiscountForBank = cityDiscountThisMonth;`
23. **L12724** `cityDebtRaisedThisMonth = 0;`
24. **L12725** `cityDiscountThisMonth = 0;`
25. **L12726** `cityPrincipalRepaidThisMonth = 0;`
26. **L12727** `bankPrincipalRepaidThisMonth = 0;`
27. **L12730** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
28. **L12731** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
29. **L12732** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
30. **L12733** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L477)
31. **L12734** `foreignDebtRaisedThisMonth = 0;`
32. **L12735** `foreignPrincipalRepaidThisMonth = 0;`
33. **L12736** `foreignInterestPaidThisMonth = 0;`
34. **L12741** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
35. **L12742** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
36. **L12743** `sectorInvested.clear();`
37. **L12744** `salvageThisMonth.clear();`
38. **L12745** `salvageBySector.clear();`
39. **L12746** `salvageUsedThisMonth = 0;`
40. **L12747** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
41. **L12748** `double pooledBefore = 0;`
42. **L12749** `for (double p : poolsBefore) pooledBefore += p;`
43. **L12750** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2088)
44. **L12752** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1326)
45. **L12753** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L2207)
46. **L12754** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L252)
47. **L12765** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
48. **L12766** `buybackAbroad = buybackAbroadUnsettled;`
49. **L12767** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
50. **L12768** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L562)
51. **L12771** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L5558)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
52. **L12790** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1315)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
53. **L12791** `if (fxRegime == ForeignAccounts.Regime.PEG) {` → [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.tiedRate](map/DebtManager.md) (L199), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1345), [DebtManager.isAutopilot](map/DebtManager.md) (L116), [DebtManager.getAim](map/DebtManager.md) (L163), [DebtManager.currencyRate](map/DebtManager.md) (L188), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L193), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L426)
54. **L12804** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L14060)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
55. **L12811** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L18410)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
56. **L12819** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L17885)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
57. **L12824** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L17509)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
58. **L12843** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L18535), [Game.studentGrantBill](map/Game.md) (L15272)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
59. **L12857** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L18560)  
   _...AND EI, THE SAME WAY (0.7.3)._
60. **L12875** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1719), [Bank.chargeTax](map/Bank.md) (L4058), [Game.bankProfitTaxRate](map/Game.md) (L4252)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
61. **L12877** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L13531)
62. **L12878** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
63. **L12879** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L14132)
64. **L12880** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2454)
65. **L12881** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L14042)
66. **L12885** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
67. **L12886** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L17282)
68. **L12887** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
69. **L12888** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1501)
70. **L12892** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L14080)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
71. **L12903** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
72. **L12905** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
73. **L12906** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L3068)
74. **L12910** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3226)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
75. **L12911** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L3062)
76. **L12927** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2812), [DebtManager.getAccretedForBank](map/DebtManager.md) (L2890)  
   _THE DISCOUNT ACCRETES (0.7.1)._
77. **L12928** `legacyDiscountDue = 0;`
78. **L12932** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
79. **L12933** `cityDiscountForBank = 0;`
80. **L12935** `double sectorInterestPaid = 0;`
81. **L12936** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L2731)
82. **L12942** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2793), [BondMarket.getCouponsStruck](map/BondMarket.md) (L1003)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
83. **L12943** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L569), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1010)
84. **L12945** `refreshBank();` → [Game.refreshBank](map/Game.md) (L4523)
85. **L12948** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L4691)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
86. **L12971** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3119), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1198), [BuildingManager.countByName](map/BuildingManager.md) (L6673), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1205)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
87. **L12974** `bankMaintenanceDue = 0;`
88. **L12982** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L4233), [Bank.openBranches](map/Bank.md) (L2731), [BuildingManager.countByName](map/BuildingManager.md) (L6673)  
   _Capital for whatever branches opened this month._
89. **L13027** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1191), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
90. **L13029** `hotMoney.setRiskOff(worldRiskOffLevel());` → [CapitalFlows.setRiskOff](map/CapitalFlows.md) (L707), [Game.worldRiskOffLevel](map/Game.md) (L1901)  
   _The risk-off the world is in (XR4): handed once here, it holds for the hot money's month below as well._
91. **L13030** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L542), [DebtManager.worldRate](map/DebtManager.md) (L815), [DebtManager.countryPremium](map/DebtManager.md) (L1141), [Bank.headroom](map/Bank.md) (L5539)
92. **L13036** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L605)
93. **L13037** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L613)
94. **L13038** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L630), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L608)
95. **L13042** `refreshBank();` → [Game.refreshBank](map/Game.md) (L4523)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
96. **L13052** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3424), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
97. **L13065** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L310), [Bank.getPlacementIncome](map/Bank.md) (L3698)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
98. **L13066** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L318), [Bank.getFundingCost](map/Bank.md) (L3696)
99. **L13067** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L303), [Bank.getBranches](map/Bank.md) (L5214), [Bank.wholesaleFunding](map/Bank.md) (L3404)
100. **L13072** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
101. **L13073** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L4818)
102. **L13077** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3858)  
   _The month is final, so the figure next month's tax is charged on is final too._
103. **L13081** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L388)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
104. **L13098** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L14028)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
105. **L13099** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L3080)
106. **L13109** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3904), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3340)  
   _...AND THE SAVERS ARE PAID._
107. **L13110** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3341)
108. **L13117** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1514)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
109. **L13118** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L620), [EconomyManager.setSectorCash](map/EconomyManager.md) (L625), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1516)
110. **L13149** `payDividends();` → [Game.payDividends](map/Game.md) (L4361)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
111. **L13150** `tradeShares();` → [Game.tradeShares](map/Game.md) (L4433)
112. **L13160** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L4992)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
113. **L13170** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1140), [Exchange.cityMarketValue](map/Exchange.md) (L1722)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
114. **L13171** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L4634)
115. **L13176** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L452), [DebtManager.firmBookRatio](map/DebtManager.md) (L2831), [DebtManager.firmBookYield](map/DebtManager.md) (L2837)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
116. **L13177** `outward.takeMonth(bank.depositRate(), debtManager.worldRate(),` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L248), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
117. **L13181** `householdBalance.investAbroad(bank.depositRate(), debtManager.worldRate(), foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3050), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...and the households, by the same rule, with what the owners were just paid._
118. **L13184** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3386), [DebtManager.householdBookYield](map/DebtManager.md) (L2860), [Bank.depositRate](map/Bank.md) (L3333)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
119. **L13202** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
120. **L13203** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L4818)
121. **L13204** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L4913)
122. **L13239** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
123. **L13240** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L748)
124. **L13243** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L651), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1356)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
125. **L13244** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L380), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.getRate](map/DebtManager.md) (L1362), [DebtManager.worldRate](map/DebtManager.md) (L815), [DebtManager.countryPremium](map/DebtManager.md) (L1141), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2448), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L2147), [Game.yearlyDepreciation](map/Game.md) (L15626), [Bank.isInsolvent](map/Bank.md) (L1808), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1300)
126. **L13266** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
127. **L13267** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1684)
128. **L13268** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1698)
129. **L13269** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1672), [CapitalFlows.getStock](map/CapitalFlows.md) (L634)
130. **L13271** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L17255)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
131. **L13273** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L497)
132. **L13276** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
133. **L13286** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L1996), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2448)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
134. **L13318** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L244), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L298), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L345)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
135. **L13337** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L373), [Game.realRateDifferential](map/Game.md) (L11709)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
136. **L13343** `handTheWorldToTheD();` → [Game.handTheWorldToTheD](map/Game.md) (L2022)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
137. **L13344** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
138. **L13345** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L891)
139. **L13352** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1358), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1361), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
140. **L13359** `noteTheWorldMoneys(rateBeforeReprice);` → [Game.noteTheWorldMoneys](map/Game.md) (L2029)  
   _...AND THE WORLD'S NEWS (WN4): money that ran to safety abroad, a world peg that broke._
141. **L13367** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L668), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L847)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
142. **L13385** `economyManager.getNationalAccounts().setDebtExchangeGain(debtManager.revalueForeign(foreign.getRate()));` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [DebtManager.revalueForeign](map/DebtManager.md) (L899), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _CD7: the same press books the exchange gain (loss) on the dollar paper - each piece's carrying value in dollars times the move from the rate it was last valued at - on the month the strike already ..._
143. **L13386** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L2094), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L920), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)
144. **L13393** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L2486)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
145. **L13394** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L1049), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L1972), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L1953)
146. **L13395** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1293)
147. **L13396** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L11683)
148. **L13409** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L15425), [Game.indexSpends](map/Game.md) (L15453), [Game.indexFees](map/Game.md) (L15468), [Game.indexFeeSpends](map/Game.md) (L15484)  
   _...AND WHAT THE MONTH COST A FAMILY._
149. **L13421** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L246)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
150. **L13422** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L501), [DebtManager.holdingRate](map/DebtManager.md) (L455), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
151. **L13425** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L15546)
152. **L13428** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _A year of the rate, so next year can tell a drift from a run._
153. **L13429** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
154. **L13431** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L18082)
155. **L13433** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L940)
156. **L13467** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
157. **L13468** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
158. **L13487** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L18336)
159. **L13489** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L3382)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
160. **L13491** `tallyThePartners();` → [Game.tallyThePartners](map/Game.md) (L1651)  
   _The month's trade split to the world's nations (WN3; THE WORLD MARKET), after its last draw: before the history and the autosave._
161. **L13492** `recordMonth();` → [Game.recordMonth](map/Game.md) (L15946)
162. **L13494** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
163. **L13496** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L2458)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
164. **L13516** `reportSaves();` → [Game.reportSaves](map/Game.md) (L16098)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
165. **L13517** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L16028)
166. **L13526** `recordForReplay();` → [Game.recordForReplay](map/Game.md) (L16654)  
   _A recorded city keeps this whole month, beside the autosave (TR1; THE RECORDING)._
167. **L13528** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L2736)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._

## Game.startOfMonthUpdate() - Game.java lines 13531-13831 (301 lines)

1. **L13583** `ensureWorldMarket();` → [Game.ensureWorldMarket](map/Game.md) (L1568)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L13584** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L260)
3. **L13585** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L309), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L298)
4. **L13587** `strikeTheWorldMarket();` → [Game.strikeTheWorldMarket](map/Game.md) (L1619)  
   _...AND THE WORLD'S BAND BEHIND IT (WN3): the January solve when the year has turned, then every good's floor and ceiling from last month's trade; before any market clears._
5. **L13601** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L381), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
6. **L13625** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L749), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
7. **L13627** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L337), [LandManager.getPricePerSqFt](map/LandManager.md) (L630)
8. **L13628** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L9878)
9. **L13638** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L14028)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
10. **L13643** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5214), [Bank.isInsolvent](map/Bank.md) (L1808)  
   _A failed bank lends nothing._
11. **L13656** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L4634)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
12. **L13657** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4963)
13. **L13658** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4991)
14. **L13663** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4646)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
15. **L13664** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L591)
16. **L13671** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L4674)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
17. **L13672** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L643), [Bank.prime](map/Bank.md) (L1137), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1158)
18. **L13676** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L965)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
19. **L13681** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L812)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
20. **L13685** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L7659)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
21. **L13689** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L7608)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
22. **L13692** `outward.payCoupons(debtManager.worldRate(), foreign.getRate(), economyManager);` → [OutwardInvestment.payCoupons](map/OutwardInvestment.md) (L218), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1136)  
   _...and the coupon on the dollars its businesses hold abroad, struck as a line of their profit and kept abroad: the profit tax and the dividend see it, the till pays them (HD1, 2026-10-29; HD2, 2026..._
23. **L13706** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L2731)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
24. **L13707** `double constructionWorkDone = construction.getRecognisedThisMonth();`
25. **L13709** `if (construction.getFittingThisMonth() != 0) constructionWorkDone += construction.getFittingThisMonth();`  
   _...and the equipment the builders were paid to fit to standing buildings since the last strike banked its ledger (R1b): capital too, on the builders' side, once._
26. **L13718** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1226)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
27. **L13720** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L2736), [Game.getSectors](map/Game.md) (L2731)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
28. **L13722** `settleReserve();` → [Game.settleReserve](map/Game.md) (L5653)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
29. **L13726** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L2731)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
30. **L13732** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
31. **L13733** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2112), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L292), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L844), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2051), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L632), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L634), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L823)
32. **L13787** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
33. **L13788** `monthlyMaterialImportBill = 0;`
34. **L13793** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L3614)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
35. **L13801** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L248)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
36. **L13809** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
37. **L13818** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
38. **L13822** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1455)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
39. **L13828** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L3423)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
40. **L13830** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L13832)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-155 (124 lines)

1. **L67** `int siteOutput = game.workTheFittings();` → [Game.workTheFittings](map/Game.md) (L1317)  
   _...less the fittings the crews work first (R1b; Game.workTheFittings()): after the repairs, before the sites, struck here once and used for the advance and for the work the sector is paid for below._
2. **L70** `Game.ForeignOffer foreign = game.foreignCrewsOffer();` → [Game.foreignCrewsOffer](map/Game.md) (L7560)  
   _The foreign contractor's month beside the city's own (FC1): nothing unless the repairs took the builders' points and left the sites none - see Game, THE FOREIGN CREWS._
3. **L72** `java.util.List<BuildingManager.Completion> opened = buildingManager.advanceConstruction(siteOutput + foreign.points()...` → [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5324)  
   _His points are in the month the sites take, and not on a rushed site's overtime (FC1b): his crews are not the city's._
4. **L73** `game.recordCompletions(opened);` → [Game.recordCompletions](map/Game.md) (L15074)
5. **L76** `game.fitWhatFinished(opened);` → [Game.fitWhatFinished](map/Game.md) (L1194)  
   _...and what the buildings that opened come with (R1b): in a city founded the 1900 way, only the equipment that pays for a new building; nothing in a city founded the default way, where every buildi..._
6. **L92** `game.getEquipmentForms().apply(game);` → [Game.getEquipmentForms](map/Game.md) (L1158)  
   _...AND THE 1900 FORMS AT REST (R1b; R1's stranger's review, 2026-11-05): the shares the counts set, said again now that the month has changed the counts' weights._
7. **L100** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L8676), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5409)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
8. **L104** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5461)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
9. **L105** `double ownMonth = overtime == 0 ? siteOutput : siteOutput + overtime;`
10. **L107** `game.settleForeignCrews(foreign, buildingManager.getPointsBuilt() - Math.max(0, ownMonth));` → [Game.settleForeignCrews](map/Game.md) (L7581), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5425)  
   _...and the contractor is paid for what the sites took past the city's own month (FC1)._
11. **L108** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L8784), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6636), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5425)
12. **L114** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L8724), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L6645)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
13. **L119** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L9391), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5454)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
14. **L128** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L187)  
   _Roads, before anything reads them._
15. **L135** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L157)  
   _Recount the posts and refresh the wage arrays._
16. **L151** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L14384)  
   _THE POPULATION STEP._
17. **L153** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L171)
18. **L154** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L235)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 157-169 (13 lines)

1. **L162** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L7453)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L163** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L14250)
3. **L164** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L14262)
4. **L167** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L15851)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L168** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L332)

## Game.advanceDemographics() - Game.java lines 14384-14936 (553 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L14389** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L561), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L14403** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L14404** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L14967)
4. **L14405** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L14967)
5. **L14406** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L14967)
6. **L14407** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7484)
7. **L14409** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
8. **L14423** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L14424** `java.util.Arrays.fill(affordable, 1);`
10. **L14425** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L14986)
11. **L14428** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L14429** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L14430** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L14433** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L646)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L14436** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L14437** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7484)
17. **L14451** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L14464** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L406)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L14475** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L14476** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L14477** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L14478** `double[] inBand = new double[AgeBand.values().length];`
23. **L14479** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L102)
24. **L14483** `double[] careFactors = mortalityFactors;`
25. **L14484** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L466), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
26. **L14494** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L316)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L14497** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L14504** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L14505** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L102)
30. **L14506** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
31. **L14526** `double[][] causeRates = causesOfDeath.rates(month, inBand, mortalityFactors,` → [CausesOfDeath.rates](map/CausesOfDeath.md) (L447), [Game.getWaterRatio](map/Game.md) (L2722), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3851), [Health.getOutbreakSeverity](map/Health.md) (L338), [PopulationCohorts.getLastBirths](map/PopulationCohorts.md) (L103)  
   _...AND WHAT THEY DIE OF (R0b)._
32. **L14529** `double[] causeTotals = CausesOfDeath.bandTotals(causeRates);` → [CausesOfDeath.bandTotals](map/CausesOfDeath.md) (L384)
33. **L14531** `for (AgeBand b : AgeBand.values()) {` → [CausesOfDeath.isHeldOff](map/CausesOfDeath.md) (L422), [CausesOfDeath.keptFactor](map/CausesOfDeath.md) (L361)  
   _...held off for a harness fixture (R0b call 7): no cause, and the swing whole._
34. **L14536** `cohorts.advanceMonth(mortalityFactors, illness, violence, causeTotals,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L177), [Healthcare.birthFactor](map/Healthcare.md) (L425)
35. **L14538** `causesOfDeath.book(month, causeRates, cohorts.getCauseDeaths());` → [CausesOfDeath.book](map/CausesOfDeath.md) (L510), [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L123)
36. **L14539** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L270), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L109)
37. **L14543** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
38. **L14544** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L112)
39. **L14545** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L109)
40. **L14546** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L114)
41. **L14547** `double[] causedDead = cohorts.getCauseDeaths();` → [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L123)
42. **L14548** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b] + causedDead[b];`
43. **L14549** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L502), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
44. **L14552** `lastOrphanDeaths = outsideDead[0];`
45. **L14553** `lastUnhousedDeaths = outsideDead[1];`
46. **L14560** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L404), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
47. **L14563** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
48. **L14577** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L102)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
49. **L14584** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L906), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3747), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
50. **L14593** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L302), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L479)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
51. **L14597** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L343), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
52. **L14602** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L788), [BuildingManager.homesBySize](map/BuildingManager.md) (L6990)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
53. **L14604** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L376), [Migration.monthlyNet](map/Migration.md) (L801), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L14254), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L6969), [PopulationCohorts.share](map/PopulationCohorts.md) (L135)
54. **L14619** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
55. **L14631** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L15886)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
56. **L14633** `double[] jobsByTier = new double[PayTier.values().length];`
57. **L14634** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
58. **L14635** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1118)
59. **L14637** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
60. **L14649** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
61. **L14650** `double[] wageByTier = new double[PayTier.values().length];`
62. **L14651** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239)
63. **L14652** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
64. **L14655** `for (int t = 0; t < wageByTier.length; t++) {`
65. **L14671** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7560)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
66. **L14672** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L737)
67. **L14673** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
68. **L14674** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L128), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7560), [Game.unskilledWage](map/Game.md) (L15246)
69. **L14681** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
70. **L14683** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L788), [Crime.prisoners](map/Crime.md) (L370)
71. **L14684** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
72. **L14686** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1092)
73. **L14687** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L794)
74. **L14688** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L15246), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
75. **L14692** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L456), [PopulationCohorts.share](map/PopulationCohorts.md) (L135)  
   _The month's adult arrivals look for work next month._
76. **L14702** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L370)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
77. **L14703** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
78. **L14707** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3711), [Education.getFinished](map/Education.md) (L903)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
79. **L14719** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L6990)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
80. **L14720** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
81. **L14721** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1297)
82. **L14722** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1384), [FamilyModel.house](map/FamilyModel.md) (L1128)
83. **L14727** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L2731), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
84. **L14731** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1342)  
   _And the advisor prices a new home on who would move into it._
85. **L14732** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1347)
86. **L14748** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1631), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L907)  
   _...and the ones who cannot afford one either._
87. **L14750** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L1014), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
88. **L14789** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L161), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
89. **L14790** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L483), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7636), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7725), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L104), [Game.burialShare](map/Game.md) (L15048), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7447), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7484)
90. **L14800** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1987), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Healthcare.getFees](map/Healthcare.md) (L732)
91. **L14815** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L3639), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
92. **L14816** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L403), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7523), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7636), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7725), [Migration.getLastDepartures](map/Migration.md) (L461)
93. **L14835** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L878), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L7687), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L7700)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
94. **L14842** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1014), [Education.getGraduates](map/Education.md) (L781)
95. **L14843** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L940), [Education.getLicences](map/Education.md) (L797)
96. **L14846** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L793), [Education.getStudying](map/Education.md) (L590)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
97. **L14849** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L982)  
   _A licence holder is a graduate first, and the graduate count has just moved._
98. **L14851** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1996), [Education.getGrossCost](map/Education.md) (L844), [Education.getFees](map/Education.md) (L852)
99. **L14857** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7636), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7725)  
   _6c._
100. **L14861** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L2009), [Crime.getGrossCost](map/Crime.md) (L452)
101. **L14894** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L2741), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L1930), [Motoring.journeyFuel](map/Motoring.md) (L175)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
102. **L14896** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L243), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
103. **L14897** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L200), [Game.getInfrastructureManager](map/Game.md) (L2741)
104. **L14902** `if (homeFuel.isOn() \|\| homeFuel.hasSale()) homeFuel.strike(this, homeFuelHomes());` → [HomeFuel.isOn](map/HomeFuel.md) (L74), [HomeFuel.hasSale](map/HomeFuel.md) (L77), [HomeFuel.strike](map/HomeFuel.md) (L94), [Game.homeFuelHomes](map/Game.md) (L4130)  
   _...and the homes' lamp oil and coal (R0c, the home form; HomeFuel): the homes that light with a lamp and heat with a stove ask Retail for their month's JET and THERMAL_COAL, drawn and sold at the p..._
105. **L14906** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2047), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7636), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7725), [Game.portsBill](map/Game.md) (L7630), [Game.getInfrastructureManager](map/Game.md) (L2741), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
106. **L14918** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L757), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3851), [Game.unhousedShareOfCity](map/Game.md) (L14949), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
107. **L14934** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L316)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 171-233 (63 lines)

1. **L174** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L175** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L176** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L7034)
4. **L177** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L507), [Game.getFamilies](map/Game.md) (L14356)
5. **L183** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L504), [Game.getFamilies](map/Game.md) (L14356)  
   _The two inputs to the rent price, which is a market now._
6. **L186** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L508), [Game.getFamilies](map/Game.md) (L14356)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L190** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L505), [Game.marginalHousingCost](map/Game.md) (L19113)
8. **L192** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2068), [Game.getCohorts](map/Game.md) (L14355)
9. **L198** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L199** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L7813)
11. **L202** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L165)
12. **L209** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L213)  
   _The split behind that total._
13. **L210** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L300)
14. **L211** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L304)
15. **L226** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L2741)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L229** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L14359)  
   _The fourth ratio._
17. **L230** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1684)

## SimulationEngine.updateServices() - SimulationEngine.java lines 235-242 (8 lines)

1. **L236** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L266), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197)
2. **L237** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L284), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
3. **L240** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L341), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L241** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L67)

## Game.run() - Game.java lines 725-729 (5 lines)

1. **L726** `initialize();` → [Game.initialize](map/Game.md) (L731)
2. **L727** `foundingBank();` → [Game.foundingBank](map/Game.md) (L868)
3. **L728** `foundingYard();` → [Game.foundingYard](map/Game.md) (L955)

