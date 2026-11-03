package ham.citybuildersim;
import java.util.*;
/** The wealth study's probe (scratch only, never the tree): what each statement of Game.nextMonth() did to the households' savings and to the audit's pools. */
public final class WealthProbe {
    public static boolean on;
    /** line -> {net savings, gross savings, net pools, savings + pools} */
    public static final TreeMap<Integer, double[]> moved = new TreeMap<>();
    private static double lastS = Double.NaN, lastP = Double.NaN;
    public static void mark(int line, Game g) {
        if (!on) return;
        double s = g.getHouseholdBalance().totalSavings();
        double p = MoneyAudit.pooled(g);
        if (line >= 0 && !Double.isNaN(lastS) && (s != lastS || p != lastP)) {
            double[] a = moved.computeIfAbsent(line, k -> new double[4]);
            a[0] += s - lastS; a[1] += Math.abs(s - lastS); a[2] += p - lastP; a[3] += (s - lastS) + (p - lastP);
        }
        lastS = s; lastP = p;
    }
    public static void open(Game g) { lastS = g.getHouseholdBalance().totalSavings(); lastP = MoneyAudit.pooled(g); }
}
