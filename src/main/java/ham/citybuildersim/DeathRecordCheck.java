package ham.citybuildersim;

/**
 * The running totals of the dead, by age and for the orphans and the unhoused.
 *
 * Jerus, 2026-09-11: "in history/graphs track how many of each category have
 * died cumulative over time" - by age band, and the orphans and the unhoused;
 * graphs only. The months are recorded, the totals derived. Every claim sets
 * its own cause.
 */
public class DeathRecordCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-80s %12.4f  expected %12.4f  %s%n", label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-80s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void quietly(Runnable work) {
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        try { work.run(); } finally { System.setOut(out); }
    }

    static double last(HistorySave h, String key) {
        java.util.List<? extends Number> s = h.seriesByName().get(key);
        return s == null || s.isEmpty() ? Double.NaN : s.get(s.size() - 1).doubleValue();
    }

    public static void main(String[] args) throws Exception {

        /* ============ 1. who among the dead ============ */
        System.out.println("--- who among the month's dead were orphans, and who had no home ---");
        int n = AgeBand.values().length;
        double[] ones = new double[n];
        java.util.Arrays.fill(ones, 1);
        double[] uncared = new double[n];
        java.util.Arrays.fill(uncared, 40);
        double[] inBand = {1000, 1000, 1000, 1000, 1000};
        double[] orphans = {100, 0, 0, 0, 0};
        double[] unhoused = {0, 0, 0, 100, 0};
        double[] dying = {100, 0, 0, 100, 0};
        double[] noIllness = new double[n];

        double[] split = Unemployment.attributeDeaths(ones, uncared, inBand, unhoused, orphans, dying, noIllness);
        check("a tenth of the babies orphaned, at forty times the rate: their share of the dead",
                split[0], 100 * .1 * 40 / (.9 + .1 * 40), 1e-9);
        check("a tenth of the adults with no home, at 3.7 times: their share",
                split[1], 100 * .1 * Unemployment.UNHOUSED_MORTALITY / (.9 + .1 * Unemployment.UNHOUSED_MORTALITY), 1e-9);

        double[] illness = {10, 0, 0, 20, 0};
        double[] withIllness = Unemployment.attributeDeaths(ones, uncared, inBand, unhoused, orphans, dying, illness);
        check("the sick die alike across the band, so an orphan takes a tenth of the babies' illness deaths",
                withIllness[0], 90 * .1 * 40 / (.9 + .1 * 40) + 10 * .1, 1e-9);
        check("...and the unhoused a tenth of the adults'",
                withIllness[1], 80 * .1 * Unemployment.UNHOUSED_MORTALITY / (.9 + .1 * Unemployment.UNHOUSED_MORTALITY) + 20 * .1, 1e-9);

        double[] nobody = Unemployment.attributeDeaths(ones, uncared, inBand, new double[n], new double[n], dying, illness);
        check("a city with no orphans and nobody unhoused attributes none of its dead to them",
                nobody[0] + nobody[1], 0, 0);

        /* ============ 2. the running total ============ */
        System.out.println("\n--- the running total ---");
        double nan = Double.NaN;
        double[] summed = HistorySave.runningTotal(new double[] {nan, nan, 1, 2, nan, 3});
        assertTrue("months before a series was recorded stay unrecorded",
                Double.isNaN(summed[0]) && Double.isNaN(summed[1]));
        check("...then it sums", summed[3], 3, 0);
        check("...carries across a gap", summed[4], 3, 0);
        check("...and goes on", summed[5], 6, 0);

        /* ============ 3. a city whose employer closes ============ */
        System.out.println("\n--- a played city, and a closing that orphans children ---");
        java.nio.file.Path root = java.nio.file.Files.createTempDirectory("deathrecord");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            BuildingManager b = g.getBuildingManager();
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 100_000_000L);
            b.addStack(b.getTemplateByName("House"), 600, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 14, true);
            b.addStack(b.getTemplateByName("Industrial Bakery"), 6, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.simulateMonths(48);
        });
        HistorySave h = g.getHistorySave();
        PopulationCohorts pyramid = g.getCohorts();
        String[] keys = {"deathsBabies", "deathsChildren", "deathsTeens",
                         "deathsAdults", "deathsSeniors", "deathsElders"};
        if (keys.length != AgeBand.values().length) {
            System.out.println("  FAIL  a band has been added with no death series beside it");
            fails++;
        }
        double bands = 0;
        for (AgeBand band : AgeBand.values()) {
            check("the graph holds this month's dead: " + band.getLabel().toLowerCase(),
                    last(h, keys[band.ordinal()]), Math.round(pyramid.getDeaths(band) * 100) / 100.0, 1e-9);
            bands += pyramid.getDeaths(band);
        }
        check("...and the bands add up to the month's dead", bands, pyramid.getLastDeaths(), 1e-9);
        double[] total = HistorySave.runningTotal(h.aligned("deaths"));
        double sum = 0;
        for (Number v : h.seriesByName().get("deaths")) sum += v.doubleValue();
        check("the running total is every month's dead since the founding", total[total.length - 1], sum, 1e-9);
        assertTrue("fixture: the city has buried somebody", sum > 0);

        /* =================================================================
           CLOSING THE MILL USED TO ORPHAN CHILDREN, AND IT NO LONGER DOES.

           This block was written on the old behaviour and its own comment said
           so: "the parents who worked there leave their families". They did -
           the families are built from the adults who WORK, so losing a job took
           an adult out of the pool and their children were left with nobody.
           On a real city at a million people that mechanism, reached through
           the university door instead, killed five to seven thousand children a
           decade.

           Since 2026-09-15 an adult who leaves work stays with their children;
           only a prisoner is genuinely away. So the premise is inverted, and
           the assertion is now a REGRESSION TEST for the fix rather than a
           fixture for the bug: a wave of layoffs must not fill the orphan
           section, and the children must be findable in the households their
           parents went to.

           The block's own subject - that a dead orphan is recorded and its
           running total grows - needs orphans to EXIST, not to be created, and
           the builder's half-of-what-is-possible throttle leaves some in every
           city. That is what it counts now.
           ================================================================= */
        double orphansBefore = g.getFamilies().getOrphansTotal();
        double atHomeBefore = g.getFamilies().getAtHomeAdults();
        double[] peakOutOfWork = { atHomeBefore };
        double[] peakOrphans = { orphansBefore };
        double[] orphanDeathsEver = new double[1];
        quietly(() -> {
            BuildingsTemplate mill = g.getBuildingManager().getTemplateByName("Industrial Bakery");
            g.getBuildingManager().retire(mill, g.getBuildingManager().getQuantity(mill.getId()));
            for (int m = 0; m < 24; m++) {
                g.simulateMonths(1);
                orphanDeathsEver[0] += g.getLastOrphanDeaths();
                /*
                 * WATCHED ACROSS THE WINDOW, not read at the end of it. The
                 * layoff lands in one month and the migration valve has two
                 * years to wash it out, so the endpoint says nothing about
                 * either the wave or what it did - measured, the out-of-work
                 * count is LOWER two years later than before the closing,
                 * because the city shrank instead. The claim is about the
                 * months in between and has to be measured there.
                 */
                peakOutOfWork[0] = Math.max(peakOutOfWork[0], g.getFamilies().getAtHomeAdults());
                peakOrphans[0] = Math.max(peakOrphans[0], g.getFamilies().getOrphansTotal());
            }
        });
        System.out.printf("   orphans %,.2f before the mill closed, %,.2f after; %,.2f of them died in two years%n",
                orphansBefore, g.getFamilies().getOrphansTotal(), orphanDeathsEver[0]);
        System.out.printf("   out of work but at home %,.2f before, peak %,.2f over the two years,"
                + " %,.2f after; their children %,.2f%n",
                atHomeBefore, peakOutOfWork[0], g.getFamilies().getAtHomeAdults(),
                g.getFamilies().getOutsideDependantsTotal());
        System.out.printf("   orphans peaked at %,.2f against %,.2f before%n", peakOrphans[0], orphansBefore);
        assertTrue("fixture: the closing put adults out of work",
                peakOutOfWork[0] > atHomeBefore);
        /*
         * ...AND BIG ENOUGH TO HAVE SHOWN THE BUG, which is the premise that
         * makes the assertion below mean anything. Under the old rule every one
         * of those adults left their children behind, so the wave would have
         * orphaned (adults) x (the city's own children per adult). Stated
         * against the model's rate rather than a number typed here, and
         * against the orphan count the city already had: if the wave could not
         * have at least doubled it, this fixture is not testing the fix.
         */
        double wouldHaveOrphaned = (peakOutOfWork[0] - atHomeBefore)
                * g.getFamilies().dependantsPerOutsideHousehold();
        System.out.printf("   under the old rule the wave would have orphaned %,.1f children,"
                + " against the %,.2f the city had%n", wouldHaveOrphaned, orphansBefore);
        assertTrue("...and big enough that the old rule would have at least doubled the orphans",
                wouldHaveOrphaned > orphansBefore);
        assertTrue("the orphan section does not fill behind it, in any month of the two years",
                peakOrphans[0] <= orphansBefore + 1);
        assertTrue("...because the children went with their parents",
                g.getFamilies().getOutsideDependantsTotal() > 0);
        assertTrue("fixture: the city still has orphans for the record to count",
                g.getFamilies().getOrphansTotal() > 0);
        assertTrue("...and some of them died", orphanDeathsEver[0] > 0);
        check("the graph holds this month's orphans who died", last(h, "deathsOrphans"),
                Math.round(g.getLastOrphanDeaths() * 100) / 100.0, 1e-9);
        check("...and this month's dead with no home", last(h, "deathsUnhoused"),
                Math.round(g.getLastUnhousedDeaths() * 100) / 100.0, 1e-9);
        double[] orphanTotal = HistorySave.runningTotal(h.aligned("deathsOrphans"));
        assertTrue("...and its running total grew with them",
                orphanTotal[orphanTotal.length - 1] >= Math.round(orphanDeathsEver[0] * 100) / 100.0 - .1);

        System.out.println();
        System.out.println(fails == 0 ? "The dead are counted." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
