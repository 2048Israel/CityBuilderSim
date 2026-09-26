# TimeSkipReport.java - 509 lines · 60 methods · 0 constants · model

`ham/citybuildersim/TimeSkipReport.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

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
>   of power or water, how long it sat with no land, how many months its
>   households went short - and since 0.7.1 how many months the treasury
>   lived on the central bank's advances and how many of those at their
>   ceiling, which is what replaced the emergency debt this line once said it
>   counted (it never did; the note is gone since 0.7.0). These cannot be
>   reconstructed from the endpoints, because a
>   city that starves for fifty months and recovers looks identical at both
>   ends to one that never had a problem.
> 
> Nothing here computes anything the simulation does not already know. It reads
> finished figures and remembers them, so no screen built on it can move a
> single number in the game.

**Uses:** [Bank](Bank.md) (1)

**Used by (4):** [Game](Game.md), [HealthCheck](HealthCheck.md), [SkipReportCheck](SkipReportCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 72 | · episodes |
| 118 | · capture |
| 247 | · deltas |
| 249 | · health, sampled |
| 340 | · buildings |
| 404 | · episodes |

## Fields (state)

| line | field | says |
|---:|---|---|
| 45 | `int month` |  |
| 46 | `double cash` |  |
| 47 | `int population` |  |
| 48 | `int housing` |  |
| 49 | `int jobs` |  |
| 50 | `double monthlyGdp` |  |
| 51 | `double annualGdp` |  |
| 52 | `double cityDebt` |  |
| 53 | `double businessDebt` |  |
| 54 | `double landOwnedBlocks` |  |
| 55 | `double landUtilisation` |  |
| 56 | `double householdSavingRate` |  |
| 57 | `double householdRentBurden` |  |
| 58 | `double cumulativeWriteOffs` |  |
| 60 | `final Map<String, Integer> buildings` |  |
| 63 | `private Snapshot before` |  |
| 64 | `private Snapshot after` |  |
| 66 | `private boolean complete` |  |
| 69 | `private int requested` | How many months were asked for, and how many actually ran. |
| 70 | `private int completed` |  |
| 73 | `private int monthsShortOfPower` |  |
| 74 | `private int monthsShortOfWater` |  |
| 84 | `private int monthsCongested` | Months the road network could not carry the traffic on it. |
| 85 | `private int monthsOutOfLand` |  |
| 86 | `private int monthsHouseholdsShort` |  |
| 87 | `private int monthsNothingBuilt` |  |
| 90 | `private int monthsOnAdvances` | Months the treasury owed the central bank advances at the month's end, and of those, months it owed them at the ceiling (0.7.1). |
| 91 | `private int monthsAtCeiling` |  |
| 93 | `private double worstEnergyRatio` |  |
| 94 | `private double worstRoadRatio` |  |
| 95 | `private int peakPopulation` |  |
| 251 | `private int outbreaks` |  |
| 252 | `private int monthsInOutbreak` |  |
| 253 | `private boolean wasOutbreak` |  |
| 254 | `private int monthsSick` |  |
| 255 | `private double worstWorkRatio` |  |
| 256 | `private double peakUnburied` |  |
| 344 | `public final String name` |  |
| 345 | `public final int change` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 40 | 470 | **type** `public class TimeSkipReport` | What happened while you were not watching. |
| 43 | 19 | **type** `private static class Snapshot` | A city, at one instant. |

### episodes (lines 72-117)

| line | len | member | says |
|---:|---:|---|---|
| 97 | 20 | `public void beginSkip(int requested)` |  |

### capture (lines 118-246)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 37 | `public void snapshot(boolean atStart, int month, double cash, int population, int housing, int jobs, double monthlyGdp, double ...` | Records the state at one instant. |
| 174 | 6 | `public void sampleMonth(double energyRatio, double waterRatio, double landAvailableSqFt, boolean householdsShort, boolean anyth...` | One month of the skip, as it goes past. |
| 182 | 6 | `public void sampleMonth(double energyRatio, double waterRatio, double roadRatio, double landAvailableSqFt, boolean householdsSh...` |  |
| 203 | 29 | `public void sampleMonth(double energyRatio, double waterRatio, double roadRatio, double landAvailableSqFt, boolean householdsSh...` | The same, plus the month's health. |
| 239 | 4 | `public void sampleTreasury(boolean onAdvances, boolean atCeiling)` | The treasury's month with its central bank (0.7.1): whether it ended the month owing advances, and whether they stood at the ceiling. |
| 244 | 1 | `public int getMonthsOnAdvances()` |  |
| 245 | 1 | `public int getMonthsAtCeiling()` |  |

### deltas (lines 247-248)

### health, sampled (lines 249-339)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 1 | `public int getOutbreaks()` | How many separate epidemics started during the skip. |
| 260 | 1 | `public int getMonthsInOutbreak()` |  |
| 261 | 1 | `public int getMonthsSick()` |  |
| 263 | 1 | `public double getWorstWorkRatio()` | The worst single month, as a share of work done. |
| 264 | 1 | `public double getPeakUnburied()` |  |
| 266 | 1 | `public boolean isComplete()` |  |
| 267 | 1 | `public int getRequested()` |  |
| 268 | 1 | `public int getCompleted()` |  |
| 269 | 1 | `public boolean stoppedEarly()` |  |
| 271 | 1 | `public int getStartMonth()` |  |
| 272 | 1 | `public int getEndMonth()` |  |
| 274 | 1 | `public double getCashChange()` |  |
| 275 | 1 | `public double getPopulationChange()` |  |
| 276 | 1 | `public double getHousingChange()` |  |
| 277 | 1 | `public double getJobsChange()` |  |
| 278 | 1 | `public double getMonthlyGdpChange()` |  |
| 279 | 1 | `public double getAnnualGdpChange()` |  |
| 280 | 1 | `public double getCityDebtChange()` |  |
| 281 | 1 | `public double getBusinessDebtChange()` |  |
| 282 | 1 | `public double getLandBlocksBought()` |  |
| 284 | 1 | `public double getStartCash()` |  |
| 285 | 1 | `public double getEndCash()` |  |
| 286 | 1 | `public int getStartPopulation()` |  |
| 287 | 1 | `public int getEndPopulation()` |  |
| 288 | 1 | `public double getEndLandUtilisation()` |  |
| 289 | 1 | `public double getEndSavingRate()` |  |
| 290 | 1 | `public double getEndRentBurden()` |  |
| 291 | 1 | `public double getStartSavingRate()` |  |
| 294 | 3 | `public double getWriteOffsDuringSkip()` | Debt the lenders wrote off during the skip. |
| 307 | 5 | `public boolean defaultsWereNews()` | WHETHER THE SKIP'S DEFAULTS ARE NEWS (0.7.8). |
| 314 | 3 | `public double getCashPerMonth()` | Cash per month, which is the number that says whether this is sustainable. |
| 318 | 3 | `public double getPopulationPerMonth()` |  |
| 323 | 7 | `public double getPopulationGrowthRate()` | Population growth over the whole skip, annualised. |
| 331 | 1 | **type** `private interface Field` |  |
| 331 | 1 | `double of(Snapshot s)` _(in TimeSkipReport.Field)_ |  |
| 333 | 6 | `private double delta(Field f)` |  |

### buildings (lines 340-403)

| line | len | member | says |
|---:|---:|---|---|
| 343 | 11 | **type** `public static class BuildingChange` | A building type whose count moved, and by how much. |
| 347 | 4 | `BuildingChange(String name, int change)` _(in TimeSkipReport.BuildingChange)_ |  |
| 352 | 1 | `public boolean isGain()` _(in TimeSkipReport.BuildingChange)_ |  |
| 364 | 23 | `public List<BuildingChange> getBuildingChanges()` | Everything whose count moved, biggest change first. |
| 388 | 7 | `public int getBuildingsGained()` |  |
| 396 | 7 | `public int getBuildingsLost()` |  |

### episodes (lines 404-509)

| line | len | member | says |
|---:|---:|---|---|
| 406 | 1 | `public int getMonthsShortOfPower()` |  |
| 407 | 1 | `public int getMonthsShortOfWater()` |  |
| 408 | 1 | `public int getMonthsCongested()` |  |
| 409 | 1 | `public double getWorstRoadRatio()` |  |
| 410 | 1 | `public int getMonthsOutOfLand()` |  |
| 411 | 1 | `public int getMonthsHouseholdsShort()` |  |
| 412 | 1 | `public int getMonthsNothingBuilt()` |  |
| 413 | 1 | `public double getWorstEnergyRatio()` |  |
| 414 | 1 | `public int getPeakPopulation()` |  |
| 417 | 3 | `public boolean shrankFromPeak()` | True when the city ended smaller than its high-water mark. |
| 422 | 3 | `public double getIdleShare()` | Share of the skip spent with nothing on any building site. |
| 434 | 75 | `public List<String> getHeadlines()` | The things worth putting in front of the player, in plain sentences. |

