package ham.citybuildersim;

/**
 * A term loan: a coupon every month on the whole face, and the whole face at
 * the end - issued only at the five maturities in MATURITIES (0.7.1).
 *
 * @author Jerus
 */
public class LongTermBond extends Debt {

    /* =======================================================================
       FIVE MATURITIES (0.7.1)

       Jerus: "i think we should only be able to issue 10y 20y 30y 40y and
       50y, so less granulity there, serial and tbills are fine tho." A term
       loan was any whole number of years from ten to fifty, which is forty-one
       points on a curve nobody could read; a real treasury issues at a
       handful of benchmark maturities so that each one trades, and the curve
       is those points. Existing paper keeps its months - a 25-year bond in a
       save runs off - and only new issues are held to the five.
       ======================================================================= */

    /** The only terms a term loan is issued at, in years: five benchmark maturities, so the curve is five points a player can read. */
    public static final int[] MATURITIES = {10, 20, 30, 40, 50};

    /** What the treasury is told when it asks for any other term. */
    public static final String REFUSAL = "Term loans are issued at 10, 20, 30, 40 or 50 years.";

    /** True for one of the five. */
    public static boolean isIssuable(int years) {
        for (int m : MATURITIES) if (m == years) return true;
        return false;
    }

    private double monthlyCouponRate;
    
    public LongTermBond(double faceValue, int months, int monthStarted, double couponRate) {
        this(faceValue, months, monthStarted, couponRate, false);
    }

    /** @param foreign true for a bond written in USD. Face and coupon are then USD. */
    public LongTermBond(double faceValue, int months, int monthStarted,
                        double couponRate, boolean foreign) {
        this.foreign = foreign;
        this.faceValue = faceValue;
        this.remainingMonths = months;
        this.duration = months;
        this.monthStarted = monthStarted;
        this.monthlyCouponRate = couponRate / 12;
        this.outstandingPrincipal = faceValue;
        this.type = "TERM";
    }

    /**
     * Long bonds are deliberately a combination instrument: a LOW monthly coupon
     * plus a redemption premium (face value exceeds the cash received). The
     * player trades a higher all-in cost for much smaller monthly payments.
     *
     * The premium is sized in Game.handleLongBondLogic(). Previously the face was
     * grossed up by (1+yield)^duration - full compound interest priced into the
     * discount - AND this coupon was charged on top, so the instrument billed the
     * same interest twice and was strictly worse than a medium bond at every
     * duration.
     */
    @Override
    public void processMonth(Game game) {
        double interest = (faceValue*monthlyCouponRate);
        payCoupon(game, interest);
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
    protected double principalOwed(){
        return outstandingPrincipal;
    }
    
    @Override
    public int getMaturityMonth(){
        return monthStarted+duration;
    }
    
    @Override
    public boolean isMatured(){
        return(remainingMonths <= 0);
    }
    
    @Override
    public String getType(){
        return type;
    }
    
    @Override
    protected double couponOwed(){
        return faceValue * monthlyCouponRate;
    }

    public double getCouponRate() {
        return monthlyCouponRate * 12;
    }

    /**
     * Coupon every month, and the whole face at the end.
     *
     * The bullet, and now the ONLY bullet among the three: the note repays a
     * lump but pays no coupon, and the serial bond amortises. That makes this
     * the instrument with the redemption cliff, which is its character rather
     * than a flaw - you buy a very low monthly payment and you owe the lot in
     * thirty years. The maturity strip along the bottom of the window
     * exists so that is visible for years beforehand rather than on the morning.
     */
    @Override
    protected double[] scheduleOwed() {

        if (remainingMonths <= 0) {
            return new double[0];
        }

        double coupon = faceValue * monthlyCouponRate;
        double[] flows = new double[remainingMonths];
        java.util.Arrays.fill(flows, coupon);
        flows[remainingMonths - 1] += faceValue;
        return flows;
    }
}
