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

    private static String plural(String unit) {
        if (unit.endsWith("s")) return unit;
        return unit + "s";
    }
}
