# ManualToMarkdown.java - 1,522 lines · 82 methods · 14 constants · tools

`ham/citybuildersim/tools/ManualToMarkdown.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> The published manual as two files the repository keeps: docs/manual.md,
> which GitHub renders when it is clicked, and docs/manual.html, the page
> itself as a standalone file that opens in a browser from a clone.
> 
>     java -cp target/classes ham.citybuildersim.tools.ManualToMarkdown --wrap page.html docs/manual.html
>     java -cp target/classes ham.citybuildersim.tools.ManualToMarkdown docs/manual.html docs/manual.md
> 
> WHY. The manual is the model written out, and it lived only as a claude.ai
> artifact ("CityBuilderSim"), so reading it from the repository meant
> following a link out of it. Both files are derived from the page at every
> publish, which makes the derivation a tool in the tree rather than a script
> somebody keeps. docs/manual.html and docs/manual.md are GENERATED: never
> edit them. Change the page, publish it, pull it, and run the two lines above
> in that order - the second reads what the first wrote.
> 
> WHAT IT ACCEPTS. The page as published - a fragment that opens with its
> <title>, <link>s and <style> and then <div class="shell"> - or the same page
> wrapped in <!doctype html><html><head>...</head><body>...</body></html>, as
> the artifact service serves it or as --wrap writes it. A text that begins
> <!doctype is the wrapped form: --wrap unwraps it first, and the Markdown
> walk reads it as it stands (html and body opened up, the head furniture), so
> a warning's line is the file's own. Either form gives the same bytes out of
> either mode. Line endings are read as LF, so a CRLF checkout changes nothing.
> 
> WHAT IT EMITS. With --wrap, the standalone page: the doctype, <html
> lang="en">, a head holding the charset, the viewport and the page's own
> title, links and style, then a body holding everything from <div
> class="shell"> on. The published bytes are carried verbatim, and --wrap of
> its own output is its own output. Without --wrap, GitHub-flavoured Markdown:
> a first line saying the file is generated, the title, the build line, the
> vitals as a table, the contents as links to GitHub's own heading anchors,
> then each section as "## N. Title" with its tables, formulas as code
> blocks, notes as block quotes and the month as a numbered list, and the
> footer under a rule.
> 
> THE WALK. No HTML library: a tokenizer, a tree, and a renderer for the tag
> and class vocabulary the page actually uses (KNOWN_TAGS, KNOWN_CLASSES). A
> tag or class outside it is passed through as its text and named once on
> stderr with its line - the next version of the page may add one, and the
> tool must degrade, not fail. A clean run prints no warnings. The Markdown
> is written to render on github.com, which is stricter than it looks: an
> emphasis GitHub would not read as one is spelled <em> or <strong>, and in a
> paragraph or cell where two dollar signs could pair every one is escaped,
> because GitHub reads $...$ as mathematics.

## Sections

| line | section |
|---:|---|
| 68 | · THE VOCABULARY |
| 118 | · RUNNING IT |
| 165 | · THE PAGE AS PUBLISHED, AND AS A STANDALONE FILE |
| 249 | · THE WALK: TOKENS, THEN A TREE |
| 467 | · THE MARKDOWN: BLOCKS |
| 847 | · THE MARKDOWN: INLINE TEXT |
| 987 | · · escapes: a plain character Markdown would read as markup gets a backslash |
| 1022 | · · emphasis: pair the markers, then * and ** where GitHub reads them so, tags where it would not |
| 1134 | · CommonMark's flanking rules, read strictly: a symbol counts as punctuation when that makes a rule fail |
| 1203 | · THE NAMED CHARACTER REFERENCES |

## Enum constants

| line | constant | says |
|---:|---|---|
| 253 | `ManualToMarkdown.Kind.TEXT` |  |
| 253 | `ManualToMarkdown.Kind.START` |  |
| 253 | `ManualToMarkdown.Kind.END` |  |
| 253 | `ManualToMarkdown.Kind.COMMENT` |  |
| 253 | `ManualToMarkdown.Kind.OTHER` |  |
| 471 | `ManualToMarkdown.Mode.BLOCK` |  |
| 471 | `ManualToMarkdown.Mode.CELL` |  |
| 471 | `ManualToMarkdown.Mode.HEADING` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `ManualToMarkdown.GENERATED` | `"<!-- Derived from the published manual by ham.citybuildersim.tools.ManualToM...` | The Markdown's first line: says the file is generated, and renders as nothing on GitHub. |
| 77 | `ManualToMarkdown.STANDALONE_HEAD` | `"<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n" + "...` | What --wrap writes in front of the page's own title, links and style. |
| 81 | `ManualToMarkdown.KNOWN_TAGS` | `Set.of("html", "head", "body", "meta", "title", "link", "style", "div", "nav"...` | Every tag the page and its wrappers use; any other is passed through as its text, with a warning. |
| 87 | `ManualToMarkdown.KNOWN_CLASSES` | `Set.of("shell", "navtitle", "navnum", "eyebrow", "standfirst", "vitals", "vit...` | Every class the page uses; an element with another is rendered as its bare tag, with a warning. |
| 92 | `ManualToMarkdown.BLOCKS` | `Set.of("head", "title", "link", "meta", "style", "div", "nav", "main", "heade...` | The elements that are blocks in Markdown; everything else is inline text inside one. |
| 97 | `ManualToMarkdown.VOID` | `Set.of("br", "link", "meta", "hr", "img", "input", "wbr", "base", "col", "are...` | Elements with no end tag. |
| 101 | `ManualToMarkdown.RAW` | `Set.of("style", "script", "title", "textarea")` | Elements whose content is text, never tags. |
| 104 | `ManualToMarkdown.HEAD_TAGS` | `Set.of("title", "link", "meta", "style", "base", "script")` | The elements that belong in a head: what --wrap moves there, and how the service's wrapper is recognised. |
| 111 | `ManualToMarkdown.EM_OPEN` | `'\uE001', EM_CLOSE = '\uE002', STRONG_OPEN = '\uE003', STRONG_CLOSE = '\uE004...` | Inline Markdown is built as text in which these private-use characters stand for the constructs whose spelling depends on what ends up beside them, and it is resolved once a whole paragraph or cell is known. |
| 116 | `ManualToMarkdown.ENTITY_LIKE` | `Pattern.compile("&(#[0-9]+\|#[xX][0-9a-fA-F]+\|[A-Za-z][A-Za-z0-9]*);")` | Text that Markdown would read as a character reference and decode a second time. |
| 458 | `ManualToMarkdown.CP1252` | `"\u20AC\u0081\u201A\u0192\u201E\u2026\u2020\u2021\u02C6\u2030\u0160\u2039\u01...` | What HTML reads &#128; to &#159; as: the Windows-1252 characters, not the C1 controls. |
| 853 | `ManualToMarkdown.Ctx.PLAIN` | `new Ctx(false, false, false, false)` |  |
| 1207 | `ManualToMarkdown.ENTITIES` | `entities()` |  |
| 1224 | `ManualToMarkdown.ENTITY_TABLE` | `""" AElig C6 AMP 26 Aacute C1 Abreve 102 Acirc C2 Acy 410 Afr 1D504 Agrave C0...` | Every named character reference HTML defines (the WHATWG list, html.spec.whatwg.org/entities.json), the forms that end in a semicolon: the name, then its code point in hex, or two joined by a plus. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 142 | `private final Set<String> warned` |  |
| 143 | `private int warnings, sections, tables, formulas, notes, steps` |  |
| 365 | `final String tag, text` |  |
| 366 | `final Map<String, String> attrs` |  |
| 367 | `final int line` |  |
| 368 | `final Node parent` |  |
| 369 | `final List<Node> kids` |  |
| 473 | `private final Map<String, Integer> slugs` | GitHub's count of each anchor so far |
| 474 | `private final Map<String, String> anchorOf` | GitHub's count of each anchor so far |
| 475 | `private final Map<String, String> numberAt` | element id -> the first heading inside it |
| 476 | `private final Map<String, Integer> linkLine` | anchor -> the section number its heading shows |
| 477 | `private final List<String[]> contents` | in-page link target -> the line it is first used on |
| 478 | `private final List<String> waiting` | {number, id, line} of each numbered in-page entry |
| 479 | `private boolean hasHeader, headerDone, inFooter` | ids whose first heading has not been written yet |
| 480 | `private Node deferredNav` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 66 | 1457 | **type** `public final class ManualToMarkdown` | The published manual as two files the repository keeps: docs/manual.md, which GitHub renders when it is clicked, and docs/manual.html, the page itself as a standalone file that opens in a browser from a clone. |

