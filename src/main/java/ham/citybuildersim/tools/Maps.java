package ham.citybuildersim.tools;

import java.io.IOException;

/**
 * Regenerates every generated document in one go: the code map, the dials,
 * the month order and the harness map. This is what `Regenerate maps.bat`
 * runs, and what the cloud loop runs after a batch lands.
 *
 * Run from the repository root:
 *
 *     java -cp target/classes ham.citybuildersim.tools.Maps
 *
 * Nothing here touches the game. The tools read src/main/java as text and
 * write docs/; they need no JavaFX, no Gson and no build beyond their own
 * classes, and they finish in a second or two.
 */
public final class Maps {

    public static void main(String[] args) throws IOException {
        long t0 = System.currentTimeMillis();
        SourceTree tree = SourceTree.open();
        System.out.printf("scanned %d files under %s%n", tree.files.size(), tree.root);
        CodeMap.run(tree);
        Dials.run(tree);
        MonthOrder.run(tree);
        HarnessMap.run(tree);
        System.out.printf("done in %.1fs - the documents are under %s%n", (System.currentTimeMillis() - t0) / 1000.0, tree.docs.toAbsolutePath());
    }
}
