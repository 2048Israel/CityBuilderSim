# The order the month runs in

Generated 2026-09-18 by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.

`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.

## Game.nextMonth() - Game.java lines 3434-3974 (541 lines)

1. **L3435** `updateConstructionCost();` → [Game.updateConstructionCost](map/Game.md) (L4494)
2. **L3437** `if (cash < 0) {` → [Game.issueEmergencyDebt](map/Game.md) (L6249)
3. **L3477** `month++;`
4. **L3478** `monthsSinceAutosave++;`
5. **L3481** `equity.startMonth();` → [Equity.startMonth](map/Equity.md) (L271)  
   _The register's month: nothing offered, nothing paid, until it is._
6. **L3482** `exchange.startMonth();` → [Exchange.startMonth](map/Exchange.md) (L358)
7. **L3483** `economyManager.clearEquityFlows();` → [EconomyManager.clearEquityFlows](map/EconomyManager.md) (L728)
8. **L3485** `if (monthsSinceAutosave >= AUTOSAVE_MONTHS) {` → [Game.autosave](map/Game.md) (L5643)
9. **L3502** `treasuryOpening = treasuryRecorded ? treasuryClosing : cash;`  
   _THE WINDOW IS PRESS TO PRESS, not tick to tick._
10. **L3503** `cityDebtRaisedThisMonth = 0;`
11. **L3504** `cityDiscountThisMonth = 0;`
12. **L3505** `cityPrincipalRepaidThisMonth = 0;`
13. **L3506** `foreignDebtRaisedThisMonth = 0;`
14. **L3507** `foreignPrincipalRepaidThisMonth = 0;`
15. **L3508** `foreignInterestPaidThisMonth = 0;`
16. **L3513** `cityInterestPaid = 0;`  
   _Its domestic twin, cleared in the same breath._
17. **L3514** `economyManager.getBusinessDebtManager().startAuditMonth();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)
18. **L3515** `sectorInvested.clear();`
19. **L3516** `double[] poolsBefore = MoneyAudit.pools(this);` → [MoneyAudit.pools](map/MoneyAudit.md) (L277)
20. **L3517** `double pooledBefore = 0;`
21. **L3518** `for (double p : poolsBefore) pooledBefore += p;`
22. **L3519** `double interestDue = economyManager.getInterestAccrued();` → [EconomyManager.getInterestAccrued](map/EconomyManager.md) (L945)
23. **L3521** `bank.startMonth();` → [Bank.startMonth](map/Bank.md) (L497)
24. **L3522** `foreign.startMonth();` → [ForeignAccounts.startMonth](map/ForeignAccounts.md) (L841)
25. **L3540** `economyManager.setBankTax(bank.chargeTax(` → [EconomyManager.setBankTax](map/EconomyManager.md) (L793), [Bank.chargeTax](map/Bank.md) (L1912), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54), [Game.getSectors](map/Game.md) (L737)  
   _THE BANK PAYS ITS PROFIT TAX, on the month that has just finished._
26. **L3542** `cityDebtRaisedForBank = cityDebtRaisedThisMonth;`
27. **L3543** `cityDiscountForBank = cityDiscountThisMonth;`
28. **L3545** `startOfMonthUpdate();` → [Game.startOfMonthUpdate](map/Game.md) (L3976)
29. **L3546** `simulationEngine.simulateMonth(this);` → [SimulationEngine.simulateMonth](map/SimulationEngine.md) (L30)
30. **L3547** `finalUpdateEconomy();` → [Game.finalUpdateEconomy](map/Game.md) (L4453)
31. **L3548** `economyManager.setPreviousGdp(historySave);` → [EconomyManager.setPreviousGdp](map/EconomyManager.md) (L1163)
32. **L3549** `priceTheDebtMarket();` → [Game.priceTheDebtMarket](map/Game.md) (L4412)
33. **L3550** `debtManager.processAllDebts(this);` → [DebtManager.processAllDebts](map/DebtManager.md) (L683)
34. **L3554** `strikeGovernmentBooks();` → [Game.strikeGovernmentBooks](map/Game.md) (L4432)  
   _The government's books, over the same window as the treasury bridge and after the month's coupon has actually been paid._
35. **L3565** `BusinessDebtManager lender = economyManager.getBusinessDebtManager();` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53)  
   _THE BANK SETTLES, BEFORE THE AUDIT LOOKS._
36. **L3566** `bank.lend(lender.getLentThisMonth() + cityDebtRaisedForBank);` → [Bank.lend](map/Bank.md) (L1206)
37. **L3567** `bank.takeRepayment(lender.getRepaidThisMonth() + cityPrincipalRepaidThisMonth);` → [Bank.takeRepayment](map/Bank.md) (L1200)
38. **L3573** `bank.takeDiscount(cityDiscountForBank);` → [Bank.takeDiscount](map/Bank.md) (L1193)  
   _The discount on this month's issuance, recognised as it is earned._
39. **L3575** `double sectorInterestPaid = 0;`
40. **L3576** `for (Sector s : getSectors().all()) sectorInterestPaid += s.statement().interest;` → [Game.getSectors](map/Game.md) (L737)
41. **L3577** `bank.takeInterest(interestDue + sectorInterestPaid);` → [Bank.takeInterest](map/Bank.md) (L1174)
42. **L3579** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1405)
43. **L3598** `bank.payRunning(economyManager.getBankPayroll(), bankMaintenanceDue);` → [Bank.payRunning](map/Bank.md) (L1238), [EconomyManager.getBankPayroll](map/EconomyManager.md) (L123)  
   _Its own staff, on its own books._
44. **L3599** `bankMaintenanceDue = 0;`
45. **L3607** `capitaliseBank(bank.openBranches(buildingManager.countByName("Commercial Bank")));` → [Game.capitaliseBank](map/Game.md) (L1275), [Bank.openBranches](map/Bank.md) (L1106), [BuildingManager.countByName](map/BuildingManager.md) (L2973)  
   _Capital for whatever branches opened this month._
46. **L3641** `double carryRate = bank.lendingRate(debtManager.getRate());` → [Bank.lendingRate](map/Bank.md) (L411), [DebtManager.getRate](map/DebtManager.md) (L606)  
   _================================================================= AND THE MONEY THAT LEAVES BECAUSE THE RATE IS BAD._