### THE VOCABULARY (lines 68-117)

### RUNNING IT (lines 118-164)

| line | len | member | says |
|---:|---:|---|---|
| 122 | 19 | `public static void main(String[] args) throws IOException` |  |
| 146 | 5 | `static String read(Path p) throws IOException` | The page as text: UTF-8, without a byte-order mark, LF line endings. |
| 152 | 5 | `static int lines(String s)` |  |
| 159 | 5 | `void warn(String key, int line, String what)` | Says once, on stderr, what the tool did not recognise and where; the output carries on regardless. |

### THE PAGE AS PUBLISHED, AND AS A STANDALONE FILE (lines 165-248)

| line | len | member | says |
|---:|---:|---|---|
| 170 | 3 | `static boolean isWrapped(String page)` | The artifact service and --wrap both put a doctype first; the page as published starts with its title. |
| 179 | 6 | `String wrap(String page)` | The standalone page. |
| 191 | 20 | `String unwrap(String text)` | The page inside a wrapper. |
| 213 | 19 | `static int headEnd(String fragment)` | Where the page's head material ends: after the last of the title, links, metas and style it opens with. |
| 233 | 7 | `static boolean startsWithHead(String body)` |  |
| 241 | 7 | `static String withoutMetas(String head)` |  |

### THE WALK: TOKENS, THEN A TREE (lines 249-466)

