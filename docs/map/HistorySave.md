# HistorySave.java - 1,714 lines · 56 methods · 3 constants · model

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
| 224 | · money and credit |
| 251 | · what the bank could carry, and how hard |
| 272 | · the price of money (0.7.7) |
| 287 | · the bank's capital (0.7.8) |
| 316 | · the budget |
| 332 | · housing |
| 342 | · school and care |
| 348 | · outside the families |
| 377 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 391 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 399 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 409 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 425 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 447 | · what runs out |
| 451 | · the market |
| 475 | · the sectors (0.7.4) |
| 500 | · GDP in layers (0.7.6) |
| 517 | · the city's fund (0.7.39) |
| 533 | · the new price model, read (0.7.45) |
| 585 | RECORDING |
| 658 | · · the edge |
| 672 | · · money and credit |
| 717 | · · the budget |
| 727 | · · housing |
| 732 | · · school and care |
| 739 | · · outside the families |
| 786 | · · what runs out |
| 790 | · · the market |
| 803 | · · the sectors |
| 811 | · · the new price model, read (0.7.45) |
| 833 | · · the city's fund (0.7.39) |
| 845 | THE FOLD (0.7.55) |
| 1207 | READING |
| 1303 | · a series' record, counted (0.7.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 952 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "d...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 974 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5). |

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
| 211 | `private List<Double> fxShadow` | The shadow rate (XR2, 2026-10-22): where the float's rule put the D$ at the month's reprice, in the rate's own units (ForeignAccounts .getShadowRate()) - the rate itself under Lean and Float, the rate the hold is set ... |
| 217 | `private List<Double> fxCover` | ...and the vault's cover, in months of imports (ForeignAccounts .importCover(), at most 99): the hold's reserve, beside the shadow. |
| 218 | `private List<Double> reservesUsd` |  |
| 219 | `private List<Double> foreignDebtUsd` |  |
| 220 | `private List<Double> currentAccount` |  |
| 221 | `private List<Double> exportsAbroad` |  |
| 222 | `private List<Double> importsAbroad` |  |
| 230 | `private List<Double> priceIndex` |  |
| 237 | `private List<Double> expectedInflation` | THE ANCHOR (0.7.42): what the city expected inflation to be, a fraction a year, and how far it believed the bank (Expectations) - both ratios, so a reform leaves them alone. |
| 238 | `private List<Double> credibility` |  |
| 239 | `private List<Double> businessDebt` |  |
| 246 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 247 | `private List<Double> bankLent` |  |
| 248 | `private List<Double> bankEquity` |  |
| 249 | `private List<Double> bankWriteOffs` |  |
| 266 | `private List<Double> bankCapacity` |  |
| 267 | `private List<Double> bankStrain` |  |
| 268 | `private List<Double> bankProfit` |  |
| 269 | `private List<Double> bankBranches` |  |
| 270 | `private List<Double> householdSavings` |  |
| 282 | `private List<Double> policyRate` |  |
| 283 | `private List<Double> bankPrime` |  |
| 284 | `private List<Double> bankDepositRate` |  |
| 285 | `private List<Double> bankFees` |  |
| 297 | `private List<Double> bankCapitalRatio` |  |
| 298 | `private List<Double> bankCapitalTarget` |  |
| 299 | `private List<Double> bankAllowance` |  |
| 300 | `private List<Double> bankProvisions` |  |
| 301 | `private List<Double> bankDividends` |  |
| 302 | `private List<Double> bankReturnOnEquity` |  |
| 313 | `private List<Double> bankLeverageRatio` | ...AND THE OTHER MEASURE (A7, 0.7.46; the Bank spec's D11): its equity over everything on its sheet (Bank.leverageRatio()) and the target it holds on that measure (leverageTarget()). |
| 314 | `private List<Double> bankLeverageTarget` |  |
| 323 | `private List<Double> taxWage` |  |
| 324 | `private List<Double> taxProperty` |  |
| 325 | `private List<Double> taxSales` |  |
| 326 | `private List<Double> taxBusiness` |  |
| 327 | `private List<Double> taxIndustrial` |  |
| 328 | `private List<Double> contributions` |  |
| 329 | `private List<Double> pensionBill` |  |
| 330 | `private List<Double> healthBill` |  |
| 338 | `private List<Double> rentPrice` |  |
| 339 | `private List<Long> homes` |  |
| 340 | `private List<Double> households` |  |
| 343 | `private List<Double> students` |  |
| 344 | `private List<Double> graduates` |  |
| 345 | `private List<Double> licences` |  |
| 346 | `private List<Double> unburied` |  |
| 355 | `private List<Double> outOfWorkOnEi` |  |
| 356 | `private List<Double> outOfWorkOffEi` |  |
| 357 | `private List<Double> unhoused` |  |
| 358 | `private List<Double> orphans` |  |
| 359 | `private List<Double> evicted` |  |
| 360 | `private List<Double> eiPaid` |  |
| 361 | `private List<Double> eiPremiums` |  |
| 363 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 364 | `private List<Double> studentGrants` |  |
| 371 | `private List<Double> foodAssistance` | FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households' groceries a month, and the baskets that bought at the price charged. |
| 372 | `private List<Double> fedByAssistance` |  |
| 373 | `private List<Double> studentLoansOwed` |  |
| 375 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 385 | `private List<Double> m0` |  |
| 386 | `private List<Double> m2` |  |
| 387 | `private List<Double> advancesToTreasury` |  |
| 388 | `private List<Double> reserves` |  |
| 389 | `private List<Double> remittance` |  |
| 395 | `private List<Double> sickPastTwoMonths` |  |
| 396 | `private List<Double> diedOfIllness` |  |
| 397 | `private List<Double> sickRecovery` |  |
| 407 | `private Map<String, List<Double>> householdsByShape` |  |
| 415 | `private List<Double> deathsBabies` |  |
| 416 | `private List<Double> deathsChildren` |  |
| 417 | `private List<Double> deathsTeens` |  |
| 418 | `private List<Double> deathsAdults` |  |
| 419 | `private List<Double> deathsSeniors` |  |
| 421 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 422 | `private List<Double> deathsOrphans` |  |
| 423 | `private List<Double> deathsUnhoused` |  |
| 432 | `private List<Double> crimeRate` |  |
| 433 | `private List<Double> policeCoverage` |  |
| 434 | `private List<Double> prisoners` |  |
| 435 | `private List<Double> caughtNotHeld` |  |
| 436 | `private List<Double> stolen` |  |
| 437 | `private List<Double> deathsKilled` |  |
| 438 | `private List<Double> safetyBill` |  |
| 439 | `private Map<String, List<Double>> crimeByCause` |  |
| 448 | `private List<Long> constructionCapacity` |  |
| 449 | `private List<Double> landUse` |  |
| 468 | `private Map<String, List<Double>> sharePrice` |  |
| 469 | `private Map<String, List<Double>> shareValue` |  |
| 492 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 493 | `private Map<String, List<Double>> sectorWorkers` |  |
| 512 | `private List<Double> consumption` |  |
| 513 | `private List<Double> investment` |  |
| 514 | `private List<Double> government` |  |
| 515 | `private List<Double> netExports` |  |
| 529 | `private List<Double> fundValue` |  |
| 530 | `private List<Double> fundPutIn` |  |
| 531 | `private List<Double> fundTakenOut` |  |
| 549 | `private List<Double> expectedLevel` |  |
| 550 | `private List<Double> indexGroceries` |  |
| 551 | `private List<Double> indexRent` |  |
| 552 | `private List<Double> indexMeals` |  |
| 553 | `private List<Double> indexLuxury` |  |
| 554 | `private List<Double> indexServices` |  |
| 555 | `private List<Double> weightGroceries` |  |
| 556 | `private List<Double> weightRent` |  |
| 557 | `private List<Double> weightMeals` |  |
| 558 | `private List<Double> weightLuxury` |  |
| 559 | `private List<Double> weightServices` |  |
| 560 | `private List<Double> basketLinkedAt` |  |
| 561 | `private List<Double> shelfPrice` |  |
| 562 | `private List<Double> shelfFloor` |  |
| 563 | `private List<Double> basketsAsked` |  |
| 564 | `private List<Double> basketsHanded` |  |
| 565 | `private List<Double> hunger` |  |
| 566 | `private List<Double> hungerPricedOut` |  |
| 567 | `private List<Double> householdsAssisted` |  |
| 568 | `private List<Double> mealMargin` |  |
| 569 | `private List<Double> mealTargetMargin` |  |
| 570 | `private List<Double> luxuryMargin` |  |
| 571 | `private List<Double> luxuryTargetMargin` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 1650 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 67-89)

