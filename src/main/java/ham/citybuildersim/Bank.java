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
 * at the central bank's window (abroad, until 0.7.0) and prices what that
 * money costs it into what it charges. Until 0.7.7 it also charged a STRAIN
 * PREMIUM, up to eighteen points on every rate in the city once the book
 * passed 80% of capacity and the full eighteen with no branch at all - the
 * "credit at a punitive rate" a city with no bank was asked to pay. It is
 * gone: a loan is priced from what it costs to make (WHAT A LOAN COSTS,
 * below), a city with no branch is priced the same way with the window as
 * its marginal money, and strain() survives only as a measure.
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
     * Equity a bank must hold against everything it has lent, whatever that
     * weighs: 3%, Basel III's leverage ratio (0.7.11, round 2). The rule is
     * Tier 1 capital of at least 3% of the exposure measure, which is the
     * on-balance-sheet assets at their accounting value with no risk weight
     * at all (BCBS, "Basel III leverage ratio framework and disclosure
     * requirements", January 2014; LEV20 and LEV30 in the consolidated
     * framework). OSFI applies it in Canada at 3%, and 3.5% for the six
     * domestic systemically important banks; this is Basel's 3%.
     *
     * THE BACKSTOP TO THE RISK WEIGHTS, and built for the case round 1 of
     * this batch found. Jerus: "Basel leverage ratio." The landlords'
     * insured mortgages weigh nothing (RISK_INSURED_MORTGAGE). So a bank
     * whose book was mostly mortgages held its capital against almost
     * nothing and paid itself down to that. Measured, its equity at year
     * 100 was half the 0.7.10 bank's and at year 200 a sixth, and it failed
     * 219 times against 42 over the eight default seeds.
     *
     * Every requirement the bank compares its capital to is now the larger
     * of the two, minimumEquity(). Its own target and band scale with it in
     * the proportion it chose on the risk side (leverageTarget()).
     *
     * A FLOOR WEIGHT, IN EFFECT: LEVERAGE_RATIO_MIN / CAPITAL_RATIO is 37.5%.
     * A dollar lent never ties up less capital than a dollar weighing that,
     * which is how the insured mortgage's price carries capital
     * (capitalPerDollar()). Every other loan here weighs 100% and is
     * unchanged.
     */
    public static final double LEVERAGE_RATIO_MIN = .03;

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

    /**
     * A foreign carry borrower, against the risk weights above.
     *
     * A hundred percent, the same as a business or a household, and NOT the
     * city's twenty. They are a good credit - a bank or a fund, borrowing a
     * currency rather than a project - but they are not a sovereign, they are
     * not here, and the money is gone the moment it is lent. Weighting them
     * lower would let the book grow on capital that is not really behind it,
     * which is the shape of every banking crisis in the record.
     *
     * Jerus's call is that they never default, so this weight does no
     * loss-absorbing work today. It does capacity work: it is what stops the
     * carry book from being free.
     */
    public static final double RISK_CARRY = 1.00;

    /**
     * An insured mortgage (0.7.11): 0%. The city insures it as the Government
     * of Canada backs CMHC, and under the Basel III final standard (CRE20) an
     * exposure guaranteed by the sovereign in its own currency takes the
     * sovereign's weight, which is nothing - the reason a CMHC-insured
     * mortgage weighs nothing at a Canadian bank. For comparison, the same
     * loan uninsured - income-producing residential real estate at 80-90% of
     * its value - would weigh 60% there. So under the risk weights it ties
     * up no capital. Since round 2 of 0.7.11 the leverage ratio still asks
     * equity of it, as of every dollar lent: its price carries the capital
     * that ties up at the bank's leverage target (capitalPerDollar(),
     * insuredMortgageRate()), and the capital rule rations it while the
     * leverage requirement is the larger (BusinessDebtManager, THE
     * LANDLORDS' MORTGAGES).
     */
    public static final double RISK_INSURED_MORTGAGE = 0.0;

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

    /** How much of its capacity the bank lends before it counts itself full: the carry trade is lent only the room below it, and a branch is worth what it adds below it. It set where the strain premium began until 0.7.7. */
    public static final double EASY_STRAIN = .80;

    /**
     * Where the private sector starts building, which is BEFORE the bank is
     * full.
     *
     * A branch takes months to put up, so an advisor that waited until the
     * bank was full would leave the city at the window for the whole of the
     * lead time, every time (until 0.7.7, paying the strain premium for it).
     * Ten points of strain ahead of EASY_STRAIN is about one branch's worth of
     * building at the rate a growing city adds loans.
     *
     * Deliberately a strain threshold rather than a forecast off the book's
     * recent growth: the growth is a flow, it would have to be carried in the
     * save, and a dial that is one number the player can be told beats a
     * projection nobody can see.
     */
    public static final double BUILD_AT_STRAIN = .70;

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

    /**
     * The insured mortgages inside sectorBook (0.7.11): part of what the
     * businesses owe, weighed at RISK_INSURED_MORTGAGE rather than
     * RISK_BUSINESS, so the weight table shows them as their own row. Set
     * by Game.refreshBank() beside the book, off the loans themselves.
     */
    private double mortgageBook;
    private double mortgageWeighted;

    /** What the treasury paid it this month on insured mortgages the month's defaults wrote down (0.7.11): cash for a claim on its book, so no income and no loss. */
    private double insuranceClaims;

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

    /**
     * WHAT ITS SPARE CASH EARNS: THE POLICY RATE, AT THE CENTRAL BANK (0.7.0).
     *
     * Its spare cash - cashReserves(), what it has not lent - is its reserves
     * at the central bank, and it is paid the POLICY RATE on them by the
     * central bank, in money made for the purpose (CentralBank
     * .payInterestOnReserves()). That is the floor under every rate in the
     * city: no bank lends below what it earns by doing nothing, so the dial
     * is the price of money rather than a suggestion on top of it. And what
     * savers are paid is a share of the dial itself since 0.7.7, never more
     * than the interest margin this line is part of can pay (WHAT TO PAY
     * SAVERS); until then it was a share of what the bank earned, and the
     * deposit rate rose with the dial through this line.
     *
     * IT WAS PLACED ABROAD, AT THE WORLD'S RATE, until 0.7.0 - PLACEMENT_RATE,
     * which was DebtManager.WORLD_BASE_RATE, declared to the audit as income
     * from abroad. The rest of this note is how it came to be placed at all,
     * and stands as history.
     *
     * A bank with money it has not lent does not keep it in a drawer; it
     * places it - overnight, in bills, abroad - at the risk-free rate, and
     * that is most of what a bank in a town with nothing to finance lives
     * on. This one kept it in a drawer. Its cash position is symmetric by
     * design - negative is borrowing, deposits first and the market for the
     * rest, and the market tranche is charged for (fundToCover) - but the
     * positive side earned nothing, so a bank whose borrowers had all repaid
     * paid its tellers out of its capital until there was none.
     *
     * Measured on the playtest's first city once the landlords stopped
     * over-borrowing (the sector template): the book fell to nothing for
     * nineteen centuries, the bank drained from $330M of equity to $195M on
     * payroll alone, and the first sector that then borrowed and defaulted
     * - the mines, $420M written down - took it under, twice, and it never
     * fully stood up again. Before the template the same bank had been
     * carried by a landlord borrowing a billion; that was fortune, not a
     * rule, and the rule is this: idle reserves are placed at the world's
     * base rate. The mirror of the wholesale funding line, and declared to
     * the audit the same way - income from abroad, the way a foreign
     * coupon is income to it. (Both lines are the central bank's now, and
     * declared as money it made and destroyed: Scope.MONEY.)
     */
    private double placementIncome;
    private double openingEquity;

    /* ------------------------------- the month ------------------------------- */

    /**
     * The fourth book: what foreigners have borrowed to take abroad.
     *
     * It has to be a book rather than a hole in the cash, and that is not a
     * bookkeeping nicety. lend() is three lines - cash -= amount - and equity
     * is cash plus book less liabilities, so lending through it would have
     * dropped the bank's equity by the whole principal and walked it into
     * resolveIfFailed() on the first loan. The money is gone; the CLAIM is the
     * asset. See CapitalFlows.carryTakeMonth().
     */
    private double carryBook;
    private double carryLent, carryRepaid, carryInterest;

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
        this.mortgageBook = 0;
        this.mortgageWeighted = 0;
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

    /**
     * ...and the insured mortgages inside the businesses' book (0.7.11), which
     * the weight table shows at RISK_INSURED_MORTGAGE. Game.refreshBank()
     * sets it with the weighted book; a Bank built by hand holds none.
     */
    public void setMortgageBook(double insured, double weighted) {
        this.mortgageBook = Math.max(0, Math.min(insured, sectorBook));
        this.mortgageWeighted = Math.max(0, Math.min(weighted, sectorWeighted));
    }

    /** The insured mortgages it holds, part of getSectorBook(). */
    public double getMortgageBook() { return mortgageBook; }

    /**
     * THE CITY PAYS AN INSURED LOSS (0.7.11): cash from the treasury for the
     * part of a borrower's write-down that came off an insured mortgage. Its
     * book loses that balance at the next refresh; the cash replaces it, so
     * neither its income nor its equity moves - the loss is the insurer's.
     * A transfer between two pools, which the money audit sees cancel.
     */
    public void receiveInsuranceClaim(double amount) {
        if (!(amount > 0)) return;
        cash += amount;
        insuranceClaims += amount;
    }

    /** What the treasury paid it on insured mortgages this month. */
    public double getInsuranceClaims() { return insuranceClaims; }

    /* ---------------------------------------------------------------- carry */

    /**
     * Lend to a foreigner who is about to take it abroad.
     *
     * Cash out, book up: the claim replaces the money, so equity does not move
     * on the loan itself. What DOES move is the strain - the book is bigger
     * against the same capital - and that is the feedback that closes the
     * trade's own window, because the carry trade is lent only headroom(),
     * the room below EASY_STRAIN (until 0.7.7 the strain premium also raised
     * the rate the carry spread is measured against).
     */
    public void lendCarry(double amount) {
        if (amount <= 0) return;
        cash -= amount;
        carryBook += amount;
        carryLent += amount;
    }

    /** ...and they bring it home. Book down, cash back. */
    public void repayCarry(double amount) {
        if (amount <= 0) return;
        double paid = Math.min(amount, carryBook);
        cash += paid;
        carryBook -= paid;
        carryRepaid += paid;
    }

    /**
     * The coupon, which arrives from abroad.
     *
     * Counted in interestEarned like any other, and ALSO in its own counter,
     * because MoneyAudit's "+ bank InterestEarned" line is DOMESTIC and this
     * money crossed a border. The audit subtracts it there and books it as
     * income, exactly as it did for the bank's placements abroad until they
     * came home to the central bank in 0.7.0.
     */
    public void takeCarryInterest(double amount) {
        if (amount <= 0) return;
        cash += amount;
        interestEarned += amount;
        carryInterest += amount;
    }

    /**
     * The carry book, set from the stock that owns it.
     *
     * NOT saved, and it does not need to be: the bank's other three books are
     * rebuilt every month from the debts themselves, and this one is rebuilt
     * the same way from CapitalFlows' carry stock, which IS saved. One source
     * of truth, and a reload cannot disagree with a played month. Exactly the
     * pattern setForeignDeposits() already uses for hot money.
     */
    public void setCarryBook(double amount) { this.carryBook = Math.max(0, amount); }

    public double getCarryBook()     { return carryBook; }
    public double getCarryLent()     { return carryLent; }
    public double getCarryRepaid()   { return carryRepaid; }
    public double getCarryInterest() { return carryInterest; }

    /* =====================================================================
       WHAT A LOAN COSTS, AND SO WHAT IT IS PRICED AT (0.7.7)

       Jerus: "lets make banks realistic, and remember, its a business, it
       wants to make money."

       Until 0.7.7 a loan here was the city's own rate, floored on this
       bank's blended cost of funds plus MIN_MARGIN, plus ratePremium(): up to
       eighteen points on every rate in the city once the book passed 80% of
       capacity, and the full eighteen with no branch, no equity or a failed
       bank. A strained bank raising every rate caused the defaults, the
       write-offs and the lost equity that strained it further - a 90-year
       played city (0.7.5) failed its bank twice that way, $450M and $1.3B
       written off - and no bank anywhere prices like that. A real one adds
       up four costs:

         THE FUNDS-TRANSFER PRICE - what the next dollar costs it for the
         loan's term: the policy rate while it holds spare reserves (the
         dollar would otherwise earn the policy rate at the central bank),
         the window's rate while it borrows there, blended by how it is
         funded, plus the city's term premium for the loan's maturity. NOT
         what its deposits cost it: the profit on cheap deposits is the
         deposit side's margin, not a gift to its borrowers.
         THE RUNNING COSTS - payroll and upkeep per dollar lent, over a year.
         THE EXPECTED LOSS - what a sound book loses through the cycle,
         BASE_LOSS_RATE; what a particular borrower adds to it is that
         borrower's own spread, not the bank's last write-off
         (expectedLossRate() says why).
         THE CAPITAL CHARGE - the equity the loan ties up, at the return the
         market prices the bank's shares on, over what that money would have
         cost as debt.

       Each is a public getter, so the Bank tab can print the build-up. PRIME
       is the four for a sound business borrowing for a business loan's term
       at RISK_BUSINESS; a business pays prime plus its own expected loss
       off the curve and its record (BusinessDebtManager.priceSector(),
       PRICING FROM THE CURVE, since 0.7.8), a household the four at
       RISK_HOUSEHOLD plus its months of income owed (Household.settle()),
       the carry trade the three without the expected loss, at RISK_CARRY
       and short (carryRate()), and since 0.7.11 a landlord's insured
       mortgage the funds-transfer price at ten years and the running costs,
       insured so no loss, and - weighing nothing - only the capital the
       leverage ratio asks of every dollar lent (round 2; capitalPerDollar(),
       insuredMortgageRate()). The city's own paper is NOT
       priced here: it keeps its market price, the dial plus its own credit
       spreads (DebtManager.priceAt()).

       STRUCK AT THE CLOSE AND CARRIED IN THE SAVE. The running costs and
       the funding blend read flows, so closeMonth() strikes
       them once and the save carries them (lastMonthToSave(),
       pricingHistoryToSave()): a price that was quoted is not a figure a
       reload can recompute. Only the policy rate is read live, because the
       dial moves at the top of the month, before anything is priced.
       ===================================================================== */

    /*
     * THE CAPITAL A LOAN IS PRICED TO CARRY was CAPITAL_RATIO plus a fixed
     * three-point CAPITAL_BUFFER in 0.7.7 - TARGET_CAPITAL_RATIO, 11% - one
     * number standing in until the bank chose its own. Since 0.7.8 it is the
     * bank's own target, capitalTarget(): the minimum plus a buffer sized on
     * the worst year of losses it has lived through (WHAT IT HOLDS, below).
     */

    /** What a sound loan is expected to lose a year through the cycle, as a share of the book: 0.4%, about what Canada's big banks provision for credit losses in a normal year (RBC, 2025). */
    public static final double BASE_LOSS_RATE = .004;

    /** Months of payroll and upkeep the running costs are measured over: a year, so one month's building bill is not a price. */
    public static final int COST_WINDOW_MONTHS = 12;

    /** The term prime is struck at: a business loan's, BusinessDebtManager.LOAN_TERM_MONTHS, which takes the short end's term premium. */
    public static final int PRIME_TERM_MONTHS = BusinessDebtManager.LOAN_TERM_MONTHS;

    /**
     * The least a lender takes for writing the loan at all.
     *
     * SINCE 0.7.7 NOT THE BANK'S MARGIN: a loan here is priced from its costs
     * (the running costs are what this point used to stand for), and this
     * survives only in the floor under the city's own paper -
     * DebtManager.floorRate(), the city never borrowing under what the bank's
     * money costs it plus this. It was the operating margin under every
     * loan: the point that pays for reading the file, keeping the ledger and
     * being wrong occasionally.
     */
    public static final double MIN_MARGIN = .01;

    /* ----------------------------- the four parts ----------------------------- */

    /**
     * The share of the bank's marginal money that comes from the window, 0 to
     * 1: struck at the close from how the month ended funded - what it owed
     * the window over everything it had borrowed, 0 while it holds reserves -
     * and 1 with no branch or a failed bank, whose marginal money is the
     * window's by construction. A bank that has never closed a month is
     * taken to be at the window, where a city with no bank yet is.
     *
     * STRUCK, NOT READ LIVE, even for the branch count: the load path prices
     * before the bank has been refreshed off the city, and a live read would
     * see no branches there and quote a reloaded city a different prime.
     */
    public double windowShare() {
        return lastWindowShare;
    }

    /**
     * THE FUNDS-TRANSFER PRICE: what a dollar lent for this many months costs
     * the bank - the policy rate, plus the window's penalty on the share of
     * its money that is the window's, plus the city's term premium for the
     * term (DebtManager.termPremium(), the table the city's own curve is
     * built on; nothing inside a year).
     */
    public double fundsTransferPrice(double policyAnnual, int months) {
        return Math.max(0, policyAnnual) + CentralBank.WINDOW_PENALTY * windowShare()
                + DebtManager.termPremium(months);
    }

    /**
     * THE RUNNING COSTS: the last year's payroll and upkeep over the book they
     * served, a year - or over what the bank's capital could carry at its
     * own target (capitalTarget(); a fixed 11% until 0.7.8), whichever is
     * larger. The second is the founding
     * guard: a book of almost nothing would put a whole branch's wages on
     * every dollar of it, so a bank is priced at its standard cost at
     * capacity until its book is the bigger number. Struck at the close.
     *
     * ...AND ITS CAPITAL IS NEVER COUNTED AT LESS THAN ITS BRANCHES WERE
     * FOUNDED WITH (paidInPerBranch each). Measured without that floor: a bank
     * that had lost its equity and its book together priced its wages at
     * 115% a year on every loan (seed 0, month 200) - the running costs
     * doing what the strain premium did, a bank in trouble raising every
     * rate. What a counter is built to carry does not fall when the bank
     * loses money.
     */
    public double runningCostRate() { return lastRunningCost; }

    /**
     * THE EXPECTED LOSS: BASE_LOSS_RATE, what a sound book loses a year
     * through the cycle. The borrower's own risk is priced where it belongs,
     * in its spread - for a business its own expected loss off the curve
     * over this base, and its restructure surcharge (BusinessDebtManager,
     * PRICING FROM THE CURVE, since 0.7.8), RISK_SLOPE on its months owed for a
     * household - and not by this bank's record.
     *
     * WHY NOT THE BANK'S OWN WRITE-OFFS, which is what this was first built
     * from. A real bank prices expected loss from the borrower's risk and
     * long-run default statistics, not from its own last write-off; and here,
     * until 0.7.8's partial defaults, a whole sector was one borrower, so the
     * bank's own record was lumpy by construction - a restructure wrote a
     * sector down to 60% of its assets - and one sector can still be half the
     * book. Two versions were measured:
     *   - a five-year window (the first brief): seed 0 priced prime at 77.9%
     *     in month 12 and went to a dial of 87.6% and a price level of 9.43 -
     *     the loop this pricing exists to remove, back through its fourth part;
     *   - a record fading over twenty years and opened on five years of this
     *     rate: a young city's first sector default (month 8 of a new game,
     *     $4.8M of a $17M book) put it at 11.1%, and after every crisis it
     *     held prime three to five points over the dial for decades. Over the
     *     default run's eight seeds the city ended at 122,287 people on
     *     average against 148,668, and at 17,123 against 33,350 by month
     *     1,300; one seed stalled at 32,000.
     * The orchestrator's call (2026-09-23) was this one: the through-the-cycle
     * base in prime, the borrower's risk in its spread. What the bank has
     * actually lost is still in the history (bankWriteOffs against bankLent),
     * and since 0.7.8 its allowance sets aside for each borrower's own risk,
     * off the curve (THE BANK AS A BUSINESS WITH ITS CAPITAL).
     */
    public double expectedLossRate() { return BASE_LOSS_RATE; }

    /** The return the bank's owners are priced to want: Equity.requiredYield() at the world's rate, 12.5% at the defaults. */
    public static double requiredReturn() {
        return Equity.requiredYield(DebtManager.WORLD_BASE_RATE);
    }

    /**
     * THE CAPITAL CHARGE: the equity a loan of this risk ties up at the
     * bank's own capital target (capitalPerDollar(); the target is
     * capitalTarget() since 0.7.8, and before that the minimum plus a fixed
     * three points), times what that equity costs over what the same money
     * would have cost as debt - requiredReturn() less the funds-transfer
     * price. Never below zero: past a policy rate of 12.5% the owners' money
     * would be the cheaper money, and a bank does not lend under the cost of
     * its borrowing because of it.
     */
    public double capitalCharge(double policyAnnual, int months, double riskWeight) {
        return capitalPerDollar(riskWeight)
                * Math.max(0, requiredReturn() - fundsTransferPrice(policyAnnual, months));
    }

    /**
     * THE EQUITY A DOLLAR LENT TIES UP at the bank's own target: its risk
     * weight times capitalTarget(), and never less than the leverage
     * requirement on the same dollar, leverageTarget() (0.7.11, round 2).
     * The leverage requirement counts a dollar whatever it weighs, so a
     * loan that weighs nothing still needs the owners' money behind it.
     * Jerus: "The bank keeps equity against its mortgages and prices that
     * capital into the mortgage rate."
     *
     * Only the insured mortgage reaches the floor: every other loan weighs
     * 100%, and 100% of the target is more than leverageTarget(), which is
     * 37.5% of it (LEVERAGE_RATIO_MIN / CAPITAL_RATIO). This is the one
     * formula, not a second one - the weight times the ratio, floored at the
     * leverage requirement per dollar.
     */
    public double capitalPerDollar(double riskWeight) {
        return Math.max(Math.max(0, riskWeight) * capitalTarget(), leverageTarget());
    }

    /** A loan of this term and risk weight: the four parts, added up. */
    public double loanRate(double policyAnnual, int months, double riskWeight) {
        return fundsTransferPrice(policyAnnual, months) + runningCostRate()
                + expectedLossRate() + capitalCharge(policyAnnual, months, riskWeight);
    }

    /** PRIME: what a sound business pays - the four parts at RISK_BUSINESS, for a business loan's term. */
    public double prime(double policyAnnual) {
        return loanRate(policyAnnual, PRIME_TERM_MONTHS, RISK_BUSINESS);
    }

    /**
     * AN INSURED MORTGAGE (0.7.11): what the money costs for its term -
     * the funds-transfer price at Mortgage.MORTGAGE_TERM_MONTHS, the curve's
     * ten-year point - running the bank, and the capital it ties up; no
     * expected loss, because the loan is insured. The same parts prime is
     * built of, as the carry trade's price is (carryRate()); no second
     * formula. What the landlord's own risk is worth does not enter it - its
     * leverage, its curve, its record - because the lender does not carry
     * it; the landlord's defaults are still its own
     * (BusinessDebtManager.defaultSlice()).
     *
     * THE CAPITAL PART SINCE ROUND 2 of 0.7.11. It weighs
     * RISK_INSURED_MORTGAGE, nothing, so under the risk weights alone the
     * charge was nothing. Under the leverage ratio a dollar of it still
     * needs leverageTarget() of equity (capitalPerDollar()), at the owners'
     * return over the money's cost: a few tenths of a point.
     */
    public double insuredMortgageRate(double policyAnnual) {
        int term = Mortgage.MORTGAGE_TERM_MONTHS;
        return fundsTransferPrice(policyAnnual, term) + runningCostRate()
                + capitalCharge(policyAnnual, term, RISK_INSURED_MORTGAGE);
    }

    /** What a household's credit line starts from: the four parts at RISK_HOUSEHOLD, revolving, so no term premium. Household.settle() adds its months of income owed. */
    public double householdRate(double policyAnnual) {
        return loanRate(policyAnnual, 0, RISK_HOUSEHOLD);
    }

    /**
     * What a good credit pays to borrow here: prime, since 0.7.7. One
     * definition, so the carry trade and the screens cannot drift. It was
     * the policy rate floored on the bank's cost of funds plus MIN_MARGIN,
     * plus ratePremium().
     */
    public double lendingRate(double policyAnnual) {
        return prime(policyAnnual);
    }

    /**
     * What the carry trade pays to borrow here: the same costs, at RISK_CARRY
     * and short, since the money is taken abroad and wanted back on demand -
     * with NO expected loss, because Jerus's call is that a carry borrower
     * never defaults (see RISK_CARRY: its weight does capacity work, which
     * is the capital charge, and no loss-absorbing work). Priced off prime,
     * the carry trade had nothing left to earn at any dial the world's 2%
     * could beat - a 4,000-month run lent it $242bn where 0.7.6 lent $21tn -
     * and it is the currency's one outflow that answers the rate. Until 0.7.7
     * it paid lendingRate(): the dial floored on the cost of funds plus
     * MIN_MARGIN, plus the strain premium.
     */
    public double carryRate(double policyAnnual) {
        return fundsTransferPrice(policyAnnual, 0) + runningCostRate()
                + capitalCharge(policyAnnual, 0, RISK_CARRY);
    }

    /* ---------------------- what the four parts remember ---------------------- */

    /** The funding blend and the running-cost rate, struck at the last close. */
    private double lastWindowShare = 1;
    private double lastRunningCost;

    /**
     * The record the running costs are struck from: a year of payroll and
     * upkeep beside the book they served, in rings indexed by closed months.
     */
    private final double[] costRing = new double[COST_WINDOW_MONTHS];
    private final double[] costBookRing = new double[COST_WINDOW_MONTHS];
    private int pricedMonths;

    /** Adds the month that has just closed to the rings and strikes the two parts that read flows. Called by closeMonth() only. */
    private void strikePrices() {
        costRing[pricedMonths % COST_WINDOW_MONTHS] = payroll + upkeep;
        costBookRing[pricedMonths % COST_WINDOW_MONTHS] = getBook();
        pricedMonths++;

        int costFilled = Math.min(pricedMonths, COST_WINDOW_MONTHS);
        double costs = 0, served = 0;
        for (int i = 0; i < costFilled; i++) { costs += costRing[i]; served += costBookRing[i]; }
        double capital = Math.max(Math.max(0, equity()), branches * paidInPerBranch);
        double carried = Math.max(served / costFilled, capital / capitalTarget());
        lastRunningCost = carried > 0 ? costs / costFilled * 12 / carried : 0;

        double owed = borrowings();
        lastWindowShare = branches <= 0 || inResolution ? 1
                : owed > 0 ? Math.min(1, Math.max(0, wholesaleFunding() / owed)) : 0;
    }

    /** The record, for the save: the count, then the two cost rings. */
    public double[] pricingHistoryToSave() {
        double[] out = new double[1 + 2 * COST_WINDOW_MONTHS];
        int i = 0;
        out[i++] = pricedMonths;
        for (double[] ring : new double[][] { costRing, costBookRing }) {
            System.arraycopy(ring, 0, out, i, ring.length);
            i += ring.length;
        }
        return out;
    }

    /** ...and back. A save from before 0.7.7 carries none, and the record starts empty: the struck parts come back with lastMonthToSave() and the rings refill a month at a time. */
    public void restorePricingHistory(double[] in) {
        if (in == null || in.length != 1 + 2 * COST_WINDOW_MONTHS) return;
        int i = 0;
        pricedMonths = (int) Math.round(in[i++]);
        for (double[] ring : new double[][] { costRing, costBookRing }) {
            System.arraycopy(in, i, ring, 0, ring.length);
            i += ring.length;
        }
    }

    /**
     * WHAT THE NEXT DOLLAR OF LENDING COSTS THIS BANK TO FUND.
     *
     * The number the whole credit system was missing, and the reason it could
     * not make money. Until 2026-09-13 every rate in the city was struck off
     * the RISK-FREE rate - lendingRate() was literally `riskFree +
     * ratePremium()`, and ratePremium() was zero whenever strain was under
     * EASY_STRAIN. Meanwhile this bank funded itself at riskFree +
     * FUNDING_SPREAD + FUNDING_STRETCH x reach (the market's price, until
     * 0.7.0). So a comfortable bank lent at
     * two points BELOW what the money cost it, by construction, on every
     * dollar its deposits did not cover.
     *
     * Nothing ever compared the two. fundingRate() was read in exactly one
     * place outside this class, and that place was the screen that prints it.
     *
     * MEASURED over two 4,000-month runs with no city debt in them at all:
     * a net interest margin of 1.70% and 0.71% a year against a real bank's
     * three to four, 44% and 58% of all months at a loss, and of those losses
     * 86% and 78% would have been profits with the funding line removed.
     * Payroll caused 4% and 15% of them; write-offs caused under one.
     *
     * TWO TRANCHES, CHEAPEST FIRST, the same two fundToCover() charges for.
     * Inside what its branches have gathered the next dollar is a saver's and
     * costs whatever savers are being paid. Past them it is the central bank's
     * window, at the rate it charged this bank last month (wholesale paper at
     * the market's price, until 0.7.0).
     *
     * LAST MONTH'S PRICE, deliberately. fundingRate is struck at the bottom of
     * the month, after every loan has moved; a rate quoted this morning cannot
     * know it. A real bank prices off yesterday's funding curve for the same
     * reason, and the standing rule applies - a flow cannot be read from the
     * state a month started in.
     *
     * ZERO WITH NO BANK: a city with no branch has no deposits and no window
     * account of its own to blend. Its loans are priced by fundsTransferPrice()
     * like everyone's since 0.7.7, with the window as the marginal money.
     *
     * SINCE 0.7.7 THIS PRICES NO LOAN. A loan's rate is built from
     * fundsTransferPrice() and the three costs beside it; this blended cost is
     * what the city's own paper is floored on (DebtManager.floorRate()) and
     * what the Bank tab reports.
     */
    public double marginalCostOfFunds() { return lastCostOfFunds; }

    /** What money cost this bank over the month that just closed. */
    private double lastCostOfFunds;

    /**
     * The two tranches, blended at the weights the bank actually used them.
     *
     * BLENDED RATHER THAN MARGINAL, and the first version was marginal: "inside
     * the gathered deposits the next dollar is a saver's, past them it is
     * wholesale paper". Theoretically the better question, and a step function
     * in practice. A bank sitting near its deposit ceiling crosses it both ways
     * most months, so the floor under every rate in the city flipped between
     * the deposit rate and the wholesale rate - measured across twelve
     * consecutive months of BankCheck's fixture at 4.9%, 8.0%, 11.0%, 1.9%,
     * 4.9%, 5.4%, 9.2%, 12.9%, 13.6%. A cost of credit that moves eleven points
     * month to month is not a signal a player can act on, it is noise.
     *
     * The average cost of the money it is using has no such cliff, moves
     * continuously as the bank reaches further past its deposits, and is what a
     * bank's own income statement means by the phrase.
     */
    private double blendedCostOfFunds() {
        if (branches <= 0 || inResolution) return 0;
        double owed = borrowings();
        if (owed <= 0) return 0;
        double wholesale = Math.min(1, Math.max(0, wholesaleFunding() / owed));
        return Math.max(0, depositRate) * (1 - wholesale)
                + Math.max(0, fundingRate) * wholesale;
    }

    /** Clears the month's flows. Called at the top of a month, before anything moves. */
    public void startMonth() {
        /*
         * THE MONTH THAT ENDED GOES ON FILE FIRST (0.7.9), whole: everything
         * booked after its close is in it by now, and nothing below has
         * cleared it yet. Only a month's lines - not a new bank's, nor an
         * older save's that carried none (isMonthKnown()).
         */
        if (monthKnown) fileStatement();
        /*
         * WHAT LANDED AFTER LAST MONTH'S CLOSE, carried into this month's
         * profit before the flows it is read from are cleared - see
         * lateProfit(). On the load path the flows are gone and the save
         * carried the figure (restoreLateProfit()).
         */
        carriedLate = closedThisMonth ? profitBeforeTax() - struckProfit : restoredLate;
        restoredLate = 0;
        closedThisMonth = false;
        taxPaid = 0;
        interestEarned = 0;
        internalInterest = 0;
        writeOffs = 0;
        payroll = 0;
        upkeep = 0;
        lentToHouseholds = 0;
        repaidByHouseholds = 0;
        fundingCost = 0;
        placementIncome = 0;
        insuranceClaims = 0;
        depositInterestToHouseholds = 0;
        depositInterestToSectors = 0;
        depositInterestToForeign = 0;
        capitalInjected = 0;
        capitalFromHome = 0;
        dividendsPaid = 0;
        tradingIncome = 0;
        markChange = 0;
        paperGains = 0;
        paperBoughtFromHouseholds = 0;
        paperSoldToCentralBank = paperBoughtFromCentralBank = 0;
        accountFees = loanFeesPaid = loanFeesOwed = 0;
        hotMoneyIn = 0;
        carryLent = carryRepaid = carryInterest = 0;
        hotMoneyOut = 0;
        bailoutReceived = 0;
        foundingSettlement = 0;
        resolutionLossThisMonth = 0;
        /*
         * ...AND 0.7.8'S: the allowance each book opens the month holding,
         * which the month's write-offs are drawn against first; what each
         * book is written off by; the provision's two halves; and the owners'
         * month - its slot in the year's record opened at nothing, the
         * shares bought back and issued cleared.
         */
        openingAllowance = getAllowance();
        openingSectorAllowance.clear();
        openingSectorAllowance.putAll(sectorAllowance);
        openingHouseholdAllowance = householdAllowance;
        writtenOffBySector.clear();
        householdWrittenOff = 0;
        allowanceUsed = 0;
        sharesBoughtBack = 0;
        sharesIssued = 0;
        payoutProfit = payoutExcess = payoutOverTarget = 0;
        // ...and 0.7.9's: the interest by who paid it, the treasury's
        // buybacks between two presses, an older save's opened allowance.
        interestFromBusinesses = interestFromCity = interestFromHouseholds = discountAccreted = 0;
        treasuryBuybackGain = 0;
        allowanceOpened = 0;
        payoutMonths++;
        dividendRing[payoutMonths % YEAR_MONTHS] = 0;
        buybackRing[payoutMonths % YEAR_MONTHS] = 0;
        // What the shareholders had before the month happened, so the statement
        // can show the movement rather than only the closing figure.
        openingEquity = equity();
        monthKnown = true;
    }

    /* ------------------------------ the arithmetic ------------------------------ */

    /**
     * Everything lent, carry included.
     *
     * The carry book HAS to be in here, and it is the difference between a
     * working mechanic and a bank that dies on its first foreign loan.
     * totalAssets() is cashReserves() + getBook() + securities, so a loan that
     * took cash out and put nothing in would cut equity by the whole principal
     * and walk the bank into resolveIfFailed(). The money is gone; the claim on
     * the borrower is the asset that replaces it.
     */
    public double getBook() { return sectorBook + cityBook + householdBook + carryBook; }

    /**
     * The book as CAPACITY sees it: risk- and maturity-weighted.
     *
     * Deliberately a different name from getBook() rather than a flag on it.
     * The two answer different questions and a city stuffed with treasury bills
     * has them a long way apart - which is the entire point of the change.
     */
    public double getWeightedBook() {
        return sectorWeighted + cityWeighted + householdWeighted
                + carryBook * RISK_CARRY
                + Math.abs(securities) * RISK_EQUITY;
    }

    /* =====================================================================
       THE TRADING DESK

       Jerus, 2026-09-10 (night): "liquidity, that's going to be an issue, we
       need to solve it realistically via bank something, right?" Right. The
       bank makes the market in the city's shares - see Exchange - and what
       that leaves on its balance sheet is here: an inventory of shares at
       the price it quotes, an asset like the loan book and a riskier one, and
       a trading result that is income like the interest. A bank that holds
       the city's shares through a crash loses money the way a bank does;
       that is the business it was asked for.
       ===================================================================== */

    /** What a dollar of shares on the desk weighs against capital. Dearer than a loan. */
    public static final double RISK_EQUITY = 1.50;

    /** The desk's inventory, at the exchange's mark. Signed: negative when short. */
    private double securities;

    /**
     * The trading result so far this month: cash from what it sold less cash
     * for what it bought, plus the change in what it holds at the mark, plus
     * the dividends its inventory was paid. Exactly the amount its equity
     * moved by on the desk's account, so the articulation holds.
     */
    private double tradingIncome;

    /**
     * The part of the trading result that is the re-mark: every change in
     * what the inventory is carried at this month, summed. Usually the
     * largest term in tradingIncome and the one the statement could not
     * show, so the desk's opened lines did not add up to the figure above
     * them. Jerus: "the bank, just explain to me the trading desk, cause a
     * bunch of times it's losing billions of dollars due to the trading
     * desk." Cleared with tradingIncome; the identity is
     * tradingIncome = cash sold - cash bought + dividends + buybacks + this.
     */
    private double markChange;

    /** The exchange re-marks the inventory. The change is income; the level is an asset. */
    public void markSecurities(double value) {
        tradingIncome += value - securities;
        markChange += value - securities;
        securities = value;
    }

    /** The desk paid cash for shares. The shares are on the mark; the cash has gone. */
    public void deskPays(double cash) {
        if (cash <= 0) return;
        this.cash -= cash;
        tradingIncome -= cash;
    }

    /** ...and was paid for shares it sold. */
    public void deskReceives(double cash) {
        if (cash <= 0) return;
        this.cash += cash;
        tradingIncome += cash;
    }

    /** Dividends on the inventory: income, and cash. Negative when the desk is short and owes them. */
    public void receiveDividend(double amount) {
        if (amount == 0) return;
        this.cash += amount;
        tradingIncome += amount;
    }

    /** The load path puts the mark back without calling it income. */
    public void restoreSecurities(double value) { securities = value; }

    public double getSecurities()    { return securities; }
    public double getTradingIncome() { return tradingIncome; }

    /** What re-marking the inventory did to this month's trading result. See markChange. */
    public double getMarkChange()    { return markChange; }

    /* ------------------ the desk held to its capital (0.7.8) ------------------ */

    /**
     * HOW MANY OF A COMPANY'S SHARES THE DESK MAY STILL BUY ON THE CAPITAL IT
     * HAS: as many as leave the bank at or over its target (targetEquity())
     * with them on its books - the shares carried at the weight the weighted
     * book already gives the desk, RISK_EQUITY, and nothing new. Its selling
     * is not limited; a desk with no capital to spare stops bidding and goes
     * on selling what it holds. Jerus, 2026-09-24: "Trading desk held to its
     * capital."
     *
     * THE REAL-WORLD VERSION is a bank's trading book held against capital -
     * the Basel market-risk requirement (BCBS, "Minimum capital requirements
     * for market risk", 2019): a position a desk holds for dealing needs
     * capital behind it like a loan, and a desk whose bank has none to spare
     * is not allowed the position. Here the charge is the weighted book's own
     * RISK_EQUITY, so the rule adds no new weight; what it adds is that the
     * desk may not buy past it.
     *
     * A purchase of x shares at `price` carried at `mark` moves equity by
     * x(mark - price) - the cash is gone, the shares are on the books at the
     * mark - and the weighted book by x mark RISK_EQUITY, so it keeps the bank
     * at its target while
     *
     *     x <= spare / (price - mark + capitalTarget() x RISK_EQUITY x mark)
     *
     * where spare is its spare capital on the weighted book
     * (spareOnRisk(inventory)). The divisor is positive
     * whenever the target's weight on a share, capitalTarget() x RISK_EQUITY
     * (at least 15.75% at the game's dials), is more than half the spread:
     * the bid is never under the mark by more than that (Exchange.bid(),
     * mark()).
     *
     * ...AND ON THE LEVERAGE TARGET since round 2 of 0.7.11: the same
     * inequality with the exposure in place of the weighted book and
     * leverageTarget() in place of capitalTarget() x RISK_EQUITY, on
     * spareOnLeverage(inventory). The tighter of the two holds.
     *
     * Until 0.7.8's round 4 the desk's inventory was weighted against capital
     * but not limited by it: its limits were shares of the bank's equity at
     * fair value (Exchange.POSITION_LIMIT, BOOK_LIMIT), which a bank at its
     * target meets out of capital it has already lent against. Measured in
     * round 3, a month's re-mark of that inventory was a median 121-148% of
     * the equity the bank opened with in the months it failed on it - 28 of
     * the default run's 101 failures, 47 of the autopilot's 97 and 124 of the
     * 136 held at a 10% dial.
     *
     * @param inventory the desk's whole inventory at the mark now - the
     *                  securities line lags the month's deals until the
     *                  exchange re-marks it (Exchange.markToMarket())
     * @param price     what the desk pays a share: the bid
     * @param mark      what it will carry the share at (Exchange.mark())
     * @return shares; none when it has no capital to spare
     */
    public double deskCanCarry(double inventory, double price, double mark) {
        double spare = spareOnRisk(inventory);
        if (!(spare > 0)) return 0;
        double perShare = price - mark + capitalTarget() * RISK_EQUITY * Math.max(0, mark);
        double onRisk = perShare > 0 ? spare / perShare : Double.POSITIVE_INFINITY;
        /*
         * ...AND ON THE LEVERAGE TARGET (0.7.11, round 2): the same
         * inequality with the exposure in place of the weighted book. A
         * share adds its mark to the exposure unweighted, so
         *     x <= spareL / (price - mark + leverageTarget() x mark).
         * The tighter of the two holds.
         */
        double spareL = spareOnLeverage(inventory);
        if (!(spareL > 0)) return 0;
        double perShareL = price - mark + leverageTarget() * Math.max(0, mark);
        double onLeverage = perShareL > 0 ? spareL / perShareL : Double.POSITIVE_INFINITY;
        return Math.min(onRisk, onLeverage);
    }

    /** How much lighter the weighting makes the book. 0 when nothing is lent. */
    public double weightingRelief() {
        double gross = getBook();
        return gross > 0 ? 1 - getWeightedBook() / gross : 0;
    }

    /**
     * The most the bank can lend before it is funding itself at the central
     * bank's window (abroad, until 0.7.0).
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
        return Math.min(capitalLimit(), fundingLimit());
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

    /*
     * THE WHOLESALE FUNDING COST WAS SPLIT BY WHOSE MONEY IT WAS until 0.7.0 -
     * fundingCostAbroad and fundingCostAtHome, on the curve that decides
     * how much of the bank's capital the city's own savers own
     * (domesticCapitalShare()). Declaring all of it foreign had put $592,109
     * a month of interest on the current account against $234,348 of
     * exports and run the exchange rate to its ceiling; the split fixed that.
     * The question is gone with the market: the window's interest is paid to
     * the central bank, all of it at home, and declared as money destroyed.
     */

    /** The share of the bank's funding that could leave at any time. */
    public double hotFundingShare() {
        double all = depositsGathered();
        return all > 0 ? Math.min(1, Math.max(0, foreignDeposits) / all) : 0;
    }

    /** What its capital supports, on the risk-weighted book - at the book's present mix, under the larger of the two requirements since round 2 of 0.7.11 (weightedBookSupportedBy()). */
    public double capitalLimit() {
        return weightedBookSupportedBy(equity());
    }

    /**
     * THE WEIGHTED BOOK THIS MUCH EQUITY SUPPORTS: equity over CAPITAL_RATIO
     * while the risk-based minimum is the larger. When the leverage
     * requirement is the larger (0.7.11, round 2), the book grows at the mix
     * it has, so the weighted book the equity supports is the one at which
     * the leverage minimum would be met:
     *     weighted x equity / minimumEquity().
     * Capacity and strain read it, and so does the value of another branch
     * (capacityWith()). With nothing weighted there is no mix to hold, and
     * it is equity over CAPITAL_RATIO as it was.
     */
    private double weightedBookSupportedBy(double equity) {
        double weighted = getWeightedBook();
        if (leverageBinds() && weighted > 0) return Math.max(0, equity) * weighted / minimumEquity();
        return Math.max(0, equity) / CAPITAL_RATIO;
    }

    /** True when it is the capital that binds rather than the funding. */
    public boolean capitalBound() {
        return capitalLimit() <= fundingLimit();
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
        // ...and what the city has put in over its life (0.7.9), on the end.
        return new double[]{ failures, resolutionLossLifetime, inResolution ? 1 : 0, bailoutsLifetime };
    }

    public void restoreSolvency(double[] state) {
        if (state == null || state.length < 3) return;
        failures               = (int) state[0];
        resolutionLossLifetime = state[1];
        inResolution           = state[2] != 0;
        // A save from before 0.7.9 counts the city's rescues from its load.
        bailoutsLifetime       = state.length > 3 ? state[3] : 0;
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
     *
     * STILL DECLARED AS ABROAD SINCE 0.7.0, though the bank's wholesale
     * lender is the central bank's window now, not creditors outside the
     * city: the hole arrives as MoneyAudit's "+ bank ResolutionLoss" from
     * outside, and the window is repaid out of it at the next settle. Who
     * should absorb a failed bank now - the central bank as lender of last
     * resort, the depositors, the treasury - is Jerus's question, open on the
     * list (the-central-bank-opens.md section 4); the code is as it was.
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
        // The larger of the two minimums since round 2 of 0.7.11.
        double byBook = minimumEquity() * RESOLUTION_EXIT_BUFFER;
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
        // In today's money - the constant is the founding unit's, and a
        // reformed city's bank read the unreformed floor here, a hundred
        // times what one of its branches is capitalised with, and never left
        // resolution. Found by DenominationCheck on 2026-09-11, the day the
        // bank first earned its way back (see placementIncome).
        double floor = branches > 0 ? paidInPerBranch : 0;
        return Math.max(byBook, floor);
    }

    /**
     * What it would take to put the bank back on its feet.
     *
     * A FAILED bank is asked for resolutionExitEquity() - the minimum with
     * RESOLUTION_EXIT_BUFFER over it, never less than one branch's capital -
     * which is what lifts the freeze.
     *
     * A STANDING bank is asked for nothing while it holds the city's minimum,
     * CAPITAL_RATIO of its weighted book - between the minimum and its own
     * target it rebuilds out of the profit its payout policy keeps - and,
     * once it is under the minimum, for what takes it back to its own target
     * (0.7.8). The trigger is the city's rule; the amount is the bank's,
     * because at the minimum the bank's own rule lets it lend nothing new
     * (lendingGrowthLimit()). Asked for the bare minimum, a bank the city
     * topped up sat on the line with its lending shut: held at a 10% dial,
     * seed 0's was put back at exactly 8.00% month after month and spent 538
     * months lending only to keep its borrowers going. A regulator's capital
     * plan restores a bank to its buffers, not to the edge of them. Until
     * 0.7.8 a standing bank was asked for 12% - the minimum times the exit
     * buffer - whenever it was under that, which the Bank tab read as "under
     * its required ratio".
     */
    public double recapitalisationNeeded() {
        if (!inResolution && equity() > 0) {
            // The larger of the two minimums and the larger of the two
            // targets since round 2 of 0.7.11 (minimumEquity()).
            if (equity() >= minimumEquity()) return 0;
            return Math.max(0, targetEquity() - equity());
        }
        return Math.max(0, resolutionExitEquity() - equity());
    }

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
     *
     * Cash in with no loan attached, so it lands squarely in equity - which is
     * the whole point of the exercise and the only thing that lifts a bank out
     * of a credit crunch.
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
        dividendRing[payoutMonths % YEAR_MONTHS] += amount;
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
        bailoutsLifetime += amount;
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
         * outside world, at the punitive rate a city with no bank paid until
         * 0.7.7 (and as the window's money since). So the new bank does not
         * inherit that history; it buys the standing loan book
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

    /**
     * Book over capacity. Infinite in a city with no bank. A MEASURE since
     * 0.7.7, not a price: the branch decisions read it (wantsBranch()) and
     * the Bank tab shows it - 0.7.8's capital rule does not read it; until
     * 0.7.7 ratePremium() turned it into up to
     * eighteen points on every rate in the city, the loop WHAT A LOAN COSTS
     * describes.
     */
    public double strain() {
        double room = capacity();
        if (room <= 0) return getWeightedBook() > 0 ? Double.MAX_VALUE : 0;
        return getWeightedBook() / room;
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
        discountAccreted += amount;
    }

    /**
     * THE TREASURY BUYS ITS PAPER BACK FROM THE BANK (0.7.0), which is who
     * holds it: the price arrives as cash, the book drops by the principal at
     * once rather than at the next refresh, and the difference is the bank's
     * gain or loss on the sale - a gain when the city pays over face, a loss
     * when rates have risen and the paper is worth less than the book carries
     * it at. Until 0.7.0 a buyback paid nobody: the treasury's cash left, the
     * bond came off the list, and the bank's book fell by the principal at the
     * next refresh with nothing arriving - a $20,000k buyback cost the bank
     * $20,067k of equity (the-bank-that-never-paid.md section 5).
     *
     * Happens between two presses, like the player's other decisions, so it
     * is in no month's income statement: it moves equity where it happens, and
     * the next month opens on it (openingEquity). The weighted book loses the
     * same share of itself, so capacity is right before the next refresh.
     *
     * @return the gain (negative: the loss) on the sale
     */
    public double sellPaperBack(double price, double principal) {
        return sellPaperBack(price, principal, 0);
    }

    /**
     * ...and since 0.7.1 at amortised cost: the unearned discount riding on
     * the face that leaves goes with it, so the gain is the price less what
     * the book carried the paper at - face less the discount not yet earned.
     * Only the bank's share of a bond reaches here; the households and the
     * central bank are paid theirs by Game.repurchaseDebt().
     */
    public double sellPaperBack(double price, double principal, double unearned) {
        double before = cityBook;
        double off = Math.min(Math.max(0, principal), before);
        double u = Math.min(Math.max(0, unearned), unearnedDiscount);
        cash += Math.max(0, price);
        cityBook = before - off;
        unearnedDiscount -= u;
        if (before > 0) cityWeighted *= cityBook / before;
        double gain = Math.max(0, price) - (off - u);
        buybackGains += gain;
        treasuryBuybackGain += gain;
        return gain;
    }

    /* =================== THE CITY'S PAPER CHANGES HANDS (0.7.1) ===================
     *
     * Jerus, on the holders: "yes households should be able to hold." The bank
     * is the desk for the city's paper as it is for the shares: a household
     * that sells before maturity sells to it, and the central bank's holdings
     * dial buys from it and sells to it. Each trade moves cash, the book by the
     * face, and the unearned discount riding on that face at once rather than
     * at the next refresh; the difference between the price and what the book
     * carried the paper at - face less the discount not yet earned - is the
     * month's gain or loss on the city's paper, a line of the income statement
     * (afterTrading()), because these happen inside the month and the
     * statement's articulation - equity moves by net income - must see them.
     * ============================================================================ */

    /** The gain or loss on the city's paper that changed hands this month. */
    private double paperGains;

    /** What the desk paid households for their paper this month: money leaving the pools for a household, which MoneyAudit declares. */
    private double paperBoughtFromHouseholds;

    /** What the central bank paid it for paper this month, and what it paid the central bank. */
    private double paperSoldToCentralBank, paperBoughtFromCentralBank;

    public double getPaperSoldToCentralBank()     { return paperSoldToCentralBank; }
    public double getPaperBoughtFromCentralBank() { return paperBoughtFromCentralBank; }

    /**
     * THE UNEARNED DISCOUNT (0.7.1): the part of what the bank paid under face
     * for the city's paper it has not yet accreted into income, carried as a
     * liability against the book - so equity does not jump by the discount
     * the month the paper settles, and it reaches interest income a month at
     * a time. Re-derived with the book at every refresh (Game.refreshBank(),
     * DebtManager.bankUnearnedDiscount()), which is why the save needs no slot
     * for it: the paper carries its own remainder.
     */
    private double unearnedDiscount;

    public void setUnearnedDiscount(double amount) { unearnedDiscount = Math.max(0, amount); }
    public double getUnearnedDiscount()            { return unearnedDiscount; }
    public double getPaperGains()                  { return paperGains; }
    public double getPaperBoughtFromHouseholds()   { return paperBoughtFromHouseholds; }

    private void addToCityBook(double face) {
        double before = cityBook;
        cityBook = Math.max(0, before + face);
        if (before > 0) cityWeighted *= cityBook / before;
        else cityWeighted = cityBook * RISK_CITY;
    }

    /** A household sells this face to the desk for this price. */
    public double buyPaperFromHouseholds(double price, double face, double unearned) {
        if (!(price > 0) || !(face > 0)) return 0;
        cash -= price;
        paperBoughtFromHouseholds += price;
        addToCityBook(face);
        unearnedDiscount += Math.max(0, unearned);
        double gain = (face - Math.max(0, unearned)) - price;
        paperGains += gain;
        return gain;
    }

    /** The central bank buys this face from the bank's book, in money it made. */
    public double sellPaperToCentralBank(double price, double face, double unearned) {
        if (!(price > 0) || !(face > 0)) return 0;
        double u = Math.min(Math.max(0, unearned), unearnedDiscount);
        cash += price;
        paperSoldToCentralBank += price;
        addToCityBook(-face);
        unearnedDiscount -= u;
        double gain = price - (face - u);
        paperGains += gain;
        return gain;
    }

    /** ...and sells it back: the bank pays, and the face returns to its book. */
    public double buyPaperFromCentralBank(double price, double face, double unearned) {
        if (!(price > 0) || !(face > 0)) return 0;
        cash -= price;
        paperBoughtFromCentralBank += price;
        addToCityBook(face);
        unearnedDiscount += Math.max(0, unearned);
        double gain = (face - Math.max(0, unearned)) - price;
        paperGains += gain;
        return gain;
    }

    /** Gains less losses on paper the treasury bought back, over the city's life. Not saved: a count for the run. */
    private double buybackGains;
    public double getBuybackGains() { return buybackGains; }

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
        if (interest > 0) { cash += interest; interestEarned += interest; interestFromHouseholds += interest; }
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

    /**
     * ...on a business's book, by name (0.7.8), so the month's provide() can
     * draw it against the allowance that sector's book opened the month
     * holding. writeOff() above is the same loss on no book in particular -
     * a fixture's - which no allowance covers.
     */
    public void writeOffSector(String sector, double amount) {
        if (amount <= 0) return;
        writeOffs += amount;
        writtenOffBySector.merge(sector, amount, Double::sum);
    }

    /** ...and on the families' book, the debts of those discharged this month. */
    public void writeOffHouseholds(double amount) {
        if (amount <= 0) return;
        writeOffs += amount;
        householdWrittenOff += amount;
    }

    /** Wages and running costs, which are real money leaving. */
    public void payRunning(double payroll, double upkeep) {
        this.payroll = Math.max(0, payroll);
        this.upkeep = Math.max(0, upkeep);
        cash -= this.payroll + this.upkeep;
    }

    /* =====================================================================
       FEES (0.7.7)

       Jerus: "yes both" - account fees and loan fees. A real bank's revenue
       is about three-fifths net interest and two-fifths fees (Canada's big
       banks); this one's was all interest, so every cost it had was priced
       into its borrowers' rates or not covered at all.

       THE ACCOUNT FEE is a month's charge on every housed household - the
       working families, the retired, the out of work and the students, not
       the orphans, the prisoners or anybody without a home - real, so
       neither inflation nor a reform erases it: accountFee() is ACCOUNT_FEE
       at the month's price index, in today's unit. It comes out of the
       households' money with their other fixed bills (HouseholdBalance) and
       arrives here as cash from outside the audit's pools, declared the way
       the interest they pay is ("+ bank AccountFees").

       THE LOAN FEE is LOAN_FEE of new lending. A business pays it out of the
       loan's proceeds - its cash to this bank's cash, both pools, so an
       internal transfer that cancels in the audit (BusinessDebtManager). A
       household's is added to what it owes, the way a credit line's fee is,
       so no cash moves and nothing is declared: it is income the day it is
       charged and cash the day it is repaid, through the repayments the
       audit already reads.
       ===================================================================== */

    /** A month's account fee on every housed household, in founding thousands: $12, the middle of what an everyday chequing account costs a month at Canada's big banks ($10-15, 2025). */
    public static final double ACCOUNT_FEE = .012;

    /** The fee on new lending, a share of the principal: one per cent, a typical arrangement fee on a commercial loan. */
    public static final double LOAN_FEE = .01;

    /** ACCOUNT_FEE in today's unit - reseeded and reformed with the other money constants. */
    private double accountFeeBase = ACCOUNT_FEE;

    /** A month's account fee per housed household at this price index, in today's money - nothing in a city with no branch, which has no bank to hold an account at. */
    public double accountFee(double priceIndex) {
        if (branches <= 0) return 0;
        return accountFeeBase * Math.max(0, priceIndex);
    }

    /** The month's fees: accounts, loans paid in cash (a business's), and loans added to what is owed (a household's). */
    private double accountFees, loanFeesPaid, loanFeesOwed;

    /** The households' account fees, in cash from outside the pools. Declared. */
    public void takeAccountFees(double amount) {
        if (!(amount > 0)) return;
        cash += amount;
        accountFees += amount;
    }

    /** Loan fees a business paid out of its proceeds: cash from another pool. Internal. */
    public void takeLoanFees(double amount) {
        if (!(amount > 0)) return;
        cash += amount;
        loanFeesPaid += amount;
    }

    /** Loan fees added to what the households owe: income now, no cash until they repay, and the book carries them from the next refresh. */
    public void bookLoanFees(double amount) {
        if (!(amount > 0)) return;
        loanFeesOwed += amount;
    }

    public double getAccountFees()   { return accountFees; }
    public double getLoanFeesPaid()  { return loanFeesPaid; }
    public double getLoanFeesOwed()  { return loanFeesOwed; }

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
     * this a constraint rather than free money. Until 0.7.0 a bank reaching
     * further past its deposits paid more for every dollar it had reached for,
     * so the strain premium it charged its borrowers was passing on a bill it
     * was actually being sent; the window's price is flat, and since 0.7.7
     * what the bank charges is priced off it (fundsTransferPrice()) rather
     * than off a premium.
     *
     * THIS MONEY CAME FROM OUTSIDE THE CITY until 0.7.0, and now it comes from
     * the CENTRAL BANK'S WINDOW, at the policy rate plus
     * CentralBank.WINDOW_PENALTY. The window's advance is money made, its
     * repayment money destroyed and its interest the central bank's income -
     * all three declared in MoneyAudit under Scope.MONEY, so a city whose bank
     * borrows at the window genuinely has more money in it than one whose bank
     * does not, and the audit knows who made it. Abroad is still there as the
     * carry trade (CapitalFlows), which is now the priced alternative rather
     * than the marginal source.
     *
     * THE WINDOW IS NOT LIMITED in 0.7.0. What bounds what the bank lends is
     * the capital ratio, which no amount of funding relaxes, and what the
     * window's money costs, which is priced into every loan - past
     * EASY_STRAIN a strain premium did it too, until 0.7.7.
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
     *   THE WINDOW for anything beyond that, at the policy rate plus
     *   CentralBank.WINDOW_PENALTY - wholesale paper from abroad, at the
     *   market's price, until 0.7.0.
     *
     * The first version of this charged the wholesale rate on the WHOLE book,
     * because deposits are a memorandum figure here rather than cash the bank
     * holds. A bank paying market funding costs on every dollar it has ever lent
     * cannot make money: measured over 90 months it went to NEGATIVE $37M of
     * equity against $48M of funding, which is not a bank, it is a bonfire.
     */

    /*
     * PAYING FOR DEPOSITS is the other half of what a bank IS. Until it did,
     * the city's savings sat with the bank earning nothing while it lent them
     * out at eleven percent, which is not a bank, it is a shoebox with a
     * licence. Paying for them turns the difference between what it charges
     * and what it pays into a NET INTEREST MARGIN, the number a real bank
     * lives or dies on.
     *
     * WHAT IT PAYS IS ITS OWN CHOICE since 0.7.7 - Jerus: "the bank chooses,
     * but it can pass on less or more if it wants." It was DEPOSIT_PASS_THROUGH,
     * 45% of the month's INTEREST INCOME (Jerus, 2026-09-09: "make it so that
     * the banks pay 55% of interest income, not 55% of the interest rate"),
     * the rate derived from the payout - so a bank lent out at high rates
     * paid its savers more than the dial and a bank with no book paid them
     * almost nothing, whatever money was worth. Now the bank sets a RATE, a
     * share of the policy rate, by how it is funded - see WHAT TO PAY SAVERS.
     * The rule that it never pays out more interest than it took in stands.
     */

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

    /** The rate savers are being paid, a year: the month's payout over the deposits - the rate the bank chose, unless its interest margin could not pay it (isDepositPayoutHeld()). */
    public double depositRate() { return depositRate; }

    /** True when rule 2 of WHAT TO PAY SAVERS held the savers under the rate the bank chose - its margin, after its running costs or at the savers' share, could not pay it. A flow of the month, not saved. Until 0.7.7 the question was the opposite one - isDepositRateCapped(), a payout over the lending rate - which a chosen rate under the window's can no longer be. */
    public boolean isDepositPayoutHeld() { return depositPayoutHeld; }

    private boolean depositPayoutHeld;

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

    /*
     * THE PRICE OF WHOLESALE MONEY WAS TWO DIALS until 0.7.0: FUNDING_SPREAD,
     * two points over the risk-free rate "for being a bank rather than a
     * treasury", and FUNDING_STRETCH, six points more at a reach of one
     * deposit book, capped at three - the market charging more the further
     * past its deposits the bank reached, and the reason the premium it
     * charged its own borrowers was a bill it was actually sent. Both retired
     * with the market itself: the window charges the policy rate plus
     * CentralBank.WINDOW_PENALTY, flat, and until 0.7.7 the strain premium
     * was what still rose with reach.
     */
    private double fundingRate;

    /** Everything it owes: the mirror of a negative cash position. */
    public double borrowings() { return Math.max(0, -cash); }

    /**
     * The cheap tranche - the city's own money, lent back out.
     *
     * Measured against what its branches can GATHER, not against everything the
     * city happens to have. A bank cannot fund itself with savings held in
     * somebody's mattress, and a bank with no branches has gathered nothing at
     * all, so every dollar it has lent is money it went to the window for.
     */
    public double depositFunding() { return Math.min(borrowings(), depositsGathered()); }

    /** ...and the part it had to go to the window for. */
    public double wholesaleFunding() { return borrowings() - depositFunding(); }

    /**
     * What the bank is paying for the money it did not have: the window's
     * rate, policy plus CentralBank.WINDOW_PENALTY. Flat since 0.7.0 - see the
     * note above it on what it used to rise with.
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
    public void fundToCover(double policyAnnual) {

        /*
         * WITH NO BRANCHES THERE IS NO BANK, and nothing to charge.
         *
         * The lending in a city with no bank is coming from outside it - from
         * strangers, which is what the eighteen-point premium on every borrower
         * WAS until 0.7.7 (priced since as the window's money) - so there is no
         * institution here paying depositors or rolling paper. Charging one anyway compounded a cost onto an entity that did
         * not exist: measured over 180 months, a bankless city's phantom bank
         * ran up NEGATIVE $48,000 of equity, and because a bank needs capital to
         * lend, that hole then made it impossible for the city ever to found a
         * real one. A trap with no way out, built entirely out of bookkeeping.
         */
        if (branches <= 0 || inResolution) {
            // A bank in resolution is not paying anybody. Charging it is what
            // turned a failure into a runaway - see resolveIfFailed().
            //
            // NOR THE WINDOW, and that is decided rather than forgotten
            // (0.7.1): the central bank goes on advancing what a failed bank
            // owes past its deposits (CentralBank.settleWindow(), from the
            // month's settle), and charges it no interest while it is in
            // resolution. The resolution has just set
            // its equity to zero; a charge on the window's tranche would put
            // it straight back under the next month, and that month's
            // failure would charge it again - the same runaway, with the
            // central bank as the creditor. The advance is a standstill until
            // the bank earns or is given its way out, which is what a lender
            // of last resort does for a bank it has resolved.
            depositRate = 0;
            depositPayoutHeld = false;
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
        /*
         * THE WINDOW'S PRICE, since 0.7.0: the policy rate plus the penalty,
         * on the whole of the wholesale tranche. The three-way split below is
         * untouched - deposits first and free, the window for the rest - and
         * only the rate it prices changed. See THE FUNDING SIDE.
         */
        double policy = Math.max(0, policyAnnual);
        double wholesaleNow = wholesaleFunding();
        fundingRate = policy + CentralBank.WINDOW_PENALTY;
        fundingCost = wholesaleNow * fundingRate / 12;

        // ...and the other side of the same position: what is not lent is
        // its reserves, and earns the policy rate at the central bank. Struck
        // here, banked with the funding below, and counted as interest
        // income - which the savers' payout is bounded by (rule 2 below).
        placementIncome = cashReserves() * policy / 12;
        interestEarned += placementIncome;

        /*
         * ...AND THEN THE DEPOSITORS, at the rate the bank chooses - see
         * chooseDepositRate() - and never more than its interest margin can
         * pay.
         *
         * THE RATE REPORTED IS WHAT WAS PAID, over the deposits. It was capped
         * at the lending rate from 0.7.3, because a payout struck as a share
         * of the whole income over a deposit base that had barely opened read
         * as thousands of per cent (7,567% on $5.7k, seed 0's founding). A
         * rate chosen as a share of the policy rate, and never above the
         * window's, is under prime by construction, so the cap had nothing
         * left to do and went with DEPOSIT_PASS_THROUGH.
         */
        double onDeposits = chooseDepositRate(policy);
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
               wholesale funding cost (abroad, as it then was), and instead it
               was an internal transfer
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

        // Only the window's tranche is charged for. The deposits are the city's
        // own money and cost the bank nothing to use beyond what it pays for
        // them - which is the whole advantage of having somewhere for people to
        // save. Struck above, banked here - and the interest on reserves with
        // it. The central bank's end of both is booked by Game in the same
        // breath, off these two figures (getFundingCost(), getPlacementIncome()).
        cash -= fundingCost;
        cash += placementIncome;
    }

    /* =====================================================================
       WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT.

       Jerus, 2026-09-23: "the bank chooses, but it can pass on less or more
       if it wants." Two rules, in the order they bind.

       1. THE RATE IS A SHARE OF THE POLICY RATE, AND THE SHARE IS WHAT THE
          BANK'S FUNDING SAYS A DEPOSIT IS WORTH. A bank flush with reserves
          only earns the policy rate at the central bank on its next deposit,
          less the cost of keeping the account, so it passes on little
          (DEPOSIT_SHARE_FLUSH); a bank borrowing at the window saves the
          window's rate on every deposit it can find, so it passes on nearly
          all of it (DEPOSIT_SHARE_AT_WINDOW); in between, by how much of what
          its branches gathered it has lent (fundingPosition()). Never below
          zero, never above the window's rate, and moved DEPOSIT_RATE_SPEED of
          the way there a month, because depositors do not see a new rate
          every month. Over a cycle real banks pass on about 0.4 of a policy
          move (the "deposit beta", NY Fed); what this one averages is
          measured, not forced.

       2. NEVER PAST NET ZERO, AND NEVER MORE THAN THE SAVERS' SHARE OF WHAT
          IT EARNED - Jerus, 2026-09-09: "the bank will not give out more than
          it needs to stay net 0". The payout is bounded twice:
            - by the margin LESS THE RUNNING COSTS: interest earned, less what
              it paid the window, less the month's payroll and upkeep. "Net 0"
              is the bank's net, and its staff and buildings are in it;
            - by depositShare() OF THE MARGIN. The city's savings are counted
              as deposits without being held as this bank's cash (the
              header's one stated fudge), so a rate on all of them is paid on
              money the bank earns nothing on. When what it did earn cannot
              pay the rate rule 1 chose, the savers get the same share of
              what it earned that rule 1 gives them of the policy rate, and
              the bank keeps the rest - the shape DEPOSIT_PASS_THROUGH had
              (45% of the income), with the share its funding sets.
          Without a bound at the margin at all, a flat payout went on while a
          bank's margin was being eaten, and measured over 4,002 months that
          took the city into a housing famine - a bank that cannot hold
          capital cannot lend. Both bounds as they stand came from the gate's
          stress run (2026-09-23): with the margin alone as the bound, a
          one-branch bank on $405M of deposits and a $9M book at a held 10%
          dial paid its savers every dollar it earned in 3,796 of 4,000 months
          and lost its payroll every one of them, and the eight held-10% seeds
          failed the bank 184 times against 0.7.6's 78. isDepositPayoutHeld()
          says when either bound held.

       THE BID FOR HOT MONEY IS GONE (0.7.7). Rule 3 was "and higher, if the
       money is worth buying": a forecast off CapitalFlows of what a better
       rate would draw in, bid only when the bank was in profit, lent out and
       short of deposits rather than capital. Its own counter asked for it to
       be taken out if it never fired, and BankCheck's note recorded a
       600-month probe firing it zero times - the bank is almost always
       either capital-bound or under-lent. Rule 1 is its successor: a bank
       short of money pays more for it, every month, not only when a grid
       search finds a profit.
       ===================================================================== */

    /** The share of the policy rate a bank flush with reserves passes to its savers: about a third, because its next deposit only earns the policy rate at the central bank less the cost of the account - set so that this bank, which is flush nearly all its life, averages near the ~0.4 of a policy move real banks pass on over a cycle (NY Fed). A quarter was measured first: it averaged 0.28 on seed 0, and the savers' weaker answer to the dial let one inflation run to a 14% dial. */
    public static final double DEPOSIT_SHARE_FLUSH = .35;

    /** ...and the share a bank funding at the window passes on: nine-tenths, because every deposit it finds saves it the window's rate, so it pays close to the policy rate for one - the way banks short of funding bid for it with online and term deposits. */
    public static final double DEPOSIT_SHARE_AT_WINDOW = .90;

    /** How far from last month's rate toward the one its funding asks for the bank moves in a month: a sixth, so most of a move reaches savers inside a year and none of it in a single step. */
    public static final double DEPOSIT_RATE_SPEED = 1.0 / 6;

    /**
     * How the bank is funded, 0 to 1: 0 while it holds reserves (its cash is
     * positive), 1 once it is borrowing at the window, and in between the
     * share of what its branches gathered that it has lent out. What the
     * deposit share is read from; 1 with no branch or a failed bank.
     */
    public double fundingPosition() {
        if (branches <= 0 || inResolution) return 1;
        double owed = borrowings();
        if (owed <= 0) return 0;
        double gathered = depositsGathered();
        return gathered > 0 ? Math.min(1, owed / gathered) : 1;
    }

    /** The share of the policy rate the bank's funding asks it to pass on: between DEPOSIT_SHARE_FLUSH and DEPOSIT_SHARE_AT_WINDOW, by fundingPosition(). */
    public double depositShare() {
        return DEPOSIT_SHARE_FLUSH
                + (DEPOSIT_SHARE_AT_WINDOW - DEPOSIT_SHARE_FLUSH) * fundingPosition();
    }

    /** The rate the bank chose this month, before rule 2 asked whether its margin could pay it. A flow, not saved. */
    private double chosenDepositRate;
    public double getChosenDepositRate() { return chosenDepositRate; }

    /**
     * The month's deposit interest, in money. Sets the rate chosen and whether
     * rule 2 held it.
     *
     * FROM LAST MONTH'S RATE, the one the savers were paid: depositRate is
     * what fundToCover() reported a month ago (setDepositRate() on the load
     * path), so a reload moves from the same place the live city does.
     *
     * @return what to pay savers this month, already capped at net zero
     */
    private double chooseDepositRate(double policyAnnual) {
        depositPayoutHeld = false;
        double policy = Math.max(0, policyAnnual);
        double window = policy + CentralBank.WINDOW_PENALTY;

        /* ---- rule 1: the rate its funding asks for, moved toward a sixth at a time ---- */
        double target = Math.max(0, Math.min(window, depositShare() * policy));
        double moved = depositRate + (target - depositRate) * DEPOSIT_RATE_SPEED;
        chosenDepositRate = Math.max(0, Math.min(window, moved));
        if (deposits <= 0) return 0;

        /* ---- rule 2: never past net zero, and never more than the savers' share of the margin ---- */
        double wanted = chosenDepositRate * deposits / 12;
        double margin = Math.max(0, Math.max(0, interestEarned) - fundingCost);
        double ceilingCash = Math.max(0, Math.min(depositShare() * margin,
                                                  margin - payroll - upkeep));
        if (wanted > ceilingCash) {
            depositPayoutHeld = true;
            monthsPayoutHeld++;
            return ceilingCash;
        }
        return wanted;
    }

    /* HOW OFTEN RULE 2 HELD THE SAVERS UNDER THE CHOSEN RATE - counted for the run, not saved, for the reason the bid-up's counter was: a rule that never binds looks exactly like one that does not exist. */
    private int monthsPayoutHeld;
    public int getMonthsPayoutHeld() { return monthsPayoutHeld; }

    /** What the window charged this month, paid to the central bank. */
    public double getFundingCost() { return fundingCost; }
    /** What its reserves earned at the central bank this month, at the policy rate. See placementIncome. */
    public double getPlacementIncome() { return placementIncome; }

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

    /**
     * Total assets: the loan book net of what it has set aside against it
     * (netLoans(), since 0.7.8), whatever cash it has not lent, and what the
     * desk holds.
     */
    public double totalAssets() { return cashReserves() + netLoans() + Math.max(0, securities); }

    /** The loans as the balance sheet carries them (0.7.8): what is owed, less the allowance for what will not come back. */
    public double netLoans() { return getBook() - getAllowance(); }

    /** ...and a desk that is short owes the shares: a liability at the mark. */
    public double shortSecurities() { return Math.max(0, -securities); }

    /**
     * What the bank owes: what it borrowed to fund its book (past its
     * deposits, at the central bank's window since 0.7.0), and the hot money.
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
     *
     * AND THE DISCOUNT IT HAS NOT YET EARNED (0.7.1), against the book: the
     * city's paper is on the book at face, and what the bank paid under face
     * is interest it earns over the paper's life, not the day it settles.
     */
    public double totalLiabilities() {
        return borrowings() + Math.max(0, foreignDeposits) + shortSecurities() + unearnedDiscount;
    }

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

    /** ...less what it paid savers and what it paid the window. */
    public double netInterestIncome() {
        return interestEarned - depositInterest() - fundingCost;
    }

    /** ...plus its fees, since 0.7.7: the accounts, and the loans written. See FEES. */
    public double feeIncome() { return accountFees + loanFeesPaid + loanFeesOwed; }

    /**
     * ...less the provision for the loans that will not come back (0.7.8):
     * what the allowance rose by, and whatever the month wrote off that it
     * had not already set aside - provisions(). It was the month's
     * write-offs until 0.7.8, which is the same money over a cycle and at a
     * different time: the loss is recognised as the borrower weakens, not
     * the month it is written down.
     */
    public double afterLosses()     { return netInterestIncome() + feeIncome() - provisions(); }

    /** ...less the tellers and the lights. */
    public double operatingExpenses() { return payroll + upkeep; }

    /** ...plus what the desk made or lost. */
    public double afterTrading()    { return afterLosses() + tradingIncome + paperGains; }

    /** What it made before the city took its share. */
    public double profitBeforeTax() { return afterTrading() - operatingExpenses(); }

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
     * Profit the tax is charged on: the month that has just finished - and,
     * since 0.7.7, what the month before it earned after its own close
     * (getCarriedLate(); see PROFIT THAT LANDS AFTER THE CLOSE).
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
        /*
         * STRUCK HERE, ONCE, AND THEN CARRIED - which is not a flourish, it is
         * the only way the same number survives a save.
         *
         * The first version had Game ask the bank for this in the middle of the
         * month, off whatever the balance sheet happened to look like at that
         * moment. The live path asks during nextMonth(); the load path asks
         * after every pool is back. Those are different moments, so they gave
         * different answers, and a city reloaded from its own autosave quoted
         * 1.35% for a loan it had quoted 1.33% for a second earlier. BankCheck
         * caught it on the first run - the assertion is "...and so does what
         * the bank is charging for money", and it exists for exactly this.
         *
         * A price that was quoted is not a figure that can be recomputed. This
         * is the month's, final, and it is what next month prices off - the
         * same one-month lag a real bank's funding curve has.
         */
        lastCostOfFunds = blendedCostOfFunds();
        /*
         * ...AND WHAT LANDED AFTER LAST MONTH'S CLOSE (0.7.7). The desk's
         * re-mark, the dividends on what it holds and the paper it buys from
         * the households all move this bank's equity AFTER this line - the
         * owners are paid and the shares trade below it in the month
         * (Game.nextMonth()) - so they were never taxed and never paid out:
         * startMonth() cleared them. They are carried into the next month's
         * taxed profit instead, which keeps the month's order and moves no
         * cash, so MoneyAudit is untouched; the month's own statement still
         * shows them where they happened.
         */
        struckProfit = profitBeforeTax();
        profitLastMonth = struckProfit + carriedLate;
        closedThisMonth = true;
        lastPayroll  = payroll;
        lastUpkeep   = upkeep;
        lastInterest = interestEarned;
        lastBook     = getBook();
        lastKept     = netInterestIncome() + feeIncome();
        // A month the book did not keep its branches' staff, on those same
        // figures: the branch test in reverse (0.7.11, round 2; closesBranch()).
        uncoveredMonths = branchesCoverTheirStaff() ? 0 : uncoveredMonths + 1;
        // The year of losses the capital target is sized on (0.7.8), before
        // the prices, whose capital charge reads the target.
        recordLosses();
        strikePrices();
    }

    /*
     * PROFIT THAT LANDS AFTER THE CLOSE (0.7.7). struckProfit is the month's
     * profit as closeMonth() found it; carriedLate is what the month before
     * earned after its own close, taken into this month's taxed profit.
     * restoredLate is the save's copy of lateProfit(), for the first
     * startMonth() after a load.
     */
    private double struckProfit, carriedLate, restoredLate;
    private boolean closedThisMonth;

    /** What this month earned after its close: the part of its profit next month's tax and dividend will carry. Zero before the close. */
    public double lateProfit() {
        return closedThisMonth ? profitBeforeTax() - struckProfit : restoredLate;
    }

    /** What last month earned after its close, inside getProfitLastMonth(). */
    public double getCarriedLate() { return carriedLate; }

    /** The load path: what the saved month earned after its close. */
    public void restoreLateProfit(double value) {
        restoredLate = Double.isFinite(value) ? value : 0;
        closedThisMonth = false;
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

    /** Last month's interest margin and fees - what its book KEPT, which the branch test asks of a counter since 0.7.7. */
    private double lastKept;

    public double[] lastMonthToSave() {
        return new double[]{ lastPayroll, lastUpkeep, lastInterest, lastBook,
                             fundingRate, lastCostOfFunds,
                             // ...and 0.7.7's: what the book kept, and the
                             // two struck parts of every loan's price.
                             lastKept, lastWindowShare, lastRunningCost,
                             // ...and round 2 of 0.7.11's: how long the book
                             // has not kept its branches' staff.
                             uncoveredMonths };
    }

    /*
     * ...AND THE TWO PRICES THE MONTH CLOSED AT, added 2026-09-13 with the
     * cost-of-funds floor.
     *
     * Every rate in the city was then floored on marginalCostOfFunds() - the
     * city's own paper still is, since 0.7.7 - so a save that did not carry
     * it reloaded into a bank whose money was free. The
     * funding rate goes with it because the Bank tab prints it and a reloaded
     * screen should not read zero.
     *
     * CARRIED RATHER THAN DERIVED, which is the opposite of what the premium
     * beside it did (until 0.7.7), and the reason is a circle. fundToCover() strikes
     * fundingRate FROM the city's rate; the city's rate is now floored on
     * fundingRate. In the live path a month separates the two and there is no
     * circle - this month's rate is priced off last month's funding. On a load
     * there is no last month to read, so re-deriving it would need the rate it
     * is an input to. It is a price that was quoted, not a figure that can be
     * recomputed, and it belongs in the save for the same reason lastPayroll
     * does.
     *
     * An older save simply has no fifth element: the bank loads with free money
     * for one month and corrects itself at the first closeMonth().
     */
    public void restoreLastMonth(double[] state) {
        if (state == null || state.length < 4) return;
        lastPayroll  = state[0];
        lastUpkeep   = state[1];
        lastInterest = state[2];
        lastBook     = state[3];
        if (state.length < 6) return;
        fundingRate     = state[4];
        lastCostOfFunds = state[5];
        // A save from before 0.7.7 has none of these: the book is taken to
        // have kept its interest, and the parts are this bank's defaults
        // until its first close strikes them.
        lastKept = state.length < 9 ? lastInterest : state[6];
        if (state.length < 9) return;
        lastWindowShare = state[7];
        lastRunningCost = state[8];
        // A save from before round 2 of 0.7.11 starts the count again: its
        // bank waits the fuse out from the load.
        uncoveredMonths = state.length < 10 ? 0 : (int) state[9];
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
        taxPaid = taxOn(profitLastMonth, annualProfitRate);
        cash -= taxPaid;
        return taxPaid;
    }

    /** The city's share of a month's profit at this rate: nothing on a loss. One definition, for the bill and for the dividend. */
    private static double taxOn(double profit, double profitTaxRate) {
        return Math.max(0, profit) * Math.max(0, profitTaxRate);
    }

    /**
     * Last month's profit AFTER the tax it will be charged at this rate: what
     * the owners are paid a share of (Game.payDividends()) and what the
     * register records (Equity.recordMonth()), as every sector's own net
     * income is. Until 0.7.7 both were handed the profit before tax.
     */
    public double getProfitAfterTaxLastMonth(double profitTaxRate) {
        return profitLastMonth - taxOn(profitLastMonth, profitTaxRate);
    }

    /* =====================================================================
       THE BANK AS A BUSINESS WITH ITS CAPITAL (0.7.8)

       Jerus: "lets make banks realistic, and remember, its a business, it
       wants to make money" - and of its capital, "the bank chooses". 0.7.7
       made it price like one. It still did not behave like one with what it
       earned: it set nothing aside for losses, so a sector's write-down hit
       its equity the month it happened; it paid 40% of every positive month
       (Equity.PAYOUT), capped at whatever cash it held that month, and kept
       the rest - seed 0 of the 0.7.7 playtest ended with $23.7bn of equity
       against a $3.2bn book, 877% where 8% is required, and a return on its
       equity under one per cent; and its lending did not tighten as its
       capital ran down, it stopped dead when it failed. Four rules now, each
       one a banker would recognise, and the regulator's minimum,
       CAPITAL_RATIO, is the only number in them that is the city's:

         THE ALLOWANCE (simplified IFRS 9). Every book it lends from - each
         sector's business debt, and the families' - holds an allowance: a
         sound book its year's expected loss ("stage 1"); a book whose
         borrower is in trouble its lifetime loss ("stage 2"). For a sector
         both are read off the curve its firms default on
         (BusinessDebtManager.defaultProbability(), since 0.7.8 - one PD, two
         uses): a year of it, never under BASE_LOSS_RATE, and the loan's whole
         term of it on the share of its firms past the watch line, staged
         firm by firm (stageTwoShare()). For the families a year's
         BASE_LOSS_RATE, and past their watch line their debt scaled by how
         close they are to discharge. A write-off is drawn
         against the allowance first. The PROVISION on the income statement
         is what the allowance rose by plus whatever was written off that it
         had not set aside: over a cycle the same money as the write-offs,
         recognised as the borrower weakens rather than the month it is
         written down - which is why the old rule was abandoned after 2008.
         The city's paper and the carry book carry none: the city is the
         sovereign, and a carry borrower never defaults, by Jerus's call.

         THE TARGET, the bank's own: the minimum plus a buffer that would
         take the worst year of provisions it has lived through and still
         leave it at the minimum - never less than the Basel conservation
         buffer, and never more than the whole Basel buffer stack
         (MAX_BUFFER, which says what went wrong without it). A young bank
         holds the standard buffer; one that has been through a crisis holds
         more. The TOP of its band is the target plus a management cushion.

         WHAT IT DOES WITH ITS PROFIT. Under its target it keeps all of it.
         Inside its band it pays PAYOUT_IN_BAND of its profit after tax, never
         so much that it would fall under the target. Over the top it also
         returns the excess, a twelfth of it a month. Its desk buys its own
         shares back while it is at or over its target, never past what it
         holds over it (buybackRoom()), and issues new ones only while it is
         under it (buysBackOwnShares(), issuesOwnShares()); those trades are
         capital, not income. Its desk holds other companies' shares only on
         the capital it has over its target (deskCanCarry()).
         Not capped at
         its cash since 0.7.8: a bank's dividend is bounded by its capital,
         and one whose book is funded by deposits pays from them like any
         other bank.

         WHAT IT LENDS. At or over its target, as it always has. Under the
         minimum, only what keeps its existing borrowers going - a business's
         interest reserve and the refinancing of what matures, a family's
         month of interest. In between, a borrower's debt may grow at most
         lendingGrowthLimit() a month, from nothing at the minimum to no limit
         at the target. Existing loans run on at their rates. Game reads it
         once, at the top of the month, and hands it to the desks that lend:
         BusinessDebtManager.setCapitalRule(), HouseholdBalance
         .setCapitalRule(), and the carry trade through headroom().

       TWO MINIMUMS SINCE ROUND 2 OF 0.7.11. CAPITAL_RATIO is no longer the
       only number in them that is the city's: LEVERAGE_RATIO_MIN asks for
       equity against everything the bank has lent, whatever it weighs.
       Every minimum and target above is the larger of the risk-based one
       and the leverage one (minimumEquity(), targetEquity(); the leverage
       ratio, below), and the lending that tightens between them reads the
       measure that binds.
       ===================================================================== */

    /* ---------------------------- the allowance ---------------------------- */

    /** Leverage past which a business borrower is in trouble ("stage 2"): BusinessDebtManager.MAX_LOAN_TO_ASSETS, the most the shortfall desk lends against a borrower's assets - past it the bank would not lend it another dollar to cover a loss. Since 0.7.8's staging a line each FIRM is read against, so a sector at it has half its book in stage 2 (stageTwoShare()), and past it most (getStage()). */
    public static final double SECTOR_WATCH_LEVERAGE = BusinessDebtManager.MAX_LOAN_TO_ASSETS;

    /** Months of income owed past which a family's credit line is in trouble ("stage 2"): half its ceiling, HouseholdBalance.CREDIT_LIMIT_MONTHS - from there it owes more of its room than it has left. */
    public static final double HOUSEHOLD_WATCH_MONTHS = HouseholdBalance.CREDIT_LIMIT_MONTHS / 2;

    /** The key the families' book is saved and shown under, beside the sectors' names. */
    public static final String HOUSEHOLD_BOOK = "Households";

    /** What the BACKSTOP would cost the bank on a business owing this against these assets (BusinessDebtManager.restructure()): everything owed past BusinessDebtManager.RESTRUCTURE_TARGET of its assets - all of it, for a sector with nothing left, which is the only one the backstop takes since 0.7.8. The allowance holds it for such a sector. */
    public static double lossIfDefaulted(double principal, double assets) {
        return Math.max(0, principal - Math.max(0, assets) * BusinessDebtManager.RESTRUCTURE_TARGET);
    }

    /** A business borrower in trouble: owing past SECTOR_WATCH_LEVERAGE of its assets, or anything at all against none. */
    public static boolean sectorWatched(double principal, double assets) {
        if (!(principal > 0)) return false;
        return assets <= 0 || principal > assets * SECTOR_WATCH_LEVERAGE;
    }

    /**
     * The share of a sector's firms, by what they owe, past the watch line:
     * the curve's spread of fortunes (BusinessDebtManager.ASSET_VOLATILITY)
     * read at SECTOR_WATCH_LEVERAGE instead of the default point,
     * N(ln(L / SECTOR_WATCH_LEVERAGE) / ASSET_VOLATILITY) - half at the watch
     * line itself, 90% at 1.24, all of it against no assets at all. The share
     * of the sector's book in stage 2 (0.7.8).
     */
    public static double stageTwoShare(double principal, double assets) {
        if (!(principal > 0)) return 0;
        if (!(assets > 0)) return 1;
        return BusinessDebtManager.normalCdf(Math.log(principal / assets / SECTOR_WATCH_LEVERAGE)
                / BusinessDebtManager.ASSET_VOLATILITY);
    }

    /**
     * The allowance a business's book holds, read off the curve its firms
     * default on (0.7.8) - the same PD(L) that writes the month's slice off
     * (BusinessDebtManager.defaultProbability()), so the allowance is what
     * the slices will cost - AND STAGED FIRM BY FIRM, as IFRS 9 stages loans
     * one by one (Jerus, 2026-09-23: "Smooth the loss reserve"):
     *
     *   EL12   = max(BASE_LOSS_RATE, LOSS_GIVEN_DEFAULT x PD(L, 12 months)),
     *            a year's expected loss, never less than prime is priced for;
     *   ELlife = max(EL12, LOSS_GIVEN_DEFAULT x PD(L, LOAN_TERM_MONTHS)),
     *            the lifetime expected loss over a loan's term;
     *   s2     = stageTwoShare(): the share of its firms past the watch line;
     *
     *   allowance = principal x ((1 - s2) x EL12 + s2 x ELlife)
     *
     * and a sector with nothing left, which the backstop writes off whole:
     * lossIfDefaulted(), all of it.
     *
     * CONTINUOUS IN LEVERAGE. The first cut of the curve (0.7.8, round 1)
     * moved the whole sector to stage 2 the month it passed the watch line,
     * and its lifetime loss landed in one month's provision: 58 of the
     * default run's 84 failures were that month, on a borrower that was a
     * median 62% of the book. Before the curve - 0.7.8 as first built -
     * stage 2 was lossIfDefaulted() scaled from nothing at the watch line to
     * all of it at INSOLVENCY_TRIGGER, where the whole sector was written
     * down at once.
     */
    public static double sectorAllowance(double principal, double assets) {
        return sectorAllowance(principal, principal, assets);
    }

    /**
     * ...ON WHAT IT OWES NOW, READ AT ITS QUARTER (0.7.8): the curve read at
     * principal over assets - the averages of its last quarter's readings
     * (BusinessDebtManager.quarterPrincipal(), quarterAssets()) - and the
     * loss struck on what the sector owes this month, owed: the rate is the
     * borrower's standing over the quarter, the debt is today's. For a
     * sector with nothing left, lossIfDefaulted() on the quarter, in the same
     * proportion. The two-argument form when the two debts are the same.
     */
    public static double sectorAllowance(double owed, double principal, double assets) {
        if (!(owed > 0)) return 0;
        if (!(principal > 0)) principal = owed;
        if (!(assets > 0)) return lossIfDefaulted(principal, assets) * (owed / principal);
        double leverage = principal / assets;
        double lgd = BusinessDebtManager.LOSS_GIVEN_DEFAULT;
        double year = Math.max(BASE_LOSS_RATE, lgd * BusinessDebtManager.defaultProbability(
                leverage, BusinessDebtManager.DEFAULT_HORIZON_MONTHS));
        double lifetime = Math.max(year, lgd * BusinessDebtManager.defaultProbability(
                leverage, BusinessDebtManager.LOAN_TERM_MONTHS));
        double s2 = stageTwoShare(principal, assets);
        return owed * ((1 - s2) * year + s2 * lifetime);
    }

    /** A household cell in trouble: owing past HOUSEHOLD_WATCH_MONTHS of its income. */
    public static boolean householdWatched(double monthsOwed) {
        return monthsOwed > HOUSEHOLD_WATCH_MONTHS;
    }

    /**
     * The allowance a household cell's debt holds: its year's expected loss
     * while sound; once it is in trouble its whole debt - a discharge writes
     * all of it off (Household.discharge()) - scaled from nothing at
     * HOUSEHOLD_WATCH_MONTHS to all of it at the discharge line,
     * HouseholdBalance.BANKRUPT_AT_MONTHS. Never less than the year's loss.
     */
    public static double householdAllowance(double debt, double monthsOwed) {
        if (!(debt > 0)) return 0;
        double stage1 = debt * BASE_LOSS_RATE;
        if (!householdWatched(monthsOwed)) return stage1;
        double closeness = Math.min(1, (monthsOwed - HOUSEHOLD_WATCH_MONTHS)
                / (HouseholdBalance.BANKRUPT_AT_MONTHS - HOUSEHOLD_WATCH_MONTHS));
        return Math.max(stage1, debt * closeness);
    }

    /** Each sector's allowance by name, what each held when the month opened, and what each was written off by this month. */
    private final java.util.Map<String, Double> sectorAllowance = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> openingSectorAllowance = new java.util.LinkedHashMap<>();
    private final java.util.Map<String, Double> writtenOffBySector = new java.util.LinkedHashMap<>();
    /** The sectors whose books are in stage 2, as the last provide() found them. */
    private final java.util.Set<String> sectorsWatched = new java.util.LinkedHashSet<>();
    /** ...and the share of each sector's book in stage 2, firm by firm (0.7.8). */
    private final java.util.Map<String, Double> stageTwoShares = new java.util.LinkedHashMap<>();

    /** The families' allowance, the same three, and how much of their debt is in cells in trouble. */
    private double householdAllowance, openingHouseholdAllowance, householdWrittenOff, householdWatchedDebt;

    /** The whole allowance as the month opened, and how much of the month's write-offs it covered. */
    private double openingAllowance, allowanceUsed;

    /**
     * THE MONTH'S PROVISION: sets every book's allowance from its borrowers
     * as they stand now, and draws the month's write-offs against what each
     * book held when the month opened. Called once a month by Game, after
     * the refresh that has taken every loan and write-off of the month onto
     * the books - never on the load path, which restores what was struck.
     *
     * @param sectors     each sector's name to {what it owed over its last
     *                    quarter, what it owned over it, what it owes now} -
     *                    BusinessDebtManager.quarterPrincipal(),
     *                    quarterAssets() and getPrincipal(): the allowance
     *                    and the stage read the quarter's leverage, the
     *                    readings a new loan is priced on, and the loss is
     *                    struck on today's debt
     * @param households  the families' allowance, summed cell by cell with
     *                    householdAllowance() (HouseholdBalance.lossAllowance())
     * @param householdsWatched the debt of the cells in trouble
     */
    public void provide(java.util.Map<String, double[]> sectors, double households, double householdsWatched) {
        /*
         * WITH NO BRANCH THERE IS NO BANK to set anything aside: the city is
         * lent to from outside and a write-off is the outside lender's, as it
         * always was (fundToCover()). The first branch takes the standing
         * book over at par (openBranches()) and provides for it from its
         * first month.
         */
        if (branches <= 0) { allowanceUsed = 0; return; }
        strikeAllowance(sectors, households, householdsWatched);
        double used = 0;
        for (java.util.Map.Entry<String, Double> e : writtenOffBySector.entrySet()) {
            used += Math.min(e.getValue(), openingSectorAllowance.getOrDefault(e.getKey(), 0.0));
        }
        used += Math.min(householdWrittenOff, openingHouseholdAllowance);
        allowanceUsed = used;
    }

    /**
     * ...and a bank that has never held one: the allowance its borrowers call
     * for, set up WITHOUT a provision - the month it opens on holds it from
     * its start. The load path's, for a save from before 0.7.8: its equity
     * falls by the allowance between two presses, the way a buyback's gain
     * does (sellPaperBack()), and no month's income statement carries it.
     */
    public void openAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched) {
        if (branches <= 0) return;   // no bank, nothing set aside - see provide()
        double before = getAllowance();
        strikeAllowance(sectors, households, householdsWatched);
        allowanceOpened += getAllowance() - before;
        openingSectorAllowance.clear();
        openingSectorAllowance.putAll(sectorAllowance);
        openingHouseholdAllowance = householdAllowance;
        openingAllowance = getAllowance();
        allowanceUsed = 0;
    }

    private void strikeAllowance(java.util.Map<String, double[]> sectors, double households, double householdsWatched) {
        sectorAllowance.clear();
        sectorsWatched.clear();
        stageTwoShares.clear();
        if (sectors != null) {
            for (java.util.Map.Entry<String, double[]> e : sectors.entrySet()) {
                double[] v = e.getValue();
                if (v == null || v.length < 2) continue;
                // {the quarter's principal, its assets, what it owes now}
                // since 0.7.8's quarter; the month's own with two.
                double held = v.length >= 3 ? sectorAllowance(v[2], v[0], v[1])
                        : sectorAllowance(v[0], v[1]);
                if (held > 0) sectorAllowance.put(e.getKey(), held);
                if (sectorWatched(v[0], v[1])) sectorsWatched.add(e.getKey());
                double share = stageTwoShare(v[0], v[1]);
                if (share > 0) stageTwoShares.put(e.getKey(), share);
            }
        }
        householdAllowance = Math.max(0, households);
        householdWatchedDebt = Math.max(0, householdsWatched);
    }

    /** Everything set aside against the book. */
    public double getAllowance() {
        double total = householdAllowance;
        for (double v : sectorAllowance.values()) total += v;
        return total;
    }

    /** ...against the businesses' book, all of it. */
    public double getSectorAllowance() { return getAllowance() - householdAllowance; }
    /** ...against one sector's. */
    public double getSectorAllowance(String sector) { return sectorAllowance.getOrDefault(sector, 0.0); }
    /** What this month wrote off one sector's book - its defaulted firms' slice, or the backstop (0.7.8: the Bank tab's "this month", beside the allowance). Saved with the allowance, so it reads the same after a load. */
    public double getWrittenOff(String sector) { return writtenOffBySector.getOrDefault(sector, 0.0); }
    /** ...against the families'. */
    public double getHouseholdAllowance() { return householdAllowance; }
    /** 2 when that sector's book is in trouble - most of its firms past the watch line - 1 when most are sound. Since 0.7.8 the allowance stages it firm by firm (getStageTwoShare()); this is which side of half it is on. */
    public int getStage(String sector) { return sectorsWatched.contains(sector) ? 2 : 1; }
    /** The share of that sector's book in stage 2, as the last provide() struck it (stageTwoShare()). Saved with the allowance. */
    public double getStageTwoShare(String sector) { return stageTwoShares.getOrDefault(sector, 0.0); }
    /** 2 when any family's line is in trouble, 1 when none is. */
    public int getHouseholdStage() { return householdWatchedDebt > 0 ? 2 : 1; }
    /** The families' debt in the cells that are in trouble. */
    public double getHouseholdWatchedDebt() { return householdWatchedDebt; }
    /** The sectors whose books are in stage 2. */
    public java.util.Set<String> getSectorsWatched() { return java.util.Collections.unmodifiableSet(sectorsWatched); }
    /** How many of its books are in stage 2: each sector's, and the families' as one (0.7.9, the Bank tab's count of borrowers in trouble). */
    public int getBooksWatched() { return sectorsWatched.size() + (householdWatchedDebt > 0 ? 1 : 0); }
    /** The allowance the month opened with. */
    public double getOpeningAllowance() { return openingAllowance; }

    /**
     * THE PROVISION, the income statement's line: what the allowance rose by
     * this month and whatever was written off that it had not set aside -
     * which is the allowance's move plus every write-off. Negative when a
     * borrower recovered and its allowance is released.
     */
    public double provisions() { return getAllowance() - openingAllowance + writeOffs; }

    /** The month's write-offs that the allowance had already set aside. */
    public double getAllowanceUsed() { return allowanceUsed; }

    /** ...and the part it had not, which reached the statement the month it was written off. */
    public double getWriteOffsBeyondAllowance() { return writeOffs - allowanceUsed; }

    /** The part of the provision that went into the allowance: its rise less what the write-offs drew out of it. */
    public double getProvisionCharge() { return provisions() - getWriteOffsBeyondAllowance(); }

    /* -------------------------- what it holds -------------------------- */

    /** The least buffer a bank holds over the minimum: the Basel III capital conservation buffer, 2.5% of risk-weighted assets (BCBS, December 2010). */
    public static final double CONSERVATION_BUFFER = .025;

    /** How far over its target a bank runs before it calls its capital surplus: 2.5 points, the gap between the ~13.5% Canada's big banks held and the 11% OSFI expected of them (June 2026). */
    public static final double MANAGEMENT_CUSHION = .025;

    /** A year, in months: the loss record's window, the dividends' and buybacks' "over the year", and how long an excess takes to return. */
    public static final int YEAR_MONTHS = 12;

    /** The last year's provisions and weighted book, a ring of the months the bank had a branch. */
    private final double[] lossRing = new double[YEAR_MONTHS];
    private final double[] riskRing = new double[YEAR_MONTHS];
    private int lossMonths;
    /** The worst year's provisions over its average weighted book it has recorded: the loss the buffer is sized to take. */
    private double worstLossRate;

    /** Files the month that has just closed into the loss record. closeMonth() only; a month with no branch is not the bank's. */
    private void recordLosses() {
        if (branches <= 0) return;
        int slot = lossMonths % YEAR_MONTHS;
        lossRing[slot] = provisions();
        riskRing[slot] = getWeightedBook();
        lossMonths++;
        if (lossMonths < YEAR_MONTHS) return;
        double rate = trailingLossRate();
        if (rate > worstLossRate) worstLossRate = rate;
    }

    /**
     * The last twelve recorded months' provisions over their average weighted
     * book - or over what its branches' founding capital is built to carry at
     * the minimum (branches x paidInPerBranch / CAPITAL_RATIO), whichever is
     * larger; over what there is, for a younger bank; 0 with nothing lent.
     *
     * THE FOUNDING GUARD, for the reason the running costs have one
     * (strikePrices()): a loss on a book of almost nothing is not a loss
     * rate. Measured without it, on BankCheck's played city: a two-branch
     * bank with $35M of equity and a $9M book wrote a sector down and read a
     * year of 141% - so it chose a capital target of 149% and then 254%, and
     * since its loans are priced on its target (capitalCharge()) quoted the
     * carry trade 45%. A young bank's small book is covered by the capital
     * its branches were founded with, which it never pays out (topEquity());
     * the rate the buffer is sized on is the one it would lose at scale.
     */
    public double trailingLossRate() {
        int n = Math.min(lossMonths, YEAR_MONTHS);
        if (n <= 0) return 0;
        double lost = 0, weighed = 0;
        for (int i = 0; i < n; i++) { lost += lossRing[i]; weighed += riskRing[i]; }
        double average = Math.max(weighed / n, branches * paidInPerBranch / CAPITAL_RATIO);
        return average > 0 ? lost * YEAR_MONTHS / n / average : 0;
    }

    /** The worst year it has lived through, as the capital target reads it. */
    public double getWorstLossRate() { return worstLossRate; }

    /**
     * The most buffer a bank holds over the minimum, however bad a year it
     * has seen: 8.5 points, the whole Basel III stack - the 2.5-point
     * conservation buffer, a countercyclical buffer at its 2.5-point ceiling
     * and the 3.5-point top G-SIB surcharge (BCBS, 2010 and 2013) - so a
     * target of 16.5% at most, where Canada's big banks hold about 13.5%.
     *
     * MEASURED BEFORE IT WAS HERE, and it is the difference between a bank
     * and a lock on the city's credit. A whole sector was one borrower in
     * this game until 0.7.8's partial defaults, so a single restructure was a
     * year of 20-100% of the book (0.7.7's own finding,
     * Bank.expectedLossRate()): seed 0 of the default
     * playtest wrote $113M off a $300M book in month 171 and $215M off a
     * $490M one in month 1,386, so the bank chose a target of 48% and then
     * 108%. Every loan is priced on the target (capitalCharge()), so prime
     * went to 10-19% on dials of 1-4% for the rest of the run, lending was
     * rationed for 1,694 months, and the city ended at 8,348 people against
     * 126,656; seed 3 failed its bank 27 times. A real bank does not size a
     * buffer for losing its whole book in a year - it keeps any one
     * borrower from being that much of it - and no regulator asks for more
     * buffer than the whole stack.
     *
     * A PRICE IN ITS PLACE WAS MEASURED AND WAS WORSE (the gate's second
     * version of 0.7.8). The target was the bank's stress test on its
     * largest borrower - that sector's loss at the restructure rule, 60% of
     * what it owed, over the weighted book - with no cap; loans were priced
     * and rationed at the minimum and the conservation buffer; and a sector
     * owed more than a quarter of the bank's capital (Basel's large-exposure
     * line) paid the capital its lending needed, 60% of it, at the owners'
     * return over funding: 5.2-6.1 points over its rate at the default
     * dials. Sectors paid it in 8,200-10,400 sector-months a seed, most of
     * them Real Estate, Manufacturing and Automotive, typically owing 0.6-1.6
     * times the bank's capital and at the most 9-19 times.
     * Over the eight default seeds the bank failed 68 times rather than 85,
     * but unemployment ended at 21.5% against 11.8%, $233bn was written off
     * against $167bn and GDP was 11% lower; held at a 10% dial it failed 195
     * times against 176, and on the autopilot 114 against 74. A whole sector
     * was one borrower, so the bank's largest default averaged 48-62% of its
     * weighted book and its target 51-65%; it lent freely at the ordinary
     * target while short of that, and the concentration price broke the
     * borrowers it was meant to slow. The batch's notes have the rest.
     */
    public static final double MAX_BUFFER = .085;

    /** Its buffer over the minimum: the worst year it has recorded, never less than CONSERVATION_BUFFER nor more than MAX_BUFFER. */
    public double capitalBuffer() { return Math.min(MAX_BUFFER, Math.max(CONSERVATION_BUFFER, worstLossRate)); }

    /** THE TARGET it chooses: the city's minimum and its own buffer. */
    public double capitalTarget() { return CAPITAL_RATIO + capitalBuffer(); }

    /** ...and the top of its band. */
    public double capitalTop() { return capitalTarget() + MANAGEMENT_CUSHION; }

    /* --------------------- the leverage ratio (0.7.11, round 2) --------------------- */

    /**
     * THE EXPOSURE MEASURE the leverage ratio is struck on: everything on
     * its balance sheet at the value the sheet carries it at, whatever it
     * weighs - totalAssets(). That is:
     *   - the loans, net of the allowance (netLoans()), insured mortgages
     *     and the city's paper included;
     *   - the carry book;
     *   - the desk's shares;
     *   - its cash, which is its reserves at the central bank.
     * Basel counts every one of those. Loans go in net of specific
     * provisions (LEV30), and central-bank reserves go in too, apart from
     * the temporary exemptions some jurisdictions granted. The families'
     * savings are not on this sheet (THE THREE STATEMENTS says why), so they
     * are not here either.
     *
     * One simplification, on the safe side: the city's paper is at face,
     * with the discount not yet earned carried as a liability
     * (totalLiabilities()) rather than netted against it. So the exposure is
     * that discount larger than an accountant's.
     */
    public double exposure() { return Math.max(0, totalAssets()); }

    /** Equity over the exposure measure: the leverage ratio a regulator reads. Infinite with nothing on the sheet. */
    public double leverageRatio() {
        double e = exposure();
        return e > 0 ? equity() / e : Double.MAX_VALUE;
    }

    /**
     * ITS OWN LEVERAGE TARGET: LEVERAGE_RATIO_MIN scaled by the buffer it
     * chose on the risk side - LEVERAGE_RATIO_MIN x capitalTarget() /
     * CAPITAL_RATIO. Derived, not a new number. A bank that holds twice
     * the risk-based minimum as its target holds twice the leverage minimum
     * too. The two requirements then keep the same proportion between
     * minimum and target, so whichever binds at the minimum binds at the
     * target and at the top of the band as well.
     */
    public double leverageTarget() { return LEVERAGE_RATIO_MIN * capitalTarget() / CAPITAL_RATIO; }

    /** ...and the top of its band on the same measure: LEVERAGE_RATIO_MIN x capitalTop() / CAPITAL_RATIO. */
    public double leverageTop() { return LEVERAGE_RATIO_MIN * capitalTop() / CAPITAL_RATIO; }

    /**
     * THE MINIMUM THE CITY REQUIRES, IN MONEY: the larger of the risk-based
     * one, CAPITAL_RATIO of the weighted book, and the leverage one,
     * LEVERAGE_RATIO_MIN of the exposure. It - and the target and band
     * struck on the same two measures (targetEquity(), topEquity()) - is
     * what every comparison the bank makes with a requirement reads:
     *   - failure and the under-minimum regime (payoutStance(),
     *     lendsOnlyToKeepBorrowersGoing(), recapitalisationNeeded(),
     *     resolutionExitEquity());
     *   - its target and band (targetEquity(), topEquity());
     *   - the payout and the two spare-capital limits (dividendDue(),
     *     buybackRoom(), deskCanCarry());
     *   - the lending that tightens under target (lendingGrowthLimit());
     *   - what its capital supports (capitalLimit()).
     */
    public double minimumEquity() {
        return Math.max(CAPITAL_RATIO * getWeightedBook(), LEVERAGE_RATIO_MIN * exposure());
    }

    /** True when the leverage requirement is the larger - when a bank's zero-weighted assets are what its capital is short against. */
    public boolean leverageBinds() {
        return LEVERAGE_RATIO_MIN * exposure() > CAPITAL_RATIO * getWeightedBook();
    }

    /** Its capital as a ratio on the measure that binds: the leverage ratio when leverageBinds(), the risk-based capitalRatio() otherwise - the figure the Bank tab's bar and status read. */
    public double bindingRatio() { return leverageBinds() ? leverageRatio() : capitalRatio(); }

    /** The minimum on the binding measure: LEVERAGE_RATIO_MIN or CAPITAL_RATIO. */
    public double bindingMinimum() { return leverageBinds() ? LEVERAGE_RATIO_MIN : CAPITAL_RATIO; }

    /** Its target on the binding measure: leverageTarget() or capitalTarget(). */
    public double bindingTarget() { return leverageBinds() ? leverageTarget() : capitalTarget(); }

    /** ...and the top of its band on it: leverageTop() or capitalTop(). */
    public double bindingTop() { return leverageBinds() ? leverageTop() : capitalTop(); }

    /* ------------------------ what it does with profit ------------------------ */

    /** The share of its profit after tax a bank inside its band pays its owners: 45%, inside the 40-50% payout range Canada's big banks target; RBC paid 43% of its 2025 earnings. */
    public static final double PAYOUT_IN_BAND = .45;

    /** How many months a bank over the top of its band takes to return the excess: a year, the term of a normal-course issuer bid on the TSX. */
    public static final double EXCESS_PAYOUT_MONTHS = YEAR_MONTHS;

    /** What the bank does with its profit, as its capital stands. */
    public enum Payout { NO_BANK, FAILED, UNDER_MINIMUM, REBUILDING, PAYING, RETURNING }

    /**
     * The equity its target calls for on the book it has: the larger of its
     * target on the weighted book and its leverage target on the exposure
     * (0.7.11, round 2 - minimumEquity() says why).
     */
    public double targetEquity() {
        return Math.max(capitalTarget() * getWeightedBook(), leverageTarget() * exposure());
    }

    /**
     * The equity at the top of its band - and NEVER LESS THAN WHAT ITS
     * STANDING BRANCHES WERE FOUNDED WITH, paidInPerBranch each: the capital
     * a counter is opened with is what the running costs are already priced
     * on (strikePrices()) and what one is worth to the city, and a young bank
     * whose book is still small does not hand its founders their money back
     * the year it opens.
     */
    public double topEquity() {
        return Math.max(Math.max(capitalTop() * getWeightedBook(), leverageTop() * exposure()),
                branches * paidInPerBranch);
    }

    /** What it holds past the top of its band: what it returns, a twelfth a month. Nothing for a failed bank or no bank. */
    public double excessCapital() {
        if (branches <= 0 || isInsolvent()) return 0;
        return Math.max(0, equity() - topEquity());
    }

    /** Where its capital puts it, for the words and the rules. */
    public Payout payoutStance() {
        if (branches <= 0) return Payout.NO_BANK;
        if (isInsolvent()) return Payout.FAILED;
        // Under the larger of the two minimums since round 2 of 0.7.11.
        double minimum = minimumEquity();
        if (minimum > 0 && equity() < minimum) return Payout.UNDER_MINIMUM;
        if (equity() < targetEquity()) return Payout.REBUILDING;
        return excessCapital() > 0 ? Payout.RETURNING : Payout.PAYING;
    }

    /** ...in words, for the Bank tab. */
    public String payoutDecision() {
        return switch (payoutStance()) {
            case NO_BANK       -> "no bank yet";
            case FAILED        -> "failed - pays nothing";
            case UNDER_MINIMUM -> "under its minimum - pays nothing";
            case REBUILDING    -> "rebuilding capital - pays nothing";
            case PAYING        -> String.format("paying out %.0f%% of its profit", PAYOUT_IN_BAND * 100);
            case RETURNING     -> "returning excess capital";
        };
    }

    /**
     * WHAT IT PAYS ITS OWNERS this month, on last month's profit after tax.
     * Nothing under its target; inside its band PAYOUT_IN_BAND of a profit,
     * never so much that it would fall under the target; over the top that
     * and a twelfth of the excess. Replaces Equity.PAYOUT for the bank only
     * - the sectors keep theirs, and Equity.requiredYield() still values the
     * shares on it, which is the market's convention and not this bank's
     * decision.
     */
    public double dividendDue(double profitAfterTax) {
        if (branches <= 0 || isInsolvent()) return 0;
        double overTarget = equity() - targetEquity();
        if (overTarget <= 0) return 0;
        double ordinary = profitAfterTax > 0 ? PAYOUT_IN_BAND * profitAfterTax : 0;
        return Math.min(overTarget, ordinary + excessCapital() / EXCESS_PAYOUT_MONTHS);
    }

    /**
     * Pays its owners what dividendDue() says, and keeps what the rule read -
     * the profit it was paid on, the excess over the top and the room over
     * the target - so the month's decision can be read back. Game's one call.
     *
     * @return what it paid
     */
    public double payOwners(double profitAfterTax) {
        payoutProfit = profitAfterTax;
        payoutExcess = excessCapital();
        payoutOverTarget = branches <= 0 || isInsolvent() ? 0 : Math.max(0, equity() - targetEquity());
        double due = dividendDue(profitAfterTax);
        payDividend(due);
        return due;
    }

    /** What the month's payout read: the profit after tax it was paid on, what the bank held over the top of its band, and over its target. Flows of the month. */
    private double payoutProfit, payoutExcess, payoutOverTarget;
    public double getPayoutProfit()     { return payoutProfit; }
    public double getPayoutExcess()     { return payoutExcess; }
    public double getPayoutOverTarget() { return payoutOverTarget; }

    /* --------------------------- its own shares --------------------------- */

    /*
     * THE DESK DEALS IN THE BANK'S OWN SHARES BY ITS CAPITAL RULE (0.7.8):
     * it buys them back from whoever sells while the bank is at or over its
     * own target (buysBackOwnShares()) and only with what it holds over it
     * (buybackRoom(), round 4), and issues new ones to whoever buys
     * while it is under it (issuesOwnShares()) - raising what it is short of,
     * never what it would hand back - at the pace of a buyback, and never on
     * a record the register reads as new or bad (Exchange). What the bank
     * returns on purpose it returns as dividends (dividendDue()).
     *
     * TWO RULES WERE MEASURED FIRST AND BOTH WERE WORSE. Buying back only
     * over the top of the band, up to the excess, and issuing only while
     * rebuilding: whether a family short of money could sell its bank shares
     * turned on a few dollars of a young bank's excess, and holding
     * MonetaryCheck's dial a month later moved its 40% row by 0.073 points
     * against an allowance of 0.05. Buying back over the top on the ratio
     * alone, never issuing: the desk bought a young bank's founding capital
     * back from its sellers at $100-200k a month with nobody buying new
     * shares, and held at a 10% dial the eight seeds failed the bank 252
     * times against 0.7.7's 40. Until 0.7.8 it did both
     * whenever the bank held twice the
     * minimum - issuing into the very surplus a bank returns - and booked
     * them as trading income: seed 0 of the 0.7.7 playtest sold $12.97bn of
     * the bank's own shares to the households and bought $4.74bn back, and
     * the net $8.2bn was nearly all of its $8.65bn of "trading income" over
     * the run, taxed and paid out as profit. A share issued or bought back
     * moves equity, not income, like a dividend: its own lines,
     * getSharesIssued() and getSharesBoughtBack().
     */
    private double sharesBoughtBack, sharesIssued;

    /** The desk bought the bank's own shares back and cancelled them: cash out, equity down, no income. */
    public void buyBackOwnShares(double paid) {
        if (!(paid > 0)) return;
        cash -= paid;
        sharesBoughtBack += paid;
        buybackRing[payoutMonths % YEAR_MONTHS] += paid;
    }

    /** ...and issued new ones: cash in, equity up, no income. The crossing is the exchange's to declare. */
    public void issueOwnShares(double received) {
        if (!(received > 0)) return;
        cash += received;
        sharesIssued += received;
    }

    /**
     * True when the desk buys the bank's own shares back from whoever sells:
     * standing, and at or over its own capital target. Read at every deal, so
     * a buyback that takes it under its target is the last one.
     */
    public boolean buysBackOwnShares() {
        if (branches <= 0 || isInsolvent()) return false;
        return targetEquity() <= 0 || equity() >= targetEquity();
    }

    /**
     * ...and when it issues new ones to whoever buys: standing, lending, and
     * UNDER its own target - raising the capital its rule says it is short
     * of, and never while it holds what it wants. The first reading of 0.7.8
     * issued at or over the target too, and the eight default seeds sold
     * $28-86bn of new bank shares each to households, created an excess with
     * them and paid it straight back as dividends - 131-183% of the bank's
     * profit over the growth years. A board does not raise equity to hand it
     * back. Read at every deal, like the buyback.
     *
     * UNDER IT BY MORE THAN ROUNDING (0.7.11): a desk that has bought its
     * shares back spends exactly what it holds over its target
     * (buybackRoom()), which leaves its equity ON the target, and whether it
     * then issued to the next buyer was decided by the last bit of a
     * subtraction. OWN_ISSUE_DEAD_BAND is that bit. Found by
     * DenominationCheck's twins, which parted on it the month their bank
     * first sat at its target - the landlords' insured mortgages weigh
     * nothing, so the weighted book it is measured against shrank to where
     * its equity was.
     */
    public boolean issuesOwnShares() {
        if (branches <= 0 || isInsolvent()) return false;
        return targetEquity() > 0 && equity() < targetEquity() * (1 - OWN_ISSUE_DEAD_BAND);
    }

    /** How far under its target, as a share of it, the bank must be before it issues its own shares: a billionth - rounding, not a rule - so a buyback that stopped exactly on the target does not turn into an issue on the last bit (issuesOwnShares()). */
    public static final double OWN_ISSUE_DEAD_BAND = 1e-9;

    /**
     * WHAT IT HOLDS OVER ITS TARGET: its equity less targetEquity(), with the
     * desk's inventory carried at `inventory` rather than at the securities
     * line's last mark - the line lags the desk's deals within a month until
     * the exchange re-marks it (Exchange.markToMarket()), so the cash a deal
     * paid is gone from equity and the shares it bought are not yet on it.
     * The capital the desk may spend on the bank's own shares
     * (buybackRoom()) and carry other companies' with (deskCanCarry()).
     * Negative when it is under its target.
     */
    public double spareCapital(double inventory) {
        return Math.min(spareOnRisk(inventory), spareOnLeverage(inventory));
    }

    /** Its spare capital against its target on the weighted book, the inventory carried at `inventory`. */
    private double spareOnRisk(double inventory) {
        double inv = Math.max(0, inventory);
        double equityNow = equity() - Math.max(0, securities) + inv;
        double weightedNow = getWeightedBook() - Math.abs(securities) * RISK_EQUITY + Math.abs(inventory) * RISK_EQUITY;
        return equityNow - capitalTarget() * weightedNow;
    }

    /**
     * ...and against its leverage target on the exposure (0.7.11, round 2).
     * A share it holds is an asset at the mark, so the inventory moves the
     * exposure one for one, and it moves equity the same way.
     */
    private double spareOnLeverage(double inventory) {
        double inv = Math.max(0, inventory);
        double equityNow = equity() - Math.max(0, securities) + inv;
        double exposureNow = Math.max(0, exposure() - Math.max(0, securities) + inv);
        return equityNow - leverageTarget() * exposureNow;
    }

    /** ...on the books as they stand: equity() less targetEquity(). */
    public double spareCapital() { return spareCapital(securities); }

    /**
     * THE MOST IT MAY SPEND BUYING ITS OWN SHARES BACK NOW: what it holds over
     * its target, spareCapital(inventory), so that no purchase takes it under
     * the target - a month's buybacks never exceed the capital over target at
     * the time. A share bought back is cancelled, so the purchase moves its
     * equity by what it paid and its weighted book not at all. The desk also
     * keeps to the buyback pace and the record guard (Exchange), and to
     * buysBackOwnShares(); the tightest holds. Jerus, 2026-09-24: "Buybacks
     * only from spare capital" - what his batch-2 rule meant.
     *
     * Until 0.7.8's round 4 nothing bounded a deal by the excess:
     * buysBackOwnShares() was read before each one, "so a buyback that takes
     * it under its target is the last one", and the last one could be the
     * whole of a month's selling. Measured in round 3, 68 of the default
     * run's 101 failures were months the desk bought back a median 131% of
     * the equity the bank opened with.
     */
    public double buybackRoom(double inventory) {
        return Math.max(0, spareCapital(inventory));
    }

    public double getSharesBoughtBack() { return sharesBoughtBack; }
    public double getSharesIssued()     { return sharesIssued; }

    /** The owners' year: dividends and buybacks, a ring of months with this one in it. */
    private final double[] dividendRing = new double[YEAR_MONTHS];
    private final double[] buybackRing = new double[YEAR_MONTHS];
    private int payoutMonths;

    /** Dividends over the last twelve months, this one included. */
    public double dividendsOverYear() { double s = 0; for (double v : dividendRing) s += v; return s; }
    /** ...and its own shares bought back. */
    public double buybacksOverYear()  { double s = 0; for (double v : buybackRing) s += v; return s; }

    /** This month's net income over the equity it opened with, a year: the return a bank is read by. Nothing on no equity. */
    public double returnOnEquity() {
        return openingEquity > 0 ? getNetIncome() * 12 / openingEquity : 0;
    }

    /* ------------------------------ what it lends ------------------------------ */

    /** A borrower's debt may grow this much a month halfway between the minimum and the target: 1%, the top of the 0-1% a month Jerus's brief gave a bank short of capital; nothing at the minimum, no limit at the target (lendingGrowthLimit()). */
    public static final double RATIONED_GROWTH = .01;

    /**
     * HOW FAST THE BANK LETS A BORROWER'S DEBT GROW THIS MONTH, on the capital
     * it has: no limit at or over its target (and with no branch - a city
     * with no bank is lent to from outside); none under the minimum or
     * failed; in between RATIONED_GROWTH x x/(1-x), where x is how far from
     * the minimum to the target the ratio stands - nothing at the minimum,
     * RATIONED_GROWTH halfway, rising without bound toward the target, so
     * the limit loosens smoothly into none.
     */
    public double lendingGrowthLimit() {
        if (branches <= 0) return Double.POSITIVE_INFINITY;
        if (isInsolvent()) return 0;
        /*
         * ON THE MEASURE THAT BINDS (0.7.11, round 2): the leverage ratio
         * between LEVERAGE_RATIO_MIN and leverageTarget() when the leverage
         * requirement is the larger, the risk-based ratio between the two it
         * always read otherwise. The two keep the same proportion
         * (leverageTarget()), so x means the same thing on either.
         */
        if (leverageBinds()) {
            double ratio = leverageRatio(), target = leverageTarget();
            if (ratio >= target) return Double.POSITIVE_INFINITY;
            if (ratio <= LEVERAGE_RATIO_MIN) return 0;
            double x = (ratio - LEVERAGE_RATIO_MIN) / (target - LEVERAGE_RATIO_MIN);
            return RATIONED_GROWTH * x / (1 - x);
        }
        double weighted = getWeightedBook();
        if (weighted <= 0) return Double.POSITIVE_INFINITY;
        double ratio = equity() / weighted;
        double target = capitalTarget();
        if (ratio >= target) return Double.POSITIVE_INFINITY;
        if (ratio <= CAPITAL_RATIO) return 0;
        double x = (ratio - CAPITAL_RATIO) / (target - CAPITAL_RATIO);
        return RATIONED_GROWTH * x / (1 - x);
    }

    /** True when it lends only what keeps its existing borrowers going: under the minimum, or failed. */
    public boolean lendsOnlyToKeepBorrowersGoing() {
        if (branches <= 0) return false;
        if (isInsolvent()) return true;
        // Under the larger of the two minimums (0.7.11, round 2).
        double minimum = minimumEquity();
        return minimum > 0 && equity() < minimum;
    }

    /** The growth of the book the capital rule allows this month, in money: infinite when it lends freely. */
    public double lendingLimit() {
        double g = lendingGrowthLimit();
        // ...on the book the binding measure counts (0.7.11, round 2).
        return Double.isInfinite(g) ? Double.POSITIVE_INFINITY
                : g * (leverageBinds() ? exposure() : getWeightedBook());
    }

    /** ...in words. */
    public String lendingStance() {
        if (branches <= 0) return "no bank - the city is lent to from outside";
        if (inResolution) return "failed - lends nothing until it is recapitalised";
        if (lendsOnlyToKeepBorrowersGoing()) return "under its minimum - lends only to keep its borrowers going";
        double g = lendingGrowthLimit();
        if (Double.isInfinite(g)) return "lends freely";
        return String.format("rebuilding capital - a borrower's debt may grow %.2f%% this month", g * 100);
    }

    /*
     * NO LARGE-EXPOSURE LIMIT, AND THAT IS DECIDED (0.7.8, round 4). Tried
     * 2026-09-23 on Basel's large-exposures rule - at most 25% of Tier 1
     * capital in one counterparty (BCBS, "Supervisory framework for measuring
     * and controlling large exposures", 2014) - with the rest of a loan past
     * it syndicated abroad at the loan's own rate, and a sector counted as one
     * connected borrower. Over the eight default seeds it sent 99% of the
     * businesses' borrowing abroad, put the interest paid abroad at 9-14% of
     * GDP a year and prices at up to 3.9 times their founding level, and left
     * the bank an arranger with $16-120M of equity; it failed 101 times.
     * Removed (Jerus, 2026-09-24: "Drop syndication, keep quarterly") because
     * a sector is many firms - the premise its partial defaults are built on
     * (BusinessDebtManager.defaultProbability()) - and Basel's rule is for a
     * firm or a group tied by control, not an industry.
     */

    /* ------------------------------- the save ------------------------------- */

    /**
     * The allowance, book by book (0.7.8): each sector's name, and
     * HOUSEHOLD_BOOK for the families, to {the allowance, what it held when
     * the month opened, what the month wrote off, and whether it is in
     * trouble - 1 or 0 for a sector, the debt in trouble for the families -
     * and, for a sector since 0.7.8, the share of its book in stage 2}.
     * A stock and four things about the month, none of which the end of a
     * month can give back.
     */
    public java.util.Map<String, double[]> allowanceToSave() {
        java.util.Map<String, double[]> out = new java.util.LinkedHashMap<>();
        java.util.Set<String> keys = new java.util.LinkedHashSet<>(sectorAllowance.keySet());
        keys.addAll(openingSectorAllowance.keySet());
        keys.addAll(writtenOffBySector.keySet());
        keys.addAll(sectorsWatched);
        keys.addAll(stageTwoShares.keySet());
        for (String k : keys) {
            out.put(k, new double[]{ sectorAllowance.getOrDefault(k, 0.0),
                    openingSectorAllowance.getOrDefault(k, 0.0),
                    writtenOffBySector.getOrDefault(k, 0.0),
                    sectorsWatched.contains(k) ? 1 : 0,
                    stageTwoShares.getOrDefault(k, 0.0) });
        }
        out.put(HOUSEHOLD_BOOK, new double[]{ householdAllowance, openingHouseholdAllowance,
                householdWrittenOff, householdWatchedDebt });
        return out;
    }

    /** ...and back. @return false for a save from before 0.7.8, which carries none - Game sets one up (openAllowance()). */
    public boolean restoreAllowance(java.util.Map<String, double[]> saved) {
        if (saved == null) return false;
        sectorAllowance.clear();
        openingSectorAllowance.clear();
        writtenOffBySector.clear();
        sectorsWatched.clear();
        stageTwoShares.clear();
        householdAllowance = openingHouseholdAllowance = householdWrittenOff = householdWatchedDebt = 0;
        for (java.util.Map.Entry<String, double[]> e : saved.entrySet()) {
            double[] v = e.getValue();
            if (e.getKey() == null || v == null || v.length < 4) continue;
            if (HOUSEHOLD_BOOK.equals(e.getKey())) {
                householdAllowance = v[0];
                openingHouseholdAllowance = v[1];
                householdWrittenOff = v[2];
                householdWatchedDebt = v[3];
                continue;
            }
            if (v[0] != 0) sectorAllowance.put(e.getKey(), v[0]);
            if (v[1] != 0) openingSectorAllowance.put(e.getKey(), v[1]);
            if (v[2] != 0) writtenOffBySector.put(e.getKey(), v[2]);
            if (v[3] != 0) sectorsWatched.add(e.getKey());
            // An older 0.7.8 save carried the flag alone: the whole book, or none of it.
            double share = v.length >= 5 ? v[4] : (v[3] != 0 ? 1 : 0);
            if (share != 0) stageTwoShares.put(e.getKey(), share);
        }
        openingAllowance = sum(openingSectorAllowance) + openingHouseholdAllowance;
        return true;
    }

    private static double sum(java.util.Map<String, Double> m) {
        double s = 0;
        for (double v : m.values()) s += v;
        return s;
    }

    /**
     * The record the target and the owners' year are struck from (0.7.8):
     * the months recorded, the worst year, the rings of provisions and of
     * the weighted book, the owners' month count and the rings of dividends
     * and buybacks. An older save carries none: a bank with no record, which
     * holds the standard buffer until it has lived a year.
     */
    public double[] capitalRecordToSave() {
        double[] out = new double[3 + 4 * YEAR_MONTHS];
        int i = 0;
        out[i++] = lossMonths;
        out[i++] = worstLossRate;
        out[i++] = payoutMonths;
        for (double[] ring : new double[][] { lossRing, riskRing, dividendRing, buybackRing }) {
            System.arraycopy(ring, 0, out, i, ring.length);
            i += ring.length;
        }
        return out;
    }

    public void restoreCapitalRecord(double[] in) {
        if (in == null || in.length != 3 + 4 * YEAR_MONTHS) return;
        int i = 0;
        lossMonths = (int) Math.round(in[i++]);
        worstLossRate = in[i++];
        payoutMonths = (int) Math.round(in[i++]);
        for (double[] ring : new double[][] { lossRing, riskRing, dividendRing, buybackRing }) {
            System.arraycopy(in, i, ring, 0, ring.length);
            i += ring.length;
        }
    }

    /**
     * THE MONTH'S STATEMENT LINES (0.7.8), for the save. Every flow startMonth()
     * clears, in a fixed order the reader keeps: a reloaded city's Income
     * page read nothing but zeroes until a month was played, because a flow
     * cannot be reconstructed from the state a month ended in. Restoring
     * them moves nothing - the next startMonth() clears them as it clears the
     * live city's, and what next month's tax and dividend read is carried
     * separately (profitLastMonth, lateProfit()). New lines go on the end.
     */
    public double[] monthLinesToSave() {
        return new double[]{
                interestEarned, internalInterest, writeOffs, payroll, upkeep,
                lentToHouseholds, repaidByHouseholds, fundingCost, placementIncome,
                depositInterestToHouseholds, depositInterestToSectors, depositInterestToForeign,
                capitalInjected, capitalFromHome, dividendsPaid, tradingIncome, markChange,
                paperGains, paperBoughtFromHouseholds, paperSoldToCentralBank, paperBoughtFromCentralBank,
                accountFees, loanFeesPaid, loanFeesOwed,
                hotMoneyIn, hotMoneyOut, carryLent, carryRepaid, carryInterest,
                bailoutReceived, foundingSettlement, resolutionLossThisMonth, taxPaid,
                openingEquity, carriedLate, chosenDepositRate, depositPayoutHeld ? 1 : 0,
                allowanceUsed, sharesBoughtBack, sharesIssued, openingAllowance,
                payoutProfit, payoutExcess, payoutOverTarget,
                // 0.7.9's: the interest by who paid it, the treasury's
                // buybacks, and whether these are a month's at all.
                interestFromBusinesses, interestFromCity, interestFromHouseholds, discountAccreted,
                treasuryBuybackGain, monthKnown ? 1 : 0 };
    }

    /** ...and back. Nothing on an older save, whose Profit page reads zero for a month as it always did. */
    public void restoreMonthLines(double[] v) {
        if (v == null || v.length < 44) return;
        int i = 0;
        interestEarned = v[i++]; internalInterest = v[i++]; writeOffs = v[i++]; payroll = v[i++]; upkeep = v[i++];
        lentToHouseholds = v[i++]; repaidByHouseholds = v[i++]; fundingCost = v[i++]; placementIncome = v[i++];
        depositInterestToHouseholds = v[i++]; depositInterestToSectors = v[i++]; depositInterestToForeign = v[i++];
        capitalInjected = v[i++]; capitalFromHome = v[i++]; dividendsPaid = v[i++]; tradingIncome = v[i++]; markChange = v[i++];
        paperGains = v[i++]; paperBoughtFromHouseholds = v[i++]; paperSoldToCentralBank = v[i++]; paperBoughtFromCentralBank = v[i++];
        accountFees = v[i++]; loanFeesPaid = v[i++]; loanFeesOwed = v[i++];
        hotMoneyIn = v[i++]; hotMoneyOut = v[i++]; carryLent = v[i++]; carryRepaid = v[i++]; carryInterest = v[i++];
        bailoutReceived = v[i++]; foundingSettlement = v[i++]; resolutionLossThisMonth = v[i++]; taxPaid = v[i++];
        openingEquity = v[i++]; carriedLate = v[i++]; chosenDepositRate = v[i++]; depositPayoutHeld = v[i++] != 0;
        allowanceUsed = v[i++]; sharesBoughtBack = v[i++]; sharesIssued = v[i++];
        openingAllowance = v[i++];
        payoutProfit = v[i++]; payoutExcess = v[i++]; payoutOverTarget = v[i++];
        // A 0.7.8 save's lines are a month's; its interest by who paid it
        // reads nothing until the next month is played.
        monthKnown = true;
        if (v.length < 50) return;
        interestFromBusinesses = v[i++]; interestFromCity = v[i++]; interestFromHouseholds = v[i++];
        discountAccreted = v[i++]; treasuryBuybackGain = v[i++];
        monthKnown = v[i++] != 0;
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

    /**
     * True when the city should be opening another counter.
     *
     * A city with loans and no bank at all always wants one - every dollar of
     * them is funded at the window. Otherwise it is a question about the
     * strain, asked slightly early so the building is finished before the
     * bank is full rather than after.
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
        /*
         * ...AND A BANK UNDER ITS MINIMUM NEEDS CAPITAL, NOT COUNTERS EITHER
         * (0.7.11, round 2). The trap branchWouldPayForItself() describes was
         * still open. A bank under its minimum is bound by its capital, so
         * its strain is past 1 and every branch it opens is worth the
         * PAID_IN_PER_BRANCH the opening brings (capacityWith()). The test
         * below then passed whenever the book it had kept its staff: the
         * owners were recapitalising a bank one building at a time. A bank
         * under its minimum is put back by its owners (issuesOwnShares())
         * or the city (recapitalisationNeeded()), not by building branches.
         * The minimum is the larger of the two, the leverage one included.
         */
        if (lendsOnlyToKeepBorrowersGoing()) return false;
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

        double runningCost = runningCostPerBranch();
        if (runningCost <= 0) return true;       // nothing known to cost yet

        double book = lastBook;
        if (book <= 0) return false;

        /*
         * WHAT THE BOOK KEPT, since 0.7.7: last month's interest less what
         * savers and the window were paid, plus its fees, over the book. It
         * was the interest times (1 - DEPOSIT_PASS_THROUGH), when what savers
         * were paid was a fixed share of it.
         */
        // (lastKept / book: see keptPerBranch(), which is the product below)

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
        return keptPerBranch() > runningCost;
    }

    /* ----------------- a branch that does not pay is closed (0.7.11, round 2) ----------------- */

    /**
     * Closed months in a row the book must fail to keep its branches' staff
     * before the bank closes one: BusinessInvestment.DISTRESS_LOSS_MONTHS,
     * the two years a landlord or a maker loses money before it sells
     * plant it is using - the city's distress fuse, not a new number.
     */
    public static final int BRANCH_CLOSE_MONTHS = BusinessInvestment.DISTRESS_LOSS_MONTHS;

    /** Closed months in a row the book has not kept its branches' staff (branchesCoverTheirStaff()); struck at closeMonth(), carried in lastMonthToSave(). */
    private int uncoveredMonths;

    /**
     * Whether what the book kept last month covers what its branches cost -
     * branchWouldPayForItself()'s own two inputs, keptPerBranch() against
     * runningCostPerBranch(), read the other way. True with no branch, or
     * with nothing known to cost yet.
     */
    public boolean branchesCoverTheirStaff() {
        if (branches <= 0) return true;
        double runningCost = runningCostPerBranch();
        if (runningCost <= 0) return true;
        return keptPerBranch() >= runningCost;
    }

    /**
     * THE BRANCH TEST RUN IN REVERSE, AND IT WAS MISSING. Jerus: "Close
     * losing branches." Opening a branch asks whether the book the branches
     * carry pays a counter's staff (branchWouldPayForItself()). Nothing ever
     * asked it again, so a branch opened on a book that later shrank stood
     * forever. Round 1 of this batch measured it: seed 3's bank had opened
     * 85 branches while its capital was short. Its book then fell to $1.4bn,
     * 93% of it insured mortgages. The branches' $13.9M a month of payroll
     * stood against $2.0M of interest, and it failed every three months
     * with no loan lost.
     *
     * So the same inputs are read each month at the close. When the book has
     * not kept the branches' staff for BRANCH_CLOSE_MONTHS closed months in a
     * row, a branch closes. Game.runRetirement() closes one a month for as
     * long as that holds, by the path any retired building takes: the plot
     * back to the city and the material to the builders. The payroll and
     * one branch's reach of the city's savings go with it.
     *
     * NOTHING MOVES ON THE CAPITAL: what the branch was founded with is not
     * handed back, and returning capital is the payout rule's business
     * (dividendDue()). So a branch opened later where one closed brings no
     * new capital (openBranches() counts past branchesCapitalised) - the
     * closed one's never left.
     *
     * THE LAST BRANCH IS NEVER CLOSED while the bank stands: a bank is a
     * branch.
     */
    public boolean closesBranch() {
        return branches > 1 && uncoveredMonths >= BRANCH_CLOSE_MONTHS;
    }

    /** Closed months in a row the book has not kept its branches' staff. */
    public int getUncoveredMonths() { return uncoveredMonths; }

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
     * they are funded by the city's deposits or at the central bank's window
     * (abroad, until 0.7.0). Zero when the bank is comfortably inside
     * itself, which is what stops the advisor building banks it does not need.
     */
    public double bookAnotherBranchWouldCarry() {
        // What one more counter is actually worth in capacity, which is nothing
        // at all when the deposits are the limit rather than the building.
        double gain = capacityAnotherBranchWouldAdd();
        double overflow = overflowPastComfortable();
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
        double capital = weightedBookSupportedBy(equityThen);
        double funding = Math.min(Math.max(0, deposits),
                Math.max(0, branchCount) * depositsPerBranch) * LEVERAGE;
        return Math.min(capital, funding);
    }

    /**
     * How much more it could lend before it counts itself full (EASY_STRAIN):
     * what the carry trade may take - and, since 0.7.8, no more than keeps
     * the bank at its own capital target. The carry trade is its capital
     * rule's third desk (WHAT IT LENDS), and the one it rations first:
     * foreigners borrowing to take the money abroad get what the bank can
     * lend and stay at its target, and nothing while it is under it - which
     * is Jerus's "domestic first" read against capital as well as against
     * the book. Measured on capacity alone, a bank sitting in its band could
     * lend the carry trade down to 10% of its weighted book (8% over
     * EASY_STRAIN), under the target it had just paid its owners down to.
     */
    public double headroom() {
        double room = Math.max(0, capacity() * EASY_STRAIN - getWeightedBook());
        if (branches > 0) {
            room = Math.min(room, Math.max(0, Math.max(0, equity()) / capitalTarget() - getWeightedBook()));
            // ...and on the leverage target (0.7.11, round 2): a dollar lent
            // abroad is a dollar of exposure, weighing RISK_CARRY or not.
            room = Math.min(room, Math.max(0, Math.max(0, equity()) / leverageTarget() - exposure()));
        }
        return room;
    }

    public void setCash(double value) { this.cash = value; }

    public void reset() {
        cash = 0;
        branches = 0;
        uncoveredMonths = 0;
        deposits = 0;
        foreignDeposits = 0;
        sectorBook = 0;
        mortgageBook = 0;
        mortgageWeighted = 0;
        insuranceClaims = 0;
        cityBook = 0;
        unearnedDiscount = 0;
        householdBook = 0;
        sectorWeighted = 0;
        cityWeighted = 0;
        householdWeighted = 0;
        householdDeposits = 0;
        sectorDeposits = 0;
        fundingRate = 0;
        depositRate = 0;
        depositPayoutHeld = false;
        chosenDepositRate = 0;
        lastWindowShare = 1;
        lastRunningCost = 0;
        lastKept = 0;
        pricedMonths = 0;
        java.util.Arrays.fill(costRing, 0);
        java.util.Arrays.fill(costBookRing, 0);
        struckProfit = carriedLate = restoredLate = 0;
        closedThisMonth = false;
        branchesCapitalised = 0;
        inResolution = false;
        failures = 0;
        taxPaid = 0;
        profitLastMonth = 0;
        internalInterest = 0;
        // 0.7.8's: no allowance, no record, no owners' year.
        sectorAllowance.clear();
        openingSectorAllowance.clear();
        writtenOffBySector.clear();
        sectorsWatched.clear();
        stageTwoShares.clear();
        householdAllowance = openingHouseholdAllowance = householdWrittenOff = householdWatchedDebt = 0;
        openingAllowance = allowanceUsed = 0;
        lossMonths = 0;
        worstLossRate = 0;
        payoutMonths = 0;
        for (double[] ring : new double[][] { lossRing, riskRing, dividendRing, buybackRing }) {
            java.util.Arrays.fill(ring, 0);
        }
        // 0.7.9's: no year of statements, no rescues, and - once the month
        // is opened below - lines that are nobody's month.
        for (double[] month : statementRing) java.util.Arrays.fill(month, 0);
        statementsFiled = 0;
        bailoutsLifetime = 0;
        monthKnown = false;
        startMonth();
        monthKnown = false;
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
        mortgageBook      *= scale;
        mortgageWeighted  *= scale;
        insuranceClaims   *= scale;
        cityBook          *= scale;
        householdBook     *= scale;
        sectorWeighted    *= scale;
        cityWeighted       *= scale;
        householdWeighted  *= scale;
        unearnedDiscount   *= scale;
        paperGains         *= scale;
        paperBoughtFromHouseholds *= scale;
        paperSoldToCentralBank *= scale;
        paperBoughtFromCentralBank *= scale;
        // The carry book is re-derived from the stock by refreshBank(), so this
        // looks redundant - and is not. DenominationCheck asserts the reform
        // INSTANT is bit-exact, and at that instant no refresh has run yet: an
        // unscaled book here makes equity() wrong for one moment, which is
        // enough to move the currency and everything downstream of it.
        carryBook          *= scale;
        carryLent          *= scale;
        carryRepaid        *= scale;
        carryInterest      *= scale;

        interestEarned    *= scale;
        internalInterest  *= scale;
        writeOffs         *= scale;
        payroll           *= scale;
        upkeep            *= scale;
        lentToHouseholds  *= scale;
        repaidByHouseholds *= scale;
        fundingCost       *= scale;
        placementIncome   *= scale;
        openingEquity     *= scale;
        hotMoneyIn        *= scale;
        hotMoneyOut       *= scale;
        resolutionLossThisMonth *= scale;
        resolutionLossLifetime  *= scale;
        capitalInjected   *= scale;
        capitalFromHome   *= scale;
        dividendsPaid     *= scale;
        securities        *= scale;
        tradingIncome     *= scale;
        markChange        *= scale;
        bailoutReceived   *= scale;
        foundingSettlement *= scale;
        depositInterestToHouseholds *= scale;
        depositInterestToSectors    *= scale;
        depositInterestToForeign    *= scale;
        taxPaid           *= scale;
        profitLastMonth   *= scale;
        // 0.7.7's: the fees, the account fee in today's unit, what landed
        // after the close and the two rings - money, all of them; the
        // struck rates are rates and do not move.
        accountFees       *= scale;
        loanFeesPaid      *= scale;
        loanFeesOwed      *= scale;
        accountFeeBase    *= scale;
        struckProfit      *= scale;
        carriedLate       *= scale;
        restoredLate      *= scale;
        for (double[] ring : new double[][] { costRing, costBookRing }) {
            for (int i = 0; i < ring.length; i++) ring[i] *= scale;
        }
        /*
         * ...AND LAST MONTH'S FIGURES, since 0.7.8. They were left in the old
         * unit on the argument that the branch test only ever divides them by
         * each other (branchWouldPayForItself()), which it does - so scaling
         * them moves no decision - but any absolute comparison with this
         * month's was a hundredfold out for the month after a reform.
         */
        lastPayroll       *= scale;
        lastUpkeep        *= scale;
        lastInterest      *= scale;
        lastBook          *= scale;
        lastKept          *= scale;
        buybackGains      *= scale;
        // ...and 0.7.8's own: the allowance, book by book, as it stands, as
        // the month opened it and as it was written off; what it covered;
        // the shares bought back and issued; and the rings of provisions,
        // weighted book, dividends and buybacks. The worst year is a rate.
        sectorAllowance.replaceAll((k, v) -> v * scale);
        openingSectorAllowance.replaceAll((k, v) -> v * scale);
        writtenOffBySector.replaceAll((k, v) -> v * scale);
        householdAllowance        *= scale;
        openingHouseholdAllowance *= scale;
        householdWrittenOff       *= scale;
        householdWatchedDebt      *= scale;
        payoutProfit              *= scale;
        payoutExcess              *= scale;
        payoutOverTarget          *= scale;
        openingAllowance          *= scale;
        allowanceUsed             *= scale;
        sharesBoughtBack          *= scale;
        sharesIssued              *= scale;
        for (double[] ring : new double[][] { lossRing, riskRing, dividendRing, buybackRing }) {
            for (int i = 0; i < ring.length; i++) ring[i] *= scale;
        }
        // ...and 0.7.9's: the interest by who paid it, the treasury's
        // buybacks, an older save's opened allowance, the city's rescues,
        // and the year of statements - every line of it money.
        interestFromBusinesses    *= scale;
        interestFromCity          *= scale;
        interestFromHouseholds    *= scale;
        discountAccreted          *= scale;
        treasuryBuybackGain       *= scale;
        allowanceOpened           *= scale;
        bailoutsLifetime          *= scale;
        for (double[] month : statementRing) {
            for (int i = 0; i < month.length; i++) month[i] *= scale;
        }
    }


    /** Re-seeds the money CONSTANTS at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        depositsPerBranch    = DEPOSITS_PER_BRANCH / unit;
        paidInPerBranch      = PAID_IN_PER_BRANCH / unit;
        domesticCapitalScale = DOMESTIC_CAPITAL_SCALE / unit;
        accountFeeBase       = ACCOUNT_FEE / unit;
    }

    /* =====================================================================
       WHAT THE BANK TAB READS (0.7.9)

       Jerus: "a redesign of the bank UI info, cause when you click on bank
       you dont even see all the relevant stuff, lets make banks realistic."
       The tab opens on whether the bank is healthy and why - a sentence, a
       scorecard, and the ladder of its rates from the policy rate to what
       each borrower pays - with its profit, its lending, its funding and its
       capital behind it.

       EVERY FIGURE IT PRINTS IS A GETTER, here or above, because the
       interface is a separate package and a figure a screen works out for
       itself is a figure nothing checks. The old tab worked these out for
       itself: the funding limit; how much of the city's savings its
       branches reach, from the founding-dollar constants - a hundred times
       out after a currency reform; what another branch adds, and the book
       spilling past comfortable; a weight table with no term and no desk,
       which did not foot; the equity's movement without the founding
       settlement; and, elsewhere, each company's share on the desk
       (Equity.deskShare()), the history's statistics (HistorySave) and the
       rescue's guard on the treasury's cash (Game.canRecapitaliseBank()).
       What it needed that nothing kept - last month's statement, the last
       twelve months', the interest by who paid it - is kept here and saved.

       BankCheck (13) asserts the three that carry arithmetic: the ladder's
       parts add up to prime, the weight table foots to getWeightedBook(),
       and the equity's movement leaves nothing unexplained.
       ===================================================================== */

    /* ---------------------- the interest, by who paid it ---------------------- */

    /**
     * The month's interest by who paid it: the businesses, the city's coupons,
     * the families' credit lines, and the discount on the city's paper as it
     * is earned. With the carry trade's (getCarryInterest()) and its
     * reserves' at the central bank (getPlacementIncome()) they are the whole
     * of interestIncome() in a played city; a fixture's takeInterest() is on
     * no book in particular. Flows, cleared at the top of the month and saved
     * with its lines.
     */
    private double interestFromBusinesses, interestFromCity, interestFromHouseholds, discountAccreted;

    /**
     * The businesses' interest and the city's coupons, settled together: the
     * same cash and income as takeInterest() on their sum, to the bit, and
     * each kept by who paid it. Game's one call.
     */
    public void takeInterest(double fromCity, double fromBusinesses) {
        double amount = fromCity + fromBusinesses;
        if (amount <= 0) return;
        cash += amount;
        interestEarned += amount;
        internalInterest += amount;
        interestFromCity += fromCity;
        interestFromBusinesses += fromBusinesses;
    }

    /** What the businesses paid it in interest this month. */
    public double getInterestFromBusinesses() { return interestFromBusinesses; }
    /** ...the city, in coupons on the paper the bank holds. */
    public double getInterestFromCity()       { return interestFromCity; }
    /** ...the families, on their credit lines. */
    public double getInterestFromHouseholds() { return interestFromHouseholds; }
    /** ...and the discount on the city's paper it earned this month, which no cash carries. */
    public double getDiscountAccreted()       { return discountAccreted; }

    /* --------------------- what moved it between two presses --------------------- */

    /**
     * What the treasury buying its paper back gained the bank (negative: lost
     * it) since the month opened. It happens between two presses, so it is in
     * no month's income statement (sellPaperBack()), and the equity's
     * movement names it. A flow, saved with the month's lines.
     */
    private double treasuryBuybackGain;
    public double getTreasuryBuybackGain() { return treasuryBuybackGain; }

    /**
     * The allowance a save from before 0.7.8 was given on load
     * (openAllowance()): its equity fell by it between two presses. Cleared
     * with the month and not saved - a save taken after it carries the
     * allowance itself.
     */
    private double allowanceOpened;
    public double getAllowanceOpened() { return allowanceOpened; }

    /**
     * What the city has put into it in rescues over its life
     * (receiveBailout()), carried in the solvency record since 0.7.9 - a save
     * from before counts from its load.
     */
    private double bailoutsLifetime;
    public double getBailoutsLifetime() { return bailoutsLifetime; }

    /**
     * True once the month's lines are a month's: one played, or lines a save
     * carried. A new bank, or a save from before 0.7.8, opens on lines that
     * are nobody's month - the tab says so rather than articulating them,
     * and the year of statements does not file them.
     */
    private boolean monthKnown;
    public boolean isMonthKnown() { return monthKnown; }

    /* --------------------------- its year of statements --------------------------- */

    /**
     * The lines of a month's statement the Bank tab sets beside last month's
     * and adds up over a year, every one money: the interest and who paid
     * it; what savers and the window were paid; fees and their three kinds;
     * provisions and write-offs; the desk and the city's paper; what it
     * earned before provisions and costs (revenue()); its costs; profit,
     * tax and what it kept; what it paid out, raised and retained - and two
     * stocks as the month ended, the book and the equity, which the year's
     * ratios average rather than add. New lines go on the end.
     */
    public enum Line {
        INTEREST, FROM_BUSINESSES, FROM_HOUSEHOLDS, FROM_CITY, FROM_CARRY, FROM_RESERVES, DISCOUNT,
        SAVERS, WINDOW, NET_INTEREST,
        FEES, ACCOUNT_FEES, LOAN_FEES_PAID, LOAN_FEES_OWED,
        PROVISIONS, WRITE_OFFS, TRADING, PAPER_GAINS, REVENUE,
        COSTS, PAYROLL, UPKEEP, PRE_TAX, TAX, NET,
        DIVIDENDS, BUYBACKS, ISSUED, RETAINED,
        BOOK, EQUITY
    }

    /** What it earned before provisions and costs: net interest, fees, the desk and the city's paper - what its costs are read against. */
    public double revenue() { return netInterestIncome() + feeIncome() + tradingIncome + paperGains; }

    /**
     * What it kept of the month's profit once its owners were paid: net
     * income less the dividend and its own shares bought back. Negative when
     * it paid out more than the month earned - a dividend is paid on LAST
     * month's profit (payOwners()).
     */
    public double getRetained() { return getNetIncome() - dividendsPaid - sharesBoughtBack; }

    /** A line as this month stands. */
    public double thisMonth(Line line) {
        return switch (line) {
            case INTEREST        -> interestEarned;
            case FROM_BUSINESSES -> interestFromBusinesses;
            case FROM_HOUSEHOLDS -> interestFromHouseholds;
            case FROM_CITY       -> interestFromCity;
            case FROM_CARRY      -> carryInterest;
            case FROM_RESERVES   -> placementIncome;
            case DISCOUNT        -> discountAccreted;
            case SAVERS          -> depositInterest();
            case WINDOW          -> fundingCost;
            case NET_INTEREST    -> netInterestIncome();
            case FEES            -> feeIncome();
            case ACCOUNT_FEES    -> accountFees;
            case LOAN_FEES_PAID  -> loanFeesPaid;
            case LOAN_FEES_OWED  -> loanFeesOwed;
            case PROVISIONS      -> provisions();
            case WRITE_OFFS      -> writeOffs;
            case TRADING         -> tradingIncome;
            case PAPER_GAINS     -> paperGains;
            case REVENUE         -> revenue();
            case COSTS           -> operatingExpenses();
            case PAYROLL         -> payroll;
            case UPKEEP          -> upkeep;
            case PRE_TAX         -> profitBeforeTax();
            case TAX             -> taxPaid;
            case NET             -> getNetIncome();
            case DIVIDENDS       -> dividendsPaid;
            case BUYBACKS        -> sharesBoughtBack;
            case ISSUED          -> sharesIssued;
            case RETAINED        -> getRetained();
            case BOOK            -> getBook();
            case EQUITY          -> equity();
        };
    }

    /**
     * The months before this one, a year of them less this one: each filed
     * whole at the top of the month after (startMonth()), when everything
     * booked after its close is in it. statementsFiled counts them; the next
     * goes in its slot modulo the ring.
     */
    private final double[][] statementRing = new double[YEAR_MONTHS - 1][Line.values().length];
    private int statementsFiled;

    private void fileStatement() {
        double[] month = statementRing[statementsFiled % statementRing.length];
        for (Line line : Line.values()) month[line.ordinal()] = thisMonth(line);
        statementsFiled++;
    }

    /** True when last month is on file: a month was played before this one, or a save carried it. */
    public boolean knowsLastMonth() { return statementsFiled > 0; }

    /** A line as last month ended, everything booked after its close included. Nothing when none is on file. */
    public double lastMonth(Line line) {
        if (statementsFiled <= 0) return 0;
        return statementRing[(statementsFiled - 1) % statementRing.length][line.ordinal()];
    }

    /** How many months the year's figures cover: this one and those on file, YEAR_MONTHS at most. */
    public int monthsInYear() { return 1 + Math.min(statementsFiled, statementRing.length); }

    /** A line added up over monthsInYear(), this month included - for the flows; the two stocks want averageOverYear(). */
    public double overYear(Line line) {
        double sum = thisMonth(line);
        int filed = Math.min(statementsFiled, statementRing.length);
        for (int k = 1; k <= filed; k++) {
            sum += statementRing[(statementsFiled - k) % statementRing.length][line.ordinal()];
        }
        return sum;
    }

    /** ...and averaged over them: a month's worth. */
    public double averageOverYear(Line line) { return overYear(line) / monthsInYear(); }

    /**
     * What it earned over the year, at a yearly rate, on the equity it held
     * on average: the return a bank is read by, and steadier than a month's
     * (returnOnEquity()). Nothing on no equity.
     */
    public double returnOnEquityOverYear() {
        double equity = averageOverYear(Line.EQUITY);
        return equity > 0 ? averageOverYear(Line.NET) * 12 / equity : 0;
    }

    /**
     * Provisions over the year, at a yearly rate, as a share of the book it
     * held on average: its credit losses as a bank reports them - a sound
     * book's is BASE_LOSS_RATE. Nothing with nothing lent.
     */
    public double provisionRateOverYear() {
        double book = averageOverYear(Line.BOOK);
        return book > 0 ? averageOverYear(Line.PROVISIONS) * 12 / book : 0;
    }

    /**
     * Net interest income over the year, at a yearly rate, on the book it held
     * on average: its net interest margin, steadier than a month's
     * (netInterestMargin()). Nothing with nothing lent.
     */
    public double netInterestMarginOverYear() {
        double book = averageOverYear(Line.BOOK);
        return book > 0 ? averageOverYear(Line.NET_INTEREST) * 12 / book : 0;
    }

    /**
     * Its staff and branches over the year as a share of what it earned
     * before them (revenue()): the efficiency ratio, about 50-60% at a real
     * bank. NaN when it earned nothing, which has no share.
     */
    public double costShareOverYear() {
        double earned = overYear(Line.REVENUE);
        return earned > 0 ? overYear(Line.COSTS) / earned : Double.NaN;
    }

    /** The year of statements, for the save: how many are filed, how many lines each, then the ring's months in slot order. */
    public double[] statementYearToSave() {
        int lines = Line.values().length;
        double[] out = new double[2 + statementRing.length * lines];
        out[0] = statementsFiled;
        out[1] = lines;
        for (int m = 0; m < statementRing.length; m++) {
            System.arraycopy(statementRing[m], 0, out, 2 + m * lines, lines);
        }
        return out;
    }

    /**
     * ...and back. A save from before 0.7.9 carries none, and the year starts
     * with the month it was saved in (its lines are filed at the next
     * startMonth()); a record with fewer lines than today's reads the new
     * ones as nothing, and one that does not add up is not read at all.
     */
    public void restoreStatementYear(double[] in) {
        if (in == null || in.length < 2) return;
        int saved = (int) Math.round(in[1]);
        if (saved <= 0 || in.length != 2 + statementRing.length * saved) return;
        int keep = Math.min(saved, Line.values().length);
        statementsFiled = Math.max(0, (int) Math.round(in[0]));
        for (int m = 0; m < statementRing.length; m++) {
            java.util.Arrays.fill(statementRing[m], 0);
            System.arraycopy(in, 2 + m * saved, statementRing[m], 0, keep);
        }
    }

    /* ------------------------------ its rates, in a ladder ------------------------------ */

    /**
     * THE LADDER OF ITS RATES at one policy rate, read at one moment: the
     * policy rate; what savers were paid, the rate the bank chose, the share
     * of the policy rate its funding asks it to pass on and the funding
     * position that sets the share (fundingPosition()), and whether its
     * margin held the savers under the rate it chose; what a prime loan's
     * money costs it (the funds-transfer price) and the three costs over it;
     * prime; what a household's credit line starts from; what the carry
     * trade pays; the window's rate; and (0.7.11) what an insured mortgage's
     * money costs it for its ten years and the rate it is written at - the
     * same running costs over it, no loss, and since round 2 the capital
     * its leverage requirement ties up (mortgageCapital). The Bank tab's
     * centrepiece.
     */
    public record Ladder(double policy, double savers, double saversChose, double saversShare,
                         double fundingPosition, boolean saversHeld,
                         double transfer, double running, double loss, double capital, double prime,
                         double household, double carry, double window,
                         double mortgageTransfer, double mortgage, double mortgageCapital) {
        /** Savers' rate less the policy rate: under it by the bank's margin on a deposit. */
        public double saversOverPolicy()   { return savers - policy; }
        /** The funds-transfer price over the policy rate: the window's penalty on its share, and the term premium. */
        public double transferOverPolicy() { return transfer - policy; }
        /** Prime over the funds-transfer price: the running costs, the expected loss and the capital charge. */
        public double primeOverTransfer()  { return prime - transfer; }
        /** The four parts added up in prime's own order - which is prime. */
        public double parts()              { return transfer + running + loss + capital; }
        /** A borrower's rate over prime: the step each borrower's rung is labelled with - its own risk, or for the carry trade the costs it does not carry. */
        public double overPrime(double rate) { return rate - prime; }
        /** An insured mortgage's money over the policy rate: the window's penalty on its share, and the ten-year term premium. */
        public double mortgageTransferOverPolicy() { return mortgageTransfer - policy; }
        /** An insured mortgage's rate over its money: running the bank, and the capital its leverage requirement ties up (round 2). */
        public double mortgageOverTransfer() { return mortgage - mortgageTransfer; }
    }

    /** The ladder at this policy rate. */
    public Ladder ladder(double policyAnnual) {
        int term = PRIME_TERM_MONTHS;
        return new Ladder(Math.max(0, policyAnnual), depositRate, chosenDepositRate, depositShare(),
                fundingPosition(), depositPayoutHeld,
                fundsTransferPrice(policyAnnual, term), runningCostRate(), expectedLossRate(),
                capitalCharge(policyAnnual, term, RISK_BUSINESS), prime(policyAnnual),
                householdRate(policyAnnual), carryRate(policyAnnual),
                Math.max(0, policyAnnual) + CentralBank.WINDOW_PENALTY,
                fundsTransferPrice(policyAnnual, Mortgage.MORTGAGE_TERM_MONTHS),
                insuredMortgageRate(policyAnnual),
                capitalCharge(policyAnnual, Mortgage.MORTGAGE_TERM_MONTHS, RISK_INSURED_MORTGAGE));
    }

    /* ------------------------------ in words ------------------------------ */

    /**
     * THE BANK'S STATE IN ONE SENTENCE, with the figure that decides it: the
     * first thing the Bank tab says. Healthy and lending freely at or over
     * its own target; rebuilding and lending carefully between the minimum
     * and the target, at the growth its rule allows this month; under the
     * minimum and lending only to keep its borrowers going; failed; or no
     * bank at all. payoutStance() is the classification, because the same
     * capital decides what it pays out and what it lends.
     *
     * ON THE MEASURE THAT BINDS since round 2 of 0.7.11: "of everything it
     * has lent" and the leverage figures when the leverage requirement is
     * the larger (leverageBinds()), "of its risk-weighted book" otherwise.
     */
    public String status() {
        boolean onLeverage = leverageBinds();
        String measure = onLeverage ? "everything it has lent (the leverage ratio)" : "its risk-weighted book";
        double ratio = onLeverage ? leverageRatio() : getWeightedBook() > 0 ? capitalRatio() : Double.NaN;
        return switch (payoutStance()) {
            case NO_BANK -> "There is no bank: every loan in the city is funded at the central bank's"
                    + " window and priced as a bank would price it. A Commercial Bank founds one.";
            case FAILED -> String.format("Failed: it lost more than it owned, and lends nothing new until"
                    + " its capital is back to %.1f%% of %s - from a rescue, or"
                    + " from its own profit.", bindingMinimum() * RESOLUTION_EXIT_BUFFER * 100, measure);
            case UNDER_MINIMUM -> String.format("Under the minimum, and lending only to keep its borrowers"
                    + " going: its capital is %.1f%% of %s, where the city requires %.1f%%.",
                    ratio * 100, measure, bindingMinimum() * 100);
            case REBUILDING -> String.format("Rebuilding its capital, and lending carefully: %.1f%% of"
                    + " %s against the %.1f%% it aims for, so a borrower's debt may grow"
                    + " %.2f%% this month.", ratio * 100, measure, bindingTarget() * 100, lendingGrowthLimit() * 100);
            case PAYING, RETURNING -> Double.isNaN(ratio)
                    ? "Healthy, and lending freely - with nothing lent yet to weigh its capital against."
                    : String.format("Healthy, and lending freely: its capital is %.1f%% of %s,"
                    + " over the %.1f%% it aims for%s.", ratio * 100, measure, bindingTarget() * 100,
                    payoutStance() == Payout.RETURNING
                            ? String.format(", and it is returning what it holds past %.1f%% to its owners",
                                    bindingTop() * 100)
                            : "");
        };
    }

    /** Why its capital target is what it is, in words: the cap, its worst year, or the standard buffer and why. */
    public String targetReason() {
        if (worstLossRate >= MAX_BUFFER) {
            return String.format("the minimum and the whole Basel buffer stack, %.1f points - its worst year"
                    + " cost %.1f%% of its risk-weighted book, and no regulator asks for more",
                    MAX_BUFFER * 100, worstLossRate * 100);
        }
        if (worstLossRate > CONSERVATION_BUFFER) {
            return String.format("the minimum and its worst year of losses, %.1f%% of its risk-weighted"
                    + " book - so a year like that one would still leave it at the minimum", worstLossRate * 100);
        }
        if (lossMonths < YEAR_MONTHS) {
            return String.format("the minimum and the standard %.1f-point buffer - it has not lived a year"
                    + " to judge its losses by", CONSERVATION_BUFFER * 100);
        }
        return String.format("the minimum and the standard %.1f-point buffer - its worst year cost %.1f%%"
                + " of its risk-weighted book, less than that", CONSERVATION_BUFFER * 100, worstLossRate * 100);
    }

    /* ---------------------------- what its book weighs ---------------------------- */

    /** The six things on its books that capacity weighs: the four it lends on, the insured mortgages inside the businesses' (0.7.11), and the desk's shares. */
    public enum Book { BUSINESSES, CITY, FAMILIES, CARRY, DESK, MORTGAGES }

    /**
     * One row of what the book weighs: its face; the share of it its
     * remaining term counts for (maturityWeight(), on average over its loans
     * - 1 where nothing runs off); its risk weight; and what it weighs, face
     * x term x risk.
     */
    public record WeightRow(Book book, double face, double term, double risk, double weighted) { }

    /**
     * Every row, the desk's shares included: the weighted column foots to
     * getWeightedBook(). The businesses' row is what they owe that nobody
     * insures; the insured mortgages are their own row since 0.7.11, at
     * RISK_INSURED_MORTGAGE - weighed at nothing, inside the businesses'
     * weighted book, which Game.refreshBank() walks loan by loan.
     */
    public java.util.List<WeightRow> weightTable() {
        return java.util.List.of(
                weightRow(Book.BUSINESSES, sectorBook - mortgageBook, RISK_BUSINESS,
                        sectorWeighted - mortgageWeighted),
                weightRow(Book.MORTGAGES, mortgageBook, RISK_INSURED_MORTGAGE, mortgageWeighted),
                weightRow(Book.CITY, cityBook, RISK_CITY, cityWeighted),
                weightRow(Book.FAMILIES, householdBook, RISK_HOUSEHOLD, householdWeighted),
                weightRow(Book.CARRY, carryBook, RISK_CARRY, carryBook * RISK_CARRY),
                weightRow(Book.DESK, Math.abs(securities), RISK_EQUITY, Math.abs(securities) * RISK_EQUITY));
    }

    private static WeightRow weightRow(Book book, double face, double risk, double weighted) {
        double atRisk = face * risk;
        return new WeightRow(book, face, atRisk > 0 ? weighted / atRisk : 1, risk, weighted);
    }

    /* ------------------------------ its funding ------------------------------ */

    /** What the families have banked with it. */
    public double getHouseholdDeposits() { return householdDeposits; }
    /** ...and what the businesses hold in credit. getDeposits() is these two and getForeignDeposits(). */
    public double getSectorDeposits()    { return sectorDeposits; }

    /** How much of a city's savings one branch reaches, in today's money: DEPOSITS_PER_BRANCH, reformed with every other figure. */
    public double getDepositsPerBranch() { return depositsPerBranch; }
    /** What its owners put up when a branch opens, in today's money: PAID_IN_PER_BRANCH, reformed. */
    public double getPaidInPerBranch()   { return paidInPerBranch; }

    /** How much of the city's own savings its branches can reach: the branches standing times getDepositsPerBranch(). */
    public double branchReach() { return branches * depositsPerBranch; }
    /** The city's own savings with it: everything banked, less the world's. */
    public double localDeposits() { return Math.max(0, deposits - foreignDeposits); }
    /** ...the part its branches reach - which, with the world's, is depositsGathered(). */
    public double localDepositsReached() { return Math.min(localDeposits(), branchReach()); }
    /** ...and the part they do not: savings only another branch would reach. */
    public double localDepositsBeyondReach() { return localDeposits() - localDepositsReached(); }

    /** What its funding would carry: what its branches gathered, lent LEVERAGE times over - the second of capacity()'s two limits. */
    public double fundingLimit() { return depositsGathered() * LEVERAGE; }

    /* ---------------------------- another branch ---------------------------- */

    /** The capacity one more branch would add, the capital it would open with counted: nothing when the deposits are the limit and the branches already reach them all. */
    public double capacityAnotherBranchWouldAdd() { return Math.max(0, capacityWith(branches + 1) - capacity()); }

    /** The weighted book past what the bank comfortably carries, EASY_STRAIN of its capacity: what another branch could take onto its own account. */
    public double overflowPastComfortable() { return Math.max(0, getWeightedBook() - capacity() * EASY_STRAIN); }

    /** What one branch cost to run last month: its payroll and upkeep over the branches standing. Nothing with none. */
    public double runningCostPerBranch() { return branches > 0 ? (lastPayroll + lastUpkeep) / branches : 0; }

    /**
     * What one branch's share of the book kept last month: its book per
     * branch at last month's kept margin (net interest and fees over the
     * book) - what branchWouldPayForItself() weighs against
     * runningCostPerBranch(). Nothing with no branch or no book.
     */
    public double keptPerBranch() {
        return branches > 0 && lastBook > 0 ? lastBook / branches * (lastKept / lastBook) : 0;
    }

    /* --------------------------- how its equity moved --------------------------- */

    /**
     * HOW ITS EQUITY MOVED since the month opened, every cause named: what it
     * kept (net income); capital put in by its shareholders at home and
     * abroad, and by the city in a rescue; the founding settlement, the month
     * the city's first branch took the standing book over; its owners paid;
     * its own shares bought back and issued; the shortfall absorbed when it
     * failed; the gain on paper the treasury bought back between two
     * presses; and the allowance an older save was given on load.
     * residual() is what none of them explains, and must be nothing.
     *
     * THE SCREEN WORKED THIS OUT UNTIL 0.7.9, without the founding
     * settlement, the treasury's buybacks or the older save's allowance - so
     * the month the first branch opened, or a buyback between two presses,
     * printed a "Not accounted for" line and an alarm under it.
     */
    public record EquityMovement(double opening, double kept, double fromShareholders, double fromCity,
                                 double founding, double dividends, double boughtBack, double issued,
                                 double absorbed, double treasuryBuyback, double allowanceOpened,
                                 double closing) {
        /** What none of the causes explains. */
        public double residual() {
            return closing - opening - kept - fromShareholders - fromCity - founding
                    + dividends + boughtBack - issued - absorbed - treasuryBuyback + allowanceOpened;
        }
    }

    /** The month's movement, as it stands. Meaningful when isMonthKnown(). */
    public EquityMovement equityMovement() {
        return new EquityMovement(openingEquity, getNetIncome(), capitalInjected + capitalFromHome,
                bailoutReceived, foundingSettlement, dividendsPaid, sharesBoughtBack, sharesIssued,
                resolutionLossThisMonth, treasuryBuybackGain, allowanceOpened, equity());
    }

}
