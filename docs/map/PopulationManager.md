# PopulationManager.java - 1,214 lines · 60 methods · 2 constants · model

`ham/citybuildersim/PopulationManager.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

> The working population: the workforce by skill band, the city's posts and
> their wages by job type, and who fills which post (fillByBand()).

**Uses:** [JobType](JobType.md) (29), [WageBand](WageBand.md) (23), [LabourMarket](LabourMarket.md) (8), [PayTier](PayTier.md) (3), [EducationType](EducationType.md) (2)

**Used by (24):** [BankCheck](BankCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [CrimeCheck](CrimeCheck.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [Migration](Migration.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 26 | WHO THE WORKFORCE ACTUALLY IS |
| 386 | WORKERS TAKE THE BEST-PAID JOB THEY QUALIFY FOR (0.7.18) |
| 1102 | · · POPULATION OVERVIEW |
| 1110 | · · LABOR MARKET SUMMARY |
| 1131 | · · JOB DISTRIBUTION TABLE |
| 1159 | · · LABOR MARKET STATUS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 463 | `PopulationManager.BAND_BASE` | `bandBases()` | Each band's base as a multiple of the unskilled floor: what its ungated posts pay at the going rate (LabourMarket.ratioOf()). |
| 1185 | `PopulationManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 485 | `public final double[] placed` | Workers holding each band's posts. |
| 487 | `public final double[] supply` | Each band's supply: its posts over this are its tightness, the figure LabourMarket prices. |
| 489 | `public final int[] licensedIn` | Licence holders in each gated job type's posts, by JobType ordinal: whole people, booked first. |
| 491 | `public final int[] market` | The highest band of the market each band was filled in: its own when alone, the top of the tie when joined. |
| 759 | `private double imprisoned` | Adults serving a sentence, set from Crime each month. |
| 764 | `private double[] studying` | Adults studying full time, by band, set from Education each month. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 13 | 1202 | **type** `public class PopulationManager` | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). |

### WHO THE WORKFORCE ACTUALLY IS (lines 26-385)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 4 | `public int applyPopulation(int newPopulation, double adultsAlreadyHere)` | Takes the population the demographics arrived at, and works out who can work this month. |
| 124 | 9 | `public void updateJobs(int[] newJobs)` |  |
| 137 | 3 | `public int getTotalJobs()` | getters |
| 141 | 3 | `public int getPopulation()` |  |
| 145 | 3 | `public double[] getTotalWagePerType()` |  |
| 156 | 8 | `public int getJobsFilled()` | Positions actually staffed, across every tier. |
| 165 | 8 | `public double getTotalWage()` |  |
| 174 | 3 | `public double[] getWagesPerType()` |  |
| 190 | 8 | `public double[] getStaffedWagePerType()` | The wage bill per job type with the fill rate already applied. |
| 211 | 8 | `public double[] getStaffedWagePerTier()` | The same staffed wage bill, collapsed onto the six pay tiers. |
| 233 | 5 | `public void setWagesPerType()` | Wages, read from PayTier rather than typed out here. |
| 248 | 5 | `public void takeWagesFrom(LabourMarket market)` | Takes this month's wages from the market instead of the constants. |
| 253 | 3 | `public void setPopulation(int population)` |  |
| 274 | 3 | `public void recomputeWorkforce()` | Recomputes workforce from the current population, without touching population itself. |
| 296 | 3 | `public void restoreWorkforce(int workforce)` | Puts back the workforce the month was actually worked by. |
| 300 | 3 | `public int getWorkforceForSave()` |  |
| 304 | 5 | `public void UpdateTotalWagePerType()` |  |
| 331 | 54 | `public int[] getJobVacancy()` | Positions nobody qualified is available to fill. |

