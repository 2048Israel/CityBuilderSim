package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Verifies the labour market: who can hold a job, and what it costs. Not part
 * of the game.
 *
 * WHY THIS EXISTS
 *
 * Two separate things were wrong and both were silent.
 *
 * PopulationManager.getJobVacancy() walked the job array from the top index
 * down, handing an undifferentiated pool of adults to the most-skilled posts
 * first. A measured city of 9,016 people staffed 220 doctor posts at 100% with
 * no school, college or university anywhere in the game, while a third of its
 * workers sat idle. The eleven job types were real on the demand side and
 * imaginary on the supply side, and nothing on any screen said so.
 *
 * And a wage was one of six constants. A city that could not staff a hospital
 * paid its doctors exactly what a city with a queue of them paid, so the one
 * price that should have been screaming was the one number that never moved.
 *
 * Both failures produce a city that looks completely normal, which is why every
 * assertion here is about a QUANTITY rather than about a screen.
 */
public class LabourCheck {

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
        System.out.printf("%-62s %s%n", label, ok ? "OK" : "FAIL");
    }

    static void close(String label, double a, double b, double tol) {
        boolean ok = Math.abs(a - b) < tol;
        if (!ok) fails++;
        System.out.printf("%-62s %s%n", label,
                ok ? "OK" : String.format("FAIL  %.6f != %.6f", a, b));
    }

    static BuildingsTemplate t(Game g, String name) {
        for (BuildingsTemplate b : g.getBuildingManager().getTemplates()) {
            if (b.getName().equals(name)) return b;
        }
        throw new IllegalStateException("no template " + name);
    }

    public static void main(String[] args) throws Exception {

        /* ============ 1. THE LADDER IS NEUTRAL AT ITS DEFAULT ============

           The wage market anchors to PayTier rather than replacing it, and the
           minimum wage starts at the unskilled tier - so a brand new city pays
           exactly what it paid before this class existed. Every balance
           measurement already taken survives, and that is not a hope, it is
           this assertion.
           ============================================================== */
        System.out.println("--- at the default minimum wage, nothing has changed ---");

        LabourMarket market = new LabourMarket();
        close("the dial starts on the unskilled tier",
                market.getMinimumWage(), PayTier.UNSKILLED.getMonthlyWage(), 1e-12);

        for (JobType job : JobType.values()) {
            close("  " + job.name() + " pays what PayTier says",
                    market.baseWage(job), PayTier.wageOf(job), 1e-12);
        }

        /* ============ 2. THE DIAL MOVES EVERYTHING ============ */
        System.out.println("\n--- and raising it lifts the whole ladder ---");

        double doctorBefore = market.baseWage(JobType.UNIV_DOCTOR);
        market.setMinimumWage(PayTier.UNSKILLED.getMonthlyWage() * 2);

        System.out.printf("   minimum wage doubled: labourer %.3f -> %.3f, doctor %.3f -> %.3f%n",
                PayTier.UNSKILLED.getMonthlyWage(), market.baseWage(JobType.NO_DIPLOMA),
                doctorBefore, market.baseWage(JobType.UNIV_DOCTOR));

        close("the doctor's base doubled too", market.baseWage(JobType.UNIV_DOCTOR),
                doctorBefore * 2, 1e-12);
        assertTrue("the ladder kept its shape",
                Math.abs(market.baseWage(JobType.UNIV_DOCTOR)
                        / market.baseWage(JobType.NO_DIPLOMA)
                        - PayTier.wageOf(JobType.UNIV_DOCTOR)
                        / PayTier.wageOf(JobType.NO_DIPLOMA)) < 1e-12);
        assertTrue("and the dial is bounded",
                new LabourMarket() {{ setMinimumWage(-5); }}.getMinimumWage()
                        >= LabourMarket.MIN_SETTABLE);

        /* ============ 3. SCARCITY RAISES IT, SURPLUS LOWERS IT ============

           Driven straight rather than played, because a played city moves
           twenty things at once and this is a claim about one function.
           ============================================================== */
        System.out.println("\n--- a shortage raises the wage, a glut lowers it ---");

        int bands = WageBand.values().length;
        LabourMarket tight = new LabourMarket();
        LabourMarket slack = new LabourMarket();

        double[] posts = new double[bands];
        double[] many  = new double[bands];
        double[] few   = new double[bands];
        for (int b = 0; b < bands; b++) { posts[b] = 1000; many[b] = 4000; few[b] = 250; }

        // Long enough for the damping to arrive somewhere.
        for (int m = 0; m < 200; m++) { tight.advanceMonth(posts, few); slack.advanceMonth(posts, many); }

        System.out.printf("   4 posts per worker -> %.2fx base;  4 workers per post -> %.2fx%n",
                tight.premium(JobType.UNIV_DOCTOR), slack.premium(JobType.UNIV_DOCTOR));

        assertTrue("a shortage pays over the odds",
                tight.premium(JobType.UNIV_DOCTOR) > 1.5);
        assertTrue("...but not without limit",
                tight.premium(JobType.UNIV_DOCTOR) <= LabourMarket.MAX_MULTIPLE + 1e-9);
        assertTrue("a glut pays under them",
                slack.premium(JobType.UNIV_DOCTOR) < 1);
        assertTrue("...and stops at the bottom of the range",
                slack.premium(JobType.UNIV_DOCTOR) >= LabourMarket.MIN_MULTIPLE - 1e-9);

        /*
         * NOBODY IS PAID UNDER THE MINIMUM, which is what makes an unskilled
         * glut unfixable by price. The band's own floor would be 0.7x base, but
         * base IS the minimum wage for the unskilled - so the two collide and
         * the wage cannot move at all. That collision is the mechanism, not a
         * rounding artefact: it is what forces the adjustment into departures.
         */
        assertTrue("no wage falls below the minimum",
                slack.getWage(JobType.NO_DIPLOMA) >= slack.getMinimumWage() - 1e-9);
        assertTrue("...so an unskilled glut is pinned, by construction",
                slack.isPinned(WageBand.NONE));

        /* ============ 4. IT IS LAGGED, WHICH IS THE POINT ============

           "just supply vs demand but lagged". An undamped market with a delayed
           supply response oscillates - the cobweb cycle - so the damping is not
           polish, it is what stops the city hunting. Asserted against
           ADJUST_RATE rather than against a month count, so re-tuning the
           constant re-tunes the test with it.
           ============================================================== */
        System.out.println("\n--- and it gets there slowly ---");

        LabourMarket lag = new LabourMarket();
        lag.advanceMonth(posts, few);
        double afterOne = lag.premium(JobType.UNIV_DOCTOR);

        /*
         * THE TARGET IS NOT THE CEILING, and the first version of this test
         * assumed it was. Four posts per worker is a tightness of 4, and the
         * curve is a square root, so the market is heading for 2x - not the 4x
         * clamp, which only binds at sixteen posts per worker. Asserting
         * against MAX_MULTIPLE measured the clamp and called it the lag.
         *
         * Derived from the constants rather than written down, so re-tuning
         * either the elasticity or the damping re-tunes the assertion with it.
         */
        double tightRatio = posts[0] / few[0];
        double targetMultiple = Math.min(LabourMarket.MAX_MULTIPLE,
                Math.pow(tightRatio, LabourMarket.ELASTICITY));
        double oneStep = 1 + (targetMultiple - 1) * LabourMarket.ADJUST_RATE;

        System.out.printf("   %.0f posts per worker is heading for %.2fx;"
                + " one month in it is %.3fx%n", tightRatio, targetMultiple, afterOne);

        assertTrue("one month does not close the gap", afterOne < targetMultiple * .7);
        close("...it closes ADJUST_RATE of it", afterOne, oneStep, .02);

        int months = 1;
        while (lag.premium(JobType.UNIV_DOCTOR) < targetMultiple * .9 && months < 500) {
            lag.advanceMonth(posts, few);
            months++;
        }
        System.out.println("   90% of the way there after " + months + " months");
        assertTrue("a shortage takes a year or more to price in", months >= 12);
        assertTrue("...and does get there eventually", months < 500);

        /* ============ 5. NOBODY IS A DOCTOR WHO IS NOT A DOCTOR ============

           The headline bug, nailed down. Played rather than driven, because the
           claim is about a whole city.
           ============================================================== */
        System.out.println("\n--- and a city cannot staff what it has not attracted ---");

        Game city = new Game(GameFiles.scratch("labourcheck"));
        quietly(() -> {
            city.newGame();
            city.getGovernmentInvestor().spend(-50_000_000);
            city.getLandManager().setOwnedSqFt(
                    city.getLandManager().getOwnedSqFt() + 400_000_000);
            city.buildStack(t(city, "Low-Rise Apartments"), 60, true);
            city.buildStack(t(city, "General Hospital"), 2, true);
            city.buildStack(t(city, "Regional Medical Centre"), 1, true);
            city.buildStack(t(city, "Steel Mini-Mill"), 4, true);
            city.buildStack(t(city, "Small Grocery Store"), 8, true);
            city.buildStack(t(city, "Coal Power Plant"), 2, true);
            city.buildStack(t(city, "Water Treatment Plant"), 2, true);
            city.buildStack(t(city, "Paved Road"), 30, true);
            city.simulateMonths(240);
        });

        PopulationManager people = city.getPopulationManager();
        double[] own = people.workforceByBand();
        double[] band = people.postsByBand();
        int[] jobs = people.getJobs();
        int[] vacancy = people.getJobVacancy();

        System.out.printf("   population %,d  workforce %,d  posts %,d%n",
                people.getPopulation(), people.getWorkforce(), people.getTotalJobs());
        for (WageBand b : WageBand.values()) {
            System.out.printf("   %-12s workers %,8.0f   posts %,8.0f   staffed %,8.0f%n",
                    b.label(), own[b.ordinal()], band[b.ordinal()],
                    staffed(jobs, vacancy, b));
        }

        /*
         * THE ASSERTION THE WHOLE FEATURE EXISTS FOR.
         *
         * Staffed posts in a band can never exceed the workers who could hold
         * them - their own, plus anyone from ABOVE who could not find work at
         * their own level or, since 0.7.18, would be better paid here. Before
         * this, the number on the left was 220 and the number on the right was
         * zero.
         */
        double[] supply = people.supplyByBand();
        for (WageBand b : WageBand.values()) {
            assertTrue("  no more " + b.label() + " posts staffed than there are people for",
                    staffed(jobs, vacancy, b) <= supply[b.ordinal()] + 1);
        }

        /*
         * AND SUBSTITUTION RUNS DOWNWARD ONLY.
         *
         * A graduate can labour. The reverse is what the old allocator did, and
         * it is the only direction that can invent a skill out of nothing.
         */
        double topSurplus = own[WageBand.UNIVERSITY.ordinal()] - band[WageBand.UNIVERSITY.ordinal()];
        if (topSurplus > 0) {
            assertTrue("  graduates with no graduate work cascade downward",
                    supply[WageBand.COLLEGE.ordinal()] > own[WageBand.COLLEGE.ordinal()]);
        }
        assertTrue("  ...and nothing cascades up",
                supply[WageBand.UNIVERSITY.ordinal()]
                        <= own[WageBand.UNIVERSITY.ordinal()] + 1e-9);

        /*
         * NO SKILL WITHOUT A MIGRANT. Until schools exist, every graduate in
         * this city arrived here. The count is bounded by the arrivals, and it
         * is emphatically not bounded by the number of posts - which is what it
         * was before, because the posts were creating the people.
         */
        assertTrue("  skilled workers exist at all - migration supplied them",
                own[WageBand.UNIVERSITY.ordinal()] > 0);

        /* ============ 6. AND IT SURVIVES A SAVE ============

           A damped price cannot be recomputed: rebuild it from today's posts
           and workers and you get the TARGET, not the wage. Neither can a
           carried stock of skilled workers. Both are exactly the "a flow cannot
           be reconstructed from the state a month ended in" rule.
           ============================================================== */
        System.out.println("\n--- through a save ---");

        Path root = Files.createTempDirectory("labour");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        Game lived = new Game(files);
        quietly(() -> {
            lived.newGame();
            lived.getGovernmentInvestor().spend(-20_000_000);
            lived.getLandManager().setOwnedSqFt(
                    lived.getLandManager().getOwnedSqFt() + 100_000_000);
            lived.buildStack(t(lived, "Low-Rise Apartments"), 30, true);
            lived.buildStack(t(lived, "General Hospital"), 1, true);
            lived.buildStack(t(lived, "Small Grocery Store"), 4, true);
            lived.buildStack(t(lived, "Coal Power Plant"), 1, true);
            lived.buildStack(t(lived, "Water Treatment Plant"), 1, true);
            lived.buildStack(t(lived, "Paved Road"), 12, true);
            lived.simulateMonths(90);
        });

        double[] wagesBefore = lived.getLabourMarket().getWages().clone();
        double[] skillsBefore = lived.getPopulationManager().workforceByBand().clone();

        assertTrue("the city saved", lived.saveGame(1, "labour").ok);

        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));

        double[] wagesAfter = back.getLabourMarket().getWages();
        double[] skillsAfter = back.getPopulationManager().workforceByBand();

        for (JobType job : JobType.values()) {
            close("  " + job.name() + " is paid the same after a reload",
                    wagesAfter[job.ordinal()], wagesBefore[job.ordinal()], 1e-9);
        }
        for (WageBand b : WageBand.values()) {
            close("  the " + b.label() + " workers came back",
                    skillsAfter[b.ordinal()], skillsBefore[b.ordinal()], 1.0);
        }

        /*
         * NOT THE SAME AS RECOMPUTING IT. If the wage were rebuilt from
         * today's scarcity the reloaded city would hold the TARGET while the
         * live one was still walking toward it - so the test has to show the
         * carried wage differs from the recomputed one, or it proves nothing.
         */
        LabourMarket fresh = new LabourMarket();
        fresh.advanceMonth(back.getPopulationManager().postsByBand(),
                back.getPopulationManager().supplyByBand());
        boolean differs = false;
        for (JobType job : JobType.values()) {
            if (Math.abs(fresh.getWage(job) - wagesAfter[job.ordinal()]) > 1e-6) differs = true;
        }
        assertTrue("...and a recomputed market would NOT have matched", differs);

        /* ------------------------------------------------------------------
         * RENT NO LONGER FOLLOWS THE UNSKILLED WAGE (2026-09-07).
         *
         * This section used to assert the opposite, and asserting the opposite
         * was correct for exactly one day. Rent was `30% of two unskilled wages
         * over four heads`, so the minimum-wage dial moved rent BY
         * CONSTRUCTION: raising the floor raised rent in the same proportion in
         * the same month, the rent burden never budged, and the one lever the
         * player had for making housing affordable could not make housing
         * affordable. It has a cost floor and a scarcity multiple now.
         *
         * Three claims, and the middle one is the whole change:
         *
         *   1. a reloaded city charges what the live one charged. Unchanged,
         *      and it earned its keep again the same afternoon - rent walks a
         *      twelfth of the way to its target each month and repriceRent()
         *      was being called from updateEcon(), which the load path also
         *      calls, so every load stepped it once more than the live city.
         *      0.174309 against 0.174210.
         *   2. doubling the minimum wage does NOT double rent.
         *   3. rent is the cost floor times the scarcity multiple - the
         *      identity the price is actually built from.
         * ------------------------------------------------------------------ */
        System.out.println("\n--- rent is a market, not a wage formula ---");
        ham.citybuildersim.sectors.RealEstate rents = back.getSectors().realEstate();
        double rentBefore = rents.getRentPrice();
        close("a reloaded city charges the rent it was charging",
                rentBefore, lived.getSectors().realEstate().getRentPrice(), 1e-9);

        assertTrue("fixture: the city has a housing cost to price against",
                rents.getMarginalHousingCost() > 0);
        /*
         * The identity - target == floor x multiple - is NOT asserted here, and
         * the first draft of this section asserted it and went red. getRentTarget()
         * is a snapshot of the target the last reprice was struck against;
         * rentFloor() and rentScarcityMultiple() recompute off inputs that have
         * moved since. Comparing the two is comparing two different months, which
         * is the same mistake as ForeignCheck dividing by a price that had moved.
         * The identity and the step size are tested where they can be held still:
         * MonetaryCheck, on a handler with numbers put into it by hand.
         */

        double floorBefore = back.getLabourMarket().getMinimumWage();
        double targetBefore = rents.getRentTarget();
        back.getLabourMarket().setMinimumWage(floorBefore * 2);
        quietly(() -> back.simulateMonths(1));
        double unskilledNow = back.getLabourMarket().getWage(JobType.NO_DIPLOMA);
        assertTrue("fixture: doubling the floor moved the unskilled wage", unskilledNow > floorBefore * 1.5);

        /*
         * The old formula would have put rent at rentFor(unskilledNow) THIS
         * MONTH. It is nowhere near it, and that gap is the mechanic.
         */
        double wouldHaveBeen = ham.citybuildersim.sectors.RealEstate.rentFor(unskilledNow);
        System.out.printf("   rent %.6f; the old formula would say %.6f%n",
                rents.getRentPrice(), wouldHaveBeen);
        assertTrue("doubling the minimum wage does not double rent",
                rents.getRentPrice() < rentBefore * 1.2);
        assertTrue("...and rent is not the wage formula any more",
                Math.abs(rents.getRentPrice() - wouldHaveBeen) > 1e-6);

        /*
         * And the target barely moved either, which is the stronger claim: it
         * is not that the lease is hiding a wage effect for a month, it is that
         * there is no wage effect to hide. What little there is arrives through
         * construction costs, which is the long way round and is meant to be.
         */
        System.out.printf("   rent target %.6f -> %.6f on a doubled wage floor%n",
                targetBefore, rents.getRentTarget());
        assertTrue("the rent TARGET is not a wage formula either",
                rents.getRentTarget() < targetBefore * 1.5);

        /* ------------------------------------------------------------------
         * ARRIVING CHILDREN ARE NOT GRADUATES (2026-09-06).
         *
         * The arrival mix sums to everybody who moved in, of every age, and
         * used to be added whole to the ADULT skilled counts. Now the skilled
         * counts move by the adult share of the mix, which is the share the
         * cohorts actually gave the migrants. Checked on a growing city with
         * no schools, so education cannot move the counts: what the skilled
         * counts gain in a month is the adult share of the skilled arrivals,
         * less the month's retirements, within the retirements' size.
         * ------------------------------------------------------------------ */
        System.out.println("\n--- arriving children are not graduates ---");
        Game growing = new Game(GameFiles.scratch("labourcheck"));
        quietly(() -> {
            growing.newGame();
            growing.getGovernmentInvestor().spend(-20_000_000);
            growing.getLandManager().setOwnedSqFt(
                    growing.getLandManager().getOwnedSqFt() + 100_000_000);
            growing.buildStack(t(growing, "Low-Rise Apartments"), 40, true);
            growing.buildStack(t(growing, "General Hospital"), 1, true);
            growing.buildStack(t(growing, "Small Grocery Store"), 6, true);
            growing.buildStack(t(growing, "Coal Power Plant"), 1, true);
            growing.buildStack(t(growing, "Water Treatment Plant"), 1, true);
            growing.buildStack(t(growing, "Paved Road"), 12, true);
            growing.simulateMonths(12);
        });

        /* -------------------------------------------------------------------
         * HUNTED FOR, NOT ASSUMED.
         *
         * This used to roll 24 months and read whatever the 25th happened to
         * be, which worked only while that particular month happened to be a
         * month with arrivals in it. The fixture's jobs are fixed, so the city
         * converges on a target and then stops moving - and anything that
         * shifts where that target sits shifts which months have arrivals.
         * Housing maintenance shifted it by about 400 residents and the 25th
         * month became a month with no arrivals at all, so a test about
         * GRADUATES failed for reasons that had nothing to do with graduates.
         *
         * So the month is now searched for. Both preconditions the assertions
         * below need - somebody arrived, and the two candidate explanations
         * are far enough apart to tell apart - are checked before the month is
         * accepted, which is the same fix the "far enough apart" assertion got
         * on 2026-09-07 for the same reason. A fixture has to CAUSE the
         * condition under test, not stand next to it and hope.
         * ------------------------------------------------------------------- */
        double adultShare = 0, skilledArrivals = 0, skilledDepartures = 0;
        double gained = 0, held = 0;
        int found = -1;

        for (int attempt = 0; attempt < 60 && found < 0; attempt++) {
            double[] before = growing.getPopulationManager().getSkilledHeads().clone();
            double share = growing.getCohorts().share(AgeBand.ADULT);
            quietly(() -> growing.simulateMonths(1));
            double[] now = growing.getPopulationManager().getSkilledHeads();
            double[] arrived = growing.getMigration().getLastArrivalMix();
            double[] left = growing.getMigration().getLastDepartureMix();

            double in = 0, out = 0, moved = 0, stock = 0;
            for (int b = 1; b < before.length; b++) {
                in += arrived[b];
                out += left[b];
                moved += now[b] - before[b];
                stock += before[b];
            }
            double n = in - out;
            if (in > 1 && Math.abs(n - n * share) > 1) {
                adultShare = share;
                skilledArrivals = in; skilledDepartures = out;
                gained = moved; held = stock;
                found = growing.getMonth();
            }
        }

        assertTrue("fixture: found a month with skilled arrivals in it", found > 0);
        assertTrue("fixture: the city is not all adults", adultShare < .9 && adultShare > .3);
        double net = skilledArrivals - skilledDepartures;
        double expected = net * adultShare;
        double retirements = held * .01;   // a generous bound on a month of deaths and ageing out
        System.out.printf("   month %d: skilled arrivals %.1f, departures %.1f, adult share %.3f,"
                + " counts moved %.1f, expected %.1f (unscaled %.1f)%n",
                found, skilledArrivals, skilledDepartures, adultShare, gained, expected, net);

        assertTrue("the skilled counts gained the ADULT share of the skilled arrivals",
                Math.abs(gained - expected) <= retirements + .5);

        /*
         * ...AND NOT THE WHOLE MIX, compared by which of the two it is NEARER.
         *
         * This asserted an absolute distance from the unscaled figure, which
         * only works while the two candidates are far apart - and on
         * 2026-09-07 they were not: departures happened to land where
         * net == net * adultShare, the two explanations coincided, and the
         * check failed on a month where nothing was wrong. A fixture must
         * CAUSE the condition it tests, so the gap between the candidates is
         * asserted first and the comparison is relative after it.
         */
        assertTrue("fixture: the two explanations are far enough apart to tell apart",
                Math.abs(net - expected) > 1);
        assertTrue("...and not the whole mix",
                Math.abs(gained - expected) < Math.abs(gained - net));

        /* ------------------------------------------------------------------
         * A DOCTOR SHORTAGE IS PRICED ON DOCTORS (2026-09-06).
         *
         * A hospital and no medical school: the doctor posts can only be
         * filled by licence holders, and there are next to none. The doctor's
         * wage must climb over the band's, the band's own premium must NOT be
         * dragged up with it, and the market must report the shortage as a
         * licence shortage rather than a graduate one.
         * ------------------------------------------------------------------ */
        System.out.println("\n--- a doctor shortage is priced on doctors ---");
        Game short_ = new Game(GameFiles.scratch("labourcheck"));
        quietly(() -> {
            short_.newGame();
            short_.getGovernmentInvestor().spend(-50_000_000);
            short_.getLandManager().setOwnedSqFt(
                    short_.getLandManager().getOwnedSqFt() + 100_000_000);
            short_.buildStack(t(short_, "Low-Rise Apartments"), 40, true);
            short_.buildStack(t(short_, "General Hospital"), 2, true);
            short_.buildStack(t(short_, "Small Grocery Store"), 6, true);
            short_.buildStack(t(short_, "Coal Power Plant"), 1, true);
            short_.buildStack(t(short_, "Water Treatment Plant"), 1, true);
            short_.buildStack(t(short_, "Paved Road"), 12, true);
            short_.simulateMonths(60);

            /*
             * ...AND THEN UNTIL SOMEBODY ACTUALLY MOVES IN.
             *
             * Everything below this reads ONE MONTH - the arrival mix, the
             * licences that came with it, the bands they came from - and month
             * 60 is not chosen, it is just where simulateMonths stopped. This
             * city settles into a churn equilibrium in the forties (arrivals
             * and departures both around 33 a month against a target of 4,980)
             * and month 60 happened to land on a month with no arrivals at all,
             * so three assertions about WHO ARRIVES were being asked of a month
             * in which nobody did.
             *
             * That is the same fault this harness was already caught by once,
             * on the 25th month, and SaveFileCheck twice - eleven sightings now
             * across the suite. The rule is in claude/todo.md: a fixture has to
             * CAUSE the condition under test, not stand next to it. Bounded, so
             * a city that genuinely never attracts anybody fails loudly instead
             * of hanging.
             */
            for (int extra = 0; extra < 120
                    && short_.getMigration().getLastArrivals() <= 1; extra++) {
                short_.simulateMonths(1);
            }
        });
        LabourMarket m2 = short_.getLabourMarket();
        int doctorPosts = short_.getPopulationManager().getJobs()[JobType.UNIV_DOCTOR.ordinal()];
        double doctorsHeld = short_.getPopulationManager().getLicensedHeads()[JobType.UNIV_DOCTOR.ordinal()];
        System.out.printf("   doctor posts %d, licensed %.1f, licence tightness %.2f, doctor premium %.2f, band premium %.2f%n",
                doctorPosts, doctorsHeld, m2.getLicenceTightness(JobType.UNIV_DOCTOR),
                m2.premium(JobType.UNIV_DOCTOR), m2.bandPremium(WageBand.UNIVERSITY));
        assertTrue("fixture: more doctor posts than licence holders",
                doctorPosts > doctorsHeld);
        assertTrue("the market reports a licence shortage",
                m2.getLicenceTightness(JobType.UNIV_DOCTOR) > 1);
        assertTrue("doctors are paid over the band",
                m2.premium(JobType.UNIV_DOCTOR) > m2.bandPremium(WageBand.UNIVERSITY) * 1.05);
        assertTrue("...and never over the ceiling",
                m2.premium(JobType.UNIV_DOCTOR) <= LabourMarket.MAX_MULTIPLE + 1e-9);
        assertTrue("an ungated graduate job carries no licence premium",
                m2.getLicenceMultiple(JobType.UNIV_SCIENCE) == 1);
        assertTrue("licence holders arrive in response to the price",
                short_.getMigration().getLastArrivalLicences()[JobType.UNIV_DOCTOR.ordinal()] > 0);

        /* ------------------------------------------------------------------
         * WHO MOVES IN, AND WHY (2026-09-07).
         *
         * Jerus, on a fresh run: "way too many very well educated people come
         * in", with no demand for them and no schools to explain them. The
         * world is now universal high school and nothing else for free -
         * a diploma is the base, and every band above the diploma is bought
         * at a premium or does not come. Since 0.7.18 every band BELOW it
         * too (Jerus: "allow arrivals without a diploma when the unskilled
         * wage is high"): NONE was zero, and the two assertions that said so
         * now say the new rule - none at the going rate, the world's share of
         * migrants with no diploma at the ceiling. The section after this one
         * causes a dear unskilled wage and asserts the arrivals it buys.
         *
         * These four assertions are the whole feature. The first two are the
         * spec; the third is the reason the old model could not be tuned into
         * this shape; the fourth is what replaces MAX_LICENSED_ARRIVALS.
         * ------------------------------------------------------------------ */
        System.out.println("\n--- who moves in, and why ---");

        double[] mixShort = short_.getMigration().getLastArrivalMix();
        double[] licShort = short_.getMigration().getLastArrivalLicences();
        double arrivalsTotal = 0;
        for (double v : mixShort) arrivalsTotal += v;
        System.out.printf("   arrivals %,.0f - none %.1f%%  diploma %.1f%%  college %.1f%%  university %.1f%%%n",
                arrivalsTotal,
                pct(mixShort, WageBand.NONE, arrivalsTotal),
                pct(mixShort, WageBand.DIPLOMA, arrivalsTotal),
                pct(mixShort, WageBand.COLLEGE, arrivalsTotal),
                pct(mixShort, WageBand.UNIVERSITY, arrivalsTotal));

        assertTrue("fixture: somebody moved in at all", arrivalsTotal > 1);
        assertTrue("fixture: this city pays its labourers no more than the going rate",
                m2.bandPremium(WageBand.NONE) <= 1);
        assertTrue("NOBODY ARRIVES WITHOUT A DIPLOMA into a city paying the going rate for them",
                mixShort[WageBand.NONE.ordinal()] == 0);
        double ceilings = 0;
        for (WageBand b : WageBand.values()) ceilings += b.arrivalCeiling();
        System.out.printf("   at every band's ceiling, %.1f%% of arrivals have no diploma%n",
                WageBand.NONE.arrivalCeiling() / ceilings * 100);
        assertTrue("...and at the ceiling they are the world's share of migrants with no diploma, 7.5%",
                Math.abs(WageBand.NONE.arrivalCeiling() / ceilings - .075) < 1e-12);

        /* ------------------------------------------------------------------
         * SOME ARRIVE WITHOUT A DIPLOMA, FOR THE WAGE (0.7.18).
         *
         * Caused, not found: a town with a bus network's worth of labouring
         * posts and nobody born here to fill them. Its unskilled wage climbs
         * past the going rate, and the labourers it buys come through the
         * same two terms every graduate band's do - reach() of the premium
         * and the chance of work at their own level - against the diploma
         * band's unconditional 1.00. Not a multiplier of their own.
         * ------------------------------------------------------------------ */
        System.out.println("\n--- some arrive without a diploma, for the wage ---");
        Game buses = new Game(GameFiles.scratch("labourcheck-buses"));
        quietly(() -> {
            buses.newGame();
            buses.getGovernmentInvestor().spend(-50_000_000);
            buses.getLandManager().setOwnedSqFt(buses.getLandManager().getOwnedSqFt() + 100_000_000);
            buses.buildStack(t(buses, "Low-Rise Apartments"), 10, true);
            buses.buildStack(t(buses, "Bus Network"), 8, true);
            buses.buildStack(t(buses, "Coal Power Plant"), 1, true);
            buses.buildStack(t(buses, "Water Treatment Plant"), 1, true);
            buses.buildStack(t(buses, "Paved Road"), 12, true);
            buses.simulateMonths(36);
            // ...and on to a month somebody moved in, bounded, as the city above is.
            for (int i = 0; i < 120 && buses.getMigration().getLastArrivals() <= 0; i++) buses.simulateMonths(1);
        });
        LabourMarket busWages = buses.getLabourMarket();
        double[] busMix = buses.getMigration().getLastArrivalMix();
        double[] busChance = buses.getMigration().getLastOpportunity();
        double nonePremium = busWages.bandPremium(WageBand.NONE);
        System.out.printf("   unskilled premium %.2f, chance of unskilled work %.2f; arrivals %.1f of whom %.2f with no diploma, %.2f with one%n",
                nonePremium, busChance[WageBand.NONE.ordinal()], buses.getMigration().getLastArrivals(),
                busMix[WageBand.NONE.ordinal()], busMix[WageBand.DIPLOMA.ordinal()]);
        assertTrue("fixture: the town pays its labourers over the going rate", nonePremium > 1);
        assertTrue("fixture: somebody moved in", buses.getMigration().getLastArrivals() > 0);
        assertTrue("a dear unskilled wage brings arrivals without a diploma", busMix[WageBand.NONE.ordinal()] > 0);
        double noneOverDiploma = WageBand.NONE.arrivalCeiling() * Migration.reach(nonePremium)
                * busChance[WageBand.NONE.ordinal()] / busChance[WageBand.DIPLOMA.ordinal()];
        assertTrue("...against the diploma arrivals, the ceiling times reach() times the chance of work - the graduates' own terms",
                busMix[WageBand.DIPLOMA.ordinal()] > 0
                        && Math.abs(busMix[WageBand.NONE.ordinal()] / busMix[WageBand.DIPLOMA.ordinal()] - noneOverDiploma)
                                < 1e-9 * Math.max(1, noneOverDiploma));

        /*
         * THE DETACH. This city is short of doctors and NOT short of graduates
         * - the graduate band is at or under its going rate - and doctors
         * arrive anyway. Under the old model they could not: a licence holder
         * was carved out of the graduate arrivals, so no graduate pull meant no
         * doctors however empty the hospital was. Two different shortages were
         * reading off one number.
         */
        double bandPrem = m2.bandPremium(WageBand.UNIVERSITY);
        System.out.printf("   graduate band premium %.2f, doctor licence premium %.2f, doctors arriving %.3f%n",
                bandPrem, m2.licencePremium(JobType.UNIV_DOCTOR),
                licShort[JobType.UNIV_DOCTOR.ordinal()]);
        assertTrue("fixture: the graduate band is NOT bid up - only doctors are",
                bandPrem < m2.licencePremium(JobType.UNIV_DOCTOR));
        assertTrue("a doctor shortage brings doctors on its own",
                licShort[JobType.UNIV_DOCTOR.ordinal()] > 0);

        /*
         * ...and they are graduates, counted once. This is the invariant that
         * let MAX_LICENSED_ARRIVALS be deleted rather than retuned: licences
         * are no longer a share of the graduate arrivals that could overshoot,
         * they are part of them by construction.
         */
        double licTotal = 0;
        for (double v : licShort) licTotal += v;
        assertTrue("a licence holder is one of the university arrivals, not an extra head",
                licTotal <= mixShort[WageBand.UNIVERSITY.ordinal()] + 1e-9);

        /*
         * AT THE GOING RATE, NOBODY ABOVE A DIPLOMA COMES.
         *
         * Priced directly rather than played, because a city sitting at exactly
         * 1.00 on every band is a fixture nobody can build. reach() is the one
         * definition of the curve and this asserts its ends: zero at the going
         * rate, the full ceiling at the wage ceiling, and a third of the way
         * at twice the going rate - which is the shape Jerus chose.
         */
        System.out.println("\n--- the pull curve ---");
        assertTrue("at the going rate a graduate has no reason to come",
                Migration.reach(1.00) == 0);
        assertTrue("...nor below it", Migration.reach(0.70) == 0);
        assertTrue("at twice the going rate, a third of the ceiling",
                Math.abs(Migration.reach(2.00) - 1 / 3.0) < 1e-9);
        assertTrue("at the wage ceiling, all of it",
                Math.abs(Migration.reach(LabourMarket.MAX_MULTIPLE) - 1) < 1e-9);
        assertTrue("...and never more, however far a premium is pushed",
                Migration.reach(99) == 1);
        assertTrue("a diploma is the base and is not competed for",
                WageBand.DIPLOMA.arrivalCeiling() == 1);
        assertTrue("the graduate ceilings stay under it - the world has few to spare",
                WageBand.COLLEGE.arrivalCeiling() < 1
                        && WageBand.UNIVERSITY.arrivalCeiling() < WageBand.COLLEGE.arrivalCeiling());

        wagesAgainstTheIndex();
        payrollByJobType();

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /* ------------------------------------------------------------------
     * WAGES AGAINST THE INDEX (2026-09-21).
     *
     * A measurement on 2026-09-15 put costOfLiving at 2.648 against a price
     * index of 1.993 on a played city: every wage a third above the level it
     * is written to chase, a real-wage gain with nothing behind it,
     * compounding for the life of the run. The code says ONE indexation -
     * baseWage() times costOfLiving, which walks DRIFT_PER_MONTH of the way
     * toward the index every month - and Game's note on the floor records a
     * second one found and taken out since; the seed-0 playtest's "wages
     * lifted" tracks its index. So the question is whether any path still
     * lifts wages past the index, and this asks it of the three the playtest
     * does not walk: a long steady inflation, a currency reform, and a save
     * and a reload.
     *
     * THE INFLATION IS CAUSED, not found: the currency is pinned and crawled
     * weaker by CRAWL a month, so every import - and the shelf priced off
     * them, and the rent priced off the wage - gets dearer month after month.
     *
     * THE BAND IS DERIVED FROM THE DIAL, not typed. A lag that closes
     * DRIFT_PER_MONTH of the gap a month holds 1 - (1 - DRIFT)^N of its
     * weight on the last N months, so over the last 2 / DRIFT_PER_MONTH of
     * them - four years, 87% - the wage index is an average of where the
     * index stood, and cannot sit outside the lowest and highest it stood
     * there unless the rest of its weight is far away. Wages a third above an
     * index that has been rising for years is exactly that: outside the band.
     * ------------------------------------------------------------------ */

    /** How fast the fixture's currency is walked weaker: half a percent a month. */
    static final double CRAWL = .005;

    /** Months of steady inflation, which is the twenty years the 2026-09-15 city had lived. */
    static final int INFLATION_MONTHS = 240;

    /** The recurrence, checked month by month on one city: what it was handed and what it did. */
    static final class LagWatch {
        final java.util.List<Double> index = new java.util.ArrayList<>();
        double worstTarget, worstStep;
        int outsideWindow, windowMonths;

        /** One month, played; the index the month was handed is the one it read at its top. */
        void month(Game g, Runnable play) {
            LabourMarket wages = g.getLabourMarket();
            double handed = g.getPriceIndex().getIndex();
            double wasCost = wages.getCostOfLiving();
            play.run();
            double target = 1 + (handed - 1) * LabourMarket.COST_OF_LIVING_PASS_THROUGH;
            worstTarget = Math.max(worstTarget, Math.abs(wages.getLivingTarget() - target));
            worstStep = Math.max(worstStep, Math.abs(wages.getCostOfLiving()
                    - (wasCost + (wages.getLivingTarget() - wasCost) * LabourMarket.DRIFT_PER_MONTH)));
            index.add(g.getPriceIndex().getIndex());
            int window = (int) Math.round(2 / LabourMarket.DRIFT_PER_MONTH);
            if (index.size() > window) {
                double low = Double.MAX_VALUE, high = -Double.MAX_VALUE;
                for (int k = index.size() - 1 - window; k < index.size(); k++) {
                    low = Math.min(low, index.get(k));
                    high = Math.max(high, index.get(k));
                }
                windowMonths++;
                if (wages.getCostOfLiving() < low - 1e-12 || wages.getCostOfLiving() > high + 1e-12) {
                    outsideWindow++;
                }
            }
        }
    }

    /** The currency one CRAWL weaker, and held there for the month. */
    static void crawl(Game g) {
        ForeignAccounts fx = g.getForeignAccounts();
        fx.pinRate(fx.getRate() * (1 + CRAWL));
    }

    static void wagesAgainstTheIndex() {
        System.out.println("\n--- wages against the index they chase ---");

        GameFiles files = GameFiles.scratch("labour-index");
        Game city = new Game(files);
        quietly(() -> {
            city.run();
            city.getGovernmentInvestor().spend(-2_000_000);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 200_000_000L);
            LongPlaytest.build(city, "House", 200);
            LongPlaytest.build(city, "Convenience Store", 10);
            /*
             * THE BUILDERS' DEPOTS STAND FROM THE START (0.7.17). The basket
             * is based on what a city that shops spends, and its shops have to
             * open for that. Since every building gets the crew it can use
             * (BuildingManager, EVERY BUILDING GETS THE CREW IT CAN USE) the
             * depots on site beside the coal plant got about a fiftieth of its
             * crew and the builders stayed the city's works department alone,
             * so the shops had not opened when the basket was due: no index to
             * chase. With the depots standing everything else is still built.
             */
            city.buildStack(t(city, "Construction Depot"), 4, true);
            LongPlaytest.build(city, "Coal Power Plant", 1);
            LongPlaytest.build(city, "Water Treatment Plant", 1);
            LongPlaytest.build(city, "Industrial Bakery", 2);
            LongPlaytest.build(city, "Commercial Bank", 1);
            LongPlaytest.build(city, "Paved Road", 10);
            // Long enough for the basket to be based on a city that shops.
            city.simulateMonths(PriceIndex.SETTLING_MONTHS + 12);
        });
        assertTrue("fixture: the basket is based, so there is an index to chase",
                city.getPriceIndex().isBased());

        /* ---- twenty years of a steady inflation ---- */
        LagWatch lived = new LagWatch();
        double indexFrom = city.getPriceIndex().getIndex();
        for (int m = 0; m < INFLATION_MONTHS; m++) {
            crawl(city);
            lived.month(city, () -> quietly(() -> city.simulateMonths(1)));
        }
        LabourMarket wages = city.getLabourMarket();
        double index = city.getPriceIndex().getIndex();
        double monthly = Math.pow(index / indexFrom, 1.0 / INFLATION_MONTHS) - 1;
        double settles = LabourMarket.DRIFT_PER_MONTH * (1 + monthly)
                / (LabourMarket.DRIFT_PER_MONTH + monthly);
        System.out.printf("   %d months: index %.4f -> %.4f (%.2f%% a year); wage index %.4f,"
                        + " target %.4f, wages/index %.3f (a steady %.3f%% a month settles the lag at %.3f)%n",
                INFLATION_MONTHS, indexFrom, index, (Math.pow(1 + monthly, 12) - 1) * 100,
                wages.getCostOfLiving(), wages.getLivingTarget(), wages.getCostOfLiving() / index,
                monthly * 100, settles);

        assertTrue("fixture: the crawl really did inflate the basket, steadily",
                index / indexFrom > Math.pow(1 + CRAWL, INFLATION_MONTHS / 2.0));
        close("every month wages chased the index the city published",
                lived.worstTarget, 0, 1e-12);
        close("...and moved DRIFT_PER_MONTH of the way to it - one indexation, not two",
                lived.worstStep, 0, 1e-12);
        assertTrue("so the wage index sits inside the lag's window, every month",
                lived.windowMonths > 0 && lived.outsideWindow == 0);
        assertTrue("...which under a rising index is BELOW it, not a third above",
                wages.getCostOfLiving() < index);

        /* ---- a currency reform: a unit change, which a ratio must not see ---- */
        double costBefore = wages.getCostOfLiving();
        double targetBefore = wages.getLivingTarget();
        double[] reformed = new double[1];
        quietly(() -> reformed[0] = city.reformCurrencyForTest(100) ? 1 : 0);
        assertTrue("fixture: the currency was reformed", reformed[0] == 1);
        close("a reform leaves the wage index where it was", wages.getCostOfLiving(), costBefore, 1e-12);
        close("...and the level it is walking toward", wages.getLivingTarget(), targetBefore, 1e-12);
        close("...and the price index it walks toward", city.getPriceIndex().getIndex(), index, 1e-12);

        LagWatch afterReform = new LagWatch();
        afterReform.index.addAll(lived.index);
        for (int m = 0; m < 24; m++) {
            crawl(city);
            afterReform.month(city, () -> quietly(() -> city.simulateMonths(1)));
        }
        close("...and two years on, wages still chase the index the city published",
                afterReform.worstTarget, 0, 1e-12);
        close("...a DRIFT_PER_MONTH at a time", afterReform.worstStep, 0, 1e-12);
        assertTrue("...inside the lag's window", afterReform.outsideWindow == 0);

        /* ---- and a save: the ratio comes back, and keeps its lag ---- */
        double[] saved = new double[1];
        quietly(() -> saved[0] = city.saveGame(10, "wages against the index").ok ? 1 : 0);
        assertTrue("fixture: the reformed city saved", saved[0] == 1);
        Game back = new Game(files);
        quietly(() -> back.loadGameSave(10));
        close("the wage index reloads", back.getLabourMarket().getCostOfLiving(),
                wages.getCostOfLiving(), 1e-12);
        close("...and the level it is walking toward", back.getLabourMarket().getLivingTarget(),
                wages.getLivingTarget(), 1e-12);
        close("...and the price index it is handed", back.getPriceIndex().getIndex(),
                city.getPriceIndex().getIndex(), 1e-12);

        back.getForeignAccounts().pinRate(city.getForeignAccounts().getRate());
        LagWatch reloaded = new LagWatch();
        reloaded.index.addAll(afterReform.index);
        for (int m = 0; m < 24; m++) {
            crawl(back);
            reloaded.month(back, () -> quietly(() -> back.simulateMonths(1)));
        }
        System.out.printf("   reformed and reloaded, two years on: wage index %.4f, index %.4f,"
                        + " wages/index %.3f%n",
                back.getLabourMarket().getCostOfLiving(), back.getPriceIndex().getIndex(),
                back.getLabourMarket().getCostOfLiving() / back.getPriceIndex().getIndex());
        close("and a reloaded city's wages chase the index it publishes",
                reloaded.worstTarget, 0, 1e-12);
        close("...a DRIFT_PER_MONTH at a time", reloaded.worstStep, 0, 1e-12);
        assertTrue("...inside the lag's window", reloaded.outsideWindow == 0);
    }

    /* ============ 13. PAYROLL BY JOB TYPE (0.7.17) ============

       Jerus: "Businesses pay each job type's wage for the jobs actually
       filled, which is what households receive" - and, of the sick, "sick
       still get paid right? if yes then good". Every employer's bill was the
       whole schedule times its AVERAGE fill (Sector.getPayroll(), the
       utilities the same), while the households were paid each type's wage
       for its own filled posts; in Jerus's year-149 city the firms paid
       $1,273M a month nobody received. On a played city whose job types fill
       unevenly:
         (a) every employer's payroll adds to what the households are paid,
             and so it does when the builders have laid idle crews off -
             Jerus's answer to the idle floor: they offer the posts their
             work needs, never fewer than the core crew, the rest are
             nobody's posts, and a month with work hires them back;
         (b) a sector's payroll does not move when the city falls sick with
             its fill held - sickness is output, never payroll;
         (c) the average-fill figure is not the payroll there, so the fixture
             causes what (a) is about.
       ============================================================ */
    static void payrollByJobType() {
        System.out.println("\n--- every employer pays each job type's wage for the posts filled, the sick included ---");

        Game g = new Game(GameFiles.scratch("labourcheck-payroll"));
        quietly(() -> {
            g.newGame();
            g.getGovernmentInvestor().spend(-50_000_000);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000);
            g.buildStack(t(g, "Low-Rise Apartments"), 60, true);
            g.buildStack(t(g, "General Hospital"), 2, true);
            g.buildStack(t(g, "Steel Mini-Mill"), 4, true);
            g.buildStack(t(g, "Small Grocery Store"), 8, true);
            g.buildStack(t(g, "Construction Depot"), 6, true);
            g.buildStack(t(g, "Coal Power Plant"), 2, true);
            g.buildStack(t(g, "Water Treatment Plant"), 2, true);
            g.buildStack(t(g, "Paved Road"), 30, true);
            g.buildStack(t(g, "Bus Network"), 2, true);
            g.buildStack(t(g, "Police Station"), 2, true);
            g.simulateMonths(120);
        });
        PopulationManager people = g.getPopulationManager();
        EconomyManager econ = g.getEconomyManager();
        BuildingManager b = g.getBuildingManager();
        int[] posts = people.getJobs();
        double[] fill = people.getJobFillRate();
        double lo = 1, hi = 0;
        for (int i = 0; i < posts.length; i++) {
            if (posts[i] <= 0) continue;
            lo = Math.min(lo, fill[i]);
            hi = Math.max(hi, fill[i]);
        }
        System.out.printf("   month %d, %,d people, %,d posts; job types filled from %.1f%% to %.1f%%%n",
                g.getMonth(), people.getPopulation(), people.getTotalJobs(), lo * 100, hi * 100);
        assertTrue("fixture: the city's job types fill unevenly", hi - lo > .10);

        double households = people.getTotalWage();
        double sectors = 0, averageFill = 0;
        for (Sector s : econ.getSectors().all()) {
            sectors += s.getPayroll();
            averageFill += s.getPayrollAtFullStaffing() * s.getAverageFill();
        }
        double employers = sectors + everyOtherEmployer(g);
        System.out.printf("   every employer %,.3f against the households' %,.3f; the sectors at their average fill %,.3f against %,.3f%n",
                employers, households, averageFill, sectors);
        close("(a) every employer's payroll is what the households are paid", employers / households, 1, 1e-9);
        assertTrue("(c) ...and the average-fill figure is not the sectors' payroll on this city",
                Math.abs(averageFill - sectors) > 1e-3 * sectors);

        // (a) each sector's own, too: its posts at the households' wage and fill.
        double[] wage = people.getWagesPerType();
        boolean each = true;
        for (Sector s : econ.getSectors().all()) {
            int[] own = s.postsOfferedPerTier();
            double bill = 0;
            for (int i = 0; i < own.length && i < wage.length; i++) bill += own[i] * wage[i] * fill[i];
            if (Math.abs(s.getPayroll() - bill) > 1e-9 * Math.max(1, bill)) each = false;
        }
        assertTrue("(a) ...and each sector's payroll is its filled posts at the households' wage", each);

        // (b) the city falls sick, its fill held.
        double[] before = new double[econ.getSectors().all().size()];
        double[] rateBefore = new double[before.length];
        int k = 0;
        for (Sector s : econ.getSectors().all()) { before[k] = s.getPayroll(); rateBefore[k++] = s.getOperatingRate(); }
        econ.setHealthRatio(.5);
        boolean paid = true, slowed = true;
        k = 0;
        for (Sector s : econ.getSectors().all()) {
            if (s.getPayroll() != before[k]) paid = false;
            if (rateBefore[k] > 0 && !(s.getOperatingRate() < rateBefore[k])) slowed = false;
            k++;
        }
        assertTrue("(b) half the city off sick, fill held: no sector's payroll moves", paid);
        assertTrue("...while every working sector's output falls with it", slowed);
        close("...and the households are paid what they were", people.getTotalWage(), households, 1e-9 * households);
        econ.setHealthRatio(g.getHealth().getWorkRatio());

        /*
         * (a) WHEN THE BUILDERS HAVE LAID CREWS OFF (0.7.17, revised: Jerus
         * chose "Lay off idle crews" over the idle floor on wages). The city
         * above keeps its builders busy, so a second one is founded whose
         * four depots stand beside a finished town with little to build: the
         * work ahead needs a share of their crews, they offer that share of
         * their posts, never fewer than the core crew, and the posts not
         * offered are not in the city's count - nobody's job, so nobody's
         * pay. Then a month with work hires them back.
         */
        Game town = new Game(GameFiles.scratch("labourcheck-layoffs"));
        quietly(() -> {
            town.newGame();
            town.buildStack(t(town, "House"), 80, true);
            town.buildStack(t(town, "Small Grocery Store"), 3, true);
            town.buildStack(t(town, "Construction Depot"), 4, true);
            town.simulateMonths(24);
            /*
             * ...AND ON TO A MONTH THEY DO LAY CREWS OFF (0.7.18, re-caused).
             * Month 24 was where the builders happened to have nothing on
             * site. Since every planner asks for staff before it orders, the
             * town's orders land in other months, and month 24 found work on
             * site - 3.6 crews' worth - and all 50 of the builders' posts
             * offered. The condition is caused by stepping to it, bounded, as
             * the arrivals are above.
             */
            for (int i = 0; i < 60 && town.getSectors().construction().getPostsOffered()
                    >= town.getSectors().construction().getPostsStanding(); i++) {
                town.simulateMonths(1);
            }
        });
        people = town.getPopulationManager();
        econ = town.getEconomyManager();
        b = town.getBuildingManager();
        ham.citybuildersim.sectors.Construction crews = econ.getSectors().construction();
        int[] standingPosts = crews.postsPerTier(), kept = crews.postsOfferedPerTier();
        double share = crews.getPostsOfferedShare();
        int standing = crews.getPostsStanding(), offered = crews.getPostsOffered();
        System.out.printf("   the builders: %,d posts, %,d offered - a share of %.3f, the work needing %.3f%n",
                standing, offered, share, crews.getCrewsNeeded());
        assertTrue("fixture: the builders have less work than crews, and lay some off", share < 1 && offered < standing);
        // REWRITTEN (0.7.17, fourth revision): the need over the fill the
        // share was struck on, so the crews that come are the crews the work
        // needs - it was the need alone.
        close("...offering the share of their posts the work needs over their fill, never fewer than the core crew",
                share, Math.min(1, Math.max(ham.citybuildersim.sectors.Construction.IDLE_PAYROLL_FLOOR,
                        crews.getCrewsNeeded() / crews.getFillStruckOn())), 1e-12);
        boolean rounded = true;
        for (int i = 0; i < standingPosts.length; i++) {
            if (kept[i] != BuildingManager.postsOffered(standingPosts[i], share)) rounded = false;
        }
        assertTrue("...each job type's posts at that share, rounded once", rounded);
        int counted = 0, everyPost = 0;
        for (int n : people.getJobs()) counted += n;
        b.setOfferedShare(null);
        for (int n : b.getTotalJobs()) everyPost += n;
        b.setOfferedShare(key -> key.equals(crews.key()) ? crews.getPostsOfferedShare() : 1);
        close("the posts not offered are not in the city's count", counted, everyPost - (standing - offered), .5);
        double townEmployers = everyOtherEmployer(town);
        for (Sector s : econ.getSectors().all()) townEmployers += s.getPayroll();
        close("(a) ...and the households are paid what the employers paid, the laid-off posts in nobody's pay",
                townEmployers / people.getTotalWage(), 1, 1e-9);

        quietly(() -> {
            // ...the city paying for it, on ground it is given.
            town.getGovernmentInvestor().spend(-50_000_000);
            town.getLandManager().setOwnedSqFt(town.getLandManager().getOwnedSqFt() + 400_000_000);
            town.buildStack(t(town, "Low-Rise Apartments"), 400, false);
            town.simulateMonths(1);
        });
        people = town.getPopulationManager();
        System.out.printf("   with 400 blocks of flats on site: %,d of %,d posts offered, the work needing %.2f%n",
                crews.getPostsOffered(), crews.getPostsStanding(), crews.getCrewsNeeded());
        assertTrue("a month with the work for them hires every crew back",
                crews.getPostsOfferedShare() == 1 && crews.getPostsOffered() == crews.getPostsStanding());
        double busyEmployers = everyOtherEmployer(town);
        for (Sector s : econ.getSectors().all()) busyEmployers += s.getPayroll();
        close("(a) ...and the households are paid what the employers paid, that month too",
                busyEmployers / people.getTotalWage(), 1, 1e-9);
    }

    /** Every payroll that is not a sector's: the bank, the utilities, and the four services the city staffs. */
    static double everyOtherEmployer(Game g) {
        PopulationManager people = g.getPopulationManager();
        double[] wage = people.getWagesPerType(), fill = people.getJobFillRate();
        BuildingManager b = g.getBuildingManager();
        double total = g.getEconomyManager().getBankPayroll()
                + g.getServicesManager().getUtilitiesHandler().getUtilityPayroll();
        for (BuildingType cat : new BuildingType[] { BuildingType.HEALTHCARE, BuildingType.EDUCATION,
                BuildingType.SAFETY, BuildingType.INFRASTRUCTURE }) {
            total += b.getCategoryPayroll(cat, wage, fill);
        }
        return total;
    }

    static double sum(double[] a) {
        double t = 0;
        for (double v : a) t += v;
        return t;
    }

    static double pct(double[] mix, WageBand band, double total) {
        return total > 0 ? mix[band.ordinal()] / total * 100 : 0;
    }

    static double staffed(int[] jobs, int[] vacancy, WageBand band) {
        double filled = 0;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) != band) continue;
            filled += jobs[job.ordinal()] - vacancy[job.ordinal()];
        }
        return filled;
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
