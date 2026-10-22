# TimingCheck.java - 470 lines · 24 methods · 7 constants · harnesses

`ham/citybuildersim/TimingCheck.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

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
>      exactly OK and verdict(false) exactly FAIL in every mode.
> 
> It sets the property itself in every section and restores it at the end, so
> it passes whatever the run was given.

**Uses:** [Timing](Timing.md) (52), [AllChecks](AllChecks.md) (5)

## Sections

| line | section |
|---:|---|
| 208 | 1. HARD |
| 228 | 2. SOFT |
| 265 | 3. ONLY A CLOCK IS SOFT |
| 309 | 4. THE PROPERTY'S VALUES |
| 323 | 5. ACROSS THE JVM BOUNDARY |
| 381 | 6. THE SUITE GOES THROUGH IT |
| 457 | 7. THE REST OF THE SUITE IS UNTOUCHED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `TimingCheck.FIXTURE_BUDGET_MS` | `0` | The fixture's own budget, in milliseconds: zero, so its measured figure always misses; never a real dial's budget. |
| 73 | `TimingCheck.SOURCES` | `Paths.get("src", "main", "java", "ham", "citybuildersim")` | Where the sources lie, seen from the repository root, which is where the suite runs. |
| 76 | `TimingCheck.COMPARES_DIAL` | `Pattern.compile("(<=?\|>=?)\\s*\\(?([A-Za-z]+\\.)?[A-Z][A-Z_]*_(MS\|NS)\\b" + "...` | A line that compares a figure against a *_MS or *_NS dial, either way round: what a timing assertion looked like before it went through Timing. |
| 81 | `TimingCheck.ALLOWED_FILE` | `"MapCheck.java"` | The file of the one allowance: the line below is a test of the clock's own resolution, which asserts no figure, so it stays a bare comparison. |
| 84 | `TimingCheck.ALLOWED_TEXT` | `"cpuStepMs()<= CPU_STEP_MS"` | The one line the dial rule allows outside Timing: MapCheck deciding whether the thread-CPU clock is fine enough to time with at all (a test of the clock's resolution, not an assertion). |
| 87 | `TimingCheck.FIXTURE_HARNESS` | `"TimingCheck$Fixture"` | What a child JVM is started as: the fixture's own main, below. |
| 90 | `TimingCheck.ALL_SOURCES` | `Paths.get("src", "main", "java")` | Where the walk of every source starts. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `static int fails` |  |
| 93 | `private static volatile long sunk` | Kept so the measured work cannot be optimised away. |
| 121 | `int fails` |  |
| 122 | `private final ByteArrayOutputStream bytes` |  |
| 123 | `private final PrintStream out` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 412 | **type** `public class TimingCheck` | The harness for soft timing (batch TM1): a timing assertion fails hard, goes SOFT and uncounted under -Dcbs.timing=soft, and nothing else in the suite moves. |
| 64 | 4 | `static void check(String label, boolean ok)` | This harness's own assertion line: never Timing.verdict, so its own lines are never soft. |
| 95 | 17 | `public static void main(String[] args) throws Exception` |  |
| 119 | 40 | **type** `public static final class Fixture` | The fixture: a miniature harness with its own failure count and its own output, whose line is the suite's, printed through Timing.verdict. |
| 125 | 4 | `Fixture()` _(in TimingCheck.Fixture)_ |  |
| 130 | 4 | `Fixture(PrintStream to)` _(in TimingCheck.Fixture)_ |  |
| 135 | 4 | `void check(String label, boolean ok)` _(in TimingCheck.Fixture)_ |  |
| 141 | 3 | `String text()` _(in TimingCheck.Fixture)_ | Everything printed so far (empty when it prints to a stream). |
| 146 | 5 | `String last()` _(in TimingCheck.Fixture)_ | The last line printed, without its line end. |
| 153 | 5 | `public static void main(String[] args)` _(in TimingCheck.Fixture)_ | What a child JVM runs: one timing assertion of a measured figure against FIXTURE_BUDGET_MS, to System.out; exits with its failure count. |
| 161 | 12 | `static double measured()` | A small piece of work timed on the nanosecond clock, in milliseconds, repeated at double the size until the clock has moved: always above zero. |
| 175 | 4 | `static void setMode(String value)` | Sets the property this run's mode is read from; null clears it. |
| 180 | 3 | `static String modeName(String value)` |  |
| 184 | 3 | `static boolean endsOk(Fixture fx)` |  |
| 188 | 3 | `static boolean endsFail(Fixture fx)` |  |
| 192 | 5 | `static int count(String text, String part)` |  |
| 199 | 4 | `static void none(String label, List<String> offenders)` | One assertion for a rule that has offenders: it holds when there are none, and each offender is a failing line of its own, so -f names it. |
| 204 | 3 | `static String readSource(Path p) throws IOException` |  |

### 1. HARD (lines 208-227)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 15 | `static void hard()` |  |

### 2. SOFT (lines 228-264)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 32 | `static void soft()` |  |

### 3. ONLY A CLOCK IS SOFT (lines 265-308)

| line | len | member | says |
|---:|---:|---|---|
| 269 | 39 | `static void onlyAClock()` |  |

### 4. THE PROPERTY'S VALUES (lines 309-322)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 9 | `static void propertyValues()` |  |

### 5. ACROSS THE JVM BOUNDARY (lines 323-380)

| line | len | member | says |
|---:|---:|---|---|
| 327 | 1 | **type** `private record Run(int exit, String output)` |  |
| 330 | 12 | `static Run runFixture(String javaExe, String classpath) throws Exception` | A child JVM started exactly as AllChecks starts a harness, with the parent's property the only source of its mode. |
| 343 | 37 | `static void acrossTheJvm() throws Exception` |  |

### 6. THE SUITE GOES THROUGH IT (lines 381-456)

| line | len | member | says |
|---:|---:|---|---|
| 385 | 71 | `static void suiteGoesThroughIt() throws IOException` |  |

### 7. THE REST OF THE SUITE IS UNTOUCHED (lines 457-470)

| line | len | member | says |
|---:|---:|---|---|
| 461 | 9 | `static void restUntouched()` |  |

