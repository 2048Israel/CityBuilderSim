package ham.citybuildersim;

import java.util.Locale;

/**
 * A cap on the yield an automatic sale takes, saved and said (D28).
 *
 * WHY (CD2, slice 6): the rollover and automatic building each sell the city's
 * paper with nobody at the button (Jerus, 2026-10-12: "each its own"), so each
 * keeps the cap the player gave it - fair plus a point by default, another
 * tail or yield, or Fill it. Auction.Cap is a record carrying NaN for Fill it,
 * and Gson does not write a NaN, so a saved cap is the two plain fields of
 * {@link Saved}: the mode's name and a value that is 0 where the mode has none.
 * The words a log line and a decision use for a cap are here too, so the
 * rollover and the builder say it the same way.
 */
public final class CapSetting {

    private CapSetting() { }

    /** A cap as it is saved: the mode by name, and the tail or the yield (0 for Fill it). */
    public static final class Saved {
        public String mode;
        public double value;

        public Saved() { }

        public Saved(String mode, double value) {
            this.mode = mode;
            this.value = value;
        }
    }

    /** The cap, as two plain fields. */
    public static Saved save(Auction.Cap cap) {
        Auction.Cap c = cap == null ? Auction.Cap.DEFAULT : cap;
        return new Saved(c.mode().name(), c.fill() ? 0 : c.value());
    }

    /** ...and back. Nothing saved, an unknown mode or a value that is not a number reads as the default: fair plus a point. */
    public static Auction.Cap restore(Saved saved) {
        if (saved == null || saved.mode == null) return Auction.Cap.DEFAULT;
        for (Auction.Mode m : Auction.Mode.values()) {
            if (!m.name().equals(saved.mode)) continue;
            switch (m) {
                case FILL: return Auction.Cap.FILL;
                case YIELD: return Double.isFinite(saved.value) ? Auction.Cap.at(saved.value) : Auction.Cap.DEFAULT;
                default: return Double.isFinite(saved.value) ? Auction.Cap.tail(saved.value) : Auction.Cap.DEFAULT;
            }
        }
        return Auction.Cap.DEFAULT;
    }

    /** Whether two caps are the same setting. */
    public static boolean same(Auction.Cap a, Auction.Cap b) {
        Auction.Cap x = a == null ? Auction.Cap.DEFAULT : a, y = b == null ? Auction.Cap.DEFAULT : b;
        if (x.mode() != y.mode()) return false;
        return x.fill() || x.value() == y.value();
    }

    /** The cap in words: "fair plus 1 point", "no more than 4.50%", "Fill it". */
    public static String words(Auction.Cap cap) {
        Auction.Cap c = cap == null ? Auction.Cap.DEFAULT : cap;
        return switch (c.mode()) {
            case FILL -> "Fill it";
            case YIELD -> String.format(Locale.ROOT, "no more than %.2f%%", c.value() * 100);
            default -> c.value() <= 0 ? "fair, no tail"
                    : "fair plus " + places(c.value() * 100) + (Math.abs(c.value() * 100 - 1) < 1e-9 ? " point" : " points");
        };
    }

    /** A number of points to as few places as it needs and never more than two: "1", "0.5", "1.25". */
    private static String places(double points) {
        String s = String.format(Locale.ROOT, "%.2f", points);
        s = s.replaceAll("0+$", "");
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }
}
