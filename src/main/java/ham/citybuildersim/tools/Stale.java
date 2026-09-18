package ham.citybuildersim.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Finds the comments and documents that have stopped being true, mechanically.
 *
 * WHY THIS EXISTS. The docs pass (docs/docs-pass.md) is a fresh agent reading
 * the prose beside a change and asking of each sentence whether the code still
 * makes it true. The two passes run so far found the same handful of shapes
 * every time, and every one of them is a thing a scanner can see: a `see
 * foo()` naming a method that no longer exists anywhere, a class header saying
 * "the twelve banners" over a file with ten of them, a javadoc left with no
 * member under it, a docs/map page older than the source it maps. This is the
 * mechanical half of that pass, so the agent's attention goes to the half that
 * needs judgement - whether a paragraph still explains the right thing.
 *
 * FIRM AND SOFT. A finding in a firm category is a defect: the prose says
 * something the tree contradicts, and somebody has to change one of the two.
 * The firm categories are the exit code, so StaleCheck can assert them, and
 * they are built to have no false positives on this tree - a count stated in a
 * class header is compared against the banners that header names, not just
 * against the file's total, because "the eleven banners from FINANCES to
 * BORROW" is a claim about a range and not about the file. The soft categories
 * are printed and counted separately because they can be right and still be
 * reported: a member name in a comment may be a library call this tree never
 * makes, and a checkout writes modification times it does not mean.
 *
 * Run from the repository root, like the other tools; -Dsrc= and -Ddocs=
 * move the tree and the documents, exactly as SourceTree takes them:
 *
 *     java -cp target/classes ham.citybuildersim.tools.Stale
 *
 * Exit status is the number of firm findings, 0 when clean.
 */
public final class Stale {

    /* ------------------------------------------------------------------
       WHAT A FINDING IS
       ------------------------------------------------------------------ */

    /** The kinds of staleness, firm first. A firm finding is a defect; a soft one is a question. */
    public enum Cat {
        ORPHANED_JAVADOC(true, "orphaned javadoc",
                "FIRM: a /** ... */ with no member under it - the next thing is another javadoc, a banner, a closing brace or the end of the file"),
        MISSING_FILE(true, "missing file",
                "FIRM: prose that names a .java file, or a docs/ page, that is not in the tree"),
        STATED_COUNT(true, "stated count",
                "FIRM: a header that counts its own banners, or a front door that counts harnesses or interface files, and is out"),
        UNRESOLVED_MEMBER(false, "unresolved member",
                "soft: a comment naming name() that nothing in the tree declares or even mentions - it may be a library call this tree never makes"),
        MAP_OLDER(false, "map older than source",
                "soft: docs/map is behind the sources - a regenerate (Maps, or Regenerate maps.bat) clears every line of this, and a fresh checkout writes modification times it does not mean"),
        FUZZY_COUNT(false, "fuzzy count",
                "soft: a count of banners or sections stated away from the class header, or of sectors, which is a game concept as well as a file count - read it before believing it");

        public final boolean firm;
        public final String title;
        public final String explains;
        Cat(boolean firm, String title, String explains) { this.firm = firm; this.title = title; this.explains = explains; }
    }

    public static final class Finding {
        public final Cat cat;
        public final String path;
        public final int line;
        public final String what;
        Finding(Cat cat, String path, int line, String what) {
            this.cat = cat; this.path = path; this.line = line; this.what = what;
        }
        @Override public String toString() { return path + ":" + line + "  " + what; }
    }

    /* ------------------------------------------------------------------
       THE REPORT
       ------------------------------------------------------------------ */

    public static void main(String[] args) throws IOException {
        SourceTree tree = SourceTree.open();
        List<Finding> all = scan(tree);
        System.out.println("# Stale - what the prose says that the tree does not, " + SourceTree.stamp());
        System.out.println("scanned " + tree.files.size() + " source files under " + tree.root
                + " and the documents under " + tree.docs);
        int firm = 0;
        for (Cat c : Cat.values()) {
            List<Finding> in = of(all, c);
            if (c.firm) firm += in.size();
            System.out.printf("%n--- %s (%d) - %s ---%n", c.title, in.size(), c.explains);
            for (Finding f : in) System.out.println(f);
            if (in.isEmpty()) System.out.println("(nothing)");
        }
        int soft = all.size() - firm;
        System.out.printf("%n%d firm finding%s, %d soft.%s%n", firm, firm == 1 ? "" : "s", soft,
                firm == 0 ? " The prose and the tree agree on everything this tool can check." : "");
        System.exit(Math.min(firm, 250));
    }

    /** The findings of one category, in file and line order. */
    public static List<Finding> of(List<Finding> all, Cat c) {
        List<Finding> out = new ArrayList<>();
        for (Finding f : all) if (f.cat == c) out.add(f);
        out.sort(Comparator.comparing((Finding f) -> f.path).thenComparingInt(f -> f.line));
        return out;
    }

    /** Every finding, in no particular order; use of() to group them. */
    public static List<Finding> scan(SourceTree tree) throws IOException {
        Index ix = new Index(tree);
        List<Finding> out = new ArrayList<>();
        orphanedJavadoc(tree, out);
        missingFile(tree, ix, out);
        statedCount(tree, ix, out);
        unresolvedMember(tree, ix, out);
        mapOlderThanSource(tree, out);
        return out;
    }

    /* ------------------------------------------------------------------
       WHAT THE TREE ACTUALLY CONTAINS
       ------------------------------------------------------------------ */

    /**
     * The counts and name sets every category asks about: which files exist,
     * which member names are declared anywhere, which identifiers the tree
     * mentions at all, and the three numbers the two front doors state.
     */
    static final class Index {
        final Set<String> javaFiles = new TreeSet<>();       // "Game.java", stubs included
        final Set<String> declared = new TreeSet<>();        // every member name in the tree
        final Set<String> mentioned = new TreeSet<>();       // every identifier the tree mentions
        final Map<String, Set<String>> byOwner = new LinkedHashMap<>();  // type -> its own member names
        int harnessFiles, sectorFiles, uiFiles;

        Index(SourceTree tree) throws IOException {
            try (Stream<Path> s = Files.walk(tree.root)) {
                for (Path p : s.filter(q -> q.toString().endsWith(".java")).toList()) {
                    String name = p.getFileName().toString();
                    javaFiles.add(name);
                    String dir = p.getParent() == null ? "" : p.getParent().getFileName().toString();
                    if (dir.equals("sectors")) sectorFiles++;
                    else if (dir.equals("ui")) uiFiles++;
                    else if (dir.equals("citybuildersim") && name.endsWith("Check.java")) harnessFiles++;
                }
            }
            for (JavaScan f : tree.files) {
                mentioned.addAll(f.identifiers);
                Set<String> own = byOwner.computeIfAbsent(f.typeName, k -> new TreeSet<>());
                for (JavaScan.Member m : f.members) {
                    declared.add(m.name);
                    own.add(m.name);
                    // an inner type's members belong to the file's type as well, so a comment
                    // naming Game.something resolves against Game.BuildQuote's members too
                    // an inner type's own name is worth having too, so a comment about a
                    // nested record's method resolves against the type that declares it
                    if (!m.owner.isEmpty() && !m.owner.equals(f.typeName)) {
                        byOwner.computeIfAbsent(m.owner.substring(m.owner.lastIndexOf('.') + 1), k -> new TreeSet<>()).add(m.name);
                    }
                }
            }
        }
    }

    /** The file's top-level banners: the section convention this codebase uses, at member level. */
    static List<JavaScan.Section> topBanners(JavaScan f) {
        List<JavaScan.Section> out = new ArrayList<>();
        for (JavaScan.Section s : f.sections) if (s.level == 1) out.add(s);
        return out;
    }

    /* ------------------------------------------------------------------
       1. ORPHANED JAVADOC
       ------------------------------------------------------------------ */

    static void orphanedJavadoc(SourceTree tree, List<Finding> out) {
        for (JavaScan f : tree.files) {
            List<JavaScan.Tok> toks = f.toks;
            int n = toks.size();
            for (int i = 0; i < n; i++) {
                JavaScan.Tok t = toks.get(i);
                if (t.kind != JavaScan.T.COMMENT || !t.text.startsWith("/**")) continue;
                if (JavaScan.isBanner(t)) continue;                       // "/** ===== TITLE" is a banner
                int j = i + 1;
                while (j < n) {
                    JavaScan.Tok u = toks.get(j);
                    if (u.kind == JavaScan.T.COMMENT) {
                        if (u.text.startsWith("/**") || JavaScan.isBanner(u)) break;
                        j++; continue;                                    // a // note between the two is fine
                    }
                    if (u.is("@")) { j = skipAnnotation(toks, j); continue; }
                    break;
                }
                String why = null;
                if (j >= n) {
                    why = "a javadoc at the end of the file, with no member under it";
                } else {
                    JavaScan.Tok u = toks.get(j);
                    if (u.kind == JavaScan.T.COMMENT && JavaScan.isBanner(u)) {
                        // a class header with the file's first banner under it is the convention,
                        // not a defect: PayTier opens that way and so does every file whose first
                        // section is introduced before the type it belongs to
                        if (!JavaScan.commentBody(t.text).equals(f.header))
                            why = "a javadoc with a section banner under it (line " + u.line + "), not a member";
                    } else if (u.kind == JavaScan.T.COMMENT) {
                        why = "two javadocs with no member between them; this is the upper one, the lower is line " + u.line;
                    } else if (u.is("}")) {
                        why = "a javadoc with nothing under it: the type or block closes on line " + u.line;
                    }
                }
                if (why != null) out.add(new Finding(Cat.ORPHANED_JAVADOC, path(f), t.line,
                        why + " - \"" + SourceTree.clip(JavaScan.firstSentence(t.text), 60) + "\""));
            }
        }
    }

    /** From an '@' at index, the index of the token after the annotation. */
    static int skipAnnotation(List<JavaScan.Tok> toks, int at) {
        int j = at + 1;
        if (j < toks.size() && toks.get(j).kind == JavaScan.T.IDENT) j++;
        while (j + 1 < toks.size() && toks.get(j).is(".") && toks.get(j + 1).kind == JavaScan.T.IDENT) j += 2;
        if (j < toks.size() && toks.get(j).is("(")) {
            int p = 0;
            while (j < toks.size()) {
                JavaScan.Tok u = toks.get(j);
                if (u.is("(")) p++;
                else if (u.is(")")) { p--; if (p == 0) { j++; break; } }
                j++;
            }
        }
        return j;
    }

    /* ------------------------------------------------------------------
       2. MISSING FILE
       ------------------------------------------------------------------ */

    static final Pattern JAVA_REF = Pattern.compile("\\b([A-Z][A-Za-z0-9]*)\\.java\\b");
    /**
     * A document is only reported when it is written as a path under docs/ with a
     * lower-case name. The design record lives in the claude.ai project, not in
     * the tree, so `claude/the-clock.md` is not a missing file; and NAME.md in a
     * table row is a placeholder for a generated page, not a claim that one is
     * called that.
     */
    static final Pattern DOC_REF = Pattern.compile("\\b(docs(?:/[A-Za-z0-9_-]+)*/[a-z0-9][a-z0-9-]*\\.md)\\b");

    /**
     * Names that are written as a shape rather than as a file: "&lt;Name&gt;Screen.java",
     * "*Check.java", "NAME.md". The match is the tail of a wildcard, not a claim.
     */
    static final Set<String> SHAPE_NAMES = Set.of("Name", "NAME", "Something", "Foo", "Bar", "Baz",
            "ClassName", "Class", "Screen", "Check", "Java", "X", "Y");

    static void missingFile(SourceTree tree, Index ix, List<Finding> out) throws IOException {
        for (JavaScan f : tree.files) {
            for (JavaScan.Tok t : f.toks) {
                if (t.kind != JavaScan.T.COMMENT) continue;
                refs(tree, ix, path(f), t.text, t.line, out);
            }
        }
        for (Path doc : prose(tree)) {
            List<String> lines = Files.readAllLines(doc, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) refs(tree, ix, rel(doc), lines.get(i), i + 1, out);
        }
    }

