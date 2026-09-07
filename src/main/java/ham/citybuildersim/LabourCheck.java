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
         * their own level. Before this, the number on the left was 220 and the
         * number on the right was zero.
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
         * RENT FOLLOWS THE LIVE UNSKILLED WAGE (2026-09-06).
         *
         * It was struck off PayTier.UNSKILLED at compile time, the last price
         * in the game that did not know labour had become a market. Doubling
         * the minimum wage must double rent, and the reloaded city must charge
         * what the live one charged.
         * ------------------------------------------------------------------ */
        System.out.println("\n--- rent follows the unskilled wage ---");
        CommercialHandler rents = back.getEconomyManager().getCommercialHandler();
        double rentBefore = rents.getRentPrice();
        close("a reloaded city charges the rent it was charging",
                rentBefore, lived.getEconomyManager().getCommercialHandler().getRentPrice(), 1e-9);
        close("rent is 30% of two unskilled wages per four-person home",
                rentBefore, CommercialHandler.rentFor(back.getLabourMarket().getWage(JobType.NO_DIPLOMA)), 1e-9);

        double floorBefore = back.getLabourMarket().getMinimumWage();
        back.getLabourMarket().setMinimumWage(floorBefore * 2);
        quietly(() -> back.simulateMonths(1));
        double unskilledNow = back.getLabourMarket().getWage(JobType.NO_DIPLOMA);
        assertTrue("fixture: doubling the floor moved the unskilled wage", unskilledNow > floorBefore * 1.5);
        close("...and rent moved with it", rents.getRentPrice(), CommercialHandler.rentFor(unskilledNow), 1e-9);
        assertTrue("...upward", rents.getRentPrice() > rentBefore * 1.5);

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
            growing.simulateMonths(24);
        });
        double[] skilledBefore = growing.getPopulationManager().getSkilledHeads().clone();
        double adultShare = growing.getCohorts().share(AgeBand.ADULT);
        quietly(() -> growing.simulateMonths(1));
        double[] skilledNow = growing.getPopulationManager().getSkilledHeads();
        double[] arrived = growing.getMigration().getLastArrivalMix();
        double[] left = growing.getMigration().getLastDepartureMix();
        double skilledArrivals = 0, skilledDepartures = 0, gained = 0, held = 0;
        for (int b = 1; b < skilledBefore.length; b++) {
            skilledArrivals += arrived[b];
            skilledDepartures += left[b];
            gained += skilledNow[b] - skilledBefore[b];
            held += skilledBefore[b];
        }
        assertTrue("fixture: skilled people arrived this month", skilledArrivals > 1);
        assertTrue("fixture: the city is not all adults", adultShare < .9 && adultShare > .3);
        double net = skilledArrivals - skilledDepartures;
        double expected = net * adultShare;
        double retirements = held * .01;   // a generous bound on a month of deaths and ageing out
        System.out.printf("   skilled arrivals %.1f, departures %.1f, adult share %.3f,"
                + " counts moved %.1f, expected %.1f (unscaled %.1f)%n",
                skilledArrivals, skilledDepartures, adultShare, gained, expected, net);

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
         * a diploma is the base, NONE is zero, and every band above the
         * diploma is bought at a premium or does not come.
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
        assertTrue("NOBODY ARRIVES WITHOUT A DIPLOMA",
                mixShort[WageBand.NONE.ordinal()] == 0);
        assertTrue("...so the unskilled band is only ever home-grown",
                WageBand.NONE.arrivalCeiling() == 0);

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

        cleanUp(root);
        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
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
