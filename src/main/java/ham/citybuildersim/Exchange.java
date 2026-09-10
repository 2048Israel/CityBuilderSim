package ham.citybuildersim;

/**
 * The stock exchange: where a share changes hands, and at what price.
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
 * ==================== THE BANK IS THE DEALER ====================
 *
 * Jerus: "liquidity, that's going to be an issue, we need to solve it
 * realistically via bank something, right?" Right. Every trade here is
 * with the bank's trading desk. It quotes a bid and an ask around what a
 * share is worth, takes the other side of whatever comes, holds what it
 * bought, and moves its quotes as its book fills: long, it quotes lower to
 * find buyers; with buyers it could not fill, it quotes higher to find
 * sellers. It earns the spread, it is paid dividends on what it holds, and
 * it loses money on a crash the way a dealer does. The desk is an asset on
 * the bank's balance sheet, weighted against its capital dearer than a loan
 * (Bank.RISK_EQUITY), so a bank stuffed with shares lends less - which is
 * what a bank stuffed with shares does.
 *
 * WHAT A SHARE IS WORTH is the register's own reckoning - book, or the
 * dividend capitalised at what the world asks, whichever is more (see
 * Equity.fairValue) - and the desk's book pushes the quote away from it:
 *
 *     mid = fair x (1 - PRESSURE x (inventory - unfilled demand) / limit)
 *
 * where the limit is the position the bank's capital allows in this company
 * and the unfilled demand is what buyers wanted and could not get, fading by
 * half a month. THE DESK NEVER SELLS WHAT IT DOES NOT HOLD. The first
 * version let it run short to its limit, and a short position in a company
 * whose book compounds is a liability that compounds: marked at the ceiling
 * as fair value rose, seven companies' worth of it took the bank's equity to
 * minus five trillion, its creditors absorbed the hole every month, and the
 * desk paid dividends on shares that did not exist out of money that did not
 * either - $4M a month to the households, a currency at its ceiling.
 * Measured, months 1,200-1,500. A buyer the desk cannot fill waits, and the
 * price it waits at goes up until somebody sells - the world into strength,
 * a household short of money, or the company itself, at the market's price.
 *
 * THE DESK CARRIES WHAT IT HOLDS AT THE QUOTE OR AT FAIR VALUE, WHICHEVER IS
 * LOWER. A dealer does not mark its own book up on a quote nobody has paid
 * yet. The second version marked at the mid, and the mid, lifted to five
 * times fair value by demand the desk could not fill, was gone the month the
 * demand was forgotten: $75M of trading gain one month, $75M of loss the
 * next, on $55M of inventory, for four hundred months - and every loss was
 * made good by the city, $40bn of it, while the gains had paid the bank's
 * dividend. Measured, seed 0, month 3,540 on. Marked low, a rising quote is
 * a gain when it is sold into and not before.
 *
 * The desk buys up to twice its limit and then stops: a dealer that kept
 * buying in a crash would be the crash. A failed bank quotes nothing, and a
 * city with a dead bank has no market: an emigrant takes their shares with
 * them, as before there was one.
 *
 * ==================== WHO TRADES, AND WHY ====================
 *
 * In this order each month, all at the month's quotes:
 *
 *   - EMIGRANTS sell on the way out. What the pool released and nobody
 *     claimed (HouseholdBalance.getSharesTakenAway) hits the bid, and the
 *     cash leaves with them - the return home, one leaver at a time.
 *   - THE WORLD buys when the dividend yield at the ask beats its rate plus
 *     the premium, sells when the yield at the bid falls under it. The same
 *     test the primary market uses, applied at the market price every month:
 *     a company the households bid up is one the world sells, and that is
 *     how Industry comes home.
 *   - HOUSEHOLDS with money past the cushion put a little of it, every month,
 *     into whichever company yields most over the bank's deposit rate -
 *     Jerus's call over spreading it evenly: demand chases yield, so a cheap
 *     company gets bought and a dear one does not. What the desk cannot sell
 *     them of the best, they put into the next best: a buyer takes what is
 *     on offer. (The first version bought one company a month or nothing,
 *     and a sold-out Retail left Real Estate unbought at twice the deposit
 *     rate for a century.)
 *   - COMPANIES with more equity than their target and cash past a cushion
 *     buy back and cancel - a tender to every holder at the ask, pro rata,
 *     never more than a tenth of the company a year. The other half of "too
 *     much equity relative to assets", and the thing that finally returns
 *     the hoard to the people who own it. When the market has the shares
 *     dear - the ask past fair value by more than the tolerance - the same
 *     money goes out as a special dividend instead: a company does not pay
 *     five times what its shares are worth to retire them. (Unpaced, at a
 *     twentieth of the company a month, Retail retired every share it had
 *     ever issued by month 3,500 - "sh 0.0" - and the last one was quoted at
 *     $61bn.)
 *
 * And out of order, in the middle of the month: a HOUSEHOLD SHORT OF MONEY
 * sells before it borrows - the waterfall is savings, then shares, then
 * credit, then going without. See HouseholdBalance.advanceMonth().
 *
 * The bank's own shares trade the same way, but the desk never holds them:
 * bought, they are cancelled; sold, they are issued. Treasury stock.
 *
 * A SHARE THAT GETS TOO DEAR IS SPLIT, a hundred for one, and one that gets
 * too cheap is consolidated, one for a hundred, so that a count of shares
 * stays a number a person can read after three centuries of buybacks and
 * offerings. Every holder's count moves by the same factor and every price
 * by its inverse; nothing anybody owns changes in value.
 */
