# JavaScan.java - 666 lines · 39 methods · 2 constants · tools

`ham/citybuildersim/tools/JavaScan.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

> A structural read of one Java source file, without a compiler.
> 
> WHY THIS EXISTS. The source tree is 120,000 lines and the biggest file in
> it was 25,000 of them until the interface was split (2026-09-18; it is
> 8,000 now); no assistant working on this game can hold that in view, and
> the alternative - grepping blind, then reading a thousand lines to find the
> two that matter - is where most of a session's budget goes.
> The scanners in this package pre-digest the tree into short indexes
> (docs/map, docs/dials.md, docs/month-order.md, docs/harnesses.md) that a
> reader can open first and then jump straight to the right line.
> 
> WHY NOT javac's Tree API. It is in the JDK, but it lives in jdk.compiler,
> which the packaged runtime in Build EXE.bat does not carry; and it drops
> comments, which are exactly the part of this codebase worth indexing - the
> banners and the WHY paragraphs. This is a tokenizer plus brace counting.
> It handles strings, text blocks, char literals and comments correctly, and
> it knows just enough grammar to tell a method from a field from an inner
> type. It does not need to understand expressions, because it never looks
> inside a body except to find where it ends.
> 
> WHAT IT PRODUCES. For a file: the class header comment, every banner
> comment (the "/* ===== TITLE" convention this codebase uses, at any depth),
> every member of every type declared in the file with its start and end
> line, kind, signature and the first sentence of its own comment, the set of
> identifiers it mentions, and every string literal with its line (for the
> harness map). Everything is line-numbered so the reader can go straight
> to the source.

**Used by (7):** [CodeMap](CodeMap.md), [Dials](Dials.md), [HarnessMap](HarnessMap.md), [MonthOrder](MonthOrder.md), [SourceTree](SourceTree.md), [Stale](Stale.md), [Where](Where.md)

## Sections

| line | section |
|---:|---|
| 46 | · THE TOKEN STREAM |
| 138 | · WHAT A FILE IS MADE OF |
| 243 | · COMMENTS: BANNERS AND PROSE |
| 320 | · THE PARSE: TYPES, MEMBERS, BODIES |
| 619 | · QUESTIONS THE GENERATORS ASK |

## Enum constants

| line | constant | says |
|---:|---|---|
| 50 | `JavaScan.T.IDENT` |  |
| 50 | `JavaScan.T.NUMBER` |  |
| 50 | `JavaScan.T.STRING` |  |
| 50 | `JavaScan.T.CHAR` |  |
| 50 | `JavaScan.T.COMMENT` |  |
| 50 | `JavaScan.T.PUNCT` |  |
| 142 | `JavaScan.Kind.TYPE` |  |
| 142 | `JavaScan.Kind.METHOD` |  |
| 142 | `JavaScan.Kind.CONSTRUCTOR` |  |
| 142 | `JavaScan.Kind.FIELD` |  |
| 142 | `JavaScan.Kind.CONSTANT` |  |
| 142 | `JavaScan.Kind.ENUM_CONSTANT` |  |
| 142 | `JavaScan.Kind.INITIALIZER` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 324 | `JavaScan.NOT_A_METHOD_NAME` | `Set.of("if", "for", "while", "switch", "catch", "synchronized", "return", "ne...` |  |
| 327 | `JavaScan.MODIFIERS` | `Set.of("public", "private", "protected", "static", "final", "abstract", "sync...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 53 | `final T kind` |  |
| 53 | `final String text` |  |
| 53 | `final int line` |  |
| 53 | `final int endLine` |  |
| 145 | `public final Kind kind` |  |
| 146 | `public final String owner` | the type it belongs to ("Game", "Game.BuildQuote") |
| 147 | `public final String name` | the type it belongs to ("Game", "Game.BuildQuote") |
| 148 | `public final String signature` | modifiers, type, name and parameters, compacted |
| 149 | `public final String value` | modifiers, type, name and parameters, compacted |
| 150 | `public final String doc` | a constant's initializer text, else "" |
| 151 | `public final String trailing` | first sentence of the comment above it, else "" |
| 152 | `public final int line, endLine` | a // comment on the same line as its end, else "" |
| 153 | `public final boolean isStatic` |  |
| 165 | `public final int line` |  |
| 165 | `public final int level` |  |
| 165 | `public final String title` |  |
| 165 | `public final int depth` |  |
| 172 | `public final int line` |  |
| 172 | `public final String text` |  |
| 172 | `public final int depth` |  |
| 172 | `public final String before` |  |
| 178 | `public final Path path` |  |
| 179 | `public final String fileName` | "Game.java" |
| 180 | `public final String typeName` | "Game.java" |
| 181 | `public final String packageName` | "Game" |
| 182 | `public final int lineCount` | "ham.citybuildersim" |
| 183 | `public final String header` | the class comment, trimmed of its scaffolding |
| 184 | `public final List<Section> sections` | the class comment, trimmed of its scaffolding |
| 185 | `public final List<Member> members` |  |
| 186 | `public final List<Literal> literals` |  |
| 187 | `public final Set<String> identifiers` |  |
| 188 | `public final Map<String, Integer> identifierCounts` |  |
| 189 | `final List<Tok> toks` |  |
| 190 | `final String source` |  |
| 332 | `final String kind` | "class", "interface", "enum", "record", "@interface" or "block" |
| 333 | `final String name` | "class", "interface", "enum", "record", "@interface" or "block" |
| 334 | `boolean enumConstantsDone` | qualified within the file |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 44 | 623 | **type** `public final class JavaScan` | A structural read of one Java source file, without a compiler. |

### THE TOKEN STREAM (lines 46-137)

| line | len | member | says |
|---:|---:|---|---|
| 50 | 1 | **type** `enum T` |  |
| 52 | 9 | **type** `static final class Tok` |  |
| 54 | 3 | `Tok(T kind, String text, int line, int endLine)` _(in JavaScan.Tok)_ |  |
| 57 | 1 | `boolean is(String s)` _(in JavaScan.Tok)_ |  |
| 58 | 1 | `boolean ident(String s)` _(in JavaScan.Tok)_ |  |
| 59 | 1 | `public String toString()` _(in JavaScan.Tok)_ |  |
| 62 | 75 | `static List<Tok> lex(String src)` |  |

### WHAT A FILE IS MADE OF (lines 138-242)

| line | len | member | says |
|---:|---:|---|---|
| 142 | 1 | **type** `public enum Kind` |  |
| 144 | 19 | **type** `public static final class Member` |  |
| 154 | 6 | `Member(Kind kind, String owner, String name, String signature, String value, String doc, String trailing, int line, int endLine...` _(in JavaScan.Member)_ |  |
| 160 | 1 | `public int length()` _(in JavaScan.Member)_ |  |
| 161 | 1 | `public String qualifiedName()` _(in JavaScan.Member)_ |  |
| 164 | 6 | **type** `public static final class Section` |  |
| 166 | 3 | `Section(int line, int level, String title, int depth)` _(in JavaScan.Section)_ |  |
| 171 | 6 | **type** `public static final class Literal` |  |
| 173 | 3 | `Literal(int line, String text, int depth, String before)` _(in JavaScan.Literal)_ |  |
| 192 | 3 | `public static JavaScan read(Path path) throws IOException` |  |
| 196 | 15 | `public JavaScan(Path path, String source)` |  |
| 212 | 10 | `private String findPackage()` |  |
| 224 | 18 | `private String findHeader()` | The javadoc (or the plain block comment) that sits right above the top-level type. |

### COMMENTS: BANNERS AND PROSE (lines 243-319)

| line | len | member | says |
|---:|---:|---|---|
| 247 | 5 | `static boolean isBanner(Tok t)` |  |
| 253 | 5 | `static String firstLine(String comment)` |  |
| 260 | 12 | `static String bannerTitle(String comment)` | The banner's title: what is between the rules, or the first prose line under them. |
| 273 | 3 | `static int bannerLevel(String comment)` |  |
| 278 | 15 | `public static String commentBody(String comment)` | The comment with its slashes and stars stripped, paragraphs kept. |
| 295 | 24 | `public static String firstSentence(String comment)` | The first sentence of a comment, for a one-line index. |

### THE PARSE: TYPES, MEMBERS, BODIES (lines 320-618)

| line | len | member | says |
|---:|---:|---|---|
| 331 | 7 | **type** `private static final class Frame` |  |
| 335 | 1 | `Frame(String kind, String name)` _(in JavaScan.Frame)_ |  |
| 336 | 1 | `boolean isType()` _(in JavaScan.Frame)_ |  |
| 339 | 149 | `private void parse()` |  |
| 489 | 19 | `private int skipAnnotation(int at)` |  |
| 509 | 27 | `private int readEnumConstants(int at, Frame top, Tok lastComment)` |  |
| 538 | 9 | `private int matchingBrace(int open)` | From a '{' at index, the index of its matching '}'. |
| 549 | 3 | `private int skipBlock(int open, int depth)` | Skip a block, noting nothing; returns the index after its '}'. |
| 554 | 15 | `private int scanBodyAndSkip(int open, int depthAtOpen)` | Skip a body, but record the banners, literals and identifiers inside it; returns the index after '}'. |
| 570 | 4 | `private void noteIdentifier(String s)` |  |
| 575 | 11 | `private void noteLiteral(int at, int depth)` |  |
| 587 | 5 | `static String unquote(String s)` |  |
| 593 | 12 | `private String sourceBetween(Tok from, Tok to)` |  |
| 606 | 5 | `static String compact(List<Tok> decl, int upTo)` |  |
| 612 | 6 | `static String compactText(String s)` |  |

### QUESTIONS THE GENERATORS ASK (lines 619-666)

| line | len | member | says |
|---:|---:|---|---|
| 623 | 4 | `public Member method(String name)` |  |
| 628 | 5 | `public List<Member> methods()` |  |
| 634 | 5 | `public List<Member> constants()` |  |
| 641 | 12 | `public List<String> lines(int from, int to)` | Source lines [from, to], 1-based inclusive, without line endings. |
| 655 | 11 | `int[] bodyRange(Member m)` | The tokens of a method body, for the month-order listing: index of '{' and of its '}'. |

