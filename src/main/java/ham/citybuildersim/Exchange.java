package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The stock exchange: one order book per company, where every share that
 * changes hands meets its buyer, and the price is the last trade.
 *
 * ==================== WHY ====================
 *
 * The register (Equity) made the city's companies owned. It left every share
 * where its first buyer put it: Industry, sold to the world in month 4 when
 * the households had six weeks of pay, was foreign-owned for ever; a family
 * that left the city took its shares and was paid abroad for 333 years;
 * households whose companies had bid up nothing could buy nothing. Jerus:
 * "we are to add the exchange now, that will solve the return."
 *
 * ==================== THE BANK WAS THE DEALER, UNTIL 0.7.12 ROUND 2 ====================
 *
 * The first exchange (0.7.2 to 0.7.12 round 1) made every trade a trade with
 * the bank's desk: it quoted a bid and an ask around fair value, took the
 * other side of whatever came, and moved its quote with its inventory -
 * mid = fair x (1 - PRESSURE x (inventory - unfilled demand) / limit). A
 * seller always found a buyer at the desk's bid, and the desk's limits were
 * the market's. Jerus, 2026-09-24: "Order book for both" - the bonds went
 * onto OrderBook in round 1, and the shares follow in round 2 (the brief:
 * the dealer's quote, PRESSURE and the desk's inventory limits are replaced
 * by the book; the desk becomes one participant; everything that trades
 * shares posts orders).
 *
 * ==================== THE BOOK ====================
 *
 * One OrderBook per company: price-time priority, a trade at the resting
 * order's price, nobody obliged to trade, no trade with oneself. A
 * company's PRICE is its last trade, and its fair value - the register's
 * reckoning, Equity.fairValue(): book, or the dividend it actually pays
 * capitalised at what the world asks, whichever is more - is beside it,
 * not in it. Before its first trade a company is priced at its fair value.
 *
 * THE ORDERS ARE GOOD FOR A MONTH, as the bonds' are: at the exchange's step
 * (takeMonth(), after the dividends) every order still resting is
 * withdrawn - the sellers who waited are counted - and every participant
 * posts again from its view of the month. Between two steps the book
 * stands, and a household short of money in the next month's waterfall
 * sells into what rests there.
 *
 * ==================== WHO POSTS, AND WHAT ====================
 *
 * In this order at the step, every company in register order within each,
 * so an order that crosses what an earlier participant posted trades at
 * that resting price. Each rule is the one the participant followed with
 * the dealer, applied to a price on the book instead of a quote:
 *
 *   - THE DESK, the bank's trading desk, makes a market if it has capital:
 *     a bid at fair value less half SPREAD for what its capital carries
 *     (Bank.deskCanCarry(), the 0.7.8 limit, Basel's market-risk requirement
 *     and the leverage target, and under its two caps since round 3), an ask
 *     AT fair value for what it holds over those caps (round 4), and one at
 *     fair value plus half SPREAD for the rest of what it holds - never what
 *     it does not. The bank's own shares are its capital, not its trading:
 *     it bids for them only with what it holds over its target
 *     (Bank.buybackRoom(), "buybacks only from spare capital") and asks new
 *     ones only while under it (Bank.issuesOwnShares()), each at the buyback
 *     pace; bought, they are cancelled, and sold, issued.
 *   - EMIGRANTS sell on the way out: what the households' pool released and
 *     nobody claimed this month (HouseholdBalance.getSharesTakenAway()),
 *     asked at the price the dealer's bid at fair value used to pay them.
 *     What nobody bids for rests, and stays abroad with them when the month
 *     withdraws it - as it did when a dead bank quoted nothing.
 *   - THE WORLD buys when the dividend yield at the best ask beats its rate
 *     plus the premium by the tolerance - a bid up to the price that yields
 *     exactly that - and sells when the yield at the best bid is under it,
 *     into strength and never into a crash: its ask is never under fair value
 *     less the tolerance. FOREIGN_SPEED of the gap, as it always moved.
 *   - COMPANIES with more equity than their target and cash past a cushion
 *     bid for their own shares at fair value plus BUYBACK_TOLERANCE - an
 *     open-market programme, on the book - and cancel what they buy. What
 *     nobody sells them stays in the till (0.7.12 round 4, Jerus: "Keep it in
 *     the company"): the bid rests for the month at its limit, and no
 *     special dividend is paid by any path.
 *   - EVERY HOUSEHOLD CELL rebalances to its target by the bonds' rule
 *     (0.7.12 round 3, "Yes, same rule"): its target is what it holds and
 *     what is past its cushion together. A cell UNDER its cushion first
 *     offers HOME_SPEED of the shortfall at the market, pro rata over what it
 *     holds; then a cell PAST it buys OUT_SPEED of the excess into the best
 *     yield over the bank's deposit rate plus HOUSEHOLD_PREMIUM, best first,
 *     taking what is asked up to the price that still yields that, then the
 *     next best; what is left rests as a bid on the best (Jerus: demand
 *     chases yield). Each cell posts its own orders (0.7.12 round 2, "Each
 *     household type trades"): the household types are participants, not
 *     one pool.
 *
 * And out of order, in the middle of the month: a HOUSEHOLD CELL SHORT OF
 * MONEY sells in the waterfall (Household.settle(): savings, the city's
 * paper, the dollars abroad, the bonds, then the shares, then credit) -
 * asking, in each company it holds, the price at which the dividend it gives
 * up yields its own borrowing rate: selling is worth it only while it is
 * cheaper than the credit below. What rests at that price or better fills
 * now; the rest waits on the book until the step withdraws it, and the
 * household borrows. So the rich cells' resting bids are where a short
 * cell's shares go: they trade with each other, a transfer inside the
 * households that no audited pool sees.
 *
 * NEW SHARES ARE NOT ON THE BOOK: an offering is built the way a bond's is,
 * at one price - the last trade, or fair value before one - from every
 * cell's subscription and the world's (Equity.offer()). A company offers
 * shares in the month it builds, and the plan needs to know the month what
 * was raised: an ask resting on the book would fill later, or never, and
 * the building would be half-financed by a sale that had not happened.
 *
 * A SHARE THAT GETS TOO DEAR IS SPLIT, a hundred for one, and one that gets
 * too cheap is consolidated, one for a hundred, so that a count of shares
 * stays a number a person can read after three centuries of buybacks and
 * offerings. Every holder's count, every resting order and the last trade
 * move by the factor; nothing anybody owns changes in value.
 */
public class Exchange {

    /* ------------------------------- the dials ------------------------------- */

    /** The desk's ask over its bid, as a share of fair value. What it earns for being there. */
    public static final double SPREAD = .02;

    /** What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. */
    public static final double FOREIGN_SPEED = .10;

    /** ...and it holds still while the yield is within this of the hurdle. */
    public static final double FOREIGN_TOLERANCE = .10;

    /** ...into a yield at least this far over the deposit rate, annual. */
    public static final double HOUSEHOLD_PREMIUM = .01;

    /**
     * A company keeps this many months of its costs before it buys back:
     * operating cost and, since round 2 of 0.7.11, its debt service - the
     * interest and the principal it repaid (Companies.monthlyDebtService()).
     * Jerus: "The cash buffer counts the loan payments too."
     */
    public static final double BUYBACK_CUSHION_MONTHS = 6;

    /** Equity this far past target before a company buys back, as a share of assets. */
    public static final double OVER_TARGET = .10;

    /* =======================================================================
       AND A DEAD BAND ON THE LINE ITSELF (2026-09-13)

       "excess > 0" is a comparison of two large numbers whose difference is
       near zero, and there is a step function on the other side of it: a
       company that crosses the line by a hair does not buy back a hair's
       worth of shares, it calls BUYBACK_CUSHION_MONTHS of operating cost home
       from abroad (see the cashAvailable call below, which asks for the
       cushion whatever the programme is worth) and then spends nothing. Six
       months of payroll moves on a rounding error.

       RELATIVE, so it never needs seeding: a reform divides equity and assets
       together and leaves the ratio alone. A billionth of the balance sheet
       is six orders of magnitude above the arithmetic's own noise and far
       below anything a person could see.
       ======================================================================= */
    public static final double MIN_EXCESS = 1e-9;

    /* =======================================================================
       AND A DEAD BAND ON THE RANKING (2026-09-15)

       "best first" is not defined when the model prices everything to the
       same yield, and it can: a company valued on its dividends yields
       exactly the discount rate at fair value, the same number for every
       company in that state. The winner takes the cell's month, and a
       ranking decided by which of six copies of one number had lost its last
       bit made two twin cities different cities (DenominationCheck month 129
       on the dealer). Buyers who are indifferent are ordered by the shares on
       issue - the deepest name first - which is what a buyer with no reason
       to prefer one would do; within a billionth of a yield, relative.
       ======================================================================= */
    public static final double TIED_YIELD = 1e-9;

    /** The most a company retires in a year, as a share of what it is worth. */
    public static final double BUYBACK_PACE = .10;

    /** A company bids for its own shares up to this far over fair value, and rests its bid there; past it, it waits and the money stays in the till (round 4). */
    public static final double BUYBACK_TOLERANCE = .10;

    /* =======================================================================
       THE DESK'S OWN CAPS ON SHARES (0.7.12 round 3; Jerus: "Put them back")

       Round 1's dealer carried two, and round 2 took them out with the
       dealer's quote - the brief's mistake, which let the desk hold what its
       capital carries alone: 15.75% of capital a dollar of shares at the
       dials, so six times the bank's equity, and nothing at all for a share
       worth nothing (round 2 measured a median 131% of equity, up to 833%;
       HealthCheck's twin cities lost their bank to one company's 40% fall,
       and the held-at-25% city ran to $130B of rescue). They stand again
       BESIDE the capital limit (Bank.deskCanCarry()), and the tightest of the
       three binds the desk's bid.

       NO SOURCE BUT THE MODEL'S OWN. Round 1's javadoc gave POSITION_LIMIT
       none, and BOOK_LIMIT only the fixture it was measured on (below). They
       are a trading book's two ordinary risk limits: a SINGLE-NAME limit on
       what the desk holds of any one issuer, and an AGGREGATE limit on its
       whole book, both set as shares of the bank's capital. A bank's own
       risk committee sets those numbers; no regulator publishes them. The
       nearest published figure is Basel's large-exposures rule, at most 25%
       of Tier 1 capital in one counterparty (BCBS, 2014) - a coincidence of
       size with the single-name cap, not its source.

       ROUND 1 DIFFERED IN ONE WAY, stated so nobody reads this as a restore
       to the letter: its hard stop in one company was CAPACITY (2) times
       POSITION_LIMIT, 50% of equity, because POSITION_LIMIT was also the
       scale its quote's PRESSURE read. The quote is gone, so the cap is the
       limit itself - 25%, as Jerus's words give it.

       Both are measured at FAIR VALUE, as round 1's were: the holding, and
       the book, at what the register says a share is worth - so a share the
       market has marked to nothing is still counted at its worth, and a
       collapsed company cannot be bought whole for nothing.

       THEY STOP THE BID, and in round 3 that was all they did, as round 1's
       ("past it, it stops buying"): what the desk already held stayed when
       the bank's equity fell or the holding's fair value rose, asked at fair
       value plus half the spread like the rest of its inventory. Measured on
       the eight default runs, a run's median book was 42-94% of the bank's
       equity at fair value (51% the median of the eight) and its largest
       holding 25-83% (30%); on two of the eight held-at-25% runs, whose banks
       ran down under what they held, a median book of 961% and 224%.

       ...AND WHAT IS OVER THEM IS OFFERED AT FAIR VALUE (0.7.12 round 4;
       Jerus: "A desk over its caps lists the part over the cap for sale at
       fair value, and the listing rests until someone buys it. There is no
       forced sale at a loss and no new number.") At the step, before anybody
       else posts, the desk works out what it holds over each cap, both read
       at fair value on the bank's equity as it stands (deskExcess()): in one
       company, what is over POSITION_LIMIT; over the book, what is over
       BOOK_LIMIT, SPREAD PRO RATA over its holdings by their value at fair
       value - the smallest rule that names no company first. A holding over
       both offers the LARGER of its two excesses, never the two added. That
       part is asked AT FAIR VALUE, never under it, and the rest of what it
       holds at fair value plus half the spread as before; both good for the
       month, withdrawn at the next step and posted again from that month's
       view, as an unfilled sale is. A share the register has at or below
       minFair adds nothing to the book at fair value, so it makes no excess;
       the desk posts nothing at all in such a company, as before, and holds
       it at its mark until its fair value comes back. A desk that is not
       posting (no capital to trade with) offers nothing, as before.
       ======================================================================= */

    /** The most of the bank's equity the desk holds in any one company's shares, at fair value: a trading book's single-name limit (see above). Round 1's, restored. */
    public static final double POSITION_LIMIT = .25;

    /**
     * ...and in all companies' together: the book's aggregate limit.
     * Round 1's, restored, with its measurement: seven companies at a quarter
     * each, twice over, is a trading book three and a half times the bank's
     * capital; the fixture bank in SaveFileCheck, with $62M of capital, took
     * $28M of mills off the world at a tenth a month, funded it wholesale at
     * 8%, earned no dividend on it and was dead by month 119. Half its
     * capital, marked down by half, is a quarter of it gone - survivable.
     */
    public static final double BOOK_LIMIT = .50;

    /** A share priced at this many times its founding price is split; at one over it, consolidated. */
    public static final double SPLIT_AT = 100;

    /**
     * The least a share may be worth before the market treats the company as
     * worthless. A MONEY CONSTANT, seeded by a reform (seedConstants()): a
     * fair value pinned at an unseeded floor is wrong by the ratio of the two
     * units after a reform, and the desk's capacity with it - measured on
     * DenominationCheck's long section, a seven per cent gap on one month's
     * dealing, 2026-09-12.
     */
    public static final double MIN_FAIR = 1e-9;

    /** The same floor in today's money. See MIN_FAIR. */
    private double minFair = MIN_FAIR;

    /* =======================================================================
       WHAT COUNTS AS A DEALER HAVING CAPITAL (2026-09-13)

       A bank wound down to zero equity does not report zero, it reports a
       few hundred picodollars either side of it, and a market that opened on
       the sign of that difference opened in one of two twin cities and not
       the other. A THOUSAND DOLLARS - one share at the founding price: "the
       desk has capital to take a position with". Seeded, because it is money.
       Since round 2 of 0.7.12 it decides only whether the DESK posts; the
       book is open to everybody else whatever the bank's state.
       ======================================================================= */
    public static final double MIN_DEALER_EQUITY = 1.0;

    /** The same floor in today's money. See MIN_DEALER_EQUITY. */
    private double minDealerEquity = MIN_DEALER_EQUITY;

    /** Re-seeds the floors at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        double u = unit > 0 ? unit : 1;
        minFair = MIN_FAIR / u;
        minDealerEquity = MIN_DEALER_EQUITY / u;
    }

    /** Whether the bank has capital enough to make a market at all. */
    private boolean hasDealerCapital(Bank bank) {
        return bank != null && bank.getBranches() > 0 && !bank.isInsolvent() && bank.equity() > minDealerEquity;
    }

    /* ------------------------------- the participants, on the book ------------------------------- */

    /** The bank's trading desk. */
    public static final String DESK = "bank";
    /** The world's investors. */
    public static final String WORLD = "world";
    /** The month's leavers, selling on the way out. */
    public static final String EMIGRANTS = "emigrants";
    /** One household cell: this, then its key (the bond market's prefix, deliberately). */
    public static final String CELL = BondMarket.CELL;
    // A company buying its own shares back posts under its own name (Equity.COMPANIES[c]).

    /* ------------------------------- the state ------------------------------- */

    private final int n = Equity.COMPANIES.length;
    private OrderBook[] books = new OrderBook[n];
    private final double[] fair = new double[n];     // the register's reckoning, per share, struck at the step
    /** This month's leavers' shares still offered: held abroad by the register, sold by EMIGRANTS. */
    private final double[] emigrantShares = new double[n];
    /** What each company's buyback bid may still spend, money; the rest of its month's programme. */
    private final double[] buybackBudget = new double[n];
    /**
     * Shares today per share at the founding, per company: the product of
     * every split and consolidation - what makes a price HISTORY possible
     * (pricePerFoundingShare()).
     */
    private final double[] splitFactor = new double[n];
    private boolean deskOpen;

    // this month, cash unless it says shares; cleared by startMonth()
    private final double[] soldToHouseholds = new double[n];      // desk -> cells
    private final double[] boughtFromHouseholds = new double[n];  // cells -> desk
    private final double[] soldAbroad = new double[n];            // desk -> the world
    private final double[] boughtFromAbroad = new double[n];      // the world or emigrants -> desk
    private final double[] emigrantsPaid = new double[n];         // emigrants, from anybody
    private final double[] buybackToHouseholds = new double[n];   // cells -> the company
    private final double[] buybackToDesk = new double[n];         // desk -> the company
    private final double[] buybackAbroad = new double[n];         // the world or emigrants -> the company
    private final double[] householdsBoughtAbroad = new double[n];// the world or emigrants -> cells
    private final double[] householdsSoldAbroad = new double[n];  // cells -> the world
    private final double[] betweenHouseholds = new double[n];     // cell -> cell
    private final double[] volume = new double[n];                // shares
    private final double[] split = new double[n];                 // this month's split factor, 0 if none
    private final int[] betweenHouseholdsTrades = new int[n];
    private double ownBoughtThisMonth, ownSoldThisMonth;          // the bank's own shares, for the pace

    // the last step's books, all companies, and over the city's life - for the screens and the playtest
    private double lastPostedSellValue, lastFilledValue;
    private int lastSellsPosted, lastSellsWaited, lastTrades;
    private double lifetimeVolume, lifePostedSellValue, lifeFilledValue, lifeBetweenHouseholds, lifeTurnover;
    private double lifeEmigrantsOffered, lifeEmigrantsPaid;
    private int lifeSellsPosted, lifeSellsWaited, lifeTrades, lifeBetweenHouseholdsTrades;

    /** Who the book settles against, set at the step and read by every fill until the next. */
    private Equity register;
    private HouseholdBalance households;
    private Bank dealer;
    private Companies companies;
    private int month;

    public Exchange() {
        for (int c = 0; c < n; c++) books[c] = new OrderBook(Equity.COMPANIES[c]);
        java.util.Arrays.fill(fair, Equity.FOUNDING_PRICE);
        java.util.Arrays.fill(splitFactor, 1);
    }

    /** Clears the month's flows, in this month. Before anything trades - the households' waterfall sells into the book before the step. */
    public void startMonth(int month) {
        this.month = month;
        startMonth();
    }

    /** Clears the month's flows. */
    public void startMonth() {
        for (int c = 0; c < n; c++) {
            soldToHouseholds[c] = 0; boughtFromHouseholds[c] = 0;
            soldAbroad[c] = 0; boughtFromAbroad[c] = 0; emigrantsPaid[c] = 0;
            buybackToHouseholds[c] = 0; buybackToDesk[c] = 0; buybackAbroad[c] = 0;
            householdsBoughtAbroad[c] = 0; householdsSoldAbroad[c] = 0; betweenHouseholds[c] = 0;
            volume[c] = 0;
            split[c] = 0;
            betweenHouseholdsTrades[c] = 0;
        }
        ownBoughtThisMonth = 0;
        ownSoldThisMonth = 0;
    }

    /* =====================================================================
       THE PRICES
       ===================================================================== */

    /** A share's price: its last trade, or its fair value before its first. */
    public double price(int c) {
        double last = books[c].lastPrice();
        return last > 0 ? last : fair[c];
    }

    /** True once a share of this company has traded on the book. */
    public boolean hasTraded(int c) { return books[c].lastPrice() > 0; }

    /** The register's reckoning of a share, struck at the step: book, or the dividend it pays capitalised (Equity.fairValue()). */
    public double fair(int c)        { return fair[c]; }

    /** The best bid resting, or NaN with none. */
    public double bestBid(int c)     { return books[c].bestBid(); }
    /** The best ask resting, or NaN with none. */
    public double bestAsk(int c)     { return books[c].bestAsk(); }

    /** What the desk carries a share at: the last trade or fair value, whichever is lower - a dealer does not mark its own book up on a price nobody has paid. */
    public double mark(int c)        { return Math.min(price(c), fair[c]); }

    /** The book a company's shares trade on. */
    public OrderBook bookOf(int c)   { return books[c]; }

    /** True while the bank's desk has capital to post on the book (hasDealerCapital()); the book is open to everybody else regardless. */
    public boolean isOpen()          { return deskOpen; }

    /**
     * Whether a company would sell new shares at today's price: not while the
     * market has them under fair value by more than the tolerance. A company
     * priced at half what it is worth borrows for its plan instead of giving
     * half of it away (Real Estate, measured on the dealer: 54,000 shares to
     * thirty million and back).
     */
    public boolean quoteSupportsIssue(int c) {
        return price(c) >= fair[c] * (1 - BUYBACK_TOLERANCE);
    }

    /** Dividend yield, annual, at a price: the dividend the register says it actually pays (Equity.dividendPerShareAnnual(), since 0.7.12 round 2). */
    public double yieldAt(Equity register, int c, double price) {
        return price > 0 ? register.dividendPerShareAnnual(c) / price : 0;
    }

    /** What the desk holds, at its mark: the bank's securities line. */
    public double markToMarket(Equity register) {
        double total = 0;
        for (int c = 0; c < n; c++) total += Math.max(0, register.getDealerShares(c)) * mark(c);
        return total;
    }

    /** What the city's households hold, at the price. */
    public double marketValueOfHouseholds(Equity register, HouseholdBalance households) {
        double total = 0;
        for (int c = 0; c < n; c++) total += households.sharesHeld(c) * price(c);
        return total;
    }

    public double marketCap(Equity register, int c) { return register.getOutstanding(c) * price(c); }

    /** The desk's bid capacity in this company now, shares: what its capital carries (Bank.deskCanCarry()), and of its own shares what it holds over target at the pace. */
    public double deskCanBuy(Equity register, int c) {
        if (dealer == null || !hasDealerCapital(dealer) || register.getShares(c) <= 0 || fair[c] <= minFair) return 0;
        double bid = fair[c] * (1 - SPREAD / 2);
        return deskCapacity(register, c, bid);
    }

    /* =====================================================================
       THE MONTH
       ===================================================================== */

    /** What the month's trading needs to know about a company that is not on the register. */
    public interface Companies {
        /** Cash in the company's till, after anything it can bring home for this. */
        double cashAvailable(int company, double wanted);
        /** ...and what is in its till now, bringing nothing home: what a resting buyback bid may still pay. */
        double till(int company);
        /** Debits the company's till for a buyback. */
        void payBuyback(int company, double cash);
        double assets(int company);
        double equity(int company);
        double monthlyOperatingCost(int company);
        /**
         * ...and what its debt costs it a month: the interest on its
         * statement and the principal it repaid (0.7.11, round 2). The
         * cushion counts it with the operating cost, because a payment is
         * money out of the till as surely as a wage.
         */
        double monthlyDebtService(int company);
    }

    /**
     * The exchange's step: last month's orders withdrawn, every share valued,
     * and every participant's orders posted - the desk, the emigrants, the
     * world, the companies, the household cells - then the splits, and the
     * bank's mark.
     *
     * @param book        each company's book equity, register order
     * @param depositRate what the bank pays savers, annual
     */
    public void takeMonth(Equity register, HouseholdBalance households, Bank bank,
                          Companies companies, double[] book, double worldRate,
                          double depositRate, int month) {
        attach(register, households, bank, companies, month);

        /* ---- the orders of last month go, and the sellers who waited are counted ---- */
        lastPostedSellValue = lastFilledValue = 0;
        java.util.Arrays.fill(lastDeskBound, 0);
        lastSellsPosted = lastSellsWaited = lastTrades = 0;
        for (int c = 0; c < n; c++) {
            OrderBook b = books[c];
            b.withdrawAll();
            noteBook(c, b);
            b.startMonth();
            buybackBudget[c] = 0;
        }

        /* ---- every share valued ---- */
        for (int c = 0; c < n; c++) {
            if (register.getShares(c) <= 0) { fair[c] = register.foundingPrice(); emigrantShares[c] = 0; continue; }
            fair[c] = Math.max(minFair, register.fairValue(c, book[c], worldRate));
            emigrantShares[c] = households == null ? 0
                    : Math.max(0, Math.min(households.getSharesTakenAway(c), register.getForeignShares(c)));
        }
        deskOpen = hasDealerCapital(bank);

        /* ---- and the orders, in the order they rest ---- */
        if (deskOpen) postDesk();
        postEmigrants();
        postWorld(worldRate);
        postCompanies();
        postHouseholds(depositRate);

        splitWhatNeedsIt();
        if (bank != null) bank.markSecurities(markToMarket(register));
    }

    /**
     * Who the book settles against from now until the next step. Game wires
     * it at the founding and on the load path too, because a household short
     * of money sells into the saved book before the city's first step.
     */
    public void attach(Equity register, HouseholdBalance households, Bank bank, Companies companies, int month) {
        this.register = register;
        this.households = households;
        this.dealer = bank;
        if (companies != null) this.companies = companies;
        this.month = month;
    }

    /** Adds a book's month, as the step closed it, to the market's record. */
    private void noteBook(int c, OrderBook b) {
        double value = price(c);
        lastPostedSellValue += b.postedSell() * value;
        lastFilledValue += b.filledSell() * value;
        lastSellsPosted += b.sellsPosted();
        lastSellsWaited += b.sellsWaited();
        lastTrades += b.trades();
        lifePostedSellValue += b.postedSell() * value;
        lifeFilledValue += b.filledSell() * value;
        lifeSellsPosted += b.sellsPosted();
        lifeSellsWaited += b.sellsWaited();
        lifeTrades += b.trades();
        lifeTurnover += b.turnover();
    }

    /* ---------------------------------- the desk ---------------------------------- */

    /** The desk makes a market around fair value, within its capital; its own shares only by its capital policy. */
    private void postDesk() {
        double[] excess = deskExcess(register);
        java.util.Arrays.fill(lastExcessOffered, 0);
        for (int c = 0; c < n; c++) {
            if (register.getShares(c) <= 0 || fair[c] <= minFair) continue;
            double bid = fair[c] * (1 - SPREAD / 2), ask = fair[c] * (1 + SPREAD / 2);
            double buy = deskCapacity(register, c, bid);
            // Which limit held the bid (round 3), counted for the playtest.
            if (c != Equity.BANK && dealer != null) {
                double[] limits = deskLimits(register, c, bid);
                int k = 0;
                for (int i = 1; i < limits.length; i++) if (limits[i] < limits[k]) k = i;
                lastDeskBound[k]++;
                lifeDeskBound[k]++;
            }
            if (buy > OrderBook.DUST) submit(c, DESK, OrderBook.Side.BUY, bid, buy);
            double sell = deskHolding(c);
            // What is over its caps at fair value first (round 4), the rest at the ask.
            double over = c == Equity.BANK ? 0 : Math.min(sell, excess[c]);
            if (over > OrderBook.DUST) {
                submit(c, DESK, OrderBook.Side.SELL, fair[c], over);
                lastExcessOffered[c] = over;
                lifeExcessOffered += over * fair[c];
            }
            if (sell - over > OrderBook.DUST) submit(c, DESK, OrderBook.Side.SELL, ask, sell - over);
        }
    }

    /**
     * What the desk holds over its caps, shares, company by company (round
     * 4): the larger of what is over POSITION_LIMIT in that company and its
     * pro-rata part, by value at fair value, of what the book is over
     * BOOK_LIMIT - both on the bank's equity as it stands. Never more than it
     * holds; nothing in its own shares or in a share at or below minFair.
     */
    public double[] deskExcess(Equity register) {
        double[] out = new double[n];
        if (dealer == null || register == null) return out;
        double equity = Math.max(0, dealer.equity());
        double book = bookAtFair(register);
        double bookOver = Math.max(0, book - BOOK_LIMIT * equity);
        for (int c = 0; c < n; c++) {
            double held = Math.max(0, register.getDealerShares(c));
            if (c == Equity.BANK || !(held > 0) || fair[c] <= minFair) continue;
            double atFair = held * fair[c];
            double overPosition = Math.max(0, atFair - POSITION_LIMIT * equity) / fair[c];
            double overBook = book > 0 ? held * bookOver / book : 0;
            out[c] = Math.min(held, Math.max(overPosition, overBook));
        }
        return out;
    }

    /** What the desk offered over its caps at the last step, shares (round 4). */
    private final double[] lastExcessOffered = new double[n];
    /** ...and over the city's life, at fair value when offered, and what of it sold, at the price paid. */
    private double lifeExcessOffered, lifeExcessSold;
    public double getLastExcessOffered(int c)   { return lastExcessOffered[c]; }
    public double getLifeExcessOffered()        { return lifeExcessOffered; }
    public double getLifeExcessSold()           { return lifeExcessSold; }

    /** What of the desk's excess still rests on the book, at fair value, in one company - its asks at fair value (round 4); the Bank tab's figure. */
    public double deskExcessOnOffer(int c) {
        double total = 0;
        for (OrderBook.Order o : books[c].asks()) if (DESK.equals(o.who()) && isExcessPrice(c, o.price())) total += o.quantity() * o.price();
        return total;
    }
    /** ...in every company. */
    public double deskExcessOnOffer() {
        double total = 0;
        for (int c = 0; c < n; c++) total += deskExcessOnOffer(c);
        return total;
    }
    /** An ask of the desk's at fair value, not at its ask half a spread over: the excess. */
    private boolean isExcessPrice(int c, double price) {
        return price <= fair[c] * (1 + SPREAD / 4);
    }

    /**
     * What the desk can buy at this price, shares: of another company, what
     * its bank's capital carries at the weight its book gives the desk
     * (Bank.deskCanCarry(), the inventory at the mark) and never more than
     * is not already its own; of the bank's own, what the bank holds over its
     * target (Bank.buybackRoom()) at the buyback pace, only while its record
     * is neither new nor bad and it buys back at all.
     *
     * AT THE MARK ITS OWN PURCHASE LEAVES (round 2): a share bought at this
     * price is the book's last trade, so the desk carries it at the lower of
     * that price and fair value - not at the mark before it traded. A desk
     * bidding under fair value on a book that had never traded would
     * otherwise count the gap as a gain its first fill takes back, and end
     * the month under its target (BankCheck 17).
     */
    private double deskCapacity(Equity register, int c, double price) {
        if (dealer == null || !(price > 0)) return 0;
        if (c == Equity.BANK) {
            if (!ownSharesTrade(register) || !dealer.buysBackOwnShares()) return 0;
            double pace = Math.max(0, BUYBACK_PACE / 12 * register.getShares(c) - ownBoughtThisMonth);
            return Math.min(pace, dealer.buybackRoom(markToMarket(register)) / price);
        }
        double[] limits = deskLimits(register, c, price);
        double most = limits[0];
        for (double v : limits) most = Math.min(most, v);
        return Math.max(0, most);
    }

    /** Which of the desk's limits binds its bid: its capital, POSITION_LIMIT, BOOK_LIMIT, or the company's float. */
    public static final int BOUND_CAPITAL = 0, BOUND_POSITION = 1, BOUND_BOOK = 2, BOUND_FLOAT = 3;

    /**
     * The desk's four limits in another company's shares at this price,
     * shares, in BOUND_ order (round 3): what its capital carries (0.7.8), its
     * room under POSITION_LIMIT in this company and under BOOK_LIMIT over all
     * of them, both at fair value, and what is not already its own.
     */
    private double[] deskLimits(Equity register, int c, double price) {
        double equity = Math.max(0, dealer.equity());
        double f = fair[c];
        double carry = dealer.deskCanCarry(markToMarket(register), price, Math.min(price, f));
        double position = f > 0 ? POSITION_LIMIT * equity / f - Math.max(0, register.getDealerShares(c)) : 0;
        double book = f > 0 ? (BOOK_LIMIT * equity - bookAtFair(register)) / f : 0;
        double notMine = Math.max(0, register.getShares(c) - Math.max(0, register.getDealerShares(c)));
        return new double[] { carry, position, book, notMine };
    }

    /** What the desk holds of every other company, at fair value: what BOOK_LIMIT reads. */
    private double bookAtFair(Equity register) {
        double total = 0;
        for (int c = 0; c < n; c++) if (c != Equity.BANK) total += Math.max(0, register.getDealerShares(c)) * fair[c];
        return total;
    }

    /** The desk's holding of one company at fair value over the bank's equity, and its whole book's - for the screens and the playtest. */
    public double deskPositionShare(Equity register, int c) {
        double eq = dealer == null ? 0 : dealer.equity();
        return eq > 0 ? Math.max(0, register.getDealerShares(c)) * fair[c] / eq : 0;
    }
    public double deskBookShare(Equity register) {
        double eq = dealer == null ? 0 : dealer.equity();
        return eq > 0 ? bookAtFair(register) / eq : 0;
    }

    /** What the desk can sell of this company: what it holds - and of the bank's own, new shares at the pace while the bank is under its target. */
    private double deskHolding(int c) {
        if (c == Equity.BANK) {
            if (dealer == null || !ownSharesTrade(register) || !dealer.issuesOwnShares()) return 0;
            return Math.max(0, BUYBACK_PACE / 12 * register.getShares(c) - ownSoldThisMonth);
        }
        return Math.max(0, register.getDealerShares(c));
    }

    /**
     * ...AND NEVER ON A RECORD THAT IS NEW OR BAD - the register's own reading
     * of the bank's last twelve months (Equity.getRegime()): a bank in its
     * first year, or losing money in half the months of one, neither buys its
     * own shares back nor sells new ones through its desk (0.7.8).
     */
    private static boolean ownSharesTrade(Equity register) {
        Equity.Regime regime = register.getRegime(Equity.BANK);
        return regime != Equity.Regime.NEW && regime != Equity.Regime.BAD;
    }

    /* ---------------------------------- the emigrants ---------------------------------- */

    /** The month's leavers ask what the dealer's bid at fair value used to pay them. */
    private void postEmigrants() {
        for (int c = 0; c < n; c++) {
            if (emigrantShares[c] <= OrderBook.DUST || fair[c] <= minFair) continue;
            double ask = fair[c] * (1 - SPREAD / 2);
            lifeEmigrantsOffered += emigrantShares[c] * ask;
            post(c, EMIGRANTS, OrderBook.Side.SELL, ask, emigrantShares[c]);
        }
    }

    /* ---------------------------------- the world ---------------------------------- */

    /**
     * The world, on yield against its rate plus the premium: a bid up to the
     * price that yields the hurdle plus the tolerance, for FOREIGN_SPEED of
     * the gap at the best ask; an ask down to the price that yields the
     * hurdle less the tolerance and never under fair value less it, for
     * FOREIGN_SPEED of the gap at the best bid. A company with no record is
     * held on prospects for its first year.
     */
    private void postWorld(double worldRate) {
        double hurdle = Math.max(0, worldRate) + Equity.FOREIGN_PREMIUM;
        if (!(hurdle > 0)) return;
        for (int c = 0; c < n; c++) {
            if (register.getShares(c) <= 0 || fair[c] <= minFair) continue;
            if (register.getMonthsRecorded(c) < Equity.RECORD_MONTHS) continue;
            double d = register.dividendPerShareAnnual(c);
            double askRef = Double.isNaN(bestAsk(c)) ? price(c) : bestAsk(c);
            double bidRef = Double.isNaN(bestBid(c)) ? price(c) : bestBid(c);
            double yAsk = askRef > 0 ? d / askRef : 0, yBid = bidRef > 0 ? d / bidRef : 0;
            if (d > 0 && yAsk > hurdle * (1 + FOREIGN_TOLERANCE)) {
                double want = FOREIGN_SPEED * (yAsk / hurdle - 1) * register.getOutstanding(c);
                post(c, WORLD, OrderBook.Side.BUY, d / (hurdle * (1 + FOREIGN_TOLERANCE)), want);
            } else if (yBid < hurdle * (1 - FOREIGN_TOLERANCE) && bidRef >= fair[c] * (1 - FOREIGN_TOLERANCE)) {
                double held = worldHolding(c);
                double want = FOREIGN_SPEED * (1 - yBid / hurdle) * held;
                double floor = fair[c] * (1 - FOREIGN_TOLERANCE);
                double limit = d > 0 ? Math.max(floor, d / (hurdle * (1 - FOREIGN_TOLERANCE))) : floor;
                post(c, WORLD, OrderBook.Side.SELL, limit, want);
            }
        }
    }

    /** What the world holds that it can sell: what is held abroad less this month's leavers' shares, which are theirs to sell. */
    private double worldHolding(int c) {
        return Math.max(0, register.getForeignShares(c) - emigrantShares[c]);
    }

    /* ---------------------------------- the companies ---------------------------------- */

    /**
     * A company past its equity target by OVER_TARGET, with cash past a
     * cushion of its costs, bids for its own shares at fair value plus
     * BUYBACK_TOLERANCE - never more than BUYBACK_PACE of what it is worth a
     * year - and cancels what it buys. It takes what is asked up to that
     * limit, at the asks' prices, and the rest of the month's programme rests
     * as a bid AT THE LIMIT, good for the month like any order: a household
     * short of money in the waterfall, a leaver, the world or the desk may
     * sell into it until the step withdraws it. What it does not spend stays
     * in the till.
     *
     * KEEP IT IN THE COMPANY (0.7.12 round 4; Jerus: "When a company's buyback
     * finds nobody selling at a fair price, the unspent cash stays in the
     * company. It sits in the bank or buys bonds, as real open-market
     * buybacks do.") Round 2 paid a special dividend when an ask stood over
     * the limit, and round 3 also when nothing was asked at all; both doors
     * are gone, and no path in the model pays a special dividend now. The
     * cash sits in the till, where the bank pays it deposit interest, and
     * OutwardInvestment and the bond market's companies' rule move it
     * abroad or into other sectors' bonds by their own rules.
     *
     * WHY THAT IS WHAT A BUYBACK DOES. About 95% of repurchases are
     * open-market programmes and fewer than 5% tender offers (Wikipedia,
     * "Share repurchase", and its citation): a company announces a programme
     * and buys on the market over time, at prices it chooses. Firms complete
     * 74-82% of the shares they announce within three years, and buy more
     * after the price has fallen (Stephens and Weisbach, "Actual Share
     * Reacquisitions in Open-Market Repurchase Programs", Journal of Finance
     * 53(1), 1998). A programme that finds no seller at its price goes
     * unfinished, and the cash stays - nobody is paid a dividend for the
     * shares he would not sell.
     */
    private void postCompanies() {
        if (companies == null) return;
        for (int c = 0; c < n; c++) {
            if (c == Equity.BANK || register.getShares(c) <= 0 || fair[c] <= minFair) continue;
            Equity.Regime regime = register.getRegime(c);
            if (regime == Equity.Regime.BAD || regime == Equity.Regime.NEW) continue;
            double assets = companies.assets(c);
            double equity = companies.equity(c);
            if (assets <= 0) continue;
            double excess = equity - (register.getTargetEquityShare(c) + OVER_TARGET) * assets;
            if (excess <= assets * MIN_EXCESS) continue;
            // ...its debt service counted with its costs (0.7.11, round 2).
            double cushion = BUYBACK_CUSHION_MONTHS * (Math.max(0, companies.monthlyOperatingCost(c))
                    + Math.max(0, companies.monthlyDebtService(c)));
            double worth = register.getOutstanding(c) * fair[c];
            double cap = Math.min(excess, BUYBACK_PACE / 12 * worth);
            double cash = companies.cashAvailable(c, cap + cushion) - cushion;
            double spend = Math.min(cap, cash);
            if (spend <= 0) continue;
            double limit = fair[c] * (1 + BUYBACK_TOLERANCE);
            double best = bestAsk(c);
            buybackBudget[c] = spend;
            // Sized at the cheapest it could pay; the book trims what rests
            // to what the budget left buys at the limit (Settle.capacity()).
            double q = spend / (Double.isNaN(best) ? Math.min(limit, price(c)) : Math.min(best, limit));
            submit(c, Equity.COMPANIES[c], OrderBook.Side.BUY, limit, q);
        }
    }

    /* ---------------------------------- the households ---------------------------------- */

    /** Whether a cell may put money into shares at all: no debt, not locked out, not going short, investing at all. */
    static boolean mayBuy(Household c) {
        return !c.isEmpty() && c.canInvest() && !(c.debt > 0) && c.lockout <= 0 && !c.isGoingShort();
    }

    /** How far a cell's savings are under its cushion, every household of it: its excess over its share target (round 3). Any cell holding shares, whether or not it may buy. */
    static double shortOfCushion(Household c) {
        if (c.isEmpty()) return 0;
        double gap = HouseholdBalance.SHARE_CUSHION_MONTHS * Math.max(0, c.disposable) - c.savings;
        return gap > 0 ? gap * c.households : 0;
    }

    /** What the cells asked to rebalance over the city's life, at the price: for the playtest (round 3). */
    private double lifeRebalanceSold;
    public double getLifeRebalanceSold() { return lifeRebalanceSold; }

    /** What a cell has past its cushion, in all: savings past SHARE_CUSHION_MONTHS of its take-home, every household of it. */
    static double spareOf(Household c) {
        if (!mayBuy(c)) return 0;
        double excess = c.savings - HouseholdBalance.SHARE_CUSHION_MONTHS * Math.max(0, c.disposable);
        return excess > 0 ? excess * c.households : 0;
    }

    /**
     * THE CELLS REBALANCE THEIR SHARES BY THE BOND RULE (0.7.12 round 3;
     * Jerus: "Yes, same rule"): a cell over its target sells HOME_SPEED of the
     * excess, a cell under it buys OUT_SPEED of the gap, as they do with the
     * businesses' bonds and the city's paper (OutwardInvestment's speeds).
     *
     * THE TARGET IS THE ONE THE SHARE RULE ALREADY HAD, WRITTEN AS A STOCK.
     * Since the exchange a cell put a share of what is past its cushion
     * (savings over SHARE_CUSHION_MONTHS of its take-home) into the best
     * dividend yield over the deposit rate plus HOUSEHOLD_PREMIUM, every
     * month, and never sold except short of money in the waterfall. It named
     * no holding it was aiming at; the one it moves towards, month after
     * month, is every dollar past the cushion in shares. So a cell's target
     * is what it holds and what is past its cushion together. Past the
     * cushion, the gap is what is past it - bought at OUT_SPEED of it a month,
     * the best yield first (taking what is asked up to the price that still
     * yields the floor, company by company, then resting the rest on the best
     * at the market), ties within TIED_YIELD to the deepest name, as before.
     * UNDER the cushion, the excess is the cushion's shortfall, up to what it
     * holds: sold at HOME_SPEED of it a month, pro rata over what it holds at
     * the price, asked at the market - a price-taker rebalancing, not a seller
     * who names a price. No appetite number of its own: the cushion is the
     * paper's and the offering's, the speeds are the bonds'.
     *
     * THE SELLERS POST FIRST, then the buyers, so a cell under its cushion
     * offers into the step and a cell past it takes what the cheapest asks
     * are - its neighbours', where the desk asks over fair value. It replaced
     * MONTHLY_SHARE_OF_EXCESS (a twentieth of the excess a month) with
     * OUT_SPEED (a twenty-fifth), the bonds' speed.
     */
    private void postHouseholds(double depositRate) {
        if (households == null) return;
        double floor = Math.max(0, depositRate) + HOUSEHOLD_PREMIUM;
        double[] divs = new double[n];
        for (int c = 0; c < n; c++) divs[c] = register.getShares(c) > 0 ? register.dividendPerShareAnnual(c) : 0;

        /* ---- the cells over their target: HOME_SPEED of the cushion's shortfall, pro rata, at the market ---- */
        for (Household cell : households.cellsForMarket()) {
            double excess = shortOfCushion(cell);
            if (!(excess > 1e-12)) continue;
            double worth = 0;
            for (int c = 0; c < n; c++) {
                if (!(cell.shares[c] > 0) || register.getShares(c) <= 0 || fair[c] <= minFair) continue;
                worth += cell.shares[c] * cell.households() * price(c);
            }
            if (!(worth > 0)) continue;
            double share = Math.min(1, OutwardInvestment.HOME_SPEED * Math.min(excess, worth) / worth);
            String who = CELL + cell.key();
            for (int c = 0; c < n; c++) {
                if (!(cell.shares[c] > 0) || register.getShares(c) <= 0 || fair[c] <= minFair) continue;
                lifeRebalanceSold += cell.shares[c] * cell.households() * share * price(c);
                post(c, who, OrderBook.Side.SELL, price(c), cell.shares[c] * cell.households() * share);
            }
        }

        /* ---- ...and the cells under it: OUT_SPEED of what is past the cushion, the best yield first ---- */
        for (Household cell : households.cellsForMarket()) {
            double budget = spareOf(cell) * OutwardInvestment.OUT_SPEED;
            if (!(budget > 1e-12)) continue;
            String who = CELL + cell.key();
            buying = cell;
            buyingBudget = budget;
            try {
                // Take what is on offer, best first.
                boolean[] tried = new boolean[n];
                while (buyingBudget > 1e-12) {
                    int best = bestBuy(divs, floor, tried, true);
                    if (best < 0) break;
                    tried[best] = true;
                    double ask = bestAsk(best);
                    double limit = divs[best] / floor;
                    books[best].submit(who, OrderBook.Side.BUY, limit, buyingBudget / ask, month, clearing(best), false);
                }
                // ...and what is left waits on the best, at the market or its
                // limit, whichever is lower.
                if (buyingBudget > 1e-12) {
                    int best = bestBuy(divs, floor, new boolean[n], false);
                    if (best >= 0) {
                        double limit = Math.min(divs[best] / floor, price(best));
                        submit(best, who, OrderBook.Side.BUY, limit, buyingBudget / limit);
                    }
                }
            } finally {
                buying = null;
            }
        }
    }

    /** The cell buying at the step right now, and what it may still spend this month. */
    private Household buying;
    private double buyingBudget;

    /**
     * The company a buyer would put the next dollar in: the highest yield
     * over the floor, at the best ask when `offered` (only a company with an
     * ask it would take), at the price otherwise; ties, within TIED_YIELD, to
     * the deepest name. -1 with none.
     */
    private int bestBuy(double[] divs, double floor, boolean[] tried, boolean offered) {
        int best = -1;
        double bestYield = 0;
        for (int c = 0; c < n; c++) {
            if (tried[c] || !(divs[c] > 0) || register.getShares(c) <= 0) continue;
            double p = offered ? bestAsk(c) : price(c);
            if (!(p > 0)) continue;
            if (offered && p > divs[c] / floor) continue;
            double y = divs[c] / p;
            if (!(y > floor)) continue;
            if (best < 0 || y > bestYield * (1 + TIED_YIELD)) {
                best = c;
                bestYield = y;
            } else if (y >= bestYield * (1 - TIED_YIELD) && register.getShares(c) > register.getShares(best)) {
                // Tied: the deepest name.
                best = c;
                bestYield = Math.max(bestYield, y);
            }
        }
        return best;
    }

    /* =====================================================================
       A HOUSEHOLD SHORT OF MONEY SELLS, mid-month, in the waterfall
       ===================================================================== */

    /**
     * A cell short of money asks, in each company it holds, the price at
     * which the dividend it gives up yields its own borrowing rate - pro rata
     * by what it holds at that price, for no more than it is short. What
     * rests at that price or better fills now at the resting price; the rest
     * waits on the book until the step withdraws it. A company that pays
     * nothing is asked at the dealer's old bid, fair value less half SPREAD:
     * selling it costs no dividend, so any fair price beats credit.
     *
     * @param needPer cash wanted, per household of the cell
     * @return cash raised now, per household
     */
    double sellForHousehold(Equity register, Bank bank, HouseholdBalance households, Household cell, double needPer) {
        if (register == null || cell == null || !(needPer > 0) || cell.households() <= 0) return 0;
        if (this.register == null) attach(register, households, bank, companies, month);
        if (this.households == null) this.households = households;
        double rate = Math.max(0, cell.rate);
        double[] limit = new double[n];
        double worth = 0;
        for (int c = 0; c < n; c++) {
            if (!(cell.shares[c] > 0) || register.getShares(c) <= 0 || fair[c] <= minFair) continue;
            double d = register.dividendPerShareAnnual(c);
            limit[c] = d > 0 && rate > 0 ? d / rate : fair[c] * (1 - SPREAD / 2);
            worth += cell.shares[c] * cell.households() * limit[c];
        }
        if (!(worth > 0)) return 0;
        double need = needPer * cell.households();
        double share = Math.min(1, need / worth);
        String who = CELL + cell.key();
        selling = cell;
        raised = 0;
        try {
            for (int c = 0; c < n; c++) {
                if (!(limit[c] > 0)) continue;
                double q = cell.shares[c] * cell.households() * share;
                post(c, who, OrderBook.Side.SELL, limit[c], q);
            }
        } finally {
            selling = null;
        }
        // Sized at its own ask, filled at the bids' prices, which are higher:
        // what it raised past what it was short is banked, not spent.
        if (raised > need) {
            cell.savings += (raised - need) / cell.households();
            raised = need;
        }
        return raised / cell.households();
    }

    /** The cell selling in the waterfall right now, whose proceeds go back to its waterfall rather than into its savings. */
    private Household selling;
    private double raised;

    /* =========================== one order, settled =========================== */

    private void submit(int c, String who, OrderBook.Side side, double price, double quantity) {
        if (!(quantity > OrderBook.DUST) || !(price > 0) || !Double.isFinite(price)) return;
        if (side == OrderBook.Side.SELL) lifePostedSellBy[classOf(who)] += quantity * price(c);
        books[c].submit(who, side, price, quantity, month, clearing(c));
    }

    /**
     * A PRICE-TAKER'S ORDER: it takes what is on offer up to its
     * reservation - the most a buyer would pay for the yield it wants, the
     * least a seller would take - at the resting orders' prices, and rests
     * what is left AT THE MARKET: a buyer at the price or its reservation,
     * whichever is lower, a seller at whichever is higher.
     *
     * WHY NOT AT THE RESERVATION. Every rule here came from the dealer, where
     * it took a quote; none of them ever named the price it would wait at.
     * Rested at its reservation, a household bidding for the bank's shares at
     * a 1% yield sat on the book at five times their price, and the first
     * cell short of money that sold into it was paid five times what they
     * were worth - by the rule's arithmetic, not by anybody's choice. A
     * buyer who has taken everything offered below its reservation waits at
     * the price the market last cleared, which is what a limit order placed
     * "at the market" is.
     */
    private void post(int c, String who, OrderBook.Side side, double reservation, double quantity) {
        if (!(quantity > OrderBook.DUST) || !(reservation > 0) || !Double.isFinite(reservation)) return;
        if (side == OrderBook.Side.SELL) lifePostedSellBy[classOf(who)] += quantity * price(c);
        double took = 0;
        for (OrderBook.Fill f : books[c].submit(who, side, reservation, quantity, month, clearing(c), false)) took += f.quantity();
        double left = quantity - took;
        if (!(left > OrderBook.DUST)) return;
        double market = price(c);
        double limit = side == OrderBook.Side.BUY ? Math.min(reservation, market) : Math.max(reservation, market);
        if (!(limit > 0) || !Double.isFinite(limit)) return;
        books[c].submit(who, side, limit, left, month, clearing(c));
    }

    /** Who sells, by class: the desk, the world, the leavers, a household cell - for the fill rate by seller. */
    public static final int BY_DESK = 0, BY_WORLD = 1, BY_EMIGRANTS = 2, BY_HOUSEHOLDS = 3;
    private static int classOf(String who) {
        if (DESK.equals(who)) return BY_DESK;
        if (WORLD.equals(who)) return BY_WORLD;
        if (EMIGRANTS.equals(who)) return BY_EMIGRANTS;
        return BY_HOUSEHOLDS;
    }
    /**
     * Over the city's life, by seller class: what was offered for sale and
     * what of it sold, both in shares at the company's price when it
     * happened - so the ratio is a fill rate, as the book's own is, and not
     * a comparison of a seller's floor with the bid it was paid.
     */
    private final double[] lifePostedSellBy = new double[4], lifeFilledSellBy = new double[4];

    /** Company-months each of the desk's limits held its bid, in BOUND_ order: the last step's and the city's life (round 3). */
    private final int[] lastDeskBound = new int[4], lifeDeskBound = new int[4];
    public int getLastDeskBound(int which) { return lastDeskBound[which]; }
    public int getLifeDeskBound(int which) { return lifeDeskBound[which]; }
    public double getLifePostedSellBy(int who) { return lifePostedSellBy[who]; }
    public double getLifeFilledSellBy(int who) { return lifeFilledSellBy[who]; }

    private OrderBook.Clearing clearing(int c) {
        return new Settle(c);
    }

    /** Where each participant's money and shares are, for one company's book. */
    private final class Settle implements OrderBook.Clearing {
        final int c;
        Settle(int c) { this.c = c; }

        @Override public double capacity(String who, OrderBook.Side side, double price) {
            if (!(price > 0)) return 0;
            if (side == OrderBook.Side.BUY) {
                if (DESK.equals(who)) return deskCapacity(register, c, price);
                if (WORLD.equals(who)) return Double.POSITIVE_INFINITY;
                if (EMIGRANTS.equals(who)) return 0;
                if (who.startsWith(CELL)) {
                    Household h = cellOf(who);
                    if (h == null) return 0;
                    double cash = h == buying ? Math.min(buyingBudget, spareOf(h)) : spareOf(h);
                    return Math.max(0, cash) / price;
                }
                if (companies == null || !who.equals(Equity.COMPANIES[c])) return 0;
                return Math.max(0, Math.min(buybackBudget[c], companies.till(c))) / price;
            }
            if (DESK.equals(who)) return deskHolding(c);
            if (WORLD.equals(who)) return worldHolding(c);
            if (EMIGRANTS.equals(who)) return Math.max(0, Math.min(emigrantShares[c], register.getForeignShares(c)));
            if (who.startsWith(CELL)) {
                Household h = cellOf(who);
                return h == null ? 0 : Math.max(0, h.shares[c] * h.households());
            }
            return 0;
        }

        @Override public void settle(String buyer, String seller, double q, double price) {
            double cash = q * price;
            boolean cellSells = seller.startsWith(CELL), cellBuys = buyer.startsWith(CELL);
            boolean abroadSells = WORLD.equals(seller) || EMIGRANTS.equals(seller);
            boolean deskSells = DESK.equals(seller), deskBuys = DESK.equals(buyer);
            boolean companyBuys = companies != null && buyer.equals(Equity.COMPANIES[c]) && c != Equity.BANK;

            /* ---- the seller gives up the shares and is paid ---- */
            if (deskSells) {
                if (c == Equity.BANK) {
                    register.issueOwn(c, q);
                    dealer.issueOwnShares(cash);
                    ownSoldThisMonth += q;
                } else {
                    register.moveDesk(c, -q);
                    dealer.deskReceives(cash);
                    if (isExcessPrice(c, price)) lifeExcessSold += cash;
                }
            } else if (WORLD.equals(seller)) {
                register.moveForeign(c, -q);
            } else if (EMIGRANTS.equals(seller)) {
                register.moveForeign(c, -q);
                emigrantShares[c] = Math.max(0, emigrantShares[c] - q);
                emigrantsPaid[c] += cash;
                lifeEmigrantsPaid += cash;
            } else if (cellSells) {
                Household h = cellOf(seller);
                h.shares[c] = Math.max(0, h.shares[c] - q / h.households());
                if (h == selling) {
                    raised += cash;
                } else {
                    h.savings += cash / h.households();
                }
            }

            /* ---- the buyer pays and takes them ---- */
            if (deskBuys) {
                if (c == Equity.BANK) {
                    register.cancelOwn(c, q);
                    dealer.buyBackOwnShares(cash);
                    ownBoughtThisMonth += q;
                } else {
                    register.moveDesk(c, q);
                    dealer.deskPays(cash);
                }
            } else if (WORLD.equals(buyer)) {
                register.moveForeign(c, q);
            } else if (cellBuys) {
                Household h = cellOf(buyer);
                h.shares[c] += q / h.households();
                h.savings -= cash / h.households();
                if (h == buying) buyingBudget -= cash;
            } else if (companyBuys) {
                companies.payBuyback(c, cash);
                register.retire(c, q);
                buybackBudget[c] = Math.max(0, buybackBudget[c] - cash);
            }

            /* ---- and what crossed the pools' edge, for the audit ---- */
            if (deskSells && cellBuys) soldToHouseholds[c] += cash;
            if (deskSells && WORLD.equals(buyer)) soldAbroad[c] += cash;
            if (deskSells && companyBuys) buybackToDesk[c] += cash;
            if (cellSells && deskBuys) boughtFromHouseholds[c] += cash;
            if (abroadSells && deskBuys) boughtFromAbroad[c] += cash;
            if (cellSells && companyBuys) buybackToHouseholds[c] += cash;
            if (abroadSells && companyBuys) buybackAbroad[c] += cash;
            if (abroadSells && cellBuys) householdsBoughtAbroad[c] += cash;
            if (cellSells && WORLD.equals(buyer)) householdsSoldAbroad[c] += cash;
            if (cellSells && cellBuys) {
                betweenHouseholds[c] += cash;
                betweenHouseholdsTrades[c]++;
                lifeBetweenHouseholds += cash;
                lifeBetweenHouseholdsTrades++;
            }
            volume[c] += q;
            lifetimeVolume += q;
            lifeFilledSellBy[classOf(seller)] += q * price(c);
        }
    }

    /** The cell a participant's name is, or null. */
    private Household cellOf(String who) {
        return households == null ? null : households.cellByKey(who.substring(CELL.length()));
    }

    /* =====================================================================
       SPLITS
       ===================================================================== */

    /**
     * A split or a consolidation, by a power of ten, for any share priced a
     * hundred times its founding price or a hundredth of it. Every count on
     * the register, in every household, on the desk and on the book moves by
     * the factor; every price by its inverse. Value is untouched.
     */
    private void splitWhatNeedsIt() {
        for (int c = 0; c < n; c++) {
            if (register.getShares(c) <= 0) continue;
            // A company worth nothing is not consolidated into nothing.
            if (fair[c] <= minFair) continue;
            double ratio = price(c) / register.foundingPrice();
            if (ratio < SPLIT_AT && ratio > 1 / SPLIT_AT) continue;
            double k = Math.pow(10, Math.floor(Math.log10(ratio)));
            if (!(k > 0) || k == 1) continue;
            register.split(c, k);
            if (households != null) households.splitShares(c, k);
            books[c].split(k);
            fair[c] /= k;
            emigrantShares[c] *= k;
            volume[c] *= k;
            split[c] = k;
            splitFactor[c] *= k;
        }
    }

    /* ------------------------------- reading ------------------------------- */

    public double getSoldToHouseholds(int c)     { return soldToHouseholds[c]; }
    public double getBoughtFromHouseholds(int c) { return boughtFromHouseholds[c]; }
    public double getSoldAbroad(int c)           { return soldAbroad[c]; }
    public double getBoughtFromAbroad(int c)     { return boughtFromAbroad[c]; }
    public double getEmigrantsPaid(int c)        { return emigrantsPaid[c]; }
    public double getBuybackToHouseholds(int c)  { return buybackToHouseholds[c]; }
    public double getBuybackToDesk(int c)        { return buybackToDesk[c]; }
    public double getBuybackAbroad(int c)        { return buybackAbroad[c]; }
    /** The households' cells bought from the world or from leavers this month (0.7.12 round 2): their savings out, a financial outflow. */
    public double getHouseholdsBoughtAbroad(int c) { return householdsBoughtAbroad[c]; }
    /** ...and sold to the world: a financial inflow into their savings. */
    public double getHouseholdsSoldAbroad(int c) { return householdsSoldAbroad[c]; }
    /** What one cell paid another for shares this month: a transfer inside the households. */
    public double getBetweenHouseholds(int c)    { return betweenHouseholds[c]; }
    public int getBetweenHouseholdsTrades(int c) { return betweenHouseholdsTrades[c]; }
    /** Shares that changed hands this month. */
    public double getVolume(int c)               { return volume[c]; }
    public double getLifetimeVolume()            { return lifetimeVolume; }
    /** This month's split factor: 100 for a hundred-for-one, .01 for a consolidation, 0 for none. */
    public double getSplit(int c)                { return split[c]; }
    /** Shares today for one share at the founding. */
    public double getSplitFactor(int c)          { return splitFactor[c]; }
    /** The price per FOUNDING share: continuous through every split. The price history is this. */
    public double pricePerFoundingShare(int c)   { return price(c) * splitFactor[c]; }
    /** ...and the register's reckoning, the same way. */
    public double fairPerFoundingShare(int c)    { return fair[c] * splitFactor[c]; }
    /** This month's leavers' shares still offered. */
    public double getEmigrantShares(int c)       { return emigrantShares[c]; }
    /** What a company's buyback bid may still spend this month. */
    public double getBuybackBudget(int c)        { return buybackBudget[c]; }

    /** What the desk has resting, shares, on one side of one company's book. */
    public double deskResting(int c, OrderBook.Side side) { return books[c].resting(DESK, side); }

    /** The best price a participant has resting on a company's book, bid or ask - NaN with none. For the screens. */
    public double bestBidOf(int c, String who) {
        for (OrderBook.Order o : books[c].bids()) if (o.who().equals(who)) return o.price();
        return Double.NaN;
    }
    public double bestAskOf(int c, String who) {
        for (OrderBook.Order o : books[c].asks()) if (o.who().equals(who)) return o.price();
        return Double.NaN;
    }

    /* ---- the last step's books and the city's life, for the screens and the playtest ---- */

    /** At the last step, every book together: the value asked to sell (at the price), the value that filled, the sell orders posted and those that waited, and the trades. */
    public double getLastPostedSellValue() { return lastPostedSellValue; }
    public double getLastFilledValue()     { return lastFilledValue; }
    public int getLastSellsPosted()        { return lastSellsPosted; }
    public int getLastSellsWaited()        { return lastSellsWaited; }
    public int getLastTrades()             { return lastTrades; }
    /** ...and over the city's life. */
    public double getLifePostedSellValue() { return lifePostedSellValue; }
    public double getLifeFilledValue()     { return lifeFilledValue; }
    public int getLifeSellsPosted()        { return lifeSellsPosted; }
    public int getLifeSellsWaited()        { return lifeSellsWaited; }
    public int getLifeTrades()             { return lifeTrades; }
    public double getLifeTurnover()        { return lifeTurnover; }
    public double getLifeBetweenHouseholds() { return lifeBetweenHouseholds; }
    /** What leavers offered on the way out, at their ask, and what they were paid, over the city's life. */
    public double getLifeEmigrantsOffered() { return lifeEmigrantsOffered; }
    public double getLifeEmigrantsPaid()    { return lifeEmigrantsPaid; }
    public int getLifeBetweenHouseholdsTrades() { return lifeBetweenHouseholdsTrades; }

    private double sum(double[] a) { double s = 0; for (double v : a) s += v; return s; }
    public double getSoldToHouseholds()     { return sum(soldToHouseholds); }
    public double getBoughtFromHouseholds() { return sum(boughtFromHouseholds); }
    public double getSoldAbroad()           { return sum(soldAbroad); }
    public double getBoughtFromAbroad()     { return sum(boughtFromAbroad); }
    public double getEmigrantsPaid()        { return sum(emigrantsPaid); }
    public double getBuybackToHouseholds()  { return sum(buybackToHouseholds); }
    public double getBuybackToDesk()        { return sum(buybackToDesk); }
    public double getHouseholdsBoughtAbroad() { return sum(householdsBoughtAbroad); }
    public double getHouseholdsSoldAbroad() { return sum(householdsSoldAbroad); }
    public double getBetweenHouseholds()    { return sum(betweenHouseholds); }
    public int getBetweenHouseholdsTrades() { int s = 0; for (int v : betweenHouseholdsTrades) s += v; return s; }

    /**
     * The desk's trading in the city's shares, the bank's own left out
     * (0.7.9): each total less the bank's line, because its own shares are
     * capital since 0.7.8 (Bank.buyBackOwnShares()), not the desk's trading.
     */
    public double deskSoldToHouseholds()     { return getSoldToHouseholds() - getSoldToHouseholds(Equity.BANK); }
    /** ...sold abroad. */
    public double deskSoldAbroad()           { return getSoldAbroad() - getSoldAbroad(Equity.BANK); }
    /** ...bought from the households. */
    public double deskBoughtFromHouseholds() { return getBoughtFromHouseholds() - getBoughtFromHouseholds(Equity.BANK); }
    /** ...and bought from abroad. */
    public double deskBoughtFromAbroad()     { return getBoughtFromAbroad() - getBoughtFromAbroad(Equity.BANK); }
    public double getBuybackAbroad()        { return sum(buybackAbroad); }
    public double getVolume()               { return sum(volume); }

    /* ------------------------------- saving ------------------------------- */

    /** Slots per company in the old dealer's array before the split factor joined (the exchange's first night): its quote, fair value and demand. */
    public static final int SLOTS_BEFORE_SPLITS = 3;
    /** ...and after: the quote, fair value, demand and split factor, four a company - what a save from before 0.7.12 round 2 carries (restore(String[], double[])). */
    public static final int SLOTS = SLOTS_BEFORE_SPLITS + 1;

    /**
     * The market in a save (0.7.12, round 2), carried by Gson as it stands:
     * every book with the orders resting on it - a cell's bid posted at the
     * step is what the next month's waterfall sells into - fair value and
     * the split factor, what the leavers and the companies' buybacks still
     * have on offer, and the record the screens read.
     */
    public static final class State {
        String[] keys;
        List<OrderBook> books;
        double[] fair, splitFactor, emigrantShares, buybackBudget;
        double[] life;
        double[] last;
    }

    public State toState() {
        State s = new State();
        s.keys = Equity.COMPANIES.clone();
        s.books = new ArrayList<>(java.util.Arrays.asList(books));
        s.fair = fair.clone();
        s.splitFactor = splitFactor.clone();
        s.emigrantShares = emigrantShares.clone();
        s.buybackBudget = buybackBudget.clone();
        s.life = new double[] { lifetimeVolume, lifePostedSellValue, lifeFilledValue, lifeBetweenHouseholds, lifeTurnover,
                lifeSellsPosted, lifeSellsWaited, lifeTrades, lifeBetweenHouseholdsTrades,
                lifeEmigrantsOffered, lifeEmigrantsPaid,
                lifePostedSellBy[0], lifePostedSellBy[1], lifePostedSellBy[2], lifePostedSellBy[3],
                lifeFilledSellBy[0], lifeFilledSellBy[1], lifeFilledSellBy[2], lifeFilledSellBy[3],
                lifeDeskBound[0], lifeDeskBound[1], lifeDeskBound[2], lifeDeskBound[3], lifeRebalanceSold,
                lifeExcessOffered, lifeExcessSold };
        s.last = new double[] { lastPostedSellValue, lastFilledValue, lastSellsPosted, lastSellsWaited, lastTrades,
                lastDeskBound[0], lastDeskBound[1], lastDeskBound[2], lastDeskBound[3] };
        return s;
    }

    /** @return false if the state is not one this build can read; nothing is changed */
    public boolean restore(State s) {
        if (s == null || s.keys == null || s.books == null || s.fair == null || s.splitFactor == null
                || s.books.size() != s.keys.length || s.fair.length != s.keys.length
                || s.splitFactor.length != s.keys.length) return false;
        reset();
        for (int i = 0; i < s.keys.length; i++) {
            int c = Equity.indexOf(s.keys[i]);
            if (c < 0) continue;
            OrderBook b = s.books.get(i);
            books[c] = b != null ? b : new OrderBook(Equity.COMPANIES[c]);
            fair[c] = s.fair[i];
            splitFactor[c] = Math.max(1e-12, s.splitFactor[i]);
            if (s.emigrantShares != null && i < s.emigrantShares.length) emigrantShares[c] = s.emigrantShares[i];
            if (s.buybackBudget != null && i < s.buybackBudget.length) buybackBudget[c] = s.buybackBudget[i];
        }
        if (s.life != null && s.life.length >= 9) {
            lifetimeVolume = s.life[0]; lifePostedSellValue = s.life[1]; lifeFilledValue = s.life[2];
            lifeBetweenHouseholds = s.life[3]; lifeTurnover = s.life[4];
            lifeSellsPosted = (int) s.life[5]; lifeSellsWaited = (int) s.life[6];
            lifeTrades = (int) s.life[7]; lifeBetweenHouseholdsTrades = (int) s.life[8];
            if (s.life.length >= 11) { lifeEmigrantsOffered = s.life[9]; lifeEmigrantsPaid = s.life[10]; }
            if (s.life.length >= 19) {
                for (int k = 0; k < 4; k++) { lifePostedSellBy[k] = s.life[11 + k]; lifeFilledSellBy[k] = s.life[15 + k]; }
            }
            if (s.life.length >= 23) {
                for (int k = 0; k < 4; k++) lifeDeskBound[k] = (int) Math.round(s.life[19 + k]);
            }
            if (s.life.length >= 24) lifeRebalanceSold = s.life[23];
            if (s.life.length >= 26) { lifeExcessOffered = s.life[24]; lifeExcessSold = s.life[25]; }
        }
        if (s.last != null && s.last.length >= 5) {
            lastPostedSellValue = s.last[0]; lastFilledValue = s.last[1];
            lastSellsPosted = (int) s.last[2]; lastSellsWaited = (int) s.last[3]; lastTrades = (int) s.last[4];
            if (s.last.length >= 9) for (int k = 0; k < 4; k++) lastDeskBound[k] = (int) Math.round(s.last[5 + k]);
        }
        return true;
    }

    /**
     * A save from before the book (0.7.12 round 1 and earlier): the dealer's
     * array, SLOTS a company - its quote, fair value, the demand it carried
     * and the split factor. Fair value and the split factor come back as they
     * were; the dealer's last quote becomes the book's last price, so the
     * price a player last saw is where the market opens; the demand has
     * nowhere to go - a buyer the dealer could not fill is not an order.
     */
    public boolean restore(String[] keys, double[] saved) {
        if (keys == null || saved == null) return false;
        int slots = keys.length > 0 ? (saved.length - 1) / keys.length : 0;
        if (saved.length != keys.length * slots + 1
                || (slots != SLOTS && slots != SLOTS_BEFORE_SPLITS)) return false;
        reset();
        int i = 0;
        for (String key : keys) {
            int c = Equity.indexOf(key);
            if (c < 0) { i += slots; continue; }
            double mid = saved[i++];
            fair[c] = saved[i++];
            i++;                                   // the dealer's unfilled demand
            splitFactor[c] = slots >= SLOTS ? Math.max(1e-12, saved[i++]) : 1;
            if (mid > 0) books[c].seedLastPrice(mid);
        }
        lifetimeVolume = saved[i];
        return true;
    }

    /** After a load: whether the desk posts is the bank's to say, read off the bank as the next step will. */
    public void reopen(Bank bank) {
        deskOpen = hasDealerCapital(bank);
        dealer = bank;
    }

    public void reset() {
        for (int c = 0; c < n; c++) books[c] = new OrderBook(Equity.COMPANIES[c]);
        java.util.Arrays.fill(fair, Equity.FOUNDING_PRICE);
        java.util.Arrays.fill(splitFactor, 1);
        java.util.Arrays.fill(emigrantShares, 0);
        java.util.Arrays.fill(buybackBudget, 0);
        lifetimeVolume = lifePostedSellValue = lifeFilledValue = lifeBetweenHouseholds = lifeTurnover = 0;
        lifeEmigrantsOffered = lifeEmigrantsPaid = 0;
        java.util.Arrays.fill(lifePostedSellBy, 0);
        java.util.Arrays.fill(lifeFilledSellBy, 0);
        java.util.Arrays.fill(lastDeskBound, 0);
        java.util.Arrays.fill(lifeDeskBound, 0);
        lifeRebalanceSold = 0;
        lifeExcessOffered = lifeExcessSold = 0;
        java.util.Arrays.fill(lastExcessOffered, 0);
        lifeSellsPosted = lifeSellsWaited = lifeTrades = lifeBetweenHouseholdsTrades = 0;
        lastPostedSellValue = lastFilledValue = 0;
        lastSellsPosted = lastSellsWaited = lastTrades = 0;
        deskOpen = false;
        startMonth();
    }

    /** Prices and the month's cash in the new unit; share counts do not move. */
    public void redenominate(double scale) {
        minFair *= scale;
        minDealerEquity *= scale;
        for (int c = 0; c < n; c++) {
            books[c].redenominate(scale, false);
            fair[c] *= scale;
            buybackBudget[c] *= scale;
            soldToHouseholds[c] *= scale; boughtFromHouseholds[c] *= scale;
            soldAbroad[c] *= scale; boughtFromAbroad[c] *= scale; emigrantsPaid[c] *= scale;
            buybackToHouseholds[c] *= scale; buybackToDesk[c] *= scale; buybackAbroad[c] *= scale;
            householdsBoughtAbroad[c] *= scale; householdsSoldAbroad[c] *= scale; betweenHouseholds[c] *= scale;
        }
        lastPostedSellValue *= scale; lastFilledValue *= scale;
        lifePostedSellValue *= scale; lifeFilledValue *= scale; lifeBetweenHouseholds *= scale; lifeTurnover *= scale;
        lifeEmigrantsOffered *= scale; lifeEmigrantsPaid *= scale;
        for (int k = 0; k < 4; k++) { lifePostedSellBy[k] *= scale; lifeFilledSellBy[k] *= scale; }
        lifeRebalanceSold *= scale;
        lifeExcessOffered *= scale; lifeExcessSold *= scale;
        if (buying != null) buyingBudget *= scale;
    }
}
