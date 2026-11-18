# HistorySave.java - 2,446 lines · 94 methods · 6 constants · model

`ham/citybuildersim/HistorySave.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

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

**Uses:** [YearBook](YearBook.md) (8), [AgeBand](AgeBand.md) (7), [PriceIndex](PriceIndex.md) (6), [PartnerTrade](PartnerTrade.md) (5), [Crime](Crime.md) (4), [PriceLevel](PriceLevel.md) (4), [FamilyStructure](FamilyStructure.md) (3), [ResearchCatalog](ResearchCatalog.md) (3), [WorldMoneys](WorldMoneys.md) (3), [Equity](Equity.md) (3), [CityCalendar](CityCalendar.md) (3), [LicenceFees](LicenceFees.md) (2), [GameFiles](GameFiles.md) (2), [Game](Game.md) (1), [EconomyManager](EconomyManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [PopulationManager](PopulationManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Migration](Migration.md) (1), [LabourMarket](LabourMarket.md) (1), [JobType](JobType.md) (1), [WageBand](WageBand.md) (1), [Education](Education.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Nations](Nations.md) (1), [PolicyRates](PolicyRates.md) (1), [Bank](Bank.md) (1), [CentralBank](CentralBank.md) (1), [Unemployment](Unemployment.md) (1)... and 8 more

**Used by (55):** [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuybackCheck](BuybackCheck.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [CityMeasures](CityMeasures.md), [ClockCheck](ClockCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DenominationCheck](DenominationCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GovernmentScreen](GovernmentScreen.md), [GovernmentView](GovernmentView.md), [GovernmentViewCheck](GovernmentViewCheck.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [InboxCheck](InboxCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [KeptBench](KeptBench.md), [LandScreen](LandScreen.md), [LicenceFeesCheck](LicenceFeesCheck.md), [PartnersCheck](PartnersCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PriceLevelCheck](PriceLevelCheck.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [Recording](Recording.md), [ReplayCheck](ReplayCheck.md), [ReplayCity](ReplayCity.md), [ReplayGraphs](ReplayGraphs.md), [ResearchScreen](ResearchScreen.md), [ResearchViewCheck](ResearchViewCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md), [SavedCities](SavedCities.md), [ScaleCheck](ScaleCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [UiProbe](UiProbe.md), [UserInterface](UserInterface.md), [VaultTable](VaultTable.md), [VaultView](VaultView.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 89 | · THE AXIS |
| 112 | · money |
| 128 | · people |
| 169 | · what throttles |
| 186 | · prices |
| 197 | · the edge |
| 256 | · money and credit |
| 301 | · what the bank could carry, and how hard |
| 322 | · the price of money (0.7.7) |
| 337 | · the bank's capital (0.7.8) |
| 366 | · the budget |
| 382 | · housing |
| 392 | · school and care |
| 398 | · outside the families |
| 427 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 441 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 449 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 459 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 475 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 497 | · what runs out |
| 501 | · the market |
| 525 | · the sectors (0.7.4) |
| 545 | · the world's moneys (WN4, 2026-10-30) |
| 569 | · the research (R5, 2026-11-06) |
| 601 | · the trade by partner (WN3, 2026-10-28) |
| 620 | · GDP in layers (0.7.6) |
| 637 | · the city's fund (0.7.39) |
| 653 | · the new price model, read (0.7.45) |
| 705 | RECORDING |
| 783 | · · the edge |
| 813 | · · money and credit |
| 860 | · · the budget |
| 870 | · · housing |
| 875 | · · school and care |
| 882 | · · outside the families |
| 929 | · · what runs out |
| 933 | · · the market |
| 946 | · · the sectors |
| 954 | · · the new price model, read (0.7.45) |
| 976 | · · the research (R5) |
| 992 | · · the city's fund (0.7.39) |
| 1004 | THE FOLD (0.7.55) |
| 1538 | READING |
| 1666 | · a series' record, counted (0.7.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 110 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 567 | `HistorySave.WORLD_CYCLE` | `"worldCycle", WORLD_RISK_OFF = "worldRiskOff"` | The dollar cycle's series and the risk-off's. |
| 596 | `HistorySave.RESEARCH_SPEND` | `"researchSpend"` | The month's research bill and prizes paid, the city's money. |
| 599 | `HistorySave.EQUIPMENT_FITTED` | `"equipmentFitted"` | The share of the equipment standing that is fitted, 0 to 1; begins when there is equipment to count. |
| 1113 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "d...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 1135 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5); and, since EP1, so are the wage, the rent and the prices of food, materials, ... |

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
| 262 | `private List<Double> priceIndex` |  |
| 273 | `private List<Double> usPrices` | The game dollar's price level since founding (LB3, 2026-10-31): the US prices (WorldEconomy.getPriceLevel()), 1 at founding, the "P" the Rankings board divides a city's money by to reach a dollar of 2015. |
| 280 | `private List<Double> expectedInflation` | THE ANCHOR (0.7.42): what the city expected inflation to be, a fraction a year, and how far it believed the bank (Expectations) - both ratios, so a reform leaves them alone. |
| 281 | `private List<Double> credibility` |  |
| 282 | `private List<Double> businessDebt` |  |
| 289 | `private List<Double> businessBonds` | The face of every business bond outstanding, in money (HD1, 2026-10-29): businessDebt is the loans and mortgages only, so the fade of the bonds (the companies that borrow in the bond market, the funds that hold them) ... |
| 296 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 297 | `private List<Double> bankLent` |  |
| 298 | `private List<Double> bankEquity` |  |
| 299 | `private List<Double> bankWriteOffs` |  |
| 316 | `private List<Double> bankCapacity` |  |
| 317 | `private List<Double> bankStrain` |  |
| 318 | `private List<Double> bankProfit` |  |
| 319 | `private List<Double> bankBranches` |  |
| 320 | `private List<Double> householdSavings` |  |
| 332 | `private List<Double> policyRate` |  |
| 333 | `private List<Double> bankPrime` |  |
| 334 | `private List<Double> bankDepositRate` |  |
| 335 | `private List<Double> bankFees` |  |
| 347 | `private List<Double> bankCapitalRatio` |  |
| 348 | `private List<Double> bankCapitalTarget` |  |
| 349 | `private List<Double> bankAllowance` |  |
| 350 | `private List<Double> bankProvisions` |  |
| 351 | `private List<Double> bankDividends` |  |
| 352 | `private List<Double> bankReturnOnEquity` |  |
| 363 | `private List<Double> bankLeverageRatio` | ...AND THE OTHER MEASURE (A7, 0.7.46; the Bank spec's D11): its equity over everything on its sheet (Bank.leverageRatio()) and the target it holds on that measure (leverageTarget()). |
| 364 | `private List<Double> bankLeverageTarget` |  |
| 373 | `private List<Double> taxWage` |  |
| 374 | `private List<Double> taxProperty` |  |
| 375 | `private List<Double> taxSales` |  |
| 376 | `private List<Double> taxBusiness` |  |
| 377 | `private List<Double> taxIndustrial` |  |
| 378 | `private List<Double> contributions` |  |
| 379 | `private List<Double> pensionBill` |  |
| 380 | `private List<Double> healthBill` |  |
| 388 | `private List<Double> rentPrice` |  |
| 389 | `private List<Long> homes` |  |
| 390 | `private List<Double> households` |  |
| 393 | `private List<Double> students` |  |
| 394 | `private List<Double> graduates` |  |
| 395 | `private List<Double> licences` |  |
| 396 | `private List<Double> unburied` |  |
| 405 | `private List<Double> outOfWorkOnEi` |  |
| 406 | `private List<Double> outOfWorkOffEi` |  |
| 407 | `private List<Double> unhoused` |  |
| 408 | `private List<Double> orphans` |  |
| 409 | `private List<Double> evicted` |  |
| 410 | `private List<Double> eiPaid` |  |
| 411 | `private List<Double> eiPremiums` |  |
| 413 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 414 | `private List<Double> studentGrants` |  |
| 421 | `private List<Double> foodAssistance` | FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households' groceries a month, and the baskets that bought at the price charged. |
| 422 | `private List<Double> fedByAssistance` |  |
| 423 | `private List<Double> studentLoansOwed` |  |
| 425 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 435 | `private List<Double> m0` |  |
| 436 | `private List<Double> m2` |  |
| 437 | `private List<Double> advancesToTreasury` |  |
| 438 | `private List<Double> reserves` |  |
| 439 | `private List<Double> remittance` |  |
| 445 | `private List<Double> sickPastTwoMonths` |  |
| 446 | `private List<Double> diedOfIllness` |  |
| 447 | `private List<Double> sickRecovery` |  |
| 457 | `private Map<String, List<Double>> householdsByShape` |  |
| 465 | `private List<Double> deathsBabies` |  |
| 466 | `private List<Double> deathsChildren` |  |
| 467 | `private List<Double> deathsTeens` |  |
| 468 | `private List<Double> deathsAdults` |  |
| 469 | `private List<Double> deathsSeniors` |  |
| 471 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 472 | `private List<Double> deathsOrphans` |  |
| 473 | `private List<Double> deathsUnhoused` |  |
| 482 | `private List<Double> crimeRate` |  |
| 483 | `private List<Double> policeCoverage` |  |
| 484 | `private List<Double> prisoners` |  |
| 485 | `private List<Double> caughtNotHeld` |  |
| 486 | `private List<Double> stolen` |  |
| 487 | `private List<Double> deathsKilled` |  |
| 488 | `private List<Double> safetyBill` |  |
| 489 | `private Map<String, List<Double>> crimeByCause` |  |
| 498 | `private List<Long> constructionCapacity` |  |
| 499 | `private List<Double> landUse` |  |
| 518 | `private Map<String, List<Double>> sharePrice` |  |
| 519 | `private Map<String, List<Double>> shareValue` |  |
| 542 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 543 | `private Map<String, List<Double>> sectorWorkers` |  |
| 558 | `private Map<String, List<Double>> worldMoney` |  |
| 588 | `private Map<String, List<Double>> researchSeries` |  |
| 613 | `private List<PartnerTrade.Table> tradeByPartner` |  |
| 632 | `private List<Double> consumption` |  |
| 633 | `private List<Double> investment` |  |
| 634 | `private List<Double> government` |  |
| 635 | `private List<Double> netExports` |  |
| 649 | `private List<Double> fundValue` |  |
| 650 | `private List<Double> fundPutIn` |  |
| 651 | `private List<Double> fundTakenOut` |  |
| 669 | `private List<Double> expectedLevel` |  |
| 670 | `private List<Double> indexGroceries` |  |
| 671 | `private List<Double> indexRent` |  |
| 672 | `private List<Double> indexMeals` |  |
| 673 | `private List<Double> indexLuxury` |  |
| 674 | `private List<Double> indexServices` |  |
| 675 | `private List<Double> weightGroceries` |  |
| 676 | `private List<Double> weightRent` |  |
| 677 | `private List<Double> weightMeals` |  |
| 678 | `private List<Double> weightLuxury` |  |
| 679 | `private List<Double> weightServices` |  |
| 680 | `private List<Double> basketLinkedAt` |  |
| 681 | `private List<Double> shelfPrice` |  |
| 682 | `private List<Double> shelfFloor` |  |
| 683 | `private List<Double> basketsAsked` |  |
| 684 | `private List<Double> basketsHanded` |  |
| 685 | `private List<Double> hunger` |  |
| 686 | `private List<Double> hungerPricedOut` |  |
| 687 | `private List<Double> householdsAssisted` |  |
| 688 | `private List<Double> mealMargin` |  |
| 689 | `private List<Double> mealTargetMargin` |  |
| 690 | `private List<Double> luxuryMargin` |  |
| 691 | `private List<Double> luxuryTargetMargin` |  |
| 1445 | `private transient List<Later> owed` | The snapshots taken with snapshotLater() and not yet copied; guarded by this object's monitor. |
| 1448 | `private transient volatile boolean anyOwed` | True while {@link #owed} holds a snapshot: read by every mutator without the lock, so a city that is not being saved pays a field read. |
| 1472 | `private HistorySave copy` |  |
| 2402 | `private final double[] values` |  |
| 2403 | `private final BitSet nulls` |  |
| 2425 | `private final long[] values` |  |
| 2426 | `private final BitSet nulls` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 87 | 2360 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 89-111)

### money (lines 112-127)

### people (lines 128-168)

### what throttles (lines 169-185)

### prices (lines 186-196)

### the edge (lines 197-255)

### money and credit (lines 256-300)

### what the bank could carry, and how hard (lines 301-321)

### the price of money (0.7.7) (lines 322-336)

### the bank's capital (0.7.8) (lines 337-365)

### the budget (lines 366-381)

### housing (lines 382-391)

### school and care (lines 392-397)

### outside the families (lines 398-426)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 427-440)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 441-448)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 449-458)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 459-474)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 475-496)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 495 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 497-500)

### the market (lines 501-524)

| line | len | member | says |
|---:|---:|---|---|
| 522 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 523 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 525-544)

### the world's moneys (WN4, 2026-10-30) (lines 545-568)

| line | len | member | says |
|---:|---:|---|---|
| 561 | 1 | `public static String worldRateKey(String code)` | The series of a major's units to the US dollar: "fxEUR". |
| 564 | 1 | `public static String policyRateKey(String code)` | The series of a central bank's policy rate, a fraction a year (XR4): "policyUSD", for each of the eight, the dollar's too. |

### the research (R5, 2026-11-06) (lines 569-600)

| line | len | member | says |
|---:|---:|---|---|
| 591 | 3 | `public static String researchersKey(ResearchCatalog.Field field)` | The series of a field's researchers (the city's and the labs'): "researchersEngineering", "researchersMedicine", "researchersBusiness", "researchersLaw". |

### the trade by partner (WN3, 2026-10-28) (lines 601-619)

| line | len | member | says |
|---:|---:|---|---|
| 616 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 618 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 620-636)

### the city's fund (0.7.39) (lines 637-652)

### the new price model, read (0.7.45) (lines 653-704)

| line | len | member | says |
|---:|---:|---|---|
| 694 | 1 | `public static String indexKey(int component)` | The name each component's chained level is kept under (PriceIndex.COMPONENTS order): "indexGroceries" and so on. |
| 697 | 1 | `public static String weightKey(int component)` | ...and its weight in the basket in force: "weightGroceries" and so on. |
| 699 | 1 | `private static String capitalised(String s)` |  |
| 702 | 1 | `private List<List<Double>> indexLists()` | The component lists in COMPONENTS order, for the recording and the reform. |
| 703 | 1 | `private List<List<Double>> weightLists()` |  |

### RECORDING (lines 705-1003)

| line | len | member | says |
|---:|---:|---|---|
| 720 | 283 | `public void recordMonth(Game game)` | One month, read off the city itself. |

### THE FOLD (0.7.55) (lines 1004-1537)

| line | len | member | says |
|---:|---:|---|---|
| 1021 | 13 | `public void foldOldYears()` | Folds every calendar year whose months are all older than the newest MONTHLY_KEPT, oldest first; nothing in a history of MONTHLY_KEPT months or fewer. |
| 1036 | 10 | `private void foldYear(int from, int to)` | Axis entries [from, to) - one calendar year's months - into one entry at the year's last month, every series by its rule. |
| 1056 | 36 | `private static void foldSeries(String name, List<? extends Number> raw, int axis, int from, int to)` | One series' months for axis entries [from, to), folded in place by its rule (YearBook.kindOf()): FLOW added, RATE averaged, LEVEL - and a series nobody has given a rule, as the year book folds it - the last. |
| 1100 | 11 | `void appendMonth(int m, java.util.function.ToDoubleFunction<String> value)` | Harnesses only (0.7.55): one more month on every series this history has, each value asked of `value` by the series' name, then the fold recordMonth() ends with - so a history can be grown past MONTHLY_KEPT without a ... |
| 1124 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 1131 | 1 | `private static double round2(double v)` |  |
| 1132 | 1 | `private static double round4(double v)` |  |
| 1138 | 5 | `static double roundSig(double v)` | A price per founding share to SHARE_PRICE_DIGITS significant figures; 0 stays 0, and a figure that is not a number is kept as 0, as finite() keeps the rest (JSON holds no NaN). |
| 1145 | 4 | `static double atTodaysPrices(double d)` | The value at today's prices to SHARE_PRICE_DIGITS significant figures, before roundSig scales it back by FACTOR; a zero (and a negative one) stays itself. |
| 1151 | 3 | `static boolean onTodaysGrid(double d)` | True if d is a figure roundSig could have recorded: its six figures at today's prices, times FACTOR, give these very bits back; a figure that is not finite, or that a reform or a fold moved off the grid, is not. |
| 1155 | 1 | `private static double round6(double v)` | Six places, for a price in thousands that four would round to a dime - and to nothing after a reform (0.7.45: the shelf). |
| 1157 | 1 | `private static double finite(double v)` | A reading the file can hold: JSON has no NaN, so a figure not struck yet is kept as 0 (0.7.45's series). |
| 1170 | 193 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 1365 | 1 | `public List<PartnerTrade.Table> getTradeByPartner()` | The finished years of the city's trade by partner (WN3), oldest first; empty on a city with no world market or a history from before it. |
| 1368 | 1 | `public int tradeYears()` | How many finished years of trade by partner the history holds. |
| 1371 | 4 | `public void setTradeByPartner(List<PartnerTrade.Table> years)` | Sets the finished years (Game, when a year finishes): a copy of the list; the tables are immutable. |
| 1377 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 1394 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 1408 | 12 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |
| 1427 | 5 | `public HistorySave snapshot()` | A copy of this history for the writer thread (UP1, 2026-10-22): every series copied into new lists, whose entries are immutable numbers, by restoreFrom() - the one place every series is listed, so a series added later... |
| 1451 | 9 | `public Later snapshotLater()` | A snapshot of this history that is copied by whoever asks for it first (the save writer), or by the next change of the history. |
| 1462 | 6 | `private void settle()` | Makes every copy still owed, before the series change. |
| 1470 | 20 | **type** `public final class Later` | One snapshotLater(): the copy, made once, on the first get(). |
| 1474 | 1 | `private Later()` _(in HistorySave.Later)_ |  |
| 1477 | 12 | `public HistorySave get()` _(in HistorySave.Later)_ | The snapshot: this history as it stood when it was asked for, copied now if nobody has yet. |
| 1500 | 32 | `public HistorySave cutAt(int lastMonth)` | A copy of this history as it stood at {@code lastMonth} (TR1, 2026-10-31): only the axis entries whose month is at most it, and what every series held for them. |
| 1534 | 3 | `private static void dropEmpty(Map<String, List<Double>> series)` | The series of a keyed map that a cut left with no point, gone with their names (cutAt()). |

### READING (lines 1538-1665)

| line | len | member | says |
|---:|---:|---|---|
| 1547 | 1 | `public int months()` | How many entries the axis has - a month each, and since 0.7.55 a year each for the oldest yearlyPoints() of a history past MONTHLY_KEPT. |
| 1550 | 1 | `public List<Integer> getMonth()` | The axis: each entry's month, and a folded year's last month (0.7.55). |
| 1553 | 1 | `public int yearlyPoints()` | How many of the axis's entries, from the first, are folded years (0.7.55); 0 in a history of MONTHLY_KEPT months or fewer. |
| 1556 | 3 | `public int monthsIn(int i)` | The months axis entry i holds: 1 for a month, and a folded year's months (0.7.55). |
| 1561 | 3 | `public int firstMonthOf(int i)` | The first month axis entry i holds: its own month for a month, a folded year's first (0.7.55). |
| 1566 | 5 | `public int monthsLived()` | How many months the city has lived: every entry's months together (0.7.55) - months() until a year folds. |
| 1580 | 9 | `public int back(int i, int months)` | The entry `months` months before entry i (0.7.55): the month that far back, or the folded year that holds it - so "a year before" a folded year is the year before it, and before a month in the first year after the fol... |
| 1609 | 8 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 1628 | 21 | `public double[] alignedTail(String name, int n)` | The last {@code n} entries of aligned(name), and no more of it (UP2): the same figures as aligned(name)'s tail, value for value - the front padding as NaN, a folded year's flow as its monthly average where the tail re... |
| 1651 | 14 | `public double[] raw(String name)` | A series as it is stored, padded at the front as aligned() is: a folded year's flow is the year's sum (0.7.55). |

### a series' record, counted (0.7.9) (lines 1666-2446)

| line | len | member | says |
|---:|---:|---|---|
| 1679 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 1689 | 6 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 1697 | 6 | `public int monthsRecorded(String series)` | Months a series was recorded in - a folded year's months each (0.7.55). |
| 1705 | 5 | `public double total(String series)` | A flow added up over the months it was recorded: the stored figures, so a folded year adds its sum (0.7.55). |
| 1718 | 7 | `public double changeOver(String series, int months)` | How far a series has moved over the last `months` months, as a share of where it stood then: the last recorded value over the one `months` entries before it, less one (0.7.35: the Trade tab's rate this month and over ... |
| 1731 | 12 | `public double recentTotal(String series, int months)` | A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports). |
| 1749 | 16 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 1767 | 13 | `private static double worstYear(double[] a)` | worstYear()'s twelve months in a row, over a run of months. |
| 1787 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 1808 | 3 | `public double[] runningTotal(String series)` | ...and a series of this history summed from its first recorded entry (0.7.55): off the stored figures, so a folded year adds its sum - what City History's running totals read. |
| 1813 | 188 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 2003 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 2004 | 1 | `public List<Double> getGdp()` |  |
| 2005 | 1 | `public List<Double> getDebt()` |  |
| 2006 | 1 | `public List<Double> getInterestRate()` |  |
| 2007 | 1 | `public List<Long> getJobs()` |  |
| 2008 | 1 | `public List<Long> getWorkforce()` |  |
| 2009 | 1 | `public List<Long> getOutOfWork()` |  |
| 2010 | 1 | `public List<Long> getPopulation()` |  |
| 2023 | 57 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 2082 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |
| 2098 | 12 | `public HistorySave slim(java.util.Set<String> keep)` | A history with this one's axis and only the named series (LB3): every other series empty, so it reads as not recorded, a name this history has no series of ignored. |
| 2112 | 3 | `private void trimFoldedMonths()` | foldedMonths cut to the axis's length: a history can never hold more folded years than it has months (slim and readKept). |
| 2123 | 31 | `static HistorySave readKept(Reader in, Set<String> keep)` | What slim(keep) gives on the history in `in`, without the whole parse (LB4): every series but the kept ones is skipped as the JSON is read (Gson skips the key of a field the exclusion strategy drops, building nothing ... |
| 2156 | 16 | `private static List<Double> packed(List<Double> list)` | A series as a PackedDoubles, null entries kept; a null series is an empty one. |
| 2174 | 21 | `private static Set<String> keptFields(Set<String> keep)` | The names of the fields readKept reads: the axis and foldedMonths (all slim copies besides the series) and the field behind each kept series, found as slim finds it, through seriesByName(). |
| 2197 | 40 | **type** `private static final class PackedDoublesAdapter extends TypeAdapter<List<Double>>` | The Gson adapter of a kept List<Double> series: a PackedDoubles, null entries kept, a null list read as an empty one. |
| 2199 | 23 | `public List<Double> read(JsonReader in) throws IOException` _(in HistorySave.PackedDoublesAdapter)_ |  |
| 2224 | 12 | `public void write(JsonWriter out, List<Double> list) throws IOException` _(in HistorySave.PackedDoublesAdapter)_ |  |
| 2245 | 11 | **type** `private static final class TodaysSeries extends TypeAdapter<List<Double>>` | CU2 (REV-CU2-1): the file's form of the eight prices roundSig records (the wage, the rent, food, materials, ore, ground and each company's two share prices): six figures at today's prices write short, {"todays":[1.234... |
| 2247 | 3 | `public List<Double> read(JsonReader in) throws IOException` _(in HistorySave.TodaysSeries)_ |  |
| 2252 | 3 | `public void write(JsonWriter out, List<Double> list) throws IOException` _(in HistorySave.TodaysSeries)_ |  |
| 2258 | 31 | **type** `private static final class TodaysSeriesByCompany extends TypeAdapter<Map<String, List<Double>>>` | CU2 (REV-CU2-1): the same form for a company's series, an object of company to series, in the map's order, each series by the rule of TodaysSeries. |
| 2260 | 14 | `public Map<String, List<Double>> read(JsonReader in) throws IOException` _(in HistorySave.TodaysSeriesByCompany)_ |  |
| 2276 | 12 | `public void write(JsonWriter out, Map<String, List<Double>> byCompany) throws IOException` _(in HistorySave.TodaysSeriesByCompany)_ |  |
| 2291 | 27 | `private static void writeTodaysSeries(JsonWriter out, List<Double> list) throws IOException` | A series as {"todays":[...]} if it has a value and every value is on roundSig's grid, else as the plain array Gson writes a List<Double>; null stays null. |
| 2320 | 18 | `private static List<Double> readTodaysSeries(JsonReader in) throws IOException` | A series from either form: the plain array as written, or "todays" times FACTOR (the product roundSig makes); null entries stay where they were, a null series is null. |
| 2340 | 13 | `private static void readDoublesInto(JsonReader in, List<Double> into, boolean todays) throws IOException` | The array at the reader as boxed doubles, nulls kept; the entries of a "todays" array are multiplied by FACTOR, as roundSig multiplies, the others are the file's own doubles. |
| 2355 | 44 | **type** `private static final class PackedLongsAdapter extends TypeAdapter<List<Long>>` | The Gson adapter of a kept List<Long> series: a PackedLongs, null entries kept, a null list read as an empty one. |
| 2357 | 27 | `public List<Long> read(JsonReader in) throws IOException` _(in HistorySave.PackedLongsAdapter)_ |  |
| 2386 | 12 | `public void write(JsonWriter out, List<Long> list) throws IOException` _(in HistorySave.PackedLongsAdapter)_ |  |
| 2401 | 21 | **type** `static final class PackedDoubles extends AbstractList<Double> implements RandomAccess` | A kept List<Double> series as a double[] (LB4): read only, a null entry (a BitSet, allocated only when one is met) reads back as null, so equals and hashCode are the boxed list's. |
| 2405 | 4 | `PackedDoubles(double[] values, BitSet nulls)` _(in HistorySave.PackedDoubles)_ |  |
| 2411 | 3 | `public int size()` _(in HistorySave.PackedDoubles)_ |  |
| 2416 | 5 | `public Double get(int index)` _(in HistorySave.PackedDoubles)_ |  |
| 2424 | 21 | **type** `static final class PackedLongs extends AbstractList<Long> implements RandomAccess` | A kept List<Long> series as a long[] (LB4): read only, a null entry reads back as null, as PackedDoubles. |
| 2428 | 4 | `PackedLongs(long[] values, BitSet nulls)` _(in HistorySave.PackedLongs)_ |  |
| 2434 | 3 | `public int size()` _(in HistorySave.PackedLongs)_ |  |
| 2439 | 5 | `public Long get(int index)` _(in HistorySave.PackedLongs)_ |  |

