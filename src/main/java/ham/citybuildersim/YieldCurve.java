package ham.citybuildersim;

import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;

/**
 * The city's curve: a discount factor for every month to fifty years, built
 * from the dial the market expects, the credit spreads, and the shape the term
 * premium and the central bank's holdings give the long end (CD1,
 * docs/studies/spec-city-debt.md section 3). Pure arithmetic - no game, no
 * screen - so a fixture can build one from numbers and DebtManager builds one
 * from the city.
 *
 * WHY. The curve this replaced sat on the dial in parallel: DebtManager's
 * priceAt() was the floor plus the two credit spreads plus a premium that
 * depended on the months and on nothing else, so a city at 7% on the dial
 * with prices rising 6% had a 50-year rate 1.6 points over its 10-year, and a
 * city holding the dial at 1% by hand against 5% inflation sold fifty-year
 * money at 2.62% (spec section 1.4). And marketValue() discounted a piece's
 * whole schedule at ONE flat rate, the curve read at its last month, so the
 * yield The book printed was the rate it had been handed: solving a schedule
 * for the yield that gives back the price it was priced at returns the rate it
 * was priced at (spec section 1.2). Here every payment is discounted at its
 * own month's factor, and a piece's yield is a result.
 *
 * THE CONSTRUCTION. The dial the market expects, month by month (p, from
 * expectedPath()), plus today's credit spreads, clamped at the ceiling and the
 * floor of rates, is the forward rate f(t) for month t. Compounded over the
 * months to m it is the zero rate before the term shape, z0(m); the term
 * premium less the compression is added on top (the shape, supplied by the
 * caller); and the discount factor is d(m) = (1 + z(m)/12)^(-m). Month 0 is
 * this month, so f(0) is the short rate the city is quoted today, to the bit.
 *
 * @author Jerus
 */
public final class YieldCurve {

    /** The time constant of the dial's expected return to the rule, in months (D15, Jerus's to move): the gap between where the dial stands and where the rule would put it closes by a factor of e in this many. */
    public static final int DRIFT_MONTHS = 6;

    /** The months the curve is built to: fifty years, the longest term loan; the curve holds its last zero rate past it. */
    public static final int HORIZON = 600;

    /** The expected dial p(t), t = 0 .. HORIZON-1. */
    private final double[] path;
    /** The forward f(t) = credit-spread-laden, clamped. */
    private final double[] forward;
    /** z0(m), m = 0 .. HORIZON: compounded forwards, before the shape. */
    private final double[] baseZero;
    /** The shape the caller gave, m = 0 .. HORIZON (0 at 0). */
    private final double[] shape;
    /** z(m) = z0(m) + shape(m), m = 0 .. HORIZON. */
    private final double[] zero;
    /** d(m), m = 0 .. HORIZON. */
    private final double[] factor;
    /** The running sum of d(1..m), m = 0 .. HORIZON: the annuity a par yield divides by. */
    private final double[] annuity;
    private final double effectiveSpread;
    private final boolean flat;

    /* ----------------------------- the paths ----------------------------- */

    /**
     * The dial the market expects, month by month: p(t) = R(t) + (floor - R(0))
     * x e^(-t / DRIFT_MONTHS), where R(t) is the rule at the inflation expected
     * t months ahead, pi(t) = pi_inf + (pi_now - pi_inf) x e^(-t /
     * Expectations.ADAPT_MONTHS). The gap between the dial and the rule closes
     * whoever holds the dial; with the rule on the dial there is no gap. Month 0
     * is the floor itself, exactly.
     *
     * @param floor             today's floor (DebtManager.floorRate())
     * @param rule              the rule as a function of inflation (the city's advisedPolicyRate())
     * @param inflationNow      the inflation the rule reads this month
     * @param expectedInflation where the city expects inflation to settle (Expectations.getExpectedInflation())
     */
    public static double[] expectedPath(double floor, DoubleUnaryOperator rule, double inflationNow, double expectedInflation) {
        double[] p = new double[HORIZON];
        p[0] = floor;
        double gap = floor - rule.applyAsDouble(inflationNow);
        for (int t = 1; t < HORIZON; t++) {
            double pi = expectedInflation + (inflationNow - expectedInflation) * Math.exp(-(double) t / Expectations.ADAPT_MONTHS);
            p[t] = rule.applyAsDouble(pi) + gap * Math.exp(-(double) t / DRIFT_MONTHS);
        }
        return p;
    }

    /** A dial that never moves: the floor every month. A manager nobody has struck expectations on prices this path, which is the parallel curve of the old pricing. */
    public static double[] flatPath(double floor) {
        double[] p = new double[HORIZON];
        java.util.Arrays.fill(p, floor);
        return p;
    }

    /* --------------------------- building one --------------------------- */

    /**
     * The curve from a path, the two credit spreads, the rate the two cannot
     * pass, and the shape.
     *
     * The forward in month t is max(MIN_RATE, min((p + gdpSpread) + revenueSpread,
     * p + (ceilingNow - p(0)))) - the sum taken in the order priceAt() took it,
     * and month 0 clamped at the ceiling itself, so f(0) is the short rate the
     * city has always been quoted, to the bit. Where every forward is the same
     * number the zero rate is that number, not the round trip of it through
     * the compounding: a flat path prices today's parallel curve to the bit.
     *
     * @param path       p(0..): the expected dial, held at its last month if shorter than HORIZON; not copied, not to be changed
     * @param gdpSpread  what the debt costs against the economy
     * @param revenueSpread ...and against what the city collects
     * @param ceilingNow the most the short rate can be today (DebtManager.ceilingRate())
     * @param shape      termPremium(m) - compression(m) for m >= 1 (0 up to a year)
     */
    public YieldCurve(double[] path, double gdpSpread, double revenueSpread, double ceilingNow, IntToDoubleFunction shape) {
        if (path == null || path.length == 0) throw new IllegalArgumentException("a curve needs a path");
        if (path.length == HORIZON) {
            this.path = path;
        } else {
            this.path = new double[HORIZON];
            for (int t = 0; t < HORIZON; t++) this.path[t] = path[Math.min(t, path.length - 1)];
        }
        double p0 = this.path[0];
        double gap = ceilingNow - p0;
        effectiveSpread = Math.min(gdpSpread + revenueSpread, gap);

        forward = new double[HORIZON];
        boolean same = true;
        for (int t = 0; t < HORIZON; t++) {
            double p = this.path[t];
            forward[t] = (t == 0 || p == p0) ? shortRate(p, gdpSpread, revenueSpread, ceilingNow)
                    : Math.max(DebtManager.MIN_RATE, Math.min((p + gdpSpread) + revenueSpread, p + gap));
            if (forward[t] != forward[0]) same = false;
        }
        flat = same;

        baseZero = new double[HORIZON + 1];
        this.shape = new double[HORIZON + 1];
        zero = new double[HORIZON + 1];
        factor = new double[HORIZON + 1];
        annuity = new double[HORIZON + 1];
        baseZero[0] = forward[0];
        zero[0] = forward[0];
        factor[0] = 1;
        double cumulative = 0;
        for (int m = 1; m <= HORIZON; m++) {
            cumulative += Math.log1p(forward[m - 1] / 12);
            baseZero[m] = flat ? forward[0] : (Math.exp(cumulative / m) - 1) * 12;
            this.shape[m] = shape.applyAsDouble(m);
            zero[m] = baseZero[m] + this.shape[m];
            factor[m] = discount(zero[m], m);
            annuity[m] = annuity[m - 1] + factor[m];
        }
    }

    /**
     * The short rate the city is quoted: the floor and the two credit spreads
     * added in that order, held between the cheapest money (DebtManager.MIN_RATE)
     * and the ceiling. The one definition month 0 of every curve reads, and what
     * DebtManager.priceAt() returns for no months - the figure the city was
     * quoted before there was a curve.
     */
    public static double shortRate(double floor, double gdpSpread, double revenueSpread, double ceilingNow) {
        return Math.max(DebtManager.MIN_RATE, Math.min((floor + gdpSpread) + revenueSpread, ceilingNow));
    }

    /** A curve from a path, one number for the credit spreads and the room between the path's first month and the ceiling - the fixture's way in; DebtManager hands the two spreads and the ceiling to the constructor. */
    public static YieldCurve of(double[] path, double spreads, double ceilingGap, IntToDoubleFunction shape) {
        if (path == null || path.length == 0) throw new IllegalArgumentException("a curve needs a path");
        return new YieldCurve(path, spreads, 0, path[0] + ceilingGap, shape);
    }

    /** (1 + z/12)^(-m); a rate that discounts nothing (as Debt.presentValue treats one) gives 1. */
    private static double discount(double z, int m) {
        double r = z / 12;
        if (r <= -1 + 1e-9) return 1;
        return Math.exp(-m * Math.log1p(r));
    }

    /* ------------------------------- reads ------------------------------- */

    /** The dial expected in month t ahead (0 this month); clamped to the path. */
    public double path(int t) {
        return path[Math.max(0, Math.min(t, HORIZON - 1))];
    }

    /** The forward rate for month t ahead (0 this month, the short rate); clamped to the path. */
    public double forward(int t) {
        return forward[Math.max(0, Math.min(t, HORIZON - 1))];
    }

    /** The zero rate for m months, a nominal annual rate: the compounded forwards plus the shape. Zero or fewer months is the short rate; past fifty years the curve holds. */
    public double zero(int m) {
        return zero[Math.max(0, Math.min(m, HORIZON))];
    }

    /** The compounded forwards over m months, before the shape (z0). Zero or fewer months is the short rate; past fifty years the curve holds. */
    public double baseZero(int m) {
        return baseZero[Math.max(0, Math.min(m, HORIZON))];
    }

    /** The shape at m months: the term premium less the compression the caller gave (0 up to a year and at 0). */
    public double shape(int m) {
        return shape[Math.max(0, Math.min(m, HORIZON))];
    }

    /** The factor a payment m months away is discounted by: (1 + z(m)/12)^(-m); 1 now; past fifty years at the last zero rate. */
    public double factor(int m) {
        if (m <= 0) return 1;
        if (m <= HORIZON) return factor[m];
        return discount(zero[HORIZON], m);
    }

    /** The present value of flows paid at months 1, 2, ...: each at its own month's factor. Nothing owed is worth 0. */
    public double pv(double[] flows) {
        if (flows == null) return 0;
        double total = 0;
        for (int i = 0; i < flows.length; i++) total += flows[i] * factor(i + 1);
        return total;
    }

    /** The spread the forward actually carries this month: the two credit spreads, or the room under the ceiling if less. The floor of rates is not a spread. */
    public double effectiveSpread() {
        return effectiveSpread;
    }

    /**
     * The dial the market expects, averaged over m months, and nothing else in
     * the rate: z0(m) less the spread. With effectiveSpread() and shape(m) it
     * adds back to zero(m) (to rounding, a few parts in 1e16; fixtures assert it
     * at 1e-12) - "the rate taken apart" on the Finances screen.
     */
    public double expectedDialOver(int m) {
        return baseZero(m) - effectiveSpread;
    }

    /**
     * The annual coupon a monthly-paying bullet of this term needs to price at
     * par on this curve: (1 - d(n)) / (the sum of d(1..n) / 12). The short rate
     * for zero or fewer months.
     */
    public double parYield(int months) {
        if (months <= 0) return forward[0];
        double sum = annuity[Math.min(months, HORIZON)];
        for (int t = HORIZON + 1; t <= months; t++) sum += factor(t);
        return sum > 0 ? (1 - factor(months)) / (sum / 12) : forward[0];
    }

    /** True when every forward is the same number: the path is flat (a city on target with the dial at the rule, or one nobody struck), and the curve is the old parallel one. */
    public boolean isFlat() {
        return flat;
    }
}