public class Exchange {

    /* ------------------------------- the dials ------------------------------- */

    /** Ask over bid, as a share of the mid. What the desk earns for being there. */
    public static final double SPREAD = .02;

    /** How far a full position moves the quote from fair value. */
    public static final double PRESSURE = .25;

    /** The desk's position in one company, as a share of the bank's equity at fair value. */
    public static final double POSITION_LIMIT = .25;

    /**
     * ...and its whole book, all companies together, as a share of the
     * bank's equity at fair value. Seven companies at a quarter each, twice
     * over, is a trading book three and a half times the bank's capital;
     * the fixture bank in SaveFileCheck, with $62M of capital, took $28M of
     * mills off the world at a tenth a month, funded it wholesale at 8%,
     * earned no dividend on it and was dead by month 119. Measured. Half
     * its capital, marked down by half at the floor, is a quarter of it
     * gone - survivable.
     */
    public static final double BOOK_LIMIT = .50;

    /** The quote never leaves this band round fair value, whatever the book. */
    public static final double FLOOR = .50, CEILING = 5.0;

    /**
     * The quote reads the desk's position against at least this share of
     * the float, however small the bank. Without it a bank down to its last
     * dollar of capital had a limit of a twentieth of a share, any position
     * at all was "full", and it quoted five times fair value - into which the
     * world sold, and the desk paid $8M it did not have. Measured, month 31.
     */
    public static final double MIN_LIMIT_OF_FLOAT = .02;

    /** The most the desk will hold, as a multiple of its position limit. Past it, it stops buying. */
    public static final double CAPACITY = 2.0;

    /** What is left of a month's unfilled demand the next month, in the quote. */
    public static final double DEMAND_DECAY = .50;

    /** What the world moves in a month per unit of yield over or under its hurdle, as a share of the float. */
    public static final double FOREIGN_SPEED = .10;

    /** ...and it holds still while the yield is within this of the hurdle. */
    public static final double FOREIGN_TOLERANCE = .10;

    /** A household's monthly buying: this share of what is past its cushion. */
    public static final double MONTHLY_SHARE_OF_EXCESS = .05;

    /** ...into a yield at least this far over the deposit rate, annual. */
    public static final double HOUSEHOLD_PREMIUM = .01;

    /** A company keeps this many months of operating cost before it buys back. */
    public static final double BUYBACK_CUSHION_MONTHS = 6;

    /** Equity this far past target before a company buys back, as a share of assets. */
    public static final double OVER_TARGET = .10;

    /** The most a company retires in a year, as a share of what it is worth. */
    public static final double BUYBACK_PACE = .10;

    /** A company buys back only while the ask is within this of fair value; past it, the money is a special dividend. */
    public static final double BUYBACK_TOLERANCE = .10;

    /** A share quoted at this many times its founding price is split; at one over it, consolidated. */
    public static final double SPLIT_AT = 100;

    /* ------------------------------- the book ------------------------------- */

