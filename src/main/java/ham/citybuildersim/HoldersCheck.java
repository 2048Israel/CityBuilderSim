package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;

/**
 * Proves who holds the city's own paper (0.7.1): that the households buy it at
 * the settle, are paid on it, sell it back, and are paid when it is bought
 * back - every crossing declared, and every holding exactly where the paper
 * says it is. Not part of the game.
 *
 * WHY THIS EXISTS. Jerus: "yes households should be able to hold." Until
 * 0.7.1 the commercial bank held every dollar of the city's paper, so a bond
 * was a loan from the city's own bank with extra steps. Now a piece of paper
 * carries what the households and the central bank hold of it, and the
 * households carry their paper as a fourth asset. Two books that must agree
 * to the dollar, and a set of crossings - households are outside the money
 * audit's pools - each of which is a dollar from nowhere if it is not
 * declared. So each is caused and asserted:
 *
 *   1. At the settle the households take the share the dials give them of an
 *      issue yielding well above the deposit rate; their savings fall by
 *      exactly what they paid, the bank pays exactly the rest, the paper's
 *      household share is the cells' paper summed, and the audit closes.
 *   2. The next month's coupon reaches them in their share, the bank's in
 *      its, and the treasury's books carry the whole of it.
 *   3. A household short of money sells its paper before its shares, and its
 *      shares only once the paper is gone.
 *   4. A household selling after the curve has risen gets less than face,
 *      and the bank books the gain against what it carries the paper at.
 *   5. A buyback pays every holder its share - the bank, the households, the
 *      central bank - and the next month declares what left the pools.
 *   6. Save and reload, and every holding is exactly where it was.
 *   7. A save from before the holders (no cell slot, no fields on the paper)
 *      loads with the bank holding everything, and runs.
 *   8. A dollar bond bought back is money leaving the country: none of the
 *      price reaches the bank or the households, and the next month declares
 *      all of it abroad (until 0.7.1 it left the treasury for nowhere).
 *
 * Each fixture causes its condition rather than finding a city in it.
 *
 * @author Jerus
 */
public class HoldersCheck {

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
            out.printf("%-72s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            out.printf("%-72s OK%n", label);
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

    static double savings(Game g) { return g.getHouseholdBalance().totalSavings(); }

    /** The two books of the households' paper agree: the cells, and the paper. */
    static boolean booksAgree(Game g) {
        double cells = g.getHouseholdBalance().totalPaper();
        double paper = g.getDebtManager().householdPrincipal();
        return Math.abs(cells - paper) <= 1e-9 * Math.max(1, paper);
    }

    static int closedMonths, brokenMonths;

    /** A month, held to the audit. */
    static MoneyAudit.Result play(Game g) {
        quietly(g::toggleNextMonth);
        MoneyAudit.Result r = g.getLastMoneyAudit();
        if (Math.abs(r.residual) <= .01 || r.relative() <= 1e-7) closedMonths++;
        else {
            brokenMonths++;
            out.printf("   m%d: residual %.4f%n", g.getMonth(), r.residual);
        }
        return r;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        GameFiles files = GameFiles.scratch("holderscheck");
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
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(60);
        });
        HouseholdBalance hb = city.getHouseholdBalance();
        DebtManager ledger = city.getDebtManager();
        Bank bank = city.getBank();
        CentralBank cb = city.getCentralBank();

        /* ================= 1. at the settle ================= */
        out.println("--- 1. at the settle the households take their share, and the bank the rest ---");

        double spare = hb.spareForPaper();
        out.printf("   m%d: households hold $%,.0fk past their cushion; the bank pays savers %.3f%%%n",
                city.getMonth(), spare, bank.depositRate() * 100);
        assertTrue("fixture: households with savings past the cushion", spare > 0);

        // Sized so the dials bind, not the households' purse: at most half an
        // issue is theirs, and this asks for a fifth of what they could pay.
        double ask = Math.max(100, spare * .2);
        quietly(() -> city.handleLongBondLogic(ask, 20, 100));
        Debt bond = null;
        for (Debt d : ledger.getDebt()) if (DebtManager.isTermPaper(d)) bond = d;
        assertTrue("fixture: the treasury sold a twenty-year bond between the presses", bond != null);
        double received = bond.getSettleDue();
        double face = bond.getOustandingPrincipal();
        double share = HouseholdBalance.paperShareAt(bond.getIssueYield(), bank.depositRate());
        out.printf("   it raised $%,.2fk on $%,.0fk of face at %.3f%%, %.3f points over the deposit rate%n",
                received, face, bond.getIssueYield() * 100,
                (bond.getIssueYield() - bank.depositRate()) * 100);
        assertTrue("fixture: its yield is well above the deposit rate",
                bond.getIssueYield() - bank.depositRate() > .01);
        assertTrue("fixture: and the households can pay for their share",
                share * received < spare);

