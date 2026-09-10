package ham.citybuildersim;

/**
 * The city's commercial bank: every loan in it, and every default.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * There were three lenders in this game and none of them was anybody. A sector
 * short of cash borrowed from `BusinessDebtManager`, the city sold bonds to a
 * market, and - since the household balance sheet went in - a family short of
 * the shop borrowed from nobody at all. Money arrived from outside the city and
 * interest disappeared out of it, and when a sector was written down in a
 * restructure the loss was written off against nothing.
 *
 * Jerus: "we need to add a commercial bank, which will be the one everyone,
 * everyone lends money from, the one earning all the interest, and the one
 * getting billed the horrible bankruptcies."
 *
 * So there is one now, and it is a building. What changes is not the arithmetic
 * of any single loan - the rates and schedules are the same - but that the
 * money has somewhere to come from and somewhere to go, and that somewhere can
 * run out and can be bankrupted.
 *
 * ==================== WHAT SETS THE PRICE ====================
 *
 * Two limits, and the tighter one binds - Jerus's answer, and it is the right
 * one because the two fail differently:
 *
 *   DEPOSITS. A bank lends a multiple of what the city has banked with it -
 *   households' savings and the sectors' cash. A city with no savings is a city
 *   with nothing to lend, whatever it has built.
 *
 *   BRANCHES. One building can only carry so much of a loan book. A city with
 *   deep savings and one branch is a city queueing at one counter.
 *
 * Past the tighter of the two the bank does not refuse - it funds the rest
 * abroad and charges for it, which is what a real bank does and what makes the
 * no-bank case fall out of the same formula rather than needing a rule: a city
 * with no branches has no capacity, is therefore infinitely strained, and pays
 * the maximum premium on everything it borrows. Jerus asked for "credit at a
 * punitive rate" with no bank, and this is that, without a special case.
 *
 * ==================== WHAT IS AND IS NOT INSIDE ====================
 *
 * The bank holds cash and it is one of MoneyAudit's pools, so lending to a
 * SECTOR or to the CITY is now an internal transfer that cancels - the money
 * moves from the bank's account to theirs and back with interest, and none of
 * it enters or leaves the city any more. That is a strictly better identity
 * than the one it replaces, where both ends were the outside world.
 *
 * HOUSEHOLDS ARE STILL OUTSIDE IT, as they have always been - wages leave the
 * audited system and rent and shopping come back into it - so lending to a
 * family is a real outflow and its repayments are real inflows. Their savings
 * are counted as deposits for the capacity above WITHOUT being held as the
 * bank's cash, which is the one deliberate fudge here and is stated rather than
 * hidden: the alternative is to make every household a pool, which is a
 * different and much larger model.
 *
 * A write-off costs the bank its BOOK, not its cash. Writing off a loan is the
 * loss of a receivable; the money went out the door months ago.
 */
public class Bank {

    /* ------------------------------- the dials ------------------------------- */

    /**
     * How many times its deposits the bank will lend.
     *
     * Six is a plain, conservative commercial multiple - deep enough that a
     * young city is not gated on its own savings before it has any, and tight
     * enough that a city which lends against nothing pays for it.
     */
    public static final double LEVERAGE = 6;

    /*
     * ================= WHAT ACTUALLY CONSTRAINS A BANK =================
     *
     * Jerus: "i just want it to be what banks do."
     *
     * What used to be here was BOOK_PER_BRANCH - an invented ceiling saying one
     * building could carry $400M of loans and no more. It was legible and it was
     * not a thing. A real bank is held back by two entirely different limits,
     * and both of them are now numbers this class already had lying around:
     *
     *   CAPITAL. It must hold equity against its risk-weighted assets. This is
     *   the binding one, it is the reason a bank that loses money must lend
     *   less, and it is what makes a run of write-offs into a credit crunch
     *   rather than a bad month. Basel says eight percent; so does this.
     *
     *   FUNDING. It can only lend money it has, and what it has is what the city
     *   has banked with it, levered. Branches are what let it reach that money -
     *   which is what branches are actually FOR, rather than being a cap on the
     *   book.
     *
     * The pleasing part is that this makes the bank's own profitability the
     * thing that lets the city borrow more. Nothing had to be invented to get
     * there; the equity was already on the balance sheet.
     */

    /** Equity a bank must hold against its risk-weighted book. */
    public static final double CAPITAL_RATIO = .08;

    /**
     * How much of a city's savings one branch can gather.
     *
     * $253M, which is US deposits over US branches - about $18tn across roughly
     * 71,000 of them. It was $60M, and the four-fold gap was one half of a
     * measured problem: over 1,202 months the bank's PAYROLL came to 180% of
     * every dollar of interest it ever earned. A real bank's whole non-interest
     * expense is 55-65% of revenue. See the note on the Commercial Bank's
     * staffing in BuildingManager for the other half and for the third anchor
     * that reconciles them.
     */
    public static final double DEPOSITS_PER_BRANCH = 250_000;

    /** The same, in today's money - reformed with every other figure. */
    private double depositsPerBranch = DEPOSITS_PER_BRANCH;

    /**
     * What the shareholders put up when a branch opens.
     *
     * A bank cannot start without capital - with none it has no capacity, and
     * with no capacity it can never earn any, which is a bank that can never
     * exist. So opening a branch is an equity injection as well as a building,
     * which is what incorporating a bank actually is. Deliberately more than the
     * premises cost: capital is not the same thing as a building.
     *
     * At the ratio above, one branch's capital supports about $400M of
     * risk-weighted lending - the figure the old invented cap used, arrived at
     * this time rather than asserted.
     *
     * CHECKED AGAINST THE WORLD 2026-09-10 and left alone, which is worth
     * recording because the two constants either side of it both moved: US
     * banks hold about $2.2tn of equity across roughly 71,000 branches, which
     * is $31M each. This says $32M. It was already right.
     */
    public static final double PAID_IN_PER_BRANCH = 32_000;

    /** The same, in today's money. */
    private double paidInPerBranch = PAID_IN_PER_BRANCH;

    /* ---------------------- what a dollar of book WEIGHS ---------------------- */

    /*
     * Jerus: "its not just cash, it can use short term debt as lending
     * instruments no? such as the tbills i issue?"
     *
     * It always could - a city bond has been on this bank's book since the day
     * it was written, because the bank is what buys the paper. What was wrong
     * was that the book was a FLAT SUM: a three-month treasury bill consumed
     * exactly as much of the bank's capacity as a twenty-year mortgage on a
     * steel foundry.
     *
     * That is wrong on both of the axes a banker actually cares about. Sovereign
     * paper does not default, and short paper repays itself before you have
     * finished worrying about it. A bank holds bills INSTEAD of cash, which is
     * the opposite of treating them as risk assets.
     *
     * So capacity is now measured against a WEIGHTED book, while the balance
     * sheet still shows what is actually owed. Two different questions, two
     * different numbers, and the getters say which is which.
     */

    /** The city cannot default on its own paper. */
    public static final double RISK_CITY = .20;

    /** A business can be restructured, and in this game regularly is. */
    public static final double RISK_BUSINESS = 1.00;

    /** ...and a family can be discharged. Revolving, so it never runs off. */
    public static final double RISK_HOUSEHOLD = 1.00;

    /** What a loan repaying tomorrow weighs against one repaying never. */
    public static final double SHORTEST_WEIGHT = .40;

    /** Months of remaining term at which a loan weighs its full amount. */
    public static final double LONG_TERM_MONTHS = 60;

    /**
     * How much of its face a loan with this long left to run counts for.
     *
     * Straight-line rather than anything cleverer, because the shape of this
     * curve is not knowledge anybody has - what matters is that it slopes, and
     * that a bill maturing in a quarter is cheap to hold.
     */
    public static double maturityWeight(double remainingMonths) {
        double travelled = Math.max(0, Math.min(1, remainingMonths / LONG_TERM_MONTHS));
        return SHORTEST_WEIGHT + (1 - SHORTEST_WEIGHT) * travelled;
    }

    /** Where the premium starts biting, as a share of capacity lent out. */
    public static final double EASY_STRAIN = .80;

    /**
     * Where the private sector starts building, which is BEFORE it bites.
     *
     * A branch takes months to put up, so an advisor that waited for the
     * premium would only ever start paying it and then start building - the
     * city would sit at a punitive rate for the whole of the lead time, every
     * time. Ten points of strain ahead of EASY_STRAIN is about one branch's
     * worth of building at the rate a growing city adds loans.
     *
     * Deliberately a strain threshold rather than a forecast off the book's
     * recent growth: the growth is a flow, it would have to be carried in the
     * save, and a dial that is one number the player can be told beats a
     * projection nobody can see.
     */
    public static final double BUILD_AT_STRAIN = .70;

    /** ...and where it is fully bitten. */
    public static final double HARD_STRAIN = 1.50;

    /**
     * The most the strain can add to any borrower's annual rate.
     *
     * Eighteen points is what a city with no bank at all pays, because a city
     * with no branches has no capacity and is therefore past HARD_STRAIN by
     * construction. It is meant to hurt: borrowing without a banking system is
     * borrowing from strangers who do not know you.
     */
    public static final double MAX_STRAIN_PREMIUM = .18;

    /* ------------------------------ the position ------------------------------ */

    private double cash;
    private double branches;
    private double deposits;

