# PopulationManager.java - 1,298 lines · 68 methods · 1 constants · model

`ham/citybuildersim/PopulationManager.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

> The working population: the workforce by skill band, the city's posts and
> their wages by job type, and who fills which post (fillByBand()).

**Uses:** [JobType](JobType.md) (31), [WageBand](WageBand.md) (26), [LabourMarket](LabourMarket.md) (10), [WageLadder](WageLadder.md) (7), [PayTier](PayTier.md) (3), [EducationType](EducationType.md) (2)

**Used by (36):** [BankCheck](BankCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CityNeeds](CityNeeds.md), [ConstructionControlCheck](ConstructionControlCheck.md), [CrimeCheck](CrimeCheck.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [FloorView](FloorView.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [Migration](Migration.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [StaffingDrawCheck](StaffingDrawCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [WageLadderCheck](WageLadderCheck.md), [WantedPostsCheck](WantedPostsCheck.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md)

## Sections

| line | section |
|---:|---|
| 26 | WHO THE WORKFORCE ACTUALLY IS |
| 452 | WORKERS TAKE THE BEST-PAID JOB THEY QUALIFY FOR (0.7.18) |
| 1186 | · · POPULATION OVERVIEW |
| 1194 | · · LABOR MARKET SUMMARY |
| 1215 | · · JOB DISTRIBUTION TABLE |
| 1243 | · · LABOR MARKET STATUS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1269 | `PopulationManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 14 | `private long population` |  |
| 16 | `JobType[] jobTypes` | dont use this one for anything other than create different jobs |
| 18 | `private long[] jobs` |  |
| 19 | `private double[] jobWage` |  |
| 20 | `private double[] totalWagePerType` |  |
| 21 | `private long totalJobs` |  |
| 23 | `private double adultPercent` | temporary |
| 24 | `private long workforce` |  |
| 61 | `private final double[] skilledHeads` | HEADS, and only the skilled ones. |
| 76 | `private final double[] licensed` | People licensed to hold a GATED job, by job type. |
| 226 | `private java.util.function.Supplier<double[]> overtimeWages` | THE OVERTIME ON A RUSHED SITE IS WAGES (0.7.22; ConstructionControl, B. |
| 531 | `private double[] bandBase` | Each band's base as a multiple of the unskilled floor: what its ungated posts pay at the going rate (the ladder's ratio, LabourMarket.ratioOf() on today's). |
| 534 | `private WageLadder ladder` | The wage ladder the wages and the bands' bases are struck on (R0d). |
| 569 | `public final double[] placed` | Workers holding each band's posts. |
| 571 | `public final double[] supply` | Each band's supply: its posts over this are its tightness, the figure LabourMarket prices. |
| 573 | `public final long[] licensedIn` | Licence holders in each gated job type's posts, by JobType ordinal: whole people, booked first. |
| 575 | `public final int[] market` | The highest band of the market each band was filled in: its own when alone, the top of the tie when joined. |
| 843 | `private double imprisoned` | Adults serving a sentence, set from Crime each month. |
| 848 | `private double[] studying` | Adults studying full time, by band, set from Education each month. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 13 | 1286 | **type** `public class PopulationManager` | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). |

### WHO THE WORKFORCE ACTUALLY IS (lines 26-451)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 4 | `public long applyPopulation(long newPopulation, double adultsAlreadyHere)` | Takes the population the demographics arrived at, and works out who can work this month. |
| 124 | 9 | `public void updateJobs(long[] newJobs)` |  |
| 137 | 3 | `public long getTotalJobs()` | getters |
| 141 | 3 | `public long getPopulation()` |  |
| 145 | 3 | `public double[] getTotalWagePerType()` |  |
| 156 | 8 | `public long getJobsFilled()` | Positions actually staffed, across every tier. |
| 169 | 4 | `public double getAverageWage()` | The average wage (WG1): the wage bill over the posts filled, the Rankings' definition (YearBook.averageWage()); 0 with nobody employed. |
| 178 | 3 | `public double[] workersPaidTheFloor(LabourMarket m)` | The posts filled that the floor is what pays (WG1), by WageBand.ordinal(): the posts filled of each job type m.isPaidTheFloor(job), counted as getJobsFilled() counts them. |
| 183 | 3 | `public double[] workersNearTheFloor(LabourMarket m)` | ...and the posts filled that are paid a little over it (m.isNearTheFloor(job)), by WageBand.ordinal(). |
| 187 | 12 | `private double[] filledWhere(LabourMarket m, boolean paid)` |  |
| 200 | 14 | `public double getTotalWage()` |  |
| 228 | 3 | `public void setOvertimeWages(java.util.function.Supplier<double[]> overtimeWages)` |  |
| 232 | 3 | `public double[] getWagesPerType()` |  |
| 248 | 13 | `public double[] getStaffedWagePerType()` | The wage bill per job type with the fill rate already applied. |
| 274 | 8 | `public double[] getStaffedWagePerTier()` | The same staffed wage bill, collapsed onto the six pay tiers. |
| 299 | 5 | `public void setWagesPerType()` | Wages, read from PayTier rather than typed out here. |
| 314 | 5 | `public void takeWagesFrom(LabourMarket market)` | Takes this month's wages from the market instead of the constants. |
| 319 | 3 | `public void setPopulation(long population)` |  |
| 340 | 3 | `public void recomputeWorkforce()` | Recomputes workforce from the current population, without touching population itself. |
| 362 | 3 | `public void restoreWorkforce(long workforce)` | Puts back the workforce the month was actually worked by. |
| 366 | 3 | `public long getWorkforceForSave()` |  |
| 370 | 5 | `public void UpdateTotalWagePerType()` |  |
| 397 | 54 | `public long[] getJobVacancy()` | Positions nobody qualified is available to fill. |

