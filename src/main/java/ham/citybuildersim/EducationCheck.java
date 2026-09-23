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
 *
 * 4. A GRANT STRUCK ON THE WRONG THING looks like a grant (2026-09-21). Four
 *    bases share one rule, and the founding one has to be the founding bill
 *    to the bit, a share of the surplus has to read the surplus the bridge
 *    shows and nothing in a deficit, and a share of tuition has to follow
 *    the price - each of them a number that would look plausible wrong.
 *
 * 5. INTEREST THAT LANDS NOWHERE, OR TWICE. The loan's rate is money out of
 *    a graduate's wages and into the treasury; it has to be a revenue line
 *    once, the principal has to stay the journal's, a student and a prisoner
 *    have to be charged nothing, and at a zero rate the arithmetic has to be
 *    bit for bit the interest-free loan it was.
 *
 * 6. A PRICE THAT ONE READER MISSES. The tuition scale reaches the fee, the
 *    household's share, the burden, the treasury's revenue and what it
 *    forgave; a reader that took the founding fee would charge one price
 *    and record another, and the identity between them is the check.
 *
 * 7. A PRICE PER SCHOOL THAT A LOAD PUTS BACK TOGETHER (0.7.6). The scale is
 *    nine, one per kind; one kind's move has to reach that kind's fee and no
 *    other's, the every-school setter has to move all nine, an old array has
 *    to read nine equal scales, and the load path must tell the schools the
 *    nine and not the one - the income rate did that to the three bases
 *    until 0.7.4. And the Schools page's row per kind has to add up to the
 *    totals it sits under.
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
        /*
         * FOUR HUNDRED MILLION, NOT FOUR BILLION, since 2026-09-07.
         *
         * This line meant "give it so much land that land never blocks
         * anything", and four billion square feet certainly does that. What
         * nobody checked is that LandMarket prices the ground off how much the
         * city has ANNEXED - 0.8% dearer per hundred-thousand-square-foot block
         * owned - so this grant alone multiplied the city's land price by 321
         * and left it there for three hundred months.
         *
         * That was invisible while nothing downstream cared, and stopped being
         * invisible the day rent got a cost floor: the marginal home was
         * suddenly 70% land, rent came out at $0.77 against a $0.91 wage, and
         * households in a city with a SURPLUS of homes were paying 182% of
         * take-home in rent. None of that was the rent model. All of it was
         * this line.
         *
         * Every other fixture in the suite grants between 12 and 400 million.
         * This is 400 million, which is still fifty times what the fixture
         * builds on, and it is now in the same world as everything else.
         *
         * (The underlying quirk is real and is NOT fixed here: annexing land
         * raises its price without bound, so a player who buys the map makes
         * their own housing unaffordable. That is arguably correct and is
         * certainly unbounded. Filed, not fixed.)
         */
        g.getLandManager().setOwnedSqFt(
                g.getLandManager().getOwnedSqFt() + 400_000_000L);
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
        /*
         * THIRTY YEARS, WATCHED. This city has no schools and shrinks for
         * three decades, and the graduates it has are whoever moved in; by
         * the end nearly every one of them is a doctor, because the doctors
         * are the graduates with a job. The claim - that graduates who cannot
         * practise medicine exist - is about the city as it grew, so it is
         * asserted on the month the graduates were most plentiful, and the
         * empty posts on the month it ended.
         */
        double mostGraduates = 0, licensedThen = 0;
        for (int m = 0; m < 360; m++) {
            quietly(() -> bare.simulateMonths(1));
            double g = bare.getPopulationManager().workforceByBand()[WageBand.UNIVERSITY.ordinal()];
            if (g > mostGraduates) {
                mostGraduates = g;
                licensedThen = bare.getPopulationManager().getLicensed(JobType.UNIV_DOCTOR);
            }
        }

        PopulationManager bp = bare.getPopulationManager();
        int doctorPosts = bp.getJobs()[JobType.UNIV_DOCTOR.ordinal()];
        double licensed = bp.getLicensed(JobType.UNIV_DOCTOR);
        int unfilled = bp.getJobVacancy()[JobType.UNIV_DOCTOR.ordinal()];
        double graduates = bp.workforceByBand()[WageBand.UNIVERSITY.ordinal()];

        System.out.printf("   %,d doctor posts, %,.0f licensed, %,d unfilled,"
                + " %,.0f graduates in town (at most %,.0f, against %,.0f licensed then)%n",
                doctorPosts, licensed, unfilled, graduates, mostGraduates, licensedThen);

        assertTrue("the fixture built hospitals, or this proves nothing", doctorPosts > 0);
        assertTrue("there ARE graduates - they simply cannot practise medicine",
                mostGraduates > licensedThen * 1.5 && graduates > licensed);
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

        /* ---- and the subsidy is forgone revenue, not a second cheque ----

           getGrossCost() used to be payroll + upkeep + citySubsidyPaid, so the
           city paid the teachers and was then billed again for the fees it had
           waived. It read $17.4M on slot 7 where wages and buildings less what
           households actually paid gives $14.2M. Worse, the money moved: the
           treasury was debited a subsidy that reached nobody, and MoneyAudit
           could not see it because households sit outside its pool.

           Pinned two ways - the identity, and the dial. Moving the subsidy
           must change what the city RECOVERS and leave what it SPENDS alone. */
        assertTrue("the bill is wages and buildings, and nothing else",
                Math.abs(le.getGrossCost() - (le.getPayroll() + le.getUpkeep())) < 1e-9);
        assertTrue("...so net cost is that, less the fees households paid",
                Math.abs(le.getNetCost()
                        - (le.getPayroll() + le.getUpkeep() - le.getFees())) < 1e-9);

        Game cheap = city(null);
        Game dear  = city(null);
        quietly(() -> {
            schools(cheap); cheap.getEducation().setTuitionSubsidy(0.9);
            cheap.simulateMonths(120);
            schools(dear);  dear.getEducation().setTuitionSubsidy(0.1);
            dear.simulateMonths(120);
        });
        Education ce = cheap.getEducation(), de = dear.getEducation();
        System.out.printf("   subsidy 90%%: spends $%,.0f, recovers $%,.0f"
                + "   |   subsidy 10%%: spends $%,.0f, recovers $%,.0f%n",
                ce.getGrossCost(), ce.getFees(), de.getGrossCost(), de.getFees());
        assertTrue("a generous dial collects less at the door",
                ce.getFees() < de.getFees());
        assertTrue("...and the subsidy it forgave is the larger one",
                ce.getSubsidy() > de.getSubsidy());

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
        double bareHeads = heads(unschooled, WageBand.NONE);
        double taughtHeads = heads(schooled, WageBand.NONE);
        System.out.printf("   no diploma: %.1f%% of the workforce with no schools, %.1f%% with them"
                + "  (%,.0f people against %,.0f; basic coverage %.0f%% vs %.0f%%)%n",
                bareNone * 100, taughtNone * 100, bareHeads, taughtHeads,
                unschooled.getEducation().basicCoverage() * 100,
                schooled.getEducation().basicCoverage() * 100);

        assertTrue("fixture: the unschooled city really has no basic coverage",
                unschooled.getEducation().basicCoverage() < .05);
        assertTrue("fixture: the schooled one really has some",
                schooled.getEducation().basicCoverage() > .5);
        /*
         * A SEVENTH AND NOT A QUARTER, AND THE OLD LEVEL WAS READING THE WRONG
         * THING. 2026-09-17, found by the car-finance batch: this line asked
         * for bareNone > .25, the unschooled city came in at 24.2%, and the
         * harness went red on a change that has nothing whatever to do with
         * schools. It was right to fire and wrong about why.
         *
         * THE SHARE IS DILUTED BY GROWTH. Nobody arrives unskilled, so every
         * immigrant lands in some other band and pushes this one's SHARE down
         * without touching its COUNT. The unchanged code walks this same city
         * through 0.0% at month 12, 7.1% at 96, 14.7% at 150, 22.6% at 200,
         * 28.8% at 250 and 34.7% at 300: it crosses .25 somewhere around month
         * 220, and where it happens to sit at month 300 is a statement about
         * how fast the city grew, not about whether it has schools. A change
         * that let households finance a car made the city grow faster and put
         * the reading back under the line, which is the threshold catching the
         * growth rate rather than the thing this section is named for.
         *
         * SO THE TEETH MOVED TO SOMETHING GROWTH CANNOT MOVE: the headcount,
         * against the schooled city's, twenty to one and usually nearer a
         * hundred. That is the claim this section actually makes, an immigrant
         * cannot dilute it, and it does not care how big either city got. The
         * share stays on as a floor meaning "a large minority and not a
         * rounding error", which is all it was ever load-bearing for - the
         * comparison below divides by it.
         */
        assertTrue("a city with no schools makes its own unskilled adults",
                bareNone > .15);
        assertTrue("...and against a city with them it is a factor, not a margin",
                bareHeads > 20 * taughtHeads);
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

        /* -------------------------------------------------------------------
           THIS PREMISE USED TO READ `open[dip] > ownHeads[dip]` - "on its own
           workers alone the market would look open" - and it was literally true
           of the fixture until 2026-09-07, when rent became a market.

           What happened to it is worth writing down, because it is a fixture
           changing under a mechanic rather than a mechanic breaking. Scarcity
           pricing made housing profitable to supply for the first time, so this
           city built 67% more homes (4,529 -> 7,560) and grew 28% (14,676 ->
           18,921). A bigger city with the same forty-seven schools makes more
           graduates than its shops make jobs: diploma-holders went 1,801 ->
           3,126 while diploma POSTS went 2,242 -> 2,685, and the inequality
           flipped.

           It cannot be bought back. Adding five Food Processing Plants - six
           hundred diploma posts on paper - moved staffable diploma posts by
           eighty-two, because BusinessInvestment simply built fewer shops with
           the land and the money. The post count is an equilibrium of this
           city, not a dial.

           So the premise is now stated as the thing it was always protecting:
           that the two readings of the same market DIFFER, materially, which is
           what makes the next assertion mean anything. That is a weaker
           sentence and a stronger test - the old one could have passed on a
           city where the gap was a rounding error.
           ------------------------------------------------------------------- */
        assertTrue("fixture: the two readings of this market really do differ",
                queue[dip] > ownHeads[dip] * 1.5
                        && Migration.opportunity(open[dip], ownHeads[dip])
                           > Migration.opportunity(open[dip], queue[dip]) + .15);
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

        /* ============ 13. THE GRANT IS A MENU ============

           Jerus, 2026-09-21: "grants its just a menu where you can choose
           between a fixed amount, or a percentage of last month's surplus, or
           a % as it is now of living costs, or a % of tuition." Four bases,
           one rule (TaxPolicy.grantBill), and the default basis at the
           default share has to be the bill it always was to the bit - the
           seed-0 playtest is byte-identical on it, and this is the same fact
           asserted where it can name itself.
           ================================================================= */
        System.out.println("\n--- the grant, on four bases ---");

        Game menu = city(GameFiles.scratch("educheck-grant"));
        quietly(() -> { schools(menu); menu.simulateMonths(149); });
        // The bill the month will strike, on the students it opens with: it is
        // struck and paid at the top of the month since 0.7.1, where the
        // students are credited it, so it is read before the month runs.
        double openingBill = menu.studentGrantBill();
        quietly(() -> menu.simulateMonths(1));
        TaxPolicy dials = menu.getEconomyManager().getTaxPolicy();
        EconomyManager mm = menu.getEconomyManager();
        double students = menu.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT);
        double wage = menu.getUnskilledWage();
        assertTrue("fixture: somebody is studying, and the wage is a wage", students > 10 && wage > 0);

        assertTrue("a new city grants a share of the unskilled wage, the founding rule",
                dials.getGrantBasis() == TaxPolicy.DEFAULT_GRANT_BASIS
                        && dials.getGrantAmount() == TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE
                        && dials.getStudentGrantShare() == TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE);
        assertTrue("...and at that basis and share the bill is bit for bit the founding expression",
                menu.studentGrantBill() == students * TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE * wage);
        assertTrue("...which is the bill the month struck and the treasury carries",
                mm.getStudentGrants() == openingBill);
        assertTrue("...and what the students' row was handed, the same month (0.7.1: it was the"
                + " month before)", menu.getHouseholds().getStudentGrants() == mm.getStudentGrants());

        // FIXED: an amount a student a month, whoever the wage is paid to - to
        // the students the month opens with (0.7.1).
        dials.setGrant(TaxPolicy.GrantBasis.FIXED, .4);
        double fixedStudents = menu.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT);
        quietly(() -> menu.simulateMonths(1));
        assertTrue("a fixed grant pays the amount per student",
                mm.getStudentGrants() == fixedStudents * .4
                        && menu.grantPerStudentUnder(TaxPolicy.GrantBasis.FIXED, .4) == .4);
        StudentHousehold paidStudents = menu.getHouseholdBalance().students();
        assertTrue("...and it reaches the students as their income",
                paidStudents.households() > 0
                        && Math.abs(paidStudents.disposable() * paidStudents.households()
                                - fixedStudents * .4) < 1e-6);
        TaxPolicy reformed = new TaxPolicy();
        reformed.setGrant(TaxPolicy.GrantBasis.FIXED, .4);
        reformed.setStudentLoanRate(.05);
        reformed.setTuitionScale(2.5);
        reformed.redenominate(.01);
        assertTrue("...and a currency reform scales it, because it is money",
                Math.abs(reformed.getGrantAmount() - .004) < 1e-15);
        assertTrue("...while the loan rate and the tuition scale, being ratios, stay",
                reformed.getStudentLoanRate() == .05 && reformed.getTuitionScale() == 2.5);
        TaxPolicy shares = new TaxPolicy();
        shares.setGrant(TaxPolicy.GrantBasis.TUITION_SHARE, .5);
        shares.redenominate(.01);
        assertTrue("...and a share of tuition does not move either",
                shares.getGrantAmount() == .5);
        assertTrue("the fixed ceiling is an unskilled wage",
                new TaxPolicy().maxGrantAmount(TaxPolicy.GrantBasis.FIXED)
                        == TaxPolicy.MAX_FIXED_GRANT_WAGES * PayTier.UNSKILLED.getMonthlyWage());

        // SURPLUS_SHARE: a share of LAST month's surplus as one pool, split
        // over this month's students, and read from where the bridge reads it.
        // The fixture city runs a deficit on three hospitals and forty-seven
        // schools, so the income tax goes up until the bridge shows a surplus
        // - the condition has to be caused, not stood next to.
        dials.setIncomeTaxRate(.5);
        quietly(() -> menu.simulateMonths(3));
        double surplusBefore = menu.getTreasurySurplus();
        assertTrue("fixture: last month ran a surplus, on the bridge", surplusBefore > 0);
        dials.setGrant(TaxPolicy.GrantBasis.SURPLUS_SHARE, .10);
        quietly(() -> menu.simulateMonths(1));
        double poolStudents = menu.getFamilies().getSeekers(FamilyModel.Seeker.STUDENT);
        System.out.printf("   last month's surplus $%,.1fk, a tenth of it $%,.1fk over %,.0f students%n",
                surplusBefore, .10 * Math.max(0, surplusBefore), poolStudents);
        assertTrue("a surplus share pays a tenth of the surplus the bridge showed, as one pool",
                mm.getStudentGrants() == .10 * Math.max(0, surplusBefore));
        assertTrue("...split evenly over this month's students",
                poolStudents > 0 && Math.abs(menu.grantPerStudentUnder(TaxPolicy.GrantBasis.SURPLUS_SHARE, .10)
                        - .10 * Math.max(0, menu.getTreasurySurplus()) / poolStudents) < 1e-9);
        assertTrue("a deficit month pays nothing",
                TaxPolicy.grantBill(TaxPolicy.GrantBasis.SURPLUS_SHARE, .5, 100, wage, -1_000, 50) == 0);
        assertTrue("...and so does a surplus with nobody to split it over",
                TaxPolicy.grantBill(TaxPolicy.GrantBasis.SURPLUS_SHARE, .5, 0, wage, 1_000, 0) == 0);

        // TUITION_SHARE: a share of each student's OWN course fee, at today's price.
        dials.setGrant(TaxPolicy.GrantBasis.TUITION_SHARE, .5);
        Education me = menu.getEducation();
        double bodyAtOne = me.studentBodyTuition();
        double halfOfTuition = menu.studentGrantBillUnder(TaxPolicy.GrantBasis.TUITION_SHARE, .5);
        assertTrue("a tuition share is half of what the student body is charged",
                bodyAtOne > 0 && halfOfTuition == .5 * bodyAtOne);
        double perCourse = 0;
        for (EducationType type : EducationType.values()) {
            if (type.isAdult()) perCourse += me.studentBody(type) * me.feeFor(type);
        }
        assertTrue("...each student at their own course's fee",
                Math.abs(bodyAtOne - perCourse) < 1e-9);
        // The scale, told to the schools as Game tells it, doubles the body's
        // tuition before anybody new enrols...
        dials.setTuitionScale(2);
        me.setTuitionScale(2);
        double bodyAtTwo = me.studentBodyTuition();
        assertTrue("...and it follows the tuition scale: at x2 the body's tuition is double",
                Math.abs(bodyAtTwo - 2 * bodyAtOne) < 1e-9);
        // ...and the month strikes its bill from exactly that figure, before
        // the education step moves the body on.
        quietly(() -> menu.simulateMonths(1));
        assertTrue("...so the bill the month struck was half of the scaled tuition",
                mm.getStudentGrants() == .5 * bodyAtTwo);
        dials.setTuitionScale(TaxPolicy.DEFAULT_TUITION_SCALE);
        dials.setStudentGrantShare(TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE);
        assertTrue("the wage share puts the founding basis back",
                dials.getGrantBasis() == TaxPolicy.GrantBasis.WAGE_SHARE
                        && dials.getStudentGrantShare() == TaxPolicy.DEFAULT_STUDENT_GRANT_SHARE);

        /* ============ 14. THE LOAN'S RATE ============

           Jerus: "the money is withdrawn from the treasury and then later
           when they pay it back it's added back, and you get the interest if
           there is any." The Canadian shape: nothing while they study,
           interest on a graduate's balance during repayment, the instalment
           unchanged, the interest the treasury's as revenue and the principal
           the journal's. And a prisoner's loan frozen with the rest of their
           debts - the 2026-09-14 finding, fixed in its own two overrides.
           ================================================================= */
        System.out.println("\n--- the loan, at a rate ---");

        int R = HouseholdBalance.ROWS;
        int unskilled = PayTier.UNSKILLED.ordinal();
        HouseholdBalance[] twins = new HouseholdBalance[3];   // never told a rate, told 0, told 5%
        Household[] families = new Household[3];
        double[] owedEach = new double[3];
        boolean studentsBorrowedFree = true;
        for (int t = 0; t < 3; t++) {
            HouseholdBalance grad = new HouseholdBalance();
            if (t == 1) grad.setStudentLoanRate(0);
            if (t == 2) grad.setStudentLoanRate(.05);
            double[] studentsIn = { 10 };
            double[] singlesIn = { 0 };
            grad.setOutsideCensus(c -> c instanceof StudentHousehold ? studentsIn[0] : 0);
            double[] payG = new double[R];
            grad.advanceMonth((s, tier) -> 0, payG, .1, new double[R], new double[R], .3, .05, 1);
            owedEach[t] = grad.students().studentDebt();
            if (!(owedEach[t] > 0) || grad.students().studentInterest() != 0) studentsBorrowedFree = false;
            studentsIn[0] = 0;
            singlesIn[0] = 10;
            double[] payW = new double[R];
            payW[unskilled] = 10 * 3.460;
            grad.advanceMonth((s, tier) -> s == FamilyStructure.SINGLE_ADULT && tier == PayTier.UNSKILLED
                    ? singlesIn[0] : 0, payW, .1, new double[R], new double[R], .3, .05, 1);
            twins[t] = grad;
            families[t] = grad.cell(FamilyStructure.SINGLE_ADULT, PayTier.UNSKILLED);
        }
        assertTrue("fixture: the students borrowed, and were charged nothing for it while they studied - at any rate",
                studentsBorrowedFree);
        assertTrue("a balance never told a rate is interest free, as it always was",
                families[0].studentInterest() == 0
                        && families[0].studentRepaid() == owedEach[0] / Household.STUDENT_LOAN_MONTHS);
        assertTrue("at 0% the repayment, the balance and the month are bit for bit what they were",
                families[1].studentRepaid() == families[0].studentRepaid()
                        && families[1].studentDebt() == families[0].studentDebt()
                        && families[1].afterFixed() == families[0].afterFixed()
                        && families[1].studentInterest() == 0);
        double interestDue = owedEach[2] * .05 / 12;
        assertTrue("at 5% a graduate is charged the month's interest on the balance",
                Math.abs(families[2].studentInterest() - interestDue) < 1e-12);
        assertTrue("...on top of the same principal as before",
                families[2].studentRepaid() == families[0].studentRepaid());
        assertTrue("...so the balance falls exactly as it did",
                families[2].studentDebt() == families[0].studentDebt());
        assertTrue("...and the month is poorer by exactly the interest",
                Math.abs((families[0].afterFixed() - families[2].afterFixed()) - interestDue) < 1e-12);
        assertTrue("the city's interest is every graduate's, summed",
                Math.abs(twins[2].totalStudentInterest() - 10 * interestDue) < 1e-9
                        && twins[0].totalStudentInterest() == 0);
        assertTrue("...and the principal repaid is what it was",
                twins[2].totalStudentRepaid() == twins[0].totalStudentRepaid());
        assertTrue("a screen asking what 5% would bring in gets the graduates' balances at the rate over twelve",
                twins[0].studentInterestAt(.05) > 0
                        && Math.abs(twins[0].studentInterestAt(.05) - twins[0].totalGraduateDebt() * .05 / 12) < 1e-9
                        && twins[0].totalGraduateDebt() == twins[0].totalStudentDebt());

        // A prisoner's loan is frozen: no instalment and no interest, and the
        // balance is carried, not forgiven.
        HouseholdBalance inside = twins[2];
        double[] singlesNow = { 5 };
        double[] prisonersNow = { 5 };
        inside.setOutsideCensus(c -> c instanceof PrisonerHousehold ? prisonersNow[0] : 0);
        double[] payHalf = new double[R];
        payHalf[unskilled] = 5 * 3.460;
        inside.advanceMonth((s, tier) -> s == FamilyStructure.SINGLE_ADULT && tier == PayTier.UNSKILLED
                ? singlesNow[0] : 0, payHalf, .1, new double[R], new double[R], .3, .05, 1);
        PrisonerHousehold jailed = inside.prisoners();
        double carriedIn = jailed.studentDebt();
        assertTrue("fixture: five graduates went to prison and their loans went with them",
                jailed.households() == 5 && carriedIn > 0);
        assertTrue("a prisoner's student loan is frozen: nothing comes off it inside",
                jailed.studentRepaid() == 0);
        assertTrue("...and nothing is charged on it, whatever the rate",
                jailed.studentInterest() == 0 && jailed.studentInterestAt(.15) == 0);
        inside.advanceMonth((s, tier) -> s == FamilyStructure.SINGLE_ADULT && tier == PayTier.UNSKILLED
                ? singlesNow[0] : 0, payHalf, .1, new double[R], new double[R], .3, .05, 1);
        assertTrue("...so a month later they owe exactly what they came in with",
                jailed.studentDebt() == carriedIn);
        assertTrue("...while the graduates still outside kept paying, interest and all",
                families[2].studentInterest() > 0 && families[2].studentRepaid() > 0);

        // And in a city: the interest lands in the treasury as revenue, to the
        // penny, on the revenue list, and the journal's line keeps the principal.
        //
        // AT A GRANT THAT DOES NOT COVER LIVING. At the founding share of the
        // wage this fixture's students' grant and savings cover their rent,
        // their fees and their food, and in its first draft nobody drew the
        // loan. (The seed-0 playtest's "student loans owed $0k" says nothing
        // either way: it never builds a school. With schools, some seeds do
        // borrow at the founding grant.) A fixture must cause the condition:
        // fifty dollars a month does, and the graduates carry the difference.
        GameFiles loanFiles = GameFiles.scratch("educheck-loan");
        Game lender = city(loanFiles);
        lender.getEconomyManager().getTaxPolicy().setGrant(TaxPolicy.GrantBasis.FIXED, .05);
        quietly(() -> { schools(lender); lender.simulateMonths(150); });
        assertTrue("fixture: at a $50 grant the students borrowed, and the graduates owe the treasury",
                lender.getHouseholdBalance().totalGraduateDebt() > 0
                        && lender.getStudentLoansLent() > 0);
        lender.getEconomyManager().getTaxPolicy().setStudentLoanRate(.05);
        quietly(() -> lender.simulateMonths(1));
        HouseholdBalance lb = lender.getHouseholdBalance();
        NationalAccounts ln = lender.getEconomyManager().getNationalAccounts();
        double interestPaid = lb.totalStudentInterest();
        System.out.printf("   graduates owe $%,.1fk; a month at 5%% brought in $%,.3fk of interest, $%,.1fk of principal%n",
                lb.totalGraduateDebt(), interestPaid, lender.getStudentLoansRepaid());
        assertTrue("the graduates paid interest this month", interestPaid > 0);
        assertTrue("...which is the treasury's, to the penny", lender.getStudentLoanInterest() == interestPaid
                && lender.getEconomyManager().getStudentLoanInterest() == interestPaid);
        assertTrue("...as its own revenue line on the national accounts", ln.getStudentLoanInterest() == interestPaid);
        assertTrue("...counted in the revenue total",
                Math.abs(ln.getTotalRevenue() - (ln.getTaxBusiness() + ln.getTaxIndustrial() + ln.getTaxSales()
                        + ln.getTaxWage() + ln.getUtilityIncome() + ln.getLandSales() + ln.getPropertyTax()
                        + ln.getContributions() + ln.getEiPremiums() + ln.getHealthFees()
                        + ln.getEducationFees() + ln.getHealthPremiums() + interestPaid)) < 1e-9);
        assertTrue("...while the journal's line is principal, net of what was lent",
                lender.getStudentLoansRepaid() == lb.totalStudentRepaid()
                        && journalLine(lender, "Lent to students, net of repayments")
                           == lender.getStudentLoansRepaid() - lender.getStudentLoansLent());
        assertTrue("...and the month passed the audit with the interest in it",
                lender.getLastMoneyAudit() != null && Math.abs(lender.getLastMoneyAudit().residual) < .01);
        assertTrue("...and the ledger was told the city's rate", lb.getStudentLoanRate() == .05);

        /* ============ 15. THE PRICE OF A PLACE ============

           Jerus: "make it so that you can tweak the price of tuition as
           well." The founding table stays; what a seat is charged at is the
           table times the scale, and everything that reads a fee reads the
           scaled one - the household's share, the burden, the treasury's
           revenue and what it forgave, the identity between them - so that a
           poor city at x3 is back in the trap the class header describes and
           at x0 everybody who would go, goes.
           ================================================================= */
        System.out.println("\n--- the price of a place ---");

        Education priced = new Education();
        priced.setTuitionScale(3);
        boolean feesScale = true, pocketScales = true;
        for (EducationType type : EducationType.values()) {
            if (type == EducationType.NONE) continue;
            if (priced.feeFor(type) != 3 * Education.foundingTuition(type)) feesScale = false;
            if (Math.abs(priced.outOfPocket(type) - 3 * Education.foundingTuition(type) * (1 - Education.DEFAULT_SUBSIDY)) > 1e-12) pocketScales = false;
            if (priced.feeAtOne(type) != Education.foundingTuition(type)) feesScale = false;
        }
        assertTrue("at x3 every fee is three times the founding table, and the table itself has not moved", feesScale);
        assertTrue("...and so is what a household pays out of pocket", pocketScales);
        assertTrue("at x1 the fee is the founding fee, bit for bit",
                new Education().feeFor(EducationType.UNIVERSITY) == Education.foundingTuition(EducationType.UNIVERSITY));
        priced.setTuitionScale(99);
        assertTrue("the schools clamp the scale to the policy's ceiling",
                priced.getTuitionScale() == TaxPolicy.MAX_TUITION_SCALE);

        /*
         * A POOR CITY WITH ONE UNIVERSITY, at five years. The fixture the
         * rest of this harness uses has six colleges and three universities
         * for a city of twenty thousand, so its seats bind: the price moved
         * the willing share from .76 to .29 and the student body by nobody,
         * because a full school admits as many as graduate whatever the
         * price. One university in the same city is willingness-bound, and
         * five years in, the wages are still near the ones the table was
         * struck against - which is what "poor" means here.
         */
        Game[] priceCities = new Game[3];
        double[] scales = { 1, 3, 0 };
        quietly(() -> {
            for (int k = 0; k < 3; k++) {
                priceCities[k] = city(GameFiles.scratch("educheck-price-" + k));
                build(priceCities[k], "Elementary School", 14);
                build(priceCities[k], "Middle School", 14);
                build(priceCities[k], "High School", 10);
                build(priceCities[k], "University", 1);
                priceCities[k].getEconomyManager().getTaxPolicy().setTuitionScale(scales[k]);
                priceCities[k].simulateMonths(60);
            }
        });
        double[] enrolled = new double[3];
        double[] willing = new double[3];
        for (int k = 0; k < 3; k++) {
            enrolled[k] = priceCities[k].getEducation().studentBody(EducationType.UNIVERSITY);
            willing[k] = priceCities[k].getEducation().willingShare(EducationType.UNIVERSITY,
                    priceCities[k].getLabourMarket());
        }
        Education atOne = priceCities[0].getEducation(), atThree = priceCities[1].getEducation(),
                atNone = priceCities[2].getEducation();
        System.out.printf("   at university: %,.0f at x1, %,.0f at x3, %,.0f at x0 (willing %.2f / %.2f / %.2f);"
                + " fees $%,.1fk / $%,.1fk / $%,.1fk, forgiven $%,.1fk / $%,.1fk / $%,.1fk%n",
                enrolled[0], enrolled[1], enrolled[2], willing[0], willing[1], willing[2],
                atOne.getFees(), atThree.getFees(), atNone.getFees(),
                atOne.getSubsidy(), atThree.getSubsidy(), atNone.getSubsidy());
        assertTrue("the schools charge at the city's scale", atThree.getTuitionScale() == 3 && atNone.getTuitionScale() == 0);
        assertTrue("fixture: the price is what decides here - x3 cuts the willing share, not the seats",
                willing[1] < willing[0] * .6);
        assertTrue("a poor city at x3 enrols fewer than at x1: the trap is back", enrolled[1] < enrolled[0] * .9);
        boolean everybodyCan = true;
        for (EducationType type : EducationType.values()) {
            if (type.isAdult() && atNone.studyAffordability(type, priceCities[2].getLabourMarket()) != 1) everybodyCan = false;
        }
        assertTrue("at x0 everybody who would go can afford to", everybodyCan);
        assertTrue("...so everybody the cap lets go, goes",
                willing[2] == Education.MAX_PARTICIPATION && willing[2] > willing[0]);
        assertTrue("...and more than at x3", enrolled[2] > enrolled[1]);
        assertTrue("a free place bills nothing and forgoes nothing", atNone.getFees() == 0 && atNone.getSubsidy() == 0);
        assertTrue("the treasury's fee revenue is the scaled fees households paid",
                atThree.getFees() > 0
                        && priceCities[1].getEconomyManager().getEducationFees() == atThree.getFees()
                        && priceCities[1].getEconomyManager().getNationalAccounts().getEducationFees() == atThree.getFees());
        for (int k = 0; k < 2; k++) {
            Education e = priceCities[k].getEducation();
            double billed = e.getSubsidy() + e.getFees();
            assertTrue(String.format("the identity holds at x%.0f: forgiven over billed is the subsidy share", scales[k]),
                    billed > 0 && Math.abs(e.getSubsidy() / billed - e.getTuitionSubsidy()) < 1e-9);
            assertTrue(String.format("...and at x%.0f the bill is wages and buildings, whatever the price", scales[k]),
                    Math.abs(e.getGrossCost() - (e.getPayroll() + e.getUpkeep())) < 1e-9);
        }
        assertTrue("...and it collects more at the door at x3, from the students it kept",
                atThree.getFees() / Math.max(1e-9, enrolled[1]) > atOne.getFees() / Math.max(1e-9, enrolled[0]));

        /* ============ 16. ALL THREE SURVIVE A SAVE ============ */
        System.out.println("\n--- and the three dials survive a save ---");

        // The lender city, whose graduates owe: a tuition-share grant, the
        // rate and a price, two months, a save, a load.
        Game kept = lender;
        TaxPolicy kp = kept.getEconomyManager().getTaxPolicy();
        kp.setGrant(TaxPolicy.GrantBasis.TUITION_SHARE, .5);
        kp.setStudentLoanRate(.05);
        kp.setTuitionScale(2.5);
        quietly(() -> kept.simulateMonths(2));
        double billBefore = kept.getEconomyManager().getStudentGrants();
        double interestBefore = kept.getEconomyManager().getNationalAccounts().getStudentLoanInterest();
        assertTrue("fixture: a tuition-share grant was struck and interest was charged",
                billBefore > 0 && interestBefore > 0);
        assertTrue("the city saved", kept.saveGame(2, "dials").ok);
        Game reloaded = new Game(loanFiles);
        quietly(() -> reloaded.loadGameSave(2));
        assertTrue("...and loaded", reloaded.getLoadFailure() == null);
        TaxPolicy rp = reloaded.getEconomyManager().getTaxPolicy();
        assertTrue("the grant's basis and amount came back",
                rp.getGrantBasis() == TaxPolicy.GrantBasis.TUITION_SHARE && rp.getGrantAmount() == .5);
        assertTrue("...and the loan rate", rp.getStudentLoanRate() == .05);
        assertTrue("...and the tuition scale", rp.getTuitionScale() == 2.5);
        assertTrue("...and the schools charge at it from the first read",
                reloaded.getEducation().getTuitionScale() == 2.5);
        assertTrue("...and the ledger knows the rate", reloaded.getHouseholdBalance().getStudentLoanRate() == .05);
        assertTrue("fixture: the rule re-struck from the reloaded city would not reproduce it - the body moved on",
                reloaded.studentGrantBill() != billBefore);
        assertTrue("the bill the month struck came back as the save struck it, not re-derived",
                reloaded.getEconomyManager().getStudentGrants() == billBefore);
        assertTrue("...and so did the interest line",
                reloaded.getEconomyManager().getNationalAccounts().getStudentLoanInterest() == interestBefore);
        double[] older = java.util.Arrays.copyOf(kp.getPolicyState(), TaxPolicy.STATE_BEFORE_EDUCATION);
        TaxPolicy fromBefore = new TaxPolicy();
        assertTrue("a policy array from before the dials is still read", fromBefore.restorePolicyState(older));
        assertTrue("...as the founding basis at the share its own slot carried, no interest, the founding price",
                fromBefore.getGrantBasis() == TaxPolicy.GrantBasis.WAGE_SHARE
                        && fromBefore.getGrantAmount() == older[TaxPolicy.STATE_BEFORE_EI + 2]
                        && fromBefore.getStudentLoanRate() == TaxPolicy.DEFAULT_STUDENT_LOAN_RATE
                        && fromBefore.getTuitionScale() == TaxPolicy.DEFAULT_TUITION_SCALE);
        TaxPolicy wageShared = new TaxPolicy();
        wageShared.setStudentGrantShare(.25);
        double[] olderStill = java.util.Arrays.copyOf(wageShared.getPolicyState(), TaxPolicy.STATE_BEFORE_EDUCATION);
        TaxPolicy readsShare = new TaxPolicy();
        readsShare.restorePolicyState(olderStill);
        assertTrue("...and an old save's own wage share is the grant it had",
                readsShare.getGrantAmount() == .25 && readsShare.getStudentGrantShare() == .25);
        assertTrue("a wrong shape is still refused whole",
                !new TaxPolicy().restorePolicyState(new double[TaxPolicy.STATE_SLOTS + 1]));

        /* ============ 17. A PRICE PER SCHOOL (0.7.6) ============

           Jerus: "have it so that not only can you raise prices but also
           raise the price for a specific university, and beside the dial it
           shows the current space, the current students, and the current
           cost and revenue." The scale is one per kind, the every-school
           setter is all nine at once, and the save carries the nine on the
           end - the shape the income taxes were given in 0.7.4.
           ================================================================= */
        System.out.println("\n--- a price per school ---");

        TaxPolicy perKind = new TaxPolicy();
        perKind.setTuitionScaleOf(EducationType.UNIVERSITY, 2.5);
        Education byKind = new Education();
        for (EducationType type : EducationType.values()) byKind.setTuitionScaleOf(type, perKind.tuitionScaleOf(type));
        boolean onlyThatOne = true;
        for (EducationType type : EducationType.values()) {
            if (type == EducationType.NONE) continue;
            double want = (type == EducationType.UNIVERSITY ? 2.5 : TaxPolicy.DEFAULT_TUITION_SCALE)
                    * Education.foundingTuition(type);
            if (byKind.feeFor(type) != want) onlyThatOne = false;
        }
        assertTrue("raising the university's price moves its fee and no other kind's", onlyThatOne);
        assertTrue("...so the nine have parted", perKind.tuitionScalesSplit());
        assertTrue("...and the every-school reading is the first kind's, unmoved",
                perKind.getTuitionScale() == TaxPolicy.DEFAULT_TUITION_SCALE
                        && perKind.tuitionScaleOf(EducationType.ELEMENTARY) == TaxPolicy.DEFAULT_TUITION_SCALE);
        perKind.setTuitionScaleOf(EducationType.MEDICAL, 99);
        assertTrue("one kind is held to the policy's ceiling",
                perKind.tuitionScaleOf(EducationType.MEDICAL) == TaxPolicy.MAX_TUITION_SCALE);
        perKind.setTuitionScale(3);
        boolean allNine = true;
        for (EducationType type : EducationType.values()) {
            if (type != EducationType.NONE && perKind.tuitionScaleOf(type) != 3) allNine = false;
        }
        assertTrue("the every-school setter moves all nine", allNine && !perKind.tuitionScalesSplit());

        int kindIndex = 0;
        for (EducationType type : EducationType.values()) {
            if (type != EducationType.NONE) perKind.setTuitionScaleOf(type, .5 + .25 * kindIndex++);
        }
        double[] nine = perKind.getPolicyState();
        TaxPolicy nineBack = new TaxPolicy();
        assertTrue("the array carries the nine on the end", nine.length == TaxPolicy.STATE_SLOTS
                && nineBack.restorePolicyState(nine));
        boolean roundTrip = true;
        for (EducationType type : EducationType.values()) {
            if (type != EducationType.NONE && nineBack.tuitionScaleOf(type) != perKind.tuitionScaleOf(type)) roundTrip = false;
        }
        assertTrue("...and each kind's scale comes back as it went", roundTrip);
        double[] beforeSchools = java.util.Arrays.copyOf(nine, TaxPolicy.STATE_BEFORE_SCHOOLS);
        TaxPolicy oneScale = new TaxPolicy();
        assertTrue("an array of the old length is still read", oneScale.restorePolicyState(beforeSchools));
        boolean nineEqual = true;
        for (EducationType type : EducationType.values()) {
            if (type != EducationType.NONE
                    && oneScale.tuitionScaleOf(type) != beforeSchools[TaxPolicy.STATE_BEFORE_SPLIT - 1]) nineEqual = false;
        }
        assertTrue("...as nine equal scales, the one its slot carried", nineEqual && !oneScale.tuitionScalesSplit()
                && oneScale.getTuitionScale() == perKind.getTuitionScale());

        /*
         * THE ROW PER KIND ADDS UP. The x3 price city above, sixty months
         * in: what each kind's buildings cost and what each kind's students
         * paid, against the two totals the page already shows.
         */
        Education rows = priceCities[1].getEducation();
        double costs = 0, fees = 0;
        for (EducationType type : EducationType.values()) {
            costs += rows.getCostOf(type);
            fees += rows.getFeesOf(type);
        }
        System.out.printf("   by kind: cost $%,.3fk against $%,.3fk, fees $%,.3fk against $%,.3fk%n",
                costs, rows.getPayroll() + rows.getUpkeep(), fees, rows.getFees());
        assertTrue("fixture: the schools cost something and collected something", costs > 0 && fees > 0);
        assertTrue("the kinds' costs add up to the staff and buildings the page shows, to the cent",
                Math.abs(costs - (rows.getPayroll() + rows.getUpkeep())) <= 1e-5);
        assertTrue("...and their fees to the tuition households paid, to the cent",
                Math.abs(fees - rows.getFees()) <= 1e-5);
        assertTrue("a kind with no school costs nothing and collects nothing",
                rows.getCostOf(EducationType.MEDICAL) == 0 && rows.getFeesOf(EducationType.MEDICAL) == 0);
        assertTrue("...and the university, standing, costs something",
                rows.getCostOf(EducationType.UNIVERSITY) > 0);
        assertTrue("the month re-struck at today's scales is what was billed",
                Math.abs(rows.billedAt(rows::getTuitionScaleOf) - (rows.getSubsidy() + rows.getFees())) <= 1e-5);

        /*
         * AND THE LOAD PATH TELLS THE SCHOOLS THE NINE. The reloaded lender
         * city from section 16 charges every school at x2.5; its university
         * goes to x4, a month is played, and it is saved and loaded again.
         */
        TaxPolicy lp = reloaded.getEconomyManager().getTaxPolicy();
        lp.setTuitionScaleOf(EducationType.UNIVERSITY, 4);
        quietly(() -> reloaded.simulateMonths(1));
        Education charged = reloaded.getEducation();
        assertTrue("the month charges the university at its own price and the college at the city's",
                charged.feeFor(EducationType.UNIVERSITY) == charged.feeAtOne(EducationType.UNIVERSITY) * 4
                        && charged.feeFor(EducationType.COLLEGE) == charged.feeAtOne(EducationType.COLLEGE) * 2.5);
        double uniCost = charged.getCostOf(EducationType.UNIVERSITY), uniFees = charged.getFeesOf(EducationType.UNIVERSITY);
        assertTrue("the city saved again", reloaded.saveGame(3, "kinds").ok);
        Game again = new Game(loanFiles);
        quietly(() -> again.loadGameSave(3));
        assertTrue("...and loaded", again.getLoadFailure() == null);
        TaxPolicy ap = again.getEconomyManager().getTaxPolicy();
        assertTrue("the nine came back parted",
                ap.tuitionScaleOf(EducationType.UNIVERSITY) == 4 && ap.tuitionScaleOf(EducationType.COLLEGE) == 2.5
                        && ap.tuitionScalesSplit());
        assertTrue("...and the load path told the schools the nine, not the one",
                again.getEducation().getTuitionScaleOf(EducationType.UNIVERSITY) == 4
                        && again.getEducation().feeFor(EducationType.UNIVERSITY)
                           == again.getEducation().feeAtOne(EducationType.UNIVERSITY) * 4
                        && again.getEducation().getTuitionScaleOf(EducationType.COLLEGE) == 2.5);
        assertTrue("...and the month by kind came back with the save, not zero",
                uniCost > 0 && again.getEducation().getCostOf(EducationType.UNIVERSITY) == uniCost
                        && again.getEducation().getFeesOf(EducationType.UNIVERSITY) == uniFees);

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /**
     * A band's headcount, which - unlike its share - no amount of immigration
     * into the OTHER bands can move. See section 11.
     */
    static double heads(Game g, WageBand band) {
        return g.getPopulationManager().workforceByBand()[band.ordinal()];
    }

    /** Last month's journal line by its label, or NaN for none. See section 14. */
    static double journalLine(Game g, String label) {
        for (TreasuryJournal.Entry e : g.getTreasuryJournal()) {
            if (label.equals(e.label())) return e.amount();
        }
        return Double.NaN;
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
