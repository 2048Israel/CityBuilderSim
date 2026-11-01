package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The faint lines behind the city in a replay (TR1, 2026-10-31), without the toolkit: a recorded series read once from the
 * recording's own history, each point a recorded month, and what a line is at the month on screen - how many of its points are
 * drawn, how high each stands against the highest the recording ever reaches, and the figure at its head. ui/ReplayGraphs
 * (ReplayMode's canvas) draws them; ReplayCheck 5 holds the arithmetic.
 *
 * WHY IT EXISTS (TR1): the trailer's 0:54-1:35 shot is the city growing fast with its GDP and population rising faintly behind
 * it. A line has to rise as time plays, so it is drawn from the recording's first month to the month on screen and scaled to the
 * whole recording - the highest point of any recorded month, not of the months drawn so far, which would always touch the top.
 */
public final class ReplayGraphs {

    private ReplayGraphs() { }

    /** The months a year of output is taken over: 12 - the header's own "a year", and it takes the month-to-month wobble out of a line that is meant to be soft. */
    public static final int YEAR = 12;

    /** One series: {@code months} ascending and {@code values} beside them, both only for the months of the recording; {@code max} the highest value among them (0 for none). */
    public record Line(String key, int[] months, double[] values, double max) {

        /** How many points are at or before {@code month}: the ones drawn when that month is on screen. */
        public int drawn(int month) {
            int n = 0;
            while (n < months.length && months[n] <= month) n++;
            return n;
        }

        /** The value at the last point drawn at {@code month}; 0 when none is. */
        public double head(int month) {
            int n = drawn(month);
            return n == 0 ? 0 : values[n - 1];
        }

        /** Point i's height against the recording's highest: 0 to 1 (0 for a line with no height at all). */
        public double share(int i) {
            return max > 0 ? Math.max(0, values[i]) / max : 0;
        }
    }

    /** Where a month stands across the recording, 0 at its first and 1 at its last (0 for a recording of one month). */
    public static double across(int month, int first, int last) {
        return last > first ? Math.max(0, Math.min(1, (month - first) / (double) (last - first))) : 0;
    }

    /**
     * The series {@code key} of the recording's history over its months {@code first} to {@code last}. A flow with {@code annual}
     * is a year of it: twelve times the mean of the last YEAR entries that have a figure (fewer in the first year), the way the
     * header's GDP tile quotes "per year" - population, a level, is read as it stands. An entry with no figure (the front padding
     * of a series that started late, NaN) is left out. A history with none of the key, or none at all, is a line with no points.
     */
    public static Line of(HistorySave master, String key, int first, int last, boolean annual) {
        List<Integer> months = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        double max = 0;
        if (master != null) {
            double[] raw = master.aligned(key);
            List<Integer> axis = master.getMonth();
            int n = Math.min(raw.length, axis.size());
            for (int i = 0; i < n; i++) {
                int month = axis.get(i);
                if (month < first || month > last || Double.isNaN(raw[i])) continue;
                double v = raw[i];
                if (annual) {
                    double sum = 0;
                    int count = 0;
                    for (int j = Math.max(0, i - YEAR + 1); j <= i; j++) {
                        if (!Double.isNaN(raw[j])) {
                            sum += raw[j];
                            count++;
                        }
                    }
                    v = count == 0 ? 0 : sum / count * YEAR;
                }
                months.add(month);
                values.add(v);
                max = Math.max(max, v);
            }
        }
        int[] m = new int[months.size()];
        double[] v = new double[m.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = months.get(i);
            v[i] = values.get(i);
        }
        return new Line(key, m, v, max);
    }
}
