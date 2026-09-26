package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies the exchange on the order book (0.7.12 round 2): what the desk
 * posts and what it is not obliged to take, who trades with whom and at what
 * price, what a company does with its surplus, a split, and that a city with
 * a market in it still adds up and survives a save.
 *
 * WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Order book for both"; the
 * round-2 brief: the dealer's quote is replaced by the book, the desk is one
 * participant, the price is the last trade):
 *
 *   1. THE PRICE IS THE LAST TRADE: before its first a company is priced at
 *      fair value; the desk bids and asks half a SPREAD round fair value,
 *      within its capital, and never asks what it does not hold; a trade
 *      sets the price, fair value stays beside it, and the desk carries its
 *      inventory at the lower of the two.
 *   2. NOBODY IS OBLIGED: a bank with no capital posts nothing, and a
 *      household short of money with no buyer waits - its ask rests, it
 *      borrows, and the step counts it; a live desk takes what its capital
 *      carries and not a share more, and the rest of the offer waits.
 *  2b. THE DESK'S CAPS (round 3, Jerus: "Put them back"): POSITION_LIMIT in
 *      one company and BOOK_LIMIT in all, at fair value, beside its capital;
 *      the tightest binds its bid.
 *  2c. ITS EXCESS (round 4, "Offer the excess at fair value"): what it holds
 *      over its caps it asks at fair value, never under, and the rest at its
 *      ask.
 *   3. THE LEAVERS sell into the desk's bid on the way out; with nobody
 *      bidding their shares rest unfilled and stay abroad with them.
 *   4. THE WORLD sells when the yield at the best bid is under its hurdle
 *      and bids when the yield at the price beats it; a household's sale into
 *      its bid is declared abroad; a company with no record it leaves alone.
 *   5. THE HOUSEHOLD CELLS each buy the best yield with their own money,
 *      taking what is asked best first and resting the rest at the market,
 *      not at their reservation; tied names go to the deepest, and a ulp
 *      does not decide it.
 *   6. CELL TO CELL: a rich cell's resting bid is where a short cell's
 *      shares go - a transfer inside the households, no pool line moved.
 *  6b. THE CELLS REBALANCE (round 3, "Yes, same rule"): a cell over its
 *      target sells HOME_SPEED of the excess to a cell under it, which buys
 *      OUT_SPEED of the gap.
 *   7. THE BUYBACK: a company over its target takes what is asked up to fair
 *      value plus the tolerance, and the rest of the month's money stays in
 *      its till while its bid rests at that limit, where a seller who comes
 *      later is met (round 4: no special dividend by any path); the cushion
 *      counts its debt service; a bad year or a company at target does nothing.
 *   8. THE SPLIT: counts, resting orders and the last trade move by the
 *      factor and nothing anybody owns changes in value.
 *   9. THE SAVE: the books with their resting orders, the last trade, fair
 *      value and the split factor round-trip; the dealer's old array loads
 *      with its quote as the last price.
 *  10. A LIVE CITY: every month of share trading closes the audit, the
 *      register and the cells agree, the bank carries the desk at the
 *      exchange's mark, the price read is the last trade, and it all comes
 *      back from a save.
 *
 * Every claim is CAUSED: a register handed shares, a book and a bank; a
 * family that leaves; a world shown a yield; cells given money, or left
 * short; a company handed a surplus and a market that is cheap, then dear.
 */