    private final int n = Equity.COMPANIES.length;
    private final double[] fair = new double[n];     // register's reckoning, per share
    private final double[] mid = new double[n];      // the desk's quote, per share
    private final double[] limit = new double[n];    // position limit, in shares
    private final double[] demand = new double[n];   // buyers the desk could not fill, in shares, fading

    // this month
    private final double[] soldToHouseholds = new double[n];    // cash
    private final double[] boughtFromHouseholds = new double[n];
    private final double[] soldAbroad = new double[n];
    private final double[] boughtFromAbroad = new double[n];
    private final double[] emigrantsPaid = new double[n];
    private final double[] buybackToHouseholds = new double[n];
    private final double[] buybackToDesk = new double[n];
    private final double[] buybackAbroad = new double[n];
    private final double[] specialDividend = new double[n];
    private final double[] volume = new double[n];              // shares
    private final double[] lastMid = new double[n];
    private final double[] unfilled = new double[n];            // shares wanted and not sold, this month
    private final double[] split = new double[n];               // this month's split factor, 0 if none
    /**
     * Shares today per share at the founding, per company: the product of
     * every split and consolidation. What makes a price HISTORY possible -
     * a hundred-for-one split cuts the quote a hundredfold overnight and a
     * chart of the raw quote would show a cliff where nothing happened, so
     * the history is kept per FOUNDING share: today's quote times this.
     */
    private final double[] splitFactor = new double[n];
    private double lifetimeVolume;
    private boolean open;

    public Exchange() {
        java.util.Arrays.fill(mid, Equity.FOUNDING_PRICE);
        java.util.Arrays.fill(fair, Equity.FOUNDING_PRICE);
        java.util.Arrays.fill(lastMid, Equity.FOUNDING_PRICE);
        java.util.Arrays.fill(splitFactor, 1);
    }

    /** Clears the month's flows and lets last month's unfilled demand fade. Before anything trades. */
    public void startMonth() {
        for (int c = 0; c < n; c++) {
            soldToHouseholds[c] = 0; boughtFromHouseholds[c] = 0;
            soldAbroad[c] = 0; boughtFromAbroad[c] = 0; emigrantsPaid[c] = 0;
            buybackToHouseholds[c] = 0; buybackToDesk[c] = 0; buybackAbroad[c] = 0;
            specialDividend[c] = 0;
            volume[c] = 0;
            split[c] = 0;
            lastMid[c] = mid[c];
            demand[c] *= DEMAND_DECAY;
            unfilled[c] = 0;
        }
        ownBoughtThisMonth = 0;
        ownSoldThisMonth = 0;
        householdBuying = 0;
        bestBuy = -1;
    }

    /* =====================================================================
       THE QUOTE
       ===================================================================== */

    /**
     * Re-reads what every share is worth and what the desk can carry, and
     * quotes accordingly. Called at the top of the month's trading and after
     * the trades, so the mark the bank carries is the closing quote.
     *
     * @param book       each company's equity on its books, register order
     * @param bankEquity the bank's, for the position limits
     */
    public void quote(Equity register, double[] book, double bankEquity, double worldRate) {
        open = bankEquity > 0;
        for (int c = 0; c < n; c++) {
            if (register.getShares(c) <= 0) { fair[c] = Equity.FOUNDING_PRICE; mid[c] = fair[c]; limit[c] = 0; continue; }
            fair[c] = Math.max(1e-9, register.fairValue(c, book[c], worldRate));
            limit[c] = open ? POSITION_LIMIT * bankEquity / fair[c] : 0;
            bookLimit = open ? BOOK_LIMIT * bankEquity : 0;
            double held = register.getDealerShares(c) - demand[c];
            double scale = Math.max(limit[c], MIN_LIMIT_OF_FLOAT * register.getShares(c));
            double pressure = scale > 0 ? PRESSURE * held / scale : 0;
            mid[c] = fair[c] * Math.min(CEILING, Math.max(FLOOR, 1 - pressure));
        }
    }

    public boolean isOpen()          { return open; }
    public double mid(int c)         { return mid[c]; }
    public double bid(int c)         { return mid[c] * (1 - SPREAD / 2); }
    public double ask(int c)         { return mid[c] * (1 + SPREAD / 2); }
    public double fair(int c)        { return fair[c]; }
    public double limit(int c)       { return limit[c]; }
    public double lastMid(int c)     { return lastMid[c]; }
    /** What the desk carries a share at: the quote or fair value, whichever is lower. */
    public double mark(int c)        { return Math.min(mid[c], fair[c]); }

