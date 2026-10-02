package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * What a time chart shows, as numbers: the window of months it looks at and
 * how a drag, a wheel, a range button and the overview move it; the ticks on
 * its two axes; and the bands, the episodes and the decisions it lays over
 * the lines (0.7.23).
 *
 * WHY THIS EXISTS
 *
 * Jerus, on the play-through: "in teh graphs you should be able to pan the
 * chart just like yahoo finance does ... and like also the crisis labels and
 * all, dont you think that it should be more clear". The chart he was looking
 * at was a JavaFX LineChart on a window picked from three chips, with no year
 * on its axis and grey bands nothing named. Pan and zoom are arithmetic on a
 * window of months; year ticks are arithmetic on a calendar; which band is
 * which and where a flag goes are lists. None of it needs a toolkit, and all
 * of it can be wrong in a way a player only notices years into a city - a
 * window that drifts past the newest month, an axis that loses its years at
 * one zoom, a band named after the wrong recession. So it lives here, where
 * ChartCheck can hold it to its arithmetic without a screen, and the
 * interface (ui.TimeChart) draws what it says.
 *
 * NOTHING HERE DECIDES WHAT HAPPENED. The bands are YearBook.recessions(),
 * the spans YearBook.episodes() and the flags the DecisionLog's entries, as
 * they are; this class only says where on the chart each one goes.
 */
public final class ChartModel {

    /** The fewest months the main chart can be zoomed down to: half a year, seven points. */
    public static final int MIN_SPAN = 6;

    /** The range buttons, in months: one, five, ten and fifty years; "All" is the whole history. */
    public static final int[] RANGES = {12, 60, 120, 600};

    /** What the range buttons say, in RANGES' order, then the whole history's. */
    public static final String[] RANGE_NAMES = {"1Y", "5Y", "10Y", "50Y", "All"};

    /** The range that means the whole history. */
    public static final int ALL = Integer.MAX_VALUE;

    /** The range a chart opens on: ten years, what City History drew before it had buttons. */
    public static final int DEFAULT_RANGE = 120;

    /** What one notch of the wheel leaves in view, zooming in; zooming out is its inverse. */
    public static final double ZOOM_STEP = 0.85;

    /** The least room, in pixels, between two year labels: a "2141" and a gap. */
    public static final double YEAR_LABEL_PX = 46;

    /** The least room between two month labels: a "Mar" and a gap. Below it a zoomed-in axis shows years only. */
    public static final double MONTH_LABEL_PX = 36;

    /** The year steps the axis may label, smallest first: every year, every second, every fifth... */
    static final int[] YEAR_STEPS = {1, 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000};

    /** The month steps a zoomed-in axis may label between its years: monthly, two-monthly, quarterly - no coarser, or ten years across a wide screen would be half-years. */
    static final int[] MONTH_STEPS = {1, 2, 3};

    /** Headroom a value axis leaves above and below what is drawn, as a share of the range: a line at its extreme is not drawn along the frame. */
    public static final double AXIS_PAD = .06;

    /* =====================================================================
       THE WINDOW

       Months on the history's own axis, as doubles: a drag moves the window
       by a fraction of a month as smoothly as the pointer moves. It never
       leaves the data - a pan stops at either end, a zoom stops at
       MIN_SPAN and at the whole history - and it FOLLOWS the newest month
       while its right edge is on it, so a running clock does not leave the
       player looking at last year.
       ===================================================================== */

    private double first, last;
    private double lo, hi;
    private int range = DEFAULT_RANGE;
    private boolean placed;

    /**
     * Whether the player has moved the window by hand - a drag, a wheel, the
     * overview - since the last range button or double-click. Until then the
     * window IS its range (after the docs pass): every month that lands it is
     * the last `range` months again, or everything while the history is
     * younger than that. A city first opened at month 30 used to keep "all"
     * for ever, because all was what its range showed that day.
     */
    private boolean touched;

    /**
     * The months the data covers, first to last - every rebuild hands it the
     * history's axis. A window still on its range is that range again: the
     * last `range` months, or the whole history while it is younger. One the
     * player moved by hand follows the newest month if its right edge was on
     * it, stays where it was put if not, and keeps showing everything if he
     * zoomed or dragged it out to everything.
     */
    public void setData(int firstMonth, int lastMonth) {
        double was = last;
        boolean all = placed && lo <= first + 1e-9 && hi >= last - 1e-9;
        boolean following = placed && hi >= last - 1e-9;
        first = firstMonth;
        last = Math.max(firstMonth, lastMonth);
        // Not yet placed, or placed by a range and not moved since: the range, as the history stands now.
        if (!placed || !touched) {
            placed = true;
            showRange(range);
            return;
        }
        // Moved by hand: everything stays everything only because the player
        // zoomed or dragged it there; on the newest month it follows; else it stays.
        if (all) {
            lo = first;
            hi = last;
        } else if (following) {
            double by = last - was;
            lo += by;
            hi += by;
        }
        clamp();
    }