47. **L3642** `double carryMoved = hotMoney.carryTakeMonth(` → [CapitalFlows.carryTakeMonth](map/CapitalFlows.md) (L411), [DebtManager.countryPremium](map/DebtManager.md) (L477), [Bank.headroom](map/Bank.md) (L2097)
48. **L3648** `if (carryMoved > 0)      bank.lendCarry(carryMoved);` → [Bank.lendCarry](map/Bank.md) (L363)
49. **L3649** `else if (carryMoved < 0) bank.repayCarry(-carryMoved);` → [Bank.repayCarry](map/Bank.md) (L371)
50. **L3650** `bank.takeCarryInterest(hotMoney.carryInterestOn(carryRate));` → [Bank.takeCarryInterest](map/Bank.md) (L387), [CapitalFlows.carryInterestOn](map/CapitalFlows.md) (L464)
51. **L3654** `refreshBank();` → [Game.refreshBank](map/Game.md) (L1405)  
   _The book just moved, so the capacity every line below reads has to be the one that includes it - strain, the premium, and what funding costs._
52. **L3672** `final double cityRateNow = debtManager.getRate();` → [DebtManager.getRate](map/DebtManager.md) (L606)  
   _WHAT THE WORLD WOULD PAY TO PARK HERE, handed to the bank as a function rather than as five numbers._
53. **L3673** `final double premiumNow = debtManager.countryPremium();` → [DebtManager.countryPremium](map/DebtManager.md) (L477)
54. **L3674** `final double gdpNow = economyManager.getMonthGdp();` → [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1158)
55. **L3675** `bank.setDepositMarket(rate -> hotMoney.arrivalsAt(` → [Bank.setDepositMarket](map/Bank.md) (L1571), [CapitalFlows.arrivalsAt](map/CapitalFlows.md) (L260)
56. **L3678** `bank.fundToCover(debtManager.getRate());` → [Bank.fundToCover](map/Bank.md) (L1404), [DebtManager.getRate](map/DebtManager.md) (L606)
57. **L3682** `bank.resolveIfFailed();` → [Bank.resolveIfFailed](map/Bank.md) (L857)  
   _...and if that left it owing more than it owns, it has failed._
58. **L3686** `bank.closeMonth();` → [Bank.closeMonth](map/Bank.md) (L1798)  
   _The month is final, so the figure next month's tax is charged on is final too._
59. **L3703** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L4397)  
   _...AND THE PRICE OF MONEY IS FINAL WITH IT._
60. **L3704** `debtManager.updateInterest();` → [DebtManager.updateInterest](map/DebtManager.md) (L919)
61. **L3714** `householdBalance.creditDepositInterest(bank.getDepositInterestToHouseholds());` → [HouseholdBalance.creditDepositInterest](map/HouseholdBalance.md) (L2529), [Bank.getDepositInterestToHouseholds](map/Bank.md) (L1346)  
   _...AND THE SAVERS ARE PAID._
62. **L3715** `double sectorInterest = bank.getDepositInterestToSectors();` → [Bank.getDepositInterestToSectors](map/Bank.md) (L1347)
63. **L3722** `economyManager.clearDepositInterest();` → [EconomyManager.clearDepositInterest](map/EconomyManager.md) (L696)  
   _Cleared before it is handed out, not after, so a month in which the bank pays nothing leaves zeroes behind rather than last month's figures._
64. **L3723** `if (sectorInterest > 0) {` → [EconomyManager.getSectorCash](map/EconomyManager.md) (L362), [EconomyManager.setSectorCash](map/EconomyManager.md) (L367), [EconomyManager.recordDepositInterest](map/EconomyManager.md) (L698)
65. **L3754** `payDividends();` → [Game.payDividends](map/Game.md) (L1295)  
   _...AND THE OWNERS ARE PAID, before the sectors decide where to keep what is left._
66. **L3755** `tradeShares();` → [Game.tradeShares](map/Game.md) (L1340)
67. **L3757** `outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE,` → [OutwardInvestment.takeMonth](map/OutwardInvestment.md) (L169), [Bank.depositRate](map/Bank.md) (L1344), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L500)
68. **L3761** `householdBalance.investAbroad(bank.depositRate(), DebtManager.WORLD_BASE_RATE, foreign.getRate());` → [HouseholdBalance.investAbroad](map/HouseholdBalance.md) (L2254), [Bank.depositRate](map/Bank.md) (L1344), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L500)  
   _...and the households, by the same rule, with what the owners were just paid._
69. **L3796** `double hotBefore = hotMoney.getStock();` → [CapitalFlows.getStock](map/CapitalFlows.md) (L490)  
   _================================================================= AND THE MONEY THAT IS HERE BECAUSE THE RATE IS GOOD._
70. **L3797** `hotMoney.setMonth(month);` → [CapitalFlows.setMonth](map/CapitalFlows.md) (L520)
71. **L3798** `hotMoney.takeMonth(` → [CapitalFlows.takeMonth](map/CapitalFlows.md) (L278), [Bank.depositRate](map/Bank.md) (L1344), [DebtManager.getRate](map/DebtManager.md) (L606), [DebtManager.countryPremium](map/DebtManager.md) (L477), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1158), [ForeignAccounts.getReserves](map/ForeignAccounts.md) (L787), [Game.yearlyDepreciation](map/Game.md) (L5384), [Bank.isInsolvent](map/Bank.md) (L788), [DebtManager.getMonthsSinceForeignDefault](map/DebtManager.md) (L551)
72. **L3818** `double hotMoved = hotMoney.getStock() - hotBefore;` → [CapitalFlows.getStock](map/CapitalFlows.md) (L490)  
   _THE MONEY MOVES FOR REAL, and now it moves where the audit can see it._
