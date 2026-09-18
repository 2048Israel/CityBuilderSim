package ham.citybuildersim.tools;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Every dial in the game on one page: docs/dials.md lists each static final
 * constant in the tree with its value, its line, and the sentence above it.
 *
 * WHY. The questions an assistant asks most often are "what is
 * DEPOSITS_PER_BRANCH", "where is the leverage set", "is there already a
 * constant for this". Each one used to cost a grep and a read; here they
 * cost a glance, and the page doubles as the balance sheet of assumptions -
 * a designer can read down it and see every number somebody chose.
 *
 * Harness constants are listed too, on their own shelf at the end, because
 * a check pinned to a literal is the thing README rule 2 warns about and
 * this is where it shows.
 */
public final class Dials {

    public static void main(String[] args) throws IOException {
        run(SourceTree.open());
    }

    static void run(SourceTree tree) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# The dials\n\n");
        sb.append("Generated ").append(SourceTree.stamp()).append(" by `ham.citybuildersim.tools.Dials` - every `static final` constant in the tree, with the comment that explains it. Do not edit; regenerate with `Regenerate maps.bat`.\n\n");
        int total = 0;
        for (Map.Entry<String, List<JavaScan>> area : tree.byArea().entrySet()) {
            int inArea = 0;
            for (JavaScan f : area.getValue()) inArea += f.constants().size();
            if (inArea == 0) continue;
            sb.append("## ").append(area.getKey()).append(" (").append(inArea).append(" constants)\n\n");
            for (JavaScan f : area.getValue()) {
                List<JavaScan.Member> cs = f.constants();
                if (cs.isEmpty()) continue;
                total += cs.size();
                sb.append("### ").append(f.fileName).append(" ([map](map/").append(f.typeName).append(".md))\n\n");
                sb.append("| line | constant | value | says |\n|---:|---|---|---|\n");
                for (JavaScan.Member m : cs) {
                    String says = !m.doc.isEmpty() ? m.doc : m.trailing;
                    sb.append("| ").append(m.line).append(" | ").append(SourceTree.code(m.qualifiedName())).append(" | ")
                      .append(SourceTree.code(SourceTree.clip(m.value, 90))).append(" | ")
                      .append(SourceTree.cell(SourceTree.clip(says, 200))).append(" |\n");
                }
                sb.append('\n');
            }
        }
        sb.insert(sb.indexOf("\n\n", sb.indexOf("Generated")) + 2, String.format("**%,d constants in %d files.**%n%n", total, tree.files.size()));
        tree.write("dials.md", sb.toString());
    }
}
