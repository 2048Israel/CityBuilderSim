# HistorySave.java - 1,693 lines · 56 methods · 3 constants · model

`ham/citybuildersim/HistorySave.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (7), [PriceIndex](PriceIndex.md) (6), [YearBook](YearBook.md) (6), [Crime](Crime.md) (4), [FamilyStructure](FamilyStructure.md) (3), [Equity](Equity.md) (3), [CityCalendar](CityCalendar.md) (2), [GameFiles](GameFiles.md) (2), [Game](Game.md) (1), [EconomyManager](EconomyManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [PopulationManager](PopulationManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Migration](Migration.md) (1), [LabourMarket](LabourMarket.md) (1), [JobType](JobType.md) (1), [WageBand](WageBand.md) (1), [Education](Education.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Bank](Bank.md) (1), [CentralBank](CentralBank.md) (1), [Unemployment](Unemployment.md) (1), [FamilyModel](FamilyModel.md) (1), [Sickness](Sickness.md) (1), [Exchange](Exchange.md) (1), [Sector](Sector.md) (1), [Retail](Retail.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

**Used by (26):** [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 67 | · THE AXIS |
| 90 | · money |
| 100 | · people |
| 141 | · what throttles |
| 158 | · prices |
| 169 | · the edge |
| 210 | · money and credit |
| 237 | · what the bank could carry, and how hard |
| 258 | · the price of money (0.7.7) |
| 273 | · the bank's capital (0.7.8) |
| 302 | · the budget |
| 318 | · housing |
| 328 | · school and care |
| 334 | · outside the families |
| 363 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 377 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 385 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 395 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 411 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 433 | · what runs out |
| 437 | · the market |
| 461 | · the sectors (0.7.4) |
| 486 | · GDP in layers (0.7.6) |
| 503 | · the city's fund (0.7.39) |
| 519 | · the new price model, read (0.7.45) |
| 571 | RECORDING |
| 644 | · · the edge |
| 656 | · · money and credit |
| 701 | · · the budget |
| 711 | · · housing |
| 716 | · · school and care |
| 723 | · · outside the families |
| 770 | · · what runs out |
| 774 | · · the market |
| 787 | · · the sectors |
| 795 | · · the new price model, read (0.7.45) |
| 817 | · · the city's fund (0.7.39) |
| 829 | THE FOLD (0.7.55) |
| 1189 | READING |
| 1285 | · a series' record, counted (0.7.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 936 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "d...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 958 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 74 | `private List<Integer> month` |  |
| 85 | `private List<Integer> foldedMonths` | How many months each folded year holds, oldest first (0.7.55): the first foldedMonths.size() entries of the axis, and of every series lined up with it, are years - see PAST FIVE HUNDRED YEARS above. |
| 91 | `private List<Double> cash` |  |
| 92 | `private List<Double> gdp` |  |
| 93 | `private List<Double> debt` |  |
| 94 | `private List<Double> interestRate` |  |
| 97 | `private List<Double> revenue` | Government revenue and what it kept, both monthly. |
| 98 | `private List<Double> surplus` |  |
| 101 | `private List<Long> jobs` |  |
| 102 | `private List<Long> workforce` |  |
| 111 | `private List<Long> outOfWork` | The people out of work - the labour force less the posts FILLED, the People screen's figure (PopulationManager.getUnemployed()). |
| 112 | `private List<Long> population` |  |
| 115 | `private List<Long> births` | The four flows that move the population, and only these four move it. |
| 116 | `private List<Long> deaths` |  |
| 117 | `private List<Long> arrivals` |  |
| 118 | `private List<Long> departures` |  |
| 121 | `private List<Double> totalWage` | The wage BILL, not the average - the average needs the workforce too. |
| 124 | `private List<Double> minimumWage` | The dial the player sets, and what the labour market did with it. |
| 134 | `private List<Double> unskilledPremium` | What an unskilled hour costs relative to its base, and how much of the workforce has any training at all. |
| 135 | `private List<Double> skilledShare` |  |
| 138 | `private List<Double> schoolCoverage` | The schools: how much of the basic ladder is covered, and what it costs. |
| 139 | `private List<Double> schoolBill` |  |
| 142 | `private List<Double> energyRatio` |  |
| 143 | `private List<Double> waterRatio` |  |
| 144 | `private List<Double> roadRatio` |  |
| 145 | `private List<Double> sickRate` |  |
| 146 | `private List<Double> careCoverage` |  |
| 156 | `private List<Double> outbreak` | The outbreak running, as extra absence on top of what the city's care explains (Health.getOutbreakSeverity()); 0 between outbreaks. |
| 164 | `private List<Double> landPrice` | What the world asks the city for a square foot of ground, in thousands of US DOLLARS since 0.7.6 (LandManager.getGroundUsdPerSqFt()); the months an older save recorded are local money, and stay as recorded. |
| 165 | `private List<Double> foodPrice` |  |
| 166 | `private List<Double> materialsPrice` |  |
| 167 | `private List<Double> orePrice` |  |
| 182 | `private List<Double> fxRate` |  |
| 189 | `private List<Double> fxParity` | Parity beside the rate (0.7.35): where a basket costs the same here and abroad, in the rate's own units (ForeignAccounts.getParity()), so the Trade tab can draw the two on one chart. |
| 197 | `private List<Double> fxFair` | The fundamentals' level beside the rate (XR1, 2026-10-21): where parity's trend and the real gap, the balance and the cover put the D$ before the market and the interventions (ForeignAccounts.getFairLevel()), in the r... |
| 203 | `private List<Double> fxMarket` | ...and how far the rate sat from that level, a fraction, positive weaker (ForeignAccounts.deviationFromFair()): the market's swing and the interventions, a ratio and not money. |
| 204 | `private List<Double> reservesUsd` |  |
| 205 | `private List<Double> foreignDebtUsd` |  |
| 206 | `private List<Double> currentAccount` |  |
| 207 | `private List<Double> exportsAbroad` |  |
| 208 | `private List<Double> importsAbroad` |  |
| 216 | `private List<Double> priceIndex` |  |
| 223 | `private List<Double> expectedInflation` | THE ANCHOR (0.7.42): what the city expected inflation to be, a fraction a year, and how far it believed the bank (Expectations) - both ratios, so a reform leaves them alone. |
| 224 | `private List<Double> credibility` |  |
| 225 | `private List<Double> businessDebt` |  |
| 232 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 233 | `private List<Double> bankLent` |  |
| 234 | `private List<Double> bankEquity` |  |
| 235 | `private List<Double> bankWriteOffs` |  |
| 252 | `private List<Double> bankCapacity` |  |
| 253 | `private List<Double> bankStrain` |  |
| 254 | `private List<Double> bankProfit` |  |
| 255 | `private List<Double> bankBranches` |  |
| 256 | `private List<Double> householdSavings` |  |
| 268 | `private List<Double> policyRate` |  |
| 269 | `private List<Double> bankPrime` |  |
| 270 | `private List<Double> bankDepositRate` |  |
| 271 | `private List<Double> bankFees` |  |
| 283 | `private List<Double> bankCapitalRatio` |  |
| 284 | `private List<Double> bankCapitalTarget` |  |
| 285 | `private List<Double> bankAllowance` |  |
| 286 | `private List<Double> bankProvisions` |  |
| 287 | `private List<Double> bankDividends` |  |
| 288 | `private List<Double> bankReturnOnEquity` |  |
| 299 | `private List<Double> bankLeverageRatio` | ...AND THE OTHER MEASURE (A7, 0.7.46; the Bank spec's D11): its equity over everything on its sheet (Bank.leverageRatio()) and the target it holds on that measure (leverageTarget()). |
| 300 | `private List<Double> bankLeverageTarget` |  |
| 309 | `private List<Double> taxWage` |  |
| 310 | `private List<Double> taxProperty` |  |
| 311 | `private List<Double> taxSales` |  |
| 312 | `private List<Double> taxBusiness` |  |
| 313 | `private List<Double> taxIndustrial` |  |
| 314 | `private List<Double> contributions` |  |
| 315 | `private List<Double> pensionBill` |  |
| 316 | `private List<Double> healthBill` |  |
| 324 | `private List<Double> rentPrice` |  |
| 325 | `private List<Long> homes` |  |
| 326 | `private List<Double> households` |  |
| 329 | `private List<Double> students` |  |
| 330 | `private List<Double> graduates` |  |
| 331 | `private List<Double> licences` |  |
| 332 | `private List<Double> unburied` |  |
| 341 | `private List<Double> outOfWorkOnEi` |  |
| 342 | `private List<Double> outOfWorkOffEi` |  |
| 343 | `private List<Double> unhoused` |  |
| 344 | `private List<Double> orphans` |  |
| 345 | `private List<Double> evicted` |  |
| 346 | `private List<Double> eiPaid` |  |
| 347 | `private List<Double> eiPremiums` |  |
| 349 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 350 | `private List<Double> studentGrants` |  |
| 357 | `private List<Double> foodAssistance` | FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households' groceries a month, and the baskets that bought at the price charged. |
| 358 | `private List<Double> fedByAssistance` |  |
| 359 | `private List<Double> studentLoansOwed` |  |
| 361 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 371 | `private List<Double> m0` |  |
| 372 | `private List<Double> m2` |  |
| 373 | `private List<Double> advancesToTreasury` |  |
| 374 | `private List<Double> reserves` |  |
| 375 | `private List<Double> remittance` |  |
| 381 | `private List<Double> sickPastTwoMonths` |  |
| 382 | `private List<Double> diedOfIllness` |  |
| 383 | `private List<Double> sickRecovery` |  |
| 393 | `private Map<String, List<Double>> householdsByShape` |  |
| 401 | `private List<Double> deathsBabies` |  |
| 402 | `private List<Double> deathsChildren` |  |
| 403 | `private List<Double> deathsTeens` |  |
| 404 | `private List<Double> deathsAdults` |  |
| 405 | `private List<Double> deathsSeniors` |  |
| 407 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 408 | `private List<Double> deathsOrphans` |  |
| 409 | `private List<Double> deathsUnhoused` |  |
| 418 | `private List<Double> crimeRate` |  |
| 419 | `private List<Double> policeCoverage` |  |
| 420 | `private List<Double> prisoners` |  |
| 421 | `private List<Double> caughtNotHeld` |  |
| 422 | `private List<Double> stolen` |  |
| 423 | `private List<Double> deathsKilled` |  |
| 424 | `private List<Double> safetyBill` |  |
| 425 | `private Map<String, List<Double>> crimeByCause` |  |
| 434 | `private List<Long> constructionCapacity` |  |
| 435 | `private List<Double> landUse` |  |
| 454 | `private Map<String, List<Double>> sharePrice` |  |
| 455 | `private Map<String, List<Double>> shareValue` |  |
| 478 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 479 | `private Map<String, List<Double>> sectorWorkers` |  |
| 498 | `private List<Double> consumption` |  |
| 499 | `private List<Double> investment` |  |
| 500 | `private List<Double> government` |  |
| 501 | `private List<Double> netExports` |  |
| 515 | `private List<Double> fundValue` |  |
| 516 | `private List<Double> fundPutIn` |  |
| 517 | `private List<Double> fundTakenOut` |  |
| 535 | `private List<Double> expectedLevel` |  |
| 536 | `private List<Double> indexGroceries` |  |
| 537 | `private List<Double> indexRent` |  |
| 538 | `private List<Double> indexMeals` |  |
| 539 | `private List<Double> indexLuxury` |  |
| 540 | `private List<Double> indexServices` |  |
| 541 | `private List<Double> weightGroceries` |  |
| 542 | `private List<Double> weightRent` |  |
| 543 | `private List<Double> weightMeals` |  |
| 544 | `private List<Double> weightLuxury` |  |
| 545 | `private List<Double> weightServices` |  |
| 546 | `private List<Double> basketLinkedAt` |  |
| 547 | `private List<Double> shelfPrice` |  |
| 548 | `private List<Double> shelfFloor` |  |
| 549 | `private List<Double> basketsAsked` |  |
| 550 | `private List<Double> basketsHanded` |  |
| 551 | `private List<Double> hunger` |  |
| 552 | `private List<Double> hungerPricedOut` |  |
| 553 | `private List<Double> householdsAssisted` |  |
| 554 | `private List<Double> mealMargin` |  |
| 555 | `private List<Double> mealTargetMargin` |  |
| 556 | `private List<Double> luxuryMargin` |  |
| 557 | `private List<Double> luxuryTargetMargin` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 1629 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 67-89)

### money (lines 90-99)

### people (lines 100-140)

### what throttles (lines 141-157)

### prices (lines 158-168)

### the edge (lines 169-209)

### money and credit (lines 210-236)

### what the bank could carry, and how hard (lines 237-257)

### the price of money (0.7.7) (lines 258-272)

### the bank's capital (0.7.8) (lines 273-301)

### the budget (lines 302-317)

### housing (lines 318-327)

### school and care (lines 328-333)

### outside the families (lines 334-362)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 363-376)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 377-384)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 385-394)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 395-410)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 411-432)

| line | len | member | says |
|---:|---:|---|---|
| 428 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 431 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 433-436)

### the market (lines 437-460)

| line | len | member | says |
|---:|---:|---|---|
| 458 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 459 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 461-485)

| line | len | member | says |
|---:|---:|---|---|
| 482 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 484 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 486-502)

### the city's fund (0.7.39) (lines 503-518)

### the new price model, read (0.7.45) (lines 519-570)

| line | len | member | says |
|---:|---:|---|---|
| 560 | 1 | `public static String indexKey(int component)` | The name each component's chained level is kept under (PriceIndex.COMPONENTS order): "indexGroceries" and so on. |
| 563 | 1 | `public static String weightKey(int component)` | ...and its weight in the basket in force: "weightGroceries" and so on. |
| 565 | 1 | `private static String capitalised(String s)` |  |
| 568 | 1 | `private List<List<Double>> indexLists()` | The component lists in COMPONENTS order, for the recording and the reform. |
| 569 | 1 | `private List<List<Double>> weightLists()` |  |

### RECORDING (lines 571-828)

| line | len | member | says |
|---:|---:|---|---|
| 586 | 242 | `public void recordMonth(Game game)` | One month, read off the city itself. |

### THE FOLD (0.7.55) (lines 829-1188)

| line | len | member | says |
|---:|---:|---|---|
| 846 | 12 | `public void foldOldYears()` | Folds every calendar year whose months are all older than the newest MONTHLY_KEPT, oldest first; nothing in a history of MONTHLY_KEPT months or fewer. |
| 860 | 10 | `private void foldYear(int from, int to)` | Axis entries [from, to) - one calendar year's months - into one entry at the year's last month, every series by its rule. |
| 880 | 36 | `private static void foldSeries(String name, List<? extends Number> raw, int axis, int from, int to)` | One series' months for axis entries [from, to), folded in place by its rule (YearBook.kindOf()): FLOW added, RATE averaged, LEVEL - and a series nobody has given a rule, as the year book folds it - the last. |
| 924 | 10 | `void appendMonth(int m, java.util.function.ToDoubleFunction<String> value)` | Harnesses only (0.7.55): one more month on every series this history has, each value asked of `value` by the series' name, then the fold recordMonth() ends with - so a history can be grown past MONTHLY_KEPT without a ... |
| 947 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 954 | 1 | `private static double round2(double v)` |  |
| 955 | 1 | `private static double round4(double v)` |  |
| 961 | 4 | `static double roundSig(double v)` | A price per founding share to SHARE_PRICE_DIGITS significant figures; 0 stays 0, and a figure that is not a number is kept as 0, as finite() keeps the rest (JSON holds no NaN). |
| 966 | 1 | `private static double round6(double v)` | Six places, for a price in thousands that four would round to a dime - and to nothing after a reform (0.7.45: the shelf). |
| 968 | 1 | `private static double finite(double v)` | A reading the file can hold: JSON has no NaN, so a figure not struck yet is kept as 0 (0.7.45's series). |
| 981 | 175 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 1158 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 1175 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 1184 | 4 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |

### READING (lines 1189-1284)

| line | len | member | says |
|---:|---:|---|---|
| 1198 | 1 | `public int months()` | How many entries the axis has - a month each, and since 0.7.55 a year each for the oldest yearlyPoints() of a history past MONTHLY_KEPT. |
| 1201 | 1 | `public List<Integer> getMonth()` | The axis: each entry's month, and a folded year's last month (0.7.55). |
| 1204 | 1 | `public int yearlyPoints()` | How many of the axis's entries, from the first, are folded years (0.7.55); 0 in a history of MONTHLY_KEPT months or fewer. |
| 1207 | 3 | `public int monthsIn(int i)` | The months axis entry i holds: 1 for a month, and a folded year's months (0.7.55). |
| 1212 | 3 | `public int firstMonthOf(int i)` | The first month axis entry i holds: its own month for a month, a folded year's first (0.7.55). |
| 1217 | 5 | `public int monthsLived()` | How many months the city has lived: every entry's months together (0.7.55) - months() until a year folds. |
| 1231 | 9 | `public int back(int i, int months)` | The entry `months` months before entry i (0.7.55): the month that far back, or the folded year that holds it - so "a year before" a folded year is the year before it, and before a month in the first year after the fol... |
| 1260 | 8 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 1270 | 14 | `public double[] raw(String name)` | A series as it is stored, padded at the front as aligned() is: a folded year's flow is the year's sum (0.7.55). |

### a series' record, counted (0.7.9) (lines 1285-1693)

| line | len | member | says |
|---:|---:|---|---|
| 1298 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 1308 | 6 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 1316 | 6 | `public int monthsRecorded(String series)` | Months a series was recorded in - a folded year's months each (0.7.55). |
| 1324 | 5 | `public double total(String series)` | A flow added up over the months it was recorded: the stored figures, so a folded year adds its sum (0.7.55). |
| 1337 | 7 | `public double changeOver(String series, int months)` | How far a series has moved over the last `months` months, as a share of where it stood then: the last recorded value over the one `months` entries before it, less one (0.7.35: the Trade tab's rate this month and over ... |
| 1350 | 12 | `public double recentTotal(String series, int months)` | A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports). |
| 1368 | 16 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 1386 | 13 | `private static double worstYear(double[] a)` | worstYear()'s twelve months in a row, over a run of months. |
| 1406 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 1427 | 3 | `public double[] runningTotal(String series)` | ...and a series of this history summed from its first recorded entry (0.7.55): off the stored figures, so a folded year adds its sum - what City History's running totals read. |
| 1432 | 176 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 1610 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 1611 | 1 | `public List<Double> getGdp()` |  |
| 1612 | 1 | `public List<Double> getDebt()` |  |
| 1613 | 1 | `public List<Double> getInterestRate()` |  |
| 1614 | 1 | `public List<Long> getJobs()` |  |
| 1615 | 1 | `public List<Long> getWorkforce()` |  |
| 1616 | 1 | `public List<Long> getOutOfWork()` |  |
| 1617 | 1 | `public List<Long> getPopulation()` |  |
| 1630 | 51 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 1683 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |

