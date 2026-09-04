package ham.citybuildersim;

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
 * So this file asserts conservation laws and nothing else. One per commodity,
 * one per money flow, plus the one that says a city is the same city after you
 * load it. They are cheap, they are unglamorous, and they are the only thing
 * that would have caught any of the three.
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
            b.addStack(b.getTemplateByName("Convience Store"), 12, true);
            b.addStack(b.getTemplateByName("Food Processing Plant"), 3, true);
            b.addStack(b.getTemplateByName("Construction Depot"), 3, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 2, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.simulateMonths(months);
        } finally { System.setOut(out); }
        return g;
    }

    public static void main(String[] args) throws Exception {

        out = System.out;
        Path root = Files.createTempDirectory("conservation");

        Game g = city(root, "main", 120);
        EconomyManager em = g.getEconomyManager();
        IndustrialHandler ih = em.getIndustrialHandler();
        UtilitiesHandler uh = g.getServicesManager().getUtilitiesHandler();

        /* ============ 1. FOOD ============ */
        System.out.println("--- food: what came in equals what went out ---");

        /*
         * THE LAW: opening stock + what was made - what spoiled - what was sold
         * - what was shipped abroad = closing stock. Exactly, every month, for
         * as long as you care to run it.
         *
         * Run over many months rather than one because the failure that
         * motivated it was a STEADY leak: stock pinned at capacity while sales
         * were booked against it, which one month in isolation looks like a
         * warehouse that happens to be full.
         */
        boolean conserved = true;
        int worstMonth = 0;
        double worstGap = 0;
        double totalMade = 0, totalSold = 0, totalExported = 0, totalSpoiled = 0;
        int lowStock = Integer.MAX_VALUE, highStock = Integer.MIN_VALUE;

        for (int m = 0; m < 36; m++) {
            int opening = ih.getFoodInventory();
            System.setOut(quiet);
            try { g.simulateMonths(1); } finally { System.setOut(out); }

            double made = ih.getReportActualProduction();
            int spoiled = ih.getInventoryWrittenOff();
            int sold = ih.getProductsSoldCopy();
            int exported = ih.getProductsImportedCopy();

            totalMade += made;
            totalSpoiled += spoiled;
            totalSold += sold;
            totalExported += exported;

            lowStock = Math.min(lowStock, ih.getFoodInventory());
            highStock = Math.max(highStock, ih.getFoodInventory());

            double expected = opening + made - spoiled - sold - exported;
            double gap = Math.abs(expected - ih.getFoodInventory());
            if (gap > worstGap) { worstGap = gap; worstMonth = m; }
            // A unit of slack: production is a double and the warehouse is an int.
            if (gap > 1.0) conserved = false;
        }

        System.out.printf("   over 36 months: made %,.0f  sold %,.0f  exported %,.0f"
                + "  spoiled %,.0f%n", totalMade, totalSold, totalExported, totalSpoiled);
        System.out.printf("   worst monthly discrepancy: %.4f units (month %d)%n",
                worstGap, worstMonth);
        assertTrue("every month, food in equals food out plus the change in stock",
                conserved);

        /*
         * AND IT ACTUALLY MOVES. The conservation law above is satisfied
         * trivially by a warehouse where nothing ever happens - which is exactly
         * the state the bug produced, since 0 in and 0 out balances perfectly.
         * A law that a broken system also passes is not a test.
         */
        /*
         * AND THE STOCK ACTUALLY MOVES, measured as the range it covered rather
         * than as first-versus-last. A settled city cycles - it ends a run where
         * it began - so comparing the two endpoints of a periodic series proves
         * nothing and fails on a perfectly healthy warehouse. What the broken
         * version looked like was a FLAT line, so flatness is the thing to test.
         */
        System.out.printf("   stock ranged %,d to %,d over the run%n", lowStock, highStock);
        assertTrue("...and the warehouse is not simply frozen", totalSold > 0);
        assertTrue("...and the stock level actually moves", highStock > lowStock);

        /* ============ 2. ELECTRICITY ============ */
        System.out.println("\n--- electricity: the utility books what the sectors pay ---");

        double billedDraw = uh.getBilledElectricityDraw();
        System.out.printf("   city draws %,.0f W, of which %,.0f W is invoiced to somebody%n",
                uh.getConsumption(), billedDraw);

        double charged = em.getCommercialHandler().getReportElectricityCost()
                + ih.getReportElectricityCost()
                + em.getHeavyIndustryHandler().getReportElectricityCost()
                + em.getMiningHandler().getReportElectricityCost();

        check("the utility's revenue is exactly what the sectors were charged",
                uh.getElectricityRevenue(), charged, .0001);

        /*
         * The unbilled draw is real and deliberate - houses, roads, the water
         * plant and the city's own yards all use power and nobody invoices them.
         * What must never happen again is the utility BOOKING that draw as
         * revenue, which is what made 73% of a city's power bill money from
         * nowhere.
         */
        assertTrue("there is unbilled draw, and it is not booked as revenue",
                uh.getUnbilledElectricityDraw() > 0);

        /* ============ 3. WATER ============ */
        System.out.println("\n--- water: the same law, which it already obeyed ---");

        double waterCharged = em.getCommercialHandler().getReportWaterCost()
                + ih.getReportWaterCost()
                + em.getHeavyIndustryHandler().getReportWaterCost();

        check("the water utility's revenue is what the sectors were charged",
                uh.getWaterRevenue(), waterCharged, .0001);

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
