package ham.citybuildersim.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * The whole source tree, scanned once, and the little that every generator
 * shares: where the sources are, where the documents go, what area a file
 * belongs to, and how to write a document without leaving a half-written one
 * behind.
 *
 * Run any of the tools from the repository root. They default to
 * src/main/java for the sources and docs/ for the output, and both can be
 * overridden with -Dsrc=... and -Ddocs=... so the cloud loop, which keeps
 * the tree somewhere else, can point them at its own copy.
 */
public final class SourceTree {

    public final Path root;
    public final Path docs;
    public final List<JavaScan> files = new ArrayList<>();
    public final Map<String, JavaScan> byType = new TreeMap<>();

    public SourceTree(Path root, Path docs) throws IOException {
        this.root = root;
        this.docs = docs;
        try (Stream<Path> s = Files.walk(root)) {
            List<Path> paths = s.filter(p -> p.toString().endsWith(".java")).sorted().toList();
            for (Path p : paths) {
                JavaScan scan = JavaScan.read(p);
                // a file that declares no type is a stub left where a class used to be
                // (a cloud session cannot delete on the PC); it is not part of the tree
                if (scan.members.stream().noneMatch(m -> m.kind == JavaScan.Kind.TYPE)) continue;
                files.add(scan);
                byType.put(scan.typeName, scan);
            }
        }
        files.sort(Comparator.comparing((JavaScan f) -> f.typeName));
    }

    public static SourceTree open() throws IOException {
        Path src = Paths.get(System.getProperty("src", "src/main/java"));
        Path docs = Paths.get(System.getProperty("docs", "docs"));
        if (!Files.isDirectory(src)) {
            throw new IOException("no source tree at " + src.toAbsolutePath()
                    + " - run from the repository root, or pass -Dsrc=<path to src/main/java>");
        }
        return new SourceTree(src, docs);
    }

    /** The file's path relative to the source root, with forward slashes. */
    public String relative(JavaScan f) {
        return root.relativize(f.path).toString().replace('\\', '/');
    }

    /**
     * Which shelf a file sits on. The tree is one flat package plus sectors/,
     * ui/ and tools/, so the shelf is read off the path and the name: sectors
     * live in sectors/, the interface in ui/ (plus its launcher, which has to
     * stay in the root package for the jar's main class), harnesses end in
     * Check (or are the runner and the playtest), and the rest is the model.
     */
    public String area(JavaScan f) {
        String rel = relative(f);
        if (rel.contains("/sectors/")) return "sectors";
        if (rel.contains("/tools/")) return "tools";
        if (rel.contains("/ui/")) return "interface";
        String n = f.typeName;
        if (n.endsWith("Check") || n.equals("AllChecks") || n.equals("LongPlaytest")) return "harnesses";
        if (n.equals("CityBuilderSim")) return "interface";
        return "model";
    }

    public static final String[] AREAS = { "model", "sectors", "interface", "harnesses", "tools" };

    /** Files grouped by area, in the order AREAS lists them. */
    public Map<String, List<JavaScan>> byArea() {
        Map<String, List<JavaScan>> out = new LinkedHashMap<>();
        for (String a : AREAS) out.put(a, new ArrayList<>());
        for (JavaScan f : files) out.get(area(f)).add(f);
        return out;
    }

    /** Which files mention this type by name (other than itself). */
    public List<JavaScan> usedBy(JavaScan f) {
        List<JavaScan> out = new ArrayList<>();
        for (JavaScan g : files) if (g != f && g.identifiers.contains(f.typeName)) out.add(g);
        return out;
    }

    /** The types in the tree this file mentions, most mentioned first. */
    public List<Map.Entry<String, Integer>> uses(JavaScan f) {
        List<Map.Entry<String, Integer>> out = new ArrayList<>();
        for (Map.Entry<String, Integer> e : f.identifierCounts.entrySet()) {
            if (!e.getKey().equals(f.typeName) && byType.containsKey(e.getKey())) out.add(e);
        }
        out.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return out;
    }

    /** Write a document, LF line endings, replacing whatever was there. */
    public void write(String relativePath, String content) throws IOException {
        Path target = docs.resolve(relativePath);
        Files.createDirectories(target.getParent());
        Path tmp = target.resolveSibling(target.getFileName() + ".part");
        Files.writeString(tmp, content.replace("\r\n", "\n"), StandardCharsets.UTF_8);
        Files.move(tmp, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        System.out.println("wrote " + target + " (" + content.length() + " chars)");
    }

    /* ------------------------------------------------------------------
       MARKDOWN SMALL CHANGE
       ------------------------------------------------------------------ */

    public static String cell(String s) {
        return s == null ? "" : s.replace("|", "\\|").replace("\r", "").replace("\n", " ");
    }

    public static String code(String s) {
        if (s == null || s.isEmpty()) return "";
        return "`" + s.replace("`", "'").replace("|", "\\|") + "`";
    }

    public static String clip(String s, int max) {
        if (s == null) return "";
        s = s.replace("\r", "").replace("\n", " ").trim();
        return s.length() <= max ? s : s.substring(0, Math.max(0, max - 3)) + "...";
    }

    public static String stamp() {
        return java.time.LocalDate.now().toString();
    }

    /** The class header with the javadoc tags dropped, or a stand-in. */
    public static String headerProse(JavaScan f) {
        StringBuilder sb = new StringBuilder();
        for (String l : f.header.split("\n")) {
            if (l.trim().startsWith("@")) continue;
            sb.append(l).append('\n');
        }
        String s = sb.toString().strip();
        return s.isEmpty() ? "(no class header - the file explains itself in its section banners)" : s;
    }
}
