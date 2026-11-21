package ham.citybuildersim;

import ham.citybuildersim.ui.KeptBench;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

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
 *   2. A BUBBLE FALLS: one print at a hundred times a share's value over a thin book falls toward value within a stated number of months with
 *      the world's shorts and does not without them.
 *   3. THE REGISTER IS WHOLE: a short is a position the exchange carries beside the long holdings (the buyer's holding rose as for any
 *      purchase, so the holdings add to the shares in issue PLUS what is short), every share short is lent by the cells and the fund's market book
 *      pro rata and stays theirs, and the money audit balances through the opening, the carrying and the covering of a short.
 *   4. THE LENDER IS PAID: the holder whose shares were lent gets the company's dividend on every share it holds, the short pays the dividend on
 *      what is short, and the world pays the lenders the lending fee on the quote the book stood at, more when the share is hard to borrow.
 *   5. A MARGIN CALL AND A RECALL COVER: a short whose account is under the maintenance margin buys in all it is short at the market, taking
 *      asks up to the most it can pay (the break price), and a short the lenders can no longer lend buys in the difference the same way, each
 *      counted; neither pays the break price where the market is under it.
 *   6. A SAVE IN THE MIDDLE OF A SHORT: the shorts, their lenders, their collateral and their fees round-trip a save and a load, and the month after
 *      is the month a city that never saved would have had. FS14: openShort()'s short, held standing (the world's own orders off) through the
 *      months it waits for its fee and a dividend.
 *   7. HOW OFTEN: over a real run, the months a short opened, the months one was covered and the bubbles the quote stood against, counted - a
 *      mechanic that never fires proves nothing. LF1b: and the months the cells traded shares with the world, each month's audit lines for
 *      them read against the exchange's figures; SX2 (FS14): in the run's first month (TRADE_ABROAD_MONTH) a cell's sale to the world and
 *      purchase from it are caused as section 9 causes them, so those lines are read on a month with both over nought. The 2,000 months'
 *      audit, register and shorts-off identities stay played.
 *   8. THE FEE ON THE LENDERS' BOOKS (LF1): the households' fee is a pair of lines in the audit, income from the world and banked; the fund's is
 *      its own income counter and a row in its ledger; the fee is charged on the shares the lenders hold and no more; emigrants are not dropped
 *      while a short is open; a short at a company with no shares in issue takes what it sold with it; a short at a company worth nothing is
 *      still called in. LF1c: that cancelling follows the month's order (a company with no book, which the month's listing leaves unlisted), and
 *      the two claims it takes across the border, the world's own shares in the company and the short closed for nothing, are valuation lines of
 *      the audit ("forgiven" counts them, the books still foot); the Fund screen says a fee's row, a share's write-off and the income a share
 *      lot's page holds in words.
 *   9. THE CELLS' SHARES ABROAD (LF1b): a cell selling a share to the world and buying one from it, in the real month, are each a pair of
 *      lines in the audit, a financial flow and its domestic half, as the cells' bonds are: the month foots, and the balance of payments'
 *      financial inflow holds the sale and its outflow the purchase.
 *
 * Every claim is CAUSED: a book emptied and given one trade, then the orders a line names and no others; a bid or an ask put on it by a participant
 * with the means to stand behind it.
 */
public class ShortSellingCheck {

    static int fails = 0;
    static int checks = 0;
    static PrintStream out;
    static PrintStream quiet;

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
        final Equity reg = ExchangeCheck.equity();
        final HouseholdBalance town = ExchangeCheck.savers(12.0, 4.0);
        final Bank bank = ExchangeCheck.bankWith(100_000);
        final Exchange ex = ExchangeCheck.exchange();
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

    /* =======================================================================
       THE BENCH (sections 2 to 5): the same market as Table, but valued on its record, with holders to lend and a fund to be lent from.
       ======================================================================= */

    static final double W = ExchangeCheck.W;
    static final double SHARES = 1_000, YEARLY = 30;

    /**
     * A thousand shares of one company with a year of earnings on its record, so the world and the cells value it on it (EM1) and the shorts may
     * sell it; a fair value the first step strikes; and holders who say what they hold: the cells (a poor one under no pressure and a rich one),
     * the world's long, and the city's fund, which has cash enough that its rule neither buys nor sells (it holds the 10% it may). The book is emptied
     * after the first step, so whatever rests on it afterwards a line put there. Both cells sit at their cushion, so neither buys nor sells until a
     * line gives it a reason to.
     */
    static final class Bench {
        final MarketCheck.City city;
        final Equity reg;
        final Exchange ex;
        final HouseholdBalance hb;
        final Household poor, rich;
        final TreasuryFund fund = new TreasuryFund();
        final double fair;

        Bench(double poorHolds, double richHolds, double worldHolds, double fundHolds, double fundCash) {
            hb = MarketCheck.twoCells(4.0, 4.0);
            poor = MarketCheck.poorOf(hb);
            rich = MarketCheck.richOf(hb);
            poor.savings = 12;
            rich.savings = 12;
            city = new MarketCheck.City(hb, ExchangeCheck.bankWith(100_000));
            city.list(INDUSTRY, SHARES, YEARLY).firm(INDUSTRY, 0, 0, 0);
            reg = city.reg;
            ex = city.ex;
            poor.shares[INDUSTRY] = poorHolds / poor.households();
            rich.shares[INDUSTRY] = richHolds / rich.households();
            reg.moveForeign(INDUSTRY, worldHolds);
            reg.moveCity(INDUSTRY, fundHolds);
            ex.attachFund(fund);
            fund.receive(fundCash);
            city.step(0);
            ex.bookOf(INDUSTRY).withdrawAll();
            fair = ex.fair(INDUSTRY);
        }

        String poorName() { return MarketCheck.nameOf(poor); }
        String richName() { return MarketCheck.nameOf(rich); }
        double price() { return ex.price(INDUSTRY); }
        double lastTrade() { return ex.lastTrade(INDUSTRY); }
        double shortShares() { return ex.shortShares(INDUSTRY); }

        /** What everybody holds, the cells, the desk, the world and the fund: the shares in issue plus what the world is short, to the drift the register allows. */
        double holdings() {
            return hb.sharesHeld(INDUSTRY) + reg.getDealerShares(INDUSTRY) + reg.getForeignShares(INDUSTRY) + reg.getCityShares(INDUSTRY);
        }

        /** One share of the poor cell's changes hands at this price, to the world's bid: the book's last trade, and nothing resting. */
        Bench print(double price) {
            ex.tradeForCheck(INDUSTRY, poorName(), OrderBook.Side.SELL, price, 1);
            ex.tradeForCheck(INDUSTRY, Exchange.WORLD, OrderBook.Side.BUY, price, 1);
            return this;
        }

        /** The world sells this many shares short at this price to this buyer's bid: the short's ask rests, the buyer's bid takes it. @return the shares it sold */
        double open(String buyer, double price, double q) {
            ex.tradeForCheck(INDUSTRY, Exchange.WORLD_SHORT, OrderBook.Side.SELL, price, q);
            double sold = filled(ex.tradeForCheck(INDUSTRY, buyer, OrderBook.Side.BUY, price, q));
            ex.bookOf(INDUSTRY).withdrawAll();
            return sold;
        }

        /** The world's short asks at fair value plus SHORT_BAND. */
        double shortAsk() { return fair * (1 + Exchange.SHORT_BAND); }
    }

    static double filled(java.util.List<OrderBook.Fill> fills) {
        double t = 0;
        for (OrderBook.Fill f : fills) t += f.quantity();
        return t;
    }

    /** The fee a year the lenders are paid at this many shares short of this many lendable (Exchange.lendingRate()), as the dials say. */
    static double rateAt(double utilisation) {
        double u = Math.min(1, utilisation);
        return Exchange.LENDING_FEE + (u > Exchange.HARD_TO_BORROW
                ? Exchange.HARD_TO_BORROW_FEE * (u - Exchange.HARD_TO_BORROW) / (1 - Exchange.HARD_TO_BORROW) : 0);
    }

