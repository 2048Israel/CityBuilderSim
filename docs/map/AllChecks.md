# AllChecks.java - 380 lines · 14 methods · 12 constants · harnesses

`ham/citybuildersim/AllChecks.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> Runs every harness, one JVM each, and says which failed.
> 
> Twenty-nine harnesses and no runner meant "all checks pass" was a claim
> made by whoever had the patience to run twenty-nine main() methods by hand
> that day - and a harness nobody ran is a harness that does not exist. Each
> one calls System.exit() with its verdict, so they cannot share a JVM; this
> spawns them with the JVM and classpath it was itself started with.
> 
>     java -cp <classes> ham.citybuildersim.AllChecks            everything
>     java -cp <classes> ham.citybuildersim.AllChecks -q         verdicts only
>     java -cp <classes> ham.citybuildersim.AllChecks -f         failures only
>     java -cp <classes> ham.citybuildersim.AllChecks Labour Money   a subset, by name
>     java -cp <classes> ham.citybuildersim.AllChecks --timing   the timing harnesses alone (Timing.HARNESSES)
> 
> Exit status is the number of harnesses that failed, so a build script can
> stop on it. BuildMenuCheck, KeptCheck and ChartFitCheck need JavaFX on the classpath and are skipped,
> with a note, when it is not there.
> 
> -f is for an AI session, where every line printed is read and paid for
> (2026-10-13). -q printed a line for each of a hundred-odd harnesses and,
> for a red one, only "FAILED", so the session ran it again in full to find
> the assertion and carried its thousands of lines from then on. -f keeps
> each harness's whole output in target/checks/NAME.log, says nothing of a
> harness that passed, and for one that failed prints its failing lines
> under the section they are in - at most FAIL_LINES of them - and where
> the rest is.
> 
> Timing (TM1, 2026-10-18): -Dcbs.timing=soft given to this JVM is given to
> every harness it starts, where a timing figure over its budget is SOFT and
> not counted (Timing). -f counts the SOFT lines in every log, a passing
> harness's too, and says how many under the summary. --timing runs only the
> harnesses that time something: what the ship gate runs, hard.
> 
> Known reds (FW7, 2026-10-25): HealthCheck was once a recorded red of main's
> and a second assertion in it went red; -q threw each harness's output away
> and printed "HealthCheck FAILED", the same as the day before, so nobody saw
> it. -q now keeps each output in target/checks/NAME.log as -f does, and both
> read main's record of its reds (docs/notes/gate.txt as ship.sh writes it,
> or the file named by -Dcbs.knownReds, which is what batch-check.sh gives
> it) and say of every failed harness "known red" when each of its red lines
> is in the record, "NEW red: N of M red lines" when not. -f also tags each
> failing line [known] or [NEW], and a last line names the harnesses of each
> kind. A red line is what batch-check.sh's reds_of reads from this output,
> word for word (redLines), so the two judges count the same. With no record
> nothing is tagged and -f prints as it did before. KnownRedsCheck causes both
> cases with a fixture of its own.

**Uses:** [Timing](Timing.md) (7)

**Used by (2):** [KnownRedsCheck](KnownRedsCheck.md), [TimingCheck](TimingCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 66 | `AllChecks.HARNESSES` | `{ "BuildingDataCheck", "BrandingCheck", "NewGameCheck", "WorldCheck", "GridCh...` | In the order they are cheapest to fail. |
| 82 | `AllChecks.FAIL_LINES` | `40` | The most lines -f prints for one failed harness; the rest stays in its log. |
| 85 | `AllChecks.LOGS` | `Paths.get("target", "checks")` | Where -q and -f keep each harness's whole output. |
| 88 | `AllChecks.SECTION` | `Pattern.compile("^\\s*---\\s.*\\s---\\s*$")` | A harness's section title, "--- what it proves ---". |
| 91 | `AllChecks.SUMMARY` | `Pattern.compile("^\\s*\\d+ FAILED\\s*$")` | A harness's closing count, "N FAILED": printed, but not under the last section's title. |
| 94 | `AllChecks.FAILING` | `Pattern.compile("\\bFAIL(ED)?\\b\|Exception\|^\\S*Error\\b\|^Caused by")` | A line that says something failed: an assertion's FAIL, the "N FAILED" summary, an exception. |
| 97 | `AllChecks.IN_ALL` | `Pattern.compile("^\\.\\.\\.[0-9]+ failing lines in all: ")` | The line failingLines prints when it left failing lines out: "... |
| 100 | `AllChecks.GATE` | `Paths.get("docs", "notes", "gate.txt")` | Main's recorded reds, one line each after the "#" lines: what ship.sh writes at every gate. |
| 103 | `AllChecks.KNOWN_PROPERTY` | `"cbs.knownReds"` | The system property that names the record when it is not GATE; batch-check.sh gives it the record it judges against. |
| 106 | `AllChecks.NO_FAIL_LINE` | `"(no FAIL line; its last lines:)"` | The line a log says before the last lines of a harness that died with no failing line of its own. |
| 109 | `AllChecks.KNOWN_TAG` | `"[known] "` | The tag -f puts before a failing line that is in the record; the same width as NEW_TAG. |
| 112 | `AllChecks.NEW_TAG` | `"[NEW] "` | The tag -f puts before a failing line that is not in the record. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 63 | 318 | **type** `public class AllChecks` | Runs every harness, one JVM each, and says which failed. |
| 114 | 80 | `public static void main(String[] args) throws Exception` |  |
| 196 | 3 | `static String header(String name, int code, double seconds)` | The first line of a harness's entry: its name, its verdict as a number or OK, and how long it took. |
| 201 | 4 | `static String summaryLine(int ran, int failed, double seconds)` | The line under the last harness: how many ran and how they ended. |
| 210 | 4 | `static Path recordFile()` | The record this run reads: the file named by -Dcbs.knownReds when that is set, else GATE. |
| 222 | 9 | `static Set<String> knownReds(Path file) throws IOException` | Main's recorded reds, as batch-check.sh reads them: the lines of the file, each exactly as written, but for the lines that start with # and the blank ones. |
| 233 | 6 | `private static String trimBlanks(String s)` | How a red line is cut of its blanks: batch-check.sh's reds_of strips spaces and tabs from the front and spaces, tabs and returns from the back. |
| 247 | 14 | `private static String[] redOf(String name, List<String> printed)` | For each printed line of a failed harness (failingLines, untagged), the red line it makes, or null when it makes none: a section title, a blank, and every line after the "(no FAIL line...)" one, which are the harness'... |
| 268 | 6 | `static List<String> redLines(String name, int code, List<String> printed)` | The red lines of one failed harness, the same lines batch-check.sh's reds_of makes of the log: "Name \| exit N", then "Name \| <line>" for each failing line printed, a section title and the last lines of a harness with ... |
| 276 | 5 | `static int newCount(List<String> red, Set<String> known)` | How many of the red lines are not in the record. |
| 289 | 18 | `static List<String> report(String name, int code, double seconds, List<String> printed, boolean lines, Set<String> known)` | What -q and -f print of one failed harness. |
| 314 | 8 | `static String againstLine(Path record, Set<String> known, List<String> knownOnly, List<String> newRed)` | The one line under the summary that says which harnesses were known reds only and which had a NEW red; with no record, that none was read. |
| 331 | 8 | `static List<String> command(String java, String classpath, String name, boolean logged)` | How a harness is started: the JVM and classpath this one was, the timing properties (the mode, and the file the figures go to, TM3) if they are set here (a child JVM inherits no system property, only JDK_JAVA_OPTIONS)... |
| 341 | 10 | `static int softLines(Path log)` | How many of a log's assertion lines went SOFT (Timing.SOFT_LINE); a log that cannot be read has none. |
| 359 | 21 | `static List<String> failingLines(Path log) throws IOException` | What -f prints of a failed harness's log: each failing line, after the title of the section it is in (once per section), at most FAIL_LINES in all; a log with no failing line in it - a harness that died some other way... |

