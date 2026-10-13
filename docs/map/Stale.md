# Stale.java - 630 lines · 26 methods · 14 constants · tools

`ham/citybuildersim/tools/Stale.java` - generated 2026-10-13 by CodeMap; line numbers are as of that run.

> Finds the comments and documents that have stopped being true, mechanically.
> 
> WHY THIS EXISTS. The docs pass (docs/docs-pass.md) is a fresh agent reading
> the prose beside a change and asking of each sentence whether the code still
> makes it true. The two passes run so far found the same handful of shapes
> every time, and every one of them is a thing a scanner can see: a `see
> foo()` naming a method that no longer exists anywhere, a class header saying
> "the twelve banners" over a file with ten of them, a javadoc left with no
> member under it, a docs/map page older than the source it maps. This is the
> mechanical half of that pass, so the agent's attention goes to the half that
> needs judgement - whether a paragraph still explains the right thing.
> 
> FIRM AND SOFT. A finding in a firm category is a defect: the prose says
> something the tree contradicts, and somebody has to change one of the two.
> The firm categories are the exit code, so StaleCheck can assert them, and
> they are built to have no false positives on this tree - a count stated in a
> class header is compared against the banners that header names, not just
> against the file's total, because "the eleven banners from FINANCES to
> BORROW" is a claim about a range and not about the file. The soft categories
> are printed and counted separately because they can be right and still be
> reported: a member name in a comment may be a library call this tree never
> makes, and a checkout writes modification times it does not mean.
> 
> Run from the repository root, like the other tools; -Dsrc= and -Ddocs=
> move the tree and the documents, exactly as SourceTree takes them:
> 
>     java -cp target/classes ham.citybuildersim.tools.Stale
> 
> Exit status is the number of firm findings, 0 when clean.

**Uses:** [JavaScan](JavaScan.md) (40), [SourceTree](SourceTree.md) (13)

**Used by (1):** [StaleCheck](StaleCheck.md)

## Sections

