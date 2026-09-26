package ham.citybuildersim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A limit-order book for one instrument: buy and sell orders from named
 * participants, each a price and a quantity, matched by price-time priority.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * Jerus, 2026-09-24: "Order book for both" - everyone posts buy and sell
 * orders at prices, orders fill only when they meet, and the rest wait; "its
 * availability to get filled both buy and sell". Until 0.7.12 every
 * secondary market in the city was a dealer: the bank's desk quoted the
 * shares (Exchange) and bought the city's paper back at the curve
 * (HouseholdBalance.PaperDesk), so a seller always found a buyer at the
 * desk's price and nobody ever waited. The corporate bonds of 0.7.12 are the
 * first thing that trades here instead; the shares move onto the same book
 * in round 2, which is why nothing in this class knows what a bond is.
 *
 * ==================== THE RULES ====================
 *
 *   PRICE-TIME PRIORITY. The best price first - the highest bid, the lowest
 *   ask - and at one price the order that arrived first.
 *
 *   A TRADE EXECUTES AT THE RESTING ORDER'S PRICE. An incoming order that
 *   crosses the book takes what rests at the resting order's price, as on any
 *   continuous exchange, and whatever it cannot fill rests at its own limit.
 *
 *   PARTIAL FILLS. An order fills as far as the other side reaches, and the
 *   rest of it stays where it was.
 *
 *   NOBODY IS OBLIGED TO TRADE. A sell with no buyer rests; a buy with no
 *   seller rests. The book never invents the other side.
 *
 *   NO TRADE WITH ONESELF. An incoming order passes over the same
 *   participant's resting orders (self-trade prevention, as every exchange
 *   runs it) and they keep their place.
 *
 *   SETTLEMENT IS THE CALLER'S. The book matches; a Clearing moves the money
 *   and the instrument, and says beforehand how much each side can settle -
 *   a buyer with less cash than its bid, or a seller with less than its ask,
 *   is filled as far as it can pay or deliver and its order trimmed to that,
 *   so a trade the book reports is a trade that happened.
 *
 * WHAT AN ORDER'S LIFE IS is the owner's decision, not the book's: it rests
 * until it fills, is withdrawn (withdraw(), withdrawAll()), or is trimmed by
 * the clearing. The bond market withdraws every order at its monthly step and
 * each participant posts again from its view of that month - see
 * BondMarket, THE ORDERS ARE GOOD FOR A MONTH.
 *
 * Every field is plain data, so the book saves with Gson as it stands.
 */
public class OrderBook {

    /** Which side of the book an order is on. */
    public enum Side { BUY, SELL }

    /** Anything smaller than this is the arithmetic's dust, not a quantity: an order this small is not rested and a fill this small is not a trade. In units of the instrument. */
    public static final double DUST = 1e-9;

    /**
     * ...THIS BOOK'S DUST, IN ITS OWN UNITS (0.7.12 round 6). DUST for a book
     * whose quantities are shares, which a currency reform does not move. A
     * bond's book counts face, which is money, so there it is a MONEY
     * CONSTANT and is seeded like every other (BondMarket.seedConstants()):
     * DUST in founding money, divided by the unit, and moved with the
     * quantities when a reform moves them (redenominate()). Read raw, a
     * billionth of a dollar of face was a trade in the plain city and dust in
     * its reformed twin, and DenominationCheck parted the two. Not saved: the
     * market that owns the book sets it again on load.
     */
    private transient double dust = DUST;

    /** Sets what this book reads as dust: see above. */
    public void setDust(double dust) { if (dust > 0 && Double.isFinite(dust)) this.dust = dust; }

    /** What this book reads as dust. */
    public double getDust() { return dust; }

    /** One order, resting or just submitted. */
    public static final class Order {
        long seq;
        String who;
        Side side;
        double price;
        double quantity;
        double submitted;
        int month;

        Order() { }

        Order(long seq, String who, Side side, double price, double quantity, int month) {
            this.seq = seq;
            this.who = who;
            this.side = side;
            this.price = price;
            this.quantity = quantity;
            this.submitted = quantity;
            this.month = month;
        }

