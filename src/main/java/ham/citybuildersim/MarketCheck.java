package ham.citybuildersim;

import java.io.PrintStream;

/**
 * Verifies the efficient market (EM1, 2026-10-29): one value for a share,
 * every holder reading it, and a price that follows it - each claim on a
 * fixture that causes it, not on a city that happens to show it.
 *
 * WHY. Households held about 100% of every company from founding and sold
 * only short of money; a company's buyback, bid 10% over value, filled
 * $0.58B of $4.4T a month. A share was worth the more of its book and its
 * dividends, and a book is 95% cash and dollars abroad: boxes of cash priced
 * as boxes of cash, earning less than the dollars abroad they could be
 * (Jerus, 2026-10-28: "why hold something that is 1.5 book value, when it
 * can get something else that is 1.2 book value and has same P/E ... perhaps
 * make it an actual efficient market"). ExchangeCheck holds what the book
 * does with an order; this holds that the orders are the right ones.
 *
 * WHAT THIS HAS TO PROVE (docs/queue/EM1.md, "Checks"):
 *
 *   1. ONE VALUE: a share is worth its spare money at face and its
 *      earnings capitalised at the required return. A company with real
 *      earnings and little money is valued on them, not its book; a cash box
 *      on its money; a company with under RECORD_MONTHS months on its
 *      prospects; and the parts add up.
 *   2. ONE YARDSTICK: the expected return at fair value is the required
 *      return for a company with no money, what its money earns for a cash
 *      box, and falls as the price rises.
 *   3. TWO ALIKE BUT FOR PRICE: with one value the two prices close; with the
 *      desk closed the money goes to the cheaper one and nobody pays the
 *      dearer one's price; a holder switches from a share to a cheaper one
 *      that returns more than a band over it, and not to one within the
 *      band.
 *   4. THE BUYBACK FINDS ITS SELLERS: a bid over value by more than BAND is
 *      sold into by the holders; at the equity target there is no bid and no
 *      sale.
 *   5. A SHARE EARNING LESS THAN DOLLARS ABROAD IS SOLD - by the cells and by
 *      the world; one earning well above it is not.
 *   6. A QUIET MARKET does not churn: every price at fair, no trade, no
 *      resting sell from a holder, and the same market with one share
 *      returning a band less does trade.
 *   7. THE SAVE: the figures fair value was struck on round-trip through the
 *      exchange's state, an old save strikes them at the next step, and a
 *      redenomination scales them.
 *
 * Every claim is CAUSED: a register handed a record and a company handed its
 * money; two shares that differ in nothing but the price of their last
 * trade; a bid placed over value; a cash box whose money earns less than
 * dollars abroad; a market of shares inside each other's band.
 */
public class MarketCheck {

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

    static final double W = ExchangeCheck.W;
    static final int N = Equity.COMPANIES.length;
    /** What a share is asked to earn: the world's rate plus the premium (Equity.requiredReturn()). */
    static final double REQUIRED = Equity.requiredReturn(W);

    /**
     * A market on a table: a register, the households that hold it, a bank
     * for the desk, the companies' tills and one exchange - listed company by
     * company, each given a year of earnings and the money it holds.
     */
    static final class City {
        final Equity reg = new Equity();
        final HouseholdBalance hb;
        final Bank bank;
        final ExchangeCheck.Firms firms = new ExchangeCheck.Firms();
        final double[] book = new double[N];
        final Exchange ex = new Exchange();
        int month = 0;

        City(HouseholdBalance hb, Bank bank) {
            this.hb = hb;
            this.bank = bank;
        }

        /** Lists the company, `shares` founders' shares on a book of as many founding prices, with a year of this net income on its record. */
        City list(int c, double shares, double yearlyEarnings) {
            book[c] = shares * reg.foundingPrice();
            reg.listIfUnlisted(c, book[c], hb);
            for (int m = 0; m < Equity.RECORD_MONTHS; m++) reg.recordMonth(c, yearlyEarnings / Equity.RECORD_MONTHS, 0);
            return this;
        }

        /** The company's money (its till, and the same as the money Exchange.Companies.money() says), what that earns it a year, and its monthly operating cost, which sets the cushion. */
        City firm(int c, double opexMonthly, double money, double moneyEarnsYearly) {
            firms.opex[c] = opexMonthly;
            firms.money[c] = money;
            firms.cash[c] = money;
            firms.moneyIncome[c] = moneyEarnsYearly;
            return this;
        }

        /** The exchange's step for the next month. */
        void step(double depositRate) {
            month++;
            ExchangeCheck.step(ex, reg, hb, bank, firms, book, W, depositRate, month);
        }
    }

    /** Six months of a company's costs, the cushion a company keeps before its money counts as spare (Exchange.BUYBACK_CUSHION_MONTHS). */
    static double cushion(double opexMonthly) {
        return Exchange.BUYBACK_CUSHION_MONTHS * opexMonthly;
    }

