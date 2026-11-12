package ham.citybuildersim;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The harness for AllChecks' known reds and harness time limit.
 *
 * WHY (FW7): a second failing HealthCheck assertion hid behind its known-red
 * verdict. Fixtures exercise the real runner's line tagging and the shell's
 * judge. GF1 adds a child whose descendant never ends, reproducing CD9's hang.
 *
 * Proves harness-scoped parts, exact records, count guards and agreement
 * through the common judge and both shell callers. The time limit fails and ends a hung process tree,
 * preserving an in-time child's exit and output. Fixture output stays out of
 * this harness's own log so its intentional failures cannot be misread.
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

    /** The short limit used to exercise the production wait without holding the suite for HARNESS_SECONDS. */
    static final int LIMIT_SECONDS = 2;

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
        Run second = run(tmp.resolve("second"), true);
        List<String> red = AllChecks.redLines(NAME, first.exit(), first.printed());
        Set<String> exact = new java.util.LinkedHashSet<>(red);
        Set<String> parts = Set.of(NAME + " | " + KNOWN_LABEL + " | FIX | 2026-11-12 | fixture");
        check("the fixture exits with one failing assertion", first.exit() == 1 && red.size() == 3);
        check("passing FAILS labels and section titles stay out of reds", red.stream().noneMatch(r -> r.contains("FAILS") || r.contains("---")));
        check("exact main lines cover the fixture", AllChecks.newCount(red, exact) == 0);
        String pipes = NAME + " | a literal | pipe | inside | an assertion FAIL";
        check("exact red lines preserve literal pipe characters", KnownReds.fresh(List.of(pipes), Set.of(pipes)).isEmpty());
        check("a harness-scoped part covers its assertion and both counts", AllChecks.newCount(red, parts) == 0);
        check("another harness's part covers nothing", AllChecks.newCount(red, Set.of("OtherCheck | " + KNOWN_LABEL + " | FIX | 2026-11-12 | fixture")) == 3);
        check("counts alone never cover a red", AllChecks.newCount(red, Set.of(NAME + " | exit 1", NAME + " | 1 FAILED")) == 3);
        List<String> added = AllChecks.redLines(NAME, second.exit(), second.printed());
        check("a second assertion keeps its counts NEW too", AllChecks.newCount(added, parts) == 3);
        check("an exit with no assertion remains NEW", AllChecks.newCount(List.of(NAME + " | exit 1"), parts) == 1);
        check("one-time qualified crash parts retain their explicit approval", KnownReds.fresh(
                List.of("OtherCheck | exit -1073741819"), Set.of("@once OtherCheck | exit -10")).isEmpty());
        List<String> report = AllChecks.report(NAME, first.exit(), .5, first.printed(), true, parts);
        check("the runner tags covered assertion and count known", report.stream().filter(l -> l.contains("[known]")).count() == 2 && report.get(0).endsWith("known red"));
        check("the quiet report keeps the same verdict", AllChecks.report(NAME, first.exit(), .5, first.printed(), false, parts).equals(List.of(report.get(0))));
        check("without a record the report has no tags", AllChecks.report(NAME, first.exit(), .5, first.printed(), true, null).stream().noneMatch(l -> l.contains("[known]") || l.contains("[NEW]")));
        check("a missing record reads as null", AllChecks.knownReds(tmp.resolve("missing")) == null);
        Set<String> removed = new java.util.LinkedHashSet<>(exact);
        removed.add("! " + parts.iterator().next());
        check("a mending batch loses its exact-record cover too", AllChecks.newCount(red, removed) == 3);
        removed.add("@once " + NAME + " | " + KNOWN_LABEL);
        check("explicit one-time words can pass a retired line", AllChecks.newCount(red, removed) == 0);
        Path longLog = tmp.resolve("many.log");
        Files.write(longLog, java.util.stream.IntStream.range(0, 60).mapToObj(i -> "assertion " + i + " FAIL").toList());
        check("late failing assertions reach the judge", AllChecks.failingLines(longLog).contains("assertion 59 FAIL"));
        Path record = tmp.resolve("parts.txt");
        Files.write(record, parts);
        check("metadata and comment lines read back intact", AllChecks.knownReds(record).equals(parts));
        System.out.println("--- one judge through the runner, batch check and publisher entry point ---");
        for (Run fixture : List.of(first, second)) {
            Path folder = tmp.resolve("judge-" + fixture.exit());
            Files.createDirectories(folder);
            String log = suiteLog(fixture, parts);
            Path suite = folder.resolve("suite.txt");
            Files.writeString(suite, log);
            int expected = fixture.exit() == 1 ? 3 : 4;
            // The publisher uses this same saved-log entry point after merging its records.
            Judged shell = judge(folder.resolve("shell"), log, record);
            int common;
            PrintStream console = System.out;
            try (PrintStream discarded = new PrintStream(OutputStream.nullOutputStream())) {
                System.setOut(discarded);
                common = KnownReds.judge(suite, parts, folder.resolve("direct"), false);
            } finally {
                System.setOut(console);
            }
            check("runner and both shell callers agree for fixture " + fixture.exit(), shell.exit() == expected && common == expected);
            if (fixture.exit() == 2) check("the shell names the extra assertion", shell.output().contains(SECOND_LABEL));
        }
        Judged old = judge(tmp.resolve("untagged"), suiteLog(first, null), record);
        check("an older untagged log gets the same verdict", old.exit() == 3);
        Judged wrong = judge(tmp.resolve("wrong"), suiteLog(second, parts).replace("NEW red: 3 of", "NEW red: 9 of"), record);
        check("inconsistent runner tags fail closed", wrong.exit() == 1 && wrong.output().contains("disagree"));
        check("a missing summary fails closed", judge(tmp.resolve("incomplete"), "unfinished", record).exit() == 1);
        check("a zero-harness summary fails closed", judge(tmp.resolve("empty"), "0 harnesses run in 1s: 0 passed, 0 failed\n", record).exit() == 1);
        limited(tmp);
        if (fails > 0) System.out.println("\n" + fails + " FAILED");
        System.exit(fails);
    }

    /** A fixture with a never-ending descendant, or an immediate successful exit. */
    public static class LimitFixture {
        public static void main(String[] args) throws Exception {
            if (args[0].equals("done")) { System.out.println("finished in time"); return; }
            if (args[0].equals("parent")) {
                List<String> command = new ArrayList<>(AllChecks.command(
                        System.getProperty("java.home") + File.separator + "bin" + File.separator + "java",
                        System.getProperty("java.class.path"), "KnownRedsCheck$LimitFixture", true));
                command.add("leaf"); command.add(args[1]);
                new ProcessBuilder(command).inheritIO().start();
            } else Files.writeString(Path.of(args[1]), Long.toString(ProcessHandle.current().pid()));
            Thread.sleep(Long.MAX_VALUE);
        }
    }

    /* =====================================================================
     * 6. A HARNESS AND ITS DESCENDANTS HAVE A LIMIT (GF1)
     * ===================================================================== */
    private static void limited(Path tmp) throws Exception {
        System.out.println("--- 6. a harness and its descendants have a limit ---");
        check("the fixture exercises the wait with a shorter limit than production", LIMIT_SECONDS < AllChecks.HARNESS_SECONDS);
        Path pid = tmp.resolve("descendant.pid");
        for (String mode : List.of("parent", "done")) {
            List<String> command = new ArrayList<>(AllChecks.command(
                    System.getProperty("java.home") + File.separator + "bin" + File.separator + "java",
                    System.getProperty("java.class.path"), "KnownRedsCheck$LimitFixture", true));
            command.add(mode); command.add(pid.toString());
            Path log = tmp.resolve("limit-" + mode + ".log");
            ProcessBuilder builder = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(log.toFile());
            builder.environment().remove("JDK_JAVA_OPTIONS");
            Process process = builder.start();
            try {
                if (mode.equals("parent")) {
                    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(WAIT_SECONDS);
                    while (!Files.exists(pid) && process.isAlive() && System.nanoTime() < deadline) Thread.sleep(20);
                    check("the fixture really started its never-ending descendant", Files.exists(pid));
                }
                int code = AllChecks.waitForHarness(process, LIMIT_SECONDS, log);
                String output = Files.readString(log);
                if (mode.equals("parent")) {
                    check("a harness over its limit is counted failed", code == 1 && !process.isAlive());
                    check("its failing line names the time limit", AllChecks.failingLines(log).contains("FAIL: timed out after " + LIMIT_SECONDS + " s"));
                    long child = Long.parseLong(Files.readString(pid));
                    check("the never-ending descendant was ended with its parent", !ProcessHandle.of(child).map(ProcessHandle::isAlive).orElse(false));
                } else {
                    check("a harness that finishes in time keeps its own exit and output", code == 0 && output.strip().equals("finished in time"));
                }
            } finally { if (process.isAlive()) AllChecks.endTree(process); }
        }
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

}
