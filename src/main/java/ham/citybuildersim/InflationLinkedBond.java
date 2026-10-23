package ham.citybuildersim;

/**
 * An inflation-linked bond: a REAL coupon every month on a principal that
 * follows the city's price index, and at the end the indexed principal, never
 * under the face - at 10 or 30 years, each sale its own line (CD5,
 * spec-city-debt.md section 2.5; D25).
 *
 * WHY: a nominal bond asks the lender to guess the inflation of thirty years
 * and charges for the guess; a city whose central bank is believed less than
 * it deserves pays dear for it (a nominal 10-year at 5.28% in the behind city,
 * where a linked one pays a real 1.92% plus the inflation the city actually
 * has). The linked bond hands the inflation back to the city that makes it:
 * Canada sold Real Return Bonds from 1991 to 2022 and the US sells TIPS. The
 * floor is TIPS': the face at the end is never under the original 100.
 *
 * WHERE THE INDEXATION LIVES. The principal on the books (outstandingPrincipal,
 * faceValue) STAYS THE ORIGINAL FACE: every holder's share is in face, the
 * households', firms', world's and central bank's own books are in face too,
 * and indexing those would touch five models. The indexation is in the flows -
 * the coupon is the real coupon on faceValue x ratio, and the last month adds
 * the uplift (faceValue x (max(1, ratio) - 1)) as interest to every holder,
 * through the same coupon path as fixed paper, then the face as principal
 * through the same principal path. So the uplift is a cash flow of the last
 * month only; the months before it carry it on the city's books as an
 * accrual, not as cash (below).
 *
 * THE BOOKS (spec section 5.3, IFRS 9 B5.4.6). The effective rate is REAL and
 * stays what the sale made it. Every month, before the month's books are
 * struck, index() reads the price index: when the ratio of the index to its
 * base has moved, the carrying value CATCHES UP - it becomes the present value
 * of the known schedule (today's ratio held flat) at the unchanged real
 * effective rate - and the change is the month's inflation uplift, an expense
 * that is not cash (below nothing in a deflation month). The uplift paid in
 * the last month is cash, and comes off the not-cash row when it is paid.
 *
 * A line is never reopened: a linker reopened mid-life would need its index
 * ratio carried into the price of the new face, and the price of a sale at
 * ratio 1 against an indexed line is Jerus's to decide (left open).
 *
 * @author Jerus
 */
public class InflationLinkedBond extends Debt {

    /** The only terms an inflation-linked bond is issued at, in years (D25). */
    public static final int[] MATURITIES = {10, 30};

    /** What the treasury is told when it asks for any other term. */
    public static final String REFUSAL = "Inflation-linked bonds are issued at 10 or 30 years.";

    /** True for one of the two. */
    public static boolean isIssuable(int years) {
        for (int m : MATURITIES) if (m == years) return true;
        return false;
    }

    /** The one of MATURITIES nearest this many years; the shorter on a tie. What a linker falling due rolls into. */
    public static int nearestYears(int years) {
        int best = MATURITIES[0];
        for (int m : MATURITIES) {
            if (Math.abs(m - years) < Math.abs(best - years)) best = m;
        }
        return best;
    }

    /** The real coupon, a year, as the sale set it; fixed. Saved with the piece. */
    private double realCoupon;

    /** The city's price index (PriceIndex.getIndex()) when the bond was sold: what the ratio is the index over. Saved with the piece. */
    private double baseIndex;

    /** The price index over its base, as the last indexing read it: 1 at the sale. The principal's index. Saved with the piece. */
    private double ratio;

    /** The month's indexation as index() last booked it, and the uplift the last month paid, in the currency the piece is written in. Saved with the piece. */
    private double lastUpliftOwn, lastUpliftPaidOwn;

    /**
     * @param realCoupon the real coupon, a year
     * @param baseIndex  the price index now; one that is not a positive number is taken as 1
     */
    public InflationLinkedBond(double faceValue, int months, int monthStarted, double realCoupon, double baseIndex) {
        this.foreign = false;
        this.faceValue = faceValue;
        this.remainingMonths = months;
        this.duration = months;
        this.monthStarted = monthStarted;
        this.realCoupon = realCoupon;
        this.baseIndex = baseIndex > 0 && Double.isFinite(baseIndex) ? baseIndex : 1.0;
        this.ratio = 1.0;
        this.outstandingPrincipal = faceValue;
        this.type = "LINKED";
    }

    /** The real coupon, a year. */
    public double getRealCoupon() {
        return realCoupon;
    }

    /** The price index when the bond was sold. */
    public double getBaseIndex() {
        return baseIndex;
    }

    /** The price index over its base, as the last indexing read it; 1 at the sale. */
    public double getRatio() {
        return ratio;
    }

    /** The principal as it now stands indexed: the face times the ratio, in local money. What the real coupon is paid on; the face on the books is unchanged. */
    public double getIndexedPrincipal() {
        return inLocal(indexedPrincipal());
    }

    private double indexedPrincipal() {
        return faceValue * ratio;
    }

    /** What the last month's indexing added to the carrying value, in local money: the inflation uplift, an expense that is not cash; below nothing in a deflation month. */
    public double getLastUplift() {
        return inLocal(lastUpliftOwn);
    }

    /** ...and the uplift the last month PAID in cash, in local money: nothing but in the bond's last month, when it is the indexed principal less the face (never under nothing: the floor). */
    public double getLastUpliftPaid() {
        return inLocal(lastUpliftPaidOwn);
    }

