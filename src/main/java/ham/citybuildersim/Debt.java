package ham.citybuildersim;

/**
 * One piece of city paper.
 *
 * PRICED OFF ITS OWN CASH FLOWS, NOT OFF ITS SHAPE
 *
 * Everything a bond is worth, and everything it yields, comes from one thing:
 * the money it still owes and when. So that is what a subclass declares -
 * `remainingCashFlows()`, one figure per remaining month - and market value and
 * yield to maturity are computed here, once, from that.
 *
 * The previous version used the closed-form annuity instead:
 *
 *     MV = c x [1 - (1+r)^-n] / r  +  F / (1+r)^n
 *
 * which is exactly right for a bullet and silently wrong for anything else. A
 * serial bond repays principal in slices, so its coupon falls every year and its
 * principal arrives in pieces; the annuity formula would have priced it as
 * though the whole face were sitting at the end, over-valuing it badly. Adding
 * the serial bond is what made the shape assumption visible, but the assumption
 * was already there.
 *
 * A schedule cannot be wrong about a shape it does not know about. The next
 * instrument - a revenue bond, a serial with a deferred first slice, anything -
 * gets correct pricing and yield with no change here at all.
 */
public abstract class Debt {

    protected double faceValue;
    protected double outstandingPrincipal;
    protected int duration;
    protected int remainingMonths;
    protected int monthStarted;
    protected String type;

    public abstract void processMonth(Game game);

    public abstract double getIssuePrice();

    public abstract double getOustandingPrincipal();

    public abstract int getMaturityMonth();

    public abstract boolean isMatured();

    public abstract String getType();

    /** This month's coupon. Zero for a discount instrument, and honestly so. */
    public abstract double getMonthlyInterestExpense();

    /**
     * Every payment still owed, in order, starting with next month's.
     *
     * Coupons AND principal, because a buyer does not care which is which - they
     * care what arrives and when. An empty array means nothing is left to pay.
     */
    public abstract double[] remainingCashFlows();

    /** Months of payments still to run. */
    public int getRemainingMonths() {
        return remainingMonths;
    }

    /** What it says on the bond. Not what is still owed - see getOustandingPrincipal(). */
    public double getFaceValue() {
        return faceValue;
    }

    public int getDuration() {
        return duration;
    }

    public int getMonthStarted() {
        return monthStarted;
    }

    /**
     * What this paper is worth today, to somebody buying it.
     *
     * The present value of everything it still owes, discounted at the rate the
     * market is charging the city NOW.
     *
     * WHY THIS IS NOT THE FACE VALUE, which is the whole point of being able to
     * buy it back:
     *
     *   - Market rate ABOVE the coupon -> worth less than face. The city can
     *     extinguish $1,000 of debt for less than $1,000. Not a loophole; it is
     *     what happens to anyone's bonds when their credit deteriorates, and a
     *     city that has damaged its own credit really can retire its paper
     *     cheaply.
     *   - Market rate BELOW the coupon -> worth more than face, and getting out
     *     early costs a premium. A city that borrowed dear and then improved
     *     pays for the lender's good deal.
     *
     * PRICED AT THE CURRENT RATE, not at the rate that would apply afterwards.
     * There is a fixed point lurking - retiring debt lowers the city's rate,
     * which raises what the remaining bonds are worth - and issuance already
     * walks that loop in quoteRate(). Deliberately not walked here: a buyer
     * quotes you off the market as it stands, and chasing the post-trade rate
     * would make every quote depend on which bond you happened to buy first.
     */
    public double getMarketValue(double annualMarketRate) {

        /*
         * Due now, so there is nothing left to discount and it costs what is
         * owed to clear it.
         *
         * processAllDebts() removes matured paper, so this should be
         * unreachable in play - but "should be unreachable" is how the schedule
         * would quietly return an empty array and price a bond that is due this
         * instant at ZERO, which on the buyback screen is a free retirement of
         * real debt. Worth a line to make impossible rather than improbable.
         */
        if (remainingMonths <= 0) {
            return getOustandingPrincipal();
        }
        return presentValue(remainingCashFlows(), annualMarketRate);
    }

    /** PV of a monthly schedule at an annual nominal rate. */
    static double presentValue(double[] cashFlows, double annualRate) {

        if (cashFlows == null || cashFlows.length == 0) {
            return 0;
        }

        double r = annualRate / 12.0;

        // A non-positive rate discounts nothing. MIN_RATE keeps the real market
        // well above this, but a fixture is free to hand in anything and an
        // undiscounted sum is the honest limit rather than a divide-by-zero.
        if (r <= -1 + 1e-9) {
            double sum = 0;
            for (double cf : cashFlows) sum += cf;
            return sum;
        }

        double pv = 0;
        double discount = 1;
        for (double cf : cashFlows) {
            discount /= (1 + r);
            pv += cf * discount;
        }
        return pv;
    }

    /**
     * The yield a buyer earns at a given price - the bond's true cost to the
     * city, as opposed to the coupon printed on it.
     *
     * This is the number that makes "premium" and "discount" mean something. A
     * 3% bond bought at 80 cents on the dollar does not yield 3%; it yields
     * whatever makes its remaining payments worth 80. Coupon is what the paper
     * says, yield is what the money does, and for a long bond issued at a deep
     * discount the two are nowhere near each other.
     *
     * SOLVED BY BISECTION, not by a formula, because there is no closed form for
     * an arbitrary schedule - which is exactly the point of having a schedule.
     * PV falls monotonically as the rate rises, so bisection cannot get lost;
     * eighty iterations over a 0-500% bracket is far more precision than a
     * percentage on a screen can use.
     *
     * @return the nominal annual yield, or 0 if the bond owes nothing
     */
    public double getYieldToMaturity(double price) {

        double[] flows = remainingCashFlows();
        if (flows.length == 0 || price <= 0) {
            return 0;
        }

        double total = 0;
        for (double cf : flows) total += cf;

        // Paying more than every remaining payment put together is a negative
        // yield. Real, but off the bottom of the bracket, so it is reported as
        // zero rather than as a wrong number.
        if (price >= total) {
            return 0;
        }

        double low = 0;
        double high = 5.0;

        for (int i = 0; i < 80; i++) {
            double mid = (low + high) / 2;
            if (presentValue(flows, mid) > price) {
                low = mid;      // still too valuable: demand a higher yield
            } else {
                high = mid;
            }
        }
        return (low + high) / 2;
    }

    /** Yield at what the market would actually pay today. */
    public double getCurrentYield(double annualMarketRate) {
        return getYieldToMaturity(getMarketValue(annualMarketRate));
    }

    /**
     * Where this bond trades against par, as a percentage of face.
     *
     * 100 is par, above is a premium, below is a discount. The convention every
     * bond desk quotes in, and much easier to read across bonds of different
     * sizes than two dollar figures.
     */
    public double getPriceAsPercentOfPar(double annualMarketRate) {
        double owed = getOustandingPrincipal();
        if (owed <= 0) return 0;
        return getMarketValue(annualMarketRate) / owed * 100;
    }
}
