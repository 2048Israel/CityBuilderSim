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
 * A future stock - savings held abroad, a pension pot - is a field here, a
 * line in redenominate(), a slot in HouseholdBalance's cell save array, and
 * an entry in followThePeople()'s list so it moves with the people. Four
 * places, all of them named, none of them in a screen. The shares in the
 * city's companies were the first to go in that way, the same evening.
 */
public abstract class Household {

    /** The row the retired sum into, after the six tiers. Same index as HouseholdAccounts.RETIRED. */
    public static final int RETIRED_ROW = PayTier.values().length;

    /*
     * THE ROWS OUTSIDE THE FAMILY MATRIX (2026-09-11). Jerus: "a new household
     * structure called unemployed... they will have their own cashflow and
     * stuff" - and the students and the orphans with them. Each is a row of
     * its own after the retired, and each is a subclass of this one, so the
     * city can still sum or change every household at once.
     */
    /** The out of work: on EI, off it, and those who have lost their home. */
    public static final int UNEMPLOYED_ROW = RETIRED_ROW + 1;
    /** Full-time students, living on a grant, their savings and a student loan. */
    public static final int STUDENT_ROW = RETIRED_ROW + 2;
    /** Children no family holds, by band. Jerus: "the orphan section". */
    public static final int ORPHAN_ROW = RETIRED_ROW + 3;
    /** Every row: the six tiers, the retired, and the three above. */
    public static final int ROWS = RETIRED_ROW + 4;

    /**
     * How long a graduate takes to repay a student loan, in months: nine and
     * a half years, the Canada Student Loan standard term (Alberta Student
     * Aid's repayment page; the six-month grace is not modelled). Interest
     * free, as Canada loans have been since April 2023.
     */
    public static final double STUDENT_LOAN_MONTHS = 114;

    /** The family shape, or null for a household that is not one: the unemployed, a student, an orphan. */
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
     * What one of these households owes the treasury on student loans. A
     * STOCK on the header's terms: borrowed while studying, carried into a
     * family when they graduate, repaid out of wages there. Interest free.
     */
    double studentDebt;

    /**
     * Shares held in each of the city's companies, per household of the cell,
     * indexed as Equity.COMPANIES. A STOCK like the savings - carried, saved,
     * pooled when households change shape - and the first one added on the
     * terms the class header sets out. Jerus: "each household needs a number
     * of shares owned per company."
     *
     * A count, not money: it does not move in a reform, and what it is worth
     * is the company's book divided by its shares, which Equity keeps.
     */
    final double[] shares = new double[Equity.COMPANIES.length];

    /** What the shares paid this month, per household. Credited to savings. */
    double dividends;

    /**
     * DOLLARS held abroad by one of these households: the world's paper,
     * bought with savings past the cushion when the world pays more than the
     * bank, sold when the bank pays more or the household needs the money. A
     * STOCK, on the class header's terms - and the second added on them. The
     * same rule the sectors follow (OutwardInvestment), for the same reason:
     * the exchange returns the companies' hoard to the people who own them,
     * and a city whose surplus sits in a bank paying nothing appreciates
     * until its exporters are dead. See HouseholdBalance.investAbroad().
     *
     * In dollars, so a reform does not touch it; what it is worth at home is
     * the dollars at the month's rate.
     */
    double abroad;

    /** This month's, per household, in local money: sent abroad, brought home, and earned there (rolled, not paid home). */
    double sentAbroad, broughtHome, foreignInterest;

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

    /** Shares sold this month to cover the shop, per household, in cash. */
    double sold;

    /** Student loan drawn this month, and repaid, per household. */
    double studentBorrowed, studentRepaid;

    /** Households of this cell that lost their home this month - a count, not money. */
    double evicted;

    /** What one of these households paid of a door's rent this month: 1 alone, a fifth sharing, 0 with no door. */
    double rentShare = 1;