| line | section |
|---:|---|
| 53 | · WHAT A FINDING IS |
| 89 | · THE REPORT |
| 133 | · WHAT THE TREE ACTUALLY CONTAINS |
| 185 | · 1. ORPHANED JAVADOC |
| 247 | · 2. MISSING FILE |
| 326 | · 3. STATED COUNT |
| 531 | · 4. UNRESOLVED MEMBER |
| 584 | · 5. MAP OLDER THAN SOURCE |
| 615 | · SMALL CHANGE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 59 | `Stale.Cat.ORPHANED_JAVADOC` |  |
| 61 | `Stale.Cat.MISSING_FILE` |  |
| 63 | `Stale.Cat.STATED_COUNT` |  |
| 65 | `Stale.Cat.UNRESOLVED_MEMBER` |  |
| 67 | `Stale.Cat.MAP_OLDER` |  |
| 69 | `Stale.Cat.FUZZY_COUNT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 251 | `Stale.JAVA_REF` | `Pattern.compile("\\b([A-Z][A-Za-z0-9]*)\\.java\\b")` |  |
| 259 | `Stale.DOC_REF` | `Pattern.compile("\\b(docs(?:/[A-Za-z0-9_-]+)*/[a-z0-9][a-z0-9-]*\\.md)\\b")` | A document is only reported when it is written as a path under docs/ with a lower-case name. |
| 265 | `Stale.SHAPE_NAMES` | `Set.of("Name", "NAME", "Something", "Foo", "Bar", "Baz", "ClassName", "Class"...` | Names that are written as a shape rather than as a file: "&lt;Name&gt;Screen.java", "*Check.java", "NAME.md". |
| 330 | `Stale.NUMBERS` | `numbers()` |  |
| 331 | `Stale.NUM` | `alternation()` |  |
| 334 | `Stale.SAID` | `"(?<![A-Za-z-])(" + NUM + ")"` | A number is only a number when a letter or a hyphen does not run into it: "fifty-six" is 56, never six. |
| 336 | `Stale.BANNERS` | `Pattern.compile("(?i)\\b(?:the\\s+)?" + SAID + "\\s+(?:top-level\\s+\|class-le...` |  |
| 337 | `Stale.SECTIONS` | `Pattern.compile("(?i)\\b(?:the\\s+)?" + SAID + "\\s+sections?\\b")` |  |
| 338 | `Stale.HARNESSES` | `Pattern.compile("(?i)\\b" + SAID + "\\s+harness(?:es)?\\b")` |  |
| 339 | `Stale.UI_FILES` | `Pattern.compile("(?i)\\b" + SAID + "\\s+files\\b")` |  |
| 340 | `Stale.SECTORS` | `Pattern.compile("(?i)\\b" + SAID + "\\s+(?:private\\s+)?sectors?(?:\\s+classe...` |  |
| 536 | `Stale.LIBRARY` | `Set.of("println", "runLater", "equals", "hashCode", "toString", "compareTo", ...` | Names that appear in prose here and belong to the JDK or JavaFX, not to the tree. |
| 544 | `Stale.PLACEHOLDERS` | `Set.of("foo", "bar", "baz", "doSomething")` | Names written as an example of a member rather than as one: "see foo()" is the shape, not a claim. |
| 552 | `Stale.CALL` | `Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\.([a-zA-Z][A-Za-z0-9_]*)\\(\\)\|\\b(...` | A member is named in prose as name() - the empty parentheses, with nothing between them and no space before them, are what makes it a member and not an English word with a parenthesis after it. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `public final boolean firm` |  |
| 73 | `public final String title` |  |
| 74 | `public final String explains` |  |
| 79 | `public final Cat cat` |  |
| 80 | `public final String path` |  |
| 81 | `public final int line` |  |
| 82 | `public final String what` |  |
| 143 | `final Set<String> javaFiles` | "Game.java", stubs included |
| 144 | `final Set<String> declared` | "Game.java", stubs included |
| 145 | `final Set<String> mentioned` | every member name in the tree |
| 146 | `final Map<String, Set<String>> byOwner` | every identifier the tree mentions |
| 147 | `int harnessFiles, sectorFiles, uiFiles` | type -> its own member names |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 51 | 580 | **type** `public final class Stale` | Finds the comments and documents that have stopped being true, mechanically. |

### WHAT A FINDING IS (lines 53-88)

| line | len | member | says |
|---:|---:|---|---|
| 58 | 19 | **type** `public enum Cat` | The kinds of staleness, firm first. |
| 75 | 1 | `Cat(boolean firm, String title, String explains)` _(in Stale.Cat)_ |  |
| 78 | 10 | **type** `public static final class Finding` |  |
| 83 | 3 | `Finding(Cat cat, String path, int line, String what)` _(in Stale.Finding)_ |  |
| 86 | 1 | `public String toString()` _(in Stale.Finding)_ |  |

### THE REPORT (lines 89-132)

| line | len | member | says |
|---:|---:|---|---|
| 93 | 19 | `public static void main(String[] args) throws IOException` |  |
| 114 | 6 | `public static List<Finding> of(List<Finding> all, Cat c)` | The findings of one category, in file and line order. |
| 122 | 10 | `public static List<Finding> scan(SourceTree tree) throws IOException` | Every finding, in no particular order; use of() to group them. |

### WHAT THE TREE ACTUALLY CONTAINS (lines 133-184)

| line | len | member | says |
|---:|---:|---|---|
| 142 | 35 | **type** `static final class Index` | The counts and name sets every category asks about: which files exist, which member names are declared anywhere, which identifiers the tree mentions at all, and the three numbers the two front doors state. |
| 149 | 27 | `Index(SourceTree tree) throws IOException` _(in Stale.Index)_ |  |
| 179 | 5 | `static List<JavaScan.Section> topBanners(JavaScan f)` | The file's top-level banners: the section convention this codebase uses, at member level. |

### 1. ORPHANED JAVADOC (lines 185-246)

| line | len | member | says |
|---:|---:|---|---|
| 189 | 40 | `static void orphanedJavadoc(SourceTree tree, List<Finding> out)` |  |
| 231 | 15 | `static int skipAnnotation(List<JavaScan.Tok> toks, int at)` | From an '@' at index, the index of the token after the annotation. |

### 2. MISSING FILE (lines 247-325)

| line | len | member | says |
|---:|---:|---|---|
| 268 | 12 | `static void missingFile(SourceTree tree, Index ix, List<Finding> out) throws IOException` |  |
| 282 | 20 | `static void refs(SourceTree tree, Index ix, String where, String text, int firstLine, List<Finding> out)` | Every .java and docs/ reference in one piece of text, checked against the tree. |
| 312 | 13 | `static List<Path> prose(SourceTree tree) throws IOException` | The prose documents: the hand-written pages directly under docs/ and the three front doors. |

### 3. STATED COUNT (lines 326-530)

| line | len | member | says |
|---:|---:|---|---|
| 342 | 96 | `static void statedCount(SourceTree tree, Index ix, List<Finding> out) throws IOException` |  |
| 448 | 16 | `static boolean agrees(int said, int banners, String sentence, JavaScan f)` | Whether a stated count is a claim the file bears out. |
| 471 | 5 | `static boolean quoted(String text, int at)` | Whether a position sits inside a quotation. |
| 478 | 13 | `static String sentence(String text, int at)` | The sentence around a position: a full stop followed by a space ends one, a dot inside a name does not. |
| 497 | 15 | `static Map<String, Integer> numbers()` | One to ninety-nine in words, and the tens on their own. |
| 513 | 7 | `static String alternation()` |  |
| 522 | 8 | `static int number(String said)` | The number a matched word or figure says, or -1 if it is neither. |

### 4. UNRESOLVED MEMBER (lines 531-583)

| line | len | member | says |
|---:|---:|---|---|
| 554 | 29 | `static void unresolvedMember(SourceTree tree, Index ix, List<Finding> out)` |  |

### 5. MAP OLDER THAN SOURCE (lines 584-614)

| line | len | member | says |
|---:|---:|---|---|
| 588 | 26 | `static void mapOlderThanSource(SourceTree tree, List<Finding> out) throws IOException` |  |

### SMALL CHANGE (lines 615-630)

| line | len | member | says |
|---:|---:|---|---|
| 619 | 1 | `static String path(JavaScan f)` |  |
| 621 | 1 | `static String rel(Path p)` |  |
| 623 | 1 | `static String flat(String s)` |  |
| 625 | 5 | `static int newlines(String s, int upTo)` |  |

