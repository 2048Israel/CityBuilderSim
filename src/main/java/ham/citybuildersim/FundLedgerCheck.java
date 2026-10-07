package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, through a split, a reform and a save, and that the ledger and the fund's own counters tell one story over a played run. Not part of the game.
 *
 * WHY THIS EXISTS. Until 0.7.39 the fund knew what it was worth and every
 * flow in and out of it, but not what any holding had cost: Jerus, "the city
 * fund should show pnl and acb and all that". The ledger is hooked where the
 * holdings move (Exchange's and BondMarket's settles, a maturity, a
 * write-down, a dividend, the rescue) and nowhere else, so a path that moves
 * a holding without a hook is exactly what section 6's identity is for. The
 * project's spec-fund-0739.md, 3.7.
 *
 * What it has to prove, a section each:
 *
 *   1. Average cost, on the book (a seller caused on it): 100 bought at 2 and
 *      100 at 4 cost 600, 3 a share; 50 sold at 5 take 150 of cost out and
 *      realize 100; the average stays 3.
 *   2. The books: the rule's buys and the hand's pool into one lot; the
 *      rescue takes the fund's market-book bank shares for nothing (their
 *      cost a realized loss); a hand sale takes the market lot first and
 *      then the rescue lot, each at its own cost, and the rescue lot's cost
 *      is TreasuryFund.getRescueCost() after.
 *   3. Bonds: a coupon is income, not cost; a default at
 *      BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT realizes that share of the
 *      cost; a write-down that keeps the whole face realizes nothing; a
 *      maturity realizes the face less the cost.
 *   4. A split at Exchange.SPLIT_AT: the cost stands and the average a share
 *      falls by the factor. A reform: every money figure in the ledger by
 *      the reform's scale, share counts not.
 *   5. A save: the ledger round-trips exactly; an older save's (none) is
 *      seeded at market value, flagged with the month, the rescue lot from
 *      the counters - a save with a rescue book, loaded with its ledger taken
 *      out, gets its rescue lot at TreasuryFund.getRescueCost() exactly and
 *      untagged, the same lot a city that tracked it all along has, and its
 *      market lots at market value, tagged.
 *   6. The identity over a played run: realized + the change in unrealized =
 *      the change in what the shares and bonds are worth + what sales and
 *      maturities brought in - what purchases and rescues cost, every month,
 *      and the ledger's purchases and proceeds the fund's counters'.
 *   7. Since it began, by kind, adds up to the fund's gain.
 *   8. The hand (0.7.39): a buy no further than the 10% cap, counting every
 *      buy of the fund's on the company, the rule's bid making way for the
 *      hand's at the step and between steps - the rule's bid and two orders,
 *      every one of them filled, leave the fund at the cap and no further,
 *      and the ticket's quote reckons the room the same way; its cash held
 *      from the rule; its order a row from the step that posts it to the step
 *      that withdraws it, with what lapsed.
 *
 * @author Jerus
 */
