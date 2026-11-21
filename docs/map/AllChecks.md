# AllChecks.java - 595 lines · 29 methods · 25 constants · harnesses

`ham/citybuildersim/AllChecks.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> Runs every harness, one JVM each, and says which failed.
> 
> WHY: harnesses exit their JVMs. GF1 bounds children after CD9's 62-minute
> JavaFX hang, and MG1 gates extra starts on free commit, RAM and averaged CPU.
> Quiet modes keep logs and timings, forwarding Timing properties and tagging
> every failing assertion through the common judge (FW7, TM1).

**Uses:** [Timing](Timing.md) (8), [KnownReds](KnownReds.md) (6), [Observation](Observation.md) (3), [FollowBench](FollowBench.md) (1)

**Used by (7):** [CopperRefiningCheck](CopperRefiningCheck.md), [DeterminismCheck](DeterminismCheck.md), [KeptCheck](KeptCheck.md), [KnownRedsCheck](KnownRedsCheck.md), [ObservationCheck](ObservationCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [TimingCheck](TimingCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 40 | `AllChecks.HARNESSES` | `{ "CopperBufferCheck", "UndemandedFeedCheck", "GroundInputCheck", "BuildingDa...` | Every harness: the gate and nightly selections together cover this registry. |
| 58 | `AllChecks.PARALLEL` | `3` | How many harnesses a soft, logged run runs at once (SP1); memory, not the cores, bounds it. |
| 61 | `AllChecks.PARALLEL_PROPERTY` | `"cbs.parallel"` | The property that sets it for one run: -Dcbs.parallel=1 runs them one at a time (given on the java command line or in JDK_JAVA_OPTIONS). |
| 64 | `AllChecks.HEAP_MB` | `2048` | The most heap a harness JVM may take, its -Xmx (SP1): measured 2026-10-27 under a 4 GB ceiling, no harness committed more than 1.5 GB (SaveSlotCheck) or kept more than 0.6 GB after a collection (MapCheck), so 2 GB cha... |
| 67 | `AllChecks.HEAP_START_MB` | `64` | The heap a harness JVM starts with, its -Xms (SP1): the JVM's default is a 64th of the RAM, 508 MB committed at once whether used or not (measured 2026-10-27: BooksCheck 616 MB of commit by default, 167 MB with 64). |
| 70 | `AllChecks.HARNESS_SECONDS` | `4 * 435` | A harness gets four times KeptCheck's usual 435 seconds before its process tree is ended (GF1, CD9's hung FX thread). |
| 73 | `AllChecks.NON_HEAP_MB` | `512` | Non-heap room is 512 MB per harness, above SP1's measured 103 MB at startup and allowing compiler, classes and JavaFX growth (MG1). |
| 76 | `AllChecks.AVAILABLE_RAM_GB` | `4` | Keep 4 GB of RAM available, above the 2.9 GB left when two KeptChecks drove this 32 GB PC to 91% RAM (MG1). |
| 79 | `AllChecks.CPU_PCT` | `80` | Start beside another harness below 80% CPU, leaving room before MG1's measured 85â€“94% contention. |
| 82 | `AllChecks.RESOURCE_SECONDS` | `2` | Sample every two seconds so held starts can resume without busy waiting (MG1). |
| 85 | `AllChecks.CPU_SAMPLES` | `3` | Average three CPU samples, covering six seconds between starts (MG1). |
| 88 | `AllChecks.DISPLAY` | `List.of("ChartFitCheck", "KeptCheck", "KeptTimingCheck", "MapCheck", "MoneyWo...` | GB5: the harnesses that start the JavaFX toolkit (ui.FollowBench.startJavaFx; TimingCheck 10 keeps the list whole). |
| 91 | `AllChecks.LONGEST_FIRST` | `List.of("KeptCheck", "KeptTimingCheck", "CentralBankCheck", "MapCheck", "Glob...` | Longest first when no previous run's seconds can be read (HA1, 2026-11-12 medians). |
| 94 | `AllChecks.NIGHTLY` | `List.of("KeptCheck", "GlobeCheck", "ShortSellingCheck", "LandmassCheck", "Edu...` | Runs nightly, and at the gate only when a direct pick names it (HA1). |
| 100 | `AllChecks.LOGS` | `Paths.get("target", "checks")` | Where -q and -f keep each harness's whole output. |
| 103 | `AllChecks.TIMES` | `LOGS.resolve("times.tsv")` | Where -q and -f write each harness's seconds and exit, a line each, longest first (GT1: no gate said where its forty minutes went). |
| 106 | `AllChecks.SECTION` | `Pattern.compile("^\\s*---\\s.*\\s---\\s*$")` | A harness's section title, "--- what it proves ---". |
| 109 | `AllChecks.SUMMARY` | `Pattern.compile("^\\s*\\d+ FAILED\\s*$")` | A harness's closing count, "N FAILED": printed, but not under the last section's title. |
| 112 | `AllChecks.FAILING` | `Pattern.compile("\\bFAIL(ED)?\\b\|Exception\|^\\S*Error\\b\|^Caused by")` | A line that says something failed: an assertion's FAIL, the "N FAILED" summary, an exception. |
| 115 | `AllChecks.IN_ALL` | `Pattern.compile("^\\.\\.\\.[0-9]+ failing lines in all: ")` | The line failingLines prints when it left failing lines out: "... |
| 118 | `AllChecks.GATE` | `Paths.get("docs", "notes", "gate.txt")` | Main's recorded reds, one line each after the "#" lines: what ship.sh writes at every gate. |
| 121 | `AllChecks.KNOWN_PROPERTY` | `"cbs.knownReds"` | The system property that names the record when it is not GATE; batch-check.sh gives it the record it judges against. |
| 124 | `AllChecks.NO_FAIL_LINE` | `"(no FAIL line; its last lines:)"` | The line a log says before the last lines of a harness that died with no failing line of its own. |
| 127 | `AllChecks.KNOWN_TAG` | `"[known] "` | The tag -f puts before a failing line that is in the record; the same width as NEW_TAG. |
| 130 | `AllChecks.NEW_TAG` | `"[NEW] "` | The tag -f puts before a failing line that is not in the record. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 366 | `final double[] samples` |  |
| 367 | `int count` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 37 | 559 | **type** `public class AllChecks` | Runs every harness, one JVM each, and says which failed. |
| 132 | 140 | `public static void main(String[] args) throws Exception` |  |
| 274 | 6 | `static boolean selected(String name, boolean timing, boolean gate, boolean nightly, List<String> only)` | Gate defaults exclude the nightly tier; only exact direct picks add its members. |
| 282 | 18 | `static Comparator<String> longestFirst(Path local, Path gate)` | Previous local seconds, else the last gate's; malformed rows cannot stop a suite. |
| 302 | 8 | `static int waitForHarness(Process process, int seconds, Path log) throws InterruptedException, IOException` | Waits at most the harness's limit, recording a timeout as a failing assertion (GF1). |
| 312 | 7 | `static void endTree(Process process)` | Ends the descendants while their parent still owns them, then the harness itself (GF1, KD1 item 1). |
| 326 | 10 | `static int parallel(boolean logged, boolean timingOnly)` | How many harnesses this run runs at once (SP1): one when its output is not logged (plain mode inherits it, and would interleave), for --timing, and when timing is hard; else the property or PARALLEL, at most half the ... |
| 338 | 8 | **type** `record Resources(double freeCommit, double availableRam, double cpu, double[] held)` | Injectable machine readings in GiB and percent, with each running child's private commitment. |
| 339 | 6 | `double needed()` _(in AllChecks.Resources)_ |  |
| 348 | 7 | `static int startBlock(Resources r)` | MG1's pure admission rule holds extra starts on unknown readings and always lets an empty run progress. |
| 356 | 7 | `static String holdLine(Resources r, int block)` |  |
| 365 | 14 | **type** `static class CpuWindow` | A rolling window holds extra starts until its first complete CPU window. |
| 368 | 1 | `synchronized void add(double value)` _(in AllChecks.CpuWindow)_ |  |
| 369 | 9 | `synchronized double average()` _(in AllChecks.CpuWindow)_ |  |
| 380 | 4 | `static double systemCpu()` |  |
| 386 | 4 | `static Resources readResources(Supplier<Resources> memory, DoubleSupplier cpu)` | Read CPU last so a slow memory probe cannot admit a child using an earlier CPU window (REV-KD1-1). |
| 392 | 43 | `static Resources resources(Set<Process> live)` | Windows exposes available commitment as swap space, Linux names its limit and charge in meminfo. |
| 437 | 3 | `static String header(String name, int code, double seconds)` | The first line of a harness's entry: its name, its verdict as a number or OK, and how long it took. |
| 442 | 4 | `static String summaryLine(int ran, int failed, double seconds)` | The line under the last harness: how many ran and how they ended. |
| 448 | 5 | `static String displayLine(String display, List<String> started)` | GB5: under the summary where -Dcbs.display names a display: the display harnesses the run started, none of which skips there. |
| 455 | 4 | `static Path recordFile()` | The named combined record, or the default gate and repository parts. |
| 461 | 11 | `static Set<String> knownReds(Path file) throws IOException` | Reads the record and adds repository parts when the default gate was requested. |
| 474 | 6 | `private static String trimBlanks(String s)` | How a red line is cut of its blanks: batch-check.sh's reds_of strips spaces and tabs from the front and spaces, tabs and returns from the back. |
| 482 | 14 | `private static String[] redOf(String name, List<String> printed)` | Maps printed lines to reds, preserving section and diagnostic-tail boundaries. |
| 498 | 6 | `static List<String> redLines(String name, int code, List<String> printed)` | The exit and failing lines of one harness, in public-log order. |
| 506 | 3 | `static int newCount(List<String> red, Set<String> known)` | How many of the red lines are not in the record. |
| 511 | 19 | `static List<String> report(String name, int code, double seconds, List<String> printed, boolean lines, Set<String> known)` | The quiet verdict and optional tagged lines, judged by KnownReds. |
| 532 | 9 | `static String againstLine(Path record, Set<String> known, List<String> knownOnly, List<String> newRed)` | Lists known and new reds without resembling the suite summary used by shell callers. |
| 543 | 10 | `static List<String> command(String java, String classpath, String name, boolean logged)` | Starts a heap-bounded harness with forwarded properties and UTF-8 logged output (SP1, TM3). |
| 555 | 8 | `static List<String> observationLines(Path log)` | PO1: legacy console bytes are replaced, as in softLines and failingLines. |
| 565 | 10 | `static int softLines(Path log)` | How many of a log's assertion lines went SOFT (Timing.SOFT_LINE); a log that cannot be read has none. |
| 577 | 18 | `static List<String> failingLines(Path log) throws IOException` | Keeps each failing section and assertion, or the last ten lines after an unlabelled death, replacing invalid UTF-8. |

