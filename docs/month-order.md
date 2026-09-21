# The order the month runs in

Generated 2026-09-21 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 3571-4118 (548 lines)

1. **L3572** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L4647)
2. **L3574** `if (cash < 0) {` → [Game.issueEmergencyDebt](map/Game.md) (L6635)
3. **L3614** `month++;`
4. **L3615** `monthsSinceAutosave++;`
5. **L3618** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L271)  
   _The register's month: nothing offered, nothing paid, until it is._
6. **L3619** `exchange.startMonth();` → [Exchange.startMonth](map/Exchange.md) (L358)
7. **L3620** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L728)
8. **L3622** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L5956)
9. **L3639** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
10. **L3640** `cityDebtRaisedThisMonth = 0;`
11. **L3641** `cityDiscountThisMonth = 0;`
12. **L3642** `cityPrincipalRepaidThisMonth = 0;`
13. **L3643** `foreignDebtRaisedThisMonth = 0;`
14. **L3644** `foreignPrincipalRepaidThisMonth = 0;`
15. **L3645** `foreignInterestPaidThisMonth = 0;`
16. **L3650** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
17. **L3651** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
18. **L3652** `sectorInvested.clear();`
19. **L3653** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L277)
20. **L3654** `double pooledBefore = 0;`
21. **L3655** `for (double p : poolsBefore) pooledBefore += p;`
22. **L3656** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L976)
23. **L3658** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L497)
24. **L3659** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L949)
25. **L3677** `economyManager.setBankTax(bank.chargeTax(` → [EconomyManager.setBankTax](map/EconomyManager.md) (L793), [Bank.chargeTax](map/Bank.md) (L1929), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54), [Game.getSectors](map/Game.md) (L802)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
26. **L3679** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`
27. **L3680** `cityDiscountForBank = cityDiscountThisMonth;`
28. **L3682** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L4120)
29. **L3683** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L30)
30. **L3684** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L4597)
31. **L3685** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L1196)
32. **L3686** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L4556)
33. **L3687** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L712)
34. **L3691** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L4576)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
35. **L3702** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
36. **L3703** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank);` → [Bank.lend](map/Bank.md) (L1223)
37. **L3704** `bank.takeRepayment(lender.getRepaidThisMonth() + cityPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L1217)
38. **L3710** `bank.takeDiscount(cityDiscountForBank);` → [Bank.takeDiscount](map/Bank.md) (L1210)  
   _The discount on this month's issuance, recognised as it is earned._
39. **L3712** `double sectorInterestPaid = 0;`
40. **L3713** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L802)
41. **L3714** `bank.takeInterest(interestDue + sectorInterestPaid);` → [Bank.takeInterest](map/Bank.md) (L1191)
42. **L3716** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1525)
43. **L3735** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue);` → [Bank.payRunning](map/Bank.md) (L1255), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L123)  
   _Its own staff, on its own books._
44. **L3736** `bankMaintenanceDue = 0;`
45. **L3744** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L1395), [Bank.openBranches](map/Bank.md) (L1123), [BuildingManager.countByName](map/BuildingManager.md) (L2973)  
   _Capital for whatever branches opened this month._
46. **L3778** `double carryRate = bank.lendingRate(debtManager.getRate());` → [Bank.lendingRate](map/Bank.md) (L411), [DebtManager.getRate](map/DebtManager.md) (L635)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
47. **L3779** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L411), [DebtManager.countryPremium](map/DebtManager.md) (L506), [Bank.headroom](map/Bank.md) (L2114)
48. **L3785** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L363)
49. **L3786** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L371)
50. **L3787** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L387), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L464)
51. **L3791** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1525)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, the premium, and what funding costs._
52. **L3809** `final double cityRateNow = debtManager.getRate();` → [DebtManager.getRate](map/DebtManager.md) (L635)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE, handed to the bank as a function rather than as five numbers._
53. **L3810** `final double premiumNow = debtManager.countryPremium();` → [DebtManager.countryPremium](map/DebtManager.md) (L506)
54. **L3811** `final double gdpNow = economyManager.getMonthGdp();` → [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1191)
55. **L3812** `bank.setDepositMarket(rate -> hotMoney.arrivalsAt(` → [Bank.setDepositMarket](map/Bank.md) (L1588), [CapitalFlows.arrivalsAt](map/CapitalFlows.md) (L260)
56. **L3815** `bank.fundToCover(debtManager.getRate());` → [Bank.fundToCover](map/Bank.md) (L1421), [DebtManager.getRate](map/DebtManager.md) (L635)
57. **L3819** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L874)  
   _...and if that left it owing more than it owns, it has failed._
