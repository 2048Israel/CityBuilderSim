package ham.citybuildersim;

/**
 * The city's residents, treated as one household.
 *
 * Every other participant in this economy had books before this: shops, mills,
 * landlords, builders, the utilities, the city itself. The people did not, even
 * though they are the largest single flow in the game - they earn every wage
 * paid and they are the customer at the other end of every rent cheque and
 * every till.
 *
 * WHERE THE NUMBERS COME FROM
 *
 * Nothing here is estimated or re-derived. Wages are the figure PopulationManager
 * already computes for filled jobs; the wage tax is what the city already
 * collects; rent and shopping are the two halves of consumption that
 * NationalAccounts already measures, because the money households spend IS
 * consumption. Building this account is a matter of putting existing figures on
 * the other side of the ledger from the businesses that receive them.
 *
 * WHAT IT IS FOR
 *
 * It answers a question nothing else in the game could: can the people afford
 * to live here? Retail spending is currently driven by how many residents there
 * are rather than by what they earn - there is no budget constraint anywhere in
 * the model - so households CAN be made to spend more than they take home, and
 * that shortfall would be money appearing from nowhere. Now it is a visible
 * negative line rather than an invisible one.
 *
 * ONE ENTITY, FOR NOW
 *
 * A single household, deliberately. Splitting residents into income groups is
 * the obvious next step and would make this the natural place to do it - the
 * statement is the same shape per group - but that is a long way off, and one
 * set of books that is correct beats five that are guesses.
 */
public class HouseholdAccounts {

    /* --------------------------- this month --------------------------- */
    private double wages;
    private double wageTax;
    private double rent;
    private double shopping;

    private int population;
    private int workforce;
    private int jobsFilled;

    /**
     * Everything households have not spent, accumulated.
     *
     * Not a pot anyone can draw on - nothing in the game lets residents spend
     * savings, and it must not, because that money has no counterparty holding
     * it. It is a running total of the gap between what the people earned and
     * what they paid out, which is the honest way to show whether the city's
     * wages have kept up with its prices over time.
     */
    private double cumulativeSaving;

    /** Feed it the month. Every argument is a figure someone else already had. */
    public void update(double wages, double wageTax, double rent, double shopping,
                       int population, int workforce, int jobsFilled) {

        this.wages = wages;
        this.wageTax = wageTax;
        this.rent = rent;
        this.shopping = shopping;

        this.population = population;
        this.workforce = workforce;
        this.jobsFilled = jobsFilled;

        cumulativeSaving += getNetSaving();
    }

    /* ----------------------------- the statement ----------------------------- */

    public double getWages()    { return wages; }
    public double getWageTax()  { return wageTax; }
    public double getRent()     { return rent; }
    public double getShopping() { return shopping; }

    /** What the people actually have to spend after the city has taken its share. */
    public double getDisposableIncome() {
        return wages - wageTax;
    }

    public double getSpending() {
        return rent + shopping;
    }

    /** Income less tax less everything paid out. Negative means living beyond it. */
    public double getNetSaving() {
        return getDisposableIncome() - getSpending();
    }

    public double getCumulativeSaving() {
        return cumulativeSaving;
    }

    /** For the load path. The running total is history and has to survive a save. */
    public void setCumulativeSaving(double value) {
        this.cumulativeSaving = value;
    }

    /**
     * Saving as a share of take-home pay.
     *
     * The single number worth watching. Real household saving rates sit around
     * 5-15%; a city pushed to 0 is one where the rent and the shops have taken
     * everything, and a negative one is not sustainable in any economy.
     */
    public double getSavingRate() {
        double disposable = getDisposableIncome();
        return (disposable > 0) ? getNetSaving() / disposable : 0;
    }

    /** Rent as a share of take-home. The affordability figure everyone knows. */
    public double getRentBurden() {
        double disposable = getDisposableIncome();
        return (disposable > 0) ? rent / disposable : 0;
    }

    public double getEffectiveTaxRate() {
        return (wages > 0) ? wageTax / wages : 0;
    }

    /* ------------------------------- per head ------------------------------- */

    public double getIncomePerResident() {
        return (population > 0) ? wages / population : 0;
    }

    public double getSpendingPerResident() {
        return (population > 0) ? getSpending() / population : 0;
    }

    /** What a filled job pays on average. Not the same as income per resident. */
    public double getAverageWage() {
        return (jobsFilled > 0) ? wages / jobsFilled : 0;
    }

    /** How many people each working resident is carrying, themselves included. */
    public double getDependencyRatio() {
        return (workforce > 0) ? population / (double) workforce : 0;
    }

    public int getPopulation() { return population; }
    public int getWorkforce()  { return workforce; }
    public int getJobsFilled() { return jobsFilled; }

    /** True when the people are being made to spend more than they earn. */
    public boolean isLivingBeyondIncome() {
        return getNetSaving() < 0;
    }

    public void reset() {
        wages = 0;
        wageTax = 0;
        rent = 0;
        shopping = 0;
        population = 0;
        workforce = 0;
        jobsFilled = 0;
        cumulativeSaving = 0;
    }
}
