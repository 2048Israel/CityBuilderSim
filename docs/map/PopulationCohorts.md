# PopulationCohorts.java - 689 lines · 44 methods · 2 constants · model

`ham/citybuildersim/PopulationCohorts.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> The city's age pyramid, and since the switch, the city's POPULATION.
> 
> ==================== THIS IS NOW LOAD-BEARING ====================
> 
> It was a placeholder for two batches, asserted inert by a harness that played
> two identical cities and required every figure to match. That assertion is
> gone, on purpose: `total()` IS the population now, and everything downstream -
> the workforce, the job fill rate, every sector's output, the wage bill, the
> wage tax, GDP - keys off it.
> 
> WHAT CHANGED, AND WHY IT MATTERS MORE THAN IT LOOKS
> 
> The population used to be `min(housing, jobs * 2.25)`: derived fresh every
> month from nothing, with no memory. A finished tower filled instantly, a
> demolished one erased its residents, and a city could double in a month.
> 
> Now it is a STOCK. Births and deaths move it, `Migration` moves it far more,
> and housing and jobs set a TARGET that migration chases rather than a ceiling
> the city snaps to. That is where the city's inertia comes from, and it is the
> whole point: a tower fills over months, a closed plant does not evaporate its
> workers, and a bust takes a year to begin.
> 
> =================================================================
> 
> THE MECHANIC
> 
> Every month each band gives up `1/spanMonths` of itself to the next one, and
> seniors give theirs up to nothing. Births arrive at the bottom. That is a
> compartment model, and its known cost is that residence time is exponential
> rather than fixed - see AgeBand.monthlyOutflowRate(), where the numbers are.
> 
> Held as doubles, not ints. Rounding 1/444 of an adult population to a whole
> person every month would round almost every transfer to zero in a small city
> and leak people steadily in a large one; the pyramid is a distribution and it
> is allowed to hold fractions. Only the DISPLAY rounds.

**Uses:** [AgeBand](AgeBand.md) (45)

**Used by (34):** [ArrivalMixCheck](ArrivalMixCheck.md), [BuildAdvice](BuildAdvice.md), [CareType](CareType.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [Crime](Crime.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Education](Education.md), [FamilyModel](FamilyModel.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [Inbox](Inbox.md), [KeptCheck](KeptCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SavedCities](SavedCities.md), [ServicesScreen](ServicesScreen.md), [Sickness](Sickness.md), [SicknessCheck](SicknessCheck.md), [StaffingDrawCheck](StaffingDrawCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 116 | · reading |
| 190 | · ageing |
| 516 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `PopulationCohorts.BIRTHS_PER_1000_PER_YEAR` | `15.0` | Births per thousand residents per year. |
| 558 | `PopulationCohorts.LEGACY_BANDS` | `{ "BABY", "CHILD", "TEEN", "ADULT", "SENIOR" }` | The bands a save written before names existed must be read with. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private final double[] band` |  |
| 64 | `private double lastBirths` | Everything that happened last month, for the screen. |
| 65 | `private double lastDeaths` |  |
| 66 | `private double lastMigration` |  |
| 67 | `private final double[] lastPromoted` |  |
| 68 | `private final double[] lastDeathsByBand` |  |
| 70 | `private final double[] lastIllnessDeathsByBand` | ...of which sickness: the share of each band's deaths its extra rate carried. |
| 72 | `private final double[] lastDyingByBand` | ...and of which the band's mortality at all, as opposed to ageing out of the top at 120. |
| 74 | `private final double[] lastKilledByBand` | ...and of which violence: the killed. |
| 76 | `private final double[] lastCauseDeathsByBand` | ...and of which the causes of death of 1900: typhoid, TB and the rest. |
| 88 | `private double lastKilled` | THE MONTH'S DEAD BY CAUSE, SUMMED AND SAVED (0.7.27). |
| 89 | `private double lastDeathsOfAge` |  |
| 90 | `private double lastAgedOut` |  |
| 96 | `private double lastCauses` | ...and a fourth since R0b: the dead of the causes of 1900 (CausesOfDeath), every band together, saved like the killed. |
| 108 | `private final double[] lastOpening` | ...AND THE MONTH'S SHARES OF EACH BAND, FOR THE HOUSEHOLDS' FLOWS (HS1, 2026-11-18). |
| 110 | `private double lastLeftShare` | The share of every band the month's net migration took out of the city, when it was out; 0 when it brought people in. |
| 112 | `private final double[] lastLeftByBand` | People of each band who left on their own account (leave(): the evicted who give up). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 650 | **type** `public class PopulationCohorts` | The city's age pyramid, and since the switch, the city's POPULATION. |
| 114 | 1 | `public PopulationCohorts()` |  |