73. **L3819** `if (hotMoved > 0)      bank.receiveHotMoney(hotMoved);` → [Bank.receiveHotMoney](map/Bank.md) (L676)
74. **L3820** `else if (hotMoved < 0) bank.returnHotMoney(-hotMoved);` → [Bank.returnHotMoney](map/Bank.md) (L690)
75. **L3821** `bank.setForeignDeposits(hotMoney.getStock());` → [Bank.setForeignDeposits](map/Bank.md) (L664), [CapitalFlows.getStock](map/CapitalFlows.md) (L490)
76. **L3823** `lastMoneyAudit = MoneyAudit.strike(this, pooledBefore, poolsBefore, interestDue);` → [MoneyAudit.strike](map/MoneyAudit.md) (L309)
77. **L3833** `foreign.takeMonth(lastMoneyAudit, economyManager.getMonthGdp());` → [ForeignAccounts.takeMonth](map/ForeignAccounts.md) (L637), [EconomyManager.getMonthGdp](map/EconomyManager.md) (L1158)  
   _...AND THE SAME MONTH, READ AS A BALANCE OF PAYMENTS._
78. **L3855** `foreign.setParity(priceIndex.getIndex(), world.getPriceLevel(),` → [ForeignAccounts.setParity](map/ForeignAccounts.md) (L224), [PriceIndex.getIndex](map/PriceIndex.md) (L179), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L267), [PriceIndex.inflation](map/PriceIndex.md) (L196), [WorldEconomy.realisedInflation](map/WorldEconomy.md) (L312)  
   _THE SAME INSTRUMENT FOR BOTH HALVES._
79. **L3865** `foreign.setRateDifferential(` → [ForeignAccounts.setRateDifferential](map/ForeignAccounts.md) (L390), [DebtManager.getPolicyRate](map/DebtManager.md) (L49)  
   _...AND WHAT THE CITY IS PAYING TO BORROW, AGAINST THE WORLD._
80. **L3867** `foreign.repriceCurrency();` → [ForeignAccounts.repriceCurrency](map/ForeignAccounts.md) (L443)
81. **L3882** `debtManager.setExchangeRate(foreign.getRate());` → [DebtManager.setExchangeRate](map/DebtManager.md) (L275), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L500)  
   _...AND THE CITY'S DOLLAR DEBT IS WORTH WHAT IT IS WORTH._
82. **L3883** `foreign.takeForeignDebt(debtManager.getForeignPrincipalUsd(), foreign.getRate());` → [ForeignAccounts.takeForeignDebt](map/ForeignAccounts.md) (L728), [DebtManager.getForeignPrincipalUsd](map/DebtManager.md) (L291), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L500)
83. **L3884** `debtManager.setTrade(foreign.monthlyExports(), foreign.importCover());` → [DebtManager.setTrade](map/DebtManager.md) (L386), [ForeignAccounts.monthlyExports](map/ForeignAccounts.md) (L614), [ForeignAccounts.importCover](map/ForeignAccounts.md) (L603)
84. **L3885** `debtManager.ageForeignStanding();` → [DebtManager.ageForeignStanding](map/DebtManager.md) (L544)
85. **L3886** `checkForeignSolvency();` → [Game.checkForeignSolvency](map/Game.md) (L3246)
86. **L3897** `priceIndex.takeMonth(` → [PriceIndex.takeMonth](map/PriceIndex.md) (L102), [Game.getSectors](map/Game.md) (L737)  
   _...AND WHAT THE MONTH COST A FAMILY._
87. **L3912** `rateHistory[month % 12] = foreign.getRate();` → [ForeignAccounts.getRate](map/ForeignAccounts.md) (L500)  
   _A year of the rate, so next year can tell a drift from a run._
88. **L3913** `if (rateHistoryFilled < 12) rateHistoryFilled++;`
89. **L3915** `takeTreasuryMonth();` → [Game.takeTreasuryMonth](map/Game.md) (L6069)
90. **L3917** `dataSave.setCash(cash);` → [DataSave.setCash](map/DataSave.md) (L337)
91. **L3951** `postAuditDrift = 0;`  
   _================================================================= NOTHING AFTER THE AUDIT MAY MOVE A POOL._
92. **L3952** `if (lastMoneyAudit != null && lastMoneyAudit.poolsAtClose != null) {` → [MoneyAudit.pools](map/MoneyAudit.md) (L277)
93. **L3971** `printEndOfTurn();` → [Game.printEndOfTurn](map/Game.md) (L6162)
94. **L3972** `recordMonth();` → [Game.recordMonth](map/Game.md) (L5591)

## Game.startOfMonthUpdate() - Game.java lines 3976-4197 (222 lines)

1. **L4016** `world.advanceMonth(month);` → [WorldEconomy.advanceMonth](map/WorldEconomy.md) (L237)  
   _THE RATE TIMES THE WORLD'S OWN PRICE LEVEL._
2. **L4017** `economyManager.setExchangeRate(foreign.getRate() * world.getPriceLevel());` → [EconomyManager.setExchangeRate](map/EconomyManager.md) (L230), [ForeignAccounts.getRate](map/ForeignAccounts.md) (L500), [WorldEconomy.getPriceLevel](map/WorldEconomy.md) (L267)
3. **L4027** `labourMarket.updateCostOfLiving(priceIndex.getIndex());` → [LabourMarket.updateCostOfLiving](map/LabourMarket.md) (L322), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _...and wages start chasing what the world now charges._
4. **L4045** `landManager.updateMarket(populationManager.getPopulation());` → [LandManager.updateMarket](map/LandManager.md) (L191), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _what the next parcel costs, are both inputs to everything below._
5. **L4047** `economyManager.setLandPricePerSqFt(landManager.getPricePerSqFt());` → [EconomyManager.setLandPricePerSqFt](map/EconomyManager.md) (L247), [LandManager.getPricePerSqFt](map/LandManager.md) (L164)
6. **L4056** `debtManager.setBankPremium(bank.ratePremium());` → [DebtManager.setBankPremium](map/DebtManager.md) (L619), [Bank.ratePremium](map/Bank.md) (L1160)  
   _THE BANK PRICES THE MONEY, BEFORE ANYTHING IS PRICED OFF IT._
7. **L4064** `pushCostOfFundsToTheDebtMarket();` → [Game.pushCostOfFundsToTheDebtMarket](map/Game.md) (L4397)  
   _...AND WHAT THE MONEY COSTS AT ALL, which is a different question from how strained the lender is._
