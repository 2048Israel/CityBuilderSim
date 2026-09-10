package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies the exchange: what the desk quotes, who trades with it and why,
 * what a company does with its surplus, and that a city with a market in it
 * still adds up and survives a save.
 *
 * Every claim is CAUSED. A register is handed shares and a book and the
 * desk is asked what it quotes; a family leaves and its shares are bought;
 * the world is shown a yield and sells or buys; households are given money
 * and a choice of two companies; a household is left short and sells before
 * it borrows; a company is handed a surplus and a market that is cheap, then
 * one that is dear. The live city at the end shows the mechanism runs inside
 * the audited month and comes back from a save.
 */
public class ExchangeCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-64s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-64s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            out.printf("%-64s OK%n", label);
        }
    }

    static final double W = DebtManager.WORLD_BASE_RATE;
    static final int N = Equity.COMPANIES.length;

    /** A register with twelve months of this income on one company's record. */
    static Equity withRecord(int company, double... monthlyIncome) {
        Equity e = new Equity();
        for (double v : monthlyIncome) e.recordMonth(company, v, 0);
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

    /** A bank with this much capital, its month open. */
    static Bank bankWith(double capital) {
        Bank b = new Bank();
        if (capital > 0) b.injectCapital(capital);
        b.startMonth();
        return b;
    }

    /** The companies, as the exchange sees them: a till, a balance sheet, a payroll. */
    static class Firms implements Exchange.Companies {
        final double[] cash = new double[N], assets = new double[N], equity = new double[N], opex = new double[N];
        final double[] boughtBack = new double[N], special = new double[N];
        boolean flush;
        @Override public double cashAvailable(int c, double wanted) { return cash[c]; }
        @Override public void payBuyback(int c, double x) { cash[c] -= x; boughtBack[c] += x; }
        @Override public void paySpecialDividend(int c, double x) { cash[c] -= x; special[c] += x; }
        @Override public double assets(int c) { return assets[c]; }
        @Override public double equity(int c) { return equity[c]; }
        @Override public double monthlyOperatingCost(int c) { return opex[c]; }
        @Override public boolean bankFlush() { return flush; }
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        int RETAIL = Equity.indexOf(BusinessDebtManager.RETAIL);
        int INDUSTRY = Equity.indexOf(BusinessDebtManager.INDUSTRY);
        double[] book = new double[N];

        /* ================= 1. the quote ================= */
        out.println("--- the desk quotes round fair value, and its book moves the quote ---");

        Equity reg = new Equity();
        reg.offer(INDUSTRY, 10_000, 0, null, W);            // founding: 10,000 shares abroad at $1
        Exchange ex = new Exchange();
        Bank bank = bankWith(10_000);
        java.util.Arrays.fill(book, 0);
        book[INDUSTRY] = 20_000;                             // $2 a share on the books
        ex.quote(reg, book, bank.equity(), W);
        assertTrue("a bank with capital makes a market", ex.isOpen());
        close("with nothing on the desk the quote is fair value", ex.mid(INDUSTRY), 2.0, 1e-12);
        close("...the ask half a spread over", ex.ask(INDUSTRY), 2.0 * (1 + Exchange.SPREAD / 2), 1e-12);
        close("...the bid half a spread under", ex.bid(INDUSTRY), 2.0 * (1 - Exchange.SPREAD / 2), 1e-12);
        close("the position limit is the bank's capital's share, in shares",
                ex.limit(INDUSTRY), Exchange.POSITION_LIMIT * 10_000 / 2.0, 1e-9);

        reg.deskBuysFromAbroad(INDUSTRY, 500);               // long 500: two fifths of the limit
        ex.quote(reg, book, bank.equity(), W);
        double expectedMid = 2.0 * (1 - Exchange.PRESSURE * 500 / ex.limit(INDUSTRY));
        close("long, it quotes under fair value by the pressure of its position", ex.mid(INDUSTRY), expectedMid, 1e-9);
        close("...and carries what it holds at that quote", ex.mark(INDUSTRY), expectedMid, 1e-9);
        close("...so the securities line is the inventory at the mark", ex.markToMarket(reg), 500 * expectedMid, 1e-9);

        reg.deskBuysFromAbroad(INDUSTRY, 4_500);             // long 5,000: four times the limit
        ex.quote(reg, book, bank.equity(), W);
        close("however long, the quote never goes under the floor", ex.mid(INDUSTRY), 2.0 * Exchange.FLOOR, 1e-12);

        Exchange dark = new Exchange();
        dark.quote(reg, book, bankWith(0).equity(), W);
        assertTrue("a bank with no capital makes no market", !dark.isOpen());
        close("...and can carry no position", dark.limit(INDUSTRY), 0, 1e-12);

        /* ================= 2. the emigrants ================= */
        out.println("\n--- a household that leaves sells its shares on the way out ---");

        HouseholdBalance town = savers(20.0, 4.0);
        Equity reg2 = new Equity();
        reg2.listIfUnlisted(RETAIL, 10_000, town);           // the founders: a hundred couples, a hundred shares each
        close("the fixture's households own the company", town.sharesHeld(RETAIL), 10_000, 1e-9);
        double[][] fewer = new double[FamilyStructure.values().length][PayTier.values().length];
        fewer[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 80;
        double[] income = new double[HouseholdBalance.ROWS];
        income[PayTier.UNSKILLED.ordinal()] = 80 * 4.0;
        double[] none = new double[HouseholdBalance.ROWS];
        town.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, none, none, .25, .05, 1);
        close("twenty households leave with two thousand shares", town.getSharesTakenAway(RETAIL), 2_000, 1e-9);
        reg2.followEmigrants(town);
        close("...held abroad from the moment they go", reg2.getForeignShares(RETAIL), 2_000, 1e-9);

        Exchange ex2 = new Exchange();
        Bank bank2 = bankWith(10_000);
        Firms firms2 = new Firms();
        java.util.Arrays.fill(book, 0);
        book[RETAIL] = 10_000;                               // $1 a share
        ex2.startMonth();
        ex2.takeMonth(reg2, town, bank2, firms2, book, W, 0);
        double bid2 = 1.0 * (1 - Exchange.SPREAD / 2);
        close("the desk buys them at the bid, and the cash leaves with the leavers",
                ex2.getEmigrantsPaid(RETAIL), 2_000 * bid2, 1e-9);
        close("...out of the bank's cash", bank2.getCash(), 10_000 - 2_000 * bid2, 1e-9);
        close("...onto the desk", reg2.getDealerShares(RETAIL), 2_000, 1e-9);
        close("...and nothing is held abroad any more", reg2.getForeignShares(RETAIL), 0, 1e-9);
        close("the register still agrees with the households", reg2.getDomesticShares(RETAIL), town.sharesHeld(RETAIL), 1e-9);
        double closingMid = 1.0 * (1 - Exchange.PRESSURE * 2_000 / ex2.limit(RETAIL));
        close("the closing quote reads the desk's new position", ex2.mid(RETAIL), closingMid, 1e-9);
        close("...and the bank carries the inventory at it", bank2.getSecurities(), 2_000 * closingMid, 1e-9);
        close("the month's trading result is the cash paid against the mark",
                bank2.getTradingIncome(), -2_000 * bid2 + 2_000 * closingMid, 1e-9);
        close("...and the bank's equity moved by exactly that",
                bank2.equity() - bank2.getOpeningEquity(), bank2.getTradingIncome(), 1e-9);

        // With a dead bank there is no market: the leavers keep their shares abroad.
        HouseholdBalance town2 = savers(20.0, 4.0);
        Equity reg2b = new Equity();
        reg2b.listIfUnlisted(RETAIL, 10_000, town2);
        town2.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, none, none, .25, .05, 1);
        reg2b.followEmigrants(town2);
        Exchange ex2b = new Exchange();
        Bank dead = bankWith(0);
        ex2b.startMonth();
        ex2b.takeMonth(reg2b, town2, dead, firms2, book, W, 0);
        assertTrue("with a dead bank there is no market", !ex2b.isOpen());
        close("...so the leavers' shares stay abroad", reg2b.getForeignShares(RETAIL), 2_000, 1e-9);
        close("...and the desk holds nothing", reg2b.getDealerShares(RETAIL), 0, 1e-9);

        /* ================= 3. the world ================= */
        out.println("\n--- the world sells when the yield falls under its hurdle, buys when it beats it ---");

        double hurdle = W + Equity.FOREIGN_PREMIUM;
        Equity reg3 = withRecord(INDUSTRY, months(100));     // $1,200 a year: $480 of dividend
        reg3.offer(INDUSTRY, 1_000, 0, null, W);             // 1,000 shares abroad
        close("the fixture's company is wholly foreign-owned", reg3.foreignShare(INDUSTRY), 1.0, 1e-12);
        Exchange ex3 = new Exchange();
        Bank bank3 = bankWith(100_000);
        Firms firms3 = new Firms();
        java.util.Arrays.fill(book, 0);
        book[INDUSTRY] = 20_000;                             // $20 a share: the dividend yields 2.4%
        ex3.quote(reg3, book, bank3.equity(), W);
        double yieldBid = ex3.yieldAt(reg3, INDUSTRY, ex3.bid(INDUSTRY));
        assertTrue("the fixture's yield at the bid is under the world's hurdle", yieldBid < hurdle * (1 - Exchange.FOREIGN_TOLERANCE));
        double wantSell = Exchange.FOREIGN_SPEED * (1 - yieldBid / hurdle) * 1_000;
        ex3.startMonth();
        ex3.takeMonth(reg3, new HouseholdBalance(), bank3, firms3, book, W, .10);
        close("the world sells a share of its holding in proportion to the shortfall",
                1_000 - reg3.getForeignShares(INDUSTRY), wantSell, 1e-9);
        close("...to the desk", reg3.getDealerShares(INDUSTRY), wantSell, 1e-9);
        close("...at the bid", ex3.getBoughtFromAbroad(INDUSTRY), wantSell * 20.0 * (1 - Exchange.SPREAD / 2), 1e-9);
        close("...and nothing was sold to it", ex3.getSoldAbroad(INDUSTRY), 0, 1e-12);

        Equity reg4 = withRecord(INDUSTRY, months(100));
        reg4.offer(INDUSTRY, 1_000, 0, null, W);
        reg4.deskBuysFromAbroad(INDUSTRY, 500);              // the desk is long half the company
        Exchange ex4 = new Exchange();
        Bank bank4 = bankWith(10_000);
        book[INDUSTRY] = 1_000;                              // book $1 a share; the earnings are worth $9.60
        ex4.quote(reg4, book, bank4.equity(), W);
        assertTrue("the fixture's desk is quoting under fair value", ex4.mid(INDUSTRY) < ex4.fair(INDUSTRY));
        double yieldAsk = ex4.yieldAt(reg4, INDUSTRY, ex4.ask(INDUSTRY));
        assertTrue("...so the yield at the ask beats the hurdle", yieldAsk > hurdle * (1 + Exchange.FOREIGN_TOLERANCE));
        double wantBuy = Exchange.FOREIGN_SPEED * (yieldAsk / hurdle - 1) * reg4.getOutstanding(INDUSTRY);
        double ask4 = ex4.ask(INDUSTRY);
        ex4.startMonth();
        ex4.takeMonth(reg4, new HouseholdBalance(), bank4, firms3, book, W, .10);
        close("the world buys in proportion to the excess yield", reg4.getForeignShares(INDUSTRY) - 500, wantBuy, 1e-9);
        close("...from the desk", 500 - reg4.getDealerShares(INDUSTRY), wantBuy, 1e-9);
        close("...at the ask", ex4.getSoldAbroad(INDUSTRY), wantBuy * ask4, 1e-9);
        close("...and the bank has the cash", bank4.getCash(), 10_000 + wantBuy * ask4, 1e-9);

        Equity reg5 = new Equity();                          // no record: NEW
        reg5.offer(INDUSTRY, 1_000, 0, null, W);
        reg5.deskBuysFromAbroad(INDUSTRY, 500);
        Exchange ex5 = new Exchange();
        ex5.startMonth();
        ex5.takeMonth(reg5, new HouseholdBalance(), bankWith(10_000), firms3, book, W, .10);
        close("a company with no record is not traded by the world: not sold", ex5.getSoldAbroad(INDUSTRY), 0, 1e-12);
        close("...and not bought", ex5.getBoughtFromAbroad(INDUSTRY), 0, 1e-12);

        /* ================= 4. the households ================= */
        out.println("\n--- households buy the best yield over the deposit rate, then the next ---");

        Equity reg6 = withRecord(INDUSTRY, months(100));
        for (double v : months(200)) reg6.recordMonth(RETAIL, v, 0);
        reg6.offer(INDUSTRY, 1_000, 0, null, W);            // 1,000 shares: $0.48 a share a year
        reg6.offer(RETAIL, 10_000, 0, null, W);             // 10,000 shares: $0.096 a share a year
        reg6.deskBuysFromAbroad(INDUSTRY, 10);              // the desk holds ten of Industry...
        reg6.deskBuysFromAbroad(RETAIL, 10_000);            // ...and all of Retail
        Exchange ex6 = new Exchange();
        Bank bank6 = bankWith(1_000_000);                   // deep enough that the quotes sit near fair value
        Firms firms6 = new Firms();                         // no assets: nobody buys back
        java.util.Arrays.fill(book, 0);
        book[INDUSTRY] = 1_000;                             // worth its earnings: yields the hurdle
        book[RETAIL] = 30_000;                              // worth its book, three dollars a share: yields less
        ex6.quote(reg6, book, bank6.equity(), W);
        double yIndustry = ex6.yieldAt(reg6, INDUSTRY, ex6.ask(INDUSTRY));
        double yRetail = ex6.yieldAt(reg6, RETAIL, ex6.ask(RETAIL));
        assertTrue("the fixture's Industry yields more than its Retail", yIndustry > yRetail);
        assertTrue("...and both clear the deposit rate plus the premium", yRetail > Exchange.HOUSEHOLD_PREMIUM);
        HouseholdBalance buyers = savers(100.0, 4.0);       // $88k past the cushion: $4.4k a month each
        double wanted = buyers.sharesWanted(Exchange.MONTHLY_SHARE_OF_EXCESS);
        close("the fixture's households want a month's share of what is past their cushion",
                wanted, 100 * (100.0 - HouseholdBalance.SHARE_CUSHION_MONTHS * 4.0) * Exchange.MONTHLY_SHARE_OF_EXCESS, 1e-9);
        double askInd = ex6.ask(INDUSTRY), askRet = ex6.ask(RETAIL);
        ex6.startMonth();
        ex6.takeMonth(reg6, buyers, bank6, firms6, book, W, 0);
        assertTrue("they go for the best yield first", ex6.getBestBuy() == INDUSTRY);
        close("...and take everything the desk has of it", ex6.getSoldToHouseholds(INDUSTRY), 10 * askInd, 1e-9);
        close("...and put the rest into the next best", ex6.getSoldToHouseholds(RETAIL), wanted - 10 * askInd, 1e-9);
        close("...so the whole month's money was spent", ex6.getHouseholdBuying(), wanted, 1e-9);
        close("...out of their savings", couple(buyers).savings(), 100.0 - wanted / 100, 1e-9);
        close("...into shares of both", buyers.sharesHeld(INDUSTRY), 10, 1e-9);
        close("...the register agreeing on each", reg6.getDomesticShares(RETAIL), buyers.sharesHeld(RETAIL), 1e-9);
        close("the desk sold nothing it did not have", reg6.getDealerShares(INDUSTRY), 0, 1e-9);
        close("what the best could not fill is remembered as demand",
                ex6.getUnfilled(INDUSTRY), (wanted - 10 * askInd) / askInd, 1e-9);
        assertTrue("...and lifts its closing quote over fair value", ex6.mid(INDUSTRY) > ex6.fair(INDUSTRY));
        close("...but not the mark: the desk does not book a gain on its own quote", ex6.mark(INDUSTRY), ex6.fair(INDUSTRY), 1e-12);
        double demandBefore = ex6.getDemand(INDUSTRY);
        ex6.startMonth();
        close("the demand fades by half a month", ex6.getDemand(INDUSTRY), demandBefore * Exchange.DEMAND_DECAY, 1e-12);

        Exchange ex6b = new Exchange();
        HouseholdBalance cautious = savers(100.0, 4.0);
        ex6b.startMonth();
        ex6b.takeMonth(reg6, cautious, bank6, firms6, book, W, .10);   // the bank pays 10%
        close("with the deposit rate above every yield, nobody buys", ex6b.getHouseholdBuying(), 0, 1e-12);
        close("...and nobody's savings moved", couple(cautious).savings(), 100.0, 1e-12);

        /* ================= 5. the distress sale ================= */
        out.println("\n--- a household short of money sells before it borrows ---");

        HouseholdBalance broke = savers(0.0, 4.0);
        Equity reg7 = new Equity();
        reg7.listIfUnlisted(RETAIL, 5_000, broke);          // fifty shares each, $1 a share
        Exchange ex7 = new Exchange();
        Bank bank7 = bankWith(100_000);
        broke.setMarket(ex7, reg7, bank7);
        java.util.Arrays.fill(book, 0);
        book[RETAIL] = 5_000;
        ex7.quote(reg7, book, bank7.equity(), W);
        double[][] hundred = new double[FamilyStructure.values().length][PayTier.values().length];
        hundred[FamilyStructure.COUPLE.ordinal()][PayTier.UNSKILLED.ordinal()] = 100;
        double[] pay = new double[HouseholdBalance.ROWS];
        pay[PayTier.UNSKILLED.ordinal()] = 100 * 4.0;
        double[] shop = new double[HouseholdBalance.ROWS];
        shop[PayTier.UNSKILLED.ordinal()] = 100 * 6.0;       // spent $6k on $4k of pay: $2k short each
        broke.advanceMonth((s, t) -> hundred[s.ordinal()][t.ordinal()], pay, 0, none, shop, .25, .05, 1);
        double bid7 = ex7.bid(RETAIL);
        close("each household sold shares for exactly what it was short", couple(broke).sold(), 2.0, 1e-9);
        close("...at the bid", couple(broke).shares(RETAIL), 50 - 2.0 / bid7, 1e-9);
        close("...and borrowed nothing", couple(broke).debt(), 0, 1e-12);
        close("the desk bought them", reg7.getDealerShares(RETAIL), 200 / bid7, 1e-9);
        close("...for cash out of the bank", bank7.getCash(), 100_000 - 200, 1e-9);
        close("...declared as bought from the households", ex7.getBoughtFromHouseholds(RETAIL), 200, 1e-9);
        close("...and the households' total says the same", broke.totalSold(), 200, 1e-9);

        HouseholdBalance abroad = savers(0.0, 4.0);
        Equity reg7b = new Equity();
        reg7b.listIfUnlisted(RETAIL, 5_000, abroad);
        couple(abroad).abroad = 10;                         // US$10k of the world's paper each, at parity
        abroad.setMarket(ex7, reg7b, bank7);
        abroad.setExchangeRate(1.0);
        abroad.advanceMonth((s, t) -> hundred[s.ordinal()][t.ordinal()], pay, 0, none, shop, .25, .05, 1);
        close("a household with paper abroad sells that first", couple(abroad).broughtHome(), 2.0, 1e-9);
        close("...keeping the rest there", couple(abroad).abroad(), 8.0, 1e-9);
        close("...and its shares", couple(abroad).sold(), 0, 1e-12);
        close("...a financial inflow of what came home", abroad.getBroughtHome(), 200, 1e-9);

        /* ================= 5b. the world's paper ================= */
        out.println("\n--- idle savings go abroad when the world pays more than the bank, and come home when it does not ---");

        HouseholdBalance idle = savers(100.0, 4.0);         // $88k past the cushion
        idle.investAbroad(0, W, 1.0);
        double spare = 100.0 - HouseholdBalance.SHARE_CUSHION_MONTHS * 4.0;
        double share = Math.min(OutwardInvestment.MAX_SHARE, W * OutwardInvestment.APPETITE);
        double firstMove = spare * share * OutwardInvestment.OUT_SPEED;
        close("a household sends a step towards its target abroad", couple(idle).abroad(), firstMove, 1e-9);
        close("...out of its savings", couple(idle).savings(), 100.0 - firstMove, 1e-9);
        close("...declared as a financial outflow", idle.getSentAbroad(), 100 * firstMove, 1e-9);
        idle.investAbroad(0, W, 1.0);
        double coupon = firstMove * W / 12;
        assertTrue("the next month it earns the world's rate, rolled there", couple(idle).foreignInterest() > 0
                && Math.abs(couple(idle).foreignInterest() - coupon) < 1e-9);
        double held = couple(idle).abroad();
        idle.investAbroad(.05, W, 1.0);                     // the bank pays more than the world
        close("when the bank pays more, a tenth comes home a month", couple(idle).broughtHome(), held * (1 + W / 12) * OutwardInvestment.HOME_SPEED, 1e-9);
        HouseholdBalance owing = savers(100.0, 4.0);
        couple(owing).debt = 5.0;
        owing.investAbroad(0, W, 1.0);
        close("a household in debt sends nothing abroad", couple(owing).abroad(), 0, 1e-12);
        HouseholdBalance emigrants = savers(20.0, 4.0);
        couple(emigrants).abroad = 3.0;
        emigrants.advanceMonth((s, t) -> fewer[s.ordinal()][t.ordinal()], income, 0, none, none, .25, .05, 1);
        close("twenty households leaving take twenty households' dollars, and nothing crosses the border",
                emigrants.getAbroadTakenAway(), 20 * 3.0, 1e-9);

        /* ================= 6. the buyback, and the special dividend ================= */
        out.println("\n--- a company over its target buys back cheap shares and pays out when they are dear ---");

        double[] normal = new double[12];
        for (int m = 0; m < 12; m++) normal[m] = m < 6 ? 100 : 50;   // profitable but fading: NORMAL
        Equity reg8 = withRecord(RETAIL, normal);
        assertTrue("the fixture's company is in a normal year", reg8.getRegime(RETAIL) == Equity.Regime.NORMAL);
        HouseholdBalance owners = savers(20.0, 4.0);
        reg8.listIfUnlisted(RETAIL, 10_000, owners);        // 10,000 to the founders, a hundred each...
        reg8.deskBuysFromHouseholds(RETAIL, 2_000);         // ...of which the desk bought 2,000...
        couple(owners).shares[RETAIL] -= 20;
        reg8.deskSellsAbroad(RETAIL, 1_000);                // ...and sold 1,000 on to the world
        double w8 = .006;                                   // a world rate at which the yield sits on the hurdle: the world holds still
        close("the fixture: 8,000 at home", owners.sharesHeld(RETAIL), 8_000, 1e-9);
        close("...1,000 on the desk", reg8.getDealerShares(RETAIL), 1_000, 1e-9);
        close("...1,000 abroad", reg8.getForeignShares(RETAIL), 1_000, 1e-9);
        Exchange ex8 = new Exchange();
        Bank bank8 = bankWith(1_000_000);
        Firms firms8 = new Firms();
        firms8.assets[RETAIL] = 10_000; firms8.equity[RETAIL] = 10_000;   // all equity: far over target
        firms8.cash[RETAIL] = 5_000; firms8.opex[RETAIL] = 100;           // $600 of cushion
        java.util.Arrays.fill(book, 0);
        book[RETAIL] = 10_000;
        ex8.quote(reg8, book, bank8.equity(), w8);
        double worth = reg8.getOutstanding(RETAIL) * ex8.fair(RETAIL);
        double pace = Exchange.BUYBACK_PACE / 12 * worth;
        double ask8 = ex8.ask(RETAIL);
        assertTrue("the fixture's market has the shares near fair value", ask8 <= ex8.fair(RETAIL) * (1 + Exchange.BUYBACK_TOLERANCE));
        ex8.startMonth();
        ex8.takeMonth(reg8, owners, bank8, firms8, book, w8, .05);       // savers paid 5%: nobody buys
        close("the fixture's world held still", ex8.getBoughtFromAbroad(RETAIL) + ex8.getSoldAbroad(RETAIL), 0, 1e-12);
        close("it retires a month of the pace, in shares", reg8.getBoughtBackThisMonth(RETAIL), pace / ask8, 1e-9);
        close("...paid from its till at the ask", 5_000 - firms8.cash[RETAIL], pace, 1e-9);
        close("...pro rata from the households", 8_000 - owners.sharesHeld(RETAIL), .8 * pace / ask8, 1e-9);
        close("...from the desk", 1_000 - reg8.getDealerShares(RETAIL), .1 * pace / ask8, 1e-9);
        close("...and from abroad", 1_000 - reg8.getForeignShares(RETAIL), .1 * pace / ask8, 1e-9);
        close("the households have the cash", couple(owners).savings(), 20.0 + .8 * pace / 100, 1e-9);
        close("...the bank has the desk's", bank8.getCash(), 1_000_000 + .1 * pace, 1e-9);
        close("...the abroad line has the world's", ex8.getBuybackAbroad(RETAIL), .1 * pace, 1e-9);
        close("...and the shares are gone", reg8.getShares(RETAIL), 10_000 - pace / ask8, 1e-9);
        close("no special dividend was paid", ex8.getSpecialDividend(RETAIL), 0, 1e-12);

        // The same company with its shares dear: households wanting more
        // than the desk holds lift the quote; the surplus goes out as a
        // special dividend and not one share is retired at the price.
        Equity reg9 = withRecord(RETAIL, normal);
        HouseholdBalance keen = savers(100.0, 4.0);
        reg9.listIfUnlisted(RETAIL, 10_000, keen);          // all at home, none on the desk
        Exchange ex9 = new Exchange();
        Bank thin = bankWith(100);                          // a small bank: a small limit, so the demand tells
        Firms firms9 = new Firms();
        firms9.assets[RETAIL] = 10_000; firms9.equity[RETAIL] = 10_000;
        firms9.cash[RETAIL] = 5_000; firms9.opex[RETAIL] = 100;
        ex9.startMonth();
        ex9.takeMonth(reg9, keen, thin, firms9, book, w8, 0);           // month one: they want, the desk has nothing
        assertTrue("the fixture's buyers were left unfilled", ex9.getUnfilled(RETAIL) > 0);
        double retiredCheap = reg9.getBoughtBackThisMonth(RETAIL);
        assertTrue("...and the company retired shares while they were still cheap", retiredCheap > 0);
        ex9.startMonth();
        reg9.startMonth();
        double sharesBefore = reg9.getShares(RETAIL);
        double cashBefore = firms9.cash[RETAIL];
        ex9.takeMonth(reg9, keen, thin, firms9, book, w8, 0);           // month two: the quote is dear
        assertTrue("the next month's quote is past fair value by more than the tolerance",
                ex9.ask(RETAIL) > ex9.fair(RETAIL) * (1 + Exchange.BUYBACK_TOLERANCE));
        double pace9 = Exchange.BUYBACK_PACE / 12 * reg9.getOutstanding(RETAIL) * ex9.fair(RETAIL);
        close("so the same money goes out as a special dividend", ex9.getSpecialDividend(RETAIL), pace9, 1e-9);
        close("...from the till", cashBefore - firms9.cash[RETAIL], pace9, 1e-9);
        close("...to every holder", reg9.getDividendHomeThisMonth(RETAIL) + reg9.getDividendAbroadThisMonth(RETAIL), pace9, 1e-9);
        close("...and not one share is retired", reg9.getShares(RETAIL), sharesBefore, 1e-12);
        close("...nor was one bought back this month", reg9.getBoughtBackThisMonth(RETAIL), 0, 1e-12);

        Equity loser = withRecord(RETAIL, months(-10));
        HouseholdBalance owners2 = savers(20.0, 4.0);
        loser.listIfUnlisted(RETAIL, 10_000, owners2);
        Exchange ex10 = new Exchange();
        Firms firms10 = new Firms();
        firms10.assets[RETAIL] = 10_000; firms10.equity[RETAIL] = 10_000; firms10.cash[RETAIL] = 5_000;
        ex10.startMonth();
        ex10.takeMonth(loser, owners2, bankWith(1_000_000), firms10, book, W, .05);
        close("a company in a bad year buys nothing back", loser.getBoughtBackThisMonth(RETAIL), 0, 1e-12);
        close("...and pays no special dividend", ex10.getSpecialDividend(RETAIL), 0, 1e-12);
        Equity atTarget = withRecord(RETAIL, normal);
        HouseholdBalance owners3 = savers(20.0, 4.0);
        atTarget.listIfUnlisted(RETAIL, 10_000, owners3);
        Firms firms11 = new Firms();
        firms11.assets[RETAIL] = 10_000; firms11.cash[RETAIL] = 5_000;
        firms11.equity[RETAIL] = atTarget.getTargetEquityShare(RETAIL) * 10_000;   // at target
        Exchange ex11 = new Exchange();
        ex11.startMonth();
        ex11.takeMonth(atTarget, owners3, bankWith(1_000_000), firms11, book, W, .05);
        close("a company at its target buys nothing back", atTarget.getBoughtBackThisMonth(RETAIL), 0, 1e-12);

        /* ================= 7. the split ================= */
        out.println("\n--- a share that gets too dear is split, and one too cheap consolidated ---");

        Equity reg12 = new Equity();
        HouseholdBalance few = savers(20.0, 4.0);
        reg12.listIfUnlisted(INDUSTRY, 10, few);            // ten shares in a hundred households
        java.util.Arrays.fill(book, 0);
        book[INDUSTRY] = 5_000;                             // $500 a share
        Exchange ex12 = new Exchange();
        Bank bank12 = bankWith(100_000);
        double valueBefore = few.sharesHeld(INDUSTRY) * 500;
        ex12.startMonth();
        ex12.takeMonth(reg12, few, bank12, new Firms(), book, W, .10);
        close("a share quoted at five hundred is split a hundred for one", ex12.getSplit(INDUSTRY), 100, 1e-12);
        close("...the register's count by the factor", reg12.getShares(INDUSTRY), 1_000, 1e-9);
        close("...every household's count by the factor", couple(few).shares(INDUSTRY), 10, 1e-9);
        close("...the quote by its inverse", ex12.mid(INDUSTRY), 5.0, 1e-9);
        close("...and what the households hold is worth what it was", few.sharesHeld(INDUSTRY) * ex12.mid(INDUSTRY), valueBefore, 1e-9);
        close("...the last sale price with it", reg12.getLastPrice(INDUSTRY), Equity.FOUNDING_PRICE / 100, 1e-12);
        close("...the split factor remembers it", ex12.getSplitFactor(INDUSTRY), 100, 1e-12);
        close("...so the price per FOUNDING share is what it was: the history has no cliff",
                ex12.midPerFoundingShare(INDUSTRY), 500.0, 1e-9);

        Equity reg13 = new Equity();
        HouseholdBalance many = savers(20.0, 4.0);
        reg13.listIfUnlisted(INDUSTRY, 1_000_000, many);    // a million shares
        book[INDUSTRY] = 100;                               // worth a hundredth of a cent each
        Exchange ex13 = new Exchange();
        ex13.startMonth();
        ex13.takeMonth(reg13, many, bank12, new Firms(), book, W, .10);
        close("a share quoted at a ten-thousandth is consolidated ten thousand for one", ex13.getSplit(INDUSTRY), 1e-4, 1e-15);
        close("...to a hundred shares", reg13.getShares(INDUSTRY), 100, 1e-9);
        close("...at a dollar", ex13.mid(INDUSTRY), 1.0, 1e-9);
        close("...the households still agreeing with the register", many.sharesHeld(INDUSTRY), reg13.getDomesticShares(INDUSTRY), 1e-9);

        /* ================= 8. the save ================= */
        out.println("\n--- the exchange survives a save, by name ---");

        Exchange back = new Exchange();
        assertTrue("it restores", back.restore(reg6.keys(), ex6.toSaveArray()));
        close("...the quote", back.mid(INDUSTRY), ex6.mid(INDUSTRY), 1e-12);
        close("...fair value", back.fair(INDUSTRY), ex6.fair(INDUSTRY), 1e-12);
        close("...and the demand the quote carries", back.getDemand(INDUSTRY), ex6.getDemand(INDUSTRY), 1e-12);
        Exchange split = new Exchange();
        split.restore(reg12.keys(), ex12.toSaveArray());
        close("...and the split factor", split.getSplitFactor(INDUSTRY), 100, 1e-12);
        double[] firstNight = new double[reg12.keys().length * Exchange.SLOTS_BEFORE_SPLITS + 1];
        Exchange older = new Exchange();
        assertTrue("the exchange's first-night save, three a company, still restores", older.restore(reg12.keys(), firstNight));
        close("...with one share then being one share now", older.getSplitFactor(INDUSTRY), 1, 1e-12);
        assertTrue("...closed until the bank says otherwise", !back.isOpen());
        back.reopen(bank6.equity());
        assertTrue("...open once a bank with capital is put back", back.isOpen());
        back.reopen(0);
        assertTrue("...and not for a bank without", !back.isOpen());
        assertTrue("an array of the wrong length is refused whole", !new Exchange().restore(reg6.keys(), new double[] {1, 2}));

        /* ================= 9. a live city ================= */
        out.println("\n--- and a city with a market in it still adds up, and comes back from a save ---");

        Path root = Files.createTempDirectory("exchangecheck");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game city = new Game(files);
        System.setOut(quiet);
        double worstResidual = 0;
        int monthsTraded = 0;
        double bought = 0, soldBack = 0, boughtBack = 0, specials = 0;
        double worstMarkGap = 0;
        try {
            city.run();
            for (int m = 0; m < 240; m++) {
                city.simulateMonths(1);
                worstResidual = Math.max(worstResidual, Math.abs(city.getLastMoneyAudit().relative()));
                Exchange x = city.getExchange();
                if (x.getVolume() > 0) monthsTraded++;
                bought += x.getSoldToHouseholds();
                soldBack += x.getBoughtFromHouseholds() + x.getBoughtFromAbroad();
                boughtBack += x.getBuybackToHouseholds() + x.getBuybackToDesk() + x.getBuybackAbroad();
                specials += x.getSpecialDividend();
                double mark = x.markToMarket(city.getEquity());
                worstMarkGap = Math.max(worstMarkGap, Math.abs(city.getBank().getSecurities() - mark) / Math.max(1, Math.abs(mark)));
            }
        } finally {
            System.setOut(out);
        }
        Exchange x = city.getExchange();
        Equity register = city.getEquity();
        HouseholdBalance people = city.getHouseholdBalance();
        out.printf("   after 20 years: traded in %d months, %,.0f shares in all; households bought $%,.0fk, the desk bought $%,.0fk,"
                + " companies retired $%,.0fk and paid $%,.0fk of special dividends; the desk holds $%,.0fk;"
                + " the households hold US$%,.0fk abroad and $%,.0fk at home%n",
                monthsTraded, x.getLifetimeVolume(), bought, soldBack, boughtBack, specials,
                city.getBank().getSecurities(), people.totalAbroadUsd(), people.totalSavings());
        assertTrue("the city's shares traded on their own", monthsTraded > 0 && x.getLifetimeVolume() > 0);
        assertTrue("...and every month of it is conserved", worstResidual < 1e-9);
        assertTrue("...with the bank carrying the desk at the exchange's mark all along", worstMarkGap < 1e-9);
        boolean agree = true, whole = true;
        for (int c = 0; c < N; c++) {
            double reg_ = register.getDomesticShares(c), held_ = people.sharesHeld(c);
            if (Math.abs(reg_ - held_) > 1e-9 * Math.max(1, register.getShares(c))) agree = false;
            if (register.getDealerShares(c) < -1e-9) whole = false;
        }
        assertTrue("...the register and the households agreeing on every company", agree);
        assertTrue("...and the desk never short of anything", whole);

        assertTrue("the city saves", city.saveGame(1, "exchangecheck").ok);
        Game again = new Game(files);
        System.setOut(quiet);
        again.loadGameSave(1);
        System.setOut(out);
        assertTrue("...and loads", again.getLoadFailure() == null);
        boolean quotesBack = true, deskBack = true;
        for (int c = 0; c < N; c++) {
            if (Math.abs(again.getExchange().mid(c) - x.mid(c)) > 1e-9 * Math.max(1, x.mid(c))) quotesBack = false;
            if (Math.abs(again.getExchange().getDemand(c) - x.getDemand(c)) > 1e-9 * Math.max(1, x.getDemand(c))) quotesBack = false;
            if (Math.abs(again.getEquity().getDealerShares(c) - register.getDealerShares(c)) > 1e-9 * Math.max(1, Math.abs(register.getDealerShares(c)))) deskBack = false;
        }
        assertTrue("every quote and the demand it carries come back", quotesBack);
        assertTrue("...and the desk's inventory", deskBack);
        close("...at the same mark", again.getBank().getSecurities(), city.getBank().getSecurities(), 1e-6 * Math.max(1, Math.abs(city.getBank().getSecurities())));
        close("...and the households' dollars abroad", again.getHouseholdBalance().totalAbroadUsd(), people.totalAbroadUsd(), 1e-9 * Math.max(1, people.totalAbroadUsd()));

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
