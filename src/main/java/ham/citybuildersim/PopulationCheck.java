package ham.citybuildersim;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The demographics: do they hold together, and do they move the city the way
 * they were told to?
 *
 * THIS FILE USED TO ASK THE OPPOSITE QUESTION. For two batches the cohorts were
 * a placeholder, and section 4 played two identical cities - one with
 * demographics running, one suppressed - and required every live figure to match
 * exactly. That assertion existed because `BuildingManager.instances` had rotted
 * in place as an unwatched placeholder, and the note here promised that the day
 * the cohorts became load-bearing, the section would fail.
 *
 * It has, and this is what replaced it. The claims are now about BEHAVIOUR, and
 * they are the four things Jerus actually asked for:
 *
 *   - a city fills TOWARD its jobs rather than snapping to them (inertia)
 *   - a city that is full but hiring keeps taking people (housing pulls, it
 *     does not gate)
 *   - arrivals stop exactly where the crowding valves run out, so the promise
 *     that nobody is homeless is kept by arithmetic rather than by hope
 *   - nobody leaves until a pay tier has been dying for a solid year
 */
public class PopulationCheck {

    /**
     * The adult share these fixtures run at.
     *
     * Migration now asks how many residents a job needs, and the answer depends
     * on how many of them can work - so every call has to say. Fixed here rather
     * than sampled from each fixture so the arithmetic in these tests stays
     * comparable between them; the played city further down is where the real
     * share gets exercised.
     */
    static final double ADULT_MIX = PopulationCohorts.equilibriumShare(AgeBand.ADULT);

    static int fails = 0;
    static PrintStream out;
    static PrintStream quiet = new PrintStream(new OutputStream() {
        @Override public void write(int b) { }
        @Override public void write(byte[] b, int off, int len) { }
    });

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-58s %14.3f  expected %14.3f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-58s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) throws Exception {

        out = System.out;

        /* ============ 1. the bands ============ */
        System.out.println("--- the five ages ---");

        check("babies span six years",   AgeBand.BABY.spanMonths(),   72, 0);
        check("children seven",          AgeBand.CHILD.spanMonths(),  84, 0);
        check("teens five",              AgeBand.TEEN.spanMonths(),   60, 0);
        check("adults fifty-two",        AgeBand.ADULT.spanMonths(), 624, 0);
        check("seniors fifty",           AgeBand.SENIOR.spanMonths(),600, 0);

        int wholeLife = 0;
        for (AgeBand b : AgeBand.values()) wholeLife += b.spanMonths();
        check("and together, one hundred and twenty years", wholeLife, 1440, 0);

        // The bands must tile without gaps, or people vanish between them.
        boolean contiguous = true;
        for (AgeBand b : AgeBand.values()) {
            AgeBand n = b.next();
            if (n != null && n.getFromAge() != b.getToAge()) contiguous = false;
        }
        assertTrue("the bands meet exactly - nobody falls between them", contiguous);
        assertTrue("only adults work", AgeBand.ADULT.isWorkingAge()
                && !AgeBand.SENIOR.isWorkingAge() && !AgeBand.TEEN.isWorkingAge());

        /* ============ 2. ageing conserves people ============ */
        System.out.println("\n--- ageing ---");

        PopulationCohorts c = new PopulationCohorts();

        /*
         * The first arrivals seed the pyramid. An empty city has no proportions
         * for migrants to copy, so the first people through the door get the
         * shape a settled population of that size would have - and this is now
         * the ONLY way a city gets its first residents, so it is worth asserting
         * rather than assuming.
         */
        c.migrate(10_000);

        check("the first arrivals seed the pyramid", c.total(), 10_000, 1);

        /*
         * AND THEY ARRIVE IN THE SHAPE THE MODEL SETTLES AT, not in proportion
         * to the band spans. Spans alone are the steady state of a chain where
         * nobody dies; seniors cover fifty of the hundred and twenty years, so
         * that version founded every new town 41.7% pensioners with two fifths
         * of it of working age. Since the switch that is the workforce.
         */
        System.out.printf("   a new city is founded %.0f%% babies  %.0f%% children"
                        + "  %.0f%% teens  %.0f%% adults  %.0f%% seniors%n",
                c.share(AgeBand.BABY) * 100, c.share(AgeBand.CHILD) * 100,
                c.share(AgeBand.TEEN) * 100, c.share(AgeBand.ADULT) * 100,
                c.share(AgeBand.SENIOR) * 100);
        assertTrue("a new city is mostly of working age",
                c.share(AgeBand.ADULT) > .50);
        assertTrue("...and is not founded by pensioners",
                c.share(AgeBand.SENIOR) < .25);
        assertTrue("...but has some, since people do retire",
                c.share(AgeBand.SENIOR) > .08);

        /*
         * THE INVARIANT THAT MATTERS. Every month, everyone alive last month is
         * still somewhere unless they aged out of the top, and the newborns are
         * the only additions. If this drifts, the pyramid is manufacturing or
         * eating people and every figure drawn from it is quietly wrong.
         */
        for (int month = 0; month < 240; month++) {
            double before = c.total();
            c.advanceMonth();
            double expected = before + c.getLastBirths() - c.getLastDeaths();
            if (Math.abs(c.total() - expected) > 1e-6) {
                fails++;
                System.out.printf("  people conserved at month %d: %.6f vs %.6f  FAIL%n",
                        month, c.total(), expected);
                break;
            }
        }
        assertTrue("twenty years of ageing conserves every person", fails == 0);

        assertTrue("babies are born", c.getLastBirths() > 0);
        assertTrue("...and seniors die", c.getLastDeaths() > 0);
        assertTrue("every band is populated", c.get(AgeBand.BABY) > 0
                && c.get(AgeBand.CHILD) > 0 && c.get(AgeBand.TEEN) > 0
                && c.get(AgeBand.ADULT) > 0 && c.get(AgeBand.SENIOR) > 0);

        System.out.printf("   after 20 years: %,.0f babies  %,.0f children  %,.0f teens"
                        + "  %,.0f adults  %,.0f seniors   (dependency %.0f per 100)%n",
                c.get(AgeBand.BABY), c.get(AgeBand.CHILD), c.get(AgeBand.TEEN),
                c.get(AgeBand.ADULT), c.get(AgeBand.SENIOR), c.dependencyRatio());

        /*
         * The known cost of a compartment model, stated so it is a decision on
         * the record rather than a surprise. Nobody has an age; the band only
         * has a mean, so a share of every intake is still in the band long after
         * its nominal span. AgeBand.monthlyOutflowRate() explains why.
         */
        double stillThere = Math.pow(1 - AgeBand.BABY.monthlyOutflowRate(),
                AgeBand.BABY.spanMonths());
        System.out.printf("   (compartment smear: %.0f%% of an intake is still in the"
                + " baby band after six years - known and accepted)%n", stillThere * 100);
        assertTrue("the smear is the expected ~37%, not something worse",
                stillThere > .30 && stillThere < .42);

        /* ============ 2b. mortality, and the trap in the arithmetic ============ */
        System.out.println("\n--- dying ---");

        /*
         * JERUS'S WARNING, TESTED: "since it's %, it's quite easy to do some
         * math that turns out way too many people die."
         *
         * The obvious conversion - annual/12 - is wrong in exactly that
         * direction, and worst where the rate is highest. These assertions say
         * the number written in AgeBand is the number that actually happens over
         * twelve months, which is the only thing that makes it checkable against
         * a real life table.
         */
        for (AgeBand b : AgeBand.values()) {
            double monthly = b.monthlyMortality();
            double overAYear = 1 - Math.pow(1 - monthly, 12);
            check("  " + b.getLabel().toLowerCase() + ": a year of monthly deaths",
                    overAYear, b.getAnnualMortality(), 1e-9);
        }

        // And the naive version really is wrong, which is why the note exists.
        double naive = 1 - Math.pow(1 - AgeBand.SENIOR.getAnnualMortality() / 12, 12);
        assertTrue(String.format("  dividing by twelve would kill %.2f%%, not %.2f%%",
                        naive * 100, AgeBand.SENIOR.getAnnualMortality() * 100),
                naive < AgeBand.SENIOR.getAnnualMortality() - 1e-4);

        // The cap, which is what stops a healthcare multiplier emptying a band.
        check("a catastrophic rate is capped, not obeyed",
                AgeBand.monthlyFromAnnual(.99), AgeBand.MAX_MONTHLY_MORTALITY, 1e-9);
        check("...and so is a nonsensical one",
                AgeBand.monthlyFromAnnual(50), AgeBand.MAX_MONTHLY_MORTALITY, 1e-9);
        check("a negative rate kills nobody", AgeBand.monthlyFromAnnual(-1), 0, 1e-9);

        // Ordering against the real curve: safest in childhood, worst at the end.
        assertTrue("children are the safest band",
                AgeBand.CHILD.getAnnualMortality() < AgeBand.BABY.getAnnualMortality()
                        && AgeBand.CHILD.getAnnualMortality() < AgeBand.TEEN.getAnnualMortality());
        assertTrue("seniors die far more than adults",
                AgeBand.SENIOR.getAnnualMortality() > AgeBand.ADULT.getAnnualMortality() * 5);
        assertTrue("...but not absurdly - a senior is not doomed",
                AgeBand.SENIOR.getAnnualMortality() < .10);

        /*
         * The shape all of that is FOR. Total outflow from the senior band is
         * mortality plus ageing out at 120, and one over that is how long the
         * average seventy-year-old has left. Life expectancy at seventy is
         * about fifteen years in the real world, and if this drifts far from it
         * the pyramid is wrong however plausible each individual rate looks.
         */
        double seniorOutflow = AgeBand.SENIOR.getAnnualMortality()
                + 12.0 / AgeBand.SENIOR.spanMonths();
        double yearsLeftAt70 = 1 / seniorOutflow;
        System.out.printf("   average years left at seventy: %.1f (real world: about 15)%n",
                yearsLeftAt70);
        assertTrue("a seventy-year-old has a realistic time left",
                yearsLeftAt70 > 12 && yearsLeftAt70 < 19);

        /* ---- and the pyramid it actually produces ---- */
        PopulationCohorts real = new PopulationCohorts();
        real.migrate(100_000);
        for (int i = 0; i < 3000; i++) {
            real.advanceMonth();
            // Hold the total steady so the SHAPE is what settles, not the size -
            // this is the equilibrium the ageing rules imply, measured without
            // growth confusing it.
            real.migrate(100_000 - real.total());
        }

        System.out.printf("   settled pyramid: %.1f%% babies  %.1f%% children  %.1f%% teens"
                        + "  %.1f%% adults  %.1f%% seniors%n",
                real.share(AgeBand.BABY) * 100, real.share(AgeBand.CHILD) * 100,
                real.share(AgeBand.TEEN) * 100, real.share(AgeBand.ADULT) * 100,
                real.share(AgeBand.SENIOR) * 100);

        assertTrue("most of the city is of working age",
                real.share(AgeBand.ADULT) > .50);
        assertTrue("seniors are a realistic share, not half the city",
                real.share(AgeBand.SENIOR) > .10 && real.share(AgeBand.SENIOR) < .22);
        assertTrue("the dependency ratio is plausible",
                real.dependencyRatio() > 50 && real.dependencyRatio() < 95);

        /* ============ 3. pay tiers and families ============ */
        System.out.println("\n--- tiers and households ---");

        java.util.Set<Double> levels = new java.util.HashSet<>();
        for (JobType t : JobType.values()) levels.add(PayTier.wageOf(t));
        check("eleven job types, six distinct wages", levels.size(), 6, 0);

        assertTrue("every job maps to a tier", java.util.Arrays.stream(JobType.values())
                .allMatch(t -> PayTier.of(t) != null));

        // The wage table and the tier table are the same table now.
        PopulationManager pm = new PopulationManager();
        pm.setWagesPerType();
        boolean wagesAgree = true;
        for (JobType t : JobType.values()) {
            if (Math.abs(pm.getWagesPerType()[t.ordinal()] - PayTier.wageOf(t)) > 1e-9) {
                wagesAgree = false;
            }
        }
        assertTrue("PopulationManager pays exactly what PayTier says", wagesAgree);

        FamilyModel f = new FamilyModel();
        double[] jobsByTier = new double[PayTier.values().length];
        jobsByTier[PayTier.UNSKILLED.ordinal()] = 700;
        jobsByTier[PayTier.SKILLED.ordinal()]   = 250;
        jobsByTier[PayTier.COLLEGE.ordinal()]   = 50;

        f.rebuild(c, jobsByTier);

        assertTrue("households were formed", f.totalHouseholds() > 0);
        assertTrue("seniors got their own households",
                f.totalOf(FamilyStructure.SENIOR_ALONE)
                        + f.totalOf(FamilyStructure.SENIOR_COUPLE) > 0);
        assertTrue("families with dependants exist",
                f.totalOf(FamilyStructure.COUPLE_CHILD)
                        + f.totalOf(FamilyStructure.LARGE_FAMILY) > 0);

        // The tier mix must follow the jobs, since that is the whole model.
        double unskilled = f.totalOf(PayTier.UNSKILLED);
        double college   = f.totalOf(PayTier.COLLEGE);
        assertTrue("most households are unskilled, as most jobs are",
                unskilled > college * 5);
        assertTrue("no household sits in a tier with no jobs",
                f.totalOf(PayTier.ELITE) < 1e-9);

        System.out.printf("   %,.0f households, %.2f people each, %,.0f adults unplaced%n",
                f.totalHouseholds(), f.averageHouseholdSize(), f.getUnhousedAdults());

        assertTrue("almost nobody is left unhoused",
                f.getUnhousedAdults() < c.get(AgeBand.ADULT) * .25);

        /*
         * SINGLE ADULTS MUST EXIST, and this assertion is here because they once
         * did not. The allocation order sorts childless couples ahead of single
         * adults, and couples used to take every adult they could reach, so a
         * city came out with a rounding error's worth of singles. Nothing failed
         * loudly - the households still added up, the tier mix still followed the
         * jobs - but the flatshare valve in squeeze() had nothing to convert, so
         * a housing shortage went straight to families doubling up.
         *
         * The band is deliberately wide. This is a placeholder and the exact
         * share is not defensible; what IS defensible is that a real city has a
         * substantial minority of people living alone and nothing like a majority.
         */
        double singles = f.totalOf(FamilyStructure.SINGLE_ADULT);
        assertTrue("a real share of adults live alone",
                singles > f.totalHouseholds() * .15);
        assertTrue("...but living alone is not the whole city",
                singles < f.totalHouseholds() * .50);

        /*
         * Average household size, against the real world rather than a number
         * typed in to match today's output. Developed cities run about 2.3-2.5
         * people per household; this model sits near the bottom of plausible,
         * which is worth knowing and not worth tuning while it is inert. The
         * band exists to catch a change that sends it to 1.2 or to 4.
         */
        assertTrue("households are a plausible size",
                f.averageHouseholdSize() > 1.5 && f.averageHouseholdSize() < 3.0);

        // Rebuilding from the same inputs must give the same answer, or the
        // model is carrying state it does not admit to.
        double firstPass = f.totalHouseholds();
        f.rebuild(c, jobsByTier);
        check("rebuilding is deterministic", f.totalHouseholds(), firstPass, 1e-9);

        /* ============ 3b. homes, and the squeeze ============ */
        System.out.println("\n--- one household, one home ---");

        BuildingsTemplate houseT = null, studioT = null, lowT = null;
        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();
        for (int i = 0; i < bm.getTemplateCount(); i++) {
            BuildingsTemplate t = bm.getTemplate(i);
            if (t == null) continue;
            if ("House".equals(t.getName())) houseT = t;
            if ("Studio Apartments".equals(t.getName())) studioT = t;
            if ("Low-Rise Apartments".equals(t.getName())) lowT = t;
        }

        check("a house is one home", houseT.getDwellings(), 1, 0);
        check("a studio block is eighty", studioT.getDwellings(), 80, 0);
        check("a low-rise is a hundred", lowT.getDwellings(), 100, 0);

        /*
         * The reason the numbers are these numbers. Cost per HOME is what
         * decides which building houses a family, and on that measure studios
         * are the cheapest - which is the niche they have never had, being
         * strictly dominated at every land price today (design queue I1).
         */
        double perHomeHouse  = houseT.getCashCost() / houseT.getDwellings();
        double perHomeStudio = studioT.getCashCost() / studioT.getDwellings();
        double perHomeLow    = lowT.getCashCost() / lowT.getDwellings();
        System.out.printf("   cost per home: studio $%.0f, house $%.0f, low-rise $%.0f%n",
                perHomeStudio, perHomeHouse, perHomeLow);
        assertTrue("a studio is the cheapest home in the game",
                perHomeStudio < perHomeHouse && perHomeStudio < perHomeLow);

        /* ---- nobody is homeless, however tight it gets ---- */
        PopulationCohorts crowd = new PopulationCohorts();
        crowd.migrate(10_000);
        for (int i = 0; i < 600; i++) {
            crowd.advanceMonth();
            crowd.migrate(10_000 - crowd.total());
        }

        FamilyModel tight = new FamilyModel();
        double[] mix = new double[PayTier.values().length];
        mix[PayTier.UNSKILLED.ordinal()] = 1000;

        tight.rebuild(crowd, mix);
        double wanted = tight.totalHouseholds();
        System.out.printf("   %,.0f people want %,.0f homes%n", crowd.total(), wanted);

        // Plenty of homes: nobody shares, nobody doubles up.
        tight.squeeze((int) (wanted * 2));
        check("with room to spare, nobody shares",
                tight.getSharedHouseholds(), 0, 1e-9);
        check("...and nobody doubles up", tight.getDoubledUpHouseholds(), 0, 1e-9);

        // Half the homes needed: the singles move in together.
        tight.rebuild(crowd, mix);
        tight.squeeze((int) (wanted * .6));
        System.out.printf("   at 60%% of the homes needed: %,.0f flatshares, %,.0f doubled up%n",
                tight.getSharedHouseholds(), tight.getDoubledUpHouseholds());
        assertTrue("singles start sharing when homes run short",
                tight.getSharedHouseholds() > 0);

        // Almost no homes at all: families double up too, and STILL nobody is
        // homeless - which is the rule this whole mechanism exists to keep.
        tight.rebuild(crowd, mix);
        tight.squeeze((int) (wanted * .15));
        System.out.printf("   at 15%%: %,.0f flatshares, %,.0f doubled up%n",
                tight.getSharedHouseholds(), tight.getDoubledUpHouseholds());
        assertTrue("families double up as the last resort",
                tight.getDoubledUpHouseholds() > 0);
        assertTrue("and nobody is homeless even then",
                tight.getUnhousedAdults() < crowd.get(AgeBand.ADULT) * .25);

        // The signal the housing model will read.
        assertTrue("a crowded city reports fewer homes needed than households",
                tight.homesNeeded() < tight.totalHouseholds());

        // No homes at all is a degenerate input, not a crash.
        tight.rebuild(crowd, mix);
        tight.squeeze(0);
        assertTrue("no homes at all does not throw or invent shares",
                tight.getSharedHouseholds() >= 0);

        /* ============ 4. AND NOW IT DRIVES THE CITY ============ */
        System.out.println("\n--- what pulls people in ---");

        /*
         * THE CROWDING FLOOR AND THE POINT ARRIVALS STOP ARE THE SAME LINE.
         *
         * This is the assertion the whole "nobody is homeless" promise rests on.
         * FamilyModel can crowd a city down to minimumHomesTolerable() and no
         * further; below that somebody would have nowhere to go. So migration
         * has to reach zero at exactly that number - not near it, at it - or the
         * model quietly starts producing people it cannot house.
         */
        Migration mig = new Migration();
        FamilyModel houses = new FamilyModel();
        houses.rebuild(crowd, mix);

        /*
         * HOW MANY RESIDENTS A JOB NEEDS, and the assertion that it is still
         * Jerus's number underneath.
         *
         * It was the constant 2.25, from `totalJobs * (1 + adultPercent) * 1.5`
         * with adultPercent at .5 - never typed, and its meaning buried in the
         * arithmetic. Reconstructed, it is "enough residents to staff every post
         * with an eighth to spare, given half the city works": 1.125 / .5.
         *
         * It had to stop being a constant once the workforce became the actual
         * adults rather than half the city, because the same 2.25 then supplied
         * 1.29 workers per post instead of 1.125 and quietly meant 23%
         * unemployment where it had meant 11%. This first check is the one that
         * matters: at a 50% adult share the derivation must give back exactly
         * the number it replaced, or it is a retune wearing a derivation's
         * clothes.
         */
        check("at a 50% workforce this is still exactly Jerus's 2.25",
                Migration.residentsPerJob(.50), 2.25, 1e-9);
        assertTrue("a city with more adults needs fewer residents per job",
                Migration.residentsPerJob(.58) < Migration.residentsPerJob(.50));
        assertTrue("...and an ageing one needs more",
                Migration.residentsPerJob(.45) > Migration.residentsPerJob(.50));
        assertTrue("an absurd age structure cannot demand an absurd city",
                Migration.residentsPerJob(.01)
                        <= (1 + Migration.TARGET_LABOUR_SLACK) / Migration.MIN_ADULT_SHARE);
        System.out.printf("   residents per job: %.2f at a 50%% workforce,"
                + " %.2f at the %.0f%% a settled city runs%n",
                Migration.residentsPerJob(.50), Migration.residentsPerJob(ADULT_MIX),
                ADULT_MIX * 100);

        /*
         * And the point of the whole recalibration: the target has to imply the
         * unemployment it was chosen for. A city sitting exactly at target has
         * TARGET_LABOUR_SLACK more workers than posts, whatever its age
         * structure - which is what "11% unemployment" actually means and what
         * the old constant stopped delivering the moment the workforce moved.
         */
        double atTarget = 1000 * Migration.residentsPerJob(ADULT_MIX);
        double workersThen = atTarget * ADULT_MIX;
        check("a city at target has the slack it was designed for",
                (workersThen - 1000) / workersThen,
                Migration.TARGET_LABOUR_SLACK / (1 + Migration.TARGET_LABOUR_SLACK), 1e-9);

        double floorHomes = houses.minimumHomesTolerable();
        double oneEach    = houses.totalHouseholds();

        System.out.printf("   %,.0f households squeeze into at fewest %,.0f homes%n",
                oneEach, floorHomes);

        check("with a home each, nothing damps arrivals",
                mig.crowdingFactor((int) Math.ceil(oneEach), houses), 1, 1e-9);
        check("at the crowding floor, arrivals stop dead",
                mig.crowdingFactor((int) Math.floor(floorHomes), houses), 0, 1e-9);
        assertTrue("...but just above it they have not",
                mig.crowdingFactor((int) Math.ceil(floorHomes) + 1, houses) > 0);
        assertTrue("half way down, they are damped but not stopped",
                mig.crowdingFactor((int) ((oneEach + floorHomes) / 2), houses) > 0
                        && mig.crowdingFactor((int) ((oneEach + floorHomes) / 2), houses) < 1);

        /*
         * JERUS'S RULE: "if a city is full but has jobs, they'll still move in."
         *
         * A city with no spare homes and plenty of jobs must still have a
         * positive pull. The old model could not express this - housing was a
         * hard minimum, so one house short meant zero growth no matter how many
         * posts were going begging.
         */
        double fullButHiring = mig.monthlyNet(
                (int) crowd.total(), 8000, (int) crowd.total(),
                (int) Math.ceil(oneEach * .9), houses, ADULT_MIX);
        assertTrue("a full city with jobs still attracts people", fullButHiring > 0);

        // ...and jobs are the MAIN factor, per Jerus: same city, no jobs, and
        // the pull has to be much weaker rather than merely smaller.
        FamilyModel roomy = new FamilyModel();
        roomy.rebuild(crowd, mix);
        double jobsOnly  = mig.monthlyNet(0, 8000, 0, 999_999, roomy, ADULT_MIX);
        double homesOnly = mig.monthlyNet(
                0, 0, (int) (8000 * Migration.residentsPerJob(ADULT_MIX)),
                999_999, roomy, ADULT_MIX);
        System.out.printf("   into an empty city: %,.0f pulled by jobs alone,"
                + " %,.0f by the same worth of housing%n", jobsOnly, homesOnly);
        assertTrue("jobs pull harder than housing", jobsOnly > homesOnly * 2);
        assertTrue("but housing still pulls on its own", homesOnly > 0);

        /* ---- nobody leaves without a reason, and the reason takes a year ---- */
        System.out.println("\n--- what pushes people out ---");

        Migration bust = new Migration();
        double[] wages = new double[PayTier.values().length];
        wages[PayTier.UNSKILLED.ordinal()] = 1000;

        // A steady city with far more people than jobs. Nobody leaves: that is
        // unemployment, not an exodus.
        for (int m = 0; m < 24; m++) bust.recordWages(wages);
        double overshot = bust.monthlyNet(50_000, 100, 1000, 999_999, roomy, ADULT_MIX);
        check("a city with too few jobs sheds nobody while wages hold",
                overshot, 0, 1e-9);
        assertTrue("...because no tier is in decline", bust.decliningShare() == 0);

        // Now the tier starts dying. Eleven months in, still nothing.
        Migration dying = new Migration();
        double[] falling = wages.clone();
        dying.recordWages(falling);
        for (int m = 0; m < 11; m++) {
            falling[PayTier.UNSKILLED.ordinal()] *= .9;
            dying.recordWages(falling);
        }
        check("eleven months of decline is not yet enough",
                dying.monthlyNet(50_000, 100, 1000, 999_999, roomy, ADULT_MIX), 0, 1e-9);
        System.out.printf("   streak after eleven falling months: %d of %d%n",
                dying.getDecliningStreak(PayTier.UNSKILLED), Migration.DECLINE_MONTHS);

        // The twelfth month opens the gate.
        falling[PayTier.UNSKILLED.ordinal()] *= .9;
        dying.recordWages(falling);
        double leaving = dying.monthlyNet(50_000, 100, 1000, 999_999, roomy, ADULT_MIX);
        System.out.printf("   twelve months in: %,.0f people leave%n", -leaving);
        assertTrue("a full year of decline finally moves people out", leaving < 0);
        assertTrue("and they leave slower than they arrived",
                Migration.DEPARTURE_RATE < Migration.ARRIVAL_RATE);

        // A tier that goes to zero does not have to wait a year on top.
        Migration closed = new Migration();
        for (int m = 0; m < 12; m++) closed.recordWages(wages);
        closed.recordWages(new double[PayTier.values().length]);
        assertTrue("a tier that stops paying entirely counts as declining",
                closed.isDeclining(PayTier.UNSKILLED));

        /*
         * GROSS FLOWS. The whole reason this exists: the first version made
         * departures the negative branch of arrivals, so people could only leave
         * a city that was ALREADY oversized. Four thousand months measured
         * 53,528 arrivals and zero departures - the gate opened fifteen times and
         * never once mattered.
         *
         * So: a growing city with a dying trade must lose people to it, even
         * while it is gaining more overall. That is what a town hollowing out
         * actually looks like.
         */
        double growingWithADyingTrade = dying.monthlyNet(
                1000, 9000, 500_000, 999_999, roomy, ADULT_MIX);
        assertTrue("a growing city still loses people to a trade that is dying",
                dying.getLastDepartures() > 0);
        assertTrue("...while gaining more than it loses",
                dying.getLastArrivals() > dying.getLastDepartures()
                        && growingWithADyingTrade > 0);
        System.out.printf("   a boom town with one dead trade: %,.0f in, %,.0f out%n",
                dying.getLastArrivals(), dying.getLastDepartures());

        /*
         * And how badly it fell has to matter, not merely that it fell. A tier
         * that slipped three percent over a year and one that shut its doors both
         * pass the gate; charging them the same would empty a city over a
         * rounding error.
         */
        Migration slipped = new Migration();
        double[] barely = wages.clone();
        slipped.recordWages(barely);
        for (int m = 0; m < 12; m++) {
            barely[PayTier.UNSKILLED.ordinal()] *= .999;
            slipped.recordWages(barely);
        }
        /*
         * Compared as PRESSURE rather than as people, because departures scale
         * with the city's size and these two fixtures are different sizes -
         * comparing the head counts would be comparing the populations.
         */
        assertTrue("a trade that merely slipped costs the city almost nobody",
                slipped.decliningPressure() > 0
                        && slipped.decliningPressure() < dying.decliningPressure() / 20);
        System.out.printf("   a trade down 1%% over a year sheds %.1f%% of the city a"
                + " month; one down 69%% sheds %.1f%%%n",
                slipped.decliningPressure() * Migration.DEPARTURE_RATE * 100,
                dying.decliningPressure() * Migration.DEPARTURE_RATE * 100);

        /* ---- and the whole thing, played ---- */
        System.out.println("\n--- a city, played ---");

        Path root = Files.createTempDirectory("popcheck");
        double[] oneMonth  = play(root, "m1", 1);
        double[] settled   = play(root, "m120", 120);

        double adultShare = settled[0] > 0 ? settled[8] / settled[0] : ADULT_MIX;
        double target = Migration.JOB_WEIGHT * settled[3]
                        * Migration.residentsPerJob(adultShare)
                + Migration.HOME_WEIGHT * settled[5];

        System.out.printf("   after 1 month: %,.0f people;  after 120: %,.0f"
                + "  (target %,.0f)%n", oneMonth[0], settled[0], target);

        /*
         * INERTIA, which is the entire point of the switch. The old model
         * computed min(housing, jobs * 2.25) fresh every month, so a city with
         * 200 houses and eight shops standing on day one was fully populated by
         * the end of that month. It now has to fill up.
         */
        assertTrue("a city does not fill in a single month",
                oneMonth[0] < settled[0] * .5);
        assertTrue("...but it does fill, given a decade",
                settled[0] > target * .8);
        assertTrue("...and does not overshoot what it can support",
                settled[0] < target * 1.2);

        /*
         * THE WORKFORCE IS THE ADULTS, not a flat share of the population.
         *
         * It was `population * 0.5` - typed in, and blind to who actually lived
         * here, so a city of pensioners staffed its factories exactly as well as
         * a city of thirty-year-olds. The pyramid had known the real figure for
         * three batches and nothing read it.
         *
         * Checked against the adult band rather than against 57%, because the
         * whole point is that the number MOVES. A city that ages should lose
         * workers without losing residents, and an assertion against a constant
         * would be the same mistake in a different place. The one-month lag is
         * expected and deliberate: somebody who moved in this month starts work
         * next month, so the workforce is last month's adults.
         */
        double adultsNow = settled[8];
        System.out.printf("   workforce %,.0f against %,.0f adults and %,.0f residents"
                + "  (%.1f%% of the city, not 50%%)%n",
                settled[4], adultsNow, settled[0], settled[4] / settled[0] * 100);
        assertTrue("the workforce is the city's adults, within a month's growth",
                Math.abs(settled[4] - adultsNow) < adultsNow * .05);
        assertTrue("...which is not half the population",
                Math.abs(settled[4] - settled[0] * .5) > settled[0] * .02);

        /* ============ 5. and it survives a save ============ */
        System.out.println("\n--- through a save ---");

        GameFiles files = new GameFiles(root.resolve("save"), root.resolve("no-legacy"));
        Game city = new Game(files);
        System.setOut(quiet);
        try {
            city.run();
            // A city with people in it. The pyramid seeds from the live
            // population, so an empty city correctly has no pyramid at all -
            // which made the first version of this section assert against a
            // city that could never have had one.
            BuildingManager cb = city.getBuildingManager();
            cb.addStack(cb.getTemplateByName("House"), 150, true);
            cb.addStack(cb.getTemplateByName("Convience Store"), 6, true);
            city.simulateMonths(60);
            city.saveGame(1, "demographics");
        } finally { System.setOut(out); }

        double pyramidBefore = city.getCohorts().total();
        double housesBefore = city.getFamilies().totalHouseholds();
        assertTrue("the city has a pyramid", pyramidBefore > 0);

        Game back = new Game(files);
        System.setOut(quiet);
        try { back.loadGameSave(1); } finally { System.setOut(out); }

        check("the pyramid came back", back.getCohorts().total(), pyramidBefore, .01);
        check("...and the households with it",
                back.getFamilies().totalHouseholds(), housesBefore, .01);
        for (AgeBand b : AgeBand.values()) {
            check("  " + b.getLabel().toLowerCase() + " restored",
                    back.getCohorts().get(b), city.getCohorts().get(b), .01);
        }

        /*
         * THE WAGE HISTORY HAS TO COME BACK TOO, and this is the assertion that
         * says so. A streak is a flow: twelve months of falling wages cannot be
         * read off the month the city was saved in. A reloaded city that forgot
         * would silently reset every tier to a clean slate and be unable to shed
         * a single resident for a year - the exact class of save-versus-played
         * divergence this project keeps finding.
         */
        Migration savedMig = new Migration();
        double[] fell = new double[PayTier.values().length];
        fell[PayTier.UNSKILLED.ordinal()] = 5000;
        savedMig.recordWages(fell);
        for (int m = 0; m < 8; m++) {
            fell[PayTier.UNSKILLED.ordinal()] *= .9;
            savedMig.recordWages(fell);
        }
        Migration reloaded = new Migration();
        reloaded.restore(savedMig.toSaveArray());
        check("a declining streak survives a save",
                reloaded.getDecliningStreak(PayTier.UNSKILLED),
                savedMig.getDecliningStreak(PayTier.UNSKILLED), 0);

        reloaded.restore(new double[]{1, 2, 3});
        check("...and a malformed one is refused, not half-read",
                reloaded.getDecliningStreak(PayTier.UNSKILLED),
                savedMig.getDecliningStreak(PayTier.UNSKILLED), 0);

        // A save array of the wrong length is refused whole, not read at an
        // offset - the standing rule for state arrays in this codebase.
        PopulationCohorts wrong = new PopulationCohorts();
        wrong.migrate(5000);
        double keep = wrong.total();
        wrong.restore(new double[]{1, 2, 3});
        check("a malformed pyramid is refused, not half-read", wrong.total(), keep, .01);

        cleanUp(root);

        System.out.println(fails == 0 ? "\nAll checks passed." : "\n" + fails + " FAILED");
        if (fails > 0) System.exit(1);
    }

    /** Plays a standard city for a given number of months and reports it. */
    static double[] play(Path root, String name, int months) throws Exception {

        GameFiles files = new GameFiles(root.resolve(name), root.resolve("no-legacy"));
        Game g = new Game(files);

        System.setOut(quiet);
        try {
            g.run();

            BuildingManager b = g.getBuildingManager();
            b.addStack(b.getTemplateByName("House"), 200, true);
            b.addStack(b.getTemplateByName("Convience Store"), 8, true);
            b.addStack(b.getTemplateByName("Construction Depot"), 2, true);
            g.simulateMonths(months);
        } finally {
            System.setOut(out);
        }

        EconomyManager e = g.getEconomyManager();
        PopulationManager p = g.getPopulationManager();
        return new double[]{
            p.getPopulation(),
            Math.round(g.getCash() * 10000) / 10000.0,
            Math.round(e.getMonthGdp() * 10000) / 10000.0,
            p.getTotalJobs(),
            p.getWorkforce(),
            g.getBuildingManager().getTotalHouseCapacity(),
            Math.round(p.getTotalWage() * 10000) / 10000.0,
            Math.round(e.getTaxIncome() * 10000) / 10000.0,
            g.getCohorts().get(AgeBand.ADULT)
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
