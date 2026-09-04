package ham.citybuildersim;

/**
 * A serial bond: the workhorse of municipal finance.
 *
 * A single issue is really a stack of maturities. A city raising $10M over ten
 * years does not sell one bond due in 2036; it sells a slice due each year, and
 * pays each slice off as it comes. Principal falls the whole way, so the coupon
 * falls with it, and there is no balloon at the end because there is no lump
 * left to balloon.
 *
 * WHAT THIS REPLACES, AND WHY IT MATTERS MORE THAN IT SOUNDS
 *
 * This was a bullet: coupon on the full face for the whole term, then the entire
 * face out of the treasury in one month. Two things follow from amortising
 * instead, and the second is the one worth having:
 *
 *   1. The city's debt actually goes DOWN as it pays. Outstanding principal is
 *      what the rate curve prices against (getPricedDebt), so a serial bond
 *      gently improves the city's credit over its life, where a bullet held the
 *      rate up until the day it vanished.
 *   2. The coupon falls every year. A serial bond starts dearer than a bullet of
 *      the same size and ends far cheaper, which is exactly the real trade and
 *      is now a real decision against the term bond.
 *
 * ANNUAL SLICES, MONTHLY COUPON, which is how these are actually structured -
 * principal on an anniversary, interest every period on whatever is still out.
 *
 * NOT A MORTGAGE. Level debt service - equal total payments, like a house - is
 * the other common municipal structure and deliberately not this one: it hides
 * how much principal is left behind a flat number, and Jerus asked for the
 * mechanics to stay legible.
 */
public class MediumTermBond extends Debt {

    private double monthlyCouponRate;

    /** Principal repaid on each anniversary. Not final: see repairAfterLoad(). */
    private double principalPerSlice;

    /** Anniversaries left to pay, including the one at maturity. */
    private int slicesRemaining;

    public MediumTermBond(double faceValue, int months, int monthStarted, double couponRate) {
        this.faceValue = faceValue;
        this.remainingMonths = months;
        this.duration = months;
        this.monthStarted = monthStarted;
        this.outstandingPrincipal = faceValue;
        this.monthlyCouponRate = couponRate / 12;
        this.type = "SERIAL";

        // At least one slice, so a sub-year bond still repays rather than
        // running to maturity owing everything and paying nothing.
        this.slicesRemaining = Math.max(1, months / 12);
        this.principalPerSlice = faceValue / slicesRemaining;
    }

    /**
     * Rebuilds the amortisation schedule after a load.
     *
     * NEEDED BECAUSE THIS CLASS CHANGED SHAPE. A save written before the serial
     * rewrite has faceValue, remainingMonths and a coupon, and knows nothing
     * about slices - Gson fills what it finds and leaves the rest at zero, which
     * would restore a bond that owes principal and has no schedule to repay it
     * on. It would sit there paying coupon forever and never amortise.
     *
     * Derived from what IS in the save rather than from the original face, so a
     * bond reloaded halfway through its life gets slices sized to what is
     * actually left. A bullet from an older save therefore comes back as a
     * serial bond over its remaining term, which is a real change to what the
     * player owes - but the alternative is a bond that never repays at all, and
     * of the two, honest amortisation is the one that is not broken.
     */
    void repairAfterLoad() {
        if (slicesRemaining > 0 && principalPerSlice > 0) {
            return;
        }
        slicesRemaining = Math.max(1, Math.max(remainingMonths, 1) / 12);
        principalPerSlice = outstandingPrincipal / slicesRemaining;
    }

    /**
     * Coupon every month; principal on each anniversary.
     *
     * The final slice is whatever is actually left rather than
     * principalPerSlice, so rounding cannot leave a city owing $0.003 forever or
     * repaying three cents more than it borrowed.
     */
    @Override
    public void processMonth(Game game) {

        double interest = outstandingPrincipal * monthlyCouponRate;
        if (interest > 0) {
            game.InterestExpense(interest);
        }

        remainingMonths--;

        boolean anniversary = (remainingMonths % 12 == 0) || remainingMonths <= 0;

        if (anniversary && slicesRemaining > 0) {
            double due = (slicesRemaining == 1)
                    ? outstandingPrincipal
                    : Math.min(principalPerSlice, outstandingPrincipal);
            game.subtractCash(due);
            outstandingPrincipal -= due;
            slicesRemaining--;
        }
    }

    @Override
    public double getIssuePrice() {
        return faceValue;
    }

    @Override
    public double getOustandingPrincipal(){
        return outstandingPrincipal;
    }

    @Override
    public int getMaturityMonth(){
        return monthStarted+duration;
    }

    @Override
    public boolean isMatured(){
        return remainingMonths <= 0 && outstandingPrincipal <= 1e-9;
    }

    @Override
    public String getType(){
        return type;
    }

    /** On what is still out, not on the original face. */
    @Override
    public double getMonthlyInterestExpense(){
        return outstandingPrincipal * monthlyCouponRate;
    }

    /** How much principal is retired on each anniversary. */
    public double getPrincipalPerSlice() {
        return principalPerSlice;
    }

    public int getSlicesRemaining() {
        return slicesRemaining;
    }

    /**
     * Coupon on the declining balance, with a principal slice each anniversary.
     *
     * Walked forward month by month rather than solved, because the balance the
     * coupon is charged on changes underneath it - which is the entire
     * difference between this and a bullet, and precisely what a closed-form
     * annuity cannot see.
     */
    @Override
    public double[] remainingCashFlows() {

        if (remainingMonths <= 0) {
            return new double[0];
        }

        double[] flows = new double[remainingMonths];

        double balance = outstandingPrincipal;
        int slices = slicesRemaining;
        int monthsLeft = remainingMonths;

        for (int i = 0; i < flows.length; i++) {

            flows[i] = balance * monthlyCouponRate;

            monthsLeft--;
            boolean anniversary = (monthsLeft % 12 == 0) || monthsLeft <= 0;

            if (anniversary && slices > 0) {
                double due = (slices == 1) ? balance : Math.min(principalPerSlice, balance);
                flows[i] += due;
                balance -= due;
                slices--;
            }
        }
        return flows;
    }
}
