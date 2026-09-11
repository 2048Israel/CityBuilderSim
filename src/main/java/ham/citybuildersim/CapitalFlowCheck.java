package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Hot money: does it come for the right reason, and does it leave for one?
 *
 * WHAT IS ACTUALLY BEING ASKED
 *
 *   1. Does the SPREAD pull it - the excess return net of what the risk costs -
 *      rather than the headline rate? A city paying 9% while the world charges
 *      it 7% for its own risk is not offering anything, and money that came for
 *      that is money that came for a number rather than a reason.
 *
 *   2. Is fragility distinct from crisis? Thin reserves must make a shock into
 *      a run without themselves being one - the first version got this backwards
 *      and produced 110 sudden stops in 333 years, each of them a city fleeing
 *      itself before any money had arrived.
 *
 *   3. Do reserves actually defend? That is the entire answer to "what can the
 *      player do", so if a war chest does not change the outcome, phases 2 and
 *      3 bought nothing.
 *
 *   4. Does it reach the bank, and does the bank's capacity go with it? Money
 *      that arrives and changes nothing is a number on a screen.
 */
public class CapitalFlowCheck {

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

    /** A quiet month with no shock in it. GDP fixed so the target is readable. */
    static void calm(CapitalFlows f, double cityRate, double reserves, int month) {
        f.setMonth(month);
        f.takeMonth(.02, cityRate, DebtManager.WORLD_BASE_RATE, .01,
                10_000, reserves, 0, false, false, month);
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        quiet = new PrintStream(new OutputStream() { @Override public void write(int b) { } });

        /* ================= 1. it comes for the spread ================= */
        out.println("--- money comes for the excess return, not the headline ---");

        CapitalFlows none = new CapitalFlows();
        for (int m = 1; m <= 60; m++) calm(none, DebtManager.WORLD_BASE_RATE, 0, m);
        close("a city paying the world rate attracts nothing", none.getStock(), 0, 1e-9);

        /*
         * AND A CITY WHOSE RATE IS ALL RISK ATTRACTS NOTHING EITHER, which is
         * the assertion that makes this a carry trade rather than a yield
         * chase. 9% against a 2% world rate looks like seven points of free
         * money; if six of them are the country premium, one is the trade, and
         * if all seven are, there is none.
         */
        CapitalFlows allRisk = new CapitalFlows();
        for (int m = 1; m <= 60; m++) {
            allRisk.setMonth(m);
            allRisk.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .07,
                    10_000, 0, 0, false, false, m);
        }
        close("...nor does one whose whole spread is its own risk premium",
                allRisk.getStock(), 0, 1e-9);

        CapitalFlows wide = new CapitalFlows();
        for (int m = 1; m <= 120; m++) calm(wide, .09, 1e9, m);
        out.printf("   9%% against a 2%% world and 1 point of risk: $%,.0f here,"
                + " target $%,.0f, spread %.2f points%n",
                wide.getStock(), wide.getTarget(), wide.getSpread() * 100);
        assertTrue("a real spread pulls money in", wide.getStock() > 0);
        assertTrue("...and it settles near what the spread justifies",
                Math.abs(wide.getStock() - wide.getTarget()) < wide.getTarget() * .05);
        close("...on the spread NET of the premium", wide.getSpread(), .09 - .02 - .01, 1e-9);

        /* ================= 2. and it leaves when it closes ================= */
        out.println("\n--- and drains away when the reason goes ---");

        double atPeak = wide.getStock();
        for (int m = 121; m <= 240; m++) calm(wide, DebtManager.WORLD_BASE_RATE + .01, 1e9, m);
        out.printf("   the spread closes: $%,.0f -> $%,.0f%n", atPeak, wide.getStock());
        assertTrue("a closing spread takes the money with it", wide.getStock() < atPeak * .1);
        assertTrue("...without anybody panicking", !wide.isStopped());

        /* ================= 3. fragility is not a crisis ================= */
        out.println("\n--- and thin reserves are a fragility, not a crisis ---");

        /*
         * THE BUG THIS SECTION EXISTS FOR. Gating the run on the backing ratio
         * alone fired on the first dollar into any city that had never bought a
         * reserve - which is every young city - so the playtest logged 110
         * sudden stops in 333 years and hot money never accumulated at all. A
         * mechanic that fires constantly is as useless as one that never fires,
         * and it hid behind a number that looked like it was working.
         */
        CapitalFlows bare = new CapitalFlows();
        for (int m = 1; m <= 120; m++) calm(bare, .09, 0, m);
        out.printf("   no reserves at all, no shock: $%,.0f here, %s%n",
                bare.getStock(), bare.isStopped() ? "STOPPED" : "calm");
        assertTrue("a city with no reserves still attracts money", bare.getStock() > 0);
        assertTrue("...and is not in crisis for it", !bare.isStopped());

