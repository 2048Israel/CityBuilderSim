package ham.citybuildersim;

/**
 * The steps of a count, an area or an amount, following its leading digit.
 *
 * WHY: fixed +10/+100 counts and million-dollar transfers took hundreds of
 * presses in a large city (SZ1). One pure rule serves every magnitude control.
 */
public final class MagnitudeSteps {
    private MagnitudeSteps() { }

    /** Leading unit, then its tenth, without duplicates or a step below least; an empty figure has just least. */
    public static double[] of(double value, double least) {
        if (!(least > 0) || !Double.isFinite(least)) throw new IllegalArgumentException("least step must be positive and finite");
        if (!(value > 0) || !Double.isFinite(value)) return new double[] { least };
        double unit = Math.pow(10, Math.floor(Math.log10(value)));
        if (unit > value) unit /= 10; // log10 can round up just below a decade.
        double big = Math.max(least, unit);
        double fine = Math.max(least, big / 10);
        return fine < big ? new double[] { big, fine } : new double[] { big };
    }

    /** A count moved by a step, clamped at zero and saturated before long arithmetic could wrap. */
    public static long count(long value, double step) {
        double moved = value + step;
        return moved >= Long.MAX_VALUE ? Long.MAX_VALUE : moved <= 0 ? 0 : (long) moved;
    }

    /** The largest 1, 2 or 5 times a power of ten no greater than value. */
    public static double roundDown125(double value) {
        if (!(value > 0) || !Double.isFinite(value)) throw new IllegalArgumentException("rounding needs a positive finite value");
        double unit = Math.pow(10, Math.floor(Math.log10(value)));
        // Compare the candidates themselves: division can round a value just
        // below 5 * unit up to 5 and wrongly skip the valid 2 * unit.
        if (5 * unit <= value) return 5 * unit;
        if (2 * unit <= value) return 2 * unit;
        return unit <= value ? unit : 5 * (unit / 10);
    }
}
