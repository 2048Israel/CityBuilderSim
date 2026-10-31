# HistorySave.java - 1,865 lines · 63 methods · 4 constants · model

`ham/citybuildersim/HistorySave.java` - generated 2026-10-31 by CodeMap; line numbers are as of that run.

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

**Uses:** [YearBook](YearBook.md) (8), [AgeBand](AgeBand.md) (7), [PriceIndex](PriceIndex.md) (6), [PartnerTrade](PartnerTrade.md) (5), [Crime](Crime.md) (4), [FamilyStructure](FamilyStructure.md) (3), [WorldMoneys](WorldMoneys.md) (3), [Equity](Equity.md) (3), [CityCalendar](CityCalendar.md) (2), [GameFiles](GameFiles.md) (2), [Game](Game.md) (1), [EconomyManager](EconomyManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [PopulationManager](PopulationManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Migration](Migration.md) (1), [LabourMarket](LabourMarket.md) (1), [JobType](JobType.md) (1), [WageBand](WageBand.md) (1), [Education](Education.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Nations](Nations.md) (1), [PolicyRates](PolicyRates.md) (1), [Bank](Bank.md) (1), [CentralBank](CentralBank.md) (1), [Unemployment](Unemployment.md) (1), [FamilyModel](FamilyModel.md) (1), [Sickness](Sickness.md) (1), [Exchange](Exchange.md) (1)... and 3 more

**Used by (35):** [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuybackCheck](BuybackCheck.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [CityMeasures](CityMeasures.md), [ClockCheck](ClockCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md), [ScaleCheck](ScaleCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 66 | · THE AXIS |
| 89 | · money |
| 105 | · people |
| 146 | · what throttles |
| 163 | · prices |
| 174 | · the edge |
| 229 | · money and credit |
| 263 | · what the bank could carry, and how hard |
| 284 | · the price of money (0.7.7) |
| 299 | · the bank's capital (0.7.8) |
| 328 | · the budget |
| 344 | · housing |
| 354 | · school and care |
| 360 | · outside the families |
| 389 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 403 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 411 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 421 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 437 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 459 | · what runs out |
| 463 | · the market |
| 487 | · the sectors (0.7.4) |
| 507 | · the world's moneys (WN4, 2026-10-30) |
| 531 | · the trade by partner (WN3, 2026-10-28) |
| 550 | · GDP in layers (0.7.6) |
| 567 | · the city's fund (0.7.39) |
| 583 | · the new price model, read (0.7.45) |
| 635 | RECORDING |
| 710 | · · the edge |
| 738 | · · money and credit |
| 784 | · · the budget |
| 794 | · · housing |
| 799 | · · school and care |
| 806 | · · outside the families |
| 853 | · · what runs out |
| 857 | · · the market |
| 870 | · · the sectors |
| 878 | · · the new price model, read (0.7.45) |
| 900 | · · the city's fund (0.7.39) |
| 912 | THE FOLD (0.7.55) |
| 1321 | READING |
| 1449 | · a series' record, counted (0.7.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 529 | `HistorySave.WORLD_CYCLE` | `"worldCycle", WORLD_RISK_OFF = "worldRiskOff"` | The dollar cycle's series and the risk-off's. |
| 1019 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "d...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 1041 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 73 | `private List<Integer> month` |  |
| 84 | `private List<Integer> foldedMonths` | How many months each folded year holds, oldest first (0.7.55): the first foldedMonths.size() entries of the axis, and of every series lined up with it, are years - see PAST FIVE HUNDRED YEARS above. |
| 90 | `private List<Double> cash` |  |
| 91 | `private List<Double> gdp` |  |
| 92 | `private List<Double> debt` |  |
| 93 | `private List<Double> interestRate` |  |
| 96 | `private List<Double> revenue` | Government revenue and what it kept, both monthly. |
| 97 | `private List<Double> surplus` |  |
| 100 | `private List<Double> retiredGain` | The gain (loss) on debt retired, monthly (CD4, CD3's ★5): the surplus carries it and spending does not, so a year's spending is revenue less the surplus PLUS this - without it the year netted a buyback's gain into spe... |
| 103 | `private List<Double> debtExchange` | The exchange gain (loss) on the city's foreign-currency debt, monthly (CD7): the surplus carries it and spending does not, and no cash moved on it, so the rollover's netting and the fund's pay-in read the surplus LESS... |
| 106 | `private List<Long> jobs` |  |
| 107 | `private List<Long> workforce` |  |
| 116 | `private List<Long> outOfWork` | The people out of work - the labour force less the posts FILLED, the People screen's figure (PopulationManager.getUnemployed()). |
| 117 | `private List<Long> population` |  |
| 120 | `private List<Long> births` | The four flows that move the population, and only these four move it. |
| 121 | `private List<Long> deaths` |  |
| 122 | `private List<Long> arrivals` |  |
| 123 | `private List<Long> departures` |  |
| 126 | `private List<Double> totalWage` | The wage BILL, not the average - the average needs the workforce too. |
| 129 | `private List<Double> minimumWage` | The dial the player sets, and what the labour market did with it. |
| 139 | `private List<Double> unskilledPremium` | What an unskilled hour costs relative to its base, and how much of the workforce has any training at all. |
| 140 | `private List<Double> skilledShare` |  |
| 143 | `private List<Double> schoolCoverage` | The schools: how much of the basic ladder is covered, and what it costs. |
| 144 | `private List<Double> schoolBill` |  |
| 147 | `private List<Double> energyRatio` |  |
| 148 | `private List<Double> waterRatio` |  |
| 149 | `private List<Double> roadRatio` |  |
| 150 | `private List<Double> sickRate` |  |
| 151 | `private List<Double> careCoverage` |  |
| 161 | `private List<Double> outbreak` | The outbreak running, as extra absence on top of what the city's care explains (Health.getOutbreakSeverity()); 0 between outbreaks. |
| 169 | `private List<Double> landPrice` | What the world asks the city for a square foot of ground, in thousands of US DOLLARS since 0.7.6 (LandManager.getGroundUsdPerSqFt()); the months an older save recorded are local money, and stay as recorded. |
| 170 | `private List<Double> foodPrice` |  |
| 171 | `private List<Double> materialsPrice` |  |
| 172 | `private List<Double> orePrice` |  |
| 187 | `private List<Double> fxRate` |  |
| 194 | `private List<Double> fxParity` | Parity beside the rate (0.7.35): where a basket costs the same here and abroad, in the rate's own units (ForeignAccounts.getParity()), so the Trade tab can draw the two on one chart. |
| 202 | `private List<Double> fxFair` | The fundamentals' level beside the rate (XR1, 2026-10-21): where parity's trend and the real gap, the balance and the cover put the D$ before the market and the interventions (ForeignAccounts.getFairLevel()), in the r... |
| 208 | `private List<Double> fxMarket` | ...and how far the rate sat from that level, a fraction, positive weaker (ForeignAccounts.deviationFromFair()): the market's swing and the interventions, a ratio and not money. |
| 216 | `private List<Double> fxShadow` | The shadow rate (XR2, 2026-10-22): where the float's rule put the D$ at the month's reprice, in the rate's own units (ForeignAccounts .getShadowRate()) - the rate itself under Lean and Float, the rate the hold is set ... |
| 222 | `private List<Double> fxCover` | ...and the vault's cover, in months of imports (ForeignAccounts .importCover(), at most 99): the hold's reserve, beside the shadow. |
| 223 | `private List<Double> reservesUsd` |  |
| 224 | `private List<Double> foreignDebtUsd` |  |
| 225 | `private List<Double> currentAccount` |  |
| 226 | `private List<Double> exportsAbroad` |  |
| 227 | `private List<Double> importsAbroad` |  |
| 235 | `private List<Double> priceIndex` |  |
| 242 | `private List<Double> expectedInflation` | THE ANCHOR (0.7.42): what the city expected inflation to be, a fraction a year, and how far it believed the bank (Expectations) - both ratios, so a reform leaves them alone. |
| 243 | `private List<Double> credibility` |  |
| 244 | `private List<Double> businessDebt` |  |
| 251 | `private List<Double> businessBonds` | The face of every business bond outstanding, in money (HD1, 2026-10-29): businessDebt is the loans and mortgages only, so the fade of the bonds (the companies that borrow in the bond market, the funds that hold them) ... |
| 258 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 259 | `private List<Double> bankLent` |  |
| 260 | `private List<Double> bankEquity` |  |
| 261 | `private List<Double> bankWriteOffs` |  |
| 278 | `private List<Double> bankCapacity` |  |
| 279 | `private List<Double> bankStrain` |  |
| 280 | `private List<Double> bankProfit` |  |
| 281 | `private List<Double> bankBranches` |  |
| 282 | `private List<Double> householdSavings` |  |
| 294 | `private List<Double> policyRate` |  |
| 295 | `private List<Double> bankPrime` |  |
| 296 | `private List<Double> bankDepositRate` |  |
| 297 | `private List<Double> bankFees` |  |
| 309 | `private List<Double> bankCapitalRatio` |  |
| 310 | `private List<Double> bankCapitalTarget` |  |
| 311 | `private List<Double> bankAllowance` |  |
| 312 | `private List<Double> bankProvisions` |  |
| 313 | `private List<Double> bankDividends` |  |
| 314 | `private List<Double> bankReturnOnEquity` |  |
| 325 | `private List<Double> bankLeverageRatio` | ...AND THE OTHER MEASURE (A7, 0.7.46; the Bank spec's D11): its equity over everything on its sheet (Bank.leverageRatio()) and the target it holds on that measure (leverageTarget()). |
| 326 | `private List<Double> bankLeverageTarget` |  |
| 335 | `private List<Double> taxWage` |  |
| 336 | `private List<Double> taxProperty` |  |
| 337 | `private List<Double> taxSales` |  |
| 338 | `private List<Double> taxBusiness` |  |
| 339 | `private List<Double> taxIndustrial` |  |
| 340 | `private List<Double> contributions` |  |
| 341 | `private List<Double> pensionBill` |  |
| 342 | `private List<Double> healthBill` |  |
| 350 | `private List<Double> rentPrice` |  |
| 351 | `private List<Long> homes` |  |
| 352 | `private List<Double> households` |  |
| 355 | `private List<Double> students` |  |
| 356 | `private List<Double> graduates` |  |
| 357 | `private List<Double> licences` |  |
| 358 | `private List<Double> unburied` |  |
| 367 | `private List<Double> outOfWorkOnEi` |  |
| 368 | `private List<Double> outOfWorkOffEi` |  |
| 369 | `private List<Double> unhoused` |  |
| 370 | `private List<Double> orphans` |  |
| 371 | `private List<Double> evicted` |  |
| 372 | `private List<Double> eiPaid` |  |
| 373 | `private List<Double> eiPremiums` |  |
| 375 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 376 | `private List<Double> studentGrants` |  |
| 383 | `private List<Double> foodAssistance` | FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households' groceries a month, and the baskets that bought at the price charged. |
| 384 | `private List<Double> fedByAssistance` |  |
| 385 | `private List<Double> studentLoansOwed` |  |
| 387 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 397 | `private List<Double> m0` |  |
| 398 | `private List<Double> m2` |  |
| 399 | `private List<Double> advancesToTreasury` |  |
| 400 | `private List<Double> reserves` |  |
| 401 | `private List<Double> remittance` |  |
| 407 | `private List<Double> sickPastTwoMonths` |  |
| 408 | `private List<Double> diedOfIllness` |  |
| 409 | `private List<Double> sickRecovery` |  |
| 419 | `private Map<String, List<Double>> householdsByShape` |  |
| 427 | `private List<Double> deathsBabies` |  |
| 428 | `private List<Double> deathsChildren` |  |
| 429 | `private List<Double> deathsTeens` |  |
| 430 | `private List<Double> deathsAdults` |  |
| 431 | `private List<Double> deathsSeniors` |  |
| 433 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 434 | `private List<Double> deathsOrphans` |  |
| 435 | `private List<Double> deathsUnhoused` |  |
| 444 | `private List<Double> crimeRate` |  |
| 445 | `private List<Double> policeCoverage` |  |
| 446 | `private List<Double> prisoners` |  |
| 447 | `private List<Double> caughtNotHeld` |  |
| 448 | `private List<Double> stolen` |  |
| 449 | `private List<Double> deathsKilled` |  |
| 450 | `private List<Double> safetyBill` |  |
| 451 | `private Map<String, List<Double>> crimeByCause` |  |
| 460 | `private List<Long> constructionCapacity` |  |
| 461 | `private List<Double> landUse` |  |
| 480 | `private Map<String, List<Double>> sharePrice` |  |
| 481 | `private Map<String, List<Double>> shareValue` |  |
| 504 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 505 | `private Map<String, List<Double>> sectorWorkers` |  |
| 520 | `private Map<String, List<Double>> worldMoney` |  |
| 543 | `private List<PartnerTrade.Table> tradeByPartner` |  |
| 562 | `private List<Double> consumption` |  |
| 563 | `private List<Double> investment` |  |
| 564 | `private List<Double> government` |  |
| 565 | `private List<Double> netExports` |  |
| 579 | `private List<Double> fundValue` |  |
| 580 | `private List<Double> fundPutIn` |  |
| 581 | `private List<Double> fundTakenOut` |  |
| 599 | `private List<Double> expectedLevel` |  |
| 600 | `private List<Double> indexGroceries` |  |
| 601 | `private List<Double> indexRent` |  |
| 602 | `private List<Double> indexMeals` |  |
| 603 | `private List<Double> indexLuxury` |  |
| 604 | `private List<Double> indexServices` |  |
| 605 | `private List<Double> weightGroceries` |  |
| 606 | `private List<Double> weightRent` |  |
| 607 | `private List<Double> weightMeals` |  |
| 608 | `private List<Double> weightLuxury` |  |
| 609 | `private List<Double> weightServices` |  |
| 610 | `private List<Double> basketLinkedAt` |  |
| 611 | `private List<Double> shelfPrice` |  |
| 612 | `private List<Double> shelfFloor` |  |
| 613 | `private List<Double> basketsAsked` |  |
| 614 | `private List<Double> basketsHanded` |  |
| 615 | `private List<Double> hunger` |  |
| 616 | `private List<Double> hungerPricedOut` |  |
| 617 | `private List<Double> householdsAssisted` |  |
| 618 | `private List<Double> mealMargin` |  |
| 619 | `private List<Double> mealTargetMargin` |  |
| 620 | `private List<Double> luxuryMargin` |  |
| 621 | `private List<Double> luxuryTargetMargin` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 1802 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 66-88)

### money (lines 89-104)

### people (lines 105-145)

### what throttles (lines 146-162)

### prices (lines 163-173)

### the edge (lines 174-228)

### money and credit (lines 229-262)

### what the bank could carry, and how hard (lines 263-283)

### the price of money (0.7.7) (lines 284-298)

### the bank's capital (0.7.8) (lines 299-327)

### the budget (lines 328-343)

### housing (lines 344-353)

### school and care (lines 354-359)

### outside the families (lines 360-388)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 389-402)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 403-410)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 411-420)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 421-436)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 437-458)

| line | len | member | says |
|---:|---:|---|---|
| 454 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 457 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 459-462)

### the market (lines 463-486)

| line | len | member | says |
|---:|---:|---|---|
| 484 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 485 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 487-506)

### the world's moneys (WN4, 2026-10-30) (lines 507-530)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 1 | `public static String worldRateKey(String code)` | The series of a major's units to the US dollar: "fxEUR". |
| 526 | 1 | `public static String policyRateKey(String code)` | The series of a central bank's policy rate, a fraction a year (XR4): "policyUSD", for each of the eight, the dollar's too. |

### the trade by partner (WN3, 2026-10-28) (lines 531-549)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 548 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 550-566)

### the city's fund (0.7.39) (lines 567-582)

### the new price model, read (0.7.45) (lines 583-634)

| line | len | member | says |
|---:|---:|---|---|
| 624 | 1 | `public static String indexKey(int component)` | The name each component's chained level is kept under (PriceIndex.COMPONENTS order): "indexGroceries" and so on. |
| 627 | 1 | `public static String weightKey(int component)` | ...and its weight in the basket in force: "weightGroceries" and so on. |
| 629 | 1 | `private static String capitalised(String s)` |  |
| 632 | 1 | `private List<List<Double>> indexLists()` | The component lists in COMPONENTS order, for the recording and the reform. |
| 633 | 1 | `private List<List<Double>> weightLists()` |  |

### RECORDING (lines 635-911)

| line | len | member | says |
|---:|---:|---|---|
| 650 | 261 | `public void recordMonth(Game game)` | One month, read off the city itself. |

### THE FOLD (0.7.55) (lines 912-1320)

| line | len | member | says |
|---:|---:|---|---|
| 929 | 12 | `public void foldOldYears()` | Folds every calendar year whose months are all older than the newest MONTHLY_KEPT, oldest first; nothing in a history of MONTHLY_KEPT months or fewer. |
| 943 | 10 | `private void foldYear(int from, int to)` | Axis entries [from, to) - one calendar year's months - into one entry at the year's last month, every series by its rule. |
| 963 | 36 | `private static void foldSeries(String name, List<? extends Number> raw, int axis, int from, int to)` | One series' months for axis entries [from, to), folded in place by its rule (YearBook.kindOf()): FLOW added, RATE averaged, LEVEL - and a series nobody has given a rule, as the year book folds it - the last. |
| 1007 | 10 | `void appendMonth(int m, java.util.function.ToDoubleFunction<String> value)` | Harnesses only (0.7.55): one more month on every series this history has, each value asked of `value` by the series' name, then the fold recordMonth() ends with - so a history can be grown past MONTHLY_KEPT without a ... |
| 1030 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 1037 | 1 | `private static double round2(double v)` |  |
| 1038 | 1 | `private static double round4(double v)` |  |
| 1044 | 4 | `static double roundSig(double v)` | A price per founding share to SHARE_PRICE_DIGITS significant figures; 0 stays 0, and a figure that is not a number is kept as 0, as finite() keeps the rest (JSON holds no NaN). |
| 1049 | 1 | `private static double round6(double v)` | Six places, for a price in thousands that four would round to a dime - and to nothing after a reform (0.7.45: the shelf). |
| 1051 | 1 | `private static double finite(double v)` | A reading the file can hold: JSON has no NaN, so a figure not struck yet is kept as 0 (0.7.45's series). |
| 1064 | 188 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 1254 | 1 | `public List<PartnerTrade.Table> getTradeByPartner()` | The finished years of the city's trade by partner (WN3), oldest first; empty on a city with no world market or a history from before it. |
| 1257 | 1 | `public int tradeYears()` | How many finished years of trade by partner the history holds. |
| 1260 | 3 | `public void setTradeByPartner(List<PartnerTrade.Table> years)` | Sets the finished years (Game, when a year finishes): a copy of the list; the tables are immutable. |
| 1265 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 1282 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 1296 | 12 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |
| 1315 | 5 | `public HistorySave snapshot()` | A copy of this history for the writer thread (UP1, 2026-10-22): every series copied into new lists, whose entries are immutable numbers, by restoreFrom() - the one place every series is listed, so a series added later... |

### READING (lines 1321-1448)

| line | len | member | says |
|---:|---:|---|---|
| 1330 | 1 | `public int months()` | How many entries the axis has - a month each, and since 0.7.55 a year each for the oldest yearlyPoints() of a history past MONTHLY_KEPT. |
| 1333 | 1 | `public List<Integer> getMonth()` | The axis: each entry's month, and a folded year's last month (0.7.55). |
| 1336 | 1 | `public int yearlyPoints()` | How many of the axis's entries, from the first, are folded years (0.7.55); 0 in a history of MONTHLY_KEPT months or fewer. |
| 1339 | 3 | `public int monthsIn(int i)` | The months axis entry i holds: 1 for a month, and a folded year's months (0.7.55). |
| 1344 | 3 | `public int firstMonthOf(int i)` | The first month axis entry i holds: its own month for a month, a folded year's first (0.7.55). |
| 1349 | 5 | `public int monthsLived()` | How many months the city has lived: every entry's months together (0.7.55) - months() until a year folds. |
| 1363 | 9 | `public int back(int i, int months)` | The entry `months` months before entry i (0.7.55): the month that far back, or the folded year that holds it - so "a year before" a folded year is the year before it, and before a month in the first year after the fol... |
| 1392 | 8 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 1411 | 21 | `public double[] alignedTail(String name, int n)` | The last {@code n} entries of aligned(name), and no more of it (UP2): the same figures as aligned(name)'s tail, value for value - the front padding as NaN, a folded year's flow as its monthly average where the tail re... |
| 1434 | 14 | `public double[] raw(String name)` | A series as it is stored, padded at the front as aligned() is: a folded year's flow is the year's sum (0.7.55). |

### a series' record, counted (0.7.9) (lines 1449-1865)

| line | len | member | says |
|---:|---:|---|---|
| 1462 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 1472 | 6 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 1480 | 6 | `public int monthsRecorded(String series)` | Months a series was recorded in - a folded year's months each (0.7.55). |
| 1488 | 5 | `public double total(String series)` | A flow added up over the months it was recorded: the stored figures, so a folded year adds its sum (0.7.55). |
| 1501 | 7 | `public double changeOver(String series, int months)` | How far a series has moved over the last `months` months, as a share of where it stood then: the last recorded value over the one `months` entries before it, less one (0.7.35: the Trade tab's rate this month and over ... |
| 1514 | 12 | `public double recentTotal(String series, int months)` | A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports). |
| 1532 | 16 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 1550 | 13 | `private static double worstYear(double[] a)` | worstYear()'s twelve months in a row, over a run of months. |
| 1570 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 1591 | 3 | `public double[] runningTotal(String series)` | ...and a series of this history summed from its first recorded entry (0.7.55): off the stored figures, so a folded year adds its sum - what City History's running totals read. |
| 1596 | 183 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 1781 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 1782 | 1 | `public List<Double> getGdp()` |  |
| 1783 | 1 | `public List<Double> getDebt()` |  |
| 1784 | 1 | `public List<Double> getInterestRate()` |  |
| 1785 | 1 | `public List<Long> getJobs()` |  |
| 1786 | 1 | `public List<Long> getWorkforce()` |  |
| 1787 | 1 | `public List<Long> getOutOfWork()` |  |
| 1788 | 1 | `public List<Long> getPopulation()` |  |
| 1801 | 52 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 1855 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |

