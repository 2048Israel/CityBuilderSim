# PopulationCohorts.java - 561 lines · 32 methods · 2 constants · model

`ham/citybuildersim/PopulationCohorts.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Used by (21):** [CareType](CareType.md), [Crime](Crime.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Education](Education.md), [FamilyModel](FamilyModel.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HistorySave](HistorySave.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [Inbox](Inbox.md), [Migration](Migration.md), [OutsideCheck](OutsideCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [ServicesScreen](ServicesScreen.md), [Sickness](Sickness.md), [SicknessCheck](SicknessCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 78 | · reading |
| 129 | · ageing |
| 420 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `PopulationCohorts.BIRTHS_PER_1000_PER_YEAR` | `15.0` | Births per thousand residents per year. |
| 462 | `PopulationCohorts.LEGACY_BANDS` | `{ "BABY", "CHILD", "TEEN", "ADULT", "SENIOR" }` | The bands a save written before names existed must be read with. |

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

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 522 | **type** `public class PopulationCohorts` | The city's age pyramid, and since the switch, the city's POPULATION. |
| 76 | 1 | `public PopulationCohorts()` |  |

### reading (lines 78-128)

| line | len | member | says |
|---:|---:|---|---|
| 80 | 1 | `public double get(AgeBand b)` |  |
| 81 | 1 | `public double getLastBirths()` |  |
| 82 | 1 | `public double getLastDeaths()` |  |
| 83 | 1 | `public double getLastMigration()` |  |
| 84 | 1 | `public double getPromoted(AgeBand b)` |  |
| 85 | 1 | `public double getDeaths(AgeBand b)` |  |
| 87 | 1 | `public double getIllnessDeaths(AgeBand b)` | Last month's deaths in a band that sickness caused. |
| 88 | 1 | `public double[] getIllnessDeaths()` |  |
| 90 | 1 | `public double getDying(AgeBand b)` | Last month's deaths in a band from its mortality - everything but the seniors who aged out at 120. |
| 92 | 1 | `public double getKilled(AgeBand b)` | Last month's deaths in a band that were killings. |
| 93 | 1 | `public double[] getKilled()` |  |
| 95 | 5 | `public double total()` |  |
| 102 | 4 | `public double share(AgeBand b)` | Share of the city in this band, 0-1. |
| 108 | 7 | `public double workingAge()` | Everyone of working age. |
| 123 | 5 | `public double dependencyRatio()` | Children and seniors per hundred working-age adults. |

### ageing (lines 129-419)

| line | len | member | says |
|---:|---:|---|---|
| 144 | 3 | `public void advanceMonth()` | One month of ageing. |
| 167 | 3 | `public void advanceMonth(double[] mortalityFactor)` | The same month, with healthcare's hand on the death rate. |
| 180 | 3 | `public void advanceMonth(double[] mortalityFactor, double birthFactor)` | The same month, with healthcare's hand on BOTH ends of a life. |
| 197 | 3 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double birthFactor)` | The same month, with the people who stayed sick dying as well. |
| 210 | 87 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double[] violence, double birthFactor)` | ...and the people violence killed (2026-09-11). |
| 314 | 19 | `public void migrate(double netArrivals)` | People moving in, or out. |
| 342 | 8 | `public double leave(AgeBand of, double people)` | People of one band leaving the city on their own account - not the proportional migration above. |
| 379 | 7 | `private void seedFrom(int livePopulation)` | Gives an empty pyramid the shape a settled population of this size has. |
| 398 | 6 | `public static double equilibriumShare(AgeBand of)` | What share of a settled city sits in this band, solved from the rates. |
| 405 | 14 | `private static double[] equilibriumWeights()` |  |

### saving (lines 420-561)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 6 | `public static String[] saveBands()` | The names this build would write beside the pyramid. |
| 472 | 8 | `public double[] toSaveArray()` |  |
| 482 | 3 | `public void restore(double[] saved)` | A pyramid saved before the names travelled with it. |
| 497 | 46 | `public void restore(String[] bands, double[] saved)` | Puts a saved pyramid back, each band found BY NAME. |
| 545 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 551 | 10 | `public void reset()` |  |

