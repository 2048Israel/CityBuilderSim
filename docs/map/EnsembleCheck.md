# EnsembleCheck.java - 509 lines · 24 methods · 10 constants · harnesses

`ham/citybuildersim/EnsembleCheck.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The inflation ensemble is in the tree and still runs: its driver compiles
> against this tree, its two halves name the same policies, and the stored
> baseline reads.
> 
> WHY (EN1, 2026-10-20). The ensemble - sixteen seeds under five monetary
> policies - lived in a workbench zip, compiled at run time against a snapshot
> of target/classes. Its driver calls LongPlaytest's code, so a refactor of the
> model could break it and nobody would know until the next batch whose trace
> moved came to run it (the ensemble runs only then, since EN1), on the day the
> answer was wanted. It now lives in tools/ensemble, outside src/ (it is never
> part of the build), and this harness is the only thing in the suite that
> touches it.
> 
> WHAT THIS HAS TO PROVE, and none of it is about what the model does - the
> ensemble itself is the check of that, and it takes about eighty minutes of
> machine time:
> 
>   1. THE RUNNER IS IN THE TREE. The shell script, the scorer, the driver and
>      the readme are there and not empty.
> 
>   2. THE DRIVER COMPILES AGAINST THIS TREE. It is compiled here with the
>      JDK's own compiler against the classpath this harness runs on, which is
>      what the ensemble does with its snapshot. The guard is proved on
>      fixtures: a source that calls a method no class has fails the same way
>      and says which, and a source that calls a real class compiles. On a JRE
>      there is no compiler and this is one SKIP line, not a failure.
> 
>   3. THE DRIVER AND THE SCORER NAME THE SAME POLICIES. The constants of the
>      driver's Policy enum and the strings of the scorer's POLICIES list, each
>      read from the source text, are the same set and not empty. A policy in
>      one and not the other is five runs the scorer never counts, or a score
>      for runs that were never made.
> 
>   4. THE BASELINE IS THERE AND READS. docs/ensemble holds one page per
>      version; the newest by number is not newer than the game; and it carries
>      the line "criteria passed: N/M" and a summary.json block that parses to
>      the seeds, the take-offs and, for every policy of (3), the median final
>      population and the two counts the next run is compared with. The reader
>      is proved on canned pages: one missing a policy, one with no block, one
>      with no verdict line and a few more each fail, and a sound one passes -
>      a reader that rejects everything would pass the rest of this section.
> 
> Run from the repository root; -Dtools=<path to tools> and -Ddocs=<path to
> docs> say where else to look.
> 
>     java -cp target/classes;gson.jar ham.citybuildersim.EnsembleCheck

**Uses:** [GameVersion](GameVersion.md) (2), [TempDirs](TempDirs.md) (1)

## Sections

| line | section |
|---:|---|
| 119 | 1. the runner is in the tree |
| 131 | 2. the driver compiles against this tree |
| 211 | 3. the same policies |
| 304 | 4. the baseline reads |
| 480 | · THE FIXTURES, WHICH CAUSE THE CONDITION RATHER THAN WAITING FOR IT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 213 | `EnsembleCheck.POLICY_ENUM` | `Pattern.compile("\\benum\\s+Policy\\s*\\{([^}]*)\\}")` |  |
| 214 | `EnsembleCheck.POLICIES_LIST` | `Pattern.compile("(?m)^POLICIES\\s*=\\s*\\[([^\\]]*)\\]")` |  |
| 216 | `EnsembleCheck.QUOTED` | `Pattern.compile("\"([^\"]*)\"\|'([^']*)'")` | A name in double or single quotes, as either half lists its policies. |
| 306 | `EnsembleCheck.BASELINE_NAME` | `Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.md")` |  |
| 308 | `EnsembleCheck.CRITERIA` | `Pattern.compile("(?m)^criteria passed:\\s*(\\d+)\\s*/\\s*(\\d+)\\b")` | A stored score's verdict line: "criteria passed: N / M". |
| 309 | `EnsembleCheck.JSON_BLOCK` | `Pattern.compile("(?s)'''json\\s*\\R(.*?)\\R\\s*'''")` |  |
| 396 | `EnsembleCheck.VERDICT` | `"criteria passed: 5/6(vacuous, not counted: 0); missing runs: none; short run...` | A canned page's verdict line, for the reader's fixtures. |
| 398 | `EnsembleCheck.JSON` | `""" { "seeds": 4, "policies": { "ALPHA": {"final_pop_median": 1000.5, "runs_w...` | ...and its JSON block, sound as it stands; a fixture edits it into a page the reader should refuse. |
| 485 | `EnsembleCheck.BROKEN` | `""" package ham.citybuildersim; final class EnsembleCheckBroken { static void...` | Calls a method that no class has: the change the guard exists for, made by hand. |
| 494 | `EnsembleCheck.SOUND` | `""" package ham.citybuildersim; final class EnsembleCheckSound { static Class...` | Calls a class that is there. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 435 | **type** `public class EnsembleCheck` | The inflation ensemble is in the tree and still runs: its driver compiles against this tree, its two halves name the same policies, and the stored baseline reads. |
| 79 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 85 | 4 | `static void assertTrue(String label, boolean ok, String whyNot)` | An assertion that has a reason to print when it fails. |
| 91 | 3 | `static Path toolsDir()` | The folder the runner lives in. |
| 96 | 3 | `static Path baselineDir()` | The folder the stored baselines live in. |
| 100 | 18 | `public static void main(String[] args) throws Exception` |  |

