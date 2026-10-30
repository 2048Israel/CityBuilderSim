package ham.citybuildersim;

import java.io.PrintStream;

/**
 * Verifies a share's price as its quote, and the short selling that brings a bubble down (EM2, 2026-10-30) - each claim on a fixture that
 * causes it, not on a city that happens to show it.
 *
 * WHY. A share's price was its last trade, and one print far over a thin book stayed the price for as long as nobody traded. Jerus, 2026-10-29:
 * "share price should reflect mid price, and if no ask or bid then last traded", over a book where "last traded is 448k but current bids are 4k".
 * On main's default playtest Business Services stood 231 months with its last trade over ten times its best bid, and nobody could sell what
 * they did not hold, so the high print had no seller to bring it down. The quote is the first half (Exchange.price()); the shorts are the second.
 * ExchangeCheck holds what the book does with an order and MarketCheck that the orders are the right ones; this holds the price they are read at,
 * and what the shorts do to it.
 *
 * WHAT THIS HAS TO PROVE (docs/queue/EM2.md):
 *
 *   1. THE QUOTE: the price is the middle of the best bid and the best ask while both rest; with one side empty it is the last trade held inside
 *      that side (never over the ask that stands, nor under the bid); with neither it is the last trade; before the first trade it is fair value,
 *      held the same way inside a side that stands. A quote set by a bid and an ask, or held inside one, is current however old the last trade:
 *      the city marks it, and only a quote that is a last trade older than Exchange.STALE_MARK_MONTHS is marked at fair value.
 *   2. A BUBBLE FALLS: one high print over a thin book falls toward value within a stated number of months, with shorts and not without them.
 *   3. THE REGISTER IS WHOLE: a short is a negative holding the register carries beside the long ones, every share lent is a share owned, and the
 *      money audit balances through the opening, the carrying and the covering of a short.
 *   4. THE LENDER IS PAID: the holder whose shares were lent gets the dividend the borrower owes and the lending fee, and loses nothing else.
 *   5. A MARGIN CALL COVERS: a short whose collateral falls under the margin is bought back, at the ask, by the step that finds it.
 *   6. A SAVE IN THE MIDDLE OF A SHORT: the shorts, their lenders, their collateral and their fees round-trip a save and a load, and the month after
 *      is the month a city that never saved would have had.
 *   7. HOW OFTEN: over a real run, the months a short opened, the months one was covered and the bubbles the quote stood against, counted - a
 *      mechanic that never fires proves nothing.
 *
 * Every claim is CAUSED: a book emptied and given one trade, then the orders a line names and no others; a bid or an ask put on it by a participant
 * with the means to stand behind it.
 */
public class ShortSellingCheck {

    static int fails = 0;
    static int checks = 0;
    static PrintStream out;

