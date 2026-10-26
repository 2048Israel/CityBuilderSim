package ham.citybuildersim;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The harness for AllChecks' known reds (batch FW7): a failed harness is said
 * to be a known red or a NEW one against main's record, line by line, and
 * batch-check.sh judges the same logs the same way.
 *
 * WHY (FW7, 2026-10-25): HealthCheck was a recorded red of main's, and a second
 * assertion in it went red. AllChecks -q printed "HealthCheck FAILED" as it had
 * the day before and nobody saw it. batch-check.sh already compared the red
 * lines against gate.txt word for word, but AllChecks itself knew nothing of
 * the record, so what a session read of -q and -f could not tell its own reds
 * from main's. A fixture harness (Fixture, below) fails one assertion and then,
 * on request, a second, and this runs it as AllChecks runs a harness, so both
 * cases are caused and not assumed: the same red again is a known red, a second
 * one is a NEW red and is named.
 *
 * What it has to prove:
 *   1. the red lines of a failed harness are its exit, each failing line and its
 *      count, and never a section title or a passing line;
 *   2. the record those lines make reads back from a file as three lines, and the
 *      same failure against it is a known red, in one line under -q and every
 *      failing line tagged [known] under -f;
 *   3. a second failing assertion is a NEW red: the header says how many red
 *      lines are new, -f tags that line [NEW] and the first one still [known],
 *      and the count in the header is the count of tags;
 *   4. with no record nothing is tagged, and -f prints as it did before FW7;
 *   5. batch-check.sh --judge reads the same logs: exit 3 for the known red,
 *      exit 4 and the new line for the second, exit 3 again for a log from before
 *      FW7 with no tags, and exit 1 when the header's count and its own disagree
 *      (not run where sh cannot be started).
 *
 * This harness's own output never echoes the fixture's, so that AllChecks -f,
 * reading this log for failing lines, finds only this harness's own.
 */
public class KnownRedsCheck {

    static int fails = 0;

    /** This harness's own assertion line, the suite's. */
    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-70s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** What the fixture is called in the red lines: not its class name, which has a $ in it. */
    static final String NAME = "KnownRedsFixture";

    /** The fixture's first assertion, always false: the red a record holds. */
    static final String KNOWN_LABEL = "the fixture's known assertion holds";

    /** The fixture's second assertion, false only when asked: the NEW red. */
    static final String SECOND_LABEL = "the fixture's second assertion holds";

    /** The system property that makes the fixture fail its second assertion as well. */
    static final String SECOND_PROPERTY = "knownreds.second";

    /** What a child JVM is started as: the fixture's own main, below. */
    static final String FIXTURE_HARNESS = "KnownRedsCheck$Fixture";

    /** The longest a child JVM or the shell may take. */
    static final int WAIT_SECONDS = 120;

    /**
     * A miniature harness for a child JVM: one assertion that fails, a passing
     * one whose label says FAILS, and when -Dknownreds.second=true a second
     * that fails; its exit status is its count of failures, as any harness's.
     */
    public static class Fixture {
        public static void main(String[] args) {
            int n = 1;
            System.out.println("--- 1. the fixture's known red ---");
            System.out.printf("%-70s %s%n", KNOWN_LABEL, "FAIL");
            System.out.println("--- 2. a passing line ---");
            System.out.printf("%-70s %s%n", "a passing label that says FAILS in its words", "OK");
            if ("true".equals(System.getProperty(SECOND_PROPERTY))) {
                n++;
                System.out.println("--- 3. the second ---");
                System.out.printf("%-70s %s%n", SECOND_LABEL, "FAIL");
            }
            System.out.println(n + " FAILED");
            System.exit(n);
        }
    }

    /** One run of the fixture: its exit status and the failing lines of its log, as AllChecks -f reads them. */
    private record Run(int exit, List<String> printed) { }

