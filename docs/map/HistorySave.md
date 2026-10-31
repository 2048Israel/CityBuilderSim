# HistorySave.java - 1,883 lines · 63 methods · 4 constants · model

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

**Uses:** [YearBook](YearBook.md) (8), [AgeBand](AgeBand.md) (7), [PriceIndex](PriceIndex.md) (6), [PartnerTrade](PartnerTrade.md) (5), [Crime](Crime.md) (4), [FamilyStructure](FamilyStructure.md) (3), [Equity](Equity.md) (3), [WorldMoneys](WorldMoneys.md) (2), [CityCalendar](CityCalendar.md) (2), [GameFiles](GameFiles.md) (2), [Game](Game.md) (1), [EconomyManager](EconomyManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [PopulationManager](PopulationManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Migration](Migration.md) (1), [LabourMarket](LabourMarket.md) (1), [JobType](JobType.md) (1), [WageBand](WageBand.md) (1), [Education](Education.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Nations](Nations.md) (1), [Bank](Bank.md) (1), [CentralBank](CentralBank.md) (1), [Unemployment](Unemployment.md) (1), [FamilyModel](FamilyModel.md) (1), [Sickness](Sickness.md) (1), [Exchange](Exchange.md) (1), [Sector](Sector.md) (1)... and 2 more

**Used by (35):** [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuybackCheck](BuybackCheck.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [CityMeasures](CityMeasures.md), [ClockCheck](ClockCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md), [SavedCities](SavedCities.md), [ScaleCheck](ScaleCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 66 | · THE AXIS |
| 89 | · money |
| 102 | · people |
| 143 | · what throttles |
| 160 | · prices |
| 171 | · the edge |
| 226 | · money and credit |
| 271 | · what the bank could carry, and how hard |
| 292 | · the price of money (0.7.7) |
| 307 | · the bank's capital (0.7.8) |
| 336 | · the budget |
| 352 | · housing |
| 362 | · school and care |
| 368 | · outside the families |
| 397 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 411 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 419 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 429 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 445 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 467 | · what runs out |
| 471 | · the market |
| 495 | · the sectors (0.7.4) |
| 515 | · the world's moneys (WN4, 2026-10-30) |
| 535 | · the trade by partner (WN3, 2026-10-28) |
| 554 | · GDP in layers (0.7.6) |
| 571 | · the city's fund (0.7.39) |
| 587 | · the new price model, read (0.7.45) |
| 639 | RECORDING |
| 713 | · · the edge |
| 736 | · · money and credit |
| 783 | · · the budget |
| 793 | · · housing |
| 798 | · · school and care |
| 805 | · · outside the families |
| 852 | · · what runs out |
| 856 | · · the market |
| 869 | · · the sectors |
| 877 | · · the new price model, read (0.7.45) |
| 899 | · · the city's fund (0.7.39) |
| 911 | THE FOLD (0.7.55) |
| 1320 | READING |
| 1448 | · a series' record, counted (0.7.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 533 | `HistorySave.WORLD_CYCLE` | `"worldCycle", WORLD_RISK_OFF = "worldRiskOff"` | The dollar cycle's series and the risk-off's. |
| 1018 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "d...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 1040 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5). |

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
| 103 | `private List<Long> jobs` |  |
| 104 | `private List<Long> workforce` |  |
| 113 | `private List<Long> outOfWork` | The people out of work - the labour force less the posts FILLED, the People screen's figure (PopulationManager.getUnemployed()). |
| 114 | `private List<Long> population` |  |
| 117 | `private List<Long> births` | The four flows that move the population, and only these four move it. |
| 118 | `private List<Long> deaths` |  |
| 119 | `private List<Long> arrivals` |  |
| 120 | `private List<Long> departures` |  |
| 123 | `private List<Double> totalWage` | The wage BILL, not the average - the average needs the workforce too. |
| 126 | `private List<Double> minimumWage` | The dial the player sets, and what the labour market did with it. |
| 136 | `private List<Double> unskilledPremium` | What an unskilled hour costs relative to its base, and how much of the workforce has any training at all. |
| 137 | `private List<Double> skilledShare` |  |
| 140 | `private List<Double> schoolCoverage` | The schools: how much of the basic ladder is covered, and what it costs. |
| 141 | `private List<Double> schoolBill` |  |
| 144 | `private List<Double> energyRatio` |  |
| 145 | `private List<Double> waterRatio` |  |
| 146 | `private List<Double> roadRatio` |  |
| 147 | `private List<Double> sickRate` |  |
| 148 | `private List<Double> careCoverage` |  |
| 158 | `private List<Double> outbreak` | The outbreak running, as extra absence on top of what the city's care explains (Health.getOutbreakSeverity()); 0 between outbreaks. |
| 166 | `private List<Double> landPrice` | What the world asks the city for a square foot of ground, in thousands of US DOLLARS since 0.7.6 (LandManager.getGroundUsdPerSqFt()); the months an older save recorded are local money, and stay as recorded. |
| 167 | `private List<Double> foodPrice` |  |
| 168 | `private List<Double> materialsPrice` |  |
| 169 | `private List<Double> orePrice` |  |
| 184 | `private List<Double> fxRate` |  |
| 191 | `private List<Double> fxParity` | Parity beside the rate (0.7.35): where a basket costs the same here and abroad, in the rate's own units (ForeignAccounts.getParity()), so the Trade tab can draw the two on one chart. |
| 199 | `private List<Double> fxFair` | The fundamentals' level beside the rate (XR1, 2026-10-21): where parity's trend and the real gap, the balance and the cover put the D$ before the market and the interventions (ForeignAccounts.getFairLevel()), in the r... |
| 205 | `private List<Double> fxMarket` | ...and how far the rate sat from that level, a fraction, positive weaker (ForeignAccounts.deviationFromFair()): the market's swing and the interventions, a ratio and not money. |
| 213 | `private List<Double> fxShadow` | The shadow rate (XR2, 2026-10-22): where the float's rule put the D$ at the month's reprice, in the rate's own units (ForeignAccounts .getShadowRate()) - the rate itself under Lean and Float, the rate the hold is set ... |
| 219 | `private List<Double> fxCover` | ...and the vault's cover, in months of imports (ForeignAccounts .importCover(), at most 99): the hold's reserve, beside the shadow. |
| 220 | `private List<Double> reservesUsd` |  |
| 221 | `private List<Double> foreignDebtUsd` |  |
| 222 | `private List<Double> currentAccount` |  |
| 223 | `private List<Double> exportsAbroad` |  |
| 224 | `private List<Double> importsAbroad` |  |
| 232 | `private List<Double> priceIndex` |  |
| 243 | `private List<Double> usPrices` | The game dollar's price level since founding (LB3, 2026-10-31): the US prices (WorldEconomy.getPriceLevel()), 1 at founding, the "P" the Rankings board divides a city's money by to reach a dollar of 2015. |
| 250 | `private List<Double> expectedInflation` | THE ANCHOR (0.7.42): what the city expected inflation to be, a fraction a year, and how far it believed the bank (Expectations) - both ratios, so a reform leaves them alone. |
| 251 | `private List<Double> credibility` |  |
| 252 | `private List<Double> businessDebt` |  |
| 259 | `private List<Double> businessBonds` | The face of every business bond outstanding, in money (HD1, 2026-10-29): businessDebt is the loans and mortgages only, so the fade of the bonds (the companies that borrow in the bond market, the funds that hold them) ... |
| 266 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 267 | `private List<Double> bankLent` |  |
| 268 | `private List<Double> bankEquity` |  |
| 269 | `private List<Double> bankWriteOffs` |  |
| 286 | `private List<Double> bankCapacity` |  |
| 287 | `private List<Double> bankStrain` |  |
| 288 | `private List<Double> bankProfit` |  |
| 289 | `private List<Double> bankBranches` |  |
| 290 | `private List<Double> householdSavings` |  |
| 302 | `private List<Double> policyRate` |  |
| 303 | `private List<Double> bankPrime` |  |
| 304 | `private List<Double> bankDepositRate` |  |
| 305 | `private List<Double> bankFees` |  |
| 317 | `private List<Double> bankCapitalRatio` |  |
| 318 | `private List<Double> bankCapitalTarget` |  |
| 319 | `private List<Double> bankAllowance` |  |
| 320 | `private List<Double> bankProvisions` |  |
| 321 | `private List<Double> bankDividends` |  |
| 322 | `private List<Double> bankReturnOnEquity` |  |
| 333 | `private List<Double> bankLeverageRatio` | ...AND THE OTHER MEASURE (A7, 0.7.46; the Bank spec's D11): its equity over everything on its sheet (Bank.leverageRatio()) and the target it holds on that measure (leverageTarget()). |
| 334 | `private List<Double> bankLeverageTarget` |  |
| 343 | `private List<Double> taxWage` |  |
| 344 | `private List<Double> taxProperty` |  |
| 345 | `private List<Double> taxSales` |  |
| 346 | `private List<Double> taxBusiness` |  |
| 347 | `private List<Double> taxIndustrial` |  |
| 348 | `private List<Double> contributions` |  |
| 349 | `private List<Double> pensionBill` |  |
| 350 | `private List<Double> healthBill` |  |
| 358 | `private List<Double> rentPrice` |  |
| 359 | `private List<Long> homes` |  |
| 360 | `private List<Double> households` |  |
| 363 | `private List<Double> students` |  |
| 364 | `private List<Double> graduates` |  |
| 365 | `private List<Double> licences` |  |
| 366 | `private List<Double> unburied` |  |
| 375 | `private List<Double> outOfWorkOnEi` |  |
| 376 | `private List<Double> outOfWorkOffEi` |  |
| 377 | `private List<Double> unhoused` |  |
| 378 | `private List<Double> orphans` |  |
| 379 | `private List<Double> evicted` |  |
| 380 | `private List<Double> eiPaid` |  |
| 381 | `private List<Double> eiPremiums` |  |
| 383 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 384 | `private List<Double> studentGrants` |  |
| 391 | `private List<Double> foodAssistance` | FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households' groceries a month, and the baskets that bought at the price charged. |
| 392 | `private List<Double> fedByAssistance` |  |
| 393 | `private List<Double> studentLoansOwed` |  |
| 395 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 405 | `private List<Double> m0` |  |
| 406 | `private List<Double> m2` |  |
| 407 | `private List<Double> advancesToTreasury` |  |
| 408 | `private List<Double> reserves` |  |
| 409 | `private List<Double> remittance` |  |
| 415 | `private List<Double> sickPastTwoMonths` |  |
| 416 | `private List<Double> diedOfIllness` |  |
| 417 | `private List<Double> sickRecovery` |  |
| 427 | `private Map<String, List<Double>> householdsByShape` |  |
| 435 | `private List<Double> deathsBabies` |  |
| 436 | `private List<Double> deathsChildren` |  |
| 437 | `private List<Double> deathsTeens` |  |
| 438 | `private List<Double> deathsAdults` |  |
| 439 | `private List<Double> deathsSeniors` |  |
| 441 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 442 | `private List<Double> deathsOrphans` |  |
| 443 | `private List<Double> deathsUnhoused` |  |
| 452 | `private List<Double> crimeRate` |  |
| 453 | `private List<Double> policeCoverage` |  |
| 454 | `private List<Double> prisoners` |  |
| 455 | `private List<Double> caughtNotHeld` |  |
| 456 | `private List<Double> stolen` |  |
| 457 | `private List<Double> deathsKilled` |  |
| 458 | `private List<Double> safetyBill` |  |
| 459 | `private Map<String, List<Double>> crimeByCause` |  |
| 468 | `private List<Long> constructionCapacity` |  |
| 469 | `private List<Double> landUse` |  |
| 488 | `private Map<String, List<Double>> sharePrice` |  |
| 489 | `private Map<String, List<Double>> shareValue` |  |
| 512 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 513 | `private Map<String, List<Double>> sectorWorkers` |  |
| 527 | `private Map<String, List<Double>> worldMoney` |  |
| 547 | `private List<PartnerTrade.Table> tradeByPartner` |  |
| 566 | `private List<Double> consumption` |  |
| 567 | `private List<Double> investment` |  |
| 568 | `private List<Double> government` |  |
| 569 | `private List<Double> netExports` |  |
| 583 | `private List<Double> fundValue` |  |
| 584 | `private List<Double> fundPutIn` |  |
| 585 | `private List<Double> fundTakenOut` |  |
| 603 | `private List<Double> expectedLevel` |  |
| 604 | `private List<Double> indexGroceries` |  |
| 605 | `private List<Double> indexRent` |  |
| 606 | `private List<Double> indexMeals` |  |
| 607 | `private List<Double> indexLuxury` |  |
| 608 | `private List<Double> indexServices` |  |
| 609 | `private List<Double> weightGroceries` |  |
| 610 | `private List<Double> weightRent` |  |
| 611 | `private List<Double> weightMeals` |  |
| 612 | `private List<Double> weightLuxury` |  |
| 613 | `private List<Double> weightServices` |  |
| 614 | `private List<Double> basketLinkedAt` |  |
| 615 | `private List<Double> shelfPrice` |  |
| 616 | `private List<Double> shelfFloor` |  |
| 617 | `private List<Double> basketsAsked` |  |
| 618 | `private List<Double> basketsHanded` |  |
| 619 | `private List<Double> hunger` |  |
| 620 | `private List<Double> hungerPricedOut` |  |
| 621 | `private List<Double> householdsAssisted` |  |
| 622 | `private List<Double> mealMargin` |  |
| 623 | `private List<Double> mealTargetMargin` |  |
| 624 | `private List<Double> luxuryMargin` |  |
| 625 | `private List<Double> luxuryTargetMargin` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 1820 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 66-88)

### money (lines 89-101)

### people (lines 102-142)

### what throttles (lines 143-159)

### prices (lines 160-170)

### the edge (lines 171-225)

### money and credit (lines 226-270)

### what the bank could carry, and how hard (lines 271-291)

### the price of money (0.7.7) (lines 292-306)

### the bank's capital (0.7.8) (lines 307-335)

### the budget (lines 336-351)

### housing (lines 352-361)

### school and care (lines 362-367)

### outside the families (lines 368-396)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 397-410)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 411-418)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 419-428)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 429-444)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 445-466)

| line | len | member | says |
|---:|---:|---|---|
| 462 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 465 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 467-470)

### the market (lines 471-494)

| line | len | member | says |
|---:|---:|---|---|
| 492 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 493 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 495-514)

### the world's moneys (WN4, 2026-10-30) (lines 515-534)

| line | len | member | says |
|---:|---:|---|---|
| 530 | 1 | `public static String worldRateKey(String code)` | The series of a major's units to the US dollar: "fxEUR". |

### the trade by partner (WN3, 2026-10-28) (lines 535-553)

| line | len | member | says |
|---:|---:|---|---|
| 550 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 552 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 554-570)