    /** Every .java and docs/ reference in one piece of text, checked against the tree. */
    static void refs(SourceTree tree, Index ix, String where, String text, int firstLine, List<Finding> out) {
        Matcher m = JAVA_REF.matcher(text);
        while (m.find()) {
            String name = m.group(1);
            char before = m.start() == 0 ? ' ' : text.charAt(m.start() - 1);
            if (before == '>' || before == '*' || before == '<' || before == '%') continue;   // <Name>Screen.java, *Check.java
            if (SHAPE_NAMES.contains(name)) continue;
            if (ix.javaFiles.contains(name + ".java")) continue;
            out.add(new Finding(Cat.MISSING_FILE, where, firstLine + newlines(text, m.start()),
                    "names " + name + ".java, which is not in the tree"));
        }
        m = DOC_REF.matcher(text);
        while (m.find()) {
            String ref = m.group(1);
            Path p = tree.docs.resolve(ref.substring("docs/".length()));
            if (Files.exists(p)) continue;
            out.add(new Finding(Cat.MISSING_FILE, where, firstLine + newlines(text, m.start()),
                    "names " + ref + ", which is not there (" + p + ")"));
        }
    }

    /**
     * The prose documents: the hand-written pages directly under docs/ and the
     * three front doors. Never docs/map, which is generated, and never
     * docs/notes/, which is the project's record copied in - the list and the
     * changelog name files that WERE in the tree, and are right to.
     */
    static List<Path> prose(SourceTree tree) throws IOException {
        List<Path> out = new ArrayList<>();
        if (Files.isDirectory(tree.docs)) {
            try (Stream<Path> s = Files.list(tree.docs)) {
                out.addAll(s.filter(p -> p.toString().endsWith(".md")).sorted().toList());
            }
        }
        for (String front : new String[]{ "README.md", "CLAUDE.md", "AGENTS.md" }) {
            Path p = Paths.get(front);
            if (Files.isRegularFile(p)) out.add(p);
        }
        return out;
    }

    /* ------------------------------------------------------------------
       3. STATED COUNT
       ------------------------------------------------------------------ */

    static final Map<String, Integer> NUMBERS = numbers();
    static final String NUM = alternation();

    /** A number is only a number when a letter or a hyphen does not run into it: "fifty-six" is 56, never six. */
    static final String SAID = "(?<![A-Za-z-])(" + NUM + ")";

    static final Pattern BANNERS = Pattern.compile("(?i)\\b(?:the\\s+)?" + SAID + "\\s+(?:top-level\\s+|class-level\\s+)?banner(?:\\s+section)?s?\\b");
    static final Pattern SECTIONS = Pattern.compile("(?i)\\b(?:the\\s+)?" + SAID + "\\s+sections?\\b");
    static final Pattern HARNESSES = Pattern.compile("(?i)\\b" + SAID + "\\s+harness(?:es)?\\b");
    static final Pattern UI_FILES = Pattern.compile("(?i)\\b" + SAID + "\\s+files\\b");
    static final Pattern SECTORS = Pattern.compile("(?i)\\b" + SAID + "\\s+(?:private\\s+)?sectors?(?:\\s+classes)?\\b");

