package ham.citybuildersim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The city's fund's cost basis: what each holding cost, by the average-cost method, what its sales and maturities realized, what it has paid in income, and every trade, income, transfer and event the fund saw (0.7.39).
 *
 * WHY. Until 0.7.39 the fund knew what it was worth and, to the cent, every
 * flow that ever went in or out of it (TreasuryFund's lifetime counters), but
 * not what any one holding had cost - only the rescue book's cost was kept
 * (TreasuryFund.getRescueCost()). Nothing recorded what one of the player's
 * orders filled, at what price, or what lapsed: the History flag said "Fund
 * to buy D$4.0M of Construction shares" of an order that bought nothing.
 * Jerus, 2026-10-02: "the city fund should show pnl and acb and all that".
 * The project's spec-fund-0739.md, section 3.
 *
 * THE RULES (Canada's adjusted cost base, by the average-cost method):
 *
 *   A BUY adds its full cost - the units times the price paid; the game
 *   charges no fee - to the lot's ACB. A SALE takes out the ACB times the
 *   units sold over the units held before it, and realizes the proceeds less
 *   that. The units are always read from the model - the register's city
 *   shares, a bond's city face - never kept here, so a split needs nothing:
 *   the ACB stands and the average a unit falls by the factor.
 *
 *   LOTS. One for each company's market book (the rule's buys and the hand's
 *   pooled: they are one register holding, and Canada's identical-property
 *   rule averages them), one for the rescue book (the bank's), one a bond.
 *   The hand's sale takes the market lot first and then the rescue lot, as
 *   the register does (Equity.sellCityByHand()).
 *
 *   INCOME apart: dividends and coupons to the lot's income, never its ACB.
 *
 *   A MATURITY realizes the face less the ACB; A WRITE-DOWN (a default or a
 *   restructure, BondMarket.writeDown()) takes the ACB out in the share the
 *   face lost, as a realized loss with no proceeds; a bond whose face has gone
 *   to dust realizes the rest of its ACB.
 *
 *   THE RESCUE. The fund's market-book bank shares pass to the city for
 *   nothing (Equity.takeAllForCity()): their ACB is realized as a loss. The
 *   rescue lot's ACB is TreasuryFund.getRescueCost() - what the city paid for
 *   the rescue book's shares it still holds - one source, read, not kept here.
 *
 *   The preferred is at par and its warrants cost nothing, so their cost is
 *   the bank's own record (Bank.preferredOutstanding()) and their gains the
 *   fund's counters; they are rows here, not lots (FundView reads them).
 *
 * BOOKKEEPING ONLY. Every hook writes this object and nothing else; nothing
 * the month reads reads it; prices, cash and units are read, never set. So
 * the default playtest's traces cannot move (the spec's 3.6).
 *
 * AN OLDER SAVE has no ledger: Game's load path seeds one once everything is
 * back (Game.seedFundLedger()) - each market lot and each bond at its market
 * value that month, flagged as such, the rescue lot from the counters, which
 * were always exact - and one TRACKING row says so.
 *
 * Saved inside TreasuryFund.State as `ledger`: plain lists Gson writes as they
 * stand, no new save key, SAVE_FORMAT unchanged.
 */
public final class FundLedger {

    /* ============================== the dials ============================== */

    /** The newest activity rows kept: the played research cities made 0-5 a month, and 37 in the busiest month after a pay-in, so this is some fifteen years of a quiet fund and two of a busy one. Older rows are dropped, and counted. */
    public static final int ACTIVITY_ROWS = 1000;

    /** Anything this small, in units or money, is the arithmetic's dust: a lot holding less is closed, a fill this small is no trade (OrderBook.DUST). */
    public static final double DUST = OrderBook.DUST;

    /** A write-down that takes less than this share of a bond's face is booked on its lot but given no row: a restructure keeping all but a sliver of the face (the 600-month research city's year made 24 such, each under D$1; the default playtest, some thirteen a month) is not an event. A tenth of a percent. */
    public static final double WRITE_DOWN_ROW = 1e-3;

    /** A buy that paid at least this share less than what it bought was worth at the step - a share's fair value, a bond's value - was a bargain, and its lot and its row say so (the spec's 5: the 600-month research city's rule took #142 at 75.4 per 100, worth 102.8). A twentieth: past a bond's spread and a share's tick. */
    public static final double BARGAIN = .05;

    /* ============================== the kinds ============================== */

    /** What an activity row records. */
    public static final String BUY = "BUY", SELL = "SELL", LAPSED = "LAPSED", DIVIDEND = "DIVIDEND", COUPON = "COUPON",
            MATURED = "MATURED", WRITTEN_DOWN = "WRITTEN_DOWN", RESCUE = "RESCUE", PREFERRED = "PREFERRED",
            WARRANTS = "WARRANTS", PAY_IN = "PAY_IN", DRAW_OUT = "DRAW_OUT", TRANSFER = "TRANSFER", SPLIT = "SPLIT",
            TRACKING = "TRACKING";

    /** What a lot is: a company's market book, a rescue book, a bond. */
    public static final String SHARES = "SHARES", RESCUE_BOOK = "RESCUE", BOND = "BOND";

    /** A lot's key: by the register's name, never its index. */
    public static String shareKey(String company)  { return "S:" + company; }
    public static String rescueKey(String company) { return "S:" + company + ":rescue"; }
    public static String bondKey(int id)           { return "B:" + id; }
    /** The preferred and the warrants, which are rows and not lots. */
    public static final String PREFERRED_KEY = "P:Bank", WARRANTS_KEY = "W:Bank";

    /* ============================== a lot ============================== */

    /**
     * One holding's cost: its key, what it is (and for a bond its issuer,
     * coupon and maturity, kept so a closed lot still says what it was); its
     * ACB now; over its life the realized gain, the proceeds and the ACB taken
     * out, so the realized share is realized over ACB out; what it was bought
     * for and the income it paid since `since`; whether its cost was counted
     * at market value then (`seeded`), and the dividends to the city before
     * that; and the month it closed, or -1 while it is held. Since 0.7.39's
     * fun (the spec's 5): what its buys were worth at the step's value, so a
     * bargain says so, and the income it was paid in the last month it was
     * paid any, so a month landing can say "paid D$640k".
     */
    public static final class Lot {
        String key, kind, name, issuer;
        int bondId = -1;
        double coupon;
        int maturity = -1;
        double acb, realized, proceeds, acbOut, bought, income, incomeBefore;
        int since = -1;
        boolean seeded;
        int closedMonth = -1;
        double boughtValue, lastIncome;
        int lastIncomeMonth = -1;

        Lot() { }

        Lot copy() {
            Lot l = new Lot();
            l.key = key; l.kind = kind; l.name = name; l.issuer = issuer; l.bondId = bondId; l.coupon = coupon;
            l.maturity = maturity; l.acb = acb; l.realized = realized; l.proceeds = proceeds; l.acbOut = acbOut;
            l.bought = bought; l.income = income; l.incomeBefore = incomeBefore; l.since = since; l.seeded = seeded;
            l.closedMonth = closedMonth;
            l.boughtValue = boughtValue; l.lastIncome = lastIncome; l.lastIncomeMonth = lastIncomeMonth;
            return l;
        }

        public String key()          { return key; }
        public String kind()         { return kind; }
        /** The company's name, or "#142 Automotive" for a bond. */
        public String name()         { return name; }
        public String issuer()       { return issuer; }
        public int bondId()          { return bondId; }
        public double coupon()       { return coupon; }
        public int maturity()        { return maturity; }
        /** What the units held now cost; the rescue lot's is TreasuryFund.getRescueCost(), not this. */
        public double acb()          { return acb; }
        public double realized()     { return realized; }
        public double proceeds()     { return proceeds; }
        public double acbOut()       { return acbOut; }
        public double bought()       { return bought; }
        public double income()       { return income; }
        public double incomeBefore() { return incomeBefore; }
        public int since()           { return since; }
        public boolean seeded()      { return seeded; }
        public int closedMonth()     { return closedMonth; }
        public boolean isClosed()    { return closedMonth >= 0; }
        public boolean isBond()      { return BOND.equals(kind); }
        public boolean isRescue()    { return RESCUE_BOOK.equals(kind); }
        /** What its buys were worth at the step's value when it made them (a share's fair value, a bond's value), against `bought`, what they cost. */
        public double boughtValue()  { return boughtValue; }
        /** How far under that value it bought, as a share of it: 0.26 for 74 paid for 100 worth; NaN with nothing bought since tracking began. */
        public double underValue()   { return boughtValue > DUST && bought > DUST ? 1 - bought / boughtValue : Double.NaN; }
        /** The income it was paid in the last month it was paid any, and that month (-1: none yet). */
        public double lastIncome()   { return lastIncome; }
        public int lastIncomeMonth() { return lastIncomeMonth; }
    }

    /* ============================== a row ============================== */

    /**
     * One line of what the fund did or had done to it: the month, the kind,
     * the lot's key (null for the fund as a whole), whether it was the
     * player's hand, the units (shares, face, or a count for an aggregate
     * row), the money, what it realized, and a note. The rule's fills are one
     * row a month, lot and side; a hand order is one row from the step it is
     * posted at to the step that withdraws it - what it asked for, at what
     * price, what it filled and what lapsed - open while it rests.
     */
    public static final class Activity {
        int month;
        String kind, key;
        boolean hand;
        double units, money, realized;
        String note;
        boolean buy, open;
        double asked, limit, lapsed;
        double value;

        Activity() { }

        Activity copy() {
            Activity a = new Activity();
            a.month = month; a.kind = kind; a.key = key; a.hand = hand; a.units = units; a.money = money;
            a.realized = realized; a.note = note; a.buy = buy; a.open = open; a.asked = asked; a.limit = limit;
            a.lapsed = lapsed; a.value = value;
            return a;
        }

        public int month()        { return month; }
        public String kind()      { return kind; }
        /** The lot's key, or null for the fund as a whole. */
        public String key()       { return key; }
        /** True for the player's hand; false for the rule and for the model. */
        public boolean hand()     { return hand; }
        /** Shares or face traded; for an aggregate row (dividends, coupons) how many lots it covers. */
        public double units()     { return units; }
        public double money()     { return money; }
        public double realized()  { return realized; }
        public String note()      { return note; }
        /** A trade's side. */
        public boolean buy()      { return buy; }
        /** A hand order still resting on its book. */
        public boolean open()     { return open; }
        /** A hand order's units asked for and its price a unit. */
        public double asked()     { return asked; }
        public double limit()     { return limit; }
        /** ...and what of it lapsed, unfilled, at the step that withdrew it. */
        public double lapsed()    { return lapsed; }
        /** The average price a unit it filled at, or NaN for nothing filled. */
        public double average()   { return units > DUST ? money / units : Double.NaN; }
        /** A buy's units at the step's value when it filled (money); 0 where it was not known. */
        public double value()     { return value; }
        /** How far under that value it paid, as a share of it, or NaN. */
        public double underValue() { return value > DUST && money > DUST ? 1 - money / value : Double.NaN; }
    }

    /* ============================== the state ============================== */

    private List<Lot> lots = new ArrayList<>();
    private List<Activity> activity = new ArrayList<>();
    /** The month the ledger began: -1 for a city founded with it, the load month for an older save it was seeded on. */
    private int trackingSince = -1;
    /** Rows dropped past ACTIVITY_ROWS, over the ledger's life. */
    private int dropped;

    /** Lots by key, rebuilt on demand (never saved). */
    private transient Map<String, Lot> index;

    public FundLedger() { }

    /** A deep copy: what a save writes and a load takes, so the two never share a row. */
    public FundLedger copy() {
        FundLedger f = new FundLedger();
        if (lots != null) for (Lot l : lots) if (l != null) f.lots.add(l.copy());
        if (activity != null) for (Activity a : activity) if (a != null) f.activity.add(a.copy());
        f.trackingSince = trackingSince;
        f.dropped = dropped;
        return f;
    }

    /** Empty, for a new city or a fund reset: tracking from the founding. */
    public void reset() {
        lots.clear();
        activity.clear();
        trackingSince = -1;
        dropped = 0;
        index = null;
    }

    private Map<String, Lot> index() {
        if (lots == null) lots = new ArrayList<>();
        if (activity == null) activity = new ArrayList<>();
        if (index == null || index.size() != lots.size()) {
            index = new HashMap<>();
            for (Lot l : lots) if (l != null && l.key != null) index.put(l.key, l);
        }
        return index;
    }

    /** A lot by its key, or null. */
    public Lot lot(String key) { return key == null ? null : index().get(key); }

    /** Every lot, held and closed, in the order each was opened. */
    public List<Lot> getLots() { index(); return java.util.Collections.unmodifiableList(lots); }

    /** Every row kept, oldest first. */
    public List<Activity> getActivity() { index(); return java.util.Collections.unmodifiableList(activity); }

    /** The month the ledger began: -1 for a city founded with it. */
    public int getTrackingSince() { return trackingSince; }

    /** Rows dropped past ACTIVITY_ROWS. */
    public int getDropped() { return dropped; }

    /** A share lot, opened if new. */
    private Lot shareLot(String company, boolean rescue, int month) {
        String key = rescue ? rescueKey(company) : shareKey(company);
        Lot l = lot(key);
        if (l == null) {
            l = new Lot();
            l.key = key;
            l.kind = rescue ? RESCUE_BOOK : SHARES;
            l.name = company;
            l.since = month;
            lots.add(l);
            index().put(key, l);
        }
        return l;
    }

    /** A bond's lot, opened if new, carrying what the bond is. */
    private Lot bondLot(CorporateBond b, int month) {
        String key = bondKey(b.id());
        Lot l = lot(key);
        if (l == null) {
            l = new Lot();
            l.key = key;
            l.kind = BOND;
            l.bondId = b.id();
            l.issuer = b.issuer();
            l.coupon = b.coupon();
            l.maturity = b.maturityMonth();
            l.name = "#" + b.id() + " " + b.issuer();
            l.since = month;
            lots.add(l);
            index().put(key, l);
        }
        return l;
    }

    /** A lot bought into again after it closed is held again; its record stands. */
    private static void reopen(Lot l, int month) {
        if (l.closedMonth >= 0) {
            l.closedMonth = -1;
            if (!(l.acb > DUST)) l.since = month;
        }
    }

    /* ============================== the rows ============================== */

    private Activity add(int month, String kind, String key, boolean hand) {
        index();
        Activity a = new Activity();
        a.month = month;
        a.kind = kind;
        a.key = key;
        a.hand = hand;
        activity.add(a);
        trim();
        return a;
    }

    /** Drops the oldest rows past ACTIVITY_ROWS, never one still open. */
    private void trim() {
        int i = 0;
        while (activity.size() > ACTIVITY_ROWS && i < activity.size()) {
            if (activity.get(i).open) { i++; continue; }
            activity.remove(i);
            dropped++;
        }
    }

    /** This month's row of a kind for a lot (the rule's or the model's), made if there is none: the rows aggregate a month. */
    private Activity monthRow(int month, String kind, String key, boolean buy) {
        return monthRow(month, kind, key, buy, false);
    }

    /** ...or the hand's, for a fill with no order of its own on record. */
    private Activity monthRow(int month, String kind, String key, boolean buy, boolean hand) {
        for (int i = activity.size() - 1; i >= 0; i--) {
            Activity a = activity.get(i);
            if (a.month != month) { if (a.month < month) break; else continue; }
            if (a.hand == hand && !a.open && a.kind.equals(kind) && java.util.Objects.equals(a.key, key) && a.buy == buy
                    && a.asked == 0) return a;
        }
        Activity a = add(month, kind, key, hand);
        a.buy = buy;
        return a;
    }

    /**
     * The hand's open order for this lot and side that a fill belongs to:
     * the oldest that has not filled what it asked for - the book matches the
     * hand's orders on one name in the order they were posted - or, past them
     * all, the newest; null with none open.
     */
    private Activity openHand(String key, boolean buy) {
        Activity last = null;
        for (Activity a : activity) {
            if (!(a.open && a.hand && a.buy == buy && java.util.Objects.equals(a.key, key))) continue;
            if (a.units < a.asked - DUST) return a;
            last = a;
        }
        return last;
    }

    /** Where a fill is written: the hand's open order for the lot and side if it is the hand's, otherwise this month's row. */
    private Activity fillRow(int month, String key, boolean hand, boolean buy) {
        if (hand) {
            Activity a = openHand(key, buy);
            if (a != null) return a;
            // A hand fill with no order on record (a save from before 0.7.39 with an order resting): the month's hand row.
            return monthRow(month, buy ? BUY : SELL, key, buy, true);
        }
        return monthRow(month, buy ? BUY : SELL, key, buy);
    }

    /* ============================== the hooks ============================== */

    /**
     * The fund bought shares of a company's market book, by its rule or the
     * player's hand: `q` at `price` (Exchange's settle), when the company's
     * fair value a share was `value`.
     */
    void boughtShare(String company, boolean hand, double q, double price, double value, int month) {
        if (!(q > 0) || !(price > 0)) return;
        double cash = q * price;
        Lot l = shareLot(company, false, month);
        reopen(l, month);
        l.acb += cash;
        l.bought += cash;
        Activity a = fillRow(month, l.key, hand, true);
        a.units += q;
        a.money += cash;
        worth(l, a, q, value);
    }

    /** A buy's units at the step's value, on its lot and its row; nothing where the value is not known. */
    private static void worth(Lot l, Activity a, double q, double value) {
        if (!(value > 0) || !Double.isFinite(value)) return;
        l.boughtValue += q * value;
        a.value += q * value;
    }

    /** Income to a lot: its total, and the last month's that paid any. */
    private static void paid(Lot l, double amount, int month) {
        l.income += amount;
        if (l.lastIncomeMonth != month) { l.lastIncomeMonth = month; l.lastIncome = 0; }
        l.lastIncome += amount;
    }

    /**
     * ...sold `q` at `price`: `fromRescue` of them out of the rescue book
     * (the hand's sale takes the market book first), the market and rescue
     * books' units before the sale, and the rescue book's cost before it
     * (TreasuryFund.getRescueCost(), which noteRescueSold() then cuts by the
     * same share). Realizes each lot's proceeds less its own ACB out.
     */
    void soldShare(String company, boolean hand, double q, double price, double fromRescue,
                   double marketBefore, double rescueBefore, double rescueCostBefore, int month) {
        if (!(q > 0) || !(price > 0)) return;
        double fromMarket = Math.max(0, q - Math.max(0, fromRescue));
        double realized = 0;
        Activity a = fillRow(month, shareKey(company), hand, false);
        if (fromMarket > 0) {
            Lot l = shareLot(company, false, month);
            double out = marketBefore > DUST ? l.acb * Math.min(1, fromMarket / marketBefore) : l.acb;
            if (marketBefore - fromMarket <= DUST) out = l.acb;
            double cash = fromMarket * price;
            l.acb -= out;
            l.acbOut += out;
            l.proceeds += cash;
            l.realized += cash - out;
            realized += cash - out;
            if (marketBefore - fromMarket <= DUST) { l.acb = 0; l.closedMonth = month; }
        }
        if (fromRescue > 0) {
            Lot r = shareLot(company, true, month);
            double out = rescueBefore > DUST ? Math.max(0, rescueCostBefore) * Math.min(1, fromRescue / rescueBefore) : 0;
            double cash = fromRescue * price;
            r.acbOut += out;
            r.proceeds += cash;
            r.realized += cash - out;
            realized += cash - out;
            if (rescueBefore - fromRescue <= DUST) r.closedMonth = month;
        }
        a.units += q;
        a.money += q * price;
        a.realized += realized;
    }

    /** The fund bought `q` of a bond's face at `price` a unit of face, when the bond's value a unit was `value`. */
    void boughtBond(CorporateBond b, boolean hand, double q, double price, double value, int month) {
        if (b == null || !(q > 0) || !(price > 0)) return;
        double cash = q * price;
        Lot l = bondLot(b, month);
        reopen(l, month);
        l.acb += cash;
        l.bought += cash;
        Activity a = fillRow(month, l.key, hand, true);
        a.units += q;
        a.money += cash;
        worth(l, a, q, value);
    }

    /** ...sold `q` of its face at `price`, out of `faceBefore`. */
    void soldBond(CorporateBond b, boolean hand, double q, double price, double faceBefore, int month) {
        if (b == null || !(q > 0) || !(price > 0)) return;
        Lot l = bondLot(b, month);
        double out = faceBefore > DUST ? l.acb * Math.min(1, q / faceBefore) : l.acb;
        if (faceBefore - q <= DUST) out = l.acb;
        double cash = q * price;
        l.acb -= out;
        l.acbOut += out;
        l.proceeds += cash;
        l.realized += cash - out;
        if (faceBefore - q <= DUST) { l.acb = 0; l.closedMonth = month; }
        Activity a = fillRow(month, l.key, hand, false);
        a.units += q;
        a.money += cash;
        a.realized += cash - out;
    }

    /** A bond's coupon to the fund, by the bond's id - it may have been repaid since the coupon was struck: income, never cost. */
    void coupon(int bondId, double amount, int month) {
        if (!(amount > 0)) return;
        Lot l = lot(bondKey(bondId));
        if (l != null) paid(l, amount, month);
        Activity a = monthRow(month, COUPON, null, false);
        a.units += 1;
        a.money += amount;
    }

    /** A bond repaid: the face against what it cost. */
    void matured(CorporateBond b, double face, int month) {
        if (b == null) return;
        Lot l = lot(bondKey(b.id()));
        if (l == null && !(face > 0)) return;
        if (l != null && l.isClosed() && !(face > 0)) return;
        if (l == null) l = bondLot(b, month);
        double out = l.acb;
        double paid = Math.max(0, face);
        l.acb = 0;
        l.acbOut += out;
        l.proceeds += paid;
        l.realized += paid - out;
        l.closedMonth = month;
        Activity a = monthRow(month, MATURED, l.key, false);
        a.units += paid;
        a.money += paid;
        a.realized += paid - out;
    }

    /** A default or a restructure took the face from `before` to `after`: that share of the ACB out, a realized loss, no proceeds. */
    void writtenDown(CorporateBond b, double before, double after, int month) {
        if (b == null || !(before > 0)) return;
        Lot l = lot(bondKey(b.id()));
        if (l == null) return;
        double keep = Math.max(0, Math.min(1, after / before));
        double out = l.acb * (1 - keep);
        l.acb -= out;
        l.acbOut += out;
        l.realized -= out;
        if (1 - keep >= WRITE_DOWN_ROW) {
            Activity a = monthRow(month, WRITTEN_DOWN, l.key, false);
            a.units += before - Math.max(0, after);
            a.realized -= out;
        }
        if (after <= DUST) { l.acb = 0; l.closedMonth = month; }
    }

    /** A bond whose face a backstop wrote to dust, taken off the market: what is left of its cost, a realized loss. */
    void gone(CorporateBond b, int month) {
        if (b == null) return;
        Lot l = lot(bondKey(b.id()));
        if (l == null || l.isClosed()) return;
        double out = l.acb;
        l.acb = 0;
        l.acbOut += out;
        l.realized -= out;
        l.closedMonth = month;
        Activity a = monthRow(month, WRITTEN_DOWN, l.key, false);
        a.realized -= out;
        a.note = "gone";
    }

    /** A dividend: income on each book's lot, by the share each holds; one row a month for them all. */
    void dividend(String company, double amount, double rescueShare, int month) {
        if (!(amount > 0)) return;
        double rescue = amount * Math.max(0, Math.min(1, rescueShare));
        if (amount - rescue > 0) paid(shareLot(company, false, month), amount - rescue, month);
        if (rescue > 0) paid(shareLot(company, true, month), rescue, month);
        Activity a = monthRow(month, DIVIDEND, null, false);
        a.units += 1;
        a.money += amount;
    }

    /** A company's shares split (k > 1) or consolidated: the ACB stands, the average a share moves by 1/k. A row. */
    void split(String company, double k, int month) {
        if (!(k > 0) || k == 1) return;
        // A hand order still on the book counts in the new shares, as the book does (OrderBook.split()).
        for (Activity a : activity) {
            if (!a.open || !shareKey(company).equals(a.key)) continue;
            a.asked *= k;
            a.units *= k;
            if (a.limit > 0) a.limit /= k;
        }
        Lot l = lot(shareKey(company)), r = lot(rescueKey(company));
        boolean held = (l != null && !l.isClosed()) || (r != null && !r.isClosed());
        if (!held) return;
        Activity a = add(month, SPLIT, shareKey(company), false);
        a.units = k;
        a.note = k > 1 ? String.format(java.util.Locale.ROOT, "%,.0f for 1", k)
                : String.format(java.util.Locale.ROOT, "1 for %,.0f", 1 / k);
    }

    /**
     * THE RESCUE: before the register takes every share, the fund's
     * market-book shares of the company pass for nothing - their ACB realized
     * as a loss. Then a row with what the city paid; the rescue lot's cost is
     * the fund's own (TreasuryFund.noteResolution()).
     */
    double rescueTakes(String company, int month) {
        Lot l = lot(shareKey(company));
        if (l == null || l.isClosed()) return 0;
        double out = l.acb;
        l.acb = 0;
        l.acbOut += out;
        l.realized -= out;
        l.closedMonth = month;
        return out;
    }

    void rescued(String company, TreasuryFund.Resolution r, double marketAcbLost, int month) {
        Lot rl = shareLot(company, true, month);
        reopen(rl, month);
        Activity a = add(month, RESCUE, rescueKey(company), false);
        a.units = r.shares();
        a.money = r.paid();
        a.realized = -marketAcbLost;
        a.note = r.preferredCancelled() > 0 ? "the preferred cancelled" : null;
    }

    /** The preferred: bought (par), a dividend, redeemed at par, or cancelled at a failure (`par` negative: a realized loss). */
    void preferred(String what, double money, double realized, int month) {
        Activity a = what.equals("dividend") ? monthRow(month, PREFERRED, PREFERRED_KEY, false)
                : add(month, PREFERRED, PREFERRED_KEY, false);
        a.money += money;
        a.realized += realized;
        a.note = what;
    }

    /** The warrants: bought back (`money`, all of it realized, they cost nothing) or exercised into `shares` of the rescue book. */
    void warrants(String what, double money, double shares, int month) {
        Activity a = add(month, WARRANTS, WARRANTS_KEY, false);
        a.money = money;
        a.units = shares;
        a.realized = money;
        a.note = what;
    }

    /** Money in or out of the fund as a whole: a pay-in (by the dial or by hand), a draw-out, the month's transfer. */
    void flow(String kind, double money, boolean hand, int month) {
        if (!(money > 0)) return;
        Activity a = kind.equals(TRANSFER) ? monthRow(month, TRANSFER, null, false) : add(month, kind, null, hand);
        a.money += money;
    }

    /**
     * One of the player's orders posted at the step: a row from now until
     * the step that withdraws it, at `limit` a unit for `units`.
     */
    void handPosted(String key, boolean buy, double units, double limit, int month) {
        Activity a = add(month, buy ? BUY : SELL, key, true);
        a.buy = buy;
        a.open = units > DUST;
        a.asked = Math.max(0, units);
        a.limit = limit;
        if (!a.open) a.note = "nothing to post";
    }

    /** ...one that could not be posted at all: the company was not listed or worth nothing, the bond had gone or was worth nothing, a bond buy too small to post, nothing was held to sell, or no room under the cap. */
    void handDropped(String key, boolean buy, double asked, double limit, String why, int month) {
        Activity a = add(month, LAPSED, key, true);
        a.buy = buy;
        a.asked = Math.max(0, asked);
        a.limit = limit;
        a.lapsed = a.asked;
        a.note = why;
    }

    /** The step withdrew what was left of the hand's orders on a lot and side: each closes, what it did not fill lapsed. */
    void handClosed(String key, boolean buy) {
        for (Activity a : activity) {
            if (!(a.open && a.hand && a.buy == buy && java.util.Objects.equals(a.key, key))) continue;
            a.open = false;
            a.lapsed = Math.max(0, a.asked - a.units);
        }
    }

    /** Every open hand row, closed: an older save, or a fund reset. */
    void closeAllHand() {
        for (Activity a : activity) if (a.open) { a.open = false; a.lapsed = Math.max(0, a.asked - a.units); }
    }

    /* ============================== an older save ============================== */

    /**
     * Seeds the ledger of a save from before it: each market lot and bond at
     * its market value at the load month (`seeded`, `since` that month), with
     * the dividends paid to the city before it; the rescue lot exact from the
     * counters - its realized gain is the hand's rescue sales less the cost
     * they took out (what was paid in rescues less what is left of it). One
     * TRACKING row says what happened. A fund that has not begun seeds nothing.
     */
    void seed(Game g, int month) {
        reset();
        trackingSince = month;
        TreasuryFund f = g.getFund();
        Equity reg = g.getEquity();
        Exchange ex = g.getExchange();
        int held = 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            String name = Equity.COMPANIES[c];
            double market = reg.getCityMarketShares(c), rescue = reg.getCityRescueShares(c);
            double rescueIncome = c == Equity.BANK ? f.getDividendsRescue() : 0;
            if (market > DUST) {
                Lot l = shareLot(name, false, month);
                l.acb = market * ex.price(c);
                l.seeded = true;
                l.incomeBefore = Math.max(0, reg.getLifetimeDividendsCity(c) - rescueIncome);
                held++;
            }
            if (rescue > DUST) {
                Lot r = shareLot(name, true, month);
                double paid = 0;
                int first = month;
                for (TreasuryFund.Resolution res : f.getResolutions()) { paid += res.paid(); first = Math.min(first, res.month()); }
                r.since = f.getResolutions().isEmpty() ? month : first;
                r.income = rescueIncome;
                r.proceeds = f.getRescueSold();
                r.acbOut = Math.max(0, paid - f.getRescueCost());
                r.realized = r.proceeds - r.acbOut;
                held++;
            }
        }
        for (CorporateBond b : g.getBondMarket().getBonds()) {
            if (!(b.city() > DUST)) continue;
            Lot l = bondLot(b, month);
            l.acb = b.city() * g.getBondMarket().modelPrice(b, month);
            l.seeded = true;
            held++;
        }
        Activity a = add(month, TRACKING, null, false);
        a.units = held;
        a.note = held > 0 ? "cost counted at market value from here" : "nothing held";
    }

    /* ============================== a reform ============================== */

    /** Every money figure in the new unit: a lot's cost and record, a row's money, a bond's face and a share's price limit; share counts and a bond's price a unit of face do not move. */
    public void redenominate(double scale) {
        index();
        for (Lot l : lots) {
            l.acb *= scale; l.realized *= scale; l.proceeds *= scale; l.acbOut *= scale;
            l.bought *= scale; l.income *= scale; l.incomeBefore *= scale;
            l.boughtValue *= scale; l.lastIncome *= scale;
        }
        for (Activity a : activity) {
            boolean bond = a.key != null && a.key.startsWith("B:");
            a.money *= scale;
            a.realized *= scale;
            a.value *= scale;
            if (bond && !COUPON.equals(a.kind)) { a.units *= scale; a.asked *= scale; a.lapsed *= scale; }
            if (!bond && (BUY.equals(a.kind) || SELL.equals(a.kind) || LAPSED.equals(a.kind))) a.limit *= scale;
        }
    }

    /* ============================== totals, for the checks ============================== */

    /** What every trade it booked paid, and what every sale and maturity brought in - since tracking began. */
    public double purchases() { double t = 0; for (Lot l : getLots()) t += l.bought; return t; }
    public double proceeds()  { double t = 0; for (Lot l : getLots()) t += l.proceeds; return t; }
    /** The realized gain on every lot. */
    public double realized()  { double t = 0; for (Lot l : getLots()) t += l.realized; return t; }
    /** The ACB of every market and bond lot (the rescue lot's is TreasuryFund.getRescueCost()). */
    public double acbHeld()   { double t = 0; for (Lot l : getLots()) if (!l.isRescue()) t += l.acb; return t; }
}
