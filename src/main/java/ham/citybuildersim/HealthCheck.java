package ham.citybuildersim;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Sickness: what it moves, and - much more importantly - what it does not.
 *
 * The specification was one sentence: "it modifies the fillrate, but doesnt
 * reduce workforce." Almost every assertion here is a way of saying the second
 * half of that, because the second half is what a plausible implementation gets
 * wrong. Cutting the workforce would have been a smaller change and would have
 * looked identical on the output line - and would have quietly cut the wage
 * bill, the wage tax, the households' income and the rent they can afford, none
 * of which anybody asked for.
 *
 * NUMBERS ARE COMPARED AGAINST THE MODEL'S OWN CONSTANTS, not against literals.
 * That rule has been earned five separate times in this codebase: an assertion
 * pinned to .06 or to 2.25 tests that nobody edited the harness, not that the
 * mechanic works.
 */
public class HealthCheck {

    static int fails = 0;

    static void check(String label, double actual, double expected, double tol) {
        boolean ok = Math.abs(actual - expected) <= tol;
        if (!ok) fails++;
        System.out.printf("%-52s %14.4f  expected %12.4f  %s%n",
                label, actual, expected, ok ? "OK" : "FAIL");
    }

    static void assertTrue(String label, boolean ok) {
        if (!ok) fails++;
        System.out.printf("%-52s %s%n", label, ok ? "OK" : "FAIL");
    }

