# HistorySave.java - 1,898 lines · 64 methods · 4 constants · model

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

**Used by (37):** [BankScreen](BankScreen.md), [BuildAdvice](BuildAdvice.md), [BuybackCheck](BuybackCheck.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [CityMeasures](CityMeasures.md), [ClockCheck](ClockCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [DebtExchangeCheck](DebtExchangeCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [Rankings](Rankings.md), [RankingsCheck](RankingsCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SaveSlotCheck](SaveSlotCheck.md), [SaveWriter](SaveWriter.md), [SavedCities](SavedCities.md), [ScaleCheck](ScaleCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [WorldMoneysCheck](WorldMoneysCheck.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

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
| 274 | · what the bank could carry, and how hard |
| 295 | · the price of money (0.7.7) |
| 310 | · the bank's capital (0.7.8) |
| 339 | · the budget |
| 355 | · housing |
| 365 | · school and care |
| 371 | · outside the families |
| 400 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 414 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 422 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 432 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 448 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 470 | · what runs out |
| 474 | · the market |
| 498 | · the sectors (0.7.4) |
| 518 | · the world's moneys (WN4, 2026-10-30) |
| 542 | · the trade by partner (WN3, 2026-10-28) |
| 561 | · GDP in layers (0.7.6) |
| 578 | · the city's fund (0.7.39) |
| 594 | · the new price model, read (0.7.45) |
| 646 | RECORDING |
| 721 | · · the edge |
| 749 | · · money and credit |
| 796 | · · the budget |
| 806 | · · housing |
| 811 | · · school and care |
| 818 | · · outside the families |
| 865 | · · what runs out |
| 869 | · · the market |
| 882 | · · the sectors |
| 890 | · · the new price model, read (0.7.45) |
| 912 | · · the city's fund (0.7.39) |
| 924 | THE FOLD (0.7.55) |
| 1334 | READING |
| 1462 | · a series' record, counted (0.7.9) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `HistorySave.MONTHLY_KEPT` | `6_000` | Months kept a month to an entry, the newest: five hundred years (0.7.55). |
| 540 | `HistorySave.WORLD_CYCLE` | `"worldCycle", WORLD_RISK_OFF = "worldRiskOff"` | The dollar cycle's series and the risk-off's. |
| 1031 | `HistorySave.LONG_SERIES` | `java.util.Set.of("jobs", "workforce", "outOfWork", "population", "births", "d...` | The series kept as whole numbers, a List<Long> each: what appendMonth() adds as longs to an empty one. |
| 1053 | `HistorySave.SHARE_PRICE_DIGITS` | `6` | Share prices are recorded to six significant figures: a consolidated company's price per founding share is far under a ten-thousandth (C5). |

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
| 246 | `private List<Double> usPrices` | The game dollar's price level since founding (LB3, 2026-10-31): the US prices (WorldEconomy.getPriceLevel()), 1 at founding, the "P" the Rankings board divides a city's money by to reach a dollar of 2015. |
| 253 | `private List<Double> expectedInflation` | THE ANCHOR (0.7.42): what the city expected inflation to be, a fraction a year, and how far it believed the bank (Expectations) - both ratios, so a reform leaves them alone. |
| 254 | `private List<Double> credibility` |  |
| 255 | `private List<Double> businessDebt` |  |
| 262 | `private List<Double> businessBonds` | The face of every business bond outstanding, in money (HD1, 2026-10-29): businessDebt is the loans and mortgages only, so the fade of the bonds (the companies that borrow in the bond market, the funds that hold them) ... |
| 269 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 270 | `private List<Double> bankLent` |  |
| 271 | `private List<Double> bankEquity` |  |
| 272 | `private List<Double> bankWriteOffs` |  |
| 289 | `private List<Double> bankCapacity` |  |
| 290 | `private List<Double> bankStrain` |  |
| 291 | `private List<Double> bankProfit` |  |
| 292 | `private List<Double> bankBranches` |  |
| 293 | `private List<Double> householdSavings` |  |
| 305 | `private List<Double> policyRate` |  |
| 306 | `private List<Double> bankPrime` |  |
| 307 | `private List<Double> bankDepositRate` |  |
| 308 | `private List<Double> bankFees` |  |
| 320 | `private List<Double> bankCapitalRatio` |  |
| 321 | `private List<Double> bankCapitalTarget` |  |
| 322 | `private List<Double> bankAllowance` |  |
| 323 | `private List<Double> bankProvisions` |  |
| 324 | `private List<Double> bankDividends` |  |
| 325 | `private List<Double> bankReturnOnEquity` |  |
| 336 | `private List<Double> bankLeverageRatio` | ...AND THE OTHER MEASURE (A7, 0.7.46; the Bank spec's D11): its equity over everything on its sheet (Bank.leverageRatio()) and the target it holds on that measure (leverageTarget()). |
| 337 | `private List<Double> bankLeverageTarget` |  |
| 346 | `private List<Double> taxWage` |  |
| 347 | `private List<Double> taxProperty` |  |
| 348 | `private List<Double> taxSales` |  |
| 349 | `private List<Double> taxBusiness` |  |
| 350 | `private List<Double> taxIndustrial` |  |
| 351 | `private List<Double> contributions` |  |
| 352 | `private List<Double> pensionBill` |  |
| 353 | `private List<Double> healthBill` |  |
| 361 | `private List<Double> rentPrice` |  |
| 362 | `private List<Long> homes` |  |
| 363 | `private List<Double> households` |  |
| 366 | `private List<Double> students` |  |
| 367 | `private List<Double> graduates` |  |
| 368 | `private List<Double> licences` |  |
| 369 | `private List<Double> unburied` |  |
| 378 | `private List<Double> outOfWorkOnEi` |  |
| 379 | `private List<Double> outOfWorkOffEi` |  |
| 380 | `private List<Double> unhoused` |  |
| 381 | `private List<Double> orphans` |  |
| 382 | `private List<Double> evicted` |  |
| 383 | `private List<Double> eiPaid` |  |
| 384 | `private List<Double> eiPremiums` |  |
| 386 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 387 | `private List<Double> studentGrants` |  |
| 394 | `private List<Double> foodAssistance` | FOOD ASSISTANCE (0.7.43): what the treasury paid toward the households' groceries a month, and the baskets that bought at the price charged. |
| 395 | `private List<Double> fedByAssistance` |  |
| 396 | `private List<Double> studentLoansOwed` |  |
| 398 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 408 | `private List<Double> m0` |  |
| 409 | `private List<Double> m2` |  |
| 410 | `private List<Double> advancesToTreasury` |  |
| 411 | `private List<Double> reserves` |  |
| 412 | `private List<Double> remittance` |  |
| 418 | `private List<Double> sickPastTwoMonths` |  |
| 419 | `private List<Double> diedOfIllness` |  |
| 420 | `private List<Double> sickRecovery` |  |
| 430 | `private Map<String, List<Double>> householdsByShape` |  |
| 438 | `private List<Double> deathsBabies` |  |
| 439 | `private List<Double> deathsChildren` |  |
| 440 | `private List<Double> deathsTeens` |  |
| 441 | `private List<Double> deathsAdults` |  |
| 442 | `private List<Double> deathsSeniors` |  |
| 444 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 445 | `private List<Double> deathsOrphans` |  |
| 446 | `private List<Double> deathsUnhoused` |  |
| 455 | `private List<Double> crimeRate` |  |
| 456 | `private List<Double> policeCoverage` |  |
| 457 | `private List<Double> prisoners` |  |
| 458 | `private List<Double> caughtNotHeld` |  |
| 459 | `private List<Double> stolen` |  |
| 460 | `private List<Double> deathsKilled` |  |
| 461 | `private List<Double> safetyBill` |  |
| 462 | `private Map<String, List<Double>> crimeByCause` |  |
| 471 | `private List<Long> constructionCapacity` |  |
| 472 | `private List<Double> landUse` |  |
| 491 | `private Map<String, List<Double>> sharePrice` |  |
| 492 | `private Map<String, List<Double>> shareValue` |  |
| 515 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 516 | `private Map<String, List<Double>> sectorWorkers` |  |
| 531 | `private Map<String, List<Double>> worldMoney` |  |
| 554 | `private List<PartnerTrade.Table> tradeByPartner` |  |
| 573 | `private List<Double> consumption` |  |
| 574 | `private List<Double> investment` |  |
| 575 | `private List<Double> government` |  |
| 576 | `private List<Double> netExports` |  |
| 590 | `private List<Double> fundValue` |  |
| 591 | `private List<Double> fundPutIn` |  |
| 592 | `private List<Double> fundTakenOut` |  |
| 610 | `private List<Double> expectedLevel` |  |
| 611 | `private List<Double> indexGroceries` |  |
| 612 | `private List<Double> indexRent` |  |
| 613 | `private List<Double> indexMeals` |  |
| 614 | `private List<Double> indexLuxury` |  |
| 615 | `private List<Double> indexServices` |  |
| 616 | `private List<Double> weightGroceries` |  |
| 617 | `private List<Double> weightRent` |  |
| 618 | `private List<Double> weightMeals` |  |
| 619 | `private List<Double> weightLuxury` |  |
| 620 | `private List<Double> weightServices` |  |
| 621 | `private List<Double> basketLinkedAt` |  |
| 622 | `private List<Double> shelfPrice` |  |
| 623 | `private List<Double> shelfFloor` |  |
| 624 | `private List<Double> basketsAsked` |  |
| 625 | `private List<Double> basketsHanded` |  |
| 626 | `private List<Double> hunger` |  |
| 627 | `private List<Double> hungerPricedOut` |  |
| 628 | `private List<Double> householdsAssisted` |  |
| 629 | `private List<Double> mealMargin` |  |
| 630 | `private List<Double> mealTargetMargin` |  |
| 631 | `private List<Double> luxuryMargin` |  |
| 632 | `private List<Double> luxuryTargetMargin` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 1835 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 66-88)

### money (lines 89-104)

### people (lines 105-145)

### what throttles (lines 146-162)

### prices (lines 163-173)

### the edge (lines 174-228)

### money and credit (lines 229-273)

### what the bank could carry, and how hard (lines 274-294)

### the price of money (0.7.7) (lines 295-309)

### the bank's capital (0.7.8) (lines 310-338)

### the budget (lines 339-354)

### housing (lines 355-364)

### school and care (lines 365-370)

### outside the families (lines 371-399)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 400-413)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 414-421)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 422-431)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 432-447)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 448-469)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 468 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 470-473)

### the market (lines 474-497)

| line | len | member | says |
|---:|---:|---|---|
| 495 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 496 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 498-517)

### the world's moneys (WN4, 2026-10-30) (lines 518-541)

| line | len | member | says |
|---:|---:|---|---|
| 534 | 1 | `public static String worldRateKey(String code)` | The series of a major's units to the US dollar: "fxEUR". |
| 537 | 1 | `public static String policyRateKey(String code)` | The series of a central bank's policy rate, a fraction a year (XR4): "policyUSD", for each of the eight, the dollar's too. |

### the trade by partner (WN3, 2026-10-28) (lines 542-560)

| line | len | member | says |
|---:|---:|---|---|
| 557 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 559 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 561-577)

### the city's fund (0.7.39) (lines 578-593)

### the new price model, read (0.7.45) (lines 594-645)

| line | len | member | says |
|---:|---:|---|---|
| 635 | 1 | `public static String indexKey(int component)` | The name each component's chained level is kept under (PriceIndex.COMPONENTS order): "indexGroceries" and so on. |
| 638 | 1 | `public static String weightKey(int component)` | ...and its weight in the basket in force: "weightGroceries" and so on. |
| 640 | 1 | `private static String capitalised(String s)` |  |
| 643 | 1 | `private List<List<Double>> indexLists()` | The component lists in COMPONENTS order, for the recording and the reform. |
| 644 | 1 | `private List<List<Double>> weightLists()` |  |

### RECORDING (lines 646-923)

| line | len | member | says |
|---:|---:|---|---|
| 661 | 262 | `public void recordMonth(Game game)` | One month, read off the city itself. |

### THE FOLD (0.7.55) (lines 924-1333)

| line | len | member | says |
|---:|---:|---|---|
| 941 | 12 | `public void foldOldYears()` | Folds every calendar year whose months are all older than the newest MONTHLY_KEPT, oldest first; nothing in a history of MONTHLY_KEPT months or fewer. |
| 955 | 10 | `private void foldYear(int from, int to)` | Axis entries [from, to) - one calendar year's months - into one entry at the year's last month, every series by its rule. |
| 975 | 36 | `private static void foldSeries(String name, List<? extends Number> raw, int axis, int from, int to)` | One series' months for axis entries [from, to), folded in place by its rule (YearBook.kindOf()): FLOW added, RATE averaged, LEVEL - and a series nobody has given a rule, as the year book folds it - the last. |
| 1019 | 10 | `void appendMonth(int m, java.util.function.ToDoubleFunction<String> value)` | Harnesses only (0.7.55): one more month on every series this history has, each value asked of `value` by the series' name, then the fold recordMonth() ends with - so a history can be grown past MONTHLY_KEPT without a ... |
| 1042 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 1049 | 1 | `private static double round2(double v)` |  |
| 1050 | 1 | `private static double round4(double v)` |  |
| 1056 | 4 | `static double roundSig(double v)` | A price per founding share to SHARE_PRICE_DIGITS significant figures; 0 stays 0, and a figure that is not a number is kept as 0, as finite() keeps the rest (JSON holds no NaN). |
| 1061 | 1 | `private static double round6(double v)` | Six places, for a price in thousands that four would round to a dime - and to nothing after a reform (0.7.45: the shelf). |
| 1063 | 1 | `private static double finite(double v)` | A reading the file can hold: JSON has no NaN, so a figure not struck yet is kept as 0 (0.7.45's series). |
| 1076 | 189 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 1267 | 1 | `public List<PartnerTrade.Table> getTradeByPartner()` | The finished years of the city's trade by partner (WN3), oldest first; empty on a city with no world market or a history from before it. |
| 1270 | 1 | `public int tradeYears()` | How many finished years of trade by partner the history holds. |
| 1273 | 3 | `public void setTradeByPartner(List<PartnerTrade.Table> years)` | Sets the finished years (Game, when a year finishes): a copy of the list; the tables are immutable. |
| 1278 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 1295 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 1309 | 12 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |
| 1328 | 5 | `public HistorySave snapshot()` | A copy of this history for the writer thread (UP1, 2026-10-22): every series copied into new lists, whose entries are immutable numbers, by restoreFrom() - the one place every series is listed, so a series added later... |

### READING (lines 1334-1461)

| line | len | member | says |
|---:|---:|---|---|
| 1343 | 1 | `public int months()` | How many entries the axis has - a month each, and since 0.7.55 a year each for the oldest yearlyPoints() of a history past MONTHLY_KEPT. |
| 1346 | 1 | `public List<Integer> getMonth()` | The axis: each entry's month, and a folded year's last month (0.7.55). |
| 1349 | 1 | `public int yearlyPoints()` | How many of the axis's entries, from the first, are folded years (0.7.55); 0 in a history of MONTHLY_KEPT months or fewer. |
| 1352 | 3 | `public int monthsIn(int i)` | The months axis entry i holds: 1 for a month, and a folded year's months (0.7.55). |
| 1357 | 3 | `public int firstMonthOf(int i)` | The first month axis entry i holds: its own month for a month, a folded year's first (0.7.55). |
| 1362 | 5 | `public int monthsLived()` | How many months the city has lived: every entry's months together (0.7.55) - months() until a year folds. |
| 1376 | 9 | `public int back(int i, int months)` | The entry `months` months before entry i (0.7.55): the month that far back, or the folded year that holds it - so "a year before" a folded year is the year before it, and before a month in the first year after the fol... |
| 1405 | 8 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 1424 | 21 | `public double[] alignedTail(String name, int n)` | The last {@code n} entries of aligned(name), and no more of it (UP2): the same figures as aligned(name)'s tail, value for value - the front padding as NaN, a folded year's flow as its monthly average where the tail re... |
| 1447 | 14 | `public double[] raw(String name)` | A series as it is stored, padded at the front as aligned() is: a folded year's flow is the year's sum (0.7.55). |

### a series' record, counted (0.7.9) (lines 1462-1898)

| line | len | member | says |
|---:|---:|---|---|
| 1475 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 1485 | 6 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 1493 | 6 | `public int monthsRecorded(String series)` | Months a series was recorded in - a folded year's months each (0.7.55). |
| 1501 | 5 | `public double total(String series)` | A flow added up over the months it was recorded: the stored figures, so a folded year adds its sum (0.7.55). |
| 1514 | 7 | `public double changeOver(String series, int months)` | How far a series has moved over the last `months` months, as a share of where it stood then: the last recorded value over the one `months` entries before it, less one (0.7.35: the Trade tab's rate this month and over ... |
| 1527 | 12 | `public double recentTotal(String series, int months)` | A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports). |
| 1545 | 16 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 1563 | 13 | `private static double worstYear(double[] a)` | worstYear()'s twelve months in a row, over a run of months. |
| 1583 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 1604 | 3 | `public double[] runningTotal(String series)` | ...and a series of this history summed from its first recorded entry (0.7.55): off the stored figures, so a folded year adds its sum - what City History's running totals read. |
| 1609 | 184 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 1795 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 1796 | 1 | `public List<Double> getGdp()` |  |
| 1797 | 1 | `public List<Double> getDebt()` |  |
| 1798 | 1 | `public List<Double> getInterestRate()` |  |
| 1799 | 1 | `public List<Long> getJobs()` |  |
| 1800 | 1 | `public List<Long> getWorkforce()` |  |
| 1801 | 1 | `public List<Long> getOutOfWork()` |  |
| 1802 | 1 | `public List<Long> getPopulation()` |  |
| 1815 | 52 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 1869 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |
| 1885 | 12 | `public HistorySave slim(java.util.Set<String> keep)` | A history with this one's axis and only the named series (LB3): every other series empty, so it reads as not recorded, a name this history has no series of ignored. |