    /** A range button: the last `months` months, ending at the newest; ALL is everything. Remembered for reset(). */
    public void showRange(int months) {
        range = months;
        touched = false;
        if (months == ALL || months >= last - first + 1) {
            lo = first;
            hi = last;
        } else {
            hi = last;
            lo = last - (months - 1);
        }
        clamp();
    }

    /** The double-click: back to the last range picked, ending at the newest month. */
    public void reset() { showRange(range); }

    /** A drag: the window moved by this many months, stopped at either end of the data. */
    public void pan(double months) {
        if (!Double.isFinite(months)) return;
        touched = true;
        lo += months;
        hi += months;
        clamp();
    }

    /**
     * A wheel notch: the window `factor` times as wide, about the month under
     * the pointer, which stays under it. Below one zooms in.
     */
    public void zoom(double factor, double anchorMonth) {
        if (!(factor > 0) || !Double.isFinite(factor)) return;
        double span = hi - lo;
        double total = last - first;
        if (!(span > 0)) return;
        touched = true;
        double want = Math.max(Math.min(MIN_SPAN, total), Math.min(total, span * factor));
        double a = Math.max(lo, Math.min(hi, Double.isFinite(anchorMonth) ? anchorMonth : (lo + hi) / 2));
        double newLo = a - (a - lo) * (want / span);
        lo = newLo;
        hi = newLo + want;
        clamp();
    }

    /** The overview's handles and its window: an explicit window, held to the data. */
    public void setWindow(double from, double to) {
        if (!Double.isFinite(from) || !Double.isFinite(to)) return;
        touched = true;
        lo = Math.min(from, to);
        hi = Math.max(from, to);
        clamp();
    }

    /** Holds the window inside the data and at least MIN_SPAN wide (or the whole of a shorter history). */
    private void clamp() {
        double total = last - first;
        if (!(total > 0)) { lo = first; hi = last; return; }
        double minSpan = Math.min(MIN_SPAN, total);
        double span = hi - lo;
        if (!(span >= minSpan)) {
            double mid = (lo + hi) / 2;
            lo = mid - minSpan / 2;
            hi = mid + minSpan / 2;
            span = minSpan;
        }
        if (span >= total) { lo = first; hi = last; return; }
        if (lo < first) { hi += first - lo; lo = first; }
        if (hi > last)  { lo -= hi - last;  hi = last; }
    }

    public double lo()     { return lo; }
    public double hi()     { return hi; }
    public double span()   { return hi - lo; }
    public double first()  { return first; }
    public double last()   { return last; }
    public int range()     { return range; }

    /** Whether the window is its range - set by a range button or a double-click and not moved by hand since: the button the chart lights. */
    public boolean onRange() { return placed && !touched; }

    /** Whether the window shows the whole history. */
    public boolean showsAll() { return lo <= first + 1e-9 && hi >= last - 1e-9; }

    /** The first and last whole months inside the window. */
    public int firstMonthShown() { return (int) Math.ceil(lo - 1e-9); }
    public int lastMonthShown()  { return (int) Math.floor(hi + 1e-9); }

    /** How many months the window shows, its two ends included. */
    public int monthsShown() { return Math.max(0, lastMonthShown() - firstMonthShown() + 1); }

    /* =====================================================================
       FROM MONTHS TO PIXELS, AND BACK
       ===================================================================== */

    /** Where a month falls across a plot `width` wide starting at `left`, for a window lo..hi. */
    public static double x(double month, double lo, double hi, double left, double width) {
        return hi > lo ? left + (month - lo) / (hi - lo) * width : left + width / 2;
    }

    /** ...and the month under a pixel. */
    public static double month(double x, double lo, double hi, double left, double width) {
        return width > 0 ? lo + (x - left) / width * (hi - lo) : lo;
    }

    /** The index of the month on the axis nearest this one, or -1 for an empty axis. The axis must be ascending. */
    public static int nearest(List<Integer> months, double month) {
        if (months == null || months.isEmpty()) return -1;
        int a = 0, b = months.size() - 1;
        if (month <= months.get(a)) return a;
        if (month >= months.get(b)) return b;
        while (b - a > 1) {
            int m = (a + b) >>> 1;
            if (months.get(m) <= month) a = m; else b = m;
        }
        return month - months.get(a) <= months.get(b) - month ? a : b;
    }