    /** The two halves of that, because they are paid separately. */
    private double householdDeposits;
    private double sectorDeposits;

    /** What is lent out, by whom it is owed. The BALANCE SHEET figure. */
    private double sectorBook;
    private double cityBook;
    private double householdBook;

    /** The same three, weighted for risk and remaining term. What CAPACITY sees. */
    private double sectorWeighted;
    private double cityWeighted;
    private double householdWeighted;

    /* --------------------------- the month's working --------------------------- */

    private double interestEarned;
    private double writeOffs;
    private double payroll;
    private double upkeep;
    private double lentToHouseholds;
    private double repaidByHouseholds;
    private double fundingCost;
    private double openingEquity;

    /* ------------------------------- the month ------------------------------- */

    /**
     * Re-reads the city and re-prices credit.
     *
     * @param branches      finished bank buildings
     * @param householdSavings what the families have banked
     * @param sectorCash    what the businesses are holding
     */
    public void refresh(double branches, double householdSavings, double sectorCash,
                        double sectorBook, double cityBook, double householdBook) {
        this.branches = Math.max(0, branches);
        this.householdDeposits = Math.max(0, householdSavings);
        this.sectorDeposits = Math.max(0, sectorCash);
        this.deposits = this.householdDeposits + this.sectorDeposits + this.foreignDeposits;
        this.sectorBook = Math.max(0, sectorBook);
        this.cityBook = Math.max(0, cityBook);
        this.householdBook = Math.max(0, householdBook);
        /*
         * Unweighted until told otherwise, so a Bank built by hand behaves the
         * way it reads. Game walks the actual loan lists and calls
         * setWeightedBook() a line later; nothing else knows the terms.
         */
        this.sectorWeighted = this.sectorBook;
        this.cityWeighted = this.cityBook;
        this.householdWeighted = this.householdBook;
    }

    /**
     * The same book, weighed by what each loan actually is.
     *
     * Called immediately after refresh() by whoever can see the individual
     * loans - the bank cannot, and should not have to. Passing figures that do
     * not correspond to the gross book above is a way to make capacity lie, so
     * there is exactly one caller: Game.refreshBank().
     */
    public void setWeightedBook(double sector, double city, double household) {
        this.sectorWeighted = Math.max(0, sector);
        this.cityWeighted = Math.max(0, city);
        this.householdWeighted = Math.max(0, household);
    }

    /** Clears the month's flows. Called at the top of a month, before anything moves. */
    public void startMonth() {
        taxPaid = 0;
        interestEarned = 0;
        internalInterest = 0;
        writeOffs = 0;
        payroll = 0;
        upkeep = 0;
        lentToHouseholds = 0;
        repaidByHouseholds = 0;
        fundingCost = 0;
        depositInterestToHouseholds = 0;
        depositInterestToSectors = 0;
        depositInterestToForeign = 0;
        capitalInjected = 0;
        capitalFromHome = 0;
        dividendsPaid = 0;
        hotMoneyIn = 0;
        hotMoneyOut = 0;
        bailoutReceived = 0;
        foundingSettlement = 0;
        resolutionLossThisMonth = 0;
        // What the shareholders had before the month happened, so the statement
        // can show the movement rather than only the closing figure.
        openingEquity = equity();
    }

    /* ------------------------------ the arithmetic ------------------------------ */

    /** What is owed to it. The balance-sheet figure, and what it will be repaid. */
    public double getBook() { return sectorBook + cityBook + householdBook; }

    /**
     * The book as CAPACITY sees it: risk- and maturity-weighted.
     *
     * Deliberately a different name from getBook() rather than a flag on it.
     * The two answer different questions and a city stuffed with treasury bills
     * has them a long way apart - which is the entire point of the change.
     */
    public double getWeightedBook() {
        return sectorWeighted + cityWeighted + householdWeighted;
    }

    /** How much lighter the weighting makes the book. 0 when nothing is lent. */
    public double weightingRelief() {
        double gross = getBook();
        return gross > 0 ? 1 - getWeightedBook() / gross : 0;
    }

    /**
     * The most the bank can lend before it is funding itself abroad.
     *
     * The TIGHTER of the two limits above. They fail differently and the player
     * fixes them with different things: a bank short of CAPITAL needs to stop
     * losing money (or be recapitalised), and a bank short of FUNDING needs
     * branches, or a city with more savings in it.
     */
    public double capacity() {
        // Frozen until recapitalised. The whole consequence of failing.
        if (inResolution) return 0;
        /*
         * The capital it HAS, not the capital it would have. capacityWith()
         * answers the forward-looking question and credits a branch with the
         * equity opening it would bring; asking it about the branches already
         * standing would credit those a second time, and a bank with fifty
         * branches and no money would report itself well capitalised.
         */
        return Math.min(capitalLimit(), depositsGathered() * LEVERAGE);
    }

    /**
     * What the branches let it gather, out of what the city has to bank -
     * PLUS whatever the world has parked here, which needed no branch at all.
     *
     * FOREIGN MONEY ARRIVES WHOLESALE. A saver down the road needs a counter to
     * queue at; a fund in another country moves a hundred million by wire. So
     * hot money is not subject to the branch cap, and that asymmetry is the
     * point rather than an oversight: it is why foreign funding can lift a
     * bank's capacity far past anything its own branch network could support,
     * and why the capacity vanishes when the money does. The bank cannot build
     * its way out of a sudden stop.
     */
    public double depositsGathered() {
        double local = Math.min(Math.max(0, deposits - foreignDeposits),
                branches * depositsPerBranch);
        return local + Math.max(0, foreignDeposits);
    }

    private double foreignDeposits;

    /** Hot money currently funding this bank, in local money. */
    public double getForeignDeposits() { return foreignDeposits; }

    /** Told by Game each month, from CapitalFlows. */
    public void setForeignDeposits(double amount) {
        this.foreignDeposits = Math.max(0, amount);
        this.deposits = this.householdDeposits + this.sectorDeposits + this.foreignDeposits;
    }

    /**
     * Hot money arriving, as cash.
     *
     * A deposit is a liability the bank funds itself with, so the money is
     * genuinely here and genuinely someone else's. Counted for the month so
     * MoneyAudit can declare it crossing the city's edge - which it did.
     */
    public void receiveHotMoney(double amount) {
        if (amount <= 0) return;
        cash += amount;
        hotMoneyIn += amount;
    }

    /**
     * ...and leaving, which is the whole danger.
     *
     * The bank must find the cash. If it does not have it the withdrawal still
     * happens and the cash goes negative, which is what a bank run IS - the
     * money was lent out and cannot be recalled on demand. Whether that leaves
     * the bank insolvent is decided where insolvency is decided, not here.
     */
    public void returnHotMoney(double amount) {
        if (amount <= 0) return;
        cash -= amount;
        hotMoneyOut += amount;
    }

    private double hotMoneyIn, hotMoneyOut;

    public double getHotMoneyIn()  { return hotMoneyIn; }
    public double getHotMoneyOut() { return hotMoneyOut; }

    /**
     * The bank's wholesale funding cost, split by whose money it is.
     *
     * THE SAME QUESTION AS injectCapital(), AND IT WAS ANSWERED IN ONE PLACE
     * AND NOT THE OTHER. Paid-in capital was split between the city's own
     * savers and foreigners on 2026-09-07, because declaring all of it foreign
     * had $34.6B of branch capital deciding the balance of payments. The
     * funding side kept declaring 100% of itself foreign, and grew into a
     * larger version of exactly the same problem:
     *
     *     exports    234,348 a month
     *     imports    203,910
     *     interest   592,109      <- this line
     *
     * Two and a half times everything the city sold abroad, paid to foreign
     * creditors, every month, for ever. The current account was therefore
     * always deeply negative however well the city traded, depreciation
     * pressure sat at +0.95 on a trade SURPLUS, and the exchange rate ran to
     * its 4.0 ceiling and stayed there - which is a correct response to the
     * number it was given and a wrong number.
     *
     * A city with $124B on deposit has savers who can fund its bank. The same
     * curve decides how much of the funding they provide as decides how much of
     * the capital they own, because it is the same question about the same
     * city.
     */
    public double fundingCostAbroad() {
        return fundingCost * (1 - domesticCapitalShare());
    }

    /** ...and the part paid to lenders down the road. */
    public double fundingCostAtHome() {
        return fundingCost * domesticCapitalShare();
    }

    /** The share of the bank's funding that could leave at any time. */
    public double hotFundingShare() {
        double all = depositsGathered();
        return all > 0 ? Math.min(1, Math.max(0, foreignDeposits) / all) : 0;
    }

    /** What its capital supports, on the risk-weighted book. */
    public double capitalLimit() {
        return Math.max(0, equity()) / CAPITAL_RATIO;
    }

    /** True when it is the capital that binds rather than the funding. */
    public boolean capitalBound() {
        return capitalLimit() <= depositsGathered() * LEVERAGE;
    }

    /**
     * Equity against the risk-weighted book - the ratio a regulator reads.
     *
     * Infinite with nothing lent, which is correct and is why the callers ask
     * about isInsolvent() rather than reading this when the book is empty.
     */
    public double capitalRatio() {
        double weighted = getWeightedBook();
        return weighted > 0 ? equity() / weighted : Double.MAX_VALUE;
    }

    /**
     * Failed, and not yet put back on its feet.
     *
     * A STATE rather than a reading of today's equity, because the moment a bank
     * fails its creditors absorb the shortfall and its equity is zero again -
     * see resolveIfFailed(). Zero equity supports no lending, so a resolved bank
     * is still frozen; what lifts the freeze is capital, and this is the flag
     * that remembers the city owes it some.
     */
    private boolean inResolution;
    private int failures;

    /*
     * ONE FIELD WAS BEING ASKED TO BE A FLOW AND A STOCK. resolutionLoss was
     * zeroed in startMonth() because MoneyAudit needs this month's boundary
     * crossing, and it was saved, displayed and documented as "the money its
     * creditors ate" across the city's life. So the Bank tab printed "Times it
     * has failed: 308" beside "Its creditors absorbed: $0" in 3,693 of 4,001
     * months, and the save carried whatever the save month happened to hold.
     * The true lifetime figure was $22.65bn - six times the $3.76bn of city
     * capital the summary did report. Two fields now, one per question.
     */
    private double resolutionLossThisMonth;
    private double resolutionLossLifetime;

    public boolean isInsolvent()      { return inResolution || equity() < 0; }
    /** Over the city's life. What the panel and the save want. */
    public double getResolutionLoss() { return resolutionLossLifetime; }
    /** This month only. What the audit wants. */
    public double getResolutionLossThisMonth() { return resolutionLossThisMonth; }
    public int getFailures()          { return failures; }

    /**
     * How far above the required ratio a rescued bank comes out.
     *
     * A bank lifted out of resolution at EXACTLY the required ratio has no
     * buffer at all, and re-fails on the next dollar of payroll - and each of
     * those months was counted as a fresh failure. The log showed unbroken
     * runs of monthly "failures" with no write-off in any of them, eighteen in
     * a row at one point; of 308 events in a 4,000-month run only 79 were not
     * preceded by a failing month. A real resolution puts a bank back above
     * the line, not on it. Recapitalising to this multiple of the minimum
     * took failures 308 -> 26 and months starting frozen 326 -> 0.
     */
    public static final double RESOLUTION_EXIT_BUFFER = 1.5;

    /**
     * The solvency record, for the save.
     *
     * CARRIED RATHER THAN REBUILT, and the reason is the whole of a bug this
     * class was hiding. `failures` counts the times this bank went under across
     * the city's life; `resolutionLoss` is the money its creditors ate doing so,
     * and MoneyAudit already credits that figure as a real crossing of the audit
     * boundary. Neither can be recomputed from the balance sheet a month ended
     * with, because resolveIfFailed() deliberately leaves that sheet at zero -
     * a bank that has failed six times and a bank that has never failed look
     * identical the instant the loss is absorbed.
     *
     * So every save was reading a clean bank. The count reset to 0, the loss to
     * $0, and `inResolution` to false - which un-froze a frozen bank's lending
     * on load and made the audit's boundary figure disagree with itself by the
     * whole of the loss. Found when a 1,124-month city was asked how often its
     * bank had gone under and answered "never" while running a negative profit.
     *
     * Null on an older save, which restores as no-op - those cities had no
     * record to lose, which is exactly what they will now report.
     */
    public double[] solvencyToSave() {
        return new double[]{ failures, resolutionLossLifetime, inResolution ? 1 : 0 };
    }

    public void restoreSolvency(double[] state) {
        if (state == null || state.length < 3) return;
        failures               = (int) state[0];
        resolutionLossLifetime = state[1];
        inResolution           = state[2] != 0;
    }

    /**
     * The bank fails, and its creditors take the loss.
     *
     * WHY THIS HAS TO EXIST. Without it an insolvent bank goes on being charged
     * for wholesale funding it can neither repay nor shed, and the charge is a
     * share of a balance that the charge itself is growing. It compounds:
     * measured over four thousand months, a failed bank reached NEGATIVE
     * 1.9 x 10^52 of equity, which is not a number about a city, it is a
     * geometric series with a bad sign.
     *
     * What happens to a real failed bank is that somebody eats the hole. Here it
     * is the wholesale creditors, who are outside the city and can afford to be
     * abstract about it - so the loss is a real crossing of the audit boundary
     * and is declared. The bank comes out of it owning nothing, owing nothing,
     * and unable to lend a penny until it is given some capital.
     */
    public void resolveIfFailed() {
        double shortfall = -equity();
        if (shortfall > 0) {
            cash += shortfall;
            resolutionLossThisMonth += shortfall;
            resolutionLossLifetime  += shortfall;
            if (!inResolution) failures++;
            inResolution = true;
            return;
        }
        /*
         * ...OR EARNS ITS WAY BACK OUT.
         *
         * A frozen bank still collects on the book it already has, and retained
         * profit is capital like any other. Requiring a bailout to lift the
         * freeze meant a bank that had rebuilt its capital stayed shut anyway -
         * measured over four thousand months, one sat on $38M of equity, lent
         * nothing, and ordered 2,190 branches trying to fix a problem it had
         * already fixed. The bailout is the fast way out, not the only one.
         */
        if (inResolution && equity() >= resolutionExitEquity() - 1e-9) {
            inResolution = false;
        }
    }

    /**
     * The equity at which the freeze lifts, however the bank gets there.
     *
     * The required ratio times RESOLUTION_EXIT_BUFFER, and the SAME number
     * recapitalisationNeeded() asks the city for, so a bailout sized by the
     * one is exactly enough for the other. The first version lifted the freeze
     * at the bare ratio and asked the city for the bare ratio, which is a bank
     * with zero buffer - see RESOLUTION_EXIT_BUFFER.
     */
    public double resolutionExitEquity() {
        double byBook = getWeightedBook() * CAPITAL_RATIO * RESOLUTION_EXIT_BUFFER;
        /*
         * ...AND NEVER LESS THAN ONE BRANCH'S CAPITAL. The ratio is struck on
         * the book, so a bank that has lost its book along with its equity
         * needs, by the ratio, nothing at all - and a bank with nothing lends
         * nothing, so it never gets a book, so it never needs anything. A
         * deadlock, and it was the end state of every long run that had ever
         * been recorded: "equity $0k against a book of $0k", two branches,
         * $66bn of deposits, not a dollar lent, and a treasury holding $45bn
         * that was never asked. The floor is what one branch is capitalised
         * with when it opens, because an institution with less than that is
         * not one.
         */
        double floor = branches > 0 ? PAID_IN_PER_BRANCH : 0;
        return Math.max(byBook, floor);
    }

    /**
     * What it would take to put the bank back on its feet, with a buffer.
     *
     * Zero for a bank that is standing and adequately capitalised, whatever
     * its size; the branch floor above applies only once it has failed.
     */
    public double recapitalisationNeeded() {
        if (!inResolution && equity() > 0) {
            return Math.max(0, getWeightedBook() * CAPITAL_RATIO * RESOLUTION_EXIT_BUFFER - equity());
        }
        return Math.max(0, resolutionExitEquity() - equity());
    }

    /**
     * Shareholders' or the city's money, put in as capital.
     *
     * Cash in with no loan attached, so it lands squarely in equity - which is
     * the whole point of the exercise and the only thing that lifts a bank out
     * of a credit crunch.
     */
    /**
     * Deposits at which half of new bank capital is found at home.
     *
     * FINANCIAL DEEPENING, which is the honest name for what this models and
     * the reason it is a curve rather than a constant. A poor country's banks
     * are owned by foreigners because nobody at home has the savings to own
     * them; a rich country's are owned by its own savers and pension funds. The
     * city crosses from one to the other as it accumulates deposits, and it
     * does so gradually.
     */
    public static final double DOMESTIC_CAPITAL_SCALE = 400_000;

    /**
     * The same, in today's money.
     *
     * A SCALE is a money quantity even though it never leaves anybody's
     * pocket: it is the size of savings at which half a bank's capital can be
     * raised at home, and after a hundred-to-one reform an unreformed $400,000
     * would mean forty million founding dollars of savings - so a mature city
     * would suddenly be raising all its bank capital abroad. Which is exactly
     * the class of bug that pinned the currency at its ceiling in phase 4.
     */
    private double domesticCapitalScale = DOMESTIC_CAPITAL_SCALE;

    /** The share of new paid-in capital the city's own savers can find. */
    public double domesticCapitalShare() {
        double savings = getDeposits();
        if (savings <= 0) return 0;
        return savings / (savings + domesticCapitalScale);
    }

    /**
     * Shareholders' money, put in as capital - and WHOSE shareholders.
     *
     * THE BALANCE OF PAYMENTS WAS BEING DECIDED BY THIS METHOD, and nobody
     * chose that. Every branch takes PAID_IN_PER_BRANCH of capital, all of it
     * declared as arriving from abroad, and the playtest opened 1,081 branches:
     * $34.6B of foreign capital over a run, unbounded, uncosted, and larger
     * than every other item on the city's foreign account put together. Jerus
     * flagged it twice; the second time was after being told phase 4 would make
     * it worse, which it would - capital flows land in this same bank.
     *
     * The fix is not a cap, because a cap would be a number somebody invented.
     * It is asking WHO the shareholders are. A city with no savings has no
     * domestic investors and its bank is foreign-owned by necessity; a city
     * with $160B on deposit owns its own banks, and the money for the next
     * branch comes from savers down the road. Nothing is capped. The foreign
     * share simply stops being 100% as soon as the city has anything of its own.
     *
     * The money is identical either way and so is the audit's cash identity -
     * households sit outside the audited pools whichever country they are in.
     * What changes is which line of the balance of payments it lands on, which
     * is the entire question.
     */
    public void injectCapital(double amount) {
        /*
         * ALL OF IT FROM ABROAD, since 2026-09-10 (evening). The curve above
         * used to split this by how much the city had on deposit, and the
         * "home" half was money no household ever paid - a declaration, not a
         * transaction. The register sells the shares now (Equity, through
         * Game.capitaliseBank()): what the households buy arrives by the
         * two-argument form below out of their own savings, and what they do
         * not buy is the world's. This form is for a fixture standing up a
         * bank with no city round it.
         */
        injectCapital(0, amount);
    }

    /**
     * Shareholders' money, put in as capital, and whose: the households'
     * part came out of their savings through the register, the rest from
     * the world. Both land in equity; the balance of payments reads only the
     * second.
     */
    public void injectCapital(double fromHome, double fromAbroad) {
        double amount = Math.max(0, fromHome) + Math.max(0, fromAbroad);
        if (amount <= 0) return;
        cash += amount;
        capitalFromHome += Math.max(0, fromHome);
        capitalInjected += Math.max(0, fromAbroad);
    }

    /**
     * Pays the owners. Cash out, equity down by the same, and its own line on
     * the statement so the equity movement still articulates - see
     * BankCheck's "equity moves by the month's profit and by nothing else".
     *
     * @param amount decided by the register and the till; nothing is checked here
     */
    public void payDividend(double amount) {
        if (amount <= 0) return;
        cash -= amount;
        dividendsPaid += amount;
    }

    private double dividendsPaid;

    /** What it paid its shareholders this month. */
    public double getDividendsPaid() { return dividendsPaid; }

    /**
     * The same money, from the TREASURY rather than from shareholders.
     *
     * Kept apart from injectCapital() for one reason and it is not bookkeeping
     * fussiness: shareholders' capital arrives from outside the city and the
     * money audit has to see it arrive, while the city's does not - it moves
     * from one pool inside the city to another and cancels. Declaring both the
     * same way would have the treasury printing money every time it did this.
     *
     * ===================== A KNOWN HOLE, ON PURPOSE =====================
     *
     * Jerus, on being shown this: "i just realized something, bail out but city
     * requires bank to lend... so for now yes thats bad, but dont worry we will
     * add foreign debt or money printing next prompt."
     *
     * He is right and it is worth writing down. The city funds itself by
     * selling paper, and the buyer of that paper is this bank. So a treasury
     * with no cash that borrows in order to recapitalise its bank has the bank
     * lend it the money the bank is about to be given: the bank's assets rise by
     * a loan to the city and its equity rises by the same amount, and it has
     * capitalised itself with its own credit. No money is created - the audit is
     * untroubled, because both ends are inside the city - but CAPITAL is, and
     * capital is the thing the ratio above is supposed to constrain.
     *
     * It is real, it has a name (circular capital), and it brought down real
     * banks. It is left standing because the fix is a source of funds that is
     * not this bank - foreign borrowing, or a printing press - and that is the
     * next thing being built rather than something to bodge in here.
     */
    public void receiveBailout(double amount) {
        if (amount <= 0) return;
        cash += amount;
        bailoutReceived += amount;
        // Back in business the moment it holds what it is required to hold.
        resolveIfFailed();
    }

    private double capitalInjected;
    private double capitalFromHome;
    private double bailoutReceived;
    private double foundingSettlement;

    /**
     * The one-off jump in the bank's cash when the city's first branch opens and
     * it takes over the standing loan book. Crosses the city's edge; declared.
     */
    public double getFoundingSettlement() { return foundingSettlement; }

    /** Shareholders' money from OUTSIDE the city. Declared as a financial inflow. */
    public double getCapitalInjected() { return capitalInjected; }

    /**
     * ...and from the city's own savers. Declared, but not as foreign.
     *
     * Still an inflow to the audited pools - households are outside them - so
     * the cash identity is untouched by the split. It is the balance of
     * payments that stops claiming it.
     */
    public double getCapitalFromHome() { return capitalFromHome; }

    /** The treasury's money, from inside it. Internal, and deliberately not declared. */
    public double getBailoutReceived() { return bailoutReceived; }

    /**
     * Opens whatever branches have been built since last month, and says what
     * capital they need.
     *
     * Counted rather than watched for, so a city that builds three at once is
     * capitalised for three. The capital itself is NOT taken here since
     * 2026-09-10 (evening): it is sold as shares by the register, to the
     * city's households first and the world for the rest, and arrives by
     * injectCapital(home, abroad). See Game.capitaliseBank().
     *
     * @return the paid-in capital the new branches want, zero if none opened
     */
    public double openBranches(double nowStanding) {
        double opened = Math.max(0, nowStanding - branchesCapitalised);
        boolean founding = branchesCapitalised <= 0 && opened > 0;
        branchesCapitalised = Math.max(branchesCapitalised, nowStanding);
        if (opened <= 0) return 0;

        /*
         * THE CITY'S FIRST BANK STARTS WITH A CLEAN SET OF BOOKS.
         *
         * Everything lent before it existed was lent by somebody else - by the
         * outside world, at the punitive rate a city with no bank pays. So the
         * new bank does not inherit that history; it buys the standing loan book
         * at par, funded by its shareholders' capital and by whatever it can
         * gather, and opens with exactly the equity they put in.
         *
         * Without this, a founding bank inherited every phantom cost the
         * bookkeeping had accrued while there was no bank to bear them, and
         * opened insolvent.
         */
        if (founding) {
            double opening = -getBook();
            /*
             * DECLARED, because the bank's cash is one of MoneyAudit's pools and
             * this moves it without any loan or payment behind it. What it
             * represents is real: the outside lenders who had been funding this
             * city are settled with, and the new bank takes their place. Money
             * genuinely crosses the city's edge to do that, in whichever
             * direction the position happened to be sitting.
             */
            foundingSettlement = opening - cash;
            cash = opening;
        }

        return opened * paidInPerBranch;
    }

    private double branchesCapitalised;
    public double getBranchesCapitalised() { return branchesCapitalised; }
    public void setBranchesCapitalised(double n) { this.branchesCapitalised = Math.max(0, n); }

    /** Book over capacity. Infinite in a city with no bank, which is the point. */
    public double strain() {
        double room = capacity();
        if (room <= 0) return getWeightedBook() > 0 ? Double.MAX_VALUE : 0;
        return getWeightedBook() / room;
    }

    /**
     * What the strain adds to every borrower's annual rate.
     *
     * Zero while the bank is comfortably within itself, rising to the cap as it
     * lends past what it holds. A city with no branches sits at the cap by
     * construction rather than by a rule about having no bank.
     */
    public double ratePremium() {
        if (branches <= 0) return getWeightedBook() > 0 ? MAX_STRAIN_PREMIUM : 0;
        double s = strain();
        if (s <= EASY_STRAIN) return 0;
        double travelled = (s - EASY_STRAIN) / (HARD_STRAIN - EASY_STRAIN);
        return MAX_STRAIN_PREMIUM * Math.max(0, Math.min(1, travelled));
    }

    /* ------------------------------- the flows ------------------------------- */

    /**
     * Interest arriving from a SECTOR or the CITY - both inside the audit, so
     * this is a transfer between pools and declares nothing.
     */
    public void takeInterest(double amount) {
        if (amount <= 0) return;
        cash += amount;
        interestEarned += amount;
        internalInterest += amount;
    }

    private double internalInterest;

    /** The part of the month's interest that came from inside the city's pools. */
    public double getInternalInterest() { return internalInterest; }

    /**
     * Paper bought below par: income the bank earns without any cash moving.
     *
     * The book already carries the bond at face value, so this is the other side
     * of that entry rather than a second one. Counted as internal, because the
     * city is the one paying it and the city is inside the audit.
     */
    public void takeDiscount(double amount) {
        if (amount <= 0) return;
        interestEarned += amount;
        internalInterest += amount;
    }

    /** Principal coming back. Cash, and the book shrinks with it. */
    public void takeRepayment(double amount) {
        if (amount <= 0) return;
        cash += amount;
    }

    /** Money out the door. */
    public void lend(double amount) {
        if (amount <= 0) return;
        cash -= amount;
    }

    /** Lending to a family, which crosses the audit's boundary and is counted. */
    public void lendToHouseholds(double amount) {
        if (amount <= 0) return;
        cash -= amount;
        lentToHouseholds += amount;
    }

    /** ...and from a household, which is outside, so both halves are declared. */
    public void takeFromHouseholds(double principal, double interest) {
        if (principal > 0) { cash += principal; repaidByHouseholds += principal; }
        if (interest > 0) { cash += interest; interestEarned += interest; }
    }

    /**
     * A loan that will not be repaid.
     *
     * COSTS THE BOOK, NOT THE CASH. The money left the building when the loan
     * was made; what is lost now is the promise to get it back. Booking it
     * against cash would be charging the bank twice and would show up in the
     * money audit as a pool that moved for no reason.
     */
    public void writeOff(double amount) {
        if (amount <= 0) return;
        writeOffs += amount;
    }

