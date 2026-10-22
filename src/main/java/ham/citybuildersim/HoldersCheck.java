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
 *   9. The holdings dial at the whole of the paper (0.7.15, Jerus: "central
 *      bank bond holding can ve 100% if one wants"): the central bank buys
 *      the bank's term paper first, and only once the bank has none left
 *      the households', at the curve's market value, their face onto its
 *      book and the price made for them - the audit closing and M0 moving by
 *      exactly the money made - until it holds all of it; a save and load
 *      in the middle keeps the dial, the holdings and the pace, and both
 *      cities buy the same from the households the month after.
 *
 * 10. The businesses are a fourth holder (CD2, slice 4, D33): the auction
 *     allots them a share of the sale; at the settle each sector pays its
 *     share out of its own till, in proportion to its own bid at the stop and
 *     never past the till, and the bank takes what a till could not pay; the
 *     coupon, the principal and a buyback's price arrive in the tills; they
 *     sell to the bank's desk when the spread has gone, when a sector
 *     borrows and when it wants the money; the central bank reaches them last
 *     when its dial is the whole of the paper; and at every step the sectors'
 *     paper is the paper's businesses' share, exactly, the audit closing
 *     (a till and the bank are both pools, so nothing is declared), through a
 *     save and load, and an old save loads with none.
 *
 * 11. The world is a fifth holder (CD2, slice 5, D7; star 2): the auction
 *     allots it a share of the sale by CapitalFlows' rule, and at the settle
 *     it pays from abroad - a financial inflow the audit declares, the bank's
 *     settle shrinking by it; its coupon leaves as income and its principal
 *     (and a buyback's price) as a financial outflow, both declared; it
 *     leaves with its money, a share of the paper sold to the bank's desk when
 *     the hot money departs and PANIC_EXIT of it in a run, when it also bids
 *     nothing; the central bank reaches it last when its dial is the whole of
 *     the paper; and at every step CapitalFlows' paper is the paper's world
 *     share, exactly, the audit closing, through a save and load, and an old
 *     save loads with none.
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
            /*
             * THE TREASURY AND THE GROUND THIS FIXTURE WAS WRITTEN AGAINST
             * (0.7.17), as the suite's other founding fixtures have been given
             * since 0.7.10: a city founds on D$100M, and its three hundred
             * houses, four depots and coal plant came back NO_LAND, so it was
             * a town of fourteen people around a bank, a shop and a bakery.
             * Since every building gets the crew it can use (BuildingManager,
             * EVERY BUILDING GETS THE CREW IT CAN USE) that town's month after
             * the issue had a household short of money, which sold a sliver of
             * its paper to the bank's desk - $0.0116k - and the shares the
             * coupon is paid on moved under the reading (section 2), and in
             * section 9 the bank held a sliver again, the central bank's next
             * step took the households' last half with it, and the middle the
             * save is made in never came. Given the money and the ground, the
             * city is the one the fixture describes.
             */
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
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

        // Sized so the rule binds, not the households' purse: they bid a fifth
        // of the issue for each point over the deposit rate (never more than
        // the issue), and this asks for a fifth of what they could pay.
        double ask = Math.max(100, spare * .2);
        quietly(() -> city.handleLongBondLogic(ask, 20, 100));
        Debt bond = null;
        for (Debt d : ledger.getDebt()) if (DebtManager.isTermPaper(d)) bond = d;
        assertTrue("fixture: the treasury sold a twenty-year bond between the presses", bond != null);
        double received = bond.getSettleDue();
        double face = bond.getOustandingPrincipal();
        // What the auction allotted them (CD2, D32): their rule at the stop, the
        // deposit rate as the sale found it, up to the issue. Read before the
        // settle, which clears it.
        double allotFace = bond.allotHouseholdsFace(), allotCash = bond.allotHouseholdsCash();
        double share = allotCash / received;
        double bid = Math.min(face, HouseholdBalance.HOUSEHOLD_PAPER_APPETITE
                * Math.max(0, bond.getIssueYield() - bank.depositRate()) * face);
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
        close("the households took what the auction allotted them, their rule at the clearing yield",
                paid, share * received, 1e-6);
        close("...which is HOUSEHOLD_PAPER_APPETITE of the issue for each unit of spread over the deposit rate, never"
                + " more than the issue (D32: no half-of-the-issue cap)", allotFace, bid, 1e-6);
        close("their savings fell by exactly what they paid", around[0] - around[1], paid, 1e-6);
        close("...for the face the issue's price buys: face times cash over received",
                bond.householdPrincipal(), face * paid / received, 1e-6);
        // ...the households and, since CD2 (slice 4), the businesses and (slice 5) the world: the sector tills pay what
        // the auction allotted them and the world pays from abroad, and the bank the rest of all three. Sections 10 and
        // 11 hold the businesses' and the world's part to account.
        double firmsPaid = city.getFirmsBoughtPaper(), worldPaid = city.getWorldBoughtPaper();
        close("the bank paid exactly the rest, after the households, the businesses and the world", city.getCityPaperSettled(),
                received - paid - firmsPaid - worldPaid, 1e-6);
        close("...and holds exactly the rest of the face", bond.bankPrincipal(),
                face - bond.householdPrincipal() - bond.firmPrincipal() - bond.worldPrincipal(), 1e-9);
        close("the paper's household share is the sum of every cell's paper",
                hb.totalPaper(), ledger.householdPrincipal(), 1e-9);
        close("...and the treasury is owed nothing more for it", bond.getSettleDue(), 0, 0);
        assertTrue("the audit closed on the settle month", Math.abs(settled.residual) <= MoneyAudit.tolerance(settled.moved()));

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
        assertTrue("the audit closes on it", Math.abs(after.residual) <= MoneyAudit.tolerance(after.moved()));
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
        assertTrue("...and both months close", Math.abs(mine.residual) <= MoneyAudit.tolerance(mine.moved())
                && Math.abs(theirs.residual) <= MoneyAudit.tolerance(theirs.moved()));

        /* ================= 7. an old save ================= */
        out.println("\n--- 7. a save from before the holders loads with the bank holding everything ---");
        quietly(() -> city.saveGame(4, "before the holders"));
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                Files.readString(files.saveFile(4))).getAsJsonObject();
        // The cell array at the width it had before the paper was appended -
        // CELL_SLOTS_BEFORE_PAPER, the model's own mark. "Two slots shorter",
        // as this read until 0.7.46, was that width while the paper and the
        // bonds were the last two; past 0.7.43's groceries it was a width no
        // build wrote, the cells were refused whole and the households held
        // nothing for that reason - until 0.7.46's slot made it 0.7.45's own
        // width, paper and all.
        com.google.gson.JsonArray keys = json.getAsJsonArray("householdCellKeys");
        com.google.gson.JsonArray cells = json.getAsJsonArray("householdCells");
        assertTrue("fixture: the save carries the cells", keys != null && cells != null);
        int n = keys.size(), slots = (cells.size() - 3) / n;
        assertTrue("fixture: at today's width", slots == HouseholdBalance.CELL_SLOTS);
        com.google.gson.JsonArray shorter = new com.google.gson.JsonArray();
        for (int c = 0; c < n; c++) {
            for (int s = 0; s < HouseholdBalance.CELL_SLOTS_BEFORE_PAPER; s++) shorter.add(cells.get(c * slots + s));
        }
        for (int t = n * slots; t < cells.size(); t++) shorter.add(cells.get(t));
        json.add("householdCells", shorter);
        for (com.google.gson.JsonElement e : json.getAsJsonArray("debts")) {
            com.google.gson.JsonObject d = e.getAsJsonObject();
            for (String k : new String[] {"householdPrincipal", "centralBankPrincipal", "firmPrincipal", "settleDue",
                    "issueYield", "issueDiscount", "discountLeft"}) d.remove(k);
        }
        // ...and the sectors' array as it was before their paper was appended (CD2, slice 4).
        com.google.gson.JsonArray sectorsFull = json.getAsJsonArray("outwardInvestment");
        com.google.gson.JsonArray sectorsOld = new com.google.gson.JsonArray();
        for (int i = 0; i < 7 + 3 * Sectors.KEYS.length; i++) sectorsOld.add(sectorsFull.get(i));
        json.add("outwardInvestment", sectorsOld);
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
        assertTrue("the audit closes on it", Math.abs(abroad.residual) <= MoneyAudit.tolerance(abroad.moved()));

        theWholeBook();
        theBusinesses();
        theWorld();

        assertTrue(String.format("every month this harness played closed the audit (%d)",
                closedMonths + brokenMonths), brokenMonths == 0);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /* ================= 9. the whole of the paper (0.7.15) ================= */

    /** The one piece of term paper a city holds, or null. */
    static Debt termPiece(DebtManager ledger) {
        Debt found = null;
        for (Debt d : ledger.getDebt()) if (DebtManager.isTermPaper(d)) found = d;
        return found;
    }

    static void theWholeBook() throws Exception {
        out.println("\n--- 9. the dial at the whole of the paper buys the households' once the bank's is gone ---");
        GameFiles files = GameFiles.scratch("holderscheck-whole");
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            // The treasury and the ground it was written against (0.7.17): see main().
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            /*
             * ...AND ITS BUILDERS HELD TO THE FOUR DEPOTS IT WAS FOUNDED WITH
             * (0.7.130, batch T1-C): the section is about the paper, not the
             * builders (BusinessInvestment.holdSector(), "a harness names the
             * sectors its question is not about"). Since a depot the crews'
             * rule would starve is built first (BuildingManager, THE BUILDERS'
             * OWN DEPOTS FIRST) the founding four opened sooner, the builders
             * had ordered six more by the bond's month, the town grew to 1,801
             * people from 1,053, and in the months watched households short of money
             * sold the desk paper - the bank held some again after the central
             * bank had cleared it, and the step took the households' last with
             * it, so the middle never came. Held, the builders order nothing,
             * as they ordered nothing here before (their founding depots on
             * site held them "already building" all sixty months).
             */
            city.getBusinessInvestment().holdSector(Sectors.CONSTRUCTION);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(60);
            /*
             * ...AND NOBODY LEAVING WHILE IT IS WATCHED, RE-CAUSED (0.7.19). A
             * household that leaves sells its city paper to the bank's desk on
             * the way out (HouseholdBalance, THE CITY'S PAPER IS SOLD ON THE
             * WAY OUT), which puts paper back on the bank's book after the
             * central bank has taken it, and moves the households' face by
             * more than the central bank's step. Since 0.7.19 this town has a
             * pay tier in decline through months 60 to 68 - its diploma
             * workers leaving a fraction a month (Migration's decline rule) -
             * and in the section's second month they sold the desk $8.6k of
             * face: the bank ended that month holding paper the central bank
             * had already cleared from it, and every later month was one step
             * out. So the town is played on until no tier is in decline before
             * the bond is written, as the town the section was written against
             * was.
             */
            for (int i = 0; i < 36 && city.getMigration().getLastDecliningShare() > 0; i++) city.simulateMonths(1);
        });
        assertTrue("fixture: no pay tier in the town is in decline, so nobody leaves while the paper is watched",
                city.getMigration().getLastDecliningShare() == 0);
        HouseholdBalance hb = city.getHouseholdBalance();
        DebtManager ledger = city.getDebtManager();
        Bank bank = city.getBank();
        CentralBank cb = city.getCentralBank();
        // One and seven tenths of what the households can pay (CD2, D32): they bid a fifth
        // of the issue for each point over the deposit rate and are limited by their purse,
        // not by half the issue as they were, so they take their whole purse - about 59% of
        // this issue - and the bank the rest. Both hold part, and the households hold more
        // than the first step off them, so the middle this section saves in still comes.
        double ask = Math.max(100, hb.spareForPaper() * 1.7);
        quietly(() -> city.handleLongBondLogic(ask, 20, 100));
        play(city);                                        // the settle: the households take their share
        Debt bond = termPiece(ledger);
        double face = bond.getOustandingPrincipal();
        assertTrue("fixture: one twenty-year bond, the households holding part and the bank the rest",
                bond.householdPrincipal() > 0 && bond.bankPrincipal() > 0 && bond.centralBankPrincipal() == 0
                        && bond.getSettleDue() == 0);
        out.printf("   the households hold $%,.2fk of $%,.2fk, the bank $%,.2fk%n",
                bond.householdPrincipal(), face, bond.bankPrincipal());

        cb.setTargetShare(1.0);
        close("the dial takes 100%, MAX_QE_SHARE", cb.getTargetShare(), CentralBank.MAX_QE_SHARE, 0);
        boolean bankFirst = true;
        int months = 0;
        while (bond.bankPrincipal() > 1e-9 * face && months < 8) {
            play(city);
            months++;
            if (bond.bankPrincipal() > 1e-9 * face) bankFirst &= cb.getBoughtFromHouseholds() == 0;
        }
        out.printf("   the bank's paper gone in %d month(s); the central bank holds %.1f%% of the term paper%n",
                months, ledger.centralBankShareOfTerm() * 100);
        assertTrue("while the bank has term paper to sell, it is all the central bank buys", bankFirst);
        assertTrue("...until it has none: the dial past the bank's holding took all of it",
                bond.bankPrincipal() <= 1e-9 * face && ledger.centralBankShareOfTerm() < 1);

        // A month the step comes from the households alone, struck at the market value the
        // operation will see at the top of the month.
        double term = ledger.termPrincipal();
        double heldBefore = cb.getPaperHeld();
        double move = Math.min(cb.stepFor(term), cb.getTargetShare() * term - heldBefore);
        double fromBank = Math.min(move, bond.bankPrincipal());
        double fromHouseholds = Math.min(move - fromBank, bond.householdPrincipal());
        city.strikeTheCurve();   // the expectations the top will strike first (CD1)
        double expectedPrice = ledger.marketValue(bond) * fromHouseholds / bond.getOustandingPrincipal();
        double hhBefore = bond.householdPrincipal(), cellsBefore = hb.totalPaper();
        double m0Before = cb.m0();
        MoneyAudit.Result r = play(city);
        assertTrue("fixture: the desk bought none of the households' paper that month",
                bank.getPaperBoughtFromHouseholds() == 0);
        assertTrue("fixture: ...and the step came from the households", fromHouseholds > 0);
        close("the central bank took the rest of its step off the households' face",
                hhBefore - bond.householdPrincipal(), fromHouseholds, 1e-6);
        close("...the cells' paper by the same, pro rata", cellsBefore - hb.totalPaper(), fromHouseholds, 1e-6);
        close("...onto its own book, at face", cb.getPaperHeld() - heldBefore, fromBank + fromHouseholds, 1e-6);
        close("it paid them the curve's market value", cb.getBoughtFromHouseholds(), expectedPrice, 1e-6);
        close("...its gain against face in the month's profit", cb.getPaperGains(),
                fromBank + fromHouseholds - cb.getBoughtPaper(), 1e-6);
        assertTrue("the money it made for them is declared: the audit closes",
                Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved()));
        assertTrue("...and M0 moved by exactly the money made, the audit's MONEY lines the same",
                Math.abs((cb.m0() - m0Before) - (cb.getIssued() - cb.getRetired())) <= .005
                        && Math.abs(r.moneyMade() - (cb.getIssued() - cb.getRetired())) <= .005);
        assertTrue("...and the two books of the households' paper agree", booksAgree(city));

        // ...and a save and load in the middle: the dial, the holdings, the pace.
        assertTrue("fixture: in the middle - the households still hold some",
                bond.householdPrincipal() > 0 && ledger.centralBankShareOfTerm() < 1);
        final Game[] back = new Game[1];
        quietly(() -> {
            city.saveGame(5, "the whole book");
            back[0] = new Game(files);
            back[0].loadGameSave(5);
        });
        Game twin = back[0];
        CentralBank cb2 = twin.getCentralBank();
        Debt bond2 = termPiece(twin.getDebtManager());
        close("through a save: the dial at 100%, not held at the old half", cb2.getTargetShare(), 1.0, 0);
        close("...the book at face", cb2.getPaperHeld(), cb.getPaperHeld(), 0);
        close("...what it has paid the households since founding", cb2.getBoughtFromHouseholdsLifetime(),
                cb.getBoughtFromHouseholdsLifetime(), 0);
        assertTrue("...the paper's holders, piece by piece",
                bond2.householdPrincipal() == bond.householdPrincipal()
                        && bond2.centralBankPrincipal() == bond.centralBankPrincipal());
        double[] was = hb.toCellSaveArray(), is = twin.getHouseholdBalance().toCellSaveArray();
        boolean cellsSame = was.length == is.length;
        for (int i = 0; cellsSame && i < was.length; i++) cellsSame = was[i] == is[i];
        assertTrue("...and every cell's paper", cellsSame);
        close("...so it steps at the same pace", cb2.stepFor(twin.getDebtManager().termPrincipal()),
                cb.stepFor(term), 1e-9);
        play(city);
        play(twin);
        close("a month on, both cities buy the same from the households",
                cb2.getBoughtFromHouseholds(), cb.getBoughtFromHouseholds(), 1e-9);
        close("...and hold the same", cb2.getPaperHeld(), cb.getPaperHeld(), 1e-9);

        for (int m = 0; m < 4 && ledger.centralBankShareOfTerm() < 1 - 1e-12; m++) play(city);
        close("the dial is reached: it holds all of the term paper", ledger.centralBankShareOfTerm(), 1, 1e-12);
        close("...the households none of it", bond.householdPrincipal(), 0, 1e-9);
        out.printf("   m%d: the central bank holds $%,.2fk, paid the households $%,.2fk for theirs; the floor %.3f%%,"
                        + " the policy rate %.3f%%, the bank's floor %.3f%%%n", city.getMonth(), cb.getPaperHeld(),
                cb.getBoughtFromHouseholdsLifetime(), ledger.floorRate() * 100, ledger.getPolicyRate() * 100,
                ledger.bankFloorRate() * 100);
    }

    /* ================= 10. the businesses (CD2, slice 4, D33) ================= */

    /** The two books of the businesses' paper agree: the sectors', and the paper's. */
    static boolean firmsAgree(Game g) {
        double sectors = g.getOutwardInvestment().totalPaper();
        double paper = g.getDebtManager().firmPrincipal();
        return Math.abs(sectors - paper) <= 1e-9 * Math.max(1, paper);
    }

    /** The domestic piece on the books that was not there before. */
    static Debt newPiece(DebtManager ledger, java.util.List<Debt> before) {
        Debt found = null;
        for (Debt d : ledger.getDebt()) if (!d.isForeign() && !before.contains(d)) found = d;
        return found;
    }

    /** Every sector's till, in Sectors.KEYS order. */
    static double[] tills(Game g) {
        double[] t = new double[Sectors.KEYS.length];
        for (int i = 0; i < t.length; i++) t[i] = g.getEconomyManager().getSectorCash(Sectors.KEYS[i]);
        return t;
    }

    /** Every sector's paper, in Sectors.KEYS order. */
    static double[] holdings(Game g) {
        double[] h = new double[Sectors.KEYS.length];
        for (int i = 0; i < h.length; i++) h[i] = g.getOutwardInvestment().paperOf(Sectors.KEYS[i]);
        return h;
    }

    /** True when these sectors' cash flow statements explain their tills to the cent this month (SectorBooks.SectorMonth.unexplained()). */
    static boolean booksExplain(Game g, java.util.List<String> sectors) {
        boolean ok = true;
        for (String k : sectors) {
            SectorBooks.SectorMonth m = g.getSectorBooks().get(k);
            ok &= Math.abs(m.unexplained()) <= MoneyAudit.tolerance(1e-6, m.unexplainedScale());
        }
        return ok;
    }

    /** One close() with the tolerance a part in a billion of the figure, or a cent. */
    static void near(String label, double actual, double expected) {
        close(label, actual, expected, Math.max(1e-6, 1e-9 * Math.abs(expected)));
    }

    static void theBusinesses() throws Exception {
        out.println("\n--- 10. the businesses are a holder: they buy at the settle, are paid, sell, and the books agree ---");
        GameFiles files = GameFiles.scratch("holderscheck-firms");
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            // The treasury, the ground and the held builders the other sections were written against (see main(), theWholeBook()).
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            city.getBusinessInvestment().holdSector(Sectors.CONSTRUCTION);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(60);
            for (int i = 0; i < 36 && city.getMigration().getLastDecliningShare() > 0; i++) city.simulateMonths(1);
        });
        HouseholdBalance hb = city.getHouseholdBalance();
        DebtManager ledger = city.getDebtManager();
        Bank bank = city.getBank();
        CentralBank cb = city.getCentralBank();
        EconomyManager econ = city.getEconomyManager();
        OutwardInvestment outward = city.getOutwardInvestment();
        ForeignAccounts fx = city.getForeignAccounts();
        final String[] keys = Sectors.KEYS;
        final int n = keys.length;
        // Three times what the households can pay: they bid first and in full up to their purse (D32), so only
        // an issue past it leaves the businesses, the bank and the world anything to be allotted.
        final double ask = Math.max(100, hb.spareForPaper() * 3);

        // The businesses with idle money: the first three sectors that owe nothing, each given a till worth
        // a hundred issues, so the businesses are a bidder at the auction and not a rounding.
        final java.util.List<String> idle = new java.util.ArrayList<>();
        for (String k : keys) if (idle.size() < 3 && econ.getBusinessDebtManager().getPrincipal(k) <= 0) idle.add(k);
        assertTrue("fixture: three sectors that owe nothing", idle.size() == 3);
        final double till = 100 * ask;
        Runnable fill = () -> { for (String k : idle) econ.setSectorCash(k, till); };
        fill.run();
        assertTrue("fixture: their idle money is what the auction counts of the businesses",
                outward.idleWealth(econ) >= 3 * till * .99);
        assertTrue("fixture: and they hold none of the city's paper yet", outward.totalPaper() == 0 && ledger.firmPrincipal() == 0);

        /* ---- the settle ---- */
        out.println("   -- the settle: each sector pays out of its till, in proportion to its own bid");
        java.util.List<Debt> had = new java.util.ArrayList<>(ledger.getDebt());
        quietly(() -> city.handleLongBondLogic(ask, 20, 100));
        final Debt bond = newPiece(ledger, had);
        assertTrue("fixture: the treasury sold a twenty-year bond between the presses", bond != null);
        double received = bond.getSettleDue(), face = bond.getOustandingPrincipal();
        double allotFace = bond.allotFirmsFace(), allotCash = bond.allotFirmsCash();
        out.printf("   it raised $%,.2fk on $%,.0fk of face at %.3f%%; the auction allotted the businesses $%,.2fk of face for $%,.2fk%n",
                received, face, bond.getIssueYield() * 100, allotFace, allotCash);
        assertTrue("fixture: the stop is over what the world pays, so the businesses' rule bids",
                bond.getIssueYield() > DebtManager.WORLD_BASE_RATE);
        assertTrue("fixture: the auction filled the businesses", allotFace > 0 && allotCash > 0);
        near("...for the issue's price: their cash over their face is what the issue raised over its face",
                allotCash / allotFace, received / face);
        close("they hold none of it until the settle", bond.firmPrincipal(), 0, 0);

        final double[] before = new double[n], atSettle = new double[n], held = new double[n], weight = new double[n];
        final double[] seen = new double[1];     // the piece's businesses' share, once the settle has run
        city.settleProbeForTest = after -> {
            if (!after) {
                for (int i = 0; i < n; i++) before[i] = econ.getSectorCash(keys[i]);
                System.arraycopy(outward.allotmentWeights(bond.getIssueYield(), bank.depositRate(),
                        DebtManager.WORLD_BASE_RATE, econ), 0, weight, 0, n);
            } else {
                for (int i = 0; i < n; i++) { atSettle[i] = econ.getSectorCash(keys[i]); held[i] = outward.paperOf(keys[i]); }
                seen[0] = bond.firmPrincipal();
            }
        };
        MoneyAudit.Result settled = play(city);
        city.settleProbeForTest = null;
        double sumW = 0, paidSum = 0;
        boolean paidEach = true, holdsEach = true, couldPay = true;
        for (int i = 0; i < n; i++) {
            sumW += weight[i];
            double paid = before[i] - atSettle[i];
            paidSum += paid;
            couldPay &= before[i] >= allotCash * weight[i];
            paidEach &= Math.abs(paid - allotCash * weight[i]) <= 1e-6 * Math.max(1, allotCash);
            holdsEach &= Math.abs(held[i] - allotFace * weight[i]) <= 1e-6 * Math.max(1, allotFace);
        }
        out.printf("   the businesses paid $%,.2fk; the three with idle money paid %.1f%% of it%n", paidSum,
                100 * (weight[java.util.Arrays.asList(keys).indexOf(idle.get(0))]
                        + weight[java.util.Arrays.asList(keys).indexOf(idle.get(1))]
                        + weight[java.util.Arrays.asList(keys).indexOf(idle.get(2))]));
        close("fixture: the sectors' shares of the allotment are their bids over all the bids, and sum to one", sumW, 1, 1e-9);
        assertTrue("fixture: every till could pay its share, so nothing was cut", couldPay);
        assertTrue("each sector paid its share of what the auction allotted the businesses, out of its till", paidEach);
        assertTrue("...and holds the face that cash buys, in the same shares", holdsEach);
        near("together they paid exactly what the auction allotted them", paidSum, allotCash);
        near("...and the month's figure is the same", city.getFirmsBoughtPaper(), allotCash);
        near("the paper's businesses' share is the face they were allotted", seen[0], allotFace);
        assertTrue("...and it is the sectors' paper summed", firmsAgree(city));
        near("...and they sold none of it in the month they bought it: the paper pays more than the world does, and"
                + " is read at its own yield", outward.totalPaper(), allotFace);
        near("the bank paid exactly the rest, after the world as well", city.getCityPaperSettled(),
                received - city.getHouseholdsBoughtPaper() - city.getFirmsBoughtPaper() - city.getWorldBoughtPaper());
        close("...and holds exactly the rest of the face", bond.bankPrincipal(),
                face - bond.householdPrincipal() - bond.firmPrincipal() - bond.worldPrincipal(), 1e-9 * face);
        assertTrue("the audit closed on the settle month: a till paying the bank is a pool paying a pool",
                Math.abs(settled.residual) <= MoneyAudit.tolerance(settled.moved()));

        /* ---- the coupon ---- */
        out.println("   -- the coupon reaches the tills, and the principal and a buyback do too");
        double expected = 0;
        for (Debt d : ledger.getDebt()) {
            double owed = d.getOustandingPrincipal();
            if (!d.isForeign() && owed > 0) expected += d.getMonthlyInterestExpense() * d.firmPrincipal() / owed;
        }
        assertTrue("fixture: the businesses hold paper that pays a coupon", expected > 0);
        MoneyAudit.Result couponMonth = play(city);
        near("the businesses were paid their share of the month's coupon", city.getCouponsToFirms(), expected);
        assertTrue("...and the audit closes: the treasury paid the tills", Math.abs(couponMonth.residual)
                <= MoneyAudit.tolerance(couponMonth.moved()));
        assertTrue("the two books still agree", firmsAgree(city));
        boolean paperCoupons = true;
        for (String k : idle) paperCoupons &= city.getSectorBooks().get(k).bondCoupons() >= outward.getPaperCouponsThisMonth(k)
                && outward.getPaperCouponsThisMonth(k) > 0;
        assertTrue("each sector's books carry its coupon on the city's paper among the coupons on the bonds it holds", paperCoupons);
        assertTrue("...and explain every cent of its till: the paper is a bond it holds, bought, paid and sold like the others",
                booksExplain(city, idle));

        // The split among the sectors, on a copy of their books (the live ones are held to the paper, so
        // these arithmetic checks are made on a twin; the tills are the city's, the money between two months).
        OutwardInvestment copy = new OutwardInvestment();
        copy.restore(outward.toSaveArray());
        double total = copy.totalPaper();
        double[] tl = tills(city), cp = holdings(city);
        copy.creditPaperCoupon(1000, econ);
        boolean couponSplit = true;
        double[] tl1 = tills(city);
        for (int i = 0; i < n; i++) couponSplit &= Math.abs((tl1[i] - tl[i]) - 1000 * cp[i] / total) <= 1e-9 * 1000;
        assertTrue("a coupon reaches each sector's till in proportion to the paper it holds", couponSplit);
        double x = total / 4;
        copy.creditPaperPrincipal(x, econ);
        double[] tl2 = tills(city);
        boolean principalSplit = true, paperDown = true;
        for (int i = 0; i < n; i++) {
            principalSplit &= Math.abs((tl2[i] - tl1[i]) - x * cp[i] / total) <= 1e-9 * x;
            paperDown &= Math.abs((cp[i] - copy.paperOf(keys[i])) - x * cp[i] / total) <= 1e-9 * x;
        }
        assertTrue("principal reaches the tills in the same proportion", principalSplit);
        assertTrue("...with each sector's paper down by the same", paperDown);
        close("...so the paper is down by exactly the principal", total - copy.totalPaper(), x, 1e-9 * x);

        /* ---- a till caps what its sector pays ---- */
        out.println("   -- a till caps what its sector pays, and the bank takes the rest");
        fill.run();
        had = new java.util.ArrayList<>(ledger.getDebt());
        quietly(() -> city.handleLongBondLogic(ask, 30, 100));
        final Debt bond2 = newPiece(ledger, had);
        assertTrue("fixture: the treasury sold a thirty-year bond", bond2 != null);
        double received2 = bond2.getSettleDue(), face2 = bond2.getOustandingPrincipal();
        double allotFace2 = bond2.allotFirmsFace(), allotCash2 = bond2.allotFirmsCash();
        assertTrue("fixture: the businesses were allotted some", allotFace2 > 0 && allotCash2 > 0);
        final String capped = idle.get(0);
        final int ci = java.util.Arrays.asList(keys).indexOf(capped);
        final double[] before2 = new double[n], atSettle2 = new double[n], held2 = new double[n], heldBefore2 = new double[n], weight2 = new double[n];
        city.settleProbeForTest = after -> {
            if (!after) {
                // Cause it: its dollars abroad (a hundred issues of them) make it a bidder like the others, and its till
                // is a quarter of what its share of the allotment would be.
                // ...and what leaves the till goes to the treasury, a pool, inside the audit's window.
                double[] arr = outward.toSaveArray();
                arr[7 + 3 * ci] = till / outward.getLastRate();
                outward.restore(arr);
                double was = econ.getSectorCash(capped);
                econ.setSectorCash(capped, 1);
                double w = outward.allotmentWeights(bond2.getIssueYield(), bank.depositRate(), DebtManager.WORLD_BASE_RATE, econ)[ci];
                econ.setSectorCash(capped, allotCash2 * w / 4);
                city.setCashForTest(city.getCash() + (was - econ.getSectorCash(capped)));
                for (int i = 0; i < n; i++) { before2[i] = econ.getSectorCash(keys[i]); heldBefore2[i] = outward.paperOf(keys[i]); }
                System.arraycopy(outward.allotmentWeights(bond2.getIssueYield(), bank.depositRate(),
                        DebtManager.WORLD_BASE_RATE, econ), 0, weight2, 0, n);
            } else {
                for (int i = 0; i < n; i++) { atSettle2[i] = econ.getSectorCash(keys[i]); held2[i] = outward.paperOf(keys[i]); }
                seen[0] = bond2.firmPrincipal();
            }
        };
        MoneyAudit.Result settled2 = play(city);
        city.settleProbeForTest = null;
        double demand = allotCash2 * weight2[ci];
        assertTrue("fixture: the till is under what its share of the allotment asks", before2[ci] > 0 && before2[ci] < demand);
        double cut = before2[ci] / demand;
        out.printf("   %s was asked $%,.2fk and had $%,.2fk: it paid %.1f%% of its share%n", capped, demand, before2[ci], cut * 100);
        close("it paid the whole of its till and not a unit more", atSettle2[ci], 0, 1e-9 * Math.max(1, demand));
        close("...for that share of its face: the cut is the same on the face as on the cash",
                held2[ci] - heldBefore2[ci], allotFace2 * weight2[ci] * cut, 1e-6 * Math.max(1, allotFace2));
        double others = 0, othersFace = 0;
        boolean fullOthers = true;
        for (int i = 0; i < n; i++) {
            if (i == ci) continue;
            others += before2[i] - atSettle2[i];
            othersFace += held2[i] - heldBefore2[i];
            fullOthers &= Math.abs((before2[i] - atSettle2[i]) - allotCash2 * weight2[i]) <= 1e-6 * Math.max(1, allotCash2);
        }
        assertTrue("every other sector paid its share in full", fullOthers);
        near("the businesses paid the others' shares and the capped one's till, no more", city.getFirmsBoughtPaper(),
                others + before2[ci]);
        near("the paper's businesses' share is the face the sectors took", seen[0], othersFace + held2[ci] - heldBefore2[ci]);
        near("the bank paid exactly the rest, the shortfall included", city.getCityPaperSettled(),
                received2 - city.getHouseholdsBoughtPaper() - city.getFirmsBoughtPaper() - city.getWorldBoughtPaper());
        close("...and holds the rest of the face", bond2.bankPrincipal(),
                face2 - bond2.householdPrincipal() - bond2.firmPrincipal() - bond2.worldPrincipal(), 1e-9 * face2);
        assertTrue("the two books still agree", firmsAgree(city));
        assertTrue("the audit closed on it", Math.abs(settled2.residual) <= MoneyAudit.tolerance(settled2.moved()));

        /* ---- selling to the desk ---- */
        out.println("   -- the spread gone, a borrower, and money wanted: each sells to the bank's desk");
        // The spread gone: the paper pays what the world pays, so the sectors' target for it is nothing.
        final double ratio = .95;
        outward.setPaperBook(ratio, DebtManager.WORLD_BASE_RATE);
        double[] h0 = holdings(city);
        double total0 = outward.totalPaper();
        double soldFace = total0 * OutwardInvestment.HOME_SPEED;
        double unearned = 0;
        for (Debt d : ledger.getDebt()) {
            if (d.firmPrincipal() > 0) unearned += d.unaccretedOn(soldFace * d.firmPrincipal() / ledger.firmPrincipal());
        }
        double bankCash = bank.getCash(), bankBook = bank.getCityBook(), bankGains = bank.getPaperGains();
        double soldBefore = outward.getLifetimePaperSold();
        outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE, fx.getRate(), econ);
        double[] h1 = holdings(city);
        boolean eachSold = true;
        for (int i = 0; i < n; i++) eachSold &= Math.abs((h0[i] - h1[i]) - h0[i] * OutwardInvestment.HOME_SPEED) <= 1e-9 * Math.max(1, h0[i]);
        assertTrue("with no spread, every sector sold HOME_SPEED of what it held, the pace the dollars come home at", eachSold);
        near("...to the desk at the month's ratio: the cash it paid", bankCash - bank.getCash(), soldFace * ratio);
        near("...and the sectors' record of what they were paid is the same", outward.getLifetimePaperSold() - soldBefore, soldFace * ratio);
        near("...its book rose by the face", bank.getCityBook() - bankBook, soldFace);
        near("...and it booked the gain against what it carries the paper at, the unearned discount riding on it",
                bank.getPaperGains() - bankGains, (soldFace - unearned) - soldFace * ratio);
        assertTrue("the two books still agree", firmsAgree(city));

        // A borrower: its target is nothing, so it sells whatever the spread, and the sectors that owe nothing do not.
        final String owes = idle.get(1), keeps1 = idle.get(0), keeps2 = idle.get(2);
        econ.getBusinessDebtManager().setAssets(owes, 4 * till);
        econ.getBusinessDebtManager().issueLoan(owes, till / 10, city.getMonth());
        assertTrue("fixture: the borrower owes something", econ.getBusinessDebtManager().getPrincipal(owes) > 0);
        outward.setPaperBook(1, .08);       // six points over the world: a sector with money wants as much as it can
        double[] h2 = holdings(city);
        int oi = java.util.Arrays.asList(keys).indexOf(owes), k1 = java.util.Arrays.asList(keys).indexOf(keeps1),
                k2 = java.util.Arrays.asList(keys).indexOf(keeps2);
        assertTrue("fixture: the borrower holds some", h2[oi] > 0);
        outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE, fx.getRate(), econ);
        double[] h3 = holdings(city);
        close("a sector that borrows sells HOME_SPEED of all it holds", h2[oi] - h3[oi], h2[oi] * OutwardInvestment.HOME_SPEED,
                1e-9 * Math.max(1, h2[oi]));
        assertTrue("...while the sectors with money and nothing owed keep theirs, the paper paying what it does",
                h3[k1] == h2[k1] && h3[k2] == h2[k2]);
        assertTrue("the two books still agree", firmsAgree(city));

        // Money wanted: after the dollars, the paper. Dollars abroad are given by hand, to have them.
        final String wants = keeps2;
        double[] arr = outward.toSaveArray();
        double abroadValue = 2 * ask;
        arr[7 + 3 * k2] = abroadValue / outward.getLastRate();
        outward.restore(arr);
        double heldW = outward.paperOf(wants);
        double tillW = econ.getSectorCash(wants), bankW = bank.getCash();
        double got = outward.recall(wants, ask, econ);
        near("asked for less than its dollars abroad are worth, the dollars come", got, ask);
        close("...and the paper is not touched", outward.paperOf(wants), heldW, 0);
        near("...into the till", econ.getSectorCash(wants) - tillW, ask);
        double wantedMore = ask + .1 * heldW * outward.getPaperRatio();
        got = outward.recall(wants, wantedMore, econ);
        near("asked for more than its dollars, it gets all of them and the rest from its paper, at the month's ratio", got, wantedMore);
        close("...the dollars gone", outward.getUsd(wants), 0, 1e-9);
        close("...and a tenth of the paper sold to the desk", heldW - outward.paperOf(wants), .1 * heldW, 1e-9 * Math.max(1, heldW));
        near("...the desk paying the paper's market value", bankW - bank.getCash(), .1 * heldW * outward.getPaperRatio());
        double everything = outward.recall(wants, 1e9 * ask, econ);
        near("asked for more than it has, it sells all its paper and no more", everything, .9 * heldW * outward.getPaperRatio());
        close("...and holds none", outward.paperOf(wants), 0, 1e-12);
        assertTrue("the two books still agree", firmsAgree(city));
        // The dollars brought home by hand are this month's, declared by the next takeMonth() as they always were; fold them now,
        // between two months, so the next month's window is not handed money that arrived before it opened.
        outward.takeMonth(bank.depositRate(), DebtManager.WORLD_BASE_RATE, fx.getRate(), econ);

        /* ---- a buyback ---- */
        out.println("   -- a buyback pays the businesses their share, into their tills");
        double firmFace = bond2.firmPrincipal(), principal2 = bond2.getOustandingPrincipal(), bankFace2 = bond2.bankPrincipal();
        assertTrue("fixture: the businesses still hold some of the thirty-year bond", firmFace > 0);
        city.setCashForTest(city.getCash() + 2 * principal2);
        double price2 = city.quoteRepurchase(bond2);
        double[] hb4 = holdings(city), tl4 = tills(city);
        double tot4 = outward.totalPaper(), bankCash4 = bank.getCash();
        double paid2 = city.repurchaseDebt(bond2);
        near("fixture: the buyback was at the quoted price", paid2, price2);
        double firmPrice = price2 * firmFace / principal2;
        double[] ha = holdings(city), ta = tills(city);
        boolean buybackTills = true, buybackPaper = true;
        for (int i = 0; i < n; i++) {
            buybackTills &= Math.abs((ta[i] - tl4[i]) - firmPrice * hb4[i] / tot4) <= 1e-6 * Math.max(1, firmPrice);
            buybackPaper &= Math.abs((hb4[i] - ha[i]) - firmFace * hb4[i] / tot4) <= 1e-6 * Math.max(1, firmFace);
        }
        assertTrue("the businesses' share of the price went into their tills, in proportion to the paper each holds", buybackTills);
        assertTrue("...and their paper is down by the face they held of it, in the same proportion", buybackPaper);
        near("the bank's share of the price went to its cash", bank.getCash() - bankCash4, price2 * bankFace2 / principal2);
        assertTrue("the two books still agree", firmsAgree(city));
        MoneyAudit.Result buyMonth = play(city);
        assertTrue("the audit closes on the month after: the businesses are paid between two pools, nothing is carried",
                Math.abs(buyMonth.residual) <= MoneyAudit.tolerance(buyMonth.moved()));

        /* ---- the principal, at maturity ---- */
        out.println("   -- the principal, at maturity: a two-year bond held to the end");
        fill.run();
        had = new java.util.ArrayList<>(ledger.getDebt());
        quietly(() -> city.handleLongBondLogic(ask, 2, 100));
        final Debt bond3 = newPiece(ledger, had);
        assertTrue("fixture: the treasury sold a two-year bond", bond3 != null);
        play(city);
        assertTrue("fixture: the businesses took part of it at the settle", bond3.firmPrincipal() > 0);
        double pre = bond3.firmPrincipal();
        boolean agreeEvery = true;
        int guard = 0;
        boolean booksEvery = true;
        while (ledger.getDebt().contains(bond3) && guard++ < 40) {
            pre = bond3.firmPrincipal();
            play(city);
            agreeEvery &= firmsAgree(city);
            booksEvery &= booksExplain(city, idle);
        }
        assertTrue("every month of its life, the sectors' paper was the paper's (a coupon, a sale, a settle among them)", agreeEvery);
        assertTrue("...and the sectors' books explained their tills to the cent, the principal's month among them", booksEvery);
        assertTrue("fixture: it fell due", !ledger.getDebt().contains(bond3) && guard < 40);
        assertTrue("fixture: and the businesses held some of it to the end", pre > 0);
        near("the businesses were paid, as principal, the face they held of it", city.getPrincipalToFirms(), pre);
        assertTrue("...and the two books agree after it", firmsAgree(city));

        /* ---- the save ---- */
        out.println("   -- save and reload, and every holding is where it was");
        assertTrue("fixture: the businesses still hold some of the twenty-year bond", outward.totalPaper() > 0 && bond.firmPrincipal() > 0);
        final Game[] back = new Game[1];
        quietly(() -> {
            city.saveGame(3, "the businesses");
            back[0] = new Game(files);
            back[0].loadGameSave(3);
        });
        Game twin = back[0];
        OutwardInvestment outward2 = twin.getOutwardInvestment();
        boolean sameSectors = true;
        for (String k : keys) sameSectors &= outward2.paperOf(k) == outward.paperOf(k);
        assertTrue("every sector's paper came back exactly", sameSectors);
        close("...the ratio and the yield it was struck at", outward2.getPaperRatio() + outward2.getPaperYield(),
                outward.getPaperRatio() + outward.getPaperYield(), 0);
        close("...what the businesses have bought and sold since founding", outward2.getLifetimePaperBought()
                + outward2.getLifetimePaperSold(), outward.getLifetimePaperBought() + outward.getLifetimePaperSold(), 0);
        boolean pieces = ledger.getDebt().size() == twin.getDebtManager().getDebt().size();
        for (int i = 0; pieces && i < ledger.getDebt().size(); i++) {
            pieces = ledger.getDebt().get(i).firmPrincipal() == twin.getDebtManager().getDebt().get(i).firmPrincipal();
        }
        assertTrue("...and every piece's businesses' share", pieces);
        assertTrue("...and the two books agree in the reloaded city", firmsAgree(twin));
        MoneyAudit.Result mine = play(city), theirs = play(twin);
        near("a month on, both pay the businesses the same coupon", twin.getCouponsToFirms(), city.getCouponsToFirms());
        near("...and hold the same", twin.getOutwardInvestment().totalPaper(), outward.totalPaper());
        assertTrue("...and both months close", Math.abs(mine.residual) <= MoneyAudit.tolerance(mine.moved())
                && Math.abs(theirs.residual) <= MoneyAudit.tolerance(theirs.moved()));

        /* ---- a save from before the businesses held any ---- */
        out.println("   -- a save from before the businesses held the paper loads with the bank holding it");
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                Files.readString(files.saveFile(3))).getAsJsonObject();
        com.google.gson.JsonArray full = json.getAsJsonArray("outwardInvestment");
        assertTrue("fixture: the save carries the sectors' paper at the end of the array", full != null && full.size() == 7 + 4 * n + 4);
        com.google.gson.JsonArray cut7 = new com.google.gson.JsonArray();
        for (int i = 0; i < 7 + 3 * n; i++) cut7.add(full.get(i));
        json.add("outwardInvestment", cut7);
        for (com.google.gson.JsonElement e : json.getAsJsonArray("debts")) e.getAsJsonObject().remove("firmPrincipal");
        Files.writeString(files.saveFile(4), json.toString());
        quietly(() -> {
            back[0] = new Game(files);
            back[0].loadGameSave(4);
        });
        Game old = back[0];
        assertTrue("it loads", old.getLoadFailure() == null && old.getMonth() > 1);
        close("the businesses hold none of the city's paper", old.getOutwardInvestment().totalPaper(), 0, 0);
        close("...nor does the paper say they do", old.getDebtManager().firmPrincipal(), 0, 0);
        close("...so the bank holds what the households, the central bank and the world do not", old.getDebtManager().bankPrincipal(),
                old.getDebtManager().getDomesticPrincipal() - old.getDebtManager().householdPrincipal()
                        - old.getDebtManager().centralBankPrincipal() - old.getDebtManager().worldPrincipal(),
                1e-9 * Math.max(1, old.getDebtManager().getDomesticPrincipal()));
        int brokenBefore = brokenMonths;
        for (int i = 0; i < 3; i++) play(old);
        assertTrue("...and it runs, the audit closing every month", brokenMonths == brokenBefore);
        assertTrue("...with the two books agreeing", firmsAgree(old));

        /* ---- the central bank, last ---- */
        out.println("   -- the dial at the whole of the paper reaches the businesses' last");
        cb.setTargetShare(1.0);
        boolean m0Exact = true, agreeAll = true;
        int months = 0;
        while (ledger.centralBankShareOfTerm() < 1 - 1e-12 && months++ < 14) {
            double m0Before = cb.m0();
            MoneyAudit.Result r = play(city);
            m0Exact &= Math.abs((cb.m0() - m0Before) - (cb.getIssued() - cb.getRetired())) <= .005
                    && Math.abs(r.moneyMade() - (cb.getIssued() - cb.getRetired())) <= .005;
            agreeAll &= firmsAgree(city);
        }
        out.printf("   the central bank holds %.1f%% of the term paper after %d month(s); the businesses hold $%,.2fk%n",
                ledger.centralBankShareOfTerm() * 100, months, ledger.firmPrincipal());
        close("the dial is reached: the central bank holds all of the term paper", ledger.centralBankShareOfTerm(), 1, 1e-9);
        close("...the businesses none of it, the last it reached", ledger.firmPrincipal(), 0, 1e-6);
        close("...and the sectors' book says the same", outward.totalPaper(), 0, 1e-6);
        assertTrue("every month on the way the books agreed", agreeAll);
        assertTrue("...M0 moved by exactly the money made, the audit's MONEY lines the same, the businesses' purchase among it", m0Exact);
    }

    /* ================= 11. the world (CD2, slice 5, D7; star 2) ================= */

    /** The two books of the world's paper agree: CapitalFlows', and the paper's. */
    static boolean worldAgrees(Game g) {
        double book = g.getCapitalFlows().getPaper();
        double paper = g.getDebtManager().worldPrincipal();
        return Math.abs(book - paper) <= 1e-9 * Math.max(1, paper);
    }

    /** One line of the month's audit by its label, to the cent: 0 when the month has none. */
    static double auditLine(MoneyAudit.Result r, String label) {
        for (String line : r.detail.split("\n")) {
            String t = line.trim();
            if (!t.startsWith(label + " ")) continue;
            try {
                return Double.parseDouble(t.substring(label.length()).trim().replace(",", ""));
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
        }
        return 0;
    }

    /** The audit's tolerance for a figure read to the cent. */
    static final double CENT = .0051;

    static void theWorld() throws Exception {
        out.println("\n--- 11. the world is a holder: it buys from abroad, is paid abroad, leaves with its money, and the books agree ---");

        /* ---- the model alone: the paper's book on CapitalFlows ---- */
        out.println("   -- the book, alone: saved, scaled, cleared, and the share that leaves with the money");
        CapitalFlows lone = new CapitalFlows();
        lone.takePaper(95, 100);
        lone.takePaper(190, 200);
        close("it holds the face it was allotted, whatever it paid", lone.getPaper(), 300, 0);
        close("...and remembers what it paid", lone.getLifetimePaperBought(), 285, 0);
        double[] saved = lone.toSaveArray();
        CapitalFlows back = new CapitalFlows();
        back.restore(saved);
        close("a save carries the book", back.getPaper(), 300, 0);
        close("...and what it has paid and been paid", back.getLifetimePaperBought() + back.getLifetimePaperSold(), 285, 0);
        double[] oldSave = java.util.Arrays.copyOf(saved, 12);
        CapitalFlows older = new CapitalFlows();
        older.restore(oldSave);
        close("a save from before the world held any has none (the array ends at the carry trade)", older.getPaper(), 0, 0);
        lone.redenominate(.01);
        close("a reform scales the book", lone.getPaper(), 3, 1e-12);
        lone.reset();
        close("...and a new game clears it", lone.getPaper() + lone.getLifetimePaperBought(), 0, 0);

        // A departure: calm, a stock, a rate that no longer pays, so the stock leaves at DEPARTURE_SPEED of its excess.
        CapitalFlows leaving = new CapitalFlows();
        for (int m = 1; m <= 120; m++) {
            leaving.setMonth(m);
            leaving.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01, 10_000, 1e9, 0, false, false, m);
        }
        double stock0 = leaving.getStock();
        assertTrue("fixture: money came for a seven-point rate", stock0 > 0 && !leaving.isStopped());
        leaving.takePaper(stock0 / 4, stock0 / 4);
        double paper0 = leaving.getPaper();
        leaving.setMonth(121);
        leaving.takeMonth(.02, DebtManager.WORLD_BASE_RATE, DebtManager.WORLD_BASE_RATE, .01, 10_000, 1e9, 0, false, false, 121);
        assertTrue("fixture: the spread is gone, so the money leaves, and nobody has panicked",
                leaving.getDeparted() > 0 && !leaving.isStopped());
        close("the paper that leaves is what departs, over the world's whole position (its stock and the paper at market),"
                + " of the face", leaving.paperLeaving(stock0, 1), leaving.getDeparted() * paper0 / (stock0 + paper0), 1e-9 * paper0);
        close("...and at a market below face the same departure is a smaller share of a smaller position",
                leaving.paperLeaving(stock0, .5), leaving.getDeparted() * paper0 / (stock0 + paper0 * .5), 1e-9 * paper0);
        assertTrue("...which is less than the face, whatever the book", leaving.paperLeaving(stock0, 1) < paper0);
        CapitalFlows steady = new CapitalFlows();
        for (int m = 1; m <= 120; m++) {
            steady.setMonth(m);
            steady.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01, 10_000, 1e9, 0, false, false, m);
        }
        steady.takePaper(1000, 1000);
        steady.setMonth(121);
        steady.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01, 10_000, 1e9, 0, false, false, 121);
        close("a month in which no money leaves sends none of the paper home", steady.paperLeaving(steady.getStock(), 1), 0, 0);
        CapitalFlows running = new CapitalFlows();
        for (int m = 1; m <= 120; m++) {
            running.setMonth(m);
            running.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01, 10_000, 0, 0, false, false, m);
        }
        running.takePaper(1000, 1000);
        running.setMonth(121);
        running.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01, 10_000, 0, .20, false, false, 121);
        assertTrue("fixture: a twenty-point fall on thin reserves is a run", running.isStopped());
        close("in a run PANIC_EXIT of the paper leaves, the same share as of the stock", running.paperLeaving(running.getStock(), 1),
                1000 * CapitalFlows.PANIC_EXIT, 1e-9);
        running.sellPaper(running.paperLeaving(running.getStock(), 1), 600);
        close("...and what is sold comes off the book", running.getPaper(), 1000 * (1 - CapitalFlows.PANIC_EXIT), 1e-9);
        close("...and is remembered", running.getLifetimePaperSold(), 600, 0);

        /* ---- the city ---- */
        GameFiles files = GameFiles.scratch("holderscheck-world");
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            // The treasury, the ground and the held builders the other sections were written against (see main(), theWholeBook()).
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 50_000_000L);
            city.getForeignAccounts().pinRate(1.0);
            city.getBusinessInvestment().holdSector(Sectors.CONSTRUCTION);
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(60);
            for (int i = 0; i < 36 && city.getMigration().getLastDecliningShare() > 0; i++) city.simulateMonths(1);
        });
        HouseholdBalance hb = city.getHouseholdBalance();
        DebtManager ledger = city.getDebtManager();
        Bank bank = city.getBank();
        CentralBank cb = city.getCentralBank();
        CapitalFlows hot = city.getCapitalFlows();
        ForeignAccounts fx = city.getForeignAccounts();
        final double ask = Math.max(100, hb.spareForPaper() * 3);
        assertTrue("fixture: the world is not in a run, so its rule bids", !hot.isStopped());
        assertTrue("fixture: and the two books of its paper agree going in", worldAgrees(city));
        // The hot money the city has drawn is most of what the world would hold here (the auction counts the two together),
        // so its bid is a rounding. Cause an appetite before a sale: a tenth of the deposits, as the world would have it
        // early in a boom (the months in between bring the rest back, at ARRIVAL_SPEED).
        final Runnable appetite = () -> {
            double[] cf = hot.toSaveArray();
            cf[0] = hot.getStock() / 10;
            hot.restore(cf);
            bank.setForeignDeposits(hot.getStock());
        };
        appetite.run();
        assertTrue("fixture: the world holds a tenth of the hot money it had, and has the appetite for the rest", hot.getStock() < hot.getTarget() / 2);

        /* ---- the settle ---- */
        out.println("   -- the settle: the world pays from abroad, and the bank's settle shrinks by it");
        double paperBefore = hot.getPaper(), boughtBefore = hot.getLifetimePaperBought();
        java.util.List<Debt> had = new java.util.ArrayList<>(ledger.getDebt());
        quietly(() -> city.handleLongBondLogic(ask, 20, 100));
        final Debt bond = newPiece(ledger, had);
        assertTrue("fixture: the treasury sold a twenty-year bond between the presses", bond != null);
        double received = bond.getSettleDue(), face = bond.getOustandingPrincipal();
        double allotFace = bond.allotWorldFace(), allotCash = bond.allotWorldCash();
        out.printf("   it raised $%,.2fk on $%,.0fk of face at %.3f%%; the auction allotted the world $%,.2fk of face for $%,.2fk"
                + " (the hot money in the city is $%,.0fk)%n", received, face, bond.getIssueYield() * 100, allotFace, allotCash,
                hot.getStock());
        assertTrue("fixture: the stop is over what the world pays, so its rule bids", bond.getIssueYield() > DebtManager.WORLD_BASE_RATE);
        assertTrue("fixture: the auction filled the world", allotFace > 0 && allotCash > 0);
        near("...for the issue's price: its cash over its face is what the issue raised over its face",
                allotCash / allotFace, received / face);
        close("it holds none of it until the settle", bond.worldPrincipal(), 0, 0);
        final double[] seen = new double[2];
        city.settleProbeForTest = after -> {
            if (after) { seen[0] = bond.worldPrincipal(); seen[1] = hot.getPaper(); }
        };
        MoneyAudit.Result settled = play(city);
        city.settleProbeForTest = null;
        near("the world paid exactly what the auction allotted it", city.getWorldBoughtPaper(), allotCash);
        near("...and the paper's world share is the face it was allotted", seen[0], allotFace);
        near("...and the world's book, CapitalFlows', is up by the same face", seen[1] - paperBefore, allotFace);
        near("...for what it paid, remembered", hot.getLifetimePaperBought() - boughtBefore, allotCash);
        assertTrue("...and the two books agree", worldAgrees(city));
        near("the bank paid exactly the rest", city.getCityPaperSettled(),
                received - city.getHouseholdsBoughtPaper() - city.getFirmsBoughtPaper() - city.getWorldBoughtPaper());
        close("...and holds exactly the rest of the face", bond.bankPrincipal(),
                face - bond.householdPrincipal() - bond.firmPrincipal() - bond.worldPrincipal(), 1e-9 * face);
        close("the audit's financial inflow line for it is what it paid", auditLine(settled, "+ world PaperBoughtByWorld"),
                allotCash, CENT);
        assertTrue("...inside the financial account the currency reads", settled.financialIn >= allotCash - CENT);
        assertTrue("...which the balance of payments took", Math.abs(fx.getFinancialIn() - settled.financialIn) <= CENT);
        assertTrue("the audit closed on the settle month: the world's money is declared coming in",
                Math.abs(settled.residual) <= MoneyAudit.tolerance(settled.moved()));

        /* ---- the coupon ---- */
        out.println("   -- the coupon goes abroad: an income debit, declared");
        double expected = 0;
        for (Debt d : ledger.getDebt()) {
            double owed = d.getOustandingPrincipal();
            if (!d.isForeign() && owed > 0) expected += d.getMonthlyInterestExpense() * d.worldPrincipal() / owed;
        }
        assertTrue("fixture: the world holds paper that pays a coupon", expected > 0);
        MoneyAudit.Result couponMonth = play(city);
        near("the world was paid its share of the month's coupon", city.getCouponsToWorld(), expected);
        close("...declared to the audit as income paid abroad", auditLine(couponMonth, "- city CouponsToWorld"),
                city.getCouponsToWorld(), CENT);
        assertTrue("...so the current account carries it", couponMonth.incomeOut >= city.getCouponsToWorld() - CENT);
        assertTrue("...and the audit closes: the treasury's pool paid a stranger", Math.abs(couponMonth.residual)
                <= MoneyAudit.tolerance(couponMonth.moved()));
        assertTrue("the two books still agree", worldAgrees(city));

        /* ---- a buyback ---- */
        out.println("   -- a buyback pays the world its share, abroad");
        double worldFace = bond.worldPrincipal(), principal = bond.getOustandingPrincipal();
        assertTrue("fixture: the world still holds some of the twenty-year bond", worldFace > 0);
        double heldBefore = hot.getPaper();
        city.setCashForTest(city.getCash() + 2 * principal);
        double price = city.quoteRepurchase(bond);
        double carried = city.getBuybackUnsettled();
        double paid = city.repurchaseDebt(bond);
        near("fixture: the buyback was at the quoted price", paid, price);
        near("the world's paper is down by the face it held of it", heldBefore - hot.getPaper(), worldFace);
        near("...and the treasury's pool carries its share of the price until the month declares it leaving",
                city.getBuybackUnsettled() - carried, price * worldFace / principal
                        + price * bond.householdPrincipal() / principal + price * bond.centralBankPrincipal() / principal);
        assertTrue("the two books still agree", worldAgrees(city));
        MoneyAudit.Result buyMonth = play(city);
        assertTrue("the month after, the audit closes: the world's share left the country, declared as a financial outflow",
                Math.abs(buyMonth.residual) <= MoneyAudit.tolerance(buyMonth.moved()));
        assertTrue("...and the financial account carries at least that share",
                buyMonth.financialOut >= price * worldFace / principal - CENT);

        /* ---- the principal, at maturity ---- */
        out.println("   -- the principal, at maturity: a two-year bond held to the end");
        appetite.run();
        // A thirty-year bond is sold with it, for the rest of the section to run on: the world holds part of it.
        had = new java.util.ArrayList<>(ledger.getDebt());
        quietly(() -> city.handleLongBondLogic(ask, 30, 100));
        final Debt bond6 = newPiece(ledger, had);
        assertTrue("fixture: the treasury sold a thirty-year bond", bond6 != null);
        had = new java.util.ArrayList<>(ledger.getDebt());
        quietly(() -> city.handleLongBondLogic(ask, 2, 100));
        final Debt bond3 = newPiece(ledger, had);
        assertTrue("fixture: the treasury sold a two-year bond", bond3 != null);
        play(city);
        assertTrue("fixture: the world took part of it at the settle", bond3.worldPrincipal() > 0);
        assertTrue("fixture: and the world took part of the thirty-year bond", bond6.worldPrincipal() > 0 && worldAgrees(city));
        double pre = bond3.worldPrincipal();
        boolean agreeEvery = true, closeEvery = true;
        int guard = 0;
        MoneyAudit.Result last = null;
        double principalLine = 0;
        while (ledger.getDebt().contains(bond3) && guard++ < 40) {
            pre = bond3.worldPrincipal();
            last = play(city);
            principalLine = auditLine(last, "- city PrincipalToWorld");
            agreeEvery &= worldAgrees(city);
            closeEvery &= Math.abs(last.residual) <= MoneyAudit.tolerance(last.moved());
        }
        assertTrue("fixture: it fell due", !ledger.getDebt().contains(bond3) && guard < 40);
        assertTrue("every month of its life, the world's book was the paper's (a coupon, a sale, a settle among them)", agreeEvery);
        assertTrue("...and the audit closed every month of it", closeEvery);
        near("the world was paid, as principal, the face it held of it at the end", city.getPrincipalToWorld(), pre);
        close("...declared to the audit as a financial outflow", principalLine, pre, CENT);
        assertTrue("...and the financial account carries it", last != null && last.financialOut >= pre - CENT);

        /* ---- the save ---- */
        out.println("   -- save and reload, and the book is where it was");
        assertTrue("fixture: the world still holds paper", hot.getPaper() > 0);
        final Game[] twinBox = new Game[1];
        quietly(() -> {
            city.saveGame(3, "the world");
            twinBox[0] = new Game(files);
            twinBox[0].loadGameSave(3);
        });
        Game twin = twinBox[0];
        close("the book came back exactly", twin.getCapitalFlows().getPaper(), hot.getPaper(), 0);
        close("...and what it paid and was paid for it since founding", twin.getCapitalFlows().getLifetimePaperBought()
                + twin.getCapitalFlows().getLifetimePaperSold(), hot.getLifetimePaperBought() + hot.getLifetimePaperSold(), 0);
        boolean pieces = ledger.getDebt().size() == twin.getDebtManager().getDebt().size();
        for (int i = 0; pieces && i < ledger.getDebt().size(); i++) {
            pieces = ledger.getDebt().get(i).worldPrincipal() == twin.getDebtManager().getDebt().get(i).worldPrincipal();
        }
        assertTrue("...and every piece's world share", pieces);
        assertTrue("...and the two books agree in the reloaded city", worldAgrees(twin));
        MoneyAudit.Result mine = play(city), theirs = play(twin);
        near("a month on, both pay the world the same coupon", twin.getCouponsToWorld(), city.getCouponsToWorld());
        near("...and hold the same", twin.getCapitalFlows().getPaper(), hot.getPaper());
        assertTrue("...and both months close", Math.abs(mine.residual) <= MoneyAudit.tolerance(mine.moved())
                && Math.abs(theirs.residual) <= MoneyAudit.tolerance(theirs.moved()));

        /* ---- a save from before the world held any ---- */
        out.println("   -- a save from before the world held the paper loads with the bank holding it");
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                Files.readString(files.saveFile(3))).getAsJsonObject();
        com.google.gson.JsonArray full = json.getAsJsonArray("capitalFlows");
        assertTrue("fixture: the save carries the world's paper in slots 12-14 of the capital flows array", full != null && full.size() >= 15);   // XR2 appended slot 15, what left on doubt
        com.google.gson.JsonArray cut12 = new com.google.gson.JsonArray();
        for (int i = 0; i < 12; i++) cut12.add(full.get(i));
        json.add("capitalFlows", cut12);
        for (com.google.gson.JsonElement e : json.getAsJsonArray("debts")) e.getAsJsonObject().remove("worldPrincipal");
        Files.writeString(files.saveFile(4), json.toString());
        quietly(() -> {
            twinBox[0] = new Game(files);
            twinBox[0].loadGameSave(4);
        });
        Game old = twinBox[0];
        assertTrue("it loads", old.getLoadFailure() == null && old.getMonth() > 1);
        close("the world holds none of the city's paper", old.getCapitalFlows().getPaper(), 0, 0);
        close("...nor does the paper say it does", old.getDebtManager().worldPrincipal(), 0, 0);
        int brokenBefore = brokenMonths;
        for (int i = 0; i < 3; i++) play(old);
        assertTrue("...and it runs, the audit closing every month", brokenMonths == brokenBefore);
        assertTrue("...with the two books agreeing", worldAgrees(old));

        /* ---- it leaves with its money ---- */
        out.println("   -- the money leaves and the paper goes with it, to the bank's desk");
        // Cause a departure: the stock well past what the spread would keep, so it leaves at DEPARTURE_SPEED of the excess.
        play(city);
        double[] arr = hot.toSaveArray();
        arr[0] = 4 * Math.max(hot.getStock(), hot.getTarget()) + 1;
        hot.restore(arr);
        double stockBefore = hot.getStock(), paperBeforeLeaving = hot.getPaper();
        assertTrue("fixture: the world holds paper going into the month", paperBeforeLeaving > 0);
        double soldBefore = hot.getLifetimePaperSold(), bookBefore = bank.getCityBook(), principalNow = city.getPrincipalToWorld();
        MoneyAudit.Result leavingMonth = play(city);
        double goneFace = paperBeforeLeaving - city.getPrincipalToWorld() - hot.getPaper();
        assertTrue("fixture: the stock was over its target, so money left, and nobody panicked", hot.getDeparted() > 0 && !hot.isStopped());
        double position = stockBefore + (paperBeforeLeaving - city.getPrincipalToWorld()) * ledger.worldBookRatio();
        double wantFace = hot.getDeparted() * (paperBeforeLeaving - city.getPrincipalToWorld()) / position;
        out.printf("   $%,.0fk of the stock left; $%,.2fk of the paper went with it, of $%,.2fk%n", hot.getDeparted(), goneFace,
                paperBeforeLeaving);
        assertTrue("part of the paper left with the money", goneFace > 0);
        close("...the departure over the world's whole position, of the face", goneFace, wantFace, 1e-3 * Math.max(1, wantFace));
        assertTrue("...sold to the bank's desk at a market price, below or at face",
                city.getPaperBoughtFromWorld() > 0 && city.getPaperBoughtFromWorld() <= goneFace * 1.02);
        near("...which is what the world was paid, remembered", hot.getLifetimePaperSold() - soldBefore, city.getPaperBoughtFromWorld());
        close("...declared to the audit as a financial outflow", auditLine(leavingMonth, "- desk PaperBoughtFromWorld"),
                city.getPaperBoughtFromWorld(), CENT);
        assertTrue("the two books still agree", worldAgrees(city));
        assertTrue("the audit closes on it: the desk's cash and the deposits that left are both declared",
                Math.abs(leavingMonth.residual) <= MoneyAudit.tolerance(leavingMonth.moved()));
        assertTrue("the bank's book rose, whatever its room: it buys what the world sells", bank.getCityBook() > bookBefore - 1e-6
                || goneFace < 1e-6);

        /* ---- a run ---- */
        out.println("   -- a run: it bids nothing, and PANIC_EXIT of the paper leaves each month");
        double[] stop = hot.toSaveArray();
        stop[1] = city.getMonth() + CapitalFlows.PANIC_MONTHS;
        hot.restore(stop);
        assertTrue("fixture: confidence is broken", hot.isStopped());
        Auction.Bidders bidders = city.auctionBidders(240);
        assertTrue("the auction's bidders say the world is in a run", bidders.worldPanicking());
        close("...so its bid is nothing, at any yield", Auction.worldAt(.20, bidders) + Auction.worldAt(.05, bidders), 0, 0);
        double nowPaper = hot.getPaper();
        assertTrue("fixture: the world still holds paper", nowPaper > 0);
        had = new java.util.ArrayList<>(ledger.getDebt());
        quietly(() -> city.handleLongBondLogic(ask, 10, 100));
        final Debt bond4 = newPiece(ledger, had);
        assertTrue("fixture: the treasury sold a ten-year bond in the run", bond4 != null);
        close("the auction allotted the world none of it", bond4.allotWorldFace() + bond4.allotWorldCash(), 0, 0);
        double beforeRun = hot.getPaper(), principalBefore = city.getPrincipalToWorld();
        MoneyAudit.Result runMonth = play(city);
        double afterPrincipal = beforeRun - city.getPrincipalToWorld();
        close("the month after, PANIC_EXIT of the paper has gone to the desk", hot.getPaper(),
                afterPrincipal * (1 - CapitalFlows.PANIC_EXIT), 1e-9 * Math.max(1, afterPrincipal));
        close("...declared as a financial outflow", auditLine(runMonth, "- desk PaperBoughtFromWorld"), city.getPaperBoughtFromWorld(), CENT);
        assertTrue("...with the two books agreeing and the audit closed", worldAgrees(city)
                && Math.abs(runMonth.residual) <= MoneyAudit.tolerance(runMonth.moved()));
        boolean agreeRun = true;
        for (int i = 0; i < 6; i++) {
            MoneyAudit.Result r = play(city);
            agreeRun &= worldAgrees(city) && Math.abs(r.residual) <= MoneyAudit.tolerance(r.moved());
        }
        assertTrue("every month of the run the books agreed and the audit closed", agreeRun);
        assertTrue("...and the paper has been running home: a third a month leaves it under what it was",
                hot.getPaper() < afterPrincipal * (1 - CapitalFlows.PANIC_EXIT) * .95);

        /* ---- the central bank, last ---- */
        out.println("   -- the dial at the whole of the paper reaches the world's last");
        // The run is over for the fixture's purposes: lift it, and give the world something to be bought from.
        double[] calm = hot.toSaveArray();
        calm[1] = -1;
        hot.restore(calm);
        appetite.run();
        had = new java.util.ArrayList<>(ledger.getDebt());
        quietly(() -> city.handleLongBondLogic(ask, 20, 100));
        final Debt bond5 = newPiece(ledger, had);
        assertTrue("fixture: the treasury sold a twenty-year bond", bond5 != null);
        boolean worldHad = bond5.allotWorldFace() > 0;
        play(city);
        assertTrue("fixture: the world took part of it", worldHad && hot.getPaper() > 0 && bond5.worldPrincipal() > 0);
        cb.setTargetShare(1.0);
        boolean m0Exact = true, agreeAll = true;
        double fromWorld = 0;
        int months = 0;
        while ((ledger.centralBankShareOfTerm() < 1 - 1e-12 || hot.getPaper() > 1e-6) && months++ < 14) {
            double m0Before = cb.m0();
            MoneyAudit.Result r = play(city);
            fromWorld += cb.getBoughtFromWorld();
            m0Exact &= Math.abs((cb.m0() - m0Before) - (cb.getIssued() - cb.getRetired())) <= .005
                    && Math.abs(r.moneyMade() - (cb.getIssued() - cb.getRetired())) <= .005;
            agreeAll &= worldAgrees(city);
        }
        out.printf("   the central bank holds %.1f%% of the term paper after %d month(s); it paid the world $%,.2fk for its%n",
                ledger.centralBankShareOfTerm() * 100, months, fromWorld);
        close("the dial is reached: the central bank holds all of the term paper", ledger.centralBankShareOfTerm(), 1, 1e-9);
        close("...the world none of it, the last it reached", ledger.worldPrincipal(), 0, 1e-6);
        assertTrue("...having paid the world for it, abroad", fromWorld > 0);
        assertTrue("every month on the way the two books agreed", agreeAll);
        assertTrue("...M0 moved by exactly the money made, the audit's MONEY lines the same, the world's purchase among it", m0Exact);
    }
}