    public static void main(String[] args) throws Exception {
        Path tmp = TempDirs.create("knownreds");

        Run first = run(tmp.resolve("first"), false);
        List<String> firstRed = redLines(first);
        redLinesAre(first, firstRed);
        Path recordFile = tmp.resolve("record").resolve("gate.txt");
        Files.createDirectories(recordFile.getParent());
        writeRecord(recordFile, firstRed);

        Set<String> known = AllChecks.knownReds(recordFile);
        knownRed(first, firstRed, known);

        Run second = run(tmp.resolve("second"), true);
        newRed(second, known);

        noRecord(tmp, first);

        judged(tmp, first, second, known, recordFile);

        if (fails > 0) System.out.println("\n" + fails + " FAILED");
        System.exit(fails);
    }

    /** The red lines the fixture's run makes. */
    private static List<String> redLines(Run run) {
        return AllChecks.redLines(NAME, run.exit(), run.printed());
    }

    /** Starts the fixture as AllChecks starts a harness and returns its exit status and failing lines. */
    private static Run run(Path folder, boolean second) throws Exception {
        Files.createDirectories(folder);
        String javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        List<String> cmd = new ArrayList<>(AllChecks.command(javaExe, classpath, FIXTURE_HARNESS, true));
        if (second) cmd.add(cmd.indexOf("-cp"), "-D" + SECOND_PROPERTY + "=true");
        Path log = folder.resolve("fixture.log");
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.environment().remove("JDK_JAVA_OPTIONS");
        pb.redirectErrorStream(true);
        pb.redirectOutput(log.toFile());
        Process p = pb.start();
        int exit = -1;
        if (p.waitFor(WAIT_SECONDS, TimeUnit.SECONDS)) exit = p.exitValue();
        else p.destroyForcibly();
        return new Run(exit, AllChecks.failingLines(log));
    }

    /** Writes a record as ship.sh writes gate.txt: a comment line, a blank, then the red lines. */
    private static void writeRecord(Path file, List<String> red) throws IOException {
        StringBuilder text = new StringBuilder("# gate of the fixture\n\n");
        for (String r : red) text.append(r).append('\n');
        Files.write(file, text.toString().getBytes(StandardCharsets.UTF_8));
    }

    /** Whether a red line, without its harness name, is a section title. */
    private static boolean isSection(String redLine) {
        return AllChecks.SECTION.matcher(redLine.substring((NAME + " | ").length())).matches();
    }

    /* =====================================================================
     * 1. THE RED LINES OF A FAILED HARNESS
     * ===================================================================== */

    private static void redLinesAre(Run first, List<String> red) {
        System.out.println("--- 1. the gate's record of a red harness: its exit, its failing line and its count ---");
        check("the fixture's first run exits 1", first.exit() == 1);
        check("it has exactly three red lines", red.size() == 3);
        boolean three = red.size() == 3;
        check("the first red line is its exit", three && red.get(0).equals(NAME + " | exit 1"));
        check("the second is the failing assertion's line",
                three && red.get(1).startsWith(NAME + " | " + KNOWN_LABEL) && red.get(1).endsWith("FAIL"));
        check("the third is its count of failures, its last line", three && red.get(2).equals(NAME + " | 1 FAILED"));
        check("the passing line that says FAILS in its words is not a red line", red.stream().noneMatch(r -> r.contains("FAILS")));
        check("no section title is a red line", red.stream().noneMatch(KnownRedsCheck::isSection));
        check("the failing lines AllChecks prints do hold a section title, so the walk skipped one",
                first.printed().stream().anyMatch(p -> AllChecks.SECTION.matcher(p).matches()));
    }

    /* =====================================================================
     * 2. THE SAME RED AGAIN IS A KNOWN RED
     * ===================================================================== */

