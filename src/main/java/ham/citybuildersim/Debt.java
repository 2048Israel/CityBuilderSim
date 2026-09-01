package ham.citybuildersim;

/**
 *
 * @author Jerus
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
    
    public abstract double getMonthlyInterestExpense();
    
}
