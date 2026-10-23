package ham.citybuildersim;

/**
 * A floating-rate note: a coupon every month on the whole face, the month's
 * policy rate plus a margin fixed at the sale, and the face at the end - at 2
 * or 5 years, each sale its own line (CD5, spec-city-debt.md section 2.4).
 *
 * WHY: a fixed bond hands the dial's risk to the lender, and a lender who
 * fears the dial moving charges for it in the coupon. The treasury that wants
 * to carry the dial's risk itself sells paper whose coupon follows it - the US
 * Treasury has sold floaters on its own bill rate since 2014 - and here the
 * index is the dial the player moves (Jerus: "floating-rate notes on the
 * policy rate"). The margin is what prices the note at 100 on the curve (the
 * term premium and the credit spread its life carries, and the gap between
 * the expected dial and the floor), so the note sells near par whatever the
 * dial does, and the cost of the credit is whatever the dial then does to the
 * coupon. Nothing sells one unasked: the default playtest holds none, and a
 * city that never sells one has every figure it had.
 *
 * THE BOOKS (spec section 5.3): the effective rate resets with the coupon, as
 * a floating liability's does. The schedule the books use is THE KNOWN ONE -
 * today's coupon held flat every month and the face at the end (IFRS 9
 * B5.4.5's convention for a floating liability) - and when the dial has moved
 * DebtManager hands the new one to reset(), which solves the effective rate
 * again on the carrying value as it stands. No catch-up: the carrying value
 * does not jump, the rate that accretes it changes. The MARKET's schedule is
 * another thing - the coupons at the dial it expects - and is
 * projectedFlows(); it prices the note (DebtManager.pricingFlows()) and the
 * buyback offers on it.
 *
 * Domestic paper only: there are no dollar floaters (section 2.7).
 *
 * @author Jerus
 */
public class FloatingRateNote extends Debt {

    /** The only terms a floating-rate note is issued at, in years (D25). */
    public static final int[] MATURITIES = {2, 5};

    /** What the treasury is told when it asks for any other term. */
    public static final String REFUSAL = "Floating-rate notes are issued at 2 or 5 years.";

    /** True for one of the two. */
    public static boolean isIssuable(int years) {
        for (int m : MATURITIES) if (m == years) return true;
        return false;
    }

    /** The one of MATURITIES nearest this many years; the shorter on a tie. What a floater falling due rolls into. */
    public static int nearestYears(int years) {
        int best = MATURITIES[0];
        for (int m : MATURITIES) {
            if (Math.abs(m - years) < Math.abs(best - years)) best = m;
        }
        return best;
    }

    /** The margin over the policy rate, a year, fixed at the sale; it may be nothing or below. Saved with the piece. */
    private double margin;

    /** The policy rate, a year, as the last reset read it: the dial the coupon now follows. Saved with the piece. */
    private double index;

    /**
     * @param margin the margin over the policy rate, a year, as the sale set it
     * @param index  the policy rate now, which the first coupon follows until the next reset
     */
    public FloatingRateNote(double faceValue, int months, int monthStarted, double margin, double index) {
        this.foreign = false;
        this.faceValue = faceValue;
        this.remainingMonths = months;
        this.duration = months;
        this.monthStarted = monthStarted;
        this.margin = margin;
        this.index = index;
        this.outstandingPrincipal = faceValue;
        this.type = "FLOAT";
    }

    /** The margin over the policy rate, a year. */
    public double getMargin() {
        return margin;
    }

    /** The policy rate, a year, as the last reset read it. */
    public double getIndex() {
        return index;
    }

    /** The coupon, a year, at the dial as the last reset read it: the index and the margin, never under nothing. */
    public double couponRate() {
        return Math.max(0, index + margin);
    }

    /** ...the same figure under the name LongTermBond's gives it. */
    public double getCouponRate() {
        return couponRate();
    }

    /**
     * The month's dial, handed in by DebtManager every month BEFORE amortise():
     * when it has moved, the coupon with it, and the effective rate is solved
     * again on the new known schedule at the carrying value as it stands
     * (Debt.reestimateRate()). Unmoved, nothing changes - to the bit.
     */
    void reset(double policyRate) {
        if (!Double.isFinite(policyRate) || policyRate == index) return;
        index = policyRate;
        reestimateRate();
    }

    /** A coupon on the whole face at the month's dial, every month; the face in the last. */
    @Override
    public void processMonth(Game game) {
        payCoupon(game, couponOwed());
        remainingMonths--;

        if (remainingMonths <= 0) {
            payPrincipal(game, faceValue);
        }
    }

    @Override
    public double getIssuePrice() {
        return faceValue;
    }

    @Override
    protected double principalOwed() {
        return outstandingPrincipal;
    }

    @Override
    public int getMaturityMonth() {
        return monthStarted + duration;
    }

    @Override
    public boolean isMatured() {
        return remainingMonths <= 0;
    }

    @Override
    public String getType() {
        return type;
    }

    /** This month's coupon: the face at the coupon rate the last reset read, a twelfth. */
    @Override
    protected double couponOwed() {
        return faceValue * couponRate() / 12;
    }

    /**
     * THE KNOWN SCHEDULE: today's coupon held flat every month, and the face
     * in the last. What the city's books follow (Debt.amortise()) and what the
     * ladder and dueWithin() read - what is owed at today's dial. Not what the
     * market prices it on (projectedFlows()).
     */
    @Override
    protected double[] scheduleOwed() {
        if (remainingMonths <= 0) {
            return new double[0];
        }
        double[] flows = new double[remainingMonths];
        java.util.Arrays.fill(flows, couponOwed());
        flows[remainingMonths - 1] += faceValue;
        return flows;
    }

    /**
     * THE MARKET'S SCHEDULE: each coupon at the dial the market expects when
     * it is paid, the face added to the last. {@code policyPath[t]} is the dial
     * expected t months ahead (t = 0 is now), and the coupon paid i + 1 months
     * from now reads the dial of that tick - the month's dial is set at its
     * top, before the debts are paid - so flows[i] reads policyPath[i + 1],
     * the last entry held if the path is shorter. A path nobody gives is the
     * known schedule.
     */
    public double[] projectedFlows(double[] policyPath) {
        if (policyPath == null || policyPath.length == 0) return scheduleOwed();
        return flowsFor(faceValue, margin, policyPath, remainingMonths);
    }

    /**
     * A floater's payments on a path of the dial: {@code face} of it, at this
     * margin, for this many months - the one arithmetic projectedFlows(),
     * DebtManager.fairMargin() and the auction's schedule share. A path
     * nobody gives reads the dial as nothing.
     */
    public static double[] flowsFor(double face, double margin, double[] policyPath, int months) {
        double[] flows = new double[Math.max(0, months)];
        if (flows.length == 0) return flows;
        int last = policyPath == null ? -1 : policyPath.length - 1;
        for (int i = 0; i < flows.length; i++) {
            double dial = last < 0 ? 0 : policyPath[Math.min(i + 1, last)];
            flows[i] = face * Math.max(0, dial + margin) / 12;
        }
        flows[flows.length - 1] += face;
        return flows;
    }
}
