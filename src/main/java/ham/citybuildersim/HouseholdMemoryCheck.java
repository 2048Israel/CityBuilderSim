package ham.citybuildersim;

/**
 * The households remember: the builder keeps what still fits.
 *
 * Jerus, 2026-09-11: a record of the households of each type, "so when the
 * model rebuilds it has a reference to try and keep but still allow change".
 * Keep what still fits, 1% a month re-forming on its own, every cell, in the
 * save and on the graphs. Every claim sets its own cause. See
 * claude/the-households-remember.md.
 */
public class HouseholdMemoryCheck {

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

    static PopulationCohorts pyramid(double babies, double children, double teens, double adults, double seniors) {
        PopulationCohorts c = new PopulationCohorts();
        c.restore(new double[] {babies, children, teens, adults, seniors, 0, 0, 0});
        return c;
    }

    static final FamilyStructure[] WORKING = java.util.Arrays.stream(FamilyStructure.values())
            .filter(s -> !s.isRetired() && s != FamilyStructure.SHARED_ADULTS)
            .toArray(FamilyStructure[]::new);

    static double total(FamilyModel f) {
        double t = 0;
        for (FamilyStructure s : FamilyStructure.values()) t += f.totalOf(s);
        return t;
    }

    public static void main(String[] args) throws Exception {

        double[] jobs = new double[PayTier.values().length];
        jobs[PayTier.UNSKILLED.ordinal()] = 600;
        jobs[PayTier.SKILLED.ordinal()] = 300;
        jobs[PayTier.values().length - 1] = 100;
        PopulationCohorts city = pyramid(800, 900, 650, 6000, 1700);

        /* ============ 1. a bare model does not remember ============ */
        System.out.println("--- a bare model builds from nothing ---");
        FamilyModel bare = new FamilyModel();
        bare.rebuild(pyramid(400, 400, 400, 3000, 900), jobs);
        bare.rebuild(city, jobs);
        FamilyModel fresh = new FamilyModel();
        fresh.rebuild(city, jobs);
        double worst = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            for (PayTier t : PayTier.values()) worst = Math.max(worst, Math.abs(bare.get(s, t) - fresh.get(s, t)));
        }
        check("a harness's FamilyModel rebuilt twice is the fresh build of the second month", worst, 0, 1e-9);
        assertTrue("...and it does not keep a record", !bare.remembersHouseholds());

        /* ============ 2. the same city a month later ============ */
        System.out.println("\n--- the same people, a month later ---");
        FamilyModel kept = new FamilyModel();
        kept.rememberHouseholds(true);
        kept.rebuild(city, jobs);
        assertTrue("the first month has no record to keep from, so it is the fresh build",
                Math.abs(total(kept) - total(fresh)) < 1e-9);
        assertTrue("...and after it there is one", kept.hasRecord());
        double before = total(kept);
        kept.rebuild(city, jobs);
        check("the second month keeps all but the 1% that re-forms", kept.getLastKept(),
                before * (1 - FamilyModel.REFORMING_EACH_MONTH), 1e-6);
        check("...the 1%", kept.getLastReformed(), before * FamilyModel.REFORMING_EACH_MONTH, 1e-6);
        check("...and nothing stopped fitting, because nobody changed", kept.getLastNoLongerFit(), 0, 1e-6);

        /* ============ 3. kept, and the rest built from the people left over ============ */
        System.out.println("\n--- arrivals: the kept households stay, the newcomers are built ---");
        FamilyModel memory = new FamilyModel();
        memory.rememberHouseholds(true);
        memory.rebuild(city, jobs);
        double[] monthOne = new double[FamilyStructure.values().length];
        for (FamilyStructure s : FamilyStructure.values()) monthOne[s.ordinal()] = memory.totalOf(s);

        // A fifth more adults arrive, nobody else changes.
        PopulationCohorts grown = pyramid(800, 900, 650, 7200, 1700);
        memory.rebuild(grown, jobs);

