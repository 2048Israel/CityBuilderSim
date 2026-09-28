package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Verifies the fast-forward summary.
 *
 * The reason this needs testing at all is that half of it cannot be derived
 * from the endpoints. A city that ran out of power for forty months and then
 * built a second station looks, at both ends, exactly like one that never had a
 * problem - so the episode counters have to be sampled, and a sampling bug
 * would silently report a smooth century on a city that spent it starving.
 */
public class SkipReportCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-6;
        if (!ok) fails++;
        System.out.printf("%-54s %13.4f  expected %13.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-54s %s%n", label, ok ? "OK" : "FAIL");
    }

    static Map<String, Integer> buildings(Object... pairs) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String) pairs[i], (Integer) pairs[i + 1]);
        }
        return map;
    }

    public static void main(String[] args) {

        /* ==================== 1. a hundred good months ==================== */
        System.out.println("--- a century that went well ---");

        TimeSkipReport skip = new TimeSkipReport();
        skip.beginSkip(100);

        skip.snapshot(true, 101, 112315, 192, 192, 780,
                1052.96, 12635.47, 0, 48074, 30, .998, .30, .40, 0,
                buildings("House", 23, "Convenience Store", 2, "Construction Depot", 10));

        // Ramped to exactly the closing figure. Overshooting it here would make
        // the report correctly announce a decline that never happened, which is
        // the test being wrong rather than the code.
        for (int m = 0; m < 100; m++) {
            skip.sampleMonth(1, 1, 500000, false, true,
                    (int) Math.round(192 + (m + 1) * (472 / 100.0)));
        }

        skip.snapshot(false, 201, 143264, 664, 664, 898,
                3279.86, 39365.56, 0, 4774, 40, 1.0, .45, .47, 0,
                buildings("House", 141, "Convenience Store", 2,
                        "Construction Depot", 11, "Coal Power Plant", 1));

        assertTrue("complete", skip.isComplete());
        check("months", skip.getCompleted(), 100);
        assertTrue("did not stop early", !skip.stoppedEarly());
        check("from month", skip.getStartMonth(), 101);
        check("to month", skip.getEndMonth(), 201);

        check("population change", skip.getPopulationChange(), 472);
        check("cash change", skip.getCashChange(), 143264 - 112315);
        check("cash per month", skip.getCashPerMonth(), (143264 - 112315) / 100.0);
        check("jobs", skip.getJobsChange(), 118);
        check("monthly GDP", skip.getMonthlyGdpChange(), 3279.86 - 1052.96);
        check("business debt fell", skip.getBusinessDebtChange(), 4774 - 48074);
        check("blocks bought", skip.getLandBlocksBought(), 10);

        // 192 -> 664 over 100 months, annualised.
        check("annualised growth", skip.getPopulationGrowthRate(),
                Math.pow(664 / 192.0, 12.0 / 100) - 1);
        System.out.printf("   %,d -> %,d is %.1f%% a year%n",
                192, 664, skip.getPopulationGrowthRate() * 100);

        /* ==================== 2. what got built ==================== */
        System.out.println("\n--- buildings, net ---");

        List<TimeSkipReport.BuildingChange> changes = skip.getBuildingChanges();
        check("three types moved", changes.size(), 3);

        // Biggest mover first, so the eye lands on what actually happened.
        assertTrue("largest change first", "House".equals(changes.get(0).name));
        check("...by 118", changes.get(0).change, 118);

        check("gained", skip.getBuildingsGained(), 118 + 1 + 1);
        check("nothing lost", skip.getBuildingsLost(), 0);

        // A type whose count did not move is not news and must not be listed.
        for (TimeSkipReport.BuildingChange c : changes) {
            assertTrue("unchanged types are omitted (" + c.name + ")",
                    !"Convenience Store".equals(c.name));
        }

        /* ==================== 3. a century that went badly ==================== */
        System.out.println("\n--- and one that did not ---");

        TimeSkipReport bad = new TimeSkipReport();
        bad.beginSkip(100);
        bad.snapshot(true, 201, 200000, 900, 960, 848,
                500, 6000, 0, 5000, 40, 1.0, .30, .40, 1000,
                buildings("House", 200, "Construction Depot", 11));

        // Forty months of brownouts, the whole time out of land, half of it with
        // nothing on any site, and households underwater for twenty.
        for (int m = 0; m < 100; m++) {
            boolean brownout = m < 40;
            // Congested for the first 30 of them, so the road counter is being
            // measured against a different span than the brownout counter - two
            // counters that always move together prove nothing about either.
            bad.sampleMonth(brownout ? .62 : 1, 1, m < 30 ? .45 : 1,
                    0, m < 20, m >= 50, 900 - m);
        }

        bad.snapshot(false, 301, 150000, 800, 960, 700,
                300, 4000, 0, 14000, 40, .98, -.05, .55, 31707,
                buildings("House", 200, "Construction Depot", 3));

        check("months short of power", bad.getMonthsShortOfPower(), 40);
        check("months congested", bad.getMonthsCongested(), 30);
        check("worst throughput", bad.getWorstRoadRatio(), .45);
        check("worst brownout", bad.getWorstEnergyRatio(), .62);
        check("months with no land", bad.getMonthsOutOfLand(), 100);
        check("months households were short", bad.getMonthsHouseholdsShort(), 20);
        check("months nothing was built", bad.getMonthsNothingBuilt(), 50);
        check("half the time idle", bad.getIdleShare(), .5);

        check("eight depots gone", bad.getBuildingsLost(), 8);
        check("nothing gained", bad.getBuildingsGained(), 0);
        check("written off during the skip", bad.getWriteOffsDuringSkip(), 31707 - 1000);

        // Peak tracking: population went 900 -> 800, so the high-water mark is
        // the start. An endpoint diff alone cannot tell a decline from a dip.
        check("peak", bad.getPeakPopulation(), 900);
        assertTrue("ended below its peak", bad.shrankFromPeak());

        /* ==================== 4. the headlines ==================== */
        System.out.println("\n--- what it says out loud ---");

        List<String> headlines = bad.getHeadlines();
        assertTrue("it has something to say", !headlines.isEmpty());

        boolean saysLand = false, saysPower = false, saysDemolished = false,
                saysWriteOff = false, saysHouseholds = false, saysPeak = false;

        for (String line : headlines) {
            if (line.contains("No land")) saysLand = true;
            if (line.contains("Short of power")) saysPower = true;
            if (line.contains("demolished")) saysDemolished = true;
            if (line.contains("wrote off")) saysWriteOff = true;
            if (line.contains("Households spent more")) saysHouseholds = true;
            if (line.contains("peaked")) saysPeak = true;
            System.out.println("   " + line);
        }

        assertTrue("mentions the land", saysLand);
        assertTrue("mentions the power", saysPower);
        assertTrue("mentions the demolitions", saysDemolished);
        assertTrue("mentions the write-offs", saysWriteOff);
        assertTrue("mentions the households", saysHouseholds);
        assertTrue("mentions the decline", saysPeak);

        // The good run should say so rather than showing a blank panel.
        List<String> good = skip.getHeadlines();
        check("one line", good.size(), 1);
        assertTrue("...and it is the reassuring one",
                good.get(0).startsWith("Nothing went wrong"));

        /* ==================== 5. stopping early ==================== */
        // Until 0.7.14 an empty treasury stopped a skip, and this fixture was that skip.
        // Since 0.7.15 (Jerus: "Skip runs too") the one thing that stops one short is a
        // month that threw (Game.takeSkipFailure()), and the headline no longer names the
        // treasury; section 5a runs a skip through an empty treasury.
        System.out.println("\n--- a skip that stopped short ---");

        TimeSkipReport broke = new TimeSkipReport();
        broke.beginSkip(100);
        broke.snapshot(true, 1, 300, 0, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, null);

        for (int m = 0; m < 12; m++) {
            broke.sampleMonth(1, 1, 100, false, true, 0);
        }
        broke.snapshot(false, 13, -5, 0, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, null);

        check("only twelve ran", broke.getCompleted(), 12);
        assertTrue("flagged as short", broke.stoppedEarly());
        assertTrue("and said so first",
                broke.getHeadlines().get(0).startsWith("Stopped after 12 of 100 months"));

        /* ============ 5a. an empty treasury, on the advances (0.7.15) ============ */
        System.out.println("\n--- an empty treasury: the skip runs on the central bank's advances ---");

        emptyTreasury();

        /* ============ 5b. the central bank's advances (0.7.1) ============ */
        System.out.println("\n--- a treasury on the central bank's advances ---");

        TimeSkipReport advanced = new TimeSkipReport();
        advanced.beginSkip(10);
        advanced.snapshot(true, 1, 300, 0, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, null);
        for (int m = 0; m < 10; m++) {
            advanced.sampleMonth(1, 1, 100, false, true, 0);
            advanced.sampleTreasury(m >= 4, m >= 7);
        }
        advanced.snapshot(false, 11, 300, 0, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, null);
        check("six months on advances", advanced.getMonthsOnAdvances(), 6);
        check("...three of them at the ceiling", advanced.getMonthsAtCeiling(), 3);
        boolean saysAdvances = false;
        for (String line : advanced.getHeadlines()) {
            if (line.contains("central bank's advances for 6 months, 3 of them at the ceiling")) saysAdvances = true;
        }
        assertTrue("and the report says it, where it once promised emergency debt", saysAdvances);

        /* ==================== 6. nothing to report ==================== */
        System.out.println("\n--- before anything has run ---");

        TimeSkipReport fresh = new TimeSkipReport();
        assertTrue("not complete", !fresh.isComplete());
        check("no change to report", fresh.getPopulationChange(), 0);
        check("...nor cash", fresh.getCashChange(), 0);
        check("no growth rate", fresh.getPopulationGrowthRate(), 0);
        check("no buildings", fresh.getBuildingChanges().size(), 0);
        assertTrue("and no headlines", fresh.getHeadlines().isEmpty());

        // A skip from a city with no people must not divide by zero.
        TimeSkipReport empty = new TimeSkipReport();
        empty.beginSkip(10);
        empty.snapshot(true, 1, 100, 0, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, null);
        empty.snapshot(false, 11, 100, 0, 0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, null);
        check("no population, no growth rate", empty.getPopulationGrowthRate(), 0);
        check("no months sampled, no cash rate", empty.getCashPerMonth(), 0);
        check("...nor an idle share", empty.getIdleShare(), 0);

        /* ============ 7. beginSkip clears the last one ============ */
        System.out.println("\n--- and it does not remember the last skip ---");

        bad.beginSkip(50);
        check("power counter cleared", bad.getMonthsShortOfPower(), 0);
        check("congestion counter cleared", bad.getMonthsCongested(), 0);
        check("land counter cleared", bad.getMonthsOutOfLand(), 0);
        check("months cleared", bad.getCompleted(), 0);
        check("worst energy back to full", bad.getWorstEnergyRatio(), 1);
        assertTrue("and it is no longer complete", !bad.isComplete());

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * Jerus, of the time skip: "Same rule as play: the central bank covers the
     * treasury, and the skip only stops for the things that already stop it."
     * An Insane city - nothing in the treasury, no revenue behind it - skipped
     * a year, and its twin stepped the same year a month at a time, the play
     * clock's way, with every month's audit held: the skip's months are the
     * twin's months, so the audit closed in each.
     */
    static void emptyTreasury() {
        Founding insane = Founding.named("Insane", Founding.Preset.INSANE, WorldEconomy.DEFAULT_MEAN_INFLATION);
        java.io.PrintStream out = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
        Game skipped, stepped;
        int ran;
        double printedBefore;
        boolean closed = true;
        double advancedStepped = 0;
        int onAdvances = 0;
        try {
            skipped = new Game(GameFiles.scratch("skipreport-skipped"), insane);
            skipped.run();
            stepped = new Game(GameFiles.scratch("skipreport-stepped"), insane);
            stepped.run();
            System.setOut(out);
            assertTrue("fixture: an empty treasury with no revenue behind it",
                    skipped.getCash() == 0 && skipped.getCentralBank().ceiling() == 0);
            System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
            printedBefore = skipped.getCentralBank().getPrintedLifetime();
            ran = skipped.simulateMonths(12);
            for (int m = 0; m < 12; m++) {
                stepped.toggleNextMonth();
                MoneyAudit.Result r = stepped.getLastMoneyAudit();
                closed &= (Math.abs(r.residual) <= .01 || r.relative() <= 1e-7)
                        && Math.abs(stepped.getPostAuditDrift()) <= .01;
                advancedStepped += stepped.getCentralBank().getAdvancedToTreasury();
                if (stepped.getCentralBank().getAdvancesToTreasury() > 0) onAdvances++;
            }
        } finally {
            System.setOut(out);
        }
        TimeSkipReport report = skipped.getSkipReport();
        check("a twelve-month skip from an empty treasury runs all twelve", ran, 12);
        assertTrue("...and does not stop short", !report.stoppedEarly() && skipped.takeSkipFailure() == null);
        assertTrue("its twin, stepped a month at a time, closes the audit every month", closed);
        assertTrue("...and ends where the skip did, to the cent",
                stepped.getMonth() == skipped.getMonth() && stepped.getCash() == skipped.getCash()
                        && stepped.getCentralBank().getAdvancesToTreasury()
                                == skipped.getCentralBank().getAdvancesToTreasury());
        assertTrue("fixture: the central bank advanced it something", advancedStepped > 0);
        check("the report says what the central bank advanced over the skip", report.getAdvancedDuringSkip(),
                advancedStepped);
        check("...which is what it printed for the treasury in those months", report.getAdvancedDuringSkip(),
                skipped.getCentralBank().getPrintedLifetime() - printedBefore);
        check("...and what the treasury owed it when the skip ended", report.getAdvancesOwedAtEnd(),
                skipped.getCentralBank().getAdvancesToTreasury());
        check("...and how many months it lived on them", report.getMonthsOnAdvances(), onAdvances);
        boolean says = false;
        for (String line : report.getHeadlines()) {
            if (line.startsWith("The treasury lived on the central bank's advances for " + onAdvances + " month")) says = true;
        }
        assertTrue("...which its headline says", says);
        System.out.printf("   an Insane city skipped a year: $%,.2fk advanced, $%,.2fk owed at the end, $%,.0fk in the"
                        + " treasury, %d people%n", report.getAdvancedDuringSkip(), report.getAdvancesOwedAtEnd(),
                skipped.getCash(), skipped.getPopulationManager().getPopulation());
    }
}