    /** The lowest and highest of a series inside the window, {low, high}; low above high when nothing is recorded there. */
    public static double[] reach(double[] values, List<Integer> months, double lo, double hi) {
        double low = Double.MAX_VALUE, high = -Double.MAX_VALUE;
        for (int i = 0; i < values.length && i < months.size(); i++) {
            int m = months.get(i);
            if (m < lo - 1e-9 || m > hi + 1e-9 || Double.isNaN(values[i])) continue;
            low = Math.min(low, values[i]);
            high = Math.max(high, values[i]);
        }
        return new double[] {low, high};
    }

    /** How many months one drawn point stands for, so a line has at most one point a pixel. */
    public static int bucket(double months, double pixels) {
        if (!(pixels >= 1)) return 1;
        return Math.max(1, (int) Math.ceil(months / pixels));
    }

    /* =====================================================================
       THE TIME AXIS

       Years, always: the step is the smallest of YEAR_STEPS whose labels
       clear YEAR_LABEL_PX. Zoomed in far enough that a month clears
       MONTH_LABEL_PX, the months between them are labelled too ("Mar").
       And no window is left without a year: one too short to hold a
       January has its first tick labelled with month and year ("Mar 2141").
       ===================================================================== */

    /** One tick on the time axis: the month it marks, its label, and whether it is a year's. */
    public record Tick(double month, String label, boolean year) { }

    /** The ticks for a window lo..hi drawn `pixels` wide, in order. Never without a year in a label. */
    public static List<Tick> timeTicks(double lo, double hi, double pixels) {
        List<Tick> out = new ArrayList<>();
        if (!(hi > lo) || !(pixels > 0)) {
            int m = (int) Math.round(lo);
            out.add(new Tick(m, CityCalendar.formatShort(m), true));
            return out;
        }
        double perPixel = (hi - lo) / pixels;
        int yearStep = YEAR_STEPS[YEAR_STEPS.length - 1];
        for (int s : YEAR_STEPS) {
            if (s * 12 / perPixel >= YEAR_LABEL_PX) { yearStep = s; break; }
        }
        int monthStep = 0;
        if (yearStep == 1) {
            for (int s : MONTH_STEPS) {
                if (s / perPixel >= MONTH_LABEL_PX) { monthStep = s; break; }
            }
        }
        int from = (int) Math.ceil(lo - 1e-9), to = (int) Math.floor(hi + 1e-9);
        if (monthStep > 0) {
            for (int m = from; m <= to; m++) {
                int moy = CityCalendar.monthOfYear(m);
                if (moy == 1) out.add(new Tick(m, String.valueOf(CityCalendar.yearOf(m)), true));
                else if ((moy - 1) % monthStep == 0) out.add(new Tick(m, CityCalendar.shortMonthName(m), false));
            }
        } else {
            int y0 = CityCalendar.yearOf(Math.max(1, from)), y1 = CityCalendar.yearOf(Math.max(1, to)) + 1;
            for (int y = y0 - (Math.floorMod(y0, yearStep)); y <= y1; y += yearStep) {
                int m = 1 + 12 * (y - CityCalendar.EPOCH_YEAR);
                if (m < from || m > to || m < 1) continue;
                out.add(new Tick(m, String.valueOf(y), true));
            }
        }
        // No year in view: the first tick says its year, or a tick is put where one fits.
        boolean anyYear = false;
        for (Tick t : out) if (t.year()) { anyYear = true; break; }
        if (!anyYear) {
            if (out.isEmpty()) {
                int m = Math.max(1, Math.min(to, Math.max(from, (int) Math.round((lo + hi) / 2))));
                out.add(new Tick(m, CityCalendar.formatShort(m), true));
            } else {
                Tick t = out.get(0);
                out.set(0, new Tick(t.month(), CityCalendar.formatShort((int) t.month()), true));
            }
        }
        return out;
    }

    /* =====================================================================
       A VALUE AXIS

       One, two or five times a power of ten - the only steps a reader can
       add up - with the bounds snapped outward to the step and AXIS_PAD of
       air. FROM ZERO for an axis whose zero means something - a rate, a
       share - when nothing on it is negative: a 3% rate is drawn near the
       top of a 0-3.5% axis, not across the middle of a 2-4% one. That is
       also the two-axis fix: in a new city the rate and the price level were
       both flat, both axes centred their line, and the two lay on each
       other; each axis now takes its own scale, and a rate's starts at zero.
       ===================================================================== */

