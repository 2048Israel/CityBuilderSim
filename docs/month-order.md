# The order the month runs in

Generated 2026-10-25 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 10257-11158 (902 lines)

1. **L10265** `autoSalesThisMonth.clear();`  
   _AUTOMATIC BUILDING FIRST (0.7.73; AutoBuilder), when the player has it on: between the presses, where the player's own Build lands, on the city the last press left and at the prices it was quoted -..._
2. **L10266** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(false);`
3. **L10267** `autoBuilder.pass(this);` → [AutoBuilder.pass](map/AutoBuilder.md) (L1086)
4. **L10268** `if (autoBuildProbeForTest != null) autoBuildProbeForTest.accept(true);`
5. **L10277** `expectations.strikeLevel();` → [Expectations.strikeLevel](map/Expectations.md) (L141)  
   _THE MONTH'S MONEY CONSTANTS, struck before anything in it is priced (0.7.42, THE ANCHOR): at the expected price level the last month ended on, so the month lives at one level from here to the next ..._
6. **L10278** `restrikeMoneyConstants();` → [Game.restrikeMoneyConstants](map/Game.md) (L13070)
7. **L10290** `fundYearEnd();` → [Game.fundYearEnd](map/Game.md) (L3825)  
   _WHAT FALLS DUE IS ROLLED FIRST (0.7.13), before the calendar turns - in the gap between two presses, where a player's own issue lands, so it settles to its buyers and crosses the audit window exact..._
8. **L10291** `rollMaturities();` → [Game.rollMaturities](map/Game.md) (L10037)
9. **L10293** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L11836)
10. **L10302** `month++;`  
   _A TREASURY BELOW ZERO USED TO BORROW HERE, before the month began: an emergency note for the gap, six months, sold to the bank._
11. **L10303** `monthsSinceAutosave++;`
12. **L10309** `closeDemolished();` → [Game.closeDemolished](map/Game.md) (L7304)  
   _The buildings the player ordered demolished between the presses close now, as the month after the order starts - the first it is in force for - before anything in it reads the city (0.7.22; THE PLA..._
13. **L10312** `bank.setMonth(month);` → [Bank.setMonth](map/Bank.md) (L2321)  
   _The bank's clock, for its preferred's anniversaries, and the fund's month (0.7.14)._
14. **L10313** `fund.startMonth();` → [TreasuryFund.startMonth](map/TreasuryFund.md) (L613)
15. **L10316** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L347)  
   _The register's month: nothing offered, nothing paid, until it is._
16. **L10317** `exchange.startMonth(month);` → [Exchange.startMonth](map/Exchange.md) (L404)
17. **L10318** `bondMarket.startMonth();` → [BondMarket.startMonth](map/BondMarket.md) (L1109)
18. **L10319** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L1642)
19. **L10337** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
20. **L10365** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`  
   _=================================================================== THE BANK PAYS FOR THE CITY'S PAPER (2026-09-21)_
21. **L10366** `cityDiscountForBank = cityDiscountThisMonth;`
22. **L10367** `cityDebtRaisedThisMonth = 0;`
23. **L10368** `cityDiscountThisMonth = 0;`
24. **L10369** `cityPrincipalRepaidThisMonth = 0;`
25. **L10370** `bankPrincipalRepaidThisMonth = 0;`
26. **L10373** `couponsToHouseholds = principalToHouseholds = householdsBoughtPaper = 0;`  
   _The holders' month (0.7.1): what the households were paid on their paper and paid for it._
27. **L10374** `couponsToFirms = principalToFirms = firmsBoughtPaper = 0;`
28. **L10375** `couponsToWorld = principalToWorld = worldBoughtPaper = paperBoughtFromWorld = 0;`
29. **L10376** `outward.startMonth();` → [OutwardInvestment.startMonth](map/OutwardInvestment.md) (L430)
30. **L10377** `foreignDebtRaisedThisMonth = 0;`
31. **L10378** `foreignPrincipalRepaidThisMonth = 0;`
32. **L10379** `foreignInterestPaidThisMonth = 0;`
33. **L10384** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
34. **L10385** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
35. **L10386** `sectorInvested.clear();`
36. **L10387** `salvageThisMonth.clear();`
37. **L10388** `salvageBySector.clear();`
38. **L10389** `salvageUsedThisMonth = 0;`
39. **L10390** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
40. **L10391** `double pooledBefore = 0;`
41. **L10392** `for (double p : poolsBefore) pooledBefore += p;`
42. **L10393** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L2076)
43. **L10395** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L1324)
44. **L10396** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L1705)
45. **L10397** `centralBank.startMonth();` → [CentralBank.startMonth](map/CentralBank.md) (L252)
46. **L10408** `buybackToHouseholds = buybackToHouseholdsUnsettled;`  
   _A BUYBACK BETWEEN THE PRESSES IS DECLARED HERE (0.7.1)._
47. **L10409** `buybackAbroad = buybackAbroadUnsettled;`
48. **L10410** `buybackToHouseholdsUnsettled = buybackAbroadUnsettled = 0;`
49. **L10411** `centralBank.settleRedemptions();` → [CentralBank.settleRedemptions](map/CentralBank.md) (L562)
50. **L10414** `settleTheEdge();` → [Game.settleTheEdge](map/Game.md) (L4135)  
   _...and the dollars the vault took at the strong edge of a band or a peg at the last reprice are paid for here, by their payer (XR2)._
