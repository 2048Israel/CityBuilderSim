package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public class ShortTermTBill extends Debt {
    
    public ShortTermTBill(double faceValue, int months, int monthStarted){
        this.faceValue = faceValue;
        this.remainingMonths = months;
        this.duration = months;
        this.monthStarted = monthStarted;
        this.outstandingPrincipal = faceValue;
        this.type = "T-BILL";
    }
    
    @Override
    public void processMonth(Game game){
        remainingMonths--;
        if(remainingMonths <= 0){
            game.subtractCash(faceValue);
        }
    }
    
    @Override
    public double getIssuePrice(){
        return .03;
    }
    
    //getters
    public double getFaceValue(){
        return faceValue;
    }
    
    public int getMonths(){
        return remainingMonths;
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
        return 0;
    }
}

