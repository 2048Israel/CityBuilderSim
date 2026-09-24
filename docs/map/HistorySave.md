# HistorySave.java - 1,159 lines · 34 methods · 0 constants · model

`ham/citybuildersim/HistorySave.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (7), [Crime](Crime.md) (4), [FamilyStructure](FamilyStructure.md) (3), [Equity](Equity.md) (3), [GameFiles](GameFiles.md) (2), [Game](Game.md) (1), [EconomyManager](EconomyManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [PopulationManager](PopulationManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Migration](Migration.md) (1), [LabourMarket](LabourMarket.md) (1), [JobType](JobType.md) (1), [WageBand](WageBand.md) (1), [Education](Education.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Bank](Bank.md) (1), [CentralBank](CentralBank.md) (1), [Unemployment](Unemployment.md) (1), [FamilyModel](FamilyModel.md) (1), [Sickness](Sickness.md) (1), [Exchange](Exchange.md) (1), [Sector](Sector.md) (1)

**Used by (13):** [BankScreen](BankScreen.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 44 | · THE AXIS |
| 53 | · money |
| 63 | · people |
| 104 | · what throttles |
| 111 | · prices |
| 122 | · the edge |
| 142 | · money and credit |
| 161 | · what the bank could carry, and how hard |
| 182 | · the price of money (0.7.7) |
| 197 | · the bank's capital (0.7.8) |
| 214 | · the budget |
| 230 | · housing |
| 240 | · school and care |
| 246 | · outside the families |
| 267 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 281 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 289 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 299 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 315 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 337 | · what runs out |
| 341 | · the market |
| 364 | · the sectors (0.7.4) |
| 389 | · GDP in layers (0.7.6) |
| 406 | RECORDING |
| 478 | · · the edge |
| 487 | · · money and credit |
| 527 | · · the budget |
| 537 | · · housing |
| 542 | · · school and care |
| 548 | · · outside the families |
| 593 | · · what runs out |
| 597 | · · the market |
| 609 | · · the sectors |
| 815 | READING |
| 852 | · a series' record, counted (0.7.9) |

## Fields (state)

| line | field | says |
|---:|---|---|
| 51 | `private List<Integer> month` |  |
| 54 | `private List<Double> cash` |  |
| 55 | `private List<Double> gdp` |  |
| 56 | `private List<Double> debt` |  |
| 57 | `private List<Double> interestRate` |  |
| 60 | `private List<Double> revenue` | Government revenue and what it kept, both monthly. |
| 61 | `private List<Double> surplus` |  |
| 64 | `private List<Integer> jobs` |  |
| 65 | `private List<Integer> workforce` |  |
| 74 | `private List<Integer> outOfWork` | The people out of work - the labour force less the posts FILLED, the People screen's figure (PopulationManager.getUnemployed()). |
| 75 | `private List<Integer> population` |  |
| 78 | `private List<Integer> births` | The four flows that move the population, and only these four move it. |
| 79 | `private List<Integer> deaths` |  |
| 80 | `private List<Integer> arrivals` |  |
| 81 | `private List<Integer> departures` |  |
| 84 | `private List<Double> totalWage` | The wage BILL, not the average - the average needs the workforce too. |
| 87 | `private List<Double> minimumWage` | The dial the player sets, and what the labour market did with it. |
| 97 | `private List<Double> unskilledPremium` | What an unskilled hour costs relative to its base, and how much of the workforce has any training at all. |
| 98 | `private List<Double> skilledShare` |  |
| 101 | `private List<Double> schoolCoverage` | The schools: how much of the basic ladder is covered, and what it costs. |
| 102 | `private List<Double> schoolBill` |  |
| 105 | `private List<Double> energyRatio` |  |
| 106 | `private List<Double> waterRatio` |  |
| 107 | `private List<Double> roadRatio` |  |
| 108 | `private List<Double> sickRate` |  |
| 109 | `private List<Double> careCoverage` |  |
| 117 | `private List<Double> landPrice` | What the world asks the city for a square foot of ground, in thousands of US DOLLARS since 0.7.6 (LandManager.getGroundUsdPerSqFt()); the months an older save recorded are local money, and stay as recorded. |
| 118 | `private List<Double> foodPrice` |  |
| 119 | `private List<Double> materialsPrice` |  |
| 120 | `private List<Double> orePrice` |  |
| 135 | `private List<Double> fxRate` |  |
| 136 | `private List<Double> reservesUsd` |  |
| 137 | `private List<Double> foreignDebtUsd` |  |
| 138 | `private List<Double> currentAccount` |  |
| 139 | `private List<Double> exportsAbroad` |  |
| 140 | `private List<Double> importsAbroad` |  |
| 148 | `private List<Double> priceIndex` |  |
| 149 | `private List<Double> businessDebt` |  |
| 156 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 157 | `private List<Double> bankLent` |  |
| 158 | `private List<Double> bankEquity` |  |
| 159 | `private List<Double> bankWriteOffs` |  |
| 176 | `private List<Double> bankCapacity` |  |
| 177 | `private List<Double> bankStrain` |  |
| 178 | `private List<Double> bankProfit` |  |
| 179 | `private List<Double> bankBranches` |  |
| 180 | `private List<Double> householdSavings` |  |
| 192 | `private List<Double> policyRate` |  |
| 193 | `private List<Double> bankPrime` |  |
| 194 | `private List<Double> bankDepositRate` |  |
| 195 | `private List<Double> bankFees` |  |
| 207 | `private List<Double> bankCapitalRatio` |  |
| 208 | `private List<Double> bankCapitalTarget` |  |
| 209 | `private List<Double> bankAllowance` |  |
| 210 | `private List<Double> bankProvisions` |  |
| 211 | `private List<Double> bankDividends` |  |
| 212 | `private List<Double> bankReturnOnEquity` |  |
| 221 | `private List<Double> taxWage` |  |
| 222 | `private List<Double> taxProperty` |  |
| 223 | `private List<Double> taxSales` |  |
| 224 | `private List<Double> taxBusiness` |  |
| 225 | `private List<Double> taxIndustrial` |  |
| 226 | `private List<Double> contributions` |  |
| 227 | `private List<Double> pensionBill` |  |
| 228 | `private List<Double> healthBill` |  |
| 236 | `private List<Double> rentPrice` |  |
| 237 | `private List<Integer> homes` |  |
| 238 | `private List<Double> households` |  |
| 241 | `private List<Double> students` |  |
| 242 | `private List<Double> graduates` |  |
| 243 | `private List<Double> licences` |  |
| 244 | `private List<Double> unburied` |  |
| 253 | `private List<Double> outOfWorkOnEi` |  |
| 254 | `private List<Double> outOfWorkOffEi` |  |
| 255 | `private List<Double> unhoused` |  |
| 256 | `private List<Double> orphans` |  |
| 257 | `private List<Double> evicted` |  |
| 258 | `private List<Double> eiPaid` |  |
| 259 | `private List<Double> eiPremiums` |  |
| 261 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 262 | `private List<Double> studentGrants` |  |
| 263 | `private List<Double> studentLoansOwed` |  |
| 265 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 275 | `private List<Double> m0` |  |
| 276 | `private List<Double> m2` |  |
| 277 | `private List<Double> advancesToTreasury` |  |
| 278 | `private List<Double> reserves` |  |
| 279 | `private List<Double> remittance` |  |
| 285 | `private List<Double> sickPastTwoMonths` |  |
| 286 | `private List<Double> diedOfIllness` |  |
| 287 | `private List<Double> sickRecovery` |  |
| 297 | `private Map<String, List<Double>> householdsByShape` |  |
| 305 | `private List<Double> deathsBabies` |  |
| 306 | `private List<Double> deathsChildren` |  |
| 307 | `private List<Double> deathsTeens` |  |
| 308 | `private List<Double> deathsAdults` |  |
| 309 | `private List<Double> deathsSeniors` |  |
| 311 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 312 | `private List<Double> deathsOrphans` |  |
| 313 | `private List<Double> deathsUnhoused` |  |
| 322 | `private List<Double> crimeRate` |  |
| 323 | `private List<Double> policeCoverage` |  |
| 324 | `private List<Double> prisoners` |  |
| 325 | `private List<Double> caughtNotHeld` |  |
| 326 | `private List<Double> stolen` |  |
| 327 | `private List<Double> deathsKilled` |  |
| 328 | `private List<Double> safetyBill` |  |
| 329 | `private Map<String, List<Double>> crimeByCause` |  |
| 338 | `private List<Integer> constructionCapacity` |  |
| 339 | `private List<Double> landUse` |  |
| 357 | `private Map<String, List<Double>> sharePrice` |  |
| 358 | `private Map<String, List<Double>> shareValue` |  |
| 381 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 382 | `private Map<String, List<Double>> sectorWorkers` |  |
| 401 | `private List<Double> consumption` |  |
| 402 | `private List<Double> investment` |  |
| 403 | `private List<Double> government` |  |
| 404 | `private List<Double> netExports` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 1118 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 44-52)

### money (lines 53-62)

### people (lines 63-103)

### what throttles (lines 104-110)

### prices (lines 111-121)

### the edge (lines 122-141)

### money and credit (lines 142-160)

### what the bank could carry, and how hard (lines 161-181)

### the price of money (0.7.7) (lines 182-196)

### the bank's capital (0.7.8) (lines 197-213)

### the budget (lines 214-229)

### housing (lines 230-239)

### school and care (lines 240-245)

### outside the families (lines 246-266)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 267-280)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 281-288)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 289-298)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 299-314)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 315-336)

| line | len | member | says |
|---:|---:|---|---|
| 332 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 335 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 337-340)

### the market (lines 341-363)

| line | len | member | says |
|---:|---:|---|---|
| 361 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 362 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 364-388)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 387 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 389-405)

### RECORDING (lines 406-814)

| line | len | member | says |
|---:|---:|---|---|
| 421 | 196 | `public void recordMonth(Game game)` | One month, read off the city itself. |
| 626 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 633 | 1 | `private static double round2(double v)` |  |
| 634 | 1 | `private static double round4(double v)` |  |
| 647 | 135 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 784 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 801 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 810 | 4 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |

### READING (lines 815-851)

| line | len | member | says |
|---:|---:|---|---|
| 820 | 1 | `public int months()` | How many months the city has lived. |
| 822 | 1 | `public List<Integer> getMonth()` |  |
| 837 | 14 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |

### a series' record, counted (0.7.9) (lines 852-1159)

| line | len | member | says |
|---:|---:|---|---|
| 862 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 872 | 5 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 879 | 5 | `public int monthsRecorded(String series)` | Months a series was recorded in. |
| 886 | 5 | `public double total(String series)` | A flow added up over the months it was recorded. |
| 897 | 14 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 918 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 935 | 147 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 1084 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 1085 | 1 | `public List<Double> getGdp()` |  |
| 1086 | 1 | `public List<Double> getDebt()` |  |
| 1087 | 1 | `public List<Double> getInterestRate()` |  |
| 1088 | 1 | `public List<Integer> getJobs()` |  |
| 1089 | 1 | `public List<Integer> getWorkforce()` |  |
| 1090 | 1 | `public List<Integer> getOutOfWork()` |  |
| 1091 | 1 | `public List<Integer> getPopulation()` |  |
| 1104 | 43 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 1149 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |

