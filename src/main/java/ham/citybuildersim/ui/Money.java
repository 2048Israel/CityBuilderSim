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
       million, B above a billion. Everything that shows city money goes through
       it, which is what makes the two figures agree.

       WHAT DOES NOT COME HERE: anything that is not money in the model's unit.
       An exchange rate is a ratio, a percentage is a percentage, and a price
       small enough to need decimals wants unitPrice() below. Passing one of
       those through here would multiply it by a thousand and be wrong in the
       opposite direction, which is exactly the bug this is fixing.
       ===================================================================== */

    /** Thousands separators, Canadian style; every figure below goes through it. */
    public static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);
    static {   // this sat in the shell until 2026-09-18 (evening); it belongs to the field
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    public static String pct2(double rate)  { return String.format("%.2f%%", rate * 100); }

    /*
     * TWO DECIMALS SINCE THE LADDER. Every rate that moves off the income tax
     * steps in quarter points now, and at one decimal a quarter point printed
     * as "+0.3 pts" - a dial that lies about where it just landed.
     */
    public static String pts(double points) { return String.format("%+.2f pts", points * 100); }

    /**
     * A headcount, as a whole number of people.
     *
     * NOBODY IS 0.74 OF A PERSON. The cohorts hold fractions by design - a
     * village of two hundred would otherwise print "0 born" every month - but
     * the fraction is an artefact of the arithmetic, not something a player can
     * act on, and printing "53,697.74 of working age" makes a screen look like
     * a debugger. flowText still keeps a decimal on the small monthly flows,
     * where it is the only thing moving.
     */
    public static String people(double count) {
        return formatter.format(Math.round(count));
    }

    /** A wage or a price the model holds in thousands, in the dollars it is. */
    public static String cash(double thousands) {
        return "$" + formatter.format(Math.round(toDollars(thousands)));
    }

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
        if (a >= 1_000_000_000) return String.format("%s$%.1fB", sign, a / 1_000_000_000);
        if (a >= 1_000_000)     return String.format("%s$%.1fM", sign, a / 1_000_000);
        if (compact && a >= 10_000) return String.format("%s$%.0fk", sign, a / 1_000);
        return sign + "$" + formatter.format(Math.round(a));
    }

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
     * A movement, signed - and a zero movement is written without one.
     *
     * "-$0" reads as a direction, and there was not one. Same rule as the
     * treasury bridge, which learned it first.
     */
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
        if (a > 0)      return String.format("$%.4f", d);
        return "$0";
    }

    /** 12.4k rather than 12,400 - the panel is narrow and these are two to a row. */
    public static String shortNumber(double value) {
        if (value >= 1_000_000) return String.format("%.1fM", value / 1_000_000);
        if (value >= 10_000)    return String.format("%.0fk", value / 1_000);
        if (value >= 1_000)     return String.format("%.1fk", value / 1_000);
        return String.format("%.0f", value);
    }

}
