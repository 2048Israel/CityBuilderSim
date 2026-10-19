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
 *      exactly OK and verdict(false) exactly FAIL in every mode.
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
        try {
            hard();
            soft();
            onlyAClock();
            propertyValues();
            acrossTheJvm();
            suiteGoesThroughIt();
            restUntouched();
        } finally {
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
            out.println(String.format("%-40s %s", label, Timing.verdict(ok)));
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

        // c. each prints through Timing.verdict
        List<String> unprinted = new ArrayList<>();
        for (String n : timed) {
            Path p = SOURCES.resolve(n + ".java");
            if (!readSource(p).contains("Timing.verdict(")) unprinted.add(n + " does not print through Timing.verdict");
        }
        none("each of them prints its assertion lines through Timing.verdict", unprinted);

        // d. the property is read in one place
        String quoted = "\"" + Timing.PROPERTY + "\"";
        List<String> readers = new ArrayList<>();
        try (Stream<Path> s = Files.walk(ALL_SOURCES)) {
            for (Path p : s.filter(q -> q.toString().endsWith(".java")).sorted().toList()) {
                if (p.getFileName().toString().equals("Timing.java")) continue;
                if (readSource(p).contains(quoted)) readers.add(ALL_SOURCES.relativize(p) + " names the property's string");
            }
        }
        none("no source but Timing.java holds the property's string literal", readers);
        check("and Timing.java holds it, so that rule is not vacuous", readSource(SOURCES.resolve("Timing.java")).contains(quoted));

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
}
