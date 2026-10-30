# KnownRedsCheck.java - 311 lines · 15 methods · 6 constants · harnesses

`ham/citybuildersim/KnownRedsCheck.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The harness for AllChecks' known reds (batch FW7): a failed harness is said
> to be a known red or a NEW one against main's record, line by line, and
> batch-check.sh judges the same logs the same way.
> 
> WHY (FW7, 2026-10-25): HealthCheck was a recorded red of main's, and a second
> assertion in it went red. AllChecks -q printed "HealthCheck FAILED" as it had
> the day before and nobody saw it. batch-check.sh already compared the red
> lines against gate.txt word for word, but AllChecks itself knew nothing of
> the record, so what a session read of -q and -f could not tell its own reds
> from main's. A fixture harness (Fixture, below) fails one assertion and then,
> on request, a second, and this runs it as AllChecks runs a harness, so both
> cases are caused and not assumed: the same red again is a known red, a second
> one is a NEW red and is named.
> 
> What it has to prove:
>   1. the red lines of a failed harness are its exit, each failing line and its
>      count, and never a section title or a passing line;
>   2. the record those lines make reads back from a file as three lines, and the
>      same failure against it is a known red, in one line under -q and every
>      failing line tagged [known] under -f;
>   3. a second failing assertion is a NEW red: the header says how many red
>      lines are new, -f tags that line [NEW] and the first one still [known],
>      and the count in the header is the count of tags;
>   4. with no record nothing is tagged, and -f prints as it did before FW7;
>   5. batch-check.sh --judge reads the same logs: exit 3 for the known red,
>      exit 4 and the new line for the second, exit 3 again for a log from before
>      FW7 with no tags, and exit 1 when the header's count and its own disagree
>      (not run where sh cannot be started).
> 
> This harness's own output never echoes the fixture's, so that AllChecks -f,
> reading this log for failing lines, finds only this harness's own.

**Uses:** [AllChecks](AllChecks.md) (25), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 159 | 1. THE RED LINES OF A FAILED HARNESS |
| 178 | 2. THE SAME RED AGAIN IS A KNOWN RED |
| 202 | 3. A SECOND FAILING ASSERTION IS A NEW RED |
| 231 | 4. WITH NO RECORD NOTHING IS TAGGED |
| 249 | 5. BATCH-CHECK.SH JUDGES THE SAME LOGS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 57 | `KnownRedsCheck.NAME` | `"KnownRedsFixture"` | What the fixture is called in the red lines: not its class name, which has a $ in it. |
| 60 | `KnownRedsCheck.KNOWN_LABEL` | `"the fixture's known assertion holds"` | The fixture's first assertion, always false: the red a record holds. |
| 63 | `KnownRedsCheck.SECOND_LABEL` | `"the fixture's second assertion holds"` | The fixture's second assertion, false only when asked: the NEW red. |
| 66 | `KnownRedsCheck.SECOND_PROPERTY` | `"knownreds.second"` | The system property that makes the fixture fail its second assertion as well. |
| 69 | `KnownRedsCheck.FIXTURE_HARNESS` | `"KnownRedsCheck$Fixture"` | What a child JVM is started as: the fixture's own main, below. |
| 72 | `KnownRedsCheck.WAIT_SECONDS` | `120` | The longest a child JVM or the shell may take. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 46 | 266 | **type** `public class KnownRedsCheck` | The harness for AllChecks' known reds (batch FW7): a failed harness is said to be a known red or a NEW one against main's record, line by line, and batch-check.sh judges the same logs the same way. |
| 51 | 4 | `static void check(String label, boolean ok)` | This harness's own assertion line, the suite's. |
| 79 | 16 | **type** `public static class Fixture` | A miniature harness for a child JVM: one assertion that fails, a passing one whose label says FAILS, and when -Dknownreds.second=true a second that fails; its exit status is its count of failures, as any harness's. |
| 80 | 14 | `public static void main(String[] args)` _(in KnownRedsCheck.Fixture)_ |  |
| 97 | 1 | **type** `private record Run(int exit, List<String> printed)` | One run of the fixture: its exit status and the failing lines of its log, as AllChecks -f reads them. |
| 99 | 23 | `public static void main(String[] args) throws Exception` |  |
| 124 | 3 | `private static List<String> redLines(Run run)` | The red lines the fixture's run makes. |
| 129 | 17 | `private static Run run(Path folder, boolean second) throws Exception` | Starts the fixture as AllChecks starts a harness and returns its exit status and failing lines. |
| 148 | 5 | `private static void writeRecord(Path file, List<String> red) throws IOException` | Writes a record as ship.sh writes gate.txt: a comment line, a blank, then the red lines. |
| 155 | 3 | `private static boolean isSection(String redLine)` | Whether a red line, without its harness name, is a section title. |

### 1. THE RED LINES OF A FAILED HARNESS (lines 159-177)

| line | len | member | says |
|---:|---:|---|---|
| 163 | 14 | `private static void redLinesAre(Run first, List<String> red)` |  |

### 2. THE SAME RED AGAIN IS A KNOWN RED (lines 178-201)

| line | len | member | says |
|---:|---:|---|---|
| 182 | 19 | `private static void knownRed(Run first, List<String> red, Set<String> known)` |  |

### 3. A SECOND FAILING ASSERTION IS A NEW RED (lines 202-230)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 24 | `private static void newRed(Run second, Set<String> known)` |  |

### 4. WITH NO RECORD NOTHING IS TAGGED (lines 231-248)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 13 | `private static void noRecord(Path tmp, Run first) throws IOException` |  |

### 5. BATCH-CHECK.SH JUDGES THE SAME LOGS (lines 249-311)

| line | len | member | says |
|---:|---:|---|---|
| 254 | 1 | **type** `private record Judged(int exit, String output)` | What batch-check.sh --judge said and exited with. |
| 257 | 15 | `private static Judged judge(Path folder, String suiteText, Path record) throws Exception` | Runs the script on a saved suite log against a record, from the repository root, which is where the suite runs. |
| 274 | 3 | `private static String slashes(Path p)` | A path as a POSIX shell takes it on every system: forward slashes. |
| 279 | 6 | `private static String suiteLog(Run run, Set<String> known)` | A suite log as AllChecks -f would print it for this one failed harness: its report, a blank line, the summary. |
| 286 | 25 | `private static void judged(Path tmp, Run first, Run second, Set<String> known, Path recordFile) throws Exception` |  |

