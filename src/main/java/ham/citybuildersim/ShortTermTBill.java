package ham.citybuildersim;

/**
 * A short-term anticipation note: borrow now, repay one lump, no coupon.
 *
 * The municipal instrument this models is a tax or bond anticipation note - cash
 * to bridge a gap until the taxes arrive or the long-term financing is arranged.
 * That is exactly how it is used here, and Jerus's own framing of it is the same
 * one a treasurer would use: an emergency-room visit, not a way to live.
 *
 * A DISCOUNT INSTRUMENT. There is no interest payment. The lender hands over
 * less than the face value and collects the face at maturity, and the gap
 * between the two IS the interest. `getMonthlyInterestExpense()` therefore
 * returns 0 and that is not an oversight - the debt bar says as much in words,
 * because a bare zero under a heading called INTEREST reads as free money.
 *
 * THE DISCOUNT NOW KNOWS HOW LONG THE NOTE RUNS
 *
 * It did not. The face was `request / (1 - rate)` with the FULL annual rate
 * applied whatever the term, so a three-month note and a two-year note were
 * discounted identically. A city borrowing for one quarter was charged a year of
 * interest, and a city borrowing for two years was charged one.
 *
 * Priced on the term now, on the bank-discount convention real bills use:
 *
 *     face = request / (1 - rate x months/12)
 *
 * That is a SIMPLE discount, while getMarketValue() compounds monthly, so the
 * two differ slightly - which is authentic rather than sloppy: a bill's quoted
 * discount rate and its true yield genuinely are different numbers, and that
 * gap is why bond desks quote bond-equivalent yield separately. Worth being
 * precise about the direction, because it is load-bearing: the compound price
 * comes out slightly ABOVE the simple-discount proceeds, so buying a note
 * straight back costs a little more than it raised. A small loss, never a gain,
 * which is the only safe direction for a round trip to fail in.
 */
public class ShortTermTBill extends Debt {

    public ShortTermTBill(double faceValue, int months, int monthStarted){
        this.faceValue = faceValue;
        this.remainingMonths = months;
        this.duration = months;
        this.monthStarted = monthStarted;
        this.outstandingPrincipal = faceValue;
        this.type = "NOTE";
    }

    /**
     * The discount for a note of this term, as a fraction of face.
     *
     * Static so the pricing screen and the booking share one definition, and
     * clamped below 1 because a long enough note at a high enough rate would
     * otherwise discount to zero or negative - at which point the face required
     * to raise a dollar is infinite. The market does not lend on those terms;
     * capping the discount means it charges a great deal instead.
     */
    public static double discountFraction(double annualRate, int months) {
        double raw = annualRate * (months / 12.0);
        return Math.max(0, Math.min(raw, .95));
    }

    /** Face value needed to raise a given sum for a given term. */
    public static double faceFor(double cashWanted, double annualRate, int months) {
        return cashWanted / (1 - discountFraction(annualRate, months));
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

    /** Nothing until maturity, then the whole face. */
    @Override
    public double[] remainingCashFlows() {
        if (remainingMonths <= 0) {
            return new double[0];
        }
        double[] flows = new double[remainingMonths];
        flows[remainingMonths - 1] = faceValue;
        return flows;
    }
}