### money (lines 90-99)

### people (lines 100-140)

### what throttles (lines 141-157)

### prices (lines 158-168)

### the edge (lines 169-223)

### money and credit (lines 224-250)

### what the bank could carry, and how hard (lines 251-271)

### the price of money (0.7.7) (lines 272-286)

### the bank's capital (0.7.8) (lines 287-315)

### the budget (lines 316-331)

### housing (lines 332-341)

### school and care (lines 342-347)

### outside the families (lines 348-376)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 377-390)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 391-398)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 399-408)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 409-424)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 425-446)

| line | len | member | says |
|---:|---:|---|---|
| 442 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 445 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 447-450)

### the market (lines 451-474)

| line | len | member | says |
|---:|---:|---|---|
| 472 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 473 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 475-499)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 498 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 500-516)

### the city's fund (0.7.39) (lines 517-532)

### the new price model, read (0.7.45) (lines 533-584)

| line | len | member | says |
|---:|---:|---|---|
| 574 | 1 | `public static String indexKey(int component)` | The name each component's chained level is kept under (PriceIndex.COMPONENTS order): "indexGroceries" and so on. |
| 577 | 1 | `public static String weightKey(int component)` | ...and its weight in the basket in force: "weightGroceries" and so on. |
| 579 | 1 | `private static String capitalised(String s)` |  |
| 582 | 1 | `private List<List<Double>> indexLists()` | The component lists in COMPONENTS order, for the recording and the reform. |
| 583 | 1 | `private List<List<Double>> weightLists()` |  |

