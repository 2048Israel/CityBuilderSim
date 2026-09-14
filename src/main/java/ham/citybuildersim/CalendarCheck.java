package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The date on the status bar, and the log of what the city has finished.
 *
 * Both are new and both are the kind of thing that looks obviously right and is
 * quietly off by one. The calendar in particular has two independent chances to
 * be wrong - the epoch and the modulo - and they cancel out at exactly the point
 * anyone would eyeball it (month 1), so it is checked at the boundaries of every
 * year it touches rather than at a couple of convenient months.
 */
public class CalendarCheck {

    static int fails = 0;

    static void check(String label, long actual, long expected) {
        boolean ok = actual == expected;
        if (!ok) fails++;
        System.out.printf("%-56s %12d  expected %12d  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void same(String label, String actual, String expected) {
        boolean ok = expected.equals(actual);
        if (!ok) fails++;
        System.out.printf("%-56s %-22s %s%n", label,
                "\"" + actual + "\"", ok ? "OK" : "FAIL  expected \"" + expected + "\"");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-56s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) throws Exception {

        /* ==================== 1. the epoch ==================== */
        System.out.println("--- month 1 is January 2000 ---");

        check("month 1 -> year", CityCalendar.yearOf(1), 2000);
        check("month 1 -> month of year", CityCalendar.monthOfYear(1), 1);
        same("month 1 formatted", CityCalendar.format(1), "January 2000");

        // A new game really does start at month 1, so the epoch is not an
        // assumption this harness is making on its own.
        Path root = Files.createTempDirectory("cal");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        PrintStream out = System.out;
        PrintStream quiet = new PrintStream(new OutputStream() {
            @Override public void write(int b) { }
            @Override public void write(byte[] b, int off, int len) { }
        });
        Game g = new Game(files);
        System.setOut(quiet);
        try { g.run(); } finally { System.setOut(out); }

        check("a new game starts at month 1", g.getMonth(), 1);

        /* ==================== 2. every boundary in the first year ============ */
        System.out.println("\n--- walking the first year ---");

        same("month 12", CityCalendar.format(12), "December 2000");
        same("month 13", CityCalendar.format(13), "January 2001");
        check("month 12 is still 2000", CityCalendar.yearOf(12), 2000);
        check("month 13 rolls over", CityCalendar.yearOf(13), 2001);

        /*
         * The one Jerus stated outright: "120 months later it's 2010". 120
         * months after month 1 is month 121, and that has to be January 2010 or
         * the epoch is off by a year somewhere.
         */
        System.out.println("\n--- the decade Jerus specified ---");
        same("month 121, ten years on", CityCalendar.format(121), "January 2010");
        check("...and 120 months IS ten years", CityCalendar.yearOf(121) - 2000, 10);
        same("month 120, the month before", CityCalendar.format(120), "December 2009");

        /* ==================== 3. the far end and the bad end ================= */
        System.out.println("\n--- long runs and nonsense input ---");

        // LongPlaytest ends at m4002, so the panel really does render this.
        same("month 4002 (the playtest's last)", CityCalendar.format(4002), "June 2333");
        check("4002 lands in 2333", CityCalendar.yearOf(4002), 2333);

        /* ================= days, which only the clock uses =================
         *
         * Added 2026-09-14 with play/pause. Nothing in the simulation happens
         * on a day - these exist so a clock running at five seconds a month has
         * something to move between months. Presentation, and therefore exactly
         * the kind of thing that is never checked until it prints February the
         * 31st at somebody.
         */
        check("January has 31",  CityCalendar.daysIn(1),  31);
        check("April has 30",    CityCalendar.daysIn(4),  30);
        check("December has 31", CityCalendar.daysIn(12), 31);
        // Month 2 is February 2000, and the epoch year is a leap year - which
        // this check asserted 28 for on the first pass and was rightly told
        // otherwise. The common-year February is month 14.
        check("February 2001 has 28", CityCalendar.daysIn(14), 28);

        // 2000 is a leap year (divisible by 400), 2100 is not (by 100 and not 400).
        assertTrue("2000 was a leap year",      CityCalendar.isLeapYear(2000));
        assertTrue("2100 is not",              !CityCalendar.isLeapYear(2100));
        assertTrue("2400 is",                   CityCalendar.isLeapYear(2400));
        assertTrue("2001 is not",              !CityCalendar.isLeapYear(2001));
        check("...so month 2 has 29 days",      CityCalendar.daysIn(2), 29);
        check("February 2100 has 28",           CityCalendar.daysIn(1 + 100 * 12 + 1), 28);

        /*
         * THE DAY NEVER LEAVES THE MONTH, at either end. The clock hands this a
         * fraction that can sit exactly on 0 or reach 1 on the frame the month
         * lands, and a day of 0 or 32 would be on screen for that frame.
         */
        check("a month opens on the first",  CityCalendar.dayOf(1, 0),     1);
        check("...and negative is still the first", CityCalendar.dayOf(1, -0.5), 1);
        check("half way through January",    CityCalendar.dayOf(1, 0.5),   16);
        check("the end of January is the 31st", CityCalendar.dayOf(1, 1),  31);
        check("...and past the end is still the 31st", CityCalendar.dayOf(1, 9), 31);
        check("the end of February is the 29th in 2000", CityCalendar.dayOf(2, 1), 29);
        boolean inside = true;
        for (int m = 1; m <= 120; m++) {
            for (int s = 0; s <= 100; s++) {
                int d = CityCalendar.dayOf(m, s / 100.0);
                if (d < 1 || d > CityCalendar.daysIn(m)) inside = false;
            }
        }
        assertTrue("ten years of days, none of them outside their month", inside);
        same("the date the clock prints", CityCalendar.formatDay(3, 0.5), "16 March 2000");

        /*
         * Month 0 and below are not reachable in play, but a corrupt save can
         * hand them over and a status bar must not be the thing that dies.
         * Floored at the epoch rather than counting back into 1999.
         */
        same("month 0 floors at the epoch", CityCalendar.format(0), "January 2000");
        same("a negative month does too", CityCalendar.format(-40), "January 2000");

        /* ==================== 4. "in N months" ==================== */
        System.out.println("\n--- how far off a maturity is ---");

        same("same month", CityCalendar.until(100, 100), "this month");
        same("one ahead", CityCalendar.until(100, 101), "next month");
        same("under two years", CityCalendar.until(100, 118), "in 18 months");
        same("exactly two years", CityCalendar.until(100, 124), "in 2 years");
        same("two and a bit", CityCalendar.until(100, 131), "in 2y 7m");
        same("already passed", CityCalendar.until(100, 96), "overdue");

        /* ==================== 5. the build log ==================== */
        System.out.println("\n--- what the city has finished ---");

        BuildLog log = new BuildLog();
        log.record("House", 4, 10);
        check("one entry", log.size(), 1);

        /*
         * MERGING IS THE POINT, and it is what this differs from DemolitionLog
         * by. advanceConstruction() reports per STACK, so a city with houses on
         * two sites finishes "House" twice in one month. Two rows saying 4 and 3
         * where the player built 7 is not a log, it is a puzzle.
         */
        log.record("House", 3, 10);
        check("same building, same month, still one entry", log.size(), 1);
        check("...and the quantities added", log.recent(10).get(0).quantity, 7);

        log.record("Convenience Store", 2, 10);
        check("a different building is its own entry", log.size(), 2);

        log.record("House", 5, 11);
        check("the same building next month is a new entry", log.size(), 3);

        /* ---- ordering and expiry ---- */
        check("newest first", log.recent(11).get(0).quantity, 5);

        // KEEP_MONTHS is 24, matching demolitions, and the boundary is inclusive.
        assertTrue("the window matches demolitions",
                BuildLog.KEEP_MONTHS == DemolitionLog.KEEP_MONTHS);
        check("at exactly 24 months it is still shown",
                log.recent(10 + BuildLog.KEEP_MONTHS).size(), 3);
        check("one month later the month-10 rows are gone",
                log.recent(11 + BuildLog.KEEP_MONTHS).size(), 1);
        check("...but nothing was actually deleted", log.size(), 3);

        same("the wording", log.recent(11).get(0).when(11), "this month");
        same("...last month", log.recent(11).get(0).when(12), "last month");
        same("...older", log.recent(11).get(0).when(15), "4 months ago");

        /* ---- a null restore, which is every pre-format-10 save ---- */
        BuildLog restored = new BuildLog();
        restored.record("Coal Power Plant", 1, 3);
        restored.restore(null);
        check("restoring null empties it rather than throwing", restored.size(), 0);

        restored.restore(log.all());
        check("a real restore keeps every entry", restored.size(), 3);
        check("...and the merge did not run again on the way back in",
                restored.recent(10).get(0).quantity, 5);

        /* ==================== 6. through a real month ==================== */
        System.out.println("\n--- a city actually finishing something ---");

        System.setOut(quiet);
        try {
            BuildingsTemplate house = g.getBuildingManager().getTemplateByName("House");
            g.buildStack(house, 6, false);
            // Long enough for six houses to actually top out.
            g.simulateMonths(24);
        } finally { System.setOut(out); }

        java.util.List<BuildLog.Entry> live = g.getBuildLog().recent(g.getMonth());
        assertTrue("the city logged what it finished", !live.isEmpty());

        int houses = 0;
        for (BuildLog.Entry e : live) {
            if ("House".equals(e.building)) houses += e.quantity;
        }
        assertTrue("...and the houses are in it (" + houses + ")", houses >= 6);

        // Every entry must be stamped with a month that actually happened.
        boolean sane = true;
        for (BuildLog.Entry e : live) {
            if (e.month < 1 || e.month > g.getMonth()) sane = false;
        }
        assertTrue("every entry is stamped inside the game's own timeline", sane);

        cleanUp(root);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
