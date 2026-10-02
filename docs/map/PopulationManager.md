# PopulationManager.java - 1,242 lines · 61 methods · 2 constants · model

`ham/citybuildersim/PopulationManager.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The working population: the workforce by skill band, the city's posts and
> their wages by job type, and who fills which post (fillByBand()).

**Uses:** [JobType](JobType.md) (29), [WageBand](WageBand.md) (23), [LabourMarket](LabourMarket.md) (8), [PayTier](PayTier.md) (3), [EducationType](EducationType.md) (2)

**Used by (27):** [BankCheck](BankCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CityNeeds](CityNeeds.md), [ConstructionControlCheck](ConstructionControlCheck.md), [CrimeCheck](CrimeCheck.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [Migration](Migration.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 26 | WHO THE WORKFORCE ACTUALLY IS |
| 414 | WORKERS TAKE THE BEST-PAID JOB THEY QUALIFY FOR (0.7.18) |
| 1130 | · · POPULATION OVERVIEW |
| 1138 | · · LABOR MARKET SUMMARY |
| 1159 | · · JOB DISTRIBUTION TABLE |
| 1187 | · · LABOR MARKET STATUS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 491 | `PopulationManager.BAND_BASE` | `bandBases()` | Each band's base as a multiple of the unskilled floor: what its ungated posts pay at the going rate (LabourMarket.ratioOf()). |
| 1213 | `PopulationManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `private int population` |  |
| 16 | `JobType[] jobTypes` | dont use this one for anything other than create different jobs |
| 18 | `private int[] jobs` |  |
| 19 | `private double[] jobWage` |  |
| 20 | `private double[] totalWagePerType` |  |
| 21 | `private int totalJobs` |  |
| 23 | `private double adultPercent` | temporary |
| 24 | `private int workforce` |  |
| 61 | `private final double[] skilledHeads` | HEADS, and only the skilled ones. |
| 76 | `private final double[] licensed` | People licensed to hold a GATED job, by job type. |
| 191 | `private java.util.function.Supplier<double[]> overtimeWages` | THE OVERTIME ON A RUSHED SITE IS WAGES (0.7.22; ConstructionControl, B. |
| 513 | `public final double[] placed` | Workers holding each band's posts. |
| 515 | `public final double[] supply` | Each band's supply: its posts over this are its tightness, the figure LabourMarket prices. |
| 517 | `public final int[] licensedIn` | Licence holders in each gated job type's posts, by JobType ordinal: whole people, booked first. |
| 519 | `public final int[] market` | The highest band of the market each band was filled in: its own when alone, the top of the tie when joined. |
| 787 | `private double imprisoned` | Adults serving a sentence, set from Crime each month. |
| 792 | `private double[] studying` | Adults studying full time, by band, set from Education each month. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 13 | 1230 | **type** `public class PopulationManager` | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). |

### WHO THE WORKFORCE ACTUALLY IS (lines 26-413)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 4 | `public int applyPopulation(int newPopulation, double adultsAlreadyHere)` | Takes the population the demographics arrived at, and works out who can work this month. |
| 124 | 9 | `public void updateJobs(int[] newJobs)` |  |
| 137 | 3 | `public int getTotalJobs()` | getters |
| 141 | 3 | `public int getPopulation()` |  |
| 145 | 3 | `public double[] getTotalWagePerType()` |  |
| 156 | 8 | `public int getJobsFilled()` | Positions actually staffed, across every tier. |
| 165 | 14 | `public double getTotalWage()` |  |
| 193 | 3 | `public void setOvertimeWages(java.util.function.Supplier<double[]> overtimeWages)` |  |
| 197 | 3 | `public double[] getWagesPerType()` |  |
| 213 | 13 | `public double[] getStaffedWagePerType()` | The wage bill per job type with the fill rate already applied. |
| 239 | 8 | `public double[] getStaffedWagePerTier()` | The same staffed wage bill, collapsed onto the six pay tiers. |
| 261 | 5 | `public void setWagesPerType()` | Wages, read from PayTier rather than typed out here. |
| 276 | 5 | `public void takeWagesFrom(LabourMarket market)` | Takes this month's wages from the market instead of the constants. |
| 281 | 3 | `public void setPopulation(int population)` |  |
| 302 | 3 | `public void recomputeWorkforce()` | Recomputes workforce from the current population, without touching population itself. |
| 324 | 3 | `public void restoreWorkforce(int workforce)` | Puts back the workforce the month was actually worked by. |
| 328 | 3 | `public int getWorkforceForSave()` |  |
| 332 | 5 | `public void UpdateTotalWagePerType()` |  |
| 359 | 54 | `public int[] getJobVacancy()` | Positions nobody qualified is available to fill. |

### WORKERS TAKE THE BEST-PAID JOB THEY QUALIFY FOR (0.7.18) (lines 414-1242)

