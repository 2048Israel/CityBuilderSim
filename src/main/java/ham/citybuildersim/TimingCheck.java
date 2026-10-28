package ham.citybuildersim;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The harness for soft timing (batch TM1): a timing assertion fails hard, goes SOFT and uncounted under -Dcbs.timing=soft, and nothing else in the suite moves.
 *
 * WHY THIS EXISTS (batch TM1, 2026-10-18). Batches on the PC took turns under
 * "one JVM at a time on the machine" because only the timing assertions
 * clashed under another JVM's load (EarthTerrainCheck 8: 116 ms against 80
 * with Jerus's game running, 27.8 without). The property lets two suites
 * check side by side; a property that could soften anything but a clock would
 * let a real failure through unseen, so this holds, with a fixture that cannot
 * but miss its budget, that the property touches only a clock - and holds the
 * suite to the one helper (Timing) that reads it.
 *
 * What it has to prove:
 *   1. hard (the property unset, or "hard"): the fixture's timing assertion
 *      prints FAIL and counts 1; a figure within a budget it can meet prints OK;
 *   2. soft: the same missed assertion's line says SOFT with its figure and
 *      budget, matches Timing.SOFT_LINE and counts nothing; the unit is the one
 *      the assertion names; a strict miss (below, equal figures) goes soft too
 *      and fails hard;
 *   3. only a clock is soft: a false non-timing assertion is FAIL and counted,
 *      whichever side of && a miss is on; a told miss is cleared, as is one a
 *      FAIL swallowed; a NaN figure is FAIL even soft; two misses in one
 *      condition are told on the one line;
 *   4. soft() is true for exactly "soft";
 *   5. across the JVM boundary: AllChecks.command forwards the property, and a
 *      child JVM started as AllChecks starts one exits 0 and prints SOFT when
 *      soft, exits 1 and prints FAIL when not; AllChecks.softLines counts the
 *      SOFT lines of a log;
 *   6. the suite goes through it: no *Check source compares a figure against a
 *      *_MS or *_NS dial but through Timing (bar one allowance), the harnesses
 *      that call it are Timing.HARNESSES, each prints through Timing.verdict,
 *      the property is read in one place, and AllChecks runs every one of them
 *      and this;
 *   7. the rest of the suite is untouched: with no miss held, verdict(true) is
 *      exactly OK and verdict(false) exactly FAIL in every mode;
 *   8. the figures' record (TM3): with -Dcbs.timing.tsv naming a file, each
 *      assertion's line writes a row for every figure it judged - harness,
 *      label, measured, budget, unit, status (OK, SOFT or FAIL), the figure's
 *      place in the assertion - and with none named nothing is written; a
 *      child JVM started as AllChecks starts one writes to the same file;
 *   9. the margin's arithmetic (tools/timing-pair.awk, which the ship gate runs
 *      on main's figures and the merge's): the batch passes at the margin and
 *      stops a hair over it, the median decides and not one slow round, main's
 *      median over the budget too or the batch's within it passes (SP1 item 7),
 *      a side with fewer than two figures stops, a line that is no clock's
 *      stops, and a failed harness's own count passes only with a timing red
 *      that passes.
 *
 * A harness that times prints its lines through the labelled verdict
 * (Timing.verdict(ok, label)), and no assertion compares two measured times
 * but through Timing (section 6): MapCheck's "the view adds nothing a tile",
 * which compared the view's time with section 5's and so stayed hard in the
 * soft suite, went red on a merge that could not reach it (CC1-S, 2026-10-26).
 *
 * It sets the property itself in every section and restores it at the end, so
 * it passes whatever the run was given.
 */
public class TimingCheck {

    static int fails = 0;

    /** This harness's own assertion line: never Timing.verdict, so its own lines are never soft. */
    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-66s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** The fixture's own budget, in milliseconds: zero, so its measured figure always misses; never a real dial's budget. */
    static final double FIXTURE_BUDGET_MS = 0;

    /** Where the sources lie, seen from the repository root, which is where the suite runs. */
    static final Path SOURCES = Paths.get("src", "main", "java", "ham", "citybuildersim");

    /** A line that compares a figure against a *_MS or *_NS dial, either way round: what a timing assertion looked like before it went through Timing. */
    static final Pattern COMPARES_DIAL = Pattern.compile(
            "(<=?|>=?)\\s*\\(?([A-Za-z]+\\.)?[A-Z][A-Z_]*_(MS|NS)\\b"
            + "|\\b[A-Z][A-Z_]*_(MS|NS)\\b(\\s*\\*\\s*[0-9.]+)?\\s*(<=?|>=?)");

    /** The file of the one allowance: the line below is a test of the clock's own resolution, which asserts no figure, so it stays a bare comparison. */
    static final String ALLOWED_FILE = "MapCheck.java";

    /** The one line the dial rule allows outside Timing: MapCheck deciding whether the thread-CPU clock is fine enough to time with at all (a test of the clock's resolution, not an assertion). */
    static final String ALLOWED_TEXT = "cpuStepMs() <= CPU_STEP_MS";