    /**
     * Whether a company would sell new shares at today's quote: not while
     * the market has them under fair value by more than the tolerance. A
     * company quoted at half what it is worth borrows for its plan instead
     * of giving half of it away - Real Estate, issuing into a desk that was
     * long it and then buying the same shares back at a tenth a year, went
     * from 54,000 shares to thirty million and back, each round at a
     * different price. Measured, seed 0, months 1,200-2,400.
     */
    public boolean quoteSupportsIssue(int c) {
        return !open || mid[c] >= fair[c] * (1 - BUYBACK_TOLERANCE);
    }

    /** Dividend yield, annual, at a price. */
    public double yieldAt(Equity register, int c, double price) {
        return price > 0 ? register.dividendPerShareAnnual(c) / price : 0;
    }

    /** What the desk holds, at its mark: the bank's securities line. */
    public double markToMarket(Equity register) {
        double total = 0;
        for (int c = 0; c < n; c++) total += register.getDealerShares(c) * mark(c);
        return total;
    }

    /** What the city's households hold, at the quote. */
    public double marketValueOfHouseholds(Equity register, HouseholdBalance households) {
        double total = 0;
        for (int c = 0; c < n; c++) total += households.sharesHeld(c) * mid[c];
        return total;
    }

    public double marketCap(Equity register, int c) { return register.getOutstanding(c) * mid[c]; }

    /* =====================================================================
       THE DEALS. Each is one trade with the desk at the month's quote; the
       bank's cash and mark move here, the register's counts move here, and
       whoever traded is the caller's business.
       ===================================================================== */

    /** Shares the desk will still buy in this company: up to its capacity, and none of its own unless flush. */
    double deskCanBuy(Equity register, int c) {
        if (!open) return 0;
        if (c == Equity.BANK) {
            // Its own shares are a buyback, and a buyback is paced like any
            // company's: only when flush, and never more than the pace a
            // month. Unpaced, the world sold a loss-making bank back its own
            // capital at a tenth a month - $23M of $32M in a year, measured.
            if (!bankFlush) return 0;
            return Math.max(0, BUYBACK_PACE / 12 * register.getShares(c) - ownBoughtThisMonth);
        }
        double room = Math.max(0, CAPACITY * limit[c] - register.getDealerShares(c));
        double bookRoom = Math.max(0, bookLimit - bookAtFair(register)) / fair[c];
        return Math.min(room, bookRoom);
    }

    /** What the desk holds in every company at fair value. */
    private double bookAtFair(Equity register) {
        double total = 0;
        for (int c = 0; c < n; c++) total += Math.max(0, register.getDealerShares(c)) * fair[c];
        return total;
    }

    private double bookLimit;
    private double ownBoughtThisMonth;

    private boolean bankFlush;

    /**
     * The desk buys shares from a household: the household's cell is debited
     * by the caller. Cash out of the bank.
     *
     * @return cash paid
     */
    double deskBuysFromHousehold(Equity register, Bank bank, int c, double shares) {
        shares = Math.min(shares, deskCanBuy(register, c));
        if (!open || shares <= 0) return 0;
        double cash = shares * bid(c);
        bank.deskPays(cash);
        register.deskBuysFromHouseholds(c, shares);
        if (c == Equity.BANK) ownBoughtThisMonth += shares;
        boughtFromHouseholds[c] += cash;
        volume[c] += shares;
        lifetimeVolume += shares;
        return cash;
    }

    /**
     * The desk sells shares to households for cash: the caller credits the
     * cells. Cash into the bank.
     *
     * @return shares sold
     */
    double deskSellsToHouseholds(Equity register, Bank bank, int c, double cash) {
        if (!open || cash <= 0) return 0;
        double shares = cash / ask(c);
        bank.deskReceives(cash);
        register.deskSellsToHouseholds(c, shares);
        if (c == Equity.BANK) ownSoldThisMonth += shares;
        soldToHouseholds[c] += cash;
        volume[c] += shares;
        lifetimeVolume += shares;
        return shares;
    }

