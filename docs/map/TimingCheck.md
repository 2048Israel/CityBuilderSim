# TimingCheck.java - 733 lines · 30 methods · 8 constants · harnesses

`ham/citybuildersim/TimingCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> The harness for soft timing (batch TM1): a timing assertion fails hard, goes SOFT and uncounted under -Dcbs.timing=soft, and nothing else in the suite moves.
> 
> WHY THIS EXISTS (batch TM1, 2026-10-18). Batches on the PC took turns under
> "one JVM at a time on the machine" because only the timing assertions
> clashed under another JVM's load (EarthTerrainCheck 8: 116 ms against 80
> with Jerus's game running, 27.8 without). The property lets two suites
> check side by side; a property that could soften anything but a clock would
> let a real failure through unseen, so this holds, with a fixture that cannot
> but miss its budget, that the property touches only a clock - and holds the
> suite to the one helper (Timing) that reads it.
> 
> What it has to prove:
>   1. hard (the property unset, or "hard"): the fixture's timing assertion
>      prints FAIL and counts 1; a figure within a budget it can meet prints OK;
>   2. soft: the same missed assertion's line says SOFT with its figure and
>      budget, matches Timing.SOFT_LINE and counts nothing; the unit is the one
>      the assertion names; a strict miss (below, equal figures) goes soft too
>      and fails hard;
>   3. only a clock is soft: a false non-timing assertion is FAIL and counted,
>      whichever side of && a miss is on; a told miss is cleared, as is one a
>      FAIL swallowed; a NaN figure is FAIL even soft; two misses in one
>      condition are told on the one line;
>   4. soft() is true for exactly "soft";
>   5. across the JVM boundary: AllChecks.command forwards the property, and a
>      child JVM started as AllChecks starts one exits 0 and prints SOFT when
>      soft, exits 1 and prints FAIL when not; AllChecks.softLines counts the
>      SOFT lines of a log;
>   6. the suite goes through it: no *Check source compares a figure against a
>      *_MS or *_NS dial but through Timing (bar one allowance), the harnesses
>      that call it are Timing.HARNESSES, each prints through Timing.verdict,
>      the property is read in one place, and AllChecks runs every one of them
>      and this;
>   7. the rest of the suite is untouched: with no miss held, verdict(true) is
>      exactly OK and verdict(false) exactly FAIL in every mode;
>   8. the figures' record (TM3): with -Dcbs.timing.tsv naming a file, each
>      assertion's line writes a row for every figure it judged - harness,
>      label, measured, budget, unit, status (OK, SOFT or FAIL), the figure's
>      place in the assertion - and with none named nothing is written; a
>      child JVM started as AllChecks starts one writes to the same file;
>   9. the margin's arithmetic (tools/timing-pair.awk, which the ship gate runs
>      on main's figures and the merge's): the batch passes at the margin and
>      stops a hair over it, the median decides and not one slow round, main's
>      median over the budget too or the batch's within it passes (SP1 item 7),
>      a side with fewer than two figures stops, a line that is no clock's
>      stops, and a failed harness's own count passes only with a timing red
>      that passes.
> 
> A harness that times prints its lines through the labelled verdict
> (Timing.verdict(ok, label)), and no assertion compares two measured times
> but through Timing (section 6): MapCheck's "the view adds nothing a tile",
> which compared the view's time with section 5's and so stayed hard in the
> soft suite, went red on a merge that could not reach it (CC1-S, 2026-10-26).
> 
> It sets the property itself in every section and restores it at the end, so
> it passes whatever the run was given.

**Uses:** [Timing](Timing.md) (81), [AllChecks](AllChecks.md) (6), [TempDirs](TempDirs.md) (2)

## Sections

| line | section |
|---:|---|
| 232 | 1. HARD |
| 252 | 2. SOFT |
| 289 | 3. ONLY A CLOCK IS SOFT |
| 333 | 4. THE PROPERTY'S VALUES |
| 347 | 5. ACROSS THE JVM BOUNDARY |
| 405 | 6. THE SUITE GOES THROUGH IT |
| 509 | 7. THE REST OF THE SUITE IS UNTOUCHED |
| 523 | 8. THE FIGURES' RECORD (TM3) |
| 613 | 9. THE MARGIN'S ARITHMETIC (TM3): tools/timing-pair.awk |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `TimingCheck.FIXTURE_BUDGET_MS` | `0` | The fixture's own budget, in milliseconds: zero, so its measured figure always misses; never a real dial's budget. |
| 91 | `TimingCheck.SOURCES` | `Paths.get("src", "main", "java", "ham", "citybuildersim")` | Where the sources lie, seen from the repository root, which is where the suite runs. |
| 94 | `TimingCheck.COMPARES_DIAL` | `Pattern.compile("(<=?\|>=?)\\s*\\(?([A-Za-z]+\\.)?[A-Z][A-Z_]*_(MS\|NS)\\b" + "...` | A line that compares a figure against a *_MS or *_NS dial, either way round: what a timing assertion looked like before it went through Timing. |
| 99 | `TimingCheck.ALLOWED_FILE` | `"MapCheck.java"` | The file of the one allowance: the line below is a test of the clock's own resolution, which asserts no figure, so it stays a bare comparison. |
| 102 | `TimingCheck.ALLOWED_TEXT` | `"cpuStepMs()<= CPU_STEP_MS"` | The one line the dial rule allows outside Timing: MapCheck deciding whether the thread-CPU clock is fine enough to time with at all (a test of the clock's resolution, not an assertion). |
| 105 | `TimingCheck.FIXTURE_HARNESS` | `"TimingCheck$Fixture"` | What a child JVM is started as: the fixture's own main, below. |
| 108 | `TimingCheck.ALL_SOURCES` | `Paths.get("src", "main", "java")` | Where the walk of every source starts. |
| 617 | `TimingCheck.PAIR_AWK` | `Paths.get("tools", "timing-pair.awk")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static int fails` |  |
| 111 | `private static volatile long sunk` | Kept so the measured work cannot be optimised away. |
| 145 | `int fails` |  |
| 146 | `private final ByteArrayOutputStream bytes` |  |
| 147 | `private final PrintStream out` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 657 | **type** `public class TimingCheck` | The harness for soft timing (batch TM1): a timing assertion fails hard, goes SOFT and uncounted under -Dcbs.timing=soft, and nothing else in the suite moves. |
| 82 | 4 | `static void check(String label, boolean ok)` | This harness's own assertion line: never Timing.verdict, so its own lines are never soft. |
| 113 | 23 | `public static void main(String[] args) throws Exception` |  |
| 143 | 40 | **type** `public static final class Fixture` | The fixture: a miniature harness with its own failure count and its own output, whose line is the suite's, printed through Timing.verdict. |
| 149 | 4 | `Fixture()` _(in TimingCheck.Fixture)_ |  |
| 154 | 4 | `Fixture(PrintStream to)` _(in TimingCheck.Fixture)_ |  |
| 159 | 4 | `void check(String label, boolean ok)` _(in TimingCheck.Fixture)_ |  |
| 165 | 3 | `String text()` _(in TimingCheck.Fixture)_ | Everything printed so far (empty when it prints to a stream). |
| 170 | 5 | `String last()` _(in TimingCheck.Fixture)_ | The last line printed, without its line end. |
| 177 | 5 | `public static void main(String[] args)` _(in TimingCheck.Fixture)_ | What a child JVM runs: one timing assertion of a measured figure against FIXTURE_BUDGET_MS, to System.out; exits with its failure count. |
| 185 | 12 | `static double measured()` | A small piece of work timed on the nanosecond clock, in milliseconds, repeated at double the size until the clock has moved: always above zero. |
| 199 | 4 | `static void setMode(String value)` | Sets the property this run's mode is read from; null clears it. |
| 204 | 3 | `static String modeName(String value)` |  |
| 208 | 3 | `static boolean endsOk(Fixture fx)` |  |
| 212 | 3 | `static boolean endsFail(Fixture fx)` |  |
| 216 | 5 | `static int count(String text, String part)` |  |
| 223 | 4 | `static void none(String label, List<String> offenders)` | One assertion for a rule that has offenders: it holds when there are none, and each offender is a failing line of its own, so -f names it. |
| 228 | 3 | `static String readSource(Path p) throws IOException` |  |

### 1. HARD (lines 232-251)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 15 | `static void hard()` |  |

### 2. SOFT (lines 252-288)

| line | len | member | says |
|---:|---:|---|---|
| 256 | 32 | `static void soft()` |  |

### 3. ONLY A CLOCK IS SOFT (lines 289-332)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 39 | `static void onlyAClock()` |  |

### 4. THE PROPERTY'S VALUES (lines 333-346)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 9 | `static void propertyValues()` |  |

### 5. ACROSS THE JVM BOUNDARY (lines 347-404)

| line | len | member | says |
|---:|---:|---|---|
| 351 | 1 | **type** `private record Run(int exit, String output)` |  |
| 354 | 12 | `static Run runFixture(String javaExe, String classpath) throws Exception` | A child JVM started exactly as AllChecks starts a harness, with the parent's property the only source of its mode. |
| 367 | 37 | `static void acrossTheJvm() throws Exception` |  |

### 6. THE SUITE GOES THROUGH IT (lines 405-508)

| line | len | member | says |
|---:|---:|---|---|
| 409 | 99 | `static void suiteGoesThroughIt() throws IOException` |  |

### 7. THE REST OF THE SUITE IS UNTOUCHED (lines 509-522)

| line | len | member | says |
|---:|---:|---|---|
| 513 | 9 | `static void restUntouched()` |  |

### 8. THE FIGURES' RECORD (TM3) (lines 523-612)

| line | len | member | says |
|---:|---:|---|---|
| 528 | 6 | `static List<String[]> rows(Path file) throws IOException` | A figures file's rows, each split on tabs (a trailing empty column kept); none when the file is not there. |
| 535 | 77 | `static void theFiguresRecord() throws Exception` |  |

### 9. THE MARGIN'S ARITHMETIC (TM3): tools/timing-pair.awk (lines 613-733)

| line | len | member | says |
|---:|---:|---|---|
| 620 | 17 | `static List<String> awk(List<String> args) throws Exception` | awk with these arguments, the repository root as its folder; its output lines, or null when awk cannot be started here. |
| 639 | 8 | `static String pairRows(String harness, String label, double[] main, double[] batch)` | The pairs file's rows for one label: the figures of each side in turn, one per round. |
| 649 | 9 | `static List<String> judge(Path dir, String pairs, String hard, String residual, String margin) throws Exception` | The judge's answer for these files: one "VERDICT\tline\twhy" per residual line. |
| 659 | 74 | `static void theMarginsArithmetic() throws Exception` |  |