    /** Wages and running costs, which are real money leaving. */
    public void payRunning(double payroll, double upkeep) {
        this.payroll = Math.max(0, payroll);
        this.upkeep = Math.max(0, upkeep);
        cash -= this.payroll + this.upkeep;
    }

    /* ============================ THE FUNDING SIDE ============================
     *
     * Jerus asked for the bank to be able to fund itself with short-term paper
     * rather than only with what the city has banked with it. It already did,
     * in the worst possible way: cash simply went NEGATIVE when it lent more
     * than it held, which is a liability written as a negative asset - the
     * balance sheet equivalent of paying for something by making your wallet
     * owe you money.
     *
     * Now it borrows, and it is charged for borrowing. The cost is what makes
     * this a constraint rather than free money: a bank reaching further past its
     * deposits pays more for every dollar it has reached for, so the strain
     * premium it charges its borrowers is no longer arbitrary - it is passing on
     * a bill it is actually being sent.
     *
     * THIS MONEY COMES FROM OUTSIDE THE CITY, which makes the three flows below
     * real crossings of the audit boundary rather than internal transfers. They
     * are declared in MoneyAudit. That is the honest reading: a city whose bank
     * is funding itself in the wholesale market genuinely has more money in it
     * than a city whose bank is not.
     */

    /*
     * THE BANK'S CASH IS ITS NET FUNDING POSITION, and once that is said out
     * loud the rest of this falls out.
     *
     * A negative cash balance is not a bank with a negative wallet - it is a
     * bank that has lent out more than has come in, which is to say a bank that
     * has borrowed. What it has borrowed is funded in two tranches, cheapest
     * first, exactly as a real one funds itself:
     *
     *   DEPOSITS, up to what the city has banked with it, at no cash cost. This
     *   is the city's own money doing the work, and it is why a city with deep
     *   savings has a cheap banking system.
     *
     *   WHOLESALE PAPER for anything beyond that, at a price, from abroad.
     *
     * The first version of this charged the wholesale rate on the WHOLE book,
     * because deposits are a memorandum figure here rather than cash the bank
     * holds. A bank paying market funding costs on every dollar it has ever lent
     * cannot make money: measured over 90 months it went to NEGATIVE $37M of
     * equity against $48M of funding, which is not a bank, it is a bonfire.
     */

    /**
     * What share of its INTEREST INCOME the bank passes on to its depositors.
     *
     * The other half of what a bank IS. Until this existed the deposits were
     * free money - the city's savings sat with the bank earning nothing while it
     * lent them out at eleven percent, which is not a bank, it is a shoebox with
     * a licence.
     *
     * Paying for them turns the difference between what it charges and what it
     * pays into a NET INTEREST MARGIN, which is the number a real bank lives or
     * dies on. It also closes a loop that was open: household savings now
     * compound, which raises deposits, which raises what the city can borrow.
     *
     * ------------------------------------------------------------------
     * A SHARE OF THE INCOME, NOT A SHARE OF THE RATE (2026-09-09).
     *
     * Jerus: "make it so that the banks pay 55% of interest income, not 55% of
     * the interest rate."
     *
     * It used to be `depositRate = riskFree * .55`, charged on whatever the bank
     * was funding with deposits. Those are the same number only while the bank
     * has lent out roughly what it has taken in, and this game's banks routinely
     * are not in that position: measured on a mature city, one branch held
     * $60M of gathered deposits against a book of $12M and earned $429,900 a
     * month in interest while paying its tellers $695,800. A bank in that shape
     * was paying a rate struck against money it had no way to earn on.
     *
     * Paying a share of what it ACTUALLY EARNED fixes that by construction. A
     * bank that cannot lend pays its savers almost nothing, which is both
     * correct and what a real deposit rate does in a slump; a bank lending its
     * whole book pays the full margin. The 45% it keeps is the margin, and it
     * can no longer be squeezed by a book that shrank after the rate was set.
     *
     * depositRate() is DERIVED from the payout now rather than driving it - see
     * fundToCover(). It is still the number the screens show, because "what am
     * I being paid on my savings" is a rate question whichever way round the
     * arithmetic runs.
     * ------------------------------------------------------------------
     */
    public static final double DEPOSIT_PASS_THROUGH = .45;

    private double depositRate;
    private double depositInterestToHouseholds;
    private double depositInterestToSectors;

    /**
     * ...and what the hot money is paid for parking here.
     *
     * A third tranche because there is a third kind of depositor and it lives
     * abroad. Used to be folded into the sectors' share, which sent foreign
     * savers' interest to domestic businesses and lost it entirely in a month
     * when no business had a positive balance. See fundToCover().
     */
    private double depositInterestToForeign;

    /** What savers are being paid. */
    public double depositRate() { return depositRate; }

    public double getDepositInterestToHouseholds() { return depositInterestToHouseholds; }
    public double getDepositInterestToSectors()    { return depositInterestToSectors; }
    public double getDepositInterestToForeign()    { return depositInterestToForeign; }
    public double depositInterest() {
        return depositInterestToHouseholds + depositInterestToSectors
                + depositInterestToForeign;
    }

    /** What it charges borrowers, less what it pays savers. A bank's whole business. */
    public double netInterestMargin() {
        double weighted = getBook();
        return weighted > 0 ? (interestEarned - depositInterest() - fundingCost) / weighted * 12 : 0;
    }

    /** Over the risk-free rate, for being a bank rather than a treasury. */
    public static final double FUNDING_SPREAD = .02;

    /** ...and more, the further past its deposits it has reached. */
    public static final double FUNDING_STRETCH = .06;

    private double fundingRate;

    /** Everything it owes: the mirror of a negative cash position. */
    public double borrowings() { return Math.max(0, -cash); }

    /**
     * The cheap tranche - the city's own money, lent back out.
     *
     * Measured against what its branches can GATHER, not against everything the
     * city happens to have. A bank cannot fund itself with savings held in
     * somebody's mattress, and a bank with no branches has gathered nothing at
     * all, so every dollar it has lent is money it went to the market for.
     */
    public double depositFunding() { return Math.min(borrowings(), depositsGathered()); }

    /** ...and the part it had to go to the market for. */
    public double wholesaleFunding() { return borrowings() - depositFunding(); }

    /**
     * What the bank is paying for the money it did not have.
     *
     * Rises with how far it has reached, not with how much it holds: a bank
     * funding a tenth of its book in the market is a normal bank, and one
     * funding twice its deposits is one the market has questions about.
     */
    public double fundingRate() { return fundingRate; }

    /**
     * Settles the funding for the month: charge for what was borrowed, then
     * borrow what is short or repay what is spare.
     *
     * ORDER MATTERS AND IS DELIBERATE. Interest is charged on the balance the
     * bank owed for the whole month, BEFORE this month's raise, because that is
     * the money it actually had the use of. Charging on the closing balance
     * would bill it for a loan it took out this afternoon.
     *
     * Called once, at the end of the month, after every loan has moved.
     */
    public void fundToCover(double riskFreeAnnual) {

        /*
         * WITH NO BRANCHES THERE IS NO BANK, and nothing to charge.
         *
         * The lending in a city with no bank is coming from outside it - from
         * strangers, which is what the eighteen-point premium on every borrower
         * IS - so there is no institution here paying depositors or rolling
         * paper. Charging one anyway compounded a cost onto an entity that did
         * not exist: measured over 180 months, a bankless city's phantom bank
         * ran up NEGATIVE $48,000 of equity, and because a bank needs capital to
         * lend, that hole then made it impossible for the city ever to found a
         * real one. A trap with no way out, built entirely out of bookkeeping.
         */
        if (branches <= 0 || inResolution) {
            // A bank in resolution is not paying anybody. Charging it is what
            // turned a failure into a runaway - see resolveIfFailed().
            depositRate = 0;
            fundingRate = 0;
            return;
        }

        /*
         * THE MARKET TRANCHE IS PRICED FIRST NOW, because the deposit decision
         * below needs to know what the month already costs before it can work
         * out what it can afford. Neither the wholesale amount nor its rate
         * depends on what savers are paid - wholesaleFunding() is borrowings
         * less the deposit-funded part, and both are fixed by the time this
         * runs - so moving it above the depositors changes no arithmetic.
         */
        double wholesaleNow = wholesaleFunding();
        double reachNow = deposits > 0 ? Math.min(3, wholesaleNow / deposits)
                                       : (wholesaleNow > 0 ? 3 : 0);
        fundingRate = Math.max(0, riskFreeAnnual) + FUNDING_SPREAD + FUNDING_STRETCH * reachNow;
        fundingCost = wholesaleNow * fundingRate / 12;

        /*
         * ...AND THEN THE DEPOSITORS, out of what is left.
         *
         * See chooseDepositRate(). Three rules, all Jerus's: a share of the
         * month's INTEREST INCOME rather than of the headline rate; never more
         * than leaves the bank at net zero; and, when it is already in profit,
         * as much more as a forecast of the money a better rate would attract
         * can justify.
         */
        double onDeposits = chooseDepositRate(riskFreeAnnual);
        depositRate = deposits > 0 ? onDeposits / deposits * 12 : 0;

        /* -------------------------------------------------------------------
           SPLIT THREE WAYS, NOT TWO, AND IT USED TO LOSE MONEY.

           This was `households get their share, SECTORS GET THE REST`, and the
           rest is not the sectors. Deposits come from three places - families,
           the city's own companies, and hot money from abroad - and lumping the
           last two together did two wrong things at once:

             - the foreign depositors' interest was handed to domestic
               businesses. It is money leaving the country, it belongs on the
               income line of the balance of payments beside the bank's
               wholesale funding cost, and instead it was an internal transfer
               to whoever happened to be holding cash.

             - and when NOBODY was holding cash, it simply vanished. Game pays
               this out by splitting it across the sectors in proportion to
               what each has in the till; the loop is guarded by
               `if (totalSectorCash > 0)`, so in a month where every sector is
               at or below zero the bank's cash went down and nobody's went up.
               Money destroyed, silently, by a defensive guard.

           Found on 2026-09-07 by MoneyCheck, in a stressed city three months
           into a slump: retail 0.00, real estate 0.00, construction 0.00,
           heavy industry 0.00, mining 0.00, industry -6,881. Every till empty
           or overdrawn, $16.05 paid to nobody, and $30.84 the month after. It
           had been reachable since foreign deposits existed and no run had
           ever been unlucky enough to land on it.

           Now each tranche is charged on its OWN base, so the three add back to
           the total by construction and the "rest" has nowhere to hide. And
           sectorDeposits is the sum of what the sectors are actually IN CREDIT
           for - see Game.refreshBank() - which is the same weighting the payout
           loop uses, so a month with no sector deposits computes no sector
           interest and there is nothing left over to lose.
           ------------------------------------------------------------------- */
        double base = deposits > 0 ? deposits : 0;
        depositInterestToHouseholds = base > 0 ? onDeposits * householdDeposits / base : 0;
        depositInterestToSectors    = base > 0 ? onDeposits * sectorDeposits    / base : 0;
        depositInterestToForeign    = onDeposits - depositInterestToHouseholds
                                                 - depositInterestToSectors;
        cash -= onDeposits;

        // Only the market tranche is charged for. The deposits are the city's
        // own money and cost the bank nothing to use beyond what it pays for
        // them - which is the whole advantage of having somewhere for people to
        // save. Struck above, banked here.
        cash -= fundingCost;
    }

