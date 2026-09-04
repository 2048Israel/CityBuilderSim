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

    private CityCalendar() { }

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
