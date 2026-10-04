package ham.citybuildersim.ui;

import ham.citybuildersim.*;
import java.text.NumberFormat;
import java.util.Locale;
import static ham.citybuildersim.ui.Statement.*;
import static ham.citybuildersim.ui.Pieces.*;
import static ham.citybuildersim.ui.Levers.*;

/**
 * Every figure the interface prints as money, in one place.
 *
 * These were instance methods of UserInterface that read nothing but their
 * arguments and one number formatter; they are static here so that every
 * screen - the shell and the screen classes split out of it - can call
 * money(x) as it always did, through an import static, without holding a
 * reference to the window. The model counts in THOUSANDS; toDollars() is the
 * one conversion and everything else calls it.
 */
public final class Money {

    /* =====================================================================
       THE ONE PLACE MODEL MONEY BECOMES A STRING.

       Jerus: "i think its better to add the UI so that everyone shows whether
       its thousands or millions, so yes to it coherent, its 2.3B."

       THE MODEL COUNTS IN THOUSANDS. SectorBooks says so, DebtManager's own
       printer writes "$ Thousand" beside every figure, and every statement
       screen runs its numbers through toDollars(). The chrome did not: the
       header, the panel, the debt strip and the dome printed the raw figure
       with a dollar sign in front of it, so the SAME debt appeared as
       "$2,300,000" on the strip and "$2.3B" in the vital six inches above it -
       a thousand-fold apart, on one screen, at the same moment.

       So this converts, and it abbreviates: k above ten thousand, M above a
       million, B above a billion, T above a trillion (0.7.20). Everything that
       shows city money goes through it, which is what makes the two figures
       agree.

       WHAT DOES NOT COME HERE: anything that is not money in the model's unit.
       An exchange rate is a ratio, a percentage is a percentage, and a price
       small enough to need decimals wants unitPrice() below. Passing one of
       those through here would multiply it by a thousand and be wrong in the
       opposite direction, which is exactly the bug this is fixing.
       ===================================================================== */