    /* =====================================================================
       WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT.

       Jerus, 2026-09-09: "make it variable, the bank will not give out more
       than it needs to stay net 0, but if its already making profit it will do
       some forecast to see what new deposits would come in if it increased the
       deposit interest rate."

       Three rules, in the order they bind.

       1. THE BASELINE IS A SHARE OF THE INCOME. DEPOSIT_PASS_THROUGH of what
          the book actually earned this month. See that constant for why a share
          of income and not of the rate.

       2. NEVER PAST A NET ZERO INTEREST MARGIN. It will not pay out more
          interest than it earned, net of what it paid the market for the money
          it had to go and borrow. This is the rule that makes rule 1 safe:
          paid as a flat share of income with no ceiling at all, a bank whose
          margin had been eaten went on paying anyway, and measured over 4,002
          months that took the city into a housing famine - 27 households with
          nowhere to live by month 261, because a bank that cannot hold capital
          cannot lend and a city that cannot borrow stops building.

          MEASURED ON THE INTEREST MARGIN AND NOT ON THE WHOLE P&L, which was
          the first attempt and was wrong. Deposit interest is a COST OF
          FUNDING, not a distribution of profit: a real bank pays its savers and
          then finds out whether its branches were affordable, not the other way
          round. Netting the payroll off first meant this game's bank - 69 staff
          against the interest a single branch can earn - had nothing left for
          savers in any month at all, which is not "variable", it is zero with
          extra steps. BankCheck and SaveFileCheck both said so immediately.

       3. AND HIGHER, IF THE MONEY IS WORTH BUYING. Only when all three of
          these hold, which is the same shape of question wantsBranch() asks
          about counters:

            - it is in profit, so there is something to spend;
            - it is LENT OUT, so another dollar of funding would be used
              rather than sat on;
            - and DEPOSITS are what is binding, not capital. A bank short of
              equity cannot lend another dollar however much money it is
              offered, and bidding for deposits it may not lend is the same
              error as building counters it cannot use.

          The forecast is CapitalFlows' own arithmetic, asked rather than
          re-derived: hot money follows the better of the deposit rate and the
          city's borrowing rate, so the bank has to outbid its own government
          before a single dollar moves. What makes this a real decision rather
          than a free lunch is that a rate is paid to EVERYBODY: buying a dollar
          of new money costs the raise on every dollar already here.
       ===================================================================== */

    /** How many candidate rates the bank considers between nothing and its ceiling. */
    private static final int RATE_STEPS = 12;

    /** Set by Game each month so the bank can price deposits without knowing what a carry trade is. */
    private DepositMarket depositMarket;

    /** What the world would park here at a given deposit rate. */
    public interface DepositMarket {
        double arrivalsAt(double depositRate);
    }

    public void setDepositMarket(DepositMarket market) { this.depositMarket = market; }

    /**
     * The month's deposit interest, in money. Sets nothing but the answer.
     *
     * @return what to pay savers this month, already capped at net zero
     */
    private double chooseDepositRate(double riskFreeAnnual) {

        if (deposits <= 0) return 0;

        /* ---- rule 2: never more interest out than interest in ---- */
        double ceilingCash = Math.max(0, interestEarned) - fundingCost;
        if (ceilingCash <= 0) return 0;          // the margin is already gone

        /* ---- rule 1: the baseline ---- */
        double baseline = Math.min(Math.max(0, interestEarned) * DEPOSIT_PASS_THROUGH,
                                   ceilingCash);

        /* ---- rule 3: and higher, if buying deposits pays for itself ---- */
        /*
         * "IF ITS ALREADY MAKING PROFIT" - the real one, after the staff and the
         * building, because that is the money it would be spending. The ceiling
         * above is about the margin; this is about whether there is anything to
         * play with.
         */
        double profitNow = ceilingCash - writeOffs - payroll - upkeep - baseline;
        if (depositMarket == null || profitNow <= 0 || baseline >= ceilingCash) return baseline;

        double lendingRate = Math.max(0, riskFreeAnnual) + ratePremium();
        double room = capacity();
        double overflow = Math.max(0, getWeightedBook() - room * EASY_STRAIN);
        boolean lentOut = overflow > 0;
        boolean depositsBind = depositsGathered() * LEVERAGE < capitalLimit();
        if (!lentOut || !depositsBind) return baseline;

        double bestCash = baseline;
        double bestGain = 0;

        /*
         * A BANK NEVER PAYS SAVERS MORE THAN IT CHARGES BORROWERS, so that is
         * where the search stops. Without it the sweep ran from zero to
         * whatever the margin could bear - 60% a year on a bank with a small
         * deposit book and a good month - and its FIRST step was already dearer
         * than any amount of new lending could repay, so a bid that was
         * comfortably profitable at one percent was never looked at. The bound
         * is what gives the grid its resolution, and it is a real rule rather
         * than a tuning constant.
         */
        double ceilingRate = Math.min(ceilingCash / deposits * 12, lendingRate);
        for (int step = 1; step <= RATE_STEPS; step++) {
            double candidate = ceilingRate * step / RATE_STEPS;
            double newMoney = Math.max(0, depositMarket.arrivalsAt(candidate));
            if (newMoney <= 0) continue;

            // What that money is worth: the lending it unlocks, capped by what
            // the bank actually wants to fund, earning the going rate.
            double unlocked = Math.min(newMoney * LEVERAGE * EASY_STRAIN, overflow);
            double extraIncome = unlocked * lendingRate / 12;

            // ...against the raise, paid to every depositor, new money included.
            double cost = (deposits + newMoney) * candidate / 12;
            if (cost > ceilingCash) break;       // past net zero; nothing beyond this is allowed

            double gain = extraIncome - cost;
            if (gain > bestGain) { bestGain = gain; bestCash = cost; }
        }

        double chosen = Math.min(Math.max(baseline, bestCash), ceilingCash);
        if (chosen > baseline + 1e-9) {
            monthsBidUp++;
            lastBidUpGain = bestGain;
        }
        return chosen;
    }

    /*
     * HOW OFTEN THE BANK ACTUALLY BID FOR MONEY.
     *
     * Counted, and counted for one reason: a mechanic that has never fired in a
     * real run looks exactly like one that does not exist, and this project has
     * shipped one of those before - see CapitalFlows' note on counting the
     * sudden stops. If this stays at zero over four thousand months then rule 3
     * is decoration and should be taken out rather than admired.
     */
    private int monthsBidUp;
    private double lastBidUpGain;

    public int getMonthsBidUp()        { return monthsBidUp; }
    public double getLastBidUpGain()   { return lastBidUpGain; }

    public double getFundingCost() { return fundingCost; }

    /* =========================== THE THREE STATEMENTS ===========================
     *
     * Jerus: "can you make the bank have financial statements?" - which is a
     * fair question to ask of the one participant in this economy that is
     * ENTIRELY a balance sheet. Every other sector is a business that happens to
     * borrow; this is a business whose product IS the borrowing.
     *
     * ASSETS = LIABILITIES + EQUITY, and it is an identity here rather than a
     * presentation: equity() is the residual, and the harness asserts that it
     * moves by exactly the month's net income and by nothing else. A plug that
     * nobody checks is how a set of books lies.
     *
     * WHERE THE DEPOSITS ARE. They are NOT a liability on this balance sheet,
     * and that is the one deliberate departure from how a real bank's books
     * look. The families' savings and the sectors' cash are counted as deposits
     * for the lending capacity without being HELD as the bank's cash - the
     * sectors' cash is already its own pool in MoneyAudit, and putting it here
     * too would count the same dollar twice. So it appears as a memorandum
     * figure, which is what it honestly is: a measure of how much the city has
     * to lend against, not money this bank is sitting on.
     */

