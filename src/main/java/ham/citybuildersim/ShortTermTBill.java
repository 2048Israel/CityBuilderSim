package ham.citybuildersim;

/**
 * A bill: borrow now, repay one lump, no coupon - at 1, 3, 6 or 12 months,
 * each sale its own line (CD2).
 *
 * The municipal instrument this models is a tax or bond anticipation note - cash
 * to bridge a gap until the taxes arrive or the long-term financing is arranged.
 * That is exactly how it is used here, and Jerus's own framing of it is the same
 * one a treasurer would use: an emergency-room visit, not a way to live.
 *
 * A DISCOUNT INSTRUMENT. There is no interest payment. The lender hands over
 * less than the face value and collects the face at maturity, and the gap
 * between the two IS the interest. `getMonthlyInterestExpense()` therefore
 * returns 0 and that is not an oversight - the COUPON figure on Finances (the
 * NEXT DUE card's totals until 0.7.32, the debt bar's until 0.7.24) says as
 * much in words, because a bare zero under a heading called INTEREST reads
 * as free money.
 *
 * WHY THE PRICE IS NOT HERE ANY MORE. The discount used to be a simple one,
 * face = request / (1 - rate x months/12), struck by this class and by Game,
 * while getMarketValue() compounds monthly - so the issue and the buyback
 * priced the same paper two ways, and the gap between them was a small loss
 * on a round trip by luck of the direction. A bill is sold at auction now
 * (Auction): one clearing yield, the price the face discounted at that yield
 * monthly, the same sum DebtManager.marketValue() reads back, so the round
 * trip is a loss of the fee and the tail by construction. The quote carries
 * the price (Game.quoteTBill()); this class holds the lump.
 */
public class ShortTermTBill extends Debt {

    public ShortTermTBill(double faceValue, int months, int monthStarted) {
        this(faceValue, months, monthStarted, false);
    }

    /** @param foreign true for a note written in USD. Face is then in USD. */
    public ShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign){
        this.foreign = foreign;
        this.faceValue = faceValue;
        this.remainingMonths = months;
        this.duration = months;
        this.monthStarted = monthStarted;
        this.outstandingPrincipal = faceValue;
        this.type = "NOTE";
    }

    /** The terms a bill is sold at, in months (D3): Auction.BILL_MONTHS. A bill already on the books may have any other. */
    public static final int[] TERMS = Auction.BILL_MONTHS;

    /** True for one of the terms a bill is sold at. */
    public static boolean isIssuable(int months) {
        for (int m : TERMS) if (m == months) return true;
        return false;
    }

    /** The one of TERMS nearest this many months; the shorter on a tie. */
    public static int nearestTerm(int months) {
        int best = TERMS[0];
        for (int m : TERMS) if (Math.abs(m - months) < Math.abs(best - months)) best = m;
        return best;
    }

    @Override
    public void processMonth(Game game){
        remainingMonths--;
        if(remainingMonths <= 0){
            payPrincipal(game, faceValue);
        }
    }

    @Override
    public double getIssuePrice(){
        return .03;
    }

    //getters
    public int getMonths(){
        return remainingMonths;
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
        return 0;
    }

    /** Nothing until maturity, then the whole face. */
    @Override
    protected double[] scheduleOwed() {
        if (remainingMonths <= 0) {
            return new double[0];
        }
        double[] flows = new double[remainingMonths];
        flows[remainingMonths - 1] = faceValue;
        return flows;
    }
}
