# SourceTree.java - 154 lines · 13 methods · 1 constants · tools

`ham/citybuildersim/tools/SourceTree.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> The whole source tree, scanned once, and the little that every generator
> shares: where the sources are, where the documents go, what area a file
> belongs to, and how to write a document without leaving a half-written one
> behind.
> 
> Run any of the tools from the repository root. They default to
> src/main/java for the sources and docs/ for the output, and both can be
> overridden with -Dsrc=... and -Ddocs=... so the cloud loop, which keeps
> the tree somewhere else, can point them at its own copy.

**Uses:** [JavaScan](JavaScan.md) (17)

**Used by (8):** [CodeMap](CodeMap.md), [Dials](Dials.md), [HarnessMap](HarnessMap.md), [Maps](Maps.md), [MonthOrder](MonthOrder.md), [Stale](Stale.md), [StaleCheck](StaleCheck.md), [Where](Where.md)

## Sections

| line | section |
|---:|---|
| 121 | · MARKDOWN SMALL CHANGE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 84 | `SourceTree.AREAS` | `{ "model", "sectors", "interface", "harnesses", "tools" }` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `public final Path root` |  |
| 30 | `public final Path docs` |  |
| 31 | `public final List<JavaScan> files` |  |
| 32 | `public final Map<String, JavaScan> byType` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 27 | 128 | **type** `public final class SourceTree` | The whole source tree, scanned once, and the little that every generator shares: where the sources are, where the documents go, what area a file belongs to, and how to write a document without leaving a half-written o... |
| 34 | 16 | `public SourceTree(Path root, Path docs) throws IOException` |  |
| 51 | 9 | `public static SourceTree open() throws IOException` |  |
| 62 | 3 | `public String relative(JavaScan f)` | The file's path relative to the source root, with forward slashes. |
| 73 | 10 | `public String area(JavaScan f)` | Which shelf a file sits on. |
| 87 | 6 | `public Map<String, List<JavaScan>> byArea()` | Files grouped by area, in the order AREAS lists them. |
| 95 | 5 | `public List<JavaScan> usedBy(JavaScan f)` | Which files mention this type by name (other than itself). |
| 102 | 8 | `public List<Map.Entry<String, Integer>> uses(JavaScan f)` | The types in the tree this file mentions, most mentioned first. |
| 112 | 8 | `public void write(String relativePath, String content) throws IOException` | Write a document, LF line endings, replacing whatever was there. |

### MARKDOWN SMALL CHANGE (lines 121-154)

| line | len | member | says |
|---:|---:|---|---|
| 125 | 3 | `public static String cell(String s)` |  |
| 129 | 4 | `public static String code(String s)` |  |
| 134 | 5 | `public static String clip(String s, int max)` |  |
| 140 | 3 | `public static String stamp()` |  |
| 145 | 9 | `public static String headerProse(JavaScan f)` | The class header with the javadoc tags dropped, or a stand-in. |

