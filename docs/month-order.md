# The order the month runs in

Generated 2026-11-07 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 12952-13881 (930 lines)

1. **L12960** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L12961** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L12962** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1097)
4. **L12963** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L12972** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L12973** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L15939)
7. **L12985** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L5564)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L12986** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L12727)
9. **L12988** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L14627)
10. **L12997** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L12998** `monthsSinceAutosave++;`
12. **L13005** `policyHearsTheMonth();` → [Game.policyHearsTheMonth](map/Game.md) (L1162)  
   _THE POLICY HEARS THE MONTH FIRST (R0): each lever opens on Canada's date, and a lever still shut is held at zero before anything in this month is taxed or paid._
13. **L13011** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L9442)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
14. **L13017** `strikeResearchPosts();` → [Game.strikeResearchPosts](map/Game.md) (L1333)  
   _THE RESEARCH POSTS ARE STRUCK NEXT (R3), AFTER THE DEMOLITIONS (R3b): what the month's grants buy within the schools' room, at the wages the labour market has now._
15. **L13020** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2327)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
16. **L13021** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L618)
17. **L13024** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L367)  
   _The register's month: nothing offered, nothing paid, until it is._
18. **L13025** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L507)
19. **L13026** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1109)
20. **L13027** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1654)
21. **L13045** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
22. **L13073** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
23. **L13074** `cityDiscountForBank = cityDiscountThisMonth;`
24. **L13075** `cityDebtRaisedThisMonth = 0;`
25. **L13076** `cityDiscountThisMonth = 0;`
26. **L13077** `cityPrincipalRepaidThisMonth = 0;`
27. **L13078** `bankPrincipalRepaidThisMonth = 0;`
28. **L13081** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
29. **L13082** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
30. **L13083** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
31. **L13084** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L477)
32. **L13085** `foreignDebtRaisedThisMonth = 0;`
33. **L13086** `foreignPrincipalRepaidThisMonth = 0;`
34. **L13087** `foreignInterestPaidThisMonth = 0;`
35. **L13092** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
36. **L13093** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
37. **L13094** `sectorInvested.clear();`
38. **L13095** `salvageThisMonth.clear();`
39. **L13096** `salvageBySector.clear();`
40. **L13097** `salvageUsedThisMonth = 0;`
41. **L13098** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
42. **L13099** `double pooledBefore = 0;`
43. **L13100** `for (double p : poolsBefore) pooledBefore += p;`
44. **L13101** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2105)
45. **L13103** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1326)
46. **L13104** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L2230)
47. **L13105** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L252)
48. **L13116** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
49. **L13117** `buybackAbroad = buybackAbroadUnsettled;`
50. **L13118** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
51. **L13119** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L562)
52. **L13122** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L5891)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
53. **L13141** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1321)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
54. **L13142** `if (fxRegime == ForeignAccounts.Regime.PEG) {` → [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.tiedRate](map/DebtManager.md) (L199), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1351), [DebtManager.isAutopilot](map/DebtManager.md) (L116), [DebtManager.getAim](map/DebtManager.md) (L163), [DebtManager.currencyRate](map/DebtManager.md) (L188), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L193), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L426)
55. **L13155** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L14414)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
56. **L13162** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L18897)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
57. **L13170** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L18372)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
58. **L13175** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L17996)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
59. **L13194** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L19025), [Game.studentGrantBill](map/Game.md) (L15690)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
60. **L13208** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L19050)  
   _...AND EI, THE SAME WAY (0.7.3)._
