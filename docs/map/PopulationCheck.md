# PopulationCheck.java - 1,338 lines · 10 methods · 1 constants · harnesses

`ham/citybuildersim/PopulationCheck.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

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

**Uses:** [AgeBand](AgeBand.md) (84), [Migration](Migration.md) (40), [PayTier](PayTier.md) (35), [PopulationCohorts](PopulationCohorts.md) (31), [FamilyModel](FamilyModel.md) (23), [Sickness](Sickness.md) (9), [Game](Game.md) (8), [JobType](JobType.md) (5), [FamilyStructure](FamilyStructure.md) (5), [GameFiles](GameFiles.md) (5), [BuildingManager](BuildingManager.md) (4), [PopulationManager](PopulationManager.md) (3), [Healthcare](Healthcare.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [DataSave](DataSave.md) (2), [EconomyManager](EconomyManager.md) (1)

## Sections

| line | section |
|---:|---|
| 65 | · 1. the bands |
| 89 | · 2. ageing conserves people |
| 167 | · 2b. mortality, and the trap in the arithmetic |
| 259 | · 3. pay tiers and families |
| 345 | · 3b. homes, and the squeeze |
| 446 | · 4. AND NOW IT DRIVES THE CITY |
| 522 | · · a door a family cannot enter is not room |
| 665 | · · DECLINING IN WHAT IT BUYS, NOT IN WHAT IT SAYS (2026-09-08) |
| 935 | · 5. and it survives a save |
| 1051 | THE PYRAMID IS SAVED BY NAME, NOT BY POSITION |
| 1186 | THE FAMILIES AND THE RING OF THE LONG SICK, SAME PROPERTY |
| 1219 | · · families |
| 1267 | · · the ring |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `PopulationCheck.ADULT_MIX` | `PopulationCohorts.equilibriumShare(AgeBand.ADULT)` | The adult share these fixtures run at. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 42 | `static int fails` |  |
| 43 | `static PrintStream out` |  |
| 44 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 29 | 1310 | **type** `public class PopulationCheck` | The demographics: do they hold together, and do they move the city the way they were told to? |
| 49 | 6 | `static void check(String label, double actual, double expected, double tol)` |  |
| 56 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 61 | 951 | `public static void main(String[] args) throws Exception` |  |
| 1014 | 36 | `static double[] play(Path root, String name, int months) throws Exception` | Plays a standard city for a given number of months and reports it. |

### THE PYRAMID IS SAVED BY NAME, NOT BY POSITION (lines 1051-1185)

| line | len | member | says |
|---:|---:|---|---|
| 1068 | 117 | `static void theBandsAreSavedByName()` |  |

### THE FAMILIES AND THE RING OF THE LONG SICK, SAME PROPERTY (lines 1186-1338)

| line | len | member | says |
|---:|---:|---|---|
| 1209 | 90 | `static void theOtherTwoBandArraysSurviveItToo()` |  |
| 1301 | 4 | `static int orphanBlockEnd(int bands, int shapes)` | Where FamilyModel's orphan block ends, counting from the front of its array. |
| 1307 | 7 | `static double[] spliceOneSlot(double[] source, int at, double value)` | The same array with one extra slot pushed in at `at`, everything behind it moved along. |
| 1315 | 15 | `static void sameArray(String label, double[] got, double[] wanted)` |  |
| 1331 | 7 | `static void cleanUp(Path root)` |  |