58. **L3823** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L1815)  
   _The month is final, so the figure next month's tax is charged on is final too._
59. **L3840** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L4541)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
60. **L3841** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L948)
61. **L3851** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L2673), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L1363)  
   _...AND THE SAVERS ARE PAID._
62. **L3852** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L1364)
63. **L3859** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L696)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
64. **L3860** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L362), [EconomyManager.setSectorCash](map/EconomyManager.md) (L367), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L698)
65. **L3891** `payDividends();` → [Game.payDividends](map/Game.md) (L1415)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
66. **L3892** `tradeShares();` → [Game.tradeShares](map/Game.md) (L1460)
67. **L3894** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L169), [Bank.depositRate](map/Bank.md) (L1361), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L565)
68. **L3898** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L2384), [Bank.depositRate](map/Bank.md) (L1361), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L565)  
   _...and the households, by the same rule, with what the owners were just paid._
69. **L3933** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L490)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
70. **L3934** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L520)
71. **L3935** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L278), [Bank.depositRate](map/Bank.md) (L1361), [DebtManager.getRate](map/DebtManager.md) (L635), [DebtManager.countryPremium](map/DebtManager.md) (L506), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1191), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L895), [Game.yearlyDepreciation](map/Game.md) (L5697), [Bank.isInsolvent](map/Bank.md) (L805), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L580)
72. **L3955** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L490)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
73. **L3956** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L693)
74. **L3957** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L707)
75. **L3958** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L681), [CapitalFlows.getStock](map/CapitalFlows.md) (L490)
76. **L3960** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L309)
77. **L3970** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L741), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1191)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
78. **L3992** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L251), [PriceIndex.getIndex](map/PriceIndex.md) (L179), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L267), [PriceIndex.inflation](map/PriceIndex.md) (L196), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L312)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
79. **L4002** `foreign.setRateDifferential(` → [ForeignAccounts.setRateDifferential](map/ForeignAccounts.md) (L421), [DebtManager.getPolicyRate](map/DebtManager.md) (L49)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD._
80. **L4004** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L508)
81. **L4019** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L303), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L565)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
82. **L4020** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L832), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L319), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L565)
83. **L4027** `foreign.revalueVault();` → [ForeignAccounts.revalueVault](map/ForeignAccounts.md) (L1034)  
   _...AND THE VAULT IS WORTH WHAT IT IS WORTH, the same line in the same place (2026-09-21): the vault is kept in dollars, so the reprice just moved its local value, and this books the move as a reval..._
84. **L4028** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L414), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L718), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L699)
85. **L4029** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L573)
86. **L4030** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L3382)
87. **L4041** `priceIndex.takeMonth(` → [PriceIndex.takeMonth](map/PriceIndex.md) (L102), [Game.getSectors](map/Game.md) (L802)  
   _...AND WHAT THE MONTH COST A FAMILY._
88. **L4056** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L565)  
   _A year of the rate, so next year can tell a drift from a run._
89. **L4057** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
90. **L4059** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L6417)
91. **L4061** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L377)
92. **L4095** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
93. **L4096** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L277)
94. **L4115** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L6545)
95. **L4116** `recordMonth();` → [Game.recordMonth](map/Game.md) (L5904)

