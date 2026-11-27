# EnsembleCheck.java - 470 lines · 25 methods · 10 constants · harnesses

`ham/citybuildersim/EnsembleCheck.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> Checks the ensemble runner, driver compilation, policy names and stored baseline.
> 
> WHY (EN1): an out-of-tree driver could break against LongPlaytest unnoticed
> until a batch needed its score. Fixtures prove both compiler verdicts, policy
> agreement and baseline parsing, including missing fields and malformed JSON.
> Run at the repository root, with -Dtools and -Ddocs overriding those folders.

**Uses:** [GameVersion](GameVersion.md) (2), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 79 | 1. the runner is in the tree |
| 91 | 2. the driver compiles against this tree |
| 167 | 3. the same policies |
| 260 | 4. the baseline reads |
| 441 | · THE FIXTURES, WHICH CAUSE THE CONDITION RATHER THAN WAITING FOR IT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 169 | `EnsembleCheck.POLICY_ENUM` | `Pattern.compile("\\benum\\s+Policy\\s*\\{([^}]*)\\}")` |  |
| 170 | `EnsembleCheck.POLICIES_LIST` | `Pattern.compile("(?m)^POLICIES\\s*=\\s*\\[([^\\]]*)\\]")` |  |
| 172 | `EnsembleCheck.QUOTED` | `Pattern.compile("\"([^\"]*)\"\|'([^']*)'")` | A name in double or single quotes, as either half lists its policies. |
| 262 | `EnsembleCheck.BASELINE_NAME` | `Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.md")` |  |
| 264 | `EnsembleCheck.CRITERIA` | `Pattern.compile("(?m)^criteria passed:\\s*(\\d+)\\s*/\\s*(\\d+)\\b")` | A stored score's verdict line: "criteria passed: N / M". |
| 265 | `EnsembleCheck.JSON_BLOCK` | `Pattern.compile("(?s)'''json\\s*\\R(.*?)\\R\\s*'''")` |  |
| 354 | `EnsembleCheck.VERDICT` | `"criteria passed: 5/6(vacuous, not counted: 0); missing runs: none; short run...` | A canned page's verdict line, for the reader's fixtures. |
| 356 | `EnsembleCheck.JSON` | `""" { "seeds": 4, "policies": { "ALPHA": {"final_pop_median": 1000.5, "runs_w...` | ...and its JSON block, sound as it stands; a fixture edits it into a page the reader should refuse. |
| 446 | `EnsembleCheck.BROKEN` | `""" package ham.citybuildersim; final class EnsembleCheckBroken { static void...` | Calls a method that no class has: the change the guard exists for, made by hand. |
| 455 | `EnsembleCheck.SOUND` | `""" package ham.citybuildersim; final class EnsembleCheckSound { static Class...` | Calls a class that is there. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 436 | **type** `public class EnsembleCheck` | Checks the ensemble runner, driver compilation, policy names and stored baseline. |
| 39 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 45 | 4 | `static void assertTrue(String label, boolean ok, String whyNot)` | An assertion that has a reason to print when it fails. |
| 51 | 3 | `static Path toolsDir()` | The folder the runner lives in. |
| 56 | 3 | `static Path baselineDir()` | The folder the stored baselines live in. |
| 60 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. the runner is in the tree (lines 79-90)

| line | len | member | says |
|---:|---:|---|---|
| 81 | 9 | `static void runnerIsThere() throws IOException` |  |

### 2. the driver compiles against this tree (lines 91-166)

| line | len | member | says |
|---:|---:|---|---|
| 93 | 41 | `static void driverCompiles() throws IOException` |  |
| 136 | 23 | `static List<String> compileProblems(JavaCompiler compiler, List<Path> sources, Path outDir) throws IOException` | Compiles against this JVM's classpath, returning English diagnostics or an empty list on success. |
| 161 | 5 | `static String firstOf(List<String> lines, int n)` | The first few of a list, one to a line, for a failing assertion to print. |

### 3. the same policies (lines 167-259)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 14 | `static List<String> driverPolicies(String source)` | The constants of the driver's Policy enum, in the order written; empty when the source has none. |
| 191 | 9 | `static List<String> scorerPolicies(String source)` | The strings of the scorer's POLICIES list, in the order written; empty when it has none. |
| 202 | 17 | `static String policyProblem(String driverSource, String scorerSource)` | Null when the two sources name the same policies, else why not. |
| 220 | 7 | `static String read(Path p)` |  |
| 229 | 30 | `static List<String> samePolicies()` | Section 3; returns the driver's policies, which section 4 holds the baseline to. |

### 4. the baseline reads (lines 260-440)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 6 | `static int[] versionOf(String s)` | The three numbers of x.y.z, or null when it is not one. |
| 276 | 5 | `static int compareVersions(String a, String b)` | Negative when a is the older version, by the numbers and not by the letters ("0.8.9" is older than "0.8.11"). |
| 283 | 3 | `static String newestBaseline(List<String> names)` | The file name of the newest baseline among these names, or null when none is named x.y.z.md. |
| 288 | 12 | `static String newestBaseline(List<String> names, String en2Version)` | EN2's untagged, data-backed baseline supersedes older literal-world scores until a later version is scored. |
| 301 | 3 | `private static boolean isNumber(JsonElement e)` |  |
| 305 | 5 | `private static boolean isInteger(JsonElement e)` |  |
| 312 | 33 | `static String baselineProblem(String text, List<String> policies)` | Checks the verdict and JSON seeds, takeoffs and per-policy population, finding and audit fields. |
| 347 | 5 | `static String canned(String verdictLine, String json)` | A baseline page with these two parts of it: the verdict line and the json. |
| 367 | 7 | `static String withJson(String from, String to)` | Changes canned JSON, failing the fixture if its replacement finds nothing. |
| 375 | 65 | `static void baselineReads(List<String> policies) throws IOException` |  |

### THE FIXTURES, WHICH CAUSE THE CONDITION RATHER THAN WAITING FOR IT (lines 441-470)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 7 | `static void cleanUp(Path root)` |  |

