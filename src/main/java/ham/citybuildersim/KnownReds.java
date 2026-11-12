package ham.citybuildersim;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The common red-line judge for the suite, batch checks and publisher.
 * WHY (GR2): temp gate scripts held approvals the builder could never read.
 * This class also runs in Java source-file mode before a checkout is built.
 */
public class KnownReds {
    static final Path PARTS = Path.of("docs/notes/known-reds.txt");
    private static final Pattern HEADER = Pattern.compile("^([A-Za-z0-9_]+) +FAILED - exit (-?[0-9]+).*");
    private static final Pattern TOTAL = Pattern.compile("^([0-9]+) harness(?:es)? run in .*, ([0-9]+) failed$");

    static Set<String> read(Path file) throws IOException {
        if (!Files.isRegularFile(file)) return null;
        Set<String> lines = new LinkedHashSet<>();
        for (String line : new String(Files.readAllBytes(file), StandardCharsets.UTF_8).split("\\R")) {
            if (!line.isBlank() && !line.startsWith("#")) lines.add(line);
        }
        return lines;
    }

    private static String harness(String line) { return line.substring(0, line.indexOf(" | ")); }
    private static boolean count(String line) { return line.matches("[^ ]+ \\| (exit -?[0-9]+|[0-9]+ FAILED)"); }

    /** Exact gate lines and five-column, harness-scoped parts share this representation. */
    static boolean matches(String line, String entry) {
        if (entry.startsWith("! ")) return false;
        if (entry.startsWith("@once ")) return line.contains(entry.substring(6));
        if (line.equals(entry)) return true;
        String[] cells = entry.split(" \\| ", -1);
        if (cells.length != 5 || !cells[3].matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) return false;
        return harness(line).equals(cells[0]) && line.substring(line.indexOf(" | ") + 3).contains(cells[1]);
    }

    /** Counts follow a known assertion only when every assertion of that harness is known. */
    static List<String> fresh(List<String> reds, Set<String> entries) {
        Set<String> good = new LinkedHashSet<>(), bad = new LinkedHashSet<>();
        List<String> fresh = new ArrayList<>();
        for (String line : reds) {
            if (count(line)) continue;
            boolean removed = entries.stream().filter(e -> e.startsWith("! ")).anyMatch(e -> matches(line, e.substring(2)));
            boolean approvedOnce = entries.stream().filter(e -> e.startsWith("@once ")).anyMatch(e -> matches(line, e));
            if (approvedOnce || (!removed && entries.stream().anyMatch(e -> matches(line, e)))) good.add(harness(line));
            else { fresh.add(line); bad.add(harness(line)); }
        }
        for (String line : reds) {
            if (count(line) && (!good.contains(harness(line)) || bad.contains(harness(line)))
                    && entries.stream().filter(e -> e.startsWith("@once ")).noneMatch(e -> matches(line, e))) fresh.add(line);
        }
        return reds.stream().filter(fresh::contains).toList();
    }

    /** Reads the public -f log, including logs made before line tags existed. */
    static List<String> reds(List<String> log) {
        List<String> reds = new ArrayList<>();
        String name = null;
        boolean tail = false;
        for (String line : log) {
            Matcher header = HEADER.matcher(line);
            if (header.matches()) {
                name = header.group(1); tail = false;
                reds.add(name + " | exit " + header.group(2));
                continue;
            }
            if (!line.startsWith("    ")) { name = null; continue; }
            if (name == null || tail) continue;
            String text = line.strip().replaceFirst("^\\[(known|NEW)\\] +", "");
            if (text.equals("(no FAIL line; its last lines:)")) { text = "(no FAIL line)"; tail = true; }
            if (text.matches("---\\s.*\\s---") || text.isBlank()) continue;
            text = text.replaceFirst("^(\\.\\.\\. [0-9]+ failing lines in all): .*", "$1");
            reds.add(name + " | " + text);
        }
        return reds;
    }

    /** Returns batch-check's status and writes the evidence that ship.sh retains. */
    static int judge(Path suite, Set<String> entries, Path out, boolean rejudge) throws Exception {
        List<String> log = Files.readAllLines(suite, StandardCharsets.UTF_8);
        List<String> reds = reds(log), fresh = fresh(reds, entries);
        Files.createDirectories(out);
        Files.write(out.resolve("reds.txt"), reds);
        Files.write(out.resolve("new-reds.txt"), fresh);
        Files.write(out.resolve("used-reds.txt"), entries.stream().filter(e -> e.split(" \\| ", -1).length == 5)
                .filter(e -> reds.stream().filter(r -> !count(r)).anyMatch(r -> matches(r, e))).toList());
        int ran = -1, failed = -1;
        for (String line : log) {
            Matcher total = TOTAL.matcher(line);
            if (total.matches()) { ran = Integer.parseInt(total.group(1)); failed = Integer.parseInt(total.group(2)); }
        }
        long named = reds.stream().filter(r -> r.matches("[^ ]+ \\| exit -?[0-9]+")).count();
        if (ran <= 0 || failed != named || named > ran) {
            System.out.println("suite     FAILED - incomplete log or unreadable summary/headers: " + suite);
            return 1;
        }
        if (!rejudge && log.stream().anyMatch(l -> l.matches(".*  (known red|NEW red: [0-9]+ of [0-9]+ red lines)$"))) {
            int tagged = 0;
            for (String line : log) {
                Matcher tag = Pattern.compile(".*  NEW red: ([0-9]+) of [0-9]+ red lines$").matcher(line);
                if (tag.matches()) tagged += Integer.parseInt(tag.group(1));
            }
            if (tagged != fresh.size()) {
                System.out.println("suite     FAILED - the two judges disagree on NEW red counts");
                return 1;
            }
        }
        if (failed == 0) { System.out.println("suite     OK - " + ran + " harnesses"); return 0; }
        if (fresh.isEmpty()) { System.out.println("suite     known red only - " + failed + " harnesses"); return 3; }
        System.out.println("suite     FAILED - NEW red: " + fresh.size() + " line(s)");
        fresh.stream().limit(30).forEach(l -> System.out.println("    " + l));
        return 4;
    }

    public static void main(String[] args) throws Exception {
        // suite, combined exact/part entries, output directory, optional rejudge
        Set<String> entries = read(Path.of(args[1]));
        System.exit(judge(Path.of(args[0]), entries == null ? Set.of() : entries, Path.of(args[2]), args.length > 3));
    }
}
