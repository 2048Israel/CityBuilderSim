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
        priceTime();
        restingPrice();
        nobodyObliged();
        partialFills();
        noSelfTrade();
        clearingLimits();
        theRecord();
        currencyReform();
        saveAndLoad();
        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================== 1. PRICE-TIME ============================== */

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
}