| line | len | member | says |
|---:|---:|---|---|
| 493 | 11 | `private static double[] bandBases()` |  |
| 511 | 23 | **type** `public static final class BandFill` | One month's workers against one count of posts, by band (0.7.18) - the single definition of who can fill a post. |
| 521 | 6 | `BandFill(int bands, int types)` _(in PopulationManager.BandFill)_ |  |
| 529 | 4 | `public boolean joinedAbove(WageBand band)` _(in PopulationManager.BandFill)_ | Whether this band was filled as one market with the band above it. |
| 538 | 3 | `public BandFill fillByBand()` | The fill against this month's posts offered. |
| 547 | 79 | `public BandFill fillByBand(double[] posts)` | The fill against a given count of staffable posts per band: this month's (staffablePostsByBand()), or a planner's, which also counts the posts no employer offers this month (Sector.staffing()). |
| 628 | 6 | `private static boolean hiring(BandFill f, double[] posts, int hi, int lo)` | Whether any band in a market has posts it has not filled. |
| 642 | 58 | `private static double fillMarket(BandFill f, int hi, int lo, double carriedIn, double[] posts, double[] own, double[] gated, do...` | Fills bands hi down to lo as one market: sets what each holds, its supply and the market it is in, puts the market's wage in wage[k] (as a multiple of the unskilled base; minus infinity for a market with no posts, whi... |
| 702 | 9 | `private static double heldAt(double lambda, int hi, int lo, double[] posts, double[] gated)` | The workers bands hi down to lo would hold if every one of their posts paid lambda. |
| 719 | 6 | `public static boolean isGated(JobType job)` | True for a job no amount of general education qualifies anybody for. |
| 735 | 50 | `public double[] workforceByBand()` | The workforce split by skill. |
| 788 | 1 | `public void setImprisoned(double adults)` |  |
| 789 | 1 | `public double getImprisoned()` |  |
| 793 | 1 | `public void setStudying(double[] byBand)` |  |
| 794 | 5 | `public double getStudyingTotal()` |  |
| 801 | 7 | `public double[] postsByBand()` | Posts that exist at each skill level. |
| 830 | 10 | `public double[] staffablePostsByBand()` | Posts a band's own members could actually be put into. |
| 859 | 3 | `public double[] supplyByBand()` | Workers actually available to each band: what the labour market prices against, a band's staffable posts over this being its tightness. |
| 864 | 3 | `public double[] supplyByBand(double[] posts)` | The same fill against a given count of posts per band - a planner's, which counts posts no employer offers this month (0.7.17; Sector.staffing()). |
| 881 | 4 | `public double surplusInBand(WageBand band)` | How many more workers of this band the city has than posts for them. |
| 887 | 9 | `public double[] getBandShare()` | The mix as shares, for anything that wants proportions. |
| 898 | 1 | `public double[] getSkilledHeads()` | The carried skilled counts, for the save. |
| 900 | 3 | `public double getLicensed(JobType job)` |  |
| 904 | 1 | `public double[] getLicensedHeads()` |  |
| 916 | 11 | `public double spareLicences(JobType job)` | Licence holders the city is not already working. |
| 928 | 4 | `public void restoreLicensed(double[] saved)` |  |
| 940 | 6 | `public void addLicences(double[] newly)` | New graduates of the professional schools. |
| 966 | 6 | `public void retireSkilled(double leaveRate)` | Death and retirement, which take the skilled along with everybody else. |
| 982 | 15 | `public void trimLicencesToBand()` | Keeps the licensed counts inside the band that contains them. |
| 998 | 5 | `public void restoreSkilledHeads(double[] saved)` |  |
| 1014 | 6 | `public void applyBandFlow(double[] flow)` | This month's schooling, as a net movement between bands. |
| 1029 | 7 | `public void applySkilledFlows(double[] arrivals, double[] departures)` | Who moved in and who moved out, by skill. |
| 1046 | 12 | `public void inferBandShareFromJobs()` | Reads a plausible skill mix off the jobs the city is currently staffing. |
| 1059 | 13 | `public double[] getJobFillRate()` |  |
| 1073 | 3 | `public int getWorkforce()` |  |
| 1092 | 3 | `public int getUnemployed()` | Adults who want work and have none. |
| 1107 | 4 | `public double getLabourForce()` | Adults who could take a post this month: the workforce less the full-time students. |
| 1113 | 4 | `public double getUnemploymentRate()` | Unemployed as a share of everyone who could work, 0-1. |
| 1118 | 3 | `public int[] getJobs()` |  |
| 1122 | 3 | `public double getAdultPercent()` |  |
| 1126 | 79 | `public void printPopulationInfo()` |  |
| 1206 | 6 | `public void resetPopulationManager()` |  |
| 1215 | 4 | `static { ... }` |  |
| 1237 | 4 | `public void redenominate(double scale)` | The wage table, in the new unit. |

