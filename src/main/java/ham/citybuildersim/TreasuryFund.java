package ham.citybuildersim;

import java.util.ArrayList;
import java.util.List;

/**
 * The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand, and the bank it took over in a rescue (0.7.14).
 *
 * WHY. Until 0.7.14 the treasury could buy nothing but land, buildings, the
 * vault's dollars and its own paper back, and a failed bank was put back on
 * its feet with a gift - capital from the treasury for no shares, the owners
 * keeping theirs, the hole "absorbed from outside" by creditors who were in
 * fact the central bank's window. It had been on the list since 0.7.12
 * (the-firms-sell-bonds.md: "the treasury as a buyer, with rescue-for-shares,
 * comes in the next batch"). Jerus's answers, 2026-09-27:
 *
 *   who absorbs a failed bank's hole  "City takes the shares ... but later
 *                                      bank can dilute by issuing shares...
 *                                      we dont want commercial to be owned by
 *                                      government fully in the long run"
 *   when the rescue happens           "Treasury setting, auto"
 *   a standing bank under its minimum "Preferred shares ... and its a popup
 *                                      message saying bank wants to issue you
 *                                      shares or something"
 *   the fund                          "Rule plus your hand"
 *   how the city's stake comes down   "New issues only"
 *   when the treasury can't pay       "Central bank advances it"
 *   what goes into the fund           "dial, default 0, max 300%" - of "the
 *                                      year's surplus"
 *   where it invests                  "Home only"
 *
 * WHAT IT HOLDS, and where each holding is kept - where the others' are:
 *
 *   THE MARKET BOOK. Company shares, the bank's among them, on the register
 *   (Equity's city holding, cityShares less the rescue book), and the
 *   businesses' bonds (CorporateBond.city). Bought on the book by the rule
 *   below and by the player's hand; never at an issue - not an offering
 *   (Equity.offer()), not the bank's own new shares
 *   (Bank.issuesOwnShares(), refused on the book by Exchange's clearing),
 *   not a bond's bookbuild. That is Jerus's "new issues only": the city's
 *   stake in anything falls as the company sells new shares to others.
 *
 *   THE RESCUE BOOK. The bank's shares taken in a resolution
 *   (Equity.takeAllForCity(); the register's cityRescue), and the senior
 *   preferred and its warrants bought when a standing bank asked
 *   (Bank.Preferred, TARP's terms). A separate book: outside the rule's
 *   70/30 mix and outside its 10% limit, and the rule never sells it. The
 *   hand may.
 *
 *   ITS CASH, here. It earns nothing: the treasury's cash earns nothing in
 *   this model (the bank pays deposit interest to households and sectors,
 *   never to the city), and the fund's is the government's too.
 *
 * WHAT IT IS WORTH is each holding at the mark every other holder uses -
 * but for a share whose last trade is over a year old (0.7.48, C4): a
 * share at the exchange's price (Exchange.price(), the last trade or fair
 * value before one - what the households' shares are valued at) while that
 * trade is no older than Exchange.STALE_MARK_MONTHS, and the register's fair
 * value after (Exchange.cityMark()), a bond at
 * the market's valuation (BondMarket.modelPrice(), what every participant
 * bids around), the preferred at par, the price it is redeemed at, and the
 * warrants at Black-Scholes (callValue()) - plus its cash. Game.fundValue()
 * adds them up.
 *
 * THE RULE (Norway's Government Pension Fund Global, where Jerus's numbers
 * come from):
 *
 *   MONEY IN: the dial, a share of the year's budget surplus, paid once a
 *   year at the calendar's year end (Game.fundYearEnd() says why that
 *   cadence) - before the rollover nets, and the surplus used once between
 *   them through the rollover's ledger (Rollover.noteFundTook()). Above
 *   100% the extra comes from the treasury's cash, never below one month of
 *   its own spending (Canada's prudential liquidity, the rollover's source),
 *   so the fund is never what the central bank then advances. A deficit
 *   year saves nothing.
 *
 *   THE MIX: EQUITY_WEIGHT in shares, the rest in bonds (nbim.no: 70/30),
 *   shares across companies by market value, bonds across bonds by market
 *   value, never more than OWNERSHIP_LIMIT of a company (the GPFG mandate,
 *   14 May 2018). Each month it bids for the gap - a share at the desk's
 *   ask, fair value plus RULE_PREMIUM (0.7.48, C3), a bond at its value -
 *   and takes only what is offered; what does not fit waits as cash. Outside the band
 *   (REBALANCE_OVER, REBALANCE_UNDER) it sells the overweight side at fair
 *   value, the desk's own rule for an excess.
 *
 *   MONEY OUT: the withdrawal dial's share of its whole value a month, to
 *   the budget as a revenue line - by default TRANSFER_RATE a year, a
 *   twelfth a month, Norway's fiscal rule - and at or under the default from
 *   its cash only; what the cash cannot cover is not paid that month. Over the default the dial
 *   spends the fund: what its cash cannot cover is sold from its market book
 *   and paid at the next month's top, and the rule buys nothing meanwhile
 *   (0.7.48, C1 - Jerus: "the city fund, you should be able to click how
 *   much to withdraw automatically, even 0 or 10% a month"). Its dividends
 *   and coupons stay in it.
 *
 * THE HAND (the order ticket on a security's page among the Finances tab's
 * fund pages, ui/FundScreen since 0.7.39): buy and sell on the book at fair
 * value - the price the rule asks at, and a bond's bid; since 0.7.48 it bids
 * a share RULE_PREMIUM over it - or at a price the player names (0.7.39),
 * good for a month (HandOrder); pay in and draw out, transfers between the
 * treasury and the fund, journalled, never revenue or spending. An order
 * waiting for the step can be cancelled, and a buy's money is the hand's
 * from the moment it is placed until the step that withdraws it: the rule
 * bids with the rest (handReserve(), 0.7.39). So is its room under
 * OWNERSHIP_LIMIT: every buy of the fund's on a company counts against the
 * cap as if it filled, and the rule's bid makes way for the hand's, at the
 * step and between steps (Exchange.fundRoom()).
 *
 * WHAT EACH HOLDING COST (0.7.39): FundLedger, kept here and saved with the
 * fund - average cost, what sales and maturities realized, the income, and
 * every trade and event - written by hooks where the holdings move and read
 * by nothing the month does.
 *
 * @author Jerus
 */
public final class TreasuryFund {

    /* ============================== the dials ============================== */

    /** The most the dial takes: three times the year's surplus - Jerus's "max 300%"; past 100% the extra comes from the treasury's cash. */
    public static final double MAX_DIAL = 3.0;

    /** The share of the market book in company shares: 70%, the Government Pension Fund Global's strategic equity weight (nbim.no/en/investments); the rest is the businesses' bonds. */
    public static final double EQUITY_WEIGHT = .70;

    /** Rebalanced when its equity share passes this: 74%, the GPFG mandate's "If the equity share ... exceeds 74 percent ... rebalancing shall take place" (management mandate, 14 May 2018). */
    public static final double REBALANCE_OVER = .74;

    /** ...or falls this far under EQUITY_WEIGHT: four points, the same mandate's "more than four percentage points lower than the weight in the strategic benchmark index". */
    public static final double REBALANCE_UNDER = .04;

    /** The rule bids at the desk's ask: the cheapest price anybody stands ready to sell at; at fair value it met nobody (C3). Since 0.7.102 the withdrawal's forced sale asks at the desk's bid, this under fair value - the mirror (A21; Exchange.postFund()). */
    public static final double RULE_PREMIUM = Exchange.SPREAD / 2;

    /** The most of any one company's shares the rule holds: 10%, the GPFG mandate's "may not be invested in more than 10 per cent of the voting shares in an individual company" (14 May 2018). The bank's shares bought on the market count; the rescue book does not. */
    public static final double OWNERSHIP_LIMIT = .10;

    /** Norway's fiscal rule, a year of the fund's whole value: 3% - transfers follow the fund's expected real return, 3% since 2017 (regjeringen.no). What it pays the budget at the default withdrawal, and the withdrawal dial's step a twelfth of it, since 0.7.48 (C1). */
    public static final double TRANSFER_RATE = .03;

    /** A year, in months: the transfer's twelfth and the surplus's year. */
    public static final int YEAR_MONTHS = 12;

    /** The withdrawal dial's step, a share of the fund's whole value a month: a twelfth of TRANSFER_RATE, so Norway's rule is one step (Jerus, 2026-10-05: "even 0 or 10% a month"). */
    public static final double WITHDRAWAL_STEP = TRANSFER_RATE / YEAR_MONTHS;

    /** A new city's and an older save's withdrawal: Norway's rule, the transfer paid since 0.7.14 to the bit. */
    public static final int DEFAULT_WITHDRAWAL_STEPS = 1;

    /** The most it withdraws: 10% of its value a month. */
    public static final int MAX_WITHDRAWAL_STEPS = 40;

    /** The months of the bank share's price history its volatility is read over, for the warrants' value: a year of the monthly prices the history keeps (HistorySave's share prices). */
    public static final int VOLATILITY_MONTHS = 12;

    /** A declined offer comes back after this many months while the bank is still under its minimum: a quarter (Jerus, round 2: "Quarter") - and the bank asks no sooner after an accepted one either, whose size was what it asked for (the rest, past 3% of its risk-weighted assets, is left to its own share issues). */
    public static final int OFFER_AGAIN_MONTHS = 3;

    /* ============================== the settings ============================== */

    /** When the bank fails: resolved the month it fails, or frozen until the player presses the button. */
    public enum RescueMode {
        /** The city steps in the month the bank fails - a new game's setting. */
        AUTOMATIC,
        /** The bank waits frozen, carrying its hole at the central bank's window, until the player presses the Bank tab's button - an old save's, and the bare constructor city's. */
        BUTTON
    }

    private double cash;
    private double dial;
    /** The withdrawal dial, in whole steps of WITHDRAWAL_STEP (0.7.48, C1). */
    private int withdrawalSteps = DEFAULT_WITHDRAWAL_STEPS;
    private RescueMode rescue = RescueMode.BUTTON;

    /* ============================== the preferred offer ============================== */

    private boolean offerPending;
    private int offerMonth = -1;
    private int declinedMonth = -1;
    private int acceptedMonth = -1;
    private int offersMade, offersAccepted, offersDeclined;

    /* ============================== the rescues ============================== */

    /** What the city paid for the rescue book's shares it still holds: their cost, reduced in proportion when the hand sells some. */
    private double rescueCost;

    /**
     * One resolution, as it happened: the month; what the city paid and how -
     * from its cash, and what the central bank will advance; the hole and the
     * capital to reopen; the shares it took, and from whom, at the price
     * they last traded; the preferred and the warrants the hole took.
     */
    public static final class Resolution {
        int month;
        double paid, fromCash, advanced, shortfall, exitCapital;
        double shares, householdsShares, householdsValue, worldShares, worldValue, fundShares, fundValue;
        double preferredCancelled, warrantsCancelled;

        Resolution() { }

        public int month()                 { return month; }
        public double paid()               { return paid; }
        public double fromCash()           { return fromCash; }
        public double advanced()           { return advanced; }
        public double shortfall()          { return shortfall; }
        public double exitCapital()        { return exitCapital; }
        public double shares()             { return shares; }
        public double householdsShares()   { return householdsShares; }
        public double householdsValue()    { return householdsValue; }
        public double worldShares()        { return worldShares; }
        public double worldValue()         { return worldValue; }
        public double fundShares()         { return fundShares; }
        public double fundValue()          { return fundValue; }
        public double preferredCancelled() { return preferredCancelled; }
        public double warrantsCancelled()  { return warrantsCancelled; }
        /** What the old owners lost, at the last price: the households', the world's and the fund's own market book. */
        public double ownersLost()         { return householdsValue + worldValue + fundValue; }
    }

    private final List<Resolution> resolutions = new ArrayList<>();

    /* ============================== the hand ============================== */

    /**
     * One of the player's orders, waiting for the month's step: a company's
     * shares (company, Equity's index) or a bond (bondId), to buy with this
     * much money or to sell this many shares or this much face. Posted at
     * the step beside the rule's, at fair value - or at `limit` when the
     * player named a price (0.7.39; 0 is fair value, what the rule asks) - and
     * good for the month like every order (Exchange, BondMarket). Once
     * posted it carries what it asked for at what price, and what of it has
     * filled, until the step after withdraws it (getPosted()).
     */
    public static final class HandOrder {
        boolean bond;
        int company = -1;
        int bondId = -1;
        boolean buy;
        double amount;
        int month;
        /** The price a unit it is to post at: money a share, or a price a unit of face; 0 for the rule's (0.7.39). */
        double limit;
        /** Once posted: the month, the units it asked for, the price it rested at, and what of it filled for how much. */
        int postedMonth = -1;
        double units, price, filled, spent;

        HandOrder() { }

        HandOrder(boolean bond, int company, int bondId, boolean buy, double amount, int month) {
            this(bond, company, bondId, buy, amount, month, 0);
        }

        HandOrder(boolean bond, int company, int bondId, boolean buy, double amount, int month, double limit) {
            this.bond = bond;
            this.company = company;
            this.bondId = bondId;
            this.buy = buy;
            this.amount = amount;
            this.month = month;
            this.limit = Double.isFinite(limit) && limit > 0 ? limit : 0;
        }

        public boolean bond()     { return bond; }
        public int company()      { return company; }
        public int bondId()       { return bondId; }
        public boolean buy()      { return buy; }
        /** Money to spend on a buy; shares or face to sell. */
        public double amount()    { return amount; }
        /** The month it was placed in. */
        public int month()        { return month; }
        /** The price it posts at, a unit: 0 for fair value (a bond's: its value) - what the rule asks at; it bids a share RULE_PREMIUM over (0.7.48). */
        public double limit()     { return limit; }
        /** The month it was posted at the step, or -1 while it waits. */
        public int postedMonth()  { return postedMonth; }
        /** Posted: the units it asked for (shares, or face). */
        public double units()     { return units; }
        /** ...the price it rested at, a unit. */
        public double price()     { return price; }
        /** ...what of it has filled, and what that cost or brought in. */
        public double filled()    { return filled; }
        public double spent()     { return spent; }
        /** What a buy still holds of the fund's cash: its amount while it waits; posted, the units still to fill at its price. */
        public double reserved() {
            if (!buy) return 0;
            return postedMonth < 0 ? Math.max(0, amount) : Math.max(0, units - filled) * price;
        }
    }

    private final List<HandOrder> hand = new ArrayList<>();

    /** The player's orders on the books: posted at the last step, resting until the next (0.7.39). */
    private final List<HandOrder> posted = new ArrayList<>();

    /* ============================== the cost basis (0.7.39) ============================== */

    /** What each holding cost, and everything the fund did: FundLedger. */
    private FundLedger ledger = new FundLedger();

    /** True after a save from before the ledger: Game seeds it once the city is back (Game.seedFundLedger()). */
    private boolean ledgerToSeed;

    /** The fund's cost basis and record. */
    public FundLedger getLedger()   { return ledger; }

    /** True while a save from before 0.7.39 waits for its ledger to be seeded. */
    public boolean needsLedgerSeed() { return ledgerToSeed; }

    /** The seed, done (Game.seedFundLedger()). */
    void ledgerSeeded()              { ledgerToSeed = false; }

    /* ============================== the record ============================== */

    // The last year-end pay-in, and the year's transfers so far (the Finances page's "this year").
    private int lastPayInMonth = -1;
    private double lastPayInYearSurplus, lastPayInFromSurplus, lastPayInFromCash;
    private int transferYear = -1;
    private double transfersThisYear, transferShortThisYear;

    // Over the fund's life, for the page and the playtest.
    private double paidInFromSurplus, paidInFromCash, handPaidIn, handDrawnOut;
    private double transfersPaid, transfersShort;
    private double dividendsMarket, dividendsRescue, coupons, principal, bondFaceLost;
    private double preferredBought, preferredDividends, preferredRedeemed, warrantsBoughtBack, warrantSharesTaken;
    private double sharesBought, sharesSold, bondsBought, bondsSold, rescueSold;

    // This month's working, cleared at the top of the month: the transfer
    // (a budget line too - NationalAccounts carries it) and what came in and
    // went out, which the Fund page reads after the month has closed - and so
    // saved with it (State.month), since a reloaded city cannot rebuild a flow.
    private double transferDue, transferPaid;
    private double monthDividends, monthCoupons, monthPrincipal, monthBought, monthSold;
    // Over the default withdrawal (C1): what the month's cash could not cover,
    // sold from the market book at the step and paid at the next month's top -
    // carried, so not cleared with the month - and what that payment was.
    private double toRaise, transferPaidLate;

    /* ============================== the money ============================== */

    /** Its cash: what waits for the book, and what the transfer is paid from. */
    public double getCash()       { return cash; }

    /** Cash in: a dividend, a coupon, a sale, a pay-in. */
    void receive(double amount)   { if (amount > 0) cash += amount; }

    /** Cash out: a purchase, the transfer, a draw-out. The caller has checked it holds it. */
    void pay(double amount)       { if (amount > 0) cash -= amount; }

    /** The dial: a share of the year's budget surplus, 0 to MAX_DIAL. */
    public double getDial()       { return dial; }

    /** ...set by the player (the Finances tab), clamped to 0-MAX_DIAL. */
    public void setDial(double d) { dial = Double.isFinite(d) ? Math.max(0, Math.min(MAX_DIAL, d)) : 0; }

    public RescueMode getRescueMode()          { return rescue; }
    public void setRescueMode(RescueMode mode) { rescue = mode == null ? RescueMode.BUTTON : mode; }

    /* ============================== money in: the dial ============================== */

    /**
     * WHAT THE DIAL TAKES AT A YEAR END, from the year's budget surplus and
     * from the treasury's cash: {from the surplus, from cash}.
     *
     * The surplus's share is min(dial, 1) of the year's surplus, never more
     * than the part of it nothing has used (the rollover's netting during
     * the year, off the same ledger); past 100% the extra, (dial - 1) of the
     * year's surplus, comes from cash. Both together never take the treasury
     * below `floor` - one month of its own spending - and the surplus's part
     * goes first. A deficit year, or no year at all, takes nothing.
     */
    public static double[] payIn(double dial, double yearSurplus, double unused, double cash, double floor) {
        if (!(dial > 0) || !(yearSurplus > 0)) return new double[] { 0, 0 };
        double room = Math.max(0, cash - Math.max(0, floor));
        double fromSurplus = Math.min(Math.min(1, dial) * yearSurplus, Math.max(0, unused));
        fromSurplus = Math.max(0, Math.min(fromSurplus, room));
        double fromCash = Math.max(0, dial - 1) * yearSurplus;
        fromCash = Math.max(0, Math.min(fromCash, room - fromSurplus));
        return new double[] { fromSurplus, fromCash };
    }

    /**
     * WHAT THE ROLLOVER MUST LEAVE FOR THE FUND during a year: the dial's
     * share of this calendar year's surplus so far, which the year-end
     * pay-in will claim - so the dial's share is taken before the rollover
     * nets (Jerus's explicit choice before the automatic netting). Nothing at
     * a dial of 0, which is 0.7.13's rollover to the byte.
     */
    public static double reservedFor(double dial, double surplusThisYearSoFar) {
        if (!(dial > 0)) return 0;
        return Math.min(1, dial) * Math.max(0, surplusThisYearSoFar);
    }

    /** A year-end pay-in, booked: the surplus's part and the cash's, off the treasury into the fund (the caller moves the treasury's cash). */
    void notePayIn(int month, double yearSurplus, double fromSurplus, double fromCash) {
        cash += fromSurplus + fromCash;
        lastPayInMonth = month;
        lastPayInYearSurplus = yearSurplus;
        lastPayInFromSurplus = fromSurplus;
        lastPayInFromCash = fromCash;
        paidInFromSurplus += fromSurplus;
        paidInFromCash += fromCash;
    }

    /** The player pays in by hand: the caller moves the treasury's cash. */
    void notePaidInByHand(double amount)  { cash += amount; handPaidIn += amount; }

    /** ...and draws out. */
    void noteDrawnOutByHand(double amount) { cash -= amount; handDrawnOut += amount; }

    /* ============================== the mix ============================== */

    /**
     * WHAT THE RULE SELLS OF ITS SHARES to rebalance, in money at its marks:
     * the excess over EQUITY_WEIGHT of its market book and cash when its
     * shares are more than REBALANCE_OVER of them; nothing otherwise. The
     * GPFG mandate's upper trigger, and rebalanced to the strategic weight.
     */
    public static double sharesOver(double shares, double bonds, double cash) {
        double v = Math.max(0, shares) + Math.max(0, bonds) + Math.max(0, cash);
        if (!(v > 0) || shares / v <= REBALANCE_OVER) return 0;
        return shares - EQUITY_WEIGHT * v;
    }

    /**
     * ...AND OF ITS BONDS: the excess over the rest of the weight when its
     * shares are more than REBALANCE_UNDER under EQUITY_WEIGHT and its bonds
     * over theirs - the mandate's lower trigger. A fund short of shares only
     * because its cash has not found a seller has no overweight side, and
     * sells nothing.
     */
    public static double bondsOver(double shares, double bonds, double cash) {
        double v = Math.max(0, shares) + Math.max(0, bonds) + Math.max(0, cash);
        if (!(v > 0) || shares / v >= EQUITY_WEIGHT - REBALANCE_UNDER) return 0;
        return Math.max(0, bonds - (1 - EQUITY_WEIGHT) * v);
    }

    /* ============================== money out: the transfer ============================== */

    /** A month's transfer on a fund of this value at Norway's rule, the default withdrawal: a twelfth of TRANSFER_RATE of it. */
    public static double transferOn(double value) {
        return value > 0 ? TRANSFER_RATE / YEAR_MONTHS * value : 0;
    }

    /* ----- the withdrawal dial (0.7.48, C1) ----- */

    /** The withdrawal, a share of the fund's whole value a month: whole steps of WITHDRAWAL_STEP, 0 to MAX_WITHDRAWAL_STEPS of them; at the default the very double transferOn() multiplies by. */
    public double getWithdrawal()          { return withdrawalSteps * WITHDRAWAL_STEP; }

    /** ...in its steps. */
    public int getWithdrawalSteps()        { return withdrawalSteps; }

    /** ...set by the player (Finances > The city's fund > Rules & cash): rounded to whole steps and held at 0-MAX_WITHDRAWAL_STEPS (stepsFor()); a share that is not a number leaves Norway's rule. */
    public void setWithdrawal(double share)  { withdrawalSteps = stepsFor(share); }

    /** The whole steps a share a month comes to, as the dial takes it: rounded, and held at 0-MAX_WITHDRAWAL_STEPS; a share that is not a number, the default. */
    public static int stepsFor(double share) {
        return Double.isFinite(share)
                ? (int) Math.max(0, Math.min(MAX_WITHDRAWAL_STEPS, Math.round(share / WITHDRAWAL_STEP)))
                : DEFAULT_WITHDRAWAL_STEPS;
    }

    /** A month's withdrawal on a fund of this value at the dial in force: transferOn() at the default, to the bit. */
    public double withdrawalOn(double value) {
        return value > 0 ? getWithdrawal() * value : 0;
    }

    /** ...and at a share of the caller's, as the dial would take it (stepsFor()): withdrawalOn() at the dial in force, to the bit - the preview's (PolicyPreview.fundWithdrawalAt()). */
    public static double withdrawalAt(double share, double value) {
        return value > 0 ? stepsFor(share) * WITHDRAWAL_STEP * value : 0;
    }

    /** True while the dial is over Norway's rule: what its cash cannot cover is sold from the market book to pay, and the rule buys nothing (a fund drawn past its expected return is being spent). */
    public boolean sellsToPay()            { return withdrawalSteps > DEFAULT_WITHDRAWAL_STEPS; }

    /** What the month's cash could not cover over the default, which the step sells the market book for and the next month's top pays: 0 at or under the default. */
    public double getToRaise()             { return toRaise; }

    /** What this month's top paid of last month's toRaise, from what it sold: 0 at or under the default. */
    public double getTransferPaidLate()    { return transferPaidLate; }

    /**
     * Pays the month's transfer on a fund worth `value` and returns what was
     * paid. At or under the default withdrawal from its cash only - "the
     * rule never sells to pay it" - and what the cash could not cover is
     * simply not paid, and counted (getTransferShort()).
     *
     * OVER THE DEFAULT (0.7.48, C1) the dial spends the fund: last month's
     * toRaise is paid first, from its cash - what the step sold came in as
     * cash - and comes off the shorts it was counted in and into the paid
     * totals; then the month's due at the dial; and what the cash could not
     * cover is this month's toRaise, which the step sells the market book for
     * (Exchange's and BondMarket's postFund()). What toRaise cannot pay next
     * month is dropped: it was counted short already, so nothing owed piles
     * up. At or under the default toRaise is always 0 and the first branch
     * never runs, so the arithmetic is 0.7.47's to the bit.
     */
    double payTransfer(double value, int year) {
        if (year != transferYear) {
            transferYear = year;
            transfersThisYear = 0;
            transferShortThisYear = 0;
        }
        double late = 0;
        if (toRaise > 0) {
            late = Math.max(0, Math.min(toRaise, cash));
            cash -= late;
            transfersShort -= late;
            // ...a year's short that began with this month has none of it: the short was the year before's.
            transferShortThisYear = Math.max(0, transferShortThisYear - late);
            transfersThisYear += late;
            transfersPaid += late;
            toRaise = 0;
        }
        transferPaidLate = late;
        transferDue = withdrawalOn(value);
        transferPaid = Math.max(0, Math.min(transferDue, cash));
        cash -= transferPaid;
        transfersThisYear += transferPaid;
        transferShortThisYear += transferDue - transferPaid;
        transfersPaid += transferPaid;
        transfersShort += transferDue - transferPaid;
        if (sellsToPay()) toRaise = transferDue - transferPaid;
        return late > 0 ? transferPaid + late : transferPaid;
    }

    /** This month's transfer as due, and as paid. */
    public double getTransferDue()          { return transferDue; }
    public double getTransferPaid()         { return transferPaid; }
    /** ...and what the cash could not cover of it this month. */
    public double getTransferShort()        { return Math.max(0, transferDue - transferPaid); }
    /** This calendar year's transfers, paid and unpaid. */
    public double getTransfersThisYear()    { return transfersThisYear; }
    public double getTransferShortThisYear(){ return transferShortThisYear; }

    /* ============================== what it receives ============================== */

    /** A dividend on its shares, split between its books by what each holds. */
    void receiveDividend(double amount, double rescueShare) {
        if (!(amount > 0)) return;
        cash += amount;
        monthDividends += amount;
        double rescue = amount * Math.max(0, Math.min(1, rescueShare));
        dividendsRescue += rescue;
        dividendsMarket += amount - rescue;
    }

    /** A coupon on its bonds. */
    void receiveCoupon(double amount)     { if (amount > 0) { cash += amount; coupons += amount; monthCoupons += amount; } }

    /** A bond's principal at its maturity. */
    void receivePrincipal(double amount)  { if (amount > 0) { cash += amount; principal += amount; monthPrincipal += amount; } }

    /** Face a default took off its bonds: a loss, no cash. */
    void noteBondLoss(double face)        { if (face > 0) bondFaceLost += face; }

    /** A preferred dividend the bank paid it. */
    void receivePreferredDividend(double amount) {
        if (amount > 0) { cash += amount; preferredDividends += amount; monthDividends += amount; }
    }

    /** The bank redeemed preferred at par, and bought warrants back at their value. */
    void receiveRedemption(double par, double warrants) {
        if (par > 0) { cash += par; preferredRedeemed += par; }
        if (warrants > 0) { cash += warrants; warrantsBoughtBack += warrants; }
    }

    /** The treasury bought preferred for the rescue book: its cost, for the record (the treasury paid). */
    void notePreferredBought(double par)  { if (par > 0) preferredBought += par; }

    /** Warrants exercised at their expiry: new shares into the rescue book, for nothing. */
    void noteWarrantShares(double n)      { if (n > 0) warrantSharesTaken += n; }

    /** A trade on the book: shares or a bond bought or sold, and how much of a sale came out of the rescue book. */
    void noteBought(double money, boolean bond) {
        if (!(money > 0)) return;
        cash -= money;
        monthBought += money;
        if (bond) bondsBought += money; else sharesBought += money;
    }
    void noteSold(double money, boolean bond) {
        if (!(money > 0)) return;
        cash += money;
        monthSold += money;
        if (bond) bondsSold += money; else sharesSold += money;
    }

    /** The hand sold rescue-book shares: their cost comes off the book's cost in proportion, and the sale is counted. */
    void noteRescueSold(double sharesSoldFromRescue, double rescueHeldBefore, double money) {
        if (!(sharesSoldFromRescue > 0) || !(rescueHeldBefore > 0)) return;
        rescueCost *= Math.max(0, 1 - sharesSoldFromRescue / rescueHeldBefore);
        rescueSold += money;
    }

    /** Clears the month's working. The top of the month. */
    void startMonth() {
        transferDue = transferPaid = transferPaidLate = 0;
        monthDividends = monthCoupons = monthPrincipal = monthBought = monthSold = 0;
    }

    /* ============================== the rescue ============================== */

    /** A resolution happened: the shares are the rescue book's, at what the city paid. */
    void noteResolution(Resolution r) {
        resolutions.add(r);
        rescueCost += r.paid;
    }

    /** Builds a resolution's record; Game fills it. */
    static Resolution newResolution(int month) {
        Resolution r = new Resolution();
        r.month = month;
        return r;
    }

    /** Every resolution the city has made, oldest first. */
    public List<Resolution> getResolutions()   { return java.util.Collections.unmodifiableList(resolutions); }

    /** What the city paid for the rescue book's shares it still holds. */
    public double getRescueCost()              { return rescueCost; }

    /* ============================== the preferred offer ============================== */

    /** True while the bank's offer waits for the player's answer. Survives a save. */
    public boolean isOfferPending()  { return offerPending; }
    /** The month it was made, or -1. */
    public int getOfferMonth()       { return offerMonth; }
    /** The month it was last declined, or -1. */
    public int getDeclinedMonth()    { return declinedMonth; }
    /** The month one was last accepted, or -1. */
    public int getAcceptedMonth()    { return acceptedMonth; }

    /** Whether the bank may ask again this month: nothing pending, and a quarter since the city last answered, either way. */
    public boolean mayOffer(int month) {
        int answered = Math.max(declinedMonth, acceptedMonth);
        return !offerPending && (answered < 0 || month - answered >= OFFER_AGAIN_MONTHS);
    }

    void noteOffered(int month)   { offerPending = true; offerMonth = month; offersMade++; }
    void noteAccepted(int month, double par) {
        offerPending = false; acceptedMonth = month; offersAccepted++; notePreferredBought(par);
    }
    void noteDeclined(int month)  { offerPending = false; declinedMonth = month; offersDeclined++; }
    /** The bank is back over its minimum on its own: the offer lapses, answered by nobody. */
    void noteLapsed()             { offerPending = false; }

    public int getOffersMade()     { return offersMade; }
    public int getOffersAccepted() { return offersAccepted; }
    public int getOffersDeclined() { return offersDeclined; }

    /* ============================== the hand ============================== */

    /** Queues one of the player's orders for the next step. */
    void queue(HandOrder order) { if (order != null && order.amount > 0) hand.add(order); }

    /** The orders waiting for the next step, oldest first. */
    public List<HandOrder> getHandOrders() { return java.util.Collections.unmodifiableList(hand); }

    /** The orders posted at the last step and resting until the next, oldest first (0.7.39). */
    public List<HandOrder> getPosted() { return java.util.Collections.unmodifiableList(posted); }

    /** Cancels the order waiting at this place, before the step posts it (0.7.39). @return it, or null */
    HandOrder cancel(int i) {
        if (i < 0 || i >= hand.size()) return null;
        return hand.remove(i);
    }