8. **L4068** `economyManager.getBusinessDebtManager().setLendingOpen(` → [EconomyManager.getBusinessDebtManager](map/EconomyManager.md) (L53), [Bank.getBranches](map/Bank.md) (L1922), [Bank.isInsolvent](map/Bank.md) (L788)  
   _A failed bank lends nothing._
9. **L4070** `economyManager.updateBusinessCredit(debtManager.getRate());` → [EconomyManager.updateBusinessCredit](map/EconomyManager.md) (L381), [DebtManager.getRate](map/DebtManager.md) (L606)
10. **L4075** `economyManager.chargePropertyTax();` → [EconomyManager.chargePropertyTax](map/EconomyManager.md) (L448)  
   _Property tax with the interest bill, and for the same reason: both are owed before the month's statements run, so what each sector banks is already net of them._
11. **L4079** `chargeBuildingMaintenance();` → [Game.chargeBuildingMaintenance](map/Game.md) (L2151)  
   _The repair bill on the housing stock, before EITHER statement runs - it is an expense on one of them and revenue on the other._
12. **L4083** `chargeFreight();` → [Game.chargeFreight](map/Game.md) (L2120)  
   _...and the freight bill on the month's trade, for exactly the same reason: an expense on eleven sets of books and revenue on a twelfth._
13. **L4097** `ham.citybuildersim.sectors.Construction construction = getSectors().construction();` → [Game.getSectors](map/Game.md) (L737)  
   _THE MONTH'S BUILDING WORK, AS THE STATEMENT WILL CARRY IT._
14. **L4098** `double constructionWorkDone = construction.getRecognisedThisMonth();`
15. **L4107** `economyManager.strikeSectors();` → [EconomyManager.strikeSectors](map/EconomyManager.md) (L575)  
   _EVERY SECTOR'S STATEMENT, STRUCK AND BANKED, and the month's VAT settled from the same figures between the two halves._
16. **L4109** `economyManager.updateNationalAccounts(` → [EconomyManager.updateNationalAccounts](map/EconomyManager.md) (L969), [ServicesManager.getUtilitiesHandler](map/ServicesManager.md) (L211), [Healthcare.getGrossCost](map/Healthcare.md) (L513), [Crime.getGrossCost](map/Crime.md) (L449), [LandManager.getLandSalesThisMonth](map/LandManager.md) (L166), [LandManager.getLandPurchasesThisMonth](map/LandManager.md) (L168), [EconomyManager.getTotalPropertyTax](map/EconomyManager.md) (L459)
17. **L4158** `monthlyMaterialImports = 0;`  
   _THE CAPITAL AND LAND ACCUMULATORS ARE NOT CLEARED HERE ANY MORE._
18. **L4159** `monthlyMaterialImportBill = 0;`
19. **L4161** `updateHouseholdAccounts();` → [Game.updateHouseholdAccounts](map/Game.md) (L880)
20. **L4169** `motoring.month(this);` → [Motoring.month](map/Motoring.md) (L91)  
   _...AND THEY SPEND SOME OF IT ON A CAR._
21. **L4177** `luxuryCounter.shop(this);` → [LuxuryCounter.shop](map/LuxuryCounter.md) (L114)  
   _...AND THEY SPEND THE REST OF IT ON SOMETHING THEY DO NOT NEED._
22. **L4186** `luxuryCounter.dine(this);` → [LuxuryCounter.dine](map/LuxuryCounter.md) (L68)  
   _...AND THE KITCHENS, right after, because they are the same kind of purchase out of the same surplus._
23. **L4190** `economyManager.settleBusinessCredit(month);` → [EconomyManager.settleBusinessCredit](map/EconomyManager.md) (L679)  
   _Then advance the loans, take back matured principal, and lend to whichever sector the month left short._
24. **L4194** `runPrivateInvestment();` → [Game.runPrivateInvestment](map/Game.md) (L804)  
   _Businesses get their turn: look at demand, forecast it forward, and expand if the new capacity would carry its own debt._
25. **L4196** `printStartOfMonth();` → [Game.printStartOfMonth](map/Game.md) (L4198)

## SimulationEngine.simulateMonth() - SimulationEngine.java lines 30-107 (78 lines)

1. **L63** `int siteOutput = game.getBuildingOutput();` → [Game.getBuildingOutput](map/Game.md) (L2101)  
   _getBuildingOutput(), not getConstructionOutput(): the second is what the sector can do in a month, the first is what is left for the SITES once the standing housing stock has had its repairs._
2. **L64** `game.recordCompletions(buildingManager.advanceConstruction(siteOutput));` → [Game.recordCompletions](map/Game.md) (L5151), [BuildingManager.advanceConstruction](map/BuildingManager.md) (L2903)
3. **L70** `game.drawSiteMaterials(buildingManager.takeMaterialsDue());` → [Game.drawSiteMaterials](map/Game.md) (L2493), [BuildingManager.takeMaterialsDue](map/BuildingManager.md) (L2936)  
   _...and the material that work drew on, bought now, and the work itself recognised on the same figure the sites advanced by._
4. **L71** `game.recogniseSiteWork(buildingManager.takeRevenueDue(), siteOutput);` → [Game.recogniseSiteWork](map/Game.md) (L2517), [BuildingManager.takeRevenueDue](map/BuildingManager.md) (L2945)
5. **L80** `servicesManager.updateInfrastructure();` → [ServicesManager.updateInfrastructure](map/ServicesManager.md) (L122)  
   _Roads, before anything reads them._
6. **L87** `updatePopulation(game);` → [SimulationEngine.updatePopulation](map/SimulationEngine.md) (L109)  
   _Recount the posts and refresh the wage arrays._
7. **L103** `game.advanceDemographics();` → [Game.advanceDemographics](map/Game.md) (L4640)  
   _THE POPULATION STEP._
8. **L105** `updateEconomy(game);` → [SimulationEngine.updateEconomy](map/SimulationEngine.md) (L118)
9. **L106** `updateServices(game);` → [SimulationEngine.updateServices](map/SimulationEngine.md) (L182)

## SimulationEngine.updatePopulation() - SimulationEngine.java lines 109-116 (8 lines)

