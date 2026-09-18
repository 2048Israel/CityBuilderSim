package ham.citybuildersim.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A structural read of one Java source file, without a compiler.
 *
 * WHY THIS EXISTS. The source tree is 120,000 lines and the biggest file in
 * it was 25,000 of them until the interface was split (2026-09-18; it is
 * 8,000 now); no assistant working on this game can hold that in view, and
 * the alternative - grepping blind, then reading a thousand lines to find the
 * two that matter - is where most of a session's budget goes.
 * The scanners in this package pre-digest the tree into short indexes
 * (docs/map, docs/dials.md, docs/month-order.md, docs/harnesses.md) that a
 * reader can open first and then jump straight to the right line.
 *
 * WHY NOT javac's Tree API. It is in the JDK, but it lives in jdk.compiler,
 * which the packaged runtime in Build EXE.bat does not carry; and it drops
 * comments, which are exactly the part of this codebase worth indexing - the
 * banners and the WHY paragraphs. This is a tokenizer plus brace counting.
 * It handles strings, text blocks, char literals and comments correctly, and
 * it knows just enough grammar to tell a method from a field from an inner
 * type. It does not need to understand expressions, because it never looks
 * inside a body except to find where it ends.
 *
 * WHAT IT PRODUCES. For a file: the class header comment, every banner
 * comment (the "/* ===== TITLE" convention this codebase uses, at any depth),
 * every member of every type declared in the file with its start and end
 * line, kind, signature and the first sentence of its own comment, the set of
 * identifiers it mentions, and every string literal with its line (for the
 * harness map). Everything is line-numbered so the reader can go straight
 * to the source.
 */
public final class JavaScan {

    /* ------------------------------------------------------------------
       THE TOKEN STREAM
       ------------------------------------------------------------------ */

    enum T { IDENT, NUMBER, STRING, CHAR, COMMENT, PUNCT }

    static final class Tok {
        final T kind; final String text; final int line; final int endLine;
        Tok(T kind, String text, int line, int endLine) {
            this.kind = kind; this.text = text; this.line = line; this.endLine = endLine;
        }
        boolean is(String s) { return kind == T.PUNCT && text.equals(s); }
        boolean ident(String s) { return kind == T.IDENT && text.equals(s); }
        @Override public String toString() { return text; }
    }

    static List<Tok> lex(String src) {
        List<Tok> out = new ArrayList<>();
        int n = src.length(), i = 0, line = 1;
        while (i < n) {
            char c = src.charAt(i);
            if (c == '\n') { line++; i++; continue; }
            if (c == '\r' || c == ' ' || c == '\t' || c == '\f') { i++; continue; }
            int start = i, startLine = line;
            if (c == '/' && i + 1 < n && src.charAt(i + 1) == '/') {
                while (i < n && src.charAt(i) != '\n') i++;
                String text = src.substring(start, i);
                // a run of // lines is one comment: the sentence above a member is
                // usually written that way, and its first line is what the index wants
                Tok prev = out.isEmpty() ? null : out.get(out.size() - 1);
                if (prev != null && prev.kind == T.COMMENT && prev.text.startsWith("//") && prev.endLine == startLine - 1) {
                    out.set(out.size() - 1, new Tok(T.COMMENT, prev.text + "\n" + text, prev.line, startLine));
                } else {
                    out.add(new Tok(T.COMMENT, text, startLine, line));
                }
                continue;
            }
            if (c == '/' && i + 1 < n && src.charAt(i + 1) == '*') {
                i += 2;
                while (i + 1 < n && !(src.charAt(i) == '*' && src.charAt(i + 1) == '/')) {
                    if (src.charAt(i) == '\n') line++;
                    i++;
                }
                i = Math.min(n, i + 2);
                out.add(new Tok(T.COMMENT, src.substring(start, i), startLine, line));
                continue;
            }
            if (c == '"') {
                if (src.startsWith("\"\"\"", i)) {                 // text block
                    i += 3;
                    while (i < n && !src.startsWith("\"\"\"", i)) {
                        if (src.charAt(i) == '\\') i++;
                        else if (src.charAt(i) == '\n') line++;
                        i++;
                    }
                    i = Math.min(n, i + 3);
                } else {
                    i++;
                    while (i < n && src.charAt(i) != '"') {
                        if (src.charAt(i) == '\\') i++;
                        else if (src.charAt(i) == '\n') line++;     // malformed, but keep counting
                        i++;
                    }
                    i = Math.min(n, i + 1);
                }
                out.add(new Tok(T.STRING, src.substring(start, i), startLine, line));
                continue;
            }
            if (c == '\'') {
                i++;
                while (i < n && src.charAt(i) != '\'') { if (src.charAt(i) == '\\') i++; i++; }
                i = Math.min(n, i + 1);
                out.add(new Tok(T.CHAR, src.substring(start, i), startLine, line));
                continue;
            }
            if (Character.isJavaIdentifierStart(c)) {
                while (i < n && Character.isJavaIdentifierPart(src.charAt(i))) i++;
                out.add(new Tok(T.IDENT, src.substring(start, i), startLine, line));
                continue;
            }
            if (Character.isDigit(c) || (c == '.' && i + 1 < n && Character.isDigit(src.charAt(i + 1)))) {
                while (i < n && (Character.isLetterOrDigit(src.charAt(i)) || src.charAt(i) == '.' || src.charAt(i) == '_'
                        || ((src.charAt(i) == '+' || src.charAt(i) == '-') && (src.charAt(i - 1) == 'e' || src.charAt(i - 1) == 'E')))) i++;
                out.add(new Tok(T.NUMBER, src.substring(start, i), startLine, line));
                continue;
            }
            i++;
            out.add(new Tok(T.PUNCT, String.valueOf(c), startLine, line));
        }
        return out;
    }