    /** A value axis: its bottom, its top and the step between its gridlines, in the axis's units. */
    public record Scale(double low, double high, double step) {
        /** The gridlines, bottom to top. */
        public List<Double> ticks() {
            List<Double> out = new ArrayList<>();
            if (!(step > 0) || !(high > low)) return out;
            int n = (int) Math.round((high - low) / step);
            for (int k = 0; k <= n && k <= 1000; k++) out.add(low + k * step);
            return out;
        }
        /** Where a value falls on a plot `height` tall whose bottom is at `bottom` (pixels grow down). */
        public double y(double v, double bottom, double height) {
            return high > low ? bottom - (v - low) / (high - low) * height : bottom - height / 2;
        }
    }

    /** One, two or five times a power of ten, at least `raw`. */
    public static double niceStep(double raw) {
        if (!(raw > 0) || !Double.isFinite(raw)) return 1;
        double power = Math.pow(10, Math.floor(Math.log10(raw)));
        double n = raw / power;
        return (n <= 1 + 1e-9 ? 1 : n <= 2 + 1e-9 ? 2 : n <= 5 + 1e-9 ? 5 : 10) * power;
    }

    /**
     * A nice scale over low..high, about `ticks` gridlines, from zero when
     * asked and nothing is negative. Low above high - nothing drawn - is
     * 0..1, unlabelled by the caller.
     */
    public static Scale niceScale(double low, double high, int ticks, boolean fromZero) {
        if (!(high >= low) || !Double.isFinite(low) || !Double.isFinite(high)) return new Scale(0, 1, .25);
        boolean zeroFloor = fromZero && low >= 0;
        if (zeroFloor) low = 0;
        double pad = high > low ? (high - low) * AXIS_PAD : (low != 0 ? Math.abs(low) * .1 : 1);
        double a = zeroFloor ? 0 : low - pad, b = high + pad;
        double step = niceStep((b - a) / Math.max(1, ticks));
        a = Math.floor(a / step + 1e-9) * step;
        b = Math.ceil(b / step - 1e-9) * step;
        if (!(b > a)) b = a + step;
        return new Scale(a, b, step);
    }

    /** A log axis over these LOGARITHMS: whole powers of ten, a gridline each. */
    public static Scale logScale(double lowLog, double highLog) {
        if (!(highLog >= lowLog)) return new Scale(0, 1, 1);
        double a = Math.floor(lowLog), b = Math.ceil(highLog);
        if (b <= a) b = a + 1;
        return new Scale(a, b, 1);
    }

    /* =====================================================================
       WHAT IS LAID OVER THE LINES
       ===================================================================== */

    /** The recession bands, exactly YearBook.recessions(), each with its depth and the episode whose name it carries. */
    public static List<YearBook.Band> bands(HistorySave h) { return YearBook.recessionBands(h); }

    /** The spans on the episode lane, exactly YearBook.episodes(), oldest first. */
    public static List<YearBook.Episode> episodes(HistorySave h) { return YearBook.episodes(h); }

    /**
     * Which row of the episode lane each episode sits on: the first row
     * whose last span ended before this one began, so two that overlap - a
     * treasury crisis inside a recession - never share a row.
     */
    public static int[] lanes(List<YearBook.Episode> episodes) {
        int[] row = new int[episodes.size()];
        List<Integer> ends = new ArrayList<>();
        for (int i = 0; i < episodes.size(); i++) {
            YearBook.Episode e = episodes.get(i);
            int r = 0;
            while (r < ends.size() && ends.get(r) >= e.fromMonth()) r++;
            if (r == ends.size()) ends.add(e.toMonth()); else ends.set(r, e.toMonth());
            row[i] = r;
        }
        return row;
    }

    /** How many rows lanes() used. */
    public static int laneCount(int[] rows) {
        int n = 0;
        for (int r : rows) n = Math.max(n, r + 1);
        return n;
    }

    /** One flag on the decision lane: a month and everything decided in it, in the order it was decided. */
    public record Flag(int month, List<DecisionLog.Entry> entries) {
        public int count() { return entries.size(); }
    }

    /** The log's entries as flags: one a month, many decisions in one month one flag with a count; oldest first. */
    public static List<Flag> flags(DecisionLog log) {
        List<Flag> out = new ArrayList<>();
        if (log == null) return out;
        Map<Integer, List<DecisionLog.Entry>> byMonth = new TreeMap<>();
        for (DecisionLog.Entry e : log.entries()) byMonth.computeIfAbsent(e.month(), m -> new ArrayList<>()).add(e);
        for (Map.Entry<Integer, List<DecisionLog.Entry>> m : byMonth.entrySet()) {
            out.add(new Flag(m.getKey(), Collections.unmodifiableList(m.getValue())));
        }
        return out;
    }

