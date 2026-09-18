package ham.citybuildersim.tools;

import java.io.IOException;
import java.util.List;

/**
 * Finds a member by name anywhere in the tree and, if asked, prints it.
 *
 *     java -cp target/classes ham.citybuildersim.tools.Where nextMonth
 *     java -cp target/classes ham.citybuildersim.tools.Where Game.nextMonth -print
 *     java -cp target/classes ham.citybuildersim.tools.Where "THE BANK"
 *
 * The first form lists every method, field, constant or type called that,
 * with file and line range. The second prints one of them, line-numbered,
 * so that a single method can be pasted into a conversation instead of the
 * file it sits in. The third - anything with a space or in capitals that is
 * not a member - searches the banner sections instead, so a screen in
 * UserInterface can be found by the title it was given.
 *
 * Matching is case-insensitive and on the whole name; a trailing * makes it
 * a prefix ("Where get*" is long).
 */
public final class Where {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("usage: Where <name | Class.name | \"SECTION TITLE\"> [-print]");
            return;
        }
        SourceTree tree = SourceTree.open();
        boolean print = false;
        String query = null;
        for (String a : args) { if (a.equals("-print") || a.equals("--print")) print = true; else query = a; }
        if (query == null) return;

        String cls = null, name = query;
        if (query.contains(".") && !query.contains(" ")) { cls = query.substring(0, query.indexOf('.')); name = query.substring(query.indexOf('.') + 1); }
        boolean prefix = name.endsWith("*");
        if (prefix) name = name.substring(0, name.length() - 1);
        String needle = name.toLowerCase();
        boolean sectionSearch = query.contains(" ") || (query.equals(query.toUpperCase()) && query.length() > 3 && !query.contains("_"));

        int hits = 0;
        for (JavaScan f : tree.files) {
            if (cls != null && !f.typeName.equalsIgnoreCase(cls)) continue;
            if (sectionSearch) {
                for (JavaScan.Section s : f.sections) {
                    if (s.title.toLowerCase().contains(needle)) {
                        hits++;
                        System.out.printf("%-28s L%-6d section: %s%n", f.fileName, s.line, s.title);
                    }
                }
                continue;
            }
            for (JavaScan.Member m : f.members) {
                String mn = m.name.toLowerCase();
                if (!(prefix ? mn.startsWith(needle) : mn.equals(needle))) continue;
                hits++;
                System.out.printf("%-28s L%-6d %-12s %s%s%n", f.fileName, m.line, m.kind.name().toLowerCase(),
                        SourceTree.clip(m.signature, 110), m.value.isEmpty() ? "" : " = " + SourceTree.clip(m.value, 60));
                if (print) {
                    List<String> lines = f.lines(m.line, m.endLine);
                    int n = m.line;
                    for (String l : lines) System.out.printf("%6d  %s%n", n++, l);
                    System.out.println();
                }
            }
        }
        if (hits == 0) System.out.println("nothing called " + query + (sectionSearch ? " among the section banners" : "") + " - try a prefix with *, or a section title in capitals");
    }
}
