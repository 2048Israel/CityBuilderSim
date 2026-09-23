package ham.citybuildersim.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The published manual as two files the repository keeps: docs/manual.md,
 * which GitHub renders when it is clicked, and docs/manual.html, the page
 * itself as a standalone file that opens in a browser from a clone.
 *
 *     java -cp target/classes ham.citybuildersim.tools.ManualToMarkdown --wrap page.html docs/manual.html
 *     java -cp target/classes ham.citybuildersim.tools.ManualToMarkdown docs/manual.html docs/manual.md
 *
 * WHY. The manual is the model written out, and it lived only as a claude.ai
 * artifact ("CityBuilderSim"), so reading it from the repository meant
 * following a link out of it. Both files are derived from the page at every
 * publish, which makes the derivation a tool in the tree rather than a script
 * somebody keeps. docs/manual.html and docs/manual.md are GENERATED: never
 * edit them. Change the page, publish it, pull it, and run the two lines above
 * in that order - the second reads what the first wrote.
 *
 * WHAT IT ACCEPTS. The page as published - a fragment that opens with its
 * <title>, <link>s and <style> and then <div class="shell"> - or the same page
 * wrapped in <!doctype html><html><head>...</head><body>...</body></html>, as
 * the artifact service serves it or as --wrap writes it. A text that begins
 * <!doctype is the wrapped form: --wrap unwraps it first, and the Markdown
 * walk reads it as it stands (html and body opened up, the head furniture), so
 * a warning's line is the file's own. Either form gives the same bytes out of
 * either mode. Line endings are read as LF, so a CRLF checkout changes nothing.
 *
 * WHAT IT EMITS. With --wrap, the standalone page: the doctype, <html
 * lang="en">, a head holding the charset, the viewport and the page's own
 * title, links and style, then a body holding everything from <div
 * class="shell"> on. The published bytes are carried verbatim, and --wrap of
 * its own output is its own output. Without --wrap, GitHub-flavoured Markdown:
 * a first line saying the file is generated, the title, the build line, the
 * vitals as a table, the contents as links to GitHub's own heading anchors,
 * then each section as "## N. Title" with its tables, formulas as code
 * blocks, notes as block quotes and the month as a numbered list, and the
 * footer under a rule.
 *
 * THE WALK. No HTML library: a tokenizer, a tree, and a renderer for the tag
 * and class vocabulary the page actually uses (KNOWN_TAGS, KNOWN_CLASSES). A
 * tag or class outside it is passed through as its text and named once on
 * stderr with its line - the next version of the page may add one, and the
 * tool must degrade, not fail. A clean run prints no warnings. The Markdown
 * is written to render on github.com, which is stricter than it looks: an
 * emphasis GitHub would not read as one is spelled <em> or <strong>, and in a
 * paragraph or cell where two dollar signs could pair every one is escaped,
 * because GitHub reads $...$ as mathematics.
 */
public final class ManualToMarkdown {

    /* ------------------------------------------------------------------
       THE VOCABULARY
       ------------------------------------------------------------------ */

    /** The Markdown's first line: says the file is generated, and renders as nothing on GitHub. */
    static final String GENERATED = "<!-- Derived from the published manual by ham.citybuildersim.tools.ManualToMarkdown"
            + " \u2014 do not edit; regenerate from docs/manual.html -->";

    /** What --wrap writes in front of the page's own title, links and style. */
    static final String STANDALONE_HEAD = "<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n";

    /** Every tag the page and its wrappers use; any other is passed through as its text, with a warning. */
    static final Set<String> KNOWN_TAGS = Set.of("html", "head", "body", "meta", "title", "link", "style",
            "div", "nav", "main", "header", "footer", "section", "h1", "h2", "h3", "h4", "h5", "h6", "p",
            "ol", "ul", "li", "dl", "dt", "dd", "table", "caption", "thead", "tbody", "tr", "th", "td",
            "a", "span", "strong", "b", "em", "code", "br", "sup");

    /** Every class the page uses; an element with another is rendered as its bare tag, with a warning. */
    static final Set<String> KNOWN_CLASSES = Set.of("shell", "navtitle", "navnum", "eyebrow", "standfirst",
            "vitals", "vital", "sechead", "secnum", "kicker", "turn", "step", "what", "why", "note", "warn",
            "crit", "lbl", "formula", "scroll", "cols", "num", "nm", "dim", "mono", "k");

    /** The elements that are blocks in Markdown; everything else is inline text inside one. */
    static final Set<String> BLOCKS = Set.of("head", "title", "link", "meta", "style", "div", "nav", "main",
            "header", "footer", "section", "h1", "h2", "h3", "h4", "h5", "h6", "p", "ol", "ul", "li", "dl", "dt",
            "dd", "table", "caption", "thead", "tbody", "tr", "th", "td");

    /** Elements with no end tag. */
    static final Set<String> VOID = Set.of("br", "link", "meta", "hr", "img", "input", "wbr", "base", "col",
            "area", "embed", "source", "track", "param");

    /** Elements whose content is text, never tags. */
    static final Set<String> RAW = Set.of("style", "script", "title", "textarea");

    /** The elements that belong in a head: what --wrap moves there, and how the service's wrapper is recognised. */
    static final Set<String> HEAD_TAGS = Set.of("title", "link", "meta", "style", "base", "script");

    /*
     * Inline Markdown is built as text in which these private-use characters stand for the constructs whose
     * spelling depends on what ends up beside them, and it is resolved once a whole paragraph or cell is known.
     * The anchor pair wraps an in-page link's #id until every heading, and so every anchor, has been written.
     */
    static final char EM_OPEN = '\uE001', EM_CLOSE = '\uE002', STRONG_OPEN = '\uE003', STRONG_CLOSE = '\uE004',
            BREAK = '\uE005', CODE_OPEN = '\uE006', CODE_CLOSE = '\uE007', LINK_OPEN = '\uE008',
            LINK_MID = '\uE009', LINK_CLOSE = '\uE00A', ANCHOR_OPEN = '\uE00B', ANCHOR_CLOSE = '\uE00C';

    /** Text that Markdown would read as a character reference and decode a second time. */
    static final Pattern ENTITY_LIKE = Pattern.compile("&(#[0-9]+|#[xX][0-9a-fA-F]+|[A-Za-z][A-Za-z0-9]*);");

    /* ------------------------------------------------------------------
       RUNNING IT
       ------------------------------------------------------------------ */

    public static void main(String[] args) throws IOException {
        boolean wrap = args.length == 3 && args[0].equals("--wrap");
        if (!wrap && (args.length != 2 || args[0].startsWith("--"))) {
            System.out.println("usage: ManualToMarkdown <page.html> <manual.md>");
            System.out.println("       ManualToMarkdown --wrap <page.html> <standalone.html>");
            System.exit(2);
        }
        Path in = Path.of(args[wrap ? 1 : 0]), out = Path.of(args[wrap ? 2 : 1]);
        String page = read(in);
        ManualToMarkdown tool = new ManualToMarkdown();
        String result = wrap ? tool.wrap(page) : tool.markdown(page);
        Files.writeString(out, result, StandardCharsets.UTF_8);
        if (wrap) System.out.printf("%s: the standalone page, %,d lines, from %s%s%n", out, lines(result), in,
                isWrapped(page) ? " (unwrapped first)" : "");
        else System.out.printf("%s: %d sections, %d tables, %d formulas, %d notes, %d steps of the month, %,d lines, from %s%n",
                out, tool.sections, tool.tables, tool.formulas, tool.notes, tool.steps, lines(result), in);
        if (tool.warnings > 0) System.out.println(tool.warnings + " warning" + (tool.warnings == 1 ? "" : "s")
                + " on stderr - the page has changed shape; read the output where they point");
    }

    private final Set<String> warned = new HashSet<>();
    private int warnings, sections, tables, formulas, notes, steps;

    /** The page as text: UTF-8, without a byte-order mark, LF line endings. */
    static String read(Path p) throws IOException {
        String s = Files.readString(p, StandardCharsets.UTF_8);
        if (s.startsWith("\uFEFF")) s = s.substring(1);
        return s.replace("\r\n", "\n");
    }