| line | len | member | says |
|---:|---:|---|---|
| 253 | 1 | **type** `enum Kind` |  |
| 256 | 1 | **type** `record Tok(Kind kind, String name, Map<String, String> attrs, String text, int start, int end, int line)` | A tag, a comment or a run of text, with where it sits in the page. |
| 258 | 54 | `static List<Tok> tokens(String s)` |  |
| 313 | 4 | `static int nameEnd(String s, int j)` |  |
| 319 | 32 | `static int attributes(String s, int j, Map<String, String> attrs)` | Reads a start tag's attributes and returns the index just past its '>'. |
| 352 | 5 | `static int newlines(String s, int from, int to)` |  |
| 358 | 4 | `static int indexOfIgnoreCase(String s, String what, int from)` |  |
| 364 | 20 | **type** `static final class Node` | One element or run of text, with the line it starts on. |
| 371 | 3 | `Node(String tag, Map<String, String> attrs, String text, int line, Node parent)` _(in ManualToMarkdown.Node)_ |  |
| 375 | 1 | `boolean isText()` _(in ManualToMarkdown.Node)_ |  |
| 376 | 1 | `boolean is(String t)` _(in ManualToMarkdown.Node)_ |  |
| 377 | 1 | `String attr(String name)` _(in ManualToMarkdown.Node)_ |  |
| 379 | 4 | `boolean has(String cls)` _(in ManualToMarkdown.Node)_ |  |
| 386 | 33 | `Node parse(String s)` | The page as a tree. |
| 420 | 7 | `void vocabulary(Tok t)` |  |
| 429 | 27 | `String decode(String s, int line)` | Decodes every character reference: the named ones HTML defines, and numeric ones as HTML reads them. |
| 461 | 5 | `static String codePoint(int cp)` |  |

### THE MARKDOWN: BLOCKS (lines 467-846)

| line | len | member | says |
|---:|---:|---|---|
| 471 | 1 | **type** `enum Mode` |  |
| 482 | 14 | `String markdown(String page)` |  |
| 497 | 8 | `static Node find(Node n, String tag)` |  |
| 507 | 8 | `static List<Node> flat(Node parent)` | The children, with unknown tags (and html and body) opened up so that their contents stand in their place. |
| 517 | 29 | `void blocks(Node parent, List<String> out, Node skip)` | Renders an element's children as blocks; each run of inline content between them is a paragraph. |
| 547 | 33 | `void block(Node k, List<String> out)` |  |
| 582 | 5 | `void paragraph(StringBuilder run, List<String> out)` | A paragraph, if the run holds anything; in the footer each line break starts a new one. |
| 588 | 3 | `static void add(List<String> out, String block)` |  |
| 592 | 5 | `StringBuilder inlineOf(Node k)` |  |
| 598 | 5 | `StringBuilder italic(Node k)` |  |
| 604 | 5 | `StringBuilder bold(Node k)` |  |
| 611 | 12 | `void header(Node k, List<String> out)` | The title first and the build line under it, then the rest in order, then the contents. |
| 625 | 12 | `void sechead(Node k, List<String> out)` | A section's head: "## 1. |
| 638 | 4 | `static String number(String s)` |  |
| 644 | 12 | `void heading(int level, String prefix, Node h, List<String> out)` | A heading, and the anchor GitHub will give it - which is where every in-page link to its section goes. |
| 662 | 11 | `static String slug(String heading)` | GitHub's heading anchor: lower case, every character dropped that is not a letter, a mark, a digit, an underscore, a hyphen or a space, then each space a hyphen - so "5. |
| 675 | 12 | `void note(Node k, List<String> out)` | A note as a block quote: its label in bold on the first line, then its paragraphs. |
| 689 | 12 | `void formula(Node k, List<String> out)` | A formula as a fenced block, with its spacing as the page shows it - the page sets it white-space: pre. |
| 703 | 8 | `static void pre(Node n, StringBuilder sb)` | Text as preformatted: whitespace kept, tags gone (a link is its words), a superscript ^, a <br> a new line. |
| 712 | 8 | `static int longestRun(String s, char c)` |  |
| 725 | 26 | `void list(Node k, List<String> out)` | A list. |
| 752 | 9 | `static String listNumber(Node n)` |  |
| 762 | 8 | `static String indent(String marker, String body)` |  |
| 772 | 7 | `void vitals(Node dl, List<String> out)` | The vitals: a table of two rows, the terms over their values. |
| 780 | 8 | `void terms(Node n, List<String> terms, List<String> values)` |  |
| 790 | 33 | `void table(Node t, List<String> out)` | A table, its caption in italics above it; the head row is the thead's, or the first. |
| 824 | 5 | `String cell(Node c)` |  |
| 831 | 15 | `static String gfmTable(List<List<String>> rows, List<Boolean> right)` | A GitHub table; a column is right-aligned where its head cell has class num. |