    /**
     * WHAT THE HAND'S BUYS HOLD OF THE FUND'S CASH (0.7.39; the spec's B9): a
     * waiting order's whole amount, and a posted one's units still to fill at
     * its price. The rule settles its bids with what is left (the capacity
     * Exchange's and BondMarket's settles give it), so an order the player
     * placed is not starved by the rule, which posts first; the rule still
     * reads its mix on the whole of the cash. Nothing with no order.
     */
    public double handReserve() {
        double t = 0;
        for (HandOrder o : hand) t += o.reserved();
        for (HandOrder o : posted) t += o.reserved();
        return t;
    }

    /** The fund's cash the rule may spend: its cash less the hand's reserve, never under nothing. */
    double cashForTheRule() {
        double reserve = handReserve();
        double cash = Math.max(0, this.cash);
        return reserve > 0 ? Math.max(0, cash - reserve) : cash;
    }

    /** A hand order posted at the step, `units` at `price` a unit: it rests until the next step, and the ledger opens its row. */
    void notePosted(HandOrder o, String key, double units, double price, int month) {
        o.postedMonth = month;
        o.units = Math.max(0, units);
        o.price = price;
        o.filled = 0;
        o.spent = 0;
        if (o.units > FundLedger.DUST) posted.add(o);
        ledger.handPosted(key, o.buy, o.units, price, month);
    }

    /** A hand order that could not be posted, and why: the ledger's row. */
    void noteDropped(HandOrder o, String key, double units, double price, String why, int month) {
        ledger.handDropped(key, o.buy, units, price, why, month);
    }

    /** One of the hand's fills, on a company's book or a bond's: the posted orders on it, oldest first, take it. */
    void handFilled(boolean bond, int which, boolean buy, double q, double cash) {
        double left = q;
        HandOrder last = null;
        for (HandOrder o : posted) {
            if (o.bond != bond || o.buy != buy || (bond ? o.bondId : o.company) != which) continue;
            last = o;
            double room = Math.max(0, o.units - o.filled);
            if (!(room > 0) || !(left > 0)) continue;
            double take = Math.min(room, left);
            o.filled += take;
            o.spent += cash * take / q;
            left -= take;
        }
        if (left > FundLedger.DUST && last != null) { last.filled += left; last.spent += cash * left / q; }
    }

    /** A company's shares split by k (a consolidation under one): the player's orders in them, waiting or on the book, count in the new shares and price in them; the ledger's row (0.7.39). */
    void noteSplit(int company, double k, int month) {
        if (!(k > 0) || k == 1) return;
        for (HandOrder o : hand) {
            if (o.bond || o.company != company) continue;
            if (!o.buy) o.amount *= k;
            if (o.limit > 0) o.limit /= k;
        }
        for (HandOrder o : posted) {
            if (o.bond || o.company != company) continue;
            if (!o.buy) o.amount *= k;
            if (o.limit > 0) o.limit /= k;
            o.units *= k;
            o.filled *= k;
            o.price /= k;
        }
        ledger.split(Equity.COMPANIES[company], k, month);
    }

    /** The step withdrew every order on one market: the hand's posted there close, and what they did not fill lapses. */
    void closePosted(boolean bond) {
        java.util.Iterator<HandOrder> it = posted.iterator();
        while (it.hasNext()) {
            HandOrder o = it.next();
            if (o.bond != bond) continue;
            it.remove();
            ledger.handClosed(o.bond ? FundLedger.bondKey(o.bondId)
                    : FundLedger.shareKey(Equity.COMPANIES[Math.max(0, Math.min(Equity.COMPANIES.length - 1, o.company))]), o.buy);
        }
    }

    /** ...taken by the market that posts them: the share orders, or the bond orders. */
    List<HandOrder> takeHandOrders(boolean bonds) {
        List<HandOrder> out = new ArrayList<>();
        java.util.Iterator<HandOrder> it = hand.iterator();
        while (it.hasNext()) {
            HandOrder o = it.next();
            if (o.bond == bonds) { out.add(o); it.remove(); }
        }
        return out;
    }

    /* ============================== reading ============================== */

    public int getLastPayInMonth()            { return lastPayInMonth; }
    public double getLastPayInYearSurplus()   { return lastPayInYearSurplus; }
    public double getLastPayInFromSurplus()   { return lastPayInFromSurplus; }
    public double getLastPayInFromCash()      { return lastPayInFromCash; }
    public double getPaidInFromSurplus()      { return paidInFromSurplus; }
    public double getPaidInFromCash()         { return paidInFromCash; }
    public double getHandPaidIn()             { return handPaidIn; }
    public double getHandDrawnOut()           { return handDrawnOut; }
    public double getTransfersPaid()          { return transfersPaid; }
    public double getTransfersShort()         { return transfersShort; }
    public double getDividendsMarket()        { return dividendsMarket; }
    public double getDividendsRescue()        { return dividendsRescue; }
    public double getCoupons()                { return coupons; }
    public double getPrincipal()              { return principal; }
    public double getBondFaceLost()           { return bondFaceLost; }
    public double getPreferredBought()        { return preferredBought; }
    public double getPreferredDividends()     { return preferredDividends; }
    public double getPreferredRedeemed()      { return preferredRedeemed; }
    public double getWarrantsBoughtBack()     { return warrantsBoughtBack; }
    public double getWarrantSharesTaken()     { return warrantSharesTaken; }
    public double getSharesBought()           { return sharesBought; }
    public double getSharesSold()             { return sharesSold; }
    public double getBondsBought()            { return bondsBought; }
    public double getBondsSold()              { return bondsSold; }
    public double getRescueSold()             { return rescueSold; }
    public double getMonthDividends()         { return monthDividends; }
    public double getMonthCoupons()           { return monthCoupons; }
    public double getMonthPrincipal()         { return monthPrincipal; }
    public double getMonthBought()            { return monthBought; }
    public double getMonthSold()              { return monthSold; }

    /** What every resolution paid for the rescue book, over the fund's life (0.7.39). */
    public double getRescuesPaid() {
        double t = 0;
        for (Resolution r : resolutions) t += r.paid;
        return t;
    }

    /**
     * WHAT THE CITY HAS PUT INTO ITS FUND, over its life (0.7.39): the dial's
     * pay-ins and the hand's, what the rescues paid, and the preferred the
     * treasury bought - each money the city's own that became the fund's.
     */
    public double getPutIn() {
        return paidInFromSurplus + paidInFromCash + handPaidIn + getRescuesPaid() + preferredBought;
    }

    /** ...AND WHAT IT HAS TAKEN OUT: the transfers paid to the budget and the hand's draw-outs. The fund's gain since it began is its value plus this less what was put in - exact on any save, from these counters. */
    public double getTakenOut() {
        return transfersPaid + handDrawnOut;
    }

