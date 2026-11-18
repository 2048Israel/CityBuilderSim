# TimingCheck.java - 728 lines · 32 methods · 8 constants · harnesses

`ham/citybuildersim/TimingCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> Checks hard and soft timing, JVM forwarding, suite coverage and paired medians.
> 
> WHY (TM1): concurrent suites missed clocks under another JVM's load, while
> correctness still needed hard verdicts. Fixtures force missed budgets and
> keep non-timing failures hard. TM3 checks TSV recording and the paired judge.
> Sections cover property values, child JVMs, suite routing, untouched verdicts,
> recorded figures and paired margins, with both passing and failing fixtures.
> The harness restores its timing properties before returning.

**Uses:** [Timing](Timing.md) (85), [AllChecks](AllChecks.md) (18), [TempDirs](TempDirs.md) (3)

## Sections

| line | section |
|---:|---|
| 182 | 1. HARD |
| 202 | 2. SOFT |
| 239 | 3. ONLY A CLOCK IS SOFT |
| 283 | 4. THE PROPERTY'S VALUES |
| 297 | 5. ACROSS THE JVM BOUNDARY |
| 355 | 6. THE SUITE GOES THROUGH IT |
| 504 | 7. THE REST OF THE SUITE IS UNTOUCHED |
| 518 | 8. THE FIGURES' RECORD (TM3) |
| 608 | 9. THE MARGIN'S ARITHMETIC (TM3): tools/timing-pair.awk |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 41 | `TimingCheck.FIXTURE_BUDGET_MS` | `0` | The fixture's own budget, in milliseconds: zero, so its measured figure always misses; never a real dial's budget. |
| 44 | `TimingCheck.SOURCES` | `Paths.get("src", "main", "java", "ham", "citybuildersim")` | Where the sources lie, seen from the repository root, which is where the suite runs. |
| 47 | `TimingCheck.COMPARES_DIAL` | `Pattern.compile("(<=?\|>=?)\\s*\\(?([A-Za-z]+\\.)?[A-Z][A-Z_]*_(MS\|NS)\\b" + "...` | A line that compares a figure against a *_MS or *_NS dial, either way round: what a timing assertion looked like before it went through Timing. |
| 52 | `TimingCheck.ALLOWED_FILE` | `"MapCheck.java"` | The file of the one allowance: the line below is a test of the clock's own resolution, which asserts no figure, so it stays a bare comparison. |
| 55 | `TimingCheck.ALLOWED_TEXT` | `"cpuStepMs()<= CPU_STEP_MS"` | The one line the dial rule allows outside Timing: MapCheck deciding whether the thread-CPU clock is fine enough to time with at all (a test of the clock's resolution, not an assertion). |
| 58 | `TimingCheck.FIXTURE_HARNESS` | `"TimingCheck$Fixture"` | What a child JVM is started as: the fixture's own main, below. |
| 61 | `TimingCheck.ALL_SOURCES` | `Paths.get("src", "main", "java")` | Where the walk of every source starts. |
| 612 | `TimingCheck.PAIR_AWK` | `Paths.get("tools", "timing-pair.awk")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 32 | `static int fails` |  |
| 64 | `private static volatile long sunk` | Kept so the measured work cannot be optimised away. |
| 95 | `int fails` |  |
| 96 | `private final ByteArrayOutputStream bytes` |  |
| 97 | `private final PrintStream out` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 699 | **type** `public class TimingCheck` | Checks hard and soft timing, JVM forwarding, suite coverage and paired medians. |
| 35 | 4 | `static void check(String label, boolean ok)` | This harness's own assertion line: never Timing.verdict, so its own lines are never soft. |
| 66 | 25 | `public static void main(String[] args) throws Exception` |  |
| 93 | 40 | **type** `public static final class Fixture` | A miniature timing harness that writes to a buffer locally or stdout in a child JVM. |
| 99 | 4 | `Fixture()` _(in TimingCheck.Fixture)_ |  |
| 104 | 4 | `Fixture(PrintStream to)` _(in TimingCheck.Fixture)_ |  |
| 109 | 4 | `void check(String label, boolean ok)` _(in TimingCheck.Fixture)_ |  |
| 115 | 3 | `String text()` _(in TimingCheck.Fixture)_ | Everything printed so far (empty when it prints to a stream). |
| 120 | 5 | `String last()` _(in TimingCheck.Fixture)_ | The last line printed, without its line end. |
| 127 | 5 | `public static void main(String[] args)` _(in TimingCheck.Fixture)_ | What a child JVM runs: one timing assertion of a measured figure against FIXTURE_BUDGET_MS, to System.out; exits with its failure count. |
| 135 | 12 | `static double measured()` | A small piece of work timed on the nanosecond clock, in milliseconds, repeated at double the size until the clock has moved: always above zero. |
| 149 | 4 | `static void setMode(String value)` | Sets the property this run's mode is read from; null clears it. |
| 154 | 3 | `static String modeName(String value)` |  |
| 158 | 3 | `static boolean endsOk(Fixture fx)` |  |
| 162 | 3 | `static boolean endsFail(Fixture fx)` |  |
| 166 | 5 | `static int count(String text, String part)` |  |
| 173 | 4 | `static void none(String label, List<String> offenders)` | One assertion for a rule that has offenders: it holds when there are none, and each offender is a failing line of its own, so -f names it. |
| 178 | 3 | `static String readSource(Path p) throws IOException` |  |

### 1. HARD (lines 182-201)

| line | len | member | says |
|---:|---:|---|---|
| 186 | 15 | `static void hard()` |  |

### 2. SOFT (lines 202-238)

| line | len | member | says |
|---:|---:|---|---|
| 206 | 32 | `static void soft()` |  |

### 3. ONLY A CLOCK IS SOFT (lines 239-282)

| line | len | member | says |
|---:|---:|---|---|
| 243 | 39 | `static void onlyAClock()` |  |

### 4. THE PROPERTY'S VALUES (lines 283-296)

| line | len | member | says |
|---:|---:|---|---|
| 287 | 9 | `static void propertyValues()` |  |

### 5. ACROSS THE JVM BOUNDARY (lines 297-354)

| line | len | member | says |
|---:|---:|---|---|
| 301 | 1 | **type** `private record Run(int exit, String output)` |  |
| 304 | 12 | `static Run runFixture(String javaExe, String classpath) throws Exception` | A child JVM started exactly as AllChecks starts a harness, with the parent's property the only source of its mode. |
| 317 | 37 | `static void acrossTheJvm() throws Exception` |  |

### 6. THE SUITE GOES THROUGH IT (lines 355-503)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 17 | `static void skippedSections()` |  |
| 377 | 26 | `static void suiteOrder() throws IOException` |  |
| 404 | 99 | `static void suiteGoesThroughIt() throws IOException` |  |

### 7. THE REST OF THE SUITE IS UNTOUCHED (lines 504-517)

| line | len | member | says |
|---:|---:|---|---|
| 508 | 9 | `static void restUntouched()` |  |

### 8. THE FIGURES' RECORD (TM3) (lines 518-607)

| line | len | member | says |
|---:|---:|---|---|
| 523 | 6 | `static List<String[]> rows(Path file) throws IOException` | A figures file's rows, each split on tabs (a trailing empty column kept); none when the file is not there. |
| 530 | 77 | `static void theFiguresRecord() throws Exception` |  |

### 9. THE MARGIN'S ARITHMETIC (TM3): tools/timing-pair.awk (lines 608-728)

| line | len | member | says |
|---:|---:|---|---|
| 615 | 17 | `static List<String> awk(List<String> args) throws Exception` | awk with these arguments, the repository root as its folder; its output lines, or null when awk cannot be started here. |
| 634 | 8 | `static String pairRows(String harness, String label, double[] main, double[] batch)` | The pairs file's rows for one label: the figures of each side in turn, one per round. |
| 644 | 9 | `static List<String> judge(Path dir, String pairs, String hard, String residual, String margin) throws Exception` | The judge's answer for these files: one "VERDICT\tline\twhy" per residual line. |
| 654 | 74 | `static void theMarginsArithmetic() throws Exception` |  |

