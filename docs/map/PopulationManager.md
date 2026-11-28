# PopulationManager.java - 1,400 lines · 75 methods · 1 constants · model

`ham/citybuildersim/PopulationManager.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The working population: the workforce by skill band, the city's posts and
> their wages by job type, and who fills which post (fillByBand()).

**Uses:** [JobType](JobType.md) (31), [WageBand](WageBand.md) (27), [LabourMarket](LabourMarket.md) (10), [WageLadder](WageLadder.md) (7), [PayTier](PayTier.md) (3), [WorksValue](WorksValue.md) (3), [EducationType](EducationType.md) (2), [Currency](Currency.md) (1)

**Used by (51):** [BankCheck](BankCheck.md), [BuildAdviceCheck](BuildAdviceCheck.md), [BusinessServicesCheck](BusinessServicesCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [ConstructionControlCheck](ConstructionControlCheck.md), [CrimeCheck](CrimeCheck.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FloorView](FloorView.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [InvestCheck](InvestCheck.md), [LabourCheck](LabourCheck.md), [LandlordPortCheck](LandlordPortCheck.md), [LongPlaytest](LongPlaytest.md), [ManufacturingCheck](ManufacturingCheck.md), [MaterialsCheck](MaterialsCheck.md), [Migration](Migration.md), [MoneyAudit](MoneyAudit.md), [NewGameCheck](NewGameCheck.md), [NewUnitCheck](NewUnitCheck.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCheck](PopulationCheck.md), [PrizeCheck](PrizeCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [SchoolingLadder](SchoolingLadder.md), [SchoolingLadderCheck](SchoolingLadderCheck.md), [SchoolingPageCheck](SchoolingPageCheck.md), [SchoolingProbe](SchoolingProbe.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [SimulationEngine](SimulationEngine.md), [StaffingDrawCheck](StaffingDrawCheck.md), [StaffingTieCheck](StaffingTieCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [Village1900Check](Village1900Check.md), [WageLadderCheck](WageLadderCheck.md), [WantedPostsCheck](WantedPostsCheck.md), [WorksValue](WorksValue.md), [YearBookCheck](YearBookCheck.md), [ZoningCheck](ZoningCheck.md)

## Sections

| line | section |
|---:|---|
| 26 | WHO THE WORKFORCE ACTUALLY IS |
| 495 | WORKERS TAKE THE BEST-PAID JOB THEY QUALIFY FOR (0.7.18) |
| 1138 | · THE APPRENTICES (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause and the fix) |
| 1287 | · · POPULATION OVERVIEW |
| 1295 | · · LABOR MARKET SUMMARY |
| 1316 | · · JOB DISTRIBUTION TABLE |
| 1344 | · · LABOR MARKET STATUS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1371 | `PopulationManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

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
| 232 | `private java.util.function.Supplier<double[]> overtimeWages` | THE OVERTIME ON A RUSHED SITE IS WAGES (0.7.22; ConstructionControl, B. |
| 574 | `private double[] bandBase` | Each band's base as a multiple of the unskilled floor: what its ungated posts pay at the going rate (the ladder's ratio, LabourMarket.ratioOf() on today's). |
| 577 | `private WageLadder ladder` | The wage ladder the wages and the bands' bases are struck on (R0d). |
| 612 | `public final double[] placed` | Workers holding each band's posts. |
| 614 | `public final double[] supply` | Each band's supply: its posts over this are its tightness, the figure LabourMarket prices. |
| 616 | `public final long[] licensedIn` | Licence holders in each gated job type's posts, by JobType ordinal: whole people, booked first. |
| 618 | `public final int[] market` | The highest band of the market each band was filled in: its own when alone, the top of the tie when joined. |
| 899 | `private double imprisoned` | Adults serving a sentence, set from Crime each month. |
| 904 | `private double owners` | Adults of the owner families (two a company, BO1), who take no job; set from the line each month. |
| 909 | `private double[] studying` | Adults studying full time, by band, set from Education each month. |
| 1151 | `public static boolean APPRENTICES_1900` | R1g's switch for the apprentices, on unless -Dr1g.apprentices=off: the controls' lever while the fix is Jerus's call, and Village1900Check's. |
| 1154 | `private double lastApprentices` | The young who learnt a trade at work last month (R1g): for the harnesses and the trace; not saved, the heads being. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 13 | 1388 | **type** `public class PopulationManager` | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). |

### WHO THE WORKFORCE ACTUALLY IS (lines 26-494)

