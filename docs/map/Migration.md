# Migration.java - 1,650 lines · 67 methods · 20 constants · model

`ham/citybuildersim/Migration.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

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
> Since WG2 the pulls lift the HOMES' draw only. Senior care, cheap rents and
> a safe city change how many people the city's homes draw; the jobs' draw is
> cut by a dear or an unsafe city and never raised by anything, and arrivals
> slow as the city's out-of-work share passes HEALTHY_OUT_OF_WORK and stop at
> ARRIVALS_STOP_OUT_OF_WORK (see MIGRATION AIMS AT A HEALTHY SLACK).
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
> 
> One push has joined it since WG2, and it is the unemployment the paragraph
> above says is no exodus: the jobless past HEALTHY_OUT_OF_WORK of the labour
> force leave too, whatever their wage, a drift and no evacuation: the excess
> over the healthy rate is counted in labour-force adults, but SURPLUS_DEPARTURE_RATE
> of it (a fiftieth) a month is counted in PEOPLE, and the people who go leave as
> the city's own do, households and all (cohorts.migrate() takes them out of every
> age band at the city's shape). So the jobless excess itself falls by about that
> rate times the adult share of those who go, while arrivals have slowed to meet it.

**Uses:** [WageBand](WageBand.md) (37), [PayTier](PayTier.md) (8), [FamilyModel](FamilyModel.md) (7), [LabourMarket](LabourMarket.md) (4), [JobType](JobType.md) (4), [ArrivalMix](ArrivalMix.md) (4), [EducationType](EducationType.md) (4), [PopulationManager](PopulationManager.md) (3), [RealEstate](RealEstate.md) (2), [PopulationCohorts](PopulationCohorts.md) (1), [AgeBand](AgeBand.md) (1)

**Used by (21):** [ArrivalMixCheck](ArrivalMixCheck.md), [BuildScreen](BuildScreen.md), [CrimeCheck](CrimeCheck.md), [EducationCheck](EducationCheck.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [Inbox](Inbox.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [Sector](Sector.md), [ServicesScreen](ServicesScreen.md), [StaffingDrawCheck](StaffingDrawCheck.md), [UserInterface](UserInterface.md), [Village1900Check](Village1900Check.md), [WantedPostsCheck](WantedPostsCheck.md)

## Sections

| line | section |
|---:|---|
| 67 | · the dials |
| 69 | HOW MANY RESIDENTS A JOB SUPPORTS |
| 118 | WHY A CITY WITH GOOD SENIOR CARE IS WORTH MOVING TO |
| 147 | WHO ARRIVES, NOT JUST HOW MANY |
| 250 | AND A CITY NOBODY CAN AFFORD TO LIVE IN IS A CITY PEOPLE DO NOT MOVE TO |
| 331 | AND A CITY WITH MORE CRIME THAN IT SHOULD HAVE (2026-09-11) |
| 388 | MIGRATION AIMS AT A HEALTHY SLACK (WG2, {next}, 2026-11-10) |
| 496 | · what it carries |
| 585 | · reading |
| 648 | A REFORM IS A CHANGE OF UNITS, AND THE STREAK IS TWELVE MONTHS LONG |
| 693 | · recording |
| 705 | IN REAL TERMS, OR EVERY DEFLATION IS AN EXODUS |
| 835 | · deciding |
| 1517 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `Migration.TARGET_LABOUR_SLACK` | `.125` | Workers per post the city aims to have, over and above one each. |
| 106 | `Migration.MIN_ADULT_SHARE` | `.25` | Below this the arithmetic stops meaning anything - a city that is 10% adults would demand ten residents per job and grow without limit. |
| 115 | `Migration.JOB_WEIGHT` | `.75` | Jobs are the main factor, per Jerus. |
| 116 | `Migration.HOME_WEIGHT` | `.25` |  |
| 145 | `Migration.SENIOR_CARE_PULL` | `.30` | How much more attractive full senior coverage makes the city. |
| 210 | `Migration.SURPLUS_DEPARTURE_RATE` | `.02` | What share of a band's unemployable surplus leaves each month, once its wage has stopped falling. |
| 243 | `Migration.OPPORTUNITY_FLOOR` | `.15` | What share of a band still moves here when there is no work at its level. |
| 299 | `Migration.PRICED_OUT_WEIGHT` | `1.0` | How much of the affordability excess turns into people not coming. |
| 352 | `Migration.CRIME_PULL_WEIGHT` | `.1` | How much of the crime excess turns into people not coming: a tenth off at twice Canada's rate. |
| 355 | `Migration.CRIME_DEPARTURE_RATE` | `.0005` | The share of the city that leaves a month for each whole Canada of crime over Canada's. |
| 382 | `Migration.ARRIVAL_RATE` | `.15` | How much of the gap closes each month. |
| 383 | `Migration.DEPARTURE_RATE` | `.05` |  |
| 386 | `Migration.DECLINE_MONTHS` | `12` | Consecutive months of falling wages before a tier's people give up. |
| 455 | `Migration.HEALTHY_OUT_OF_WORK` | `.06` | The share of the labour force out of work that migration aims at: arrivals slow past it and the jobless over it leave (the middle of big rich-country cities, Tokyo 2.4% to Madrid 9%). |
| 458 | `Migration.ARRIVALS_STOP_OUT_OF_WORK` | `.12` | The out-of-work share past which nobody moves here for work: twice the healthy rate, where the People screen's verdict turns red. |
| 461 | `Migration.BRAKE_FROM_MONTH` | `240` | The city's age in months (this Migration's months recorded) before which arrivals are not braked and the jobless are not sent away, because a young city's out-of-work share swings with every works that opens. |
| 498 | `Migration.TIERS` | `PayTier.values().length` |  |
| 1547 | `Migration.HISTORY_SLOTS` | `TIERS * DECLINE_MONTHS + TIERS + 1` | The wage history, its streaks and the months recorded: the array before 0.7.27. |
| 1550 | `Migration.LAST_SCALARS` | `17` | The month's scalars that follow it, in toSaveArray()'s order; the last of them is lastRoom (GW1), and a save from before it has one fewer. |
| 1558 | `Migration.BRAKE_SLOTS` | `1` | The slots after the licences: lastBrake (WG2-R), which a save from before it does not have. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 323 | `private double rentBurden` | What rent costs a household here, as a share of take-home. |
| 329 | `private double lastAffordabilityPull` |  |
| 365 | `private double crimeVsCanada` | Crime's rate against Canada's, last month's. |
| 368 | `private double lastCrimePull` |  |
| 369 | `private double lastCrimeDepartures` |  |
| 510 | `private final double[][] history` | A rolling year of each tier's wage bill, oldest at index 0. |
| 511 | `private final int[] decliningStreak` |  |
| 512 | `private int monthsRecorded` |  |
| 516 | `private double[] lastArrivalMix` | The skills of the people who moved in this month, by band. |
| 526 | `private final double[] lastOpportunity` | Each band's chance of work at its own level, as last computed. |
| 543 | `private double[] lastArrivalLicences` | ...and which of them already hold a professional licence. |
| 554 | `private double[] lastDepartureMix` | ...and of the ones who left, which is NOT the same shape. |
| 556 | `private double lastTarget` |  |
| 557 | `private double lastArrivals` |  |
| 558 | `private double lastDepartures` |  |
| 559 | `private double lastCrowding` |  |
| 560 | `private double lastDecliningShare` |  |
| 561 | `private double lastSeniorPull` |  |
| 562 | `private double lastResidentsPerJob` |  |
| 565 | `private double lastRoom` | The people the placement had room for, the month's bound on arrivals (0.7.17); NaN with no census. |
| 568 | `private double lastBrake` | The share of the draw's arrivals the brake let through last month (WG2-R): 1 unbraked, 0 stopped. |
| 579 | `private double lastJobs` | THE BRIDGE AND THE LEAVERS, KEPT (0.7.27). |
| 580 | `private double lastHomeCapacity` |  |
| 581 | `private double lastJobDraw` |  |
| 582 | `private double lastHomeDraw` |  |
| 583 | `private double lastWorkDepartures` |  |
| 926 | `private long[] doors` | The door census this month's migration is asked against (0.7.17): the homes standing, by size. |
| 1046 | `private double bankruptcyPush` | Households the balance sheet discharged this month, whose people are leaving because they are broke. |
| 1054 | `private double lastBankruptcyPush` |  |
| 1239 | `private final boolean[] closed` | Bands a harness has closed to arrivals, by ordinal; see holdArrivals(). |
| 1270 | `private ArrivalMix.Point yearMix` | The year's skill mix this month's arrivals are drawn from (R0d slice 2), or null: null in every city but one founded the 1900 way, whose Game sets it each month from ArrivalMix.at(), and so null is the model composeAr... |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 1586 | **type** `public class Migration` | Why people move to this city, and the much narrower question of why they leave. |

### the dials (lines 67-68)

### HOW MANY RESIDENTS A JOB SUPPORTS (lines 69-117)

| line | len | member | says |
|---:|---:|---|---|
| 108 | 5 | `public static double residentsPerJob(double adultShare)` |  |

### WHY A CITY WITH GOOD SENIOR CARE IS WORTH MOVING TO (lines 118-146)

### WHO ARRIVES, NOT JUST HOW MANY (lines 147-249)

| line | len | member | says |
|---:|---:|---|---|
| 190 | 5 | `public static double reach(double premium)` | How far a premium has travelled from the going rate to the ceiling. |
| 246 | 3 | `public static double seniorCarePull(double seniorCoverage)` | The multiplier on the target, given senior-care coverage. |

### AND A CITY NOBODY CAN AFFORD TO LIVE IN IS A CITY PEOPLE DO NOT MOVE TO (lines 250-330)

| line | len | member | says |
|---:|---:|---|---|
| 307 | 7 | `public static double affordabilityPull(double rentBurden)` | The multiplier dear housing puts on how big a city these conditions support. |
| 325 | 1 | `public void setRentBurden(double burden)` |  |
| 327 | 1 | `public double getLastAffordabilityPull()` |  |

### AND A CITY WITH MORE CRIME THAN IT SHOULD HAVE (2026-09-11) (lines 331-387)

| line | len | member | says |
|---:|---:|---|---|
| 358 | 5 | `public static double crimePull(double rateVsCanada)` | The multiplier crime puts on how big a city these conditions support. |
| 366 | 1 | `public void setCrimeVsCanada(double ratio)` |  |
| 370 | 1 | `public double getLastCrimePull()` |  |
| 371 | 1 | `public double getLastCrimeDepartures()` |  |

### MIGRATION AIMS AT A HEALTHY SLACK (WG2, {next}, 2026-11-10) (lines 388-495)

| line | len | member | says |
|---:|---:|---|---|
| 467 | 3 | `public static double jobsPull(double affordability, double crime)` | What the pulls leave of the jobs' draw: the rent's and the crime's, never above 1. |
| 472 | 3 | `public static double homesPull(double senior, double affordability, double crime)` | What the pulls make of the homes' draw: senior care, the rent and the crime, multiplied. |
| 480 | 5 | `public static double arrivalBrake(double outOfWork)` | The share of the arrivals that still come, given the share of the labour force out of work: 1 at and under HEALTHY_OUT_OF_WORK, 0 at and past ARRIVALS_STOP_OUT_OF_WORK, a straight line between. |
| 487 | 3 | `public boolean brakeApplies()` | True once this Migration has recorded BRAKE_FROM_MONTH months: the city is old enough for the brake and the drift to act. |
| 492 | 3 | `public double arrivalBrakeNow(double outOfWork)` | The share of the arrivals that still come now: arrivalBrake() of the out-of-work share once brakeApplies(), else 1. |

### what it carries (lines 496-584)

### reading (lines 585-647)

| line | len | member | says |
|---:|---:|---|---|
| 587 | 1 | `public double getLastTarget()` |  |
| 589 | 1 | `public double getLastSeniorPull()` | What senior care multiplied the homes' draw by (since WG2 the jobs' draw is not lifted by it). |
| 590 | 1 | `public double getLastArrivals()` |  |
| 592 | 1 | `public double getLastBrake()` | What the People screen's migration word reads, so it agrees with getLastArrivals(): the share of the draw's arrivals the brake let through that month; 1 unbraked (a young city, or no people read), 0 stopped. |
| 593 | 1 | `public double[] getLastArrivalMix()` |  |
| 594 | 1 | `public double[] getLastOpportunity()` |  |
| 595 | 1 | `public double[] getLastArrivalLicences()` |  |
| 596 | 1 | `public double[] getLastDepartureMix()` |  |
| 597 | 1 | `public double getLastDepartures()` |  |
| 598 | 1 | `public double getLastCrowding()` |  |
| 600 | 1 | `public double getLastRoom()` | People the placement had room for this month, which arrivals may not exceed; NaN when it was not asked. |
| 601 | 1 | `public double getLastDecliningShare()` |  |
| 602 | 1 | `public double getLastResidentsPerJob()` |  |
| 603 | 1 | `public double getLastNet()` |  |
| 605 | 1 | `public double getLastJobs()` | The posts the month's draw was struck on (0.7.27). |
| 607 | 1 | `public double getLastHomeCapacity()` | ...and the people the city's homes comfortably hold. |
| 609 | 1 | `public double getLastJobDraw()` | The jobs' half of the draw: the posts, times the residents each supports, at JOB_WEIGHT. |
| 611 | 1 | `public double getLastHomeDraw()` | ...and the homes', at HOME_WEIGHT. |
| 613 | 1 | `public double getLastDrawBeforePulls()` | The two halves together: the draw before the pulls (senior care, the rent and crime) multiply it. |
| 615 | 1 | `public double getLastJobsPull()` | What the pulls left of the jobs' draw: the rent's and the crime's, never above 1, from the last pulls. |
| 617 | 1 | `public double getLastHomesPull()` | What the pulls made of the homes' draw: senior care, the rent and the crime, from the last pulls. |
| 619 | 1 | `public double getLastWorkDepartures()` | The leavers the work pushed out - a dying trade, a band pinned with people to spare; with the crime's and the broke, the month's departures. |
| 621 | 3 | `public int getDecliningStreak(PayTier tier)` |  |
| 626 | 3 | `public boolean hasFullHistory()` | True once there is a full year of history to judge a streak against. |
| 638 | 6 | `public boolean isDeclining(PayTier tier)` | True if this tier's people are entitled to leave. |
| 645 | 1 | `private double newest(int t)` |  |
| 646 | 1 | `private double oldest(int t)` |  |

### A REFORM IS A CHANGE OF UNITS, AND THE STREAK IS TWELVE MONTHS LONG (lines 648-692)

| line | len | member | says |
|---:|---:|---|---|
| 686 | 6 | `public void redenominate(double scale)` | Scales the wage history so a reformed city reads its own past correctly. |

### recording (lines 693-704)

| line | len | member | says |
|---:|---:|---|---|
| 701 | 3 | `public void recordWages(double[] wagePerTier)` | Files this month's wage bill per tier and updates every streak. |

### IN REAL TERMS, OR EVERY DEFLATION IS AN EXODUS (lines 705-834)

| line | len | member | says |
|---:|---:|---|---|
| 746 | 23 | `public void recordWages(double[] wagePerTier, double priceLevel)` | whose index has not based yet, which is also what every fixture that calls the one-argument form gets |
| 782 | 13 | `public double decliningShare()` | How much of the city's payroll sits in tiers whose people may leave. |
| 804 | 7 | `public double severity(PayTier tier)` | How far a tier has fallen over the year, 0 to 1. |
| 821 | 13 | `public double decliningPressure()` | The share of the city's payroll that has actually been destroyed, as opposed to merely sitting in a tier that qualifies. |

### deciding (lines 835-1516)

| line | len | member | says |
|---:|---:|---|---|
| 871 | 3 | `public double crowdingFactor(long homes, FamilyModel families)` | How crowded the city is, as a multiplier on arrivals: 1 is room to spare, 0 is physically full. |
| 879 | 40 | `public double crowdingFactor(long homes, FamilyModel families, FamilyModel.Room room)` | The same, read against the room the placement has left (the Game's path), or against last month's placement when that is null. |
| 928 | 3 | `public void setDoors(long[] homesBySize)` |  |
| 941 | 5 | `public double monthlyNet(long population, long totalJobs, long householdCapacity, long homes, FamilyModel families, double adul...` | The month's net migration: positive is people arriving. |
| 954 | 82 | `public double monthlyNet(long population, long totalJobs, long householdCapacity, long homes, FamilyModel families, double adul...` | The same, with the draw good senior care adds. |
| 1048 | 3 | `public void setBankruptcyDepartures(double people)` |  |
| 1052 | 1 | `public double getLastBankruptcyDepartures()` |  |
| 1065 | 160 | `public double monthlyNet(long population, long totalJobs, long householdCapacity, long homes, FamilyModel families, double adul...` | The same month, with a labour market behind it. |
| 1233 | 4 | `public static double opportunity(double open, double queue)` | A band's chance of work at its own level, as a multiplier on its pull. |
| 1250 | 3 | `public void holdArrivals(WageBand band)` | Harnesses only: nobody of this band arrives for the rest of the run, whatever it is paid - the way BusinessInvestment.holdSector() holds a sector out of the investment loop. |
| 1261 | 3 | `public boolean admits(WageBand band)` | Whether any migrant can come for this band at all (0.7.18): it has an arrival ceiling above zero and no harness has closed it. |
| 1273 | 1 | `public void setYearMix(ArrivalMix.Point mix)` | Game, each month of a city founded the 1900 way: the mix of skills the month's arrivals are drawn from (ArrivalMix.at()); null for any other city. |
| 1276 | 1 | `public ArrivalMix.Point getYearMix()` | The year's skill mix set for this month, or null where the arrivals are drawn from today's ceilings alone. |
| 1291 | 132 | `private void composeArrivals(LabourMarket market, PopulationManager people)` | Splits this month's arrivals across the skill bands. |
| 1468 | 48 | `public double[] drawFor(double[] wanted, LabourMarket market, PopulationManager people, int window, ArrivalMix.Point mix)` | The workers per wage band migration would bring for an order's posts within {@code window} months of its opening, for the staffing gate (Sector.staffing(), GW1). |

### saving (lines 1517-1650)

| line | len | member | says |
|---:|---:|---|---|
| 1553 | 3 | `private static int lastSlots()` | ...then the arrival mix and the departure mix by band, and the licences by job. |
| 1560 | 21 | `public double[] toSaveArray()` |  |
| 1582 | 39 | `public void restore(double[] saved)` |  |
| 1622 | 28 | `public void reset()` |  |