    public static void main(String[] args) throws Exception {

        java.io.PrintStream out = System.out;
        java.io.PrintStream quiet = new java.io.PrintStream(java.io.OutputStream.nullOutputStream());
        Path root = Files.createTempDirectory("healthcheck");

        /* ================= 1. the buildings know what they treat ================= */
        System.out.println("--- care types ---");

        BuildingManager bm = new BuildingManager();
        bm.initializeTemplates();

        BuildingsTemplate hospital = bm.getTemplateByName("General Hospital");
        BuildingsTemplate daycare  = bm.getTemplateByName("Home Daycare");
        BuildingsTemplate nursing  = bm.getTemplateByName("Nursing Home");
        BuildingsTemplate house    = bm.getTemplateByName("House");

        assertTrue("a hospital is general care", hospital.getCare() == CareType.GENERAL);
        assertTrue("a daycare is childcare",     daycare.getCare() == CareType.CHILDCARE);
        assertTrue("a nursing home is senior care", nursing.getCare() == CareType.SENIOR);
        assertTrue("a house treats nobody",      house.getCare() == CareType.NONE);

        /*
         * The whole reason the field exists rather than a string match: two
         * buildings that share a word in their names and nothing else.
         */
        assertTrue("\"Home Daycare\" and \"Nursing Home\" are not the same care",
                daycare.getCare() != nursing.getCare());

        // An aggregate assertion cannot see a missing category, so count the
        // buildings of each type rather than checking that some exist.
        int[] byCare = new int[CareType.values().length];
        for (BuildingsTemplate t : bm.getTemplates()) byCare[t.getCare().ordinal()]++;
        for (CareType care : CareType.values()) {
            if (care == CareType.NONE) continue;
            assertTrue("  something in the catalogue does " + care.getLabel().toLowerCase()
                    + " (" + byCare[care.ordinal()] + ")", byCare[care.ordinal()] > 0);
        }

        /* ---- capacity is counted by care type, and only when finished ---- */
        System.out.println("\n--- care capacity ---");

        /*
         * A CITY THAT HAS BUILT NOTHING IS NOT A CITY WITH NOTHING. It is
         * founded with a doctor, a nursery, an almshouse and a churchyard, the
         * same way it is founded with 100 units of housing and 400 of road
         * capacity - so that a new game opens on a decision rather than on a
         * crisis. Compared against the endowment rather than against zero.
         */
        check("a new city has the doctor it was founded with",
                bm.getCareCapacity(CareType.GENERAL),
                Healthcare.foundingCapacity(CareType.GENERAL), 1e-9);
        check("...and a churchyard",
                bm.getCareCapacity(CareType.BURIAL),
                Healthcare.foundingCapacity(CareType.BURIAL), 1e-9);
        check("...but nobody founds a city with a crematorium",
                bm.getCareCapacity(CareType.CREMATION), 0, 1e-9);
        assertTrue("the endowment is sized off the pyramid's own shares",
                Healthcare.foundingCapacity(CareType.CHILDCARE)
                        < Healthcare.foundingCapacity(CareType.GENERAL)
                && Healthcare.foundingCapacity(CareType.SENIOR)
                        < Healthcare.foundingCapacity(CareType.CHILDCARE));
        System.out.printf("  founded for %,d people: %,.0f general, %,.0f childcare,"
                + " %,.0f senior, %,.0f plots%n",
                Healthcare.FOUNDING_CITY,
                Healthcare.foundingCapacity(CareType.GENERAL),
                Healthcare.foundingCapacity(CareType.CHILDCARE),
                Healthcare.foundingCapacity(CareType.SENIOR),
                Healthcare.foundingCapacity(CareType.BURIAL));

        double foundedGeneral = bm.getCareCapacity(CareType.GENERAL);
        double foundedChild   = bm.getCareCapacity(CareType.CHILDCARE);

        bm.addStack(hospital, 2, true);
        bm.addStack(daycare, 3, true);
        check("two hospitals, on top of it",
                bm.getCareCapacity(CareType.GENERAL),
                foundedGeneral + 2 * hospital.getCapacity(), 1e-9);
        check("...and the daycares are not counted with them",
                bm.getCareCapacity(CareType.CHILDCARE),
                foundedChild + 3 * daycare.getCapacity(), 1e-9);

        double finished = bm.getCareCapacity(CareType.GENERAL);
        bm.addStack(hospital, 1, false);          // ordered, not built
        check("a hospital under construction treats nobody",
                bm.getCareCapacity(CareType.GENERAL), finished, 1e-9);

        /* ================= 2. coverage sets the baseline ================= */
        System.out.println("\n--- coverage and the baseline rate ---");

        Health none = new Health();
        none.advanceMonth(0, 10000, 1);
        check("no beds at all", none.getBaselineRate(), Health.UNTREATED_RATE, 1e-9);

        Health full = new Health();
        full.advanceMonth(10000, 10000, 1);
        check("beds for everybody", full.getBaselineRate(), Health.WELL_SERVED_RATE, 1e-9);

        Health half = new Health();
        half.advanceMonth(5000, 10000, 1);
        check("half covered is halfway between",
                half.getBaselineRate(),
                (Health.UNTREATED_RATE + Health.WELL_SERVED_RATE) / 2, 1e-9);

        Health over = new Health();
        over.advanceMonth(90000, 10000, 1);
        check("surplus beds do not go below the floor",
                over.getBaselineRate(), Health.WELL_SERVED_RATE, 1e-9);

        // An empty city is well, not in crisis - the zero-denominator guard.
        Health empty = new Health();
        empty.advanceMonth(0, 0, 1);
        check("a city with nobody in it is fully covered", empty.getCoverage(), 1, 1e-9);
        check("...and is not mid-plague", empty.getSickRate(), Health.WELL_SERVED_RATE, 1e-9);

        /* ================= 3. outbreaks actually happen ================= */
        /*
         * A mechanic that never fires looks exactly like one that does not
         * exist, and this project has shipped one of those before - out-migration
         * fired zero times in four thousand months. So: count them.
         */
        System.out.println("\n--- outbreaks, over three hundred years ---");

        Health rolled = new Health();
        int months = 3600, outbreaks = 0, monthsIll = 0;
        double worst = 0;
        boolean wasIll = false;
        for (int m = 1; m <= months; m++) {
            rolled.advanceMonth(10000, 10000, m);       // fully covered
            boolean ill = rolled.isOutbreak();
            if (ill && !wasIll) outbreaks++;
            if (ill) monthsIll++;
            worst = Math.max(worst, rolled.getSickRate());
            wasIll = ill;
        }
        System.out.printf("  %d outbreaks in %d months, ill in %d of them, worst %.1f%%%n",
                outbreaks, months, monthsIll, worst * 100);

        assertTrue("outbreaks happen at all", outbreaks > 0);
        // Expected count is months * chance; allow a wide band, because the
        // point is that the rate is roughly right, not that this RNG stream is.
        double expected = months * Health.OUTBREAK_CHANCE;
        assertTrue("...about as often as the chance says (" + Math.round(expected) + " ± half)",
                outbreaks > expected * .5 && outbreaks < expected * 1.5);
        assertTrue("...and each one lasts more than a month",
                monthsIll > outbreaks);
        assertTrue("...but none of them is permanent", !rolled.isOutbreak() || monthsIll < months);
        assertTrue("no month ever loses more than the cap",
                worst <= Health.MAX_SICK_RATE + 1e-9);

        /* ---- an outbreak decays to nothing on its own ---- */
        System.out.println("\n--- an outbreak ends ---");

        Health sick = new Health();
        int began = -1;
        for (int m = 1; m <= months && began < 0; m++) {
            sick.advanceMonth(0, 10000, m);
            if (sick.isOutbreak()) began = m;
        }
        assertTrue("found an outbreak to follow", began > 0);

        double peak = sick.getOutbreakSeverity();
        double previous = peak;
        int length = 1;
        for (int m = began + 1; sick.isOutbreak() && m < began + 60; m++) {
            sick.advanceMonth(0, 10000, m);
            if (sick.isOutbreak()) {
                if (sick.getOutbreakSeverity() >= previous) {
                    fails++;
                    System.out.println("  FAIL severity did not fall in month " + m);
                }
                previous = sick.getOutbreakSeverity();
                length++;
            }
        }
        System.out.printf("  peaked at %.1f%% and ran %d months%n", peak * 100, length);
        assertTrue("it ended", !sick.isOutbreak());
        assertTrue("...after more than one month", length > 1);
        check("and the city is back to its baseline",
                sick.getSickRate(), sick.getBaselineRate(), 1e-9);

        /* ---- hospitals blunt an outbreak without preventing it ---- */
        System.out.println("\n--- what coverage buys ---");

        double untreatedPeak = 0, coveredPeak = 0;
        Health bare = new Health(), cared = new Health();
        for (int m = 1; m <= months; m++) {
            bare.advanceMonth(0, 10000, m);
            cared.advanceMonth(10000, 10000, m);
            untreatedPeak = Math.max(untreatedPeak, bare.getOutbreakSeverity());
            coveredPeak = Math.max(coveredPeak, cared.getOutbreakSeverity());
        }
        System.out.printf("  worst outbreak: %.1f%% untreated, %.1f%% covered%n",
                untreatedPeak * 100, coveredPeak * 100);
        assertTrue("coverage takes the edge off an outbreak", coveredPeak < untreatedPeak);
        assertTrue("...but does not prevent one", coveredPeak > 0);
        check("...by exactly the mitigation it claims",
                coveredPeak, untreatedPeak * (1 - Health.OUTBREAK_MITIGATION), 1e-9);

        /* ================= 4. the same month rolls the same way ================= */
        /*
         * The outbreak is a function of the month number and NOT of a generator
         * whose state has to be saved. That is what makes an epidemic something
         * a player has to live through rather than reload past - the same
         * property the consecutive-loss streaks were carried for.
         */
        System.out.println("\n--- not save-scummable ---");

        Health first = new Health(), second = new Health();
        boolean identical = true;
        for (int m = 1; m <= 600; m++) {
            first.advanceMonth(4000, 10000, m);
            second.advanceMonth(4000, 10000, m);
            if (Math.abs(first.getSickRate() - second.getSickRate()) > 1e-12) identical = false;
        }
        assertTrue("two cities living the same months get the same illness", identical);

        Health saved = new Health();
        for (int m = 1; m <= 400; m++) saved.advanceMonth(0, 10000, m);
        // Wind forward to a month that IS an outbreak, so the save carries one.
        int at = 401;
        while (!saved.isOutbreak() && at < 4000) saved.advanceMonth(0, 10000, at++);
        assertTrue("saving mid-outbreak", saved.isOutbreak());

        Health restored = new Health();
        assertTrue("the save was accepted", restored.restore(saved.getState()));
        check("the outbreak came back",
                restored.getOutbreakSeverity(), saved.getOutbreakSeverity(), 1e-12);
        check("...and so did the rate the month was throttled by",
                restored.getWorkRatio(), saved.getWorkRatio(), 1e-12);

        Health untouched = new Health();
        untouched.restore(saved.getState());
        double keep = untouched.getSickRate();
        assertTrue("a malformed array is refused", !untouched.restore(new double[]{1, 2, 3}));
        check("...and nothing was half-read", untouched.getSickRate(), keep, 1e-12);
        assertTrue("a save from before sickness is refused too, not read at an offset",
                !untouched.restore(null));

        /* ================= 5. THE POINT: output falls, nobody does ================= */
        System.out.println("\n--- the workforce does not move ---");

        GameFiles files = new GameFiles(root.resolve("save"), root.resolve("no-legacy"));

        Game well = new Game(files);
        System.setOut(quiet);
        try {
            well.run();
            stock(well);
            well.simulateMonths(36);
        } finally { System.setOut(out); }

        /*
         * The comparison city is the same city, told it is ill. Done by setting
         * the ratio directly rather than by building hospitals, because building
         * hospitals changes the jobs, the payroll, the land and the power draw -
         * and then any difference in output proves nothing about sickness.
         *
         * THE BASELINE IS A RECOMPUTE AT FULL HEALTH, not the statement the last
         * month left behind. Two reasons, and the first version of this section
         * fell into both. A recompute reads the CLOSING inventory where the
         * month's statement was written against the opening one - the trap
         * computeMonthlyReport(int) exists for - so the two are not comparable
         * figures at all. And the city has no hospitals, so it was ALREADY ill:
         * measured against its own statement the ratio only moved from .82 to
         * .80, and the assertion caught a 2.4% fall where it expected 20%. That
         * failure was the harness's, and it is also the first hard evidence that
         * sickness is live in a played city rather than only in a unit test.
         */
        EconomyManager wellEcon = well.getEconomyManager();
        PopulationManager wellPop = well.getPopulationManager();

        System.out.printf("  the played city was already running at %.1f%% health%n",
                wellEcon.getHealthRatio() * 100);
        assertTrue("a city with no clinics is already ill", wellEcon.getHealthRatio() < 1);

        wellEcon.setHealthRatio(1);
        wellEcon.getCommercialHandler().computeMonthlyReport();
        wellEcon.getIndustrialHandler().computeMonthlyReport();

        double workforceBefore = wellPop.getWorkforce();
        double populationBefore = wellPop.getPopulation();
        double wageBillBefore = wellPop.getTotalWage();
        double payrollBefore = wellEcon.getCommercialHandler().getReportPayroll();
        double revenueBefore = wellEcon.getCommercialHandler().getGrossRevenue();
        double millRateBefore = wellEcon.getIndustrialHandler().getOperatingRate();
        double buildBefore = well.getConstructionOutput();

        double sickness = .20;
        wellEcon.setHealthRatio(1 - sickness);
        wellEcon.getCommercialHandler().computeMonthlyReport();
        wellEcon.getIndustrialHandler().computeMonthlyReport();

        System.out.printf("  a city of %.0f with %.0f working, told %.0f%% of them are ill%n",
                populationBefore, workforceBefore, sickness * 100);

        check("the workforce is unchanged", wellPop.getWorkforce(), workforceBefore, 1e-9);
        check("the population is unchanged", wellPop.getPopulation(), populationBefore, 1e-9);
        check("the wage bill is unchanged", wellPop.getTotalWage(), wageBillBefore, 1e-9);
        check("the employer still pays the full payroll",
                wellEcon.getCommercialHandler().getReportPayroll(), payrollBefore, 1e-9);

        check("...and the shops' revenue falls by exactly the sick rate",
                wellEcon.getCommercialHandler().getGrossRevenue(),
                revenueBefore * (1 - sickness), Math.max(1e-9, revenueBefore * 1e-9));
        check("the mills run slower by the same share",
                wellEcon.getIndustrialHandler().getOperatingRate(),
                millRateBefore * (1 - sickness), 1e-9);

        /*
         * CONSTRUCTION READS THE GAME'S OWN Health, not the economy's ratio, so
         * setHealthRatio above cannot move it and buildBefore is unchanged. That
         * is not a gap - it is the same figure reaching the sites by the shorter
         * route - but it does mean this needs its own comparison.
         *
         * Two build rates off the same city, one ill and one covered, compared
         * as a proportion. Deliberately NOT restated as capacity x fill x road x
         * health: a harness that repeats the formula passes whatever the formula
         * says, which is how a duplicated calculation gets blessed instead of
         * caught.
         */
        check("the sites were not moved by the economy's ratio",
                well.getConstructionOutput(), buildBefore, 1e-9);

        double illRatio = well.getHealth().getWorkRatio();
        double illBuild = well.getConstructionOutput();

        well.getHealth().advanceMonth(Double.MAX_VALUE / 4, well.getCohorts().total(), 7);
        double wellRatio = well.getHealth().getWorkRatio();
        double wellBuild = well.getConstructionOutput();

        System.out.printf("  build rate %.0f at %.1f%% health, %.0f at %.1f%%%n",
                illBuild, illRatio * 100, wellBuild, wellRatio * 100);
        assertTrue("beds for everybody speeds the sites up", wellBuild > illBuild);
        // Compared as whole points with a tolerance of one, not as a ratio of
        // two rounded integers - getConstructionOutput() rounds, and on a build
        // rate in the twenties that rounding is worth two points of ratio.
        check("...by exactly the difference in the sick rate",
                illBuild, Math.round(wellBuild * (illRatio / wellRatio)), 1);

        /* ---- and the whole thing, played, through a save ---- */
        System.out.println("\n--- a city, played and reloaded ---");

        Game city = new Game(files);
        System.setOut(quiet);
        try {
            city.run();
            stock(city);
            BuildingManager cb = city.getBuildingManager();
            cb.addStack(cb.getTemplateByName("Walk-in Clinic"), 4, true);
            city.simulateMonths(120);
            city.saveGame(1, "health");
        } finally { System.setOut(out); }

        Health lived = city.getHealth();
        System.out.printf("  coverage %.1f%%, sick %.2f%%, outbreak %s%n",
                lived.getCoverage() * 100, lived.getSickRate() * 100,
                lived.isOutbreak() ? "yes" : "no");

        assertTrue("clinics gave the city some coverage", lived.getCoverage() > 0);
        assertTrue("...so it is healthier than an untreated one",
                lived.getBaselineRate() < Health.UNTREATED_RATE);
        assertTrue("...but not perfectly healthy",
                lived.getSickRate() > 0);

        Game back = new Game(files);
        System.setOut(quiet);
        try { back.loadGameSave(1); } finally { System.setOut(out); }

        check("the sick rate came back", back.getHealth().getSickRate(),
                lived.getSickRate(), 1e-9);
        check("...and the outbreak with it", back.getHealth().getOutbreakSeverity(),
                lived.getOutbreakSeverity(), 1e-9);
        check("...and the coverage the month was priced at",
                back.getHealth().getCoverage(), lived.getCoverage(), 1e-9);

        /*
         * The load path has to hand the sectors the same ratio the live game
         * did. This is the assertion for the class of bug that has bitten this
         * codebase repeatedly - a line that made it into
         * SimulationEngine.updateEconomy and not into rebuildSimulationState().
         */
        check("and the sectors were told about it on the load path",
                back.getEconomyManager().getHealthRatio(),
                city.getEconomyManager().getHealthRatio(), 1e-9);
        check("...including the basis its statements were written against",
                back.getEconomyManager().getHealthRatioBasis(),
                city.getEconomyManager().getHealthRatioBasis(), 1e-9);

        /* ================= 6. an unstaffed hospital treats nobody ================= */
        System.out.println("\n--- staffing ---");

        BuildingManager sm = new BuildingManager();
        sm.initializeTemplates();
        sm.addStack(sm.getTemplateByName("General Hospital"), 1, true);

        double[] fullyStaffed = new double[JobType.values().length];
        java.util.Arrays.fill(fullyStaffed, 1);
        double[] noDoctors = fullyStaffed.clone();
        noDoctors[JobType.UNIV_DOCTOR.ordinal()] = 0;
        double[] nobody = new double[JobType.values().length];

        double endowment = Healthcare.foundingCapacity(CareType.GENERAL);
        double nominal = sm.getCareCapacity(CareType.GENERAL);
        check("fully staffed, a hospital treats its whole capacity",
                sm.getStaffedCareCapacity(CareType.GENERAL, fullyStaffed), nominal, 1e-9);
        /*
         * The endowment survives an empty payroll, and that is deliberate: it is
         * not a building, nobody is employed by it, and there is no fill rate
         * for it to be short of. Everything the PLAYER built goes to zero.
         */
        check("with nobody at all, only the founding doctor is left",
                sm.getStaffedCareCapacity(CareType.GENERAL, nobody), endowment, 1e-9);

        /*
         * The point of doing staffing PER BUILDING off its own job mix: a
         * General Hospital is 45 doctors in 363 posts, so losing every doctor
         * costs it 12.4% of its capacity and not 100% and not 9%. A city-wide
         * average fill rate could not produce that number.
         */
        BuildingsTemplate gh = sm.getTemplateByName("General Hospital");
        double doctorShare = gh.getJobs(JobType.UNIV_DOCTOR) / (double) gh.getTotalJobs();
        check("...and losing only its doctors costs it exactly their share of the posts",
                sm.getStaffedCareCapacity(CareType.GENERAL, noDoctors),
                endowment + (nominal - endowment) * (1 - doctorShare), 1e-9);
        System.out.printf("  %.0f of %d posts are doctors, so an undoctored hospital is at %.1f%%%n",
                (double) gh.getJobs(JobType.UNIV_DOCTOR), gh.getTotalJobs(),
                (1 - doctorShare) * 100);

        /* ================= 7. what care does to mortality ================= */
        System.out.println("\n--- mortality ---");

        /*
         * HALF COVERAGE IS TODAY'S RATE, for every band. That is the property
         * the whole curve is built around, and the reason it is geometric
         * rather than linear: the linear version capped the uncovered end at 2x
         * by construction, so "really really really" was not expressible in it.
         */
        for (AgeBand b : AgeBand.values()) {
            check("half-covered is exactly today's rate: " + b.getLabel().toLowerCase(),
                    Healthcare.mortalityFactor(b, .5, .5, .5), 1, 1e-9);
        }

        check("no childcare at all", Healthcare.mortalityFactor(AgeBand.BABY, 0, .5, 0),
                Healthcare.CHILDCARE_SWING, 1e-9);
        check("childcare for everybody", Healthcare.mortalityFactor(AgeBand.BABY, 1, .5, 0),
                1 / Healthcare.CHILDCARE_SWING, 1e-9);
        check("no general care at all", Healthcare.mortalityFactor(AgeBand.ADULT, 0, 0, 0),
                Healthcare.GENERAL_SWING, 1e-9);
        check("no senior care at all", Healthcare.mortalityFactor(AgeBand.SENIOR, 0, .5, 0),
                Healthcare.SENIOR_SWING, 1e-9);

        // Drastic for children, real for adults, gentle for seniors - Jerus's
        // ordering, asserted as an ordering rather than as three literals.
        System.out.printf("  swings: children %.0fx, adults %.1fx, seniors %.2fx%n",
                Healthcare.CHILDCARE_SWING, Healthcare.GENERAL_SWING,
                Healthcare.SENIOR_SWING);
        assertTrue("children are the drastic ones",
                Healthcare.CHILDCARE_SWING > Healthcare.GENERAL_SWING * 5);
        assertTrue("...adults come next",
                Healthcare.GENERAL_SWING > Healthcare.SENIOR_SWING);
        assertTrue("...and seniors are the gentlest",
                Healthcare.SENIOR_SWING > 1);

        // One care type per band, so the levers stay readable.
        check("general care does not also treat babies",
                Healthcare.mortalityFactor(AgeBand.BABY, .5, 0, .5), 1, 1e-9);
        check("...nor seniors", Healthcare.mortalityFactor(AgeBand.SENIOR, .5, 0, .5), 1, 1e-9);
        check("but it does treat adults now",
                Healthcare.mortalityFactor(AgeBand.ADULT, .5, 1, .5),
                1 / Healthcare.GENERAL_SWING, 1e-9);

        // Even the worst end cannot empty a band in a month.
        for (AgeBand b : AgeBand.values()) {
            double worstMonth = AgeBand.monthlyFromAnnual(
                    b.getAnnualMortality() * Healthcare.mortalityFactor(b, 0, 0, 0));
            assertTrue("  " + b.getLabel().toLowerCase() + " cannot be wiped out in a month",
                    worstMonth <= AgeBand.MAX_MONTHLY_MORTALITY);
        }

        /* ---- and childcare decides how many are born in the first place ---- */
        check("no childcare, no bonus", Healthcare.birthFactor(0), 1, 1e-9);
        check("childcare for everybody doubles it", Healthcare.birthFactor(1),
                1 + Healthcare.CHILDCARE_BIRTH_BONUS, 1e-9);
        System.out.printf("  births run %.0f per 1,000/yr with no childcare and %.0f with it%n",
                PopulationCohorts.BIRTHS_PER_1000_PER_YEAR,
                PopulationCohorts.BIRTHS_PER_1000_PER_YEAR * Healthcare.birthFactor(1));

        PopulationCohorts fertile = new PopulationCohorts();
        PopulationCohorts barren = new PopulationCohorts();
        fertile.migrate(10000);
        barren.migrate(10000);
        fertile.advanceMonth(Healthcare.mortalityFactors(1, .5, .5), Healthcare.birthFactor(1));
        barren.advanceMonth(Healthcare.mortalityFactors(0, .5, .5), Healthcare.birthFactor(0));
        /*
         * Not to 1e-9, and the reason is the mechanic working. Births are a
         * share of the CURRENT total, and advanceMonth() kills before it gives
         * birth - so by the time the two cities reach the birth line they no
         * longer hold the same number of people: the served one has already kept
         * forty times more of its infants. The gap is about 0.03%, which is the
         * one month of mortality, and asserting it away to zero would mean
         * asserting the mortality change did nothing.
         */
        check("...and the pyramid gets that many more babies",
                fertile.getLastBirths() / barren.getLastBirths(),
                Healthcare.birthFactor(1), .001);

        // ...and it actually reaches the pyramid.
        PopulationCohorts served = new PopulationCohorts();
        PopulationCohorts neglected = new PopulationCohorts();
        served.migrate(20000);
        neglected.migrate(20000);
        double bornServed = 0, bornNeglected = 0, diedServed = 0, diedNeglected = 0;
        for (int m = 0; m < 12; m++) {
            served.advanceMonth(Healthcare.mortalityFactors(1, 1, 1), Healthcare.birthFactor(1));
            neglected.advanceMonth(Healthcare.mortalityFactors(0, 0, 0), Healthcare.birthFactor(0));
            bornServed += served.getLastBirths();
            bornNeglected += neglected.getLastBirths();
            diedServed += served.getDeaths(AgeBand.BABY);
            diedNeglected += neglected.getDeaths(AgeBand.BABY);
        }
        System.out.printf("  over a year: %,.0f babies born and %,.0f lost where care exists;"
                + " %,.0f born and %,.0f lost where it does not%n",
                bornServed, diedServed, bornNeglected, diedNeglected);
        assertTrue("a city with childcare loses far fewer infants",
                diedNeglected > diedServed * 20);
        assertTrue("...and has far more of them", bornServed > bornNeglected * 1.5);
        System.out.printf("  a year on: %,.0f babies where care exists, %,.0f where it does not"
                + "  (%.0f%% more)%n",
                served.get(AgeBand.BABY), neglected.get(AgeBand.BABY),
                (served.get(AgeBand.BABY) / neglected.get(AgeBand.BABY) - 1) * 100);
        System.out.printf("  untreated infant mortality is %.2f%%/yr against AgeBand's own %.2f%%%n",
                AgeBand.BABY.getAnnualMortality() * Healthcare.CHILDCARE_SWING * 100,
                AgeBand.BABY.getAnnualMortality() * 100);
        assertTrue("a cared-for city keeps more of its babies",
                served.get(AgeBand.BABY) > neglected.get(AgeBand.BABY));
        assertTrue("...and more of its seniors",
                served.get(AgeBand.SENIOR) > neglected.get(AgeBand.SENIOR));

        /* ================= 8. death care ================= */
        System.out.println("\n--- burial, cremation, and neither ---");

        double[] noCare = new double[CareType.values().length];

        // Rich city, plots available: everybody is buried.
        Healthcare rich = new Healthcare();
        rich.advanceMonth(0, 0, noCare, 100, 1.0, 1000, 500);
        check("with savings and plots, everybody is buried", rich.getBurials(), 100, 1e-9);
        check("...and nobody is cremated", rich.getCremations(), 0, 1e-9);
        check("...and the plots are gone for good", rich.getPlotsUsed(), 100, 1e-9);
        check("...and the city collected the burial fee",
                rich.getFuneralFees(), 100 * Healthcare.BURIAL_FEE, 1e-9);

        // Poor city: the crematorium.
        Healthcare poor = new Healthcare();
        poor.advanceMonth(0, 0, noCare, 100, 0.0, 1000, 500);
        check("with no savings, everybody is cremated", poor.getCremations(), 100, 1e-9);
        check("...and the ground is untouched", poor.getPlotsUsed(), 0, 1e-9);
        assertTrue("...which is the cheaper funeral",
                Healthcare.CREMATION_FEE < Healthcare.BURIAL_FEE);

        // Overflow, both ways - "or just whichever option is available".
        Healthcare fullGround = new Healthcare();
        fullGround.advanceMonth(0, 0, noCare, 100, 1.0, 40, 500);
        check("a full cemetery sends the rest to the oven", fullGround.getBurials(), 40, 1e-9);
        check("...which takes them", fullGround.getCremations(), 60, 1e-9);
        check("...and nobody is left waiting", fullGround.getUnburied(), 0, 1e-9);

        Healthcare fullOven = new Healthcare();
        fullOven.advanceMonth(0, 0, noCare, 100, 0.0, 1000, 30);
        check("a busy crematorium sends the rest to the ground",
                fullOven.getCremations(), 30, 1e-9);
        check("...even though nobody could afford a plot", fullOven.getBurials(), 70, 1e-9);
        check("...and nobody is left waiting", fullOven.getUnburied(), 0, 1e-9);

        // Neither.
        Healthcare nowhere = new Healthcare();
        nowhere.advanceMonth(0, 0, noCare, 100, 1.0, 0, 0);
        check("with neither, they all wait", nowhere.getUnburied(), 100, 1e-9);
        check("...and nothing was collected", nowhere.getFuneralFees(), 0, 1e-9);

        // ...and the backlog drains the month a cemetery opens.
        nowhere.advanceMonth(0, 0, noCare, 20, 1.0, 5000, 0);
        check("a new cemetery clears the backlog and the month together",
                nowhere.getBurials(), 120, 1e-9);
        check("...leaving nobody", nowhere.getUnburied(), 0, 1e-9);

        // The backlog cannot grow without bound.
        Healthcare hopeless = new Healthcare();
        for (int m = 0; m < 200; m++) hopeless.advanceMonth(0, 0, noCare, 10, 1.0, 0, 0);
        check("a city that never builds one stops counting after two years",
                hopeless.getUnburied(), 10 * Healthcare.MAX_BACKLOG_MONTHS, 1e-9);

        /* ---- and the dead make the living ill ---- */
        System.out.println("\n--- and it makes people ill ---");

        Health tidy = new Health(), grim = new Health();
        tidy.advanceMonth(10000, 10000, 3, 0);
        grim.advanceMonth(10000, 10000, 3, 50);
        System.out.printf("  50 unburied in a city of 10,000: %.1f%% sick against %.1f%%%n",
                grim.getSickRate() * 100, tidy.getSickRate() * 100);
        assertTrue("leaving them where they fell costs output", grim.getSickRate() > tidy.getSickRate());
        check("...by the weight it claims", grim.getUnburiedRate(),
                50.0 / 10000 * Health.UNBURIED_WEIGHT, 1e-9);

        Health swamped = new Health();
        swamped.advanceMonth(10000, 10000, 3, 900000);
        check("however many there are, it is capped",
                swamped.getUnburiedRate(), Health.MAX_UNBURIED_SICKNESS, 1e-9);

        /* ================= 9. senior care draws people in ================= */
        System.out.println("\n--- what senior care is worth ---");

        check("no senior care, no bonus", Migration.seniorCarePull(0), 1, 1e-9);
        check("full coverage, the full draw", Migration.seniorCarePull(1),
                1 + Migration.SENIOR_CARE_PULL, 1e-9);

        Migration plain = new Migration(), caring = new Migration();
        FamilyModel homes = new FamilyModel();
        PopulationCohorts some = new PopulationCohorts();
        some.migrate(4000);
        homes.rebuild(some, new double[PayTier.values().length]);

        plain.monthlyNet(4000, 2000, 9000, 3000, homes, .55, 0);
        caring.monthlyNet(4000, 2000, 9000, 3000, homes, .55, 1);
        System.out.printf("  a city of 4,000 wants to be %,.0f people, or %,.0f with senior care%n",
                plain.getLastTarget(), caring.getLastTarget());
        check("senior care raises the target by exactly the pull",
                caring.getLastTarget(),
                plain.getLastTarget() * Migration.seniorCarePull(1), 1e-6);

        /* ================= 10. and somebody pays for all of it ================= */
        System.out.println("\n--- the city's books ---");

        Game paid = new Game(files);
        System.setOut(quiet);
        try {
            paid.run();
            stock(paid);
            BuildingManager pb = paid.getBuildingManager();
            pb.addStack(pb.getTemplateByName("Walk-in Clinic"), 2, true);
            pb.addStack(pb.getTemplateByName("Memorial Cemetery"), 1, true);
            pb.addStack(pb.getTemplateByName("Neighbourhood Daycare"), 2, true);
            paid.simulateMonths(59);
        } finally { System.setOut(out); }

        /*
         * THE HOUSEHOLD STATEMENT IS A MONTH BEHIND, BY CONSTRUCTION.
         *
         * startOfMonthUpdate() strikes the residents' books for the month that
         * has just finished, before simulateMonth() runs the new one - so the
         * statement on screen in month 60 describes month 59, while the
         * government block is re-struck at the END of month 60 and describes
         * month 60. The pension figures on that same statement already carry
         * exactly this lag.
         *
         * So the honest test is not "the two numbers match today", which would
         * be comparing two different months, but "the households paid what the
         * city collected, one month later". Captured here, checked after one
         * more month has run.
         */
        double feesCollectedThisMonth = paid.getHealthcare().getFees();
        System.setOut(quiet);
        try { paid.simulateMonths(1); } finally { System.setOut(out); }

        EconomyManager pe = paid.getEconomyManager();
        NationalAccounts pn = pe.getNationalAccounts();
        Healthcare ph = paid.getHealthcare();

        System.out.printf("  bill $%,.1fk (%.0f wages + %.0f upkeep), fees $%,.1fk, %.0f%% recovered%n",
                ph.getGrossCost(), ph.getPayroll(), ph.getUpkeep(),
                ph.getFees(), ph.getCostRecovery() * 100);

        assertTrue("the service costs something", ph.getGrossCost() > 0);
        assertTrue("...most of which is wages", ph.getPayroll() > ph.getUpkeep() * .5);
        assertTrue("...and it is a NET DEFICIT business, per the spec",
                ph.getNetCost() > 0 && ph.getCostRecovery() < .5);

        check("the treasury is billed for it",
                pe.getHealthcareBill(), ph.getGrossCost(), 1e-9);
        check("...and it is on the city's expenditure list",
                pn.getHealthSpending(), ph.getGrossCost(), 1e-9);
        check("...and the fees are on its revenue list",
                pn.getHealthFees(), ph.getFees(), 1e-9);

        /*
         * THE CONSERVATION ASSERTION, and the reason this whole batch happened.
         * Fee revenue credited to the city and debited to nobody is money from
         * nowhere - the same shape as the payroll hole this replaced.
         */
        check("and the households paid exactly what the city collected a month ago",
                paid.getHouseholds().getHealthcare(), feesCollectedThisMonth, 1e-9);
        assertTrue("...which is not the same as this month's, so the test means something",
                Math.abs(feesCollectedThisMonth - ph.getFees()) > 0);

        double rows = 0;
        for (int r = 0; r < paid.getHouseholds().getRowCount(); r++) {
            rows += paid.getHouseholds().getRowHealthcare(r);
        }
        check("...and the seven tiers add back up to it",
                rows, paid.getHouseholds().getHealthcare(), 1e-6);

        // GDP: a government that staffs a hospital is producing something.
        assertTrue("healthcare is counted as government output",
                pn.getGovernment() >= ph.getGrossCost());

        /*
         * The expense actually reaches the cash. getTotalIncome() is
         * getTaxIncome() minus getExpenses(), and finalUpdateEconomy() moves the
         * treasury by it - so a bill that never joined getExpenses() would show
         * on every screen and cost nothing.
         */
        double before = pe.getTotalIncome();
        pe.setHealthcare(ph.getGrossCost() + 1000, ph.getFees());
        check("a bigger bill is a smaller surplus, penny for penny",
                pe.getTotalIncome(), before - 1000, 1e-6);
        pe.setHealthcare(ph.getGrossCost(), ph.getFees());

        /* ---- the graves and the backlog survive a reload ---- */
        System.setOut(quiet);
        try { paid.saveGame(2, "healthcare"); } finally { System.setOut(out); }

        Game reopened = new Game(files);
        System.setOut(quiet);
        try { reopened.loadGameSave(2); } finally { System.setOut(out); }

        check("the graves came back", reopened.getHealthcare().getPlotsUsed(),
                ph.getPlotsUsed(), 1e-9);
        check("...and the backlog", reopened.getHealthcare().getUnburied(),
                ph.getUnburied(), 1e-9);
        check("...and the bill the city was paying",
                reopened.getEconomyManager().getHealthcareBill(),
                pe.getHealthcareBill(), 1e-9);
        assertTrue("a save from before healthcare had books is refused whole",
                !new Healthcare().restore(new double[]{1, 2, 3}));

        /* ============ 11. a skip cannot hide an epidemic ============ */
        /*
         * The screen is the only place an outbreak exists for a player, and a
         * skip is where one is most easily missed - it lasts three or four
         * months and decays, so a city that lost a quarter of its output to one
         * mid-skip looks identical at both ends to a city that was well the
         * whole time. The report has to carry it, so the report is tested.
         */
        System.out.println("\n--- a skip reports what it lived through ---");

        TimeSkipReport report = new TimeSkipReport();
        Health lifetime = new Health();
        int seen = 0;
        for (int m = 1; m <= 600; m++) {
            lifetime.advanceMonth(0, 10000, m, 0);
            if (lifetime.isOutbreak()) seen++;
            report.sampleMonth(1, 1, 1, 1e9, false, true, 10000,
                    lifetime.getWorkRatio(), lifetime.isOutbreak(), 0);
        }

        System.out.printf("  600 months: %d outbreaks, ill in %d of them, worst month %.0f%% out%n",
                report.getOutbreaks(), report.getMonthsInOutbreak(),
                (1 - report.getWorstWorkRatio()) * 100);

        assertTrue("the skip noticed the epidemics", report.getOutbreaks() > 0);
        check("...and counted every month of them",
                report.getMonthsInOutbreak(), seen, 0);
        assertTrue("...and kept the worst month, which the endpoints cannot show",
                report.getWorstWorkRatio() < 1 - Health.UNTREATED_RATE);
        check("an untreated city is below full every single month",
                report.getMonthsSick(), 600, 0);

        TimeSkipReport tidy2 = new TimeSkipReport();
        tidy2.sampleMonth(1, 1, 1, 1e9, false, true, 10000, 1, false, 0);
        check("a healthy month reports no outbreak", tidy2.getOutbreaks(), 0, 0);
        check("...and nothing left unburied", tidy2.getPeakUnburied(), 0, 1e-9);

        cleanUp(root);
        System.out.println(fails == 0
                ? "\nAll checks passed."
                : "\n" + fails + " FAILED");
        System.exit(fails == 0 ? 0 : 1);
    }

    /** A city with enough in it that the sectors have something to lose. */
    static void stock(Game g) {
        BuildingManager b = g.getBuildingManager();
        b.addStack(b.getTemplateByName("House"), 400, true);
        b.addStack(b.getTemplateByName("Convience Store"), 10, true);
        b.addStack(b.getTemplateByName("Texttile Mill"), 3, true);
        b.addStack(b.getTemplateByName("Construction Depot"), 3, true);
        b.addStack(b.getTemplateByName("Coal Power Plant"), 1, true);
        b.addStack(b.getTemplateByName("Water Treatment Plant"), 1, true);
    }

    static void cleanUp(Path root) {
        try (var walk = Files.walk(root)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (java.io.IOException ignored) { }
            });
        } catch (java.io.IOException ignored) { }
    }
}