1. **L110** `game.updatePopulation();` → [Game.updatePopulation](map/Game.md) (L4516)
2. **L111** `populationManager.updateJobs(game.getJobs());` → [PopulationManager.updateJobs](map/PopulationManager.md) (L122), [Game.getJobs](map/Game.md) (L4528)
3. **L114** `game.repriceLabour();` → [Game.repriceLabour](map/Game.md) (L5506)  
   _Between these two on purpose: the market prices against this month's posts, and the wage bill below multiplies by the price it sets._
4. **L115** `populationManager.UpdateTotalWagePerType();` → [PopulationManager.UpdateTotalWagePerType](map/PopulationManager.md) (L302)

## Game.advanceDemographics() - Game.java lines 4640-5069 (430 lines)

> Ages the city, moves people in and out, and rebuilds the households.

1. **L4645** `migration.recordWages(populationManager.getStaffedWagePerTier(),` → [Migration.recordWages](map/Migration.md) (L526), [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209), [PriceIndex.getIndex](map/PriceIndex.md) (L179)  
   _In REAL terms - see Migration.recordWages()._
2. **L4659** `double[] fill = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _WHAT THE HEALTH SERVICE COULD ACTUALLY DO THIS MONTH, measured before anybody ages, is born, dies or moves._
3. **L4660** `double childcareCoverage = careCoverage(CareType.CHILDCARE, fill);` → [Game.careCoverage](map/Game.md) (L5100)
4. **L4661** `double seniorCoverage    = careCoverage(CareType.SENIOR, fill);` → [Game.careCoverage](map/Game.md) (L5100)
5. **L4662** `double generalCoverage   = careCoverage(CareType.GENERAL, fill);` → [Game.careCoverage](map/Game.md) (L5100)
6. **L4663** `double generalCapacity   =` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)
7. **L4665** `double servedThisMonth   = cohorts.total();` → [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
8. **L4667** `double[] served = new double[CareType.values().length];`
9. **L4668** `for (CareType care : CareType.values()) {` → [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)
10. **L4685** `double[] mortalityFactors = Healthcare.mortalityFactors(` → [Healthcare.mortalityFactors](map/Healthcare.md) (L339)  
   _BOTH ENDS OF A LIFE, and now the beginning of one too._
11. **L4696** `double[] unhousedByBand = families.unhousedPeopleByBand();` → [FamilyModel.unhousedPeopleByBand](map/FamilyModel.md) (L268)  
   _THE UNHOUSED AND THE ORPHANS DIE SOONER (2026-09-11)._
12. **L4697** `unhousedByBand[AgeBand.ADULT.ordinal()] += unemployment.getUnhoused();` → [Unemployment.getUnhoused](map/Unemployment.md) (L150)
13. **L4698** `double[] orphansByBand = new double[AgeBand.values().length];`
14. **L4699** `double[] inBand = new double[AgeBand.values().length];`
15. **L4700** `for (AgeBand b : AgeBand.values()) {` → [FamilyModel.getOrphans](map/FamilyModel.md) (L185), [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
16. **L4704** `double[] careFactors = mortalityFactors;`
17. **L4705** `mortalityFactors = Unemployment.blendMortality(mortalityFactors,` → [Unemployment.blendMortality](map/Unemployment.md) (L429), [Healthcare.mortalityFactors](map/Healthcare.md) (L339)
18. **L4715** `if (!sickness.isSeeded()) {` → [Sickness.isSeeded](map/Sickness.md) (L238), [Sickness.seed](map/Sickness.md) (L191), [Health.getSickRate](map/Health.md) (L296)  
   _...AND THE PEOPLE WHO STAYED SICK (2026-09-11)._
19. **L4718** `double[] illness = sickness.deathRates();` → [Sickness.deathRates](map/Sickness.md) (L134)
20. **L4725** `double[] violence = new double[AgeBand.values().length];`  
   _...AND THE PEOPLE VIOLENCE KILLED (2026-09-11)._
21. **L4726** `double adultsBefore = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)
22. **L4727** `violence[AgeBand.ADULT.ordinal()] = adultsBefore > 0` → [Crime.getKilled](map/Crime.md) (L410)
23. **L4729** `cohorts.advanceMonth(mortalityFactors, illness, violence,` → [PopulationCohorts.advanceMonth](map/PopulationCohorts.md) (L144), [Healthcare.birthFactor](map/Healthcare.md) (L358)
24. **L4731** `sickness.setLastDeaths(cohorts.getIllnessDeaths());` → [Sickness.setLastDeaths](map/Sickness.md) (L258), [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
25. **L4735** `double[] dyingByBand = new double[AgeBand.values().length];`  
   _Who among them were orphans, and who had no home - for the running totals on the graphs._
26. **L4736** `for (AgeBand b : AgeBand.values()) dyingByBand[b.ordinal()] = cohorts.getDying(b);` → [PopulationCohorts.getDying](map/PopulationCohorts.md) (L90)
27. **L4737** `double[] spreadDead = cohorts.getIllnessDeaths();` → [PopulationCohorts.getIllnessDeaths](map/PopulationCohorts.md) (L87)
28. **L4738** `double[] killedDead = cohorts.getKilled();` → [PopulationCohorts.getKilled](map/PopulationCohorts.md) (L92)
29. **L4739** `for (int b = 0; b < spreadDead.length; b++) spreadDead[b] += killedDead[b];`
30. **L4740** `double[] outsideDead = Unemployment.attributeDeaths(careFactors,` → [Unemployment.attributeDeaths](map/Unemployment.md) (L465), [Healthcare.mortalityFactors](map/Healthcare.md) (L339)
31. **L4743** `lastOrphanDeaths = outsideDead[0];`
32. **L4744** `lastUnhousedDeaths = outsideDead[1];`
33. **L4751** `cohorts.leave(AgeBand.ADULT, unemployment.takeEvicted());` → [PopulationCohorts.leave](map/PopulationCohorts.md) (L342), [Unemployment.takeEvicted](map/Unemployment.md) (L208)  
   _...AND THE EVICTED WHO GIVE UP ON THE CITY LEAVE._
34. **L4754** `lastAdultMortality = AgeBand.monthlyFromAnnual(` → [AgeBand.monthlyFromAnnual](map/AgeBand.md) (L172)  
   _Kept for the skills step below: the graduates die at the rate the adults do, and that rate depends on the clinics._
35. **L4767** `double adultsAlreadyHere = cohorts.get(AgeBand.ADULT);` → [PopulationCohorts.get](map/PopulationCohorts.md) (L80)  
   _Who works this month: the adults who were already living here, read off before migration moves anybody._
36. **L4774** `migration.setBankruptcyDepartures(householdBalance.getLeavingCity()` → [Migration.setBankruptcyDepartures](map/Migration.md) (L803), [HouseholdBalance.getLeavingCity](map/HouseholdBalance.md) (L2410), [FamilyModel.averageHouseholdSize](map/FamilyModel.md) (L359)  
   _The families the bank discharged last month are leaving._
37. **L4783** `migration.setRentBurden(households.getRentBurden());` → [Migration.setRentBurden](map/Migration.md) (L299), [HouseholdAccounts.getRentBurden](map/HouseholdAccounts.md) (L313)  
   _...and what a flat costs here, which is a PULL rather than a push and so is set in the same place for a different reason._
38. **L4787** `migration.setCrimeVsCanada(crime.getRateVsCanada());` → [Migration.setCrimeVsCanada](map/Migration.md) (L340), [Crime.getRateVsCanada](map/Crime.md) (L433)  
   _...and what last month's crime says about living here._
39. **L4789** `cohorts.migrate(migration.monthlyNet(` → [PopulationCohorts.migrate](map/PopulationCohorts.md) (L314), [Migration.monthlyNet](map/Migration.md) (L720), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135), [Game.getHouseholdCapacity](map/Game.md) (L4520), [BuildingManager.getTotalHomes](map/BuildingManager.md) (L3193), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)
40. **L4804** `population = populationManager.applyPopulation(` → [PopulationManager.applyPopulation](map/PopulationManager.md) (L117), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
41. **L4816** `applyMigrationSkills();` → [Game.applyMigrationSkills](map/Game.md) (L5531)  
   _AND THE SKILLS MOVE WITH THE PEOPLE._
42. **L4818** `double[] jobsByTier = new double[PayTier.values().length];`
43. **L4819** `double[] fillRate = populationManager.getJobFillRate();` → [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
44. **L4820** `int[] posts = populationManager.getJobs();` → [PopulationManager.getJobs](map/PopulationManager.md) (L814)
45. **L4822** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
46. **L4834** `double[] postsByTier = new double[PayTier.values().length];`  
   _WHO IS OUT OF WORK, AND WHO THEY WERE._
47. **L4835** `double[] wageByTier = new double[PayTier.values().length];`
48. **L4836** `double[] staffedWage = populationManager.getStaffedWagePerTier();` → [PopulationManager.getStaffedWagePerTier](map/PopulationManager.md) (L209)
49. **L4837** `for (JobType type : JobType.values()) {` → [PayTier.of](map/PayTier.md) (L106)
50. **L4840** `for (int t = 0; t < wageByTier.length; t++) {`
51. **L4856** `double officers = buildingManager.getStaffedSafetyCapacity(SafetyType.POLICE, fill);` → [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3667)  
   _CRIME, AND WHO GOES TO PRISON FOR IT (2026-09-11)._
52. **L4857** `double[] offenderWeight = new double[householdBalance.cellCount()];` → [HouseholdBalance.cellCount](map/HouseholdBalance.md) (L484)
53. **L4858** `Crime.Causes causes = offending.causes(this, Crime.coverageOf(officers, cohorts.total()), offenderWeight);` → [Offending.causes](map/Offending.md) (L54), [Crime.coverageOf](map/Crime.md) (L242), [PopulationCohorts.total](map/PopulationCohorts.md) (L95)
54. **L4859** `crime.advanceMonth(causes, cohorts.total(), officers,` → [Crime.advanceMonth](map/Crime.md) (L284), [PopulationCohorts.total](map/PopulationCohorts.md) (L95), [BuildingManager.getStaffedSafetyCapacity](map/BuildingManager.md) (L3667), [Game.unskilledWage](map/Game.md) (L5286)
55. **L4866** `unemployment.imprison(crime.getPressure() > 0` → [Unemployment.imprison](map/Unemployment.md) (L234), [Crime.getPressure](map/Crime.md) (L382), [Crime.getAdmitted](map/Crime.md) (L422)  
   _Who went in: out of the pool by the pool's share of the pressure, group by group; the rest leave the families, by the labour market's own identity, when the supply shrinks by them below._
56. **L4868** `populationManager.setImprisoned(crime.prisoners());` → [PopulationManager.setImprisoned](map/PopulationManager.md) (L487), [Crime.prisoners](map/Crime.md) (L367)
57. **L4869** `offending.steal(this, offenderWeight);` → [Offending.steal](map/Offending.md) (L129)
58. **L4871** `double outOfWork = populationManager.getUnemployed();` → [PopulationManager.getUnemployed](map/PopulationManager.md) (L788)
59. **L4872** `double studying = populationManager.getStudyingTotal();` → [PopulationManager.getStudyingTotal](map/PopulationManager.md) (L493)
60. **L4873** `unemployment.advanceMonth(outOfWork, jobsByTier, postsByTier, wageByTier,` → [Unemployment.advanceMonth](map/Unemployment.md) (L291), [Game.unskilledWage](map/Game.md) (L5286), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
61. **L4877** `unemployment.noteArrivals(migration.getLastArrivals() * cohorts.share(AgeBand.ADULT));` → [Unemployment.noteArrivals](map/Unemployment.md) (L275), [Migration.getLastArrivals](map/Migration.md) (L435), [PopulationCohorts.share](map/PopulationCohorts.md) (L102)  
   _The month's adult arrivals look for work next month._
62. **L4887** `families.rebuild(cohorts, jobsByTier, crime.prisoners(), outOfWork + studying);` → [FamilyModel.rebuild](map/FamilyModel.md) (L378), [Crime.prisoners](map/Crime.md) (L367)  
   _THE PRISONERS ARE AWAY; THE REST ARE OUT OF WORK, NOT OUT OF THE HOUSE._
63. **L4888** `families.setSeekers(unemployment.getHoused(), studying);` → [FamilyModel.setSeekers](map/FamilyModel.md) (L200), [Unemployment.getHoused](map/Unemployment.md) (L153)
64. **L4892** `householdBalance.setGraduates(education.getFinished());` → [HouseholdBalance.setGraduates](map/HouseholdBalance.md) (L2395), [Education.getFinished](map/Education.md) (L697)  
   _The student body above is last month's education step, and so are the ones who finished: they leave it with their loans at this month's census._
65. **L4904** `int[] stock = buildingManager.homesBySize();` → [BuildingManager.homesBySize](map/BuildingManager.md) (L3214)  
   _HOMES HAVE SIZES NOW, so this is a match rather than a count._
66. **L4905** `double unplaced = families.house(stock);` → [FamilyModel.house](map/FamilyModel.md) (L1128)
67. **L4906** `families.squeezeUnplaced(unplaced);` → [FamilyModel.squeezeUnplaced](map/FamilyModel.md) (L1278)
68. **L4907** `families.noteUnplaced(families.house(stock));` → [FamilyModel.noteUnplaced](map/FamilyModel.md) (L1365), [FamilyModel.house](map/FamilyModel.md) (L1128)
69. **L4912** `getSectors().realEstate().setRentWeight(` → [Game.getSectors](map/Game.md) (L737), [FamilyModel.studioRentWeight](map/FamilyModel.md) (L987), [FamilyModel.familyRentWeight](map/FamilyModel.md) (L988)  
   _What the landlords can bill, off the match rather than off an average, and split by which segment the door was in._
70. **L4916** `businessInvestment.setFamilies(families);` → [BusinessInvestment.setFamilies](map/BusinessInvestment.md) (L765)  
   _And the advisor prices a new home on who would move into it._
71. **L4917** `businessInvestment.setBank(bank);` → [BusinessInvestment.setBank](map/BusinessInvestment.md) (L770)
72. **L4933** `families.shareByAffordability(households.livingAlonePressure(families));` → [FamilyModel.shareByAffordability](map/FamilyModel.md) (L1492), [HouseholdAccounts.livingAlonePressure](map/HouseholdAccounts.md) (L633)  
   _...and the ones who cannot afford one either._
73. **L4935** `families.shareSeekersByAffordability(households.seekerPressure(new double[] {` → [FamilyModel.shareSeekersByAffordability](map/FamilyModel.md) (L1523), [HouseholdAccounts.seekerPressure](map/HouseholdAccounts.md) (L684), [Unemployment.getHoused](map/Unemployment.md) (L153), [FamilyModel.getSeekers](map/FamilyModel.md) (L209)  
   _...and the people outside the families, with their own kind._
74. **L4939** `economyManager.setOutsidePayments(unemployment.getBenefitsPaid(),` → [EconomyManager.setOutsidePayments](map/EconomyManager.md) (L845), [Unemployment.getBenefitsPaid](map/Unemployment.md) (L183), [FamilyModel.getSeekers](map/FamilyModel.md) (L209), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54), [Game.unskilledWage](map/Game.md) (L5286)  
   _What EI and the grants cost the treasury this month, before the cash moves._
75. **L4971** `healthcare.advanceMonth(` → [Healthcare.advanceMonth](map/Healthcare.md) (L416), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [PopulationCohorts.getLastDeaths](map/PopulationCohorts.md) (L82), [Game.burialShare](map/Game.md) (L5125), [BuildingManager.getCareCapacity](map/BuildingManager.md) (L3561), [BuildingManager.getStaffedCareCapacity](map/BuildingManager.md) (L3593)  
   _6._
76. **L4981** `economyManager.setHealthcare(healthcare.getGrossCost(), healthcare.getFees());` → [EconomyManager.setHealthcare](map/EconomyManager.md) (L862), [Healthcare.getGrossCost](map/Healthcare.md) (L513), [Healthcare.getFees](map/Healthcare.md) (L515)
77. **L4993** `education.advanceMonth(` → [Education.advanceMonth](map/Education.md) (L278), [BuildingManager.getStaffedEducationPlaces](map/BuildingManager.md) (L3631), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [Migration.getLastDepartures](map/Migration.md) (L440)  
   _6b._
78. **L5006** `populationManager.applyBandFlow(education.getGraduates());` → [PopulationManager.applyBandFlow](map/PopulationManager.md) (L710), [Education.getGraduates](map/Education.md) (L635)
79. **L5007** `populationManager.addLicences(education.getLicences());` → [PopulationManager.addLicences](map/PopulationManager.md) (L636), [Education.getLicences](map/Education.md) (L638)
80. **L5010** `populationManager.setStudying(education.getStudying());` → [PopulationManager.setStudying](map/PopulationManager.md) (L492), [Education.getStudying](map/Education.md) (L445)  
   _And whoever is in a lecture theatre is out of the labour supply until they come out of it - see PopulationManager.workforceByBand()._
81. **L5013** `populationManager.trimLicencesToBand();` → [PopulationManager.trimLicencesToBand](map/PopulationManager.md) (L678)  
   _A licence holder is a graduate first, and the graduate count has just moved._
82. **L5015** `economyManager.setEducation(education.getGrossCost(), education.getFees());` → [EconomyManager.setEducation](map/EconomyManager.md) (L871), [Education.getGrossCost](map/Education.md) (L678), [Education.getFees](map/Education.md) (L686)
83. **L5021** `crime.setCosts(` → [Crime.setCosts](map/Crime.md) (L359), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832)  
   _6c._
84. **L5025** `economyManager.setSafety(crime.getGrossCost());` → [EconomyManager.setSafety](map/EconomyManager.md) (L884), [Crime.getGrossCost](map/Crime.md) (L449)
85. **L5039** `servicesManager.updateTransitFare(economyManager.getTaxPolicy().getTransitFare());` → [ServicesManager.updateTransitFare](map/ServicesManager.md) (L162), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)  
   _6d._
86. **L5040** `economyManager.setTransit(` → [EconomyManager.setTransit](map/EconomyManager.md) (L909), [BuildingManager.getCategoryPayroll](map/BuildingManager.md) (L3743), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getUpkeepByCategory](map/BuildingManager.md) (L3832), [Game.getInfrastructureManager](map/Game.md) (L747), [EconomyManager.getTaxPolicy](map/EconomyManager.md) (L54)
87. **L5051** `health.advanceMonth(generalCapacity, servedThisMonth, month,` → [Health.advanceMonth](map/Health.md) (L181), [Healthcare.getUnburied](map/Healthcare.md) (L540), [HouseholdBalance.getHungerRate](map/HouseholdBalance.md) (L2501), [Game.unhousedShareOfCity](map/Game.md) (L5082), [Crime.getInjuredShare](map/Crime.md) (L413)  
   _7._