## Game.startOfMonthUpdate() - Game.java lines 4120-4341 (222 lines)

1. **L4160** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L237)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L4161** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L230), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L565), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L267)
3. **L4171** `labourMarket.updateCostOfLiving(priceIndex.getIndex());` → [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L322), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _...and wages start chasing what the world now charges._
4. **L4189** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L191), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _what the next parcel costs, are both inputs to everything below._
5. **L4191** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L247), [LandManager.getPricePerSqFt](map/LandManager.md) (L164)
6. **L4200** `debtManager.setBankPremium(bank.ratePremium());` → [DebtManager.setBankPremium](map/DebtManager.md) (L648), [Bank.ratePremium](map/Bank.md) (L1177)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L4208** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L4541)  
   _...AND WHAT THE MONEY COSTS AT ALL, which is a different question from how strained the lender is._
8. **L4212** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L1939), [Bank.isInsolvent](map/Bank.md) (L805)  
   _A failed bank lends nothing._
9. **L4214** `economyManager.updateBusinessCredit(debtManager.getRate());` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L381), [DebtManager.getRate](map/DebtManager.md) (L635)
10. **L4219** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L448)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
11. **L4223** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L2274)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
12. **L4227** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L2243)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
13. **L4241** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L802)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
14. **L4242** `double constructionWorkDone = construction.getRecognisedThisMonth();`
15. **L4251** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L575)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
16. **L4253** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L1000), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L211), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Crime.getGrossCost](map/Crime.md) (L449), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L166), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L168), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L459)
17. **L4302** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
18. **L4303** `monthlyMaterialImportBill = 0;`
19. **L4305** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L945)
20. **L4313** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L91)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
21. **L4321** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L114)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
22. **L4330** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L68)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
23. **L4334** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L679)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
24. **L4338** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L869)  
   _Businesses get their turn: look at demand, forecast it forward, and expand if the new capacity would carry its own debt._
25. **L4340** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L4342)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 30-107 (78 lines)