    /** What the last trade of the lines below was, fair value's $1 being neither it nor the middle of anything they rest. */
    static final double LAST = 1.5;

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());

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

        theBubble();
        theRegister();
        theLender();
        theCalls();
        theSave();
        theBooks();
        theCellsAbroad();
        howOften();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* =======================================================================
       THE LINES OF SECTIONS 2 TO 5
       ======================================================================= */

    /** A bench with every share in one cell under its cushion, the desk bidding near value and holding none, and one print at 100 times fair value. */
    static Bench bubble(boolean shorts) {
        Bench b = new Bench(SHARES, 0, 0, 0, 0);
        b.ex.setShortSelling(shorts);
        b.print(100 * b.fair);
        b.poor.savings = 0;             // under its cushion by three months of its take-home: it offers HOME_SPEED of the shortfall at the market
        return b;
    }

    static final int BUBBLE_MONTHS = 24;

    static void theBubble() {
        section("2. a bubble falls: one print at 100 times fair value, " + BUBBLE_MONTHS + " months, without the world's shorts and with them");

        Bench off = bubble(false);
        double fairOff = off.fair;
        close("fixture: the book's last trade is a print at 100 times fair value", off.lastTrade(), 100 * fairOff, 1e-9 * fairOff);
        assertTrue("fixture: the cell holding the shares is under its cushion, so it sells at the market, and the desk holds none",
                off.poor.savings() < HouseholdBalance.SHARE_CUSHION_MONTHS * off.poor.disposable() && off.reg.getDealerShares(INDUSTRY) == 0
                        && MarketCheck.heldBy(off.poor, INDUSTRY) == SHARES - 1);
        int overTen = 0;
        double lowestOff = Double.MAX_VALUE;
        for (int m = 1; m <= BUBBLE_MONTHS; m++) {
            off.city.step(0);
            if (off.price() > 10 * fairOff) overTen++;
            lowestOff = Math.min(lowestOff, off.price() / fairOff);
        }
        assertTrue("fixture: the desk bids near value, under fair value and not by more than BAND",
                off.ex.bestBid(INDUSTRY) < fairOff && off.ex.bestBid(INDUSTRY) > fairOff * (1 - Exchange.BAND));
        assertTrue("without shorts, after " + BUBBLE_MONTHS + " steps (" + BUBBLE_MONTHS + " months) the quote is still over 10 times fair value",
                off.price() > 10 * fairOff);
        assertTrue("...it stood over 10 times fair value at every one of those " + BUBBLE_MONTHS + " months (" + overTen + ")", overTen == BUBBLE_MONTHS);
        close("...because the cell asks at the market, which is the print: its ask stands at 100 times fair value",
                off.ex.bestAskOf(INDUSTRY, off.poorName()), 100 * fairOff, 1e-9 * fairOff);
        assertTrue("...and the world sold nothing short", off.ex.getLifeShortSold() == 0 && off.shortShares() == 0
                && Double.isNaN(off.ex.bestAskOf(INDUSTRY, Exchange.WORLD_SHORT)));
        out.printf("   without shorts the quote never went under %.1f times fair value in %d months%n", lowestOff, BUBBLE_MONTHS);

        Bench on = bubble(true);
        double fairOn = on.fair;
        on.city.step(0);
        assertTrue("with shorts, after 1 step (1 month) the quote is within SHORT_BAND of fair value: " + String.format("%.3f", on.price() / fairOn) + " times it",
                Math.abs(on.price() / fairOn - 1) <= Exchange.SHORT_BAND);
        close("...the world's short asks at fair value plus SHORT_BAND, a limit order", on.ex.bestAskOf(INDUSTRY, Exchange.WORLD_SHORT), on.shortAsk(), 1e-9 * fairOn);
        close("...for MAX_SHORT_INTEREST of the shares outstanding (the cell lends more than that)",
                on.ex.bookOf(INDUSTRY).resting(Exchange.WORLD_SHORT, OrderBook.Side.SELL), Exchange.MAX_SHORT_INTEREST * on.reg.getOutstanding(INDUSTRY), 1e-9);
        double cellAsk = (fairOn * (1 - Exchange.SPREAD / 2) + on.shortAsk()) / 2;
        close("...and the cell, a price-taker, rests its ask at the market the short's ask made - the middle of the desk's bid and the short's ask - under it",
                on.ex.bestAskOf(INDUSTRY, on.poorName()), cellAsk, 1e-9 * fairOn);
        assertTrue("...so an ask near value brought the quote down and no trade did: nothing changed hands", on.ex.getVolume(INDUSTRY) == 0);
        close("...and the print is still the book's last trade, beside the quote", on.lastTrade(), 100 * fairOn, 1e-9 * fairOn);
        int inBand = 1;
        for (int m = 2; m <= BUBBLE_MONTHS; m++) {
            on.city.step(0);
            if (Math.abs(on.price() / fairOn - 1) <= Exchange.SHORT_BAND) inBand++;
        }
        assertTrue("...and it stays within SHORT_BAND of fair value for all " + BUBBLE_MONTHS + " months (" + inBand + ")", inBand == BUBBLE_MONTHS);
        assertTrue("...the world posting an ask in each of them, and the register untouched: nobody bought, so nothing is short",
                on.ex.getLifeShortAsks() == BUBBLE_MONTHS && on.shortShares() == 0 && on.holdings() == SHARES);
        sectionEnd();
    }

    /** A thousand-share bench, the holders as in the sections below: poor 300, rich 400, the world 200 and the fund 100. */
    static Bench standard() { return new Bench(300, 400, 200, 100, 10_000_000); }

    static void theRegister() throws Exception {
        section("3. the register is whole and the audit balances: a short opened, carried and covered");

        Bench r = standard();
        double ask = r.shortAsk();
        assertTrue("fixture: the cells hold 700, the world 200 and the fund 100 of the 1,000 shares in issue, and nothing is short",
                r.holdings() == SHARES && r.shortShares() == 0 && r.reg.getShares(INDUSTRY) == SHARES);
        assertTrue("fixture: the fund's rule posts nothing at its 10% limit", r.ex.bookOf(INDUSTRY).resting(Exchange.FUND, OrderBook.Side.BUY) == 0
                && r.ex.bookOf(INDUSTRY).resting(Exchange.FUND, OrderBook.Side.SELL) == 0);
        double toDesk = r.open(Exchange.DESK, ask, 40);
        r.rich.savings = 5_000;                                 // a buyer with the means to stand behind it
        double toCell = r.open(r.richName(), ask, 30);
        r.rich.savings = 12;                                    // ...and at its cushion again, so it buys no more
        double s = r.shortShares();
        close("the world sold 40 shares short to the desk and 30 to a cell, at fair value plus SHORT_BAND", toDesk + toCell, 70, 1e-9);
        close("...so it is short 70", s, 70, 1e-9);
        close("...with INITIAL_MARGIN times what it was paid held against them", r.ex.shortCollateral(INDUSTRY), Exchange.INITIAL_MARGIN * 70 * ask, 1e-9 * ask);
        close("...the holdings add to the shares in issue PLUS what is short: the buyers' rose as for any purchase", r.holdings(), SHARES + s, 1e-9 * SHARES);
        close("...the desk holds 40 of them and the buying cell has 430", r.reg.getDealerShares(INDUSTRY) + MarketCheck.heldBy(r.rich, INDUSTRY), 40 + 430, 1e-9);
        close("...the world's own long is untouched by the short", r.reg.getForeignShares(INDUSTRY), 200, 0);
        close("...as is the fund's, which lends", r.reg.getCityMarketShares(INDUSTRY), 100, 0);
        close("...and the cell that lent: it holds what it did", MarketCheck.heldBy(r.poor, INDUSTRY), 300, 1e-9);
        double lendable = r.hb.sharesHeld(INDUSTRY) + r.reg.getCityMarketShares(INDUSTRY);
        close("every share short is lent: the cells' part and the fund's add to what is short", r.ex.lentByCells(INDUSTRY) + r.ex.lentByFund(INDUSTRY), s, 1e-9);
        close("...the fund's part its share of what can be lent, pro rata to what each holds", r.ex.lentByFund(INDUSTRY),
                s * r.reg.getCityMarketShares(INDUSTRY) / lendable, 1e-9);
        // The next step: the desk asks again, fair value is the quote, and the short bids for what it is short at fair value...
        r.city.step(0);
        close("the next step the quote is within BAND of fair value, and the short bids for all it is short", r.ex.bookOf(INDUSTRY).resting(Exchange.WORLD_SHORT, OrderBook.Side.BUY), s, 1e-9);
        close("...at fair value", r.ex.bestBidOf(INDUSTRY, Exchange.WORLD_SHORT), r.fair, 1e-9 * r.fair);
        // ...and a lender sells into it: a cover of half of it.
        double collateralBefore = r.ex.shortCollateral(INDUSTRY);
        double covered = filled(r.ex.tradeForCheck(INDUSTRY, r.poorName(), OrderBook.Side.SELL, r.fair, s / 2));
        close("a cell sells 35 shares into the short's bid, and the short covers them", covered, s / 2, 1e-9);
        close("...at the bid, fair value", r.ex.getLifeShortCoveredValue(), covered * r.fair, 1e-9 * r.fair);
        close("...it is short 35 now", r.shortShares(), s - covered, 1e-9);
        close("...the collateral goes back pro rata: half of it is held", r.ex.shortCollateral(INDUSTRY), collateralBefore * (1 - covered / s), 1e-9 * collateralBefore);
        close("...the holdings still add to the shares in issue plus what is short", r.holdings(), SHARES + r.shortShares(), 1e-9 * SHARES);
        close("...and every share short is still lent", r.ex.lentByCells(INDUSTRY) + r.ex.lentByFund(INDUSTRY), r.shortShares(), 1e-9);
        assertTrue("...counted: 70 sold short and 35 covered over the city's life",
                Math.abs(r.ex.getLifeShortSold() - 70) < 1e-9 && Math.abs(r.ex.getLifeShortCovered() - 35) < 1e-9);
        // Covered whole, the position is gone and so is its collateral.
        r.ex.tradeForCheck(INDUSTRY, r.richName(), OrderBook.Side.SELL, r.fair, r.shortShares());
        close("covered whole, nothing is short", r.shortShares(), 0, 1e-9);
        close("...no collateral is held", r.ex.shortCollateral(INDUSTRY), 0, 1e-9);
        close("...nothing is lent", r.ex.lentByCells(INDUSTRY) + r.ex.lentByFund(INDUSTRY), 0, 1e-9);
        close("...and the holdings are the shares in issue", r.holdings(), SHARES, 1e-9 * SHARES);

        // A company with no shares left: the short is closed and keeps what it was paid.
        Bench gone = standard();
        gone.open(Exchange.DESK, gone.shortAsk(), 40);
        assertTrue("fixture: 40 shares short before the company's shares are all retired", gone.shortShares() == 40);
        gone.reg.retire(INDUSTRY, gone.reg.getShares(INDUSTRY), 0);
        gone.city.step(0);
        assertTrue("a company with no shares left while the world is short of it: the short is closed, its collateral and its lenders with it",
                gone.shortShares() == 0 && gone.ex.shortCollateral(INDUSTRY) == 0 && gone.ex.lentByCells(INDUSTRY) == 0 && gone.ex.lentByFund(INDUSTRY) == 0);

        // A split moves the shares short by the factor and leaves the money.
        Bench split = standard();
        // SX1: this fixture's fair quote starts at the wage band's middle.
        split.city.firms.wage = split.fair / Math.sqrt(Exchange.SPLIT_AT * Exchange.CONSOLIDATE_BELOW);
        split.open(Exchange.DESK, split.shortAsk(), 40);
        double collateralSplit = split.ex.shortCollateral(INDUSTRY), lentSplit = split.ex.lentByFund(INDUSTRY);
        split.print(10_000 * split.fair);
        split.ex.splitForCheck();
        double k = split.ex.getSplit(INDUSTRY);
        double high = 10_000 * split.fair;
        double expectedSplit = Math.pow(10, Math.floor(Math.log10(high
                / (split.city.firms.wage * Math.sqrt(Exchange.SPLIT_AT * Exchange.CONSOLIDATE_BELOW)))));
        assertTrue("fixture: the print at 10,000 times fair value crosses the wage dial",
                high > Exchange.SPLIT_AT * split.city.firms.wage);
        close("the wage-triggered split is the derived power of ten", k, expectedSplit, 1e-12);
        assertTrue("fixture: the print actually split the shares", k > 1);
        close("a split moves the shares short by the factor, as every share count", split.shortShares(), 40 * k, 1e-9 * k);
        close("...and the shares lent by the fund", split.ex.lentByFund(INDUSTRY), lentSplit * k, 1e-9 * k);
        close("...the collateral, which is money, stays", split.ex.shortCollateral(INDUSTRY), collateralSplit, 1e-9);
        close("...and the holdings still add to the shares in issue plus what is short", split.holdings(), split.reg.getShares(INDUSTRY) + split.shortShares(), 1e-6 * k);

        theCity();
        sectionEnd();
    }

    /**
     * The city with the owner line off (BO1-S): this harness's subject is the shorts, and its towns are young - under
     * OwnerHousehold.OWNER_ADULT_SHARE a founding town of a few adults has an empty owner cell, so its companies would wait to
     * list and no household would hold a share to lend. The line's own claims are OwnerCheck's.
     */
    static Game lineOff(Game g) { g.getHouseholdBalance().setOwnerLine(false); return g; }

    /** The playtest's town, as FundLedgerCheck builds it, `months` months old. */
    static Game town(String label, int months) {
        Path root;
        try { root = TempDirs.create(label); } catch (java.io.IOException e) { throw new IllegalStateException(e); }
        Game g = lineOff(new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy"))));
        PrintStream real = System.out;
        System.setOut(quiet);
        try {
            g.run();
            g.getForeignAccounts().pinRate(1.0);
            g.buildStack(template(g, "Gravel Road"), 6, true);
            g.buildStack(template(g, "House"), 300, false);
            g.buildStack(template(g, "Convenience Store"), 6, false);
            g.buildStack(template(g, "Industrial Bakery"), 2, false);
            g.buildStack(template(g, "Construction Depot"), 4, false);
            g.buildStack(template(g, "Coal Power Plant"), 1, false);
            g.simulateMonths(months);
        } finally {
            System.setOut(real);
        }
        return g;
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    /** A town with the world short of one company (openShort()): the town, and the company's index. */
    static final class Shorted {
        final Game city;
        final int firm;
        Shorted(Game city, int firm) { this.city = city; this.firm = firm; }
    }

    /**
     * The playtest's town, 48 months old, with the world short of the company that pays a dividend and has most in the households' hands: the
     * fund comes to hold 4% of it, bought from the cell that holds most, and the world sells short nine tenths of what the lenders could lend,
     * to the desk. Null, with its assertion failed, when the town has no such company.
     */
    static Shorted openShort(String label) {
        Game city = town(label, 48);
        Equity cityReg = city.getEquity();
        Exchange cityEx = city.getExchange();
        int firm = -1;
        double most = 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (c == Equity.BANK || !cityReg.valuedOnRecord(c) || !(cityReg.getDividendsPaidOverYear(c) > 0)) continue;
            double held = city.getHouseholdBalance().sharesHeld(c) * cityEx.fair(c);
            if (held > most) { most = held; firm = c; }
        }
        assertTrue("fixture: a company valued on its record, paying a dividend, whose shares the households hold", firm >= 0);
        if (firm < 0) return null;
        final int f = firm;
        Household lender = null;
        for (Household h : city.getHouseholdBalance().cells()) {
            if (h.households() > 0 && (lender == null || h.shares[f] * h.households() > lender.shares[f] * lender.households())) lender = h;
        }
        double fairCity = cityEx.fair(f);
        cityEx.bookOf(f).withdrawAll();
        city.getFund().receive(10 * cityReg.getShares(f) * fairCity);
        // The fund comes to hold 4% of the company, bought from the cell that holds the most of it...
        double fundBuys = .04 * cityReg.getShares(f);
        String lenderName = Exchange.CELL + lender.key();
        cityEx.tradeForCheck(f, lenderName, OrderBook.Side.SELL, fairCity, fundBuys);
        double fundGot = filled(cityEx.tradeForCheck(f, Exchange.FUND_HAND, OrderBook.Side.BUY, fairCity, fundBuys));
        // ...and the world sells short nine tenths of what the lenders could lend, to the desk.
        double room = Math.min(Exchange.MAX_SHORT_INTEREST * cityReg.getOutstanding(f), city.getHouseholdBalance().sharesHeld(f) + cityReg.getCityMarketShares(f));
        double want = .9 * room;
        double at = cityEx.fair(f) * (1 + Exchange.SHORT_BAND);
        cityEx.tradeForCheck(f, Exchange.WORLD_SHORT, OrderBook.Side.SELL, at, want);
        double sold = filled(cityEx.tradeForCheck(f, Exchange.DESK, OrderBook.Side.BUY, at, want));
        cityEx.bookOf(f).withdrawAll();
        assertTrue("fixture: the fund holds some of " + Equity.COMPANIES[f] + " and the world is short of it, to a desk with the means to buy",
                fundGot > 0 && sold > 0 && cityEx.shortShares(f) == sold);
        out.printf("   %s: %,.0f shares in issue, the fund holds %,.0f, short %,.0f (%.2f%% of the shares outstanding)%n", Equity.COMPANIES[f],
                cityReg.getShares(f), cityReg.getCityMarketShares(f), cityEx.shortShares(f), 100 * cityEx.shortShares(f) / cityReg.getOutstanding(f));
        return fundGot > 0 && sold > 0 && cityEx.shortShares(f) == sold ? new Shorted(city, f) : null;
    }

    /** A whole month of a city, with a short open, a dividend paid on it and the lending fee: the audit balances. */
    static void theCity() {
        out.println("   -- a city with a short open through its dividend and its lending fee --");
        Shorted opened = openShort("shortsellingcheck");
        if (opened == null) return;
        Game city = opened.city;
        Equity cityReg = city.getEquity();
        Exchange cityEx = city.getExchange();
        final int f = opened.firm;
        boolean paid = false, named = false;
        int audited = 0, bad = 0;
        double worst = 0, feeFund = 0, feeCells = 0;
        // ...and the lending fee on the lenders' books (LF1): the audit's lines for it, the fund's counters and its ledger's rows, month by month.
        double auditCells = 0, auditBanked = 0, auditFund = 0, fundMonths = 0, incomeShort = 0;
        boolean incomeSeen = true, fundBooked = true;
        TreasuryFund cityFund = city.getFund();
        for (int m = 0; m < 6 && !(paid && audited >= 3); m++) {
            PrintStream real = System.out;
            System.setOut(quiet);
            try { city.simulateMonths(1); } finally { System.setOut(real); }
            MoneyAudit.Result a = city.getLastMoneyAudit();
            audited++;
            worst = Math.max(worst, Math.abs(a.residual));
            // ...held to a tenth of a cent here, tighter than the playtest's own floor: the lending fee is cents a month in this town.
            if (Math.abs(a.residual) > .001) bad++;
            double pays = cityReg.getShortPaysThisMonth(f);
            if (pays > 0) {
                paid = true;
                named |= a.detail.contains("DividendsPaidByShorts");
                assertTrue("a month the company paid its dividend with a short open: the short paid " + String.format("%,.2f", pays)
                        + ", more than the audit's own floor, so a month that left the line out would show it", pays > MoneyAudit.CENT && pays / Math.max(1, a.moved()) > 1e-7);
            }
            feeFund += cityEx.getLendingFeeToFund();
            feeCells += cityEx.getLendingFeeToCells();
            auditCells += lineOf(a.detail, "+ households LendingFee");
            auditBanked += lineOf(a.detail, "- households LendingFeeBanked");
            auditFund += lineOf(a.detail, "+ fund LendingFee");
            // The current account's receipts (ForeignAccounts reads incomeIn) hold both fees, not the fund's alone.
            incomeSeen &= a.incomeIn + MoneyAudit.CENT >= cityEx.getLendingFeeToCells() + cityEx.getLendingFeeToFund();
            fundMonths += cityFund.getMonthLendingFees();
            fundBooked &= Math.abs(cityFund.getMonthLendingFees() - cityEx.getLendingFeeToFund()) <= 1e-9 * Math.max(1, cityEx.getLendingFeeToFund());
        }
        assertTrue("every month of the city's with the short open passes the audit: worst residual " + String.format("%.6f", worst) + " over " + audited
                + " months", bad == 0 && audited > 0);
        assertTrue("...the dividend was paid on the short, and the audit names its line", paid && named);
        assertTrue("...and the lending fee reached the fund's cash and the cells' savings: $" + String.format("%,.2f", feeFund) + " and $"
                + String.format("%,.2f", feeCells), feeFund > 0 && feeCells > 0);
        // LF1: neither receiver's fee was on any line of its own.
        double lineTol = .005 * audited + 1e-9;
        out.printf("   the months' fees: the cells' $%,.4f, the fund's $%,.4f; the audit's lines $%,.2f, $%,.2f and $%,.2f%n", feeCells, feeFund, auditCells, auditBanked, auditFund);
        close("the households' fee is a line of the audit, income from the world (rounded to the cent a month)", auditCells, feeCells, lineTol);
        close("...paired with its debit, the money banked, the same size", auditBanked, auditCells, 1e-9);
        close("...and the fund's line is the fund's part", auditFund, feeFund, lineTol);
        assertTrue("...the current account's receipts hold both fees every month", incomeSeen);
        assertTrue("the fund's own counter for the month is what the exchange says it was paid, every month", fundBooked);
        close("...and its life's is all of them", cityFund.getLendingFees(), fundMonths, 1e-9 * Math.max(1, fundMonths));
        double rows = 0;
        for (FundLedger.Activity row : cityFund.getLedger().getActivity()) if (FundLedger.LENDING_FEE.equals(row.kind())) rows += row.money();
        close("...and the ledger has its rows, a LENDING_FEE a month, which add to it", rows, cityFund.getLendingFees(), 1e-9 * Math.max(1, fundMonths));
        assertTrue("...the fund's page files them under income, with its dividends and coupons", FundView.INCOME.equals(FundView.groupOf(FundLedger.LENDING_FEE)));
        FundView.Portfolio view = FundView.portfolio(city);
        close("...its income last month holds the fee beside its dividends and coupons", view.incomeLastMonth(),
                cityFund.getMonthDividends() + cityFund.getMonthCoupons() + cityFund.getMonthLendingFees(), 1e-9 * Math.max(1, view.incomeLastMonth()));
        FundView.Kind shares = view.kinds().get(0);
        assertTrue("...and the company shares' income, since it began, holds it too: " + shares.name(),
                shares.income() >= cityFund.getDividendsMarket() + cityFund.getLendingFees() - 1e-9);
        theWipe(city, f);
        theWords(city, f, rows);
    }

    /**
     * A company with no shares in issue and no book, with a short open and the world holding some of it, in the real month (LF1c): the month's
     * listing finds no book and leaves it unlisted, the exchange's step cancels what the short sold and takes the world's shares and the short
     * across the border for nothing, and the audit declares both as valuations - the wipe in the city's favour, the short closed against it - so
     * ForeignAccounts' forgiven counts them, and the month still foots. A company the town never listed is the one a real month leaves so.
     */
    static void theWipe(Game city, int firm) {
        out.println("   -- a company with no shares and no book, a short open and the world holding some of it: the month wipes both and says so --");
        Equity reg = city.getEquity();
        Exchange ex = city.getExchange();
        int u = -1;
        for (int c = 0; c < Equity.COMPANIES.length && u < 0; c++) {
            if (c != Equity.BANK && c != firm && !(reg.getShares(c) > 0) && !(reg.getForeignShares(c) > 0)) u = c;
        }
        assertTrue("fixture: the town has a company it never listed, with no shares in issue and none held abroad", u >= 0);
        if (u < 0) return;
        // The short is sold while the company has shares and holders to lend them (Exchange.shortRoom()); the book it is listed against is the
        // fixture's, and the month's own listing finds the town's, which is nothing. Then every share is retired: the company is left with none.
        HouseholdBalance hb = city.getHouseholdBalance();
        assertTrue("fixture: " + Equity.COMPANIES[u] + " is listed, by hand, against a book of 5,000 and its founders", reg.listIfUnlisted(u, 5_000, hb));
        double q = ex.fair(u), founded = reg.getShares(u), asked = .05 * founded;
        // ...a cell with savings buys what the short sells; the founders' cell holds the shares in issue.
        Household founders = null, buyer = null;
        for (Household h : hb.cells()) {
            if (h.households() > 0 && h.shares[u] > 0 && (founders == null || h.shares[u] * h.households() > founders.shares[u] * founders.households())) founders = h;
        }
        for (Household h : hb.cells()) {
            if (h != founders && h.households() > 0 && (buyer == null || h.savings * h.households() > buyer.savings * buyer.households())) buyer = h;
        }
        assertTrue("fixture: a cell holds the founders' shares and a cell has savings to buy a short", founders != null && buyer != null && buyer != founders);
        if (founders == null || buyer == null || buyer == founders) return;
        ex.tradeForCheck(u, Exchange.WORLD_SHORT, OrderBook.Side.SELL, q, asked);
        double sold = filled(ex.tradeForCheck(u, Exchange.CELL + buyer.key(), OrderBook.Side.BUY, q, asked));
        ex.bookOf(u).withdrawAll();
        assertTrue("fixture: the register is whole with the short open: the cells hold the shares in issue plus the short, " + String.format("%,.0f", hb.sharesHeld(u)),
                sold > 0 && ex.shortShares(u) == sold && Math.abs(hb.sharesHeld(u) - (founded + sold)) <= 1e-6 * founded);
        // Every share in issue is bought from the founders and retired; the buyer sells half of what the short sold it to the world and the rest to the
        // desk, so no cell holds any to carry abroad this month (edge 2) and the holdings still add to the shares in issue, none, plus the short.
        reg.retire(u, founded, 0);
        double abroad = .5 * sold;
        for (Household h : hb.cells()) h.shares[u] = 0;
        reg.moveForeign(u, abroad);
        reg.moveDesk(u, sold - abroad);
        ex.tradeForCheck(u, Exchange.WORLD, OrderBook.Side.BUY, 1.5 * q, 1);   // the month-end quote over the last trade: the one the figures are struck at
        double quoted = ex.price(u);
        assertTrue("fixture: " + Equity.COMPANIES[u] + " has no shares in issue, the world holds " + String.format("%,.0f", abroad) + " of what a short sold " + String.format("%,.0f", sold)
                + " and the desk the rest, at $" + String.format("%.4f", quoted) + " over the last trade's", reg.getShares(u) == 0 && reg.getForeignShares(u) == abroad && quoted > 1.4 * q
                && hb.sharesHeld(u) == 0 && Math.abs(reg.getDealerShares(u) + abroad - sold) <= 1e-6 * sold);
        ForeignAccounts fx = city.getForeignAccounts();
        double forgivenBefore = fx.getForgiven();
        PrintStream real = System.out;
        System.setOut(quiet);
        try { city.simulateMonths(1); } finally { System.setOut(real); }
        MoneyAudit.Result a = city.getLastMoneyAudit();
        assertTrue("the short is closed, what it sold gone with the company, the desk's and the world's shares with them", ex.shortShares(u) == 0
                && reg.getDealerShares(u) == 0 && reg.getForeignShares(u) == 0 && reg.getShares(u) == 0);
        close("the exchange's wipe is the world's shares at the quote", ex.getWorldSharesWiped(), abroad * quoted, 1e-9 * abroad * quoted);
        close("...and its closed short the shares it owed at the quote", ex.getShortClosedForNothing(), sold * quoted, 1e-9 * sold * quoted);
        assertTrue("fixture: both are above the audit's cent, so a line that was left out would show", ex.getWorldSharesWiped() > MoneyAudit.CENT && ex.getShortClosedForNothing() > MoneyAudit.CENT);
        out.printf("   the wipe $%,.2f in the city's favour, the short $%,.2f against it; the audit's residual %.6f%n", ex.getWorldSharesWiped(), ex.getShortClosedForNothing(), a.residual);
        assertTrue("the month foots with both declared: residual " + String.format("%.6f", a.residual), Math.abs(a.residual) <= .001);
        // HL1: both valuation offsets name no cash; classifying either DOMESTIC would invent a household receipt or payment.
        MoneyAudit.Households households = city.getLastHouseholdsAudit();
        assertTrue("the households' cash identity closes through the noncash wipe and short closure: " + households,
                LongPlaytest.MoneyFixtures.householdsClose(households));
        double lineIn = a.figures.getOrDefault("+ desk WorldSharesWiped", 0.0), lineOut = a.figures.getOrDefault("- desk ShortClosedForNothing", 0.0);
        close("the audit's wipe line is the exchange's figure, a valuation in the city's favour", lineIn, ex.getWorldSharesWiped(), 1e-9);
        close("...and its closed-short line, a valuation against it", lineOut, ex.getShortClosedForNothing(), 1e-9);
        close("...each with its half that moves no cash: the wipe's debit", a.figures.getOrDefault("- desk WorldSharesWiped (no cash)", 0.0), lineIn, 1e-9);
        close("...and the closed short's credit", a.figures.getOrDefault("+ desk ShortClosedForNothing (no cash)", 0.0), lineOut, 1e-9);
        close("the balance of payments' valuation in is the wipe: nothing else was written off that month", a.valuationIn, ex.getWorldSharesWiped(), MoneyAudit.CENT);
        close("...and its valuation out the closed short", a.valuationOut, ex.getShortClosedForNothing(), MoneyAudit.CENT);
        close("ForeignAccounts' forgiven counts the two: it moved by the wipe less the short closed", fx.getForgiven() - forgivenBefore, ex.getWorldSharesWiped() - ex.getShortClosedForNothing(), MoneyAudit.CENT);
        // ...and the Trade screen says what that month was: a valuation against the city, not a gain (the words said a gain only).
        assertTrue("fixture: the month's valuation nets under nothing, the short closed being more than the world's shares wiped: " + String.format("%.2f", fx.valuationChange()),
                fx.valuationChange() < -MoneyAudit.CENT);
        String[] loss = KeptBench.tradeValuationWords(fx.valuationChange(), "D$" + String.format("%.2f", -fx.valuationChange()));
        assertTrue("the Trade screen's chip for it says the claims were lost, not written off in the city's favour: " + loss[0], loss[0].startsWith("claims lost abroad ")
                && !loss[1].contains("improves") && loss[1].contains("against the city") && !loss[2].contains("improves the position") && loss[2].contains("against the city"));
        String[] gain = KeptBench.tradeValuationWords(5, "D$5");
        assertTrue("...a month that wrote claims off in its favour still says it improves what the city owes: " + gain[0], gain[0].equals("claims written off D$5")
                && gain[1].contains("improves what the city owes") && gain[2].contains("improves the position"));
        assertTrue("...and the chip for what was written off since founding says forgiven or settled for a gain and against the city for a loss",
                KeptBench.tradeForgivenWords(3, "D$3")[0].equals("forgiven or settled D$3") && KeptBench.tradeForgivenWords(-3, "D$3")[0].equals("written off against the city D$3")
                        && !KeptBench.tradeForgivenWords(-3, "D$3")[1].contains("improved"));
    }

    /**
     * The Fund screen's words for what the short's lending fee added to the fund's record (LF1c): the row of the month's fees says what it is
     * instead of its kind's name, a share's write-off is not a bond's ("its face gone"), and the income a share lot's page holds is named for
     * what is in it - dividends and the lending fees, where it said dividends. Read off the city's own fund, through the screen's own words.
     */
    static void theWords(Game city, int firm, double feesRowed) {
        out.println("   -- the Fund screen's words: a fee's row, a share's write-off, the income that holds the fees --");
        TreasuryFund fund = city.getFund();
        int feeRows = 0;
        for (FundLedger.Activity row : fund.getLedger().getActivity()) if (FundLedger.LENDING_FEE.equals(row.kind())) feeRows++;
        List<String> words = KeptBench.fundActivityWords(city);
        long feeLines = words.stream().filter(w -> w.startsWith("Lending fees from the world on the shares lent to a short in ")).count();
        assertTrue("fixture: the fund was paid fees (" + String.format("%,.2f", feesRowed) + ") in " + feeRows + " months", feesRowed > 0 && feeRows > 0);
        assertTrue("the Activity page has a line in words for each month's fees: " + feeLines + " of " + feeRows, feeLines == feeRows);
        assertTrue("...and none says the row's kind, " + FundLedger.LENDING_FEE, words.stream().noneMatch(w -> w.contains(FundLedger.LENDING_FEE)));
        String shareKey = FundLedger.shareKey(Equity.COMPANIES[firm]);
        List<String[]> position = KeptBench.fundPositionLines(city, shareKey);
        boolean named = position.stream().anyMatch(l -> l[0].equals(KeptBench.fundIncomeLabel(FundView.SHARE)));
        assertTrue("the market book's share lot, which the fees are credited to, labels its income for what is in it: " + KeptBench.fundIncomeLabel(FundView.SHARE),
                named && KeptBench.fundIncomeLabel(FundView.SHARE).contains("lending fees") && position.stream().noneMatch(l -> l[0].equals("Dividends")));
        assertTrue("...a bond's stays coupons, and the rescue book's, which no fee is credited to (FundLedger.lendingFee()), dividends",
                "Coupons".equals(KeptBench.fundIncomeLabel(FundView.BOND)) && "Dividends".equals(KeptBench.fundIncomeLabel(FundView.RESCUE))
                        && "Dividends".equals(KeptBench.fundIncomeLabel(FundView.PREFERRED)));
        FundLedger.Lot rescue = fund.getLedger().lot(FundLedger.rescueKey(Equity.COMPANIES[firm]));
        assertTrue("fixture: the company's rescue book holds no income of the fees: it holds none at all", rescue == null || rescue.income() == 0);
        FundView.Portfolio view = FundView.portfolio(city);
        assertTrue("the income card says what the city's fund's income covered, as its own note does", KeptBench.fundHeroIncomeNote(city)
                .equals(KeptBench.fundIncomeNote(view.transferLastMonth(), view.incomeLastMonth())));
        String note = KeptBench.fundIncomeNote(0, view.incomeLastMonth());
        assertTrue("...and with no transfer to cover it says what the income is made of, the lending fees beside the dividends and coupons: " + note,
                note.contains("dividends") && note.contains("coupons") && note.contains("lending fees"));
        assertTrue("...but with one it says how much of it the income covered: " + KeptBench.fundIncomeNote(100, 50), KeptBench.fundIncomeNote(100, 50).equals("covered 50% of the transfer"));
        // A company's shares written off with it, as Exchange.cancelShortHeld() does it (FundLedger.sharesGone()).
        fund.getLedger().sharesGone(Equity.COMPANIES[firm], city.getMonth());
        words = KeptBench.fundActivityWords(city);
        String shareWords = KeptBench.fundWrittenOffWords(Equity.COMPANIES[firm], false);
        assertTrue("a share's write-off reads on the page as the shares going with the company: " + shareWords, words.contains(shareWords) && shareWords.contains("shares gone with the company")
                && !shareWords.contains("face"));
        assertTrue("...a bond's keeps its face", KeptBench.fundWrittenOffWords("a bond", true).contains("its face gone"));
    }

    /** The number a line of the audit's detail ends in (its label, then the amount at two places), 0 when the month has no such line. */
    static double lineOf(String detail, String label) {
        for (String line : detail.split("\\R")) {
            String t = line.strip();
            if (!t.startsWith(label + " ") && !t.equals(label)) continue;
            String num = t.substring(t.lastIndexOf(' ') + 1).replace(",", "");
            try { return Double.parseDouble(num); } catch (NumberFormatException e) { return 0; }
        }
        return 0;
    }

    static void theLender() {
        section("4. the lender is paid: the dividend on every share it holds, the short's on what is short, and the lending fee on the quote");

        // The dividend. The same company, once with nothing short and once with 100 short to the desk.
        double dividend = 1_000;
        Bench plain = standard();
        plain.reg.payDividend(INDUSTRY, dividend, plain.hb, 0);
        Bench shorted = standard();
        shorted.open(Exchange.DESK, shorted.shortAsk(), 100);
        double s = shorted.shortShares();
        assertTrue("fixture: the desk holds the 100 shares the world is short of, and the holdings are the shares in issue plus 100",
                s == 100 && shorted.reg.getDealerShares(INDUSTRY) == 100 && Math.abs(shorted.holdings() - (SHARES + s)) < 1e-9);
        shorted.reg.payDividend(INDUSTRY, dividend, shorted.hb, s);
        double perShare = dividend / SHARES;
        close("with nothing short the cell is paid the company's dividend on every share it holds", plain.poor.dividends(),
                MarketCheck.heldBy(plain.poor, INDUSTRY) / plain.poor.households() * perShare, 1e-9);
        close("...and with 100 short, the same: the company's dividend per share on its shares in issue", shorted.poor.dividends(), plain.poor.dividends(), 1e-9);
        close("...the lender kept the shares it lent, so was paid on all of them", shorted.poor.dividends(),
                MarketCheck.heldBy(shorted.poor, INDUSTRY) / shorted.poor.households() * perShare, 1e-9);
        close("the fund is paid the same either way", shorted.reg.getDividendCityThisMonth(INDUSTRY), plain.reg.getDividendCityThisMonth(INDUSTRY), 1e-9);
        close("...and the world on its own long", shorted.reg.getDividendAbroadThisMonth(INDUSTRY), plain.reg.getDividendAbroadThisMonth(INDUSTRY), 1e-9);
        close("the short pays the dividend per share on the shares it is short", shorted.reg.getShortPaysThisMonth(INDUSTRY), perShare * s, 1e-9);
        close("...the desk, which holds them, is paid it", shorted.reg.getDividendDeskThisMonth(INDUSTRY), perShare * s, 1e-9);
        close("...every holder was paid, the company's dividend and the short's together",
                shorted.reg.getDividendHomeThisMonth(INDUSTRY) + shorted.reg.getDividendDeskThisMonth(INDUSTRY) + shorted.reg.getDividendCityThisMonth(INDUSTRY)
                        + shorted.reg.getDividendAbroadThisMonth(INDUSTRY), dividend + perShare * s, 1e-9);
        close("...and with nothing short the short paid nothing", plain.reg.getShortPaysThisMonth(INDUSTRY), 0, 0);

        // The fee, on the quote the book stood at before the step. 700 and 100 can be lent, 100 are short: well under HARD_TO_BORROW.
        Bench feed = standard();
        feed.poor.lockout = 1;                                  // both cells locked out of buying, so the fee is all that reaches their savings
        feed.rich.lockout = 1;
        feed.open(Exchange.DESK, feed.shortAsk(), 100);
        double lendable = feed.hb.sharesHeld(INDUSTRY) + feed.reg.getCityMarketShares(INDUSTRY);
        double u = feed.shortShares() / lendable;
        double q0 = feed.price();
        double cash0 = feed.fund.getCash(), poorSavings0 = feed.poor.savings(), richSavings0 = feed.rich.savings();
        double poorIncome0 = feed.poor.investmentIncome, cellsHeld = feed.hb.sharesHeld(INDUSTRY), fundHeld = feed.reg.getCityMarketShares(INDUSTRY);
        assertTrue("fixture: the short is " + String.format("%.1f%%", 100 * u) + " of what can be lent, under HARD_TO_BORROW", u <= Exchange.HARD_TO_BORROW);
        feed.city.step(0);
        assertTrue("fixture: the quote moved at the step, so the fee's price is the one the book stood at before it", Math.abs(feed.price() - q0) > 1e-6 * q0);
        double rate = Exchange.LENDING_FEE;
        double fee = rate * 100 * q0 / 12;
        close("the world pays the lenders LENDING_FEE a year of the shares lent at the quote before the step, a twelfth a month",
                feed.ex.lendingFeeThisMonth(INDUSTRY), fee, 1e-9 * fee);
        close("...the fund's part pro rata to what it holds of what can be lent", feed.ex.lendingFeeToFund(INDUSTRY), fee * fundHeld / lendable, 1e-9 * fee);
        close("...and the cells', the rest", feed.ex.lendingFeeToCells(INDUSTRY), fee * cellsHeld / lendable, 1e-9 * fee);
        close("...the fund's cash rose by its part", feed.fund.getCash() - cash0, fee * fundHeld / lendable, 1e-6);
        double poorGets = fee * cellsHeld / lendable * MarketCheck.heldBy(feed.poor, INDUSTRY) / cellsHeld / feed.poor.households();
        close("...a cell's savings by its part, per household, pro rata to what it holds", feed.poor.savings() - poorSavings0, poorGets, 1e-9 * fee);
        close("...the other cell the same per share it holds", (feed.rich.savings() - richSavings0) * feed.rich.households() / MarketCheck.heldBy(feed.rich, INDUSTRY),
                (feed.poor.savings() - poorSavings0) * feed.poor.households() / MarketCheck.heldBy(feed.poor, INDUSTRY), 1e-12);
        close("...as investment income, which it plans on", feed.poor.investmentIncome - poorIncome0, poorGets, 1e-9 * fee);
        close("...and not as a dividend, which is a company's: it paid nothing", feed.poor.dividends(), 0, 0);
        assertTrue("...counted over the city's life", Math.abs(feed.ex.getLifeLendingFees() - fee) < 1e-9 * fee);

        // Hard to borrow: 250 can be lent, 200 are short: past the line the fee a year rises with the utilisation.
        Bench hard = new Bench(100, 100, 750, 50, 10_000_000);
        hard.open(Exchange.DESK, hard.shortAsk(), 200);
        double lendableHard = hard.hb.sharesHeld(INDUSTRY) + hard.reg.getCityMarketShares(INDUSTRY);
        double uHard = hard.shortShares() / lendableHard;
        double qHard = hard.price();
        assertTrue("fixture: 200 short of 250 that can be lent: " + String.format("%.0f%%", 100 * uHard) + ", past HARD_TO_BORROW", uHard > Exchange.HARD_TO_BORROW && uHard < 1);
        hard.city.step(0);
        double rateHard = rateAt(uHard);
        close("hard to borrow, the fee a year is LENDING_FEE plus HARD_TO_BORROW_FEE of the way from HARD_TO_BORROW to all lent",
                hard.ex.lendingFeeThisMonth(INDUSTRY), rateHard * 200 * qHard / 12, 1e-9 * rateHard * 200 * qHard / 12);
        assertTrue("...which is more a share lent than at the general rate (" + String.format("%.2f%% against %.2f%%", 100 * rateHard, 100 * rate) + ")",
                rateHard > rate && hard.ex.lendingFeeThisMonth(INDUSTRY) / (200 * qHard) > feed.ex.lendingFeeThisMonth(INDUSTRY) / (100 * q0));
        close("the rate at HARD_TO_BORROW is the general rate", Exchange.lendingRate(Exchange.HARD_TO_BORROW), Exchange.LENDING_FEE, 1e-15);
        close("...at all of it lent, the general rate and the whole of HARD_TO_BORROW_FEE", Exchange.lendingRate(1), Exchange.LENDING_FEE + Exchange.HARD_TO_BORROW_FEE, 1e-15);
        close("...halfway between, half of it", Exchange.lendingRate((1 + Exchange.HARD_TO_BORROW) / 2), Exchange.LENDING_FEE + Exchange.HARD_TO_BORROW_FEE / 2, 1e-15);
        close("...and no more past all of it lent", Exchange.lendingRate(3), Exchange.lendingRate(1), 0);
        close("...the dial's own reading of the fixtures", Exchange.lendingRate(uHard), rateHard, 1e-15);
        sectionEnd();
    }

    static void theCalls() {
        section("5. a margin call makes the short cover, at the most it can pay; a recall covers what the lenders can no longer lend");

        // The margin call. 50 short, sold at fair value plus SHORT_BAND to a cell; no world long and no desk inventory, so a print is the quote.
        Bench call = new Bench(300, 600, 0, 100, 10_000_000);
        call.rich.savings = 5_000;
        call.open(call.richName(), call.shortAsk(), 50);
        call.rich.savings = 12;
        double s = call.shortShares(), collateral = call.ex.shortCollateral(INDUSTRY);
        double callAt = collateral / ((1 + Exchange.MAINTENANCE_MARGIN) * s);
        double breakPrice = collateral / s;
        out.printf("   50 short at %.3f times fair value: the account is called once the quote passes %.3f times it, and can pay %.3f times at most%n",
                1 + Exchange.SHORT_BAND, callAt / call.fair, breakPrice / call.fair);
        assertTrue("fixture: short 50, with INITIAL_MARGIN times the sale held, and the call line over the price it sold at, under the break price",
                s == 50 && Math.abs(collateral - Exchange.INITIAL_MARGIN * 50 * call.shortAsk()) < 1e-9 * collateral
                        && callAt > call.shortAsk() && callAt < breakPrice);

        // Control: a quote under the line is no call; the short, over SHORT_BAND still, sells more.
        Bench calm = new Bench(300, 600, 0, 100, 10_000_000);
        calm.rich.savings = 5_000;
        calm.open(calm.richName(), calm.shortAsk(), 50);
        calm.rich.savings = 12;
        double under = (calm.shortAsk() + callAt) / 2;
        calm.print(under);
        assertTrue("control: a print between the ask it sold at and the call line (" + String.format("%.3f", under / calm.fair)
                        + " times fair value) is a quote over SHORT_BAND and under the call",
                calm.price() == under && under > calm.shortAsk() && under < callAt);
        calm.city.step(0);
        assertTrue("...so no margin call: none counted, nothing bid by the short, and it asks to sell more at its band",
                calm.ex.getLifeMarginCalls() == 0 && calm.ex.bookOf(INDUSTRY).resting(Exchange.WORLD_SHORT, OrderBook.Side.BUY) == 0
                        && calm.ex.bestAskOf(INDUSTRY, Exchange.WORLD_SHORT) == calm.shortAsk());

        // The call: a print over the line.
        double over = callAt * 1.05;
        call.print(over);
        assertTrue("a print at " + String.format("%.3f", over / call.fair) + " times fair value is the quote, and the account's equity "
                        + String.format("%.2f", collateral - s * call.price()) + " is under " + String.format("%.0f%%", 100 * Exchange.MAINTENANCE_MARGIN)
                        + " of the short's value " + String.format("%.2f", Exchange.MAINTENANCE_MARGIN * s * call.price()),
                call.price() == over && collateral - s * over < Exchange.MAINTENANCE_MARGIN * s * over);
        double heldBefore = MarketCheck.heldBy(call.poor, INDUSTRY) + MarketCheck.heldBy(call.rich, INDUSTRY) + call.reg.getCityMarketShares(INDUSTRY);
        call.city.step(0);
        assertTrue("the next step counts the margin call", call.ex.getLifeMarginCalls() == 1 && call.ex.getLifeRecalls() == 0);
        // The bid is the highest on the book and over value by far: every holder sells into it (the households' and the fund's rule for a bid over value).
        close("...the short bid for all it is short at the most it can pay, and the holders sold into the bid at once: it has covered all of it",
                call.ex.getLifeShortCovered(), s, 1e-9);
        close("...every share at the quote it bid at, the market, and not the break price (the collateral over the shares): a buy-in at the market is the squeeze",
                call.ex.getLifeShortCoveredValue(), s * over, 1e-9 * s * over);
        assertTrue("...which is under the break price, so the account kept some of its collateral's worth", over < breakPrice);
        assertTrue("...so it is short nothing, holds no collateral and has nothing resting, and the holders hold " + String.format("%.0f", s) + " fewer shares",
                call.shortShares() == 0 && call.ex.shortCollateral(INDUSTRY) == 0 && call.ex.bookOf(INDUSTRY).resting(Exchange.WORLD_SHORT, OrderBook.Side.BUY) == 0
                        && Math.abs(heldBefore - (MarketCheck.heldBy(call.poor, INDUSTRY) + MarketCheck.heldBy(call.rich, INDUSTRY)
                        + call.reg.getCityMarketShares(INDUSTRY)) - s) < 1e-9);
        close("...and the holdings are the shares in issue again", call.holdings(), SHARES, 1e-9 * SHARES);

        // The recall. The lenders hold fewer shares than are short: the cells sell theirs, the fund keeps 50.
        Bench recall = new Bench(40, 110, 800, 50, 10_000_000);
        recall.rich.savings = 5_000;
        recall.open(recall.richName(), recall.shortAsk(), 60);
        recall.rich.savings = 12;
        double sr = recall.shortShares(), collateralR = recall.ex.shortCollateral(INDUSTRY);
        for (Household cell : new Household[] { recall.poor, recall.rich }) {
            double q = MarketCheck.heldBy(cell, INDUSTRY);
            recall.ex.tradeForCheck(INDUSTRY, Exchange.WORLD, OrderBook.Side.BUY, recall.fair, q);
            recall.ex.tradeForCheck(INDUSTRY, MarketCheck.nameOf(cell), OrderBook.Side.SELL, recall.fair, q);
        }
        recall.ex.bookOf(INDUSTRY).withdrawAll();
        double lendableR = recall.hb.sharesHeld(INDUSTRY) + recall.reg.getCityMarketShares(INDUSTRY);
        assertTrue("fixture: 60 short, and the lenders hold " + String.format("%.0f", lendableR) + " between them after the cells sold, under what is short",
                sr == 60 && lendableR == 50 && lendableR < sr);
        assertTrue("fixture: the quote is fair value, so no margin call", recall.price() == recall.fair
                && collateralR - sr * recall.price() >= Exchange.MAINTENANCE_MARGIN * sr * recall.price());
        recall.city.step(0);
        assertTrue("the next step counts the recall, and no margin call", recall.ex.getLifeRecalls() == 1 && recall.ex.getLifeMarginCalls() == 0);
        // The recall is a buy-in at the market: with the quote at fair value its bid rests there, under the break price, and nobody is paid more.
        close("...the short bids for what is short over what can be lent", recall.ex.bookOf(INDUSTRY).resting(Exchange.WORLD_SHORT, OrderBook.Side.BUY),
                sr - lendableR, 1e-9);
        close("...at the market, fair value here, and not at the break price", recall.ex.bestBidOf(INDUSTRY, Exchange.WORLD_SHORT), recall.fair, 1e-9 * recall.fair);
        assertTrue("...which is under the break price, the collateral over the shares", recall.fair < collateralR / sr);
        assertTrue("...and a bid at value is no bid over it, so no holder sold into it at the step: nothing covered yet", recall.ex.getLifeShortCovered() == 0);
        // A holder selling at the market fills it: the fund's hand sells half of what is recalled at fair value.
        double sold = (sr - lendableR) / 2;
        recall.ex.tradeForCheck(INDUSTRY, Exchange.FUND_HAND, OrderBook.Side.SELL, recall.fair, sold);
        close("...a holder selling at the market covers it: the fund's hand sold " + String.format("%.0f", sold) + " into the recall's bid",
                recall.ex.getLifeShortCovered(), sold, 1e-9);
        close("...at fair value, the bid's price", recall.ex.getLifeShortCoveredValue(), sold * recall.fair, 1e-9 * recall.fair);
        close("...it is short " + String.format("%.0f", sr - sold) + " now", recall.shortShares(), sr - sold, 1e-9);
        sectionEnd();
    }

    /**
     * Section 6's two cities, the saved and the loaded, read the same way: every figure of the short the exchange carries (Exchange.State), on
     * the company the world is short of and over all of them, to this tolerance of each figure (0 before a month is played, a rounding after).
     */
    static void sameShort(String when, Game live, Game back, int f, double tol) {
        Exchange a = live.getExchange(), b = back.getExchange();
        int n = Equity.COMPANIES.length;
        double[][] sums = new double[2][5];
        for (int c = 0; c < n; c++) {
            sums[0][0] += a.shortShares(c); sums[1][0] += b.shortShares(c);
            sums[0][1] += a.shortCollateral(c); sums[1][1] += b.shortCollateral(c);
            sums[0][2] += a.lentByCells(c) + a.lentByFund(c); sums[1][2] += b.lentByCells(c) + b.lentByFund(c);
            sums[0][3] += a.lendingFeeThisMonth(c); sums[1][3] += b.lendingFeeThisMonth(c);
            sums[0][4] += live.getEquity().getLifetimeShortPays(c); sums[1][4] += back.getEquity().getLifetimeShortPays(c);
        }
        String name = Equity.COMPANIES[f];
        close(when + ": " + name + "'s shares short", b.shortShares(f), a.shortShares(f), tol * Math.max(1, a.shortShares(f)));
        close("...the collateral held against them", b.shortCollateral(f), a.shortCollateral(f), tol * Math.max(1, a.shortCollateral(f)));
        close("...the shares the cells lend", b.lentByCells(f), a.lentByCells(f), tol * Math.max(1, a.lentByCells(f)));
        close("...the shares the fund lends", b.lentByFund(f), a.lentByFund(f), tol * Math.max(1, a.lentByFund(f)));
        close("...the month's lending fee the cells were paid", b.lendingFeeToCells(f), a.lendingFeeToCells(f), tol * Math.max(1, a.lendingFeeToCells(f)));
        close("...and the fund", b.lendingFeeToFund(f), a.lendingFeeToFund(f), tol * Math.max(1, a.lendingFeeToFund(f)));
        close("...what the short has paid at the dividends over the city's life",
                back.getEquity().getLifetimeShortPays(f), live.getEquity().getLifetimeShortPays(f), tol * Math.max(1, live.getEquity().getLifetimeShortPays(f)));
        close("...shares sold short and what they fetched over the city's life", b.getLifeShortSold() + b.getLifeShortSoldValue(),
                a.getLifeShortSold() + a.getLifeShortSoldValue(), tol * Math.max(1, a.getLifeShortSold() + a.getLifeShortSoldValue()));
        close("...shares covered and what that cost", b.getLifeShortCovered() + b.getLifeShortCoveredValue(),
                a.getLifeShortCovered() + a.getLifeShortCoveredValue(), tol * Math.max(1, a.getLifeShortCovered() + a.getLifeShortCoveredValue()));
        close("...asks posted, company-months a short stood open, margin calls and recalls",
                b.getLifeShortAsks() + 1e3 * b.getLifeShortMonths() + 1e6 * b.getLifeMarginCalls() + 1e9 * b.getLifeRecalls(),
                a.getLifeShortAsks() + 1e3 * a.getLifeShortMonths() + 1e6 * a.getLifeMarginCalls() + 1e9 * a.getLifeRecalls(), 0);
        close("...the lending fees paid over the city's life", b.getLifeLendingFees(), a.getLifeLendingFees(), tol * Math.max(1, a.getLifeLendingFees()));
        close("...the quote of " + name, b.price(f), a.price(f), tol * Math.max(1, a.price(f)));
        boolean all = true;
        for (int k = 0; k < sums[0].length; k++) all &= Math.abs(sums[0][k] - sums[1][k]) <= tol * Math.max(1, Math.abs(sums[0][k]));
        assertTrue("...and every company's shorts, collateral, lenders, fees and dividends added up, the same", all);
    }

    static void theSave() throws Exception {
        section("6. a save in the middle of a short: the shorts, their lenders, their fees and the dividends paid on them come back, and the month after is the same");

        Shorted opened = openShort("shortsellingcheck-save");
        if (opened == null) { sectionEnd(); return; }
        Game live = opened.city;
        final int f = opened.firm;
        Exchange ex = live.getExchange();
        // Months until the short has been paid a dividend on and charged a fee, and still stands: the state a save has to carry.
        // FS14: the world's own orders held off while it waits (Exchange.setShortSelling(false) stops postShorts() alone: the margin call,
        // the recall, shorting more and the cover; the lending fee and the dividend on what is short are charged as ever), so the short the
        // fixture opened is the one that stands. On HS2's path the world covered it in the month the fixture read (Construction, 16,116
        // shares), and nothing stood to save. Switched on again before the save: the loaded city's switch is the default, on, as the live one's.
        ex.setShortSelling(false);
        for (int m = 0; m < 8 && !(live.getEquity().getLifetimeShortPays(f) > 0 && ex.shortShares(f) > 0 && ex.lendingFeeThisMonth(f) > 0); m++) {
            PrintStream real = System.out;
            System.setOut(quiet);
            try { live.simulateMonths(1); } finally { System.setOut(real); }
        }
        ex.setShortSelling(true);
        assertTrue("fixture: the short stands, lent by the cells and the fund, charged a fee this month, and has been paid a dividend on",
                ex.shortShares(f) > 0 && ex.lentByCells(f) > 0 && ex.lentByFund(f) > 0 && ex.lendingFeeToCells(f) > 0 && ex.lendingFeeToFund(f) > 0
                        && ex.shortCollateral(f) > 0 && live.getEquity().getLifetimeShortPays(f) > 0);
        assertTrue("...with its life counted: sold short, asks posted, months open and the fees paid",
                ex.getLifeShortSold() > 0 && ex.getLifeShortSoldValue() > 0 && ex.getLifeShortMonths() > 0 && ex.getLifeLendingFees() > 0);
        out.printf("   %s: short %,.0f, lent by the cells %,.0f and the fund %,.0f; fee this month %,.4f; paid on the short over its life %,.4f%n",
                Equity.COMPANIES[f], ex.shortShares(f), ex.lentByCells(f), ex.lentByFund(f), ex.lendingFeeThisMonth(f), live.getEquity().getLifetimeShortPays(f));

        // Saved to slot 10 of its own scratch folder, and loaded into a second game.
        boolean[] saved = new boolean[1];
        PrintStream real = System.out;
        System.setOut(quiet);
        Game back;
        try {
            saved[0] = live.saveGame(10, "shortsellingcheck: a short open").ok;
            back = lineOff(new Game(live.getGameFiles()));
            back.loadGameSave(10);
        } finally {
            System.setOut(real);
        }
        assertTrue("saved to slot 10 of the scratch folder and loaded into a second game", saved[0] && back.getLoadFailure() == null);
        sameShort("the loaded city", live, back, f, 0);

        // The month after: both cities play one month, and a city that never saved has the month the loaded one does.
        System.setOut(quiet);
        try {
            live.simulateMonths(1);
            back.simulateMonths(1);
        } finally {
            System.setOut(real);
        }
        sameShort("a month later", live, back, f, 1e-9);

        // A save from before the shorts has none of these fields (Gson leaves a missing key alone): it restores, and nothing is short. No format bump.
        com.google.gson.Gson gson = new com.google.gson.Gson();
        Exchange.State old = gson.fromJson(gson.toJson(live.getExchange().toState()), Exchange.State.class);
        assertTrue("fixture: the state carries a short before the fields are taken out of it", old.shortShares != null && old.shortShares[f] > 0);
        old.shortShares = old.shortCollateral = old.lentCells = old.lentFund = old.feeToCells = old.feeToFund = old.shortPays = null;
        old.lifeShortSold = old.lifeShortSoldValue = old.lifeShortCovered = old.lifeShortCoveredValue = old.lifeLendingFees = 0;
        old.lifeShortAsks = old.lifeShortMonths = old.lifeMarginCalls = old.lifeRecalls = 0;
        Exchange older = ExchangeCheck.exchange();
        assertTrue("a state without the shorts' fields still restores", older.restore(old));
        close("...with nothing short, lent or charged, and nothing counted", older.shortShares(f) + older.shortCollateral(f) + older.lentByCells(f)
                + older.lentByFund(f) + older.lendingFeeThisMonth(f) + older.getLifeShortSold() + older.getLifeShortMonths() + older.getLifeLendingFees(), 0, 0);
        sectionEnd();
    }

    static final int RUN_START = 60, RUN_MONTHS = 1000;

    /**
     * SX2 (FS14): the month of the run in which the cells' trades with the world are caused, as section 9 causes them, the run's first: the
     * ordinary cell holding the most of the company the cells hold most of by value (the bank's own aside) sells the world half of it inside
     * the spread, and buys half of that back at the same price. On LX1's path the run's city never bought a share from the world in 1,000
     * months (63 months before it), so the audit's four lines were read against figures that were never more than nought on one side.
     */
    static final int TRADE_ABROAD_MONTH = RUN_START + 1;

    /** SX2: the settle probe that makes section 7's caused trades with the world (TRADE_ABROAD_MONTH): [0] the shares sold, [1] bought, [2] the company. */
    static java.util.function.Consumer<Boolean> tradeAbroad(Game city, double[] done) {
        return after -> {
            if (after || done[2] >= 0) return;
            Equity reg = city.getEquity();
            Exchange ex = city.getExchange();
            HouseholdBalance hb = city.getHouseholdBalance();
            int f = -1;
            double most = 0;
            for (int k = 0; k < Equity.COMPANIES.length; k++) {
                double v = hb.sharesHeld(k) * ex.price(k);
                if (k != Equity.BANK && reg.getShares(k) > 0 && v > most) { most = v; f = k; }
            }
            if (f < 0) return;
            Household cell = null;
            for (Household h : hb.cells()) {
                if (h instanceof OwnerHousehold || h.households() <= 0) continue;
                if (h.shares[f] > 0 && (cell == null || h.shares[f] * h.households() > cell.shares[f] * cell.households())) cell = h;
            }
            if (cell == null) return;
            done[2] = f;
            double bid = ex.bestBid(f), ask = ex.bestAsk(f);
            double p = !Double.isNaN(bid) && !Double.isNaN(ask) ? (bid + ask) / 2
                    : !Double.isNaN(bid) ? 1.05 * bid : !Double.isNaN(ask) ? .95 * ask : ex.price(f);
            double q1 = .5 * cell.shares[f] * cell.households(), q2 = .5 * q1;
            ex.tradeForCheck(f, Exchange.WORLD, OrderBook.Side.BUY, p, q1);
            done[0] = filled(ex.tradeForCheck(f, Exchange.CELL + cell.key(), OrderBook.Side.SELL, p, q1));
            ex.tradeForCheck(f, Exchange.WORLD, OrderBook.Side.SELL, p, q2);
            done[1] = filled(ex.tradeForCheck(f, Exchange.CELL + cell.key(), OrderBook.Side.BUY, p, q2));
        };
    }

    /** What a run of the playtest's city counted. */
    static final class Run {
        Exchange ex;
        Equity reg;
        int months, auditBad, driftBad, shortCompanyMonths, bubbleMonths, bankBubbleMonths;
        double worstAudit, worstDrift, mostShort;
        // ...and the cells' share trades with the world (LF1b): the months each was made, what it came to, and the months a line was not its figure.
        int abroadSoldMonths, abroadBoughtMonths, abroadLinesBad;
        double abroadSold, abroadBought;
        // ...and SX2's caused month (TRADE_ABROAD_MONTH): the shares the cell sold the world and bought back, and the company (-1 if none was traded).
        final double[] causedAbroad = {0, 0, -1};
        double causedSold, causedBought;
        String worstWhere = "";
        long seconds;
    }

    /**
     * The playtest's city (the default founding, its player's rhythm) played to RUN_MONTHS, with the world's shorts on or off, every month
     * audited and every company's register checked: the holdings against the shares in issue plus what is short.
     */
    static Run play(boolean shorts, String label) {
        return play(shorts, false, label);
    }

    /** ...and with SX2's trades with the world caused in TRADE_ABROAD_MONTH when `tradeAbroad`. */
    static Run play(boolean shorts, boolean tradeAbroad, String label) {
        Path root;
        try { root = TempDirs.create(label); } catch (java.io.IOException e) { throw new IllegalStateException(e); }
        Game g = lineOff(new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")), LongPlaytest.founding()));
        g.getExchange().setShortSelling(shorts);
        Run r = new Run();
        long started = System.currentTimeMillis();
        OrderSearchCheck.playtestCity(g, RUN_START, quiet);
        int n = Equity.COMPANIES.length;
        r.reg = g.getEquity();
        r.ex = g.getExchange();
        PrintStream was = LongPlaytest.out;
        while (g.getMonth() < RUN_MONTHS) {
            LongPlaytest.out = quiet;
            PrintStream real = System.out;
            System.setOut(quiet);
            boolean caused = tradeAbroad && g.getMonth() + 1 == TRADE_ABROAD_MONTH;
            if (caused) g.settleProbeForTest = tradeAbroad(g, r.causedAbroad);
            try {
                LongPlaytest.run(g, 1);
                if (caused) {
                    g.settleProbeForTest = null;
                    r.causedSold = r.ex.getHouseholdsSoldAbroad();
                    r.causedBought = r.ex.getHouseholdsBoughtAbroad();
                }
                if (g.getMonth() % 12 == 0) {
                    LongPlaytest.ensureSchools(g);
                    for (int move = 0; move < LongPlaytest.movesPerLook(); move++) {
                        if (LongPlaytest.advise(g) == null) break;
                        LongPlaytest.run(g, 1);
                    }
                }
            } finally {
                System.setOut(real);
                LongPlaytest.out = was;
            }
            r.months++;
            MoneyAudit.Result a = g.getLastMoneyAudit();
            r.worstAudit = Math.max(r.worstAudit, Math.abs(a.residual));
            if (Math.abs(a.residual) > MoneyAudit.CENT && a.relative() > 1e-7) r.auditBad++;
            double sold = r.ex.getHouseholdsSoldAbroad(), bought = r.ex.getHouseholdsBoughtAbroad(), lineTol = 1e-9 * Math.max(1, sold + bought);
            if (sold > 0) r.abroadSoldMonths++;
            if (bought > 0) r.abroadBoughtMonths++;
            r.abroadSold += sold;
            r.abroadBought += bought;
            if (Math.abs(a.figures.getOrDefault("+ shares HouseholdsSoldAbroad", 0.0) - sold) > lineTol
                    || Math.abs(a.figures.getOrDefault("- shares HouseholdsSoldAbroad (into their savings)", 0.0) - sold) > lineTol
                    || Math.abs(a.figures.getOrDefault("- shares HouseholdsBoughtAbroad", 0.0) - bought) > lineTol
                    || Math.abs(a.figures.getOrDefault("+ shares HouseholdsBoughtAbroad (from their savings)", 0.0) - bought) > lineTol) r.abroadLinesBad++;
            for (int c = 0; c < n; c++) {
                double s = r.ex.shortShares(c);
                double holdings = g.getHouseholdBalance().sharesHeld(c) + r.reg.getDealerShares(c) + r.reg.getForeignShares(c) + r.reg.getCityShares(c);
                double drift = Math.abs(holdings - (r.reg.getShares(c) + s)) / Math.max(1, r.reg.getShares(c));
                if (drift > r.worstDrift) { r.worstDrift = drift; r.worstWhere = Equity.COMPANIES[c] + " m" + g.getMonth() + (s > 0 ? " (short)" : ""); }
                if (drift > 1e-6) r.driftBad++;
                if (s > 0) {
                    r.shortCompanyMonths++;
                    r.mostShort = Math.max(r.mostShort, s / Math.max(1, r.reg.getOutstanding(c)));
                }
                if (r.reg.valuedOnRecord(c) && r.ex.fair(c) > 0 && r.ex.price(c) > 10 * r.ex.fair(c)) {
                    if (c == Equity.BANK) r.bankBubbleMonths++;
                    else r.bubbleMonths++;
                }
            }
        }
        r.seconds = (System.currentTimeMillis() - started) / 1000;
        return r;
    }

    /**
     * Section 8 (LF1): the lending fee on the lenders' books - the fund's counters and its ledger's row - and the four edges the stranger's
     * review found at EM2's gate, each caused by a fixture first: the fee charged on shares the lenders no longer hold, emigrants dropped
     * while a short is open, a short at a company with no shares in issue, and a short at a company worth nothing. (The households' part, the
     * audit's pair, is section 3's city.)
     */
    static void theBooks() throws Exception {
        section("8. the lending fee on the lenders' books, and the review's four edges (LF1)");

        // The fund's books. The fixture of section 4's fee: 700 can be lent by the cells and the fund, 100 are short.
        Bench feed = standard();
        feed.poor.lockout = 1;
        feed.rich.lockout = 1;
        feed.open(Exchange.DESK, feed.shortAsk(), 100);
        feed.city.step(0);
        double toFund = feed.ex.lendingFeeToFund(INDUSTRY);
        assertTrue("fixture: the fund lent shares to the short and was paid for them: $" + String.format("%.6f", toFund), toFund > 0 && feed.fund.getCash() > 1e7);
        close("the fund's income for the month counts the fee, to the cent it was paid", feed.fund.getMonthLendingFees(), toFund, 1e-12);
        close("...and its life's", feed.fund.getLendingFees(), toFund, 1e-12);
        int rowsNow = 0;
        double rowMoney = 0;
        for (FundLedger.Activity row : feed.fund.getLedger().getActivity()) {
            if (FundLedger.LENDING_FEE.equals(row.kind()) && row.month() == feed.city.month) { rowsNow++; rowMoney += row.money(); }
        }
        assertTrue("the ledger has one LENDING_FEE row for the month", rowsNow == 1);
        close("...of the money it was paid", rowMoney, toFund, 1e-12);
        FundLedger.Lot marketLot = null;
        for (FundLedger.Lot l : feed.fund.getLedger().getLots()) if (FundLedger.shareKey(Equity.COMPANIES[INDUSTRY]).equals(l.key())) marketLot = l;
        assertTrue("...and the company's market lot has it as income", marketLot != null && Math.abs(marketLot.income() - toFund) <= 1e-12);
        assertTrue("the fund's page files the row under income", FundView.INCOME.equals(FundView.groupOf(FundLedger.LENDING_FEE)));

        // The fee on what the lenders hold (edge 1). The recall fixture of section 5: 60 short, and the lenders hold 50 after the cells sold.
        Bench held = new Bench(40, 110, 800, 50, 10_000_000);
        held.rich.savings = 5_000;
        held.open(held.richName(), held.shortAsk(), 60);
        held.rich.savings = 12;
        for (Household cell : new Household[] { held.poor, held.rich }) {
            double q = MarketCheck.heldBy(cell, INDUSTRY);
            held.ex.tradeForCheck(INDUSTRY, Exchange.WORLD, OrderBook.Side.BUY, held.fair, q);
            held.ex.tradeForCheck(INDUSTRY, MarketCheck.nameOf(cell), OrderBook.Side.SELL, held.fair, q);
        }
        held.ex.bookOf(INDUSTRY).withdrawAll();
        double sHeld = held.shortShares(), lendableHeld = held.hb.sharesHeld(INDUSTRY) + held.reg.getCityMarketShares(INDUSTRY);
        double q0 = held.price();
        assertTrue("fixture: 60 short, and the lenders hold 50 between them, the fund all of it", sHeld == 60 && lendableHeld == 50
                && held.hb.sharesHeld(INDUSTRY) == 0 && held.reg.getCityMarketShares(INDUSTRY) == 50 && q0 == held.fair);
        held.city.step(0);
        close("the step lends what the lenders hold and no more: 50 of the 60", held.ex.lentByCells(INDUSTRY) + held.ex.lentByFund(INDUSTRY), 50, 1e-9);
        double feeHeld = Exchange.lendingRate(1) * 50 * q0 / 12;
        close("...and the fee is charged on those 50, all lent so the top of the rate", held.ex.lendingFeeThisMonth(INDUSTRY), feeHeld, 1e-9 * feeHeld);
        assertTrue("...which is not the fee on all 60, shares the lenders had sold", held.ex.lendingFeeThisMonth(INDUSTRY) < Exchange.lendingRate(1) * 60 * q0 / 12 - 1e-9);
        close("...the fund's, which holds them all", held.ex.lendingFeeToFund(INDUSTRY), feeHeld, 1e-9 * feeHeld);
        close("...and the cells', which hold none, nothing", held.ex.lendingFeeToCells(INDUSTRY), 0, 0);
        assertTrue("...and the step recalls the 10 the lenders do not hold", held.ex.getLifeRecalls() == 1);

        // Emigrants with a short open (edge 2): the town that bought the short leaves, with the shares it was sold on top of the founders'.
        double s2 = 3_000;
        double[] none = new double[HouseholdBalance.ROWS];
        double[][] nobody = new double[FamilyStructure.values().length][PayTier.values().length];
        HouseholdBalance leaving = ExchangeCheck.savers(20.0, 4.0);
        Equity regLeaving = ExchangeCheck.equity();
        regLeaving.listIfUnlisted(INDUSTRY, 10_000, leaving);
        Household leaver = ExchangeCheck.couple(leaving);
        leaver.shares[INDUSTRY] += s2 / leaver.households();
        double held2 = leaving.sharesHeld(INDUSTRY);
        leaving.advanceMonth((st, t) -> nobody[st.ordinal()][t.ordinal()], none, 0, none, none, .25, .05, 1);
        double taken = leaving.getSharesTakenAway(INDUSTRY);
        assertTrue("fixture: the town holds " + String.format("%,.0f", held2) + " shares, the 10,000 founders' and 3,000 a short sold it, and they all leave: "
                + String.format("%,.0f", taken), held2 == 13_000 && Math.abs(taken - 13_000) < 1e-6);
        Equity regKnown = ExchangeCheck.equity();
        regKnown.listIfUnlisted(INDUSTRY, 10_000, ExchangeCheck.savers(20.0, 4.0));
        regKnown.followEmigrants(leaving, c -> c == INDUSTRY ? s2 : 0);
        close("told what is short, the register holds them all abroad: the holdings add to the shares in issue plus the short", regKnown.getForeignShares(INDUSTRY), 13_000, 1e-6);
        regLeaving.followEmigrants(leaving);
        close("...as the one-argument form, which knows of no short, caps them at the shares in issue", regLeaving.getForeignShares(INDUSTRY), 10_000, 1e-6);

        // A company with no shares in issue and a short open (edge 3): what the short sold goes with it, and a later listing holds only the founders'.
        Bench gone = standard();
        gone.open(Exchange.DESK, gone.shortAsk(), 40);
        gone.fund.receiveLendingFee(Equity.COMPANIES[INDUSTRY], 1, 1);                   // so the fund's ledger has a market lot to close
        assertTrue("fixture: 40 short, held by the desk, and the register is whole", gone.shortShares() == 40 && gone.reg.getDealerShares(INDUSTRY) == 40
                && Math.abs(gone.holdings() - (SHARES + 40)) < 1e-9);
        gone.reg.retire(INDUSTRY, gone.reg.getShares(INDUSTRY), 0);
        // The month's order (docs/month-order.md: payDividends' listIfUnlisted, then tradeShares' step): a company with a book is listed again before
        // the step finds it empty, so the cancelling is for one with NO book - listed against nothing, it stays at no shares in issue, as here (LF1c).
        // The quote the position carried the world's shares at is the month-end mid, not the last trade the step's emptied book falls back to: a bid
        // rests over the last trade, so a figure struck after the books were withdrawn would be the wrong one (LF1c, review).
        gone.ex.tradeForCheck(INDUSTRY, Exchange.WORLD, OrderBook.Side.BUY, 1.5 * gone.price(), 1);
        double worldBefore = gone.reg.getForeignShares(INDUSTRY), quoted = gone.price();
        assertTrue("fixture: the quote is over the last trade, " + String.format("%.4f", quoted) + " against " + String.format("%.4f", gone.lastTrade()), quoted > 1.4 * gone.lastTrade());
        assertTrue("fixture: the month's listing comes first and finds no book to list against, so the company is still with no shares in issue, the world holding "
                + String.format("%,.0f", worldBefore) + " of it", !gone.reg.listIfUnlisted(INDUSTRY, 0, gone.hb) && gone.reg.getShares(INDUSTRY) == 0 && worldBefore > 0);
        gone.city.step(0);
        assertTrue("the short is closed", gone.shortShares() == 0);
        close("...the world's own shares in it, wiped for nothing, are the month's valuation at the quote the company stood at", gone.ex.getWorldSharesWiped(), worldBefore * quoted,
                1e-9 * worldBefore * quoted);
        close("...and the short closed for nothing, the shares it owed at that quote", gone.ex.getShortClosedForNothing(), 40 * quoted, 1e-9 * 40 * quoted);
        close("...the world holds none of it after", gone.reg.getForeignShares(INDUSTRY), 0, 0);
        close("...and what it sold goes with the company: nobody holds a share of it", gone.holdings(), 0, 1e-9);
        close("...the desk's 40 among them", gone.reg.getDealerShares(INDUSTRY), 0, 0);
        FundLedger.Lot goneLot = null;
        for (FundLedger.Lot l : gone.fund.getLedger().getLots()) if (FundLedger.shareKey(Equity.COMPANIES[INDUSTRY]).equals(l.key())) goneLot = l;
        assertTrue("...and the fund's lot is closed in its ledger, the shares it held gone", goneLot != null && goneLot.isClosed());
        gone.city.step(0);
        close("...and a month after the figures are cleared, a company with no short wiping nothing", gone.ex.getWorldSharesWiped() + gone.ex.getShortClosedForNothing(), 0, 0);
        gone.reg.listIfUnlisted(INDUSTRY, 1_000, gone.hb);
        close("a listing of the company later, when its book is back, holds the founders' shares alone: the holdings are the shares in issue", gone.holdings(), gone.reg.getShares(INDUSTRY),
                1e-9 * gone.reg.getShares(INDUSTRY));

        // A company worth nothing, minFair here lifted over its value (edge 4): a short already open is still called in; no new one opens.
        double lifted = Exchange.MIN_FAIR / 1.5;                                             // minFair $1.50, over fair value of about $1
        Bench worthless = standard();
        worthless.open(Exchange.DESK, worthless.shortAsk(), 40);
        worthless.print(2.0);
        worthless.ex.seedConstants(lifted);
        worthless.city.step(0);
        assertTrue("fixture: fair value is minFair now, and the quote is over the short's collateral", worthless.ex.fair(INDUSTRY) <= 1.5 + 1e-12
                && worthless.price() > 1.5);
        assertTrue("a short open at a company under minFair is still margin-called when its account is under water", worthless.ex.getLifeMarginCalls() == 1);
        Bench shut = standard();
        shut.print(2.0);
        shut.ex.seedConstants(lifted);
        shut.city.step(0);
        assertTrue("fixture: the same quote, no short open, and over the band above minFair", shut.price() > 1.5 * (1 + Exchange.SHORT_BAND));
        assertTrue("...opens no new short at a company under minFair", shut.ex.getLifeShortAsks() == 0 && shut.shortShares() == 0);
        sectionEnd();
    }

    /* =======================================================================
       THE CELLS' SHARES ABROAD (LF1b), SECTION 9
       ======================================================================= */

    /**
     * Section 9 (LF1b): a cell selling a share to the world and a cell buying one from it, made by the fixture inside the real month
     * (Game.settleProbeForTest: after the exchange's month has opened, before its step and the audit) at a price inside the spread, so that
     * no other order on the book fills. The cells and the world are both outside the pools, so neither trade moves a pool and the month
     * foots with no line at all; the audit declares each as a pair all the same, as it does the cells' bonds (+ bonds HouseholdsSoldAbroad):
     * the sale a financial inflow into the cells' savings, the purchase a financial outflow out of them, so the balance of payments'
     * financial account, which the rate reads, holds the cells' shares beside their bonds.
     */
    static void theCellsAbroad() {
        section("9. the cells' share trades with the world: each a pair of lines in the audit, a financial flow and its domestic half (LF1b)");
        Game city = town("shortsellingcheck-abroad", 48);
        Equity reg = city.getEquity();
        Exchange ex = city.getExchange();
        HouseholdBalance hb = city.getHouseholdBalance();
        MoneyAudit.Result monthBefore = city.getLastMoneyAudit();
        // The company the cells hold most of by value, and the ordinary cell holding most of it: it sells half to the world and buys a quarter
        // back with what it was paid (the town's savers are poor beside what its shares are worth, and the purchase has to show on the outflow).
        int c = -1;
        double most = 0;
        for (int k = 0; k < Equity.COMPANIES.length; k++) {
            double v = hb.sharesHeld(k) * ex.price(k);
            if (k != Equity.BANK && reg.getShares(k) > 0 && v > most) { most = v; c = k; }
        }
        assertTrue("fixture: the town's cells hold shares in a company", c >= 0);
        if (c < 0) { sectionEnd(); return; }
        final int f = c;
        Household seller = null;
        for (Household h : hb.cells()) {
            if (h instanceof OwnerHousehold || h.households() <= 0) continue;
            if (h.shares[f] > 0 && (seller == null || h.shares[f] * h.households() > seller.shares[f] * seller.households())) seller = h;
        }
        assertTrue("fixture: an ordinary cell holds " + Equity.COMPANIES[f], seller != null);
        if (seller == null) { sectionEnd(); return; }
        final Household cell = seller;
        // [0, 1] the month's two figures before the fixture's trades; [2, 3] the shares each filled; [4, 5] the figures' rise; [6, 7] the cell's
        // savings' change at the sale and at the purchase; [8] the world's holding's; [9] the price; [10, 11] the quantities asked.
        double[] seen = new double[12];
        city.settleProbeForTest = after -> {
            if (after) return;
            seen[0] = ex.getHouseholdsSoldAbroad();
            seen[1] = ex.getHouseholdsBoughtAbroad();
            // Inside the spread as the month finds the book (or beside the one side that rests), so the world's order is the best and fills.
            double bid = ex.bestBid(f), ask = ex.bestAsk(f);
            double p = !Double.isNaN(bid) && !Double.isNaN(ask) ? (bid + ask) / 2
                    : !Double.isNaN(bid) ? 1.05 * bid : !Double.isNaN(ask) ? .95 * ask : ex.price(f);
            double q1 = .5 * cell.shares[f] * cell.households(), q2 = .5 * q1;
            double savings = cell.savings, abroad = reg.getForeignShares(f);
            ex.tradeForCheck(f, Exchange.WORLD, OrderBook.Side.BUY, p, q1);
            seen[2] = filled(ex.tradeForCheck(f, Exchange.CELL + cell.key(), OrderBook.Side.SELL, p, q1));
            seen[6] = (cell.savings - savings) * cell.households();
            savings = cell.savings;
            ex.tradeForCheck(f, Exchange.WORLD, OrderBook.Side.SELL, p, q2);
            seen[3] = filled(ex.tradeForCheck(f, Exchange.CELL + cell.key(), OrderBook.Side.BUY, p, q2));
            seen[7] = (cell.savings - savings) * cell.households();
            seen[4] = ex.getHouseholdsSoldAbroad() - seen[0];
            seen[5] = ex.getHouseholdsBoughtAbroad() - seen[1];
            seen[8] = reg.getForeignShares(f) - abroad;
            seen[9] = p;
            seen[10] = q1;
            seen[11] = q2;
        };
        PrintStream real = System.out;
        System.setOut(quiet);
        try { city.simulateMonths(1); } finally { System.setOut(real); city.settleProbeForTest = null; }
        MoneyAudit.Result a = city.getLastMoneyAudit();
        double p = seen[9], q1 = seen[10], q2 = seen[11];
        assertTrue("fixture: no cell had traded a share with the world that month before the fixture's two trades", seen[0] == 0 && seen[1] == 0);
        close("fixture: a cell sold the world half its " + Equity.COMPANIES[f] + " at $" + String.format("%.4f", p) + ", inside the spread", seen[2], q1, 1e-9 * q1);
        close("...and the exchange's sale abroad rose by it, at that price", seen[4], q1 * p, 1e-9 * q1 * p);
        close("...the money in its savings", seen[6], q1 * p, 1e-9 * q1 * p);
        close("fixture: the same cell bought half of that back from the world, at the same price", seen[3], q2, 1e-9 * q2);
        close("...and the exchange's purchase abroad rose by it", seen[5], q2 * p, 1e-9 * q2 * p);
        close("...the money out of its savings", seen[7], -q2 * p, 1e-9 * q2 * p);
        close("...the world holding what it bought less what it sold", seen[8], q1 - q2, 1e-9 * q1);
        double sold = ex.getHouseholdsSoldAbroad(), bought = ex.getHouseholdsBoughtAbroad();
        out.printf("   sold abroad $%,.4f, bought $%,.4f (the step after the fixture's added $%,.4f and $%,.4f); the month before's financial account in $%,.4f, out $%,.4f%n",
                sold, bought, sold - seen[4], bought - seen[5], monthBefore.financialIn, monthBefore.financialOut);
        assertTrue("the month's two figures hold the fixture's trades", sold >= seen[4] * (1 - 1e-12) && bought >= seen[5] * (1 - 1e-12));
        assertTrue("fixture: both trades are over the audit's cent, so a line that was left out would show", seen[4] > MoneyAudit.CENT && seen[5] > MoneyAudit.CENT);
        assertTrue("the month foots with both declared: residual " + String.format("%.6f", a.residual), Math.abs(a.residual) <= .001);
        double tol = 1e-9 * Math.max(1, sold + bought);
        close("the audit's line for the cells' sale abroad is the exchange's figure", a.figures.getOrDefault("+ shares HouseholdsSoldAbroad", 0.0), sold, tol);
        close("...with its half into their savings, the same size", a.figures.getOrDefault("- shares HouseholdsSoldAbroad (into their savings)", 0.0), sold, tol);
        close("the audit's line for the cells' purchase abroad is the exchange's figure", a.figures.getOrDefault("- shares HouseholdsBoughtAbroad", 0.0), bought, tol);
        close("...with its half out of their savings, the same size", a.figures.getOrDefault("+ shares HouseholdsBoughtAbroad (from their savings)", 0.0), bought, tol);
        assertTrue("fixture: the sale is over ten times the town's whole financial inflow the month before, the purchase ten times its outflow",
                seen[4] > 10 * monthBefore.financialIn && seen[5] > 10 * monthBefore.financialOut);
        assertTrue("the balance of payments' financial inflow holds the sale: $" + String.format("%,.4f", a.financialIn), a.financialIn >= sold - MoneyAudit.CENT);
        assertTrue("...and its financial outflow the purchase: $" + String.format("%,.4f", a.financialOut), a.financialOut >= bought - MoneyAudit.CENT);
        ForeignAccounts fx = city.getForeignAccounts();
        close("ForeignAccounts' month, which the rate reads, is that financial account", fx.financialAccount(), a.financialAccount(), 0);
        sectionEnd();
    }

    static void howOften() {
        section("7. how often, in a real run: " + RUN_MONTHS + " months of the playtest's city, every month audited and every register checked");
        Run on = play(true, true, "shortsellingcheck-run");
        out.printf("   %d months played in %d s (the first %d by the founding script)%n", on.months, on.seconds, RUN_START);
        // SX2: the month the cells' trades with the world are caused in, as section 9 causes them, so the audit's four lines are read on a
        // month with both of them over nought, whatever the played city does by itself.
        int tradedIn = (int) on.causedAbroad[2];
        assertTrue("fixture: in month " + TRADE_ABROAD_MONTH + " an ordinary cell sold the world " + (tradedIn < 0 ? "nothing" : Equity.COMPANIES[tradedIn])
                + " and bought half of it back (SX2, as section 9 causes it)", tradedIn >= 0 && on.causedAbroad[0] > 0 && on.causedAbroad[1] > 0);
        assertTrue("fixture: ...over the audit's cent each, so a line left out of that month's audit would show", on.causedSold > MoneyAudit.CENT && on.causedBought > MoneyAudit.CENT);
        out.printf("   company-months a short's ask was posted: %d; a short stood open: %d; most short at once: %.2f%% of the shares outstanding%n",
                on.ex.getLifeShortAsks(), on.shortCompanyMonths, 100 * on.mostShort);
        out.printf("   shares sold short %,.0f for $%,.0f, covered %,.0f for $%,.0f; margin calls %d, recalls %d%n",
                on.ex.getLifeShortSold(), on.ex.getLifeShortSoldValue(), on.ex.getLifeShortCovered(), on.ex.getLifeShortCoveredValue(),
                on.ex.getLifeMarginCalls(), on.ex.getLifeRecalls());
        out.printf("   lending fees paid $%,.2f; dividends paid on what was short $%,.2f%n", on.ex.getLifeLendingFees(), on.reg.getLifetimeShortPays());
        out.printf("   months the cells sold shares to the world: %d, for $%,.2f; bought from it (its long, its short or the leavers): %d, for $%,.2f%n",
                on.abroadSoldMonths, on.abroadSold, on.abroadBoughtMonths, on.abroadBought);
        assertTrue("the cells traded shares with the world in the real run: they sold to it in " + on.abroadSoldMonths + " months and bought from it in "
                + on.abroadBoughtMonths, on.abroadSoldMonths > 0 && on.abroadBoughtMonths > 0);
        assertTrue("...and every month the audit's four lines for them were the exchange's two figures (" + on.abroadLinesBad + " months not)", on.abroadLinesBad == 0);
        out.printf("   company-months the quote stood over 10 times fair value: %d, and %d in the bank's own shares, which are never shorted%n",
                on.bubbleMonths, on.bankBubbleMonths);
        assertTrue("every month passed the audit: worst residual $" + String.format("%.6f", on.worstAudit) + " over " + on.months + " months", on.auditBad == 0);
        assertTrue("every company's holdings were the shares in issue plus what the world was short, every month: worst drift "
                + String.format("%.2e", on.worstDrift) + " of the shares in issue (" + on.worstWhere + ")", on.driftBad == 0);
        Run off = play(false, "shortsellingcheck-run-off");
        out.printf("   the same city with the shorts off (%d s): %d ask(s) posted; company-months the quote stood over 10 times fair value: %d, and %d in the bank's%n",
                off.seconds, off.ex.getLifeShortAsks(), off.bubbleMonths, off.bankBubbleMonths);
        assertTrue("...and with them off the world posted no ask and sold nothing short, as the switch says",
                off.ex.getLifeShortAsks() == 0 && off.ex.getLifeShortSold() == 0 && off.shortCompanyMonths == 0);
        assertTrue("...and that city passed the audit and kept its register whole too", off.auditBad == 0 && off.driftBad == 0);
        sectionEnd();
    }
}