    private double deskBuysFromAbroad(Equity register, Bank bank, int c, double shares, boolean emigrant) {
        shares = Math.min(shares, deskCanBuy(register, c));
        if (!open || shares <= 0) return 0;
        double cash = shares * bid(c);
        bank.deskPays(cash);
        register.deskBuysFromAbroad(c, shares);
        if (c == Equity.BANK) ownBoughtThisMonth += shares;
        boughtFromAbroad[c] += cash;
        if (emigrant) emigrantsPaid[c] += cash;
        volume[c] += shares;
        lifetimeVolume += shares;
        return cash;
    }

    private double deskSellsAbroad(Equity register, Bank bank, int c, double shares) {
        if (!open || shares <= 0) return 0;
        double cash = shares * ask(c);
        bank.deskReceives(cash);
        register.deskSellsAbroad(c, shares);
        if (c == Equity.BANK) ownSoldThisMonth += shares;
        soldAbroad[c] += cash;
        volume[c] += shares;
        lifetimeVolume += shares;
        return cash;
    }

    /* =====================================================================
       THE MONTH
       ===================================================================== */

    /** What the month's trading needs to know about a company that is not on the register. */
    public interface Companies {
        /** Cash in the company's till, after anything it can bring home for this. */
        double cashAvailable(int company, double wanted);
        /** Debits the company's till for a buyback. */
        void payBuyback(int company, double cash);
        /** Debits the company's till for a special dividend and books it as a dividend paid; the exchange pays the holders. */
        void paySpecialDividend(int company, double cash);
        double assets(int company);
        double equity(int company);
        double monthlyOperatingCost(int company);
        /** True when the bank may buy its own shares: capital well past what it must hold. */
        boolean bankFlush();
    }

    /**
     * Runs the month: emigrants, the world, the households, the companies -
     * then re-quotes, splits what needs splitting, and hands the bank its mark.
     *
     * @param book        each company's book equity, register order
     * @param depositRate what the bank pays savers, annual
     */
    public void takeMonth(Equity register, HouseholdBalance households, Bank bank,
                          Companies companies, double[] book, double worldRate,
                          double depositRate) {

        bankFlush = companies.bankFlush();
        quote(register, book, bank.equity(), worldRate);
        if (!open) {
            // No dealer, no market. Leavers keep their shares abroad, as before.
            bank.markSecurities(markToMarket(register));
            return;
        }

        double hurdle = Math.max(0, worldRate) + Equity.FOREIGN_PREMIUM;

        for (int c = 0; c < n; c++) {
            if (register.getShares(c) <= 0) continue;

            /* ---- 1. the emigrants ---- */
            double leaving = households.getSharesTakenAway(c);
            if (leaving > 0) {
                deskBuysFromAbroad(register, bank, c, Math.min(leaving, register.getForeignShares(c)), true);
            }

            /* ---- 2. the world ---- */
            // A company with no record has no yield to be judged on. The
            // world bought its founding shares on prospects and holds them
            // for the year; the alternative was the world selling every new
            // company back to the desk at a tenth a month, which it did.
            if (register.getMonthsRecorded(c) < Equity.RECORD_MONTHS) continue;
            double yieldAsk = yieldAt(register, c, ask(c));
            double yieldBid = yieldAt(register, c, bid(c));
            if (yieldAsk > hurdle * (1 + FOREIGN_TOLERANCE)) {
                double want = FOREIGN_SPEED * (yieldAsk / hurdle - 1) * register.getOutstanding(c);
                double can = deskCanSell(register, c);
                deskSellsAbroad(register, bank, c, Math.min(want, can));
                noteUnfilled(c, want - can);
            } else if (yieldBid < hurdle * (1 - FOREIGN_TOLERANCE)
                    && bid(c) >= fair[c] * (1 - FOREIGN_TOLERANCE)) {
                // Into strength, never into a crash: a holder with no yield
                // at a price under what the company is worth waits.
                double want = FOREIGN_SPEED * (1 - yieldBid / hurdle) * register.getForeignShares(c);
                deskBuysFromAbroad(register, bank, c, Math.min(want, register.getForeignShares(c)), false);
            }
        }

        /* ---- 3. the households, into the best yield, then the next ---- */
        buyForHouseholds(register, households, bank, depositRate);

        /* ---- 4. the companies buy back, or pay out ---- */
        for (int c = 0; c < n; c++) {
            if (c == Equity.BANK || register.getShares(c) <= 0) continue;
            Equity.Regime regime = register.getRegime(c);
            if (regime == Equity.Regime.BAD || regime == Equity.Regime.NEW) continue;
            double assets = companies.assets(c);
            double equity = companies.equity(c);
            if (assets <= 0) continue;
            double excess = equity - (register.getTargetEquityShare(c) + OVER_TARGET) * assets;
            if (excess <= 0) continue;
            double cushion = BUYBACK_CUSHION_MONTHS * Math.max(0, companies.monthlyOperatingCost(c));
            double worth = register.getOutstanding(c) * fair[c];
            double cap = Math.min(excess, BUYBACK_PACE / 12 * worth);
            double cash = companies.cashAvailable(c, cap + cushion) - cushion;
            double spend = Math.min(cap, cash);
            if (spend <= 0) continue;
            if (ask(c) <= fair[c] * (1 + BUYBACK_TOLERANCE)) {
                tender(register, households, bank, companies, c, spend);
            } else {
                // Every holder paid once, the desk's part into the bank -
                // the same hands as the ordinary dividend (Game.payDividends).
                companies.paySpecialDividend(c, spend);
                double deskBefore = register.getDividendDeskThisMonth(c);
                register.payDividend(c, spend, households);
                bank.receiveDividend(register.getDividendDeskThisMonth(c) - deskBefore);
                specialDividend[c] += spend;
            }
        }

        /* ---- and the closing quote ---- */
        quote(register, book, bank.equity(), worldRate);
        splitWhatNeedsIt(register, households);
        bank.markSecurities(markToMarket(register));
    }