public class ExchangeCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-72s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-72s FAIL  %,.9f != %,.9f%n", label, actual, expected);
        } else {
            out.printf("%-72s OK%n", label);
        }
    }

    static final double W = DebtManager.WORLD_BASE_RATE;
    static final int N = Equity.COMPANIES.length;
    static final double HURDLE = W + Equity.FOREIGN_PREMIUM;

    /** A register with twelve months of this income on one company's record, and no dividend paid. */
    static Equity withRecord(int company, double... monthlyIncome) {
        Equity e = new Equity();
        for (double v : monthlyIncome) e.recordMonth(company, v, 0);
        return e;
    }

    /** A register with twelve months of this income and this ordinary dividend actually paid on one company's record - what the participants value a share on since round 2. */
    static Equity withPaid(int company, double monthlyIncome, double monthlyPaid) {
        Equity e = new Equity();
        addPaid(e, company, monthlyIncome, monthlyPaid);
        return e;
    }

    static void addPaid(Equity e, int company, double monthlyIncome, double monthlyPaid) {
        for (int m = 0; m < Equity.RECORD_MONTHS; m++) {
            e.recordMonth(company, monthlyIncome, 0);
            e.noteDividendPaid(company, monthlyPaid);
            e.closeDividendMonth();
        }
    }

    /** ...for two companies over the same twelve months: the register closes every company's month together, so each is noted in it. */
    static Equity withPaid(int a, double incomeA, double paidA, int b, double incomeB, double paidB) {
        Equity e = new Equity();
        for (int m = 0; m < Equity.RECORD_MONTHS; m++) {
            e.recordMonth(a, incomeA, 0);
            e.recordMonth(b, incomeB, 0);
            e.noteDividendPaid(a, paidA);
            e.noteDividendPaid(b, paidB);
            e.closeDividendMonth();
        }
        return e;
    }

    static double[] months(double v) { double[] r = new double[12]; java.util.Arrays.fill(r, v); return r; }

    /** Households: a hundred unskilled couples with this much saved each, struck once so they have a take-home. */
    static HouseholdBalance savers(double savedEach, double takeHome) {
        HouseholdBalance hb = new HouseholdBalance();
        double[][] mix = new double[FamilyStructure.values().length][PayTier.values().length];
        mix[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 100;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 100 * takeHome;
        double[] none = new double[HouseholdBalance.ROWS];
        double[] spent = new double[HouseholdBalance.ROWS];
        hb.advanceMonth((s, t) -> mix[s.ordinal()][t.ordinal()], income, 0, none, spent, .25, .05, 1);
        hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED).savings = savedEach;
        return hb;
    }

    static Household couple(HouseholdBalance hb) { return hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED); }

    /** A bank with one branch and this much capital, its month open: a desk posts only for a bank that stands (0.7.12 round 2). */
    static Bank bankWith(double capital) {
        Bank b = new Bank();
        if (capital > 0) b.injectCapital(capital);
        b.refresh(1, 0, 0, 0, 0, 0);
        b.startMonth();
        return b;
    }

    /** The companies, as the exchange sees them: a till, a balance sheet, a payroll. */
    static class Firms implements Exchange.Companies {
        final double[] cash = new double[N], assets = new double[N], equity = new double[N], opex = new double[N];
        /** Interest and principal a month (0.7.11, round 2): nothing unless a fixture says so. */
        final double[] debt = new double[N];
        final double[] boughtBack = new double[N];
        @Override public double cashAvailable(int c, double wanted) { return cash[c]; }
        @Override public double till(int c) { return cash[c]; }
        @Override public void payBuyback(int c, double x) { cash[c] -= x; boughtBack[c] += x; }
        @Override public double assets(int c) { return assets[c]; }
        @Override public double equity(int c) { return equity[c]; }
        @Override public double monthlyOperatingCost(int c) { return opex[c]; }
        @Override public double monthlyDebtService(int c) { return debt[c]; }
    }

    /** The exchange's step for month m, its flows opened first, as Game runs it. */
    static void step(Exchange ex, Equity reg, HouseholdBalance hb, Bank bank, Firms firms, double[] book,
                     double worldRate, double depositRate, int m) {
        ex.startMonth(m);
        ex.takeMonth(reg, hb, bank, firms, book, worldRate, depositRate, m);
    }

    /** What a desk holding nothing bids for at this price: the tightest of its capital, its two caps at fair value, and the float (round 3). */
    static double freshDeskLimit(Bank bank, double bid, double fair, double floatShares) {
        double carry = bank.deskCanCarry(0, bid, Math.min(bid, fair));
        double position = Exchange.POSITION_LIMIT * bank.equity() / fair;
        double book = Exchange.BOOK_LIMIT * bank.equity() / fair;
        return Math.min(Math.min(carry, floatShares), Math.min(position, book));
    }

    static double[] bookOf(int company, double equity) {
        double[] book = new double[N];
        book[company] = equity;
        return book;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        int RETAIL = Equity.indexOf(Sectors.RETAIL);
        int INDUSTRY = Equity.indexOf(Sectors.INDUSTRY);
        int MATERIALS = Equity.indexOf(Sectors.MATERIALS);
        String DESK = Exchange.DESK;

        /* ================= 1. the price is the last trade ================= */
        out.println("--- 1. before its first trade a company is priced at fair value; after it, at the last trade ---");

        HouseholdBalance town1 = savers(12.0, 4.0);          // at its cushion: it neither buys nor rebalances
        Equity reg1 = new Equity();
        reg1.listIfUnlisted(INDUSTRY, 20_000, town1);       // 200 shares each, $1 a share on the books
        Exchange ex1 = new Exchange();
        Bank bank1 = bankWith(100_000);
        double[] book1 = bookOf(INDUSTRY, 20_000);
        step(ex1, reg1, town1, bank1, new Firms(), book1, W, 0, 1);
        double fair1 = 1.0, bid1 = fair1 * (1 - Exchange.SPREAD / 2);
        assertTrue("a bank with capital posts on the book", ex1.isOpen());
        close("fair value is the register's reckoning: book over shares", ex1.fair(INDUSTRY), fair1, 1e-12);
        assertTrue("...and before any trade the price is it", !ex1.hasTraded(INDUSTRY) && ex1.price(INDUSTRY) == fair1);
        close("the desk bids half a spread under fair value", ex1.bestBid(INDUSTRY), bid1, 1e-12);
        close("...for the tightest of its limits: its capital (marked at the price it pays), its caps, the float",
                ex1.bookOf(INDUSTRY).resting(DESK, OrderBook.Side.BUY), freshDeskLimit(bank1, bid1, fair1, reg1.getShares(INDUSTRY)), 1e-6);
        assertTrue("...and asks nothing: it holds nothing", Double.isNaN(ex1.bestAsk(INDUSTRY)));

        Household seller1 = couple(town1);
        double raised1 = ex1.sellForHousehold(reg1, bank1, town1, seller1, 50);
        double sold1 = 100 * 50 / bid1;
        close("a household short of money sells into the desk's bid for what it is short", raised1, 50, 1e-9);
        close("...at the bid: the shares it gave up", 200 - seller1.shares(INDUSTRY), 50 / bid1, 1e-9);
        close("THE PRICE READ IS THE LAST TRADE, the desk's bid", ex1.price(INDUSTRY), bid1, 1e-12);
        close("...the book's own last price", ex1.bookOf(INDUSTRY).lastPrice(), bid1, 1e-12);
        close("...with fair value beside it, unmoved", ex1.fair(INDUSTRY), fair1, 1e-12);
        close("the desk carries its inventory at the lower of the two", ex1.mark(INDUSTRY), bid1, 1e-12);
        close("...and holds what it bought", reg1.getDealerShares(INDUSTRY), sold1, 1e-9);
        close("the register still agrees with the households", reg1.getDomesticShares(INDUSTRY), town1.sharesHeld(INDUSTRY), 1e-9);

        step(ex1, reg1, town1, bank1, new Firms(), book1, W, 0, 2);
        close("a month on, with nothing traded, the price is still the last trade", ex1.price(INDUSTRY), bid1, 1e-12);
        close("...the desk asks half a spread over fair value", ex1.bestAsk(INDUSTRY), fair1 * (1 + Exchange.SPREAD / 2), 1e-12);
        close("...for exactly what it holds", ex1.bookOf(INDUSTRY).resting(DESK, OrderBook.Side.SELL), sold1, 1e-9);

        /* ================= 2. nobody is obliged ================= */
        out.println("\n--- 2. nobody is obliged: a seller with no buyer waits, and the desk takes what its capital carries ---");

        HouseholdBalance broke = savers(0.0, 4.0);
        Equity reg2 = new Equity();
        reg2.listIfUnlisted(RETAIL, 5_000, broke);          // fifty shares each, $1 a share
        Exchange ex2 = new Exchange();
        Bank dead = bankWith(0);
        double[] book2 = bookOf(RETAIL, 5_000);
        step(ex2, reg2, broke, dead, new Firms(), book2, W, 0, 1);
        assertTrue("a bank with no capital posts nothing", !ex2.isOpen() && Double.isNaN(ex2.bestBid(RETAIL)));
        broke.setMarket(ex2, reg2, dead);
        double[][] hundred = new double[FamilyStructure.values().length][PayTier.values().length];
        hundred[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 100;
        double[] pay = new double[HouseholdBalance.ROWS];
        pay[PayTier.UNSKILLED.ordinal()] = 100 * 4.0;
        double[] none = new double[HouseholdBalance.ROWS];
        double[] shop = new double[HouseholdBalance.ROWS];
        shop[PayTier.UNSKILLED.ordinal()] = 100 * 6.0;       // spent $6k on $4k of pay: $2k short each
        broke.advanceMonth((s, t) -> hundred[s.ordinal()][t.ordinal()], pay, 0, none, shop, .25, .05, 1);
        String brokeName = Exchange.CELL + couple(broke).key();
        close("a household short of money with nobody bidding sells nothing", couple(broke).sold(), 0, 1e-12);
        close("...keeps its shares", couple(broke).shares(RETAIL), 50, 1e-12);
        assertTrue("...its ask waits on the book", ex2.bookOf(RETAIL).resting(brokeName, OrderBook.Side.SELL) > 0);
        assertTrue("...and it borrows what it was short", couple(broke).debt() > 0);
        close("the price did not move: nothing traded", ex2.price(RETAIL), 1.0, 1e-12);
        step(ex2, reg2, broke, dead, new Firms(), book2, W, 0, 2);
        boolean fromMonthOne = false;
        for (OrderBook.Order o : ex2.bookOf(RETAIL).asks()) if (o.who().equals(brokeName) && o.month() < 2) fromMonthOne = true;
        assertTrue("the step withdraws it and counts the seller who waited (what rests now is the step's own)",
                ex2.getLastSellsWaited() >= 1 && !fromMonthOne);

        HouseholdBalance many = savers(0.0, 4.0);
        Equity reg2b = new Equity();
        reg2b.listIfUnlisted(RETAIL, 10_000, many);         // a hundred shares each
        Exchange ex2b = new Exchange();
        Bank small = bankWith(100);
        step(ex2b, reg2b, many, small, new Firms(), bookOf(RETAIL, 10_000), W, 0, 1);
        double carries = ex2b.bookOf(RETAIL).resting(DESK, OrderBook.Side.BUY);
        close("fixture: a small bank's desk bids for the tightest of its limits",
                carries, freshDeskLimit(small, 1.0 * (1 - Exchange.SPREAD / 2), 1.0, 10_000), 1e-6);
        double wants = 100 * 90 / (1.0 * (1 - Exchange.SPREAD / 2));
        assertTrue("fixture: ...which is less than a hundred households short of $90k each offer", wants > carries);
        String manyName = Exchange.CELL + couple(many).key();
        double restingBefore2b = ex2b.bookOf(RETAIL).resting(manyName, OrderBook.Side.SELL);
        double raised2b = ex2b.sellForHousehold(reg2b, small, many, couple(many), 90) * 100;
        close("the desk takes what its limits allow and not a share more", reg2b.getDealerShares(RETAIL), carries, 1e-6);
        close("...the household raising what it paid", raised2b, carries * (1 - Exchange.SPREAD / 2), 1e-6);
        close("...and the rest of its offer waits on the book, unfilled: AN UNFILLED SALE RESTS",
                ex2b.bookOf(RETAIL).resting(manyName, OrderBook.Side.SELL) - restingBefore2b, wants - carries, 1e-6);

        /* ================= 2b. the desk's caps (round 3) ================= */
        out.println("\n--- 2b. the desk's caps: POSITION_LIMIT in one company and BOOK_LIMIT in all, beside its capital; the tightest binds ---");

        // A fresh bank with capital to spare: the single-name cap binds.
        HouseholdBalance capHolders = savers(12.0, 4.0);
        Equity regC = new Equity();
        regC.listIfUnlisted(RETAIL, 100_000, capHolders);    // a thousand shares a household, $1 each
        Exchange exC = new Exchange();
        Bank capBank = bankWith(10_000);
        step(exC, regC, capHolders, capBank, new Firms(), bookOf(RETAIL, 100_000), W, 0, 1);
        double bidC = 1.0 * (1 - Exchange.SPREAD / 2);
        double position = Exchange.POSITION_LIMIT * capBank.equity() / 1.0;
        assertTrue("fixture: its capital would carry more than a quarter of its equity in the company",
                capBank.deskCanCarry(0, bidC, bidC) > position);
        close("the desk bids for POSITION_LIMIT of the bank's equity in one company, at fair value",
                exC.bookOf(RETAIL).resting(DESK, OrderBook.Side.BUY), position, 1e-9);
        assertTrue("...counted as the cap that bound", exC.getLastDeskBound(Exchange.BOUND_POSITION) >= 1);
        double restingC = exC.bookOf(RETAIL).resting(Exchange.CELL + couple(capHolders).key(), OrderBook.Side.SELL);
        double raisedC = exC.sellForHousehold(regC, capBank, capHolders, couple(capHolders), 50) * 100;
        close("a household offering $5,000k sells only what the cap leaves", raisedC, position * bidC, 1e-6);
        close("...the desk holding a quarter of the equity it posted on, at fair value, no more", regC.getDealerShares(RETAIL) * 1.0,
                position, 1e-9);
        close("...and the rest of the sale rests on the book", exC.bookOf(RETAIL).resting(Exchange.CELL + couple(capHolders).key(),
                OrderBook.Side.SELL) - restingC, 100 * 50 / bidC - position, 1e-6);

        // The same bank already holding its aggregate cap's worth elsewhere: the book cap binds.
        HouseholdBalance bookHolders = savers(12.0, 4.0);
        Equity regB = new Equity();
        regB.listIfUnlisted(RETAIL, 100_000, bookHolders);
        regB.listIfUnlisted(INDUSTRY, 100_000, bookHolders);
        Bank bookBank = bankWith(10_000);
        couple(bookHolders).shares[INDUSTRY] -= 40;          // 4,000 of Industry on the desk: $4,000k at fair value
        regB.moveDesk(INDUSTRY, 4_000);
        Exchange exB = new Exchange();
        double[] bookBoth = new double[N];
        bookBoth[RETAIL] = 100_000;
        bookBoth[INDUSTRY] = 100_000;
        double eqB = bookBank.equity();                      // the equity the desk posts on
        step(exB, regB, bookHolders, bookBank, new Firms(), bookBoth, W, 0, 1);
        double bookRoom = (Exchange.BOOK_LIMIT * eqB - 4_000 * 1.0) / 1.0;
        assertTrue("fixture: what the book has left is under the single-name cap", bookRoom < Exchange.POSITION_LIMIT * eqB);
        close("with $4,000k of Industry held, the desk bids for Retail only what BOOK_LIMIT leaves",
                exB.bookOf(RETAIL).resting(DESK, OrderBook.Side.BUY), bookRoom, 1e-9);
        assertTrue("...counted as the cap that bound", exB.getLastDeskBound(Exchange.BOUND_BOOK) >= 1);
        close("...its Industry bid none: it is at the single-name cap there", exB.bookOf(INDUSTRY).resting(DESK, OrderBook.Side.BUY),
                Math.max(0, Math.min(Exchange.POSITION_LIMIT * eqB - 4_000, bookRoom)), 1e-9);

        /* ================= 2c. what is over the caps, offered at fair value ================= */
        out.println("\n--- 2c. what the desk holds over its caps it offers at fair value, and the rest at its ask ---");

        // A $10,000k bank holding 4,000 of Retail at $1: 40% of its equity,
        // 1,500 shares over POSITION_LIMIT. Its holders at their cushion: no
        // household buys, and no company bids.
        HouseholdBalance xsHolders = savers(12.0, 4.0);
        Equity regX = new Equity();
        regX.listIfUnlisted(RETAIL, 100_000, xsHolders);
        couple(xsHolders).shares[RETAIL] -= 40;
        regX.moveDesk(RETAIL, 4_000);
        Bank xsBank = bankWith(10_000);
        Exchange exX = new Exchange();
        double eqX = xsBank.equity();
        step(exX, regX, xsHolders, xsBank, new Firms(), bookOf(RETAIL, 100_000), W, 0, 1);
        double fairX = exX.fair(RETAIL), overX = 4_000 - Exchange.POSITION_LIMIT * eqX / fairX;
        assertTrue("fixture: the desk holds more than POSITION_LIMIT of the bank's equity in one company, under BOOK_LIMIT in all",
                4_000 * fairX > Exchange.POSITION_LIMIT * eqX && 4_000 * fairX <= Exchange.BOOK_LIMIT * eqX);
        double atFair = 0, atAsk = 0, lowest = Double.POSITIVE_INFINITY;
        for (OrderBook.Order o : exX.bookOf(RETAIL).asks()) {
            if (!o.who().equals(DESK)) continue;
            lowest = Math.min(lowest, o.price());
            if (o.price() == fairX) atFair += o.quantity();
            else if (o.price() == fairX * (1 + Exchange.SPREAD / 2)) atAsk += o.quantity();
        }
        close("a desk over its position cap offers exactly the excess, at fair value", atFair, overX, 1e-9);
        close("...counted as the month's excess", exX.getLastExcessOffered(RETAIL), overX, 1e-9);
        close("...and the rest of what it holds at fair value plus half the spread", atAsk, 4_000 - overX, 1e-9);
        assertTrue("nothing is asked under fair value", lowest >= fairX);
        close("with no bid to meet it, the excess rests, on offer at fair value", exX.deskExcessOnOffer(RETAIL), overX * fairX, 1e-9);
        close("...and nothing of it sold", exX.getLifeExcessSold(), 0, 0);

        // The next month the company, far over its target, bids for its own
        // shares: the excess at fair value is the cheapest ask, and it sells.
        Equity regXs = withRecord(RETAIL, new double[] { 100, 100, 100, 100, 100, 100, 50, 50, 50, 50, 50, 50 });
        HouseholdBalance xsOwners = savers(12.0, 4.0);
        regXs.listIfUnlisted(RETAIL, 100_000, xsOwners);
        couple(xsOwners).shares[RETAIL] -= 40;
        regXs.moveDesk(RETAIL, 4_000);
        Bank xsBank2 = bankWith(10_000);
        Exchange exXs = new Exchange();
        Firms buyer = new Firms();
        buyer.assets[RETAIL] = 100_000; buyer.equity[RETAIL] = 100_000;
        buyer.cash[RETAIL] = 50_000; buyer.opex[RETAIL] = 100;
        double deskCash = xsBank2.getCash(), eqXs = xsBank2.equity();   // the equity the desk posts on
        step(exXs, regXs, xsOwners, xsBank2, buyer, bookOf(RETAIL, 100_000), W, .05, 1);
        double spentX = 50_000 - buyer.cash[RETAIL];
        double fairXs = exXs.fair(RETAIL), overXs = 4_000 - Exchange.POSITION_LIMIT * eqXs / fairXs;
        assertTrue("fixture: the company's month of buybacks is less than the desk's excess", spentX > 0 && spentX / fairXs < overXs);
        close("the excess sells when a bid meets it: the company's buyback took it", exXs.getLifeExcessSold(), spentX, 1e-9);
        close("...at fair value, not under it", exXs.price(RETAIL), fairXs, 1e-12);
        close("...out of the desk's holding", 4_000 - regXs.getDealerShares(RETAIL), spentX / fairXs, 1e-9);
        close("...into the bank's cash", xsBank2.getCash() - deskCash, spentX, 1e-9);
        close("...and the rest of the excess still rests at fair value", exXs.deskExcessOnOffer(RETAIL),
                (overXs - spentX / fairXs) * fairXs, 1e-9);

        // A desk under both caps: 1,000 of Retail on the same bank.
        HouseholdBalance underHolders = savers(12.0, 4.0);
        Equity regUc = new Equity();
        regUc.listIfUnlisted(RETAIL, 100_000, underHolders);
        couple(underHolders).shares[RETAIL] -= 10;
        regUc.moveDesk(RETAIL, 1_000);
        Exchange exUc = new Exchange();
        step(exUc, regUc, underHolders, bankWith(10_000), new Firms(), bookOf(RETAIL, 100_000), W, 0, 1);
        close("a desk under both caps offers no excess", exUc.getLastExcessOffered(RETAIL), 0, 0);
        close("...and asks what it holds at fair value plus half the spread", exUc.bestAskOf(RETAIL, DESK),
                exUc.fair(RETAIL) * (1 + Exchange.SPREAD / 2), 1e-12);

        // The book over BOOK_LIMIT: 4,000 of Retail, 1,000 each of Industry
        // and Materials - $6,000k at fair value against a $5,000k cap.
        HouseholdBalance bookX = savers(12.0, 4.0);
        Equity regBx = new Equity();
        for (int c : new int[] { RETAIL, INDUSTRY, MATERIALS }) regBx.listIfUnlisted(c, 100_000, bookX);
        couple(bookX).shares[RETAIL] -= 40;
        couple(bookX).shares[INDUSTRY] -= 10;
        couple(bookX).shares[MATERIALS] -= 10;
        regBx.moveDesk(RETAIL, 4_000);
        regBx.moveDesk(INDUSTRY, 1_000);
        regBx.moveDesk(MATERIALS, 1_000);
        Bank bookBankX = bankWith(10_000);
        double eqBx = bookBankX.equity();
        Exchange exBx = new Exchange();
        double[] three = new double[N];
        three[RETAIL] = three[INDUSTRY] = three[MATERIALS] = 100_000;
        step(exBx, regBx, bookX, bookBankX, new Firms(), three, W, 0, 1);
        double overBook = 6_000 - Exchange.BOOK_LIMIT * eqBx;
        assertTrue("fixture: the book is over BOOK_LIMIT, and only Retail over POSITION_LIMIT",
                overBook > 0 && 1_000 < Exchange.POSITION_LIMIT * eqBx && 4_000 > Exchange.POSITION_LIMIT * eqBx);
        close("the book's excess is spread pro rata by value at fair value: Industry its sixth",
                exBx.getLastExcessOffered(INDUSTRY), overBook * 1_000 / 6_000, 1e-9);
        close("...Materials the same", exBx.getLastExcessOffered(MATERIALS), overBook * 1_000 / 6_000, 1e-9);
        close("a holding over both caps offers the larger of its two excesses, not the two added",
                exBx.getLastExcessOffered(RETAIL), Math.max(4_000 - Exchange.POSITION_LIMIT * eqBx, overBook * 4_000 / 6_000), 1e-9);

        /* ================= 3. the leavers ================= */
        out.println("\n--- 3. a household that leaves sells its shares on the way out, or they rest unfilled ---");

        double[][] fewer = new double[FamilyStructure.values().length][PayTier.values().length];
        fewer[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 80;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 80 * 4.0;

        HouseholdBalance town3 = savers(20.0, 4.0);
        Equity reg3 = new Equity();
        reg3.listIfUnlisted(RETAIL, 10_000, town3);
        town3.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, none, none, .25, .05, 1);
        close("fixture: twenty households leave with two thousand shares", town3.getSharesTakenAway(RETAIL), 2_000, 1e-9);
        reg3.followEmigrants(town3);
        close("...held abroad from the moment they go", reg3.getForeignShares(RETAIL), 2_000, 1e-9);
        Exchange ex3 = new Exchange();
        Bank bank3 = bankWith(10_000);
        step(ex3, reg3, town3, bank3, new Firms(), bookOf(RETAIL, 10_000), W, 0, 1);
        double bid3 = 1.0 * (1 - Exchange.SPREAD / 2);
        close("the leavers sell into the desk's bid, and the cash leaves with them",
                ex3.getEmigrantsPaid(RETAIL), 2_000 * bid3, 1e-9);
        close("...out of the bank's cash", bank3.getCash(), 10_000 - 2_000 * bid3, 1e-9);
        close("...onto the desk", reg3.getDealerShares(RETAIL), 2_000, 1e-9);
        close("...and nothing is held abroad any more", reg3.getForeignShares(RETAIL), 0, 1e-9);
        close("the register still agrees with the households", reg3.getDomesticShares(RETAIL), town3.sharesHeld(RETAIL), 1e-9);

        HouseholdBalance town3b = savers(20.0, 4.0);
        Equity reg3b = new Equity();
        reg3b.listIfUnlisted(RETAIL, 10_000, town3b);
        town3b.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, none, none, .25, .05, 1);
        reg3b.followEmigrants(town3b);
        Exchange ex3b = new Exchange();
        step(ex3b, reg3b, town3b, dead, new Firms(), bookOf(RETAIL, 10_000), W, 0, 1);
        close("with nobody bidding, an emigrant's shares rest unfilled on the book",
                ex3b.bookOf(RETAIL).resting(Exchange.EMIGRANTS, OrderBook.Side.SELL), 2_000, 1e-9);
        close("...nobody paid them", ex3b.getEmigrantsPaid(RETAIL), 0, 1e-12);
        town3b.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, none, none, .25, .05, 1);
        step(ex3b, reg3b, town3b, dead, new Firms(), bookOf(RETAIL, 10_000), W, 0, 2);
        assertTrue("the next step withdraws their ask and counts it waited",
                ex3b.getLastSellsWaited() >= 1 && ex3b.bookOf(RETAIL).resting(Exchange.EMIGRANTS, OrderBook.Side.SELL) == 0);
        close("...and their shares stay abroad with them", reg3b.getForeignShares(RETAIL), 2_000, 1e-9);

        /* ================= 4. the world ================= */
        out.println("\n--- 4. the world sells when the yield at the bid is under its hurdle, and bids when the price beats it ---");

        Equity reg4 = withPaid(INDUSTRY, 100, 40);          // $480 a year paid
        reg4.offer(INDUSTRY, 1_000, 0, null, W);            // 1,000 shares abroad
        close("fixture: the company is wholly foreign-owned", reg4.foreignShare(INDUSTRY), 1.0, 1e-12);
        Exchange ex4 = new Exchange();
        Bank bank4 = bankWith(100_000);
        double[] book4 = bookOf(INDUSTRY, 20_000);          // $20 a share: the dividend yields 2.4%
        step(ex4, reg4, new HouseholdBalance(), bank4, new Firms(), book4, W, .10, 1);
        double deskBid4 = 20.0 * (1 - Exchange.SPREAD / 2);
        double y4 = .48 / deskBid4;
        assertTrue("fixture: the yield at the desk's bid is under the world's hurdle", y4 < HURDLE * (1 - Exchange.FOREIGN_TOLERANCE));
        double wantSell = Exchange.FOREIGN_SPEED * (1 - y4 / HURDLE) * 1_000;
        close("the world sells a share of its holding in proportion to the shortfall",
                1_000 - reg4.getForeignShares(INDUSTRY), wantSell, 1e-9);
        close("...to the desk", reg4.getDealerShares(INDUSTRY), wantSell, 1e-9);
        close("...at the desk's bid, the resting price", ex4.getBoughtFromAbroad(INDUSTRY), wantSell * deskBid4, 1e-9);

        // The world bids when a distressed last trade leaves the yield at the
        // price past its hurdle - and rests AT the market, where a household
        // short of money finds it.
        HouseholdBalance town4 = savers(12.0, 4.0);         // at its cushion: nothing to rebalance at the step
        Equity reg4b = withPaid(INDUSTRY, 100, 40);
        reg4b.offer(INDUSTRY, 1_000, 0, null, W);
        reg4b.moveForeign(INDUSTRY, -500);                   // half of it held at home
        couple(town4).shares[INDUSTRY] += 5;
        double[] book4b = bookOf(INDUSTRY, 1_000);           // book $1; the dividend is worth $9.60 a share
        Exchange ex4b = new Exchange();
        double fair4b = .48 / HURDLE;
        double last4b = .8 * fair4b;
        ex4b.bookOf(INDUSTRY).seedLastPrice(last4b);         // it last traded at 80% of what it is worth
        step(ex4b, reg4b, town4, dead, new Firms(), book4b, W, .10, 1);
        close("fixture: fair value is the dividend capitalised at the hurdle", ex4b.fair(INDUSTRY), fair4b, 1e-9);
        double yPrice = .48 / last4b;
        double wantBuy = Exchange.FOREIGN_SPEED * (yPrice / HURDLE - 1) * reg4b.getOutstanding(INDUSTRY);
        close("the world bids for a share of the float in proportion to the excess yield",
                ex4b.bookOf(INDUSTRY).resting(Exchange.WORLD, OrderBook.Side.BUY), wantBuy, 1e-9);
        close("...resting at the market, the last trade - not at its reservation",
                ex4b.bestBid(INDUSTRY), Math.min(last4b, .48 / (HURDLE * (1 + Exchange.FOREIGN_TOLERANCE))), 1e-12);
        couple(town4).rate = .10;                            // its credit costs 10%: it asks $4.80
        double raised4 = ex4b.sellForHousehold(reg4b, dead, town4, couple(town4), 1.0) * 100;
        double q4 = 500 * 1.0 * 100 / (500 * .48 / .10);    // sized at its own ask
        close("a household short of money sells into the world's bid", 500 - town4.sharesHeld(INDUSTRY), q4, 1e-9);
        close("...declared abroad, at the world's price", ex4b.getHouseholdsSoldAbroad(INDUSTRY), q4 * last4b, 1e-9);
        close("...raising what it was short and banking the rest", raised4, 100, 1e-9);
        close("...the shares abroad again", reg4b.getForeignShares(INDUSTRY), 500 + q4, 1e-9);

        Equity reg4c = new Equity();                         // no record: NEW
        reg4c.offer(INDUSTRY, 1_000, 0, null, W);
        Exchange ex4c = new Exchange();
        step(ex4c, reg4c, new HouseholdBalance(), bankWith(10_000), new Firms(), book4, W, .10, 1);
        close("a company with no record is not traded by the world: not sold", ex4c.getBoughtFromAbroad(INDUSTRY), 0, 1e-12);
        assertTrue("...and not bid for", ex4c.bookOf(INDUSTRY).resting(Exchange.WORLD, OrderBook.Side.BUY) == 0);

        /* ================= 5. the household cells ================= */
        out.println("\n--- 5. each cell buys the best yield with its own money, and rests the rest at the market ---");

        Equity reg5 = withPaid(INDUSTRY, 100, 40,            // $0.48 a share a year on 1,000
                RETAIL, 200, 80);                            // $0.096 a share a year on 10,000
        reg5.offer(INDUSTRY, 1_000, 0, null, W);
        reg5.offer(RETAIL, 10_000, 0, null, W);
        reg5.moveForeign(INDUSTRY, -10); reg5.moveDesk(INDUSTRY, 10);         // the desk holds ten of Industry...
        reg5.moveForeign(RETAIL, -10_000); reg5.moveDesk(RETAIL, 10_000);     // ...and all of Retail
        Bank bank5 = bankWith(1_000_000);
        double[] book5 = new double[N];
        book5[INDUSTRY] = 1_000;                             // worth its dividend: $9.60, yielding the hurdle
        book5[RETAIL] = 30_000;                              // worth its book, $3: yielding less
        HouseholdBalance buyers = savers(100.0, 4.0);        // $88k past the cushion each
        double wanted = 100 * (100.0 - HouseholdBalance.SHARE_CUSHION_MONTHS * 4.0) * OutwardInvestment.OUT_SPEED;
        Exchange ex5 = new Exchange();
        step(ex5, reg5, buyers, bank5, new Firms(), book5, W, 0, 1);
        double askInd = ex5.fair(INDUSTRY) * (1 + Exchange.SPREAD / 2), askRet = ex5.fair(RETAIL) * (1 + Exchange.SPREAD / 2);
        assertTrue("fixture: Industry yields more at the desk's ask than Retail, both over the deposit rate plus the premium",
                .48 / askInd > .096 / askRet && .096 / askRet > Exchange.HOUSEHOLD_PREMIUM);
        close("the cell takes everything asked of the best yield first", ex5.getSoldToHouseholds(INDUSTRY), 10 * askInd, 1e-9);
        close("...then the next best with the rest", ex5.getSoldToHouseholds(RETAIL), wanted - 10 * askInd, 1e-9);
        close("...its month's money, out of its own savings", couple(buyers).savings(), 100.0 - wanted / 100, 1e-9);
        close("...into shares of both", buyers.sharesHeld(INDUSTRY), 10, 1e-9);
        close("...the register agreeing on each", reg5.getDomesticShares(RETAIL), buyers.sharesHeld(RETAIL), 1e-9);
        close("the desk sold nothing it did not have", reg5.getDealerShares(INDUSTRY), 0, 1e-9);

        Equity reg5b = withPaid(INDUSTRY, 100, 40, RETAIL, 200, 80);
        reg5b.offer(INDUSTRY, 1_000, 0, null, W);
        reg5b.offer(RETAIL, 10_000, 0, null, W);             // Retail all abroad: nothing asked
        reg5b.moveForeign(INDUSTRY, -10); reg5b.moveDesk(INDUSTRY, 10);
        HouseholdBalance keen = savers(100.0, 4.0);
        Exchange ex5b = new Exchange();
        step(ex5b, reg5b, keen, bank5, new Firms(), book5, W, 0, 1);
        String keenName = Exchange.CELL + couple(keen).key();
        double left5 = wanted - 10 * askInd;
        close("with nothing else asked, what is left rests as a bid on the best yield",
                ex5b.bookOf(INDUSTRY).resting(keenName, OrderBook.Side.BUY), left5 / askInd, 1e-9);
        close("...AT THE MARKET, its last trade - not at the price that yields the floor",
                ex5b.bestBid(INDUSTRY), askInd, 1e-12);
        assertTrue("...which is five times higher", .48 / Exchange.HOUSEHOLD_PREMIUM > 4 * askInd);

        HouseholdBalance cautious = savers(100.0, 4.0);
        Exchange ex5c = new Exchange();
        step(ex5c, reg5, cautious, bank5, new Firms(), book5, W, .10, 1);   // the bank pays 10%
        close("with the deposit rate above every yield, nobody buys", ex5c.getSoldToHouseholds(), 0, 1e-12);
        close("...and nobody's savings moved", couple(cautious).savings(), 100.0, 1e-12);

        /*
         * AND WHEN THE MODEL PRICES TWO OF THEM THE SAME. A company valued on
         * its dividends yields exactly the discount rate at fair value, so two
         * such companies tie to the last bit; before TIED_YIELD the sort
         * decided between them on which copy had lost it (DenominationCheck,
         * month 129 on the dealer). A tie goes to the deepest name, and a ulp
         * does not move it.
         */
        Equity regT = withPaid(INDUSTRY, 300, 120, MATERIALS, 100, 40);
        regT.offer(INDUSTRY, 3_000, 0, null, W);            // three thousand shares...
        regT.offer(MATERIALS, 1_000, 0, null, W);           // ...against one
        double[] bookT = new double[N];
        bookT[INDUSTRY] = 1;
        bookT[MATERIALS] = 1;
        Exchange exT = new Exchange();
        HouseholdBalance keenT = savers(100.0, 4.0);
        step(exT, regT, keenT, dead, new Firms(), bookT, W, 0, 1);
        close("fixture: both are priced to the discount rate", regT.dividendPerShareAnnual(INDUSTRY) / exT.fair(INDUSTRY), HURDLE, 1e-12);
        close("fixture: ...on different share counts", regT.dividendPerShareAnnual(MATERIALS) / exT.fair(MATERIALS), HURDLE, 1e-12);
        String tName = Exchange.CELL + couple(keenT).key();
        assertTrue("tied names: the bid rests on the deepest", exT.bookOf(INDUSTRY).resting(tName, OrderBook.Side.BUY) > 0
                && exT.bookOf(MATERIALS).resting(tName, OrderBook.Side.BUY) == 0);
        Equity regU = withPaid(INDUSTRY, 300, Math.nextUp(120.0), MATERIALS, 100, 40);
        regU.offer(INDUSTRY, 3_000, 0, null, W);
        regU.offer(MATERIALS, 1_000, 0, null, W);
        Exchange exU = new Exchange();
        HouseholdBalance keenU = savers(100.0, 4.0);
        step(exU, regU, keenU, dead, new Firms(), bookT, W, 0, 1);
        close("a dividend moved by one ulp does not move where the money goes",
                exU.bookOf(INDUSTRY).resting(tName, OrderBook.Side.BUY), exT.bookOf(INDUSTRY).resting(tName, OrderBook.Side.BUY), 1e-9);

        /* ================= 6. cell to cell ================= */
        out.println("\n--- 6. a short cell's shares go to a rich cell's resting bid: a transfer inside the households ---");

        HouseholdBalance two = new HouseholdBalance();
        double[][] mix6 = new double[FamilyStructure.values().length][PayTier.values().length];
        mix6[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 100;
        mix6[FamilyStructure.COUPLE.ordinal()][PayTier.SKILLED.ordinal()] = 100;
        double[] pay6 = new double[HouseholdBalance.ROWS];
        pay6[PayTier.UNSKILLED.ordinal()] = 100 * 4.0;
        pay6[PayTier.SKILLED.ordinal()] = 100 * 4.0;
        two.advanceMonth((s, t) -> mix6[s.ordinal()][t.ordinal()], pay6, 0, none, none, .25, .05, 1);
        Household poor = two.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
        Household rich = two.cell(FamilyStructure.COUPLE, PayTier.SKILLED);
        poor.savings = 12;                                   // at its cushion: nothing to rebalance at the step
        rich.savings = 100;
        Equity reg6 = withPaid(RETAIL, 100, 25);             // $300 a year on 10,000 shares: 3%
        reg6.listIfUnlisted(RETAIL, 10_000, two);            // the founders: fifty shares a household
        Exchange ex6 = new Exchange();
        double[] book6 = bookOf(RETAIL, 10_000);
        step(ex6, reg6, two, dead, new Firms(), book6, W, 0, 1);
        String richName = Exchange.CELL + rich.key(), poorName = Exchange.CELL + poor.key();
        double richBid = ex6.bookOf(RETAIL).resting(richName, OrderBook.Side.BUY);
        assertTrue("fixture: the rich cell's bid rests on the book; the short cell has nothing to bid with",
                richBid > 0 && ex6.bookOf(RETAIL).resting(poorName, OrderBook.Side.BUY) == 0);
        close("fixture: ...at the market, fair value", ex6.bestBid(RETAIL), 1.0, 1e-12);
        two.setMarket(ex6, reg6, dead);
        double[] shop6 = new double[HouseholdBalance.ROWS];
        shop6[PayTier.UNSKILLED.ordinal()] = 100 * 18.0;     // $18k on $4k of pay and $12k saved: $2k short each
        shop6[PayTier.SKILLED.ordinal()] = 100 * 1.0;
        double richShares = rich.shares(RETAIL), poorShares = poor.shares(RETAIL);
        two.advanceMonth((s, t) -> mix6[s.ordinal()][t.ordinal()], pay6, 0, none, shop6, .25, .05, 1);
        double moved = poorShares - poor.shares(RETAIL);
        out.printf("   the short cell sold %,.3f shares a household; the rich cell's bid took them at $%.4f%n", moved, ex6.price(RETAIL));
        assertTrue("the short cell sold into the rich cell's bid", moved > 0 && ex6.getBetweenHouseholdsTrades(RETAIL) > 0);
        close("...the rich cell holds what it sold", (rich.shares(RETAIL) - richShares) * 100, moved * 100, 1e-9);
        close("...at the rich cell's resting price, the last trade", ex6.price(RETAIL), 1.0, 1e-12);
        close("...sized at its own ask, the price its borrowing rate puts on the dividend",
                moved, 2.0 / (reg6.dividendPerShareAnnual(RETAIL) / poor.rate()), 1e-9);
        close("...the cash between them the transfer counted", ex6.getBetweenHouseholds(RETAIL), moved * 100 * 1.0, 1e-9);
        close("...the short cell raising what it was short", poor.sold(), 2.0, 1e-9);
        close("...and borrowing nothing", poor.debt(), 0, 1e-12);
        close("no pool line moved: nothing sold to or bought from the households by anybody else",
                ex6.getSoldToHouseholds() + ex6.getBoughtFromHouseholds() + ex6.getBuybackToHouseholds()
                        + ex6.getHouseholdsBoughtAbroad() + ex6.getHouseholdsSoldAbroad(), 0, 1e-12);
        close("the register still agrees with the cells", reg6.getDomesticShares(RETAIL), two.sharesHeld(RETAIL), 1e-9);

        /* ================= 6b. the cells rebalance by the bond rule (round 3) ================= */
        out.println("\n--- 6b. a cell over its target sells HOME_SPEED of the excess to a cell under it, which buys OUT_SPEED of the gap ---");

        HouseholdBalance two2 = new HouseholdBalance();
        two2.advanceMonth((s, t) -> mix6[s.ordinal()][t.ordinal()], pay6, 0, none, none, .25, .05, 1);
        Household under = two2.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED);
        Household over = two2.cell(FamilyStructure.COUPLE, PayTier.SKILLED);
        Equity reg6b = withPaid(RETAIL, 100, 25);
        reg6b.listIfUnlisted(RETAIL, 10_000, two2);          // fifty shares a household in both
        under.savings = 0;                                   // $12k under its cushion: its target is $12k of shares less
        over.savings = 100;                                  // $88k past it: its target is $88k of shares more
        double underShares = under.shares(RETAIL), overShares = over.shares(RETAIL);
        double shortfall = 100 * (HouseholdBalance.SHARE_CUSHION_MONTHS * 4.0 - 0), excessOver = 100 * (100 - HouseholdBalance.SHARE_CUSHION_MONTHS * 4.0);
        assertTrue("fixture: the cell under its cushion holds more than its shortfall in shares", 100 * underShares * 1.0 > shortfall);
        Exchange ex6b = new Exchange();
        step(ex6b, reg6b, two2, dead, new Firms(), book6, W, 0, 1);
        double sold6b = (underShares - under.shares(RETAIL)) * 100;
        out.printf("   the cell under its cushion sold %,.1f shares; the cell past it bought %,.1f and bids for %,.1f more%n",
                sold6b, (over.shares(RETAIL) - overShares) * 100, ex6b.bookOf(RETAIL).resting(Exchange.CELL + over.key(), OrderBook.Side.BUY));
        close("the cell under its cushion offers HOME_SPEED of the shortfall, at the market", sold6b,
                OutwardInvestment.HOME_SPEED * shortfall / 1.0, 1e-9);
        close("...the cell past its cushion takes all of it", (over.shares(RETAIL) - overShares) * 100, sold6b, 1e-9);
        close("...cell to cell, the transfer counted", ex6b.getBetweenHouseholds(RETAIL), sold6b * 1.0, 1e-9);
        close("...at the market: the price is where it was", ex6b.price(RETAIL), 1.0, 1e-12);
        close("...the seller's savings up by what it was paid", under.savings() * 100, sold6b * 1.0, 1e-9);
        close("...and the buyer bids for the rest of OUT_SPEED of its excess",
                ex6b.bookOf(RETAIL).resting(Exchange.CELL + over.key(), OrderBook.Side.BUY) * 1.0,
                OutwardInvestment.OUT_SPEED * excessOver - sold6b * 1.0, 1e-9);
        close("no pool line moved", ex6b.getSoldToHouseholds() + ex6b.getBoughtFromHouseholds() + ex6b.getHouseholdsBoughtAbroad()
                + ex6b.getHouseholdsSoldAbroad(), 0, 1e-12);

        /* ================= 7. the buyback, and the money that stays ================= */
        out.println("\n--- 7. a company over its target buys back what is asked at its limit, and the rest of its money stays in the till ---");

        double[] normal = new double[12];
        for (int m = 0; m < 12; m++) normal[m] = m < 6 ? 100 : 50;   // profitable but fading: NORMAL
        Equity reg7 = withRecord(RETAIL, normal);
        assertTrue("fixture: the company is in a normal year", reg7.getRegime(RETAIL) == Equity.Regime.NORMAL);
        HouseholdBalance owners = savers(20.0, 4.0);
        reg7.listIfUnlisted(RETAIL, 10_000, owners);        // 10,000 to the founders, a hundred each...
        couple(owners).shares[RETAIL] -= 10;
        reg7.moveDesk(RETAIL, 1_000);                        // ...of which the desk holds 1,000
        Exchange ex7 = new Exchange();
        Bank bank7 = bankWith(1_000_000);
        Firms firms7 = new Firms();
        firms7.assets[RETAIL] = 10_000; firms7.equity[RETAIL] = 10_000;   // all equity: far over target
        firms7.cash[RETAIL] = 5_000; firms7.opex[RETAIL] = 100;           // $600 of cushion
        double[] book7 = bookOf(RETAIL, 10_000);
        double cash7 = bank7.getCash();
        step(ex7, reg7, owners, bank7, firms7, book7, W, .05, 1);           // savers paid 5%: no household buys
        double ask7 = 1.0 * (1 + Exchange.SPREAD / 2);
        double pace7 = Exchange.BUYBACK_PACE / 12 * 9_000 * 1.0;           // on what was outstanding when it bid
        close("it retires a month of the pace, in shares, at the desk's ask",
                reg7.getBoughtBackThisMonth(RETAIL), pace7 / ask7, 1e-9);
        close("...paid from its till", 5_000 - firms7.cash[RETAIL], pace7, 1e-9);
        close("...to the desk, which was asking", ex7.getBuybackToDesk(RETAIL), pace7, 1e-9);
        close("...into the bank's cash", bank7.getCash() - cash7, pace7, 1e-9);
        close("...and the shares are gone", reg7.getShares(RETAIL), 10_000 - pace7 / ask7, 1e-9);
        close("...and nothing of its bid is left to rest: it spent the month's money",
                ex7.bookOf(RETAIL).resting(Equity.COMPANIES[RETAIL], OrderBook.Side.BUY), 0, 1e-9);

        // ...AND THE CUSHION COUNTS THE LOAN PAYMENTS (0.7.11, round 2).
        Equity reg7d = withRecord(RETAIL, normal);
        HouseholdBalance owners7d = savers(20.0, 4.0);
        reg7d.listIfUnlisted(RETAIL, 10_000, owners7d);
        Exchange ex7d = new Exchange();
        Firms firms7d = new Firms();
        firms7d.assets[RETAIL] = 10_000; firms7d.equity[RETAIL] = 10_000;
        firms7d.cash[RETAIL] = 5_000; firms7d.opex[RETAIL] = 100; firms7d.debt[RETAIL] = 750;
        assertTrue("fixture: six months of its costs with the payments are more than its till, without them far less",
                Exchange.BUYBACK_CUSHION_MONTHS * (100 + 750) > 5_000
                        && 5_000 - Exchange.BUYBACK_CUSHION_MONTHS * 100 >= pace7);
        step(ex7d, reg7d, owners7d, bankWith(1_000_000), firms7d, book7, W, .05, 1);
        close("a company whose loan payments take its cushion past its till buys nothing back",
                reg7d.getBoughtBackThisMonth(RETAIL), 0, 1e-12);
        close("...bids for nothing either", ex7d.bookOf(RETAIL).resting(Equity.COMPANIES[RETAIL], OrderBook.Side.BUY), 0, 0);
        close("...and its till is untouched", firms7d.cash[RETAIL], 5_000, 1e-12);

        // The shares dear: they last traded at half again fair value, and
        // what is asked rests there. Round 2 paid the money out as a special
        // dividend here; since round 4 (Jerus: "Keep it in the company") it
        // stays in the till, and the company's bid rests at its limit.
        Equity reg7s = withRecord(RETAIL, normal);
        addPaidOnly(reg7s, RETAIL, 1);                      // a small dividend paid, so the world holds a view
        HouseholdBalance owners7s = savers(20.0, 4.0);
        reg7s.listIfUnlisted(RETAIL, 10_000, owners7s);
        couple(owners7s).shares[RETAIL] -= 10;
        reg7s.moveForeign(RETAIL, 1_000);                    // the world holds 1,000
        Exchange ex7s = new Exchange();
        ex7s.bookOf(RETAIL).seedLastPrice(1.5);
        Firms firms7s = new Firms();
        firms7s.assets[RETAIL] = 10_000; firms7s.equity[RETAIL] = 10_000;
        firms7s.cash[RETAIL] = 5_000; firms7s.opex[RETAIL] = 100;
        double shares7s = reg7s.getShares(RETAIL);
        step(ex7s, reg7s, owners7s, dead, firms7s, book7, W, .05, 1);
        assertTrue("fixture: the only ask rests past fair value plus the tolerance",
                ex7s.bestAsk(RETAIL) > ex7s.fair(RETAIL) * (1 + Exchange.BUYBACK_TOLERANCE));
        double pace7s = Exchange.BUYBACK_PACE / 12 * reg7s.getOutstanding(RETAIL) * ex7s.fair(RETAIL);
        double limit7s = ex7s.fair(RETAIL) * (1 + Exchange.BUYBACK_TOLERANCE);
        close("so the money stays in the till", firms7s.cash[RETAIL], 5_000, 1e-9);
        close("...and the bid rests at its limit, fair value plus the tolerance",
                ex7s.bestBidOf(RETAIL, Equity.COMPANIES[RETAIL]), limit7s, 1e-12);
        close("...for what the month's money buys there",
                ex7s.bookOf(RETAIL).resting(Equity.COMPANIES[RETAIL], OrderBook.Side.BUY), pace7s / limit7s, 1e-9);
        close("...no holder is paid a dividend for it", reg7s.getDividendHomeThisMonth(RETAIL) + reg7s.getDividendAbroadThisMonth(RETAIL), 0, 0);
        close("...not one share retired", reg7s.getShares(RETAIL), shares7s, 1e-12);

        // ...AND WHEN NOBODY OFFERS THE SHARES AT ALL: every share at home with
        // owners content, no desk, nothing abroad. Round 3 paid the money out
        // as a special dividend here; since round 4 it stays in the till, the
        // bid rests at its limit, and a seller who comes later in the month
        // is met there.
        Equity reg7n = withRecord(RETAIL, normal);
        HouseholdBalance owners7n = savers(20.0, 4.0);
        reg7n.listIfUnlisted(RETAIL, 10_000, owners7n);
        Exchange ex7n = new Exchange();
        Firms firms7n = new Firms();
        firms7n.assets[RETAIL] = 10_000; firms7n.equity[RETAIL] = 10_000;
        firms7n.cash[RETAIL] = 5_000; firms7n.opex[RETAIL] = 100;
        double shares7n = reg7n.getShares(RETAIL);
        step(ex7n, reg7n, owners7n, dead, firms7n, book7, W, .05, 1);
        double pace7n = Exchange.BUYBACK_PACE / 12 * reg7n.getOutstanding(RETAIL) * ex7n.fair(RETAIL);
        assertTrue("fixture: nobody offers the company's shares at all", Double.isNaN(ex7n.bestAsk(RETAIL)));
        double limit7n = ex7n.fair(RETAIL) * (1 + Exchange.BUYBACK_TOLERANCE);
        close("with nobody offering its shares, the money stays in the till", firms7n.cash[RETAIL], 5_000, 1e-9);
        close("...and the bid rests at its limit", ex7n.bestBidOf(RETAIL, Equity.COMPANIES[RETAIL]), limit7n, 1e-12);
        close("...for what the month's money buys there",
                ex7n.bookOf(RETAIL).resting(Equity.COMPANIES[RETAIL], OrderBook.Side.BUY), pace7n / limit7n, 1e-9);
        close("...not one share retired yet", reg7n.getShares(RETAIL), shares7n, 1e-12);
        Household short7n = couple(owners7n);
        double held7n = short7n.shares(RETAIL);
        double raised7n = ex7n.sellForHousehold(reg7n, dead, owners7n, short7n, 20) * 100;
        assertTrue("fixture: a household short of money sells in the waterfall and the bid meets it", raised7n > 0);
        close("a household short of money sells into the resting bid, at the bid's price",
                (held7n - short7n.shares(RETAIL)) * 100 * limit7n, raised7n, 1e-9);
        close("...paid from the company's till", 5_000 - firms7n.cash[RETAIL], raised7n, 1e-9);
        close("...the shares retired", shares7n - reg7n.getShares(RETAIL), raised7n / limit7n, 1e-9);
        close("...counted as a buyback from the households", ex7n.getBuybackToHouseholds(RETAIL), raised7n, 1e-9);
        close("...and the rest of the bid still rests", ex7n.bookOf(RETAIL).resting(Equity.COMPANIES[RETAIL], OrderBook.Side.BUY),
                (pace7n - raised7n) / limit7n, 1e-9);

        Equity loser = withRecord(RETAIL, months(-10));
        HouseholdBalance owners7l = savers(20.0, 4.0);
        loser.listIfUnlisted(RETAIL, 10_000, owners7l);
        Exchange ex7l = new Exchange();
        Firms firms7l = new Firms();
        firms7l.assets[RETAIL] = 10_000; firms7l.equity[RETAIL] = 10_000; firms7l.cash[RETAIL] = 5_000;
        step(ex7l, loser, owners7l, bankWith(1_000_000), firms7l, book7, W, .05, 1);
        close("a company in a bad year buys nothing back", loser.getBoughtBackThisMonth(RETAIL), 0, 1e-12);
        close("...and bids for nothing", ex7l.bookOf(RETAIL).resting(Equity.COMPANIES[RETAIL], OrderBook.Side.BUY), 0, 0);
        Equity atTarget = withRecord(RETAIL, normal);
        HouseholdBalance owners7t = savers(20.0, 4.0);
        atTarget.listIfUnlisted(RETAIL, 10_000, owners7t);
        Firms firms7t = new Firms();
        firms7t.assets[RETAIL] = 10_000; firms7t.cash[RETAIL] = 5_000;
        firms7t.equity[RETAIL] = atTarget.getTargetEquityShare(RETAIL) * 10_000;   // at target
        Exchange ex7t = new Exchange();
        step(ex7t, atTarget, owners7t, bankWith(1_000_000), firms7t, book7, W, .05, 1);
        close("a company at its target buys nothing back", atTarget.getBoughtBackThisMonth(RETAIL), 0, 1e-12);

        /* ================= 8. the split ================= */
        out.println("\n--- 8. a share that gets too dear is split, and one too cheap consolidated ---");

        Equity reg8 = new Equity();
        HouseholdBalance few = savers(20.0, 4.0);
        reg8.listIfUnlisted(INDUSTRY, 10, few);              // ten shares in a hundred households
        double[] book8 = bookOf(INDUSTRY, 5_000);            // $500 a share
        Exchange ex8 = new Exchange();
        ex8.bookOf(INDUSTRY).seedLastPrice(520);             // last traded at $520
        Bank bank8 = bankWith(100_000);
        double valueBefore = few.sharesHeld(INDUSTRY) * 520;
        step(ex8, reg8, few, bank8, new Firms(), book8, W, .10, 1);
        close("a share last traded at five hundred and twenty is split a hundred for one", ex8.getSplit(INDUSTRY), 100, 1e-12);
        close("...the register's count by the factor", reg8.getShares(INDUSTRY), 1_000, 1e-9);
        close("...every household's count by the factor", couple(few).shares(INDUSTRY), 10, 1e-9);
        close("...the last trade by its inverse: the price", ex8.price(INDUSTRY), 5.2, 1e-9);
        close("...fair value with it", ex8.fair(INDUSTRY), 5.0, 1e-9);
        close("...and what the households hold is worth what it was", few.sharesHeld(INDUSTRY) * ex8.price(INDUSTRY), valueBefore, 1e-9);
        close("the desk's resting bid moves too: its price by the inverse", ex8.bestBid(INDUSTRY), 5.0 * (1 - Exchange.SPREAD / 2), 1e-9);
        close("...its quantity by the factor: still the ten shares' worth it was", ex8.bookOf(INDUSTRY).resting(DESK, OrderBook.Side.BUY), 1_000, 1e-9);
        close("the split factor remembers it", ex8.getSplitFactor(INDUSTRY), 100, 1e-12);
        close("...so the price per FOUNDING share is what it was: the history has no cliff",
                ex8.pricePerFoundingShare(INDUSTRY), 520.0, 1e-9);

        Equity reg8b = new Equity();
        HouseholdBalance lots = savers(20.0, 4.0);
        reg8b.listIfUnlisted(INDUSTRY, 1_000_000, lots);     // a million shares
        Exchange ex8b = new Exchange();
        step(ex8b, reg8b, lots, bank8, new Firms(), bookOf(INDUSTRY, 100), W, .10, 1);   // worth a hundredth of a cent each
        close("a share priced at a ten-thousandth is consolidated ten thousand for one", ex8b.getSplit(INDUSTRY), 1e-4, 1e-15);
        close("...to a hundred shares", reg8b.getShares(INDUSTRY), 100, 1e-9);
        close("...at a dollar", ex8b.price(INDUSTRY), 1.0, 1e-9);
        close("...the households still agreeing with the register", lots.sharesHeld(INDUSTRY), reg8b.getDomesticShares(INDUSTRY), 1e-9);

        /* ================= 9. the save ================= */
        out.println("\n--- 9. the exchange survives a save: the books, their orders, the last trade ---");

        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create();
        Exchange back = new Exchange();
        Exchange.State s1 = gson.fromJson(gson.toJson(ex1.toState()), Exchange.State.class);
        assertTrue("it restores", back.restore(s1));
        close("...the last trade, so the price", back.price(INDUSTRY), ex1.price(INDUSTRY), 1e-12);
        close("...fair value", back.fair(INDUSTRY), ex1.fair(INDUSTRY), 1e-12);
        close("...the desk's resting ask", back.bookOf(INDUSTRY).resting(DESK, OrderBook.Side.SELL),
                ex1.bookOf(INDUSTRY).resting(DESK, OrderBook.Side.SELL), 1e-12);
        close("...and its bid", back.bookOf(INDUSTRY).resting(DESK, OrderBook.Side.BUY),
                ex1.bookOf(INDUSTRY).resting(DESK, OrderBook.Side.BUY), 1e-12);
        Exchange backSplit = new Exchange();
        backSplit.restore(gson.fromJson(gson.toJson(ex8.toState()), Exchange.State.class));
        close("...and the split factor", backSplit.getSplitFactor(INDUSTRY), 100, 1e-12);
        assertTrue("...closed until the bank says otherwise", !back.isOpen());
        back.reopen(bank1);
        assertTrue("...open once a bank with capital is put back", back.isOpen());
        back.reopen(dead);
        assertTrue("...and not for a bank without", !back.isOpen());

        String[] keys = Equity.COMPANIES.clone();
        double[] dealer = new double[keys.length * Exchange.SLOTS + 1];
        int at = INDUSTRY * Exchange.SLOTS;
        dealer[at] = 2.5; dealer[at + 1] = 2.4; dealer[at + 2] = 7; dealer[at + 3] = 100;
        Exchange old = new Exchange();
        assertTrue("the dealer's save, four a company, still restores", old.restore(keys, dealer));
        close("...its last quote the book's last price, so where the market opens", old.price(INDUSTRY), 2.5, 1e-12);
        close("...fair value and the split factor as they were", old.fair(INDUSTRY) + old.getSplitFactor(INDUSTRY), 2.4 + 100, 1e-12);
        assertTrue("...and the demand it carried is no order", old.bookOf(INDUSTRY).bids().isEmpty());
        double[] firstNight = new double[keys.length * Exchange.SLOTS_BEFORE_SPLITS + 1];
        Exchange older = new Exchange();
        assertTrue("the exchange's first-night save, three a company, still restores", older.restore(keys, firstNight));
        close("...with one share then being one share now", older.getSplitFactor(INDUSTRY), 1, 1e-12);
        assertTrue("an array of the wrong length is refused whole", !new Exchange().restore(keys, new double[] {1, 2}));

        /* ================= 10. a live city ================= */
        out.println("\n--- 10. and a city with a market in it still adds up, prices at its last trades, and comes back from a save ---");

        Path root = Files.createTempDirectory("exchangecheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game city = new Game(files);
        System.setOut(quiet);
        double worstResidual = 0;
        int monthsTraded = 0, lastTradeMisread = 0, tradedBooks = 0;
        double bought = 0, soldBack = 0, boughtBack = 0, between = 0, emigrants = 0;
        double worstMarkGap = 0;
        int excessMonths = 0;
        double worstExcessResidual = 0, excessSoldBefore = 0;
        try {
            city.run();
            for (int m = 0; m < 240; m++) {
                city.simulateMonths(1);
                worstResidual = Math.max(worstResidual, Math.abs(city.getLastMoneyAudit().relative()));
                Exchange x = city.getExchange();
                // The months the desk's excess over its caps sold (round 4).
                if (x.getLifeExcessSold() > excessSoldBefore + 1e-12) {
                    excessMonths++;
                    worstExcessResidual = Math.max(worstExcessResidual, Math.abs(city.getLastMoneyAudit().residual));
                }
                excessSoldBefore = x.getLifeExcessSold();
                if (x.getVolume() > 0) monthsTraded++;
                bought += x.getSoldToHouseholds();
                soldBack += x.getBoughtFromHouseholds() + x.getBoughtFromAbroad();
                boughtBack += x.getBuybackToHouseholds() + x.getBuybackToDesk() + x.getBuybackAbroad();
                between += x.getBetweenHouseholds();
                emigrants += x.getEmigrantsPaid();
                double mark = x.markToMarket(city.getEquity());
                worstMarkGap = Math.max(worstMarkGap, Math.abs(city.getBank().getSecurities() - mark) / Math.max(1, Math.abs(mark)));
                for (int c = 0; c < N; c++) {
                    if (!x.hasTraded(c)) continue;
                    tradedBooks++;
                    if (x.price(c) != x.bookOf(c).lastPrice()) lastTradeMisread++;
                }
            }
        } finally {
            System.setOut(out);
        }
        Exchange x = city.getExchange();
        Equity register = city.getEquity();
        HouseholdBalance people = city.getHouseholdBalance();
        out.printf("   after 20 years: traded in %d months, %,.0f shares in %,d trades; the desk sold households $%,.0fk,"
                + " bought $%,.0fk; companies retired $%,.0fk; cell to cell $%,.0fk;"
                + " leavers paid $%,.0fk; the desk holds $%,.0fk%n",
                monthsTraded, x.getLifetimeVolume(), x.getLifeTrades(), bought, soldBack, boughtBack, between,
                emigrants, city.getBank().getSecurities());
        assertTrue("the city's shares traded on the book", monthsTraded > 0 && x.getLifeTrades() > 0);
        assertTrue("...and every month of it is conserved", worstResidual < 1e-9);
        out.printf("   the desk's excess over its caps sold in %d month(s), $%,.0fk offered and $%,.0fk sold over the 20 years%n",
                excessMonths, x.getLifeExcessOffered(), x.getLifeExcessSold());
        assertTrue("fixture: the desk's excess over its caps sold in some month", excessMonths > 0);
        assertTrue("...and the audit closed through every month it did", worstExcessResidual < .01);
        assertTrue("...with the bank carrying the desk at the exchange's mark all along", worstMarkGap < 1e-9);
        assertTrue("...and every price read, on a book that has traded, is its last trade (" + tradedBooks + " company-months)",
                tradedBooks > 0 && lastTradeMisread == 0);
        boolean agree = true, whole = true;
        for (int c = 0; c < N; c++) {
            double reg_ = register.getDomesticShares(c), held_ = people.sharesHeld(c);
            if (Math.abs(reg_ - held_) > 1e-9 * Math.max(1, register.getShares(c))) agree = false;
            if (register.getDealerShares(c) < -1e-9) whole = false;
        }
        assertTrue("...the register and the cells agreeing on every company", agree);
        assertTrue("...and the desk never short of anything", whole);

        assertTrue("the city saves", city.saveGame(1, "exchangecheck").ok);
        Game again = new Game(files);
        System.setOut(quiet);
        again.loadGameSave(1);
        System.setOut(out);
        assertTrue("...and loads", again.getLoadFailure() == null);
        boolean pricesBack = true, booksBack = true, deskBack = true;
        for (int c = 0; c < N; c++) {
            Exchange y = again.getExchange();
            if (Math.abs(y.price(c) - x.price(c)) > 1e-12 * Math.max(1, x.price(c))
                    || Math.abs(y.fair(c) - x.fair(c)) > 1e-12 * Math.max(1, x.fair(c))) pricesBack = false;
            if (y.bookOf(c).bids().size() != x.bookOf(c).bids().size() || y.bookOf(c).asks().size() != x.bookOf(c).asks().size()) booksBack = false;
            for (OrderBook.Side side : OrderBook.Side.values()) {
                if (Math.abs(y.bookOf(c).depth(side, side == OrderBook.Side.BUY ? 0 : Double.MAX_VALUE)
                        - x.bookOf(c).depth(side, side == OrderBook.Side.BUY ? 0 : Double.MAX_VALUE)) > 1e-9) booksBack = false;
            }
            if (Math.abs(again.getEquity().getDealerShares(c) - register.getDealerShares(c)) > 1e-9 * Math.max(1, Math.abs(register.getDealerShares(c)))) deskBack = false;
        }
        assertTrue("every last trade and fair value comes back", pricesBack);
        assertTrue("...every book with the orders resting on it", booksBack);
        assertTrue("...and the desk's inventory", deskBack);
        close("...at the same mark", again.getBank().getSecurities(), city.getBank().getSecurities(), 1e-6 * Math.max(1, Math.abs(city.getBank().getSecurities())));

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** Twelve months of this ordinary dividend paid, no income recorded: a register whose regime its income sets elsewhere. */
    static void addPaidOnly(Equity e, int company, double monthlyPaid) {
        for (int m = 0; m < Equity.RECORD_MONTHS; m++) {
            e.noteDividendPaid(company, monthlyPaid);
            e.closeDividendMonth();
        }
    }
}
