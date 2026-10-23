package ham.citybuildersim;

import java.util.function.IntToDoubleFunction;

/**
 * What a callable bond is worth, and what the right to call it costs: a Ho-Lee
 * short-rate tree on monthly steps, fitted to the city's curve (CD4,
 * docs/studies/spec-city-debt.md section 2.6; D22, D23, D24). Pure arithmetic -
 * no game, no screen - so a fixture can price a call from a hand-built
 * YieldCurve and Game and DebtManager price one from the city's.
 *
 * WHY. Until CD4 the city could not sell callable paper at all; the only way to
 * close a line early was to buy it back at the market's price, which a
 * treasury that sold at 4% and now borrows at 2% does at a premium. A call is a
 * right the city buys at the sale and pays for in the coupon, and what it is
 * worth depends on how far rates can move and on the shape of the curve, so a
 * flat add-on (the same points for a calm city and a volatile one, for a
 * 10-year and a 30-year) would be wrong in every city but one. The tree is the
 * smallest model that prices it from the curve the game already has and from
 * one measured number, the volatility of the city's own rate.
 *
 * THE TREE. At node (i, j) - i months on, j of the steps up - the short rate
 * is r(i, j) = theta(i) + sigma x sqrt(dt) x (2j - i), each step up or down
 * with probability a half, and a month's discount at a node is 1 / (1 + r /
 * 12), the curve's own convention (YieldCurve.factor(m) = (1 + z(m)/12)^-m).
 * theta(i) is fitted one step at a time by forward induction on Arrow-Debreu
 * prices (the price today of a unit paid at one node): each theta is the one
 * that makes the tree's price of a zero-coupon payment i + 1 months away the
 * curve's factor(i + 1), to rounding. So a bond with no call prices on the
 * tree exactly as on the curve - the check that the tree is the curve - and
 * the call is the only thing the tree adds. Backward through the tree, from the
 * first call month on, the city calls whenever the bond is worth more than the
 * call price: the value at a node is the smaller of what the bond is worth
 * going on and the call price.
 *
 * @author Jerus
 */
public final class CallTree {

    /* =======================================================================
       THE DIALS (spec-city-debt.md section 9, D22-D24)
       ======================================================================= */

    /** The least volatility of the short rate the tree is priced at, a year (D23): 0.75 points, so a calm city that has hardly moved its rate still pays for the option. */
    public static final double RATE_VOL_FLOOR = 0.0075;

    /** How many months of the city's own rate the volatility is measured over (D23): five years of monthly changes. */
    public static final int VOL_MONTHS = 60;

    /** The fewest monthly changes the volatility is measured from: under a year's worth (13 months of the rate) the city has no history worth the name and prices at the floor. */
    public static final int VOL_MIN_CHANGES = 12;

    /** A callable 10-year may first be called this many years after it is sold (D22). */
    public static final int FIRST_CALL_10Y = 5;

    /** ...and a callable 20-, 30- or 50-year this many (D22). */
    public static final int FIRST_CALL_LONG = 10;

    /** The shortest term a bond may be sold callable at, in years (D22, Jerus: "any bond of ten years or more"). */
    public static final int CALLABLE_MIN_YEARS = 10;

    /** What a call pays, per 100 of the principal called: par (D22). */
    public static final double CALL_PRICE = 100;

    /** A call is worth making when the present value it saves passes this share of the face (D24): 3%, the threshold US municipal refunding practice uses. */
    public static final double WORTH_CALLING = 0.03;

    /** What the treasury is told when it asks for a callable bond that is not one of the terms that may be. */
    public static final String REFUSAL = "Only bonds of 10 years or more can be sold callable, at 10, 20, 30 or 50 years.";

    /** The bisection finds a coupon to a hundred-millionth of a point: far under the eighth of a point a coupon is rounded to. */
    private static final double COUPON_TOLERANCE = 1e-12;

    /** The most Newton steps the fit of one theta takes before it settles for the bisection's bracket. */
    private static final int MAX_FIT_STEPS = 80;

    /* =======================================================================
       WHICH BONDS MAY BE CALLABLE, AND FROM WHEN
       ======================================================================= */

    /** True for a term a bond may be sold callable at: one of the issuable terms, ten years or more. */
    public static boolean canBeCallable(int years) {
        return years >= CALLABLE_MIN_YEARS && LongTermBond.isIssuable(years);
    }

    /**
     * Years from the sale to the first call (D22): 5 for a 10-year, 10 for a
     * 20-, 30- or 50-year; -1 for a term that may not be callable.
     */
    public static int firstCallYears(int years) {
        if (!canBeCallable(years)) return -1;
        return years == 10 ? FIRST_CALL_10Y : FIRST_CALL_LONG;
    }

    /* =======================================================================
       THE VOLATILITY (D23)
       ======================================================================= */

    /**
     * The volatility of the short rate, a year, measured from a history of it:
     * the standard deviation (of the sample) of the last VOL_MONTHS monthly
     * changes, times the square root of twelve, and never under
     * RATE_VOL_FLOOR. A month that was not counted (NaN) breaks the run: a
     * change is taken only between two months that were. With fewer than
     * VOL_MIN_CHANGES changes to go on the floor stands.
     *
     * @param rates the rate month by month, oldest first, a fraction a year; NaN where it was not kept
     */
    public static double volatility(double[] rates) {
        if (rates == null || rates.length < 2) return RATE_VOL_FLOOR;
        double[] changes = new double[rates.length - 1];
        int n = 0;
        for (int i = 1; i < rates.length; i++) {
            if (Double.isFinite(rates[i]) && Double.isFinite(rates[i - 1])) changes[n++] = rates[i] - rates[i - 1];
        }
        if (n < VOL_MIN_CHANGES) return RATE_VOL_FLOOR;
        int from = Math.max(0, n - VOL_MONTHS);
        int count = n - from;
        double mean = 0;
        for (int i = from; i < n; i++) mean += changes[i];
        mean /= count;
        double squares = 0;
        for (int i = from; i < n; i++) squares += (changes[i] - mean) * (changes[i] - mean);
        double std = Math.sqrt(squares / (count - 1));
        return Math.max(RATE_VOL_FLOOR, std * Math.sqrt(12));
    }

    /* =======================================================================
       THE TREE
       ======================================================================= */

    /** The tree's months. */
    private final int steps;
    /** The volatility the tree was fitted at, a year. */
    private final double sigma;
    /** rate[i][j]: the short rate at node (i, j), i = 0 .. steps-1, j = 0 .. i. */
    private final double[][] rate;

    private CallTree(int steps, double sigma, double[][] rate) {
        this.steps = steps;
        this.sigma = sigma;
        this.rate = rate;
    }

    /** The months the tree reaches: the longest schedule it prices. */
    public int steps() {
        return steps;
    }

    /** The volatility it was fitted at, a year. */
    public double sigma() {
        return sigma;
    }

    /** The short rate at node (i, j), a year: theta(i) plus the step times (2j - i). */
    public double rateAt(int i, int j) {
        return rate[i][j];
    }

    /** A tree fitted to the curve, for a bond of this many months, at this volatility. */
    public static CallTree fit(YieldCurve curve, double sigma, int steps) {
        return fit(curve, 0, sigma, steps);
    }

    /**
     * ...fitted to the curve with a spread added to every month's zero rate
     * (YieldCurve.factorAtSpread()): what a sale that cleared a tail over fair
     * is priced on, exactly as the non-call bond's is (YieldCurve.pvAtSpread()).
     */
    public static CallTree fit(YieldCurve curve, double spread, double sigma, int steps) {
        return fit(m -> curve.factorAtSpread(m, spread), sigma, steps);
    }

    /**
     * A tree fitted to a discount factor for every month: factor(m) for m = 1
     * .. steps. Forward induction on the Arrow-Debreu prices q(i, j): at step
     * i, theta(i) is the rate that makes the sum over j of q(i, j) / (1 +
     * r(i, j)/12) equal factor(i + 1) (a Newton iteration inside a bracket:
     * the sum falls with theta and is convex in it), and the prices of the
     * next step are half of each node's discounted price to the node above and
     * the node below.
     */
    public static CallTree fit(IntToDoubleFunction factor, double sigma, int steps) {
        int n = Math.max(0, steps);
        double s = Math.max(0, sigma) * Math.sqrt(1.0 / 12);
        double[][] rate = new double[n][];
        double[] q = {1.0};
        double previous = 1.0;
        for (int i = 0; i < n; i++) {
            double target = factor.applyAsDouble(i + 1);
            double[] shape = new double[i + 1];
            for (int j = 0; j <= i; j++) shape[j] = s * (2 * j - i);
            // The forward rate this month, as the starting guess.
            double guess = target > 0 && previous > 0 ? 12 * (previous / target - 1) : 0;
            double theta = solveTheta(q, shape, target, guess, s * i);
            double[] r = new double[i + 1];
            double[] next = new double[i + 2];
            for (int j = 0; j <= i; j++) {
                r[j] = theta + shape[j];
                double d = q[j] / (1 + r[j] / 12);
                next[j] += 0.5 * d;
                next[j + 1] += 0.5 * d;
            }
            rate[i] = r;
            q = next;
            previous = target;
        }
        return new CallTree(n, sigma, rate);
    }

    /**
     * The theta that prices a zero-coupon payment a month further out at the
     * target: the root of f(theta) = sum q_j / (1 + (theta + shape_j)/12) -
     * target, which falls as theta rises. Bracketed above the lowest theta that
     * keeps every node's month's discount positive, and walked by Newton's
     * method from the guess, halving the bracket whenever a step would leave
     * it.
     *
     * @param lowestShape s x i: the deepest the nodes fall under theta
     */
    private static double solveTheta(double[] q, double[] shape, double target, double guess, double lowestShape) {
        double lo = -12 + lowestShape + 1e-9;
        double hi = Math.max(1, lo + 1);
        for (int k = 0; k < 60 && sumAt(q, shape, hi) > target; k++) hi = lo + (hi - lo) * 2;
        double theta = Math.min(hi, Math.max(lo + 1e-6, guess));
        for (int k = 0; k < MAX_FIT_STEPS; k++) {
            double value = 0, slope = 0;
            for (int j = 0; j < q.length; j++) {
                double d = 1 / (1 + (theta + shape[j]) / 12);
                value += q[j] * d;
                slope -= q[j] * d * d / 12;
            }
            double gap = value - target;
            if (gap == 0) return theta;
            if (gap > 0) lo = theta; else hi = theta;
            double next = slope < 0 ? theta - gap / slope : Double.NaN;
            if (!(next > lo && next < hi)) next = 0.5 * (lo + hi);
            if (Math.abs(next - theta) <= 1e-13 * Math.max(1, Math.abs(theta))) return next;
            theta = next;
        }
        return theta;
    }

    private static double sumAt(double[] q, double[] shape, double theta) {
        double value = 0;
        for (int j = 0; j < q.length; j++) value += q[j] / (1 + (theta + shape[j]) / 12);
        return value;
    }

    /* =======================================================================
       PRICING ON IT
       ======================================================================= */

    /**
     * What flows paid at months 1, 2, ... n are worth today when the issuer
     * may call the bond: backward through the tree, a month's flow added as
     * the step is made, and at every node from firstCallAhead on (after that
     * month's payment) the value is the smaller of what the bond is worth
     * going on and the call price. A firstCallAhead under 0, or at or past n
     * (the last month, where the face is repaid anyway), is a bond that cannot
     * be called. Flows and call price in the same unit; the answer is in it.
     *
     * @param flows          what is paid, month 1 first; at most steps() of them
     * @param firstCallAhead the first month, counted from now, the issuer may call after paying; 0 is callable now
     * @param callPrice      what a call pays, in the flows' unit
     */
    public double price(double[] flows, int firstCallAhead, double callPrice) {
        int n = flows == null ? 0 : flows.length;
        if (n == 0) return 0;
        if (n > steps) throw new IllegalArgumentException("a tree of " + steps + " months cannot price " + n);
        boolean call = firstCallAhead >= 0 && firstCallAhead < n;
        double[] v = new double[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            double[] r = rate[i];
            double f = flows[i];
            for (int j = 0; j <= i; j++) {
                v[j] = (0.5 * (v[j] + v[j + 1]) + f) / (1 + r[j] / 12);
            }
            if (call && i >= firstCallAhead) {
                for (int j = 0; j <= i; j++) if (v[j] > callPrice) v[j] = callPrice;
            }
        }
        return v[0];
    }

    /** What the tree prices a single unit paid m months from now at, no call: the zero-coupon price, which is the curve's factor(m) it was fitted to. */
    public double zeroCouponPrice(int m) {
        double[] flows = new double[m];
        flows[m - 1] = 1;
        return price(flows, -1, 0);
    }

    /**
     * The coupon a monthly-paying bullet of this many months needs to be worth
     * its call price - par - with the call from firstCallAhead on, less the
     * coupon it needs with none (the curve's par yield): what the option costs
     * the city in the coupon, a year. Nothing when it cannot be called.
     */
    public double optionSpread(YieldCurve curve, int months, int firstCallAhead) {
        if (months <= 0 || firstCallAhead < 0 || firstCallAhead >= months) return 0;
        double par = curve.parYield(months);
        double lo = par, hi = par + 0.25;
        for (int k = 0; k < 200 && hi - lo > COUPON_TOLERANCE; k++) {
            double mid = 0.5 * (lo + hi);
            if (price(Auction.bulletPer100(mid, months), firstCallAhead, CALL_PRICE) < CALL_PRICE - 1e-10) lo = mid; else hi = mid;
        }
        return 0.5 * (lo + hi) - par;
    }

    /* =======================================================================
       THE STATIC FORMS: a curve and a volatility, a tree fitted for the job
       ======================================================================= */

    /** price(), on a tree fitted to the curve at this volatility for just these flows. */
    public static double price(YieldCurve curve, double sigma, double[] flows, int firstCallAhead, double callPrice) {
        return price(curve, 0, sigma, flows, firstCallAhead, callPrice);
    }

    /** ...on the curve with a spread added to every month's zero rate. */
    public static double price(YieldCurve curve, double spread, double sigma, double[] flows, int firstCallAhead, double callPrice) {
        if (flows == null || flows.length == 0) return 0;
        return fit(curve, spread, sigma, flows.length).price(flows, firstCallAhead, callPrice);
    }

    /** optionSpread(), on a tree fitted to the curve at this volatility for a bond of this many months. */
    public static double optionSpread(YieldCurve curve, double sigma, int months, int firstCallAhead) {
        if (months <= 0 || firstCallAhead < 0 || firstCallAhead >= months) return 0;
        return fit(curve, sigma, months).optionSpread(curve, months, firstCallAhead);
    }
}
