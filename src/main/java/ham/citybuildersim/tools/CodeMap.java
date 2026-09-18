package ham.citybuildersim.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The code map: docs/map/README.md, one row per file, and docs/map/NAME.md
 * for every file, listing its banner sections and every member with the
 * line it starts on.
 *
 * WHY. UserInterface.java is 25,000 lines and Game.java is 8,000. An
 * assistant that has to change the bank screen cannot read either; what it
 * can do is open docs/map/UserInterface.md, find "THE BANK" at line 7120
 * with its forty methods listed under it, and read exactly those. The map is
 * the table of contents the files never had, kept outside them so the files
 * do not have to carry it.
 *
 * Run from the repository root: java -cp target/classes ham.citybuildersim.tools.CodeMap
 * or let Maps run all four generators at once. Regenerate after every batch;
 * the map is only as current as its last run, and the date at the top says
 * when that was.
 */
public final class CodeMap {

    public static void main(String[] args) throws IOException {
        SourceTree tree = SourceTree.open();
        run(tree);
    }

    static void run(SourceTree tree) throws IOException {
        int lines = 0, methods = 0, consts = 0;
        for (JavaScan f : tree.files) { lines += f.lineCount; methods += f.methods().size(); consts += f.constants().size(); }

        StringBuilder sb = new StringBuilder();
        sb.append("# The code map\n\n");
        sb.append("Generated ").append(SourceTree.stamp()).append(" by `ham.citybuildersim.tools.CodeMap` - do not edit; regenerate with `Regenerate maps.bat` (or `Maps`).\n\n");
        sb.append("**How to use it.** Open this file first. Every source file is one row here; open `docs/map/NAME.md` for the one you need and it lists that file's banner sections and every method with its line number, so you can read the forty lines that matter instead of the file. `docs/dials.md` has every constant, `docs/month-order.md` the order the month runs in, `docs/harnesses.md` what every check asserts.\n\n");
        sb.append(String.format("**The tree:** %d files, %,d lines, %,d methods, %,d constants.", tree.files.size(), lines, methods, consts));
        JavaScan gv = tree.byType.get("GameVersion");
        if (gv != null) {
            String version = "", format = "";
            for (JavaScan.Member c : gv.constants()) {
                if (c.name.equals("VERSION")) version = c.value;
                if (c.name.equals("SAVE_FORMAT")) format = c.value;
            }
            if (!version.isEmpty()) sb.append(" `GameVersion.VERSION` is ").append(version).append(", `SAVE_FORMAT` ").append(format).append(".");
        }
        sb.append("\n\n");

        for (Map.Entry<String, List<JavaScan>> area : tree.byArea().entrySet()) {
            if (area.getValue().isEmpty()) continue;
            sb.append("## ").append(area.getKey()).append(" (").append(area.getValue().size()).append(" files)\n\n");
            sb.append("| file | lines | methods | what it is | used by |\n|---|---:|---:|---|---:|\n");
            for (JavaScan f : area.getValue()) {
                String what = JavaScan.firstSentence("/*" + f.header + "*/");
                if (what.isEmpty() || what.startsWith("@")) {
                    what = f.sections.isEmpty() ? "" : "sections: " + f.sections.get(0).title
                            + (f.sections.size() > 1 ? ", " + f.sections.get(1).title + "..." : "");
                }
                sb.append("| [").append(f.fileName).append("](").append(f.typeName).append(".md) | ")
                  .append(String.format("%,d", f.lineCount)).append(" | ").append(f.methods().size()).append(" | ")
                  .append(SourceTree.cell(SourceTree.clip(what, 150))).append(" | ")
                  .append(tree.usedBy(f).size()).append(" |\n");
            }
            sb.append('\n');
        }
        tree.write("map/README.md", sb.toString());

        for (JavaScan f : tree.files) tree.write("map/" + f.typeName + ".md", one(tree, f));
    }

    static String one(SourceTree tree, JavaScan f) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(f.fileName).append(String.format(" - %,d lines · %d methods · %d constants · %s%n%n",
                f.lineCount, f.methods().size(), f.constants().size(), tree.area(f)));
        sb.append("`").append(tree.relative(f)).append("` - generated ").append(SourceTree.stamp()).append(" by CodeMap; line numbers are as of that run.\n\n");

        String header = SourceTree.headerProse(f);
        String[] hl = header.split("\n");
        int keep = Math.min(hl.length, 60);
        for (int i = 0; i < keep; i++) sb.append("> ").append(hl[i]).append('\n');
        if (hl.length > keep) sb.append("> ... (").append(hl.length - keep).append(" more lines in the source)\n");
        sb.append('\n');