    /** Cash it is actually sitting on. Zero while it is net borrowed. */
    public double cashReserves() { return Math.max(0, cash); }

    /** Total assets: the loan book, plus whatever cash it has not lent. */
    public double totalAssets() { return cashReserves() + getBook(); }

    /** Total liabilities: what it owes, in its two tranches. */
    /**
     * What the bank owes: its market funding, and the hot money.
     *
     * WHY FOREIGN DEPOSITS ARE HERE AND HOUSEHOLD ONES ARE NOT, which looks
     * inconsistent and is the opposite:
     *
     * A family's savings are already counted, in the household pool. They are
     * the city's own money sitting in an account, and refresh() reads them as a
     * capacity input without ever moving them onto this balance sheet - putting
     * them here as both an asset and a liability would count the same dollars
     * twice.
     *
     * Hot money is different in exactly the way that matters. It is money that
     * was not in the city at all until it arrived, so the cash is genuinely new
     * here - and it is owed to somebody who can ask for it back on no notice.
     * An asset with no matching liability is equity, and without this line the
     * bank's capital rose every time a foreign fund wired it money. Caught by
     * BankCheck's "equity moves by net income and capital and nothing else",
     * which is the third distinct bug that assertion has found.
     */
    public double totalLiabilities() { return borrowings() + Math.max(0, foreignDeposits); }

    /**
     * The residual - and, once the two above are written out, simply the book
     * plus the cash position. A bank that has lent exactly what it borrowed owns
     * nothing; everything it earns accumulates here.
     *
     * The harness asserts this moves by exactly the month's net income, which is
     * how a restructure that forgot to bill the bank was found.
     */
    public double equity() { return totalAssets() - totalLiabilities(); }

    /** Equity as it stood at the top of the month. */
    public double getOpeningEquity() { return openingEquity; }

    /* ---- the income statement, in the order a bank's actually runs ---- */

    /** What every borrower paid it this month. */
    public double interestIncome()  { return interestEarned; }

    /** ...less what it paid savers and what it paid the market. */
    public double netInterestIncome() {
        return interestEarned - depositInterest() - fundingCost;
    }

    /** ...less the loans that will not come back. */
    public double afterLosses()     { return netInterestIncome() - writeOffs; }

    /** ...less the tellers and the lights. */
    public double operatingExpenses() { return payroll + upkeep; }

    /** What it made before the city took its share. */
    public double profitBeforeTax() { return afterLosses() - operatingExpenses(); }

    /* --------------------------------- tax --------------------------------- */

    /*
     * Jerus: "quick question banks are taxed right?"
     *
     * Half of it was. Its STAFF have always been taxed - the wage tax is struck
     * from the city's staffed wage bill on the population side, so moving the
     * tellers onto the bank's own books changed nothing there. Its PROFITS were
     * not, by anybody: the bank's income statement lives in this class and
     * EconomyManager.getTaxIncome() had never heard of it. The one business in
     * the city with a full set of financial statements was the one paying no
     * profit tax at all.
     *
     * CHARGED IN ARREARS, on last month's profit, and that is an ordering
     * constraint rather than a flourish. The city's tax is struck inside
     * finalUpdateEconomy(), and the bank does not settle its month until after
     * that - it cannot, because its interest income is read off statements
     * finalUpdateEconomy() is in the middle of producing. So this month's profit
     * simply is not known when this month's tax is collected.
     *
     * Paying on the prior period is what companies actually do, it keeps the
     * money in ONE channel (the bank's pool to the city's, in a single tick,
     * cancelling in the audit), and it needs no eleventh pool to hold tax in
     * transit. The alternative - collecting mid-month on a figure that does not
     * exist yet - is how this codebase produces money from nowhere.
     */

    private double taxPaid;
    private double profitLastMonth;

    /**
     * Profit the tax is charged on: the month that has just finished.
     *
     * A CARRIED FIGURE, set once at the end of the month by closeMonth() and
     * written into the save. The first version derived it at the top of the next
     * month from the flow fields, which still held the old month's numbers on
     * the live path and held zero on the load path - so a reloaded city quietly
     * collected no bank tax, and SaveFileCheck caught the two cities' next-month
     * income $19 apart. A flow cannot be reconstructed from the state a month
     * ended in; this is the ninth time that has been true in this codebase.
     */
    public double getProfitLastMonth() { return profitLastMonth; }

    /** Called at the end of the month, once the profit is final. */
    public void closeMonth() {
        profitLastMonth = profitBeforeTax();
        lastPayroll  = payroll;
        lastUpkeep   = upkeep;
        lastInterest = interestEarned;
        lastBook     = getBook();
    }

    /* ---------------------- LAST MONTH, KEPT ON PURPOSE ----------------------
     *
     * startMonth() zeroes every flow above, and the investment advisor asks its
     * questions BETWEEN the two - after the month has opened and before anything
     * has moved through it. So a branch decision reading `payroll` or
     * `interestEarned` reads zero, every time, in every month.
     *
     * That is the standing rule of this codebase turned on its own author: a
     * flow cannot be read from the state a month STARTED in either. The first
     * two versions of branchWouldPayForItself() were written against the live
     * fields and were silently inert - four thousand months of a playtest came
     * back byte-identical twice, which is what a test that never fires looks
     * like.
     *
     * These four are last month's, struck when the month closed and therefore
     * final. Carried in the save for the same reason profitLastMonth is: the
     * advisor's answer would otherwise differ between a live city and a
     * reloaded one for a month.
     * ------------------------------------------------------------------------ */
    private double lastPayroll, lastUpkeep, lastInterest, lastBook;

    public double[] lastMonthToSave() {
        return new double[]{ lastPayroll, lastUpkeep, lastInterest, lastBook };
    }

    public void restoreLastMonth(double[] state) {
        if (state == null || state.length < 4) return;
        lastPayroll  = state[0];
        lastUpkeep   = state[1];
        lastInterest = state[2];
        lastBook     = state[3];
    }

    /** Restored from the save, so next month taxes the right figure. */
    public void setProfitLastMonth(double value) { this.profitLastMonth = value; }

    /**
     * What savers were paid in the month this save was taken in.
     *
     * A FLOW struck inside fundToCover(), which only a month tick runs - so a
     * reloaded city read 0.00% for savers beside a live policy rate of 5.16% on
     * the Policies landing, and the Bank tab said the same thing in its own
     * words. Carried rather than re-derived because re-deriving it means
     * re-reading the deposit book, and the deposit book is every sector's till
     * as it stands NOW rather than as it stood when the rate was set.
     */
    public void setDepositRate(double rate) { this.depositRate = Math.max(0, rate); }

    /** What the city took this month. */
    public double getTaxPaid() { return taxPaid; }

    /**
     * Hands the city its share of last month's profit.
     *
     * Only on a PROFIT - there is no refund for a bad month, which is how every
     * other sector in this game is treated. A loss-making bank simply pays
     * nothing, which also means tax can never be the thing that pushes one into
     * failure: what is left after paying is always a fraction of something
     * positive.
     *
     * @return what was taken, for the treasury to receive in the same tick
     */
    public double chargeTax(double annualProfitRate) {
        taxPaid = Math.max(0, profitLastMonth) * Math.max(0, annualProfitRate);
        cash -= taxPaid;
        return taxPaid;
    }

    /* ------------------------------- reading ------------------------------- */

    public double getCash()               { return cash; }
    public double getDeposits()           { return deposits; }
    public double getBranches()           { return branches; }
    public double getSectorBook()         { return sectorBook; }
    public double getCityBook()           { return cityBook; }
    public double getHouseholdBook()      { return householdBook; }
    public double getInterestEarned()     { return interestEarned; }
    public double getWriteOffs()          { return writeOffs; }
    public double getPayroll()            { return payroll; }
    public double getUpkeep()             { return upkeep; }
    public double getLentToHouseholds()   { return lentToHouseholds; }
    public double getRepaidByHouseholds() { return repaidByHouseholds; }

    /**
     * The month's profit: interest earned, less the cost of the money, less the
     * loans that died, less the cost of running the place.
     *
     * The funding cost is new and is the line that makes this a business rather
     * than a machine that prints interest. Before it, a bank could lend without
     * limit and every dollar of it was pure margin.
     */
    public double getNetIncome() {
        return profitBeforeTax() - taxPaid;
    }

    /** True when the bank is lending past what it holds and charging for it. */
    public boolean isStrained() { return ratePremium() > 0; }

