# TimingCheck.java - 787 lines · 35 methods · 9 constants · harnesses

`ham/citybuildersim/TimingCheck.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

> Checks hard and soft timing, JVM forwarding, suite coverage and paired medians.
> 
> WHY (TM1): concurrent suites missed clocks under another JVM's load, while
> correctness still needed hard verdicts. Fixtures force missed budgets and
> keep non-timing failures hard. TM3 checks TSV recording and the paired judge.
> Sections cover property values, child JVMs, suite routing, untouched verdicts,
> recorded figures and paired margins, with both passing and failing fixtures.
> The harness restores its timing properties before returning.

**Uses:** [Timing](Timing.md) (85), [AllChecks](AllChecks.md) (24), [TempDirs](TempDirs.md) (3), [FollowBench](FollowBench.md) (1)

## Sections

| line | section |
|---:|---|
| 183 | 1. HARD |
| 203 | 2. SOFT |
| 240 | 3. ONLY A CLOCK IS SOFT |
| 284 | 4. THE PROPERTY'S VALUES |
| 298 | 5. ACROSS THE JVM BOUNDARY |
| 367 | 6. THE SUITE GOES THROUGH IT |
| 516 | 7. THE REST OF THE SUITE IS UNTOUCHED |
| 530 | 8. THE FIGURES' RECORD (TM3) |
| 620 | 9. THE MARGIN'S ARITHMETIC (TM3): tools/timing-pair.awk |
| 740 | 10. THE DISPLAY (GB5) |

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
| 624 | `TimingCheck.PAIR_AWK` | `Paths.get("tools", "timing-pair.awk")` |  |
| 745 | `TimingCheck.DISPLAY_FIXTURE` | `"ChartFitCheck", NO_PIPELINE = "nonesuch"` | GB5: the display harness the children run (its toolkit start is its first act), and the pipeline Prism has none of. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 32 | `static int fails` |  |
| 64 | `private static volatile long sunk` | Kept so the measured work cannot be optimised away. |
| 96 | `int fails` |  |
| 97 | `private final ByteArrayOutputStream bytes` |  |
| 98 | `private final PrintStream out` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 30 | 758 | **type** `public class TimingCheck` | Checks hard and soft timing, JVM forwarding, suite coverage and paired medians. |
| 35 | 4 | `static void check(String label, boolean ok)` | This harness's own assertion line: never Timing.verdict, so its own lines are never soft. |
| 66 | 26 | `public static void main(String[] args) throws Exception` |  |
| 94 | 40 | **type** `public static final class Fixture` | A miniature timing harness that writes to a buffer locally or stdout in a child JVM. |
| 100 | 4 | `Fixture()` _(in TimingCheck.Fixture)_ |  |
| 105 | 4 | `Fixture(PrintStream to)` _(in TimingCheck.Fixture)_ |  |
| 110 | 4 | `void check(String label, boolean ok)` _(in TimingCheck.Fixture)_ |  |
| 116 | 3 | `String text()` _(in TimingCheck.Fixture)_ | Everything printed so far (empty when it prints to a stream). |
| 121 | 5 | `String last()` _(in TimingCheck.Fixture)_ | The last line printed, without its line end. |
| 128 | 5 | `public static void main(String[] args)` _(in TimingCheck.Fixture)_ | What a child JVM runs: one timing assertion of a measured figure against FIXTURE_BUDGET_MS, to System.out; exits with its failure count. |
| 136 | 12 | `static double measured()` | A small piece of work timed on the nanosecond clock, in milliseconds, repeated at double the size until the clock has moved: always above zero. |
| 150 | 4 | `static void setMode(String value)` | Sets the property this run's mode is read from; null clears it. |
| 155 | 3 | `static String modeName(String value)` |  |
| 159 | 3 | `static boolean endsOk(Fixture fx)` |  |
| 163 | 3 | `static boolean endsFail(Fixture fx)` |  |
| 167 | 5 | `static int count(String text, String part)` |  |
| 174 | 4 | `static void none(String label, List<String> offenders)` | One assertion for a rule that has offenders: it holds when there are none, and each offender is a failing line of its own, so -f names it. |
| 179 | 3 | `static String readSource(Path p) throws IOException` |  |

### 1. HARD (lines 183-202)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 15 | `static void hard()` |  |

### 2. SOFT (lines 203-239)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 32 | `static void soft()` |  |

### 3. ONLY A CLOCK IS SOFT (lines 240-283)

| line | len | member | says |
|---:|---:|---|---|
| 244 | 39 | `static void onlyAClock()` |  |

### 4. THE PROPERTY'S VALUES (lines 284-297)

| line | len | member | says |
|---:|---:|---|---|
| 288 | 9 | `static void propertyValues()` |  |

### 5. ACROSS THE JVM BOUNDARY (lines 298-366)

| line | len | member | says |
|---:|---:|---|---|
| 302 | 1 | **type** `private record Run(int exit, String output)` |  |
| 305 | 3 | `static Run runFixture(String javaExe, String classpath) throws Exception` | A child JVM started exactly as AllChecks starts a harness, with the parent's property the only source of its mode. |
| 310 | 12 | `static Run run(List<String> command) throws Exception` | A child JVM on this command alone: JDK_JAVA_OPTIONS (the gate box's display, the data files) is not passed down. |
| 323 | 43 | `static void acrossTheJvm() throws Exception` |  |

### 6. THE SUITE GOES THROUGH IT (lines 367-515)

| line | len | member | says |
|---:|---:|---|---|
| 371 | 17 | `static void skippedSections()` |  |
| 389 | 26 | `static void suiteOrder() throws IOException` |  |
| 416 | 99 | `static void suiteGoesThroughIt() throws IOException` |  |

### 7. THE REST OF THE SUITE IS UNTOUCHED (lines 516-529)

| line | len | member | says |
|---:|---:|---|---|
| 520 | 9 | `static void restUntouched()` |  |

### 8. THE FIGURES' RECORD (TM3) (lines 530-619)

| line | len | member | says |
|---:|---:|---|---|
| 535 | 6 | `static List<String[]> rows(Path file) throws IOException` | A figures file's rows, each split on tabs (a trailing empty column kept); none when the file is not there. |
| 542 | 77 | `static void theFiguresRecord() throws Exception` |  |

### 9. THE MARGIN'S ARITHMETIC (TM3): tools/timing-pair.awk (lines 620-739)

| line | len | member | says |
|---:|---:|---|---|
| 627 | 17 | `static List<String> awk(List<String> args) throws Exception` | awk with these arguments, the repository root as its folder; its output lines, or null when awk cannot be started here. |
| 646 | 8 | `static String pairRows(String harness, String label, double[] main, double[] batch)` | The pairs file's rows for one label: the figures of each side in turn, one per round. |
| 656 | 9 | `static List<String> judge(Path dir, String pairs, String hard, String residual, String margin) throws Exception` | The judge's answer for these files: one "VERDICT\tline\twhy" per residual line. |
| 666 | 74 | `static void theMarginsArithmetic() throws Exception` |  |

### 10. THE DISPLAY (GB5) (lines 740-787)

| line | len | member | says |
|---:|---:|---|---|
| 748 | 7 | `static Run onMonocle(String...options) throws Exception` | The gate box draws on Monocle; the children do too, on this machine, with only these options (the box's own not passed down). |
| 756 | 31 | `static void noDisplayHarnessSkips() throws Exception` |  |

