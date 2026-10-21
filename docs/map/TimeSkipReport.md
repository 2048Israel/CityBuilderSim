# TimeSkipReport.java - 674 lines · 68 methods · 1 constants · model

`ham/citybuildersim/TimeSkipReport.java` - generated 2026-10-21 by CodeMap; line numbers are as of that run.

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
> AND THE SALES NOBODY PRESSED (CD2, slice 6; D28 to D30). The rollover and
> automatic building sell the city's paper month after month inside a skip, each
> at its own cap, and a short one pays the maturity from the cash and the
> central bank's advances or keeps an ask open - none of which either end of
> the skip shows. sampleSales() takes each month's sales (AutoSale), and the
> report lists every one, its stop, its tail and where the rest of a short
> one came from (getAutoSaleLines()), with a line or two of headlines.
> 
> Nothing here computes anything the simulation does not already know. It reads
> finished figures and remembers them, so no screen built on it can move a
> single number in the game.

**Uses:** [DecisionLog](DecisionLog.md) (11), [CityCalendar](CityCalendar.md) (1), [Bank](Bank.md) (1)

**Used by (6):** [AuctionCheck](AuctionCheck.md), [AutoBuilder](AutoBuilder.md), [Game](Game.md), [HealthCheck](HealthCheck.md), [SkipReportCheck](SkipReportCheck.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 81 | · episodes |
| 139 | · capture |
| 276 | · the automatic sales (CD2, slice 6) |
| 401 | · deltas |
| 403 | · health, sampled |
| 494 | · buildings |
| 558 | · episodes |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 326 | `TimeSkipReport.AUTO_SALES_MOST` | `5000` | The most automatic sales a report keeps: a skip of centuries is not a ledger of them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 54 | `int month` |  |
| 55 | `double cash` |  |
| 56 | `long population` |  |
| 57 | `long housing` |  |
| 58 | `long jobs` |  |
| 59 | `double monthlyGdp` |  |
| 60 | `double annualGdp` |  |
| 61 | `double cityDebt` |  |
| 62 | `double businessDebt` |  |
| 63 | `double landOwnedBlocks` |  |
| 64 | `double landUtilisation` |  |
| 65 | `double householdSavingRate` |  |
| 66 | `double householdRentBurden` |  |
| 67 | `double cumulativeWriteOffs` |  |
| 69 | `final Map<String, Integer> buildings` |  |
| 72 | `private Snapshot before` |  |
| 73 | `private Snapshot after` |  |
| 75 | `private boolean complete` |  |
| 78 | `private int requested` | How many months were asked for, and how many actually ran. |
| 79 | `private int completed` |  |
| 82 | `private int monthsShortOfPower` |  |
| 83 | `private int monthsShortOfWater` |  |
| 93 | `private int monthsCongested` | Months the road network could not carry the traffic on it. |
| 94 | `private int monthsOutOfLand` |  |
| 95 | `private int monthsHouseholdsShort` |  |
| 96 | `private int monthsNothingBuilt` |  |
| 99 | `private int monthsOnAdvances` | Months the treasury owed the central bank advances at the month's end, and of those, months it owed them at the ceiling (0.7.1). |
| 100 | `private int monthsAtCeiling` |  |
| 108 | `private double advancedDuringSkip` | What the central bank advanced the treasury over the skip, and what the treasury owed it when the last month closed (0.7.15): a skip runs through an empty treasury on the advances since Jerus's "Skip runs too", so the... |
| 109 | `private double advancesOwedAtEnd` |  |
| 111 | `private double worstEnergyRatio` |  |
| 112 | `private double worstRoadRatio` |  |
| 113 | `private long peakPopulation` |  |
| 323 | `private final List<AutoSale> autoSales` |  |
| 405 | `private int outbreaks` |  |
| 406 | `private int monthsInOutbreak` |  |
| 407 | `private boolean wasOutbreak` |  |
| 408 | `private int monthsSick` |  |
| 409 | `private double worstWorkRatio` |  |
| 410 | `private double peakUnburied` |  |
| 498 | `public final String name` |  |
| 499 | `public final int change` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 626 | **type** `public class TimeSkipReport` | What happened while you were not watching. |
| 52 | 19 | **type** `private static class Snapshot` | A city, at one instant. |

### episodes (lines 81-138)

| line | len | member | says |
|---:|---:|---|---|
| 115 | 23 | `public void beginSkip(int requested)` |  |

### capture (lines 139-275)

| line | len | member | says |
|---:|---:|---|---|
| 149 | 37 | `public void snapshot(boolean atStart, int month, double cash, long population, long housing, long jobs, double monthlyGdp, doub...` | Records the state at one instant. |
| 195 | 6 | `public void sampleMonth(double energyRatio, double waterRatio, double landAvailableSqFt, boolean householdsShort, boolean anyth...` | One month of the skip, as it goes past. |
| 203 | 6 | `public void sampleMonth(double energyRatio, double waterRatio, double roadRatio, double landAvailableSqFt, boolean householdsSh...` |  |
| 224 | 29 | `public void sampleMonth(double energyRatio, double waterRatio, double roadRatio, double landAvailableSqFt, boolean householdsSh...` | The same, plus the month's health. |
| 260 | 4 | `public void sampleTreasury(boolean onAdvances, boolean atCeiling)` | The treasury's month with its central bank (0.7.1): whether it ended the month owing advances, and whether they stood at the ceiling. |
| 270 | 5 | `public void sampleTreasury(boolean onAdvances, boolean atCeiling, double advanced, double owed)` | ...and since 0.7.15 what the central bank advanced the treasury this month (CentralBank.getAdvancedToTreasury(), at the month's settle) and what the treasury owes it at the month's end. |

### the automatic sales (CD2, slice 6) (lines 276-400)

| line | len | member | says |
|---:|---:|---|---|
| 297 | 25 | **type** `public record AutoSale(String by, int month, String paper, double asked, double sold, double raised, double...` | One sale the player did not press (D28 to D30): a rollover issue or an automatic-building bond, sold at auction at the setting's own cap. |
| 302 | 19 | `public String line()` _(in TimeSkipReport.AutoSale)_ | The sale as a line of the report: when, who, what, the stop and the tail, and where the rest of a short one came from. |
| 334 | 4 | `public void sampleSales(List<AutoSale> sales)` | ...the month's automatic sales (Game.getAutoSalesThisMonth()), sampled beside sampleMonth(): the rollover and automatic building sell month after month inside a skip with nobody at the button, and none of it can be re... |
| 340 | 1 | `public List<AutoSale> getAutoSales()` | Every automatic sale over the skip, in the order made. |
| 343 | 5 | `public List<String> getAutoSaleLines()` | ...as lines, one a sale: the stop, the tail, anything short and where the rest came from. |
| 356 | 37 | `private List<String> autoSaleHeadlines(String by, boolean worry)` | What one seller sold over the skip, for the headlines: with worry, only the line for the sales that came up short and where the rest came from (the headlines' worrying lines, before "nothing went wrong" is decided); w... |
| 394 | 1 | `public int getMonthsOnAdvances()` |  |
| 395 | 1 | `public int getMonthsAtCeiling()` |  |
| 397 | 1 | `public double getAdvancedDuringSkip()` | What the central bank advanced the treasury over the skip - printed for it. |
| 399 | 1 | `public double getAdvancesOwedAtEnd()` | ...and what the treasury owed it in advances when the skip's last month closed. |

### deltas (lines 401-402)

### health, sampled (lines 403-493)

| line | len | member | says |
|---:|---:|---|---|
| 413 | 1 | `public int getOutbreaks()` | How many separate epidemics started during the skip. |
| 414 | 1 | `public int getMonthsInOutbreak()` |  |
| 415 | 1 | `public int getMonthsSick()` |  |
| 417 | 1 | `public double getWorstWorkRatio()` | The worst single month, as a share of work done. |
| 418 | 1 | `public double getPeakUnburied()` |  |
| 420 | 1 | `public boolean isComplete()` |  |
| 421 | 1 | `public int getRequested()` |  |
| 422 | 1 | `public int getCompleted()` |  |
| 423 | 1 | `public boolean stoppedEarly()` |  |
| 425 | 1 | `public int getStartMonth()` |  |
| 426 | 1 | `public int getEndMonth()` |  |
| 428 | 1 | `public double getCashChange()` |  |
| 429 | 1 | `public double getPopulationChange()` |  |
| 430 | 1 | `public double getHousingChange()` |  |
| 431 | 1 | `public double getJobsChange()` |  |
| 432 | 1 | `public double getMonthlyGdpChange()` |  |
| 433 | 1 | `public double getAnnualGdpChange()` |  |
| 434 | 1 | `public double getCityDebtChange()` |  |
| 435 | 1 | `public double getBusinessDebtChange()` |  |
| 436 | 1 | `public double getLandBlocksBought()` |  |
| 438 | 1 | `public double getStartCash()` |  |
| 439 | 1 | `public double getEndCash()` |  |
| 440 | 1 | `public long getStartPopulation()` |  |
| 441 | 1 | `public long getEndPopulation()` |  |
| 442 | 1 | `public double getEndLandUtilisation()` |  |
| 443 | 1 | `public double getEndSavingRate()` |  |
| 444 | 1 | `public double getEndRentBurden()` |  |
| 445 | 1 | `public double getStartSavingRate()` |  |
| 448 | 3 | `public double getWriteOffsDuringSkip()` | Debt the lenders wrote off during the skip. |
| 461 | 5 | `public boolean defaultsWereNews()` | WHETHER THE SKIP'S DEFAULTS ARE NEWS (0.7.8). |
| 468 | 3 | `public double getCashPerMonth()` | Cash per month, which is the number that says whether this is sustainable. |
| 472 | 3 | `public double getPopulationPerMonth()` |  |
| 477 | 7 | `public double getPopulationGrowthRate()` | Population growth over the whole skip, annualised. |
| 485 | 1 | **type** `private interface Field` |  |
| 485 | 1 | `double of(Snapshot s)` _(in TimeSkipReport.Field)_ |  |
| 487 | 6 | `private double delta(Field f)` |  |

### buildings (lines 494-557)

| line | len | member | says |
|---:|---:|---|---|
| 497 | 11 | **type** `public static class BuildingChange` | A building type whose count moved, and by how much. |
| 501 | 4 | `BuildingChange(String name, int change)` _(in TimeSkipReport.BuildingChange)_ |  |
| 506 | 1 | `public boolean isGain()` _(in TimeSkipReport.BuildingChange)_ |  |
| 518 | 23 | `public List<BuildingChange> getBuildingChanges()` | Everything whose count moved, biggest change first. |
| 542 | 7 | `public int getBuildingsGained()` |  |
| 550 | 7 | `public int getBuildingsLost()` |  |

### episodes (lines 558-674)

| line | len | member | says |
|---:|---:|---|---|
| 560 | 1 | `public int getMonthsShortOfPower()` |  |
| 561 | 1 | `public int getMonthsShortOfWater()` |  |
| 562 | 1 | `public int getMonthsCongested()` |  |
| 563 | 1 | `public double getWorstRoadRatio()` |  |
| 564 | 1 | `public int getMonthsOutOfLand()` |  |
| 565 | 1 | `public int getMonthsHouseholdsShort()` |  |
| 566 | 1 | `public int getMonthsNothingBuilt()` |  |
| 567 | 1 | `public double getWorstEnergyRatio()` |  |
| 568 | 1 | `public long getPeakPopulation()` |  |
| 571 | 3 | `public boolean shrankFromPeak()` | True when the city ended smaller than its high-water mark. |
| 576 | 3 | `public double getIdleShare()` | Share of the skip spent with nothing on any building site. |
| 588 | 86 | `public List<String> getHeadlines()` | The things worth putting in front of the player, in plain sentences. |

