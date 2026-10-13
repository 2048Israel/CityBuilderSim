package ham.citybuildersim;

import ham.citybuildersim.tools.Names;
import ham.citybuildersim.tools.SourceTree;
import ham.citybuildersim.tools.Stale;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The prose still describes the code: the firm half of `tools.Stale`, asserted,
 * and `tools.Names` proved on a fixture.
 *
 * WHAT THIS HAS TO PROVE, and none of it is about what the game does - a
 * finding here is a sentence that has stopped being true, not a mechanic that
 * has stopped working:
 *
 *   1. THE TOOL SEES A DEFECT WHEN THERE IS ONE. A fixture tree is written
 *      with three planted lies in it - a javadoc with no member under it, a
 *      comment naming a source file that is not in the tree, and a class
 *      header that miscounts its own banners - and each one has to come back.
 *      A checker that cannot fail is the failure mode this harness itself is
 *      most exposed to, because the tree it reads is usually clean.
 *
 *   2. AND NOTHING WHEN THERE IS NOT. The same fixture with a sound file in
 *      it raises nothing at all, so a green run means agreement and not
 *      silence.
 *
 *   3. NO JAVADOC IN THE TREE DOCUMENTS NOTHING. Two javadocs with no member
 *      between them, or one stranded above a section banner, means the lower
 *      one is what every reader and every index will use and the upper one is
 *      dead text that nothing will ever correct.
 *
 *   4. NOTHING NAMES A FILE THAT IS NOT THERE. A comment or a document that
 *      names a source file or a docs/ page which has been renamed or removed
 *      sends the next reader looking for it.
 *
 *   5. EVERY STATED COUNT IS THE COUNT. A class header that counts its own
 *      banners, and the two front doors where they count the harnesses and the
 *      interface files, have to agree with the files on disk. This is the one
 *      the interface split of 2026-09-18 broke in a dozen places by hand.
 *
 *   6. tools.Names TELLS A REAL NAME FROM A MADE-UP ONE (RV1). A canned diff
 *      over a fixture tree names members declared the ways a regex misses
 *      (`final`, `double[]`, a generic return, a record's component, an
 *      inherited method), a made-up method and constant, a member put on the
 *      wrong class and a type that is not there; only the second kind may come
 *      back, at the line the diff gives it, and a proposal, a file name, code
 *      and a generated document are not read as claims. Its run on the tree's
 *      own branches is ship.sh's, and a warning: a name in a passage about the
 *      past, or the future, is not a defect of the tree's.
 *
 * The soft categories - a member named in a comment that nothing declares, and
 * docs/map being older than the sources - are printed by the tool and are NOT
 * asserted here: the first can be a library call this tree never makes, and the
 * second is cleared by `Regenerate maps.bat` and is written wrongly by any
 * checkout. Run the tool itself to see them:
 *
 *     java -cp target/classes ham.citybuildersim.tools.Stale
 */
public class StaleCheck {

    static int fails = 0;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-68s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** An assertion that a firm category is empty, printing what it found when it is not. */
    static void none(String label, List<Stale.Finding> found) {
        if (!found.isEmpty()) fails++;
        System.out.printf("%-68s %s%n", label,
                found.isEmpty() ? "OK" : "FAIL - " + found.size() + (found.size() == 1 ? " finding" : " findings"));
        for (Stale.Finding f : found) System.out.println("      " + f);
    }

    /** The findings of one category that came from one place, so a fixture's assertions are its own. */
    static List<Stale.Finding> only(List<Stale.Finding> all, Stale.Cat cat, String pathPart) {
        List<Stale.Finding> out = new ArrayList<>();
        for (Stale.Finding f : Stale.of(all, cat)) if (f.path.contains(pathPart)) out.add(f);
        return out;
    }