    /** Thousands separators, Canadian style; every figure below goes through it. */
    public static final NumberFormat formatter = withoutNegativeZero(NumberFormat.getNumberInstance(Locale.CANADA));
    static {   // this sat in the shell until 2026-09-18 (evening); it belongs to the field
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    /* =====================================================================
       NO NEGATIVE ZERO (0.7.20).

       "Died -0.0", "...killed -0.0": a figure a hair below zero, printed at a
       precision that rounds it to nothing, keeps its minus sign - Java's
       DecimalFormat writes -0.001 as "-0", and String.format writes -0.04 as
       "-0.0" and -0.0 itself as "-0.0". A minus on nothing reads as a
       direction there was not. So the shared formatters drop it here, once,
       rather than every screen guarding its own: the formatter every screen
       uses through `formatter`, and unsigned0() for the String.format ones
       below.
       ===================================================================== */

    /** The same format, except that a negative which rounds to nothing prints with no sign. */
    private static NumberFormat withoutNegativeZero(NumberFormat base) {
        if (!(base instanceof java.text.DecimalFormat d)) return base;
        return new java.text.DecimalFormat(d.toPattern(), d.getDecimalFormatSymbols()) {
            @Override
            public StringBuffer format(double number, StringBuffer to, java.text.FieldPosition at) {
                double places = Math.pow(10, getMaximumFractionDigits());
                if (number == 0 || (number < 0 && -number * places <= .5)) number = 0.0;
                return super.format(number, to, at);
            }
        };
    }

    /**
     * A value for String.format at `decimals` places, with a negative that
     * rounds to nothing there - and -0.0 itself - made a plain zero.
     */
    public static double unsigned0(double value, int decimals) {
        if (value == 0) return 0.0;
        if (value < 0 && -value * Math.pow(10, decimals) < .5) return 0.0;
        return value;
    }

    public static String pct2(double rate)  { return String.format("%.2f%%", unsigned0(rate * 100, 2)); }

    /*
     * A RATE, A SHARE AND A SPREAD, the Bank tab's three (0.7.33; its own
     * rate(), share() and points() until then: rate() wrote a year of net
     * releases a hair under nothing as "-0.00% a year" - the spec's B1 - and
     * it and share() a hyphen where points() wrote a minus). Through
     * unsigned0(), with a true minus; a rate under a tenth of a per cent to
     * three places, so savers paid 0.008% and the 0.014% the bank chose do
     * not both read "0.01%" beside a sentence saying they differ (B11).
     */

    /** A yearly rate: "2.37% a year", "0.008% a year". */
    public static String ratePerYear(double rate) { return ratePct(rate) + " a year"; }

    /** ...without its unit, for a column headed "a year": "2.37%". */
    public static String ratePct(double rate) {
        double shown = rate * 100;
        int places = shown != 0 && Math.abs(shown) < .1 ? 3 : 2;
        return String.format("%." + places + "f%%", unsigned0(shown, places)).replace('-', '−');
    }

    /** A share or a ratio that is not a yearly rate, to one place, grouped: "14.1%", "1,991.3%". */
    public static String share1(double share) {
        return String.format("%,.1f%%", unsigned0(share * 100, 1)).replace('-', '−');
    }

    /** A spread between two rates, signed, to the rates' own two places: "+2.06 points", "−0.22 points", "0.00 points". */
    public static String points(double spread) {
        double shown = unsigned0(spread * 100, 2);
        if (Math.abs(shown) < .005) return "0.00 points";
        return String.format("%s%.2f points", shown < 0 ? "−" : "+", Math.abs(shown));
    }

    /*
     * TWO DECIMALS SINCE THE LADDER. Every rate that moves off the income tax
     * steps in quarter points now, and at one decimal a quarter point printed
     * as "+0.3 pts" - a dial that lies about where it just landed.
     */
    public static String pts(double points) {
        double shown = unsigned0(points * 100, 2);
        return shown == 0 ? "0.00 pts" : String.format("%+.2f pts", shown);
    }

    /**
     * A headcount, as a whole number of people.
     *
     * NOBODY IS 0.74 OF A PERSON. The cohorts hold fractions by design - a
     * village of two hundred would otherwise print "0 born" every month - but
     * the fraction is an artefact of the arithmetic, not something a player can
     * act on, and printing "53,697.74 of working age" makes a screen look like
     * a debugger. Since 0.7.20 the monthly flows are whole people too
     * (Pieces.flowText), with "under 1" for one too small to round to a person.
     */
    public static String people(double count) {
        return formatter.format(Math.round(count));
    }

    /** A wage or a price the model holds in thousands, in the dollars it is. */
    public static String cash(double thousands) {
        return "$" + formatter.format(Math.round(toDollars(thousands)));
    }

    /**
     * Thousands into dollars, for the one screen that has to talk about a family.
     *
     * The game counts money in thousands everywhere and prints it raw, which is
     * fine for a power plant and useless for a household: an unskilled wage is
     * 0.800, so a family's monthly budget rendered in the house style is "$1"
     * and a pensioner "lives on $0.36 a month". That is not a rounding problem,
     * it is the wrong unit for the question, and this screen is the only place
     * the question gets asked. Land already does the same thing for the same
     * reason - it is kept per square foot in thousands and shown multiplied out.
     */
    public static double toDollars(double thousands) { return thousands * 1000; }

    /**
     * Money at a width that cannot overflow its column.
     *
     * THE READABILITY BUG THIS FIXES. The per-tier table formatted every cell as
     * "$" + a comma-grouped total into fields of six to nine characters. A city
     * of eighty thousand earns tens of millions, so almost every cell was wider
     * than the cell it was in - and a mono table whose first row overflows is
     * not a table any more, it is nine columns of numbers sliding sideways past
     * each other. Compact above a hundred thousand keeps every cell inside seven
     * characters whatever the city's size.
     */
    public static String tightMoney(double value) { return tightMoney(value, true); }

    /**
     * @param compact where to start abbreviating.
     *
     *        Two thresholds, because the two views of the tier table hold
     *        numbers three orders apart. A city total wants k and M from ten
     *        thousand up, or the column goes ragged - "$418k" beside
     *        "-$87,579" is the same misalignment in a politer font. A family's
     *        budget wants every digit to a million, because "$19k a month" is
     *        an answer nobody asked for when the question was "can they pay
     *        the rent".
     */
    public static String tightMoney(double value, boolean compact) {
        double a = Math.abs(value);
        String sign = value < 0 ? "-" : "";
        // Trillions (0.7.20): the load list printed a big city as "$3108.1B".
        // And each unit from where the one below it would print a thousand (0.7.40): 999.97B is
        // "$1.0T", not "$1000.0B", and 999,600 in a compact column "$1.0M", not "$1000k".
        if (a >= 1e12 || tenths(a / 1e9) >= 1000)          return String.format("%s$%.1fT", sign, a / 1e12);
        if (a >= 1_000_000_000 || tenths(a / 1e6) >= 1000) return String.format("%s$%.1fB", sign, a / 1_000_000_000);
        if (a >= 1_000_000 || (compact && Math.round(a / 1e3) >= 1000)) return String.format("%s$%.1fM", sign, a / 1_000_000);
        if (compact && a >= 10_000) return String.format("%s$%.0fk", sign, a / 1_000);
        // ...and no sign on a figure that rounds to no dollars at all: "-$0" is a direction.
        long whole = Math.round(a);
        return (whole == 0 ? "" : sign) + "$" + formatter.format(whole);
    }

    /** A figure to one place, as "%.1f" prints it: where a unit's figure would read a thousand. */
    private static double tenths(double v) { return Math.round(v * 10) / 10.0; }

    /** City money. Takes THOUSANDS - the unit the whole model counts in. */
    public static String money(double thousands) {
        return tightMoney(toDollars(thousands));
    }

    /** The same, with every digit rather than an abbreviation. */
    public static String moneyFull(double thousands) {
        return tightMoney(toDollars(thousands), false);
    }

    /**
     * Somebody else's money, marked as such: "US$" in place of the "$".
     *
     * Currency.foreign() prepends "US$" and expects a BARE figure, while every
     * money function in this file already carries a "$". Written together they
     * produced "US$$723.2M", which is the kind of thing that gets past a
     * reviewer and never past a player.
     *
     * So the mark REPLACES the symbol rather than sitting in front of it, and
     * a negative keeps its sign outside: "-$5" becomes "-US$5" rather than
     * "US$-5".
     */
    public static String marked(String prefix, String amount) {
        int at = amount.indexOf('$');
        return at < 0 ? prefix + amount
                : amount.substring(0, at) + prefix + amount.substring(at + 1);
    }

    /**
     * signed(), in the k/M column a city-scale statement wants.
     *
     * The balance-of-payments ledger reads in millions on every line but one,
     * and moneyFull() printed that one as "-$336,443" beside "+$51.2M". A
     * column that changes units line to line is a column nobody can scan.
     */
    public static String signedTight(double thousands, boolean negate) {
        if (Math.abs(thousands) < .5) return "$0";
        boolean minus = negate != (thousands < 0);
        return (minus ? "\u2212" : "+") + money(Math.abs(thousands));
    }

    /**
     * A movement, signed - and a zero movement is written without one.
     *
     * "-$0" reads as a direction, and there was not one. Same rule as the
     * treasury bridge, which learned it first.
     */
    public static String signed(double thousands, boolean negate) {
        if (Math.abs(thousands) < .5) return "$0";
        boolean minus = negate != (thousands < 0);
        return (minus ? "\u2212" : "+") + moneyFull(Math.abs(thousands));
    }

    /** Foreign money, abbreviated. */
    public static String usd(double thousands) {
        return marked(Currency.FOREIGN_SYMBOL, money(thousands));
    }

    /** ...and with every digit. */
    public static String usdFull(double thousands) {
        return marked(Currency.FOREIGN_SYMBOL, moneyFull(thousands));
    }

    /**
     * A price small enough that the cents matter - a unit on the shelf, an
     * hourly rate, anything a lopped currency has just made tiny.
     *
     * tightMoney() rounds to the dollar, which is right for a treasury and
     * useless for a loaf: the redenomination preview exists to show a $20 unit
     * becoming a $0.02 one, and rounding draws both as "$0".
     */
    public static String unitPrice(double thousands) {
        double d = toDollars(thousands);
        double a = Math.abs(d);
        if (a >= 1_000) return tightMoney(d, false);
        if (a >= 1)     return String.format("$%,.2f", d);
        if (a > 0)      return String.format("$%.4f", unsigned0(d, 4));
        return "$0";
    }

    /**
     * An exchange rate - local dollars per US dollar, a ratio and not money,
     * so it never goes through toDollars().
     *
     * AT ANY SIZE SINCE 0.7.3, when the currency's guards became a billion
     * either way (ForeignAccounts.MAX_RATE and MIN_RATE, numerical guards
     * only): four decimals where every rate has lived until now, grouped two
     * past a thousand, whole past a million, and three significant figures
     * under a ten-thousandth - where "%.4f" printed a currency still worth
     * something as 0.0000.
     *
     * Formats.rate() since 0.7.26, the same lines moved, so a rate the model
     * writes into a sentence - the land office's receipt - is written the
     * screens' way.
     */
    public static String fxRate(double rate) {
        return Formats.INSTANCE.rate(rate);
    }

    /** 12.4k rather than 12,400 - the panel is narrow and these are two to a row. */
    public static String shortNumber(double value) {
        if (value >= 1_000_000) return String.format("%.1fM", value / 1_000_000);
        if (value >= 10_000)    return String.format("%.0fk", value / 1_000);
        if (value >= 1_000)     return String.format("%.1fk", value / 1_000);
        return String.format("%.0f", value);
    }

    /**
     * Power, from the model's kilowatts (0.7.28): "900 kW", "38.9 MW", "199
     * MW", "1.18 GW" - three figures at most, the unit scaled to fit. The
     * model's power figures were always kilowatts (a coal plant's 280,000 is
     * "the 325 MW this plant actually is", Game's note on its price), and
     * the screens printed them as watts (" W") or "units a month", which
     * was a thousand times out on one and not a rate on the other. Never a
     * sign: the caller says short or spare.
     */
    public static String power(double kW) {
        if (!Double.isFinite(kW)) return "—";
        double a = Math.abs(kW);
        if (a >= 1_000_000) return scaled(a / 1_000_000) + " GW";
        if (a >= 1_000)     return scaled(a / 1_000) + " MW";
        return (a >= 10 ? formatter.format(Math.round(a)) : String.format("%.1f", a)) + " kW";
    }

    /** A figure of one to three digits before its unit: 8.1, 38.9, 199, 1.18 - its three significant figures, under 10 to two places only when they say something. */
    private static String scaled(double v) {
        if (v >= 100) return formatter.format(Math.round(v));
        if (v >= 10)  return String.format("%.1f", v);
        String two = String.format("%.2f", v);
        return two.endsWith("0") ? String.format("%.1f", v) : two;
    }

    /**
     * Months of import cover in words a player can act on (Trade's since
     * 0.7.35, every screen's since 0.7.38): "over 10 years" past ten years (a
     * city with D$100M in the vault and D$36k a month of imports has 2,777
     * months, which means nothing), "under 0.1 months" under a tenth of a
     * month (the Trade spec's B16 and D11: it printed "0.0 months" for
     * US$6.8M), "none" for an empty vault, else to one place. Its verdict
     * is ForeignAccounts.coverLevel().
     */
    public static String coverMonths(double months) {
        if (months >= 120) return "over 10 years";
        if (!(months >= .1)) return months > 0 ? "under 0.1 months" : "none";
        return String.format("%.1f months", months);
    }

}
