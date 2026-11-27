package ham.citybuildersim;

import com.google.gson.Gson;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The limit-order book (0.7.12), on its own: the rules any instrument trades
 * by, proved on a book that knows nothing about what it trades.
 *
 * WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Order book for both" - "its
 * availability to get filled both buy and sell"):
 *
 *   1. PRICE-TIME PRIORITY: the best price first, and at one price the
 *      order that came first.
 *   2. A TRADE IS AT THE RESTING ORDER'S PRICE, whichever side arrives.
 *   3. NOBODY IS OBLIGED TO TRADE: a sell with no buyer rests unfilled, a
 *      buy with no seller rests, and the close counts who waited.
 *   4. PARTIAL FILLS: an order fills as far as the other side reaches and
 *      the rest keeps its place, or rests at its own limit.
 *   5. NO TRADE WITH ONESELF, and the order passed over keeps its place.
 *   6. THE CLEARING'S LIMITS: a buyer that cannot pay for all of it, or a
 *      seller that cannot deliver, fills as far as it can and is trimmed -
 *      so every trade the book reports is one that settled.
 *   7. THE RECORD: posted, filled, volume, turnover, the last price, the
 *      depth by level.
 *   8. A CURRENCY REFORM: quantities move for an instrument counted in
 *      money, prices for one that is not.
 *   9. SAVE AND LOAD: a book with resting orders round-trips through Gson,
 *      and the next order after a reload still arrives after them.
 *  10. FLOATING PRICES: a one-step roundoff crosses and keeps time priority,
 *      in both money units; a gap past the model's allowance never crosses.
 *  12. THE KNOWN ORDER (AP8): a side in known raw price order is halved to
 *      find where a new order rests, and rests it where walking the side from
 *      its best order would - warmed, then after each writer of the side (an
 *      order, a fill, a trim, a cancel, the close, a roundoff out of raw
 *      order, a split, a reform, a load, the autosave's copy), the book to
 *      the bit against a twin that always walks.
 *
 * The bond market's own use of the book - its participants, their orders,
 * and a played month's audit closing through the trades - is BondCheck's,
 * and the shares' since 0.7.12 round 2 ExchangeCheck's.
 * Every fixture causes its condition.
 */
public class OrderBookCheck {

    static int fails = 0;
    static PrintStream out;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-86s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        out.printf("%-86s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    /** A clearing with cash and holdings per participant: each buyer pays out of its cash, each seller delivers out of its holding. */
    static final class Ledger implements OrderBook.Clearing {
        final Map<String, Double> cash = new LinkedHashMap<>(), held = new LinkedHashMap<>();
        int settled;

        Ledger give(String who, double money, double units) {
            cash.put(who, money);
            held.put(who, units);
            return this;
        }

        @Override public double capacity(String who, OrderBook.Side side, double price) {
            return side == OrderBook.Side.BUY ? cash.getOrDefault(who, 0.0) / price : held.getOrDefault(who, 0.0);
        }

        @Override public void settle(String buyer, String seller, double quantity, double price) {
            cash.merge(buyer, -quantity * price, Double::sum);
            cash.merge(seller, quantity * price, Double::sum);
            held.merge(buyer, quantity, Double::sum);
            held.merge(seller, -quantity, Double::sum);
            settled++;
        }
    }

    /** Everybody able to settle anything. */
    static Ledger rich(String... who) {
        Ledger l = new Ledger();
        for (String w : who) l.give(w, 1e6, 1e6);
        return l;
    }

    public static void main(String[] args) {
        out = System.out;
        floatingPrices();
        staggeredBand();
        priceTime();
        restingPrice();
        nobodyObliged();
        partialFills();
        noSelfTrade();
        clearingLimits();
        theRecord();
        currencyReform();
        saveAndLoad();
        knownOrder();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================== 1. PRICE-TIME ============================== */

    static void floatingPrices() {
        out.println("--- 10. floating prices meet at the model's rounding allowance ---");
        for (double unit : new double[] { PriceLevel.FACTOR, 1 }) {
            double price = .15195099627782777 * unit;
            for (OrderBook.Side first : OrderBook.Side.values()) {
                OrderBook book = new OrderBook("rounding");
                Ledger ledger = rich("buyer", "seller");
                double bid = Math.nextDown(price);
                book.submit(first == OrderBook.Side.BUY ? "buyer" : "seller", first,
                        first == OrderBook.Side.BUY ? bid : price, 1, 1, ledger);
                List<OrderBook.Fill> fills = book.submit(first == OrderBook.Side.BUY ? "seller" : "buyer",
                        first == OrderBook.Side.BUY ? OrderBook.Side.SELL : OrderBook.Side.BUY,
                        first == OrderBook.Side.BUY ? price : bid, 1, 1, ledger);
                assertTrue("a one-step price difference crosses with either side resting, unit " + unit + " " + first,
                        fills.size() == 1 && ledger.settled == 1);
                if (!fills.isEmpty()) close("the resting price still settles", fills.get(0).price(),
                        first == OrderBook.Side.BUY ? bid : price, 0);
            }
            double gap = 2 * MoneyAudit.ULP_STEPS * Math.ulp(price);
            OrderBook separated = new OrderBook("separated");
            separated.submit("seller", OrderBook.Side.SELL, price + gap, 1, 1, null);
            assertTrue("a price gap past the model's allowance stays unfilled, unit " + unit,
                    separated.submit("buyer", OrderBook.Side.BUY, price, 1, 1, null).isEmpty());
            OrderBook queue = new OrderBook("time");
            queue.submit("first", OrderBook.Side.BUY, price, 1, 1, null);
            queue.submit("second", OrderBook.Side.BUY, Math.nextUp(price), 1, 1, null);
            assertTrue("a roundoff price improvement keeps arrival priority, unit " + unit,
                    queue.bids().get(0).who().equals("first"));
            close("depth uses the same price comparison", queue.depth(OrderBook.Side.BUY, Math.nextUp(price)), 2, 0);
            assertTrue("the same numerical price is one depth level", queue.levels(OrderBook.Side.BUY).size() == 1);
        }
    }

    static void staggeredBand() {
        out.println("--- 11. a rounding band cannot hide a later crossing order ---");
        double step = Math.ulp(1.0);
        for (OrderBook.Side side : OrderBook.Side.values()) {
            double sign = side == OrderBook.Side.SELL ? 1 : -1;
            OrderBook book = new OrderBook("staggered");
            book.submit("first", side, 1 + sign * .75 * MoneyAudit.ULP_STEPS * step, 1, 1, null);
            book.submit("second", side, 1, 1, 1, null);
            double limit = 1 - sign * .5 * MoneyAudit.ULP_STEPS * step;
            close("depth finds the later crossing order on " + side, book.depth(side, limit), 1, 0);
            List<OrderBook.Fill> fills = book.submit("taker",
                    side == OrderBook.Side.SELL ? OrderBook.Side.BUY : OrderBook.Side.SELL,
                    limit, 1, 1, null, false);
            assertTrue("the later crossing order trades on " + side,
                    fills.size() == 1 && fills.get(0).seller().equals(side == OrderBook.Side.SELL ? "second" : "taker")
                    && fills.get(0).buyer().equals(side == OrderBook.Side.BUY ? "second" : "taker"));
        }
    }

    static void priceTime() {
        out.println("--- 1. price-time priority: the best price first, and at one price the first to come ---");
        Ledger l = rich("A", "B", "C", "D", "S");
        OrderBook book = new OrderBook("x");
        book.submit("A", OrderBook.Side.BUY, 1.00, 5, 0, l);
        book.submit("B", OrderBook.Side.BUY, 1.02, 5, 0, l);
        book.submit("C", OrderBook.Side.BUY, 1.02, 5, 0, l);
        book.submit("D", OrderBook.Side.BUY, 1.01, 5, 0, l);
        List<OrderBook.Order> bids = book.bids();
        assertTrue("fixture: four bids resting, nobody selling", bids.size() == 4 && book.asks().isEmpty());
        assertTrue("the bids stand best first: 1.02 (B), 1.02 (C), 1.01 (D), 1.00 (A)",
                bids.get(0).who().equals("B") && bids.get(1).who().equals("C")
                        && bids.get(2).who().equals("D") && bids.get(3).who().equals("A"));
        List<OrderBook.Fill> fills = book.submit("S", OrderBook.Side.SELL, .95, 12, 0, l);
        assertTrue("a sell of 12 at 0.95 takes B's 5, then C's 5, then 2 of D's - in that order",
                fills.size() == 3 && fills.get(0).buyer().equals("B") && fills.get(1).buyer().equals("C")
                        && fills.get(2).buyer().equals("D"));
        close("...B's first, whole", fills.get(0).quantity(), 5, 0);
        close("...D's last, as far as the sell reached", fills.get(2).quantity(), 2, 1e-12);
        assertTrue("...and A's bid at 1.00, the worst, is untouched", book.resting("A", OrderBook.Side.BUY) == 5);

        OrderBook asks = new OrderBook("y");
        asks.submit("A", OrderBook.Side.SELL, 1.05, 3, 0, l);
        asks.submit("B", OrderBook.Side.SELL, 1.03, 3, 0, l);
        asks.submit("C", OrderBook.Side.SELL, 1.03, 3, 0, l);
        List<OrderBook.Fill> bought = asks.submit("S", OrderBook.Side.BUY, 1.10, 4, 0, l);
        assertTrue("the other way up: a buy takes the lowest ask first, and at one price the earlier (B, then C)",
                bought.size() == 2 && bought.get(0).seller().equals("B") && bought.get(1).seller().equals("C"));
    }

    /* ============================ 2. RESTING PRICE ============================ */

    static void restingPrice() {
        out.println("\n--- 2. a trade is at the resting order's price, whichever side arrives ---");
        Ledger l = rich("A", "B");
        OrderBook book = new OrderBook("x");
        book.submit("A", OrderBook.Side.SELL, 1.05, 10, 0, l);
        List<OrderBook.Fill> f = book.submit("B", OrderBook.Side.BUY, 1.20, 4, 0, l);
        close("a buy at 1.20 meeting a resting ask at 1.05 trades at 1.05", f.get(0).price(), 1.05, 0);
        close("...and the buyer paid 1.05 a unit, not its limit", l.cash.get("B"), 1e6 - 4 * 1.05, 1e-9);
        OrderBook other = new OrderBook("y");
        other.submit("A", OrderBook.Side.BUY, .98, 10, 0, l);
        List<OrderBook.Fill> g = other.submit("B", OrderBook.Side.SELL, .90, 4, 0, l);
        close("a sell at 0.90 meeting a resting bid at 0.98 trades at 0.98", g.get(0).price(), .98, 0);
        close("the last price is that trade's", other.lastPrice(), .98, 0);
    }

    /* =========================== 3. NOBODY OBLIGED =========================== */

    static void nobodyObliged() {
        out.println("\n--- 3. nobody is obliged to trade: an order with nobody across rests, and the close counts who waited ---");
        Ledger l = rich("A", "B", "C");
        OrderBook book = new OrderBook("x");
        List<OrderBook.Fill> f = book.submit("A", OrderBook.Side.SELL, 1.00, 7, 0, l);
        assertTrue("a sell with no buyer makes no trade", f.isEmpty() && l.settled == 0);
        close("...and rests, unfilled", book.resting("A", OrderBook.Side.SELL), 7, 0);
        List<OrderBook.Fill> g = book.submit("B", OrderBook.Side.BUY, .90, 3, 0, l);
        assertTrue("a buy under the ask makes no trade either", g.isEmpty() && l.settled == 0);
        close("...and rests beside it: the book does not close the gap", book.bestBid(), .90, 0);
        close("...the spread stands", book.bestAsk() - book.bestBid(), .10, 1e-12);
        assertTrue("before any trade there is no last price", Double.isNaN(book.lastPrice()));
        book.submit("C", OrderBook.Side.SELL, 1.10, 2, 0, l);
        book.withdrawAll();
        assertTrue("the close withdraws every order", book.bids().isEmpty() && book.asks().isEmpty());
        assertTrue("...and counts both sellers as having waited", book.sellsPosted() == 2 && book.sellsWaited() == 2);
        close("...with what they still had to sell", book.sellQuantityWaited(), 9, 0);
        assertTrue("...and the buyer too", book.buysWaited() == 1);
    }

    /* ============================ 4. PARTIAL FILLS ============================ */

    static void partialFills() {
        out.println("\n--- 4. partial fills ---");
        Ledger l = rich("A", "B", "C", "D");
        OrderBook book = new OrderBook("x");
        book.submit("A", OrderBook.Side.SELL, 1.00, 10, 0, l);
        book.submit("B", OrderBook.Side.SELL, 1.00, 5, 0, l);
        book.submit("C", OrderBook.Side.BUY, 1.00, 4, 0, l);
        close("a buy of 4 against a resting 10 fills 4", l.held.get("C"), 1e6 + 4, 0);
        close("...and the 6 left of it stay", book.resting("A", OrderBook.Side.SELL), 6, 0);
        assertTrue("...at the head of the book, where it was", book.asks().get(0).who().equals("A"));
        book.submit("D", OrderBook.Side.BUY, 1.00, 15, 0, l);
        close("a buy of 15 against what is left, 11, fills 11", l.held.get("D"), 1e6 + 11, 1e-9);
        close("...and rests the other 4 at its own limit", book.resting("D", OrderBook.Side.BUY), 4, 1e-12);
        assertTrue("...on a book with no asks left", book.asks().isEmpty());
        close("the month filled 15 of the 15 posted to sell", book.filledSell(), 15, 1e-12);
        book.withdrawAll();
        assertTrue("no seller waited: both filled whole", book.sellsWaited() == 0);
    }

    /* ============================ 5. NO SELF-TRADE ============================ */

    static void noSelfTrade() {
        out.println("\n--- 5. no trade with oneself ---");
        Ledger l = rich("A", "B");
        OrderBook book = new OrderBook("x");
        book.submit("A", OrderBook.Side.SELL, 1.00, 5, 0, l);
        book.submit("B", OrderBook.Side.SELL, 1.01, 5, 0, l);
        List<OrderBook.Fill> f = book.submit("A", OrderBook.Side.BUY, 1.05, 3, 0, l);
        assertTrue("A's buy passes over A's own ask and takes B's", f.size() == 1 && f.get(0).seller().equals("B"));
        close("...at B's price", f.get(0).price(), 1.01, 0);
        assertTrue("...and A's ask keeps its place at the head", book.asks().get(0).who().equals("A")
                && book.resting("A", OrderBook.Side.SELL) == 5);
    }

    /* =========================== 6. CLEARING LIMITS =========================== */

    static void clearingLimits() {
        out.println("\n--- 6. what the clearing can settle: a buyer short of cash, a seller short of the thing ---");
        Ledger l = new Ledger().give("A", 0, 10).give("B", 3.0, 0).give("C", 0, 2).give("D", 100, 0);
        OrderBook book = new OrderBook("x");
        book.submit("A", OrderBook.Side.SELL, 1.00, 10, 0, l);
        List<OrderBook.Fill> f = book.submit("B", OrderBook.Side.BUY, 1.00, 8, 0, l);
        close("a buyer with $3 for 8 at $1 fills 3", f.get(0).quantity(), 3, 1e-12);
        assertTrue("...and nothing of it rests: it cannot pay for more", book.bids().isEmpty());
        close("...the seller's 7 still rest", book.resting("A", OrderBook.Side.SELL), 7, 1e-12);
        OrderBook two = new OrderBook("y");
        two.submit("C", OrderBook.Side.SELL, 1.00, 6, 0, l);
        List<OrderBook.Fill> g = two.submit("D", OrderBook.Side.BUY, 1.00, 6, 0, l);
        close("a resting seller that holds 2 of the 6 it asked for delivers 2", g.get(0).quantity(), 2, 1e-12);
        close("...and the buyer's other 4 rest at its limit", two.resting("D", OrderBook.Side.BUY), 4, 1e-12);
        assertTrue("...where the seller's ask, trimmed to nothing, is gone", two.asks().isEmpty());
        assertTrue("every trade the book reported settled", l.settled == 2);
        close("...and no participant holds less than nothing", Math.min(l.held.get("C"), l.cash.get("B")), 0, 1e-9);
    }

    /* ============================== 7. THE RECORD ============================== */

    static void theRecord() {
        out.println("\n--- 7. the record: posted, filled, volume, turnover, the last price, the depth ---");
        Ledger l = rich("A", "B", "C", "D");
        OrderBook book = new OrderBook("x");
        book.submit("A", OrderBook.Side.BUY, .99, 4, 3, l);
        book.submit("B", OrderBook.Side.BUY, .99, 6, 3, l);
        book.submit("C", OrderBook.Side.BUY, .97, 5, 3, l);
        book.submit("D", OrderBook.Side.SELL, .98, 7, 3, l);
        close("posted to buy: 4 + 6 + 5", book.postedBuy(), 15, 0);
        close("posted to sell: 7", book.postedSell(), 7, 0);
        close("filled: 7, all at 0.99", book.filledSell(), 7, 1e-12);
        close("...its turnover 7 x 0.99", book.turnover(), 7 * .99, 1e-12);
        assertTrue("two trades, in the month they were made", book.trades() == 2 && book.lastTradeMonth() == 3);
        List<OrderBook.Level> levels = book.levels(OrderBook.Side.BUY);
        assertTrue("the depth by level: 3 left at 0.99 (one order), 5 at 0.97", levels.size() == 2
                && levels.get(0).price() == .99 && Math.abs(levels.get(0).quantity() - 3) < 1e-12
                && levels.get(0).orders() == 1 && levels.get(1).quantity() == 5);
        close("...and the depth at or better than 0.97 is all of it", book.depth(OrderBook.Side.BUY, .97), 8, 1e-12);
        book.startMonth();
        assertTrue("a new month opens a new record, and the orders and the last price stand",
                book.postedBuy() == 0 && book.trades() == 0 && book.bids().size() == 2 && book.lastPrice() == .99);
    }

    /* =========================== 8. A CURRENCY REFORM =========================== */

    static void currencyReform() {
        out.println("\n--- 8. a currency reform: quantities for an instrument counted in money, prices for one that is not ---");
        Ledger l = rich("A", "B");
        OrderBook face = new OrderBook("bond");
        face.submit("A", OrderBook.Side.BUY, .97, 100, 0, l);
        face.submit("B", OrderBook.Side.SELL, 1.02, 50, 0, l);
        face.redenominate(.01, true);
        close("a bond's bid: its face a hundredth", face.bids().get(0).quantity(), 1, 1e-12);
        close("...and its price a unit of face unchanged", face.bids().get(0).price(), .97, 0);
        OrderBook shares = new OrderBook("shares");
        shares.submit("A", OrderBook.Side.BUY, 40, 3, 0, l);
        shares.redenominate(.01, false);
        close("a share's bid: its price a hundredth", shares.bids().get(0).price(), .40, 1e-12);
        close("...and still three shares", shares.bids().get(0).quantity(), 3, 0);
    }

    /* ============================ 9. SAVE AND LOAD ============================ */

    static void saveAndLoad() {
        out.println("\n--- 9. save and load: the resting orders, their order and the last price ---");
        Ledger l = rich("A", "B", "C");
        OrderBook book = new OrderBook("bond-7");
        book.submit("A", OrderBook.Side.BUY, .96, 10, 4, l);
        book.submit("B", OrderBook.Side.BUY, .96, 10, 4, l);
        book.submit("C", OrderBook.Side.SELL, .95, 5, 4, l);
        book.submit("C", OrderBook.Side.SELL, 1.01, 8, 4, l);
        Gson gson = new Gson();
        OrderBook back = gson.fromJson(gson.toJson(book), OrderBook.class);
        assertTrue("it comes back under its own instrument", "bond-7".equals(back.instrument()));
        assertTrue("...with the same bids, best first", back.bids().size() == 2 && back.bids().get(0).who().equals("A")
                && Math.abs(back.bids().get(0).quantity() - 5) < 1e-12 && back.bids().get(1).who().equals("B"));
        assertTrue("...the same ask", back.asks().size() == 1 && back.asks().get(0).price() == 1.01);
        close("...and the same last price", back.lastPrice(), .96, 0);
        back.submit("D", OrderBook.Side.BUY, .96, 1, 5, rich("D"));
        assertTrue("an order posted after the reload queues behind the saved ones at its price",
                back.bids().get(2).who().equals("D"));
        OrderBook none = gson.fromJson("{}", OrderBook.class);
        assertTrue("a book saved with nothing on it loads empty, with no last price",
                none.bids().isEmpty() && none.asks().isEmpty() && Double.isNaN(none.lastPrice()));
    }

    /* ======================= 12. THE KNOWN ORDER (AP8) ======================= */

    static final OrderBook.Side BUY = OrderBook.Side.BUY, SELL = OrderBook.Side.SELL;

    /** Every halving checked against its walk, over every twin of section 12. */
    static long knownRested, knownHalved, knownCompared, knownDiffered;

    /**
     * Two books fed the same orders (AP8): `held` halves a side in known
     * order, play's path, under a checked probe that also walks the side from
     * its best order and compares where the two stop; `bare` always walks, as
     * every book did before AP8. Each submit's fills, and the two books after
     * each writer, must agree to the bit.
     */
    static final class Twin {
        OrderBook held, bare;
        final Ledger heldLedger = new Ledger(), bareLedger = new Ledger();
        final OrderBook.Probe probe = new OrderBook.Probe(), walking = new OrderBook.Probe();
        int fillsDiffered;

        Twin(String name) {
            held = new OrderBook(name);
            bare = new OrderBook(name);
            for (int i = 0; i < 12; i++) give("b" + i);
            for (int i = 0; i < 8; i++) give("s" + i);
            give("x");
            give("k");
            give("j");
            give("y");
            probe.checked = true;
            walking.bare = true;
            attach();
        }

        void give(String who) { heldLedger.give(who, 1e6, 1e6); bareLedger.give(who, 1e6, 1e6); }

        void attach() { held.probe = probe; bare.probe = walking; }

        void submit(String who, OrderBook.Side side, double price, double quantity) {
            String a = fills(held.submit(who, side, price, quantity, 1, heldLedger));
            String b = fills(bare.submit(who, side, price, quantity, 1, bareLedger));
            if (!a.equals(b)) fillsDiffered++;
        }

        void cash(String who, double money) { heldLedger.cash.put(who, money); bareLedger.cash.put(who, money); }

        void withdrawAll() { held.withdrawAll(); bare.withdrawAll(); }
    }

    static String fills(List<OrderBook.Fill> fills) {
        StringBuilder s = new StringBuilder();
        for (OrderBook.Fill f : fills) s.append(f.buyer()).append(" to ").append(f.seller()).append(' ')
                .append(Double.doubleToRawLongBits(f.quantity())).append(' ')
                .append(Double.doubleToRawLongBits(f.price())).append(';');
        return s.toString();
    }

    /** A book to the bit: every resting order on both sides in its place, and the month's record. */
    static String bits(OrderBook b) {
        StringBuilder s = new StringBuilder();
        for (List<OrderBook.Order> side : List.of(b.bids(), b.asks())) {
            for (OrderBook.Order o : side) s.append(o.seq()).append(' ').append(o.who()).append(' ').append(o.side())
                    .append(' ').append(Double.doubleToRawLongBits(o.price()))
                    .append(' ').append(Double.doubleToRawLongBits(o.quantity()))
                    .append(' ').append(Double.doubleToRawLongBits(o.submitted())).append(' ').append(o.month()).append(';');
            s.append('|');
        }
        for (double v : new double[] { b.lastPrice(), b.volume(), b.turnover(), b.postedBuy(), b.postedSell(),
                b.filledBuy(), b.filledSell(), b.sellQuantityWaited() }) s.append(Double.doubleToRawLongBits(v)).append(' ');
        s.append(b.trades()).append(' ').append(b.buysPosted()).append(' ').append(b.sellsPosted()).append(' ')
                .append(b.buysWaited()).append(' ').append(b.sellsWaited()).append(' ').append(b.lastTradeMonth());
        return s.toString();
    }

    /** The held book against the bare one after a writer: to the bit, every fill alike, every halving where its walk stops. */
    static void same(String label, Twin t) {
        knownRested += t.probe.rested;
        knownHalved += t.probe.halved;
        knownCompared += t.probe.compared;
        knownDiffered += t.probe.differed;
        assertTrue(label + ": the book to the bit against the walking one, " + t.probe.compared + " rests compared",
                bits(t.held).equals(bits(t.bare)) && t.fillsDiffered == 0 && t.probe.differed == 0
                        && t.probe.compared == t.probe.rested && t.probe.compared > 0);
        t.probe.rested = t.probe.halved = t.probe.compared = t.probe.differed = 0;
        t.fillsDiffered = 0;
    }

    /**
     * Both sides warmed the way the households post (AP8's count on slot 9:
     * two in three of the orders a new bid walked past stood at its very
     * price): bids in rising runs, in falling runs and many at one price,
     * asks above them, none crossing.
     */
    static Twin warmed(String name) {
        Twin t = new Twin(name);
        for (int i = 0; i < 24; i++) t.submit("b" + (i % 12), BUY, cents(90 + i % 6), 1 + i % 3);
        for (int i = 0; i < 24; i++) t.submit("b" + (i % 12), BUY, cents(95 - i % 6), 1 + i % 2);
        for (int i = 0; i < 12; i++) t.submit("b" + i, BUY, cents(92), 2);
        for (int i = 0; i < 24; i++) t.submit("s" + (i % 8), SELL, cents(97 + i % 4), 1 + i % 2);
        return t;
    }

    /** A price level as one double however it is reached: n hundredths. */
    static double cents(int n) { return n / 100.0; }

    /** A few orders after a writer, on both sides, between and past their levels. */
    static void more(Twin t, double scale) {
        t.submit("b2", BUY, .925 * scale, 1);
        t.submit("b3", BUY, .92 * scale, 1);
        t.submit("b4", BUY, .5 * scale, 1);
        t.submit("s3", SELL, .985 * scale, 1);
        t.submit("s4", SELL, .97 * scale, 1);
        t.submit("s5", SELL, 1.5 * scale, 1);
    }

    /** Where a participant's first order stands on a side, -1 if it has none. */
    static int indexOf(List<OrderBook.Order> side, String who) {
        for (int i = 0; i < side.size(); i++) if (side.get(i).who().equals(who)) return i;
        return -1;
    }

    /** The binade's roundoff: a bid just under the edge, then a higher one just over it, within its band (k, j). */
    static void roundoff(Twin t, double k, double j) {
        t.submit("k", BUY, k, 1);
        t.submit("j", BUY, j, 1);
    }

    static void knownOrder() {
        out.println("\n--- 12. the known order: a side in raw price order halved, the book to the bit against one that walks (AP8) ---");
        Twin w = warmed("warm");
        assertTrue("warmed: both sides in known order, every one of " + w.probe.rested + " rests halved",
                w.held.known(BUY) && w.held.known(SELL) && w.probe.halved == w.probe.rested && w.probe.rested == 84);
        same("warmed", w);

        // an order: at the end of its price, a level of its own, past every other; then a roundoff within a level
        Twin a = warmed("order");
        more(a, 1);
        assertTrue("an order: both sides still in known order", a.held.known(BUY) && a.held.known(SELL));
        same("an order", a);
        double top = a.bare.bids().get(0).price(), bottom = a.bare.asks().get(0).price();
        long atTop = a.bare.bids().stream().filter(o -> o.price() == top).count();
        long atBottom = a.bare.asks().stream().filter(o -> o.price() == bottom).count();
        a.submit("b6", BUY, Math.nextUp(top), 1);
        a.submit("s6", SELL, Math.nextDown(bottom), 1);
        assertTrue("a one-step improvement on the best bid and ask rests behind its whole level (fixture: "
                        + atTop + " and " + atBottom + " orders there), past the first the halving finds",
                atTop > 1 && atBottom > 1 && a.bare.bids().get((int) atTop).who().equals("b6")
                        && a.bare.asks().get((int) atBottom).who().equals("s6"));
        same("a roundoff within a level", a);

        // a fill: a sell takes the best bids and part of a level, a buy the best asks
        Twin b = warmed("fill");
        int bidsBefore = b.held.bids().size(), asksBefore = b.held.asks().size();
        b.submit("x", SELL, .94, 30);
        b.submit("x", BUY, .975, 9);
        assertTrue("a fill: bids and asks taken off the front (fixture: " + b.held.trades() + " trades)",
                b.held.trades() > 2 && b.held.bids().size() < bidsBefore && b.held.asks().size() < asksBefore);
        assertTrue("...and both sides still in known order", b.held.known(BUY) && b.held.known(SELL));
        more(b, 1);
        same("a fill", b);

        // a trim: b5 bid at the best price and can no longer pay for it
        Twin c = warmed("trim");
        long b5Before = c.held.bids().stream().filter(o -> o.who().equals("b5") && o.price() >= .95 - 1e-9).count();
        c.cash("b5", .5);
        c.submit("x", SELL, .95, 200);
        assertTrue("a trim: b5's " + b5Before + " bids at the best price filled as far as its cash, and gone",
                b5Before > 1 && c.held.bids().stream().noneMatch(o -> o.who().equals("b5") && o.price() >= .95 - 1e-9)
                        && c.held.known(BUY) && c.held.known(SELL));
        more(c, 1);
        same("a trim", c);

        // a cancel
        Twin d = warmed("cancel");
        int gone = d.held.withdraw("b7");
        d.bare.withdraw("b7");
        d.held.withdraw("s1");
        d.bare.withdraw("s1");
        assertTrue("a cancel: b7's " + gone + " bids withdrawn, both sides still in known order",
                gone > 1 && d.held.known(BUY) && d.held.known(SELL));
        more(d, 1);
        same("a cancel", d);

        // the month's close
        Twin e = warmed("close");
        e.withdrawAll();
        more(e, 1);
        assertTrue("the close: every order withdrawn, and the next ones rest in known order",
                e.held.bids().size() == 3 && e.held.known(BUY) && e.held.known(SELL));
        same("the close", e);

        // a roundoff out of raw order at a binade (a half, below every bid and ask of the warm book): the later, higher
        // bid kept behind the earlier by the band, and the next bid past the earlier one's band - where a halving that
        // trusted the order would rest it behind both
        double edge = .5, below = Math.ulp(Math.nextDown(edge)), above = Math.ulp(edge);
        double k = edge - (MoneyAudit.ULP_STEPS + 6) * below, j = edge + (MoneyAudit.ULP_STEPS / 2 - 4) * above;
        double x = Math.nextDown(edge);
        assertTrue("fixture: the higher bid within the earlier's band, the next past it and no higher than the higher, raw",
                j - k <= MoneyAudit.tolerance(0, j) && x - k > MoneyAudit.tolerance(0, x) && j >= x && k < x);
        Twin f = warmed("roundoff");
        roundoff(f, k, j);
        int at = indexOf(f.held.bids(), "k");
        assertTrue("a roundoff out of raw order: the higher bid rests behind the earlier, last, and the bids forget their order",
                at == f.held.bids().size() - 2 && f.held.bids().get(at + 1).who().equals("j")
                        && !f.held.known(BUY) && f.held.known(SELL));
        f.submit("y", BUY, x, 1);
        assertTrue("...so the next bid rests ahead of the one it is past the band of, behind every better one",
                indexOf(f.bare.bids(), "y") == at && indexOf(f.held.bids(), "y") == at);
        more(f, 1);
        same("a roundoff out of raw order", f);
        f.withdrawAll();
        f.submit("b1", BUY, .93, 1);
        assertTrue("...and once the side is empty its order is known again", f.held.known(BUY));
        same("a roundoff, the side emptied", f);

        // a split, and a consolidation
        Twin g = warmed("split");
        g.held.split(2);
        g.bare.split(2);
        assertTrue("a split: every price over two, both sides still in known order",
                g.held.known(BUY) && g.held.known(SELL) && g.held.bids().get(0).price() == cents(95) / 2);
        more(g, .5);
        same("a split", g);
        g.held.split(.5);
        g.bare.split(.5);
        more(g, 1);
        same("a consolidation", g);

        // a reform: a share's prices, then a bond's quantities
        Twin h = warmed("reform");
        h.held.redenominate(.37, false);
        h.bare.redenominate(.37, false);
        assertTrue("a reform of a share's book: every price times the scale, both sides still in known order",
                h.held.known(BUY) && h.held.known(SELL) && h.held.bids().get(0).price() == cents(95) * .37);
        more(h, .37);
        same("a reform of the prices", h);
        h.held.redenominate(1000, true);
        h.bare.redenominate(1000, true);
        more(h, .37);
        same("a reform of the quantities", h);

        // a load: in raw order or not, a loaded side is unknown until it empties
        Gson gson = new Gson();
        Twin l = warmed("load");
        roundoff(l, k, j);
        l.held = gson.fromJson(gson.toJson(l.held), OrderBook.class);
        l.bare = gson.fromJson(gson.toJson(l.bare), OrderBook.class);
        l.attach();
        assertTrue("a load: both sides unknown, the bids out of raw order and the asks in it",
                !l.held.known(BUY) && !l.held.known(SELL)
                        && l.held.bids().get(indexOf(l.held.bids(), "k") + 1).who().equals("j"));
        l.submit("y", BUY, x, 1);
        more(l, 1);
        same("a load", l);
        l.withdrawAll();
        more(l, 1);
        assertTrue("...known again once emptied", l.held.known(BUY) && l.held.known(SELL));
        same("a load, the sides emptied", l);

        // the autosave's copy carries the order it knows
        Twin m = warmed("copy");
        OrderBook copy = m.held.copy();
        assertTrue("the autosave's copy: both sides in known order, as the book's", copy.known(BUY) && copy.known(SELL));
        m.held = copy;
        m.attach();
        more(m, 1);
        same("the autosave's copy", m);

        assertTrue("every rest of section 12 checked against its walk: " + knownCompared + " compared, "
                        + knownHalved + " halved, " + knownDiffered + " differed",
                knownCompared == knownRested && knownHalved > 0 && knownDiffered == 0);
    }
}