### WORKERS TAKE THE BEST-PAID JOB THEY QUALIFY FOR (0.7.18) (lines 452-1298)

| line | len | member | says |
|---:|---:|---|---|
| 537 | 4 | `public void setLadder(WageLadder l)` | Puts the city's wages and the allocator's band bases on a ladder (R0d): today's by default, 1901's in a city founded the 1900 way. |
| 543 | 1 | `public WageLadder getLadder()` | The ladder this city's wages are struck on (R0d). |
| 546 | 11 | `static double[] bandBases(WageLadder l)` | Each band's lowest ungated ratio on a ladder: the base its posts are compared on. |
| 559 | 1 | `public double getBandBase(WageBand band)` | The allocator's base for a band: its lowest ungated ratio on this city's ladder (R0d; read by WageLadderCheck). |
| 567 | 23 | **type** `public static final class BandFill` | One month's workers against one count of posts, by band (0.7.18) - the single definition of who can fill a post. |
| 577 | 6 | `BandFill(int bands, int types)` _(in PopulationManager.BandFill)_ |  |
| 585 | 4 | `public boolean joinedAbove(WageBand band)` _(in PopulationManager.BandFill)_ | Whether this band was filled as one market with the band above it. |
| 594 | 3 | `public BandFill fillByBand()` | The fill against this month's posts offered. |
| 603 | 79 | `public BandFill fillByBand(double[] posts)` | The fill against a given count of staffable posts per band: this month's (staffablePostsByBand()), or a planner's, which also counts the posts no employer offers this month (Sector.staffing()). |
| 684 | 6 | `private static boolean hiring(BandFill f, double[] posts, int hi, int lo)` | Whether any band in a market has posts it has not filled. |
| 698 | 58 | `private double fillMarket(BandFill f, int hi, int lo, double carriedIn, double[] posts, double[] own, double[] gated, double[] ...` | Fills bands hi down to lo as one market: sets what each holds, its supply and the market it is in, puts the market's wage in wage[k] (as a multiple of the unskilled base; minus infinity for a market with no posts, whi... |
| 758 | 9 | `private double heldAt(double lambda, int hi, int lo, double[] posts, double[] gated)` | The workers bands hi down to lo would hold if every one of their posts paid lambda. |
| 775 | 6 | `public static boolean isGated(JobType job)` | True for a job no amount of general education qualifies anybody for. |
| 791 | 50 | `public double[] workforceByBand()` | The workforce split by skill. |
| 844 | 1 | `public void setImprisoned(double adults)` |  |
| 845 | 1 | `public double getImprisoned()` |  |
| 849 | 1 | `public void setStudying(double[] byBand)` |  |
| 850 | 5 | `public double getStudyingTotal()` |  |
| 857 | 7 | `public double[] postsByBand()` | Posts that exist at each skill level. |
| 886 | 10 | `public double[] staffablePostsByBand()` | Posts a band's own members could actually be put into. |
| 915 | 3 | `public double[] supplyByBand()` | Workers actually available to each band: what the labour market prices against, a band's staffable posts over this being its tightness. |
| 920 | 3 | `public double[] supplyByBand(double[] posts)` | The same fill against a given count of posts per band - a planner's, which counts posts no employer offers this month (0.7.17; Sector.staffing()). |
| 937 | 4 | `public double surplusInBand(WageBand band)` | How many more workers of this band the city has than posts for them. |
| 943 | 9 | `public double[] getBandShare()` | The mix as shares, for anything that wants proportions. |
| 954 | 1 | `public double[] getSkilledHeads()` | The carried skilled counts, for the save. |
| 956 | 3 | `public double getLicensed(JobType job)` |  |
| 960 | 1 | `public double[] getLicensedHeads()` |  |
| 972 | 11 | `public double spareLicences(JobType job)` | Licence holders the city is not already working. |
| 984 | 4 | `public void restoreLicensed(double[] saved)` |  |
| 996 | 6 | `public void addLicences(double[] newly)` | New graduates of the professional schools. |
| 1022 | 6 | `public void retireSkilled(double leaveRate)` | Death and retirement, which take the skilled along with everybody else. |
| 1038 | 15 | `public void trimLicencesToBand()` | Keeps the licensed counts inside the band that contains them. |
| 1054 | 5 | `public void restoreSkilledHeads(double[] saved)` |  |
| 1070 | 6 | `public void applyBandFlow(double[] flow)` | This month's schooling, as a net movement between bands. |
| 1085 | 7 | `public void applySkilledFlows(double[] arrivals, double[] departures)` | Who moved in and who moved out, by skill. |
| 1102 | 12 | `public void inferBandShareFromJobs()` | Reads a plausible skill mix off the jobs the city is currently staffing. |
| 1115 | 13 | `public double[] getJobFillRate()` |  |
| 1129 | 3 | `public long getWorkforce()` |  |
| 1148 | 3 | `public long getUnemployed()` | Adults who want work and have none. |
| 1163 | 4 | `public double getLabourForce()` | Adults who could take a post this month: the workforce less the full-time students. |
| 1169 | 4 | `public double getUnemploymentRate()` | Unemployed as a share of everyone who could work, 0-1. |
| 1174 | 3 | `public long[] getJobs()` |  |
| 1178 | 3 | `public double getAdultPercent()` |  |
| 1182 | 79 | `public void printPopulationInfo()` |  |
| 1262 | 6 | `public void resetPopulationManager()` |  |
| 1271 | 4 | `static { ... }` |  |
| 1293 | 4 | `public void redenominate(double scale)` | The wage table, in the new unit. |

