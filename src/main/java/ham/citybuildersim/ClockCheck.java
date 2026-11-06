package ham.citybuildersim;

import ham.citybuildersim.ui.UserInterface;

import java.util.function.IntToDoubleFunction;

/**
 * The clock's pace (UP2): a redraw is paced by what it cost, a frame spends a budget on months and
 * drops what it could not run, the header's speed says when the city falls short of it, and the
 * header reads only the months it draws.
 *
 * WHY THIS EXISTS. The timer ran every month that was due in a frame however long they took, drew
 * the open screen at a fixed 120 ms whatever the draw cost, and the header rebuilt five whole
 * histories to draw the last ten years of each. None of it showed while a month and a page were
 * cheap; at month 4,000 it was a third of the FX thread and a slow stretch banked time to burst
 * afterwards. This runs ClockPace on a clock it moves by hand, frame by frame as the timer runs
 * them, with months and redraws of chosen cost, and the windowed reads against the whole-history
 * reads they stand in for. No JavaFX toolkit: it runs in the suite in the cloud as on the PC.
 *
 * What it has to prove:
 *   1. a redraw costing 10, 60 and 200 ms is followed by the next no sooner than
 *      max(REDRAW_EVERY, cost x REDRAW_SPACING) and is due once that has passed; redraws take at
 *      most a third of the time; a redraw never costs the screen more than REDRAW_COST_MAX;
 *   2. a frame runs as many months as are due while they have taken under MONTH_FRAME_MS, and
 *      always the first; across 600 frames at 50x with months of mixed cost none is skipped or run
 *      twice, and the time worth is the months run, owed and dropped;
 *   3. a 2 s stall at 50x is worth FRAME_CLAMP, leaves at most one month owed, and is not paid
 *      back in a burst afterwards, with months of 5 and of 50 ms;
 *   4. the speed's reading is the rung while the city keeps up and until a window has run, the
 *      speed reached ALONE (UP7: "31×", never "50× · 31×") once it falls more than SHORT_BY
 *      under, the rung again after restart() or a new rung, and a slow rung (0.1x) is not read
 *      as a shortfall; reached() is null exactly when the reading is the rung;
 *   5. the header's five series, read the windowed way, are the last SPARK_MONTHS of the
 *      whole-history read, value for value, on a young city, an older one and a history past
 *      MONTHLY_KEPT (folded years), and so is the GDP tile's change line;
 *   6. a stalled frame is not a shortfall (UP7): after one frame of half a second, and one of
 *      under FRAME_CLAMP, the reading is the rung at 1x and at 50x with cheap months, and a city
 *      really short stays short across the same stall; a speed that prints as the rung's own
 *      words ("2×" reached at 1.75 of a rung of 2) is not a shortfall, one that rounds apart is;
 *      and every text reached anywhere above is one of ClockPace.reachedTexts().
 */
public class ClockCheck {

    static int fails = 0;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** Real seconds a month takes at 1x, as the window's clock has it. */
    static final double SPM = UserInterface.SECONDS_PER_MONTH;

    /** The top rung. */
    static final double TOP = 50;
    static final String TOP_LABEL = "50×";

    static final double FRAME = 1.0 / 60;

    static boolean near(double a, double b, double eps) {
        return Math.abs(a - b) <= eps;
    }

    /**
     * The timer's frames on a clock moved by hand: each step is one frame of FRAME seconds plus
     * whatever the work of the frame before it took (the timer's dt is the gap between two
     * pulses, and the work is in it), months and redraws costing what the fixtures say.
     */
    static final class Sim {
        long nanos;
        final ClockPace pace = new ClockPace(() -> nanos);
        double speed = TOP, spm = SPM;
        /** What month number m costs, in milliseconds. */
        IntToDoubleFunction monthMs = m -> 0;
        long last;
        int called, returned, badFrames;
        double elapsed;

        void advance(double ms) {
            nanos += Math.round(ms * 1e6);
        }

        /** One frame; dt is the gap since the last. Returns the months run. */
        int step() {
            return frame(FRAME);
        }

        int frame(double interval) {
            nanos += Math.round(interval * 1e9);
            double dt = (nanos - last) / 1e9;
            last = nanos;
            elapsed += dt;
            double before = pace.progress();
            int due = (int) Math.floor(before + Math.min(dt, ClockPace.FRAME_CLAMP) * speed / spm);
            int ran = pace.frame(dt, speed, spm, () -> {
                advance(monthMs.applyAsDouble(called));
                called++;
                return false;
            });
            returned += ran;
            // Never a month that was not due, and the first whenever one was.
            if (ran > due || (due >= 1 && ran < 1)) badFrames++;
            return ran;
        }

        /** A redraw that costs {@code ms}, begun and ended as the timer does. */
        void redraw(double ms) {
            pace.redrawStarted();
            advance(ms);
            pace.redrawEnded();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== ClockCheck: the clock's pace (UP2) ===");
        redraws();
        months();
        stall();
        reading();
        windows();
        stallAndPair();
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============ 1. a redraw is paced by what it cost ============ */

    static void redraws() {
        System.out.println("\n--- 1: the next redraw waits for the cost of this one ---");
        ClockPace fresh = new ClockPace();
        check("before any redraw the gap is REDRAW_EVERY", fresh.redrawGap() == ClockPace.REDRAW_EVERY);
        fresh.redrawEnded();
        check("an end with no redraw begun changes nothing", fresh.redrawGap() == ClockPace.REDRAW_EVERY && !fresh.redrawing());

        for (double ms : new double[] {10, 60, 200}) {
            Sim s = new Sim();
            s.speed = 1;
            for (int i = 0; i < 5; i++) s.step();
            long began = s.nanos;
            s.redraw(ms);
            double gap = Math.max(ClockPace.REDRAW_EVERY, ms / 1000 * ClockPace.REDRAW_SPACING);
            check(String.format("a %.0f ms redraw sets the gap to max(REDRAW_EVERY, cost x REDRAW_SPACING) = %.0f ms",
                    ms, gap * 1000), near(s.pace.redrawGap(), gap, 1e-9) && near(s.pace.redrawCost(), ms / 1000, 1e-9));
            boolean early = false, late = false;
            double dueAt = -1;
            for (int i = 0; i < 100; i++) {
                s.step();
                double since = (s.nanos - began) / 1e9;
                boolean due = s.pace.redrawDue();
                if (due && since < gap - 1e-6) early = true;
                if (!due && since >= gap + 1e-6) late = true;
                if (due && dueAt < 0) dueAt = since;
            }
            check(String.format("...never due before it (first due at %.0f ms)", dueAt * 1000), !early && dueAt >= gap - 1e-6);
            check("...and due on the frame once it has passed", !late && dueAt < gap + FRAME + 1e-6);
        }
        Sim floor = new Sim();
        floor.redraw(10);
        Sim spaced = new Sim();
        spaced.redraw(60);
        check("a cheap redraw keeps the floor, a dear one is spaced by its cost",
                floor.pace.redrawGap() == ClockPace.REDRAW_EVERY
                        && spaced.pace.redrawGap() > ClockPace.REDRAW_EVERY
                        && spaced.pace.redrawGap() == spaced.pace.redrawCost() * ClockPace.REDRAW_SPACING);

        // A run of frames that redraws whenever it may: the redraws take at most 1 / REDRAW_SPACING of the time.
        for (double ms : new double[] {10, 60, 200}) {
            Sim s = new Sim();
            s.speed = 1;
            double spent = 0;
            int drawn = 0;
            long t0 = s.nanos;
            for (int i = 0; i < 1200; i++) {
                s.step();
                if (s.pace.redrawDue()) {
                    s.redraw(ms);
                    spent += ms / 1000;
                    drawn++;
                }
            }
            double share = spent / ((s.nanos - t0) / 1e9);
            check(String.format("redraws of %.0f ms, drawn whenever due, take %.0f%% of the time (%d of them)",
                    ms, share * 100, drawn), drawn > 0 && share <= 1 / ClockPace.REDRAW_SPACING + 0.01);
        }

        Sim stuck = new Sim();
        stuck.redraw(5000);
        check("a redraw whose pulse came late is counted at REDRAW_COST_MAX, not at minutes",
                near(stuck.pace.redrawCost(), ClockPace.REDRAW_COST_MAX, 1e-9)
                        && near(stuck.pace.redrawGap(), ClockPace.REDRAW_COST_MAX * ClockPace.REDRAW_SPACING, 1e-9));
    }

    /* ============ 2. a frame has a budget for months ============ */

    static void months() {
        System.out.println("\n--- 2: the months a frame runs ---");
        // Five ms months in the biggest frame there is: every whole month of its 2.5.
        Sim cheap = new Sim();
        cheap.monthMs = m -> 5;
        int ran = cheap.frame(ClockPace.FRAME_CLAMP);
        int due = (int) Math.floor(ClockPace.FRAME_CLAMP * TOP / SPM);
        check("5 ms months: a clamped frame at 50x runs every whole month due (" + due + ")", ran == due && cheap.called == due);
        check("...and keeps the rest of the month as the progress",
                near(cheap.pace.progress(), ClockPace.FRAME_CLAMP * TOP / SPM - due, 1e-9));

        // Fifty ms months: one a frame.
        Sim dear = new Sim();
        dear.monthMs = m -> 50;
        int most = 0, total = 0;
        for (int i = 0; i < 40; i++) {
            int r = dear.frame(i == 0 ? ClockPace.FRAME_CLAMP : FRAME);
            most = Math.max(most, r);
            total += r;
        }
        check("50 ms months (over MONTH_FRAME_MS): one a frame whenever one is due, never more",
                most == 1 && total > 1 && dear.badFrames == 0);

        // The budget from both sides: ten months due, a month costing a share of the budget.
        double speed = 10 * SPM / ClockPace.FRAME_CLAMP;
        for (double share : new double[] {0, .4, .9, 1.5}) {
            Sim s = new Sim();
            s.speed = speed;
            final double cost = share * ClockPace.MONTH_FRAME_MS;
            s.monthMs = m -> cost;
            int r = s.frame(ClockPace.FRAME_CLAMP);
            int expect = cost == 0 ? 10 : (int) Math.min(10, Math.max(1, Math.ceil(ClockPace.MONTH_FRAME_MS / cost)));
            check(String.format("10 months due, each %.0f%% of MONTH_FRAME_MS: %d run", share * 100, expect), r == expect);
        }

        // 600 frames at 50x, the cost of the months mixed.
        Sim run = new Sim();
        double[] costs = {1, 5, 30, 50, 120, 2, 8, 70};
        // ...with a slow stretch of 120 ms months among them, the stretch the old loop would have paid back in a burst.
        run.monthMs = m -> (m / 12) % 4 == 1 ? 120 : costs[(m * 7 + m / 3) % costs.length];
        for (int i = 0; i < 600; i++) run.step();
        double worth = run.elapsed * TOP / SPM;
        double accounted = run.called + run.pace.progress() + run.pace.droppedMonths();
        check("600 frames at 50x: the months the callback ran are the sum of the frames' returns",
                run.called == run.returned && run.called > 0);
        check("...no frame ran a month that was not due, and each ran the first when one was", run.badFrames == 0);
        check(String.format("...the time is worth %.1f months: run %d + owed %.2f + dropped %.1f",
                worth, run.called, run.pace.progress(), run.pace.droppedMonths()), near(worth, accounted, 1e-6 * worth));
        check("...and the slow months dropped time (the cut ran)", run.pace.droppedMonths() > 0);
        check("...leaving at most one month owed", run.pace.progress() <= 1);

        // The clock stopping itself ends the loop and zeroes the progress.
        Sim stop = new Sim();
        int[] seen = {0};
        int r = stop.pace.frame(ClockPace.FRAME_CLAMP, TOP, SPM, () -> ++seen[0] == 1);
        check("the clock stopping itself ends the frame after that month and zeroes the progress",
                r == 1 && seen[0] == 1 && stop.pace.progress() == 0);
    }

    /* ============ 3. a stall is not paid back ============ */

    static void stall() {
        System.out.println("\n--- 3: a 2 s stall at 50x ---");
        for (double ms : new double[] {5, 50}) {
            Sim s = new Sim();
            s.monthMs = m -> ms;
            for (int i = 0; i < 60; i++) s.step();
            double before = s.pace.progress();
            double droppedBefore = s.pace.droppedMonths();
            int ran = s.frame(2.0);
            double clamp = ClockPace.FRAME_CLAMP * TOP / SPM;
            check(String.format("%.0f ms months: the stall runs at most FRAME_CLAMP's worth (%d months)", ms, ran),
                    ran <= (int) Math.floor(before + clamp));
            check("...and owes under two months after it", s.pace.progress() < 2);
            check("...the seconds past the clamp are dropped, not banked",
                    s.pace.droppedMonths() - droppedBefore >= (2.0 - ClockPace.FRAME_CLAMP) * TOP / SPM - 1e-9);
            int next = 0;
            for (int i = 0; i < 30 && next == 0; i++) next += s.step();
            check("...and under one month is owed once the next frame has run its month", next >= 1 && s.pace.progress() < 1);
            double start = s.elapsed;
            int landed = 0;
            while (s.elapsed - start < 1) landed += s.step();
            double seconds = s.elapsed - start;
            int most = (int) Math.floor(TOP / SPM * seconds) + 1;
            check(String.format("...over the next %.2f s %d months land, at most the rung's rate + 1 = %d", seconds, landed, most),
                    landed <= most);
        }
    }

    /* ============ 4. the speed says when it falls short ============ */

    /** Every text the pace said it reached in this run: section 6 holds them against the list reachedTexts() gives. */
    static final java.util.Set<String> SEEN = new java.util.TreeSet<>();

    /** The speed reached (ClockPace.reached), noted in SEEN when there is one. */
    static String reachedOf(ClockPace pace, String label, double rung) {
        String reached = pace.reached(label, rung);
        if (reached != null) SEEN.add(reached);
        return reached;
    }

    /** Whether the pace reads the rung's own words: reading() is the rung and reached() says nothing. */
    static boolean readsRung(ClockPace pace, String label, double rung) {
        return pace.reading(label, rung).equals(label) && reachedOf(pace, label, rung) == null;
    }

    /** Whether the pace reads a speed reached, alone: reading() is reached(), which is not the rung's words. */
    static boolean readsReached(ClockPace pace, String label, double rung) {
        String reached = reachedOf(pace, label, rung);
        return reached != null && pace.reading(label, rung).equals(reached) && !reached.equals(label) && !reached.contains("·");
    }

    static void reading() {
        System.out.println("\n--- 4: the speed's reading ---");
        Sim keeps = new Sim();
        keeps.monthMs = m -> 5;
        boolean rungAlone = true;
        for (int i = 0; keeps.elapsed < 3; i++) {
            keeps.step();
            if (!readsRung(keeps.pace, TOP_LABEL, TOP)) rungAlone = false;
        }
        check("5 ms months at 50x for 3 s: the reading is the rung alone throughout, and no speed is reached", rungAlone);

        Sim falls = new Sim();
        falls.monthMs = m -> 120;
        boolean aloneToWindow = true;
        while (falls.elapsed < 3) {
            falls.step();
            if (falls.elapsed < ClockPace.SHORT_WINDOW - 0.2 && !readsRung(falls.pace, TOP_LABEL, TOP)) aloneToWindow = false;
        }
        String reached = reachedOf(falls.pace, TOP_LABEL, TOP);
        // One month a frame, and a frame is FRAME plus the month: that is the speed reached.
        double expect = SPM / (FRAME + .120);
        int shown = reached != null && reached.endsWith("×") ? Integer.parseInt(reached.substring(0, reached.length() - 1)) : -1;
        check("120 ms months at 50x: until a full window has run the reading is the rung", aloneToWindow);
        check(String.format("...then the speed reached alone, the rung gone: \"%s\" (%.1f expected)", falls.pace.reading(TOP_LABEL, TOP), expect),
                shown >= 0 && readsReached(falls.pace, TOP_LABEL, TOP) && near(shown, expect, 1));
        check("...the figure reached is under the rung by more than SHORT_BY", shown >= 0 && shown < TOP * (1 - ClockPace.SHORT_BY));

        falls.pace.restart();
        check("the rung alone again after restart()", readsRung(falls.pace, TOP_LABEL, TOP));
        while (falls.elapsed < 3 + ClockPace.SHORT_WINDOW - 0.3) falls.step();
        check("...and for the first part of the new window", readsRung(falls.pace, TOP_LABEL, TOP));
        while (falls.elapsed < 3 + ClockPace.SHORT_WINDOW + 0.5) falls.step();
        check("...the speed reached again once a full window has run", readsReached(falls.pace, TOP_LABEL, TOP));
        falls.speed = 20;
        falls.step();
        check("a new rung starts the window again (the timer is told the speed each frame)", readsRung(falls.pace, "20×", 20));

        // The tenth, from both sides: a quicker city (months of 0.5 s at 1x, so a window has hundreds)
        // run a little under and well under the rung it is read against.
        for (double under : new double[] {ClockPace.SHORT_BY / 2, ClockPace.SHORT_BY * 2}) {
            Sim s = new Sim();
            s.spm = .5;
            s.speed = TOP * (1 - under);
            while (s.elapsed < ClockPace.SHORT_WINDOW + 1) s.step();
            String text = s.pace.reading(TOP_LABEL, TOP);
            boolean inside = under < ClockPace.SHORT_BY;
            check(String.format("%.0f%% under the rung (the tenth is %.0f%%): %s", under * 100, ClockPace.SHORT_BY * 100,
                    inside ? "the rung alone" : "the speed reached alone (\"" + text + "\")"),
                    inside ? readsRung(s.pace, TOP_LABEL, TOP)
                            : readsReached(s.pace, TOP_LABEL, TOP) && text.equals(Math.round(TOP * (1 - under)) + "×"));
        }

        // Under 1x the figure has one decimal.
        Sim crawl = new Sim();
        crawl.spm = .5;
        crawl.speed = .4;
        while (crawl.elapsed < ClockPace.SHORT_WINDOW + 1) crawl.step();
        check("a speed reached under 1 is written with one decimal (0.4 against a rung of 1)",
                readsReached(crawl.pace, "1×", 1) && crawl.pace.reading("1×", 1).equals("0.4×"));

        // 0.1x lands a month in fifty seconds: two seconds hold no month, and that is not a shortfall.
        Sim slow = new Sim();
        slow.speed = .1;
        while (slow.elapsed < ClockPace.SHORT_WINDOW + 1) slow.step();
        check("a rung of 0.1x with no month landed in the window reads the rung alone", readsRung(slow.pace, "0.1×", .1));
    }


    /* ============ 5. the header reads the window it draws ============ */

    static boolean same(double a, double b) {
        return (Double.isNaN(a) && Double.isNaN(b)) || a == b;
    }

    /** Whether the tail is the last tail.length entries of the whole, value for value, NaN where NaN. */
    static boolean isTailOf(double[] tail, double[] whole) {
        if (tail.length > whole.length) return false;
        for (int i = 0; i < tail.length; i++) {
            if (!same(tail[i], whole[whole.length - tail.length + i])) return false;
        }
        return true;
    }

    /** The header's five keys, and the whole-history read each one stands in for. */
    static final String[] KEYS = {"population", "gdp", "priceIndex", "unemployment", "policyRate"};

    static double[] whole(HistorySave h, String key) {
        return "unemployment".equals(key) ? YearBook.unemployment(h) : h.aligned(key);
    }

    /** The header's reads on one history: the five tiles, the change line, and every series' tail. */
    static void headerReads(String what, HistorySave h, int... tails) {
        int spark = UserInterface.SPARK_MONTHS;
        int expectLength = Math.min(spark, h.months());
        boolean five = true, lengths = true;
        int nans = 0;
        for (String key : KEYS) {
            double[] tail = UserInterface.sparkSeries(h, key);
            five &= isTailOf(tail, whole(h, key)) && tail.length == expectLength;
            lengths &= tail.length == expectLength;
            for (double v : tail) if (Double.isNaN(v)) nans++;
        }
        check(what + ": the five tiles' series are the last SPARK_MONTHS of the whole read (" + expectLength + " each, "
                + nans + " NaN)", five && lengths);

        double[] growth = YearBook.realGrowth(h);
        double[] last = YearBook.realGrowthTail(h, 1);
        check(what + ": the GDP change line's last value is realGrowth's last",
                last.length == 1 && same(last[0], growth[growth.length - 1]));

        boolean tailsOk = true, growthOk = true, jobsOk = true;
        String firstWrong = null;
        int series = 0;
        for (int n : tails) {
            for (String name : h.seriesByName().keySet()) {
                double[] a = h.alignedTail(name, n);
                boolean ok = a.length == Math.min(n, h.months()) && isTailOf(a, h.aligned(name));
                series++;
                if (!ok && firstWrong == null) firstWrong = name + " n=" + n;
                tailsOk &= ok;
            }
            double[] g = YearBook.realGrowthTail(h, n);
            growthOk &= g.length == Math.min(n, h.months()) && isTailOf(g, growth);
            double[] u = YearBook.unemploymentTail(h, n);
            jobsOk &= u.length == Math.min(n, h.months()) && isTailOf(u, YearBook.unemployment(h));
        }
        check(what + ": every series' alignedTail is aligned's tail, padding and fold included (" + series + " reads"
                + (firstWrong == null ? "" : ", first wrong: " + firstWrong) + ")", tailsOk);
        check(what + ": realGrowthTail and unemploymentTail are their whole reads' tails at each length", growthOk && jobsOk);
        check(what + ": a tail of nothing is nothing", h.alignedTail("gdp", 0).length == 0
                && YearBook.realGrowthTail(h, 0).length == 0 && YearBook.unemploymentTail(h, 0).length == 0);
    }

    static void windows() {
        System.out.println("\n--- 5: the header reads the window it draws ---");
        int spark = UserInterface.SPARK_MONTHS;
        GameFiles files = GameFiles.scratch("clockcheck");
        Game g = LongPlaytest.probeCity(files, null);
        HistorySave young = g.getHistorySave();
        System.out.printf("  a young city: %d months recorded (SPARK_MONTHS %d)%n", young.months(), spark);
        check("the young city has fewer than SPARK_MONTHS months, so the tile reads everything recorded",
                young.months() > 12 && young.months() < spark);
        headerReads("young city", young, 1, 7, young.months(), spark, 10_000);

        LongPlaytest.probePlay(g, spark + 24 + 12);
        HistorySave older = g.getHistorySave();
        System.out.printf("  an older city: %d months recorded%n", older.months());
        check("the older city has more than SPARK_MONTHS + 24 months", older.months() > spark + 24);
        headerReads("older city", older, 1, 13, spark, older.months() - 1, older.months(), 10_000);

        // A history past MONTHLY_KEPT, grown by hand: its oldest years are one entry each, and a tail long
        // enough to reach them reads a folded year's flow as its monthly average, as aligned() does.
        int total = HistorySave.MONTHLY_KEPT + 3 * 12 + 5;
        HistorySave folded = new HistorySave();
        for (int m = 1; m <= total; m++) {
            final int month = m;
            folded.appendMonth(m, name -> foldedValue(name, month));
        }
        int monthly = folded.months() - folded.yearlyPoints();
        System.out.printf("  a folded history: %d months lived, %d folded years, %d monthly entries%n",
                folded.monthsLived(), folded.yearlyPoints(), monthly);
        check("the grown history has folded years", folded.yearlyPoints() == 3);
        headerReads("folded history", folded, 1, 13, spark, monthly - 1, monthly, monthly + 1, monthly + 5, folded.months());
    }

    /** A value for every series of a history grown by hand: positive, different by series and month, with a hole in the price index. */
    static double foldedValue(String name, int month) {
        int code = Math.floorMod(name.hashCode(), 89);
        if (name.equals("priceIndex")) {
            // A gap of NaN poisons the rolling year over it and nothing else.
            if (month >= 6000 && month <= 6003) return Double.NaN;
            return 1 + month / 400.0;
        }
        if (name.equals("students") || name.equals("prisoners")) return 10 + code % 7;
        return 100 + Math.floorMod(month * 13 + code, 71) + code;
    }

    /* ============ 6. a stalled frame is not a shortfall, and a speed that prints as the rung is not one ============ */

    static void stallAndPair() {
        System.out.println("\n--- 6: the stall and the equal pair ---");
        // (a) One frame of a stall, then a window and more: a frame is worth FRAME_CLAMP at most, so the seconds the stall took
        // past that are no shortfall - from both sides of the clamp (a stall under it is worth all its seconds).
        for (double rung : new double[] {1, TOP}) {
            String label = rung == TOP ? TOP_LABEL : "1×";
            for (double stall : new double[] {ClockPace.FRAME_CLAMP * .8, ClockPace.FRAME_CLAMP * 2}) {
                Sim s = new Sim();
                s.speed = rung;
                s.monthMs = m -> 5;
                while (s.elapsed < ClockPace.SHORT_WINDOW + .5) s.step();
                boolean before = readsRung(s.pace, label, rung);
                s.frame(stall);
                boolean alone = readsRung(s.pace, label, rung);
                int frames = 0;
                for (double from = s.elapsed; s.elapsed - from < ClockPace.SHORT_WINDOW + .5; frames++) {
                    s.step();
                    alone &= readsRung(s.pace, label, rung);
                }
                check(String.format("%s, a %.2f s stall (%s FRAME_CLAMP): the reading is the rung at the stall and at each of %d frames after it",
                        label, stall, stall > ClockPace.FRAME_CLAMP ? "over" : "under", frames), before && alone);
            }
        }

        // ...and caused from the other side: a city really short (months of 120 ms at 50x) stays short across the same stall.
        Sim real = new Sim();
        real.monthMs = m -> 120;
        while (real.elapsed < ClockPace.SHORT_WINDOW + .5) real.step();
        boolean short0 = readsReached(real.pace, TOP_LABEL, TOP);
        real.frame(ClockPace.FRAME_CLAMP * 2);
        boolean stays = readsReached(real.pace, TOP_LABEL, TOP);
        int frames = 0;
        for (double from = real.elapsed; real.elapsed - from < ClockPace.SHORT_WINDOW + .5; frames++) {
            real.step();
            stays &= readsReached(real.pace, TOP_LABEL, TOP);
        }
        check(String.format("120 ms months at 50x, a %.2f s stall: short before it, and short at the stall and at each of %d frames after (\"%s\")",
                ClockPace.FRAME_CLAMP * 2, frames, real.pace.reading(TOP_LABEL, TOP)), short0 && stays);

        // (b) The equal pair: past the tenth, the speed reached can still print as the rung's own words.
        Sim twin = new Sim();
        twin.spm = .5;
        twin.speed = 2 * (1 - 1.25 * ClockPace.SHORT_BY);
        while (twin.elapsed < ClockPace.SHORT_WINDOW + 1) twin.step();
        check(String.format("a rung of 2 run at %.2f (past the tenth, which rounds to 2): the reading is \"2×\" and nothing is reached", twin.speed),
                readsRung(twin.pace, "2×", 2));
        check("...the same city read against other words is short, and says \"2×\" (it is the words that are equal)",
                "2×".equals(twin.pace.reached("two", 2)));
        Sim near = new Sim();
        near.spm = .5;
        near.speed = 2 * (1 - 3 * ClockPace.SHORT_BY);
        while (near.elapsed < ClockPace.SHORT_WINDOW + 1) near.step();
        check(String.format("a rung of 2 run at %.2f (rounds to 1): the reading is \"1×\", the speed reached", near.speed),
                readsReached(near.pace, "2×", 2) && near.pace.reading("2×", 2).equals("1×"));
        Sim apart = new Sim();
        apart.spm = .5;
        apart.speed = TOP * (1 - 1.1 * ClockPace.SHORT_BY);
        while (apart.elapsed < ClockPace.SHORT_WINDOW + 1) apart.step();
        check(String.format("a rung of 50 run at %.2f (rounds apart): the reading is \"%d×\", the speed reached", apart.speed, Math.round(apart.speed)),
                readsReached(apart.pace, TOP_LABEL, TOP) && apart.pace.reading(TOP_LABEL, TOP).equals(Math.round(apart.speed) + "×"));
        // A speed just under 1 that rounds to it at one decimal: written as the ladder writes 1x, not "1.0×" (and, from the other side, one that does not round up keeps its decimal).
        Sim justUnder = new Sim();
        justUnder.spm = .5;
        justUnder.speed = .97;
        while (justUnder.elapsed < ClockPace.SHORT_WINDOW + 1) justUnder.step();
        Sim under = new Sim();
        under.spm = .5;
        under.speed = .92;
        while (under.elapsed < ClockPace.SHORT_WINDOW + 1) under.step();
        check(String.format("a rung of 2 run at %.2f reads \"1×\", as the ladder writes 1x, never \"1.0×\"; at %.2f, \"0.9×\"", justUnder.speed, under.speed),
                readsReached(justUnder.pace, "2×", 2) && justUnder.pace.reading("2×", 2).equals("1×")
                        && readsReached(under.pace, "2×", 2) && under.pace.reading("2×", 2).equals("0.9×"));

        // (c) The list: every text seen above and in section 4 is one reachedTexts() gives, and a sweep of speeds finds none outside it.
        java.util.List<String> list = ClockPace.reachedTexts(TOP);
        for (int k = 1; k < 100; k++) {          // the top rung, run from nothing up to the tenth under it
            Sim s = new Sim();
            s.spm = .5;
            s.speed = TOP * (1 - ClockPace.SHORT_BY) * k / 100;
            while (s.elapsed < ClockPace.SHORT_WINDOW + .5) s.step();
            reachedOf(s.pace, TOP_LABEL, TOP);
        }
        for (int k = 1; k <= 40; k++) {          // a rung of 2 under 1.8, in steps of a twentieth: the decimals, and 1x
            Sim s = new Sim();
            s.spm = .5;
            s.speed = k / 20.0;
            while (s.elapsed < ClockPace.SHORT_WINDOW + .5) s.step();
            reachedOf(s.pace, "2×", 2);
        }
        System.out.printf("  %d texts in the list (%s ... %s); %d seen in this run%n", list.size(), list.get(0), list.get(list.size() - 1), SEEN.size());
        check("every text the pace reached in this run is in reachedTexts() (" + SEEN.size() + " seen, " + list.size() + " listed)",
                list.containsAll(SEEN) && SEEN.size() > 40);
        check("...the list holds a text once, the tenths from 0.0× to 0.9× (no 1.0×) and the wholes up to a tenth under the top rung",
                new java.util.HashSet<>(list).size() == list.size() && list.contains("0.0×") && list.contains("0.9×") && !list.contains("1.0×")
                        && list.contains("1×") && list.contains(Math.round(TOP * (1 - ClockPace.SHORT_BY)) + "×")
                        && !list.contains(TOP_LABEL));
    }
}
