package ham.citybuildersim;

/**
 * Every household of one shape at one pay tier, as one ledger.
 *
 * ==================== WHY A CLASS AND NOT A ROW ====================
 *
 * Jerus, 2026-09-10: "I need it per household and pay tier type... create an
 * object, being the basic, and then each extends... that way it's a lot easier
 * to sum everything up, or modify across all."
 *
 * Before this the households' money was seven rows - one per pay tier and one
 * for the retired - so an unskilled single adult and an unskilled large family
 * drew on the same savings and owed the same debt, and the screen admitted it by
 * giving every shape in a tier the tier's position. The two households whose
 * money differs most were the same number.
 *
 * This is one CELL of the family matrix with its own books: how many households
 * it holds, what each of them has saved and owes, whether the bank has stopped
 * lending to them, and what last month did to them. Sixty-eight of them - eleven
 * working shapes across six tiers, and two retired shapes with no tier - and
 * HouseholdBalance is the thing that holds them all, sums them, and strikes them
 * every month.
 *
 * THE BASE CLASS IS THE LEDGER. Everything a household has or owes lives here,
 * so anything that has to be summed across the city, or changed for everybody at
 * once - a new stock, a redenomination, a stock that has to follow people when
 * they change shape - is written once and applies to every cell. The subclasses
 * say only what differs: where the income comes from, and who in the household
 * carries the money.
 *
 * ==================== PER HOUSEHOLD, NOT PER CELL ====================
 *
 * Every money figure here is PER HOUSEHOLD of the cell, in the game's
 * thousands, and the cell's total is that figure times `households`. The reason
 * is the same one HouseholdBalance has always given: a total would be diluted
 * every month the city grew, and a screen would show a city getting poorer for
 * growing. A newcomer arrives with what a household like theirs has.
 *
 * ==================== THE CELL SURVIVES THE REBUILD ====================
 *
 * FamilyModel re-allocates every household from scratch each month - "nobody
 * keeps the family they had last month" - which was the argument for keeping
 * the stocks per tier rather than per cell. It is answered by HouseholdBalance
 * .followThePeople(): when a cell loses households, their money goes into a
 * pool, and the cells that gain draw on it, so a child ageing into a teen moves
 * a family's savings from one cell to the next rather than losing them. Nothing
 * in this class needs to know that; it just carries the position.
 *
 * ==================== ADDING A STOCK ====================
 *
 * A future stock - shares, savings held abroad, a pension pot - is a field
 * here, a line in redenominate(), a slot in HouseholdBalance's cell save array,
 * and a term in followThePeople() so it moves with the people. Four places,
 * all of them named, none of them in a screen.
 */
public abstract class Household {

    /** The row the retired sum into, after the six tiers. Same index as HouseholdAccounts.RETIRED. */
    public static final int RETIRED_ROW = PayTier.values().length;

    protected final FamilyStructure shape;

    /* ------------------------------ the position ------------------------------
     * Per household of the cell, in thousands. STOCKS: carried across months,
     * saved, pooled when households change shape.
     */

    /** What one of these households has in the bank. */
    double savings;

    /** ...and what it owes the bank on its revolving credit. */
    double debt;

    /** Months this cell cannot borrow, after a discharge. A countdown, so a stock. */
    int lockout;

    /**
     * Households this cell was last struck for - the multiplier on every
     * per-household figure, and the count the next month's census is compared
     * against to see who moved.
     */
    double households;

    /* --------------------- last month's working, per household ---------------------
     * FLOWS: recomputed every strike. The four the next month reads against
     * (want, planned, interest, subsistence) are saved, for the reasons
     * HouseholdBalance.toSaveArray() gives; the rest are for the screen.
     */

    /** Take-home this month: wages or pension, after tax and contributions. */
    double disposable;
    double afterFixed;
    double interest;
    double drawn;
    double unfunded;
    double borrowed;
    double repaid;
    double banked;
    double want;
    double planned;
    double rate;
    double subsistence;

    /** Households of this cell discharged this month - a count, not money. */
    double bankrupt;

    protected Household(FamilyStructure shape) {
        this.shape = shape;
    }

    /* ------------------------------ what differs ------------------------------ */

    /** The pay tier, or null for a household with no earner. */
    public abstract PayTier tier();