61. **L13226** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1719), [Bank.chargeTax](map/Bank.md) (L4058), [Game.bankProfitTaxRate](map/Game.md) (L4585)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
62. **L13228** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L13883)
63. **L13229** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
64. **L13230** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L14487)
65. **L13231** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2473)
66. **L13232** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L14396)
67. **L13236** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
68. **L13237** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L17769)
69. **L13238** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
70. **L13239** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1501)
71. **L13243** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L14434)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
72. **L13254** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
73. **L13256** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
74. **L13257** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L3068)
75. **L13261** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3226)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
76. **L13262** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L3062)
77. **L13278** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2812), [DebtManager.getAccretedForBank](map/DebtManager.md) (L2890)  
   _THE DISCOUNT ACCRETES (0.7.1)._
78. **L13279** `legacyDiscountDue = 0;`
79. **L13283** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
80. **L13284** `cityDiscountForBank = 0;`
81. **L13286** `double sectorInterestPaid = 0;`
82. **L13287** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L2955)
83. **L13293** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2793), [BondMarket.getCouponsStruck](map/BondMarket.md) (L1003)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
84. **L13294** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L569), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1010)
85. **L13296** `refreshBank();` → [Game.refreshBank](map/Game.md) (L4856)
86. **L13299** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L5024)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
87. **L13322** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3119), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1198), [BuildingManager.countByName](map/BuildingManager.md) (L6771), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1205)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
88. **L13325** `bankMaintenanceDue = 0;`
89. **L13333** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L4566), [Bank.openBranches](map/Bank.md) (L2731), [BuildingManager.countByName](map/BuildingManager.md) (L6771)  
   _Capital for whatever branches opened this month._
90. **L13378** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1191), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
91. **L13380** `hotMoney.setRiskOff(worldRiskOffLevel());` → [CapitalFlows.setRiskOff](map/CapitalFlows.md) (L707), [Game.worldRiskOffLevel](map/Game.md) (L2120)  
   _The risk-off the world is in (XR4): handed once here, it holds for the hot money's month below as well._
92. **L13381** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L542), [DebtManager.worldRate](map/DebtManager.md) (L815), [DebtManager.countryPremium](map/DebtManager.md) (L1141), [Bank.headroom](map/Bank.md) (L5539)
93. **L13387** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L605)
94. **L13388** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L613)
95. **L13389** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L630), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L608)
96. **L13393** `refreshBank();` → [Game.refreshBank](map/Game.md) (L4856)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
97. **L13403** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3424), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
98. **L13416** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L310), [Bank.getPlacementIncome](map/Bank.md) (L3698)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
99. **L13417** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L318), [Bank.getFundingCost](map/Bank.md) (L3696)
100. **L13418** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L303), [Bank.getBranches](map/Bank.md) (L5214), [Bank.wholesaleFunding](map/Bank.md) (L3404)
101. **L13423** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
102. **L13424** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5151)
103. **L13428** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3858)  
   _The month is final, so the figure next month's tax is charged on is final too._
104. **L13432** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L388)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
105. **L13449** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L14382)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
106. **L13450** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L3080)
107. **L13460** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3904), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3340)  
   _...AND THE SAVERS ARE PAID._
