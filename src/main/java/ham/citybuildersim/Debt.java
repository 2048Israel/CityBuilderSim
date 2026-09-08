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

    /* =======================================================================
       WHICH MONEY THIS PAPER IS WRITTEN IN
       =======================================================================

       ORIGINAL SIN, which is the name the literature gives it: a city that
       cannot borrow abroad in its own currency carries the exchange risk
       itself. It owes dollars and earns local money, so a devaluation makes the
       debt dearer without anybody having borrowed another cent. Domestic paper
       does the opposite - inflation and devaluation quietly shrink it.

       HOW IT IS STORED, and this is the part that has to be got right once:

         - a foreign bond holds its face, its principal and its coupon in USD,
           because that is what the contract says and it does not change when
           the currency moves
         - every PUBLIC getter returns LOCAL money, converted at the live rate,
           because that is what the city's books, its debt-to-GDP, its credit
           rating and every screen are denominated in

       So `getOustandingPrincipal()` on a USD bond rises when the currency falls,
       everywhere in the game at once, with no call site knowing anything about
       it. That is the whole mechanic.

       ENFORCED BY THE COMPILER rather than by memory. The three money-valued
       getters are FINAL here and convert; what a subclass declares instead is
       the figure in the paper's OWN currency. A subclass cannot forget to
       convert, because it is not given the chance - the previous shape, where
       each subclass returned its own number, would have needed the conversion
       written correctly in nine places and wrong in none.
       ======================================================================= */

    /** True if this paper is written in USD. */
    protected boolean foreign;

    /** Local currency per USD, as of the last month tick. See DebtManager. */
    protected double exchangeRate = 1.0;

    public boolean isForeign() { return foreign; }

    /** The rate this paper is currently being valued at. */
    public double getExchangeRate() { return exchangeRate; }

    /**
     * Told to it by DebtManager, every month and on the load path.
     *
     * A rate of zero or less is refused rather than accepted, because a bond
     * valued at zero disappears out of the city's debt silently, and a save
     * restored before the exchange rate is would do exactly that.
     */
    void setExchangeRate(double rate) {
        if (rate > 0) this.exchangeRate = rate;
    }

    /** USD into local money, for foreign paper; the identity for domestic. */
    protected double inLocal(double own) {
        return foreign ? own * exchangeRate : own;
    }

    public abstract void processMonth(Game game);

    public abstract double getIssuePrice();

    public abstract int getMaturityMonth();

    public abstract boolean isMatured();

    public abstract String getType();

    /* ------------- what a subclass declares, in its own currency ------------- */

    /** Principal still owed, in the currency the paper is written in. */
    protected abstract double principalOwed();

    /** This month's coupon, in its own currency. Zero for a discount instrument. */
    protected abstract double couponOwed();

    /**
     * Every payment still owed, in order, starting with next month's, in its own
     * currency.
     *
     * Coupons AND principal, because a buyer does not care which is which - they
     * care what arrives and when. An empty array means nothing is left to pay.
     */
    protected abstract double[] scheduleOwed();

    /* -------------------- ...and what the city's books see -------------------- */

    public final double getOustandingPrincipal() { return inLocal(principalOwed()); }

    /** This month's coupon, in local money. Zero for a discount instrument. */
    public final double getMonthlyInterestExpense() { return inLocal(couponOwed()); }

    public final double[] remainingCashFlows() {
        double[] own = scheduleOwed();
        if (!foreign) return own;
        double[] local = new double[own.length];
        for (int i = 0; i < own.length; i++) local[i] = own[i] * exchangeRate;
        return local;
    }

    /* ------------------- and the same figures, in dollars ------------------- */

    /** Principal still owed, in the currency written on the paper. */
    public final double principalInCurrency() { return principalOwed(); }

    /** Face value in the currency written on the paper. */
    public final double faceInCurrency() { return faceValue; }

    /** This month's coupon in the currency written on the paper. */
    public final double couponInCurrency() { return couponOwed(); }

    /* --------------------------- paying for it --------------------------- */

    /**
     * A repayment of principal, routed by the currency it is owed in.
     *
     * The subclasses used to call `game.subtractCash()` directly, which is
     * exactly right for domestic paper and exactly wrong for foreign: a USD
     * repayment does not just leave the treasury, it leaves the COUNTRY, and it
     * must not be handed to the bank as a repayment of a loan the bank never
     * made. Both differences live behind this one call.
     *
     * @param owed in the paper's own currency
     */
    protected void payPrincipal(Game game, double owed) {
        if (foreign) game.repayForeignPrincipal(owed);
        else         game.subtractCash(owed);
    }

    /** A coupon, likewise. @param owed in the paper's own currency */
    protected void payCoupon(Game game, double owed) {
        if (foreign) game.payForeignInterest(owed);
        else         game.InterestExpense(owed);
    }

    /** Months of payments still to run. */
    public int getRemainingMonths() {
        return remainingMonths;
    }

    /** What it says on the bond, in local money. See getOustandingPrincipal(). */
    public final double getFaceValue() {
        return inLocal(faceValue);
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

    /**
     * The instrument in the new unit.
     *
     * FOREIGN PAPER DOES NOT MOVE, and that is the whole reason Debt knows what
     * currency it is in. A bond issued in US dollars owes US dollars whatever
     * the city calls its money this century; what changes is the exchange rate
     * it is translated at, and inLocal() reads that. Redenominating a foreign
     * face value would be defaulting on it by arithmetic.
     */
    public void redenominate(double scale) {
        if (!foreign) {
            faceValue *= scale;
            outstandingPrincipal *= scale;
        }
        exchangeRate *= scale;
        redenominateSchedule(scale);
    }

    /**
     * Anything a subclass carries in its own currency - a coupon, an
     * amortisation schedule - in the new unit. Default: nothing to do.
     */
    protected void redenominateSchedule(double scale) { }

}
