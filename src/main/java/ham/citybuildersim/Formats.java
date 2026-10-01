package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * The few formats a sector needs to describe itself, without the toolkit.
 *
 * Sector.operations() hands the screens lines of text, and a sector class
 * must not import JavaFX to write them. These are the same shapes
 * UserInterface uses - money in dollars from a field in thousands, a
 * percentage, a count with its unit - kept here so both sides print a tonne
 * the same way.
 */
public final class Formats {

    public static final Formats INSTANCE = new Formats();

    private final NumberFormat whole = NumberFormat.getIntegerInstance(Locale.CANADA);
    private final NumberFormat money = NumberFormat.getNumberInstance(Locale.CANADA);

    private Formats() {
        money.setMinimumFractionDigits(2);
        money.setMaximumFractionDigits(2);
    }

    /** A field in thousands, as dollars: 0.12 is "$120.00". */
    public String cash(double thousands) {
        if (!Double.isFinite(thousands)) return "—";
        double dollars = thousands * 1000;
        if (Math.abs(dollars) >= 1_000_000) return "$" + whole.format(Math.round(dollars));
        return "$" + money.format(dollars);
    }

    /**
     * The same field as the screens print money (0.7.20), Money.unitPrice()'s
     * shape: cents only under a thousand dollars, where a price a unit or a
     * point needs them; whole dollars from there to a million; a million or
     * more compact, "$58.7M", as every hub prints it. The Construction page
     * read "$58,726,150" and "$18,071.98" beside "$4.1M". Never a sign on a
     * figure that rounds to nothing.
     *
     * A method of its own rather than cash() in a new shape, because cash()
     * is also how the planners write their reasons, which the playtest's
     * traces carry ("needs $1,922,909 of its own for the down payment").
     *
     * EVERY SECTOR PAGE SINCE 0.7.21, with the text cut: each sector's
     * operations() writes its money in this form, as Construction's did from
     * 0.7.20. The planners' reasons still use cash().
     */
    public String amount(double thousands) {
        if (!Double.isFinite(thousands)) return "—";
        double dollars = thousands * 1000;
        double a = Math.abs(dollars);
        if (a >= 1e12) return sign(dollars, a / 1e12, 1) + String.format("$%.1fT", a / 1e12);
        if (a >= 1e9)  return sign(dollars, a / 1e9, 1) + String.format("$%.1fB", a / 1e9);
        if (a >= 1e6)  return sign(dollars, a / 1e6, 1) + String.format("$%.1fM", a / 1e6);
        if (a >= 1_000) return sign(dollars, a, 0) + "$" + whole.format(Math.round(a));
        return sign(dollars, a, 2) + "$" + money.format(a);
    }

    /** "-" for a negative that still reads as something at `decimals` places, "" otherwise. */
    private static String sign(double value, double shown, int decimals) {
        return value < 0 && Math.round(shown * Math.pow(10, decimals)) > 0 ? "-" : "";
    }

    public String pct(double share) {
        if (!Double.isFinite(share)) return "—";
        return String.format("%.0f%%", share * 100);
    }

    public String count(double n) {
        if (!Double.isFinite(n)) return "—";
        return whole.format(Math.round(n));
    }

    /** "1,200 tonnes", "1 unit". */
    public String units(double n, Good g) {
        String unit = g == null ? "unit" : g.unit();
        long r = Math.round(n);
        return count(n) + " " + (r == 1 ? unit : plural(unit));
    }

    /** "tonnes", "units" - and "kg", which is its own plural (0.7.20). */
    public static String plural(String unit) {
        if (unit.endsWith("s") || unit.equals("kg")) return unit;
        return unit + "s";
    }
}
