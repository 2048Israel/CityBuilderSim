package ham.citybuildersim.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The month as a numbered list: docs/month-order.md walks the top-level
 * statements of the methods that make up a month, in order, each with its
 * line and the comment above it, and says which method in the tree each
 * call lands in.
 *
 * WHY. README says "the month is a sequence, not a set", and that most of
 * the hard bugs in this project's history were a line that made it into one
 * phase and not the other. The sequence lives in Game.nextMonth() (540
 * lines) and SimulationEngine.simulateMonth(), between paragraphs of prose
 * that explain why each line is where it is. The prose is worth keeping and
 * impossible to skim, so this is the skim: the lines alone, numbered, with
 * one sentence each and a pointer to where each call goes. A change that
 * has to land "after the wages are paid and before the shops buy" starts
 * here.
 *
 * Which methods are listed is a property, -Dmonth.methods=Class.method,...
 * The default is the month's spine.
 */
public final class MonthOrder {

    static final String DEFAULT = "Game.nextMonth,Game.startOfMonthUpdate,SimulationEngine.simulateMonth,"
            + "SimulationEngine.updatePopulation,Game.advanceDemographics,SimulationEngine.updateEconomy,"
            + "SimulationEngine.updateServices,Game.run";

    public static void main(String[] args) throws IOException {
        run(SourceTree.open());
    }

    static void run(SourceTree tree) throws IOException {
        String[] wanted = System.getProperty("month.methods", DEFAULT).split(",");
        StringBuilder sb = new StringBuilder();
        sb.append("# The order the month runs in\n\n");
        sb.append("Generated ").append(SourceTree.stamp()).append(" by `ham.citybuildersim.tools.MonthOrder` - the top-level statements of each method below, in order, with the comment above each and where each call goes. Do not edit; regenerate with `Regenerate maps.bat`.\n\n");
        sb.append("`Game.nextMonth()` is what the clock and the button call; it does the bookkeeping either side of `SimulationEngine.simulateMonth()`, which is the spine. Read the two together. A statement shown as `if (...) {` or `for (...) {` is a whole block; open the source at that line for its body.\n\n");
        for (String w : wanted) {
            String[] parts = w.trim().split("\\.");
            if (parts.length != 2) continue;
            JavaScan f = tree.byType.get(parts[0]);
            if (f == null) { sb.append("## ").append(w).append("\n\n_(no such file)_\n\n"); continue; }
            JavaScan.Member m = f.method(parts[1]);
            if (m == null) { sb.append("## ").append(w).append("\n\n_(no such method in ").append(f.fileName).append(")_\n\n"); continue; }
            sb.append("## ").append(w).append("() - ").append(f.fileName).append(" lines ").append(m.line).append("-").append(m.endLine)
              .append(" (").append(m.length()).append(" lines)\n\n");
            if (!m.doc.isEmpty()) sb.append("> ").append(m.doc).append("\n\n");
            List<Statement> stmts = statements(f, m);
            Map<String, String> fieldTypes = fieldTypes(f);
            int k = 0;
            for (Statement s : stmts) {
                k++;
                sb.append(k).append(". **L").append(s.line).append("** ").append(SourceTree.code(SourceTree.clip(s.text, 120)));
                String where = targets(tree, f, fieldTypes, s);
                if (!where.isEmpty()) sb.append(" → ").append(where);
                if (!s.comment.isEmpty()) sb.append("  \n   _").append(SourceTree.cell(SourceTree.clip(s.comment, 200))).append("_");
                sb.append('\n');
            }
            sb.append('\n');
        }
        tree.write("month-order.md", sb.toString());
    }

    static final class Statement {
        int line; String text = ""; String comment = ""; final List<JavaScan.Tok> toks = new ArrayList<>();
    }