    static int lines(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '\n') n++;
        return n;
    }

    /** Says once, on stderr, what the tool did not recognise and where; the output carries on regardless. */
    void warn(String key, int line, String what) {
        if (!warned.add(key)) return;
        warnings++;
        System.err.println("ManualToMarkdown: line " + line + ": " + what);
    }

    /* ------------------------------------------------------------------
       THE PAGE AS PUBLISHED, AND AS A STANDALONE FILE
       ------------------------------------------------------------------ */

    /** The artifact service and --wrap both put a doctype first; the page as published starts with its title. */
    static boolean isWrapped(String page) {
        return page.stripLeading().regionMatches(true, 0, "<!doctype", 0, 9);
    }

    /**
     * The standalone page. Everything before the page's first body element (its title, links and style) goes
     * in the head, everything from there on in the body, both byte for byte; a wrapped page is unwrapped
     * first, so wrapping is idempotent.
     */
    String wrap(String page) {
        String fragment = isWrapped(page) ? unwrap(page) : page;
        int split = headEnd(fragment);
        String head = fragment.substring(0, split).strip(), body = fragment.substring(split).stripTrailing();
        return STANDALONE_HEAD + (head.isEmpty() ? "" : head + "\n") + "</head>\n<body>" + body + "\n</body>\n</html>\n";
    }

    /**
     * The page inside a wrapper. The artifact service puts all of it in the body, after one line break, and
     * its own head is furniture; --wrap puts the page's title, links and style in the head after the two
     * metas it adds, and the rest in the body.
     */
    String unwrap(String text) {
        Tok headOpen = null, headClose = null, bodyOpen = null, bodyClose = null;
        for (Tok t : tokens(text)) {
            boolean start = t.kind() == Kind.START, end = t.kind() == Kind.END;
            if (start && "head".equals(t.name()) && headOpen == null) headOpen = t;
            else if (end && "head".equals(t.name()) && headClose == null) headClose = t;
            else if (start && "body".equals(t.name()) && bodyOpen == null) bodyOpen = t;
            else if (end && "body".equals(t.name())) bodyClose = t;
        }
        if (bodyOpen == null) {
            warn("no body", 1, "the page starts <!doctype but has no <body> - taken as it stands");
            return text;
        }
        int bodyEnd = bodyClose != null && bodyClose.start() >= bodyOpen.end() ? bodyClose.start() : text.length();
        String body = text.substring(bodyOpen.end(), bodyEnd);
        if (startsWithHead(body)) return (body.startsWith("\n") ? body.substring(1) : body).stripTrailing() + "\n";
        String head = headOpen != null && headClose != null && headClose.start() >= headOpen.end()
                ? text.substring(headOpen.end(), headClose.start()) : "";
        return withoutMetas(head).strip() + body.stripTrailing() + "\n";
    }

    /** Where the page's head material ends: after the last of the title, links, metas and style it opens with. */
    static int headEnd(String fragment) {
        List<Tok> toks = tokens(fragment);
        int split = 0;
        for (int i = 0; i < toks.size(); i++) {
            Tok t = toks.get(i);
            if (t.kind() == Kind.COMMENT || t.kind() == Kind.TEXT && t.text().isBlank()) continue;
            if (t.kind() != Kind.START || !HEAD_TAGS.contains(t.name())) break;
            int end = t.end();
            if (!VOID.contains(t.name())) {
                int j = i + 1;
                while (j < toks.size() && !(toks.get(j).kind() == Kind.END && t.name().equals(toks.get(j).name()))) j++;
                if (j == toks.size()) break;
                end = toks.get(j).end();
                i = j;
            }
            split = end;
        }
        return split;
    }

    static boolean startsWithHead(String body) {
        for (Tok t : tokens(body)) {
            if (t.kind() == Kind.COMMENT || t.kind() == Kind.TEXT && t.text().isBlank()) continue;
            return t.kind() == Kind.START && HEAD_TAGS.contains(t.name());
        }
        return false;
    }

    static String withoutMetas(String head) {
        for (Tok t : tokens(head)) {
            if (t.kind() == Kind.TEXT && t.text().isBlank() || t.kind() == Kind.START && t.name().equals("meta")) continue;
            return head.substring(t.start());
        }
        return "";
    }

    /* ------------------------------------------------------------------
       THE WALK: TOKENS, THEN A TREE
       ------------------------------------------------------------------ */

    enum Kind { TEXT, START, END, COMMENT, OTHER }

    /** A tag, a comment or a run of text, with where it sits in the page. */
    record Tok(Kind kind, String name, Map<String, String> attrs, String text, int start, int end, int line) { }

    static List<Tok> tokens(String s) {
        List<Tok> out = new ArrayList<>();
        int n = s.length(), i = 0, line = 1;
        while (i < n) {
            int at = i;
            Kind kind;
            String name = null, text = null;
            Map<String, String> attrs = Map.of();
            char next = i + 1 < n ? s.charAt(i + 1) : '\0';
            if (s.charAt(i) != '<') {
                int lt = s.indexOf('<', i);
                i = lt < 0 ? n : lt;
                kind = Kind.TEXT;
                text = s.substring(at, i);
            } else if (s.startsWith("<!--", i)) {
                int c = s.indexOf("-->", i + 4);
                i = c < 0 ? n : c + 3;
                kind = Kind.COMMENT;
            } else if (next == '!' || next == '?') {
                int c = s.indexOf('>', i);
                i = c < 0 ? n : c + 1;
                kind = Kind.OTHER;
            } else if (next == '/' && i + 2 < n && Character.isLetter(s.charAt(i + 2))) {
                int j = nameEnd(s, i + 2);
                name = s.substring(i + 2, j).toLowerCase(Locale.ROOT);
                int c = s.indexOf('>', j);
                i = c < 0 ? n : c + 1;
                kind = Kind.END;
            } else if (Character.isLetter(next)) {
                int j = nameEnd(s, i + 1);
                name = s.substring(i + 1, j).toLowerCase(Locale.ROOT);
                attrs = new LinkedHashMap<>();
                i = attributes(s, j, attrs);
                kind = Kind.START;
            } else {                                             // a '<' that opens nothing is text
                int lt = s.indexOf('<', i + 1);
                i = lt < 0 ? n : lt;
                kind = Kind.TEXT;
                text = s.substring(at, i);
            }
            out.add(new Tok(kind, name, attrs, text, at, i, line));
            line += newlines(s, at, i);
            if (kind == Kind.START && RAW.contains(name)) {   // its content runs to the matching end tag, tags or not
                int close = indexOfIgnoreCase(s, "</" + name, i);
                if (close < 0) close = n;
                if (close > i) {
                    out.add(new Tok(Kind.TEXT, null, Map.of(), s.substring(i, close), i, close, line));
                    line += newlines(s, i, close);
                }
                i = close;
            }
        }
        return out;
    }

    static int nameEnd(String s, int j) {
        while (j < s.length() && (Character.isLetterOrDigit(s.charAt(j)) || "-_:".indexOf(s.charAt(j)) >= 0)) j++;
        return j;
    }

    /** Reads a start tag's attributes and returns the index just past its '>'. */
    static int attributes(String s, int j, Map<String, String> attrs) {
        int n = s.length();
        while (j < n) {
            char c = s.charAt(j);
            if (c == '>') return j + 1;
            if (Character.isWhitespace(c) || c == '/') { j++; continue; }
            int k = j;
            while (k < n && !Character.isWhitespace(s.charAt(k)) && "=>/".indexOf(s.charAt(k)) < 0) k++;
            String name = s.substring(j, k).toLowerCase(Locale.ROOT), value = "";
            int m = k;
            while (m < n && Character.isWhitespace(s.charAt(m))) m++;
            if (m < n && s.charAt(m) == '=') {
                m++;
                while (m < n && Character.isWhitespace(s.charAt(m))) m++;
                if (m < n && (s.charAt(m) == '"' || s.charAt(m) == '\'')) {
                    int close = s.indexOf(s.charAt(m), m + 1);
                    if (close < 0) close = n;
                    value = s.substring(m + 1, close);
                    m = Math.min(n, close + 1);
                } else {
                    int v = m;
                    while (v < n && !Character.isWhitespace(s.charAt(v)) && s.charAt(v) != '>') v++;
                    value = s.substring(m, v);
                    m = v;
                }
                k = m;
            }
            if (!name.isEmpty()) attrs.putIfAbsent(name, value);
            j = Math.max(k, j + 1);
        }
        return n;
    }

    static int newlines(String s, int from, int to) {
        int n = 0;
        for (int i = from; i < to; i++) if (s.charAt(i) == '\n') n++;
        return n;
    }

    static int indexOfIgnoreCase(String s, String what, int from) {
        for (int i = from; i + what.length() <= s.length(); i++) if (s.regionMatches(true, i, what, 0, what.length())) return i;
        return -1;
    }

    /** One element or run of text, with the line it starts on. */
    static final class Node {
        final String tag, text;
        final Map<String, String> attrs;
        final int line;
        final Node parent;
        final List<Node> kids = new ArrayList<>();

        Node(String tag, Map<String, String> attrs, String text, int line, Node parent) {
            this.tag = tag; this.attrs = attrs; this.text = text; this.line = line; this.parent = parent;
        }

        boolean isText() { return tag == null; }
        boolean is(String t) { return t.equals(tag); }
        String attr(String name) { return attrs.getOrDefault(name, ""); }

        boolean has(String cls) {
            for (String c : attr("class").split("\\s+")) if (c.equals(cls)) return true;
            return false;
        }
    }

    /** The page as a tree. Comments (the "==== 1" rule before each section) and the doctype are dropped. */
    Node parse(String s) {
        Node root = new Node("#root", Map.of(), null, 1, null);
        List<Node> open = new ArrayList<>(List.of(root));
        for (Tok t : tokens(s)) {
            Node top = open.get(open.size() - 1);
            switch (t.kind()) {
                case TEXT -> top.kids.add(new Node(null, Map.of(),
                        top.is("style") || top.is("script") ? t.text() : decode(t.text(), t.line()), t.line(), top));
                case START -> {
                    vocabulary(t);
                    Map<String, String> attrs = new LinkedHashMap<>();
                    t.attrs().forEach((k, v) -> attrs.put(k, decode(v, t.line())));
                    Node el = new Node(t.name(), attrs, null, t.line(), top);
                    top.kids.add(el);
                    if (!VOID.contains(t.name())) open.add(el);
                }
                case END -> {
                    if (VOID.contains(t.name())) break;
                    int k = open.size() - 1;
                    while (k > 0 && !open.get(k).is(t.name())) k--;
                    if (k == 0) {
                        warn("stray " + t.name(), t.line(), "</" + t.name() + "> closes nothing that is open - ignored");
                        break;
                    }
                    if (k < open.size() - 1) warn("nest " + t.line(), t.line(), "</" + t.name() + "> also closes <"
                            + open.get(open.size() - 1).tag + "> - the page is misnested here");
                    while (open.size() > k) open.remove(open.size() - 1);
                }
                default -> { }
            }
        }
        return root;
    }

    void vocabulary(Tok t) {
        if (!KNOWN_TAGS.contains(t.name()))
            warn("tag " + t.name(), t.line(), "unknown tag <" + t.name() + "> - passed through as its text");
        for (String c : t.attrs().getOrDefault("class", "").trim().split("\\s+"))
            if (!c.isEmpty() && !KNOWN_CLASSES.contains(c))
                warn("class " + c, t.line(), "unknown class \"" + c + "\" on <" + t.name() + "> - rendered as a plain <" + t.name() + ">");
    }

    /** Decodes every character reference: the named ones HTML defines, and numeric ones as HTML reads them. */
    String decode(String s, int line) {
        if (s.indexOf('&') >= 0) {
            StringBuilder sb = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); ) {
                char c = s.charAt(i);
                int semi = c == '&' ? s.indexOf(';', i) : -1;
                if (semi > i + 1 && semi - i <= 33) {
                    String name = s.substring(i + 1, semi), v = null;
                    if (name.matches("#[0-9]{1,8}")) v = codePoint(Integer.parseInt(name.substring(1)));
                    else if (name.matches("#[xX][0-9a-fA-F]{1,7}")) v = codePoint(Integer.parseInt(name.substring(2), 16));
                    else if (name.matches("[A-Za-z][A-Za-z0-9]*")) {
                        v = ENTITIES.get(name);
                        if (v == null) warn("entity " + name, line + newlines(s, 0, i), "unknown entity &" + name + "; - kept as written");
                    }
                    if (v != null) {
                        sb.append(v);
                        i = semi + 1;
                        continue;
                    }
                }
                sb.append(c);
                i++;
            }
            s = sb.toString();
        }
        return s.replaceAll("[\uE000-\uE00F]", "\uFFFD");     // the sentinels are this tool's own
    }

    /** What HTML reads &#128; to &#159; as: the Windows-1252 characters, not the C1 controls. */
    static final String CP1252 = "\u20AC\u0081\u201A\u0192\u201E\u2026\u2020\u2021\u02C6\u2030\u0160\u2039\u0152\u008D\u017D\u008F"
            + "\u0090\u2018\u2019\u201C\u201D\u2022\u2013\u2014\u02DC\u2122\u0161\u203A\u0153\u009D\u017E\u0178";

    static String codePoint(int cp) {
        if (cp >= 0x80 && cp <= 0x9F) return String.valueOf(CP1252.charAt(cp - 0x80));
        if (cp == 0 || cp > 0x10FFFF || cp >= 0xD800 && cp <= 0xDFFF) return "\uFFFD";
        return new String(Character.toChars(cp));
    }

    /* ------------------------------------------------------------------
       THE MARKDOWN: BLOCKS
       ------------------------------------------------------------------ */

    enum Mode { BLOCK, CELL, HEADING }

    private final Map<String, Integer> slugs = new HashMap<>();        // GitHub's count of each anchor so far
    private final Map<String, String> anchorOf = new HashMap<>();      // element id -> the first heading inside it
    private final Map<String, String> numberAt = new HashMap<>();      // anchor -> the section number its heading shows
    private final Map<String, Integer> linkLine = new HashMap<>();     // in-page link target -> the line it is first used on
    private final List<String[]> contents = new ArrayList<>();         // {number, id, line} of each numbered in-page entry
    private final List<String> waiting = new ArrayList<>();            // ids whose first heading has not been written yet
    private boolean hasHeader, headerDone, inFooter;
    private Node deferredNav;

    String markdown(String page) {
        Node root = parse(page);
        hasHeader = find(root, "header") != null;
        List<String> out = new ArrayList<>();
        blocks(root, out, null);
        if (deferredNav != null) block(deferredNav, out);
        String md = anchors(GENERATED + "\n\n" + String.join("\n\n", out) + "\n");
        for (String[] c : contents) {
            String anchor = anchorOf.get(c[1]), shown = anchor == null ? null : numberAt.get(anchor);
            if (shown != null && !shown.equals(c[0]))
                warn("contents " + c[1], Integer.parseInt(c[2]), "the contents number #" + c[1] + " " + c[0] + " but its heading says " + shown);
        }
        return tidy(md);
    }

    static Node find(Node n, String tag) {
        if (n.is(tag)) return n;
        for (Node k : n.kids) {
            Node f = find(k, tag);
            if (f != null) return f;
        }
        return null;
    }

    /** The children, with unknown tags (and html and body) opened up so that their contents stand in their place. */
    static List<Node> flat(Node parent) {
        List<Node> out = new ArrayList<>();
        for (Node k : parent.kids) {
            if (!k.isText() && (!KNOWN_TAGS.contains(k.tag) || k.is("html") || k.is("body"))) out.addAll(flat(k));
            else out.add(k);
        }
        return out;
    }

    /** Renders an element's children as blocks; each run of inline content between them is a paragraph. */
    void blocks(Node parent, List<String> out, Node skip) {
        StringBuilder run = new StringBuilder();
        List<Node> kids = flat(parent);
        for (int i = 0; i < kids.size(); i++) {
            Node k = kids.get(i);
            if (k == skip) continue;
            if (k.isText() || !BLOCKS.contains(k.tag)) {
                inline(k, run, Ctx.PLAIN);
                continue;
            }
            paragraph(run, out);
            if (k.has("what")) {                                // a step of the month: what in bold, then why
                StringBuilder p = new StringBuilder().append(STRONG_OPEN);
                kids(k, p, Ctx.PLAIN.strong());
                p.append(STRONG_CLOSE);
                int j = i + 1;
                while (j < kids.size() && kids.get(j).isText() && kids.get(j).text.isBlank()) j++;
                if (j < kids.size() && kids.get(j).has("why")) {
                    p.append(" \u2014 ");
                    kids(kids.get(j), p, Ctx.PLAIN);
                    i = j;
                }
                paragraph(p, out);
                continue;
            }
            block(k, out);
        }
        paragraph(run, out);
    }

    void block(Node k, List<String> out) {
        String id = k.attr("id");
        if (!id.isEmpty()) waiting.add(id);
        switch (k.tag) {
            case "p" -> paragraph(k.has("kicker") ? italic(k) : inlineOf(k), out);
            case "div" -> {
                if (k.has("sechead")) sechead(k, out);
                else if (k.has("note")) note(k, out);
                else if (k.has("formula")) formula(k, out);
                else if (k.has("eyebrow")) paragraph(italic(k), out);
                else if (k.has("navtitle")) paragraph(bold(k), out);
                else blocks(k, out, null);                      // shell, cols, scroll, vital, why: children in order
            }
            case "h1", "h2", "h3", "h4", "h5", "h6" -> heading(k.tag.charAt(1) - '0', "", k, out);
            case "nav" -> {                                     // the contents belong under the title, not above it
                if (hasHeader && !headerDone && deferredNav == null) deferredNav = k;
                else blocks(k, out, null);
            }
            case "header" -> header(k, out);
            case "footer" -> {
                out.add("---");
                inFooter = true;
                blocks(k, out, null);
                inFooter = false;
            }
            case "ol", "ul" -> list(k, out);
            case "dl" -> vitals(k, out);
            case "table" -> table(k, out);
            case "head", "title", "link", "meta", "style" -> { }   // furniture: the h1 is the title
            default -> blocks(k, out, null);                    // section, main, and anything out of its usual place
        }
        waiting.remove(id);                                     // an id with no heading inside it anchors nothing
    }

    /** A paragraph, if the run holds anything; in the footer each line break starts a new one. */
    void paragraph(StringBuilder run, List<String> out) {
        if (inFooter) for (String part : run.toString().split(String.valueOf(BREAK))) add(out, resolve(part, Mode.BLOCK));
        else add(out, resolve(run, Mode.BLOCK));
        run.setLength(0);
    }

    static void add(List<String> out, String block) {
        if (!block.isEmpty()) out.add(block);
    }

    StringBuilder inlineOf(Node k) {
        StringBuilder sb = new StringBuilder();
        kids(k, sb, Ctx.PLAIN);
        return sb;
    }

    StringBuilder italic(Node k) {
        StringBuilder sb = new StringBuilder().append(EM_OPEN);
        kids(k, sb, Ctx.PLAIN.em());
        return sb.append(EM_CLOSE);
    }

    StringBuilder bold(Node k) {
        StringBuilder sb = new StringBuilder().append(STRONG_OPEN);
        kids(k, sb, Ctx.PLAIN.strong());
        return sb.append(STRONG_CLOSE);
    }

    /** The title first and the build line under it, then the rest in order, then the contents. */
    void header(Node k, List<String> out) {
        Node h1 = null;
        for (Node c : flat(k)) if (c.is("h1")) { h1 = c; break; }
        if (h1 != null) block(h1, out);
        blocks(k, out, h1);
        headerDone = true;
        if (deferredNav != null) {
            Node nav = deferredNav;
            deferredNav = null;
            block(nav, out);
        }
    }

    /** A section's head: "## 1. The month" from <span class="secnum">01</span><h2>The month</h2>. */
    void sechead(Node k, List<String> out) {
        Node num = null, h = null;
        for (Node c : flat(k)) {
            if (c.isText()) continue;
            if (num == null && c.has("secnum")) num = c;
            else if (h == null && c.tag.matches("h[1-6]")) h = c;
        }
        if (h == null) { blocks(k, out, null); return; }
        String n = num == null ? "" : number(text(num));
        heading(h.tag.charAt(1) - '0', n.isEmpty() ? "" : n + ". ", h, out);
        sections++;
    }

    static String number(String s) {
        String t = s.strip();
        return t.matches("\\d+") ? t.replaceFirst("^0+(?=\\d)", "") : t;
    }

    /** A heading, and the anchor GitHub will give it - which is where every in-page link to its section goes. */
    void heading(int level, String prefix, Node h, List<String> out) {
        String slug = slug((prefix + collapse(text(h))).strip());
        int seen = slugs.merge(slug, 1, Integer::sum) - 1;     // GitHub: the second "foo" is foo-1, the third foo-2
        String anchor = seen == 0 ? slug : slug + "-" + seen;
        for (String id : waiting) anchorOf.putIfAbsent(id, anchor);
        waiting.clear();
        if (!prefix.isEmpty()) numberAt.put(anchor, prefix.substring(0, prefix.length() - 2));
        StringBuilder sb = new StringBuilder(prefix);
        kids(h, sb, Ctx.PLAIN);
        String text = resolve(sb, Mode.HEADING);
        out.add("#".repeat(level) + (text.isEmpty() ? "" : " " + text));
    }

    /**
     * GitHub's heading anchor: lower case, every character dropped that is not a letter, a mark, a digit, an
     * underscore, a hyphen or a space, then each space a hyphen - so "5. Crime & police" is 5-crime--police.
     * The contents and every in-page link use this one function.
     */
    static String slug(String heading) {
        StringBuilder sb = new StringBuilder();
        heading.toLowerCase(Locale.ROOT).codePoints().forEach(cp -> {
            int t = Character.getType(cp);
            if (cp == ' ') sb.append('-');
            else if (cp == '-' || Character.isLetter(cp) || t == Character.DECIMAL_DIGIT_NUMBER
                    || t == Character.CONNECTOR_PUNCTUATION || t == Character.NON_SPACING_MARK
                    || t == Character.COMBINING_SPACING_MARK || t == Character.ENCLOSING_MARK) sb.appendCodePoint(cp);
        });
        return sb.toString();
    }

    /** A note as a block quote: its label in bold on the first line, then its paragraphs. */
    void note(Node k, List<String> out) {
        List<String> inner = new ArrayList<>();
        Node label = null;
        for (Node c : flat(k)) if (!c.isText() && c.has("lbl")) { label = c; break; }
        if (label != null) paragraph(bold(label), inner);
        blocks(k, inner, label);
        if (inner.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        for (String line : String.join("\n\n", inner).split("\n", -1)) sb.append(line.isEmpty() ? ">" : "> " + line).append('\n');
        out.add(sb.substring(0, sb.length() - 1));
        notes++;
    }

    /** A formula as a fenced block, with its spacing as the page shows it - the page sets it white-space: pre. */
    void formula(Node k, List<String> out) {
        StringBuilder sb = new StringBuilder();
        pre(k, sb);
        List<String> lines = new ArrayList<>();
        for (String l : sb.toString().split("\n", -1)) lines.add(l.stripTrailing());
        while (!lines.isEmpty() && lines.get(0).isBlank()) lines.remove(0);
        while (!lines.isEmpty() && lines.get(lines.size() - 1).isBlank()) lines.remove(lines.size() - 1);
        if (lines.isEmpty()) return;
        String body = String.join("\n", lines), fence = "`".repeat(Math.max(3, longestRun(body, '`') + 1));
        out.add(fence + "\n" + body + "\n" + fence);
        formulas++;
    }

    /** Text as preformatted: whitespace kept, tags gone (a link is its words), a superscript ^, a <br> a new line. */
    static void pre(Node n, StringBuilder sb) {
        for (Node c : n.kids) {
            if (c.isText()) sb.append(c.text);
            else if (c.is("br")) sb.append('\n');
            else if (c.is("sup")) sb.append(sup(text(c)));
            else pre(c, sb);
        }
    }

    static int longestRun(String s, char c) {
        int best = 0, run = 0;
        for (int i = 0; i < s.length(); i++) {
            run = s.charAt(i) == c ? run + 1 : 0;
            best = Math.max(best, run);
        }
        return best;
    }

    /**
     * A list. An item's number is its navnum or step when it has one (the contents, the month), so the page's
     * number is the Markdown's and is not printed twice; each is checked against the heading it links to.
     */
    void list(Node k, List<String> out) {
        boolean ordered = k.is("ol"), tight = true;
        int next = 1;
        List<String> items = new ArrayList<>();
        for (Node li : flat(k)) {
            if (li.isText() && li.text.isBlank()) continue;
            List<String> body = new ArrayList<>();
            if (li.is("li")) blocks(li, body, null);
            else if (!li.isText() && BLOCKS.contains(li.tag)) block(li, body);
            else {                                          // text or an inline element loose in the list: an item of its own
                StringBuilder loose = new StringBuilder();
                inline(li, loose, Ctx.PLAIN);
                paragraph(loose, body);
            }
            if (body.size() > 1) tight = false;
            String num = number(listNumber(li));
            int value = num.matches("\\d{1,9}") ? Integer.parseInt(num) : next;
            next = value + 1;
            items.add(indent(ordered ? value + ". " : "- ", String.join("\n\n", body)));
            Node a = find(li, "a");
            if (ordered && !num.isEmpty() && a != null && a.attr("href").startsWith("#"))
                contents.add(new String[] { num, a.attr("href").substring(1), String.valueOf(a.line) });
        }
        if (k.has("turn")) steps += items.size();
        if (!items.isEmpty()) out.add(String.join(tight ? "\n" : "\n\n", items));
    }

    static String listNumber(Node n) {
        for (Node c : n.kids) {
            if (c.isText()) continue;
            if (c.is("span") && (c.has("navnum") || c.has("step"))) return text(c);
            String inner = listNumber(c);
            if (!inner.isEmpty()) return inner;
        }
        return "";
    }

    static String indent(String marker, String body) {
        if (body.isEmpty()) return marker.stripTrailing();
        String pad = " ".repeat(marker.length());
        String[] lines = body.split("\n", -1);
        StringBuilder sb = new StringBuilder(marker).append(lines[0]);
        for (int i = 1; i < lines.length; i++) sb.append('\n').append(lines[i].isEmpty() ? "" : pad + lines[i]);
        return sb.toString();
    }

    /** The vitals: a table of two rows, the terms over their values. */
    void vitals(Node dl, List<String> out) {
        List<String> terms = new ArrayList<>(), values = new ArrayList<>();
        terms(dl, terms, values);
        if (terms.isEmpty() && values.isEmpty()) return;
        out.add(gfmTable(List.of(terms, values), List.of()));
        tables++;
    }

    void terms(Node n, List<String> terms, List<String> values) {
        for (Node c : flat(n)) {
            if (c.isText()) continue;
            if (c.is("dt")) terms.add(cell(c));
            else if (c.is("dd")) values.add(cell(c));
            else if (!c.is("dl")) terms(c, terms, values);
        }
    }

    /** A table, its caption in italics above it; the head row is the thead's, or the first. */
    void table(Node t, List<String> out) {
        List<Node> rows = new ArrayList<>();
        Node header = null;
        for (Node c : flat(t)) {
            if (c.isText()) continue;
            if (c.is("caption")) paragraph(italic(c), out);
            else if (c.is("tr")) rows.add(c);
            else for (Node r : flat(c)) {
                if (!r.is("tr")) continue;
                rows.add(r);
                if (c.is("thead") && header == null) header = r;
            }
        }
        if (rows.isEmpty()) return;
        if (header == null) header = rows.get(0);
        rows.remove(header);
        rows.add(0, header);
        List<List<String>> cells = new ArrayList<>();
        List<Boolean> right = new ArrayList<>();
        for (Node r : rows) {
            List<String> row = new ArrayList<>();
            for (Node c : flat(r)) {
                if (c.isText() || !(c.is("th") || c.is("td"))) continue;
                if (c.attrs.containsKey("colspan") || c.attrs.containsKey("rowspan"))
                    warn("span " + c.line, c.line, "a spanning cell - GitHub tables have none, so the columns after it shift");
                row.add(cell(c));
                if (r == header) right.add(c.has("num"));
            }
            cells.add(row);
        }
        out.add(gfmTable(cells, right));
        tables++;
    }

    String cell(Node c) {
        StringBuilder sb = new StringBuilder();
        kids(c, sb, Ctx.PLAIN);
        return resolve(sb, Mode.CELL);
    }

    /** A GitHub table; a column is right-aligned where its head cell has class num. */
    static String gfmTable(List<List<String>> rows, List<Boolean> right) {
        int width = 0;
        for (List<String> r : rows) width = Math.max(width, r.size());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) sb.append('\n');
            sb.append('|');
            for (int c = 0; c < width; c++) sb.append(' ').append(c < rows.get(i).size() ? rows.get(i).get(c) : "").append(" |");
            if (i == 0) {
                sb.append("\n|");
                for (int c = 0; c < width; c++) sb.append(c < right.size() && right.get(c) ? " ---: |" : " --- |");
            }
        }
        return sb.toString();
    }

    /* ------------------------------------------------------------------
       THE MARKDOWN: INLINE TEXT
       ------------------------------------------------------------------ */

    /** Where inline text sits: in italics, in bold, in a link - and whether an <em> here can be set roman. */
    record Ctx(boolean italic, boolean bold, boolean link, boolean reversible) {
        static final Ctx PLAIN = new Ctx(false, false, false, false);
        Ctx em() { return new Ctx(true, bold, link, true); }
        Ctx roman() { return new Ctx(false, bold, link, false); }
        Ctx strong() { return new Ctx(italic, true, link, false); }
        Ctx inLink() { return new Ctx(italic, bold, true, false); }
    }

    void kids(Node n, StringBuilder sb, Ctx ctx) {
        for (Node c : n.kids) inline(c, sb, ctx);
    }

    /** One node as inline Markdown, with sentinels where the spelling waits on the neighbours. */
    void inline(Node n, StringBuilder sb, Ctx ctx) {
        if (n.isText()) { sb.append(n.text); return; }
        switch (n.tag) {
            case "strong", "b" -> {
                if (ctx.bold()) { kids(n, sb, ctx); break; }
                sb.append(STRONG_OPEN);
                kids(n, sb, ctx.strong());
                sb.append(STRONG_CLOSE);
            }
            case "em" -> {
                if (!ctx.italic()) {
                    sb.append(EM_OPEN);
                    kids(n, sb, ctx.em());
                    sb.append(EM_CLOSE);
                } else if (ctx.reversible()) {                  // emphasis inside an italic line is set roman
                    sb.append(EM_CLOSE);
                    kids(n, sb, ctx.roman());
                    sb.append(EM_OPEN);
                } else kids(n, sb, ctx);
            }
            case "code" -> code(text(n), sb);
            case "a" -> {
                String href = n.attr("href");
                if (href.isEmpty() || ctx.link()) { kids(n, sb, ctx); break; }
                sb.append(LINK_OPEN);
                kids(n, sb, ctx.inLink());
                sb.append(LINK_MID).append(target(href, n.line)).append(LINK_CLOSE);
            }
            case "sup" -> sb.append(sup(text(n)));
            case "br" -> sb.append(BREAK);
            case "span" -> {                                    // a list item's number is its marker, not its text
                if (!((n.has("navnum") || n.has("step")) && inside(n, "li"))) kids(n, sb, ctx);
            }
            case "head", "title", "link", "meta", "style" -> { }
            default -> {                                        // an unknown tag, or a block where only text can go
                boolean block = BLOCKS.contains(n.tag);
                if (block) sb.append(' ');
                kids(n, sb, ctx);
                if (block) sb.append(' ');
            }
        }
    }

    static boolean inside(Node n, String tag) {
        for (Node p = n.parent; p != null; p = p.parent) if (p.is(tag)) return true;
        return false;
    }

    /** The text of an element as it reads: tags gone, a superscript ^, a line break a space. */
    static String text(Node n) {
        if (n.isText()) return n.text;
        if (n.is("br")) return " ";
        if (n.is("style") || n.is("script")) return "";
        StringBuilder sb = new StringBuilder();
        for (Node c : n.kids) sb.append(c.is("sup") ? sup(text(c)) : text(c));
        return sb.toString();
    }

    static String collapse(String s) {
        return s.replaceAll("[ \\t\\n\\r\\f]+", " ");
    }

    static String sup(String s) {
        String t = collapse(s).strip();
        return t.contains(" ") ? "^(" + t + ")" : "^" + t;
    }

    /** A code span: fenced with more backticks than it holds, its outer spaces left outside it. */
    static void code(String raw, StringBuilder sb) {
        String c = collapse(raw), t = c.strip();
        if (t.isEmpty()) { sb.append(c); return; }
        String fence = "`".repeat(longestRun(t, '`') + 1), pad = t.startsWith("`") || t.endsWith("`") ? " " : "";
        if (c.startsWith(" ")) sb.append(' ');
        sb.append(CODE_OPEN).append(fence).append(pad).append(t).append(pad).append(fence).append(CODE_CLOSE);
        if (c.endsWith(" ")) sb.append(' ');
    }

    /** A link's target: an in-page #id waits for the anchor of the heading it names. */
    String target(String href, int line) {
        if (href.startsWith("#") && href.length() > 1) {
            linkLine.putIfAbsent(href.substring(1), line);
            return ANCHOR_OPEN + href.substring(1) + ANCHOR_CLOSE;
        }
        return href.matches(".*[\\s()<>].*") ? "<" + href.replace("<", "%3C").replace(">", "%3E") + ">" : href;
    }

    String anchors(String md) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (int a; (a = md.indexOf(ANCHOR_OPEN, i)) >= 0; ) {
            int b = md.indexOf(ANCHOR_CLOSE, a);
            String id = md.substring(a + 1, b), anchor = anchorOf.get(id);
            if (anchor == null) {
                warn("anchor " + id, linkLine.getOrDefault(id, 0), "a link to #" + id + ", which no heading carries - left as #" + id);
                anchor = id;
            }
            sb.append(md, i, a).append('#').append(anchor);
            i = b + 1;
        }
        return sb.append(md.substring(i)).toString();
    }

    /**
     * Inline text with its sentinels, as Markdown. HTML's whitespace is collapsed, spaces are moved outside
     * emphasis and links, plain characters are escaped where Markdown would misread them, and each emphasis
     * is spelled * or ** where GitHub will read it so, <em> or <strong> where it would not.
     */
    static String resolve(CharSequence raw, Mode mode) {
        String s = collapseOutsideCode(raw), was;
        do {
            was = s;
            s = s.replaceAll("([" + EM_OPEN + STRONG_OPEN + LINK_OPEN + "]) ", " $1")
                 .replaceAll(" ([" + EM_CLOSE + STRONG_CLOSE + "])", "$1 ")
                 .replaceAll(" (" + LINK_MID + "[^" + LINK_CLOSE + "]*" + LINK_CLOSE + ")", "$1 ")
                 .replace("  ", " ")
                 .replace("" + EM_OPEN + EM_CLOSE, "").replace("" + STRONG_OPEN + STRONG_CLOSE, "")
                 .replaceAll(EM_CLOSE + "( ?)" + EM_OPEN, "$1").replaceAll(STRONG_CLOSE + "( ?)" + STRONG_OPEN, "$1");
        } while (!s.equals(was));
        s = s.replaceAll(" ?" + BREAK + " ?", String.valueOf(BREAK)).strip();
        while (!s.isEmpty() && s.charAt(0) == BREAK) s = s.substring(1).strip();
        while (!s.isEmpty() && s.charAt(s.length() - 1) == BREAK) s = s.substring(0, s.length() - 1).strip();

        /* ----- escapes: a plain character Markdown would read as markup gets a backslash ----- */
        boolean dollars = mathPair(s), tildes = plainCount(s, '~') > 1, code = false, target = false;
        boolean lineStart = mode == Mode.BLOCK;
        int numberDot = -1;
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == CODE_OPEN) code = true;
            else if (c == CODE_CLOSE) code = false;
            else if (c == LINK_MID) target = true;
            else if (c == LINK_CLOSE) target = false;
            if (code || target || sentinel(c)) {
                out.append(c);
                lineStart = c == BREAK && mode == Mode.BLOCK;
                continue;
            }
            boolean first = lineStart;
            lineStart = false;
            if (first) numberDot = orderedMarker(s, i);
            boolean esc = switch (c) {
                case '\\', '`', '*', '[', ']' -> true;
                case '_' -> !Character.isLetterOrDigit(at(s, i - 1)) || !Character.isLetterOrDigit(at(s, i + 1));
                case '<' -> Character.isLetter(at(s, i + 1)) || "/!?".indexOf(at(s, i + 1)) >= 0;
                case '&' -> ENTITY_LIKE.matcher(s).region(i, s.length()).lookingAt();
                case '$' -> dollars;                            // GitHub reads $...$ as mathematics
                case '~' -> tildes;                             // and ~...~ as strikethrough
                case '!' -> at(s, i + 1) == LINK_OPEN;
                case '#' -> first || mode == Mode.HEADING && i == s.length() - 1;
                case '>', '-', '+', '=' -> first;
                default -> i == numberDot;                      // "1." at the start of a line would start a list
            };
            if (esc) out.append('\\');
            out.append(c);
        }

        /* ----- emphasis: pair the markers, then * and ** where GitHub reads them so, tags where it would not ----- */
        String e = out.toString();
        int[] mate = new int[e.length()];
        Arrays.fill(mate, -1);
        Deque<Integer> open = new ArrayDeque<>();
        for (int i = 0; i < e.length(); i++) {
            char c = e.charAt(i);
            if (c == EM_OPEN || c == STRONG_OPEN) open.push(i);
            else if ((c == EM_CLOSE || c == STRONG_CLOSE) && !open.isEmpty() && e.charAt(open.peek()) == c - 1) {
                int o = open.pop();
                mate[o] = i;
                mate[i] = o;
            }
        }
        boolean[] html = new boolean[e.length()];
        for (int o = 0; o < e.length(); o++) {
            int c = mate[o];
            if (c <= o) continue;
            boolean star = opens(before(e, o, mode), after(e, o, mode)) && closes(before(e, c, mode), after(e, c, mode))
                    && !emphasis(at(e, o - 1)) && !emphasis(at(e, c + 1));
            html[o] = html[c] = !star;
        }
        StringBuilder md = new StringBuilder(e.length() + 16);
        for (int i = 0; i < e.length(); i++) {
            char c = e.charAt(i);
            switch (c) {
                case EM_OPEN, EM_CLOSE, STRONG_OPEN, STRONG_CLOSE -> {
                    if (mate[i] < 0) break;                     // unpaired: dropped
                    boolean strong = c == STRONG_OPEN || c == STRONG_CLOSE, opening = c == EM_OPEN || c == STRONG_OPEN;
                    md.append(!html[i] ? (strong ? "**" : "*") : (opening ? "<" : "</") + (strong ? "strong>" : "em>"));
                }
                case CODE_OPEN, CODE_CLOSE -> { }
                case LINK_OPEN -> md.append('[');
                case LINK_MID -> md.append("](");
                case LINK_CLOSE -> md.append(')');
                case BREAK -> md.append(mode == Mode.BLOCK ? "  \n" : mode == Mode.CELL ? "<br>" : " ");
                default -> md.append(c);
            }
        }
        return mode == Mode.CELL ? md.toString().replace("|", "\\|") : md.toString();
    }

    /** Collapses runs of HTML whitespace to one space, leaving code spans (already collapsed) alone. */
    static String collapseOutsideCode(CharSequence raw) {
        StringBuilder sb = new StringBuilder(raw.length());
        boolean code = false, space = false;
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == CODE_OPEN) code = true;
            else if (c == CODE_CLOSE) code = false;
            if (!code && (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f')) {
                if (!space) sb.append(' ');
                space = true;
            } else {
                sb.append(c);
                space = false;
            }
        }
        return sb.toString();
    }

    static boolean sentinel(char c) {
        return c >= EM_OPEN && c <= ANCHOR_CLOSE;
    }

    static boolean emphasis(char c) {
        return c >= EM_OPEN && c <= STRONG_CLOSE;
    }

    static char at(String s, int i) {
        return i < 0 || i >= s.length() ? '\0' : s.charAt(i);
    }

    /** Whether a text could hold $...$ that GitHub would take for mathematics: an opening $, and a closing one after it. */
    static boolean mathPair(String s) {
        boolean opener = false, code = false, target = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == CODE_OPEN) code = true;
            else if (c == CODE_CLOSE) code = false;
            else if (c == LINK_MID) target = true;
            else if (c == LINK_CLOSE) target = false;
            if (c != '$' || code || target) continue;
            if (opener && i > 0 && s.charAt(i - 1) != ' ') return true;
            if (i + 1 < s.length() && s.charAt(i + 1) != ' ') opener = true;
        }
        return false;
    }

    static int plainCount(String s, char what) {
        int n = 0;
        boolean code = false, target = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == CODE_OPEN) code = true;
            else if (c == CODE_CLOSE) code = false;
            else if (c == LINK_MID) target = true;
            else if (c == LINK_CLOSE) target = false;
            else if (c == what && !code && !target) n++;
        }
        return n;
    }

    /** The index of the '.' or ')' in a line that opens "12." or "12)" and so would start an ordered list, or -1. */
    static int orderedMarker(String s, int i) {
        int j = i;
        while (j < s.length() && j - i < 10 && Character.isDigit(s.charAt(j))) j++;
        if (j == i || j - i > 9 || j >= s.length() || s.charAt(j) != '.' && s.charAt(j) != ')') return -1;
        char after = at(s, j + 1);
        return after == '\0' || after == ' ' || after == BREAK ? j : -1;
    }

    /* ----- CommonMark's flanking rules, read strictly: a symbol counts as punctuation when that makes a rule fail ----- */

    static boolean opens(char before, char after) {
        return !space(after) && (!maybePunct(after) || space(before) || surePunct(before));
    }

    static boolean closes(char before, char after) {
        return !space(before) && (!maybePunct(before) || space(after) || surePunct(after));
    }

    static boolean space(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\0' || Character.getType(c) == Character.SPACE_SEPARATOR;
    }

    static boolean surePunct(char c) {
        if (c < 128) return c > ' ' && c < 127 && !Character.isLetterOrDigit(c);
        int t = Character.getType(c);
        return t == Character.CONNECTOR_PUNCTUATION || t == Character.DASH_PUNCTUATION || t == Character.START_PUNCTUATION
                || t == Character.END_PUNCTUATION || t == Character.INITIAL_QUOTE_PUNCTUATION
                || t == Character.FINAL_QUOTE_PUNCTUATION || t == Character.OTHER_PUNCTUATION;
    }

    static boolean maybePunct(char c) {
        int t = Character.getType(c);
        return surePunct(c) || t == Character.MATH_SYMBOL || t == Character.CURRENCY_SYMBOL
                || t == Character.MODIFIER_SYMBOL || t == Character.OTHER_SYMBOL;
    }

    /** The character that will stand just before position i once the sentinels are spelled out. */
    static char before(String e, int i, Mode mode) {
        for (int j = i - 1; j >= 0; j--) {
            char c = e.charAt(j);
            if (c != CODE_OPEN && c != CODE_CLOSE) return spelled(c, mode, false);
        }
        return '\0';
    }

    /** The character that will stand just after position i once the sentinels are spelled out. */
    static char after(String e, int i, Mode mode) {
        for (int j = i + 1; j < e.length(); j++) {
            char c = e.charAt(j);
            if (c != CODE_OPEN && c != CODE_CLOSE) return spelled(c, mode, true);
        }
        return '\0';
    }

    static char spelled(char c, Mode mode, boolean first) {
        return switch (c) {
            case EM_OPEN, EM_CLOSE, STRONG_OPEN, STRONG_CLOSE -> '*';
            case LINK_OPEN -> '[';
            case LINK_MID -> first ? ']' : '(';
            case LINK_CLOSE -> ')';
            case BREAK -> mode == Mode.CELL ? (first ? '<' : '>') : ' ';
            default -> c;
        };
    }

    /** No trailing spaces except a deliberate line break, and one newline at the end. */
    static String tidy(String md) {
        String[] lines = md.split("\n", -1);
        StringBuilder sb = new StringBuilder(md.length());
        for (int i = 0; i < lines.length; i++) {
            String l = lines[i];
            boolean hardBreak = l.endsWith("  ") && !l.isBlank() && i + 1 < lines.length && !lines[i + 1].isBlank();
            sb.append(hardBreak ? l.stripTrailing() + "  " : l.stripTrailing()).append('\n');
        }
        return sb.toString().stripTrailing() + "\n";
    }

    /* ------------------------------------------------------------------
       THE NAMED CHARACTER REFERENCES
       ------------------------------------------------------------------ */

    static final Map<String, String> ENTITIES = entities();

    static Map<String, String> entities() {
        Map<String, String> m = new HashMap<>();
        String[] t = ENTITY_TABLE.trim().split("\\s+");
        for (int i = 0; i + 1 < t.length; i += 2) {
            StringBuilder v = new StringBuilder();
            for (String cp : t[i + 1].split("\\+")) v.appendCodePoint(Integer.parseInt(cp, 16));
            m.put(t[i], v.toString());
        }
        return m;
    }

    /**
     * Every named character reference HTML defines (the WHATWG list, html.spec.whatwg.org/entities.json),
     * the forms that end in a semicolon: the name, then its code point in hex, or two joined by a plus.
     */
    static final String ENTITY_TABLE = """
            AElig C6 AMP 26 Aacute C1 Abreve 102 Acirc C2 Acy 410 Afr 1D504 Agrave C0 Alpha 391 Amacr 100
            And 2A53 Aogon 104 Aopf 1D538 ApplyFunction 2061 Aring C5 Ascr 1D49C Assign 2254 Atilde C3
            Auml C4 Backslash 2216 Barv 2AE7 Barwed 2306 Bcy 411 Because 2235 Bernoullis 212C Beta 392
            Bfr 1D505 Bopf 1D539 Breve 2D8 Bscr 212C Bumpeq 224E CHcy 427 COPY A9 Cacute 106 Cap 22D2
            CapitalDifferentialD 2145 Cayleys 212D Ccaron 10C Ccedil C7 Ccirc 108 Cconint 2230 Cdot 10A
            Cedilla B8 CenterDot B7 Cfr 212D Chi 3A7 CircleDot 2299 CircleMinus 2296 CirclePlus 2295
            CircleTimes 2297 ClockwiseContourIntegral 2232 CloseCurlyDoubleQuote 201D CloseCurlyQuote 2019
            Colon 2237 Colone 2A74 Congruent 2261 Conint 222F ContourIntegral 222E Copf 2102 Coproduct 2210
            CounterClockwiseContourIntegral 2233 Cross 2A2F Cscr 1D49E Cup 22D3 CupCap 224D DD 2145
            DDotrahd 2911 DJcy 402 DScy 405 DZcy 40F Dagger 2021 Darr 21A1 Dashv 2AE4 Dcaron 10E Dcy 414
            Del 2207 Delta 394 Dfr 1D507 DiacriticalAcute B4 DiacriticalDot 2D9 DiacriticalDoubleAcute 2DD
            DiacriticalGrave 60 DiacriticalTilde 2DC Diamond 22C4 DifferentialD 2146 Dopf 1D53B Dot A8
            DotDot 20DC DotEqual 2250 DoubleContourIntegral 222F DoubleDot A8 DoubleDownArrow 21D3
            DoubleLeftArrow 21D0 DoubleLeftRightArrow 21D4 DoubleLeftTee 2AE4 DoubleLongLeftArrow 27F8
            DoubleLongLeftRightArrow 27FA DoubleLongRightArrow 27F9 DoubleRightArrow 21D2
            DoubleRightTee 22A8 DoubleUpArrow 21D1 DoubleUpDownArrow 21D5 DoubleVerticalBar 2225
            DownArrow 2193 DownArrowBar 2913 DownArrowUpArrow 21F5 DownBreve 311 DownLeftRightVector 2950
            DownLeftTeeVector 295E DownLeftVector 21BD DownLeftVectorBar 2956 DownRightTeeVector 295F
            DownRightVector 21C1 DownRightVectorBar 2957 DownTee 22A4 DownTeeArrow 21A7 Downarrow 21D3
            Dscr 1D49F Dstrok 110 ENG 14A ETH D0 Eacute C9 Ecaron 11A Ecirc CA Ecy 42D Edot 116 Efr 1D508
            Egrave C8 Element 2208 Emacr 112 EmptySmallSquare 25FB EmptyVerySmallSquare 25AB Eogon 118
            Eopf 1D53C Epsilon 395 Equal 2A75 EqualTilde 2242 Equilibrium 21CC Escr 2130 Esim 2A73 Eta 397
            Euml CB Exists 2203 ExponentialE 2147 Fcy 424 Ffr 1D509 FilledSmallSquare 25FC
            FilledVerySmallSquare 25AA Fopf 1D53D ForAll 2200 Fouriertrf 2131 Fscr 2131 GJcy 403 GT 3E
            Gamma 393 Gammad 3DC Gbreve 11E Gcedil 122 Gcirc 11C Gcy 413 Gdot 120 Gfr 1D50A Gg 22D9
            Gopf 1D53E GreaterEqual 2265 GreaterEqualLess 22DB GreaterFullEqual 2267 GreaterGreater 2AA2
            GreaterLess 2277 GreaterSlantEqual 2A7E GreaterTilde 2273 Gscr 1D4A2 Gt 226B HARDcy 42A
            Hacek 2C7 Hat 5E Hcirc 124 Hfr 210C HilbertSpace 210B Hopf 210D HorizontalLine 2500 Hscr 210B
            Hstrok 126 HumpDownHump 224E HumpEqual 224F IEcy 415 IJlig 132 IOcy 401 Iacute CD Icirc CE
            Icy 418 Idot 130 Ifr 2111 Igrave CC Im 2111 Imacr 12A ImaginaryI 2148 Implies 21D2 Int 222C
            Integral 222B Intersection 22C2 InvisibleComma 2063 InvisibleTimes 2062 Iogon 12E Iopf 1D540
            Iota 399 Iscr 2110 Itilde 128 Iukcy 406 Iuml CF Jcirc 134 Jcy 419 Jfr 1D50D Jopf 1D541
            Jscr 1D4A5 Jsercy 408 Jukcy 404 KHcy 425 KJcy 40C Kappa 39A Kcedil 136 Kcy 41A Kfr 1D50E
            Kopf 1D542 Kscr 1D4A6 LJcy 409 LT 3C Lacute 139 Lambda 39B Lang 27EA Laplacetrf 2112 Larr 219E
            Lcaron 13D Lcedil 13B Lcy 41B LeftAngleBracket 27E8 LeftArrow 2190 LeftArrowBar 21E4
            LeftArrowRightArrow 21C6 LeftCeiling 2308 LeftDoubleBracket 27E6 LeftDownTeeVector 2961
            LeftDownVector 21C3 LeftDownVectorBar 2959 LeftFloor 230A LeftRightArrow 2194
            LeftRightVector 294E LeftTee 22A3 LeftTeeArrow 21A4 LeftTeeVector 295A LeftTriangle 22B2
            LeftTriangleBar 29CF LeftTriangleEqual 22B4 LeftUpDownVector 2951 LeftUpTeeVector 2960
            LeftUpVector 21BF LeftUpVectorBar 2958 LeftVector 21BC LeftVectorBar 2952 Leftarrow 21D0
            Leftrightarrow 21D4 LessEqualGreater 22DA LessFullEqual 2266 LessGreater 2276 LessLess 2AA1
            LessSlantEqual 2A7D LessTilde 2272 Lfr 1D50F Ll 22D8 Lleftarrow 21DA Lmidot 13F
            LongLeftArrow 27F5 LongLeftRightArrow 27F7 LongRightArrow 27F6 Longleftarrow 27F8
            Longleftrightarrow 27FA Longrightarrow 27F9 Lopf 1D543 LowerLeftArrow 2199 LowerRightArrow 2198
            Lscr 2112 Lsh 21B0 Lstrok 141 Lt 226A Map 2905 Mcy 41C MediumSpace 205F Mellintrf 2133 Mfr 1D510
            MinusPlus 2213 Mopf 1D544 Mscr 2133 Mu 39C NJcy 40A Nacute 143 Ncaron 147 Ncedil 145 Ncy 41D
            NegativeMediumSpace 200B NegativeThickSpace 200B NegativeThinSpace 200B
            NegativeVeryThinSpace 200B NestedGreaterGreater 226B NestedLessLess 226A NewLine A Nfr 1D511
            NoBreak 2060 NonBreakingSpace A0 Nopf 2115 Not 2AEC NotCongruent 2262 NotCupCap 226D
            NotDoubleVerticalBar 2226 NotElement 2209 NotEqual 2260 NotEqualTilde 2242+338 NotExists 2204
            NotGreater 226F NotGreaterEqual 2271 NotGreaterFullEqual 2267+338 NotGreaterGreater 226B+338
            NotGreaterLess 2279 NotGreaterSlantEqual 2A7E+338 NotGreaterTilde 2275 NotHumpDownHump 224E+338
            NotHumpEqual 224F+338 NotLeftTriangle 22EA NotLeftTriangleBar 29CF+338 NotLeftTriangleEqual 22EC
            NotLess 226E NotLessEqual 2270 NotLessGreater 2278 NotLessLess 226A+338
            NotLessSlantEqual 2A7D+338 NotLessTilde 2274 NotNestedGreaterGreater 2AA2+338
            NotNestedLessLess 2AA1+338 NotPrecedes 2280 NotPrecedesEqual 2AAF+338 NotPrecedesSlantEqual 22E0
            NotReverseElement 220C NotRightTriangle 22EB NotRightTriangleBar 29D0+338
            NotRightTriangleEqual 22ED NotSquareSubset 228F+338 NotSquareSubsetEqual 22E2
            NotSquareSuperset 2290+338 NotSquareSupersetEqual 22E3 NotSubset 2282+20D2 NotSubsetEqual 2288
            NotSucceeds 2281 NotSucceedsEqual 2AB0+338 NotSucceedsSlantEqual 22E1 NotSucceedsTilde 227F+338
            NotSuperset 2283+20D2 NotSupersetEqual 2289 NotTilde 2241 NotTildeEqual 2244
            NotTildeFullEqual 2247 NotTildeTilde 2249 NotVerticalBar 2224 Nscr 1D4A9 Ntilde D1 Nu 39D
            OElig 152 Oacute D3 Ocirc D4 Ocy 41E Odblac 150 Ofr 1D512 Ograve D2 Omacr 14C Omega 3A9
            Omicron 39F Oopf 1D546 OpenCurlyDoubleQuote 201C OpenCurlyQuote 2018 Or 2A54 Oscr 1D4AA
            Oslash D8 Otilde D5 Otimes 2A37 Ouml D6 OverBar 203E OverBrace 23DE OverBracket 23B4
            OverParenthesis 23DC PartialD 2202 Pcy 41F Pfr 1D513 Phi 3A6 Pi 3A0 PlusMinus B1
            Poincareplane 210C Popf 2119 Pr 2ABB Precedes 227A PrecedesEqual 2AAF PrecedesSlantEqual 227C
            PrecedesTilde 227E Prime 2033 Product 220F Proportion 2237 Proportional 221D Pscr 1D4AB Psi 3A8
            QUOT 22 Qfr 1D514 Qopf 211A Qscr 1D4AC RBarr 2910 REG AE Racute 154 Rang 27EB Rarr 21A0
            Rarrtl 2916 Rcaron 158 Rcedil 156 Rcy 420 Re 211C ReverseElement 220B ReverseEquilibrium 21CB
            ReverseUpEquilibrium 296F Rfr 211C Rho 3A1 RightAngleBracket 27E9 RightArrow 2192
            RightArrowBar 21E5 RightArrowLeftArrow 21C4 RightCeiling 2309 RightDoubleBracket 27E7
            RightDownTeeVector 295D RightDownVector 21C2 RightDownVectorBar 2955 RightFloor 230B
            RightTee 22A2 RightTeeArrow 21A6 RightTeeVector 295B RightTriangle 22B3 RightTriangleBar 29D0
            RightTriangleEqual 22B5 RightUpDownVector 294F RightUpTeeVector 295C RightUpVector 21BE
            RightUpVectorBar 2954 RightVector 21C0 RightVectorBar 2953 Rightarrow 21D2 Ropf 211D
            RoundImplies 2970 Rrightarrow 21DB Rscr 211B Rsh 21B1 RuleDelayed 29F4 SHCHcy 429 SHcy 428
            SOFTcy 42C Sacute 15A Sc 2ABC Scaron 160 Scedil 15E Scirc 15C Scy 421 Sfr 1D516
            ShortDownArrow 2193 ShortLeftArrow 2190 ShortRightArrow 2192 ShortUpArrow 2191 Sigma 3A3
            SmallCircle 2218 Sopf 1D54A Sqrt 221A Square 25A1 SquareIntersection 2293 SquareSubset 228F
            SquareSubsetEqual 2291 SquareSuperset 2290 SquareSupersetEqual 2292 SquareUnion 2294 Sscr 1D4AE
            Star 22C6 Sub 22D0 Subset 22D0 SubsetEqual 2286 Succeeds 227B SucceedsEqual 2AB0
            SucceedsSlantEqual 227D SucceedsTilde 227F SuchThat 220B Sum 2211 Sup 22D1 Superset 2283
            SupersetEqual 2287 Supset 22D1 THORN DE TRADE 2122 TSHcy 40B TScy 426 Tab 9 Tau 3A4 Tcaron 164
            Tcedil 162 Tcy 422 Tfr 1D517 Therefore 2234 Theta 398 ThickSpace 205F+200A ThinSpace 2009
            Tilde 223C TildeEqual 2243 TildeFullEqual 2245 TildeTilde 2248 Topf 1D54B TripleDot 20DB
            Tscr 1D4AF Tstrok 166 Uacute DA Uarr 219F Uarrocir 2949 Ubrcy 40E Ubreve 16C Ucirc DB Ucy 423
            Udblac 170 Ufr 1D518 Ugrave D9 Umacr 16A UnderBar 5F UnderBrace 23DF UnderBracket 23B5
            UnderParenthesis 23DD Union 22C3 UnionPlus 228E Uogon 172 Uopf 1D54C UpArrow 2191
            UpArrowBar 2912 UpArrowDownArrow 21C5 UpDownArrow 2195 UpEquilibrium 296E UpTee 22A5
            UpTeeArrow 21A5 Uparrow 21D1 Updownarrow 21D5 UpperLeftArrow 2196 UpperRightArrow 2197 Upsi 3D2
            Upsilon 3A5 Uring 16E Uscr 1D4B0 Utilde 168 Uuml DC VDash 22AB Vbar 2AEB Vcy 412 Vdash 22A9
            Vdashl 2AE6 Vee 22C1 Verbar 2016 Vert 2016 VerticalBar 2223 VerticalLine 7C
            VerticalSeparator 2758 VerticalTilde 2240 VeryThinSpace 200A Vfr 1D519 Vopf 1D54D Vscr 1D4B1
            Vvdash 22AA Wcirc 174 Wedge 22C0 Wfr 1D51A Wopf 1D54E Wscr 1D4B2 Xfr 1D51B Xi 39E Xopf 1D54F
            Xscr 1D4B3 YAcy 42F YIcy 407 YUcy 42E Yacute DD Ycirc 176 Ycy 42B Yfr 1D51C Yopf 1D550
            Yscr 1D4B4 Yuml 178 ZHcy 416 Zacute 179 Zcaron 17D Zcy 417 Zdot 17B ZeroWidthSpace 200B Zeta 396
            Zfr 2128 Zopf 2124 Zscr 1D4B5 aacute E1 abreve 103 ac 223E acE 223E+333 acd 223F acirc E2
            acute B4 acy 430 aelig E6 af 2061 afr 1D51E agrave E0 alefsym 2135 aleph 2135 alpha 3B1
            amacr 101 amalg 2A3F amp 26 and 2227 andand 2A55 andd 2A5C andslope 2A58 andv 2A5A ang 2220
            ange 29A4 angle 2220 angmsd 2221 angmsdaa 29A8 angmsdab 29A9 angmsdac 29AA angmsdad 29AB
            angmsdae 29AC angmsdaf 29AD angmsdag 29AE angmsdah 29AF angrt 221F angrtvb 22BE angrtvbd 299D
            angsph 2222 angst C5 angzarr 237C aogon 105 aopf 1D552 ap 2248 apE 2A70 apacir 2A6F ape 224A
            apid 224B apos 27 approx 2248 approxeq 224A aring E5 ascr 1D4B6 ast 2A asymp 2248 asympeq 224D
            atilde E3 auml E4 awconint 2233 awint 2A11 bNot 2AED backcong 224C backepsilon 3F6
            backprime 2035 backsim 223D backsimeq 22CD barvee 22BD barwed 2305 barwedge 2305 bbrk 23B5
            bbrktbrk 23B6 bcong 224C bcy 431 bdquo 201E becaus 2235 because 2235 bemptyv 29B0 bepsi 3F6
            bernou 212C beta 3B2 beth 2136 between 226C bfr 1D51F bigcap 22C2 bigcirc 25EF bigcup 22C3
            bigodot 2A00 bigoplus 2A01 bigotimes 2A02 bigsqcup 2A06 bigstar 2605 bigtriangledown 25BD
            bigtriangleup 25B3 biguplus 2A04 bigvee 22C1 bigwedge 22C0 bkarow 290D blacklozenge 29EB
            blacksquare 25AA blacktriangle 25B4 blacktriangledown 25BE blacktriangleleft 25C2
            blacktriangleright 25B8 blank 2423 blk12 2592 blk14 2591 blk34 2593 block 2588 bne 3D+20E5
            bnequiv 2261+20E5 bnot 2310 bopf 1D553 bot 22A5 bottom 22A5 bowtie 22C8 boxDL 2557 boxDR 2554
            boxDl 2556 boxDr 2553 boxH 2550 boxHD 2566 boxHU 2569 boxHd 2564 boxHu 2567 boxUL 255D
            boxUR 255A boxUl 255C boxUr 2559 boxV 2551 boxVH 256C boxVL 2563 boxVR 2560 boxVh 256B
            boxVl 2562 boxVr 255F boxbox 29C9 boxdL 2555 boxdR 2552 boxdl 2510 boxdr 250C boxh 2500
            boxhD 2565 boxhU 2568 boxhd 252C boxhu 2534 boxminus 229F boxplus 229E boxtimes 22A0 boxuL 255B
            boxuR 2558 boxul 2518 boxur 2514 boxv 2502 boxvH 256A boxvL 2561 boxvR 255E boxvh 253C
            boxvl 2524 boxvr 251C bprime 2035 breve 2D8 brvbar A6 bscr 1D4B7 bsemi 204F bsim 223D bsime 22CD
            bsol 5C bsolb 29C5 bsolhsub 27C8 bull 2022 bullet 2022 bump 224E bumpE 2AAE bumpe 224F
            bumpeq 224F cacute 107 cap 2229 capand 2A44 capbrcup 2A49 capcap 2A4B capcup 2A47 capdot 2A40
            caps 2229+FE00 caret 2041 caron 2C7 ccaps 2A4D ccaron 10D ccedil E7 ccirc 109 ccups 2A4C
            ccupssm 2A50 cdot 10B cedil B8 cemptyv 29B2 cent A2 centerdot B7 cfr 1D520 chcy 447 check 2713
            checkmark 2713 chi 3C7 cir 25CB cirE 29C3 circ 2C6 circeq 2257 circlearrowleft 21BA
            circlearrowright 21BB circledR AE circledS 24C8 circledast 229B circledcirc 229A
            circleddash 229D cire 2257 cirfnint 2A10 cirmid 2AEF cirscir 29C2 clubs 2663 clubsuit 2663
            colon 3A colone 2254 coloneq 2254 comma 2C commat 40 comp 2201 compfn 2218 complement 2201
            complexes 2102 cong 2245 congdot 2A6D conint 222E copf 1D554 coprod 2210 copy A9 copysr 2117
            crarr 21B5 cross 2717 cscr 1D4B8 csub 2ACF csube 2AD1 csup 2AD0 csupe 2AD2 ctdot 22EF
            cudarrl 2938 cudarrr 2935 cuepr 22DE cuesc 22DF cularr 21B6 cularrp 293D cup 222A cupbrcap 2A48
            cupcap 2A46 cupcup 2A4A cupdot 228D cupor 2A45 cups 222A+FE00 curarr 21B7 curarrm 293C
            curlyeqprec 22DE curlyeqsucc 22DF curlyvee 22CE curlywedge 22CF curren A4 curvearrowleft 21B6
            curvearrowright 21B7 cuvee 22CE cuwed 22CF cwconint 2232 cwint 2231 cylcty 232D dArr 21D3
            dHar 2965 dagger 2020 daleth 2138 darr 2193 dash 2010 dashv 22A3 dbkarow 290F dblac 2DD
            dcaron 10F dcy 434 dd 2146 ddagger 2021 ddarr 21CA ddotseq 2A77 deg B0 delta 3B4 demptyv 29B1
            dfisht 297F dfr 1D521 dharl 21C3 dharr 21C2 diam 22C4 diamond 22C4 diamondsuit 2666 diams 2666
            die A8 digamma 3DD disin 22F2 div F7 divide F7 divideontimes 22C7 divonx 22C7 djcy 452
            dlcorn 231E dlcrop 230D dollar 24 dopf 1D555 dot 2D9 doteq 2250 doteqdot 2251 dotminus 2238
            dotplus 2214 dotsquare 22A1 doublebarwedge 2306 downarrow 2193 downdownarrows 21CA
            downharpoonleft 21C3 downharpoonright 21C2 drbkarow 2910 drcorn 231F drcrop 230C dscr 1D4B9
            dscy 455 dsol 29F6 dstrok 111 dtdot 22F1 dtri 25BF dtrif 25BE duarr 21F5 duhar 296F dwangle 29A6
            dzcy 45F dzigrarr 27FF eDDot 2A77 eDot 2251 eacute E9 easter 2A6E ecaron 11B ecir 2256 ecirc EA
            ecolon 2255 ecy 44D edot 117 ee 2147 efDot 2252 efr 1D522 eg 2A9A egrave E8 egs 2A96 egsdot 2A98
            el 2A99 elinters 23E7 ell 2113 els 2A95 elsdot 2A97 emacr 113 empty 2205 emptyset 2205
            emptyv 2205 emsp 2003 emsp13 2004 emsp14 2005 eng 14B ensp 2002 eogon 119 eopf 1D556 epar 22D5
            eparsl 29E3 eplus 2A71 epsi 3B5 epsilon 3B5 epsiv 3F5 eqcirc 2256 eqcolon 2255 eqsim 2242
            eqslantgtr 2A96 eqslantless 2A95 equals 3D equest 225F equiv 2261 equivDD 2A78 eqvparsl 29E5
            erDot 2253 erarr 2971 escr 212F esdot 2250 esim 2242 eta 3B7 eth F0 euml EB euro 20AC excl 21
            exist 2203 expectation 2130 exponentiale 2147 fallingdotseq 2252 fcy 444 female 2640 ffilig FB03
            fflig FB00 ffllig FB04 ffr 1D523 filig FB01 fjlig 66+6A flat 266D fllig FB02 fltns 25B1 fnof 192
            fopf 1D557 forall 2200 fork 22D4 forkv 2AD9 fpartint 2A0D frac12 BD frac13 2153 frac14 BC
            frac15 2155 frac16 2159 frac18 215B frac23 2154 frac25 2156 frac34 BE frac35 2157 frac38 215C
            frac45 2158 frac56 215A frac58 215D frac78 215E frasl 2044 frown 2322 fscr 1D4BB gE 2267
            gEl 2A8C gacute 1F5 gamma 3B3 gammad 3DD gap 2A86 gbreve 11F gcirc 11D gcy 433 gdot 121 ge 2265
            gel 22DB geq 2265 geqq 2267 geqslant 2A7E ges 2A7E gescc 2AA9 gesdot 2A80 gesdoto 2A82
            gesdotol 2A84 gesl 22DB+FE00 gesles 2A94 gfr 1D524 gg 226B ggg 22D9 gimel 2137 gjcy 453 gl 2277
            glE 2A92 gla 2AA5 glj 2AA4 gnE 2269 gnap 2A8A gnapprox 2A8A gne 2A88 gneq 2A88 gneqq 2269
            gnsim 22E7 gopf 1D558 grave 60 gscr 210A gsim 2273 gsime 2A8E gsiml 2A90 gt 3E gtcc 2AA7
            gtcir 2A7A gtdot 22D7 gtlPar 2995 gtquest 2A7C gtrapprox 2A86 gtrarr 2978 gtrdot 22D7
            gtreqless 22DB gtreqqless 2A8C gtrless 2277 gtrsim 2273 gvertneqq 2269+FE00 gvnE 2269+FE00
            hArr 21D4 hairsp 200A half BD hamilt 210B hardcy 44A harr 2194 harrcir 2948 harrw 21AD hbar 210F
            hcirc 125 hearts 2665 heartsuit 2665 hellip 2026 hercon 22B9 hfr 1D525 hksearow 2925
            hkswarow 2926 hoarr 21FF homtht 223B hookleftarrow 21A9 hookrightarrow 21AA hopf 1D559
            horbar 2015 hscr 1D4BD hslash 210F hstrok 127 hybull 2043 hyphen 2010 iacute ED ic 2063 icirc EE
            icy 438 iecy 435 iexcl A1 iff 21D4 ifr 1D526 igrave EC ii 2148 iiiint 2A0C iiint 222D
            iinfin 29DC iiota 2129 ijlig 133 imacr 12B image 2111 imagline 2110 imagpart 2111 imath 131
            imof 22B7 imped 1B5 in 2208 incare 2105 infin 221E infintie 29DD inodot 131 int 222B intcal 22BA
            integers 2124 intercal 22BA intlarhk 2A17 intprod 2A3C iocy 451 iogon 12F iopf 1D55A iota 3B9
            iprod 2A3C iquest BF iscr 1D4BE isin 2208 isinE 22F9 isindot 22F5 isins 22F4 isinsv 22F3
            isinv 2208 it 2062 itilde 129 iukcy 456 iuml EF jcirc 135 jcy 439 jfr 1D527 jmath 237 jopf 1D55B
            jscr 1D4BF jsercy 458 jukcy 454 kappa 3BA kappav 3F0 kcedil 137 kcy 43A kfr 1D528 kgreen 138
            khcy 445 kjcy 45C kopf 1D55C kscr 1D4C0 lAarr 21DA lArr 21D0 lAtail 291B lBarr 290E lE 2266
            lEg 2A8B lHar 2962 lacute 13A laemptyv 29B4 lagran 2112 lambda 3BB lang 27E8 langd 2991
            langle 27E8 lap 2A85 laquo AB larr 2190 larrb 21E4 larrbfs 291F larrfs 291D larrhk 21A9
            larrlp 21AB larrpl 2939 larrsim 2973 larrtl 21A2 lat 2AAB latail 2919 late 2AAD lates 2AAD+FE00
            lbarr 290C lbbrk 2772 lbrace 7B lbrack 5B lbrke 298B lbrksld 298F lbrkslu 298D lcaron 13E
            lcedil 13C lceil 2308 lcub 7B lcy 43B ldca 2936 ldquo 201C ldquor 201E ldrdhar 2967
            ldrushar 294B ldsh 21B2 le 2264 leftarrow 2190 leftarrowtail 21A2 leftharpoondown 21BD
            leftharpoonup 21BC leftleftarrows 21C7 leftrightarrow 2194 leftrightarrows 21C6
            leftrightharpoons 21CB leftrightsquigarrow 21AD leftthreetimes 22CB leg 22DA leq 2264 leqq 2266
            leqslant 2A7D les 2A7D lescc 2AA8 lesdot 2A7F lesdoto 2A81 lesdotor 2A83 lesg 22DA+FE00
            lesges 2A93 lessapprox 2A85 lessdot 22D6 lesseqgtr 22DA lesseqqgtr 2A8B lessgtr 2276
            lesssim 2272 lfisht 297C lfloor 230A lfr 1D529 lg 2276 lgE 2A91 lhard 21BD lharu 21BC
            lharul 296A lhblk 2584 ljcy 459 ll 226A llarr 21C7 llcorner 231E llhard 296B lltri 25FA
            lmidot 140 lmoust 23B0 lmoustache 23B0 lnE 2268 lnap 2A89 lnapprox 2A89 lne 2A87 lneq 2A87
            lneqq 2268 lnsim 22E6 loang 27EC loarr 21FD lobrk 27E6 longleftarrow 27F5
            longleftrightarrow 27F7 longmapsto 27FC longrightarrow 27F6 looparrowleft 21AB
            looparrowright 21AC lopar 2985 lopf 1D55D loplus 2A2D lotimes 2A34 lowast 2217 lowbar 5F
            loz 25CA lozenge 25CA lozf 29EB lpar 28 lparlt 2993 lrarr 21C6 lrcorner 231F lrhar 21CB
            lrhard 296D lrm 200E lrtri 22BF lsaquo 2039 lscr 1D4C1 lsh 21B0 lsim 2272 lsime 2A8D lsimg 2A8F
            lsqb 5B lsquo 2018 lsquor 201A lstrok 142 lt 3C ltcc 2AA6 ltcir 2A79 ltdot 22D6 lthree 22CB
            ltimes 22C9 ltlarr 2976 ltquest 2A7B ltrPar 2996 ltri 25C3 ltrie 22B4 ltrif 25C2 lurdshar 294A
            luruhar 2966 lvertneqq 2268+FE00 lvnE 2268+FE00 mDDot 223A macr AF male 2642 malt 2720
            maltese 2720 map 21A6 mapsto 21A6 mapstodown 21A7 mapstoleft 21A4 mapstoup 21A5 marker 25AE
            mcomma 2A29 mcy 43C mdash 2014 measuredangle 2221 mfr 1D52A mho 2127 micro B5 mid 2223 midast 2A
            midcir 2AF0 middot B7 minus 2212 minusb 229F minusd 2238 minusdu 2A2A mlcp 2ADB mldr 2026
            mnplus 2213 models 22A7 mopf 1D55E mp 2213 mscr 1D4C2 mstpos 223E mu 3BC multimap 22B8
            mumap 22B8 nGg 22D9+338 nGt 226B+20D2 nGtv 226B+338 nLeftarrow 21CD nLeftrightarrow 21CE
            nLl 22D8+338 nLt 226A+20D2 nLtv 226A+338 nRightarrow 21CF nVDash 22AF nVdash 22AE nabla 2207
            nacute 144 nang 2220+20D2 nap 2249 napE 2A70+338 napid 224B+338 napos 149 napprox 2249
            natur 266E natural 266E naturals 2115 nbsp A0 nbump 224E+338 nbumpe 224F+338 ncap 2A43
            ncaron 148 ncedil 146 ncong 2247 ncongdot 2A6D+338 ncup 2A42 ncy 43D ndash 2013 ne 2260
            neArr 21D7 nearhk 2924 nearr 2197 nearrow 2197 nedot 2250+338 nequiv 2262 nesear 2928
            nesim 2242+338 nexist 2204 nexists 2204 nfr 1D52B ngE 2267+338 nge 2271 ngeq 2271 ngeqq 2267+338
            ngeqslant 2A7E+338 nges 2A7E+338 ngsim 2275 ngt 226F ngtr 226F nhArr 21CE nharr 21AE nhpar 2AF2
            ni 220B nis 22FC nisd 22FA niv 220B njcy 45A nlArr 21CD nlE 2266+338 nlarr 219A nldr 2025
            nle 2270 nleftarrow 219A nleftrightarrow 21AE nleq 2270 nleqq 2266+338 nleqslant 2A7D+338
            nles 2A7D+338 nless 226E nlsim 2274 nlt 226E nltri 22EA nltrie 22EC nmid 2224 nopf 1D55F not AC
            notin 2209 notinE 22F9+338 notindot 22F5+338 notinva 2209 notinvb 22F7 notinvc 22F6 notni 220C
            notniva 220C notnivb 22FE notnivc 22FD npar 2226 nparallel 2226 nparsl 2AFD+20E5 npart 2202+338
            npolint 2A14 npr 2280 nprcue 22E0 npre 2AAF+338 nprec 2280 npreceq 2AAF+338 nrArr 21CF
            nrarr 219B nrarrc 2933+338 nrarrw 219D+338 nrightarrow 219B nrtri 22EB nrtrie 22ED nsc 2281
            nsccue 22E1 nsce 2AB0+338 nscr 1D4C3 nshortmid 2224 nshortparallel 2226 nsim 2241 nsime 2244
            nsimeq 2244 nsmid 2224 nspar 2226 nsqsube 22E2 nsqsupe 22E3 nsub 2284 nsubE 2AC5+338 nsube 2288
            nsubset 2282+20D2 nsubseteq 2288 nsubseteqq 2AC5+338 nsucc 2281 nsucceq 2AB0+338 nsup 2285
            nsupE 2AC6+338 nsupe 2289 nsupset 2283+20D2 nsupseteq 2289 nsupseteqq 2AC6+338 ntgl 2279
            ntilde F1 ntlg 2278 ntriangleleft 22EA ntrianglelefteq 22EC ntriangleright 22EB
            ntrianglerighteq 22ED nu 3BD num 23 numero 2116 numsp 2007 nvDash 22AD nvHarr 2904
            nvap 224D+20D2 nvdash 22AC nvge 2265+20D2 nvgt 3E+20D2 nvinfin 29DE nvlArr 2902 nvle 2264+20D2
            nvlt 3C+20D2 nvltrie 22B4+20D2 nvrArr 2903 nvrtrie 22B5+20D2 nvsim 223C+20D2 nwArr 21D6
            nwarhk 2923 nwarr 2196 nwarrow 2196 nwnear 2927 oS 24C8 oacute F3 oast 229B ocir 229A ocirc F4
            ocy 43E odash 229D odblac 151 odiv 2A38 odot 2299 odsold 29BC oelig 153 ofcir 29BF ofr 1D52C
            ogon 2DB ograve F2 ogt 29C1 ohbar 29B5 ohm 3A9 oint 222E olarr 21BA olcir 29BE olcross 29BB
            oline 203E olt 29C0 omacr 14D omega 3C9 omicron 3BF omid 29B6 ominus 2296 oopf 1D560 opar 29B7
            operp 29B9 oplus 2295 or 2228 orarr 21BB ord 2A5D order 2134 orderof 2134 ordf AA ordm BA
            origof 22B6 oror 2A56 orslope 2A57 orv 2A5B oscr 2134 oslash F8 osol 2298 otilde F5 otimes 2297
            otimesas 2A36 ouml F6 ovbar 233D par 2225 para B6 parallel 2225 parsim 2AF3 parsl 2AFD part 2202
            pcy 43F percnt 25 period 2E permil 2030 perp 22A5 pertenk 2031 pfr 1D52D phi 3C6 phiv 3D5
            phmmat 2133 phone 260E pi 3C0 pitchfork 22D4 piv 3D6 planck 210F planckh 210E plankv 210F
            plus 2B plusacir 2A23 plusb 229E pluscir 2A22 plusdo 2214 plusdu 2A25 pluse 2A72 plusmn B1
            plussim 2A26 plustwo 2A27 pm B1 pointint 2A15 popf 1D561 pound A3 pr 227A prE 2AB3 prap 2AB7
            prcue 227C pre 2AAF prec 227A precapprox 2AB7 preccurlyeq 227C preceq 2AAF precnapprox 2AB9
            precneqq 2AB5 precnsim 22E8 precsim 227E prime 2032 primes 2119 prnE 2AB5 prnap 2AB9 prnsim 22E8
            prod 220F profalar 232E profline 2312 profsurf 2313 prop 221D propto 221D prsim 227E prurel 22B0
            pscr 1D4C5 psi 3C8 puncsp 2008 qfr 1D52E qint 2A0C qopf 1D562 qprime 2057 qscr 1D4C6
            quaternions 210D quatint 2A16 quest 3F questeq 225F quot 22 rAarr 21DB rArr 21D2 rAtail 291C
            rBarr 290F rHar 2964 race 223D+331 racute 155 radic 221A raemptyv 29B3 rang 27E9 rangd 2992
            range 29A5 rangle 27E9 raquo BB rarr 2192 rarrap 2975 rarrb 21E5 rarrbfs 2920 rarrc 2933
            rarrfs 291E rarrhk 21AA rarrlp 21AC rarrpl 2945 rarrsim 2974 rarrtl 21A3 rarrw 219D ratail 291A
            ratio 2236 rationals 211A rbarr 290D rbbrk 2773 rbrace 7D rbrack 5D rbrke 298C rbrksld 298E
            rbrkslu 2990 rcaron 159 rcedil 157 rceil 2309 rcub 7D rcy 440 rdca 2937 rdldhar 2969 rdquo 201D
            rdquor 201D rdsh 21B3 real 211C realine 211B realpart 211C reals 211D rect 25AD reg AE
            rfisht 297D rfloor 230B rfr 1D52F rhard 21C1 rharu 21C0 rharul 296C rho 3C1 rhov 3F1
            rightarrow 2192 rightarrowtail 21A3 rightharpoondown 21C1 rightharpoonup 21C0
            rightleftarrows 21C4 rightleftharpoons 21CC rightrightarrows 21C9 rightsquigarrow 219D
            rightthreetimes 22CC ring 2DA risingdotseq 2253 rlarr 21C4 rlhar 21CC rlm 200F rmoust 23B1
            rmoustache 23B1 rnmid 2AEE roang 27ED roarr 21FE robrk 27E7 ropar 2986 ropf 1D563 roplus 2A2E
            rotimes 2A35 rpar 29 rpargt 2994 rppolint 2A12 rrarr 21C9 rsaquo 203A rscr 1D4C7 rsh 21B1
            rsqb 5D rsquo 2019 rsquor 2019 rthree 22CC rtimes 22CA rtri 25B9 rtrie 22B5 rtrif 25B8
            rtriltri 29CE ruluhar 2968 rx 211E sacute 15B sbquo 201A sc 227B scE 2AB4 scap 2AB8 scaron 161
            sccue 227D sce 2AB0 scedil 15F scirc 15D scnE 2AB6 scnap 2ABA scnsim 22E9 scpolint 2A13
            scsim 227F scy 441 sdot 22C5 sdotb 22A1 sdote 2A66 seArr 21D8 searhk 2925 searr 2198
            searrow 2198 sect A7 semi 3B seswar 2929 setminus 2216 setmn 2216 sext 2736 sfr 1D530
            sfrown 2322 sharp 266F shchcy 449 shcy 448 shortmid 2223 shortparallel 2225 shy AD sigma 3C3
            sigmaf 3C2 sigmav 3C2 sim 223C simdot 2A6A sime 2243 simeq 2243 simg 2A9E simgE 2AA0 siml 2A9D
            simlE 2A9F simne 2246 simplus 2A24 simrarr 2972 slarr 2190 smallsetminus 2216 smashp 2A33
            smeparsl 29E4 smid 2223 smile 2323 smt 2AAA smte 2AAC smtes 2AAC+FE00 softcy 44C sol 2F
            solb 29C4 solbar 233F sopf 1D564 spades 2660 spadesuit 2660 spar 2225 sqcap 2293
            sqcaps 2293+FE00 sqcup 2294 sqcups 2294+FE00 sqsub 228F sqsube 2291 sqsubset 228F
            sqsubseteq 2291 sqsup 2290 sqsupe 2292 sqsupset 2290 sqsupseteq 2292 squ 25A1 square 25A1
            squarf 25AA squf 25AA srarr 2192 sscr 1D4C8 ssetmn 2216 ssmile 2323 sstarf 22C6 star 2606
            starf 2605 straightepsilon 3F5 straightphi 3D5 strns AF sub 2282 subE 2AC5 subdot 2ABD sube 2286
            subedot 2AC3 submult 2AC1 subnE 2ACB subne 228A subplus 2ABF subrarr 2979 subset 2282
            subseteq 2286 subseteqq 2AC5 subsetneq 228A subsetneqq 2ACB subsim 2AC7 subsub 2AD5 subsup 2AD3
            succ 227B succapprox 2AB8 succcurlyeq 227D succeq 2AB0 succnapprox 2ABA succneqq 2AB6
            succnsim 22E9 succsim 227F sum 2211 sung 266A sup 2283 sup1 B9 sup2 B2 sup3 B3 supE 2AC6
            supdot 2ABE supdsub 2AD8 supe 2287 supedot 2AC4 suphsol 27C9 suphsub 2AD7 suplarr 297B
            supmult 2AC2 supnE 2ACC supne 228B supplus 2AC0 supset 2283 supseteq 2287 supseteqq 2AC6
            supsetneq 228B supsetneqq 2ACC supsim 2AC8 supsub 2AD4 supsup 2AD6 swArr 21D9 swarhk 2926
            swarr 2199 swarrow 2199 swnwar 292A szlig DF target 2316 tau 3C4 tbrk 23B4 tcaron 165 tcedil 163
            tcy 442 tdot 20DB telrec 2315 tfr 1D531 there4 2234 therefore 2234 theta 3B8 thetasym 3D1
            thetav 3D1 thickapprox 2248 thicksim 223C thinsp 2009 thkap 2248 thksim 223C thorn FE tilde 2DC
            times D7 timesb 22A0 timesbar 2A31 timesd 2A30 tint 222D toea 2928 top 22A4 topbot 2336
            topcir 2AF1 topf 1D565 topfork 2ADA tosa 2929 tprime 2034 trade 2122 triangle 25B5
            triangledown 25BF triangleleft 25C3 trianglelefteq 22B4 triangleq 225C triangleright 25B9
            trianglerighteq 22B5 tridot 25EC trie 225C triminus 2A3A triplus 2A39 trisb 29CD tritime 2A3B
            trpezium 23E2 tscr 1D4C9 tscy 446 tshcy 45B tstrok 167 twixt 226C twoheadleftarrow 219E
            twoheadrightarrow 21A0 uArr 21D1 uHar 2963 uacute FA uarr 2191 ubrcy 45E ubreve 16D ucirc FB
            ucy 443 udarr 21C5 udblac 171 udhar 296E ufisht 297E ufr 1D532 ugrave F9 uharl 21BF uharr 21BE
            uhblk 2580 ulcorn 231C ulcorner 231C ulcrop 230F ultri 25F8 umacr 16B uml A8 uogon 173
            uopf 1D566 uparrow 2191 updownarrow 2195 upharpoonleft 21BF upharpoonright 21BE uplus 228E
            upsi 3C5 upsih 3D2 upsilon 3C5 upuparrows 21C8 urcorn 231D urcorner 231D urcrop 230E uring 16F
            urtri 25F9 uscr 1D4CA utdot 22F0 utilde 169 utri 25B5 utrif 25B4 uuarr 21C8 uuml FC uwangle 29A7
            vArr 21D5 vBar 2AE8 vBarv 2AE9 vDash 22A8 vangrt 299C varepsilon 3F5 varkappa 3F0
            varnothing 2205 varphi 3D5 varpi 3D6 varpropto 221D varr 2195 varrho 3F1 varsigma 3C2
            varsubsetneq 228A+FE00 varsubsetneqq 2ACB+FE00 varsupsetneq 228B+FE00 varsupsetneqq 2ACC+FE00
            vartheta 3D1 vartriangleleft 22B2 vartriangleright 22B3 vcy 432 vdash 22A2 vee 2228 veebar 22BB
            veeeq 225A vellip 22EE verbar 7C vert 7C vfr 1D533 vltri 22B2 vnsub 2282+20D2 vnsup 2283+20D2
            vopf 1D567 vprop 221D vrtri 22B3 vscr 1D4CB vsubnE 2ACB+FE00 vsubne 228A+FE00 vsupnE 2ACC+FE00
            vsupne 228B+FE00 vzigzag 299A wcirc 175 wedbar 2A5F wedge 2227 wedgeq 2259 weierp 2118 wfr 1D534
            wopf 1D568 wp 2118 wr 2240 wreath 2240 wscr 1D4CC xcap 22C2 xcirc 25EF xcup 22C3 xdtri 25BD
            xfr 1D535 xhArr 27FA xharr 27F7 xi 3BE xlArr 27F8 xlarr 27F5 xmap 27FC xnis 22FB xodot 2A00
            xopf 1D569 xoplus 2A01 xotime 2A02 xrArr 27F9 xrarr 27F6 xscr 1D4CD xsqcup 2A06 xuplus 2A04
            xutri 25B3 xvee 22C1 xwedge 22C0 yacute FD yacy 44F ycirc 177 ycy 44B yen A5 yfr 1D536 yicy 457
            yopf 1D56A yscr 1D4CE yucy 44E yuml FF zacute 17A zcaron 17E zcy 437 zdot 17C zeetrf 2128
            zeta 3B6 zfr 1D537 zhcy 436 zigrarr 21DD zopf 1D56B zscr 1D4CF zwj 200D zwnj 200C
            """;
}