        /** Arrival order: earlier is smaller. */
        public long seq()        { return seq; }
        /** The participant. */
        public String who()      { return who; }
        public Side side()       { return side; }
        /** The limit: the most a buyer pays, the least a seller takes, per unit. */
        public double price()    { return price; }
        /** What is still unfilled. */
        public double quantity() { return quantity; }
        /** What was asked for when it was posted. */
        public double submitted() { return submitted; }
        /** The month it was posted in. */
        public int month()       { return month; }
    }

    /**
     * What settles a trade. The book asks how much each side can settle
     * before it matches, then settles exactly that.
     */
    public interface Clearing {
        /** The most of the instrument this participant can settle now on this side at this price: a buyer's cash over the price, a seller's holding. */
        double capacity(String who, Side side, double price);

        /** Moves the money and the instrument: `quantity` from the seller to the buyer at `price` a unit. Called only within both sides' capacity. */
        void settle(String buyer, String seller, double quantity, double price);
    }

    /** One trade, as the book reports it back to whoever submitted the order. */
    public record Fill(String buyer, String seller, double quantity, double price) { }

    private String instrument;
    /** Resting bids, best first: highest price, then earliest. */
    private final List<Order> bids = new ArrayList<>();
    /** Resting asks, best first: lowest price, then earliest. */
    private final List<Order> asks = new ArrayList<>();
    private long nextSeq;
    /** The last trade's price, 0 before the first (a save carries no NaN). */
    private double lastPrice;
    private int lastTradeMonth = -1;

    /* ---- the month's record: flows, cleared by startMonth() ---- */
    private double postedBuy, postedSell, filledBuy, filledSell;
    private double volume, turnover;
    private int trades, sellsWaited, sellsPosted, buysWaited, buysPosted;
    private double sellQuantityWaited;

    OrderBook() { }

    public OrderBook(String instrument) { this.instrument = instrument; }

    /** What this book trades. */
    public String instrument() { return instrument; }

    /* =========================== submitting =========================== */

    /**
     * Submits an order: it takes what crosses it on the other side, best
     * first, at each resting order's price, and whatever is left rests at
     * its own limit. A non-positive quantity, a non-finite or non-positive
     * price, or a participant with no name does nothing.
     *
     * @return the trades it made, in the order made
     */
    public List<Fill> submit(String who, Side side, double price, double quantity, int month, Clearing clearing) {
        return submit(who, side, price, quantity, month, clearing, true);
    }

    /**
     * ...or, with `rest` false, TAKES what crosses it and rests nothing
     * (immediate-or-cancel): a buyer walking down its list of preferences
     * takes what is on offer at one before trying the next (0.7.12 round 2,
     * Exchange). What it did not fill is not counted as waiting - it never
     * rested - and not as posted either, past what it took.
     */
    public List<Fill> submit(String who, Side side, double price, double quantity, int month, Clearing clearing, boolean rest) {
        List<Fill> fills = new ArrayList<>();
        if (who == null || side == null || !(quantity > dust) || !(price > 0) || !Double.isFinite(price)) return fills;
        Order order = new Order(nextSeq++, who, side, price, quantity, month);
        if (rest) {
            if (side == Side.BUY) { postedBuy += quantity; buysPosted++; }
            else { postedSell += quantity; sellsPosted++; }
        }

        List<Order> other = side == Side.BUY ? asks : bids;
        Iterator<Order> it = other.iterator();
        while (order.quantity > dust && it.hasNext()) {
            Order resting = it.next();
            boolean crosses = side == Side.BUY ? resting.price <= order.price : resting.price >= order.price;
            if (!crosses) break;                        // the rest are worse still
            if (resting.who.equals(order.who)) continue; // no trade with oneself

            double price0 = resting.price;
            String buyer = side == Side.BUY ? order.who : resting.who;
            String seller = side == Side.BUY ? resting.who : order.who;
            double restingCan = clearing == null ? resting.quantity
                    : Math.max(0, clearing.capacity(resting.who, resting.side, price0));
            if (restingCan < resting.quantity) {
                // It cannot settle what it posted: trimmed to what it can,
                // and gone if that is nothing.
                resting.quantity = Math.max(0, restingCan);
                if (resting.quantity <= dust) { it.remove(); continue; }
            }
            double incomingCan = clearing == null ? order.quantity
                    : Math.max(0, clearing.capacity(order.who, order.side, price0));
            if (incomingCan <= dust) {                  // it cannot pay or deliver any more:
                order.quantity = 0;                     // trimmed to what it could, which it has
                break;
            }

            double q = Math.min(order.quantity, Math.min(resting.quantity, incomingCan));
            if (!(q > dust)) break;
            if (clearing != null) clearing.settle(buyer, seller, q, price0);
            order.quantity -= q;
            resting.quantity -= q;
            recordTrade(q, price0, month);
            fills.add(new Fill(buyer, seller, q, price0));
            if (resting.quantity <= dust) it.remove();
        }
        if (!rest) {
            // Taken, not posted: what it took counts as posted, so a fill
            // rate never reads over one.
            double took = 0;
            for (Fill f : fills) took += f.quantity();
            if (took > dust) {
                if (side == Side.BUY) { postedBuy += took; buysPosted++; }
                else { postedSell += took; sellsPosted++; }
            }
            return fills;
        }
        // What rests is what it could settle at its own limit, and no more.
        if (order.quantity > dust && clearing != null) {
            order.quantity = Math.min(order.quantity, Math.max(0, clearing.capacity(order.who, order.side, order.price)));
        }
        if (order.quantity > dust) rest(order);
        return fills;
    }

