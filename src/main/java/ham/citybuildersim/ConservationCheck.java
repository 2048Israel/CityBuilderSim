package ham.citybuildersim;

import ham.citybuildersim.sectors.Mining;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Nothing is created and nothing is destroyed.
 *
 * ==================== WHY THIS FILE EXISTS ====================
 *
 * A deliberate audit in September 2026 turned up three bugs that all twenty-two
 * existing harnesses and four thousand months of `LongPlaytest` had missed:
 *
 *   1. Loading a save into a running game DOUBLED every building. loadGameSave()
 *      called only initialize(), which is guarded and therefore a no-op once a
 *      game is running, and the reset inside loadGame() was commented out.
 *      Measured: house capacity 2,720 -> 2,720 -> 5,340.
 *
 *   2. The food warehouse never drained. updateFinalIndustrialHandler()
 *      subtracted two fields that only a dead method ever wrote, so stock sat at
 *      capacity while the mills booked revenue on it every month and the shops
 *      added inventory the mills never lost.
 *
 *   3. 73% of the city's electricity was billed to nobody. The utility collected
 *      on every building's draw; only four sector categories were ever charged.
 *
 * Every one of them is the same KIND of bug and none of them is a broken
 * mechanism. The existing harnesses assert a great deal about mechanisms - that
 * a bond amortises, that a streak survives a save, that households fit their
 * homes - and almost nothing about whether the books balance. A mechanism can be
 * perfectly correct while quietly manufacturing food.
 *
 * So this asserts conservation laws and nothing else. They are cheap, they
 * are unglamorous, and they are the only thing that would have caught any of
 * the three: one per commodity, one per money flow, plus the one that says a
 * city is the same city after you load it.
 *
 * SINCE GT1 (2026-11-02) THE FOOD, ELECTRICITY AND WATER LAWS ARE ASSERTED ON
 * EVERY MONTH OF LongPlaytest (`LongPlaytest.conservation`, which `audit`
 * calls after each month `run` plays) instead of a 156-month city of this
 * file's own, founded and played only to read them (Jerus: a claim that must
 * hold in every city every month belongs on the playtest's months). Their
 * premises - bread sold, the stock moving, unbilled draw - are findings of the
 * playtest's report too, since a law a broken system also passes is not a
 * test. This file keeps the load law and the rest, below.
 *
 * THE BLIND QUADRANT. Bug 1 was invisible BY CONSTRUCTION: every other harness
 * builds a fresh Game per case, so the same Game object is never loaded into
 * twice. Section 4 deliberately reuses one. Any future bug that only appears
 * when state survives across a load or a new game lives in that quadrant too.
 *
 * ==============================================================
 */
