package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Buying the city's own debt back, at what the paper is actually worth.
 *
 * The arithmetic is textbook and the risk is not in the arithmetic. It is that
 * a buyback priced off the same market the city moves by borrowing might be a
 * MONEY PUMP: issue, buy back, pocket the difference, repeat. Section 5 is the
 * reason this file exists, and it is written to try to break the feature rather
 * than to demonstrate it.
 *
 * The rest is the property that makes the button worth having at all: market
 * value is not face value, and which side of face it lands on says something
 * true about the city's credit.
 */
public class RestructureCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static void check(String label, double actual, double expected, double tolerance) {
        boolean ok = Math.abs(actual - expected) <= tolerance;
        if (!ok) fails++;
        System.out.printf("%-58s %14.2f  expected %14.2f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) throws Exception {

        out = System.out;

        /* ============ 1. the present value, against hand arithmetic ============ */
        System.out.println("--- what a bond is worth ---");

        /*
         * A par bond checked against the one case anybody can verify by
         * inspection: discount a bond at exactly its own coupon rate and it is
         * worth exactly its face, whatever the term. If this is wrong, every
         * other number in the file is wrong in a way that looks plausible.
         */
        /*
         * THE TERM BOND IS THE BULLET NOW, so the closed-form check lives here.
         * MediumTermBond amortises and is checked on its own schedule below -
         * pricing it with an annuity formula was exactly the assumption the
         * cash-flow rewrite removed, and re-introducing it in the test would
         * prove only that the test agrees with itself.
         */
        LongTermBond par = new LongTermBond(100_000, 60, 1, .06);
        check("discounted at its own coupon, a bond is worth par",
                par.getMarketValue(.06), 100_000, 0.01);

        // Rate up, price down. Hand-computed: c = 500/mo, n = 60, r = .10/12.
        double c = 100_000 * (.06 / 12);
        double r = .10 / 12;
        double disc = Math.pow(1 + r, -60);
        double expected = c * (1 - disc) / r + 100_000 * disc;
        check("rate above the coupon -> below par", par.getMarketValue(.10), expected, 0.01);
        assertTrue("...which is a discount", par.getMarketValue(.10) < 100_000);

        check("rate below the coupon -> above par", par.getMarketValue(.02),
                (100_000 * (.06 / 12)) * (1 - Math.pow(1 + .02 / 12, -60)) / (.02 / 12)
                        + 100_000 * Math.pow(1 + .02 / 12, -60), 0.01);
        assertTrue("...which is a premium", par.getMarketValue(.02) > 100_000);

        /*
         * A T-Bill has no coupon, so its whole value is the discounted face -
         * and it gets that for free from getMonthlyInterestExpense() returning
         * zero, without ShortTermTBill knowing this method exists.
         */
        ShortTermTBill bill = new ShortTermTBill(50_000, 12, 1);
        check("a T-Bill is a pure discount instrument",
                bill.getMarketValue(.08), 50_000 * Math.pow(1 + .08 / 12, -12), 0.01);
        assertTrue("...worth less than its face while it has time to run",
                bill.getMarketValue(.08) < 50_000);

        /* ============ 2. the edges ============ */
        System.out.println("\n--- edges ---");

        ShortTermTBill due = new ShortTermTBill(50_000, 0, 1);
        check("a bond due now costs its face to clear", due.getMarketValue(.08), 50_000, 0.01);

        check("a zero rate discounts nothing", par.getMarketValue(0),
                (100_000 * (.06 / 12)) * 60 + 100_000, 0.01);

        // Longer paper is more rate-sensitive. Not a special case in the code -
        // it falls out of the discounting - but if it ever stopped being true
        // the formula would have to be wrong.
        LongTermBond shortOne = new LongTermBond(100_000, 12, 1, .06);
        LongTermBond longOne  = new LongTermBond(100_000, 300, 1, .06);
        assertTrue("the same rate move hurts long paper more",
                (100_000 - longOne.getMarketValue(.12))
                        > (100_000 - shortOne.getMarketValue(.12)));

        /* ============ 3. a real city buying real paper back ============ */
        System.out.println("\n--- a city actually doing it ---");

        Path root = Files.createTempDirectory("restructure");
        Game g = founded(root, "city");

        System.setOut(quiet);
        try { g.handleMediumBondLogic(200_000, 5, 1000); } finally { System.setOut(out); }

        assertTrue("the city has a bond", !g.getDebtManager().getDebt().isEmpty());
        Debt bond = g.getDebtManager().getDebt().get(0);

        double principalBefore = g.getDebtManager().getAllPrincipal();
        double cashBefore = g.getCash();
        double price = g.quoteRepurchase(bond);

        assertTrue("it is quoted a price", price > 0);
        System.out.printf("   face $%,.0f, quoted $%,.0f, gain $%,.0f%n",
                bond.getOustandingPrincipal(), price, g.repurchaseGain(bond));

        double paid = g.repurchaseDebt(bond);

        check("it paid exactly what it was quoted", paid, price, 0.01);
        check("...and the cash came out", g.getCash(), cashBefore - price, 0.01);
        check("the bond is off the books", g.getDebtManager().getDebt().size(), 0, 0);
        check("...and the principal went with it",
                g.getDebtManager().getAllPrincipal(),
                principalBefore - bond.getOustandingPrincipal(), 0.01);

        assertTrue("buying the same bond twice does nothing",
                g.repurchaseDebt(bond) == 0);

        /* ---- and it must refuse when the city cannot pay ---- */
        System.setOut(quiet);
        try { g.handleMediumBondLogic(200_000, 5, 1000); } finally { System.setOut(out); }
        Debt second = g.getDebtManager().getDebt().get(0);

        g.setCashForTest(10);
        double refusedCash = g.getCash();
        assertTrue("a city that cannot afford it is refused",
                g.repurchaseDebt(second) == 0);
        check("...and was not charged anyway", g.getCash(), refusedCash, 0.001);
        check("...and still owes it", g.getDebtManager().getDebt().size(), 1, 0);

        /* ============ 4. the credit story ============ */
        System.out.println("\n--- what the discount MEANS ---");

        /*
         * The point of the whole feature. A city whose credit has deteriorated
         * since it borrowed can retire its old paper for less than face - not a
         * loophole, just what happens to a bond when the rate moves against the
         * holder. Driven here by hand rather than hoped for: the same bond,
         * priced at two different market rates.
         */
        LongTermBond issued = new LongTermBond(500_000, 120, 1, .03);

        double whenCheap = issued.getMarketValue(.03);
        double whenDear  = issued.getMarketValue(.09);

        System.out.printf("   3%% coupon, 10 years: worth $%,.0f at 3%%, $%,.0f at 9%%%n",
                whenCheap, whenDear);

        check("at its own coupon it is worth par", whenCheap, 500_000, 1);
        assertTrue("after the city's credit worsens, it is cheap to retire",
                whenDear < 500_000 * .8);
        assertTrue("...so the city books a real gain",
                (500_000 - whenDear) > 100_000);

        /* ============ 5. THE ONE THAT MATTERS: is it a money pump? ============ */
        System.out.println("\n--- can the city print money with this? ---");

        /*
         * THE ATTACK. Issue debt, immediately buy it back, repeat. If a round
         * trip ever nets positive the player has an infinite money button, and
         * it would be invisible in ordinary play right up until somebody found
         * it.
         *
         * Run against all three instruments, because they are priced three
         * different ways and only one of them (the medium bond) is a par
         * instrument where the answer is obvious. The long bond is the one to
         * watch: it is issued at a REDEMPTION PREMIUM, so its face is well above
         * the cash received, and if market value tracked face rather than the
         * discounted coupons it would cost more to retire than it raised - or,
         * worse, the other way round.
         */
        for (String kind : new String[]{"medium", "long", "bill"}) {

            Game pump = founded(root, "pump-" + kind);
            pump.setCashForTest(5_000_000);

            double start = pump.getCash();
            double worstGain = 0;

            for (int round = 0; round < 8; round++) {

                double before = pump.getCash();

                System.setOut(quiet);
                try {
                    switch (kind) {
                        case "medium" -> pump.handleMediumBondLogic(200_000, 5, 1000);
                        case "long"   -> pump.handleLongBondLogic(200_000, 25, 100_000);
                        default       -> pump.handleTBillLogic(200_000, 12, 1000);
                    }
                } finally { System.setOut(out); }

                if (pump.getDebtManager().getDebt().isEmpty()) break;

                Debt fresh = pump.getDebtManager().getDebt()
                        .get(pump.getDebtManager().getDebt().size() - 1);
                pump.repurchaseDebt(fresh);

                worstGain = Math.max(worstGain, pump.getCash() - before);
            }

            double net = pump.getCash() - start;
            System.out.printf("   %-7s 8 round trips: net $%,.2f  (best single round $%,.2f)%n",
                    kind, net, worstGain);

            assertTrue("  a " + kind + " round trip never nets the city money",
                    worstGain <= 0.01);
            assertTrue("  ...and eight of them do not either", net <= 0.01);

            assertTrue("  nothing is left outstanding after a round trip",
                    pump.getDebtManager().getDebt().isEmpty());
        }

        /* ============ 5b. the serial bond, on its own terms ============ */
        System.out.println("\n--- a serial bond amortises ---");

        MediumTermBond serial = new MediumTermBond(120_000, 120, 1, .06);

        check("ten annual slices", serial.getSlicesRemaining(), 10, 0);
        check("...of a tenth each", serial.getPrincipalPerSlice(), 12_000, .01);
        check("coupon starts on the whole balance",
                serial.getMonthlyInterestExpense(), 120_000 * .06 / 12, .01);

        double[] flows = serial.remainingCashFlows();
        check("one payment a month for ten years", flows.length, 120, 0);

        /*
         * Everything it will ever pay must equal the principal plus the coupons
         * actually accrued on a DECLINING balance - which is far less than face
         * x rate x years, and that gap is the whole point of the instrument.
         */
        double total = 0;
        for (double cf : flows) total += cf;
        double bulletInterest = 120_000 * .06 * 10;
        double serialInterest = total - 120_000;
        System.out.printf("   interest over its life: serial $%,.0f vs a bullet's $%,.0f%n",
                serialInterest, bulletInterest);
        assertTrue("a serial pays materially less interest than a bullet",
                serialInterest < bulletInterest * .6);
        assertTrue("...but not zero", serialInterest > 0);

        // Discounted at its own coupon, ANY schedule is worth its principal.
        check("at its own coupon it is worth par",
                serial.getMarketValue(.06), 120_000, 1);

        /*
         * Walked forward through a real year. The balance has to actually fall,
         * which is the one thing a bullet never did and the reason the closed
         * form could not price this.
         */
        Game amort = founded(root, "amortise");
        System.setOut(quiet);
        try {
            amort.handleMediumBondLogic(120_000, 10, 1000);
        } finally { System.setOut(out); }

        Debt live = amort.getDebtManager().getDebt()
                .get(amort.getDebtManager().getDebt().size() - 1);
        double owedAtIssue = live.getOustandingPrincipal();

        System.setOut(quiet);
        try { amort.simulateMonths(13); } finally { System.setOut(out); }

        System.out.printf("   owed at issue $%,.0f, thirteen months on $%,.0f%n",
                owedAtIssue, live.getOustandingPrincipal());
        assertTrue("principal actually fell over the first year",
                live.getOustandingPrincipal() < owedAtIssue);
        assertTrue("...and the coupon fell with it",
                live.getMonthlyInterestExpense()
                        < owedAtIssue * .06 / 12 + 1e-9);

        /* ============ 5c. the note knows its own term ============ */
        System.out.println("\n--- a note is discounted on its term ---");

        check("three months discounts a quarter of the annual rate",
                ShortTermTBill.discountFraction(.08, 3), .02, 1e-9);
        check("twelve months discounts the whole of it",
                ShortTermTBill.discountFraction(.08, 12), .08, 1e-9);
        check("twenty-four months, twice",
                ShortTermTBill.discountFraction(.08, 24), .16, 1e-9);

        assertTrue("a short note costs less than a long one for the same cash",
                ShortTermTBill.faceFor(100_000, .08, 3)
                        < ShortTermTBill.faceFor(100_000, .08, 24));

        // The bug this replaced: every term produced the same face.
        assertTrue("...which it did NOT before, when every term priced alike",
                Math.abs(ShortTermTBill.faceFor(100_000, .08, 3)
                        - ShortTermTBill.faceFor(100_000, .08, 24)) > 1_000);

        check("the discount is capped rather than going negative",
                ShortTermTBill.discountFraction(.50, 120), .95, 1e-9);

        /* ============ 5d. yield to maturity ============ */
        System.out.println("\n--- yield, as opposed to coupon ---");

        LongTermBond ytmBond = new LongTermBond(100_000, 60, 1, .06);

        check("bought at par, the yield IS the coupon",
                ytmBond.getYieldToMaturity(100_000), .06, 1e-4);
        assertTrue("bought at a discount, the yield is higher",
                ytmBond.getYieldToMaturity(85_000) > .06);
        assertTrue("bought at a premium, the yield is lower",
                ytmBond.getYieldToMaturity(115_000) < .06);

        /*
         * The round trip that makes YTM trustworthy: price at a rate, solve the
         * yield back out, and get the rate you started with. Anything else means
         * the two halves disagree about what the schedule is.
         */
        for (double rate : new double[]{.02, .05, .09, .15}) {
            check(String.format("  price at %.0f%% -> yield solves back to it", rate * 100),
                    ytmBond.getYieldToMaturity(ytmBond.getMarketValue(rate)), rate, 1e-4);
        }
        check("and it round-trips for an amortising schedule too",
                serial.getYieldToMaturity(serial.getMarketValue(.09)), .09, 1e-4);

        check("par is quoted as 100", ytmBond.getPriceAsPercentOfPar(.06), 100, .01);
        assertTrue("a discount quotes below 100",
                ytmBond.getPriceAsPercentOfPar(.12) < 100);

        /* ============ 6. and it survives a save ============ */
        System.out.println("\n--- a repurchase sticks across a reload ---");

        /*
         * Nothing new is stored - the bond is simply gone from a list that was
         * already saved - which is exactly why this is worth stating. A debt
         * that came back after a reload would be the most expensive possible
         * version of "nothing to save here".
         */
        Path saveRoot = Files.createTempDirectory("restructure-save");
        GameFiles files = new GameFiles(saveRoot.resolve("data"), saveRoot.resolve("no-legacy"));
        Game city = new Game(files);
        System.setOut(quiet);
        try {
            city.run();
            city.handleMediumBondLogic(300_000, 5, 1000);
            city.handleMediumBondLogic(150_000, 10, 1000);
        } finally { System.setOut(out); }

        check("two bonds", city.getDebtManager().getDebt().size(), 2, 0);
        city.repurchaseDebt(city.getDebtManager().getDebt().get(0));
        check("one bought back", city.getDebtManager().getDebt().size(), 1, 0);

        double owedAfter = city.getDebtManager().getAllPrincipal();
        double cashAfter = city.getCash();

        System.setOut(quiet);
        boolean saved;
        try { saved = city.saveGame(1, "bought back").ok; } finally { System.setOut(out); }
        assertTrue("saved", saved);

        Game reloaded = new Game(files);
        System.setOut(quiet);
        try { reloaded.loadGameSave(1); } finally { System.setOut(out); }

        check("the retired bond did not come back",
                reloaded.getDebtManager().getDebt().size(), 1, 0);
        check("...and the principal matches", reloaded.getDebtManager().getAllPrincipal(),
                owedAfter, 0.01);
        check("...and so does the cash", reloaded.getCash(), cashAfter, 0.01);

        /* ---- and every instrument survives, by the name the SAVE uses ---- */
        System.out.println("\n--- all three instruments round-trip ---");

        /*
         * THE REGRESSION THIS EXISTS FOR.
         *
         * The load path switches on the type STRING. Renaming the instruments to
         * Note / Serial / Term dropped every debt in every save on the floor -
         * silently, because an unmatched case falls through and the city simply
         * reloads owing nothing. It was caught by SaveFileCheck noticing the
         * debt had vanished, which is one assertion away from having shipped.
         *
         * So: one of each, saved, reloaded, counted. Anything that renames an
         * instrument again fails here.
         */
        Path allRoot = Files.createTempDirectory("all-instruments");
        GameFiles allFiles = new GameFiles(allRoot.resolve("data"), allRoot.resolve("no-legacy"));
        Game every = new Game(allFiles);
        System.setOut(quiet);
        try {
            every.run();
            every.handleTBillLogic(50_000, 6, 1000);
            every.handleMediumBondLogic(100_000, 10, 1000);
            every.handleLongBondLogic(100_000, 25, 1000);
            every.saveGame(2, "one of each");
        } finally { System.setOut(out); }

        check("three instruments issued", every.getDebtManager().getDebt().size(), 3, 0);
        double owedBefore = every.getDebtManager().getAllPrincipal();

        Game back = new Game(allFiles);
        System.setOut(quiet);
        try { back.loadGameSave(2); } finally { System.setOut(out); }

        check("all three came back", back.getDebtManager().getDebt().size(), 3, 0);
        check("...owing the same", back.getDebtManager().getAllPrincipal(), owedBefore, 0.01);

        for (Debt d : back.getDebtManager().getDebt()) {
            assertTrue("  " + d.getType() + " restored with a live schedule",
                    d.remainingCashFlows().length > 0);
            assertTrue("  " + d.getType() + " is worth something",
                    d.getMarketValue(.05) > 0);
        }

        // The serial specifically: it must come back able to amortise, not as a
        // bond with principal and no schedule to repay it on.
        for (Debt d : back.getDebtManager().getDebt()) {
            if (d instanceof MediumTermBond m) {
                assertTrue("  the serial kept its slices", m.getSlicesRemaining() > 0);
                assertTrue("  ...and its slice size", m.getPrincipalPerSlice() > 0);
            }
        }

        cleanUp(allRoot);
        cleanUp(root);
        cleanUp(saveRoot);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /** A city with an economy, so the market has something to price against. */
    static Game founded(Path root, String name) throws Exception {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve("no-legacy"));
        Game g = new Game(files);
        System.setOut(quiet);
        try {
            g.run();
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplateByName("House"), 200, true);
            b.addStack(b.getTemplateByName("Convience Store"), 6, true);
            b.addStack(b.getTemplateByName("Construction Depot"), 2, true);
            g.simulateMonths(18);
        } finally { System.setOut(out); }
        return g;
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