    static void assertTrue(String label, boolean ok) {
        checks++;
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        checks++;
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-96s FAIL  %,.9f != %,.9f%n", label, actual, expected);
        } else {
            out.printf("%-96s OK%n", label);
        }
    }

    static int sectionStart;

    static void section(String title) {
        out.println("\n--- " + title + " ---");
        sectionStart = checks;
    }

    static void sectionEnd() {
        out.printf("    (%d assertions)%n", checks - sectionStart);
    }

    static final int INDUSTRY = Equity.indexOf(Sectors.INDUSTRY);

    /**
     * A one-company market on a table: the register with the company listed (200 shares to each of a hundred households, $1 a share), a bank for
     * the desk, an exchange stepped once at month 1 so that fair value is struck - and then the book emptied, so that whatever rests on it afterwards
     * a line put there. A trade is a cell's share sold to the world at the price named; a bid is the world's, an ask a cell's.
     */
    static final class Table {
        final Equity reg = new Equity();
        final HouseholdBalance town = ExchangeCheck.savers(12.0, 4.0);
        final Bank bank = ExchangeCheck.bankWith(100_000);
        final Exchange ex = new Exchange();
        final String cell;

        Table() {
            reg.listIfUnlisted(INDUSTRY, 20_000, town);
            ExchangeCheck.step(ex, reg, town, bank, new ExchangeCheck.Firms(), ExchangeCheck.bookOf(INDUSTRY, 20_000), ExchangeCheck.W, 0, 1);
            ex.bookOf(INDUSTRY).withdrawAll();
            cell = Exchange.CELL + ExchangeCheck.couple(town).key();
        }

        double fair() { return ex.fair(INDUSTRY); }

        /** One share changes hands at this price, the cell's to the world's bid: the book's last trade, and nothing left resting. */
        Table trade(double price) {
            ex.tradeForCheck(INDUSTRY, cell, OrderBook.Side.SELL, price, 1);
            ex.tradeForCheck(INDUSTRY, Exchange.WORLD, OrderBook.Side.BUY, price, 1);
            return this;
        }

        /** A bid rests at this price: the world's, which can stand behind any. */
        Table bid(double price) {
            ex.tradeForCheck(INDUSTRY, Exchange.WORLD, OrderBook.Side.BUY, price, 1);
            return this;
        }

        /** An ask rests at this price: a cell's, which holds the shares. */
        Table ask(double price) {
            ex.tradeForCheck(INDUSTRY, cell, OrderBook.Side.SELL, price, 1);
            return this;
        }

        /** The exchange's clock moved on to this month, its book left alone. */
        Table clockAt(int month) {
            ex.attach(reg, town, bank, null, month);
            return this;
        }

        double price() { return ex.price(INDUSTRY); }
        double bestBid() { return ex.bestBid(INDUSTRY); }
        double bestAsk() { return ex.bestAsk(INDUSTRY); }
        double lastTrade() { return ex.lastTrade(INDUSTRY); }
        boolean noBid() { return Double.isNaN(bestBid()); }
        boolean noAsk() { return Double.isNaN(bestAsk()); }
    }

    /** What the last trade of the lines below was, fair value's $1 being neither it nor the middle of anything they rest. */
    static final double LAST = 1.5;

    public static void main(String[] args) {
        out = System.out;

        /* ================= 1. the quote ================= */
        section("1. the quote: the middle while both sides rest, the last trade held inside the side that stands, fair value before the first trade");

        // Before the first trade.
        Table fresh = new Table();
        close("fixture: fair value is the register's reckoning of $1 a share", fresh.fair(), 1.0, 1e-12);
        assertTrue("fixture: nothing has traded and nothing rests", !fresh.ex.hasTraded(INDUSTRY) && Double.isNaN(fresh.lastTrade()) && fresh.noBid() && fresh.noAsk());
        close("before the first trade the quote is fair value", fresh.price(), fresh.fair(), 1e-12);
        assertTrue("...which is no last trade, and is not marked at fair value for being old: there is no trade to be old",
                !fresh.ex.quoteIsLastTrade(INDUSTRY) && !fresh.clockAt(1 + Exchange.STALE_MARK_MONTHS + 1).ex.markedAtFair(INDUSTRY));

        Table preAsk = new Table().ask(0.8);
        assertTrue("fixture: an ask under fair value rests, and nothing has traded", !preAsk.ex.hasTraded(INDUSTRY) && preAsk.noBid() && preAsk.bestAsk() == 0.8);
        close("before the first trade, with only an ask under fair value, the quote is the ask", preAsk.price(), 0.8, 1e-12);
        Table preBid = new Table().bid(1.3);
        assertTrue("fixture: a bid over fair value rests, and nothing has traded", !preBid.ex.hasTraded(INDUSTRY) && preBid.noAsk() && preBid.bestBid() == 1.3);
        close("...with only a bid over fair value, the bid", preBid.price(), 1.3, 1e-12);
        Table preBoth = new Table().bid(0.9).ask(1.2);
        close("...with a bid and an ask resting, the middle of them", preBoth.price(), (0.9 + 1.2) / 2, 1e-12);
        Table preOver = new Table().ask(1.4);
        close("...with only an ask over fair value, fair value: the ask does not bound it", preOver.price(), preOver.fair(), 1e-12);

        // After a trade at LAST, with fair value at $1 and the last trade neither it nor the middle of anything below.
        Table both = new Table().trade(LAST).bid(0.9).ask(1.2);
        close("fixture: the last trade is $1.50 and fair value $1", both.lastTrade(), LAST, 1e-12);
        assertTrue("fixture: a bid at 0.90 and an ask at 1.20 rest, both under the last trade", both.bestBid() == 0.9 && both.bestAsk() == 1.2 && LAST > both.bestAsk());
        close("with a bid and an ask resting the quote is exactly their middle", both.price(), (0.9 + 1.2) / 2, 1e-12);
        assertTrue("...and not the last trade, nor fair value", both.price() != both.lastTrade() && both.price() != both.fair());
        assertTrue("...which a quote that is the last trade is not", !both.ex.quoteIsLastTrade(INDUSTRY));
        close("...the last trade is still the book's, beside the quote", both.ex.lastTrade(INDUSTRY), LAST, 1e-12);

        Table askUnder = new Table().trade(LAST).ask(1.2);
        assertTrue("fixture: only an ask rests, under the last trade", askUnder.noBid() && askUnder.bestAsk() == 1.2 && askUnder.bestAsk() < askUnder.lastTrade());
        close("with only an ask under the last trade the quote is the ask", askUnder.price(), 1.2, 1e-12);
        assertTrue("...which sets it, so it is not the last trade", !askUnder.ex.quoteIsLastTrade(INDUSTRY));

        Table askOver = new Table().trade(LAST).ask(1.8);
        assertTrue("fixture: only an ask rests, over the last trade", askOver.noBid() && askOver.bestAsk() == 1.8 && askOver.bestAsk() > askOver.lastTrade());
        close("with only an ask over the last trade the quote is the last trade", askOver.price(), LAST, 1e-12);
        assertTrue("...an ask that stands over it bounds nothing, so the quote is the last trade", askOver.ex.quoteIsLastTrade(INDUSTRY));

        Table bidOver = new Table().trade(LAST).bid(1.8);
        assertTrue("fixture: only a bid rests, over the last trade", bidOver.noAsk() && bidOver.bestBid() == 1.8 && bidOver.bestBid() > bidOver.lastTrade());
        close("with only a bid over the last trade the quote is the bid", bidOver.price(), 1.8, 1e-12);
        assertTrue("...which sets it, so it is not the last trade", !bidOver.ex.quoteIsLastTrade(INDUSTRY));

        Table bidUnder = new Table().trade(LAST).bid(1.2);
        assertTrue("fixture: only a bid rests, under the last trade", bidUnder.noAsk() && bidUnder.bestBid() == 1.2 && bidUnder.bestBid() < bidUnder.lastTrade());
        close("with only a bid under the last trade the quote is the last trade", bidUnder.price(), LAST, 1e-12);
        assertTrue("...a bid that stands under it bounds nothing, so the quote is the last trade", bidUnder.ex.quoteIsLastTrade(INDUSTRY));

        Table neither = new Table().trade(LAST);
        assertTrue("fixture: nothing rests, a trade has been made", neither.noBid() && neither.noAsk() && neither.ex.hasTraded(INDUSTRY));
        close("with neither side the quote is the last trade", neither.price(), LAST, 1e-12);
        assertTrue("...and it is the last trade, not fair value", neither.ex.quoteIsLastTrade(INDUSTRY) && neither.price() != neither.fair());

        // The city's mark: a current quote however old the last trade; only a last trade is marked at fair value, and then only once it is old.
        int lastMonth = 1;
        int older = lastMonth + Exchange.STALE_MARK_MONTHS + 1;
        Table markBoth = new Table().trade(LAST).bid(0.9).ask(1.2).clockAt(older);
        assertTrue("fixture: the last trade is older than STALE_MARK_MONTHS, and a bid and an ask rest",
                markBoth.ex.bookOf(INDUSTRY).lastTradeMonth() == lastMonth && older - lastMonth > Exchange.STALE_MARK_MONTHS && !markBoth.noBid() && !markBoth.noAsk());
        assertTrue("a quote set by a bid and an ask is not marked at fair value, however old the last trade", !markBoth.ex.markedAtFair(INDUSTRY));
        close("...the city marks the holding at the quote", markBoth.ex.cityMark(INDUSTRY), (0.9 + 1.2) / 2, 1e-12);

        Table markNeither = new Table().trade(LAST).clockAt(lastMonth + Exchange.STALE_MARK_MONTHS);
        assertTrue("a quote that is the last trade, a year old, is still current: STALE_MARK_MONTHS is not yet passed",
                markNeither.ex.quoteIsLastTrade(INDUSTRY) && !markNeither.ex.markedAtFair(INDUSTRY));
        close("...and the city marks the holding at it", markNeither.ex.cityMark(INDUSTRY), LAST, 1e-12);
        markNeither.clockAt(older);
        assertTrue("with neither side and a last trade older than STALE_MARK_MONTHS the quote is marked at fair value",
                markNeither.noBid() && markNeither.noAsk() && older - lastMonth > Exchange.STALE_MARK_MONTHS && markNeither.ex.markedAtFair(INDUSTRY));
        close("...the city marks the holding at fair value, not at the old trade", markNeither.ex.cityMark(INDUSTRY), markNeither.fair(), 1e-12);
        close("...while the price trading reads is still the last trade", markNeither.price(), LAST, 1e-12);

        assertTrue("a side held inside, old trade: an ask under the last trade sets the quote, so it is current",
                !new Table().trade(LAST).ask(1.2).clockAt(older).ex.markedAtFair(INDUSTRY));
        assertTrue("...a bid over the last trade the same", !new Table().trade(LAST).bid(1.8).clockAt(older).ex.markedAtFair(INDUSTRY));
        assertTrue("...but an ask over the last trade leaves the quote the old trade, and it is marked at fair value",
                new Table().trade(LAST).ask(1.8).clockAt(older).ex.markedAtFair(INDUSTRY));
        assertTrue("...and a bid under it the same", new Table().trade(LAST).bid(1.2).clockAt(older).ex.markedAtFair(INDUSTRY));
        sectionEnd();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