51. **L10433** `ForeignAccounts.Regime fxRegime = foreign.getRegime();` → [ForeignAccounts.getRegime](map/ForeignAccounts.md) (L1170)  
   _THE AUTOPILOT (0.7.0), before anything is priced: with the rule's hand on the dial, the dial goes where the rule says - DebtManager .advisedPolicyRate() on the year's inflation - and holds where it..._
52. **L10434** `if (fxRegime == ForeignAccounts.Regime.PEG) {` → [DebtManager.setPolicyRate](map/DebtManager.md) (L79), [DebtManager.tiedRate](map/DebtManager.md) (L197), [ForeignAccounts.currencyGap](map/ForeignAccounts.md) (L1200), [DebtManager.isAutopilot](map/DebtManager.md) (L116), [DebtManager.getAim](map/DebtManager.md) (L161), [DebtManager.currencyRate](map/DebtManager.md) (L186), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [DebtManager.mixRate](map/DebtManager.md) (L191), [PriceIndex.inflation](map/PriceIndex.md) (L549), [DebtManager.advisedPolicyRate](map/DebtManager.md) (L424)
53. **L10447** `strikeTheCurve();` → [Game.strikeTheCurve](map/Game.md) (L11670)  
   _THE CURVE'S EXPECTATIONS, struck once a month after the dial is set (autopilot or not): see strikeTheCurve()._
54. **L10454** `settleTreasury();` → [Game.settleTreasury](map/Game.md) (L15588)  
   _The central bank settles with the treasury: last month's profit remitted, the advances' interest charged, repaid from cash above zero or advanced the shortfall._
55. **L10462** `rollCentralBankAtIssue();` → [Game.rollCentralBankAtIssue](map/Game.md) (L15088)  
   _The central bank's own maturing paper, replaced at issue by its add-on to what the city sold between the presses, par for par (0.7.15, round 2): after the settle, so its money waits for the maturit..._
56. **L10467** `openMarketOperation();` → [Game.openMarketOperation](map/Game.md) (L14712)  
   _The holdings dial (0.7.1): the central bank buys or sells the city's term paper toward its target, after the settle above and before the market is priced._
57. **L10486** `payStudentGrants(studentGrantBill());` → [Game.payStudentGrants](map/Game.md) (L15713), [Game.studentGrantBill](map/Game.md) (L12821)  
   _THE STUDENTS' GRANT, PAID IN THE MONTH IT IS CREDITED (0.7.1)._
58. **L10500** `payEiBenefits();` → [Game.payEiBenefits](map/Game.md) (L15738)  
   _...AND EI, THE SAME WAY (0.7.3)._
59. **L10518** `economyManager.setBankTax(bank.chargeTax(bankProfitTaxRate()));` → [EconomyManager.setBankTax](map/EconomyManager.md) (L1707), [Bank.chargeTax](map/Bank.md) (L3950), [Game.bankProfitTaxRate](map/Game.md) (L2897)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
60. **L10520** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L11160)
61. **L10521** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L32)
62. **L10522** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L11740)
63. **L10523** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L2442)
64. **L10524** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L11652)
65. **L10528** `if (settleProbeForTest != null) settleProbeForTest.accept(false);`  
   _The paper sold between the presses settles to its holders: the households first, before the month's coupon (0.7.1)._
66. **L10529** `householdsTakeTheirShare();` → [Game.householdsTakeTheirShare](map/Game.md) (L14527)
67. **L10530** `if (settleProbeForTest != null) settleProbeForTest.accept(true);`
68. **L10531** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L1267)
69. **L10535** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L11690)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
70. **L10546** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
71. **L10548** `cityPaperSettled = cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper - worldBoughtPaper;`  
   _The residual buyer (0.7.1): what the households, the businesses and the world did not pay for._
72. **L10549** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank - householdsBoughtPaper - firmsBoughtPaper` → [Bank.lend](map/Bank.md) (L2983)
73. **L10553** `bank.takeLoanFees(lender.getFeesThisMonth());` → [Bank.takeLoanFees](map/Bank.md) (L3141)  
   _...and the fee on what it lent the businesses, which they were handed their loans net of (0.7.7): pool to pool, so it cancels._
74. **L10554** `bank.takeRepayment(lender.getRepaidThisMonth() + bankPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L2977)
75. **L10570** `bank.takeDiscount(debtManager.getAccretedForBank() + legacyDiscountDue);` → [Bank.takeDiscount](map/Bank.md) (L2806), [DebtManager.getAccretedForBank](map/DebtManager.md) (L2650)  
   _THE DISCOUNT ACCRETES (0.7.1)._
76. **L10571** `legacyDiscountDue = 0;`
77. **L10575** `cityDebtRaisedForBank = 0;`  
   _Settled: the bank has paid for the paper, so it owes nothing for it and MoneyAudit's closing pool is its cash._