    /** Two cells of a hundred couples each: the unskilled with this take-home, the skilled with theirs (both struck once, savings left to the caller). */
    static HouseholdBalance twoCells(double poorTakeHome, double richTakeHome) {
        HouseholdBalance hb = new HouseholdBalance();
        double[][] mix = new double[FamilyStructure.values().length][PayTier.values().length];
        mix[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 100;
        mix[FamilyStructure.COUPLE.ordinal()][PayTier.SKILLED.ordinal()] = 100;
        double[] pay = new double[HouseholdBalance.ROWS];
        pay[PayTier.UNSKILLED.ordinal()] = 100 * poorTakeHome;
        pay[PayTier.SKILLED.ordinal()] = 100 * richTakeHome;
        double[] none = new double[HouseholdBalance.ROWS];
        hb.advanceMonth((s, t) -> mix[s.ordinal()][t.ordinal()], pay, 0, none, none, .25, .05, 1);
        return hb;
    }

    static Household poorOf(HouseholdBalance hb) { return hb.cell(FamilyStructure.COUPLE, PayTier.UNSKILLED); }

    static Household richOf(HouseholdBalance hb) { return hb.cell(FamilyStructure.COUPLE, PayTier.SKILLED); }

    static String nameOf(Household cell) { return Exchange.CELL + cell.key(); }

    /** Every share of the company the households hold goes to this one cell (the register's total is unchanged). */
    static void holdOnly(HouseholdBalance hb, int company, Household holder) {
        double total = hb.sharesHeld(company);
        for (Household k : hb.cellsForMarket()) k.shares[company] = 0;
        holder.shares[company] = total / holder.households();
    }

    /** `n` of the households' shares of a company go to the desk, or to the world: the register and the cells both move. */
    static void takeFromHouseholds(Equity reg, HouseholdBalance hb, int company, double n, boolean toDesk) {
        double total = hb.sharesHeld(company);
        double keep = (total - n) / total;
        for (Household k : hb.cellsForMarket()) k.shares[company] *= keep;
        if (toDesk) reg.moveDesk(company, n);
        else reg.moveForeign(company, n);
    }

    /** What a cell holds of a company, all its households together. */
    static double heldBy(Household cell, int company) {
        return cell.shares(company) * cell.households();
    }

    /** Trades made on every book since the step opened them (the books clear their month at the step; Exchange.getLastTrades() reads the month before). */
    static int tradesThisStep(Exchange ex) {
        int trades = 0;
        for (int c = 0; c < N; c++) trades += ex.bookOf(c).trades();
        return trades;
    }

    /** The best the alternatives to a share earn a year: the deposit, the dollars abroad and the city's paper (none in these fixtures), plus the premium. */
    static double alternative(double depositRate) {
        return Math.max(depositRate, W) + Exchange.HOUSEHOLD_PREMIUM;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;

        int RETAIL = Equity.indexOf(Sectors.RETAIL);
        int INDUSTRY = Equity.indexOf(Sectors.INDUSTRY);
        int MATERIALS = Equity.indexOf(Sectors.MATERIALS);
        int ESTATE = Equity.indexOf(Sectors.REAL_ESTATE);

        /* ================= 1. one value ================= */
        section("1. one value: a share is its spare money at face and its earnings capitalised - not its book");

        HouseholdBalance town1 = ExchangeCheck.savers(12.0, 4.0);
        City c1 = new City(town1, ExchangeCheck.bankWith(0));
        c1.list(INDUSTRY, 1_000, 1_200)          // earns $1,200 a year on a book of $1,000...
                .list(ESTATE, 1_000, 2_400)       // ...this one twice that on the same book...
                .list(MATERIALS, 1_000, 1_200)    // ...this one the same as Industry, and a stack of money besides...
                .list(RETAIL, 10_000, 0);         // ...and Retail earns nothing: a cash box.
        c1.firm(INDUSTRY, 100, 300, 0);           // $300 in the till against a $600 cushion: nothing spare
        c1.firm(ESTATE, 100, 300, 0);
        c1.firm(MATERIALS, 100, cushion(100) + 5_000, 100);   // $5,000 past its cushion, earning $100 a year
        c1.firm(RETAIL, 100, cushion(100) + 5_000, 100);
        c1.step(0);

        // (a) real earnings, little money: valued on the earnings.
        double shares1 = c1.reg.getShares(INDUSTRY);
        assertTrue("fixture: Industry's money is under its cushion, so nothing of it is spare",
                c1.firms.money[INDUSTRY] < cushion(100) && c1.ex.spareOf(INDUSTRY) == 0);
        close("a company with real earnings and money under its cushion is worth its earnings over the required return",
                c1.ex.fair(INDUSTRY), c1.reg.getTrailingIncome(INDUSTRY) / Equity.requiredReturn(W) / shares1, 1e-9);
        double bookPerShare1 = c1.reg.bookPerShare(INDUSTRY, c1.book[INDUSTRY]);
        assertTrue("...and not its book: the book is a twenty-fourth of it", c1.ex.fair(INDUSTRY) > 20 * bookPerShare1);
        close("...the exchange quotes the register's own reckoning", c1.ex.fair(INDUSTRY),
                c1.reg.fairValue(INDUSTRY, c1.book[INDUSTRY], c1.ex.spareOf(INDUSTRY), W), 1e-12);
        close("twice the earnings on the same book and money, twice the value",
                c1.ex.fair(ESTATE), 2 * c1.ex.fair(INDUSTRY), 1e-9);

        // (b) a cash box: its money past the cushion, at face.
        double spareRetail = c1.firms.money[RETAIL] - Exchange.BUYBACK_CUSHION_MONTHS * (c1.firms.opex[RETAIL] + c1.firms.debt[RETAIL]);
        assertTrue("fixture: Retail earns nothing and holds $5,000 past its cushion",
                c1.reg.getTrailingIncome(RETAIL) == 0 && Math.abs(spareRetail - 5_000) < 1e-9);
        close("a cash box is worth its spare money at face", c1.ex.fair(RETAIL) * c1.reg.getShares(RETAIL), spareRetail, 1e-9);
        assertTrue("...which is nowhere near its book", Math.abs(c1.ex.fair(RETAIL) - c1.reg.bookPerShare(RETAIL, c1.book[RETAIL])) > .4);
        close("...and the cushion, six months of its costs, is not in it", c1.ex.spareOf(RETAIL), spareRetail, 1e-9);

        // (c) a company on prospects: fewer than RECORD_MONTHS months on the record.
        HouseholdBalance town1c = ExchangeCheck.savers(12.0, 4.0);
        City young = new City(town1c, ExchangeCheck.bankWith(0));
        young.book[INDUSTRY] = 1_000;
        young.reg.listIfUnlisted(INDUSTRY, 1_000, town1c);
        for (int m = 0; m < Equity.RECORD_MONTHS - 1; m++) young.reg.recordMonth(INDUSTRY, 100, 0);
        young.firm(INDUSTRY, 100, cushion(100) + 5_000, 0);
        young.step(0);
        assertTrue("fixture: eleven months on the record, $5,000 past the cushion, earnings that would be worth $24 a share",
                young.reg.getMonthsRecorded(INDUSTRY) == Equity.RECORD_MONTHS - 1 && young.ex.spareOf(INDUSTRY) == 5_000);
        assertTrue("a company with fewer than RECORD_MONTHS months is not valued on its record", !young.reg.valuedOnRecord(INDUSTRY));
        close("...it is on its prospects: its book", young.ex.fair(INDUSTRY), young.reg.bookPerShare(INDUSTRY, young.book[INDUSTRY]), 1e-12);
        close("...none of its money is in the value yet", young.ex.fairSpare(INDUSTRY), 0, 0);
        young.reg.recordMonth(INDUSTRY, 100, 0);
        young.step(0);
        assertTrue("the twelfth month puts it on its record", young.reg.valuedOnRecord(INDUSTRY));
        close("...and its value is its money and its earnings: $5,000 and $1,200 over the required return",
                young.ex.fair(INDUSTRY), (5_000 + 1_200 / REQUIRED) / young.reg.getShares(INDUSTRY), 1e-9);

        // (d) the parts add up, for every kind of company here.
        boolean partsAdd = true;
        for (int c : new int[] {INDUSTRY, ESTATE, MATERIALS, RETAIL}) {
            double parts = c1.ex.fairSpare(c) * c1.reg.getShares(c) + c1.ex.earningsOf(c) / Equity.requiredReturn(W);
            if (Math.abs(parts - c1.ex.fair(c) * c1.reg.getShares(c)) > 1e-9 * Math.max(1, parts)) partsAdd = false;
        }
        assertTrue("the parts add up: spare money per share times the shares, plus the earnings over the required return, is the value", partsAdd);
        close("...Materials, with both, is worth the sum of Industry's earnings and Retail's money",
                c1.ex.fair(MATERIALS) * c1.reg.getShares(MATERIALS),
                c1.ex.fair(INDUSTRY) * c1.reg.getShares(INDUSTRY) + c1.ex.fair(RETAIL) * c1.reg.getShares(RETAIL), 1e-9);
        sectionEnd();

        /* ================= 2. one yardstick ================= */
        section("2. one yardstick: the expected return at the price, from the earnings and what the money earns");

        close("at fair value a company with no money returns the required return, exactly",
                c1.ex.expectedReturn(INDUSTRY, c1.ex.fair(INDUSTRY)), REQUIRED, 1e-12);
        close("...whatever its earnings", c1.ex.expectedReturn(ESTATE, c1.ex.fair(ESTATE)), REQUIRED, 1e-12);
        close("at fair value a cash box returns what its money earns on its spare money: $100 on $5,000",
                c1.ex.expectedReturn(RETAIL, c1.ex.fair(RETAIL)), 100.0 / 5_000.0, 1e-12);
        double mixed = c1.ex.expectedReturn(MATERIALS, c1.ex.fair(MATERIALS));
        assertTrue("a company with both returns between the two", mixed > 100.0 / 5_000.0 && mixed < REQUIRED);
        close("...its earnings and its money's, over what it is worth", mixed, (1_200.0 + 100.0) / (5_000.0 + 1_200.0 / REQUIRED), 1e-12);
        boolean falls = true, constant = true;
        for (int c : new int[] {INDUSTRY, MATERIALS, RETAIL}) {
            double f = c1.ex.fair(c);
            double lo = c1.ex.expectedReturn(c, .8 * f), at = c1.ex.expectedReturn(c, f), hi = c1.ex.expectedReturn(c, 1.25 * f);
            if (!(lo > at && at > hi)) falls = false;
            // ...a share's earnings are what they are: the return times the price is the same at every price.
            if (Math.abs(lo * .8 * f - at * f) > 1e-12 * Math.max(1, at * f) || Math.abs(hi * 1.25 * f - at * f) > 1e-12 * Math.max(1, at * f)) constant = false;
        }
        assertTrue("the expected return falls as the price rises, for every kind of company", falls);
        assertTrue("...by the price alone: the return times the price is the share's earnings and its money's", constant);
        close("a share with no price has no return", c1.ex.expectedReturn(INDUSTRY, 0), 0, 0);
        sectionEnd();

        /* ================= 3. two alike but for price ================= */
        section("3. two shares alike but for the price of their last trade: one value, and the money goes to the cheaper");

        // (a) the desk open, flow both ways. Since EM2 a share's price is its quote, and with a bid and an ask both resting that is the middle: a cell
        // short of money that takes the market asks at the middle, over the desk's bid, and cannot sell to the desk in a book where the desk asks. It
        // reaches the desk's bid where the desk asks nothing - the quote is then the last trade held inside the bid - so the desk holds A, which it asks,
        // and none of B, which it only bids for; the cell under its cushion holds B and the cell with money holds A, so the one sells to the desk's
        // bid and the other buys from its ask, and no cell meets the other.
        HouseholdBalance hb3 = twoCells(4.0, 4.0);
        Household poor3 = poorOf(hb3), rich3 = richOf(hb3);
        poor3.savings = 0;                                   // $12 under its $12 cushion: it offers HOME_SPEED of the shortfall at the market
        rich3.savings = 100;                                 // $88 past it: it buys OUT_SPEED of that
        City c3 = new City(hb3, ExchangeCheck.bankWith(100_000));
        int A = INDUSTRY, B = MATERIALS;
        c3.list(A, 1_000, 1_200).list(B, 1_000, 1_200);      // identical in record, money and shares
        c3.firm(A, 0, 0, 0).firm(B, 0, 0, 0);
        holdOnly(hb3, A, rich3);
        holdOnly(hb3, B, poor3);
        double fair3 = c3.reg.fairValue(A, c3.book[A], 0, W);
        close("fixture: the register values them alike", c3.reg.fairValue(B, c3.book[B], 0, W), fair3, 1e-12);
        double dearA = fair3 * 1.3, cheapB = fair3 * .7;
        c3.ex.bookOf(A).seedLastPrice(dearA);                // A last traded 30% over what it is worth...
        c3.ex.bookOf(B).seedLastPrice(cheapB);               // ...B 30% under
        assertTrue("fixture: their last trades are further apart than the band round value on either side",
                dearA - cheapB > 2 * Exchange.BAND * fair3);
        // The desk holds a little of A, which it asks round the value; the cell with money takes it at its quote, the cell under its cushion gives B at its bid.
        takeFromHouseholds(c3.reg, hb3, A, 10, true);
        assertTrue("fixture: the cell with money holds all of A but the desk's, and the cell under its cushion all of B",
                heldBy(poor3, A) == 0 && heldBy(rich3, B) == 0 && heldBy(poor3, B) > 0 && heldBy(rich3, A) > 0 && c3.reg.getDealerShares(B) == 0);
        c3.step(0);
        double bid3 = fair3 * (1 - Exchange.SPREAD / 2), ask3 = fair3 * (1 + Exchange.SPREAD / 2);
        out.printf("   A last traded at $%.2f and B at $%.2f; after the step A at $%.2f and B at $%.2f, round their value of $%.2f%n",
                dearA, cheapB, c3.ex.price(A), c3.ex.price(B), fair3);
        close("they have one value", c3.ex.fair(A), c3.ex.fair(B), 1e-12);
        assertTrue("the desk's quotes are inside the band round it", Exchange.SPREAD / 2 < Exchange.BAND);
        assertTrue("flow both ways: the cell under its cushion sold to the desk, the cell with spare money bought from it",
                c3.ex.getBoughtFromHouseholds() > 0 && c3.ex.getSoldToHouseholds() > 0);
        assertTrue("...into both books", c3.ex.getVolume(A) > 0 && c3.ex.getVolume(B) > 0);
        assertTrue("the two prices close: they are within a band either side of the value of each other",
                Math.abs(c3.ex.price(A) - c3.ex.price(B)) < 2 * Exchange.BAND * c3.ex.fair(A));
        boolean atQuotes = true;
        for (int c : new int[] {A, B}) {
            // Every share that changed hands changed hands at one of the desk's two quotes, bought or sold.
            double viaDesk = c3.ex.getSoldToHouseholds(c) / ask3 + c3.ex.getBoughtFromHouseholds(c) / bid3;
            if (Math.abs(c3.ex.getVolume(c) - viaDesk) > 1e-9 * Math.max(1, viaDesk)) atQuotes = false;
            double p = c3.ex.price(c);
            if (!(p >= c3.ex.fair(c) * (1 - Exchange.BAND) && p <= c3.ex.fair(c) * (1 + Exchange.BAND))) atQuotes = false;
        }
        assertTrue("every trade was at the desk's bid or its ask, inside the band, and the last of each is inside it too", atQuotes);
        assertTrue("...so neither stale price was paid", c3.ex.price(A) != dearA && c3.ex.price(B) != cheapB
                && c3.ex.lastTrade(A) != dearA && c3.ex.lastTrade(B) != cheapB);
        close("the registers still agree with the cells", c3.reg.getDomesticShares(A), hb3.sharesHeld(A), 1e-9);
        close("...and in the other company", c3.reg.getDomesticShares(B), hb3.sharesHeld(B), 1e-9);

        // (b) the desk closed: only the cells meet, and the money goes where it is cheaper.
        HouseholdBalance hb3b = twoCells(100.0, 4.0);
        Household poor3b = poorOf(hb3b), rich3b = richOf(hb3b);
        poor3b.savings = 0;                                  // $300 under its cushion each: a big shortfall, so a big offer
        rich3b.savings = 100;
        City c3b = new City(hb3b, ExchangeCheck.bankWith(0));
        // The world's short is off here (EM2): it would sell A at SHORT_BAND over its value, under the stale ask, and a cell would buy that. What this holds is what a
        // buyer does with a stale ask nobody undercuts; what the shorts do to one is ShortSellingCheck's.
        c3b.ex.setShortSelling(false);
        c3b.list(A, 1_000, 1_200).list(B, 1_000, 1_200);
        c3b.firm(A, 0, 0, 0).firm(B, 0, 0, 0);
        holdOnly(hb3b, A, poor3b);
        holdOnly(hb3b, B, poor3b);                           // the cell under its cushion holds both and offers both at the market
        c3b.ex.bookOf(A).seedLastPrice(dearA);
        c3b.ex.bookOf(B).seedLastPrice(cheapB);
        double richSavings3b = rich3b.savings();
        assertTrue("fixture: the cell with money holds none of either, and the cell under its cushion is a hundred dollars a household short of it",
                heldBy(rich3b, A) == 0 && heldBy(rich3b, B) == 0 && rich3b.savings() > 0
                        && HouseholdBalance.SHARE_CUSHION_MONTHS * poor3b.disposable() - poor3b.savings() > 100);
        c3b.step(0);
        assertTrue("fixture: the desk is closed", !c3b.ex.isOpen());
        String poorName3b = nameOf(poor3b), richName3b = nameOf(rich3b);
        assertTrue("fixture: the cell under its cushion still offers A at its stale price, to anybody",
                c3b.ex.bookOf(A).resting(poorName3b, OrderBook.Side.SELL) > 0 && c3b.ex.bestAsk(A) == dearA);
        out.printf("   the desk closed: the cell with money bought %,.1f shares of B at $%.2f, where it last traded; A, offered at $%.2f, was left%n",
                heldBy(rich3b, B), c3b.ex.price(B), c3b.ex.bestAsk(A));
        assertTrue("the money of the cell with spare money went to the cheaper one: it holds B now", heldBy(rich3b, B) > 0);
        assertTrue("...and it holds none of the dearer, and has none resting on it", heldBy(rich3b, A) == 0
                && c3b.ex.bookOf(A).resting(richName3b, OrderBook.Side.BUY) == 0);
        close("...paying the cheaper one's own price", c3b.ex.price(B), cheapB, 1e-12);
        assertTrue("...out of its savings", rich3b.savings() < richSavings3b);
        close("nobody paid A's price: not one share of it changed hands", c3b.ex.getVolume(A), 0, 0);
        close("...so its last trade is where it was", c3b.ex.price(A), dearA, 1e-12);
        assertTrue("...while B's cheap ask was taken", c3b.ex.getVolume(B) > 0);
        sectionEnd();

        // (c) the switch.
        section("3c. the switch: a holder sells a share when another on offer returns more than a band over it");

        double alt3c = alternative(0);
        // The cell not short of its cushion holds A; the cell under it holds B and offers it at the market. The desk is open, but full of
        // B to its position limit, so it bids for A and not for B: a desk bidding for B at value takes whatever is offered of it
        // at its bid, and no ask of B rests under it for the holder of A to read (the desk-closed case below has the same market).
        City c3c = switchCity(A, B, fair3, .85 * fair3, 24_000);   // A last traded at its value, B 15% under it
        Household keeper3c = richOf(c3c.hb);
        double keeperHeld = heldBy(keeper3c, A);
        assertTrue("fixture: the desk has capital, and holds B to its position limit", c3c.bank.equity() > Exchange.MIN_DEALER_EQUITY
                && Math.abs(c3c.reg.getDealerShares(B) - Exchange.POSITION_LIMIT * c3c.bank.equity() / fair3) < 1e-9);
        c3c.step(0);
        double bidA = Double.isNaN(c3c.ex.bestBid(A)) ? c3c.ex.price(A) : c3c.ex.bestBid(A);
        double askB = c3c.ex.bestAsk(B);
        assertTrue("fixture: the desk is open and bids for A at its quote, and for none of B", c3c.ex.isOpen()
                && c3c.ex.bookOf(A).resting(Exchange.DESK, OrderBook.Side.BUY) > 0 && c3c.ex.bookOf(B).resting(Exchange.DESK, OrderBook.Side.BUY) == 0);
        assertTrue("fixture: B's ask is on offer, at the price it last traded at", Math.abs(askB - .85 * fair3) < 1e-12);
        assertTrue("fixture: A's return at its bid is not under the alternatives less the band - they alone would keep it",
                c3c.ex.expectedReturn(A, bidA) >= alt3c * (1 - Exchange.BAND));
        assertTrue("fixture: B's return at its ask is more than a band over A's at its bid",
                c3c.ex.expectedReturn(B, askB) > c3c.ex.expectedReturn(A, bidA) / (1 - Exchange.BAND));
        assertTrue("fixture: ...and a buyer would pay that ask (it is under the desk's ask round value)", askB <= fair3 * (1 + Exchange.SPREAD / 2));
        close("the cell holding A posts a sell of it in the step: HOME_SPEED of its holding, valued at the price",
                c3c.ex.getLifeValueSold(), OutwardInvestment.HOME_SPEED * keeperHeld * fair3, 1e-9);
        close("...and it fills: the desk's bid, which is at or over the price the cell asks (fair value less the band)",
                keeperHeld - heldBy(keeper3c, A), OutwardInvestment.HOME_SPEED * keeperHeld, 1e-9);
        close("...at the desk's bid: the last trade of A", c3c.ex.price(A), fair3 * (1 - Exchange.SPREAD / 2), 1e-12);
        assertTrue("...a bid inside the band", c3c.ex.price(A) >= fair3 * (1 - Exchange.BAND));

        // Where the sell is posted: with nothing bidding, its ask rests at the market - or, with the last trade of A under what the
        // cell asks, at fair value less the band. The desk is closed, B is cheaper still.
        City c3e = switchCity(A, B, .9 * fair3, .8 * fair3, 0);
        c3e.step(0);
        double bidA3e = Double.isNaN(c3e.ex.bestBid(A)) ? c3e.ex.price(A) : c3e.ex.bestBid(A);
        assertTrue("fixture: the desk is closed; A last traded 10% under its value, under the price the cell would ask, and B cheaper still",
                !c3e.ex.isOpen() && c3e.ex.price(A) < fair3 * (1 - Exchange.BAND) && c3e.ex.price(B) < c3e.ex.price(A));
        assertTrue("fixture: B's return at its ask is again more than a band over A's at its bid",
                c3e.ex.expectedReturn(B, c3e.ex.bestAsk(B)) > c3e.ex.expectedReturn(A, bidA3e) / (1 - Exchange.BAND));
        close("the cell's sell of A rests at fair value less the band", c3e.ex.bestAskOf(A, nameOf(richOf(c3e.hb))), fair3 * (1 - Exchange.BAND), 1e-12);

        // The same as the first, B also at its value: the same return, and the cell sits.
        City c3d = switchCity(A, B, fair3, fair3, 24_000);
        c3d.step(0);
        double askB3d = c3d.ex.bestAsk(B);
        assertTrue("fixture: B is on offer here too, at its value, returning the same as A at its price",
                Math.abs(askB3d - fair3) < 1e-12 && c3d.ex.isOpen() && c3d.ex.bookOf(B).resting(Exchange.DESK, OrderBook.Side.BUY) == 0
                        && Math.abs(c3d.ex.expectedReturn(B, askB3d) - c3d.ex.expectedReturn(A, c3d.ex.price(A))) < 1e-12);
        close("with B at the same return, within the band, the cell posts nothing", c3d.ex.getLifeValueSold(), 0, 0);
        close("...and has no sell of A resting", c3d.ex.bookOf(A).resting(nameOf(richOf(c3d.hb)), OrderBook.Side.SELL), 0, 0);
        close("...and A is untraded", c3d.ex.getVolume(A), 0, 0);
        sectionEnd();

        /* ================= 4. the buyback finds its sellers ================= */
        section("4. the buyback finds its sellers: a bid past the band is sold into, and no bid is sold into by nobody");

        assertTrue("the band is under the buyback's tolerance, so a company's bid for its own shares is past it",
                Exchange.BAND < Exchange.BUYBACK_TOLERANCE);
        City c4 = buybackCity(RETAIL, false);
        double cash4 = c4.firms.cash[RETAIL];
        double shares4 = c4.reg.getShares(RETAIL), outstanding4 = c4.reg.getOutstanding(RETAIL);
        Equity.Regime regime4 = c4.reg.getRegime(RETAIL);
        c4.step(0);
        double fair4 = c4.ex.fair(RETAIL);
        double limit4 = fair4 * (1 + Exchange.BUYBACK_TOLERANCE);
        double pace4 = Exchange.BUYBACK_PACE / 12 * outstanding4 * fair4;
        assertTrue("fixture: a cash box - nearly all of its value is money past its cushion - over its equity target, in a good year",
                c4.ex.fairSpare(RETAIL) > .5 * fair4 && regime4 != Equity.Regime.BAD && regime4 != Equity.Regime.NEW
                        && c4.firms.equity[RETAIL] > (c4.reg.getTargetEquityShare(RETAIL) + Exchange.OVER_TARGET) * c4.firms.assets[RETAIL]);
        assertTrue("fixture: its bid is past the band: the holders' reservation is under it", limit4 > fair4 * (1 + Exchange.BAND));
        assertTrue("fixture: and the holders would not sell it but for the bid - nothing else puts it under the alternatives",
                c4.ex.expectedReturn(RETAIL, fair4) >= alternative(0) * (1 - Exchange.BAND));
        assertTrue("the cells sold into the bid: the buyback filled", c4.ex.getBuybackToHouseholds(RETAIL) > 0);
        close("...at its limit, fair value plus the tolerance: the last trade", c4.ex.lastTrade(RETAIL), limit4, 1e-12);
        close("...the company's till fell by what it paid", cash4 - c4.firms.cash[RETAIL], c4.ex.getBuybackToHouseholds(RETAIL), 1e-9);
        close("...which is the month's pace, all of it filled", c4.ex.getBuybackToHouseholds(RETAIL), pace4, 1e-9);
        close("...its shares in issue fell by what it bought", shares4 - c4.reg.getShares(RETAIL), pace4 / limit4, 1e-9);
        close("...the register still agrees with the cells", c4.reg.getDomesticShares(RETAIL), c4.hb.sharesHeld(RETAIL), 1e-9);
        close("...and no bid is left resting, the cells sold more than it could pay for",
                c4.ex.bookOf(RETAIL).resting(Equity.COMPANIES[RETAIL], OrderBook.Side.BUY), 0, 1e-9);
        assertTrue("...the cells' remainder rests as an ask at the price", c4.ex.bookOf(RETAIL).resting(nameOf(poorOf(c4.hb)), OrderBook.Side.SELL) > 0);

        City c4b = buybackCity(RETAIL, true);
        double cash4b = c4b.firms.cash[RETAIL];
        c4b.step(0);
        assertTrue("fixture: at its equity target it does not buy back: no bid of its rests",
                c4b.ex.bookOf(RETAIL).resting(Equity.COMPANIES[RETAIL], OrderBook.Side.BUY) == 0);
        assertTrue("fixture: the same holders, nothing under the alternatives by a band",
                c4b.ex.expectedReturn(RETAIL, c4b.ex.price(RETAIL)) >= alternative(0) * (1 - Exchange.BAND));
        close("the same company at its equity target fills nothing from the same cells", c4b.ex.getBuybackToHouseholds(RETAIL), 0, 0);
        close("...not one share of it changed hands", c4b.ex.getVolume(RETAIL), 0, 0);
        close("...its till is untouched", c4b.firms.cash[RETAIL], cash4b, 0);
        close("...and nothing was put up for sale on a value", c4b.ex.getLifeValueSold(), 0, 0);
        sectionEnd();

        /* ================= 5. a share earning less than dollars abroad is sold ================= */
        section("5. a share earning less than the dollars abroad is sold, by the cells and by the world; one earning well above is not");

        HouseholdBalance hb5 = ExchangeCheck.savers(12.0, 4.0);   // at its cushion: not short, nothing spare
        City c5 = new City(hb5, ExchangeCheck.bankWith(100_000));
        c5.list(RETAIL, 10_000, 0).list(INDUSTRY, 1_000, 1_200);
        double money5 = cushion(100) + 10_000;
        c5.firm(RETAIL, 100, money5, .005 * money5);         // $10,000 past the cushion, earning half a percent: under the 2% abroad
        c5.firm(INDUSTRY, 0, 0, 0);
        takeFromHouseholds(c5.reg, hb5, RETAIL, 2_000, false);     // a fifth of each held abroad
        takeFromHouseholds(c5.reg, hb5, INDUSTRY, 200, false);
        Household cell5 = ExchangeCheck.couple(hb5);
        double cellsHeldRetail = heldBy(cell5, RETAIL), worldHeldRetail = c5.reg.getForeignShares(RETAIL);
        double cellsHeldIndustry = heldBy(cell5, INDUSTRY), worldHeldIndustry = c5.reg.getForeignShares(INDUSTRY);
        c5.step(0);
        double fairRetail5 = c5.ex.fair(RETAIL);
        double deskBid5 = fairRetail5 * (1 - Exchange.SPREAD / 2);
        assertTrue("fixture: Retail's money earns under the world's rate, and its return at the desk's bid is under the alternatives less the band",
                c5.ex.moneyIncomeOf(RETAIL) / c5.ex.spareOf(RETAIL) < W
                        && c5.ex.expectedReturn(RETAIL, deskBid5) < alternative(0) * (1 - Exchange.BAND)
                        && c5.ex.expectedReturn(RETAIL, deskBid5) < (W + Equity.FOREIGN_PREMIUM) * (1 - Exchange.FOREIGN_TOLERANCE));
        assertTrue("fixture: the desk's bid is not over value by the band, so it is the return that sells it, not the bid",
                deskBid5 <= fairRetail5 * (1 + Exchange.BAND));
        // The world posted first and sold to the desk, so the price the cells read when they post is the desk's bid, the last trade.
        close("the cells post sells of it: HOME_SPEED of their holding, valued at the price they read (the desk's bid, where the world just sold)",
                c5.ex.getLifeValueSold(), OutwardInvestment.HOME_SPEED * cellsHeldRetail * deskBid5, 1e-9);
        close("...and some fill, to the desk's bid, all of what they put up", cellsHeldRetail - heldBy(cell5, RETAIL),
                OutwardInvestment.HOME_SPEED * cellsHeldRetail, 1e-9);
        double yBid5 = c5.ex.expectedReturn(RETAIL, deskBid5);
        close("the world sells FOREIGN_SPEED of what it holds in proportion to how far its return is under the hurdle",
                worldHeldRetail - c5.reg.getForeignShares(RETAIL),
                Exchange.FOREIGN_SPEED * (1 - yBid5 / (W + Equity.FOREIGN_PREMIUM)) * worldHeldRetail, 1e-9);
        close("...both to the desk, at its bid: what it bought in all", c5.reg.getDealerShares(RETAIL),
                (cellsHeldRetail - heldBy(cell5, RETAIL)) + (worldHeldRetail - c5.reg.getForeignShares(RETAIL)), 1e-9);
        close("...at the desk's bid: the last trade", c5.ex.price(RETAIL), deskBid5, 1e-12);
        close("Industry, earning the required return, is not sold by the cells", cellsHeldIndustry - heldBy(cell5, INDUSTRY), 0, 1e-12);
        close("...nor by the world", worldHeldIndustry - c5.reg.getForeignShares(INDUSTRY), 0, 1e-12);
        close("...not one share of it changed hands", c5.ex.getVolume(INDUSTRY), 0, 0);
        sectionEnd();

        /* ================= 6. a quiet market ================= */
        section("6. a quiet market does not churn: no trade, no sell from a holder, no price moves - and a share a band behind does trade");

        City c6 = quietCity(ESTATE, INDUSTRY, MATERIALS, 0);
        // INDUSTRY earns $1,200, MATERIALS $1,200 with $1,000 of money earning 2%, ESTATE the same as INDUSTRY: all within the band of each other.
        double[] last6 = new double[N];
        for (int c : new int[] {INDUSTRY, MATERIALS}) {
            last6[c] = c6.reg.fairValue(c, c6.book[c], c6.firms.money[c], W);
            c6.ex.bookOf(c).seedLastPrice(last6[c]);
        }
        String cellName6 = nameOf(ExchangeCheck.couple(c6.hb));
        boolean fixtureOk = true;
        c6.step(0);
        for (int c : new int[] {INDUSTRY, MATERIALS}) {
            double r = c6.ex.expectedReturn(c, c6.ex.fair(c));
            for (int d : new int[] {INDUSTRY, MATERIALS}) {
                if (c6.ex.expectedReturn(d, c6.ex.fair(d)) < r * (1 - Exchange.BAND)) fixtureOk = false;
            }
            if (r < alternative(0) * (1 - Exchange.BAND)) fixtureOk = false;
        }
        assertTrue("fixture: every return at value is within the band of the other's, and none under the alternatives less the band", fixtureOk);
        assertTrue("fixture: the desk is open and offers both - the switch is in play, asks on offer to read",
                c6.ex.isOpen() && !Double.isNaN(c6.ex.bestAsk(INDUSTRY)) && !Double.isNaN(c6.ex.bestAsk(MATERIALS)));
        boolean quiet = tradesThisStep(c6.ex) == 0 && c6.ex.getLifeValueSold() == 0;
        boolean unmoved = true, noSell = true;
        for (int m = 2; m <= 7; m++) {
            c6.step(0);
            if (tradesThisStep(c6.ex) != 0 || c6.ex.getLifeValueSold() != 0) quiet = false;
            for (int c : new int[] {INDUSTRY, MATERIALS}) {
                if (c6.ex.price(c) != last6[c]) unmoved = false;
                if (Math.abs(c6.ex.fair(c) - last6[c]) > 1e-12) unmoved = false;
                if (c6.ex.bookOf(c).resting(cellName6, OrderBook.Side.SELL) != 0
                        || c6.ex.bookOf(c).resting(Exchange.WORLD, OrderBook.Side.SELL) != 0) noSell = false;
            }
        }
        assertTrue("after seven steps no trade has been made", quiet);
        assertTrue("...no holder has a sell resting: neither the cells nor the world", noSell);
        assertTrue("...and every price is where it was, the last trade and the value both", unmoved);

        // ...and the same market with one share returning a band less than the other trades: the fixture can see churn.
        City c6b = quietCity(ESTATE, INDUSTRY, MATERIALS, 5_000);   // Materials holds $5,000 of money earning 2%: a return well under Industry's
        for (int c : new int[] {INDUSTRY, MATERIALS}) {
            c6b.ex.bookOf(c).seedLastPrice(c6b.reg.fairValue(c, c6b.book[c], c6b.firms.money[c], W));
        }
        c6b.step(0);
        double rI = c6b.ex.expectedReturn(INDUSTRY, c6b.ex.fair(INDUSTRY)), rM = c6b.ex.expectedReturn(MATERIALS, c6b.ex.fair(MATERIALS));
        assertTrue("fixture: Materials now returns more than a band under Industry, and over the alternatives", rM < rI * (1 - Exchange.BAND)
                && rM >= alternative(0) * (1 - Exchange.BAND));
        assertTrue("the same market with a share a band behind: a holder sells it for the other", c6b.ex.getLifeValueSold() > 0);
        assertTrue("...and it trades", tradesThisStep(c6b.ex) > 0 && c6b.ex.getVolume(MATERIALS) > 0);
        sectionEnd();

        /* ================= 7. the save ================= */
        section("7. the figures fair value was struck on survive a save, an old save strikes them again, and a redenomination scales them");

        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().serializeSpecialFloatingPointValues().create();
        Exchange back = new Exchange();
        assertTrue("the state restores through Gson", back.restore(gson.fromJson(gson.toJson(c1.ex.toState()), Exchange.State.class)));
        boolean figures = true;
        for (int c = 0; c < N; c++) {
            if (back.spareOf(c) != c1.ex.spareOf(c) || back.earningsOf(c) != c1.ex.earningsOf(c)
                    || back.moneyIncomeOf(c) != c1.ex.moneyIncomeOf(c) || back.fair(c) != c1.ex.fair(c)) figures = false;
        }
        assertTrue("...with every company's spare money, earnings, what its money earns and its value as they were", figures);
        assertTrue("fixture: there is something to carry: Retail's spare money, Industry's earnings, Materials' money's income",
                back.spareOf(RETAIL) > 0 && back.earningsOf(INDUSTRY) > 0 && back.moneyIncomeOf(MATERIALS) > 0);

        Exchange.State oldSave = gson.fromJson(gson.toJson(c1.ex.toState()), Exchange.State.class);
        oldSave.spare = null;
        oldSave.earnings = null;
        oldSave.moneyIncome = null;
        Exchange older = new Exchange();
        assertTrue("a save from before them still restores", older.restore(oldSave));
        close("...with them at nothing until the next step", older.spareOf(RETAIL) + older.earningsOf(INDUSTRY) + older.moneyIncomeOf(MATERIALS), 0, 0);
        ExchangeCheck.step(older, c1.reg, c1.hb, c1.bank, c1.firms, c1.book, W, 0, 2);
        boolean struck = true;
        for (int c = 0; c < N; c++) {
            if (older.spareOf(c) != c1.ex.spareOf(c) || older.earningsOf(c) != c1.ex.earningsOf(c)
                    || older.moneyIncomeOf(c) != c1.ex.moneyIncomeOf(c)) struck = false;
        }
        assertTrue("...and the next step strikes them again, as they were", struck);

        double scale = .01;
        Exchange reformed = new Exchange();
        reformed.restore(gson.fromJson(gson.toJson(c1.ex.toState()), Exchange.State.class));
        reformed.redenominate(scale);
        boolean scaled = true;
        for (int c = 0; c < N; c++) {
            if (Math.abs(reformed.spareOf(c) - c1.ex.spareOf(c) * scale) > 1e-12 * Math.max(1, c1.ex.spareOf(c))
                    || Math.abs(reformed.earningsOf(c) - c1.ex.earningsOf(c) * scale) > 1e-12 * Math.max(1, c1.ex.earningsOf(c))
                    || Math.abs(reformed.moneyIncomeOf(c) - c1.ex.moneyIncomeOf(c) * scale) > 1e-12 * Math.max(1, c1.ex.moneyIncomeOf(c))
                    || Math.abs(reformed.fair(c) - c1.ex.fair(c) * scale) > 1e-12 * Math.max(1, c1.ex.fair(c))) scaled = false;
        }
        assertTrue("a redenomination scales spare money, earnings, what the money earns and the value by the scale", scaled);
        close("...Retail's spare money, in particular: times the scale", reformed.spareOf(RETAIL), c1.ex.spareOf(RETAIL) * scale, 1e-12);
        close("...and Materials' earnings and its money's income scale together, so its return at a scaled price is unmoved",
                reformed.earningsOf(MATERIALS) + reformed.moneyIncomeOf(MATERIALS),
                (c1.ex.earningsOf(MATERIALS) + c1.ex.moneyIncomeOf(MATERIALS)) * scale, 1e-12);
        sectionEnd();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * Section 3c's market: two companies alike in everything but where they
     * last traded. The cell not short of its cushion, with money past it,
     * holds all of A; the cell $300 a household under its cushion holds all of
     * B and offers a tenth of it at the market. The desk has this much capital
     * (none: closed); with some it is full of B to its position limit, so it
     * bids for A and not for B.
     */
    static City switchCity(int a, int b, double lastA, double lastB, double deskCapital) {
        HouseholdBalance hb = twoCells(100.0, 4.0);
        Household offerer = poorOf(hb), keeper = richOf(hb);
        offerer.savings = 0;
        keeper.savings = 100;
        City c = new City(hb, ExchangeCheck.bankWith(deskCapital));
        c.list(a, 1_000, 1_200).list(b, 1_000, 1_200);
        c.firm(a, 0, 0, 0).firm(b, 0, 0, 0);
        holdOnly(hb, a, keeper);
        holdOnly(hb, b, offerer);
        if (deskCapital > 0) {
            double fair = c.reg.fairValue(b, c.book[b], 0, W);
            takeFromHouseholds(c.reg, hb, b, Exchange.POSITION_LIMIT * c.bank.equity() / fair, true);
        }
        c.ex.bookOf(a).seedLastPrice(lastA);
        c.ex.bookOf(b).seedLastPrice(lastB);
        return c;
    }

    /**
     * Section 4's company: a cash box - a little earned, a great deal of money
     * past its cushion, the money earning what the world pays - all equity, held
     * by one cell that is neither short of anything nor has anything to spend.
     * At its equity target when `atTarget`, else far over it.
     */
    static City buybackCity(int company, boolean atTarget) {
        HouseholdBalance owners = ExchangeCheck.savers(12.0, 4.0);
        City c = new City(owners, ExchangeCheck.bankWith(0));
        c.list(company, 10_000, 240);                        // $240 a year: $4,800 of value in the business...
        double money = cushion(100) + 5_200;                 // ...and $5,200 past the cushion: it is worth $1 a share
        c.firm(company, 100, money, W * money);
        c.firms.assets[company] = 10_000;
        c.firms.equity[company] = atTarget ? c.reg.getTargetEquityShare(company) * 10_000 : 10_000;
        return c;
    }

    /**
     * Section 6's market: three companies earning $1,200 a year on a book of
     * $1,000, the third also holding this much money past its cushion, earning
     * 2% - a spread of returns inside the band. The desk is open and holds a
     * tenth of each of the first two, the world a tenth, the cells the rest;
     * the cells are at their cushion.
     */
    static City quietCity(int third, int first, int second, double secondMoney) {
        HouseholdBalance hb = ExchangeCheck.savers(12.0, 4.0);
        City c = new City(hb, ExchangeCheck.bankWith(100_000));
        c.list(first, 1_000, 1_200).list(second, 1_000, 1_200).list(third, 1_000, 1_200);
        double money = secondMoney > 0 ? secondMoney : 1_000;
        c.firm(first, 0, 0, 0).firm(third, 0, 0, 0).firm(second, 0, money, W * money);
        for (int k : new int[] {first, second}) {
            takeFromHouseholds(c.reg, hb, k, 100, true);
            takeFromHouseholds(c.reg, hb, k, 100, false);
        }
        return c;
    }
}
