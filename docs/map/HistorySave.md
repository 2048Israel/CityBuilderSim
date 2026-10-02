# HistorySave.java - 1,225 lines · 36 methods · 0 constants · model

`ham/citybuildersim/HistorySave.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

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

**Used by (24):** [BankScreen](BankScreen.md), [ChartCheck](ChartCheck.md), [ChartModel](ChartModel.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [FinancesScreen](FinancesScreen.md), [FundScreen](FundScreen.md), [FundView](FundView.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [LandScreen](LandScreen.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 44 | · THE AXIS |
| 53 | · money |
| 63 | · people |
| 104 | · what throttles |
| 111 | · prices |
| 122 | · the edge |
| 149 | · money and credit |
| 168 | · what the bank could carry, and how hard |
| 189 | · the price of money (0.7.7) |
| 204 | · the bank's capital (0.7.8) |
| 221 | · the budget |
| 237 | · housing |
| 247 | · school and care |
| 253 | · outside the families |
| 274 | · THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: |
| 288 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 296 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 306 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 322 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 344 | · what runs out |
| 348 | · the market |
| 372 | · the sectors (0.7.4) |
| 397 | · GDP in layers (0.7.6) |
| 414 | · the city's fund (0.7.39) |
| 430 | RECORDING |
| 502 | · · the edge |
| 512 | · · money and credit |
| 552 | · · the budget |
| 562 | · · housing |
| 567 | · · school and care |
| 573 | · · outside the families |
| 618 | · · what runs out |
| 622 | · · the market |
| 634 | · · the sectors |
| 642 | · · the city's fund (0.7.39) |
| 852 | READING |
| 889 | · a series' record, counted (0.7.9) |

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
| 142 | `private List<Double> fxParity` | Parity beside the rate (0.7.35): where a basket costs the same here and abroad, in the rate's own units (ForeignAccounts.getParity()), so the Trade tab can draw the two on one chart. |
| 143 | `private List<Double> reservesUsd` |  |
| 144 | `private List<Double> foreignDebtUsd` |  |
| 145 | `private List<Double> currentAccount` |  |
| 146 | `private List<Double> exportsAbroad` |  |
| 147 | `private List<Double> importsAbroad` |  |
| 155 | `private List<Double> priceIndex` |  |
| 156 | `private List<Double> businessDebt` |  |
| 163 | `private List<Double> bankDeposits` | bankPremium WAS HERE until 0.7.7: the points the strained bank added to every rate in the city. |
| 164 | `private List<Double> bankLent` |  |
| 165 | `private List<Double> bankEquity` |  |
| 166 | `private List<Double> bankWriteOffs` |  |
| 183 | `private List<Double> bankCapacity` |  |
| 184 | `private List<Double> bankStrain` |  |
| 185 | `private List<Double> bankProfit` |  |
| 186 | `private List<Double> bankBranches` |  |
| 187 | `private List<Double> householdSavings` |  |
| 199 | `private List<Double> policyRate` |  |
| 200 | `private List<Double> bankPrime` |  |
| 201 | `private List<Double> bankDepositRate` |  |
| 202 | `private List<Double> bankFees` |  |
| 214 | `private List<Double> bankCapitalRatio` |  |
| 215 | `private List<Double> bankCapitalTarget` |  |
| 216 | `private List<Double> bankAllowance` |  |
| 217 | `private List<Double> bankProvisions` |  |
| 218 | `private List<Double> bankDividends` |  |
| 219 | `private List<Double> bankReturnOnEquity` |  |
| 228 | `private List<Double> taxWage` |  |
| 229 | `private List<Double> taxProperty` |  |
| 230 | `private List<Double> taxSales` |  |
| 231 | `private List<Double> taxBusiness` |  |
| 232 | `private List<Double> taxIndustrial` |  |
| 233 | `private List<Double> contributions` |  |
| 234 | `private List<Double> pensionBill` |  |
| 235 | `private List<Double> healthBill` |  |
| 243 | `private List<Double> rentPrice` |  |
| 244 | `private List<Integer> homes` |  |
| 245 | `private List<Double> households` |  |
| 248 | `private List<Double> students` |  |
| 249 | `private List<Double> graduates` |  |
| 250 | `private List<Double> licences` |  |
| 251 | `private List<Double> unburied` |  |
| 260 | `private List<Double> outOfWorkOnEi` |  |
| 261 | `private List<Double> outOfWorkOffEi` |  |
| 262 | `private List<Double> unhoused` |  |
| 263 | `private List<Double> orphans` |  |
| 264 | `private List<Double> evicted` |  |
| 265 | `private List<Double> eiPaid` |  |
| 266 | `private List<Double> eiPremiums` |  |
| 268 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 269 | `private List<Double> studentGrants` |  |
| 270 | `private List<Double> studentLoansOwed` |  |
| 272 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 282 | `private List<Double> m0` |  |
| 283 | `private List<Double> m2` |  |
| 284 | `private List<Double> advancesToTreasury` |  |
| 285 | `private List<Double> reserves` |  |
| 286 | `private List<Double> remittance` |  |
| 292 | `private List<Double> sickPastTwoMonths` |  |
| 293 | `private List<Double> diedOfIllness` |  |
| 294 | `private List<Double> sickRecovery` |  |
| 304 | `private Map<String, List<Double>> householdsByShape` |  |
| 312 | `private List<Double> deathsBabies` |  |
| 313 | `private List<Double> deathsChildren` |  |
| 314 | `private List<Double> deathsTeens` |  |
| 315 | `private List<Double> deathsAdults` |  |
| 316 | `private List<Double> deathsSeniors` |  |
| 318 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 319 | `private List<Double> deathsOrphans` |  |
| 320 | `private List<Double> deathsUnhoused` |  |
| 329 | `private List<Double> crimeRate` |  |
| 330 | `private List<Double> policeCoverage` |  |
| 331 | `private List<Double> prisoners` |  |
| 332 | `private List<Double> caughtNotHeld` |  |
| 333 | `private List<Double> stolen` |  |
| 334 | `private List<Double> deathsKilled` |  |
| 335 | `private List<Double> safetyBill` |  |
| 336 | `private Map<String, List<Double>> crimeByCause` |  |
| 345 | `private List<Integer> constructionCapacity` |  |
| 346 | `private List<Double> landUse` |  |
| 365 | `private Map<String, List<Double>> sharePrice` |  |
| 366 | `private Map<String, List<Double>> shareValue` |  |
| 389 | `private Map<String, List<Double>> sectorNetIncome` |  |
| 390 | `private Map<String, List<Double>> sectorWorkers` |  |
| 409 | `private List<Double> consumption` |  |
| 410 | `private List<Double> investment` |  |
| 411 | `private List<Double> government` |  |
| 412 | `private List<Double> netExports` |  |
| 426 | `private List<Double> fundValue` |  |
| 427 | `private List<Double> fundPutIn` |  |
| 428 | `private List<Double> fundTakenOut` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 1184 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 44-52)

### money (lines 53-62)

### people (lines 63-103)

### what throttles (lines 104-110)

### prices (lines 111-121)

### the edge (lines 122-148)

### money and credit (lines 149-167)

### what the bank could carry, and how hard (lines 168-188)

### the price of money (0.7.7) (lines 189-203)

### the bank's capital (0.7.8) (lines 204-220)

### the budget (lines 221-236)

### housing (lines 237-246)

### school and care (lines 247-252)

### outside the families (lines 253-273)

### THE CENTRAL BANK, 0.7.0. The Money page's year and the year book's: (lines 274-287)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 288-295)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 296-305)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 306-321)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 322-343)

| line | len | member | says |
|---:|---:|---|---|
| 339 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 342 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 344-347)

### the market (lines 348-371)

| line | len | member | says |
|---:|---:|---|---|
| 369 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 370 | 1 | `public static String valueKey(String company)` |  |

### the sectors (0.7.4) (lines 372-396)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 1 | `public static String netIncomeKey(String sector)` | The series name the screens ask for, per sector: the month's net income after tax. |
| 395 | 1 | `public static String workersKey(String sector)` | ...and its posts filled. |

### GDP in layers (0.7.6) (lines 397-413)

### the city's fund (0.7.39) (lines 414-429)

### RECORDING (lines 430-851)

| line | len | member | says |
|---:|---:|---|---|
| 445 | 205 | `public void recordMonth(Game game)` | One month, read off the city itself. |
| 659 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 666 | 1 | `private static double round2(double v)` |  |
| 667 | 1 | `private static double round4(double v)` |  |
| 680 | 139 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 821 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 838 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 847 | 4 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |

### READING (lines 852-888)

| line | len | member | says |
|---:|---:|---|---|
| 857 | 1 | `public int months()` | How many months the city has lived. |
| 859 | 1 | `public List<Integer> getMonth()` |  |
| 874 | 14 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |

### a series' record, counted (0.7.9) (lines 889-1225)

| line | len | member | says |
|---:|---:|---|---|
| 899 | 8 | `public int monthsUnder(String series, String line)` | Months in which both series were recorded and the first stood under the second: the bank's months under its capital target (bankCapitalRatio against bankCapitalTarget). |
| 909 | 5 | `public int monthsUnder(String series, double level)` | ...and under a fixed level: its months under the city's minimum. |
| 916 | 5 | `public int monthsRecorded(String series)` | Months a series was recorded in. |
| 923 | 5 | `public double total(String series)` | A flow added up over the months it was recorded. |
| 936 | 7 | `public double changeOver(String series, int months)` | How far a series has moved over the last `months` months, as a share of where it stood then: the last recorded value over the one `months` entries before it, less one (0.7.35: the Trade tab's rate this month and over ... |
| 945 | 6 | `public double recentTotal(String series, int months)` | A flow added up over the last `months` months only, those recorded among them (0.7.35: the Trade tab's year of exports and imports). |
| 957 | 14 | `public double worstYear(String series)` | A flow's worst year: the largest sum of any twelve months in a row since it was first recorded - over fewer than twelve, what there is. |
| 978 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 995 | 151 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 1148 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 1149 | 1 | `public List<Double> getGdp()` |  |
| 1150 | 1 | `public List<Double> getDebt()` |  |
| 1151 | 1 | `public List<Double> getInterestRate()` |  |
| 1152 | 1 | `public List<Integer> getJobs()` |  |
| 1153 | 1 | `public List<Integer> getWorkforce()` |  |
| 1154 | 1 | `public List<Integer> getOutOfWork()` |  |
| 1155 | 1 | `public List<Integer> getPopulation()` |  |
| 1168 | 45 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 1215 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |

