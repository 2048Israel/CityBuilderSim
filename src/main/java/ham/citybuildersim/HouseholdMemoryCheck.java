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

    /** A pyramid from the five bands this fixture cares about. Elders stay empty. */
    static PopulationCohorts pyramid(double babies, double children, double teens, double adults, double seniors) {
        PopulationCohorts c = new PopulationCohorts();
        // The LEGACY_BANDS path on purpose: five values plus three scalars is
        // what these arguments are, whatever this build's band list has become.
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

    /* ----- the two adult stages (HS2) -----
       Each stage keeps and builds out of its own people, so a claim about
       "the builder's answer for the people left over" is one stage's pool
       at a time: the families' adults split as the pyramid's stages are,
       their children by FamilyModel.youngShareOf(). */

    /** A shape's households of one stage, every tier. */
    static double totalOf(FamilyModel f, FamilyStructure s, AdultStage g) {
        double t = 0;
        for (PayTier tier : PayTier.values()) t += f.get(s, tier, g);
        return t;
    }

    /** Of a band in the families, the share a stage holds: the children by the dials, the adults by the pyramid. */
    static double shareOf(AgeBand b, AdultStage g, PopulationCohorts c) {
        double y = b == AgeBand.ADULT
                ? c.get(AgeBand.ADULT) > 0 ? c.get(AdultStage.YOUNG) / c.get(AgeBand.ADULT) : 0
                : FamilyModel.youngShareOf(b);
        return g == AdultStage.YOUNG ? y : 1 - y;
    }

    /** A pyramid with its adults' young named: the cohorts' own save, the two stages after its seven scalars. */
    static PopulationCohorts staged(double babies, double children, double teens, double adults, double young) {
        String[] names = PopulationCohorts.saveBands();
        double[] a = new double[names.length + 9];
        a[AgeBand.BABY.ordinal()] = babies;
        a[AgeBand.CHILD.ordinal()] = children;
        a[AgeBand.TEEN.ordinal()] = teens;
        a[AgeBand.ADULT.ordinal()] = adults;
        a[names.length + 7] = young;
        a[names.length + 8] = Math.max(0, adults - young);
        PopulationCohorts c = new PopulationCohorts();
        c.restore(names, a);
        return c;
    }

    /**
     * The bare builder's answer for one stage's own pool (babies, children,
     * teens, adults): a pyramid whose adults are all that stage's; the other
     * stage has no adults and forms nothing. The young's children are scaled
     * so the young's share of them is the pool, and what the young's builder
     * leaves of them is offered to the established, who have no adults here,
     * so it is the orphans' (youngLeftOf()). The established's are the pool
     * itself: with no young adults the young's share of them is offered to
     * the established whole (HS2's offer, FamilyModel.offerChildren()).
     */
    static FamilyModel builtFor(AdultStage g, double[] pool, double[] jobs) {
        double[] kids = new double[3];
        AgeBand[] bands = { AgeBand.BABY, AgeBand.CHILD, AgeBand.TEEN };
        for (int b = 0; b < 3; b++) {
            double share = FamilyModel.youngShareOf(bands[b]);
            kids[b] = g == AdultStage.ESTABLISHED ? Math.max(0, pool[b])
                    : share > 0 ? Math.max(0, pool[b]) / share : 0;
        }
        FamilyModel f = new FamilyModel();
        f.rebuild(staged(kids[0], kids[1], kids[2], Math.max(0, pool[3]), g == AdultStage.YOUNG ? Math.max(0, pool[3]) : 0), jobs);
        return f;
    }

    /** Of builtFor(YOUNG, pool)'s children of a band, what the young's builder left: its orphans less the established's share, who had no adults to go to. */
    static double youngLeftOf(FamilyModel builtYoung, AgeBand band, double pool) {
        double share = FamilyModel.youngShareOf(band);
        if (!(share > 0)) return 0;
        return Math.max(0, builtYoung.getOrphans(band) - Math.max(0, pool) / share * (1 - share));
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
        // ...and each adult stage's own (HS2).
        double[][] monthOneBy = new double[AdultStage.values().length][FamilyStructure.values().length];
        for (AdultStage g : AdultStage.values()) {
            for (FamilyStructure s : WORKING) monthOneBy[g.ordinal()][s.ordinal()] = totalOf(memory, s, g);
        }

        // A fifth more adults arrive, nobody else changes.
        PopulationCohorts grown = pyramid(800, 900, 650, 7200, 1700);
        memory.rebuild(grown, jobs);

        /*
         * What each stage's kept households use, and what is left of the
         * stage's own people for the builder (HS2): the working shapes stage
         * by stage, and the retired as before, out of the seniors. The
         * established's kept households draw on what the young's left of the
         * children where their own fall short (they formed with the young's
         * leftovers); the young build out of what is left of theirs, and the
         * children their builder leaves are offered to the established's
         * before the orphans' (FamilyModel.offerChildren()).
         */
        double r = 1 - FamilyModel.REFORMING_EACH_MONTH;
        AgeBand[] familyBands = { AgeBand.BABY, AgeBand.CHILD, AgeBand.TEEN, AgeBand.ADULT };
        int y = AdultStage.YOUNG.ordinal(), e = AdultStage.ESTABLISHED.ordinal();
        double[][] pool = new double[AdultStage.values().length][familyBands.length];
        for (AdultStage g : AdultStage.values()) {
            for (int b = 0; b < familyBands.length; b++) pool[g.ordinal()][b] = grown.get(familyBands[b]) * shareOf(familyBands[b], g, grown);
            for (FamilyStructure s : WORKING) {
                double k = monthOneBy[g.ordinal()][s.ordinal()] * r;
                for (int b = 0; b < familyBands.length; b++) pool[g.ordinal()][b] -= k * s.membersOf(familyBands[b]);
            }
        }
        double drawn = 0, offered = 0;
        for (int b = 0; b < 3; b++) {
            double draw = Math.min(Math.max(0, pool[y][b]), Math.max(0, -pool[e][b]));
            pool[y][b] -= draw;
            pool[e][b] += draw;
            drawn += draw;
        }
        FamilyModel youngBuilt = builtFor(AdultStage.YOUNG, pool[y], jobs);
        for (int b = 0; b < 3; b++) {
            double left = youngLeftOf(youngBuilt, familyBands[b], pool[y][b]);
            pool[e][b] += left;
            offered += left;
        }
        FamilyModel establishedBuilt = builtFor(AdultStage.ESTABLISHED, pool[e], jobs);
        double worstShape = 0;
        for (FamilyStructure s : WORKING) {
            worstShape = Math.max(worstShape, Math.abs(totalOf(memory, s, AdultStage.YOUNG)
                    - (monthOneBy[y][s.ordinal()] * r + totalOf(youngBuilt, s, AdultStage.YOUNG))));
            worstShape = Math.max(worstShape, Math.abs(totalOf(memory, s, AdultStage.ESTABLISHED)
                    - (monthOneBy[e][s.ordinal()] * r + totalOf(establishedBuilt, s, AdultStage.ESTABLISHED))));
        }
        System.out.printf("   the established's kept households drew %,.2f children of the young's; the young's builder offered them %,.2f%n",
                drawn, offered);
        assertTrue("fixture: the young's builder left children over, offered to the established", offered > 0);
        assertTrue("fixture: and the established's kept households drew on the young's children", drawn > 0);
        double usedSeniors = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            if (s.isRetired()) usedSeniors += monthOne[s.ordinal()] * r * s.membersOf(AgeBand.SENIOR);
        }
        FamilyModel retiredFromLeftover = new FamilyModel();
        retiredFromLeftover.rebuild(pyramid(0, 0, 0, 0, grown.get(AgeBand.SENIOR) - usedSeniors), jobs);
        for (FamilyStructure s : FamilyStructure.values()) {
            if (!s.isRetired()) continue;
            double expected = monthOne[s.ordinal()] * r + retiredFromLeftover.totalOf(s);
            worstShape = Math.max(worstShape, Math.abs(memory.totalOf(s) - expected));
        }
        check("every shape is last month's, less 1%, plus the builder's answer for the people left over,"
                + " each adult stage out of its own people and the established of the young's leftover children", worstShape, 0, 1e-6);
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
        // ...and each adult stage's (HS2): its own babies, the share of the band the dial gives it.
        double[][] hadBy = new double[AdultStage.values().length][FamilyStructure.values().length];
        double[] babiesHousedBy = new double[AdultStage.values().length];
        for (AdultStage g : AdultStage.values()) {
            for (FamilyStructure s : WORKING) {
                hadBy[g.ordinal()][s.ordinal()] = totalOf(ageing, s, g);
                babiesHousedBy[g.ordinal()] += hadBy[g.ordinal()][s.ordinal()] * s.membersOf(AgeBand.BABY);
            }
        }
        // Half the housed babies are gone from the band, into the children's.
        double babiesNow = babiesHoused * r / 2;
        ageing.rebuild(pyramid(babiesNow, 900 + babiesHoused / 2, 650, 6000, 1700), jobs);
        double dissolved = 0, coupleBaby = 0;
        for (AdultStage g : AdultStage.values()) {
            double stayed = babiesHousedBy[g.ordinal()] * r;
            double fits = stayed > 0 ? Math.min(1, babiesNow * shareOf(AgeBand.BABY, g, city) / stayed) : 1;
            for (FamilyStructure s : WORKING) {
                if (s.membersOf(AgeBand.BABY) > 0) dissolved += hadBy[g.ordinal()][s.ordinal()] * r * (1 - fits);
            }
            coupleBaby += hadBy[g.ordinal()][FamilyStructure.COUPLE_BABY.ordinal()] * r * fits;
        }
        check("the households that needed a baby shrink by exactly the babies who are gone, stage by stage",
                ageing.getLastNoLongerFit(), dissolved, 1e-6);
        check("...a couple with a baby is last month's, less 1%, times what fits in its stage - no new ones form",
                ageing.totalOf(FamilyStructure.COUPLE_BABY), coupleBaby, 1e-6);
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
        // Named on both axes: this array was written by THIS build, and the
        // nameless path is five bands and thirteen shapes for ever.
        loaded.restore(PopulationCohorts.saveBands(), FamilyModel.saveShapes(), state);
        assertTrue("the record comes back", loaded.hasRecord());
        check("...with the month's kept households", loaded.getLastKept(), saved.getLastKept(), 0);
        saved.rebuild(city, jobs);
        loaded.rebuild(city, jobs);
        double worstLoad = 0;
        for (FamilyStructure s : FamilyStructure.values()) {
            for (PayTier t : PayTier.values()) worstLoad = Math.max(worstLoad, Math.abs(saved.get(s, t) - loaded.get(s, t)));
        }
        check("the month after a load keeps exactly what the unloaded city keeps", worstLoad, 0, 0);

        /*
         * THE BOUNDARY COMES FROM THE MODEL, NOT FROM ARITHMETIC ON TODAY'S
         * LENGTH. This worked the old length out by subtracting the memory
         * block from the full array, which is correct exactly while the memory
         * block is the LAST thing in it. A tail was appended on 2026-09-15 and
         * the subtraction started producing a length no reader accepts, so this
         * fixture failed reporting that a pre-record save loads no households -
         * which was true of the fixture's array and of nothing else.
         */
        int oldLength = FamilyModel.slotsBeforeMemory();
        FamilyModel old = new FamilyModel();
        old.rememberHouseholds(true);
        old.restore(PopulationCohorts.saveBands(), FamilyModel.saveShapes(),
                java.util.Arrays.copyOf(state, oldLength));
        assertTrue("a save from before the record still loads its households", old.totalHouseholds() > 0);
        assertTrue("...and has no record", !old.hasRecord());
        old.rebuild(city, jobs);
        FamilyModel oldFresh = new FamilyModel();
        oldFresh.rebuild(city, jobs);
        check("...so its first month is the fresh build", total(old), total(oldFresh), 1e-9);

        /* ============ 7. a city ============ */
        System.out.println("\n--- a played city ---");
        java.nio.file.Path root = TempDirs.create("householdmemory");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));
        Game g = new Game(files);
        quietly(() -> {
            g.run();
            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplateByName("House"), 400, true);
            b.addStack(b.getTemplateByName("Convenience Store"), 10, true);
            b.addStack(b.getTemplateByName("Industrial Bakery"), 3, true);
            b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
            b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
            /*
             * ...AND THE GROUND THEY STAND ON (BA1, 2026-11-04). addStack() puts a building up without allocating its land,
             * and the load then derived allocation from the buildings, so the city
             * saved below read 6,290,000 sq ft more free than its twin loaded from it: 525,192 free live, none loaded. On
             * BA1's path the city is short of ground by month 36 and the twins' planners part on it ("no land - 0 m2 free"
             * in the loaded one only). The city is handed the ground its fixture's buildings take and it is allocated, as a
             * its fixture requires; what is free stays what it was before them. AW2 now saves that ledger explicitly.
             */
            LandManager land = g.getLandManager();
            double unallocated = b.getTotalLandFootprint() - land.getAllocatedSqFt();
            land.setOwnedSqFt(land.getOwnedSqFt() + unallocated);
            land.setAllocatedSqFt(b.getTotalLandFootprint());
            g.simulateMonths(36);
        });
        assertTrue("fixture: the city's allocated ground is what its buildings stand on",
                Math.abs(g.getLandManager().getAllocatedSqFt() - g.getBuildingManager().getTotalLandFootprint()) < 1);
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