    static void statedCount(SourceTree tree, Index ix, List<Finding> out) throws IOException {
        for (JavaScan f : tree.files) {
            int banners = topBanners(f).size();
            for (JavaScan.Tok t : f.toks) {
                if (t.kind != JavaScan.T.COMMENT) continue;
                String body = flat(JavaScan.commentBody(t.text));
                boolean isHeader = JavaScan.commentBody(t.text).equals(f.header);
                List<int[]> counted = new ArrayList<>();
                Matcher m = BANNERS.matcher(body);
                while (m.find()) {
                    counted.add(new int[]{ m.start(), m.end() });
                    int said = number(m.group(1));
                    if (said < 0 || quoted(body, m.start())) continue;
                    if (!isHeader) {
                        // away from the header, "the four banners" is as often about another file
                        if (said != banners) out.add(new Finding(Cat.FUZZY_COUNT, path(f), t.line,
                                "says \"" + m.group() + "\" away from the class header; this file has " + banners
                                + " top-level banners, but the sentence may be about another file"));
                        continue;
                    }
                    if (agrees(said, banners, sentence(body, m.start()), f)) continue;
                    out.add(new Finding(Cat.STATED_COUNT, path(f), t.line,
                            "the class header says \"" + m.group() + "\" and the file has " + banners
                            + " top-level banner" + (banners == 1 ? "" : "s")));
                }
                m = SECTIONS.matcher(body);
                while (m.find()) {
                    boolean overlaps = false;
                    for (int[] r : counted) if (m.start() < r[1] && m.end() > r[0]) overlaps = true;
                    if (overlaps) continue;
                    int said = number(m.group(1));
                    if (said < 2 || quoted(body, m.start())) continue;   // "one section" is prose, not a count
                    if (said == banners || said == f.sections.size()) continue;
                    if (agrees(said, banners, sentence(body, m.start()), f)) continue;
                    out.add(new Finding(Cat.FUZZY_COUNT, path(f), t.line,
                            "says \"" + m.group() + "\"; this file has " + banners + " top-level banners and "
                            + f.sections.size() + " in all - \"sections\" may mean something else here"));
                }
            }
        }
        // the two front doors, against three counts that are simply files on disk
        for (Path doc : List.of(Paths.get("README.md"), Paths.get("CLAUDE.md"))) {
            if (!Files.isRegularFile(doc)) continue;
            List<String> lines = Files.readAllLines(doc, StandardCharsets.UTF_8);
            boolean fenced = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int no = i + 1;
                if (line.stripLeading().startsWith("```")) { fenced = !fenced; continue; }
                // a fenced block is the tree diagram ("about 200 files" is a shape, not a
                // count) and a command line is an invocation ("SaveDump 3 sectors" asks a
                // save for its sectors). The indented block in CLAUDE.md is neither: it
                // states the counts this looks at, so indentation alone is not a reason to skip.
                if (fenced || line.contains("java -cp") || line.contains("java -jar") || line.contains("mvn ")) continue;
                Matcher m = HARNESSES.matcher(line);
                while (m.find()) {
                    int said = number(m.group(1));
                    if (said >= 0 && !quoted(line, m.start()) && said != ix.harnessFiles)
                        out.add(new Finding(Cat.STATED_COUNT, rel(doc), no,
                                "says \"" + m.group() + "\" and there are " + ix.harnessFiles + " *Check.java files"));
                }
                String low = line.toLowerCase();
                if (low.contains("interface") || low.contains("ui/")) {
                    m = UI_FILES.matcher(line);
                    while (m.find()) {
                        int said = number(m.group(1));
                        if (said >= 0 && !quoted(line, m.start()) && said != ix.uiFiles)
                            out.add(new Finding(Cat.STATED_COUNT, rel(doc), no,
                                    "says the interface is \"" + m.group() + "\" and ui/ holds " + ix.uiFiles));
                    }
                }
                m = SECTORS.matcher(line);
                while (m.find()) {
                    int said = number(m.group(1));
                    if (said >= 1 && !quoted(line, m.start()) && said != ix.sectorFiles && said != 1)
                        out.add(new Finding(Cat.FUZZY_COUNT, rel(doc), no,
                                "says \"" + m.group() + "\" and sectors/ holds " + ix.sectorFiles
                                + " classes - a sector is a game concept too, so this may be about something else"));
                }
                // "Game.java ... 24 banner sections": a count of another file's banners
                Matcher jm = JAVA_REF.matcher(line);
                while (jm.find()) {
                    JavaScan other = tree.byType.get(jm.group(1));
                    if (other == null) continue;
                    Matcher bm = BANNERS.matcher(line);
                    while (bm.find()) {
                        int said = number(bm.group(1));
                        int has = topBanners(other).size();
                        if (said >= 0 && !quoted(line, bm.start()) && said != has)
                            out.add(new Finding(Cat.STATED_COUNT, rel(doc), no,
                                    "says " + other.fileName + " has \"" + bm.group() + "\" and it has " + has));
                    }
                }
            }
        }
    }

    /**
     * Whether a stated count is a claim the file bears out. A header that names
     * the banners it is counting is counting those and not the file: "the
     * eleven banners from FINANCES to BORROW" is right about a file with twelve
     * of them, and "the two banners, THE HISTORY SCREEN and THE RECORD" is
     * right about a file with three. So the number is believed when it matches
     * the file's total, the number of its own banners the sentence names, or
     * the run of banners those names span.
     */
    static boolean agrees(int said, int banners, String sentence, JavaScan f) {
        if (said == banners) return true;
        List<JavaScan.Section> tops = topBanners(f);
        String hay = flat(sentence).toUpperCase();
        int first = -1, last = -1, named = 0;
        for (int i = 0; i < tops.size(); i++) {
            String title = flat(tops.get(i).title).toUpperCase();
            if (title.length() < 5 || !hay.contains(title)) continue;
            named++;
            if (first < 0) first = i;
            last = i;
        }
        if (named == 0) return false;
        if (said == named) return true;
        return said == last - first + 1;
    }

    /**
     * Whether a position sits inside a quotation. A count in quotation marks is
     * being quoted, not asserted - this tool's own header says "the twelve
     * banners" as the example of the defect, and docs-pass.md names the same
     * three counts. An odd number of quotes before it means it is inside one.
     */
    static boolean quoted(String text, int at) {
        int n = 0;
        for (int i = 0; i < at && i < text.length(); i++) if (text.charAt(i) == '"') n++;
        return n % 2 == 1;
    }

    /** The sentence around a position: a full stop followed by a space ends one, a dot inside a name does not. */
    static String sentence(String text, int at) {
        int from = 0;
        for (int i = at - 1; i > 0; i--) {
            char c = text.charAt(i);
            if ((c == '.' || c == ';' || c == '!' || c == '?') && Character.isWhitespace(text.charAt(i + 1))) { from = i + 1; break; }
        }
        int to = text.length();
        for (int i = at; i < text.length() - 1; i++) {
            char c = text.charAt(i);
            if ((c == '.' || c == ';' || c == '!' || c == '?') && Character.isWhitespace(text.charAt(i + 1))) { to = i; break; }
        }
        return text.substring(from, to);
    }

    /**
     * One to ninety-nine in words, and the tens on their own. Past thirty because
     * the harness count is written out ("fifty-six harnesses") and a scanner that
     * stopped at thirty would read that as six.
     */
    static Map<String, Integer> numbers() {
        String[] ones = { "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
                "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen" };
        String[] tens = { "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety" };
        Map<String, Integer> m = new LinkedHashMap<>();
        for (int i = 0; i < ones.length; i++) m.put(ones[i], i);
        for (int t = 0; t < tens.length; t++) {
            m.put(tens[t], 20 + t * 10);
            for (int i = 1; i <= 9; i++) {
                m.put(tens[t] + "-" + ones[i], 20 + t * 10 + i);
                m.put(tens[t] + " " + ones[i], 20 + t * 10 + i);
            }
        }
        return m;
    }

    static String alternation() {
        List<String> words = new ArrayList<>(NUMBERS.keySet());
        words.sort((a, b) -> b.length() - a.length());          // "twenty-one" before "twenty"
        StringBuilder sb = new StringBuilder("\\d{1,4}");
        for (String w : words) sb.append('|').append(w.replace(" ", "\\s+"));
        return sb.toString();
    }

    /** The number a matched word or figure says, or -1 if it is neither. */
    static int number(String said) {
        String s = said.toLowerCase().replaceAll("\\s+", " ").trim();
        Integer word = NUMBERS.get(s);
        if (word != null) return word;
        word = NUMBERS.get(s.replace(' ', '-'));
        if (word != null) return word;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; }
    }

    /* ------------------------------------------------------------------
       4. UNRESOLVED MEMBER
       ------------------------------------------------------------------ */

    /** Names that appear in prose here and belong to the JDK or JavaFX, not to the tree. */
    static final Set<String> LIBRARY = Set.of(
            "println", "runLater", "equals", "hashCode", "toString", "compareTo", "format", "get", "put", "add",
            "remove", "size", "isEmpty", "main", "run", "apply", "accept", "test", "layout", "applyCss",
            "setVvalue", "getVvalue", "setStyle", "getChildren", "sort", "max", "min", "abs", "signum", "floor",
            "ceil", "round", "sqrt", "pow", "stream", "filter", "map", "collect", "forEach", "values", "ordinal",
            "name", "length", "substring", "split", "trim", "contains", "startsWith", "endsWith", "indexOf");

    /** Names written as an example of a member rather than as one: "see foo()" is the shape, not a claim. */
    static final Set<String> PLACEHOLDERS = Set.of("foo", "bar", "baz", "doSomething");

    /**
     * A member is named in prose as name() - the empty parentheses, with nothing
     * between them and no space before them, are what makes it a member and not
     * an English word with a parenthesis after it. "paid separately (see below)"
     * is prose; "see advise()" is a claim about the tree.
     */
    static final Pattern CALL = Pattern.compile("\\b([A-Z][A-Za-z0-9_]*)\\.([a-zA-Z][A-Za-z0-9_]*)\\(\\)|\\b([a-z][A-Za-z0-9_]*)\\(\\)");

    static void unresolvedMember(SourceTree tree, Index ix, List<Finding> out) {
        for (JavaScan f : tree.files) {
            for (JavaScan.Tok t : f.toks) {
                if (t.kind != JavaScan.T.COMMENT) continue;
                Matcher m = CALL.matcher(t.text);
                while (m.find()) {
                    String owner = m.group(1), name = m.group(2) != null ? m.group(2) : m.group(3);
                    if (name == null || LIBRARY.contains(name) || PLACEHOLDERS.contains(name)) continue;
                    int line = t.line + newlines(t.text, m.start());
                    if (owner != null) {
                        Set<String> own = ix.byOwner.get(owner);
                        if (own == null || own.contains(name)) continue;       // not a tree type, or it is declared there
                        if (!ix.declared.contains(name) && !ix.mentioned.contains(name)) {
                            out.add(new Finding(Cat.UNRESOLVED_MEMBER, path(f), line,
                                    "says " + owner + "." + name + "() and nothing in the tree declares " + name));
                        } else {
                            out.add(new Finding(Cat.UNRESOLVED_MEMBER, path(f), line,
                                    "says " + owner + "." + name + "() and " + owner + " does not declare " + name
                                    + " (supertypes not chased - it may be inherited)"));
                        }
                        continue;
                    }
                    if (ix.declared.contains(name) || ix.mentioned.contains(name)) continue;
                    out.add(new Finding(Cat.UNRESOLVED_MEMBER, path(f), line,
                            "says " + name + "() and nothing in the tree declares or mentions it"));
                }
            }
        }
    }

    /* ------------------------------------------------------------------
       5. MAP OLDER THAN SOURCE
       ------------------------------------------------------------------ */

    static void mapOlderThanSource(SourceTree tree, List<Finding> out) throws IOException {
        Path mapDir = tree.docs.resolve("map");
        Set<String> mapped = new LinkedHashSet<>();
        for (JavaScan f : tree.files) {
            Path page = mapDir.resolve(f.typeName + ".md");
            mapped.add(f.typeName + ".md");
            if (!Files.exists(page)) {
                out.add(new Finding(Cat.MAP_OLDER, path(f), 1, "no map page: " + page + " was never generated"));
                continue;
            }
            long src = Files.getLastModifiedTime(f.path).toMillis();
            long doc = Files.getLastModifiedTime(page).toMillis();
            if (doc < src) out.add(new Finding(Cat.MAP_OLDER, rel(page), 1,
                    "older than " + path(f) + " by " + ((src - doc) / 60000) + " minutes"));
        }
        if (!Files.isDirectory(mapDir)) return;
        try (Stream<Path> s = Files.list(mapDir)) {
            for (Path p : s.filter(q -> q.toString().endsWith(".md")).sorted().toList()) {
                String name = p.getFileName().toString();
                if (name.equals("README.md") || mapped.contains(name)) continue;
                out.add(new Finding(Cat.MAP_OLDER, rel(p), 1,
                        "a map page whose source is gone - nothing in the tree is called "
                        + name.substring(0, name.length() - 3)));
            }
        }
    }

    /* ------------------------------------------------------------------
       SMALL CHANGE
       ------------------------------------------------------------------ */

    static String path(JavaScan f) { return f.path.toString().replace('\\', '/'); }

    static String rel(Path p) { return p.toString().replace('\\', '/'); }

    static String flat(String s) { return s.replace("\r", " ").replace("\n", " ").replaceAll("\\s+", " ").trim(); }

    static int newlines(String s, int upTo) {
        int n = 0;
        for (int i = 0; i < upTo && i < s.length(); i++) if (s.charAt(i) == '\n') n++;
        return n;
    }
}