    private int bestBuy = -1;
    private double householdBuying;

    /**
     * The households' month: every company yielding more than the deposit
     * rate plus the premium, best first, and what the desk cannot sell them
     * of one they put into the next. The demand the best could not meet is
     * what lifts its quote.
     */
    private void buyForHouseholds(Equity register, HouseholdBalance households, Bank bank, double depositRate) {
        double floor = Math.max(0, depositRate) + HOUSEHOLD_PREMIUM;
        Integer[] order = new Integer[n];
        int k = 0;
        for (int c = 0; c < n; c++) {
            if (register.getShares(c) > 0 && yieldAt(register, c, ask(c)) > floor) order[k++] = c;
        }
        if (k == 0) return;
        Integer[] wanted = java.util.Arrays.copyOf(order, k);
        java.util.Arrays.sort(wanted, (a, b) -> Double.compare(yieldAt(register, b, ask(b)), yieldAt(register, a, ask(a))));
        int[] companies = new int[k];
        double[] capacity = new double[k];
        for (int i = 0; i < k; i++) {
            companies[i] = wanted[i];
            capacity[i] = deskCanSell(register, wanted[i]) * ask(wanted[i]);
        }
        bestBuy = companies[0];
        double want = households.sharesWanted(MONTHLY_SHARE_OF_EXCESS);
        double soldOfBestBefore = soldToHouseholds[bestBuy];
        householdBuying = households.buyShares(this, register, bank, companies, MONTHLY_SHARE_OF_EXCESS, capacity);
        noteUnfilled(bestBuy, (want - (soldToHouseholds[bestBuy] - soldOfBestBefore)) / ask(bestBuy));
    }

    /**
     * Shares the desk can still sell in this company: what it holds, and
     * nothing it does not. Its own shares it can always issue, at the pace of
     * a buyback - selling its own shares is raising capital, not going short.
     */
    private double deskCanSell(Equity register, int c) {
        if (c == Equity.BANK) {
            // Issuing its own shares is raising capital, and a bank in
            // trouble raises it through its branches, not by printing
            // shares at a price near nothing: a dead bank issued a hundred
            // billion of them for pennies. Only when flush, at the pace of a
            // buyback.
            if (!open || !bankFlush) return 0;
            return Math.max(0, BUYBACK_PACE / 12 * register.getShares(c) - ownSoldThisMonth);
        }
        return Math.max(0, register.getDealerShares(c));
    }

    private double ownSoldThisMonth;

    /** A buyer the desk could not fill: remembered, and the next quote goes up to find a seller. */
    private void noteUnfilled(int c, double shares) {
        if (shares > 0) { unfilled[c] += shares; demand[c] += shares; }
    }

