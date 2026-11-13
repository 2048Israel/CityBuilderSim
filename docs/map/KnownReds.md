# KnownReds.java - 129 lines · 8 methods · 3 constants · model

`ham/citybuildersim/KnownReds.java` - generated 2026-11-13 by CodeMap; line numbers are as of that run.

> The common red-line judge for the suite, batch checks and publisher.
> WHY (GR2): temp gate scripts held approvals the builder could never read.
> This class also runs in Java source-file mode before a checkout is built.

**Used by (2):** [AllChecks](AllChecks.md), [KnownRedsCheck](KnownRedsCheck.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 20 | `KnownReds.PARTS` | `Path.of("docs/notes/known-reds.txt")` |  |
| 21 | `KnownReds.HEADER` | `Pattern.compile("^([A-Za-z0-9_]+) +FAILED - exit(-?[0-9]+).*")` |  |
| 22 | `KnownReds.TOTAL` | `Pattern.compile("^([0-9]+) harness(?:es)? run in.*,([0-9]+) failed$")` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 19 | 111 | **type** `public class KnownReds` | The common red-line judge for the suite, batch checks and publisher. |
| 24 | 8 | `static Set<String> read(Path file) throws IOException` |  |
| 33 | 1 | `private static String harness(String line)` |  |
| 34 | 1 | `private static boolean count(String line)` |  |
| 37 | 8 | `static boolean matches(String line, String entry)` | Exact gate lines and five-column, harness-scoped parts share this representation. |
| 47 | 16 | `static List<String> fresh(List<String> reds, Set<String> entries)` | Counts follow a known assertion only when every assertion of that harness is known. |
| 65 | 21 | `static List<String> reds(List<String> log)` | Reads the public -f log, including logs made before line tags existed. |
| 88 | 35 | `static int judge(Path suite, Set<String> entries, Path out, boolean rejudge) throws Exception` | Returns batch-check's status and writes the evidence that ship.sh retains. |
| 124 | 5 | `public static void main(String[] args) throws Exception` |  |

