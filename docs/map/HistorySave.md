# HistorySave.java - 2,587 lines · 96 methods · 6 constants · model

`ham/citybuildersim/HistorySave.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> Every month the city has ever lived, one number at a time.
> 
> WHY THE SERIES ARE NAMED FIELDS AND NOT A MAP
> 
> A Map<String, List<Double>> would be shorter and would let a series be added
> without touching anything. It is not used, and the reason is on disk: this
> class IS the file format. Gson matches JSON to fields BY NAME, so a history
> written before a series existed still loads into this build - the missing
> list simply stays empty - and a history written by this build still loads
> into an older one, which ignores what it does not recognise. Moving to a map
> would change the shape of every history file ever written and break both
> directions at once, to save a few lines.
> 
> WHAT A SHORT SERIES MEANS
> 
> It means the city was ALIVE before that number was being kept, so the series
> lines up with the END of the month axis and not the start. A save from before
> sickness was recorded has 400 months and no sick rate; play ten more and it
> has 410 months and ten sick rates, describing months 401-410. aligned() is
> the only correct way to read one, and the graph goes through it - drawing a
> short series from the left would silently place the last decade's data in the
> founding years.
> 
> WHAT IS NOT HERE
> 
> Anything derivable. Unemployment is workforce and jobs, GDP per capita is GDP
> and population, the average wage is the wage bill and the workforce. Storing
> a derived series would double the file to hold numbers that can disagree with
> the ones they came from, and the first time they did, nothing would say which
> was right. Derived() computes them on the way to the screen.
> 
> PAST FIVE HUNDRED YEARS, A YEAR TO A POINT (0.7.55)
> 
> Jerus: "at the 500y mark, past data starts converting to yearly figures,
> but always keep 500 recent years in monthly". The newest MONTHLY_KEPT
> months stay a month to an entry; a calendar year all of whose months are
> older than those folds into one entry (foldOldYears(), at the end of every
> recordMonth()), each series by the rule the year book reads it by
> (YearBook.kindOf()): a flow's months ADDED, a level's LAST month, a rate's
> months AVERAGED. The fold's entry sits on the axis at its year's last
> month, and foldedMonths says how many months each folded entry holds - the
> first yearlyPoints() entries are years, every one after is a month. A
> history from before 0.7.55 has no foldedMonths and is all months, as it
> always was.
> 
> READ A MONTH AT A TIME. aligned() hands a folded year's flow back as its
> monthly average - the year's sum over its months - so a chart's line, a
> per-head figure and a rolling year all read one unit across the boundary;
> a level is its year's end and a rate its year's average, as stored. What
> needs the sum itself (total(), runningTotal(), recentTotal(), worstYear())
> reads the stored figures, and monthsIn() says what each entry weighs.

**Uses:** [YearBook](YearBook.md) (8), [AgeBand](AgeBand.md) (7), [PriceIndex](PriceIndex.md) (6), [DebtStatistics](DebtStatistics.md) (5), [PartnerTrade](PartnerTrade.md) (5), [Crime](Crime.md) (4), [PriceLevel](PriceLevel.md) (4), [FamilyStructure](FamilyStructure.md) (3), [ResearchCatalog](ResearchCatalog.md) (3), [WorldMoneys](WorldMoneys.md) (3), [Equity](Equity.md) (3), [CityCalendar](CityCalendar.md) (3), [Game](Game.md) (2), [PowerView](PowerView.md) (2), [LicenceFees](LicenceFees.md) (2), [GameFiles](GameFiles.md) (2), [EconomyManager](EconomyManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [PopulationManager](PopulationManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Migration](Migration.md) (1), [LabourMarket](LabourMarket.md) (1), [JobType](JobType.md) (1), [WageBand](WageBand.md) (1), [Education](Education.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Nations](Nations.md) (1), [PolicyRates](PolicyRates.md) (1), [Bank](Bank.md) (1)... and 10 more

**Used by (60):** [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuybackCheck](BuybackCheck.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [ChildcareCheck](ChildcareCheck.md), [CityMeasures](CityMeasures.md), [ClockCheck](ClockCheck.md), [CpiCoverageFinding](CpiCoverageFinding.md), [DeathRecordCheck](DeathRecordCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GovernmentScreen](GovernmentScreen.md), [GovernmentView](GovernmentView.md), [GovernmentViewCheck](GovernmentViewCheck.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [InboxCheck](InboxCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [LandScreen](LandScreen.md), [LicenceFeesCheck](LicenceFeesCheck.md), [PartnersCheck](PartnersCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PowerViewCheck](PowerViewCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [Recording](Recording.md), [ReplayCheck](ReplayCheck.md), [ReplayCity](ReplayCity.md), [ReplayGraphs](ReplayGraphs.md), [ResearchScreen](ResearchScreen.md), [ResearchViewCheck](ResearchViewCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md), [SavedCities](SavedCities.md), [ScaleCheck](ScaleCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [UiProbe](UiProbe.md), [UserInterface](UserInterface.md), [VaultTable](VaultTable.md), [VaultView](VaultView.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 89 | · THE AXIS |
| 112 | · money |
| 128 | · people |
| 169 | · what throttles |
| 186 | · prices |
| 197 | · the edge |
| 260 | · money and credit |
| 305 | · what the bank could carry, and how hard |
| 344 | · the price of money (0.7.7) |
| 359 | · the bank's capital (0.7.8) |
| 388 | · the budget |
| 404 | · housing |
| 414 | · school and care |
| 420 | · outside the families |
| 449 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 463 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 471 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 481 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 497 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 521 | · what runs out |
| 525 | · the market |
| 549 | · the sectors (0.7.4) |
| 569 | · the world's moneys (WN4, 2026-10-30) |
| 593 | · the research (R5, 2026-11-06) |
| 640 | · the trade by partner (WN3, 2026-10-28) |
| 659 | · GDP in layers (0.7.6) |
| 676 | · the city's fund (0.7.39) |
| 692 | · the new price model, read (0.7.45) |
| 758 | RECORDING |
| 837 | · · the edge |
| 869 | · · money and credit |
| 934 | · · the budget |
| 944 | · · housing |
| 949 | · · school and care |
| 956 | · · outside the families |
| 1004 | · · what runs out |
| 1008 | · · the market |
| 1021 | · · the sectors |
| 1029 | · · the new price model, read (0.7.45) |
| 1052 | · · the research (R5) |
| 1068 | · · the city's fund (0.7.39) |
| 1080 | THE FOLD (0.7.55) |
| 1653 | READING |
| 1781 | · a series' record, counted (0.7.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 110 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 591 | `HistorySave.WORLD_CYCLE` | `"worldCycle", WORLD_RISK_OFF = "worldRiskOff"` | The dollar cycle's series and the risk-off's. |
| 635 | `HistorySave.RESEARCH_SPEND` | `"researchSpend"` | The month's research bill and prizes paid, the city's money. |
| 638 | `HistorySave.EQUIPMENT_FITTED` | `"equipmentFitted"` | The share of the equipment standing that is fitted, 0 to 1; begins when there is equipment to count. |
| 1195 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "d...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 1217 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5); and, since EP1, so are the wage, the rent and the prices of food, materials, ... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 96 | `private List<Integer> month` |  |
| 107 | `private List<Integer> foldedMonths` | How many months each folded year holds, oldest first (0.7.55): the first foldedMonths.size() entries of the axis, and of every series lined up with it, are years - see PAST FIVE HUNDRED YEARS above. |
| 113 | `private List<Double> cash` |  |
| 114 | `private List<Double> gdp` |  |
| 115 | `private List<Double> debt` |  |
| 116 | `private List<Double> interestRate` |  |
| 119 | `private List<Double> revenue` | Government revenue and what it kept, both monthly. |
| 120 | `private List<Double> surplus` |  |
| 123 | `private List<Double> retiredGain` | The gain (loss) on debt retired, monthly (CD4, CD3's ★5): the surplus carries it and spending does not, so a year's spending is revenue less the surplus PLUS this - without it the year netted a buyback's gain into spe... |
| 126 | `private List<Double> debtExchange` | The exchange gain (loss) on the city's foreign-currency debt, monthly (CD7): the surplus carries it and spending does not, and no cash moved on it, so the rollover's netting and the fund's pay-in read the surplus LESS... |
| 129 | `private List<Long> jobs` |  |
| 130 | `private List<Long> workforce` |  |
| 139 | `private List<Long> outOfWork` | The people out of work - the labour force less the posts FILLED, the People screen's figure (PopulationManager.getUnemployed()). |
| 140 | `private List<Long> population` |  |
| 143 | `private List<Long> births` | The four flows that move the population, and only these four move it. |
| 144 | `private List<Long> deaths` |  |
| 145 | `private List<Long> arrivals` |  |
| 146 | `private List<Long> departures` |  |
| 149 | `private List<Double> totalWage` | The wage BILL, not the average - the average needs the workforce too. |
| 152 | `private List<Double> minimumWage` | The dial the player sets, and what the labour market did with it. |
| 162 | `private List<Double> unskilledPremium` | What an unskilled hour costs relative to its base, and how much of the workforce has any training at all. |
| 163 | `private List<Double> skilledShare` |  |
| 166 | `private List<Double> schoolCoverage` | The schools: how much of the basic ladder is covered, and what it costs. |
| 167 | `private List<Double> schoolBill` |  |
| 170 | `private List<Double> energyRatio` |  |
| 171 | `private List<Double> waterRatio` |  |
| 172 | `private List<Double> roadRatio` |  |
| 173 | `private List<Double> sickRate` |  |
| 174 | `private List<Double> careCoverage` |  |
| 184 | `private List<Double> outbreak` | The outbreak running, as extra absence on top of what the city's care explains (Health.getOutbreakSeverity()); 0 between outbreaks. |
| 192 | `private List<Double> landPrice` | What the world asks the city for a square foot of ground, in thousands of US DOLLARS since 0.7.6 (LandManager.getGroundUsdPerSqFt()); the months an older save recorded are local money, and stay as recorded. |
| 193 | `private List<Double> foodPrice` |  |
| 194 | `private List<Double> materialsPrice` |  |
| 195 | `private List<Double> orePrice` |  |
| 210 | `private List<Double> fxRate` |  |
| 217 | `private List<Double> fxParity` | Parity beside the rate (0.7.35): where a basket costs the same here and abroad, in the rate's own units (ForeignAccounts.getParity()), so the Trade tab can draw the two on one chart. |
| 225 | `private List<Double> fxFair` | The fundamentals' level beside the rate (XR1, 2026-10-21): where parity's trend and the real gap, the balance and the cover put the D$ before the market and the interventions (ForeignAccounts.getFairLevel()), in the r... |
| 231 | `private List<Double> fxMarket` | ...and how far the rate sat from that level, a fraction, positive weaker (ForeignAccounts.deviationFromFair()): the market's swing and the interventions, a ratio and not money. |
| 239 | `private List<Double> fxShadow` | The shadow rate (XR2, 2026-10-22): where the float's rule put the D$ at the month's reprice, in the rate's own units (ForeignAccounts .getShadowRate()) - the rate itself under Lean and Float, the rate the hold is set ... |
| 245 | `private List<Double> fxCover` | ...and the vault's cover, in months of imports (ForeignAccounts .importCover(), at most 99): the hold's reserve, beside the shadow. |
| 246 | `private List<Double> reservesUsd` |  |
| 247 | `private List<Double> foreignDebtUsd` |  |
| 248 | `private List<Double> currentAccount` |  |
| 249 | `private List<Double> exportsAbroad` |  |
| 250 | `private List<Double> importsAbroad` |  |
| 252 | `private List<Double> ipCharges` | Imported patent services, local money, already inside importsAbroad (R2c). |
| 254 | `private List<Double> ipAccrued` | Production's accrued IP bill, paid at the following strike (R2c). |
| 256 | `private List<Double> publicIpCharges` | The licence the city's own patented works paid abroad at the strike, local money, inside importsAbroad (R2c-P). |
| 258 | `private List<Double> publicIpAccrued` | The public works' bill the month accrued, paid at the following strike (R2c-P). |
| 266 | `private List<Double> priceIndex` |  |
| 277 | `private List<Double> usPrices` | The game dollar's price level since founding (LB3, 2026-10-31): the US prices (WorldEconomy.getPriceLevel()), 1 at founding, the "P" the Rankings board divides a city's money by to reach a dollar of 2015. |
| 284 | `private List<Double> expectedInflation` | THE ANCHOR (0.7.42): what the city expected inflation to be, a fraction a year, and how far it believed the bank (Expectations) - both ratios, so a reform leaves them alone. |
| 285 | `private List<Double> credibility` |  |
| 286 | `private List<Double> businessDebt` |  |
| 293 | `private List<Double> businessBonds` | The face of every business bond outstanding, in money (HD1, 2026-10-29): businessDebt is the loans and mortgages only, so the fade of the bonds (the companies that borrow in the bond market, the funds that hold them) ... |
| 300 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 301 | `private List<Double> bankLent` |  |
| 302 | `private List<Double> bankEquity` |  |
| 303 | `private List<Double> bankWriteOffs` |  |
| 320 | `private List<Double> bankCapacity` |  |
| 321 | `private List<Double> bankStrain` |  |
| 322 | `private List<Double> bankProfit` |  |
| 323 | `private List<Double> bankBranches` |  |
| 324 | `private List<Double> householdSavings` |  |
| 325 | `private List<Double> capitalGainsTax` |  |
| 326 | `private List<Double> unclaimedReceipts` |  |
| 327 | `private List<Double> custodyNoncash` |  |
| 328 | `private List<Double> custodyLoss` |  |
| 329 | `private List<Double> custodyDebtGain` |  |
| 330 | `private List<Double> custodyPaperRetired` |  |
| 331 | `private List<Double> custodyBook` |  |
| 332 | `private List<Double> custodyPaper` |  |
| 333 | `private List<Double> custodyPaperFinding` |  |
| 334 | `private List<Double> custodyUsd` |  |
| 335 | `private List<Double> custodyCars` |  |
| 336 | `private List<Double> custodyLossCarry` |  |
| 337 | `private List<Double> custodyCouponDue` |  |
| 338 | `private List<Double> dividendTax` |  |
| 339 | `private List<Double> capitalTaxConsolidated` |  |
| 340 | `private List<Double> capitalTaxWrittenOff` |  |
| 341 | `private List<Double> capitalTaxOutstanding` |  |
| 342 | `private List<Double> capitalTaxAssessed` |  |
| 354 | `private List<Double> policyRate` |  |
| 355 | `private List<Double> bankPrime` |  |
| 356 | `private List<Double> bankDepositRate` |  |
| 357 | `private List<Double> bankFees` |  |
| 369 | `private List<Double> bankCapitalRatio` |  |
| 370 | `private List<Double> bankCapitalTarget` |  |
| 371 | `private List<Double> bankAllowance` |  |
| 372 | `private List<Double> bankProvisions` |  |
| 373 | `private List<Double> bankDividends` |  |
| 374 | `private List<Double> bankReturnOnEquity` |  |
| 385 | `private List<Double> bankLeverageRatio` | ...AND THE OTHER MEASURE (A7, 0.7.46; the Bank spec's D11): its equity over everything on its sheet (Bank.leverageRatio()) and the target it holds on that measure (leverageTarget()). |
| 386 | `private List<Double> bankLeverageTarget` |  |
| 395 | `private List<Double> taxWage` |  |
| 396 | `private List<Double> taxProperty` |  |
| 397 | `private List<Double> taxSales` |  |
| 398 | `private List<Double> taxBusiness` |  |
| 399 | `private List<Double> taxIndustrial` |  |
| 400 | `private List<Double> contributions` |  |
| 401 | `private List<Double> pensionBill` |  |
| 402 | `private List<Double> healthBill` |  |
| 410 | `private List<Double> rentPrice` |  |
| 411 | `private List<Long> homes` |  |
| 412 | `private List<Double> households` |  |
| 415 | `private List<Double> students` |  |
| 416 | `private List<Double> graduates` |  |
| 417 | `private List<Double> licences` |  |
| 418 | `private List<Double> unburied` |  |
| 427 | `private List<Double> outOfWorkOnEi` |  |
| 428 | `private List<Double> outOfWorkOffEi` |  |
| 429 | `private List<Double> unhoused` |  |
| 430 | `private List<Double> orphans` |  |
| 431 | `private List<Double> evicted` |  |
| 432 | `private List<Double> eiPaid` |  |
| 433 | `private List<Double> eiPremiums` |  |
| 435 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 436 | `private List<Double> studentGrants` |  |
| 443 | `private List<Double> foodAssistance` | FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households' groceries a month, and the baskets that bought at the price charged. |
| 444 | `private List<Double> fedByAssistance` |  |
| 445 | `private List<Double> studentLoansOwed` |  |
| 447 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 457 | `private List<Double> m0` |  |
| 458 | `private List<Double> m2` |  |
| 459 | `private List<Double> advancesToTreasury` |  |
| 460 | `private List<Double> reserves` |  |
| 461 | `private List<Double> remittance` |  |
| 467 | `private List<Double> sickPastTwoMonths` |  |
| 468 | `private List<Double> diedOfIllness` |  |
| 469 | `private List<Double> sickRecovery` |  |
| 479 | `private Map<String, List<Double>> householdsByShape` |  |
| 487 | `private List<Double> deathsBabies` |  |
| 488 | `private List<Double> deathsChildren` |  |
| 489 | `private List<Double> deathsTeens` |  |
| 490 | `private List<Double> deathsAdults` |  |
| 491 | `private List<Double> deathsSeniors` |  |
| 493 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 494 | `private List<Double> deathsOrphans` |  |
| 495 | `private List<Double> deathsUnhoused` |  |
| 504 | `private List<Double> crimeRate` |  |
| 505 | `private List<Double> policeCoverage` |  |
| 506 | `private List<Double> prisoners` |  |
| 508 | `private List<Double> owners` | The owner families' adults, off the labour force (PopulationManager.getOwners()): YearBook.labourForce() takes them off as the model does (BO1-S). |
| 509 | `private List<Double> caughtNotHeld` |  |
| 510 | `private List<Double> stolen` |  |
| 511 | `private List<Double> deathsKilled` |  |
| 512 | `private List<Double> safetyBill` |  |
| 513 | `private Map<String, List<Double>> crimeByCause` |  |
| 522 | `private List<Long> constructionCapacity` |  |
| 523 | `private List<Double> landUse` |  |
| 542 | `private Map<String, List<Double>> sharePrice` |  |
| 543 | `private Map<String, List<Double>> shareValue` |  |
| 566 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 567 | `private Map<String, List<Double>> sectorWorkers` |  |
| 582 | `private Map<String, List<Double>> worldMoney` |  |
| 612 | `private Map<String, List<Double>> researchSeries` |  |
| 615 | `private Map<String, List<Double>> powerSeries` | Struck power observations, at the strike month; source dispatch and paid dispatch months are separate series. |
| 617 | `private List<DebtStatistics.Survey> debtSurveys` | Annual household debt surveys (HC2), on their own year axis. |
| 652 | `private List<PartnerTrade.Table> tradeByPartner` |  |
| 671 | `private List<Double> consumption` |  |
| 672 | `private List<Double> investment` |  |
| 673 | `private List<Double> government` |  |
| 674 | `private List<Double> netExports` |  |
| 688 | `private List<Double> fundValue` |  |
| 689 | `private List<Double> fundPutIn` |  |
| 690 | `private List<Double> fundTakenOut` |  |
| 708 | `private List<Double> expectedLevel` |  |
| 709 | `private List<Double> indexGroceries` |  |
| 710 | `private List<Double> indexRent` |  |
| 711 | `private List<Double> indexMeals` |  |
| 712 | `private List<Double> indexLuxury` |  |
| 713 | `private List<Double> indexServices` |  |
| 714 | `private List<Double> indexNewCars` |  |
| 715 | `private List<Double> indexPetrol` |  |
| 716 | `private List<Double> indexLampOil` |  |
| 717 | `private List<Double> indexHomeCoal` |  |
| 718 | `private List<Double> weightGroceries` |  |
| 719 | `private List<Double> weightRent` |  |
| 720 | `private List<Double> weightMeals` |  |
| 721 | `private List<Double> weightLuxury` |  |
| 722 | `private List<Double> weightServices` |  |
| 723 | `private List<Double> weightNewCars` |  |
| 724 | `private List<Double> weightPetrol` |  |
| 725 | `private List<Double> weightLampOil` |  |
| 726 | `private List<Double> weightHomeCoal` |  |
| 727 | `private List<Double> basketLinkedAt` |  |
| 728 | `private List<Double> shelfPrice` |  |
| 729 | `private List<Double> shelfFloor` |  |
| 730 | `private List<Double> basketsAsked` |  |
| 731 | `private List<Double> basketsHanded` |  |
| 732 | `private List<Double> hunger` |  |
| 733 | `private List<Double> hungerPricedOut` |  |
| 734 | `private List<Double> householdsAssisted` |  |
| 735 | `private List<Double> mealMargin` |  |
| 736 | `private List<Double> mealTargetMargin` |  |
| 737 | `private List<Double> luxuryMargin` |  |
| 738 | `private List<Double> luxuryTargetMargin` |  |
| 1558 | `private transient List<Later> owed` | The snapshots taken with snapshotLater() and not yet copied; guarded by this object's monitor. |
| 1561 | `private transient volatile boolean anyOwed` | True while {@link #owed} holds a snapshot: read by every mutator without the lock, so a city that is not being saved pays a field read. |
| 1585 | `private HistorySave copy` |  |
| 2543 | `private final double[] values` |  |
| 2544 | `private final BitSet nulls` |  |
| 2566 | `private final long[] values` |  |
| 2567 | `private final BitSet nulls` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 87 | 2501 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 89-111)

### money (lines 112-127)

### people (lines 128-168)

### what throttles (lines 169-185)

### prices (lines 186-196)

### the edge (lines 197-259)

### money and credit (lines 260-304)

### what the bank could carry, and how hard (lines 305-343)

### the price of money (0.7.7) (lines 344-358)

### the bank's capital (0.7.8) (lines 359-387)

### the budget (lines 388-403)

### housing (lines 404-413)

### school and care (lines 414-419)

### outside the families (lines 420-448)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 449-462)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 463-470)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 471-480)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 481-496)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 497-520)

| line | len | member | says |
|---:|---:|---|---|
| 516 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 519 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 521-524)

### the market (lines 525-548)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 547 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 549-568)

### the world's moneys (WN4, 2026-10-30) (lines 569-592)

| line | len | member | says |
|---:|---:|---|---|
| 585 | 1 | `public static String worldRateKey(String code)` | The series of a major's units to the US dollar: "fxEUR". |
| 588 | 1 | `public static String policyRateKey(String code)` | The series of a central bank's policy rate, a fraction a year (XR4): "policyUSD", for each of the eight, the dollar's too. |

### the research (R5, 2026-11-06) (lines 593-639)

| line | len | member | says |
|---:|---:|---|---|
| 619 | 1 | `public List<DebtStatistics.Survey> getDebtSurveys()` |  |
| 621 | 7 | `private void recordPower(Game game)` |  |
| 630 | 3 | `public static String researchersKey(ResearchCatalog.Field field)` | The series of a field's researchers (the city's and the labs'): "researchersEngineering", "researchersMedicine", "researchersBusiness", "researchersLaw". |

### the trade by partner (WN3, 2026-10-28) (lines 640-658)

| line | len | member | says |
|---:|---:|---|---|
| 655 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 657 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 659-675)

### the city's fund (0.7.39) (lines 676-691)

### the new price model, read (0.7.45) (lines 692-757)

| line | len | member | says |
|---:|---:|---|---|
| 741 | 1 | `public static String indexKey(int component)` | The name each component's chained level is kept under (PriceIndex.COMPONENTS order): "indexGroceries" and so on. |
| 744 | 1 | `public static String weightKey(int component)` | ...and its weight in the basket in force: "weightGroceries" and so on. |
| 746 | 7 | `private static String capitalised(String s)` |  |
| 755 | 1 | `private List<List<Double>> indexLists()` | The component lists in COMPONENTS order, for the recording and the reform. |
| 756 | 1 | `private List<List<Double>> weightLists()` |  |

### RECORDING (lines 758-1079)

| line | len | member | says |
|---:|---:|---|---|
| 773 | 306 | `public void recordMonth(Game game)` | One month, read off the city itself. |

### THE FOLD (0.7.55) (lines 1080-1652)

| line | len | member | says |
|---:|---:|---|---|
| 1097 | 13 | `public void foldOldYears()` | Folds every calendar year whose months are all older than the newest MONTHLY_KEPT, oldest first; nothing in a history of MONTHLY_KEPT months or fewer. |
| 1112 | 10 | `private void foldYear(int from, int to)` | Axis entries [from, to) - one calendar year's months - into one entry at the year's last month, every series by its rule. |
| 1132 | 36 | `private static void foldSeries(String name, List<? extends Number> raw, int axis, int from, int to)` | One series' months for axis entries [from, to), folded in place by its rule (YearBook.kindOf()): FLOW added, RATE averaged, LEVEL - and a series nobody has given a rule, as the year book folds it - the last. |
| 1178 | 15 | `void appendMonth(int m, java.util.function.ToDoubleFunction<String> value)` | Harnesses only (0.7.55): one more month on every series this history has, each value asked of `value` by the series' name, then the fold recordMonth() ends with - so a history can be grown past MONTHLY_KEPT without a ... |
| 1206 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 1213 | 1 | `private static double round2(double v)` |  |
| 1214 | 1 | `private static double round4(double v)` |  |
| 1220 | 5 | `static double roundSig(double v)` | A price per founding share to SHARE_PRICE_DIGITS significant figures; 0 stays 0, and a figure that is not a number is kept as 0, as finite() keeps the rest (JSON holds no NaN). |
| 1227 | 4 | `static double atTodaysPrices(double d)` | The value at today's prices to SHARE_PRICE_DIGITS significant figures, before roundSig scales it back by FACTOR; a zero (and a negative one) stays itself. |
| 1233 | 3 | `static boolean onTodaysGrid(double d)` | True if d is a figure roundSig could have recorded: its six figures at today's prices, times FACTOR, give these very bits back; a figure that is not finite, or that a reform or a fold moved off the grid, is not. |
| 1237 | 1 | `private static double round6(double v)` | Six places, for a price in thousands that four would round to a dime - and to nothing after a reform (0.7.45: the shelf). |
| 1239 | 1 | `private static double finite(double v)` | A reading the file can hold: JSON has no NaN, so a figure not struck yet is kept as 0 (0.7.45's series). |
| 1252 | 224 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 1478 | 1 | `public List<PartnerTrade.Table> getTradeByPartner()` | The finished years of the city's trade by partner (WN3), oldest first; empty on a city with no world market or a history from before it. |
| 1481 | 1 | `public int tradeYears()` | How many finished years of trade by partner the history holds. |
| 1484 | 4 | `public void setTradeByPartner(List<PartnerTrade.Table> years)` | Sets the finished years (Game, when a year finishes): a copy of the list; the tables are immutable. |
| 1490 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 1507 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 1521 | 12 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |
| 1540 | 5 | `public HistorySave snapshot()` | A copy of this history for the writer thread (UP1, 2026-10-22): every series copied into new lists, whose entries are immutable numbers, by restoreFrom() - the one place every series is listed, so a series added later... |
| 1564 | 9 | `public Later snapshotLater()` | A snapshot of this history that is copied by whoever asks for it first (the save writer), or by the next change of the history. |
| 1575 | 6 | `private void settle()` | Makes every copy still owed, before the series change. |
| 1583 | 20 | **type** `public final class Later` | One snapshotLater(): the copy, made once, on the first get(). |
| 1587 | 1 | `private Later()` _(in HistorySave.Later)_ |  |
| 1590 | 12 | `public HistorySave get()` _(in HistorySave.Later)_ | The snapshot: this history as it stood when it was asked for, copied now if nobody has yet. |
| 1613 | 34 | `public HistorySave cutAt(int lastMonth)` | A copy of this history as it stood at {@code lastMonth} (TR1, 2026-10-31): only the axis entries whose month is at most it, and what every series held for them. |
| 1649 | 3 | `private static void dropEmpty(Map<String, List<Double>> series)` | The series of a keyed map that a cut left with no point, gone with their names (cutAt()). |

### READING (lines 1653-1780)

| line | len | member | says |
|---:|---:|---|---|
| 1662 | 1 | `public int months()` | How many entries the axis has - a month each, and since 0.7.55 a year each for the oldest yearlyPoints() of a history past MONTHLY_KEPT. |
| 1665 | 1 | `public List<Integer> getMonth()` | The axis: each entry's month, and a folded year's last month (0.7.55). |
| 1668 | 1 | `public int yearlyPoints()` | How many of the axis's entries, from the first, are folded years (0.7.55); 0 in a history of MONTHLY_KEPT months or fewer. |
| 1671 | 3 | `public int monthsIn(int i)` | The months axis entry i holds: 1 for a month, and a folded year's months (0.7.55). |
| 1676 | 3 | `public int firstMonthOf(int i)` | The first month axis entry i holds: its own month for a month, a folded year's first (0.7.55). |
| 1681 | 5 | `public int monthsLived()` | How many months the city has lived: every entry's months together (0.7.55) - months() until a year folds. |
| 1695 | 9 | `public int back(int i, int months)` | The entry `months` months before entry i (0.7.55): the month that far back, or the folded year that holds it - so "a year before" a folded year is the year before it, and before a month in the first year after the fol... |
| 1724 | 8 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 1743 | 21 | `public double[] alignedTail(String name, int n)` | The last {@code n} entries of aligned(name), and no more of it (UP2): the same figures as aligned(name)'s tail, value for value - the front padding as NaN, a folded year's flow as its monthly average where the tail re... |
| 1766 | 14 | `public double[] raw(String name)` | A series as it is stored, padded at the front as aligned() is: a folded year's flow is the year's sum (0.7.55). |

### a series' record, counted (0.7.9) (lines 1781-2587)

| line | len | member | says |
|---:|---:|---|---|
| 1794 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 1804 | 6 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 1812 | 6 | `public int monthsRecorded(String series)` | Months a series was recorded in - a folded year's months each (0.7.55). |
| 1820 | 5 | `public double total(String series)` | A flow added up over the months it was recorded: the stored figures, so a folded year adds its sum (0.7.55). |
| 1833 | 7 | `public double changeOver(String series, int months)` | How far a series has moved over the last `months` months, as a share of where it stood then: the last recorded value over the one `months` entries before it, less one (0.7.35: the Trade tab's rate this month and over ... |
| 1846 | 12 | `public double recentTotal(String series, int months)` | A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports). |
| 1864 | 16 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 1882 | 13 | `private static double worstYear(double[] a)` | worstYear()'s twelve months in a row, over a run of months. |
| 1902 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 1923 | 3 | `public double[] runningTotal(String series)` | ...and a series of this history summed from its first recorded entry (0.7.55): off the stored figures, so a folded year adds its sum - what City History's running totals read. |
| 1928 | 210 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 2140 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 2141 | 1 | `public List<Double> getGdp()` |  |
| 2142 | 1 | `public List<Double> getDebt()` |  |
| 2143 | 1 | `public List<Double> getInterestRate()` |  |
| 2144 | 1 | `public List<Long> getJobs()` |  |
| 2145 | 1 | `public List<Long> getWorkforce()` |  |
| 2146 | 1 | `public List<Long> getOutOfWork()` |  |
| 2147 | 1 | `public List<Long> getPopulation()` |  |
| 2160 | 61 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 2223 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |
| 2239 | 12 | `public HistorySave slim(java.util.Set<String> keep)` | A history with this one's axis and only the named series (LB3): every other series empty, so it reads as not recorded, a name this history has no series of ignored. |
| 2253 | 3 | `private void trimFoldedMonths()` | foldedMonths cut to the axis's length: a history can never hold more folded years than it has months (slim and readKept). |
| 2264 | 31 | `static HistorySave readKept(Reader in, Set<String> keep)` | What slim(keep) gives on the history in `in`, without the whole parse (LB4): every series but the kept ones is skipped as the JSON is read (Gson skips the key of a field the exclusion strategy drops, building nothing ... |
| 2297 | 16 | `private static List<Double> packed(List<Double> list)` | A series as a PackedDoubles, null entries kept; a null series is an empty one. |
| 2315 | 21 | `private static Set<String> keptFields(Set<String> keep)` | The names of the fields readKept reads: the axis and foldedMonths (all slim copies besides the series) and the field behind each kept series, found as slim finds it, through seriesByName(). |
| 2338 | 40 | **type** `private static final class PackedDoublesAdapter extends TypeAdapter<List<Double>>` | The Gson adapter of a kept List<Double> series: a PackedDoubles, null entries kept, a null list read as an empty one. |
| 2340 | 23 | `public List<Double> read(JsonReader in) throws IOException` _(in HistorySave.PackedDoublesAdapter)_ |  |
| 2365 | 12 | `public void write(JsonWriter out, List<Double> list) throws IOException` _(in HistorySave.PackedDoublesAdapter)_ |  |
| 2386 | 11 | **type** `private static final class TodaysSeries extends TypeAdapter<List<Double>>` | CU2 (REV-CU2-1): the file's form of the eight prices roundSig records (the wage, the rent, food, materials, ore, ground and each company's two share prices): six figures at today's prices write short, {"todays":[1.234... |
| 2388 | 3 | `public List<Double> read(JsonReader in) throws IOException` _(in HistorySave.TodaysSeries)_ |  |
| 2393 | 3 | `public void write(JsonWriter out, List<Double> list) throws IOException` _(in HistorySave.TodaysSeries)_ |  |
| 2399 | 31 | **type** `private static final class TodaysSeriesByCompany extends TypeAdapter<Map<String, List<Double>>>` | CU2 (REV-CU2-1): the same form for a company's series, an object of company to series, in the map's order, each series by the rule of TodaysSeries. |
| 2401 | 14 | `public Map<String, List<Double>> read(JsonReader in) throws IOException` _(in HistorySave.TodaysSeriesByCompany)_ |  |
| 2417 | 12 | `public void write(JsonWriter out, Map<String, List<Double>> byCompany) throws IOException` _(in HistorySave.TodaysSeriesByCompany)_ |  |
| 2432 | 27 | `private static void writeTodaysSeries(JsonWriter out, List<Double> list) throws IOException` | A series as {"todays":[...]} if it has a value and every value is on roundSig's grid, else as the plain array Gson writes a List<Double>; null stays null. |
| 2461 | 18 | `private static List<Double> readTodaysSeries(JsonReader in) throws IOException` | A series from either form: the plain array as written, or "todays" times FACTOR (the product roundSig makes); null entries stay where they were, a null series is null. |
| 2481 | 13 | `private static void readDoublesInto(JsonReader in, List<Double> into, boolean todays) throws IOException` | The array at the reader as boxed doubles, nulls kept; the entries of a "todays" array are multiplied by FACTOR, as roundSig multiplies, the others are the file's own doubles. |
| 2496 | 44 | **type** `private static final class PackedLongsAdapter extends TypeAdapter<List<Long>>` | The Gson adapter of a kept List<Long> series: a PackedLongs, null entries kept, a null list read as an empty one. |
| 2498 | 27 | `public List<Long> read(JsonReader in) throws IOException` _(in HistorySave.PackedLongsAdapter)_ |  |
| 2527 | 12 | `public void write(JsonWriter out, List<Long> list) throws IOException` _(in HistorySave.PackedLongsAdapter)_ |  |
| 2542 | 21 | **type** `static final class PackedDoubles extends AbstractList<Double> implements RandomAccess` | A kept List<Double> series as a double[] (LB4): read only, a null entry (a BitSet, allocated only when one is met) reads back as null, so equals and hashCode are the boxed list's. |
| 2546 | 4 | `PackedDoubles(double[] values, BitSet nulls)` _(in HistorySave.PackedDoubles)_ |  |
| 2552 | 3 | `public int size()` _(in HistorySave.PackedDoubles)_ |  |
| 2557 | 5 | `public Double get(int index)` _(in HistorySave.PackedDoubles)_ |  |
| 2565 | 21 | **type** `static final class PackedLongs extends AbstractList<Long> implements RandomAccess` | A kept List<Long> series as a long[] (LB4): read only, a null entry reads back as null, as PackedDoubles. |
| 2569 | 4 | `PackedLongs(long[] values, BitSet nulls)` _(in HistorySave.PackedLongs)_ |  |
| 2575 | 3 | `public int size()` _(in HistorySave.PackedLongs)_ |  |
| 2580 | 5 | `public Long get(int index)` _(in HistorySave.PackedLongs)_ |  |

