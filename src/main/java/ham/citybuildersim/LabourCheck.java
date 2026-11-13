package ham.citybuildersim;

import ham.citybuildersim.sectors.HeavyIndustry;

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
 *
 * AND THE FLOOR IS A REAL MINIMUM (WG1, 2026-11-10). What this proves about it:
 * the ladder is anchored on the going rate and pays what PayTier paid; a new
 * city's floor starts at DEFAULT_FLOOR_SHARE of the going rate, with no job paid
 * it; the dial lifts only the wages under it and leaves the ladder's bases
 * alone; the floor's tie (prices, the average wage, nothing) holds the floor
 * where each says, to the dial's bounds, in both forms of the cost of living's
 * walk; the going rate is redenominated with the floor; and the tie is saved,
 * an older array reading prices at the default share. And (WS1, 2026-11-10) the average the tie last read is saved and scaled by a reform, so a
 * dial move after a load or in a reform month reads its share against the right average, and the tie's share stops short of the ratchet: the range
 * is the world file's ratios, never 1, and a floor tied at its top settles under the mean of the wages it does not hold.
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

        /* ============ 1. THE GOING RATE ANCHORS THE LADDER, AND THE FLOOR STARTS UNDER IT ============

           The wage market anchors to PayTier rather than replacing it, and the
           ladder is anchored on the going rate - the unskilled tier, what an
           unskilled post pays where there are exactly enough workers - so a
           brand new city pays exactly what it paid before this class existed.
           Every balance measurement already taken survives, and that is not a
           hope, it is this assertion. (Until WG1, 2026-11-10, the minimum wage
           WAS the anchor and started on the unskilled tier; Jerus: "yes a real
           minimum wage", "0.85, Nova Scotia's". The anchor's neutrality is
           restated on the going rate, not bent: the bases are unmoved, and the
           floor is a figure of its own, under them.)
           ======================================================================================= */
        System.out.println("--- the going rate anchors the ladder, and the floor starts under it ---");

        LabourMarket market = new LabourMarket();
        close("the going rate is the unskilled tier",
                market.getGoingRate(), PayTier.UNSKILLED.getMonthlyWage(), 1e-12);
        close("the floor starts at the default share of the going rate",
                market.getMinimumWage(), market.getGoingRate() * LabourMarket.DEFAULT_FLOOR_SHARE, 1e-12);
        assertTrue("...which is under the going rate, a floor of its own",
                market.getMinimumWage() < market.getGoingRate());
        close("the going rate in today's money is the going rate lifted by the cost of living",
                market.cashGoingRate(), market.getGoingRate() * market.getCostOfLiving(), 1e-12);

        for (JobType job : JobType.values()) {
            close("  " + job.name() + " pays what PayTier says",
                    market.baseWage(job), PayTier.wageOf(job), 1e-12);
        }
        boolean someoneOnTheFloor = false;
        for (JobType job : JobType.values()) {
            if (market.isPaidTheFloor(job)) someoneOnTheFloor = true;
        }
        assertTrue("a new city has no job paid the floor", !someoneOnTheFloor);

        // A city founded on a ladder anchors on that ladder's going rate and starts its floor under it, the 1901 way as today's.
        for (WageLadder ladder : new WageLadder[] { WageLadder.TODAY, WageLadder.Y1901 }) {
            LabourMarket founded = new LabourMarket();
            founded.foundOn(ladder);
            close("  founded on " + ladder.name() + " ladder, the going rate is its anchor",
                    founded.getGoingRate(), ladder.anchor(), 1e-12);
            close("  ...the floor starts at the default share of it",
                    founded.getMinimumWage(), ladder.anchor() * LabourMarket.DEFAULT_FLOOR_SHARE, 1e-12);
            close("  ...and the unskilled base is the anchor, as the ladder pays it",
                    founded.baseWage(JobType.NO_DIPLOMA), ladder.anchor(), 1e-12);
        }

        // A reform moves the going rate with the floor and every base, and the floor's share of the going rate stays what it was.
        LabourMarket reformed = new LabourMarket();
        double shareBefore = reformed.getMinimumWage() / reformed.getGoingRate();
        reformed.redenominate(.01);
        close("a currency reform takes the going rate with it", reformed.getGoingRate(), market.getGoingRate() * .01, 1e-15);
        boolean basesFollow = true;
        for (JobType job : JobType.values()) {
            if (Math.abs(reformed.baseWage(job) - market.baseWage(job) * .01) > 1e-15) basesFollow = false;
        }
        assertTrue("...and every base", basesFollow);
        close("...and the floor's share of the going rate is unchanged",
                reformed.getMinimumWage() / reformed.getGoingRate(), shareBefore, 1e-12);

        /* ============ 2. THE DIAL MOVES ONLY THE WAGES UNDER IT ============

           It used to move the whole ladder ("THE DIAL MOVES EVERYTHING": the
           doctor's base doubled with it, the ladder kept its shape), because
           the floor was the anchor. It is only a floor now (WG1, 2026-11-10):
           it lifts the wages under it and nothing over it. The fixture puts
           the floor between the diploma's base and the lowest college job's,
           computed from the ladder's ratios and the going rate, and walks the
           months so the wages have had the time to arrive.
           ============================================================== */
        System.out.println("\n--- the dial moves only the wages under it ---");

        double goingRate = market.getGoingRate();
        double diplomaBase = goingRate * market.ratio(JobType.DIPLOMA);
        double lowestCollegeBase = Double.MAX_VALUE;
        JobType lowestCollege = null;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) == WageBand.COLLEGE && goingRate * market.ratio(job) < lowestCollegeBase) {
                lowestCollegeBase = goingRate * market.ratio(job);
                lowestCollege = job;
            }
        }
        double between = (diplomaBase + lowestCollegeBase) / 2;
        assertTrue("fixture: the floor sits between the diploma's base and the college jobs', inside the dial's bounds",
                between > diplomaBase && between < lowestCollegeBase
                        && between >= market.getMinSettable() && between <= market.getMaxSettable());

        double[] atPar = new double[WageBand.values().length];
        java.util.Arrays.fill(atPar, 1000);   // posts equal to workers in every band: a multiple of one, wages at their bases
        LabourMarket lifted = new LabourMarket();
        LabourMarket twin = new LabourMarket();   // the same city at the default floor
        double doctorBefore = lifted.baseWage(JobType.UNIV_DOCTOR);
        lifted.setMinimumWage(between);
        for (int m = 0; m < 200; m++) { lifted.advanceMonth(atPar, atPar); twin.advanceMonth(atPar, atPar); }

        System.out.printf("   floor between the diploma's base and the college jobs': labourer %.4f -> %.4f, doctor %.4f -> %.4f%n",
                twin.getWage(JobType.NO_DIPLOMA), lifted.getWage(JobType.NO_DIPLOMA),
                twin.getWage(JobType.UNIV_DOCTOR), lifted.getWage(JobType.UNIV_DOCTOR));

        close("the no-diploma wage is lifted to the floor", lifted.getWage(JobType.NO_DIPLOMA), lifted.cashMinimumWage(), 1e-9);
        close("the diploma wage is lifted to the floor", lifted.getWage(JobType.DIPLOMA), lifted.cashMinimumWage(), 1e-9);
        boolean overTheFloor = true;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) != WageBand.COLLEGE && WageBand.of(job) != WageBand.UNIVERSITY) continue;
            close("  the " + job.name() + " wage equals a twin city's at the default floor",
                    lifted.getWage(job), twin.getWage(job), 1e-9);
            if (lifted.getWage(job) <= lifted.cashMinimumWage()) overTheFloor = false;
        }
        assertTrue("fixture: every college and university wage is over the floor, so it is no floor to them", overTheFloor);
        close("the doctor's base is unchanged", lifted.baseWage(JobType.UNIV_DOCTOR), doctorBefore, 1e-12);
        assertTrue("...and the ladder's bases kept their shape",
                Math.abs(lifted.baseWage(JobType.UNIV_DOCTOR) / lifted.baseWage(JobType.NO_DIPLOMA)
                        - PayTier.wageOf(JobType.UNIV_DOCTOR) / PayTier.wageOf(JobType.NO_DIPLOMA)) < 1e-12);
        assertTrue("the no-diploma and diploma bands are the ones paid the floor",
                lifted.isPaidTheFloor(WageBand.NONE) && lifted.isPaidTheFloor(WageBand.DIPLOMA)
                        && !lifted.isPaidTheFloor(WageBand.COLLEGE) && !lifted.isPaidTheFloor(WageBand.UNIVERSITY));
        assertTrue("and the dial is bounded",
                new LabourMarket() {{ setMinimumWage(-5); }}.getMinimumWage()
                        >= LabourMarket.MIN_SETTABLE);

        // Before the law the floor is paid to nobody, whatever the wage: floorBinding off, no job is paid it or near it.
        LabourMarket lawless = new LabourMarket();
        lawless.setFloorBinding(false);
        lawless.setMinimumWage(between);
        assertTrue("without the law no job is paid the floor or near it",
                !lawless.isPaidTheFloor(JobType.NO_DIPLOMA) && !lawless.isNearTheFloor(JobType.NO_DIPLOMA));

        // A floor just under the lowest college job: its wage is over the floor by half of NEAR_FLOOR, so it is near it and is not paid it.
        LabourMarket nearby = new LabourMarket();
        nearby.setMinimumWage(lowestCollegeBase / (1 + LabourMarket.NEAR_FLOOR / 2));
        for (int m = 0; m < 200; m++) nearby.advanceMonth(atPar, atPar);
        assertTrue("a wage a little over the floor is near it, and not paid it",
                nearby.isNearTheFloor(lowestCollege) && !nearby.isPaidTheFloor(lowestCollege));
        assertTrue("...and the doctor's, far over it, is neither",
                !nearby.isNearTheFloor(JobType.UNIV_DOCTOR) && !nearby.isPaidTheFloor(JobType.UNIV_DOCTOR));
        assertTrue("...and the job paid the floor is not near it",
                nearby.isPaidTheFloor(JobType.NO_DIPLOMA) && !nearby.isNearTheFloor(JobType.NO_DIPLOMA));

        /* ============ 2b. THE FLOOR'S TIE ============

           What the floor keeps pace with (WG1, 2026-11-10; spec-wages 3.2):
           prices (the default, and the only rule there was), the average wage
           at a share, or nothing - a cash figure inflation wears down. Each
           fixture causes the condition it reads: the cost of living is raised
           over months by the market's own updateCostOfLiving(), and the average
           wage is said by followAverageWage(), as Game says it each month.
           ============================================================== */
        System.out.println("\n--- the floor's tie ---");
        {
            assertTrue("a new market's floor is tied to prices, at the default share",
                    new LabourMarket().getFloorTie() == LabourMarket.FloorTie.PRICES
                            && new LabourMarket().getTieShare() == LabourMarket.DEFAULT_TIE_SHARE);
            assertTrue("the ties have their words",
                    LabourMarket.FloorTie.PRICES.label().equals("prices")
                            && LabourMarket.FloorTie.AVERAGE_WAGE.label().equals("the average wage")
                            && LabourMarket.FloorTie.NOTHING.label().equals("nothing"));

            // Both overloads of updateCostOfLiving() walk the cost of living, so both are held to the tie: prices rising 50% over the months.
            for (int overload = 0; overload < 2; overload++) {
                final boolean based = overload == 1;
                String how = based ? "once the basket is based" : "before the basket is based";

                LabourMarket byPrices = new LabourMarket();
                double realFloor = byPrices.getMinimumWage(), cashFloor = byPrices.cashMinimumWage();
                for (int m = 0; m < 120; m++) {
                    if (based) byPrices.updateCostOfLiving(1.5, 0); else byPrices.updateCostOfLiving(1.5);
                }
                assertTrue("fixture: the cost of living rose (" + how + ")", byPrices.getCostOfLiving() > 1.2);
                close("tied to prices, the real floor is unchanged while prices rise (" + how + ")",
                        byPrices.getMinimumWage(), realFloor, 1e-12);
                assertTrue("...and the cash floor rises with them",
                        byPrices.cashMinimumWage() > cashFloor * 1.2);
                close("...by the cost of living", byPrices.cashMinimumWage(), cashFloor * byPrices.getCostOfLiving(), 1e-12);

                LabourMarket byNothing = new LabourMarket();
                byNothing.setFloorTie(LabourMarket.FloorTie.NOTHING, LabourMarket.DEFAULT_TIE_SHARE);
                for (int m = 0; m < 120; m++) {
                    if (based) byNothing.updateCostOfLiving(1.5, 0); else byNothing.updateCostOfLiving(1.5);
                }
                assertTrue("fixture: the same rise in the cost of living (" + how + ")", byNothing.getCostOfLiving() > 1.2);
                close("tied to nothing, the cash floor holds while prices rise (" + how + ")",
                        byNothing.cashMinimumWage(), cashFloor, 1e-9);
                assertTrue("...so the real floor falls with them",
                        byNothing.getMinimumWage() < realFloor / 1.2);
                close("...by the cost of living", byNothing.getMinimumWage(), realFloor / byNothing.getCostOfLiving(), 1e-9);

                // The cash figure fixed through a fall in prices too: the real floor rises, held at the dial's top.
                LabourMarket deflated = new LabourMarket();
                deflated.setFloorTie(LabourMarket.FloorTie.NOTHING, LabourMarket.DEFAULT_TIE_SHARE);
                for (int m = 0; m < 240; m++) {
                    if (based) deflated.updateCostOfLiving(.1, 0); else deflated.updateCostOfLiving(.1);
                }
                assertTrue("fixture: prices fell far enough to take a cash floor past the top of the dial",
                        cashFloor / deflated.getCostOfLiving() > deflated.getMaxSettable());
                close("tied to nothing, a cash floor that prices would take over the dial's top is held at the top (" + how + ")",
                        deflated.getMinimumWage(), deflated.getMaxSettable(), 1e-12);
            }

            // setCostOfLiving() is the load path's setter: it puts the figure back and moves no floor.
            LabourMarket loading = new LabourMarket();
            loading.setFloorTie(LabourMarket.FloorTie.NOTHING, LabourMarket.DEFAULT_TIE_SHARE);
            double floorOnLoad = loading.getMinimumWage();
            loading.setCostOfLiving(1.7);
            close("setting the cost of living (the load path) does not strike a cash floor", loading.getMinimumWage(), floorOnLoad, 1e-15);

            // THE AVERAGE WAGE: followAverageWage(a) puts the cash floor at share x a, at any cost of living and nudge.
            LabourMarket byAverage = new LabourMarket();
            double floorBeforeTie = byAverage.getMinimumWage();
            byAverage.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, .5);
            close("tying the floor does not move it that month", byAverage.getMinimumWage(), floorBeforeTie, 1e-15);
            double average = byAverage.cashGoingRate() * 1.2;
            byAverage.followAverageWage(average);
            close("tied to the average wage, the cash floor is the share of it", byAverage.cashMinimumWage(), .5 * average, 1e-12);
            byAverage.setCostOfLiving(1.3);
            byAverage.setMinimumWageAdjustment(.1);
            byAverage.followAverageWage(average);
            close("...whatever the cost of living and the player's nudge", byAverage.cashMinimumWage(), .5 * average, 1e-12);

            double movedTo = .6 * average;
            byAverage.setMinimumWage(byAverage.floorForCash(movedTo));
            close("setting the floor under it moves the share to match", byAverage.getTieShare(), .6, 1e-9);
            close("...and the cash floor is what was set", byAverage.cashMinimumWage(), movedTo, 1e-12);
            byAverage.followAverageWage(average);
            close("...and the next month's strike keeps it", byAverage.cashMinimumWage(), movedTo, 1e-12);

            double struck = byAverage.getMinimumWage();
            byAverage.followAverageWage(0);
            byAverage.followAverageWage(Double.NaN);
            byAverage.followAverageWage(Double.POSITIVE_INFINITY);
            close("a month with no average wage (nobody paid, or not a number) leaves the floor where it was",
                    byAverage.getMinimumWage(), struck, 1e-15);

            // A floor the tie would take outside the dial is held at the bound.
            byAverage.followAverageWage(byAverage.getMaxSettable() * 1e6);
            close("a tie that would take the floor over the dial's top holds it at the top",
                    byAverage.getMinimumWage(), byAverage.getMaxSettable(), 1e-12);
            byAverage.followAverageWage(byAverage.getMinSettable() * 1e-6);
            close("...and one that would take it under the dial's bottom holds it at the bottom",
                    byAverage.getMinimumWage(), byAverage.getMinSettable(), 1e-12);
            byAverage.setMinimumWage(byAverage.getMaxSettable() * 1e6);
            close("setting the floor over the dial holds the share at its top too", byAverage.getTieShare(), LabourMarket.MAX_TIE_SHARE, 1e-12);

            // Under the other ties the dial leaves the share alone, and the average wage moves nothing.
            LabourMarket aside = new LabourMarket();
            aside.followAverageWage(aside.cashGoingRate());
            close("tied to prices, the average wage moves no floor",
                    aside.getMinimumWage(), LabourMarket.DEFAULT_MINIMUM_WAGE, 1e-15);
            aside.setMinimumWage(aside.getGoingRate());
            close("...and the dial leaves the share alone", aside.getTieShare(), LabourMarket.DEFAULT_TIE_SHARE, 1e-15);

            // The share is held to its range, a null tie reads prices, and each move is one decision in the log.
            DecisionLog log = new DecisionLog();
            LabourMarket decided = new LabourMarket();
            decided.recordTo(log);
            decided.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, 5);
            close("a share over the top of the range is held to it", decided.getTieShare(), LabourMarket.MAX_TIE_SHARE, 1e-15);
            decided.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, .01);
            close("...and one under the bottom of it", decided.getTieShare(), LabourMarket.MIN_TIE_SHARE, 1e-15);
            decided.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, LabourMarket.DEFAULT_TIE_SHARE);
            assertTrue("a tie to the average wage is said at its share",
                    log.last().label().equals("Wage floor tied to the average wage at 45%"));
            int said = log.size();
            decided.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, LabourMarket.DEFAULT_TIE_SHARE);
            assertTrue("...and said once: the same tie again is no decision", log.size() == said);
            decided.setFloorTie(LabourMarket.FloorTie.NOTHING, LabourMarket.DEFAULT_TIE_SHARE);
            assertTrue("a tie to nothing is said as a cash figure",
                    log.last().label().equals("Wage floor tied to nothing: a cash figure"));
            decided.setFloorTie(null, LabourMarket.DEFAULT_TIE_SHARE);
            assertTrue("a null tie reads prices, and is said so",
                    decided.getFloorTie() == LabourMarket.FloorTie.PRICES && log.last().label().equals("Wage floor tied to prices"));
        }

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
         * glut unfixable by price. The band's own floor is MIN_MULTIPLE (0.7x)
         * of the going rate, and the default minimum wage is DEFAULT_FLOOR_SHARE
         * (0.85x) of it, over it - so the glut stops at the minimum, not at its
         * own floor, and the wage cannot fall further. That is the mechanism, not
         * a rounding artefact: it is what forces the adjustment into departures.
         * (Until WG1, 2026-11-10, the two were the same number, by construction,
         * because the floor was the unskilled base itself; now it holds only for
         * a floor over MIN_MULTIPLE of the going rate, which the default is.)
         */
        assertTrue("fixture: the default floor is over the unskilled band's own floor of the going rate",
                LabourMarket.DEFAULT_FLOOR_SHARE > LabourMarket.MIN_MULTIPLE);
        assertTrue("no wage falls below the minimum",
                slack.getWage(JobType.NO_DIPLOMA) >= slack.cashMinimumWage() - 1e-9);
        assertTrue("...so an unskilled glut is pinned, at a floor over its own",
                slack.isPinned(WageBand.NONE));
        assertTrue("...and paid the floor",
                slack.isPaidTheFloor(JobType.NO_DIPLOMA));

        /*
         * ...AND PINNED AT THE FLOOR IN TODAY'S MONEY (B8, 0.7.47). Wages are
         * held at cashMinimumWage(), the floor lifted by the cost of living;
         * isPinned() read the founding figure, so once living cost more a band
         * held at the indexed floor with people to spare read as one that could
         * still get cheaper, and nobody left. A glut with living 10% dearer.
         */
        LabourMarket dearer = new LabourMarket();
        dearer.setCostOfLiving(1.1);
        // THE FLOOR AT THE GOING RATE, for this fixture (WG1, 2026-11-10). It meant "the floor is the unskilled base", which held the Diploma
        // glut (0.70 x 1.30 = .91 of the going rate) at the floor; the default floor is .85 of it, under the Diploma glut's own, which would
        // hold nobody here. A floor at the going rate is over .91 and holds the Diploma glut, and so causes the condition the section needs.
        dearer.setMinimumWage(dearer.getGoingRate());
        for (int m = 0; m < 200; m++) dearer.advanceMonth(posts, many);
        double diploma = dearer.getWage(JobType.DIPLOMA), cashFloor = dearer.cashMinimumWage();
        System.out.printf("   living x1.1: Diploma paid %.4f, the floor in today's money %.4f, in founding money %.4f%n",
                diploma, cashFloor, dearer.getMinimumWage());
        assertTrue("fixture: the Diploma glut is held at the indexed floor, over the founding one and its tolerance",
                Math.abs(diploma - cashFloor) <= 1e-9 * cashFloor
                        && diploma > dearer.getMinimumWage() * (1 + LabourMarket.PINNED_TOLERANCE));
        assertTrue("a band held at the indexed floor with people spare is pinned at a cost of living of 1.1",
                dearer.isPinned(WageBand.DIPLOMA));

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
            city.getGovernmentInvestor().spend(-50_000_000 * PriceLevel.FACTOR);
            city.getLandManager().setOwnedSqFt(
                    city.getLandManager().getOwnedSqFt() + 400_000_000);
            city.buildStack(t(city, "Low-Rise Apartments"), 60, true);
            city.buildStack(t(city, "General Hospital"), 2, true);
            city.buildStack(t(city, "Regional Medical Centre"), 1, true);
            city.buildStack(city.getBuildingManager().getTemplate(HeavyIndustry.MINI_MILL), 4, true);
            city.buildStack(t(city, "Small Grocery Store"), 8, true);
            city.buildStack(t(city, "Coal Power Plant"), 2, true);
            city.buildStack(t(city, "Water Treatment Plant"), 2, true);
            city.buildStack(t(city, "Paved Road"), 30, true);
            city.simulateMonths(240);
        });

        PopulationManager people = city.getPopulationManager();
        double[] own = people.workforceByBand();
        double[] band = people.postsByBand();
        long[] jobs = people.getJobs();
        long[] vacancy = people.getJobVacancy();

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

        Path root = TempDirs.create("labour");
        GameFiles files = new GameFiles(root.resolve("data"), root.resolve("no-legacy"));

        Game lived = new Game(files);
        quietly(() -> {
            lived.newGame();
            lived.getGovernmentInvestor().spend(-20_000_000 * PriceLevel.FACTOR);
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

        // THE FLOOR'S TIE (WG1): a tie of NOTHING at a share moved, set before the save so the real save below carries it.
        final double tieShareSaved = .33;
        lived.getLabourMarket().setFloorTie(LabourMarket.FloorTie.NOTHING, tieShareSaved);

        assertTrue("the city saved", lived.saveGame(1, "labour").ok);

        Game back = new Game(files);
        quietly(() -> back.loadGameSave(1));

        assertTrue("the floor's tie came back through a real save", back.getLabourMarket().getFloorTie() == LabourMarket.FloorTie.NOTHING);
        close("...and its share", back.getLabourMarket().getTieShare(), tieShareSaved, 1e-12);

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

        // The tie through state() and restore() alone, and an array cut before the three values the tie and its average added: an older save, which is prices.
        LabourMarket tiedMarket = new LabourMarket();
        tiedMarket.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, .61);
        double[] tiedState = tiedMarket.state();
        LabourMarket tiedBack = new LabourMarket();
        tiedBack.restore(tiedState);
        assertTrue("a tie to the average wage survives state() and restore()", tiedBack.getFloorTie() == LabourMarket.FloorTie.AVERAGE_WAGE);
        close("...with its share", tiedBack.getTieShare(), .61, 1e-12);
        LabourMarket olderBack = new LabourMarket();
        olderBack.setFloorTie(LabourMarket.FloorTie.NOTHING, .77);
        olderBack.restore(java.util.Arrays.copyOf(tiedState, tiedState.length - 3));
        assertTrue("an array cut before the tie restores prices", olderBack.getFloorTie() == LabourMarket.FloorTie.PRICES);
        close("...at the default share", olderBack.getTieShare(), LabourMarket.DEFAULT_TIE_SHARE, 1e-15);
        double[] outOfRange = tiedState.clone();
        outOfRange[outOfRange.length - 3] = LabourMarket.FloorTie.values().length;   // the tie is the third value from the end (WS1: the last average follows its share)
        LabourMarket garbledBack = new LabourMarket();
        garbledBack.setFloorTie(LabourMarket.FloorTie.NOTHING, .77);
        garbledBack.restore(outOfRange);
        assertTrue("a tie out of range reads prices", garbledBack.getFloorTie() == LabourMarket.FloorTie.PRICES);

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

        // The tie the save above carried is put back to prices: this section asks what a floor that moves wages does to rent, and a floor tied
        // to nothing would be restruck by the cost of living this month too.
        back.getLabourMarket().setFloorTie(LabourMarket.FloorTie.PRICES, LabourMarket.DEFAULT_TIE_SHARE);
        double floorBefore = back.getLabourMarket().getMinimumWage();
        double targetBefore = rents.getRentTarget();
        // THE MINIMUM WAGE OPENED IN THE FIXTURE (R0): the question is whether rent follows a floor that moves wages, and a city on history's
        // dates has no floor binding any wage until 1918. Opened here (the next month's top tells the labour market) so the floor is the law.
        back.getEconomyManager().getTaxPolicy().openAll();
        back.getLabourMarket().setMinimumWage(floorBefore * 2);
        quietly(() -> back.simulateMonths(1));
        double unskilledNow = back.getLabourMarket().getWage(JobType.NO_DIPLOMA);
        // (WG1, 2026-11-10) The floor is a real minimum now: doubling it lifts the wages under it, not the whole ladder. The unskilled wage is
        // one of them - the default floor is .85 of the going rate, the doubled one 1.7 of it, over what the unskilled are paid unless they
        // are very scarce - and the unskilled wage is the one the old rent formula read, so the fixture still causes what the section asks.
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
            growing.getGovernmentInvestor().spend(-20_000_000 * PriceLevel.FACTOR);
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

        boolean hasSkilledArrivals = Observation.reached("fixture: found a month with skilled arrivals in it",
                found > 0,
                "arriving skill mix");
        if (hasSkilledArrivals) assertTrue("fixture: the city is not all adults", adultShare < .9 && adultShare > .3);
        double net = skilledArrivals - skilledDepartures;
        double expected = net * adultShare;
        double retirements = held * .01;   // a generous bound on a month of deaths and ageing out
        System.out.printf("   month %d: skilled arrivals %.1f, departures %.1f, adult share %.3f,"
                + " counts moved %.1f, expected %.1f (unscaled %.1f)%n",
                found, skilledArrivals, skilledDepartures, adultShare, gained, expected, net);

        if (hasSkilledArrivals) assertTrue("the skilled counts gained the ADULT share of the skilled arrivals",
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
        boolean distinctSkillMix = Observation.reached("fixture: the two explanations are far enough apart to tell apart",
                Math.abs(net - expected) > 1,
                "arrival mix compared with the whole mix");
        if (hasSkilledArrivals && distinctSkillMix) assertTrue("...and not the whole mix",
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
            short_.getGovernmentInvestor().spend(-50_000_000 * PriceLevel.FACTOR);
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
            // ...AND UNTIL ITS LABOURERS ARE PAID NO MORE THAN THE GOING RATE (GW1).
            // GW1: the gate counts a year's draw (Sector.GATE_DRAW_MONTHS), so this city's works come earlier and its unskilled posts outrun its labourers for ten years; the fixture now runs on to a month somebody moves in and the labourers are paid no more than the going rate.
            for (int extra = 0; extra < 180
                    && (short_.getMigration().getLastArrivals() <= 1
                            || short_.getLabourMarket().bandPremium(WageBand.NONE) > 1); extra++) {
                short_.simulateMonths(1);
            }
        });
        LabourMarket m2 = short_.getLabourMarket();
        long doctorPosts = short_.getPopulationManager().getJobs()[JobType.UNIV_DOCTOR.ordinal()];
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

        boolean arrivalsPresent = Observation.reached("fixture: somebody moved in at all",
                arrivalsTotal > 1,
                "migration at no wage premium");
        boolean noWagePremium = Observation.reached("fixture: this city pays its labourers no more than the going rate",
                m2.bandPremium(WageBand.NONE) <= 1,
                "migration at no wage premium");
        if (arrivalsPresent && noWagePremium) assertTrue("NOBODY ARRIVES WITHOUT A DIPLOMA into a city paying the going rate for them",
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
            buses.getGovernmentInvestor().spend(-50_000_000 * PriceLevel.FACTOR);
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
        boolean wagePremium = Observation.reached("fixture: the town pays its labourers over the going rate",
                nonePremium > 1,
                "migration at a wage premium");
        boolean premiumArrivals = Observation.reached("fixture: somebody moved in",
                buses.getMigration().getLastArrivals() > 0,
                "migration at a wage premium");
        if (wagePremium && premiumArrivals) assertTrue("a dear unskilled wage brings arrivals without a diploma", busMix[WageBand.NONE.ordinal()] > 0);
        double noneOverDiploma = WageBand.NONE.arrivalCeiling() * Migration.reach(nonePremium)
                * busChance[WageBand.NONE.ordinal()] / busChance[WageBand.DIPLOMA.ordinal()];
        if (wagePremium && premiumArrivals) assertTrue("...against the diploma arrivals, the ceiling times reach() times the chance of work - the graduates' own terms",
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

        /* ============ 7. THE TIE'S AVERAGE SURVIVES A LOAD AND A REFORM (WS1) ============

           WG1's review found the average wage the tie last read was neither
           saved nor scaled by a reform: after a load it was 0, so the dial
           could not move the tie's share and the next month's strike undid the
           move; in a reform month the dial divided a new-unit floor by an
           old-unit average. Each fixture causes its condition. (a) is a played
           city under the tie, saved by the real save path and loaded into the
           harness's temp folder: the live city and the loaded one are given the
           same dial move before the loaded one has run a month. (b) is a market
           that has read an average, set a floor, and gone through a reform.
           ================================================================== */
        System.out.println("\n--- the tie's average survives a load and a reform (WS1) ---");
        {
            final double tiedAt = .5, movedTo = .6;
            LabourMarket live = lived.getLabourMarket();
            live.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, tiedAt);
            quietly(() -> lived.simulateMonths(1));
            double lastAverage = lastAverageOf(live);
            assertTrue("fixture: the month struck the floor on an average of the played city's wages", lastAverage > 0 && Double.isFinite(lastAverage));
            assertTrue("fixture: the floor was struck, not held at a bound of the dial",
                    live.getMinimumWage() > live.getMinSettable() && live.getMinimumWage() < live.getMaxSettable());
            close("the floor the month struck is the share of the average it read", live.cashMinimumWage(), tiedAt * lastAverage, 1e-9);

            assertTrue("the played city under the tie saved", lived.saveGame(2, "labour-tie").ok);
            Game tiedBack2 = new Game(files);
            quietly(() -> tiedBack2.loadGameSave(2));
            LabourMarket loaded = tiedBack2.getLabourMarket();
            assertTrue("the tie came back through the real save path, at its share",
                    loaded.getFloorTie() == LabourMarket.FloorTie.AVERAGE_WAGE && Math.abs(loaded.getTieShare() - tiedAt) < 1e-12);
            close("...and the average it last read came back with it", lastAverageOf(loaded), lastAverage, 1e-9);

            // The same dial move on both cities, before the loaded one has run a month.
            double asked = live.floorForCash(movedTo * lastAverage);
            live.setMinimumWage(asked);
            loaded.setMinimumWage(asked);
            close("the live city's dial move puts the share at the move's cash over the last average", live.getTieShare(), movedTo, 1e-9);
            close("the loaded city's, before any month, puts it at the move's cash over the last average",
                    loaded.getTieShare(), loaded.cashAt(asked) / lastAverage, 1e-9);
            close("...the same share the live city gets from the same move, not the share before it", loaded.getTieShare(), live.getTieShare(), 1e-9);

            quietly(() -> tiedBack2.simulateMonths(1));
            double newAverage = lastAverageOf(loaded);
            assertTrue("fixture: the month read an average of its own", newAverage > 0 && Double.isFinite(newAverage));
            assertTrue("fixture: the floor was struck again, not held at a bound of the dial",
                    loaded.getMinimumWage() > loaded.getMinSettable() && loaded.getMinimumWage() < loaded.getMaxSettable());
            close("after a month the loaded city's floor is still the dial's share of the new average (not undone to the share before)",
                    loaded.cashMinimumWage() / newAverage, movedTo, 1e-9);

            // The array alone: cut before the average, an older save reads 0 and the dial moves no share, as it did; a month with nobody employed saves 0.
            LabourMarket withAverage = new LabourMarket();
            withAverage.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, .61);
            withAverage.followAverageWage(withAverage.cashGoingRate() * 1.3);
            double[] withState = withAverage.state();
            LabourMarket withBack = new LabourMarket();
            withBack.restore(withState);
            close("the average survives state() and restore()", lastAverageOf(withBack), withAverage.cashGoingRate() * 1.3, 1e-12);
            LabourMarket olderAverage = new LabourMarket();
            olderAverage.restore(java.util.Arrays.copyOf(withState, withState.length - 1));
            assertTrue("an array cut before the average keeps the tie it carries",
                    olderAverage.getFloorTie() == LabourMarket.FloorTie.AVERAGE_WAGE && Math.abs(olderAverage.getTieShare() - .61) < 1e-12);
            close("...and reads no average, as before", lastAverageOf(olderAverage), 0, 1e-15);
            olderAverage.setMinimumWage(olderAverage.getMinimumWage() * 1.5);
            close("...so a dial move leaves its share alone", olderAverage.getTieShare(), .61, 1e-12);
            LabourMarket nobody = new LabourMarket();
            nobody.followAverageWage(Double.NaN);
            close("a month with no average wage saves 0, never a NaN", lastAverageOf(nobody), 0, 1e-15);

            // (b) A reform: the same real floor, in the new unit, has the same share of an average that is in the new unit too.
            for (double scale : new double[] { .01, 4 }) {
                LabourMarket reform = new LabourMarket();
                reform.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, tiedAt);
                double average = reform.cashGoingRate() * 1.4;
                reform.followAverageWage(average);
                double realFloor = reform.floorForCash(movedTo * average);
                reform.setMinimumWage(realFloor);
                double shareBeforeReform = reform.getTieShare();
                assertTrue("fixture (reform x" + scale + "): the dial's move put the share at its own", Math.abs(shareBeforeReform - movedTo) < 1e-9);
                reform.redenominate(scale);
                close("a reform x" + scale + " scales the average the tie last read with the wages", lastAverageOf(reform), average * scale, 1e-12);
                reform.setMinimumWage(realFloor * scale);
                close("...so the same real floor times the scale has the same tie share as before it",
                        reform.getTieShare(), shareBeforeReform, 1e-9);
            }
        }

        /* ============ 8. THE TIE'S SHARE STOPS SHORT OF THE RATCHET (WS1) ============

           WG1's review: at 1.0 the floor is last month's average itself, so
           each month it lifts every wage under the average, the average rises
           with them, and the ladder ratchets toward its top. Under 1 a floor
           struck at share s of an average that includes it settles: with a
           share p of the posts held at the floor f and the rest at a mean w,
           f = s (p f + (1 - p) w), so f = s (1 - p) w / (1 - s p) < w.
           The range is the world file's: the ratios run from the United States
           to Costa Rica, and the range holds every one and no more.
           ================================================================== */
        System.out.println("\n--- the tie's share stops short of the ratchet (WS1) ---");
        {
            LabourMarket asking = new LabourMarket();
            asking.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, 1.0);
            close("asking the average wage itself holds the share at the top of the range", asking.getTieShare(), LabourMarket.MAX_TIE_SHARE, 1e-15);
            asking.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, .05);
            close("...and asking .05 holds it at the bottom", asking.getTieShare(), LabourMarket.MIN_TIE_SHARE, 1e-15);
            assertTrue("the top of the range stops short of the average wage itself", LabourMarket.MAX_TIE_SHARE < 1.0);

            double lowestRatio = Double.POSITIVE_INFINITY, highestRatio = Double.NEGATIVE_INFINITY;
            int nationsCounted = 0;
            for (WorldMeasures.Place nation : WorldMeasures.load().nations()) {
                double ratio = nation.value("kaitz");
                if (!Double.isFinite(ratio)) continue;
                nationsCounted++;
                lowestRatio = Math.min(lowestRatio, ratio);
                highestRatio = Math.max(highestRatio, ratio);
            }
            assertTrue("fixture: the world file has nations with a minimum-wage-to-average-wage ratio", nationsCounted > 0);
            assertTrue("the range reaches the lowest nation's ratio, so it can be copied", LabourMarket.MIN_TIE_SHARE <= lowestRatio);
            assertTrue("...and the highest, so none is out of reach", LabourMarket.MAX_TIE_SHARE >= highestRatio);

            // A city whose average is well over its bottom rung: posts by job, so many unskilled and diploma posts and a spread of skilled ones. At par
            // (posts equal to workers in every band) every wage is at its base, and the average wage is the posts' wage bill over the posts, as
            // PopulationManager.getAverageWage() reads it with every post filled.
            JobType[] ladderJobs = JobType.values();
            double[] postsByJob = new double[ladderJobs.length];
            double[] postsByBand = new double[WageBand.values().length];
            double postsTotal = 0;
            for (JobType job : ladderJobs) {
                double postsOfJob = switch (WageBand.of(job)) { case NONE -> 300; case DIPLOMA -> 300; case COLLEGE -> 100; case UNIVERSITY -> 50; };
                postsByJob[job.ordinal()] = postsOfJob;
                postsByBand[WageBand.of(job).ordinal()] += postsOfJob;
                postsTotal += postsOfJob;
            }
            LabourMarket tied = new LabourMarket();
            tied.setFloorTie(LabourMarket.FloorTie.AVERAGE_WAGE, LabourMarket.MAX_TIE_SHARE);
            LabourMarket untied = new LabourMarket();   // the same city at the default floor: what the market alone pays
            double startingBill = 0;
            for (JobType job : ladderJobs) startingBill += postsByJob[job.ordinal()] * tied.getWage(job);
            assertTrue("fixture: the average wage is over the unskilled wage by more than the top of the range lets the floor reach",
                    LabourMarket.MAX_TIE_SHARE * startingBill / postsTotal > tied.getWage(JobType.NO_DIPLOMA));

            final int tiedMonths = 150;
            int bound = 0;
            boolean underUnheldMean = true, underTop = true, rungAbove = true;
            double floorNow = 0, floorLastMonth = 0, unheldMean = 0, lowestUniversity = 0;
            for (int m = 0; m < tiedMonths; m++) {
                double bill = 0;
                for (JobType job : ladderJobs) bill += postsByJob[job.ordinal()] * tied.getWage(job);
                tied.followAverageWage(bill / postsTotal);
                tied.advanceMonth(postsByBand, postsByBand);
                untied.advanceMonth(postsByBand, postsByBand);

                floorLastMonth = floorNow;
                floorNow = tied.cashMinimumWage();
                double unheldBill = 0, unheldPosts = 0;
                lowestUniversity = Double.MAX_VALUE;
                for (JobType job : ladderJobs) {
                    if (!tied.isPaidTheFloor(job)) {
                        unheldBill += postsByJob[job.ordinal()] * tied.getWage(job);
                        unheldPosts += postsByJob[job.ordinal()];
                    }
                    if (WageBand.of(job) == WageBand.UNIVERSITY) lowestUniversity = Math.min(lowestUniversity, tied.getWage(job));
                }
                unheldMean = unheldBill / unheldPosts;
                if (tied.getWage(JobType.NO_DIPLOMA) > untied.getWage(JobType.NO_DIPLOMA) * (1 + 1e-9)) bound++;
                if (!(floorNow < unheldMean)) underUnheldMean = false;
                if (tied.getMinimumWage() >= tied.getMaxSettable()) underTop = false;
                if (!(lowestUniversity > floorNow)) rungAbove = false;
            }
            System.out.printf("   tied at %.2f for %d months: floor %.4f (the going rate %.4f), mean of the posts it does not hold %.4f, bound %d months%n",
                    LabourMarket.MAX_TIE_SHARE, tiedMonths, floorNow, tied.cashGoingRate(), unheldMean, bound);
            assertTrue("the floor bound in most of the months (it lifted the unskilled wage over what the market alone pays)", bound > tiedMonths * 9 / 10);
            assertTrue("the floor settles: its month-on-month move is under 0.1% by the end", Math.abs(floorNow - floorLastMonth) / floorLastMonth < .001);
            assertTrue("the floor stayed under the mean wage of the posts it does not hold, every month", underUnheldMean);
            assertTrue("the floor never reached the top of the dial", underTop);
            assertTrue("the university band's lowest wage stayed above the floor, every month", rungAbove);
        }

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
     * (Derived for the chase alone; since 0.7.42 half of each step is what
     * people expect and the chase a forty-eighth - LagWatch's recurrence -
     * and the window is still 2 / DRIFT_PER_MONTH.)
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

        /** One month, played; the index and the expectation the month was handed are the ones it read at its top. */
        void month(Game g, Runnable play) {
            LabourMarket wages = g.getLabourMarket();
            double handed = g.getPriceIndex().getIndex();
            double expectedMonthly = g.getExpectations().monthlyExpected();
            boolean based = g.getPriceIndex().isBased();
            double wasCost = wages.getCostOfLiving();
            play.run();
            double target = 1 + (handed - 1) * LabourMarket.COST_OF_LIVING_PASS_THROUGH;
            worstTarget = Math.max(worstTarget, Math.abs(wages.getLivingTarget() - target));
            // REWRITTEN FOR 0.7.42 (the anchor, spec-inflation.md 2.2): once
            // the basket is based the step is the expected month to the
            // EXPECTED_SHARE times the chase to the rest, a DRIFT_PER_MONTH's
            // share of it; it was the chase alone, DRIFT_PER_MONTH of the gap.
            double step = based
                    ? wasCost * Math.pow(1 + expectedMonthly, LabourMarket.EXPECTED_SHARE)
                            * Math.pow(wages.getLivingTarget() / wasCost,
                                    (1 - LabourMarket.EXPECTED_SHARE) * LabourMarket.DRIFT_PER_MONTH)
                    : wasCost + (wages.getLivingTarget() - wasCost) * LabourMarket.DRIFT_PER_MONTH;
            worstStep = Math.max(worstStep, Math.abs(wages.getCostOfLiving() - step));
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
        // WN3: the premise is a STEADY inflation, the crawl alone moving the index (twenty years of it) against goods at a fixed world
        // price. On the world's band the index also takes the real series' moves, re-solved each January (+3.6% and +4.3% in two months
        // at month 253, -2.8% at month 290): the wage index, a lagged chase, then stood under the window's low for months 315 to 320.
        city.playOnLiterals();
        quietly(() -> {
            city.run();
            /*
             * NO SECTOR ASKS TO BUILD (IC1, 2026-11-01): the town is the one stood up by hand below, because the question is the wage
             * index against the price index under a steady crawl and no sector's building is about it (BusinessInvestment.holdSector()).
             * An idle builder's laid-off crews are spare for the staffing gate since IC1, so the planners order what the gate used to
             * refuse, and the town is not the 1,660 people that stood still through the stretch on main's rule: it is 6,567 at the
             * crawl's start, 8,182 at month 164 and 5,432 at month 347, with steel, vehicles, food works and a call centre founded, and
             * its groceries index swinging between 2.06 and 8.75 with the town's own shortages and gluts, not the crawl alone. The wage
             * index, a lagged chase, then stood below the window's low (the index of four years before) for 9 months (182 to 190) and
             * 17 (332 to 348): the lag is a design lag and the window fits a steady inflation, which is the premise. Held, the town is
             * the same on both rules (894 people at the crawl's start, the index 2.0 to 8.2), none is outside the window, and the
             * assertions below read as they were written.
             */
            for (String sector : Sectors.KEYS) city.getBusinessInvestment().holdSector(sector);
            city.getGovernmentInvestor().spend(-2_000_000 * PriceLevel.FACTOR);
            city.getLandManager().setOwnedSqFt(city.getLandManager().getOwnedSqFt() + 200_000_000L);
            /*
             * FC1: THE TOWN IS STOOD UP LIKE ITS DEPOTS AND ROADS BELOW, free (noConstruction). Ordered through the builders (LongPlaytest.build)
             * the founding queue read hundreds of months at one yard's pace and every order carried the builders' premium; the town took
             * another path, its price index pausing for a year in the crawl (4.939 at month 296, 4.931 at 308) and the wage index, a lagged
             * chase, standing 0.07% to 0.3% under the window's low in months 341 to 345 (5 of 240). Stood free the index is steady again (none
             * outside). The question is the wage index against the price index under a steady crawl; nothing about building is in it.
             */
            city.buildStack(t(city, "House"), 200, true);
            city.buildStack(t(city, "Convenience Store"), 10, true);
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
            city.buildStack(t(city, "Coal Power Plant"), 1, true);
            city.buildStack(t(city, "Water Treatment Plant"), 1, true);
            city.buildStack(t(city, "Industrial Bakery"), 2, true);
            city.buildStack(t(city, "Commercial Bank"), 1, true);
            /*
             * ...AND ITS ROADS STAND FROM THE START (0.7.43), as the depots
             * do. Groceries are sold as baskets wanted at a price since
             * 0.7.43, and this town's shops, on roads still on site, ran at a
             * third of their reach: they sold the third the town ate, went
             * broke at their floor, emptied the shelf at month 72, and the
             * index ran a shortage boom to 29% a year and a bust to -12% -
             * not the crawl's steady inflation the premise is. On standing
             * roads the shops run at nine tenths and the crawl is the index.
             */
            city.buildStack(t(city, "Paved Road"), 10, true);
            /*
             * Long enough for the basket to be based on a city that shops -
             * AND FOR THE TOWN TO SETTLE ITS PEOPLE (0.7.47), the lag's window
             * twice over. Since a band held at the indexed floor reads as
             * pinned (LabourMarket.isPinned(), B8) this town's Diploma glut
             * leaves as Migration says it should, and still leaving while the
             * crawl ran it shrank by a tenth from month 86 to 122: the index
             * stood still for three years under a weakening currency, people
             * came to expect next to nothing, and when it climbed again the
             * wage index sat under the window for twenty months - not the
             * crawl's steady inflation the premise is. Settled first, it is.
             */
            city.simulateMonths(PriceIndex.SETTLING_MONTHS + 12
                    + 2 * (int) Math.round(2 / LabourMarket.DRIFT_PER_MONTH));
        });
        if (!Observation.reached("fixture: the basket is based, so there is an index to chase",
                city.getPriceIndex().isBased(),
                "indexed wage chase, lag and reform experiment")) return;

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
        // Where the lag settles under a steady month of `monthly` with people
        // expecting `expected` a month (0.7.42): wages grow with the index
        // when (1 + expected)^share x (target / wages)^((1 - share) x drift)
        // is 1 + monthly.
        double expected = city.getExpectations().monthlyExpected();
        double settles = Math.pow(Math.pow(1 + expected, LabourMarket.EXPECTED_SHARE) / (1 + monthly),
                1 / ((1 - LabourMarket.EXPECTED_SHARE) * LabourMarket.DRIFT_PER_MONTH));
        System.out.printf("   %d months: index %.4f -> %.4f (%.2f%% a year); wage index %.4f,"
                        + " target %.4f, wages/index %.3f (a steady %.3f%% a month settles the lag at %.3f)%n",
                INFLATION_MONTHS, indexFrom, index, (Math.pow(1 + monthly, 12) - 1) * 100,
                wages.getCostOfLiving(), wages.getLivingTarget(), wages.getCostOfLiving() / index,
                monthly * 100, settles);

        boolean steadyInflation = Observation.reached("fixture: the crawl really did inflate the basket, steadily",
                index / indexFrom > Math.pow(1 + CRAWL, INFLATION_MONTHS / 2.0),
                "steady-inflation lag-window laws");
        close("every month wages chased the index the city published",
                lived.worstTarget, 0, 1e-12);
        close("...and moved by the expected month to EXPECTED_SHARE and the chase to the rest - one indexation, not two",
                lived.worstStep, 0, 1e-12);
        if (steadyInflation) assertTrue("so the wage index sits inside the lag's window, every month",
                lived.windowMonths > 0 && lived.outsideWindow == 0);
        if (steadyInflation) assertTrue("...which under a rising index is BELOW it, not a third above",
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
        close("...the expected month and the chase at a time", afterReform.worstStep, 0, 1e-12);
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
        close("...the expected month and the chase at a time", reloaded.worstStep, 0, 1e-12);
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
            g.getGovernmentInvestor().spend(-50_000_000 * PriceLevel.FACTOR);
            g.getLandManager().setOwnedSqFt(g.getLandManager().getOwnedSqFt() + 400_000_000);
            g.buildStack(t(g, "Low-Rise Apartments"), 60, true);
            g.buildStack(t(g, "General Hospital"), 2, true);
            g.buildStack(g.getBuildingManager().getTemplate(HeavyIndustry.MINI_MILL), 4, true);
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
        long[] posts = people.getJobs();
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
            long[] own = s.postsOfferedPerTier();
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
            // ...AND ON TO A MONTH THEY DO LAY CREWS OFF (0.7.18, re-caused).
            for (int i = 0; i < 180 && town.getSectors().construction().getPostsOffered()
                    >= town.getSectors().construction().getPostsStanding(); i++) {
                town.simulateMonths(1);
            }
        });
        people = town.getPopulationManager();
        econ = town.getEconomyManager();
        b = town.getBuildingManager();
        ham.citybuildersim.sectors.Construction crews = econ.getSectors().construction();
        long[] standingPosts = crews.postsPerTier(), kept = crews.postsOfferedPerTier();
        double share = crews.getPostsOfferedShare();
        long standing = crews.getPostsStanding(), offered = crews.getPostsOffered();
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
        long counted = 0, everyPost = 0;
        for (long n : people.getJobs()) counted += n;
        b.setOfferedShare(null);
        for (long n : b.getTotalJobs()) everyPost += n;
        b.setOfferedShare(key -> key.equals(crews.key()) ? crews.getPostsOfferedShare() : 1);
        close("the posts not offered are not in the city's count", counted, everyPost - (standing - offered), .5);
        double townEmployers = everyOtherEmployer(town);
        for (Sector s : econ.getSectors().all()) townEmployers += s.getPayroll();
        close("(a) ...and the households are paid what the employers paid, the laid-off posts in nobody's pay",
                townEmployers / people.getTotalWage(), 1, 1e-9);

        quietly(() -> {
            // ...the city paying for it, on ground it is given.
            town.getGovernmentInvestor().spend(-50_000_000 * PriceLevel.FACTOR);
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

    static double staffed(long[] jobs, long[] vacancy, WageBand band) {
        double filled = 0;
        for (JobType job : JobType.values()) {
            if (WageBand.of(job) != band) continue;
            filled += jobs[job.ordinal()] - vacancy[job.ordinal()];
        }
        return filled;
    }

    /** The average wage a market's tie last read: the last value of its state array (WS1; there is no getter for it). */
    static double lastAverageOf(LabourMarket m) {
        double[] state = m.state();
        return state[state.length - 1];
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
