package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;

/**
 * Proves the central bank's books: that money is made and destroyed on them
 * and nowhere else, every price 0.7.0 hangs off the policy rate, and its two
 * dials - the holdings (0.7.1) and the advances ceiling (0.7.2). Not part of
 * the game.
 *
 * WHY THIS EXISTS. Jerus: "the feds sheet would show how much debt it holds,
 * like debt to itself aka money printing." Until 0.7.0 the city's central bank
 * was the rest of the world - the bank placed its spare cash abroad and funded
 * its shortfalls abroad - and the treasury's overdraft was an emergency note
 * with no limit, which compounded a broke seed to $193 quadrillion once the
 * bank really paid for it. The central bank brings all of it home, and a
 * balance sheet that makes money is exactly the kind of thing that must be
 * checked to the cent: a dollar it makes that the audit does not see is a
 * dollar from nowhere with an official name on it.
 *
 * What it has to prove, a section each, every one a fixture that causes the
 * condition rather than waiting for it:
 *
 *   1. Money made less money destroyed is the change in M0, every month, and
 *      the audit's own MONEY lines say the same - on a city played through
 *      every kind of flow the central bank has, and a month holding most of
 *      them at once.
 *   2. The bank earns exactly the policy rate on its reserves, and what it
 *      pays savers rises with it.
 *   3. The window prices at the policy rate plus the penalty, and its
 *      interest is the central bank's - and a failed bank's window debt is
 *      advanced and charged nothing (0.7.1: decided, see Bank.fundToCover()).
 *   4. The city's note prices at policy + spreads (+ the bank's strain
 *      premium, until 0.7.7), with no discount under the dial, and never
 *      under what the money costs the bank.
 *   5. A broke treasury draws advances at the policy rate, repays them from
 *      cash first, and the remittance carries the interest back less what
 *      reserves cost - and a buyback pays the bank that held the bond.
 *   6. The ceiling binds, and the arrears rule pays the promises, refuses the
 *      rest, books what it refused, and pays it down first when cash returns.
 *   7. The autopilot: the rule moves the dial, the player's hand stops it, and
 *      the toggle survives a save - as does a dial at 0%.
 *   8. A reform scales the money figures and not the ratios.
 *   9. Everything above survives a save.
 *  10. A save from before the central bank founds one empty and runs, a save
 *      carrying a note runs it off, and a new game after a load founds a
 *      fresh bank.
 *
 * And since 0.7.1, the holdings dial - Jerus: "the central bank would buy
 * gbonds or sell gbonds from thin air ... like QE and QT" - on a city of its
 * own whose bank holds a twenty-year bond:
 *
 *  11. With the dial at 30% the central bank buys QE_SPEED of it a month from
 *      the bank, at the curve's market value, in money it makes; the bank's
 *      book falls by the face; the long end of the curve sits exactly
 *      compression(240) under the table and the note does not move.
 *  12. The coupon on its share is paid it and destroyed, is in the month's
 *      profit, and reaches the treasury as the remittance the month after.
 *  13. The dial to nothing sells the book back over 1/QE_SPEED months, the
 *      bank pays what the central bank destroys, and the curve returns to
 *      the table.
 *  14. The holdings survive a save.
 *  15. A hundred-to-one reform scales the holdings and not the dial.
 *
 * And since 0.7.2, the ceiling as a dial - batch B found six months of
 * revenue "binds within months of first drawing in a small city":
 *
 *  16. A city opens at DEFAULT_ADVANCES_MONTHS; set to twelve, the ceiling
 *      doubles and a treasury that was at the old ceiling draws again; the
 *      setting survives a save, a save from before it reads the default, and
 *      a reform does not move it.
 *
 * And since 0.7.15, the dial to the whole of the paper - Jerus: "central bank
 * bond holding can ve 100% if one wants" - and the floor split by who holds
 * it, on bare debt markets whose holdings are set exactly (HoldersCheck 9
 * plays the purchase from the households):
 *
 *  17. The dial takes MAX_QE_SHARE, all of it, and no more; the compression
 *      at a quarter is what it was, half the premium, and at 50%, 75% and
 *      100% the whole premium - 0.7.14's full compression, nothing more.
 *  18. With the bank at the window (its money costing the policy rate and
 *      the penalty), the floor with the central bank holding nothing is
 *      0.7.14's to the bit; holding all of the paper, the policy rate;
 *      between, proportional to its share of all the city's paper - the
 *      notes it never buys counted - and the short end moves with it.
 *
 * And since 0.7.15, the central bank rolling its own - Jerus,
 * "Build the rollover fix", the way the Federal Reserve Bank of New York's
 * "FAQs: Treasury Rollovers" says the Desk does - on a city with a two-year
 * serial the households, the bank and the central bank hold part of:
 *
 *  19. At its dial, the par it holds of a maturing slice is added on to the
 *      rollover's issue, at the issue's price, the issue sized for the rest;
 *      the bank's book of the new paper excludes it, the market paid for its
 *      own part only, and the treasury repaid it its maturing par, so it
 *      holds what it held - and a month on buys its dial's share of what the
 *      roll capitalised, not its par back. A player's issue in the gap
 *      carries its par the same way; by hand with nothing sold, and under
 *      its dial (QT), its holding runs off; a little over its dial, that much
 *      runs off and the rest is rolled; holding all of the paper, its par is
 *      issued it alone. Every month closes the audit, and a save and load
 *      keeps the new paper's holders and its book.
 *
 * And last year's surplus paying it too - Jerus's "Surplus pays everyone" -
 * on a city whose two-year serial is small against the surplus it runs, the
 * central bank at its dial:
 *
 *  20. Last year's surplus nets all of what falls due, the market's part
 *      first and then the central bank's par: a surplus the size of the
 *      slice pays it all, the central bank's par repaid and no add-on
 *      issued; a smaller one, past the market's part, leaves the central
 *      bank the remainder to roll, issued it alone - which leaves it over
 *      its dial, and the holdings step sells the excess to the bank. The
 *      one ledger carries what was netted once; every month closes the
 *      audit; a save and load keeps the paper, the book and the ledger.
 *
 * The numbers are labels, not the running order. The run prints 2, 3 and 4
 * first, on bare objects; then it builds a city, where 1 is held on every
 * month the harness plays and its closing assertions - every kind of flow
 * seen, every month closed - print after 5, which supplies the treasury's
 * kinds; then 6, 7, 9 and 8, because 8's reform scales the advances and
 * arrears 9's fixture leaves behind; then 10; and 11 to 20 last, in order.
 *
 * @author Jerus
 */
