package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public class MediumTermBond extends Debt {
    
    private double monthlyCouponRate;
    
    public MediumTermBond(double faceValue, int months, int monthStarted,double couponRate) {
        this.faceValue = faceValue;
        this.remainingMonths = months;
        this.duration = months;
        this.monthStarted = monthStarted;
        this.monthlyCouponRate = couponRate / 12;
        this.outstandingPrincipal = faceValue;
        this.type = "MEDIUM-BOND";
    }

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