    /**
     * A tender: the company buys back this much, at the ask, from every
     * holder pro rata, and cancels the shares.
     */
    private void tender(Equity register, HouseholdBalance households, Bank bank,
                        Companies companies, int c, double spend) {
        double price = ask(c);
        double shares = spend / price;
        double outstanding = register.getShares(c);
        if (outstanding <= 0 || shares <= 0) return;
        shares = Math.min(shares, outstanding);
        spend = shares * price;

        double home = households.sharesHeld(c);
        double desk = Math.max(0, register.getDealerShares(c));
        double abroad = register.getForeignShares(c);
        double total = home + desk + abroad;
        if (total <= 0) return;
        double fromHome = shares * home / total;
        double fromDesk = shares * desk / total;
        double fromAbroad = shares * abroad / total;

        companies.payBuyback(c, spend);
        households.tenderShares(c, fromHome / Math.max(home, 1e-12), price);
        bank.deskReceives(fromDesk * price);
        register.cancel(c, fromHome, fromDesk, fromAbroad);
        buybackToHouseholds[c] += fromHome * price;
        buybackToDesk[c] += fromDesk * price;
        buybackAbroad[c] += fromAbroad * price;
        volume[c] += shares;
        lifetimeVolume += shares;
    }

    /**
     * A split or a consolidation, by a power of ten, for any share quoted a
     * hundred times its founding price or a hundredth of it. Every count on
     * the register, in every household and on the desk moves by the factor;
     * every price by its inverse. Value is untouched.
     */
    private void splitWhatNeedsIt(Equity register, HouseholdBalance households) {
        for (int c = 0; c < n; c++) {
            if (register.getShares(c) <= 0) continue;
            // A company worth nothing is not consolidated into nothing: a
            // failed bank's shares were folded a million to one on a fair
            // value of zero, and "last sold at" read a million dollars.
            if (fair[c] <= 1e-9) continue;
            double ratio = mid[c] / Equity.FOUNDING_PRICE;
            if (ratio < SPLIT_AT && ratio > 1 / SPLIT_AT) continue;
            double k = Math.pow(10, Math.floor(Math.log10(ratio)));
            if (!(k > 0) || k == 1) continue;
            register.split(c, k);
            households.splitShares(c, k);
            mid[c] /= k; fair[c] /= k; lastMid[c] /= k;
            limit[c] *= k; demand[c] *= k; unfilled[c] *= k; volume[c] *= k;
            split[c] = k;
            splitFactor[c] *= k;
        }
    }

    /* =====================================================================
       THE DISTRESS SALE, mid-month: a household short of money sells at the
       bid before it borrows. See HouseholdBalance.advanceMonth().
       ===================================================================== */

