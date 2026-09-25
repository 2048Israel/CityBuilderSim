package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The carry trade: the other side of hot money, and the bank's first borrower.
 *
 * Jerus's framing, and it is the right one: "foreign borrow from the bank and
 * convert to usd to do stuff with it, aka effectively having outflow of
 * currency... its basically the opposite of hot money". Hot money comes here
 * because the city pays more than the world. The carry goes the other way: when
 * the city pays LESS than the world, the trade is to owe the cheap currency and
 * hold the dear one, and every dollar of it is local currency SOLD.
 *
 * WHAT IS ACTUALLY BEING ASKED
 *
 *   1. Is the direction right? Hot money's condition is cityRate > worldRate;
 *      this one's is the reverse, and a check that passes under both is
 *      checking nothing. Section 1 asserts the sign by building both cities.
 *
 *   2. Is it the spread net of the premium, again? A foreigner who owes this
 *      currency is short it, and a currency that might jump is one nobody
 *      wants to be short of. The same country premium that keeps hot money
 *      out keeps the carry out.
 *
 *   3. Is the bank's book really the bound? The target is headroom, not
 *      output - Jerus's call, and the arithmetic behind it is in
 *      CapitalFlows.carryTakeMonth. If the cap does not bind, the bank can be
 *      lent past its own capacity by people who do not live here.
 *
 *   4. DOES IT REACH THE BORDER? This is the one that matters. Hot money spent
 *      its entire life moving money in and out of the city without ever
 *      appearing on the balance of payments, because it moved after the audit
 *      struck; the two errors cancelled and the residual stayed $0.00 for
 *      years. A carry trade that does not reach the financial account is that
 *      bug again, and section 5 is the assertion that would have caught it.
 */