        final double[] around = new double[2];
        city.settleProbeForTest = after -> around[after ? 1 : 0] = savings(city);
        MoneyAudit.Result settled = play(city);
        city.settleProbeForTest = null;
        double paid = city.getHouseholdsBoughtPaper();
        out.printf("   the households paid $%,.2fk for $%,.2fk of face; the bank $%,.2fk%n",
                paid, bond.householdPrincipal(), city.getCityPaperSettled());
        close("the households took the share the dials give an issue this far over the deposit rate",
                paid, share * received, 1e-6);
        assertTrue("...which is the most they take, MAX_HOUSEHOLD_PAPER_SHARE, or the spread times"
                + " HOUSEHOLD_PAPER_APPETITE", share == Math.min(HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE,
                        (bond.getIssueYield() - bank.depositRate()) * HouseholdBalance.HOUSEHOLD_PAPER_APPETITE));
        close("their savings fell by exactly what they paid", around[0] - around[1], paid, 1e-6);
        close("...for the face the issue's price buys: face times cash over received",
                bond.householdPrincipal(), face * paid / received, 1e-6);
        close("the bank paid exactly the rest", city.getCityPaperSettled(), received - paid, 1e-6);
        close("...and holds exactly the rest of the face", bond.bankPrincipal(),
                face - bond.householdPrincipal(), 1e-9);
        close("the paper's household share is the sum of every cell's paper",
                hb.totalPaper(), ledger.householdPrincipal(), 1e-9);
        close("...and the treasury is owed nothing more for it", bond.getSettleDue(), 0, 0);
        assertTrue("the audit closed on the settle month", Math.abs(settled.residual) <= .01);

        /* ================= 2. the coupon ================= */
        out.println("\n--- 2. the coupon reaches each holder in its share ---");
        double hhShare = bond.householdPrincipal() / face, bankShare = bond.bankPrincipal() / face;
        double coupon = bond.getMonthlyInterestExpense();
        double incomeBefore = hb.totalPaperIncome();
        play(city);
        close("the households were paid their share of the coupon", city.getCouponsToHouseholds(),
                coupon * hhShare, 1e-6);
        close("...into their savings, as investment income", hb.totalPaperIncome(), coupon * hhShare, 1e-6);
        close("the bank's share is on the month's interest bill, which it takes at the next settle",
                city.getEconomyManager().getInterestAccrued(), coupon * bankShare, 1e-6);
        assertTrue("(fixture: the paper income was cleared and re-paid, not carried)", incomeBefore >= 0);
        assertTrue("the two books still agree", booksAgree(city));

        /* ================= 3. the waterfall ================= */
        out.println("\n--- 3. a household short of money sells its paper before its shares ---");
        WorkingHousehold family = new WorkingHousehold(FamilyStructure.COUPLE, PayTier.SKILLED);
        family.households = 1;
        family.paper = 40;
        family.shares[0] = 20;
        final StringBuilder order = new StringBuilder();
        final double ratio = .9;
        Household.Liquidity desk = new Household.Liquidity() {
            @Override public double sell(Household c, double needPer) {
                order.append('S');
                double cash = Math.min(needPer, c.shares[0] * 2);
                c.shares[0] -= cash / 2;
                return cash;
            }
            @Override public double sellPaper(Household c, double needPer) {
                order.append('P');
                double cash = Math.min(needPer, c.paper * ratio);
                c.paper -= cash / ratio;
                return cash;
            }
        };
        family.settle(100, 30, 0, 90, 1, .03, desk, 0);        // short by 20: the paper covers it
        assertTrue("short by less than its paper is worth, it sells paper and nothing else",
                order.toString().equals("P") && family.shares[0] == 20);
        close("...for exactly what it was short", family.paperSold(), 20, 1e-9);
        order.setLength(0);
        family.settle(100, 30, 0, 110, 1, .03, desk, 0);       // short by 40: more than the paper left
        assertTrue("short by more, it sells all of its paper first and its shares after",
                order.toString().equals("PS") && family.paper() <= 1e-12 && family.shares[0] < 20);
        close("...the shares covering what the paper could not", family.sold(),
                40 - (40 - 20 / ratio) * ratio, 1e-9);