public class CentralBankCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-66s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-66s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            out.printf("%-66s OK%n", label);
        }
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }

    static void quietly(Runnable r) {
        System.setOut(quiet);
        try { r.run(); } finally { System.setOut(out); }
    }

    /* =====================================================================
       ONE MONTH, AUDITED - the identity section 1 asserts, held on every
       month this harness plays, with the kinds of flow counted.
       ===================================================================== */

    static final String[] KINDS = { "interest on reserves", "lent at the window",
            "repaid at the window", "the window's interest", "printed for the treasury",
            "repaid by the treasury", "the advances' interest", "the remittance" };
    static final boolean[] seen = new boolean[KINDS.length];
    static int monthsPlayed, monthsBroken, mostKindsInAMonth;
    static double worstResidual;

    static int kindsThisMonth(CentralBank cb) {
        double[] amounts = { cb.getInterestOnReserves(), cb.getAdvancedToBank(),
                cb.getRepaidByBank(), cb.getWindowInterest(), cb.getAdvancedToTreasury(),
                cb.getRepaidByTreasury(), cb.getAdvancesInterest(), cb.getRemitted() };
        int kinds = 0;
        for (int k = 0; k < amounts.length; k++) {
            if (amounts[k] > 0) { kinds++; seen[k] = true; }
        }
        return kinds;
    }

    /** Plays a month and holds it to the identities. Returns how many kinds of flow it had. */
    static int play(Game g) {
        double m0Before = g.getCentralBank().m0();
        quietly(g::toggleNextMonth);
        CentralBank cb = g.getCentralBank();
        MoneyAudit.Result r = g.getLastMoneyAudit();
        double made = cb.getIssued() - cb.getRetired();
        boolean closes = Math.abs(r.residual) <= .01 || r.relative() <= 1e-7;
        boolean sameMoney = Math.abs(r.moneyMade() - made) <= .005;
        boolean m0Moved = Math.abs((cb.m0() - m0Before) - made) <= .005;
        boolean keptNothing = Math.abs(cb.equity() - cb.getVault()
                - (cb.getRemittanceDue() - cb.getLossCarried())) <= .01;
        monthsPlayed++;
        worstResidual = Math.max(worstResidual, Math.abs(r.residual));
        if (!(closes && sameMoney && m0Moved && keptNothing)) {
            monthsBroken++;
            out.printf("   m%d: residual %.4f, audit made %.4f, bank made %.4f, M0 moved %.4f, kept %.4f%n",
                    g.getMonth(), r.residual, r.moneyMade(), made, cb.m0() - m0Before,
                    cb.equity() - cb.getVault() - (cb.getRemittanceDue() - cb.getLossCarried()));
        }
        int kinds = kindsThisMonth(cb);
        mostKindsInAMonth = Math.max(mostKindsInAMonth, kinds);
        return kinds;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 2. reserves earn the policy rate ================= */
        out.println("--- 2. the bank earns exactly the policy rate on its reserves ---");

        Bank low = new Bank();
        low.refresh(1, 100_000, 0, 0, 0, 0);
        low.injectCapital(50_000);
        double reservesLow = low.cashReserves();
        low.fundToCover(.03);
        close("its spare cash is paid the policy rate, a month of it",
                low.getPlacementIncome(), reservesLow * .03 / 12, 1e-9);

        Bank high = new Bank();
        high.refresh(1, 100_000, 0, 0, 0, 0);
        high.injectCapital(50_000);
        high.fundToCover(.10);
        close("...at 10% as at 3%", high.getPlacementIncome(), reservesLow * .10 / 12, 1e-9);
        // 0.7.7: savers are paid the share of the dial the bank's funding
        // asks for, moved DEPOSIT_RATE_SPEED of the way from where it was -
        // nothing, for a bank this new. It was DEPOSIT_PASS_THROUGH of what
        // the reserves earned.
        close("savers are paid the share of the dial its funding asks for, a step of the way there",
                high.depositRate(), Bank.DEPOSIT_RATE_SPEED * high.depositShare() * .10, 1e-12);
        assertTrue("...out of what its reserves earn",
                high.depositInterest() <= high.getPlacementIncome() + 1e-9);
        assertTrue("...so the deposit rate rises with the dial",
                high.depositRate() > low.depositRate() && low.depositRate() > 0);
        out.printf("   deposit rate %.3f%% at a 3%% dial, %.3f%% at 10%%%n",
                low.depositRate() * 100, high.depositRate() * 100);

        CentralBank books = new CentralBank();
        books.payInterestOnReserves(high.getPlacementIncome());
        close("...paid in money the central bank made", books.m0(), high.getPlacementIncome(), 1e-9);

        /* ================= 3. the window ================= */
        out.println("\n--- 3. the window prices at policy plus the penalty ---");

        Bank short_ = new Bank();
        short_.refresh(1, 100_000, 0, 0, 0, 0);
        short_.injectCapital(50_000);
        short_.setCash(-400_000);
        double gathered = short_.depositsGathered();
        double atWindow = short_.wholesaleFunding();
        close("past its deposits, what it borrows is the window's",
                atWindow, 400_000 - gathered, 1e-9);
        short_.fundToCover(.05);
        close("the window charges the dial plus the penalty",
                short_.fundingRate(), .05 + CentralBank.WINDOW_PENALTY, 1e-12);
        close("...on the window's tranche, and nothing on the deposits",
                short_.getFundingCost(), atWindow * (.05 + CentralBank.WINDOW_PENALTY) / 12, 1e-9);
        close("...and a bank that is borrowing has no reserves to be paid on",
                short_.getPlacementIncome(), 0, 0);

        CentralBank window = new CentralBank();
        window.settleWindow(atWindow);
        window.chargeWindow(short_.getFundingCost());
        close("the advance is money made", window.getIssued(), atWindow, 1e-9);
        close("...and the interest is the central bank's, and destroyed",
                window.getWindowInterest(), short_.getFundingCost(), 1e-9);
        close("...so M0 is what it lent less what it was paid",
                window.m0(), atWindow - short_.getFundingCost(), 1e-9);
        window.settleWindow(atWindow / 2);
        close("a smaller shortfall is a repayment", window.getRepaidByBank(), atWindow / 2, 1e-9);

        // A FAILED BANK AT THE WINDOW (0.7.1, decided rather than forgotten):
        // the resolution sets its equity to zero, and it is charged no
        // interest on what it still owes past its deposits - see the note in
        // Bank.fundToCover(). The advance stands; the charge does not.
        Bank failed = new Bank();
        failed.refresh(1, 100_000, 0, 500_000, 0, 0);   // a book, funded past its deposits
        failed.injectCapital(50_000);
        failed.setCash(-600_000);
        failed.resolveIfFailed();
        assertTrue("fixture: a bank past its deposits whose equity went under has failed",
                failed.isInsolvent() && failed.getFailures() == 1 && failed.wholesaleFunding() > 0);
        failed.fundToCover(.05);
        close("a failed bank is charged nothing at the window: its equity is zero by the resolution,"
                + " and a charge would fail it again", failed.getFundingCost(), 0, 0);
        CentralBank lender = new CentralBank();
        lender.settleWindow(failed.wholesaleFunding());
        lender.chargeWindow(failed.getFundingCost());
        close("...while the central bank goes on advancing what it owes", lender.getAdvancesToBank(),
                failed.wholesaleFunding(), 1e-9);
        close("...interest-free", lender.getWindowInterest(), 0, 0);

        /* ================= 4. the city's paper ================= */
        out.println("\n--- 4. the city's note prices at policy + spreads ---");

        DebtManager market = new DebtManager();
        market.setPolicyRate(.05);
        market.setGDP(9_068);
        market.setTaxRevenue(1_916);
        market.setCashPosition(0);
        market.setCostOfFunds(0);
        market.updateInterest();
        close("the floor is the policy rate itself", market.floorRate(), .05, 1e-12);
        close("...so a debt-free city is quoted the dial",
                market.getRate(), .05, 1e-12);
        market.addLongTermBond(300_000, 240, 1, .05);
        market.updateInterest();
        close("...and one that owes, the dial plus both spreads",
                market.getRate(),
                .05 + market.gdpSpread() + market.revenueSpread(), 1e-12);
        assertTrue("...where the spreads are real, not zero",
                market.gdpSpread() + market.revenueSpread() > 0);
        close("the ceiling sits on the dial too, not under it",
                market.ceilingRate(), .05 + 2 * DebtManager.maxSpreadPerMeasure(), 1e-12);
        close("what a different dial would be quoted is the same sum",
                market.rateAtPolicy(.08), .08 + market.gdpSpread() + market.revenueSpread(),
                1e-12);
        market.setCostOfFunds(.07);
        close("...and nothing lends the city below what the money costs the bank",
                market.floorRate(), .07 + Bank.MIN_MARGIN, 1e-12);

        /* ================= the city ================= */
        out.println("\n--- a city with a bank in it ---");

        GameFiles files = GameFiles.scratch("centralbankcheck");
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            city.getForeignAccounts().pinRate(1.0);
            // Roads first, and standing at once: nobody but the city owns
            // them, so the treasury has repairs of its own to pay - or to be
            // refused, in section 6.
            city.buildStack(template(city, "Gravel Road"), 6, true);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Commercial Bank"), 1, false);
            city.simulateMonths(72);
        });
        Bank bank = city.getBank();
        CentralBank cb = city.getCentralBank();
        DebtManager ledger = city.getDebtManager();
        out.printf("   m%d: %,.0f branch(es), cash $%,.0fk, deposits gathered $%,.0fk, treasury $%,.0fk%n",
                city.getMonth(), bank.getBranches(), bank.getCash(), bank.depositsGathered(),
                city.getCash());
        assertTrue("fixture: the city has a bank", bank.getBranches() >= 1 && !bank.isInsolvent());
        assertTrue("fixture: and a year of revenue for the ceiling", cb.trailingRevenue() > 0);

        /* ================= 1 and 5. every kind of flow ================= */
        out.println("\n--- 1. money made less money destroyed is the change in M0 ---");

        // Reserves: shareholders capitalise the bank until it has spare cash -
        // the treasury did, by a gift, until 0.7.14, when the gift went (the
        // city's capital is a resolution's or the preferred's now, and neither
        // is on offer to a standing bank over its minimum).
        final double[] putIn = new double[1];
        quietly(() -> {
            putIn[0] = bank.borrowings() + 150_000;
            bank.injectCapital(0, putIn[0]);
        });
        double put = putIn[0];
        assertTrue("fixture: the bank has spare cash to be paid on", put > 0 && bank.cashReserves() > 0);
        play(city);
        close("the central bank paid exactly what the bank booked on its reserves",
                cb.getInterestOnReserves(), bank.getPlacementIncome(), 1e-9);
        assertTrue("...and it paid something", cb.getInterestOnReserves() > 0);

        // The window: the treasury sells the bank more paper than it has money
        // for - twice over since 0.7.1, because the households take up to half
        // of an issue at the settle (HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE)
        // and the bank is the buyer of what is left. Twenty years: one of the
        // five terms (LongTermBond.MATURITIES).
        double ask = (bank.cashReserves() + bank.depositsGathered() + 100_000)
                / (1 - HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE);
        quietly(() -> city.handleLongBondLogic(ask, 20, 100));
        Debt bond = null;
        for (Debt d : ledger.getDebt()) if (!d.isForeign()) bond = d;
        assertTrue("fixture: the treasury sold the bank a bond", bond != null);
        play(city);
        assertTrue("the bank bought it at the window", cb.getAdvancedToBank() > 0);
        close("...which charges the dial plus the penalty",
                bank.fundingRate(), ledger.getPolicyRate() + CentralBank.WINDOW_PENALTY, 1e-12);
        close("...and its interest is the central bank's, to the cent",
                cb.getWindowInterest(), bank.getFundingCost(), 1e-9);

        /* ---- 5. the treasury runs dry ---- */
        out.println("\n--- 5. a broke treasury draws advances, and repays them first ---");

        /*
         * A GAP BIG ENOUGH, AT A DIAL HIGH ENOUGH, that a month of the
         * advances' interest is twice the loss the central bank is carrying -
         * so the month after next remits something, and the remittance is
         * shown arriving rather than asserted at zero.
         */
        ledger.setPolicyRate(DebtManager.MAX_POLICY_RATE);
        double gap = Math.max(300_000, 2 * 12 * cb.getLossCarried() / DebtManager.MAX_POLICY_RATE);
        city.setCashForTest(-gap);
        play(city);
        close("the shortfall is advanced whole, net of the remittance that arrived first",
                cb.getAdvancedToTreasury(), gap - cb.getRemitted(), 1e-6);
        close("...and owed", cb.getAdvancesToTreasury(), cb.getAdvancedToTreasury(), 1e-6);
        assertTrue("...and journalled as printing on the bridge",
                journalAmount(city, "Advanced by the central bank (printed)") > 0);

        // The month's close struck a profit or a loss; next month pays it.
        double owed = cb.getAdvancesToTreasury();
        double dueStruck = cb.getRemittanceDue();
        double policy = ledger.getPolicyRate();
        double half = owed / 2;
        city.setCashForTest(half);
        double lossBefore = cb.getLossCarried();
        int kinds = play(city);
        close("the advances' interest is the policy rate on what was owed",
                cb.getAdvancesInterest(), owed * policy / 12, 1e-6);
        close("the remittance is last month's profit, as struck", cb.getRemitted(), dueStruck, 1e-9);
        close("cash above zero repays the advances before anything else",
                cb.getRepaidByTreasury(),
                Math.min(owed, half - owed * policy / 12 + dueStruck), 1e-6);
        List<TreasuryJournal.Entry> journal = city.getTreasuryJournal();
        assertTrue("...the first thing the month's journal records",
                !journal.isEmpty() && journal.get(0).label().equals("Repaid the central bank"));
        double profit = cb.getWindowInterest() + cb.getAdvancesInterest() - cb.getInterestOnReserves();
        close("the month's profit is the interest it took less what reserves cost",
                cb.monthProfit(), profit, 1e-9);
        close("...and it is owed back to the treasury once any loss is made good",
                cb.getRemittanceDue(), Math.max(0, profit - lossBefore), 1e-9);
        close("...or carried, if it was a loss",
                cb.getLossCarried(), Math.max(0, lossBefore - profit), 1e-9);
        out.printf("   the treasury's month held %d kinds of the central bank's flow%n", kinds);

        assertTrue("fixture: a profit was struck to remit", cb.getRemittanceDue() > 0);
        double dueNext = cb.getRemittanceDue();

        // A buyback pays the bank that held the bond - its share of it, since
        // 0.7.1, the households theirs (HoldersCheck). The treasury is given
        // the money first, because its cash is part of what the market prices.
        double principal = bond.getOustandingPrincipal();
        double bankFace = bond.bankPrincipal();
        double bankUnearned = bond.unaccretedOn(bankFace);
        city.setCashForTest(2 * principal);
        double bankCash = bank.getCash(), bankBook = bank.getCityBook(), bankEquity = bank.equity();
        double price = city.quoteRepurchase(bond);
        double paid = city.repurchaseDebt(bond);
        double bankPrice = paid * bankFace / principal;
        close("a buyback: the bank's share of the price goes to its cash", bank.getCash() - bankCash,
                bankPrice, 1e-6);
        close("...its book drops by its share of the principal", bankBook - bank.getCityBook(),
                bankFace, 1e-6);
        close("...and the difference against what it carried the paper at is its gain or loss",
                bank.equity() - bankEquity, bankPrice - (bankFace - bankUnearned), 1e-6);
        close("...and was the price quoted", paid, price, 1e-9);
        ledger.setPolicyRate(DebtManager.NEUTRAL_RATE);
        play(city);
        assertTrue("with the bond sold back, the bank repays the window", cb.getRepaidByBank() > 0);
        close("...and the month's profit reaches the treasury the month after",
                cb.getRemitted(), dueNext, 1e-9);
        close("...as a revenue line on its budget",
                city.getEconomyManager().getNationalAccounts().getCentralBankRemittance(), dueNext, 1e-9);

        // Section 1's closing assertions, under section 1's own title: they
        // print after 5 because 5 supplies the treasury's kinds (0.7.1 - they
        // used to sit under 5's heading).
        out.println("\n--- 1, closed: every kind of flow, and every month ---");
        for (int k = 0; k < KINDS.length; k++) {
            assertTrue("the run had " + KINDS[k], seen[k]);
        }
        /*
         * FIVE IS AS MANY AS ONE MONTH CAN HOLD. Two pairs of the eight exclude
         * each other: at the settle the bank either has spare cash, which earns
         * interest on reserves, or is past its deposits, which the window
         * charges - and the treasury either draws or repays. So the fullest
         * month is one side of each pair with the three that always can.
         */
        out.printf("   the fullest month held %d of the %d kinds%n", mostKindsInAMonth, KINDS.length);
        assertTrue("...and a month with five kinds at once, which is all a month can hold",
                mostKindsInAMonth >= 5);
        assertTrue(String.format("every month of %d closed the audit, and M0 moved by exactly the"
                + " money made", monthsPlayed), monthsBroken == 0);

        /* ================= 6. the ceiling and the arrears ================= */
        out.println("\n--- 6. the ceiling binds, and the promises are paid first ---");

        double ceiling = cb.ceiling();
        city.setCashForTest(-(1.5 * ceiling + cb.getAdvancesToTreasury()));
        play(city);
        out.printf("   owes $%,.0fk against a ceiling of $%,.0fk%n", cb.getAdvancesToTreasury(), cb.ceiling());
        assertTrue("fixture: the advances are past the ceiling", cb.ceilingBound());
        assertTrue("fixture: the city owns something that needs repairing",
                city.getEconomyManager().getCityMaintenanceBill() > 0);
        close("its repairs were refused, cash being nothing",
                city.getCityMaintenancePaid(), 0, 1e-9);
        double owedRepairs = city.getArrearsByLine().getOrDefault(TreasuryLine.CITY_REPAIRS, 0.0);
        close("...and owed to the builders as arrears",
                owedRepairs, city.getEconomyManager().getCityMaintenanceBill(), 1e-6);
        close("...who were paid the rest of the bill and not that part",
                city.getSectors().construction().getRepairsThisMonth(),
                city.getEconomyManager().getMaintenanceBillTotal() - owedRepairs, 1e-6);

        // A promise the size of the ceiling itself, so there is no mistaking
        // what the next month's advance is for.
        city.setCashForTest(0);
        double promise = cb.ceiling();
        close("a promise is paid whatever the treasury holds",
                city.treasuryPays(TreasuryLine.PENSIONS, promise), promise, 0);
        close("...overdrawing it", city.getCash(), -promise, 1e-9);
        double arrearsBefore = city.getArrearsTotal();
        close("a discretionary line is refused",
                city.treasuryPays(TreasuryLine.BUSINESS_SUBSIDIES, 500,
                        city.getSectors().retail().key()), 0, 0);
        close("...and the refusal is owed", city.getArrearsTotal() - arrearsBefore, 500, 1e-9);
        close("a purchase is refused and nothing is owed",
                city.treasuryPays(TreasuryLine.LAND, 500) + (city.getArrearsTotal() - arrearsBefore - 500),
                0, 1e-9);
        final Game.BuildResult[] built = new Game.BuildResult[1];
        double cashNow = city.getCash();
        quietly(() -> built[0] = city.buildStack(template(city, "Gravel Road"), 1, false));
        assertTrue("...a building the treasury cannot pay for is not ordered",
                built[0] != Game.BuildResult.SUCCESS && city.getCash() == cashNow);
        close("...nor capital put into the bank", city.treasuryPays(TreasuryLine.BANK_CAPITAL, 1_000), 0, 0);
        close("...nor reserves bought", city.buyForeignCurrency(1_000), 0, 0);

        play(city);
        assertTrue("the promise's overdraft is advanced past the ceiling",
                cb.getAdvancedToTreasury() > 0 && cb.getAdvancesToTreasury() > cb.ceiling());

        out.println("\n--- ...and the arrears are paid down first when cash returns ---");
        double arrearsOwed = city.getArrearsTotal();
        double grantArrears = city.getArrearsByLine().getOrDefault(TreasuryLine.STUDENT_GRANTS, 0.0);
        assertTrue("fixture: the treasury owes arrears", arrearsOwed > 0);
        double advancesOwed = cb.getAdvancesToTreasury();
        city.setCashForTest(advancesOwed * 1.2 + arrearsOwed + 200_000);
        play(city);
        close("the central bank was repaid in full", cb.getAdvancesToTreasury(), 0, 1e-6);
        close("...and then the arrears", city.getArrearsTotal(), grantArrears, 1e-6);
        close("...every dollar of them", city.getArrearsPaidThisMonth(), arrearsOwed - grantArrears, 1e-6);
        journal = city.getTreasuryJournal();
        int repaidAt = -1, arrearsAt = -1;
        for (int i = 0; i < journal.size(); i++) {
            if (journal.get(i).label().equals("Repaid the central bank")) repaidAt = i;
            if (journal.get(i).label().equals("Paid down arrears")) arrearsAt = i;
        }
        assertTrue("...in that order", repaidAt >= 0 && arrearsAt > repaidAt);
        assertTrue(String.format("...and the audit closed on all %d months", monthsPlayed),
                monthsBroken == 0);

        /* ================= 7. the autopilot ================= */
        out.println("\n--- 7. the rule moves the dial, and the player's hand stops it ---");

        ledger.setPolicyRate(.20);
        ledger.setAutopilot(true);
        assertTrue("fixture: there is a year of prices to read", city.getPriceIndex().hasRate());
        double inflation = city.getPriceIndex().inflation();
        double advised = ledger.advisedPolicyRate(inflation);
        assertTrue("fixture: the rule wants something else", Math.abs(advised - .20) > 1e-6);
        play(city);
        close("with the rule's hand on it, the month opens with the dial where the rule says",
                ledger.getPolicyRate(), advised, 1e-12);
        ledger.takeTheDial(.07);
        assertTrue("the player's hand takes it back", !ledger.isAutopilot());
        play(city);
        close("...and the rule leaves it where the player put it", ledger.getPolicyRate(), .07, 1e-12);

        ledger.setAutopilot(true);
        final Game[] back = new Game[1];
        quietly(() -> {
            city.saveGame(4, "the rule's hand");
            back[0] = new Game(files);
            back[0].loadGameSave(4);
        });
        assertTrue("the toggle survives a save", back[0].getDebtManager().isAutopilot());
        ledger.takeTheDial(0);
        quietly(() -> {
            city.saveGame(5, "a dial at zero");
            back[0] = new Game(files);
            back[0].loadGameSave(5);
        });
        assertTrue("...off, too", !back[0].getDebtManager().isAutopilot());
        close("...and a dial at 0% reloads at 0%, not the 3% default",
                back[0].getDebtManager().getPolicyRate(), 0, 0);
        ledger.takeTheDial(.04);

        /* ================= 9. the save ================= */
        out.println("\n--- 9. everything survives a save ---");

        // Something to carry: the treasury broke again, arrears owed.
        city.setCashForTest(-(1.5 * cb.ceiling()));
        play(city);
        city.setCashForTest(0);
        city.treasuryPays(TreasuryLine.CONSTRUCTION_SUBSIDY, 250);
        assertTrue("fixture: advances and arrears to carry",
                cb.getAdvancesToTreasury() > 0 && city.hasArrears());
        quietly(() -> {
            city.saveGame(6, "the central bank's books");
            back[0] = new Game(files);
            back[0].loadGameSave(6);
        });
        Game reloaded = back[0];
        double[] was = cb.toSaveArray(), is = reloaded.getCentralBank().toSaveArray();
        boolean same = was.length == is.length;
        for (int i = 0; same && i < was.length; i++) same = Math.abs(was[i] - is[i]) <= 1e-9;
        assertTrue("the central bank's whole balance sheet reloads exactly", same);
        close("...M0", reloaded.getCentralBank().m0(), cb.m0(), 1e-9);
        close("...what the treasury owes it", reloaded.getCentralBank().getAdvancesToTreasury(),
                cb.getAdvancesToTreasury(), 1e-9);
        close("...the ceiling", reloaded.getCentralBank().ceiling(), cb.ceiling(), 1e-9);
        assertTrue("...and the arrears, line by line",
                reloaded.getArrearsByLine().equals(city.getArrearsByLine()));
        close("...and M2", reloaded.getM2(), city.getM2(), 1e-6);
        double[] m0History = reloaded.getHistorySave().aligned("m0");
        close("...and a year of M0 behind it", m0History[m0History.length - 1],
                Math.round(cb.m0() * 100) / 100.0, .005);

        /* ================= 8. a currency reform ================= */
        out.println("\n--- 8. a reform scales the money and not the ratios ---");

        double m0 = cb.m0(), adv = cb.getAdvancesToTreasury(), window_ = cb.getAdvancesToBank();
        double loss = cb.getLossCarried(), ceil = cb.ceiling(), unpaid = city.getArrearsTotal();
        double printed = cb.getPrintedLifetime(), m2 = city.getM2(), rate = ledger.getPolicyRate();
        final boolean[] reformed = new boolean[1];
        quietly(() -> reformed[0] = city.reformCurrencyForTest(100));
        assertTrue("fixture: the reform happened", reformed[0]);
        close("M0 is a hundredth", cb.m0(), m0 / 100, 1e-6);
        close("...the treasury's advances", cb.getAdvancesToTreasury(), adv / 100, 1e-6);
        close("...the bank's at the window", cb.getAdvancesToBank(), window_ / 100, 1e-6);
        close("...the loss carried", cb.getLossCarried(), loss / 100, 1e-6);
        close("...printed since founding", cb.getPrintedLifetime(), printed / 100, 1e-6);
        close("...the ceiling", cb.ceiling(), ceil / 100, 1e-6);
        close("...and the arrears", city.getArrearsTotal(), unpaid / 100, 1e-6);
        close("but the policy rate is the policy rate", ledger.getPolicyRate(), rate, 0);
        close("...the advances against the ceiling are where they were",
                cb.getAdvancesToTreasury() / cb.ceiling(), adv / ceil, 1e-9);
        close("...and M0 against M2", cb.m0() / city.getM2(), m0 / m2, 1e-9);
        int brokenBefore = monthsBroken;
        play(city);
        assertTrue("...and the first month in the new money closes", monthsBroken == brokenBefore);

        /* ================= 10. an old save ================= */
        out.println("\n--- 10. a save from before the central bank founds an empty one ---");

        quietly(() -> city.handleTBillLogic(20_000, Game.BUILD_NOTE_MONTHS, 1000.0));
        int notesBefore = 0;
        for (Debt d : ledger.getDebt()) if (d instanceof ShortTermTBill) notesBefore++;
        assertTrue("fixture: the city carries a note", notesBefore > 0);
        quietly(() -> city.saveGame(7, "before the central bank"));
        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                Files.readString(files.saveFile(7))).getAsJsonObject();
        for (String key : new String[] {"centralBank", "treasuryArrearsKeys",
                "treasuryArrearsAmounts", "policyAutopilot"}) {
            assertTrue("fixture: the save carried " + key, json.has(key));
            json.remove(key);
        }
        Files.writeString(files.saveFile(7), json.toString());
        quietly(() -> {
            back[0] = new Game(files);
            back[0].loadGameSave(7);
        });
        Game old = back[0];
        assertTrue("it loads", old.getLoadFailure() == null && old.getMonth() > 1);
        CentralBank founded = old.getCentralBank();
        close("with an empty central bank: nothing made", founded.m0(), 0, 0);
        close("...nothing advanced", founded.getAdvancesToTreasury() + founded.getAdvancesToBank(), 0, 0);
        close("...nothing printed", founded.getPrintedLifetime(), 0, 0);
        assertTrue("...nothing owed in arrears", !old.hasArrears());
        assertTrue("...and the player's hand on the dial", !old.getDebtManager().isAutopilot());
        int brokenOld = monthsBroken;
        for (int i = 0; i < Game.BUILD_NOTE_MONTHS + 1; i++) play(old);
        assertTrue("and it runs, closing the audit and the M0 identity every month",
                monthsBroken == brokenOld);
        int notesAfter = 0;
        for (Debt d : old.getDebtManager().getDebt()) if (d instanceof ShortTermTBill) notesAfter++;
        assertTrue("...and runs its note off", notesAfter < notesBefore);

        quietly(old::newGame);
        close("a new game after a load founds a fresh central bank",
                old.getCentralBank().m0() + old.getCentralBank().getPrintedLifetime(), 0, 0);
        assertTrue("...with nothing owed", !old.hasArrears());

        theHoldingsDial(files);
        theCeilingDial(files);
        theWholeBook();
        theSplitFloor();
        theRolloverAtIssue();
        theSurplusPaysEveryone();

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails);
    }

    /* =====================================================================
       11-15. THE HOLDINGS DIAL (0.7.1) - QE and QT, on a city of its own.
       ===================================================================== */

    /** The long end's shape over the note: the premium at twenty years less what the holdings compress. */
    static double shape20(DebtManager m) { return m.curveRate(240) - m.curveRate(6); }

    static void theHoldingsDial(GameFiles files) throws Exception {
        out.println("\n--- 11. the holdings dial buys the bank's term paper with money it makes ---");

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
        Bank bank = city.getBank();
        CentralBank cb = city.getCentralBank();
        DebtManager ledger = city.getDebtManager();

        /*
         * A BANK HOLDING $1,000k OF TWENTY-YEAR PAPER, ALL OF IT: the issue's
         * yield is marked at nothing over the bank's deposit rate, so the
         * households - who take their share at the settle when the paper pays
         * them more than the bank does (HoldersCheck) - take none, and the
         * bank is the whole of the market the dial buys from.
         */
        quietly(() -> city.handleLongBondLogic(1_000, 20, 100));
        Debt bond = null;
        for (Debt d : ledger.getDebt()) if (DebtManager.isTermPaper(d)) bond = d;
        assertTrue("fixture: the treasury sold a twenty-year bond", bond != null);
        bond.markIssued(bond.getSettleDue(), 0);
        play(city);
        close("fixture: ...and the bank holds all of it", bond.bankPrincipal(),
                bond.getOustandingPrincipal(), 1e-9);
        close("fixture: ...and the central bank none", cb.getPaperHeld(), 0, 0);
        close("with nothing held, the long end is the table's premium over the note",
                shape20(ledger), DebtManager.TERM_PREMIUM_20Y, 1e-12);

        // The dial at 30%, and what one month of it should buy, at the market
        // value the operation will see at the top of the month.
        cb.setTargetShare(.30);
        double term = ledger.termPrincipal();
        double face = CentralBank.QE_SPEED * .30 * term;
        double price = ledger.marketValue(bond) * face / bond.getOustandingPrincipal();
        double unearned = bond.unaccretedOn(face);
        double bookBefore = ledger.bankPrincipal();
        double m0Before = cb.m0();
        play(city);
        close("after one month the central bank holds QE_SPEED x 30% of it, at face",
                cb.getPaperHeld(), face, 1e-6);
        close("...which is what the paper says it holds", ledger.centralBankPrincipal(), cb.getPaperHeld(), 1e-9);
        close("it paid the market value at the curve", cb.getBoughtPaper(), price, 1e-6);
        close("...and the bank was paid exactly that", bank.getPaperSoldToCentralBank(), price, 1e-6);
        close("...money it made: the audit's issue carries the price",
                cb.getIssued() - (cb.getInterestOnReserves() + cb.getAdvancedToBank()
                        + cb.getAdvancedToTreasury() + cb.getRemitted()), price, 1e-6);
        close("...and M0 moved by exactly what it made less what it destroyed",
                cb.m0() - m0Before, cb.getIssued() - cb.getRetired(), 1e-6);
        close("the bank's book fell by the face", bookBefore - ledger.bankPrincipal(), face, 1e-6);
        close("...and its book on the bank's own sheet with it", bank.getCityBook(), ledger.bankPrincipal(), 1e-6);
        close("the bank booked its gain against what it carried the paper at",
                bank.getPaperGains(), price - (face - unearned), 1e-6);
        close("...and the central bank its own against face, into the month's profit",
                cb.getPaperGains(), face - price, 1e-6);
        double share = cb.getPaperHeld() / ledger.termPrincipal();
        close("compression(240) is the twenty-year premium times the share held over the share it is whole at",
                ledger.compression(240),
                DebtManager.TERM_PREMIUM_20Y * share / CentralBank.FULL_COMPRESSION_SHARE * CentralBank.QE_COMPRESSION, 1e-12);
        close("the twenty-year rate sits exactly compression(240) under the table",
                shape20(ledger), DebtManager.TERM_PREMIUM_20Y - ledger.compression(240), 1e-12);
        close("...and the note carries none of it", ledger.compression(6), 0, 0);
        close("...so the short end is where it was: the dial and the spreads",
                ledger.curveRate(6) - ledger.floorRate()
                        - ledger.gdpSpread() - ledger.revenueSpread(), 0, 1e-12);

        /* ---- 12. the coupon on its share ---- */
        out.println("\n--- 12. the coupon on its share is the central bank's, destroyed, and remitted ---");
        double lossBefore = cb.getLossCarried();
        play(city);
        double heldNow = bond.centralBankPrincipal();
        double coupon = bond.getMonthlyInterestExpense() * heldNow / bond.getOustandingPrincipal();
        close("the coupon on its share arrived at the central bank", cb.getPaperCoupons(), coupon, 1e-6);
        assertTrue("...and was destroyed with the rest of what it took back",
                cb.getPaperCoupons() > 0 && cb.getRetired() >= cb.getPaperCoupons());
        double profit = cb.getWindowInterest() + cb.getAdvancesInterest() + cb.getPaperCoupons()
                + cb.getPaperGains() - cb.getInterestOnReserves();
        close("the month's profit carries it", cb.monthProfit(), profit, 1e-9);
        double due = cb.getRemittanceDue();
        close("...owed back to the treasury once any loss is made good", due,
                Math.max(0, profit - lossBefore), 1e-9);
        play(city);
        close("...and remitted the month after", cb.getRemitted(), due, 1e-9);

        /* ---- 13. QT ---- */
        out.println("\n--- 13. the dial to nothing sells it back, and the curve returns to the table ---");
        double held = cb.getPaperHeld();
        assertTrue("fixture: the central bank holds some of the bond", held > 0);
        cb.setTargetShare(0);
        double retiredForIt = 0, bankPaid = 0;
        int months = 0;
        int speedMonths = (int) Math.ceil(1 / CentralBank.QE_SPEED);
        for (; months < speedMonths && cb.getPaperHeld() > 0; months++) {
            play(city);
            retiredForIt += cb.getSoldPaper();
            bankPaid += bank.getPaperBoughtFromCentralBank();
        }
        out.printf("   sold $%,.2fk of face back in %d month(s)%n", held, months);
        close("the dial to 0 sells it all back within the speed's months", cb.getPaperHeld(), 0, 1e-9);
        close("...the paper agrees", ledger.centralBankPrincipal(), 0, 1e-9);
        assertTrue("...a step at a time, not in one month", months > 1);
        close("money retired equals what the bank paid", retiredForIt, bankPaid, 1e-9);
        close("...and the curve returns to the table", shape20(ledger), DebtManager.TERM_PREMIUM_20Y, 1e-12);

        /* ---- 14. the save ---- */
        out.println("\n--- 14. the holdings survive a save ---");
        cb.setTargetShare(.30);
        play(city);
        play(city);
        assertTrue("fixture: holdings to carry", cb.getPaperHeld() > 0);
        // Half way through a move: the dial turned down while the setting
        // before it still sets the pace. A reload that forgot the setting
        // before would step at the new dial's pace alone (the 0.7.1 docs pass
        // found the load path doing exactly that).
        cb.setTargetShare(.10);
        assertTrue("fixture: the dial was moved, so the setting before it is not the dial",
                cb.getPreviousTarget() != cb.getTargetShare() && cb.getPreviousTarget() > 0);
        double stepBefore = cb.stepFor(ledger.termPrincipal());
        final Game[] back = new Game[1];
        quietly(() -> {
            city.saveGame(8, "the holdings");
            back[0] = new Game(files);
            back[0].loadGameSave(8);
        });
        CentralBank reloaded = back[0].getCentralBank();
        close("the paper it holds", reloaded.getPaperHeld(), cb.getPaperHeld(), 1e-9);
        close("...the dial", reloaded.getTargetShare(), cb.getTargetShare(), 0);
        close("...and the setting before it, which sets the pace", reloaded.getPreviousTarget(),
                cb.getPreviousTarget(), 0);
        close("...so the reloaded city steps at the same pace",
                reloaded.stepFor(back[0].getDebtManager().termPrincipal()), stepBefore, 1e-9);
        close("...what the paper says it holds", back[0].getDebtManager().centralBankPrincipal(),
                ledger.centralBankPrincipal(), 1e-9);
        close("...and the long end of the curve", shape20(back[0].getDebtManager()), shape20(ledger), 1e-12);

        /* ---- 15. a reform ---- */
        out.println("\n--- 15. a reform scales the holdings and not the dial ---");
        double paper = cb.getPaperHeld(), dial = cb.getTargetShare(), comp = ledger.compression(240);
        double onPaper = ledger.centralBankPrincipal();
        final boolean[] reformed = new boolean[1];
        quietly(() -> reformed[0] = city.reformCurrencyForTest(100));
        assertTrue("fixture: the reform happened", reformed[0]);
        close("the paper it holds is a hundredth", cb.getPaperHeld(), paper / 100, 1e-9);
        close("...on the paper too", ledger.centralBankPrincipal(), onPaper / 100, 1e-9);
        close("the dial does not move", cb.getTargetShare(), dial, 0);
        close("...nor the compression it buys", ledger.compression(240), comp, 1e-12);
        int brokenBefore = monthsBroken;
        play(city);
        assertTrue("...and the first month in the new money closes", monthsBroken == brokenBefore);
        assertTrue(String.format("every month the holdings were played closed the audit, and M0"
                + " moved by exactly the money made (%d months in all)", monthsPlayed), monthsBroken == 0);
    }

    /* =====================================================================
       16. THE CEILING AS A DIAL (0.7.2) - on a city of its own, with no road
       or grant of the city's that a refusal could leave owed, so the only
       thing between the treasury and a draw is the ceiling.
       ===================================================================== */

    static void theCeilingDial(GameFiles files) throws Exception {
        out.println("\n--- 16. the ceiling is the player's dial, up to three years of revenue ---");

        Game city = new Game(files);
        quietly(() -> {
            city.run();
            // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
            // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
            // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getForeignAccounts().pinRate(1.0);
            city.buildStack(template(city, "House"), 300, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Industrial Bakery"), 2, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.simulateMonths(30);
        });
        CentralBank cb = city.getCentralBank();
        close("a city opens at the default", cb.getAdvancesCeilingMonths(),
                CentralBank.DEFAULT_ADVANCES_MONTHS, 0);
        assertTrue("fixture: a year of revenue to set it on, nothing owed, nothing in arrears",
                cb.trailingRevenue() > 0 && cb.getAdvancesToTreasury() == 0 && !city.hasArrears());

        /*
         * AT THE OLD CEILING: overdrawn by exactly six months of revenue, so
         * the room for anything that is not a promise is nothing. A purchase
         * (land: refused, nothing owed) is how the room is read.
         */
        double atSix = cb.ceiling();
        close("...which is DEFAULT_ADVANCES_MONTHS of trailing revenue", atSix,
                CentralBank.DEFAULT_ADVANCES_MONTHS * cb.trailingRevenue(), 1e-9);
        city.setCashForTest(-atSix);
        close("fixture: overdrawn by the whole ceiling, the treasury has no room",
                city.discretionaryRoom(), 0, 1e-6);
        close("...so a purchase is refused", city.treasuryPays(TreasuryLine.LAND, 500), 0, 0);

        cb.setAdvancesCeilingMonths(12);
        close("set to twelve months, the ceiling doubles", cb.ceiling(), 2 * atSix, 1e-6);
        close("...and the room is the six months it added", city.discretionaryRoom(), atSix, 1e-6);
        close("...so the same purchase is paid, overdrawing further",
                city.treasuryPays(TreasuryLine.LAND, 500), 500, 0);
        double drawBefore = cb.getAdvancesToTreasury();
        play(city);
        out.printf("   drew $%,.0fk at the top of the month against a ceiling of $%,.0fk (twelve months)%n",
                cb.getAdvancedToTreasury(), cb.ceiling());
        assertTrue("a treasury that was at the old ceiling draws past it",
                cb.getAdvancesToTreasury() - drawBefore > atSix);
        assertTrue("...and is still inside the new one", !cb.ceilingBound());
        cb.setAdvancesCeilingMonths(CentralBank.MAX_ADVANCES_CEILING * 10);
        close("the dial stops at MAX_ADVANCES_CEILING", cb.getAdvancesCeilingMonths(),
                CentralBank.MAX_ADVANCES_CEILING, 0);
        cb.setAdvancesCeilingMonths(-3);
        close("...and at nothing below", cb.getAdvancesCeilingMonths(), 0, 0);
        cb.setAdvancesCeilingMonths(12);

        final Game[] back = new Game[1];
        quietly(() -> {
            city.saveGame(9, "the ceiling at a year");
            back[0] = new Game(files);
            back[0].loadGameSave(9);
        });
        close("the setting survives a save", back[0].getCentralBank().getAdvancesCeilingMonths(), 12, 0);
        close("...and so the ceiling", back[0].getCentralBank().ceiling(), cb.ceiling(), 1e-9);

        com.google.gson.JsonObject json = com.google.gson.JsonParser.parseString(
                Files.readString(files.saveFile(9))).getAsJsonObject();
        assertTrue("fixture: the save carried the dial under its own key", json.has("advancesCeilingMonths"));
        json.remove("advancesCeilingMonths");
        Files.writeString(files.saveFile(9), json.toString());
        quietly(() -> {
            back[0] = new Game(files);
            back[0].loadGameSave(9);
        });
        close("a save from before the dial reads the default: six months, the constant it was",
                back[0].getCentralBank().getAdvancesCeilingMonths(), CentralBank.DEFAULT_ADVANCES_MONTHS, 0);

        double ceilingBefore = cb.ceiling();
        final boolean[] reformed = new boolean[1];
        quietly(() -> reformed[0] = city.reformCurrencyForTest(100));
        assertTrue("fixture: the reform happened", reformed[0]);
        close("a reform does not move the dial: months are not money", cb.getAdvancesCeilingMonths(), 12, 0);
        close("...while the ceiling, which is money, is a hundredth", cb.ceiling(), ceilingBefore / 100, 1e-6);
        assertTrue(String.format("every month the ceiling was played closed the audit, and M0"
                + " moved by exactly the money made (%d months in all)", monthsPlayed), monthsBroken == 0);
    }

    /** The amount on last month's journal line with this label, or 0. */
    static double journalAmount(Game g, String label) {
        for (TreasuryJournal.Entry e : g.getTreasuryJournal()) {
            if (e.label().equals(label)) return e.amount();
        }
        return 0;
    }

    /* =====================================================================
       17-18. THE WHOLE BOOK AND THE SPLIT FLOOR (0.7.15), on bare debt
       markets whose holdings are set to the exact share - the paper's own
       holder field, moved as the open-market operation moves it.
       ===================================================================== */

    static void theWholeBook() {
        out.println("\n--- 17. the dial takes the whole of the paper, and the compression is whole from half ---");
        CentralBank bare = new CentralBank();
        bare.setTargetShare(1.0);
        close("the dial takes MAX_QE_SHARE", bare.getTargetShare(), CentralBank.MAX_QE_SHARE, 0);
        close("...which is all of the term paper, Jerus's 100%", CentralBank.MAX_QE_SHARE, 1.0, 0);
        bare.setTargetShare(1.5);
        close("...and no more", bare.getTargetShare(), CentralBank.MAX_QE_SHARE, 0);
        bare.restoreTargetShare(1.0);
        close("...as the load path restores it, unclamped at a half", bare.getTargetShare(), 1.0, 0);

        DebtManager market = new DebtManager();
        Debt bond = market.addLongTermBond(1_000, 240, 1, .05);
        double full = DebtManager.termPremium(240) * CentralBank.QE_COMPRESSION;
        close("fixture: twenty-year paper carries the table's twenty-year premium",
                DebtManager.termPremium(240), DebtManager.TERM_PREMIUM_20Y, 0);
        bond.moveToCentralBank(250);
        close("at a quarter held, half the premium, as it always was",
                market.compression(240), full * .25 / CentralBank.FULL_COMPRESSION_SHARE, 0);
        for (double held : new double[] { .5, .75, 1.0 }) {
            bond.moveToCentralBank(1_000 * held - bond.centralBankPrincipal());
            close(String.format("at %.0f%% held, the whole premium: 0.7.14's full compression", held * 100),
                    market.compression(240), full, 0);
        }
        close("...so the long end over the note is flat at the whole book",
                market.curveRate(240) - market.curveRate(6), 0, 1e-15);
    }

    static void theSplitFloor() {
        out.println("\n--- 18. the floor is split by who holds the paper ---");
        double policy = .05;
        // A bank at the window: its money costs the policy rate and the window's penalty.
        double costOfFunds = policy + CentralBank.WINDOW_PENALTY;
        double bankFloor = Math.max(policy, costOfFunds + Bank.MIN_MARGIN);   // the floor until 0.7.15
        DebtManager market = new DebtManager();
        market.setPolicyRate(policy);
        market.setGDP(9_068);
        market.setTaxRevenue(1_916);
        market.setCashPosition(0);
        market.setCostOfFunds(costOfFunds);
        Debt term = market.addLongTermBond(750, 240, 1, .05);
        Debt note = market.addShortTermTBill(250, 6, 1);
        market.updateInterest();
        assertTrue("fixture: the bank at the window - its cost of funds over the policy rate",
                market.getCostOfFunds() > market.getPolicyRate());
        close("the central bank holding nothing, the floor is 0.7.14's, to the bit",
                market.floorRate(), bankFloor, 0);
        close("...and so is the bank's own floor beside it", market.bankFloorRate(), bankFloor, 0);
        double shortEndBefore = market.curveRate(6) - market.gdpSpread() - market.revenueSpread();

        term.moveToCentralBank(750);
        close("fixture: it holds all the term paper", market.centralBankShareOfTerm(), 1, 0);
        close("...which is three quarters of the city's paper, the note the bank's",
                market.centralBankShareOfPaper(), .75, 0);
        close("the floor is the policy rate on its share and the bank's on the rest",
                market.floorRate(), policy + .25 * (bankFloor - policy), 1e-15);
        close("...and the bank's own floor has not moved", market.bankFloorRate(), bankFloor, 0);
        close("the short end moves with it: the note sits on the split floor",
                market.curveRate(6) - market.gdpSpread() - market.revenueSpread(), market.floorRate(), 1e-15);
        assertTrue("...lower than it was with the bank holding everything",
                market.floorRate() < shortEndBefore);
        close("what another dial would be quoted splits the same way",
                market.rateAtPolicy(.06) - market.gdpSpread() - market.revenueSpread(),
                .06 + .25 * (Math.max(.06, costOfFunds + Bank.MIN_MARGIN) - .06), 1e-15);

        boolean proportional = true;
        for (double held : new double[] { .25, .5, .75 }) {
            term.moveToCentralBank(1_000 * held - term.centralBankPrincipal());
            double s = market.centralBankShareOfPaper();
            proportional &= s == held
                    && Math.abs(market.floorRate() - (policy + (1 - s) * (bankFloor - policy))) <= 1e-15;
        }
        assertTrue("in between, proportional: a quarter, a half and three quarters held", proportional);

        // All of the paper: no note outstanding, and the central bank holding every term piece.
        DebtManager whole = new DebtManager();
        whole.setPolicyRate(policy);
        whole.setGDP(9_068);
        whole.setTaxRevenue(1_916);
        whole.setCashPosition(0);
        whole.setCostOfFunds(costOfFunds);
        Debt only = whole.addLongTermBond(1_000, 240, 1, .05);
        only.moveToCentralBank(1_000);
        close("the central bank holding all of it, the floor is the policy rate",
                whole.floorRate(), policy, 0);
        close("...whatever the bank's money costs it", whole.bankFloorRate(), bankFloor, 0);
        whole.setCostOfFunds(costOfFunds + .10);
        close("...even ten points dearer", whole.floorRate(), policy, 0);
    }

    /* =====================================================================
       19. THE CENTRAL BANK ROLLS ITS OWN, AT ISSUE (0.7.15, round 2) - a
       city with a two-year serial the households, the bank and the central
       bank each hold part of, saved three months before its first slice and
       played to it once for every case, each from that save.
       ===================================================================== */

    /** The scratch slot this section's saves go to - the assistant's slot, in a scratch folder. */
    static final int ROLL_SLOT = 10;

    /** The serial the fixture sold: the one piece of the city's paper that is a serial and started before the month the fixture saved in. */
    static Debt fixtureSerial(Game g, int soldIn) {
        for (Debt d : g.getDebtManager().getDebt()) {
            if (d instanceof MediumTermBond && !d.isForeign() && d.getMonthStarted() == soldIn) return d;
        }
        return null;
    }

    /** The city's own paper sold at the last press or between it and the one before - started the month before this one. */
    static java.util.List<Debt> soldAtTheLastPress(Game g) {
        java.util.List<Debt> sold = new java.util.ArrayList<>();
        for (Debt d : g.getDebtManager().getDebt()) {
            if (!d.isForeign() && d.getMonthStarted() == g.getMonth() - 1) sold.add(d);
        }
        return sold;
    }

    /** What the central bank paid a unit of face for a piece at its issue: what the treasury was paid for it over its face, which the add-on does not move. */
    static double issuePricePerFace(Debt d) {
        return 1 - d.getIssueDiscount() / d.getOustandingPrincipal();
    }

    /** The fixture as saved, loaded fresh, and played to the gap before its first slice. */
    static Game toTheSlice(GameFiles files, int soldIn) {
        Game g = new Game(files);
        quietly(() -> g.loadGameSave(ROLL_SLOT));
        Debt serial = fixtureSerial(g, soldIn);
        while (!(serial.principalDueNextMonth() > 0)) play(g);
        return g;
    }

    static void theRolloverAtIssue() throws Exception {
        out.println("\n--- 19. what the central bank holds of a maturing piece it takes again at issue, par for par ---");
        GameFiles files = GameFiles.scratch("centralbankcheck-rollover");
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
        city.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        final int soldIn = city.getMonth();
        quietly(() -> city.handleMediumBondLogic(20_000, 2, 100));
        Debt serial = fixtureSerial(city, soldIn);
        assertTrue("fixture: the treasury sold a two-year serial", serial != null);
        play(city);                                        // the settle: the households take their share
        double dial = .5;
        city.getCentralBank().setTargetShare(dial);
        while (serial.getRemainingMonths() > 16) play(city);
        assertTrue("fixture: the households, the bank and the central bank each hold part of it",
                serial.householdPrincipal() > 0 && serial.bankPrincipal() > 0 && serial.centralBankPrincipal() > 0);
        close("fixture: ...the central bank its dial's share of the term paper, and no more",
                city.getCentralBank().getPaperHeld(), dial * city.getDebtManager().termPrincipal(), 1e-6);
        quietly(() -> city.saveGame(ROLL_SLOT, "three months before the slice"));
        out.printf("   m%d: the serial $%,.0fk - the households $%,.0fk, the bank $%,.0fk, the central bank $%,.0fk%n",
                city.getMonth(), serial.getOustandingPrincipal(), serial.householdPrincipal(),
                serial.bankPrincipal(), serial.centralBankPrincipal());

        /* ---- at its dial: its par added on to the rollover's issue ---- */
        Game a = toTheSlice(files, soldIn);
        Debt sa = fixtureSerial(a, soldIn);
        CentralBank cba = a.getCentralBank();
        a.setCashForTest(0);                               // no cash for S to net: the market's part is issued
        double slice = sa.principalDueNextMonth();
        double itsPar = slice * sa.centralBankPrincipal() / sa.getOustandingPrincipal();
        double heldBefore = cba.getPaperHeld();
        Rollover.Plan plan = a.rolloverPlan();
        assertTrue("fixture: a slice falls due next month and the central bank holds part of it",
                itsPar > 0 && itsPar < slice && a.centralBankOverItsDial() == 0);
        close("the plan names its par in what falls due", plan.centralBankDue(), itsPar, 1e-9);
        close("...and, at its dial, rolls all of it", plan.centralBankPar(), itsPar, 0);
        close("...so the market's issue is sized for the rest, less what is netted", plan.toRoll(),
                slice - itsPar - plan.netted(), 1e-9);
        assertTrue("fixture: ...nothing netted, and the rest is issued", plan.netted() == 0 && plan.issues().size() == 1);
        play(a);
        java.util.List<Debt> sold = soldAtTheLastPress(a);
        assertTrue("the rollover sold one piece, a serial like the one falling due",
                sold.size() == 1 && sold.get(0) instanceof MediumTermBond);
        Debt fresh = sold.get(0);
        close("it holds exactly its maturing par of the new paper, from issue", fresh.centralBankPrincipal(), itsPar, 1e-6);
        close("...which is what its books say it rolled", cba.getParAtIssue(), itsPar, 1e-6);
        close("...paid at the issue's price on each unit of face", cba.getBoughtAtIssue(),
                itsPar * issuePricePerFace(fresh), 1e-6);
        assertTrue("...under par, as the market paid", cba.getBoughtAtIssue() < itsPar);
        close("the bank's book of the new issue excludes its par: the bank and the households hold the market's face",
                fresh.bankPrincipal() + fresh.householdPrincipal(), fresh.getOustandingPrincipal() - itsPar, 1e-6);
        assertTrue("...and the households took part of it", fresh.householdPrincipal() > 0);
        close("...for which they and the bank paid what the rollover raised, and no more",
                a.getCityPaperSettled() + a.getHouseholdsBoughtPaper(), a.getRollover().getLastRaised(), 1e-6);
        close("...which is what the bank's own book carries", a.getBank().getCityBook(),
                a.getDebtManager().bankBook(), 1e-6);
        close("...and the households' cells hold what the paper says they do",
                a.getHouseholdBalance().totalPaper(), a.getDebtManager().householdPrincipal(), 1e-6);
        close("the treasury repaid it its maturing par, as it always has", cba.getPaperRedeemed(), itsPar, 1e-6);
        close("so it holds what it held: the replacement in, the maturing par out", cba.getPaperHeld(), heldBefore, 1e-6);
        double termNow = a.getDebtManager().termPrincipal();
        double capitalised = fresh.getOustandingPrincipal() - slice;
        double heldNow = cba.getPaperHeld();
        play(a);
        close("a month on it buys its dial's share of what the roll capitalised, and not its par back",
                cba.getPaperHeld() - heldNow, dial * capitalised, 1e-6);
        close("...which puts it at its dial of the term paper", cba.getPaperHeld(), dial * termNow, 1e-6);

        /* ---- by hand: an issue in the gap carries its par on top ---- */
        Game h = toTheSlice(files, soldIn);
        h.setRolloverMode(Rollover.Mode.MANUAL);
        Debt sh = fixtureSerial(h, soldIn);
        double handPar = sh.principalDueNextMonth() * sh.centralBankPrincipal() / sh.getOustandingPrincipal();
        close("by hand, the plan still names its par", h.rolloverPlan().centralBankPar(), handPar, 1e-9);
        quietly(() -> h.handleLongBondLogic(5_000, 20, 100));
        play(h);
        java.util.List<Debt> byHand = soldAtTheLastPress(h);
        assertTrue("fixture: by hand, the one term loan the player sold in the gap",
                byHand.size() == 1 && byHand.get(0) instanceof LongTermBond);
        close("the player's issue the month its holding falls due carries its par on top",
                byHand.get(0).centralBankPrincipal(), handPar, 1e-6);
        close("...at that issue's price on each unit of face", h.getCentralBank().getBoughtAtIssue(),
                handPar * issuePricePerFace(byHand.get(0)), 1e-6);

        /* ---- by hand, nothing sold: no issue, and it runs off ---- */
        Game m = toTheSlice(files, soldIn);
        m.setRolloverMode(Rollover.Mode.MANUAL);
        Debt sm = fixtureSerial(m, soldIn);
        double offPar = sm.principalDueNextMonth() * sm.centralBankPrincipal() / sm.getOustandingPrincipal();
        double heldM = m.getCentralBank().getPaperHeld();
        play(m);
        close("by hand with nothing sold, there is nothing to add it on to", m.getCentralBank().getParAtIssue(), 0, 0);
        close("...and its holding runs off, repaid it", m.getCentralBank().getPaperRedeemed(), offPar, 1e-6);
        close("...off its book", heldM - m.getCentralBank().getPaperHeld(), offPar, 1e-6);

        /* ---- its QT: the dial under what it holds, and it runs off ---- */
        Game q = toTheSlice(files, soldIn);
        q.getCentralBank().setTargetShare(0);
        Debt sq = fixtureSerial(q, soldIn);
        q.setCashForTest(0);
        double qtSlice = sq.principalDueNextMonth();
        double qtPar = qtSlice * sq.centralBankPrincipal() / sq.getOustandingPrincipal();
        assertTrue("fixture: its dial under what it holds, by more than its par in the slice",
                q.centralBankOverItsDial() >= qtPar && qtPar > 0);
        Rollover.Plan qtPlan = q.rolloverPlan();
        close("in its QT the plan rolls nothing of its own", qtPlan.centralBankPar(), 0, 0);
        close("...all of its par runs off", qtPlan.centralBankRunsOff(), qtPar, 1e-9);
        close("...and the market's issue is sized for all of what falls due, less what is netted",
                qtPlan.toRoll(), qtSlice - qtPlan.netted(), 1e-9);
        play(q);
        close("it takes nothing at issue", q.getCentralBank().getParAtIssue(), 0, 0);
        double qtOnNew = 0;
        for (Debt d : soldAtTheLastPress(q)) qtOnNew += d.centralBankPrincipal();
        close("...holds none of the new paper", qtOnNew, 0, 0);
        close("...and what it held of the slice is repaid it", q.getCentralBank().getPaperRedeemed(), qtPar, 1e-6);

        /* ---- at the margin: a little over its dial, that much runs off ---- */
        Game p = toTheSlice(files, soldIn);
        Debt sp = fixtureSerial(p, soldIn);
        p.setCashForTest(0);
        double pSlice = sp.principalDueNextMonth();
        double pPar = pSlice * sp.centralBankPrincipal() / sp.getOustandingPrincipal();
        double pHeld = p.getCentralBank().getPaperHeld();
        p.getCentralBank().setTargetShare((pHeld - pPar / 2) / p.getDebtManager().termPrincipal());
        double over = p.centralBankOverItsDial();
        assertTrue("fixture: over its dial by less than its par in the slice", over > 0 && over < pPar);
        Rollover.Plan pPlan = p.rolloverPlan();
        close("over its dial, it rolls its par less how far over it is", pPlan.centralBankPar(), pPar - over, 1e-6);
        close("...that much runs off", pPlan.centralBankRunsOff(), over, 1e-6);
        close("...and the market's issue is sized for the rest", pPlan.toRoll(),
                pSlice - pPlan.centralBankPar() - pPlan.netted(), 1e-9);
        play(p);
        double pOnNew = 0;
        for (Debt d : soldAtTheLastPress(p)) pOnNew += d.centralBankPrincipal();
        close("it holds that much of the new paper", pOnNew, pPar - over, 1e-6);
        close("...so what it held past its dial ran off, and the rest was rolled",
                p.getCentralBank().getPaperHeld(), pHeld - over, 1e-6);

        /* ---- all of the paper: nothing for the market, its par alone ---- */
        Game w = toTheSlice(files, soldIn);
        Debt sw = fixtureSerial(w, soldIn);
        w.getCentralBank().setTargetShare(1.0);
        w.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        // The dial at the whole of it from here, played to the next slice, a year on.
        play(w);
        while (!(sw.principalDueNextMonth() > 0)) play(w);
        w.setCashForTest(0);
        close("fixture: it holds all of the term paper", w.getDebtManager().centralBankShareOfTerm(), 1, 1e-12);
        double allPar = sw.principalDueNextMonth();
        double wHeld = w.getCentralBank().getPaperHeld();
        Rollover.Plan wPlan = w.rolloverPlan();
        close("the plan rolls all of what falls due as its own", wPlan.centralBankPar(), allPar, 1e-9);
        assertTrue("...so there is nothing for the market to be sold", wPlan.issues().isEmpty() && wPlan.toRoll() == 0);
        play(w);
        java.util.List<Debt> alone = soldAtTheLastPress(w);
        assertTrue("the rollover issues it one serial like the one falling due",
                alone.size() == 1 && alone.get(0) instanceof MediumTermBond
                        && alone.get(0).getDuration() == sw.getDuration());
        close("...its par exactly, all of it the central bank's", alone.get(0).centralBankPrincipal(), allPar, 1e-6);
        close("...none of it the bank's", alone.get(0).bankPrincipal(), 0, 1e-9);
        close("...paid for at its quote's price on each unit of face", w.getCentralBank().getBoughtAtIssue(),
                allPar * issuePricePerFace(alone.get(0)), 1e-6);
        assertTrue("...under par, and settled: nobody owes for it", w.getCentralBank().getBoughtAtIssue() < allPar
                && alone.get(0).getSettleDue() == 0);
        close("so it holds what it held, the whole of the term paper", w.getCentralBank().getPaperHeld(), wHeld, 1e-6);
        close("...all of it", w.getDebtManager().centralBankShareOfTerm(), 1, 1e-12);

        /* ---- a save and load after the roll: it keeps it all, and both go on the same ---- */
        final Game[] back = new Game[1];
        quietly(() -> {
            a.saveGame(ROLL_SLOT, "rolled at issue");
            back[0] = new Game(files);
            back[0].loadGameSave(ROLL_SLOT);
        });
        Game twin = back[0];
        Debt freshBack = null;
        for (Debt d : twin.getDebtManager().getDebt()) {
            if (d.getMonthStarted() == fresh.getMonthStarted() && d instanceof MediumTermBond) freshBack = d;
        }
        assertTrue("through a save: the new paper is there", freshBack != null);
        close("...the central bank's par in it", freshBack.centralBankPrincipal(), fresh.centralBankPrincipal(), 0);
        close("...the households'", freshBack.householdPrincipal(), fresh.householdPrincipal(), 0);
        close("...its discount still to accrete", freshBack.getDiscountLeft(), fresh.getDiscountLeft(), 0);
        close("...the central bank's book", twin.getCentralBank().getPaperHeld(), cba.getPaperHeld(), 0);
        close("...what it has rolled at issue since founding", twin.getCentralBank().getParAtIssueLifetime(),
                cba.getParAtIssueLifetime(), 0);
        close("...and paid for it", twin.getCentralBank().getBoughtAtIssueLifetime(), cba.getBoughtAtIssueLifetime(), 0);
        play(a);
        play(twin);
        close("a month on, both hold the same", twin.getCentralBank().getPaperHeld(), cba.getPaperHeld(), 1e-9);
        close("...and have made the same money", twin.getCentralBank().m0(), cba.m0(), 1e-6);

        assertTrue(String.format("every month of the rollover's branches closed the audit, and M0"
                + " moved by exactly the money made (%d months in all)", monthsPlayed), monthsBroken == 0);
    }

    /* =====================================================================
       20. THE SURPLUS PAYS EVERYONE (0.7.15, round 3) - a city with a
       two-year serial small against the surplus it runs, the central bank
       at its dial, saved at the press before the first slice; each case
       from that save.
       ===================================================================== */

    static void theSurplusPaysEveryone() throws Exception {
        out.println("\n--- 20. last year's surplus pays the market's part first, then the central bank's par ---");
        GameFiles files = GameFiles.scratch("centralbankcheck-surplus");
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
        city.setRolloverMode(Rollover.Mode.SAME_STRUCTURE);
        final int soldIn = city.getMonth();
        quietly(() -> city.handleMediumBondLogic(400, 2, 100));
        Debt serial = fixtureSerial(city, soldIn);
        assertTrue("fixture: the treasury sold a small two-year serial", serial != null);
        play(city);
        double dial = .5;
        city.getCentralBank().setTargetShare(dial);
        while (!(serial.principalDueNextMonth() > 0)) play(city);
        double slice = serial.principalDueNextMonth();
        double itsPar = slice * serial.centralBankPrincipal() / serial.getOustandingPrincipal();
        double market = slice - itsPar;
        double unused = city.surplusOverLastYear() - city.getRollover().usedInYear(city.getMonth())
                - city.fundReservation();
        out.printf("   m%d: the slice $%,.1fk, the central bank's par in it $%,.1fk; last year's surplus unused $%,.1fk%n",
                city.getMonth(), slice, itsPar, unused);
        assertTrue("fixture: the central bank at its dial holds part of the slice",
                itsPar > 0 && itsPar < slice && city.centralBankOverItsDial() == 0);
        assertTrue("fixture: last year's surplus, unused, is more than all of the slice", unused >= slice);
        quietly(() -> city.saveGame(ROLL_SLOT, "at the press before the slice"));

        /* ---- a surplus the size of the slice pays it all ---- */
        Game big = new Game(files);
        quietly(() -> big.loadGameSave(ROLL_SLOT));
        Debt sb = fixtureSerial(big, soldIn);
        CentralBank cbb = big.getCentralBank();
        big.setCashForTest(10 * slice);                    // the cash no bound: S is the slice
        Rollover.Plan bigPlan = big.rolloverPlan();
        close("S nets all of what falls due", bigPlan.netted(), slice, 1e-9);
        close("...the market's part first, then all of the central bank's par", bigPlan.centralBankNetted(), itsPar, 1e-9);
        close("...so it rolls nothing at issue", bigPlan.centralBankPar(), 0, 0);
        assertTrue("...and nothing is sold to the market", bigPlan.issues().isEmpty() && bigPlan.toRoll() == 0);
        double heldBig = cbb.getPaperHeld();
        play(big);
        close("no add-on is issued", cbb.getParAtIssue(), 0, 0);
        assertTrue("...nor any other paper", soldAtTheLastPress(big).isEmpty());
        close("its maturing par is paid off, repaid it at the maturity", cbb.getPaperRedeemed(), itsPar, 1e-6);
        close("...and off its book", heldBig - cbb.getPaperHeld(), itsPar, 1e-6);
        close("the one ledger carries what was netted, once", big.getRollover().getLastNetted(), slice, 1e-9);
        close("...and the serial is down by the slice", sb.getOustandingPrincipal(), serial.getOustandingPrincipal() - slice, 1e-6);

        /* ---- a smaller surplus: the market's part, then part of the central bank's ---- */
        Game small = new Game(files);
        quietly(() -> small.loadGameSave(ROLL_SLOT));
        CentralBank cbs = small.getCentralBank();
        double s = market + itsPar / 2;
        small.setCashForTest(s);                           // the cash the bound: S is the market's part and half the par
        Rollover.Plan smallPlan = small.rolloverPlan();
        close("with less, S is what the treasury can net", smallPlan.netted(), s, 1e-9);
        close("...the market's part netted first, then what is left of S off the central bank's par",
                smallPlan.centralBankNetted(), s - market, 1e-9);
        close("...and it rolls the remainder", smallPlan.centralBankPar(), itsPar - (s - market), 1e-9);
        assertTrue("...with nothing sold to the market", smallPlan.issues().isEmpty() && smallPlan.toRoll() == 0);
        double heldSmall = cbs.getPaperHeld();
        play(small);
        close("the add-on is the remainder", cbs.getParAtIssue(), itsPar - (s - market), 1e-6);
        java.util.List<Debt> alone = soldAtTheLastPress(small);
        assertTrue("...issued it alone, one serial like the one falling due",
                alone.size() == 1 && alone.get(0) instanceof MediumTermBond);
        close("...its face the remainder", alone.get(0).getOustandingPrincipal(), itsPar - (s - market), 1e-6);
        close("...all of it the central bank's but what the step then sold on to the bank",
                alone.get(0).centralBankPrincipal() + alone.get(0).bankPrincipal(), itsPar - (s - market), 1e-6);
        close("...at its quote's price on each unit of face", cbs.getBoughtAtIssue(),
                (itsPar - (s - market)) * issuePricePerFace(alone.get(0)), 1e-6);
        close("its whole maturing par is repaid it", cbs.getPaperRedeemed(), itsPar, 1e-6);
        // The market's part paid down, the paper is smaller and the remainder leaves the
        // central bank over its dial; the holdings step sells the excess to the bank, as it
        // sells any holding over the dial.
        assertTrue("with the market's part paid down, the remainder left it over its dial, and the step sold to the bank",
                cbs.getSoldPaper() > 0);
        close("...back to its dial's share of the term paper", cbs.getPaperHeld(),
                dial * small.getDebtManager().termPrincipal(), 1e-6);
        close("...its book what it held, less the part of its par the surplus paid, less what it sold",
                cbs.getPaperHeld(), heldSmall - (s - market) - (alone.get(0).bankPrincipal()), 1e-6);
        close("the one ledger carries S, once", small.getRollover().getLastNetted(), s, 1e-9);

        /* ---- a save and load after it ---- */
        final Game[] back = new Game[1];
        quietly(() -> {
            small.saveGame(ROLL_SLOT, "the surplus paid part of it");
            back[0] = new Game(files);
            back[0].loadGameSave(ROLL_SLOT);
        });
        Game twin = back[0];
        Debt aloneBack = soldAtTheLastPress(twin).isEmpty() ? null : soldAtTheLastPress(twin).get(0);
        assertTrue("through a save: the paper issued it alone is there", aloneBack != null);
        close("...its par the central bank's", aloneBack.centralBankPrincipal(), alone.get(0).centralBankPrincipal(), 0);
        close("...the central bank's book", twin.getCentralBank().getPaperHeld(), cbs.getPaperHeld(), 0);
        close("...what it has rolled at issue since founding", twin.getCentralBank().getParAtIssueLifetime(),
                cbs.getParAtIssueLifetime(), 0);
        close("...and the ledger: what the year's surplus has paid",
                twin.getRollover().usedInYear(twin.getMonth()), small.getRollover().usedInYear(small.getMonth()), 0);
        play(small);
        play(twin);
        close("a month on, both hold the same", twin.getCentralBank().getPaperHeld(), cbs.getPaperHeld(), 1e-9);
        close("...and have made the same money", twin.getCentralBank().m0(), cbs.m0(), 1e-6);

        assertTrue(String.format("every month of the surplus's branches closed the audit, and M0"
                + " moved by exactly the money made (%d months in all)", monthsPlayed), monthsBroken == 0);
    }
}