    /** The row this cell sums into: the tier's index, or RETIRED_ROW. */
    public abstract int row();

    public abstract boolean isRetired();

    /**
     * Who in the household carries the money: the earners, or the pensioners.
     *
     * Two jobs. It is the weight a row's income is split across its cells by -
     * a couple takes home twice what a single adult does at the same tier, a
     * senior couple draws two pensions - and it is the weight the STOCK follows
     * when households change shape, so that five single adults becoming one
     * flatshare bring five wallets with them and a child arriving in a family
     * brings none.
     */
    public abstract int grownUps();

    /* ------------------------------ reading ------------------------------ */

    public FamilyStructure shape() { return shape; }

    /** People in one of these households. */
    public int size() { return shape.size(); }

    public String label() {
        return isRetired() ? shape.getLabel()
                : shape.getLabel() + ", " + tier().getLabel();
    }

    /** "COUPLE_TEEN:SKILLED", or the shape alone for the retired. The save's key. */
    public String key() {
        return isRetired() ? shape.name() : shape.name() + ":" + tier().name();
    }

    public double households() { return households; }
    public double people()     { return households * shape.size(); }

    /** Per household. */
    public double savings()     { return savings; }
    public double debt()        { return debt; }
    public int    lockout()     { return lockout; }
    public boolean isLockedOut(){ return lockout > 0; }
    public double disposable()  { return disposable; }
    public double afterFixed()  { return afterFixed; }
    public double interest()    { return interest; }
    public double drawn()       { return drawn; }
    public double unfunded()    { return unfunded; }
    public double borrowed()    { return borrowed; }
    public double repaid()      { return repaid; }
    public double banked()      { return banked; }
    public double want()        { return want; }
    public double planned()     { return planned; }
    public double rate()        { return rate; }
    public double subsistence() { return subsistence; }
    public double bankrupt()    { return bankrupt; }

    /** True when the bank has stopped lending to this cell - ceiling or lockout. */
    public boolean isCutOff()    { return unfunded > 0; }

    /** True when this cell is buying less than it wants. */
    public boolean isGoingShort() { return want > 0 && planned < want - 1e-9; }

    /** The cell's totals: the per-household figure times the households. */
    public double totalSavings()  { return savings * households; }
    public double totalDebt()     { return debt * households; }
    public double totalInterest() { return interest * households; }
    public double totalBorrowed() { return borrowed * households; }
    public double totalRepaid()   { return repaid * households; }
    public double totalPlanned()  { return planned * households; }
    public double totalWant()     { return want * households; }

    /**
     * Net worth of one of these households: what it has less what it owes.
     * The first figure a future stock joins.
     */
    public double netWorth() { return savings - debt; }

    /* ------------------------------ the month ------------------------------
     * The waterfall, for ONE household of the cell. HouseholdBalance decides
     * what each cell is handed; this is what the household does with it.
     */

    /**
     * Settles a month: the bills in order, the shop against what was actually
     * spent, and savings, then credit, then going without.
     *
     * @param disposablePer   take-home per household
     * @param rentPerHome     what one let home pays
     * @param feesPer         healthcare and tuition per household
     * @param spentPer        what one of these households actually spent in the shops
     * @param foodPricePerHead one basket
     * @param riskFreeAnnual  the rate the lender prices off
     */
    void settle(double disposablePer, double rentPerHome, double feesPer,
                double spentPer, double foodPricePerHead, double riskFreeAnnual) {

        disposable = disposablePer;

        /* ---------------- what the lender charges this one ---------------- */
        double owedMonths = disposablePer > 0 ? debt / disposablePer : 0;
        rate = Math.min(HouseholdBalance.MAX_RATE,
                Math.max(0, riskFreeAnnual) + HouseholdBalance.BASE_SPREAD
                        + HouseholdBalance.RISK_SLOPE * owedMonths);
        interest = debt * rate / 12;

        /* ---------------- the bills, in order ---------------- */
        afterFixed = disposablePer - rentPerHome - feesPer - interest;
        subsistence = shape.size() * foodPricePerHead;

        /* ---------------- settle what they actually spent ---------------- */
        double gap = spentPer - afterFixed;

        drawn = 0; borrowed = 0; repaid = 0; banked = 0; unfunded = 0;
        if (gap > 0) {
            drawn = Math.min(gap, Math.max(0, savings));
            savings -= drawn;

            /*
             * THE LIMIT BINDS HERE TOO, NOT ONLY IN THE PLAN. What is refused
             * is simply not funded: not a debt, not a loss to anybody - the
             * household went without, which is what next month's plan already
             * says and what the hunger measure already reads.
             */
            double room = creditRoom(disposablePer);
            borrowed = Math.min(gap - drawn, room);
            unfunded = (gap - drawn) - borrowed;
            debt += borrowed;
        } else {
            double surplus = -gap;
            repaid = Math.min(surplus, debt);
            debt -= repaid;
            banked = surplus - repaid;
            savings += banked;
        }
    }

