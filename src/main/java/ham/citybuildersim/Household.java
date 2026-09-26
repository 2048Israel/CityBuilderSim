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
    /**
     * Adults serving a sentence (2026-09-11). Jerus: "yes that means another
     * population category, only adults can go to jail." Their money is held
     * in this row's ledger while they are inside. See PrisonerHousehold.
     */
    public static final int PRISON_ROW = RETIRED_ROW + 4;
    /** Every row: the six tiers, the retired, and the four above. */
    public static final int ROWS = RETIRED_ROW + 5;
    /** The rows a save from before the prisons carries. */
    public static final int ROWS_BEFORE_PRISON = ROWS - 1;

    /**
     * How long a graduate takes to repay a student loan, in months: nine and
     * a half years, the Canada Student Loan standard term (Alberta Student
     * Aid's repayment page; the six-month grace is not modelled). Interest
     * free by default, as Canada loans have been since April 2023 - and at
     * whatever rate the player sets since 2026-09-21, see THE LOAN'S RATE.
     */
    public static final double STUDENT_LOAN_MONTHS = 114;

    /* =====================================================================
       THE LOAN'S RATE (2026-09-21)

       Jerus: "another slider which is the interest rate for the student
       loans, and idk if real life is like that but have it so that the
       money is withdrawn from the treasury and then later when they pay it
       back it's added back, and you get the interest if there is any."

       The money already went out and came back: a student's shortfall is
       lent by the treasury (StudentHousehold.fundShortfall) and a graduate
       repays a 114th of the balance a month out of wages
       (WorkingHousehold.studentRepayment). What was missing was the
       interest, and it is the Canadian shape: the government carries the
       interest while the student studies, and a GRADUATE is charged it
       during repayment. So a student's balance grows only by what they
       borrow, and a graduate's is charged the month's interest - the
       balance times TaxPolicy.getStudentLoanRate() over twelve - which the
       month's payment covers before a 114th of the balance comes off it.
       Interest is struck and paid in the same month, so the balance itself
       only ever carries principal and declines exactly as it did before the
       rate existed; what the household PAYS is the instalment plus the
       interest, and at a zero rate the arithmetic is bit for bit what it
       was.

       The interest is the treasury's, as its own revenue line
       (NationalAccounts.getStudentLoanInterest()), beside the health and EI
       premiums; the principal keeps arriving through the journal's "Lent to
       students, net of repayments". A prisoner's loan is frozen with the
       rest of their debts: no instalment and no interest while inside -
       PrisonerHousehold says so in its own two overrides. The rate is a
       city-wide policy told to every cell by HouseholdBalance before it
       settles, like the rent share.
       ===================================================================== */

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

    /**
     * Dividends and foreign coupons received since this cell last planned a
     * month. A STOCK between strikes, and saved, which is why it is here and
     * not with the working below.
     *
     * WHAT IT IS FOR. Both of these used to be paid straight into savings and
     * seen by nothing else: creditDividend() did `c.savings += each` and the
     * coupon was rolled abroad. Neither ever touched `disposable`, so neither
     * ever touched afterFixed, so neither ever touched `want` - a household
     * owning half the city ate precisely what its wage bought. Measured over
     * four thousand months, cumulative coupons came to $2,649bn against $584bn
     * of every wage the city ever paid, and none of it was ever spent by
     * anybody. See plan().
     *
     * THE CASH IS ALREADY IN SAVINGS and this does not move it. This is the
     * household KNOWING it has the money, which is the part that was missing.
     */
    double investmentIncome;

    /** Months this cell cannot borrow, after a discharge. A countdown, so a stock. */
    int lockout;

    /**
     * THE MOST THIS CELL MAY OWE THIS MONTH ON THE BANK'S CAPITAL (0.7.8),
     * per household: what it owed when the bank's rule was set at the top of
     * the month, grown by the month's limit - infinite while the bank lends
     * freely. Set by HouseholdBalance.setCapitalRule(); not saved, because it
     * is set again before the next month lends anything, and the plan never
     * reads it (a reloaded city plans exactly as the live one did).
     */
    double capitalCeiling = Double.POSITIVE_INFINITY;

    /**
     * What one of these households owes the treasury on student loans. A
     * STOCK on the header's terms: borrowed while studying, carried into a
     * family when they graduate, repaid out of wages there. Principal only:
     * the interest a graduate is charged is paid the month it is struck, see
     * THE LOAN'S RATE.
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

    /**
     * THE CITY'S OWN PAPER, AT HOME (0.7.1): face held, per household of the
     * cell, in local money - the fourth asset beside the bank balance, the
     * shares and the dollars abroad. Jerus: "yes households should be able to
     * hold." Bought at the settle of an issue when the yield beats what the
     * bank pays savers, sold back to the bank's desk before the shares when
     * the household is short, and a little a month when the spread that
     * brought it in has gone; its coupons and its principal are paid into
     * savings. A STOCK on the class header's terms - carried, saved, and it
     * follows the people. What it is worth is its face at the households'
     * book ratio (HouseholdBalance.getPaperRatio()), one figure a month; the
     * sum over every cell is DebtManager.householdPrincipal() exactly.
     */
    double paper;

    /**
     * CORPORATE BONDS (0.7.12): the face this cell holds, per household of
     * it, in the businesses' bonds, all together - the fifth asset, beside
     * the bank balance, the shares, the dollars abroad and the city's paper.
     * The sum of bondFace, kept with it (recountBonds()).
     *
     * THE CELL'S OWN SINCE ROUND 2 (Jerus: "Each household type trades").
     * Round 1 held the households' bonds as one pool and this was a claim on
     * it; the holding is the cell's now, bond by bond (bondFace), and a
     * coupon, a principal, a default and a sale land on what the cell itself
     * holds. Bought on the order book and at issue out of savings past the
     * cushion when a bond's expected return beats what the bank pays savers
     * (BondMarket, THE PARTICIPANTS); sold in the waterfall after the paper
     * and the dollars abroad and before the shares, when somebody meets the
     * price (settle()). A STOCK on the class header's terms: carried, saved
     * by the cell's name, and it follows the people; a household that leaves
     * holds its bonds from abroad (BondMarket.householdLeft()).
     */
    double bonds;

    /** ...bond by bond: the face per household of the cell, by the bond's id (CorporateBond.id()). Ordered by id, so every walk over it is the same walk. */
    final java.util.TreeMap<Integer, Double> bondFace = new java.util.TreeMap<>();

    /** The face per household this cell holds of one bond. */
    public double bondFace(int id) { Double v = bondFace.get(id); return v == null ? 0 : v; }

    /**
     * ...set, per household, and the total with it; nothing held is not kept.
     * The total is RECOUNTED, not moved by the difference: a load recounts it
     * from the saved holdings (HouseholdBalance.restoreBondsByCell()), and a
     * total kept by differences parts from that sum in its last bits - which
     * the market's weights read, so a reloaded city would not replay.
     */
    void setBondFace(int id, double perHousehold) {
        putBondFace(id, perHousehold);
        recountBonds();
    }

    /** ...without the total, for a caller that sets many and recounts once (HouseholdBalance's coupons, principal and write-downs): the same sum, not the square of the bonds held. */
    void putBondFace(int id, double perHousehold) {
        if (perHousehold > 0) bondFace.put(id, perHousehold); else bondFace.remove(id);
    }

    /** The total, from the bonds themselves: after anything that moved many at once. */
    void recountBonds() {
        double t = 0;
        for (double v : bondFace.values()) t += v;
        bonds = t;
    }

    /**
     * CARS THIS HOUSEHOLD OWNS, per household of the cell, 0 to 1 (2026-09-16).
     *
     * A STOCK LIKE THE OTHERS AND NOT LIKE THEM. It sits beside savings, debt,
     * shares and the dollars abroad because it moves the way they move - it
     * follows the people, it is saved, a household that leaves takes it - and
     * it is the first one that is not money. Two consequences, both deliberate:
     * redenominate() does not touch it (a currency reform does not halve a
     * car), and it is counted rather than valued, so nothing reads it as
     * wealth.
     *
     * WHY THE HOUSEHOLDS AND NOT THE CITY. A city-wide fleet would have been
     * one field and would have answered the road question just as well. It
     * would not have answered the one Jerus actually asked - "income decides
     * who can afford one" - because a city-wide count has no income. Here the
     * question is asked of the cell that has the savings, which is the same
     * cell the share offer is made to and for the same reason.
     *
     * ONE PER HOUSEHOLD IS SATURATION. A second car is a real thing and it is
     * not modelled: the number this feeds is commuter road load, a household
     * that owns two cars does not make two commutes, and the day somebody
     * wants garages and school runs the ceiling is one constant.
     */
    double cars;

    /**
     * What this household would spend on luxuries this month, per household.
     * Within-month working: struck by plan(), spent by LuxuryCounter.shop().
     */
    double luxuryWant;

    /**
     * ...and what it would spend eating out, per household. Same shape:
     * struck by plan(), spent by LuxuryCounter.dine().
     */
    double mealWant;

    /**
     * MEALS this household ate out last month, per household.
     *
     * A POSITION, NOT WORKING, AND THAT IS THE AWKWARD PART. It is written by
     * LuxuryCounter.dine(), which runs in the second half of the month, and read
     * by HouseholdBalance.advanceMonth() in the FIRST half of the next one -
     * where it is added to what the household ate before that is compared
     * against subsistence. So it crosses a month boundary, which means it has
     * to survive a save, and it is cleared after the read rather than in
     * clearWorking().
     *
     * A MONTH LATE, AND HONEST FOR THE SAME REASON THE DIVIDEND IS: the line
     * it joins is already last month's - `ate` is `planned * delivered`, and
     * planned is the plan the shops sold against. Both halves of what a
     * household ate therefore come from the same month, which is the property
     * that actually matters.
     *
     * IN MEALS, NOT IN MONEY. What it is worth as subsistence is the meals
     * times a person-month per meal times what a person-month of food costs,
     * and the price of a meal - which carries the kitchen's margin - has
     * nothing to do with how full anybody is. A reform does not divide it;
     * see redenominate().
     */
    double mealsEaten;

    public double mealsEaten() { return mealsEaten; }

    /* =====================================================================
       WHO CAN AFFORD THE CLINIC (2026-09-19)

       Jerus: "healthcare should be an adjustable price" - and his decision
       for the household that cannot pay it: it goes without care, not
       without food. The tuition trap in Education is the same shape
       (CAN THEY AFFORD IT?), and this deliberately is NOT its linear
       falloff, which would shave coverage off every household at any fee
       and change every city at the default. This is a cliff on the
       household's own solvency:

         A household pays its care bill out of what its plan says it will
         have next month AFTER rent, the other bills and a basket for
         everybody in it - its income, its savings, the city's paper it holds
         (0.7.1), its paper abroad and the credit still open to it, which is
         the waterfall's own order. What fits is paid; what would have to
         come out of the food budget is not, and that share of the
         household's people goes without care.

       So a household that can still cover its bills out of income, savings,
       shares or credit pays the whole fee and is served exactly as it was;
       only a household that has exhausted all of them and would otherwise
       eat less skips care instead, and eats that much more. The share is
       struck against the bill the household WOULD face at full service
       (see HouseholdBalance.setCareBills()) rather than the one it was
       handed, so a household that skipped last month's bill does not read
       an empty bill as a bill it can afford and swing between served and
       starving every other month.

       A POSITION, NOT WORKING, for the same reason mealsEaten is: it is
       struck at the top of the month and read in the middle of it by
       Game.careAffordability(), and again at the next month's top by the
       fee split, so it crosses a save. An old save reads 1: everybody paid,
       which is what that city did.
       ===================================================================== */

    /** Of this household's people, the share who paid for care at the last strike: 1 for all of them. */
    double carePaid = 1;

    /** What one of these households could fund next month: the plan's own figure, kept for the care test. */
    double spendable;

    /** The care bill this household skipped at the last strike, per household - what it ate instead. */
    double careSkipped;

    public double carePaid()    { return carePaid; }
    public double spendable()   { return spendable; }
    public double careSkipped() { return careSkipped; }

    /**
     * Decides how much of its care bill this household pays, after the month
     * is settled and the plan is struck - the rule in the banner above.
     *
     * @param fullBill what one of these households would be billed for care
     *                 at full service, per household
     * @param paidBill what one of them was actually billed this month, per
     *                 household - added back, because the plan's spendable is
     *                 net of it and the question is what is left with no bill
     * @return the share of the bill it can pay, 0 to 1, which is the share of
     *         its people who are served
     */
    double affordCare(double fullBill, double paidBill) {
        careSkipped = 0;
        if (fullBill <= 0) {
            carePaid = 1;
            return 1;
        }
        double room = spendable + Math.max(0, paidBill) - subsistence;
        carePaid = Math.max(0, Math.min(1, room / fullBill));
        careSkipped = (1 - carePaid) * fullBill;
        return carePaid;
    }

    /**
     * ...and the ones this cell sold into the second-hand market this month,
     * in total rather than per household. Within-month working, not a stock:
     * what it is FOR is the statement line that tells a player a family in
     * this cell had to give up the car. See HouseholdBalance.clearUsedCars().
     */
    double carsSold;

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

    /** The city's paper sold this month - to cover the shop or because the spread went - and its coupons and principal received, per household, in cash (0.7.1). */
    double paperSold, paperIncome;

    /** Its bonds sold this month - in the waterfall or at the market's step - and their coupons and principal received, per household, in cash (0.7.12). */
    double bondsSold, bondIncome;

    /** Student loan drawn this month, and repaid (principal), per household. */
    double studentBorrowed, studentRepaid;

    /** Interest charged on the student loan this month, per household: paid with the instalment, and the treasury's. See THE LOAN'S RATE. */
    double studentInterest;

    /** The annual rate the treasury charges a graduate on the loan: the city's one policy, told to every cell by HouseholdBalance before it settles. */
    double studentLoanRate;

    /** Households of this cell that lost their home this month - a count, not money. */
    double evicted;

    /** The bank's account fee this household paid this month, with its other fixed bills (0.7.7); nothing without a home. */
    double accountFee;

    /** The bank's fee on what it borrowed this month, added to what it owes rather than paid (0.7.7): Bank.LOAN_FEE of the credit drawn and the cars financed. */
    double loanFee;

    /** What one of these households paid of a door's rent this month: 1 alone, a fifth sharing, 0 with no door. */
    double rentShare = 1;

    /** Somewhere a household short of money can sell shares before it borrows. See Exchange. */
    interface Liquidity {
        /** @return cash raised, per household of the cell */
        double sell(Household cell, double needPer);

        /**
         * ...and the city's paper, first (0.7.1): sold to the bank's desk at
         * the households' book ratio for exactly what is short, or everything
         * held if that is less. Nothing where there is no desk.
         *
         * @return cash raised, per household of the cell
         */
        default double sellPaper(Household cell, double needPer) { return 0; }

        /**
         * ...and the bonds (0.7.12), after the paper and the dollars abroad:
         * asked on each bond's order book at the price that makes the buyer's
         * yield this household's borrowing rate, and filled only by what rests
         * there at that price or better - nobody is obliged to buy. Nothing
         * where there is no market.
         *
         * @return cash raised now, per household of the cell
         */
        default double sellBonds(Household cell, double needPer) { return 0; }
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

    /**
     * Baskets of food one of these households has to buy: one a head, for
     * everybody but a prisoner, whom the city feeds (the prisons' upkeep).
     * What going short is measured against - see subsistence.
     */
    protected double baskets() { return size(); }

    /**
     * True when the debt is frozen: no interest charged, nothing discharged,
     * nothing borrowed. A prisoner's - Jerus's call on the prisoners' ledger.
     */
    protected boolean debtFrozen() { return false; }

    /**
     * True when the household decides what to do with its savings - shares,
     * paper abroad, an offering. False for a prisoner, whose money is held in
     * the ledger until they come out.
     */
    public boolean canInvest() { return true; }

    /* ------------------------------ reading ------------------------------ */

    public FamilyStructure shape() { return shape; }

    /** People in one of these households, as its SHAPE declares them. */
    public int size() { return shape.size(); }

    /* =====================================================================
       A SHAPE IS NOT ALWAYS THE CENSUS (2026-09-15)

       Every family cell is a shape and a shape is a shopping list of people,
       so size() is the whole answer for them. The cells outside the families
       have no shape at all - they are one adult, and until now that was the
       whole answer for them too.

       It was wrong for the two of them whose adult still lives with somebody.
       A full-time student and an adult out of work go on being parents; the
       children who were in their household go with them, and those children
       eat, get ill, and are billed. FamilyModel works out how many (see the
       note there), and the figure is a fraction per household because a cell
       is an average of thousands - 0.4 children per student household is what
       "two in five students are parents" looks like from here.

       SO THE MOUTHS AND THE EARNERS PART COMPANY, and that is the point of
       having both. people() is who eats: hunger, the shopping plan, and the
       fees that follow heads. grownUps() stays ONE, because the money still
       follows the adult - a student with two children is one wallet, not
       three, and the row's income is split by wallets. Reading the wrong one
       of these is how an elder household came to draw no pension at all four
       days ago; they are named apart so the next person has to choose.
       ===================================================================== */

    /** Dependants who live in one of these households but are not its shape. */
    double dependants;

    /** People in one of these households, counting anybody who came with them. */
    public double headcount() { return size() + dependants; }

    /** ...of whom this many live here without being its shape's own. */
    public double getDependants() { return dependants; }

    public String label() {
        return isRetired() ? shape.getLabel()
                : shape.getLabel() + ", " + tier().getLabel();
    }

    /** "COUPLE_TEEN:SKILLED", or the shape alone for the retired. The save's key. */
    public String key() {
        return isRetired() ? shape.name() : shape.name() + ":" + tier().name();
    }

    public double households() { return households; }
    /** Under HouseholdBalance.EMPTY_CELL households: too few to strike, to hold anything or to be paid. */
    public boolean isEmpty()   { return households < HouseholdBalance.EMPTY_CELL; }
    /** People in the whole cell - its households times what each of them holds. */
    public double people()     { return households * headcount(); }

    public double studentDebt()     { return studentDebt; }
    public double studentBorrowed() { return studentBorrowed; }
    public double studentRepaid()   { return studentRepaid; }
    /** The month's interest on the student loan, per household - paid on top of studentRepaid(). */
    public double studentInterest() { return studentInterest; }
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
    /** The city's paper held, per household, at face (0.7.1). */
    public double paper()       { return paper; }
    /** ...sold this month, and its coupons and principal received, per household, in cash. */
    public double paperSold()   { return paperSold; }
    public double paperIncome() { return paperIncome; }
    /** Its bonds, per household, at face, all together (0.7.12; the cell's own since round 2 - see bonds). */
    public double bonds()       { return bonds; }
    public double bondsSold()   { return bondsSold; }
    public double bondIncome()  { return bondIncome; }
    public double cars()        { return cars; }
    /** Every car this cell's households own between them. */
    public double totalCars()   { return cars * households; }
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
    /** The bank's fee on this month's borrowing, added to the debt (0.7.7). */
    public double loanFee()     { return loanFee; }
    /** The bank's account fee this month (0.7.7). */
    public double accountFee()  { return accountFee; }
    public double repaid()      { return repaid; }
    public double banked()      { return banked; }
    public double want()        { return want; }
    public double planned()     { return planned; }
    public double rate()        { return rate; }
    public double subsistence() { return subsistence; }

    /** What this household would put over a luxury counter this month. */
    public double luxuryWant()  { return luxuryWant; }

    /** Cars this cell sold second-hand this month, in total. */
    public double carsSold()    { return carsSold; }
    public double bankrupt()    { return bankrupt; }

    /** True when the bank has stopped lending to this cell - ceiling or lockout. */
    public boolean isCutOff()    { return unfunded > 0; }

    /**
     * Whether this household planned to spend less than it wanted to.
     *
     * THE TOLERANCE IS RELATIVE, AND IT IS THE TWENTY-FIFTH MONEY CONSTANT
     * (2026-09-17). It was `planned < want - 1e-9`: an absolute amount of
     * money, written as a rounding guard, in a codebase where a currency
     * reform divides every amount by a hundred. A household short by a
     * billionth of a dollar was "not short" in the plain city and short in the
     * reformed one - or the other way about, depending which side of the
     * constant the difference happened to sit.
     *
     * IT HAD BEEN HARMLESS AND STOPPED BEING SO. This flag gates the share
     * offer, where it fires rarely; since 2026-09-17 it also gates buying a
     * car, and cars are bought by nearly every household nearly every month.
     * DenominationCheck went red on nine assertions within a year of the
     * reform - bit-identical the month after it, 9% apart on output a year
     * later, which is the signature of a discrete decision taken differently
     * once rather than of an arithmetic that drifts.
     *
     * A share of what was wanted is the same statement at any denomination, so
     * there is nothing left to seed: "short by a billionth of what it asked
     * for" means the same thing in dollars and in hundredths of them. That is
     * the better answer than the seeded field the other four of this family
     * got, and it is available here because this constant was always a
     * tolerance rather than a threshold.
     */
    public boolean isGoingShort() { return want > 0 && planned < want * (1 - 1e-9); }

    /** The cell's totals: the per-household figure times the households. */
    public double totalSavings()  { return savings * households; }
    public double totalAbroad()   { return abroad * households; }
    public double totalPaper()    { return paper * households; }
    public double totalBonds()    { return bonds * households; }
    public double totalDebt()     { return debt * households; }
    public double totalInterest() { return interest * households; }
    public double totalBorrowed() { return borrowed * households; }
    public double totalAccountFees() { return accountFee * households; }
    public double totalLoanFees() { return loanFee * households; }
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
     * @param feesPer         healthcare and tuition per household, and the
     *                        bank's account fee since 0.7.7
     * @param spentPer        what one of these households actually spent in the shops
     * @param foodPricePerHead one basket
     * @param riskFreeAnnual  the rate the lender's household line starts from -
     *                        Bank.householdRate() since 0.7.7, the city's rate
     *                        before it
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

        /* ---------------- what the lender charges this one ----------------
         * The bank's household rate - its four costs at RISK_HOUSEHOLD (Bank,
         * WHAT A LOAN COSTS) - plus RISK_SLOPE for every month of income
         * owed, capped. Until 0.7.7 it was the city's own rate plus
         * BASE_SPREAD, three points.
         */
        double owedMonths = disposablePer > 0 ? debt / disposablePer : 0;
        rate = debtFrozen() ? 0 : Math.min(HouseholdBalance.MAX_RATE,
                Math.max(0, riskFreeAnnual) + HouseholdBalance.RISK_SLOPE * owedMonths);
        interest = debt * rate / 12;

        /* ---------------- the bills, in order ---------------- */
        // The student loan: the month's interest on the balance, then the
        // instalment - see THE LOAN'S RATE. Both are nothing except in a
        // working family, and the interest is nothing at a zero rate.
        studentInterest = studentInterestDue();
        studentRepaid = Math.min(studentDebt, studentRepayment());
        afterFixed = disposablePer - rentPerHome - feesPer - interest - studentRepaid - studentInterest;
        subsistence = baskets() * foodPricePerHead;

        /* ---------------- settle what they actually spent ---------------- */
        double gap = spentPer - afterFixed;

        drawn = 0; borrowed = 0; repaid = 0; banked = 0; unfunded = 0; sold = 0;
        loanFee = 0;
        paperSold = 0; paperIncome = 0;
        bondsSold = 0; bondIncome = 0;
        sentAbroad = 0; broughtHome = 0; foreignInterest = 0;
        studentBorrowed = 0; evicted = 0;
        // Principal only comes off the balance: the interest was charged on
        // it and paid above, so what is owed never carries it.
        studentDebt -= studentRepaid;
        if (gap > 0) {
            drawn = Math.min(gap, Math.max(0, savings));
            savings -= drawn;
            double still = gap - drawn;

            /*
             * THEN THE CITY'S PAPER (0.7.1), before the dollars abroad and
             * the shares: it is the nearer thing to cash - written in the
             * city's own money, bought back by the bank's desk at the
             * month's market value, no currency to cross and no mark to
             * wait for. Sold for exactly what is still short, or all of it.
             * A crossing out of the pools, declared through the desk's own
             * counter - see HouseholdBalance.setPaperDesk().
             */
            if (still > 0 && paper > 0 && market != null) {
                double raised = market.sellPaper(this, still);
                paperSold += raised;
                still -= raised;
            }

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
             * THEN THE BONDS (0.7.12), after the two that always find a
             * buyer at a known price and before the shares: a bond sells only
             * when somebody on its book meets the price that makes the buyer's
             * yield this household's own borrowing rate - selling is worth it
             * only while it is cheaper than the credit below - and what nobody
             * meets waits there while the household borrows. A crossing out
             * of the pools where the buyer is the bank or a company, declared
             * through the market's own counter.
             */
            if (still > 0 && bonds > 0 && market != null) {
                double raised = market.sellBonds(this, still);
                still -= raised;
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
            /*
             * CLAMPED, LIKE EVERY OTHER WRITE TO A POSITION (2026-09-16).
             *
             * A household cannot owe minus a dollar. This subtraction is exact
             * when the minimum picks `debt` - x minus x is zero for every
             * finite x - so it is not where the dust comes from, and that is
             * precisely why the clamp is here anyway: the invariant is
             * enforced at the boundary rather than at whichever arithmetic is
             * currently suspected. See HouseholdBalance.moveStock() for the
             * 158-month divergence the SIGN of one of these cost.
             */
            debt = Math.max(0, debt - repaid);
            banked = surplus - repaid;
            savings += banked;
        }
    }

    /**
     * Plans the next month, without settling one. What one of these can afford.
     *
     * @return the plan per household
     */
    double plan(double localPerUsd) {
        return plan(localPerUsd, 1, 1);
    }

    /**
     * @param paperRatio what a dollar of the city's paper is worth this month
     *                   (0.7.1): the households' book at the curve over its face
     */
    double plan(double localPerUsd, double paperRatio) {
        return plan(localPerUsd, paperRatio, 1);
    }

    /**
     * @param spendFactor the share of its spending above subsistence this
     *                    household plans (0.7.3): HouseholdBalance
     *                    .spendFactor() on the month's real deposit rate, 1
     *                    at no real return - see AND WHAT IT SPENDS ANSWERS
     *                    THE REAL RATE below
     */
    double plan(double localPerUsd, double paperRatio, double spendFactor) {
        return plan(localPerUsd, paperRatio, spendFactor, 1);
    }

    /**
     * @param bondRatio what a dollar of face of the households' bonds is worth
     *                  this month (0.7.12): every cell's holdings together at
     *                  the bonds' value over their face - in the net worth the
     *                  wealth term reads, and not in what can be spent,
     *                  because a bond sells only when somebody buys it
     */
    double plan(double localPerUsd, double paperRatio, double spendFactor, double bondRatio) {
        /*
         * INVESTMENT INCOME IS INCOME (2026-09-17).
         *
         * Jerus, on being shown the household sector's books: dividends and the
         * coupon go "into income". They already arrived - creditDividend() and
         * investAbroad() both put the cash in savings - and what was missing is
         * that nothing downstream knew. `want` read afterFixed and afterFixed
         * is wages less the fixed bills; a family living off a portfolio
         * planned its month as though it had no portfolio.
         *
         * A MONTH LATE, AND THAT IS HONEST. Both are credited after the strike,
         * so what is spent this month is what was received last month - which
         * is how a dividend actually reaches a household's spending, and how
         * anybody's budget works.
         *
         * AND spendable BELOW NEEDS NO CHANGE, which is the whole reason this
         * is two lines rather than a rework. The money is in `savings` and
         * savings are already in the sum: raising want is the household
         * deciding to draw on what it has. If it draws, settle() sees a gap
         * and takes it out of savings the way it takes out anything else.
         */
        /*
         * ...AND OUT OF WHAT IT HAS. See HouseholdBalance.WEALTH_SPENT_A_MONTH
         * for why a term that reads the STOCK is the only thing that can bound
         * one, and for the two changes that were measured failing to.
         *
         * NET WORTH, WHICH INCLUDES THE MONEY ABROAD. Four fifths of these
         * households' wealth is the world's paper, so a term that only read the
         * bank balance would be reading a fifth of the fortune and would be
         * beaten by the compounding. A household owes the debt against it for
         * the same reason: somebody at their credit ceiling is not wealthy.
         */
        double netWorth = Math.max(0,
                savings + abroad * Math.max(0, localPerUsd)
                        + paper * Math.max(0, paperRatio)
                        + bonds * Math.max(0, bondRatio) - debt);
        /* =================================================================
           AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3)

           Jerus's design (the-central-bank.md section 9): for the rate to
           bite at home, a household's saving must answer the real return.
           The two terms above subsistence are what a household at NO real
           return would spend; `spendFactor` is the share of them it plans
           this month - less when saving pays, more when it costs. The dial
           and the WHY are HouseholdBalance's, under the same title.

           THE WHOLE DISCRETIONARY BUDGET, NOT ONLY THE GROCER'S. The
           counter and the table below spend out of the SURPLUS, which is
           income less `want`; scaling `want` alone would have handed what
           the grocer lost straight to the counter and the table, three
           quarters of it - and since the grocer's discretionary share is
           mostly refused past appetite anyway (see
           LUXURY_SHARE_OF_SURPLUS), a hike would have RAISED what the city
           spends. So the surplus is struck at no real return and scaled by
           the same factor, and so is the wealth term wherever it is spent:
           every dollar above a basket a head answers the one rate, and
           what is left of the month is saved. At a factor of 1 every line
           below is the plan as it was, to the bit.
           ================================================================= */
        double fromIncome = HouseholdBalance.MARGINAL_PROPENSITY
                * Math.max(0, afterFixed + Math.max(0, investmentIncome) - subsistence);
        double fromWealth = HouseholdBalance.WEALTH_SPENT_A_MONTH * netWorth;
        double wantAtNoReturn = subsistence + fromIncome + fromWealth;
        want = subsistence + spendFactor * fromIncome + spendFactor * fromWealth;
        /* =================================================================
           AND THE FORTUNE ALSO ASKS FOR WATCHES (2026-09-17)

           WHERE THIS OUGHT TO LIVE, AND WHY IT DOES NOT YET. `want` above is
           the GROCERY bill, and the wealth term has no business in it: a
           household with a hundred million banked asks its grocer for a
           hundred and twenty-two months of food in one month, which the shops
           rightly refuse because a basket is one person-month and nobody eats
           twice. The term belongs here and nowhere else.

           MOVING IT OUT OF `want` BROKE DenominationCheck, AND NOT FOR ITS OWN
           SAKE. A city and its reformed twin held the same population to the
           PERSON a decade later and 6e-05 apart on every money figure - a
           small persistent arithmetic difference, not a decision taken
           differently. Isolated by putting the term back: green. The mechanism
           is that a huge `want` had made every household permanently
           `isGoingShort()`, which switches OFF the share offer and the car
           purchase entirely; a smaller `want` switches them back ON, and
           something in one of those paths is reform-sensitive in a fixture
           that had never once exercised them.

           That is a real bug and it is NOT this one. So this batch adds
           without subtracting: the grocer keeps a want it cannot fill, which
           is the fiction already written up in `who-is-playing.md`, and the
           fortune asks the counter as well. It costs nothing - grocery
           spending is capped by what the shops SELL, not by what is wanted, so
           no household pays twice - and the day the share-path divergence is
           found, the term comes out of `want` and this comment goes with it.
           ================================================================= */
        // ...struck at no real return and scaled like the rest (0.7.3):
        // see AND WHAT IT SPENDS ANSWERS THE REAL RATE above.
        double surplus = spendFactor
                * Math.max(0, afterFixed + Math.max(0, investmentIncome) - wantAtNoReturn);
        luxuryWant = spendFactor * fromWealth
                + HouseholdBalance.LUXURY_SHARE_OF_SURPLUS * surplus;
        /* =================================================================
           AND WHAT IT WOULD SPEND EATING OUT (2026-09-18)

           OUT OF THE SAME SURPLUS THE COUNTER IS, AND A SMALLER SHARE OF IT.
           A household eats out with money it did not need for anything else,
           which is what the surplus is; it does not eat out with its rent.
           The two shares together are three quarters of it, so a household
           that got everything it asked for still banks a quarter.

           AND A WEALTH TERM, WHICH THE FIRST DRAFT LEFT OUT AND SHOULD NOT
           HAVE. The reasoning for leaving it out was sound - a fortune can
           absorb an unbounded number of watches and cannot absorb an
           unbounded number of dinners, because a person eats ninety meals a
           month whatever they are worth - and the consequence was that the
           sector had NO DEMAND AT ALL.

           WHY: `want` above already carries the wealth term, so in any city
           where a household's fortune is large its grocery plan exceeds its
           income and `surplus` is exactly zero, every month, for everybody. A
           budget written on the surplus alone is a budget of nothing. Measured
           over 4,000 months of the default playtest: 0 meals asked for, 0
           served, the sector written down seventeen times and finishing with
           $3k to its name, while the boutiques beside it - which have the
           wealth term - finished on $2.03bn of assets.

           THE CEILING BELONGS WHERE THE PRICE IS KNOWN, not here. The right
           statement is not "a rich household wants no more dinners", it is "a
           rich household cannot eat more than a certain number of them", and
           that is a bound in MEALS - see HouseholdBalance.MOST_MEALS_EATEN_OUT,
           which applies it at the counter where a meal has a price. Same
           shape as Consumption's APPETITE on the grocery basket, and for the
           same reason.
           ================================================================= */
        mealWant = HouseholdBalance.MEAL_SHARE_OF_SURPLUS * surplus
                + HouseholdBalance.MEAL_SHARE_OF_WEALTH
                        * HouseholdBalance.WEALTH_SPENT_A_MONTH * netWorth * spendFactor;
        /*
         * AND THE PAPER ABROAD IS SPENDABLE, which it always was and the plan
         * never knew. settle()'s waterfall sells it at the month's rate the
         * moment a household is short - savings, then the paper, then the
         * shares, then credit - so leaving it out here made the plan cap itself
         * below what the household could actually pay for, and the wealth term
         * above would have been a wish rather than a purchase.
         */
        spendable = Math.max(0, afterFixed) + savings
                + abroad * Math.max(0, localPerUsd) + paper * Math.max(0, paperRatio)
                + planningRoom();
        planned = Math.min(want, spendable);
        return planned;
    }

    /** Re-strikes the fixed part of the month for the plan alone: the load path. */
    void restrike(double disposablePer, double rentPerHome, double feesPer,
                  double foodPricePerHead, double riskFreeAnnual) {
        disposable = disposablePer;
        double owedMonths = disposablePer > 0 ? debt / disposablePer : 0;
        rate = debtFrozen() ? 0 : Math.min(HouseholdBalance.MAX_RATE,
                Math.max(0, riskFreeAnnual) + HouseholdBalance.RISK_SLOPE * owedMonths);
        interest = debt * rate / 12;
        studentInterest = studentInterestDue();
        studentRepaid = Math.min(studentDebt, studentRepayment());
        afterFixed = disposablePer - rentPerHome - feesPer - interest - studentRepaid - studentInterest;
        subsistence = baskets() * foodPricePerHead;
    }

    /**
     * What is still short after savings, the city's paper, the paper abroad
     * and the shares:
     * the revolving credit line, up to its ceiling. A student's is a student
     * loan instead, which never runs out - see StudentHousehold.
     *
     * @return what was funded
     */
    protected double fundShortfall(double still, double disposablePer) {
        double room = creditRoom(disposablePer);
        /*
         * AND NOBODY BORROWS A NEGATIVE AMOUNT (2026-09-16).
         *
         * `still` is the tail of a subtraction waterfall - what is short after
         * savings, the paper abroad and the shares have each been taken off it
         * - so it can come out a HAIR BELOW ZERO when the last sale overshoots
         * by a rounding. Without this, min(still, room) is that hair, and
         * `debt += borrowed` hands a household a debt of minus 4.6e-19.
         *
         * WHICH IS NOT MONEY, AND SOMETHING READS ITS SIGN. investAbroad() asks
         * `c.debt <= 0` to decide whether a household may hold money abroad, so
         * a negative dust reads debt-free where a zero reads the same and a
         * positive one does not - and once one cell has it, every cell it
         * shares a migration pool with inherits a slice. Seed 2 of the
         * eight-seed ensemble carried it for THREE THOUSAND MONTHS.
         *
         * The three sibling clamps - moveStock's write, the repayment, the
         * restore - were put in first and cut it from 35,199 flagged months to
         * 157. This is the one that makes it none: the others stop the dust
         * spreading, this stops it being created.
         */
        /*
         * ...AND THE BANK'S FEE ON IT, ADDED TO WHAT IS OWED (0.7.7), the way
         * a credit line's fee is: a household borrowing because it is short
         * of cash cannot pay a fee in cash. So the most it can draw is the
         * room less the fee on it, and the debt stays inside the limit.
         */
        /*
         * ...AND NO MORE THAN THE BANK'S CAPITAL LETS IT LEND (0.7.8) - except
         * this month's interest, which keeps an existing borrower going and
         * is outside the rule, as a business's interest reserve is (Bank,
         * WHAT IT LENDS). Under the minimum a family may draw its interest
         * and nothing else; the ceiling above still binds either way.
         */
        if (!Double.isInfinite(capitalCeiling)) {
            room = Math.min(room, capitalRoom() + Math.max(0, interest));
        }
        borrowed = Math.max(0, Math.min(still, room / (1 + Bank.LOAN_FEE)));
        loanFee = borrowed * Bank.LOAN_FEE;
        debt += borrowed + loanFee;
        return borrowed;
    }

    /**
     * What the plan may count on borrowing: the credit room, less the fee
     * drawing it would add (0.7.7), for everyone but a student. The bank's
     * capital rule is not in it (lendableRoom()): the plan is struck at the
     * end of a month for the next, before the bank has said what it will
     * lend - and a reloaded city strikes it again at the load.
     */
    protected double planningRoom() { return creditRoom(disposable) / (1 + Bank.LOAN_FEE); }

    /** What the bank's capital lets this cell draw this month, per household: up to its capitalCeiling. */
    public double capitalRoom() {
        return Double.isInfinite(capitalCeiling) ? Double.POSITIVE_INFINITY
                : Math.max(0, capitalCeiling - debt);
    }

    /** What the bank will actually lend one of these this month: its credit room, and no more than its capital allows (0.7.8). */
    public double lendableRoom(double disposablePer) {
        return Math.min(creditRoom(disposablePer), capitalRoom());
    }

    /** Months of income this household owes - and with a debt and no income at all, past any ceiling, as discharge() reads it. */
    public double monthsOwed() {
        return disposable > 0 ? debt / disposable : debt > 0 ? Double.POSITIVE_INFINITY : 0;
    }

    /** What the bank sets aside against this cell's debt, in total (0.7.8): Bank.householdAllowance() on its months owed - a year's loss on a frozen debt, which nothing discharges while it is frozen. */
    public double lossAllowance() {
        return debtFrozen() ? totalDebt() * Bank.BASE_LOSS_RATE
                : Bank.householdAllowance(totalDebt(), monthsOwed());
    }

    /** This cell's debt, in total, when it is in trouble (Bank.householdWatched()); nothing when it is not. */
    public double debtInTrouble() {
        return !debtFrozen() && debt > 0 && Bank.householdWatched(monthsOwed()) ? totalDebt() : 0;
    }

    /** The month's student-loan instalment (principal), per household. Nothing, except in a working family. */
    protected double studentRepayment() { return 0; }

    /** The month's interest on the student loan at the city's rate, per household. Nothing, except in a working family. */
    protected double studentInterestDue() { return studentInterestAt(studentLoanRate); }

    /**
     * What a month's interest on the loan would be at an annual rate - the
     * same figure studentInterestDue() charges, for a screen previewing a
     * rate the city has not set. Nothing, except in a working family.
     */
    public double studentInterestAt(double annualRate) { return 0; }

    /** What the bank will still lend one of these: the ceiling less what is owed, or nothing. */
    public double creditRoom(double disposablePer) {
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
        if (debtFrozen()) return 0;

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
        subsistence = 0; bankrupt = 0; dividends = 0; sold = 0; carsSold = 0;
        luxuryWant = 0; mealWant = 0; spendable = 0; careSkipped = 0;
        paperSold = 0; paperIncome = 0;
        bondsSold = 0; bondIncome = 0;
        sentAbroad = 0; broughtHome = 0; foreignInterest = 0;
        studentBorrowed = 0; studentRepaid = 0; studentInterest = 0; evicted = 0;
        accountFee = 0; loanFee = 0;
    }

    /** The cell is empty: no position either. */
    void clearAll() {
        savings = 0; debt = 0; lockout = 0; households = 0; abroad = 0; studentDebt = 0;
        paper = 0; bonds = 0; bondFace.clear();
        cars = 0; mealsEaten = 0; carePaid = 1;
        java.util.Arrays.fill(shares, 0);
        clearWorking();
    }

    /** Everything in money, in the new unit. Rates, counts, months, SHARES and DOLLARS do not move. */
    void redenominate(double scale) {
        savings *= scale;  debt *= scale;  dividends *= scale;  sold *= scale;
        // The bank's ceiling on it this month is money too (0.7.8); an
        // infinite one - lending freely - stays infinite.
        capitalCeiling *= scale;
        // The city's paper is in the city's money: it moves (0.7.1).
        paper *= scale;  paperSold *= scale;  paperIncome *= scale;
        // ...and so are its bonds (0.7.12).
        bondsSold *= scale;  bondIncome *= scale;
        bondFace.replaceAll((id, v) -> v * scale);
        recountBonds();
        /*
         * AND THE MONTH'S INVESTMENT INCOME, which is money like the rest of
         * this line and was left off it on the first try (2026-09-17).
         *
         * DenominationCheck went red on fourteen assertions, all of them a
         * tenth of a percent or less apart - the signature of a small
         * persistent difference rather than a threshold taken differently. A
         * reform divides every amount in the city by a hundred; this field was
         * left at its old size, so every household in the reformed city planned
         * its month believing it had a hundred times the dividend it had, and
         * the two cities parted immediately and gently.
         *
         * THE TEST FOR THIS LINE is simply: is the field money? Every new field
         * that is belongs here the day it is written. That is a cheaper rule
         * than the twenty-five constants of the other family, and this is its
         * first sighting.
         */
        investmentIncome *= scale;
        disposable *= scale;  afterFixed *= scale;  interest *= scale;
        drawn *= scale;  unfunded *= scale;  borrowed *= scale;  repaid *= scale;
        accountFee *= scale;  loanFee *= scale;
        banked *= scale;  want *= scale;  planned *= scale;  subsistence *= scale;
        // Money, so it belongs on this line the day it is written. See the
        // note on investmentIncome above.
        luxuryWant *= scale;  mealWant *= scale;
        // ...and the plan's spendable and the care bill skipped are money
        // too; the share who paid (carePaid) is a share and stays.
        spendable *= scale;  careSkipped *= scale;
        /*
         * `mealsEaten` IS NOT HERE, and for `cars`' reason one paragraph down:
         * it is a COUNT OF DINNERS. A reform divides every amount of money in
         * the city by a hundred and it does not divide a plate of food.
         */
        sentAbroad *= scale;  broughtHome *= scale;  foreignInterest *= scale;
        studentDebt *= scale;  studentBorrowed *= scale;  studentRepaid *= scale;
        // Money, so on this line the day it was written; the rate it was
        // struck at is a ratio and stays.
        studentInterest *= scale;
        /*
         * `cars` IS NOT HERE AND THAT IS THE POINT. A reform divides every
         * amount of money by a hundred; it does not divide a car park. The
         * same sentence the note on `abroad` makes about dollars - a stock
         * denominated in something other than this city's money does not move
         * when this city's money does - and cars are denominated in cars.
         */
    }

    @Override
    public String toString() {
        return String.format("%s x%.1f: $%.3fk saved, $%.3fk owed", label(), households, savings, debt);
    }
}