88. **L5067** `sickness.advanceMonth(health.getSickRate(), generalCoverage,` → [Sickness.advanceMonth](map/Sickness.md) (L150), [Health.getSickRate](map/Health.md) (L296)  
   _8._

## SimulationEngine.updateEconomy() - SimulationEngine.java lines 118-180 (63 lines)

1. **L121** `economyManager.setTotalJobs(populationManager.getTotalJobs());` → [EconomyManager.setTotalJobs](map/EconomyManager.md) (L82), [PopulationManager.getTotalJobs](map/PopulationManager.md) (L135)  
   _TBE_
2. **L122** `economyManager.setPopulation(populationManager.getPopulation());` → [EconomyManager.setPopulation](map/EconomyManager.md) (L83), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)
3. **L123** `economyManager.setHouseholds(buildingManager.getTotalHouseCapacity());` → [EconomyManager.setHouseholds](map/EconomyManager.md) (L84), [BuildingManager.getTotalHouseCapacity](map/BuildingManager.md) (L3251)
4. **L124** `economyManager.setOccupiedHomes(game.getFamilies().homesNeeded());` → [EconomyManager.setOccupiedHomes](map/EconomyManager.md) (L272), [Game.getFamilies](map/Game.md) (L4612)
5. **L130** `economyManager.setHouseholdCount(game.getFamilies().totalHouseholds());` → [EconomyManager.setHouseholdCount](map/EconomyManager.md) (L269), [Game.getFamilies](map/Game.md) (L4612)  
   _The two inputs to the rent price, which is a market now._
