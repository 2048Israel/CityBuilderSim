# HarnessMap.java - 133 lines · 3 methods · 2 constants · tools

`ham/citybuildersim/tools/HarnessMap.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> What every harness asserts, in its own words: docs/harnesses.md.
> 
> WHY. Fifty-seven harnesses, 28,000 lines, and the question that matters
> before any change is "what already checks this?" - because README rule 4
> says a check that fails is the finding, and you cannot honour that rule
> for a check you did not know existed. Each harness labels its assertions
> in prose ("the catalogue has both kitchens", "...and the same fact the
> other way up") and groups them under printed section headings; this
> lifts those labels out, so the whole suite reads as a table of contents
> of what the model promises. It also says which harnesses mention which
> model class, and which harness files AllChecks does not run.
> 
> The labels are found by the helpers a harness declares - any
> "static void NAME(String label, ...)" in the file - plus the usual names.
> A label built from an expression ("t.getName() + ...") is not a literal
> and is not listed; the count under each harness is of labelled calls.

**Uses:** [JavaScan](JavaScan.md) (11), [SourceTree](SourceTree.md) (9)

**Used by (1):** [Maps](Maps.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 33 | `HarnessMap.HELPERS` | `Set.of("assertTrue", "check", "close", "report", "assertEquals", "same", "wit...` |  |
| 35 | `HarnessMap.SECTION` | `Pattern.compile("^\\s*(?:\\\\n)?\\s*(?:---\|===)+\\s*(.*?)\\s*(?:---\|===)+\\s*...` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 31 | 103 | **type** `public final class HarnessMap` | What every harness asserts, in its own words: docs/harnesses.md. |
| 37 | 3 | `public static void main(String[] args) throws IOException` |  |
| 41 | 42 | `static void run(SourceTree tree) throws IOException` |  |
| 85 | 48 | `static int one(SourceTree tree, JavaScan h, StringBuilder sb, boolean registered)` | One harness's sections and labels; returns the number of labels. |