1. **L63** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L2224)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L64** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L5401), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L2903)
3. **L70** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L2626), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L2936)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L71** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), siteOutput);` → [Game.recogniseSiteWork](map/Game.md) (L2650), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L2945)
5. **L80** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L122)  
   _Roads, before anything reads them._
6. **L87** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L109)  
   _Recount the posts and refresh the wage arrays._
7. **L103** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L4793)  
   _THE POPULATION STEP._
8. **L105** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L118)
9. **L106** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L182)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 109-116 (8 lines)

1. **L110** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L4669)
2. **L111** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L122), [Game.getJobs](map/Game.md) (L4681)
3. **L114** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L5819)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
4. **L115** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L302)

## Game.advanceDemographics() - Game.java lines 4793-5263 (471 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L4798** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L526), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _In REAL terms - see Migration.recordWages()._
2. **L4812** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L4813** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L5294)
4. **L4814** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L5294)
5. **L4815** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L5294)
6. **L4816** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)
7. **L4818** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
8. **L4832** `double[] affordable = new double[CareType.values().length];`  
   _...AND WHAT THE PRICE AT THE DOOR TAKES OFF IT (2026-09-19)._
9. **L4833** `java.util.Arrays.fill(affordable, 1);`
10. **L4834** `for (CareType care : CareType.values()) {` → [Game.careAffordability](map/Game.md) (L5313)
11. **L4837** `childcareCoverage *= affordable[CareType.CHILDCARE.ordinal()];`
12. **L4838** `seniorCoverage    *= affordable[CareType.SENIOR.ordinal()];`
13. **L4839** `generalCoverage   *= affordable[CareType.GENERAL.ordinal()];`
14. **L4842** `healthcare.noteCoverage(childcareCoverage, generalCoverage, seniorCoverage);` → [Healthcare.noteCoverage](map/Healthcare.md) (L627)  
   _...and the service keeps the three, so a screen shows the coverage the month read rather than one it worked out from the beds._
15. **L4845** `double[] served = new double[CareType.values().length];`  
   _What the beds could do; the service takes the priced-out off it._
16. **L4846** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)
17. **L4860** `generalCapacity = served[CareType.GENERAL.ordinal()]`  
   _...AND WHAT THE SICK RATE READS IS THE PEOPLE TREATED, not the beds built: the people the beds could take, less the ones the fee turned away, over the people._
18. **L4873** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L388)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
19. **L4884** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
20. **L4885** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
21. **L4886** `double[] orphansByBand = new double[AgeBand.values().length];`
22. **L4887** `double[] inBand = new double[AgeBand.values().length];`
23. **L4888** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
24. **L4892** `double[] careFactors = mortalityFactors;`
25. **L4893** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L429), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
26. **L4903** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L298)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
27. **L4906** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
28. **L4913** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
29. **L4914** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
30. **L4915** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L410)
31. **L4917** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L144), [Healthcare.birthFactor](map/Healthcare.md) (L407)
32. **L4919** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L258), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
33. **L4923** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
34. **L4924** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L90)
35. **L4925** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
36. **L4926** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L92)
37. **L4927** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
38. **L4928** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L465), [Healthcare.mortalityFactors](map/Healthcare.md) (L388)
39. **L4931** `lastOrphanDeaths = outsideDead[0];`
40. **L4932** `lastUnhousedDeaths = outsideDead[1];`
41. **L4939** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L342), [Unemployment.takeEvicted](map/Unemployment.md) (L208)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
42. **L4942** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
43. **L4955** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
44. **L4962** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L803), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L2553), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
45. **L4971** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L299), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L332)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
46. **L4975** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L340), [Crime.getRateVsCanada](map/Crime.md) (L433)  
   _...and what last month's crime says about living here._
47. **L4977** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L314), [Migration.monthlyNet](map/Migration.md) (L720), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135), [Game.getHouseholdCapacity](map/Game.md) (L4673), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L3193), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)
48. **L4992** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L117), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
49. **L5004** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L5844)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
50. **L5006** `double[] jobsByTier = new double[PayTier.values().length];`
51. **L5007** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
52. **L5008** `int[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L814)
53. **L5010** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
54. **L5022** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
55. **L5023** `double[] wageByTier = new double[PayTier.values().length];`
56. **L5024** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209)
57. **L5025** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
58. **L5028** `for (int t = 0; t < wageByTier.length; t++) {`
59. **L5044** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3667)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
60. **L5045** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L587)
61. **L5046** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L242), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
62. **L5047** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L284), [PopulationCohorts.total](map/PopulationCohorts.md) (L95), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3667), [Game.unskilledWage](map/Game.md) (L5540)
63. **L5054** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L234), [Crime.getPressure](map/Crime.md) (L382), [Crime.getAdmitted](map/Crime.md) (L422)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
64. **L5056** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L487), [Crime.prisoners](map/Crime.md) (L367)
65. **L5057** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
66. **L5059** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L788)
67. **L5060** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L493)
68. **L5061** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L291), [Game.unskilledWage](map/Game.md) (L5540), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
69. **L5065** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L275), [Migration.getLastArrivals](map/Migration.md) (L435), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)  
   _The month's adult arrivals look for work next month._
