# DeterminismCheck.java - 447 lines · 19 methods · 5 constants · harnesses

`ham/citybuildersim/DeterminismCheck.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The month reads nothing of the machine it runs on: the same city, played in JVMs set up as opposite machines, comes out the same to the bit.
> 
> WHY. BA1's notes said "the same code gave another city twice under load": FloatLinkCheck's played town at month 125 instead of 61, SaveFileCheck's
> at 1,067 people instead of 909. DT1 (2026-11-05) traced it, and it was not the machine: the two odd runs were the only two probe runs started
> without -Dcbs.earth and -Dcbs.world, in a worktree with no data/ folder, so WorldData.locate() found no world file, the city had no world market
> (Game.getWorldMarket() null: it trades on the literals) and it was another city. Nothing printed that the file was missing. Rerun with and without
> the properties, the numbers came back to the digit every time. DT1 also played the model under opposite JVM settings - identity hashes
> (-XX:hashCode=2), -Xint, C1 only, one processor or thirty-two, a common pool of one, soft references cleared at every collection, the serial
> collector, an autosave every twelve months against one (the save clock) - and found it bit for bit the same. This turns both findings into the
> suite's: a harness that holds the month to that, with a control that shows the comparison tells two cities apart and that the world file is an
> input to the month, and (WorldData.locate()) a notice on stderr when the file is not found, so a run without it says so.
> 
> WHAT THIS HAS TO PROVE. The parent starts three child JVMs of itself, one after the other (never at once), each plays the same fixtures and writes
> one fingerprint line a month, to the bit, to a file the parent reads:
> 
>   A, the machine as AllChecks gives it, on the main thread; B, the opposite machine (identity hash 1 for every object, one processor, a common
>   pool of one, C1 alone, soft references cleared at every collection, the serial collector, an autosave as often as the city is due one) on a
>   thread of its own; C, the control, in a folder with no data/ and no world file named.
> 
>   1. THE FIXTURE CAUSES THE CONDITIONS. The children report what they ran under (hashes, processors, pool, collector, soft reference, C1, the
>      thread, the autosaves, the world market); a comparison between two machines that were the same machine proves nothing.
>   2. THE SAME CITY ON BOTH MACHINES, BIT FOR BIT: FloatLinkCheck's played town, 130 months one at a time, and the Earth city
>      LongPlaytest.earthCity plays (Medellin, 240 months), A's every line equal to B's. A difference names the first month and the first field.
>   3. A CITY AFTER ANOTHER IN THE SAME JVM: A plays the town twice, the second equal to the first month by month (no state left by the first).
>   4. THE CONTROL: WITHOUT THE WORLD FILE IT IS ANOTHER CITY. C's town has no world market, says so on stderr (the notice), and parts from A's;
>      so the world file is an input to the month, and the comparison of 2 and 3 can tell two cities apart. Only where the parent has a world
>      file to take away.

**Uses:** [FloatLinkCheck](FloatLinkCheck.md) (8), [WorldData](WorldData.md) (6), [Game](Game.md) (6), [GameFiles](GameFiles.md) (3), [TempDirs](TempDirs.md) (2), [LongPlaytest](LongPlaytest.md) (2), [AllChecks](AllChecks.md) (1), [EarthSite](EarthSite.md) (1), [Founding](Founding.md) (1), [Sectors](Sectors.md) (1), [Sector](Sector.md) (1)

**Used by (1):** [CausesOfDeathCheck](CausesOfDeathCheck.md)

## Sections

| line | section |
|---:|---|
| 76 | (untitled) |
| 111 | THE PARENT: three children, one after the other |
| 203 | · (untitled) |
| 298 | THE CHILD: play the fixtures, write the fingerprints and the report |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `DeterminismCheck.TOWN_MONTHS` | `130` | The months FloatLinkCheck's played town is played, one at a time, a fingerprint after each (and one before the first). |
| 64 | `DeterminismCheck.EARTH_MONTHS` | `240` | The months the Earth city is played (LongPlaytest.earthCity); 8 s on the PC. |
| 67 | `DeterminismCheck.CHILD_MINUTES` | `15` | How long a child JVM may run before the check kills it and fails: the slowest (B, on one processor with C1 alone) takes a few minutes. |
| 70 | `DeterminismCheck.OPPOSITE_MACHINE` | `List.of("-XX:+UnlockExperimentalVMOptions", "-XX:hashCode=2", "-XX:ActiveProc...` | The flags that make a JVM the opposite machine: every identity hash 1, one processor, a common pool of one, C1 alone, soft references cleared at each collection, the serial collector. |
| 74 | `DeterminismCheck.HEAD` | `"head", TOWN1 = "town1", TOWN2 = "town2", EARTH = "earth", REPORT = "report"` | The line a child begins its fingerprint file's field names with, and the prefixes of the rest. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |
| 53 | `static PrintStream out` |  |
| 117 | `final String which` |  |
| 118 | `boolean ran` |  |
| 119 | `Path log` |  |
| 120 | `final Map<String, String> report` |  |
| 121 | `List<String> head` |  |
| 122 | `final List<String[]> town1` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 398 | **type** `public class DeterminismCheck` | The month reads nothing of the machine it runs on: the same city, played in JVMs set up as opposite machines, comes out the same to the bit. |
| 55 | 4 | `static void check(String label, boolean ok)` |  |

### (untitled) (lines 76-110)

| line | len | member | says |
|---:|---:|---|---|
| 78 | 32 | `public static void main(String[] args) throws Exception` |  |

### THE PARENT: three children, one after the other (lines 111-202)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 32 | **type** `static final class Result` | What a child kept: its report, the field names, and the fingerprint lines of each city. |
| 124 | 1 | `Result(String which)` _(in DeterminismCheck.Result)_ |  |
| 126 | 1 | `String get(String key)` _(in DeterminismCheck.Result)_ |  |
| 128 | 1 | `boolean is(String key)` _(in DeterminismCheck.Result)_ |  |
| 130 | 3 | `int num(String key)` _(in DeterminismCheck.Result)_ |  |
| 134 | 13 | `void read(Path file) throws IOException` _(in DeterminismCheck.Result)_ |  |
| 150 | 52 | `static Result runChild(String which, boolean worldHere) throws Exception` | A child JVM started as AllChecks starts a harness, set up as machine A, B or C (the header says how); one after the other, never at once. |

### (untitled) (lines 203-297)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 31 | `static void theMachines(Result a, Result b, boolean worldHere)` | Section 1: the report of each child says the conditions were there. |
| 239 | 11 | `static void sameCity(Result a, Result b)` | Section 2: A's lines equal B's, every month. |
| 252 | 5 | `static void afterAnother(Result a)` | Section 3: A's second town equals its first. |
| 259 | 15 | `static void theControl(Result a, Result c, boolean worldHere) throws IOException` | Section 4: the town without the world file is another city, and says so. |
| 276 | 15 | `static String firstDifference(List<String> head, List<String[]> x, List<String[]> y)` | The first month and field two runs' lines part at, with both values; null if they are the same lines. |
| 293 | 4 | `static String show(String s)` | A hexadecimal double with its decimal beside it. |

### THE CHILD: play the fixtures, write the fingerprints and the report (lines 298-447)

| line | len | member | says |
|---:|---:|---|---|
| 303 | 1 | **type** `record Town(List<String> head, List<String> lines, int autosaves, boolean worldMarket)` | What one town gave: the field names, a line a month, how many autosaves it printed, whether it had a world market. |
| 305 | 37 | `static void child(String which, Path outFile) throws Exception` |  |
| 344 | 36 | `static void fixtures(String which, List<String> file, Map<String, String> report) throws Exception` | The fixtures of one child: the town (twice in A), then the Earth city (A and B, where the Earth file is named). |
| 382 | 42 | `static Town playTown(String label, LongSupplier clock)` | FloatLinkCheck's played town with the firms free to order (DT1's probe, shape freestep), played TOWN_MONTHS months one at a time. |
| 430 | 15 | `static Map<String, String> fingerprint(Game c)` | What a city is, in one line a month, to the bit: the month, the people, the city's cash, the bank's cash and equity, what the households have banked, the price index, the city's debt outstanding, the stacks standing a... |
| 446 | 1 | `static String hex(double x)` |  |

