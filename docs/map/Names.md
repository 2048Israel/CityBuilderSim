# Names.java - 436 lines · 26 methods · 13 constants · tools

`ham/citybuildersim/tools/Names.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> Lists the names a branch's new prose uses that the tree does not declare.
> 
> WHY THIS EXISTS (RV1, 2026-10-13). ChatGPT and Gemini write handoffs and
> notes on chatgpt/ and gemini/ branches, and most of each review was the
> same mechanical question asked by hand, a call at a time: does every method
> and constant the text names exist, and on the class it says? This asks it
> of every line the branch adds against main - each line of Markdown, and the
> comments in Java - and lists only what does not resolve, so the reviewer's
> attention goes to whether the text is right rather than whether it is real.
> 
> WHAT A NAME IS: foo(), Class.foo() (with or without arguments), Class.CONSTANT,
> and a whole backticked identifier, dotted or not (`Expectations`,
> `DataSave.expectations`, `quoteRate(..., months)`). A bare name resolves when
> the tree declares or uses it anywhere; Class.member only when Class, or a
> supertype the tree declares, has it - so a member put on the wrong class is
> found. The declarations are Stale's Index over JavaScan, never a regex over
> source, which misses `final`, `double[]` and generic return types; a record's
> components and an enum's values() are added here, because JavaScan keeps
> them in the type's signature and not as members. The branch's own Java files
> are read as the branch has them, so a method the branch adds resolves.
> 
> NOT COUNTED: a name the text itself marks as new - written as "new `Name`",
> or given a value in backticks (`DRIFT_MONTHS = 6`) anywhere in the added
> lines - is a proposal, not a claim, and is listed apart; so is one written
> after "proposed" or "for example". A file name in backticks (`Game.java`) is
> Stale's to check, not this, and the generated documents (docs/map, dials.md,
> month-order.md, harnesses.md, manual.md) are not read: the tree wrote them.
> 
> It warns and does not gate: a passage about the past may name a method that
> existed then. ship.sh runs it on chatgpt/* and gemini/* branches. Run it
> from the repository root, after a build:
> 
>     java -cp target/classes ham.citybuildersim.tools.Names chatgpt/cd1-handoff
>     java -cp target/classes ham.citybuildersim.tools.Names 0a516a5 0a516a5^    (a commit, against its parent)
> 
> The diff is base...branch (from where the branch left the base; main by
> default). Exit status is the number of unresolved names, at most 250; 255
> when git could not be asked.

**Uses:** [JavaScan](JavaScan.md) (9), [Stale](Stale.md) (5), [SourceTree](SourceTree.md) (4)

**Used by (1):** [StaleCheck](StaleCheck.md)

## Sections

| line | section |
|---:|---|
| 65 | · WHAT IT REPORTS |
| 162 | · THE ADDED LINES |
| 213 | · THE NAMES IN A LINE |
| 272 | · WHAT THE TREE DECLARES |
| 413 | · GIT |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 166 | `Names.HUNK` | `Pattern.compile("^@@ -\\d+(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@")` |  |
| 204 | `Names.GENERATED` | `List.of("docs/map/", "docs/dials.md", "docs/month-order.md", "docs/harnesses....` | The documents Maps and ManualToMarkdown write: what they name is the tree's own, or the manual's. |
| 228 | `Names.TICKS` | `Pattern.compile("'([^'\\n]+)'")` |  |
| 230 | `Names.WHOLE` | `Pattern.compile("([A-Za-z_][A-Za-z0-9_]*(?:\\.[A-Za-z_][A-Za-z0-9_]*)*)(\\([^...` | A whole span that is one name: dotted identifiers, then perhaps one argument list. |
| 232 | `Names.GIVEN` | `Pattern.compile("([A-Za-z_][A-Za-z0-9_]*)\\s*=[^=].*")` | A span that gives a name a value is the text proposing it: `DRIFT_MONTHS = 6`. |
| 233 | `Names.FILE` | `Pattern.compile(".*\\.(java\|md\|json\|bin\|bat\|cmd\|sh\|txt\|html\|jar\|csv\|png\|xml\|p...` |  |
| 234 | `Names.QCALL` | `Pattern.compile("\\b([A-Z][A-Za-z0-9_]*(?:\\.[A-Z][A-Za-z0-9_]*)*)\\.([a-zA-Z...` |  |
| 235 | `Names.CONST` | `Pattern.compile("\\b([A-Z][A-Za-z0-9_]*(?:\\.[A-Z][A-Za-z0-9_]*)*)\\.([A-Z][A...` |  |
| 236 | `Names.CALL` | `Pattern.compile("\\b([a-z_][A-Za-z0-9_]*)\\(\\)")` |  |
| 238 | `Names.NEW_BEFORE` | `Pattern.compile("(?i)\\b(?:new\|proposed\|for example)\\s+['*_\"(]*$")` | "new `CurveCheck`", "for example `HistoricalEconomy`": the text says the name is one it proposes. |
| 277 | `Names.LANGUAGE` | `Set.of("abstract", "boolean", "break", "byte", "case", "catch", "char", "clas...` | Words a backtick holds that are Java's, the codebase's conventions' or the shell's, not the tree's. |
| 286 | `Names.OBJECT` | `Set.of("equals", "hashCode", "toString", "getClass", "clone")` | What every object has, and every enum. |
| 287 | `Names.ENUM` | `Set.of("values", "valueOf", "ordinal", "name", "compareTo")` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 71 | `public final String path` |  |
| 72 | `public final int line` |  |
| 73 | `public final String name` |  |
| 74 | `public final String why` |  |
| 75 | `public int more` |  |
| 84 | `public final List<Miss> unresolved` |  |
| 85 | `public final Set<String> proposed` |  |
| 86 | `public final Set<String> read` |  |
| 219 | `final String path` |  |
| 219 | `final int line` |  |
| 219 | `final List<String> parts` |  |
| 219 | `final boolean call` |  |
| 219 | `final String shown` |  |
| 294 | `final Stale.Index ix` |  |
| 295 | `final Set<String> types` | simple names, nested ones too |
| 296 | `final Map<String, Set<String>> supers` | simple names, nested ones too |
| 297 | `final Map<String, Set<String>> implicit` | simple name -> extends and implements |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 374 | **type** `public final class Names` | Lists the names a branch's new prose uses that the tree does not declare. |

### WHAT IT REPORTS (lines 65-161)

| line | len | member | says |
|---:|---:|---|---|
| 70 | 11 | **type** `public static final class Miss` | One unresolved name, where it is first written and how many more lines write it. |
| 76 | 1 | `Miss(String path, int line, String name, String why)` _(in Names.Miss)_ |  |
| 77 | 3 | `public String toString()` _(in Names.Miss)_ |  |
| 83 | 5 | **type** `public static final class Result` | What one diff came to: the unresolved names, the proposals set apart, and every name read. |
| 89 | 19 | `public static void main(String[] args) throws IOException` |  |
| 110 | 6 | `public static Result ofBranch(SourceTree tree, String branch, String base) throws IOException` | What base...branch adds, read from git: the diff, and each changed Java file as the branch has it. |
| 124 | 37 | `public static Result scan(SourceTree tree, String diff, Function<String, String> newSource)` | The names the added lines of a diff use and the tree does not declare. |

### THE ADDED LINES (lines 162-212)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 19 | `static Map<String, TreeMap<Integer, String>> added(String diff)` | The lines a unified diff adds, by file, numbered as the new file has them. |
| 190 | 12 | `static Map<Integer, String> comments(String source, Set<Integer> lines)` | Of a Java file's added lines, the part of each that is comment: code names nothing to a reader. |
| 206 | 4 | `static boolean generated(String path)` |  |
| 211 | 1 | `static String strip(String s)` |  |

### THE NAMES IN A LINE (lines 213-271)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 9 | **type** `static final class Ref` | One name as a line writes it: its dotted parts, and whether it was written as a call. |
| 220 | 5 | `Ref(String path, int line, String dotted, boolean call)` _(in Names.Ref)_ |  |
| 225 | 1 | `String last()` _(in Names.Ref)_ |  |
| 241 | 26 | `static void refs(String path, int line, String text, List<Ref> out, Set<String> proposed)` | The names one line writes, in the order: whole backticked spans, Class.member, Class.CONSTANT, foo(). |
| 268 | 1 | `static void blank(StringBuilder sb, int from, int to)` |  |
| 270 | 1 | `static String lastPart(String dotted)` |  |

### WHAT THE TREE DECLARES (lines 272-412)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 90 | **type** `static final class Decls` | Stale's Index, and what it leaves in a type's signature: each type's supertypes, a record's components, an enum's implicit members. |
| 299 | 6 | `Decls(SourceTree tree)` _(in Names.Decls)_ | simple name -> components, values()... |
| 306 | 1 | `void add(JavaScan f)` _(in Names.Decls)_ |  |
| 308 | 26 | `private void types(JavaScan f)` _(in Names.Decls)_ |  |
| 335 | 1 | `boolean isType(String name)` _(in Names.Decls)_ |  |
| 338 | 14 | `boolean has(String type, String member)` _(in Names.Decls)_ | Whether a tree type, or any supertype of it, has the member; a supertype outside the tree has whatever the tree uses. |
| 353 | 5 | `boolean anywhere(String name)` _(in Names.Decls)_ |  |
| 360 | 22 | `String resolve(Ref r)` _(in Names.Decls)_ | Null when the name resolves; otherwise why not, in a few words. |
| 384 | 8 | `static int closing(String s, int open)` |  |
| 394 | 12 | `static List<String> topLevel(String s, char sep)` | Split on the separator where no <> or () is open. |
| 407 | 5 | `static String withoutGenerics(String s)` |  |

### GIT (lines 413-436)

| line | len | member | says |
|---:|---:|---|---|
| 417 | 13 | `static String git(String...args) throws IOException` |  |
| 431 | 5 | `static String read(InputStream in) throws IOException` |  |