### WORKERS TAKE THE BEST-PAID JOB THEY QUALIFY FOR (0.7.18) (lines 386-1214)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 11 | `private static double[] bandBases()` |  |
| 483 | 23 | **type** `public static final class BandFill` | One month's workers against one count of posts, by band (0.7.18) - the single definition of who can fill a post. |
| 493 | 6 | `BandFill(int bands, int types)` _(in PopulationManager.BandFill)_ |  |
| 501 | 4 | `public boolean joinedAbove(WageBand band)` _(in PopulationManager.BandFill)_ | Whether this band was filled as one market with the band above it. |
| 510 | 3 | `public BandFill fillByBand()` | The fill against this month's posts offered. |
| 519 | 79 | `public BandFill fillByBand(double[] posts)` | The fill against a given count of staffable posts per band: this month's (staffablePostsByBand()), or a planner's, which also counts the posts no employer offers this month (Sector.staffing()). |
| 600 | 6 | `private static boolean hiring(BandFill f, double[] posts, int hi, int lo)` | Whether any band in a market has posts it has not filled. |
| 614 | 58 | `private static double fillMarket(BandFill f, int hi, int lo, double carriedIn, double[] posts, double[] own, double[] gated, do...` | Fills bands hi down to lo as one market: sets what each holds, its supply and the market it is in, puts the market's wage in wage[k] (as a multiple of the unskilled base; minus infinity for a market with no posts, whi... |
| 674 | 9 | `private static double heldAt(double lambda, int hi, int lo, double[] posts, double[] gated)` | The workers bands hi down to lo would hold if every one of their posts paid lambda. |
| 691 | 6 | `public static boolean isGated(JobType job)` | True for a job no amount of general education qualifies anybody for. |
| 707 | 50 | `public double[] workforceByBand()` | The workforce split by skill. |
| 760 | 1 | `public void setImprisoned(double adults)` |  |
| 761 | 1 | `public double getImprisoned()` |  |
| 765 | 1 | `public void setStudying(double[] byBand)` |  |
| 766 | 5 | `public double getStudyingTotal()` |  |
| 773 | 7 | `public double[] postsByBand()` | Posts that exist at each skill level. |
| 802 | 10 | `public double[] staffablePostsByBand()` | Posts a band's own members could actually be put into. |
| 831 | 3 | `public double[] supplyByBand()` | Workers actually available to each band: what the labour market prices against, a band's staffable posts over this being its tightness. |
| 836 | 3 | `public double[] supplyByBand(double[] posts)` | The same fill against a given count of posts per band - a planner's, which counts posts no employer offers this month (0.7.17; Sector.staffing()). |
| 853 | 4 | `public double surplusInBand(WageBand band)` | How many more workers of this band the city has than posts for them. |
| 859 | 9 | `public double[] getBandShare()` | The mix as shares, for anything that wants proportions. |
| 870 | 1 | `public double[] getSkilledHeads()` | The carried skilled counts, for the save. |
| 872 | 3 | `public double getLicensed(JobType job)` |  |
| 876 | 1 | `public double[] getLicensedHeads()` |  |
| 888 | 11 | `public double spareLicences(JobType job)` | Licence holders the city is not already working. |
| 900 | 4 | `public void restoreLicensed(double[] saved)` |  |
| 912 | 6 | `public void addLicences(double[] newly)` | New graduates of the professional schools. |
| 938 | 6 | `public void retireSkilled(double leaveRate)` | Death and retirement, which take the skilled along with everybody else. |
| 954 | 15 | `public void trimLicencesToBand()` | Keeps the licensed counts inside the band that contains them. |
| 970 | 5 | `public void restoreSkilledHeads(double[] saved)` |  |
| 986 | 6 | `public void applyBandFlow(double[] flow)` | This month's schooling, as a net movement between bands. |
| 1001 | 7 | `public void applySkilledFlows(double[] arrivals, double[] departures)` | Who moved in and who moved out, by skill. |
| 1018 | 12 | `public void inferBandShareFromJobs()` | Reads a plausible skill mix off the jobs the city is currently staffing. |
| 1031 | 13 | `public double[] getJobFillRate()` |  |
| 1045 | 3 | `public int getWorkforce()` |  |
| 1064 | 3 | `public int getUnemployed()` | Adults who want work and have none. |
| 1079 | 4 | `public double getLabourForce()` | Adults who could take a post this month: the workforce less the full-time students. |
| 1085 | 4 | `public double getUnemploymentRate()` | Unemployed as a share of everyone who could work, 0-1. |
| 1090 | 3 | `public int[] getJobs()` |  |
| 1094 | 3 | `public double getAdultPercent()` |  |
| 1098 | 79 | `public void printPopulationInfo()` |  |
| 1178 | 6 | `public void resetPopulationManager()` |  |
| 1187 | 4 | `static { ... }` |  |
| 1209 | 4 | `public void redenominate(double scale)` | The wage table, in the new unit. |