78. **L10576** `cityDiscountForBank = 0;`
79. **L10578** `double sectorInterestPaid = 0;`
80. **L10579** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L1681)
81. **L10585** `bank.takeInterest(interestDue, sectorInterestPaid - bondMarket.getCouponsStruck());` → [Bank.takeInterest](map/Bank.md) (L2787), [BondMarket.getCouponsStruck](map/BondMarket.md) (L1003)  
   _The city's coupons and the businesses' interest, kept by who paid them since 0.7.9 - the same cash and income as their sum, to the bit._
82. **L10586** `bank.takeBondCoupons(bondMarket.getCouponsDueToBank());` → [Bank.takeBondCoupons](map/Bank.md) (L567), [BondMarket.getCouponsDueToBank](map/BondMarket.md) (L1010)
83. **L10588** `refreshBank();` → [Game.refreshBank](map/Game.md) (L3154)
84. **L10591** `provideForLosses();` → [Game.provideForLosses](map/Game.md) (L3322)  
   _...and what it sets aside against it, now every loan and write-off of the month is on the books (0.7.8)._
85. **L10614** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue,` → [Bank.payRunning](map/Bank.md) (L3034), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L138), [EconomyManager.bankOperatingCost](map/EconomyManager.md) (L1198), [BuildingManager.countByName](map/BuildingManager.md) (L6415), [EconomyManager.bankOperatingCostPerLaterBranch](map/EconomyManager.md) (L1205)  
   _...and what its branches cost to run, the ones standing now (0.7.19), the charter exempt from the operating cost (revised), with what a later branch carries of it for the branch rule to read._
86. **L10617** `bankMaintenanceDue = 0;`
87. **L10625** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L2878), [Bank.openBranches](map/Bank.md) (L2725), [BuildingManager.countByName](map/BuildingManager.md) (L6415)  
   _Capital for whatever branches opened this month._
88. **L10670** `double carryRate = bank.carryRate(debtManager.getPolicyRate());` → [Bank.carryRate](map/Bank.md) (L1189), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
89. **L10671** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L527), [DebtManager.countryPremium](map/DebtManager.md) (L933), [Bank.headroom](map/Bank.md) (L5427)
90. **L10677** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L603)
91. **L10678** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L611)
92. **L10679** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L628), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L580)
93. **L10683** `refreshBank();` → [Game.refreshBank](map/Game.md) (L3154)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, and what funding costs._
94. **L10693** `bank.fundToCover(debtManager.getPolicyRate());` → [Bank.fundToCover](map/Bank.md) (L3316), [DebtManager.getPolicyRate](map/DebtManager.md) (L77)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE was handed to the bank here as a function until 0.7.7, for the deposit rate's bid for hot money (rule 3, off CapitalFlows.arrivalsAt(), deleted in 0.7.8 with n..._
95. **L10706** `centralBank.payInterestOnReserves(bank.getPlacementIncome());` → [CentralBank.payInterestOnReserves](map/CentralBank.md) (L310), [Bank.getPlacementIncome](map/Bank.md) (L3590)  
   _...AND THE CENTRAL BANK'S END OF IT (0.7.0), off the bank's own two figures so the two cannot disagree: the policy rate on its reserves, paid in money made; the window's interest, paid back and des..._
96. **L10707** `centralBank.chargeWindow(bank.getFundingCost());` → [CentralBank.chargeWindow](map/CentralBank.md) (L318), [Bank.getFundingCost](map/Bank.md) (L3588)
97. **L10708** `centralBank.settleWindow(bank.getBranches() > 0 ? bank.wholesaleFunding() : 0);` → [CentralBank.settleWindow](map/CentralBank.md) (L303), [Bank.getBranches](map/Bank.md) (L5102), [Bank.wholesaleFunding](map/Bank.md) (L3296)
98. **L10713** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1896)  
   _...and if that left it owing more than it owns, it has failed - and waits for the city, which resolves it now if its setting is automatic (0.7.14; resolveIfAutomatic())._
99. **L10714** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L3449)
100. **L10718** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L3750)  
   _The month is final, so the figure next month's tax is charged on is final too._
101. **L10722** `centralBank.closeMonth();` → [CentralBank.closeMonth](map/CentralBank.md) (L388)  
   _...and the central bank's: its profit struck, to be remitted at the top of next month, or its loss carried._
102. **L10739** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L11638)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
103. **L10740** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L2840)
104. **L10750** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L3892), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L3255)  
   _...AND THE SAVERS ARE PAID._
105. **L10751** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L3256)
106. **L10758** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L1514)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
107. **L10759** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L620), [EconomyManager.setSectorCash](map/EconomyManager.md) (L625), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L1516)
108. **L10790** `payDividends();` → [Game.payDividends](map/Game.md) (L3006)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
109. **L10791** `tradeShares();` → [Game.tradeShares](map/Game.md) (L3077)
110. **L10801** `settleThePreferred();` → [Game.settleThePreferred](map/Game.md) (L3623)  
   _...and the city's preferred (0.7.14): every block at its third anniversary repaid whole at par with its unpaid dividends, from the bank's capital over its target and the rest by an offering of new ..._
111. **L10811** `bondMarket.takeMonth(month, exchange.cityMarketValue(equity));` → [BondMarket.takeMonth](map/BondMarket.md) (L1140), [Exchange.cityMarketValue](map/Exchange.md) (L1245)  
   _...AND THE BONDS TRADE (0.7.12): the month's coupons paid to their holders, last month's orders withdrawn, every bond valued and every participant's orders posted again - after the shares, and befo..._
112. **L10812** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L3265)
113. **L10817** `outward.setPaperBook(debtManager.firmBookRatio(), debtManager.firmBookYield());` → [OutwardInvestment.setPaperBook](map/OutwardInvestment.md) (L405), [DebtManager.firmBookRatio](map/DebtManager.md) (L2591), [DebtManager.firmBookYield](map/DebtManager.md) (L2597)  
   _The businesses' paper at today's curve, struck here as the households' book yield is a few lines below, so paper bought at this month's settle is read at its own yield and not at the short rate it ..._
114. **L10818** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L181), [Bank.depositRate](map/Bank.md) (L3248), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L991)
115. **L10822** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L3039), [Bank.depositRate](map/Bank.md) (L3248), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L991)  
   _...and the households, by the same rule, with what the owners were just paid._
116. **L10825** `householdBalance.sellPaperForSpread(debtManager.householdBookYield(), bank.depositRate());` → [HouseholdBalance.sellPaperForSpread](map/HouseholdBalance.md) (L3375), [DebtManager.householdBookYield](map/DebtManager.md) (L2620), [Bank.depositRate](map/Bank.md) (L3248)  
   _...and the city's paper, when the spread that brought them in has gone (0.7.1): home to the bank's desk, a little a month._
117. **L10843** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L1896)  
   _...AND IF WHAT LANDED AFTER THE CLOSE BROKE IT, IT HAS FAILED THIS MONTH (0.7.8)._
118. **L10844** `resolveIfAutomatic();` → [Game.resolveIfAutomatic](map/Game.md) (L3449)
119. **L10845** `considerPreferredOffer();` → [Game.considerPreferredOffer](map/Game.md) (L3544)
120. **L10880** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L606)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
121. **L10881** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L651)
122. **L10884** `hotMoney.setDoubt(foreign.getDoubtShare());` → [CapitalFlows.setDoubt](map/CapitalFlows.md) (L623), [ForeignAccounts.getDoubtShare](map/ForeignAccounts.md) (L1211)  
   _The hot money takes the break chance of itself home under a peg (XR2): the share the last reprice left, 0 under every other regime._
123. **L10885** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L378), [Bank.depositRate](map/Bank.md) (L3248), [DebtManager.getRate](map/DebtManager.md) (L1128), [DebtManager.countryPremium](map/DebtManager.md) (L933), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2436), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L1645), [Game.yearlyDepreciation](map/Game.md) (L13175), [Bank.isInsolvent](map/Bank.md) (L1804), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L1066)
124. **L10907** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L606)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
125. **L10908** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L1680)
126. **L10909** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L1694)
127. **L10910** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L1668), [CapitalFlows.getStock](map/CapitalFlows.md) (L606)
128. **L10912** `worldPaperLeavesWithTheMoney(hotBefore);` → [Game.worldPaperLeavesWithTheMoney](map/Game.md) (L14502)  
   _...and the city's paper the world holds leaves with it (CD2, slice 5): sold to the bank's desk, inside the window._
129. **L10914** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L489)
130. **L10917** `ownersWipedAbroadThisMonth = 0;`  
   _Declared (0.7.14): the valuation a resolution wiped off the world's shares is a month's, whether the month's or a press's before it._
131. **L10927** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L1484), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L2436)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
132. **L10959** `foreign.setParity(priceIndex.getParityLevel(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L244), [PriceIndex.getParityLevel](map/PriceIndex.md) (L512), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293), [PriceIndex.inflation](map/PriceIndex.md) (L549), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L340)  
   _THE CAPPED LEVEL, not the index (XR1, star 2: "Basket, rent capped")._
