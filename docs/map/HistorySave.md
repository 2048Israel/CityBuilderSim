# HistorySave.java - 1,026 lines · 29 methods · 0 constants · model

`ham/citybuildersim/HistorySave.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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

**Used by (12):** [BankScreen](BankScreen.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

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
| 156 | · what the bank could carry, and how hard |
| 176 | · the budget |
| 192 | · housing |
| 202 | · school and care |
| 208 | · outside the families |
| 229 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 243 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 251 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 261 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 277 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 299 | · what runs out |
| 303 | · the market |
| 326 | · the sectors (0.7.4) |
| 351 | · GDP in layers (0.7.6) |
| 368 | RECORDING |
| 440 | · · the edge |
| 449 | · · money and credit |
| 476 | · · the budget |
| 486 | · · housing |
| 491 | · · school and care |
| 497 | · · outside the families |
| 542 | · · what runs out |
| 546 | · · the market |
| 558 | · · the sectors |
| 755 | READING |

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
| 150 | `private List<Double> bankPremium` |  |
| 151 | `private List<Double> bankDeposits` |  |
| 152 | `private List<Double> bankLent` |  |
| 153 | `private List<Double> bankEquity` |  |
| 154 | `private List<Double> bankWriteOffs` |  |
| 170 | `private List<Double> bankCapacity` |  |
| 171 | `private List<Double> bankStrain` |  |
| 172 | `private List<Double> bankProfit` |  |
| 173 | `private List<Double> bankBranches` |  |
| 174 | `private List<Double> householdSavings` |  |
| 183 | `private List<Double> taxWage` |  |
| 184 | `private List<Double> taxProperty` |  |
| 185 | `private List<Double> taxSales` |  |
| 186 | `private List<Double> taxBusiness` |  |
| 187 | `private List<Double> taxIndustrial` |  |
| 188 | `private List<Double> contributions` |  |
| 189 | `private List<Double> pensionBill` |  |
| 190 | `private List<Double> healthBill` |  |
| 198 | `private List<Double> rentPrice` |  |
| 199 | `private List<Integer> homes` |  |
| 200 | `private List<Double> households` |  |
| 203 | `private List<Double> students` |  |
| 204 | `private List<Double> graduates` |  |
| 205 | `private List<Double> licences` |  |
| 206 | `private List<Double> unburied` |  |
| 215 | `private List<Double> outOfWorkOnEi` |  |
| 216 | `private List<Double> outOfWorkOffEi` |  |
| 217 | `private List<Double> unhoused` |  |
| 218 | `private List<Double> orphans` |  |
| 219 | `private List<Double> evicted` |  |
| 220 | `private List<Double> eiPaid` |  |
| 221 | `private List<Double> eiPremiums` |  |
| 223 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 224 | `private List<Double> studentGrants` |  |
| 225 | `private List<Double> studentLoansOwed` |  |
| 227 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 237 | `private List<Double> m0` |  |
| 238 | `private List<Double> m2` |  |
| 239 | `private List<Double> advancesToTreasury` |  |
| 240 | `private List<Double> reserves` |  |
| 241 | `private List<Double> remittance` |  |
| 247 | `private List<Double> sickPastTwoMonths` |  |
| 248 | `private List<Double> diedOfIllness` |  |
| 249 | `private List<Double> sickRecovery` |  |
| 259 | `private Map<String, List<Double>> householdsByShape` |  |
| 267 | `private List<Double> deathsBabies` |  |
| 268 | `private List<Double> deathsChildren` |  |
| 269 | `private List<Double> deathsTeens` |  |
| 270 | `private List<Double> deathsAdults` |  |
| 271 | `private List<Double> deathsSeniors` |  |
| 273 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 274 | `private List<Double> deathsOrphans` |  |
| 275 | `private List<Double> deathsUnhoused` |  |
| 284 | `private List<Double> crimeRate` |  |
| 285 | `private List<Double> policeCoverage` |  |
| 286 | `private List<Double> prisoners` |  |
| 287 | `private List<Double> caughtNotHeld` |  |
| 288 | `private List<Double> stolen` |  |
| 289 | `private List<Double> deathsKilled` |  |
| 290 | `private List<Double> safetyBill` |  |
| 291 | `private Map<String, List<Double>> crimeByCause` |  |
| 300 | `private List<Integer> constructionCapacity` |  |
| 301 | `private List<Double> landUse` |  |
| 319 | `private Map<String, List<Double>> sharePrice` |  |
| 320 | `private Map<String, List<Double>> shareValue` |  |
| 343 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 344 | `private Map<String, List<Double>> sectorWorkers` |  |
| 363 | `private List<Double> consumption` |  |
| 364 | `private List<Double> investment` |  |
| 365 | `private List<Double> government` |  |
| 366 | `private List<Double> netExports` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 985 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 44-52)

### money (lines 53-62)

### people (lines 63-103)

### what throttles (lines 104-110)

### prices (lines 111-121)

### the edge (lines 122-141)

### money and credit (lines 142-155)

### what the bank could carry, and how hard (lines 156-175)

### the budget (lines 176-191)

### housing (lines 192-201)

### school and care (lines 202-207)

### outside the families (lines 208-228)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 229-242)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 243-250)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 251-260)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 261-276)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 277-298)

| line | len | member | says |
|---:|---:|---|---|
| 294 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 297 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 299-302)

### the market (lines 303-325)

| line | len | member | says |
|---:|---:|---|---|
| 323 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 324 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 326-350)

| line | len | member | says |
|---:|---:|---|---|
| 347 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 349 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 351-367)

### RECORDING (lines 368-754)

| line | len | member | says |
|---:|---:|---|---|
| 383 | 183 | `public void recordMonth(Game game)` | One month, read off the city itself. |
| 575 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 582 | 1 | `private static double round2(double v)` |  |
| 583 | 1 | `private static double round4(double v)` |  |
| 596 | 126 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 724 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 741 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 750 | 4 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |

### READING (lines 755-1026)

| line | len | member | says |
|---:|---:|---|---|
| 760 | 1 | `public int months()` | How many months the city has lived. |
| 762 | 1 | `public List<Integer> getMonth()` |  |
| 777 | 14 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 798 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 815 | 138 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 955 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 956 | 1 | `public List<Double> getGdp()` |  |
| 957 | 1 | `public List<Double> getDebt()` |  |
| 958 | 1 | `public List<Double> getInterestRate()` |  |
| 959 | 1 | `public List<Integer> getJobs()` |  |
| 960 | 1 | `public List<Integer> getWorkforce()` |  |
| 961 | 1 | `public List<Integer> getOutOfWork()` |  |
| 962 | 1 | `public List<Integer> getPopulation()` |  |
| 975 | 39 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 1016 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |

