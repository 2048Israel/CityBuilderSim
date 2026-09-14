package ham.citybuildersim;

/**
 * Turns the month counter into a date a person can hold in their head.
 *
 * WHY THE COUNTER STAYS
 *
 * The city runs on an integer month and everything is keyed to it: saves, the
 * demolition and build logs, every bond's maturity, every report. None of that
 * changes. This is a presentation layer and nothing else - no state, no
 * instances, and deliberately no reverse function, because nothing in the game
 * should ever be deriving a month from a date string.
 *
 * THE EPOCH
 *
 * Month 1 is January 2000, which is where a new game starts (Game sets
 * this.month = 1). That makes the arithmetic land where a player expects:
 * month 121 is exactly January 2010, ten years on.
 *
 * Month 0 and negatives are not real game states, but they are reachable from a
 * corrupt save and from any month-difference someone hands in by mistake, so
 * they floor at the epoch rather than counting backwards into 1999. A date is
 * cosmetic; crashing the status bar over one is not.
 */
public final class CityCalendar {

    /** The year month 1 falls in. */
    public static final int EPOCH_YEAR = 2000;

    private static final String[] MONTHS = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    private static final String[] SHORT_MONTHS = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    /** Days in each month of a common year; February is corrected below. */
    private static final int[] DAYS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private CityCalendar() { }

    /* =====================================================================
       DAYS, WHICH THE SIMULATION DOES NOT HAVE AND THE CLOCK NEEDS

       Jerus, 2026-09-14: "have it show days so that you know whats happening
       even tho everything still only updates monthly".

       Nothing in this game happens on a day. Every market clears, every wage is
       paid and every loan accrues once a month, and that is the model rather
       than a simplification to be apologised for. What a day is for is the
       PLAYER: a clock running at five seconds a month with nothing moving in
       between looks frozen, and a date that only ever jumps is a date nobody
       can pace themselves against.

       So this is presentation, and it is deliberately honest presentation - the
       day is derived from how far through the month the clock has travelled and
       is never read by anything that decides anything. Real lengths and real
       leap years, because a February that runs to the 31st is the kind of
       detail that makes a player stop trusting the rest of the screen.
       ===================================================================== */

    /** Whether a calendar year is a leap year, by the Gregorian rule. */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    /** How many days this game month actually has. */
    public static int daysIn(int gameMonth) {
        int m = monthOfYear(gameMonth);
        if (m == 2 && isLeapYear(yearOf(gameMonth))) return 29;
        return DAYS[m - 1];
    }

    /**
     * The day of the month a given share of the way through it.
     *
     * @param progress 0 at the first of the month, 1 at the end of the last day
     * @return 1 to the length of the month, never past it
     */
    public static int dayOf(int gameMonth, double progress) {
        int days = daysIn(gameMonth);
        if (!(progress > 0)) return 1;
        return Math.max(1, Math.min(days, (int) Math.floor(progress * days) + 1));
    }

    /** "14 March 2031" - the date bar's line while the clock is running. */
    public static String formatDay(int gameMonth, double progress) {
        return dayOf(gameMonth, progress) + " " + monthName(gameMonth) + " " + yearOf(gameMonth);
    }

    /** Months since the epoch, floored at zero. */
    private static int elapsed(int gameMonth) {
        return Math.max(0, gameMonth - 1);
    }

    public static int yearOf(int gameMonth) {
        return EPOCH_YEAR + elapsed(gameMonth) / 12;
    }

    /** 1-12, the way a person counts months rather than the way an array does. */
    public static int monthOfYear(int gameMonth) {
        return elapsed(gameMonth) % 12 + 1;
    }

    public static String monthName(int gameMonth) {
        return MONTHS[elapsed(gameMonth) % 12];
    }

    public static String shortMonthName(int gameMonth) {
        return SHORT_MONTHS[elapsed(gameMonth) % 12];
    }

    /** "March 2014" - the status bar. */
    public static String format(int gameMonth) {
        return monthName(gameMonth) + " " + yearOf(gameMonth);
    }

    /** "Mar 2014" - tables and strips, where the long form does not fit. */
    public static String formatShort(int gameMonth) {
        return shortMonthName(gameMonth) + " " + yearOf(gameMonth);
    }

    /**
     * "in 3 months", "next month", "this month", "overdue".
     *
     * For anything dated in the future, which is what the maturity strip needs.
     * Overdue is a real state rather than a defensive one: a bond's maturity
     * month can pass while the player is mid-skip.
     */
    public static String until(int fromMonth, int targetMonth) {
        int gap = targetMonth - fromMonth;
        if (gap < 0)  return "overdue";
        if (gap == 0) return "this month";
        if (gap == 1) return "next month";
        if (gap < 24) return "in " + gap + " months";

        int years = gap / 12;
        int months = gap % 12;
        if (months == 0) return "in " + years + " years";
        return "in " + years + "y " + months + "m";
    }
}