public class ConservationCheck {

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-56s %14.4f  expected %14.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-56s %s%n", label, ok ? "OK" : "FAIL");
    }

    /** A city with something of everything in it, so every law has something to test. */
    static Game city(Path root, String name, int months) throws Exception {
        GameFiles files = new GameFiles(root.resolve(name), root.resolve("no-legacy"));
        Game g = new Game(files);
        System.setOut(quiet);
        try {
            g.run();
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplateByName("House"), 300, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 12, true);
            b.addStack(b.getTemplateByName("Bakery"), 3, true);
            b.addStack(b.getTemplateByName("Construction Depot"), 3, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 2, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            /*
             * THE FIXTURE HAS TO KEEP THE PLANTS IT IS MEASURING. Three food
             * plants for twelve hundred people is a sector losing money on
             * every unit, and since 2026-09-10 a sector that does so sells its
             * capacity: at a buildable plant size the spare-capacity rule
             * thins three plants to one, and the distress rule takes the last
             * one two years later - so a 36-month conservation law measured
             * at month 120 read "made 0, sold 0" against a warehouse that no
             * longer existed. Twentieth sighting. The city protects the
             * sector, as a player can from the Policy tab, so the loss the
             * fixture's own shape creates is covered rather than acted on and
             * the law is measured against a warehouse that is still there.
             */
            g.setAutoSubsidised(Sectors.INDUSTRY, true);
            /*
             * AND THE TREASURY THAT PAYS FOR ALL OF IT, SAID OUT LOUD (0.7.10).
             * The subsidy above, two coal plants' unbilled draw and a hundred
             * and fifty-six months of a city's running costs come out of the
             * treasury, and this fixture was written against the D$2.5B every
             * city opened with until 0.7.10. On the D$100M a city founds with
             * now it ran dry, simulateMonths() stopped running months, and the
             * food law was measured on a warehouse nothing moved through. The
             * fixture is given the treasury it always spent - the Wealthy
             * preset's - explicitly.
             */
            g.setCashForTest(Founding.WEALTHY_CASH);
            g.simulateMonths(months);
        } finally { System.setOut(out); }
        return g;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        Path root = TempDirs.create("conservation");

        /* ============ 4. A CITY IS THE SAME CITY AFTER YOU LOAD IT ============ */
        System.out.println("\n--- loading: the blind quadrant ---");

        /*
         * THE ONE NO OTHER HARNESS CAN SEE. Every other file builds a fresh Game
         * per case, so a Game object is never loaded into twice - and the bug
         * this catches only exists on the second load. Reusing one object here
         * is the entire point of the section, not a shortcut.
         */
        Game source = city(root, "saved", 60);
        System.setOut(quiet);
        try { source.saveGame(1, "conservation"); } finally { System.setOut(out); }

        GameFiles files = new GameFiles(root.resolve("saved"), root.resolve("no-legacy"));
        Game reused = new Game(files);

        System.setOut(quiet);
        try { reused.loadGameSave(1); } finally { System.setOut(out); }
        double[] first = snapshot(reused);

        System.setOut(quiet);
        try {
            reused.loadGameSave(1);
            reused.loadGameSave(1);
        } finally { System.setOut(out); }
        double[] third = snapshot(reused);

        String[] names = {"house capacity", "total jobs", "population", "cash",
                          "land allocated", "monthly GDP", "city debt"};
        System.out.printf("   after 1 load: %,.0f house capacity;  after 3: %,.0f%n",
                first[0], third[0]);
        for (int i = 0; i < names.length; i++) {
            check("  " + names[i] + " survives three loads unchanged",
                    third[i], first[i], .0001);
        }

        // And it agrees with the city it came from, not merely with itself.
        double[] original = snapshot(source);
        check("...and matches the city that was saved", first[0], original[0], .0001);

        /*
         * NEW GAME AFTER A PLAYED GAME, the other half of the quadrant. Same
         * object, and it must come back to a founding city rather than
         * inheriting one - buildWorld() exists for exactly this and is asserted
         * here rather than assumed.
         */
        /*
         * Compared against a GENUINELY FRESH Game rather than against zero. A new
         * city is not empty - it is founded with a starter block of housing - so
         * asserting zero tests the founding grant, not the reset. The property
         * buildWorld() actually promises is that a new game on a used object is
         * indistinguishable from one in a fresh process, and that is what this
         * says.
         */
        Game virgin = new Game(new GameFiles(root.resolve("virgin"), root.resolve("no-legacy")));
        System.setOut(quiet);
        try {
            virgin.run();
            reused.newGame();
        } finally { System.setOut(out); }

        double[] afterNew = snapshot(reused);
        double[] neverUsed = snapshot(virgin);
        for (int i = 0; i < names.length; i++) {
            check("  " + names[i] + ": a new game on a used object matches a fresh one",
                    afterNew[i], neverUsed[i], .0001);
        }
        check("...and it is back at month one", reused.getMonth(), 1, 0);

        /* ============ 5. WHAT A RELOAD MUST NOT FORGET ============ */
        System.out.println("\n--- history a reload cannot rebuild ---");

        /*
         * Four setters and five fields were found in the same audit, all the
         * same shape: something the monthly path maintains that the load path
         * never touched. None is a wrong calculation; each is a missing call or
         * a missing field, and every one of them is invisible until you compare
         * a played city against a reloaded one.
         *
         * The sharpest was MINING'S PAYROLL. updateMiningWages() had exactly one
         * call site, on the month loop, and startOfMonthUpdate() writes the
         * mine's statement BEFORE simulateMonth() would have filled the wage
         * array in - so the first month after any reload the mine booked full
         * revenue against a payroll of zero and the city taxed the profit.
         */
        Game mined = city(root, "mined", 40);
        System.setOut(quiet);
        try {
            BuildingManager b = mined.getBuildingManager();
            // ...over ore. A mine over a worked-out deposit is spare plant and
            // is sold within the year since 2026-09-11 (sectors.Mining), and
            // a sold mine has no payroll to survive a load. The fixture wants
            // a WORKING mine, so it gets a deposit to work.
            mined.getLandManager().restoreIron(3, 50_000_000);
            b.addStack(b.getTemplate(Mining.IRON_MINE), 3, true);
            mined.simulateMonths(30);
            mined.saveGame(1, "mined");
        } finally { System.setOut(out); }

        GameFiles minedFiles = new GameFiles(root.resolve("mined"), root.resolve("no-legacy"));
        Game minedBack = new Game(minedFiles);
        System.setOut(quiet);
        try { minedBack.loadGameSave(1); } finally { System.setOut(out); }

        double livePayroll = mined.getSectors().mining().getPayroll();
        double backPayroll = minedBack.getSectors().mining().getPayroll();
        System.out.printf("   mining payroll: live $%.4f, reloaded $%.4f%n",
                livePayroll, backPayroll);
        assertTrue("the mine has a payroll at all after a load", backPayroll > 0);
        check("...and it is the payroll it was working", backPayroll, livePayroll, .0001);

        // The utility's net income, the residents' statement and the whole
        // government block were all set on the monthly path only.
        assertTrue("utility income survives a load",
                minedBack.getEconomyManager().getUtilityIncome() != 0);
        assertTrue("the residents' statement is not blank after a load",
                minedBack.getHouseholds().getWages() > 0);
        assertTrue("...and the government's revenue block is not either",
                minedBack.getEconomyManager().getNationalAccounts().getTotalRevenue() > 0);

        /*
         * And the running total the residents' books carry must NOT gain a month
         * from being rebuilt - the reason the load path calls refresh() rather
         * than update().
         */
        check("...and rebuilding it does not book the month twice",
                minedBack.getHouseholds().getCumulativeSaving(),
                mined.getHouseholds().getCumulativeSaving(), .01);

        /* ---- the private sector's memory ---- */
        BusinessInvestment liveBi = mined.getBusinessInvestment();
        BusinessInvestment backBi = minedBack.getBusinessInvestment();

        check("the population trend survives, so forecasts are not flat",
                backBi.getPopulationGrowth(), liveBi.getPopulationGrowth(), .0001);

        boolean streaksMatch = true;
        for (java.util.Map.Entry<String, Integer> e : liveBi.getLossMonthsState().entrySet()) {
            if (!e.getValue().equals(backBi.getLossMonths(e.getKey()))) streaksMatch = false;
        }
        System.out.printf("   loss streaks carried: %s%n", liveBi.getLossMonthsState());
        assertTrue("every sector's losing streak survives a load", streaksMatch);

        cleanUp(root);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /** The figures a load must not move. */
    static double[] snapshot(Game g) {
        return new double[]{
            g.getBuildingManager().getTotalHouseCapacity(),
            g.getPopulationManager().getTotalJobs(),
            g.getPopulationManager().getPopulation(),
            Math.round(g.getCash() * 10000) / 10000.0,
            g.getLandManager().getAllocatedSqFt(),
            Math.round(g.getEconomyManager().getMonthGdp() * 10000) / 10000.0,
            Math.round(g.getDebtManager().getTotalMarketValue() * 10000) / 10000.0
        };
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
