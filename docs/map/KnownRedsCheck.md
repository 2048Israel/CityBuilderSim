# KnownRedsCheck.java - 252 lines · 9 methods · 7 constants · harnesses

`ham/citybuildersim/KnownRedsCheck.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> The harness for AllChecks' known reds and harness time limit.
> 
> WHY (FW7): a second failing HealthCheck assertion hid behind its known-red
> verdict. Fixtures exercise the real runner's line tagging and the shell's
> judge. GF1 adds a child whose descendant never ends, reproducing CD9's hang.
> 
> Proves harness-scoped parts, exact records, count guards and agreement
> through the common judge and both shell callers. The time limit fails and ends a hung process tree,
> preserving an in-time child's exit and output. Fixture output stays out of
> this harness's own log so its intentional failures cannot be misread.

**Uses:** [AllChecks](AllChecks.md) (25), [KnownReds](KnownReds.md) (3), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 164 | 6. A HARNESS AND ITS DESCENDANTS HAVE A LIMIT (GF1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 38 | `KnownRedsCheck.NAME` | `"KnownRedsFixture"` | What the fixture is called in the red lines: not its class name, which has a $ in it. |
| 41 | `KnownRedsCheck.KNOWN_LABEL` | `"the fixture's known assertion holds"` | The fixture's first assertion, always false: the red a record holds. |
| 44 | `KnownRedsCheck.SECOND_LABEL` | `"the fixture's second assertion holds"` | The fixture's second assertion, false only when asked: the NEW red. |
| 47 | `KnownRedsCheck.SECOND_PROPERTY` | `"knownreds.second"` | The system property that makes the fixture fail its second assertion as well. |
| 50 | `KnownRedsCheck.FIXTURE_HARNESS` | `"KnownRedsCheck$Fixture"` | What a child JVM is started as: the fixture's own main, below. |
| 53 | `KnownRedsCheck.WAIT_SECONDS` | `120` | The longest a child JVM or the shell may take. |
| 56 | `KnownRedsCheck.LIMIT_SECONDS` | `2` | The short limit used to exercise the production wait without holding the suite for HARNESS_SECONDS. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 226 | **type** `public class KnownRedsCheck` | The harness for AllChecks' known reds and harness time limit. |
| 32 | 4 | `static void check(String label, boolean ok)` | This harness's own assertion line, the suite's. |
| 63 | 16 | **type** `public static class Fixture` | A miniature harness for a child JVM: one assertion that fails, a passing one whose label says FAILS, and when -Dknownreds.second=true a second that fails; its exit status is its count of failures, as any harness's. |
| 64 | 14 | `public static void main(String[] args)` _(in KnownRedsCheck.Fixture)_ |  |
| 81 | 1 | **type** `private record Run(int exit, List<String> printed)` | One run of the fixture: its exit status and the failing lines of its log, as AllChecks -f reads them. |
| 83 | 65 | `public static void main(String[] args) throws Exception` |  |
| 150 | 13 | **type** `public static class LimitFixture` | A fixture with a never-ending descendant, or an immediate successful exit. |
| 151 | 11 | `public static void main(String[] args) throws Exception` _(in KnownRedsCheck.LimitFixture)_ |  |

### 6. A HARNESS AND ITS DESCENDANTS HAVE A LIMIT (GF1) (lines 164-252)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 32 | `private static void limited(Path tmp) throws Exception` |  |
| 201 | 17 | `private static Run run(Path folder, boolean second) throws Exception` | Starts the fixture as AllChecks starts a harness and returns its exit status and failing lines. |
| 220 | 1 | **type** `private record Judged(int exit, String output)` | What batch-check.sh --judge said and exited with. |
| 223 | 15 | `private static Judged judge(Path folder, String suiteText, Path record) throws Exception` | Runs the script on a saved suite log against a record, from the repository root, which is where the suite runs. |
| 240 | 3 | `private static String slashes(Path p)` | A path as a POSIX shell takes it on every system: forward slashes. |
| 245 | 6 | `private static String suiteLog(Run run, Set<String> known)` | A suite log as AllChecks -f would print it for this one failed harness: its report, a blank line, the summary. |