    /** What a child JVM is started as: the fixture's own main, below. */
    static final String FIXTURE_HARNESS = "TimingCheck$Fixture";

    /** Where the walk of every source starts. */
    static final Path ALL_SOURCES = Paths.get("src", "main", "java");

    /** Kept so the measured work cannot be optimised away. */
    private static volatile long sunk;

    public static void main(String[] args) throws Exception {
        String saved = System.getProperty(Timing.PROPERTY);
        String savedTsv = System.getProperty(Timing.TSV_PROPERTY);
        System.clearProperty(Timing.TSV_PROPERTY);   // the fixtures of sections 1 to 7 miss on purpose: their rows are not the suite's
        try {
            hard();
            soft();
            onlyAClock();
            propertyValues();
            acrossTheJvm();
            suiteGoesThroughIt();
            restUntouched();
            theFiguresRecord();
            theMarginsArithmetic();
        } finally {
            if (savedTsv == null) System.clearProperty(Timing.TSV_PROPERTY);
            else System.setProperty(Timing.TSV_PROPERTY, savedTsv);
            setMode(saved);
            Timing.verdict(true);
        }
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * The fixture: a miniature harness with its own failure count and its own
     * output, whose line is the suite's, printed through Timing.verdict. In
     * process it writes to a buffer; as a child JVM (its main, below) to
     * System.out, one timing assertion that cannot hold.
     */
    public static final class Fixture {

        int fails = 0;
        private final ByteArrayOutputStream bytes;
        private final PrintStream out;

        Fixture() {
            bytes = new ByteArrayOutputStream();
            out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        }

        Fixture(PrintStream to) {
            bytes = null;
            out = to;
        }

        void check(String label, boolean ok) {
            if (!ok) fails++;
            out.println(String.format("%-40s %s", label, Timing.verdict(ok, label)));
        }

        /** Everything printed so far (empty when it prints to a stream). */
        String text() {
            return bytes == null ? "" : new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        }

        /** The last line printed, without its line end. */
        String last() {
            String t = text().strip();
            int i = t.lastIndexOf('\n');
            return (i < 0 ? t : t.substring(i + 1)).strip();
        }

        /** What a child JVM runs: one timing assertion of a measured figure against FIXTURE_BUDGET_MS, to System.out; exits with its failure count. */
        public static void main(String[] args) {
            Fixture fx = new Fixture(System.out);
            fx.check("a measured figure against a zero budget", Timing.within(measured(), FIXTURE_BUDGET_MS));
            System.exit(fx.fails);
        }
    }

    /** A small piece of work timed on the nanosecond clock, in milliseconds, repeated at double the size until the clock has moved: always above zero. */
    static double measured() {
        long sink = 0;
        for (int spin = 1; ; spin *= 2) {
            long t = System.nanoTime();
            for (int i = 0; i < spin; i++) sink += Long.numberOfTrailingZeros(i + sink + 1);
            long elapsed = System.nanoTime() - t;
            if (elapsed > 0) {
                sunk = sink;
                return elapsed / 1e6;
            }
        }
    }

    /** Sets the property this run's mode is read from; null clears it. */
    static void setMode(String value) {
        if (value == null) System.clearProperty(Timing.PROPERTY);
        else System.setProperty(Timing.PROPERTY, value);
    }

    static String modeName(String value) {
        return value == null ? "the property unset" : "the property " + (value.isEmpty() ? "empty" : value);
    }

    static boolean endsOk(Fixture fx) {
        return fx.last().endsWith(" OK");
    }

    static boolean endsFail(Fixture fx) {
        return fx.last().endsWith(" FAIL");
    }

    static int count(String text, String part) {
        int n = 0;
        for (int i = text.indexOf(part); i >= 0; i = text.indexOf(part, i + part.length())) n++;
        return n;
    }

    /** One assertion for a rule that has offenders: it holds when there are none, and each offender is a failing line of its own, so -f names it. */
    static void none(String label, List<String> offenders) {
        if (offenders.isEmpty()) check(label, true);
        else for (String o : offenders) check(label + ": " + o, false);
    }

    static String readSource(Path p) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    /* =====================================================================
     * 1. HARD
     * ===================================================================== */

    static void hard() {
        System.out.println("--- 1. hard: a timing miss fails ---");
        for (String mode : new String[] { null, Timing.HARD }) {
            setMode(mode);
            Fixture fx = new Fixture();
            fx.check("a measured figure against a zero budget", Timing.within(measured(), FIXTURE_BUDGET_MS));
            check(modeName(mode) + ": the missed timing assertion fails", endsFail(fx));
            check(modeName(mode) + ": and the fixture counts it", fx.fails == 1);
        }
        setMode(null);
        Fixture fx = new Fixture();
        fx.check("a figure within a budget it can meet", Timing.within(measured(), Double.MAX_VALUE));
        check("a figure within a budget it can meet prints OK", endsOk(fx));
        check("and the fixture counts nothing", fx.fails == 0);
    }

    /* =====================================================================
     * 2. SOFT
     * ===================================================================== */

    static void soft() {
        System.out.println("--- 2. soft: a timing miss is told, not counted ---");
        setMode(Timing.SOFT);
        Fixture fx = new Fixture();
        fx.check("a measured figure against a zero budget", Timing.within(measured(), FIXTURE_BUDGET_MS));
        String line = fx.last();
        check("the missed assertion's line says SOFT", line.contains(" SOFT - "));
        check("it matches Timing.SOFT_LINE", Timing.SOFT_LINE.matcher(line).find());
        check("it carries the figure, in ms", Pattern.compile("SOFT - [0-9][0-9.]* ms, ").matcher(line).find());
        check("it carries the budget", line.endsWith("budget " + (long) FIXTURE_BUDGET_MS + " ms"));
        check("and the fixture counts nothing", fx.fails == 0);

        fx.check("a known figure against a zero budget", Timing.within(1.5, 0));
        check("a figure of 1.5 is told as 1.5 ms, budget 0 ms", fx.last().endsWith("SOFT - 1.5 ms, budget 0 ms"));

        fx.check("a figure in nanoseconds against zero", Timing.within(1.5, 0, "ns"));
        check("the unit it names is the line's: 1.5 ns, budget 0 ns", fx.last().endsWith("SOFT - 1.5 ns, budget 0 ns"));
        check("and no milliseconds are told", !fx.last().contains(" ms"));

        double f = measured();
        fx.check("below, with a figure equal to its budget", Timing.below(f, f));
        check("below(f, f), a strict miss, goes SOFT", Timing.SOFT_LINE.matcher(fx.last()).find());
        check("and the fixture still counts nothing", fx.fails == 0);

        setMode(null);
        Fixture hard = new Fixture();
        hard.check("below, with a figure equal to its budget", Timing.below(f, f));
        check("below(f, f) hard fails", endsFail(hard));
        check("and the fixture counts it", hard.fails == 1);
        hard.check("within, with a figure equal to its budget", Timing.within(f, f));
        check("within(f, f) hard holds: at most is not below", endsOk(hard) && hard.fails == 1);
    }

    /* =====================================================================
     * 3. ONLY A CLOCK IS SOFT
     * ===================================================================== */

    static void onlyAClock() {
        System.out.println("--- 3. only a clock is soft ---");
        setMode(Timing.SOFT);
        Timing.verdict(true);
        Fixture fx = new Fixture();
        boolean broken = false;

        fx.check("a plain false assertion", broken);
        check("soft: a false non-timing assertion still fails", endsFail(fx));
        check("and counted", fx.fails == 1);

        fx.check("false and a missed timing figure", broken && Timing.within(measured(), FIXTURE_BUDGET_MS));
        check("soft: false && a miss fails", endsFail(fx));
        check("and counted", fx.fails == 2);

        fx.check("a missed timing figure and false", Timing.within(measured(), FIXTURE_BUDGET_MS) && broken);
        check("soft: a miss && false fails", endsFail(fx));
        check("and counted", fx.fails == 3);

        fx.check("an assertion after a FAIL that held a miss", true);
        check("it prints OK: the miss a failure swallowed is cleared", endsOk(fx));

        fx.check("a missed timing figure alone", Timing.within(measured(), FIXTURE_BUDGET_MS));
        check("a miss alone is SOFT", Timing.SOFT_LINE.matcher(fx.last()).find());
        check("and not counted", fx.fails == 3);
        fx.check("an assertion after a told SOFT line", true);
        check("it prints OK: the misses are cleared", endsOk(fx));

        fx.check("a figure that is not a number", Timing.within(Double.NaN, 1));
        check("soft: a NaN figure fails, never measured", endsFail(fx));
        check("and counted", fx.fails == 4);

        fx.check("two missed figures in one condition", Timing.within(3.0, 1) && Timing.within(7.5, 2));
        String line = fx.last();
        check("one SOFT line", count(line, "SOFT - ") == 1);
        check("it carries both figures and budgets",
                line.contains("3 ms, budget 1 ms") && line.contains("7.5 ms, budget 2 ms"));
        check("and the fixture counts nothing more", fx.fails == 4);
    }

    /* =====================================================================
     * 4. THE PROPERTY'S VALUES
     * ===================================================================== */

    static void propertyValues() {
        System.out.println("--- 4. the property's values: soft is exactly \"soft\" ---");
        setMode(Timing.SOFT);
        check("soft() is true for soft", Timing.soft());
        for (String v : new String[] { null, "", Timing.HARD, "SOFT", "yes" }) {
            setMode(v);
            check("soft() is false for " + (v == null ? "an unset property" : "\"" + v + "\""), !Timing.soft());
        }
    }

    /* =====================================================================
     * 5. ACROSS THE JVM BOUNDARY
     * ===================================================================== */

    private record Run(int exit, String output) { }

    /** A child JVM started exactly as AllChecks starts a harness, with the parent's property the only source of its mode. */
    static Run runFixture(String javaExe, String classpath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(AllChecks.command(javaExe, classpath, FIXTURE_HARNESS, true));
        pb.environment().remove("JDK_JAVA_OPTIONS");
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String output = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (!p.waitFor(60, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            return new Run(-1, output);
        }
        return new Run(p.exitValue(), output);
    }

    static void acrossTheJvm() throws Exception {
        System.out.println("--- 5. across the JVM boundary: AllChecks forwards the property ---");
        String javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        setMode(Timing.SOFT);
        List<String> soft = AllChecks.command(javaExe, classpath, FIXTURE_HARNESS, true);
        check("soft here: forwarded() is -D" + Timing.PROPERTY + "=" + Timing.SOFT,
                ("-D" + Timing.PROPERTY + "=" + Timing.SOFT).equals(Timing.forwarded()));
        check("soft here: the child's command carries it", soft.contains(Timing.forwarded()));
        Run softRun = runFixture(javaExe, classpath);
        check("soft here: the child JVM exits 0", softRun.exit() == 0);
        check("soft here: and prints a SOFT line", Timing.SOFT_LINE.matcher(softRun.output()).find());

        setMode(null);
        List<String> unset = AllChecks.command(javaExe, classpath, FIXTURE_HARNESS, true);
        check("unset here: forwarded() is null", Timing.forwarded() == null);
        check("unset here: the child's command carries no -D" + Timing.PROPERTY,
                unset.stream().noneMatch(a -> a.startsWith("-D" + Timing.PROPERTY)));
        Run hardRun = runFixture(javaExe, classpath);
        check("unset here: the child JVM exits 1", hardRun.exit() == 1);
        check("unset here: and its line fails", hardRun.output().contains("FAIL"));

        setMode(Timing.SOFT);
        Fixture fx = new Fixture();
        fx.check("a line that held", true);
        fx.check("a line that went soft", Timing.within(measured(), FIXTURE_BUDGET_MS));
        fx.check("a line that failed", false);
        Path log = Files.createTempFile("timingcheck", ".log");
        try {
            Files.write(log, fx.text().getBytes(StandardCharsets.UTF_8));
            check("AllChecks.softLines counts one in a log of a held, a soft and a failed line", AllChecks.softLines(log) == 1);
        } finally {
            Files.deleteIfExists(log);
        }
        check("and the temp log is removed", !Files.exists(log));
    }

    /* =====================================================================
     * 6. THE SUITE GOES THROUGH IT
     * ===================================================================== */

    static void suiteGoesThroughIt() throws IOException {
        System.out.println("--- 6. the suite goes through Timing ---");
        if (!Files.isDirectory(SOURCES)) {
            check("the sources are at " + SOURCES + " (the suite runs from the repository root)", false);
            return;
        }
        List<Path> checks;
        try (Stream<Path> s = Files.list(SOURCES)) {
            checks = s.filter(p -> p.getFileName().toString().endsWith("Check.java"))
                    .filter(p -> !p.getFileName().toString().equals("TimingCheck.java"))
                    .sorted().toList();
        }
        check("the *Check sources are found", !checks.isEmpty());

        // a. no figure against a *_MS or *_NS dial, but through Timing
        List<String> offenders = new ArrayList<>();
        int allowed = 0;
        for (Path p : checks) {
            String file = p.getFileName().toString();
            String[] lines = readSource(p).split("\\R");
            for (int i = 0; i < lines.length; i++) {
                if (!COMPARES_DIAL.matcher(lines[i]).find()) continue;
                if (file.equals(ALLOWED_FILE) && lines[i].contains(ALLOWED_TEXT)) { allowed++; continue; }
                offenders.add(file + ":" + (i + 1) + "  " + lines[i].strip());
            }
        }
        none("no *Check compares a figure against a *_MS or *_NS dial but through Timing", offenders);
        check("the one allowance still matches exactly one line (a stale one is a finding)", allowed == 1);

        // b. the harnesses that call Timing are Timing.HARNESSES
        Set<String> timed = new TreeSet<>();
        for (Path p : checks) {
            String src = readSource(p);
            if (src.contains("Timing.within(") || src.contains("Timing.below(")) {
                String name = p.getFileName().toString();
                timed.add(name.substring(0, name.length() - ".java".length()));
            }
        }
        Set<String> listed = new TreeSet<>(Timing.HARNESSES);
        List<String> mismatched = new ArrayList<>();
        for (String n : timed) if (!listed.contains(n)) mismatched.add(n + " calls Timing.within or below, and is not in Timing.HARNESSES");
        for (String n : listed) if (!timed.contains(n)) mismatched.add(n + " is in Timing.HARNESSES, and calls neither");
        none("the harnesses that call Timing.within or below are exactly Timing.HARNESSES", mismatched);

        // c. each prints through Timing.verdict, with the label its line prints (TM3: the figures' rows carry it)
        List<String> unprinted = new ArrayList<>();
        Pattern labelled = Pattern.compile("Timing\\.verdict\\(\\s*\\w+\\s*,\\s*\\w+\\s*\\)");
        for (String n : timed) {
            Path p = SOURCES.resolve(n + ".java");
            String src = readSource(p);
            int calls = count(src, "Timing.verdict("), withLabel = 0;
            for (java.util.regex.Matcher m = labelled.matcher(src); m.find(); ) withLabel++;
            if (calls == 0) unprinted.add(n + " does not print through Timing.verdict");
            else if (withLabel != calls) unprinted.add(n + " has " + (calls - withLabel) + " Timing.verdict call(s) without the label (Timing.verdict(ok, label))");
        }
        none("each of them prints its assertion lines through Timing.verdict(ok, label)", unprinted);

        // d. the property is read in one place
        String quoted = "\"" + Timing.PROPERTY + "\"";
        List<String> readers = new ArrayList<>();
        try (Stream<Path> s = Files.walk(ALL_SOURCES)) {
            for (Path p : s.filter(q -> q.toString().endsWith(".java")).sorted().toList()) {
                if (p.getFileName().toString().equals("Timing.java")) continue;
                if (readSource(p).contains(quoted)) readers.add(ALL_SOURCES.relativize(p) + " names the property's string");
            }
        }
        String quotedTsv = "\"" + Timing.TSV_PROPERTY + "\"";
        try (Stream<Path> s = Files.walk(ALL_SOURCES)) {
            for (Path p : s.filter(q -> q.toString().endsWith(".java")).sorted().toList()) {
                if (p.getFileName().toString().equals("Timing.java")) continue;
                if (readSource(p).contains(quotedTsv)) readers.add(ALL_SOURCES.relativize(p) + " names the figures file property's string");
            }
        }
        none("no source but Timing.java holds the properties' string literals", readers);
        check("and Timing.java holds them, so that rule is not vacuous", readSource(SOURCES.resolve("Timing.java")).contains(quoted)
                && readSource(SOURCES.resolve("Timing.java")).contains(quotedTsv));

        // f. no assertion line compares a measured time (a variable named ...Ms, ...Ns, ...Nanos) with < <= > >= but through Timing
        //    (TM3: MapCheck compared the view's time with section 5's, a clock's comparison that stayed hard in the soft suite)
        Pattern measured = Pattern.compile("[A-Za-z_]*(Ms|Ns|Nanos)(\\[[^\\]]*\\])?\\s*(<=?|>=?)(?!=)|(<=?|>=?)\\s*[A-Za-z_]*(Ms|Ns|Nanos)(\\[[^\\]]*\\])?\\b");
        Pattern assertion = Pattern.compile("^\\s*(check|report|assertTrue|assertTimed)\\(");
        List<String> compared = new ArrayList<>();
        for (Path p : checks) {
            String file = p.getFileName().toString();
            String[] lines = readSource(p).split("\\R");
            for (int i = 0; i < lines.length; i++) {
                if (assertion.matcher(lines[i]).find() && measured.matcher(lines[i]).find() && !lines[i].contains("Timing."))
                    compared.add(file + ":" + (i + 1) + "  " + lines[i].strip());
            }
        }
        none("no assertion compares a measured time but through Timing", compared);

        // e. AllChecks runs them all, and this
        List<String> registry = Arrays.asList(AllChecks.HARNESSES);
        List<String> absent = new ArrayList<>();
        for (String n : Timing.HARNESSES) if (!registry.contains(n)) absent.add(n + " is not in AllChecks.HARNESSES");
        none("AllChecks.HARNESSES holds every name in Timing.HARNESSES", absent);
        check("and holds TimingCheck", registry.contains("TimingCheck"));
    }

    /* =====================================================================
     * 7. THE REST OF THE SUITE IS UNTOUCHED
     * ===================================================================== */

    static void restUntouched() {
        System.out.println("--- 7. with no miss held, the verdict is the old one ---");
        for (String mode : new String[] { null, Timing.HARD, Timing.SOFT }) {
            setMode(mode);
            Timing.verdict(true);
            check("verdict(true) is exactly OK, " + modeName(mode), "OK".equals(Timing.verdict(true)));
            check("verdict(false) is exactly the old failing word, " + modeName(mode), "FAIL".equals(Timing.verdict(false)));
        }
    }

    /* =====================================================================
     * 8. THE FIGURES' RECORD (TM3)
     * ===================================================================== */

    /** A figures file's rows, each split on tabs (a trailing empty column kept); none when the file is not there. */
    static List<String[]> rows(Path file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        if (!Files.exists(file)) return rows;
        for (String line : readSource(file).split("\\R")) if (!line.isEmpty()) rows.add(line.split("\t", -1));
        return rows;
    }

    static void theFiguresRecord() throws Exception {
        System.out.println("--- 8. the figures' record: each timing assertion writes its figure and budget for the gate ---");
        Path dir = TempDirs.create("timing-tsv");
        Path file = dir.resolve("timing.tsv");

        System.clearProperty(Timing.TSV_PROPERTY);
        setMode(Timing.HARD);
        Fixture fx = new Fixture();
        fx.check("a measured figure against a zero budget", Timing.within(measured(), FIXTURE_BUDGET_MS));
        check("no file named: none is written, and nothing is forwarded to a child", !Files.exists(file) && Timing.forwardedTsv() == null);

        System.setProperty(Timing.TSV_PROPERTY, file.toString());
        check("named: forwardedTsv() is -D" + Timing.TSV_PROPERTY + "=<the file>", ("-D" + Timing.TSV_PROPERTY + "=" + file).equals(Timing.forwardedTsv()));

        // a. a hard miss: FAIL, with its figure, its budget and its unit
        fx = new Fixture();
        fx.check("a measured figure against a zero budget", Timing.within(measured(), FIXTURE_BUDGET_MS));
        List<String[]> r = rows(file);
        check("hard miss: one row of seven columns", r.size() == 1 && r.get(0).length == 7);
        String[] row = r.isEmpty() ? new String[7] : r.get(0);
        check("...the harness is the class that printed the line (this one), the label as printed",
                "TimingCheck".equals(row[0]) && "a measured figure against a zero budget".equals(row[1]));
        check("...the figure is a measured one above the budget, the budget is the fixture's, in ms",
                row[2] != null && Double.parseDouble(row[2]) > FIXTURE_BUDGET_MS && Double.parseDouble(row[3]) == FIXTURE_BUDGET_MS && "ms".equals(row[4]));
        check("...it is FAIL, the first figure of its assertion", "FAIL".equals(row[5]) && "1".equals(row[6]));

        // b. soft: the same miss is SOFT; a held figure is OK with its figure and budget; the unit is the one named
        Files.delete(file);
        setMode(Timing.SOFT);
        fx = new Fixture();
        fx.check("a measured figure against a zero budget", Timing.within(measured(), FIXTURE_BUDGET_MS));
        fx.check("a figure that held", Timing.within(12.5, 40));
        fx.check("a figure in microseconds", Timing.within(7, 90, "us"));
        r = rows(file);
        check("soft: three rows, one for each assertion", r.size() == 3);
        check("...the missed one is SOFT", r.size() == 3 && "SOFT".equals(r.get(0)[5]));
        check("...the held one is OK with its figure and its budget", r.size() == 3 && "OK".equals(r.get(1)[5]) && "12.5".equals(r.get(1)[2]) && "40".equals(r.get(1)[3]));
        check("...and the one in microseconds says so", r.size() == 3 && "us".equals(r.get(2)[4]) && "90".equals(r.get(2)[3]));

        // c. two figures in one condition: a row for each, numbered; a label with a tab and a line end is one clean row
        Files.delete(file);
        setMode(Timing.HARD);
        fx = new Fixture();
        fx.check("two figures,\twith a tab\nand a line end", Timing.within(measured(), FIXTURE_BUDGET_MS) & Timing.within(measured(), FIXTURE_BUDGET_MS));
        r = rows(file);
        check("two figures in one condition: two rows, numbered 1 and 2, the one label",
                r.size() == 2 && "1".equals(r.get(0)[6]) && "2".equals(r.get(1)[6]) && r.get(0)[1].equals(r.get(1)[1]));
        check("...a tab or a line end in a label is a space, and a row stays one line of seven columns",
                r.size() == 2 && "two figures, with a tab and a line end".equals(r.get(0)[1]) && r.get(0).length == 7);

        // d. a figure that is not a number is FAIL even soft; an unlabelled verdict writes nothing and clears what was judged
        Files.delete(file);
        setMode(Timing.SOFT);
        fx = new Fixture();
        fx.check("a figure that was never measured", Timing.within(Double.NaN, 40));
        r = rows(file);
        check("NaN, soft: the row says FAIL, as the verdict does", r.size() == 1 && "FAIL".equals(r.get(0)[5]) && "NaN".equals(r.get(0)[2]));
        Files.delete(file);
        Timing.within(3, 40);
        Timing.verdict(true);
        fx = new Fixture();
        fx.check("a figure after an unlabelled verdict", Timing.within(5, 40));
        r = rows(file);
        check("an unlabelled verdict writes no row, and what it judged is not told on the next line", r.size() == 1 && "5".equals(r.get(0)[2]));

        // e. a child JVM started as AllChecks starts one writes to the same file
        String javaExe = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        Files.delete(file);
        setMode(Timing.HARD);
        check("the child's command carries the file", AllChecks.command(javaExe, classpath, FIXTURE_HARNESS, true).contains(Timing.forwardedTsv()));
        runFixture(javaExe, classpath);
        r = rows(file);
        check("a child JVM writes its row to the file: TimingCheck's, FAIL",
                r.size() == 1 && "TimingCheck".equals(r.get(0)[0]) && "FAIL".equals(r.get(0)[5]));
        System.clearProperty(Timing.TSV_PROPERTY);
    }

    /* =====================================================================
     * 9. THE MARGIN'S ARITHMETIC (TM3): tools/timing-pair.awk
     * ===================================================================== */

    static final Path PAIR_AWK = Paths.get("tools", "timing-pair.awk");

    /** awk with these arguments, the repository root as its folder; its output lines, or null when awk cannot be started here. */
    static List<String> awk(List<String> args) throws Exception {
        List<String> cmd = new ArrayList<>(List.of("awk"));
        cmd.addAll(args);
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p;
        try {
            p = pb.start();
        } catch (IOException e) {
            return null;
        }
        String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (!p.waitFor(60, TimeUnit.SECONDS)) p.destroyForcibly();
        List<String> lines = new ArrayList<>();
        for (String l : out.split("\\R")) if (!l.isEmpty()) lines.add(l);
        return lines;
    }

    /** The pairs file's rows for one label: the figures of each side in turn, one per round. */
    static String pairRows(String harness, String label, double[] main, double[] batch) {
        StringBuilder b = new StringBuilder();
        for (int r = 0; r < Math.max(main.length, batch.length); r++) {
            if (r < main.length) b.append("main\t").append(r + 1).append('\t').append(harness).append('\t').append(label).append('\t').append(main[r]).append("\t100\tms\tOK\t1\n");
            if (r < batch.length) b.append("batch\t").append(r + 1).append('\t').append(harness).append('\t').append(label).append('\t').append(batch[r]).append("\t100\tms\tOK\t1\n");
        }
        return b.toString();
    }

    /** The judge's answer for these files: one "VERDICT\tline\twhy" per residual line. */
    static List<String> judge(Path dir, String pairs, String hard, String residual, String margin) throws Exception {
        Files.writeString(dir.resolve("pairs.tsv"), pairs, StandardCharsets.UTF_8);
        Files.writeString(dir.resolve("hard.tsv"), hard, StandardCharsets.UTF_8);
        Files.writeString(dir.resolve("residual.txt"), residual, StandardCharsets.UTF_8);
        List<String> args = new ArrayList<>(List.of("-v", "mode=judge"));
        if (margin != null) args.addAll(List.of("-v", "margin=" + margin));
        args.addAll(List.of("-f", PAIR_AWK.toString(), dir.resolve("pairs.tsv").toString(), dir.resolve("hard.tsv").toString(), dir.resolve("residual.txt").toString()));
        return awk(args);
    }

    static void theMarginsArithmetic() throws Exception {
        System.out.println("--- 9. the margin's arithmetic: the batch against main, at the median of the paired rounds ---");
        if (!Files.isRegularFile(PAIR_AWK)) {
            check(PAIR_AWK + " is there (the suite runs from the repository root)", false);
            return;
        }
        Path dir = TempDirs.create("timing-pair");
        Files.writeString(dir.resolve("empty"), "", StandardCharsets.UTF_8);
        List<String> dial = awk(List.of("-v", "mode=dial", "-f", PAIR_AWK.toString(), dir.resolve("empty").toString()));
        if (dial == null) {
            System.out.println("awk is not on the PATH here, so section 9 did not run (the ship gate runs it)");
            return;
        }
        double margin = dial.isEmpty() ? Double.NaN : Double.parseDouble(dial.get(0));
        check("the dial is a margin of 15 percent (the brief's), told by the script", margin == 15);

        String label = "a month's FX-side map work at 5B takes no more than 100 ms";
        String hard = "MapCheck\t" + label + "\t119.9\t100\tms\tFAIL\t1\nMapCheck\ta figure that held\t3\t80\tms\tOK\t1\n";
        String residual = "MapCheck | exit 1\nMapCheck | " + label + "     FAIL\nMapCheck | 1 FAILED\n";
        double[] steady = { 100, 100, 100 };
        double atMargin = 100 * (1 + margin / 100);

        List<String> v = judge(dir, pairRows("MapCheck", label, steady, new double[] { atMargin, atMargin, atMargin }), hard, residual, null);
        check("a batch exactly at the margin over main passes (the margin is a 'more than')", v != null && v.size() == 3 && v.get(1).startsWith("PASS\t"));
        check("...the line's notes carry both medians, the budget and why (main at the budget is within it)", v != null && v.size() == 3 && v.get(1).contains("main 100 ms") && v.get(1).contains("batch 115 ms")
                && v.get(1).contains("budget 100 ms") && v.get(1).endsWith(": within 15% of main's"));
        check("...and the harness's own count and exit pass with it", v != null && v.size() == 3 && v.get(0).startsWith("PASS\t") && v.get(2).startsWith("PASS\t"));

        double over = atMargin + 0.1;
        v = judge(dir, pairRows("MapCheck", label, steady, new double[] { over, over, over }), hard, residual, null);
        check("a hair over the margin stops, main at the budget and the batch over it: FAIL, over main's by more than the margin, the figures named",
                v != null && v.size() == 3 && v.get(1).startsWith("FAIL\t") && v.get(1).contains("over main's by more than 15%") && v.get(1).contains("batch 115 ms"));
        check("...and the harness's own count and exit stop with it", v != null && v.size() == 3 && v.get(0).startsWith("FAIL\t") && v.get(2).startsWith("FAIL\t"));

        v = judge(dir, pairRows("MapCheck", label, steady, new double[] { 100, 100, 900 }), hard, residual, null);
        check("the median decides, not one slow round of the batch's: [100 100 900] against main's 100 passes", v != null && v.size() == 3 && v.get(1).startsWith("PASS\t"));
        v = judge(dir, pairRows("MapCheck", label, new double[] { 100, 900, 900 }, new double[] { 100, 100, 130 }), hard, residual, null);
        check("...nor one fast round of main's: main's median 900 against the batch's 100 passes", v != null && v.size() == 3 && v.get(1).startsWith("PASS\t"));
        v = judge(dir, pairRows("MapCheck", label, new double[] { 60, 70 }, new double[] { 100, 110 }), hard, residual, null);
        check("two rounds are a median of two (the mean): main 65, batch 105 stops", v != null && v.size() == 3 && v.get(1).startsWith("FAIL\t") && v.get(1).contains("main 65 ms, batch 105 ms"));

        v = judge(dir, pairRows("MapCheck", label, steady, new double[] { 130, 130, 130 }), hard, residual, "40");
        check("the margin is a dial: 30% slower passes at 40", v != null && v.size() == 3 && v.get(1).startsWith("PASS\t"));
        v = judge(dir, pairRows("MapCheck", label, new double[] { 200, 200, 200 }, new double[] { 260, 260, 260 }), hard, residual, null);
        check("main's median over the budget too passes though the batch is 30% over main's: main over the budget too", v != null && v.size() == 3 && v.get(1).startsWith("PASS\t") && v.get(1).endsWith(": main over the budget too"));
        v = judge(dir, pairRows("MapCheck", label, new double[] { 50, 50, 50 }, new double[] { 100, 100, 100 }), hard, residual, null);
        check("the batch's median within the budget (at it) passes though it is double main's: the batch within the budget", v != null && v.size() == 3 && v.get(1).startsWith("PASS\t") && v.get(1).endsWith(": the batch within the budget"));

        v = judge(dir, pairRows("MapCheck", label, steady, new double[] { 100 }), hard, residual, null);
        check("a side with one figure has no median: FAIL, no pair to compare", v != null && v.size() == 3 && v.get(1).startsWith("FAIL\t") && v.get(1).contains("no pair to compare"));
        v = judge(dir, "", hard, residual, null);
        check("no figures at all (the paired runs did not run): FAIL, no pair to compare", v != null && v.size() == 3 && v.get(1).startsWith("FAIL\t") && v.get(1).contains("no pair to compare"));
        v = judge(dir, pairRows("MapCheck", "another assertion entirely", steady, steady), hard, residual, null);
        check("an assertion the pairs do not have (a batch's own new one, a label changed): FAIL, no pair", v != null && v.size() == 3 && v.get(1).startsWith("FAIL\t") && v.get(1).contains("no pair to compare"));

        String shifted = "a month's FX-side map work at 6B takes no more than 101 ms";
        v = judge(dir, pairRows("MapCheck", shifted, steady, steady), hard, residual, null);
        check("a label that prints a figure pairs with itself across the sides' digits (6B for 5B, 101 for 100)", v != null && v.size() == 3 && v.get(1).startsWith("PASS\t"));

        v = judge(dir, pairRows("MapCheck", label, steady, steady), hard, "MapCheck | exit 1\nMapCheck | a failure that is not a clock's     FAIL\nMapCheck | 2 FAILED\n", null);
        check("a red with no missed figure behind it is no timing red: FAIL, and the harness's own count with it",
                v != null && v.size() == 3 && v.get(0).startsWith("FAIL\t") && v.get(1).startsWith("FAIL\t") && v.get(1).contains("not a timing red") && v.get(2).startsWith("FAIL\t"));
        v = judge(dir, pairRows("MapCheck", label, steady, steady), hard,
                "MapCheck | exit 1\nMapCheck | " + label + "     FAIL\nMapCheck | a failure that is not a clock's     FAIL\nMapCheck | 2 FAILED\n", null);
        check("a passing timing red beside a red that is no clock's: the second stops, and the harness's count with it",
                v != null && v.size() == 4 && v.get(1).startsWith("PASS\t") && v.get(2).startsWith("FAIL\t") && v.get(0).startsWith("FAIL\t") && v.get(3).startsWith("FAIL\t"));
        v = judge(dir, pairRows("MapCheck", label, steady, steady), hard, "MapCheck | exit 1\n", null);
        check("a harness that failed with no timing red of its own: FAIL", v != null && v.size() == 1 && v.get(0).startsWith("FAIL\t"));

        List<String> names = awk(List.of("-v", "mode=harnesses", "-f", PAIR_AWK.toString(), dir.resolve("hard.tsv").toString(), dir.resolve("residual.txt").toString()));
        Files.writeString(dir.resolve("residual.txt"), residual + "GridCheck | exit 1\nGridCheck | a failure that is not a clock's     FAIL\n", StandardCharsets.UTF_8);
        names = awk(List.of("-v", "mode=harnesses", "-f", PAIR_AWK.toString(), dir.resolve("hard.tsv").toString(), dir.resolve("residual.txt").toString()));
        check("the harnesses to time again are those with a timing red among the lines: MapCheck, not GridCheck", names != null && names.equals(List.of("MapCheck")));
    }
}