6. **L133** `economyManager.setHousingSeekers(game.getFamilies().studioSeekers(),` → [EconomyManager.setHousingSeekers](map/EconomyManager.md) (L273), [Game.getFamilies](map/Game.md) (L4612)  
   _...and the same count split into the two segments the rent market now prices separately._
7. **L137** `economyManager.setMarginalHousingCost(game.marginalHousingCost());` → [EconomyManager.setMarginalHousingCost](map/EconomyManager.md) (L270), [Game.marginalHousingCost](map/Game.md) (L6347)
8. **L139** `economyManager.setSeniors(game.getCohorts().get(AgeBand.SENIOR)` → [EconomyManager.setSeniors](map/EconomyManager.md) (L927), [Game.getCohorts](map/Game.md) (L4611)
9. **L145** `economyManager.updateJobFillRate(populationManager.getJobFillRate());` → [EconomyManager.updateJobFillRate](map/EconomyManager.md) (L93), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)  
   _The fill first: every sector's payroll is discounted by it, so it has to be current before the wages are set._
10. **L146** `economyManager.updateWages(` → [EconomyManager.updateWages](map/EconomyManager.md) (L105), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172), [BuildingManager.getJobArrayByName](map/BuildingManager.md) (L3903)
11. **L149** `economyManager.setTotalWage(populationManager.getTotalWage());` → [EconomyManager.setTotalWage](map/EconomyManager.md) (L86), [PopulationManager.getTotalWage](map/PopulationManager.md) (L163)
12. **L156** `economyManager.setWageDetail(populationManager.getStaffedWagePerType());` → [EconomyManager.setWageDetail](map/EconomyManager.md) (L87), [PopulationManager.getStaffedWagePerType](map/PopulationManager.md) (L188)  
   _The split behind that total._