    /** The top-level statements of a method body. */
    static List<Statement> statements(JavaScan f, JavaScan.Member m) {
        List<Statement> out = new ArrayList<>();
        int[] range = f.bodyRange(m);
        if (range == null) return out;
        List<JavaScan.Tok> toks = f.toks;
        int i = range[0] + 1, end = range[1];
        int d = 0, p = 0;
        Statement cur = null;
        JavaScan.Tok pendingComment = null;
        while (i < end) {
            JavaScan.Tok t = toks.get(i);
            if (t.kind == JavaScan.T.COMMENT) {
                if (cur == null) pendingComment = t;
                i++; continue;
            }
            if (cur == null) {
                cur = new Statement();
                cur.line = t.line;
                List<String> src = f.lines(t.line, t.line);
                cur.text = src.isEmpty() ? t.text : src.get(0).trim();
                if (pendingComment != null && pendingComment.endLine >= t.line - 2) cur.comment = JavaScan.firstSentence(pendingComment.text);
                pendingComment = null;
            }
            cur.toks.add(t);
            boolean ends = false;
            if (t.is("(")) p++;
            else if (t.is(")")) p--;
            else if (t.is("{")) d++;
            else if (t.is("}")) {
                d--;
                if (d == 0 && p == 0) {
                    JavaScan.Tok next = peek(toks, i + 1, end);
                    if (next == null) ends = true;
                    else if (next.ident("else") || next.ident("catch") || next.ident("finally") || next.ident("while")) ends = false;
                    else if (next.is(";")) { cur.toks.add(next); i++; ends = true; }
                    else ends = true;
                }
            }
            else if (t.is(";") && d == 0 && p == 0) ends = true;
            if (ends) { out.add(cur); cur = null; }
            i++;
        }
        if (cur != null) out.add(cur);
        return out;
    }

    static JavaScan.Tok peek(List<JavaScan.Tok> toks, int from, int end) {
        for (int j = from; j < end; j++) if (toks.get(j).kind != JavaScan.T.COMMENT) return toks.get(j);
        return null;
    }

    /** field name -> declared type, from the file's field declarations, so "game.foo()" can be followed. */
    static Map<String, String> fieldTypes(JavaScan f) {
        Map<String, String> out = new HashMap<>();
        for (JavaScan.Member m : f.members) {
            if (m.kind != JavaScan.Kind.FIELD && m.kind != JavaScan.Kind.CONSTANT) continue;
            String[] w = m.signature.replaceAll("<[^>]*>", "").split("\\s+");
            if (w.length >= 2) out.put(m.name, w[w.length - 2].replace("[]", ""));
        }
        return out;
    }

    /** Where the calls in a statement go, as "Class.method (Lnnn)" links, for calls the tree can resolve. */
    static String targets(SourceTree tree, JavaScan here, Map<String, String> fieldTypes, Statement s) {
        StringBuilder sb = new StringBuilder();
        List<JavaScan.Tok> t = s.toks;
        for (int i = 0; i + 1 < t.size(); i++) {
            if (t.get(i).kind != JavaScan.T.IDENT || !t.get(i + 1).is("(")) continue;
            String name = t.get(i).text;
            JavaScan target = null;
            if (i >= 2 && t.get(i - 1).is(".") && t.get(i - 2).kind == JavaScan.T.IDENT) {
                String q = t.get(i - 2).text;
                if (tree.byType.containsKey(q)) target = tree.byType.get(q);                 // static: Class.method
                else if (fieldTypes.containsKey(q)) target = tree.byType.get(fieldTypes.get(q)); // field: x.method
                else if (q.equals("this")) target = here;
                else if (q.equals("game") || q.equals("g")) target = tree.byType.get("Game");
                else continue;                                                                  // a local or a chain; cannot follow
            } else if (i >= 1 && t.get(i - 1).is(".")) {
                continue;                                                                        // a chained call
            } else {
                target = here;                                                                   // an unqualified call
            }
            if (target == null) continue;
            JavaScan.Member m = target.method(name);
            if (m == null) continue;
            String link = "[" + target.typeName + "." + name + "](map/" + target.typeName + ".md) (L" + m.line + ")";
            if (sb.indexOf(link) >= 0) continue;
            if (sb.length() > 0) sb.append(", ");
            sb.append(link);
        }
        return sb.toString();
    }
}