108. **L13461** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3341)
109. **L13468** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1514)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
110. **L13469** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L620), [EconomyManager.setSectorCash](map/EconomyManager.md) (L625), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1516)
111. **L13500** `payDividends();` → [Game.payDividends](map/Game.md) (L4694)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
112. **L13501** `tradeShares();` → [Game.tradeShares](map/Game.md) (L4766)
113. **L13511** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L5325)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
114. **L13521** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1140), [Exchange.cityMarketValue](map/Exchange.md) (L1722)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
115. **L13522** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L4967)
116. **L13527** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L452), [DebtManager.firmBookRatio](map/DebtManager.md) (L2831), [DebtManager.firmBookYield](map/DebtManager.md) (L2837)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
117. **L13528** `outward.takeMonth(bank.depositRate(), debtManager.worldRate(),` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L248), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142)
118. **L13532** `householdBalance.investAbroad(bank.depositRate(), debtManager.worldRate(), foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3050), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142)  
   _...and the households, by the same rule, with what the owners were just paid._
119. **L13535** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3386), [DebtManager.householdBookYield](map/DebtManager.md) (L2860), [Bank.depositRate](map/Bank.md) (L3333)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
120. **L13553** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1900)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
121. **L13554** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L5151)
122. **L13555** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L5246)
123. **L13590** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
124. **L13591** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L748)
125. **L13594** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L651), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1362)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
126. **L13595** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L380), [Bank.depositRate](map/Bank.md) (L3333), [DebtManager.getRate](map/DebtManager.md) (L1362), [DebtManager.worldRate](map/DebtManager.md) (L815), [DebtManager.countryPremium](map/DebtManager.md) (L1141), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2467), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L2170), [Game.yearlyDepreciation](map/Game.md) (L16044), [Bank.isInsolvent](map/Bank.md) (L1808), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1300)
127. **L13617** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L634)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
128. **L13618** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1684)
129. **L13619** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1698)
130. **L13620** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1672), [CapitalFlows.getStock](map/CapitalFlows.md) (L634)
131. **L13622** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L17742)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
132. **L13624** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L497)
133. **L13627** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
134. **L13637** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L2019), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2467)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
135. **L13669** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L244), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L298), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L345)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
136. **L13688** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L373), [Game.realRateDifferential](map/Game.md) (L12054)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
137. **L13694** `handTheWorldToTheD();` → [Game.handTheWorldToTheD](map/Game.md) (L2241)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
138. **L13695** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142)
139. **L13696** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L894)
140. **L13703** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1364), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1367), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
141. **L13710** `noteTheWorldMoneys(rateBeforeReprice);` → [Game.noteTheWorldMoneys](map/Game.md) (L2248)  
   _...AND THE WORLD'S NEWS (WN4): money that ran to safety abroad, a world peg that broke._
142. **L13718** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L668), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L850)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
143. **L13736** `economyManager.getNationalAccounts().setDebtExchangeGain(debtManager.revalueForeign(foreign.getRate()));` → [EconomyManager.getNationalAccounts](map/EconomyManager.md) (L56), [DebtManager.revalueForeign](map/DebtManager.md) (L899), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142)  
   _CD7: the same press books the exchange gain (loss) on the dollar paper - each piece's carrying value in dollars times the move from the rate it was last valued at - on the month the strike already ..._
144. **L13737** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L2117), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L920), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142)
145. **L13744** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L2509)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
146. **L13745** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L1049), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L1995), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L1976)
147. **L13746** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1293)
148. **L13747** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L12028)
149. **L13760** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L15843), [Game.indexSpends](map/Game.md) (L15871), [Game.indexFees](map/Game.md) (L15886), [Game.indexFeeSpends](map/Game.md) (L15902)  
   _...AND WHAT THE MONTH COST A FAMILY._
150. **L13772** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L246)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
151. **L13773** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L501), [DebtManager.holdingRate](map/DebtManager.md) (L455), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
152. **L13776** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L15964)
153. **L13779** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142)  
   _A year of the rate, so next year can tell a drift from a run._
154. **L13780** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
155. **L13782** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L18569)
156. **L13784** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L940)
157. **L13818** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
158. **L13819** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
159. **L13838** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L18823)
160. **L13840** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L3606)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
161. **L13842** `tallyThePartners();` → [Game.tallyThePartners](map/Game.md) (L1870)  
   _The month's trade split to the world's nations (WN3; THE WORLD MARKET), after its last draw: before the history and the autosave._
162. **L13843** `recordMonth();` → [Game.recordMonth](map/Game.md) (L16364)
163. **L13845** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
164. **L13847** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L2677)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
165. **L13868** `reportSaves();` → [Game.reportSaves](map/Game.md) (L16525)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
166. **L13869** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L16448)
167. **L13878** `recordForReplay();` → [Game.recordForReplay](map/Game.md) (L17141)  
   _A recorded city keeps this whole month, beside the autosave (TR1; THE RECORDING)._
168. **L13880** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L2960)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._

## Game.startOfMonthUpdate() - Game.java lines 13883-14185 (303 lines)

1. **L13935** `ensureWorldMarket();` → [Game.ensureWorldMarket](map/Game.md) (L1787)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L13936** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L260)
3. **L13937** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L309), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L298)
4. **L13939** `strikeTheWorldMarket();` → [Game.strikeTheWorldMarket](map/Game.md) (L1838)  
   _...AND THE WORLD'S BAND BEHIND IT (WN3): the January solve when the year has turned, then every good's floor and ceiling from last month's trade; before any market clears._
5. **L13953** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L423), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
6. **L13977** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L749), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
7. **L13979** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L337), [LandManager.getPricePerSqFt](map/LandManager.md) (L630)
8. **L13980** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L10223)
9. **L13990** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L14382)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
10. **L13995** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5214), [Bank.isInsolvent](map/Bank.md) (L1808)  
   _A failed bank lends nothing._
11. **L14008** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L4967)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
12. **L14009** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4963)
13. **L14010** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4991)
14. **L14015** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4646)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
15. **L14016** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L591)
16. **L14023** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L5007)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
17. **L14024** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L643), [Bank.prime](map/Bank.md) (L1137), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1158)
18. **L14028** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L965)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
19. **L14033** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L812)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
20. **L14037** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L7995)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
21. **L14041** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L7944)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
22. **L14044** `outward.payCoupons(debtManager.worldRate(), foreign.getRate(), economyManager);` → [OutwardInvestment.payCoupons](map/OutwardInvestment.md) (L218), [DebtManager.worldRate](map/DebtManager.md) (L815), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L1142)  
   _...and the coupon on the dollars its businesses hold abroad, struck as a line of their profit and kept abroad: the profit tax and the dividend see it, the till pays them (HD1, 2026-10-29; HD2, 2026..._
23. **L14058** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L2955)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
24. **L14059** `double constructionWorkDone = construction.getRecognisedThisMonth();`
25. **L14061** `if (construction.getFittingThisMonth() != 0) constructionWorkDone += construction.getFittingThisMonth();`  
   _...and the equipment the builders were paid to fit to standing buildings since the last strike banked its ledger (R1b): capital too, on the builders' side, once._
26. **L14070** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1226)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
27. **L14072** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L2960), [Game.getSectors](map/Game.md) (L2955)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
28. **L14074** `settleReserve();` → [Game.settleReserve](map/Game.md) (L5986)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
29. **L14078** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L2955)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
30. **L14084** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
31. **L14085** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2129), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L292), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L844), [EconomyManager.getResearchBill](map/EconomyManager.md) (L2025), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2066), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L632), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L634), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L823)
32. **L14141** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
33. **L14142** `monthlyMaterialImportBill = 0;`
34. **L14147** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L3947)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
35. **L14155** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L248)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
36. **L14163** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
37. **L14172** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
38. **L14176** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1455)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
39. **L14182** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L3647)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
40. **L14184** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L14186)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-156 (125 lines)

1. **L67** `int siteOutput = game.workTheFittings();` → [Game.workTheFittings](map/Game.md) (L1536)  
   _...less the fittings the crews work first (R1b; Game.workTheFittings()): after the repairs, before the sites, struck here once and used for the advance and for the work the sector is paid for below._
2. **L70** `Game.ForeignOffer foreign = game.foreignCrewsOffer();` → [Game.foreignCrewsOffer](map/Game.md) (L7896)  
   _The foreign contractor's month beside the city's own (FC1): nothing unless the repairs took the builders' points and left the sites none - see Game, THE FOREIGN CREWS._
3. **L72** `java.util.List<BuildingManager.Completion> opened = buildingManager.advanceConstruction(siteOutput + foreign.points()...` → [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5422)  
   _His points are in the month the sites take, and not on a rushed site's overtime (FC1b): his crews are not the city's._
4. **L73** `game.recordCompletions(opened);` → [Game.recordCompletions](map/Game.md) (L15492)
5. **L76** `game.fitWhatFinished(opened);` → [Game.fitWhatFinished](map/Game.md) (L1259)  
   _...and what the buildings that opened come with (R1b): in a city founded the 1900 way, only the equipment that pays for a new building; nothing in a city founded the default way, where every buildi..._
6. **L92** `game.getRecipes().apply(game);   // the ways the buildings run, at the counts the month leaves (R1c): first, the stea...` → [Game.getRecipes](map/Game.md) (L1180)  
   _...AND THE 1900 FORMS AT REST (R1b; R1's stranger's review, 2026-11-05): the shares the counts set, said again now that the month has changed the counts' weights._
7. **L93** `game.getEquipmentForms().apply(game);` → [Game.getEquipmentForms](map/Game.md) (L1186)  
   _the ways the buildings run, at the counts the month leaves (R1c): first, the steam form below weighs each building by its electricity_
8. **L101** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L9021), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5507)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
9. **L105** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5559)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
10. **L106** `double ownMonth = overtime == 0 ? siteOutput : siteOutput + overtime;`
11. **L108** `game.settleForeignCrews(foreign, buildingManager.getPointsBuilt() - Math.max(0, ownMonth));` → [Game.settleForeignCrews](map/Game.md) (L7917), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5523)  
   _...and the contractor is paid for what the sites took past the city's own month (FC1)._