    /* ------------------------------------------------------------------
       WHAT A FILE IS MADE OF
       ------------------------------------------------------------------ */

    public enum Kind { TYPE, METHOD, CONSTRUCTOR, FIELD, CONSTANT, ENUM_CONSTANT, INITIALIZER }

    public static final class Member {
        public final Kind kind;
        public final String owner;      // the type it belongs to ("Game", "Game.BuildQuote")
        public final String name;
        public final String signature;  // modifiers, type, name and parameters, compacted
        public final String value;      // a constant's initializer text, else ""
        public final String doc;        // first sentence of the comment above it, else ""
        public final String trailing;   // a // comment on the same line as its end, else ""
        public final int line, endLine;
        public final boolean isStatic;
        Member(Kind kind, String owner, String name, String signature, String value,
               String doc, String trailing, int line, int endLine, boolean isStatic) {
            this.kind = kind; this.owner = owner; this.name = name; this.signature = signature;
            this.value = value; this.doc = doc; this.trailing = trailing;
            this.line = line; this.endLine = endLine; this.isStatic = isStatic;
        }
        public int length() { return endLine - line + 1; }
        public String qualifiedName() { return owner.isEmpty() ? name : owner + "." + name; }
    }

    public static final class Section {
        public final int line; public final int level; public final String title; public final int depth;
        Section(int line, int level, String title, int depth) {
            this.line = line; this.level = level; this.title = title; this.depth = depth;
        }
    }

    public static final class Literal {
        public final int line; public final String text; public final int depth; public final String before;
        Literal(int line, String text, int depth, String before) {
            this.line = line; this.text = text; this.depth = depth; this.before = before;
        }
    }

    public final Path path;
    public final String fileName;      // "Game.java"
    public final String typeName;      // "Game"
    public final String packageName;   // "ham.citybuildersim"
    public final int lineCount;
    public final String header;        // the class comment, trimmed of its scaffolding
    public final List<Section> sections = new ArrayList<>();
    public final List<Member> members = new ArrayList<>();
    public final List<Literal> literals = new ArrayList<>();
    public final Set<String> identifiers = new TreeSet<>();
    public final Map<String, Integer> identifierCounts = new LinkedHashMap<>();
    final List<Tok> toks;
    final String source;

    public static JavaScan read(Path path) throws IOException {
        return new JavaScan(path, Files.readString(path, StandardCharsets.UTF_8));
    }

    public JavaScan(Path path, String source) {
        this.path = path;
        this.source = source;
        this.fileName = path.getFileName().toString();
        this.typeName = fileName.endsWith(".java") ? fileName.substring(0, fileName.length() - 5) : fileName;
        this.toks = lex(source);
        int lc = 1;
        for (int i = 0; i < source.length(); i++) if (source.charAt(i) == '\n') lc++;
        if (source.endsWith("\n")) lc--;
        this.lineCount = lc;
        this.packageName = findPackage();
        this.header = findHeader();
        parse();
        Collections.sort(sections, (a, b) -> Integer.compare(a.line, b.line));
    }

