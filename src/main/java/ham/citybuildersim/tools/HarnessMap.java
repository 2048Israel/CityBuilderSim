package ham.citybuildersim.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * What every harness asserts, in its own words: docs/harnesses.md.
 *
 * WHY. Fifty-seven harnesses, 28,000 lines, and the question that matters
 * before any change is "what already checks this?" - because README rule 4
 * says a check that fails is the finding, and you cannot honour that rule
 * for a check you did not know existed. Each harness labels its assertions
 * in prose ("the catalogue has both kitchens", "...and the same fact the
 * other way up") and groups them under printed section headings; this
 * lifts those labels out, so the whole suite reads as a table of contents
 * of what the model promises. It also says which harnesses mention which
 * model class, and which harness files AllChecks does not run.
 *
 * The labels are found by the helpers a harness declares - any
 * "static void NAME(String label, ...)" in the file - plus the usual names.
 * A label built from an expression ("t.getName() + ...") is not a literal
 * and is not listed; the count under each harness is of labelled calls.
 */
public final class HarnessMap {

    static final Set<String> HELPERS = Set.of("assertTrue", "check", "close", "report", "assertEquals", "same",
            "within", "sameArray", "relative", "expect", "ok", "assertFalse", "near", "equal", "assertNear", "must");
    static final Pattern SECTION = Pattern.compile("^\\s*(?:\\\\n)?\\s*(?:---|===)+\\s*(.*?)\\s*(?:---|===)+\\s*(?:\\\\n)?\\s*$");

    public static void main(String[] args) throws IOException {
        run(SourceTree.open());
    }

    static void run(SourceTree tree) throws IOException {
        List<JavaScan> harnesses = tree.byArea().get("harnesses");
        Set<String> registered = new LinkedHashSet<>();
        JavaScan all = tree.byType.get("AllChecks");
        if (all != null) for (JavaScan.Literal l : all.literals) if (l.text.endsWith("Check") || l.text.equals("LongPlaytest")) registered.add(l.text);

        StringBuilder sb = new StringBuilder();
        sb.append("# The harnesses\n\n");
        sb.append("Generated ").append(SourceTree.stamp()).append(" by `ham.citybuildersim.tools.HarnessMap` - every labelled assertion in every harness, under the section it prints. Do not edit; regenerate with `Regenerate maps.bat`.\n\n");

        // the reverse index first: which harnesses mention which model class
        Map<String, List<String>> readers = new TreeMap<>();
        for (JavaScan f : tree.files) {
            String a = tree.area(f);
            if (!a.equals("model") && !a.equals("sectors")) continue;
            List<String> who = new ArrayList<>();
            for (JavaScan h : harnesses) if (!h.typeName.equals("AllChecks") && h.identifiers.contains(f.typeName)) who.add(h.typeName);
            readers.put(f.typeName, who);
        }
        List<String> unregistered = new ArrayList<>();
        for (JavaScan h : harnesses) if (!h.typeName.equals("AllChecks") && !registered.contains(h.typeName)) unregistered.add(h.typeName);
        int totalLabels = 0;
        StringBuilder body = new StringBuilder();
        for (JavaScan h : harnesses) {
            if (h.typeName.equals("AllChecks")) continue;
            totalLabels += one(tree, h, body, registered.contains(h.typeName));
        }
        sb.append(String.format("**%d harness files, %,d labelled assertions.** AllChecks runs %d of them", harnesses.size() - (all == null ? 0 : 1), totalLabels, registered.size()));
        if (unregistered.isEmpty()) sb.append(".\n\n");
        else sb.append("; **not in AllChecks:** ").append(String.join(", ", unregistered)).append(".\n\n");

        sb.append("## Which harnesses read which class\n\n");
        sb.append("A class nobody reads is a class nothing checks. Mentions by name, so a harness that reaches a class only through another is not counted.\n\n");
        sb.append("| class | harnesses that mention it |\n|---|---|\n");
        for (Map.Entry<String, List<String>> e : readers.entrySet()) {
            sb.append("| [").append(e.getKey()).append("](map/").append(e.getKey()).append(".md) | ")
              .append(e.getValue().isEmpty() ? "**none**" : String.join(", ", e.getValue())).append(" |\n");
        }
        sb.append('\n');
        sb.append(body);
        tree.write("harnesses.md", sb.toString());
    }

    /** One harness's sections and labels; returns the number of labels. */
    static int one(SourceTree tree, JavaScan h, StringBuilder sb, boolean registered) {
        Set<String> helpers = new LinkedHashSet<>(HELPERS);
        for (JavaScan.Member m : h.methods()) {
            if (m.signature.matches(".*\\b(void|boolean)\\s+\\w+\\(String (label|name|what|title|text)\\b.*")) helpers.add(m.name);
        }
        // events in line order: sections (printed or bannered) and labels
        List<Object[]> events = new ArrayList<>();
        for (JavaScan.Literal l : h.literals) {
            if (l.before.equals("println") || l.before.equals("printf") || l.before.equals("print")) {
                Matcher mm = SECTION.matcher(l.text);
                if (mm.matches() && !mm.group(1).isBlank()) events.add(new Object[]{ l.line, "S", mm.group(1) });
            } else if (helpers.contains(l.before)) {
                events.add(new Object[]{ l.line, "L", l.text });
            }
        }
        for (JavaScan.Section s : h.sections) if (s.depth >= 2) events.add(new Object[]{ s.line, "S", s.title });
        events.sort((a, b) -> Integer.compare((int) a[0], (int) b[0]));

        int labels = 0;
        for (Object[] e : events) if (e[1].equals("L")) labels++;
        sb.append("## ").append(h.fileName).append(" - ").append(labels).append(" labelled assertions")
          .append(registered ? "" : " - **not run by AllChecks**").append("\n\n");
        String header = SourceTree.headerProse(h);
        int shown = 0;
        for (String l : header.split("\n")) {
            if (shown++ >= 12) { sb.append("> ...\n"); break; }
            sb.append("> ").append(l).append('\n');
        }
        sb.append('\n');
        if (events.isEmpty()) { sb.append("_(no printed sections or labelled assertions found)_\n\n"); return 0; }
        if (labels == 0) sb.append("_(this harness does not label its checks through a helper - it prints its findings; read its header and its sections)_\n\n");
        int lastSectionLine = -1;
        boolean lastWasSection = false;
        for (Object[] e : events) {
            int line = (int) e[0];
            if (e[1].equals("S")) {
                if (lastWasSection && line - lastSectionLine <= 2) continue;   // a banner and the println under it are one section
                lastSectionLine = line;
                lastWasSection = true;
                sb.append("- **L").append(line).append(" ").append(SourceTree.cell(SourceTree.clip((String) e[2], 140))).append("**\n");
            } else {
                lastWasSection = false;
                sb.append("  - L").append(line).append(" ").append(SourceTree.cell(SourceTree.clip((String) e[2], 160))).append('\n');
            }
        }
        sb.append('\n');
        return labels;
    }
}
