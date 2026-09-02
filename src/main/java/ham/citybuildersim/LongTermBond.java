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
        this.type = "LONG-BOND";
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
}
