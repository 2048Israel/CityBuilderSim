package ham.citybuildersim;

/**
 * Base class for private-sector borrowing.
 *
 * Deliberately a separate hierarchy from {@link Debt} rather than a subclass of
 * it, because the two behave differently in one way that matters:
 *
 *   Debt.processMonth(Game) charges interest straight to the CITY's cash via
 *   game.InterestExpense(). A business loan must not do that. Its interest is an
 *   operating expense on that sector's income statement, and the sector's cash
 *   already moves by its net income - so charging cash here as well would take
 *   the money twice.
 *
 * So a BusinessDebt only ever advances its own clock. The manager reports the
 * interest, the income statement expenses it, and cash follows from net income.
 * The single exception is principal at maturity, which is a cash movement and
 * not an expense; the manager hands that back to the sector to settle.
 */
public abstract class BusinessDebt {

    /** Which set of books this sits on - see the constants on BusinessDebtManager. */
    protected String sector;

    protected double faceValue;
    protected double outstandingPrincipal;
    protected int duration;
    protected int remainingMonths;
    protected int monthStarted;

    /** Fixed at issue. A term loan carries the rate it was written at. */
    protected double annualRate;

    protected String type;

    /** Advances the clock by one month. Must not move any cash. */
    public abstract void processMonth();

    public abstract double getMonthlyInterestExpense();

    public abstract double getOutstandingPrincipal();

    public abstract int getMaturityMonth();

    public abstract boolean isMatured();

    public abstract String getType();

    public String getSector() {
        return sector;
    }

    public double getAnnualRate() {
        return annualRate;
    }

    public double getFaceValue() {
        return faceValue;
    }

    public int getRemainingMonths() {
        return remainingMonths;
    }
}