133. **L10978** `foreign.setRealRateDifferential(realRateDifferential());` → [ForeignAccounts.setRealRateDifferential](map/ForeignAccounts.md) (L373), [Game.realRateDifferential](map/Game.md) (L9443)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD - IN REAL TERMS (0.7.2)._
134. **L10982** `double rateBeforeReprice = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L991)  
   _The rate follows parity's trend, the real gap's level, the balance, the cover, the market and the interventions felt (ForeignAccounts, THE FLOAT), and since XR2 the regime holds it where it says (T..._
135. **L10983** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L763)
136. **L10990** `if (foreign.brokeThisMonth()) {` → [ForeignAccounts.brokeThisMonth](map/ForeignAccounts.md) (L1213), [Expectations.pegBroke](map/Expectations.md) (L237), [GameLog.note](map/GameLog.md) (L161), [ForeignAccounts.getLastBreakCause](map/ForeignAccounts.md) (L1216), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L991)  
   _...AND A PEG THAT BROKE (XR2, star 4): credibility takes its dent, and the log says so - by the vault or by doubt, and where the D$ landed._
137. **L11003** `centralBank.dollarsSold(foreign.getDefenceLocal());` → [CentralBank.dollarsSold](map/CentralBank.md) (L668), [ForeignAccounts.getDefenceLocal](map/ForeignAccounts.md) (L719)  
   _...and what the vault's dollars fetched, if the reprice defended the currency: the central bank's equity line, booked here, after the audit, because no pool moves on it - a capital transaction agai..._
138. **L11018** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L730), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L991)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
139. **L11019** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L1582), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L746), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L991)
140. **L11026** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L1919)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
141. **L11027** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L841), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L1460), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L1441)
142. **L11028** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L1059)
143. **L11029** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L9418)
144. **L11042** `priceIndex.takeMonth(indexPrices(), indexSpends(), indexFees(), indexFeeSpends(), month);` → [PriceIndex.takeMonth](map/PriceIndex.md) (L211), [Game.indexPrices](map/Game.md) (L12974), [Game.indexSpends](map/Game.md) (L13002), [Game.indexFees](map/Game.md) (L13017), [Game.indexFeeSpends](map/Game.md) (L13033)  
   _...AND WHAT THE MONTH COST A FAMILY._
145. **L11054** `double target = debtManager.getInflationTarget();` → [DebtManager.getInflationTarget](map/DebtManager.md) (L244)  
   _The lean is measured against what holding the target takes - the Standard rule's advice and the neutral rate - at any strictness (0.7.52, DebtManager's HOW STRICT); at Standard they are the rule's ..._
146. **L11055** `expectations.takeMonth(priceIndex, target, debtManager.getPolicyRate(),` → [Expectations.takeMonth](map/Expectations.md) (L163), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [DebtManager.neutralRate](map/DebtManager.md) (L499), [DebtManager.holdingRate](map/DebtManager.md) (L453), [PriceIndex.hasRate](map/PriceIndex.md) (L557), [PriceIndex.inflation](map/PriceIndex.md) (L549)
147. **L11058** `handOnExpectations();` → [Game.handOnExpectations](map/Game.md) (L13095)
148. **L11061** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L991)  
   _A year of the rate, so next year can tell a drift from a run._
149. **L11062** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
150. **L11064** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L15277)
151. **L11066** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L831)
152. **L11100** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
153. **L11101** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L322)
154. **L11120** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L15514)
155. **L11122** `deferBorrowingCosts();` → [Game.deferBorrowingCosts](map/Game.md) (L2122)  
   _What each business's borrowing cost it up front this month, deferred to be expensed (0.7.102, A16)._
156. **L11123** `recordMonth();` → [Game.recordMonth](map/Game.md) (L13484)
157. **L11125** `monthRun = true;`  
   _A month has run since the city was founded or loaded: its flows are this month's (0.7.102)._
158. **L11127** `reconcileMap();` → [Game.reconcileMap](map/Game.md) (L1414)  
   _The city map takes the month's buildings (0.7.60): THE CITY MAP._
159. **L11147** `reportSaves();` → [Game.reportSaves](map/Game.md) (L13630)  
   _THE AUTOSAVE HOLDS A WHOLE MONTH (0.7.52)._
160. **L11148** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L13563)
161. **L11157** `getMarkets().closeMonth();` → [Game.getMarkets](map/Game.md) (L1686)  
   _The month's draws were shown with its fills; a draw from here on is the next month's (FW5, 2026-10-25)._

## Game.startOfMonthUpdate() - Game.java lines 11160-11446 (287 lines)

1. **L11206** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L263)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L11207** `economyManager.strikeExchangeRate(foreign.getRate(), world.getPriceLevel());` → [EconomyManager.strikeExchangeRate](map/EconomyManager.md) (L309), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L991), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L293)
3. **L11221** `if (priceIndex.isBased()) {` → [PriceIndex.isBased](map/PriceIndex.md) (L487), [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L365), [PriceIndex.getIndex](map/PriceIndex.md) (L485), [Expectations.monthlyExpected](map/Expectations.md) (L147)  
   _...half of it from what people expect, once the basket is based (0.7.42): see LabourMarket, HALF WHAT PEOPLE EXPECT, HALF THE CHASE._
4. **L11245** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L563), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _what the next parcel costs, are both inputs to everything below._
5. **L11247** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L337), [LandManager.getPricePerSqFt](map/LandManager.md) (L445)
6. **L11248** `pushLandByRelief();` → [Game.pushLandByRelief](map/Game.md) (L8029)
7. **L11258** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L11638)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
8. **L11263** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L5102), [Bank.isInsolvent](map/Bank.md) (L1804)  
   _A failed bank lends nothing._
9. **L11276** `strikeBankBonds();` → [Game.strikeBankBonds](map/Game.md) (L3265)  
   _...its bonds and its book's concentration re-read first (0.7.12), so the rule and the prices below read the book as it stands._
10. **L11277** `double growth = bank.lendingGrowthLimit();` → [Bank.lendingGrowthLimit](map/Bank.md) (L4855)
11. **L11278** `boolean keepGoingOnly = bank.lendsOnlyToKeepBorrowersGoing();` → [Bank.lendsOnlyToKeepBorrowersGoing](map/Bank.md) (L4883)
12. **L11283** `economyManager.getBusinessDebtManager().setCapitalRule(growth, keepGoingOnly, bank.leverageBinds());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.leverageBinds](map/Bank.md) (L4538)  
   _...the insured mortgages with the rest while the leverage ratio binds (0.7.11, round 2): then a mortgage uses the capital the bank is short of like any loan (BusinessDebtManager, THE LANDLORDS' MOR..._
13. **L11284** `householdBalance.setCapitalRule(growth, keepGoingOnly);` → [HouseholdBalance.setCapitalRule](map/HouseholdBalance.md) (L581)
14. **L11291** `economyManager.getBusinessDebtManager().setConcentrationCharges(concentrationCharges());` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Game.concentrationCharges](map/Game.md) (L3305)  
   _...and every business's spread sits on the bank's prime (0.7.7; the city's rate until then), on the dial as it stands this month - and the landlords' insured mortgages are written and renewed at th..._
15. **L11292** `economyManager.updateBusinessCredit(bank.prime(debtManager.getPolicyRate()),` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L643), [Bank.prime](map/Bank.md) (L1135), [DebtManager.getPolicyRate](map/DebtManager.md) (L77), [Bank.insuredMortgageRate](map/Bank.md) (L1156)
16. **L11296** `bondMarket.strikeCoupons();` → [BondMarket.strikeCoupons](map/BondMarket.md) (L965)  
   _...and the bonds' coupons struck on the same face the interest bills just were, by who holds each now (0.7.12)._
17. **L11301** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L812)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
18. **L11305** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L5988)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
19. **L11309** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L5937)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
20. **L11323** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L1681)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
21. **L11324** `double constructionWorkDone = construction.getRecognisedThisMonth();`
22. **L11333** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L1226)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
23. **L11335** `getMarkets().rowDrawsBeforeStrike(getSectors());` → [Game.getMarkets](map/Game.md) (L1686), [Game.getSectors](map/Game.md) (L1681)  
   _...the month's draws before the strike - the railway's diesel, its trucks', bitumen ordered between the presses - back on their sellers' rows the strike just cleared (FW5, 2026-10-25)._