    /** Somewhere a household short of money can sell shares before it borrows. See Exchange. */
    interface Liquidity {
        /** @return cash raised, per household of the cell */
        double sell(Household cell, double needPer);
    }

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

    /**
     * Who in the household the row's INCOME is split by. The grown-ups, for a
     * family or a pensioner; for the out of work, only those still on EI -
     * the EI bill is theirs, and nobody past the twelfth month shares it.
     */
    public double earningWeight() { return grownUps(); }

    /** True for a cell whose households lose their home when they cannot pay for it. */
    public boolean canBeEvicted() { return false; }

    /**
     * The row whose people this cell's people most often ARE, for the money to
     * follow them: its own row, for everybody but the out of work. See
     * HouseholdBalance.followThePeople().
     */
    public int stockGroup() { return row(); }

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
    public double people()     { return households * size(); }

    public double studentDebt()     { return studentDebt; }
    public double studentBorrowed() { return studentBorrowed; }
    public double studentRepaid()   { return studentRepaid; }
    public double totalStudentDebt(){ return studentDebt * households; }
    public double evicted()         { return evicted; }
    public double rentShare()       { return rentShare; }

    /** Per household. */
    public double savings()     { return savings; }
    public double debt()        { return debt; }
    public int    lockout()     { return lockout; }

    /** Shares held in this company, per household. */
    public double shares(int company) { return shares[company]; }
    public double totalShares(int company) { return shares[company] * households; }

    /** This month's dividends, per household. */
    public double dividends()   { return dividends; }

    /** Dollars held abroad, per household. */
    public double abroad()      { return abroad; }
    /** ...worth this much at home, per household, at a rate. */
    public double abroadValue(double localPerUsd) { return abroad * localPerUsd; }
    public double sentAbroad()  { return sentAbroad; }
    public double broughtHome() { return broughtHome; }
    public double foreignInterest() { return foreignInterest; }

