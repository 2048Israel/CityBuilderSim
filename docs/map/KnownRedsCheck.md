# KnownRedsCheck.java - 346 lines · 17 methods · 7 constants · harnesses

`ham/citybuildersim/KnownRedsCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The harness for AllChecks' known reds and harness time limit.
> 
> WHY (FW7): a second failing HealthCheck assertion hid behind its known-red
> verdict. Fixtures exercise the real runner's line tagging and the shell's
> judge. GF1 adds a child whose descendant never ends, reproducing CD9's hang.
> 
> Proves that sections and passing lines stay out of reds, the same red is
> known, a second is NEW, no record tags nothing, and batch-check.sh judges
> the same saved logs. The time limit fails and ends a hung process tree,
> preserving an in-time child's exit and output. Fixture output stays out of
> this harness's own log so its intentional failures cannot be misread.

**Uses:** [AllChecks](AllChecks.md) (31), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 122 | 6. A HARNESS AND ITS DESCENDANTS HAVE A LIMIT (GF1) |
| 194 | 1. THE RED LINES OF A FAILED HARNESS |
| 213 | 2. THE SAME RED AGAIN IS A KNOWN RED |
| 237 | 3. A SECOND FAILING ASSERTION IS A NEW RED |
| 266 | 4. WITH NO RECORD NOTHING IS TAGGED |
| 284 | 5. BATCH-CHECK.SH JUDGES THE SAME LOGS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 37 | `KnownRedsCheck.NAME` | `"KnownRedsFixture"` | What the fixture is called in the red lines: not its class name, which has a $ in it. |
| 40 | `KnownRedsCheck.KNOWN_LABEL` | `"the fixture's known assertion holds"` | The fixture's first assertion, always false: the red a record holds. |
| 43 | `KnownRedsCheck.SECOND_LABEL` | `"the fixture's second assertion holds"` | The fixture's second assertion, false only when asked: the NEW red. |
| 46 | `KnownRedsCheck.SECOND_PROPERTY` | `"knownreds.second"` | The system property that makes the fixture fail its second assertion as well. |
| 49 | `KnownRedsCheck.FIXTURE_HARNESS` | `"KnownRedsCheck$Fixture"` | What a child JVM is started as: the fixture's own main, below. |
| 52 | `KnownRedsCheck.WAIT_SECONDS` | `120` | The longest a child JVM or the shell may take. |
| 55 | `KnownRedsCheck.LIMIT_SECONDS` | `2` | The short limit used to exercise the production wait without holding the suite for HARNESS_SECONDS. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 28 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 26 | 321 | **type** `public class KnownRedsCheck` | The harness for AllChecks' known reds and harness time limit. |
| 31 | 4 | `static void check(String label, boolean ok)` | This harness's own assertion line, the suite's. |
| 62 | 16 | **type** `public static class Fixture` | A miniature harness for a child JVM: one assertion that fails, a passing one whose label says FAILS, and when -Dknownreds.second=true a second that fails; its exit status is its count of failures, as any harness's. |
| 63 | 14 | `public static void main(String[] args)` _(in KnownRedsCheck.Fixture)_ |  |
| 80 | 1 | **type** `private record Run(int exit, List<String> printed)` | One run of the fixture: its exit status and the failing lines of its log, as AllChecks -f reads them. |
| 82 | 24 | `public static void main(String[] args) throws Exception` |  |
| 108 | 13 | **type** `public static class LimitFixture` | A fixture with a never-ending descendant, or an immediate successful exit. |
| 109 | 11 | `public static void main(String[] args) throws Exception` _(in KnownRedsCheck.LimitFixture)_ |  |

### 6. A HARNESS AND ITS DESCENDANTS HAVE A LIMIT (GF1) (lines 122-193)

| line | len | member | says |
|---:|---:|---|---|
| 125 | 32 | `private static void limited(Path tmp) throws Exception` |  |
| 159 | 3 | `private static List<String> redLines(Run run)` | The red lines the fixture's run makes. |
| 164 | 17 | `private static Run run(Path folder, boolean second) throws Exception` | Starts the fixture as AllChecks starts a harness and returns its exit status and failing lines. |
| 183 | 5 | `private static void writeRecord(Path file, List<String> red) throws IOException` | Writes a record as ship.sh writes gate.txt: a comment line, a blank, then the red lines. |
| 190 | 3 | `private static boolean isSection(String redLine)` | Whether a red line, without its harness name, is a section title. |

### 1. THE RED LINES OF A FAILED HARNESS (lines 194-212)

| line | len | member | says |
|---:|---:|---|---|
| 198 | 14 | `private static void redLinesAre(Run first, List<String> red)` |  |

### 2. THE SAME RED AGAIN IS A KNOWN RED (lines 213-236)

| line | len | member | says |
|---:|---:|---|---|
| 217 | 19 | `private static void knownRed(Run first, List<String> red, Set<String> known)` |  |

### 3. A SECOND FAILING ASSERTION IS A NEW RED (lines 237-265)

| line | len | member | says |
|---:|---:|---|---|
| 241 | 24 | `private static void newRed(Run second, Set<String> known)` |  |

### 4. WITH NO RECORD NOTHING IS TAGGED (lines 266-283)

| line | len | member | says |
|---:|---:|---|---|
| 270 | 13 | `private static void noRecord(Path tmp, Run first) throws IOException` |  |

### 5. BATCH-CHECK.SH JUDGES THE SAME LOGS (lines 284-346)

| line | len | member | says |
|---:|---:|---|---|
| 289 | 1 | **type** `private record Judged(int exit, String output)` | What batch-check.sh --judge said and exited with. |
| 292 | 15 | `private static Judged judge(Path folder, String suiteText, Path record) throws Exception` | Runs the script on a saved suite log against a record, from the repository root, which is where the suite runs. |
| 309 | 3 | `private static String slashes(Path p)` | A path as a POSIX shell takes it on every system: forward slashes. |
| 314 | 6 | `private static String suiteLog(Run run, Set<String> known)` | A suite log as AllChecks -f would print it for this one failed harness: its report, a blank line, the summary. |
| 321 | 25 | `private static void judged(Path tmp, Run first, Run second, Set<String> known, Path recordFile) throws Exception` |  |