24. **L11337** `settleReserve();` → [Game.settleReserve](map/Game.md) (L4230)  
   _...and the strategic reserve's month, beside the businesses it traded with (0.7.85)._
25. **L11341** `for (Sector s : getSectors().all()) {` → [Game.getSectors](map/Game.md) (L1681)  
   _...and the sales tax a business claimed back on the buildings it bought reached its till at the bank (Sector.bank()) as cash back on them, so the month's building spending is net of it (0.7.19)._
26. **L11347** `economyManager.setCityCrude(reserve.getTonnes(), reserve.getSettledImports(), reserve.getSettledExports());` → [EconomyManager.setCityCrude](map/EconomyManager.md) (L97), [StrategicReserve.getTonnes](map/StrategicReserve.md) (L81), [StrategicReserve.getSettledImports](map/StrategicReserve.md) (L101), [StrategicReserve.getSettledExports](map/StrategicReserve.md) (L102)  
   _The reserve's crude among the goods held, and what it bought and shipped among the trade (0.7.85)._
27. **L11348** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L2100), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L272), [Healthcare.getGrossCost](map/Healthcare.md) (L725), [Crime.getGrossCost](map/Crime.md) (L452), [Education.getGrossCost](map/Education.md) (L839), [EconomyManager.getTransitBill](map/EconomyManager.md) (L2039), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L447), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L449), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L823)
28. **L11402** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
29. **L11403** `monthlyMaterialImportBill = 0;`
30. **L11408** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L2286)  
   _The households' month - and with it the bank's account fees and (0.7.19) its customers, the fees over the fee, which the branch rule in runPrivateInvestment() below reads._
