package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The grocers' supplier credit (0.7.44): stock bought on the suppliers' credit
 * when the till cannot pay for it, owed for a month, repaid out of the sale it
 * stocked - on both sides' books, through the money audit to the cent.
 *
 * WHY. Since 0.7.12 a firm restocks only as far as its cash and its credit
 * reach, and since 0.7.43 the shops sell what is on their shelf and no more;
 * a grocer whose till ran dry bought nothing, sold nothing the next month and
 * stayed dry, and 11.9% of the autopilot's months after month 240 delivered
 * under 5% of what was wanted (runs/diag-0743.md, section 5). SupplierCredit
 * is the design; EconomyManager.purchaseBudget() opens it, Sector.bookPurchase()
 * and closePurchases() strike it, EconomyManager.settleSupplierCredit() moves
 * the money at the strike, and MoneyAudit declares the world's share.
 *
 * WHAT THIS HAS TO PROVE
 *
 *   1. The rule alone: a till that covers the stock takes no credit; one that
 *      cannot takes the part it cannot cover, never more than the limit nor
 *      than the covered goods' bill - the fleet is not covered - shared over
 *      the suppliers by what each filled; nothing is noted outside a
 *      clearing; the strike makes what was bought owed and what was owed
 *      repaid; it saves, restores and moves with a reform.
 *   2. In a city, a grocer whose till and lender cover none of its stock -
 *      the trap - restocks on its suppliers' credit, all of it on credit and
 *      no more than they would wait for: a month of the baskets it expects to
 *      sell, at what they cost to bring in.
 *   3. At the strike it owes that, on its balance sheet; each local supplier
 *      is owed its share on its own; every sector's cash flow still adds up,
 *      the world's share crosses the audit as a financial inflow to the cent,
 *      and the lender reads both sides as if the credit were not there.
 *   4. The stock sold, and the strike after repays what was owed out of the
 *      takings of that sale, which were more than it; the suppliers are paid;
 *      the world's repayment crosses the audit as a financial outflow to the
 *      cent; the audit closes every month.
 *   5. A grocer whose till covers its stock takes none.
 *   6. Saved and loaded, what it owes and what it bought on credit come back.
 *
 * FIXTURES (FS9). Items 2-6 run in a town stood at once (addStack, no construction, farms
 * among it so there is a supplier in the city) and played only until the shops expect to sell
 * (Retail.expectedBaskets() > 0, at most WARM_AT_MOST months, a fixture that fails when the
 * bound passes): a sale and a running shop are what the suppliers' limit is read from, and
 * neither has a door to set. Each month's order is then set by hand between months: the
 * shelf emptied (setStoreInventory) and the till set (setSectorCash) - covering, in the month
 * of item 5, and nought in the trap's; the lender's room shut by a claim of 1.2 times its
 * assets and its quarter (issueLoan, recordStatement), no month played between them; the shelf
 * made up to the baskets the credit was lent for before the strike that repays it, once the
 * stock is asserted to have reached it, so the sale the credit is repaid out of sells what it
 * was lent for. WHY: the town played two years moved under the sizes and the sale these were
 * read off, and a month of the stood town's lopsided delivery sells a third of what was lent for.
 */