### RECORDING (lines 585-844)

| line | len | member | says |
|---:|---:|---|---|
| 600 | 244 | `public void recordMonth(Game game)` | One month, read off the city itself. |

### THE FOLD (0.7.55) (lines 845-1206)

| line | len | member | says |
|---:|---:|---|---|
| 862 | 12 | `public void foldOldYears()` | Folds every calendar year whose months are all older than the newest MONTHLY_KEPT, oldest first; nothing in a history of MONTHLY_KEPT months or fewer. |
| 876 | 10 | `private void foldYear(int from, int to)` | Axis entries [from, to) - one calendar year's months - into one entry at the year's last month, every series by its rule. |
| 896 | 36 | `private static void foldSeries(String name, List<? extends Number> raw, int axis, int from, int to)` | One series' months for axis entries [from, to), folded in place by its rule (YearBook.kindOf()): FLOW added, RATE averaged, LEVEL - and a series nobody has given a rule, as the year book folds it - the last. |
| 940 | 10 | `void appendMonth(int m, java.util.function.ToDoubleFunction<String> value)` | Harnesses only (0.7.55): one more month on every series this history has, each value asked of `value` by the series' name, then the fold recordMonth() ends with - so a history can be grown past MONTHLY_KEPT without a ... |
| 963 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 970 | 1 | `private static double round2(double v)` |  |
| 971 | 1 | `private static double round4(double v)` |  |
| 977 | 4 | `static double roundSig(double v)` | A price per founding share to SHARE_PRICE_DIGITS significant figures; 0 stays 0, and a figure that is not a number is kept as 0, as finite() keeps the rest (JSON holds no NaN). |
| 982 | 1 | `private static double round6(double v)` | Six places, for a price in thousands that four would round to a dime - and to nothing after a reform (0.7.45: the shelf). |
| 984 | 1 | `private static double finite(double v)` | A reading the file can hold: JSON has no NaN, so a figure not struck yet is kept as 0 (0.7.45's series). |
| 997 | 177 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 1176 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 1193 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 1202 | 4 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |

### READING (lines 1207-1302)

| line | len | member | says |
|---:|---:|---|---|
| 1216 | 1 | `public int months()` | How many entries the axis has - a month each, and since 0.7.55 a year each for the oldest yearlyPoints() of a history past MONTHLY_KEPT. |
| 1219 | 1 | `public List<Integer> getMonth()` | The axis: each entry's month, and a folded year's last month (0.7.55). |
| 1222 | 1 | `public int yearlyPoints()` | How many of the axis's entries, from the first, are folded years (0.7.55); 0 in a history of MONTHLY_KEPT months or fewer. |
| 1225 | 3 | `public int monthsIn(int i)` | The months axis entry i holds: 1 for a month, and a folded year's months (0.7.55). |
| 1230 | 3 | `public int firstMonthOf(int i)` | The first month axis entry i holds: its own month for a month, a folded year's first (0.7.55). |
| 1235 | 5 | `public int monthsLived()` | How many months the city has lived: every entry's months together (0.7.55) - months() until a year folds. |
| 1249 | 9 | `public int back(int i, int months)` | The entry `months` months before entry i (0.7.55): the month that far back, or the folded year that holds it - so "a year before" a folded year is the year before it, and before a month in the first year after the fol... |
| 1278 | 8 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 1288 | 14 | `public double[] raw(String name)` | A series as it is stored, padded at the front as aligned() is: a folded year's flow is the year's sum (0.7.55). |

### a series' record, counted (0.7.9) (lines 1303-1714)

| line | len | member | says |
|---:|---:|---|---|
| 1316 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 1326 | 6 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 1334 | 6 | `public int monthsRecorded(String series)` | Months a series was recorded in - a folded year's months each (0.7.55). |
| 1342 | 5 | `public double total(String series)` | A flow added up over the months it was recorded: the stored figures, so a folded year adds its sum (0.7.55). |
| 1355 | 7 | `public double changeOver(String series, int months)` | How far a series has moved over the last `months` months, as a share of where it stood then: the last recorded value over the one `months` entries before it, less one (0.7.35: the Trade tab's rate this month and over ... |
| 1368 | 12 | `public double recentTotal(String series, int months)` | A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports). |
| 1386 | 16 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 1404 | 13 | `private static double worstYear(double[] a)` | worstYear()'s twelve months in a row, over a run of months. |
| 1424 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 1445 | 3 | `public double[] runningTotal(String series)` | ...and a series of this history summed from its first recorded entry (0.7.55): off the stored figures, so a folded year adds its sum - what City History's running totals read. |
| 1450 | 178 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 1630 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 1631 | 1 | `public List<Double> getGdp()` |  |
| 1632 | 1 | `public List<Double> getDebt()` |  |
| 1633 | 1 | `public List<Double> getInterestRate()` |  |
| 1634 | 1 | `public List<Long> getJobs()` |  |
| 1635 | 1 | `public List<Long> getWorkforce()` |  |
| 1636 | 1 | `public List<Long> getOutOfWork()` |  |
| 1637 | 1 | `public List<Long> getPopulation()` |  |
| 1650 | 52 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 1704 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |

