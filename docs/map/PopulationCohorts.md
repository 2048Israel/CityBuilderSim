# PopulationCohorts.java - 845 lines · 54 methods · 2 constants · model

`ham/citybuildersim/PopulationCohorts.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (59), [AdultStage](AdultStage.md) (27), [HouseholdBalance](HouseholdBalance.md) (1)

**Used by (46):** [ArrivalMixCheck](ArrivalMixCheck.md), [AshbourneCheck](AshbourneCheck.md), [AutoBuildCheck](AutoBuildCheck.md), [BuildAdvice](BuildAdvice.md), [CardCheck](CardCheck.md), [CareType](CareType.md), [CausesOfDeathCheck](CausesOfDeathCheck.md), [ChildcareCheck](ChildcareCheck.md), [CityNeeds](CityNeeds.md), [Crime](Crime.md), [CrimeCheck](CrimeCheck.md), [DeathRecordCheck](DeathRecordCheck.md), [Education](Education.md), [EducationCheck](EducationCheck.md), [EssentialWorksCheck](EssentialWorksCheck.md), [FamilyModel](FamilyModel.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [Healthcare](Healthcare.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [HouseholdMemoryCheck](HouseholdMemoryCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [HousingCheck](HousingCheck.md), [HousingMarketCheck](HousingMarketCheck.md), [Inbox](Inbox.md), [KeptCheck](KeptCheck.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [Migration](Migration.md), [MoneyWordsCheck](MoneyWordsCheck.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [PopulationCheck](PopulationCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SavedCities](SavedCities.md), [SchoolingLadderCheck](SchoolingLadderCheck.md), [ServicesScreen](ServicesScreen.md), [Sickness](Sickness.md), [SicknessCheck](SicknessCheck.md), [StaffingDrawCheck](StaffingDrawCheck.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md), [WorksFinanceCheck](WorksFinanceCheck.md)

## Sections

| line | section |
|---:|---|
| 131 | · reading |
| 227 | · ageing |
| 644 | · saving |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `PopulationCohorts.BIRTHS_PER_1000_PER_YEAR` | `15.0` | Births per thousand residents per year. |
| 686 | `PopulationCohorts.LEGACY_BANDS` | `{ "BABY", "CHILD", "TEEN", "ADULT", "SENIOR" }` | The bands a save written before names existed must be read with. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `private final double[] band` |  |
| 72 | `private final double[] stage` | THE ADULTS IN TWO STAGES (HS2, 2026-11-19; AdultStage's note). |
| 74 | `private final double[] lastStageOpening` | What each stage opened the month with, moved on with (the young to the established, the established to the seniors) and lost to death. |
| 75 | `private final double[] lastStagePromoted` |  |
| 76 | `private final double[] lastStageDeaths` |  |
| 79 | `private double lastBirths` | Everything that happened last month, for the screen. |
| 80 | `private double lastDeaths` |  |
| 81 | `private double lastMigration` |  |
| 82 | `private final double[] lastPromoted` |  |
| 83 | `private final double[] lastDeathsByBand` |  |
| 85 | `private final double[] lastIllnessDeathsByBand` | ...of which sickness: the share of each band's deaths its extra rate carried. |
| 87 | `private final double[] lastDyingByBand` | ...and of which the band's mortality at all, as opposed to ageing out of the top at 120. |
| 89 | `private final double[] lastKilledByBand` | ...and of which violence: the killed. |
| 91 | `private final double[] lastCauseDeathsByBand` | ...and of which the causes of death of 1900: typhoid, TB and the rest. |
| 103 | `private double lastKilled` | THE MONTH'S DEAD BY CAUSE, SUMMED AND SAVED (0.7.27). |
| 104 | `private double lastDeathsOfAge` |  |
| 105 | `private double lastAgedOut` |  |
| 111 | `private double lastCauses` | ...and a fourth since R0b: the dead of the causes of 1900 (CausesOfDeath), every band together, saved like the killed. |
| 123 | `private final double[] lastOpening` | ...AND THE MONTH'S SHARES OF EACH BAND, FOR THE HOUSEHOLDS' FLOWS (HS1, 2026-11-18). |
| 125 | `private double lastLeftShare` | The share of every band the month's net migration took out of the city, when it was out; 0 when it brought people in. |
| 127 | `private final double[] lastLeftByBand` | People of each band who left on their own account (leave(): the evicted who give up). |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 806 | **type** `public class PopulationCohorts` | The city's age pyramid, and since the switch, the city's POPULATION. |
| 129 | 1 | `public PopulationCohorts()` |  |

### reading (lines 131-226)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 1 | `public double get(AgeBand b)` |  |
| 134 | 1 | `public double getLastBirths()` |  |
| 135 | 1 | `public double getLastDeaths()` |  |
| 136 | 1 | `public double getLastMigration()` |  |
| 137 | 1 | `public double getPromoted(AgeBand b)` |  |
| 138 | 1 | `public double getDeaths(AgeBand b)` |  |
| 140 | 1 | `public double getIllnessDeaths(AgeBand b)` | Last month's deaths in a band that sickness caused. |
| 141 | 1 | `public double[] getIllnessDeaths()` |  |
| 143 | 1 | `public double getDying(AgeBand b)` | Last month's deaths in a band from its mortality - everything but the seniors who aged out at 120. |
| 145 | 1 | `public double getKilled(AgeBand b)` | Last month's deaths in a band that were killings. |
| 146 | 1 | `public double[] getKilled()` |  |
| 148 | 1 | `public double getLastKilled()` | Last month's killings, every band together - saved, unlike the bands (0.7.27). |
| 150 | 1 | `public double getLastDeathsOfAge()` | Last month's deaths of the bands' own mortality: the dying, less illness and violence (0.7.27). |
| 152 | 1 | `public double getLastAgedOut()` | Last month's deaths out of the top of the pyramid at 120: the deaths that are not dying (0.7.27). |
| 154 | 1 | `public double getCauseDeaths(AgeBand b)` | Last month's deaths in a band that were a cause of 1900's: typhoid, TB and the rest (R0b). |
| 155 | 1 | `public double[] getCauseDeaths()` |  |
| 157 | 1 | `public double getLastCauseDeaths()` | Last month's deaths of the causes of 1900, every band together - saved, unlike the bands (R0b). |
| 159 | 1 | `public double deathShare(AgeBand b)` | Of what the band opened the month with, the share that died - aged out at 120 included (HS1). |
| 161 | 3 | `public double promotedShare(AgeBand b)` | ...and the share that aged into the next band: for the adults, the month's retirements (HS1). |
| 165 | 1 | `public double getLeftShare()` | The share of every band the month's net migration took out of the city: 0 in a month it brought people (HS1). |
| 167 | 1 | `public double getLeft(AgeBand b)` | People of a band who left on their own account this month (leave(); HS1). |
| 169 | 1 | `private static double share(double part, double of)` |  |
| 172 | 1 | `public double get(AdultStage s)` | The adults of one stage (HS2): get(AgeBand.ADULT) is the two together. |
| 174 | 1 | `public double getPromoted(AdultStage s)` | Last month's adults who moved on out of a stage: the young who turned 35, the established who retired (the same as getPromoted(ADULT)). |
| 176 | 1 | `public double getDeaths(AdultStage s)` | Last month's deaths in a stage: the two add to getDeaths(ADULT). |
| 178 | 3 | `public double promotedShare(AdultStage s)` | Of what the stage opened the month with, the share that moved on: for the young, the month's turning 35; for the established, its retirements (HS2). |
| 182 | 1 | `public double getYoungShareOfAdults()` | Of the adults, the young's share: a no-argument reading for the ensemble's extra columns (HS2; the settled city's is settledYoungShareOfAdults()). |
| 184 | 1 | `public double getRetiringShareOfAdults()` | Last month's retirements over the adults who opened it: the ensemble's reading of the two stages' flow beside the one bucket's flat AgeBand.ADULT.monthlyOutflowRate() (HS2). |
| 187 | 5 | `private void syncAdults()` | The band's figure struck again from its two stages, after anything moved them. |
| 193 | 5 | `public double total()` |  |
| 200 | 4 | `public double share(AgeBand b)` | Share of the city in this band, 0-1. |
| 206 | 7 | `public double workingAge()` | Everyone of working age. |
| 221 | 5 | `public double dependencyRatio()` | Children and seniors per hundred working-age adults. |

### ageing (lines 227-643)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 3 | `public void advanceMonth()` | One month of ageing. |
| 265 | 3 | `public void advanceMonth(double[] mortalityFactor)` | The same month, with healthcare's hand on the death rate. |
| 278 | 3 | `public void advanceMonth(double[] mortalityFactor, double birthFactor)` | The same month, with healthcare's hand on BOTH ends of a life. |
| 295 | 3 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double birthFactor)` | The same month, with the people who stayed sick dying as well. |
| 308 | 4 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double[] violence, double birthFactor)` | ...and the people violence killed (2026-09-11). |
| 322 | 139 | `public void advanceMonth(double[] mortalityFactor, double[] illness, double[] violence, double[] causes, double birthFactor)` | ...and the people the causes of death of 1900 took (R0b). |
| 463 | 8 | `public void migrate(double netArrivals, HouseholdBalance.BankruptcyDepartures discharged)` | Discharged source families leave first; only the other net movers use population shares. |
| 488 | 25 | `public void migrate(double netArrivals)` | People moving in, or out. |
| 522 | 16 | `public double leave(AgeBand of, double people)` | People of one band leaving the city on their own account - not the proportional migration above. |
| 567 | 11 | `private void seedFrom(long livePopulation)` | Gives an empty pyramid the shape a settled population of this size has. |
| 590 | 6 | `public static double equilibriumShare(AgeBand of)` | What share of a settled city sits in this band, solved from the rates. |
| 598 | 3 | `public static double settledYoungShareOfAdults()` | Of a settled city's adults, the young's share (HS2): what the People screen sets beside the city's own, and how a save without the stages splits its adults. |
| 603 | 6 | `public static double equilibriumShare(AdultStage of)` | ...and what share of a settled city sits in one adult stage (HS2): the two add to the adults'. |
| 617 | 26 | `private static double[] equilibriumWeights()` | The bands' weights, then the adult stages' (HS2). |

### saving (lines 644-845)

| line | len | member | says |
|---:|---:|---|---|
| 689 | 6 | `public static String[] saveBands()` | The names this build would write beside the pyramid. |
| 696 | 19 | `public double[] toSaveArray()` |  |
| 717 | 3 | `public void restore(double[] saved)` | A pyramid saved before the names travelled with it. |
| 735 | 80 | `public void restore(String[] bands, double[] saved)` | Puts a saved pyramid back, each band found BY NAME. |
| 817 | 5 | `private static AgeBand bandNamed(String name)` | The band of that name, or null if this build has no such band. |
| 823 | 22 | `public void reset()` |  |