| line | len | member | says |
|---:|---:|---|---|
| 119 | 4 | `public long applyPopulation(long newPopulation, double adultsAlreadyHere)` | Takes the population the demographics arrived at, and works out who can work this month. |
| 124 | 9 | `public void updateJobs(long[] newJobs)` |  |
| 137 | 3 | `public long getTotalJobs()` | getters |
| 141 | 3 | `public long getPopulation()` |  |
| 145 | 3 | `public double[] getTotalWagePerType()` |  |
| 150 | 4 | `public void restoreTotalWagePerType(double[] saved)` | The held wage bill, restored after the load has rebuilt posts and wages. |
| 162 | 8 | `public long getJobsFilled()` | Positions actually staffed, across every tier. |
| 175 | 4 | `public double getAverageWage()` | The average wage (WG1): the wage bill over the posts filled, the Rankings' definition (YearBook.averageWage()); 0 with nobody employed. |
| 184 | 3 | `public double[] workersPaidTheFloor(LabourMarket m)` | The posts filled that the floor is what pays (WG1), by WageBand.ordinal(): the posts filled of each job type m.isPaidTheFloor(job), counted as getJobsFilled() counts them. |
| 189 | 3 | `public double[] workersNearTheFloor(LabourMarket m)` | ...and the posts filled that are paid a little over it (m.isNearTheFloor(job)), by WageBand.ordinal(). |
| 193 | 12 | `private double[] filledWhere(LabourMarket m, boolean paid)` |  |
| 206 | 14 | `public double getTotalWage()` |  |
| 234 | 3 | `public void setOvertimeWages(java.util.function.Supplier<double[]> overtimeWages)` |  |
| 238 | 3 | `public double[] getWagesPerType()` |  |
| 254 | 13 | `public double[] getStaffedWagePerType()` | The wage bill per job type with the fill rate already applied. |
| 280 | 8 | `public double[] getStaffedWagePerTier()` | The same staffed wage bill, collapsed onto the six pay tiers. |
| 305 | 5 | `public void setWagesPerType()` | Wages, read from PayTier rather than typed out here. |
| 320 | 5 | `public void takeWagesFrom(LabourMarket market)` | Takes this month's wages from the market instead of the constants. |
| 325 | 3 | `public void setPopulation(long population)` |  |
| 346 | 3 | `public void recomputeWorkforce()` | Recomputes workforce from the current population, without touching population itself. |
| 368 | 3 | `public void restoreWorkforce(long workforce)` | Puts back the workforce the month was actually worked by. |
| 372 | 3 | `public long getWorkforceForSave()` |  |
| 376 | 5 | `public void UpdateTotalWagePerType()` |  |
| 403 | 12 | `public long[] getJobVacancy()` | Positions nobody qualified is available to fill. |
| 422 | 16 | `long[] vacancyInputs()` | Every number getJobVacancy()'s fill reads (AP5), as raw bits: the posts and the licences by job type, the skilled heads, the workforce, the prisoners, the owner families, the students (their count first, -1 for none) ... |
| 440 | 54 | `long[] getJobVacancyDrawn()` | The vacancies by job type as the bands stand, filled afresh: getJobVacancy() outside a pass, and the figure a held one is compared with (AP5). |

### WORKERS TAKE THE BEST-PAID JOB THEY QUALIFY FOR (0.7.18) (lines 495-1137)

