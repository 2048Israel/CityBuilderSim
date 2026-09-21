# TimeSkipReport.java - 463 lines · 56 methods · 0 constants · model

`ham/citybuildersim/TimeSkipReport.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> What happened while you were not watching.
> 
> Fast-forwarding a hundred months is the normal way to play this game, and
> until now it told you nothing: the screen said "100 of 100 months simulated"
> and left you to work out what had changed by comparing numbers you had not
> written down. Worse, anything that reports itself for a while and then expires
> - the demolition log keeps entries for twenty-four months - could happen and
> disappear entirely inside a single skip.
> 
> So this takes a snapshot before the jump, samples every month during it, and
> diffs against a snapshot after.
> 
> TWO KINDS OF THING
> 
>   DELTAS come from the two snapshots: population, cash, output, debt, land,
>   what got built and what got demolished. Cheap and exact.
> 
>   EPISODES come from the monthly samples: how many months the city ran short
>   of power, how long it sat with no land, how often it had to issue emergency
>   debt. These cannot be reconstructed from the endpoints, because a city that
>   starves for fifty months and recovers looks identical at both ends to one
>   that never had a problem.
> 
> Nothing here computes anything the simulation does not already know. It reads
> finished figures and remembers them, so no screen built on it can move a
> single number in the game.

**Used by (4):** [Game](Game.md), [HealthCheck](HealthCheck.md), [SkipReportCheck](SkipReportCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 68 | · episodes |
| 108 | · capture |
| 223 | · deltas |
| 225 | · health, sampled |
| 301 | · buildings |
| 365 | · episodes |

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `int month` |  |
| 42 | `double cash` |  |
| 43 | `int population` |  |
| 44 | `int housing` |  |
| 45 | `int jobs` |  |
| 46 | `double monthlyGdp` |  |
| 47 | `double annualGdp` |  |
| 48 | `double cityDebt` |  |
| 49 | `double businessDebt` |  |
| 50 | `double landOwnedBlocks` |  |
| 51 | `double landUtilisation` |  |
| 52 | `double householdSavingRate` |  |
| 53 | `double householdRentBurden` |  |
| 54 | `double cumulativeWriteOffs` |  |
| 56 | `final Map<String, Integer> buildings` |  |
| 59 | `private Snapshot before` |  |
| 60 | `private Snapshot after` |  |
| 62 | `private boolean complete` |  |
| 65 | `private int requested` | How many months were asked for, and how many actually ran. |
| 66 | `private int completed` |  |
| 69 | `private int monthsShortOfPower` |  |
| 70 | `private int monthsShortOfWater` |  |
| 80 | `private int monthsCongested` | Months the road network could not carry the traffic on it. |
| 81 | `private int monthsOutOfLand` |  |
| 82 | `private int monthsHouseholdsShort` |  |
| 83 | `private int monthsNothingBuilt` |  |
| 85 | `private double worstEnergyRatio` |  |
| 86 | `private double worstRoadRatio` |  |
| 87 | `private int peakPopulation` |  |
| 227 | `private int outbreaks` |  |
| 228 | `private int monthsInOutbreak` |  |
| 229 | `private boolean wasOutbreak` |  |
| 230 | `private int monthsSick` |  |
| 231 | `private double worstWorkRatio` |  |
| 232 | `private double peakUnburied` |  |
| 305 | `public final String name` |  |
| 306 | `public final int change` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 36 | 428 | **type** `public class TimeSkipReport` | What happened while you were not watching. |
| 39 | 19 | **type** `private static class Snapshot` | A city, at one instant. |

### episodes (lines 68-107)

| line | len | member | says |
|---:|---:|---|---|
| 89 | 18 | `public void beginSkip(int requested)` |  |

### capture (lines 108-222)

| line | len | member | says |
|---:|---:|---|---|
| 118 | 37 | `public void snapshot(boolean atStart, int month, double cash, int population, int housing, int jobs, double monthlyGdp, double ...` | Records the state at one instant. |
| 164 | 6 | `public void sampleMonth(double energyRatio, double waterRatio, double landAvailableSqFt, boolean householdsShort, boolean anyth...` | One month of the skip, as it goes past. |
| 172 | 6 | `public void sampleMonth(double energyRatio, double waterRatio, double roadRatio, double landAvailableSqFt, boolean householdsSh...` |  |
| 193 | 29 | `public void sampleMonth(double energyRatio, double waterRatio, double roadRatio, double landAvailableSqFt, boolean householdsSh...` | The same, plus the month's health. |

### deltas (lines 223-224)

### health, sampled (lines 225-300)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 1 | `public int getOutbreaks()` | How many separate epidemics started during the skip. |
| 236 | 1 | `public int getMonthsInOutbreak()` |  |
| 237 | 1 | `public int getMonthsSick()` |  |
| 239 | 1 | `public double getWorstWorkRatio()` | The worst single month, as a share of work done. |
| 240 | 1 | `public double getPeakUnburied()` |  |
| 242 | 1 | `public boolean isComplete()` |  |
| 243 | 1 | `public int getRequested()` |  |
| 244 | 1 | `public int getCompleted()` |  |
| 245 | 1 | `public boolean stoppedEarly()` |  |
| 247 | 1 | `public int getStartMonth()` |  |
| 248 | 1 | `public int getEndMonth()` |  |
| 250 | 1 | `public double getCashChange()` |  |
| 251 | 1 | `public double getPopulationChange()` |  |
| 252 | 1 | `public double getHousingChange()` |  |
| 253 | 1 | `public double getJobsChange()` |  |
| 254 | 1 | `public double getMonthlyGdpChange()` |  |
| 255 | 1 | `public double getAnnualGdpChange()` |  |
| 256 | 1 | `public double getCityDebtChange()` |  |
| 257 | 1 | `public double getBusinessDebtChange()` |  |
| 258 | 1 | `public double getLandBlocksBought()` |  |
| 260 | 1 | `public double getStartCash()` |  |
| 261 | 1 | `public double getEndCash()` |  |
| 262 | 1 | `public int getStartPopulation()` |  |
| 263 | 1 | `public int getEndPopulation()` |  |
| 264 | 1 | `public double getEndLandUtilisation()` |  |
| 265 | 1 | `public double getEndSavingRate()` |  |
| 266 | 1 | `public double getEndRentBurden()` |  |
| 267 | 1 | `public double getStartSavingRate()` |  |
| 270 | 3 | `public double getWriteOffsDuringSkip()` | Debt the lenders wrote off during the skip. |
| 275 | 3 | `public double getCashPerMonth()` | Cash per month, which is the number that says whether this is sustainable. |
| 279 | 3 | `public double getPopulationPerMonth()` |  |
| 284 | 7 | `public double getPopulationGrowthRate()` | Population growth over the whole skip, annualised. |
| 292 | 1 | **type** `private interface Field` |  |
| 292 | 1 | `double of(Snapshot s)` _(in TimeSkipReport.Field)_ |  |
| 294 | 6 | `private double delta(Field f)` |  |

### buildings (lines 301-364)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 11 | **type** `public static class BuildingChange` | A building type whose count moved, and by how much. |
| 308 | 4 | `BuildingChange(String name, int change)` _(in TimeSkipReport.BuildingChange)_ |  |
| 313 | 1 | `public boolean isGain()` _(in TimeSkipReport.BuildingChange)_ |  |
| 325 | 23 | `public List<BuildingChange> getBuildingChanges()` | Everything whose count moved, biggest change first. |
| 349 | 7 | `public int getBuildingsGained()` |  |
| 357 | 7 | `public int getBuildingsLost()` |  |

### episodes (lines 365-463)

| line | len | member | says |
|---:|---:|---|---|
| 367 | 1 | `public int getMonthsShortOfPower()` |  |
| 368 | 1 | `public int getMonthsShortOfWater()` |  |
| 369 | 1 | `public int getMonthsCongested()` |  |
| 370 | 1 | `public double getWorstRoadRatio()` |  |
| 371 | 1 | `public int getMonthsOutOfLand()` |  |
| 372 | 1 | `public int getMonthsHouseholdsShort()` |  |
| 373 | 1 | `public int getMonthsNothingBuilt()` |  |
| 374 | 1 | `public double getWorstEnergyRatio()` |  |
| 375 | 1 | `public int getPeakPopulation()` |  |
| 378 | 3 | `public boolean shrankFromPeak()` | True when the city ended smaller than its high-water mark. |
| 383 | 3 | `public double getIdleShare()` | Share of the skip spent with nothing on any building site. |
| 395 | 68 | `public List<String> getHeadlines()` | The things worth putting in front of the player, in plain sentences. |

