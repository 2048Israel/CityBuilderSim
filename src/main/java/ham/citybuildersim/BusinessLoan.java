package ham.citybuildersim;

/**
 * A fixed-term business loan: interest-only each month, principal repaid in full
 * at maturity.
 *
 * Bullet repayment rather than amortising, for a reason specific to how these get
 * issued. They are underwritten to cover a sector that could not pay its bills,
 * so a loan that demanded principal back every month would immediately push that
 * sector negative again and trigger another loan, and another - a spiral driven
 * by the fix rather than by the business. Interest-only keeps the monthly burden
 * to what the sector can plausibly carry, and the balloon at the end simply gets
 * refinanced by a new loan, which is what a distressed borrower actually does.
 *
 * It also matches the government bonds, which are bullet too.
 *
 * The rate is fixed at issue. BusinessDebtManager.getRate() moves with the
 * sector's leverage, but that prices NEW borrowing - an existing loan keeps the
 * rate it was written at, so a sector that borrowed while healthy stays cheap
 * even after its credit deteriorates.
 */
public class BusinessLoan extends BusinessDebt {

    private double monthlyRate;

    public BusinessLoan(String sector, double faceValue, int months, int monthStarted, double annualRate) {
        this.sector = sector;
        this.faceValue = faceValue;
        this.outstandingPrincipal = faceValue;
        this.duration = months;
        this.remainingMonths = months;
        this.monthStarted = monthStarted;
        this.annualRate = annualRate;
        this.monthlyRate = annualRate / 12;
        this.type = "BUSINESS-LOAN";
    }

    /**
     * No cash movement here on purpose - see the note on BusinessDebt. Interest
     * reaches cash through the sector's income statement; principal reaches it
     * through BusinessDebtManager, which collects it at maturity.
     */
    @Override
    public void processMonth() {
        remainingMonths--;
    }

    @Override
    public double getMonthlyInterestExpense() {
        return faceValue * monthlyRate;
    }

    @Override
    public double getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    @Override
    public int getMaturityMonth() {
        return monthStarted + duration;
    }

    @Override
    public boolean isMatured() {
        return remainingMonths <= 0;
    }

    @Override
    public String getType() {
        return type;
    }
}