12. **L109** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L9129), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6734), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5523)
13. **L115** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L9069), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L6743)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
14. **L120** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L9736), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5552)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
15. **L129** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L187)  
   _Roads, before anything reads them._
16. **L136** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L158)  
   _Recount the posts and refresh the wage arrays._
17. **L152** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L14788)  
   _THE POPULATION STEP._
18. **L154** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L172)
19. **L155** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L236)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 158-170 (13 lines)

1. **L163** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L7789)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L164** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L14651)
3. **L165** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L14664)
4. **L168** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L16269)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L169** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L335)

## Game.advanceDemographics() - Game.java lines 14788-15354 (567 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L14793** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L561), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L14807** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1080)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L14808** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L15385)
4. **L14809** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L15385)
5. **L14810** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L15385)
6. **L14811** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7582)
7. **L14813** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
8. **L14827** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L14828** `java.util.Arrays.fill(affordable, 1);`
10. **L14829** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L15404)
11. **L14832** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L14833** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L14834** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L14837** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L646)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L14840** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L14841** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7582)
17. **L14855** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L14868** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L406)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L14879** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L14880** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L14881** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L14882** `double[] inBand = new double[AgeBand.values().length];`
23. **L14883** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L102)
24. **L14887** `double[] careFactors = mortalityFactors;`
25. **L14888** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L466), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
26. **L14898** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L316)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L14901** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L14908** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L14909** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L102)
30. **L14910** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
31. **L14930** `double[][] causeRates = causesOfDeath.rates(month, inBand, mortalityFactors,` → [CausesOfDeath.rates](map/CausesOfDeath.md) (L447), [Game.getWaterRatio](map/Game.md) (L2946), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3851), [Health.getOutbreakSeverity](map/Health.md) (L338), [PopulationCohorts.getLastBirths](map/PopulationCohorts.md) (L103)  
   _...AND WHAT THEY DIE OF (R0b)._
