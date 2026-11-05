# DT1 - The same code, another city: what in a month reads the machine ({next}, 2026-11-05)

**WHERE IT STANDS (2026-11-05): done; waiting for RT1 to land, then main merged in and `--pick` again.**

## The finding: not the machine, the world file

BA1's notes read "the same code gave another city twice under load (month 125 instead of 61, 1,067 people
instead of 909), and the same in five other repeats". It was two probe runs started without the world file.

- **BA1's transcript** (its slice A2, 2026-11-05 06:05-06:28). The two odd runs were the only two probe commands
  without `export JDK_JAVA_OPTIONS=...`, in a worktree with no `data/` folder, so they had neither `-Dcbs.earth`
  nor `-Dcbs.world`. Every run with the export agreed with every other. "Under load" was a guess: the same
  ensemble ran beside the runs that agreed.
- **They were two different cities.** Month 125 is FloatLinkCheck's played town (`ProbeA`). 1,067 people is a copy
  of SaveFileCheck's city at month 91 (`ProbeG`). The DT1 row joins them into one town.
- **Reproduced on BA1's commit** (b4bf96f4, extracted and compiled in scratch, BA1's two probes rerun, each twice):

  | probe | with the two properties | without them |
  |---|---|---|
  | ProbeA (FloatLinkCheck.playedCity) | month 61, 1,955 people, room 32,888 | month 125, 4,216 people, room 15,582; "+11: month 136 pop 4658", BA1's line to the digit |
  | ProbeG (SaveFileCheck's city) | month 91, 909 people, cash 1,977 | month 91, 1,067 people, cash 1,690 |

- **The world file is the input, not the Earth file.** On this tree, FloatLinkCheck's town with the firms free stops
  at month 61 with 1,955 people with both files or the world file alone, and at month 65 with 2,804 people with
  neither or the Earth file alone. Without `world-1.bin`, `WorldData.locate()` is null, `Game.getWorldMarket()`
  null, and the city trades on the literals. Nothing said so. A player never meets it: `Build EXE.bat` refuses
  to build without the file and ships it.

## What reads the machine in a month: nothing found

**Read, each candidate the sweep of the model found** (parallel streams, pools, clocks, the processor count,
soft, weak and identity maps, unseeded randoms, system properties):

- **The autosave's floor** (`Game.saveClock`, `SaveWriter.FLOOR_SECONDS` 30 s) is the one clock in the month: a
  slower machine writes more autosaves inside a skip. It moves nothing. FloatLinkCheck's town with one autosave
  and with ten (`useSaveClockForChecks`) is the same bit for bit for 130 months. `prepareSave()` fills a save and
  takes a snapshot. The writer's thread only reads the live history: `HistorySave.Later` copies it under its
  monitor, `restoreFrom()` copies every list, and every change of the history settles the copies it owes first.
- **CityLand's parallel count of plots** adds whole numbers in row order, and `World`'s caches are synchronized.
  It was reached in neither city measured (0 of 44 counts in the town, 0 of 59 in the ensemble's city).
- **DistrictPlan's soft-held builder and CityMap's identity-hash keys** are the map's. `Game` only hands the map
  its pictures (`atSea`, `railPicture`, `landmassPicture`, `bridgePicture`, `reconcile`, `writeSidecar`,
  `lastStamp`) and reads nothing back. No plan was made in either harness city.
- **The rest:** `Founding.rollWorldSeed()` is the founding screen's dice. `WorldMarket`'s `nanoTime` is a build
  time it reports. `TempDirs` names every folder at random under the JVM's pid, so no two JVMs share one. The
  model reads three system properties: `cbs.earth` and `cbs.world` (which files) and `cbs.priceLevel` (a test's
  forced level).

**Run, the month under opposite machines** (DT1's probe and the ensemble's driver, each variant a JVM of its own,
three at once, compared with an unflagged JVM bit for bit (`Double.toHexString`) month by month):

| variant | FloatLinkCheck's town, sectors held and free, 61-131 months | the ensemble's city, AUTO2 seed 0, 600 months (10,227 people) |
|---|---|---|
| a second unflagged JVM | same | same |
| `-Xint`; `-XX:TieredStopAtLevel=1` | same; same | -; same |
| `-XX:hashCode=2` (every identity hash 1), `=3`, `=4` | same | same (2 and 4) |
| `-XX:ActiveProcessorCount=1`, `=32` | same | same (1) |
| `-Djava.util.concurrent.ForkJoinPool.common.parallelism=1` | same | same |
| `-XX:SoftRefLRUPolicyMSPerMB=0` with a small heap | same | same |
| `-XX:+UseSerialGC` | same | same |
| one autosave against ten (the save clock) | same | - |
| a city after another in the same JVM | same | - |

72 town runs in 24 JVMs (66 compared with the unflagged JVM's) and 10 of the city's: not one figure moved.

## What DT1 adds

- **The notice** (`WorldData.locate()`): no world file, neither named nor at `data/world-1.bin`, prints one line
  per JVM to stderr, `world-1.bin not named ...: the cities of this run trade on the literals`. It goes to stderr
  because a check's `quietly()` and BA1's probes silence System.out.
- **DeterminismCheck**, the finding as a harness. Three child JVMs one after the other: A as AllChecks starts a
  harness, B the opposite machine (identity hashes all 1, one processor and a common pool of one, C1 alone, soft
  references cleared at each collection, the serial collector, an autosave every 12 months against A's one, a
  thread of its own), and C, the control, with no world file. 1, the children's machines differ as asked. 2, A's
  and B's towns (130 months, one line a month) and Earth cities (Medellin, 240 months) agree bit for bit. 3, a
  town after another in one JVM agrees with the first. 4, without the world file it is another city, and it says
  so.

  DeterminismCheck takes 25-29 s: A 12 s, B 13 s, C 3 s, one after the other. In the suite's run it passed its 27
  lines, and C's town parts from A's at month 2 (its cash 14,255.87 against 14,303.39). It also passes with the
  Earth file alone (section 4 says it did not run) and with neither (the Earth city says so).
- **The review** (Sonnet wrote the slice, from a brief). The diff was read whole as a stranger's. `pick.sh` asked
  for a threading review. The `AtomicBoolean` is a once-only flag set by `compareAndSet`, with no other state
  shared. Two departures, both right. B's soft reference needs two collections, because the soft-reference clock
  moves at a collection's end. Three assertions were added: both machines played the Earth city, A's second town
  wrote as many autosaves as its first, and every child exits 0 with its file.

## The checks

- **`./batch-check.sh --pick`** (on 8edd98fd, built on RT1's tip): 20 of 175 harnesses, exit 4 on MapCheck alone.
  Two lines: 2d "every box stands four months on (500 of them, 7 moved)" and "a yard that goes moves none of the
  buildings ... (698 of them, 70 moved)". They are RT1's two lines word for word, passed by name at its gate (RT1's
  notes, MapCheck 2d, DM1's class). They are not this batch's. With the properties set, `WorldData.locate()`
  returns before the new code, so no path in any harness moved. The run after RT1 lands and main is merged in is
  below.
- **The full suite: not run.** No harness's path moved. The model's one new line runs only when no world file is
  found, and the gate always names it.

## The ensemble

Not run. The month did not change: the model's one new line is a notice on stderr.

## Found on the way

- BA1's notes and the DT1 row join two cities into one: month 125 is FloatLinkCheck's town, and 1,067 people is
  SaveFileCheck's city at month 91.
- DT1's research agent exported BA1's two transcripts to read them. The zips are in Jerus's Downloads:
  `session-export-1793894032578.zip` and `session-export-1793894034723.zip`. Nothing else needs them; they can
  be deleted.

## The calls

- **★1 Should a check refuse to run without the world file?** Recommended: no, keep the notice. The gate names
  both files, the cloud workbench may have neither, and the Earth harnesses already work the same way (a note,
  not a refusal). A refusal would make every harness city depend on a 9 MB file that is not in git.
