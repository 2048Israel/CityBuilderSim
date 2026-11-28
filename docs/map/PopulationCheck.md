# PopulationCheck.java - 1,730 lines · 14 methods · 3 constants · harnesses

`ham/citybuildersim/PopulationCheck.java` - generated 2026-11-28 by CodeMap; line numbers are as of that run.

> The demographics: do they hold together, and do they move the city the way
> they were told to?
> 
> THIS FILE USED TO ASK THE OPPOSITE QUESTION. For two batches the cohorts were
> a placeholder, and section 4 played two identical cities - one with
> demographics running, one suppressed - and required every live figure to match
> exactly. That assertion existed because `BuildingManager.instances` had rotted
> in place as an unwatched placeholder, and the note here promised that the day
> the cohorts became load-bearing, the section would fail.
> 
> It has, and this is what replaced it. The claims are now about BEHAVIOUR, and
> they are the four things Jerus actually asked for:
> 
>   - a city fills TOWARD its jobs rather than snapping to them (inertia)
>   - a city that is full but hiring keeps taking people (housing pulls, it
>     does not gate)
>   - arrivals stop exactly where the crowding valves run out, so the promise
>     that nobody is homeless is kept by arithmetic rather than by hope
>   - nobody leaves until a pay tier has been dying for a solid year

**Uses:** [Migration](Migration.md) (112), [AgeBand](AgeBand.md) (84), [PayTier](PayTier.md) (39), [PopulationCohorts](PopulationCohorts.md) (32), [FamilyModel](FamilyModel.md) (32), [PopulationManager](PopulationManager.md) (10), [Sickness](Sickness.md) (9), [LabourMarket](LabourMarket.md) (8), [Game](Game.md) (8), [JobType](JobType.md) (7), [FamilyStructure](FamilyStructure.md) (6), [WageBand](WageBand.md) (5), [GameFiles](GameFiles.md) (5), [BuildingManager](BuildingManager.md) (4), [RealEstate](RealEstate.md) (3), [Healthcare](Healthcare.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [DataSave](DataSave.md) (2), [TempDirs](TempDirs.md) (1), [Sectors](Sectors.md) (1), [EconomyManager](EconomyManager.md) (1), [HousingMarket](HousingMarket.md) (1)

## Sections

| line | section |
|---:|---|
| 105 | · 1. the bands |
| 129 | · 2. ageing conserves people |
| 207 | · 2b. mortality, and the trap in the arithmetic |
| 299 | · 3. pay tiers and families |
| 385 | · 3b. homes, and the squeeze |
| 486 | · 4. AND NOW IT DRIVES THE CITY |
| 562 | · · a door a family cannot enter is not room |
| 654 | · · limited, not switched off (0.7.17) |
| 784 | · · DECLINING IN WHAT IT BUYS, NOT IN WHAT IT SAYS (2026-09-08) |
| 1284 | · 5. and it survives a save |
| 1440 | THE PYRAMID IS SAVED BY NAME, NOT BY POSITION |
| 1575 | THE FAMILIES AND THE RING OF THE LONG SICK, SAME PROPERTY |
| 1608 | · · families |
| 1659 | · · the ring |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `PopulationCheck.ADULT_MIX` | `PopulationCohorts.equilibriumShare(AgeBand.ADULT)` | The adult share these fixtures run at. |
| 1363 | `PopulationCheck.STANDARD_HOUSES` | `200` | The Houses the standard played city starts with. |
| 1366 | `PopulationCheck.DEAR_HOUSES` | `60` | The Houses the dear twin starts with, the same shops and depots: its households outnumber its doors, so the landlord's asks rise on the pressure. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `static int fails` |  |
| 43 | `static PrintStream out` |  |
| 44 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 1702 | **type** `public class PopulationCheck` | The demographics: do they hold together, and do they move the city the way they were told to? |
| 49 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 61 | 8 | `static double arrivalsFrom(Migration mig, PopulationCohorts people, FamilyModel households, int totalJobs, long[] census)` | One month's arrivals into a city of these people and households against this door census, with this many posts pulling - the census handed to migration the way Game hands it (Migration.setDoors()). |
| 74 | 9 | `static PopulationManager cityOutOfWork(long adults, double share)` | A population manager whose labour force is this many adults and whose posts are all unskilled ones, as many as leave this share of them out of work (to a person): so its getUnemploymentRate() is the share asked for, a... |
| 88 | 7 | `static Migration oldMigration()` | A Migration that has recorded Migration.BRAKE_FROM_MONTH months of the same steady wage (one tier pays, the rest pay nothing, as the pushes section has it), so no tier is declining and no one leaves for it: an old cit... |
| 96 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 101 | 1260 | `public static void main(String[] args) throws Exception` |  |
| 1369 | 3 | `static double[] play(Path root, String name, int months) throws Exception` | Plays a standard city for a given number of months and reports it. |
| 1374 | 65 | `static double[] play(Path root, String name, int months, int houses) throws Exception` | ...with `houses` Houses placed on day one in place of the standard city's. |

### THE PYRAMID IS SAVED BY NAME, NOT BY POSITION (lines 1440-1574)

| line | len | member | says |
|---:|---:|---|---|
| 1457 | 117 | `static void theBandsAreSavedByName()` |  |

### THE FAMILIES AND THE RING OF THE LONG SICK, SAME PROPERTY (lines 1575-1730)

| line | len | member | says |
|---:|---:|---|---|
| 1598 | 93 | `static void theOtherTwoBandArraysSurviveItToo()` |  |
| 1693 | 4 | `static int orphanBlockEnd(int bands, int shapes)` | Where FamilyModel's orphan block ends, counting from the front of its array. |
| 1699 | 7 | `static double[] spliceOneSlot(double[] source, int at, double value)` | The same array with one extra slot pushed in at `at`, everything behind it moved along. |
| 1707 | 15 | `static void sameArray(String label, double[] got, double[] wanted)` |  |
| 1723 | 7 | `static void cleanUp(Path root)` |  |