32. **L14933** `double[] causeTotals = CausesOfDeath.bandTotals(causeRates);` → [CausesOfDeath.bandTotals](map/CausesOfDeath.md) (L384)
33. **L14935** `for (AgeBand b : AgeBand.values()) {` → [CausesOfDeath.isHeldOff](map/CausesOfDeath.md) (L422), [CausesOfDeath.keptFactor](map/CausesOfDeath.md) (L361)  
   _...held off for a harness fixture (R0b call 7): no cause, and the swing whole._
34. **L14940** `cohorts.advanceMonth(mortalityFactors, illness, violence, causeTotals,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L177), [Healthcare.birthFactor](map/Healthcare.md) (L425)
35. **L14942** `causesOfDeath.book(month, causeRates, cohorts.getCauseDeaths());` → [CausesOfDeath.book](map/CausesOfDeath.md) (L510), [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L123)
36. **L14943** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L270), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L109)
37. **L14947** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
38. **L14948** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L112)
39. **L14949** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L109)
40. **L14950** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L114)
41. **L14951** `double[] causedDead = cohorts.getCauseDeaths();` → [PopulationCohorts.getCauseDeaths](map/PopulationCohorts.md) (L123)
42. **L14952** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b] + causedDead[b];`
43. **L14953** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L502), [Healthcare.mortalityFactors](map/Healthcare.md) (L406)
44. **L14956** `lastOrphanDeaths = outsideDead[0];`
45. **L14957** `lastUnhousedDeaths = outsideDead[1];`
46. **L14964** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L404), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
47. **L14967** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
48. **L14981** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L102)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
49. **L14988** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L906), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3747), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
50. **L14997** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L302), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L479)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
51. **L15001** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L343), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
52. **L15006** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L788), [BuildingManager.homesBySize](map/BuildingManager.md) (L7088)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
53. **L15010** `migration.setYearMix(foundedUnfitted() ? ArrivalMix.at(month) : null);` → [Migration.setYearMix](map/Migration.md) (L1081), [Game.foundedUnfitted](map/Game.md) (L1196), [ArrivalMix.at](map/ArrivalMix.md) (L145)  
   _...and, in a city founded the 1900 way, what the world's movers hold this year (R0d; ArrivalMix): the skills the month's arrivals are drawn from._