    private static void knownRed(Run first, List<String> red, Set<String> known) {
        System.out.println("--- 2. the same red again is a known red, under -q and -f ---");
        check("the record reads back as a set", known != null);
        if (known == null) return;
        check("it holds three lines: the comment and the blank one are not in it", known.size() == 3);
        check("every red line of the fixture's run is in it", known.containsAll(red));
        List<String> q = AllChecks.report(NAME, first.exit(), 0.5, first.printed(), false, known);
        check("under -q the report is one line", q.size() == 1);
        check("and it ends in the words known red", q.get(0).endsWith("  known red"));
        List<String> f = AllChecks.report(NAME, first.exit(), 0.5, first.printed(), true, known);
        check("under -f the header ends in the words known red", f.get(0).endsWith("  known red"));
        List<String> under = f.subList(1, f.size());
        List<String> notSections = under.stream().filter(l -> !AllChecks.SECTION.matcher(l).matches()).toList();
        check("under -f there are lines under the header besides section titles", notSections.size() >= 2);
        check("every one of them is tagged [known]", notSections.stream().allMatch(l -> l.startsWith("    " + AllChecks.KNOWN_TAG)));
        check("none is tagged [NEW]", under.stream().noneMatch(l -> l.contains(AllChecks.NEW_TAG.strip())));
        check("a section title is printed as it was, with no tag", under.stream()
                .filter(l -> AllChecks.SECTION.matcher(l).matches()).allMatch(l -> l.startsWith("    ---")));
    }

    /* =====================================================================
     * 3. A SECOND FAILING ASSERTION IS A NEW RED
     * ===================================================================== */

    private static void newRed(Run second, Set<String> known) {
        System.out.println("--- 3. a second failing assertion is a NEW red, and -f names it ---");
        if (known == null) { check("the record was read", false); return; }
        List<String> red = redLines(second);
        int fresh = AllChecks.newCount(red, known);
        check("the fixture's second run exits 2", second.exit() == 2);
        check("some of its red lines are not in the record", fresh > 0);
        check("the line of the second assertion is one of them",
                red.stream().anyMatch(r -> r.startsWith(NAME + " | " + SECOND_LABEL) && !known.contains(r)));
        List<String> f = AllChecks.report(NAME, second.exit(), 0.5, second.printed(), true, known);
        String header = f.get(0);
        check("the header says NEW red", header.contains("  NEW red: "));
        check("it states how many red lines are new and how many there are",
                header.endsWith("  NEW red: " + fresh + " of " + red.size() + " red lines"));
        check("the second assertion's line is tagged [NEW]", f.stream().anyMatch(l ->
                l.startsWith("    " + AllChecks.NEW_TAG) && l.contains(SECOND_LABEL)));
        check("the first assertion's line is still tagged [known]", f.stream().anyMatch(l ->
                l.startsWith("    " + AllChecks.KNOWN_TAG) && l.contains(KNOWN_LABEL)));
        long tags = f.stream().filter(l -> l.startsWith("    " + AllChecks.NEW_TAG)).count();
        boolean exitNew = !known.contains(red.get(0));
        check("the header's count is the [NEW] tags plus one for a new exit line", tags + (exitNew ? 1 : 0) == fresh);
        List<String> q = AllChecks.report(NAME, second.exit(), 0.5, second.printed(), false, known);
        check("under -q the report is the header alone, with the same verdict", q.size() == 1 && q.get(0).equals(header));
    }

    /* =====================================================================
     * 4. WITH NO RECORD NOTHING IS TAGGED
     * ===================================================================== */

    private static void noRecord(Path tmp, Run first) throws IOException {
        System.out.println("--- 4. with no record nothing is tagged: -f prints as before FW7 ---");
        check("a record that does not exist reads as null", AllChecks.knownReds(tmp.resolve("no-such-folder").resolve("gate.txt")) == null);
        List<String> f = AllChecks.report(NAME, first.exit(), 0.5, first.printed(), true, null);
        check("the header has no verdict, it ends with the time", f.get(0).endsWith("s)"));
        check("there are as many lines as the header and the failing lines", f.size() == 1 + first.printed().size());
        boolean same = true;
        for (int i = 0; i < first.printed().size(); i++) same &= f.get(i + 1).equals("    " + first.printed().get(i));
        check("each is the failing line, indented four spaces and nothing else", same);
        check("none has a tag", f.stream().noneMatch(l -> l.contains("[known]") || l.contains("[NEW]")));
        check("the line under the summary says no record was read",
                AllChecks.againstLine(AllChecks.GATE, null, List.of(), List.of(NAME)).startsWith("No known reds read ("));
    }