public class CarryTradeCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet;

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) {
            fails++;
            out.printf("%-58s FAIL  %,.6f != %,.6f%n", label, actual, expected);
        } else {
            out.printf("%-58s OK%n", label);
        }
    }

    /** Run a stock to rest against a fixed rate, premium and headroom. */
    static CapitalFlows settle(double lendingRate, double premium, double headroom, int months) {
        CapitalFlows f = new CapitalFlows();
        for (int m = 1; m <= months; m++) {
            f.carryTakeMonth(lendingRate, DebtManager.WORLD_BASE_RATE, premium, headroom);
        }
        return f;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 1. it is the opposite sign ================= */
        out.println("--- the carry goes the way hot money does not ---");

        /*
         * THE SIGN, ASSERTED BY BUILDING BOTH. A city paying 6% against a 2%
         * world is the hot-money city: money comes IN and no foreigner would
         * dream of owing it. A city paying 0.5% is the carry city. Nothing
         * else differs between these two lines.
         */
        CapitalFlows dear = settle(.06, 0, 1e9, 240);
        CapitalFlows cheap = settle(.005, 0, 1e9, 240);
        close("a city paying MORE than the world is never borrowed from",
                dear.getCarryStock(), 0, 1e-9);
        out.printf("   0.5%% here against a 2%% world: $%,.0f borrowed on %.2f points%n",
                cheap.getCarryStock(), cheap.getCarrySpread() * 100);
        assertTrue("...and one paying less is", cheap.getCarryStock() > 0);
        close("...on the gap between the two rates",
                cheap.getCarrySpread(), DebtManager.WORLD_BASE_RATE - .005, 1e-9);

        /*
         * AND THE PREMIUM IS CHARGED AGAINST IT, exactly as it is against hot
         * money coming the other way. Somebody who owes local and holds
         * dollars is short this currency; if it might jump, that risk eats the
         * trade. Same 1.5 points of spread, 2 points of premium, no trade.
         */
        CapitalFlows risky = settle(.005, .02, 1e9, 240);
        close("...net of what it costs to be short this currency",
                risky.getCarryStock(), 0, 1e-9);
        CapitalFlows nicked = settle(.005, .005, 1e9, 240);
        close("...and a small premium only narrows it",
                nicked.getCarrySpread(), DebtManager.WORLD_BASE_RATE - .005 - .005, 1e-9);
        assertTrue("...leaving a smaller book, not none",
                nicked.getCarryStock() > 0 && nicked.getCarryStock() < cheap.getCarryStock());

        /* ================= 2. the bank's book is the bound ================= */
        out.println("\n--- and the bank's spare book is what bounds it ---");

        CapitalFlows small = settle(.005, 0, 1_000_000, 240);
        CapitalFlows big = settle(.005, 0, 2_000_000, 240);
        out.printf("   headroom $1,000,000 -> $%,.0f;  $2,000,000 -> $%,.0f%n",
                small.getCarryStock(), big.getCarryStock());
        close("twice the headroom is twice the trade",
                big.getCarryStock(), small.getCarryStock() * 2, 1);
        assertTrue("...and never more of it than the cap allows",
                small.getCarryStock()
                        <= 1_000_000 * CapitalFlows.CARRY_MAX_SHARE + 1e-6);
        assertTrue("fixture: the cap actually leaves the bank something",
                CapitalFlows.CARRY_MAX_SHARE < 1);

        /*
         * A BANK WITH NOTHING SPARE LENDS NOTHING, however wide the spread.
         * The order in Game.nextMonth() puts this call after refreshBank(), so
         * headroom() is already net of every domestic borrower this month:
         * the carry gets what is left, never what somebody here wanted.
         */
        close("a bank with no room lends none of it",
                settle(.005, 0, 0, 240).getCarryStock(), 0, 1e-9);

        /* ================= 3. it builds and unwinds gradually ================= */
        out.println("\n--- it builds slowly and leaves faster ---");

        CapitalFlows paced = new CapitalFlows();
        paced.carryTakeMonth(.005, DebtManager.WORLD_BASE_RATE, 0, 1e9);
        double firstMonth = paced.getCarryStock();
        double target = paced.getCarryTarget();
        out.printf("   month one takes $%,.0f of a $%,.0f target (%.1f%%)%n",
                firstMonth, target, firstMonth / target * 100);
        assertTrue("no one arrives all at once", firstMonth < target * .25);
        close("...but at the speed the constant says",
                firstMonth, target * CapitalFlows.CARRY_BORROW_SPEED, 1);

        for (int m = 2; m <= 240; m++) {
            paced.carryTakeMonth(.005, DebtManager.WORLD_BASE_RATE, 0, 1e9);
        }
        double atRest = paced.getCarryStock();
        assertTrue("...and it does get there in the end",
                Math.abs(atRest - target) < target * .02);

        /* The spread closes: the city raises its own rate above the world's. */
        for (int m = 241; m <= 360; m++) {
            paced.carryTakeMonth(.05, DebtManager.WORLD_BASE_RATE, 0, 1e9);
        }
        out.printf("   the spread closes: $%,.0f -> $%,.0f%n", atRest, paced.getCarryStock());
        close("a closed spread is repaid to nothing", paced.getCarryStock(), 0, 1e-9);
        assertTrue("...and repaying is the faster of the two",
                CapitalFlows.CARRY_REPAY_SPEED > CapitalFlows.CARRY_BORROW_SPEED);

        /* The coupon is the book at the rate they borrowed at. */
        CapitalFlows paying = settle(.005, 0, 1e9, 240);
        close("the coupon is the book at the rate they took it at",
                paying.carryInterestOn(.005), paying.getCarryStock() * .005 / 12, 1e-6);

        /* ================= 4. it survives a save ================= */
        out.println("\n--- and it survives being written down ---");

        CapitalFlows saved = settle(.005, 0, 1e9, 240);
        saved.carryInterestOn(.005);
        CapitalFlows read = new CapitalFlows();
        read.restore(saved.toSaveArray());
        close("the book comes back", read.getCarryStock(), saved.getCarryStock(), 1e-9);
        close("...and what it borrowed over its life",
                read.getLifetimeCarryBorrowed(), saved.getLifetimeCarryBorrowed(), 1e-9);
        close("...and what it paid for it",
                read.getLifetimeCarryInterest(), saved.getLifetimeCarryInterest(), 1e-9);
        close("...and the high-water mark",
                read.getPeakCarryStock(), saved.getPeakCarryStock(), 1e-9);

        /*
         * AND A SAVE FROM BEFORE THE CARRY EXISTED READS BACK AS NO CARRY,
         * which is true rather than merely uncrashing: that city had none.
         */
        double[] old = new double[7];
        System.arraycopy(saved.toSaveArray(), 0, old, 0, 7);
        CapitalFlows older = new CapitalFlows();
        older.restore(old);
        close("a save from before it existed has none of it",
                older.getCarryStock(), 0, 1e-9);

        /* ================= 5. and it reaches the border ================= */
        out.println("\n--- and the money actually leaves the country ---");

        /*
         * THE ASSERTION THIS HARNESS EXISTS FOR. Hot money moved cash in and
         * out of the city for the whole life of the mechanic without ever
         * appearing on the balance of payments, because it moved after the
         * audit struck: the flow was missing AND the pool was already wrong,
         * the two cancelled, and the residual stayed $0.00. A conserved month
         * proves nothing on its own. What proves it is that the audit's
         * FINANCIAL out-leg moves when the carry does, and by the same amount.
         */
        Path root = Files.createTempDirectory("carrytrade");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));

        double worstResidual = 0, movedOut = 0, lentTotal = 0;
        int monthsLending = 0;
        System.setOut(quiet);
        try {
            city.run();
            // THE TREASURY THIS FIXTURE WAS WRITTEN AGAINST (0.7.10): its build list
            // is bought out of cash, and a city founds on D$100M since 0.7.10, not the
            // D$2.5B it assumed - so it is given that, the Wealthy preset's, explicitly.
            city.setCashForTest(Founding.WEALTHY_CASH);
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(city.getBuildingManager().getTemplateByName("House"), 400, false);
            city.buildStack(city.getBuildingManager()
                    .getTemplateByName("Convenience Store"), 6, false);
            city.buildStack(city.getBuildingManager()
                    .getTemplateByName("Construction Depot"), 4, false);
            city.buildStack(city.getBuildingManager()
                    .getTemplateByName("Coal Power Plant"), 1, false);
            city.buildStack(city.getBuildingManager()
                    .getTemplateByName("Water Treatment Plant"), 1, false);
            city.buildStack(city.getBuildingManager().getTemplateByName("Paved Road"), 30, false);
            city.buildStack(city.getBuildingManager()
                    .getTemplateByName("Commercial Bank"), 3, false);
            /*
             * THE FIXTURE HAS TO CAUSE THE CONDITION - fortieth sighting. The
             * carry needs the city to be CHEAPER than the world, and a city
             * that never touches the dial prices its paper off a 3% policy
             * rate, which is above the 2% world. Left alone this fixture would
             * have asserted "the carry reaches the border" in a city where no
             * carry was possible, and passed by never testing anything.
             */
            city.getDebtManager().setPolicyRate(0);
            for (int m = 0; m < 36; m++) {
                city.simulateMonths(1);
                MoneyAudit.Result r = city.getLastMoneyAudit();
                if (r != null) worstResidual = Math.max(worstResidual, Math.abs(r.relative()));
                double lent = city.getBank().getCarryLent();
                if (lent > 0) {
                    monthsLending++;
                    lentTotal += lent;
                    movedOut += r == null ? 0 : r.financialOut;
                }
            }
        } finally {
            System.setOut(out);
        }

        out.printf("   %d months lending, $%,.0fk lent, financial out $%,.0fk,"
                + " book $%,.0fk, worst residual %.2e%n",
                monthsLending, lentTotal, movedOut,
                city.getBank().getCarryBook(), worstResidual);
        assertTrue("fixture: a cheap city really is borrowed from", monthsLending > 0);
        assertTrue("what the bank lends crosses the border", movedOut >= lentTotal - 1e-6);
        assertTrue("...and every month of it still balances", worstResidual < 1e-6);
        close("...with the bank's book holding exactly the stock",
                city.getBank().getCarryBook(),
                city.getCapitalFlows().getCarryStock(), 1e-9);

        /*
         * AND THE CURRENCY SEES IT. This is the whole point of the mechanic
         * and it is worth stating as arithmetic: the same trade surplus, with
         * and without an outflow financing it, on a ForeignAccounts told
         * nothing else. Measured on the ensemble, the carry covered 79% of a
         * $1.6M current account - and moved the rate by nothing, because the
         * rate mean-reverts to purchasing-power parity and parity is prices,
         * not flows. The flow channel works; it is not the channel that is
         * holding the currency at 0.38.
         */
        ForeignAccounts alone = new ForeignAccounts();
        ForeignAccounts financed = new ForeignAccounts();
        for (int m = 0; m < 60; m++) {
            alone.takeMonth(bop(4_000, 2_000, 0), 6_000);
            financed.takeMonth(bop(4_000, 2_000, 2_000), 6_000);
        }
        out.printf("   pressure on a $2,000k surplus: %+.2f alone, %+.2f with the carry%n",
                alone.pressure(), financed.pressure());
        assertTrue("fixture: the surplus alone would strengthen the currency",
                alone.pressure() < 0);
        assertTrue("money borrowed and taken out offsets a surplus",
                financed.pressure() > alone.pressure());

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** A month at the city's edge, with an outflow on the financial account. */
    static MoneyAudit.Result bop(double exports, double imports, double borrowedOut) {
        double[] f = new double[10];
        f[0] = exports;
        f[1] = imports;
        f[5] = borrowedOut;
        return new MoneyAudit.Result(0, 0, 0, exports, imports + borrowedOut, 0, "", f);
    }
}
