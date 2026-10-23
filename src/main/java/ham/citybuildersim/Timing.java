package ham.citybuildersim;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The one helper every timing assertion in the suite goes through: a figure
 * on a clock against its budget, hard as the harness wrote it, or soft while
 * another batch's checks or Jerus's game share the machine.
 *
 * <p>WHY (batch TM1, 2026-10-18): batches on the PC took turns under "one JVM
 * at a time on the machine", and waited on each other's suites and on
 * Jerus's game, though 14 cores and 32 GB fit two suites easily. What
 * clashed was only the timing assertions: EarthTerrainCheck 8 measured 116 ms
 * against its 80 ms with the game running, 27.8 ms without it. So under
 * {@code -Dcbs.timing=soft} a timing figure that misses its budget is not a
 * failure: the assertion's line says SOFT, with the figure and the budget,
 * and the harness does not count it. Without the property every assertion is
 * exactly as before, and no budget moved. The ship gate runs the timing
 * harnesses alone and hard ({@code AllChecks --timing}), so a budget is still
 * held where it means something.
 *
 * <p>How a harness uses it: the timing part of a condition is
 * {@code Timing.within(figure, budget)} in place of {@code figure <= budget},
 * and the harness's own line prints {@code Timing.verdict(ok)} in place of
 * {@code ok ? "OK" : "FAIL"}. A soft miss answers true and waits for the next
 * verdict, which then reads "SOFT - 116.2 ms, budget 80 ms". A condition whose
 * other part fails is FAIL as ever: only a clock is ever soft. TimingCheck
 * holds the mechanism, and holds that no harness compares a figure against a
 * {@code *_MS} or {@code *_NS} dial except through here.
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
            "GlobeCheck", "EarthTerrainCheck", "GridCheck", "SaveSlotCheck");

    /** A harness's assertion line that went soft, as {@link #verdict} words it: what AllChecks counts in a run's logs. */
    static final Pattern SOFT_LINE = Pattern.compile("\\sSOFT - ");

    /** The misses since the last verdict, each "figure unit, budget budget unit": what the next verdict prints. */
    private static final List<String> misses = new ArrayList<>();

    /** Whether timing is soft this run: the property read, every time, here and nowhere else. */
    static boolean soft() {
        return SOFT.equals(System.getProperty(PROPERTY));
    }

    /** A figure in milliseconds within its budget (at most it): {@code figure <= budget}, or soft. */
    static boolean within(double figure, double budget) {
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
    static String verdict(boolean ok) {
        String words = !ok ? "FAIL" : misses.isEmpty() ? "OK" : "SOFT - " + String.join("; ", misses);
        misses.clear();
        return words;
    }

    /** The argument a child JVM is given so it times as this one does: {@code -Dcbs.timing=...}, or null when the property is not set here. */
    static String forwarded() {
        String v = System.getProperty(PROPERTY);
        return v == null ? null : "-D" + PROPERTY + "=" + v;
    }
}