        List<Map.Entry<String, Integer>> uses = tree.uses(f);
        if (!uses.isEmpty()) {
            sb.append("**Uses:** ");
            int shown = 0;
            for (Map.Entry<String, Integer> e : uses) {
                if (shown++ == 30) { sb.append("... and ").append(uses.size() - 30).append(" more"); break; }
                if (shown > 1) sb.append(", ");
                sb.append("[").append(e.getKey()).append("](").append(e.getKey()).append(".md) (").append(e.getValue()).append(")");
            }
            sb.append("\n\n");
        }
        List<JavaScan> usedBy = tree.usedBy(f);
        if (!usedBy.isEmpty()) {
            sb.append("**Used by (").append(usedBy.size()).append("):** ");
            for (int i = 0; i < usedBy.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("[").append(usedBy.get(i).typeName).append("](").append(usedBy.get(i).typeName).append(".md)");
            }
            sb.append("\n\n");
        }

        if (!f.sections.isEmpty()) {
            sb.append("## Sections\n\n| line | section |\n|---:|---|\n");
            for (JavaScan.Section s : f.sections) {
                String indent = s.level <= 1 ? "" : s.level == 2 ? "· " : "· · ";
                sb.append("| ").append(s.line).append(" | ").append(indent).append(SourceTree.cell(SourceTree.clip(s.title, 120))).append(" |\n");
            }
            sb.append('\n');
        }

        List<JavaScan.Member> enums = new ArrayList<>();
        List<JavaScan.Member> fields = new ArrayList<>();
        List<JavaScan.Member> consts = new ArrayList<>();
        List<JavaScan.Member> code = new ArrayList<>();
        for (JavaScan.Member m : f.members) {
            switch (m.kind) {
                case ENUM_CONSTANT -> enums.add(m);
                case FIELD -> fields.add(m);
                case CONSTANT -> consts.add(m);
                default -> code.add(m);
            }
        }

        if (!enums.isEmpty()) {
            sb.append("## Enum constants\n\n| line | constant | says |\n|---:|---|---|\n");
            for (JavaScan.Member m : enums)
                sb.append("| ").append(m.line).append(" | ").append(SourceTree.code(m.qualifiedName())).append(" | ").append(SourceTree.cell(m.doc)).append(" |\n");
            sb.append('\n');
        }
        if (!consts.isEmpty()) {
            sb.append("## Constants\n\n| line | constant | value | says |\n|---:|---|---|---|\n");
            for (JavaScan.Member m : consts)
                sb.append("| ").append(m.line).append(" | ").append(SourceTree.code(m.qualifiedName())).append(" | ")
                  .append(SourceTree.code(SourceTree.clip(m.value, 80))).append(" | ")
                  .append(SourceTree.cell(!m.doc.isEmpty() ? m.doc : m.trailing)).append(" |\n");
            sb.append('\n');
        }
        if (!fields.isEmpty()) {
            sb.append("## Fields (state)\n\n| line | field | says |\n|---:|---|---|\n");
            for (JavaScan.Member m : fields)
                sb.append("| ").append(m.line).append(" | ").append(SourceTree.code(SourceTree.clip(m.signature, 110))).append(" | ")
                  .append(SourceTree.cell(!m.doc.isEmpty() ? m.doc : m.trailing)).append(" |\n");
            sb.append('\n');
        }

        // methods, constructors, inner types and initializers, grouped under the member-level sections
        List<JavaScan.Section> groups = new ArrayList<>();
        for (JavaScan.Section s : f.sections) if (s.depth <= 1) groups.add(s);
        sb.append("## Methods, in file order").append(groups.isEmpty() ? "" : ", under their sections").append("\n\n");
        int gi = 0;
        boolean tableOpen = false;
        JavaScan.Section current = null;
        for (JavaScan.Member m : code) {
            while (gi < groups.size() && groups.get(gi).line <= m.line) { current = groups.get(gi); gi++;
                if (tableOpen) { sb.append('\n'); tableOpen = false; }
                int end = gi < groups.size() ? groups.get(gi).line - 1 : f.lineCount;
                sb.append("### ").append(SourceTree.clip(current.title, 120)).append(" (lines ").append(current.line).append("-").append(end).append(")\n\n");
            }
            if (!tableOpen) { sb.append("| line | len | member | says |\n|---:|---:|---|---|\n"); tableOpen = true; }
            String shown = m.kind == JavaScan.Kind.TYPE ? "**" + m.signature + "**" : m.signature;
            if (m.kind == JavaScan.Kind.TYPE) shown = "**type** " + SourceTree.code(SourceTree.clip(m.signature, 110));
            else shown = SourceTree.code(SourceTree.clip(m.signature, 130));
            String owner = m.owner.contains(".") || (!m.owner.isEmpty() && !m.owner.equals(f.typeName)) ? " _(in " + m.owner + ")_" : "";
            sb.append("| ").append(m.line).append(" | ").append(m.length()).append(" | ").append(shown).append(owner)
              .append(" | ").append(SourceTree.cell(m.doc)).append(" |\n");
        }
        if (code.isEmpty()) sb.append("_(no methods)_\n");
        sb.append('\n');
        return sb.toString();
    }
}