    private String findPackage() {
        for (int i = 0; i + 1 < toks.size(); i++) {
            if (toks.get(i).ident("package")) {
                StringBuilder sb = new StringBuilder();
                for (int j = i + 1; j < toks.size() && !toks.get(j).is(";"); j++) sb.append(toks.get(j).text);
                return sb.toString();
            }
        }
        return "";
    }

    /** The javadoc (or the plain block comment) that sits right above the top-level type. */
    private String findHeader() {
        Tok best = null;
        int depth = 0;
        for (int i = 0; i < toks.size(); i++) {
            Tok t = toks.get(i);
            if (t.kind == T.COMMENT) {
                if (depth == 0 && t.text.startsWith("/*") && !t.text.contains("nbfs://") && !isBanner(t)) best = t;
                continue;
            }
            if (t.is("{")) depth++;
            if (t.is("}")) depth--;
            if (depth == 0 && t.kind == T.IDENT && (t.text.equals("class") || t.text.equals("interface")
                    || t.text.equals("enum") || t.text.equals("record"))) {
                return best == null ? "" : commentBody(best.text);
            }
        }
        return best == null ? "" : commentBody(best.text);
    }

    /* ------------------------------------------------------------------
       COMMENTS: BANNERS AND PROSE
       ------------------------------------------------------------------ */

    static boolean isBanner(Tok t) {
        if (t.kind != T.COMMENT || !t.text.startsWith("/*")) return false;
        String first = firstLine(t.text);
        return first.matches(".*[=]{5,}.*") || first.matches(".*[-]{5,}.*");
    }

    static String firstLine(String comment) {
        String s = comment.startsWith("/**") ? comment.substring(3) : comment.startsWith("/*") ? comment.substring(2) : comment;
        int nl = s.indexOf('\n');
        return (nl < 0 ? s : s.substring(0, nl)).replace("*/", "").trim();
    }

    /** The banner's title: what is between the rules, or the first prose line under them. */
    static String bannerTitle(String comment) {
        String[] lines = comment.split("\n");
        String first = lines[0].replaceFirst("^/\\*+", "").replace("*/", "").trim();
        String between = first.replaceAll("^[=\\-\\s]+", "").replaceAll("[=\\-\\s]+$", "").trim();
        if (!between.isEmpty()) return between;
        for (int i = 1; i < lines.length; i++) {
            String l = lines[i].trim().replaceFirst("^\\*+", "").replace("*/", "").trim();
            if (l.isEmpty() || l.matches("^[=\\-]+$")) continue;
            return l;
        }
        return "(untitled)";
    }

    static int bannerLevel(String comment) {
        return firstLine(comment).contains("=====") ? 1 : 2;
    }

    /** The comment with its slashes and stars stripped, paragraphs kept. */
    public static String commentBody(String comment) {
        String s = comment;
        if (s.startsWith("/**")) s = s.substring(3); else if (s.startsWith("/*")) s = s.substring(2);
        if (s.endsWith("*/")) s = s.substring(0, s.length() - 2);
        StringBuilder sb = new StringBuilder();
        for (String raw : s.split("\n")) {
            String l = raw.replace("\r", "");
            String t = l.trim();
            if (t.startsWith("*")) t = t.substring(1);
            if (t.startsWith(" ")) t = t.substring(1);
            if (t.startsWith("//")) t = t.substring(2).trim();
            sb.append(t.stripTrailing()).append('\n');
        }
        return sb.toString().strip();
    }