    /**
     * Sells a cell's shares for cash, pro rata across what it holds by value.
     *
     * @param needPer cash wanted, per household of the cell
     * @return cash raised, per household
     */
    double sellForHousehold(Equity register, Bank bank, Household cell, double needPer) {
        if (!open || needPer <= 0 || cell.households() <= 0) return 0;
        double worth = 0;
        for (int c = 0; c < n; c++) worth += cell.shares[c] * bid(c);
        if (worth <= 0) return 0;
        double share = Math.min(1, needPer / worth);
        double raised = 0;
        for (int c = 0; c < n; c++) {
            if (cell.shares[c] <= 0) continue;
            double sell = Math.min(cell.shares[c] * share * cell.households(), deskCanBuy(register, c));
            if (sell <= 0) continue;
            double cash = deskBuysFromHousehold(register, bank, c, sell);
            cell.shares[c] -= sell / cell.households();
            raised += cash / cell.households();
        }
        return raised;
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
    public double getSpecialDividend(int c)      { return specialDividend[c]; }
    public double getVolume(int c)               { return volume[c]; }
    public double getLifetimeVolume()            { return lifetimeVolume; }
    public int getBestBuy()                      { return bestBuy; }
    /** Shares buyers wanted and could not get this month. */
    public double getUnfilled(int c)             { return unfilled[c]; }
    /** ...and what of that, this month's and earlier, the quote still carries. */
    public double getDemand(int c)               { return demand[c]; }
    public double getHouseholdBuying()           { return householdBuying; }
    /** This month's split factor: 100 for a hundred-for-one, .01 for a consolidation, 0 for none. */
    public double getSplit(int c)                { return split[c]; }
    /** Shares today for one share at the founding. */
    public double getSplitFactor(int c)          { return splitFactor[c]; }
    /** The quote per FOUNDING share: continuous through every split. The price history is this. */
    public double midPerFoundingShare(int c)     { return mid[c] * splitFactor[c]; }
    /** ...and the register's reckoning, the same way. */
    public double fairPerFoundingShare(int c)    { return fair[c] * splitFactor[c]; }

    private double sum(double[] a) { double s = 0; for (double v : a) s += v; return s; }
    public double getSoldToHouseholds()     { return sum(soldToHouseholds); }
    public double getBoughtFromHouseholds() { return sum(boughtFromHouseholds); }
    public double getSoldAbroad()           { return sum(soldAbroad); }
    public double getBoughtFromAbroad()     { return sum(boughtFromAbroad); }
    public double getEmigrantsPaid()        { return sum(emigrantsPaid); }
    public double getBuybackToHouseholds()  { return sum(buybackToHouseholds); }
    public double getBuybackToDesk()        { return sum(buybackToDesk); }
    public double getBuybackAbroad()        { return sum(buybackAbroad); }
    public double getSpecialDividend()      { return sum(specialDividend); }
    public double getVolume()               { return sum(volume); }

    /* ------------------------------- saving ------------------------------- */

    /** Slots per company before the split factor joined (the exchange's first night). */
    public static final int SLOTS_BEFORE_SPLITS = 3;

    /**
     * The quote, the demand it carries and the split factor are STOCKS: the
     * next month trades at the first two before anything re-quotes, and the
     * price history is read through the third.
     */
    public static final int SLOTS = SLOTS_BEFORE_SPLITS + 1;

    public double[] toSaveArray() {
        double[] out = new double[n * SLOTS + 1];
        int i = 0;
        for (int c = 0; c < n; c++) {
            out[i++] = mid[c]; out[i++] = fair[c]; out[i++] = demand[c]; out[i++] = splitFactor[c];
        }
        out[i] = lifetimeVolume;
        return out;
    }

    public boolean restore(String[] keys, double[] saved) {
        if (keys == null || saved == null) return false;
        int slots = keys.length > 0 ? (saved.length - 1) / keys.length : 0;
        if (saved.length != keys.length * slots + 1
                || (slots != SLOTS && slots != SLOTS_BEFORE_SPLITS)) return false;
        int i = 0;
        for (String key : keys) {
            int c = Equity.indexOf(key);
            if (c < 0) { i += slots; continue; }
            mid[c] = saved[i++];
            fair[c] = saved[i++];
            demand[c] = saved[i++];
            // A save from before the factor was kept has had no split its
            // history could remember: one share then is one share now.
            splitFactor[c] = slots >= SLOTS ? Math.max(1e-12, saved[i++]) : 1;
            lastMid[c] = mid[c];
        }
        lifetimeVolume = saved[i];
        // Whether there is a market is the bank's to say, not the save's:
        // a city loaded with its bank in resolution has no dealer until it
        // is put back on its feet. See reopen().
        open = false;
        return true;
    }

    /**
     * After a load: the market is open exactly when the bank has capital,
     * as the next month's quote will say. Seen on the PC, 2026-09-11: a
     * loaded city with a dead bank showed its households' shares "at the
     * market" and every owners block a quote, until the first month closed.
     */
    public void reopen(double bankEquity) {
        open = bankEquity > 0;
    }

    public void reset() {
        java.util.Arrays.fill(mid, Equity.FOUNDING_PRICE);
        java.util.Arrays.fill(fair, Equity.FOUNDING_PRICE);
        java.util.Arrays.fill(lastMid, Equity.FOUNDING_PRICE);
        java.util.Arrays.fill(limit, 0);
        java.util.Arrays.fill(demand, 0);
        java.util.Arrays.fill(splitFactor, 1);
        lifetimeVolume = 0;
        open = false;
        startMonth();
    }

    /** Prices and the month's cash in the new unit; share counts do not move. */
    public void redenominate(double scale) {
        for (int c = 0; c < n; c++) {
            mid[c] *= scale; fair[c] *= scale; lastMid[c] *= scale;
            soldToHouseholds[c] *= scale; boughtFromHouseholds[c] *= scale;
            soldAbroad[c] *= scale; boughtFromAbroad[c] *= scale; emigrantsPaid[c] *= scale;
            buybackToHouseholds[c] *= scale; buybackToDesk[c] *= scale; buybackAbroad[c] *= scale;
            specialDividend[c] *= scale;
        }
    }
}