        /* ================= 4. selling after the curve rose ================= */
        out.println("\n--- 4. a household selling after the curve has risen gets less than face ---");
        ledger.setPolicyRate(ledger.getPolicyRate() + .04);
        play(city);
        double paperRatio = hb.getPaperRatio();
        out.printf("   the dial four points up: the month's ratio for the households' paper is %.4f of face%n",
                paperRatio);
        assertTrue("the month's ratio - their book at the curve over its face - is under face, the rate"
                + " having risen", paperRatio < 1);
        double heldBefore = hb.totalPaper(), savingsBefore = savings(city);
        double deciding = 0;     // what the households who decide what to do with their money hold
        for (Household c : hb.cells()) if (c.households() >= .5 && c.canInvest()) deciding += c.totalPaper();
        double bankBook = bank.getCityBook(), bankCash = bank.getCash(), bankGains = bank.getPaperGains();
        double unearnedPerFace = bond.getDiscountLeft() / bond.getOustandingPrincipal();
        double raised = hb.sellPaperForSpread(0, bank.depositRate());     // the spread made to vanish
        double sold = heldBefore - hb.totalPaper();
        out.printf("   they sold $%,.2fk of face for $%,.2fk%n", sold, raised);
        assertTrue("fixture: they sold some", sold > 0);
        close("...a little of it: HOME_SPEED of what the households who decide hold (a prisoner's stays put)",
                sold, deciding * OutwardInvestment.HOME_SPEED, 1e-9);
        close("...for less than face: its face at the month's ratio", raised, sold * paperRatio, 1e-9);
        close("...into their savings", savings(city) - savingsBefore, raised, 1e-9);
        close("the bank paid it", bankCash - bank.getCash(), raised, 1e-9);
        close("...its book rose by the face", bank.getCityBook() - bankBook, sold, 1e-9);
        close("...and it booked the gain against what it carries the paper at",
                bank.getPaperGains() - bankGains, sold - sold * unearnedPerFace - raised, 1e-9);
        assertTrue("...a gain", bank.getPaperGains() - bankGains > 0);
        assertTrue("the two books still agree", booksAgree(city));
        play(city);

        /* ================= 5. a buyback ================= */
        out.println("\n--- 5. a buyback pays every holder its share ---");
        cb.setTargetShare(.30);
        play(city);
        assertTrue("fixture: three holders - the households, the central bank and the bank",
                bond.householdPrincipal() > 0 && bond.centralBankPrincipal() > 0 && bond.bankPrincipal() > 0);
        double principal = bond.getOustandingPrincipal();
        double hhFace = bond.householdPrincipal(), cbFace = bond.centralBankPrincipal();
        double bFace = bond.bankPrincipal(), bUnearned = bond.unaccretedOn(bFace);
        city.setCashForTest(city.getCash() + 2 * principal);
        double price = city.quoteRepurchase(bond);
        double sBefore = savings(city), bCash = bank.getCash(), bEquity = bank.equity();
        double cbHeld = cb.getPaperHeld(), m0 = cb.m0();
        double paidBack = city.repurchaseDebt(bond);
        close("fixture: the buyback was at the quoted price", paidBack, price, 1e-9);
        close("the households' share of the price went to their savings", savings(city) - sBefore,
                price * hhFace / principal, 1e-6);
        close("...and their paper is gone with the bond", hb.totalPaper(), 0, 1e-9);
        close("the bank's share to its cash", bank.getCash() - bCash, price * bFace / principal, 1e-6);
        close("...its gain against what it carried its face at",
                bank.equity() - bEquity, price * bFace / principal - (bFace - bUnearned), 1e-6);
        close("the central bank's face came off its book", cbHeld - cb.getPaperHeld(), cbFace, 1e-9);
        close("...and its share of the price waits for the month to destroy it", cb.getRedemptionDue(),
                price * cbFace / principal, 1e-6);
        close("...held in the treasury's pool until then, with the households'",
                city.getBuybackUnsettled(), price * (hhFace + cbFace) / principal, 1e-6);
        MoneyAudit.Result after = play(city);
        close("the next month declares what the households were paid", city.getBuybackToHouseholds(),
                price * hhFace / principal, 1e-6);
        close("...and the central bank destroys its share", cb.getBoughtBack(), price * cbFace / principal, 1e-6);
        close("...so nothing is carried any more", city.getBuybackUnsettled(), 0, 1e-9);
        assertTrue("the audit closes on it", Math.abs(after.residual) <= .01);
        assertTrue("...and M0 moved by exactly the money made, the redemption among it",
                Math.abs((cb.m0() - m0) - (cb.getIssued() - cb.getRetired())) <= .005);
        close("the central bank kept nothing: its gain against face is in the month's profit",
                cb.equity() - cb.getVault(), cb.getRemittanceDue() - cb.getLossCarried(), .01);

