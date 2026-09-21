# CodeMap.java - 177 lines · 3 methods · 0 constants · tools

`ham/citybuildersim/tools/CodeMap.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> The code map: docs/map/README.md, one row per file, and docs/map/NAME.md
> for every file, listing its banner sections and every member with the
> line it starts on.
> 
> WHY. UserInterface.java is 25,000 lines and Game.java is 8,000. An
> assistant that has to change the bank screen cannot read either; what it
> can do is open docs/map/UserInterface.md, find "THE BANK" at line 7120
> with its forty methods listed under it, and read exactly those. The map is
> the table of contents the files never had, kept outside them so the files
> do not have to carry it.
> 
> Run from the repository root: java -cp target/classes ham.citybuildersim.tools.CodeMap
> or let Maps run all four generators at once. Regenerate after every batch;
> the map is only as current as its last run, and the date at the top says
> when that was.

**Uses:** [SourceTree](SourceTree.md) (26), [JavaScan](JavaScan.md) (24)

**Used by (1):** [Maps](Maps.md)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 153 | **type** `public final class CodeMap` | The code map: docs/map/README.md, one row per file, and docs/map/NAME.md for every file, listing its banner sections and every member with the line it starts on. |
| 27 | 4 | `public static void main(String[] args) throws IOException` |  |
| 32 | 41 | `static void run(SourceTree tree) throws IOException` |  |
| 74 | 103 | `static String one(SourceTree tree, JavaScan f)` |  |