    /* =====================================================================
     * 5. BATCH-CHECK.SH JUDGES THE SAME LOGS
     * ===================================================================== */

    /** What batch-check.sh --judge said and exited with. */
    private record Judged(int exit, String output) { }

    /** Runs the script on a saved suite log against a record, from the repository root, which is where the suite runs. */
    private static Judged judge(Path folder, String suiteText, Path record) throws Exception {
        Files.createDirectories(folder);
        Path suite = folder.resolve("suite.txt");
        Files.write(suite, suiteText.getBytes(StandardCharsets.UTF_8));
        Path out = folder.resolve("judged.txt");
        ProcessBuilder pb = new ProcessBuilder("sh", "batch-check.sh", "--judge", slashes(suite));
        pb.environment().put("CBS_KNOWN_REDS", slashes(record));
        pb.redirectErrorStream(true);
        pb.redirectOutput(out.toFile());
        Process p = pb.start();
        int exit = -1;
        if (p.waitFor(60, TimeUnit.SECONDS)) exit = p.exitValue();
        else p.destroyForcibly();
        return new Judged(exit, new String(Files.readAllBytes(out), StandardCharsets.UTF_8));
    }

    /** A path as a POSIX shell takes it on every system: forward slashes. */
    private static String slashes(Path p) {
        return p.toAbsolutePath().toString().replace('\\', '/');
    }

    /** A suite log as AllChecks -f would print it for this one failed harness: its report, a blank line, the summary. */
    private static String suiteLog(Run run, Set<String> known) {
        List<String> lines = new ArrayList<>(AllChecks.report(NAME, run.exit(), 0.5, run.printed(), true, known));
        lines.add("");
        lines.add(AllChecks.summaryLine(1, 1, 0));
        return String.join("\n", lines) + "\n";
    }

    private static void judged(Path tmp, Run first, Run second, Set<String> known, Path recordFile) throws Exception {
        System.out.println("--- 5. batch-check.sh judges the same logs: exit 3 for the known red, exit 4 and the NEW line for the second ---");
        if (known == null) { check("the record was read", false); return; }
        String firstTagged = suiteLog(first, known);
        String secondTagged = suiteLog(second, known);
        int wrong = AllChecks.newCount(redLines(second), known) + 1;   // a count the script cannot find, whatever the right one is
        Judged a, b, c, d;
        try {
            a = judge(tmp.resolve("judge-a"), firstTagged, recordFile);
            b = judge(tmp.resolve("judge-b"), secondTagged, recordFile);
            c = judge(tmp.resolve("judge-c"), suiteLog(first, null), recordFile);
            d = judge(tmp.resolve("judge-d"), secondTagged.replaceFirst("  NEW red: [0-9]+ of ", "  NEW red: " + wrong + " of "), recordFile);
        } catch (IOException e) {
            System.out.println("sh is not on the PATH here, so section 5 did not run (batch-check.sh's suite runs it)");
            return;
        }
        check("the known red, tagged by AllChecks, is judged exit 3", a.exit() == 3);
        check("and its verdict line says known red", a.output().contains("known red"));
        check("the second run, tagged, is judged exit 4", b.exit() == 4);
        check("and its verdict line says NEW red", b.output().contains("NEW red"));
        check("and it prints the second assertion's line", b.output().contains(SECOND_LABEL));
        check("a log from before FW7, with no tags, is judged exit 3 as well", c.exit() == 3);
        check("a header whose count is not the one batch-check.sh finds is refused, exit 1", d.exit() == 1);
        check("and the line says the two judges disagree", d.output().contains("disagree"));
    }
}