### THE MARKDOWN: INLINE TEXT (lines 847-1133)

| line | len | member | says |
|---:|---:|---|---|
| 852 | 7 | **type** `record Ctx(boolean italic, boolean bold, boolean link, boolean reversible)` | Where inline text sits: in italics, in bold, in a link - and whether an <em> here can be set roman. |
| 854 | 1 | `Ctx em()` _(in ManualToMarkdown.Ctx)_ |  |
| 855 | 1 | `Ctx roman()` _(in ManualToMarkdown.Ctx)_ |  |
| 856 | 1 | `Ctx strong()` _(in ManualToMarkdown.Ctx)_ |  |
| 857 | 1 | `Ctx inLink()` _(in ManualToMarkdown.Ctx)_ |  |
| 860 | 3 | `void kids(Node n, StringBuilder sb, Ctx ctx)` |  |
| 865 | 42 | `void inline(Node n, StringBuilder sb, Ctx ctx)` | One node as inline Markdown, with sentinels where the spelling waits on the neighbours. |
| 908 | 4 | `static boolean inside(Node n, String tag)` |  |
| 914 | 8 | `static String text(Node n)` | The text of an element as it reads: tags gone, a superscript ^, a line break a space. |
| 923 | 3 | `static String collapse(String s)` |  |
| 927 | 4 | `static String sup(String s)` |  |
| 933 | 8 | `static void code(String raw, StringBuilder sb)` | A code span: fenced with more backticks than it holds, its outer spaces left outside it. |
| 943 | 7 | `String target(String href, int line)` | A link's target: an in-page #id waits for the anchor of the heading it names. |
| 951 | 15 | `String anchors(String md)` |  |
| 972 | 91 | `static String resolve(CharSequence raw, Mode mode)` | Inline text with its sentinels, as Markdown. |
| 1065 | 17 | `static String collapseOutsideCode(CharSequence raw)` | Collapses runs of HTML whitespace to one space, leaving code spans (already collapsed) alone. |
| 1083 | 3 | `static boolean sentinel(char c)` |  |
| 1087 | 3 | `static boolean emphasis(char c)` |  |
| 1091 | 3 | `static char at(String s, int i)` |  |
| 1096 | 14 | `static boolean mathPair(String s)` | Whether a text could hold $...$ that GitHub would take for mathematics: an opening $, and a closing one after it. |
| 1111 | 13 | `static int plainCount(String s, char what)` |  |
| 1126 | 7 | `static int orderedMarker(String s, int i)` | The index of the '.' or ')' in a line that opens "12." or "12)" and so would start an ordered list, or -1. |

### CommonMark's flanking rules, read strictly: a symbol counts as punctuation when that makes a rule fail (lines 1134-1202)

| line | len | member | says |
|---:|---:|---|---|
| 1136 | 3 | `static boolean opens(char before, char after)` |  |
| 1140 | 3 | `static boolean closes(char before, char after)` |  |
| 1144 | 3 | `static boolean space(char c)` |  |
| 1148 | 7 | `static boolean surePunct(char c)` |  |
| 1156 | 5 | `static boolean maybePunct(char c)` |  |
| 1163 | 7 | `static char before(String e, int i, Mode mode)` | The character that will stand just before position i once the sentinels are spelled out. |
| 1172 | 7 | `static char after(String e, int i, Mode mode)` | The character that will stand just after position i once the sentinels are spelled out. |
| 1180 | 10 | `static char spelled(char c, Mode mode, boolean first)` |  |
| 1192 | 10 | `static String tidy(String md)` | No trailing spaces except a deliberate line break, and one newline at the end. |

### THE NAMED CHARACTER REFERENCES (lines 1203-1522)

| line | len | member | says |
|---:|---:|---|---|
| 1209 | 10 | `static Map<String, String> entities()` |  |