    private void recordTrade(double q, double price, int month) {
        volume += q;
        turnover += q * price;
        filledBuy += q;
        filledSell += q;
        trades++;
        lastPrice = price;
        lastTradeMonth = month;
    }

    /** Puts an order where its price and arrival say it belongs. */
    private void rest(Order order) {
        List<Order> side = order.side == Side.BUY ? bids : asks;
        int i = 0;
        while (i < side.size()) {
            Order o = side.get(i);
            boolean worse = order.side == Side.BUY ? o.price < order.price : o.price > order.price;
            if (worse || (o.price == order.price && o.seq > order.seq)) break;
            i++;
        }
        side.add(i, order);
    }

    /* =========================== withdrawing =========================== */

    /** Withdraws every order this participant has resting, on both sides. @return how many were withdrawn */
    public int withdraw(String who) {
        int n = 0;
        for (List<Order> side : List.of(bids, asks)) {
            Iterator<Order> it = side.iterator();
            while (it.hasNext()) {
                Order o = it.next();
                if (o.who.equals(who)) { noteWithdrawn(o); it.remove(); n++; }
            }
        }
        return n;
    }

    /** Withdraws every order on the book: the month's close. Counts what waited unfilled. */
    public void withdrawAll() {
        for (Order o : bids) noteWithdrawn(o);
        for (Order o : asks) noteWithdrawn(o);
        bids.clear();
        asks.clear();
    }

    private void noteWithdrawn(Order o) {
        if (o.side == Side.SELL) { sellsWaited++; sellQuantityWaited += o.quantity; }
        else buysWaited++;
    }

    /** Drops every order, uncounted: an instrument that has gone. */
    public void clear() {
        bids.clear();
        asks.clear();
    }

    /** Opens the month's record. The book's orders and its last price stand. */
    public void startMonth() {
        postedBuy = postedSell = filledBuy = filledSell = volume = turnover = 0;
        trades = sellsWaited = sellsPosted = buysWaited = buysPosted = 0;
        sellQuantityWaited = 0;
    }

    /* ============================== reading ============================== */

    /** The resting bids, best first. */
    public List<Order> bids() { return java.util.Collections.unmodifiableList(bids); }
    /** The resting asks, best first. */
    public List<Order> asks() { return java.util.Collections.unmodifiableList(asks); }

    /** The highest bid, or NaN with none. */
    public double bestBid() { return bids.isEmpty() ? Double.NaN : bids.get(0).price; }
    /** The lowest ask, or NaN with none. */
    public double bestAsk() { return asks.isEmpty() ? Double.NaN : asks.get(0).price; }

    /** What is resting at or better than this price on one side: the book's depth there. */
    public double depth(Side side, double price) {
        double total = 0;
        for (Order o : side == Side.BUY ? bids : asks) {
            boolean better = side == Side.BUY ? o.price >= price : o.price <= price;
            if (!better) break;
            total += o.quantity;
        }
        return total;
    }