### 1. the runner is in the tree (lines 119-130)

| line | len | member | says |
|---:|---:|---|---|
| 121 | 9 | `static void runnerIsThere() throws IOException` |  |

### 2. the driver compiles against this tree (lines 131-210)

| line | len | member | says |
|---:|---:|---|---|
| 133 | 41 | `static void driverCompiles() throws IOException` |  |
| 180 | 23 | `static List<String> compileProblems(JavaCompiler compiler, List<Path> sources, Path outDir) throws IOException` | Compile these sources into outDir against the classpath this JVM runs on, and say what went wrong: one line per error, file:line and the message, in English whatever the machine's own language is. |
| 205 | 5 | `static String firstOf(List<String> lines, int n)` | The first few of a list, one to a line, for a failing assertion to print. |

### 3. the same policies (lines 211-303)

| line | len | member | says |
|---:|---:|---|---|
| 219 | 14 | `static List<String> driverPolicies(String source)` | The constants of the driver's Policy enum, in the order written; empty when the source has none. |
| 235 | 9 | `static List<String> scorerPolicies(String source)` | The strings of the scorer's POLICIES list, in the order written; empty when it has none. |
| 246 | 17 | `static String policyProblem(String driverSource, String scorerSource)` | Null when the two sources name the same policies, else why not. |
| 264 | 7 | `static String read(Path p)` |  |
| 273 | 30 | `static List<String> samePolicies()` | Section 3; returns the driver's policies, which section 4 holds the baseline to. |

### 4. the baseline reads (lines 304-479)

| line | len | member | says |
|---:|---:|---|---|
| 312 | 5 | `static int[] versionOf(String s)` | The three numbers of x.y.z, or null when it is not one. |
| 319 | 5 | `static int compareVersions(String a, String b)` | Negative when a is the older version, by the numbers and not by the letters ("0.8.9" is older than "0.8.11"). |
| 326 | 10 | `static String newestBaseline(List<String> names)` | The file name of the newest baseline among these names, or null when none is named x.y.z.md. |
| 337 | 3 | `private static boolean isNumber(JsonElement e)` |  |
| 341 | 5 | `private static boolean isInteger(JsonElement e)` |  |
| 354 | 33 | `static String baselineProblem(String text, List<String> policies)` | Null when this baseline page is sound, else the reason it is not: the verdict line, then the first json block, which has to parse to the seeds (an integer of 1 or more), the take-offs (an integer) and, for every one o... |
| 389 | 5 | `static String canned(String verdictLine, String json)` | A baseline page with these two parts of it: the verdict line and the json. |
| 413 | 7 | `static String withJson(String from, String to)` | A canned page with one change made to its json. |
| 421 | 58 | `static void baselineReads(List<String> policies) throws IOException` |  |

### THE FIXTURES, WHICH CAUSE THE CONDITION RATHER THAN WAITING FOR IT (lines 480-509)

| line | len | member | says |
|---:|---:|---|---|
| 502 | 7 | `static void cleanUp(Path root)` |  |

