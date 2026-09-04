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
 * AND NOW, SEVEN SETS OF BOOKS
 *
 * The note that used to sit here said splitting residents into income groups was
 * "the obvious next step" and "a long way off". It is here: the city total, plus
 * one statement per pay tier and one for the retired, who have no tier because
 * they have no earner.
 *
 * NOTHING IN THE SPLIT IS ESTIMATED, which is the only reason it is worth having.
 * Wages come from the job mix by tier. The tax is the SAME banded calculation the
 * city collects, split rather than re-derived. Rent is charged per resident at a
 * uniform price, and shopping is driven by headcount, so allocating both by the
 * people living in each tier's households is exact arithmetic rather than an
 * apportionment.
 *
 * That last point is also the finding. Income across the tiers varies about
 * tenfold; rent and shopping per head do not vary at all, because nothing in the
 * model lets what a household earns affect what it spends. So the poorest tier
 * runs a deficit and the richest banks almost everything, and both are artefacts
 * of a missing budget constraint rather than results. The screen says so.
 */
public class HouseholdAccounts {

    /* --------------------------- this month --------------------------- */
    private double wages;
    private double wageTax;
    private double rent;
    private double shopping;

    /**
     * Pension contributions off the workers, and pensions in to the retired.
     *
     * Two flows in opposite directions between the same two parties, and they
     * must NOT be netted into one line. A worker seeing 5.95% gone from their
     * pay and a pensioner seeing money arrive are different facts about
     * different households, and the whole point of splitting these books by tier
     * was to stop facts about one group being averaged into another.
     */
    private double contributions;
    private double pensions;

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
        update(wages, wageTax, rent, shopping, 0, 0, population, workforce, jobsFilled);
    }

    public void update(double wages, double wageTax, double rent, double shopping,
                       double contributions, double pensions,
                       int population, int workforce, int jobsFilled) {

        assign(wages, wageTax, rent, shopping, contributions, pensions,
                population, workforce, jobsFilled);
        cumulativeSaving += getNetSaving();
    }

    /**
     * The same figures, WITHOUT adding a month to the running total.
     *
     * For the load path. rebuildSimulationState() has to repopulate this
     * statement or every row on the People screen reads $0 after a reload - but
     * cumulativeSaving is history, it was restored from the save, and calling
     * update() would book the same month onto it a second time.
     *
     * Splitting the two is the honest fix. A month's statement is derived state
     * and can be rebuilt; a running total is a fact about every month that came
     * before and cannot.
     */
    public void refresh(double wages, double wageTax, double rent, double shopping,
                        double contributions, double pensions,
                        int population, int workforce, int jobsFilled) {

        assign(wages, wageTax, rent, shopping, contributions, pensions,
                population, workforce, jobsFilled);
    }

    private void assign(double wages, double wageTax, double rent, double shopping,
                        double contributions, double pensions,
                        int population, int workforce, int jobsFilled) {

        this.wages = wages;
        this.wageTax = wageTax;
        this.rent = rent;
        this.shopping = shopping;
        this.contributions = contributions;
        this.pensions = pensions;

        this.population = population;
        this.workforce = workforce;
        this.jobsFilled = jobsFilled;
    }

    /* ----------------------------- the statement ----------------------------- */

    public double getWages()         { return wages; }
    public double getWageTax()       { return wageTax; }
    public double getRent()          { return rent; }
    public double getShopping()      { return shopping; }
    public double getContributions() { return contributions; }
    public double getPensions()      { return pensions; }

    /**
     * What the people actually have to spend after the city has taken its share.
     *
     * Pensions are income and contributions are a deduction, so both belong
     * here rather than beside the rent. A pensioner's whole income is on this
     * line; a worker's is this line minus a seventeenth.
     */
    public double getDisposableIncome() {
        return wages - wageTax - contributions + pensions;
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
        return (population > 0) ? (wages + pensions) / population : 0;
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

    /* =====================================================================
       THE SAME STATEMENT, PER PAY TIER

       Seven rows: the six tiers, plus the retired at the end. Held as parallel
       arrays rather than seven objects because every row is the same four
       figures and the screen walks them in order; a class per row would be
       ceremony around an array index.

       The retired row is not an afterthought. Pensioners have NO income in this
       model - there is no pension, no savings drawdown, nothing - so their row
       is pure outgoing, and their deficit is the largest single thing on the
       screen. That is a real gap in the game, and putting it in the same table
       as everyone else is how it stops being invisible.
       ===================================================================== */

    /** Index of the retired row, which sits after the six tiers. */
    public static final int RETIRED = PayTier.values().length;
    private static final int ROWS = PayTier.values().length + 1;

    private final double[] rowWages     = new double[ROWS];
    private final double[] rowTax       = new double[ROWS];
    private final double[] rowRent      = new double[ROWS];
    private final double[] rowShopping  = new double[ROWS];
    private final double[] rowPeople    = new double[ROWS];
    private final double[] rowHouseholds = new double[ROWS];
    private final double[] rowContributions = new double[ROWS];
    private final double[] rowPensions      = new double[ROWS];

    /**
     * Splits the month across the tiers.
     *
     * Called straight after update(), with the totals it was given. Rent and
     * shopping are shared out BY PEOPLE rather than by household or by income,
     * because that is literally how the model charges them - rent is
     * `residents * rentPrice` and retail demand is a headcount - so this is the
     * same arithmetic read backwards, not an allocation rule invented here.
     *
     * @param wagesPerTier what each tier earned, staffed
     * @param taxPerTier   the banded wage tax on it, split not re-derived
     * @param peoplePerRow residents in each row's households, retired last
     * @param housePerRow  households in each row, retired last
     */
    public void updateByTier(double[] wagesPerTier, double[] taxPerTier,
                             double[] peoplePerRow, double[] housePerRow) {

        java.util.Arrays.fill(rowWages, 0);
        java.util.Arrays.fill(rowTax, 0);
        java.util.Arrays.fill(rowRent, 0);
        java.util.Arrays.fill(rowShopping, 0);
        java.util.Arrays.fill(rowPeople, 0);
        java.util.Arrays.fill(rowHouseholds, 0);
        java.util.Arrays.fill(rowContributions, 0);
        java.util.Arrays.fill(rowPensions, 0);

        if (peoplePerRow == null || peoplePerRow.length != ROWS
                || housePerRow == null || housePerRow.length != ROWS) {
            return;   // refused whole, per the standing rule on state arrays
        }

        double heads = 0;
        for (double n : peoplePerRow) heads += n;

        for (int r = 0; r < ROWS; r++) {
            rowPeople[r] = peoplePerRow[r];
            rowHouseholds[r] = housePerRow[r];

            if (r < PayTier.values().length) {
                if (wagesPerTier != null && r < wagesPerTier.length) rowWages[r] = wagesPerTier[r];
                if (taxPerTier != null && r < taxPerTier.length)     rowTax[r]   = taxPerTier[r];
            }

            double share = heads > 0 ? peoplePerRow[r] / heads : 0;
            rowRent[r] = rent * share;
            rowShopping[r] = shopping * share;
        }

        /*
         * Contributions follow WAGES, not people - it is a slice off a payslip,
         * so a tier that earns nothing contributes nothing. The pension goes
         * entirely to the retired row, which is the whole point of it: that row
         * read "earned $0" before this existed.
         */
        double totalWages = 0;
        for (double w : rowWages) totalWages += w;
        for (int r = 0; r < ROWS; r++) {
            rowContributions[r] = totalWages > 0
                    ? contributions * (rowWages[r] / totalWages) : 0;
        }
        rowPensions[RETIRED] = pensions;
    }

    public double getRowWages(int row)      { return rowWages[row]; }
    public double getRowTax(int row)        { return rowTax[row]; }
    public double getRowRent(int row)       { return rowRent[row]; }
    public double getRowShopping(int row)   { return rowShopping[row]; }
    public double getRowPeople(int row)     { return rowPeople[row]; }
    public double getRowHouseholds(int row) { return rowHouseholds[row]; }
    public int getRowCount()                { return ROWS; }

    public double getRowContributions(int row) { return rowContributions[row]; }
    public double getRowPensions(int row)      { return rowPensions[row]; }

    public double getRowDisposable(int row) {
        return rowWages[row] - rowTax[row] - rowContributions[row] + rowPensions[row];
    }

    public double getRowSpending(int row) {
        return rowRent[row] + rowShopping[row];
    }

    public double getRowSaving(int row) {
        return getRowDisposable(row) - getRowSpending(row);
    }

    /** Saving as a share of take-home. Zero income has no rate, only a deficit. */
    public double getRowSavingRate(int row) {
        double disposable = getRowDisposable(row);
        return disposable > 0 ? getRowSaving(row) / disposable : 0;
    }

    public String getRowLabel(int row) {
        return row == RETIRED ? "Retired (no earner)" : PayTier.values()[row].getLabel();
    }

    public void reset() {
        wages = 0;
        wageTax = 0;
        rent = 0;
        shopping = 0;
        contributions = 0;
        pensions = 0;
        population = 0;
        workforce = 0;
        jobsFilled = 0;
        cumulativeSaving = 0;
        java.util.Arrays.fill(rowWages, 0);
        java.util.Arrays.fill(rowTax, 0);
        java.util.Arrays.fill(rowRent, 0);
        java.util.Arrays.fill(rowShopping, 0);
        java.util.Arrays.fill(rowPeople, 0);
        java.util.Arrays.fill(rowHouseholds, 0);
        java.util.Arrays.fill(rowContributions, 0);
        java.util.Arrays.fill(rowPensions, 0);
    }
}
