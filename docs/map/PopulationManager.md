# PopulationManager.java - 938 lines · 51 methods · 1 constants · model

`ham/citybuildersim/PopulationManager.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [JobType](JobType.md) (25), [WageBand](WageBand.md) (18), [PayTier](PayTier.md) (3), [EducationType](EducationType.md) (2), [LabourMarket](LabourMarket.md) (1)

**Used by (22):** [BusinessServicesCheck](BusinessServicesCheck.md), [CrimeCheck](CrimeCheck.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [Migration](Migration.md), [NewGameCheck](NewGameCheck.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [SummaryScreen](SummaryScreen.md), [YearBookCheck](YearBookCheck.md)

## Sections

| line | section |
|---:|---|
| 24 | WHO THE WORKFORCE ACTUALLY IS |
| 826 | · · POPULATION OVERVIEW |
| 834 | · · LABOR MARKET SUMMARY |
| 855 | · · JOB DISTRIBUTION TABLE |
| 883 | · · LABOR MARKET STATUS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 909 | `PopulationManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 12 | `private int population` |  |
| 14 | `JobType[] jobTypes` | dont use this one for anything other than create different jobs |
| 16 | `private int[] jobs` |  |
| 17 | `private double[] jobWage` |  |
| 18 | `private double[] totalWagePerType` |  |
| 19 | `private int totalJobs` |  |
| 21 | `private double adultPercent` | temporary |
| 22 | `private int workforce` |  |
| 59 | `private final double[] skilledHeads` | HEADS, and only the skilled ones. |
| 74 | `private final double[] licensed` | People licensed to hold a GATED job, by job type. |
| 486 | `private double imprisoned` | Adults serving a sentence, set from Crime each month. |
| 491 | `private double[] studying` | Adults studying full time, by band, set from Education each month. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 11 | 928 | **type** `public class PopulationManager` |  |

### WHO THE WORKFORCE ACTUALLY IS (lines 24-938)

| line | len | member | says |
|---:|---:|---|---|
| 117 | 4 | `public int applyPopulation(int newPopulation, double adultsAlreadyHere)` | Takes the population the demographics arrived at, and works out who can work this month. |
| 122 | 9 | `public void updateJobs(int[] newJobs)` |  |
| 135 | 3 | `public int getTotalJobs()` | getters |
| 139 | 3 | `public int getPopulation()` |  |
| 143 | 3 | `public double[] getTotalWagePerType()` |  |
| 154 | 8 | `public int getJobsFilled()` | Positions actually staffed, across every tier. |
| 163 | 8 | `public double getTotalWage()` |  |
| 172 | 3 | `public double[] getWagesPerType()` |  |
| 188 | 8 | `public double[] getStaffedWagePerType()` | The wage bill per job type with the fill rate already applied. |
| 209 | 8 | `public double[] getStaffedWagePerTier()` | The same staffed wage bill, collapsed onto the six pay tiers. |
| 231 | 5 | `public void setWagesPerType()` | Wages, read from PayTier rather than typed out here. |
| 246 | 5 | `public void takeWagesFrom(LabourMarket market)` | Takes this month's wages from the market instead of the constants. |
| 251 | 3 | `public void setPopulation(int population)` |  |
| 272 | 3 | `public void recomputeWorkforce()` | Recomputes workforce from the current population, without touching population itself. |
| 294 | 3 | `public void restoreWorkforce(int workforce)` | Puts back the workforce the month was actually worked by. |
| 298 | 3 | `public int getWorkforceForSave()` |  |
| 302 | 5 | `public void UpdateTotalWagePerType()` |  |
| 325 | 85 | `public int[] getJobVacancy()` | Positions nobody qualified is available to fill. |
| 418 | 6 | `public static boolean isGated(JobType job)` | True for a job no amount of general education qualifies anybody for. |
| 434 | 50 | `public double[] workforceByBand()` | The workforce split by skill. |
| 487 | 1 | `public void setImprisoned(double adults)` |  |
| 488 | 1 | `public double getImprisoned()` |  |
| 492 | 1 | `public void setStudying(double[] byBand)` |  |
| 493 | 5 | `public double getStudyingTotal()` |  |
| 500 | 7 | `public double[] postsByBand()` | Posts that exist at each skill level. |
| 529 | 10 | `public double[] staffablePostsByBand()` | Posts a band's own members could actually be put into. |
| 551 | 12 | `public double[] supplyByBand()` | Workers actually available to each band, cascade included. |
| 577 | 4 | `public double surplusInBand(WageBand band)` | How many more workers of this band the city has than posts for them. |
| 583 | 9 | `public double[] getBandShare()` | The mix as shares, for anything that wants proportions. |
| 594 | 1 | `public double[] getSkilledHeads()` | The carried skilled counts, for the save. |
| 596 | 3 | `public double getLicensed(JobType job)` |  |
| 600 | 1 | `public double[] getLicensedHeads()` |  |
| 612 | 11 | `public double spareLicences(JobType job)` | Licence holders the city is not already working. |
| 624 | 4 | `public void restoreLicensed(double[] saved)` |  |
| 636 | 6 | `public void addLicences(double[] newly)` | New graduates of the professional schools. |
| 662 | 6 | `public void retireSkilled(double leaveRate)` | Death and retirement, which take the skilled along with everybody else. |
| 678 | 15 | `public void trimLicencesToBand()` | Keeps the licensed counts inside the band that contains them. |
| 694 | 5 | `public void restoreSkilledHeads(double[] saved)` |  |
| 718 | 6 | `public void applyBandFlow(double[] flow)` | This month's schooling, as a net movement between bands. |
| 725 | 7 | `public void applySkilledFlows(double[] arrivals, double[] departures)` |  |
| 742 | 12 | `public void inferBandShareFromJobs()` | Reads a plausible skill mix off the jobs the city is currently staffing. |
| 755 | 13 | `public double[] getJobFillRate()` |  |
| 769 | 3 | `public int getWorkforce()` |  |
| 788 | 3 | `public int getUnemployed()` | Adults who want work and have none. |
| 803 | 4 | `public double getLabourForce()` | Adults who could take a post this month: the workforce less the full-time students. |
| 809 | 4 | `public double getUnemploymentRate()` | Unemployed as a share of everyone who could work, 0-1. |
| 814 | 3 | `public int[] getJobs()` |  |
| 818 | 3 | `public double getAdultPercent()` |  |
| 822 | 79 | `public void printPopulationInfo()` |  |
| 902 | 6 | `public void resetPopulationManager()` |  |
| 911 | 4 | `static { ... }` |  |
| 933 | 4 | `public void redenominate(double scale)` | The wage table, in the new unit. |