    /** One price level of the depth: a price and everything resting there. */
    public record Level(double price, double quantity, int orders) { }

    /** The depth of one side, one row per price, best first. */
    public List<Level> levels(Side side) {
        List<Level> out = new ArrayList<>();
        for (Order o : side == Side.BUY ? bids : asks) {
            if (!out.isEmpty() && out.get(out.size() - 1).price() == o.price) {
                Level l = out.remove(out.size() - 1);
                out.add(new Level(l.price(), l.quantity() + o.quantity, l.orders() + 1));
            } else {
                out.add(new Level(o.price, o.quantity, 1));
            }
        }
        return out;
    }

    /** What this participant has resting on one side. */
    public double resting(String who, Side side) {
        double total = 0;
        for (Order o : side == Side.BUY ? bids : asks) if (o.who.equals(who)) total += o.quantity;
        return total;
    }

    /** The price of the last trade, or NaN before the first. */
    public double lastPrice() { return lastPrice > 0 ? lastPrice : Double.NaN; }
    /** The month of the last trade, or -1 before the first. */
    public int lastTradeMonth() { return lastTradeMonth; }

    /** This month: quantity posted to buy and to sell, and what of it filled. */
    public double postedBuy()  { return postedBuy; }
    public double postedSell() { return postedSell; }
    public double filledBuy()  { return filledBuy; }
    public double filledSell() { return filledSell; }
    /** This month's volume in units and its value. */
    public double volume()   { return volume; }
    public double turnover() { return turnover; }
    public int trades()      { return trades; }
    /** Sell orders posted this month, and those withdrawn at the close still unfilled in whole or part - the sellers who waited - with what they still had to sell. */
    public int sellsPosted() { return sellsPosted; }
    public int sellsWaited() { return sellsWaited; }
    public double sellQuantityWaited() { return sellQuantityWaited; }
    public int buysPosted()  { return buysPosted; }
    public int buysWaited()  { return buysWaited; }

    /**
     * A split (k > 1) or a consolidation (k < 1) of the instrument (0.7.12
     * round 2, the shares): every quantity resting and the month's in units
     * times k, every price and the last one over it. What anybody has on
     * offer is worth what it was.
     */
    public void split(double k) {
        if (!(k > 0) || k == 1) return;
        for (Order o : bids) { o.quantity *= k; o.submitted *= k; o.price /= k; }
        for (Order o : asks) { o.quantity *= k; o.submitted *= k; o.price /= k; }
        postedBuy *= k; postedSell *= k; filledBuy *= k; filledSell *= k;
        volume *= k; sellQuantityWaited *= k;
        lastPrice /= k;
    }

    /** Opens a book with a price already on it: a save from before the book, whose dealer's last quote is where the market opens (Exchange.restore()). */
    public void seedLastPrice(double price) {
        if (price > 0 && Double.isFinite(price)) lastPrice = price;
    }

    /**
     * The book in a reformed currency: for an instrument whose unit is money
     * (a bond's face, pricePerUnitOfMoney) every quantity and the month's
     * volumes times the scale and no price; for one whose unit is not (a
     * share) every price and the last one.
     */
    public void redenominate(double scale, boolean pricePerUnitOfMoney) {
        // A bond's price is per unit of face, and the face is money: the units
        // move with the reform and the price per unit does not. A share's
        // price is money a share, and would move (round 2).
        if (pricePerUnitOfMoney) {
            // ...and the dust with them, since it is an amount of face.
            dust *= scale;
            for (Order o : bids) { o.quantity *= scale; o.submitted *= scale; }
            for (Order o : asks) { o.quantity *= scale; o.submitted *= scale; }
            postedBuy *= scale; postedSell *= scale; filledBuy *= scale; filledSell *= scale;
            volume *= scale; sellQuantityWaited *= scale;
            turnover *= scale;
        } else {
            for (Order o : bids) o.price *= scale;
            for (Order o : asks) o.price *= scale;
            lastPrice *= scale;
            turnover *= scale;
        }
    }
}