### reading (lines 116-189)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 1 | `public double get(AgeBand b)` |  |
| 119 | 1 | `public double getLastBirths()` |  |
| 120 | 1 | `public double getLastDeaths()` |  |
| 121 | 1 | `public double getLastMigration()` |  |
| 122 | 1 | `public double getPromoted(AgeBand b)` |  |
| 123 | 1 | `public double getDeaths(AgeBand b)` |  |
| 125 | 1 | `public double getIllnessDeaths(AgeBand b)` | Last month's deaths in a band that sickness caused. |
| 126 | 1 | `public double[] getIllnessDeaths()` |  |
| 128 | 1 | `public double getDying(AgeBand b)` | Last month's deaths in a band from its mortality - everything but the seniors who aged out at 120. |
| 130 | 1 | `public double getKilled(AgeBand b)` | Last month's deaths in a band that were killings. |
| 131 | 1 | `public double[] getKilled()` |  |
| 133 | 1 | `public double getLastKilled()` | Last month's killings, every band together - saved, unlike the bands (0.7.27). |
| 135 | 1 | `public double getLastDeathsOfAge()` | Last month's deaths of the bands' own mortality: the dying, less illness and violence (0.7.27). |
| 137 | 1 | `public double getLastAgedOut()` | Last month's deaths out of the top of the pyramid at 120: the deaths that are not dying (0.7.27). |
| 139 | 1 | `public double getCauseDeaths(AgeBand b)` | Last month's deaths in a band that were a cause of 1900's: typhoid, TB and the rest (R0b). |
| 140 | 1 | `public double[] getCauseDeaths()` |  |
| 142 | 1 | `public double getLastCauseDeaths()` | Last month's deaths of the causes of 1900, every band together - saved, unlike the bands (R0b). |
| 144 | 1 | `public double deathShare(AgeBand b)` | Of what the band opened the month with, the share that died - aged out at 120 included (HS1). |
| 146 | 3 | `public double promotedShare(AgeBand b)` | ...and the share that aged into the next band: for the adults, the month's retirements (HS1). |
| 150 | 1 | `public double getLeftShare()` | The share of every band the month's net migration took out of the city: 0 in a month it brought people (HS1). |
| 152 | 1 | `public double getLeft(AgeBand b)` | People of a band who left on their own account this month (leave(); HS1). |
| 154 | 1 | `private static double share(double part, double of)` |  |
| 156 | 5 | `public double total()` |  |
| 163 | 4 | `public double share(AgeBand b)` | Share of the city in this band, 0-1. |
| 169 | 7 | `public double workingAge()` | Everyone of working age. |
| 184 | 5 | `public double dependencyRatio()` | Children and seniors per hundred working-age adults. |

### ageing (lines 190-515)

| line | len | member | says |
|---:|---:|---|---|
| 205 | 3 | `public void advanceMonth()` | One month of ageing. |
| 228 | 3 | `public void advanceMonth(double[] mortalityFactor)` | The same month, with healthcare's hand on the death rate. |
| 241 | 3 | `public void advanceMonth(double[] mortalityFactor, double birthFactor)` | The same month, with healthcare's hand on BOTH ends of a life. |
| 258 | 3 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double birthFactor)` | The same month, with the people who stayed sick dying as well. |
| 271 | 4 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double[] violence, double birthFactor)` | ...and the people violence killed (2026-09-11). |
| 285 | 106 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double[] violence, double[] causes, double birthFactor)` | ...and the people the causes of death of 1900 took (R0b). |
| 408 | 20 | `public void migrate(double netArrivals)` | People moving in, or out. |
| 437 | 9 | `public double leave(AgeBand of, double people)` | People of one band leaving the city on their own account - not the proportional migration above. |
| 475 | 7 | `private void seedFrom(long livePopulation)` | Gives an empty pyramid the shape a settled population of this size has. |
| 494 | 6 | `public static double equilibriumShare(AgeBand of)` | What share of a settled city sits in this band, solved from the rates. |
| 501 | 14 | `private static double[] equilibriumWeights()` |  |

### saving (lines 516-689)

| line | len | member | says |
|---:|---:|---|---|
| 561 | 6 | `public static String[] saveBands()` | The names this build would write beside the pyramid. |
| 568 | 14 | `public double[] toSaveArray()` |  |
| 584 | 3 | `public void restore(double[] saved)` | A pyramid saved before the names travelled with it. |
| 601 | 62 | `public void restore(String[] bands, double[] saved)` | Puts a saved pyramid back, each band found BY NAME. |
| 665 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 671 | 18 | `public void reset()` |  |