54. **L15012** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L376), [Migration.monthlyNet](map/Migration.md) (L801), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L14655), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L7067), [PopulationCohorts.share](map/PopulationCohorts.md) (L135)
55. **L15027** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
56. **L15039** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L16304)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
57. **L15041** `double[] jobsByTier = new double[PayTier.values().length];`
58. **L15042** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1080)
59. **L15043** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1139)
60. **L15045** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
61. **L15057** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
62. **L15058** `double[] wageByTier = new double[PayTier.values().length];`
63. **L15059** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239)
64. **L15060** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L116)
65. **L15063** `for (int t = 0; t < wageByTier.length; t++) {`
66. **L15079** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7658)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
67. **L15080** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L737)
68. **L15081** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L128)
69. **L15082** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L128), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7658), [Game.unskilledWage](map/Game.md) (L15664)
70. **L15089** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
71. **L15091** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L809), [Crime.prisoners](map/Crime.md) (L370)
72. **L15092** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
73. **L15094** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1113)
74. **L15095** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L815)
75. **L15096** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L15664), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
76. **L15100** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L456), [PopulationCohorts.share](map/PopulationCohorts.md) (L135)  
   _The month's adult arrivals look for work next month._
77. **L15110** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L370)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
78. **L15111** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
79. **L15115** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3711), [Education.getFinished](map/Education.md) (L903)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
80. **L15127** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L7088)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
81. **L15128** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
82. **L15129** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1297)
83. **L15130** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1384), [FamilyModel.house](map/FamilyModel.md) (L1128)
84. **L15135** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L2955), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
85. **L15139** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1342)  
   _And the advisor prices a new home on who would move into it._
86. **L15140** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1347)
87. **L15156** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1631), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L907)  
   _...and the ones who cannot afford one either._
