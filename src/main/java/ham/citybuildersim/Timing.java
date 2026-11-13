package ham.citybuildersim;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The suite's clock verdicts and the soft skip for work whose only verdict is time.
 *
 * WHY (TM1, HA1): concurrent runs measure the PC's load as well as the code;
 * the old gate spent 38 minutes doing clock-only rounds it did not judge.
 * Soft runs skip those sections before their rounds. Mixed correctness
 * sections still run; only a missed clock becomes SOFT and uncounted.
 * Hard runs retain all rounds and budgets; nightly timing judges regressions.
 *
 * A timing condition uses {@code Timing.within(figure, budget)} and prints
 * through {@code Timing.verdict(ok, label)}. A non-clock failure stays FAIL,
 * including beside a soft miss; NaN stays a failure. TimingCheck holds this
 * contract and the source rules. {@link #TSV_PROPERTY} records each labelled
 * figure, budget and unit for the nightly's paired comparisons (GR1).
 */
public final class Timing {

    private Timing() { }

    /** The system property that makes timing soft: {@code -Dcbs.timing=soft}; anything else, or none, is hard. */
    static final String PROPERTY = "cbs.timing";

    /** The property's value that makes timing soft. */
    static final String SOFT = "soft";

    /** The property's value the gate passes to say hard out loud (any value but SOFT is hard). */
    static final String HARD = "hard";

    /** The harnesses that hold a timing assertion: what {@code AllChecks --timing} runs, and the ship gate (TimingCheck holds it to the sources). */
    static final List<String> HARNESSES = List.of(
            "WorldCheck", "MapCheck", "ScaleCheck", "PlanCheck", "EarthCheck",
            "GlobeCheck", "EarthTerrainCheck", "GridCheck", "SaveSlotCheck", "KeptCheck", "IslandNetCheck", "BridgeCheck",
            "WorldMarketCheck", "ReplayCheck", "PartnerCallsCheck", "LandNationsCheck", "PortCheck", "QuayCraneCheck");

    /** A harness's assertion line that went soft, as {@link #verdict} words it: what AllChecks counts in a run's logs. */
    static final Pattern SOFT_LINE = Pattern.compile("\\sSOFT - ");

    /**
     * The system property that names the file every timing assertion appends
     * its figure to, {@code -Dcbs.timing.tsv=target/checks/timing.tsv} (TM3);
     * none, and nothing is written. One row per figure, tab-separated: the
     * harness, the assertion's label, the measured figure, its budget, the
     * unit, the status (OK within it, SOFT over it and not counted, FAIL over
     * it and counted) and the figure's place in the assertion (1, or 1 to n
     * when one condition holds n figures). The gate reads the file, never a
     * label's prose, to time main's classes and the merge's in turn.
     */
    static final String TSV_PROPERTY = "cbs.timing.tsv";

    /** The misses since the last verdict, each "figure unit, budget budget unit": what the next verdict prints. */
    private static final List<String> misses = new ArrayList<>();

    /** One figure judged since the last verdict, kept for the file's row (only while the file is named). */
    private record Reading(double figure, double budget, String unit, boolean held) { }

    /** The figures judged since the last verdict. */
    private static final List<Reading> readings = new ArrayList<>();

    /** Whether timing is soft this run: the property read, every time, here and nowhere else. */
    static boolean soft() {
        return SOFT.equals(System.getProperty(PROPERTY));
    }

    /** A section with only a clock's verdict does no rounds in a soft suite (HA1). */
    static boolean skip(String section) {
        if (!soft()) return false;
        System.out.println("SKIPPED soft timing: " + section);
        return true;
    }

    /** A figure in milliseconds within its budget (at most it): {@code figure <= budget}, or soft. Public, with verdict, for ui.UiProbe (UP2). */
    public static boolean within(double figure, double budget) {
        return within(figure, budget, "ms");
    }

    /** A figure within its budget (at most it), in the unit named: {@code figure <= budget}, or soft. */
    static boolean within(double figure, double budget, String unit) {
        return judge(figure <= budget, figure, budget, unit);
    }

    /** A figure in milliseconds below its budget (strictly): {@code figure < budget}, or soft. */
    static boolean below(double figure, double budget) {
        return judge(figure < budget, figure, budget, "ms");
    }

    /**
     * The timing half of an assertion: true when it held; when it missed, true
     * all the same if timing is soft, the miss kept for the next verdict. A
     * figure that is not a number was never measured, which no load explains:
     * it fails, soft or hard.
     */
    private static boolean judge(boolean held, double figure, double budget, String unit) {
        if (tsv() != null) readings.add(new Reading(figure, budget, unit, held));
        if (held) return true;
        if (Double.isNaN(figure) || !soft()) return false;
        misses.add(String.format(Locale.ROOT, "%s %s, budget %s %s", figures(figure), unit, figures(budget), unit));
        return true;
    }

    /** A figure as the verdict prints it: a whole number bare (a budget of 80 is "80"), else a tenth from 1 up, whole from 100, and three significant places below 1 ("0.3", "0.000123"), never an exponent. */
    private static String figures(double v) {
        double a = Math.abs(v);
        if (v == Math.rint(v) && a < 1e15 || a >= 100) return String.format(Locale.ROOT, "%.0f", v);
        if (a >= 1) return String.format(Locale.ROOT, "%.1f", v);
        return new BigDecimal(v).round(new MathContext(3)).stripTrailingZeros().toPlainString();
    }

    /**
     * What an assertion's line ends with: FAIL when it failed, SOFT with each
     * timing miss's figure and budget when it held only because timing is
     * soft, OK otherwise. Without soft timing this is exactly
     * {@code ok ? "OK" : "FAIL"}. Every verdict clears the misses, so a soft
     * miss is never told on a later assertion's line.
     */
    public static String verdict(boolean ok) {
        String words = !ok ? "FAIL" : misses.isEmpty() ? "OK" : "SOFT - " + String.join("; ", misses);
        misses.clear();
        readings.clear();
        return words;
    }

    /**
     * {@link #verdict(boolean)}, and when {@link #TSV_PROPERTY} names a file
     * the figures judged since the last verdict written there under this
     * label, the harness's own (the class that called it). Every harness that
     * times prints its lines through this one (TimingCheck holds it), with the
     * label it prints.
     */
    public static String verdict(boolean ok, String label) {
        record(label);
        return verdict(ok);
    }

    /** The file the figures go to, or null when none is named. */
    private static String tsv() {
        String v = System.getProperty(TSV_PROPERTY);
        return v == null || v.isBlank() ? null : v;
    }

    /** Appends the pending figures as rows (TSV_PROPERTY says the columns), under an exclusive lock, so harnesses running at once (AllChecks.PARALLEL, SP1) never interleave their rows. A file that cannot be written loses its rows: no assertion fails for it, and the gate, finding none, judges the red as the batch's. */
    private static void record(String label) {
        String path = tsv();
        if (path == null || readings.isEmpty()) return;
        String harness = StackWalker.getInstance().walk(frames -> frames.map(StackWalker.StackFrame::getClassName)
                .filter(c -> !c.equals(Timing.class.getName())).findFirst().orElse("?"));
        harness = harness.substring(harness.lastIndexOf('.') + 1);
        if (harness.indexOf('$') > 0) harness = harness.substring(0, harness.indexOf('$'));
        String clean = label == null ? "" : label.replaceAll("[\\t\\r\\n]+", " ").strip();
        StringBuilder rows = new StringBuilder();
        int part = 0;
        for (Reading r : readings) {
            part++;
            String status = r.held() ? "OK" : !Double.isNaN(r.figure()) && soft() ? "SOFT" : "FAIL";
            rows.append(harness).append('\t').append(clean).append('\t').append(plain(r.figure())).append('\t')
                    .append(plain(r.budget())).append('\t').append(r.unit()).append('\t').append(status).append('\t').append(part).append('\n');
        }
        try {
            append(Path.of(path), rows.toString());
        } catch (IllegalStateException e) {
            // The timing judge treats absent figures as an incomplete run.
        }
    }

    /** Locked UTF-8 rows shared by timing and path observations (PO1). */
    static synchronized void append(Path path, String rows) {
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE); FileLock lock = ch.lock()) {
            ch.position(ch.size());
            ByteBuffer bytes = ByteBuffer.wrap(rows.getBytes(StandardCharsets.UTF_8));
            while (bytes.hasRemaining()) ch.write(bytes);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Cannot write check readings to " + path, e);
        }
    }

    /** A figure for the file: six significant places, never an exponent; NaN and the infinities as Java words them. */
    private static String plain(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return Double.toString(v);
        return new BigDecimal(v).round(new MathContext(6)).stripTrailingZeros().toPlainString();
    }

    /** The argument a child JVM is given so it times as this one does: {@code -Dcbs.timing=...}, or null when the property is not set here. */
    static String forwarded() {
        String v = System.getProperty(PROPERTY);
        return v == null ? null : "-D" + PROPERTY + "=" + v;
    }

    /** The argument a child JVM is given so it writes its figures to the same file: {@code -Dcbs.timing.tsv=...}, or null when none is named here. */
    static String forwardedTsv() {
        String v = tsv();
        return v == null ? null : "-D" + TSV_PROPERTY + "=" + v;
    }
}