public class FundLedgerCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void check(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-96s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-96s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            out.printf("%-96s OK%n", label);
        }
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    static void play(Game g) { quietly(g::toggleNextMonth); }

    static GameFiles files;

    /** A copy of the fixture city, loaded from its save: every section starts from the same city. */
    static Game copy() {
        Game g = new Game(files);
        quietly(() -> g.loadGameSave(1));
        return g;
    }

    /** The household cell holding the most of a company's shares: a seller the fixture can cause on the book. */
    static Household holder(Game g, int c) {
        Household best = null;
        for (Household h : g.getHouseholdBalance().cells()) {
            if (h.households() <= 0) continue;
            if (best == null || h.shares[c] * h.households() > best.shares[c] * best.households()) best = h;
        }
        return best;
    }

    static double filled(List<OrderBook.Fill> fills) {
        double t = 0;
        for (OrderBook.Fill f : fills) t += f.quantity();
        return t;
    }

    public static void main(String[] args) {
        out = System.out;
        quiet = new PrintStream(OutputStream.nullOutputStream());

        /* ================= the city ================= */
        out.println("--- a city whose households own its companies and its bank (FundCheck's) ---");
        files = GameFiles.scratch("fundledgercheck");
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            city.getForeignAccounts().pinRate(1.0);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.simulateMonths(72);
            city.saveGame(1);
        });
        check("fixture: the city's fund holds nothing and its ledger tracks from the founding",
                city.getFund().getLedger().getTrackingSince() == -1 && city.getFund().getLedger().acbHeld() == 0
                        && city.getBondMarket().faceHeldByCity() == 0);

        averageCost();
        theBooks();
        bonds();
        splitAndReform();
        theSave();
        theIdentity();
        theHand();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** A company the households hold shares of, other than the bank. */
    static int heldCompany(Game g) {
        int best = -1;
        double most = 0;
        for (int c = 0; c < Equity.COMPANIES.length; c++) {
            if (c == Equity.BANK) continue;
            double held = g.getHouseholdBalance().sharesHeld(c);
            if (held > most) { most = held; best = c; }
        }
        return best;
    }

    /* ================= 1. average cost ================= */

    static void averageCost() {
        out.println("\n--- 1. average cost: two buys at two prices, then a sale, on the book ---");
        Game g = copy();
        int c = heldCompany(g);
        check("fixture: a company the households hold more than 200 shares of", c >= 0
                && g.getHouseholdBalance().sharesHeld(c) > 200);
        if (c < 0) return;
        Exchange ex = g.getExchange();
        TreasuryFund f = g.getFund();
        f.receive(10_000);
        ex.bookOf(c).withdrawAll();
        Household seller = holder(g, c);
        String cell = Exchange.CELL + seller.key();
        ex.tradeForCheck(c, cell, OrderBook.Side.SELL, 2, 100);
        double got = filled(ex.tradeForCheck(c, Exchange.FUND_HAND, OrderBook.Side.BUY, 2, 100));
        ex.tradeForCheck(c, cell, OrderBook.Side.SELL, 4, 100);
        got += filled(ex.tradeForCheck(c, Exchange.FUND_HAND, OrderBook.Side.BUY, 4, 100));
        FundLedger.Lot lot = f.getLedger().lot(FundLedger.shareKey(Equity.COMPANIES[c]));
        close("100 bought at 2 and 100 at 4 fill whole, as the book reports", got, 200, 1e-9);
        check("...into one lot, the company's market book", lot != null && !lot.isClosed());
        if (lot == null) return;
        close("...which cost 600", lot.acb(), 600, 1e-9);
        close("...3 a share, on the register's 200", lot.acb() / g.getEquity().getCityMarketShares(c), 3, 1e-12);
        double fair = ex.fair(c);
        close("...and was worth 200 shares at the step's fair value: a bargain's measure", lot.boughtValue(), 200 * fair,
                1e-12 * Math.max(1, 200 * fair));
        close("...how far under it the lot bought being the one against the other", lot.underValue(), 1 - 600 / (200 * fair), 1e-12);
        ex.tradeForCheck(c, Exchange.WORLD, OrderBook.Side.BUY, 5, 50);
        double sold = filled(ex.tradeForCheck(c, Exchange.FUND_HAND, OrderBook.Side.SELL, 5, 50));
        close("50 sold at 5 to a bid the world rested", sold, 50, 1e-9);
        close("...take 150 of cost out", lot.acbOut(), 150, 1e-9);
        close("...and realize 100: 250 in, less the 150", lot.realized(), 100, 1e-9);
        close("...and leave the average a share at 3", lot.acb() / g.getEquity().getCityMarketShares(c), 3, 1e-12);
        close("the proceeds are the lot's, and the fund's counter's", lot.proceeds(), f.getSharesSold(), 1e-9);
        close("...the purchases too", lot.bought(), f.getSharesBought(), 1e-9);
        int buys = 0, sells = 0;
        for (FundLedger.Activity a : f.getLedger().getActivity()) {
            if (!a.hand() || !FundLedger.shareKey(Equity.COMPANIES[c]).equals(a.key())) continue;
            if (a.kind().equals(FundLedger.BUY)) {
                buys++;
                close("...the hand's buys a row, 200 for 600, 3 on average", a.average(), 3, 1e-12);
                close("...worth 200 shares at fair value on the row too", a.value(), 200 * fair, 1e-12 * Math.max(1, 200 * fair));
            }
            if (a.kind().equals(FundLedger.SELL)) { sells++; close("...its sale a row, realizing 100", a.realized(), 100, 1e-9); }
        }
        check("the record has one row each way: fills of a month, a lot and a side are one", buys == 1 && sells == 1);
    }

    /* ================= 2. the books ================= */

    static void theBooks() {
        out.println("\n--- 2. the books: the rule and the hand pooled; the rescue; a hand sale through both books ---");
        Game g = copy();
        int c = heldCompany(g);
        if (c < 0) return;
        Exchange ex = g.getExchange();
        TreasuryFund f = g.getFund();
        f.receive(10_000);
        ex.bookOf(c).withdrawAll();
        String cell = Exchange.CELL + holder(g, c).key();
        ex.tradeForCheck(c, cell, OrderBook.Side.SELL, 2, 30);
        ex.tradeForCheck(c, Exchange.FUND, OrderBook.Side.BUY, 2, 30);
        ex.tradeForCheck(c, cell, OrderBook.Side.SELL, 3, 20);
        ex.tradeForCheck(c, Exchange.FUND_HAND, OrderBook.Side.BUY, 3, 20);
        FundLedger.Lot lot = f.getLedger().lot(FundLedger.shareKey(Equity.COMPANIES[c]));
        int lots = 0;
        for (FundLedger.Lot l : f.getLedger().getLots()) if (l.name().equals(Equity.COMPANIES[c])) lots++;
        check("the rule's 30 at 2 and the hand's 20 at 3 are one lot", lots == 1 && lot != null);
        if (lot == null) return;
        close("...costing 120, 2.4 a share", lot.acb() / g.getEquity().getCityMarketShares(c), 2.4, 1e-12);

        // THE RESCUE: the fund holds the bank's shares on its market book first, bought from a household.
        int b = Equity.BANK;
        ex.bookOf(b).withdrawAll();
        // The seller: the household cell holding the most of the bank, or the world if it holds more.
        Household bankHolder = holder(g, b);
        double cellHolds = bankHolder == null ? 0 : bankHolder.shares[b] * bankHolder.households();
        double worldHolds = g.getEquity().getForeignShares(b);
        String bankSeller = cellHolds >= worldHolds ? Exchange.CELL + bankHolder.key() : Exchange.WORLD;
        double q = Math.max(cellHolds, worldHolds) / 2;
        check("fixture: somebody other than the city holds the bank's shares", q > 0);
        if (!(q > 0)) return;
        double p0 = ex.price(b);
        f.receive(q * p0);
        ex.tradeForCheck(b, bankSeller, OrderBook.Side.SELL, p0, q);
        ex.tradeForCheck(b, Exchange.FUND_HAND, OrderBook.Side.BUY, p0, q);
        FundLedger.Lot bankLot = f.getLedger().lot(FundLedger.shareKey(Equity.COMPANIES[b]));
        check("fixture: the fund holds half of what they held on its market book, at the price",
                bankLot != null && Math.abs(bankLot.acb() - q * p0) < 1e-9 * Math.max(1, q * p0)
                        && Math.abs(g.getEquity().getCityMarketShares(b) - q) < 1e-9 * Math.max(1, q));
        if (bankLot == null) return;
        double cost = bankLot.acb();
        Bank bank = g.getBank();
        g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
        FundCheck.takeEquityTo(bank, -10 * Math.max(1_000, g.getCentralBank().ceiling()));
        play(g);
        TreasuryFund.Resolution r = g.getLastResolution();
        check("fixture: the month resolved the failed bank for its shares", r != null && r.month() == g.getMonth());
        if (r == null) return;
        close("the rescue took the fund's market-book bank shares for nothing: their cost realized as a loss",
                bankLot.realized(), -cost, 1e-9);
        check("...and the market lot closed with nothing left of its cost", bankLot.isClosed() && bankLot.acb() == 0);
        FundLedger.Lot rescueLot = f.getLedger().lot(FundLedger.rescueKey(Equity.COMPANIES[b]));
        check("...a rescue lot opened, whose cost is what the city paid", rescueLot != null
                && Math.abs(f.getRescueCost() - r.paid()) < 1e-6);
        boolean row = false;
        for (FundLedger.Activity a : f.getLedger().getActivity()) {
            if (a.kind().equals(FundLedger.RESCUE) && a.month() == r.month()) row = Math.abs(a.money() - r.paid()) < 1e-6
                    && Math.abs(a.realized() + cost) < 1e-9;
        }
        check("...and a row says what was paid and what the market lot lost", row);
        if (rescueLot == null) return;

        // The hand sells a quarter of the rescue book to the world: the rescue lot's own cost comes out.
        double held = g.getEquity().getCityRescueShares(b);
        double costBefore = f.getRescueCost();
        double price = ex.price(b);
        ex.bookOf(b).withdrawAll();
        ex.tradeForCheck(b, Exchange.WORLD, OrderBook.Side.BUY, price, held / 4);
        ex.tradeForCheck(b, Exchange.FUND_HAND, OrderBook.Side.SELL, price, held / 4);
        close("a hand sale of a quarter of the rescue book takes a quarter of its cost out", rescueLot.acbOut(), costBefore / 4,
                1e-9 * Math.max(1, costBefore));
        close("...and realizes the proceeds less it", rescueLot.realized(), held / 4 * price - costBefore / 4,
                1e-9 * Math.max(1, costBefore));
        close("...and the rescue book's cost on the fund is the rest", f.getRescueCost(), costBefore * .75,
                1e-9 * Math.max(1, costBefore));

        // ...then the rule buys some back from the world onto its market book, and the hand sells more than it holds there.
        f.receive(10 * price * 10);
        ex.tradeForCheck(b, Exchange.WORLD, OrderBook.Side.SELL, price, 10);
        ex.tradeForCheck(b, Exchange.FUND, OrderBook.Side.BUY, price, 10);
        check("fixture: the fund holds 10 bank shares on its market book again, beside the rescue book",
                Math.abs(g.getEquity().getCityMarketShares(b) - 10) < 1e-9 && !bankLot.isClosed());
        double marketCost = bankLot.acb(), rescueCost = f.getRescueCost(), rescueHeld = g.getEquity().getCityRescueShares(b);
        double marketOutBefore = bankLot.acbOut(), rescueOutBefore = rescueLot.acbOut();
        ex.tradeForCheck(b, Exchange.WORLD, OrderBook.Side.BUY, price, 30);
        ex.tradeForCheck(b, Exchange.FUND_HAND, OrderBook.Side.SELL, price, 30);
        close("a hand sale of 30 takes the 10 on the market book first, all its cost", bankLot.acbOut() - marketOutBefore, marketCost,
                1e-9 * Math.max(1, marketCost));
        close("...then 20 from the rescue book, at the rescue lot's own cost a share", rescueLot.acbOut() - rescueOutBefore,
                rescueCost * 20 / rescueHeld, 1e-9 * Math.max(1, rescueCost));
        close("...and the rescue lot's cost is the fund's own after", f.getRescueCost(), rescueCost * (1 - 20 / rescueHeld),
                1e-9 * Math.max(1, rescueCost));
    }

    /* ================= 3. bonds ================= */

    /** The issuer of section 3's one bond: a sector's name, which BondMarket.writeDown() finds an issuer's bonds by. */
    static final String ISSUER = "Mining";

    static BondMarket.Readings readings(int month) {
        return new BondMarket.Readings() {
            @Override public int month() { return month; }
            @Override public double curve(int months) { return .04; }
            @Override public double policyRate() { return .03; }
            @Override public double depositRate() { return .01; }
            @Override public double worldRate() { return .02; }
            @Override public double countryPremium() { return 0; }
            @Override public double monthlyGdp() { return 1_000_000; }
            @Override public double localPerUsd() { return 1; }
            @Override public boolean worldRunning() { return false; }
            @Override public double unit() { return 1; }
        };
    }

    static void bonds() {
        out.println("\n--- 3. bonds: a coupon is income, a default realizes its share, a maturity the face ---");
        BondMarket bm = new BondMarket();
        bm.attach(readings(1), null, null, null, null);
        TreasuryFund f = new TreasuryFund();
        bm.attachFund(f);
        CorporateBond b = new CorporateBond(1, ISSUER, 10_000, .06, 0, 24);
        b.world = 10_000;
        BondMarket.State s = new BondMarket.State();
        s.bonds = new java.util.ArrayList<>(List.of(b));
        s.nextId = 2;
        bm.restore(s);
        b = bm.getBonds().get(0);
        f.receive(5_000);
        bm.tradeForCheck(b, BondMarket.WORLD, OrderBook.Side.SELL, .98, 1_000);
        bm.tradeForCheck(b, BondMarket.FUND_HAND, OrderBook.Side.BUY, .98, 1_000);
        bm.tradeForCheck(b, BondMarket.WORLD, OrderBook.Side.SELL, 1.02, 1_000);
        bm.tradeForCheck(b, BondMarket.FUND, OrderBook.Side.BUY, 1.02, 1_000);
        FundLedger.Lot lot = f.getLedger().lot(FundLedger.bondKey(b.id()));
        check("fixture: the fund holds 2,000 of a bond's face, bought at .98 and 1.02", lot != null && Math.abs(b.city() - 2_000) < 1e-9);
        if (lot == null) return;
        close("...which cost 2,000, 1.00 a unit of face", lot.acb(), 2_000, 1e-9);
        double value = bm.modelPrice(b, 1);
        close("...and was worth its face at the bond's value at the step: a bargain's measure", lot.boughtValue(), 2_000 * value, 1e-9);
        // ...its cash spent and its shares where its mix wants them, so the step's rule neither buys nor rebalances.
        f.pay(f.getCash());
        bm.strikeCoupons();
        bm.takeMonth(1, 2_000 * TreasuryFund.EQUITY_WEIGHT / (1 - TreasuryFund.EQUITY_WEIGHT));
        double coupon = 2_000 * b.coupon() / CorporateBond.COUPONS_A_YEAR;
        close("a month's coupon is the lot's income", lot.income(), coupon, 1e-12);
        int couponMonth = -1;
        for (FundLedger.Activity a : f.getLedger().getActivity()) if (a.kind().equals(FundLedger.COUPON)) couponMonth = a.month();
        check("...its income in the last month that paid any, the month of the coupon's row",
                Math.abs(lot.lastIncome() - coupon) < 1e-12 && lot.lastIncomeMonth() == couponMonth && couponMonth >= 0);
        close("...and not its cost", lot.acb(), 2_000, 1e-9);
        close("...and the fund's, to the cent", f.getCoupons(), coupon, 1e-12);
        double keep = 1 - BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT;
        bm.writeDown(ISSUER, keep);
        close("a default at the bonds' loss given default realizes that share of the cost", lot.realized(),
                -2_000 * BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT, 1e-9);
        close("...and leaves the rest on the face kept", lot.acb(), 2_000 * keep, 1e-9);
        double realized = lot.realized();
        bm.writeDown(ISSUER, 1);
        close("a write-down that keeps the whole face realizes nothing", lot.realized(), realized, 0);
        double face = b.city(), acb = lot.acb();
        bm.redeemMaturing(b.maturityMonth());
        close("its maturity realizes the face less the cost", lot.realized() - realized, face - acb, 1e-9);
        check("...and closes the lot, with its record", lot.isClosed() && lot.acb() == 0 && lot.income() > 0);
        close("...the face is what the fund was paid", f.getPrincipal(), face, 1e-9);
        close("the ledger's proceeds are the counters' sales and principal", f.getLedger().proceeds(),
                f.getBondsSold() + f.getPrincipal(), 1e-9);
    }

    /* ================= 4. a split, and a reform ================= */

    static void splitAndReform() {
        out.println("\n--- 4. a split at SPLIT_AT and a currency reform ---");
        Game g = copy();
        int c = heldCompany(g);
        if (c < 0) return;
        Exchange ex = g.getExchange();
        TreasuryFund f = g.getFund();
        Equity reg = g.getEquity();
        f.receive(1_000_000);
        ex.bookOf(c).withdrawAll();
        String cell = Exchange.CELL + holder(g, c).key();
        ex.tradeForCheck(c, cell, OrderBook.Side.SELL, 2, 40);
        ex.tradeForCheck(c, Exchange.FUND_HAND, OrderBook.Side.BUY, 2, 40);
        // ...and a trade past SPLIT_AT times the founding price, so the step's split takes it.
        double high = 1.5 * Exchange.SPLIT_AT * reg.foundingPrice();
        ex.tradeForCheck(c, cell, OrderBook.Side.SELL, high, 1);
        ex.tradeForCheck(c, Exchange.FUND_HAND, OrderBook.Side.BUY, high, 1);
        FundLedger.Lot lot = f.getLedger().lot(FundLedger.shareKey(Equity.COMPANIES[c]));
        if (lot == null) { check("fixture: a lot", false); return; }
        double acb = lot.acb(), units = reg.getCityMarketShares(c), avg = acb / units;
        ex.splitForCheck();
        double k = ex.getSplit(c);
        check("fixture: the share split at SPLIT_AT, by a power of ten", k >= Exchange.SPLIT_AT && Math.abs(Math.log10(k) - Math.round(Math.log10(k))) < 1e-9);
        close("a split leaves the lot's cost where it was", lot.acb(), acb, 0);
        close("...the register's count goes up by the factor", reg.getCityMarketShares(c), units * k, 1e-9 * units * k);
        close("...and the average a share falls by it", lot.acb() / reg.getCityMarketShares(c), avg / k, 1e-12 * avg);
        boolean row = false;
        for (FundLedger.Activity a : f.getLedger().getActivity()) if (a.kind().equals(FundLedger.SPLIT) && a.units() == k) row = true;
        check("...and a row says so", row);

        // A reform: everything in money by the scale, share counts not.
        double factor = 1_000;
        double acb0 = lot.acb(), real0 = lot.realized(), bought0 = lot.bought();
        double rowMoney0 = 0;
        for (FundLedger.Activity a : f.getLedger().getActivity()) rowMoney0 += a.money();
        double units0 = reg.getCityMarketShares(c);
        boolean reformed = g.reformCurrencyForTest(factor);
        check("fixture: the currency reformed, " + (int) factor + " to one", reformed);
        if (!reformed) return;
        double scale = 1 / factor;
        close("a reform scales the lot's cost", lot.acb(), acb0 * scale, 1e-12 * acb0);
        close("...what it realized and what it was bought for", lot.realized() + lot.bought(), (real0 + bought0) * scale,
                1e-12 * Math.max(1, real0 + bought0));
        double rowMoney = 0;
        for (FundLedger.Activity a : f.getLedger().getActivity()) rowMoney += a.money();
        close("...and every row's money", rowMoney, rowMoney0 * scale, 1e-12 * Math.max(1, rowMoney0));
        close("...and not the shares it holds", reg.getCityMarketShares(c), units0, 0);
    }

    /* ================= 5. the save ================= */

    static void theSave() {
        out.println("\n--- 5. through a save, and an older save's ledger seeded ---");
        Game g = copy();
        int c = heldCompany(g);
        if (c < 0) return;
        Exchange ex = g.getExchange();
        TreasuryFund f = g.getFund();
        f.receive(10_000);
        ex.bookOf(c).withdrawAll();
        String cell = Exchange.CELL + holder(g, c).key();
        ex.tradeForCheck(c, cell, OrderBook.Side.SELL, 2, 60);
        ex.tradeForCheck(c, Exchange.FUND, OrderBook.Side.BUY, 2, 60);
        ex.tradeForCheck(c, Exchange.WORLD, OrderBook.Side.BUY, 2.5, 10);
        ex.tradeForCheck(c, Exchange.FUND_HAND, OrderBook.Side.SELL, 2.5, 10);
        g.fundBuyShares(c, 100);
        play(g);
        quietly(() -> g.saveGame(2));
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(2));
        FundLedger a = f.getLedger(), b = back.getFund().getLedger();
        boolean lots = a.getLots().size() == b.getLots().size();
        for (int i = 0; lots && i < a.getLots().size(); i++) {
            FundLedger.Lot x = a.getLots().get(i), y = b.getLots().get(i);
            lots = x.key().equals(y.key()) && x.acb() == y.acb() && x.realized() == y.realized() && x.proceeds() == y.proceeds()
                    && x.acbOut() == y.acbOut() && x.bought() == y.bought() && x.income() == y.income()
                    && x.since() == y.since() && x.seeded() == y.seeded() && x.closedMonth() == y.closedMonth();
        }
        check("every lot comes back exactly: its cost, what it realized and paid, and since when", lots && !a.getLots().isEmpty());
        boolean rows = a.getActivity().size() == b.getActivity().size();
        for (int i = 0; rows && i < a.getActivity().size(); i++) {
            FundLedger.Activity x = a.getActivity().get(i), y = b.getActivity().get(i);
            rows = x.month() == y.month() && x.kind().equals(y.kind()) && java.util.Objects.equals(x.key(), y.key())
                    && x.money() == y.money() && x.units() == y.units() && x.realized() == y.realized()
                    && x.open() == y.open() && x.asked() == y.asked() && x.lapsed() == y.lapsed();
        }
        check("...every row, an order still on the book among them", rows && a.getActivity().stream().anyMatch(FundLedger.Activity::open));
        check("...the hand's order on the book, and the month tracking began", back.getFund().getPosted().size() == f.getPosted().size()
                && b.getTrackingSince() == a.getTrackingSince() && !back.getFund().needsLedgerSeed());

        // An older save: the fund's state with no ledger, as a save from before 0.7.39 reads.
        TreasuryFund.State st = back.getFund().toState();
        st.ledger = null;
        back.getFund().restore(st);
        check("an older save's fund has no ledger, and waits to have one seeded", back.getFund().needsLedgerSeed());
        back.seedFundLedger();
        FundLedger seeded = back.getFund().getLedger();
        FundLedger.Lot l = seeded.lot(FundLedger.shareKey(Equity.COMPANIES[c]));
        check("...seeded at the load's month", seeded.getTrackingSince() == back.getMonth() && !back.getFund().needsLedgerSeed());
        check("...each market lot at its market value then, flagged so", l != null && l.seeded() && l.since() == back.getMonth()
                && Math.abs(l.acb() - back.getEquity().getCityMarketShares(c) * back.getExchange().price(c)) < 1e-9);
        check("...with the dividends paid before it apart", l != null && l.incomeBefore() == back.getEquity().getLifetimeDividendsCity(c));
        check("...and one row says what happened", seeded.getActivity().size() == 1
                && seeded.getActivity().get(0).kind().equals(FundLedger.TRACKING));
        TreasuryFund none = new TreasuryFund();
        none.restore(null);
        check("a save from before the fund itself waits for a ledger too, which it seeds from nothing", none.needsLedgerSeed());
        olderSaveWithARescueBook();
    }

    /**
     * AN OLDER SAVE WITH A RESCUE BOOK (0.7.39, after its docs pass): the
     * path a city that resolved its bank before 0.7.39 takes at its first
     * load - Jerus's long city, if it has. The fixture rescues the bank, sells
     * a quarter of the rescue book by hand and buys bank shares back onto the
     * market book, then saves; the save is loaded with the ledger taken out
     * of the fund's JSON, as a 0.7.38 save reads, and set beside the city
     * that tracked it all along. The seeded rescue lot must be the tracked
     * one: its cost the fund's own record of what the city paid (never a
     * market value), untagged, since the rescue, its income, proceeds, cost
     * out and realized gain.
     */
    static void olderSaveWithARescueBook() {
        Game g = copy();
        int c = heldCompany(g), b = Equity.BANK;
        if (c < 0) return;
        Exchange ex = g.getExchange();
        TreasuryFund f = g.getFund();
        f.receive(10_000);
        // A market lot in a company, bought from a household.
        ex.bookOf(c).withdrawAll();
        ex.tradeForCheck(c, Exchange.CELL + holder(g, c).key(), OrderBook.Side.SELL, 2, 40);
        ex.tradeForCheck(c, Exchange.FUND, OrderBook.Side.BUY, 2, 40);
        // The bank's shares on the market book, then the rescue.
        ex.bookOf(b).withdrawAll();
        Household bankHolder = holder(g, b);
        double cellHolds = bankHolder == null ? 0 : bankHolder.shares[b] * bankHolder.households();
        double worldHolds = g.getEquity().getForeignShares(b);
        String bankSeller = cellHolds >= worldHolds ? Exchange.CELL + bankHolder.key() : Exchange.WORLD;
        double q = Math.max(cellHolds, worldHolds) / 2;
        if (!(q > 0)) { check("fixture: somebody other than the city holds the bank's shares", false); return; }
        double p0 = ex.price(b);
        f.receive(q * p0);
        ex.tradeForCheck(b, bankSeller, OrderBook.Side.SELL, p0, q);
        ex.tradeForCheck(b, Exchange.FUND_HAND, OrderBook.Side.BUY, p0, q);
        g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
        FundCheck.takeEquityTo(g.getBank(), -10 * Math.max(1_000, g.getCentralBank().ceiling()));
        play(g);
        TreasuryFund.Resolution r = g.getLastResolution();
        if (r == null) { check("fixture: the month resolved the failed bank for its shares", false); return; }
        // A quarter of the rescue book sold by hand, and ten shares bought back onto the market book.
        double rescueHeld = g.getEquity().getCityRescueShares(b), price = ex.price(b);
        ex.bookOf(b).withdrawAll();
        ex.tradeForCheck(b, Exchange.WORLD, OrderBook.Side.BUY, price, rescueHeld / 4);
        ex.tradeForCheck(b, Exchange.FUND_HAND, OrderBook.Side.SELL, price, rescueHeld / 4);
        f.receive(10 * price * 10);
        ex.tradeForCheck(b, Exchange.WORLD, OrderBook.Side.SELL, price, 10);
        ex.tradeForCheck(b, Exchange.FUND, OrderBook.Side.BUY, price, 10);
        play(g);
        FundLedger tracked = f.getLedger();
        FundLedger.Lot was = tracked.lot(FundLedger.rescueKey(Equity.COMPANIES[b]));
        check("fixture: a city that resolved its bank, sold some of the rescue book and holds bank shares on its market book too",
                was != null && !was.isClosed() && was.proceeds() > 0 && g.getEquity().getCityRescueShares(b) > 0
                        && g.getEquity().getCityMarketShares(b) > 0 && g.getEquity().getCityMarketShares(c) > 0);
        if (was == null) return;
        quietly(() -> g.saveGame(3));
        Game seeded = new Game(files);
        try {
            com.google.gson.JsonObject json = com.google.gson.JsonParser
                    .parseString(java.nio.file.Files.readString(files.saveFile(3))).getAsJsonObject();
            check("fixture: the save carries the ledger inside the fund", json.getAsJsonObject("fund").has("ledger"));
            json.getAsJsonObject("fund").remove("ledger");
            java.nio.file.Files.writeString(files.saveFile(4), new com.google.gson.Gson().toJson(json));
            java.nio.file.Files.copy(files.historyFile(3), files.historyFile(4), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (java.io.IOException e) {
            check("fixture: the save read, its ledger taken out and written as an older save (" + e + ")", false);
            return;
        }
        quietly(() -> seeded.loadGameSave(4));
        FundLedger led = seeded.getFund().getLedger();
        check("a save with a rescue book and no ledger is seeded at its load: tracking from that month, one row to say so",
                !seeded.getFund().needsLedgerSeed() && led.getTrackingSince() == seeded.getMonth() && seeded.getMonth() == g.getMonth()
                        && led.getActivity().size() == 1 && led.getActivity().get(0).kind().equals(FundLedger.TRACKING));
        FundLedger.Lot lot = led.lot(FundLedger.rescueKey(Equity.COMPANIES[b]));
        FundView.Position pos = FundView.sharePosition(seeded, b, true);
        check("fixture: its rescue book is worth something other than what the city paid for it", pos != null
                && Math.abs(pos.value() - seeded.getFund().getRescueCost()) > 1e-6 * Math.max(1, pos.value()));
        if (lot == null || pos == null) { check("...its rescue lot is there", false); return; }
        check("...its rescue lot costed at what the city paid for the shares it still holds, exactly, not at their market value",
                pos.acb() == seeded.getFund().getRescueCost() && pos.acb() == f.getRescueCost() && pos.acb() != pos.value());
        check("...and not tagged as counted at market value, its cost dated from the rescue",
                !lot.seeded() && !pos.seeded() && lot.since() == r.month() && lot.since() == was.since());
        close("...what the hand's sales of it brought, as the city that tracked it all along has it", lot.proceeds(), was.proceeds(),
                1e-9 * Math.max(1, was.proceeds()));
        close("...the cost those sales took out: what the rescue paid, less what is left of it", lot.acbOut(), was.acbOut(),
                1e-9 * Math.max(1, was.acbOut()));
        close("...and the gain they realized", lot.realized(), was.realized(), 1e-9 * Math.max(1, Math.abs(was.realized())));
        close("...its dividends since the rescue, the rescue book's own", lot.income(), was.income(), 1e-9 * Math.max(1, was.income()));
        boolean market = true;
        int lots = 0;
        for (FundLedger.Lot l : led.getLots()) {
            lots++;
            if (l.isRescue()) continue;
            double value = l.isBond() ? seeded.getBondMarket().bond(l.bondId()).city()
                    * seeded.getBondMarket().modelPrice(seeded.getBondMarket().bond(l.bondId()), seeded.getMonth())
                    : seeded.getEquity().getCityMarketShares(FundView.companyOf(l.key())) * seeded.getExchange().price(FundView.companyOf(l.key()));
            market &= l.seeded() && l.since() == seeded.getMonth() && Math.abs(l.acb() - value) <= 1e-9 * Math.max(1, value);
        }
        check("its market lots, the bank's among them, at their market value at the load, each tagged with the month",
                market && led.lot(FundLedger.shareKey(Equity.COMPANIES[b])) != null && led.lot(FundLedger.shareKey(Equity.COMPANIES[c])) != null);
        FundLedger.Lot bankMarket = led.lot(FundLedger.shareKey(Equity.COMPANIES[b]));
        close("...the bank's market lot with the dividends before it apart from the rescue book's",
                bankMarket == null ? Double.NaN : bankMarket.incomeBefore(),
                Math.max(0, seeded.getEquity().getLifetimeDividendsCity(b) - seeded.getFund().getDividendsRescue()), 1e-9);
        close("...and the row counts every holding, the rescue lot among them", led.getActivity().get(0).units(), lots, 0);
    }

    /* ================= 6 and 7. the identity, and by kind ================= */

    record Snap(double mv, double acb, double realized, double bought, double sold, double principal, double rescues,
                double ledgerBought, double ledgerProceeds) {
        static Snap of(Game g) {
            TreasuryFund f = g.getFund();
            FundLedger l = f.getLedger();
            return new Snap(g.fundSharesValue() + g.fundBondsValue(), l.acbHeld() + f.getRescueCost(), l.realized(),
                    f.getSharesBought() + f.getBondsBought(), f.getSharesSold() + f.getBondsSold(), f.getPrincipal(),
                    f.getRescuesPaid(), l.purchases(), l.proceeds());
        }
    }

    /** The identity's residual between two snapshots: realized + the change in unrealized, less the change in value + proceeds - purchases - rescues. */
    static double residual(Snap s0, Snap s) {
        double lhs = (s.realized - s0.realized) + ((s.mv - s.acb) - (s0.mv - s0.acb));
        double rhs = (s.mv - s0.mv) + (s.sold - s0.sold) + (s.principal - s0.principal) - (s.bought - s0.bought) - (s.rescues - s0.rescues);
        return lhs - rhs;
    }

    static void theIdentity() {
        out.println("\n--- 6. the identity over a played run, and 7. since it began, by kind ---");
        Game g = copy();
        g.setRescueMode(TreasuryFund.RescueMode.AUTOMATIC);
        g.setCashForTest(g.getCash() + 200_000);
        g.fundPayIn(150_000);
        g.setFundDial(1);
        Snap s0 = Snap.of(g);
        double worst = 0, worstCounters = 0;
        int c = heldCompany(g);
        for (int m = 0; m < 36; m++) {
            // ...and the hand, so the run trades whatever the book offers the rule: a buy over fair, a sale under it.
            if (c >= 0 && m % 6 == 0) g.fundBuyShares(c, 2_000, g.getExchange().fair(c) * 1.2);
            if (c >= 0 && m % 6 == 3) g.fundSellShares(c, g.getEquity().getCityShares(c) / 4, g.getExchange().fair(c) * .8);
            Snap before = Snap.of(g);
            play(g);
            Snap after = Snap.of(g);
            double scale = Math.max(1, Math.abs(after.mv));
            worst = Math.max(worst, Math.abs(residual(before, after)) / scale);
            worstCounters = Math.max(worstCounters, Math.abs((after.ledgerBought - s0.ledgerBought) - (after.bought - s0.bought))
                    + Math.abs((after.ledgerProceeds - s0.ledgerProceeds) - (after.sold - s0.sold) - (after.principal - s0.principal)));
        }
        TreasuryFund f = g.getFund();
        int buys = 0, sells = 0;
        for (FundLedger.Activity a : f.getLedger().getActivity()) {
            if (a.kind().equals(FundLedger.BUY) && !a.hand()) buys++;
            if (a.kind().equals(FundLedger.SELL) && !a.hand()) sells++;
        }
        out.printf("   36 months: the rule traded %d lot-months buying and %d selling, $%,.0fk of shares and $%,.0fk of bonds bought;"
                        + " realized $%,.3fk; the worst month's residual %.2e of what it holds%n",
                buys, sells, f.getSharesBought(), f.getBondsBought(), f.getLedger().realized(), worst);
        check("fixture: the rule bought something over the run", f.getSharesBought() + f.getBondsBought() > 0);
        check("realized + the change in unrealized = the change in value + proceeds - purchases - rescues, every month",
                worst <= 1e-9);
        check("...and the ledger's purchases and proceeds are the fund's own counters'", worstCounters <= 1e-6);
        close("the whole run, end to end, the same", residual(s0, Snap.of(g)) / Math.max(1, Snap.of(g).mv), 0, 1e-9);

        FundView.Portfolio p = FundView.portfolio(g);
        double sum = 0;
        for (FundView.Kind k : p.kinds()) sum += k.gain();
        close("since it began, the gain by kind adds up to the fund's: worth + taken out - put in", sum, p.gain(),
                1e-6 * Math.max(1, p.value()));
        close("...whose put in is the pay-ins, the rescues and the preferred", p.putIn(),
                f.getPaidInFromSurplus() + f.getPaidInFromCash() + f.getHandPaidIn() + f.getRescuesPaid() + f.getPreferredBought(), 1e-9);
        double cashBack = f.getPaidInFromSurplus() + f.getPaidInFromCash() + f.getHandPaidIn() + f.getDividendsMarket()
                + f.getDividendsRescue() + f.getCoupons() + f.getPrincipal() + f.getPreferredDividends() + f.getPreferredRedeemed()
                + f.getWarrantsBoughtBack() + f.getSharesSold() + f.getBondsSold() - f.getSharesBought() - f.getBondsBought()
                - f.getTransfersPaid() - f.getHandDrawnOut();
        close("...because its cash is what came in less what went out, to the cent", cashBack, f.getCash(), 1e-6);
        // Every position's cost is known: a city founded with the ledger has nothing seeded.
        boolean known = true;
        for (FundView.Position pos : FundView.positions(g)) {
            if (!FundView.WARRANTS.equals(pos.kind()) && (Double.isNaN(pos.acb()) || pos.seeded())) known = false;
        }
        check("every holding of a city founded with the ledger has a cost, none counted at market value", known);
    }

    /* ================= 8. the hand ================= */

    static void theHand() {
        out.println("\n--- 8. the hand: its cap, its cash held from the rule, its order on the record ---");
        Game g = copy();
        int c = heldCompany(g);
        if (c < 0) return;
        TreasuryFund f = g.getFund();
        g.setCashForTest(g.getCash() + 100_000);
        g.fundPayIn(50_000);
        Equity reg = g.getEquity();
        double cash = f.getCash();
        g.fundBuyShares(c, 30_000);
        close("a buy waiting for the step holds its money from the rule", f.handReserve(), 30_000, 1e-9);
        close("...which bids with the rest", f.cashForTheRule(), cash - 30_000, 1e-9);
        close("...and a second order can take no more than the rest", g.fundCashFree(), cash - 30_000, 1e-9);
        play(g);
        TreasuryFund.HandOrder posted = f.getPosted().isEmpty() ? null : f.getPosted().get(0);
        check("fixture: the order was posted at the step and rests on the book", posted != null);
        if (posted == null) return;
        double room = TreasuryFund.OWNERSHIP_LIMIT * reg.getShares(c) - (reg.getCityMarketShares(c) - posted.filled());
        check("the hand's buy is posted no further than the room under the 10% cap the rule keeps",
                posted.units() <= room + 1e-9);
        close("...and what it still holds of the cash is its units still to fill at its price", f.handReserve(),
                (posted.units() - posted.filled()) * posted.price(), 1e-9);
        double ruleBids = 0;
        for (OrderBook.Order o : g.getExchange().bookOf(c).bids()) if (o.who().equals(Exchange.FUND)) ruleBids += o.quantity() * o.price();
        check("...and the rule's bid on the same book rests on no more than what the hand leaves",
                ruleBids <= Math.max(0, f.getCash() - f.handReserve()) + 1e-6);
        FundLedger.Activity row = null;
        for (FundLedger.Activity a : f.getLedger().getActivity()) if (a.hand() && a.open() && a.buy()) row = a;
        check("the order is a row from the step that posted it, open while it rests", row != null
                && Math.abs(row.asked() - posted.units()) < 1e-9 && Math.abs(row.limit() - posted.price()) < 1e-12);
        play(g);
        check("the next step closes it: off the book and off the cash", f.getPosted().isEmpty() && f.handReserve() == 0);
        check("...its row says what it filled and what lapsed", row != null && !row.open()
                && Math.abs(row.units() + row.lapsed() - row.asked()) < 1e-9);

        // An order for nothing the step can post is a row that says why.
        g.fundBuyShares(c, 1e15);
        double queued = f.getHandOrders().isEmpty() ? 0 : f.getHandOrders().get(0).amount();
        close("fixture: a buy far past the cap is queued at the free cash", queued, g.getFund().getCash(), 1e-6);
        Game full = copy();
        TreasuryFund ff = full.getFund();
        full.setCashForTest(full.getCash() + 1_000);
        full.fundPayIn(1_000);
        full.getEquity().moveCity(c, TreasuryFund.OWNERSHIP_LIMIT * full.getEquity().getShares(c));
        full.fundBuyShares(c, 500);
        play(full);
        boolean said = false;
        for (FundLedger.Activity a : ff.getLedger().getActivity()) {
            if (a.kind().equals(FundLedger.LAPSED) && a.note() != null && a.note().contains("cap")) said = true;
        }
        check("a buy in a company the fund already holds 10% of is not posted, and its row says why", said
                && ff.getPosted().isEmpty());
        theCapCountsEveryBuy(c);
    }

    /**
     * THE CAP COUNTS EVERY BUY OF THE FUND'S ON THE COMPANY (0.7.39, closed
     * after its docs pass): what it holds, its own bids on the book - the
     * rule's and the hand's - and the hand's buys still waiting, as if every
     * one filled (Exchange.fundRoom()). Counted against the market book
     * alone, the rule's bid and two orders each inside that room could all
     * fill in the month and carry the fund past 10%, which the rule then asks
     * back at fair value: the loss the cap at posting was put there to stop.
     * The rule's bid makes way for the hand's: sized while the orders wait,
     * and filled between steps no further than the room they leave.
     */
    static void theCapCountsEveryBuy(int c) {
        Game twin = copy();
        Equity r0 = twin.getEquity();
        Exchange x0 = twin.getExchange();
        double caps = 0;
        for (int i = 0; i < Equity.COMPANIES.length; i++) {
            if (r0.getShares(i) > 0 && x0.fair(i) > 0) caps += x0.marketCap(r0, i);
        }
        // The rule given money for about a hundredth of the market and a few orders' worth; orders for 4% of the company.
        double each = .04 * r0.getShares(c) * x0.fair(c);
        double lump = .01 * caps + 4 * each;
        twin.setCashForTest(twin.getCash() + lump);
        twin.fundPayIn(lump);
        play(twin);
        double ruleAlone = x0.bookOf(c).resting(Exchange.FUND, OrderBook.Side.BUY);

        // The same city and money, with two orders for 4% each waiting for the step.
        Game k = copy();
        TreasuryFund fk = k.getFund();
        Equity rk = k.getEquity();
        Exchange xk = k.getExchange();
        k.setCashForTest(k.getCash() + lump);
        k.fundPayIn(lump);
        k.fundBuyShares(c, each);
        k.fundBuyShares(c, each);
        double mktBefore = rk.getCityMarketShares(c);
        play(k);
        OrderBook bk = xk.bookOf(c);
        double cap = TreasuryFund.OWNERSHIP_LIMIT * rk.getShares(c);
        double mkt = rk.getCityMarketShares(c);
        double ruleBid = bk.resting(Exchange.FUND, OrderBook.Side.BUY), handBids = bk.resting(Exchange.FUND_HAND, OrderBook.Side.BUY);
        List<TreasuryFund.HandOrder> posted = fk.getPosted();
        // Since 0.7.48 (C3) the rule bids at the desk's ask, which fills at the step: what it bid is what rests and what it took.
        double handTook = 0;
        for (TreasuryFund.HandOrder o : posted) handTook += o.filled();
        double ruleTook = mkt - mktBefore - handTook;
        check("fixture: the rule bids for the company at the step - resting, or taken at the desk's ask - and both orders were posted beside it",
                ruleBid + ruleTook > FundLedger.DUST && posted.size() == 2);
        if (posted.size() != 2) return;
        double asked1 = posted.get(0).amount() / posted.get(0).price(), asked2 = posted.get(1).amount() / posted.get(1).price();
        out.printf("   the cap %,.0f shares: alone the rule bids %,.0f; the orders ask %,.0f and %,.0f - %.1f%% of the company"
                        + " had all three been posted whole; the rule bids %,.0f beside them%n", cap, ruleAlone, asked1, asked2,
                (mkt + ruleAlone + asked1 + asked2) / rk.getShares(c) * 100, ruleBid);
        check("fixture: each order fits the room the market book alone leaves, and with the rule's bid they come to more than 10%",
                asked1 <= cap - mkt && asked2 <= cap - mkt && mkt + ruleAlone + asked1 + asked2 > cap + 1);
        check("the rule's bid makes way for the orders waiting: it rests on no more than the room they leave",
                ruleBid < ruleAlone - 1 && ruleBid <= cap - mkt - asked1 - asked2 + 1e-9 * cap);
        check("...and both orders are posted whole", Math.abs(posted.get(0).units() - asked1) < 1e-9 * cap
                && Math.abs(posted.get(1).units() - asked2) < 1e-9 * cap);
        check("...so the fund's market book and every bid of its on the book come to no more than the cap",
                mkt + ruleBid + handBids <= cap * (1 + 1e-12));
        check("fixture: every bid of the fund's on the book filled - the rule's and both orders'", fillTheFundsBids(k, c));
        check("...and the fund holds no more than the cap of the company: nothing for the rule to ask back at fair value",
                rk.getCityMarketShares(c) <= cap * (1 + 1e-12));

        // Two orders past the room on their own: the rule bids nothing there, and the second order stops at the room.
        Game m = copy();
        TreasuryFund fm = m.getFund();
        Equity rm = m.getEquity();
        Exchange xm = m.getExchange();
        m.setCashForTest(m.getCash() + lump);
        m.fundPayIn(lump);
        m.fundBuyShares(c, 1.5 * each);
        m.fundBuyShares(c, 1.5 * each);
        play(m);
        OrderBook bm = xm.bookOf(c);
        double capM = TreasuryFund.OWNERSHIP_LIMIT * rm.getShares(c), mktM = rm.getCityMarketShares(c);
        List<TreasuryFund.HandOrder> postedM = fm.getPosted();
        check("fixture: two orders for 6% of the company each, both posted", postedM.size() == 2);
        if (postedM.size() != 2) return;
        double a1 = postedM.get(0).amount() / postedM.get(0).price(), a2 = postedM.get(1).amount() / postedM.get(1).price();
        check("...together past the room the market book leaves", a1 + a2 > capM - mktM + 1 && a1 <= capM - mktM);
        check("orders past the room on their own leave the rule's bid nothing in the company",
                bm.resting(Exchange.FUND, OrderBook.Side.BUY) <= FundLedger.DUST);
        check("...and the step posts the first whole and the second only to the room the first leaves",
                Math.abs(postedM.get(0).units() - a1) < 1e-9 * capM && postedM.get(1).units() < a2 - 1);
        double sumM = mktM + bm.resting(Exchange.FUND, OrderBook.Side.BUY) + bm.resting(Exchange.FUND_HAND, OrderBook.Side.BUY);
        check("...so the market book and every bid of the fund's come to the cap and no more",
                sumM <= capM * (1 + 1e-12) && sumM >= capM * (1 - 1e-9));
        check("fixture: both orders filled", fillTheFundsBids(m, c));
        check("...and the fund holds the cap of the company and no more",
                rm.getCityMarketShares(c) <= capM * (1 + 1e-12) && rm.getCityMarketShares(c) >= capM * (1 - 1e-9));

        // The rule's bid resting at the whole room, and two orders placed after it: it makes way between steps too.
        Game w = copy();
        TreasuryFund fw = w.getFund();
        Equity rw = w.getEquity();
        Exchange xw = w.getExchange();
        double big = .2 * caps + 4 * each;
        w.setCashForTest(w.getCash() + big);
        w.fundPayIn(big);
        play(w);
        OrderBook bw = xw.bookOf(c);
        double capW = TreasuryFund.OWNERSHIP_LIMIT * rw.getShares(c), heldW = rw.getCityMarketShares(c);
        double ruleW = bw.resting(Exchange.FUND, OrderBook.Side.BUY);
        check("fixture: a fund whose rule bids for the whole room in the company", ruleW > FundLedger.DUST
                && Math.abs(heldW + ruleW - capW) <= 1e-9 * capW);
        String key = FundLedger.shareKey(Equity.COMPANIES[c]);
        FundView.Quote qw = FundView.quote(w, new FundView.Order(key, true, true, each, 0));
        check("the ticket offers an order the room all the same: the rule's bid makes way for it",
                qw != null && !qw.capped() && Math.abs(qw.room() - (capW - heldW)) <= 1e-9 * capW
                        && Math.abs(qw.ruleGivesWay() - qw.units()) <= 1e-9 * capW
                        && Math.abs(qw.capAfter() * rw.getShares(c) - capW) <= 1e-9 * capW);
        // ...each 4% of the company, or two-fifths of the room the step left when that is less: since 0.7.48 (C3) the
        // rule's bid fills part of its room at the step, at the desk's ask, and the orders are to fit what it still bids for.
        double eachW = Math.min(each, .4 * (capW - heldW) * xw.fair(c));
        w.fundBuyShares(c, eachW);
        w.fundBuyShares(c, eachW);
        double queuedW = xw.handOnOrder(fw, c);
        close("fixture: two orders placed after it, for 4% of the company each or two-fifths of the room left", queuedW,
                2 * eachW / xw.fair(c), 1e-9 * capW);
        check("...everything the fund could hold of it, every buy filled, the rule's as far as it may, is still the cap",
                xw.fundCouldHold(rw, fw, c, 0) <= capW * (1 + 1e-12));
        double before = rw.getCityMarketShares(c);
        fillTheFundsBids(w, c);
        double after = rw.getCityMarketShares(c);
        check("a seller hitting the rule's bid fills it no further than the room the two orders leave",
                after > before + 1 && after <= capW - queuedW + 1e-9 * capW);
        play(w);
        List<TreasuryFund.HandOrder> postedW = fw.getPosted();
        double sumW = rw.getCityMarketShares(c) + bw.resting(Exchange.FUND, OrderBook.Side.BUY)
                + bw.resting(Exchange.FUND_HAND, OrderBook.Side.BUY);
        check("at the step both orders post, and the market book and every bid of the fund's come to no more than the cap",
                postedW.size() == 2 && postedW.get(0).units() > FundLedger.DUST && postedW.get(1).units() > FundLedger.DUST
                        && sumW <= rw.getShares(c) * TreasuryFund.OWNERSHIP_LIMIT * (1 + 1e-12));
        check("fixture: every bid filled", fillTheFundsBids(w, c));
        check("...and the fund holds no more than the cap of the company",
                rw.getCityMarketShares(c) <= TreasuryFund.OWNERSHIP_LIMIT * rw.getShares(c) * (1 + 1e-12));

        // The ticket reckons the room as the step does: the market book and the hand's order waiting; the rule's bid makes way.
        Game t = copy();
        TreasuryFund ft = t.getFund();
        Equity rt = t.getEquity();
        Exchange xt = t.getExchange();
        ft.receive(300 * xt.fair(c));
        xt.bookOf(c).withdrawAll();
        xt.tradeForCheck(c, Exchange.CELL + holder(t, c).key(), OrderBook.Side.SELL, xt.fair(c), 300);
        xt.tradeForCheck(c, Exchange.FUND_HAND, OrderBook.Side.BUY, xt.fair(c), 300);
        t.setCashForTest(t.getCash() + lump);
        t.fundPayIn(lump);
        play(t);
        double held = rt.getCityMarketShares(c), rule = xt.bookOf(c).resting(Exchange.FUND, OrderBook.Side.BUY);
        t.fundBuyShares(c, each / 2);
        double waiting = ft.getHandOrders().get(0).amount() / xt.fair(c);
        double capT = TreasuryFund.OWNERSHIP_LIMIT * rt.getShares(c);
        check("fixture: the fund holds some of the company, the rule bids for more, an order of the hand's waits, and room is left",
                held >= 300 - 1e-9 && rule > FundLedger.DUST && waiting > FundLedger.DUST && capT - held - waiting > rule + 1);
        FundView.Quote qt = FundView.quote(t, new FundView.Order(key, true, true, capT * xt.fair(c), 0));
        if (qt == null) { check("...the ticket quotes a buy", false); return; }
        close("the ticket's room is the cap less what the fund holds and the order waiting",
                qt.room(), capT - held - waiting, 1e-9 * capT);
        close("...the model's own reckoning, which the step trims the order with and the rule's bid fills by",
                qt.room(), xt.fundRoom(rt, ft, c), 0);
        check("...and a ticket for the whole cap is capped at it, and says so",
                qt.capped() && qt.warnings().contains("capped") && Math.abs(qt.units() - qt.room()) <= 1e-9 * capT);
        close("...the market book against the cap after: every one of them filled, the cap itself",
                qt.capAfter(), TreasuryFund.OWNERSHIP_LIMIT, 1e-12);
        close("...the fund's other buys on it counted apart, the order waiting and the rule's bid, as a share of the company",
                qt.capOrders(), (rule + waiting) / rt.getShares(c), 1e-12);
        close("...and the rule's bid makes way for as much of the order as it would otherwise take", qt.ruleGivesWay(), rule,
                1e-9 * capT);
    }

    /** Every bid of the fund's on a company's book filled, at its price: the others' bids withdrawn, households selling into the fund's. True when none is left. */
    static boolean fillTheFundsBids(Game g, int c) {
        Exchange ex = g.getExchange();
        OrderBook book = ex.bookOf(c);
        g.getFund().receive(4 * TreasuryFund.OWNERSHIP_LIMIT * g.getEquity().getShares(c) * ex.fair(c));
        java.util.Set<String> others = new java.util.HashSet<>();
        for (OrderBook.Order o : book.bids()) if (!Exchange.isFund(o.who())) others.add(o.who());
        for (String who : others) book.withdraw(who);
        double want = book.resting(Exchange.FUND, OrderBook.Side.BUY) + book.resting(Exchange.FUND_HAND, OrderBook.Side.BUY);
        for (Household h : g.getHouseholdBalance().cells()) {
            if (!(want > FundLedger.DUST)) break;
            if (h.households() <= 0) continue;
            double has = h.shares[c] * h.households();
            if (!(has > FundLedger.DUST)) continue;
            want -= filled(ex.tradeForCheck(c, Exchange.CELL + h.key(), OrderBook.Side.SELL, ex.fair(c) / 2, Math.min(has, want)));
            book.withdraw(Exchange.CELL + h.key());
            // ...and no further than the bids would take: a bid the rule's room trimmed is gone.
            want = Math.min(want, book.resting(Exchange.FUND, OrderBook.Side.BUY) + book.resting(Exchange.FUND_HAND, OrderBook.Side.BUY));
        }
        return book.resting(Exchange.FUND, OrderBook.Side.BUY) + book.resting(Exchange.FUND_HAND, OrderBook.Side.BUY) <= FundLedger.DUST;
    }
}
