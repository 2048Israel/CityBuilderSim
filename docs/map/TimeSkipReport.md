# TimeSkipReport.java - 538 lines · 63 methods · 0 constants · model

`ham/citybuildersim/TimeSkipReport.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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
>   counted (it never did; the note is gone since 0.7.0), and since 0.7.15
>   what those advances came to over the skip. These cannot be
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
| 73 | · episodes |
| 130 | · capture |
| 274 | · deltas |
| 276 | · health, sampled |
| 367 | · buildings |
| 431 | · episodes |

## Fields (state)

| line | field | says |
|---:|---|---|
| 46 | `int month` |  |
| 47 | `double cash` |  |
| 48 | `int population` |  |
| 49 | `int housing` |  |
| 50 | `int jobs` |  |
| 51 | `double monthlyGdp` |  |
| 52 | `double annualGdp` |  |
| 53 | `double cityDebt` |  |
| 54 | `double businessDebt` |  |
| 55 | `double landOwnedBlocks` |  |
| 56 | `double landUtilisation` |  |
| 57 | `double householdSavingRate` |  |
| 58 | `double householdRentBurden` |  |
| 59 | `double cumulativeWriteOffs` |  |
| 61 | `final Map<String, Integer> buildings` |  |
| 64 | `private Snapshot before` |  |
| 65 | `private Snapshot after` |  |
| 67 | `private boolean complete` |  |
| 70 | `private int requested` | How many months were asked for, and how many actually ran. |
| 71 | `private int completed` |  |
| 74 | `private int monthsShortOfPower` |  |
| 75 | `private int monthsShortOfWater` |  |
| 85 | `private int monthsCongested` | Months the road network could not carry the traffic on it. |
| 86 | `private int monthsOutOfLand` |  |
| 87 | `private int monthsHouseholdsShort` |  |
| 88 | `private int monthsNothingBuilt` |  |
| 91 | `private int monthsOnAdvances` | Months the treasury owed the central bank advances at the month's end, and of those, months it owed them at the ceiling (0.7.1). |
| 92 | `private int monthsAtCeiling` |  |
| 100 | `private double advancedDuringSkip` | What the central bank advanced the treasury over the skip, and what the treasury owed it when the last month closed (0.7.15): a skip runs through an empty treasury on the advances since Jerus's "Skip runs too", so the... |
| 101 | `private double advancesOwedAtEnd` |  |
| 103 | `private double worstEnergyRatio` |  |
| 104 | `private double worstRoadRatio` |  |
| 105 | `private int peakPopulation` |  |
| 278 | `private int outbreaks` |  |
| 279 | `private int monthsInOutbreak` |  |
| 280 | `private boolean wasOutbreak` |  |
| 281 | `private int monthsSick` |  |
| 282 | `private double worstWorkRatio` |  |
| 283 | `private double peakUnburied` |  |
| 371 | `public final String name` |  |
| 372 | `public final int change` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 41 | 498 | **type** `public class TimeSkipReport` | What happened while you were not watching. |
| 44 | 19 | **type** `private static class Snapshot` | A city, at one instant. |

### episodes (lines 73-129)

| line | len | member | says |
|---:|---:|---|---|
| 107 | 22 | `public void beginSkip(int requested)` |  |

### capture (lines 130-273)

| line | len | member | says |
|---:|---:|---|---|
| 140 | 37 | `public void snapshot(boolean atStart, int month, double cash, int population, int housing, int jobs, double monthlyGdp, double ...` | Records the state at one instant. |
| 186 | 6 | `public void sampleMonth(double energyRatio, double waterRatio, double landAvailableSqFt, boolean householdsShort, boolean anyth...` | One month of the skip, as it goes past. |
| 194 | 6 | `public void sampleMonth(double energyRatio, double waterRatio, double roadRatio, double landAvailableSqFt, boolean householdsSh...` |  |
| 215 | 29 | `public void sampleMonth(double energyRatio, double waterRatio, double roadRatio, double landAvailableSqFt, boolean householdsSh...` | The same, plus the month's health. |
| 251 | 4 | `public void sampleTreasury(boolean onAdvances, boolean atCeiling)` | The treasury's month with its central bank (0.7.1): whether it ended the month owing advances, and whether they stood at the ceiling. |
| 261 | 5 | `public void sampleTreasury(boolean onAdvances, boolean atCeiling, double advanced, double owed)` | ...and since 0.7.15 what the central bank advanced the treasury this month (CentralBank.getAdvancedToTreasury(), at the month's settle) and what the treasury owes it at the month's end. |
| 267 | 1 | `public int getMonthsOnAdvances()` |  |
| 268 | 1 | `public int getMonthsAtCeiling()` |  |
| 270 | 1 | `public double getAdvancedDuringSkip()` | What the central bank advanced the treasury over the skip - printed for it. |
| 272 | 1 | `public double getAdvancesOwedAtEnd()` | ...and what the treasury owed it in advances when the skip's last month closed. |

### deltas (lines 274-275)

### health, sampled (lines 276-366)

| line | len | member | says |
|---:|---:|---|---|
| 286 | 1 | `public int getOutbreaks()` | How many separate epidemics started during the skip. |
| 287 | 1 | `public int getMonthsInOutbreak()` |  |
| 288 | 1 | `public int getMonthsSick()` |  |
| 290 | 1 | `public double getWorstWorkRatio()` | The worst single month, as a share of work done. |
| 291 | 1 | `public double getPeakUnburied()` |  |
| 293 | 1 | `public boolean isComplete()` |  |
| 294 | 1 | `public int getRequested()` |  |
| 295 | 1 | `public int getCompleted()` |  |
| 296 | 1 | `public boolean stoppedEarly()` |  |
| 298 | 1 | `public int getStartMonth()` |  |
| 299 | 1 | `public int getEndMonth()` |  |
| 301 | 1 | `public double getCashChange()` |  |
| 302 | 1 | `public double getPopulationChange()` |  |
| 303 | 1 | `public double getHousingChange()` |  |
| 304 | 1 | `public double getJobsChange()` |  |
| 305 | 1 | `public double getMonthlyGdpChange()` |  |
| 306 | 1 | `public double getAnnualGdpChange()` |  |
| 307 | 1 | `public double getCityDebtChange()` |  |
| 308 | 1 | `public double getBusinessDebtChange()` |  |
| 309 | 1 | `public double getLandBlocksBought()` |  |
| 311 | 1 | `public double getStartCash()` |  |
| 312 | 1 | `public double getEndCash()` |  |
| 313 | 1 | `public int getStartPopulation()` |  |
| 314 | 1 | `public int getEndPopulation()` |  |
| 315 | 1 | `public double getEndLandUtilisation()` |  |
| 316 | 1 | `public double getEndSavingRate()` |  |
| 317 | 1 | `public double getEndRentBurden()` |  |
| 318 | 1 | `public double getStartSavingRate()` |  |
| 321 | 3 | `public double getWriteOffsDuringSkip()` | Debt the lenders wrote off during the skip. |
| 334 | 5 | `public boolean defaultsWereNews()` | WHETHER THE SKIP'S DEFAULTS ARE NEWS (0.7.8). |
| 341 | 3 | `public double getCashPerMonth()` | Cash per month, which is the number that says whether this is sustainable. |
| 345 | 3 | `public double getPopulationPerMonth()` |  |
| 350 | 7 | `public double getPopulationGrowthRate()` | Population growth over the whole skip, annualised. |
| 358 | 1 | **type** `private interface Field` |  |
| 358 | 1 | `double of(Snapshot s)` _(in TimeSkipReport.Field)_ |  |
| 360 | 6 | `private double delta(Field f)` |  |

### buildings (lines 367-430)

| line | len | member | says |
|---:|---:|---|---|
| 370 | 11 | **type** `public static class BuildingChange` | A building type whose count moved, and by how much. |
| 374 | 4 | `BuildingChange(String name, int change)` _(in TimeSkipReport.BuildingChange)_ |  |
| 379 | 1 | `public boolean isGain()` _(in TimeSkipReport.BuildingChange)_ |  |
| 391 | 23 | `public List<BuildingChange> getBuildingChanges()` | Everything whose count moved, biggest change first. |
| 415 | 7 | `public int getBuildingsGained()` |  |
| 423 | 7 | `public int getBuildingsLost()` |  |

### episodes (lines 431-538)

| line | len | member | says |
|---:|---:|---|---|
| 433 | 1 | `public int getMonthsShortOfPower()` |  |
| 434 | 1 | `public int getMonthsShortOfWater()` |  |
| 435 | 1 | `public int getMonthsCongested()` |  |
| 436 | 1 | `public double getWorstRoadRatio()` |  |
| 437 | 1 | `public int getMonthsOutOfLand()` |  |
| 438 | 1 | `public int getMonthsHouseholdsShort()` |  |
| 439 | 1 | `public int getMonthsNothingBuilt()` |  |
| 440 | 1 | `public double getWorstEnergyRatio()` |  |
| 441 | 1 | `public int getPeakPopulation()` |  |
| 444 | 3 | `public boolean shrankFromPeak()` | True when the city ended smaller than its high-water mark. |
| 449 | 3 | `public double getIdleShare()` | Share of the skip spent with nothing on any building site. |
| 461 | 77 | `public List<String> getHeadlines()` | The things worth putting in front of the player, in plain sentences. |