| line | len | member | says |
|---:|---:|---|---|
| 580 | 4 | `public void setLadder(WageLadder l)` | Puts the city's wages and the allocator's band bases on a ladder (R0d): today's by default, 1901's in a city founded the 1900 way. |
| 586 | 1 | `public WageLadder getLadder()` | The ladder this city's wages are struck on (R0d). |
| 589 | 11 | `static double[] bandBases(WageLadder l)` | Each band's lowest ungated ratio on a ladder: the base its posts are compared on. |
| 602 | 1 | `public double getBandBase(WageBand band)` | The allocator's base for a band: its lowest ungated ratio on this city's ladder (R0d; read by WageLadderCheck). |
| 610 | 23 | **type** `public static final class BandFill` | One month's workers against one count of posts, by band (0.7.18) - the single definition of who can fill a post. |
| 620 | 6 | `BandFill(int bands, int types)` _(in PopulationManager.BandFill)_ |  |
| 628 | 4 | `public boolean joinedAbove(WageBand band)` _(in PopulationManager.BandFill)_ | Whether this band was filled as one market with the band above it. |
| 637 | 3 | `public BandFill fillByBand()` | The fill against this month's posts offered. |
| 646 | 79 | `public BandFill fillByBand(double[] posts)` | The fill against a given count of staffable posts per band: this month's (staffablePostsByBand()), or a planner's, which also counts the posts no employer offers this month (Sector.staffing()). |
| 727 | 6 | `private static boolean hiring(BandFill f, double[] posts, int hi, int lo)` | Whether any band in a market has posts it has not filled. |
| 741 | 58 | `private double fillMarket(BandFill f, int hi, int lo, double carriedIn, double[] posts, double[] own, double[] gated, double[] ...` | Fills bands hi down to lo as one market: sets what each holds, its supply and the market it is in, puts the market's wage in wage[k] (as a multiple of the unskilled base; minus infinity for a market with no posts, whi... |
| 801 | 9 | `private double heldAt(double lambda, int hi, int lo, double[] posts, double[] gated)` | The workers bands hi down to lo would hold if every one of their posts paid lambda. |
| 818 | 6 | `public static boolean isGated(JobType job)` | True for a job no amount of general education qualifies anybody for. |
| 834 | 63 | `public double[] workforceByBand()` | The workforce split by skill. |
| 900 | 1 | `public void setImprisoned(double adults)` |  |
| 901 | 1 | `public double getImprisoned()` |  |
| 905 | 1 | `public void setOwners(double adults)` |  |
| 906 | 1 | `public double getOwners()` |  |
| 910 | 1 | `public void setStudying(double[] byBand)` |  |
| 911 | 5 | `public double getStudyingTotal()` |  |
| 918 | 7 | `public double[] postsByBand()` | Posts that exist at each skill level. |
| 947 | 10 | `public double[] staffablePostsByBand()` | Posts a band's own members could actually be put into. |
| 976 | 3 | `public double[] supplyByBand()` | Workers actually available to each band: what the labour market prices against, a band's staffable posts over this being its tightness. |
| 981 | 3 | `public double[] supplyByBand(double[] posts)` | The same fill against a given count of posts per band - a planner's, which counts posts no employer offers this month (0.7.17; Sector.staffing()). |
| 998 | 4 | `public double surplusInBand(WageBand band)` | How many more workers of this band the city has than posts for them. |
| 1004 | 9 | `public double[] getBandShare()` | The mix as shares, for anything that wants proportions. |
| 1015 | 1 | `public double[] getSkilledHeads()` | The carried skilled counts, for the save. |
| 1017 | 3 | `public double getLicensed(JobType job)` |  |
| 1021 | 1 | `public double[] getLicensedHeads()` |  |
| 1033 | 11 | `public double spareLicences(JobType job)` | Licence holders the city is not already working. |
| 1045 | 4 | `public void restoreLicensed(double[] saved)` |  |
| 1057 | 6 | `public void addLicences(double[] newly)` | New graduates of the professional schools. |
| 1083 | 6 | `public void retireSkilled(double leaveRate)` | Death and retirement, which take the skilled along with everybody else. |
| 1099 | 15 | `public void trimLicencesToBand()` | Keeps the licensed counts inside the band that contains them. |
| 1115 | 5 | `public void restoreSkilledHeads(double[] saved)` |  |
| 1131 | 6 | `public void applyBandFlow(double[] flow)` | This month's schooling, as a net movement between bands. |

### THE APPRENTICES (R1g, 2026-11-19; docs/queue/R1g-notes.md, the cause and the fix) (lines 1138-1400)

| line | len | member | says |
|---:|---:|---|---|
| 1164 | 9 | `public double apprentice(double comingOfAge, double tradeShare, double schooled)` | The month's apprentices: of the young who came of age, the year's trade share, less the schools' diploma-holders of the month, into the diploma band. |
| 1175 | 1 | `public double getLastApprentices()` | The young who learnt a trade at work last month (apprentice()). |
| 1185 | 7 | `public void applySkilledFlows(double[] arrivals, double[] departures)` | Who moved in and who moved out, by skill. |
| 1202 | 12 | `public void inferBandShareFromJobs()` | Reads a plausible skill mix off the jobs the city is currently staffing. |
| 1215 | 13 | `public double[] getJobFillRate()` |  |
| 1229 | 3 | `public long getWorkforce()` |  |
| 1248 | 3 | `public long getUnemployed()` | Adults who want work and have none. |
| 1263 | 5 | `public double getLabourForce()` | Adults who could take a post this month: the workforce less the full-time students. |
| 1270 | 4 | `public double getUnemploymentRate()` | Unemployed as a share of everyone who could work, 0-1. |
| 1275 | 3 | `public long[] getJobs()` |  |
| 1279 | 3 | `public double getAdultPercent()` |  |
| 1283 | 79 | `public void printPopulationInfo()` |  |
| 1363 | 7 | `public void resetPopulationManager()` |  |
| 1373 | 4 | `static { ... }` |  |
| 1395 | 4 | `public void redenominate(double scale)` | The wage table, in the new unit. |

