package ham.citybuildersim.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lists the names a branch's new prose uses that the tree does not declare.
 *
 * WHY THIS EXISTS (RV1, 2026-10-13). ChatGPT and Gemini write handoffs and
 * notes on chatgpt/ and gemini/ branches, and most of each review was the
 * same mechanical question asked by hand, a call at a time: does every method
 * and constant the text names exist, and on the class it says? This asks it
 * of every line the branch adds against main - each line of Markdown, and the
 * comments in Java - and lists only what does not resolve, so the reviewer's
 * attention goes to whether the text is right rather than whether it is real.
 *
 * WHAT A NAME IS: foo(), Class.foo() (with or without arguments), Class.CONSTANT,
 * and a whole backticked identifier, dotted or not (`Expectations`,
 * `DataSave.expectations`, `quoteRate(..., months)`). A bare name resolves when
 * the tree declares or uses it anywhere; Class.member only when Class, or a
 * supertype the tree declares, has it - so a member put on the wrong class is
 * found. The declarations are Stale's Index over JavaScan, never a regex over
 * source, which misses `final`, `double[]` and generic return types; a record's
 * components and an enum's values() are added here, because JavaScan keeps
 * them in the type's signature and not as members. The branch's own Java files
 * are read as the branch has them, so a method the branch adds resolves.
 *
 * NOT COUNTED: a name the text itself marks as new - written as "new `Name`",
 * or given a value in backticks (`DRIFT_MONTHS = 6`) anywhere in the added
 * lines - is a proposal, not a claim, and is listed apart; so is one written
 * after "proposed" or "for example". A file name in backticks (`Game.java`) is
 * Stale's to check, not this, and the generated documents (docs/map, dials.md,
 * month-order.md, harnesses.md, manual.md) are not read: the tree wrote them.
 *
 * It warns and does not gate: a passage about the past may name a method that
 * existed then. ship.sh runs it on chatgpt/* and gemini/* branches. Run it
 * from the repository root, after a build:
 *
 *     java -cp target/classes ham.citybuildersim.tools.Names chatgpt/cd1-handoff
 *     java -cp target/classes ham.citybuildersim.tools.Names 0a516a5 0a516a5^    (a commit, against its parent)
 *
 * The diff is base...branch (from where the branch left the base; main by
 * default). Exit status is the number of unresolved names, at most 250; 255
 * when git could not be asked.
 */
public final class Names {

    /* ------------------------------------------------------------------
       WHAT IT REPORTS
       ------------------------------------------------------------------ */

    /** One unresolved name, where it is first written and how many more lines write it. */
    public static final class Miss {
        public final String path;
        public final int line;
        public final String name;
        public final String why;
        public int more;
        Miss(String path, int line, String name, String why) { this.path = path; this.line = line; this.name = name; this.why = why; }
        @Override public String toString() {
            return path + ":" + line + "  " + name + "  - " + why + (more > 0 ? " (and " + more + " more line" + (more == 1 ? "" : "s") + ")" : "");
        }
    }