public class SupplierCreditCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-92s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        out.printf("%-92s %s  %,.9f against %,.9f%n", label, ok ? "OK" : "FAIL", actual, expected);
    }

    static void quietly(Runnable r) {
        PrintStream real = System.out;
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(real); }
    }

    public static void main(String[] args) throws Exception {
        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });
        Path root = TempDirs.create("suppliercredit");

        theRule();
        inACity(root);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ============================ 1. THE RULE ALONE ============================ */

    /** The two local suppliers the rule's fixtures name: the farms, which fill the shelf's meat, and the car makers, which fill the fleet's vans. */
    static final String FARM = Sectors.AGRICULTURE, LORRIES = Sectors.AUTOMOTIVE;

    /** One clearing on a fresh ledger covering meat and grains: 200 of meat from the farms, 50 of grains from the world, 30 of vans. */
    static SupplierCredit clearing(double limit, double till) {
        SupplierCredit c = new SupplierCredit(EnumSet.of(Good.MEAT, Good.GRAINS));
        c.open(limit, till);
        c.note(FARM, Good.MEAT, 200);
        c.note(Trade.WORLD, Good.GRAINS, 50);
        c.note(LORRIES, Good.VANS, 30);
        c.close();
        return c;
    }

    static void theRule() {
        out.println("--- the rule alone: credit for what the till cannot cover, up to the limit, the shelf's only, by supplier ---");

        SupplierCredit full = clearing(1_000, 280);
        close("a till that covers its stock bill takes none of its suppliers' credit", full.boughtTotal(), 0, 0);

        SupplierCredit capped = clearing(100, 150);
        close("one that cannot takes the part it cannot cover, never more than the limit", capped.boughtTotal(), 100, 1e-12);
        close("...shared over the suppliers by what each filled: the farms' share", capped.boughtFrom(FARM), 100 * 200 / 250.0, 1e-12);
        close("...and the world's", capped.boughtFrom(Trade.WORLD), 100 * 50 / 250.0, 1e-12);
        close("...and nothing from the fleet's maker: the vans are not covered", capped.boughtFrom(LORRIES), 0, 0);

        SupplierCredit part = clearing(1_000, 240);
        close("a till short by less than the limit takes exactly what it is short", part.boughtTotal(), 280 - 240, 1e-12);

        SupplierCredit broke = clearing(1_000, -50);
        close("a till in overdraft takes the covered goods' bill and no more: the fleet is paid from the till",
                broke.boughtTotal(), 250, 1e-12);

        SupplierCredit shut = new SupplierCredit(EnumSet.of(Good.MEAT));
        shut.note(FARM, Good.MEAT, 200);
        shut.close();
        close("nothing is noted outside a clearing", shut.boughtTotal(), 0, 0);
        close("a limit that is not a finite positive sum opens as nothing", shut.open(Double.NaN, 0), 0, 0);

        SupplierCredit c = clearing(100, 150);
        c.strike();
        close("the strike: what it bought on credit is owed", c.owedTotal(), 100, 1e-12);
        assertTrue("...nothing is repaid yet, and nothing is bought since", c.repaidTotal() == 0 && c.boughtTotal() == 0);
        c.open(100, 260);
        c.note(FARM, Good.MEAT, 200);
        c.note(Trade.WORLD, Good.GRAINS, 80);
        c.close();
        c.strike();
        close("the strike after repays it, supplier by supplier", c.repaidTo(FARM), 80, 1e-12);
        close("...and owes the new month's", c.owedTotal(), 20, 1e-12);

        Map<String, Double> extras = new LinkedHashMap<>();
        c.save(extras, "sc.");
        SupplierCredit back = new SupplierCredit(EnumSet.of(Good.MEAT, Good.GRAINS));
        back.restore(extras, "sc.");
        assertTrue("saved and restored, every supplier's sum comes back",
                back.owedTo(FARM) == c.owedTo(FARM) && back.owedTo(Trade.WORLD) == c.owedTo(Trade.WORLD)
                        && back.repaidTo(FARM) == c.repaidTo(FARM) && back.repaidTo(Trade.WORLD) == c.repaidTo(Trade.WORLD)
                        && back.getLimit() == c.getLimit());
        SupplierCredit old = new SupplierCredit(EnumSet.of(Good.MEAT));
        old.restore(new LinkedHashMap<>(), "sc.");
        assertTrue("...and a save from before 0.7.44 owes nothing", old.owedTotal() == 0 && old.boughtTotal() == 0);
        back.redenominate(.01);
        close("a reform moves what it owes with the unit", back.owedTotal(), c.owedTotal() * .01, 1e-15);
    }

    /* ============================ 2-6. IN A CITY ============================ */

    /**
     * A town with households, shops, a bank and the food plants: founded and stood at once, no month played (FS9; it was played
     * two years, BondCheck's, without its second branch). Farms stand with it, so there is a supplier in the city to be owed.
     */
    static Game city(Path root, String name) {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve(name + "-no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            g.setCashForTest(Founding.WEALTHY_CASH);
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplateByName("House"), 300, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 12, true);
            b.addStack(b.getTemplateByName("Bakery"), 3, true);
            b.addStack(b.getTemplateByName("Mixed Farm"), 3, true);
            b.addStack(b.getTemplateByName("Construction Depot"), 3, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 2, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.setAutoSubsidised(Sectors.INDUSTRY, true);
            g.getBusinessInvestment().holdSector(Sectors.RETAIL);
        });
        return g;
    }

    /** The most months the town is played for its shops to expect a sale: a bound, not a count - the fixture fails when it passes. */
    static final int WARM_AT_MOST = 12;

    static int closedMonths, playedMonths;

    static MoneyAudit.Result play(Game g) {
        quietly(g::toggleNextMonth);
        MoneyAudit.Result r = g.getLastMoneyAudit();
        playedMonths++;
        if (BondCheck.closes(r)) closedMonths++;
        else out.printf("   m%d: residual %.4f%n", g.getMonth(), r.residual);
        return r;
    }

    /** What the shops bought of the shelf's goods in the month just played, in money. */
    static double shelfBought(ham.citybuildersim.sectors.Retail shops) {
        double value = 0;
        for (Good gd : ham.citybuildersim.sectors.Retail.SHELF) {
            Sector.Split s = shops.pending().bought.get(gd);
            if (s != null) value += s.total();
        }
        return value;
    }

    static void inACity(Path root) throws Exception {
        out.println("\n--- in a city: a grocer its till and its lender will not stock restocks on its suppliers' credit ---");

        Game g = city(root, "credit");
        EconomyManager em = g.getEconomyManager();
        BusinessDebtManager credit = em.getBusinessDebtManager();
        Sectors sectors = g.getSectors();
        String R = Sectors.RETAIL;
        ham.citybuildersim.sectors.Retail shops = sectors.retail();
        SupplierCredit ledger = shops.supplierCredit();

        /*
         * PLAYED UNTIL THE SHOPS EXPECT TO SELL, AND NO LONGER (FS9). What
         * the suppliers would wait for is read off the shops' expectation
         * (Retail.expectedBaskets(): what the households asked for at the last
         * sale, up to the coverage times the operating rate), and a town stood
         * at once has had no sale and runs its shops at nought: both come over
         * the months of its first sales, and neither has a door to set. So the
         * town is played only until they do, and this fails as a fixture if
         * they have not by WARM_AT_MOST. Every later month's order is set by
         * hand below.
         */
        int warm = 0;
        while (!(shops.expectedBaskets() > 0) && warm < WARM_AT_MOST) { play(g); warm++; }
        out.printf("   the shops expect to sell after %d month(s) of the town's first sales%n", warm);
        assertTrue("fixture: the first sales were asked for and the shops run, so the shops expect to sell", shops.expectedBaskets() > 0);

        /*
         * 5 FIRST: A TILL THAT COVERS ITS STOCK. The shelf emptied between
         * months, so the month's order is the whole shelf, and the till the
         * city's founding purse - more than any order: the same month the trap
         * is, with the till the one thing that differs.
         */
        shops.setStoreInventory(0);
        em.setSectorCash(R, Founding.WEALTHY_CASH);
        play(g);
        assertTrue("fixture: the shops ordered stock this month", shops.getOrderValue() > 0);
        assertTrue("fixture: and their suppliers would have waited for some of it, so none was taken only because the till covered it",
                ledger.getLimit() > 0);
        close("a grocer whose till covers its stock buys none of it on credit", ledger.boughtTotal(), 0, 0);
        play(g);
        close("...and owes its suppliers nothing at the strike", ledger.owedTotal(), 0, 0);

        /*
         * THE TRAP, CAUSED: owing past the shortfall desk's ceiling (a claim
         * taken on between months, its quarter read with it, as BondCheck's
         * 5d does), the shelf emptied and the till with it - less than empty
         * by the takings the strike is about to bank, so the month starts
         * with nothing from the sale before. Its sale this month sells
         * nothing, so the till has nothing to restock with - the month 0.7.43
         * left empty-shelved until a loan came. All of it set between months
         * (FS9): the till first, the lender's reading of it refreshed, the
         * claim sized on what it reads, and no month played between the claim
         * and its quarter.
         */
        em.setSectorCash(R, -shops.pending().revenue());
        em.pushBalanceSheetInputs();
        em.refreshCreditAssets();
        credit.issueLoan(R, Math.max(0, 1.2 * credit.getAssets(R) - credit.getPrincipal(R)), g.getMonth());
        for (int q = 0; q < BusinessDebtManager.STATEMENT_MONTHS; q++) {
            credit.recordStatement(R, credit.getPrincipal(R), credit.getPrincipal(R) / 1.2);
        }
        shops.setStoreInventory(0);
        MoneyAudit.Result r = play(g);
        double limit = ledger.getLimit(), budget = shops.getPurchaseBudget(), bought = shelfBought(shops);
        double onCredit = ledger.boughtTotal();
        out.printf("   the empty shop: could pay for $%,.1fk, its suppliers would wait for $%,.1fk; bought $%,.1fk of stock,"
                + " $%,.1fk of it on credit%n", budget, limit, bought, onCredit);
        assertTrue("fixture: the shelf sold nothing this month", shops.getProductsSold() == 0);
        assertTrue("fixture: it expects to sell, so its suppliers would wait for something", limit > 0);
        close("fixture: its till and its lender cover none of its stock - what it can pay for is its suppliers' credit",
                budget - limit, 0, 0);
        assertTrue("it restocks on its suppliers' credit", bought > 0 && onCredit > 0);
        close("...all of it on their credit", onCredit, bought, 1e-9 * bought);
        assertTrue("...and no more than they would wait for", bought <= limit * (1 + 1e-9));
        close("...the credit it took: the stock its till and its lender could not cover, within what they would wait for",
                onCredit, Math.min(limit, Math.max(0, bought - Math.max(0, budget - limit))), 1e-9 * Math.max(1, bought));
        close("what they would wait for: SUPPLIER_CREDIT_MONTHS of the baskets it expects at their landed cost",
                shops.supplierCreditLimit(), ham.citybuildersim.sectors.Retail.SUPPLIER_CREDIT_MONTHS
                        * shops.expectedBaskets() * shops.basketLandedCost(), 1e-9 * Math.max(1, limit));
        close("...the baskets it expects: wanted at the shelf price, up to what the shops can hand over",
                shops.expectedBaskets(), Math.min(shops.getDemandAtPrice(), shops.getStoreCoverage() * shops.getOperatingRate()), 0);
        assertTrue("the month closes", BondCheck.closes(r));
        Map<String, Double> boughtFrom = new LinkedHashMap<>();
        for (String k : ledger.suppliers()) boughtFrom.put(k, ledger.boughtFrom(k));

        /*
         * THE SALE THE CREDIT IS REPAID OUT OF, SET (FS9). The credit was lent
         * for the baskets the shops expect to sell, at what they cost to bring
         * in; what sells is whole baskets, the scarcest good of the delivery
         * first (Retail.basketsOnShelf()), and what a stood town's suppliers
         * fill is lopsided - left as delivered, a third of the expected baskets
         * sold (186 of 551) and the takings were under what it owed, which is a
         * town still filling its larders and not what this section is about. It
         * is about the strike repaying out of the sale of the stock it lent
         * for, so once the stock has reached the shelf (asserted below, on what
         * the order month left there) the shelf is made up to the baskets the
         * credit was lent for, as many as the suppliers would wait for.
         */
        long onShelf = shops.getStoreInventory();
        shops.setStoreInventory((int) Math.ceil(ham.citybuildersim.sectors.Retail.SUPPLIER_CREDIT_MONTHS * shops.expectedBaskets()));

        /* ---------------------------- 3. AT THE STRIKE ---------------------------- */
        out.println("\n--- at the strike: it owes its suppliers, on both sides' books, and the world's share crosses the audit ---");
        r = play(g);
        SectorBooks books = g.getSectorBooks();
        close("the strike owes its suppliers what it bought on their credit", ledger.owedTotal(), onCredit, 1e-9 * onCredit);
        close("...on its balance sheet, a current liability", shops.getBalanceSheet().getTradePayables(), ledger.owedTotal(), 0);
        close("...and on its books", books.get(R).tradePayables(), ledger.owedTotal(), 0);
        close("...whose balance sheet still balances", books.get(R).totalAssets(),
                books.get(R).totalLiabilities() + books.get(R).equity(), 1e-6);
        close("its till took the credit at the strike: its cash flow's line", books.get(R).tradeCredit(),
                ledger.takenTotal() - ledger.repaidTotal(), 1e-9);
        double worstSupplier = 0, worstGap = 0, local = 0;
        for (String k : ledger.suppliers()) {
            Sector seller = sectors.byKey(k);
            if (seller == null) continue;
            local += ledger.owedTo(k);
            worstSupplier = Math.max(worstSupplier, Math.abs(seller.getTradeReceivable() - ledger.owedTo(k))
                    + Math.abs(books.get(k).tradeReceivables() - ledger.owedTo(k))
                    + Math.abs(books.get(k).tradeCredit() - (ledger.repaidTo(k) - ledger.owedTo(k))));
        }
        for (String k : Sectors.KEYS) worstGap = Math.max(worstGap, Math.abs(books.get(k).unexplained()));
        out.printf("   owed $%,.1fk: $%,.1fk to the city's own suppliers, $%,.1fk to the world%n",
                ledger.owedTotal(), local, ledger.owedTo(Trade.WORLD));
        assertTrue("fixture: it owes the world and somebody in the city", ledger.owedTo(Trade.WORLD) > 0 && local > 0);
        close("each local supplier is owed its share, on its sheet and its books, and paid that much less", worstSupplier, 0, 1e-9);
        close("every sector's cash flow still adds up", worstGap, 0, SectorStatementCheck.BooksAudit.TOLERANCE);
        close("the world's share crossed the audit as a financial inflow, to the cent",
                BondCheck.line(r, "+ " + R + " SupplierCreditAbroad"), ledger.owedTo(Trade.WORLD), .005);
        assertTrue("...in the financial account", r.financialIn >= ledger.owedTo(Trade.WORLD) - 1e-9);
        assertTrue("the month closes", BondCheck.closes(r));
        em.refreshCreditAssets();
        BalanceSheet sheet = shops.getBalanceSheet();
        close("the lender reads its assets net of what it owes its suppliers", credit.getAssets(R),
                sheet.getTotalAssets() - ledger.owedTotal() + em.getForeignAssets(R) + em.getBondAssets(R), 1e-6);
        String farm = null;
        for (String k : ledger.suppliers()) if (sectors.byKey(k) != null && ledger.owedTo(k) > 0) farm = k;
        if (farm != null) {
            BalanceSheet theirs = sectors.byKey(farm).getBalanceSheet();
            close("...and a supplier's with what it is owed, as if it had been paid", credit.getAssets(farm),
                    theirs.getTotalAssets() + em.getForeignAssets(farm) + em.getBondAssets(farm), 1e-6);
            close("...which is on that supplier's sheet", theirs.getTradeReceivables(), ledger.owedTo(farm), 0);
        }
        long sold = shops.getProductsSold();
        out.printf("   the month after: sold %,d baskets of the %,.0f it expected%n", sold, shops.expectedBaskets());
        assertTrue("the stock it bought on credit reached the shelf, and sold", onShelf > 0 && sold > 0);

        /* ---------------------------- 6. SAVED AND LOADED ---------------------------- */
        Map<String, Double> owedThen = new LinkedHashMap<>(), boughtThen = new LinkedHashMap<>();
        for (String k : ledger.suppliers()) { owedThen.put(k, ledger.owedTo(k)); boughtThen.put(k, ledger.boughtFrom(k)); }
        boolean[] saved = { false };
        quietly(() -> saved[0] = g.saveGame(1, "supplier credit").ok);
        Game reloaded = new Game(new GameFiles(root.resolve("credit"), root.resolve("credit-no-legacy")));
        quietly(() -> reloaded.loadGameSave(1));
        SupplierCredit back = reloaded.getSectors().retail().supplierCredit();
        boolean same = saved[0];
        for (String k : owedThen.keySet()) {
            same &= back.owedTo(k) == owedThen.get(k) && back.boughtFrom(k) == boughtThen.get(k);
        }
        assertTrue("saved and loaded, what it owes each supplier and what it bought on credit since come back", same
                && back.owedTotal() == ledger.owedTotal() && back.boughtTotal() == ledger.boughtTotal());

        /* ---------------------------- 4. REPAID ---------------------------- */
        out.println("\n--- the strike after: repaid out of the takings of the sale it stocked, and the world's share goes back ---");
        double owedBefore = ledger.owedTotal();
        Map<String, Double> owedBy = new LinkedHashMap<>();
        for (String k : ledger.suppliers()) owedBy.put(k, ledger.owedTo(k));
        r = play(g);
        close("the strike after repays what it owed", ledger.repaidTotal(), owedBefore, 1e-9 * owedBefore);
        double takings = shops.statement().revenue;
        out.printf("   repaid $%,.1fk out of takings of $%,.1fk%n", ledger.repaidTotal(), takings);
        assertTrue("...out of the takings of the sale it stocked, which were more than it", takings > ledger.repaidTotal());
        double worstPaid = 0;
        for (Map.Entry<String, Double> e : owedBy.entrySet()) {
            worstPaid = Math.max(worstPaid, Math.abs(ledger.repaidTo(e.getKey()) - e.getValue()));
            if (sectors.byKey(e.getKey()) == null) continue;
            worstPaid = Math.max(worstPaid, Math.abs(books.get(e.getKey()).tradeCredit()
                    - (ledger.repaidTo(e.getKey()) - ledger.owedTo(e.getKey()))));
        }
        close("every supplier is paid what it was owed, on its books", worstPaid, 0, 1e-9);
        close("the world's repayment crossed the audit as a financial outflow, to the cent",
                BondCheck.line(r, "- " + R + " SupplierCreditRepaidAbroad"), ledger.repaidTo(Trade.WORLD), .005);
        worstGap = 0;
        for (String k : Sectors.KEYS) worstGap = Math.max(worstGap, Math.abs(books.get(k).unexplained()));
        close("every sector's cash flow still adds up", worstGap, 0, SectorStatementCheck.BooksAudit.TOLERANCE);
        assertTrue("the month closes", BondCheck.closes(r));
        assertTrue("every month this harness played closed the audit (" + playedMonths + ")", closedMonths == playedMonths);
    }
}