    /** What the bond will repay at the end at today's ratio: the face, or the indexed principal when that is more - the deflation floor (D25, as TIPS). In local money. */
    public double owedAtMaturity() {
        return inLocal(faceValue * Math.max(1, ratio));
    }

    /** The ratio the next indexing will read at this price level: the level over the base, or the ratio as it stands for a level that is not a positive number. */
    public double ratioAt(double priceIndexLevel) {
        return priceIndexLevel > 0 && Double.isFinite(priceIndexLevel) ? priceIndexLevel / baseIndex : ratio;
    }

    /**
     * The month's indexing, handed the price index by DebtManager every month
     * BEFORE amortise(). When the level and the base are positive numbers the
     * ratio is the level over the base; when it has moved, the books CATCH UP
     * (Debt.reestimateCarrying()) and the change in the carrying value is the
     * month's uplift - below nothing when the index fell. A level that is no
     * number, or a ratio that has not moved, books nothing.
     *
     * @return the month's uplift, in the piece's own currency
     */
    double index(double priceIndexLevel) {
        lastUpliftOwn = 0;
        if (!(priceIndexLevel > 0) || !Double.isFinite(priceIndexLevel)) return 0;
        double now = priceIndexLevel / baseIndex;
        if (now == ratio) return 0;
        // A piece nobody sold (a fixture's) opens its books at the ratio it had, not the one it is about to have.
        if (!booksOpen) openBooks(principalOwed());
        ratio = now;
        lastUpliftOwn = reestimateCarrying();
        return lastUpliftOwn;
    }

    /**
     * This month's books (Debt.amortise()) and the uplift this month will pay:
     * in the last month the indexed principal less the face, never under
     * nothing - the deflation floor - and in any other month nothing.
     */
    @Override
    double amortise() {
        double discount = super.amortise();
        lastUpliftPaidOwn = remainingMonths == 1 ? faceValue * (Math.max(1, ratio) - 1) : 0;
        return discount;
    }

    /**
     * The real coupon on the indexed principal, every month; the face, and the
     * uplift as interest with the last coupon, in the last. The uplift is paid
     * through the coupon path so every holder has it as income, split by the
     * shares struck before the face is repaid.
     */
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

    /** This month's coupon: the real coupon on the indexed principal, a twelfth - and in the last month the uplift as well, faceValue x (max(1, ratio) - 1). */
    @Override
    protected double couponOwed() {
        double coupon = faceValue * ratio * realCoupon / 12;
        if (remainingMonths == 1) coupon += faceValue * (Math.max(1, ratio) - 1);
        return coupon;
    }

    /**
     * THE KNOWN SCHEDULE at today's ratio held flat: the real coupon on the
     * indexed principal every month, and in the last also the face or the
     * indexed principal if that is more. What the books follow, the ladder
     * and dueWithin() read; the market's own is projectedFlows().
     */
    @Override
    protected double[] scheduleOwed() {
        if (remainingMonths <= 0) {
            return new double[0];
        }
        double[] flows = new double[remainingMonths];
        java.util.Arrays.fill(flows, faceValue * ratio * realCoupon / 12);
        flows[remainingMonths - 1] += faceValue * Math.max(1, ratio);
        return flows;
    }

    /** The last uplift figures are quantities of money, in the new unit (and a part bought back is the same bond, smaller). */
    @Override
    protected void redenominateSchedule(double scale) {
        lastUpliftOwn *= scale;
        lastUpliftPaidOwn *= scale;
    }

    /**
     * THE MARKET'S SCHEDULE: the ratio the next payment carries is
     * {@code nextRatio} - the price index now over the base, because the next
     * tick indexes on it (the index takes its month after the debts are
     * paid) - and each later month's ratio grows by {@code monthlyGrowth[j]},
     * the growth the market expects j months ahead; the coupon is the real
     * coupon on faceValue x ratio, and the last month adds faceValue x
     * max(1, ratio) - the floor. A growth nobody gives is a ratio held flat.
     */
    public double[] projectedFlows(double nextRatio, double[] monthlyGrowth) {
        return flowsFor(faceValue, realCoupon, nextRatio, monthlyGrowth, remainingMonths);
    }

    /**
     * A linker's payments on a path of the price index: {@code face} of it, at
     * this real coupon, the first payment's ratio {@code nextRatio} and each
     * later month's grown by monthlyGrowth[i] (i months ahead; the last entry
     * held if the path is shorter, and none read as flat) - the one arithmetic
     * projectedFlows(), DebtManager.fairRealCoupon() and the auction's
     * schedule share.
     */
    public static double[] flowsFor(double face, double realCoupon, double nextRatio, double[] monthlyGrowth, int months) {
        double[] ratios = ratiosFor(nextRatio, monthlyGrowth, months);
        double[] flows = new double[ratios.length];
        if (flows.length == 0) return flows;
        for (int i = 0; i < flows.length; i++) flows[i] = face * ratios[i] * realCoupon / 12;
        flows[flows.length - 1] += face * Math.max(1, ratios[flows.length - 1]);
        return flows;
    }

    /** The index ratio each payment carries on that path: payment 0 the next ratio, each after it grown by its month's growth. */
    public static double[] ratiosFor(double nextRatio, double[] monthlyGrowth, int months) {
        double[] ratios = new double[Math.max(0, months)];
        double r = nextRatio;
        int last = monthlyGrowth == null ? -1 : monthlyGrowth.length - 1;
        for (int i = 0; i < ratios.length; i++) {
            if (i > 0 && last >= 0) r *= monthlyGrowth[Math.min(i, last)];
            ratios[i] = r;
        }
        return ratios;
    }
}