### the city's fund (0.7.39) (lines 571-586)

### the new price model, read (0.7.45) (lines 587-638)

| line | len | member | says |
|---:|---:|---|---|
| 628 | 1 | `public static String indexKey(int component)` | The name each component's chained level is kept under (PriceIndex.COMPONENTS order): "indexGroceries" and so on. |
| 631 | 1 | `public static String weightKey(int component)` | ...and its weight in the basket in force: "weightGroceries" and so on. |
| 633 | 1 | `private static String capitalised(String s)` |  |
| 636 | 1 | `private List<List<Double>> indexLists()` | The component lists in COMPONENTS order, for the recording and the reform. |
| 637 | 1 | `private List<List<Double>> weightLists()` |  |

### RECORDING (lines 639-910)

| line | len | member | says |
|---:|---:|---|---|
| 654 | 256 | `public void recordMonth(Game game)` | One month, read off the city itself. |

### THE FOLD (0.7.55) (lines 911-1319)

| line | len | member | says |
|---:|---:|---|---|
| 928 | 12 | `public void foldOldYears()` | Folds every calendar year whose months are all older than the newest MONTHLY_KEPT, oldest first; nothing in a history of MONTHLY_KEPT months or fewer. |
| 942 | 10 | `private void foldYear(int from, int to)` | Axis entries [from, to) - one calendar year's months - into one entry at the year's last month, every series by its rule. |
| 962 | 36 | `private static void foldSeries(String name, List<? extends Number> raw, int axis, int from, int to)` | One series' months for axis entries [from, to), folded in place by its rule (YearBook.kindOf()): FLOW added, RATE averaged, LEVEL - and a series nobody has given a rule, as the year book folds it - the last. |
| 1006 | 10 | `void appendMonth(int m, java.util.function.ToDoubleFunction<String> value)` | Harnesses only (0.7.55): one more month on every series this history has, each value asked of `value` by the series' name, then the fold recordMonth() ends with - so a history can be grown past MONTHLY_KEPT without a ... |
| 1029 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 1036 | 1 | `private static double round2(double v)` |  |
| 1037 | 1 | `private static double round4(double v)` |  |
| 1043 | 4 | `static double roundSig(double v)` | A price per founding share to SHARE_PRICE_DIGITS significant figures; 0 stays 0, and a figure that is not a number is kept as 0, as finite() keeps the rest (JSON holds no NaN). |
| 1048 | 1 | `private static double round6(double v)` | Six places, for a price in thousands that four would round to a dime - and to nothing after a reform (0.7.45: the shelf). |
| 1050 | 1 | `private static double finite(double v)` | A reading the file can hold: JSON has no NaN, so a figure not struck yet is kept as 0 (0.7.45's series). |
| 1063 | 188 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 1253 | 1 | `public List<PartnerTrade.Table> getTradeByPartner()` | The finished years of the city's trade by partner (WN3), oldest first; empty on a city with no world market or a history from before it. |
| 1256 | 1 | `public int tradeYears()` | How many finished years of trade by partner the history holds. |
| 1259 | 3 | `public void setTradeByPartner(List<PartnerTrade.Table> years)` | Sets the finished years (Game, when a year finishes): a copy of the list; the tables are immutable. |
| 1264 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 1281 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 1295 | 12 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |
| 1314 | 5 | `public HistorySave snapshot()` | A copy of this history for the writer thread (UP1, 2026-10-22): every series copied into new lists, whose entries are immutable numbers, by restoreFrom() - the one place every series is listed, so a series added later... |

### READING (lines 1320-1447)

| line | len | member | says |
|---:|---:|---|---|
| 1329 | 1 | `public int months()` | How many entries the axis has - a month each, and since 0.7.55 a year each for the oldest yearlyPoints() of a history past MONTHLY_KEPT. |
| 1332 | 1 | `public List<Integer> getMonth()` | The axis: each entry's month, and a folded year's last month (0.7.55). |
| 1335 | 1 | `public int yearlyPoints()` | How many of the axis's entries, from the first, are folded years (0.7.55); 0 in a history of MONTHLY_KEPT months or fewer. |
| 1338 | 3 | `public int monthsIn(int i)` | The months axis entry i holds: 1 for a month, and a folded year's months (0.7.55). |
| 1343 | 3 | `public int firstMonthOf(int i)` | The first month axis entry i holds: its own month for a month, a folded year's first (0.7.55). |
| 1348 | 5 | `public int monthsLived()` | How many months the city has lived: every entry's months together (0.7.55) - months() until a year folds. |
| 1362 | 9 | `public int back(int i, int months)` | The entry `months` months before entry i (0.7.55): the month that far back, or the folded year that holds it - so "a year before" a folded year is the year before it, and before a month in the first year after the fol... |
| 1391 | 8 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 1410 | 21 | `public double[] alignedTail(String name, int n)` | The last {@code n} entries of aligned(name), and no more of it (UP2): the same figures as aligned(name)'s tail, value for value - the front padding as NaN, a folded year's flow as its monthly average where the tail re... |
| 1433 | 14 | `public double[] raw(String name)` | A series as it is stored, padded at the front as aligned() is: a folded year's flow is the year's sum (0.7.55). |

### a series' record, counted (0.7.9) (lines 1448-1883)

| line | len | member | says |
|---:|---:|---|---|
| 1461 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 1471 | 6 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 1479 | 6 | `public int monthsRecorded(String series)` | Months a series was recorded in - a folded year's months each (0.7.55). |
| 1487 | 5 | `public double total(String series)` | A flow added up over the months it was recorded: the stored figures, so a folded year adds its sum (0.7.55). |
| 1500 | 7 | `public double changeOver(String series, int months)` | How far a series has moved over the last `months` months, as a share of where it stood then: the last recorded value over the one `months` entries before it, less one (0.7.35: the Trade tab's rate this month and over ... |
| 1513 | 12 | `public double recentTotal(String series, int months)` | A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports). |
| 1531 | 16 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 1549 | 13 | `private static double worstYear(double[] a)` | worstYear()'s twelve months in a row, over a run of months. |
| 1569 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 1590 | 3 | `public double[] runningTotal(String series)` | ...and a series of this history summed from its first recorded entry (0.7.55): off the stored figures, so a folded year adds its sum - what City History's running totals read. |
| 1595 | 183 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 1780 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 1781 | 1 | `public List<Double> getGdp()` |  |
| 1782 | 1 | `public List<Double> getDebt()` |  |
| 1783 | 1 | `public List<Double> getInterestRate()` |  |
| 1784 | 1 | `public List<Long> getJobs()` |  |
| 1785 | 1 | `public List<Long> getWorkforce()` |  |
| 1786 | 1 | `public List<Long> getOutOfWork()` |  |
| 1787 | 1 | `public List<Long> getPopulation()` |  |
| 1800 | 52 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 1854 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |
| 1870 | 12 | `public HistorySave slim(java.util.Set<String> keep)` | A history with this one's axis and only the named series (LB3): every other series empty, so it reads as not recorded, a name this history has no series of ignored. |