31. **L11416** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L246)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
32. **L11424** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L121)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
33. **L11433** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L72)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
34. **L11437** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L1455)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
35. **L11443** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L2163)  
   _Businesses get their turn: shrink first - where the bank's branches past what this month's fees cover at last month's cost close (0.7.19, runRetirement()) - then look at demand, forecast it forward..._
36. **L11445** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L11447)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 32-127 (96 lines)

1. **L66** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L5918)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L67** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L12640), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L5176)
3. **L75** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L6913), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L5252)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L79** `double overtime = buildingManager.getOvertimePoints();` → [BuildingManager.getOvertimePoints](map/BuildingManager.md) (L5304)  
   _The site output the crews had: what the sites were left after the repairs, and - on the city's rushed sites (0.7.22) - the hours they worked over it, or the hours a tired crew lost._
5. **L80** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), buildingManager.getPointsBuilt(),` → [Game.recogniseSiteWork](map/Game.md) (L7021), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L6378), [BuildingManager.getPointsBuilt](map/BuildingManager.md) (L5268)
6. **L86** `game.settleSiteContracts(buildingManager.takeContractsDue());` → [Game.settleSiteContracts](map/Game.md) (L6961), [BuildingManager.takeContractsDue](map/BuildingManager.md) (L6387)  
   _...and each owner pays the material its work drew at the price it was drawn at, less what its quote allowed (0.7.19): the escalation clause, settled on the same month's work._
7. **L91** `game.settleConstructionControl(buildingManager.takeControlEvents());` → [Game.settleConstructionControl](map/Game.md) (L7580), [BuildingManager.takeControlEvents](map/BuildingManager.md) (L5297)  
   _...and what the player's hand on the queue left (0.7.22): the overtime on the city's rushed sites paid and paid out as wages, a cancelled order's refund, and a finished demolition's material and gr..._
8. **L100** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L167)  
   _Roads, before anything reads them._
9. **L107** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L129)  
   _Recount the posts and refresh the wage arrays._
10. **L123** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L11986)  
   _THE POPULATION STEP._
11. **L125** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L143)
12. **L126** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L207)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 129-141 (13 lines)

1. **L134** `game.strikeBuildersCrews();` → [Game.strikeBuildersCrews](map/Game.md) (L5843)  
   _The builders strike their crews before the posts are counted (0.7.17): the work ahead says how many of their posts they offer, and the labour market fills what is offered._
2. **L135** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L11858)
3. **L136** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L124), [Game.getJobs](map/Game.md) (L11870)
4. **L139** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L13399)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
5. **L140** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L332)

## Game.advanceDemographics() - Game.java lines 11986-12502 (517 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L11991** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L561), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239), [PriceIndex.getIndex](map/PriceIndex.md) (L485)  
   _In REAL terms - see Migration.recordWages()._
2. **L12005** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L12006** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L12533)
4. **L12007** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L12533)
5. **L12008** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L12533)
6. **L12009** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7186)
7. **L12011** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
8. **L12025** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L12026** `java.util.Arrays.fill(affordable, 1);`
10. **L12027** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L12552)
11. **L12030** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L12031** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L12032** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L12035** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L641)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L12038** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L12039** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7186)
17. **L12053** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L12066** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L401)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L12077** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L12078** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L12079** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L12080** `double[] inBand = new double[AgeBand.values().length];`
23. **L12081** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L94)
24. **L12085** `double[] careFactors = mortalityFactors;`
25. **L12086** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L466), [Healthcare.mortalityFactors](map/Healthcare.md) (L401)
26. **L12096** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L12099** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L12106** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L12107** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L94)
30. **L12108** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L413)
31. **L12110** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L164), [Healthcare.birthFactor](map/Healthcare.md) (L420)
32. **L12112** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L270), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L101)
33. **L12116** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L12117** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L104)
35. **L12118** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L101)
36. **L12119** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L106)
37. **L12120** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L12121** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L502), [Healthcare.mortalityFactors](map/Healthcare.md) (L401)
39. **L12124** `lastOrphanDeaths = outsideDead[0];`
40. **L12125** `lastUnhousedDeaths = outsideDead[1];`
41. **L12132** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L368), [Unemployment.takeEvicted](map/Unemployment.md) (L213)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L12135** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L12148** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L94)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L12155** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L906), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L3736), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L12164** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L302), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L443)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L12168** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L343), [Crime.getRateVsCanada](map/Crime.md) (L436)  
   _...and what last month's crime says about living here._
47. **L12173** `migration.setDoors(buildingManager.homesBySize());` → [Migration.setDoors](map/Migration.md) (L788), [BuildingManager.homesBySize](map/BuildingManager.md) (L6701)  
   _...and the doors standing today, which the room the placement has left is asked against (0.7.17): arrivals are bounded by it._
48. **L12175** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L340), [Migration.monthlyNet](map/Migration.md) (L801), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137), [Game.getHouseholdCapacity](map/Game.md) (L11862), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L6680), [PopulationCohorts.share](map/PopulationCohorts.md) (L122)
49. **L12190** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L119), [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
50. **L12202** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L13424)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
51. **L12204** `double[] jobsByTier = new double[PayTier.values().length];`
52. **L12205** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
53. **L12206** `long[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L1118)
54. **L12208** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
55. **L12220** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
56. **L12221** `double[] wageByTier = new double[PayTier.values().length];`
57. **L12222** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L239)
58. **L12223** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
59. **L12226** `for (int t = 0; t < wageByTier.length; t++) {`
60. **L12242** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7262)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
61. **L12243** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L727)
62. **L12244** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L245), [PopulationCohorts.total](map/PopulationCohorts.md) (L115)
63. **L12245** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L287), [PopulationCohorts.total](map/PopulationCohorts.md) (L115), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L7262), [Game.unskilledWage](map/Game.md) (L12797)
64. **L12252** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L239), [Crime.getPressure](map/Crime.md) (L385), [Crime.getAdmitted](map/Crime.md) (L425)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
65. **L12254** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L788), [Crime.prisoners](map/Crime.md) (L370)
66. **L12255** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
67. **L12257** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L1092)
68. **L12258** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L794)
69. **L12259** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L296), [Game.unskilledWage](map/Game.md) (L12797), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
70. **L12263** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L280), [Migration.getLastArrivals](map/Migration.md) (L456), [PopulationCohorts.share](map/PopulationCohorts.md) (L122)  
   _The month's adult arrivals look for work next month._
