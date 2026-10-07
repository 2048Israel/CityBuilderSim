# PopulationCohorts.java - 604 lines · 35 methods · 2 constants · model

`ham/citybuildersim/PopulationCohorts.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (37)

**Used by (26):** [BuildAdvice](BuildAdvice.md), [CareType](CareType.md), [CityNeeds](CityNeeds.md), [Crime](Crime.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Education](Education.md), [FamilyModel](FamilyModel.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HistorySave](HistorySave.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [ServicesScreen](ServicesScreen.md), [Sickness](Sickness.md), [SicknessCheck](SicknessCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 92 | · reading |
| 149 | · ageing |
| 446 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `PopulationCohorts.BIRTHS_PER_1000_PER_YEAR` | `15.0` | Births per thousand residents per year. |
| 488 | `PopulationCohorts.LEGACY_BANDS` | `{ "BABY", "CHILD", "TEEN", "ADULT", "SENIOR" }` | The bands a save written before names existed must be read with. |

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
| 86 | `private double lastKilled` | THE MONTH'S DEAD BY CAUSE, SUMMED AND SAVED (0.7.27). |
| 87 | `private double lastDeathsOfAge` |  |
| 88 | `private double lastAgedOut` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 565 | **type** `public class PopulationCohorts` | The city's age pyramid, and since the switch, the city's POPULATION. |
| 90 | 1 | `public PopulationCohorts()` |  |

### reading (lines 92-148)

| line | len | member | says |
|---:|---:|---|---|
| 94 | 1 | `public double get(AgeBand b)` |  |
| 95 | 1 | `public double getLastBirths()` |  |
| 96 | 1 | `public double getLastDeaths()` |  |
| 97 | 1 | `public double getLastMigration()` |  |
| 98 | 1 | `public double getPromoted(AgeBand b)` |  |
| 99 | 1 | `public double getDeaths(AgeBand b)` |  |
| 101 | 1 | `public double getIllnessDeaths(AgeBand b)` | Last month's deaths in a band that sickness caused. |
| 102 | 1 | `public double[] getIllnessDeaths()` |  |
| 104 | 1 | `public double getDying(AgeBand b)` | Last month's deaths in a band from its mortality - everything but the seniors who aged out at 120. |
| 106 | 1 | `public double getKilled(AgeBand b)` | Last month's deaths in a band that were killings. |
| 107 | 1 | `public double[] getKilled()` |  |
| 109 | 1 | `public double getLastKilled()` | Last month's killings, every band together - saved, unlike the bands (0.7.27). |
| 111 | 1 | `public double getLastDeathsOfAge()` | Last month's deaths of the bands' own mortality: the dying, less illness and violence (0.7.27). |
| 113 | 1 | `public double getLastAgedOut()` | Last month's deaths out of the top of the pyramid at 120: the deaths that are not dying (0.7.27). |
| 115 | 5 | `public double total()` |  |
| 122 | 4 | `public double share(AgeBand b)` | Share of the city in this band, 0-1. |
| 128 | 7 | `public double workingAge()` | Everyone of working age. |
| 143 | 5 | `public double dependencyRatio()` | Children and seniors per hundred working-age adults. |

### ageing (lines 149-445)

| line | len | member | says |
|---:|---:|---|---|
| 164 | 3 | `public void advanceMonth()` | One month of ageing. |
| 187 | 3 | `public void advanceMonth(double[] mortalityFactor)` | The same month, with healthcare's hand on the death rate. |
| 200 | 3 | `public void advanceMonth(double[] mortalityFactor, double birthFactor)` | The same month, with healthcare's hand on BOTH ends of a life. |
| 217 | 3 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double birthFactor)` | The same month, with the people who stayed sick dying as well. |
| 230 | 93 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double[] violence, double birthFactor)` | ...and the people violence killed (2026-09-11). |
| 340 | 19 | `public void migrate(double netArrivals)` | People moving in, or out. |
| 368 | 8 | `public double leave(AgeBand of, double people)` | People of one band leaving the city on their own account - not the proportional migration above. |
| 405 | 7 | `private void seedFrom(long livePopulation)` | Gives an empty pyramid the shape a settled population of this size has. |
| 424 | 6 | `public static double equilibriumShare(AgeBand of)` | What share of a settled city sits in this band, solved from the rates. |
| 431 | 14 | `private static double[] equilibriumWeights()` |  |

### saving (lines 446-604)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 6 | `public static String[] saveBands()` | The names this build would write beside the pyramid. |
| 498 | 12 | `public double[] toSaveArray()` |  |
| 512 | 3 | `public void restore(double[] saved)` | A pyramid saved before the names travelled with it. |
| 528 | 55 | `public void restore(String[] bands, double[] saved)` | Puts a saved pyramid back, each band found BY NAME. |
| 585 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 591 | 13 | `public void reset()` |  |