    /** The first sentence of a comment, for a one-line index. */
    public static String firstSentence(String comment) {
        String body = commentBody(comment);
        // drop javadoc tags and a leading "WHY" style heading that is all caps on its own line
        StringBuilder para = new StringBuilder();
        for (String l : body.split("\n")) {
            String t = l.trim();
            if (t.isEmpty()) { if (para.length() > 0) break; else continue; }
            if (t.startsWith("@")) { if (para.length() > 0) break; else continue; }
            para.append(t).append(' ');
        }
        String p = para.toString().trim();
        int end = -1;
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if ((c == '.' || c == '!' || c == '?') && (i + 1 == p.length() || p.charAt(i + 1) == ' ')) {
                // not a decimal point or an abbreviation like "e.g."
                if (c == '.' && i > 0 && Character.isDigit(p.charAt(i - 1)) && i + 1 < p.length() && Character.isDigit(p.charAt(i + 1))) continue;
                if (c == '.' && i >= 1 && (p.substring(Math.max(0, i - 3), i + 1).matches(".*\\b(e\\.g|i\\.e|vs|etc)\\."))) continue;
                end = i; break;
            }
        }
        String s = end < 0 ? p : p.substring(0, end + 1);
        return s.length() > 220 ? s.substring(0, 217) + "..." : s;
    }

    /* ------------------------------------------------------------------
       THE PARSE: TYPES, MEMBERS, BODIES
       ------------------------------------------------------------------ */

    private static final Set<String> NOT_A_METHOD_NAME = Set.of(
            "if", "for", "while", "switch", "catch", "synchronized", "return", "new", "super", "this",
            "try", "do", "else", "throw", "case", "default", "assert", "yield");
    private static final Set<String> MODIFIERS = Set.of(
            "public", "private", "protected", "static", "final", "abstract", "synchronized", "native",
            "transient", "volatile", "strictfp", "default", "sealed", "non-sealed");

    private static final class Frame {
        final String kind;       // "class", "interface", "enum", "record", "@interface" or "block"
        final String name;       // qualified within the file
        boolean enumConstantsDone;
        Frame(String kind, String name) { this.kind = kind; this.name = name; }
        boolean isType() { return !kind.equals("block"); }
    }

    private void parse() {
        List<Frame> stack = new ArrayList<>();
        int i = 0, n = toks.size();
        int depth = 0;                 // brace depth, for sections and literals
        Tok lastComment = null;
        while (i < n) {
            Tok t = toks.get(i);
            if (t.kind == T.COMMENT) {
                if (isBanner(t)) sections.add(new Section(t.line, bannerLevel(t.text), bannerTitle(t.text), depth));
                lastComment = t;
                i++;
                continue;
            }
            if (t.kind == T.STRING) { noteLiteral(i, depth); i++; continue; }
            if (t.kind == T.IDENT) noteIdentifier(t.text);

            Frame top = stack.isEmpty() ? null : stack.get(stack.size() - 1);
            if (top == null || top.isType()) {
                // member level: read one declaration up to '{' or ';'
                if (t.is("}")) { stack.remove(stack.size() - 1); depth--; i++; lastComment = null; continue; }
                if (t.is(";")) { i++; continue; }
                if (top == null && (t.ident("package") || t.ident("import"))) {
                    while (i < n && !toks.get(i).is(";")) i++;
                    i++; lastComment = null; continue;
                }
                if (t.is("@")) {                                   // annotation: @Name or @Name(...)
                    i = skipAnnotation(i);
                    continue;
                }
                if (top != null && top.kind.equals("enum") && !top.enumConstantsDone) {
                    i = readEnumConstants(i, top, lastComment);
                    lastComment = null;
                    top.enumConstantsDone = true;
                    continue;
                }
                int declStart = i;
                int j = i;
                int paren = 0, angle = 0;
                boolean sawEquals = false, sawParen = false;
                String typeKeyword = null; int typeKeywordAt = -1;
                int endTok = -1; boolean endsWithBrace = false;
                while (j < n) {
                    Tok u = toks.get(j);
                    if (u.kind == T.COMMENT) { j++; continue; }
                    if (u.kind == T.STRING) { noteLiteral(j, depth); j++; continue; }
                    if (u.kind == T.IDENT) {
                        noteIdentifier(u.text);
                        if (paren == 0 && typeKeyword == null && !sawEquals && (u.text.equals("class") || u.text.equals("interface")
                                || u.text.equals("enum") || u.text.equals("record"))) {
                            // 'record' and 'enum' can also be identifiers in old code; require a name after
                            if (j + 1 < n && toks.get(j + 1).kind == T.IDENT) { typeKeyword = u.text; typeKeywordAt = j; }
                        }
                    }
                    if (u.is("(")) { if (paren == 0 && !sawEquals) sawParen = true; paren++; }
                    else if (u.is(")")) paren--;
                    else if (u.is("<") && paren == 0) angle++;
                    else if (u.is(">") && paren == 0 && angle > 0) angle--;
                    else if (u.is("=") && paren == 0 && !sawEquals && !(j + 1 < n && toks.get(j + 1).is("="))) sawEquals = true;
                    else if (u.is("@") && paren == 0 && !sawEquals && typeKeyword == null) {
                        // "@interface Name {" is an annotation type; any other '@' here is an
                        // annotation on the declaration or on a type, which the index does not need
                        if (j + 1 < n && toks.get(j + 1).ident("interface")) { typeKeyword = "@interface"; typeKeywordAt = j + 1; j += 2; continue; }
                        j = skipAnnotation(j); continue;
                    }
                    else if (u.is("{") && paren == 0) {
                        if (sawEquals && typeKeyword == null) {       // an initializer with a block in it; skip the block
                            j = skipBlock(j, depth); continue;
                        }
                        endTok = j; endsWithBrace = true; break;
                    }
                    else if (u.is(";") && paren == 0) { endTok = j; break; }
                    else if (u.is("}") && paren == 0) { endTok = j; break; }   // malformed; bail
                    j++;
                }
                if (endTok < 0) break;
                List<Tok> decl = new ArrayList<>();
                for (int k = declStart; k < endTok; k++) if (toks.get(k).kind != T.COMMENT) decl.add(toks.get(k));
                String owner = top == null ? "" : top.name;
                String doc = lastComment != null && lastComment.endLine >= toks.get(declStart).line - 3 && !isBanner(lastComment)
                        ? firstSentence(lastComment.text) : "";
                if (lastComment != null && lastComment.endLine >= toks.get(declStart).line - 3 && isBanner(lastComment)) doc = "";
                boolean isStatic = decl.stream().anyMatch(d -> d.ident("static"));
                int startLine = toks.get(declStart).line;

                if (typeKeyword != null) {
                    String name = toks.get(typeKeywordAt + 1).text;
                    String qual = owner.isEmpty() ? name : owner + "." + name;
                    int close = endsWithBrace ? matchingBrace(endTok) : endTok;
                    members.add(new Member(Kind.TYPE, owner, name, compact(decl, endTok - declStart), "", doc, "",
                            startLine, toks.get(close).endLine, isStatic));
                    if (endsWithBrace) { stack.add(new Frame(typeKeyword, qual)); depth++; i = endTok + 1; }
                    else i = endTok + 1;
                    lastComment = null;
                    continue;
                }
                if (endsWithBrace && !sawParen) {                  // "static {" or "{" initializer
                    int close = matchingBrace(endTok);
                    members.add(new Member(Kind.INITIALIZER, owner, isStatic ? "static {}" : "{}", isStatic ? "static { ... }" : "{ ... }",
                            "", doc, "", startLine, toks.get(close).endLine, isStatic));
                    i = scanBodyAndSkip(endTok, depth);
                    lastComment = null;
                    continue;
                }
                if (sawParen && !sawEquals) {                      // a method or constructor (abstract if ';')
                    int open = -1;
                    for (int k = 0; k < decl.size(); k++) if (decl.get(k).is("(")) { open = k; break; }
                    String name = open > 0 ? decl.get(open - 1).text : "?";
                    String simpleOwner = owner.contains(".") ? owner.substring(owner.lastIndexOf('.') + 1) : owner;
                    Kind kind = name.equals(simpleOwner) ? Kind.CONSTRUCTOR : Kind.METHOD;
                    if (NOT_A_METHOD_NAME.contains(name)) kind = Kind.METHOD;
                    int close = endsWithBrace ? matchingBrace(endTok) : endTok;
                    members.add(new Member(kind, owner, name, compact(decl, decl.size()), "", doc, "",
                            startLine, toks.get(close).endLine, isStatic));
                    i = endsWithBrace ? scanBodyAndSkip(endTok, depth) : endTok + 1;
                    lastComment = null;
                    continue;
                }
                // a field (possibly several: "int a, b;")
                {
                    int eq = -1;
                    for (int k = 0; k < decl.size(); k++) if (decl.get(k).is("=")) { eq = k; break; }
                    int nameAt = -1;
                    if (eq > 0) nameAt = eq - 1;
                    else {
                        for (int k = decl.size() - 1; k >= 0; k--) if (decl.get(k).kind == T.IDENT) { nameAt = k; break; }
                    }
                    String name = nameAt >= 0 ? decl.get(nameAt).text : "?";
                    boolean isFinal = decl.stream().anyMatch(d -> d.ident("final"));
                    Kind kind = isStatic && isFinal ? Kind.CONSTANT : Kind.FIELD;
                    String value = "";
                    if (eq >= 0) {
                        // the initializer text, from the source, so that spacing and casts survive
                        Tok from = decl.get(eq + 1 < decl.size() ? eq + 1 : eq);
                        value = sourceBetween(from, toks.get(endTok)).trim();
                    }
                    String trailing = "";
                    if (endTok + 1 < n && toks.get(endTok + 1).kind == T.COMMENT && toks.get(endTok + 1).line == toks.get(endTok).line)
                        trailing = commentBody(toks.get(endTok + 1).text).replace('\n', ' ').trim();
                    String sig = compact(decl, eq >= 0 ? eq : decl.size());
                    members.add(new Member(kind, owner, name, sig, value, doc, trailing, startLine, toks.get(endTok).endLine, isStatic));
                    i = endTok + 1;
                    lastComment = null;
                    continue;
                }
            }
            // never reached: bodies are skipped wholesale by scanBodyAndSkip
            i++;
        }
    }

    private int skipAnnotation(int at) {
        int j = at + 1;                                   // the name (possibly dotted)
        if (j < toks.size() && toks.get(j).kind == T.IDENT) j++;
        while (j + 1 < toks.size() && toks.get(j).is(".") && toks.get(j + 1).kind == T.IDENT) j += 2;
        // (not "every identifier that follows": "@Override public void start(Stage s) {" is an
        // annotation, three words and a method - reading the words as the name swallowed the
        // method and filed it as an initializer, until 2026-09-18)
        if (j < toks.size() && toks.get(j).is("(")) {
            int p = 0;
            while (j < toks.size()) {
                Tok u = toks.get(j);
                if (u.kind == T.STRING) noteLiteral(j, -1);
                if (u.is("(")) p++;
                else if (u.is(")")) { p--; if (p == 0) { j++; break; } }
                j++;
            }
        }
        return j;
    }

    private int readEnumConstants(int at, Frame top, Tok lastComment) {
        int j = at, paren = 0;
        int constStart = -1; Tok doc = lastComment;
        while (j < toks.size()) {
            Tok u = toks.get(j);
            if (u.kind == T.COMMENT) { doc = u; j++; continue; }
            if (u.kind == T.STRING) noteLiteral(j, -1);
            if (u.kind == T.IDENT) noteIdentifier(u.text);
            if (u.is("@") && paren == 0) { j = skipAnnotation(j); continue; }
            if (paren == 0 && constStart < 0 && u.kind == T.IDENT) constStart = j;
            if (u.is("(")) paren++;
            else if (u.is(")")) paren--;
            else if (u.is("{") && paren == 0) { j = skipBlock(j, -1); continue; }   // a constant body
            else if ((u.is(",") || u.is(";") || u.is("}")) && paren == 0) {
                if (constStart >= 0) {
                    Tok c = toks.get(constStart);
                    String d = doc != null && doc.endLine >= c.line - 2 && !isBanner(doc) ? firstSentence(doc.text) : "";
                    members.add(new Member(Kind.ENUM_CONSTANT, top.name, c.text, c.text, "", d, "", c.line, u.line, true));
                }
                constStart = -1; doc = null;
                if (u.is(";")) return j + 1;
                if (u.is("}")) return j;                 // enum with constants only; the frame's '}' closes it
            }
            j++;
        }
        return j;
    }

    /** From a '{' at index, the index of its matching '}'. */
    private int matchingBrace(int open) {
        int d = 0;
        for (int j = open; j < toks.size(); j++) {
            Tok u = toks.get(j);
            if (u.is("{")) d++;
            else if (u.is("}")) { d--; if (d == 0) return j; }
        }
        return toks.size() - 1;
    }

    /** Skip a block, noting nothing; returns the index after its '}'. */
    private int skipBlock(int open, int depth) {
        return scanBodyAndSkip(open, Math.max(0, depth));
    }

    /** Skip a body, but record the banners, literals and identifiers inside it; returns the index after '}'. */
    private int scanBodyAndSkip(int open, int depthAtOpen) {
        int d = 0;
        int j = open;
        while (j < toks.size()) {
            Tok u = toks.get(j);
            if (u.kind == T.COMMENT) {
                if (isBanner(u)) sections.add(new Section(u.line, bannerLevel(u.text) + 1, bannerTitle(u.text), depthAtOpen + d));
            } else if (u.kind == T.STRING) noteLiteral(j, depthAtOpen + d);
            else if (u.kind == T.IDENT) noteIdentifier(u.text);
            else if (u.is("{")) d++;
            else if (u.is("}")) { d--; if (d == 0) return j + 1; }
            j++;
        }
        return j;
    }

    private void noteIdentifier(String s) {
        identifiers.add(s);
        identifierCounts.merge(s, 1, Integer::sum);
    }

    private void noteLiteral(int at, int depth) {
        Tok t = toks.get(at);
        // what precedes it: an identifier and '(' (a call), for the harness map
        String before = "";
        int k = at - 1;
        while (k >= 0 && toks.get(k).kind == T.COMMENT) k--;
        if (k >= 1 && toks.get(k).is("(") && toks.get(k - 1).kind == T.IDENT) before = toks.get(k - 1).text;
        else if (k >= 0 && toks.get(k).kind == T.IDENT) before = toks.get(k).text;
        else if (k >= 0) before = toks.get(k).text;
        literals.add(new Literal(t.line, unquote(t.text), depth, before));
    }

    static String unquote(String s) {
        if (s.startsWith("\"\"\"")) return s.substring(3, Math.max(3, s.length() - 3)).strip();
        if (s.length() >= 2) return s.substring(1, s.length() - 1);
        return s;
    }

    private String sourceBetween(Tok from, Tok to) {
        // the two tokens are on known lines; find their character offsets by re-lexing is wasteful,
        // so reconstruct from tokens instead, which loses only whitespace
        StringBuilder sb = new StringBuilder();
        boolean started = false;
        for (Tok u : toks) {
            if (u == from) started = true;
            if (u == to) break;
            if (started && u.kind != T.COMMENT) sb.append(u.text).append(' ');
        }
        return compactText(sb.toString());
    }

    static String compact(List<Tok> decl, int upTo) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < upTo && k < decl.size(); k++) sb.append(decl.get(k).text).append(' ');
        return compactText(sb.toString());
    }

    static String compactText(String s) {
        return s.replace(" ( ", "(").replace(" (", "(").replace("( ", "(").replace(" )", ")")
                .replace(" ,", ",").replace(" .", ".").replace(". ", ".").replace(" [ ]", "[]").replace("[ ]", "[]")
                .replace(" <", "<").replace("< ", "<").replace(" >", ">").replace(" ;", ";")
                .replaceAll("\\s+", " ").trim();
    }

    /* ------------------------------------------------------------------
       QUESTIONS THE GENERATORS ASK
       ------------------------------------------------------------------ */

    public Member method(String name) {
        for (Member m : members) if ((m.kind == Kind.METHOD || m.kind == Kind.CONSTRUCTOR) && m.name.equals(name)) return m;
        return null;
    }

    public List<Member> methods() {
        List<Member> out = new ArrayList<>();
        for (Member m : members) if (m.kind == Kind.METHOD || m.kind == Kind.CONSTRUCTOR) out.add(m);
        return out;
    }

    public List<Member> constants() {
        List<Member> out = new ArrayList<>();
        for (Member m : members) if (m.kind == Kind.CONSTANT) out.add(m);
        return out;
    }

    /** Source lines [from, to], 1-based inclusive, without line endings. */
    public List<String> lines(int from, int to) {
        List<String> out = new ArrayList<>();
        int line = 1, start = 0;
        for (int i = 0; i <= source.length(); i++) {
            if (i == source.length() || source.charAt(i) == '\n') {
                if (line >= from && line <= to) out.add(source.substring(start, i).replace("\r", ""));
                if (line > to) break;
                line++; start = i + 1;
            }
        }
        return out;
    }

    /** The tokens of a method body, for the month-order listing: index of '{' and of its '}'. */
    int[] bodyRange(Member m) {
        int open = -1;
        for (int j = 0; j < toks.size(); j++) {
            Tok u = toks.get(j);
            if (u.line < m.line) continue;
            if (u.is("{")) { open = j; break; }
            if (u.line > m.endLine) break;
        }
        if (open < 0) return null;
        return new int[]{ open, matchingBrace(open) };
    }
}