71. **L12273** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L370)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
72. **L12274** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
73. **L12278** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L3700), [Education.getFinished](map/Education.md) (L898)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
74. **L12290** `long[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L6701)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
75. **L12291** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
76. **L12292** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1297)
77. **L12293** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1384), [FamilyModel.house](map/FamilyModel.md) (L1128)
78. **L12298** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L1681), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
79. **L12302** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L1144)  
   _And the advisor prices a new home on who would move into it._
80. **L12303** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L1149)
81. **L12319** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1631), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L863)  
   _...and the ones who cannot afford one either._
82. **L12321** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1662), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L970), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
83. **L12360** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L158), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
84. **L12361** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L478), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7338), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7427), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L96), [Game.burialShare](map/Game.md) (L12614), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L7149), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L7186)
85. **L12371** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L1975), [Healthcare.getGrossCost](map/Healthcare.md) (L725), [Healthcare.getFees](map/Healthcare.md) (L727)
86. **L12386** `tellTheSchoolsTheirPrices(economyManager.getTaxPolicy());` → [Game.tellTheSchoolsTheirPrices](map/Game.md) (L2311), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's - kind by kind since 0.7.6, each school at its own price._
87. **L12387** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L398), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L7225), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7338), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7427), [Migration.getLastDepartures](map/Migration.md) (L461)
88. **L12406** `for (EducationType kind : EducationType.values()) {` → [Education.setCostOf](map/Education.md) (L873), [BuildingManager.getSchoolPayroll](map/BuildingManager.md) (L7389), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getSchoolUpkeep](map/BuildingManager.md) (L7402)  
   _...AND WHAT EACH KIND OF SCHOOL COST (0.7.6), for the Schools page's row per kind: the same payroll and upkeep the total above was struck from, narrowed to the buildings teaching each course, at th..._
89. **L12413** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L1014), [Education.getGraduates](map/Education.md) (L776)
90. **L12414** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L940), [Education.getLicences](map/Education.md) (L792)
91. **L12417** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L793), [Education.getStudying](map/Education.md) (L585)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
92. **L12420** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L982)  
   _A licence holder is a graduate first, and the graduate count has just moved._
93. **L12422** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L1984), [Education.getGrossCost](map/Education.md) (L839), [Education.getFees](map/Education.md) (L847)
94. **L12428** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L362), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7338), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7427)  
   _6c._
95. **L12432** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L1997), [Crime.getGrossCost](map/Crime.md) (L452)
96. **L12465** `getInfrastructureManager().setCommute(householdBalance.captiveShare(),` → [Game.getInfrastructureManager](map/Game.md) (L1691), [HouseholdBalance.captiveShare](map/HouseholdBalance.md) (L1919), [Motoring.journeyFuel](map/Motoring.md) (L173)  
   _...AT THE PUMP SINCE 0.7.83 (batch O6): the forecourts' price on the sign, and the drivers' litres bought from them (sectors.Retail, THE FORECOURTS)._
97. **L12467** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L223), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
98. **L12468** `motoring.drawFuel(this, getInfrastructureManager().getDrivers() * TaxPolicy.JOURNEYS_A_MONTH);` → [Motoring.drawFuel](map/Motoring.md) (L198), [Game.getInfrastructureManager](map/Game.md) (L1691)
99. **L12472** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L2035), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L7338), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L7427), [Game.portsBill](map/Game.md) (L5959), [Game.getInfrastructureManager](map/Game.md) (L1691), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _...AND THE PORTS' REPAIRS (0.7.86, batch O9; the dockers left it in FR3 for Freight's payroll): a terminal's upkeep added to the bill, nothing with no terminal (star O9-2)._
100. **L12484** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L752), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L3839), [Game.unhousedShareOfCity](map/Game.md) (L12515), [Crime.getInjuredShare](map/Crime.md) (L416)  
   _7._
101. **L12500** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 143-205 (63 lines)

1. **L146** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L137)  
   _TBE_
2. **L147** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)
3. **L148** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L6745)
4. **L149** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L507), [Game.getFamilies](map/Game.md) (L11958)
5. **L155** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L504), [Game.getFamilies](map/Game.md) (L11958)  
   _The two inputs to the rent price, which is a market now._
6. **L158** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L508), [Game.getFamilies](map/Game.md) (L11958)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L162** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L505), [Game.marginalHousingCost](map/Game.md) (L16291)
8. **L164** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L2056), [Game.getCohorts](map/Game.md) (L11957)
9. **L170** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L108), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L171** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L120), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L7515)
11. **L174** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L165)
12. **L181** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L102), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L213)  
   _The split behind that total._
13. **L182** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L164), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L280)
14. **L183** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L165), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L284)
15. **L198** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L167), [Game.getInfrastructureManager](map/Game.md) (L1691)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L201** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L195), [Game.getHealth](map/Game.md) (L11961)  
   _The fourth ratio._
17. **L202** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L1672)

## SimulationEngine.updateServices() - SimulationEngine.java lines 207-214 (8 lines)

1. **L208** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L246), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L197)
2. **L209** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L264), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L1059)
3. **L212** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L321), [PopulationManager.getPopulation](map/PopulationManager.md) (L141)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L213** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L48)

## Game.run() - Game.java lines 675-679 (5 lines)

1. **L676** `initialize();` → [Game.initialize](map/Game.md) (L681)
2. **L677** `foundingBank();` → [Game.foundingBank](map/Game.md) (L812)
3. **L678** `foundingYard();` → [Game.foundingYard](map/Game.md) (L896)

