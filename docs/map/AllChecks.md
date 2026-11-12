# AllChecks.java - 470 lines · 18 methods · 20 constants · harnesses

`ham/citybuildersim/AllChecks.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> Runs every harness, one JVM each, and says which failed.
> 
> WHY: each harness calls System.exit(), so it needs its own JVM. Hand-running
> the growing list left checks unrun. GF1 bounds each child's life after CD9's
> JavaFX thread survived an OutOfMemoryError for 62 minutes.
> 
> -q prints verdicts, -f only failures, keeping whole logs in target/checks.
> Other arguments select by substring, --timing selects Timing.HARNESSES.
> The exit counts failed harnesses. JavaFX checks skip without its classpath.
> -f tags each red against the same record as batch-check.sh (FW7), prints at
> most FAIL_LINES and keeps the rest. Timing properties are forwarded (TM1).
> Logged soft runs share PARALLEL slots, memory-gated and heap-bounded (SP1),
> hard timing and plain output run serially. TIMES keeps each run's duration.

**Uses:** [Timing](Timing.md) (8)

**Used by (4):** [DeterminismCheck](DeterminismCheck.md), [KnownRedsCheck](KnownRedsCheck.md), [PriceLevelCheck](PriceLevelCheck.md), [TimingCheck](TimingCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 44 | `AllChecks.HARNESSES` | `{ "BuildingDataCheck", "BrandingCheck", "CreditsCheck", "MoneySignCheck", "Ne...` | In the order they are cheapest to fail. |
| 62 | `AllChecks.FAIL_LINES` | `40` | The most lines -f prints for one failed harness; the rest stays in its log. |
| 65 | `AllChecks.PARALLEL` | `3` | How many harnesses a soft, logged run runs at once (SP1); memory, not the cores, bounds it. |
| 68 | `AllChecks.PARALLEL_PROPERTY` | `"cbs.parallel"` | The property that sets it for one run: -Dcbs.parallel=1 runs them one at a time (given on the java command line or in JDK_JAVA_OPTIONS). |
| 71 | `AllChecks.HEAP_MB` | `2048` | The most heap a harness JVM may take, its -Xmx (SP1): measured 2026-10-27 under a 4 GB ceiling, no harness committed more than 1.5 GB (SaveSlotCheck) or kept more than 0.6 GB after a collection (MapCheck), so 2 GB cha... |
| 74 | `AllChecks.HEAP_START_MB` | `64` | The heap a harness JVM starts with, its -Xms (SP1): the JVM's default is a 64th of the RAM, 508 MB committed at once whether used or not (measured 2026-10-27: BooksCheck 616 MB of commit by default, 167 MB with 64). |
| 77 | `AllChecks.HARNESS_SECONDS` | `4 * 435` | A harness gets four times KeptCheck's usual 435 seconds before its process tree is ended (GF1, CD9's hung FX thread). |
| 80 | `AllChecks.COMMIT_PCT` | `75` | No harness is started beside another of this run while the machine's commit charge is over this percent (SP1): the first three, started at 79.8%, took the PC to 85.9% within a minute as they grew (2026-10-27), and the... |
| 83 | `AllChecks.LONGEST_FIRST` | `List.of("MapCheck", "GlobeCheck", "LongPlaytest", "EarthTerrainCheck", "Bridg...` | The harnesses started first when more than one runs at once, the longest first (measured 2026-10-27, earth-2.bin named: MapCheck 192 s, GlobeCheck 72, LongPlaytest 55, EarthTerrainCheck 52, the rest 41 to 30), so the ... |
| 86 | `AllChecks.LOGS` | `Paths.get("target", "checks")` | Where -q and -f keep each harness's whole output. |
| 89 | `AllChecks.TIMES` | `LOGS.resolve("times.tsv")` | Where -q and -f write each harness's seconds and exit, a line each, longest first (GT1: no gate said where its forty minutes went). |
| 92 | `AllChecks.SECTION` | `Pattern.compile("^\\s*---\\s.*\\s---\\s*$")` | A harness's section title, "--- what it proves ---". |
| 95 | `AllChecks.SUMMARY` | `Pattern.compile("^\\s*\\d+ FAILED\\s*$")` | A harness's closing count, "N FAILED": printed, but not under the last section's title. |
| 98 | `AllChecks.FAILING` | `Pattern.compile("\\bFAIL(ED)?\\b\|Exception\|^\\S*Error\\b\|^Caused by")` | A line that says something failed: an assertion's FAIL, the "N FAILED" summary, an exception. |
| 101 | `AllChecks.IN_ALL` | `Pattern.compile("^\\.\\.\\.[0-9]+ failing lines in all: ")` | The line failingLines prints when it left failing lines out: "... |
| 104 | `AllChecks.GATE` | `Paths.get("docs", "notes", "gate.txt")` | Main's recorded reds, one line each after the "#" lines: what ship.sh writes at every gate. |
| 107 | `AllChecks.KNOWN_PROPERTY` | `"cbs.knownReds"` | The system property that names the record when it is not GATE; batch-check.sh gives it the record it judges against. |
| 110 | `AllChecks.NO_FAIL_LINE` | `"(no FAIL line; its last lines:)"` | The line a log says before the last lines of a harness that died with no failing line of its own. |
| 113 | `AllChecks.KNOWN_TAG` | `"[known] "` | The tag -f puts before a failing line that is in the record; the same width as NEW_TAG. |
| 116 | `AllChecks.NEW_TAG` | `"[NEW] "` | The tag -f puts before a failing line that is not in the record. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 41 | 430 | **type** `public class AllChecks` | Runs every harness, one JVM each, and says which failed. |
| 118 | 118 | `public static void main(String[] args) throws Exception` |  |
| 238 | 8 | `static int waitForHarness(Process process, int seconds, Path log) throws InterruptedException, IOException` | Waits at most the harness's limit, recording a timeout as a failing assertion (GF1). |
| 248 | 7 | `static void endTree(Process process)` | Ends the descendants while their parent still owns them, then the harness itself (GF1, KD1 item 1). |
| 262 | 10 | `static int parallel(boolean logged, boolean timingOnly)` | How many harnesses this run runs at once (SP1): one when its output is not logged (plain mode inherits it, and would interleave), for --timing, and when timing is hard; else the property or PARALLEL, at most half the ... |
| 278 | 5 | `static double commitPct()` | The machine's commit charge in percent (SP1): on Windows the commit limit and what is left of it, as Task Manager shows it; elsewhere swap, which only a starved machine fills. |
| 285 | 3 | `static String header(String name, int code, double seconds)` | The first line of a harness's entry: its name, its verdict as a number or OK, and how long it took. |
| 290 | 4 | `static String summaryLine(int ran, int failed, double seconds)` | The line under the last harness: how many ran and how they ended. |
| 299 | 4 | `static Path recordFile()` | The record this run reads: the file named by -Dcbs.knownReds when that is set, else GATE. |
| 311 | 9 | `static Set<String> knownReds(Path file) throws IOException` | Main's recorded reds, as batch-check.sh reads them: the lines of the file, each exactly as written, but for the lines that start with # and the blank ones. |
| 322 | 6 | `private static String trimBlanks(String s)` | How a red line is cut of its blanks: batch-check.sh's reds_of strips spaces and tabs from the front and spaces, tabs and returns from the back. |
| 336 | 14 | `private static String[] redOf(String name, List<String> printed)` | For each printed line of a failed harness (failingLines, untagged), the red line it makes, or null when it makes none: a section title, a blank, and every line after the "(no FAIL line...)" one, which are the harness'... |
| 357 | 6 | `static List<String> redLines(String name, int code, List<String> printed)` | The red lines of one failed harness, the same lines batch-check.sh's reds_of makes of the log: "Name \| exit N", then "Name \| <line>" for each failing line printed, a section title and the last lines of a harness with ... |
| 365 | 5 | `static int newCount(List<String> red, Set<String> known)` | How many of the red lines are not in the record. |
| 378 | 18 | `static List<String> report(String name, int code, double seconds, List<String> printed, boolean lines, Set<String> known)` | What -q and -f print of one failed harness. |
| 403 | 8 | `static String againstLine(Path record, Set<String> known, List<String> knownOnly, List<String> newRed)` | The one line under the summary that says which harnesses were known reds only and which had a NEW red; with no record, that none was read. |
| 421 | 8 | `static List<String> command(String java, String classpath, String name, boolean logged)` | How a harness is started: the JVM and classpath this one was, under -Xmx HEAP_MB and -Xms HEAP_START_MB (SP1), the timing properties (the mode, and the file the figures go to, TM3) if they are set here (a child JVM in... |
| 431 | 10 | `static int softLines(Path log)` | How many of a log's assertion lines went SOFT (Timing.SOFT_LINE); a log that cannot be read has none. |
| 449 | 21 | `static List<String> failingLines(Path log) throws IOException` | What -f prints of a failed harness's log: each failing line, after the title of the section it is in (once per section), at most FAIL_LINES in all; a log with no failing line in it - a harness that died some other way... |

