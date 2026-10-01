# Migration.java - 1,209 lines · 49 methods · 14 constants · model

`ham/citybuildersim/Migration.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

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

**Uses:** [WageBand](WageBand.md) (25), [PayTier](PayTier.md) (8), [FamilyModel](FamilyModel.md) (7), [LabourMarket](LabourMarket.md) (3), [RealEstate](RealEstate.md) (2), [JobType](JobType.md) (2), [PopulationManager](PopulationManager.md) (2), [EducationType](EducationType.md) (2), [PopulationCohorts](PopulationCohorts.md) (1), [AgeBand](AgeBand.md) (1)

**Used by (14):** [BuildScreen](BuildScreen.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [Inbox](Inbox.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [UserInterface](UserInterface.md)

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
| 436 | · reading |
| 481 | A REFORM IS A CHANGE OF UNITS, AND THE STREAK IS TWELVE MONTHS LONG |
| 526 | · recording |
| 538 | IN REAL TERMS, OR EVERY DEFLATION IS AN EXODUS |
| 668 | · deciding |
| 1166 | · saving |

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
| 759 | `private int[] doors` | The door census this month's migration is asked against (0.7.17): the homes standing, by size. |
| 871 | `private double bankruptcyPush` | Households the balance sheet discharged this month, whose people are leaving because they are broke. |
| 879 | `private double lastBankruptcyPush` |  |
| 1009 | `private final boolean[] closed` | Bands a harness has closed to arrivals, by ordinal; see holdArrivals(). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1160 | **type** `public class Migration` | Why people move to this city, and the much narrower question of why they leave. |

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

### what it carries (lines 365-435)

### reading (lines 436-480)

| line | len | member | says |
|---:|---:|---|---|
| 438 | 1 | `public double getLastTarget()` |  |
| 440 | 1 | `public double getLastSeniorPull()` | What senior care multiplied the target by. |
| 441 | 1 | `public double getLastArrivals()` |  |
| 442 | 1 | `public double[] getLastArrivalMix()` |  |
| 443 | 1 | `public double[] getLastOpportunity()` |  |
| 444 | 1 | `public double[] getLastArrivalLicences()` |  |
| 445 | 1 | `public double[] getLastDepartureMix()` |  |
| 446 | 1 | `public double getLastDepartures()` |  |
| 447 | 1 | `public double getLastCrowding()` |  |
| 449 | 1 | `public double getLastRoom()` | People the placement had room for this month, which arrivals may not exceed; NaN when it was not asked. |
| 450 | 1 | `public double getLastDecliningShare()` |  |
| 451 | 1 | `public double getLastResidentsPerJob()` |  |
| 452 | 1 | `public double getLastNet()` |  |
| 454 | 3 | `public int getDecliningStreak(PayTier tier)` |  |
| 459 | 3 | `public boolean hasFullHistory()` | True once there is a full year of history to judge a streak against. |
| 471 | 6 | `public boolean isDeclining(PayTier tier)` | True if this tier's people are entitled to leave. |
| 478 | 1 | `private double newest(int t)` |  |
| 479 | 1 | `private double oldest(int t)` |  |

### A REFORM IS A CHANGE OF UNITS, AND THE STREAK IS TWELVE MONTHS LONG (lines 481-525)

| line | len | member | says |
|---:|---:|---|---|
| 519 | 6 | `public void redenominate(double scale)` | Scales the wage history so a reformed city reads its own past correctly. |

### recording (lines 526-537)

| line | len | member | says |
|---:|---:|---|---|
| 534 | 3 | `public void recordWages(double[] wagePerTier)` | Files this month's wage bill per tier and updates every streak. |

### IN REAL TERMS, OR EVERY DEFLATION IS AN EXODUS (lines 538-667)

| line | len | member | says |
|---:|---:|---|---|
| 579 | 23 | `public void recordWages(double[] wagePerTier, double priceLevel)` | whose index has not based yet, which is also what every fixture that calls the one-argument form gets |
| 615 | 13 | `public double decliningShare()` | How much of the city's payroll sits in tiers whose people may leave. |
| 637 | 7 | `public double severity(PayTier tier)` | How far a tier has fallen over the year, 0 to 1. |
| 654 | 13 | `public double decliningPressure()` | The share of the city's payroll that has actually been destroyed, as opposed to merely sitting in a tier that qualifies. |

### deciding (lines 668-1165)

| line | len | member | says |
|---:|---:|---|---|
| 704 | 3 | `public double crowdingFactor(int homes, FamilyModel families)` | How crowded the city is, as a multiplier on arrivals: 1 is room to spare, 0 is physically full. |
| 712 | 40 | `public double crowdingFactor(int homes, FamilyModel families, FamilyModel.Room room)` | The same, read against the room the placement has left (the Game's path), or against last month's placement when that is null. |
| 761 | 3 | `public void setDoors(int[] homesBySize)` |  |
| 774 | 5 | `public double monthlyNet(int population, int totalJobs, int householdCapacity, int homes, FamilyModel families, double adultShare)` | The month's net migration: positive is people arriving. |
| 787 | 74 | `public double monthlyNet(int population, int totalJobs, int householdCapacity, int homes, FamilyModel families, double adultSha...` | The same, with the draw good senior care adds. |
| 873 | 3 | `public void setBankruptcyDepartures(double people)` |  |
| 877 | 1 | `public double getLastBankruptcyDepartures()` |  |
| 890 | 105 | `public double monthlyNet(int population, int totalJobs, int householdCapacity, int homes, FamilyModel families, double adultSha...` | The same month, with a labour market behind it. |
| 1003 | 4 | `public static double opportunity(double open, double queue)` | A band's chance of work at its own level, as a multiplier on its pull. |
| 1020 | 3 | `public void holdArrivals(WageBand band)` | Harnesses only: nobody of this band arrives for the rest of the run, whatever it is paid - the way BusinessInvestment.holdSector() holds a sector out of the investment loop. |
| 1031 | 3 | `public boolean admits(WageBand band)` | Whether any migrant can come for this band at all (0.7.18): it has an arrival ceiling above zero and no harness has closed it. |
| 1045 | 120 | `private void composeArrivals(LabourMarket market, PopulationManager people)` | Splits this month's arrivals across the skill bands. |

### saving (lines 1166-1209)

| line | len | member | says |
|---:|---:|---|---|
| 1168 | 10 | `public double[] toSaveArray()` |  |
| 1179 | 12 | `public void restore(double[] saved)` |  |
| 1192 | 17 | `public void reset()` |  |