        /* ...until something happens. */
        double beforeShock = bare.getStock();
        bare.setMonth(121);
        bare.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01,
                10_000, 0, .20, false, false, 121);
        out.printf("   then the currency falls 20%%: $%,.0f -> $%,.0f  (%s)%n",
                beforeShock, bare.getStock(), bare.getStopReason());
        assertTrue("a shock on top of fragility is a run", bare.isStopped());
        assertTrue("...and the money starts leaving at once", bare.getStock() < beforeShock);
        assertTrue("...and is told why in words, both halves",
                bare.getStopReason().contains("currency")
                        && bare.getStopReason().contains("reserves"));

        /* ================= 4. reserves are the defence ================= */
        out.println("\n--- and a war chest is what stops it ---");

        CapitalFlows armed = new CapitalFlows();
        for (int m = 1; m <= 120; m++) calm(armed, .09, 1e9, m);
        double armedBefore = armed.getStock();
        armed.setMonth(121);
        armed.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01,
                10_000, armedBefore, .20, false, false, 121);

        out.printf("   the same 20%% fall, fully backed: $%,.0f -> $%,.0f  (%s)%n",
                armedBefore, armed.getStock(), armed.isStopped() ? "STOPPED" : "held");
        assertTrue("the same shock on a backed position is not a run", !armed.isStopped());
        assertTrue("...and the money stays", armed.getStock() > armedBefore * .9);

        /*
         * AND THE THRESHOLD IS WHERE IT SAYS IT IS, walked rather than asserted
         * at one point, because a rule that only holds at the two ends is not a
         * rule.
         */
        double firstRun = -1;
        for (double backing = 1.0; backing >= 0; backing -= .05) {
            CapitalFlows t = new CapitalFlows();
            for (int m = 1; m <= 120; m++) calm(t, .09, 1e9, m);
            double held = t.getStock();
            t.setMonth(121);
            t.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01,
                    10_000, held * backing, .20, false, false, 121);
            if (t.isStopped() && firstRun < 0) firstRun = backing;
        }
        out.printf("   the run starts once backing falls below %.0f%%"
                + " (the rule says %.0f%%)%n",
                firstRun * 100, CapitalFlows.PANIC_BACKING * 100);
        assertTrue("the defence works exactly where the constant says it does",
                Math.abs(firstRun - CapitalFlows.PANIC_BACKING) <= .05);

        /* A default runs the money whatever the reserves. */
        CapitalFlows welched = new CapitalFlows();
        for (int m = 1; m <= 120; m++) calm(welched, .09, 1e9, m);
        double richBefore = welched.getStock();
        welched.setMonth(121);
        welched.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01,
                10_000, 1e9, 0, false, true, 121);
        assertTrue("a default runs the money however deep the reserves",
                welched.isStopped() && welched.getStock() < richBefore);

        /* ================= 5. painful, never fatal ================= */
        out.println("\n--- and a stop is a bad decade, not an ending ---");

        CapitalFlows cycle = new CapitalFlows();
        for (int m = 1; m <= 120; m++) calm(cycle, .09, 0, m);
        double boom = cycle.getStock();
        cycle.setMonth(121);
        cycle.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01,
                10_000, 0, .20, false, false, 121);

        int monthsToEmpty = 0;
        for (int m = 122; m <= 200 && cycle.getStock() > boom * .05; m++) {
            calm(cycle, .09, 0, m);
            monthsToEmpty++;
        }
        out.printf("   $%,.0f left over %d months%n", boom, monthsToEmpty);
        assertTrue("the money does not vanish in a single month", monthsToEmpty > 2);
        assertTrue("...but it is gone within a couple of years", monthsToEmpty < 24);

        /* ...and it comes back, once the city is worth coming to again. */
        for (int m = 201; m <= 400; m++) calm(cycle, .09, 1e9, m);
        out.printf("   and returns to $%,.0f once the city is worth it again%n",
                cycle.getStock());
        assertTrue("a city that survives a stop can be lent to again",
                cycle.getStock() > boom * .5);
        assertTrue("...and is not still marked as stopped", !cycle.isStopped());

        /* ================= 6. it reaches the bank ================= */
        out.println("\n--- and it is the bank's funding, which is why it matters ---");

        Bank b = new Bank();
        b.refresh(10, 50_000, 10_000, 0, 0, 0);
        double capacityBefore = b.capacity();
        double gatheredBefore = b.depositsGathered();

        b.setForeignDeposits(200_000);
        out.printf("   deposits gathered $%,.0f -> $%,.0f, capacity $%,.0f -> $%,.0f%n",
                gatheredBefore, b.depositsGathered(), capacityBefore, b.capacity());

        assertTrue("hot money is funding the branch network could not gather",
                b.depositsGathered() > gatheredBefore + 199_000);
        assertTrue("...so it lifts what the bank can lend",
                b.capacity() > capacityBefore || b.capitalBound());
        close("...and the bank knows how much of its funding can walk",
                b.hotFundingShare(),
                200_000 / b.depositsGathered(), 1e-9);

        /*
         * AND IT IS OWED, WHICH IS THE HALF THAT IS EASY TO MISS. Cash arriving
         * with no matching liability is equity, and without that line the bank
         * recapitalised itself every time a foreign fund wired it money.
         *
         * The cash and the liability move TOGETHER, as Game moves them - a test
         * that sets one without the other measures a state the game cannot be
         * in, and this one did on the first attempt: it read the equity after
         * booking the liability and before the money arrived, and then asserted
         * the money arriving changed nothing.
         */
        Bank paired = new Bank();
        paired.refresh(10, 50_000, 10_000, 0, 0, 0);
        double equityBefore = paired.equity();

        paired.receiveHotMoney(200_000);
        paired.setForeignDeposits(200_000);
        close("money arriving does not make the bank richer",
                paired.equity(), equityBefore, .005);

        paired.returnHotMoney(200_000);
        paired.setForeignDeposits(0);
        close("...and money leaving does not make it poorer either",
                paired.equity(), equityBefore, .005);

        /*
         * AND THE TWO CONSTANTS AGREE WITH EACH OTHER.
         *
         * The largest position APPETITE and MAX_SPREAD can produce has to be
         * bigger than the threshold at which a position is called material, or
         * fragility is unreachable and every shock passes harmlessly. It was
         * unreachable: three months of output required against 1.08 possible.
         * Nothing failed, because a mechanic that cannot fire looks exactly
         * like one that has nothing to do.
         */
        out.printf("   the most that can ever be here is %.2f months of output;"
                + " material is %.2f%n",
                CapitalFlows.maxStockInMonthsOfOutput(), CapitalFlows.MATERIAL_MONTHS);
        assertTrue("a position CAN get big enough to be fragile",
                CapitalFlows.maxStockInMonthsOfOutput() > CapitalFlows.MATERIAL_MONTHS * 1.5);

        /* ================= 7. in a real city, and across a reload ================= */
        out.println("\n--- and it survives a city, and a reload ---");

        Path root = Files.createTempDirectory("hotmoney");
        Game city = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));

        double worst = 0;
        System.setOut(quiet);
        try {
            city.run();
            city.getLandManager().setOwnedSqFt(30_000_000);
            city.buildStack(template(city, "House"), 400, false);
            city.buildStack(template(city, "Convenience Store"), 6, false);
            city.buildStack(template(city, "Construction Depot"), 4, false);
            city.buildStack(template(city, "Coal Power Plant"), 1, false);
            city.buildStack(template(city, "Water Treatment Plant"), 1, false);
            city.buildStack(template(city, "Textile Mill"), 2, false);
            city.buildStack(template(city, "Paved Road"), 30, false);
            city.buildStack(template(city, "Commercial Bank"), 3, false);
            /*
             * THE FIXTURE HAS TO CAUSE THE CONDITION. Nineteenth sighting, and
             * the cleanest yet. Hot money comes for a carry: the city's rate
             * over the world's. A city that never touches the policy dial
             * prices its own paper from floorRate() = policy - 2%, which with
             * the default 3% policy is 1% against a world paying 2% - so the
             * spread is structurally ZERO, by a full point, before anything
             * else happens. This fixture never set the dial, and its assertion
             * "hot money actually arrives" was decided by whether two months
             * of bank-strain premium happened to spike the city's rate: it
             * passed on one buildings.json and failed on another, on a
             * power-plant capacity number. The threshold is policy > 4%; the
             * city is put well past it, and the spread is asserted as well as
             * the arrival, so the reason it passes is the reason it names.
             */
            city.getDebtManager().setPolicyRate(.06);
            for (int m = 0; m < 180; m++) {
                city.simulateMonths(1);
                double r = city.getLastMoneyAudit().relative();
                if (Math.abs(r) > Math.abs(worst)) worst = r;
            }
        } finally {
            System.setOut(out);
        }

        CapitalFlows flows = city.getCapitalFlows();
        out.printf("   fifteen years at a 6%% dial: $%,.0f in, $%,.0f out, peak $%,.0f"
                + " on a peak spread of %.2f points, %d stop(s); worst residual %.1e%n",
                flows.getLifetimeArrived(), flows.getLifetimeDeparted(),
                flows.getPeakStock(), flows.getPeakSpread() * 100,
                flows.getStopsSuffered(), worst);

        assertTrue("fixture: the city actually offered a carry over the world",
                flows.getPeakSpread() > 0.005);

        /*
         * COUNT HOW OFTEN IT ACTUALLY FIRES - K1 in the design queue, earned by
         * out-migration shipping fully tested and firing zero times in 4,002
         * months. A harness proves the code does what it was told; it never
         * proves the situation it was told about happens.
         */
        assertTrue("hot money actually arrives in a real city that offers a carry",
                flows.getLifetimeArrived() > 0);
        assertTrue("...in real money, not a rounding error",
                flows.getPeakStock() > 1000);
        assertTrue("...and the books balance every month it does",
                Math.abs(worst) < 1e-9);

        System.setOut(quiet);
        Game reloaded;
        try {
            city.saveGame(5, "the hot city");
            reloaded = new Game(new GameFiles(root.resolve("data"), root.resolve("no-legacy")));
            reloaded.run();
            reloaded.loadGameSave(5);
        } finally {
            System.setOut(out);
        }

        CapitalFlows back = reloaded.getCapitalFlows();
        close("the position reloads", back.getStock(), flows.getStock(), .005);
        close("...and the record of what has moved",
                back.getLifetimeArrived(), flows.getLifetimeArrived(), .005);
        close("...and the bank is funded by it on the first tick, not the second",
                reloaded.getBank().getForeignDeposits(), flows.getStock(), .005);

        /*
         * AND THE PANIC CLOCK, which is the half a save would quietly drop.
         * A city loaded mid-stop with the clock reset finds the money flooding
         * straight back into a currency it had just run from.
         */
        CapitalFlows mid = new CapitalFlows();
        for (int m = 1; m <= 120; m++) calm(mid, .09, 0, m);
        mid.setMonth(121);
        mid.takeMonth(.02, .09, DebtManager.WORLD_BASE_RATE, .01,
                10_000, 0, .20, false, false, 121);
        assertTrue("the fixture is genuinely mid-stop", mid.isStopped());

        CapitalFlows carried = new CapitalFlows();
        carried.restore(mid.toSaveArray());
        carried.setMonth(121);
        assertTrue("a city loaded mid-stop is still in it", carried.isStopped());
        close("...with the same months left to run",
                carried.stopMonthsLeft(), mid.stopMonthsLeft(), 0);

        /* ======== 8. and the city's own money goes the other way ======== */
        out.println("\n--- and the city's own money goes the other way ---");

        /*
         * THE MIRROR (2026-09-10). Seven sections above are a stranger's money
         * coming for a spread; this is the sectors' own going for one. See
         * OutwardInvestment for why a surplus economy needs it. Every claim is
         * CAUSED: the till is filled by hand, the bank's rate is handed in,
         * the borrower is given its loan directly.
         */
        Path outRoot = Files.createTempDirectory("outward");
        Game rich = new Game(new GameFiles(outRoot.resolve("data"), outRoot.resolve("no-legacy")));
        System.setOut(quiet);
        try { rich.run(); } finally { System.setOut(out); }
        EconomyManager econ = rich.getEconomyManager();
        OutwardInvestment abroad = rich.getOutwardInvestment();
        String con = Sectors.CONSTRUCTION;
        String ind = Sectors.INDUSTRY;

        econ.setSectorCash(con, 1_000_000);          // a billion, idle
        econ.setSectorCash(ind, 500_000);            // half a billion, and a loan
        econ.getBusinessDebtManager().setAssets(ind, 2_000_000);
        econ.getBusinessDebtManager().issueLoan(ind, 100_000, 1);
        assertTrue("fixture: the borrower really owes something",
                econ.getBusinessDebtManager().getPrincipal(ind) > 0);

        // the bank pays nothing, the world pays two percent, for five years
        for (int m = 0; m < 60; m++) abroad.takeMonth(0, DebtManager.WORLD_BASE_RATE, 1.0, econ);

        double wealth = econ.getSectorCash(con) + abroad.localValue(con);
        out.printf("   after five years at a 2-point spread: US$%,.0fk abroad of $%,.0fk, target %.0f%%%n",
                abroad.getUsd(con), wealth, abroad.getTargetShare() * 100);
        assertTrue("idle money goes abroad for the world's rate", abroad.getUsd(con) > 0);
        assertTrue("...most of it, at this spread",
                abroad.localValue(con) / wealth > .5);
        assertTrue("...and not all of it - working capital stays",
                econ.getSectorCash(con) > 0
                        && abroad.localValue(con) / wealth <= OutwardInvestment.MAX_SHARE + 1e-9);
        assertTrue("the coupon rolls where it is earned, so the wealth grew",
                wealth > 1_000_000 && abroad.getInterestThisMonth(con) > 0);
        close("...and nothing of it landed in the till",
                econ.getSectorCash(con) + abroad.getLifetimeOut() - abroad.getLifetimeHome(), 1_000_000, .005);
        close("a sector with a loan keeps its money home", abroad.getUsd(ind), 0, 1e-9);

        // ...and the bank starts paying three percent: it comes home
        double wasAbroad = abroad.getUsd(con);
        for (int m = 0; m < 24; m++) abroad.takeMonth(.03, DebtManager.WORLD_BASE_RATE, 1.0, econ);
        out.printf("   two years after the bank pays 3%%: US$%,.0fk abroad, from US$%,.0fk%n",
                abroad.getUsd(con), wasAbroad);
        assertTrue("it comes home when the bank pays better", abroad.getUsd(con) < wasAbroad * .1);
        close("...to nothing, in the end", abroad.getTargetShare(), 0, 1e-9);

        /*
         * AND THE CURRENCY SEES IT. A surplus the sectors recycle abroad is
         * not a surplus of demand for the currency. Same trade, with and
         * without the outflow, on a ForeignAccounts told nothing else.
         */
        ForeignAccounts unrecycled = new ForeignAccounts();
        ForeignAccounts recycled = new ForeignAccounts();
        for (int m = 0; m < 60; m++) {
            unrecycled.takeMonth(bop(4_000, 2_000, 0), 6_000);
            recycled.takeMonth(bop(4_000, 2_000, 2_000), 6_000);
        }
        out.printf("   pressure on a $2,000k surplus: %+.2f unrecycled, %+.2f recycled abroad%n",
                unrecycled.pressure(), recycled.pressure());
        assertTrue("fixture: the surplus alone would strengthen the currency",
                unrecycled.pressure() < 0);
        close("a surplus sent abroad pushes the currency nowhere", recycled.pressure(), 0, 1e-9);

        /*
         * AND A LIVE CITY CONSERVES IT. The move is inside the audited window;
         * if it were not, this is the assertion that would say so.
         */
        econ.setSectorCash(con, 1_000_000);
        rich.getBank().setDepositRate(0);
        double worstResidual = 0;
        System.setOut(quiet);
        try {
            for (int m = 0; m < 24; m++) {
                rich.simulateMonths(1);
                worstResidual = Math.max(worstResidual, Math.abs(rich.getLastMoneyAudit().relative()));
            }
        } finally {
            System.setOut(out);
        }
        out.printf("   two years live: US$%,.0fk abroad, worst residual %.2e%n",
                abroad.totalUsd(), worstResidual);
        assertTrue("a live city sends money abroad on its own", abroad.totalUsd() > 0);
        assertTrue("...and every month of it is conserved", worstResidual < 1e-9);

        out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** A month at the city's edge: goods in and out, and money the sectors sent abroad. */
    static MoneyAudit.Result bop(double exports, double imports, double investedAbroad) {
        double[] f = new double[10];
        f[0] = exports;
        f[1] = imports;
        f[5] = investedAbroad;
        return new MoneyAudit.Result(0, 0, 0, exports, imports + investedAbroad, 0, "", f);
    }

    static BuildingsTemplate template(Game game, String name) {
        for (BuildingsTemplate t : game.getBuildingManager().getTemplates()) {
            if (t.getName().equals(name)) return t;
        }
        throw new IllegalStateException("no template named " + name);
    }
}