    /** What one diff came to: the unresolved names, the proposals set apart, and every name read. */
    public static final class Result {
        public final List<Miss> unresolved = new ArrayList<>();
        public final Set<String> proposed = new TreeSet<>();
        public final Set<String> read = new TreeSet<>();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 2) {
            System.out.println("usage: Names <branch> [<base>]    the names <base>...<branch> adds that the tree does not declare (base: main)");
            System.exit(255);
        }
        Result r;
        try {
            r = ofBranch(SourceTree.open(), args[0], args.length > 1 ? args[1] : "main");
        } catch (IOException e) {
            System.out.println("Names: " + e.getMessage());
            System.exit(255);
            return;
        }
        for (Miss m : r.unresolved) System.out.println(m);
        int n = r.unresolved.size();
        System.out.println(n + " unresolved of " + r.read.size() + " name" + (r.read.size() == 1 ? "" : "s") + " read"
                + (r.proposed.isEmpty() ? "" : "; named as new, not counted: " + String.join(", ", r.proposed)));
        System.exit(Math.min(n, 250));
    }

    /** What base...branch adds, read from git: the diff, and each changed Java file as the branch has it. */
    public static Result ofBranch(SourceTree tree, String branch, String base) throws IOException {
        String diff = git("diff", "-U0", "--no-color", "--no-ext-diff", base + "..." + branch, "--", "*.md", "*.java");
        return scan(tree, diff, path -> {
            try { return git("show", branch + ":" + path); } catch (IOException e) { return null; }
        });
    }

    /**
     * The names the added lines of a diff use and the tree does not declare.
     * The diff is git's unified form (-U0 is enough); newSource gives a changed
     * file's text as the branch has it, or null, and the branch's Java files are
     * read through it both for their declarations and for which added lines are
     * comments.
     */
    public static Result scan(SourceTree tree, String diff, Function<String, String> newSource) {
        Map<String, TreeMap<Integer, String>> added = added(diff);
        Decls d = new Decls(tree);
        Map<String, String> javaSource = new LinkedHashMap<>();
        for (String path : added.keySet()) {
            if (!path.endsWith(".java")) continue;
            String src = newSource.apply(path);
            if (src == null) continue;
            javaSource.put(path, src);
            d.add(new JavaScan(Paths.get(path), src));
        }

        // every name read, in file and line order; the proposals first, from all of it
        List<Ref> refs = new ArrayList<>();
        Set<String> proposed = new HashSet<>();
        for (var e : added.entrySet()) {
            String path = e.getKey();
            if (generated(path)) continue;
            Map<Integer, String> lines = path.endsWith(".java") ? comments(javaSource.get(path), e.getValue().keySet()) : e.getValue();
            for (var l : lines.entrySet()) refs(path, l.getKey(), l.getValue(), refs, proposed);
        }

        Result r = new Result();
        Map<String, Miss> byName = new LinkedHashMap<>();
        for (Ref ref : refs) {
            r.read.add(ref.shown);
            String why = d.resolve(ref);
            if (why == null) continue;
            if (proposed.contains(ref.last())) { r.proposed.add(ref.shown); continue; }
            Miss seen = byName.get(ref.shown);
            if (seen != null) { if (seen.line != ref.line || !seen.path.equals(ref.path)) seen.more++; continue; }
            Miss m = new Miss(ref.path, ref.line, ref.shown, why);
            byName.put(ref.shown, m);
            r.unresolved.add(m);
        }
        return r;
    }

    /* ------------------------------------------------------------------
       THE ADDED LINES
       ------------------------------------------------------------------ */

    static final Pattern HUNK = Pattern.compile("^@@ -\\d+(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@");

    /** The lines a unified diff adds, by file, numbered as the new file has them. */
    static Map<String, TreeMap<Integer, String>> added(String diff) {
        Map<String, TreeMap<Integer, String>> out = new LinkedHashMap<>();
        String file = null;
        int next = 0;
        for (String line : diff.split("\n", -1)) {
            if (line.startsWith("+++ ")) {
                String p = line.substring(4).trim();
                file = p.equals("/dev/null") ? null : p.startsWith("b/") ? p.substring(2) : p;
                continue;
            }
            if (line.startsWith("--- ") || line.startsWith("diff ") || line.startsWith("index ")) continue;
            Matcher h = HUNK.matcher(line);
            if (h.find()) { next = Integer.parseInt(h.group(1)); continue; }
            if (file == null) continue;
            if (line.startsWith("+")) out.computeIfAbsent(file, k -> new TreeMap<>()).put(next++, strip(line.substring(1)));
            else if (line.startsWith(" ")) next++;
        }
        return out;
    }

    /** Of a Java file's added lines, the part of each that is comment: code names nothing to a reader. */
    static Map<Integer, String> comments(String source, Set<Integer> lines) {
        Map<Integer, String> out = new TreeMap<>();
        if (source == null) return out;
        for (JavaScan.Tok t : JavaScan.lex(source)) {
            if (t.kind != JavaScan.T.COMMENT) continue;
            String[] parts = t.text.split("\n", -1);
            for (int k = 0; k < parts.length; k++) {
                if (lines.contains(t.line + k)) out.merge(t.line + k, strip(parts[k]), (a, b) -> a + " " + b);
            }
        }
        return out;
    }

    /** The documents Maps and ManualToMarkdown write: what they name is the tree's own, or the manual's. */
    static final List<String> GENERATED = List.of("docs/map/", "docs/dials.md", "docs/month-order.md", "docs/harnesses.md", "docs/manual.md");

    static boolean generated(String path) {
        for (String g : GENERATED) if (path.startsWith(g)) return true;
        return false;
    }

    static String strip(String s) { return s.endsWith("\r") ? s.substring(0, s.length() - 1) : s; }

    /* ------------------------------------------------------------------
       THE NAMES IN A LINE
       ------------------------------------------------------------------ */

    /** One name as a line writes it: its dotted parts, and whether it was written as a call. */
    static final class Ref {
        final String path; final int line; final List<String> parts; final boolean call; final String shown;
        Ref(String path, int line, String dotted, boolean call) {
            this.path = path; this.line = line; this.call = call;
            this.parts = List.of(dotted.split("\\."));
            this.shown = dotted + (call ? "()" : "");
        }
        String last() { return parts.get(parts.size() - 1); }
    }

    static final Pattern TICKS = Pattern.compile("`([^`\\n]+)`");
    /** A whole span that is one name: dotted identifiers, then perhaps one argument list. */
    static final Pattern WHOLE = Pattern.compile("([A-Za-z_][A-Za-z0-9_]*(?:\\.[A-Za-z_][A-Za-z0-9_]*)*)(\\([^()]*\\))?");
    /** A span that gives a name a value is the text proposing it: `DRIFT_MONTHS = 6`. */
    static final Pattern GIVEN = Pattern.compile("([A-Za-z_][A-Za-z0-9_]*)\\s*=[^=].*");
    static final Pattern FILE = Pattern.compile(".*\\.(java|md|json|bin|bat|cmd|sh|txt|html|jar|csv|png|xml|properties|class|py|exe)");
    static final Pattern QCALL = Pattern.compile("\\b([A-Z][A-Za-z0-9_]*(?:\\.[A-Z][A-Za-z0-9_]*)*)\\.([a-zA-Z_][A-Za-z0-9_]*)\\(");
    static final Pattern CONST = Pattern.compile("\\b([A-Z][A-Za-z0-9_]*(?:\\.[A-Z][A-Za-z0-9_]*)*)\\.([A-Z][A-Z0-9]*_[A-Z0-9_]*|[A-Z][A-Z0-9]{2,})\\b(?![(.])");
    static final Pattern CALL = Pattern.compile("\\b([a-z_][A-Za-z0-9_]*)\\(\\)");
    /** "new `CurveCheck`", "for example `HistoricalEconomy`": the text says the name is one it proposes. */
    static final Pattern NEW_BEFORE = Pattern.compile("(?i)\\b(?:new|proposed|for example)\\s+[`*_\"(]*$");

    /** The names one line writes, in the order: whole backticked spans, Class.member, Class.CONSTANT, foo(). */
    static void refs(String path, int line, String text, List<Ref> out, Set<String> proposed) {
        StringBuilder rest = new StringBuilder(text);
        Matcher t = TICKS.matcher(text);
        while (t.find()) {
            String span = t.group(1).trim();
            Matcher g = GIVEN.matcher(span);
            if (g.matches()) { proposed.add(g.group(1)); blank(rest, t.start(), t.end()); continue; }
            if (FILE.matcher(span).matches()) { blank(rest, t.start(), t.end()); continue; }
            Matcher w = WHOLE.matcher(span);
            if (!w.matches()) continue;                       // `toSaveArray()/restore()`: the patterns below read it
            out.add(new Ref(path, line, w.group(1), w.group(2) != null));
            if (NEW_BEFORE.matcher(text.substring(0, t.start())).find()) proposed.add(lastPart(w.group(1)));
            blank(rest, t.start(), t.end());
        }
        for (Pattern p : List.of(QCALL, CONST, CALL)) {
            Matcher m = p.matcher(rest.toString());
            List<int[]> spans = new ArrayList<>();
            while (m.find()) {
                String dotted = p == CALL ? m.group(1) : m.group(1) + "." + m.group(2);
                out.add(new Ref(path, line, dotted, p != CONST));
                if (NEW_BEFORE.matcher(rest.substring(0, m.start())).find()) proposed.add(lastPart(dotted));
                spans.add(new int[] { m.start(), m.end() });
            }
            for (int[] s : spans) blank(rest, s[0], s[1]);
        }
    }

    static void blank(StringBuilder sb, int from, int to) { for (int i = from; i < to; i++) sb.setCharAt(i, ' '); }

    static String lastPart(String dotted) { return dotted.substring(dotted.lastIndexOf('.') + 1); }

    /* ------------------------------------------------------------------
       WHAT THE TREE DECLARES
       ------------------------------------------------------------------ */

    /** Words a backtick holds that are Java's, the codebase's conventions' or the shell's, not the tree's. */
    static final Set<String> LANGUAGE = Set.of(
            "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default",
            "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected",
            "public", "record", "return", "short", "static", "super", "switch", "synchronized", "this", "throw",
            "throws", "transient", "try", "var", "void", "volatile", "while", "yield", "true", "false", "null",
            "WHY", "FAIL", "OK", "TODO", "FIXME",
            "git", "mvn", "java", "javac", "gh", "flock", "sh", "bash", "grep", "sed", "awk", "cat", "ls", "cd", "curl");
    /** What every object has, and every enum. */
    static final Set<String> OBJECT = Set.of("equals", "hashCode", "toString", "getClass", "clone");
    static final Set<String> ENUM = Set.of("values", "valueOf", "ordinal", "name", "compareTo");

    /**
     * Stale's Index, and what it leaves in a type's signature: each type's
     * supertypes, a record's components, an enum's implicit members.
     */
    static final class Decls {
        final Stale.Index ix;
        final Set<String> types = new HashSet<>();                       // simple names, nested ones too
        final Map<String, Set<String>> supers = new HashMap<>();         // simple name -> extends and implements
        final Map<String, Set<String>> implicit = new HashMap<>();       // simple name -> components, values()...

        Decls(SourceTree tree) {
            Stale.Index made;
            try { made = new Stale.Index(tree); } catch (IOException e) { throw new IllegalStateException(e); }
            ix = made;
            for (JavaScan f : tree.files) types(f);
        }

        void add(JavaScan f) { ix.add(f); types(f); }

        private void types(JavaScan f) {
            for (JavaScan.Member m : f.members) {
                if (m.kind != JavaScan.Kind.TYPE) continue;
                types.add(m.name);
                ix.byOwner.computeIfAbsent(m.name, k -> new TreeSet<>());
                String sig = m.signature;
                Set<String> imp = implicit.computeIfAbsent(m.name, k -> new TreeSet<>());
                if (sig.matches("(?:.*\\s)?enum\\s.*")) imp.addAll(ENUM);
                int rec = sig.indexOf("record " + m.name + "(");
                if (rec >= 0) {
                    int open = rec + ("record " + m.name).length(), close = closing(sig, open);
                    for (String comp : topLevel(sig.substring(open + 1, close), ',')) {
                        String[] w = comp.trim().split("[\\s.]+");
                        if (w.length > 0 && !w[w.length - 1].isEmpty()) imp.add(w[w.length - 1]);
                    }
                    sig = sig.substring(0, open) + sig.substring(close + 1);
                }
                String flat = withoutGenerics(sig);
                Set<String> up = supers.computeIfAbsent(m.name, k -> new TreeSet<>());
                for (String kw : List.of("extends", "implements")) {
                    Matcher k = Pattern.compile("\\b" + kw + "\\s+([^{]*?)(?=\\s+(?:extends|implements|permits)\\b|$)").matcher(flat);
                    if (!k.find()) continue;
                    for (String s : k.group(1).split(",")) if (!s.isBlank()) up.add(lastPart(s.trim()));
                }
            }
        }

        boolean isType(String name) { return types.contains(name) || ix.byOwner.containsKey(name); }

        /** Whether a tree type, or any supertype of it, has the member; a supertype outside the tree has whatever the tree uses. */
        boolean has(String type, String member) {
            if (OBJECT.contains(member)) return true;
            Deque<String> todo = new ArrayDeque<>(List.of(type));
            Set<String> seen = new HashSet<>();
            while (!todo.isEmpty()) {
                String t = todo.pop();
                if (!seen.add(t)) continue;
                if (!isType(t)) { if (ix.mentioned.contains(member) || ix.declared.contains(member)) return true; continue; }
                Set<String> own = ix.byOwner.get(t), imp = implicit.get(t);
                if ((own != null && own.contains(member)) || (imp != null && imp.contains(member))) return true;
                todo.addAll(supers.getOrDefault(t, Set.of()));
            }
            return false;
        }

        boolean anywhere(String name) {
            if (ix.declared.contains(name) || ix.mentioned.contains(name) || types.contains(name)) return true;
            for (Set<String> s : implicit.values()) if (s.contains(name)) return true;
            return false;
        }

        /** Null when the name resolves; otherwise why not, in a few words. */
        String resolve(Ref r) {
            List<String> parts = r.parts;
            int from = 0;
            // a package or a variable in front ("ham.citybuildersim.tools.Stale", "game.nextMonth") names no type
            while (from < parts.size() - 1 && Character.isLowerCase(parts.get(from).charAt(0))) from++;
            parts = parts.subList(from, parts.size());
            if (parts.size() == 1) {
                String n = parts.get(0);
                if (LANGUAGE.contains(n) || Stale.LIBRARY.contains(n) || Stale.PLACEHOLDERS.contains(n) || OBJECT.contains(n) || anywhere(n)) return null;
                return "declared nowhere in the tree";
            }
            String owner = parts.get(0);
            if (!isType(owner)) return ix.mentioned.contains(owner) ? null : "no type " + owner + " in the tree";
            for (int i = 1; i < parts.size(); i++) {
                String m = parts.get(i);
                if (!has(owner, m)) return owner + " does not declare " + m;
                if (i == parts.size() - 1) break;
                if (!isType(m)) return null;                  // a field, whose type this does not chase
                owner = m;
            }
            return null;
        }
    }

    static int closing(String s, int open) {
        int depth = 0;
        for (int i = open; i < s.length(); i++) {
            if (s.charAt(i) == '(') depth++;
            else if (s.charAt(i) == ')' && --depth == 0) return i;
        }
        return s.length() - 1;
    }

    /** Split on the separator where no <> or () is open. */
    static List<String> topLevel(String s, char sep) {
        List<String> out = new ArrayList<>();
        int depth = 0, from = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '<' || c == '(') depth++;
            else if (c == '>' || c == ')') depth--;
            else if (c == sep && depth == 0) { out.add(s.substring(from, i)); from = i + 1; }
        }
        out.add(s.substring(from));
        return out;
    }

    static String withoutGenerics(String s) {
        String prev;
        do { prev = s; s = s.replaceAll("<[^<>]*>", ""); } while (!s.equals(prev));
        return s;
    }

    /* ------------------------------------------------------------------
       GIT
       ------------------------------------------------------------------ */

    static String git(String... args) throws IOException {
        List<String> cmd = new ArrayList<>(List.of("git", "-c", "core.quotepath=off"));
        cmd.addAll(List.of(args));
        Process p = new ProcessBuilder(cmd).redirectErrorStream(false).start();
        String out = read(p.getInputStream()), err = read(p.getErrorStream());
        try {
            if (p.waitFor() != 0) throw new IOException("git " + String.join(" ", args) + " failed: " + err.trim());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted");
        }
        return out;
    }

    static String read(InputStream in) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        in.transferTo(b);
        return b.toString(StandardCharsets.UTF_8);
    }
}