    /** Shares sold this month to cover the shop, per household, in cash. */
    public double sold()        { return sold; }
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
    public double totalAbroad()   { return abroad * households; }
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
        settle(disposablePer, rentPerHome, feesPer, spentPer, foodPricePerHead, riskFreeAnnual, null, 0);
    }

    /**
     * @param market      where shares can be sold before credit is drawn, or null for nowhere
     * @param localPerUsd the month's exchange rate, for the paper held abroad; zero when there is no world
     */
    void settle(double disposablePer, double rentPerHome, double feesPer,
                double spentPer, double foodPricePerHead, double riskFreeAnnual,
                Liquidity market, double localPerUsd) {

        disposable = disposablePer;

        /* ---------------- what the lender charges this one ---------------- */
        double owedMonths = disposablePer > 0 ? debt / disposablePer : 0;
        rate = Math.min(HouseholdBalance.MAX_RATE,
                Math.max(0, riskFreeAnnual) + HouseholdBalance.BASE_SPREAD
                        + HouseholdBalance.RISK_SLOPE * owedMonths);
        interest = debt * rate / 12;

        /* ---------------- the bills, in order ---------------- */
        studentRepaid = Math.min(studentDebt, studentRepayment());
        afterFixed = disposablePer - rentPerHome - feesPer - interest - studentRepaid;
        subsistence = size() * foodPricePerHead;

        /* ---------------- settle what they actually spent ---------------- */
        double gap = spentPer - afterFixed;

        drawn = 0; borrowed = 0; repaid = 0; banked = 0; unfunded = 0; sold = 0;
        sentAbroad = 0; broughtHome = 0; foreignInterest = 0;
        studentBorrowed = 0; evicted = 0;
        studentDebt -= studentRepaid;
        if (gap > 0) {
            drawn = Math.min(gap, Math.max(0, savings));
            savings -= drawn;
            double still = gap - drawn;

            /*
             * THEN THE PAPER ABROAD, which is liquid: sold at the month's rate
             * for exactly what is still short. A financial inflow, declared
             * through HouseholdBalance.getBroughtHome().
             */
            if (still > 0 && abroad > 0 && localPerUsd > 0) {
                double home = Math.min(still, abroad * localPerUsd);
                abroad -= home / localPerUsd;
                if (abroad < 1e-15) abroad = 0;
                broughtHome = home;
                still -= home;
            }

            /*
             * THEN THE SHARES, since the exchange (2026-09-10, night): savings,
             * then shares, then credit, then going without. Sold at the bid
             * to the bank's desk for exactly what is still short, or for
             * everything held if that is less.
             */
            if (still > 0 && market != null) {
                sold = market.sell(this, still);
                still -= sold;
            }

            /*
             * THE LIMIT BINDS HERE TOO, NOT ONLY IN THE PLAN. What is refused
             * is simply not funded: not a debt, not a loss to anybody - the
             * household went without, which is what next month's plan already
             * says and what the hunger measure already reads.
             */
            unfunded = still - fundShortfall(still, disposablePer);
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
        double spendable = Math.max(0, afterFixed) + savings + planningRoom();
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
        studentRepaid = Math.min(studentDebt, studentRepayment());
        afterFixed = disposablePer - rentPerHome - feesPer - interest - studentRepaid;
        subsistence = size() * foodPricePerHead;
    }

    /**
     * What is still short after savings, the paper abroad and the shares:
     * the revolving credit line, up to its ceiling. A student's is a student
     * loan instead, which never runs out - see StudentHousehold.
     *
     * @return what was funded
     */
    protected double fundShortfall(double still, double disposablePer) {
        double room = creditRoom(disposablePer);
        borrowed = Math.min(still, room);
        debt += borrowed;
        return borrowed;
    }

    /** What the plan may count on borrowing. The credit room, for everyone but a student. */
    protected double planningRoom() { return creditRoom(disposable); }

    /** The month's student-loan repayment, per household. Nothing, except in a working family. */
    protected double studentRepayment() { return 0; }

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

        /*
         * A HOUSEHOLD WITH NO INCOME AND A DEBT IS AT ITS CEILING (2026-09-11).
         * Until the out of work had books of their own nobody had no income,
         * so the guard was disposable > 0; somebody whose EI has run out and
         * who still owes the bank from the job they had can never borrow again
         * and never repay, and without this they would owe it for ever.
         */
        boolean atTheCeiling = disposable > 0
                ? debt >= HouseholdBalance.BANKRUPT_AT_MONTHS * disposable
                : debt > 0;
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
        subsistence = 0; bankrupt = 0; dividends = 0; sold = 0;
        sentAbroad = 0; broughtHome = 0; foreignInterest = 0;
        studentBorrowed = 0; studentRepaid = 0; evicted = 0;
    }

    /** The cell is empty: no position either. */
    void clearAll() {
        savings = 0; debt = 0; lockout = 0; households = 0; abroad = 0; studentDebt = 0;
        java.util.Arrays.fill(shares, 0);
        clearWorking();
    }

    /** Everything in money, in the new unit. Rates, counts, months, SHARES and DOLLARS do not move. */
    void redenominate(double scale) {
        savings *= scale;  debt *= scale;  dividends *= scale;  sold *= scale;
        disposable *= scale;  afterFixed *= scale;  interest *= scale;
        drawn *= scale;  unfunded *= scale;  borrowed *= scale;  repaid *= scale;
        banked *= scale;  want *= scale;  planned *= scale;  subsistence *= scale;
        sentAbroad *= scale;  broughtHome *= scale;  foreignInterest *= scale;
        studentDebt *= scale;  studentBorrowed *= scale;  studentRepaid *= scale;
    }

    @Override
    public String toString() {
        return String.format("%s x%.1f: $%.3fk saved, $%.3fk owed", label(), households, savings, debt);
    }
}
