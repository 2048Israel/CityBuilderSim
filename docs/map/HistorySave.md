# HistorySave.java - 908 lines · 27 methods · 0 constants · model

`ham/citybuildersim/HistorySave.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (7), [Crime](Crime.md) (4), [FamilyStructure](FamilyStructure.md) (3), [Equity](Equity.md) (3), [GameFiles](GameFiles.md) (2), [Game](Game.md) (1), [EconomyManager](EconomyManager.md) (1), [NationalAccounts](NationalAccounts.md) (1), [PopulationManager](PopulationManager.md) (1), [PopulationCohorts](PopulationCohorts.md) (1), [Migration](Migration.md) (1), [LabourMarket](LabourMarket.md) (1), [JobType](JobType.md) (1), [WageBand](WageBand.md) (1), [Education](Education.md) (1), [Good](Good.md) (1), [ForeignAccounts](ForeignAccounts.md) (1), [Bank](Bank.md) (1), [Unemployment](Unemployment.md) (1), [FamilyModel](FamilyModel.md) (1), [Sickness](Sickness.md) (1), [Exchange](Exchange.md) (1)

**Used by (11):** [BankScreen](BankScreen.md), [DeathRecordCheck](DeathRecordCheck.md), [EconomyManager](EconomyManager.md), [Game](Game.md), [HistoryCheck](HistoryCheck.md), [HistoryScreen](HistoryScreen.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [YearBook](YearBook.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 44 | · THE AXIS |
| 53 | · money |
| 63 | · people |
| 104 | · what throttles |
| 111 | · prices |
| 117 | · the edge |
| 137 | · money and credit |
| 151 | · what the bank could carry, and how hard |
| 171 | · the budget |
| 187 | · housing |
| 197 | · school and care |
| 203 | · outside the families |
| 224 | · THE LONG SICK, 2026-09-11. How many have been sick more than two |
| 232 | · THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many |
| 242 | · WHO DIED, 2026-09-11. The month's dead by age band, and how many of |
| 258 | · CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a |
| 280 | · what runs out |
| 284 | · the market |
| 307 | RECORDING |
| 374 | · · the edge |
| 383 | · · money and credit |
| 403 | · · the budget |
| 413 | · · housing |
| 418 | · · school and care |
| 424 | · · outside the families |
| 469 | · · what runs out |
| 473 | · · the market |
| 662 | READING |

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
| 112 | `private List<Double> landPrice` |  |
| 113 | `private List<Double> foodPrice` |  |
| 114 | `private List<Double> materialsPrice` |  |
| 115 | `private List<Double> orePrice` |  |
| 130 | `private List<Double> fxRate` |  |
| 131 | `private List<Double> reservesUsd` |  |
| 132 | `private List<Double> foreignDebtUsd` |  |
| 133 | `private List<Double> currentAccount` |  |
| 134 | `private List<Double> exportsAbroad` |  |
| 135 | `private List<Double> importsAbroad` |  |
| 143 | `private List<Double> priceIndex` |  |
| 144 | `private List<Double> businessDebt` |  |
| 145 | `private List<Double> bankPremium` |  |
| 146 | `private List<Double> bankDeposits` |  |
| 147 | `private List<Double> bankLent` |  |
| 148 | `private List<Double> bankEquity` |  |
| 149 | `private List<Double> bankWriteOffs` |  |
| 165 | `private List<Double> bankCapacity` |  |
| 166 | `private List<Double> bankStrain` |  |
| 167 | `private List<Double> bankProfit` |  |
| 168 | `private List<Double> bankBranches` |  |
| 169 | `private List<Double> householdSavings` |  |
| 178 | `private List<Double> taxWage` |  |
| 179 | `private List<Double> taxProperty` |  |
| 180 | `private List<Double> taxSales` |  |
| 181 | `private List<Double> taxBusiness` |  |
| 182 | `private List<Double> taxIndustrial` |  |
| 183 | `private List<Double> contributions` |  |
| 184 | `private List<Double> pensionBill` |  |
| 185 | `private List<Double> healthBill` |  |
| 193 | `private List<Double> rentPrice` |  |
| 194 | `private List<Integer> homes` |  |
| 195 | `private List<Double> households` |  |
| 198 | `private List<Double> students` |  |
| 199 | `private List<Double> graduates` |  |
| 200 | `private List<Double> licences` |  |
| 201 | `private List<Double> unburied` |  |
| 210 | `private List<Double> outOfWorkOnEi` |  |
| 211 | `private List<Double> outOfWorkOffEi` |  |
| 212 | `private List<Double> unhoused` |  |
| 213 | `private List<Double> orphans` |  |
| 214 | `private List<Double> evicted` |  |
| 215 | `private List<Double> eiPaid` |  |
| 216 | `private List<Double> eiPremiums` |  |
| 218 | `private List<Double> healthPremiums` | The health premium collected, a month at a time - the EI premium's shape (2026-09-19). |
| 219 | `private List<Double> studentGrants` |  |
| 220 | `private List<Double> studentLoansOwed` |  |
| 222 | `private List<Double> studentLoanInterest` | Interest the graduates paid on their student loans, a month at a time - the premiums' shape (2026-09-21). |
| 228 | `private List<Double> sickPastTwoMonths` |  |
| 229 | `private List<Double> diedOfIllness` |  |
| 230 | `private List<Double> sickRecovery` |  |
| 240 | `private Map<String, List<Double>> householdsByShape` |  |
| 248 | `private List<Double> deathsBabies` |  |
| 249 | `private List<Double> deathsChildren` |  |
| 250 | `private List<Double> deathsTeens` |  |
| 251 | `private List<Double> deathsAdults` |  |
| 252 | `private List<Double> deathsSeniors` |  |
| 254 | `private List<Double> deathsElders` | The over-85s, since the band was split on 2026-09-15. |
| 255 | `private List<Double> deathsOrphans` |  |
| 256 | `private List<Double> deathsUnhoused` |  |
| 265 | `private List<Double> crimeRate` |  |
| 266 | `private List<Double> policeCoverage` |  |
| 267 | `private List<Double> prisoners` |  |
| 268 | `private List<Double> caughtNotHeld` |  |
| 269 | `private List<Double> stolen` |  |
| 270 | `private List<Double> deathsKilled` |  |
| 271 | `private List<Double> safetyBill` |  |
| 272 | `private Map<String, List<Double>> crimeByCause` |  |
| 281 | `private List<Integer> constructionCapacity` |  |
| 282 | `private List<Double> landUse` |  |
| 300 | `private Map<String, List<Double>> sharePrice` |  |
| 301 | `private Map<String, List<Double>> shareValue` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 42 | 867 | **type** `public class HistorySave` | Every month the city has ever lived, one number at a time. |

### THE AXIS (lines 44-52)

### money (lines 53-62)

### people (lines 63-103)

### what throttles (lines 104-110)

### prices (lines 111-116)

### the edge (lines 117-136)

### money and credit (lines 137-150)

### what the bank could carry, and how hard (lines 151-170)

### the budget (lines 171-186)

### housing (lines 187-196)

### school and care (lines 197-202)

### outside the families (lines 203-223)

### THE LONG SICK, 2026-09-11. How many have been sick more than two (lines 224-231)

### THE HOUSEHOLDS, BY SHAPE, 2026-09-11. Jerus: a record of how many (lines 232-241)

### WHO DIED, 2026-09-11. The month's dead by age band, and how many of (lines 242-257)

### CRIME, THE POLICE AND THE PRISONS, 2026-09-11 (night). The rate a (lines 258-279)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 1 | `public static String crimeKey(Crime.Cause cause)` | The series name the screens ask for, per reason for crime. |
| 278 | 1 | `public static String householdKey(FamilyStructure shape)` | The series name the screens ask for, per household shape. |

### what runs out (lines 280-283)

### the market (lines 284-306)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 1 | `public static String priceKey(String company)` | The series name the screens ask for, per company. |
| 305 | 1 | `public static String valueKey(String company)` |  |

### RECORDING (lines 307-661)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 163 | `public void recordMonth(Game game)` | One month, read off the city itself. |
| 494 | 6 | `private static double sum(double[] values)` | A whole array in one figure. |
| 501 | 1 | `private static double round2(double v)` |  |
| 502 | 1 | `private static double round4(double v)` |  |
| 515 | 114 | `public void restoreFrom(HistorySave loaded)` | Takes over another history wholesale - the load path. |
| 631 | 6 | `private static Map<String, List<Double>> copyMap(Map<String, List<Double>> from)` | A map of series, copied list by list, and never null - see copy(). |
| 648 | 3 | `private static<T> List<T> copy(List<T> from)` | A copy, and never null. |
| 657 | 4 | `public GameFiles.Result saveHistory(GameFiles files, int slot)` | The graph history. |

### READING (lines 662-908)

| line | len | member | says |
|---:|---:|---|---|
| 667 | 1 | `public int months()` | How many months the city has lived. |
| 669 | 1 | `public List<Integer> getMonth()` |  |
| 684 | 14 | `public double[] aligned(String name)` | A series as doubles, padded at the FRONT to the full month axis. |
| 705 | 15 | `public static double[] runningTotal(double[] monthly)` | A monthly series summed from its first recorded month, for the running totals of the dead. |
| 722 | 122 | `public Map<String, List<? extends Number>> seriesByName()` | Every stored series, by the name the screen asks for. |
| 846 | 1 | `public List<Double> getCash()` | The originals, still here because other code and the harnesses read them. |
| 847 | 1 | `public List<Double> getGdp()` |  |
| 848 | 1 | `public List<Double> getDebt()` |  |
| 849 | 1 | `public List<Double> getInterestRate()` |  |
| 850 | 1 | `public List<Integer> getJobs()` |  |
| 851 | 1 | `public List<Integer> getWorkforce()` |  |
| 852 | 1 | `public List<Integer> getOutOfWork()` |  |
| 853 | 1 | `public List<Integer> getPopulation()` |  |
| 866 | 30 | `public void redenominate(double scale)` | Redraws the city's whole history in the new unit. |
| 898 | 9 | `private static void scaleAll(double scale, List<Double>...series)` |  |