    /** True when it holds nothing and has never been asked for anything: a fund that has not begun. */
    public boolean isEmpty() {
        return cash == 0 && resolutions.isEmpty() && hand.isEmpty() && posted.isEmpty() && preferredBought == 0
                && paidInFromSurplus == 0 && paidInFromCash == 0 && handPaidIn == 0;
    }

    /* ============================== the warrants' value ============================== */

    /**
     * WHAT A CALL IS WORTH, Black and Scholes (1973): S N(d1) - K e^(-rT)
     * N(d2), d1 = (ln(S/K) + (r + sigma^2/2) T) / (sigma sqrt T), d2 = d1 -
     * sigma sqrt T. With no time or no volatility, its limit: what it would
     * be worth exercised against the strike discounted to today, never
     * below nothing.
     *
     * @param price the share today
     * @param strike the exercise price
     * @param rate the risk-free rate, a year: the policy rate
     * @param sigma the share's volatility, a year
     * @param years the term left
     */
    public static double callValue(double price, double strike, double rate, double sigma, double years) {
        if (!(price > 0)) return 0;
        if (!(strike > 0)) return price;
        double discount = Math.exp(-Math.max(-1, rate) * Math.max(0, years));
        if (!(years > 0) || !(sigma > 0)) return Math.max(0, price - strike * discount);
        double root = sigma * Math.sqrt(years);
        double d1 = (Math.log(price / strike) + (rate + sigma * sigma / 2) * years) / root;
        double d2 = d1 - root;
        return Math.max(0, price * normalCdf(d1) - strike * discount * normalCdf(d2));
    }

    /**
     * The standard normal distribution function: Zelen and Severo's
     * approximation, Abramowitz and Stegun (1964) 26.2.17, to within 7.5e-8.
     */
    public static double normalCdf(double x) {
        if (Double.isNaN(x)) return .5;
        if (x > 8) return 1;
        if (x < -8) return 0;
        double t = 1 / (1 + .2316419 * Math.abs(x));
        double poly = t * (.319381530 + t * (-.356563782 + t * (1.781477937 + t * (-1.821255978 + t * 1.330274429))));
        double upper = Math.exp(-x * x / 2) / Math.sqrt(2 * Math.PI) * poly;
        return x >= 0 ? 1 - upper : upper;
    }

    /**
     * A share's volatility, a year, from its monthly prices: the standard
     * deviation of the last VOLATILITY_MONTHS monthly log returns, times the
     * square root of twelve. NaN readings and months with no price are
     * skipped; fewer than two returns is no reading, and nothing.
     */
    public static double annualVolatility(double[] prices) {
        if (prices == null) return 0;
        List<Double> returns = new ArrayList<>();
        int from = Math.max(1, prices.length - VOLATILITY_MONTHS);
        for (int i = from; i < prices.length; i++) {
            double a = prices[i - 1], b = prices[i];
            if (a > 0 && b > 0 && Double.isFinite(a) && Double.isFinite(b)) returns.add(Math.log(b / a));
        }
        if (returns.size() < 2) return 0;
        double mean = 0;
        for (double r : returns) mean += r;
        mean /= returns.size();
        double var = 0;
        for (double r : returns) var += (r - mean) * (r - mean);
        var /= returns.size() - 1;
        return Math.sqrt(var) * Math.sqrt(YEAR_MONTHS);
    }

    /* ============================== save and load ============================== */

    /** Everything the fund carries from one month to the next, by name. */
    public static final class State {
        double cash, dial;
        /** The withdrawal dial's steps (0.7.48, C1): null in an older save, which reads Norway's rule. */
        Integer withdrawalSteps;
        String rescue;
        boolean offerPending;
        int offerMonth, declinedMonth, acceptedMonth, offersMade, offersAccepted, offersDeclined;
        double rescueCost;
        List<Resolution> resolutions;
        List<HandOrder> hand;
        /** The hand's orders resting on the books (0.7.39). */
        List<HandOrder> posted;
        /** What each holding cost, and the record (0.7.39): absent from an older save, which Game seeds. */
        FundLedger ledger;
        int lastPayInMonth, transferYear;
        double lastPayInYearSurplus, lastPayInFromSurplus, lastPayInFromCash, transfersThisYear, transferShortThisYear;
        double[] life;
        /** The month just closed: its transfer due and paid, and what came in and went out; slots 7 and 8 (0.7.48) what it sells for to pay next month, and what this month paid late. */
        double[] month;
    }

    public State toState() {
        State s = new State();
        s.cash = cash;
        s.dial = dial;
        s.withdrawalSteps = withdrawalSteps;
        s.rescue = rescue.name();
        s.offerPending = offerPending;
        s.offerMonth = offerMonth;
        s.declinedMonth = declinedMonth;
        s.acceptedMonth = acceptedMonth;
        s.offersMade = offersMade;
        s.offersAccepted = offersAccepted;
        s.offersDeclined = offersDeclined;
        s.rescueCost = rescueCost;
        s.resolutions = new ArrayList<>(resolutions);
        s.hand = new ArrayList<>(hand);
        s.posted = new ArrayList<>(posted);
        s.ledger = ledger.copy();
        s.lastPayInMonth = lastPayInMonth;
        s.lastPayInYearSurplus = lastPayInYearSurplus;
        s.lastPayInFromSurplus = lastPayInFromSurplus;
        s.lastPayInFromCash = lastPayInFromCash;
        s.transferYear = transferYear;
        s.transfersThisYear = transfersThisYear;
        s.transferShortThisYear = transferShortThisYear;
        s.life = new double[] { paidInFromSurplus, paidInFromCash, handPaidIn, handDrawnOut,
                transfersPaid, transfersShort, dividendsMarket, dividendsRescue, coupons, principal, bondFaceLost,
                preferredBought, preferredDividends, preferredRedeemed, warrantsBoughtBack, warrantSharesTaken,
                sharesBought, sharesSold, bondsBought, bondsSold, rescueSold };
        s.month = new double[] { transferDue, transferPaid, monthDividends, monthCoupons, monthPrincipal,
                monthBought, monthSold, toRaise, transferPaidLate };
        return s;
    }

