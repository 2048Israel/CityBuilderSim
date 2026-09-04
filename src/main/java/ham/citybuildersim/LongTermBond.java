package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public class LongTermBond extends Debt {
    
    private double monthlyCouponRate;
    
    public LongTermBond(double faceValue, int months, int monthStarted,double couponRate) {
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
        game.InterestExpense(interest);
        remainingMonths--;

        if (remainingMonths <= 0) {
            game.subtractCash(faceValue);
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
        return(remainingMonths <= 0);
    }
    
    @Override
    public String getType(){
        return type;
    }
    
    @Override
    public double getMonthlyInterestExpense(){
        double interest = (faceValue*monthlyCouponRate);
        return interest;
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
     * twenty-five years. The maturity strip along the bottom of the window
     * exists so that is visible for years beforehand rather than on the morning.
     */
    @Override
    public double[] remainingCashFlows() {

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