        // What the kept households use, and what is left for the builder.
        double r = 1 - FamilyModel.REFORMING_EACH_MONTH;
        double[] used = new double[5];
        for (FamilyStructure s : FamilyStructure.values()) {
            double k = monthOne[s.ordinal()] * r;
            for (AgeBand b : AgeBand.values()) used[b.ordinal()] += k * s.membersOf(b);
        }
        PopulationCohorts leftover = pyramid(grown.get(AgeBand.BABY) - used[0], grown.get(AgeBand.CHILD) - used[1],
                grown.get(AgeBand.TEEN) - used[2], grown.get(AgeBand.ADULT) - used[3],
                grown.get(AgeBand.SENIOR) - used[4]);
        FamilyModel builtFromLeftover = new FamilyModel();
        builtFromLeftover.rebuild(leftover, jobs);
        double worstShape = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            double expected = monthOne[s.ordinal()] * r + builtFromLeftover.totalOf(s);
            worstShape = Math.max(worstShape, Math.abs(memory.totalOf(s) - expected));
        }
        check("every shape is last month's, less 1%, plus the builder's answer for the people left over",
                worstShape, 0, 1e-6);
        /*
         * Not asserted: "fewer households change shape than a fresh build". It
         * was, and it failed with the mechanism working - a fifth more adults
         * mostly ADD households under either builder, so the difference is
         * the arrivals, not the memory. The claim above is the one the
         * fixture causes: nobody who was kept was redrawn.
         */
        for (FamilyStructure s : WORKING) {
            assertTrue("  nobody kept was redrawn: " + s.getLabel().toLowerCase(),
                    memory.totalOf(s) >= monthOne[s.ordinal()] * r - 1e-9);
        }

        /* ============ 4. a child grows up ============ */
        System.out.println("\n--- the babies turn six ---");
        FamilyModel ageing = new FamilyModel();
        ageing.rememberHouseholds(true);
        ageing.rebuild(city, jobs);
        double babiesHoused = 0;
        double[] had = new double[FamilyStructure.values().length];
        for (FamilyStructure s : FamilyStructure.values()) {
            had[s.ordinal()] = ageing.totalOf(s);
            babiesHoused += had[s.ordinal()] * s.membersOf(AgeBand.BABY);
        }
        // Half the housed babies are gone from the band, into the children's.
        double babiesNow = babiesHoused * r / 2;
        ageing.rebuild(pyramid(babiesNow, 900 + babiesHoused / 2, 650, 6000, 1700), jobs);
        double fits = babiesNow / (babiesHoused * r);
        double dissolved = 0;
        for (FamilyStructure s : WORKING) {
            if (s.membersOf(AgeBand.BABY) > 0) dissolved += had[s.ordinal()] * r * (1 - fits);
        }
        check("the households that needed a baby shrink by exactly the babies who are gone",
                ageing.getLastNoLongerFit(), dissolved, 1e-6);
        check("...a couple with a baby is last month's, less 1%, times what fits - no new ones form",
                ageing.totalOf(FamilyStructure.COUPLE_BABY), had[FamilyStructure.COUPLE_BABY.ordinal()] * r * fits, 1e-6);
        assertTrue("...and the grown children found households of their own among the new ones",
                ageing.getLastNew() > 0);

        /* ============ 5. the tiers follow the jobs ============ */
        System.out.println("\n--- the jobs move to the skilled tier ---");
        FamilyModel tiers = new FamilyModel();
        tiers.rememberHouseholds(true);
        tiers.rebuild(city, jobs);
        FamilyModel sameJobs = new FamilyModel();
        sameJobs.rememberHouseholds(true);
        sameJobs.rebuild(city, jobs);
        double[] skilledJobs = jobs.clone();
        skilledJobs[PayTier.UNSKILLED.ordinal()] = 300;
        skilledJobs[PayTier.SKILLED.ordinal()] = 600;
        tiers.rebuild(city, skilledJobs);
        sameJobs.rebuild(city, jobs);
        double working = 0;
        for (FamilyStructure s : WORKING) working += tiers.totalOf(s);
        double jobTotal = 0;
        for (double j : skilledJobs) jobTotal += j;
        double worstTier = 0;
        for (PayTier t : PayTier.values()) {
            double inTier = 0;
            for (FamilyStructure s : WORKING) inTier += tiers.get(s, t);
            worstTier = Math.max(worstTier, Math.abs(inTier - working * skilledJobs[t.ordinal()] / jobTotal));
        }
        check("every tier holds the jobs' share of the working households", worstTier, 0, 1e-6);
        double worstRow = 0;
        for (FamilyStructure s : WORKING) worstRow = Math.max(worstRow, Math.abs(tiers.totalOf(s) - sameJobs.totalOf(s)));
        check("...and no shape gained or lost a household for it", worstRow, 0, 1e-6);

        /* ============ 6. a save ============ */
        System.out.println("\n--- a save, and a save from before ---");
        FamilyModel saved = new FamilyModel();
        saved.rememberHouseholds(true);
        saved.rebuild(city, jobs);
        saved.rebuild(grown, jobs);
        double[] state = saved.toSaveArray();
        FamilyModel loaded = new FamilyModel();
        loaded.rememberHouseholds(true);
        loaded.restore(state);
        assertTrue("the record comes back", loaded.hasRecord());
        check("...with the month's kept households", loaded.getLastKept(), saved.getLastKept(), 0);
        saved.rebuild(city, jobs);
        loaded.rebuild(city, jobs);
        double worstLoad = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            for (PayTier t : PayTier.values()) worstLoad = Math.max(worstLoad, Math.abs(saved.get(s, t) - loaded.get(s, t)));
        }
        check("the month after a load keeps exactly what the unloaded city keeps", worstLoad, 0, 0);

        int oldLength = state.length - (FamilyStructure.values().length * PayTier.values().length + 5);
        FamilyModel old = new FamilyModel();
        old.rememberHouseholds(true);
        old.restore(java.util.Arrays.copyOf(state, oldLength));
        assertTrue("a save from before the record still loads its households", old.totalHouseholds() > 0);
        assertTrue("...and has no record", !old.hasRecord());
        old.rebuild(city, jobs);
        FamilyModel oldFresh = new FamilyModel();
        oldFresh.rebuild(city, jobs);
        check("...so its first month is the fresh build", total(old), total(oldFresh), 1e-9);

        /* ============ 7. a city ============ */
        System.out.println("\n--- a played city ---");
        java.nio.file.Path root = java.nio.file.Files.createTempDirectory("householdmemory");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplateByName("House"), 400, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 10, true);
            b.addStack(b.getTemplateByName("Textile Mill"), 3, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            g.simulateMonths(36);
        });
        FamilyModel families = g.getFamilies();
        assertTrue("the game's families remember", families.remembersHouseholds() && families.hasRecord());
        System.out.printf("   kept %,.1f  re-formed %,.1f  no longer fitted %,.1f  new %,.1f%n",
                families.getLastKept(), families.getLastReformed(), families.getLastNoLongerFit(), families.getLastNew());
        assertTrue("...and keep most of last month's", families.getLastKept() > families.getLastNew());
        HistorySave history = g.getHistorySave();
        for (FamilyStructure s : new FamilyStructure[]{FamilyStructure.SINGLE_ADULT, FamilyStructure.COUPLE_CHILD,
                FamilyStructure.SENIOR_ALONE}) {
            java.util.List<? extends Number> series = history.seriesByName().get(HistorySave.householdKey(s));
            check("the graph holds this month's " + s.getLabel().toLowerCase(),
                    series == null || series.isEmpty() ? -1 : series.get(series.size() - 1).doubleValue(),
                    Math.round(families.totalOf(s) * 100) / 100.0, 1e-9);
        }
        assertTrue("fixture: the city saved", g.saveGame(1, "memory").ok);
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));
        double worstGame = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            for (PayTier t : PayTier.values()) {
                worstGame = Math.max(worstGame, Math.abs(back.getFamilies().getFormed(s, t) - families.getFormed(s, t)));
            }
        }
        check("a played city's record survives a save", worstGame, 0, 1e-9);
        quietly(() -> { g.simulateMonths(1); back.simulateMonths(1); });
        double worstNext = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            worstNext = Math.max(worstNext, Math.abs(back.getFamilies().totalOf(s) - g.getFamilies().totalOf(s)));
        }
        check("...and the month after, both cities hold the same households", worstNext, 0, 1e-6);

        System.out.println();
        System.out.println(fails == 0 ? "The households remember what still fits." : fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }
}