13. **L157** `economyManager.setEnergyRatio(servicesManager.getEnergyRatio());` → [EconomyManager.setEnergyRatio](map/EconomyManager.md) (L149), [ServicesManager.getEnergyRatio](map/ServicesManager.md) (L219)
14. **L158** `economyManager.setWaterRatio(servicesManager.getWaterRatio());` → [EconomyManager.setWaterRatio](map/EconomyManager.md) (L150), [ServicesManager.getWaterRatio](map/ServicesManager.md) (L223)
15. **L173** `economyManager.setRoadRatio(game.getInfrastructureManager(), buildingManager);` → [EconomyManager.setRoadRatio](map/EconomyManager.md) (L152), [Game.getInfrastructureManager](map/Game.md) (L747)  
   _THE ROAD, PER SECTOR, AND IT HAS TO BE THE SAME CALL AS ON THE LOAD PATH._
16. **L176** `economyManager.setHealthRatio(game.getHealth().getWorkRatio());` → [EconomyManager.setHealthRatio](map/EconomyManager.md) (L180), [Game.getHealth](map/Game.md) (L4615)  
   _The fourth ratio._
17. **L177** `economyManager.updateEcon();` → [EconomyManager.updateEcon](map/EconomyManager.md) (L758)

## SimulationEngine.updateServices() - SimulationEngine.java lines 182-189 (8 lines)

1. **L183** `servicesManager.updateServiceWages(populationManager.getWagesPerType());` → [ServicesManager.updateServiceWages](map/ServicesManager.md) (L185), [PopulationManager.getWagesPerType](map/PopulationManager.md) (L172)
2. **L184** `servicesManager.updateJobFillRate(populationManager.getJobFillRate());` → [ServicesManager.updateJobFillRate](map/ServicesManager.md) (L203), [PopulationManager.getJobFillRate](map/PopulationManager.md) (L755)
3. **L187** `servicesManager.setPopulation(populationManager.getPopulation());` → [ServicesManager.setPopulation](map/ServicesManager.md) (L260), [PopulationManager.getPopulation](map/PopulationManager.md) (L139)  
   _must precede updateServices(): the residents' draw is part of the water demand the ratio is computed against_
4. **L188** `servicesManager.updateServices();` → [ServicesManager.updateServices](map/ServicesManager.md) (L33)

## Game.run() - Game.java lines 309-312 (4 lines)

1. **L310** `initialize();` → [Game.initialize](map/Game.md) (L314)
2. **L311** `foundingBank();` → [Game.foundingBank](map/Game.md) (L405)