        /* ================= 6. the save ================= */
        out.println("\n--- 6. save and reload, and every holding is where it was ---");
        ledger.setPolicyRate(ledger.getPolicyRate() - .04);
        quietly(() -> city.handleLongBondLogic(ask, 30, 100));
        play(city);                                        // the settle: households take theirs
        play(city);                                        // and the central bank buys at the top of the next
        assertTrue("fixture: paper held by all three again",
                ledger.householdPrincipal() > 0 && ledger.centralBankPrincipal() > 0 && ledger.bankPrincipal() > 0);
        quietly(() -> city.handleLongBondLogic(ask, 10, 100));   // and one still owed for
        final Game[] back = new Game[1];
        quietly(() -> {
            city.saveGame(3, "the holders");
            back[0] = new Game(files);
            back[0].loadGameSave(3);
        });
        Game twin = back[0];
        double[] was = hb.toCellSaveArray(), is = twin.getHouseholdBalance().toCellSaveArray();
        boolean cellsSame = was.length == is.length;
        for (int i = 0; cellsSame && i < was.length; i++) cellsSame = was[i] == is[i];
        assertTrue("every cell's paper, and everything else in the cell, came back exactly", cellsSame);
        boolean paperSame = ledger.getDebt().size() == twin.getDebtManager().getDebt().size();
        for (int i = 0; paperSame && i < ledger.getDebt().size(); i++) {
            Debt a = ledger.getDebt().get(i), b = twin.getDebtManager().getDebt().get(i);
            paperSame = a.householdPrincipal() == b.householdPrincipal()
                    && a.centralBankPrincipal() == b.centralBankPrincipal()
                    && a.getSettleDue() == b.getSettleDue() && a.getIssueYield() == b.getIssueYield()
                    && a.getIssueDiscount() == b.getIssueDiscount() && a.getDiscountLeft() == b.getDiscountLeft();
        }
        assertTrue("every piece of paper's holders, what it is owed for and its discount came back",
                paperSame);
        close("...the households' paper ratio", twin.getHouseholdBalance().getPaperRatio(), hb.getPaperRatio(), 0);
        close("...the central bank's book", twin.getCentralBank().getPaperHeld(), cb.getPaperHeld(), 0);
        close("...and the bank's unearned discount, re-derived from the paper",
                twin.getBank().getUnearnedDiscount(), bank.getUnearnedDiscount(), 1e-9);
        assertTrue("...and the two books agree in the reloaded city", booksAgree(twin));
        MoneyAudit.Result mine = play(city), theirs = play(twin);
        close("a month on, both settle the paper still owed for the same way",
                twin.getHouseholdsBoughtPaper(), city.getHouseholdsBoughtPaper(), 1e-9);
        close("...and pay the households the same coupons", twin.getCouponsToHouseholds(),
                city.getCouponsToHouseholds(), 1e-9);
        assertTrue("...and both months close", Math.abs(mine.residual) <= .01 && Math.abs(theirs.residual) <= .01);