70. **L5075** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L367)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
71. **L5076** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
72. **L5080** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L2538), [Education.getFinished](map/Education.md) (L757)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
73. **L5092** `int[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L3214)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
74. **L5093** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
75. **L5094** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1278)
76. **L5095** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1365), [FamilyModel.house](map/FamilyModel.md) (L1128)
77. **L5100** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L802), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
78. **L5104** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L765)  
   _And the advisor prices a new home on who would move into it._
79. **L5105** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L770)
80. **L5121** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1492), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L739)  
   _...and the ones who cannot afford one either._
81. **L5123** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1523), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L790), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
82. **L5129** `economyManager.setOutsidePayments(unemployment.getBenefitsPaid(), studentGrantBill());` → [EconomyManager.setOutsidePayments](map/EconomyManager.md) (L876), [Unemployment.getBenefitsPaid](map/Unemployment.md) (L183), [Game.studentGrantBill](map/Game.md) (L5564)  
   _What EI and the grants cost the treasury this month, before the cash moves._
83. **L5161** `healthcare.setFeeScale(economyManager.getTaxPolicy().getHealthFeeScale());` → [Healthcare.setFeeScale](map/Healthcare.md) (L145), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's fee scale (2026-09-19), and with the share of each kind of care's people who could pay - see Healthcare.advanceMonth()._
84. **L5162** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L465), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L82), [Game.burialShare](map/Game.md) (L5375), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L3561), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)
85. **L5172** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L893), [Healthcare.getGrossCost](map/Healthcare.md) (L711), [Healthcare.getFees](map/Healthcare.md) (L713)
86. **L5186** `education.setTuitionScale(economyManager.getTaxPolicy().getTuitionScale());` → [Education.setTuitionScale](map/Education.md) (L132), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _At the player's tuition scale (2026-09-21), told here as the clinic's scale is above, so the fees this step charges are this month's._
87. **L5187** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L338), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L3631), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [Migration.getLastDepartures](map/Migration.md) (L440)
88. **L5200** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L710), [Education.getGraduates](map/Education.md) (L695)
89. **L5201** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L636), [Education.getLicences](map/Education.md) (L698)
90. **L5204** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L492), [Education.getStudying](map/Education.md) (L505)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
91. **L5207** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L678)  
   _A licence holder is a graduate first, and the graduate count has just moved._
92. **L5209** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L902), [Education.getGrossCost](map/Education.md) (L738), [Education.getFees](map/Education.md) (L746)
93. **L5215** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L359), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832)  
   _6c._
94. **L5219** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L915), [Crime.getGrossCost](map/Crime.md) (L449)
95. **L5233** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L162), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _6d._
96. **L5234** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L940), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [Game.getInfrastructureManager](map/Game.md) (L812), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
97. **L5245** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L738), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L2645), [Game.unhousedShareOfCity](map/Game.md) (L5276), [Crime.getInjuredShare](map/Crime.md) (L413)  
   _7._
98. **L5261** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L298)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 118-180 (63 lines)

1. **L121** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135)  
   _TBE_
2. **L122** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)
3. **L123** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L3251)
4. **L124** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L272), [Game.getFamilies](map/Game.md) (L4765)
5. **L130** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L269), [Game.getFamilies](map/Game.md) (L4765)  
   _The two inputs to the rent price, which is a market now._
6. **L133** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L273), [Game.getFamilies](map/Game.md) (L4765)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L137** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L270), [Game.marginalHousingCost](map/Game.md) (L6734)
8. **L139** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L958), [Game.getCohorts](map/Game.md) (L4764)
9. **L145** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L93), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L146** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L105), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L3903)
11. **L149** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L163)
12. **L156** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L87), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L188)  
   _The split behind that total._
13. **L157** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L149), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L219)
14. **L158** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L150), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L223)
15. **L173** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L152), [Game.getInfrastructureManager](map/Game.md) (L812)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L176** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L180), [Game.getHealth](map/Game.md) (L4768)  
   _The fourth ratio._
17. **L177** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L758)

## SimulationEngine.updateServices() - SimulationEngine.java lines 182-189 (8 lines)

1. **L183** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L185), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172)
2. **L184** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L203), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
3. **L187** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L260), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L188** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L33)

## Game.run() - Game.java lines 340-343 (4 lines)

1. **L341** `initialize();` → [Game.initialize](map/Game.md) (L345)
2. **L342** `foundingBank();` → [Game.foundingBank](map/Game.md) (L436)