    /**
     * True when the city should be opening another counter.
     *
     * A city with loans and no bank at all always wants one - it is paying the
     * maximum premium on every dollar of them. Otherwise it is a question about
     * the strain, asked slightly early so the building is finished before the
     * premium arrives rather than after.
     */
    public boolean wantsBranch() {
        /*
         * A COUNTER CANNOT FIX A SHORTAGE OF SAVINGS.
         *
         * Capacity is the tighter of two limits, and only one of them is a
         * building. When it is the DEPOSITS that bind, another branch buys the
         * city nothing at all - capacity stays exactly where it was - and an
         * advisor that built one anyway would be putting up bank branches
         * because the city is poor. Which is what it did: a young city's
         * deposits are small, so its capacity was small, so its strain was
         * enormous, and MiningCheck's control city quietly grew a second bank
         * whose five hundred jobs came out of the foundry's shift.
         *
         * bookAnotherBranchWouldCarry() is zero in exactly that case, so this is
         * one question rather than two.
         */
        /*
         * A FAILED BANK NEEDS CAPITAL, NOT COUNTERS.
         *
         * While it is frozen its capacity is zero, so its strain is infinite,
         * so every test below says "build another branch" - and it did, 2,190
         * of them, none of which could help, each of which took capital from
         * shareholders and jobs from the city. Branches are for gathering
         * deposits; a bank that may not lend has no use for more of them.
         */
        if (inResolution) return false;
        if (bookAnotherBranchWouldCarry() <= 0) return false;
        if (branches <= 0) return getWeightedBook() > 0;
        if (!branchWouldPayForItself()) return false;
        return strain() > BUILD_AT_STRAIN;
    }

    /**
     * Whether the counter earns more than it costs to keep open.
     *
     * THE TEST THAT WAS MISSING, and its absence was the third reason this bank
     * kept failing. The two above ask whether a branch would RELIEVE anything;
     * neither asks whether it is worth having.
     *
     * That mattered because of how capacityWith() works, which is correct and
     * is also a trap. Opening a branch is an equity injection - PAID_IN_PER_BRANCH
     * of capital, which at CAPITAL_RATIO supports about $400M of lending - so
     * while CAPITAL is what binds, one more branch always raises capacity and
     * bookAnotherBranchWouldCarry() is always positive. A bank that is losing
     * money is short of capital by definition, so it always wants another
     * branch, and every branch it opens brings a wage bill it is already unable
     * to cover. The city recapitalises its bank one building at a time and the
     * hole gets deeper with each one.
     *
     * Measured: 1,049 branches by month 4,000, holding $124bn of deposits
     * against a $7.6bn book - $10,553 of book per branch where one branch's
     * capital supports $400,000 of it, and $510 of equity left per branch out
     * of the $32,000 each put in. It had burned 98% of its capital and gone on
     * building.
     *
     * A REAL BANK SHORT OF CAPITAL RAISES CAPITAL; it does not open branches.
     * There is nothing to invent for the test - the bank already knows what its
     * book yields, what it passes to savers, and what a counter costs, because
     * all three are on this month's income statement.
     */
    public boolean branchWouldPayForItself() {
        if (branches <= 0) return true;          // the first one is a different question

        double runningCost = (lastPayroll + lastUpkeep) / branches;
        if (runningCost <= 0) return true;       // nothing known to cost yet

        double book = lastBook;
        if (book <= 0) return false;

        double monthlyYield = lastInterest / book;
        double keptMargin = monthlyYield * (1 - DEPOSIT_PASS_THROUGH);

        /*
         * ON WHAT A BRANCH ACTUALLY CARRIES, not on what one more is worth.
         *
         * The first version of this asked bookAnotherBranchWouldCarry() and was
         * a test that could never fail: that figure is the CAPACITY a branch
         * unlocks - about $320M once the 80% strain allowance is taken - and
         * $320M of book at any plausible margin dwarfs one branch's wages by a
         * factor of eight. It passed every month of a four-thousand-month run
         * and changed not one number in it.
         *
         * The capacity is not the book. Over that same run the bank's branches
         * carried $10,553 of book EACH, against the $400,000 their capital was
         * supposed to support - because the capital that arrived with each
         * branch was written off long before it could be lent. Asking what a
         * branch is entitled to carry, in a bank that has never once managed
         * it, is asking the wrong question in the confident direction.
         *
         * So: the branches standing carry this much book apiece, another will
         * carry about the same, and the question is whether that pays a
         * counter's wages. Self-correcting in both directions - a bank whose
         * book per branch recovers starts wanting branches again.
         */
        double bookPerBranch = book / branches;
        return bookPerBranch * keptMargin > runningCost;
    }

    /**
     * The loan book one more branch would take onto the bank's own account.
     *
     * What a branch is WORTH, and the reason the advisor can price one at all.
     * A bank building has no coverage and sells nothing, so every profit
     * estimate in BusinessInvestment returned zero for it and a branch could
     * only ever be paid for in cash - which a city whose credit has gone dear
     * is exactly the city least likely to have.
     *
     * It is the OVERFLOW it absorbs, not a flat BOOK_PER_BRANCH: the loans
     * already exist and are already earning: what the branch changes is whether
     * they are funded here or abroad. Zero when the bank is comfortably inside
     * itself, which is what stops the advisor building banks it does not need.
     */
    public double bookAnotherBranchWouldCarry() {
        // What one more counter is actually worth in capacity, which is nothing
        // at all when the deposits are the limit rather than the building.
        double gain = Math.max(0, capacityWith(branches + 1) - capacity());
        double overflow = Math.max(0, getWeightedBook() - capacity() * EASY_STRAIN);
        return Math.min(gain * EASY_STRAIN, overflow);
    }

    /**
     * Capacity this bank would have with a given number of branches.
     *
     * COUNTING THE CAPITAL THOSE BRANCHES WOULD BRING, which is the difference
     * between a bank that can be founded and one that cannot. A bank with no
     * branches has no capital, and with no capital it can lend nothing, and if
     * this asked what it could lend on the capital it has TODAY then the first
     * branch would always be worth exactly nothing and no city would ever build
     * one. Opening a branch is an equity injection; that is what makes it worth
     * opening.
     */
    public double capacityWith(double branchCount) {
        double newBranches = Math.max(0, branchCount - branchesCapitalised);
        double equityThen = equity() + newBranches * paidInPerBranch;
        double capital = Math.max(0, equityThen) / CAPITAL_RATIO;
        double funding = Math.min(Math.max(0, deposits),
                Math.max(0, branchCount) * depositsPerBranch) * LEVERAGE;
        return Math.min(capital, funding);
    }

    /** How much more it could lend before the premium starts. */
    public double headroom() {
        return Math.max(0, capacity() * EASY_STRAIN - getWeightedBook());
    }

    public void setCash(double value) { this.cash = value; }

    public void reset() {
        cash = 0;
        branches = 0;
        deposits = 0;
        foreignDeposits = 0;
        sectorBook = 0;
        cityBook = 0;
        householdBook = 0;
        sectorWeighted = 0;
        cityWeighted = 0;
        householdWeighted = 0;
        householdDeposits = 0;
        sectorDeposits = 0;
        fundingRate = 0;
        depositRate = 0;
        branchesCapitalised = 0;
        inResolution = false;
        failures = 0;
        taxPaid = 0;
        profitLastMonth = 0;
        internalInterest = 0;
        startMonth();
    }

    /*
     * NOTE: there is no funding field to carry in a save. Everything on the
     * liability side is derived from the cash position, which already is
     * carried - so the two halves of this balance sheet cannot come back out of
     * step with each other, because there is only one number.
     */

    /**
     * Every figure on the bank's balance sheet, in the new unit.
     *
     * The books are RE-DERIVED each month by Game.refreshBank() from household
     * savings, sector tills and the loan ledgers, so scaling them here is
     * belt-and-braces for the month between the reform and the next refresh -
     * but the cash, the branch scales, the lifetime capital counters and
     * last month's profit are carried, not derived, and would be wrong for ever
     * without this.
     *
     * branches is a COUNT and does not move. Nor do the rates.
     */
    public void redenominate(double scale) {
        depositsPerBranch    *= scale;
        paidInPerBranch      *= scale;
        domesticCapitalScale *= scale;

        cash              *= scale;
        deposits          *= scale;
        householdDeposits *= scale;
        sectorDeposits    *= scale;
        foreignDeposits   *= scale;
        sectorBook        *= scale;
        cityBook          *= scale;
        householdBook     *= scale;
        sectorWeighted    *= scale;
        cityWeighted      *= scale;
        householdWeighted *= scale;

        interestEarned    *= scale;
        internalInterest  *= scale;
        writeOffs         *= scale;
        payroll           *= scale;
        upkeep            *= scale;
        lentToHouseholds  *= scale;
        repaidByHouseholds *= scale;
        fundingCost       *= scale;
        openingEquity     *= scale;
        hotMoneyIn        *= scale;
        hotMoneyOut       *= scale;
        resolutionLossThisMonth *= scale;
        resolutionLossLifetime  *= scale;
        capitalInjected   *= scale;
        capitalFromHome   *= scale;
        dividendsPaid     *= scale;
        bailoutReceived   *= scale;
        foundingSettlement *= scale;
        depositInterestToHouseholds *= scale;
        depositInterestToSectors    *= scale;
        depositInterestToForeign    *= scale;
        taxPaid           *= scale;
        profitLastMonth   *= scale;
    }


    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        depositsPerBranch    = DEPOSITS_PER_BRANCH / unit;
        paidInPerBranch      = PAID_IN_PER_BRANCH / unit;
        domesticCapitalScale = DOMESTIC_CAPITAL_SCALE / unit;
    }

}