    /**
     * Puts a saved fund back. A save from before 0.7.14 has none, and loads
     * as a fund that has not begun: empty, the dial at 0, the rescue on the
     * button - how that city was played.
     */
    public void restore(State s) {
        reset();
        // ...and with it no ledger: seeded at the end of the load, from nothing held (0.7.39).
        if (s == null) { ledgerToSeed = true; return; }
        cash = Double.isFinite(s.cash) ? s.cash : 0;
        setDial(s.dial);
        withdrawalSteps = s.withdrawalSteps == null ? DEFAULT_WITHDRAWAL_STEPS
                : Math.max(0, Math.min(MAX_WITHDRAWAL_STEPS, s.withdrawalSteps));
        RescueMode mode = RescueMode.BUTTON;
        if (s.rescue != null) for (RescueMode m : RescueMode.values()) if (m.name().equals(s.rescue)) mode = m;
        rescue = mode;
        offerPending = s.offerPending;
        offerMonth = s.offerMonth;
        declinedMonth = s.declinedMonth;
        acceptedMonth = s.acceptedMonth;
        offersMade = s.offersMade;
        offersAccepted = s.offersAccepted;
        offersDeclined = s.offersDeclined;
        rescueCost = s.rescueCost;
        if (s.resolutions != null) for (Resolution r : s.resolutions) if (r != null) resolutions.add(r);
        if (s.hand != null) for (HandOrder o : s.hand) if (o != null) hand.add(o);
        if (s.posted != null) for (HandOrder o : s.posted) if (o != null) posted.add(o);
        // An older save has no ledger: Game seeds it at the end of the load, when every price is back.
        if (s.ledger != null) ledger = s.ledger.copy();
        else ledgerToSeed = true;
        lastPayInMonth = s.lastPayInMonth;
        lastPayInYearSurplus = s.lastPayInYearSurplus;
        lastPayInFromSurplus = s.lastPayInFromSurplus;
        lastPayInFromCash = s.lastPayInFromCash;
        transferYear = s.transferYear;
        transfersThisYear = s.transfersThisYear;
        transferShortThisYear = s.transferShortThisYear;
        double[] l = s.life;
        if (l != null && l.length >= 21) {
            int i = 0;
            paidInFromSurplus = l[i++]; paidInFromCash = l[i++]; handPaidIn = l[i++]; handDrawnOut = l[i++];
            transfersPaid = l[i++]; transfersShort = l[i++]; dividendsMarket = l[i++]; dividendsRescue = l[i++];
            coupons = l[i++]; principal = l[i++]; bondFaceLost = l[i++];
            preferredBought = l[i++]; preferredDividends = l[i++]; preferredRedeemed = l[i++];
            warrantsBoughtBack = l[i++]; warrantSharesTaken = l[i++];
            sharesBought = l[i++]; sharesSold = l[i++]; bondsBought = l[i++]; bondsSold = l[i++]; rescueSold = l[i];
        }
        double[] m = s.month;
        if (m != null && m.length >= 7) {
            transferDue = m[0]; transferPaid = m[1]; monthDividends = m[2]; monthCoupons = m[3];
            monthPrincipal = m[4]; monthBought = m[5]; monthSold = m[6];
        }
        // ...what it sells for to pay next month, and what this month paid late (0.7.48): 0 in an older save.
        if (m != null && m.length >= 9) {
            toRaise = Double.isFinite(m[7]) ? Math.max(0, m[7]) : 0;
            transferPaidLate = Double.isFinite(m[8]) ? m[8] : 0;
        }
    }

    /** A fund that has not begun: nothing held, the dial at 0, the withdrawal at Norway's rule, the rescue on the button. Game.newGame() then sets the rescue automatic. */
    public void reset() {
        cash = 0;
        dial = 0;
        withdrawalSteps = DEFAULT_WITHDRAWAL_STEPS;
        toRaise = 0;
        rescue = RescueMode.BUTTON;
        offerPending = false;
        offerMonth = declinedMonth = acceptedMonth = -1;
        offersMade = offersAccepted = offersDeclined = 0;
        rescueCost = 0;
        resolutions.clear();
        hand.clear();
        posted.clear();
        ledger = new FundLedger();
        ledgerToSeed = false;
        lastPayInMonth = -1;
        lastPayInYearSurplus = lastPayInFromSurplus = lastPayInFromCash = 0;
        transferYear = -1;
        transfersThisYear = transferShortThisYear = 0;
        paidInFromSurplus = paidInFromCash = handPaidIn = handDrawnOut = 0;
        transfersPaid = transfersShort = 0;
        dividendsMarket = dividendsRescue = coupons = principal = bondFaceLost = 0;
        preferredBought = preferredDividends = preferredRedeemed = warrantsBoughtBack = warrantSharesTaken = 0;
        sharesBought = sharesSold = bondsBought = bondsSold = rescueSold = 0;
        startMonth();
    }

    /** Every figure it keeps in money, in the new unit (Game, THE CURRENCY REFORM); share counts and the two dials do not move. */
    public void redenominate(double scale) {
        cash *= scale;
        rescueCost *= scale;
        for (Resolution r : resolutions) {
            r.paid *= scale; r.fromCash *= scale; r.advanced *= scale; r.shortfall *= scale; r.exitCapital *= scale;
            r.householdsValue *= scale; r.worldValue *= scale; r.fundValue *= scale;
            r.preferredCancelled *= scale; r.warrantsCancelled *= scale;
        }
        for (HandOrder o : hand) {
            if (o.buy || o.bond) o.amount *= scale;
            // A share's limit is money a share; a bond's is a price a unit of face (0.7.39).
            if (!o.bond) o.limit *= scale;
        }
        for (HandOrder o : posted) {
            if (o.buy || o.bond) o.amount *= scale;
            if (o.bond) { o.units *= scale; o.filled *= scale; } else { o.limit *= scale; o.price *= scale; }
            o.spent *= scale;
        }
        ledger.redenominate(scale);
        lastPayInYearSurplus *= scale; lastPayInFromSurplus *= scale; lastPayInFromCash *= scale;
        transfersThisYear *= scale; transferShortThisYear *= scale;
        paidInFromSurplus *= scale; paidInFromCash *= scale; handPaidIn *= scale; handDrawnOut *= scale;
        transfersPaid *= scale; transfersShort *= scale;
        dividendsMarket *= scale; dividendsRescue *= scale; coupons *= scale; principal *= scale; bondFaceLost *= scale;
        preferredBought *= scale; preferredDividends *= scale; preferredRedeemed *= scale; warrantsBoughtBack *= scale;
        sharesBought *= scale; sharesSold *= scale; bondsBought *= scale; bondsSold *= scale; rescueSold *= scale;
        transferDue *= scale; transferPaid *= scale; toRaise *= scale; transferPaidLate *= scale;
        monthDividends *= scale; monthCoupons *= scale; monthPrincipal *= scale; monthBought *= scale; monthSold *= scale;
    }
}