        /* ================= 7. an old save ================= */
        out.println("\n--- 7. a save from before the holders loads with the bank holding everything ---");
        quietly(() -> city.saveGame(4, "before the holders"));
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                Files.readString(files.saveFile(4))).getAsJsonObject();
        // The cell array two slots shorter a cell - the paper was the last
        // slot, and the businesses' bonds have come after it since 0.7.12.
        com.google.gson.JsonArray keys = json.getAsJsonArray("householdCellKeys");
        com.google.gson.JsonArray cells = json.getAsJsonArray("householdCells");
        assertTrue("fixture: the save carries the cells", keys != null && cells != null);
        int n = keys.size(), slots = (cells.size() - 3) / n;
        assertTrue("fixture: at today's width", slots == HouseholdBalance.CELL_SLOTS);
        com.google.gson.JsonArray shorter = new com.google.gson.JsonArray();
        for (int c = 0; c < n; c++) {
            for (int s = 0; s < slots - 2; s++) shorter.add(cells.get(c * slots + s));
        }
        for (int t = n * slots; t < cells.size(); t++) shorter.add(cells.get(t));
        json.add("householdCells", shorter);
        for (com.google.gson.JsonElement e : json.getAsJsonArray("debts")) {
            com.google.gson.JsonObject d = e.getAsJsonObject();
            for (String k : new String[] {"householdPrincipal", "centralBankPrincipal", "settleDue",
                    "issueYield", "issueDiscount", "discountLeft"}) d.remove(k);
        }
        for (String k : new String[] {"qeTargetShare", "householdPaperRatio",
                "buybackToHouseholdsUnsettled", "buybackAbroadUnsettled"}) json.remove(k);
        // ...and the central bank's array as 0.7.0 wrote it, without the tail.
        com.google.gson.JsonArray cbArray = json.getAsJsonArray("centralBank");
        com.google.gson.JsonArray cbOld = new com.google.gson.JsonArray();
        for (int i = 0; i < 13 + CentralBank.REVENUE_MONTHS; i++) cbOld.add(cbArray.get(i));
        json.add("centralBank", cbOld);
        Files.writeString(files.saveFile(4), json.toString());
        quietly(() -> {
            back[0] = new Game(files);
            back[0].loadGameSave(4);
        });
        Game old = back[0];
        assertTrue("it loads", old.getLoadFailure() == null && old.getMonth() > 1);
        close("the households hold none of the city's paper", old.getHouseholdBalance().totalPaper(), 0, 0);
        close("...nor the central bank", old.getDebtManager().centralBankPrincipal(), 0, 0);
        close("...so the bank holds everything", old.getDebtManager().bankPrincipal(),
                old.getDebtManager().getDomesticPrincipal(), 1e-9);
        close("...and its book says so", old.getBank().getCityBook(), old.getDebtManager().getDomesticPrincipal(), 1e-6);
        close("the dial reads nothing", old.getCentralBank().getTargetShare(), 0, 0);
        int brokenBefore = brokenMonths;
        for (int i = 0; i < 3; i++) play(old);
        assertTrue("...and it runs, the audit closing every month", brokenMonths == brokenBefore);
        assertTrue("...with the two books agreeing", booksAgree(old));

        /* ================= 8. a dollar bond bought back ================= */
        out.println("\n--- 8. a dollar bond bought back is money leaving the country ---");
        quietly(() -> city.handleForeignLogic("Term", ask, 20, 100, false));
        Debt dollars = null;
        for (Debt d : ledger.getDebt()) if (d.isForeign()) dollars = d;
        assertTrue("fixture: the treasury sold a twenty-year dollar bond", dollars != null);
        play(city);
        assertTrue("fixture: ...still owed a month on", ledger.getDebt().contains(dollars));
        city.setCashForTest(city.getCash() + 2 * ledger.getForeignPrincipal());
        double dollarPrice = city.quoteRepurchase(dollars);
        double cashBefore = city.getCash(), bankCash8 = bank.getCash(), sBefore8 = savings(city);
        double carried = city.getBuybackUnsettled();
        double paid8 = city.repurchaseDebt(dollars);
        close("fixture: bought back at the quoted price", paid8, dollarPrice, 1e-9);
        assertTrue("fixture: ...which cost something", paid8 > 0);
        close("the treasury paid it", cashBefore - city.getCash(), dollarPrice, 1e-6);
        close("...and owes the world nothing on it", ledger.getForeignPrincipal(), 0, 1e-9);
        close("none of the price went to the bank", bank.getCash() - bankCash8, 0, 0);
        close("...nor to the households", savings(city) - sBefore8, 0, 0);
        close("the treasury's pool carries it until the month declares it",
                city.getBuybackUnsettled() - carried, dollarPrice, 1e-6);
        MoneyAudit.Result abroad = play(city);
        close("the next month declares the whole price leaving the country", city.getBuybackAbroad(),
                dollarPrice, 1e-6);
        close("...so nothing is carried any more", city.getBuybackUnsettled(), 0, 1e-9);
        assertTrue("the audit closes on it", Math.abs(abroad.residual) <= .01);

        assertTrue(String.format("every month this harness played closed the audit (%d)",
                closedMonths + brokenMonths), brokenMonths == 0);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails);
    }
}