88. **L15158** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L1014), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
89. **L15197** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L161), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
90. **L15198** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L483), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7734), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7823), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L104), [Game.burialShare](map/Game.md) (L15466), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7545), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7582)
91. **L15208** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1987), [Healthcare.getGrossCost](map/Healthcare.md) (L730), [Healthcare.getFees](map/Healthcare.md) (L732)
92. **L15223** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L3972), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
93. **L15224** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L403), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7621), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7734), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7823), [Migration.getLastDepartures](map/Migration.md) (L461)
94. **L15243** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L878), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L7785), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L7798)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
95. **L15250** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1035), [Education.getGraduates](map/Education.md) (L781)
96. **L15251** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L961), [Education.getLicences](map/Education.md) (L797)
97. **L15254** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L814), [Education.getStudying](map/Education.md) (L590)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
98. **L15257** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L1003)  
   _A licence holder is a graduate first, and the graduate count has just moved._
99. **L15259** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1996), [Education.getGrossCost](map/Education.md) (L844), [Education.getFees](map/Education.md) (L852)
100. **L15268** `settleResearchBill(fill);` → [Game.settleResearchBill](map/Game.md) (L1390)  
   _...AND THE RESEARCH (R3): beside the schools because their staff are its faculty and its room, at the same wages and fill they were paid at._
101. **L15269** `workTheResearch(fill);` → [Game.workTheResearch](map/Game.md) (L1412)
102. **L15275** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7734), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7823)  
   _6c._
103. **L15279** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L2009), [Crime.getGrossCost](map/Crime.md) (L452)
104. **L15312** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L2965), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L1930), [Motoring.journeyFuel](map/Motoring.md) (L175)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
105. **L15314** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L243), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
106. **L15315** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L200), [Game.getInfrastructureManager](map/Game.md) (L2965)
107. **L15320** `if (homeFuel.isOn() \|\| homeFuel.hasSale()) homeFuel.strike(this, homeFuelHomes());` → [HomeFuel.isOn](map/HomeFuel.md) (L74), [HomeFuel.hasSale](map/HomeFuel.md) (L77), [HomeFuel.strike](map/HomeFuel.md) (L94), [Game.homeFuelHomes](map/Game.md) (L4463)  
   _...and the homes' lamp oil and coal (R0c, the home form; HomeFuel): the homes that light with a lamp and heat with a stove ask Retail for their month's JET and THERMAL_COAL, drawn and sold at the p..._
108. **L15324** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2062), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7734), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7823), [Game.portsBill](map/Game.md) (L7966), [Game.getInfrastructureManager](map/Game.md) (L2965), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
109. **L15336** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L757), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3851), [Game.unhousedShareOfCity](map/Game.md) (L15367), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
110. **L15352** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L316)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 172-234 (63 lines)

1. **L175** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L176** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L177** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L7132)
4. **L178** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L507), [Game.getFamilies](map/Game.md) (L14760)
5. **L184** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L504), [Game.getFamilies](map/Game.md) (L14760)  
   _The two inputs to the rent price, which is a market now._
6. **L187** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L508), [Game.getFamilies](map/Game.md) (L14760)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L191** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L505), [Game.marginalHousingCost](map/Game.md) (L19603)
8. **L193** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2083), [Game.getCohorts](map/Game.md) (L14759)
9. **L199** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1080)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L200** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L7911)
11. **L203** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L165)
12. **L210** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L213)  
   _The split behind that total._
13. **L211** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L300)
14. **L212** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L304)
15. **L227** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L2965)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L230** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L14763)  
   _The fourth ratio._
17. **L231** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1684)

## SimulationEngine.updateServices() - SimulationEngine.java lines 236-243 (8 lines)

1. **L237** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L266), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197)
2. **L238** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L284), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1080)
3. **L241** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L341), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L242** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L67)

## Game.run() - Game.java lines 731-735 (5 lines)

1. **L732** `initialize();` → [Game.initialize](map/Game.md) (L737)
2. **L733** `foundingBank();` → [Game.foundingBank](map/Game.md) (L876)
3. **L734** `foundingYard();` → [Game.foundingYard](map/Game.md) (L963)