    /**
     * Plans the next month, without settling one. What one of these can afford.
     *
     * @return the plan per household
     */
    double plan() {
        want = subsistence
                + HouseholdBalance.MARGINAL_PROPENSITY * Math.max(0, afterFixed - subsistence);
        double spendable = Math.max(0, afterFixed) + savings + creditRoom(disposable);
        planned = Math.min(want, spendable);
        return planned;
    }

    /** Re-strikes the fixed part of the month for the plan alone: the load path. */
    void restrike(double disposablePer, double rentPerHome, double feesPer,
                  double foodPricePerHead, double riskFreeAnnual) {
        disposable = disposablePer;
        double owedMonths = disposablePer > 0 ? debt / disposablePer : 0;
        rate = Math.min(HouseholdBalance.MAX_RATE,
                Math.max(0, riskFreeAnnual) + HouseholdBalance.BASE_SPREAD
                        + HouseholdBalance.RISK_SLOPE * owedMonths);
        interest = debt * rate / 12;
        afterFixed = disposablePer - rentPerHome - feesPer - interest;
        subsistence = shape.size() * foodPricePerHead;
    }

    /** What the bank will still lend one of these: the ceiling less what is owed, or nothing. */
    double creditRoom(double disposablePer) {
        return lockout > 0 ? 0
                : Math.max(0, HouseholdBalance.CREDIT_LIMIT_MONTHS
                        * Math.max(0, disposablePer) - debt);
    }

    /**
     * Whoever cannot carry it any more: a share of the cell discharges.
     *
     * At the ceiling AND short of the shop - reaching the ceiling is not the
     * same as failing; a household at the ceiling that can still service the
     * interest is merely poor.
     *
     * @return debt written off, in total money
     */
    double discharge() {
        bankrupt = 0;
        if (lockout > 0) lockout--;

        boolean atTheCeiling = disposable > 0
                && debt >= HouseholdBalance.BANKRUPT_AT_MONTHS * disposable;
        boolean cannotEat = afterFixed < subsistence;
        if (!(atTheCeiling && cannotEat)) return 0;

        double going = households * HouseholdBalance.BANKRUPT_RATE;
        bankrupt = going;

        // The debt dies with the household's position, not with the
        // household: the per-household figures are averages, so a share of
        // them discharging is that share off the average.
        double writtenOff = debt * going;
        debt    *= (1 - HouseholdBalance.BANKRUPT_RATE);
        savings *= (1 - HouseholdBalance.BANKRUPT_RATE);
        lockout = HouseholdBalance.LOCKOUT_MONTHS;
        return writtenOff;
    }

    /** Nothing to strike: the working is blank, the position stands. */
    void clearWorking() {
        disposable = 0; afterFixed = 0; interest = 0; drawn = 0; unfunded = 0;
        borrowed = 0; repaid = 0; banked = 0; want = 0; planned = 0; rate = 0;
        subsistence = 0; bankrupt = 0;
    }

    /** The cell is empty: no position either. */
    void clearAll() {
        savings = 0; debt = 0; lockout = 0; households = 0;
        clearWorking();
    }

    /** Everything in money, in the new unit. Rates, counts and months do not move. */
    void redenominate(double scale) {
        savings *= scale;  debt *= scale;
        disposable *= scale;  afterFixed *= scale;  interest *= scale;
        drawn *= scale;  unfunded *= scale;  borrowed *= scale;  repaid *= scale;
        banked *= scale;  want *= scale;  planned *= scale;  subsistence *= scale;
    }

    @Override
    public String toString() {
        return String.format("%s x%.1f: $%.3fk saved, $%.3fk owed", label(), households, savings, debt);
    }
}
