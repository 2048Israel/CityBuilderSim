package ham.citybuildersim;

/**
 * A bond: a fixed coupon every month on the whole face, and the face at the
 * end - at 2, 5, 10, 20, 30 or 50 years, sold in lines that reopen (CD2).
 *
 * WHY: until CD2 this was a deep-discount "term loan" - a coupon of about a
 * third of the market rate, the face grossed up until its payments were worth
 * the cash - which no treasury sells and which showed the player a coupon
 * that was not the price. A bond is sold at auction now (Auction): its coupon
 * is the clearing yield rounded down to an eighth of a point, set at issue
 * and never changed, so a new line sells just under 100, and a sale at the
 * same term while a line has nine tenths of it left goes into that line
 * (Debt.reopen(), DebtManager.benchmarkLine()). The class keeps its name
 * (spec-city-debt.md, star 3): it is any bond of 2 to 50 years.
 *
 * @author Jerus
 */
public class LongTermBond extends Debt {

    /* =======================================================================
       SIX MATURITIES (CD2; five from 0.7.1)

       Jerus: "2, 5, 10, 20, 30, 50" - 40 goes (spec-city-debt.md section
       2.2). A real treasury issues at a handful of benchmark maturities so
       that each one trades, and the curve is those points. Existing paper
       keeps its months - a 25-year bond in a save runs off - and only new
       issues are held to the six.
       ======================================================================= */

    /** The only terms a bond is issued at, in years: six benchmark maturities, so each trades as a line. */
    public static final int[] MATURITIES = {2, 5, 10, 20, 30, 50};

    /** What the treasury is told when it asks for any other term. */
    public static final String REFUSAL = "Bonds are issued at 2, 5, 10, 20, 30 or 50 years.";

    /** True for one of the six. */
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

    /** A coupon on the whole face, every month; the face in the last. */
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

    /** The coupon, a year, as the auction set it at issue (Auction.COUPON_STEP): the same figure getCouponRate() gives. */
    public double couponRate() {
        return monthlyCouponRate * 12;
    }

    /**
     * Coupon every month, and the whole face at the end.
     *
     * The bullet, and the ONLY bullet among the three: the bill repays a
     * lump but pays no coupon, and the amortizing bond amortises. That makes
     * this the instrument with the redemption cliff, which is its character
     * rather than a flaw - you owe the lot in thirty years. The Finances hub's ladder (the maturity strip along the
     * bottom of the window until 0.7.24) exists so that is visible for years
     * beforehand rather than on the morning.
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
