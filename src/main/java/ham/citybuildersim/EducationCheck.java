package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies the schools: who gets taught, who is allowed to practise, and what
 * it costs. Not part of the game.
 *
 * WHY THIS EXISTS
 *
 * Education is the longest feedback loop in the game - a medical school built
 * today is doctors in the 2040s - and a loop that long is one nobody can debug
 * by playing. Every failure mode here looks like patience:
 *
 * 1. A SCHOOL THAT TEACHES NOBODY looks exactly like a school whose graduates
 *    have not arrived yet. The first version of this feature had a medical
 *    school that enrolled zero students for three hundred and sixty months
 *    while costing $78M, because the return on the course was computed against
 *    the doctor's own wage and therefore came out at exactly 1.00.
 *
 * 2. A SCHOOL THAT TEACHES TOO FAST empties the band it draws from. The first
 *    version took ninety per cent of every diploma-holder in the city the month
 *    the college opened; the diploma band went from 1,390 people to 28 and
 *    never recovered, because a STOCK was being consumed at the rate a FLOW
 *    should be.
 *
 * 3. A LICENCE THAT LEAKS staffs posts out of nothing, which is the
 *    220-doctors bug in a new hat.
 */
public class EducationCheck {

    static int fails = 0;
    static final PrintStream OUT = System.out;
    static final PrintStream QUIET = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
    });

    static void quietly(Runnable work) {
        System.setOut(QUIET);
        try { work.run(); } finally { System.setOut(OUT); }
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-64s %s%n", label, ok ? "OK" : "FAIL");
    }

    static BuildingsTemplate t(Game g, String name) {
        for (BuildingsTemplate b : g.getBuildingManager().getTemplates()) {
            if (b.getName().equals(name)) return b;
        }
        throw new IllegalStateException("no template " + name);
    }

    static void build(Game g, String name, int n) { g.buildStack(t(g, name), n, true); }

    /** A funded city with room, so the thing under test is never money or land. */
    static Game city(GameFiles files) {
        Game g = new Game(files == null ? GameFiles.scratch("educheck") : files);
        g.newGame();
        g.getGovernmentInvestor().spend(-900_000_000);
        g.getLandManager().setOwnedSqFt(
                g.getLandManager().getOwnedSqFt() + 4_000_000_000L);
        build(g, "Low-Rise Apartments", 120);
        build(g, "General Hospital", 3);
        build(g, "Small Grocery Store", 14);
        build(g, "Coal Power Plant", 4);
        build(g, "Water Treatment Plant", 4);
        build(g, "Paved Road", 60);
        build(g, "Construction Depot", 6);
        return g;
    }

    static void schools(Game g) {
        build(g, "Elementary School", 14);
        build(g, "Middle School", 14);
        build(g, "High School", 10);
        build(g, "Community College", 6);
        build(g, "University", 3);
    }

    public static void main(String[] args) throws Exception {

        /* ============ 1. THE PROFESSION IS GATED ============

           The headline. A city can IMPORT some doctors - a share of the
           graduates who move in already hold the licence, which is what stops
           this being a wall - but it cannot make one, and it cannot import
           enough for a hospital-building city.
           ================================================================= */
        System.out.println("--- without a medical school, a city cannot make a doctor ---");

        Game bare = city(null);
        quietly(() -> bare.simulateMonths(360));

        PopulationManager bp = bare.getPopulationManager();
        int doctorPosts = bp.getJobs()[JobType.UNIV_DOCTOR.ordinal()];
        double licensed = bp.getLicensed(JobType.UNIV_DOCTOR);
        int unfilled = bp.getJobVacancy()[JobType.UNIV_DOCTOR.ordinal()];
        double graduates = bp.workforceByBand()[WageBand.UNIVERSITY.ordinal()];

        System.out.printf("   %,d doctor posts, %,.0f licensed, %,d unfilled,"
                + " %,.0f graduates in town%n",
                doctorPosts, licensed, unfilled, graduates);

        assertTrue("the fixture built hospitals, or this proves nothing", doctorPosts > 0);
        assertTrue("there ARE graduates - they simply cannot practise medicine",
                graduates > licensed * 1.5);
        assertTrue("so doctor posts stand empty", unfilled > 0);

        /*
         * NOT ZERO EITHER. A wall would be worse than a cost: the world has
         * doctors in it and some of them move. The point is that it is not
         * enough, which is what makes the school worth $78M.
         */
        assertTrue("...but some doctors moved in anyway", licensed > 0);
        assertTrue("...and never more than the graduates who contain them",
                licensed <= graduates + 1);

        /* ============ 2. AND WITH ONE, IT CAN ============ */
        System.out.println("\n--- build the school and it staffs itself ---");

        Game taught = city(null);
        quietly(() -> {
            schools(taught);
            build(taught, "Medical School", 1);
            taught.simulateMonths(360);
        });

        PopulationManager tp = taught.getPopulationManager();
        double taughtLicensed = tp.getLicensed(JobType.UNIV_DOCTOR);
        int taughtUnfilled = tp.getJobVacancy()[JobType.UNIV_DOCTOR.ordinal()];

        System.out.printf("   %,.0f licensed, %,d unfilled%n", taughtLicensed, taughtUnfilled);

        assertTrue("the school produced doctors", taughtLicensed > licensed * 2);
        assertTrue("...enough to staff the hospitals", taughtUnfilled == 0);

        /* ============ 3. NO SCHOOL TEACHES NOBODY ============

           Failure mode 1, nailed down. A school that enrols zero looks exactly
           like one whose first cohort has not graduated yet, so it is asserted
           rather than watched for.
           ================================================================= */
        System.out.println("\n--- every school actually enrols somebody ---");

        Education ed = taught.getEducation();
        for (EducationType type : EducationType.values()) {
            if (type == EducationType.NONE) continue;
            boolean built = type == EducationType.MEDICAL || !type.isProfessional();
            if (!built) continue;
            System.out.printf("   %-24s enrolled %,10.0f%n",
                    type.getLabel(), ed.getEnrolled(type));
            assertTrue("  " + type.getLabel() + " has students in it",
                    ed.getEnrolled(type) > 0);
        }

        /* ============ 4. AND DOES NOT EMPTY THE BAND IT DRAWS FROM ============

           Failure mode 2. Colleges and universities both take diploma-holders,
           and between them they can seat far more people than the high schools
           produce - so without a rate limit they consume the entire stock in
           one month and the band never recovers.
           ================================================================= */
        System.out.println("\n--- and does not drain the band beneath it ---");

        double[] bands = tp.workforceByBand();
        for (WageBand band : WageBand.values()) {
            System.out.printf("   %-12s %,9.0f%n", band.label(), bands[band.ordinal()]);
        }
        assertTrue("the diploma band survives having colleges above it",
                bands[WageBand.DIPLOMA.ordinal()] > tp.getWorkforce() * .02);
        assertTrue("...and the city is genuinely better educated than the bare one",
                bands[WageBand.COLLEGE.ordinal()] + bands[WageBand.UNIVERSITY.ordinal()]
                        > bp.workforceByBand()[WageBand.COLLEGE.ordinal()]
                        + bp.workforceByBand()[WageBand.UNIVERSITY.ordinal()]);

        /* ============ 5. A DEGREE IS A MOVE, NOT AN APPEARANCE ============

           Somebody who finishes a degree LEAVES the band they were in. If the
           flow only ever added, the city would end up with more people in its
           bands than it has workers, and the allocator would staff posts out of
           the difference.
           ================================================================= */
        System.out.println("\n--- a graduate stops being a diploma-holder ---");

        double[] flow = ed.getGraduates();
        double net = 0;
        for (double f : flow) net += f;
        System.out.printf("   this month's band flow sums to %+.4f%n", net);
        assertTrue("the month's schooling moves people rather than making them",
                Math.abs(net) < 1e-6);

        double bandTotal = 0;
        for (double b : bands) bandTotal += b;
        // Plus the students: since the pipeline exists (2026-09-06) the people
        // in a lecture theatre are out of the supply, and the supply plus the
        // students is the workforce.
        double studying = tp.getStudyingTotal();
        System.out.printf("   supply %,.0f + studying %,.0f = workforce %,d%n",
                bandTotal, studying, tp.getWorkforce());
        assertTrue("the bands plus the students still add up to the workforce",
                Math.abs(bandTotal + studying - tp.getWorkforce()) < 2);
        assertTrue("fixture: somebody is actually studying", studying > 1);

        /* ============ 6. LICENCES CANNOT OUTNUMBER GRADUATES ============

           A licence holder is a graduate first. When graduates die or leave,
           the licences have to go with them - otherwise the city slowly
           accumulates doctors who are not people, and the allocator will
           happily staff hospitals out of them.
           ================================================================= */
        System.out.println("\n--- and a licence belongs to a person ---");

        for (WageBand band : WageBand.values()) {
            double held = 0;
            for (JobType job : JobType.values()) {
                if (WageBand.of(job) == band) held += tp.getLicensed(job);
            }
            if (held <= 0) continue;
            System.out.printf("   %-12s %,8.0f licensed of %,8.0f workers%n",
                    band.label(), held, bands[band.ordinal()]);
            assertTrue("  no more " + band.label() + " licences than there are people",
                    held <= bands[band.ordinal()] + 1);
        }

        /* ============ 7. THE PIPELINE IS ITS NARROWEST STAGE ============ */
        System.out.println("\n--- the basic ladder is only as wide as its bottleneck ---");

        Game pinched = city(null);
        quietly(() -> {
            /*
             * NO HIGH SCHOOL AT ALL, and the first version built one.
             *
             * One high school seats 900, which in a city this size covers every
             * teenager in it - so the "bottleneck" was at a hundred per cent
             * and the test was measuring a city with no bottleneck. A fixture
             * has to CAUSE the condition it is testing, and a missing stage is
             * both the clearest way to cause it and the case a player will
             * actually hit.
             */
            build(pinched, "Elementary School", 20);
            build(pinched, "Middle School", 20);
            pinched.simulateMonths(180);
        });

        Education pe = pinched.getEducation();
        System.out.printf("   elementary %.0f%%  middle %.0f%%  high %.0f%%  ->  basic %.0f%%"
                + "  (bottleneck %s)%n",
                pe.getCoverage(EducationType.ELEMENTARY) * 100,
                pe.getCoverage(EducationType.MIDDLE) * 100,
                pe.getCoverage(EducationType.HIGH) * 100,
                pe.basicCoverage() * 100, pe.basicBottleneck().getLabel());

        assertTrue("plenty of primary places", pe.getCoverage(EducationType.ELEMENTARY) > .9);
        assertTrue("...and no high school at all", pe.getCoverage(EducationType.HIGH) <= 0);
        assertTrue("the pipeline reports the SMALLEST, not the average",
                Math.abs(pe.basicCoverage() - pe.getCoverage(EducationType.HIGH)) < 1e-9);
        assertTrue("...and names the building to fix",
                pe.basicBottleneck() == EducationType.HIGH);
        assertTrue("so the city produces no diplomas of its own at all",
                pe.basicCoverage() <= 0);

        /* ============ 8. THE SUBSIDY IS A REAL DIAL ============

           The poverty trap, stated as a test. At no subsidy a university place
           costs a diploma-holder more than half a month's pay, and almost
           nobody goes - so a city can own the buildings, want the graduates,
           and produce none of them.
           ================================================================= */
        System.out.println("\n--- and tuition decides who can actually go ---");

        Game free = city(null);
        Game paid = city(null);
        quietly(() -> {
            schools(free);
            free.getEducation().setTuitionSubsidy(1.0);
            free.simulateMonths(240);

            schools(paid);
            paid.getEducation().setTuitionSubsidy(0.0);
            paid.simulateMonths(240);
        });

        double freeGrads = free.getPopulationManager()
                .workforceByBand()[WageBand.UNIVERSITY.ordinal()];
        double paidGrads = paid.getPopulationManager()
                .workforceByBand()[WageBand.UNIVERSITY.ordinal()];

        System.out.printf("   free tuition: %,.0f graduates    full price: %,.0f%n",
                freeGrads, paidGrads);
        System.out.printf("   and the bill:  $%,.0f            $%,.0f%n",
                free.getEducation().getGrossCost(), paid.getEducation().getGrossCost());

        assertTrue("paying for it yourself keeps people out", freeGrads > paidGrads);
        assertTrue("...and free education costs the city more",
                free.getEducation().getSubsidy() > paid.getEducation().getSubsidy());

        /* ============ 9. IT SURVIVES A SAVE ============

           A licence took seven years to acquire and nothing in a closing
           balance reproduces it.
           ================================================================= */
        System.out.println("\n--- through a save ---");

        Path root = Files.createTempDirectory("education");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        Game lived = city(files);
        quietly(() -> {
            schools(lived);
            build(lived, "Medical School", 1);
            lived.getEducation().setTuitionSubsidy(.8);
            lived.simulateMonths(180);
        });

        double[] licencesBefore = lived.getPopulationManager().getLicensedHeads().clone();
        double[] taughtBefore = lived.getEducation().getEverGraduated().clone();
        double subsidyBefore = lived.getEducation().getTuitionSubsidy();

        assertTrue("the city saved", lived.saveGame(1, "education").ok);

        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));

        double[] licencesAfter = back.getPopulationManager().getLicensedHeads();
        double[] taughtAfter = back.getEducation().getEverGraduated();

        boolean sameLicences = true;
        for (int i = 0; i < licencesBefore.length; i++) {
            if (Math.abs(licencesBefore[i] - licencesAfter[i]) > 1e-9) sameLicences = false;
        }
        boolean sameTaught = true;
        for (int i = 0; i < taughtBefore.length; i++) {
            if (Math.abs(taughtBefore[i] - taughtAfter[i]) > 1e-9) sameTaught = false;
        }

        System.out.printf("   %,.0f doctors before, %,.0f after%n",
                licencesBefore[JobType.UNIV_DOCTOR.ordinal()],
                licencesAfter[JobType.UNIV_DOCTOR.ordinal()]);

        assertTrue("the fixture actually licensed somebody",
                licencesBefore[JobType.UNIV_DOCTOR.ordinal()] > 0);
        assertTrue("every licence came back", sameLicences);
        assertTrue("...and the running totals with them", sameTaught);
        assertTrue("...and the tuition policy the player chose",
                Math.abs(back.getEducation().getTuitionSubsidy() - subsidyBefore) < 1e-9);

        // And the students part way through, who are the pipeline itself.
        boolean sameStudents = true;
        double inFlight = 0;
        for (EducationType type : EducationType.values()) {
            if (!type.isAdult()) continue;
            inFlight += lived.getEducation().studentBody(type);
            if (Math.abs(lived.getEducation().studentBody(type)
                    - back.getEducation().studentBody(type)) > 1e-6) sameStudents = false;
        }
        System.out.printf("   %,.0f students in flight before the save%n", inFlight);
        assertTrue("fixture: somebody is part way through a course", inFlight > 10);
        assertTrue("...and every student came back", sameStudents);
        assertTrue("...and they are out of the supply on both sides",
                Math.abs(lived.getPopulationManager().getStudyingTotal()
                        - back.getPopulationManager().getStudyingTotal()) < 1e-6
                && back.getPopulationManager().getStudyingTotal() > 10);

        /* ============ 9b. THE WAIT IS REAL ============

           A university built today is graduates in four years, not this
           month. The steady-state throughput was always right; the lag was
           missing until 2026-09-06, and a mechanic whose whole character is
           the wait has to be measured on the wait.
           ================================================================= */
        System.out.println("\n--- the wait is real ---");

        Game waits = city(null);
        quietly(() -> {
            build(waits, "Elementary School", 4);
            build(waits, "Middle School", 3);
            build(waits, "High School", 3);
            waits.simulateMonths(120);   // a city with diploma-holders and no university
        });
        double universityBefore = waits.getPopulationManager().workforceByBand()[WageBand.UNIVERSITY.ordinal()]
                + waits.getPopulationManager().getStudyingTotal() * 0;   // graduates, not students
        double everBefore = waits.getEducation().getEverGraduated()[WageBand.UNIVERSITY.ordinal()];
        quietly(() -> build(waits, "University", 1));

        int course = EducationType.UNIVERSITY.months();
        int firstGraduation = -1;
        double peakStudents = 0;
        for (int m = 1; m <= course + 12; m++) {
            quietly(() -> waits.simulateMonths(1));
            peakStudents = Math.max(peakStudents, waits.getEducation().studentBody(EducationType.UNIVERSITY));
            if (firstGraduation < 0
                    && waits.getEducation().getEverGraduated()[WageBand.UNIVERSITY.ordinal()] > everBefore + .5) {
                firstGraduation = m;
            }
        }
        System.out.printf("   course %d months; first graduates in month %d; peak %,.0f students%n",
                course, firstGraduation, peakStudents);
        assertTrue("fixture: people enrolled", peakStudents > 10);
        assertTrue("nobody graduates before the course is over",
                firstGraduation < 0 || firstGraduation >= course);
        assertTrue("...and somebody graduates once it is",
                firstGraduation > 0 && firstGraduation <= course + 6);
        assertTrue("...while the students were out of the supply",
                waits.getPopulationManager().getStudyingTotal() > 10);

        /* ============ 10. AND THE TREASURY PAYS FOR IT ============ */
        System.out.println("\n--- and it is on the city's books ---");

        Education le = lived.getEducation();
        EconomyManager lm = lived.getEconomyManager();
        System.out.printf("   bill $%,.1fk (%,.0f wages + %,.0f upkeep + %,.0f tuition),"
                + " fees $%,.1fk%n",
                le.getGrossCost(), le.getPayroll(), le.getUpkeep(), le.getSubsidy(),
                le.getFees());

        assertTrue("the schools cost something", le.getGrossCost() > 0);
        assertTrue("...charged to the treasury",
                Math.abs(lm.getEducationBill() - le.getGrossCost()) < 1e-9);
        assertTrue("...and on the national accounts' expenditure",
                Math.abs(lm.getNationalAccounts().getEducationSpending()
                        - le.getGrossCost()) < 1e-9);
        assertTrue("it never pays for itself, which is the point",
                le.getNetCost() > 0);

        /* ============ 11. THE UNSKILLED BAND IS A REPORT CARD ============

           The other half of the 2026-09-07 migration change, and the half that
           belongs here rather than in LabourCheck. Nobody moves to this city
           without a high school diploma - the world has universal high school
           (WageBand.arrivalCeiling). So the only adults in the NONE band are
           the ones this city failed to put through school: children who aged
           out of the teen band while the high schools were full or missing.

           Which makes the unskilled share a direct read on the schools, and
           this asserts exactly that: the same city, same jobs, same everything,
           schooled and unschooled.
           ================================================================= */
        System.out.println("\n--- the unskilled band is a report card on the schools ---");

        Game[] pair = new Game[2];
        quietly(() -> {
            pair[0] = city(GameFiles.scratch("educheck-none-a"));
            pair[0].simulateMonths(300);
            pair[1] = city(GameFiles.scratch("educheck-none-b"));
            schools(pair[1]);
            pair[1].simulateMonths(300);
        });
        Game unschooled = pair[0], schooled = pair[1];

        double bareNone = share(unschooled, WageBand.NONE);
        double taughtNone = share(schooled, WageBand.NONE);
        System.out.printf("   no diploma: %.1f%% of the workforce with no schools, %.1f%% with them"
                + "  (basic coverage %.0f%% vs %.0f%%)%n",
                bareNone * 100, taughtNone * 100,
                unschooled.getEducation().basicCoverage() * 100,
                schooled.getEducation().basicCoverage() * 100);

        assertTrue("fixture: the unschooled city really has no basic coverage",
                unschooled.getEducation().basicCoverage() < .05);
        assertTrue("fixture: the schooled one really has some",
                schooled.getEducation().basicCoverage() > .5);
        assertTrue("a city with no schools makes its own unskilled adults",
                bareNone > .25);
        assertTrue("...and schools are what stop it",
                taughtNone < bareNone * .6);
        assertTrue("nobody arrived unskilled - not one, in either city",
                unschooled.getMigration().getLastArrivalMix()[WageBand.NONE.ordinal()] == 0
                        && schooled.getMigration().getLastArrivalMix()[WageBand.NONE.ordinal()] == 0);

        /* ============ 12. THE QUEUE FOR A JOB INCLUDES THE OVERQUALIFIED ============

           Jerus, 2026-09-07: "demand isn't just those who don't have a diploma
           but also those left over from above tiers." Right, and it was half
           true: LabourMarket had always priced a band's tightness against
           supplyByBand(), cascade included, but Migration's opportunity term
           read the band's OWN workers - so a city whose colleges had flooded
           the diploma jobs with graduates showed an incoming diploma-holder a
           wide-open market, because the people already holding those jobs were
           filed one band up. The price said full and the opportunity said
           empty, out of the same city in the same month.

           The schooled fixture is exactly that city, so it can say so.
           ==================================================================== */
        System.out.println("\n--- the queue for a job includes the overqualified ---");

        double[] queue = schooled.getPopulationManager().supplyByBand();
        double[] ownHeads = schooled.getPopulationManager().workforceByBand();
        double[] open = schooled.getPopulationManager().staffablePostsByBand();
        double[] chance = schooled.getMigration().getLastOpportunity();
        int dip = WageBand.DIPLOMA.ordinal();

        System.out.printf("   diploma jobs %,.0f - %,.0f diploma-holders, but %,.0f in the queue"
                + " once the graduates come down.  opportunity %.2f%n",
                open[dip], ownHeads[dip], queue[dip], chance[dip]);

        assertTrue("fixture: graduates really have come down into diploma work",
                queue[dip] > ownHeads[dip] * 1.2);
        assertTrue("fixture: on its own workers alone the market would look open",
                open[dip] > ownHeads[dip]);
        assertTrue("...and it is not: the queue is what counts",
                chance[dip] < Migration.opportunity(open[dip], ownHeads[dip]) - .05);
        /*
         * Compared by which of the two it is NEARER, not by equality. The
         * opportunity was struck mid-month, before this month's arrivals and
         * job update landed, so the end-of-month arrays cannot reproduce it
         * exactly - a flow cannot be read off the state a month ended in, and
         * an equality here would be asserting that it can.
         */
        double fromQueue = Migration.opportunity(open[dip], queue[dip]);
        double fromOwn   = Migration.opportunity(open[dip], ownHeads[dip]);
        assertTrue("the opportunity read is the queue's, not the band's own",
                Math.abs(chance[dip] - fromQueue) < Math.abs(chance[dip] - fromOwn));
        assertTrue("nobody is ever written off entirely - the floor holds",
                Migration.opportunity(0, 1_000_000) == Migration.OPPORTUNITY_FLOOR);

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** A band's share of the workforce. */
    static double share(Game g, WageBand band) {
        double[] heads = g.getPopulationManager().workforceByBand();
        double total = 0;
        for (double h : heads) total += h;
        return total > 0 ? heads[band.ordinal()] / total : 0;
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