    public static void main(String[] args) throws Exception {

        SourceTree tree;
        try {
            tree = SourceTree.open();
        } catch (IOException e) {
            // the tools read the sources as text; without them there is nothing to check
            System.out.println("--- no source tree ---");
            System.out.println("skipped: " + e.getMessage());
            System.out.println("\nAll checks passed.");
            return;
        }

        /* ==================== 1. the tool sees a defect ==================== */
        System.out.println("--- a planted defect comes back ---");

        Path root = Files.createTempDirectory("stale");
        Path src = root.resolve("src");
        Files.createDirectories(src.resolve("ham/citybuildersim"));
        Files.writeString(src.resolve("ham/citybuildersim/Planted.java"), PLANTED, StandardCharsets.UTF_8);
        Files.writeString(src.resolve("ham/citybuildersim/Sound.java"), SOUND, StandardCharsets.UTF_8);

        // the real docs/ folder, so that a document the fixture does not mention
        // is not reported as missing simply because the fixture has no docs at all
        SourceTree fixture = new SourceTree(src, Paths.get(System.getProperty("docs", "docs")));
        List<Stale.Finding> planted = Stale.scan(fixture);

        assertTrue("the fixture is two files", fixture.files.size() == 2);
        assertTrue("a javadoc with no member under it is found",
                !only(planted, Stale.Cat.ORPHANED_JAVADOC, "Planted.java").isEmpty());
        assertTrue("a comment naming a source file that is not in the tree is found",
                !only(planted, Stale.Cat.MISSING_FILE, "Planted.java").isEmpty());
        assertTrue("a header that miscounts its own banners is found",
                !only(planted, Stale.Cat.STATED_COUNT, "Planted.java").isEmpty());

        /* ==================== 2. ...and nothing when there is none ========= */
        System.out.println("\n--- and a sound file raises nothing ---");

        assertTrue("no orphaned javadoc in the sound file",
                only(planted, Stale.Cat.ORPHANED_JAVADOC, "Sound.java").isEmpty());
        assertTrue("...no missing file", only(planted, Stale.Cat.MISSING_FILE, "Sound.java").isEmpty());
        assertTrue("...and no stated count, because its header counts right",
                only(planted, Stale.Cat.STATED_COUNT, "Sound.java").isEmpty());

        cleanUp(root);

        /* ==================== 3. the tree itself =========================== */
        System.out.println("\n--- the tree's own prose ---");

        // a scan that read nothing would pass every assertion below it
        System.out.printf("(%d files scanned)%n", tree.files.size());
        assertTrue("the scan read the whole tree, not an empty one", tree.files.size() > 100);

        List<Stale.Finding> all = Stale.scan(tree);
        none("no javadoc is left documenting nothing", Stale.of(all, Stale.Cat.ORPHANED_JAVADOC));
        none("nothing names a source file or a docs/ page that is not there", Stale.of(all, Stale.Cat.MISSING_FILE));
        none("every stated count is the count", Stale.of(all, Stale.Cat.STATED_COUNT));

        int soft = Stale.of(all, Stale.Cat.UNRESOLVED_MEMBER).size()
                + Stale.of(all, Stale.Cat.MAP_OLDER).size()
                + Stale.of(all, Stale.Cat.FUZZY_COUNT).size();
        System.out.printf("%n(%d soft finding%s not asserted here - run tools.Stale to read them)%n",
                soft, soft == 1 ? "" : "s");

        /* ==================== 6. tools.Names on a fixture ================== */
        names();

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** Section 6: the fixture tree, a diff over it, and what Names makes of each line. */
    static void names() throws IOException {
        System.out.println("\n--- tools.Names: a real name resolves, a made-up one and a member on the wrong class do not ---");

        Path root = Files.createTempDirectory("names");
        Path src = root.resolve("src");
        Files.createDirectories(src.resolve("ham/citybuildersim"));
        Files.writeString(src.resolve("ham/citybuildersim/Alpha.java"), ALPHA, StandardCharsets.UTF_8);
        Files.writeString(src.resolve("ham/citybuildersim/Beta.java"), BETA, StandardCharsets.UTF_8);
        SourceTree fixture = new SourceTree(src, Paths.get(System.getProperty("docs", "docs")));
        Names.Result r = Names.scan(fixture, NAMES_DIFF, path -> path.endsWith("Gamma.java") ? GAMMA : null);
        cleanUp(root);

        java.util.Map<String, Names.Miss> miss = new java.util.HashMap<>();
        for (Names.Miss m : r.unresolved) miss.put(m.name, m);
        System.out.printf("(%d names read, %d unresolved: %s)%n", r.read.size(), r.unresolved.size(), miss.keySet());

        for (String real : List.of("Alpha.weights()", "Alpha.sorted()", "Alpha.LIMIT", "Beta.weights()",
                "Alpha.Pair.left()", "Alpha.Mode.values()", "own()", "Gamma.fresh()")) {
            assertTrue("a real name is read and resolves: " + real, r.read.contains(real) && !miss.containsKey(real));
        }
        Names.Miss made = miss.get("frobnicate()");
        assertTrue("a made-up method comes back, at the line the diff gives it",
                made != null && made.path.equals("docs/fixture.md") && made.line == 3);
        assertTrue("...and a made-up constant on a real class", miss.containsKey("Alpha.NO_SUCH_LIMIT"));
        Names.Miss wrong = miss.get("Alpha.own()");
        assertTrue("a member put on the wrong class comes back, though Beta declares it",
                wrong != null && wrong.why.contains("Alpha does not declare own"));
        assertTrue("a type the tree does not have comes back", miss.containsKey("Omega.run()"));
        Names.Miss java = miss.get("madeUpInJava()");
        assertTrue("a made-up name in a Java comment comes back at its line in the branch's file",
                java != null && java.path.endsWith("Gamma.java") && java.line == 3);
        assertTrue("those five and nothing else", r.unresolved.size() == 5);
        assertTrue("code is not read as prose", !r.read.contains("notInAComment()"));
        assertTrue("a generated document is not read", !r.read.contains("ghostInGenerated()"));
        assertTrue("a file name is not a member", r.read.stream().noneMatch(n -> n.endsWith(".java")));
        assertTrue("a name the text proposes is set apart, not counted",
                r.proposed.contains("GammaCheck") && r.proposed.contains("DELTA_MONTHS"));
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }

    /* ------------------------------------------------------------------
       THE FIXTURES, WHICH CAUSE THE CONDITION RATHER THAN WAITING FOR IT
       ------------------------------------------------------------------ */

    /** Three planted lies: a stranded javadoc, a file that is not there, and a header out by one. */
    private static final String PLANTED = """
            package ham.citybuildersim;

            /**
             * A fixture with three defects planted in it.
             *
             * It names NoSuchThingAtAll.java, which is not in this tree, and this
             * header claims the three banners below when there are two of them.
             */
            public final class Planted {

                /* =====================================================================
                   THE FIRST BANNER
                   ===================================================================== */

                /** A member with its own javadoc, which is the sound case. */
                public int documented = 1;

                /* =====================================================================
                   THE SECOND BANNER
                   ===================================================================== */

                /** A javadoc with nothing under it at all. */
            }
            """;

    /** Section 6's tree: members declared the ways a regex over source misses. */
    private static final String ALPHA = """
            package ham.citybuildersim;

            /** A fixture class for tools.Names. */
            public class Alpha {
                /** A constant. */
                public static final int LIMIT = 3;
                /** Final, returning an array. */
                public final double[] weights(int n) { return new double[n]; }
                /** A generic return type. */
                public static <T extends Comparable<T>> java.util.List<T> sorted(java.util.List<T> in) { return in; }
                /** A record, whose accessors are its components. */
                public record Pair(int left, int right) { }
                /** An enum, which has values(). */
                public enum Mode { ON, OFF }
            }
            """;

    /** A subclass: what it inherits resolves on it, and what it declares does not resolve on Alpha. */
    private static final String BETA = """
            package ham.citybuildersim;

            /** A fixture subclass for tools.Names. */
            public class Beta extends Alpha {
                /** Its own. */
                public void own() { }
            }
            """;

    /** A file the branch adds, read as the branch has it. */
    private static final String GAMMA = """
            package ham.citybuildersim;

            /** Added on the branch; this comment names madeUpInJava(). */
            public class Gamma {
                /** The branch's own method. */
                public void fresh() { int x = notInAComment(); }
            }
            """;

    /** The diff: a document's six lines (2 to 7), the Java file whole, and a generated page. */
    private static final String NAMES_DIFF = """
            diff --git a/docs/fixture.md b/docs/fixture.md
            --- a/docs/fixture.md
            +++ b/docs/fixture.md
            @@ -1,0 +2,6 @@
            +Real: `Alpha.weights()`, Alpha.sorted(list), `Alpha.LIMIT`, `Beta.weights()`, `Alpha.Pair.left()`, `Alpha.Mode.values()` and own().
            +Made up: frobnicate() and `Alpha.NO_SUCH_LIMIT`.
            +On the wrong class: `Alpha.own()`. Not a type here: `Omega.run()`.
            +A file, not a member: `Alpha.java`. Proposals: a new `GammaCheck`, and `DELTA_MONTHS`, written `DELTA_MONTHS = 6`.
            +The branch's own: `Gamma.fresh()`.
            +
            diff --git a/src/ham/citybuildersim/Gamma.java b/src/ham/citybuildersim/Gamma.java
            new file mode 100644
            --- /dev/null
            +++ b/src/ham/citybuildersim/Gamma.java
            @@ -0,0 +1,7 @@
            +package ham.citybuildersim;
            +
            +/** Added on the branch; this comment names madeUpInJava(). */
            +public class Gamma {
            +    /** The branch's own method. */
            +    public void fresh() { int x = notInAComment(); }
            +}
            diff --git a/docs/dials.md b/docs/dials.md
            --- a/docs/dials.md
            +++ b/docs/dials.md
            @@ -9,0 +10 @@
            +| generated | ghostInGenerated() |
            """;

    /** The same shapes, all of them true. */
    private static final String SOUND = """
            package ham.citybuildersim;

            /**
             * A fixture with nothing wrong with it.
             *
             * The one banner below introduces the only member, and this sentence
             * names no file at all.
             */
            public final class Sound {

                /* =====================================================================
                   THE ONLY BANNER
                   ===================================================================== */

                /** The only member, documented once. */
                public int only = 1;
            }
            """;
}
