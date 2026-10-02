# Migration.java - 1,312 lines · 56 methods · 16 constants · model

`ham/citybuildersim/Migration.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> Why people move to this city, and the much narrower question of why they leave.
> 
> This class is what replaced `min(housing, jobs * 2.25)`. That expression was
> the population: derived fresh every month from nothing, with no memory, so a
> finished tower filled instantly and a demolished one erased its residents. It
> is now a TARGET that migration chases, and the chasing is what gives the city
> inertia.
> 
> ==================== JERUS'S SPEC ====================
> 
>   "make it so its city housing space and current jobs available. If a city is
>    full but has jobs, they'll still move in."
> 
>   "homes pull too, but not as much as jobs. Crowding slows but jobs is still
>    the main factor."
> 
>   "they only leave if the respective job tier cashflow is declining for 12
>    months straight or is zero."
> 
> ======================================================
> 
> THE PULL is a weighted target, jobs three parts to housing one:
> 
>   jobs  -> every post supports RESIDENTS_PER_JOB people, worker and household
>   homes -> the people the city's residential buildings comfortably hold
> 
> Weighting them rather than taking the smaller of the two is the whole change
> Jerus asked for. A minimum makes housing a GATE - build one house too few and
> the city stops dead no matter how many jobs are going begging. A weighted sum
> makes it a PULL: a city with jobs and no houses still attracts people, they
> just arrive into a shortage. Which is what actually happens, and what the
> flatshare model in FamilyModel was built to absorb.
> 
> Housing gets a say twice, and the second is the interesting one: it also
> DAMPS. A crowded city keeps attracting people, more slowly, until there is
> physically nowhere left to put them - and that point is asked of FamilyModel
> rather than typed in here, because it has to be the same line as the one where
> the squeeze runs out of valves. See crowdingFactor().
> 
> THE PUSH is deliberately hard to trigger. A negative gap does NOT empty a
> city; a city with more people than jobs is a city with unemployment, not a
> city with an exodus. People only leave when a pay tier has been shrinking for
> a solid year or has stopped paying anything at all - which means a bust takes
> a year to start and then years to play out, and a bad month is just a bad
> month. That asymmetry is the point of the twelve-month gate.

**Uses:** [WageBand](WageBand.md) (26), [PayTier](PayTier.md) (8), [FamilyModel](FamilyModel.md) (7), [LabourMarket](LabourMarket.md) (3), [JobType](JobType.md) (3), [RealEstate](RealEstate.md) (2), [PopulationManager](PopulationManager.md) (2), [EducationType](EducationType.md) (2), [PopulationCohorts](PopulationCohorts.md) (1), [AgeBand](AgeBand.md) (1)

**Used by (16):** [BuildScreen](BuildScreen.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [Inbox](Inbox.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 52 | · the dials |
| 54 | HOW MANY RESIDENTS A JOB SUPPORTS |
| 103 | WHY A CITY WITH GOOD SENIOR CARE IS WORTH MOVING TO |
| 124 | WHO ARRIVES, NOT JUST HOW MANY |
| 227 | AND A CITY NOBODY CAN AFFORD TO LIVE IN IS A CITY PEOPLE DO NOT MOVE TO |
| 308 | AND A CITY WITH MORE CRIME THAN IT SHOULD HAVE (2026-09-11) |
| 365 | · what it carries |
| 451 | · reading |
| 508 | A REFORM IS A CHANGE OF UNITS, AND THE STREAK IS TWELVE MONTHS LONG |
| 553 | · recording |
| 565 | IN REAL TERMS, OR EVERY DEFLATION IS AN EXODUS |
| 695 | · deciding |
| 1204 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 84 | `Migration.TARGET_LABOUR_SLACK` | `.125` | Workers per post the city aims to have, over and above one each. |
| 91 | `Migration.MIN_ADULT_SHARE` | `.25` | Below this the arithmetic stops meaning anything - a city that is 10% adults would demand ten residents per job and grow without limit. |
| 100 | `Migration.JOB_WEIGHT` | `.75` | Jobs are the main factor, per Jerus. |
| 101 | `Migration.HOME_WEIGHT` | `.25` |  |
| 122 | `Migration.SENIOR_CARE_PULL` | `.30` | How much more attractive full senior coverage makes the city. |
| 187 | `Migration.SURPLUS_DEPARTURE_RATE` | `.02` | What share of a band's unemployable surplus leaves each month, once its wage has stopped falling. |
| 220 | `Migration.OPPORTUNITY_FLOOR` | `.15` | What share of a band still moves here when there is no work at its level. |
| 276 | `Migration.PRICED_OUT_WEIGHT` | `1.0` | How much of the affordability excess turns into people not coming. |
| 329 | `Migration.CRIME_PULL_WEIGHT` | `.1` | How much of the crime excess turns into people not coming: a tenth off at twice Canada's rate. |
| 332 | `Migration.CRIME_DEPARTURE_RATE` | `.0005` | The share of the city that leaves a month for each whole Canada of crime over Canada's. |
| 359 | `Migration.ARRIVAL_RATE` | `.15` | How much of the gap closes each month. |
| 360 | `Migration.DEPARTURE_RATE` | `.05` |  |
| 363 | `Migration.DECLINE_MONTHS` | `12` | Consecutive months of falling wages before a tier's people give up. |
| 367 | `Migration.TIERS` | `PayTier.values().length` |  |
| 1221 | `Migration.HISTORY_SLOTS` | `TIERS * DECLINE_MONTHS + TIERS + 1` | The wage history, its streaks and the months recorded: the array before 0.7.27. |
| 1224 | `Migration.LAST_SCALARS` | `16` | The month's scalars that follow it, in toSaveArray()'s order. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 300 | `private double rentBurden` | What rent costs a household here, as a share of take-home. |
| 306 | `private double lastAffordabilityPull` |  |
| 342 | `private double crimeVsCanada` | Crime's rate against Canada's, last month's. |
| 345 | `private double lastCrimePull` |  |
| 346 | `private double lastCrimeDepartures` |  |
| 379 | `private final double[][] history` | A rolling year of each tier's wage bill, oldest at index 0. |
| 380 | `private final int[] decliningStreak` |  |
| 381 | `private int monthsRecorded` |  |
| 385 | `private double[] lastArrivalMix` | The skills of the people who moved in this month, by band. |
| 395 | `private final double[] lastOpportunity` | Each band's chance of work at its own level, as last computed. |
| 412 | `private double[] lastArrivalLicences` | ...and which of them already hold a professional licence. |
| 423 | `private double[] lastDepartureMix` | ...and of the ones who left, which is NOT the same shape. |
| 425 | `private double lastTarget` |  |
| 426 | `private double lastArrivals` |  |
| 427 | `private double lastDepartures` |  |
| 428 | `private double lastCrowding` |  |
| 429 | `private double lastDecliningShare` |  |
| 430 | `private double lastSeniorPull` |  |
| 431 | `private double lastResidentsPerJob` |  |
| 434 | `private double lastRoom` | The people the placement had room for, the month's bound on arrivals (0.7.17); NaN with no census. |
| 445 | `private double lastJobs` | THE BRIDGE AND THE LEAVERS, KEPT (0.7.27). |
| 446 | `private double lastHomeCapacity` |  |
| 447 | `private double lastJobDraw` |  |
| 448 | `private double lastHomeDraw` |  |
| 449 | `private double lastWorkDepartures` |  |
| 786 | `private int[] doors` | The door census this month's migration is asked against (0.7.17): the homes standing, by size. |
| 904 | `private double bankruptcyPush` | Households the balance sheet discharged this month, whose people are leaving because they are broke. |
| 912 | `private double lastBankruptcyPush` |  |
| 1047 | `private final boolean[] closed` | Bands a harness has closed to arrivals, by ordinal; see holdArrivals(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1263 | **type** `public class Migration` | Why people move to this city, and the much narrower question of why they leave. |

### the dials (lines 52-53)

### HOW MANY RESIDENTS A JOB SUPPORTS (lines 54-102)

| line | len | member | says |
|---:|---:|---|---|
| 93 | 5 | `public static double residentsPerJob(double adultShare)` |  |

### WHY A CITY WITH GOOD SENIOR CARE IS WORTH MOVING TO (lines 103-123)

### WHO ARRIVES, NOT JUST HOW MANY (lines 124-226)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 5 | `public static double reach(double premium)` | How far a premium has travelled from the going rate to the ceiling. |
| 223 | 3 | `public static double seniorCarePull(double seniorCoverage)` | The multiplier on the target, given senior-care coverage. |

### AND A CITY NOBODY CAN AFFORD TO LIVE IN IS A CITY PEOPLE DO NOT MOVE TO (lines 227-307)

| line | len | member | says |
|---:|---:|---|---|
| 284 | 7 | `public static double affordabilityPull(double rentBurden)` | The multiplier dear housing puts on how big a city these conditions support. |
| 302 | 1 | `public void setRentBurden(double burden)` |  |
| 304 | 1 | `public double getLastAffordabilityPull()` |  |

### AND A CITY WITH MORE CRIME THAN IT SHOULD HAVE (2026-09-11) (lines 308-364)

| line | len | member | says |
|---:|---:|---|---|
| 335 | 5 | `public static double crimePull(double rateVsCanada)` | The multiplier crime puts on how big a city these conditions support. |
| 343 | 1 | `public void setCrimeVsCanada(double ratio)` |  |
| 347 | 1 | `public double getLastCrimePull()` |  |
| 348 | 1 | `public double getLastCrimeDepartures()` |  |

### what it carries (lines 365-450)

### reading (lines 451-507)

| line | len | member | says |
|---:|---:|---|---|
| 453 | 1 | `public double getLastTarget()` |  |
| 455 | 1 | `public double getLastSeniorPull()` | What senior care multiplied the target by. |
| 456 | 1 | `public double getLastArrivals()` |  |
| 457 | 1 | `public double[] getLastArrivalMix()` |  |
| 458 | 1 | `public double[] getLastOpportunity()` |  |
| 459 | 1 | `public double[] getLastArrivalLicences()` |  |
| 460 | 1 | `public double[] getLastDepartureMix()` |  |
| 461 | 1 | `public double getLastDepartures()` |  |
| 462 | 1 | `public double getLastCrowding()` |  |
| 464 | 1 | `public double getLastRoom()` | People the placement had room for this month, which arrivals may not exceed; NaN when it was not asked. |
| 465 | 1 | `public double getLastDecliningShare()` |  |
| 466 | 1 | `public double getLastResidentsPerJob()` |  |
| 467 | 1 | `public double getLastNet()` |  |
| 469 | 1 | `public double getLastJobs()` | The posts the month's draw was struck on (0.7.27). |
| 471 | 1 | `public double getLastHomeCapacity()` | ...and the people the city's homes comfortably hold. |
| 473 | 1 | `public double getLastJobDraw()` | The jobs' half of the draw: the posts, times the residents each supports, at JOB_WEIGHT. |
| 475 | 1 | `public double getLastHomeDraw()` | ...and the homes', at HOME_WEIGHT. |
| 477 | 1 | `public double getLastDrawBeforePulls()` | The two halves together: the draw before senior care, the rent and crime multiply it. |
| 479 | 1 | `public double getLastWorkDepartures()` | The leavers the work pushed out - a dying trade, a band pinned with people to spare; with the crime's and the broke, the month's departures. |
| 481 | 3 | `public int getDecliningStreak(PayTier tier)` |  |
| 486 | 3 | `public boolean hasFullHistory()` | True once there is a full year of history to judge a streak against. |
| 498 | 6 | `public boolean isDeclining(PayTier tier)` | True if this tier's people are entitled to leave. |
| 505 | 1 | `private double newest(int t)` |  |
| 506 | 1 | `private double oldest(int t)` |  |

### A REFORM IS A CHANGE OF UNITS, AND THE STREAK IS TWELVE MONTHS LONG (lines 508-552)

| line | len | member | says |
|---:|---:|---|---|
| 546 | 6 | `public void redenominate(double scale)` | Scales the wage history so a reformed city reads its own past correctly. |

### recording (lines 553-564)

| line | len | member | says |
|---:|---:|---|---|
| 561 | 3 | `public void recordWages(double[] wagePerTier)` | Files this month's wage bill per tier and updates every streak. |

### IN REAL TERMS, OR EVERY DEFLATION IS AN EXODUS (lines 565-694)

| line | len | member | says |
|---:|---:|---|---|
| 606 | 23 | `public void recordWages(double[] wagePerTier, double priceLevel)` | whose index has not based yet, which is also what every fixture that calls the one-argument form gets |
| 642 | 13 | `public double decliningShare()` | How much of the city's payroll sits in tiers whose people may leave. |
| 664 | 7 | `public double severity(PayTier tier)` | How far a tier has fallen over the year, 0 to 1. |
| 681 | 13 | `public double decliningPressure()` | The share of the city's payroll that has actually been destroyed, as opposed to merely sitting in a tier that qualifies. |

### deciding (lines 695-1203)

| line | len | member | says |
|---:|---:|---|---|
| 731 | 3 | `public double crowdingFactor(int homes, FamilyModel families)` | How crowded the city is, as a multiplier on arrivals: 1 is room to spare, 0 is physically full. |
| 739 | 40 | `public double crowdingFactor(int homes, FamilyModel families, FamilyModel.Room room)` | The same, read against the room the placement has left (the Game's path), or against last month's placement when that is null. |
| 788 | 3 | `public void setDoors(int[] homesBySize)` |  |
| 801 | 5 | `public double monthlyNet(int population, int totalJobs, int householdCapacity, int homes, FamilyModel families, double adultShare)` | The month's net migration: positive is people arriving. |
| 814 | 80 | `public double monthlyNet(int population, int totalJobs, int householdCapacity, int homes, FamilyModel families, double adultSha...` | The same, with the draw good senior care adds. |
| 906 | 3 | `public void setBankruptcyDepartures(double people)` |  |
| 910 | 1 | `public double getLastBankruptcyDepartures()` |  |
| 923 | 110 | `public double monthlyNet(int population, int totalJobs, int householdCapacity, int homes, FamilyModel families, double adultSha...` | The same month, with a labour market behind it. |
| 1041 | 4 | `public static double opportunity(double open, double queue)` | A band's chance of work at its own level, as a multiplier on its pull. |
| 1058 | 3 | `public void holdArrivals(WageBand band)` | Harnesses only: nobody of this band arrives for the rest of the run, whatever it is paid - the way BusinessInvestment.holdSector() holds a sector out of the investment loop. |
| 1069 | 3 | `public boolean admits(WageBand band)` | Whether any migrant can come for this band at all (0.7.18): it has an arrival ceiling above zero and no harness has closed it. |
| 1083 | 120 | `private void composeArrivals(LabourMarket market, PopulationManager people)` | Splits this month's arrivals across the skill bands. |

### saving (lines 1204-1312)

| line | len | member | says |
|---:|---:|---|---|
| 1227 | 3 | `private static int lastSlots()` | ...then the arrival mix and the departure mix by band, and the licences by job. |
| 1231 | 19 | `public double[] toSaveArray()` |  |
| 1251 | 33 | `public void restore(double[] saved)` |  |
| 1285 | 27 | `public void reset()` |  |