    /**
     * ...of one kind only (0.7.32): the Finances tab's chart of what the city
     * owes and its rate carries the BORROWING decisions alone - an issue, a
     * buyback, the rollover's setting, a default abroad. A kind nobody records
     * gives none.
     */
    public static List<Flag> flags(DecisionLog log, String kind) {
        List<Flag> out = new ArrayList<>();
        if (log == null || kind == null) return out;
        Map<Integer, List<DecisionLog.Entry>> byMonth = new TreeMap<>();
        for (DecisionLog.Entry e : log.entries()) {
            if (kind.equals(e.kind())) byMonth.computeIfAbsent(e.month(), m -> new ArrayList<>()).add(e);
        }
        for (Map.Entry<Integer, List<DecisionLog.Entry>> m : byMonth.entrySet()) {
            out.add(new Flag(m.getKey(), Collections.unmodifiableList(m.getValue())));
        }
        return out;
    }

    /**
     * ...of several kinds, one flag a month (0.7.33): the Bank tab's rates
     * carry the CENTRAL_BANK decisions that move the policy rate under them
     * and the BANK ones - a rescue, the preferred offer - in one lane, so a
     * month with both is one flag with both in it, not two drawn on top of
     * each other.
     */
    public static List<Flag> flagsOf(DecisionLog log, String... kinds) {
        List<Flag> out = new ArrayList<>();
        if (log == null || kinds == null || kinds.length == 0) return out;
        java.util.Set<String> wanted = new java.util.HashSet<>(java.util.Arrays.asList(kinds));
        Map<Integer, List<DecisionLog.Entry>> byMonth = new TreeMap<>();
        for (DecisionLog.Entry e : log.entries()) {
            if (wanted.contains(e.kind())) byMonth.computeIfAbsent(e.month(), m -> new ArrayList<>()).add(e);
        }
        for (Map.Entry<Integer, List<DecisionLog.Entry>> m : byMonth.entrySet()) {
            out.add(new Flag(m.getKey(), Collections.unmodifiableList(m.getValue())));
        }
        return out;
    }

    /**
     * The flags as the lane can draw them (0.7.37): a flag from before the
     * axis's first month - the founding month's, which the history does not
     * record, so no window reaches it - is moved onto that first month and
     * merged with any flag already there, every entry keeping its own month
     * (the card says it). Until then the dials a player set before pressing
     * play were in the log and on no lane (City History's spec, B4).
     */
    public static List<Flag> onAxis(List<Flag> flags, int firstMonth) {
        List<Flag> out = new ArrayList<>();
        if (flags == null) return out;
        List<DecisionLog.Entry> first = new ArrayList<>();
        for (Flag f : flags) if (f.month() < firstMonth) first.addAll(f.entries());
        for (Flag f : flags) if (f.month() == firstMonth) first.addAll(f.entries());
        if (!first.isEmpty()) out.add(new Flag(firstMonth, Collections.unmodifiableList(first)));
        for (Flag f : flags) if (f.month() > firstMonth) out.add(f);
        return out;
    }

    /** Flags drawn as one: at the first one's month, so many months packed under one pixel read as one count. */
    public record Cluster(int month, List<Flag> flags) {
        public int count() {
            int n = 0;
            for (Flag f : flags) n += f.count();
            return n;
        }
    }

    /**
     * The flags inside the window, each month's one flag, and a flag that
     * would sit within `gapPx` of the FIRST flag of the group before it drawn
     * in that group, as one: the decision lane of a long city zoomed out is a
     * row of counts, not a smear.
     */
    public static List<Cluster> clusters(List<Flag> flags, double lo, double hi, double pixels, double gapPx) {
        List<Cluster> out = new ArrayList<>();
        List<Flag> run = null;
        double runX = 0;
        for (Flag f : flags) {
            if (f.month() < lo - 1e-9 || f.month() > hi + 1e-9) continue;
            double x = x(f.month(), lo, hi, 0, pixels);
            if (run != null && x - runX < gapPx) { run.add(f); continue; }
            if (run != null) out.add(new Cluster(run.get(0).month(), run));
            run = new ArrayList<>();
            run.add(f);
            runX = x;
        }
        if (run != null) out.add(new Cluster(run.get(0).month(), run));
        return out;
    }

    /** A fresh window: no data yet; the first setData() opens it on DEFAULT_RANGE. */
    public ChartModel() { }
}
